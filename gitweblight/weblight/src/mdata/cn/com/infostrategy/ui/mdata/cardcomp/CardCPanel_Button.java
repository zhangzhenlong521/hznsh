/**************************************************************************
 * $RCSfile: CardCPanel_Button.java,v $  $Revision: 1.8 $  $Date: 2012/10/08 02:22:48 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import cn.com.infostrategy.to.mdata.ButtonDefineVO;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;

/**
 * 卡片中按钮控件!!!按钮发现还是需要的!!
 * @author xch
 *
 */
public class CardCPanel_Button extends AbstractWLTCompentPanel {

	private static final long serialVersionUID = 1523290372757996226L;

	private Pub_Templet_1_ItemVO templetItemVO = null; //
	private String key = null;
	private String name = null;
	private String refdesc = null; //

	private JLabel label = null;
	private JButton btn = null; //
	private BillPanel billPanel = null;

	private int li_label_width = 120;
	private int li_cardheight = 20; //
	private int li_btnwidth = 75; //

	public CardCPanel_Button(Pub_Templet_1_ItemVO _templetVO) {
		super();
		this.templetItemVO = _templetVO;
		this.key = templetItemVO.getItemkey();
		this.name = templetItemVO.getItemname();
		this.refdesc = templetItemVO.getRefdesc(); //参照说明
		this.li_label_width = templetItemVO.getLabelwidth(); //li_label_width = templetItemVO.getLabelwidth();  //
		this.li_btnwidth = templetItemVO.getCardwidth().intValue(); // 设置宽度
		this.li_cardheight = templetItemVO.getCardHeight().intValue(); //高度  
		initialize();

	}

	public CardCPanel_Button(Pub_Templet_1_ItemVO _templetVO, StringItemVO _initItemVO, BillPanel _billPanel) {
		super();
		this.templetItemVO = _templetVO;
		this.key = templetItemVO.getItemkey();
		this.name = templetItemVO.getItemname();
		this.refdesc = templetItemVO.getRefdesc(); //参照说明
		this.billPanel = _billPanel; //
		this.li_label_width = templetItemVO.getLabelwidth(); //
		this.li_btnwidth = templetItemVO.getCardwidth().intValue(); // 设置宽度
		this.li_cardheight = templetItemVO.getCardHeight().intValue(); //高度  
		initialize();

	}

	public CardCPanel_Button(String key, String name) {
		super();
		this.key = key;
		this.name = name;
		initialize();
	}

	public CardCPanel_Button(String key, String name, int r, int c) {
		super();
		this.key = key;
		this.name = name;

		initialize();
	}

