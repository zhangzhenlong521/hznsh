package cn.com.infostrategy.ui.workflow.design;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

/**
 * 工作流中参与部门公式定义的参照!!! 很重要,新的机制可以放弃动态参与者,而是直接通过机构与角色搞定!!
 * 而机构则又尤其重要,它存在各种复杂变化!!! 特别是根据机构类型进行各种计算!!!
 * @author xch
 *
 */
public class ParticipanCorpDefineRefDialog extends AbstractRefDialog implements ActionListener {

	private static final long serialVersionUID = 751089599977539836L;

	private WLTButton btn_1, btn_2, btn_3, btn_4, btn_5; //
	private WLTButton btn_reset; //清空
	protected WLTButton btn_confirm, btn_cancel, btn_wrap;
	private JTextArea textArea_1, textArea_help = null;
	private String str_text = null; //

	public ParticipanCorpDefineRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
		super(_parent, _title, refItemVO, panel);
	}

	@Override
	public RefItemVO getReturnRefItemVO() {
		return new RefItemVO(str_text, null, str_text);
	}

	@Override
	public void initialize() {
		textArea_1 = new JTextArea(); //
		textArea_1.setFont(LookAndFeel.font); //
		textArea_1.setLineWrap(true); //
		if (getInitRefItemVO() != null) {
			textArea_1.setText(this.getInitRefItemVO().getId()); //
		}

		WLTSplitPane splitPanel = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, new JScrollPane(textArea_1), new JScrollPane(getHelpTextArea())); //
		splitPanel.setDividerLocation(100); //
		this.getContentPane().add(splitPanel); //
		this.getContentPane().add(getNorthBtnPanel(), BorderLayout.NORTH); //按钮面板
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	private JPanel getNorthBtnPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
		btn_1 = new WLTButton("范例1"); //
		btn_2 = new WLTButton("范例2"); //
		btn_3 = new WLTButton("范例3"); //
		btn_4 = new WLTButton("范例4"); //
		btn_5 = new WLTButton("范例5"); //
		btn_reset = new WLTButton("清空"); //

		btn_1.setToolTipText("找总行的法律合规部"); //
		btn_2.setToolTipText("找创建人的所有机构范围"); //
		btn_3.setToolTipText("找创建人的一级分行下的特定扩展类型(法规部)"); //
		btn_4.setToolTipText("找创建人的一级分行下的特定机构类型交集特定扩展类型"); //
		btn_5.setToolTipText("根据上溯条件找到指定机构,再根据下探的条件进行下探"); //

		btn_1.addActionListener(this); //
		btn_2.addActionListener(this); //
		btn_3.addActionListener(this); //
		btn_4.addActionListener(this); //
		btn_5.addActionListener(this); //
		btn_reset.addActionListener(this); //

		panel.add(btn_1); //
		panel.add(btn_2); //
		panel.add(btn_3); //
		panel.add(btn_4); //
		panel.add(btn_5); //
		panel.add(btn_reset); //

		return panel; //
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");
		btn_wrap = new WLTButton("自动换行"); //

		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		btn_wrap.addActionListener(this); //

		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		panel.add(btn_wrap); //

		return panel;
	}

	private JTextArea getHelpTextArea() {
		textArea_help = new JTextArea(); //
		textArea_help.setFont(LookAndFeel.font); //
		textArea_help.setEditable(false); //
		textArea_help.setLineWrap(false); //
		textArea_help.setBackground(new Color(240, 240, 240)); //
		textArea_help.append("getWFCorp(\"type=某种类型的机构\",\"类型=总行\",\"二次下探的子机构类型=总行部门\",\"二次下探的子机构扩展类型=总行法规部;\",\"二次下探是否包含子孙=Y\");  //直接根据机构类型找出其下的所有子结构\r\n"); //

		textArea_help.append("\r\n**************************根据流程创建人计算(优先采用)********************************\r\n"); //
		textArea_help.append("getWFCorp(\"type=流程创建人所在机构\",\"二次下探的子机构类型=一级分行部门\",\"二次下探的子机构扩展类型=一级分行合规部;一级分行法律部;\",\"二次下探是否包含子孙=Y\");  //找出与创建人的同一机构的所有子机构\r\n\r\n"); //
		textArea_help
				.append("getWFCorp(\"type=流程创建人所在机构的范围\",\"二次下探的子机构类型=一级分行部门\",\"二次下探的子机构扩展类型=一级分行合规部;一级分行法律部;\",\"二次下探是否包含子孙=Y\");     //找出与创建人的机构类型相同的第一个上级部门下的所有子机构,如果创建人所在部门是\"**下属机构\"则还自动截掉\"下属机构\",比如当创建人是\"一级分行部门\"时或\"一级分行部门下属机构\"时都将找到其父亲机构链中第一个类型为\"一级分行部门\"的机构,之所以叫[机构的范围],是类似于一个国家的领海范围一样,即与本人类型相同的都属于我的范围之内!\r\n\r\n"); //
		textArea_help.append("getWFCorp(\"type=流程创建人某类型的上级机构\",\"首次上溯到的根机构=一级分行\",\"二次下探的子机构类型=一级分行部门\");\r\n\r\n"); //
		textArea_help.append("getWFCorp(\"type=流程创建人某类型的上级机构\",\"首次上溯到的根机构=*=>一级分行\",\"二次下探的子机构扩展类型=一级分行法规部\"); \r\n\r\n"); //
		textArea_help.append("getWFCorp(\"type=流程创建人某类型的上级机构\",\"首次上溯到的根机构=一级支行部门/一级支行=>一级支行;一级分行部门=>一级分行\",\"二次下探的子机构类型=一级分行=>一级分行部门;二级分行=>二级分行部门/二级分行\");\r\n\r\n"); //
		textArea_help.append("getWFCorp(\"type=流程创建人某类型的上级机构\",\"首次上溯到的根机构=一级分行部门/一级支行=>一级分行;二级分行部门/二级支行=>二级分行\",\"二次下探的子机构类型=一级分行部门\",\"二次下探的子机构扩展类型=一级分行合规部;一级分行法律部;\",\"二次下探是否包含子孙=Y\"); \r\n\r\n"); //

		textArea_help.append("\r\n**************************根据登录人计算********************************\r\n"); //
		textArea_help.append("getWFCorp(\"type=登录人所在机构\",\"二次下探的子机构类型=一级分行部门\",\"二次下探的子机构扩展类型=一级分行合规部;一级分行法律部;\",\"二次下探是否包含子孙=Y\"); //找出与登录人的同一机构的所有子机构\r\n\r\n"); //
		textArea_help
				.append("getWFCorp(\"type=登录人所在机构的范围\",\"二次下探的子机构类型=一级分行部门\",\"二次下探的子机构扩展类型=一级分行合规部;一级分行法律部;\",\"二次下探是否包含子孙=Y\");  //找出与登录人的机构类型相同的第一个上级部门下的所有子机构,如果创建人所在部门是\"**下属机构\"则还自动截掉\"下属机构\",比如当创建人是\"一级分行部门\"时或\"一级分行部门下属机构\"时都将找到其父亲机构链中第一个类型为\"一级分行部门\"的机构,之所以叫[机构的范围],是类似于一个国家的领海范围一样,即与本人类型相同的都属于我的范围之内!\r\n\r\n"); //
		textArea_help.append("getWFCorp(\"type=登录人某类型的上级机构\",\"首次上溯到的根机构=一级分行部门/一级支行=>一级分行;二级分行部门/二级支行=>二级分行\",\"二次下探的子机构类型=一级分行部门\",\"二次下探的子机构扩展类型=一级分行合规部;一级分行法律部;\",\"二次下探是否包含子孙=Y\");\r\n\r\n"); ////

		textArea_help.append("\r\n备注:\r\n"); //
		textArea_help.append("1.最常用配置方案主要有三种情况:\r\n"); //
		textArea_help.append("  第一种是直接采用创建人所在机构(即部门内部流转)\r\n");
		textArea_help.append("  第二种是取创建人某种类型上级机构范围下的某种扩展类型的机构(比如其所在一级分行下的法规部),这常常适用于到了分行层面,要给同一分行下的另一个部门(比如法规部)时\r\n");
		textArea_help.append("  第三种是直接取某机构类型下的某种扩展类型的机构(比如总行下的法规部),即与人无关,直接根据机构类型判断!这常常适用于到了总行层面!\r\n"); //
		textArea_help.append("2.尽可能使用创建人进行计算,而不使用登录人计算,因为有时一个环节有多条来源的连线,而每根连线的机构又各不一样,所以不同机构上的待办人登录处理时,如果根据登录人计算,就需要有较复杂的逻辑判断,而根据创建人计算则没有这个麻烦:\r\n"); //
		textArea_help.setSelectionStart(0); //
		textArea_help.setSelectionEnd(0); //
		return textArea_help; //
	}

	/**
	 * 确定
	 */
	private void onConfirm() {
		this.setCloseType(1); //
		str_text = textArea_1.getText().trim(); //
		if (!str_text.equals("")) {
			if (!str_text.startsWith("getWFCorp(")) {
				MessageBox.show(this, "公式没有以【getWFCorp(】开头,这是不对的配置,请重新配置!!"); ////
				return; //
			}
		}

		//TBUtil tbUtil = new TBUtil();
		//str_text = tbUtil.replaceAll(str_text, " ", ""); //替换掉空格
		//str_text = tbUtil.replaceAll(str_text, "\r", ""); //去掉换行符
		//str_text = tbUtil.replaceAll(str_text, "\n", ""); //
		this.dispose(); //
	}

	/**
	 * 取消
	 */
	private void onCanCel() {
		this.setCloseType(2); //
		str_text = null;
		this.dispose(); //
	}

	private void onWrap() {
		if (btn_wrap.getText().endsWith("自动换行")) {
			textArea_help.setLineWrap(true); //
			btn_wrap.setText("取消换行"); //
		} else if (btn_wrap.getText().endsWith("取消换行")) {
			textArea_help.setLineWrap(false); //
			btn_wrap.setText("自动换行"); //
		}
	}

	@Override
	public int getInitHeight() {
		return 480; //
	}

	@Override
	public int getInitWidth() {
		return 800; //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			onConfirm(); //
		} else if (e.getSource() == btn_cancel) {
			onCanCel(); //
		} else if (e.getSource() == btn_wrap) {
			onWrap(); //
		} else if (e.getSource() == btn_1) {
			textArea_1.setText("getWFCorp(\"type=某种类型的机构\",\"类型=总行\",\"二次下探的子机构扩展类型=法律合规部\");"); //找总行的法规部
		} else if (e.getSource() == btn_2) {
			textArea_1.setText("getWFCorp(\"type=流程创建人所在机构的范围\");"); //创建人的所在机构!!
		} else if (e.getSource() == btn_3) {
			textArea_1.setText("getWFCorp(\"type=流程创建人某类型的上级机构\",\"首次上溯到的根机构=一级分行\",\"二次下探的子机构扩展类型=一级分行法律合规部\");"); //
		} else if (e.getSource() == btn_4) {
			textArea_1.setText("getWFCorp(\"type=流程创建人某类型的上级机构\",\r\n\"首次上溯到的根机构=一级分行\",\r\n\"二次下探的子机构类型=一级分行部门\",\r\n\"二次下探的子机构扩展类型=法律合规部\"\r\n);"); //
		} else if (e.getSource() == btn_5) {
			textArea_1.setText("getWFCorp(\"type=流程创建人某类型的上级机构\",\r\n\"首次上溯到的根机构=一级分行部门/一级分行/一级支行=>一级分行;二级分行部门/二级分行/二级支行=>二级分行\",\r\n\"二次下探的子机构类型=一级分行=>一级分行部门;二级分行=>二级分行部门\",\r\n\"二次下探的子机构扩展类型=法律合规部\",\r\n\"二次下探的子机构名称=$本部门\"\r\n);"); //
		} else if (e.getSource() == btn_reset) {
			textArea_1.setText(""); //
		}
	}

}
