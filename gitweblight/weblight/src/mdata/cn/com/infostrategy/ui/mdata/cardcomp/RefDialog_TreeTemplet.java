package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.ui.common.BackGroundDrawingUtil;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;

/**
 * 录入时中的树型参照窗口!!!
 * 一种通过将注册面板快速转换成一个自定义参照的基类!!
 * 即一般一个自定义参照都可以通过一个注册面板生成主页面,然后在下面增加一个确定与取消按扭从而形成一个参照!!!
 * 在树型模板参照中将路径也传过来了!!
 * @author xch
 *
 */
public class RefDialog_TreeTemplet extends AbstractRefDialog implements ActionListener, ChangeListener {
	private static final long serialVersionUID = 1L;
	private WLTButton btn_confirm, btn_cancel; //
	private RefItemVO returnRefItemVO = null; //
	private RefItemVO refItemVO = null;
	private BillTreePanel billTreePanel = null, billTreePanel2 = null; //
	private JButton bnt_1, bnt_2, bnt_3, bnt_4, bnt_5; //

	private JTabbedPane tabbedPanel = null; //
	private JPanel tabContentPanel_1, tabContentPanel_2 = null; //

	private JCheckBox[] checkBoxs = null; //
	private DefaultMutableTreeNode[] tree2AllNodes = null; //
	private BillListPanel billListPanel2 = null; //复杂的购物车模式右边的表格!!
	private JPanel southPanel = null; //
	private CommUCDefineVO dfvo = null; //这个东西要去掉!!!
	private TBUtil tbUtil = new TBUtil(); //

	private BillDialog copySelGroupDialog = null; //拷贝组时的窗口!
	private JCheckBox isOnlyFirstCheck = null;
	private JCheckBox[] copySelGroupConsCheck = null; //
	private JTextField copySelGroupCustConText = null; //
	private boolean isTabLoaded_1 = false;
	private boolean isTabLoaded_2 = false; //

	public RefDialog_TreeTemplet(Container _parent, String _title, RefItemVO _initRefItemVO, BillPanel panel, CommUCDefineVO _dfvo) {
		super(_parent, _title, _initRefItemVO, panel);
		this.refItemVO = _initRefItemVO;
		dfvo = _dfvo;
		if (ClientEnvironment.isAdmin()) {
			this.setTitle(this.getTitle() + ",实现类是[RefDialog_TreeTemplet]"); //
		}
	}

	@Override
	public void initialize() {
		//购物车模式就是左边是树,右边是表格,然后可以把左边树中的结点加入右边表格中!!!
		String str_isChooseCopy = dfvo.getConfValue("是否支持两种模式", "N"); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
		if (str_isChooseCopy.startsWith("Y")) { //
			tabContentPanel_1 = new JPanel(new BorderLayout()); //
			tabContentPanel_2 = new JPanel(new BorderLayout()); //

			tabbedPanel = new JTabbedPane(); //
			tabbedPanel.setFocusable(false); //
			tabbedPanel.addTab("简单模式", UIUtil.getImage("office_045.gif"), tabContentPanel_1); //
			tabbedPanel.addTab("复杂模式", UIUtil.getImage("office_020.gif"), tabContentPanel_2); //

			if (str_isChooseCopy.equals("Y2")) {
				tabbedPanel.setSelectedIndex(1);
				initTab2();
			} else {
				tabbedPanel.setSelectedIndex(0);
				initTab1();
			}
			tabbedPanel.addChangeListener(this); //
			this.getContentPane().add(tabbedPanel, BorderLayout.CENTER); //
			this.setSize(800, 600); //
			this.locationToCenterPosition(); //
		} else {
			tabContentPanel_1 = new JPanel(new BorderLayout()); //
			this.getContentPane().add(tabContentPanel_1, BorderLayout.CENTER);
			initTab1(); //加载第一个页面
		}
	}

