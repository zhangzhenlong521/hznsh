package cn.com.infostrategy.ui.mdata.querycomp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

/**
 * 一种通过将注册面板快速转换成一个自定义参照的基类!!
 * 即一般一个自定义参照都可以通过一个注册面板生成主页面,然后在下面增加一个确定与取消按扭从而形成一个参照!!!
 * @author xch
 *
 */
public class RefDialog_QueryTableModel extends AbstractRefDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	private WLTButton btn_confirm, btn_cancel;

	private RefItemVO returnRefItemVO = null; //
	private BillListPanel billListPanel = null;

	private CommUCDefineVO dfvo = null; //
	private int refStyleType = 0; //默认单选

	public RefDialog_QueryTableModel(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel, CommUCDefineVO _dfvo) {
		super(_parent, _title, refItemVO, panel);
		dfvo = _dfvo;
	}

	@Override
	public RefItemVO getReturnRefItemVO() {
		return returnRefItemVO;
	}

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout()); //
		billListPanel = new BillListPanel(dfvo.getConfValue("模板编码")); //通过注册码生成一个格式面板
		billListPanel.setAllBillListBtnVisiable(false); //
		billListPanel.setQuickQueryPanelVisiable(!dfvo.getConfBooleanValue("查询面板隐藏", false));//sunfujun/20120817/bug
		billListPanel.setItemEditable(false); //
		billListPanel.setDataFilterCustCondition(dfvo.getConfValue("附加SQL条件")); //增加使用定义的SQL条件和自动查询功能，以前在定义中配置了，但没写逻辑!【李春娟/2012-06-27】
		if (dfvo.getConfBooleanValue("自动查询", false)) {
			billListPanel.QueryDataByCondition(null);
		}
		this.add(billListPanel, BorderLayout.CENTER); //
		this.add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");
		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) { //如果是确定则返回数据
			String str_id_field = dfvo.getConfValue("ID字段");
			String str_name_field = dfvo.getConfValue("NAME字段"); //
			if (!dfvo.getConfBooleanValue("可以多选", false)) { //如果是单选!!
				BillVO billvo = billListPanel.getSelectedBillVO();
				if (billvo == null) { ////
					MessageBox.showSelectOne(this); ////
					return; //
				}

				HashVO hvo = convertHashVO(billvo); //
				returnRefItemVO = new RefItemVO(hvo); //
				if (str_id_field != null) { //如果定义了id返回来源
					if (billvo.containsKey(dfvo.getConfValue("ID字段"))) {
						returnRefItemVO.setId(billvo.getStringValue(str_id_field)); //
					} else {
						MessageBox.show(this, "公式定义中指定从【" + str_id_field + "】字段返回参照ID,但模板中没有该项！"); //
						return; //
					}
				}

				if (str_name_field != null) { //如果定义了id返回来源
					if (billvo.containsKey(str_name_field)) {
						returnRefItemVO.setName(billvo.getStringValue(str_name_field)); //
					} else {
						MessageBox.show(this, "公式定义中指定从【" + str_name_field + "】字段返回参照Name,但模板中没有该项！"); //
						return; //
					}
				}
			} else {
				BillVO[] billVOs = billListPanel.getSelectedBillVOs(); //
				if (billVOs == null || billVOs.length == 0) {
					MessageBox.show(this, "必须选择一条数据!"); //
					return; //
				}
				StringBuffer sb_ids = new StringBuffer(); //
				StringBuffer sb_names = new StringBuffer(); //
				String[] str_keys = billVOs[0].getKeys(); //
				for (int i = 0; i < billVOs.length; i++) {
					if (str_id_field != null) { //如果定义了id返回来源
						if (billVOs[i].containsKey(str_id_field)) {
							sb_ids.append(billVOs[i].getStringValue(str_id_field) + ";"); ////
						} else {
							MessageBox.show(this, "公式定义中指定从【" + str_id_field + "】字段返回参照ID,但模板中没有该项！"); //
							return; //
						}
					} else { //
						if (billVOs[i].getStringValue(str_keys[1]) != null) {
							sb_names.append(billVOs[i].getStringValue(str_keys[1]) + ";"); //
						}
					}

					if (str_name_field != null) { //如果定义了id返回来源
						if (billVOs[i].containsKey(str_name_field)) {
							sb_names.append(billVOs[i].getStringValue(str_name_field) + ";"); ////
						} else {
							MessageBox.show(this, "公式定义中指定从【" + str_name_field + "】字段返回参照Name,但模板中没有该项！"); //
							return; //
						}
					} else {
						if (billVOs[i].getStringValue(str_keys[3]) != null) {
							sb_names.append(billVOs[i].getStringValue(str_keys[3]) + ";"); //
						}
					}
				}
				returnRefItemVO = new RefItemVO(); //
				returnRefItemVO.setId(sb_ids.toString()); //
				returnRefItemVO.setName(sb_names.toString()); //
			}
			this.setCloseType(BillDialog.CONFIRM);
			this.dispose(); //
		} else if (e.getSource() == btn_cancel) {
			this.setCloseType(BillDialog.CANCEL);
			this.dispose(); //
		}
	}

	private HashVO convertHashVO(BillVO _billvo) {
		String[] strkeys = _billvo.getKeys(); //
		HashVO hvo = new HashVO();
		for (int i = 1; i < strkeys.length; i++) {
			hvo.setAttributeValue(strkeys[i], _billvo.getStringValue(strkeys[i])); //
		}
		return hvo;
	}

	public int getRefStyleType() {
		return refStyleType;
	}

	public void setRefStyleType(int refStyleType) {
		this.refStyleType = refStyleType;
	}
}
