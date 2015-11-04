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
package com.osparking.vehicle;

import com.osparking.global.Globals;
import static com.osparking.global.Globals.OSPiconList;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultCaret;
import static com.osparking.global.Globals.RET_CANCEL;
import static com.osparking.global.Globals.createStretchedIcon;
import static com.osparking.global.Globals.font_Size;
import static com.osparking.global.Globals.font_Style;
import static com.osparking.global.Globals.font_Type;
import static com.osparking.global.Globals.logParkingException;
import com.osparking.global.names.ImageDisplay;
import com.osparking.global.names.OSP_enums.ODS_TYPE;

/**
 *
 * @author Open Source Parking Inc.
 */
public class ODS_HelpJDialog extends javax.swing.JDialog {

    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int REOT_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;
    
    ODS_TYPE odsType;

    /**
     * Creates new form NewOkCancelDialog
     */
    public ODS_HelpJDialog(java.awt.Frame parent, boolean modal, String helpTitle, ODS_TYPE odsType) {        
        super(parent, modal);
        this.odsType = odsType;
        initComponents();
        setIconImages(OSPiconList);
        setTitle("Help on '" + (odsType == ODS_TYPE.AFFILIATION ? "Affliation" : "Building") + "' ods File");
        setHelpContents(helpTitle, odsType);

        // Close the dialog when Esc is pressed
        String cancelName = "cancel";
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), cancelName);
        ActionMap actionMap = getRootPane().getActionMap();
        actionMap.put(cancelName, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                doClose(RET_CANCEL);
            }
        });
    }

    /**
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(32767, 20));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(32767, 20));
        jPanel3 = new javax.swing.JPanel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        HelpTitleLabel = new javax.swing.JLabel();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        closeButton = new javax.swing.JButton();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        jScrollPane1 = new javax.swing.JScrollPane(topTextArea,
            JScrollPane.VERTICAL_SCROLLBAR_NEVER,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        topTextArea = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        odsHelpLabel = new javax.swing.JLabel();

        setTitle("Help on ods File");
        setMinimumSize(new java.awt.Dimension(560, 550));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.PAGE_AXIS));

        jPanel1.setLayout(new java.awt.BorderLayout());
        jPanel1.add(filler1, java.awt.BorderLayout.SOUTH);
        jPanel1.add(filler2, java.awt.BorderLayout.NORTH);

        jPanel3.setMinimumSize(new java.awt.Dimension(217, 30));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));
        jPanel3.add(filler3);

        HelpTitleLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        HelpTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        HelpTitleLabel.setText("<Help Window Title>");
        HelpTitleLabel.setToolTipText("");
        HelpTitleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        HelpTitleLabel.setMaximumSize(new java.awt.Dimension(300, 23));
        HelpTitleLabel.setMinimumSize(new java.awt.Dimension(300, 23));
        HelpTitleLabel.setPreferredSize(new java.awt.Dimension(300, 23));
        jPanel3.add(HelpTitleLabel);
        jPanel3.add(filler4);

        closeButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        closeButton.setText("Close");
        closeButton.setMaximumSize(new java.awt.Dimension(90, 40));
        closeButton.setMinimumSize(new java.awt.Dimension(90, 40));
        closeButton.setPreferredSize(new java.awt.Dimension(90, 40));
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        jPanel3.add(closeButton);
        getRootPane().setDefaultButton(closeButton);
        jPanel3.add(filler5);

        jPanel1.add(jPanel3, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1);

        jScrollPane1.setHorizontalScrollBar(null);

        topTextArea.setColumns(20);
        topTextArea.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        topTextArea.setRows(5);
        topTextArea.setMaximumSize(new java.awt.Dimension(32767, 32767));
        topTextArea.setPreferredSize(new java.awt.Dimension(511, 94));
        jScrollPane1.setViewportView(topTextArea);

        getContentPane().add(jScrollPane1);

        jPanel2.setLayout(new java.awt.BorderLayout());

        odsHelpLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        odsHelpLabel.setToolTipText("Click to Close");
        odsHelpLabel.setPreferredSize(new java.awt.Dimension(572, 378));
        odsHelpLabel.setVerifyInputWhenFocusTarget(false);
        odsHelpLabel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                odsHelpLabelMouseMoved(evt);
            }
        });
        odsHelpLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                odsHelpLabelMouseClicked(evt);
            }
        });
        jPanel2.add(odsHelpLabel, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        doClose(RET_OK);
    }//GEN-LAST:event_closeButtonActionPerformed

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    private void odsHelpLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_odsHelpLabelMouseClicked
        if (Globals.DEBUG) {
            System.out.println("x: " + evt.getX() + ", y: " + evt.getY());
        }
                
        if (mouseInWrongExampleImageButton(evt.getX(), evt.getY())) {
            String imgFilename = null;
            String title = null;

            if (odsType == ODS_TYPE.AFFILIATION) {
                imgFilename = "/affiliation_wrong_Eng.png";
                title = "Wrong Affiliation Example in ods file";
            }  else {
                imgFilename = "/building_wrong_Eng.png";
                title = "Wrong Building Example in ods file";
            }
            ImageDisplay bigImage = new ImageDisplay(imgFilename, title);
            bigImage.setVisible(true);               
        } 
    }//GEN-LAST:event_odsHelpLabelMouseClicked

    private void odsHelpLabelMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_odsHelpLabelMouseMoved
        boolean mouseCurrentlyInImageButton 
                = mouseInWrongExampleImageButton(evt.getX(), evt.getY());
        
        if (mousePreviouslyInImageButton)
        {
            if (!mouseCurrentlyInImageButton) {
                odsHelpLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                mousePreviouslyInImageButton = false;
            }            
        } else {
            if (mouseCurrentlyInImageButton) {
                odsHelpLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                mousePreviouslyInImageButton = true;
            }
        }
    }//GEN-LAST:event_odsHelpLabelMouseMoved
    
    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel HelpTitleLabel;
    private javax.swing.JButton closeButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel odsHelpLabel;
    private javax.swing.JTextArea topTextArea;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;

    private void setHelpContents(String helpTitle, ODS_TYPE odsType) {
        HelpTitleLabel.setText(helpTitle);
        
        DefaultCaret caret = (DefaultCaret)topTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);   
        
        StringBuffer sb = new StringBuffer();
        sb.append("\u278A Default file type of a office SW 'OpenOffice Calc'");
        sb.append(System.getProperty("line.separator"));
        sb.append("\u278B Creatable using MS Excel, OpenOffice Calc, etc.");
        sb.append(System.getProperty("line.separator"));
        sb.append("\u278C In MS Excel, 'ods' can be created by --");
        sb.append(System.getProperty("line.separator"));
        sb.append("     [File] > [Save As...] > [File Type: (choose) 'OpenDocu...'");
        topTextArea.setText(sb.toString());
        
        ImageIcon odsHelp_icon = null;
        String filename = null;
        
        if (odsType == ODS_TYPE.AFFILIATION) {
            filename = "/affiliation_good_Eng.png";
        }
        else {
            filename = "/building_good_Eng.png";
        }
        
        try {
            BufferedImage originalImg = ImageIO.read(getClass().getResource(filename));
            odsHelp_icon = createStretchedIcon(odsHelpLabel.getPreferredSize(), originalImg, false);
        } catch (Exception ex) {
            logParkingException(Level.SEVERE, ex, "(while stretching help image file");
        }
        odsHelpLabel.setIcon(odsHelp_icon); 
    }

    private static boolean mousePreviouslyInImageButton = false;
    
    private boolean mouseInWrongExampleImageButton(int xAxis, int yAxis) {

        if (odsType == ODS_TYPE.AFFILIATION) {
            if (xAxis > 365 && xAxis < 545 && yAxis > 315 && yAxis < 333)
            {
                return true;
            }
            else
            {
                return false;
            }
        } else {
            if (xAxis > 364 && xAxis < 545 && yAxis > 315 && yAxis < 333)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }
}
