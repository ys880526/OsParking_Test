package com.osparking.global;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Open Source Parking Inc.
 */


public class global2Test {
    
    public global2Test() {
        
    }

    @Test
    public void testGetFirstPart() {
        System.out.println("getFirstPart");
        StringBuilder firstPart = new StringBuilder();
        
        String tagRecognized = "s32456";
        int result = global2.getFirstPart(tagRecognized, firstPart);
        
        firstPart = new StringBuilder();
        tagRecognized = "s3tr2456";
        int r2esult = global2.getFirstPart(tagRecognized, firstPart);
        
        firstPart = new StringBuilder();
        tagRecognized = "s32gh456";
        int r3esult = global2.getFirstPart(tagRecognized, firstPart);

        int expResult = 0;
    }
    
    @Test
    public void testGetSecondPart() {
        System.out.println("getSecondPart");
        StringBuilder firstPart = new StringBuilder();
        StringBuilder secondPart = new StringBuilder();
        
        String tagRecognized = "s32456";
        global2.getSecondPart(tagRecognized, 
                global2.getFirstPart(tagRecognized, firstPart), secondPart);
        
        firstPart = new StringBuilder();
        secondPart = new StringBuilder();
        tagRecognized = "s3tr2456";
        global2.getSecondPart(tagRecognized, 
                global2.getFirstPart(tagRecognized, firstPart), secondPart);
        
        firstPart = new StringBuilder();
        secondPart = new StringBuilder();
        tagRecognized = "s32gh456";
        global2.getSecondPart(tagRecognized,
                global2.getFirstPart(tagRecognized, firstPart), secondPart);

        int expResult = 0;
    }
}
