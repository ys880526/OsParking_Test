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

import static com.osparking.global.Globals.MAX_TOLERANCE;

/**
 *
 * @author Open Source Parking Inc.
 */
public class ToleranceLevel {
    private int level = MAX_TOLERANCE;// + 10;
    
    public void  assignMAX() {
        level = MAX_TOLERANCE;
    }
    
    public int decrease() {
        if (level >= 0)
            return level--;
        return level;
    }

    /**
     * @return the level
     */
    public int getLevel() {
        return level;
    }
}
