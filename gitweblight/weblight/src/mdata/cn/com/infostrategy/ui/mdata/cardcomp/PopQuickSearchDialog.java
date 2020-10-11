package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.ScrollablePopupFactory;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.mdata.hmui.LAFUtil;

/** 
 * Copyright Pushine
 * @ClassName: cn.com.infostrategy.ui.mdata.cardcomp.PopQuickSearchDialog 
 * @Description: �������ٲ�ѯ����塣���ưٶ�������һ�����������ݾͻᵯ������
 * @author haoming
 * @date Apr 28, 2013 5:16:40 PM
 *  
*/
public class PopQuickSearchDialog extends JPopupMenu implements MouseListener, MouseMotionListener {
	JList list = new JList();
	private String condition;
	JScrollPane scrollPanel;
	private TBUtil tbUtil = TBUtil.getTBUtil();
	private AbstractWLTCompentPanel parent;
	private JComponent showOnComponent;
	private ActionListener clickDeleteAction;
	public final static String MOUSE_STATE_ON = "ON";
	public final static String MOUSE_STATE_CLICKED = "CLICK";
	private boolean showDeleteBtn = false;

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public PopQuickSearchDialog(JComponent _showOnComponent, AbstractWLTCompentPanel _parent, boolean _showDeleteBtn) {
		showOnComponent = _showOnComponent;
		parent = _parent;
		showDeleteBtn = _showDeleteBtn;
		_showOnComponent.putClientProperty("POPMENUTYPE", ScrollablePopupFactory.POP_TYPE_VERTICAL);
		this.putClientProperty("POPMENUTYPE", ScrollablePopupFactory.POP_TYPE_VERTICAL);
	}

	private DefaultListModel model = new DefaultListModel();

	public void setList() {

	}

