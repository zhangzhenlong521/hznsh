/*
 * @(#)NavigatableTabbedPaneUI.java  1.0  September 4, 2006
 *
 * Copyright (c) 2006 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package cn.com.infostrategy.ui.mdata.hmui.util;

/**
 * NavigatableTabbedPaneUI.
 *
 * @author Werner Randelshofer
 * @version 1.0 September 4, 2006 Created.
 */
public interface NavigatableTabbedPaneUI {
    /** Tab Navigation methods. */
    public void navigateSelectedTab(int direction);
    public boolean requestFocusForVisibleComponent();
    public Integer getIndexForMnemonic(int mnemonic);
}
