package cn.com.infostrategy.ui.workflow.engine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.HashVOStruct;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.FrameWorkCommServiceIfc;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.UIUtil;

/**
 * �鿴���йؼ���ϸ��Ϣ,������Ҫ�Ĺ���,����һ����ıȽ����������Ϣ!!
 * ���ʵʩ��Ա�������ⶨλʱ�Ϳ�����!
 * @author xch
 *
 */
public class LookDealPoolAndTaskInfoDialog extends BillDialog {

	private HashVO[] hvs = null; //

	public LookDealPoolAndTaskInfoDialog(Container _parent, String _title, int _width, int li_height, HashVO[] _hvs) {
		super(_parent, _title, _width, li_height);
		this.hvs = _hvs; //
		initialize(); //��ʼ��ҳ��
	}

	/**
	 * ��ʼ��ҳ��!
	 */
	private void initialize() {
		JLabel label_info = new JLabel("��ʾ:��ҳ���е����������ڷ�����������ʱ���ٷ���ԭ����!һ�������һ������!"); //
		label_info.setForeground(Color.RED); //
		this.getContentPane().add(label_info, BorderLayout.NORTH); //

		JSplitPane split_1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getTaskDealPanel(), getTaskOffPanel()); //
		split_1.setDividerLocation(300); //
		split_1.setOneTouchExpandable(true); //

