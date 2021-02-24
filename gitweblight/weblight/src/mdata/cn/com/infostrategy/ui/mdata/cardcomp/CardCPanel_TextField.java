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

	protected String hyperlinkdesc = null; //�����Ӷ���
	private StringItemVO initItemVO = null; //��ʼ����
	private BillPanel billPanel = null;

	private int li_label_width = 120;

	private int li_textfield_width = 150;

	private int li_cardheight = 20; //

	private JLabel label = null;

	private JTextField textField = null;

	private boolean isNeed = false;//��������ʲô��˼��������û�õ�����˭�ܽ���һ�£������/2012-02-24��

	private boolean isEditable = true;

	private int li_width_all;

	private StringItemVO oldItemVO = null; //ԭ��������,�����жϸ���ֵ�Ƿ����仯!

	private JPopupMenu popMenu = null; //�����˵�..
	private JMenuItem menuItem_cut, menuItem_paste;
	private CommUCDefineVO ucvo = null;

	public CardCPanel_TextField(Pub_Templet_1_ItemVO _templetVO) {
		this(_templetVO, WLTConstants.COMP_TEXTFIELD); //Ĭ���Ǵ��ı�
	}

	public CardCPanel_TextField(Pub_Templet_1_ItemVO _templetVO, int _width, int _height) {
		this(_templetVO, WLTConstants.COMP_TEXTFIELD, _width, _height); //Ĭ���Ǵ��ı�
	}

	public CardCPanel_TextField(Pub_Templet_1_ItemVO _templetVO, String _type) {
		super();
		this.type = _type; //����,���ı���,���ֿ�,�����
		this.templetItemVO = _templetVO;
		this.key = templetItemVO.getItemkey();
		this.name = templetItemVO.getItemname();
		this.hyperlinkdesc = _templetVO.getHyperlinkdesc();
		this.li_textfield_width = templetItemVO.getCardwidth().intValue(); // ���ÿ��
		this.li_cardheight = templetItemVO.getCardHeight().intValue(); //�߶�
		initialize();
	}

	public CardCPanel_TextField(Pub_Templet_1_ItemVO _templetVO, String _type, int _width, int _height) {
		super();
		this.type = _type; //����,���ı���,���ֿ�,�����
		this.templetItemVO = _templetVO;
		this.key = templetItemVO.getItemkey();
		this.name = templetItemVO.getItemname();
		this.hyperlinkdesc = _templetVO.getHyperlinkdesc();
		this.li_textfield_width = _width; //���ÿ��..
		this.li_cardheight = _height; //�߶�
		initialize();
	}

	public CardCPanel_TextField(Pub_Templet_1_ItemVO _templetVO, String _type, StringItemVO _initItemVO, BillPanel _billPanel) {
		super();
		this.type = _type; //����,���ı���,���ֿ�,�����
		this.templetItemVO = _templetVO;
		this.key = templetItemVO.getItemkey();
		this.name = templetItemVO.getItemname();
		this.hyperlinkdesc = _templetVO.getHyperlinkdesc();
		this.initItemVO = _initItemVO; //
		this.billPanel = _billPanel; //
		this.li_textfield_width = templetItemVO.getCardwidth().intValue(); //���ÿ��
		this.li_cardheight = templetItemVO.getCardHeight().intValue(); //�߶�
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
			label = createLabel(templetItemVO); //���ø����ṩ�ķ�������Label
		} else {
			label = new JLabel(name); //
			label.setHorizontalAlignment(SwingConstants.RIGHT); //
			label.setVerticalAlignment(SwingConstants.TOP); //
			label.setPreferredSize(new Dimension(li_label_width, li_cardheight)); //���ÿ��
		}

		ucvo = templetItemVO.getUCDfVO();
		if (this.type.equals(WLTConstants.COMP_TEXTFIELD)) { //����Ǵ��ı���
			textField = new JTextField(); //
			addComponentUndAndRedoFunc(textField);
			if (ucvo != null && "�Զ���У��".equals(ucvo.getConfValue("����"))) { //by haoming 2016-05-09
				textField.setDocument(new NumberFormatdocument(ucvo)); //
			}
		} else if (this.type.equals(WLTConstants.COMP_NUMBERFIELD)) { //��������ֿ�
			textField = new JFormattedTextField(); //��ʽ�������ֿ�
			textField.setHorizontalAlignment(SwingConstants.RIGHT); //
			textField.setDocument(new NumberFormatdocument(ucvo)); //�������ֿ�ֻ����������,������ĸ���ü���!!!!
		} else if (this.type.equals(WLTConstants.COMP_PASSWORDFIELD)) { //����������
			textField = new JPasswordField(); //
			textField.setHorizontalAlignment(SwingConstants.LEFT); //
		} else if (this.type.equals(WLTConstants.COMP_REGULAR)) {//�����������ʽ [zzl 2016-11-22]
			textField = new JTextField();
			textField.setHorizontalAlignment(SwingConstants.LEFT);
			textField.addCaretListener(this);
			textField.addFocusListener(this);

		}

		//textField.setOpaque(false);
		textField.setForeground(LookAndFeel.inputforecolor_enable); //��Чʱ��ǰ��ɫ
		textField.setBackground(LookAndFeel.inputbgcolor_enable); //��Чʱ�ı���ɫ

		if (!isEditable) {
			textField.setText("Sequence");
			setItemEditable(false);
		}

		popMenu = new JPopupMenu(); //
		menuItem_cut = new JMenuItem("����"); //
		JMenuItem menuItem_copy = new JMenuItem("����"); //
		menuItem_paste = new JMenuItem("ճ��"); //
		JMenuItem menuItem_selectall = new JMenuItem("ѡ��ȫ��"); //
		menuItem_cut.setPreferredSize(new Dimension(70, 19)); //
		menuItem_copy.setPreferredSize(new Dimension(70, 19)); //
		menuItem_paste.setPreferredSize(new Dimension(70, 19)); //
		menuItem_selectall.setPreferredSize(new Dimension(70, 19)); //
		popMenu.add(menuItem_cut); //����
		popMenu.add(menuItem_copy); //����
		popMenu.add(menuItem_paste); //ճ��
		popMenu.add(menuItem_selectall); //ȫѡ
		menuItem_cut.setVisible(this.isEditable);//���ɱ༭ʱ��������Ϊ�����ã������ͻ����"����"�����ã�"����"���ã�"ճ��"�����ã�"ѡ��ȫ��"���ã��о����ң����Ը�Ϊ���ɼ��������/2012-02-24��
		menuItem_paste.setVisible(this.isEditable);

		menuItem_cut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCutText(); //ѡ���ĵ�
			}
		}); //

		menuItem_copy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCopyText(); //ѡ���ĵ�
			}
		}); //

		menuItem_paste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onPasteText(); //ճ���ĵ�
			}
		}); //

		menuItem_selectall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSelectAll(); //ѡ��ȫ��
			}
		}); //

		textField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) { //������Ҽ�
					showTextAreaPopMenu((JTextField) e.getSource(), e.getX(), e.getY()); //
				}
			}
		}); //

		textField.setPreferredSize(new Dimension(li_textfield_width, li_cardheight)); //
		if (!"WebPushUIByHm".equalsIgnoreCase(UIManager.getLookAndFeel().getID())) { //�Զ���UI���Ѿ�ȥ��border��
			if (templetItemVO != null) {
				if (templetItemVO.getPub_Templet_1VO().getCardBorder().equals("LINE")) { //����ǵ���
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

		li_width_all = (int) (label.getPreferredSize().getWidth() + textField.getPreferredSize().getWidth() + li_hyperlinkwidth); //�ܿ��
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
		oldItemVO = new StringItemVO(_value); //�����ݸ���
		textField.setText(_value); //
		textField.select(0, 0); //
		this.repaint(); //�������»���;
	}

	public void reset() {
		oldItemVO = null; //
		textField.setText("");
	}

	public void setItemEditable(boolean _bo) {
		isEditable = _bo;
		textField.setEditable(isEditable);
		if (isEditable) {
			textField.setForeground(LookAndFeel.inputforecolor_enable); //��Чʱ��ǰ��ɫ
			textField.setBackground(LookAndFeel.inputbgcolor_enable); //��Чʱ�ı���ɫ
		} else {
			textField.setForeground(LookAndFeel.inputforecolor_disable); //��Чʱ��ǰ��ɫ
			textField.setBackground(LookAndFeel.inputbgcolor_disable); //��Чʱ�ı���ɫ
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
				if (_obj instanceof String) {//�����������Ĭ��ֵ����ΪString���ͣ����ж�һ�£�����ᷢ������ת���쳣�����/2016-12-17��
					oldItemVO = new StringItemVO((String) _obj);
				} else {
					oldItemVO = (StringItemVO) _obj; //
					setValue(oldItemVO.getStringValue());
				}
			} catch (ClassCastException ex) {
				ex.printStackTrace(); //
				throw new WLTAppException("ģ��[" + templetItemVO.getPub_Templet_1VO().getTempletcode() + "]����[" + templetItemVO.getItemkey() + "]������������ת������,�ܿ�������Ϊ���ع�ʽ�����޸����ı������������!!!" + ex.getMessage()); //
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
	 * �����ļ�����
	 */
	protected void onCutText() {
		StringSelection ss = new StringSelection(this.textField.getSelectedText()); //ȡ��ѡ�������
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null); //��ϵͳ������������
		try {
			textField.replaceSelection(""); //
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	/**
	 * �����ļ�����
	 */
	protected void onCopyText() {
		StringSelection ss = new StringSelection(this.textField.getSelectedText()); //ȡ��ѡ�������
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null); //��ϵͳ������������
	}

	/**
	 * ճ������
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
	 * ѡ����������
	 */
	protected void onSelectAll() {
		textField.selectAll(); //
	}

	/**
	 * ��ʾ�Ҽ��˵�.
	 * @param _textarea
	 * @param _x
	 * @param _y
	 */
	protected void showTextAreaPopMenu(JTextField _textfield, int _x, int _y) {
		this.popMenu.show(_textfield, _x, _y); //
	}

	@Override
	/**
	 * �ع�,���ı���Ϊ׼! ��stopeditingʱ��Ҫ
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
			style = ucvo.getConfValue("����");
			String str = textField.getText();
			Pattern p = null;
			Matcher m = null;
			p = Pattern.compile(style);
			m = p.matcher(str.toString());
			if (m.matches()) {
				//У��ͨ��

			} else {
				//У�鲻ͨ���� zzl��ֵ����ˡ� Ӧ�û��и��õķ���...
				textField.setText(null);
			}
		} catch (Exception e) {
			e.printStackTrace(); //
			throw new WLTAppException("������ʽ����ʧ�ܣ�");
		}
	}

}
