package cn.com.infostrategy.ui.sysapp.install;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.hmui.ninepatch.IconFactory;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellRender_JLabel;

/** 
 * Copyright Pushine
 * @ClassName: cn.com.infostrategy.ui.sysapp.install.InstallScheduleTableRenderer 
 * @Description: 安装 进度框自定义表格渲染器。
 * @author haoming
 * @date May 16, 2013 2:30:48 PM
 *  
*/
public class InstallScheduleTableRenderer implements TableCellRenderer {
	Pub_Templet_1_ItemVO vo;

	public InstallScheduleTableRenderer(Pub_Templet_1_ItemVO _vo) {
		vo = _vo;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		int cellWidth = (int) table.getColumnModel().getColumn(column).getPreferredWidth();
		int cellHeight = table.getRowHeight(row);
		if (value instanceof Integer || value instanceof Float) { //如果是文本，则显示进度条
			try {
				float f = Float.parseFloat(value.toString()); //如果可以强转，说明是文本
				JProgressBar bar = new JProgressBar(0, 100) {
					protected void paintComponent(Graphics g) {
						Graphics2D g2d = (Graphics2D) g.create();
						IconFactory.getInstance().getButtonIcon_PressedOrange().draw(g2d, 0, 0, (int) (getWidth() * ((float) getValue() / (float) 100)), getHeight() - 2);
						g2d.setColor(Color.BLACK);
						g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
						g2d.drawString(getString(), getWidth() / 2 - 18, getHeight() - 6);
					}
				};
				bar.setStringPainted(true);
				JPanel panel = new JPanel(null);
				panel.setOpaque(true);
				setComponentBColor(panel, table, isSelected, row);
				bar.setBorder(BorderFactory.createEmptyBorder());
				bar.setValue((int) f);
				bar.setString(f + "%");
				bar.setBounds(5, cellHeight / 2 - 10, cellWidth - 20, 20);
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
				com.setBounds(0, (int)((cellHeight-minSize.getHeight())/2), cellWidth - 20, (int)minSize.getHeight());
				panel.add(com);
			} else {
				JButton btn = new WLTButton(value.toString());
				btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
				btn.setBounds(5, cellHeight / 2 - 10, cellWidth - 20, 20);
				panel.add(btn);
				if (WLTConstants.MODULE_INSTALL_STATUS_KSJ.equals(value.toString()) || WLTConstants.MODULE_INSTALL_STATUS_YAZ.equals(value.toString())) {
					//添加一个“配置”按钮
					JButton btn_config = new WLTButton("配置");
					btn.setBounds(5, cellHeight / 2 - 10, cellWidth / 2 - 10, 20);
					btn_config.setBounds(5 + cellWidth / 2 - 10, table.getRowHeight(row) / 2 - 10, cellWidth / 2 - 10, 20);
					panel.add(btn_config);
				}
			}
			return panel;
		}
		ListCellRender_JLabel label = new ListCellRender_JLabel(vo);
		return label.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}

	private void setComponentBColor(JComponent _component, JTable table, boolean isSelected, int row) {
		if (isSelected) {
			_component.setBackground(LookAndFeel.tablerowselectbgcolor);
		} else {
			Color defColor = (Color) table.getClientProperty("$rowbackground_" + row); //
			if (defColor != null) { //如果指定了背景颜色,则直接使用!!!
				_component.setBackground(defColor); //
			} else { //否则是奇偶变色!!!
				if (row % 2 == 0) {
					_component.setBackground(LookAndFeel.table_bgcolor_odd); //
				} else {
					_component.setBackground(LookAndFeel.tablebgcolor); //
				}
			}
		}
	}
}
