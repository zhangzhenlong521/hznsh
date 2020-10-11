/*
 * IteratorEnumeration.java
 *
 * Created on March 9, 2004, 5:41 PM
 */

package cn.com.infostrategy.ui.mdata.hmui.util;

import java.util.Enumeration;
import java.util.Iterator;
/**
 * This Enumeration is a wrapper over an Iterator.
 * @author  werni
 */
public class IteratorEnumeration implements Enumeration {
    private Iterator iterator;
    
    /** Creates a new instance. */
    public IteratorEnumeration(Iterator iterator) {
        this.iterator = iterator;
    }
    
    public boolean hasMoreElements() {
        return iterator.hasNext();
    }
    
    public Object nextElement() {
        return iterator.next();
    }
    
}
