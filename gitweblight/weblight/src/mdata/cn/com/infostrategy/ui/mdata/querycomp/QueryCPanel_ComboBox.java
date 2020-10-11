package cn.com.infostrategy.ui.mdata.querycomp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.ui.common.ImageIconFactory;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractWLTCompentPanel;

/**
 * 查询框的下拉框控件
 * @author xch
 *
 */
public class QueryCPanel_ComboBox extends AbstractWLTCompentPanel {
	private static final long serialVersionUID = 1L;

	private Pub_Templet_1_ItemVO templetItemVO = null; //
	private String key = null; //
	private String name = null; //

	protected ComBoxItemVO initItemVO = null; //
	private ComBoxItemVO[] itemVOs = null; ////

	private JLabel label = null; //
	private JComboBox combox = null; //下拉框,只在单选时才创建!

	private JTextField cmb_textField = null; //文本框!!
	private JButton arrowButton = null; //
	private ComBoxItemVO currComBoxItemVO = null; //当前的实际值!!

	private int li_label_width = 120; //
	private int li_combox_width = 150; //
	private int li_cardheight = 20; //
	private int li_width_all = 0;

	private long ll_closeDialogTime = 0; //
	private boolean isCloseByPressBtn = false; //
	private TBUtil tbUtil = null; //
	private Vector v_listener = new Vector(); //

	public QueryCPanel_ComboBox(Pub_Templet_1_ItemVO _templetVO) {
		super();
		this.setOpaque(false);
		this.templetItemVO = _templetVO;
		this.key = templetItemVO.getItemkey();
		this.name = templetItemVO.getItemname();
		if (templetItemVO.getQueryItemType() != null && !templetItemVO.getQueryItemType().trim().equals("")) { //如果定义了查询框的条件
			this.itemVOs = templetItemVO.getQueryComBoxItemVos(); //
		} else {
			this.itemVOs = templetItemVO.getComBoxItemVos(); ////
		}
		if (templetItemVO.getQueryUCDfVO() == null) { //如果查询框定义VO为空,则使用
			templetItemVO.setQueryUCDfVO(templetItemVO.getUCDfVO()); //
		}
		if (templetItemVO.getQuerylabelwidth() != null) {
			this.li_label_width = templetItemVO.getQuerylabelwidth().intValue(); //
		}
		if (templetItemVO.getQuerycompentwidth() != null) { //宽度
			this.li_combox_width = templetItemVO.getQuerycompentwidth().intValue(); //
		}
		if (templetItemVO.getQuerycompentheight() != null) { //高度!!
			this.li_cardheight = templetItemVO.getQuerycompentheight().intValue(); // 设置高度
		}
		initialize(); //
	}

	public QueryCPanel_ComboBox(Pub_Templet_1_ItemVO _templetVO, ComBoxItemVO _initItemVO, BillPanel _billPanel) {
		super();
		this.templetItemVO = _templetVO;
		this.key = templetItemVO.getItemkey();
		this.name = templetItemVO.getItemname();
		this.li_cardheight = _templetVO.getCardHeight().intValue();
		if (templetItemVO.getQueryItemType() != null && !templetItemVO.getQueryItemType().trim().equals("")) { //如果定义了查询框的条件,即可能查询时的条件与录入时不一样!!
			this.itemVOs = templetItemVO.getQueryComBoxItemVos(); //
		} else {
			this.itemVOs = templetItemVO.getComBoxItemVos(); ////
		}
		if (templetItemVO.getQueryUCDfVO() == null) {
			templetItemVO.setQueryUCDfVO(templetItemVO.getUCDfVO()); //
		}
		this.initItemVO = _initItemVO; //
		setBillPanel(_billPanel); //
		this.li_label_width = templetItemVO.getQuerylabelwidth().intValue(); //
		this.li_combox_width = templetItemVO.getQuerycompentwidth().intValue(); //
		if (templetItemVO.getQuerycompentheight() != null) { //高度!!
			this.li_cardheight = templetItemVO.getQuerycompentheight().intValue(); // 设置高度
		}
		initialize();
	}

