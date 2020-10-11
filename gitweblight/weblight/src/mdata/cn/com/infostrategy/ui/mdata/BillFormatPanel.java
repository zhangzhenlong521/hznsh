package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTRadioPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.formatcomp.FormatEventBindFormulaParse;
import cn.com.infostrategy.ui.mdata.formatcomp.FormatPanelFormulaParse;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.infostrategy.ui.workflow.design.WorkFlowDesignWPanel;
import cn.com.infostrategy.ui.workflow.pbom.BillBomPanel;

public class BillFormatPanel extends BillPanel {

	private static final long serialVersionUID = -3012691858806670226L;

	private int li_count = 0;
	private String formatformula = null;

	private HashMap panelMap = new HashMap(); //

	private int suggestWidth = 800;
	private int suggestHeight = 500;

	private String returnRefItemVOIDFieldName = null;
	private String returnRefItemVONameFieldName = null;
	private BillPanel returnRefItemVOFrom = null; //

	private BillFormatPanel() {
	}

	/**
	 * 根据一个公式生成页面.
	 * @param _format
	 */
	public BillFormatPanel(String _format) {
		this.formatformula = _format; //
		this.setLayout(new BorderLayout()); //
		FormatPanelFormulaParse parse = new FormatPanelFormulaParse(this); //
		String str_return = (String) parse.execFormula(_format); //
		java.awt.Component compent = (java.awt.Component) panelMap.get(str_return); //输出最后一个面板
		this.add(compent, BorderLayout.CENTER); //
		//this.updateUI(); //
	}

