package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import cn.com.infostrategy.bs.common.XMLIOUtil;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.jepfunctions.JepFormulaParse;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTPopupButton;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractWLTCompentPanel;
import cn.com.infostrategy.ui.mdata.querycomp.BillQueryQuickSQLCreaterIFC;
import cn.com.infostrategy.ui.mdata.querycomp.CommQueryDialog;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_Button;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_CheckBox;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_ComboBox;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_TextField;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;

/**
 * 查询面板,即根据元原模板编码生成一个快速查询面板,通用查询面板,高级查询面板!!
 * 查询面板是平台中非常关键的一个功能，因为查询是系统中到处都需要的一个功能!!
 * 查询面板需要继续进行深入的封装与加大功能,因为查询一直是影响开发功能的一个顽疾!! 必须解决!! 一定要做到各种查询需求的易配置,易扩展!!!
 * 通过调用方法getQuerySQLConditionByItemKeyMapping()可以快速得到查询面板生成的查询条件!! 然后前面加上"select * from 表名 where 1=1" + 生成的条件!! 
 * @author xch
 * 
 */
public class BillQueryPanel extends BillPanel {

	private static final long serialVersionUID = 1L;
	private String str_templetcode = null; // 模板编码
	private Pub_Templet_1VO templetVO = null; // 模板主表对象
	private Pub_Templet_1_ItemVO[] templetItemVOs = null; // 模板子表对象

	private JMenuItem menuItem_commquery, menuItem_complexquery, menuItem_reset, menuItem_exportConditiontoXML; //

	private ArrayList v_compents = new ArrayList();
	private ArrayList v_compentsaves = new ArrayList();
	private HashMap v_compents_map = new HashMap();
	private Vector v_listeners = new Vector(); // 反射注册的事件监听者!!!
	private BillListPanel billlist = null; //

	Logger logger = WLTLogger.getLogger(BillQueryPanel.class);
	private boolean bo_isallowtriggereditevent = true; // 是否允许触发编辑事件

	private BillQueryQuickSQLCreaterIFC quickSQLCreater = null; // //

	private VFlowLayoutPanel commqueryPanel = null;
	private CommQueryDialog commQueryDialog = null; //

	private WLTPopupButton popupButton = null; //
	private WLTButton queryButton = null; //邮储查询去箭头 【杨科/2012-09-10】	
	private WLTButton btn_reset, btn_complexquery = null; // 清空条件
	private boolean complexquery = false;
	private String str_currquicksql = null; // F

	private ActionListener allActionListener = null;
	private ActionListener custActionListener = null; //
	private ActionListener custCommonActionListener = null; //

	private boolean isContainQueryButton = true; //
	private boolean isContainCommonQueryButton = false; // 有些地方不想要通用查询 true为不显示
	private boolean isQuickQueryQuiet = false; // 点击快速查询时是否是安静模式，即没有弹出一个等待框!因为有时客户需要自己自定义方法
	private TBUtil tbUtil = null; //工具类!
	private JPanel queryPanel = null;
	private WLTPanel btnpanel = null;

	private String dataPolicyCondition = null;//袁江晓20120913添加，解决报表的数据权限问题，由于报表用的并不是billlist，所以不能用$SQL_1普通的查询的权限机制 ，需要特殊处理

	/**
	 * 根据模板编码创建查询面板
	 * 
	 * @param _templetcode
	 */
	public BillQueryPanel(String _templetcode) {
		this(_templetcode, true);
	}

