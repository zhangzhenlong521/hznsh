package cn.com.infostrategy.ui.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledEditorKit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import cn.com.infostrategy.to.common.TBUtil;

/**
 * 多风格的面板,即多行文本框上面可以支持【粗体,斜体,颜色】等效果的面板!!!类似BBS发言的效果!!!
 * @author xch
 *
 */
public class WLTStylePadPanel extends JPanel implements ActionListener, ItemListener, KeyListener {

	private JButton btn_new, btn_open, btn_save; //创建,打开,保存!
	private JComboBox combox_fonttype, combox_fontsize; //字体类型,字体大小!!
	private JButton btn_color, btn_font_b, btn_font_i, btn_font_u; //颜色,粗体,斜体,下划线
	private JButton btn_align_l, btn_align_c, btn_align_r; //居左,居中,居右!
	private JButton btn_undo, btn_redo; //

	private UndoableEditListener undoHandler = new UndoHandler(); //撤退操作的管理器
	private UndoManager undo = new UndoManager();

	private DefaultStyledDocument doc = null; //
	private JTextPane editor = null; //
	private StyledEditorKit editorKit = null; //

	public WLTStylePadPanel() {
		this.setLayout(new BorderLayout()); //

		StyleContext sc = new StyleContext();
		doc = new DefaultStyledDocument(sc);
		doc.addUndoableEditListener(undoHandler); //可以后退!!!
		editor = new JTextPane(doc); //
		editor.setAutoscrolls(true);
		editor.addKeyListener(this); //

		editorKit = (StyledEditorKit) editor.getEditorKit(); //
		editor.setDragEnabled(true);

		//富文本框换行BUG修改 杨科/2013-12-25
		JPanel panel_content = new JPanel();
		javax.swing.GroupLayout panel_contentLayout = new javax.swing.GroupLayout(panel_content);
		panel_content.setLayout(panel_contentLayout);
		panel_contentLayout.setHorizontalGroup(panel_contentLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(editor, javax.swing.GroupLayout.DEFAULT_SIZE,
						313, Short.MAX_VALUE));
		panel_contentLayout.setVerticalGroup(panel_contentLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(editor));  

		JScrollPane scrollPanel = new JScrollPane(panel_content); //
		this.add(scrollPanel, BorderLayout.CENTER); //
		this.add(getButtonPanel(), BorderLayout.NORTH); //
	}