	public QueryCPanel_ComboBox(String _key, String _name, String _sql) {
		super(); //
		this.key = _key;
		this.name = _name;
		try {
			HashVO[] vos = UIUtil.getHashVoArrayByDS(null, _sql); //
			ComBoxItemVO[] tmp_itemVOs = new ComBoxItemVO[vos.length]; //
			for (int i = 0; i < vos.length; i++) {
				tmp_itemVOs[i] = new ComBoxItemVO(vos[i]); //
			}
			this.itemVOs = tmp_itemVOs;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		initialize();
	}

	public QueryCPanel_ComboBox(String _key, String _name, ComBoxItemVO[] _itemVOs) {
		super(); //
		this.key = _key;
		this.name = _name;
		this.itemVOs = _itemVOs;
		initialize();
	}

	private void initialize() {
		if (!isMultiSelect()) { //如果不是多选,即单选
			initialize1(); //单选的初始化逻辑!
		} else {
			initialize2(); //多选的初始化逻辑!
		}
	}

	//单选时的初始化逻辑
	private void initialize1() {
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
		}
		label.setBackground(LookAndFeel.systembgcolor);
		label.setPreferredSize(new Dimension(li_label_width, li_cardheight)); //设置宽度
		//		label.setPreferredSize(new Dimension(li_label_width, 20)); //设置宽度
		combox = new JComboBox(); //
		combox.setLightWeightPopupEnabled(false); //
		combox.setEditable(true); //
		combox.setOpaque(false); //
		combox.setPreferredSize(new Dimension(li_combox_width, li_cardheight)); //
		cmb_textField = ((JTextField) ((JComponent) combox.getEditor().getEditorComponent()));

		cmb_textField.setBackground(LookAndFeel.inputbgcolor_enable); //与参照的背景颜色一致【李春娟/2012-03-29】
		cmb_textField.setForeground(Color.BLACK); //
		if (!"WebPushUIByHm".equalsIgnoreCase(UIManager.getLookAndFeel().getID())) { //自定义UI中已经去掉border。
			if (templetItemVO != null) {
				if (templetItemVO.getPub_Templet_1VO().getCardBorder().equals("BORDER")) {
					cmb_textField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, cn.com.infostrategy.ui.common.LookAndFeel.compBorderLineColor)); //
				} else if (templetItemVO.getPub_Templet_1VO().getCardBorder().equals("LINE")) {
					cmb_textField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, cn.com.infostrategy.ui.common.LookAndFeel.compBorderLineColor)); //
				}
			} else {
				cmb_textField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, cn.com.infostrategy.ui.common.LookAndFeel.compBorderLineColor)); //
			}
		}

		cmb_textField.setEditable(false); //
		combox.addItem(new ComBoxItemVO("", "", "")); //第一个总是空值!!
		if (itemVOs != null) {
			for (int i = 0; i < itemVOs.length; i++) {
				combox.addItem(itemVOs[i]); //赋值!!!!!!!
			}
		}
		JPanel panel_tmp = new JPanel(new BorderLayout(0, 0)); //
		panel_tmp.setBorder(BorderFactory.createEmptyBorder()); //
		panel_tmp.setPreferredSize(new Dimension(li_combox_width, li_cardheight)); //
		panel_tmp.setOpaque(false); //
		panel_tmp.add(combox, BorderLayout.NORTH); //
		this.add(label);
		this.add(panel_tmp);
		//System.out.println(label.getPreferredSize().getHeight() + ":==========");
		li_width_all = (int) (label.getPreferredSize().getWidth() + combox.getPreferredSize().getWidth()); //总宽度
		this.setPreferredSize(new Dimension(li_width_all, li_cardheight)); //
	}

	//多选状态时的初始化逻辑!
	private void initialize2() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0)); //水平布局
		//标签说明
		if (templetItemVO != null) {
			label = createLabel(templetItemVO); //采用父亲提供的方法创建Label
			if (templetItemVO.getIsmustinput()) {//如果卡片定义为必输项，则需要判断查询面板中是否为必输项。刚开始弄了单选的情况，没有弄多选的【李春娟/2012-03-20】
				if (!"Y".equalsIgnoreCase(templetItemVO.getIsQueryMustInput())) {//如果查询面板中不是必输项，则需要将必输符号"*"号去掉
					label.setText(label.getText().replace("*", ""));
				}
			} else if ("Y".equalsIgnoreCase(templetItemVO.getIsQueryMustInput())) {//如果卡片定义为非必输项，但查询面板中是必输项，则需要添加上必输符号"*"号
				label.setText("*" + label.getText());
				((WLTLabel) label).addStrItemColor("*", Color.RED); //
			}
		} else {
			label = new JLabel(name); //
		}
		label.setPreferredSize(new Dimension(li_label_width, li_cardheight)); //设置宽度
		//文本框
		cmb_textField = new JTextField(); //文本框!!
		cmb_textField.setBackground(LookAndFeel.inputbgcolor_enable); //与参照的背景颜色一致【李春娟/2012-03-29】
		if (!"WebPushUIByHm".equalsIgnoreCase(UIManager.getLookAndFeel().getID())) { //自定义UI中已经去掉border。
			cmb_textField.setBorder(BorderFactory.createLineBorder(LookAndFeel.compBorderLineColor)); //
		}
		cmb_textField.setPreferredSize(new Dimension(li_combox_width - 20, 20)); //高度是20!
		cmb_textField.setEditable(false); //是不可编辑的

		//按钮
		if ("WebPushUIByHm".equalsIgnoreCase(UIManager.getLookAndFeel().getID())) { //自定义UI中已经去掉border。
			arrowButton = new JButton() {
				Icon ic = ImageIconFactory.getDownEntityArrowIcon(new Color(120, 120, 120));

				public void paint(Graphics g) {
					super.paint(g);
					if (getModel().isEnabled() && getModel().isPressed()) {
						ic.paintIcon(this, g, 5, this.getHeight() / 2 - 4);
					} else {
						ic.paintIcon(this, g, 4, this.getHeight() / 2 - 5);
					}
				}
			}; //	
		} else {
			arrowButton = new JButton(ImageIconFactory.getDownEntityArrowIcon(new Color(61, 61, 61)));
			arrowButton.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, LookAndFeel.compBorderLineColor)); //
		}
		arrowButton.setFocusable(false); //
		arrowButton.setBackground(LookAndFeel.systembgcolor); //
		arrowButton.setMargin(new Insets(0, 0, 0, 0)); //
		arrowButton.setPreferredSize(new Dimension(20, 20)); //
		arrowButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.isShiftDown() && e.getButton() == MouseEvent.BUTTON3) { //如果是按住Shift
					onShowInfo(); //显示内容
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) { //必须是左键
					if (!arrowButton.isEnabled()) {//如果按钮不可用则直接返回【李春娟/2012-03-29】
						return;
					}
					onMousePressed(); //
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (!arrowButton.isEnabled()) {//如果按钮不可用则直接返回【李春娟/2012-03-29】
					return;
				}
				if (e.getButton() == MouseEvent.BUTTON1) { //必须是左键
					cmb_textField.requestFocus();
					onMouseReleased(); //
				}
			}

		}); //

		this.add(label);
		this.add(cmb_textField);
		this.add(arrowButton);

		li_width_all = (int) (label.getPreferredSize().getWidth() + cmb_textField.getPreferredSize().getWidth() + arrowButton.getPreferredSize().getWidth()); //总宽度
		this.setPreferredSize(new Dimension(li_width_all, li_cardheight)); //

		if (initItemVO != null) {
			setObject(initItemVO); //
		}
		arrowButton.putClientProperty("JButton.RefTextField", cmb_textField);
		cmb_textField.putClientProperty("JTextField.OverWidth", (int) arrowButton.getPreferredSize().getWidth());
	}

	public JLabel getLabel() {
		return label;
	}

	public String getItemKey() {
		return key;
	}

	public String getItemName() {
		return name;
	}

	//根据权限需要设置查询框某些下拉框不显示【李春娟/2017-06-28】
	public JComboBox getComBox() {
		return combox;
	}

	//查询时是否可以多选,即有的下拉框在录入时是单选,但在查询时是多选
	public boolean isMultiSelect() {
		if (this.templetItemVO != null && this.templetItemVO.getQueryUCDfVO() != null && this.templetItemVO.getQueryUCDfVO().getConfBooleanValue("查询时是否多选", false)) {
			return true; //
		} else {
			return false; //
		}
	}

	//取得实际值!!
	public String getValue() { //
		if (!isMultiSelect()) { //如果是单选!
			return getValue1(); //
		} else {
			return getValue2(); //
		}
	}

	//单选时的取值逻辑!
	private String getValue1() { //
		Object obj = null;
		String str_text = cmb_textField.getText(); //
		if (str_text == null || str_text.trim().equals("")) {
			return null;
		}
		if (this.itemVOs == null) {
			return null;
		}
		for (int i = 0; i < this.itemVOs.length; i++) {
			if (itemVOs[i].getName().equals(str_text)) {
				obj = itemVOs[i];
				return ((ComBoxItemVO) obj).getId(); //返回主键!!
			}
		}
		return str_text; //
	}

	private String getValue2() { //
		if (currComBoxItemVO == null || currComBoxItemVO.getId() == null) {
			return null; //
		}
		return currComBoxItemVO.getId(); //
	}

	/**
	 * 设置值!!
	 */
	public void setValue(String _value) {
		if (!isMultiSelect()) { //如果是单选!
			setValue1(_value); //
		} else {
			setValue2(_value); //多选时的设置值!
		}
	}

	//单选时设置值!
	private void setValue1(String _value) { //
		if (itemVOs == null) { //
			this.combox.setSelectedIndex(0); //如果为空,则设置第一个值!其实就是空值!
		} else {
			for (int i = 0; i < itemVOs.length; i++) {
				if (itemVOs[i].getId().equalsIgnoreCase(_value)) {
					this.combox.setSelectedIndex(i + 1); //设置值!因为第一个总是空值,所以序号要加1
					return;
				}
			}
		}
	}

	//设置实际值! 需要从定义列表中找到对应的名称,麻烦一点!
	public void setValue2(String _value) { //
		if (_value == null || _value.trim().equals("")) { //如果为空!!
			reset(); //
		}
		String[] str_ids = getTBUtil().split(_value, ";"); //分隔所有id
		String[] str_codes = new String[str_ids.length]; //
		String[] str_names = new String[str_ids.length]; //
		for (int i = 0; i < str_ids.length; i++) { //遍历!!
			for (int j = 0; j < itemVOs.length; j++) {
				if (itemVOs[j].getId().equals(str_ids[i])) { //如果匹配上的定义列表中的id
					str_codes[i] = itemVOs[j].getCode(); //
					str_names[i] = itemVOs[j].getName(); //
					break; //
				}
			} //end for j
		}
		if (str_ids.length <= 1) { //如果只有一个!!则没有分号,兼容旧的!!
			this.currComBoxItemVO = new ComBoxItemVO(str_ids[0], str_codes[0], str_names[0]); //

		} else {
			StringBuilder sb_ids = new StringBuilder(";"); //
			StringBuilder sb_codes = new StringBuilder(";"); //
			StringBuilder sb_names = new StringBuilder(); //
			for (int i = 0; i < str_ids.length; i++) { //遍历所有Id..
				sb_ids.append(str_ids[i] + ";"); //
				sb_codes.append(str_codes[i] + ";"); //
				sb_names.append(str_names[i] + ";"); //
			}
			this.currComBoxItemVO = new ComBoxItemVO(sb_ids.toString(), sb_codes.toString(), sb_names.toString()); //赋值!!

		}
		cmb_textField.setText(currComBoxItemVO.getName()); //
		cmb_textField.setSelectionStart(0); //
		cmb_textField.setSelectionEnd(0); //
	}

	public Object getObject() {
		if (!isMultiSelect()) { //如果是单选,则从控件中取!!!
			String str_text = cmb_textField.getText(); //
			if (str_text == null || str_text.trim().equals("")) {
				return null;
			}
			if (this.itemVOs == null) {
				return null;
			}
			for (int i = 0; i < this.itemVOs.length; i++) {
				if (itemVOs[i].getName().equals(str_text)) {
					return itemVOs[i];
				}
			}
			return new ComBoxItemVO(str_text, str_text, str_text); //
		} else { //如果是多选!
			return currComBoxItemVO; //直接返回当前变量!!!
		}
	}

	public void setObject(Object _obj) {
		ComBoxItemVO vo = (ComBoxItemVO) _obj;
		if (!isMultiSelect()) { //如果是单选,则从控件中取!!!
			combox.setSelectedItem(vo);
		} else { //如果是多选!!
			currComBoxItemVO = vo; //赋给当前值
			cmb_textField.setText(vo.getName()); //
			cmb_textField.setSelectionStart(0); //
			cmb_textField.setSelectionEnd(0); //
		}
	}

	public void reset() {
		if (!isMultiSelect()) {
			this.combox.setSelectedIndex(-1); //清空选择!!
		} else {
			currComBoxItemVO = null; //
			this.cmb_textField.setText(""); //
		}
	}

	public void focus() {
		if (combox != null) {
			combox.requestFocus();
			combox.requestFocusInWindow();
		}
	}

	public int getAllWidth() {
		return this.li_width_all;//
	}

	public void setItemEditable(boolean _bo) {
		if (combox != null) { //如果有下拉框!即单选模式!!
			combox.setEnabled(_bo);
		} else {
			this.arrowButton.setEnabled(_bo);
			if (_bo) {
				arrowButton.setBackground(LookAndFeel.systembgcolor); //
			} else {
				arrowButton.setBackground(LookAndFeel.inputbgcolor_disable); //
				arrowButton.setIcon(ImageIconFactory.getDownEntityArrowIcon(new Color(184, 207, 229)));//下拉框为不可用时的背景颜色
			}
		}
	}

	@Override
	public boolean isItemEditable() {
		return combox.isEditable();
	}

	public void setItemVisiable(boolean _bo) {
		this.setVisible(_bo); //
	}

	public ComBoxItemVO[] getItemVOs() {
		return itemVOs;
	}

	public void setItemVOs(ComBoxItemVO[] itemVOs) {
		this.itemVOs = itemVOs;
	}

	/**
	 * 塞入值..
	 * @param _itemVOs
	 */
	public void pushItems(ComBoxItemVO[] _itemVOs) {
		setItemVOs(_itemVOs); //
		combox.removeAllItems(); //
		//cmb_textField.setEditable(false); //
		combox.addItem(new ComBoxItemVO("", "", "")); //第一个总是空值!!
		if (itemVOs != null) {
			for (int i = 0; i < itemVOs.length; i++) {
				combox.addItem(itemVOs[i]);
			}
		}
	}

	//点击按钮弹出框!!!
	private void onMousePressed() {
		long ll_delay = Math.abs(System.currentTimeMillis() - ll_closeDialogTime); //
		if (ll_delay < 30) {
			isCloseByPressBtn = true;
		} else {
			isCloseByPressBtn = false; //
		}
	}

	//点击释放,弹出窗口!!!每次都是创建!!
	private void onMouseReleased() {
		if (isCloseByPressBtn) { //如果是按下按钮使用弹出窗口关闭的,则不做直接返回,这样保证不会在弹出状态下点击时仍然是弹出的!!
			return;
		}
		PopDialog popDialog = null; //
		Window rootWindow = getWindowForComponent(this); //
		if (rootWindow instanceof Frame) {
			popDialog = new PopDialog((Frame) rootWindow, this.itemVOs, this.currComBoxItemVO); ////
		} else if (rootWindow instanceof Dialog) {
			popDialog = new PopDialog((Dialog) rootWindow, this.itemVOs, this.currComBoxItemVO); ////
		} else {
			popDialog = new PopDialog(JOptionPane.getRootFrame(), this.itemVOs, this.currComBoxItemVO); ////
		}
		Point point = new Point(0, 0); //
		popDialog.setTextField(cmb_textField);
		SwingUtilities.convertPointToScreen(point, cmb_textField); //取得位置!!
		popDialog.setSize((li_combox_width < 80 ? 150 : li_combox_width), 150); //
		popDialog.setLocation((int) point.getX(), (int) point.getY() + 20); //
		cmb_textField.putClientProperty("JTextField.DrawFocusBorder", "Y");
		popDialog.setVisible(true); //
	}

	//找出根窗口!!!
	private Window getWindowForComponent(Component parentComponent) throws HeadlessException {
		if (parentComponent == null)
			return JOptionPane.getRootFrame(); //
		if (parentComponent instanceof Frame || parentComponent instanceof Dialog)
			return (Window) parentComponent;
		return getWindowForComponent(parentComponent.getParent()); //递归!!!
	}

	//显示实际值!!
	private void onShowInfo() {
		if (this.currComBoxItemVO == null) {
			MessageBox.show(this, "当前值为空!"); //
			return;
		}
		if (this.currComBoxItemVO.getId() == null) {
			MessageBox.show(this, "当前值的id为空!"); //
			return;
		}

		StringBuilder sb_info = new StringBuilder(); //
		sb_info.append("id=[" + currComBoxItemVO.getId() + "]\r\n"); //
		sb_info.append("code=[" + currComBoxItemVO.getCode() + "]\r\n"); //
		sb_info.append("name=[" + currComBoxItemVO.getName() + "]\r\n"); //
		if (currComBoxItemVO.getHashVO() == null) {
			sb_info.append("HashVO为空!");
		} else {
			String[] str_keys = currComBoxItemVO.getHashVO().getKeys(); //
			sb_info.append("\r\n**********HashVO中的值***********\r\n");
			for (int i = 0; i < str_keys.length; i++) {
				sb_info.append(str_keys[i] + "=[" + currComBoxItemVO.getHashVO().getStringValue(str_keys[i]) + "]\r\n"); //
			}
		}
		MessageBox.show(this, sb_info.toString()); //
	}

	private TBUtil getTBUtil() {
		if (tbUtil != null) {
			return tbUtil;
		}
		tbUtil = new TBUtil();
		return tbUtil;
	}

	public void addActionListener(ActionListener _listener) {
		v_listener.add(_listener); //
	}

	class PopDialog extends JDialog implements MouseListener, ActionListener {
		private JList list = null; //
		WLTButton btn_confirm, btn_cancel; //
		private JTextField textField;

		//
		public void setTextField(JTextField field) {
			this.textField = field;
		}

		public PopDialog(Frame _parentFrame, ComBoxItemVO[] _comBoxItemVOs, ComBoxItemVO _comBoxItemVO) {
			super(_parentFrame); //
			initUI(_comBoxItemVOs, _comBoxItemVO); //
		}

		public PopDialog(Dialog _parentDialog, ComBoxItemVO[] _comBoxItemVOs, ComBoxItemVO _comBoxItemVO) {
			super(_parentDialog); //
			initUI(_comBoxItemVOs, _comBoxItemVO); //
		}

		private void initUI(ComBoxItemVO[] _comBoxItemVOs, ComBoxItemVO _comBoxItemVO) {
			this.setUndecorated(true); //
			this.setModalityType(java.awt.Dialog.ModalityType.MODELESS); //只对该frame有效..
			this.addWindowFocusListener(new WindowFocusListener() { //原有是添加FocusListener。但是把滚动条的UI重写后，这个对话框就不执行focusListener @author haoming
						public void windowGainedFocus(WindowEvent e) {

						}

						public void windowLostFocus(WindowEvent e) {
							JDialog dialog = (JDialog) e.getSource(); //
							dialog.dispose(); //关闭
							ll_closeDialogTime = System.currentTimeMillis(); //
							if (textField != null) {
								textField.putClientProperty("JTextField.DrawFocusBorder", null);
								textField.requestFocus();
							}
						}
					});

			//克隆一份!
			if (_comBoxItemVOs != null && _comBoxItemVOs.length > 0) {
				ComBoxItemVO[] cloneItemVOs = new ComBoxItemVO[_comBoxItemVOs.length]; //
				for (int i = 0; i < cloneItemVOs.length; i++) {
					cloneItemVOs[i] = (ComBoxItemVO) _comBoxItemVOs[i].deepClone(); //必须要克隆!!
				}

				//赋初始值,即将实际值中的id拆分,赋给弹出页面中的值!!!
				if (_comBoxItemVO != null && _comBoxItemVO.getId() != null) { //如果有初始值
					String[] str_ids = getTBUtil().split(_comBoxItemVO.getId(), ";"); //分隔!!
					for (int i = 0; i < str_ids.length; i++) {
						for (int j = 0; j < cloneItemVOs.length; j++) {
							if (str_ids[i].equals(cloneItemVOs[j].getId())) { //如果找到
								cloneItemVOs[j].setSelected(true); //
								break; //
							}
						}
					}
				}
				list = new JList(cloneItemVOs);
			} else {
				list = new JList(); //
			}

			JPanel contentPanel = new JPanel(new BorderLayout()); //
			contentPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY)); //
			contentPanel.setBackground(Color.WHITE); //

			list.setOpaque(false); //透明
			list.setToolTipText("按住Ctrl键可以全选/全消"); //
			list.setFocusable(false); //
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //
			list.setCellRenderer(new CheckListRenderer()); //
			JScrollPane scrollPanel = new JScrollPane(list); //createMatteBorder(1, 1, 0, 1, Color.GRAY)
			//scrollPanel.setBorder(BorderFactory.createEmptyBorder()); //
			//			scrollPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, Color.GRAY)); //
			scrollPanel.setFocusable(false);
			scrollPanel.getViewport().setFocusable(false); //
			scrollPanel.setOpaque(false); //透明
			scrollPanel.getViewport().setOpaque(false); //透明

			list.addMouseListener(this); //
			contentPanel.add(scrollPanel, BorderLayout.CENTER); //

			btn_confirm = new WLTButton("确定"); //
			btn_confirm.setFocusable(false); //
			btn_confirm.addActionListener(this); //
			//btn_confirm.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY)); //

			btn_cancel = new WLTButton("取消"); //
			btn_cancel.setFocusable(false); //
			btn_cancel.addActionListener(this); //

			//			JPanel btnPanel = new JPanel(new FlowLayout()); //
			//			btnPanel.setBackground(Color.WHITE); //白色!!
			//			btnPanel.add(btn_confirm); //
			//			btnPanel.add(btn_cancel); //

			contentPanel.add(btn_confirm, BorderLayout.SOUTH); //
			this.getContentPane().add(contentPanel); //
		}

		public void mouseClicked(MouseEvent e) {
			if (list.getModel() == null || list.getModel().getSize() <= 0) {
				return;
			}
			int index = list.locationToIndex(e.getPoint()); //得到选中的是第几个
			if (index < 0) {
				return;
			}
			ComBoxItemVO clickedVO = (ComBoxItemVO) list.getModel().getElementAt(index); //取得其值
			boolean isItemSelected = clickedVO.isSelected(); //点击是否处理选中状态!!
			if (e.isControlDown()) {
				for (int i = 0; i < list.getModel().getSize(); i++) {
					ComBoxItemVO itemVO = (ComBoxItemVO) list.getModel().getElementAt(i); //
					itemVO.setSelected(!isItemSelected); //修改值
				}
			} else {
				clickedVO.setSelected(!isItemSelected); //修改值
			}

			//Rectangle rect = list.getCellBounds(index, index); //取得范围
			list.repaint(); //重绘!
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {

		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		//点击确定按钮
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btn_confirm) { //确定!!
				if (list.getModel() == null || list.getModel().getSize() <= 0) {
					return;
				}
				StringBuilder sb_ids = new StringBuilder(";"); //
				StringBuilder sb_codes = new StringBuilder(";"); //
				StringBuilder sb_names = new StringBuilder(""); //
				for (int i = 0; i < list.getModel().getSize(); i++) {
					ComBoxItemVO itemVO = (ComBoxItemVO) list.getModel().getElementAt(i); //
					if (itemVO.isSelected()) {
						sb_ids.append(itemVO.getId() + ";"); //
						sb_codes.append(itemVO.getCode() + ";"); //
						sb_names.append(itemVO.getName() + ";"); //
					}
				}
				String str_id = sb_ids.toString(); //
				String str_code = sb_codes.toString(); //
				String str_name = sb_names.toString(); //
				int li_count = getTBUtil().findCount(sb_ids.toString(), ";"); //
				if (li_count <= 2) { //如果只有两个分号,即说明只有一个值!则去掉分号! 这样可以兼容旧的模型,即有可能有的人不使用平台BillQueryPanel中的方式去取条件!而自己取条件,然后是等于匹配,这样就会查不到数据! 这柆一处理就有可能在只选一个条件的情况下仍然能取得值!!
					str_id = getTBUtil().replaceAll(str_id, ";", ""); //
					str_code = getTBUtil().replaceAll(str_code, ";", ""); //
					str_name = getTBUtil().replaceAll(str_name, ";", ""); //
				}
				ComBoxItemVO spanVOs = new ComBoxItemVO(str_id, str_code, str_name); //创建值,即合并的VO!
				setObject(spanVOs); //
				this.dispose(); //关闭!!

				for (int i = 0; i < v_listener.size(); i++) {
					ActionListener listener = (ActionListener) v_listener.get(i); //
					listener.actionPerformed(new ActionEvent(QueryCPanel_ComboBox.this, 1, "Confirm")); //
				}
			} else if (e.getSource() == btn_cancel) { //如果是取消,则直接关闭!!!
				this.dispose(); //关闭!
			}
		}

	}

	class CheckListRenderer extends JCheckBox implements ListCellRenderer {

		private static final long serialVersionUID = -2395273952467989799L;

		public CheckListRenderer() {
			setBackground(Color.WHITE);
			setForeground(Color.BLACK);
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus) {
			setEnabled(list.isEnabled());
			setSelected(((ComBoxItemVO) value).isSelected());
			setFont(list.getFont());
			setText(value.toString());
			if (isSelected) {
				setBackground(LookAndFeel.defaultShadeColor1);
			} else {
				setBackground(Color.WHITE);
			}
			return this;
		}
	}

}