	/**
	 * 构造方法
	 * 
	 * @param _templetcode
	 * @param _containQueryButton,是否包含查询按钮,因为有时可能不需要显示查询按钮
	 */
	public BillQueryPanel(String _templetcode, boolean _containQueryButton) {
		this.isContainQueryButton = _containQueryButton; //
		if (_templetcode.indexOf(".") > 0) { // 如果是个类名,即编码中有个".",我们则认为是个类名!!则直接反射调用
			try {
				init_2((AbstractTMO) Class.forName(_templetcode).newInstance());

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			init_1(_templetcode);
		}
	}

	/**
	 * @param _templetcode
	 *            模板编码
	 * @param _containQueryButton
	 *            是否需要查询按钮
	 * @param __containCommonQueryButton
	 *            是否需要通用查询按钮
	 */
	public BillQueryPanel(String _templetcode, boolean _containQueryButton, boolean _containCommonQueryButton) {
		this.isContainQueryButton = _containQueryButton; //
		this.isContainCommonQueryButton = _containCommonQueryButton;
		if (_templetcode.indexOf(".") > 0) { // 如果是个类名,即编码中有个".",我们则认为是个类名!!则直接反射调用
			try {
				init_2((AbstractTMO) Class.forName(_templetcode).newInstance());

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			init_1(_templetcode);
		}
	}

	public BillQueryPanel(Pub_Templet_1VO _TempletVO) {
		this(_TempletVO, true);
	}

	public BillQueryPanel(Pub_Templet_1VO _TempletVO, boolean _containQueryButton) {
		this.str_templetcode = _TempletVO.getTempletcode();
		this.isContainQueryButton = _containQueryButton; //
		templetVO = _TempletVO;
		templetItemVOs = templetVO.getItemVos(); // 各项
		this.isContainCommonQueryButton = templetVO.getIsshowcommquerybtn();
		initialize(); //
	}

	public BillQueryPanel(Pub_Templet_1VO _TempletVO, boolean _containQueryButton, String dataPolicyCondition) { //袁江晓20120913添加报表的构造方法，最后一个参数为报表的数据权限
		this.dataPolicyCondition = dataPolicyCondition;
		this.str_templetcode = _TempletVO.getTempletcode();
		this.isContainQueryButton = _containQueryButton; //
		templetVO = _TempletVO;
		templetItemVOs = templetVO.getItemVos(); // 各项
		this.isContainCommonQueryButton = templetVO.getIsshowcommquerybtn();
		initialize(); //
	}

	public BillQueryPanel(BillListPanel _billList) {
		this.billlist = _billList; //
		templetVO = billlist.getTempletVO();
		templetItemVOs = templetVO.getItemVos(); // 各项
		this.isContainCommonQueryButton = templetVO.getIsshowcommquerybtn();
		initialize(); //
	}

	private void init_1(String _templetcode) {
		try {
			templetVO = UIUtil.getPub_Templet_1VO(_templetcode);
			templetItemVOs = templetVO.getItemVos(); // 各项
			str_templetcode = templetVO.getTempletcode(); //
			this.isContainCommonQueryButton = templetVO.getIsshowcommquerybtn();
			initialize(); //
		} catch (Exception e) {
			e.printStackTrace();
		} //
	}

	private void init_2(AbstractTMO _abstractTempletVO) {
		try {
			templetVO = UIUtil.getPub_Templet_1VO(_abstractTempletVO);
			templetItemVOs = templetVO.getItemVos(); // 各项
			str_templetcode = templetVO.getTempletcode(); //
			this.isContainCommonQueryButton = templetVO.getIsshowcommquerybtn();
			initialize(); //
		} catch (Exception e) {
			e.printStackTrace();
		} //
	}

	/**
	 * 重新加载页面   20130314  袁江晓添加
	 */
	public void reload() {
		reload(this.str_templetcode); //
	}

	/**
	 * 重新加载页面
	 */
	public void reload(String _templetcode) {
		v_compents.clear(); //
		v_compents_map.clear(); //
		v_listeners.clear();
		queryPanel = null;
		btnpanel = null;
		init_1(_templetcode); //
	}

	/**
	 * 初始化页面
	 * 
	 */
	private void initialize() {
		this.setLayout(new BorderLayout()); //
		this.setOpaque(false);
		// this.setBackground(LookAndFeel.defaultShadeColor1);
		// this.setUI(new
		// WLTPanelUI(BackGroundDrawingUtil.VERTICAL_BOTTOM_TO_TOP));
		VFlowLayoutPanel panel_query = getQueryPanel(1); //
		queryPanel = new JPanel(new BorderLayout());
		queryPanel.setOpaque(false);
		queryPanel.add(panel_query);
		this.add(queryPanel, BorderLayout.WEST);

		allActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == btn_reset) {
					resetAllQuickQueryCompent();
				} else if (e.getSource() == menuItem_commquery) {
					onCommonQuery();
				} else if (e.getSource() == menuItem_complexquery) {
					//complexQuery(); //
				} else if (e.getSource() == menuItem_reset) { //清空条件!!
					resetAllQuickQueryCompent();
				} else if (e.getSource() == menuItem_exportConditiontoXML) {
					exportConditionToXML(); // 将条件保存至XML
				} else if (e.getSource() == btn_complexquery) {
					showOrHidenComplexQuery();
				}
			}
		};
		// 加入右方按钮
		if (isContainQueryButton) {
			btnpanel = new WLTPanel();
			// btnpanel.setBackground(LookAndFeel.billlistquickquerypanelbgcolor);
			btnpanel.setLayout(new BorderLayout());

			JPopupMenu popup = new JPopupMenu("PopupMenu");
			menuItem_commquery = new JMenuItem(UIUtil.getLanguage("通用查询")); //
			menuItem_complexquery = new JMenuItem(UIUtil.getLanguage("高级查询")); //就是坚起来一行行搞条件!!!
			menuItem_reset = new JMenuItem(" 重 置 "); //一开始是放在下拉中的,但民生,兴业等客户又说要放出来,放出来后中铁建的肖主任又说要去掉!!只能搞参数配置了!!!
			menuItem_exportConditiontoXML = new JMenuItem(UIUtil.getLanguage("导出条件")); //

			menuItem_commquery.setBackground(LookAndFeel.systembgcolor);
			menuItem_complexquery.setBackground(LookAndFeel.systembgcolor);
			menuItem_reset.setBackground(LookAndFeel.systembgcolor); //
			menuItem_exportConditiontoXML.setBackground(LookAndFeel.systembgcolor);

			Dimension dim_size = new Dimension(78, 19); //
			menuItem_commquery.setPreferredSize(dim_size); //通用查询
			menuItem_complexquery.setPreferredSize(dim_size); //复杂查询
			menuItem_reset.setPreferredSize(dim_size); //清空条件
			menuItem_exportConditiontoXML.setPreferredSize(dim_size); //导出条件
			menuItem_commquery.addActionListener(allActionListener);
			menuItem_reset.addActionListener(allActionListener);
			menuItem_complexquery.addActionListener(allActionListener);
			menuItem_exportConditiontoXML.addActionListener(allActionListener);
			boolean isHaveOneCommQueryItem = false;
			if (isContainCommonQueryButton) {
				for (int i = 0; i < templetItemVOs.length; i++) { //看是否存在通用查询!!在兴业中遇到有的客户反映,没有配置一个通用查询，结果出来一个空白,不好!所以加了一个机制,如果一个没有,则连按钮都没有!【xch/2012-04-25】
					if (templetItemVOs[i].getIsCommQueryShowable()) { //
						isHaveOneCommQueryItem = true; //
						break; //只要有一个显示,就当有!
					}
				}

				if (isHaveOneCommQueryItem) { //只要有一个就加入,即只有为0时才不加入!!
					boolean isavecommquery = getTBUtil().getSysOptionBooleanValue("查询框是否显示通用查询", true);
					if (isavecommquery) {
						popup.add(menuItem_commquery); //
					}
				}
			}
			boolean isRestBtnInPop = getTBUtil().getSysOptionBooleanValue("查询框清空条件按钮是否放在下拉菜单中", false); //
			if (isRestBtnInPop) { //如果是放在下拉菜单中,则加入
				popup.add(menuItem_reset); //
			}

			if (ClientEnvironment.isAdmin()) { // 如果是管理身份
				popup.add(menuItem_exportConditiontoXML); //
			}

			popupButton = new WLTPopupButton(WLTPopupButton.TYPE_WITH_RIGHT_TOGGLE, UIUtil.getLanguage(" 查 询"), null, popup); //
			if (ClientEnvironment.isAdmin()) {
				popupButton.getButton().setToolTipText("按住Shift键右键可以查看帮助!"); //
			} else {
				popupButton.getButton().setToolTipText("点击查询"); //
			}
			if (getTBUtil().getSysOptionBooleanValue("查询框查询按钮是否突出显示", true)) {
				popupButton.getButton().setFont(new Font("宋体", Font.BOLD, 13)); //
				popupButton.getButton().setForeground(Color.BLUE); //
				//				popupButton.getButton().setIcon(UIUtil.getImage("office_089.gif")); //
			} else { //中铁建项目中因为所谓的UI标准,必须是指定的样子,即没有图标,字也是黑色的!!其实是没我们的默认好看的，但没办法!!!
				popupButton.getButton().setFont(LookAndFeel.font); //
			}
			popupButton.getButton().setBackground(LookAndFeel.defaultShadeColor1); //systembgcolor
			popupButton.getPopButton().setBackground(LookAndFeel.systembgcolor); //
			//popupButton.getButton().addActionListener(allActionListener);

			popupButton.getButton().addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						onQuickQueryClicked(e.isControlDown());
					} else if (e.getButton() == MouseEvent.BUTTON3 && (ClientEnvironment.getInstance().getLoginModel() == ClientEnvironment.LOGINMODEL_ADMIN || e.isShiftDown())) { //点击右键,如果是管理员,或者Shift右键!!可以预览显示SQL!!! 
						showQuickSQL(); //
					}
				}
			}); //

			popupButton.setPreferredSize(new Dimension(80, 25)); // 设大小
			if (ClientEnvironment.getInstance().getLoginModel() == ClientEnvironment.LOGINMODEL_ADMIN) {
				this.setToolTipText("点击右键可以查看详细查询SQL!");
			}

			queryButton = new WLTButton("  查  询  "); //邮储查询去箭头 【杨科/2012-09-10】	
			if (ClientEnvironment.isAdmin()) {
				queryButton.setToolTipText("按住Shift键右键可以查看帮助!");
			} else {
				queryButton.setToolTipText("点击查询");
			}
			if (getTBUtil().getSysOptionBooleanValue("查询框查询按钮是否突出显示", true)) {
				queryButton.setFont(new Font("宋体", Font.BOLD, 13));
				queryButton.setForeground(Color.BLUE);
			} else { //中铁建项目中因为所谓的UI标准,必须是指定的样子,即没有图标,字也是黑色的!!其实是没我们的默认好看的，但没办法!!!
				queryButton.setFont(LookAndFeel.font);
			}
			queryButton.setBackground(LookAndFeel.defaultShadeColor1);
			queryButton.setBackground(LookAndFeel.systembgcolor);

			queryButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					queryButton.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
					BillQueryPanel.this.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
					if (e.getButton() == MouseEvent.BUTTON1) {
						onQuickQueryClicked(e.isControlDown());
					} else if (e.getButton() == MouseEvent.BUTTON3 && (ClientEnvironment.getInstance().getLoginModel() == ClientEnvironment.LOGINMODEL_ADMIN || e.isShiftDown())) { //点击右键,如果是管理员,或者Shift右键!!可以预览显示SQL!!! 
						showQuickSQL();
					}
					queryButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
					BillQueryPanel.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
				}
			});

			queryButton.setPreferredSize(new Dimension(80, 25));
			if (ClientEnvironment.getInstance().getLoginModel() == ClientEnvironment.LOGINMODEL_ADMIN) {
				this.setToolTipText("点击右键可以查看详细查询SQL!");
			}

			btn_reset = new WLTButton("  重  置   ");
			btn_reset.addActionListener(allActionListener);
			btn_reset.setBackground(LookAndFeel.defaultShadeColor1); //
			btn_complexquery = new WLTButton("显示高级查询");
			btn_complexquery.addActionListener(allActionListener);
			btn_complexquery.setBackground(LookAndFeel.defaultShadeColor1);
			btn_complexquery.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.GRAY));
			JPanel tempPanel_reset = new JPanel(new BorderLayout(0, 0)); //
			tempPanel_reset.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20)); //
			tempPanel_reset.setOpaque(false);
			List btnList = new ArrayList();
			boolean isavecomplexquery = getTBUtil().getSysOptionBooleanValue("查询框是否显示高级查询", false);
			if (!isRestBtnInPop) { //如果不放在下拉菜单中才加入!!!
				if (!getTBUtil().getSysOptionBooleanValue("查询框清空条件按钮是否在上面", false)) { //中铁建项目中肖主任的UI标准竟然是重置按钮在上面,很变态!!!
				//					btn_reset.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.GRAY)); //
					if (ClientEnvironment.isAdmin() || !getTBUtil().getSysOptionBooleanValue("查询是否去箭头", false)) {
						btnList.add(popupButton);
					} else {
						btnList.add(queryButton);
					}
					btnList.add(btn_reset);
				} else {
					//					btn_reset.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, Color.GRAY)); //
					btnList.add(btn_reset);
					if (ClientEnvironment.isAdmin() || !getTBUtil().getSysOptionBooleanValue("查询是否去箭头", false)) {
						btnList.add(popupButton);
					} else {
						btnList.add(queryButton);
					}
				}
				if (isavecomplexquery && isHaveOneCommQueryItem) {
					btnList.add(btn_complexquery);
				}
			} else {
				if (ClientEnvironment.isAdmin() || !getTBUtil().getSysOptionBooleanValue("查询是否去箭头", false)) {
					btnList.add(popupButton);
				} else {
					btnList.add(queryButton);
				}
				if (isavecomplexquery && isHaveOneCommQueryItem) {
					btnList.add(btn_complexquery);
				}
			}
			tempPanel_reset.add(new VFlowLayoutPanel(btnList, 1), BorderLayout.NORTH); //上下排列!!
			btnpanel.add(tempPanel_reset, BorderLayout.NORTH); // 按钮面板...
			this.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));

			JPanel panel_temp = new JPanel(new BorderLayout()); //
			panel_temp.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0)); // 空边框
			panel_temp.setOpaque(false); // 透明的
			boolean isSide = new TBUtil().getSysOptionBooleanValue("查询框查询按钮是否靠边显示", false); //民生/招行/兴业都说紧靠着查询框,中铁建说要靠右,没办法,只能搞开关参数做成活的!!!
			if (isSide) {
				panel_temp.add(btnpanel, BorderLayout.EAST); //
			} else {
				panel_temp.add(btnpanel, BorderLayout.WEST); //
			}
			if (queryPanel.getPreferredSize().getHeight() <= btnpanel.getPreferredSize().getHeight()) {//当查询面板比按钮面板矮的时候2者之间的间距有些问题，即把按钮面板向右挤了，没找到好办法
				queryPanel.setPreferredSize(new Dimension(queryPanel.getPreferredSize().width, btnpanel.getPreferredSize().height));
			}
			this.add(panel_temp, BorderLayout.CENTER); //
		}
	}

	/**
	 * 清空所有快速查询面板中的控件!
	 */
	public void resetAllQuickQueryCompent() {
		for (int i = 0; i < this.v_compents.size(); i++) {
			AbstractWLTCompentPanel quickQueryCompent = (AbstractWLTCompentPanel) v_compents.get(i); //
			quickQueryCompent.reset(); //
		}
	}

	public void resetAllHideQueryCompent() {
		for (int i = 0; i < this.v_compents.size(); i++) {
			AbstractWLTCompentPanel quickQueryCompent = (AbstractWLTCompentPanel) v_compents.get(i); //
			if (!quickQueryCompent.isShowing()) {
				quickQueryCompent.reset(); //
			}
		}
	}

	private VFlowLayoutPanel getQueryPanel(int _type) {
		ArrayList al_allRows = new ArrayList(); // 所有行
		ArrayList al_oneRowCompents = null; // 一行
		for (int i = 0; i < templetItemVOs.length; i++) {
			if (_type == 1) {
				if (!templetItemVOs[i].getIsQuickQueryShowable()) {
					continue; // 如果快速查询不显示,则跳过.
				}
			} else if (_type == 2) {
				if (!templetItemVOs[i].getIsCommQueryShowable()) {
					continue; // 如果通用查询不显示,则跳过.
				}
			}

			String str_type = templetItemVOs[i].getItemtype(); // 编辑时的控件类型
			if (templetItemVOs[i].getQueryItemType() != null && !templetItemVOs[i].getQueryItemType().trim().equals("")) { // 如果定义了查询框条件
				str_type = templetItemVOs[i].getQueryItemType(); // 如果定义了查询框的控件类型
			}
			AbstractWLTCompentPanel compentPanel = null;
			if (v_compents_map.containsKey(templetItemVOs[i].getItemkey())) {//
				compentPanel = (AbstractWLTCompentPanel) v_compents_map.get(templetItemVOs[i].getItemkey());
			} else {
				if (str_type.equals(WLTConstants.COMP_TEXTFIELD) || str_type.equals(WLTConstants.COMP_TEXTAREA) || str_type.equals(WLTConstants.COMP_BIGAREA) || str_type.equals(WLTConstants.COMP_FILECHOOSE) || str_type.equals(WLTConstants.COMP_STYLEAREA)) { // 文本框,多行文本框,附件
					compentPanel = getTextFieldCompent(templetItemVOs[i]);
				} else if (str_type.equals(WLTConstants.COMP_COMBOBOX)) { //下拉框,
					compentPanel = getComboBoxCompent(templetItemVOs[i]);
				} else if (str_type.equals(WLTConstants.COMP_REFPANEL) || // 表形参照
						str_type.equals(WLTConstants.COMP_REFPANEL_TREE) || // 树型参照
						str_type.equals(WLTConstants.COMP_REFPANEL_MULTI) || // 多选参照
						str_type.equals(WLTConstants.COMP_REFPANEL_CUST) || // 自定义参照
						str_type.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET) || // 列表模板参照
						str_type.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || // 树型模板参照
						str_type.equals(WLTConstants.COMP_REFPANEL_REGFORMAT) || // 注册样板参照
						str_type.equals(WLTConstants.COMP_REFPANEL_REGEDIT) || // 注册参照
						str_type.equals(WLTConstants.COMP_DATE) || // 日历控件
						str_type.equals(WLTConstants.COMP_DATETIME) || // 时间控件
						str_type.equals(WLTConstants.COMP_NUMBERFIELD) // 数字框
				) {
					compentPanel = getUIRefCompent(templetItemVOs[i]);
				} else if (str_type.equals(WLTConstants.COMP_CHECKBOX)) { // 勾选框
					compentPanel = getCheckBoxCompent(templetItemVOs[i]);
				} else if (str_type.equals(WLTConstants.COMP_BUTTON)) { // 按钮
					compentPanel = new QueryCPanel_Button(templetItemVOs[i], this);
				} else {
					continue;
				}
				v_compents.add(compentPanel); // 先加入缓存
				if (templetItemVOs[i].getIssave()) {
					v_compentsaves.add(compentPanel);
				}
				v_compents_map.put(templetItemVOs[i].getItemkey(), compentPanel);
			}

			if (_type == 1) {
				compentPanel.setItemEditable(templetItemVOs[i].getIsQuickQueryEditable()); // 是否可编辑
			} else if (_type == 2) {
				compentPanel.setItemEditable(templetItemVOs[i].getIsCommQueryEditable()); // 是否可编辑
			}
			compentPanel.setOpaque(false);
			boolean bo_iswrap = false; //
			if (_type == 1) {
				bo_iswrap = templetItemVOs[i].getIsQuickQueryWrap(); // 是否换行
			} else if (_type == 2) {
				bo_iswrap = templetItemVOs[i].getIsCommQueryWrap(); //
			}

			if (bo_iswrap) { // 如果快速查询换行
				al_oneRowCompents = new ArrayList(); //
				al_allRows.add(al_oneRowCompents); //
				al_oneRowCompents.add(v_compents_map.get(templetItemVOs[i].getItemkey())); // 加入控件
			} else {
				if (al_oneRowCompents == null) { //
					al_oneRowCompents = new ArrayList(); //
					al_allRows.add(al_oneRowCompents); //
				}
				al_oneRowCompents.add(v_compents_map.get(templetItemVOs[i].getItemkey())); // 直接在原来的行中增加新的控件!!!
			}
		} // end for循环

		// 执行默认值公式
		execDefaultFormula(); //

		// 创建页面..
		JComponent[] hflowPanels = new JComponent[al_allRows.size()]; //
		for (int i = 0; i < hflowPanels.length; i++) {
			ArrayList al_row = (ArrayList) al_allRows.get(i); //
			hflowPanels[i] = new HFlowLayoutPanel(al_row); //
		}

		VFlowLayoutPanel vflowPanel = new VFlowLayoutPanel(hflowPanels); // //垂直布局..
		return vflowPanel; //
	}

	private String findItemType(String _itemkey) {
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			if (templetItemVOs[i].getItemkey().equals(_itemkey)) {
				return templetItemVOs[i].getItemtype(); //
			}
		}
		return null;
	}

	private String findItemQueryDefaultFormula(String _itemkey) {
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			if (templetItemVOs[i].getItemkey().equals(_itemkey)) {
				return templetItemVOs[i].getQuerydefaultformula(); //
			}
		}
		return null;
	}

	private void execDefaultFormula() {
		JepFormulaParseAtUI jepParse = new JepFormulaParseAtUI(this); //
		for (int i = 0; i < v_compents.size(); i++) { //
			AbstractWLTCompentPanel compent = (AbstractWLTCompentPanel) v_compents.get(i);
			String str_key = compent.getItemKey();
			String str_type = findItemType(str_key); //
			String formula = findItemQueryDefaultFormula(str_key); //
			if (formula == null || formula.trim().equals("")) {
				continue;
			}
			String[] str_formulas = getTBUtil().split1(formula, ";"); //
			for (int j = 0; j < str_formulas.length; j++) {
				Object obj = jepParse.execFormula(str_formulas[j]); //
				if (j == str_formulas.length - 1) { // 如是是最后一个,则认为是返回值
					try {
						if (obj instanceof String) { //
							String str_objstr = (String) obj; //
							if (str_objstr.equals("ok")) { // 如果是ok则跳过
								continue; //
							}

							if (str_type.equals(WLTConstants.COMP_TEXTFIELD) || // 文本框
									str_type.equals(WLTConstants.COMP_TEXTAREA) || // 多行文本框
									str_type.equals(WLTConstants.COMP_BIGAREA) || // 大文本框
									str_type.equals(WLTConstants.COMP_FILECHOOSE) // 文件选择框
							) {
								compent.setObject(new StringItemVO(str_objstr)); //
							} else if (str_type.equals(WLTConstants.COMP_COMBOBOX)) { // 如果是下拉框
								setCompentObjectValue(str_key, new ComBoxItemVO(str_objstr, str_objstr, str_objstr)); //
							} else if (str_type.equals(WLTConstants.COMP_REFPANEL) || // 表型参照1
									str_type.equals(WLTConstants.COMP_REFPANEL_TREE) || // 树型参照1
									str_type.equals(WLTConstants.COMP_REFPANEL_MULTI) || // 多选参照1
									str_type.equals(WLTConstants.COMP_REFPANEL_CUST) || // 自定义参照
									str_type.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET) || // 列表模板参照
									str_type.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || // 树型模板参照
									str_type.equals(WLTConstants.COMP_REFPANEL_REGFORMAT) || // 注册样板参照
									str_type.equals(WLTConstants.COMP_REFPANEL_REGEDIT) || // 注册参照
									str_type.equals(WLTConstants.COMP_DATE) || // 日历
									str_type.equals(WLTConstants.COMP_DATETIME) || // 时间
									str_type.equals(WLTConstants.COMP_COLOR) || // 颜色选择框
									str_type.equals(WLTConstants.COMP_CALCULATE) || // 计算器
									str_type.equals(WLTConstants.COMP_PICTURE) || // 图片选择框
									str_type.equals(WLTConstants.COMP_NUMBERFIELD) // 数字框
							) { // 如果是参照...
								compent.setObject(new RefItemVO(str_objstr, str_objstr, str_objstr)); //
							} else {
								compent.setObject(new StringItemVO(str_objstr)); //
							}
						} else if (obj instanceof java.lang.Double) {
							compent.setObject(new StringItemVO("" + obj)); //
						} else {
							compent.setObject(obj); //
						}
					} catch (Exception ex) {
						this.logger.error("设置[" + str_key + "]的默认值失败!!!", ex); //
					}

				}
			}
		}
	}

	/**
	 * 文本框
	 * 
	 * @return
	 */
	private AbstractWLTCompentPanel getTextFieldCompent(final Pub_Templet_1_ItemVO _itemvo) {
		AbstractWLTCompentPanel compentPanel = new QueryCPanel_TextField(this, _itemvo); //
		compentPanel.setBillPanel(this); //
		compentPanel.setBackground(LookAndFeel.billlistquickquerypanelbgcolor); //

		((QueryCPanel_TextField) compentPanel).getTextField().addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) { // 光标失去时触发事件!!
				onChanged(_itemvo.getItemkey());
			}
		});

		// 监听事件
		((QueryCPanel_TextField) compentPanel).getTextField().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onChangedAndFocusNext(_itemvo.getItemkey()); // 如果是敲回车的话,则要跳光标
			}
		});
		return compentPanel; //
	}

	/**
	 * 下拉框控件,以前就是一个标准的下拉框,后来改进成可以支持多选的!因为在录入时是单选的,但查询时应该是多选的!
	 * @param _itemvo
	 * @return
	 */
	private AbstractWLTCompentPanel getComboBoxCompent(final Pub_Templet_1_ItemVO _itemvo) {
		AbstractWLTCompentPanel compentPanel = new QueryCPanel_ComboBox(_itemvo); // 下拉框
		compentPanel.setBillPanel(this); //
		compentPanel.setBackground(LookAndFeel.billlistquickquerypanelbgcolor); //
		((QueryCPanel_ComboBox) compentPanel).addActionListener(new ActionListener() { //因为下拉框在查询中变成了多选,而不是标准的JcomboBox了，所以不是ItemListener监听,而是自己包装了个ActionListener
					public void actionPerformed(ActionEvent e) {
						onChanged(_itemvo.getItemkey());
					}
				});
		return compentPanel; //
	}

	/**
	 * 各种参照控件
	 * 
	 * @param _itemvo
	 * @return
	 */
	private AbstractWLTCompentPanel getUIRefCompent(final Pub_Templet_1_ItemVO _itemvo) {
		AbstractWLTCompentPanel compentPanel = new QueryCPanel_UIRefPanel(_itemvo, this, null); // 表型参照
		compentPanel.setBillPanel(this); //
		compentPanel.setBackground(LookAndFeel.billlistquickquerypanelbgcolor); //
		((QueryCPanel_UIRefPanel) compentPanel).addBillQueryEditListener(new BillQueryEditListener() {
			public void onBillQueryValueChanged(BillQueryEditEvent _evt) {
				onChanged(_itemvo.getItemkey()); // 在参照中加入监听器
			}
		});
		return compentPanel; //
	}

	/**
	 * 勾选框控件
	 * 
	 * @param _itemvo
	 * @return
	 */
	private AbstractWLTCompentPanel getCheckBoxCompent(final Pub_Templet_1_ItemVO _itemvo) {
		AbstractWLTCompentPanel compentPanel = new QueryCPanel_CheckBox(_itemvo); // 勾选框//
		compentPanel.setBillPanel(this); //
		compentPanel.setBackground(LookAndFeel.billlistquickquerypanelbgcolor); //
		((QueryCPanel_CheckBox) compentPanel).getComBox().addItemListener(new ItemListener() { // 监听事件
					public void itemStateChanged(ItemEvent e) {
						if (e.getStateChange() == ItemEvent.SELECTED) {
							onChanged(_itemvo.getItemkey());
						}
					}
				});
		return compentPanel; //
	}

	public JButton getQuickQueryButton() {
		return popupButton.getButton(); //
	}

	/**
	 * 反取属于哪个面板
	 * 
	 * @return
	 */
	public BillListPanel getBillListPanel() {
		return billlist;
	}

	public Pub_Templet_1VO getTempletVO() {
		return templetVO;
	}

	public HashMap getQuickQueryConditionAsMap() {
		return getQuickQueryConditionAsMap(false); //
	}

	public HashMap getQuickQueryConditionAsMap(boolean _isAdd$SQL) {
		return getQuickQueryConditionAsMap(_isAdd$SQL, true); //
	}

	/**
	 * 将快速查询条件放在一个HashMap中返回..
	 * 
	 * @return
	 */
	public HashMap getQuickQueryConditionAsMap(boolean _isAdd$SQL, boolean _isDocheck) {
		if (_isDocheck) {
			if (!checkQuickQueryConditionIsNull()) {
				return null;
			}
		}

		HashMap map_condition = new HashMap();
		AbstractWLTCompentPanel[] alcp = getAllQuickQueryCompents();
		for (int i = 0; i < alcp.length; i++) {
			if (alcp.length != 0 && alcp != null) {
				map_condition.put(alcp[i].getItemKey(), getRealValueAt(alcp[i].getItemKey())); //
				map_condition.put("obj_" + alcp[i].getItemKey(), getValueAt(alcp[i].getItemKey())); //
			}
		}

		if (_isAdd$SQL) { //如果需要直接将动态SQL计算出来加入,则处理之
			String str_sql = getQuerySQLConditionByItemKeyMapping(null, false, false, _isDocheck ? false : true); //不使用数据权限
			String str_sql_1 = getQuerySQLConditionByItemKeyMapping(null, false, true, _isDocheck ? false : true); //使用数据权限
			if (str_sql == null || str_sql.trim().equals("")) {
				str_sql = " and '没有条件'='没有条件'"; //
			}
			if (str_sql_1 == null || str_sql_1.trim().equals("")) {
				str_sql_1 = " and '没有条件'='没有条件'"; //
			}
			map_condition.put("$SQL", str_sql); //
			map_condition.put("$SQL_1", str_sql_1); //
		}
		return map_condition;
	}

	public void addBillQuickActionListener(ActionListener _custActionListener) {
		custActionListener = _custActionListener; //
	}

	public void addBillCommonActionListener(ActionListener _custActionListener) {
		custCommonActionListener = _custActionListener; //
	}

	public boolean isQuickQueryQuiet() {
		return isQuickQueryQuiet;
	}

	public void setQuickQueryQuiet(boolean isQuickQueryQuiet) {
		this.isQuickQueryQuiet = isQuickQueryQuiet;
	}

	public void onQuickQueryClicked(final boolean _isCtrl) {
		try {
			popupButton.getButton().setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
			if (isQuickQueryQuiet) { // 如果是静态查询!即有时不想出等待框!!
				if (custActionListener != null) {//sunfujun/20121207/自定义的没有等待框了修改
					custActionListener.actionPerformed(new ActionEvent(BillQueryPanel.this, 1, "" + _isCtrl));
				} else {
					onQuickQuery(_isCtrl); //
				}
			} else {
				if (billlist != null) {
					billlist.stopEditing(); //
				}
				new SplashWindow(JOptionPane.getFrameForComponent(this), new AbstractAction() {
					public void actionPerformed(ActionEvent e) {
						if (custActionListener != null) {//sunfujun/20121207/自定义的没有等待框了修改
							custActionListener.actionPerformed(new ActionEvent(BillQueryPanel.this, 1, "" + _isCtrl));
						} else {
							onQuickQuery(_isCtrl); //
						}
					}
				});
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		} finally {
			popupButton.getButton().setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
		}
	}

	/**
	 * 快速查询动作
	 */
	public void onQuickQuery(boolean _isCtrlDown) {
		onQuickQuery(_isCtrlDown, false);

	}

	public void onQuickQuery(boolean _isCtrlDown, boolean _isRegHVOinRowNumberVO) {
		boolean isHiddenAfterQuery = getTBUtil().getSysOptionBooleanValue("列表查询后是否立即隐藏查询框", false); //
		// 如果自定义ActionListener,则冲掉默认运用
		if (custActionListener != null) {
			custActionListener.actionPerformed(new ActionEvent(this, 1, "" + _isCtrlDown)); //
			if (isHiddenAfterQuery && billlist != null) {
				billlist.setBillQueryPanelVisible(false); //查找数据后立即隐藏查询框,仿照南北软件的功能!!
			}
			return;
		}
		try {
			if (billlist != null) {
				String str_sql = null; //
				if (quickSQLCreater != null) {
					str_sql = quickSQLCreater.getQuickSQL(this); // 如果自定义了SQL生成器,则拿定义的SQL生成器创建SQL!!!
				} else {
					str_sql = getQuerySQL(); // 如果没有定义SQL生成器,则标准的逻辑拼出!!!
				}
				if (str_sql == null) {
					return;
				}
				str_currquicksql = str_sql; //
				if (billlist.getOrderCondition() != null) {
					str_currquicksql = str_currquicksql + " order by " + billlist.getOrderCondition(); //排序!!
				}
				billlist.queryDataByDS(billlist.getDataSourceName(), str_currquicksql, _isRegHVOinRowNumberVO); // 拿拼出的完整的SQL进行查询!!!
				if (billlist.getBillVOs() == null || billlist.getBillVOs().length == 0) {
					MessageBox.show(billlist, "没有查询到记录,请修改条件重新查询!", 3, null); //
					return; //
				}

				//查找数据后立即隐藏查询框,仿照南北软件的功能!!
				if (isHiddenAfterQuery) {
					billlist.setBillQueryPanelVisible(false); //
				}
				afterQuickQueryAddHisToClient();
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	//点击完查询后。查到数据把控件值添加到常用记录中。
	private void afterQuickQueryAddHisToClient() {
		AbstractWLTCompentPanel[] _compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]); //
		for (int i = 0; i < _compents.length; i++) {
			if (_compents[i] instanceof QueryCPanel_TextField) { //文本内容
				Object str = _compents[i].getObject(); //获取查询条件
				QuickSearchHisMap.putValue(templetVO.getTempletcode(), _compents[i].getItemKey(), QuickSearchHisMap.HIS_TYPE_QUERYPANEL, str);
			}
		}
	}

	/**
	 * 校验必输入项
	 * 
	 * @return
	 */
	public boolean checkValidate() {
		// 校验是否有必输入项..
		String str_itemkey = null;
		String str_name = null;
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]); //
		for (int i = 0; i < compents.length; i++) {
			str_itemkey = compents[i].getItemKey();
			str_name = compents[i].getItemName();
			for (int j = 0; j < templetItemVOs.length; j++) {
				if (templetItemVOs[j].getItemkey().equals(str_itemkey)) {
					if ("Y".equalsIgnoreCase(templetItemVOs[j].getIsQueryMustInput())) { // 如果查询框必须输入
						if (compents[i] == null || compents[i].getValue() == null || compents[i].getValue().equals("")) {
							MessageBox.show(this, str_name + "不能为空!", 3, null);
							return false;
						}
					}
				}
			}
		}
		HashMap groupA = new HashMap();
		HashMap groupB = new HashMap();
		HashMap groupC = new HashMap();

		String groupAMustInput = null;
		String groupBMustInput = null;
		String groupCMustInput = null;

		for (int i = 0; i < compents.length; i++) {
			str_itemkey = compents[i].getItemKey();
			str_name = compents[i].getItemName();
			for (int j = 0; j < templetItemVOs.length; j++) {
				if (templetItemVOs[j].getItemkey().equals(str_itemkey)) {
					if (!"Y".equals(groupAMustInput) && "A".equalsIgnoreCase(templetItemVOs[j].getIsQueryMustInput())) {
						if (compents[i] == null || compents[i].getValue() == null || compents[i].getValue().equals("")) {
							groupA.put(str_itemkey, str_name);
							groupAMustInput = "N";
						} else {
							groupAMustInput = "Y";
						}
					} else if (!"Y".equals(groupBMustInput) && "B".equalsIgnoreCase(templetItemVOs[j].getIsQueryMustInput())) {
						if (compents[i] == null || compents[i].getValue() == null || compents[i].getValue().equals("")) {
							groupB.put(str_itemkey, str_name);
							groupBMustInput = "N";
						} else {
							groupBMustInput = "Y";
						}
					} else if (!"Y".equals(groupCMustInput) && "C".equalsIgnoreCase(templetItemVOs[j].getIsQueryMustInput())) {
						if (compents[i] == null || compents[i].getValue() == null || compents[i].getValue().equals("")) {
							groupC.put(str_itemkey, str_name);
							groupCMustInput = "N";
						} else {
							groupCMustInput = "Y";
						}
					}
				}
			}
		}
		StringBuffer str_names = new StringBuffer("以下查询条件,请至少输入一项：\r\n【");
		if ("N".equals(groupAMustInput)) {
			String[] str_groupA = (String[]) groupA.values().toArray(new String[0]);
			for (int i = 0; i < str_groupA.length; i++) {
				str_names.append(str_groupA[i] + ";");
			}
			MessageBox.show(billlist, str_names.substring(0, str_names.length() - 1) + "】", 3, null);
			return false;
		}
		if ("N".equals(groupBMustInput)) {
			String[] str_groupB = (String[]) groupB.values().toArray(new String[0]);
			for (int i = 0; i < str_groupB.length; i++) {
				str_names.append(str_groupB[i] + ";");
			}
			MessageBox.show(billlist, str_names.substring(0, str_names.length() - 1) + "】", 3, null);
			return false;
		}
		if ("N".equals(groupCMustInput)) {
			String[] str_groupC = (String[]) groupC.values().toArray(new String[0]);
			for (int i = 0; i < str_groupC.length; i++) {
				str_names.append(str_groupC[i] + ";");
			}
			MessageBox.show(billlist, str_names.substring(0, str_names.length() - 1) + "】", 3, null);
			return false;
		}

		return true; //
	}

	public String getQuerySQL() {
		AbstractWLTCompentPanel[] _compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]); //
		return getQuerySQL(_compents); // 直接拿所有控件!
	}

	// 得到整个SQL
	public String getQuerySQL(AbstractWLTCompentPanel[] _compentPanels) {
		boolean isValidate = checkValidate(); // 是否通过!!!
		if (!isValidate) { // 如果没通过,则返回
			return null;
		}

		StringBuilder sb_sql_all = new StringBuilder(); //
		sb_sql_all.append("select * from " + templetVO.getTablename() + " where 1=1 "); // //
		sb_sql_all.append(getQuerySQLCondition(_compentPanels, true, false, null)); // 取得条件,并且告诉它,已经做过校验了!
		return sb_sql_all.toString(); //
	}

	public String getTableAllItem() {
		StringBuffer sb_item = new StringBuffer();
		int itemlength = templetItemVOs.length;
		for (int i = 0; i < itemlength; i++) {
			if (templetItemVOs[i].isNeedSave()) {
				if (sb_item.length() == 0) {
					sb_item.append(" " + templetItemVOs[i].getItemkey());
				} else {
					sb_item.append("," + templetItemVOs[i].getItemkey());
				}
			}
		}
		sb_item.append(" ");
		if (sb_item.toString().trim().length() == 0) {
			return "*";
		}
		return sb_item.toString();
	}

	/**
	 * 这个方法非常有用!!,用天其他类在需要的时候调用!!!即只想拼出后面的查询条件!
	 * 
	 * @return
	 */
	public String getQuerySQLCondition() {
		return getQuerySQLCondition(null);
	}

	/**
	 * 非常重要的方法!许多查询面板使用的同一个,但业务表单中的字段名又不一样,需要进行itemkey映射!! 即将模板中的itemkey转换成我的实际数据库字段!!! 
	 * @param _mapping
	 * @return
	 */
	public String getQuerySQLConditionByItemKeyMapping(String[][] _fieldNameMap) {
		HashMap itemKeyMapping = new HashMap(); //
		if (_fieldNameMap != null) {
			for (int i = 0; i < _fieldNameMap.length; i++) {
				itemKeyMapping.put(_fieldNameMap[i][0], _fieldNameMap[i][1]); //
			}
		}
		return getQuerySQLConditionByItemKeyMapping(itemKeyMapping); //
	}

	/**
	 * 许多查询面板使用的同一个,但业务表单中的字段名又不一样,需要进行itemkey映射!! 即将模板中的itemkey转换成我的实际数据库字段!!! 
	 * @param _mapping
	 * @return
	 */
	public String getQuerySQLConditionByItemKeyMapping(HashMap _fieldNameMap) {
		return getQuerySQLConditionByItemKeyMapping(_fieldNameMap, true, true); //
	}

	//是否忽略模板定义的条件
	public String getQuerySQLConditionByItemKeyMapping(HashMap _fieldNameMap, boolean _isIgnoreDefineCondition) {
		return getQuerySQLConditionByItemKeyMapping(_fieldNameMap, _isIgnoreDefineCondition, true); //
	}

	//是否忽略模板定义的条件,以及是否启用数据权限!!!
	public String getQuerySQLConditionByItemKeyMapping(HashMap _fieldNameMap, boolean _isIgnoreDefineCondition, boolean _isUseDataAccess) {
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]); //
		return getQuerySQLCondition(compents, false, _isIgnoreDefineCondition, _isUseDataAccess, null, _fieldNameMap); //
	}

	//是否忽略模板定义的条件,以及是否启用数据权限!!!
	public String getQuerySQLConditionByItemKeyMapping(HashMap _fieldNameMap, boolean _isIgnoreDefineCondition, boolean _isUseDataAccess, boolean _isHaveCheck) {
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]); //
		return getQuerySQLCondition(compents, _isHaveCheck, _isIgnoreDefineCondition, _isUseDataAccess, null, _fieldNameMap); //
	}

	public String getQuerySQLCondition(String _tablePrefix) {
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]); //
		return getQuerySQLCondition(compents, false, false, true, _tablePrefix, null); //
	}

	public String getQuerySQLCondition(boolean _isIgnoreDefineCondition, String _tablePrefix) {
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]); //
		return getQuerySQLCondition(compents, false, _isIgnoreDefineCondition, true, _tablePrefix, null); //
	}

	public String getQuerySQLCondition(AbstractWLTCompentPanel[] _compents, boolean _isHaveChecked, boolean _isIgnoreDefineCondition, String _tablePrefix) {
		return getQuerySQLCondition(_compents, _isHaveChecked, _isIgnoreDefineCondition, true, _tablePrefix, null); //
	}

	/**
	 * 最重要的方法,取得各个控件的查询条件,拼成后面的Where条件用!!
	 * 
	 * @param _compents
	 * @param _isHaveChecked 是否校验过条件了,即如果查询条件是否是必输项!!!
	 * @return
	 */
	public String getQuerySQLCondition(AbstractWLTCompentPanel[] _compents, boolean _isHaveChecked, boolean _isIgnoreDefineCondition, boolean _isUseDataAccess, String _tablePrefix, HashMap _itemKeyMap) {
		if (!_isHaveChecked) { // 如果没有检查过则需要检查一把!
			boolean isValidate = checkValidate(); // 是否通过!!!
			if (!isValidate) { // 如果没通过,则返回
				return null;
			}
		}

		StringBuffer sb_sql_all = new StringBuffer(""); //

		JepFormulaParseAtUI jepUI = null; //
		LinkedHashMap lhmap_itemValue = new LinkedHashMap(); // 存储各个条件的值
		LinkedHashMap lhmap_itemSQL = new LinkedHashMap(); // 存储各个条件拼成的SQL
		if (!_isIgnoreDefineCondition) { // 如果忽略模板定义的条件!则不拼其定义的条件
			if (billlist != null) {
				String str_datafiltercondition = billlist.getDataconstraint(); //
				if (str_datafiltercondition != null && !str_datafiltercondition.trim().equals("")) {
					sb_sql_all.append(" and (" + str_datafiltercondition + ") "); //
					lhmap_itemValue.put("$datacondition", str_datafiltercondition); //
					lhmap_itemSQL.put("$datacondition", str_datafiltercondition); //
				}
			}
		}

		if (billlist != null) {
			String str_custdatafiltercondition = billlist.getDataFilterCustCondition(); //
			if (str_custdatafiltercondition != null && !str_custdatafiltercondition.trim().equals("")) {
				sb_sql_all.append(" and (" + str_custdatafiltercondition + ") "); //
				lhmap_itemValue.put("$custdatacondition", str_custdatafiltercondition); //
				lhmap_itemSQL.put("$custdatacondition", str_custdatafiltercondition); //
			}
		}

		//先计算出默认的查询条件中空格的解析关系!!!
		String str_andorype = getTBUtil().getSysOptionStringValue("查询条件中空格的解析关系", "and"); //一个文本框中输入的查询条件有空格时自动解析成多个条件,但多个条件之间到底是and的关系还是or的关系,都有道理!! 以前默认是or的关系,但兴业银行中的常辉非常纠结,认为一定是and关系!!
		if (str_andorype.trim().equalsIgnoreCase("or")) { //如果是明确是or,则是or
			str_andorype = "or"; //
		} else {
			str_andorype = "and"; //否则默认永远是and,即不小心设成其他值,都认为是and
		}

		LinkedHashMap lm_custSQLCreateClass = new LinkedHashMap(); // 创建自定义SQL的Class类名,必须实现接口IBillQueryPanelSQLCreateIFC
		//遍历各个控件!!!
		for (int i = 0; i < _compents.length; i++) { // 遍历所有控件
			AbstractWLTCompentPanel comptmp = _compents[i]; // //
			Object valueobj = comptmp.getObject(); //
			if (valueobj == null) {
				continue; // 如何值为空,则跳过....
			}
			// 取得实际的值!!即下拉框或参照分别取id的值!
			String str_realvalue = null; //
			String str_allChildIds = null; //所有子结点数据!在树型结构中,如果是单选,则也同时返回子结点!只不过存储在一个特殊变量【$所有子结点ID】中,以后如果配置成【In机制】,则默认强行先找一下该变量!如果有,则使用该变量!
			if (valueobj instanceof StringItemVO) { // 如果是文本框
				str_realvalue = ((StringItemVO) valueobj).getStringValue(); //
			} else if (valueobj instanceof ComBoxItemVO) {
				str_realvalue = ((ComBoxItemVO) valueobj).getId(); //
			} else if (valueobj instanceof RefItemVO) {
				RefItemVO refVO = (RefItemVO) valueobj; //
				str_realvalue = refVO.getId(); //
				if (refVO.getHashVO() != null) {
					str_allChildIds = refVO.getHashVO().getStringValue("$所有子结点ID"); //
				}
			}
			if (str_realvalue == null || str_realvalue.trim().equals("")) { // 如果是空值或空串!!则直接跳过去!
				continue; // 直接跳过!// 曾经在招行项目中发现多选参照没选时返回是空串,结果导致SQL报错!
			}

			if (isHenDeng(str_realvalue)) { //如果是恒等式,则直接加入并跳过循环！
				sb_sql_all.append(" and " + str_realvalue + " "); // 加入全部...
				continue; //
			}

			String str_itemkey = comptmp.getItemKey().toLowerCase(); // 统统转换成小写
			if (str_itemkey.startsWith("#")) {
				str_itemkey = str_itemkey.substring(1, str_itemkey.length()); // 如果是#开头,则截去之,这是为了解决itemkey相同的问题
			}
			String str_queryitemkey = str_itemkey; //
			if (_itemKeyMap != null) { // 如果有转换,换名
				String[] str_itemkeyMappings = (String[]) _itemKeyMap.keySet().toArray(new String[0]); //
				for (int j = 0; j < str_itemkeyMappings.length; j++) {
					if (str_itemkeyMappings[j].equalsIgnoreCase(str_itemkey)) { // 忽略大小写
						str_queryitemkey = (String) _itemKeyMap.get(str_itemkeyMappings[j]); // 换名!
						break; //
					}
				}
			}
			if (_tablePrefix != null) { // 如果指定了伪表名称!!!则加在前面!!
				str_queryitemkey = _tablePrefix + "." + str_queryitemkey; //
			}
			lhmap_itemValue.put(str_itemkey, str_realvalue); // 对象的实际值!!
			lhmap_itemValue.put(str_itemkey + "_obj", valueobj); // 对象值!!

			Pub_Templet_1_ItemVO itemVO = templetVO.getItemVo(str_itemkey); //
			String str_itemtype = null; // 控件类型
			String str_SqlCreateType = itemVO.getQuerycreatetype(); // SQL创建类型!!

			StringBuffer sb_sql = new StringBuffer(); //
			if (str_SqlCreateType == null || str_SqlCreateType.trim().equals("") || str_SqlCreateType.trim().equals("默认机制")) {// 如果是默认机制
				if (itemVO.getQueryItemType() != null && !itemVO.getQueryItemType().trim().equals("")) {
					str_itemtype = itemVO.getQueryItemType(); // 如果指定义查询框类型定义,则取之,否则用默认的
				} else {
					str_itemtype = itemVO.getItemtype(); //
				}
				if (str_itemtype.equals(WLTConstants.COMP_TEXTFIELD) || str_itemtype.equals(WLTConstants.COMP_TEXTAREA)) { //文本框,多行文本框
					str_realvalue = getTBUtil().replaceAll(str_realvalue.trim(), "'", "''");//处理查询条件中特殊字符【李春娟/2016-02-15】
					String[] str_items = getTBUtil().split(str_realvalue, " "); //如果输了多个条件,则以空格分隔,每个条件之间用
					if (str_items.length <= 1) { //如果只有一个条件!!
						if (str_realvalue.startsWith("~")) {
							sb_sql.append(" and " + str_queryitemkey + " like '" + str_realvalue.substring(1, str_realvalue.length()) + "%'"); //
						} else {
							sb_sql.append(" and " + str_queryitemkey + " like '%" + str_realvalue + "%'"); //
						}
					} else {
						sb_sql.append(" and (");
						for (int j = 0; j < str_items.length; j++) { //如果有多个条件,则
							String str_itemTrimValue = str_items[j].trim(); // //
							if (str_itemTrimValue.startsWith("~")) {
								sb_sql.append(str_queryitemkey + " like '" + str_itemTrimValue.substring(1, str_itemTrimValue.length()) + "%'"); // //
							} else {
								sb_sql.append(str_queryitemkey + " like '%" + str_itemTrimValue + "%'"); // //
							}
							if (j != str_items.length - 1) { //如果不是最后一位,则加上and/or
								sb_sql.append(" " + str_andorype + " "); //默认是and关系,以前是or的关系,但兴业与招行的客户都要求是and关系!!其实两者都有一定道理!! 现在的机制是默认是and关系,但可以通过参数【查询条件中空格的解析关系】=【or】而改变!
							}
						}
						sb_sql.append(")");
					}
				} else if (str_itemtype.equals(WLTConstants.COMP_NUMBERFIELD)) {// 数字框
					RefItemVO refItemVO = (RefItemVO) valueobj;
					if (refItemVO.getHashVO() != null && refItemVO.getHashVO().getStringValue("querycondition") != null) {
						String str_macrocondition = refItemVO.getHashVO().getStringValue("querycondition"); //
						String str_convertcondition = getTBUtil().replaceAll(str_macrocondition, "{itemkey}", str_itemkey); //
						sb_sql.append(" and " + str_convertcondition); //
					} else {
						String[] str_value = refItemVO.getId().split(";");
						if (!str_value[0].equals("")) {
							if (str_value[1].equals("")) {
								sb_sql.append(" and " + str_queryitemkey + ">=" + str_value[0]);
							} else {
								sb_sql.append(" and (" + str_queryitemkey + ">=" + str_value[0] + " and " + str_queryitemkey + "<=" + str_value[1] + ")");
							}
						} else {
							if (!str_value[1].equals("")) {
								sb_sql.append(" and " + str_queryitemkey + "<=" + str_value[1]);
							}
						}
					}
				} else if (str_itemtype.equals(WLTConstants.COMP_COMBOBOX)) { //下拉框!!!!以前查询的下拉框与编辑是一样的,后来查询改成了多选!! 就自然是变成了in了!!
					if (str_realvalue.indexOf(";") >= 0) { //如果有分号,则分隔处理,使用in查询!!! 因为后来的机制是下拉框在查询时变成了多选!所以是in机制了!!
						String[] str_in_items = getTBUtil().split(str_realvalue, ";"); //
						sb_sql.append(" and " + str_queryitemkey + " in (" + getTBUtil().getInCondition(str_in_items) + ")"); //
					} else {
						sb_sql.append(" and " + str_queryitemkey + "='" + str_realvalue + "'"); //
					}
				} else if (str_itemtype.equals(WLTConstants.COMP_CHECKBOX)) { // 勾选框
					//如果是勾选框，并且模板中设置“快速查询是否可编辑”为【否】， 就不拼sql条件了 Gwang 2016-05-10
					if (!comptmp.isItemEditable()) {
						continue;
					}
					sb_sql.append(" and " + str_queryitemkey + "='" + str_realvalue + "'");
				} else if (str_itemtype.equals(WLTConstants.COMP_DATE) || str_itemtype.equals(WLTConstants.COMP_DATETIME)) { // 日历与时间
					RefItemVO refItemVO = (RefItemVO) valueobj;
					if (refItemVO.getHashVO() != null && refItemVO.getHashVO().getStringValue("querycondition") != null) {
						String str_macrocondition = refItemVO.getHashVO().getStringValue("querycondition"); //
						String str_convertcondition = getTBUtil().replaceAll(str_macrocondition, "{itemkey}", str_queryitemkey); //
						sb_sql.append(" and " + str_convertcondition); //
					} else {
						sb_sql.append(" and " + str_queryitemkey + "='" + str_realvalue+"'"); //zzl [2019-1-7 bug调整]
					}
				} else if (str_itemtype.equals("多选参照")) { // 多选参照!!!
					String[] tempid = getTBUtil().split(str_realvalue, ";"); // str_realvalue.split(";");
					if (tempid != null && tempid.length > 0) {
						sb_sql.append(" and (");
						for (int j = 0; j < tempid.length; j++) {
							sb_sql.append(str_queryitemkey + " like '%;" + tempid[j] + ";%'"); // 
							if (j != tempid.length - 1) { //
								sb_sql.append(" or ");
							}
						}
						sb_sql.append(") "); //
					}
				} else if (str_itemtype.equals(WLTConstants.COMP_REFPANEL) || // 表形参照
						str_itemtype.equals(WLTConstants.COMP_REFPANEL_TREE) || // 树型参照
						str_itemtype.equals(WLTConstants.COMP_REFPANEL_CUST) || // 自定义参照
						str_itemtype.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET) || // 列表模板参照
						str_itemtype.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || // 树型模板参照
						str_itemtype.equals(WLTConstants.COMP_REFPANEL_REGFORMAT) || // 注册样板参照
						str_itemtype.equals(WLTConstants.COMP_REFPANEL_REGEDIT) // 注册参照
				) {
					if (str_itemtype.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET)) { //如果是树型参照,则无论是单选还是多选,都默认是in机制!!!因为“In机制”本身就天然兼容“=机制”的,所以这样怎么都没错!
						//以前不知道为什么还判断如果是多选,则认为是like or机制?只有在存储的是多选时才是LikeOr机制!! 因为后来已经增加了【查询机制】这个属性，只需直接将【查询机制】设置为【LikeOr机制】则就可以了!
						sb_sql.append(" and " + str_queryitemkey + " in (" + getDynInConditions(str_realvalue) + ") "); //
					} else {
						if (str_realvalue.indexOf(";") > 0) { //如果不是树型参照,比如下拉框(可能还是多选哦!),则加个智能判断,从而判断是“In机制”还是“=机制”
							sb_sql.append(" and " + str_queryitemkey + " in (" + getDynInConditions(str_realvalue) + ") ");
						} else {
							sb_sql.append(" and " + str_queryitemkey + "='" + str_realvalue + "'");
						}
					}

				} else if (str_itemtype.equals("文件选择框") || str_itemtype.equals(WLTConstants.COMP_STYLEAREA)) { //如果是文件选择框,富文本框
					sb_sql.append(" and " + str_queryitemkey + " like '%" + str_realvalue + "%'");
				} else { //
					sb_sql.append(" and " + str_queryitemkey + " like '%" + str_realvalue + "%'"); //其他条件都是
				}
			} else if (str_SqlCreateType.equals("In机制")) { // in机制
				String str_parseStr = null; //
				if (str_allChildIds != null) { //如果是个参照对象,并且返回的参照VO中的HashVO中存在一个名为【$所有子结点ID】的变量,则自动使用该变量!!
					str_parseStr = str_allChildIds; //
				} else {
					str_parseStr = str_realvalue; //
				}
				sb_sql.append(" and " + str_queryitemkey + " in (" + getDynInConditions(str_parseStr) + ") "); //
			} else if (str_SqlCreateType.equals("LikeOr机制")) { // like机制
				String[] tempid = getTBUtil().split(str_realvalue, ";"); //
				sb_sql.append(" and (");
				for (int j = 0; j < tempid.length; j++) {
					sb_sql.append(str_queryitemkey + " like '%;" + tempid[j] + ";%'"); // 
					if (j != tempid.length - 1) { //
						sb_sql.append(" or ");
					}
				}
				sb_sql.append(") "); //
			} else if (str_SqlCreateType.equals("SQLServer全文检索")) { // 全文检索!!
				// 在招行中遇到SQLServer在 like '%条件%'时死活过不去! 最后不得不用全文检索技术!
				sb_sql.append(" and contains(" + str_queryitemkey + ",'" + str_realvalue + "') ");
			} else if (str_SqlCreateType.equals("SQLServer全文检索2")) { // 全文检索!!,因为遇到中文分词又有问题,不得不仿照英文使用空格强行分拆,从而保证所有的条件都能查到!!!
				// 在招行中遇到SQLServer在 like '%条件%'时死活过不去!最后不得不用全文检索技术!
				sb_sql.append(" and contains(" + str_queryitemkey + ",'" + appendSearchCon(str_realvalue) + "') ");
			} else if (str_SqlCreateType.equals("自定义SQL")) {
				String str_SqlCreateCustDefine = itemVO.getQuerycreatecustdef(); // SQL创建器自定义!!
				if (str_SqlCreateCustDefine != null && !str_SqlCreateCustDefine.trim().equals("")) { //
					if (jepUI == null) {
						jepUI = new JepFormulaParseAtUI(); // 如果没有创建,则创建之!!!
					}
					str_SqlCreateCustDefine = (String) jepUI.execFormula(str_SqlCreateCustDefine); // 执行公式...
					String[] str_items = getTBUtil().split(str_realvalue, ";"); //
					String str_insqls = getTBUtil().getInCondition(str_items); //
					StringBuffer sb_LikeOrSqls = new StringBuffer(); // //
					sb_LikeOrSqls.append(" ("); // //
					for (int j = 0; j < str_items.length; j++) {
						sb_LikeOrSqls.append(str_queryitemkey + " like '%;" + str_items[j] + ";%'"); // 
						if (j != str_items.length - 1) { //
							sb_LikeOrSqls.append(" or ");
						}
					}
					sb_LikeOrSqls.append(") "); // //
					// 支持三种宏代码替换: {ItemValue},{InValues},{LikeOrSQL}
					str_SqlCreateCustDefine = getTBUtil().replaceAll(str_SqlCreateCustDefine, "{itemvalue}", str_realvalue); //
					str_SqlCreateCustDefine = getTBUtil().replaceAll(str_SqlCreateCustDefine, "{invalues}", str_insqls); //
					str_SqlCreateCustDefine = getTBUtil().replaceAll(str_SqlCreateCustDefine, "{LikeOrSQL}", sb_LikeOrSqls.toString()); //
					sb_sql.append(" and (" + str_SqlCreateCustDefine + ") "); // //
				} else {
					sb_sql.append(" and '没有定义SQL'='没有定义SQL' "); //
				}
			} else if (str_SqlCreateType.equals("自定义类")) { // 如果自定了类!!
				String str_SqlCreateCustDefine = itemVO.getQuerycreatecustdef(); // SQL创建器自定义!!!
				if (str_SqlCreateCustDefine != null && !str_SqlCreateCustDefine.trim().equals("")) { //
					lm_custSQLCreateClass.put(str_itemkey, str_SqlCreateCustDefine); // 类名!!!先塞进去,最后一起计算,因为他可能会用到其他的SQL条件,从而提高性能!!!
				}
			} else if (str_SqlCreateType.equals("不参与")) { // 如果不参与就不做
			}

			if (!sb_sql.toString().equals("")) {
				lhmap_itemSQL.put(str_itemkey, sb_sql.toString()); // //
				sb_sql_all.append(sb_sql.toString()); // 加入全部...
			}

		} // for 循环结束!!!

		// 处理数据权限!!!
		if (_isUseDataAccess && billlist != null) { //如果使用模板定义的数据权限!
			String str_AccessSqlCons = this.billlist.getDataPolicyCondition(); // 数据权限资源定义!!
			if (str_AccessSqlCons != null) { //
				lhmap_itemSQL.put("$resaccess", " and (" + str_AccessSqlCons + ") "); // 资源权限生成的!!!
				sb_sql_all.append(" and (" + str_AccessSqlCons + ") "); //
			}
		} else if (_isUseDataAccess && null != dataPolicyCondition && !dataPolicyCondition.equals("")) {//袁江晓20120913添加             处理报表部分的权限问题
			lhmap_itemSQL.put("$resaccess", " and (" + dataPolicyCondition + ") "); // 资源权限生成的!!!
			sb_sql_all.append(" and (" + dataPolicyCondition + ") "); //

		}

		// 最后处理自定义的类,因为这是最费时的,因为它要查询关键字等，所以为了提高性能，必须将前面生成的SQL都送到后台去,这样,也许他就不用做了!!从而提高性能
		String[] str_sqlCreates = (String[]) lm_custSQLCreateClass.keySet().toArray(new String[0]); //
		for (int i = 0; i < str_sqlCreates.length; i++) { // 循环处理各个类
			String str_className = (String) lm_custSQLCreateClass.get(str_sqlCreates[i]); // 类名!!
			String str_thisItemValue = (String) lhmap_itemValue.get(str_sqlCreates[i]); //
			try {
				String str_sql = UIUtil.getMetaDataService().getBillQueryPanelSQLCustCreate(str_className, str_sqlCreates[i], str_thisItemValue, lhmap_itemValue, lhmap_itemSQL, sb_sql_all.toString());
				if (str_sql != null && !str_sql.trim().equals("")) { // 如果不为空!!!
					lhmap_itemSQL.put(str_sqlCreates[i], " and (" + str_sql + ") "); // 再加入!!
					sb_sql_all.append(" and (" + str_sql + ") "); //
				}
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}
		}

		// System.out.println(sb_sql);
		return sb_sql_all.toString();
	}

	//是否恒等,比如【'所有数据'='所有数据'】??
	private boolean isHenDeng(String _str) {
		_str = _str.trim(); //
		if (_str.indexOf("=") > 0) {
			String str_1 = _str.substring(0, _str.indexOf("=")); //等于号前的
			String str_2 = _str.substring(_str.indexOf("=") + 1); //等于号前的
			if (str_1.trim().equals(str_2.trim())) {
				return true; //
			}
		}
		return false; //
	}

	/**
	 * 根据结点数据动态决定到底是否要到服务器端转一把???
	 * @param _initStr
	 * @return
	 * @throws Exception
	 */
	private String getDynInConditions(String _initStr) {
		try {
			String[] str_items = getTBUtil().split(_initStr, ";"); //; //
			if (str_items.length <= 999) { // 如果是在1000个以内,则不干了,直接拼!!
				return getTBUtil().getInCondition(str_items); //
			} else {
				String str_inCons = UIUtil.getSubSQLFromTempSQLTableByIDs(str_items); // 取得子查询的条件,如果没有超过800则直接返回!!
				return str_inCons; //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return "'发生异常'";
		}
	}

	// //
	private String appendSearchCon(String _str) {
		_str = new TBUtil().replaceAll(_str, " ", ""); // 王雷在招行项目中发现有空格会报错,所以先将空格统统去掉!!!
		StringBuilder sb_con = new StringBuilder(); //
		for (int i = 0; i < _str.length(); i++) {
			if (i != _str.length() - 1) {
				sb_con.append(_str.substring(i, i + 1) + "*"); //
			} else {
				sb_con.append(_str.substring(i, i + 1)); //
			}
		}
		return sb_con.toString(); //
	}

	public boolean checkQuickQueryConditionIsNull() {
		// 校验是否有必输入项..
		String str_itemkey = null;
		String str_name = null;
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]); //
		for (int i = 0; i < compents.length; i++) {
			str_itemkey = compents[i].getItemKey();
			str_name = compents[i].getItemName();
			for (int j = 0; j < templetItemVOs.length; j++) {
				if (templetItemVOs[j].getItemkey().equals(str_itemkey)) {
					if ("Y".equalsIgnoreCase(templetItemVOs[j].getIsQueryMustInput())) { // 如果查询框必须输入
						if (compents[i] == null || compents[i].getValue() == null || compents[i].getValue().equals("")) {
							MessageBox.show(this, str_name + "不能为空!", 3, null);
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	//右键显示查询条件!!
	private void showQuickSQL() {
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("当前列表实际查询的SQL是:\r\n[" + this.str_currquicksql + "]\r\n\r\n"); //
		sb_sql.append("调用方法BillQueryPanel.getQuerySQLConditionByItemKeyMapping(HashMap _fieldMap,boolean _isIgnoreDefineCondition, boolean _isUseDataAccess)得到查询条件的SQL:\r\n"); //
		String str_sql = getQuerySQLConditionByItemKeyMapping(null, true, true, true); //后面两个参数分别表示:是否忽略模板自定义条件;是否启用数据权限
		sb_sql.append("[" + str_sql + "],该SQL最后将拼接成【select * from 表名 where 1=1 ${生成的条件}】\r\n"); //
		sb_sql.append("其中参数_fieldMap表示可以将对应的itemkey替换成新的名称,_isIgnoreDefineCondition表示是否忽略模板中定义的附加条件,_isUseDataAccess表示是否启用模板中定义的数据权限策略!"); //
		MessageBox.show(this, sb_sql.toString()); //
	}

	/**
	 * 通用查询动作. 就是点击通用查询按钮后执行的动作
	 */
	public void onCommonQuery() {
		onCommonQuery(false);
	}

	public void onCommonQuery(boolean _isRegHVOinRowNumberVO) {
		if (1 == 2) {//以前是弹出来后来邮储希望不弹出来和高级查询是一个逻辑，以前如果快速查询与通用查询都有同一个条件会有问题，只是一般情况不会出现（编辑公式），现在不会了/sunfujun/20121207
			if (commQueryDialog == null) { // 如果没有创建查询框,则创建之
				commqueryPanel = getQueryPanel(2); // //
				int li_width = (int) (commqueryPanel.getPreferredSize().getWidth() + 100); //
				int li_height = (int) (commqueryPanel.getPreferredSize().getHeight() + 60); //

				if (li_width < 300) {
					li_width = 300;
				}
				if (li_width > 1000) {
					li_width = 1000;
				}

				if (li_height < 150) {
					li_height = 150;
				}
				if (li_width > 700) {
					li_width = 700;
				}

				commQueryDialog = new CommQueryDialog(this, this.templetVO.getTempletname() + "[通用查询]", li_width, li_height, commqueryPanel); //
			}
			commQueryDialog.setVisible(true); // 显示查询框.
			if (commQueryDialog.getCloseType() == BillDialog.CONFIRM) { // 如果是确定返回
				if (custCommonActionListener != null) {
					custCommonActionListener.actionPerformed(new ActionEvent(this, 1, "commonquery")); //
					return;
				}
				String str_sql = null; //
				if (quickSQLCreater != null) {
					str_sql = quickSQLCreater.getQuickSQL(this); // 如果自定义了SQL生成器,则拿定义的SQL生成器创建SQL!!!
				} else {
					str_sql = getQuerySQL(); // 如果没有定义SQL生成器,则标准的逻辑拼出!!!
				}
				billlist.queryDataByDS(billlist.getDataSourceName(), str_sql, _isRegHVOinRowNumberVO); // 执行SQL
			}
		} else {
			showOrHidenComplexQuery();
		}
	}

	/**
	 * 将当前条件保存成XML
	 */
	public void exportConditionToXML() {
		HashMap conditionMap = getQuickQueryConditionAsMap(); // 先取出查询条件.
		if (conditionMap == null) {
			return;
		}
		XMLIOUtil xmlutil = new XMLIOUtil(); //
		try {
			String str_xml = xmlutil.exportObjToXMLString(conditionMap); //
			BillDialog dialog = new BillDialog(this, "导出条件", 800, 500); // //
			JTextArea textArea = new JTextArea(str_xml);
			textArea.setForeground(Color.BLUE); //
			textArea.setEditable(false); //
			textArea.setFont(LookAndFeel.font); //
			dialog.getContentPane().add(new JScrollPane(textArea)); //
			dialog.setVisible(true); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * 
	 * @return
	 */
	public AbstractWLTCompentPanel[] getAllQuickQueryCompents() {
		return (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]); //
	}

	public AbstractWLTCompentPanel[] getAllQuickQuerySaveCompents() {
		return (AbstractWLTCompentPanel[]) v_compentsaves.toArray(new AbstractWLTCompentPanel[0]); //
	}

	public AbstractWLTCompentPanel getCompent(String _key) {
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]);
		for (int i = 0; i < compents.length; i++) {
			if (_key.equalsIgnoreCase(compents[i].getItemKey())) {
				return compents[i];
			}
		}
		return null;
	}

	/**
	 * 真正执行某一个公式..使用JEP去执行!!
	 * 
	 * @param _formula
	 */
	private void dealFormula(String _formula) {
		//String str_formula = UIUtil.replaceAll(_formula, " ", "");//这里同BillCardPanel中都需要注释掉，否则，如果公式中有查询sql语句，则会报错【李春娟/2013-06-03】
		getJepFormulaValue(_formula); //
	}

	private String getJepFormulaValue(String _exp) {
		JepFormulaParse jepParse = new JepFormulaParseAtUI(this); // 创建解析器!!
		Object obj = jepParse.execFormula(_exp);
		if (obj == null) {
			return "";
		}
		String str_return = "";

		if (obj instanceof Double) {
			Double d_value = (Double) obj;
			double ld_tmp = d_value.doubleValue();
			long ll_tmp = d_value.longValue(); //
			if (ld_tmp == ll_tmp) {
				return "" + ll_tmp;
			} else {
				return "" + ld_tmp;
			}
		} else if (obj instanceof String) {
			str_return = "" + obj;
		}

		return str_return;
	}

	/**
	 * 执行某一项的加载公式!
	 */
	public void execEditFormula(String _itemkey) {
		String[] str_editformulas = getEditFormulas(_itemkey); //
		if (str_editformulas == null || str_editformulas.length == 0) {
			return;
		}

		// 循环处理公式...
		for (int i = 0; i < str_editformulas.length; i++) {
			dealFormula(str_editformulas[i]); // 真正执行公式!!!
			logger.debug("快速查询面板执行[" + _itemkey + "]的编辑公式:[" + str_editformulas[i] + "]"); //
		}
	}

	/**
	 * 取得某一项的所有公式,它是一个数组
	 * 
	 * @param _itemkey
	 * @return
	 */
	public String[] getEditFormulas(String _itemkey) {
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			if (templetItemVOs[i].getItemkey().equalsIgnoreCase(_itemkey)) {
				String str_formula = templetItemVOs[i].getEditformula();
				if (str_formula != null && !str_formula.trim().equals("null") && !str_formula.trim().equals("")) {
					return getTBUtil().split1(str_formula, ";");
				} else {
					return null;
				}
			}
		}
		return null;
	}

	public AbstractWLTCompentPanel getCompentByKey(String _key) {
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]);
		for (int i = 0; i < compents.length; i++) {
			if (compents[i].getItemKey().equalsIgnoreCase(_key)) {
				return compents[i];
			}
		}

		return null;
	}

	/**
	 * 
	 * @param _itemkey
	 * @return
	 */
	private AbstractWLTCompentPanel getNextCompent(String _itemkey) {
		int li_startindex = findIndex(_itemkey);
		for (int i = li_startindex + 1; i < this.templetItemVOs.length; i++) {
			if (templetItemVOs[i].isCardisshowable().booleanValue()) { // 如果某个控件是卡片显示的!!
				AbstractWLTCompentPanel panel = getCompentByKey(templetItemVOs[i].getItemkey()); //
				if (panel != null && panel.isVisible()) { // 如果控件在,而且显示着!!
					return panel;
				}
			}
		}

		return null;
	}

	private int findIndex(String _itemkey) {
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			if (templetItemVOs[i].getItemkey().equals(_itemkey)) {
				return i;
			}
		}
		return 9999;
	}

	/**
	 * 得到真正的值
	 */
	public String getRealValueAt(String _key) {
		return getCompentRealValue(_key);

	}

	public String getCompentRealValue(String _key) {
		int li_pos = _key.indexOf(".");
		String str_itemkey = null;
		String str_subfix = null;
		if (li_pos > 0) {
			str_itemkey = _key.substring(0, li_pos); //
			str_subfix = _key.substring(li_pos + 1, _key.length()); //
		} else {
			str_itemkey = _key;
		}

		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]);
		for (int i = 0; i < compents.length; i++) {
			if (compents[i].getItemKey().equalsIgnoreCase(str_itemkey)) {
				if (li_pos > 0) { // 如果有后辍
					Object obj = compents[i].getObject();
					if (obj instanceof ComBoxItemVO) {
						return ((ComBoxItemVO) obj).getItemValue(str_subfix); //
					} else if (obj instanceof RefItemVO) {
						return ((RefItemVO) obj).getItemValue(str_subfix); //
					}
				} else {
					return compents[i].getValue();
				}
			}
		}
		return "";
	}

	/**
	 * 设置真正的值
	 * 
	 * @param _key
	 * @param _value
	 */
	// 设置值..
	public void setCompentObjectValue(String _key, Object _obj) {
		AbstractWLTCompentPanel compent = getCompentByKey(_key);
		if (compent != null) {
			compent.setObject(_obj); //
		}
	}

	public Pub_Templet_1_ItemVO getTempletItemVO(String key) {
		for (int i = 0; i < templetItemVOs.length; i++) {
			if (templetItemVOs[i].getItemkey().equalsIgnoreCase(key))
				return templetItemVOs[i];
		}
		return null;
	}

	public String getTempletCode() {
		return this.str_templetcode; //
	}

	public void setRealValueAt(String _key, String _value) {
		AbstractWLTCompentPanel compent = getCompentByKey(_key);
		if (compent == null) {
			return;
		}
		compent.setValue(_value); //
	}

	// 设置各个控件的前景颜色!文本框,下拉框,参照等,暂不实现,以后会慎重考虑是通过修改值再触发事件来实现，还是直接修改控件属性来实现!即如何保证值与控件属性保持一致!
	public void setItemForeGroundColor(String _key, String _foreGroundColor) {

	}

	private synchronized void onChanged(String _itemkey) {
		if (bo_isallowtriggereditevent) { // 如果允许触发
			bo_isallowtriggereditevent = false;
			Object obj = this.getCompent(_itemkey).getObject(); // 取得当前值
			execEditFormula(_itemkey); // 执行编辑公式..它与下一行代码谁先谁后还有待进一步考虑!!!!!!
			for (int i = 0; i < v_listeners.size(); i++) {
				BillQueryEditListener listener = (BillQueryEditListener) v_listeners.get(i);
				listener.onBillQueryValueChanged(new BillQueryEditEvent(_itemkey, obj, this)); //
			}

			bo_isallowtriggereditevent = true; //
		}
	}

	public void setVisiable(String _itemkey, boolean _bo) {
		AbstractWLTCompentPanel panel = getCompentByKey(_itemkey);
		if (panel != null) {//这里需要判断一下，只有找到了控件才设置是否显示，否则会报空指针错误【李春娟/2012-08-09】
			panel.setItemVisiable(_bo); //
		}
	}

	public void setVisiable(String[] _itemkeys, boolean _bo) {
		for (int i = 0; i < _itemkeys.length; i++) {
			getCompentByKey(_itemkeys[i]).setItemVisiable(_bo); //
		}
	}

	public Object getCompentObjectValue(String _key) {
		AbstractWLTCompentPanel[] compents = (AbstractWLTCompentPanel[]) v_compents.toArray(new AbstractWLTCompentPanel[0]);
		for (int i = 0; i < compents.length; i++) {
			if (compents[i].getItemKey().equalsIgnoreCase(_key)) {
				return compents[i].getObject(); //
			}
		}
		return null;
	}

	public Object getValueAt(String _key) {
		return getCompentObjectValue(_key);
	}

	/**
	 * 注册事件
	 * 
	 * @param _listener
	 */
	public void addBillQueryEditListener(BillQueryEditListener _listener) {
		v_listeners.add(_listener); //
	}

	private synchronized void onChangedAndFocusNext(String _itemkey) {
		onChanged(_itemkey);
		AbstractWLTCompentPanel actionpanel = this.getNextCompent(_itemkey); //
		// 光标跳到下一个!
		if (actionpanel != null) {
			actionpanel.focus(); //
		}
	}

	public void setQuickSQLCreater(BillQueryQuickSQLCreaterIFC quickSQLCreater) {
		this.quickSQLCreater = quickSQLCreater;
	}

	public void showOrHidenComplexQuery() {
		queryPanel.removeAll();
		JPanel jp = null;
		if (complexquery) {
			jp = getQueryPanel(1);
			queryPanel.add(jp);
			complexquery = false;
			if (btn_complexquery != null) {
				btn_complexquery.setText("显示高级查询");
			}
			if (menuItem_commquery != null) {
				menuItem_commquery.setText("通用查询");
			}
		} else {
			jp = getQueryPanel(2);
			queryPanel.add(jp);
			complexquery = true;
			if (btn_complexquery != null) {
				btn_complexquery.setText("隐藏高级查询");
			}
			if (menuItem_commquery != null) {
				menuItem_commquery.setText("快速查询");
			}
		}
		resetAllHideQueryCompent();
		//袁江晓20130408更改，主要解决郝明提出的显示高级查询后宽度变小或之类的情况
		double height = (jp.getPreferredSize().getHeight() >= btnpanel.getPreferredSize().getHeight() ? jp.getPreferredSize().getHeight() : btnpanel.getPreferredSize().getHeight());
		double weight = (jp.getPreferredSize().getWidth() >= btnpanel.getPreferredSize().getWidth() ? jp.getPreferredSize().getWidth() : btnpanel.getPreferredSize().getWidth());
		queryPanel.setPreferredSize(new Dimension((int) weight, (int) height));

		queryPanel.updateUI();
	}

	private TBUtil getTBUtil() {
		if (this.tbUtil != null) {
			return tbUtil; //
		}
		tbUtil = new TBUtil(); //
		return tbUtil; //
	}
}