	/**
	 * 取得系统注册的Format面板
	 * @param _regcode
	 * @return
	 */
	public static BillFormatPanel getRegisterFormatPanel(String _regcode) {
		try {
			HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, "select formatformula,eventbindformula,regformula1,regformula2,regformula3,suggestWidth,suggestheight from pub_regformatpanel where code='" + _regcode + "'"); //
			if (hvs != null && hvs.length > 0) {
				String str_formula = hvs[0].getStringValue("formatformula");
				String str_bindFoemula = hvs[0].getStringValue("eventbindformula"); //
				BillFormatPanel formatPanel = new BillFormatPanel(str_formula); //
				if (hvs[0].getIntegerValue("suggestWidth") != null && hvs[0].getIntegerValue("suggestWidth") > 0) {
					formatPanel.setSuggestWidth(hvs[0].getIntegerValue("suggestWidth").intValue()); //
				}
				if (hvs[0].getIntegerValue("suggestheight") != null && hvs[0].getIntegerValue("suggestheight") > 0) {
					formatPanel.setSuggestHeight(hvs[0].getIntegerValue("suggestheight").intValue()); //
				}

				if (str_bindFoemula != null && !str_bindFoemula.trim().equals("")) { //处理绑定公式,非常有用
					FormatEventBindFormulaParse formulaParse = new FormatEventBindFormulaParse(formatPanel, hvs[0].getStringValue("regformula1"), hvs[0].getStringValue("regformula2"), hvs[0].getStringValue("regformula3")); //
					String[] str_formulas = new TBUtil().split1(str_bindFoemula, ";"); //
					for (int i = 0; i < str_formulas.length; i++) {
						formulaParse.execFormula(str_formulas[i]); //
					}
				}
				return formatPanel;
			} else {
				throw new WLTAppException("没有找到编码为[" + _regcode + "]的注册面板!"); //
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 执行事件处理公式!!!
	 * @param _formula
	 */
	public void execEventBindFormula(String _formula) {
		if (_formula != null && !_formula.trim().equals("")) { //处理绑定公式,非常有用
			FormatEventBindFormulaParse formulaParse = new FormatEventBindFormulaParse(this); //
			String[] str_formulas = new TBUtil().split1(_formula, ";"); //
			for (int i = 0; i < str_formulas.length; i++) {
				formulaParse.execFormula(str_formulas[i]); //
			}
		}
	}

	public HashMap getPanelMap() {
		return panelMap;
	}

	public int getCompentSeq() {
		li_count++;
		return li_count;
	}

	private String[] getAllKeys() {
		return (String[]) panelMap.keySet().toArray(new String[0]);
	}

	public BillCardPanel getBillCardPanel() {
		String[] str_allkeys = getAllKeys(); //
		int li_pos = 0; //
		for (int i = 0; i < str_allkeys.length; i++) {
			Object obj = panelMap.get(str_allkeys[i]);
			if (obj instanceof BillCardPanel) {
				BillCardPanel cardPanel = (BillCardPanel) obj;
				return cardPanel; //
			}
		}
		return null;
	}

	public BillCardPanel getBillCardPanelByTempletCode(String _templetcode) {
		String[] str_allkeys = getAllKeys(); //
		for (int i = 0; i < str_allkeys.length; i++) {
			if (str_allkeys[i].startsWith("billcard_")) {
				BillCardPanel cardPanel = (BillCardPanel) panelMap.get(str_allkeys[i]);
				if (cardPanel.getTempletVO().getTempletcode().equalsIgnoreCase(_templetcode)) {
					return cardPanel;
				}
			}
		}
		return null;
	}

	public BillCardPanel getBillCardPanelByTempletCode(String _templetcode, int _index) {
		String[] str_allkeys = getAllKeys(); //
		int li_pos = 0; //
		for (int i = 0; i < str_allkeys.length; i++) {
			Object obj = panelMap.get(str_allkeys[i]);
			if (obj instanceof BillCardPanel) {
				BillCardPanel cardPanel = (BillCardPanel) obj;
				if (cardPanel.getTempletVO().getTempletcode().equalsIgnoreCase(_templetcode)) {
					li_pos++;
					if (li_pos == _index) {
						return cardPanel;
					}
				}
			}
		}
		return null;
	}

	public BillListPanel getBillListPanel() {
		String[] str_allkeys = getAllKeys(); //
		int li_pos = 0; //
		for (int i = 0; i < str_allkeys.length; i++) {
			Object obj = panelMap.get(str_allkeys[i]);
			if (obj instanceof BillListPanel) {
				BillListPanel listPanel = (BillListPanel) obj;
				return listPanel; //
			}
		}
		return null;
	}

	public BillListPanel getBillListPanelByTempletCode(String _templetcode) {
		String[] str_allkeys = getAllKeys(); //
		for (int i = 0; i < str_allkeys.length; i++) {
			if (str_allkeys[i].startsWith("billlist_")) {
				BillListPanel listPanel = (BillListPanel) panelMap.get(str_allkeys[i]);
				if (listPanel.getTempletVO().getTempletcode().equalsIgnoreCase(_templetcode)) {
					return listPanel;
				}
			}
		}
		return null;
	}

	public BillListPanel getBillListPanelByTempletCode(String _templetcode, int _index) {
		String[] str_allkeys = getAllKeys(); //
		int li_pos = 0; //
		for (int i = 0; i < str_allkeys.length; i++) {
			Object obj = panelMap.get(str_allkeys[i]);
			if (obj instanceof BillListPanel) {
				BillListPanel listPanel = (BillListPanel) obj;
				if (listPanel.getTempletVO().getTempletcode().equalsIgnoreCase(_templetcode)) {
					li_pos++;
					if (li_pos == _index) {
						return listPanel;
					}
				}
			}
		}
		return null;
	}

	public BillTreePanel getBillTreePanel() {
		String[] str_allkeys = getAllKeys(); //
		int li_pos = 0; //
		for (int i = 0; i < str_allkeys.length; i++) {
			Object obj = panelMap.get(str_allkeys[i]);
			if (obj instanceof BillTreePanel) {
				BillTreePanel treePanel = (BillTreePanel) obj;
				return treePanel; //
			}
		}
		return null;
	}

	public BillTreePanel getBillTreePanelByTempletCode(String _templetcode) {
		String[] str_allkeys = getAllKeys(); //
		for (int i = 0; i < str_allkeys.length; i++) {
			if (str_allkeys[i].startsWith("billtree_")) {
				BillTreePanel treePanel = (BillTreePanel) panelMap.get(str_allkeys[i]);
				if (treePanel.getTempletVO().getTempletcode().equalsIgnoreCase(_templetcode)) {
					return treePanel;
				}
			}
		}
		return null;
	}

	public BillTreePanel getBillTreePanelByTempletCode(String _templetcode, int _index) {
		String[] str_allkeys = getAllKeys(); //
		int li_pos = 0; //
		for (int i = 0; i < str_allkeys.length; i++) {
			Object obj = panelMap.get(str_allkeys[i]);
			if (obj instanceof BillTreePanel) {
				BillTreePanel treePanel = (BillTreePanel) obj;
				if (treePanel.getTempletVO().getTempletcode().equalsIgnoreCase(_templetcode)) {
					li_pos++;
					if (li_pos == _index) {
						return treePanel;
					}
				}
			}
		}
		return null;
	}

	public BillQueryPanel getBillQueryPanel() {
		String[] str_allkeys = getAllKeys(); //
		int li_pos = 0; //
		for (int i = 0; i < str_allkeys.length; i++) {
			Object obj = panelMap.get(str_allkeys[i]);
			if (obj instanceof BillQueryPanel) {
				BillQueryPanel queryPanel = (BillQueryPanel) obj;
				return queryPanel; //
			}
		}
		return null;
	}

	public BillQueryPanel getBillQueryPanelByTempletCode(String _templetcode) {
		String[] str_allkeys = getAllKeys(); //
		for (int i = 0; i < str_allkeys.length; i++) {
			if (str_allkeys[i].startsWith("billquery_")) {
				BillQueryPanel queryPanel = (BillQueryPanel) panelMap.get(str_allkeys[i]);
				if (queryPanel.getTempletCode().equalsIgnoreCase(_templetcode)) {
					return queryPanel;
				}
			}
		}
		return null;
	}

	public BillCellPanel getBillCellPanel() {
		String[] str_allkeys = getAllKeys(); //
		for (int i = 0; i < str_allkeys.length; i++) {
			if (str_allkeys[i].startsWith("billcell_")) {
				BillCellPanel cellPanel = (BillCellPanel) panelMap.get(str_allkeys[i]);
				return cellPanel;
			}
		}
		return null; //
	}

	/**
	 * 
	 * @param _templetcode
	 * @return
	 */
	public BillCellPanel getBillCellPanelByTempletCode(String _templetcode) {
		String[] str_allkeys = getAllKeys(); //
		for (int i = 0; i < str_allkeys.length; i++) {
			if (str_allkeys[i].startsWith("billcell_")) {
				BillCellPanel cellPanel = (BillCellPanel) panelMap.get(str_allkeys[i]);
				if (cellPanel.getTempletcode().equals(_templetcode)) {
					return cellPanel;
				}
			}
		}
		return null;
	}

	public WorkFlowDesignWPanel getWorkFlowPanel() {
		String[] str_allkeys = getAllKeys(); //
		for (int i = 0; i < str_allkeys.length; i++) {
			if (str_allkeys[i].startsWith("workflow_")) {
				WorkFlowDesignWPanel wfPanel = (WorkFlowDesignWPanel) panelMap.get(str_allkeys[i]);
				return wfPanel;
			}
		}
		return null; //
	}

	public WLTTabbedPane getTabbedPane() {
		String[] str_allkeys = getAllKeys(); //
		for (int i = 0; i < str_allkeys.length; i++) {
			if (str_allkeys[i].startsWith("tab_")) {
				WLTTabbedPane tabbedPane = (WLTTabbedPane) panelMap.get(str_allkeys[i]);
				return tabbedPane;
			}
		}
		return null; //
	}

	public JSplitPane getSplitPane() {
		String[] str_allkeys = getAllKeys(); //
		for (int i = 0; i < str_allkeys.length; i++) {
			if (str_allkeys[i].startsWith("split_")) {
				JSplitPane splitPane = (JSplitPane) panelMap.get(str_allkeys[i]);
				return splitPane;
			}
		}
		return null; //
	}

	public BillLevelPanel getBillLevelPanel() {
		String[] str_allkeys = getAllKeys(); //
		for (int i = 0; i < str_allkeys.length; i++) {
			Object obj = panelMap.get(str_allkeys[i]);
			if (obj instanceof BillLevelPanel) {
				BillLevelPanel levelPanel = (BillLevelPanel) obj;
				return levelPanel; //
			}
		}
		return null;
	}

	public WLTRadioPane getRadioTabbedPane() {
		String[] str_allkeys = getAllKeys(); //
		for (int i = 0; i < str_allkeys.length; i++) {
			if (str_allkeys[i].startsWith("radiotab_")) {
				WLTRadioPane tabbedPane = (WLTRadioPane) panelMap.get(str_allkeys[i]);
				return tabbedPane;
			}
		}
		return null; //
	}

	public BillBomPanel getBillBomPanel() {
		String[] str_allkeys = getAllKeys(); //
		int li_pos = 0; //
		for (int i = 0; i < str_allkeys.length; i++) {
			Object obj = panelMap.get(str_allkeys[i]);
			if (obj instanceof BillBomPanel) {
				BillBomPanel bomPanel = (BillBomPanel) obj;
				return bomPanel; //
			}
		}
		return null;
	}

	public BillButtonPanel getBillButtonPanel() {
		String[] str_allkeys = getAllKeys(); //
		for (int i = 0; i < str_allkeys.length; i++) {
			Object obj = panelMap.get(str_allkeys[i]);
			if (obj instanceof BillButtonPanel) {
				BillButtonPanel buttonPanel = (BillButtonPanel) obj;
				return buttonPanel; //
			}
		}
		return null;
	}

	public JPanel getLabelPanel() {
		String[] str_allkeys = getAllKeys(); //
		for (int i = 0; i < str_allkeys.length; i++) {
			if (str_allkeys[i].startsWith("labelPanel_")) {
				JPanel jPanel = (JPanel) panelMap.get(str_allkeys[i]);
				return jPanel;
			}
		}
		return null;
	}

	public JPanel getLabelPanelByCode(String _code) {
		String[] str_allkeys = getAllKeys(); //
		for (int i = 0; i < str_allkeys.length; i++) {
			if (str_allkeys[i].startsWith("labelPanel_")) {
				JPanel jPanel = (JPanel) panelMap.get(str_allkeys[i]);
				String str_code = (String) jPanel.getClientProperty("BillFormatReg_labelPanelCode"); //
				if (str_code != null && str_code.equalsIgnoreCase(_code)) {
					return jPanel;
				}
			}
		}
		return null;
	}

	/**
	 * 根据类名取得JPanel...
	 * @param _classsName
	 * @return
	 */
	public JPanel getJPanelByClassName(String _classsName) {
		String[] str_allkeys = getAllKeys(); //
		for (int i = 0; i < str_allkeys.length; i++) {
			if (str_allkeys[i].startsWith("panel_" + _classsName)) {
				JPanel jPanel = (JPanel) panelMap.get(str_allkeys[i]);
				return jPanel; //
			}
		}
		return null; //
	}

	public int getSuggestWidth() {
		return suggestWidth;
	}

	public int getSuggestHeight() {
		return suggestHeight;
	}

	public void setSuggestWidth(int suggestWidth) {
		this.suggestWidth = suggestWidth;
	}

	public void setSuggestHeight(int suggestHeight) {
		this.suggestHeight = suggestHeight;
	}

	public BillPanel getReturnRefItemVOFrom() {
		return returnRefItemVOFrom;
	}

	public void setReturnRefItemVOFrom(BillPanel returnRefItemVOFrom) {
		this.returnRefItemVOFrom = returnRefItemVOFrom;
	}

	public String getReturnRefItemVOIDFieldName() {
		return returnRefItemVOIDFieldName;
	}

	public void setReturnRefItemVOIDFieldName(String returnRefItemVOIDFieldName) {
		this.returnRefItemVOIDFieldName = returnRefItemVOIDFieldName;
	}

	public String getReturnRefItemVONameFieldName() {
		return returnRefItemVONameFieldName;
	}

	public void setReturnRefItemVONameFieldName(String returnRefItemVONameFieldName) {
		this.returnRefItemVONameFieldName = returnRefItemVONameFieldName;
	}

}
