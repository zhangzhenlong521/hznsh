/**************************************************************************
 * $RCSfile: CardCPanel_ComboBox.java,v $  $Revision: 1.24 $  $Date: 2013/02/28 06:14:48 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTComBoBoxUI;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.WLTHyperLinkLabel;
import cn.com.infostrategy.ui.mdata.hmui.I_ComboBoxUI;

public class CardCPanel_ComboBox extends AbstractWLTCompentPanel implements ItemListener, MouseListener, FocusListener {
	private static final long serialVersionUID = 1L;

	Pub_Templet_1_ItemVO templetItemVO = null;
	private String key = null;
	private String name = null;

	protected String hyperlinkdesc = null; //超链接定义
	private BillPanel billPanel = null;
	protected ComBoxItemVO initItemVO = null;

	private ComBoxItemVO[] itemVOs = null; //
	private JLabel label = null;
	private JComboBox combox = null;
	private JTextField cmb_textField = null;
	private JButton arrowButton = null; //

	private int li_label_width = 120;
	private int li_combox_width = 150;
	private int li_cardheight = 20; //
	private int li_width_all = 0;
	private String str_msg = new TBUtil().getSysOptionStringValue("下拉框控件选择提示语", "");//因邮储项目提出在新增时下拉框必须要有个默认的提示语，故增加该参数【李春娟/2012-08-15】

	public CardCPanel_ComboBox(Pub_Templet_1_ItemVO _templetVO) {
		super();
		this.templetItemVO = _templetVO;
		this.key = templetItemVO.getItemkey();
		this.name = templetItemVO.getItemname();
		this.li_cardheight = _templetVO.getCardHeight().intValue();
		this.hyperlinkdesc = _templetVO.getHyperlinkdesc(); //超链接说明
		this.itemVOs = templetItemVO.getComBoxItemVos();
		this.li_combox_width = templetItemVO.getCardwidth().intValue(); //
		initialize();
	}

	public CardCPanel_ComboBox(Pub_Templet_1_ItemVO _templetVO, ComBoxItemVO _initItemVO, BillPanel _billPanel) {
		super();
		this.templetItemVO = _templetVO;
		this.key = templetItemVO.getItemkey();
		this.name = templetItemVO.getItemname();
		this.li_cardheight = _templetVO.getCardHeight().intValue();
		this.hyperlinkdesc = _templetVO.getHyperlinkdesc(); //超链接说明
		this.itemVOs = templetItemVO.getComBoxItemVos(); //
		this.initItemVO = _initItemVO; //
		this.billPanel = _billPanel; //
		this.billPanel = _billPanel; //
		this.li_combox_width = templetItemVO.getCardwidth().intValue(); //
		initialize();
	}

	public CardCPanel_ComboBox(String _key, String _name, String _sql) {
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

	public CardCPanel_ComboBox(String _key, String _name, ComBoxItemVO[] _itemVOs) {
		super(); //
		this.key = _key;
		this.name = _name;
		this.itemVOs = _itemVOs;
		initialize();
	}

	private void initialize() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		this.setBackground(LookAndFeel.cardbgcolor); //

		if (templetItemVO != null) {
			label = createLabel(templetItemVO); //采用父亲提供的方法创建Label
		} else {
			label = new JLabel(name); //
			label.setPreferredSize(new Dimension(li_label_width, li_cardheight)); //设置宽度
		}

		combox = new JComboBox();
		combox.setLightWeightPopupEnabled(false); // 为什么要这么搞？
		combox.setEditable(true); //
		combox.setOpaque(false); //
		combox.setPreferredSize(new Dimension(li_combox_width, li_cardheight)); //
		cmb_textField = ((JTextField) ((JComponent) combox.getEditor().getEditorComponent())); //
		if(!(combox.getUI() instanceof I_ComboBoxUI)){
			combox.setUI(new WLTComBoBoxUI());
			arrowButton = ((WLTComBoBoxUI) combox.getUI()).getArrowButton(); //那个下拉按钮,曾经非常痛苦的一直得不到这个句柄。
		}else{
			arrowButton = ((I_ComboBoxUI) combox.getUI()).getArrowButton(); //那个下拉按钮,曾经非常痛苦的一直得不到这个句柄。
		}
	
		arrowButton.addMouseListener(this); //
		if (!"WebPushUIByHm".equalsIgnoreCase(UIManager.getLookAndFeel().getID())) { //自定义UI中已经去掉border。
			//设置边框
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

		//设置颜色
		cmb_textField.setForeground(LookAndFeel.inputforecolor_enable); //
		if (!"WebPushUIByHm".equalsIgnoreCase(UIManager.getLookAndFeel().getID())) { //
			cmb_textField.setBackground(LookAndFeel.inputbgcolor_enable); //	
		}else{
			cmb_textField.setBackground(new Color(251, 251, 251)); //
		}
		

		if (templetItemVO != null && templetItemVO.getUCDfVO() != null && templetItemVO.getUCDfVO().getConfBooleanValue("文本框是否可编辑", false)) { //有的下拉框要求文本框是可以编辑的,即既可选择,又可直接录入!!!
			cmb_textField.setEditable(true); //
		} else {
			cmb_textField.setEditable(false); //
		}

		combox.addItem(new ComBoxItemVO("", "", str_msg)); //第一个总是空值!!
		if (itemVOs != null) {
			for (int i = 0; i < itemVOs.length; i++) {
				combox.addItem(itemVOs[i]);
			}
		}

		this.add(label);
		JPanel panel_tmp = new JPanel(new BorderLayout(0, 0)); //
		panel_tmp.setBorder(BorderFactory.createEmptyBorder()); //
		panel_tmp.setPreferredSize(new Dimension(li_combox_width, li_cardheight)); //
		panel_tmp.setOpaque(false); //
		panel_tmp.add(combox, BorderLayout.NORTH); //
		this.add(panel_tmp); //

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

		li_width_all = (int) (label.getPreferredSize().getWidth() + combox.getPreferredSize().getWidth() + li_hyperlinkwidth); //总宽度
		this.setPreferredSize(new Dimension(li_width_all, li_cardheight)); //

		combox.addItemListener(this); //
		cmb_textField.addMouseListener(this);

		if (!"".equals(this.str_msg.trim())) {//
			cmb_textField.setText(this.str_msg);//先设置一把
			cmb_textField.addFocusListener(this);//文本框增加焦点事件，为了在失去焦点时判断如果值为空，则重新设置默认提示语【李春娟/2012-08-15】
		}
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == cmb_textField) {
			if (e.isControlDown() && e.getClickCount() == 2) { //是按住Ctrl+双击
				showTextFieldText(); //
			} else if (cmb_textField.isEditable() && this.str_msg.equals(cmb_textField.getText())) {//如果文本框可编辑并且当前值为空，这时点击一般是输入值，故先将提示语清空【李春娟/2012-08-15】
				cmb_textField.setText("");
			}
		} else if (e.getSource() == this.arrowButton) { //如果是按钮!
			if (e.isShiftDown() && e.getButton() == MouseEvent.BUTTON3) { //是按住Shift+右键
				StringBuilder sb_info = new StringBuilder(); //
				Object obj = getObject(); //
				if (obj == null) {
					sb_info.append("当前值为空!!"); //
				} else {
					if (obj instanceof ComBoxItemVO) {
						sb_info.append("当前数据类型是ComBoxItemVO:\r\n"); //
						ComBoxItemVO itemVO = (ComBoxItemVO) obj; //
						sb_info.append("id=[" + itemVO.getId() + "]\r\n"); //
						sb_info.append("code=[" + itemVO.getCode() + "]\r\n"); //
						sb_info.append("name=[" + itemVO.getName() + "]\r\n"); //
						if (itemVO.getHashVO() == null) {
							sb_info.append("当前值中的HashVO为空!!\r\n"); //
						} else {
							sb_info.append("********** 当前值中HashVO中的数据 ***********\r\n"); //
							String[] str_keys = itemVO.getHashVO().getKeys(); //
							for (int i = 0; i < str_keys.length; i++) {
								sb_info.append(str_keys[i] + "=[" + itemVO.getHashVO().getStringValue(str_keys[i]) + "]\r\n"); //
							}
						}
					} else {
						sb_info.append("当前数据类型是:[" + obj.getClass().getName() + "]\r\n"); //
						sb_info.append("当前值是:[" + obj.toString() + "]\r\n"); //
					}
				}

				if (itemVOs == null || itemVOs.length <= 0) {
					sb_info.append("\r\n所有可选值为空!!\r\n"); //
				} else {
					sb_info.append("\r\n*************所有可选值************\r\n"); //
					for (int i = 0; i < itemVOs.length; i++) {
						sb_info.append("id=[" + itemVOs[i].getId() + "],code=[" + itemVOs[i].getCode() + "],name=[" + itemVOs[i].getName() + "]\r\n"); //
					}
				}
				MessageBox.show(this, sb_info.toString()); //
			}
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

	/**
	 * 塞入值..
	 * @param _itemVOs
	 */
	public void pushItems(ComBoxItemVO[] _itemVOs) {
		combox.removeAllItems(); //
		this.itemVOs = _itemVOs; //
		//cmb_textField.setEditable(false); //
		combox.addItem(new ComBoxItemVO("", "", str_msg)); //第一个总是空值!!
		if (itemVOs != null) {
			for (int i = 0; i < itemVOs.length; i++) {
				combox.addItem(itemVOs[i]);
			}
		}
	}

	public JLabel getLabel() {
		return label;
	}

	public JComboBox getComBox() {
		return combox;
	}

	public String getItemKey() {
		return key;
	}

	public String getItemName() {
		return name;
	}

	public String getValue() {
		Object obj = getObject(); //
		if (obj == null) {
			return null;
		}
		if (obj instanceof ComBoxItemVO) {
			return ((ComBoxItemVO) obj).getId(); //返回主键!!
		} else {
			return obj.toString();
		}
	}

	public void setValue(String _value) {
		if (itemVOs != null) {
			for (int i = 0; i < itemVOs.length; i++) {
				if (itemVOs[i].getId().equalsIgnoreCase(_value)) {
					getComBox().setSelectedIndex(i + 1);
					return;
				}
			}
		} else {
			getComBox().setSelectedIndex(0);
		}

	}

	public void reset() {
		getComBox().setSelectedIndex(-1);
		if (this.str_msg != null && !"".equals(this.str_msg)) {
			((JTextField) getComBox().getEditor().getEditorComponent()).setText(this.str_msg);//发现平台更新后默认选中 请选择 不好使了原来是这里/sunfujun/20121106
		}
	}

	public int getAllWidth() {
		return this.li_width_all;//
	}

	public void setItemEditable(boolean _bo) {
		//arrowButton.setEnabled(_bo);
		combox.setEnabled(_bo); //
		cmb_textField.setEnabled(true); //
		//cmb_textField.setEditable(_bo); //
		if (_bo) {
			//((BasicComboBoxUI) combox.getUI()).configureArrowButton(); //
			cmb_textField.setForeground(LookAndFeel.inputforecolor_enable); //有效时的前景色
			cmb_textField.setBackground(LookAndFeel.inputbgcolor_enable); //
			if (templetItemVO != null && templetItemVO.getUCDfVO() != null && templetItemVO.getUCDfVO().getConfBooleanValue("文本框是否可编辑", false)) { //有的下拉框要求文本框是可以编辑的,即既可选择,又可直接录入!!!
				cmb_textField.setEditable(true); //
			} else {
				cmb_textField.setEditable(false); //
			}
			if ("".equals(this.str_msg.trim()) && combox.getSelectedIndex() < 1) {//如果该控件可编辑才设置选择提示语，不可编辑时没必要提示【李春娟/2012-08-15】
				cmb_textField.setText(this.str_msg);
			}
		} else {
			//((BasicComboBoxUI) combox.getUI()).unconfigureArrowButton(); //
			cmb_textField.setForeground(LookAndFeel.inputforecolor_disable); //无效时的前景色
			cmb_textField.setBackground(LookAndFeel.inputbgcolor_disable); //无效时的背景色
			cmb_textField.setEditable(false); //
		}
	}

	@Override
	public boolean isItemEditable() {
		return combox.isEditable();
	}

	public void setItemVisiable(boolean _bo) {
		this.setVisible(_bo); //
	}

	public Object getObject() {
		JTextField cmb_textField = ((JTextField) ((JComponent) combox.getEditor().getEditorComponent()));
		String str_text = cmb_textField.getText(); //
		//return combox.getSelectedItem(); //为何以前用的这句，后面的给注释了，这句有个小bug，在文本框可编辑的下拉框中编辑后直接点确定或保存不能将改后的值保存下来

		if (this.itemVOs == null && (str_text == null || str_text.trim().equals(""))) {
			return null;
		} else if (this.itemVOs == null && str_text != null) { //gaofeng 只有值，没有下拉框定义时，直接new一个ComBoxItemVO返回
			itemVOs = new ComBoxItemVO[] { new ComBoxItemVO(str_text, str_text, str_text) };
		}

		for (int i = 0; i < this.itemVOs.length; i++) {
			if (itemVOs[i] != null && itemVOs[i].getName() != null && itemVOs[i].getName().trim().equals(str_text.trim())) { //
				return itemVOs[i]; //
			}
		}
		if (this.str_msg.equals(str_text)) {//如果文本框中文本为提示语，则返回的ComBoxItemVO的id必须为null
			return new ComBoxItemVO(null, null, str_text);
		}
		return new ComBoxItemVO(str_text, str_text, str_text); //
	}

	public void focus() {
		combox.requestFocus();
		combox.requestFocusInWindow();
	}

	public void setObject(Object _obj) {
		ComBoxItemVO vo = null;
		if (_obj instanceof StringItemVO) {
			vo = new ComBoxItemVO(_obj.toString(), _obj.toString(), _obj.toString());
		} else {
			vo = (ComBoxItemVO) _obj;
		}
		if (itemVOs == null) {
			itemVOs = new ComBoxItemVO[] { vo }; //中铁建项目上,耿东华发现下拉框的默认值中的id在下拉框定义的列表中没有匹配时,这时setSelectedItem(vo)是找不到的!然后就造成取值时,返回的id与name一样! 
			//所以先加上这一行!! 但个人感觉这种改法并不好! 以后还要优化! 应该有个当前值的概念! 然后返回值时要判断如果文本框是可以编辑的,则以文本框为准!(这个可以解决可选可填的问题), 如果不可编辑,则按选择的返回!
		}
		if (this.str_msg != null && !"".equals(this.str_msg)) {
			if (vo == null) {
				vo = new ComBoxItemVO("", "", this.str_msg);
			} else if ("".equals(vo.toString())) {
				vo.setName(this.str_msg);
			}
		}
		combox.setSelectedItem(vo);
	}

	public ComBoxItemVO[] getItemVOs() {
		return itemVOs;
	}

	public void setItemVOs(ComBoxItemVO[] itemVOs) {
		this.itemVOs = itemVOs;
	}

	public void itemStateChanged(ItemEvent e) {
		cmb_textField.setSelectionStart(0); //
		cmb_textField.setSelectionEnd(0); //
		String str_text = cmb_textField.getText(); //
		if (str_text == null || str_text.trim().equals("")) {
			cmb_textField.setToolTipText(null); //
		} else {
			cmb_textField.setToolTipText(str_text); //
		}
	}

	private void showTextFieldText() {
		MessageBox.show(this, cmb_textField.getText()); //
	}

	//文本框时焦点事件，判断如果值为空，则重新设置默认提示语【李春娟/2012-08-15】
	public void focusLost(FocusEvent e) {
		if (combox.isEnabled() && "".equals(cmb_textField.getText().trim()) && combox.getSelectedIndex() < 1) {
			cmb_textField.setText(this.str_msg);
		}
	}

	public void focusGained(FocusEvent e) {

	}

}