	/**
	 * 第一个页签,简单模式!
	 */
	private void initTab1() {
		if (tabbedPanel != null) {
			tabbedPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
		}
		tabContentPanel_1.removeAll(); //
		tabContentPanel_1.setLayout(new BorderLayout()); //

		billTreePanel = new BillTreePanel(dfvo.getConfValue("模板编码")); //通过注册码生成一个格式面板
		String str_isMultiChoose = dfvo.getConfValue("可以多选"); //
		if (str_isMultiChoose != null) { //如果显式指定了,则处理,即如果没此参数!则原来参照模板中定义的是啥就是啥! 经前不是这样的! 以前没定义,则强行变成了N
			if (str_isMultiChoose.equalsIgnoreCase("Y")) {
				if (!billTreePanel.isChecked()) { //如果原来不是勾选框的样子
					billTreePanel.reSetTreeChecked(true); //则设成勾选框
				}
			} else if (str_isMultiChoose.equalsIgnoreCase("N")) {
				if (billTreePanel.isChecked()) { //如果原来模板定义的是多选,则要还原成单选!
					billTreePanel.reSetTreeChecked(false);
				}
			}
		}

		if (billTreePanel.isChecked()) {
			boolean isLinkCheck = dfvo.getConfBooleanValue("是否连动勾选", true); //
			billTreePanel.setDefaultLinkedCheck(isLinkCheck); //不联动!
		} else {
			//billTreePanel.getJTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); //单选
		}

		if (!dfvo.getConfBooleanValue("是否显示按钮面板", false)) {//新增树型模板是否显示按钮面板的参数，如果模板中配置了树型按钮，在选择参照时，如果没有对应的记录，可进行新增操作！【李春娟/2012-03-05】
			billTreePanel.getBtnPanel().setVisible(false); //
		}

		billTreePanel.setDragable(false); //

		//查询数据!!!
		String queryStr = dfvo.getConfValue("附加SQL条件");
		String str_dataPolicy = dfvo.getConfValue("数据权限策略"); //数据过滤权限！！
		if (str_dataPolicy != null) { //如果有这样一个定义,则强行手工修改模板中的数据权限策略!之所以这么做是因为，如果不这样，则必须为每个策略先配置一个模板！然后依赖更多的模板！如果有这样一个参数,则只需要一个模板!而且可以重用以前的某个模板（哪怕其已经定义了策略）!因为反正我会冲掉之！
			billTreePanel.setDataPolicy(str_dataPolicy, dfvo.getConfValue("数据权限策略映射")); //
		}
		billTreePanel.queryDataByCondition(queryStr, getViewOnlyLevel()); //查询数据!!!

		//加入两个控制参数  展开某一层
		if (dfvo.getConfBooleanValue("展开", false)) {
			billTreePanel.myExpandAll();
		}

		String[] str_expandnoneNodes = dfvo.getAllConfKeys("展开某个结点", true); //
		if (str_expandnoneNodes.length > 0) { //
			for (int i = 0; i < str_expandnoneNodes.length; i++) {
				String str_idValue = dfvo.getConfValue(str_expandnoneNodes[i]); //取得实际的值!!
				billTreePanel.expandOneNodeByKey(str_idValue);
			}
		}

		int li_allNodeCount = billTreePanel.getAllNodesCount(); //
		if (li_allNodeCount > 0 && li_allNodeCount <= 20) { //如果结点小于20个,则自动全部展开!省得费功了!
			billTreePanel.myExpandAll();
		}

		if (refItemVO != null && refItemVO.getId() != null && !refItemVO.getId().equals("")) {
			billTreePanel.findNodesByKeysAndScrollTo(refItemVO.getId().split(";")); //树型模板参照的反向勾选 【杨科/2012-08-30】
		}

		billTreePanel.setHelpInfoVisiable(true);

		if (ClientEnvironment.isAdmin()) {
			if (billTreePanel.isChecked() && billTreePanel.isDefaultLinkedCheck()) { //如果是勾选,且联动,则强烈警告
				JTextField warnTextField = new JTextField("致开发人员提醒:多选树型参照在维护数据时,不要设置成联动选择,即只存储单个结点!(而现在不是)"); //
				warnTextField.setPreferredSize(new Dimension(2000, 22)); //
				warnTextField.setSelectionStart(0);
				warnTextField.setSelectionEnd(0); //
				warnTextField.setEditable(false); //
				warnTextField.setBackground(Color.RED); //
				warnTextField.setForeground(Color.BLACK); //
				warnTextField.setBorder(BorderFactory.createEmptyBorder()); //空白边框
				tabContentPanel_1.add(warnTextField, BorderLayout.NORTH); //
			}
		}

		//如果只有一个节点, 并且模板中"是否显示根节点"为True, 就不显示这个没用的树了! Gwang 2012-09-19

		JPanel treePanel = new JPanel(new BorderLayout()); //
		if (li_allNodeCount == 1 && billTreePanel.getTempletVO().getTreeisshowroot()) {
			String templetName = billTreePanel.getTempletVO().getTempletname();
			String nodataAlert = dfvo.getConfValue("数据为空时的提示"); //袁江晓 20130526 添加 杨庆提出的需求
			String tempstr = "";
			if (null == nodataAlert || "".equals(nodataAlert)) {
				tempstr = "没有找到[" + templetName + "]的相关数据.";
			} else {
				tempstr = nodataAlert;
			}
			WLTLabel lbmsg = new WLTLabel(tempstr);
			treePanel.add(lbmsg, BorderLayout.NORTH);
			btn_confirm.setVisible(false);
		} else {
			billTreePanel.setHelpInfoVisiable(true);
			treePanel.add(billTreePanel, BorderLayout.CENTER);
		}

		tabContentPanel_1.add(treePanel); //

		if (tabbedPanel != null) {
			tabbedPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
		}

		isTabLoaded_1 = true; //
	}

	/**
	 * 第二个页签,复杂模式!即购物车选择方式!
	 */
	private void initTab2() {
		if (tabbedPanel != null) {
			tabbedPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
		}
		JPanel dragTreeItemPanel = getDragItemTreeItemPanel(); //购物车面板!!
		tabContentPanel_2.removeAll(); //
		tabContentPanel_2.setLayout(new BorderLayout()); //
		tabContentPanel_2.add(dragTreeItemPanel);
		if (tabbedPanel != null) {
			tabbedPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
		}
		isTabLoaded_2 = true; //
	}

