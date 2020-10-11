package cn.com.pushworld.salary.ui.posteval.p030;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListButtonActinoAdapter;
import cn.com.infostrategy.ui.mdata.BillListButtonClickedEvent;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_ChildTable;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

/**
 * 岗位价值评估计划维护【李春娟/2013-10-21】
 * 计划状态：未评估、评估中、评估结束
 * 
 */
public class PostEvalPlanWKPanel extends AbstractWorkPanel implements ActionListener {
	private static final long serialVersionUID = 1803665179022243678L;
	private BillListPanel planListPanel;
	private WLTButton btn_add, btn_edit, btn_delete, btn_startup, btn_copy, btn_moveup, btn_movedown;
	private ImageIcon iconUp, iconDown;

	public void initialize() {
		planListPanel = new BillListPanel("SAL_POST_EVAL_PLAN_LCJ_E01");
		btn_add = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT);
		btn_edit = new WLTButton("修改");
		btn_delete = new WLTButton("删除");
		btn_startup = new WLTButton("启动计划");
		btn_copy = new WLTButton("复制计划");
		btn_edit.addActionListener(this);
		btn_delete.addActionListener(this);
		btn_startup.addActionListener(this);
		btn_copy.addActionListener(this);
		planListPanel.addBatchBillListButton(new WLTButton[] { btn_add, btn_edit, btn_delete, btn_startup, btn_copy, WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD) });
		planListPanel.repaintBillListButton();

		planListPanel.addBillListButtonActinoListener(new BillListButtonActinoAdapter() {
			public void onBillListAddButtonClicking(BillListButtonClickedEvent _event) throws Exception {
				addMoveBtn(_event.getCardPanel());
			}
		});

		this.add(planListPanel);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_edit) {
			onEdit();
		} else if (e.getSource() == btn_delete) {
			onDelete();
		} else if (e.getSource() == btn_startup) {
			onStartup();
		} else if (e.getSource() == btn_copy) {
			onCopyPlan();
		}
	}

	/**
	 * 计划修改逻辑
	 */
	private void onEdit() {
		BillVO billVO = planListPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(planListPanel);
			return;
		}
		String status = billVO.getStringValue("status");
		if (status == null || "".equals(status) || "未评估".equals(status)) {
			BillCardPanel cardPanel = new BillCardPanel(planListPanel.getTempletVO());
			cardPanel.setBillVO(billVO); //
			this.addMoveBtn(cardPanel);//增加上下移动的按钮
			BillCardDialog dialog = new BillCardDialog(planListPanel, planListPanel.getTempletVO().getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
			dialog.setVisible(true); //
			if (dialog.getCloseType() == 1) {
				if (planListPanel.getSelectedRow() == -1) {//袁江晓 20130415添加，主要解决刷新模板时候可能出现的错
				} else {
					planListPanel.setBillVOAt(planListPanel.getSelectedRow(), dialog.getBillVO());
					planListPanel.setRowStatusAs(planListPanel.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT);
				}
			}
		} else {
			MessageBox.show(this, "该计划状态为[" + status + "],不能修改.");
		}
	}

	/**
	 * 计划删除逻辑
	 */
	private void onDelete() {
		BillVO billVO = planListPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(planListPanel);
			return;
		}
		String planid = billVO.getStringValue("id");
		String status = billVO.getStringValue("status");
		if (status == null || "".equals(status) || "未评估".equals(status)) {
			if (!MessageBox.confirmDel(this)) {
				return; //
			}
			planListPanel.doDelete(true);
		} else if (MessageBox.confirm(this, "[重要提示]该操作将删除已选中计划的所有评估结果,您确定要删除吗?")) {
			planListPanel.doDelete(true);
		} else {
			return;
		}
		try {
			ArrayList sqlList = new ArrayList();
			sqlList.add("delete from sal_post_eval_target_copy where planid=" + planid);
			sqlList.add("delete from sal_post_eval_plan_post where planid=" + planid);
			sqlList.add("delete from sal_post_eval_score where planid=" + planid);
			sqlList.add("delete from sal_post_eval_user_copy where planid=" + planid);
			sqlList.add("delete from sal_post_eval_score_statistics where planid=" + planid);
			sqlList.add("delete from sal_post_eval_score_total where planid=" + planid);
			UIUtil.executeBatchByDS(null, sqlList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 计划启动逻辑
	 */
	private void onStartup() {
		BillVO billVO = planListPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(planListPanel);
			return;
		}
		String status = billVO.getStringValue("status");
		if (status == null || "".equals(status) || "未评估".equals(status)) {//只有未评估的才可启动
			try {
				String planid = UIUtil.getStringValueByDS(null, "select count(id) from sal_post_eval_plan where status ='评估中'");
				if (planid != null && Integer.parseInt(planid) > 0) {
					MessageBox.show(this, "还有未结束的评估,不能进行此操作!");
					return;
				}
				String[] targetnames = UIUtil.getStringArrayFirstColByDS(null, "select targetname from sal_post_eval_target where id not in(select distinct(parentid) from sal_post_eval_target where parentid is not null) and weight is null");
				if (targetnames != null && targetnames.length > 0) {//检查末级节点是否都有权重，如果有没设置权重的指标，则必须先设置权重
					StringBuffer sb_names = new StringBuffer("请先维护以下指标的权重：\r\n");
					for (int i = 0; i < targetnames.length; i++) {
						sb_names.append(targetnames[i]);
						sb_names.append(",\r\n");
					}
					MessageBox.showTextArea(planListPanel, sb_names.substring(0, sb_names.length() - 3));
					return;
				}
				new SplashWindow(this, new AbstractAction() {
					public void actionPerformed(ActionEvent e) {
						try {
							createPostEvalTable();
						} catch (Exception e1) {
							e1.printStackTrace();
							MessageBox.showException(planListPanel, e1);
						}//如果末级节点都有权重，则生成岗位评估表记录
					}
				});
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			MessageBox.show(this, "该计划状态为[" + status + "],不能重新启动.");
		}
	}

	/**
	 * 复制计划
	 */
	private void onCopyPlan() {
		BillVO billVO = planListPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(planListPanel);
			return;
		}
		BillCardPanel cardPanel = new BillCardPanel(planListPanel.getTempletVO()); //创建一个卡片面板
		cardPanel.insertRow(); //卡片新增一行!
		cardPanel.setValueAt("planname", new StringItemVO(billVO.getStringValue("planname") + "_复制"));
		cardPanel.setValueAt("status", new ComBoxItemVO("未评估", "", "未评估"));

		String selected_planid = billVO.getStringValue("id");
		String new_planid = cardPanel.getBillVO().getStringValue("id");//新增的记录主键
		ArrayList childIDList = new ArrayList();
		try {
			//设置评分人及被评岗位记录
			HashVO[] postvos = UIUtil.getHashVoArrayByDS(null, "select * from sal_post_eval_plan_post where planid=" + selected_planid + " order by seq");
			if (postvos != null && postvos.length > 0) {
				InsertSQLBuilder sqlBuilder = new InsertSQLBuilder("sal_post_eval_plan_post");
				ArrayList sqlList = new ArrayList();//记录所有子表复制sql
				for (int i = 0; i < postvos.length; i++) {
					String new_id = UIUtil.getSequenceNextValByDS(null, "s_sal_post_eval_plan_post");
					childIDList.add(new_id);
					sqlBuilder.putFieldValue("id", new_id);
					sqlBuilder.putFieldValue("planid", new_planid);
					sqlBuilder.putFieldValue("evaluser", postvos[i].getStringValue("evaluser"));
					sqlBuilder.putFieldValue("userids", postvos[i].getStringValue("userids"));
					sqlBuilder.putFieldValue("evalrange", postvos[i].getStringValue("evalrange"));
					sqlBuilder.putFieldValue("postlist", postvos[i].getStringValue("postlist"));
					sqlBuilder.putFieldValue("remark", postvos[i].getStringValue("remark"));
					sqlBuilder.putFieldValue("seq", postvos[i].getStringValue("seq"));
					sqlList.add(sqlBuilder.getSQL());
				}
				UIUtil.executeBatchByDS(null, sqlList);//执行子表复制sql，这样在打开卡片时才能看到子表记录
			}
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.addMoveBtn(cardPanel);//增加上下移动的按钮
		cardPanel.setEditableByEditInit(); //设置卡片编辑状态为新增时的设置
		BillCardDialog dialog = new BillCardDialog(planListPanel, planListPanel.getTempletVO().getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); //弹出卡片新增框
		dialog.setVisible(true); //显示卡片窗口
		if (dialog.getCloseType() == 1) { //如是是点击确定返回!将则卡片中的数据赋给列表!
			int li_newrow = planListPanel.newRow(false); //
			planListPanel.setBillVOAt(li_newrow, dialog.getBillVO(), false);
			planListPanel.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); //设置列表该行的数据为初始化状态.
			planListPanel.setSelectedRow(li_newrow); //
		} else if (childIDList.size() > 0) {//如果没有点击【确定】而关闭窗口，则需要将子表信息清空
			try {
				UIUtil.executeUpdateByDS(null, "delete from sal_post_eval_plan_post where id in(" + TBUtil.getTBUtil().getInCondition(childIDList) + ")");
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 卡片增加上移下移按钮
	 * @param bc
	 */
	private void addMoveBtn(final BillCardPanel bc) {
		final CardCPanel_ChildTable childTable = ((CardCPanel_ChildTable) bc.getCompentByKey("postids"));
		final BillListPanel bl = childTable.getBillListPanel();
		if (iconDown == null) {
			iconDown = UIUtil.getImage("down1.gif");
			iconUp = new ImageIcon(TBUtil.getTBUtil().getImageRotate(iconDown.getImage(), 180)); //转180度!		
		}

		btn_moveup = new WLTButton("");
		btn_moveup.setIcon(iconUp);
		btn_moveup.setToolTipText("上移");
		btn_moveup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					new SalaryUIUtil().billListRowUp(bl);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		btn_movedown = new WLTButton("");
		btn_movedown.setIcon(iconDown);
		btn_movedown.setToolTipText("下移");
		btn_movedown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					new SalaryUIUtil().billListRowDown(bl);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		bl.getTitleLabel().setText("");
		bl.addCustButton(btn_moveup);
		bl.addCustButton(btn_movedown);
		childTable.getBtn_insert().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				childTable.onInsert();
				int li_rowcount = bl.getRowCount();
				for (int i = 0; i < li_rowcount; i++) {
					bl.setValueAt("" + (i + 1), i, "SEQ"); //
				}
				bl.saveData();//这里直接保存有一个弊端，如果窗口取消了，序号不能恢复。【李春娟/2013-11-22】
			}
		});
		bl.queryDataByCondition("planid=" + bc.getBillVO().getPkValue(), "seq");
	}

	/**
	 * 生成岗位评估表记录
	 */
	public void createPostEvalTable() throws Exception {
		BillTreePanel treePanel = new BillTreePanel("SAL_POST_EVAL_TARGET_LCJ_E01");
		treePanel.queryDataByCondition(null);
		Vector v_sqls = new Vector(); //
		DefaultMutableTreeNode[] allNodes = treePanel.getAllNodes(); //
		// 重置所有LinkCode
		for (int i = 1; i < 10; i++) { //假设有10层，这里的指标一般就2层。
			boolean bo_iffind = false; //
			for (int j = 0; j < allNodes.length; j++) { //遍历所有结点..
				if (allNodes[j].getLevel() == i) { //如果是第几层
					bo_iffind = true; //打上标记,表示已经发现了!
					DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) allNodes[j].getParent(); //取得父亲!!!
					int li_index = parentNode.getIndex(allNodes[j]); //判断我在父结点中的位置
					String str_currlinkcode = ("" + (10000 + li_index + 1)).substring(1, 5); //生成序号,四位数据
					String str_parlinkcode = ""; //
					if (!parentNode.isRoot()) {
						str_parlinkcode = treePanel.getBillVOFromNode(parentNode).getStringValue("linkcode");
					}
					String str_linkcode = str_parlinkcode + str_currlinkcode; //
					BillVO billVO = treePanel.getBillVOFromNode(allNodes[j]); //
					String str_billvolinkcode = billVO.getStringValue("linkcode"); //
					if (str_billvolinkcode == null || !str_billvolinkcode.equals(str_linkcode)) { //如果树中原来的数据为空或者不等于新的值
						v_sqls.add("update " + treePanel.getTempletVO().getSavedtablename() + " set linkcode='" + str_linkcode + "' where id='" + billVO.getPkValue() + "'"); //
						billVO.setObject("linkcode", new StringItemVO(str_linkcode)); //
						treePanel.setBillVO(allNodes[j], billVO); //
					}
				}
			}
			if (!bo_iffind) { //如果某一层一个也没发现,则退出循环..
				break;
			}
		}
		String planid = planListPanel.getSelectedBillVO().getStringValue("id");
		v_sqls.add("update sal_post_eval_plan set status='评估中' where id=" + planid);//修改当前计划的状态
		UIUtil.executeBatchByDS(null, v_sqls, false, false); //
		SalaryServiceIfc service = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
		service.createPostEvalTable(planid);//生成实际的岗位评估表记录，这一步很耗时
		planListPanel.refreshCurrSelectedRow();
		MessageBox.show(planListPanel, "操作成功!");
	}

}