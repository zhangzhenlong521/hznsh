/**************************************************************************
 * $RCSfile: CardCPanel_Label.java,v $  $Revision: 1.5 $  $Date: 2012/10/08 02:22:48 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;

public class CardCPanel_Label extends AbstractWLTCompentPanel {

	private static final long serialVersionUID = -6967496356909878013L;

	Pub_Templet_1_ItemVO templetItemVO = null;

	private String key = null;

	private String name = null;

	private int li_label_width = 120;

	private int li_cardheight = 20; //
	private int li_textfield_width = 150;

	private JLabel label = null;

	private JLabel textLabel = null;

	private int li_width_all;

	public CardCPanel_Label(Pub_Templet_1_ItemVO _templetVO) {
		super();
		this.templetItemVO = _templetVO;
		this.key = templetItemVO.getItemkey();
		this.name = templetItemVO.getItemname();
		this.li_cardheight = templetItemVO.getCardHeight().intValue(); //
		this.li_textfield_width = templetItemVO.getCardwidth().intValue(); // 设置宽度
		initialize();

	}

	public CardCPanel_Label(String _key, String _name) {
		super();
		this.key = _key;
		this.name = _name;
		initialize();
	}

	public CardCPanel_Label(String _key, String _name, int _width) {
		super();
		this.key = _key;
		this.name = _name;
		this.li_textfield_width = _width;
		initialize();
	}

	public CardCPanel_Label(String _key, String _name, int _width, boolean isNeed) {
		super(); //
		this.key = _key;
		this.name = _name;
		this.li_textfield_width = _width;
		initialize();
	}

	private void initialize() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0)); //
		this.setOpaque(false); //--isOpaque//this.setBackground(LookAndFeel.cardbgcolor); //
		if (templetItemVO != null) {
			label = createLabel(templetItemVO); //采用父亲提供的方法创建Label
		} else {
			label = new JLabel(name); //
			label.setPreferredSize(new Dimension(li_label_width, li_cardheight)); //设置宽度
		}

		textLabel = new JLabel();
		textLabel.setPreferredSize(new Dimension(li_textfield_width, li_cardheight));

		li_width_all = (int) (label.getPreferredSize().getWidth() + textLabel.getPreferredSize().getWidth()); //总宽度
		this.setPreferredSize(new Dimension(li_width_all, li_cardheight)); //
		this.add(label); //
		this.add(textLabel);
	}

	public JLabel getLabel() {
		return label;
	}

	public JLabel getTextLabel() {
		return textLabel;
	}

	public String getItemKey() {
		return key;
	}

	public String getItemName() {
		return name;
	}

	public String getValue() {
		return textLabel.getText();
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return textLabel.getText();
	}

	public void setText(String _text) {
		textLabel.setText(_text);
	}

	public void setValue(String _value) {
		if (_value == null) {
			textLabel.setIcon(null);
			textLabel.setText(null);
		} else {
			if (_value.toLowerCase().endsWith(".gif") || _value.toLowerCase().endsWith(".jpg")) {
				textLabel.setIcon(UIUtil.getImage(_value));
				textLabel.setText(null);
			} else {
				textLabel.setIcon(null);
				textLabel.setText(_value);
			}
		}
	}

	public void reset() {
		textLabel.setText("");
	}

	public void setItemEditable(boolean _bo) {
	}

	@Override
	public boolean isItemEditable() {
		return false;
	}

	public void setItemVisiable(boolean _bo) {
		this.setVisible(_bo); //
	}

	public Object getObject() {
		return getValue();
	}

	public void setObject(Object _obj) {
		if (_obj != null)
			setValue(_obj.toString());
	}

	public void focus() {
		textLabel.requestFocus(); //
		textLabel.requestFocusInWindow();
	}

	public int getAllWidth() {
		return li_width_all;
	}

	private boolean isEnglish() {
		if (ClientEnvironment.getInstance().getDefaultLanguageType().equals(WLTConstants.ENGLISH)) {
			return true;
		} else {
			return false;
		}
	}

}

/**************************************************************************
 * $RCSfile: CardCPanel_Label.java,v $  $Revision: 1.5 $  $Date: 2012/10/08 02:22:48 $
 *
 * $Log: CardCPanel_Label.java,v $
 * Revision 1.5  2012/10/08 02:22:48  xch123
 * *** empty log message ***
 *
 * Revision 1.4  2012/09/14 09:22:57  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:40:59  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:46  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:23:15  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:32:04  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:14  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:02:00  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:58  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2009/06/22 09:43:35  chendu
 * *** empty log message ***
 *
 * Revision 1.2  2009/06/17 04:37:23  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:45  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2009/02/19 07:30:59  wangjian
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:31  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:22  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:20  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:30  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/22 12:11:43  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/09/21 07:45:12  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:41  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:24  xch
 * *** empty log message ***
 *
 * Revision 1.5  2007/03/28 09:22:09  shxch
 * *** empty log message ***
 *
 * Revision 1.4  2007/03/09 01:49:36  sunxf
 * *** empty log message ***
 *
 * Revision 1.3  2007/03/08 09:52:00  sunxf
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 05:14:31  lujian
 * *** empty log message ***
 *
 *
 **************************************************************************/
