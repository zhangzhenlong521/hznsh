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
 * ��λ��ֵ�����ƻ�ά�������/2013-10-21��
 * �ƻ�״̬��δ�����������С���������
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
		btn_edit = new WLTButton("�޸�");
		btn_delete = new WLTButton("ɾ��");
		btn_startup = new WLTButton("�����ƻ�");
		btn_copy = new WLTButton("���Ƽƻ�");
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
	 * �ƻ��޸��߼�
	 */
	private void onEdit() {
		BillVO billVO = planListPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(planListPanel);
			return;
		}
		String status = billVO.getStringValue("status");
		if (status == null || "".equals(status) || "δ����".equals(status)) {
			BillCardPanel cardPanel = new BillCardPanel(planListPanel.getTempletVO());
			cardPanel.setBillVO(billVO); //
			this.addMoveBtn(cardPanel);//���������ƶ��İ�ť
			BillCardDialog dialog = new BillCardDialog(planListPanel, planListPanel.getTempletVO().getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
			dialog.setVisible(true); //
			if (dialog.getCloseType() == 1) {
				if (planListPanel.getSelectedRow() == -1) {//Ԭ���� 20130415��ӣ���Ҫ���ˢ��ģ��ʱ����ܳ��ֵĴ�
				} else {
					planListPanel.setBillVOAt(planListPanel.getSelectedRow(), dialog.getBillVO());
					planListPanel.setRowStatusAs(planListPanel.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT);
				}
			}
		} else {
			MessageBox.show(this, "�üƻ�״̬Ϊ[" + status + "],�����޸�.");
		}
	}

	/**
	 * �ƻ�ɾ���߼�
	 */
	private void onDelete() {
		BillVO billVO = planListPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(planListPanel);
			return;
		}
		String planid = billVO.getStringValue("id");
		String status = billVO.getStringValue("status");
		if (status == null || "".equals(status) || "δ����".equals(status)) {
			if (!MessageBox.confirmDel(this)) {
				return; //
			}
			planListPanel.doDelete(true);
		} else if (MessageBox.confirm(this, "[��Ҫ��ʾ]�ò�����ɾ����ѡ�мƻ��������������,��ȷ��Ҫɾ����?")) {
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
	 * �ƻ������߼�
	 */
	private void onStartup() {
		BillVO billVO = planListPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(planListPanel);
			return;
		}
		String status = billVO.getStringValue("status");
		if (status == null || "".equals(status) || "δ����".equals(status)) {//ֻ��δ�����Ĳſ�����
			try {
				String planid = UIUtil.getStringValueByDS(null, "select count(id) from sal_post_eval_plan where status ='������'");
				if (planid != null && Integer.parseInt(planid) > 0) {
					MessageBox.show(this, "����δ����������,���ܽ��д˲���!");
					return;
				}
				String[] targetnames = UIUtil.getStringArrayFirstColByDS(null, "select targetname from sal_post_eval_target where id not in(select distinct(parentid) from sal_post_eval_target where parentid is not null) and weight is null");
				if (targetnames != null && targetnames.length > 0) {//���ĩ���ڵ��Ƿ���Ȩ�أ������û����Ȩ�ص�ָ�꣬�����������Ȩ��
					StringBuffer sb_names = new StringBuffer("����ά������ָ���Ȩ�أ�\r\n");
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
						}//���ĩ���ڵ㶼��Ȩ�أ������ɸ�λ�������¼
					}
				});
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			MessageBox.show(this, "�üƻ�״̬Ϊ[" + status + "],������������.");
		}
	}

	/**
	 * ���Ƽƻ�
	 */
	private void onCopyPlan() {
		BillVO billVO = planListPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(planListPanel);
			return;
		}
		BillCardPanel cardPanel = new BillCardPanel(planListPanel.getTempletVO()); //����һ����Ƭ���
		cardPanel.insertRow(); //��Ƭ����һ��!
		cardPanel.setValueAt("planname", new StringItemVO(billVO.getStringValue("planname") + "_����"));
		cardPanel.setValueAt("status", new ComBoxItemVO("δ����", "", "δ����"));

		String selected_planid = billVO.getStringValue("id");
		String new_planid = cardPanel.getBillVO().getStringValue("id");//�����ļ�¼����
		ArrayList childIDList = new ArrayList();
		try {
			//���������˼�������λ��¼
			HashVO[] postvos = UIUtil.getHashVoArrayByDS(null, "select * from sal_post_eval_plan_post where planid=" + selected_planid + " order by seq");
			if (postvos != null && postvos.length > 0) {
				InsertSQLBuilder sqlBuilder = new InsertSQLBuilder("sal_post_eval_plan_post");
				ArrayList sqlList = new ArrayList();//��¼�����ӱ���sql
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
				UIUtil.executeBatchByDS(null, sqlList);//ִ���ӱ���sql�������ڴ򿪿�Ƭʱ���ܿ����ӱ��¼
			}
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.addMoveBtn(cardPanel);//���������ƶ��İ�ť
		cardPanel.setEditableByEditInit(); //���ÿ�Ƭ�༭״̬Ϊ����ʱ������
		BillCardDialog dialog = new BillCardDialog(planListPanel, planListPanel.getTempletVO().getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); //������Ƭ������
		dialog.setVisible(true); //��ʾ��Ƭ����
		if (dialog.getCloseType() == 1) { //�����ǵ��ȷ������!����Ƭ�е����ݸ����б�!
			int li_newrow = planListPanel.newRow(false); //
			planListPanel.setBillVOAt(li_newrow, dialog.getBillVO(), false);
			planListPanel.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); //�����б���е�����Ϊ��ʼ��״̬.
			planListPanel.setSelectedRow(li_newrow); //
		} else if (childIDList.size() > 0) {//���û�е����ȷ�������رմ��ڣ�����Ҫ���ӱ���Ϣ���
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
	 * ��Ƭ�����������ư�ť
	 * @param bc
	 */
	private void addMoveBtn(final BillCardPanel bc) {
		final CardCPanel_ChildTable childTable = ((CardCPanel_ChildTable) bc.getCompentByKey("postids"));
		final BillListPanel bl = childTable.getBillListPanel();
		if (iconDown == null) {
			iconDown = UIUtil.getImage("down1.gif");
			iconUp = new ImageIcon(TBUtil.getTBUtil().getImageRotate(iconDown.getImage(), 180)); //ת180��!		
		}

		btn_moveup = new WLTButton("");
		btn_moveup.setIcon(iconUp);
		btn_moveup.setToolTipText("����");
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
		btn_movedown.setToolTipText("����");
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
				bl.saveData();//����ֱ�ӱ�����һ���׶ˣ��������ȡ���ˣ���Ų��ָܻ��������/2013-11-22��
			}
		});
		bl.queryDataByCondition("planid=" + bc.getBillVO().getPkValue(), "seq");
	}

	/**
	 * ���ɸ�λ�������¼
	 */
	public void createPostEvalTable() throws Exception {
		BillTreePanel treePanel = new BillTreePanel("SAL_POST_EVAL_TARGET_LCJ_E01");
		treePanel.queryDataByCondition(null);
		Vector v_sqls = new Vector(); //
		DefaultMutableTreeNode[] allNodes = treePanel.getAllNodes(); //
		// ��������LinkCode
		for (int i = 1; i < 10; i++) { //������10�㣬�����ָ��һ���2�㡣
			boolean bo_iffind = false; //
			for (int j = 0; j < allNodes.length; j++) { //�������н��..
				if (allNodes[j].getLevel() == i) { //����ǵڼ���
					bo_iffind = true; //���ϱ��,��ʾ�Ѿ�������!
					DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) allNodes[j].getParent(); //ȡ�ø���!!!
					int li_index = parentNode.getIndex(allNodes[j]); //�ж����ڸ�����е�λ��
					String str_currlinkcode = ("" + (10000 + li_index + 1)).substring(1, 5); //�������,��λ����
					String str_parlinkcode = ""; //
					if (!parentNode.isRoot()) {
						str_parlinkcode = treePanel.getBillVOFromNode(parentNode).getStringValue("linkcode");
					}
					String str_linkcode = str_parlinkcode + str_currlinkcode; //
					BillVO billVO = treePanel.getBillVOFromNode(allNodes[j]); //
					String str_billvolinkcode = billVO.getStringValue("linkcode"); //
					if (str_billvolinkcode == null || !str_billvolinkcode.equals(str_linkcode)) { //�������ԭ��������Ϊ�ջ��߲������µ�ֵ
						v_sqls.add("update " + treePanel.getTempletVO().getSavedtablename() + " set linkcode='" + str_linkcode + "' where id='" + billVO.getPkValue() + "'"); //
						billVO.setObject("linkcode", new StringItemVO(str_linkcode)); //
						treePanel.setBillVO(allNodes[j], billVO); //
					}
				}
			}
			if (!bo_iffind) { //���ĳһ��һ��Ҳû����,���˳�ѭ��..
				break;
			}
		}
		String planid = planListPanel.getSelectedBillVO().getStringValue("id");
		v_sqls.add("update sal_post_eval_plan set status='������' where id=" + planid);//�޸ĵ�ǰ�ƻ���״̬
		UIUtil.executeBatchByDS(null, v_sqls, false, false); //
		SalaryServiceIfc service = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
		service.createPostEvalTable(planid);//����ʵ�ʵĸ�λ�������¼����һ���ܺ�ʱ
		planListPanel.refreshCurrSelectedRow();
		MessageBox.show(planListPanel, "�����ɹ�!");
	}

}