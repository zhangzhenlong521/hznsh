package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.Hashtable;

import javax.swing.JPanel;

/**
 * ֧�ֶ������,������CardLayout���ֽ����������,�Ժ������ǰ��,���˵ȹ���!
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
		//this.add(getNotrhPanel(), BorderLayout.NORTH); //�Ժ���������ǰ��,���˵ȹ���!
	}

	public void addLevelPanel(String _levelname, java.awt.Component _panel) {
		toftPanel.add(_panel, _levelname); //����ĳ����
		ht_levels.put(_levelname, _panel); //

	}

	public void removeLevelPanel(String _levelname) {
		toftPanel.remove((java.awt.Component) ht_levels.get(_levelname));//��ǰ��cardLayout.removeLayoutComponent((java.awt.Component) ht_levels.get(_levelname)); 
		//ֻ���ڲ�����ȥ���ˣ���toftPanel�л����иÿؼ���������һͼ������ɾ���������̺��ܻ���һ����廹��ʾ�š����/2012-03-21��
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
