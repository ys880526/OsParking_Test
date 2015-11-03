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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static com.osparking.global.names.DB_Access.pwStrengthLevel;
import com.osparking.global.names.OSP_enums.PWStrengthLevel;

public class PasswordValidator {

    private Pattern pattern;
    private Matcher matcher;

    private static final String PW_PATTERN_4DIGITS = "((?=\\d{4}).{4,4})";
    private static final String PW_PATTERN_6ALNUM = "((?=.*[a-zA-Z])(?=.*\\d).{6,40})";
        // one Upper, one Lower, one digit, one special char, 
    private static final String PW_PATTERN_COMPLEX = "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[!@#$%&*()]).{8,40})";

    public PasswordValidator() {
        if (pwStrengthLevel == PWStrengthLevel.FourDigit.ordinal()) 
        {
            pattern = Pattern.compile(PW_PATTERN_4DIGITS);
        } else if (pwStrengthLevel == PWStrengthLevel.SixDigit.ordinal()) 
        {
            pattern = Pattern.compile(PW_PATTERN_6ALNUM);
        } else 
        {
            pattern = Pattern.compile(PW_PATTERN_COMPLEX);
        }
    }

    public boolean isInValidForm(final String password) {
            matcher = pattern.matcher(password);
            return matcher.matches();
    }
    
    public String getPasswordTooltip() 
    {
        if (pwStrengthLevel == PWStrengthLevel.FourDigit.ordinal()) 
        {
            return "Enter a 4 digit number(for details click '?')";
        } 
        else if (pwStrengthLevel == PWStrengthLevel.SixDigit.ordinal()) 
        {
            return "Enter 6 or more digits of alpha-numeric(for details click '?')";
        } 
        else 
        {
            return "Enter 8 or more digits of alphabet, number, and special character(for details click '?')";
        }   
    }
    
    
    public String getWrongPWFormatMsg(short level) 
    {
        StringBuilder sBuilder = new StringBuilder();
        
        sBuilder.append("* Enter password satisfying below conditions:\n");
        sBuilder.append("\n");
        
        if (level == PWStrengthLevel.FourDigit.ordinal()) 
        {
            sBuilder.append("  - four digit number (0~9)\n");
            sBuilder.append("\n");
            sBuilder.append("(e.g., 0123)");
        } 
        else if (level == PWStrengthLevel.SixDigit.ordinal()) 
        {
            sBuilder.append("  - consists of 6 to 40 characters\n");
            sBuilder.append("  - contains at least one English alphabet (a-z,A~Z)\n");
            sBuilder.append("  - includes more than one number key(0-9)\n");
            sBuilder.append("\n");
            sBuilder.append("(e.g., pti34z)");
        } 
        else 
        {
            sBuilder.append("  - consists of 8 to 40 characters\n");
            sBuilder.append("  - contains at least one lower case alphabet (a-z)\n");
            sBuilder.append("  - contains at least one upper case alphabet (A-Z)\n");
            sBuilder.append("  - includes more than one number key(0-9)\n");
            sBuilder.append("  - includes at least one special character shown below\n");
            sBuilder.append("     !  @  #  $  %  &  *  (  )\n");
            sBuilder.append("\n");
            sBuilder.append("(e.g., abM56!xy)");
        }   
        return sBuilder.toString();
    }
    
}
