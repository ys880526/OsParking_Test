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

import java.awt.Font;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import com.osparking.global.Globals;
import static com.osparking.global.Globals.DEBUG;
import static com.osparking.global.Globals.LED_PERIOD;
import static com.osparking.global.Globals.PULSE_PERIOD;
import static com.osparking.global.Globals.getPort;
import static com.osparking.global.Globals.logParkingException;
import com.osparking.global.names.OSP_enums.DeviceType;
import com.osparking.osparking.ControlGUI;
import static com.osparking.global.names.DB_Access.deviceIP;

/**
 * 
 * @author Park, Jongbum <Park, Jongbum at Open Source Parking Inc.>
 */
public class ConnectDeviceTask implements Runnable {
    ControlGUI managerGUI;
    DeviceType deviceType;
    byte deviceID = 0;
    int seq = 0;
    
    public ConnectDeviceTask(ControlGUI managerGUI, DeviceType deviceType, byte deviceID) {
        this.managerGUI = managerGUI;
        this.deviceType = deviceType;
        this.deviceID = deviceID;
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                //<editor-fold desc="-- establish socket connection">
                if (DEBUG && (seq == 0 || seq % 20 == 1)) {
                    managerGUI.getStatusTextField().setText(
                            "Socket() IP: " + deviceIP[deviceType.ordinal()][deviceID] + ", port: " + 
                                    (getPort(deviceType, Globals.versionType) + deviceID) + " (" + seq + "-th)");      
                }
                synchronized (managerGUI.getSocketMutex()[deviceType.ordinal()][deviceID]) 
                {                
                    Socket deviceSocket = new Socket();
                    deviceSocket.connect(new InetSocketAddress(deviceIP[deviceType.ordinal()][deviceID], 
                            getPort(deviceType, Globals.versionType) + deviceID) , LED_PERIOD);  

                    managerGUI.getSockConnStat()[deviceType.ordinal()][deviceID].recordSocketConnection(
                            System.currentTimeMillis());               

                    deviceSocket.setTcpNoDelay(true);
                    deviceSocket.setSoTimeout(PULSE_PERIOD);
                    managerGUI.getStatusTextField().setFont(new Font(
                            managerGUI.getStatusTextField().getFont().getFontName(), Font.PLAIN, 
                            managerGUI.getStatusTextField().getFont().getSize()));  
                    //</editor-fold>                  

                    managerGUI.tolerance[deviceType.ordinal()][deviceID].assignMAX();
                    // ...
                    if (managerGUI.getDeviceManagers()[deviceType.ordinal()][deviceID] == null) {
                        System.out.println("It is null");
                    }
                    managerGUI.getDeviceManagers()[deviceType.ordinal()][deviceID].setSocket(deviceSocket);
                    managerGUI.getSocketMutex()[deviceType.ordinal()][deviceID].notifyAll();
                }
                return;
            } catch (SocketTimeoutException ex) {
            } catch (IOException e) {
                //<editor-fold desc="--handle ioexception">
                if (e.getMessage().indexOf("refused") >= 0) {
                    String msg = deviceType + " #"  + deviceID + " refused connection: " + (++seq) + " times";

                    managerGUI.getStatusTextField().setText(msg); 
                    if (seq % 20 == 1) {
                        logParkingException(Level.INFO, null, msg + System.lineSeparator(), deviceID);
                    }
                    managerGUI.getStatusTextField().setText(msg);

                } else {
                    String tip = "";
                    if (e.getMessage().indexOf("timed out") >= 0) {
                    } else {
                        logParkingException(Level.SEVERE, e, "IOEx during socket connection", deviceID);
                    }
                }
                //</editor-fold>
            } 
        }
    }
}
