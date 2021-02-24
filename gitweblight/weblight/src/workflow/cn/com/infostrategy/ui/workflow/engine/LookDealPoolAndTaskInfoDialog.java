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
 * 查看所有关键详细信息,极其重要的工具,它可一眼清的比较流程相关信息!!
 * 最后实施人员进行问题定位时就靠它了!
 * @author xch
 *
 */
public class LookDealPoolAndTaskInfoDialog extends BillDialog {

	private HashVO[] hvs = null; //

	public LookDealPoolAndTaskInfoDialog(Container _parent, String _title, int _width, int li_height, HashVO[] _hvs) {
		super(_parent, _title, _width, li_height);
		this.hvs = _hvs; //
		initialize(); //初始化页面
	}

	/**
	 * 初始化页面!
	 */
	private void initialize() {
		JLabel label_info = new JLabel("提示:该页面中的数据用于在发生流程问题时快速分析原因用!一个结点是一列数据!"); //
		label_info.setForeground(Color.RED); //
		this.getContentPane().add(label_info, BorderLayout.NORTH); //

		JSplitPane split_1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getTaskDealPanel(), getTaskOffPanel()); //
		split_1.setDividerLocation(300); //
		split_1.setOneTouchExpandable(true); //

		JSplitPane split_2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getDealPoolPanel(), split_1); // 加入
		split_2.setDividerLocation(300); //
		split_2.setOneTouchExpandable(true); //

		this.getContentPane().add(split_2, BorderLayout.CENTER); //
	}

	private JPanel getDealPoolPanel() {
		JPanel panel = new JPanel(new BorderLayout()); //
		panel.add(new JLabel("流程任务表(select * from pub_wf_dealpool where id='')"), BorderLayout.NORTH); //
		String[] str_cols = null;
		if (hvs.length == 1) {
			str_cols = new String[hvs.length + 1]; //
		} else {
			str_cols = new String[hvs.length + 2]; //
		}
		str_cols[0] = "列名"; //
		for (int i = 0; i < hvs.length; i++) {
			str_cols[i + 1] = hvs[i].toString(); ////
		}
		if (hvs.length > 1) {
			str_cols[hvs.length + 1] = "比较结果"; //
		}
		DefaultTableModel tableModel = new DefaultTableModel(new String[][] {}, str_cols); //
		String[] str_keys = hvs[0].getKeys(); //所有列!
		ArrayList al_notequals_rows = new ArrayList(); //
		for (int i = 0; i < str_keys.length; i++) { //各行!
			String[] str_rowData = new String[hvs.length + 2]; //
			str_rowData[0] = str_keys[i]; //列名
			for (int j = 0; j < hvs.length; j++) { //各个结点数据!!
				str_rowData[j + 1] = hvs[j].getStringValue(str_keys[i]); //
			}
			if (hvs.length > 1) {
				if (!compareArrayItemEquals(str_rowData, 1, str_rowData.length - 2)) {
					str_rowData[hvs.length + 1] = "有差异"; //
					al_notequals_rows.add(new Integer(i)); //
				}
			}
			tableModel.addRow(str_rowData); //插入数据
		}
		JTable table = new JTable(tableModel); //
		Integer[] li_selRows = (Integer[]) al_notequals_rows.toArray(new Integer[0]); //
		for (int i = 0; i < al_notequals_rows.size(); i++) {
			table.addRowSelectionInterval((Integer) al_notequals_rows.get(i), (Integer) al_notequals_rows.get(i)); //
		}
		table.setFont(LookAndFeel.font); //
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //
		table.getColumnModel().getColumn(0).setPreferredWidth(100); //
		for (int i = 1; i < str_cols.length - 1; i++) { //各行!
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

	//待办箱
	private JPanel getTaskDealPanel() {
		return getTaskPanelByPar("待办箱(select * from pub_task_deal where prdealpoolid='')", "pub_task_deal"); //
	}

	private JPanel getTaskOffPanel() {
		return getTaskPanelByPar("已办箱(select * from pub_task_off where prdealpoolid='')", "pub_task_off"); ///
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
			str_cols[0] = "列名"; //
			for (int i = 0; i < hvs.length; i++) {
				str_cols[i + 1] = hvs[i].toString(); ////
			}
			if (hvs.length > 1) {
				str_cols[hvs.length + 1] = "比较结果"; //
			}
			DefaultTableModel tableModel = new DefaultTableModel(new String[][] {}, str_cols); //
			String[] str_sqls = new String[hvs.length]; //
			for (int i = 0; i < hvs.length; i++) {
				str_sqls[i] = "select * from " + _tableName + " where prdealpoolid='" + hvs[i].getStringValue("id") + "'"; //一条条SQL
			}
			FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class); //
			Vector v_hvs = service.getHashVoStructReturnVectorByDS(null, str_sqls); //一次次查下,从而提高性能!!
			String[] str_keys = null; //
			for (int i = 0; i < v_hvs.size(); i++) {
				HashVOStruct hvStruct = (HashVOStruct) v_hvs.get(i); //
				if (str_keys == null && hvStruct != null) {
					str_keys = hvStruct.getHeaderName(); //列名!
					break; //
				}
			}
			ArrayList al_notequals_rows = new ArrayList(); ////
			if (str_keys != null) { ////
				for (int i = 0; i < str_keys.length; i++) { //遍历各行数据!!!
					String[] str_rowData = new String[str_cols.length]; ////
					str_rowData[0] = str_keys[i]; //
					for (int j = 0; j < v_hvs.size(); j++) {
						HashVOStruct hvStruct = (HashVOStruct) v_hvs.get(j); //
						if (hvStruct != null) {
							HashVO[] hvs_item = (HashVO[]) hvStruct.getHashVOs(); //
							if (hvs_item != null && hvs_item.length > 0) { //如果有数据!!!
								String str_value = hvs_item[0].getStringValue(str_keys[i]); //
								if (hvs_item.length > 1) {
									str_value = (str_value == null ? "" : str_value) + "(竟然有" + hvs_item.length + "条)"; //
								}
								str_rowData[j + 1] = str_value; //设置值!
							}
						}
					}

					if (hvs.length > 1) {
						if (!compareArrayItemEquals(str_rowData, 1, str_rowData.length - 2)) {
							str_rowData[str_rowData.length - 1] = "有差异"; //
							al_notequals_rows.add(new Integer(i)); //
						}
					}
					tableModel.addRow(str_rowData); //插入数据
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
			for (int i = 1; i < str_cols.length - 1; i++) { //各行!
				table.getColumnModel().getColumn(i).setPreferredWidth(120); //
			}
			if (hvs.length > 1) {
				table.getColumnModel().getColumn(str_cols.length - 1).setPreferredWidth(70); //
			}
			panel.add(new JScrollPane(table), BorderLayout.CENTER); ////
		} catch (Exception ex) {
			ex.printStackTrace(); //
			panel.add(new JLabel("发生异常,请至控制台查看详细信息!")); //
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
					if (!("" + _arrays[i]).equals("" + _arrays[j])) { //如果有一个不一样,则立即返回!
						return false; //
					}
				}
			}
		}
		return true; //
	}
}