	//�����б�ֵ
	public void setList(Object[] _list) {
		list = new JList();
		list.setModel(model); //
		list.setCellRenderer(new MyCellRender());//�Զ�����Ⱦ��
		for (int i = 0; i < _list.length; i++) {
			model.add(i, _list[i]);
		}
		if (_list.length > 0) {
			list.setSelectedIndex(0); //Ĭ��ѡ�е�һ��
			list.ensureIndexIsVisible(0);
		}
		list.addMouseListener(this);
		list.addMouseMotionListener(this);
		list.setOpaque(false); //͸��
		list.setFocusable(false); //
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //
		scrollPanel = new JScrollPane(list); //createMatteBorder(1, 1, 0, 1, Color.GRAY)
		scrollPanel.setFocusable(false);
		scrollPanel.getViewport().setFocusable(false); //
		scrollPanel.setOpaque(false); //͸��
		scrollPanel.getViewport().setOpaque(false); //͸��
		if (_list.length >= 8) {
			scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		} else {
			scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		}
		this.add(scrollPanel); //
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void setObject() {
		Object refVO = (Object) list.getSelectedValue();
		if (refVO != null) {
			parent.setObject(refVO);
		}
	}

	public JList getList() {
		return list;
	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public void mousePressed(MouseEvent e) {
		if (showDeleteBtn) {
			if (e.getButton() == MouseEvent.BUTTON1 && e.getX() >= list.getWidth() - 14 && e.getX() < list.getWidth()) {
				list.putClientProperty(MOUSE_STATE_CLICKED, "Y");
			} else {
				list.putClientProperty(MOUSE_STATE_CLICKED, "N");
			}
			list.repaint();
		}
	}

	public void addClickDeleteBtnAction(ActionListener l) {
		clickDeleteAction = l; //���ɾ���¼�
	}

	public void mouseReleased(MouseEvent e) {
		if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1 && (showDeleteBtn ? e.getX() <= list.getWidth() - 14 && e.getX() > 0 : true)) { //���˫��
			super.setVisible(false);
			setObject(); //���ò�������
		} else if (showDeleteBtn && e.getX() >= list.getWidth() - 14 && e.getX() <= list.getWidth()) { //�Ƴ�
			if (clickDeleteAction == null) {
				MessageBox.show(showOnComponent, "û�ж���ɾ���¼�!");
				return;
			}
			if ("Y".equals(list.getClientProperty(MOUSE_STATE_CLICKED))) { //����ɾ��
				ActionEvent event = new ActionEvent(e.getSource(), e.getID(), ""); //��ɾ������
				clickDeleteAction.actionPerformed(event);
				model.remove(list.getSelectedIndex()); // �Ƴ��б�
				double old_height = scrollPanel.getPreferredSize().getHeight();
				resetSize();
				int cz = (int) (old_height - scrollPanel.getPreferredSize().getHeight());
				Window w = SwingUtilities.getWindowAncestor(this);
				w.setSize(this.getWidth(), this.getHeight() - cz); //���¶����С��
				if (list.getModel().getSize() == 0) { //
					setVisible(false);
				}
			}
			list.putClientProperty(MOUSE_STATE_CLICKED, "N");
		}
	}

	public void mouseDragged(MouseEvent e) {

	}

	//����ƶ�״̬��
	public void mouseMoved(MouseEvent e) {
		int i = list.getUI().locationToIndex(list, e.getPoint());
		if (showDeleteBtn) {
			if (e.getX() >= list.getWidth() - 14 && e.getX() < list.getWidth()) {
				list.putClientProperty(MOUSE_STATE_ON, "Y");
				list.repaint(list.getWidth() - 16, 0, 16, list.getHeight());
			} else {
				list.putClientProperty(MOUSE_STATE_ON, "N");
				list.repaint(list.getWidth() - 16, 0, 16, list.getHeight());
			}
		}
		list.setSelectedIndex(i);
	}

	void resetSize() {
		Dimension scrollSize = scrollPanel.getPreferredSize(); //�������������һ��scrollPane�Ĵ�С����popMenu��getPreferredSize�����ᱨ��
		scrollSize.setSize(scrollSize.getWidth() < showOnComponent.getPreferredSize().getWidth() ? showOnComponent.getPreferredSize().getWidth() : scrollSize.getWidth(), list.getPreferredSize().getHeight() < scrollSize.getHeight() ? list.getPreferredSize().getHeight() : scrollSize.getHeight());
		scrollPanel.setPreferredSize(scrollSize);
	}

	//��д����
	public void show(Component invoker, int x, int y) {
		resetSize();
		super.show(invoker, x, y);
		showOnComponent.requestFocus();//�ı���һֱ�н����ɱ༭
	}

	//ѡ��״̬����
	public void moveDown() {
		if (list.getSelectedIndex() < list.getModel().getSize()) {
			list.setSelectedIndex(list.getSelectedIndex() + 1);
			Rectangle rec = list.getCellBounds(list.getSelectedIndex(), list.getSelectedIndex());
			list.scrollRectToVisible(rec);
		}
	}

	//ѡ��״̬����
	public void moveUp() {
		if (list.getSelectedIndex() > 0) {
			list.setSelectedIndex(list.getSelectedIndex() - 1);
			Rectangle rec = list.getCellBounds(list.getSelectedIndex(), list.getSelectedIndex());
			list.scrollRectToVisible(rec);
		}
	}

	private ImageIcon closeImg_clicked = new ImageIcon(TBUtil.getTBUtil().getImageScale(UIUtil.getImage("del.gif").getImage(), 13, 13));

	//
	public class MyCellRender extends DefaultListCellRenderer {
		public Component getListCellRendererComponent(final JList list, Object value, int index, final boolean isSelected, boolean cellHasFocus) {
			JLabel label2 = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			WLTLabel label = new WLTLabel(label2.getText()) {
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					if (isSelected && showDeleteBtn) {
						int x = getWidth() - closeImg_clicked.getIconWidth() - 2;
						int y = (getHeight() - closeImg_clicked.getIconHeight()) / 2;
						if ("Y".equals(list.getClientProperty(MOUSE_STATE_CLICKED))) {
							x++;
							y++;
						}
						g.drawImage(closeImg_clicked.getImage(), x, y, this);
						if ("Y".equals(list.getClientProperty(MOUSE_STATE_ON))) {
							Graphics2D g2d = (Graphics2D) g.create();
							LAFUtil.drawRoundRect(g2d, x, y, closeImg_clicked.getIconWidth(), closeImg_clicked.getIconHeight(), 4, Color.WHITE, 1.5f);
						}
					}

				}
			};
			label.setBorder(BorderFactory.createEmptyBorder(2, 1, 2, 1));
			label.setOpaque(true); //
			label.setHorizontalAlignment(SwingConstants.LEFT); //
			if (isSelected) {
				label.setBackground(super.getBackground()); //
			} else {
				label.setBackground(super.getBackground()); //
			}
			if (!tbUtil.isEmpty(condition)) {
				String[] str_items = tbUtil.split(condition.trim(), " ");
				for (int i = 0; i < str_items.length; i++) {
					label.addStrItemColor(str_items[i].toLowerCase(), Color.RED); //
				}
			}
			return label; //
		}
	}
}
