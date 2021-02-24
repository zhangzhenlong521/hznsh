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
 * ��λְ��ά��!!  ����ǻ�����,�ұ��ϱ��Ǹ�λ[pub_post],�±��Ǹ�λ�ĸ�ְ[cmp_postduty]!
 * @author xch
 *
 */
public class PostDutyEditWKPanel extends AbstractWorkPanel implements BillTreeSelectListener, BillListSelectListener, ActionListener {

	private BillTreePanel billTree_corp = null; //��������!!
	private BillListPanel billList_post = null; //��λ�б�!!
	private BillListPanel billList_postduty = null; //��λ����!!
	private BillListPanel list_post;
	private BillListPanel list_postduty;
	private WLTButton btn_insert, btn_update, btn_delete, btn_seq, btn_import; //��,ɾ,��
	private WLTButton btn_post_insert, btn_post_delete, btn_post_update;
	private WLTButton btn_duty; //��ɽ����ġ�ͨ������ͻ��ڲ���Ҫ����ά��ϸ����������
	private WLTButton btn_cancel, btn_confirm;
	private WLTButton btn_addduty, btn_showduty;//���롢�鿴��ť
	private TBUtil tbUtil = new TBUtil(); //
	private int tempMap = 0;//�鿴���o������ʾ
	private List sqlList = new ArrayList();//��¼��Ҫִ�е�sql���
	private BillDialog billDialog;//
	private HashMap billvoMap = new HashMap();//��¼ѡ��ĸ�λ�ĸ�����Ϣ������鿴���������ʾ
	private List hasBillVo = new ArrayList();//��¼�Ѿ�����ĸ����¼

