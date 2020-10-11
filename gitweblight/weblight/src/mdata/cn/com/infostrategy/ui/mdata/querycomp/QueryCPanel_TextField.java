/**************************************************************************
 * $RCSfile: QueryCPanel_TextField.java,v $  $Revision: 1.14 $  $Date: 2013/02/28 06:14:47 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.querycomp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.ScrollablePopupFactory;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.QuickSearchHisMap;
import cn.com.infostrategy.ui.mdata.QuickSearchHisMap.HisBean;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractWLTCompentPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.PopQuickSearchDialog;

public class QueryCPanel_TextField extends AbstractWLTCompentPanel implements KeyListener {

	private static final long serialVersionUID = -6967496356909878013L;

	private Pub_Templet_1_ItemVO templetItemVO = null;
	private String key = null;
	private String name = null;
	private StringItemVO initItemVO = null; //初始数据

	private int li_label_width = 120;
	private int li_textfield_width = 150;
	private int li_cardheight = 20; //

	private JLabel label = null;

	private JTextField textField = null;

	private int li_width_all;

	private StringItemVO oldItemVO = null; //原来的数据,用于判断该项值是否发生变化!

	private JPopupMenu popMenu = null; //弹出菜单..

	private PopQuickSearchDialog dialog; //弹出历史提示框

	public QueryCPanel_TextField(Pub_Templet_1_ItemVO _templetVO) {
		super();
		this.templetItemVO = _templetVO;
		this.key = templetItemVO.getItemkey();
		this.name = templetItemVO.getItemname();
		if (templetItemVO.getQuerylabelwidth() != null) {
			this.li_label_width = templetItemVO.getQuerylabelwidth().intValue(); //文字说明..
		}
		if (templetItemVO.getQuerycompentwidth() != null) {
			this.li_textfield_width = templetItemVO.getQuerycompentwidth().intValue(); // 设置宽度
		}
		if (templetItemVO.getQuerycompentheight() != null) {
			this.li_cardheight = templetItemVO.getQuerycompentheight().intValue(); // 设置高度
		}
		initialize();
	}

	public QueryCPanel_TextField(BillPanel billpanel, Pub_Templet_1_ItemVO _templetVO) {
		super();
		this.templetItemVO = _templetVO;
		this.key = templetItemVO.getItemkey();
		this.name = templetItemVO.getItemname();
		setBillPanel(billpanel); //
		if (templetItemVO.getQuerylabelwidth() != null) {
			this.li_label_width = templetItemVO.getQuerylabelwidth().intValue(); //文字说明..
		}
		if (templetItemVO.getQuerycompentwidth() != null) {
			this.li_textfield_width = templetItemVO.getQuerycompentwidth().intValue(); // 设置宽度
		}
		if (templetItemVO.getQuerycompentheight() != null) {
			this.li_cardheight = templetItemVO.getQuerycompentheight().intValue(); // 设置高度
		}
		initialize();
	}

	private void initialize() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		this.setBackground(LookAndFeel.systembgcolor); //
		if (templetItemVO != null) {
			label = createLabel(templetItemVO); //采用父亲提供的方法创建Label
			if (templetItemVO.getIsmustinput()) {//如果卡片定义为必输项，则需要判断查询面板中是否为必输项【李春娟/2012-03-19】
				if (!"Y".equalsIgnoreCase(templetItemVO.getIsQueryMustInput())) {//如果查询面板中不是必输项，则需要将必输符号"*"号去掉
					label.setText(label.getText().replace("*", ""));
				}
			} else if ("Y".equalsIgnoreCase(templetItemVO.getIsQueryMustInput())) {//如果卡片定义为非必输项，但查询面板中是必输项，则需要添加上必输符号"*"号
				label.setText("*" + label.getText());
				((WLTLabel) label).addStrItemColor("*", Color.RED); //
			}
		} else {
			label = new JLabel(name); //
			label.setHorizontalAlignment(SwingConstants.RIGHT); //
			label.setVerticalAlignment(SwingConstants.TOP); //
		}
		label.setBackground(LookAndFeel.systembgcolor);
		label.setPreferredSize(new Dimension(li_label_width, li_cardheight)); //设置宽度
		textField = new JTextField(); //
		addComponentUndAndRedoFunc(textField); // add Redo and Undo function 
		if (!"WebPushUIByHm".equalsIgnoreCase(UIManager.getLookAndFeel().getID())) { //自定义UI中已经去掉border。
			textField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, cn.com.infostrategy.ui.common.LookAndFeel.compBorderLineColor)); //
		}
		textField.setPreferredSize(new Dimension(li_textfield_width, li_cardheight)); //
		textField.addKeyListener(this);
		popMenu = new JPopupMenu(); //
		JMenuItem menuItem_cut = new JMenuItem("剪切"); //
		JMenuItem menuItem_copy = new JMenuItem("复制"); //
		JMenuItem menuItem_paste = new JMenuItem("粘贴"); //

		JMenuItem menuItem_selectall = new JMenuItem("选择全部"); //
		menuItem_cut.setPreferredSize(new Dimension(70, 19)); //
		menuItem_copy.setPreferredSize(new Dimension(70, 19)); //
		menuItem_paste.setPreferredSize(new Dimension(70, 19)); //
		menuItem_selectall.setPreferredSize(new Dimension(70, 19)); //
		popMenu.add(menuItem_cut); //剪切
		popMenu.add(menuItem_copy); //复制
		popMenu.add(menuItem_paste); //粘贴
		popMenu.add(menuItem_selectall); //全选

		menuItem_cut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCutText(); //选择文档
			}
		}); //

		menuItem_copy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCopyText(); //选择文档
			}
		}); //

		menuItem_paste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onPasteText(); //粘贴文档
			}
		}); //

		menuItem_selectall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSelectAll(); //选择全部
			}
		}); //

		textField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) { //如果是右键
					showTextAreaPopMenu((JTextField) e.getSource(), e.getX(), e.getY()); //
				} else if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) { //双击左键,弹出历史提示
					showQuickSearchPopupMenu();
				}
			}
		}); //
		this.add(label);
		this.add(textField);
		li_width_all = (int) (label.getPreferredSize().getWidth() + textField.getPreferredSize().getWidth()); //总宽度
		this.setPreferredSize(new Dimension(li_width_all, li_cardheight)); //
	}

	private void showQuickSearchPopupMenu() {
		List list = QuickSearchHisMap.getQuickSearchHisValues(templetItemVO.getPub_Templet_1VO().getTempletcode(), getItemKey(), QuickSearchHisMap.HIS_TYPE_QUERYPANEL);
		if (list != null) {
			List valueList = new ArrayList();
			for (int i = 0; i < list.size(); i++) {
				HisBean bean = (HisBean) list.get(i);
				valueList.add(bean.get_text());
			}
			if (valueList.size() == 0) {
				return;
			}
			dialog = new PopQuickSearchDialog(textField, QueryCPanel_TextField.this, true);
			dialog.addClickDeleteBtnAction(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JList list = (JList) e.getSource();
					QuickSearchHisMap.removeValue(templetItemVO.getPub_Templet_1VO().getTempletcode(), getItemKey(), QuickSearchHisMap.HIS_TYPE_QUERYPANEL, list.getSelectedValue());
				}
			});
			textField.putClientProperty("POPMENUTYPE", ScrollablePopupFactory.POP_TYPE_VERTICAL);
			dialog.setCondition(textField.getText());

			dialog.setList((Object[]) valueList.toArray(new Object[0]));
			textField.requestFocus(false);
			dialog.show(textField, 0, textField.getHeight() + 1);
		}
	}

	/**
	 * 剪切文件内容
	 */
	protected void onCutText() {
		StringSelection ss = new StringSelection(this.textField.getSelectedText()); //取得选择的内容
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null); //在系统剪贴板中设置
		try {
			textField.replaceSelection(""); //
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	/**
	 * 拷贝文件内容
	 */
	protected void onCopyText() {
		StringSelection ss = new StringSelection(this.textField.getSelectedText()); //取得选择的内容
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null); //在系统剪贴板中设置
	}

	/**
	 * 粘贴内容
	 */
	protected void onPasteText() {
		Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		try {
			if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				String text = (String) t.getTransferData(DataFlavor.stringFlavor); //
				if (text == null) {
					return;
				}
				int li_start = textField.getSelectionStart(); //
				int li_end = textField.getSelectionEnd(); //
				if (li_start == li_end) {
					String str_prefix = textField.getText().substring(0, li_start); //
					String str_subfix = textField.getText().substring(li_start, textField.getText().length()); //
					textField.setText(str_prefix + text + str_subfix); //
				} else {
					textField.replaceSelection(text); //
				}
			}
		} catch (Exception e) {
			e.printStackTrace(); //
		}

	}

	/**
	 * 选择所有内容
	 */
	protected void onSelectAll() {
		textField.selectAll(); //
	}

	/**
	 * 显示右键菜单.
	 * @param _textarea
	 * @param _x
	 * @param _y
	 */
	protected void showTextAreaPopMenu(JTextField _textfield, int _x, int _y) {
		this.popMenu.show(_textfield, _x, _y); //
	}

	public JLabel getLabel() {
		return label;
	}

	public JTextField getTextField() {
		return textField;
	}

	public String getItemKey() {
		return key;
	}

	public String getItemName() {
		return name;
	}

	public String getValue() {
		String str_text = textField.getText(); //
		return str_text;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return textField.getText();
	}

	public void setText(String _text) {
		textField.setText(_text);
	}

	public void setValue(String _value) {
		oldItemVO = new StringItemVO(_value); //旧数据更新
		textField.setText(_value); //
		textField.select(0, 0); //
	}

	public void reset() {
		oldItemVO = null; //
		textField.setText("");
	}

	public void setItemEditable(boolean _bo) {
		textField.setEditable(_bo); //
	}

	@Override
	public boolean isItemEditable() {
		return textField.isEditable();
	}

	public void setItemVisiable(boolean _bo) {
		this.setVisible(_bo); //
	}

	public Object getObject() {
		String str_text = getValue(); //
		if (str_text == null || str_text.trim().equals("")) {
			return null;
		} else {
			return new StringItemVO(str_text); //
		}
	}

	public void setObject(Object _obj) {
		if (_obj != null) {
			oldItemVO = (StringItemVO) _obj; //
			setValue(oldItemVO.getStringValue());
		}
	}

	public void focus() {
		textField.requestFocus(); //
		textField.requestFocusInWindow();
	}

	public int getAllWidth() {
		return li_width_all;
	}

	public void keyPressed(KeyEvent e) {
		if (e.getSource() == textField) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				if (dialog == null || !dialog.isVisible()) { //如果提示框没有弹出，进行查询
					if (getBillPanel() != null) {
						((BillQueryPanel) getBillPanel()).onQuickQuery(false);
					}
					textField.requestFocus(); //
					textField.requestFocusInWindow();
				} else {
					dialog.setObject();
					dialog.setVisible(false);
				}
			} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				if (dialog == null || !dialog.isVisible()) {
					showQuickSearchPopupMenu();
				} else {
					dialog.moveDown();
				}
			} else if (e.getKeyCode() == KeyEvent.VK_UP) {
				String textValue = textField.getText();
				if (textValue != null && dialog != null && dialog.isShowing() && textValue.equals(dialog.getCondition())) {
					dialog.moveUp();
				}
			} else if (e.getKeyCode() == KeyEvent.VK_ENTER && dialog.isShowing()) { //回车键。赋值
				dialog.setObject();
				dialog.setVisible(false);
			}
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {

	}

}
