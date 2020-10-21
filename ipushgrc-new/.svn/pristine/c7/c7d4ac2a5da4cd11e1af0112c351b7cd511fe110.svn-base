package com.pushworld.ipushgrc.ui.cmpscore.p020;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JTabbedPane;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 违规积分处理
 * @author p17
 *getTab("待处理",getList("CMP_SCORE_RECORD_CODE"),"已处理",getList("CMP_SCORE_RECORD_CODE_DONE"))
 */
public class CmpScorePunishWKPanel extends AbstractWorkPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6366498309289719225L;

	private WLTButton deal = null;//积分处理按钮
	private BillListPanel waitlist = null;//列表

	private BillListPanel donelist = null;
	private WLTButton edit = null;
	private WLTButton view = null;

	@Override
	public void initialize() {
		waitlist = new BillListPanel("CMP_SCORE_RECORD_CODE");
		donelist = new BillListPanel("CMP_SCORE_RECORD_CODE_DONE");

		deal = new WLTButton("积分处理");
		deal.addActionListener(this);
		waitlist.addBatchBillListButton(new WLTButton[] { deal });
		waitlist.repaintBillListButton();

		edit = new WLTButton("修改");
		edit.addActionListener(this);
		view = new WLTButton("浏览");
		view.addActionListener(this);
		donelist.addBatchBillListButton(new WLTButton[] { edit, view });
		donelist.repaintBillListButton();

		JTabbedPane pane = new JTabbedPane();
		pane.addTab("待处理", waitlist);
		pane.addTab("已处理", donelist);
		this.add(pane);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == deal) {
			this.onScoreDeal();
		}
		if (e.getSource() == edit) {
			onEdit();
		}
		if (e.getSource() == view) {
			onView();
		}
	}

	private void onView() {
		BillVO vo = donelist.getSelectedBillVO();
		if (vo == null) {
			MessageBox.showSelectOne(donelist);
			return;
		}

		final String recordid = vo.getStringValue("id");
		BillCardPanel dcard = new BillCardPanel(waitlist.getTempletVO());

		BillCardPanel recordCard = new BillCardPanel(waitlist.templetVO);//显示扣分信息--分割器上方面板
		recordCard.setEditable(false);
		recordCard.setBillVO(vo);

		final BillCardPanel dealCard = new BillCardPanel("CMP_PUNISH_RECORD_CODE1");//处理信息---分割器下方面板
		dealCard.setEditable(false);

		dealCard.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT);
		dealCard.queryDataByCondition(" record_id = " + recordid);

		WLTSplitPane split = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, recordCard, dealCard);//分割器
		split.setDividerLocation(150);
		dcard.add(split);//添加分割器

		final BillCardDialog dialog = new BillCardDialog(donelist, "积分处理", dcard, WLTConstants.BILLDATAEDITSTATE_INIT);

		dialog.setVisible(true);

		waitlist.refreshData();

	}

	private void onEdit() {
		BillVO vo = donelist.getSelectedBillVO();
		if (vo == null) {
			MessageBox.showSelectOne(donelist);
			return;
		}

		final String recordid = vo.getStringValue("id");
		BillCardPanel dcard = new BillCardPanel(waitlist.getTempletVO());

		BillCardPanel recordCard = new BillCardPanel(waitlist.templetVO);//显示扣分信息--分割器上方面板
		recordCard.setEditable(false);
		recordCard.setBillVO(vo);

		final BillCardPanel dealCard = new BillCardPanel("CMP_PUNISH_RECORD_CODE1");//处理信息---分割器下方面板
		dealCard.setEditable(false);
		dealCard.setEditable("DEAL_TYPE", true);//处理情况能编辑
		dealCard.setEditable("DESCRIPE", true);//备注可以编辑
		dealCard.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dealCard.queryDataByCondition(" record_id = " + recordid);

		WLTSplitPane split = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, recordCard, dealCard);//分割器
		split.setDividerLocation(150);
		dcard.add(split);//添加分割器

		final BillCardDialog dialog = new BillCardDialog(donelist, "积分处理", dcard, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.getBtn_confirm().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					dealCard.stopEditing();
					if (!dealCard.checkValidate()) {
						return;
					}
					String descripe = dealCard.getBillVO().getStringValue("DESCRIPE");
					String deal_type = dealCard.getBillVO().getStringValue("DEAL_TYPE");
					String updateSql = "update  " + dealCard.getTempletVO().getTablename() + "  set descripe ='" + descripe + "' ,deal_type='" + deal_type + "' where id =" + dealCard.getBillVO().getStringValue("id");
					UIUtil.executeUpdateByDS(dealCard.getDataSourceName(), updateSql);
					dialog.dispose();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		dialog.getBtn_save().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					dealCard.stopEditing();
					if (!dealCard.checkValidate()) {
						return;
					}
					String descripe = dealCard.getBillVO().getStringValue("DESCRIPE");
					String deal_type = dealCard.getBillVO().getStringValue("DEAL_TYPE");
					String updateSql = "update  " + dealCard.getTempletVO().getTablename() + "  set descripe ='" + descripe + "' ,deal_type='" + deal_type + "' where id =" + dealCard.getBillVO().getStringValue("id");
					UIUtil.executeUpdateByDS(dealCard.getDataSourceName(), updateSql);
					MessageBox.show(dealCard, "数据保存成功！");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		dialog.setVisible(true);

		waitlist.refreshData();
	}

	/**
	 * 积分处理--对话框
	 * @throws Exception 
	 * @throws WLTRemoteException 
	 */
	@SuppressWarnings("unchecked")
	private void onScoreDeal() {
		final BillVO vo = waitlist.getSelectedBillVO();
		if (vo == null) {
			MessageBox.showSelectOne(waitlist);
			return;
		}

		final String recordid = vo.getStringValue("id");
		BillCardPanel dcard = new BillCardPanel(waitlist.getTempletVO());

		BillCardPanel recordCard = new BillCardPanel(waitlist.templetVO);//显示扣分信息--分割器上方面板
		recordCard.setEditable(false);
		recordCard.setBillVO(vo);

		final BillCardPanel dealCard = new BillCardPanel("CMP_PUNISH_RECORD_CODE1");//处理信息---分割器下方面板

		dealCard.setEditableByInsertInit();
		String id = null;
		try {
			id = UIUtil.getSequenceNextValByDS(null, "S_CMP_PUNISH_RECORD");//这里先前取的ID的sequence值 ，即pub_sequence 中有条记录的sename='S_PUNISH_RECORD'，更新代码或需要执行sql：update pub_sequence set sename='S_CMP_PUNISH_RECORD' where sename='S_PUNISH_RECORD'【李春娟/2012-07-17】
		} catch (WLTRemoteException e2) {
			e2.printStackTrace();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		if (id == null || id.equals("")) //原有的方法 String.isEmpty()是jdk1.6的。在WebSphere6的jdk5上会报错。
			return;
		HashMap map = new HashMap();
		map.put("ID", new StringItemVO(id));//主键
		map.put("CONFIRM_ID", new StringItemVO(vo.getStringValue("code")));//实时确认单编号
		map.put("RECORD_ID", new StringItemVO(recordid));//记录id
		map.put("PUNISH_USER", new StringItemVO(vo.getStringValue("userid")));//被扣分人
		map.put("REAL_SCORE", new StringItemVO(vo.getStringValue("resultscore")));//实际扣分
		map.put("TOTAL_SCORE", new StringItemVO(vo.getStringValue("totalscore")));//累计扣分
		map.put("CREATE_USER", new RefItemVO(ClientEnvironment.getInstance().getLoginUserID(), null, ClientEnvironment.getInstance().getLoginUserName()));//创建人
		map.put("CREATE_DATE", new RefItemVO(UIUtil.getCurrDate(), UIUtil.getCurrDate(), UIUtil.getCurrDate()));//创建时间
		map.put("CREATE_DEPT", new RefItemVO(ClientEnvironment.getInstance().getLoginUserDeptId(), null, ClientEnvironment.getInstance().getLoginUserDeptId()));//创建机构
		dealCard.setValue(map);//设置默认值

		WLTSplitPane split = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, recordCard, dealCard);//分割器
		split.setDividerLocation(150);
		dcard.add(split);//添加分割器

		final BillCardDialog dialog = new BillCardDialog(waitlist, "积分处理", dcard, WLTConstants.BILLDATAEDITSTATE_INSERT);
		dialog.getBtn_confirm().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					dealCard.stopEditing();
					//					dealCard.updateData();
					String insertSql = dealCard.getInsertSQL();
					String updateSql = "update cmp_score_record set sendstate = '6' where id = " + recordid;
					String updateSql2 = "update " + dealCard.getTempletVO().getSavedtablename() + " set TOTAL_SCORE = " + vo.getStringValue("totalscore") + "where punish_user = " + vo.getStringValue("userid");
					UIUtil.executeBatchByDS(null, new String[] { insertSql, updateSql, updateSql2 });//更新记录状态
					dialog.dispose();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		dialog.getBtn_save().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					dealCard.stopEditing();
					//					dealCard.updateData();
					String insertSql = dealCard.getInsertSQL();
					String updateSql = "update cmp_score_record set sendstate = '6' where id = " + recordid;
					UIUtil.executeBatchByDS(null, new String[] { insertSql, updateSql });//更新记录状态
					MessageBox.show(dialog, "保存成功!");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		dialog.setVisible(true);

		waitlist.refreshData();
	}

}
