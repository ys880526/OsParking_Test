/* 
 * OSParking, Parking Lot Management Software
 * Copyright (C) 2015 Open Source Parking Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.osparking.osparking.device;

import com.osparking.global.names.DeviceManager;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import com.osparking.global.Globals;
import static com.osparking.global.Globals.*;
import static com.osparking.global.names.OSP_enums.DeviceType.*;
import com.osparking.global.names.OSP_enums.OpLogLevel;
import com.osparking.osparking.ControlGUI;
import static com.osparking.global.names.DB_Access.gateCount;
import com.osparking.global.names.OSP_enums.MsgCode;
import static com.osparking.global.names.OSP_enums.MsgCode.CarImage;
import static com.osparking.global.names.OSP_enums.MsgCode.IAmHere;
import static com.osparking.global.names.OSP_enums.MsgCode.JustBooted;

/**
 * Maintains communication with a camera  thru a socket as long as the socket is connected,
 * employes a socket reader thread dedicated to read message from the socket and handle them 
 * considering their type. the reader send back "ACK" for some messages to the camera.
 * 
 * @author Park, Jongbum <Park, Jongbum at Open Source Parking Inc.>
 */
public class CameraManager extends Thread implements DeviceManager {
    //<editor-fold desc="--class variables">
    private byte cameraID = 0;
    private ControlGUI mainForm;
    /**
     * socket to use when it communicates with the camera
     */
    private Socket socket;

    /**
     * this object is used to ensure a streamlined use of the socket for Manager and SocketReader threads
     */
    Object WriteMUTEX = new Object();    
    int currImgSN = 0; // the ID of the most recently processed car entry image
    int smearExperienced_ID = 0;        
    
    /**
     * Data structure for socket reading.
     */
    byte[] bytesID = new byte[4];
    byte[] bytesSize = new byte[4];
    byte[] bytes9 =new byte[9];    
    
    final static int ImageSizeMax = 1024 * 1024 ;
    static byte[][] oneMBytes = new byte[gateCount][ImageSizeMax];
    
    FileWriter fw;
    
    byte[] messageImg_ACK = new byte[5];
    //</editor-fold>    
    private boolean neverConnected = true;

    /**
     * Initializes some data members and starts socket reader thread.
     * 
     * @param mainForm manager main form
     * @param cameraID ID of the camera to manage
     */
    public CameraManager(ControlGUI mainForm, byte cameraID)
    {
        super("osp_Camera_" + cameraID + "_Manager");
        this.mainForm = mainForm;
        this.cameraID = cameraID;
        fw = mainForm.getIDLogFile()[Camera.ordinal()][cameraID];
    }    
    
