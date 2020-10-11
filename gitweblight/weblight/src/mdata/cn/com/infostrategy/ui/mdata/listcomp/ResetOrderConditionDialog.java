/**************************************************************************
 * $RCSfile: ResetOrderConditionDialog.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.listcomp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 重新定义排序条件的窗口!! 双击表头排序只对当前页数据! 但在上海家商行及其他客户都提出过基于所有页的数据排序!
 * 因为必须要分页(否则性能测试肯定通不过,招行中已经验证了),所以只能在数据库的SQL语句进行分页!!也就是说该功能只是对排序条件进行重新定义!!!
 * @author xch
 *
 */
public class ResetOrderConditionDialog extends BillDialog implements ActionListener {

	private static final long serialVersionUID = 1L; //

	private BillListPanel billList = null; //
	private WLTButton btn_additem, btn_delitem, btn_moveup, btn_movedown; //新增,删除条件!
	private WLTButton btn_confirm, btn_cancel; //确定/取消按钮!

	private String[][] str_allViewColumns = null; //
	private String str_initOrderCons = null; //初始排序条件!!
	private String returnCons = null; //返回的条件!!

	private TBUtil tbUtil = new TBUtil(); //

	/**
	 * 
	 * @param _parent
	 * @param str_orderCOndition 
	 * @param _allViewColumnNames 
	 * @param _title
	 * @param _width
	 * @param li_height
	 * @param _templetItemVOs
	 * @param str_filterkeys
	 */
	public ResetOrderConditionDialog(Container _parent, String[][] _allViewColumnNames, String _orderCondition) {
		super(_parent, "设置排序条件", 300, 350); //
		this.str_allViewColumns = _allViewColumnNames; //
		this.str_initOrderCons = _orderCondition; //
		initialize(); //
	}

	/**
	 * 初始化页面
	 * 
	 */
	private void initialize() {
		billList = new BillListPanel(getTMO()); //
		if (str_initOrderCons != null && !str_initOrderCons.trim().equals("")) { //如果有初始排序条件!
			String[] str_items = tbUtil.split(str_initOrderCons, ","); //以逗号分隔!!
			for (int i = 0; i < str_items.length; i++) { //遍历各个条件!!
				String str_itemcon = str_items[i].trim(); //
				str_itemcon = tbUtil.replaceAll(str_itemcon, "    ", " "); //有可能有4个空格,则替换成一个
				str_itemcon = tbUtil.replaceAll(str_itemcon, "   ", " "); //有可能有3个空格,则替换成一个
				str_itemcon = tbUtil.replaceAll(str_itemcon, "  ", " "); //有可能有2个空格,则替换成一个
				String[] str_key_asc = tbUtil.split(str_itemcon, " "); //以空格相隔!
				String str_key = str_key_asc[0]; //
				String str_isdesc = "N"; //是否倒序?
				if (str_key_asc.length > 1 && str_key_asc[1].trim().equalsIgnoreCase("desc")) { //如果有两个,且后面是desc标记!!!则表示是倒序!!!
					str_isdesc = "Y"; //
				}
				String str_name = findItemName(this.str_allViewColumns, str_key); //
				int li_newRow = billList.addEmptyRow(false); //
				billList.setValueAt(new StringItemVO(str_key), li_newRow, "ITEMKEY"); //key
				billList.setValueAt(new StringItemVO(str_name), li_newRow, "ITEMNAME"); //name
				billList.setValueAt(new StringItemVO(str_isdesc), li_newRow, "ISDESC"); //是否倒序
			}
		}
		this.getContentPane().add(billList, BorderLayout.CENTER); //
		this.getContentPane().add(getNorthPanel(), BorderLayout.NORTH); //上面的两个按钮
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //下面的两个按钮
	}

	//
	private JPanel getNorthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
		btn_additem = new WLTButton("增加");
		btn_delitem = new WLTButton("删除");
		btn_moveup = new WLTButton("上移"); //
		btn_movedown = new WLTButton("上移"); //

