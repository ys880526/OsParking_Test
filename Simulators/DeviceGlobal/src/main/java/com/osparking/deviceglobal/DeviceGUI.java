/* 
 * DeviceGlobal, Library--Part of OSParking Software 
 * Copyright (C) 2015 Open Source Parking Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.osparking.deviceglobal;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import com.osparking.global.names.DeviceReader;
import com.osparking.global.names.ParkingTimer;
import com.osparking.global.names.ToleranceLevel;

/**
 *
 * @author Park, Jongbum <Park, Jongbum at Open Source Parking Inc.>
 */
public interface DeviceGUI {

    byte getID();
    DeviceReader getReader();
    JTextField getCriticalInfoTextField();
    JTextField getManagerIPaddr();
    ToleranceLevel getTolerance();
    JLabel getConnectionLED();
    JTextArea getMessageTextArea();
    Object getSocketConnection();
    String getDeviceType();
    Object getSocketMUTEX();
    boolean isSHUT_DOWN();    

    public ParkingTimer getAcceptManagerTimer();
}

    