		JSplitPane split_2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getDealPoolPanel(), split_1); // ����
		split_2.setDividerLocation(300); //
		split_2.setOneTouchExpandable(true); //

		this.getContentPane().add(split_2, BorderLayout.CENTER); //
	}

	private JPanel getDealPoolPanel() {
		JPanel panel = new JPanel(new BorderLayout()); //
		panel.add(new JLabel("���������(select * from pub_wf_dealpool where id='')"), BorderLayout.NORTH); //
		String[] str_cols = null;
		if (hvs.length == 1) {
			str_cols = new String[hvs.length + 1]; //
		} else {
			str_cols = new String[hvs.length + 2]; //
		}
		str_cols[0] = "����"; //
		for (int i = 0; i < hvs.length; i++) {
			str_cols[i + 1] = hvs[i].toString(); ////
		}
		if (hvs.length > 1) {
			str_cols[hvs.length + 1] = "�ȽϽ��"; //
		}
		DefaultTableModel tableModel = new DefaultTableModel(new String[][] {}, str_cols); //
		String[] str_keys = hvs[0].getKeys(); //������!
		ArrayList al_notequals_rows = new ArrayList(); //
		for (int i = 0; i < str_keys.length; i++) { //����!
			String[] str_rowData = new String[hvs.length + 2]; //
			str_rowData[0] = str_keys[i]; //����
			for (int j = 0; j < hvs.length; j++) { //�����������!!
				str_rowData[j + 1] = hvs[j].getStringValue(str_keys[i]); //
			}
			if (hvs.length > 1) {
				if (!compareArrayItemEquals(str_rowData, 1, str_rowData.length - 2)) {
					str_rowData[hvs.length + 1] = "�в���"; //
					al_notequals_rows.add(new Integer(i)); //
				}
			}
			tableModel.addRow(str_rowData); //��������
		}
		JTable table = new JTable(tableModel); //
		Integer[] li_selRows = (Integer[]) al_notequals_rows.toArray(new Integer[0]); //
		for (int i = 0; i < al_notequals_rows.size(); i++) {
			table.addRowSelectionInterval((Integer) al_notequals_rows.get(i), (Integer) al_notequals_rows.get(i)); //
		}
		table.setFont(LookAndFeel.font); //
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //
		table.getColumnModel().getColumn(0).setPreferredWidth(100); //
		for (int i = 1; i < str_cols.length - 1; i++) { //����!
			table.getColumnModel().getColumn(i).setPreferredWidth(120); //
		}
		if (hvs.length > 1) {
			table.getColumnModel().getColumn(str_cols.length - 1).setPreferredWidth(70); //
		}
		panel.add(new JScrollPane(table), BorderLayout.CENTER); //
		JScrollPane scrollPanel = new JScrollPane(panel); //
		//scrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); //
		//scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER); //
		JPanel returnPanel = new JPanel(new BorderLayout()); //
		returnPanel.add(scrollPanel); //
		return returnPanel; //
	}

	//������
	private JPanel getTaskDealPanel() {
		return getTaskPanelByPar("������(select * from pub_task_deal where prdealpoolid='')", "pub_task_deal"); //
	}

	private JPanel getTaskOffPanel() {
		return getTaskPanelByPar("�Ѱ���(select * from pub_task_off where prdealpoolid='')", "pub_task_off"); ///
	}

	private JPanel getTaskPanelByPar(String _title, String _tableName) {
		JPanel panel = new JPanel(new BorderLayout()); ////
		panel.add(new JLabel(_title), BorderLayout.NORTH); //
		try {
			String[] str_cols = null;
			if (hvs.length == 1) {
				str_cols = new String[hvs.length + 1]; //
			} else {
				str_cols = new String[hvs.length + 2]; //
			}
			str_cols[0] = "����"; //
			for (int i = 0; i < hvs.length; i++) {
				str_cols[i + 1] = hvs[i].toString(); ////
			}
			if (hvs.length > 1) {
				str_cols[hvs.length + 1] = "�ȽϽ��"; //
			}
			DefaultTableModel tableModel = new DefaultTableModel(new String[][] {}, str_cols); //
			String[] str_sqls = new String[hvs.length]; //
			for (int i = 0; i < hvs.length; i++) {
				str_sqls[i] = "select * from " + _tableName + " where prdealpoolid='" + hvs[i].getStringValue("id") + "'"; //һ����SQL
			}
			FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class); //
			Vector v_hvs = service.getHashVoStructReturnVectorByDS(null, str_sqls); //һ�δβ���,�Ӷ��������!!
			String[] str_keys = null; //
			for (int i = 0; i < v_hvs.size(); i++) {
				HashVOStruct hvStruct = (HashVOStruct) v_hvs.get(i); //
				if (str_keys == null && hvStruct != null) {
					str_keys = hvStruct.getHeaderName(); //����!
					break; //
				}
			}
			ArrayList al_notequals_rows = new ArrayList(); ////
			if (str_keys != null) { ////
				for (int i = 0; i < str_keys.length; i++) { //������������!!!
					String[] str_rowData = new String[str_cols.length]; ////
					str_rowData[0] = str_keys[i]; //
					for (int j = 0; j < v_hvs.size(); j++) {
						HashVOStruct hvStruct = (HashVOStruct) v_hvs.get(j); //
						if (hvStruct != null) {
							HashVO[] hvs_item = (HashVO[]) hvStruct.getHashVOs(); //
							if (hvs_item != null && hvs_item.length > 0) { //���������!!!
								String str_value = hvs_item[0].getStringValue(str_keys[i]); //
								if (hvs_item.length > 1) {
									str_value = (str_value == null ? "" : str_value) + "(��Ȼ��" + hvs_item.length + "��)"; //
								}
								str_rowData[j + 1] = str_value; //����ֵ!
							}
						}
					}

					if (hvs.length > 1) {
						if (!compareArrayItemEquals(str_rowData, 1, str_rowData.length - 2)) {
							str_rowData[str_rowData.length - 1] = "�в���"; //
							al_notequals_rows.add(new Integer(i)); //
						}
					}
					tableModel.addRow(str_rowData); //��������
				}
			}

			JTable table = new JTable(tableModel); //
			Integer[] li_selRows = (Integer[]) al_notequals_rows.toArray(new Integer[0]); //
			for (int i = 0; i < al_notequals_rows.size(); i++) {
				table.addRowSelectionInterval((Integer) al_notequals_rows.get(i), (Integer) al_notequals_rows.get(i)); //
			}
			table.setFont(LookAndFeel.font); //
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //
			table.getColumnModel().getColumn(0).setPreferredWidth(100); //
			for (int i = 1; i < str_cols.length - 1; i++) { //����!
				table.getColumnModel().getColumn(i).setPreferredWidth(120); //
			}
			if (hvs.length > 1) {
				table.getColumnModel().getColumn(str_cols.length - 1).setPreferredWidth(70); //
			}
			panel.add(new JScrollPane(table), BorderLayout.CENTER); ////
		} catch (Exception ex) {
			ex.printStackTrace(); //
			panel.add(new JLabel("�����쳣,��������̨�鿴��ϸ��Ϣ!")); //
		}

		JScrollPane scrollPanel = new JScrollPane(panel); //
		//scrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); //
		//scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER); //
		JPanel returnPanel = new JPanel(new BorderLayout()); //
		returnPanel.add(scrollPanel); //
		return returnPanel; //
	}

	private boolean compareArrayItemEquals(String[] _arrays, int _beginPos, int _endPos) {
		for (int i = _beginPos; i <= _endPos; i++) {
			for (int j = _beginPos; j <= _endPos; j++) {
				if (i != j) {
					if (!("" + _arrays[i]).equals("" + _arrays[j])) { //�����һ����һ��,����������!
						return false; //
					}
				}
			}
		}
		return true; //
	}
}
