package cn.com.infostrategy.ui.report;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 万维报表选择自定义维度时,弹出的选择维度对话框!
 * @author xch
 *
 */
public class MultiLevelChooseGroupDialog extends BillDialog implements ActionListener {

	private static final long serialVersionUID = 6210230660775593956L;

	private BillListPanel billlistPanel_all, billlistPanel_row, billlistPanel_col, billlistPanel_sum; //
	private JComboBox row_groupTypeComBox, col_groupTypeComBox;
	private JCheckBox row_isSubtotal, col_isSubtotal; //

	private FormulaDialog formulaDialog = null; //定义公式维度窗口 【杨科/2012-08-21】

	private JRadioButton btn_grid, btn_chart; //

	private String[] all_groupname = null; //
	private String[] all_canSumNname = null; //

	private WLTButton btn_confirm, btn_cancel;
	private WLTButton btn_code; //追加查看源码 【杨科/2012-08-13】
	private int closeType = -1;

	private String[] rowGroupnames = null;
	private String[] colGroupnames = null; //
	private String[][] computeFunAndFields = null;
	private String reportType = "GRID"; //返回的图表类型
	private JCheckBox field_sort; //追加是否按计算值排序? 【杨科/2012-08-21】

	private WLTButton btn_formulaGroup, btn_extconfHelp = null; //扩展配置帮助说明!

	public MultiLevelChooseGroupDialog(Container _parent, String[] _allgroupname, String[] _allCanSumFieldName) {
		super(_parent, "自定义选择维度", 800, 800); //
		this.all_groupname = _allgroupname; //
		this.all_canSumNname = _allCanSumFieldName; //
		initialize();
	}

	/**
	 * 初始页面..
	 */
	private void initialize() {
		this.getContentPane().setLayout(new BorderLayout()); //
		try {
			billlistPanel_all = new BillListPanel(new DefaultTMO("可选维度", new String[][] { { "name", "维度名称", "138" } })); //
			billlistPanel_row = new BillListPanel(new DefaultTMO("上表头维度", new String[][] { { "name", "维度名称", "100" }, { "formulagroup", "公式维度定义", "250" } })); //
			billlistPanel_col = new BillListPanel(new DefaultTMO("左表头维度", new String[][] { { "name", "维度名称", "138" } })); //

			billlistPanel_all.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			billlistPanel_row.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			billlistPanel_col.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			btn_formulaGroup = new WLTButton("定义公式维度");
			btn_formulaGroup.addActionListener(this); //
			billlistPanel_row.addBatchBillListButton(new WLTButton[] { WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEUP), WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEDOWN), btn_formulaGroup }); //
			billlistPanel_row.repaintBillListButton(); //

