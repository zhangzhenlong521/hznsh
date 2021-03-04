package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cn.com.infostrategy.ui.common.LookAndFeel;

import com.sun.java.swing.plaf.windows.WindowsTreeUI;

/**
 * 卡片中的标题栏,点击后可以展开,收缩卡片中下面的行
 * @author xch
 *
 */
public class CardGroupTitlePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private String titlename = null; //
	private int row = -1; //
	private JButton button = null; //
	private java.awt.Color bgColor = LookAndFeel.groupTitlecolor;//new Color(238, 221, 255); //

	private boolean expandState = true; //展开状态

	public CardGroupTitlePanel(String _title, int _row) {
		this.titlename = _title; //
		this.row = _row; //
		this.setLayout(new BorderLayout()); //
		this.setBackground(bgColor); //

		button = new JButton(WindowsTreeUI.ExpandedIcon.createExpandedIcon()); //
		button.setOpaque(false);  //
		button.setToolTipText("按住Ctrl键点击可以全部展开/收缩");  //
		button.setBackground(bgColor); //
		button.setBorder(BorderFactory.createEmptyBorder()); //
		button.putClientProperty("grouptitle", titlename); //
		button.putClientProperty("row", new Integer(_row)); //
		button.setPreferredSize(new Dimension(16, 16)); //
		this.add(button, BorderLayout.WEST);//
		
		JLabel label = new JLabel(titlename);  //
		this.add(label, BorderLayout.CENTER);//
	}

	public JButton getButton() {
		return button; //
	}

	/**
	 * 设置展开状态
	 * @param _expand
	 */
	public void setExpandSate(boolean _expand) {
		expandState = _expand; //
		if (expandState) {
			button.setIcon(WindowsTreeUI.CollapsedIcon.createExpandedIcon()); //
		} else {
			button.setIcon(WindowsTreeUI.CollapsedIcon.createCollapsedIcon()); //
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean getExpandState() {
		return expandState; //
	}

	/**
	 * 表示该标题栏一卡片中是第几行
	 * @return
	 */
	public int getRow() {
		return row; //
	}

	public String getTitlename() {
		return titlename;
	}
}
