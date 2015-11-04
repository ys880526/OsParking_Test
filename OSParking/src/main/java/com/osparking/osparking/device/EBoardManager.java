/* 
 * Copyright (C) 2015 Open Source Parking Inc.(www.osparking.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.osparking.osparking.device;

import com.osparking.osparking.ControlGUI;
import com.osparking.global.names.DeviceManager;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.logging.Level;
import static com.osparking.global.Globals.*;
import static com.osparking.global.names.OSP_enums.DeviceType.*;
import com.osparking.global.names.ParkingTimer;
import static com.osparking.global.names.DB_Access.gateCount;
import com.osparking.global.names.OSP_enums.MsgCode;
import static com.osparking.global.names.OSP_enums.MsgCode.EBD_ACK;
import static com.osparking.global.names.OSP_enums.MsgCode.EBD_DEFAULT1;
import static com.osparking.global.names.OSP_enums.MsgCode.EBD_DEFAULT2;
import static com.osparking.global.names.OSP_enums.MsgCode.EBD_ID_ACK;
import static com.osparking.global.names.OSP_enums.MsgCode.EBD_INTERRUPT1;
import static com.osparking.global.names.OSP_enums.MsgCode.EBD_INTERRUPT2;
import static com.osparking.global.names.OSP_enums.MsgCode.JustBooted;

/**
 * Manages a gate bar via a socket communication while current socket connection is valid.
 * To do that, gateManager uses a socket reader(SockReader) Thread instance and a Runnable class
 * (AreYouThereSender) instance which is created by the SockReader object.
 * 
 * @author Open Source Parking Inc.
 */
public class EBoardManager extends Thread implements DeviceManager {
    //<editor-fold desc="--class variables">
    private byte deviceNo = 0; // ID of the gate bar being served by this manager. A valid ID starts from 1.
    private ControlGUI mainForm; // main form of the gate bar simulator.
    
    /**
     * socket for the communication with the gate bar.
     */
    private Socket socket = null; // socket that connects with a e-board
    
    /**
     * a timer employed to send Open commands to the designated gate bar for sure.
     */
    ParkingTimer timerSendOpenCmd = null;
    
    //</editor-fold>    

    byte [] cmdIDarr = new byte[4]; // open command ID
    byte [] fiveByteArr =new byte[5]; // storage for (code + ID)
    
    boolean justBooted = true;
    private boolean neverConnected = true;

    /**
     * 
     * @param mainForm main GUI form of the whole manager program
     * @param deviceNo ID of the E-Board to manage
     */
    public EBoardManager(ControlGUI mainForm, byte deviceNo)
    {
        super("osp_EBD_" + deviceNo + "_Manager");
        this.mainForm = mainForm; 
        this.deviceNo = deviceNo;
    }    

