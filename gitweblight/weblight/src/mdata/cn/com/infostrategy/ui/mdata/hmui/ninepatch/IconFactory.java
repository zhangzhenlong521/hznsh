package cn.com.infostrategy.ui.mdata.hmui.ninepatch;

import java.io.IOException;
import java.io.InputStream;


/** 
 * Copyright Pushine
 * @ClassName: cn.com.infostrategy.ui.mdata.hmui.ninepatch.IconFactory 
 * @Description: 用NinePatch技术的图片生成类。
 * @author haoming
 * @date Mar 19, 2013 4:32:39 PM
 *  
*/
public class IconFactory extends RawCache<NinePatch> {
	public static IconFactory instance;
	public final static String imagePath = "/cn/com/weblight/images/pushineui/";
	public static IconFactory getInstance() {
		if (instance == null)
			instance = new IconFactory();
		return instance;
	}

	protected NinePatch getResource(String relativePath, Class baseClass) {
		return NinePatchHelper.createNinePatch(baseClass.getResource(relativePath), false);
	}

	public static NinePatch getResource(InputStream stream, boolean is9Patch, boolean convert) {
		try {
			return NinePatchHelper.createNinePatch(stream, is9Patch, convert);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public NinePatch getRaw(String relativePath) {
		return (NinePatch) getRaw(relativePath, getClass());
	}

	public NinePatch getPopupBg() {
		return getRaw(imagePath+"shadow_bg_popup.9.png");
	}

	public NinePatch getTooltipBg() {
		return getRaw(imagePath+"shadow_bg_tooltip2.9.png");
	}

	public NinePatch getComboxItemBg() {
		return getRaw(imagePath+"menu_bg.9.png");
	}

	public NinePatch getTextFieldBgNormal() {
		return getRaw(imagePath+"bg_login_text_normal.9.png");
	}

	public NinePatch getTextFieldBgFocused() {
		return getRaw(imagePath+"bg_login_text_pressed.9.png");
	}

	public NinePatch getTextFieldBgDisabled() {
		return getRaw(imagePath+"bg_login_text_disable.9.png");
	}

	public NinePatch getButtonArrow_normal() {
		return getRaw(imagePath+"button_arrow.9.png");
	}

	public NinePatch getButtonIcon_NormalGreen() {
		return getRaw(imagePath+"btn_special_default.9.png");
	}

	public NinePatch getButtonIcon_NormalGray() {
		return getRaw(imagePath+"btn_general_default.9.png");
	}

	public NinePatch getButtonIcon_DisableGray() {
		return getRaw(imagePath+"btn_special_disabled.9.png");
	}

	public NinePatch getButtonIcon_NormalBlue() {
		return getRaw(imagePath+"btn_special_blue.9.png");
	}
    public NinePatch getButtonIcon_PressedOrange()
    {
        return getRaw(imagePath+"btn_general_pressed.9.png");
    }
    public NinePatch getButtonIcon_NormalLightBlue()
    {
        return getRaw(imagePath+"btn_special_lightblue.9.png");
    }
    
    public NinePatch getPanelQueryItem_BG()
    {
        return getRaw(imagePath+"query_item_bg_2.9.png");
    }
}