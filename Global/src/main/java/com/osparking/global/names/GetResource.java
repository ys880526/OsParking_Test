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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import static com.osparking.global.Globals.logParkingException;

/**
 *
 * @author Park, Jongbum <Park, Jongbum at Open Source Parking Inc.>
 */
public class GetResource {
    public static final String resourceFolder = "/resources/";
    
    public static BufferedImage getBufferedImage(String filename) {
        String currDir = System.getProperty("user.dir");
        
        BufferedImage originalImg = null;
        File sourceFile = new File(resourceFolder + filename);

        try {
            originalImg = ImageIO.read(sourceFile);
        } catch (IOException ex) {
            logParkingException(Level.SEVERE, ex, "(Stretching Help Image File)");
        } finally {
            return originalImg;
        }
    }         
}