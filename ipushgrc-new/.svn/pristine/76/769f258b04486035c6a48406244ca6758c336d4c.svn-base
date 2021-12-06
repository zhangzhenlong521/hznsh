package com.pushworld.ipushgrc.ui.duty.p020;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BackGroundDrawingUtil;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.sysapp.corpdept.SeqListDialog;

/**
 * 岗位职责维护!!  左边是机构树,右边上边是岗位[pub_post],下边是岗位的岗职[cmp_postduty]!
 * @author xch
 *
 */
public class PostDutyEditWKPanel extends AbstractWorkPanel implements BillTreeSelectListener, BillListSelectListener, ActionListener {

	private BillTreePanel billTree_corp = null; //机构部门!!
	private BillListPanel billList_post = null; //岗位列表!!
	private BillListPanel billList_postduty = null; //岗位岗责!!
	private BillListPanel list_post;
	private BillListPanel list_postduty;
	private WLTButton btn_insert, btn_update, btn_delete, btn_seq, btn_import; //增,删,改
	private WLTButton btn_post_insert, btn_post_delete, btn_post_update;
	private WLTButton btn_duty; //佛山提出的。通过岗责和环节操作要求来维护细化岗责任务。
	private WLTButton btn_cancel, btn_confirm;
	private WLTButton btn_addduty, btn_showduty;//加入、查看按钮
	private TBUtil tbUtil = new TBUtil(); //
	private int tempMap = 0;//查看按鈕数字显示
	private List sqlList = new ArrayList();//记录需要执行的sql语句
	private BillDialog billDialog;//
	private HashMap billvoMap = new HashMap();//记录选择的岗位的岗责信息，方便查看面板数据显示
	private List hasBillVo = new ArrayList();//记录已经加入的岗责记录