		btn_additem.setFocusable(false);
		btn_delitem.setFocusable(false);
		btn_moveup.setFocusable(false);
		btn_movedown.setFocusable(false);

		btn_additem.addActionListener(this); //
		btn_delitem.addActionListener(this); //
		btn_moveup.addActionListener(this); //
		btn_movedown.addActionListener(this); //
		panel.add(btn_additem); //
		panel.add(btn_delitem); //
		panel.add(btn_moveup); //
		panel.add(btn_movedown); //
		return panel;
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout());
		btn_confirm = new WLTButton("确定"); //
		btn_cancel = new WLTButton("取消"); //
		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_additem) {
			onAddItem();
		} else if (e.getSource() == btn_delitem) {
			onDelItem();
		} else if (e.getSource() == btn_moveup) {
			billList.moveUpRow(); //上移
		} else if (e.getSource() == btn_movedown) {
			billList.moveDownRow(); //下移
		} else if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		}
	}

	//增加新的条件!!
	private void onAddItem() {
		AddNewOrderConditionDialog addDialog = new AddNewOrderConditionDialog(this, this.str_allViewColumns); //
		addDialog.setVisible(true); //
		if (addDialog.getCloseType() == 1) { //如果是确定返回的!!
			String[][] str_newCons = addDialog.getReturnCons(); //
			for (int i = 0; i < str_newCons.length; i++) {
				int li_row = billList.addEmptyRow(false); //
				billList.setValueAt(new StringItemVO(str_newCons[i][0]), li_row, "ITEMKEY"); //
				billList.setValueAt(new StringItemVO(str_newCons[i][1]), li_row, "ITEMNAME"); //
				billList.setValueAt(new StringItemVO("N"), li_row, "ISDESC"); //
			}
		}
	}

	private void onDelItem() {
		if (billList.getSelectedRow() < 0) {
			MessageBox.show(this, "必须选择一条记录才能进行此操作!"); //
			return; //
		}
		billList.removeSelectedRows(); //删除所有选中的行!!
	}

	//确定
	private void onConfirm() {
		BillVO[] billVOs = billList.getAllBillVOs(); //
		if (billVOs == null || billVOs.length <= 0) {
			returnCons = null; //没有条件
		} else {
			StringBuilder sb_cons = new StringBuilder(); //
			for (int i = 0; i < billVOs.length; i++) {
				String str_key = billVOs[i].getStringValue("ITEMKEY"); //列名!
				String str_isdesc = billVOs[i].getStringValue("ISDESC"); //是否倒序?
				sb_cons.append(str_key); //
				if ("Y".equals(str_isdesc)) { //如果是倒序
					sb_cons.append(" desc"); //
				}
				if (i != billVOs.length - 1) {
					sb_cons.append(","); //如果不是最后一个,则加上逗号!!
				}
			}
			returnCons = sb_cons.toString(); //
		}
		setCloseType(1);
		this.dispose(); //
	}

	private void onCancel() {
		setCloseType(2); //
		this.dispose(); //
	}

	public String getReturnCons() {
		return returnCons;
	}

	//
	private String findItemName(String[][] _allViewColumns, String _key) {
		for (int i = 0; i < _allViewColumns.length; i++) { //
			if (_allViewColumns[i][0].equalsIgnoreCase(_key)) {
				return _allViewColumns[i][1]; //
			}
		}
		return null;
	}

	/**
	 * 弹出列表
	 * @return
	 */
	private DefaultTMO getTMO() {
		HashVO hvo_parent = new HashVO(); //
		hvo_parent.setAttributeValue("templetcode", "ResetSortCondition"); //模版编码,请勿随便修改
		hvo_parent.setAttributeValue("templetname", "重置排序条件"); //模板名称

		HashVO[] hvs_child = new HashVO[3]; //
		hvs_child[0] = new HashVO(); //
		hvs_child[0].setAttributeValue("itemkey", "ITEMKEY"); //唯一标识,用于取数与保存
		hvs_child[0].setAttributeValue("itemname", "主键"); //显示名称
		hvs_child[0].setAttributeValue("itemname_e", "Id"); //显示名称
		hvs_child[0].setAttributeValue("itemtype", "文本框"); //控件类型
		hvs_child[0].setAttributeValue("listwidth", "145"); //列表是宽度
		hvs_child[0].setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
		hvs_child[0].setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用

		hvs_child[1] = new HashVO(); //
		hvs_child[1].setAttributeValue("itemkey", "ITEMNAME"); //唯一标识,用于取数与保存
		hvs_child[1].setAttributeValue("itemname", "列名"); //显示名称
		hvs_child[1].setAttributeValue("itemname_e", "ItemName"); //显示名称
		hvs_child[1].setAttributeValue("itemtype", "文本框"); //控件类型
		hvs_child[1].setAttributeValue("listwidth", "125"); //列表是宽度
		hvs_child[1].setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		hvs_child[1].setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用

		hvs_child[2] = new HashVO(); //
		hvs_child[2].setAttributeValue("itemkey", "ISDESC"); //唯一标识,用于取数与保存
		hvs_child[2].setAttributeValue("itemname", "是否倒序"); //显示名称
		hvs_child[2].setAttributeValue("itemname_e", "isDesc"); //显示名称
		hvs_child[2].setAttributeValue("itemtype", WLTConstants.COMP_CHECKBOX); //控件类型
		hvs_child[2].setAttributeValue("listwidth", "80"); //列表是宽度
		hvs_child[2].setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
		hvs_child[2].setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用

		DefaultTMO tmo = new DefaultTMO(hvo_parent, hvs_child); //
		return tmo; //
	}

	/**
	 * 新增的窗口!!!
	 * @author xch
	 *
	 */
	class AddNewOrderConditionDialog extends BillDialog implements ActionListener {

		private static final long serialVersionUID = 1L;
		private WLTButton btn_addConfirm, btn_addCancel; //
		private JList list = null; //
		private String[][] str_returnCons = null; //

		public AddNewOrderConditionDialog(Container _parent, String[][] _allColumns) {
			super(_parent, "新增条件", 250, 250);
			ComBoxItemVO[] itemVOs = new ComBoxItemVO[_allColumns.length]; //
			for (int i = 0; i < itemVOs.length; i++) {
				itemVOs[i] = new ComBoxItemVO(_allColumns[i][0], null, _allColumns[i][1]); //
			}
			list = new JList(itemVOs); //
			list.setBackground(Color.WHITE); //
			JScrollPane scroll = new JScrollPane(list); //
			this.getContentPane().add(scroll); //

			btn_addConfirm = new WLTButton("确定"); //
			btn_addCancel = new WLTButton("取消"); //
			btn_addConfirm.addActionListener(this); //
			btn_addCancel.addActionListener(this); //
			JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout()); //
			panel.add(btn_addConfirm); //
			panel.add(btn_addCancel); //
			this.getContentPane().add(panel, BorderLayout.SOUTH); //
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btn_addConfirm) {
				Object[] objs = list.getSelectedValues(); //
				if (objs == null || objs.length <= 0) {
					MessageBox.show(this, "必须选择一条记录进行此操作"); //
					return; //
				}
				str_returnCons = new String[objs.length][2]; //
				for (int i = 0; i < objs.length; i++) {
					ComBoxItemVO selVO = (ComBoxItemVO) objs[i]; //
					str_returnCons[i][0] = selVO.getId(); //
					str_returnCons[i][1] = selVO.getName(); //
				}
				setCloseType(1); //
				this.dispose(); //
			} else if (e.getSource() == btn_addCancel) {
				str_returnCons = null; //
				setCloseType(2); //
				this.dispose(); //
			}

		}

		public String[][] getReturnCons() {
			return this.str_returnCons; //新增的返回条件!!!
		}
	}

}
