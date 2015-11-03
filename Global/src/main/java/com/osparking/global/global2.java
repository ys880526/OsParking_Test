package com.osparking.global;

/**
 *
 * @author Park Jongbum <Park Jongbum at Open Source Parking Inc.>
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
