package cn.com.infostrategy.ui.common;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

/**
 * �����ı���,��������Windows������Ҽ�ʱ����"����/ճ��/ѡ��ȫ��"
 * @author xch
 *
 */
public class WLTTextArea extends JTextArea implements ActionListener {

	private JPopupMenu popMenu = null; //�����˵�
	private JMenuItem menuItem_cut, menuItem_copy, menuItem_paste, menuItem_selectall; //
	private boolean isCanCopyText = true; //�Ƿ���Կ����ı�����? �еĵط��ǲ����������ֵ�,�������߿��ٰ�������Ϊ���ǹؼ�˼��,�±��������ֿ��ٸ��ƣ���xch/2012-02-28��

	public WLTTextArea() {
		super();
		initlize(); //
	}

	public WLTTextArea(Document doc, String text, int rows, int columns) {
		super(doc, text, rows, columns);
		initlize(); //
	}

	public WLTTextArea(Document doc) {
		super(doc);
		initlize(); //
	}

	public WLTTextArea(int rows, int columns) {
		super(rows, columns);
		initlize(); //
	}

	public WLTTextArea(String text, int rows, int columns) {
		super(text, rows, columns);
		initlize(); //
	}

	public WLTTextArea(String text) {
		super(text);
		initlize(); //
	}

