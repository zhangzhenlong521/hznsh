package cn.com.infostrategy.ui.sysapp.install;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListModel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.hmui.ninepatch.IconFactory;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellEditor_TextArea;

/** 
 * Copyright Pushine
 * @ClassName: cn.com.infostrategy.ui.sysapp.install.InstallScheduleTableEditor 
 * @Description: 
 * @author haoming
 * @date May 16, 2013 2:31:30 PM
 *  
*/
public class InstallScheduleTableEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
	private static final long serialVersionUID = 1L;
	private Pub_Templet_1_ItemVO vo;
	private JTable jtable;

	public InstallScheduleTableEditor(Pub_Templet_1_ItemVO _vo) {
		vo = _vo;
	}

	private Object cvalue;

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		cvalue = value;
		jtable = table;
		int cellWidth = (int) table.getColumnModel().getColumn(column).getPreferredWidth();
		int cellHeight = table.getRowHeight(row);
		if (value instanceof Integer || value instanceof Float) { //如果是文本，则显示进度条
			try {
				float f = Float.parseFloat(value.toString()); //如果可以强转，说明是文本
				table.setRowSelectionInterval(row, 0); //设置选定第一列。否则进度条就不动了。
				JProgressBar bar = new JProgressBar(0, 100) {
					protected void paintComponent(Graphics g) {
						Graphics2D g2d = (Graphics2D) g.create();
						IconFactory.getInstance().getButtonIcon_PressedOrange().draw(g2d, 0, 0, (int) (getWidth() * ((float) getValue() / (float) 100)), getHeight() - 2);
						g2d.setColor(Color.BLACK);
						g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
						g2d.drawString(getString(), getWidth() / 2 - 15, getHeight() - 6);
					}
				};
				bar.setStringPainted(true);
				JPanel panel = new JPanel(null);
				panel.setOpaque(true);
				setComponentBColor(panel, table, isSelected, row);
				bar.setBorder(BorderFactory.createEmptyBorder());
				bar.setValue((int) f);
				bar.setString(f + "%");
				bar.setBounds(5, cellHeight / 2 - 10, cellWidth - 10, 20);
				panel.add(bar);
				return panel;
			} catch (Exception e) { //
				e.printStackTrace();
			}
		}
		if (value != null) {
			JPanel panel = new JPanel(null);
			panel.setOpaque(true);
			setComponentBColor(panel, table, isSelected, row);
			if (value instanceof JComponent) {
				JComponent com = (JComponent) value;
				Dimension minSize = com.getMinimumSize();
				com.setBounds(0, (int) ((cellHeight - minSize.getHeight()) / 2), cellWidth - 20, (int) minSize.getHeight());
				panel.add(com);
			} else {
				JButton btn = new WLTButton(value.toString());
				btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
				btn.setBounds(5, cellHeight / 2 - 10, cellWidth - 20, 20);
				panel.add(btn);
				btn.addActionListener(this);
				if (WLTConstants.MODULE_INSTALL_STATUS_KSJ.equals(value.toString()) || WLTConstants.MODULE_INSTALL_STATUS_YAZ.equals(value.toString())) {
					//添加一个“配置”按钮
					JButton btn_config = new WLTButton("配置");
					btn_config.addActionListener(this);
					btn.setBounds(5, cellHeight / 2 - 10, cellWidth / 2 - 10, 20);
					btn_config.setBounds(5 + cellWidth / 2 - 10, table.getRowHeight(row) / 2 - 10, cellWidth / 2 - 10, 20);
					panel.add(btn_config);
				}
			}
			return panel;
		}
		ListCellEditor_TextArea area = new ListCellEditor_TextArea(vo);
		return area.getCompent();
	}

	public Object getCellEditorValue() {
		return cvalue;
	}

	//强制执行按钮
	public void actionPerformed(ActionEvent e) {
		BillListPanel billListPanel = ((BillListModel) jtable.getModel()).getBillListPanel(); //
		if (billListPanel.getClientProperty("action") != null) {
			BillListHtmlHrefListener lis = (BillListHtmlHrefListener) billListPanel.getClientProperty("action");
			if (lis != null) {
				TableColumn column = jtable.getColumnModel().getColumn(jtable.getSelectedColumn()); //
				BillListHtmlHrefEvent event = new BillListHtmlHrefEvent(((JButton) e.getSource()).getName() + ";" + column.getIdentifier().toString(), jtable.getSelectedRow(), jtable.getSelectedColumn(), false, billListPanel);
				lis.onBillListHtmlHrefClicked(event);
			}
		}

	}

	private void setComponentBColor(JComponent _component, JTable table, boolean isSelected, int row) {
		_component.setBackground(LookAndFeel.tablerowselectbgcolor);
	}

	@Override
	public boolean stopCellEditing() {
		return super.stopCellEditing();
	}
}
