/**************************************************************************
 * $RCSfile: CardCPanel_CheckBox.java,v $  $Revision: 1.10 $  $Date: 2012/10/08 02:22:48 $
 **************************************************************************/

package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.LookAndFeel;

public class CardCPanel_CheckBox extends AbstractWLTCompentPanel {

	private static final long serialVersionUID = 1L;

	private Pub_Templet_1_ItemVO templetItemVO = null; //
	private String key = null;
	private String name = null;

	private JLabel label = null;
	private JCheckBox checkBox = null;

	private int li_label_width = 120;
	private int li_checkbox_width = 150;
	private int li_width_all = 0; //

	private boolean isNeed = false;

	public CardCPanel_CheckBox(Pub_Templet_1_ItemVO _templetVO) {
		super();
		this.templetItemVO = _templetVO; //
		this.key = templetItemVO.getItemkey(); //
		this.name = templetItemVO.getItemname();

		this.li_checkbox_width = templetItemVO.getCardwidth().intValue();
		initialize();
	}

	public CardCPanel_CheckBox(String _key, String _name) {
		super();
		this.key = _key; //
		this.name = _name; //
		initialize();
	}

	public CardCPanel_CheckBox(String _key, String _name, boolean isNeed) {
		super();
		this.key = _key; //
		this.name = _name; //
		this.isNeed = isNeed;
		initialize();
	}

	private void initialize() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		this.setBackground(LookAndFeel.cardbgcolor); //
		this.setOpaque(false); //
		if (templetItemVO != null) {
			label = createLabel(templetItemVO); //采用父亲提供的方法创建Label
		} else {
			label = new JLabel(name); //
			label.setPreferredSize(new Dimension(li_label_width, 20)); //设置宽度
		}

		checkBox = new JCheckBox();
		checkBox.setBorderPaintedFlat(true);
		checkBox.setHorizontalAlignment(JCheckBox.LEFT);
		checkBox.setBackground(LookAndFeel.systembgcolor); //
		checkBox.setForeground(LookAndFeel.systembgcolor); //
		checkBox.setOpaque(false); //

		checkBox.setPreferredSize(new Dimension(li_checkbox_width, 20)); // 按扭的宽度与高度
		checkBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCheckBoxClicked();
			}

		});

		li_width_all = (int) (label.getPreferredSize().getWidth() + checkBox.getPreferredSize().getWidth()); //总宽度
		this.setPreferredSize(new Dimension(li_width_all, 20)); //

		boolean isCheckBoxLeft = false;
		if (templetItemVO != null && templetItemVO.getUCDfVO() != null) {
			isCheckBoxLeft = templetItemVO.getUCDfVO().getConfBooleanValue("是否勾选框在前面", false); //
		}
		if (isCheckBoxLeft) { //如果指定了某个参数,则勾选框在前面!!从控制中定义!!但还需调换一下上面的label与勾选框的宽度说明!! 【xch/2012-02-23】
			label.setHorizontalAlignment(SwingConstants.LEFT); //
			checkBox.setHorizontalAlignment(SwingConstants.RIGHT); //
			this.add(checkBox); //
			this.add(label);
		} else { ///默认界面,勾选框在后面!!
			this.add(label); //label在前
			this.add(checkBox); //
		}
	}

	public String getItemKey() {
		return key;
	}

	public JLabel getLabel() {
		return label;
	}

	public String getItemName() {
		return name;
	}

	public JCheckBox getCheckBox() {
		return checkBox;
	}

	private void onCheckBoxClicked() {
	}

	public void setValue(String _value) {
		if (_value.equals("Y")) {
			this.checkBox.setSelected(true);
		} else {
			this.checkBox.setSelected(false);
		}
	}

	public void reset() {
		checkBox.setSelected(false); //
	}

	public void setItemEditable(boolean _bo) {
		checkBox.setEnabled(_bo); //
	}

	@Override
	public boolean isItemEditable() {
		return checkBox.isEnabled();
	}

	public void setItemVisiable(boolean _bo) {
		this.setVisible(_bo); //
	}

	public String getValue() {
		if (checkBox.isSelected())
			return "Y";
		else
			return "N";
	}

	public Object getObject() {
		return new StringItemVO(getValue()); //
	}

	public void setObject(Object _obj) {
		if (_obj != null) {
			StringItemVO value = new StringItemVO(_obj.toString()); //
			if (value != null && value.getStringValue().equalsIgnoreCase("Y")) {
				this.checkBox.setSelected(true);
			} else {
				this.checkBox.setSelected(false);
			}
		}
	}

	public void focus() {
		checkBox.requestFocus();
		checkBox.requestFocusInWindow();
	}

	public int getAllWidth() {
		return this.li_width_all;//
	}

}
/*******************************************************************************
 * $RCSfile: CardCPanel_CheckBox.java,v $ $Revision: 1.10 $ $Date: 2007/01/30
 * 05:14:30 $
 * 
 * $Log: CardCPanel_CheckBox.java,v $
 * Revision 1.10  2012/10/08 02:22:48  xch123
 * *** empty log message ***
 *
 * Revision 1.9  2012/09/14 09:22:57  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:40:59  Administrator
 * *** empty log message ***
 *
 * Revision 1.8  2012/02/23 07:55:48  xch123
 * *** empty log message ***
 *
 * Revision 1.7  2012/02/23 07:50:24  xch123
 * *** empty log message ***
 *
 * Revision 1.6  2012/02/23 07:48:47  xch123
 * *** empty log message ***
 *
 * Revision 1.5  2011/10/10 06:31:46  wanggang
 * restore
 *
 * Revision 1.3  2011/05/05 06:38:43  xch123
 * *** empty log message ***
 *
 * Revision 1.2  2011/02/18 10:37:30  xch123
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/17 10:23:14  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:32:04  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:14  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.4  2010/02/08 11:02:00  sunfujun
 * *** empty log message ***
 *
 * Revision 1.2  2009/12/02 08:43:22  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:58  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2009/06/17 04:37:23  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:45  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:31  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:22  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2008/06/18 09:44:02  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/02/28 13:13:37  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:20  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:29  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/11/01 02:04:24  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:41  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:23  xch
 * *** empty log message ***
 *
 * Revision 1.4  2007/03/28 09:51:09  shxch
 * *** empty log message ***
 *
 * Revision 1.3  2007/03/09 01:49:36  sunxf
 * *** empty log message ***
 * Revision 1.2 2007/01/30 05:14:30 lujian ***
 * empty log message ***
 * 
 * 
 ******************************************************************************/
