/**************************************************************************
 * $RCSfile: RefDialog_BigArea.java,v $  $Revision: 1.38 $  $Date: 2012/11/26 02:59:50 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import cn.com.infostrategy.bs.workflow.JepFormulaParseAtWorkFlow;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.jepfunctions.JepFormulaParse;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTextArea;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedEvent;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.JepFormulaParseAtUI;
import cn.com.infostrategy.ui.mdata.MultiStyleTextPanel;
import cn.com.infostrategy.ui.mdata.formatcomp.FormatEventBindFormulaParse;

/**
 * 大文本框对话框
 * 
 * @author Administrator
 * 
 */
public class RefDialog_BigArea extends AbstractRefDialog implements ActionListener, BillListMouseDoubleClickedListener {

	private static final long serialVersionUID = -2855052189366290591L;
	private WLTButton btn_clear, btn_confirm, btn_reset, btn_cancel, btn_help; //
	private MultiStyleTextPanel textPane = null;
	private JTabbedPane jtp_detail = null;
	private BillListPanel billList_sysFormula = null; //
	private BillListPanel billList_sysCustRef = null; //系统自定义参照

	private String[] str_keyWrod1 = new String[] { "select", "from", "where", "like", "and", "or", "order", "group" };
	private String[] str_keyWrod2 = new String[] { "getCommUC", "(", ")", ",", ":", "+", "-", "*", "/" }; //"\"", ";",
	private RefItemVO returnRefItemVO = null; //返回的ItemVO...
	private CommUCDefineVO uCDfVO; //

	public RefDialog_BigArea(Container _parent, String _title, RefItemVO value, BillPanel panel, CommUCDefineVO _uCDfVO) {
		super(_parent, _title, value, panel);
		this.uCDfVO = _uCDfVO; //
	}

	/**
	 * 初始化面板
	 */
	public void initialize() {
		this.getContentPane().setLayout(new BorderLayout());
		textPane = new MultiStyleTextPanel();
		textPane.addKeyWordStyle(str_keyWrod1, Color.BLUE, false); //
		textPane.addKeyWordStyle(getAllBtnType(), Color.BLUE, false); //
		textPane.addKeyWordStyle(getAllFunctionName(), Color.RED, false); //
		textPane.addKeyWordStyle(str_keyWrod2, Color.RED, false); //
		if (getInitRefItemVO() != null) {
			textPane.setText(getInitRefItemVO().getId());
		}

		JScrollPane scrollPane = new JScrollPane(textPane);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		jtp_detail = getDetailPane(); //
		WLTSplitPane jsp_con = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, jtp_detail);
		jsp_con.setDividerLocation(175);
		jsp_con.setDividerSize(15);
		//jsp_con.setDividerSize(5);
		//		jsp_con.setOneTouchExpandable(true);//郝明于2013-05-13把此行注释掉，不知道为啥执行此代码，同界面的参照，下拉框的按钮会重绘背景。很丑。

		btn_confirm = new WLTButton("确定"); //
		btn_reset = new WLTButton("清空"); //
		btn_cancel = new WLTButton("取消"); //
		btn_confirm.addActionListener(this);
		btn_reset.addActionListener(this);
		btn_cancel.addActionListener(this);

