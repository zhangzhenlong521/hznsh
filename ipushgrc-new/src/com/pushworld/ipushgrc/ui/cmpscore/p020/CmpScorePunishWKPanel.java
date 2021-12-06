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
 * Υ����ִ���
 * @author p17
 *getTab("������",getList("CMP_SCORE_RECORD_CODE"),"�Ѵ���",getList("CMP_SCORE_RECORD_CODE_DONE"))
 */
public class CmpScorePunishWKPanel extends AbstractWorkPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6366498309289719225L;

	private WLTButton deal = null;//���ִ���ť
	private BillListPanel waitlist = null;//�б�

	private BillListPanel donelist = null;
	private WLTButton edit = null;
	private WLTButton view = null;

	@Override
	public void initialize() {
		waitlist = new BillListPanel("CMP_SCORE_RECORD_CODE");
		donelist = new BillListPanel("CMP_SCORE_RECORD_CODE_DONE");

		deal = new WLTButton("���ִ���");
		deal.addActionListener(this);
		waitlist.addBatchBillListButton(new WLTButton[] { deal });
		waitlist.repaintBillListButton();

		edit = new WLTButton("�޸�");
		edit.addActionListener(this);
		view = new WLTButton("���");
		view.addActionListener(this);
		donelist.addBatchBillListButton(new WLTButton[] { edit, view });
		donelist.repaintBillListButton();

		JTabbedPane pane = new JTabbedPane();
		pane.addTab("������", waitlist);
		pane.addTab("�Ѵ���", donelist);
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

		BillCardPanel recordCard = new BillCardPanel(waitlist.templetVO);//��ʾ�۷���Ϣ--�ָ����Ϸ����
		recordCard.setEditable(false);
		recordCard.setBillVO(vo);

		final BillCardPanel dealCard = new BillCardPanel("CMP_PUNISH_RECORD_CODE1");//������Ϣ---�ָ����·����
		dealCard.setEditable(false);

		dealCard.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT);
		dealCard.queryDataByCondition(" record_id = " + recordid);

		WLTSplitPane split = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, recordCard, dealCard);//�ָ���
		split.setDividerLocation(150);
		dcard.add(split);//��ӷָ���

		final BillCardDialog dialog = new BillCardDialog(donelist, "���ִ���", dcard, WLTConstants.BILLDATAEDITSTATE_INIT);

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

		BillCardPanel recordCard = new BillCardPanel(waitlist.templetVO);//��ʾ�۷���Ϣ--�ָ����Ϸ����
		recordCard.setEditable(false);
		recordCard.setBillVO(vo);

		final BillCardPanel dealCard = new BillCardPanel("CMP_PUNISH_RECORD_CODE1");//������Ϣ---�ָ����·����
		dealCard.setEditable(false);
		dealCard.setEditable("DEAL_TYPE", true);//��������ܱ༭
		dealCard.setEditable("DESCRIPE", true);//��ע���Ա༭
		dealCard.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dealCard.queryDataByCondition(" record_id = " + recordid);

		WLTSplitPane split = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, recordCard, dealCard);//�ָ���
		split.setDividerLocation(150);
		dcard.add(split);//��ӷָ���

		final BillCardDialog dialog = new BillCardDialog(donelist, "���ִ���", dcard, WLTConstants.BILLDATAEDITSTATE_UPDATE);
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
					MessageBox.show(dealCard, "���ݱ���ɹ���");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		dialog.setVisible(true);

		waitlist.refreshData();
	}

	/**
	 * ���ִ���--�Ի���
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

		BillCardPanel recordCard = new BillCardPanel(waitlist.templetVO);//��ʾ�۷���Ϣ--�ָ����Ϸ����
		recordCard.setEditable(false);
		recordCard.setBillVO(vo);

		final BillCardPanel dealCard = new BillCardPanel("CMP_PUNISH_RECORD_CODE1");//������Ϣ---�ָ����·����

		dealCard.setEditableByInsertInit();
		String id = null;
		try {
			id = UIUtil.getSequenceNextValByDS(null, "S_CMP_PUNISH_RECORD");//������ǰȡ��ID��sequenceֵ ����pub_sequence ��������¼��sename='S_PUNISH_RECORD'�����´������Ҫִ��sql��update pub_sequence set sename='S_CMP_PUNISH_RECORD' where sename='S_PUNISH_RECORD'�����/2012-07-17��
		} catch (WLTRemoteException e2) {
			e2.printStackTrace();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		if (id == null || id.equals("")) //ԭ�еķ��� String.isEmpty()��jdk1.6�ġ���WebSphere6��jdk5�ϻᱨ��
			return;
		HashMap map = new HashMap();
		map.put("ID", new StringItemVO(id));//����
		map.put("CONFIRM_ID", new StringItemVO(vo.getStringValue("code")));//ʵʱȷ�ϵ����
		map.put("RECORD_ID", new StringItemVO(recordid));//��¼id
		map.put("PUNISH_USER", new StringItemVO(vo.getStringValue("userid")));//���۷���
		map.put("REAL_SCORE", new StringItemVO(vo.getStringValue("resultscore")));//ʵ�ʿ۷�
		map.put("TOTAL_SCORE", new StringItemVO(vo.getStringValue("totalscore")));//�ۼƿ۷�
		map.put("CREATE_USER", new RefItemVO(ClientEnvironment.getInstance().getLoginUserID(), null, ClientEnvironment.getInstance().getLoginUserName()));//������
		map.put("CREATE_DATE", new RefItemVO(UIUtil.getCurrDate(), UIUtil.getCurrDate(), UIUtil.getCurrDate()));//����ʱ��
		map.put("CREATE_DEPT", new RefItemVO(ClientEnvironment.getInstance().getLoginUserDeptId(), null, ClientEnvironment.getInstance().getLoginUserDeptId()));//��������
		dealCard.setValue(map);//����Ĭ��ֵ

		WLTSplitPane split = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, recordCard, dealCard);//�ָ���
		split.setDividerLocation(150);
		dcard.add(split);//��ӷָ���

		final BillCardDialog dialog = new BillCardDialog(waitlist, "���ִ���", dcard, WLTConstants.BILLDATAEDITSTATE_INSERT);
		dialog.getBtn_confirm().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					dealCard.stopEditing();
					//					dealCard.updateData();
					String insertSql = dealCard.getInsertSQL();
					String updateSql = "update cmp_score_record set sendstate = '6' where id = " + recordid;
					String updateSql2 = "update " + dealCard.getTempletVO().getSavedtablename() + " set TOTAL_SCORE = " + vo.getStringValue("totalscore") + "where punish_user = " + vo.getStringValue("userid");
					UIUtil.executeBatchByDS(null, new String[] { insertSql, updateSql, updateSql2 });//���¼�¼״̬
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
					UIUtil.executeBatchByDS(null, new String[] { insertSql, updateSql });//���¼�¼״̬
					MessageBox.show(dialog, "����ɹ�!");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		dialog.setVisible(true);

		waitlist.refreshData();
	}

}