    /**
     * Sends out heartbeats('Are You There') regularly to the camera by employing a formClockTimer task
     */
    public void run()
    {   
        while (true)
        {
            if (mainForm.isSHUT_DOWN()) {
                return;
            }

            int cameraMessageCode = -2;
            
            // Receive vehicle entry image and camera response as long as connection is good
            try {
                synchronized(mainForm.getSocketMutex()[Camera.ordinal()][cameraID])
                {
                    //<editor-fold desc="-- Wait cnnection and read message code">
                    if (! isConnected(getSocket())) {
                        mainForm.getSocketMutex()[Camera.ordinal()][cameraID].wait();
                        neverConnected = false;
                    }
                    //</editor-fold>
                }
                // --Read message code from the camera
                cameraMessageCode = socket.getInputStream().read(); // waits for PULSE_PERIOD mili-seconds

                //<editor-fold defaultstate="collapsed" desc="-- Reject irrelevant message code">
                if (cameraMessageCode == -1) {
                    finishConnection(null,  "End of stream reached", cameraID);
                    continue;
                } else if (cameraMessageCode < -1 || MsgCode.values().length <= cameraMessageCode) {
                    finishConnection(null,  "unexpected camera message code", cameraID);
                    continue;
                }
                //</editor-fold>
                
                //<editor-fold defaultstate="collapsed" desc="-- Process message from camera">
                synchronized(mainForm.getSocketMutex()[Camera.ordinal()][cameraID]) 
                {
                    switch (MsgCode.values()[cameraMessageCode]) 
                    {
                        case IAmHere:
                            //<editor-fold defaultstate="collapsed" desc="--Handle camera response">
                            if (noArtificialErrorInserted(mainForm.errorCheckBox)) {
                                mainForm.tolerance[Camera.ordinal()][cameraID].assignMAX();
                            }
                            break;
                            //</editor-fold>

                        case CarImage:
                            //<editor-fold defaultstate="collapsed" desc="--Handle Car Entry Image">
                            mainForm.getPassingDelayStat()[cameraID].setICodeArrivalTime(System.currentTimeMillis());
                            
                            if (isConnected(getSocket())) {
                                socket.getInputStream().read(bytesID);
                                socket.getInputStream().read(bytesSize);
                            } else {
                                System.out.println("unexpected/rare execution path 1");
                                continue;
                            }
                            bytes9[0] = (byte)CarImage.ordinal();
                            System.arraycopy(bytesID, 0, bytes9, 1, 4);
                            System.arraycopy(bytesSize, 0, bytes9, 5, 4);

                            currImgSN = ByteBuffer.wrap(bytesID).getInt();

                            if (noArtificialErrorInserted(mainForm.errorCheckBox))
                            {
                                //<editor-fold defaultstate="collapsed" desc="--Correct Image Size">
                                int readSize = 0, thisRead = 0, startIdx = 0;
                                int imageSize = ByteBuffer.wrap(bytesSize).getInt();

                                if (ImageSizeMax < imageSize) {
                                    JOptionPane.showMessageDialog(mainForm, "Image size too big!" 
                                            + System.lineSeparator() + "It's more than " 
                                            + getFilesizeStr(ImageSizeMax) + " bytes" 
                                            + System.lineSeparator() 
                                            + "Please discuss with the system provider on this",
                                            "Image Size Error", JOptionPane.ERROR_MESSAGE);
                                }
                                /**
                                 * since an image data is large usually, it can be divided into many small
                                 * packets and the reader needs to read many times from the socket
                                 * until it gets the amount of data to make the perfect image out of it.
                                 */
                                while (readSize < imageSize) {
                                    thisRead = socket.getInputStream()
                                            .read(oneMBytes[cameraID - 1], startIdx, imageSize - readSize);
                                    readSize += thisRead;
                                    startIdx = readSize;
                                }

                                messageImg_ACK[0] = (byte)MsgCode.Img_ACK.ordinal();
                                System.arraycopy(ByteBuffer.allocate(4).putInt(currImgSN).array(), 0, 
                                        messageImg_ACK, 1, 4);

                                while (! isConnected(socket)) {
                                    mainForm.getSocketMutex()[Camera.ordinal()][cameraID].wait();
                                }
                                socket.getOutputStream().write(messageImg_ACK);

                                if (currImgSN != mainForm.prevImgSN[cameraID]) {
                                    // handle a brand new image sent from the camera
                                    if (DEBUG) {
                                        saveImageID(currImgSN, mainForm.prevImgSN[cameraID]);
                                    }
                                    mainForm.prevImgSN[cameraID] = currImgSN;
                                    BufferedImage image = ImageIO.read(new ByteArrayInputStream(
                                            oneMBytes[cameraID - 1], 0, imageSize));

                                    String tagNumber  = getTagNumber(getPictureNo(imageSize)); // LPR_engine

                                    if (!(mainForm.isGateBusy[cameraID])) {
                                        mainForm.processCarArrival(cameraID, currImgSN, tagNumber, image);
                                    }
                                }
                                //</editor-fold>
                            } else {
                                throw new Exception("Image #" + currImgSN + " is broken");
                            }
                            break;
                            //</editor-fold>

                        case JustBooted:
                            //<editor-fold defaultstate="collapsed" desc="-- First connection after device booting">
                            // reset recent device disconnection time 
//                            System.out.println("just booted arrived");
                            mainForm.getSockConnStat()[Camera.ordinal()][cameraID].resetStatChangeTm();
                            break;
                            //</editor-fold>
                                                    
                        default:
                            throw new Exception("Unhandled message code from Camera #" + cameraID);
                    }
                } 
                //</editor-fold>
                
            } catch (SocketTimeoutException e) {
            } catch (InterruptedException ex) {
                if (!mainForm.isSHUT_DOWN()) {
                    logParkingException(Level.INFO, ex, "Camera manager #" + cameraID + " waits socket conn'");
                    finishConnection(ex, "camera manager IOEx ", cameraID);
                }
            } catch (IOException e) {
                if (!mainForm.isSHUT_DOWN()) {
                    logParkingExceptionStatus(Level.SEVERE, e, "server- closed socket for camera #" + cameraID,
                            mainForm.getStatusTextField(), cameraID);
                    finishConnection(e, "camera manager IOEx ", cameraID);
                }
            } catch (Exception e2) {
//                if (!mainForm.isSHUT_DOWN()) 
                {
                    logParkingExceptionStatus(Level.SEVERE, e2, "server- closed socket for camera #" + cameraID,
                            mainForm.getStatusTextField(), cameraID);
                    finishConnection(e2, "camera manager Excp  ", cameraID);
                }
            }
            //</editor-fold>
            
            if (mainForm.tolerance[Camera.ordinal()][cameraID].getLevel() < 0) {
                finishConnection(null, "LED: tolerance depleted for", cameraID);
            }
        }
    }

