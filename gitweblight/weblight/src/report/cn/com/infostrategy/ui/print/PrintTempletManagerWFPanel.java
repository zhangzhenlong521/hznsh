package cn.com.infostrategy.ui.print;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.print.PubPrintItemBandVO;
import cn.com.infostrategy.to.print.PubPrintTempletVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.RemoteServiceFactory;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.report.ReportServiceIfc;

public class PrintTempletManagerWFPanel extends AbstractWorkPanel implements ActionListener {

	private JButton btn_add, btn_save, btn_open, btn_import, btn_view;

	private JButton btn_leftalign, btn_topalign, btn_widthalign, btn_heightalign;
	private JButton btn_left, btn_center, btn_right, btn_top, btn_middle, btn_bottom; //
	private JButton btn_foreground, btn_background; //前后景颜色
	private JCheckBox checkBoxIsshowBorder = null;

	private String templetcode = null; //

	private JLayeredPane mainPanel = null;

	private EditerTextField textfield = null; //

	private VectorMap vm_items = new VectorMap(); //

	/**
	 * 初始化页面....
	 */
	public void initialize() {
		this.setLayout(new BorderLayout()); //

		mainPanel = new MainPanel(); //
		mainPanel.setLayout(null); //绝对位置布局..
		mainPanel.setBackground(new Color(255, 255, 250)); //
		mainPanel.setPreferredSize(new Dimension(1000, 1000)); //  

		JScrollPane scrollPanel = new JScrollPane(mainPanel); //
		KeyListener[] allkeyListener = scrollPanel.getViewport().getKeyListeners(); //
		for (int i = 0; i < allkeyListener.length; i++) {
			scrollPanel.getViewport().removeKeyListener(allkeyListener[i]);
		}
		scrollPanel.setFocusable(false); //
		//		JSplitPane splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPanel, new JLabel("config"));
		//		splitPanel.setDividerLocation(800); 

		this.add(scrollPanel, BorderLayout.CENTER); //
		this.add(getNorthPanel(), BorderLayout.NORTH); //
	}