	private JPanel getButtonPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2)); //
		btn_new = new WLTButton("创建"); //
		btn_open = new WLTButton("打开"); //
		btn_save = new WLTButton("保存"); //
		btn_new.setPreferredSize(new Dimension(40, 18)); //
		btn_open.setPreferredSize(new Dimension(40, 18)); //
		btn_save.setPreferredSize(new Dimension(40, 18)); //
		btn_new.addActionListener(this); //
		btn_open.addActionListener(this); //
		btn_save.addActionListener(this); //

		//字体类型!!
		combox_fonttype = new JComboBox(); //
		combox_fonttype.setPreferredSize(new Dimension(100, 18)); //
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String fontNames[] = ge.getAvailableFontFamilyNames();
		for (int i = 0; i < fontNames.length; i++) {
			combox_fonttype.addItem(fontNames[i]); //
		}
		combox_fonttype.setSelectedItem("宋体");
		combox_fonttype.addItemListener(this); //

		//字体大小
		combox_fontsize = new JComboBox(); //
		combox_fontsize.setPreferredSize(new Dimension(55, 18)); //
		combox_fontsize.addItem("8"); //
		combox_fontsize.addItem("10"); //
		combox_fontsize.addItem("12"); //
		combox_fontsize.addItem("14"); //
		combox_fontsize.addItem("16"); //
		combox_fontsize.addItem("18"); //
		combox_fontsize.addItem("20"); //
		combox_fontsize.addItem("24"); //
		combox_fontsize.addItem("30"); //
		combox_fontsize.addItem("36"); //
		combox_fontsize.addItem("40"); //
		combox_fontsize.setSelectedItem("12");
		combox_fontsize.addItemListener(this); //

		//颜色
		btn_color = new WLTButton(UIUtil.getImage("colorchoose.gif")); //
		btn_color.setPreferredSize(new Dimension(18, 18)); //
		btn_color.addActionListener(this); //

		//粗,斜,下划线
		btn_font_b = new WLTButton(UIUtil.getImage("font_b.gif")); //
		btn_font_i = new WLTButton(UIUtil.getImage("font_i.gif")); //
		btn_font_u = new WLTButton(UIUtil.getImage("font_u.gif")); //
		btn_font_b.setPreferredSize(new Dimension(18, 18)); //
		btn_font_i.setPreferredSize(new Dimension(18, 18)); //
		btn_font_u.setPreferredSize(new Dimension(18, 18)); //

		//居左,中,右
		btn_align_l = new WLTButton(UIUtil.getImage("align_left.gif")); //
		btn_align_c = new WLTButton(UIUtil.getImage("align_center.gif")); //
		btn_align_r = new WLTButton(UIUtil.getImage("align_right.gif")); //
		btn_align_l.setPreferredSize(new Dimension(18, 18)); //
		btn_align_c.setPreferredSize(new Dimension(18, 18)); //
		btn_align_r.setPreferredSize(new Dimension(18, 18)); //

		//undo,redo
		btn_undo = new WLTButton(UIUtil.getImage("undo.gif")); //
		btn_redo = new WLTButton(UIUtil.getImage("redo.gif")); //
		btn_undo.setPreferredSize(new Dimension(18, 18)); //
		btn_redo.setPreferredSize(new Dimension(18, 18)); //
		btn_undo.setToolTipText("取消操作(按Ctrl+Z)"); //
		btn_redo.setToolTipText("前进操作(按Ctrl+Y)"); //

		combox_fonttype.setFocusable(false); //
		combox_fontsize.setFocusable(false); //
		btn_color.setFocusable(false); //
		btn_font_b.setFocusable(false); //
		btn_font_i.setFocusable(false); //
		btn_font_u.setFocusable(false); //
		btn_align_l.setFocusable(false); //
		btn_align_c.setFocusable(false); //
		btn_align_r.setFocusable(false); //
		btn_undo.setFocusable(false); //
		btn_redo.setFocusable(false); //

		btn_font_b.addActionListener(editor.getActionMap().get("font-bold")); //粗体
		btn_font_i.addActionListener(editor.getActionMap().get("font-italic")); //斜体
		btn_font_u.addActionListener(editor.getActionMap().get("font-underline")); //下划线
		btn_align_l.addActionListener(editor.getActionMap().get("left-justify")); //居左
		btn_align_c.addActionListener(editor.getActionMap().get("center-justify")); //居左
		btn_align_r.addActionListener(editor.getActionMap().get("right-justify")); //居右
		btn_undo.addActionListener(this); //
		btn_redo.addActionListener(this); //

		//加入各个控件!!
		panel.add(btn_new); //
		panel.add(btn_open); //
		panel.add(btn_save); //

		panel.add(combox_fonttype); //
		panel.add(combox_fontsize); //
		panel.add(btn_color); //

		panel.add(btn_font_b); //
		panel.add(btn_font_i); //
		panel.add(btn_font_u); //

		panel.add(btn_align_l); //
		panel.add(btn_align_c); //
		panel.add(btn_align_r); //

		//panel.add(btn_undo); //
		//panel.add(btn_redo); //
		setFileButtonVisible(false); //
		return panel; //
	}

	/**
	 * 设置文件处理按钮是否显示!!!
	 * @param _visiable
	 */
	public void setFileButtonVisible(boolean _visible) {
		btn_new.setVisible(_visible); //
		btn_open.setVisible(_visible); //
		btn_save.setVisible(_visible); //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_new) {
			onCreateDoc(); //
		} else if (e.getSource() == btn_open) {
			onOpenDoc(); //
		} else if (e.getSource() == btn_save) {
			onSaveDoc(); //
		} else if (e.getSource() == btn_color) {
			onChangColor(); //
		} else if (e.getSource() == btn_undo) {
			onUndo(); //
		} else if (e.getSource() == btn_redo) {
			onRedo(); //
		}
	}

	public void setText(String _str) {
		editor.setText(_str); //
	}

	private void onCreateDoc() {
		doc.removeUndoableEditListener(undoHandler);
		doc = new DefaultStyledDocument(); //
		doc.addUndoableEditListener(undoHandler);
		resetUndoManager();
		editor.setDocument(doc);
		this.validate();
	}

	/**
	 * 打开文件!!!
	 */
	private void onOpenDoc() {
		try {
			JFileChooser fc = new JFileChooser(new File("C:/"));
			fc.showOpenDialog(this);
			File selFile = fc.getSelectedFile(); //
			if (selFile == null) {
				return;
			}
			TBUtil tbUtil = new TBUtil(); //
			String str_64code = tbUtil.readFromInputStreamToStr(new FileInputStream(selFile)); //读文件,这个文件必须是64位码的文件!!
			load64CodeToDocument(str_64code); //将64位码保存到数据库!!
			//JOptionPane.showMessageDialog(this, "加载文件成功!"); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			JOptionPane.showMessageDialog(this, "加载失败!"); //
		}
	}

	/**
	 * 保存文件!!
	 */
	private void onSaveDoc() {
		try {
			JFileChooser fc = new JFileChooser(new File("C:/"));
			fc.showSaveDialog(this);
			File selFile = fc.getSelectedFile();
			if (selFile == null) {
				return;
			}
			if (selFile.exists()) {
				if (JOptionPane.showConfirmDialog(this, "文件已存在,你是否想覆盖之?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
					return;
				}
			}
			TBUtil tbUtil = new TBUtil(); //
			FileOutputStream fo = new FileOutputStream(selFile); //
			tbUtil.writeStrToOutputStream(fo, getDocument64Code()); //文件转成64位码输出!!!
			JOptionPane.showMessageDialog(this, "保存文件[" + selFile.getAbsolutePath() + "]成功!"); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			JOptionPane.showMessageDialog(this, "保存失败!"); //
		}
	}

	/**
	 * 取得文本的不带格式的内容!!即业务表中的该字段存储的是不带格式的纯内容!! 只存前1000个字!
	 * 然后通过点击按钮弹出编辑新内容!!
	 * @return
	 */
	public String getDocumentText() {
		try {
			return editor.getDocument().getText(0, editor.getDocument().getLength()); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		}
	}

	/**
	 * 将文档内容转成64位码输出!!!而且是压缩过的!!! 
	 * @return
	 */
	public String getDocument64Code() {
		TBUtil tbUtil = new TBUtil(); //
		byte[] bytes = tbUtil.serialize(doc); //
		byte[] zipedBytes = tbUtil.compressBytes(bytes); //
		String str_64code = tbUtil.convertBytesTo64Code(zipedBytes); //
		return str_64code; //
	}

	/**
	 * 将一个64位码输入进文档,这可以是从一个文件中读取,也可以从数据库中取得!
	 * 实际情况是从数据库中取得的!!!
	 * @param _64code
	 */
	public void load64CodeToDocument(String _64code) {
		TBUtil tbUtil = new TBUtil(); //
		byte[] bytes = tbUtil.convert64CodeToBytes(_64code); //转成二进制码
		byte[] unzipedBytes = tbUtil.decompressBytes(bytes); //解压后的二进制!!!
		doc.removeUndoableEditListener(undoHandler);
		doc = (DefaultStyledDocument) tbUtil.deserialize(unzipedBytes); //反序列化!!!
		doc.addUndoableEditListener(undoHandler); //增加监听!!!
		editor.setDocument(doc); //
		resetUndoManager(); //重置!!
		this.validate();
	}

	/**
	 * 改变颜色!!
	 */
	private void onChangColor() {
		Color returnColor = JColorChooser.showDialog(this, "选择颜色", Color.BLACK);
		if (returnColor == null) {
			return;
		}

		MutableAttributeSet attr = new SimpleAttributeSet(); //
		StyleConstants.setForeground(attr, returnColor); //
		setCharacterAttributes(editor, attr, false); //
	}

	/**
	 * 下拉框选择变化!!!
	 */
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == combox_fonttype) {
			onChangeFontType(); //
		} else if (e.getSource() == combox_fontsize) {
			onChangeFontSize(); //
		}
	}

	/**
	 * 字体类型变化!!!
	 */
	private void onChangeFontType() {
		String str_fonttype = (String) combox_fonttype.getSelectedItem(); //
		if (str_fonttype == null || str_fonttype.trim().equals("")) {
			return;
		}
		MutableAttributeSet attr = new SimpleAttributeSet();
		StyleConstants.setFontFamily(attr, str_fonttype); //
		setCharacterAttributes(editor, attr, false); //
	}

	/**
	 * 字体大小变化!!!
	 */
	private void onChangeFontSize() {
		String str_fontsize = (String) combox_fontsize.getSelectedItem(); //
		if (str_fontsize == null || str_fontsize.trim().equals("")) {
			return;
		}
		MutableAttributeSet attr = new SimpleAttributeSet();
		StyleConstants.setFontSize(attr, Integer.parseInt(str_fontsize)); //
		setCharacterAttributes(editor, attr, false); //
	}

	private void setCharacterAttributes(JEditorPane editor, AttributeSet attr, boolean replace) {
		int p0 = editor.getSelectionStart();
		int p1 = editor.getSelectionEnd();
		if (p0 != p1) {
			doc.setCharacterAttributes(p0, p1 - p0, attr, replace);
		}
		MutableAttributeSet inputAttributes = editorKit.getInputAttributes();
		if (replace) {
			inputAttributes.removeAttributes(inputAttributes);
		}
		inputAttributes.addAttributes(attr);
	}

	/**
	 * 后退
	 */
	private void onUndo() {
		try {
			undo.undo(); //
		} catch (CannotUndoException ex) {
			//System.out.println("Unable to undo: " + ex);
			return;
		}
	}

	/**
	 * 前进!!
	 */
	private void onRedo() {
		try {
			undo.redo(); //
		} catch (CannotRedoException ex) {
			//System.out.println("Unable to redo: " + ex);
			return;
		}
	}
	
	public void setEditble(boolean bo) {
		btn_new.setEnabled(bo);
		btn_open.setEnabled(bo);
		btn_save.setEnabled(bo);
		combox_fonttype.setEnabled(bo);
		combox_fontsize.setEnabled(bo);
		btn_color.setEnabled(bo);
		btn_font_b.setEnabled(bo);
		btn_font_i.setEnabled(bo);
		btn_font_u.setEnabled(bo);
		btn_align_l.setEnabled(bo);
		btn_align_c.setEnabled(bo);
		btn_align_r.setEnabled(bo);
		editor.setEditable(bo);
	}

	private void resetUndoManager() {
		undo.discardAllEdits(); //
	}

	public void keyPressed(KeyEvent e) {
		if (e.isControlDown()) {
			if (e.getKeyCode() == KeyEvent.VK_Z) {
				onUndo(); //
			} else if (e.getKeyCode() == KeyEvent.VK_Y) {
				onRedo(); //
			}
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {

	}

	class UndoHandler implements UndoableEditListener {
		public void undoableEditHappened(UndoableEditEvent e) {
			undo.addEdit(e.getEdit());
		}
	}

}
