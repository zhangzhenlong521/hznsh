/**************************************************************************
 * $RCSfile: LookAndFeel.java,v $  $Revision: 1.37 $  $Date: 2013/02/28 06:14:41 $
 **************************************************************************/
package cn.com.infostrategy.ui.common;

import java.awt.Color;
import java.awt.Font;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.UIDefaults;
import javax.swing.plaf.metal.MetalLookAndFeel;

import com.sun.java.swing.plaf.windows.WindowsTreeUI;

public class LookAndFeel extends MetalLookAndFeel {

	private static final long serialVersionUID = -1514571451294676756L;
	public static HashMap map_value = new HashMap(); //�洢���е�ֵ
	public static HashMap defaultCacheMap = new HashMap(); //��Ϊ������һ��Ĭ�Ϸ��,��������д����ֵ!!! ��Ϊ�Ǿ�̬����,ֻҪһ�л�������������л���Ĭ�Ϸ��ʱ���Ҳ���ԭ����Ĭ��ֵ��,������Ҫ����������洢!!!
	private static int FONT_REVISE_SIZE = 0;//���ֵ��� 
	//public static Font font = new Font("System", 0, 12); //��������//Arial,Serif
	public static Font font = new Font("Windows 7".equalsIgnoreCase(System.getProperty("os.name")) ? "΢���ź�" : "������", Font.PLAIN, 12);
	public static Font font_b = new Font("������", Font.BOLD, 12);
	public static Font font_i = new Font("������", Font.ITALIC, 12); //б��!!
	public static Font font_big = new Font("������", Font.PLAIN, 12); //������,�����ͻ�ϲ�������!!

	public static Font font2 = new Font("������", 0, 12);
	public static java.awt.Color systemLabelFontcolor = new Color(66, 66, 66); //�����ֵ���ɫ,��ǰ���Ǵ���ɫ,ʹϵͳ�Ե���Ӳ,�����������ɫ,�ͻ�ʹϵͳ�Ե�һ������,��Flash��Ч��
	//	����ɫ
	//	������屳��ɫ
	public static java.awt.Color systembgcolor = new Color(231, 236, 247); //232, 236, 244����Ҫ��һ����ɫ,�����������ı�����ɫ.246, 249, 251
	public static java.awt.Color defaultShadeColor1 = new Color(192, 206, 233); //��224,236,255��/��192,206,233������Ҫ��һ����ɫ,�����������ı�����ɫ.119, 186, 219

	public static java.awt.Color compBorderLineColor = new Color(108, 134, 157); //���ֿؼ��߿����ɫ,ʹ����������ɫ!!

	public static java.awt.Color desktop_Background = new Color(192, 206, 233); //��ҳ���汳����ɫ!!
	public static java.awt.Color desktop_Foreground = new Color(2, 68, 152); //��ҳ����ǰ����ɫ!!�������ֵ���ɫ!!
	public static java.awt.Font desktop_GroupTitleFont = new Font("������", Font.PLAIN, 12); //����ÿ��������������!!
	public static java.awt.Font desktop_OutLookBarFont = new Font("������", Font.PLAIN, 12); //������߳��빤��������!!
	public static java.awt.Color desktop_BtnBarBg = new Color(19, 61, 170); //��ť���������ҳ��ť���ı�����ɫ!!
	public static java.awt.Color desktop_OutLookBarBg = new Color(192, 206, 233); //������߳��빤�����ı�����ɫ�����������!!

