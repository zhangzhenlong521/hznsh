package com.pushworld.ipushgrc.ui.cmpevent.p170;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JSplitPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryEvent;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;

/**
 * ���ĸ���ά��
 * @author hm
 *
 */
public class CmpAdjustTrackWKPanel extends AbstractWorkPanel implements ActionListener, BillListSelectListener, BillListAfterQueryListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5423260581405561469L;
	private BillListPanel billlist_proj = null; //
	private BillListPanel billList_eventtrack = null; //
	private WLTButton btn_insert, btn_update, btn_delete, btn_list;

	private BillCardDialog updateDialog;
	private boolean ischange;
	private BillCardPanel cardPanel = null;

	private String getUpCardPanelCode() {//�õ��ָ�������Ŀ�Ƭcode
		return "CMP_EVENT_ADJUSTPROJECT_CODE3";
	}

	private String getDownListPanelCode() {//�õ��ָ������·����б�code
		return "CMP_EVENT_TRACK_CODE1_x";
	}

	private String getDownLinkedColumn() {//�õ��·��б����Ϸ����Ĺ����ֶ�,�·��б��и��ֶβ����Ϸ���id
		return "projectid";
	}

	public void initialize() {
		billlist_proj = new BillListPanel(getUpCardPanelCode()); //����
		billlist_proj.addBillListSelectListener(this);
		billlist_proj.addBillListAfterQueryListener(this);//���Ӳ�ѯ���¼�������ӱ����/2012-08-08��
		WLTButton view_btn = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD, "�鿴��ϸ");
		billlist_proj.addBatchBillListButton(new WLTButton[] { view_btn });
		billlist_proj.repaintBillListButton();
		billList_eventtrack = new BillListPanel(this.getDownListPanelCode()); //��������
		btn_update = new WLTButton("�޸�");
		btn_update.addActionListener(this);
		btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE); //
		btn_list = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD); //
		btn_insert = new WLTButton("����");
		btn_insert.addActionListener(this);
		billList_eventtrack.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_list }); //
		billList_eventtrack.repaintBillListButton(); //ˢ�°�ť!!!
		btn_insert.setEnabled(false);
		btn_update.setEnabled(false);
		btn_delete.setEnabled(false);
		WLTSplitPane split = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, billlist_proj, billList_eventtrack); //
		split.setDividerLocation(380); //
		this.add(split); //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_insert) { //�������
			onInsert();
		}
		if (e.getSource() == btn_update) { //�޸ĸ���
			onupdate();
		}
	}

	/**
	 * �޸ĸ�������
	 */
	public void onupdate() {
		BillVO eventItemVO = billList_eventtrack.getSelectedBillVO();
		if (eventItemVO == null || "".equals(eventItemVO.getStringValue("id"))) {
			MessageBox.showSelectOne(this);
			return;
		}

		String sql0 = "select * from CMP_EVENT_ADJUSTSTEP where PROJECTID = " + billlist_proj.getSelectedBillVO().getStringValue("id");
		try {//����Ƿ����  ������ ������
			HashVO[] vos = UIUtil.getHashVoArrayByDS(null, sql0);
			for (HashVO vo : vos) {
				if ("����ʵ".equals(vo.getStringValue("status"))) {//�����״̬�� û����ʵ��
					ischange = false;
				} else {
					ischange = true;
				}
			}
		} catch (Exception e1) {
			// TODO: handle exception
		}
		cardPanel = new BillCardPanel("CMP_EVENT_TRACK_CODE1_x");
		cardPanel.setBillVO(eventItemVO);
		updateDialog = new BillCardDialog(this, "�޸�", cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);

		updateDialog.getBtn_save().addActionListener(new ActionListener() { //�����¼�
					public void actionPerformed(ActionEvent e) {

						//״̬����, ֻ�������ӱ����ʵ״̬����"����ʵ"ʱ, ����״̬������"�ﵽ����Ч��"
						//����״̬����Ϊ"�ﵽ����Ч��"ʱ, ���ķ���״̬��Ϊ"������"
						if ("�ﵽ����Ч��".equals(updateDialog.getBillcardPanel().getBillVO().getStringValue("result")) && ischange) {
							MessageBox.show(updateDialog, "[���Ľ��]������Ч\r\nϵͳ������δ��ʵ��[���Ĵ�ʩ]");
							return;
						} else if ("�ﵽ����Ч��".equals(updateDialog.getBillcardPanel().getBillVO().getStringValue("result")) && !ischange) {
							//���·���, �¼�
							String sql1 = "update CMP_EVENT_ADJUSTPROJECT set status = '������' " + "where id = " + billlist_proj.getSelectedBillVO().getStringValue("id");
							String sql2 = "update CMP_EVENT set adjustresulttype='������' where id = " + billlist_proj.getSelectedBillVO().getStringValue("eventid");
							try {
								UIUtil.executeBatchByDS(null, new String[] { sql1, sql2 });
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							btn_insert.setEnabled(false);
							btn_update.setEnabled(false);
							btn_delete.setEnabled(false);
							billlist_proj.refreshCurrSelectedRow();
							updateDialog.onSave();
							return;
						} else {
							updateDialog.onSave();
						}
					}
				});
		updateDialog.getBtn_confirm().addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				//״̬����, ֻ�������ӱ����ʵ״̬����"����ʵ"ʱ, ����״̬������"�ﵽ����Ч��"
				//����״̬����Ϊ"�ﵽ����Ч��"ʱ, ���ķ���״̬��Ϊ"������"
				if ("�ﵽ����Ч��".equals(updateDialog.getBillcardPanel().getBillVO().getStringValue("result")) && ischange) {
					MessageBox.show(updateDialog, "[���Ľ��]������Ч\r\nϵͳ������δ��ʵ��[���Ĵ�ʩ]");
					return;
				} else if ("�ﵽ����Ч��".equals(updateDialog.getBillcardPanel().getBillVO().getStringValue("result")) && !ischange) {
					//���·���, �¼�
					String sql1 = "update CMP_EVENT_ADJUSTPROJECT set status = '������' " + "where id = " + billlist_proj.getSelectedBillVO().getStringValue("id");
					String sql2 = "update CMP_EVENT set adjustresulttype='������' where id = " + billlist_proj.getSelectedBillVO().getStringValue("eventid");
					try {
						UIUtil.executeBatchByDS(null, new String[] { sql1, sql2 });
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					btn_insert.setEnabled(false);
					btn_update.setEnabled(false);
					btn_delete.setEnabled(false);
					billlist_proj.refreshCurrSelectedRow();
					updateDialog.onConfirm();
					billList_eventtrack.refreshCurrSelectedRow();
					return;
				} else {
					updateDialog.onConfirm();
					billList_eventtrack.refreshCurrSelectedRow();
				}
			}
		});
		updateDialog.setVisible(true);
	}

	/**
	 * �����������
	 */
	public void onInsert() {
		BillVO eventItemVO = billlist_proj.getSelectedBillVO();
		if (eventItemVO == null || "".equals(eventItemVO.getStringValue("id"))) {
			MessageBox.showSelectOne(this);
			return;
		}
		BillCardPanel cardPanel = new BillCardPanel("CMP_EVENT_TRACK_CODE1_x");
		cardPanel.insertRow();
		cardPanel.setEditableByInsertInit();
		cardPanel.setValueAt(this.getDownLinkedColumn(), new StringItemVO(eventItemVO.getStringValue("id")));
		cardPanel.setValueAt("projectname", new StringItemVO(eventItemVO.getStringValue("projectname")));
		final BillCardDialog insertDialog = new BillCardDialog(this, "����", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);

		insertDialog.getBtn_confirm().removeAll();
		insertDialog.getBtn_confirm().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				insertTraceMsg(true, insertDialog);
			}
		});
		insertDialog.getBtn_save().removeAll();
		insertDialog.getBtn_save().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				insertTraceMsg(false, insertDialog);
			}
		});
		insertDialog.setVisible(true);
	}

	public void insertTraceMsg(boolean isDispose, BillCardDialog insertDialog) {
		BillCardPanel card = insertDialog.getBillcardPanel();
		card.stopEditing(); //
		if (!card.checkValidate()) {
			return;
		}
		String selectedStatus = card.getBillVO().getStringValue("result");

		if ("�ﵽ����Ч��".equals(selectedStatus)) {
			boolean ok = true;
			String sql0 = "select * from CMP_EVENT_ADJUSTSTEP where PROJECTID = " + billlist_proj.getSelectedBillVO().getStringValue("id");
			try {
				//״̬����, ֻ�������ӱ����ʵ״̬����"����ʵ"ʱ, ����״̬������"�ﵽ����Ч��"				
				HashVO[] vos = UIUtil.getHashVoArrayByDS(null, sql0);
				for (HashVO vo : vos) {
					if (!"����ʵ".equals(vo.getStringValue("status"))) {//�����״̬�� û����ʵ��
						ok = false;
						break;
					}
				}
			} catch (WLTRemoteException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			//����״̬����Ϊ"�ﵽ����Ч��"ʱ, ���ķ���, �¼�״̬��Ϊ"������"
			if (ok) {//�������������Բ���
				try {
					card.updateData();
					if (isDispose)
						insertDialog.dispose(); //
					else
						MessageBox.show(this, "�������ݳɹ�!");

					//���·���, �¼�
					String sql1 = "update CMP_EVENT_ADJUSTPROJECT set status = '������' " + "where id = " + billlist_proj.getSelectedBillVO().getStringValue("id");
					String sql2 = "update CMP_EVENT set adjustresulttype='������' where id = " + billlist_proj.getSelectedBillVO().getStringValue("eventid");
					UIUtil.executeBatchByDS(null, new String[] { sql1, sql2 });
					billlist_proj.refreshCurrSelectedRow();
					btn_insert.setEnabled(false);
					btn_update.setEnabled(false);
					btn_delete.setEnabled(false);
				} catch (Exception e1) {
					e1.printStackTrace();
				}//������������
			} else {
				MessageBox.show(this, "[���Ľ��]������Ч\r\nϵͳ������δ��ʵ��[���Ĵ�ʩ]");
				return;
			}
		} else {
			try {
				card.updateData();//������������				
			} catch (Exception e1) {
				e1.printStackTrace();
			} //����������??
			if (isDispose)
				insertDialog.dispose(); //
			else
				MessageBox.show(this, "�������ݳɹ�!");
		}

		billList_eventtrack.QueryDataByCondition(" projectid = " + billlist_proj.getSelectedBillVO().getStringValue("id"));
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		billList_eventtrack.QueryDataByCondition(" " + this.getDownLinkedColumn() + "=" + _event.getCurrSelectedVO().getStringValue("id"));
		if ("������".equals(_event.getCurrSelectedVO().getStringValue("status"))) {
			btn_insert.setEnabled(false);
			btn_update.setEnabled(false);
			btn_delete.setEnabled(false);
		} else {
			btn_insert.setEnabled(true);
			btn_update.setEnabled(true);
			btn_delete.setEnabled(true);
		}
	}

	//���Ӳ�ѯ���¼�������ӱ����/2012-08-08��
	public void onBillListAfterQuery(BillListAfterQueryEvent _event) {
		billList_eventtrack.clearTable();
	}
}
