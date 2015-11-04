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

import com.osparking.global.Globals;
import com.osparking.global.names.DeviceManager;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import static com.osparking.global.Globals.*;
import com.osparking.global.names.DB_Access;
import static com.osparking.global.names.OSP_enums.DeviceType.GateBar;
import com.osparking.global.names.OSP_enums.MsgCode;
import static com.osparking.global.names.OSP_enums.MsgCode.IAmHere;
import static com.osparking.global.names.OSP_enums.MsgCode.JustBooted;
import static com.osparking.global.names.OSP_enums.MsgCode.Open_ACK;
import com.osparking.global.names.ParkingTimer;
import com.osparking.osparking.ControlGUI;

/**
 * Manages a gate bar via a socket communication while current socket connection is valid.
 * To do that, gateManager uses a socket reader(SockReader) Thread instance and a Runnable class
 * (AreYouThereSender) instance which is created by the SockReader object.
 * 
 * @author Open Source Parking Inc.
 */
public class GateBarManager extends Thread implements DeviceManager {
    //<editor-fold desc="--class variables">
    private byte gateID = 0; // ID of the gate bar being served by this manager. A valid ID starts from 1.
    private ControlGUI mainForm; // main form of the gate bar simulator.
    
    /**
     * socket for the communication with the gate bar.
     */
    private Socket socket; // socket that connects with the gate bar
    
    /**
     * a common gate managers array. shared among all threads/timers for the gate bars
     */
    DeviceManager[] deviceManagers;
        
    /**
     * a timer employed to send Open commands to the designated gate bar for sure.
     */
    ParkingTimer openGateTimer = null;
    
    /**
     * a timer used to send a heartbeat to the gate bar regularily.
     */
    public ParkingTimer timerAreYouThere = null;
    //</editor-fold>    

    byte [] cmdIDarr = new byte[4]; // open command ID
    byte [] fiveByteArr =new byte[5]; // storage for (code + ID)
    
    private boolean neverConnected = true;
    
    /**
     * A unique constructor of the GateManager class.
     * 
     * @param mainForm main GUI form of the whole manager program
     * @param gateID ID of the gate bar to manage
     */
    public GateBarManager(ControlGUI mainForm, byte gateID)
    {
        super("osp_GateBar_" + gateID + "_Manager");        
        this.mainForm = mainForm; 
        this.gateID = gateID; 
        openGateTimer = mainForm.getOpenGateCmdTimer()[gateID];
    }    