	//���� ��ҳǩ --ϵͳ��ҳǩ ����ɫ ϵͳ��ҳǩ ��ǰѡ��ҳǩ����ɫ  ϵͳ��ҳǩ ��ǰδѡ��ҳǩ����ɫ
	public static java.awt.Color systabbedPanelSelectedBackground = new java.awt.Color(200, 221, 242); //
	public static java.awt.Color systabbedPanelNotSelectedBackground = new java.awt.Color(235, 235, 235); //
	//
	//	//--�Զ����ҳǩ ����ɫ
	//	�Զ����ҳǩ ��ǰѡ��ҳǩ����ɫ
	//	�Զ����ҳǩ ��ǰδѡ��ҳǩ����ɫ
	public static java.awt.Color apptabbedPanelSelectedBackground = defaultShadeColor1; //��ҳǩѡ�е���ɫ
	public static java.awt.Color apptabbedPanelNotSelectedBackground = new java.awt.Color(217, 230, 238); //��ҳǩû��ѡ�е���ɫ
	//
	//	�ָ���
	//	�ָ�������ɫ
	public static java.awt.Color splitDividerColor = new Color(239, 242, 242); //�ָ�������ɫ
	//
	//	������
	//	�����鱳��ɫ
	//	�����۱���ɫ
	public static java.awt.Color scrollBarTarckColor = new Color(210, 210, 210); //�������ĵ�ɫ
	public static java.awt.Color scrollBarBorderColor = new Color(240, 240, 240); //new Color(232, 232, 232);  //������߿����ɫ
	public static java.awt.Color scrollBarBackground = new Color(240, 240, 240); //�����۵�ɫ
	//
	//	���
	//	��
	//	ѡ��·���ϸ��ڵ�ǰ��ɫ
	//	δѡ�нڵ�ǰ��ɫ
	//	��ǰѡ�нڵ�߿���ɫ
	//	��ǰѡ�нڵ㱳��ɫ
	public static java.awt.Color treebgcolor = defaultShadeColor1;
	public static java.awt.Color appLabelHighlightFontcolor = new Color(254, 115, 17); //�ۻ�ɫ
	public static java.awt.Color appLabelSelectedFontcolor = Color.RED; //for selected treenode path
	public static java.awt.Color appLabelNotSelectedFontcolor = systemLabelFontcolor;//for not selected treenode path

	//
	//	�б�
	//	����ͷ����ɫ
	//	�����������ɫ
	//	��Ԫ������ǰ��ɫ
	//	��ǰѡ���еı���ɫ
	//	�����html���֣������ӣ���ɫ
	public static java.awt.Color table_headFontColor = new Color(125, 125, 125); //��ͷ������ɫ
	public static java.awt.Color table_toolBarBgcolor = new Color(169, 169, 169); //��������ɫ!!
	public static java.awt.Color tableHeadLineClolr = new Color(169, 169, 169); //����ͷ���ߵ���ɫ
	public static java.awt.Color tableheaderbgcolor = new Color(229, 231, 237); //��ͷ������ɫ192, 206, 233
	public static java.awt.Color tablebgcolor = new Color(234, 240, 248); //������
	public static java.awt.Color table_bgcolor_odd = Color.WHITE; //245, 245, 245
	public static java.awt.Color tablerowselectbgcolor = new Color(184, 207, 229); // ���ѡ���б���ɫ

	public static java.awt.Color htmlrefcolor = Color.BLUE; // ����еĳ�������ɫ
	//
	//	��Ƭ
	//	��Ƭ����ʱ���ָ�������ɫ
	//	��Ƭ�ռ�˵�����ֱ���ɫ
	//	��Ƭ�ؼ��༭״̬��Y/N������ɫ
	public static java.awt.Color cardbgcolor = defaultShadeColor1; //��Ƭ������ɫ
	public static java.awt.Color inputlabelcolor = new Color(255, 0, 0); //�����ѡ�еı���
	public static java.awt.Color inputbgcolor_enable = new Color(255, 255, 255); //�����ѡ�еı���
	public static java.awt.Color inputforecolor_enable = new Color(0, 0, 0); //������ֵ���ɫ
	public static java.awt.Color inputbgcolor_disable = new Color(238, 238, 238); //�������Чʱ�ı���ɫ(Ĭ����ǳ��ɫ)
	public static java.awt.Color inputforecolor_disable = new Color(50, 50, 50); //�������Чʱ��ǰ��ɫ(Ĭ�������ɫ)
	public static java.awt.Color groupTitlecolor = new Color(238, 221, 255); //���������ɫ

	//	��ť����ɫ
	public static java.awt.Color btnbgcolor = new java.awt.Color(192, 211, 187); //��ť������ɫ,ʹ����ҵ�д�Ҷ����ò������ɫ!

	public static java.awt.Color billlisttoolpanelbgcolor = defaultShadeColor1; //�б�����
	public static java.awt.Color billlistquickquerypanelbgcolor = defaultShadeColor1; //��ѯ��

	public static Icon treeExpandedIcon = WindowsTreeUI.ExpandedIcon.createExpandedIcon();
	public static Icon treeCollapsedIcon = WindowsTreeUI.CollapsedIcon.createCollapsedIcon();

	public LookAndFeel() {
	}

