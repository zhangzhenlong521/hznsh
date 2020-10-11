/**************************************************************************
 * $RCSfile: QueryCPanel_CheckBox.java,v $  $Revision: 1.8 $  $Date: 2012/11/02 08:33:37 $
 **************************************************************************/

package cn.com.infostrategy.ui.mdata.querycomp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractWLTCompentPanel;

/**
 * 查询框的勾选控件,需要转换成下拉框..
 * @author xch
 *
 */
public class QueryCPanel_CheckBox extends AbstractWLTCompentPanel {

	private static final long serialVersionUID = 1L;
	Pub_Templet_1_ItemVO templetItemVO = null; //

	private String key = null;
	private String name = null;

	private JLabel label = null;
	private JCheckBox checkBox = null; //勾选框

	private int li_label_width = 120;
	private int li_checkbox_width = 138;
	private int li_cardheight = 20;

	private boolean isNeed = false;

	Point origin = new Point();

	private int li_width_all = 0; //

	public QueryCPanel_CheckBox(Pub_Templet_1_ItemVO _templetVO) {
		super();
		this.templetItemVO = _templetVO; //
		this.key = templetItemVO.getItemkey(); //
		this.name = templetItemVO.getItemname();
		if (templetItemVO.getQuerylabelwidth() != null) {
			this.li_label_width = templetItemVO.getQuerylabelwidth().intValue(); //文字说明..
		}
		if (templetItemVO.getQuerycompentwidth() != null) {
			this.li_checkbox_width = templetItemVO.getQuerycompentwidth().intValue(); // 设置宽度
		}
		if (templetItemVO.getQuerycompentheight() != null) {
			this.li_cardheight = templetItemVO.getQuerycompentheight().intValue(); // 设置高度
		}
		initialize();
	}

	public QueryCPanel_CheckBox(String _key, String _name) {
		super();
		this.key = _key; //
		this.name = _name; //
		initialize();
	}

	public QueryCPanel_CheckBox(String _key, String _name, boolean isNeed) {
		super();
		this.key = _key; //
		this.name = _name; //
		this.isNeed = isNeed;
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
		}
		label.setPreferredSize(new Dimension(li_label_width, li_cardheight)); //设置宽度
		label.setBackground(LookAndFeel.systembgcolor);
		checkBox = new JCheckBox(); //
		checkBox.setOpaque(false); //
		checkBox.setPreferredSize(new Dimension(li_checkbox_width, li_cardheight)); // 按扭的宽度与高度
		li_width_all = (int) (label.getPreferredSize().getWidth() + checkBox.getPreferredSize().getWidth()); //总宽度
		this.setPreferredSize(new Dimension(li_width_all, li_cardheight)); //
		this.add(label);
		this.add(checkBox); //
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

	public JCheckBox getComBox() {
		return checkBox;
	}

	public void setValue(String _value) {
		if (_value.equals("Y")) {

		} else {

		}
	}

	public void reset() {
		try {
			getComBox().setSelected(false); //
		} catch (Exception ex) {
		}
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
		StringItemVO strvo = (StringItemVO) getObject(); //
		if (strvo == null) {
			return null;
		}
		return strvo.getStringValue(); //
	}

	public Object getObject() {
		return new StringItemVO(checkBox.isSelected() ? "Y" : "N"); //
	}

	public void setObject(Object _obj) {
		StringItemVO vo = (StringItemVO) _obj;
		if (vo.getStringValue() != null && vo.getStringValue().equals("Y")) {
			checkBox.setSelected(true);
		} else {
			checkBox.setSelected(false);
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
