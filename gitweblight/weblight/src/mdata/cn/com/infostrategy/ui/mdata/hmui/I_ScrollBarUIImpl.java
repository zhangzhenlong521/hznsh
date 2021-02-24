package cn.com.infostrategy.ui.mdata.hmui;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.DimensionUIResource;

import cn.com.infostrategy.to.common.TBUtil;

public class I_ScrollBarUIImpl extends I_UIIfc {
	public void initClassDefaults(UIDefaults table) {
		String packagePath = "cn.com.infostrategy.ui.mdata.hmui.";
		   Object scrollPaneBorder = new UIDefaults.ProxyLazyValue(
	                "ch.randelshofer.quaqua.QuaquaScrollPaneBorder$UIResource",
	                new Object[] { commonDir+"ScrollPane.borders.png", commonDir+"TextField.borders.png" }
	        );
		  Boolean opaque = false;
		Object[] ui = { 
				"ScrollPaneUI","cn.com.infostrategy.ui.mdata.hmui.I_ScrollPanelUI",
				
				    "ScrollBarUI", packagePath + "QuaquaScrollBarUI",
		            "ScrollBar.smallMinimumThumbSize", new DimensionUIResource(18,18),
		            "ScrollBar.maximumThumbSize", new DimensionUIResource(Integer.MAX_VALUE,Integer.MAX_VALUE),
		            
		            "ScrollBar.hThumbBody", makeBufferedImage(commonDir+"ScrollBar.hThumbBody.png"),
		            "ScrollBar.hThumbLeft", makeIcons(commonDir+"ScrollBar.hThumbLeft.png", 5, false),
		            "ScrollBar.hThumbRight", makeIcons(commonDir+"ScrollBar.hThumbRight.png", 5, false),
		            "ScrollBar.hTrack", makeImageBevelBorder(commonDir+"ScrollBar.tog.hButtons.png", new Insets(15,18,0,18), new Insets(15,18,0,18),true,new Rectangle(0,0,42,15)),
		            "ScrollBar.vTrack", makeImageBevelBorder(commonDir+"ScrollBar.tog.vButtons.png",new Insets(18,15,18,0),new Insets(15,15,18,0),true,new Rectangle(0,0,15,42)),
		            
		            "ScrollBar.ihThumb", makeImageBevelBorder(commonDir+"ScrollBar.ihThumb.png", new Insets(15,11,0,11)),
		            "ScrollBar.sep.hButtons", makeImageBevelBorders(commonDir+"ScrollBar.sep.hButtons.png", new Insets(15,28,0,28), 4, false),
		            "ScrollBar.tog.hButtons", makeImageBevelBorders(commonDir+"ScrollBar.tog.hButtons.png", new Insets(15,18,0,44), 4, false),
		            
		            "ScrollBar.vThumbBody", makeBufferedImage(commonDir+"ScrollBar.vThumbBody.png"),
		            "ScrollBar.vThumbTop", makeIcons(commonDir+"ScrollBar.vThumbTop.png", 5, true),
		            "ScrollBar.vThumbBottom", makeIcons(commonDir+"ScrollBar.vThumbBottom.png", 5, true),
		            "ScrollBar.sep.vButtons", makeImageBevelBorders(commonDir+"ScrollBar.sep.vButtons.png", new Insets(28,15,28,0), 4, true),
		            "ScrollBar.tog.vButtons", makeImageBevelBorders(commonDir+"ScrollBar.tog.vButtons.png", new Insets(18,15,44,0), 4, true),
		            
		            "ScrollBar.small.hThumbBody", makeBufferedImage(commonDir+"ScrollBar.small.hThumbBody.png"),
		            "ScrollBar.small.hThumbLeft", makeIcons(commonDir+"ScrollBar.small.hThumbLeft.png", 5, false),
		            "ScrollBar.small.hThumbRight", makeIcons(commonDir+"ScrollBar.small.hThumbRight.png", 5, false),
		            "ScrollBar.small.hTrack", makeImageBevelBorder(commonDir+"ScrollBar.small.hTrack.png", new Insets(11,0,0,0)),
		            "ScrollBar.small.ihThumb", makeImageBevelBorder(commonDir+"ScrollBar.small.ihThumb.png", new Insets(11,8,0,8)),
		            "ScrollBar.smallSep.hButtons", makeImageBevelBorders(commonDir+"ScrollBar.smallSep.hButtons.png", new Insets(11,21,0,21), 4, false),
		            "ScrollBar.smallTog.hButtons", makeImageBevelBorders(commonDir+"ScrollBar.smallTog.hButtons.png", new Insets(11,14,0,34), 4, false),
		            
		            "ScrollBar.small.vThumbBody", makeBufferedImage(commonDir+"ScrollBar.small.vThumbBody.png"), //滚动条
		            "ScrollBar.small.vThumbTop", makeIcons(commonDir+"ScrollBar.small.vThumbTop.png", 5, true),
		            "ScrollBar.small.vThumbBottom", makeIcons(commonDir+"ScrollBar.small.vThumbBottom.png", 5, true),
		            "ScrollBar.small.vTrack", makeImageBevelBorder(commonDir+"ScrollBar.small.vTrack.png", new Insets(0,11,0,0)),
		            "ScrollBar.small.ivThumb", makeImageBevelBorder(commonDir+"ScrollBar.small.ivThumb.png", new Insets(8,11,8,0)),
		            "ScrollBar.smallSep.vButtons", makeImageBevelBorders(commonDir+"ScrollBar.smallSep.vButtons.png", new Insets(21,11,21,0), 4, true),
		            "ScrollBar.smallTog.vButtons", makeImageBevelBorders(commonDir+"ScrollBar.smallTog.vButtons.png", new Insets(14,11,34,0), 4, true),
		            "ScrollBar.focusable", Boolean.FALSE,
		            "ScrollPane.opaque", opaque,	
		            "ScrollPane.qq", Boolean.valueOf(TBUtil.getTBUtil().getSysOptionBooleanValue("滚动条是否淡化", Boolean.TRUE)), //滚动条模式	
		            "ScrollPane.fouceMin", Boolean.valueOf(TBUtil.getTBUtil().getSysOptionBooleanValue("滚动条是否强制缩小", Boolean.TRUE)), //强制缩小版	
		};
		UIManager.put("ScrollBar.minimumThumbSize", new Dimension(24,24));
		table.putDefaults(ui);
	}
	   
}
