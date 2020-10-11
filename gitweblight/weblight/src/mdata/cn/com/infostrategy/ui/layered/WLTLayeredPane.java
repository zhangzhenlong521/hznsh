package cn.com.infostrategy.ui.layered;

import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

/**
 * �㲼�֣����һ����ʾ
 * ÿһ�㶼���Լ��Ĳ��֡�
 * @author haoming
 * create by 2015-9-24
 */
public class WLTLayeredPane extends JComponent implements ComponentListener {
	JLayeredPane layeredPane = new JLayeredPane();
	HashMap map = new HashMap();

	public WLTLayeredPane() {
		layeredPane.addComponentListener(this); //����������¼�
		this.setLayout(new BorderLayout());//��Ӳ���
		this.add(layeredPane);//�Ų����
	}

	/**
	 * ���һ��
	 * @param _com Ҫ��ӵ����
	 * @param _layout �ڲ��еĲ���
	 * @param _level ����
	 */
	public void addLevel(JComponent _com, LayoutManager _layout, Integer _level) {
		JPanel container = new JPanel(_layout);
		container.setOpaque(false);
		map.put(_level, container);
		container.add(_com);
		container.setBounds(0, 0, this.getWidth(), this.getHeight());
		layeredPane.add(container, _level);
	}

	/*
	 * 
	 */
	public JPanel getLevelPanel(Integer _level) {
		return (JPanel) map.get(_level);
	}

	public void componentHidden(ComponentEvent componentevent) {

	}

	public void componentMoved(ComponentEvent componentevent) {

	}

	public void componentResized(ComponentEvent componentevent) {
		JPanel jp[] = (JPanel[]) map.values().toArray(new JPanel[0]);
		for (int i = 0; i < jp.length; i++) {
			jp[i].setBounds(0, 0, this.getWidth(), this.getHeight());
		}
	}

	public void componentShown(ComponentEvent componentevent) {

	}
}
