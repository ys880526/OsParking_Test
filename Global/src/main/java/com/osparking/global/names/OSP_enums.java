/* 
 * Global, Library--Part of OSParking Software 
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
package com.osparking.global.names;

/**
 *
 * @author Park, Jongbum <Park, Jongbum at Open Source Parking Inc.>
 */
public class OSP_enums {

    public enum VehicleCol {
        RowNo(0),
        PlateNumber(1),
        Name(2),
        Affiliation(3),
        Building(4),
        OtherInfo(5),
        CellPhone(6),
        Phone(7),
        Notification(8),
        Whole(9),
        Permitted(10),
        Causes(11),
        Creation(12),
        Modification(13),
        SeqNo(14); // driver's sequence number

        private int numVal;

        VehicleCol(int numVal) {
            this.numVal = numVal;
        }

        public int getNumVal() {
            return numVal;
        }    
    }    

    public enum PWStrengthLevel {
        FourDigit,
        SixDigit,
        Complex
    }    
    
    public enum SearchPeriod {
        OneHour,
        OneDay,
        GivenPeriod
    }    
    
    public enum ODS_TYPE {
        AFFILIATION,
        BUILDING
    }    
    
    public enum DriverCol {
        RowNo(0),
        DriverName(1),
        CellPhone(2),
        LandLine(3),
        AffiliationL1(4),
        AffiliationL2(5),
        BuildingNo(6),
        UnitNo(7),
        SEQ_NO(8);

        private int numVal;

        DriverCol(int numVal) {
            this.numVal = numVal;
        }

        public int getNumVal() {
            return numVal;
        }
    }    
    
    public enum BarOperation {
        /**
         * Gate bar opened as a result of an arrival of a vehicle allowed to 
         * park legally.
         */
        ALLOWED, 
        /**
         * Attendant in charge isn't able to take care the gate at the moment. 
         * He/She set the gate on automatic open mode as soon as the tag 
         * is recognized.
         */
        LAZY_ATT,
        /**
         * Attendant opened the gate manually. It could be done for a visitor,
         * in case of LPR malfunction, or for a disallowed car, etc.
         */
        MANUAL, 
        /**
         * Gate remained closed since the attendant disallowed a car to enter.
         */
        REMAIN_CLOSED
    }    
    
    public enum OPType {
        START, STOP        
    }
    
    public enum MsgCode {
        AreYouThere, IAmHere, DeviceID, ID_Ack, CarImage, Img_ACK,
        Open, Open_ACK, EBD_Default, 

        /**
         * Interrupt E-Board upper row display and display current message temporarily.
         */
        EBD_INTERRUPT1, 

        /**
         * Interrupt E-Board lower row display and display current message temporarily.
         */
        EBD_INTERRUPT2, 

        /**
         * Change default display of the E-Board higher row permanently.
         */
        EBD_DEFAULT1,

        /**
         * Change default display of the E-Board lower row permanently.
         */
        EBD_DEFAULT2,

        EBD_GetID, EBD_ACK, EBD_ID_ACK, JustBooted
    }

    public enum E_Board_Status {
        NORMAL, 
        CarIN
    }

    public enum VersionType {
        DEVELOP, TESTRUN, RELEASE
    }    
    
    /**
     *  General System Operation Logging Level
     */ 
    public enum OpLogLevel {
        LogAlways, // log this operation always
        SettingsChange, // log system settings change
        EBDsettingsChange // log system settings change plus E-board settings change
    }    
    
    public enum FormMode {
        MODIFICATION,
        CREATION,
        SEARCHING
    }      
    
    public enum DeviceType {
        Camera, GateBar, E_Board
    }    
    
    public enum EBD_CycleType {
        EBD_FLOW_CYCLE,
        EBD_BLINK_CYCLE
    }
    
    public enum PermissionType {
        /**
         * Car at the entry gate is allowed to park at this parking lot. So, the gate was opened
         * to allow the car to come on in.
         */
        ALLOWED, DISALLOWED, UNREGISTERED, BADTAGFORMAT       
    }  

    public enum EBD_DisplayUsage {        
        DEFAULT_TOP_ROW(1),            // used for the top row when no vehicle arrives
        DEFAULT_BOTTOM_ROW(2),      // used for the bottom row when no vehicle arrives 
        CAR_ENTRY_TOP_ROW(3),         // used for the top row when vehicle arrived at the gate entry
        CAR_ENTRY_BOTTOM_ROW(4);   // used for the bottom row when vehicle arrived at the gate entry
        private final int val;
        private EBD_DisplayUsage(int v) { val = v; }
        
        /**
         * A unique integer number assigned for each usage row.
         * 
         * @return integer value starting 1
         */
        public int getVal() { return val; }        
    }

    public enum EBD_ContentType {
        VERBATIM,            // display as it is (character by character)
        VEHICLE_TAG,        // car license tag number
        REGISTRATION_STAT,      // registered, un-registered, parking-restricted
        GATE_NAME,        // gate name which is stored in the system settings already 
        CURRENT_DATE,     // format: 20XX-12-31(Mon.)
        CURRENT_TIME,     // format: AM/PM HH:MM:SS
        CURRENT_DATE_TIME // format: 20XX-12-31(Mon.) HH:MM:SS AM/PM
    };
            
    public enum EBD_Effects {
        RTOL_FLOW,            // flow to the left
        LTOR_FLOW,           // flow to the right
        BLINKING,                 // blink    
        STILL_FRAME           // stationary
    };
    
    public enum EBD_Fonts {
        Dialog,
        DialogInput,
        Microsoft_NeoGothic,
        Monospaced,
        Sans_Serif
    }   
    
    public enum EBD_Colors {
        RED,
        ORANGE,
        GREEN,
        BLACK,
        BLUE
    }
}
