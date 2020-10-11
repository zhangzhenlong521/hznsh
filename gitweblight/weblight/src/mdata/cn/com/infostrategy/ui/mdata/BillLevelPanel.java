package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.Hashtable;

import javax.swing.JPanel;

/**
 * 支持多层的面板,就是用CardLayout布局将多个面板加入,以后会增加前进,后退等功能!
 * @author xch
 *
 */
public class BillLevelPanel extends JPanel {

	private static final long serialVersionUID = 8916811343536311428L;

	private JPanel toftPanel = new JPanel(); //
	private CardLayout cardLayout = new CardLayout(); //

	private Hashtable ht_levels = new Hashtable(); //

	public BillLevelPanel() {
		this.setLayout(new BorderLayout()); //
		toftPanel.setLayout(cardLayout); //
		this.add(toftPanel, BorderLayout.CENTER); //
		//this.add(getNotrhPanel(), BorderLayout.NORTH); //以后这里会加入前进,后退等功能!
	}

	public void addLevelPanel(String _levelname, java.awt.Component _panel) {
		toftPanel.add(_panel, _levelname); //加入某个层
		ht_levels.put(_levelname, _panel); //

	}

	public void removeLevelPanel(String _levelname) {
		toftPanel.remove((java.awt.Component) ht_levels.get(_levelname));//以前用cardLayout.removeLayoutComponent((java.awt.Component) ht_levels.get(_levelname)); 
		//只是在布局中去除了，但toftPanel中还是有该控件，导致在一图两表中删除所有流程后，总会有一个面板还显示着【李春娟/2012-03-21】
		ht_levels.remove(_levelname); //
	}

	public void showLevel(String _levelname) {
		cardLayout.show(toftPanel, _levelname); //
	}

	public java.awt.Component getLevelPanel(String _levelname) {
		return (java.awt.Component) ht_levels.get(_levelname);
	}

	public Hashtable getHt_levels() {
		return ht_levels;
	}

}
