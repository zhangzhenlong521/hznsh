package com.pushworld.ipushgrc.ui.score.p010;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.infostrategy.ui.sysapp.corpdept.SeqListDialog;

/**
 * 违规积分-》基础信息-》积分标准定义【李春娟/2013-05-09】
 * 由参数配置，可使用两种扣分模式，默认为第一种。
 * 1-使用风险等级和发现渠道两个维度定义标准分值。
 * 2-使用违规行为和发现渠道两个维度定义标准分值。
 * @author lcj
 *
 */
public class StandardEditWKPanel extends AbstractWorkPanel implements ActionListener, BillTreeSelectListener {
	private BillCellPanel cellPanel;//第一种模式，二维表
	private WLTButton btn_save;//第一种模式，保存按钮
	private String[] riskrank;//第一种模式，风险等级名称
	private String[] findrank;//第一种模式，发现渠道名称
	private BillTreePanel typeTreePanel;//第二种模式，违规类型树
	private BillListPanel listPanel;//第二种模式，违规行为列表
	private WLTButton btn_add, btn_edit, btn_delete, btn_seq, btn_publish, btn_abolish;//第二种模式，违规行为列表上的所有按钮

	/**
	 * 界面初始化类
	 */

	@Override
	public void initialize() {
		int model = TBUtil.getTBUtil().getSysOptionIntegerValue("违规积分扣分模式", 1);
		if (model == 1) {
			init1();
		} else {
			init2();
		}
	}

	/**
	 * 违规积分第一种模式，使用风险等级和发现渠道两个维度定义标准分值
	 */
	private void init1() {
		try {
			riskrank = UIUtil.getStringArrayFirstColByDS(null, "select name from PUB_COMBOBOXDICT where type='违规积分_风险等级' order by seq");
			findrank = UIUtil.getStringArrayFirstColByDS(null, "select name from PUB_COMBOBOXDICT where type='违规积分_发现渠道' order by seq");
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showException(this, e);
			return;
		}
		JPanel northPanel = WLTPanel.createDefaultPanel();
		northPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		WLTLabel label = new WLTLabel();//说明
		boolean addbtn = true;//是否需要增加按钮
		if (riskrank == null || riskrank.length == 0) {
			if (findrank == null || findrank.length == 0) {
				label.setText("    请先维护[风险等级定义]和[发现渠道定义]    ");
			} else {
				label.setText("    请先维护[风险等级定义]    ");
			}
			addbtn = false;
		} else {
			if (findrank == null || findrank.length == 0) {
				label.setText("    请先维护[发现渠道定义]    ");
				addbtn = false;
			} else {
				label.setText("    请双击白色单元格进行编辑    ");
			}
		}
		if (addbtn) {
			btn_save = new WLTButton("保存");
			btn_save.addActionListener(this);
			northPanel.add(btn_save);
		}
		label.setForeground(Color.BLUE);
		northPanel.add(label);
		cellPanel = new BillCellPanel(getBillCellVO());
		cellPanel.setAllowShowPopMenu(false);
		this.add(northPanel, BorderLayout.NORTH);
		this.add(cellPanel, BorderLayout.CENTER);
	}

