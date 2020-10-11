package cn.com.pushworld.salary.ui.target.p040;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListModel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * �����������Ѿ��������˰���Ÿ㶨�����׳��������ԭ�򡣵�����ͻ�����༭�¼��������趨��bo_ifProgramIsEditing�����������
 * �����������stopCellEditing�����ԡ���Ҫ�趨table.setValueAt�������༭��ʽ��̫���ᡣ
 * ���淵��ֵ������ BillItemVO ��������������isValueChanged���᲻ִ�б༭��ʽ��
 * 
 * @author haoming create by 2013-7-11
 */
public class ScoreCellEditor extends AbstractCellEditor implements TableCellEditor {
	private static final long serialVersionUID = -6072002002138287635L;
	private Pub_Templet_1_ItemVO itemvo;
	private Object cvalue;
	JPanel panel;
	private JTable table;
	private int row;
	private int col;
	private BillListPanel billListPanel;
	private int cellHeight;
	private boolean needRefine = TBUtil.getTBUtil().getSysOptionBooleanValue("��������۷ֲ����Ƿ�ϸ��", false);

	public ScoreCellEditor(Pub_Templet_1_ItemVO _vo) {
		itemvo = _vo;
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		cvalue = value;
		this.row = row;
		this.col = column;
		this.table = table;
		cellHeight = table.getRowHeight(row);
		billListPanel = ((BillListModel) table.getModel()).getBillListPanel(); //
		panel = new JPanel(new BorderLayout());
		panel.setBackground(LookAndFeel.tablerowselectbgcolor);
		JButton btn = new WLTButton("");
		if (value == null || "".equals(value)) {
			btn.setText("��ѡ��");
		} else {
			btn.setText("-" + String.valueOf(value) + "%");
		}
		billListPanel.bo_ifProgramIsEditing = true;
		if (itemvo.getItemkey().equalsIgnoreCase("checkdratio")) {
			Object status = billListPanel.getValueAt(row, "status");
			if (status == null | String.valueOf(status).equals(DeptTargetScoredWKPanel.SCORE_STATUS_INIT)) { // û����д
				btn.addMouseListener(new MyClickAction());
			} else {
				btn.setEnabled(false);
			}
		} else if (itemvo.getItemkey().equalsIgnoreCase("recheckdratio")) {
			if (billListPanel.getClientProperty("reedit") != null) {
				btn.addMouseListener(new MyClickAction());
			} else {
				btn.setEnabled(false);
			}
		}
		btn.setBorder(BorderFactory.createEmptyBorder());
		btn.setBackground(LookAndFeel.tablerowselectbgcolor);
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		panel.add(btn);
		return panel;
	}

	public Object getCellEditorValue() {
		if (cvalue == null) {
			billListPanel.bo_ifProgramIsEditing = true;
		}
		return cvalue;
	}

	JPopupMenu popmenu;

	@Override
	public boolean isCellEditable(EventObject eventobject) {
		JTable table = (JTable) eventobject.getSource();
		row = table.getSelectedRow();
		if (row < 0) {
			return false;
		}
		billListPanel = ((BillListModel) table.getModel()).getBillListPanel(); //
		Object edit = billListPanel.getClientProperty("editable");
		if (edit != null && !Boolean.valueOf(edit + "")) {
			return false;
		}
		if (itemvo.getItemkey().equalsIgnoreCase("checkdratio")) {
			Object status = billListPanel.getValueAt(row, "status");
			if (status == null | String.valueOf(status).equals(DeptTargetScoredWKPanel.SCORE_STATUS_INIT)) { // û����д
				return true;
			}
		} else if (itemvo.getItemkey().equalsIgnoreCase("recheckdratio")) {
			if (billListPanel.getClientProperty("reedit") != null) {
				return true;
			}
		}
		return false;
	}

	class MyClickAction extends MouseAdapter implements ActionListener {
		public void mousePressed(MouseEvent mouseevent) {
			billListPanel.bo_ifProgramIsEditing = false;
			if (popmenu == null) {
				popmenu = new JPopupMenu();
				if (needRefine) {
					for (int i = 0; i <= 4; i++) {
						JMenuItem item = new JMenuItem("-" + i * 5 + "%");
						item.addActionListener(this);
						popmenu.add(item);
					}
					for (int i = 3; i <= 10; i++) {
						JMenuItem item = new JMenuItem("-" + i * 10 + "%");
						item.addActionListener(this);
						popmenu.add(item);
					}
				} else {
					for (int i = 0; i <= 10; i++) {
						JMenuItem item = new JMenuItem("-" + i * 10 + "%");
						item.addActionListener(this);
						popmenu.add(item);
					}
				}
			}
			if (cvalue == null) {
				popmenu.show(panel, mouseevent.getX(), mouseevent.getY() - 19 * 2 - 7);
			} else {
				int value = Integer.parseInt(String.valueOf(cvalue));
				popmenu.show(panel, mouseevent.getX(), mouseevent.getY() - 19 * value / 10 - 7); // ��֤����
			}
		}

		public void actionPerformed(ActionEvent actionevent) {
			if (actionevent.getSource() instanceof JMenuItem) {
				JMenuItem item = ((JMenuItem) (actionevent.getSource()));
				if (cvalue == null || !item.getText().equals(String.valueOf(cvalue))) { // ǰ��ֵ��һ��
					cvalue = item.getText(); // ���ﲻ�ܼ̳� BillItemVO ��������������isValueChanged���᲻ִ�б༭��ʽ��
					billListPanel.bo_ifProgramIsEditing = false;
					String str = String.valueOf(cvalue);
					cvalue = str.substring(1, str.length() - 1);
					table.setValueAt(cvalue, row, col);
				}
			}
		}
	}

	public boolean stopCellEditing() {
		return super.stopCellEditing();
	}
}
