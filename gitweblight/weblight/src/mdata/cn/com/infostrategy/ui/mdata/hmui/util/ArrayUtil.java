/*
 * @(#)ArrayUtil.java  1.3  2004-09-12
 *
 * Copyright (c) 2003 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

//package ch.randelshofer.util;
package cn.com.infostrategy.ui.mdata.hmui.util;
import java.util.ArrayList;
/**
 * ArrayUtil.
 *
 * @author  Werner Randelshofer, Staldenmattweg 2, Immensee, CH-6405, Switzerland
 * @version 1.3 2003-09-12 Removed unused methods.
 * <br>1.2 2003-09-27 Method toString added.
 * <br>1.1 2003-08-24 Methods asVector and toHexString added.
 * <br>1.0 2003-07-18 Created.
 */
public class ArrayUtil {
    
    /** Prevent instance creation. */
    private ArrayUtil() {
    }
    
    /*
    public static Vector asVector(int[] a) {
        Vector list = new Vector(a.length);
        for (int i=0; i < a.length; i++) {
            list.addElement(new Integer(a[i]));
        }
        return list;
    }
    public static List asList(Object[] a) {
        return Arrays.asList(a);
    }*/
    public static ArrayList asList(int[] a) {
        ArrayList list = new ArrayList(a.length);
        for (int i=0; i < a.length; i++) {
            list.add(new Integer(a[i]));
        }
        return list;
    }/*
    public static ArrayList asList(int[] a, int off, int len) {
        ArrayList list = new ArrayList(a.length);
        int end = off + len;
        for (; off < end; off++) {
            list.add(new Integer(a[off]));
        }
        return list;
    }
    */
    private final static char[] hexChars = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };
    
    public static String toHexString(byte[] b, int off, int len) {
        StringBuffer buf = new StringBuffer();
        int end = off + len;
        for (; off < end; off++) {
            buf.append(hexChars[(b[off] & 0xf0) >>> 4]);
            buf.append(hexChars[b[off] & 0x0f]);
        }
        return buf.toString();
    }
    
    public static String toHexString(byte[] b) {
        return toHexString(b, 0, b.length);
    }

    public static int[] truncate(int[] a, int off, int len) {
        int[] b = new int[len];
        System.arraycopy(a, off, b, 0, len);
        return b;
    }
}
