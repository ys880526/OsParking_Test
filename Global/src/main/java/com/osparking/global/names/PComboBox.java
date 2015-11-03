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

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.ComboPopup;

/**
 *
 * @author Park, Jongbum <Park, Jongbum at Open Source Parking Inc.>
 */
public class PComboBox<ItemType> extends JComboBox<ItemType> {
    private ListSelectionListener listener;
    private ItemType highlightedCbxItem;

    public PComboBox() {
        uninstall();
        install();
    }

    @Override
    public void updateUI() {
        uninstall();
        super.updateUI();
        install();
    }

    private void uninstall() {
        if (listener == null) return;
        getPopupList().removeListSelectionListener(listener);
        listener = null;
    }

    protected void install() {
        listener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;

                JList list = getPopupList();
                highlightedCbxItem = (ItemType) list.getSelectedValue();
                //System.out.println("--> " + String.valueOf(list.getSelectedValue()));
            }
        };
        getPopupList().addListSelectionListener(listener);
    }

    private JList getPopupList() {
        ComboPopup popup = (ComboPopup) getUI().getAccessibleChild(this, 0);
        return popup.getList();

    }

    /**
     * @return the highlightedCbxItem
     */
    public ItemType getHighlightedCbxItem() {
        return highlightedCbxItem;
    }
}