	/**
	 * ����ϵͳ����,Ҳ���Ƕ�ϵͳ������۱�����ֵ,����ֵ����Դ�����ݿ���ȡ����������������Ĺ�ϣ���е�!!!
	 */
	public void setLookAndFeelValues(boolean _idForceDefault) {
		FONT_REVISE_SIZE = ClientEnvironment.getCurrLoginUserVO().getFontrevise();
		font = getFont("sys_font", font, _idForceDefault); //
		font_big = getFont("sys_font_big", font_big, _idForceDefault); //
		font_b = getFont("sys_font_b", font_b, _idForceDefault); //
		font2 = getFont("table_font2", font2, _idForceDefault); //

		systemLabelFontcolor = getColor("sys_Fontcolor", systemLabelFontcolor, _idForceDefault); //

		desktop_Background = getColor("desktop_Background", desktop_Background, _idForceDefault); //������ɫ
		desktop_Foreground = getColor("desktop_Foreground", desktop_Foreground, _idForceDefault); //����ǰ����ɫ!,�������������ֵ���ɫ!
		desktop_GroupTitleFont = getFont("desktop_GroupTitleFont", desktop_GroupTitleFont, _idForceDefault); //
		desktop_OutLookBarFont = getFont("desktop_OutLookBarFont", desktop_OutLookBarFont, _idForceDefault); //
		desktop_BtnBarBg = getColor("desktop_BtnBarBg", desktop_BtnBarBg, _idForceDefault); //
		desktop_OutLookBarBg = getColor("desktop_OutLookBarBg", desktop_OutLookBarBg, _idForceDefault); //
		//background
		systembgcolor = getColor("sys_bgcolor", systembgcolor, _idForceDefault); //
		defaultShadeColor1 = getColor("defaultShadeColor1", defaultShadeColor1, _idForceDefault); //

		//tabbed panel
		systabbedPanelSelectedBackground = getColor("tabbedPanel_sys_SelectedBg", systabbedPanelSelectedBackground, _idForceDefault); //
		systabbedPanelNotSelectedBackground = getColor("tabbedPanel_sys_NotSelectedBg", systabbedPanelNotSelectedBackground, _idForceDefault); //

		apptabbedPanelSelectedBackground = getColor("tabbedPanel_app_SelectedBg", apptabbedPanelSelectedBackground, _idForceDefault); //
		apptabbedPanelNotSelectedBackground = getColor("tabbedPanel_app_NotSelectedBg", apptabbedPanelNotSelectedBackground, _idForceDefault); //

		//split
		splitDividerColor = getColor("split_DividerColor", splitDividerColor, _idForceDefault); //

		//scrollBar
		scrollBarTarckColor = getColor("scrollBar_TarckColor", scrollBarTarckColor, _idForceDefault); //
		scrollBarBorderColor = getColor("scrollBar_BorderColor", scrollBarBorderColor, _idForceDefault); //
		scrollBarBackground = getColor("scrollBar_Background", scrollBarBackground, _idForceDefault); //

		//tree
		treebgcolor = getColor("tree_bgcolor", treebgcolor, _idForceDefault); //
		appLabelHighlightFontcolor = getColor("tree_PathHighlightFontcolor", appLabelHighlightFontcolor, _idForceDefault); //
		appLabelSelectedFontcolor = getColor("tree_SelectedFontcolor", appLabelSelectedFontcolor, _idForceDefault); //
		appLabelNotSelectedFontcolor = getColor("tree_NotSelectedFontcolor", appLabelNotSelectedFontcolor, _idForceDefault); //

		//table
		table_headFontColor = getColor("table_headFontColor", table_headFontColor, _idForceDefault);
		tableHeadLineClolr = getColor("table_HeadLineColor", tableHeadLineClolr, _idForceDefault); //
		tableheaderbgcolor = getColor("table_headerbgcolor", tableheaderbgcolor, _idForceDefault); //
		tablebgcolor = getColor("table_bgcolor", tablebgcolor, _idForceDefault); //
		table_toolBarBgcolor = getColor("table_toolBarBgcolor", table_toolBarBgcolor, _idForceDefault); //

		table_bgcolor_odd = getColor("table_bgcolor_odd", table_bgcolor_odd, _idForceDefault); //
		tablerowselectbgcolor = getColor("table_rowselectbgcolor", tablerowselectbgcolor, _idForceDefault); //
		htmlrefcolor = getColor("table_htmlrefcolor", htmlrefcolor, _idForceDefault); //

		billlisttoolpanelbgcolor = getColor("sys_toolpanelbgcolor", billlisttoolpanelbgcolor, _idForceDefault); //
		billlistquickquerypanelbgcolor = getColor("sys_query_panelbgcolor", billlistquickquerypanelbgcolor, _idForceDefault); //

		//card
		cardbgcolor = getColor("cardbgcolor", cardbgcolor, _idForceDefault); //
		inputlabelcolor = getColor("card_inputlabelcolor", inputlabelcolor, _idForceDefault); //
		inputbgcolor_enable = getColor("card_inputbgcolor_enable", inputbgcolor_enable, _idForceDefault); //��Чʱ�ı���ɫ
		inputforecolor_enable = getColor("card_inputforecolor_enable", inputforecolor_enable, _idForceDefault); //��Чʱ��ǰ��ɫ
		inputbgcolor_disable = getColor("card_inputbgcolor_disable", inputbgcolor_disable, _idForceDefault); //��Чʱ�ı���ɫ
		inputforecolor_disable = getColor("card_inputforecolor_disable", inputforecolor_disable, _idForceDefault); //��Чʱ��ǰ��ɫ
		groupTitlecolor = getColor("groupTitlecolor", groupTitlecolor, _idForceDefault); //���������ɫ
		//button
		btnbgcolor = getColor("btn_bgcolor", btnbgcolor, _idForceDefault); //
		compBorderLineColor = getColor("sys_compBorderLineColor", compBorderLineColor, _idForceDefault); //
	}

