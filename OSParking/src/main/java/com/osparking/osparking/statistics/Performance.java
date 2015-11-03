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
package com.osparking.osparking.statistics;

/**
 *
 * @author Park, Jongbum <Park, Jongbum at Open Source Parking Inc.>
 */
public class Performance {
    
    /**
     * gate open command statistics
     */
    String commandName;
    int commandCount = 0;
    int commAckDelayTot = 0;
    int commResendCntTot = 0;
    
    /**
     * socket connection break statistics
     */
    
    private long recentCloseTimeMs = 0L; 
    
    /**
     * 
     * @param commandName 
     */
    public Performance(String commandName) {
        this.commandName = commandName;
    }
    
    public void addPerformData (int delayMs, int resendCnt) {
        commandCount++;
        commAckDelayTot += delayMs;
        commResendCntTot += resendCnt;
    }
    
    /**
     * Check if at least one data is stored.
     * @return true, if one or more data is stored; false, otherwise
     */
    public boolean hasData() {
        if (commandCount > 0) 
            return true;
        else
            return false;
    }
}
