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
 * 多行文本框,可以依照Windows风格在右键时弹出"复制/粘贴/选择全部"
 * @author xch
 *
 */
public class WLTTextArea extends JTextArea implements ActionListener {

	private JPopupMenu popMenu = null; //弹出菜单
	private JMenuItem menuItem_cut, menuItem_copy, menuItem_paste, menuItem_selectall; //
	private boolean isCanCopyText = true; //是否可以拷贝文本内容? 有的地方是不允许拷贝文字的,比如在线快速帮助，因为这是关键思想,怕被竞争对手快速复制！【xch/2012-02-28】

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
	 * 初始化,即监听右键事件
	 */
	private void initlize() {
		this.setToolTipText("按住Ctrl键双击可放大"); //
		this.setFont(LookAndFeel.font);
		this.setLineWrap(true); //
		addComponentUndAndRedoFunc(this); //可以undo
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) { //如果是右键
					onMouseRightClicked(e.getX(), e.getY()); //
				} else if (e.getButton() == MouseEvent.BUTTON1) {
					if (e.isControlDown() && e.getClickCount() == 2) { //最大化
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
			TextAreaDialog dialog = new TextAreaDialog(this, "满屏显示", this.getText(), 1000, 618, Color.WHITE, false, this.isCanCopyText); //
			JTextArea jta_context = dialog.getJta_context();
			jta_context.setOpaque(true);
			jta_context.setEditable(true);
			jta_context.setForeground(LookAndFeel.inputforecolor_enable); //有效时的前景色
			jta_context.setBackground(LookAndFeel.inputbgcolor_enable); //有效时的背景色
			jta_context.setText(jta_context.getText());
			dialog.setVisible(true); //
			if (dialog.getCloseType() == 1) {
				this.setText(jta_context.getText());
			}
		} else {
			if (SplashWindow.window != null) {
				SplashWindow.window.closeWindow(); //
			}
			TextAreaDialog dialog = new TextAreaDialog(this, "满屏显示", this.getText(), 1000, 618, new Color(240, 240, 240), false, this.isCanCopyText); //
			JTextArea jta_context = dialog.getJta_context();
			jta_context.setOpaque(true);
			jta_context.setEditable(false);
			jta_context.setForeground(LookAndFeel.inputforecolor_disable); //无效时的前景色
			jta_context.setBackground(LookAndFeel.inputbgcolor_disable); //无效时的背景色
			jta_context.setText(jta_context.getText());
			dialog.setVisible(true); //
		}
	}

	/**
	 * 当点击右键时触发
	 * @param _x
	 * @param _y
	 */
	private void onMouseRightClicked(int _x, int _y) {
		if (popMenu == null) {
			popMenu = new JPopupMenu(); //
			menuItem_cut = new JMenuItem("剪切"); //
			menuItem_copy = new JMenuItem("复制"); //
			menuItem_paste = new JMenuItem("粘贴"); //
			menuItem_selectall = new JMenuItem("选择全部"); //
			menuItem_cut.addActionListener(this); //
			menuItem_copy.addActionListener(this); //
			menuItem_paste.addActionListener(this); //
			menuItem_selectall.addActionListener(this); //
			popMenu.add(menuItem_cut); //
			popMenu.add(menuItem_copy); //
			popMenu.add(menuItem_paste); //
			popMenu.add(menuItem_selectall); //
		}
		menuItem_cut.setVisible(this.isEditable());//不可编辑时剪切应该设置为不可用，这样就会出现"剪切"不可用，"复制"可用，"粘贴"不可用，"选择全部"可用，感觉很乱，所以改为不可见！【李春娟/2012-02-24】
		menuItem_paste.setVisible(this.isEditable()); //

		if (!isCanCopyText) { //如果不允许拷贝,则灰掉这三个按钮!! 因为有的地方不允许拷贝的,比如快速在线帮助!
			menuItem_copy.setEnabled(false); //
			menuItem_paste.setEnabled(false); //
			menuItem_selectall.setEnabled(false); //
		}
		popMenu.show(this, _x, _y); //显示
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
	 * 剪切文件内容
	 */
	protected void onCutText() {
		StringSelection ss = new StringSelection(this.getSelectedText()); //取得选择的内容
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null); //在系统剪贴板中设置
		try {
			this.replaceSelection(""); //
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	/**
	 * 拷贝
	 */
	private void onCopy() {
		StringSelection ss = new StringSelection(this.getSelectedText()); //取得选择的内容
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null); //在系统剪贴板中设置
	}

	/**
	 * 粘贴
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
	 * 选中所有
	 */
	private void onSelectAll() {
		this.selectAll(); //
	}

	/**
	 * 后来系统增加了“在线快速帮助”,但这些信息都是关键信息,是不允许拷贝的!!!
	 * 所以需要重构默认的copy方法,即防止有人使用【Ctrl+C】将内容拷贝走! 【xch/2012-02-28】
	 */
	@Override
	public void copy() {
		if (isCanCopyText) {
			super.copy(); //
		}
	}

	/**
	 * 后来系统增加了“在线快速帮助”,但这些信息都是关键信息!!!
	 *  所以需要重构默认的copy方法,即防止有人使用【Ctrl+C】将内容拷贝走! 【xch/2012-02-28】
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

	//增加组建的重做和撤销操作
	protected void addComponentUndAndRedoFunc(JTextComponent textComp) {
		final UndoManager undo = new UndoManager();
		Document doc = textComp.getDocument();
		doc.addUndoableEditListener(new UndoableEditListener() {
			public void undoableEditHappened(UndoableEditEvent e) {
				undo.addEdit(e.getEdit());
			}
		});

		//定义事件处理
		textComp.getActionMap().put("Undo", new AbstractAction("Undo") {
			public void actionPerformed(ActionEvent e) {
				if (undo.canUndo()) {
					undo.undo();
				}
			}
		});
		//为组件绑定快捷键Ctrl+Z
		textComp.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK), "Undo");
		//定义事件处理
		textComp.getActionMap().put("Redo", new AbstractAction("Redo") {
			public void actionPerformed(ActionEvent e) {
				if (undo.canRedo()) {
					undo.redo();
				}
			}
		});
		//为组件绑定快捷键Ctrl+Y
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