		JPanel panel_south = new JPanel();
		panel_south.setLayout(new FlowLayout());
		panel_south.add(btn_confirm);
		panel_south.add(btn_reset);
		panel_south.add(btn_cancel);
		setFocusable(true);
		this.getContentPane().add(jsp_con, BorderLayout.CENTER); //
		this.getContentPane().add(panel_south, BorderLayout.SOUTH); //
		this.getContentPane().add(getNorthPanel(), BorderLayout.NORTH); //
		textPane.requestFocus();
	}

	/**
	 * 上面一排按钮!!!
	 * @return
	 */
	private JPanel getNorthPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3)); //
		if (this.uCDfVO != null) { //如果有说明!!
			String str_needViewBtns = uCDfVO.getConfValue("显示的控件按钮", ""); //
			if (!str_needViewBtns.equals("")) { //如果不为空!
				String[] str_btntexts = getAllBtnType(); //取得所有按钮钮类型
				if (str_needViewBtns.equals("所有")) { //加入所有按钮!
					for (int i = 0; i < str_btntexts.length; i++) { //循环所有按钮
						WLTButton btn = new WLTButton(str_btntexts[i]); //
						btn.addActionListener(this); //
						panel.add(btn); //
					}
				} else { //只有一部分按钮!!
					String[] str_items = new TBUtil().split(str_needViewBtns, ";"); //
					HashSet hst = new HashSet(); //
					for (int i = 0; i < str_items.length; i++) {
						hst.add(str_items[i]); //
					}
					for (int i = 0; i < str_btntexts.length; i++) {
						if (hst.contains(str_btntexts[i])) {
							WLTButton btn = new WLTButton(str_btntexts[i]); //
							btn.addActionListener(this); //
							panel.add(btn); //
						}
					}
				}
				btn_clear = new WLTButton("清空"); //
				btn_clear.addActionListener(this);
				btn_help = new WLTButton("帮助"); //
				btn_help.addActionListener(this); //
				panel.add(btn_clear); //
				panel.add(btn_help); //
			}
			if (str_needViewBtns.equals("所有")) {
				panel.setPreferredSize(new Dimension(-1, 55)); //
			} else {
				panel.setPreferredSize(new Dimension(-1, 30)); //
			}
		}
		return panel;
	}

	/**
	 * 获取所有的公式应该是最全的了
	 * @return
	 */
	private String[] getAllFunctionName() {
		HashMap rtnMap = new HashMap();
		try {
			JepFormulaParseAtWorkFlow allWorkFlowFunctionName = new JepFormulaParseAtWorkFlow(null, null, null, null); // 工作流的公式
			FormatEventBindFormulaParse allFormatEFunctionName = new FormatEventBindFormulaParse(null); // 注册样板的公式？
			JepFormulaParseAtUI allUiFunctionName = new JepFormulaParseAtUI(null); // 
			JepFormulaParseAtUI allUiFunctionName2 = new JepFormulaParseAtUI(null, null); // 
			JepFormulaParseAtUI allUiFunctionName3 = new JepFormulaParseAtUI(null, null, null, null, null, null); // 
			JepFormulaParseAtUI allUiFunctionName4 = new JepFormulaParseAtUI(null, 0, 0); // 
			HashMap allBsFunctionName = UIUtil.commMethod("cn.com.infostrategy.bs.mdata.MetaDataBSUtil", "getJepBsFunctionName", new HashMap()); // 
			String[] allWFFcName = (String[]) allWorkFlowFunctionName.getParser().getFunctionTable().keySet().toArray(new String[0]);
			if (allWFFcName != null && allWFFcName.length > 0) {
				for (int i = 0; i < allWFFcName.length; i++) {
					rtnMap.put(allWFFcName[i], "");
				}
			}

			String[] allFBFcName = (String[]) allFormatEFunctionName.getParser().getFunctionTable().keySet().toArray(new String[0]);
			if (allFBFcName != null && allFBFcName.length > 0) {
				for (int i = 0; i < allFBFcName.length; i++) {
					rtnMap.put(allFBFcName[i], "");
				}
			}

			String[] allUIFcName = (String[]) allUiFunctionName.getParser().getFunctionTable().keySet().toArray(new String[0]);
			if (allUIFcName != null && allUIFcName.length > 0) {
				for (int i = 0; i < allUIFcName.length; i++) {
					rtnMap.put(allUIFcName[i], "");
				}
			}

			String[] allUIFcName2 = (String[]) allUiFunctionName2.getParser().getFunctionTable().keySet().toArray(new String[0]);
			if (allUIFcName2 != null && allUIFcName2.length > 0) {
				for (int i = 0; i < allUIFcName2.length; i++) {
					rtnMap.put(allUIFcName2[i], "");
				}
			}

			String[] allUIFcName3 = (String[]) allUiFunctionName3.getParser().getFunctionTable().keySet().toArray(new String[0]);
			if (allUIFcName3 != null && allUIFcName3.length > 0) {
				for (int i = 0; i < allUIFcName3.length; i++) {
					rtnMap.put(allUIFcName3[i], "");
				}
			}

			String[] allUIFcName4 = (String[]) allUiFunctionName4.getParser().getFunctionTable().keySet().toArray(new String[0]);
			if (allUIFcName4 != null && allUIFcName4.length > 0) {
				for (int i = 0; i < allUIFcName4.length; i++) {
					rtnMap.put(allUIFcName4[i], "");
				}
			}

			String[] allBSFcName = (String[]) allBsFunctionName.get("allBsFunctionName");
			if (allBSFcName != null && allBSFcName.length > 0) {
				for (int i = 0; i < allBSFcName.length; i++) {
					rtnMap.put(allBSFcName[i], "");
				}
			}

			String[] allBSFcName2 = (String[]) allBsFunctionName.get("allBsFunctionName2");
			if (allBSFcName2 != null && allBSFcName2.length > 0) {
				for (int i = 0; i < allBSFcName2.length; i++) {
					rtnMap.put(allBSFcName2[i], "");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (String[]) rtnMap.keySet().toArray(new String[0]);
	}

	/**
	 * 获得下边的页签面板
	 * @return
	 */
	private JTabbedPane getDetailPane() {
		JTabbedPane pane = new JTabbedPane(); //
		pane.setFocusable(false); //

		// 系统公式初始化区域
		billList_sysFormula = new BillListPanel(new DefaultTMO("系统公式", new String[][] { { "公式名", "200" }, { "说明", "250" }, { "示例", "200" } }));
		billList_sysFormula.putSolidValue(getExpression());
		billList_sysFormula.addBillListMouseDoubleClickedListener(this); //
		pane.addTab("系统公式", billList_sysFormula);

		//系统自定义参照
		billList_sysCustRef = new BillListPanel(new DefaultTMO("系统自定义参照", new String[][] { { "参照名称", "100" }, { "参照定义", "320" }, { "参照说明", "400" } }));
		String[][] str_syscustref = new String[][] { //
		{ "机构选择", "getCommUC(\"自定义参照\",\"自定义类名\",\"机构选择\",\"参数\",\"是否过滤权限=Y;权限过滤模式=3;是否多选=Y;是否复杂选择=N;是否扁平选择=N\");", "实现类:cn.com.infostrategy.ui.sysapp.refdialog.CommonCorpDeptRefDialog,根据参数可以定义复杂设置,其中[是否权限过滤]表示是否要进行权限过滤,如果是N表示不过滤,即显示整个机构!!" }, //
				{ "登录人员直属机构", "getCommUC(\"自定义参照\",\"自定义类名\",\"登录人员直属机构\");", "实现类:cn.com.infostrategy.ui.sysapp.corpdept.LoginUserDirtDeptRefDialog,登录人员直属机构路径!" }, //
				{ "万能日期范围选择", "getCommUC(\"自定义参照\",\"自定义类名\",\"万能日期范围选择\",\"参数\",\"年;季;月;天;日历\");", "实现类:cn.com.infostrategy.ui.sysapp.refdialog.CommonDateTimeRefDialog,可以指定年月日" }, //
				{ "静态与动态脚本定义", "getCommUC(\"自定义参照\",\"自定义类名\",\"静态与动态脚本定义\");", "实现类:cn.com.infostrategy.ui.sysapp.runtime.RunTimeActionEditRefDialog,第二个参数是实现拦截器的基类" }, //
		};
		billList_sysCustRef.putSolidValue(str_syscustref); //
		billList_sysCustRef.addBillListMouseDoubleClickedListener(this); //
		if (this.uCDfVO != null && uCDfVO.getConfValue("显示的控件按钮", "").equals("所有")) {
			pane.addTab("系统自定义参照", billList_sysCustRef);
			pane.addTab("所有注册参照", getRegisterRefPanel());
			pane.addTab("所有注册样板", getRegFormatRef());
		}
		return pane;
	}

	public void onMouseDoubleClicked(BillListMouseDoubleClickedEvent _event) {
		if (_event.getSource() == billList_sysCustRef) {
			textPane.inputText(_event.getCurrSelectedVO().getStringValue(1)); //
		} else {
			textPane.inputText(_event.getCurrSelectedVO().getStringValue(0)); //
		}
	}

	/**
	 * 注册参照
	 * @return
	 */
	private BillListPanel getRegisterRefPanel() {
		BillListPanel billListPanel_regref = new BillListPanel("pub_refregister_CODE1"); //
		billListPanel_regref.getQuickQueryPanel().setVisible(true); //
		billListPanel_regref.setItemEditable(false); //
		return billListPanel_regref; //
	}

	/**
	 * 注册样板
	 * @return
	 */
	private BillListPanel getRegFormatRef() {
		BillListPanel billListPanel_regref = new BillListPanel("pub_regformatpanel_CODE1"); //
		billListPanel_regref.getQuickQueryPanel().setVisible(true); //
		billListPanel_regref.setItemEditable(false); //
		return billListPanel_regref; //
	}

	/**
	 * 获得系统公式
	 * 
	 * @return
	 */
	private String[][] getExpression() {
		Vector vec_function = JepFormulaParse.getFunctionDetail();
		String[][] str_values = new String[vec_function.size()][];
		for (int i = 0; i < str_values.length; i++) {
			str_values[i] = (String[]) vec_function.get(i);
		}
		return str_values;
	}

	/**
	 * 获得文本区域的内容
	 * 
	 * @return
	 */
	public String getInfo() {
		return this.textPane.getText();
	}

	private void onConfirm() {
		returnRefItemVO = new RefItemVO(textPane.getText(), null, textPane.getText()); //
		this.setCloseType(1);
		this.dispose(); //
	}

	private void onReset() {
		returnRefItemVO = null; //
		textPane.setText("");
		textPane.requestFocus();
	}

	private void onCancel() {
		this.setCloseType(2);
		this.dispose();
	}

	public RefItemVO getReturnRefItemVO() {
		return returnRefItemVO; //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_clear) {
			textPane.setText(""); //
		} else if (e.getSource() == btn_confirm) {
			onConfirm(); //
		} else if (e.getSource() == btn_reset) {
			onReset(); //
		} else if (e.getSource() == btn_cancel) {
			onCancel(); //
		} else if (e.getSource() == btn_help) {
			onHelp(); //
		} else { //上面的各个按钮!!
			String str_text = ((WLTButton) e.getSource()).getText(); //按钮名称!!
			String[] str_models = getBtnModels(str_text); //取得该控件定义是否分许多种模式??
			if (str_models.length > 0) { //如果控件定义有许多种模式,即弹出一个多页签,然后分成简单定义,复杂定义,所有定义,这一是为了开发人员可以顺便学习,二是大部分情况只要简单公式即可!搞了一堆,许多人还不明白!
				JTabbedPane tabb = new JTabbedPane(); //
				tabb.setOpaque(false); //

				String[] str_formula = new String[str_models.length + 1]; //
				String[] str_formula_help = new String[str_models.length + 1]; //
				for (int i = 0; i < str_formula.length; i++) { //遍历
					if (i == str_formula.length - 1) {
						str_formula[i] = getDefineFormula(str_text, false, getOneBtnTypeInfoMap(), null); //
						str_formula_help[i] = getDefineFormula(str_text, true, getOneBtnTypeInfoMap(), null); //
					} else {
						str_formula[i] = getDefineFormula(str_text, false, getOneBtnTypeInfoMap(), str_models[i]); //
						str_formula_help[i] = getDefineFormula(str_text, true, getOneBtnTypeInfoMap(), str_models[i]); //
					}
					JTextArea textArea = new WLTTextArea(str_formula_help[i]); //
					textArea.setLineWrap(false); //
					textArea.setEditable(false); //
					textArea.setOpaque(false); //
					JScrollPane scroll = new JScrollPane(textArea); //
					if (i == str_formula.length - 1) {
						if (getOneBtnTypeInfoMap().containsKey(str_text)) {
							tabb.addTab("【所有参数】", scroll);
						}
					} else {
						tabb.addTab(str_models[i], scroll);
					}
				}
				BillDialog dialog = new BillDialog(this, str_text, 600, 400); //
				dialog.addConfirmButtonPanel(); //
				dialog.getContentPane().add(tabb, BorderLayout.CENTER); ////
				dialog.setVisible(true); //
				if (dialog.getCloseType() == BillDialog.CONFIRM) {
					int li_pos = tabb.getSelectedIndex(); //看选中的是第几个页签??
					textPane.inputText(str_formula[li_pos]); //
				}
			} else { //如果没有定义按钮的公式定义的模式,则直接
				String str_formula = getDefineFormula(str_text, false, null); //不带公式的说明!
				textPane.inputText(str_formula); //
			}
		}
	}

	private void onHelp() {
		StringBuilder sb_text = new StringBuilder();
		sb_text.append("新的控件定义统一使用getCommUC()公式,但以前的公式仍然能用!主要核心代码入口介绍:\n"); //
		sb_text.append("卡片中方法BillCardPanel.getMainPanel(),其中非参照的控件(即不需要再点一次弹出新窗口),分别是CardCPanel_为前辍的一批类,而参照的核心逻辑入口处是UIRefPanel.onButtonClicked()方法处!\n"); //
		sb_text.append("列表中方法BillListPanel.getTableColumns(),即每个控件分别有一个对应的渲染器(Render)与编辑器(Editor),但所有参照具体的实现最后又会转调到RefDialog_为前辍的一批类,即与卡片中调用的是一个类!\n\n"); //

		HashMap infoMap = getOneBtnTypeInfoMap(); //
		String[] str_btntypes = getAllBtnType(); //
		for (int i = 0; i < str_btntypes.length; i++) {
			sb_text.append(getDefineFormula(str_btntypes[i], true, infoMap)); //取得该控件的定义说明!
			sb_text.append("\n\n"); //
		}
		MessageBox.showTextArea(this, "所有控件的定义说明", sb_text.toString(), 700, 600); //
	}

	//取得公式定义!!
	private String getDefineFormula(String _type, boolean _isHelp, HashMap _map) {
		return getDefineFormula(_type, _isHelp, _map, null); //
	}

	/**
	 * 取得某个控件的定义公式!!!
	 * @param _type
	 * @return
	 */
	private String getDefineFormula(String _type, boolean _isHelp, HashMap _map, String _model) {
		StringBuilder sb_text = new StringBuilder();
		if (_map.containsKey(_type)) {
			sb_text.append("getCommUC(\"" + _type + "\""); //
			sb_text.append(","); //
			if (_isHelp && _map != null && _map.get(_type) != null) {
				sb_text.append(" //" + _map.get(_type)); //
			}
			sb_text.append("\n"); //前面的逗号不能少!!
		}
		String[][] str_allinfos = getAllBtnInfo(); //
		ArrayList al_this = new ArrayList(); //
		for (int i = 0; i < str_allinfos.length; i++) {
			if (str_allinfos[i][0].equals(_type) && !str_allinfos[i][1].trim().equals("")) { //如果找到,且有参数!
				al_this.add(str_allinfos[i]); //
			}
		}

		if (al_this.size() > 0) { //如果找到!
			ArrayList al_filter = new ArrayList(); //需要过滤
			for (int i = 0; i < al_this.size(); i++) { //遍历所有定义!!!
				String[] str_items = (String[]) al_this.get(i); //取得一行数据!!!!
				if (_model != null) { //如果指定义模式!!
					if (str_items.length >= 5 && str_items[4] != null && !str_items[4].equals("")) { //如果是
						String[] str_modelItems = TBUtil.getTBUtil().split(str_items[4], ";"); //到底有几个模式?
						if (TBUtil.getTBUtil().isExistInArray(_model, str_modelItems)) { //如果我在这个模式中!
							al_filter.add(str_items); //
						}
					}
				} else { //如果没有指定模式!
					al_filter.add(str_items); //
				}
			}
			for (int i = 0; i < al_filter.size(); i++) { //遍历所有定义!!!
				String[] str_items = (String[]) al_filter.get(i); //
				if (_map.containsKey(_type)) {

					sb_text.append("\"" + str_items[1] + "\",\"" + str_items[2] + "\""); //
					if (i != al_filter.size() - 1) {
						sb_text.append(","); //如果不是最后一个,则加逗号!!!
					}
					if (_isHelp) {
						sb_text.append("  //" + str_items[3]); //
					}
				} else {
					sb_text.append(str_items[3]);
				}
				sb_text.append("\n"); //换行
			}
		}
		if (_map.containsKey(_type)) {
			sb_text.append(");");
		}
		return sb_text.toString(); //
	}

	//取得所有按钮类型!
	private String[] getAllBtnType() {
		String[][] str_uCBtns = getAllBtnInfo(); //
		LinkedHashSet hst = new LinkedHashSet(); //
		for (int i = 0; i < str_uCBtns.length; i++) {
			hst.add(str_uCBtns[i][0]); //
		}
		String[] str_btntexts = (String[]) hst.toArray(new String[0]); //
		return str_btntexts; //
	}

	//取得某一个按钮的模式!
	private String[] getBtnModels(String _btnType) {
		LinkedHashSet hst_model = new LinkedHashSet(); //
		String[][] str_allTypes = getAllBtnInfo();
		for (int i = 0; i < str_allTypes.length; i++) {
			if (str_allTypes[i][0].equals(_btnType)) { //如果是本类型!
				if (str_allTypes[i].length >= 5 && str_allTypes[i][4] != null && !str_allTypes[i][4].equals("")) {
					String[] str_items = TBUtil.getTBUtil().split(str_allTypes[i][4], ";"); //
					for (int j = 0; j < str_items.length; j++) {
						hst_model.add(str_items[j]); //加入!!!
					}
				}
			}
		}
		return (String[]) hst_model.toArray(new String[0]);
	}

	/**
	 * 取得某个控件的说明
	 * @return
	 */
	private HashMap getOneBtnTypeInfoMap() {//认为在此描述的为控件定义否则为纯说明文字
		HashMap infoMap = new HashMap(); //
		infoMap.put("下拉框", "对应的类是:CardCPanel_ComboBox"); //
		infoMap.put("勾选框", "对应的类是:CardCPanel_CheckBox"); //
		infoMap.put("按钮", "对应的类是:CardCPanel_Button"); //
		infoMap.put("数字框", "对应的类是:CardCPanel_TextField");
		infoMap.put("正则表达式控件", "对应的类是:CardCPanel_TextField");//张珍龙弄了正则表达式控件，没加入选择按钮【李春娟/2016-12-08】
		infoMap.put("参照", "对应的类是:RefDialog_Table"); //
		infoMap.put("树型参照", "对应的类是:RefDialog_Tree"); //
		infoMap.put("列表模板参照", "对应的类是:RefDialog_ListTemplet"); //
		infoMap.put("树型模板参照", "对应的类是:RefDialog_TreeTemplet"); //
		infoMap.put("自定义参照", "写一个类继承于AbstractRefDialog"); //
		infoMap.put("注册样板参照", "对应的类是:RefDialog_RegFormat"); //
		infoMap.put("注册参照", "没有对应的类,只是从表pub_refregister中再查询一下,找到实际的参照类型"); //
		infoMap.put("引用子表", "对应的类是:CardCPanel_ChildTable"); //
		infoMap.put("导入子表", "对应的类是:CardCPanel_ChildTableImport"); //
		infoMap.put("文件选择框", "对应的类是:CardCPanel_FileDeal"); //
		infoMap.put("Excel控件", "对应的类是:RefDialog_Excel"); //
		infoMap.put("Office控件", "对应的类是:RefDialog_Office"); //
		infoMap.put("日历", "对应的类是:RefDialog_Date"); //
		infoMap.put("自定义控件", "");
		infoMap.put("多选参照", "");
		return infoMap; //
	}

	private String[][] getAllBtnInfo() {
		String[][] str_info = new String[][] { //
				//******下拉框
				{ "下拉框", "直接值", "男;女;", "直接写一批数值,不执行SQL,性能更高", "直接值模式;" }, //
				{ "下拉框", "SQL语句", "select id,code,name from pub_comboboxdict where type = '类型' order by seq", "通过一个SQL创建", "SQL模式" }, //常用的是查pub_comboboxdict表中的数据[郝明2012-03-01]，发现开发人员总忘了写排序字段，故在这里加上【李春娟/2012-05-02】
				{ "下拉框", "文本框是否可编辑", "N", "既可以选择也可以直接录入", "直接值模式;SQL模式" }, //
				{ "下拉框", "查询时是否多选", "N", "下拉框在查询时可以多选(许多客户提过),默认是N", "直接值模式;SQL模式" }, //

				{ "勾选框", "是否勾选框在前面", "N", "web中勾选框常在标签前面", "普通模式" },
				{ "按钮", "显示文字", "请输入显示文字", "删除此属性默认为字段名", "普通模式" },
				{ "按钮", "显示图片", "", "按钮图标的图片名称", "普通模式" },
				{ "按钮", "提示", "", "按钮提示", "普通模式" },
				{ "按钮", "点击事件", "", "实现WLTActionListener接口的类路径", "普通模式" },
				//数字框
				{ "数字框", "类型", "纯数字", "只能输入0-9", "纯数字" },//sunfujun/20121126/增加几种类型的简单验证，复杂验证应该在验证公式中提交的时候，卡片应该有这个功能目前还没有
				{ "数字框", "类型", "小数", "小数不解释，简单验证", "小数" },
				{ "数字框", "类型", "整数", "整数不解释，简单验证", "整数" },
				{ "数字框", "类型", "电话", "电话，简单验证", "电话" },
				{ "数字框", "类型", "", "原来的逻辑", "其他" },
				
				//正则表达式控件
				{ "正则表达式控件", "类型", "100|([0-9]|[1-9][0-9])(\\.\\d*)?", "", "100以内小数" },

				//参照
				{ "参照", "SQL语句", "select id,code,name from pub_role", "直接写一个SQL,不得少于3列,字段以$号结尾的自动隐藏,如果实在只有两列,则写有个固定值的冗余列,比如select 1 id$,2 code$,name from pub_user", "普通模式" }, //

				//树型参照
				{ "树型参照", "SQL语句", "select id 主键$,code 编码,name 名称,parentmenuid 父键$ from pub_menu", "直接写一个SQL,不得少于3列,字段以$号结尾的自动隐藏", "普通模式" }, //
				{ "树型参照", "PKField", "主键$", "暂无帮助", "普通模式" }, //
				{ "树型参照", "ParentPKField", "父键$", "暂无帮助", "普通模式" }, //
				{ "树型参照", "文本是否换行", "N", "参照文本框多行有滚动条", "普通模式" }, //

				//多选参照
				{ "多选参照", "SQL语句", "select id,code,name from pub_comboboxdict where type='学历' order by seq", "直接写一个SQL,不得少于3列,字段以$号结尾的自动隐藏", "普通模式" }, //

				//列表模板参照
				{ "列表模板参照", "模板编码", "PUB_ROLE_CODE_1", "必须指定一个模板编码", "简单模式;普通模式" }, //
				{ "列表模板参照", "ID字段", "id", "默认值是id", "简单模式;普通模式" }, //
				{ "列表模板参照", "NAME字段", "name", "默认值是name", "简单模式;普通模式" }, //
				{ "列表模板参照", "附加SQL条件", "type='总行'", "打开窗口后执行的过滤条件", "普通模式" }, //
				{ "列表模板参照", "可以多选", "N", "列表窗口可以多选返回", "普通模式" }, //
				{ "列表模板参照", "自动查询", "N", "弹出窗口出自动查询数据", "普通模式" }, //
				{ "列表模板参照", "文本是否换行", "N", "参照文本框多行有滚动条", "普通模式" }, //

				//树型模板参照
				{ "树型模板参照", "模板编码", "pub_corp_dept_CODE1", "模板名称(必输项)", "简单模式;普通模式;复杂模式" }, //
				{ "树型模板参照", "ID字段", "id", "返回的存储字段名,一般都是id", "简单模式;普通模式;复杂模式" }, //
				{ "树型模板参照", "NAME字段", "name", "返回的显示名称,一般都是name", "简单模式;普通模式;复杂模式" }, //
				{ "树型模板参照", "附加SQL条件", "corptype='分行'", "打开时会根据该过滤条件查询", "普通模式;复杂模式" }, //
				{ "树型模板参照", "数据权限策略", "工作数据_查询", "使用哪个数据权限策略", "复杂模式" }, //
				{ "树型模板参照", "数据权限策略映射", "过滤方式=机构过滤;机构字段名=id;", "数据权限策略映射方式", "复杂模式" }, //
				{ "树型模板参照", "只留前几层", "0", "有时只想看目录,比如只想到部门而不关心科室", "复杂模式" }, //
				{ "树型模板参照", "展开", "N", "是否展开所有结点", "复杂模式" }, //
				{ "树型模板参照", "展开某个结点1", "", "根据主键展开某个结点,主键值为100,可配置多个结点,如下", "复杂模式" }, //这里为什么这样设计，而不是用id串，如“100;125”？需要徐老师补充！
				{ "树型模板参照", "展开某个结点2", "", "以'展开某个结点'打头的key,值为主键值.注意后缀不要一样,否则存到hashmap中会被覆盖", "复杂模式" }, //以前设置的“100”、“125”，这里默认设为空串，因为大部分情况下不需要设置展开结点，并且如果开发人员忘记删除该参数时，树就会展开【李春娟/2012-06-06】
				{ "树型模板参照", "可以多选", "N", "树结点是否可以多选,默认不可以", "普通模式;复杂模式" }, //李春娟增加，升级平台给落掉了些参数
				{ "树型模板参照", "只能选叶子", "N", "有时必须选择叶子结点,不允许选择目录返回", "普通模式;复杂模式" }, //
				{ "树型模板参照", "返回路径链名", "N", "将结点的路径拼起来返回", "复杂模式" }, //
				{ "树型模板参照", "是否截掉第一层", "Y", "是否截掉第一层,默认截取", "复杂模式" }, //
				{ "树型模板参照", "是否连动勾选", "Y", "勾选一个结点时同时连动把其所有子结点也勾上", "复杂模式" }, //
				{ "树型模板参照", "是否显示按钮面板", "N", "有时在关联树型结构数据时也可以对该数据进行维护", "复杂模式" }, //
				{ "树型模板参照", "是否选中已选择的条目", "N", "重新打开时将之前选择的条目重新选上(如果节点数量大于2000此参数自动失效!)", "复杂模式" }, //袁江晓20120904添加
				{ "树型模板参照", "文本框是否可编辑", "N", "树形模板参照单选时可以配置文本框可编辑，直接输入值进行检索选择", "普通模式;复杂模式" }, //袁江晓20120904添加
				{ "树型模板参照", "数据为空时的提示", "暂无数据", "当模板查询到的数据为空时的提示", "普通模式;复杂模式" }, //袁江晓20130426添加  杨庆提出的需求
				{ "树型模板参照", "文本是否换行", "N", "参照文本框多行有滚动条", "普通模式" }, //

				//自定义参照
				{ "自定义参照", "自定义类名", "cn.com.pushworld.TestRefDialog", "类名,将反射调用,必须继承cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog", "简单模式;普通模式" }, //
				{ "自定义参照", "参数", "2", "可以有多个参数,分别用参数1,参数2...N个参数需匹配构造方法中4+N个变量,\r\n\t如:public TestRefDialog(Container _parent, String _title, RfItemVO refItemVO, BillPanel panel,String 参数1,String 参数2)", "普通模式" }, //

				//注册样板参照
				{ "注册样板参照", "注册编码", "2", "", "普通模式" }, //
				{ "注册样板参照", "数据生成器", "", "", "普通模式" }, //
				{ "注册样板参照", "风格", "2", "", "普通模式" }, //

				//注册参照
				{ "注册参照", "注册名称", "RegCorp001", "", "普通模式" }, //

				//引用子表
				{ "引用子表", "模板编码", "cmp_cmpevent_CODE1", "子表的模板编码", "普通模式;复杂模式" }, //
				{ "引用子表", "主键字段名", "id", "本表(主表)中的主键字希名", "普通模式;复杂模式" }, //
				{ "引用子表", "关联外键字段名", "parentid", "子表中的哪个字段与上面定义的主表字段关联,在新增子表记录时,会将该字段默认值设置成主表中那个字段的值,形成关联关系", "普通模式;复杂模式" }, //
				{ "引用子表", "拦截器", "", "在UI端实现ChildTableCommUIIntercept接口。", "普通模式;复杂模式" }, //
				{ "引用子表", "取值", "", "在引用子表新增或修改时获得主表字段值，格式为“子表字段1=主表字段1;子表字段2=主表字段2”,如“checkdept=finddept;userid=userid”", "复杂模式" }, //

				//导入子表
				{ "导入子表", "模板编码", "cmp_cmpevent_CODE1", "子表的模板编码", "普通模式" }, //
				{ "导入子表", "主键字段名", "id", "本表(主表)中的主键字希名", "普通模式" }, //
				{ "导入子表", "主键字段显示名", "name", "", "普通模式" }, //
				{ "导入子表", "关联外键字段名", "parentid", "子表中的哪个字段与上面定义的主表字段关联,在新增子表记录时,会将该字段默认值设置成主表中那个字段的值,形成关联关系", "普通模式" }, //
				{ "导入子表", "拦截器", "", "在UI端实现ChildTableCommUIIntercept接口。", "普通模式" }, //

				//文件选择框
				{ "文件选择框", "文件存储子路径", "/upload", "存储时系统会根据日期在所选目录中生成子文件夹来存储文件,默认是/upload", "普通模式" }, //
				{ "文件选择框", "文件编号生成器", "", "文件编号生成器的类名请继承AbstractRefFileNoCreate且放在UI端", "普通模式" }, //
				{ "文件选择框", "文件名是否转码", "Y", "", "普通模式" }, //
				{ "文件选择框", "拦截器", "", "在UI端搞一个类继承于抽象类AbstractRefFileIntercept,其中有方法可以在上传之前与上传后进行拦截!", "普通模式" }, //
				{ "文件选择框", "文件类型", "Word文档,doc,docx", "文件类型, 比如:Excle文档,xls,xlsx;Word文档,doc,docx;所有文件,*", "筛选模式" }, //增加"文件类型"参数, 可以对打开文件类型进行过滤 Gwang 2013/3/13

				//Excel控件
				{ "Excel控件", "模板编码", "风险矩阵", "Excel模板的编码", "普通模式" }, //
				{ "Excel控件", "初始化方法", "", "", "普通模式" }, //
				{ "Excel控件", "按钮自定义面板", "", "", "普通模式" }, //
				{ "Excel控件", "返回显示值", "", "", "普通模式" }, //
				{ "Excel控件", "返回隐藏值", "", "", "普通模式" }, //
				{ "Excel控件", "初始化sql", "", "", "普通模式" }, //

				//Office控件
				{ "Office控件", "文件类型", "doc", "有doc,xls,wps等几种", "简单模式;普通模式" }, //
				{ "Office控件", "模板文件名", "blank.doc", "即第一次创建时会自动加载一个模板,比如起草合同", "普通模式;复杂模式" }, //
				{ "Office控件", "书签生成器", "cn.com.infostrategy.ui.mdata.cardcomp.RefDialog_Office_BookMarkCreaterIFC", "必须定义一个类实现该接口", "复杂模式" }, //
				{ "Office控件", "控件厂家", "千航", "目前支持金格,千航两家", "复杂模式" }, //
				{ "Office控件", "是否显示导入按钮", "Y", "右边会多个加号的导入按钮", "普通模式;复杂模式" }, //
				{ "Office控件", "存储目录", "/officecompfile", "有时想存在指定的目录下,比如/heguifile", "普通模式;复杂模式" }, //
				{ "Office控件", "Office参照的打开方式", "IE", "点击文件名查看时，用IE还是Office打开，如果此处不设置则使用平台参数的设置", "复杂模式" }, //

				//日历控件
				{ "日历", "条件", "{}<={otheritem}", "可以执行公式，例如:{}>getCurrDate() and {}<getItemValue(date1) and <{data2} and {}+30>=2012-04-08，单独{}表示当前选择值可以不写", "普通模式" }, //
				{ "日历", "查询时日期类型", "年;季;月;天;日历", "可以是这五项的任意组合，前四项表示可选择的日期类型，最后一个[日历]表示可以用日历的方式选择两个日期作为区间段", "普通模式" }, //有时需要控制查询时选择的日期类型，比如只能选择季度，或年份等，故增加该参数【李春娟/2012-06-20】
				{ "日历", "查询时日期默认值类型", "年;季;月;天", "查询时默认已选择当前年或当前季度或当天", "普通模式" }, // 20130514 袁江晓 添加 主要解决查询时根据设置的类型设置默认值

				//自定义控件
				{ "自定义控件", "卡片中的类", "", "第一个参数为卡片时显示类的类名继承AbstractWLTCompentPanel,且有Pub_Templet_1_ItemVO.class, BillPanel.class 2个类型参数的构造函数", "普通模式" }, //
				{ "自定义控件", "列表渲染器", "", "第二个参数为列表渲染器类名,可以参照其他控件的写法", "普通模式" }, //
				{ "自定义控件", "列表编辑器", "", "第3个参数为列表编辑器类名，可以参照其他控件的写法", "普通模式" }, //
				//查询创建器
				{ "查询创建器", "1", "1", "\"id in (select parentid from PSBC_NOSTYLEFILE_ADD where FILE_ANDPEOPLE like '%{itemvalue}%')\" " + "\n {invalues}=当为分号分隔的值时此宏代码等价于getTBUtil().getInCondition(str_items) " + "\n {LikeOrSQL}=当为分号分隔的值时(a;b)此宏代码等价于like '%a%' or like '%b%' ", "自定义SQL" },
				{
						"查询创建器",
						"1",
						"1",
						"此处填写类的全路径,需要实现IBillQueryPanelSQLCreateIFC,且写在BS端。\n" + "public String getSQLByCondition(String key, String value,\n" + "HashMap itemValues, HashMap itemSQLs, String _wholesql)\n" + "throws Exception {\n" + "   RefItemVO refItemVO=(RefItemVO) itemValues.get(key + \"_obj\");	//获取选中值\n" + "   if(refItemVO==null){	//如果未选,则返回1=1,即查询全部数据\n" + "    return \"1=1\";\n" + "   }\n"
								+ "   String condition=refItemVO.getHashVO().getStringValue(\"querycondition\");	//得到转换好的SQL条件\n" + "   String beginCondition=new TBUtil().replaceAll(condition, \"{itemkey}\", \"train_begin_date\");	//将参数替换为实际字段\n" + "   String endCondition=new TBUtil().replaceAll(condition, \"{itemkey}\", \"train_end_date\");	//将参数替换为实际字段\n"
								+ "   String sql=beginCondition+\" or \"+endCondition;	//拼接SQL\n" + "   return sql;\n" + "}\n", "自定义类" }, { "封装html事件", "1", "", "常有点击列表html如合同名称,要求打开实际的合同文本的需求,\n只需要在合同名称字段的控件定义增加\"html连接key\"参数,\n如果没有控件定义的就使用getCommUC,\n例如:文本框可使用公式getCommUC(\"文本框\",\"html连接key\",\"附件字段key\")", "写法" }, };
		return str_info; //
	}

	public int getInitWidth() {
		return 920;
	}

	public int getInitHeight() {
		return 700;
	}

}