	@Override
	public void initialize() {
		billTree_corp = new BillTreePanel("PUB_CORP_DEPT_1"); //������
		billTree_corp.setMoveUpDownBtnVisiable(false); //
		billTree_corp.queryDataByCondition(null); //��ѯ��������,Ҫ��Ȩ�޹���!!
		billTree_corp.addBillTreeSelectListener(this); //ˢ���¼�����!!

		billList_post = new BillListPanel("PUB_POST_CODE1"); //��λ!!
		billList_post.addBillListSelectListener(this); //

		billList_postduty = new BillListPanel("CMP_POSTDUTY_CODE1"); //��λ����!!

		if (isCanEdit()) {
			btn_post_insert = new WLTButton("����"); //������λ!!��ҵ�ڿ�Ū��Bom��ʱ����λ�͸�λְ��һ��ά�������Լ���һ���˵��������Ӹ�λά�����ܡ����/2013-09-13��
			btn_post_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); //�޸ĸ�λ
			btn_post_delete = new WLTButton("ɾ��"); //ɾ����λ������
			btn_duty = new WLTButton("ά����λ�ֲ�");
			btn_post_insert.addActionListener(this); //
			btn_post_update.addActionListener(this);
			btn_post_delete.addActionListener(this);
			btn_duty.addActionListener(this);
			billList_post.addBatchBillListButton(new WLTButton[] { btn_post_insert, btn_post_update, btn_post_delete, btn_duty }); //
			billList_post.repaintBillListButton();

			btn_insert = new WLTButton("����"); //
			btn_insert.addActionListener(this); //
			btn_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); //�޸�
			btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE); //ɾ��
			btn_seq = new WLTButton("����");//����ְ������ť�����/2014-12-16��
			btn_seq.addActionListener(this);
			btn_import = new WLTButton("����");
			btn_import.addActionListener(this);
			billList_postduty.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_seq,btn_import }); //�������ð�ť!!!
			billList_postduty.repaintBillListButton(); //
		}

		WLTSplitPane split1 = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, billList_post, billList_postduty); //
		split1.setDividerLocation(250); //

		WLTSplitPane split2 = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, billTree_corp, split1); //
		split2.setDividerLocation(230);
		this.add(split2); //
	}

	/**
	 * ������ѡ��仯�¼�!!
	 */
	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		billList_post.clearTable(); //
		billList_postduty.clearTable(); //
		BillVO billVO = _event.getCurrSelectedVO(); //
		if (billVO == null) {
			return;
		}
		String str_deptid = billVO.getStringValue("id"); //��������id.
		billList_post.QueryDataByCondition("deptid='" + tbUtil.getNullCondition(str_deptid) + "'"); //
	}

	/**
	 * ��λ�б�ѡ��仯�¼�!!
	 */
	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		if (_event.getSource() == billList_post) {
			billList_postduty.clearTable(); //
			BillVO billVO = _event.getCurrSelectedVO(); //
			if (billVO == null) {
				return;
			}
			String str_postid = billVO.getStringValue("id"); //��λid
			billList_postduty.QueryDataByCondition("postid='" + tbUtil.getNullCondition(str_postid) + "'"); //ˢ�¸�λ����!!
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
			onInsert(); //��������!
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
			MessageBox.show(this, "��ѡ��һ������"); //
			return;
		}
		BillCardPanel billCardPanel = new BillCardPanel(billList_post.getTempletVO());
		billCardPanel.setEditableByInsertInit();
		billCardPanel.insertRow();
		billCardPanel.setValueAt("deptid", new RefItemVO(vo.getPkValue(), "", vo.getStringValue("name")));
		BillCardDialog billCardDialog = new BillCardDialog(billList_post, billCardPanel.getTempletVO().getTempletname() + "-����", billCardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);
		billCardDialog.setVisible(true);
		if (billCardDialog.getCloseType() == 1) {
			int li_newrow = billList_post.newRow(false); //
			billList_post.setBillVOAt(li_newrow, billCardPanel.getBillVO(), false);
			billList_post.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); //�����б���е�����Ϊ��ʼ��״̬.
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
		BillCardDialog billCardDialog = new BillCardDialog(billList_post, billCardPanel.getTempletVO().getTempletname() + "-�޸�", billCardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		billCardDialog.setVisible(true);
		if (billCardDialog.getCloseType() == 1) {
			billList_post.refreshCurrSelectedRow();
		}

	}

	private void onDeletePost() {
		BillVO billVO = billList_post.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, "��ѡ��һ����λ���д˲���!"); //
			return;
		}
		boolean result = MessageBox.confirm(billList_post, "ȷ��ɾ��[��λ]�Լ����Ӧ��[��λ����]?");
		if (result) {
			String postid = billVO.getStringValue("id");
			String sql1 = "delete from pub_post where id = " + postid;
			String sql2 = "delete from cmp_postduty where postid=" + postid;
			try {
				UIUtil.executeBatchByDS(null, new String[] { sql1, sql2 });
				MessageBox.show(billList_post, "ɾ�������ɹ�.");
			} catch (Exception e) {
				MessageBox.show(billList_post, "ɾ������ʧ��.");
				e.printStackTrace();
			}
		}
		billList_post.refreshData();
		billList_postduty.clearTable();
	}

	/**
	 * ������λ����!!
	 */
	private void onInsert() {
		BillVO billVO = billList_post.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, "��ѡ��һ����λ���д˲���!"); //
			return;
		}
		HashMap defaultValueMap = new HashMap(); //
		defaultValueMap.put("deptid", billVO.getStringValue("deptid")); //
		defaultValueMap.put("postid", billVO.getStringValue("id")); //
		billList_postduty.doInsert(defaultValueMap); //������λ����!!!
	}

	/**
	 * �����λ����
	 */
	private void onImportDate() {
		BillVO billVO = billList_post.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, "��ѡ��һ����λ���д˲���!"); //
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
		billDialog = new BillDialog(billList_postduty, "�����λ��������", 800, 700);
		billDialog.add(getCenterPanel(), BorderLayout.CENTER); //��ӷָ����
		billDialog.add(getSouthPanel(), BorderLayout.SOUTH);//��Ӱ�ť���
		billDialog.setVisible(true);

	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout(), BackGroundDrawingUtil.HORIZONTAL_FROM_MIDDLE);
		btn_confirm = new WLTButton("ȷ��");
		btn_cancel = new WLTButton("ȡ��");
		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel;
	}

	private WLTSplitPane getCenterPanel() {
		list_post = new BillListPanel("PUB_POST_CODE2");
		list_post.setTitleLabelText("��λ��");
		list_post.setItemVisible("post_status", false);//��������ģ���ˣ�Ϊ�˲�Ӱ������ҳ�棬��������һ�����ظ�λ״̬����ʾ��λ����
		list_post.setItemVisible("refpostid", false);
		list_post.setItemVisible("descr", true);
		list_post.setQuickQueryPanelVisiable(true);
		list_post.setDataFilterCustCondition("deptid is null");
		list_post.QueryDataByCondition(" 1=1 ");
		list_post.setItemWidth("code", 200);//��������ģ���ˣ�Ϊ�˲�Ӱ������ҳ�棬��������һ���п�
		list_post.setItemWidth("name", 200);
		list_post.setItemWidth("descr", 200);
		btn_addduty = new WLTButton("����", "office_199.gif");
		btn_showduty = new WLTButton("�鿴(" + tempMap + ")", "office_062.gif");
		list_postduty = new BillListPanel("CMP_POSTDUTY_CODE1");
		list_postduty.setTitleLabelText("���ڱ��б���ѡ���λְ�������������롿");
		list_postduty.getTitleLabel().setForeground(Color.BLUE);
		btn_addduty.addActionListener(this);
		btn_showduty.addActionListener(this);
		list_postduty.addBatchBillListButton(new WLTButton[] { btn_addduty, btn_showduty });
		list_postduty.repaintBillListButton();
		list_post.getQuickQueryPanel().addBillQuickActionListener(new ActionListener() {//��ѯ�¼�����
					public void actionPerformed(ActionEvent e) {
						BillQueryPanel pan = list_post.getQuickQueryPanel();
						list_post.QueryData(pan.getQuerySQL(pan.getAllQuickQueryCompents()));
						list_postduty.clearTable();//��ո����¼
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
	 * ȡ����ť�¼�����
	 * @author ��Ӫ�� 2013-07-05
	 * */
	private void onCancelDate() {
		sqlList.clear();
		billDialog.dispose();

	}

	/**
	 * ȷ����ť�¼�����
	 * @author ��Ӫ�� 2013-07-05
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
	 * ���밴ť�¼�����
	 * @author ��Ӫ�� 2013-07-05
	 * */
	private void onAddDuty() {
		BillVO[] billVOs = list_postduty.getSelectedBillVOs();
		BillVO billVO = billList_post.getSelectedBillVO();
		if (billVOs == null || billVOs.length < 1) {
			MessageBox.showSelectOne(list_postduty);
			return;
		}
		int hasAdd = 0;//��¼����ļ�¼��Ŀ����ѡ��ļ�¼�����Աȣ��ж��Ƿ���ȫ������
		for (int i = 0; i < billVOs.length; i++) {
			try {
				if (hasBillVo.contains(billVOs[i].getStringValue("id"))) {//�жϴ˼�¼�Ƿ��Ѿ�����
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
				tempMap++;//�鿴��ť����ʾ����������
				billvoMap.put(billvoMap.size(), billVOs[i]);//����鿴��ť�㿪�������ʾ����
				hasBillVo.add(billVOs[i].getStringValue("id"));//��¼�˴μ���ļ�¼��id
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (hasAdd == billVOs.length && hasAdd == 1) {
			MessageBox.show(list_postduty, "�ü�¼�Ѽ���,�����ظ�����!");
		} else if (hasAdd == billVOs.length) {
			MessageBox.show(list_postduty, "��ȫ������,�����ظ�����!");
		} else {
			MessageBox.show(list_postduty, "����ɹ�");
		}
		btn_showduty.setText("�鿴(" + tempMap + ")");//���ò鿴��ť������
	}

	/**
	 * �鿴��ť�¼�����
	 * @author ��Ӫ�� 2013-07-05
	 * */
	private void onShowDuty() {
		if (tempMap == 0) {
			MessageBox.show(billDialog, "���ȼ����ٽ��в鿴!");
			return;
		}
		BillListDialog billdialog = new BillListDialog(list_postduty, "���й����ĸ���", "CMP_POSTDUTY_CODE1", 850, 550);
		BillListPanel showListPanel = billdialog.getBilllistPanel();
		for (int i = 0; i < billvoMap.size(); i++) {
			showListPanel.addRow((BillVO) billvoMap.get(i));
		}
		billdialog.getBtn_confirm().setVisible(false);
		billdialog.getBtn_cancel().setText("�ر�");
		billdialog.getBtn_cancel().setToolTipText("�ر�");
		billdialog.setVisible(true);

	}

	/*
	 * ͨ�����ڲ���Ҫ�� ���޸ĸ�����Ϣ.��ɽ���
	 */
	private void onEditPostDuty() {
		BillVO postvo = billList_post.getSelectedBillVO();
		if (postvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		PostDutyDetailWKDialog dialog = new PostDutyDetailWKDialog(this, "��λְ������ά��", 1020, 650, postvo);
		dialog.locationToCenterPosition();
		dialog.setVisible(true);
		billList_postduty.refreshData();
	}

	/**
	 * ְ���������/2014-12-16��
	 */
	private void onSeqDuty() {
		SeqListDialog dialog_post = new SeqListDialog(this, "ְ������", billList_postduty.getTempletVO(), billList_postduty.getAllBillVOs());
		dialog_post.setVisible(true);
		if (dialog_post.getCloseType() == 1) {//������ȷ�����أ�����Ҫˢ��һ��ҳ��
			billList_postduty.refreshData(); //
		}
	}
}