    public void run()
    {   
        while (true) // infinite communication with an e-board
        {
            if (mainForm.isSHUT_DOWN()) {
                return;
            }
            
            int msgCode = -2;
            // read device message as long as connection is good
            
            try {
                synchronized(mainForm.getSocketMutex()[E_Board.ordinal()][deviceNo])  
                {
                    //<editor-fold desc="-- Wait connection, send default settings, read message code">
                    if (! isConnected(getSocket())) 
                    {
                        mainForm.getSocketMutex()[E_Board.ordinal()][deviceNo].wait();
                        neverConnected = false;
                    
                        if (justBooted) {
                            justBooted = false;
                            sendEBoardDefaultSetting(mainForm, deviceNo, TOP_ROW);
                            sendEBoardDefaultSetting(mainForm, deviceNo, BOTTOM_ROW);
                        } 
                    }
                    //</editor-fold>
                } 
                // SocketTimeoutException will arise when no data on the socket during 1 second
                msgCode = socket.getInputStream().read(); // waits for PULSE_PERIOD miliseconds                        

                //<editor-fold defaultstate="collapsed" desc="-- Reject irrelevant message code">
                if (msgCode == -1) {
                    // 'End of stream' means other party closed socket. So, I need to close it from my side.
                    finishConnection(null,  "End of stream reached, gate #" + deviceNo, deviceNo);
                    continue;
                } else if (msgCode < -1 || MsgCode.values().length <= msgCode) {
                    finishConnection(null, "Wrong message code: "+ msgCode, deviceNo);
                    continue;
                }
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="-- Process message from e-board">
                synchronized(mainForm.getSocketMutex()[E_Board.ordinal()][deviceNo]) 
                {
                    switch (MsgCode.values()[msgCode]) 
                    {      
                        case EBD_ID_ACK: // e-board heartbeat
                            //<editor-fold defaultstate="collapsed" desc="--handle gate bar heartbeat">
                            if (noArtificialErrorInserted(mainForm.errorCheckBox))
                            {
                                /**
                                 * Maximize tolerance value for the E-Board.
                                 * manager will consider this bar connected for the next MAX_TOLERANCE
                                 * LED blinking cycles.
                                 */
                                mainForm.tolerance[E_Board.ordinal()][deviceNo].assignMAX();
                            }
                            break;
                            //</editor-fold>

                        case EBD_ACK: // gate acknowledges an e-board display message reception.
                            //<editor-fold defaultstate="collapsed" desc="--ACK for display interrupt or default display change">
                            byte[] restOfMessage = new byte[3];

                            if (isConnected(socket))
                                socket.getInputStream().read(restOfMessage);
                            else
                                continue;

                            System.out.println("4. message ACD arrived at: " + System.currentTimeMillis() % 10000);

                            int codeAcked = restOfMessage[0];
                            short checkTSC = (short)(msgCode + codeAcked); // TCS: This Site Calculation

                            if (restOfMessage[1] == (byte)((checkTSC >> 8) & 0xff)
                                    && restOfMessage[2] == (byte)(checkTSC & 0xff))
                            {
                                //<editor-fold desc="-- Handle ACK response from E-Board">
                                int row = -1;

                                //<editor-fold desc="-- Calculate row number(0 or 1)">
                                if (codeAcked == EBD_INTERRUPT1.ordinal()
                                        || codeAcked == EBD_DEFAULT1.ordinal()) {
                                        row = TOP_ROW;
                                } else if (codeAcked == EBD_INTERRUPT2.ordinal()
                                        || codeAcked == EBD_DEFAULT2.ordinal()) {
                                        row = BOTTOM_ROW;
                                } else {
                                    logParkingException(Level.SEVERE, null, "wrong row number", deviceNo);
                                    break;
                                }
                                //</editor-fold>

                                ParkingTimer msgSendingTimer = mainForm.getSendEBDmsgTimer()[deviceNo][row];
                                if (msgSendingTimer.hasTask()) {
                                    //<editor-fold desc="-- Save debugging info">
                                    if (codeAcked == EBD_INTERRUPT1.ordinal() || 
                                            codeAcked == EBD_INTERRUPT2.ordinal() ) 
                                    {
                                        long currTmMs = System.currentTimeMillis();
                                        long ackDelay = (int)(currTmMs - mainForm.eBoardMsgSentMs[deviceNo][row]);
                                        int resendCnt = ( (SendEBDMessageTask)msgSendingTimer.getParkingTask() )
                                                .getResendCount();

                                        if (DEBUG) {
                                            mainForm.getPerfomStatistics()[E_Board.ordinal()][deviceNo]
                                                    .addAckSpeedStatistics((int)ackDelay, resendCnt);
//                                            System.out.println(timeFormat.format(new Date()) + "--row:" + row 
//                                                    + ", resent: " + resendCnt + ", delay:$ " + ackDelay);
                                        }
                                    }
                                    //</editor-fold>

                                    msgSendingTimer.cancelTask();
                                }
                                //</editor-fold>
                            }
                            break;
                            //</editor-fold>

                        case JustBooted:
                            //<editor-fold defaultstate="collapsed" desc="-- First connection after device booting">
                            // reset recent device disconnection time 
//                            System.out.println("just booted arrived");
                            mainForm.getSockConnStat()[E_Board.ordinal()][deviceNo].resetStatChangeTm();
                            break;
                            //</editor-fold>
                                                  
                        default:
                            throw new Exception("Unhandled message code " + MsgCode.values()[msgCode] 
                                + " from E-Board #" + deviceNo);
                    }
                }
                //</editor-fold>
                
            } catch (SocketTimeoutException e) {
            } catch (InterruptedException ex) {
                if (!mainForm.isSHUT_DOWN()) {
                    logParkingException(Level.INFO, ex, "E-Board manager #" + deviceNo + " waits socket conn'");
                    finishConnection(ex,  "E-Board manager #" + deviceNo + " waits socket conn'", deviceNo);
                }
            } catch (IOException e) {
                if (!mainForm.isSHUT_DOWN()) {
                    logParkingExceptionStatus(Level.SEVERE, e, "IOEx- closed socket, E-board #" + deviceNo,
                            mainForm.getStatusTextField(), deviceNo);
                    finishConnection(e, "server closed socket for ", deviceNo);
                }
            } catch (Exception e2) {
                logParkingExceptionStatus(Level.SEVERE, e2, 
                        e2.getMessage() + "server- closed socket forE-Board #" + deviceNo,
                        mainForm.getStatusTextField(), deviceNo);
                finishConnection(e2, "E-Board manager Excp", deviceNo);
            }
            //</editor-fold>

            if (mainForm.tolerance[E_Board.ordinal()][deviceNo].getLevel() <= 0) {
                finishConnection(null, "LED: tolerance depleted for", deviceNo);
            }
        }
    }

