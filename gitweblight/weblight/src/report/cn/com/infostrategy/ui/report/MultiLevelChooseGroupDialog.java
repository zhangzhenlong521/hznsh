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
 * ��ά����ѡ���Զ���ά��ʱ,������ѡ��ά�ȶԻ���!
 * @author xch
 *
 */
public class MultiLevelChooseGroupDialog extends BillDialog implements ActionListener {

	private static final long serialVersionUID = 6210230660775593956L;

	private BillListPanel billlistPanel_all, billlistPanel_row, billlistPanel_col, billlistPanel_sum; //
	private JComboBox row_groupTypeComBox, col_groupTypeComBox;
	private JCheckBox row_isSubtotal, col_isSubtotal; //

	private FormulaDialog formulaDialog = null; //���幫ʽά�ȴ��� �����/2012-08-21��

	private JRadioButton btn_grid, btn_chart; //

	private String[] all_groupname = null; //
	private String[] all_canSumNname = null; //

	private WLTButton btn_confirm, btn_cancel;
	private WLTButton btn_code; //׷�Ӳ鿴Դ�� �����/2012-08-13��
	private int closeType = -1;

	private String[] rowGroupnames = null;
	private String[] colGroupnames = null; //
	private String[][] computeFunAndFields = null;
	private String reportType = "GRID"; //���ص�ͼ������
	private JCheckBox field_sort; //׷���Ƿ񰴼���ֵ����? �����/2012-08-21��

	private WLTButton btn_formulaGroup, btn_extconfHelp = null; //��չ���ð���˵��!

	public MultiLevelChooseGroupDialog(Container _parent, String[] _allgroupname, String[] _allCanSumFieldName) {
		super(_parent, "�Զ���ѡ��ά��", 800, 800); //
		this.all_groupname = _allgroupname; //
		this.all_canSumNname = _allCanSumFieldName; //
		initialize();
	}

	/**
	 * ��ʼҳ��..
	 */
	private void initialize() {
		this.getContentPane().setLayout(new BorderLayout()); //
		try {
			billlistPanel_all = new BillListPanel(new DefaultTMO("��ѡά��", new String[][] { { "name", "ά������", "138" } })); //
			billlistPanel_row = new BillListPanel(new DefaultTMO("�ϱ�ͷά��", new String[][] { { "name", "ά������", "100" }, { "formulagroup", "��ʽά�ȶ���", "250" } })); //
			billlistPanel_col = new BillListPanel(new DefaultTMO("���ͷά��", new String[][] { { "name", "ά������", "138" } })); //

			billlistPanel_all.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			billlistPanel_row.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			billlistPanel_col.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			btn_formulaGroup = new WLTButton("���幫ʽά��");
			btn_formulaGroup.addActionListener(this); //
			billlistPanel_row.addBatchBillListButton(new WLTButton[] { WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEUP), WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEDOWN), btn_formulaGroup }); //
			billlistPanel_row.repaintBillListButton(); //

