package com.pushworld.ipushgrc.ui.cmpcheck.p030;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BackGroundDrawingUtil;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

public class EventCorpDeptRef extends AbstractRefDialog implements ActionListener {

	private WLTButton btn_confirm, btn_cancel; //
	private RefItemVO returnRefItemVO = null; //
	private BillTreePanel billTreePanel = null; //

	public EventCorpDeptRef(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
		super(_parent, _title, refItemVO, panel);
	}

	public RefItemVO getReturnRefItemVO() {
		return returnRefItemVO;
	}

	public void initialize() {
		RefItemVO checkedcorp = (RefItemVO) this.getBillPanel().getClientProperty("checkedcorp"); //显示的所有机构
		String deptids = null;
		if (checkedcorp != null) { //如果不为空，那么就显示部分机构
			String value = checkedcorp.getId();
			if (value != null && !value.trim().equals("")) {
				deptids = new TBUtil().getInCondition(value);
			}
		}
		this.setLayout(new BorderLayout()); //
		billTreePanel = new BillTreePanel("PUB_CORP_DEPT_CODE1"); //通过注册码生成一个格式面板
		billTreePanel.reSetTreeChecked(true); //则设成勾选框
		billTreePanel.getBtnPanel().setVisible(false); //
		billTreePanel.setDragable(false); //
		billTreePanel.setDefaultLinkedCheck(false); //设置为不连动选择

		if (deptids != null && !deptids.equals("-99999")) {
			billTreePanel.queryDataByCondition(" id in(" + deptids + ")");
		} else {
			billTreePanel.queryDataByCondition(null);
		}

		//加入两个控制参数  展开某一层

		billTreePanel.setHelpInfoVisiable(true);
		this.add(billTreePanel, BorderLayout.CENTER); //
		this.add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout(), BackGroundDrawingUtil.HORIZONTAL_FROM_MIDDLE);
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
			onConfirm(); //确认!!!
		} else if (e.getSource() == btn_cancel) {
			onCancel(); //取消
		}
	}

	//点击确认!
	private void onConfirm() {
		String str_id_field = "id";//
		String str_name_field = "name"; //
		boolean isOnlyChooseLeafNode = false; //只能选叶子结点,默认是否

		if (!billTreePanel.isChecked()) { //如果不是多选情况,即单选的
			BillVO billvo = billTreePanel.getSelectedVO(); //选中一条记录!!!
			if (billvo == null) {
				MessageBox.showSelectOne(this); //
				return; //
			}
			if (isOnlyChooseLeafNode) { //判断是否只能选择叶子结点!!!
				if (!billTreePanel.getSelectedNode().isLeaf()) {
					MessageBox.show(this, "只能选择叶子结点,即最末级的结点!"); //
					return; //
				}
			}
			BillVO[] parentPathVOs = billTreePanel.getSelectedParentPathVOs(); //父亲路径的所有结点!!
			HashVO hvo = convertHashVO(billvo); //
			StringBuffer sb_parentPathIds = new StringBuffer(); //
			StringBuffer sb_parentPathNames = new StringBuffer(); //

			for (int i = 0; i < parentPathVOs.length; i++) {
				sb_parentPathIds.append(parentPathVOs[i].getStringValue(str_id_field) + ";"); //将父亲结点的所有ID拼起来!!!
				sb_parentPathNames.append(parentPathVOs[i].getStringValue(str_name_field) + ";"); //将父亲结点所有的Name拼起来!!
			}
			hvo.setAttributeValue("$ParentPathIds", ";" + sb_parentPathIds.toString()); //赋一个指定的特殊变量
			hvo.setAttributeValue("$ParentPathNames", ";" + billvo.getUserObject("$ParentPathNames")); //赋一个指定的特殊变量
			hvo.setAttributeValue("$ReturnPathName", billvo.getUserObject("$ParentPathName")); //赋一个指定的特殊变量

			returnRefItemVO = new RefItemVO(hvo); //			
			if (str_id_field != null) { //如果id字段不为空
				if (billvo.containsKey(str_id_field)) {
					returnRefItemVO.setId(billvo.getStringValue(str_id_field)); //
				} else {
					MessageBox.show(this, "公式定义中指定从【" + str_id_field + "】字段返回参照ID,但模板中没有该项！"); //
					return; //
				}
			} else {
				returnRefItemVO.setId(billvo.getPkValue()); //
			}

			if (str_name_field != null) { //如果name字段不为空
				if (billvo.containsKey(str_name_field)) {
					returnRefItemVO.setName(billvo.getStringValue(str_name_field)); //
				} else {
					MessageBox.show(this, "公式定义中指定从【" + str_name_field + "】字段返回参照Name,但模板中没有该项！"); //
					return; //
				}

			} else {
				returnRefItemVO.setName(billvo.toString()); //
			}
		} else { //如果是多选的..
			BillVO[] billVOs = billTreePanel.getCheckedVOs(); //新的机制有了个$ParentPathNames用户字段在里面!!
			if (billVOs == null || billVOs.length == 0) {
				MessageBox.show(this, "请至少勾选一条数据!\r\n温馨提示:这是可以多选的,点击结点前的那个勾选框,才算真正选中!"); //
				return; //
			}

			returnRefItemVO = new RefItemVO(); //
			StringBuffer sb_ids = new StringBuffer(";");
			StringBuffer sb_names = new StringBuffer(); //
			for (int i = 0; i < billVOs.length; i++) { //遍历所有对象!!
				if (str_id_field != null) { //如果id字段不为空
					if (billVOs[i].containsKey(str_id_field)) {
						sb_ids.append(billVOs[i].getStringValue(str_id_field) + ";"); //
					} else {
						MessageBox.show(this, "公式定义中指定从【" + str_id_field + "】字段返回参照ID,但模板中没有该项！"); //
						return; //
					}
				} else {
					sb_ids.append(billVOs[i].getPkValue() + ";"); //如果为树形模板参照且为多选则查询就不管用了所以改成多选参照存储的方式
				}

				if (str_name_field != null) { //如果name字段不为空
					if (billVOs[i].containsKey(str_name_field)) {
						sb_names.append(billVOs[i].getStringValue(str_name_field) + ";"); //
					} else {
						MessageBox.show(this, "公式定义中指定从【" + str_name_field + "】字段返回参照ID,但模板中没有该项！"); //
						return; //
					}
				} else {
					sb_names.append(billVOs[i].toString() + ";"); //
				}
			}
			returnRefItemVO.setId(sb_ids.toString()); //
			returnRefItemVO.setName(sb_names.toString()); //
		}
		this.setCloseType(BillDialog.CONFIRM);
		this.dispose(); //
	}

	//点击取消!!!
	private void onCancel() {
		returnRefItemVO = null; //
		this.setCloseType(BillDialog.CANCEL);
		this.dispose(); //
	}

	private HashVO convertHashVO(BillVO _billvo) {
		String[] strkeys = _billvo.getKeys(); //
		HashVO hvo = new HashVO();
		for (int i = 0; i < strkeys.length; i++) {
			hvo.setAttributeValue(strkeys[i], _billvo.getStringValue(strkeys[i])); //
		}
		return hvo;
	}

	/**
	 * 初始宽度
	 * @return
	 */
	public int getInitWidth() {
		return 500;
	}

	public int getInitHeight() {
		return 500;
	}
}
