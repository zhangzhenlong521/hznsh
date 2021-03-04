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

	protected String hyperlinkdesc = null; //�����Ӷ���
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
	private String str_msg = new TBUtil().getSysOptionStringValue("������ؼ�ѡ����ʾ��", "");//���ʴ���Ŀ���������ʱ���������Ҫ�и�Ĭ�ϵ���ʾ������Ӹò��������/2012-08-15��

	public CardCPanel_ComboBox(Pub_Templet_1_ItemVO _templetVO) {
		super();
		this.templetItemVO = _templetVO;
		this.key = templetItemVO.getItemkey();
		this.name = templetItemVO.getItemname();
		this.li_cardheight = _templetVO.getCardHeight().intValue();
		this.hyperlinkdesc = _templetVO.getHyperlinkdesc(); //������˵��
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
		this.hyperlinkdesc = _templetVO.getHyperlinkdesc(); //������˵��
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
			label = createLabel(templetItemVO); //���ø����ṩ�ķ�������Label
		} else {
			label = new JLabel(name); //
			label.setPreferredSize(new Dimension(li_label_width, li_cardheight)); //���ÿ��
		}

		combox = new JComboBox();
		combox.setLightWeightPopupEnabled(false); // ΪʲôҪ��ô�㣿
		combox.setEditable(true); //
		combox.setOpaque(false); //
		combox.setPreferredSize(new Dimension(li_combox_width, li_cardheight)); //
		cmb_textField = ((JTextField) ((JComponent) combox.getEditor().getEditorComponent())); //
		if(!(combox.getUI() instanceof I_ComboBoxUI)){
			combox.setUI(new WLTComBoBoxUI());
			arrowButton = ((WLTComBoBoxUI) combox.getUI()).getArrowButton(); //�Ǹ�������ť,�����ǳ�ʹ���һֱ�ò�����������
		}else{
			arrowButton = ((I_ComboBoxUI) combox.getUI()).getArrowButton(); //�Ǹ�������ť,�����ǳ�ʹ���һֱ�ò�����������
		}
	
		arrowButton.addMouseListener(this); //
		if (!"WebPushUIByHm".equalsIgnoreCase(UIManager.getLookAndFeel().getID())) { //�Զ���UI���Ѿ�ȥ��border��
			//���ñ߿�
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

		//������ɫ
		cmb_textField.setForeground(LookAndFeel.inputforecolor_enable); //
		if (!"WebPushUIByHm".equalsIgnoreCase(UIManager.getLookAndFeel().getID())) { //
			cmb_textField.setBackground(LookAndFeel.inputbgcolor_enable); //	
		}else{
			cmb_textField.setBackground(new Color(251, 251, 251)); //
		}
		

		if (templetItemVO != null && templetItemVO.getUCDfVO() != null && templetItemVO.getUCDfVO().getConfBooleanValue("�ı����Ƿ�ɱ༭", false)) { //�е�������Ҫ���ı����ǿ��Ա༭��,���ȿ�ѡ��,�ֿ�ֱ��¼��!!!
			cmb_textField.setEditable(true); //
		} else {
			cmb_textField.setEditable(false); //
		}

		combox.addItem(new ComBoxItemVO("", "", str_msg)); //��һ�����ǿ�ֵ!!
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

		//���볬����!!!,Ŀǰ�ı���,ֻ���ڿ�Ƭ�в��г����ӳ���,�б��в�����!!
		int li_hyperlinkwidth = 0;
		if (hyperlinkdesc != null) {
			WLTHyperLinkLabel[] linkLabels = this.createHyperLinkLabel(this.key, this.name, hyperlinkdesc, this.billPanel); //
			if (linkLabels != null) {
				if (linkLabels != null) { //����г�����
					for (int i = 0; i < linkLabels.length; i++) {
						li_hyperlinkwidth = li_hyperlinkwidth + (int) linkLabels[i].getPreferredSize().getWidth(); //
						this.add(linkLabels[i]); //
					}
				}
			}
		}

		li_width_all = (int) (label.getPreferredSize().getWidth() + combox.getPreferredSize().getWidth() + li_hyperlinkwidth); //�ܿ��
		this.setPreferredSize(new Dimension(li_width_all, li_cardheight)); //

		combox.addItemListener(this); //
		cmb_textField.addMouseListener(this);

		if (!"".equals(this.str_msg.trim())) {//
			cmb_textField.setText(this.str_msg);//������һ��
			cmb_textField.addFocusListener(this);//�ı������ӽ����¼���Ϊ����ʧȥ����ʱ�ж����ֵΪ�գ�����������Ĭ����ʾ����/2012-08-15��
		}
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == cmb_textField) {
			if (e.isControlDown() && e.getClickCount() == 2) { //�ǰ�סCtrl+˫��
				showTextFieldText(); //
			} else if (cmb_textField.isEditable() && this.str_msg.equals(cmb_textField.getText())) {//����ı���ɱ༭���ҵ�ǰֵΪ�գ���ʱ���һ��������ֵ�����Ƚ���ʾ����ա����/2012-08-15��
				cmb_textField.setText("");
			}
		} else if (e.getSource() == this.arrowButton) { //����ǰ�ť!
			if (e.isShiftDown() && e.getButton() == MouseEvent.BUTTON3) { //�ǰ�סShift+�Ҽ�
				StringBuilder sb_info = new StringBuilder(); //
				Object obj = getObject(); //
				if (obj == null) {
					sb_info.append("��ǰֵΪ��!!"); //
				} else {
					if (obj instanceof ComBoxItemVO) {
						sb_info.append("��ǰ����������ComBoxItemVO:\r\n"); //
						ComBoxItemVO itemVO = (ComBoxItemVO) obj; //
						sb_info.append("id=[" + itemVO.getId() + "]\r\n"); //
						sb_info.append("code=[" + itemVO.getCode() + "]\r\n"); //
						sb_info.append("name=[" + itemVO.getName() + "]\r\n"); //
						if (itemVO.getHashVO() == null) {
							sb_info.append("��ǰֵ�е�HashVOΪ��!!\r\n"); //
						} else {
							sb_info.append("********** ��ǰֵ��HashVO�е����� ***********\r\n"); //
							String[] str_keys = itemVO.getHashVO().getKeys(); //
							for (int i = 0; i < str_keys.length; i++) {
								sb_info.append(str_keys[i] + "=[" + itemVO.getHashVO().getStringValue(str_keys[i]) + "]\r\n"); //
							}
						}
					} else {
						sb_info.append("��ǰ����������:[" + obj.getClass().getName() + "]\r\n"); //
						sb_info.append("��ǰֵ��:[" + obj.toString() + "]\r\n"); //
					}
				}

				if (itemVOs == null || itemVOs.length <= 0) {
					sb_info.append("\r\n���п�ѡֵΪ��!!\r\n"); //
				} else {
					sb_info.append("\r\n*************���п�ѡֵ************\r\n"); //
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
	 * ����ֵ..
	 * @param _itemVOs
	 */
	public void pushItems(ComBoxItemVO[] _itemVOs) {
		combox.removeAllItems(); //
		this.itemVOs = _itemVOs; //
		//cmb_textField.setEditable(false); //
		combox.addItem(new ComBoxItemVO("", "", str_msg)); //��һ�����ǿ�ֵ!!
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
			return ((ComBoxItemVO) obj).getId(); //��������!!
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
			((JTextField) getComBox().getEditor().getEditorComponent()).setText(this.str_msg);//����ƽ̨���º�Ĭ��ѡ�� ��ѡ�� ����ʹ��ԭ��������/sunfujun/20121106
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
			cmb_textField.setForeground(LookAndFeel.inputforecolor_enable); //��Чʱ��ǰ��ɫ
			cmb_textField.setBackground(LookAndFeel.inputbgcolor_enable); //
			if (templetItemVO != null && templetItemVO.getUCDfVO() != null && templetItemVO.getUCDfVO().getConfBooleanValue("�ı����Ƿ�ɱ༭", false)) { //�е�������Ҫ���ı����ǿ��Ա༭��,���ȿ�ѡ��,�ֿ�ֱ��¼��!!!
				cmb_textField.setEditable(true); //
			} else {
				cmb_textField.setEditable(false); //
			}
			if ("".equals(this.str_msg.trim()) && combox.getSelectedIndex() < 1) {//����ÿؼ��ɱ༭������ѡ����ʾ����ɱ༭ʱû��Ҫ��ʾ�����/2012-08-15��
				cmb_textField.setText(this.str_msg);
			}
		} else {
			//((BasicComboBoxUI) combox.getUI()).unconfigureArrowButton(); //
			cmb_textField.setForeground(LookAndFeel.inputforecolor_disable); //��Чʱ��ǰ��ɫ
			cmb_textField.setBackground(LookAndFeel.inputbgcolor_disable); //��Чʱ�ı���ɫ
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
		//return combox.getSelectedItem(); //Ϊ����ǰ�õ���䣬����ĸ�ע���ˣ�����и�Сbug�����ı���ɱ༭���������б༭��ֱ�ӵ�ȷ���򱣴治�ܽ��ĺ��ֵ��������

		if (this.itemVOs == null && (str_text == null || str_text.trim().equals(""))) {
			return null;
		} else if (this.itemVOs == null && str_text != null) { //gaofeng ֻ��ֵ��û����������ʱ��ֱ��newһ��ComBoxItemVO����
			itemVOs = new ComBoxItemVO[] { new ComBoxItemVO(str_text, str_text, str_text) };
		}

		for (int i = 0; i < this.itemVOs.length; i++) {
			if (itemVOs[i] != null && itemVOs[i].getName() != null && itemVOs[i].getName().trim().equals(str_text.trim())) { //
				return itemVOs[i]; //
			}
		}
		if (this.str_msg.equals(str_text)) {//����ı������ı�Ϊ��ʾ��򷵻ص�ComBoxItemVO��id����Ϊnull
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
			itemVOs = new ComBoxItemVO[] { vo }; //��������Ŀ��,�����������������Ĭ��ֵ�е�id������������б���û��ƥ��ʱ,��ʱsetSelectedItem(vo)���Ҳ�����!Ȼ������ȡֵʱ,���ص�id��nameһ��! 
			//�����ȼ�����һ��!! �����˸о����ָķ�������! �Ժ�Ҫ�Ż�! Ӧ���и���ǰֵ�ĸ���! Ȼ�󷵻�ֵʱҪ�ж�����ı����ǿ��Ա༭��,�����ı���Ϊ׼!(������Խ����ѡ���������), ������ɱ༭,��ѡ��ķ���!
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

	//�ı���ʱ�����¼����ж����ֵΪ�գ�����������Ĭ����ʾ����/2012-08-15��
	public void focusLost(FocusEvent e) {
		if (combox.isEnabled() && "".equals(cmb_textField.getText().trim()) && combox.getSelectedIndex() < 1) {
			cmb_textField.setText(this.str_msg);
		}
	}

	public void focusGained(FocusEvent e) {

	}

}