	/**
	 * ���е�ֵ
	 * @return
	 */
	private Object[] getWLTObjects() {
		Object aobj[] = { // 
		"Label.font", font, // 
				"Label.foreground", systemLabelFontcolor,
				//"Label.background", systembgcolor, //
				//"Label.disabledForeground", Color.BLACK, //

				"Panel.background", systembgcolor, //���ĵ�ɫ!!!����Ҫ!!

				"MenuBar.font", font, //
				"MenuBar.background", systembgcolor, //
				"Menu.font", font, // 
				"MenuItem.font", font, //
				"RadioButtonMenuItem.font", font, // 

				"ToolBar.font", font, //
				//"ToolBar.background", Color.RED, //

				//"TabbedPane.tabsOpaque", Boolean.TRUE, //
				//"TabbedPane.contentOpaque", Boolean.FALSE, //
				"TabbedPane.font", font, //
				//"TabbedPane.background", Color.RED,
				//"TabbedPane.foreground", Color.RED,
				//"TabbedPane.highlight", Color.RED,
				//"TabbedPane.light", Color.RED,
				//"TabbedPane.shadow", Color.RED,
				//              "TabbedPane.darkShadow", Color.RED,
				"TabbedPane.selected", Color.BLUE, //
				"TabbedPane.focus", apptabbedPanelSelectedBackground, //ҳǩ�ĵ�ɫ
				"TabbedPane.tabsOpaque", Boolean.TRUE, //ҳǩ�Ƿ�͸��... 
				"TabbedPane.contentOpaque", Boolean.TRUE, //���ݿ��Ƿ�͸��,������False,�����ֱ߿�����ɫ�����,����Ȼ����һ�����ۼ�.
				//"TabbedPane.tabAreaBackground", Color.RED, // 

				//"SplitPane.dividerSize", new Integer(5), //
				//"SplitPane.background", Color.RED, //
				//          "SplitPane.background", systembgcolor, //
				//"SplitPane.dividerFocusColor",  Color.RED, //
				//          "SplitPane.highlight", systembgcolor, //
				//          "SplitPane.shadow", systembgcolor, //
				//          "SplitPane.darkShadow", systembgcolor,  //
				"SplitPaneDivider.background", Color.BLUE, //
				//"SplitPaneDivider.border", BorderFactory.createLineBorder(billlistquickquerypanelbgcolor, 1), //
				//"SplitPaneDivider.border", BorderFactory.createMatteBorder(0, 1, 0, 1, Color.RED), //
				"SplitPaneDivider.border", BorderFactory.createMatteBorder(2, 2, 2, 2, splitDividerColor), //
				"ScrollPane.background", systembgcolor, //

				"Viewport.background", systembgcolor, //

				"OptionPane.questionDialog.titlePane.background", systembgcolor, //

				"Tree.font", font, //
				"Tree.background", treebgcolor, //
				"Tree.textBackground", treebgcolor, //
				"Tree.foreground", systemLabelFontcolor, //
				"Tree.textForeground", systemLabelFontcolor, "Tree.selectionForeground", appLabelSelectedFontcolor, "Tree.notSelectionForeground", appLabelNotSelectedFontcolor,
				//"Tree.openIcon", WindowsTreeUI.ExpandedIcon.createExpandedIcon(),  //
				//"Tree.closedIcon", WindowsTreeUI.CollapsedIcon.createCollapsedIcon(),  //
				"Tree.expandedIcon", treeExpandedIcon, //
				"Tree.collapsedIcon", treeCollapsedIcon, //

				"List.font", font, //
				"List.background", systembgcolor, //
				"TableHeader.background", systembgcolor, //����б���

				"TextField.font", font,// 
				"TextField.foreground", systemLabelFontcolor, //
				//"TextField.background", inputbgcolor_enable, //
				"TextField.inactiveForeground", inputforecolor_disable, //

				"Button.font", font, //
				"Button.foreground", systemLabelFontcolor, //
				//"Button.background", btnbgcolor, //
				"ToggleButton.font", font, // 
				//"ToggleButton.background", btnbgcolor, //

				"RadioButton.font", font, //
				"RadioButton.background", systembgcolor, //

				//				"CheckBox.font", font, //
				//              "CheckBox.background", systembgcolor, //
				"CheckBox.font", font, //
				"CheckBox.background", systembgcolor, //
				"CheckBoxMenuItem.background", systembgcolor, //
				"CheckBoxMenuItem.foreground", systembgcolor, //
				"CheckBoxMenuItem.selectionForeground", systembgcolor, //
				"CheckBoxMenuItem.selectionBackground", systembgcolor, //

				"ComboBox.font", font, //
				"ComboBox.background", systembgcolor, //�����򱳾���ɫ
				"ComboBox.disabledForeground", new Color(255, 0, 0), // 

				"ToolTip.font", font, // 
				"ToolTip.background", systembgcolor, //

				"ScrollBar.width", new Integer(13), //

				"ScrollBar.background", scrollBarBackground, //systembgcolor, //
				"ScrollBar.foreground", scrollBarBorderColor, //

				"ScrollBar.thumb", scrollBarTarckColor, //
				"ScrollBar.thumbHighlight", scrollBarTarckColor, //
				"ScrollBar.thumbShadow", scrollBarTarckColor, //
				"ScrollBar.thumbDarkShadow", scrollBarTarckColor, //

				"ScrollBar.track", scrollBarBorderColor, //
				"ScrollBar.trackHighlight", scrollBarBorderColor, //
				"ScrollBar.shadow", scrollBarBorderColor, //
				"ScrollBar.highlight", scrollBarBorderColor, //
				"ScrollBar.darkShadow", scrollBarBorderColor, //

				"ScrollBar.gradient", getScrollPanelGradient(), //
		};
		return aobj;
	}