			billlistPanel_col.addBatchBillListButton(new WLTButton[] { WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEUP), WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEDOWN) }); //
			billlistPanel_col.repaintBillListButton(); //

			JPanel row_custPanel = new JPanel(new FlowLayout());
			row_custPanel.setOpaque(false); //
			row_groupTypeComBox = new JComboBox(new String[] { "����", "ƽ��" }); //
			row_groupTypeComBox.setPreferredSize(new Dimension(75, 20)); //
			row_isSubtotal = new JCheckBox("�Ƿ�С��", true);
			row_isSubtotal.setOpaque(false);
			row_custPanel.add(row_groupTypeComBox); //
			row_custPanel.add(row_isSubtotal); //
			billlistPanel_row.setCustomerNavigationJPanel(row_custPanel); //

			JPanel col_custPanel = new JPanel(new FlowLayout());
			col_custPanel.setOpaque(false); //
			col_groupTypeComBox = new JComboBox(new String[] { "����", "ƽ��" }); //
			col_groupTypeComBox.setPreferredSize(new Dimension(75, 20)); //
			col_isSubtotal = new JCheckBox("�Ƿ�С��", true);
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

			billlistPanel_all.setTableToolTipText("ѡ���¼ֱ���϶������������Ŀ�"); //

			billlistPanel_all.getBillListBtn("������").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onAddRow();
				}
			});

			billlistPanel_all.getBillListBtn("������").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onAddCol();
				}
			});

			billlistPanel_row.getBillListBtn("��ȥ").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onRemoveRow();
				}
			});

			billlistPanel_col.getBillListBtn("��ȥ").addActionListener(new ActionListener() {
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

			//����������!
			btn_extconfHelp = new WLTButton("��չ����˵��"); //
			btn_extconfHelp.addActionListener(this); //
			billlistPanel_sum.addBatchBillListButton(new WLTButton[] { WLTButton.createButtonByType(WLTButton.LIST_ROWINSERT), WLTButton.createButtonByType(WLTButton.LIST_DELETEROW), WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEUP), WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEDOWN), btn_extconfHelp }); //
			billlistPanel_sum.repaintBillListButton(); //

			//�������м�����!
			for (int i = 0; i < all_canSumNname.length; i++) {
				int li_newrow_1 = billlistPanel_sum.addEmptyRow(false); //
				billlistPanel_sum.setValueAt(new StringItemVO(i == 0 ? "Y" : "N"), li_newrow_1, "ischecked"); //�Ƿ�ѡ��,����ǵ�һ��,��Ĭ��ѡ��!
				billlistPanel_sum.setValueAt(new ComBoxItemVO(all_canSumNname[i], null, all_canSumNname[i]), li_newrow_1, "fieldname"); //
				if (i == 0) {
					billlistPanel_sum.setValueAt(new ComBoxItemVO("count", "", "��¼��"), li_newrow_1, "computefunctionname"); //
				} else {
					billlistPanel_sum.setValueAt(new ComBoxItemVO("sum", "", "���"), li_newrow_1, "computefunctionname"); //
				}
				billlistPanel_sum.clearSelection(); //
				billlistPanel_sum.moveToTop(); //
				billlistPanel_sum.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT); //

				int li_newrow_2 = billlistPanel_sum.addEmptyRow(false); //
				billlistPanel_sum.setValueAt(new StringItemVO("N"), li_newrow_2, "ischecked"); //�Ƿ�ѡ��,����ǵ�һ��,��Ĭ��ѡ��!
				billlistPanel_sum.setValueAt(new ComBoxItemVO(all_canSumNname[i], null, all_canSumNname[i]), li_newrow_2, "fieldname"); //
				if (i == 0) { //����ǵ�һ������,һ�㶼�Ǽ�¼��,���Ժ���Ĭ���Ǽ�¼��ռ��
					billlistPanel_sum.setValueAt(new ComBoxItemVO("PercentCount", "", "��¼��ռ��"), li_newrow_2, "computefunctionname"); //
				} else { //���������Ӧ�þ������,�������
					billlistPanel_sum.setValueAt(new ComBoxItemVO("PercentSum", "", "���ռ��"), li_newrow_2, "computefunctionname"); //
				}
				billlistPanel_sum.clearSelection(); //
				billlistPanel_sum.moveToTop(); //
				billlistPanel_sum.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT); //

				//				int li_newrow_3 = billlistPanel_sum.addEmptyRow(false); //
				//				billlistPanel_sum.setValueAt(new StringItemVO("N"), li_newrow_3, "ischecked"); //�Ƿ�ѡ��,����ǵ�һ��,��Ĭ��ѡ��!
				//				billlistPanel_sum.setValueAt(new StringItemVO(all_canSumNname[i]), li_newrow_3, "fieldname"); //
				//				billlistPanel_sum.clearSelection(); //
				//				billlistPanel_sum.moveToTop(); //
				//				billlistPanel_sum.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT); //
			}

			//�����϶��¼�..
			billlistPanel_all.getTable().putClientProperty("dragtype", "ALL"); //
			billlistPanel_row.getTable().putClientProperty("dragtype", "ROW"); //
			billlistPanel_col.getTable().putClientProperty("dragtype", "COL"); //

			new MyDraggListener(billlistPanel_all.getTable()); //�����϶�����..
			new MyDropListener(billlistPanel_all.getTable()); //

			new MyDraggListener(billlistPanel_row.getTable()); //�����϶�����..			
			new MyDropListener(billlistPanel_row.getTable()); //

			new MyDraggListener(billlistPanel_col.getTable()); //�����϶�����..			
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
	 * �������...
	 * @return
	 */
	private BillListPanel getBillListPanel_sum() {
		billlistPanel_sum = new BillListPanel(new TMO_SumField(all_canSumNname)); //
		billlistPanel_sum.isCanShowCardInfo = false;
		return billlistPanel_sum; //
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
		panel.setOpaque(false); //͸��!
		btn_grid = new JRadioButton("���", true); //
		btn_chart = new JRadioButton("ͼ��"); //
		btn_grid.setOpaque(false); //͸��
		btn_chart.setOpaque(false); //͸��
		btn_grid.setFocusable(false); //
		btn_chart.setFocusable(false); //

		ButtonGroup group = new ButtonGroup(); //
		group.add(btn_grid); //
		group.add(btn_chart); //

		panel.add(new JLabel("��������:", SwingConstants.RIGHT)); //
		panel.add(btn_grid); //
		panel.add(btn_chart); //

		field_sort = new JCheckBox("�Ƿ񰴼���ֵ����", false);
		field_sort.setOpaque(false);
		panel.add(field_sort); //

		JLabel label_info = new JLabel("�ر�����:1.ֱ������϶�ѡ��ά��  2.ѡ��ά�ȷ��غ�,Ҫ�������ѯ����ť�����ݲŻ�仯"); //
		label_info.setForeground(Color.RED); //
		panel.add(label_info); //

		JPanel panel_btn = new JPanel(new FlowLayout()); //
		panel_btn.setOpaque(false); //͸��
		btn_confirm = new WLTButton("ȷ��");
		btn_cancel = new WLTButton("ȡ��");

		btn_code = new WLTButton("�鿴Դ��");

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
		billlistPanel_sum.stopEditing(); //�����༭״̬
		billlistPanel_row.stopEditing(); //
		if (!onConfirmAndCodeBefore()) {
			return;
		}

		closeType = 1; //
		this.dispose();
	}

	private boolean isLikeDateName(String _name) {
		if (_name.indexOf("ʱ��") >= 0 || _name.indexOf("����") >= 0 || _name.indexOf("����") >= 0 || _name.indexOf("��") >= 0) {
			return true;
		} else {
			return false;
		}
	}

	public void onCancel() {
		billlistPanel_sum.stopEditing(); //�����༭״̬
		closeType = 2;
		this.dispose();
	}

	public boolean onConfirmAndCodeBefore() { //ȷ����鿴Դ�븳ֵ �����/2012-08-13��
		int li_count_row = billlistPanel_row.getRowCount();
		int li_count_col = billlistPanel_col.getRowCount();
		if (li_count_row == 0 && li_count_col == 0) {
			MessageBox.show(this, "����ѡ��һ������ͳ�Ƶ���!"); //
			return false; //
		}

		//ͳ����Ҫ�������..
		ArrayList al_ComputeFunctionAndfield = new ArrayList(); //��¼��Щ����ͳ�Ƶ���
		HashSet hst_dis = new HashSet(); //
		boolean isHaveTBHB = false; //�Ƿ���ͬ�Ȼ򻷱�
		boolean isChecked = false;//�����/2019-03-21��
		for (int i = 0; i < billlistPanel_sum.getRowCount(); i++) {
			if (billlistPanel_sum.getRealValueAtModel(i, "ischecked").equals("Y")) { //���ѡ�е�
				isChecked = true;//�����/2019-03-21��
				String str_field = billlistPanel_sum.getRealValueAtModel(i, "fieldname").trim(); //
				String str_extconfmap = billlistPanel_sum.getRealValueAtModel(i, "extconfmap"); //
				if (str_extconfmap != null) {
					str_extconfmap = str_extconfmap.trim(); //
				}
				if (str_field == null || str_field.equals("")) {
					MessageBox.show(this, "��[" + (i + 1) + "]��û��ָ����������,��ѡ��"); //
					return false; //
				}
				ComBoxItemVO comBoxItemVO = (ComBoxItemVO) billlistPanel_sum.getValueAt(i, "computefunctionname"); //
				if (comBoxItemVO == null || comBoxItemVO.getId() == null || comBoxItemVO.getId().equals("")) {
					MessageBox.show(this, "��[" + (i + 1) + "]��û��ָ��ͳ������,��ѡ��"); //
					return false; //
				}
				String str_diskey = str_field + "��" + comBoxItemVO.getId(); //
				if (hst_dis.contains(str_diskey)) {
					MessageBox.show(this, "��[" + (i + 1) + "]����ǰ�淢���ظ�,������ѡ��"); //
					return false; //
				}
				hst_dis.add(str_diskey); //

				//����У��!
				if (comBoxItemVO.getId().startsWith("Two") && al_ComputeFunctionAndfield.size() < 2) { //��������߲��ʲô��,�����
					MessageBox.show(this, "��[" + (i + 1) + "]���Ǽ���ǰ���ߵĲ������,��ǰ��ȴ��������,������ѡ��"); //
					return false; //
				}

				if (comBoxItemVO.getId().endsWith("ChainIncrease") || comBoxItemVO.getId().endsWith("PeriodIncrease")) { //�����ͬ�Ȼ򻷱�
					isHaveTBHB = true; //
				}

				String str_funname = comBoxItemVO.getId(); //count,sum,avg
				if (str_funname.equals("count") || str_funname.equals("sum")) { //��¼�������,Ĭ�ϲ������
					al_ComputeFunctionAndfield.add(new String[] { str_field, str_funname, str_extconfmap }); //������,������ʾ��,��������!!!
				} else { //������ƽ����,ռ�ȵĴ����!
					String str_funviewname = comBoxItemVO.getName();//
					if (str_funviewname.endsWith("ռ��")) {
						str_funviewname = "ռ��"; //
					}
					if (str_funviewname.endsWith("ͬ��")) {
						str_funviewname = "ͬ��"; //
					}
					if (str_funviewname.endsWith("����")) {
						str_funviewname = "����"; //
					}
					String str_realWeiLi = str_field + "-" + str_funviewname; //
					if (str_funviewname.startsWith("����")) {
						str_realWeiLi = str_funviewname; //
					}
					al_ComputeFunctionAndfield.add(new String[] { str_realWeiLi, str_funname, str_extconfmap }); //������,������ʾ��,��������!!!
				}
			}
		}
		if (!isChecked) {
			MessageBox.show(this, "����ѡ��һ�����������!"); //�޸�bug����ǰû��ѡ���������лᱨ�����/2019-03-21��
			return false; //
		}

		computeFunAndFields = new String[al_ComputeFunctionAndfield.size()][3]; ///
		for (int i = 0; i < al_ComputeFunctionAndfield.size(); i++) {
			computeFunAndFields[i] = (String[]) al_ComputeFunctionAndfield.get(i); //
		}

		if (btn_grid.isSelected()) { //����,���汨��....
			reportType = "GRID"; //
		} else if (btn_chart.isSelected()) { //ͼ��....
			reportType = "CHART"; //
			if (li_count_row > 1 || li_count_col > 1) { //�����һ������0,�ǲ������
				MessageBox.show(this, "��ͼ��ʱ,ֻ�����ѡ��һ������������!"); //
				return false; //
			}

			if (li_count_row == 1 && li_count_col == 1) { //���ͬʱѡ����������,��ֻ��ѡ��һ������������,
				if (computeFunAndFields.length > 1) {
					MessageBox.show(this, "��ͬʱѡ����������ͼ��ʱ,ֻ��ѡ��һ������ͳ�Ƽ������!������ѡ���,����ֻѡ���л���!"); //
					return false; //
				}
			} else { //���ֻѡ�����л���,������ѡ����ͳ�Ƽ�����,���������ǰ��û�е�,���û���ʱ��Ҫ�Ƚ�ĳ��������֮�伸�����֮��ıȽ�,����,����ë��,˰������,������!��ǰû�������,�ܾ���ȱ��ʲô!!
				//ͼ����ֻѡ��
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
			if (!MessageBox.confirm(this, "��ѡ��һ������/ͬ�ȼ�������,���ƺ�����ά����û��һ����ʱ������!\r\n���п��ܵ��¼���ֵΪ��,�Ƿ������")) {
				return false;
			}
		}

		return true;
	}

	public void onCode() { //�鿴Դ�� �����/2012-08-13��
		if (!onConfirmAndCodeBefore()) {
			return;
		}

		StringBuffer sb_codes = new StringBuffer();
		StringBuffer sb_name = new StringBuffer();
		StringBuffer sb_row = new StringBuffer();
		StringBuffer sb_col = new StringBuffer();
		StringBuffer sb_compute = new StringBuffer();

		String[] chooseRowFields = getRowGroupnames(); //��ͷ
		String[] chooseColFields = getColGroupnames(); //��ͷ
		String[][] chooseComputeFields = getComputeFunAndFields(); //����

		for (int i = 0; i < chooseColFields.length; i++) {
			sb_name.append(chooseColFields[i] + "-");
			sb_col.append("\"" + chooseColFields[i] + "\", ");
		}

		for (int i = 0; i < chooseRowFields.length; i++) {
			sb_name.append(chooseRowFields[i] + "-");
			sb_row.append("\"" + chooseRowFields[i] + "\", ");
		}

		String str_name = sb_name.toString(); //������
		if (str_name.endsWith("-")) {
			str_name = str_name.substring(0, str_name.length() - 1);
		}

		String str_row = sb_row.toString(); //��ͷ
		if (str_row.endsWith(", ")) {
			str_row = str_row.substring(0, str_row.length() - 2);
		}

		String str_col = sb_col.toString(); //��ͷ
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

		String str_compute = sb_compute.toString(); //����
		if (str_compute.endsWith(", ")) {
			str_compute = str_compute.substring(0, str_compute.length() - 2);
		}

		String str_reportType = getReportType(); //��������

		sb_codes.append("ArrayList al_vos = new ArrayList(); \r\n");
		sb_codes.append("BeforeHandGroupTypeVO bhGroupVO = null; \r\n");
		sb_codes.append("\r\n");
		sb_codes.append("bhGroupVO = new BeforeHandGroupTypeVO(); \r\n");
		sb_codes.append("bhGroupVO.setName((al_vos.size() + 1 + \"-" + str_name + "\")); \r\n");
		if (str_reportType.equals("GRID")) {
			sb_codes.append("bhGroupVO.setRowHeaderGroupFields(new String[] { " + str_row + " }); \r\n");
			sb_codes.append("bhGroupVO.setColHeaderGroupFields(new String[] { " + str_col + " }); \r\n");
		}
		if (str_reportType.equals("CHART")) { //ͼ�������л���
			sb_codes.append("bhGroupVO.setRowHeaderGroupFields(new String[] { " + str_col + " }); \r\n");
			sb_codes.append("bhGroupVO.setColHeaderGroupFields(new String[] { " + str_row + " }); \r\n");
		}
		sb_codes.append("bhGroupVO.setComputeGroupFields(new String[][] { " + str_compute + " }); \r\n");
		if (isRowGroupTiled()) {
			sb_codes.append("bhGroupVO.setRowGroupTiled(true); \r\n"); //��ͷ�Ƿ���ƽ��
		}
		if (!isRowGroupSubTotal()) {
			sb_codes.append("bhGroupVO.setRowGroupSubTotal(false); \r\n"); //��ͷ�Ƿ���С��
		}
		if (isColGroupTiled()) {
			sb_codes.append("bhGroupVO.setColGroupTiled(true); \r\n"); //��ͷ�Ƿ���ƽ��
		}
		if (!isColGroupSubTotal()) {
			sb_codes.append("bhGroupVO.setColGroupSubTotal(false); \r\n"); //��ͷ�Ƿ���С��?		
		}
		sb_codes.append("bhGroupVO.setType(\"" + str_reportType + "\"); \r\n");
		sb_codes.append("al_vos.add(bhGroupVO); \r\n");
		sb_codes.append("\r\n");
		sb_codes.append("return (BeforeHandGroupTypeVO[]) al_vos.toArray(new BeforeHandGroupTypeVO[0]); \r\n");

		MessageBox.showTextArea(this, "ά��ѡ��Դ��", sb_codes.toString());
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
			String str_groupName = billlistPanel_row.getRealValueAtModel(i, "name"); //ά������
			String str_formula = billlistPanel_row.getRealValueAtModel(i, "formulagroup"); //��ʽ����
			if (str_formula != null && !str_formula.trim().equals("")) { //�����Ϊ��!!
				String[] str_items = TBUtil.getTBUtil().split(str_formula, "��"); //
				String[][] str_def = new String[str_items.length][3]; //
				for (int j = 0; j < str_items.length; j++) {
					str_def[j] = TBUtil.getTBUtil().split(str_items[j], "��"); //
				}
				//String[][] str_def = new String[]
				//str_formula
				map.put(str_groupName, str_def); //
			}
		}
		return map; //
	}

	//��ͷ���Ƿ�ƽ��?
	public boolean isRowGroupTiled() {
		return (row_groupTypeComBox.getSelectedIndex() == 1 ? true : false);
	}

	//��ͷ���Ƿ�ƽ��?
	public boolean isColGroupTiled() {
		return (col_groupTypeComBox.getSelectedIndex() == 1 ? true : false);
	}

	//��ͷ���Ƿ�С��?
	public boolean isRowGroupSubTotal() {
		return row_isSubtotal.isSelected(); //
	}

	//�Ƿ񰴼���ֵ����?
	public boolean isSortByCpValue() {
		return field_sort.isSelected(); //
	}

	//��ͷ���Ƿ�С��?
	public boolean isColGroupSubTotal() {
		return col_isSubtotal.isSelected(); //
	}

	public int getCloseType() {
		return closeType;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_extconfHelp) {
			onShowExtConfHelp(); //
		} else if (e.getSource() == btn_formulaGroup) { //������ʽά��
			onDefineNewFormulaGroup(); //
		}
	}

	/**
	 * ��ԭ����ά�Ⱥ����������һЩ��ʽά��!
	 */
	private void onDefineNewFormulaGroup() {
		int li_row = billlistPanel_row.getSelectedRow(); //
		if (li_row < 0) {
			MessageBox.show(this, "��ѡ��һ����¼���в���!"); //
			return; //
		}

		//����һ�����,�������������,��ɾ��,��ȷ��,Ȼ���Ƚ���������ݽ������������!����ʱ����ƴ�ӳ��ַ���! �����/2012-08-21��	
		formulaDialog = new FormulaDialog(this, li_row);
		formulaDialog.setVisible(true);
	}

	//��ʾ��չ��������
	private void onShowExtConfHelp() {
		StringBuilder sb_help = new StringBuilder(); //
		sb_help.append("��չ��������ָ����������,���������﷨����:\r\n"); //
		sb_help.append("��ʾ����=��ȥ��ͬ�ڱ���\r\n"); //
		sb_help.append("�Ƿ���ٷֺ�=Y\r\n"); //
		sb_help.append("�������=300,150,100\r\n"); //
		sb_help.append("�������={ƽ����}*2,{ƽ����}*1.5,{ƽ����}\r\n"); //

		sb_help.append("\r\n"); //

		sb_help.append("���Զ��������������,֮���÷ֺ����\r\n"); //
		sb_help.append("����ֵ�����ж��,֮���ö������,������ɫ�ֱ��Ǻ�Ȼ���\r\n"); //
		sb_help.append("����ֵ֧�ֹ�ʽ����,֧�ֺ������:{ƽ����}\r\n"); //
		MessageBox.show(this, sb_help.toString()); //
	}

	/**
	 * �϶��б�..
	 * @param _source
	 * @param _target
	 */
	private void moveDataBetweenBillListPanel(BillListPanel _source, BillListPanel _target) {
		if (_source == _target) {
			return; //�����һ������,�򷵻�!
		}

		//System.out.println("��������!!");
		BillVO[] billVOs = _source.getSelectedBillVOs(); //
		for (int i = 0; i < billVOs.length; i++) {
			int li_row = _target.addEmptyRow(); //
			_target.setBillVOAt(li_row, billVOs[i].deepClone()); //��Ŀ���м���!!!
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
			//System.out.println("ע���¼�!!"); //
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
			vo.setAttributeValue("templetcode", "���������"); //ģ�����,��������޸�
			vo.setAttributeValue("templetname", "���������"); //ģ������
			vo.setAttributeValue("templetname_e", "���������"); //ģ������
			return vo;
		}

		public HashVO[] getPub_templet_1_itemData() {
			Vector vector = new Vector();
			HashVO itemVO = null;

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ischecked"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "ѡ��"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "ischecked"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", "\"N\""); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "60"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "N"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "4"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "fieldname"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "ͳ�Ƽ�����"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "fieldname"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "������"); //�ؼ�����

			StringBuilder sb_items = new StringBuilder(); //
			for (int i = 0; i < str_cpfields.length; i++) {
				sb_items.append(str_cpfields[i] + ";"); //
			}
			if (all_groupname != null) { //  Ԭ���������ʾ��ʼֵ  20130312
				for (int j = 0; j < all_groupname.length; j++) {
					sb_items.append(all_groupname[j] + ";"); //
				}
			}
			itemVO.setAttributeValue("comboxdesc", "getCommUC(\"������\",\"ֱ��ֵ\",\"" + sb_items + "\");"); //��������

			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "85"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "N"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "4"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "computefunctionname"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "ͳ������"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "computetype"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "������"); //�ؼ�����

			StringBuilder sb_sql = new StringBuilder(); //
			sb_sql.append("select 'count' id,'' code,'��¼��' name from wltdual union all  ");
			sb_sql.append("select 'init' id,'' code,'ԭʼֵ' name from wltdual union all  "); //Ԭ������� ͳ����ʾ��ʼֵ  20130312
			sb_sql.append("select 'sum' id,'' code,'���' name from wltdual union all ");
			sb_sql.append("select 'avg' id,'' code,'ƽ����' name from wltdual union all ");

			sb_sql.append("select 'PercentCount' id,'' code,'��¼��ռ��' name from wltdual union all ");
			sb_sql.append("select 'PercentSum' id,'' code,'���ռ��' name from wltdual union all ");

			sb_sql.append("select 'max' id,'' code,'���ֵ' name from wltdual union all ");
			sb_sql.append("select 'min' id,'' code,'��Сֵ' name from wltdual union all ");

			sb_sql.append("select 'FormulaCompute' id,'' code,'���㹫ʽ' name from wltdual union all "); //���㹫ʽ

			sb_sql.append("select 'CountChainIncrease' id,'' code,'��¼������' name from wltdual union all ");
			sb_sql.append("select 'SumChainIncrease' id,'' code,'��ͻ���' name from wltdual union all ");
			sb_sql.append("select 'AvgChainIncrease' id,'' code,'ƽ��������' name from wltdual union all ");

			sb_sql.append("select 'CountChainSeries' id,'' code,'��¼��������������' name from wltdual union all ");
			sb_sql.append("select 'SumChainSeries' id,'' code,'��ͻ�����������' name from wltdual union all ");
			sb_sql.append("select 'AvgChainSeries' id,'' code,'ƽ����������������' name from wltdual union all ");

			sb_sql.append("select 'CountPeriodIncrease' id,'' code,'��¼��ͬ��' name from wltdual union all ");
			sb_sql.append("select 'SumPeriodIncrease' id,'' code,'���ͬ��' name from wltdual union all ");
			sb_sql.append("select 'AvgPeriodIncrease' id,'' code,'ƽ����ͬ��' name from wltdual union all ");

			sb_sql.append("select 'CountPeriodSeries' id,'' code,'��¼��ͬ����������' name from wltdual union all ");
			sb_sql.append("select 'SumPeriodSeries' id,'' code,'���ͬ����������' name from wltdual union all ");
			sb_sql.append("select 'AvgPeriodSeries' id,'' code,'ƽ����ͬ����������' name from wltdual ");

			itemVO.setAttributeValue("comboxdesc", sb_sql.toString()); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "135"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "N"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "4"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "extconfmap"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "��չ����"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "extconfmap"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("defaultvalueformula", "\"N\""); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "135"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "N"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "4"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);

			return (HashVO[]) vector.toArray(new HashVO[0]);
		}
	}

	class FormulaDialog extends BillDialog { //���幫ʽά�ȵ����� �����/2012-08-21��
		private BillListPanel billlistPanel_formula;
		private WLTButton formula_confirm, formula_cancel, formula_help;
		private int li_row;

		public FormulaDialog(Container _parent, int li_row) {
			super(_parent, "���幫ʽά��", 600, 400);
			this.li_row = li_row;
			initialize();
		}

		private void initialize() {
			this.getContentPane().setLayout(new BorderLayout());
			this.getContentPane().add(getFormulaPanel(), BorderLayout.CENTER);
			this.getContentPane().add(getBottomPanel(), BorderLayout.SOUTH);
		}

		public JPanel getFormulaPanel() { //���幫ʽά����� 
			formula_help = new WLTButton("���幫ʽά��˵��");
			billlistPanel_formula = new BillListPanel(new DefaultTMO("���幫ʽά��", new String[][] { { "name", "ά������", "100" }, { "formula", "��ʽ����", "200" }, { "rule", "��ʽ����", "250" } }));
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
				String[] str_items = TBUtil.getTBUtil().split(str_formula, "��");
				for (int j = 0; j < str_items.length; j++) {
					String[] str_def = TBUtil.getTBUtil().split(str_items[j], "��");
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

		public String getFormulaValue() { //��ȡ��ʽά�ȶ��� 
			HashMap hs = new HashMap();
			String str_formula = "";

			for (int i = 0; i < billlistPanel_formula.getRowCount(); i++) {
				String name = billlistPanel_formula.getRealValueAtModel(i, "name");
				String formula = billlistPanel_formula.getRealValueAtModel(i, "formula");
				String rule = billlistPanel_formula.getRealValueAtModel(i, "rule");

				if (hs.get(name) != null) {
					MessageBox.show(this, "��" + (i + 1) + "�����" + hs.get(name) + "�� ά����������!");
					return null;
				}
				if (name == null || name.trim().equals("")) {
					MessageBox.show(this, "��" + (i + 1) + "�� ��һ��-ά������Ϊ��!");
					return null;
				}
				if (formula == null || formula.trim().equals("")) {
					MessageBox.show(this, "��" + (i + 1) + "�� �ڶ���-��ʽ����Ϊ��!");
					return null;
				}
				if (rule == null) {
					rule = "";
				}

				hs.put(name, (i + 1));
				str_formula += name + "��" + formula + "��" + rule + "��";
			}

			return str_formula;
		}

		public void setFormulaValue(String str_formula) { //���ù�ʽά�ȶ���ֵ 
			//String str_formula = "��������֮�͡�{�߷���}+{�������}���Ƿ���ٷֺ�=N;�������=�������=300,150,100������͵ı��ʡ�({�߷���}*100)/{��С����}���Ƿ���ٷֺ�=Y��"; 
			billlistPanel_row.setValueAt(new StringItemVO(str_formula), li_row, "formulagroup"); //
		}

		private JPanel getBottomPanel() {
			JPanel panel_btn = new JPanel(new FlowLayout());
			panel_btn.setOpaque(false);
			formula_confirm = new WLTButton("ȷ��");
			formula_cancel = new WLTButton("ȡ��");

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
			billlistPanel_formula.stopEditing(); //�����༭״̬
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

			sb_help.append("���幫ʽά��,���������﷨����:\r\n");
			sb_help.append("ά������ = ����͵ı���\r\n");
			sb_help.append("��ʽ���� = {�߷���}*100)/{�ͷ���}\r\n");
			sb_help.append("��ʽ���� = �Ƿ���ٷֺ�=Y;�������=300,150,100\r\n");

			sb_help.append("\r\n");

			sb_help.append("�������ö���,��ά�����Ʋ�����ͬ\r\n");
			sb_help.append("ά�������빫ʽ���岻��Ϊ��,��ʽ�������Ϊ��\r\n");
			sb_help.append("��ʽ����֧�ּӼ��˳�����\r\n");

			sb_help.append("\r\n");

			sb_help.append("��ʽ����\r\n");
			sb_help.append("���Զ��������������,֮���÷ֺ����\r\n");
			sb_help.append("����ֵ�����ж��,֮���ö������,������ɫ�ֱ��Ǻ�Ȼ���\r\n");
			sb_help.append("����ֵ֧�ֹ�ʽ����,֧�ֺ������:{ƽ����}\r\n");
			MessageBox.show(this, sb_help.toString());
		}

	}

}