    /**
     * @return the socket
     */
    public Socket getSocket() {
        return socket;
    }

    @Override
    public void stopOperation(String reason) {
        finishConnection(null, reason, cameraID);
        interrupt();
    }

    private synchronized void saveImageID(int currentID, int previousID) {
        if (cameraID == 0)
            return; 
        String message = currentID + " " + previousID + System.lineSeparator();
//        if (DEBUG)
//            Globals.logParkingOperation(OpLogLevel.LogAlways, message, cameraID);
        
        try {
            fw.write(message);
            fw.flush();
        } catch (FileNotFoundException ex) {
            logParkingExceptionStatus(Level.SEVERE, ex, "car image ID logging module", 
                    mainForm.getStatusTextField(), cameraID);
        } catch (IOException ex) {
            logParkingExceptionStatus(Level.SEVERE, ex, "car image ID logging module", 
                    mainForm.getStatusTextField(), cameraID);
        }        
    }

    @Override
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * stop serving a camera in case of exceptional event happens.
     */
    @Override
    public void finishConnection(Exception e, String description, byte cameraID)
    {
        synchronized (mainForm.getSocketMutex()[Camera.ordinal()][cameraID]) 
        {
            /** 
             * close IO streams and camera socket before finishin service of this manager thread.
             */
            if (0 < cameraID && cameraID <= gateCount) {
                if (isConnected(socket)) {
                    String msg = "  ------Camera #" + cameraID + " disconnected";

                    addMessageLine(mainForm.getMessageTextArea(), msg);
                    logParkingException(Level.INFO, e, description + msg);

                    long closeTm = System.currentTimeMillis();

                    mainForm.getSockConnStat()[Camera.ordinal()][cameraID]
                            .recordSocketDisconnection(closeTm);   
                    if (DEBUG) {
                        System.out.println("Camera #" + cameraID + " disconnected at: " + closeTm);
                    }
                    closeSocket(socket, "while socket closing during camera connection finishing");
                    socket = null;
                }
            } else {
                System.out.println("this never ever cameraID");
            }
        }
            
        if (mainForm.getConnectDeviceTimer()[Camera.ordinal()][cameraID] == null) {
            System.out.println("this never ever happens");
        } else {
            mainForm.getConnectDeviceTimer()[Camera.ordinal()][cameraID].reRunOnce();
//            addMessageLine(mainForm.getMessageTextArea(), "M5'. Cam#" + cameraID + " conn' resch'ed");
        }
    }

    @Override
    public boolean isNeverConnected() {
        return neverConnected;
    }

    private byte getPictureNo(int size) {
        byte picNo = 0;
        
        if (size == 47610)
            picNo = 1;
        else if (size == 35476)
            picNo = 2;
        else if (size == 35103)
            picNo = 3;
        else if (size == 23330)
            picNo = 4;
        else if (size == 28217)
            picNo = 5;
        else if (size ==  42467)
            picNo = 6;
        
        return picNo;
    }
}