	/**
	 * ��ʼ��,�������Ҽ��¼�
	 */
	private void initlize() {
		this.setToolTipText("��סCtrl��˫���ɷŴ�"); //
		this.setFont(LookAndFeel.font);
		this.setLineWrap(true); //
		addComponentUndAndRedoFunc(this); //����undo
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) { //������Ҽ�
					onMouseRightClicked(e.getX(), e.getY()); //
				} else if (e.getButton() == MouseEvent.BUTTON1) {
					if (e.isControlDown() && e.getClickCount() == 2) { //���
						onMaxShow(); //
					}
				}
			}
		});
	}

	private void onMaxShow() {
		if (this.isEditable()) {
			if (SplashWindow.window != null) {
				SplashWindow.window.closeWindow(); //
			}
			TextAreaDialog dialog = new TextAreaDialog(this, "������ʾ", this.getText(), 1000, 618, Color.WHITE, false, this.isCanCopyText); //
			JTextArea jta_context = dialog.getJta_context();
			jta_context.setOpaque(true);
			jta_context.setEditable(true);
			jta_context.setForeground(LookAndFeel.inputforecolor_enable); //��Чʱ��ǰ��ɫ
			jta_context.setBackground(LookAndFeel.inputbgcolor_enable); //��Чʱ�ı���ɫ
			jta_context.setText(jta_context.getText());
			dialog.setVisible(true); //
			if (dialog.getCloseType() == 1) {
				this.setText(jta_context.getText());
			}
		} else {
			if (SplashWindow.window != null) {
				SplashWindow.window.closeWindow(); //
			}
			TextAreaDialog dialog = new TextAreaDialog(this, "������ʾ", this.getText(), 1000, 618, new Color(240, 240, 240), false, this.isCanCopyText); //
			JTextArea jta_context = dialog.getJta_context();
			jta_context.setOpaque(true);
			jta_context.setEditable(false);
			jta_context.setForeground(LookAndFeel.inputforecolor_disable); //��Чʱ��ǰ��ɫ
			jta_context.setBackground(LookAndFeel.inputbgcolor_disable); //��Чʱ�ı���ɫ
			jta_context.setText(jta_context.getText());
			dialog.setVisible(true); //
		}
	}

	/**
	 * ������Ҽ�ʱ����
	 * @param _x
	 * @param _y
	 */
	private void onMouseRightClicked(int _x, int _y) {
		if (popMenu == null) {
			popMenu = new JPopupMenu(); //
			menuItem_cut = new JMenuItem("����"); //
			menuItem_copy = new JMenuItem("����"); //
			menuItem_paste = new JMenuItem("ճ��"); //
			menuItem_selectall = new JMenuItem("ѡ��ȫ��"); //
			menuItem_cut.addActionListener(this); //
			menuItem_copy.addActionListener(this); //
			menuItem_paste.addActionListener(this); //
			menuItem_selectall.addActionListener(this); //
			popMenu.add(menuItem_cut); //
			popMenu.add(menuItem_copy); //
			popMenu.add(menuItem_paste); //
			popMenu.add(menuItem_selectall); //
		}
		menuItem_cut.setVisible(this.isEditable());//���ɱ༭ʱ����Ӧ������Ϊ�����ã������ͻ����"����"�����ã�"����"���ã�"ճ��"�����ã�"ѡ��ȫ��"���ã��о����ң����Ը�Ϊ���ɼ��������/2012-02-24��
		menuItem_paste.setVisible(this.isEditable()); //

		if (!isCanCopyText) { //�����������,��ҵ���������ť!! ��Ϊ�еĵط�����������,����������߰���!
			menuItem_copy.setEnabled(false); //
			menuItem_paste.setEnabled(false); //
			menuItem_selectall.setEnabled(false); //
		}
		popMenu.show(this, _x, _y); //��ʾ
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == menuItem_copy) {
			onCopy();
		} else if (e.getSource() == menuItem_paste) {
			onPaste();
		} else if (e.getSource() == menuItem_selectall) {
			onSelectAll(); //
		} else if (e.getSource() == menuItem_cut) {
			onCutText(); //
		}
	}

	/**
	 * �����ļ�����
	 */
	protected void onCutText() {
		StringSelection ss = new StringSelection(this.getSelectedText()); //ȡ��ѡ�������
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null); //��ϵͳ������������
		try {
			this.replaceSelection(""); //
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	/**
	 * ����
	 */
	private void onCopy() {
		StringSelection ss = new StringSelection(this.getSelectedText()); //ȡ��ѡ�������
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null); //��ϵͳ������������
	}

	/**
	 * ճ��
	 */
	private void onPaste() {
		Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		try {
			if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				String text = (String) t.getTransferData(DataFlavor.stringFlavor); //
				if (text == null) {
					return;
				}
				int li_start = this.getSelectionStart(); //
				int li_end = this.getSelectionEnd(); //
				if (li_start == li_end) {
					this.insert(text, this.getSelectionStart());
				} else {
					this.replaceSelection(text); //
				}
			}
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	/**
	 * ѡ������
	 */
	private void onSelectAll() {
		this.selectAll(); //
	}

	/**
	 * ����ϵͳ�����ˡ����߿��ٰ�����,����Щ��Ϣ���ǹؼ���Ϣ,�ǲ���������!!!
	 * ������Ҫ�ع�Ĭ�ϵ�copy����,����ֹ����ʹ�á�Ctrl+C�������ݿ�����! ��xch/2012-02-28��
	 */
	@Override
	public void copy() {
		if (isCanCopyText) {
			super.copy(); //
		}
	}

	/**
	 * ����ϵͳ�����ˡ����߿��ٰ�����,����Щ��Ϣ���ǹؼ���Ϣ!!!
	 *  ������Ҫ�ع�Ĭ�ϵ�copy����,����ֹ����ʹ�á�Ctrl+C�������ݿ�����! ��xch/2012-02-28��
	 */
	@Override
	public void cut() {
		if (isCanCopyText) {
			super.cut(); //
		}
	}

	public boolean isCanCopyText() {
		return isCanCopyText;
	}

	public void setCanCopyText(boolean isCanCopyText) {
		this.isCanCopyText = isCanCopyText;
	}

	//�����齨�������ͳ�������
	protected void addComponentUndAndRedoFunc(JTextComponent textComp) {
		final UndoManager undo = new UndoManager();
		Document doc = textComp.getDocument();
		doc.addUndoableEditListener(new UndoableEditListener() {
			public void undoableEditHappened(UndoableEditEvent e) {
				undo.addEdit(e.getEdit());
			}
		});

		//�����¼�����
		textComp.getActionMap().put("Undo", new AbstractAction("Undo") {
			public void actionPerformed(ActionEvent e) {
				if (undo.canUndo()) {
					undo.undo();
				}
			}
		});
		//Ϊ����󶨿�ݼ�Ctrl+Z
		textComp.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK), "Undo");
		//�����¼�����
		textComp.getActionMap().put("Redo", new AbstractAction("Redo") {
			public void actionPerformed(ActionEvent e) {
				if (undo.canRedo()) {
					undo.redo();
				}
			}
		});
		//Ϊ����󶨿�ݼ�Ctrl+Y
		textComp.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK), "Redo");
	}

	@Override
	public float getAlignmentX() {
		return 0;
	}
	@Override
	public float getAlignmentY() {
		return 0;
	}
}
