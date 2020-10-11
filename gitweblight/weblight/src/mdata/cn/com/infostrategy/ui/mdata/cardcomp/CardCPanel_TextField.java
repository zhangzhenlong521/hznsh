/**************************************************************************
 * $RCSfile: CardCPanel_TextField.java,v $  $Revision: 1.12 $  $Date: 2013/02/28 06:14:48 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.NumberFormatdocument;
import cn.com.infostrategy.ui.mdata.WLTHyperLinkLabel;

public class CardCPanel_TextField extends AbstractWLTCompentPanel implements CaretListener, FocusListener {

	private static final long serialVersionUID = -6967496356909878013L;

	private Pub_Templet_1_ItemVO templetItemVO = null;
	private String type = null;

	private String key = null;

	private String name = null;

	protected String hyperlinkdesc = null; //超链接定义
	private StringItemVO initItemVO = null; //初始数据
	private BillPanel billPanel = null;

	private int li_label_width = 120;

	private int li_textfield_width = 150;

	private int li_cardheight = 20; //

	private JLabel label = null;

	private JTextField textField = null;

	private boolean isNeed = false;//该属性是什么意思，代码中没用到啊，谁能解释一下？【李春娟/2012-02-24】

	private boolean isEditable = true;

	private int li_width_all;

	private StringItemVO oldItemVO = null; //原来的数据,用于判断该项值是否发生变化!

	private JPopupMenu popMenu = null; //弹出菜单..
	private JMenuItem menuItem_cut, menuItem_paste;
	private CommUCDefineVO ucvo = null;

	public CardCPanel_TextField(Pub_Templet_1_ItemVO _templetVO) {
		this(_templetVO, WLTConstants.COMP_TEXTFIELD); //默认是纯文本
	}

	public CardCPanel_TextField(Pub_Templet_1_ItemVO _templetVO, int _width, int _height) {
		this(_templetVO, WLTConstants.COMP_TEXTFIELD, _width, _height); //默认是纯文本
	}

	public CardCPanel_TextField(Pub_Templet_1_ItemVO _templetVO, String _type) {
		super();
		this.type = _type; //类型,有文本框,数字框,密码框
		this.templetItemVO = _templetVO;
		this.key = templetItemVO.getItemkey();
		this.name = templetItemVO.getItemname();
		this.hyperlinkdesc = _templetVO.getHyperlinkdesc();
		this.li_textfield_width = templetItemVO.getCardwidth().intValue(); // 设置宽度
		this.li_cardheight = templetItemVO.getCardHeight().intValue(); //高度
		initialize();
	}

	public CardCPanel_TextField(Pub_Templet_1_ItemVO _templetVO, String _type, int _width, int _height) {
		super();
		this.type = _type; //类型,有文本框,数字框,密码框
		this.templetItemVO = _templetVO;
		this.key = templetItemVO.getItemkey();
		this.name = templetItemVO.getItemname();
		this.hyperlinkdesc = _templetVO.getHyperlinkdesc();
		this.li_textfield_width = _width; //设置宽度..
		this.li_cardheight = _height; //高度
		initialize();
	}

	public CardCPanel_TextField(Pub_Templet_1_ItemVO _templetVO, String _type, StringItemVO _initItemVO, BillPanel _billPanel) {
		super();
		this.type = _type; //类型,有文本框,数字框,密码框
		this.templetItemVO = _templetVO;
		this.key = templetItemVO.getItemkey();
		this.name = templetItemVO.getItemname();
		this.hyperlinkdesc = _templetVO.getHyperlinkdesc();
		this.initItemVO = _initItemVO; //
		this.billPanel = _billPanel; //
		this.li_textfield_width = templetItemVO.getCardwidth().intValue(); //设置宽度
		this.li_cardheight = templetItemVO.getCardHeight().intValue(); //高度
		initialize();
	}

	public CardCPanel_TextField(String _key, String _name) {
		this(_key, _name, WLTConstants.COMP_TEXTFIELD);
	}

	public CardCPanel_TextField(String _key, String _name, String _type) {
		super();
		this.key = _key;
		this.name = _name;
		this.type = _type; //
		initialize();
	}

	public CardCPanel_TextField(String _key, String _name, String _type, int _label_width, int _textfield_width) {
		super();
		this.key = _key;
		this.name = _name;
		this.type = _type; //
		this.li_label_width = _label_width;
		this.li_textfield_width = _textfield_width; //
		initialize();
	}

	public CardCPanel_TextField(String _key, String _name, boolean isNeed) {
		super();
		this.key = _key;
		this.name = _name;
		this.isNeed = isNeed;
		initialize();
	}

	public CardCPanel_TextField(String _key, String _name, boolean isNeed, boolean isEditable) {
		super(); //
		this.key = _key;
		this.name = _name;
		this.isNeed = isNeed;
		this.isEditable = isEditable;
		initialize();
	}

	public CardCPanel_TextField(String _key, String _name, int _width) {
		super();
		this.type = WLTConstants.COMP_TEXTFIELD;
		this.key = _key;
		this.name = _name;
		this.li_textfield_width = _width;
		initialize();
	}

	public CardCPanel_TextField(String _key, String _name, int _width, boolean isNeed) {
		super();
		this.key = _key;
		this.name = _name;
		this.li_textfield_width = _width;
		this.isNeed = isNeed;
		initialize();
	}

	private void initialize() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		this.setBackground(LookAndFeel.cardbgcolor); //
		if (templetItemVO != null) {
			label = createLabel(templetItemVO); //采用父亲提供的方法创建Label
		} else {
			label = new JLabel(name); //
			label.setHorizontalAlignment(SwingConstants.RIGHT); //
			label.setVerticalAlignment(SwingConstants.TOP); //
			label.setPreferredSize(new Dimension(li_label_width, li_cardheight)); //设置宽度
		}

		ucvo = templetItemVO.getUCDfVO();
		if (this.type.equals(WLTConstants.COMP_TEXTFIELD)) { //如果是纯文本框
			textField = new JTextField(); //
			addComponentUndAndRedoFunc(textField);
			if (ucvo != null && "自定义校验".equals(ucvo.getConfValue("类型"))) { //by haoming 2016-05-09
				textField.setDocument(new NumberFormatdocument(ucvo)); //
			}
		} else if (this.type.equals(WLTConstants.COMP_NUMBERFIELD)) { //如果是数字框
			textField = new JFormattedTextField(); //格式化的数字框
			textField.setHorizontalAlignment(SwingConstants.RIGHT); //
			textField.setDocument(new NumberFormatdocument(ucvo)); //定义数字框只能输入数字,输入字母不让键入!!!!
		} else if (this.type.equals(WLTConstants.COMP_PASSWORDFIELD)) { //如果是密码框
			textField = new JPasswordField(); //
			textField.setHorizontalAlignment(SwingConstants.LEFT); //
		} else if (this.type.equals(WLTConstants.COMP_REGULAR)) {//如果是正则表达式 [zzl 2016-11-22]
			textField = new JTextField();
			textField.setHorizontalAlignment(SwingConstants.LEFT);
			textField.addCaretListener(this);
			textField.addFocusListener(this);

		}

		//textField.setOpaque(false);
		textField.setForeground(LookAndFeel.inputforecolor_enable); //有效时的前景色
		textField.setBackground(LookAndFeel.inputbgcolor_enable); //有效时的背景色

		if (!isEditable) {
			textField.setText("Sequence");
			setItemEditable(false);
		}

		popMenu = new JPopupMenu(); //
		menuItem_cut = new JMenuItem("剪切"); //
		JMenuItem menuItem_copy = new JMenuItem("复制"); //
		menuItem_paste = new JMenuItem("粘贴"); //
		JMenuItem menuItem_selectall = new JMenuItem("选择全部"); //
		menuItem_cut.setPreferredSize(new Dimension(70, 19)); //
		menuItem_copy.setPreferredSize(new Dimension(70, 19)); //
		menuItem_paste.setPreferredSize(new Dimension(70, 19)); //
		menuItem_selectall.setPreferredSize(new Dimension(70, 19)); //
		popMenu.add(menuItem_cut); //剪切
		popMenu.add(menuItem_copy); //复制
		popMenu.add(menuItem_paste); //粘贴
		popMenu.add(menuItem_selectall); //全选
		menuItem_cut.setVisible(this.isEditable);//不可编辑时剪切设置为不可用，这样就会出现"剪切"不可用，"复制"可用，"粘贴"不可用，"选择全部"可用，感觉很乱，所以改为不可见！【李春娟/2012-02-24】
		menuItem_paste.setVisible(this.isEditable);

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
				}
			}
		}); //

		textField.setPreferredSize(new Dimension(li_textfield_width, li_cardheight)); //
		if (!"WebPushUIByHm".equalsIgnoreCase(UIManager.getLookAndFeel().getID())) { //自定义UI中已经去掉border。
			if (templetItemVO != null) {
				if (templetItemVO.getPub_Templet_1VO().getCardBorder().equals("LINE")) { //如果是底线
					textField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, cn.com.infostrategy.ui.common.LookAndFeel.compBorderLineColor)); //
				} else {
					textField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, cn.com.infostrategy.ui.common.LookAndFeel.compBorderLineColor)); //
				}
			} else {
				textField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, cn.com.infostrategy.ui.common.LookAndFeel.compBorderLineColor)); //
			}
		}

		this.add(label);
		this.add(textField);

		//加入超链接!!!,目前文本框,只有在卡片中才有超链接出现,列表中不出现!!
		int li_hyperlinkwidth = 0;
		if (hyperlinkdesc != null) {
			WLTHyperLinkLabel[] linkLabels = this.createHyperLinkLabel(this.key, this.name, hyperlinkdesc, this.billPanel); //
			if (linkLabels != null) {
				if (linkLabels != null) { //如果有超连接
					for (int i = 0; i < linkLabels.length; i++) {
						li_hyperlinkwidth = li_hyperlinkwidth + (int) linkLabels[i].getPreferredSize().getWidth(); //
						this.add(linkLabels[i]); //
					}
				}
			}
		}

		li_width_all = (int) (label.getPreferredSize().getWidth() + textField.getPreferredSize().getWidth() + li_hyperlinkwidth); //总宽度
		this.setPreferredSize(new Dimension(li_width_all, li_cardheight)); //
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
		String str_text = null;
		if (textField instanceof JPasswordField) {
			str_text = new String(((JPasswordField) textField).getPassword());
		} else {
			str_text = textField.getText();
		}
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
		this.repaint(); //整体重新绘制;
	}

	public void reset() {
		oldItemVO = null; //
		textField.setText("");
	}

	public void setItemEditable(boolean _bo) {
		isEditable = _bo;
		textField.setEditable(isEditable);
		if (isEditable) {
			textField.setForeground(LookAndFeel.inputforecolor_enable); //有效时的前景色
			textField.setBackground(LookAndFeel.inputbgcolor_enable); //有效时的背景色
		} else {
			textField.setForeground(LookAndFeel.inputforecolor_disable); //无效时的前景色
			textField.setBackground(LookAndFeel.inputbgcolor_disable); //无效时的背景色
		}
		menuItem_cut.setVisible(this.isEditable);
		menuItem_paste.setVisible(this.isEditable);
	}

	@Override
	public boolean isItemEditable() {
		return textField.isEditable();
	}

	public void setItemVisiable(boolean _bo) {
		this.setVisible(_bo); //
	}

	public Object getObject() {
		return new StringItemVO(getValue());
	}

	public void setObject(Object _obj) {
		if (_obj != null) {
			try {
				if (_obj instanceof String) {//这里可能这是默认值设置为String类型，故判断一下，否则会发生数据转换异常【李春娟/2016-12-17】
					oldItemVO = new StringItemVO((String) _obj);
				} else {
					oldItemVO = (StringItemVO) _obj; //
					setValue(oldItemVO.getStringValue());
				}
			} catch (ClassCastException ex) {
				ex.printStackTrace(); //
				throw new WLTAppException("模板[" + templetItemVO.getPub_Templet_1VO().getTempletcode() + "]子项[" + templetItemVO.getItemkey() + "]发生数据类型转换错误,很可能是因为加载公式重新修改了文本框的数据类型!!!" + ex.getMessage()); //
			}
		}
	}

	public void focus() {
		textField.requestFocus(); //
		textField.requestFocusInWindow();
	}

	public int getAllWidth() {
		return li_width_all;
	}

	public String getType() {
		return type;
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

	@Override
	/**
	 * 重构,以文本框为准! 在stopediting时需要
	 */
	public boolean isFocusOwner() {
		return textField.isFocusOwner(); //
	}

	public void caretUpdate(CaretEvent e) {
		//		System.out.println("000000000000000000000");

	}

	public void focusGained(FocusEvent focusevent) {
		//		System.out.println("11111111111111111111111");

	}

	public void focusLost(FocusEvent focusevent) {
		try {
			String style = null;
			style = ucvo.getConfValue("类型");
			String str = textField.getText();
			Pattern p = null;
			Matcher m = null;
			p = Pattern.compile(style);
			m = p.matcher(str.toString());
			if (m.matches()) {
				//校验通过

			} else {
				//校验不通过， zzl把值清空了。 应该还有更好的方法...
				textField.setText(null);
			}
		} catch (Exception e) {
			e.printStackTrace(); //
			throw new WLTAppException("正则表达式检验失败！");
		}
	}

}
