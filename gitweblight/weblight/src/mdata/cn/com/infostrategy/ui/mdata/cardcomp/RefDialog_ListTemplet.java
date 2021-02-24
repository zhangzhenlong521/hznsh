package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 * 一种通过将一个列表模板直接变成一个参照！极其常用！
 * 即一般一个自定义参照都可以通过一个注册面板生成主页面,然后在下面增加一个确定与取消按扭从而形成一个参照!!! 一个列表模板参照要实现以下几点核心需求：
 * 1.可以指定单选与多选 2.可以指定返回参照的id,name的字段名
 * 3.可以指定是否有查询框，或者理解为是否默认查询出数据，一种是上来就查询出所有数据，一种是再点击查询按钮查询数据 4.是否可以多选,即选择多条数据返回!!
 * 5.可以指定查询条件,即在模板的查询条件的基础上再加上新的自定义条件!模板本身的查询条件一般都是与登录人员/机构/角色相关，而这里的查询条件则可以与页面上的元素相关!
 * 
 * @author xch
 * 
 */
public class RefDialog_ListTemplet extends AbstractRefDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	private WLTButton btn_confirm, btn_cancel;

	private RefItemVO returnRefItemVO = null; //
	private BillListPanel billListPanel = null;

	private CommUCDefineVO dfvo = null; //
	private int refStyleType = 0; // 默认单选

	public RefDialog_ListTemplet(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel, CommUCDefineVO _dfvo) {
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
		billListPanel = new BillListPanel(dfvo.getConfValue("模板编码")); // 通过注册码生成一个格式面板
		if (dfvo.getConfBooleanValue("自动查询", false)) {
			billListPanel.QueryDataByCondition(dfvo.getConfValue("附加SQL条件")); //使用定义的SQL条件!
		}
		billListPanel.setQuickQueryPanelVisiable(!dfvo.getConfBooleanValue("查询面板隐藏", false)); //默认是显示的,隐藏时是不允许展开查询的！以前用到billListPanel.getQuickQueryPanel().setVisible(false); 只是将查询面板收起！李春娟修改
		if (dfvo.getConfBooleanValue("可以多选", false)) {
			billListPanel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // 可以多选
		} else {
			billListPanel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 单选
		}
		billListPanel.setDataFilterCustCondition(dfvo.getConfValue("附加SQL条件")); // 附加查询条件
		billListPanel.setItemEditable(false); //
		this.add(billListPanel, BorderLayout.CENTER); //
		this.add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout());
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");
		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel;
	}

	/**
	 * 点击按钮执行!
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) { // 如果是确定则返回数据
			String str_id_field = dfvo.getConfValue("ID字段");
			String str_name_field = dfvo.getConfValue("NAME字段"); //
			if (!dfvo.getConfBooleanValue("可以多选", false)) { // 如果是单选!!
				BillVO billvo = billListPanel.getSelectedBillVO();
				if (billvo == null) {
					MessageBox.showSelectOne(this); //
					return; //
				}

				HashVO hvo = convertHashVO(billvo); //
				returnRefItemVO = new RefItemVO(hvo); //
				if (str_id_field != null) { // 如果定义了id返回来源
					if (billvo.containsKey(str_id_field)) {
						returnRefItemVO.setId(billvo.getStringValue(str_id_field)); //
					} else {
						MessageBox.show(this, "公式定义中指定从【" + str_id_field + "】字段返回参照ID,但模板中没有该项！"); //
						return; //
					}
				}

				if (str_name_field != null) { // 如果定义了id返回来源
					if (billvo.containsKey(str_name_field)) {
						returnRefItemVO.setName(billvo.getStringValue(str_name_field)); //
					} else {
						MessageBox.show(this, "公式定义中指定从【" + str_name_field + "】字段返回参照Name,但模板中没有该项！"); //
						return; //
					}
				}
			} else { // 多选!!!
				BillVO[] billVOs = billListPanel.getSelectedBillVOs(); //
				if (billVOs == null || billVOs.length == 0) {
					MessageBox.showSelectOne(this); //
					return; //
				}
				StringBuffer sb_ids = new StringBuffer(";"); //
				StringBuffer sb_names = new StringBuffer(); //
				String[] str_keys = billVOs[0].getKeys(); //
				for (int i = 0; i < billVOs.length; i++) {
					if (str_id_field != null) { // 如果定义了id返回来源
						if (billVOs[i].containsKey(str_id_field)) {
							sb_ids.append(billVOs[i].getStringValue(str_id_field) + ";"); // //
						} else {
							MessageBox.show(this, "公式定义中指定从【" + str_id_field + "】字段返回参照ID,但模板中没有该项！"); //
							return; //
						}
					} else {
						if (billVOs[i].getStringValue(str_keys[1]) != null) {
							sb_names.append(billVOs[i].getStringValue(str_keys[1]) + ";"); //
						}
					}

					if (str_name_field != null) { // 如果定义了id返回来源
						if (billVOs[i].containsKey(str_name_field)) {
							sb_names.append(billVOs[i].getStringValue(str_name_field) + ";"); // //
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

	/**
	 * 初始宽度
	 * @return
	 */
	public int getInitWidth() {
		return 800;
	}

	public int getInitHeight() {
		return 500;
	}

	public BillListPanel getBillListPanel() {
		return billListPanel;
	}

	public void setBillListPanel(BillListPanel billListPanel) {
		this.billListPanel = billListPanel;
	}
}