			billlistPanel_col.addBatchBillListButton(new WLTButton[] { WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEUP), WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEDOWN) }); //
			billlistPanel_col.repaintBillListButton(); //

			JPanel row_custPanel = new JPanel(new FlowLayout());
			row_custPanel.setOpaque(false); //
			row_groupTypeComBox = new JComboBox(new String[] { "多层次", "平铺" }); //
			row_groupTypeComBox.setPreferredSize(new Dimension(75, 20)); //
			row_isSubtotal = new JCheckBox("是否小计", true);
			row_isSubtotal.setOpaque(false);
			row_custPanel.add(row_groupTypeComBox); //
			row_custPanel.add(row_isSubtotal); //
			billlistPanel_row.setCustomerNavigationJPanel(row_custPanel); //

			JPanel col_custPanel = new JPanel(new FlowLayout());
			col_custPanel.setOpaque(false); //
			col_groupTypeComBox = new JComboBox(new String[] { "多层次", "平铺" }); //
			col_groupTypeComBox.setPreferredSize(new Dimension(75, 20)); //
			col_isSubtotal = new JCheckBox("是否小计", true);
			col_isSubtotal.setOpaque(false);
			col_custPanel.add(col_groupTypeComBox); //
			col_custPanel.add(col_isSubtotal); //
			billlistPanel_col.setCustomerNavigationJPanel(col_custPanel); //

			billlistPanel_all.setItemEditable(false);
			billlistPanel_row.setItemEditable(false);
			billlistPanel_col.setItemEditable(false);

			billlistPanel_all.setCanShowCardInfo(false); //
			billlistPanel_row.setCanShowCardInfo(false); //
			billlistPanel_col.setCanShowCardInfo(false); //

			//billlistPanel_row.setItemEditable("formulagroup", true); //

			billlistPanel_all.setTableToolTipText("选择记录直接拖动到右面或下面的框"); //

			billlistPanel_all.getBillListBtn("加入行").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onAddRow();
				}
			});

			billlistPanel_all.getBillListBtn("加入列").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onAddCol();
				}
			});

			billlistPanel_row.getBillListBtn("移去").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onRemoveRow();
				}
			});

			billlistPanel_col.getBillListBtn("移去").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onRemoveCol();
				}
			});

			WLTSplitPane splitPanel_1 = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, billlistPanel_all, billlistPanel_row); //
			WLTSplitPane splitPanel_2 = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, billlistPanel_col, getBillListPanel_sum()); //

			splitPanel_1.setDividerLocation(335); //
			splitPanel_2.setDividerLocation(335); //

			WLTSplitPane splitPanel_3 = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, splitPanel_1, splitPanel_2); //
			splitPanel_3.setDividerLocation(280); //

			for (int i = 0; i < all_groupname.length; i++) {
				int li_newrow = billlistPanel_all.addEmptyRow(false); //
				billlistPanel_all.setValueAt(new StringItemVO(all_groupname[i]), li_newrow, "name"); //
				billlistPanel_all.clearSelection(); //
				billlistPanel_all.moveToTop(); //
				billlistPanel_all.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT); //
			}

			//有上移下移!
			btn_extconfHelp = new WLTButton("扩展属性说明"); //
			btn_extconfHelp.addActionListener(this); //
			billlistPanel_sum.addBatchBillListButton(new WLTButton[] { WLTButton.createButtonByType(WLTButton.LIST_ROWINSERT), WLTButton.createButtonByType(WLTButton.LIST_DELETEROW), WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEUP), WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEDOWN), btn_extconfHelp }); //
			billlistPanel_sum.repaintBillListButton(); //

			//遍历所有计算列!
			for (int i = 0; i < all_canSumNname.length; i++) {
				int li_newrow_1 = billlistPanel_sum.addEmptyRow(false); //
				billlistPanel_sum.setValueAt(new StringItemVO(i == 0 ? "Y" : "N"), li_newrow_1, "ischecked"); //是否选中,如果是第一行,则默认选中!
				billlistPanel_sum.setValueAt(new ComBoxItemVO(all_canSumNname[i], null, all_canSumNname[i]), li_newrow_1, "fieldname"); //
				if (i == 0) {
					billlistPanel_sum.setValueAt(new ComBoxItemVO("count", "", "记录数"), li_newrow_1, "computefunctionname"); //
				} else {
					billlistPanel_sum.setValueAt(new ComBoxItemVO("sum", "", "求和"), li_newrow_1, "computefunctionname"); //
				}
				billlistPanel_sum.clearSelection(); //
				billlistPanel_sum.moveToTop(); //
				billlistPanel_sum.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT); //

				int li_newrow_2 = billlistPanel_sum.addEmptyRow(false); //
				billlistPanel_sum.setValueAt(new StringItemVO("N"), li_newrow_2, "ischecked"); //是否选中,如果是第一行,则默认选中!
				billlistPanel_sum.setValueAt(new ComBoxItemVO(all_canSumNname[i], null, all_canSumNname[i]), li_newrow_2, "fieldname"); //
				if (i == 0) { //如果是第一个参数,一般都是记录数,所以后面默认是记录数占比
					billlistPanel_sum.setValueAt(new ComBoxItemVO("PercentCount", "", "记录数占比"), li_newrow_2, "computefunctionname"); //
				} else { //按道理后面应该就是求和,所以求和
					billlistPanel_sum.setValueAt(new ComBoxItemVO("PercentSum", "", "求和占比"), li_newrow_2, "computefunctionname"); //
				}
				billlistPanel_sum.clearSelection(); //
				billlistPanel_sum.moveToTop(); //
				billlistPanel_sum.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT); //

				//				int li_newrow_3 = billlistPanel_sum.addEmptyRow(false); //
				//				billlistPanel_sum.setValueAt(new StringItemVO("N"), li_newrow_3, "ischecked"); //是否选中,如果是第一行,则默认选中!
				//				billlistPanel_sum.setValueAt(new StringItemVO(all_canSumNname[i]), li_newrow_3, "fieldname"); //
				//				billlistPanel_sum.clearSelection(); //
				//				billlistPanel_sum.moveToTop(); //
				//				billlistPanel_sum.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT); //
			}

			//增加拖动事件..
			billlistPanel_all.getTable().putClientProperty("dragtype", "ALL"); //
			billlistPanel_row.getTable().putClientProperty("dragtype", "ROW"); //
			billlistPanel_col.getTable().putClientProperty("dragtype", "COL"); //

			new MyDraggListener(billlistPanel_all.getTable()); //增加拖动监听..
			new MyDropListener(billlistPanel_all.getTable()); //

			new MyDraggListener(billlistPanel_row.getTable()); //增加拖动监听..			
			new MyDropListener(billlistPanel_row.getTable()); //

			new MyDraggListener(billlistPanel_col.getTable()); //增加拖动监听..			
			new MyDropListener(billlistPanel_col.getTable()); //

			new MyDropListener(billlistPanel_all.getMainScrollPane()); //
			new MyDropListener(billlistPanel_row.getMainScrollPane()); //
			new MyDropListener(billlistPanel_col.getMainScrollPane()); //

			this.getContentPane().add(splitPanel_3, BorderLayout.CENTER); //
			this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * 分组计算...
	 * @return
	 */
	private BillListPanel getBillListPanel_sum() {
		billlistPanel_sum = new BillListPanel(new TMO_SumField(all_canSumNname)); //
		billlistPanel_sum.isCanShowCardInfo = false;
		return billlistPanel_sum; //
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
		panel.setOpaque(false); //透明!
		btn_grid = new JRadioButton("表格", true); //
		btn_chart = new JRadioButton("图表"); //
		btn_grid.setOpaque(false); //透明
		btn_chart.setOpaque(false); //透明
		btn_grid.setFocusable(false); //
		btn_chart.setFocusable(false); //

		ButtonGroup group = new ButtonGroup(); //
		group.add(btn_grid); //
		group.add(btn_chart); //

		panel.add(new JLabel("报表类型:", SwingConstants.RIGHT)); //
		panel.add(btn_grid); //
		panel.add(btn_chart); //

		field_sort = new JCheckBox("是否按计算值排序", false);
		field_sort.setOpaque(false);
		panel.add(field_sort); //

		JLabel label_info = new JLabel("特别提醒:1.直接鼠标拖动选择维度  2.选择维度返回后,要点击【查询】按钮后数据才会变化"); //
		label_info.setForeground(Color.RED); //
		panel.add(label_info); //

		JPanel panel_btn = new JPanel(new FlowLayout()); //
		panel_btn.setOpaque(false); //透明
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");

		btn_code = new WLTButton("查看源码");

		btn_confirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onConfirm(); //
			}
		}); //

		btn_cancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				onCancel(); //
			}
		}); //

		btn_code.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				onCode();
			}
		});

		panel_btn.add(btn_confirm); //
		panel_btn.add(btn_cancel); //

		if (ClientEnvironment.isAdmin()) {
			panel_btn.add(btn_code);
		}

		JPanel panel_south = WLTPanel.createDefaultPanel(new BorderLayout());
		panel_south.add(panel, BorderLayout.NORTH); //
		panel_south.add(panel_btn, BorderLayout.CENTER); //
		return panel_south;
	}

	protected void onRemoveRow() {
		moveDataBetweenBillListPanel(billlistPanel_row, billlistPanel_all); //
	}

	private void onRemoveCol() {
		moveDataBetweenBillListPanel(billlistPanel_col, billlistPanel_all); //
	}

	private void onAddRow() {
		moveDataBetweenBillListPanel(billlistPanel_all, billlistPanel_row); //
	}

	private void onAddCol() {
		moveDataBetweenBillListPanel(billlistPanel_all, billlistPanel_col); //
	}

	/**
	 * 
	 */
	public void onConfirm() {
		billlistPanel_sum.stopEditing(); //结束编辑状态
		billlistPanel_row.stopEditing(); //
		if (!onConfirmAndCodeBefore()) {
			return;
		}

		closeType = 1; //
		this.dispose();
	}

	private boolean isLikeDateName(String _name) {
		if (_name.indexOf("时间") >= 0 || _name.indexOf("日期") >= 0 || _name.indexOf("季度") >= 0 || _name.indexOf("月") >= 0) {
			return true;
		} else {
			return false;
		}
	}

	public void onCancel() {
		billlistPanel_sum.stopEditing(); //结束编辑状态
		closeType = 2;
		this.dispose();
	}

	public boolean onConfirmAndCodeBefore() { //确定或查看源码赋值 【杨科/2012-08-13】
		int li_count_row = billlistPanel_row.getRowCount();
		int li_count_col = billlistPanel_col.getRowCount();
		if (li_count_row == 0 && li_count_col == 0) {
			MessageBox.show(this, "至少选择一个参与统计的组!"); //
			return false; //
		}

		//统计需要计算的列..
		ArrayList al_ComputeFunctionAndfield = new ArrayList(); //记录哪些参与统计的列
		HashSet hst_dis = new HashSet(); //
		boolean isHaveTBHB = false; //是否有同比或环比
		boolean isChecked = false;//【李春娟/2019-03-21】
		for (int i = 0; i < billlistPanel_sum.getRowCount(); i++) {
			if (billlistPanel_sum.getRealValueAtModel(i, "ischecked").equals("Y")) { //如果选中的
				isChecked = true;//【李春娟/2019-03-21】
				String str_field = billlistPanel_sum.getRealValueAtModel(i, "fieldname").trim(); //
				String str_extconfmap = billlistPanel_sum.getRealValueAtModel(i, "extconfmap"); //
				if (str_extconfmap != null) {
					str_extconfmap = str_extconfmap.trim(); //
				}
				if (str_field == null || str_field.equals("")) {
					MessageBox.show(this, "第[" + (i + 1) + "]条没有指定计算列名,请选择！"); //
					return false; //
				}
				ComBoxItemVO comBoxItemVO = (ComBoxItemVO) billlistPanel_sum.getValueAt(i, "computefunctionname"); //
				if (comBoxItemVO == null || comBoxItemVO.getId() == null || comBoxItemVO.getId().equals("")) {
					MessageBox.show(this, "第[" + (i + 1) + "]条没有指定统计类型,请选择！"); //
					return false; //
				}
				String str_diskey = str_field + "，" + comBoxItemVO.getId(); //
				if (hst_dis.contains(str_diskey)) {
					MessageBox.show(this, "第[" + (i + 1) + "]条与前面发生重复,请重新选择！"); //
					return false; //
				}
				hst_dis.add(str_diskey); //

				//两者校验!
				if (comBoxItemVO.getId().startsWith("Two") && al_ComputeFunctionAndfield.size() < 2) { //如果是两者差额什么的,则必须
					MessageBox.show(this, "第[" + (i + 1) + "]条是计算前两者的差额或比例,但前面却不足两条,请重新选择！"); //
					return false; //
				}

				if (comBoxItemVO.getId().endsWith("ChainIncrease") || comBoxItemVO.getId().endsWith("PeriodIncrease")) { //如果是同比或环比
					isHaveTBHB = true; //
				}

				String str_funname = comBoxItemVO.getId(); //count,sum,avg
				if (str_funname.equals("count") || str_funname.equals("sum")) { //记录数与求和,默认不带后辍
					al_ComputeFunctionAndfield.add(new String[] { str_field, str_funname, str_extconfmap }); //函数名,函数显示名,函数参数!!!
				} else { //其他像平均数,占比的带后辍!
					String str_funviewname = comBoxItemVO.getName();//
					if (str_funviewname.endsWith("占比")) {
						str_funviewname = "占比"; //
					}
					if (str_funviewname.endsWith("同比")) {
						str_funviewname = "同比"; //
					}
					if (str_funviewname.endsWith("环比")) {
						str_funviewname = "环比"; //
					}
					String str_realWeiLi = str_field + "-" + str_funviewname; //
					if (str_funviewname.startsWith("两者")) {
						str_realWeiLi = str_funviewname; //
					}
					al_ComputeFunctionAndfield.add(new String[] { str_realWeiLi, str_funname, str_extconfmap }); //函数名,函数显示名,函数参数!!!
				}
			}
		}
		if (!isChecked) {
			MessageBox.show(this, "至少选择一个分组计算列!"); //修改bug，以前没有选择分组计算列会报错【李春娟/2019-03-21】
			return false; //
		}

		computeFunAndFields = new String[al_ComputeFunctionAndfield.size()][3]; ///
		for (int i = 0; i < al_ComputeFunctionAndfield.size(); i++) {
			computeFunAndFields[i] = (String[]) al_ComputeFunctionAndfield.get(i); //
		}

		if (btn_grid.isSelected()) { //网格,交叉报表....
			reportType = "GRID"; //
		} else if (btn_chart.isSelected()) { //图表....
			reportType = "CHART"; //
			if (li_count_row > 1 || li_count_col > 1) { //如果有一个大于0,是不允许的
				MessageBox.show(this, "做图表时,只能最多选择一个参与分组的列!"); //
				return false; //
			}

			if (li_count_row == 1 && li_count_col == 1) { //如果同时选择了行与列,则只能选择一个参与计算的列,
				if (computeFunAndFields.length > 1) {
					MessageBox.show(this, "在同时选择行与列做图表时,只能选择一个参与统计计算的列!而不能选多个,除非只选择行或列!"); //
					return false; //
				}
			} else { //如果只选择了行或列,则允许选择多个统计计算列,这个功能以前是没有的,即用户有时需要比较某几个机构之间几个金额之间的比较,比如,销售毛利,税后利润,纯利润!以前没这个功能,总觉得缺点什么!!
				//图表在只选择
			}
		}

		boolean isHaveDateGroup = false; //
		rowGroupnames = new String[li_count_row];
		for (int i = 0; i < rowGroupnames.length; i++) {
			rowGroupnames[i] = "" + billlistPanel_row.getValueAt(i, "name"); //
			if (isLikeDateName(rowGroupnames[i])) {
				isHaveDateGroup = true; //
			}
		}

		colGroupnames = new String[li_count_col];
		for (int i = 0; i < colGroupnames.length; i++) {
			colGroupnames[i] = "" + billlistPanel_col.getValueAt(i, "name");
			if (isLikeDateName(colGroupnames[i])) {
				isHaveDateGroup = true; //
			}
		}

		if (isHaveTBHB && !isHaveDateGroup) {
			if (!MessageBox.confirm(this, "您选择一个环比/同比计算类型,但似乎分组维度中没有一个是时间类型!\r\n这有可能导致计算值为空,是否继续？")) {
				return false;
			}
		}

		return true;
	}

	public void onCode() { //查看源码 【杨科/2012-08-13】
		if (!onConfirmAndCodeBefore()) {
			return;
		}

		StringBuffer sb_codes = new StringBuffer();
		StringBuffer sb_name = new StringBuffer();
		StringBuffer sb_row = new StringBuffer();
		StringBuffer sb_col = new StringBuffer();
		StringBuffer sb_compute = new StringBuffer();

		String[] chooseRowFields = getRowGroupnames(); //行头
		String[] chooseColFields = getColGroupnames(); //列头
		String[][] chooseComputeFields = getComputeFunAndFields(); //计算

		for (int i = 0; i < chooseColFields.length; i++) {
			sb_name.append(chooseColFields[i] + "-");
			sb_col.append("\"" + chooseColFields[i] + "\", ");
		}

		for (int i = 0; i < chooseRowFields.length; i++) {
			sb_name.append(chooseRowFields[i] + "-");
			sb_row.append("\"" + chooseRowFields[i] + "\", ");
		}

		String str_name = sb_name.toString(); //报表名
		if (str_name.endsWith("-")) {
			str_name = str_name.substring(0, str_name.length() - 1);
		}

		String str_row = sb_row.toString(); //行头
		if (str_row.endsWith(", ")) {
			str_row = str_row.substring(0, str_row.length() - 2);
		}

		String str_col = sb_col.toString(); //列头
		if (str_col.endsWith(", ")) {
			str_col = str_col.substring(0, str_col.length() - 2);
		}

		for (int i = 0; i < chooseComputeFields.length; i++) {
			sb_compute.append("{ ");

			StringBuffer sb_compute_temp = new StringBuffer();
			String[] tempComputeFields = chooseComputeFields[i];
			for (int j = 0; j < tempComputeFields.length; j++) {
				if (tempComputeFields[j] != null) {
					sb_compute_temp.append("\"" + tempComputeFields[j] + "\", ");
				}
			}

			String str_compute_temp = sb_compute_temp.toString();
			if (str_compute_temp.endsWith(", ")) {
				str_compute_temp = str_compute_temp.substring(0, str_compute_temp.length() - 2);
			}

			sb_compute.append(str_compute_temp);
			sb_compute.append(" }, ");
		}

		String str_compute = sb_compute.toString(); //计算
		if (str_compute.endsWith(", ")) {
			str_compute = str_compute.substring(0, str_compute.length() - 2);
		}

		String str_reportType = getReportType(); //报表类型

		sb_codes.append("ArrayList al_vos = new ArrayList(); \r\n");
		sb_codes.append("BeforeHandGroupTypeVO bhGroupVO = null; \r\n");
		sb_codes.append("\r\n");
		sb_codes.append("bhGroupVO = new BeforeHandGroupTypeVO(); \r\n");
		sb_codes.append("bhGroupVO.setName((al_vos.size() + 1 + \"-" + str_name + "\")); \r\n");
		if (str_reportType.equals("GRID")) {
			sb_codes.append("bhGroupVO.setRowHeaderGroupFields(new String[] { " + str_row + " }); \r\n");
			sb_codes.append("bhGroupVO.setColHeaderGroupFields(new String[] { " + str_col + " }); \r\n");
		}
		if (str_reportType.equals("CHART")) { //图表行与列互换
			sb_codes.append("bhGroupVO.setRowHeaderGroupFields(new String[] { " + str_col + " }); \r\n");
			sb_codes.append("bhGroupVO.setColHeaderGroupFields(new String[] { " + str_row + " }); \r\n");
		}
		sb_codes.append("bhGroupVO.setComputeGroupFields(new String[][] { " + str_compute + " }); \r\n");
		if (isRowGroupTiled()) {
			sb_codes.append("bhGroupVO.setRowGroupTiled(true); \r\n"); //行头是否是平铺
		}
		if (!isRowGroupSubTotal()) {
			sb_codes.append("bhGroupVO.setRowGroupSubTotal(false); \r\n"); //行头是否有小计
		}
		if (isColGroupTiled()) {
			sb_codes.append("bhGroupVO.setColGroupTiled(true); \r\n"); //列头是否是平铺
		}
		if (!isColGroupSubTotal()) {
			sb_codes.append("bhGroupVO.setColGroupSubTotal(false); \r\n"); //列头是否有小计?		
		}
		sb_codes.append("bhGroupVO.setType(\"" + str_reportType + "\"); \r\n");
		sb_codes.append("al_vos.add(bhGroupVO); \r\n");
		sb_codes.append("\r\n");
		sb_codes.append("return (BeforeHandGroupTypeVO[]) al_vos.toArray(new BeforeHandGroupTypeVO[0]); \r\n");

		MessageBox.showTextArea(this, "维度选择源码", sb_codes.toString());
	}

	public String[] getRowGroupnames() {
		return rowGroupnames;
	}

	public String[] getColGroupnames() {
		return colGroupnames;
	}

	public String[][] getComputeFunAndFields() {
		return computeFunAndFields;
	}

	public String getReportType() {
		return reportType;
	}

	public HashMap getRowHeaderFormulaMap() {
		if (billlistPanel_row.getRowCount() <= 0) {
			return null; //
		}
		HashMap map = new HashMap(); //
		for (int i = 0; i < billlistPanel_row.getRowCount(); i++) {
			String str_groupName = billlistPanel_row.getRealValueAtModel(i, "name"); //维护名称
			String str_formula = billlistPanel_row.getRealValueAtModel(i, "formulagroup"); //公式定义
			if (str_formula != null && !str_formula.trim().equals("")) { //如果不为空!!
				String[] str_items = TBUtil.getTBUtil().split(str_formula, "■"); //
				String[][] str_def = new String[str_items.length][3]; //
				for (int j = 0; j < str_items.length; j++) {
					str_def[j] = TBUtil.getTBUtil().split(str_items[j], "▲"); //
				}
				//String[][] str_def = new String[]
				//str_formula
				map.put(str_groupName, str_def); //
			}
		}
		return map; //
	}

	//行头组是否平铺?
	public boolean isRowGroupTiled() {
		return (row_groupTypeComBox.getSelectedIndex() == 1 ? true : false);
	}

	//列头组是否平铺?
	public boolean isColGroupTiled() {
		return (col_groupTypeComBox.getSelectedIndex() == 1 ? true : false);
	}

	//行头组是否小计?
	public boolean isRowGroupSubTotal() {
		return row_isSubtotal.isSelected(); //
	}

	//是否按计算值排序?
	public boolean isSortByCpValue() {
		return field_sort.isSelected(); //
	}

	//列头组是否小计?
	public boolean isColGroupSubTotal() {
		return col_isSubtotal.isSelected(); //
	}

	public int getCloseType() {
		return closeType;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_extconfHelp) {
			onShowExtConfHelp(); //
		} else if (e.getSource() == btn_formulaGroup) { //新增公式维度
			onDefineNewFormulaGroup(); //
		}
	}

	/**
	 * 在原来的维度后面可以新增一些公式维度!
	 */
	private void onDefineNewFormulaGroup() {
		int li_row = billlistPanel_row.getSelectedRow(); //
		if (li_row < 0) {
			MessageBox.show(this, "请选择一条记录进行操作!"); //
			return; //
		}

		//弹出一个表格,表格中有行增加,行删除,有确定,然后先将这里的数据解析带到表格中!返回时重新拼接成字符串! 【杨科/2012-08-21】	
		formulaDialog = new FormulaDialog(this, li_row);
		formulaDialog.setVisible(true);
	}

	//显示扩展参数帮助
	private void onShowExtConfHelp() {
		StringBuilder sb_help = new StringBuilder(); //
		sb_help.append("扩展参数可以指定更多属性,常见配置语法如下:\r\n"); //
		sb_help.append("显示名称=与去年同期比率\r\n"); //
		sb_help.append("是否带百分号=Y\r\n"); //
		sb_help.append("警界规则=300,150,100\r\n"); //
		sb_help.append("警界规则={平均数}*2,{平均数}*1.5,{平均数}\r\n"); //

		sb_help.append("\r\n"); //

		sb_help.append("可以多个参数联合配置,之间用分号相隔\r\n"); //
		sb_help.append("警界值可以有多个,之间用逗号相隔,警界颜色分别是红橙黄绿\r\n"); //
		sb_help.append("警界值支持公式计算,支持宏代码有:{平均数}\r\n"); //
		MessageBox.show(this, sb_help.toString()); //
	}

	/**
	 * 拖动列表..
	 * @param _source
	 * @param _target
	 */
	private void moveDataBetweenBillListPanel(BillListPanel _source, BillListPanel _target) {
		if (_source == _target) {
			return; //如果是一个东西,则返回!
		}

		//System.out.println("移数据了!!");
		BillVO[] billVOs = _source.getSelectedBillVOs(); //
		for (int i = 0; i < billVOs.length; i++) {
			int li_row = _target.addEmptyRow(); //
			_target.setBillVOAt(li_row, billVOs[i].deepClone()); //向目标中加入!!!
		}
		_source.removeSelectedRows();
		_source.clearSelection(); //
	}

	class MyDropListener implements DropTargetListener {
		public MyDropListener(JComponent _component) {
			new DropTarget(_component, this); //
		}

		public void dragEnter(DropTargetDragEvent evt) {
			// Called when the user is dragging and enters this drop target.
		}

		public void dragOver(DropTargetDragEvent evt) {
			// Called when the user is dragging and moves over this drop target.
		}

		public void dragExit(DropTargetEvent evt) {
			// Called when the user is dragging and leaves this drop target.
		}

		public void dropActionChanged(DropTargetDragEvent evt) {
			// Called when the user changes the drag action between copy or move.
		}

		public void drop(DropTargetDropEvent evt) {
			// Called when the user finishes or cancels the drag operation.
			//((StringSelection)evt.getTransferable()).
			//System.out.println(evt.getCurrentDataFlavors()[0].getHumanPresentableName());
			String str_dragsource = null;
			try {
				Object obj = evt.getTransferable().getTransferData(evt.getCurrentDataFlavors()[0]);
				str_dragsource = (String) obj;
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}

			if (str_dragsource != null) {
				BillListPanel billList_source = null;
				if (str_dragsource.equals("ALL")) {
					billList_source = billlistPanel_all;
				} else if (str_dragsource.equals("ROW")) {
					billList_source = billlistPanel_row;
				} else if (str_dragsource.equals("COL")) {
					billList_source = billlistPanel_col;
				}

				BillListPanel billList_target = null;
				if (evt.getDropTargetContext().getComponent() == billlistPanel_all.getTable()) {
					billList_target = billlistPanel_all;
				} else if (evt.getDropTargetContext().getComponent() == billlistPanel_row.getTable()) {
					billList_target = billlistPanel_row;
				} else if (evt.getDropTargetContext().getComponent() == billlistPanel_col.getTable()) {
					billList_target = billlistPanel_col;
				} else if (evt.getDropTargetContext().getComponent() == billlistPanel_all.getMainScrollPane()) {
					billList_target = billlistPanel_all;
				} else if (evt.getDropTargetContext().getComponent() == billlistPanel_row.getMainScrollPane()) {
					billList_target = billlistPanel_row;
				} else if (evt.getDropTargetContext().getComponent() == billlistPanel_col.getMainScrollPane()) {
					billList_target = billlistPanel_col;
				}

				if (billList_source != null && billList_target != null) {
					moveDataBetweenBillListPanel(billList_source, billList_target); //
				}
			}
		}
	}

	class MyDraggListener implements DragGestureListener, DragSourceListener {
		DragSource dragSource;
		JTable dragtable = null;

		public MyDraggListener(JTable table) {
			dragtable = table; //
			dragSource = new DragSource();
			dragSource.createDefaultDragGestureRecognizer(table, DnDConstants.ACTION_COPY_OR_MOVE, this);
		}

		public void dragGestureRecognized(DragGestureEvent evt) {
			String str_tabtype = (String) dragtable.getClientProperty("dragtype");
			Transferable t = new StringSelection(str_tabtype);
			dragSource.startDrag(evt, DragSource.DefaultCopyDrop, t, this);
		}

		public void dragEnter(DragSourceDragEvent evt) {
			// Called when the user is dragging this drag source and enters
			// the drop target.
		}

		public void dragOver(DragSourceDragEvent evt) {
			// Called when the user is dragging this drag source and moves
			// over the drop target.
		}

		public void dragExit(DragSourceEvent evt) {
			// Called when the user is dragging this drag source and leaves
			// the drop target.
		}

		public void dropActionChanged(DragSourceDragEvent evt) {
			// Called when the user changes the drag action between copy or move.
		}

		public void dragDropEnd(DragSourceDropEvent evt) {

			// Called when the user finishes or cancels the drag operation.
			//System.out.println("注册事件!!"); //
		}
	}

	class TMO_SumField extends AbstractTMO {
		private static final long serialVersionUID = 8057184541083294474L;

		private String[] str_cpfields = null; //

		public TMO_SumField(String[] _cpfields) {
			str_cpfields = _cpfields; //
		}

		public HashVO getPub_templet_1Data() {
			HashVO vo = new HashVO(); //
			vo.setAttributeValue("templetcode", "分组计算列"); //模版编码,请勿随便修改
			vo.setAttributeValue("templetname", "分组计算列"); //模板名称
			vo.setAttributeValue("templetname_e", "分组计算列"); //模板名称
			return vo;
		}

		public HashVO[] getPub_templet_1_itemData() {
			Vector vector = new Vector();
			HashVO itemVO = null;

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ischecked"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "选择"); //显示名称
			itemVO.setAttributeValue("itemname_e", "ischecked"); //显示名称
			itemVO.setAttributeValue("itemtype", "勾选框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", "\"N\""); //默认值公式
			itemVO.setAttributeValue("listwidth", "60"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "fieldname"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "统计计算列"); //显示名称
			itemVO.setAttributeValue("itemname_e", "fieldname"); //显示名称
			itemVO.setAttributeValue("itemtype", "下拉框"); //控件类型

			StringBuilder sb_items = new StringBuilder(); //
			for (int i = 0; i < str_cpfields.length; i++) {
				sb_items.append(str_cpfields[i] + ";"); //
			}
			if (all_groupname != null) { //  袁江晓添加显示初始值  20130312
				for (int j = 0; j < all_groupname.length; j++) {
					sb_items.append(all_groupname[j] + ";"); //
				}
			}
			itemVO.setAttributeValue("comboxdesc", "getCommUC(\"下拉框\",\"直接值\",\"" + sb_items + "\");"); //下拉框定义

			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "85"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "computefunctionname"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "统计类型"); //显示名称
			itemVO.setAttributeValue("itemname_e", "computetype"); //显示名称
			itemVO.setAttributeValue("itemtype", "下拉框"); //控件类型

			StringBuilder sb_sql = new StringBuilder(); //
			sb_sql.append("select 'count' id,'' code,'记录数' name from wltdual union all  ");
			sb_sql.append("select 'init' id,'' code,'原始值' name from wltdual union all  "); //袁江晓添加 统计显示初始值  20130312
			sb_sql.append("select 'sum' id,'' code,'求和' name from wltdual union all ");
			sb_sql.append("select 'avg' id,'' code,'平均数' name from wltdual union all ");

			sb_sql.append("select 'PercentCount' id,'' code,'记录数占比' name from wltdual union all ");
			sb_sql.append("select 'PercentSum' id,'' code,'求和占比' name from wltdual union all ");

			sb_sql.append("select 'max' id,'' code,'最大值' name from wltdual union all ");
			sb_sql.append("select 'min' id,'' code,'最小值' name from wltdual union all ");

			sb_sql.append("select 'FormulaCompute' id,'' code,'计算公式' name from wltdual union all "); //计算公式

			sb_sql.append("select 'CountChainIncrease' id,'' code,'记录数环比' name from wltdual union all ");
			sb_sql.append("select 'SumChainIncrease' id,'' code,'求和环比' name from wltdual union all ");
			sb_sql.append("select 'AvgChainIncrease' id,'' code,'平均数环比' name from wltdual union all ");

			sb_sql.append("select 'CountChainSeries' id,'' code,'记录数环比连续增长' name from wltdual union all ");
			sb_sql.append("select 'SumChainSeries' id,'' code,'求和环比连续增长' name from wltdual union all ");
			sb_sql.append("select 'AvgChainSeries' id,'' code,'平均数环比连续增长' name from wltdual union all ");

			sb_sql.append("select 'CountPeriodIncrease' id,'' code,'记录数同比' name from wltdual union all ");
			sb_sql.append("select 'SumPeriodIncrease' id,'' code,'求和同比' name from wltdual union all ");
			sb_sql.append("select 'AvgPeriodIncrease' id,'' code,'平均数同比' name from wltdual union all ");

			sb_sql.append("select 'CountPeriodSeries' id,'' code,'记录数同比连续增长' name from wltdual union all ");
			sb_sql.append("select 'SumPeriodSeries' id,'' code,'求和同比连续增长' name from wltdual union all ");
			sb_sql.append("select 'AvgPeriodSeries' id,'' code,'平均数同比连续增长' name from wltdual ");

			itemVO.setAttributeValue("comboxdesc", sb_sql.toString()); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "135"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "extconfmap"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "扩展参数"); //显示名称
			itemVO.setAttributeValue("itemname_e", "extconfmap"); //显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("defaultvalueformula", "\"N\""); //默认值公式
			itemVO.setAttributeValue("listwidth", "135"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);

			return (HashVO[]) vector.toArray(new HashVO[0]);
		}
	}

	class FormulaDialog extends BillDialog { //定义公式维度弹出框 【杨科/2012-08-21】
		private BillListPanel billlistPanel_formula;
		private WLTButton formula_confirm, formula_cancel, formula_help;
		private int li_row;

		public FormulaDialog(Container _parent, int li_row) {
			super(_parent, "定义公式维度", 600, 400);
			this.li_row = li_row;
			initialize();
		}

		private void initialize() {
			this.getContentPane().setLayout(new BorderLayout());
			this.getContentPane().add(getFormulaPanel(), BorderLayout.CENTER);
			this.getContentPane().add(getBottomPanel(), BorderLayout.SOUTH);
		}

		public JPanel getFormulaPanel() { //定义公式维度面板 
			formula_help = new WLTButton("定义公式维度说明");
			billlistPanel_formula = new BillListPanel(new DefaultTMO("定义公式维度", new String[][] { { "name", "维度名称", "100" }, { "formula", "公式定义", "200" }, { "rule", "公式规则", "250" } }));
			billlistPanel_formula.addBatchBillListButton(new WLTButton[] { WLTButton.createButtonByType(WLTButton.LIST_ROWINSERT), WLTButton.createButtonByType(WLTButton.LIST_DELETEROW), WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEUP), WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEDOWN), formula_help });
			billlistPanel_formula.repaintBillListButton();
			billlistPanel_formula.setItemEditable(true);

			formula_help.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					doHelp();
				}
			});

			String str_formula = billlistPanel_row.getRealValueAtModel(li_row, "formulagroup");
			if (str_formula != null && !(str_formula.equals(""))) {
				String[] str_items = TBUtil.getTBUtil().split(str_formula, "■");
				for (int j = 0; j < str_items.length; j++) {
					String[] str_def = TBUtil.getTBUtil().split(str_items[j], "▲");
					int li_newrow = billlistPanel_formula.addEmptyRow(false);
					billlistPanel_formula.setValueAt(new StringItemVO(str_def[0]), li_newrow, "name");
					billlistPanel_formula.setValueAt(new StringItemVO(str_def[1]), li_newrow, "formula");
					if (str_def.length > 2) {
						billlistPanel_formula.setValueAt(new StringItemVO(str_def[2]), li_newrow, "rule");
					}
					billlistPanel_formula.clearSelection();
					billlistPanel_formula.moveToTop();
					billlistPanel_formula.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT);
				}
			}

			return billlistPanel_formula;
		}

		public String getFormulaValue() { //获取公式维度定义 
			HashMap hs = new HashMap();
			String str_formula = "";

			for (int i = 0; i < billlistPanel_formula.getRowCount(); i++) {
				String name = billlistPanel_formula.getRealValueAtModel(i, "name");
				String formula = billlistPanel_formula.getRealValueAtModel(i, "formula");
				String rule = billlistPanel_formula.getRealValueAtModel(i, "rule");

				if (hs.get(name) != null) {
					MessageBox.show(this, "第" + (i + 1) + "行与第" + hs.get(name) + "行 维度名称重名!");
					return null;
				}
				if (name == null || name.trim().equals("")) {
					MessageBox.show(this, "第" + (i + 1) + "行 第一列-维度名称为空!");
					return null;
				}
				if (formula == null || formula.trim().equals("")) {
					MessageBox.show(this, "第" + (i + 1) + "行 第二列-公式定义为空!");
					return null;
				}
				if (rule == null) {
					rule = "";
				}

				hs.put(name, (i + 1));
				str_formula += name + "▲" + formula + "▲" + rule + "■";
			}

			return str_formula;
		}

		public void setFormulaValue(String str_formula) { //设置公式维度定义值 
			//String str_formula = "高与大风险之和▲{高风险}+{极大风险}▲是否带百分号=N;警界规则=警界规则=300,150,100■高与低的比率▲({高风险}*100)/{极小风险}▲是否带百分号=Y■"; 
			billlistPanel_row.setValueAt(new StringItemVO(str_formula), li_row, "formulagroup"); //
		}

		private JPanel getBottomPanel() {
			JPanel panel_btn = new JPanel(new FlowLayout());
			panel_btn.setOpaque(false);
			formula_confirm = new WLTButton("确定");
			formula_cancel = new WLTButton("取消");

			formula_confirm.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					doConfirm();
				}
			});

			formula_cancel.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					doCancel();
				}
			});

			panel_btn.add(formula_confirm);
			panel_btn.add(formula_cancel);
			return panel_btn;
		}

		private void doConfirm() {
			billlistPanel_formula.stopEditing(); //结束编辑状态
			String str_formula = getFormulaValue();
			if (str_formula != null) {
				setFormulaValue(str_formula);
				doCancel();
			}
		}

		private void doCancel() {
			this.dispose();
		}

		private void doHelp() {
			StringBuilder sb_help = new StringBuilder();

			sb_help.append("定义公式维度,常见配置语法如下:\r\n");
			sb_help.append("维度名称 = 高与低的比率\r\n");
			sb_help.append("公式定义 = {高风险}*100)/{低风险}\r\n");
			sb_help.append("公式规则 = 是否带百分号=Y;警界规则=300,150,100\r\n");

			sb_help.append("\r\n");

			sb_help.append("可以配置多行,但维度名称不能相同\r\n");
			sb_help.append("维度名称与公式定义不能为空,公式规则可以为空\r\n");
			sb_help.append("公式定义支持加减乘除计算\r\n");

			sb_help.append("\r\n");

			sb_help.append("公式规则\r\n");
			sb_help.append("可以多个参数联合配置,之间用分号相隔\r\n");
			sb_help.append("警界值可以有多个,之间用逗号相隔,警界颜色分别是红橙黄绿\r\n");
			sb_help.append("警界值支持公式计算,支持宏代码有:{平均数}\r\n");
			MessageBox.show(this, sb_help.toString());
		}

	}

}
