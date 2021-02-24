package cn.com.infostrategy.ui.mdata.hmui;

import java.awt.Font;

import javax.swing.UIDefaults;

import cn.com.infostrategy.ui.common.LookAndFeel;

/** 
 * Copyright Pushine
 * @ClassName: cn.com.infostrategy.ui.mdata.hmui.I_LookAndFeel 
 * @Description: 事业系统中自定义皮肤。
 * @author haoming
 * @date Mar 19, 2013 4:14:34 PM
 *  
*/
public class I_LookAndFeel extends LookAndFeel {

	private static final long serialVersionUID = 1L;
	//测试中发现宋体在以下平台抗锯齿。
	public static boolean windows_word_antialias = (System.getProperty("os.name").contains("Windows") && !"Windows 2003".equalsIgnoreCase(System.getProperty("os.name")));
	public static Font font_word_yh = new Font("Windows 7".equalsIgnoreCase(System.getProperty("os.name")) || "Windows xp".equalsIgnoreCase(System.getProperty("os.name")) ? "微软雅黑" : "新宋体", Font.PLAIN, 12);

	public I_LookAndFeel() {
		super.map_value.put("分页按钮是否是图片", "true");
		super.map_value.put("列表分页条是否在下面", "true");
	}

	protected void initClassDefaults(UIDefaults table) {
		super.initClassDefaults(table);
		new I_ScrollBarUIImpl().initClassDefaults(table);
		initOther(table);
	}

	protected void initComponentDefaults(UIDefaults _uidefaults) {
		super.initComponentDefaults(_uidefaults);
	}

	//有一些配置比较简单皮肤配置。放到该方法中。
	void initOther(UIDefaults table) {
		String commonDir = "/cn/com/weblight/images/scrollbar/";
		Object obj[] = { "ComboBoxUI", "cn.com.infostrategy.ui.mdata.hmui.I_ComboBoxUI", 
				"TextFieldUI", "cn.com.infostrategy.ui.mdata.hmui.I_TextFieldUI", 
				"TextAreaUI", "cn.com.infostrategy.ui.mdata.hmui.I_TextAreaUI", 
				"PasswordFieldUI", "cn.com.infostrategy.ui.mdata.hmui.I_PasswordFieldUI",
				"FormattedTextFieldUI", "cn.com.infostrategy.ui.mdata.hmui.I_TextFieldUI",
				"ButtonUI", "cn.com.infostrategy.ui.mdata.hmui.I_ButtonUI", 
				"MenuItemUI", "cn.com.infostrategy.ui.mdata.hmui.I_MenuItemUI", 
				"MenuUI", "cn.com.infostrategy.ui.mdata.hmui.I_MenuUI"

		};
		table.putDefaults(obj);
	}

	public String getDescription() {
		return "The WebPush Java(tm) Look and Feel";
	}

	public String getID() {
		return "WebPushUIByHm";
	}

	public String getName() {
		return "WebPushUIByHm";
	}
}