    /**
     * stops serving a gate bar.
     */
    @Override
    public void stopOperation(String reason) {
        finishConnection(null, reason, deviceNo);
        interrupt();
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * closes socket connection to a gate bar.
     * 
     * before closing the socket, it cancels any existing relevant tasks.
     */
    @Override
    public void finishConnection(Exception e, String description, byte gateNo) 
    {
        synchronized(mainForm.getSocketMutex()[E_Board.ordinal()][gateNo]) 
        {
            if (0 < gateNo && gateNo <= gateCount) 
            {
                if (isConnected(socket)) 
                {   
                    String msg =  "E-Board #" + gateNo;

                    addMessageLine(mainForm.getMessageTextArea(), "  ------" +  msg + " disconnected");
                    logParkingException(Level.INFO, e, description + " " + msg);

                    mainForm.getSockConnStat()[E_Board.ordinal()][gateNo].
                            recordSocketDisconnection(System.currentTimeMillis());
                    closeSocket(getSocket(), "while gate bar socket closing");
                    socket = null;
                }
            } else {
                System.out.println("this never ever gateNo");
            }
        }
            
        if (mainForm.getConnectDeviceTimer()[E_Board.ordinal()][gateNo] == null) {
            System.out.println("this never ever happens");
        } else {
            mainForm.getConnectDeviceTimer()[E_Board.ordinal()][gateNo].reRunOnce();
            addMessageLine(mainForm.getMessageTextArea(), "Trying to connect to E-Board #" + gateNo);
        }
    }

    public static void sendEBoardDefaultSetting(ControlGUI mainForm, byte deviceNo, byte row) {
        if (! mainForm.getSendEBDmsgTimer()[deviceNo][row].hasTask())
        {
            mainForm.getSendEBDmsgTimer()[deviceNo][row].reschedule(
                        new SendEBDMessageTask(
                            mainForm, deviceNo, row, 
                            mainForm.getDefaultMessage(
                                    deviceNo, row,
                                    --mainForm.msgSNs[deviceNo]), 
                            mainForm.msgSNs[deviceNo]));
        }    
    }

    @Override
    public boolean isNeverConnected() {
        return neverConnected;
    }
}