	public void initialize() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0)); //
		this.setBackground(LookAndFeel.cardbgcolor); //
		if (templetItemVO != null) {
			label = createLabel(templetItemVO); //采用父亲提供的方法创建Label
		} else {
			label = new JLabel(name); //
			label.setHorizontalAlignment(SwingConstants.RIGHT); //
			label.setVerticalAlignment(SwingConstants.TOP); //
			label.setPreferredSize(new Dimension(li_label_width, li_cardheight)); //设置宽度
		}

		label.setText(""); //按钮的前面的Label的字不要
		ButtonDefineVO btnVO = new ButtonDefineVO(this.getItemKey());
		btnVO.setBtntext(name); //
		CommUCDefineVO ucvo = templetItemVO.getUCDfVO();
		if (ucvo != null && ucvo.getConfValue("显示文字") != null) {
			btnVO.setBtntext(ucvo.getConfValue("显示文字", ""));
		}

		if (ucvo != null && !"".equals(ucvo.getConfValue("显示图片", ""))) {
			btnVO.setBtnimg(ucvo.getConfValue("显示图片", ""));
		}

		if (ucvo != null && !"".equals(ucvo.getConfValue("提示", ""))) {
			btnVO.setBtntooltiptext(ucvo.getConfValue("提示", ""));
		}
		btn = new WLTButton(btnVO); //创建按钮
		//btn.setBackground(LookAndFeel.systembgcolor); //设置背景颜色,要去掉,否则效果与其他普通按钮效果不一致,没有质感!!!
		btn.setPreferredSize(new Dimension(li_btnwidth, li_cardheight)); //设置宽度与高度!!
		if (ClientEnvironment.isAdmin()) {//sunfujun/20120817/不知为何这样
			btn.setToolTipText("<html>弹出列表=>LAW_LAW_CODE1<br>弹出列表=>LAW_LAW_CODE1;导入数据成功!<br>弹出消息=>导入数据成功!<br></html>");//新增按钮三种定义【李春娟/2012-05-11】
		}
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onButtonClick((JButton) e.getSource()); //
			}
		}); //
		this.add(label); //
		this.add(btn); //
		this.setPreferredSize(new Dimension(li_label_width + li_btnwidth, li_cardheight)); //
	}

	/**
	 * 点击按钮...
	 * @param _button
	 */
	protected void onButtonClick(JButton _button) {
		try {
			String str_actionname = null;
			if (templetItemVO.getUCDfVO() != null) {
				if (!"".equals(templetItemVO.getUCDfVO().getConfValue("点击事件", ""))) {
					str_actionname = templetItemVO.getUCDfVO().getConfValue("点击事件", "");
				}
			} else {
				str_actionname = this.refdesc;
			}
			if (str_actionname != null) {
				if (str_actionname.startsWith("弹出列表=>")) {//逻辑是弹出一个列表窗口，选择一项记录，点确定后可显示提示信息，主要是为了系统Demo中给客户快速配置进行演示【李春娟/2012-05-11】
					String sub1 = str_actionname.substring(6);
					String sub2 = "";
					if (sub1.contains(";")) {
						sub2 = sub1.substring(sub1.indexOf(";") + 1);
						sub1 = sub1.substring(0, sub1.indexOf(";"));
					}
					BillDialog billdialog = new BillListDialog(this, "请选择", sub1);
					billdialog.setVisible(true);
					if (billdialog.getCloseType() == 1 && !"".equals(sub2)) {
						MessageBox.show(this, sub2);
					}
				} else if (str_actionname.startsWith("弹出消息=>")) {//逻辑是直接显示提示信息，主要是为了系统Demo中给客户快速配置进行演示【李春娟/2012-05-11】
					MessageBox.show(this, str_actionname.substring(6));
				} else {
					str_actionname = str_actionname.replaceAll("\"", "");
					WLTActionListener actionListener = (WLTActionListener) Class.forName(str_actionname).newInstance(); //
					WLTActionEvent event = new WLTActionEvent(_button, billPanel, templetItemVO.getItemkey()); //
					actionListener.actionPerformed(event); //执行按钮动作!!
				}
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	public String getDataType() {
		return null;
	}

	public String getItemKey() {
		return key;
	}

	public String getItemName() {
		return name;
	}

	public String getValue() {
		return btn.getText();
	}

	public void setText(String str) {
		this.btn.setText(str);
	}

	public void setValue(String _value) {
		btn.setText(_value);
	}

	public void reset() {
		//btn.setText(""); //
	}

	public void setItemEditable(boolean _bo) {
		btn.setEnabled(_bo); //
	}

	public void setItemVisiable(boolean _bo) {
		this.setVisible(_bo); //
	}

	@Override
	public boolean isItemEditable() {
		return btn.isEnabled();
	}

	public Object getObject() {
		return new StringItemVO(getValue());
	}

	public void setObject(Object _obj) {
		if (_obj != null) {
			StringItemVO itemVO = (StringItemVO) _obj;
			setValue(itemVO.getStringValue()); //
		}
	}

	public JLabel getLabel() {
		return null; //按钮没有描述!!
	}

	public void focus() {
		btn.requestFocus();
		btn.requestFocusInWindow();
	}

	public int getAllWidth() {
		return this.li_btnwidth; //
	}

	public JButton getButtontn() {
		return btn;
	}

}
