package cn.com.infostrategy.ui.report;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;

/**
 * 进行过滤时,弹出选择的对话框!
 * 在该框中列出某一个组的维度的所有值,然后采用JList,选择过滤的值,返回
 * 找到时要将曾经过滤过的值带进来,即能看到所有值,过滤的值,不被过滤的值!一眼清.
 * @author xch
 *
 */
public class MultiLevelChooseFilterDialog extends BillDialog implements ActionListener {

	private static final long serialVersionUID = 5036379844871708304L;
	private String[][] allFilterData = null;
	private JList list = null;
	private WLTButton btn_confirm, btn_cancel;
	private WLTButton btn_selectAll, btn_clearAll; //
	private int closeType = -1;

	private String[][] returnFilterField = null; // 

	public MultiLevelChooseFilterDialog(java.awt.Container _parent, String _title, String[][] _allFilterData) {
		super(_parent, _title, 300, 350); //
		this.allFilterData = _allFilterData; //
		initialize(); //
	}

	/**
	 * 初始化页面
	 */
	private void initialize() {
		list = new JList(createData(allFilterData));
		list.setBackground(Color.WHITE); //
		list.setCellRenderer(new CheckListRenderer());
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setBorder(new EmptyBorder(0, 4, 0, 0));
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				onChangeClick(e);
			}
		});
		JScrollPane sp = new JScrollPane(list); //

		this.getContentPane().add(getNorthPanel(), BorderLayout.NORTH); //
		this.getContentPane().add(sp, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	private void onChangeClick(MouseEvent e) {
		int index = list.locationToIndex(e.getPoint()); //得到选中的是第几个
		CheckableItem item = (CheckableItem) list.getModel().getElementAt(index); //取得其值
		item.setSelected(!item.isSelected()); //修改值
		Rectangle rect = list.getCellBounds(index, index); //取得范围
		list.repaint(rect); //重绘!
	}

	/**
	 * 
	 * @return
	 */
	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		btn_confirm = new WLTButton("确定"); //
		btn_cancel = new WLTButton("取消"); //
		btn_confirm.addActionListener(this);
		btn_cancel.addActionListener(this);
		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel;
	}

	/**
	 * 
	 * @return
	 */
	private JPanel getNorthPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		btn_selectAll = new WLTButton("全选"); //
		btn_clearAll = new WLTButton("全消"); //
		btn_selectAll.addActionListener(this);
		btn_clearAll.addActionListener(this);
		panel.add(btn_selectAll); //
		panel.add(btn_clearAll); //
		return panel;
	}

	public String[][] getReturnFilterField() {
		return returnFilterField;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		} else if (e.getSource() == btn_selectAll) {
			onSelectAll(true);
		} else if (e.getSource() == btn_clearAll) {
			onSelectAll(false);
		}
	}

	private void onSelectAll(boolean _checked) {
		int li_size = list.getModel().getSize();
		for (int i = 0; i < li_size; i++) {
			CheckableItem item = (CheckableItem) list.getModel().getElementAt(i);
			item.setSelected(_checked);
		}
		list.repaint();
	}

	public void onConfirm() {
		int li_size = list.getModel().getSize();
		returnFilterField = new String[li_size][2];
		boolean bo_ischeck = false;
		for (int i = 0; i < li_size; i++) {
			CheckableItem item = (CheckableItem) list.getModel().getElementAt(i);
			returnFilterField[i][0] = item.toString();
			returnFilterField[i][1] = item.isSelected() ? "1" : "0"; //如果选中,则是1,否则是0
			if (item.isSelected()) {
				bo_ischeck = true; //
			}
		}

		if (!bo_ischeck) {
			MessageBox.showSelectOne(this); //
			return; //
		}
		closeType = 1; //
		this.dispose();
	}

	public void onCancel() {
		closeType = 2; //
		this.dispose();
	}

	public int getCloseType() {
		return closeType;
	}

	private CheckableItem[] createData(String[][] strs) {
		int n = strs.length;
		CheckableItem[] items = new CheckableItem[n];
		for (int i = 0; i < n; i++) {
			items[i] = new CheckableItem(strs[i][0]); //
			if (strs[i][1].equals("0")) {
				items[i].setSelected(false);
			} else if (strs[i][1].equals("1")) {
				items[i].setSelected(true);
			}
		}
		return items;
	}

	class CheckableItem {
		private String str;
		private boolean isSelected;

		public CheckableItem(String str) {
			this.str = str;
			isSelected = false;
		}

		public void setSelected(boolean b) {
			isSelected = b;
		}

		public boolean isSelected() {
			return isSelected;
		}

		public String toString() {
			return str;
		}
	}

	class CheckListRenderer extends JCheckBox implements ListCellRenderer {

		public CheckListRenderer() {
			setBackground(UIManager.getColor("List.textBackground"));
			setForeground(UIManager.getColor("List.textForeground"));
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus) {
			setEnabled(list.isEnabled());
			setSelected(((CheckableItem) value).isSelected());
			setFont(list.getFont());
			setText(value.toString());
			return this;
		}
	}

}
