package cn.com.infostrategy.ui.sysapp.refdialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTRadioPane;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;
import cn.com.infostrategy.ui.sysapp.corpdept.CorpDeptBillTreePanel;

/**
 * 通用的机构参照,它与通用时间参照是最重要的两个查询条件!!所有两者都做成万能能用的!
 * 绝大多数查询条件都是通过机构与时间进行过滤的!!!所以至关重要!!!
 * @author xch
 *
 */
public class CommonCorpDeptRefDialog extends AbstractRefDialog implements ActionListener, BillTreeSelectListener {

	private HashMap parMaps = new HashMap(); //可以转送参数!!

	public CommonCorpDeptRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
		super(_parent, _title, refItemVO, panel);
	}

	public CommonCorpDeptRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel, String _parDefineStr) {
		super(_parent, _title, refItemVO, panel); //
		parMaps.putAll(new TBUtil().parseStrAsMap(_parDefineStr)); //
	}

	private final String STR_SIMPLE = "直接选择";
	private final String STR_COMPLEX = "复杂选择";

	private WLTRadioPane radioPanel = null;

	private WLTButton btn_allarea, btn_confirm, btn_cancel; //确定与取消按钮
	private CorpDeptBillTreePanel billTreePanel_Corp_simple = null; //
	private CorpDeptBillTreePanel billTreePanel_Corp = null;

	private JCheckBox rb_zhonghbm, rb_fengh, rb_shiyb, rb_fenghbm, rb_zhih, rb_shiybfb; //总行部门,分行,事业部,分行部门,支行,事业部分部.
	private JCheckBox rb_zhonghbm_sub, rb_zhongh, rb_shiyb_sub, rb_fenghbm_sub, rb_zhih_sub, rb_shiybfb_sub; //总行部门,分行,事业部,分行部门,支行,事业部分部.

	private WLTButton btn_selAllType, btn_selAllRecord; //全选,全消
	private BillListPanel billListPanel_Corp = null; //选中的机构

	private RefItemVO returnRefItemVO = null; //

	@Override
	public void initialize() {
		//
		radioPanel = new WLTRadioPane();
		billTreePanel_Corp_simple = getBillTreeWithChecked(true);
		radioPanel.addTab(STR_SIMPLE, billTreePanel_Corp_simple);
		radioPanel.addTab(STR_COMPLEX, getSplitPanel());

		this.getContentPane().setLayout(new BorderLayout()); //
		this.getContentPane().add(radioPanel, BorderLayout.CENTER);
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //

		if (!billTreePanel_Corp_simple.isCustConditionIsNull()) { //如果不为空,则要隐藏所有范围的按钮
			btn_allarea.setVisible(false); //
		}
	}

	@Override
	public RefItemVO getReturnRefItemVO() {
		return returnRefItemVO;
	}

	/**
	 * 生成一棵机构树
	 * @param canCheck 设定树节点前是否带复选框
	 * @return
	 */
	private CorpDeptBillTreePanel getBillTreeWithChecked(boolean canCheck) {
		CorpDeptBillTreePanel billTreePanel = new CorpDeptBillTreePanel(3, true); //机构树...
		billTreePanel.getJTree().setRootVisible(true); //
		billTreePanel.queryDataByCondition(null); //查询所有数据!!!
		billTreePanel.setSelected(billTreePanel.getRootNode()); //选中根结点
		billTreePanel.addBillTreeSelectListener(this); //树结点选择变化事件!!
		billTreePanel.reSetTreeChecked(canCheck);//设置树为可选
		return billTreePanel;
	}

	private WLTSplitPane getSplitPanel() {
		//机构树
		billTreePanel_Corp = getBillTreeWithChecked(false);

		//右边的按钮与表格(第一排)
		JPanel panel_btn_1 = new JPanel(); //两行七列的网格布局
		panel_btn_1.setLayout(null); //
		rb_zhonghbm = new JCheckBox("总行部门"); //
		rb_fengh = new JCheckBox("分行"); //
		rb_shiyb = new JCheckBox("事业部"); //
		rb_fenghbm = new JCheckBox("分行部门"); //
		rb_zhih = new JCheckBox("支行"); //
		rb_shiybfb = new JCheckBox("事业部分部"); //

		btn_selAllType = new WLTButton("选中所有机构"); //
		btn_selAllType.putClientProperty("selected", Boolean.FALSE); //
		btn_selAllType.setFocusable(false); //

		rb_zhonghbm_sub = new JCheckBox("总行部门_下属"); //
		rb_zhongh = new JCheckBox("总行"); //
		rb_shiyb_sub = new JCheckBox("事业部_下属"); //
		rb_fenghbm_sub = new JCheckBox("分行部门_下属"); //
		rb_zhih_sub = new JCheckBox("支行_下属"); //
		rb_shiybfb_sub = new JCheckBox("事业部分部_下属"); //

		btn_selAllRecord = new WLTButton("选中表格所有数据"); //
		btn_selAllRecord.putClientProperty("selected", Boolean.FALSE); //
		btn_selAllRecord.setFocusable(false); //

		int li_x = 0; //

		int li_width = 105; ////
		rb_zhonghbm.setBounds(li_x, 0, li_width, 20); ////..
		rb_zhonghbm_sub.setBounds(li_x, 20, li_width, 20); ////
		li_x = li_x + li_width; ////

		li_width = 55; //
		rb_fengh.setBounds(li_x, 0, li_width, 20); //
		rb_zhongh.setBounds(li_x, 20, li_width, 20); //
		li_x = li_x + li_width; //

		li_width = 100; //
		rb_shiyb.setBounds(li_x, 0, li_width, 20); //
		rb_shiyb_sub.setBounds(li_x, 20, li_width, 20); //
		li_x = li_x + li_width; //

		li_width = 105; //
		rb_fenghbm.setBounds(li_x, 0, li_width, 20); //
		rb_fenghbm_sub.setBounds(li_x, 20, li_width, 20); //
		li_x = li_x + li_width; //

		li_width = 90; //
		rb_zhih.setBounds(li_x, 0, li_width, 20); //
		rb_zhih_sub.setBounds(li_x, 20, li_width, 20); //
		li_x = li_x + li_width; //

		li_width = 120; //
		rb_shiybfb.setBounds(li_x, 0, li_width, 20); //
		rb_shiybfb_sub.setBounds(li_x, 20, li_width, 20); //
		li_x = li_x + li_width; //

		li_width = 125; //
		btn_selAllType.setBounds(li_x, 0, li_width, 20); //
		//li_x = li_x + li_width; //

		li_width = 125; //
		btn_selAllRecord.setBounds(li_x, 22, li_width, 20); //
		li_x = li_x + li_width; //

		rb_zhonghbm.addActionListener(this); //
		rb_fengh.addActionListener(this); //
		rb_shiyb.addActionListener(this); //
		rb_fenghbm.addActionListener(this); //
		rb_zhih.addActionListener(this); //
		rb_shiybfb.addActionListener(this); //
		btn_selAllType.addActionListener(this); //

		rb_zhonghbm_sub.addActionListener(this); //
		rb_zhongh.addActionListener(this); //
		rb_shiyb_sub.addActionListener(this); //
		rb_fenghbm_sub.addActionListener(this); //
		rb_zhih_sub.addActionListener(this); //
		rb_shiybfb_sub.addActionListener(this); //
		btn_selAllRecord.addActionListener(this); //

		panel_btn_1.add(rb_zhonghbm); //
		panel_btn_1.add(rb_fengh); //
		panel_btn_1.add(rb_shiyb); //
		panel_btn_1.add(rb_fenghbm); //
		panel_btn_1.add(rb_zhih); //
		panel_btn_1.add(rb_shiybfb); //
		panel_btn_1.add(btn_selAllType); //

		panel_btn_1.add(rb_zhonghbm_sub); //
		panel_btn_1.add(rb_zhongh); //
		panel_btn_1.add(rb_shiyb_sub); //
		panel_btn_1.add(rb_fenghbm_sub); //
		panel_btn_1.add(rb_zhih_sub); //
		panel_btn_1.add(rb_shiybfb_sub); //
		panel_btn_1.add(btn_selAllRecord); //
		panel_btn_1.setPreferredSize(new Dimension(li_x, 45)); //

		JPanel panel_btn_list = new JPanel(new BorderLayout()); //
		panel_btn_list.add(panel_btn_1, BorderLayout.NORTH); //

		billListPanel_Corp = new BillListPanel(new DefaultTMO(getListParnetTMO(), getListChildTMO())); //
		billListPanel_Corp.setToolbarVisiable(false); //
		panel_btn_list.add(billListPanel_Corp, BorderLayout.CENTER); //

		WLTSplitPane splitPanel = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTreePanel_Corp, panel_btn_list); //
		splitPanel.setDividerLocation(225);

		return splitPanel;
	}

	/**
	 * 按钮面板
	 * @return
	 */
	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		btn_confirm = new WLTButton("确定");
		btn_allarea = new WLTButton("所有范围"); ////
		btn_cancel = new WLTButton("取消");

		btn_cancel.addActionListener(this); //
		btn_allarea.addActionListener(this); //
		btn_confirm.addActionListener(this); //

		panel.add(btn_confirm); //
		panel.add(btn_allarea); //
		panel.add(btn_cancel); //
		return panel;
	}

	/**
	 * 点击按钮动作!!!!
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_allarea) {
			onChooseAllArea(); //
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		} else if (e.getSource() == rb_zhonghbm || //
				e.getSource() == rb_fengh || //
				e.getSource() == rb_shiyb || //
				e.getSource() == rb_fenghbm || //
				e.getSource() == rb_zhih || //
				e.getSource() == rb_shiybfb || //
				e.getSource() == rb_zhonghbm_sub || //
				e.getSource() == rb_zhongh || //
				e.getSource() == rb_shiyb_sub || //
				e.getSource() == rb_fenghbm_sub || //
				e.getSource() == rb_zhih_sub || //
				e.getSource() == rb_shiybfb_sub) {
			onSelectCorpChanged(); //
		} else if (e.getSource() == btn_selAllType) {
			onSelectAllCorpType((WLTButton) e.getSource()); //是否选中所有!
		} else if (e.getSource() == btn_selAllRecord) {
			onSelectAllRecord((WLTButton) e.getSource()); //是否选中所有!
		}
	}

	private void onSelectAllCorpType(WLTButton _button) {
		Boolean bo_selected = (Boolean) _button.getClientProperty("selected"); //
		if (bo_selected) {
			setAllCorpTypeChecked(false); //
			_button.setText("选中所有机构"); //
			_button.putClientProperty("selected", Boolean.FALSE); //
		} else {
			setAllCorpTypeChecked(true); //
			_button.setText("取消选中所有机构"); //
			_button.putClientProperty("selected", Boolean.TRUE); //
		}
		onSelectCorpChanged(); //
	}

	private void setAllCorpTypeChecked(boolean _checked) {
		rb_zhonghbm.setSelected(_checked);
		rb_fengh.setSelected(_checked);
		rb_shiyb.setSelected(_checked);
		rb_fenghbm.setSelected(_checked);
		rb_zhih.setSelected(_checked);
		rb_shiybfb.setSelected(_checked);
		rb_zhonghbm_sub.setSelected(_checked);
		rb_zhongh.setSelected(_checked);
		rb_shiyb_sub.setSelected(_checked);
		rb_fenghbm_sub.setSelected(_checked);
		rb_zhih_sub.setSelected(_checked);
		rb_shiybfb_sub.setSelected(_checked);
	}

	private void onSelectAllRecord(WLTButton _button) {
		Boolean bo_selected = (Boolean) _button.getClientProperty("selected"); //
		if (bo_selected) {
			billListPanel_Corp.clearSelection(); //
			_button.setText("选中表格所有数据"); //
			_button.putClientProperty("selected", Boolean.FALSE); //
		} else {
			billListPanel_Corp.selectAll();
			_button.setText("取消选中表格数据"); //
			_button.putClientProperty("selected", Boolean.TRUE); //
		}
	}

	/**
	 * 点击确定时所做的
	 */
	private void onConfirm() {
		BillVO[] billVOs = null;
		if (radioPanel.getSelectTitle().equals(this.STR_SIMPLE)) {
			billVOs = billTreePanel_Corp_simple.getCheckedVOs();
			if (billVOs == null || billVOs.length == 0) {
				MessageBox.show(this, "请选择机构树上的一个机构,可勾选要选择的机构节点前的复选框来选择该机构!"); //
				return;
			}
		} else if (radioPanel.getSelectTitle().equals(this.STR_COMPLEX)) {
			billVOs = billListPanel_Corp.getSelectedBillVOs(); //
			if (billVOs == null || billVOs.length == 0) {
				MessageBox.show(this, "请选择表格中的一个机构,可点击右上方的\"选中表格所有数据\"按钮快速选择中所有数据!"); //
				return;
			}
		}

		//遍历选中的所有机构..
		StringBuilder sb_ids = new StringBuilder(); //
		StringBuilder sb_names = new StringBuilder(); //
		for (int i = 0; i < billVOs.length; i++) {
			sb_ids.append(billVOs[i].getStringValue("id") + ";"); //
			sb_names.append(billVOs[i].getStringValue("name") + ";"); //
		}

		returnRefItemVO = new RefItemVO(sb_ids.toString(), null, sb_names.toString()); //
		this.setCloseType(1); //
		this.dispose(); //
	}

	/**
	 * 选择所有范围
	 */
	private void onChooseAllArea() {
		returnRefItemVO = new RefItemVO("所有范围", null, "所有范围"); //
		this.setCloseType(1); //
		this.dispose(); //
	}

	/**
	 * 点击取消时所做的!!
	 */
	private void onCancel() {
		returnRefItemVO = null; //
		this.setCloseType(2); //
		this.dispose(); //
	}

	/**
	 * 
	 */
	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		onSelectCorpChanged();
	}

	private void onSelectCorpChanged() {
		billListPanel_Corp.clearTable(); //清空表格数据!!!
		DefaultMutableTreeNode node = billTreePanel_Corp.getSelectedNode(); //
		if (node == null) {
			return;
		}

		ArrayList al_corpTypes = getChooseCorpType(); //选中的机构类型!!
		ArrayList al_allCorpNodes = new ArrayList(); //
		DefaultMutableTreeNode[] selectetNodes = billTreePanel_Corp.getSelectedNodes(); //
		for (int i = 0; i < selectetNodes.length; i++) {
			DefaultMutableTreeNode[] selNode_allNodes = billTreePanel_Corp.getOneNodeChildPathNodes(selectetNodes[i]); //取得所有机构
			for (int j = 0; j < selNode_allNodes.length; j++) {
				if (!al_allCorpNodes.contains(selNode_allNodes[j])) { //如果不包含
					al_allCorpNodes.add(selNode_allNodes[j]); //
				}
			}
		}

		DefaultMutableTreeNode[] allNodes = (DefaultMutableTreeNode[]) al_allCorpNodes.toArray(new DefaultMutableTreeNode[0]); //
		for (int i = 0; i < allNodes.length; i++) {
			String str_corpType = billTreePanel_Corp.getModelItemVaueFromNode(allNodes[i], "corptype"); //
			if (al_corpTypes.contains(str_corpType)) { //如果是属于这种类型的!!!
				int li_row = billListPanel_Corp.addEmptyRow(false); //
				billListPanel_Corp.setValueAt(new StringItemVO(billTreePanel_Corp.getModelItemVaueFromNode(allNodes[i], "id")), li_row, "id"); //
				billListPanel_Corp.setValueAt(new StringItemVO(billTreePanel_Corp.getModelItemVaueFromNode(allNodes[i], "code")), li_row, "code"); //
				billListPanel_Corp.setValueAt(new StringItemVO(billTreePanel_Corp.getModelItemVaueFromNode(allNodes[i], "name")), li_row, "name"); //
				billListPanel_Corp.setValueAt(new StringItemVO(str_corpType), li_row, "corptype"); //CORPTYPE
			}

		}
	}

	/**
	 * 现取当前选中的是什么类型!!!!
	 * @return
	 */
	private ArrayList getChooseCorpType() {
		ArrayList al_type = new ArrayList(); //
		if (rb_zhonghbm.isSelected()) {
			al_type.add("总行部门"); //
		}
		if (rb_fengh.isSelected()) {
			al_type.add("分行"); //
		}
		if (rb_shiyb.isSelected()) {
			al_type.add("事业部"); //
		}
		if (rb_fenghbm.isSelected()) {
			al_type.add("分行部门"); //
		}
		if (rb_zhih.isSelected()) {
			al_type.add("支行"); //
		}
		if (rb_shiybfb.isSelected()) {
			al_type.add("事业部分部"); //
		}

		if (rb_zhonghbm_sub.isSelected()) {
			al_type.add("总行部门_下属机构"); //
		}
		if (rb_zhongh.isSelected()) {
			al_type.add("总行"); //
		}
		if (rb_shiyb_sub.isSelected()) {
			al_type.add("事业部_下属机构"); //
		}
		if (rb_fenghbm_sub.isSelected()) {
			al_type.add("分行部门_下属机构"); //
		}
		if (rb_zhih_sub.isSelected()) {
			al_type.add("支行_下属机构"); //
		}
		if (rb_shiybfb_sub.isSelected()) {
			al_type.add("事业部分部_下属机构"); //
		}
		return al_type;
	}

	private HashVO getListParnetTMO() {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", "机构表"); //模版编码,请勿随便修改
		vo.setAttributeValue("templetname", "机构表"); //模板名称
		vo.setAttributeValue("templetname_e", "机构表"); //模板名称
		return vo;
	}

	@Override
	public int getInitWidth() {
		return 950; //
	}

	/**
	 * 取得列表的子表定义VO.
	 * @return
	 */
	private HashVO[] getListChildTMO() {
		ArrayList al_hvo = new ArrayList(); //

		HashVO itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ID"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "主键"); //显示名称
		itemVO.setAttributeValue("itemname_e", "Id"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "3"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "145"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "Y"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		al_hvo.add(itemVO); //

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "CODE"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "编码"); //显示名称
		itemVO.setAttributeValue("itemname_e", "CODE"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "3"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "125"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "Y"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		al_hvo.add(itemVO); //

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "NAME"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "名称"); //显示名称
		itemVO.setAttributeValue("itemname_e", "NAME"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "3"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "225"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "Y"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		al_hvo.add(itemVO); //

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "CORPTYPE"); //唯一标识,用于取数与保存
		itemVO.setAttributeValue("itemname", "类型"); //显示名称
		itemVO.setAttributeValue("itemname_e", "CORPTYPE"); //显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
		itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
		itemVO.setAttributeValue("refdesc", null); //参照定义
		itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "3"); //1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); //加载公式
		itemVO.setAttributeValue("editformula", null); //编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
		itemVO.setAttributeValue("listwidth", "100"); //列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "Y"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "Y");
		al_hvo.add(itemVO); //

		return (HashVO[]) al_hvo.toArray(new HashVO[0]);
	}

}
