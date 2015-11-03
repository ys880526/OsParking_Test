/* 
 * Camera, Simulator Program--Part of OSParking Software 
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
package com.osparking.camera.stat;

import java.util.logging.Level;
import static com.osparking.global.Globals.getFormattedRealNumber;
import static com.osparking.global.Globals.logParkingException;

/**
 * Statistics on the socket disconnection period/duration in milli-second unit.
 * @author Park, Jongbum <Park, Jongbum at Open Source Parking Inc.>
 */
public class SocketDisconnection {
    
    /**
     * socket connection break statistics
     */
    
    private boolean isConnected = false;
    private long recentCloseTimeMs = 0L; 
    private long disconnectionTotalMs = 0L; 
    private int disconnectionCount = 0; 
    
    public String getPerformanceDescription() {
        StringBuilder sb = new StringBuilder();
        
        if (disconnectionCount == 0) {
            sb.append("\tno socket disconnections.");
            sb.append(System.lineSeparator());
        } else {
            sb.append("\taverage disconnection time: ");
            sb.append(getFormattedRealNumber(disconnectionTotalMs/(float)disconnectionCount, 1));
            sb.append("(ms)");
            sb.append(System.lineSeparator());
            sb.append("\tsocket reconnect count: ");
            sb.append(disconnectionCount);
            sb.append(System.lineSeparator());            
        }
        return sb.toString();
    }

    public void recordSocketDisconnection() {
        if (isConnected) {
            isConnected = false;
            recentCloseTimeMs = System.currentTimeMillis();
        }
    }

    public void recordSocketConnection() {
        if (! isConnected) {
            isConnected = true;
            long currentMs = System.currentTimeMillis();
            if (currentMs < recentCloseTimeMs) {
                logParkingException(Level.SEVERE, null, 
                        "later event connection precedes previous disconnection");                
                throw new AssertionError("later event connection precedes previous disconnection");
            }
            
            if (recentCloseTimeMs > 0) {
                disconnectionCount++;
                disconnectionTotalMs += (currentMs - recentCloseTimeMs);
            }
        }
    }
}