	/**
	 * 违规积分第二种模式，使用违规行为和发现渠道两个维度定义标准分值，这里先维护违规行为及其对应的基本分值
	 */
	private void init2() {
		typeTreePanel = new BillTreePanel("SCORE_TYPE_LCJ_E01");
		typeTreePanel.setMoveUpDownBtnVisiable(false);
		typeTreePanel.queryDataByCondition(null);
		typeTreePanel.addBillTreeSelectListener(this);

		listPanel = new BillListPanel("SCORE_STANDARD2_LCJ_E01");
		btn_add = new WLTButton("新增");
		btn_edit = new WLTButton("修改");
		btn_delete = new WLTButton("删除");
		btn_seq = new WLTButton("排序");
		btn_publish = new WLTButton("发布");
		btn_abolish = new WLTButton("废止");
		btn_add.addActionListener(this);
		btn_edit.addActionListener(this);
		btn_delete.addActionListener(this);
		btn_seq.addActionListener(this);
		btn_publish.addActionListener(this);
		btn_abolish.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] { btn_add, btn_edit, btn_delete, btn_seq, btn_publish, btn_abolish });
		listPanel.repaintBillListButton();
		WLTSplitPane splitPane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, typeTreePanel, listPanel);
		splitPane.setDividerLocation(220);
		this.add(splitPane);
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		if (_event.getCurrSelectedNode().isRoot() || _event.getCurrSelectedVO() == null) {
			listPanel.clearTable();
		} else {
			BillVO billVO = _event.getCurrSelectedVO();
			listPanel.QueryDataByCondition("scoretype=" + billVO.getStringValue("id"));
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_save) {
			onSave();
		} else if (e.getSource() == btn_add) {
			onAdd();
		} else if (e.getSource() == btn_edit) {
			onEdit();
		} else if (e.getSource() == btn_delete) {
			onDelete();
		} else if (e.getSource() == btn_seq) {
			onSeq();
		} else if (e.getSource() == btn_publish) {
			onPublish();
		} else if (e.getSource() == btn_abolish) {
			onAbolish();
		}
	}

	/**
	 * 获得BillCellPanel的数据VO
	 * @return
	 */
	private BillCellVO getBillCellVO() {
		BillCellVO cellVO = new BillCellVO(); // //...
		cellVO.setRowlength(riskrank.length + 2); //
		cellVO.setCollength(findrank.length + 2); //

		String str_sql = "select t1.riskrank,t1.findrank, t2.score from v_score_risk_find t1 left join score_standard t2 on t1.RISKRANK=t2.RISKRANK and t1.FINDRANK=t2.FINDRANK left join PUB_COMBOBOXDICT t3 on t3.type='违规积分_风险等级' and t3.id=t1.riskrank left join PUB_COMBOBOXDICT t4 on t4.type='违规积分_发现渠道' and t4.id=t1.findrank order by t3.seq,t4.seq";
		String[][] str_scores = null;
		try {
			str_scores = UIUtil.getStringArrayByDS(null, str_sql);
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showException(this, e);
			return cellVO;
		}
		BillCellItemVO[][] cellItemVOs = new BillCellItemVO[cellVO.getRowlength()][cellVO.getCollength()]; //
		cellItemVOs[0][0] = getBillCellItemVO(-1); //
		cellItemVOs[1][1] = getBillCellItemVO(-1); //
		cellItemVOs[0][1] = getBillCellItemVO(-1); //
		cellItemVOs[0][1].setCellvalue("发现渠道→");
		cellItemVOs[1][0] = getBillCellItemVO(-1); //
		cellItemVOs[1][0].setCellvalue("风险等级↓");
		cellItemVOs[1][1] = getBillCellItemVO(-1); //
		cellItemVOs[1][1].setCellvalue("分值↘");
		for (int j = 2; j < cellVO.getCollength(); j++) {//第一行，表头
			cellItemVOs[0][j] = getBillCellItemVO(0); //
			cellItemVOs[0][j].setCellvalue(findrank[j - 2]);
			cellItemVOs[0][j].setSpan("2,1");//合并两行一列
		}
		for (int i = 2; i < cellVO.getRowlength(); i++) {//第一列，表头
			cellItemVOs[i][0] = getBillCellItemVO(1); //
			cellItemVOs[i][0].setCellvalue(riskrank[i - 2]);
			cellItemVOs[i][0].setSpan("1,2");//合并一行两列
		}
		int count = 0;
		for (int i = 2; i < cellVO.getRowlength(); i++) {
			for (int j = 2; j < cellVO.getCollength(); j++) {
				cellItemVOs[i][j] = getBillCellItemVO(2); //
				cellItemVOs[i][j].setCellvalue(str_scores[count][2]);
				cellItemVOs[i][j].setCellkey(str_scores[count][0] + "$" + str_scores[count][1]);
				count++;
			}
		}

		cellVO.setCellItemVOs(cellItemVOs); //
		return cellVO;
	}

	/**
	 * 获得单元格VO
	 * @param _type  -1-最左上角的说明， 0-第一行行头，1-第一列列头 2-数据
	 * @return
	 */
	private BillCellItemVO getBillCellItemVO(int _type) {
		BillCellItemVO itemVO = new BillCellItemVO();
		itemVO.setHalign(2);//水平居中
		itemVO.setValign(2);//垂直居中
		itemVO.setRowheight("40");//统一高度
		if (_type == -1) {//最左上角的说明没有合并，故宽度为行头的一半
			itemVO.setColwidth("70");
			itemVO.setIseditable("N");
			itemVO.setBackground("255,255,160");
		} else if (_type == 0) {
			itemVO.setIseditable("N");
			itemVO.setColwidth("120");
			itemVO.setBackground("255,215,255");
		} else if (_type == 1) {
			itemVO.setIseditable("N");
			itemVO.setBackground("204,232,207");
		} else {
			//行头已经设置了宽度，故这里无需设置
			itemVO.setCelltype(BillCellPanel.ITEMTYPE_NUMBERTEXT);//设置为数字框
		}
		return itemVO;
	}

	/**
	 * 保存按钮的逻辑，billcellpanel无法判断哪个单元格修改过，故全部删除后再新增
	 * 
	 */
	private void onSave() {
		cellPanel.stopEditing();
		ArrayList sqlList = new ArrayList();
		InsertSQLBuilder insertSQLBuilder = new InsertSQLBuilder("SCORE_STANDARD");
		try {
			BillCellVO cellVO = cellPanel.getBillCellVO();
			BillCellItemVO[][] cellItemVOs = cellVO.getCellItemVOs(); //
			int count = 1;
			boolean hasmsg = false;
			for (int i = 2; i < cellVO.getRowlength(); i++) {
				for (int j = 2; j < cellVO.getCollength(); j++) {
					String key = cellItemVOs[i][j].getCellkey();
					String riskrank = key.substring(0, key.indexOf("$"));
					String findrank = key.substring(key.indexOf("$") + 1);
					String value = cellItemVOs[i][j].getCellvalue();
					if (!hasmsg && (value == null || value.trim().equals(""))) {
						hasmsg = true;
					}
					insertSQLBuilder.putFieldValue("id", count);
					count++;
					insertSQLBuilder.putFieldValue("riskrank", riskrank);
					insertSQLBuilder.putFieldValue("findrank", findrank);
					insertSQLBuilder.putFieldValue("score", value);

					sqlList.add(insertSQLBuilder.getSQL());
				}
			}
			if (sqlList.size() > 0) {
				UIUtil.executeUpdateByDS(null, "delete from score_standard");
				UIUtil.executeBatchByDS(null, sqlList);
			}
			if (hasmsg) {
				MessageBox.show(this, "保存成功!\r\n标准未全部定义,请继续编写.");
			} else {
				MessageBox.show(this, "保存成功!");
			}
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	/**
	 * 违规积分第二种模式，新增标准的逻辑，如果树中选中类型则设置为新增标准的违规类型
	 */
	private void onAdd() {
		HashMap map = new HashMap();
		BillVO billVO = typeTreePanel.getSelectedVO();
		if (billVO != null) {
			map.put("SCORETYPE", new RefItemVO(billVO.getStringValue("id"), "", billVO.getStringValue("typename")));
		}
		listPanel.doInsert(map);
	}

	/**
	 * 违规积分第二种模式，编辑标准的逻辑，只有状态为空的可以编辑，有效和废止的不可编辑
	 */
	private void onEdit() {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		String rolescode[] = ClientEnvironment.getCurrLoginUserVO().getAllRoleCodes();
		boolean ismanager = false;
		for (int i = 0; i < rolescode.length; i++) {
			if ("系统管理员".equals(rolescode[i]) || "超级管理员".equals(rolescode[i])) {
				ismanager = true;
				break;
			}
		}
		if (billVO.getStringValue("state") == null || "".equals(billVO.getStringValue("state")) || ismanager) {
			listPanel.doEdit();
		} else {
			MessageBox.show(this, "该记录已" + billVO.getStringValue("state") + ",不能编辑.");
		}
	}

	/**
	 * 违规积分第二种模式，删除标准的逻辑，只有有效的不能删除，其他状态的都可删除
	 */
	private void onDelete() {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if ("有效".equals(billVO.getStringValue("state"))) {
			MessageBox.show(this, "该记录已" + billVO.getStringValue("state") + ",不能删除.");
		} else {
			listPanel.doDelete(false);
		}
	}

	/**
	 * 违规积分第二种模式，排序逻辑
	 */
	private void onSeq() {
		SeqListDialog dialog_user = new SeqListDialog(this, "标准排序", listPanel.getTempletVO(), listPanel.getAllBillVOs());
		dialog_user.getBilllistPanel().setQuickQueryPanelVisiable(false);
		dialog_user.setVisible(true);
		if (dialog_user.getCloseType() == 1) {//如果点击确定返回，则需要刷新一下页面
			listPanel.refreshData(); //
		}
	}

	/**
	 * 违规积分第二种模式，发布标准的逻辑，只有有效的不可重复发布，状态为空或废止的都可发布为有效
	 */
	private void onPublish() {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if ("有效".equals(billVO.getStringValue("state"))) {
			MessageBox.show(this, "该记录已发布,请勿重复操作.");
			return;
		}
		try {
			if (billVO.getStringValue("SCORE") == null || billVO.getStringValue("SCORE").trim().equals("")) {
				MessageBox.show(this, "请设置分值后再发布.\r\n分值可以为固定分值[2],也可以为分值范围[2-5].");
				return;
			}

			UIUtil.executeUpdateByDSPS(null, "update " + listPanel.getTempletVO().getSavedtablename() + " set state='有效' where id=" + billVO.getPkValue());
			listPanel.refreshCurrSelectedRow();
			MessageBox.show(this, "发布成功!");
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showException(this, e);
		}
	}

	/**
	 * 违规积分第二种模式，废止标准的逻辑，只有废止的不可重复废止，状态为空或有效的都可废止
	 */
	private void onAbolish() {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if ("废止".equals(billVO.getStringValue("state"))) {
			MessageBox.show(this, "该记录已废止,请勿重复操作.");
			return;
		}
		if (MessageBox.confirm(this, "废止的记录在违规登记中不能使用,是否继续?")) {
			try {
				UIUtil.executeUpdateByDSPS(null, "update " + listPanel.getTempletVO().getSavedtablename() + " set state='废止' where id=" + billVO.getPkValue());
				listPanel.refreshCurrSelectedRow();
				MessageBox.show(this, "废止成功!");
			} catch (Exception e) {
				e.printStackTrace();
				MessageBox.showException(this, e);
			}
		}
	}
}
