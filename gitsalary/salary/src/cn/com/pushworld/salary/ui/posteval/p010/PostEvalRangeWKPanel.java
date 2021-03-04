package cn.com.pushworld.salary.ui.posteval.p010;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.ListSelectionModel;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.RowNumberItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.sysapp.corpdept.SeqListDialog;
import cn.com.pushworld.salary.ui.target.ChoosePostRefDialog;

/**
 * ��λ��ֵ������Χά�������/2013-10-24��
 * 
 */
public class PostEvalRangeWKPanel extends AbstractWorkPanel implements ActionListener, BillListSelectListener {
	private static final long serialVersionUID = 1803665179022243678L;
	private BillListPanel planListPanel;
	private BillListPanel postListPanel;
	private WLTButton btn_add, btn_edit, btn_delete, btn_copy, btn_seq, btn_addpost, btn_movepost;

	public void initialize() {
		planListPanel = new BillListPanel("SAL_POST_EVAL_RANGE_LCJ_E01");
		planListPanel.QueryDataByCondition(null);//ȫ�����
		planListPanel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);//��ѡ
		btn_add = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT);
		btn_add.addActionListener(this);
		btn_edit = new WLTButton("�޸�");
		btn_delete = new WLTButton("ɾ��");
		btn_copy = new WLTButton("����");
		btn_seq = new WLTButton("����");
		btn_edit.addActionListener(this);
		btn_delete.addActionListener(this);
		btn_copy.addActionListener(this);
		btn_seq.addActionListener(this);
		planListPanel.addBatchBillListButton(new WLTButton[] { btn_add, btn_edit, btn_delete, btn_copy, btn_seq });
		planListPanel.repaintBillListButton();
		planListPanel.addBillListSelectListener(this);

		postListPanel = new BillListPanel("PUB_POST_LCJ_Q01");
		postListPanel.setQuickQueryPanelVisiable(false);
		postListPanel.setRowNumberChecked(true);

		btn_addpost = new WLTButton("��Ӹ�λ", "office_163.gif");
		btn_movepost = new WLTButton("�Ƴ���λ", "office_165.gif");
		btn_addpost.addActionListener(this);
		btn_movepost.addActionListener(this);

		postListPanel.addBatchBillListButton(new WLTButton[] { btn_addpost, btn_movepost });
		postListPanel.repaintBillListButton();

		WLTSplitPane splitPane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, planListPanel, postListPanel);
		splitPane.setDividerLocation(430);
		this.add(splitPane);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_add) {
			onAdd();
		} else if (e.getSource() == btn_edit) {
			onEdit();
		} else if (e.getSource() == btn_delete) {
			onDelete();
		} else if (e.getSource() == btn_copy) {
			onCopy();
		} else if (e.getSource() == btn_seq) {
			onSeq();
		} else if (e.getSource() == btn_addpost) {
			onAddPost();
		} else if (e.getSource() == btn_movepost) {
			onRemovePost();
		}
	}

	/**
	 * ����    @��Ӫ����2013-11-13�� Ĭ�������ļ�¼�ŵ����һ�У������ֶ�sqlȡ��ǰ����1
	 * */
	private void onAdd() {
		String seq = "";
		try {
			seq = UIUtil.getStringValueByDS(null, "select max(seq) from sal_post_eval_range");
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (seq == null || "".equals(seq)) {
			seq = "0";
		}

		BillCardPanel cardpanel = new BillCardPanel(planListPanel.getTempletVO());
		String sqlindex = (Integer.parseInt(seq) + 1) + "";
		cardpanel.setLoaderBillFormatPanel(planListPanel.getLoaderBillFormatPanel()); //���б��BillFormatPanel�ľ��������Ƭ
		cardpanel.insertRow(); //��Ƭ����һ��!
		cardpanel.setValueAt("seq", new StringItemVO(sqlindex));
		cardpanel.setEditableByInsertInit(); //���ÿ�Ƭ�༭״̬Ϊ����ʱ������
		BillCardDialog cardDialog = new BillCardDialog(this, "����", cardpanel, WLTConstants.BILLDATAEDITSTATE_INSERT);
		cardDialog.setSaveBtnVisiable(false);
		cardDialog.setVisible(true);
		if (cardDialog.getCloseType() == 1) {
			int li_newrow = planListPanel.getRowCount(); //
			planListPanel.insertEmptyRow(li_newrow);
			planListPanel.setBillVOAt(li_newrow, cardDialog.getBillVO(), false);
			planListPanel.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); //�����б���е�����Ϊ��ʼ��״̬.
			planListPanel.setSelectedRow(li_newrow);
		}
	}

	/**
	 * ����
	 */
	private void onSeq() {
		SeqListDialog dialog_range = new SeqListDialog(this, "����", planListPanel.getTempletVO(), planListPanel.getAllBillVOs());
		dialog_range.getBilllistPanel().setQuickQueryPanelVisiable(false);
		dialog_range.setVisible(true);
		if (dialog_range.getCloseType() == 1) {//������ȷ�����أ�����Ҫˢ��һ��ҳ��
			planListPanel.refreshData(); //
			postListPanel.clearTable();
		}
	}

	/**
	 * ������Χ�޸��߼�
	 */
	private void onEdit() {
		BillVO billVO = planListPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(planListPanel);
			return;
		}

		String status = billVO.getStringValue("status");
		if (status == null || "".equals(status) || "δ����".equals(status)) {
			planListPanel.doEdit();
		} else {
			MessageBox.show(this, "��������Χ״̬Ϊ[" + status + "],�����޸�.");
		}
	}

	/**
	 * ������Χɾ���߼�
	 */
	private void onDelete() {
		BillVO billVO = planListPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(planListPanel);
			return;
		}
		String status = billVO.getStringValue("status");
		String seq = billVO.getStringValue("seq");
		if (status == null || "".equals(status)) {
			planListPanel.doDelete(false);
			String sql = "update sal_post_eval_range set seq = seq-1 where seq >= " + seq;//����ǰ��¼�Ժ�ļ�¼��seq�Զ���1
			try {
				UIUtil.executeUpdateByDS(null, sql);
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			postListPanel.clearTable();
			planListPanel.refreshData();
		} else {
			MessageBox.show(this, "��������Χ״̬Ϊ[" + status + "],����ɾ��.");
		}
	}

	/**
	 * ����������Χ
	 */
	private void onCopy() {
		BillVO billVO = planListPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(planListPanel);
			return;
		}

		HashMap hashMap = new HashMap();
		hashMap.put("rangename", billVO.getStringValue("rangename") + "_����");
		String seq = "";
		try {
			hashMap.put("postlist", TBUtil.getTBUtil().deepClone(billVO.getObject("postlist")));//������Ҫ��ȿ�¡��������Ӻ������λʱ��Ҫ�޸�����������postlist�ֶεĶ���������ﲻ��¡�����ƺͱ����Ƶļ�¼����ı�
			seq = UIUtil.getStringValueByDS(null, "select max(seq) from sal_post_eval_range");//ȥ��ǰ���seq
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		hashMap.put("remark", billVO.getObject("remark"));
		hashMap.put("seq", Integer.parseInt(seq) + 1);
		planListPanel.doInsert(hashMap);
		planListPanel.refreshData();
		postListPanel.clearTable();
	}

	/**
	 * ��Ӹ�λ
	 */
	private void onAddPost() {
		BillVO billVO = planListPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.show(this, "��ѡ��һ��������Χ�ٽ��д˲���!");
			return;
		}
		ChoosePostRefDialog refdialog = new ChoosePostRefDialog(this, "", (RefItemVO) billVO.getObject("postlist"), null);
		refdialog.initialize();
		refdialog.setVisible(true);
		if (refdialog.getCloseType() == BillDialog.CONFIRM) {
			RefItemVO itemVO = refdialog.getReturnRefItemVO();
			if (itemVO != null && itemVO.getId() != null) {
				planListPanel.getSelectedBillVO().getRowNumberItemVO().setState(RowNumberItemVO.UPDATE);
				billVO.setObject("postlist", itemVO);
				planListPanel.setBillVOAt(planListPanel.getSelectedRow(), billVO);
				planListPanel.saveData();
				String postlist = itemVO.getId();
				postListPanel.QueryData("select p.* from pub_post p left join pub_corp_dept d on d.id=p.deptid where p.id in(" + TBUtil.getTBUtil().getInCondition(postlist) + ") order by d.linkcode,p.seq,p.code");
			}
		}
	}

	/**
	 * �Ƴ���λ
	 */

	private void onRemovePost() {
		int[] rows = postListPanel.getCheckedRows();
		if (rows.length == 0) {
			MessageBox.show(this, "�����ٹ�ѡһ����λ��¼�ٽ��д˲���!.");
			return;
		}
		BillVO billVO = planListPanel.getSelectedBillVO();
		if (billVO == null) {
			return;
		}
		String postlist = billVO.getStringValue("postlist");
		for (int i = 0; i < rows.length; i++) {
			String postid = postListPanel.getRealValueAtModel(rows[i], "id");
			postlist = TBUtil.getTBUtil().replaceAll(postlist, postid + ";", "");
		}
		postListPanel.removeRows(rows);//�Ƴ�����ѡ��λ

		planListPanel.getSelectedBillVO().getRowNumberItemVO().setState(RowNumberItemVO.UPDATE);
		RefItemVO itemVO = billVO.getRefItemVOValue("postlist");
		itemVO.setId(postlist);
		billVO.setObject("postlist", itemVO);
		planListPanel.setBillVOAt(planListPanel.getSelectedRow(), billVO);
		planListPanel.saveData();
		if (postListPanel.getRowCount() == 0) {
			postListPanel.setTitleChecked(false);
		}
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		BillVO billVO = _event.getCurrSelectedVO();
		if (billVO == null) {
			return;
		}
		String postlist = billVO.getStringValue("postlist");
		postListPanel.QueryData("select p.* from pub_post p left join pub_corp_dept d on d.id=p.deptid where p.id in(" + TBUtil.getTBUtil().getInCondition(postlist) + ") order by d.linkcode,p.seq,p.code");
		postListPanel.setTitleChecked(false);
	}
}
