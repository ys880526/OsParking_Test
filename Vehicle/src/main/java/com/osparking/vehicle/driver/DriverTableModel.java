/* 
 * Vehicle, Module--Part of OSParking Software 
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
package com.osparking.vehicle.driver;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Park, Jongbum <Park, Jongbum at Open Source Parking Inc.>
 */
class DriverTableModel extends DefaultTableModel {
    private ManageDrivers parent = null;

    public DriverTableModel() {
    }
    
    public DriverTableModel(Object[][] rowData, Object[] columnNames, 
            ManageDrivers parent) {
        super(rowData, columnNames);
        this.parent = parent;        
    }

    public boolean isCellEditable(int row, int column)
    {
        if (column == 0 || column == 8)
        return false;
        else
        return true;
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {

        Class<?> result = null;
        switch (columnIndex) {
            case 1: case 2: case 3:
            result = String.class;
            break;

            case 0: case 8:
            result = Integer.class;
            break;

            case 4: case 5: case 6: case 7:
            result = Object.class;
            break;
        }
        return result;
    }    

    /**
     * @return the parent
     */
    public ManageDrivers getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(ManageDrivers parent) {
        this.parent = parent;
    }
}
