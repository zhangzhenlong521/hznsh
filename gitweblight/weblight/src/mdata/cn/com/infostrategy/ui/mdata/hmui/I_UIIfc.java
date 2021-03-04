package cn.com.infostrategy.ui.mdata.hmui;

import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;

import javax.swing.UIDefaults;


public abstract class I_UIIfc  implements Serializable {
	protected final static String commonDir = "/cn/com/weblight/images/scrollbar/";
	public abstract void initClassDefaults(UIDefaults table);
	 protected Object makeImage(String location) {
	     return new UIDefaults.ProxyLazyValue(
	             "cn.com.infostrategy.ui.mdata.hmui.QuaquaIconFactory","createImage",
	             new Object[] {location}
	     );
	 }
	 protected Object makeBufferedImage(String location) {
	     return new UIDefaults.ProxyLazyValue(
	             "cn.com.infostrategy.ui.mdata.hmui.QuaquaIconFactory","createBufferedImage",
	             new Object[] {location}
	     );
	 }
	 public static Object makeIcon(Class baseClass, String location) {
	     return new UIDefaults.ProxyLazyValue(
	             "cn.com.infostrategy.ui.mdata.hmui.QuaquaIconFactory","createIcon",
	             new Object[] { baseClass, location }
	     );
	 }
	 public static Object makeIcon(Class baseClass, String location, Point shift) {
	     return new UIDefaults.ProxyLazyValue(
	             "cn.com.infostrategy.ui.mdata.hmui.QuaquaIconFactory","createIcon",
	             new Object[] { baseClass, location, shift }
	     );
	 }
	 public static Object makeIcon(Class baseClass, String location, Rectangle shiftAndSize) {
	     return new UIDefaults.ProxyLazyValue(
	             "cn.com.infostrategy.ui.mdata.hmui.QuaquaIconFactory","createIcon",
	             new Object[] { baseClass, location, shiftAndSize }
	     );
	 }
	 protected static Object makeIcons(String location, int states, boolean horizontal) {
	     return new UIDefaults.ProxyLazyValue(
	             "cn.com.infostrategy.ui.mdata.hmui.QuaquaIconFactory","createIcons",
	             new Object[] {location, new Integer(states), new Boolean(horizontal)}
	     );
	 }
	 protected static Object makeButtonStateIcon(String location, int states) {
	     return new UIDefaults.ProxyLazyValue(
	             "cn.com.infostrategy.ui.mdata.hmui.QuaquaIconFactory","createButtonStateIcon",
	             new Object[] {location, new Integer(states)}
	     );
	 }
	 protected static Object makeButtonStateIcon(String location, int states, Point shift) {
	     return new UIDefaults.ProxyLazyValue(
	             "cn.com.infostrategy.ui.mdata.hmui.QuaquaIconFactory","createButtonStateIcon",
	             new Object[] {location, new Integer(states), shift}
	     );
	 }
	 protected static Object makeButtonStateIcon(String location, int states, Rectangle shift) {
	     return new UIDefaults.ProxyLazyValue(
	             "cn.com.infostrategy.ui.mdata.hmui.QuaquaIconFactory","createButtonStateIcon",
	             new Object[] {location, new Integer(states), shift}
	     );
	 }
	 protected static Object makeFrameButtonStateIcon(String location, int states) {
	     return new UIDefaults.ProxyLazyValue(
	             "cn.com.infostrategy.ui.mdata.hmui.QuaquaIconFactory","createFrameButtonStateIcon",
	             new Object[] {location, new Integer(states)}
	     );
	 }
	 protected static Object makeSliderThumbIcon(String location) {
	     return new UIDefaults.ProxyLazyValue(
	             "cn.com.infostrategy.ui.mdata.hmui.QuaquaIconFactory","createSliderThumbIcon",
	             new Object[] { location }
	     );
	 }
	 protected Object makeImageBevelBorder(String location, Insets insets) {
	     return makeImageBevelBorder(location, insets, false);
	 }
	 protected Object makeImageBevelBorder(String location, Insets insets, boolean fill) {
	     return new UIDefaults.ProxyLazyValue(
	             "cn.com.infostrategy.ui.mdata.hmui.QuaquaBorderFactory","create",
	             new Object[] { location, insets, new Boolean(fill) }
	     );
	 }
	 protected Object makeImageBevelBorder(String location, Insets imageInsets, Insets borderInsets, boolean fill) {
	     return new UIDefaults.ProxyLazyValue(
	             "cn.com.infostrategy.ui.mdata.hmui.QuaquaBorderFactory","create",
	             new Object[] { location, imageInsets, borderInsets, new Boolean(fill) }
	     );
	 }
	 //新添加的方法，可以根据一定的矩形把图片截取一把，在拉伸。
	 protected Object makeImageBevelBorder(String location, Insets imageInsets, Insets borderInsets, boolean fill,Rectangle _clip) {
	     return new UIDefaults.ProxyLazyValue(
	             "cn.com.infostrategy.ui.mdata.hmui.QuaquaBorderFactory","create",
	             new Object[] { location, imageInsets, borderInsets, new Boolean(fill),new Boolean(false),_clip }
	     );
	 }
	 protected Object makeImageBevelBackgroundBorder(String location, Insets imageInsets, Insets borderInsets, boolean fill) {
	     return new UIDefaults.ProxyLazyValue(
	             "cn.com.infostrategy.ui.mdata.hmui.QuaquaBorderFactory","createBackgroundBorder",
	             new Object[] { location, imageInsets, borderInsets, new Boolean(fill) }
	     );
	 }
	 protected Object makeImageBevelBorders(String location, Insets insets, int states, boolean horizontal) {
	     return new UIDefaults.ProxyLazyValue(
	             "cn.com.infostrategy.ui.mdata.hmui.QuaquaBorderFactory","create",
	             new Object[] { location, insets, new Integer(states), new Boolean(horizontal) }
	     );
	 }
}
