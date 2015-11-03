/* 
 * OSParking, Parking Lot Management Software
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
package com.osparking.osparking;

import static com.osparking.osparking.ControlGUI.show100percentSizeImageOfGate;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import com.osparking.global.names.CarAdmission;
import static com.osparking.global.names.DB_Access.PIC_HEIGHT;
import static com.osparking.global.names.DB_Access.PIC_WIDTH;
import com.osparking.global.names.GatePanel;
import static com.osparking.global.Globals.*;

/**
 *
 * @author Park, Jongbum <Park, Jongbum at Open Source Parking Inc.>
 */
public class PanelFor2Gates extends GatePanel {
    JList entryList[] = new JList[5];
    final DefaultListModel model_1 = new DefaultListModel();    
    final DefaultListModel model_2 = new DefaultListModel();  
    DefaultListModel models[] = new DefaultListModel[5];
    private JPanel[] Panel_Gates = new JPanel[5];

    /**
     * Creates new form PanelFor2Gates
     */
    public PanelFor2Gates() {
        initComponents();
        CarPicLabels[1] = CarPicLabel1;
        CarPicLabels[2] = CarPicLabel2;
        entryList[1] = List_Gate1;
        entryList[2] = List_Gate2;
        models[1] = model_1;
        models[2] = model_2;
        Panel_Gates[1] = Panel_Gate1;
        Panel_Gates[2] = Panel_Gate2;
    }
    
    @Override   
    public JLabel getPictureLabel(int gateNo) {
        switch (gateNo) 
        {
            case 1: return CarPicLabel1;
            case 2: return CarPicLabel2;
            default:
                return null;
        }
    } 
    
    @Override
    public void resizeComponents(Dimension gatesPanelSize) 
    {
        setComponentSize(this, gatesPanelSize);
        
        int picWidthNew = 0, picHeightNew = 0;

        // Calculate Picture Frame Initial Size
        if (gatesPanelSize.width >= PIC_WIDTH * 2 + 58) {
            picWidthNew = PIC_WIDTH;
        } else {
            picWidthNew = (gatesPanelSize.width - 58) / 2;
        } 

        picHeightNew = picWidthNew * PIC_HEIGHT / PIC_WIDTH;

        // in case of car entry list box height isn't enough adjust picture size by reducing
        // its width
        if (gatesPanelSize.height - picHeightNew - 10 < LIST_HEIGHT_MIN) {
            picHeightNew = gatesPanelSize.height - LIST_HEIGHT_MIN - 10;
            picWidthNew =  picHeightNew * PIC_WIDTH / PIC_HEIGHT;
        } 

        setComponentSize(Panel_Gate1, 
                new Dimension(picWidthNew + 23, gatesPanelSize.height));
        setComponentSize(Panel_Gate2, 
                new Dimension(picWidthNew + 23, gatesPanelSize.height));

        for (int gateNo = 1; gateNo <= 2; gateNo++)
        {
            setComponentSize(
                    CarPicLabels[gateNo], new Dimension(picWidthNew, picHeightNew));

            if (originalImgWidth[gateNo] > 0)
            {
                ImageIcon iIcon = createStretchedIcon(CarPicLabels[gateNo].getPreferredSize(), 
                        getNoPictureImg(), false); // after components resized
                CarPicLabels[gateNo].setIcon(null);
                CarPicLabels[gateNo].revalidate();
                CarPicLabels[gateNo].setIcon(iIcon);
            }
        }
        revalidate();

        int width = gatesPanelSize.width - picWidthNew * 2;

        if (width > 60)
        { // PanelRestArea
            add(PanelMargin);
            try {
                setComponentSize(MarginLabel, new Dimension(width - 58, getSize().height));
                revalidate();

                BufferedImage originalImg 
                        = ImageIO.read(getClass().getResourceAsStream(restAreaImage));
                MarginLabel.setIcon(null);
                MarginLabel.revalidate();
                MarginLabel.setIcon(
                        createStretchedIcon(MarginLabel.getSize(), originalImg, true));
            } catch (Exception e) {
                logParkingException(Level.SEVERE, e, "(Window size update event handler)");
            }
        }
        else
        {
            remove(PanelMargin);
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Panel_Gate1 = new javax.swing.JPanel();
        CarPicLabel1 = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        ScrollPane_Gate1 = new javax.swing.JScrollPane();
        List_Gate1 = new javax.swing.JList();
        Panel_Gate2 = new javax.swing.JPanel();
        CarPicLabel2 = new javax.swing.JLabel();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        ScrollPane_Gate2 = new javax.swing.JScrollPane();
        List_Gate2 = new javax.swing.JList();
        PanelMargin = new javax.swing.JPanel();
        MarginLabel = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));

