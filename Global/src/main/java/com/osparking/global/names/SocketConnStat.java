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
package com.osparking.global.names;

import java.util.logging.Level;
import static com.osparking.global.Globals.addMessageLine;
import static com.osparking.global.Globals.getFormattedRealNumber;
import static com.osparking.global.Globals.logParkingException;
import static com.osparking.global.Globals.logParkingOperation;
import com.osparking.global.names.OSP_enums.DeviceType;

/**
 * Statistics on the socket disconnection period/duration in milli-second unit.
 * @author Open Source Parking Inc.
 */
public class SocketConnStat {
    
    /**
     * socket connection break statistics
     */
    private boolean connected = false;
    private long statChangeTm = 0L; 
    private long disconnectionTotalMs = 0L; 
    private int TOO_LARGE_DISCONN_PERIOD = 100; 
    
    /**
     * Disconnection Period Maximum
     */
    private long disconnPeriodMax = 0L; 
    /**
     * Latest Disconnection Period in Milliseconds.
     */
    private long latest_D_P_Ms = 0L; 
    private int disconnectionCount = 0; 
    ManagerGUI mainForm = null;
    DeviceType deviceType;
    byte deviceID;
    boolean rejectOneLatest_D_P_Ms = false;
    
    public SocketConnStat(ManagerGUI mainForm, DeviceType deviceType, byte deviceID) {
        this.mainForm = mainForm;
        this.deviceType = deviceType;
        this.deviceID = deviceID;
    }
    
    public String getPerformanceDescription() {
        StringBuilder sb = new StringBuilder();
        
        if (disconnectionCount == 0) {
            sb.append(": no socket disconn'");
        } else {
            sb.append(": connection count: ");
            sb.append(disconnectionCount);
            sb.append(System.lineSeparator());            
            sb.append("      disconnection(ms)--avg: ");
            sb.append(getFormattedRealNumber(disconnectionTotalMs/(float)disconnectionCount, 1));
            sb.append(", max: ");
            sb.append(getFormattedRealNumber(disconnPeriodMax, 0));
        }
        sb.append(System.lineSeparator());
        return sb.toString();
    }  
    
    public synchronized void recordSocketConnection(long connectionTm) {
        if (statChangeTm <= connectionTm) {
            
            if (! connected) {
//                System.out.println("change(use): " + statChangeTm);
                if (statChangeTm == 0 || rejectOneLatest_D_P_Ms) { 
                    rejectOneLatest_D_P_Ms = false;
//                    System.out.println("Rejected: " + latest_D_P_Ms);
                } else {
                    disconnectionCount++;
//                    System.out.println("added--: " + latest_D_P_Ms);
                    disconnectionTotalMs += latest_D_P_Ms;
                    if (disconnPeriodMax < latest_D_P_Ms)
                        disconnPeriodMax = latest_D_P_Ms;
                    
                    /**
                     * save large disconnection period and occurrence time for debugging.
                     */
                    if (latest_D_P_Ms > TOO_LARGE_DISCONN_PERIOD)
                        logParkingException(Level.INFO, null, "Large disconn period: " + latest_D_P_Ms, deviceID);
                    latest_D_P_Ms = connectionTm - statChangeTm;
                }
                String msg =  "  ------" + deviceType +" #" + deviceID + " connected";
                addMessageLine(mainForm.getMessageTextArea(), msg);
                logParkingException(Level.INFO, null, msg);                 
                connected = true;
            }
            statChangeTm = connectionTm;
//            System.out.println("change(con): " + statChangeTm);
        }
    }
    
    public synchronized void resetStatChangeTm() {
        statChangeTm = 0;
        latest_D_P_Ms = 0;
//        System.out.println("change(res): " + statChangeTm);
}

    public synchronized void recordSocketDisconnection(long disconnectionTm) {
        if (statChangeTm <= disconnectionTm) {
            if (statChangeTm == 0)
                rejectOneLatest_D_P_Ms = true;
            statChangeTm = disconnectionTm;
//            System.out.println("change(dis): " + statChangeTm);
            connected = false;
        }
    }

    /**
     * @return the connected
     */
    public boolean isConnected() {
        return connected;
    }
}
