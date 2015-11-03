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

import com.osparking.global.names.EBD_DisplaySetting;
import java.awt.Insets;
import javax.swing.JTextField;
import static com.osparking.global.Globals.*;
import com.osparking.global.names.OSP_enums.EBD_Effects;
import static com.osparking.global.names.OSP_enums.EBD_Effects.BLINKING;

/**
 *
 * @author Song, YongSeok <Song, YongSeok at Open Source Parking Inc.>
 */
public class InnerCycleTask implements Runnable {
    A_EBD_GUI mainform;
    private byte row;
    EBD_DisplaySetting rowSetting;
    int textWidth, flowDelta;    
    
    private JTextField eBoardRow;
    private int topMG, leftMG, botMG, rightMG;

    int top_count=  1;
    int bot_count = 1;
    int count = 1;
    
    public InnerCycleTask(A_EBD_GUI mainform, byte row, EBD_DisplaySetting rowSetting, int textWidth, 
            int flowDelta, JTextField eBoardRow){
        this.mainform = mainform;
        this.row = row;
        this.rowSetting = rowSetting;
        
        this.textWidth = textWidth;
        this.flowDelta = flowDelta;
        
        this.eBoardRow = eBoardRow;
        initMargin(eBoardRow, rowSetting.displayPattern);
    }
    
    public void run(){
        
        switch(rowSetting.displayPattern) {
            case RTOL_FLOW : 
                if(leftMG > -textWidth){
                    if (row == TOP_ROW) 
                        mainform.topTextField.setMargin(new Insets(topMG, leftMG -= flowDelta, botMG, rightMG));
                    else
                        mainform.botTextField.setMargin(new Insets(topMG, leftMG -= flowDelta, botMG, rightMG));
                        
                }else{
                    mainform.parking_Display_InnerTimer[row].cancelTask();
                    mainform.parking_Display_OuterTimer[row].reRunOnce();
                }
                break;
                
            case LTOR_FLOW : 
                if(rightMG > -textWidth){
                    if (row == TOP_ROW) {
                        mainform.topTextField.setMargin(new Insets(topMG, leftMG, botMG, rightMG -= flowDelta));
                    } else {
                        mainform.botTextField.setMargin(new Insets(topMG, leftMG, botMG, rightMG -= flowDelta));
                    }                    
                }else{
                    mainform.parking_Display_InnerTimer[row].cancelTask();
                    mainform.parking_Display_OuterTimer[row].reRunOnce();                    
                }
                break;
                
            default:
                break;                
        }        
        
        eBoardRow.repaint();
    }
    
    public void initMargin(JTextField eboard, EBD_Effects pattern){
        switch (pattern) {
            case RTOL_FLOW:
                eboard.setHorizontalAlignment(javax.swing.JTextField.LEFT);
                topMG = eboard.getMargin().top;
                leftMG = eboard.getWidth();
                botMG = eboard.getMargin().bottom;
                rightMG = -textWidth;
                break;

            case LTOR_FLOW:
                eboard.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
                topMG = eboard.getMargin().top;
                leftMG = -textWidth;
                botMG = eboard.getMargin().bottom;
                rightMG = eboard.getWidth();
                break;
                
            case STILL_FRAME:
                eboard.setHorizontalAlignment(javax.swing.JTextField.CENTER);
                eboard.setMargin(new Insets(2, 2, 2, 2) );
                break;
                
            case BLINKING:
                eboard.setHorizontalAlignment(javax.swing.JTextField.CENTER);
                eboard.setMargin(new Insets(2, 2, 2, 2) );
                break;
                
            default:
                break;
        }
    }
}
    
