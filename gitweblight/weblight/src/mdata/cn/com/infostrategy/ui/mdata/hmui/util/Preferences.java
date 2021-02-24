/*
 * @(#)Preferences.java  1.0.1  2006-02-19
 *
 * Copyright (c) 2005 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package cn.com.infostrategy.ui.mdata.hmui.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Utility class for accessing Mac OS X System Preferences.
 *
 * @author  Werner Randelshofer
 * @version 2006-02-19 Catch Throwable instead of IOException in method
 * load global Preferences.
 * <br>1.0 September 17, 2005 Created.
 */
public class Preferences {
    private static HashMap prefs;
    
    /**
     * Creates a new instance.
     */
    public Preferences() {
    }
    
    public static String getString(String key) {
        return (String) get(key);
    }
    
    public static String getString(String key, String defaultValue) {
        return prefs.containsKey(key) ? (String) get(key) : defaultValue;
    }
    
    public static Object get(String key) {
        if (prefs == null) {
            prefs = new HashMap();
            loadGlobalPreferences();
        }
        //System.out.println("Preferences.get("+key+"):"+prefs.get(key));
        return prefs.get(key);
    }
    
    
    private static void loadGlobalPreferences() {
        // Load Mac OS X global preferences
        // --------------------------------
        
        // Fill preferences with default values, in case we fail to read them
        
        // Appearance: "1"=Blue, "6"=Graphite
        prefs.put("AppleAquaColorVariant","1");
        // Highlight Color: (RGB float values)
        prefs.put("AppleHighlightColor","0.709800 0.835300 1.000000");
        // Collation order: (Language code)
        prefs.put("AppleCollationOrder","en");
        // Place scroll arrows: "Single"=At top and bottom, "DoubleMax"=Together
        prefs.put("AppleScrollBarVariant","DoubleMax");
        // Click in the scroll bar to: "true"=Jump to here, "false"=Jump to next page
        prefs.put("AppleScrollerPagingBehavior","false");
    }
    
    
    /**
     * Reads the specified PList file and returns it as an XMLElement.
     * This method can deal with XML encoded and binary encoded PList files.
     */
    private static XMLElement readPList(File plistFile) throws IOException {
        FileReader reader = null;
        XMLElement xml = null;
        try {
            reader =  new FileReader(plistFile);
            xml = new XMLElement(new HashMap(), false, false);
            try {
                xml.parseFromReader(reader);
            } catch (XMLParseException e) {
                xml = new BinaryPListParser().parse(plistFile);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return xml;
    }
}

