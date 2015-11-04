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
package com.osparking.global;

/**
 *
 * @author Open Source Parking Inc.
 */


public class global2 {

    public static int getFirstPart(String tagRecognized, StringBuilder firstPart) {
        int idx = 0;
        for (   ; idx < tagRecognized.length() && firstPart.length() < 2; idx++) {
            if (Character.isDigit(tagRecognized.charAt(idx))) {
                firstPart.append(tagRecognized.charAt(idx));
            } else {
                if (firstPart.length() > 0)
                    return idx;
            }
        }
        return idx;
    }  
    
    public static void getSecondPart(String tagRecognized, int secondStart, StringBuilder secondPart) {
        //        String secondPart = tagRecognized.substring(
        //                tagRecognized.length() - 4, tagRecognized.length());   
        int idx = secondStart;
        for (   ; idx < tagRecognized.length() && secondPart.length() < 4; idx++) {
            if (Character.isDigit(tagRecognized.charAt(idx))) {
                secondPart.append(tagRecognized.charAt(idx));
            } else {
                if (secondPart.length() > 0)
                    return;
            }
        }
        return;
    }    
}
