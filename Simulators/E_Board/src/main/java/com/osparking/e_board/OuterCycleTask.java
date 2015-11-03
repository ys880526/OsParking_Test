/* 
 * E_Board, Simulator Program--Part of OSParking Software 
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
package com.osparking.e_board;

import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import com.osparking.global.names.EBD_DisplaySetting;
import static com.osparking.global.names.EBD_DisplaySetting.EBD_PERIOD;
import static com.osparking.global.names.EBD_DisplaySetting.MAX_PERIOD;
import javax.swing.JTextField;
import static com.osparking.global.Globals.*;
import static com.osparking.global.names.OSP_enums.EBD_ContentType.VERBATIM;
import static com.osparking.global.names.OSP_enums.EBD_Effects.*;

/**
 *
 * @author Song, YongSeok <Song, YongSeok at Open Source Parking Inc.>
 */
public class OuterCycleTask implements Runnable{
    A_EBD_GUI mainform;
    private byte row;
    EBD_DisplaySetting rowSetting;
    
    JTextField eBoardTextField;
    int textWidth, flowDelta;
    int count = 1;
    private boolean isTextShowing = false;
        
    AffineTransform affinetransform = new AffineTransform();     
    FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
    
    /**
     * 
     * @param mainform
     * @param row
     * @param rowSetting 
     */
    public OuterCycleTask(A_EBD_GUI mainform, byte row, EBD_DisplaySetting rowSetting)
    {
        this.mainform = mainform;
        this.row = row;
        this.rowSetting = rowSetting;
               
        eBoardTextField = (row == TOP_ROW ? mainform.topTextField : mainform.botTextField);
        
        flowDelta = Math.round(MAX_PERIOD * eBoardTextField.getWidth() / (float)rowSetting.displayCycle);
        flowDelta = (flowDelta == 0) ?  1 : flowDelta;
        
        if (rowSetting.displayPattern == RTOL_FLOW || rowSetting.displayPattern == LTOR_FLOW)
            EBD_PERIOD = rowSetting.displayCycle * flowDelta / eBoardTextField.getWidth();
    }
    
    public void run(){
        String renderedContent = null;

        if (rowSetting.contentType == VERBATIM)
            renderedContent = rowSetting.verbatimContent;
        else
            renderedContent = getRenderedContent(rowSetting.contentType, mainform.getID());
        
        if (rowSetting.displayPattern == BLINKING) {
            if (isTextShowing) {
                if(row == TOP_ROW)
                    mainform.topTextField.setText("");
                else
                    mainform.botTextField.setText("");;                
            } else {
                if(row == TOP_ROW)
                    mainform.topTextField.setText(renderedContent);
                else
                    mainform.botTextField.setText(renderedContent);
            }
            isTextShowing = ! isTextShowing;
        } else {
            textWidth = (int)(eBoardTextField.getFont().getStringBounds(renderedContent, frc).getWidth());   
            if(row == TOP_ROW)
                mainform.topTextField.setText(renderedContent);
            else
                mainform.botTextField.setText(renderedContent);
            
            InnerCycleTask innerCycleTask = new InnerCycleTask(mainform, row, rowSetting, textWidth, flowDelta, 
                    row == TOP_ROW ? mainform.topTextField : mainform.botTextField);
            mainform.parking_Display_InnerTimer[row].reschedule(innerCycleTask, 0, EBD_PERIOD);
        }
    }
}