	/**
	 * 加入某个模板的控件控件
	 */
	private void inputItem() {
		clearMainPanel();
		if (textfield == null) {
			textfield = new EditerTextField(); //
		}
		textfield.setVisible(false); //
		mainPanel.add(textfield, JLayeredPane.MODAL_LAYER); //

		try {
			ReportServiceIfc printService = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class);
			PubPrintTempletVO templetVO = printService.getPubPrintTempletVO(templetcode); ////
			PubPrintItemBandVO[] itemVOs = templetVO.getItemBandVOs(); //

			if (itemVOs != null && itemVOs.length > 0) {
				for (int i = 0; i < itemVOs.length; i++) {
					ItemLabel label = new ItemLabel(itemVOs[i]); //
					vm_items.put(itemVOs[i].getItemkey(), label); ////..
					mainPanel.add(label); ////层次..
					mainPanel.setLayer(label, label.getMyLayer()); //
				}
			}
			mainPanel.invalidate();
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	/**
	 * 按钮面板..
	 * @return
	 */
	private JPanel getNorthPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JPanel panel_1 = new JPanel(); //
		panel_1.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 1));
		btn_leftalign = new WLTButton(UIUtil.getImage("workflow/align_left.gif")); //
		btn_topalign = new WLTButton(UIUtil.getImage("workflow/valign_top.gif")); //
		btn_widthalign = new WLTButton(UIUtil.getImage("workflow/same_width.gif")); //
		btn_heightalign = new WLTButton(UIUtil.getImage("workflow/same_height.gif")); //
		btn_left = new WLTButton(UIUtil.getImage("align_left.gif")); //
		btn_center = new WLTButton(UIUtil.getImage("align_center.gif")); //
		btn_right = new WLTButton(UIUtil.getImage("align_right.gif")); //
		btn_top = new WLTButton(UIUtil.getImage("valign_top.gif")); //
		btn_middle = new WLTButton(UIUtil.getImage("valign_middle.gif")); //
		btn_bottom = new WLTButton(UIUtil.getImage("valign_bottom.gif")); //
		btn_foreground = new WLTButton(UIUtil.getImage("foreground.gif")); //
		btn_background = new WLTButton(UIUtil.getImage("background.gif")); //
		checkBoxIsshowBorder = new JCheckBox("显示边框"); //

		btn_leftalign.addActionListener(this); //
		btn_topalign.addActionListener(this); //
		btn_widthalign.addActionListener(this); //
		btn_heightalign.addActionListener(this); //
		btn_left.addActionListener(this); //
		btn_center.addActionListener(this); //
		btn_right.addActionListener(this); //
		btn_top.addActionListener(this); //
		btn_middle.addActionListener(this); //
		btn_bottom.addActionListener(this); //
		btn_foreground.addActionListener(this); //
		btn_background.addActionListener(this); //
		checkBoxIsshowBorder.addActionListener(this); //

		btn_leftalign.setToolTipText("左对齐");
		btn_topalign.setToolTipText("顶端对齐");
		btn_widthalign.setToolTipText("同宽");
		btn_heightalign.setToolTipText("同高");

		btn_left.setToolTipText("文字水平居左");
		btn_center.setToolTipText("文字水平居中");
		btn_right.setToolTipText("文字水平居右");
		btn_top.setToolTipText("文字垂直居上");
		btn_middle.setToolTipText("文字垂直居中");
		btn_bottom.setToolTipText("文字垂直居下");
		btn_foreground.setToolTipText("设置字体颜色");
		btn_background.setToolTipText("设置背景颜色");
		checkBoxIsshowBorder.setToolTipText("请选中标签进行设置,保存后预览查看效果");

		panel_1.add(btn_leftalign); //
		panel_1.add(btn_topalign); //
		panel_1.add(btn_widthalign); //
		panel_1.add(btn_heightalign); //

		panel_1.add(btn_left); //
		panel_1.add(btn_center); //
		panel_1.add(btn_right); //

		panel_1.add(btn_top); //
		panel_1.add(btn_middle); //
		panel_1.add(btn_bottom); //

		panel_1.add(btn_foreground); //
		panel_1.add(btn_background); //

		panel_1.add(checkBoxIsshowBorder); //

		JPanel panel_2 = new JPanel();
		panel_2.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 2));

		btn_add = new WLTButton("新建"); //
		btn_save = new WLTButton("保存"); //
		btn_open = new WLTButton("打开"); //
		btn_import = new WLTButton("导入"); //
		btn_view = new WLTButton("预览"); //

		btn_add.addActionListener(this);
		btn_save.addActionListener(this);
		btn_open.addActionListener(this);
		btn_import.addActionListener(this);
		btn_view.addActionListener(this); //

		btn_view.setToolTipText("保存后预览查看效果");
		panel_2.add(btn_add);
		panel_2.add(btn_open);
		panel_2.add(btn_save);
		panel_2.add(btn_import);
		panel_2.add(btn_view);

		panel.add(panel_2, BorderLayout.NORTH);
		panel.add(panel_1, BorderLayout.CENTER);
		return panel; //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_save) { //
			onSave();
		} else if (e.getSource() == btn_leftalign) {
			onToSameLeftAlign(); //
		} else if (e.getSource() == btn_topalign) {
			onToSameTopAlign(); //
		} else if (e.getSource() == btn_widthalign) {
			onWidthAlign(); //
		} else if (e.getSource() == btn_heightalign) {
			onHeightAlign(); //
		} else if (e.getSource() == btn_left) {
			onAlignLeft(); //
		} else if (e.getSource() == btn_center) {
			onAlignCenter(); //
		} else if (e.getSource() == btn_right) {
			onAlignRight(); //
		} else if (e.getSource() == btn_top) {
			onValignTop(); //
		} else if (e.getSource() == btn_middle) {
			onValignMiddle(); //
		} else if (e.getSource() == btn_bottom) {
			onValignBottom(); //
		} else if (e.getSource() == btn_foreground) {
			onSetForeGround(); //
		} else if (e.getSource() == btn_background) {
			onSetBackGround(); //
		} else if (e.getSource() == checkBoxIsshowBorder) {
			onSetBorder(); //
		} else if (e.getSource() == btn_view) {
			onView(); //
		} else if (e.getSource() == btn_add) {
			onAdd();
		} else if (e.getSource() == btn_open) {
			onOpen();
		} else if (e.getSource() == btn_import) {
			onImport();
		}
	}

	private void onOpen() {
		OpenPrintTempletDialog openDialog = new OpenPrintTempletDialog(this);
		openDialog.setVisible(true);
		if (openDialog.getCloseType() == 1) {//如果点击确定返回
			this.templetcode = openDialog.getPrinttempletcode();
			inputItem();
		}

	}

	private void onAdd() {
		BillCardPanel cardPanel = new BillCardPanel("pub_printtemplet_CODE1"); //创建一个卡片面板
		cardPanel.insertRow(); //卡片新增一行!
		cardPanel.setEditableByInsertInit(); //设置卡片编辑状态为新增时的设置

		BillCardDialog dialog = new BillCardDialog(this, "", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); //弹出卡片新增框
		dialog.setVisible(true); //显示卡片窗口
		if (dialog.getCloseType() == 1) { //如是是点击确定返回!将则卡片中的数据赋给列表!
			this.templetcode = dialog.getBillVO().getStringValue("templetcode");
			inputItem();
		}
	}

	public ItemLabel[] getAllSelectedLabels() {
		Vector v_labels = new Vector(); //
		for (int i = 0; i < vm_items.size(); i++) {
			ItemLabel label_item = (ItemLabel) vm_items.get(i);
			if (label_item.isSelected()) {
				v_labels.add(label_item); //
			}
		}
		return (ItemLabel[]) v_labels.toArray(new ItemLabel[0]);
	}

	/**
	 * 保存
	 */
	private void onSave() {
		try {
			if (templetcode == null) {
				MessageBox.show(this, "请先打开一个打印模板!");
				return;
			}
			String[] keys = vm_items.getKeysAsString(); //
			TBUtil tbUtil = new TBUtil(); //

			PubPrintItemBandVO[] itemVOS = new PubPrintItemBandVO[keys.length]; //
			for (int i = 0; i < keys.length; i++) {
				itemVOS[i] = new PubPrintItemBandVO(); //
				ItemLabel label = (ItemLabel) vm_items.get(keys[i]); //

				itemVOS[i].setItemkey(label.getName()); //
				itemVOS[i].setItemname(label.getText()); //
				itemVOS[i].setX(label.getBounds().getX());
				itemVOS[i].setY(label.getBounds().getY());
				itemVOS[i].setWidth((int) label.getBounds().getWidth());
				itemVOS[i].setHeight((int) label.getBounds().getHeight());

				Font font = label.getFont();
				itemVOS[i].setFonttype(font.getName());
				itemVOS[i].setFontsize(font.getSize());
				itemVOS[i].setFontstyle(font.getStyle());

				itemVOS[i].setHalign(label.getHorizontalAlignment()); //
				itemVOS[i].setValign(label.getVerticalAlignment()); //
				itemVOS[i].setLayer(label.getMyLayer()); //

				itemVOS[i].setForeground(tbUtil.convertColor(label.getForeground())); //
				itemVOS[i].setBackground(tbUtil.convertColor(label.getBackground())); //

				itemVOS[i].setShowBorder(label.isShowborder()); ////..
				itemVOS[i].setShowBaseline(false); ////..
			}

			ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class);
			service.savePrintTempletItemBands(this.templetcode, itemVOS); //
			MessageBox.show(this, "保存成功!");
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	private void onDelete() {
		Vector v_keys = new Vector(); //

		for (int i = 0; i < vm_items.size(); i++) {
			ItemLabel label = (ItemLabel) vm_items.get(i);
			if (label.isSelected()) {
				v_keys.add(label.getName()); //
				mainPanel.remove(label); //
			}
		}

		for (int i = 0; i < v_keys.size(); i++) {
			vm_items.remove((String) v_keys.get(i)); //
		}

		mainPanel.updateUI(); //
		mainPanel.getRootPane().updateUI();
	}

	/**
	 * 清空屏幕上所有对象,以备新的操作!!
	 */
	public void clearMainPanel() {
		mainPanel.removeAll();
		mainPanel.updateUI(); //
		mainPanel.getRootPane().updateUI();
		mainPanel.validate();
	}

	private void onToSameLeftAlign() {
		int li_minleftpos = 0; //
		for (int i = 0; i < vm_items.size(); i++) {
			ItemLabel label = (ItemLabel) vm_items.get(i);
			if (label.isSelected()) {
				int li_left = (int) label.getBounds().getX(); //
				if (li_minleftpos == 0) {
					li_minleftpos = li_left;
				} else {
					if (li_left < li_minleftpos) {
						li_minleftpos = li_left;
					}
				}
			}
		}

		for (int i = 0; i < vm_items.size(); i++) {
			ItemLabel label = (ItemLabel) vm_items.get(i);
			if (label.isSelected()) {
				int li_y = (int) label.getBounds().getY();
				int li_width = (int) label.getBounds().getWidth();
				int li_height = (int) label.getBounds().getHeight();
				label.setBounds(li_minleftpos, li_y, li_width, li_height); //
				label.updateUI(); //
			}
		}
	}

	private void onToSameTopAlign() {
		int li_minpos = 0; //
		for (int i = 0; i < vm_items.size(); i++) {
			ItemLabel label = (ItemLabel) vm_items.get(i);
			if (label.isSelected()) {
				int li_pos = (int) label.getBounds().getY(); //
				if (li_minpos == 0) {
					li_minpos = li_pos;
				} else {
					if (li_pos < li_minpos) {
						li_minpos = li_pos;
					}
				}
			}
		}

		for (int i = 0; i < vm_items.size(); i++) {
			ItemLabel label = (ItemLabel) vm_items.get(i);
			if (label.isSelected()) {
				int li_x = (int) label.getBounds().getX();
				int li_width = (int) label.getBounds().getWidth();
				int li_height = (int) label.getBounds().getHeight();
				label.setBounds(li_x, li_minpos, li_width, li_height); //
				label.updateUI(); //
			}
		}
	}

	private void onWidthAlign() {
		int li_minpos = 0; //
		for (int i = 0; i < vm_items.size(); i++) {
			ItemLabel label = (ItemLabel) vm_items.get(i);
			if (label.isSelected()) {
				int li_pos = (int) label.getBounds().getWidth(); //
				if (li_minpos == 0) {
					li_minpos = li_pos;
				} else {
					if (li_pos > li_minpos) {
						li_minpos = li_pos;
					}
				}
			}
		}

		for (int i = 0; i < vm_items.size(); i++) {
			ItemLabel label = (ItemLabel) vm_items.get(i);
			if (label.isSelected()) {
				int li_x = (int) label.getBounds().getX();
				int li_y = (int) label.getBounds().getY();
				//int li_width = (int) label.getBounds().getWidth();
				int li_height = (int) label.getBounds().getHeight();
				label.setBounds(li_x, li_y, li_minpos, li_height); //
				label.updateUI(); //
			}
		}
	}

	private void onHeightAlign() {
		int li_minpos = 0; //
		for (int i = 0; i < vm_items.size(); i++) {
			ItemLabel label = (ItemLabel) vm_items.get(i);
			if (label.isSelected()) {
				int li_pos = (int) label.getBounds().getHeight(); //
				if (li_minpos == 0) {
					li_minpos = li_pos;
				} else {
					if (li_pos > li_minpos) {
						li_minpos = li_pos;
					}
				}
			}
		}

		for (int i = 0; i < vm_items.size(); i++) {
			ItemLabel label = (ItemLabel) vm_items.get(i);
			if (label.isSelected()) {
				int li_x = (int) label.getBounds().getX();
				int li_y = (int) label.getBounds().getY();
				int li_width = (int) label.getBounds().getWidth();
				//int li_height = (int) label.getBounds().getHeight();
				label.setBounds(li_x, li_y, li_width, li_minpos); //
				label.updateUI(); //
			}
		}
	}

	private void onMoveUp() {
		for (int i = 0; i < vm_items.size(); i++) {
			ItemLabel label = (ItemLabel) vm_items.get(i);
			if (label.isSelected()) {
				int li_x = (int) label.getBounds().getX();
				int li_y = (int) label.getBounds().getY();
				int li_width = (int) label.getBounds().getWidth();
				int li_height = (int) label.getBounds().getHeight();

				li_y = li_y - 2;
				label.setBounds(li_x, li_y, li_width, li_height); //
				label.updateUI(); //
			}
		}
	}

	private void onMoveDown() {
		for (int i = 0; i < vm_items.size(); i++) {
			ItemLabel label = (ItemLabel) vm_items.get(i);
			if (label.isSelected()) {
				int li_x = (int) label.getBounds().getX();
				int li_y = (int) label.getBounds().getY();
				int li_width = (int) label.getBounds().getWidth();
				int li_height = (int) label.getBounds().getHeight();

				li_y = li_y + 2;
				label.setBounds(li_x, li_y, li_width, li_height); //
				label.updateUI(); //
			}
		}
	}

	private void onMoveLeft() {
		for (int i = 0; i < vm_items.size(); i++) {
			ItemLabel label = (ItemLabel) vm_items.get(i);
			if (label.isSelected()) {
				int li_x = (int) label.getBounds().getX();
				int li_y = (int) label.getBounds().getY();
				int li_width = (int) label.getBounds().getWidth();
				int li_height = (int) label.getBounds().getHeight();

				li_x = li_x - 2;
				label.setBounds(li_x, li_y, li_width, li_height); //
				label.updateUI(); //
			}
		}
	}

	private void onMoveRight() {
		for (int i = 0; i < vm_items.size(); i++) {
			ItemLabel label = (ItemLabel) vm_items.get(i);
			if (label.isSelected()) {
				int li_x = (int) label.getBounds().getX();
				int li_y = (int) label.getBounds().getY();
				int li_width = (int) label.getBounds().getWidth();
				int li_height = (int) label.getBounds().getHeight();

				li_x = li_x + 2;
				label.setBounds(li_x, li_y, li_width, li_height); //
				label.updateUI(); //
			}
		}
	}

	private void onSize() {
		JLabel label_1 = new JLabel("Width", JLabel.RIGHT);
		JLabel label_2 = new JLabel("Height", JLabel.RIGHT);
		JTextField textField_1 = new JTextField(); //
		JTextField textField_2 = new JTextField(); //

		label_1.setBounds(5, 5, 75, 20); //
		label_2.setBounds(5, 30, 75, 20); //
		textField_1.setBounds(85, 5, 120, 20); //
		textField_2.setBounds(85, 30, 120, 20); //

		JPanel panel = new JPanel(); //
		panel.setLayout(null); //
		panel.add(label_1);
		panel.add(label_2);
		panel.add(textField_1);
		panel.add(textField_2);

		panel.setPreferredSize(new Dimension(220, 60)); //

		if (JOptionPane.showConfirmDialog(this, panel, "您确定要重置大小吗?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			if (textField_1.getText().equals("") && textField_2.getText().equals("")) {

			} else {
				int li_width = Integer.parseInt(textField_1.getText()); //
				int li_height = Integer.parseInt(textField_2.getText()); //

				for (int i = 0; i < vm_items.size(); i++) {
					ItemLabel label = (ItemLabel) vm_items.get(i);
					if (label.isSelected()) {
						int li_x = (int) label.getBounds().getX();
						int li_y = (int) label.getBounds().getY();
						label.setBounds(li_x, li_y, li_width, li_height); //
						label.updateUI(); //
					}
				}
			}
		}
	}

	private void onAlignLeft() {
		ItemLabel[] labels = getAllSelectedLabels();
		for (int i = 0; i < labels.length; i++) {
			labels[i].setHorizontalAlignment(SwingConstants.LEFT); //
		}
	}

	private void onAlignCenter() {
		ItemLabel[] labels = getAllSelectedLabels();
		for (int i = 0; i < labels.length; i++) {
			labels[i].setHorizontalAlignment(SwingConstants.CENTER); //
		}
	}

	private void onAlignRight() {
		ItemLabel[] labels = getAllSelectedLabels();
		for (int i = 0; i < labels.length; i++) {
			labels[i].setHorizontalAlignment(SwingConstants.RIGHT); //
		}
	}

	private void onValignTop() {
		ItemLabel[] labels = getAllSelectedLabels();
		for (int i = 0; i < labels.length; i++) {
			labels[i].setVerticalAlignment(SwingConstants.TOP); //
		}
	}

	private void onValignMiddle() {
		ItemLabel[] labels = getAllSelectedLabels();
		for (int i = 0; i < labels.length; i++) {
			labels[i].setVerticalAlignment(SwingConstants.CENTER); //
		}
	}

	private void onValignBottom() {
		ItemLabel[] labels = getAllSelectedLabels();
		for (int i = 0; i < labels.length; i++) {
			labels[i].setVerticalAlignment(SwingConstants.BOTTOM); //
		}
	}

	/**
	 * 设置前景颜色
	 */
	private void onSetForeGround() {
		Color returnColor = JColorChooser.showDialog(this, "选择前景颜色", Color.BLACK); //
		ItemLabel[] labels = getAllSelectedLabels();
		for (int i = 0; i < labels.length; i++) {
			labels[i].setForeground(returnColor); //
		}
	}

	/**
	 * 设置前景颜色
	 */
	private void onSetBackGround() {
		Color returnColor = JColorChooser.showDialog(this, "选择背景颜色", Color.BLACK); //
		ItemLabel[] labels = getAllSelectedLabels();
		for (int i = 0; i < labels.length; i++) {
			labels[i].setBackground(returnColor); //
		}
	}

	/**
	 * 设置边框..
	 */
	private void onSetBorder() {
		ItemLabel[] labels = getAllSelectedLabels();
		if (checkBoxIsshowBorder.isSelected()) { //如果是勾上的话
			for (int i = 0; i < labels.length; i++) {
				labels[i].setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); //设置边框
				labels[i].setShowborder(true); //
			}
		} else {
			for (int i = 0; i < labels.length; i++) {
				labels[i].setBorder(BorderFactory.createLineBorder(Color.RED, 1)); //设置边框
				labels[i].setShowborder(false); //
			}
		}
	}

	private void onView() {
		if (this.templetcode == null) {
			MessageBox.show(this, "请先打开一个打印模板!");
			return;
		}
		HashMap map = new HashMap(); //
		map.put("loginusercode", ClientEnvironment.getInstance().getLoginUserCode()); //
		new BillPrintJob(null, this.templetcode, null, map); //
	}

	private void onImport() {
		try {
			if (this.templetcode == null) {
				MessageBox.show(this, "请选择一个打印模板!");
				return;
			}
			if (!MessageBox.confirm(this, "该操作将覆盖原有的打印模板,是否继续?")) {
				return;
			}
			String str_code = JOptionPane.showInputDialog("请输入单据模板编码:");
			if (str_code == null || "".equals(str_code.trim())) {
				return;
			}

			ReportServiceIfc printService = (ReportServiceIfc) RemoteServiceFactory.getInstance().lookUpService(ReportServiceIfc.class);
			printService.importPrintTemplet(str_code, templetcode); //
			MessageBox.show(this, "导入成功!");
			this.inputItem();
		} catch (Exception ex) {
			ex.printStackTrace(); //
			MessageBox.showException(this, ex); //
		}
	}

	public String getTempletcode() {
		return templetcode;
	}

	public void updateRootPanel() {
		this.getRootPane().updateUI();
	}

	class MainPanel extends JLayeredPane implements DropTargetListener, MouseListener {
		private static final long serialVersionUID = -4564713376322464611L;

		public MainPanel() {
			this.setEnabled(true);
			this.setFocusable(true); //
			new DropTarget(this, this);
			this.addMouseListener(this); //
		}

		public void dragEnter(DropTargetDragEvent evt) {
		}

		public void dragOver(DropTargetDragEvent evt) {
		}

		public void dragExit(DropTargetEvent evt) {
		}

		public void dropActionChanged(DropTargetDragEvent evt) {
		}

		public void drop(DropTargetDropEvent evt) {
		}

		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == e.BUTTON3) //如果是左键
			{
				showPopMenu_mainPanel(e.getPoint()); //  //
			}
			if (textfield != null) {
				textfield.setVisible(false); //
			}
			for (int i = 0; i < vm_items.size(); i++) {
				ItemLabel label_item = (ItemLabel) vm_items.get(i);
				if (label_item.isShowborder()) {
					label_item.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); //边框
				} else {
					label_item.setBorder(BorderFactory.createLineBorder(new Color(240, 240, 240), 1)); //边框	
				}
				label_item.setSelected(false);
			}
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		private void showPopMenu_mainPanel(final Point _point) {
			JPopupMenu popMenu = new JPopupMenu(); //
			JMenuItem menuItem_additem = new JMenuItem("Add Item"); //

			menuItem_additem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onAddItem(_point); //
				}
			});

			popMenu.add(menuItem_additem); //
			popMenu.show(mainPanel, (int) _point.getX(), (int) _point.getY()); //
		}

		private void onAddItem(Point _point) {
			JLabel label_1 = new JLabel("ItemKey");//
			JLabel label_2 = new JLabel("ItemName");//
			JTextField textField_1 = new JTextField("temp_" + System.currentTimeMillis());//
			JTextField textField_2 = new JTextField();//

			label_1.setBounds(5, 5, 80, 20);
			label_2.setBounds(5, 30, 80, 20);
			textField_1.setBounds(85, 5, 200, 20);
			textField_2.setBounds(85, 30, 200, 20);

			JPanel panel = new JPanel(); //
			panel.setLayout(null); //
			panel.setPreferredSize(new Dimension(300, 55)); //

			panel.add(label_1); //
			panel.add(label_2); //
			panel.add(textField_1); //
			panel.add(textField_2); //

			if (JOptionPane.showConfirmDialog(this, panel, "Add Item", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				if (textField_1.getText() == null || textField_1.getText().trim().equals("")) {
					MessageBox.show(this, "Item key is null."); //
					return;
				}

				if (textField_2.getText() == null || textField_2.getText().trim().equals("")) {
					MessageBox.show(this, "Item Name is null."); //
					return;
				}

				String str_itemkey = textField_1.getText();
				String str_itemname = textField_2.getText();
				if (vm_items.containsKey(str_itemkey)) {
					MessageBox.show(this, "the key [" + str_itemkey + "] is exists!"); //
					return; //
				}

				PubPrintItemBandVO itemVO = new PubPrintItemBandVO(); //
				itemVO.setItemkey(str_itemkey); //
				itemVO.setItemname(str_itemname); //
				itemVO.setX(_point.getX()); //
				itemVO.setY(_point.getY()); //
				itemVO.setHalign(SwingConstants.LEFT); //
				itemVO.setValign(SwingConstants.CENTER);
				itemVO.setWidth(125);
				itemVO.setHeight(20); //
				itemVO.setFonttype("Serif"); //
				itemVO.setFontstyle(0); //
				itemVO.setFontsize(12); //
				itemVO.setForeground("000000"); //
				itemVO.setBackground("FFFFFF"); //
				itemVO.setShowBorder(false);
				itemVO.setShowBaseline(false); //
				itemVO.setLayer(JLayeredPane.PALETTE_LAYER.intValue()); //层次

				ItemLabel label = new ItemLabel(itemVO); //
				vm_items.put(str_itemkey, label); //
				mainPanel.add(label, JLayeredPane.PALETTE_LAYER); //
				mainPanel.updateUI(); //
			}
		}
	}

	class ItemLabel extends JLabel implements MouseListener, DragGestureListener, DragSourceListener, KeyListener {
		private static final long serialVersionUID = 6333717693529088676L;

		private DragSource dragSource; //

		private int clickedX = 100;
		private int clickedY = 10;

		private boolean selected = false; //

		private boolean showborder = false; //显示边框
		private boolean showbaseline = false; //显示底线

		private int mylayer = JLayeredPane.PALETTE_LAYER.intValue(); //默认都在上一层,总共就两层

		public ItemLabel(PubPrintItemBandVO _pubPrintItemBandVO) {
			super(); //
			this.setName(_pubPrintItemBandVO.getItemkey()); //名称
			this.setToolTipText(_pubPrintItemBandVO.getItemkey()); //
			this.setText(_pubPrintItemBandVO.getItemname()); //

			//设置位置
			this.setBounds((int) _pubPrintItemBandVO.getX(), (int) _pubPrintItemBandVO.getY(), (int) _pubPrintItemBandVO.getWidth(), (int) _pubPrintItemBandVO.getHeight()); //位置与大小

			//设置字体
			this.setFont(new Font(_pubPrintItemBandVO.getFonttype(), _pubPrintItemBandVO.getFontstyle(), _pubPrintItemBandVO.getFontsize())); //

			//设置左右上下排列位置
			this.setHorizontalAlignment(_pubPrintItemBandVO.getHalign()); //左右位置
			this.setVerticalAlignment(_pubPrintItemBandVO.getValign()); //上下位置

			//设置边框
			if (_pubPrintItemBandVO.isShowBorder()) { //如果显示边框
				this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); //边框
			} else {
				this.setBorder(BorderFactory.createLineBorder(new Color(240, 240, 240), 1)); //边框
			}

			this.setOpaque(true); //

			TBUtil tbutil = new TBUtil(); //

			this.setForeground(tbutil.getColor(_pubPrintItemBandVO.getForeground())); //前景颜色..
			this.setBackground(tbutil.getColor(_pubPrintItemBandVO.getBackground())); //背景颜色..

			this.setMyLayer(_pubPrintItemBandVO.getLayer()); //设置层次,即是上一层还是下一层

			this.setFocusable(true); //
			this.setFocusTraversalKeysEnabled(false); //
			this.addKeyListener(this); //

			dragSource = new DragSource();
			dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_LINK, this);

			this.addMouseListener(this); //

			this.setShowborder(_pubPrintItemBandVO.isShowBorder()); //
			this.setShowbaseline(_pubPrintItemBandVO.isShowBaseline()); //
		}

		public int getClickedX() {
			return clickedX;
		}

		public void setClickedX(int clickedX) {
			this.clickedX = clickedX;
		}

		public int getClickedY() {
			return clickedY;
		}

		public void setClickedY(int clickedY) {
			this.clickedY = clickedY;
		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}

		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				ItemLabel itemLabel = (ItemLabel) e.getSource();
				textfield.setBindLabel(itemLabel); //
				textfield.setBounds(itemLabel.getBounds()); //
				textfield.setVisible(true); //
				textfield.setText(itemLabel.getText()); //
				textfield.setEditable(true); //
				textfield.requestFocus(); //
				textfield.requestFocusInWindow(); //

			} else {
				if (e.getButton() == e.BUTTON3) //如果是左键
				{
					showPopMenu((ItemLabel) e.getSource(), e.getX(), e.getY()); //  //
				}
			}
		}

		public void mouseEntered(MouseEvent e) {
			ItemLabel itemLabel = (ItemLabel) e.getSource();
			int li_width = (int) itemLabel.getBounds().getWidth(); //
			int li_height = (int) itemLabel.getBounds().getHeight(); //
			if (itemLabel.isSelected()) {
				if (e.getX() > li_width - 10 && e.getY() > li_height - 10) {
					itemLabel.setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
				} else {
					itemLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			} else {
				itemLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			if (e.getButton() == e.BUTTON1) {
				ItemLabel label = (ItemLabel) e.getSource();
				Point point = e.getPoint(); ////
				label.setClickedX((int) point.getX()); //
				label.setClickedY((int) point.getY()); //
				textfield.setVisible(false); //
			}
		}

		public void mouseReleased(MouseEvent e) {
			ItemLabel label = (ItemLabel) e.getSource();
			if (e.getButton() == e.BUTTON1) {
				if (e.isControlDown()) {
					if (!label.isSelected()) {
						label.setBorder(BorderFactory.createLineBorder(Color.RED, 1)); //边框
						label.setSelected(true); //
						label.requestFocusInWindow(); //
					} else {
						if (label.isShowborder()) {
							label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); //边框
						} else {
							label.setBorder(BorderFactory.createLineBorder(new Color(240, 240, 240), 1)); //边框
						}
						label.setSelected(false); //
					}
				} else {
					for (int i = 0; i < vm_items.size(); i++) {
						ItemLabel label_item = (ItemLabel) vm_items.get(i);
						if (label_item.isShowborder()) {
							label_item.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); //边框
						} else {
							label_item.setBorder(BorderFactory.createLineBorder(new Color(240, 240, 240), 1)); //边框
						}
						label_item.setSelected(false);
					}

					label.setBorder(BorderFactory.createLineBorder(Color.RED, 1)); //边框
					label.setSelected(true); //
					label.requestFocusInWindow(); //
				}
			}
			label.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}

		public void dragGestureRecognized(DragGestureEvent evt) {
			Transferable t = new StringSelection(null);
			dragSource.startDrag(evt, DragSource.DefaultMoveDrop, t, this);
		}

		public void dragEnter(DragSourceDragEvent evt) {
		}

		public void dragOver(DragSourceDragEvent evt) {
			ItemLabel label = (ItemLabel) evt.getDragSourceContext().getComponent();
			if (!label.isSelected()) {
				return;
			}

			if (label.getCursor().getType() == Cursor.SE_RESIZE_CURSOR) {
				Point point = evt.getLocation(); //屏幕位置
				SwingUtilities.convertPointFromScreen(point, mainPanel); //转换成在面板中位置

				int li_x = (int) (point.getX()); //
				int li_y = (int) (point.getY()); //

				int li_x_old = (int) label.getBounds().getX();
				int li_y_old = (int) label.getBounds().getY();

				int li_new_width = li_x - li_x_old;
				int li_new_height = li_y - li_y_old;

				if (li_new_width < 20) {
					li_new_width = 20;
				}

				if (li_new_height < 1) {
					li_new_height = 1;
				}

				label.setBounds(li_x_old, li_y_old, li_new_width, li_new_height); //
			} else {
				int li_oldX = (int) label.getBounds().getX();
				int li_oldY = (int) label.getBounds().getY();

				Point point = evt.getLocation(); //屏幕位置
				SwingUtilities.convertPointFromScreen(point, mainPanel); //

				int li_x = (int) (point.getX() - label.getClickedX()); //
				int li_y = (int) (point.getY() - label.getClickedY()); //

				int li_grap_x = li_x - li_oldX; //
				int li_grap_y = li_y - li_oldY; //

				ItemLabel[] selectedLabels = getAllSelectedLabels(); //
				for (int i = 0; i < selectedLabels.length; i++) {
					int li_itemoldX = (int) selectedLabels[i].getBounds().getX();
					int li_itemoldY = (int) selectedLabels[i].getBounds().getY();
					int li_itemnewX = li_itemoldX + li_grap_x; ////
					int li_itemnewY = li_itemoldY + li_grap_y; ////

					selectedLabels[i].setBounds(li_itemnewX, li_itemnewY, (int) selectedLabels[i].getBounds().getWidth(), (int) selectedLabels[i].getBounds().getHeight()); //
				}
			}
		}

		public void dragExit(DragSourceEvent evt) {
		}

		public void dropActionChanged(DragSourceDragEvent evt) {
		}

		public void dragDropEnd(DragSourceDropEvent evt) {
			ItemLabel label = (ItemLabel) evt.getDragSourceContext().getComponent();
			label.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
		}

		private void showPopMenu(final ItemLabel _label, int _x, int _y) {
			JPopupMenu popMenu = new JPopupMenu(); //
			JMenuItem menuItem_setkey = new JMenuItem("Set Key&Name"); //
			JMenuItem menuItem_setfont = new JMenuItem("Set Font"); //
			JMenuItem menuItem_movefront = new JMenuItem("Move to front"); //
			JMenuItem menuItem_moveback = new JMenuItem("Move to back"); //

			menuItem_setkey.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onSetKey(_label); //
				}
			});

			menuItem_setfont.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onSetFont(_label); //
				}
			});

			menuItem_movefront.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					moveToFront(_label); //
				}
			});

			menuItem_moveback.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					moveToBack(_label); //
				}
			});

			popMenu.add(menuItem_setkey); //
			popMenu.add(menuItem_setfont); //
			popMenu.add(menuItem_movefront); //
			popMenu.add(menuItem_moveback); //

			popMenu.show(_label, _x, _y); //
		}

		/**
		 * @param _label 
		 * 
		 */
		private void onSetKey(ItemLabel _label) {
			String str_oldkey = _label.getName(); //
			JLabel label_1 = new JLabel("ItemKey");//
			JLabel label_2 = new JLabel("ItemName");//
			JTextField textField_1 = new JTextField(_label.getName());//
			JTextField textField_2 = new JTextField(_label.getText());//

			label_1.setBounds(5, 5, 80, 20);
			label_2.setBounds(5, 30, 80, 20);
			textField_1.setBounds(85, 5, 200, 20);
			textField_2.setBounds(85, 30, 200, 20);

			JPanel panel = new JPanel(); //
			panel.setLayout(null); //
			panel.setPreferredSize(new Dimension(300, 55)); //

			panel.add(label_1); //
			panel.add(label_2); //
			panel.add(textField_1); //
			panel.add(textField_2); //

			if (JOptionPane.showConfirmDialog(this, panel, "Set Key&Name", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				if (textField_1.getText() == null || textField_1.getText().trim().equals("")) {
					MessageBox.show(this, "Item key is null."); //
					return;
				}

				if (textField_2.getText() == null || textField_2.getText().trim().equals("")) {
					MessageBox.show(this, "Item Name is null."); //
					return;
				}

				String str_itemkey = textField_1.getText();
				String str_itemname = textField_2.getText();

				_label.setName(str_itemkey); //
				_label.setText(str_itemname); //
				_label.setToolTipText(str_itemkey); //

				vm_items.remove(str_oldkey); //先移去原来的
				vm_items.put(str_itemkey, _label); //再新增新的

				_label.updateUI(); //
			}
		}

		private void onSetFont(final ItemLabel _label) {
			Font font = _label.getFont();
			String str_fontname = font.getName();
			int li_fontsize = font.getSize();
			int li_fontstyle = font.getStyle();

			JComboBox combox_type = new JComboBox(); //
			JComboBox combox_size = new JComboBox(); //
			JComboBox combox_style = new JComboBox(); //

			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			String fontNames[] = ge.getAvailableFontFamilyNames();
			for (int i = 0; i < fontNames.length; i++) {
				combox_type.addItem(fontNames[i]); //
			}
			combox_type.setSelectedItem(str_fontname); //

			combox_size.addItem("6"); //
			combox_size.addItem("8"); //
			combox_size.addItem("9"); //
			combox_size.addItem("10"); //
			combox_size.addItem("12"); //
			combox_size.addItem("14"); //
			combox_size.addItem("16"); //
			combox_size.addItem("20"); //
			combox_size.addItem("35"); //
			combox_size.addItem("45"); //
			combox_size.setSelectedItem("" + li_fontsize); //

			combox_style.addItem("PLAIN"); //
			combox_style.addItem("BOLD"); //
			combox_style.addItem("ITALIC"); //
			combox_style.setSelectedIndex(li_fontstyle); //

			combox_type.setPreferredSize(new Dimension(125, 20)); //
			combox_size.setPreferredSize(new Dimension(50, 20)); //
			combox_style.setPreferredSize(new Dimension(85, 20)); //

			JPanel panel = new JPanel(); //
			panel.setLayout(new FlowLayout()); //
			panel.setPreferredSize(new Dimension(350, 25)); //
			panel.add(combox_type); //
			panel.add(combox_size); //
			panel.add(combox_style); //

			if (JOptionPane.showConfirmDialog(this, panel, "Font", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				String str_type = "" + combox_type.getSelectedItem();
				int li_style = Integer.parseInt("" + combox_style.getSelectedIndex());
				int li_size = Integer.parseInt("" + combox_size.getSelectedItem());
				_label.setFont(new Font(str_type, li_style, li_size));
			}
		}

		private void moveToFront(final ItemLabel _label) {
			mainPanel.setLayer(_label, JLayeredPane.PALETTE_LAYER.intValue()); //
			_label.setMyLayer(JLayeredPane.PALETTE_LAYER.intValue()); //
			_label.updateUI(); //
		}

		private void moveToBack(final ItemLabel _label) {
			mainPanel.setLayer(_label, JLayeredPane.DEFAULT_LAYER.intValue()); //
			_label.setMyLayer(JLayeredPane.DEFAULT_LAYER.intValue()); //
			_label.updateUI(); //
		}

		public void keyPressed(KeyEvent e) {
			if (e.isAltDown()) {
				if (e.getKeyCode() == e.VK_LEFT) {
					onMoveLeft(); //
				} else if (e.getKeyCode() == e.VK_RIGHT) {
					onMoveRight(); //
				} else if (e.getKeyCode() == e.VK_UP) {
					onMoveUp(); //
				} else if (e.getKeyCode() == e.VK_DOWN) {
					onMoveDown(); //
				}
			} else {
				if (e.getKeyCode() == e.VK_DELETE) {
					onDelete(); //
				}
			}
		}

		public void keyReleased(KeyEvent e) {
		}

		public void keyTyped(KeyEvent e) {
		}

		public boolean isShowborder() {
			return showborder;
		}

		public void setShowborder(boolean showborder) {
			this.showborder = showborder;
		}

		public boolean isShowbaseline() {
			return showbaseline;
		}

		public void setShowbaseline(boolean showbaseline) {
			this.showbaseline = showbaseline;
		}

		public int getMyLayer() {
			return mylayer; //
		}

		public void setMyLayer(int layer) {
			this.mylayer = layer;
		}

	}

	/**
	 * 编辑器.
	 * @author xch
	 *
	 */
	class EditerTextField extends JTextField {
		private JLabel bindLabel = null;

		public EditerTextField() {
			super(); //
			this.setBounds(0, 0, 200, 20); //
			this.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1)); //边框
			this.setBackground(new Color(230, 255, 255)); //
			this.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent evt) {
					EditerTextField textfield = (EditerTextField) evt.getSource(); //

					if (evt.getKeyCode() == KeyEvent.VK_ENTER || evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
						textfield.setVisible(false); //
					}

					if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
						textfield.getBindLabel().setText(textfield.getText()); //
					}
				}

			});
		}

		public JLabel getBindLabel() {
			return bindLabel;
		}

		public void setBindLabel(JLabel bindLabel) {
			this.bindLabel = bindLabel;
		}
	}

}
