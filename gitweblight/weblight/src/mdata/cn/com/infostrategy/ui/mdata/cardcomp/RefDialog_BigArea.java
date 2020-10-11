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
 * ���ı���Ի���
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
	private BillListPanel billList_sysCustRef = null; //ϵͳ�Զ������

	private String[] str_keyWrod1 = new String[] { "select", "from", "where", "like", "and", "or", "order", "group" };
	private String[] str_keyWrod2 = new String[] { "getCommUC", "(", ")", ",", ":", "+", "-", "*", "/" }; //"\"", ";",
	private RefItemVO returnRefItemVO = null; //���ص�ItemVO...
	private CommUCDefineVO uCDfVO; //

	public RefDialog_BigArea(Container _parent, String _title, RefItemVO value, BillPanel panel, CommUCDefineVO _uCDfVO) {
		super(_parent, _title, value, panel);
		this.uCDfVO = _uCDfVO; //
	}

	/**
	 * ��ʼ�����
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
		//		jsp_con.setOneTouchExpandable(true);//������2013-05-13�Ѵ���ע�͵�����֪��Ϊɶִ�д˴��룬ͬ����Ĳ��գ�������İ�ť���ػ汳�����ܳ�

		btn_confirm = new WLTButton("ȷ��"); //
		btn_reset = new WLTButton("���"); //
		btn_cancel = new WLTButton("ȡ��"); //
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
	 * ����һ�Ű�ť!!!
	 * @return
	 */
	private JPanel getNorthPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3)); //
		if (this.uCDfVO != null) { //�����˵��!!
			String str_needViewBtns = uCDfVO.getConfValue("��ʾ�Ŀؼ���ť", ""); //
			if (!str_needViewBtns.equals("")) { //�����Ϊ��!
				String[] str_btntexts = getAllBtnType(); //ȡ�����а�ťť����
				if (str_needViewBtns.equals("����")) { //�������а�ť!
					for (int i = 0; i < str_btntexts.length; i++) { //ѭ�����а�ť
						WLTButton btn = new WLTButton(str_btntexts[i]); //
						btn.addActionListener(this); //
						panel.add(btn); //
					}
				} else { //ֻ��һ���ְ�ť!!
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
				btn_clear = new WLTButton("���"); //
				btn_clear.addActionListener(this);
				btn_help = new WLTButton("����"); //
				btn_help.addActionListener(this); //
				panel.add(btn_clear); //
				panel.add(btn_help); //
			}
			if (str_needViewBtns.equals("����")) {
				panel.setPreferredSize(new Dimension(-1, 55)); //
			} else {
				panel.setPreferredSize(new Dimension(-1, 30)); //
			}
		}
		return panel;
	}

	/**
	 * ��ȡ���еĹ�ʽӦ������ȫ����
	 * @return
	 */
	private String[] getAllFunctionName() {
		HashMap rtnMap = new HashMap();
		try {
			JepFormulaParseAtWorkFlow allWorkFlowFunctionName = new JepFormulaParseAtWorkFlow(null, null, null, null); // �������Ĺ�ʽ
			FormatEventBindFormulaParse allFormatEFunctionName = new FormatEventBindFormulaParse(null); // ע������Ĺ�ʽ��
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
	 * ����±ߵ�ҳǩ���
	 * @return
	 */
	private JTabbedPane getDetailPane() {
		JTabbedPane pane = new JTabbedPane(); //
		pane.setFocusable(false); //

		// ϵͳ��ʽ��ʼ������
		billList_sysFormula = new BillListPanel(new DefaultTMO("ϵͳ��ʽ", new String[][] { { "��ʽ��", "200" }, { "˵��", "250" }, { "ʾ��", "200" } }));
		billList_sysFormula.putSolidValue(getExpression());
		billList_sysFormula.addBillListMouseDoubleClickedListener(this); //
		pane.addTab("ϵͳ��ʽ", billList_sysFormula);

		//ϵͳ�Զ������
		billList_sysCustRef = new BillListPanel(new DefaultTMO("ϵͳ�Զ������", new String[][] { { "��������", "100" }, { "���ն���", "320" }, { "����˵��", "400" } }));
		String[][] str_syscustref = new String[][] { //
		{ "����ѡ��", "getCommUC(\"�Զ������\",\"�Զ�������\",\"����ѡ��\",\"����\",\"�Ƿ����Ȩ��=Y;Ȩ�޹���ģʽ=3;�Ƿ��ѡ=Y;�Ƿ���ѡ��=N;�Ƿ��ƽѡ��=N\");", "ʵ����:cn.com.infostrategy.ui.sysapp.refdialog.CommonCorpDeptRefDialog,���ݲ������Զ��帴������,����[�Ƿ�Ȩ�޹���]��ʾ�Ƿ�Ҫ����Ȩ�޹���,�����N��ʾ������,����ʾ��������!!" }, //
				{ "��¼��Աֱ������", "getCommUC(\"�Զ������\",\"�Զ�������\",\"��¼��Աֱ������\");", "ʵ����:cn.com.infostrategy.ui.sysapp.corpdept.LoginUserDirtDeptRefDialog,��¼��Աֱ������·��!" }, //
				{ "�������ڷ�Χѡ��", "getCommUC(\"�Զ������\",\"�Զ�������\",\"�������ڷ�Χѡ��\",\"����\",\"��;��;��;��;����\");", "ʵ����:cn.com.infostrategy.ui.sysapp.refdialog.CommonDateTimeRefDialog,����ָ��������" }, //
				{ "��̬�붯̬�ű�����", "getCommUC(\"�Զ������\",\"�Զ�������\",\"��̬�붯̬�ű�����\");", "ʵ����:cn.com.infostrategy.ui.sysapp.runtime.RunTimeActionEditRefDialog,�ڶ���������ʵ���������Ļ���" }, //
		};
		billList_sysCustRef.putSolidValue(str_syscustref); //
		billList_sysCustRef.addBillListMouseDoubleClickedListener(this); //
		if (this.uCDfVO != null && uCDfVO.getConfValue("��ʾ�Ŀؼ���ť", "").equals("����")) {
			pane.addTab("ϵͳ�Զ������", billList_sysCustRef);
			pane.addTab("����ע�����", getRegisterRefPanel());
			pane.addTab("����ע������", getRegFormatRef());
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
	 * ע�����
	 * @return
	 */
	private BillListPanel getRegisterRefPanel() {
		BillListPanel billListPanel_regref = new BillListPanel("pub_refregister_CODE1"); //
		billListPanel_regref.getQuickQueryPanel().setVisible(true); //
		billListPanel_regref.setItemEditable(false); //
		return billListPanel_regref; //
	}

	/**
	 * ע������
	 * @return
	 */
	private BillListPanel getRegFormatRef() {
		BillListPanel billListPanel_regref = new BillListPanel("pub_regformatpanel_CODE1"); //
		billListPanel_regref.getQuickQueryPanel().setVisible(true); //
		billListPanel_regref.setItemEditable(false); //
		return billListPanel_regref; //
	}

	/**
	 * ���ϵͳ��ʽ
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
	 * ����ı����������
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
		} else { //����ĸ�����ť!!
			String str_text = ((WLTButton) e.getSource()).getText(); //��ť����!!
			String[] str_models = getBtnModels(str_text); //ȡ�øÿؼ������Ƿ�������ģʽ??
			if (str_models.length > 0) { //����ؼ������������ģʽ,������һ����ҳǩ,Ȼ��ֳɼ򵥶���,���Ӷ���,���ж���,��һ��Ϊ�˿�����Ա����˳��ѧϰ,���Ǵ󲿷����ֻҪ�򵥹�ʽ����!����һ��,����˻�������!
				JTabbedPane tabb = new JTabbedPane(); //
				tabb.setOpaque(false); //

				String[] str_formula = new String[str_models.length + 1]; //
				String[] str_formula_help = new String[str_models.length + 1]; //
				for (int i = 0; i < str_formula.length; i++) { //����
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
							tabb.addTab("�����в�����", scroll);
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
					int li_pos = tabb.getSelectedIndex(); //��ѡ�е��ǵڼ���ҳǩ??
					textPane.inputText(str_formula[li_pos]); //
				}
			} else { //���û�ж��尴ť�Ĺ�ʽ�����ģʽ,��ֱ��
				String str_formula = getDefineFormula(str_text, false, null); //������ʽ��˵��!
				textPane.inputText(str_formula); //
			}
		}
	}

	private void onHelp() {
		StringBuilder sb_text = new StringBuilder();
		sb_text.append("�µĿؼ�����ͳһʹ��getCommUC()��ʽ,����ǰ�Ĺ�ʽ��Ȼ����!��Ҫ���Ĵ�����ڽ���:\n"); //
		sb_text.append("��Ƭ�з���BillCardPanel.getMainPanel(),���зǲ��յĿؼ�(������Ҫ�ٵ�һ�ε����´���),�ֱ���CardCPanel_Ϊǰ꡵�һ����,�����յĺ����߼���ڴ���UIRefPanel.onButtonClicked()������!\n"); //
		sb_text.append("�б��з���BillListPanel.getTableColumns(),��ÿ���ؼ��ֱ���һ����Ӧ����Ⱦ��(Render)��༭��(Editor),�����в��վ����ʵ������ֻ�ת����RefDialog_Ϊǰ꡵�һ����,���뿨Ƭ�е��õ���һ����!\n\n"); //

		HashMap infoMap = getOneBtnTypeInfoMap(); //
		String[] str_btntypes = getAllBtnType(); //
		for (int i = 0; i < str_btntypes.length; i++) {
			sb_text.append(getDefineFormula(str_btntypes[i], true, infoMap)); //ȡ�øÿؼ��Ķ���˵��!
			sb_text.append("\n\n"); //
		}
		MessageBox.showTextArea(this, "���пؼ��Ķ���˵��", sb_text.toString(), 700, 600); //
	}

	//ȡ�ù�ʽ����!!
	private String getDefineFormula(String _type, boolean _isHelp, HashMap _map) {
		return getDefineFormula(_type, _isHelp, _map, null); //
	}

	/**
	 * ȡ��ĳ���ؼ��Ķ��幫ʽ!!!
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
			sb_text.append("\n"); //ǰ��Ķ��Ų�����!!
		}
		String[][] str_allinfos = getAllBtnInfo(); //
		ArrayList al_this = new ArrayList(); //
		for (int i = 0; i < str_allinfos.length; i++) {
			if (str_allinfos[i][0].equals(_type) && !str_allinfos[i][1].trim().equals("")) { //����ҵ�,���в���!
				al_this.add(str_allinfos[i]); //
			}
		}

		if (al_this.size() > 0) { //����ҵ�!
			ArrayList al_filter = new ArrayList(); //��Ҫ����
			for (int i = 0; i < al_this.size(); i++) { //�������ж���!!!
				String[] str_items = (String[]) al_this.get(i); //ȡ��һ������!!!!
				if (_model != null) { //���ָ����ģʽ!!
					if (str_items.length >= 5 && str_items[4] != null && !str_items[4].equals("")) { //�����
						String[] str_modelItems = TBUtil.getTBUtil().split(str_items[4], ";"); //�����м���ģʽ?
						if (TBUtil.getTBUtil().isExistInArray(_model, str_modelItems)) { //����������ģʽ��!
							al_filter.add(str_items); //
						}
					}
				} else { //���û��ָ��ģʽ!
					al_filter.add(str_items); //
				}
			}
			for (int i = 0; i < al_filter.size(); i++) { //�������ж���!!!
				String[] str_items = (String[]) al_filter.get(i); //
				if (_map.containsKey(_type)) {

					sb_text.append("\"" + str_items[1] + "\",\"" + str_items[2] + "\""); //
					if (i != al_filter.size() - 1) {
						sb_text.append(","); //����������һ��,��Ӷ���!!!
					}
					if (_isHelp) {
						sb_text.append("  //" + str_items[3]); //
					}
				} else {
					sb_text.append(str_items[3]);
				}
				sb_text.append("\n"); //����
			}
		}
		if (_map.containsKey(_type)) {
			sb_text.append(");");
		}
		return sb_text.toString(); //
	}

	//ȡ�����а�ť����!
	private String[] getAllBtnType() {
		String[][] str_uCBtns = getAllBtnInfo(); //
		LinkedHashSet hst = new LinkedHashSet(); //
		for (int i = 0; i < str_uCBtns.length; i++) {
			hst.add(str_uCBtns[i][0]); //
		}
		String[] str_btntexts = (String[]) hst.toArray(new String[0]); //
		return str_btntexts; //
	}

	//ȡ��ĳһ����ť��ģʽ!
	private String[] getBtnModels(String _btnType) {
		LinkedHashSet hst_model = new LinkedHashSet(); //
		String[][] str_allTypes = getAllBtnInfo();
		for (int i = 0; i < str_allTypes.length; i++) {
			if (str_allTypes[i][0].equals(_btnType)) { //����Ǳ�����!
				if (str_allTypes[i].length >= 5 && str_allTypes[i][4] != null && !str_allTypes[i][4].equals("")) {
					String[] str_items = TBUtil.getTBUtil().split(str_allTypes[i][4], ";"); //
					for (int j = 0; j < str_items.length; j++) {
						hst_model.add(str_items[j]); //����!!!
					}
				}
			}
		}
		return (String[]) hst_model.toArray(new String[0]);
	}

	/**
	 * ȡ��ĳ���ؼ���˵��
	 * @return
	 */
	private HashMap getOneBtnTypeInfoMap() {//��Ϊ�ڴ�������Ϊ�ؼ��������Ϊ��˵������
		HashMap infoMap = new HashMap(); //
		infoMap.put("������", "��Ӧ������:CardCPanel_ComboBox"); //
		infoMap.put("��ѡ��", "��Ӧ������:CardCPanel_CheckBox"); //
		infoMap.put("��ť", "��Ӧ������:CardCPanel_Button"); //
		infoMap.put("���ֿ�", "��Ӧ������:CardCPanel_TextField");
		infoMap.put("������ʽ�ؼ�", "��Ӧ������:CardCPanel_TextField");//������Ū��������ʽ�ؼ���û����ѡ��ť�����/2016-12-08��
		infoMap.put("����", "��Ӧ������:RefDialog_Table"); //
		infoMap.put("���Ͳ���", "��Ӧ������:RefDialog_Tree"); //
		infoMap.put("�б�ģ�����", "��Ӧ������:RefDialog_ListTemplet"); //
		infoMap.put("����ģ�����", "��Ӧ������:RefDialog_TreeTemplet"); //
		infoMap.put("�Զ������", "дһ����̳���AbstractRefDialog"); //
		infoMap.put("ע���������", "��Ӧ������:RefDialog_RegFormat"); //
		infoMap.put("ע�����", "û�ж�Ӧ����,ֻ�Ǵӱ�pub_refregister���ٲ�ѯһ��,�ҵ�ʵ�ʵĲ�������"); //
		infoMap.put("�����ӱ�", "��Ӧ������:CardCPanel_ChildTable"); //
		infoMap.put("�����ӱ�", "��Ӧ������:CardCPanel_ChildTableImport"); //
		infoMap.put("�ļ�ѡ���", "��Ӧ������:CardCPanel_FileDeal"); //
		infoMap.put("Excel�ؼ�", "��Ӧ������:RefDialog_Excel"); //
		infoMap.put("Office�ؼ�", "��Ӧ������:RefDialog_Office"); //
		infoMap.put("����", "��Ӧ������:RefDialog_Date"); //
		infoMap.put("�Զ���ؼ�", "");
		infoMap.put("��ѡ����", "");
		return infoMap; //
	}

	private String[][] getAllBtnInfo() {
		String[][] str_info = new String[][] { //
				//******������
				{ "������", "ֱ��ֵ", "��;Ů;", "ֱ��дһ����ֵ,��ִ��SQL,���ܸ���", "ֱ��ֵģʽ;" }, //
				{ "������", "SQL���", "select id,code,name from pub_comboboxdict where type = '����' order by seq", "ͨ��һ��SQL����", "SQLģʽ" }, //���õ��ǲ�pub_comboboxdict���е�����[����2012-03-01]�����ֿ�����Ա������д�����ֶΣ�����������ϡ����/2012-05-02��
				{ "������", "�ı����Ƿ�ɱ༭", "N", "�ȿ���ѡ��Ҳ����ֱ��¼��", "ֱ��ֵģʽ;SQLģʽ" }, //
				{ "������", "��ѯʱ�Ƿ��ѡ", "N", "�������ڲ�ѯʱ���Զ�ѡ(���ͻ����),Ĭ����N", "ֱ��ֵģʽ;SQLģʽ" }, //

				{ "��ѡ��", "�Ƿ�ѡ����ǰ��", "N", "web�й�ѡ���ڱ�ǩǰ��", "��ͨģʽ" },
				{ "��ť", "��ʾ����", "��������ʾ����", "ɾ��������Ĭ��Ϊ�ֶ���", "��ͨģʽ" },
				{ "��ť", "��ʾͼƬ", "", "��ťͼ���ͼƬ����", "��ͨģʽ" },
				{ "��ť", "��ʾ", "", "��ť��ʾ", "��ͨģʽ" },
				{ "��ť", "����¼�", "", "ʵ��WLTActionListener�ӿڵ���·��", "��ͨģʽ" },
				//���ֿ�
				{ "���ֿ�", "����", "������", "ֻ������0-9", "������" },//sunfujun/20121126/���Ӽ������͵ļ���֤��������֤Ӧ������֤��ʽ���ύ��ʱ�򣬿�ƬӦ�����������Ŀǰ��û��
				{ "���ֿ�", "����", "С��", "С�������ͣ�����֤", "С��" },
				{ "���ֿ�", "����", "����", "���������ͣ�����֤", "����" },
				{ "���ֿ�", "����", "�绰", "�绰������֤", "�绰" },
				{ "���ֿ�", "����", "", "ԭ�����߼�", "����" },
				
				//������ʽ�ؼ�
				{ "������ʽ�ؼ�", "����", "100|([0-9]|[1-9][0-9])(\\.\\d*)?", "", "100����С��" },

				//����
				{ "����", "SQL���", "select id,code,name from pub_role", "ֱ��дһ��SQL,��������3��,�ֶ���$�Ž�β���Զ�����,���ʵ��ֻ������,��д�и��̶�ֵ��������,����select 1 id$,2 code$,name from pub_user", "��ͨģʽ" }, //

				//���Ͳ���
				{ "���Ͳ���", "SQL���", "select id ����$,code ����,name ����,parentmenuid ����$ from pub_menu", "ֱ��дһ��SQL,��������3��,�ֶ���$�Ž�β���Զ�����", "��ͨģʽ" }, //
				{ "���Ͳ���", "PKField", "����$", "���ް���", "��ͨģʽ" }, //
				{ "���Ͳ���", "ParentPKField", "����$", "���ް���", "��ͨģʽ" }, //
				{ "���Ͳ���", "�ı��Ƿ���", "N", "�����ı�������й�����", "��ͨģʽ" }, //

				//��ѡ����
				{ "��ѡ����", "SQL���", "select id,code,name from pub_comboboxdict where type='ѧ��' order by seq", "ֱ��дһ��SQL,��������3��,�ֶ���$�Ž�β���Զ�����", "��ͨģʽ" }, //

				//�б�ģ�����
				{ "�б�ģ�����", "ģ�����", "PUB_ROLE_CODE_1", "����ָ��һ��ģ�����", "��ģʽ;��ͨģʽ" }, //
				{ "�б�ģ�����", "ID�ֶ�", "id", "Ĭ��ֵ��id", "��ģʽ;��ͨģʽ" }, //
				{ "�б�ģ�����", "NAME�ֶ�", "name", "Ĭ��ֵ��name", "��ģʽ;��ͨģʽ" }, //
				{ "�б�ģ�����", "����SQL����", "type='����'", "�򿪴��ں�ִ�еĹ�������", "��ͨģʽ" }, //
				{ "�б�ģ�����", "���Զ�ѡ", "N", "�б��ڿ��Զ�ѡ����", "��ͨģʽ" }, //
				{ "�б�ģ�����", "�Զ���ѯ", "N", "�������ڳ��Զ���ѯ����", "��ͨģʽ" }, //
				{ "�б�ģ�����", "�ı��Ƿ���", "N", "�����ı�������й�����", "��ͨģʽ" }, //

				//����ģ�����
				{ "����ģ�����", "ģ�����", "pub_corp_dept_CODE1", "ģ������(������)", "��ģʽ;��ͨģʽ;����ģʽ" }, //
				{ "����ģ�����", "ID�ֶ�", "id", "���صĴ洢�ֶ���,һ�㶼��id", "��ģʽ;��ͨģʽ;����ģʽ" }, //
				{ "����ģ�����", "NAME�ֶ�", "name", "���ص���ʾ����,һ�㶼��name", "��ģʽ;��ͨģʽ;����ģʽ" }, //
				{ "����ģ�����", "����SQL����", "corptype='����'", "��ʱ����ݸù���������ѯ", "��ͨģʽ;����ģʽ" }, //
				{ "����ģ�����", "����Ȩ�޲���", "��������_��ѯ", "ʹ���ĸ�����Ȩ�޲���", "����ģʽ" }, //
				{ "����ģ�����", "����Ȩ�޲���ӳ��", "���˷�ʽ=��������;�����ֶ���=id;", "����Ȩ�޲���ӳ�䷽ʽ", "����ģʽ" }, //
				{ "����ģ�����", "ֻ��ǰ����", "0", "��ʱֻ�뿴Ŀ¼,����ֻ�뵽���Ŷ������Ŀ���", "����ģʽ" }, //
				{ "����ģ�����", "չ��", "N", "�Ƿ�չ�����н��", "����ģʽ" }, //
				{ "����ģ�����", "չ��ĳ�����1", "", "��������չ��ĳ�����,����ֵΪ100,�����ö�����,����", "����ģʽ" }, //����Ϊʲô������ƣ���������id�����硰100;125������Ҫ����ʦ���䣡
				{ "����ģ�����", "չ��ĳ�����2", "", "��'չ��ĳ�����'��ͷ��key,ֵΪ����ֵ.ע���׺��Ҫһ��,����浽hashmap�лᱻ����", "����ģʽ" }, //��ǰ���õġ�100������125��������Ĭ����Ϊ�մ�����Ϊ�󲿷�����²���Ҫ����չ����㣬�������������Ա����ɾ���ò���ʱ�����ͻ�չ�������/2012-06-06��
				{ "����ģ�����", "���Զ�ѡ", "N", "������Ƿ���Զ�ѡ,Ĭ�ϲ�����", "��ͨģʽ;����ģʽ" }, //������ӣ�����ƽ̨�������Щ����
				{ "����ģ�����", "ֻ��ѡҶ��", "N", "��ʱ����ѡ��Ҷ�ӽ��,������ѡ��Ŀ¼����", "��ͨģʽ;����ģʽ" }, //
				{ "����ģ�����", "����·������", "N", "������·��ƴ��������", "����ģʽ" }, //
				{ "����ģ�����", "�Ƿ�ص���һ��", "Y", "�Ƿ�ص���һ��,Ĭ�Ͻ�ȡ", "����ģʽ" }, //
				{ "����ģ�����", "�Ƿ�������ѡ", "Y", "��ѡһ�����ʱͬʱ�������������ӽ��Ҳ����", "����ģʽ" }, //
				{ "����ģ�����", "�Ƿ���ʾ��ť���", "N", "��ʱ�ڹ������ͽṹ����ʱҲ���ԶԸ����ݽ���ά��", "����ģʽ" }, //
				{ "����ģ�����", "�Ƿ�ѡ����ѡ�����Ŀ", "N", "���´�ʱ��֮ǰѡ�����Ŀ����ѡ��(����ڵ���������2000�˲����Զ�ʧЧ!)", "����ģʽ" }, //Ԭ����20120904���
				{ "����ģ�����", "�ı����Ƿ�ɱ༭", "N", "����ģ����յ�ѡʱ���������ı���ɱ༭��ֱ������ֵ���м���ѡ��", "��ͨģʽ;����ģʽ" }, //Ԭ����20120904���
				{ "����ģ�����", "����Ϊ��ʱ����ʾ", "��������", "��ģ���ѯ��������Ϊ��ʱ����ʾ", "��ͨģʽ;����ģʽ" }, //Ԭ����20130426���  �������������
				{ "����ģ�����", "�ı��Ƿ���", "N", "�����ı�������й�����", "��ͨģʽ" }, //

				//�Զ������
				{ "�Զ������", "�Զ�������", "cn.com.pushworld.TestRefDialog", "����,���������,����̳�cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog", "��ģʽ;��ͨģʽ" }, //
				{ "�Զ������", "����", "2", "�����ж������,�ֱ��ò���1,����2...N��������ƥ�乹�췽����4+N������,\r\n\t��:public TestRefDialog(Container _parent, String _title, RfItemVO refItemVO, BillPanel panel,String ����1,String ����2)", "��ͨģʽ" }, //

				//ע���������
				{ "ע���������", "ע�����", "2", "", "��ͨģʽ" }, //
				{ "ע���������", "����������", "", "", "��ͨģʽ" }, //
				{ "ע���������", "���", "2", "", "��ͨģʽ" }, //

				//ע�����
				{ "ע�����", "ע������", "RegCorp001", "", "��ͨģʽ" }, //

				//�����ӱ�
				{ "�����ӱ�", "ģ�����", "cmp_cmpevent_CODE1", "�ӱ��ģ�����", "��ͨģʽ;����ģʽ" }, //
				{ "�����ӱ�", "�����ֶ���", "id", "����(����)�е�������ϣ��", "��ͨģʽ;����ģʽ" }, //
				{ "�����ӱ�", "��������ֶ���", "parentid", "�ӱ��е��ĸ��ֶ������涨��������ֶι���,�������ӱ��¼ʱ,�Ὣ���ֶ�Ĭ��ֵ���ó��������Ǹ��ֶε�ֵ,�γɹ�����ϵ", "��ͨģʽ;����ģʽ" }, //
				{ "�����ӱ�", "������", "", "��UI��ʵ��ChildTableCommUIIntercept�ӿڡ�", "��ͨģʽ;����ģʽ" }, //
				{ "�����ӱ�", "ȡֵ", "", "�������ӱ��������޸�ʱ��������ֶ�ֵ����ʽΪ���ӱ��ֶ�1=�����ֶ�1;�ӱ��ֶ�2=�����ֶ�2��,�硰checkdept=finddept;userid=userid��", "����ģʽ" }, //

				//�����ӱ�
				{ "�����ӱ�", "ģ�����", "cmp_cmpevent_CODE1", "�ӱ��ģ�����", "��ͨģʽ" }, //
				{ "�����ӱ�", "�����ֶ���", "id", "����(����)�е�������ϣ��", "��ͨģʽ" }, //
				{ "�����ӱ�", "�����ֶ���ʾ��", "name", "", "��ͨģʽ" }, //
				{ "�����ӱ�", "��������ֶ���", "parentid", "�ӱ��е��ĸ��ֶ������涨��������ֶι���,�������ӱ��¼ʱ,�Ὣ���ֶ�Ĭ��ֵ���ó��������Ǹ��ֶε�ֵ,�γɹ�����ϵ", "��ͨģʽ" }, //
				{ "�����ӱ�", "������", "", "��UI��ʵ��ChildTableCommUIIntercept�ӿڡ�", "��ͨģʽ" }, //

				//�ļ�ѡ���
				{ "�ļ�ѡ���", "�ļ��洢��·��", "/upload", "�洢ʱϵͳ�������������ѡĿ¼���������ļ������洢�ļ�,Ĭ����/upload", "��ͨģʽ" }, //
				{ "�ļ�ѡ���", "�ļ����������", "", "�ļ������������������̳�AbstractRefFileNoCreate�ҷ���UI��", "��ͨģʽ" }, //
				{ "�ļ�ѡ���", "�ļ����Ƿ�ת��", "Y", "", "��ͨģʽ" }, //
				{ "�ļ�ѡ���", "������", "", "��UI�˸�һ����̳��ڳ�����AbstractRefFileIntercept,�����з����������ϴ�֮ǰ���ϴ����������!", "��ͨģʽ" }, //
				{ "�ļ�ѡ���", "�ļ�����", "Word�ĵ�,doc,docx", "�ļ�����, ����:Excle�ĵ�,xls,xlsx;Word�ĵ�,doc,docx;�����ļ�,*", "ɸѡģʽ" }, //����"�ļ�����"����, ���ԶԴ��ļ����ͽ��й��� Gwang 2013/3/13

				//Excel�ؼ�
				{ "Excel�ؼ�", "ģ�����", "���վ���", "Excelģ��ı���", "��ͨģʽ" }, //
				{ "Excel�ؼ�", "��ʼ������", "", "", "��ͨģʽ" }, //
				{ "Excel�ؼ�", "��ť�Զ������", "", "", "��ͨģʽ" }, //
				{ "Excel�ؼ�", "������ʾֵ", "", "", "��ͨģʽ" }, //
				{ "Excel�ؼ�", "��������ֵ", "", "", "��ͨģʽ" }, //
				{ "Excel�ؼ�", "��ʼ��sql", "", "", "��ͨģʽ" }, //

				//Office�ؼ�
				{ "Office�ؼ�", "�ļ�����", "doc", "��doc,xls,wps�ȼ���", "��ģʽ;��ͨģʽ" }, //
				{ "Office�ؼ�", "ģ���ļ���", "blank.doc", "����һ�δ���ʱ���Զ�����һ��ģ��,������ݺ�ͬ", "��ͨģʽ;����ģʽ" }, //
				{ "Office�ؼ�", "��ǩ������", "cn.com.infostrategy.ui.mdata.cardcomp.RefDialog_Office_BookMarkCreaterIFC", "���붨��һ����ʵ�ָýӿ�", "����ģʽ" }, //
				{ "Office�ؼ�", "�ؼ�����", "ǧ��", "Ŀǰ֧�ֽ��,ǧ������", "����ģʽ" }, //
				{ "Office�ؼ�", "�Ƿ���ʾ���밴ť", "Y", "�ұ߻����Ӻŵĵ��밴ť", "��ͨģʽ;����ģʽ" }, //
				{ "Office�ؼ�", "�洢Ŀ¼", "/officecompfile", "��ʱ�����ָ����Ŀ¼��,����/heguifile", "��ͨģʽ;����ģʽ" }, //
				{ "Office�ؼ�", "Office���յĴ򿪷�ʽ", "IE", "����ļ����鿴ʱ����IE����Office�򿪣�����˴���������ʹ��ƽ̨����������", "����ģʽ" }, //

				//�����ؼ�
				{ "����", "����", "{}<={otheritem}", "����ִ�й�ʽ������:{}>getCurrDate() and {}<getItemValue(date1) and <{data2} and {}+30>=2012-04-08������{}��ʾ��ǰѡ��ֵ���Բ�д", "��ͨģʽ" }, //
				{ "����", "��ѯʱ��������", "��;��;��;��;����", "�������������������ϣ�ǰ�����ʾ��ѡ����������ͣ����һ��[����]��ʾ�����������ķ�ʽѡ������������Ϊ�����", "��ͨģʽ" }, //��ʱ��Ҫ���Ʋ�ѯʱѡ����������ͣ�����ֻ��ѡ�񼾶ȣ�����ݵȣ������Ӹò��������/2012-06-20��
				{ "����", "��ѯʱ����Ĭ��ֵ����", "��;��;��;��", "��ѯʱĬ����ѡ��ǰ���ǰ���Ȼ���", "��ͨģʽ" }, // 20130514 Ԭ���� ��� ��Ҫ�����ѯʱ�������õ���������Ĭ��ֵ

				//�Զ���ؼ�
				{ "�Զ���ؼ�", "��Ƭ�е���", "", "��һ������Ϊ��Ƭʱ��ʾ��������̳�AbstractWLTCompentPanel,����Pub_Templet_1_ItemVO.class, BillPanel.class 2�����Ͳ����Ĺ��캯��", "��ͨģʽ" }, //
				{ "�Զ���ؼ�", "�б���Ⱦ��", "", "�ڶ�������Ϊ�б���Ⱦ������,���Բ��������ؼ���д��", "��ͨģʽ" }, //
				{ "�Զ���ؼ�", "�б�༭��", "", "��3������Ϊ�б�༭�����������Բ��������ؼ���д��", "��ͨģʽ" }, //
				//��ѯ������
				{ "��ѯ������", "1", "1", "\"id in (select parentid from PSBC_NOSTYLEFILE_ADD where FILE_ANDPEOPLE like '%{itemvalue}%')\" " + "\n {invalues}=��Ϊ�ֺŷָ���ֵʱ�˺����ȼ���getTBUtil().getInCondition(str_items) " + "\n {LikeOrSQL}=��Ϊ�ֺŷָ���ֵʱ(a;b)�˺����ȼ���like '%a%' or like '%b%' ", "�Զ���SQL" },
				{
						"��ѯ������",
						"1",
						"1",
						"�˴���д���ȫ·��,��Ҫʵ��IBillQueryPanelSQLCreateIFC,��д��BS�ˡ�\n" + "public String getSQLByCondition(String key, String value,\n" + "HashMap itemValues, HashMap itemSQLs, String _wholesql)\n" + "throws Exception {\n" + "   RefItemVO refItemVO=(RefItemVO) itemValues.get(key + \"_obj\");	//��ȡѡ��ֵ\n" + "   if(refItemVO==null){	//���δѡ,�򷵻�1=1,����ѯȫ������\n" + "    return \"1=1\";\n" + "   }\n"
								+ "   String condition=refItemVO.getHashVO().getStringValue(\"querycondition\");	//�õ�ת���õ�SQL����\n" + "   String beginCondition=new TBUtil().replaceAll(condition, \"{itemkey}\", \"train_begin_date\");	//�������滻Ϊʵ���ֶ�\n" + "   String endCondition=new TBUtil().replaceAll(condition, \"{itemkey}\", \"train_end_date\");	//�������滻Ϊʵ���ֶ�\n"
								+ "   String sql=beginCondition+\" or \"+endCondition;	//ƴ��SQL\n" + "   return sql;\n" + "}\n", "�Զ�����" }, { "��װhtml�¼�", "1", "", "���е���б�html���ͬ����,Ҫ���ʵ�ʵĺ�ͬ�ı�������,\nֻ��Ҫ�ں�ͬ�����ֶεĿؼ���������\"html����key\"����,\n���û�пؼ�����ľ�ʹ��getCommUC,\n����:�ı����ʹ�ù�ʽgetCommUC(\"�ı���\",\"html����key\",\"�����ֶ�key\")", "д��" }, };
		return str_info; //
	}

	public int getInitWidth() {
		return 920;
	}

	public int getInitHeight() {
		return 700;
	}

}