	public static ArrayList getScrollPanelGradient() {
		ArrayList arraylist = new ArrayList();
		arraylist.add(new BigDecimal(1));
		arraylist.add(new BigDecimal(2));
		arraylist.add(LookAndFeel.scrollBarTarckColor);
		arraylist.add(LookAndFeel.scrollBarTarckColor);
		arraylist.add(LookAndFeel.scrollBarTarckColor);
		return arraylist; //
	}

	private Color getColor(String _key, Color _nvlColor, boolean _idForceDefault) {
		if (!defaultCacheMap.containsKey(_key)) {
			defaultCacheMap.put(_key, _nvlColor); //��¼����!!!
		}
		if (_idForceDefault) {
			Color oldCacheColor = (Color) defaultCacheMap.get(_key); //
			if (oldCacheColor != null) {
				return oldCacheColor;
			} else {
				return _nvlColor;
			}
		}
		String str_value = (String) this.map_value.get(_key);
		if (str_value == null || str_value.trim().equals("")) {
			System.err.println("��ɫ[" + _key + "]û�ж���");
			return _nvlColor; //
		}

		try {
			String[] str_rgb = split(str_value, ","); //
			return new Color(Integer.parseInt(str_rgb[0].trim()), Integer.parseInt(str_rgb[1].trim()), Integer.parseInt(str_rgb[2].trim()));
		} catch (Exception e) {
			e.printStackTrace(); //
			return _nvlColor;
		}
	}