    public void run()
    {   
        while (true) // infinite reading of a gate bar socket 
        {
            if (mainForm.isSHUT_DOWN()) {
                return;
            }            

            // read device message as long as connection is good
            int barMessageCode = -2;
            
            //<editor-fold defaultstate="collapsed" desc="--process messages from a gate bar">
            try {
                synchronized(mainForm.getSocketMutex()[GateBar.ordinal()][gateID])  
                {
                    //<editor-fold defaultstate="collapsed" desc="-- Wait connection, read message code">
                    if (! isConnected(socket)) 
                    {
                        mainForm.getSocketMutex()[GateBar.ordinal()][gateID].wait(); 
                    }
                    neverConnected = false;
                    //</editor-fold>
                }
                // SocketTimeoutException will arise when no data on the socket during 1 second
                barMessageCode = socket.getInputStream().read(); // waits for PULSE_PERIOD miliseconds
                openGateTimer = mainForm.getOpenGateCmdTimer()[gateID];

                //<editor-fold defaultstate="collapsed" desc="-- Reject irrelevant message code">
                if (barMessageCode == -1) {
                    // 'End of stream' means other party closed socket. So, I need to close it from my side.
                    finishConnection(null,  "End of stream reached, gate #" + gateID, gateID);
                    continue;
                } else if (barMessageCode < -1 || MsgCode.values().length <= barMessageCode) {
                    finishConnection(null, "Wrong message code: "+ barMessageCode, gateID);
                    continue;
                } 
                //</editor-fold>
                
//                System.out.println("msg code: " + MsgCode.values()[barMessageCode]);

                //<editor-fold defaultstate="collapsed" desc="-- Process message from gate bar">
                synchronized(mainForm.getSocketMutex()[GateBar.ordinal()][gateID]) 
                {                
                    switch (MsgCode.values()[barMessageCode]) 
                    {
                        case IAmHere: // gate bar heartbeat
                            //<editor-fold defaultstate="collapsed" desc="--handle gate bar heartbeat">
                            if (noArtificialErrorInserted(mainForm.errorCheckBox))
                            {
                                /**
                                 * maximize tolerance value for the gate bar.
                                 * manager will consider this bar connected for the next MAX_TOLERANCE
                                 * LED blinking cycles.
                                 */
                                mainForm.tolerance[GateBar.ordinal()][gateID].assignMAX();
                            }
                            break;
                            //</editor-fold>

                        case Open_ACK: // gate acknowledges an open command reception.
                            //<editor-fold defaultstate="collapsed" desc="--handling Open_ACK message">
                            socket.getInputStream().read(cmdIDarr);
                            fiveByteArr[0] = (byte)Open_ACK.ordinal(); // message code
                            System.arraycopy(cmdIDarr, 0, fiveByteArr, 1, 4); // Open command ID

                            if (noArtificialErrorInserted(mainForm.errorCheckBox)) 
                            {
                                // read command ID from the socket and compare it with the local command ID
                                int ackedCmdID = ByteBuffer.wrap(cmdIDarr).getInt();

                                if (! mainForm.openCommAcked[gateID] &&
                                        ackedCmdID == mainForm.openCommandIDs[gateID])
                                {        
                                    //<editor-fold desc="--Process fresh and warm ACK message">
                                    if (DB_Access.storePassingDelay) 
                                    {
                                        /**
                                         * record vehicle processing performance if it is meaningful.
                                         */
                                        if (mainForm.getPassingDelayStat()[gateID].isAccumulatable()) {
                                            String msg 
                                                    = mainForm.getPassingDelayStat()[gateID].recordBarACKspeed(gateID);
                                            if (msg != null)
                                                addMessageLine(mainForm.getMessageTextArea(), msg);                
                                        }

                                        if (Globals.DEBUG) {
                                            // time the manager took until it gets ACK for an open command it issued
                                            int ackDelay = (int)
                                                    (System.currentTimeMillis() - mainForm.openCommandIssuedMs[gateID]);
                                            int resendCnt = ((SendGateOpenTask)openGateTimer.getParkingTask())
                                                    .getResendCount();

                                            mainForm.getPerfomStatistics()[GateBar.ordinal()][gateID]
                                                    .addAckSpeedStatistics(ackDelay, resendCnt);
                                        }
                                    }
                                    openGateTimer.cancelTask(); // Stop resending open command
                                    mainForm.openCommAcked[gateID] = true;
                                    //</editor-fold>
                                }
                            }
                            break;
                            //</editor-fold>

                        case JustBooted:
                            //<editor-fold defaultstate="collapsed" desc="-- First connection after device booting">
                            // reset recent device disconnection time 
//                            System.out.println("just booted arrived");
                            mainForm.getSockConnStat()[GateBar.ordinal()][gateID].resetStatChangeTm();
                            break;
                            //</editor-fold>
                            
                        default:
                            throw new Exception("Unhandled message code from Gate bar #" + gateID);
                    }
                }
                //</editor-fold>
                
            } catch (SocketTimeoutException e) {
            } catch (InterruptedException ex) {
                if (!mainForm.isSHUT_DOWN()) {
                    logParkingException(Level.INFO, ex, "Gate bar manager #" + gateID + " waits socket conn'");
                    finishConnection(ex,  "Gate bar manager #" + gateID + " waits socket conn'", gateID);
                }
            } catch (IOException e) {
                if (!mainForm.isSHUT_DOWN()) {
                    logParkingExceptionStatus(Level.SEVERE, e, "IOEx- closed socket, Gate bar #" + gateID,
                            mainForm.getStatusTextField(), gateID);
                    finishConnection(e, "server closed socket for ", gateID);
                }
            } catch (Exception e2) {
                logParkingExceptionStatus(Level.SEVERE, e2, "server- closed socket for Gate bar #" + gateID,
                            mainForm.getStatusTextField(), gateID);
                finishConnection(e2, "Gate bar manager Excp  ", gateID);
            }
            //</editor-fold>

            if (mainForm.tolerance[GateBar.ordinal()][gateID].getLevel() < 0) {
                finishConnection(null, "LED: tolerance depleted for", gateID);
            }
        }
    }

    /**
     * stops serving a gate bar.
     */
    @Override
    public void stopOperation(String reason) {
        if (timerAreYouThere != null)
            timerAreYouThere.cancelTask();
        
        finishConnection(null, reason, gateID);
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
    public void finishConnection(Exception e, String description, byte gateNo) {

        synchronized(mainForm.getSocketMutex()[GateBar.ordinal()][gateNo]) 
        {
            if (isConnected(socket)) 
            {
                String msg =  "Gate bar #" + gateNo;

                addMessageLine(mainForm.getMessageTextArea(), "  ------" + msg + " disconnected");
                logParkingException(Level.INFO, e, description + " " + msg);

                long closeTm = System.currentTimeMillis();

                mainForm.getSockConnStat()[GateBar.ordinal()][gateNo].recordSocketDisconnection(closeTm);
                
                if (DEBUG) {
                    System.out.println("M9. Gate bar #" + gateNo + " disconnected at: " + closeTm);                        
                }
                closeSocket(getSocket(), "while gate bar socket closing");
                socket = null;
            }                
        } 
        
        if (mainForm.getConnectDeviceTimer()[GateBar.ordinal()][gateNo] == null) {
            System.out.println("this never ever happens");
        }
        else {
            mainForm.getConnectDeviceTimer()[GateBar.ordinal()][gateNo].reRunOnce();
            addMessageLine(mainForm.getMessageTextArea(), "Trying to connect to Gate Bar #" + gateNo);
        }        
    }

    /**
     * @return the everConnected
     */
    public boolean isNeverConnected() {
        return neverConnected;
    }
}
