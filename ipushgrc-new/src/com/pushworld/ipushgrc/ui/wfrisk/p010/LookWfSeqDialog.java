package com.pushworld.ipushgrc.ui.wfrisk.p010;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class LookWfSeqDialog extends BillDialog implements ActionListener {
	private BillListPanel billlist_process;
	private WLTButton btn_confirm, btn_cancel;
	private String[] returnArray = null;//返回的流程id数组

	public LookWfSeqDialog(Container _parent, String _title, String[][] _process) {
		super(_parent, _title, 600, 450);
		this.getContentPane().setLayout(new BorderLayout()); //
		billlist_process = new BillListPanel(new TMO_Process());

		for (int i = 0; i < _process.length; i++) {
			billlist_process.newRow();
			billlist_process.setRealValueAt(_process[i][0], i, "id");
			billlist_process.setRealValueAt(_process[i][2], i, "name");
			billlist_process.setRealValueAt((i + 1) + "", i, "seq");
		}
		billlist_process.addBatchBillListButton(new WLTButton[] { WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEUP, "上移"), WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEDOWN, "下移") });
		billlist_process.repaintBillListButton();
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(billlist_process, BorderLayout.CENTER);
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
	}

	public String[] getReturnArray() {
		return returnArray;
	}

	/**
	 * 按钮点击事件
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		}
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout()); //
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");
		btn_confirm.addActionListener(this); //
		btn_cancel.addActionListener(this); //
		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel;
	}

	private void onConfirm() {
		this.setCloseType(1);
		int count = billlist_process.getRowCount();
		returnArray = new String[count];
		for (int i = 0; i < count; i++) {
			returnArray[i] = billlist_process.getRealValueAtModel(i, "id");
		}
		this.dispose();
	}

	private void onCancel() {
		this.setCloseType(2);
		this.dispose();
	}

	private class TMO_Process extends AbstractTMO {
		@Override
		public HashVO getPub_templet_1Data() {
			HashVO vo = new HashVO(); //
			vo.setAttributeValue("templetcode", "pub_wf_process"); //模版编码，请勿随便修改
			vo.setAttributeValue("CardWidth", "577"); //卡片宽度
			vo.setAttributeValue("Isshowlistpagebar", "N"); //列表是否显示分页栏
			vo.setAttributeValue("Isshowlistopebar", "Y"); //列表是否显示操作按钮栏

			return vo;
		}

		@Override
		public HashVO[] getPub_templet_1_itemData() {
			Vector vector = new Vector();
			HashVO itemVO = null;

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "id"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "流程id"); //显示名称
			itemVO.setAttributeValue("itemname_e", ""); //显示名称
			itemVO.setAttributeValue("itemtype", "数字框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "180"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "name"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "流程名称"); //显示名称
			itemVO.setAttributeValue("itemname_e", ""); //显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "280"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "280"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "seq"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "顺序"); //显示名称
			itemVO.setAttributeValue("itemname_e", ""); //显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式
			itemVO.setAttributeValue("listwidth", "100"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "4"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("propisshowable", "Y"); //属性框中是否显示(Y,N)
			itemVO.setAttributeValue("propiseditable", "Y"); //属性框中是否可编辑(Y,N)
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);

			return (HashVO[]) vector.toArray(new HashVO[0]);
		}
	}
}