	private Font getFont(String _key, Font _nvlFont, boolean _isForceDefault) {
		//ͳһ���Ŵ�
		if (!defaultCacheMap.containsKey(_key)) { //��һ�ο϶���Ĭ��ֵ,��ֱ�Ӵ洢����!!
			defaultCacheMap.put(_key, _nvlFont); //��¼����!!!
		}
		if (_isForceDefault) { //�ڶ��θ���ΪĬ�Ϸ��ʱ,ֻ�ܴӻ�����ȡ,������Ϊ�����Ѿ��ı�,����Զȡ����!!!
			Font oldCacheFont = (Font) defaultCacheMap.get(_key); //
			if (oldCacheFont != null) {
				if (FONT_REVISE_SIZE != 0) {
					oldCacheFont = oldCacheFont.deriveFont(oldCacheFont.getStyle(), oldCacheFont.getSize() + FONT_REVISE_SIZE);
				}
				return oldCacheFont;
			} else {
				return _nvlFont;
			}
		}else{
			if (_nvlFont != null && FONT_REVISE_SIZE != 0) {
				_nvlFont = _nvlFont.deriveFont(_nvlFont.getStyle(), _nvlFont.getSize() + FONT_REVISE_SIZE);
			}
		}

		String str_value = (String) this.map_value.get(_key);
		if (str_value == null || str_value.trim().equals("")) {
			System.err.println("����[" + _key + "]û�ж���");
			return _nvlFont; //
		}

		try {
			String[] str_rgb = split(str_value, ","); //
			String str_realname = str_rgb[0].trim(); //gaofeng
			String str_osversion = System.getProperty("os.name");
			if (str_osversion.equals("Windows 7")) { //˵����win7ϵͳ
				str_realname = "΢���ź�";
			}
			return new Font(str_realname, Integer.parseInt(str_rgb[1].trim()), Integer.parseInt(str_rgb[2].trim()) + FONT_REVISE_SIZE);
		} catch (Exception e) {
			e.printStackTrace(); //
			return _nvlFont;
		}
	}

	/**
	 * ȡ��һ������ֵ!!
	 * @param _key
	 * @param _nvl
	 * @return
	 */
	public static boolean getBoleanValue(String _key, boolean _nvl) { //
		String str_value = (String) map_value.get(_key); //
		if (str_value == null) {
			return _nvl; //
		} else {
			if (str_value.equalsIgnoreCase("Y") || str_value.equalsIgnoreCase("YES") || str_value.equalsIgnoreCase("true")) {
				return true; //
			} else {
				return false;
			}
		}
	}

	/**
	 * ȡ��һ���ַ��͵Ĳ���ֵ!!
	 * @param _key
	 * @param _nvl
	 * @return
	 */
	public static String getStringValue(String _key, String _nvl) { //
		String str_value = (String) map_value.get(_key); //
		if (str_value == null) {
			return _nvl; //
		} else {
			return str_value; //
		}
	}

	private String[] split(String _par, String _separator) {
		Vector<String> v_return = new Vector<String>();
		StringTokenizer st = new StringTokenizer(_par, _separator);
		while (st.hasMoreTokens()) {
			v_return.add(st.nextToken());
		}
		return (String[]) v_return.toArray(new String[0]); //
	}

	public String getDescription() {
		return "The WebPush Java(tm) Look and Feel";
	}

	public String getID() {
		return "WebPush";
	}

	public String getName() {
		return "WebPush";
	}

	protected void initComponentDefaults(UIDefaults _uidefaults) {
		super.initComponentDefaults(_uidefaults);
		_uidefaults.putDefaults(getWLTObjects());
	}

	protected void initSystemColorDefaults(UIDefaults _uidefaults) {
		super.initSystemColorDefaults(_uidefaults); //
	}

	protected void initClassDefaults(UIDefaults table) {
		super.initClassDefaults(table);
		//		String motifPackageName = "com.sun.java.swing.plaf.motif.";
		//		String windowsPackageName = "com.sun.java.swing.plaf.windows."; //
		//		Object[] uiDefaults = { "TreeUI", "cn.com.infostrategy.ui.common.WLTTreeUI", //
		//		}; //
		//
		//		//Object[] uiDefaults = { "SplitPaneUI", windowsPackageName + "WindowsSplitPaneUI" }; //
		//		table.putDefaults(uiDefaults);
	}

	public static int getFONT_REVISE_SIZE() {
		return FONT_REVISE_SIZE;
	}

	public static void setFONT_REVISE_SIZE(int fONTREVISESIZE) {
		FONT_REVISE_SIZE = fONTREVISESIZE;
	}

}