	//切换状态!
	public void stateChanged(ChangeEvent _event) {
		int li_pos = tabbedPanel.getSelectedIndex(); //
		if (li_pos == 0 && !isTabLoaded_1) {
			initTab1(); //
		} else if (li_pos == 1 && !isTabLoaded_2) {
			initTab2(); //
		}
	}

	/**
	 * 购物车模式的面板,即像机构这种树型参照必须有强大的多种模式选择的问题!
	 * @return
	 */
	private JPanel getDragItemTreeItemPanel() {
		JPanel panel = new JPanel(new BorderLayout()); //

		billTreePanel2 = new BillTreePanel(dfvo.getConfValue("模板编码")); //
		if (billTreePanel2.isChecked()) { //如果原来是勾选的!则强行弄成不是勾选的!!!
			billTreePanel2.reSetTreeChecked(false); //
		}

		String queryStr = dfvo.getConfValue("附加SQL条件");
		String str_dataPolicy = dfvo.getConfValue("数据权限策略"); //数据过滤权限！！
		if (str_dataPolicy != null) { //如果有这样一个定义,则强行手工修改模板中的数据权限策略!之所以这么做是因为，如果不这样，则必须为每个策略先配置一个模板！然后依赖更多的模板！如果有这样一个参数,则只需要一个模板!而且可以重用以前的某个模板（哪怕其已经定义了策略）!因为反正我会冲掉之！
			billTreePanel2.setDataPolicy(str_dataPolicy, dfvo.getConfValue("数据权限策略映射")); //
		}
		billTreePanel2.queryDataByCondition(queryStr, getViewOnlyLevel()); //查询数据!!!

		TBUtil tbUtil = new TBUtil(); //

		JPanel rightPanel = new JPanel(new BorderLayout()); //
		billListPanel2 = new BillListPanel(new DefaultTMO("选中的机构", new String[][] { { "id", "50" }, { "名称", "250" } })); //
		billListPanel2.setItemVisible("id", false); //

		if (this.refItemVO != null && refItemVO.getId() != null) { //
			String str_refid = refItemVO.getId(); //
			String str_refname = refItemVO.getName(); //
			String[] str_ids = tbUtil.split(str_refid, ";"); //
			String[] str_names = tbUtil.split(str_refname, ";"); //

			for (int i = 0; i < str_ids.length; i++) {
				int li_newrow = billListPanel2.addEmptyRow(false, false); //
				billListPanel2.setValueAt(new StringItemVO(str_ids[i]), li_newrow, "id"); //
				billListPanel2.setValueAt(new StringItemVO(str_names[i]), li_newrow, "名称"); //
			}
		}
		rightPanel.add(billListPanel2, BorderLayout.CENTER); //

		String[] str_conKeys = dfvo.getAllConfKeys("复杂模式预置组", true); //
		if (str_conKeys != null && str_conKeys.length > 0) {
			checkBoxs = new JCheckBox[str_conKeys.length]; // 
			JPanel btnPanel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.LEFT)); //
			for (int i = 0; i < str_conKeys.length; i++) { //
				String str_value = dfvo.getConfValue(str_conKeys[i]); //
				HashMap itemConMap = tbUtil.convertStrToMapByExpress(str_value, ";", "="); //
				String str_name = (String) itemConMap.get("名称"); //
				checkBoxs[i] = new JCheckBox(str_name); //
				checkBoxs[i].putClientProperty("ConfMap", itemConMap); //
				checkBoxs[i].setToolTipText("<html>按住Ctrl键可以多选!<br>" + itemConMap + "</html>"); //
				checkBoxs[i].addActionListener(this); //
				checkBoxs[i].setOpaque(false); //透明!!
				checkBoxs[i].setFocusable(false); //
				btnPanel.add(checkBoxs[i]); //
			}
			rightPanel.add(btnPanel, BorderLayout.NORTH); //
		}

		rightPanel.add(getCopyBtnPanel(), BorderLayout.WEST); //

		WLTSplitPane split = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, billTreePanel2, rightPanel); //
		split.setDividerLocation(270); //

		panel.add(split); //
		return panel; //
	}

	private JPanel getCopyBtnPanel() {
		bnt_1 = new JButton(" >> ");
		bnt_2 = new JButton(" >  ");
		bnt_3 = new JButton(" >% ");
		bnt_4 = new JButton(" <  ");
		bnt_5 = new JButton(" << ");

		bnt_3.setToolTipText("根据指定条件在选中的范围中过滤!"); //
		//bnt_3.setMargin(new Insets(0, 0, 0, 0)); //

		bnt_1.setPreferredSize(new Dimension(200, 20));
		bnt_2.setPreferredSize(new Dimension(200, 20));
		bnt_3.setPreferredSize(new Dimension(200, 20));
		bnt_4.setPreferredSize(new Dimension(200, 20));
		bnt_5.setPreferredSize(new Dimension(200, 20));

		bnt_1.addActionListener(this); //
		bnt_2.addActionListener(this); //
		bnt_3.addActionListener(this); //
		bnt_4.addActionListener(this); //
		bnt_5.addActionListener(this); //

		Box box = Box.createVerticalBox();
		box.add(Box.createGlue());
		box.add(bnt_1); //
		box.add(Box.createVerticalStrut(10));
		box.add(bnt_2); //
		box.add(Box.createVerticalStrut(10));
		box.add(bnt_3); //
		box.add(Box.createVerticalStrut(10));
		box.add(bnt_4); //
		box.add(Box.createVerticalStrut(10));
		box.add(bnt_5); //
		box.add(Box.createGlue());

		JPanel panel = new WLTPanel(WLTPanel.VERTICAL_OUTSIDE_TO_MIDDLE, new BorderLayout(), LookAndFeel.defaultShadeColor1, false);
		panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0)); //
		panel.setPreferredSize(new Dimension(100, 2000)); //
		panel.add(box);

		return panel; //
	}

	private void onCopyAll() {
		if (!MessageBox.confirm(this, "你真的想选择所有数据么?")) {
			return; //
		}
		doCopyNodes(billTreePanel2.getAllNodes()); //
	}

	//复制选中的结点
	private void onCopySel() { //
		DefaultMutableTreeNode[] selNodes = billTreePanel2.getSelectedNodes(); //
		if (selNodes == null || selNodes.length <= 0) {
			MessageBox.show(this, "请选择一个结点进行此操作!"); //
			return; //
		}

		if (selNodes.length == 1 && selNodes[0].isRoot()) {
			MessageBox.show(this, "不能选择根结点进行拷贝,请选择数据结点!"); //
			return; //
		}
		doCopyNodes(selNodes); //
	}

	private void onCopyCust() {
		DefaultMutableTreeNode[] selNodes = billTreePanel2.getSelectedNodes(); //
		if (selNodes == null || selNodes.length <= 0) {
			MessageBox.show(this, "请选择一个或多个结点进行此操作!"); //
			return; //
		}

		//弹出一个窗口
		if (copySelGroupDialog != null) {
			copySelGroupDialog.setVisible(true); //
		} else {
			String[] str_defs = dfvo.getAllConfKeys("复杂模式选择组", true); //
			String[][] str_allDefs = new String[str_defs.length][2]; //new String[][] { { "法律合规条线", "合规/法律/风险" }, { "计财条线", "计划财务部/计财部" }}; //
			for (int i = 0; i < str_defs.length; i++) {
				String str_defValue = dfvo.getConfValue(str_defs[i]); //
				HashMap defMap = tbUtil.convertStrToMapByExpress(str_defValue, ";", "="); //解析!
				str_allDefs[i][0] = (String) defMap.get("名称"); //
				str_allDefs[i][1] = (String) defMap.get("条件"); //
			}

			JPanel panel = WLTPanel.createDefaultPanel(new BorderLayout()); //
			isOnlyFirstCheck = new JCheckBox("限制只取第一层?", true); //
			isOnlyFirstCheck.setToolTipText("如果不限制,则对选中结点的所有子孙进行计算!"); //
			isOnlyFirstCheck.setOpaque(false); //透明
			isOnlyFirstCheck.setFocusable(false);

			JPanel panel_north = new JPanel(new FlowLayout(FlowLayout.CENTER)); //
			panel_north.setOpaque(false);
			copySelGroupConsCheck = new JCheckBox[str_allDefs.length]; //
			for (int i = 0; i < copySelGroupConsCheck.length; i++) { //
				copySelGroupConsCheck[i] = new JCheckBox(str_allDefs[i][0]); //
				copySelGroupConsCheck[i].setToolTipText(str_allDefs[i][1]); //
				copySelGroupConsCheck[i].setOpaque(false); //透明
				copySelGroupConsCheck[i].setFocusable(false); //
				panel_north.add(copySelGroupConsCheck[i]); //
			}
			panel.add(panel_north, BorderLayout.NORTH); //

			JPanel panel_south = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
			panel_south.setOpaque(false);
			JLabel label = new JLabel("自定义条件", SwingConstants.RIGHT); //
			label.setPreferredSize(new Dimension(60, 22)); //
			label.setToolTipText("除了预置的条线外,还可以临时新加一些条件!比如:计财/人力资源"); //
			copySelGroupCustConText = new JTextField(); //
			copySelGroupCustConText.setToolTipText("比如:合规/风险/法律/计财");
			copySelGroupCustConText.setPreferredSize(new Dimension(200, 22)); //

			panel_south.add(label); //
			panel_south.add(copySelGroupCustConText); //
			panel_south.add(isOnlyFirstCheck); //

			panel.add(panel_south, BorderLayout.SOUTH); //
			int li_width = str_allDefs.length * 85 + 100;
			if (li_width < 450) {
				li_width = 450;
			}
			copySelGroupDialog = new BillDialog(this, "选择组条件", li_width, str_allDefs.length > 0 ? 135 : 100); //
			copySelGroupDialog.getContentPane().add(panel); //
			copySelGroupDialog.addConfirmButtonPanel(); //
			copySelGroupDialog.setVisible(true); //
		}

		if (copySelGroupDialog.getCloseType() != 1) {
			return;
		}

		ArrayList al_sels = new ArrayList(); //
		for (int i = 0; i < copySelGroupConsCheck.length; i++) {
			if (copySelGroupConsCheck[i].isSelected()) {
				al_sels.add(copySelGroupConsCheck[i].getToolTipText()); //
			}
		}
		if (!copySelGroupCustConText.getText().trim().equals("")) {
			al_sels.add(copySelGroupCustConText.getText().trim()); //
		}

		if (al_sels.size() <= 0) {
			MessageBox.show(this, "请至少选择或输入一个条件!"); //
			return; //
		}

		ArrayList aldefAll = new ArrayList(); //
		for (int i = 0; i < al_sels.size(); i++) {
			String[] str_items = tbUtil.split((String) al_sels.get(i), "/"); //
			aldefAll.addAll(Arrays.asList(str_items)); //
		}
		String[] str_text = (String[]) aldefAll.toArray(new String[0]); //

		//根据弹出窗口的返回值进行计算!
		boolean isOnlyFirstLevelChild = isOnlyFirstCheck.isSelected(); //
		HashSet hst_childs = new LinkedHashSet(); //
		if (isOnlyFirstLevelChild) { //如果只取第一层!
			for (int i = 0; i < selNodes.length; i++) {
				for (int j = 0; j < selNodes[i].getChildCount(); j++) { //遍历一级子结点!
					DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) selNodes[i].getChildAt(j); //
					hst_childs.add(childNode); //
				}
			}
		} else { //
			for (int i = 0; i < selNodes.length; i++) {
				DefaultMutableTreeNode[] allChilds = billTreePanel2.getOneNodeChildPathNodes(selNodes[i], false); //
				for (int j = 0; j < allChilds.length; j++) {
					hst_childs.add(allChilds[j]); //
				}
			}
		}
		DefaultMutableTreeNode[] allSelNodeChildrens = (DefaultMutableTreeNode[]) hst_childs.toArray(new DefaultMutableTreeNode[0]); //所有子结点!

		HashSet hst = getOneNodeAllChildNodesByCons(allSelNodeChildrens, -1, str_text, null); //
		DefaultMutableTreeNode[] allNodes = (DefaultMutableTreeNode[]) hst.toArray(new DefaultMutableTreeNode[0]); //
		billListPanel2.clearTable(); //先清空数据!!!
		doCopyNodes(allNodes); //
	}

	//删除选中的!
	private void onDelSel() {
		billListPanel2.removeSelectedRows(); //
	}

	private void onDelAll() {
		billListPanel2.clearTable(); //
	}

	private void doCopyNodes(DefaultMutableTreeNode[] _nodes) {
		boolean isTrimFirstLevel = dfvo.getConfBooleanValue("是否截掉第一层", true);
		for (int i = 0; i < _nodes.length; i++) { //
			if (_nodes[i].isRoot()) {
				continue;
			}
			TreeNode[] allParentNodes = _nodes[i].getPath(); //
			StringBuilder sb_name = new StringBuilder(); // 
			for (int j = 1; j < allParentNodes.length; j++) {
				sb_name.append(((DefaultMutableTreeNode) allParentNodes[j]).getUserObject().toString()); //
				if (j != allParentNodes.length - 1) {
					sb_name.append("-");
				}
			}
			BillVO itemVO = billTreePanel2.getBillVOFromNode(_nodes[i]); //
			if (itemVO.isVirtualNode()) { //如果是虚拟结点则跳过!
				continue;
			}
			String str_id = itemVO.getStringValue(dfvo.getConfValue("ID字段", "id")); ///
			String str_name = sb_name.toString(); //
			if (isTrimFirstLevel && str_name.indexOf("-") > 0) {
				str_name = str_name.substring(str_name.indexOf("-") + 1, str_name.length()); //
			}
			int li_newrow = billListPanel2.addEmptyRow(false, false); //
			billListPanel2.setValueAt(new StringItemVO(str_id), li_newrow, "id"); //
			billListPanel2.setValueAt(new StringItemVO(str_name), li_newrow, "名称"); //
		}
	}

	//截取第几层!
	private int getViewOnlyLevel() {
		int li_level = 0; //
		if (dfvo.getConfValue("只留前几层") != null) { //
			try {
				li_level = Integer.parseInt(dfvo.getConfValue("只留前几层")); //
			} catch (Exception e) {
				e.printStackTrace(); //可能数字不合法,则吃掉异常，即当0处理,保证界面能出来.
			}
		}
		return li_level; //
	}

	public JPanel getSouthPanel() {
		if (southPanel != null) {
			return southPanel;
		}
		southPanel = WLTPanel.createDefaultPanel(new FlowLayout(), BackGroundDrawingUtil.HORIZONTAL_FROM_MIDDLE);
		btn_cancel = new WLTButton("取消");
		btn_cancel.addActionListener(this); //
		btn_confirm = new WLTButton("确定");
		btn_confirm.addActionListener(this); //
		southPanel.add(btn_confirm); // 
		southPanel.add(btn_cancel); //
		return southPanel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) { //如果是确定则返回数据
			onConfirm(); //确认!!!
		} else if (e.getSource() == btn_cancel) {
			onCancel(); //取消
		} else if (e.getSource() == bnt_1) { //所有数据
			onCopyAll(); //
		} else if (e.getSource() == bnt_2) { //拷贝所有数据
			onCopySel(); //
		} else if (e.getSource() == bnt_3) { //
			onCopyCust(); //选中指定的!
		} else if (e.getSource() == bnt_4) { //
			onDelSel(); //删除选中的
		} else if (e.getSource() == bnt_5) { //
			onDelAll(); //删除所有
		} else if (e.getSource() instanceof JCheckBox) { //如果点的是勾选框!
			if (e.getModifiers() == 17 || e.getModifiers() == 18) { //如果按住了Ctrl或Shift键,则,可以多选!
			} else { //否则先取消掉其他选项!
				JCheckBox checkBox = (JCheckBox) e.getSource();
				for (int i = 0; i < checkBoxs.length; i++) {
					if (checkBoxs[i] != checkBox) {
						checkBoxs[i].setSelected(false); //
					}
				}
			}
			onQuickChooseDept(); //
		}
	}

	//快速选择机构!!!
	private void onQuickChooseDept() {
		billListPanel2.clearTable(); //先清空数据!!!
		DefaultMutableTreeNode rootNode = billTreePanel2.getRootNode(); //
		if (tree2AllNodes == null) {
			tree2AllNodes = billTreePanel2.getAllNodes(); //
		}
		HashSet hstAll = new LinkedHashSet(); //
		for (int i = 0; i < checkBoxs.length; i++) {
			if (checkBoxs[i].isSelected()) { //如果已经勾选了
				HashMap confMap = (HashMap) checkBoxs[i].getClientProperty("ConfMap"); //
				String str_startRoot = (String) confMap.get("起点"); //"名称=总行各部门;起点=12;层级=2;条件=风险/合规/计划"
				int li_level = -1; //
				if (confMap.containsKey("层级")) {
					li_level = Integer.parseInt((String) confMap.get("层级")); //
				}

				String[] str_texts = null; //
				if (confMap.containsKey("条件")) {
					str_texts = tbUtil.split((String) confMap.get("条件"), "/"); //
				}

				String[] str_exceptTexts = null; //
				if (confMap.containsKey("除去")) {
					str_exceptTexts = tbUtil.split((String) confMap.get("除去"), "/"); //
				}

				if (str_startRoot == null) {
					HashSet hst = getOneNodeAllChildNodesByCons(tree2AllNodes, li_level, str_texts, str_exceptTexts); //
					hstAll.addAll(hst); //
				} else {
					DefaultMutableTreeNode[] startRootNodes = findStartNodes(tree2AllNodes, str_startRoot); //
					for (int j = 0; j < startRootNodes.length; j++) {
						DefaultMutableTreeNode[] allchildNodes = billTreePanel2.getOneNodeChildPathNodes(startRootNodes[j], false); //
						HashSet hst = getOneNodeAllChildNodesByCons(allchildNodes, li_level, str_texts, str_exceptTexts); //
						hstAll.addAll(hst); //
					}
				}

			}
		}

		DefaultMutableTreeNode[] allnodes = (DefaultMutableTreeNode[]) hstAll.toArray(new DefaultMutableTreeNode[0]); //
		doCopyNodes(allnodes); //拷贝过去!
	}

	//找到某个些启动结点!如果只有一个条件,实际上只会返回一个!
	private DefaultMutableTreeNode[] findStartNodes(DefaultMutableTreeNode[] _allNodes, String _startRoot) {
		HashSet hst = new LinkedHashSet(); //
		String[] str_rootIds = tbUtil.split(_startRoot, "/"); //可能是[12/15/17]
		for (int i = 0; i < str_rootIds.length; i++) { //
			String str_id = str_rootIds[i]; //
			for (int j = 0; j < _allNodes.length; j++) { //遍历所有结点!!!
				BillVO billVO = billTreePanel2.getBillVOFromNode(_allNodes[j]); //
				if (billVO != null) {
					String str_idValue = billVO.getStringValue(dfvo.getConfValue("ID字段", "id")); ///
					if (str_idValue != null && str_idValue.equals(str_id)) {
						hst.add(_allNodes[j]); //
						break; //
					}
				}
			}
		}
		DefaultMutableTreeNode[] nodes = (DefaultMutableTreeNode[]) hst.toArray(new DefaultMutableTreeNode[0]); //
		return nodes;
	}

	//根据条件!取得一个结点下面所有子孙结点
	private HashSet getOneNodeAllChildNodesByCons(DefaultMutableTreeNode[] allChildNodes, int _level, String[] _textItems, String[] _exceptTextItems) {
		LinkedHashSet hst = new LinkedHashSet(); ////
		//DefaultMutableTreeNode[] allChildNodes = billTreePanel2.getOneNodeChildPathNodes(_rootNode, false); //

		for (int i = 0; i < allChildNodes.length; i++) {
			boolean isLevelSucc = true; //
			if (_level > 0) { //如果定义了!
				if (allChildNodes[i].getLevel() == _level) {
					isLevelSucc = true; //对上了
				} else {
					isLevelSucc = false; //没对上!
				}
			}

			boolean isTextSucc = true; //
			String str_nodeText = allChildNodes[i].getUserObject().toString(); //
			if (_textItems != null) {
				boolean isMatched = false; //
				for (int j = 0; j < _textItems.length; j++) {
					if (str_nodeText.indexOf(_textItems[j]) >= 0) { //只要有一个条件对上了就是对上!否则就还是否!!!
						isMatched = true; //
						break; //
					}
				}
				isTextSucc = isMatched; //
			}

			if (_exceptTextItems != null && isTextSucc) { //如果定义了除去的条件,且原来计算出来为true
				TreeNode[] thisParentNodes = allChildNodes[i].getPath(); //
				StringBuilder sb_fulltext = new StringBuilder(); //
				for (int p = 0; p < thisParentNodes.length; p++) {
					sb_fulltext.append(((DefaultMutableTreeNode) thisParentNodes[p]).getUserObject().toString()); //
					if (p != thisParentNodes.length - 1) {
						sb_fulltext.append("-"); //
					}
				}
				String str_fulltext = sb_fulltext.toString(); //
				for (int j = 0; j < _exceptTextItems.length; j++) {
					if (str_fulltext.indexOf(_exceptTextItems[j]) >= 0) { //只要有一个条件对上了就是对上就不行!
						isTextSucc = false; //再次变成否!
						break; //
					}
				}
			}

			if (isLevelSucc && isTextSucc) {
				hst.add(allChildNodes[i]); //
			}
		}

		return hst; //
	}

	private void onConfirm() {
		if (this.tabbedPanel == null) { //如果没有多页签,则肯定是第一个!
			onConfirmTab_1(); //
		} else {
			if (tabbedPanel.getSelectedIndex() == 0) {
				onConfirmTab_1(); //
			} else if (tabbedPanel.getSelectedIndex() == 1) {
				onConfirmTab_2(); //
			}
		}
	}

	//点击确认!
	private void onConfirmTab_1() {
		String str_id_field = dfvo.getConfValue("ID字段"); //
		String str_name_field = dfvo.getConfValue("NAME字段"); //
		boolean isTrimFirstLevel = dfvo.getConfBooleanValue("是否截掉第一层", true); //是否截掉第一层??默认是截的!
		boolean isReturnPathName = dfvo.getConfBooleanValue("返回路径链名", true); //以前默认是否,后来发现实际上绝大多数都是要返回全路径的,干脆默认是true
		boolean isOnlyChooseLeafNode = dfvo.getConfBooleanValue("只能选叶子", false); //只能选叶子结点,默认是否

		if (str_id_field == null) {
			str_id_field = billTreePanel.getTempletVO().getPkname(); //
		}
		if (str_id_field == null) {
			MessageBox.show(this, "公式中没有定义返回的ID字段名,模板中也没有定义表的主键！"); //
			return; //
		}
		if (!billTreePanel.getTempletVO().containsItemKey(str_id_field)) { //
			MessageBox.show(this, "指定从【" + str_id_field + "】字段返回参照ID,但模板中没有该项！"); //
			return; //
		}

		if (billTreePanel.isChecked() && billTreePanel.isDefaultLinkedCheck() && ClientEnvironment.isAdmin()) { //如果发现树是联动的,且又是多选的!!则来一个强烈提醒!!
			MessageBox.show(this, getHelpMsg_1()); //

		}
		if (!billTreePanel.isChecked()) { //如果不是多选情况,即单选的
			BillVO billvo = billTreePanel.getSelectedVO(); //选中一条记录!!!
			if (billvo == null) {
				MessageBox.showSelectOne(this); //
				return; //
			}
			if (isOnlyChooseLeafNode) { //判断是否只能选择叶子结点!!!
				if (!billTreePanel.getSelectedNode().isLeaf()) {
					MessageBox.show(this, "只能选择叶子结点,即最末级的结点!"); //
					return; //
				}
			}

			if (billvo.isVirtualNode()) { //虚拟结点也跳过!,先不处理!因为可能录入时本身就有必须是叶子结点的约束!!
				MessageBox.show(this, "斜体字体的结点不可以选择!\r\n这是因为根据权限计算,您没有选择该结点的权利!\r\n但为了保证完整显示树型路径而虚拟计算出来的,即必须看到,但又不能选择!"); //
				return; //
			}

			HashVO hvo = billvo.convertToHashVO(); //转换一下!!!
			hvo.setAttributeValue("$ReturnPathName", hvo.getStringValue("$parentpathname")); //赋一个指定的特殊变量!!其实只要一个就行了,这里是为了兼容旧的!

			//创建返回RefVO
			returnRefItemVO = new RefItemVO(hvo); //
			//处理返回vo的id
			returnRefItemVO.setId(billvo.getStringValue(str_id_field)); //

			//以前还有个判断[返回路径链名],现在统一强行返回路径!!
			String str_pathName = hvo.getStringValue("$parentpathname"); //强行永远带路径!!!
			if (isTrimFirstLevel && str_pathName != null && str_pathName.indexOf("-") > 0) { //如果要裁掉第一层!
				str_pathName = str_pathName.substring(str_pathName.indexOf("-") + 1, str_pathName.length()); //
			}
			returnRefItemVO.setName(str_pathName); //直接将看到的结点名称返回!!(String) billvo.getUserObject("$ParentPathName")
		} else { //如果是多选的..
			BillVO[] billVOs = billTreePanel.getCheckedVOs(); //新的机制有了个$ParentPathNames用户字段在里面!!
			if (billVOs == null || billVOs.length == 0) {
				MessageBox.show(this, "请至少勾选一条数据!\r\n温馨提示:这是可以多选的,点击结点前的那个勾选框,才算真正选中!"); //
				return; //
			}

			//将名称的全路径搞上去!
			if (dfvo.getConfBooleanValue("只能选叶子", false)) { //只能选叶子叶点,则要校验!!
				DefaultMutableTreeNode[] allCheckedNodes = billTreePanel.getCheckedNodes(); //
				for (int i = 0; i < allCheckedNodes.length; i++) {
					if (!allCheckedNodes[i].isLeaf()) {
						MessageBox.show(this, "只能选择叶子结点,即最末级的结点!"); //
						return; //
					}
				}
			}

			returnRefItemVO = new RefItemVO(); //
			StringBuffer sb_ids = new StringBuffer(";");
			StringBuffer sb_names = new StringBuffer(); //
			int virtualmark = 0; //虚拟节点标记 【杨科/2012-09-10】

			//循环处理!!!
			for (int i = 0; i < billVOs.length; i++) { //遍历所有对象!!
				if (billVOs[i] == null) {
					continue;
				}

				//虚拟结点不让选择!
				if (billVOs[i].isVirtualNode()) { //虚拟结点也跳过!,先不处理!因为可能录入时本身就有必须是叶子结点的约束!!
					//MessageBox.show(this, "斜体字体的结点不可以选择!\r\n这是因为根据权限计算,您没有选择该结点的权利!\r\n但为了保证完整显示树型路径而虚拟计算出来的,即必须看到,但又不能选择!"); //
					virtualmark++;
					continue;
				}
				sb_ids.append(billVOs[i].getStringValue(str_id_field) + ";"); //
				String str_pathName = "";
				if (isReturnPathName) {
					str_pathName = (String) billVOs[i].getUserObject("$ParentPathName"); //强行永远带路径!!!
					if (isTrimFirstLevel && str_pathName != null && str_pathName.indexOf("-") > 0) { //如果要裁掉第一层!
						str_pathName = str_pathName.substring(str_pathName.indexOf("-") + 1, str_pathName.length()); //
					}
				}else{ //如果不返回链路名称
					str_pathName = billVOs[i].getStringValue(str_name_field);
				}
				sb_names.append(str_pathName + ";");
			}

			if (billVOs.length > 0 && virtualmark == billVOs.length) {
				MessageBox.show(this, "斜体字体的结点不可以选择!\r\n这是因为根据权限计算,您没有选择该结点的权利!\r\n但为了保证完整显示树型路径而虚拟计算出来的,即必须看到,但又不能选择!");
			}

			returnRefItemVO.setId(sb_ids.toString()); //
			returnRefItemVO.setName(sb_names.toString()); //
		}
		this.setCloseType(BillDialog.CONFIRM);
		this.dispose(); //
	}

	private void onConfirmTab_2() {
		BillVO[] billVOs = billListPanel2.getAllBillVOs(); //
		if (billVOs == null || billVOs.length <= 0) {
			MessageBox.show(this, "请选择一条记录!"); //
			return;
		}

		returnRefItemVO = new RefItemVO(); //
		StringBuffer sb_ids = new StringBuffer(";");
		StringBuffer sb_names = new StringBuffer(); //
		for (int i = 0; i < billVOs.length; i++) { //遍历所有对象!!
			sb_ids.append(billVOs[i].getStringValue("id") + ";"); //
			sb_names.append(billVOs[i].getStringValue("名称") + ";");
		}

		returnRefItemVO.setId(sb_ids.toString()); //
		returnRefItemVO.setName(sb_names.toString()); //
		this.setCloseType(BillDialog.CONFIRM);
		this.dispose(); //
	}

	//点击取消!!!
	private void onCancel() {
		returnRefItemVO = null; //
		this.setCloseType(BillDialog.CANCEL);
		this.dispose(); //
	}

	@Override
	public RefItemVO getReturnRefItemVO() {
		return returnRefItemVO;
	}

	/**
	 * 初始宽度
	 * @return
	 */
	public int getInitWidth() {
		return 500;
	}

	public int getInitHeight() {
		return 500;
	}

	private String getHelpMsg_1() {
		StringBuilder sb_help = new StringBuilder(); //
		sb_help.append("发现当前树形控件为多选模式,并且为连动勾选,建议不要这样！\r\n"); //
		sb_help.append("可以通过参数[\"是否连动勾选\",\"N\"]来修改！\r\n");
		sb_help.append("\r\n");
		return sb_help.toString(); //
	}

}