        Panel_Gate1.setBackground(MainBackground);
        Panel_Gate1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Gate 1 Title", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dotum", 1, 14))); // NOI18N
        Panel_Gate1.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        Panel_Gate1.setName("Panel_Gate1"); // NOI18N
        Panel_Gate1.setPreferredSize(new java.awt.Dimension(343, 450));
        Panel_Gate1.setLayout(new javax.swing.BoxLayout(Panel_Gate1, javax.swing.BoxLayout.Y_AXIS));

        CarPicLabel1.setBackground(MainBackground);
        CarPicLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        CarPicLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        CarPicLabel1.setAlignmentX(0.5F);
        CarPicLabel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        CarPicLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        CarPicLabel1.setIconTextGap(0);
        CarPicLabel1.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        CarPicLabel1.setMinimumSize(new java.awt.Dimension(100, 100));
        CarPicLabel1.setName(""); // NOI18N
        CarPicLabel1.setOpaque(true);
        CarPicLabel1.setPreferredSize(new java.awt.Dimension(303, 200));
        CarPicLabel1.setVerifyInputWhenFocusTarget(false);
        CarPicLabel1.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        CarPicLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                CarPicLabel1MouseClicked(evt);
            }
        });
        Panel_Gate1.add(CarPicLabel1);
        Panel_Gate1.add(filler2);

        ScrollPane_Gate1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Recent Car Arrivals", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("Dotum", 1, 13))); // NOI18N
        ScrollPane_Gate1.setPreferredSize(new java.awt.Dimension(302, 155));

        List_Gate1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        List_Gate1.setFont(new java.awt.Font("Dotum", 1, 12)); // NOI18N
        List_Gate1.setModel((DefaultListModel<CarAdmission>)admissionListModel[1]);
        List_Gate1.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        List_Gate1.setMinimumSize(new java.awt.Dimension(45, 240));
        List_Gate1.setName(""); // NOI18N
        ScrollPane_Gate1.setViewportView(List_Gate1);

        Panel_Gate1.add(ScrollPane_Gate1);

        add(Panel_Gate1);

        Panel_Gate2.setBackground(MainBackground);
        Panel_Gate2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Gate 2 Title", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dotum", 1, 14))); // NOI18N
        Panel_Gate2.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        Panel_Gate2.setName("Panel_Gate1"); // NOI18N
        Panel_Gate2.setPreferredSize(new java.awt.Dimension(343, 450));
        Panel_Gate2.setLayout(new javax.swing.BoxLayout(Panel_Gate2, javax.swing.BoxLayout.Y_AXIS));

        CarPicLabel2.setBackground(MainBackground);
        CarPicLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        CarPicLabel2.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        CarPicLabel2.setAlignmentX(0.5F);
        CarPicLabel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        CarPicLabel2.setDoubleBuffered(true);
        CarPicLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        CarPicLabel2.setIconTextGap(0);
        CarPicLabel2.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        CarPicLabel2.setMinimumSize(new java.awt.Dimension(100, 100));
        CarPicLabel2.setName(""); // NOI18N
        CarPicLabel2.setOpaque(true);
        CarPicLabel2.setPreferredSize(new java.awt.Dimension(303, 200));
        CarPicLabel2.setVerifyInputWhenFocusTarget(false);
        CarPicLabel2.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        CarPicLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                CarPicLabel2MouseClicked(evt);
            }
        });
        Panel_Gate2.add(CarPicLabel2);
        Panel_Gate2.add(filler4);

        ScrollPane_Gate2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Recent Car Arrivals", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("Dotum", 1, 13))); // NOI18N
        ScrollPane_Gate2.setPreferredSize(new java.awt.Dimension(302, 155));

        List_Gate2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        List_Gate2.setFont(new java.awt.Font("Dotum", 1, 12)); // NOI18N
        List_Gate2.setModel((DefaultListModel<CarAdmission>)admissionListModel[2]);
        List_Gate2.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        List_Gate2.setMinimumSize(new java.awt.Dimension(45, 240));
        List_Gate2.setName(""); // NOI18N
        ScrollPane_Gate2.setViewportView(List_Gate2);

        Panel_Gate2.add(ScrollPane_Gate2);

        add(Panel_Gate2);

        PanelMargin.setBackground(MainBackground);
        PanelMargin.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        PanelMargin.setMinimumSize(new java.awt.Dimension(145, 111));
        PanelMargin.setPreferredSize(new java.awt.Dimension(245, 261));
        PanelMargin.setLayout(new javax.swing.BoxLayout(PanelMargin, javax.swing.BoxLayout.Y_AXIS));

        MarginLabel.setBackground(new java.awt.Color(255, 153, 153));
        MarginLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        MarginLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        MarginLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        MarginLabel.setMinimumSize(new java.awt.Dimension(100, 100));
        MarginLabel.setName(""); // NOI18N
        MarginLabel.setPreferredSize(new java.awt.Dimension(500, 250));
        MarginLabel.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        PanelMargin.add(MarginLabel);

        /*

        add(PanelMargin);
        */
    }// </editor-fold>//GEN-END:initComponents

    private void CarPicLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CarPicLabel1MouseClicked
        show100percentSizeImageOfGate(1, getNoPictureImg());
    }//GEN-LAST:event_CarPicLabel1MouseClicked

    private void CarPicLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CarPicLabel2MouseClicked
        show100percentSizeImageOfGate(2, getNoPictureImg());
    }//GEN-LAST:event_CarPicLabel2MouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JLabel CarPicLabel1;
    public javax.swing.JLabel CarPicLabel2;
    public javax.swing.JList List_Gate1;
    public javax.swing.JList List_Gate2;
    public javax.swing.JLabel MarginLabel;
    public javax.swing.JPanel PanelMargin;
    public javax.swing.JPanel Panel_Gate1;
    public javax.swing.JPanel Panel_Gate2;
    private javax.swing.JScrollPane ScrollPane_Gate1;
    private javax.swing.JScrollPane ScrollPane_Gate2;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler4;
    // End of variables declaration//GEN-END:variables

    @Override
    public void displaySizes() {
        StringBuffer sb = new StringBuffer();
        
        sb.append("Whole");         
        sb.append(System.lineSeparator());
        sb.append("Pan = ");        
        sb.append(getSizeString(this));
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        
        sb.append("Gate1");         
        sb.append(System.lineSeparator());
        sb.append("Pan = ");        
        sb.append(getSizeString(Panel_Gate1));
        sb.append(System.lineSeparator());
        sb.append("PIC = ");        
        sb.append(getSizeString(CarPicLabel1));
        sb.append(System.lineSeparator());
        sb.append("ScL = ");        
        sb.append(getSizeString(ScrollPane_Gate1));
        sb.append(System.lineSeparator());
        sb.append("LsT = ");        
        sb.append(getSizeString(List_Gate1));
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        
        sb.append("Gate2");         
        sb.append(System.lineSeparator());
        sb.append("Pan = ");        
        sb.append(getSizeString(Panel_Gate2));
        sb.append(System.lineSeparator());
        sb.append("PIC = ");        
        sb.append(getSizeString(CarPicLabel2));
        sb.append(System.lineSeparator());
        sb.append("ScL = ");        
        sb.append(getSizeString(ScrollPane_Gate2));
        sb.append(System.lineSeparator());        
        sb.append("LsT = ");        
        sb.append(getSizeString(List_Gate2));
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        
        sb.append("Margin");         
        sb.append(System.lineSeparator());
        sb.append("Pan = ");        
        sb.append(getSizeString(PanelMargin ));
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());        
        sb.append("PIC = ");        
        sb.append(getSizeString(MarginLabel));
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());        
                
        JOptionPane.showMessageDialog(null,
            sb.toString(), "InfoBox: CarPicLabel", JOptionPane.INFORMATION_MESSAGE);        
    }    

    @Override
    public JList getEntryList(int gateNo) {
        return entryList[gateNo];
    }

    @Override
    public DefaultListModel getDefaultListModel(int gateNo) {
        return models[gateNo];
    }

    @Override
    public JPanel getPanel_Gate(int gateNo) {
        return Panel_Gates[gateNo];
    }
}