	@Override
	public void initialize() {
		billTree_corp = new BillTreePanel("PUB_CORP_DEPT_1"); //机构树
		billTree_corp.setMoveUpDownBtnVisiable(false); //
		billTree_corp.queryDataByCondition(null); //查询所有数据,要有权限过滤!!
		billTree_corp.addBillTreeSelectListener(this); //刷新事件监听!!

		billList_post = new BillListPanel("PUB_POST_CODE1"); //岗位!!
		billList_post.addBillListSelectListener(this); //

		billList_postduty = new BillListPanel("CMP_POSTDUTY_CODE1"); //岗位岗责!!

		if (isCanEdit()) {
			btn_post_insert = new WLTButton("新增"); //新增岗位!!企业内控弄成Bom版时，岗位和岗位职责一起维护，可以减少一个菜单，故增加岗位维护功能【李春娟/2013-09-13】
			btn_post_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); //修改岗位
			btn_post_delete = new WLTButton("删除"); //删除岗位及岗责
			btn_duty = new WLTButton("维护岗位手册");
			btn_post_insert.addActionListener(this); //
			btn_post_update.addActionListener(this);
			btn_post_delete.addActionListener(this);
			btn_duty.addActionListener(this);
			billList_post.addBatchBillListButton(new WLTButton[] { btn_post_insert, btn_post_update, btn_post_delete, btn_duty }); //
			billList_post.repaintBillListButton();

			btn_insert = new WLTButton("新增"); //
			btn_insert.addActionListener(this); //
			btn_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); //修改
			btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE); //删除
			btn_seq = new WLTButton("排序");//增加职责排序按钮【李春娟/2014-12-16】
			btn_seq.addActionListener(this);
			btn_import = new WLTButton("导入");
			btn_import.addActionListener(this);
			billList_postduty.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_seq,btn_import }); //批量设置按钮!!!
			billList_postduty.repaintBillListButton(); //
		}

		WLTSplitPane split1 = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, billList_post, billList_postduty); //
		split1.setDividerLocation(250); //

		WLTSplitPane split2 = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, billTree_corp, split1); //
		split2.setDividerLocation(230);
		this.add(split2); //
	}

	/**
	 * 机构树选择变化事件!!
	 */
	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		billList_post.clearTable(); //
		billList_postduty.clearTable(); //
		BillVO billVO = _event.getCurrSelectedVO(); //
		if (billVO == null) {
			return;
		}
		String str_deptid = billVO.getStringValue("id"); //机构树的id.
		billList_post.QueryDataByCondition("deptid='" + tbUtil.getNullCondition(str_deptid) + "'"); //
	}

	/**
	 * 岗位列表选择变化事件!!
	 */
	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		if (_event.getSource() == billList_post) {
			billList_postduty.clearTable(); //
			BillVO billVO = _event.getCurrSelectedVO(); //
			if (billVO == null) {
				return;
			}
			String str_postid = billVO.getStringValue("id"); //岗位id
			billList_postduty.QueryDataByCondition("postid='" + tbUtil.getNullCondition(str_postid) + "'"); //刷新岗位岗责!!
		} else if (_event.getSource() == list_post) {
			list_postduty.clearTable(); //
			BillVO billVO = _event.getCurrSelectedVO(); //
			if (billVO == null) {
				return;
			}
			list_postduty.QueryDataByCondition("postid=" + billVO.getStringValue("id"));
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_post_insert) {
			onAddPost();
		} else if (e.getSource() == btn_post_update) {
			onEditlistpanel();
		} else if (e.getSource() == btn_post_delete) {
			onDeletePost();
		} else if (e.getSource() == btn_insert) {
			onInsert(); //新增操作!
		} else if (e.getSource() == btn_duty) {
			onEditPostDuty();
		} else if (e.getSource() == btn_import) {
			onImportDate();
		} else if (e.getSource() == btn_addduty) {
			onAddDuty();
		} else if (e.getSource() == btn_showduty) {
			onShowDuty();
		} else if (e.getSource() == btn_confirm) {
			onConfirmdate();
		} else if (e.getSource() == btn_cancel) {
			onCancelDate();
		} else if (e.getSource() == btn_seq) {
			onSeqDuty();
		}
	}

	public void onAddPost() {
		BillVO vo = billTree_corp.getSelectedVO();
		if (vo == null) {
			MessageBox.show(this, "请选择一个机构"); //
			return;
		}
		BillCardPanel billCardPanel = new BillCardPanel(billList_post.getTempletVO());
		billCardPanel.setEditableByInsertInit();
		billCardPanel.insertRow();
		billCardPanel.setValueAt("deptid", new RefItemVO(vo.getPkValue(), "", vo.getStringValue("name")));
		BillCardDialog billCardDialog = new BillCardDialog(billList_post, billCardPanel.getTempletVO().getTempletname() + "-新增", billCardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);
		billCardDialog.setVisible(true);
		if (billCardDialog.getCloseType() == 1) {
			int li_newrow = billList_post.newRow(false); //
			billList_post.setBillVOAt(li_newrow, billCardPanel.getBillVO(), false);
			billList_post.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); //设置列表该行的数据为初始化状态.
			billList_post.setSelectedRow(li_newrow); //
		}
	}

	public void onEditlistpanel() {
		BillVO vo = billList_post.getSelectedBillVO();
		if (vo == null) {
			MessageBox.showSelectOne(billList_post); //
			return;
		}
		BillCardPanel billCardPanel = new BillCardPanel(billList_post.getTempletVO());
		billCardPanel.setEditableByEditInit();
		billCardPanel.setBillVO(vo);
		BillCardDialog billCardDialog = new BillCardDialog(billList_post, billCardPanel.getTempletVO().getTempletname() + "-修改", billCardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		billCardDialog.setVisible(true);
		if (billCardDialog.getCloseType() == 1) {
			billList_post.refreshCurrSelectedRow();
		}

	}

	private void onDeletePost() {
		BillVO billVO = billList_post.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, "请选择一个岗位进行此操作!"); //
			return;
		}
		boolean result = MessageBox.confirm(billList_post, "确定删除[岗位]以及其对应的[岗位岗责]?");
		if (result) {
			String postid = billVO.getStringValue("id");
			String sql1 = "delete from pub_post where id = " + postid;
			String sql2 = "delete from cmp_postduty where postid=" + postid;
			try {
				UIUtil.executeBatchByDS(null, new String[] { sql1, sql2 });
				MessageBox.show(billList_post, "删除操作成功.");
			} catch (Exception e) {
				MessageBox.show(billList_post, "删除操作失败.");
				e.printStackTrace();
			}
		}
		billList_post.refreshData();
		billList_postduty.clearTable();
	}

	/**
	 * 新增岗位岗责!!
	 */
	private void onInsert() {
		BillVO billVO = billList_post.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, "请选择一个岗位进行此操作!"); //
			return;
		}
		HashMap defaultValueMap = new HashMap(); //
		defaultValueMap.put("deptid", billVO.getStringValue("deptid")); //
		defaultValueMap.put("postid", billVO.getStringValue("id")); //
		billList_postduty.doInsert(defaultValueMap); //新增岗位岗责!!!
	}

	/**
	 * 导入岗位岗责
	 */
	private void onImportDate() {
		BillVO billVO = billList_post.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, "请选择一个岗位进行此操作!"); //
			return;
		}
		tempMap = 0;
		sqlList.clear();
		billvoMap.clear();
		hasBillVo.clear();
		BillVO[] billVOs = billList_postduty.getAllBillVOs();
		if (billVOs != null) {
			for (int i = 0; i < billVOs.length; i++) {
				billvoMap.put(i, billVOs[i]);
				hasBillVo.add(billVOs[i].getStringValue("id"));
			}
			tempMap = billVOs.length;
		}
		billDialog = new BillDialog(billList_postduty, "导入岗位岗责及任务", 800, 700);
		billDialog.add(getCenterPanel(), BorderLayout.CENTER); //添加分割面板
		billDialog.add(getSouthPanel(), BorderLayout.SOUTH);//添加按钮面板
		billDialog.setVisible(true);

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

	private WLTSplitPane getCenterPanel() {
		list_post = new BillListPanel("PUB_POST_CODE2");
		list_post.setTitleLabelText("岗位组");
		list_post.setItemVisible("post_status", false);//不想新增模板了，为了不影响其他页面，代码设置一下隐藏岗位状态和显示岗位描述
		list_post.setItemVisible("refpostid", false);
		list_post.setItemVisible("descr", true);
		list_post.setQuickQueryPanelVisiable(true);
		list_post.setDataFilterCustCondition("deptid is null");
		list_post.QueryDataByCondition(" 1=1 ");
		list_post.setItemWidth("code", 200);//不想新增模板了，为了不影响其他页面，代码设置一下列宽
		list_post.setItemWidth("name", 200);
		list_post.setItemWidth("descr", 200);
		btn_addduty = new WLTButton("加入", "office_199.gif");
		btn_showduty = new WLTButton("查看(" + tempMap + ")", "office_062.gif");
		list_postduty = new BillListPanel("CMP_POSTDUTY_CODE1");
		list_postduty.setTitleLabelText("请在本列表中选择岗位职责及任务点击【加入】");
		list_postduty.getTitleLabel().setForeground(Color.BLUE);
		btn_addduty.addActionListener(this);
		btn_showduty.addActionListener(this);
		list_postduty.addBatchBillListButton(new WLTButton[] { btn_addduty, btn_showduty });
		list_postduty.repaintBillListButton();
		list_post.getQuickQueryPanel().addBillQuickActionListener(new ActionListener() {//查询事件处理
					public void actionPerformed(ActionEvent e) {
						BillQueryPanel pan = list_post.getQuickQueryPanel();
						list_post.QueryData(pan.getQuerySQL(pan.getAllQuickQueryCompents()));
						list_postduty.clearTable();//清空岗责记录
					}
				});
		list_post.addBillListSelectListener(this);
		WLTSplitPane split1 = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, list_post, list_postduty); //
		split1.setDividerLocation(330); //
		return split1;
	}

	public boolean isCanEdit() {
		return true;
	}

	/**
	 * 取消按钮事件处理
	 * @author 张营闯 2013-07-05
	 * */
	private void onCancelDate() {
		sqlList.clear();
		billDialog.dispose();

	}

	/**
	 * 确定按钮事件处理
	 * @author 张营闯 2013-07-05
	 * */
	private void onConfirmdate() {
		if (sqlList.size() > 0) {
			try {
				UIUtil.executeBatchByDS(null, sqlList);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		billDialog.dispose();
		sqlList.clear();
		billList_postduty.refreshData();
	}

	/**
	 * 加入按钮事件处理
	 * @author 张营闯 2013-07-05
	 * */
	private void onAddDuty() {
		BillVO[] billVOs = list_postduty.getSelectedBillVOs();
		BillVO billVO = billList_post.getSelectedBillVO();
		if (billVOs == null || billVOs.length < 1) {
			MessageBox.showSelectOne(list_postduty);
			return;
		}
		int hasAdd = 0;//记录加入的记录数目，与选择的记录个数对比，判断是否已全部加入
		for (int i = 0; i < billVOs.length; i++) {
			try {
				if (hasBillVo.contains(billVOs[i].getStringValue("id"))) {//判断此记录是否已经加入
					hasAdd++;
					continue;
				}
				InsertSQLBuilder sqlbuilder = new InsertSQLBuilder("cmp_postduty");
				String id = UIUtil.getSequenceNextValByDS(null, "s_cmp_postduty");
				sqlbuilder.putFieldValue("id", id);
				sqlbuilder.putFieldValue("deptid", billVO.getStringValue("deptid"));
				sqlbuilder.putFieldValue("postid", billVO.getStringValue("id"));
				sqlbuilder.putFieldValue("dutydescr", billVOs[i].getStringValue("dutydescr"));
				sqlbuilder.putFieldValue("dutyname", billVOs[i].getStringValue("dutyname"));
				sqlbuilder.putFieldValue("task", billVOs[i].getStringValue("task"));
				sqlbuilder.putFieldValue("frequency", billVOs[i].getStringValue("frequency"));
				sqlbuilder.putFieldValue("usetime", billVOs[i].getStringValue("usetime"));
				sqlbuilder.putFieldValue("wprocessrequire", billVOs[i].getStringValue("wprocessrequire"));
				sqlbuilder.putFieldValue("operateids", billVOs[i].getStringValue("operateids"));
				sqlbuilder.putFieldValue("practiceids", billVOs[i].getStringValue("practiceids"));
				sqlList.add(sqlbuilder.getSQL());
				tempMap++;//查看按钮上显示的数字自增
				billvoMap.put(billvoMap.size(), billVOs[i]);//方便查看按钮点开的面板显示数据
				hasBillVo.add(billVOs[i].getStringValue("id"));//记录此次加入的记录的id
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (hasAdd == billVOs.length && hasAdd == 1) {
			MessageBox.show(list_postduty, "该记录已加入,请勿重复加入!");
		} else if (hasAdd == billVOs.length) {
			MessageBox.show(list_postduty, "已全部加入,请勿重复加入!");
		} else {
			MessageBox.show(list_postduty, "加入成功");
		}
		btn_showduty.setText("查看(" + tempMap + ")");//重置查看按钮的数字
	}

	/**
	 * 查看按钮事件处理
	 * @author 张营闯 2013-07-05
	 * */
	private void onShowDuty() {
		if (tempMap == 0) {
			MessageBox.show(billDialog, "请先加入再进行查看!");
			return;
		}
		BillListDialog billdialog = new BillListDialog(list_postduty, "所有关联的岗责", "CMP_POSTDUTY_CODE1", 850, 550);
		BillListPanel showListPanel = billdialog.getBilllistPanel();
		for (int i = 0; i < billvoMap.size(); i++) {
			showListPanel.addRow((BillVO) billvoMap.get(i));
		}
		billdialog.getBtn_confirm().setVisible(false);
		billdialog.getBtn_cancel().setText("关闭");
		billdialog.getBtn_cancel().setToolTipText("关闭");
		billdialog.setVisible(true);

	}

	/*
	 * 通过环节操作要求 来修改岗责信息.佛山提出
	 */
	private void onEditPostDuty() {
		BillVO postvo = billList_post.getSelectedBillVO();
		if (postvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		PostDutyDetailWKDialog dialog = new PostDutyDetailWKDialog(this, "岗位职责及任务维护", 1020, 650, postvo);
		dialog.locationToCenterPosition();
		dialog.setVisible(true);
		billList_postduty.refreshData();
	}

	/**
	 * 职责排序【李春娟/2014-12-16】
	 */
	private void onSeqDuty() {
		SeqListDialog dialog_post = new SeqListDialog(this, "职责排序", billList_postduty.getTempletVO(), billList_postduty.getAllBillVOs());
		dialog_post.setVisible(true);
		if (dialog_post.getCloseType() == 1) {//如果点击确定返回，则需要刷新一下页面
			billList_postduty.refreshData(); //
		}
	}
}
