package com.pushworld.ipushgrc.ui.cmpscore.p020;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardEditEvent;
import cn.com.infostrategy.ui.mdata.BillCardEditListener;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.cmpscore.p050.CmpScoreMyScoreWKPanel;

/**
 * Υ�����¼��
 * 
 * @author hm
 * 
 */
public class CmpScoreLostWKPanel1 extends AbstractWorkPanel implements ActionListener, BillCardEditListener {
	private WLTButton btn_insert, btn_insertfromevent, btn_update, btn_delete, btn_show, btn_confirm, btn_conf;

	private BillListPanel listPanel;

	private TBUtil tbutil = null;

	private BillCardPanel cardPanel, cardPanelInsert;

	private BillCardDialog cardDialog, cardDialogInsert;

	public void initialize() {
		btn_insert = new WLTButton("ֱ�Ӵ���");
		btn_insertfromevent = new WLTButton("���¼�����");
		btn_update = new WLTButton("�޸�");
		btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
		btn_show = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);
		btn_insert.addActionListener(this);
		btn_insertfromevent.addActionListener(this);
		btn_update.addActionListener(this);
		btn_delete.addActionListener(this);
		listPanel = new BillListPanel("CMP_SCORE_RECORD_CODE1");
		listPanel.addBatchBillListButton(new WLTButton[] { btn_insert, btn_insertfromevent, btn_update, btn_delete, btn_show });
		listPanel.repaintBillListButton();
		this.add(listPanel);
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_insert) {
			onInsert();
		} else if (obj == btn_insertfromevent) {
			onInsertFromEvent();
		} else if (obj == btn_update) {
			onUpdate();
		} else if (obj == btn_confirm) {
			if (cardPanel != null) {
				onConfirm(); //���¼����� ������ ��ť
			}
		} else if (obj == btn_delete) {
			onDelete();
		} else if (obj == btn_conf) {
			onConf();
		}

	}

	/*
	 * ֱ�Ӵ���
	 */
	public void onInsert() {
		cardPanelInsert = new BillCardPanel("CMP_SCORE_RECORD_CODE1");
		cardPanelInsert.setEditable("cmp_risk_ids", false);
		cardPanelInsert.setEditableByInsertInit();
		cardPanelInsert.addBillCardEditListener(this); //���뿨Ƭ����
		cardPanelInsert.insertRow();
		cardDialogInsert = new BillCardDialog(this, "ֱ������Υ�����", cardPanelInsert, WLTConstants.BILLDATAEDITSTATE_INSERT);
		btn_conf = cardDialogInsert.getBtn_confirm();
		btn_conf.setText("���");
		btn_conf.addActionListener(this);
		cardDialogInsert.getBtn_save().setVisible(false);
		cardDialogInsert.setVisible(true);
		if (cardDialogInsert.getCloseType() != 1) {

			return;
		}
		listPanel.QueryDataByCondition(null);
	}

	public void onConf() {
		List insertSql = new ArrayList();
		BillVO vo = cardPanelInsert.getBillVO();
		String sqls = "select * from cmp_score_record where code ='" + vo.getStringValue("Code") + "'";

		try {
			HashVO[] hashVO = UIUtil.getHashVoArrayByDS(null, sqls.toString());
			if (hashVO.length == 0) {
				String[] userid = vo.getStringValue("userid").split(";");
				String deptid = vo.getStringValue("deptid");
				for (int i = 0; i < userid.length; i++) {
					String id = null;
					id = UIUtil.getSequenceNextValByDS(null, "S_CMP_SCORE_RECORD");
					// Υ����Ա������Ϣsql
					insertSql.add(getInsertSQL(id, vo, userid[i], deptid));
					String username = "''";
					for (int j = 0; j < userid.length; j++) {
						username = username + ",'" + userid[j] + "'";
						insertSql.add("update cmp_wardevent_user set relstate = '��' where username in (" + username + ") and cmp_wardevent_id = " + vo.getStringValue("eventid"));
						insertSql.add(createMsgToUser(userid[j], id, vo.getStringValue("scorelost")));
					}
				}
				cardDialogInsert.setCloseType(1);
				UIUtil.executeBatchByDS(null, insertSql);
				cardDialogInsert.dispose();

			} else {
				MessageBox.show(this, "���¼�ȷ�ϵ���š�������Ψһ�ԣ���������������!");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// ɾ������
	private void onDelete() {
		BillVO billvo = listPanel.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		// ����ط�ȡ��̨��״̬
		String sql_query = "select sendstate from " + billvo.getSaveTableName() + " where id = " + billvo.getStringValue("id");
		try {
			String sendstate = UIUtil.getStringValueByDS(null, sql_query);
			if (sendstate.equals("1")) {
				if (MessageBox.confirmDel(this)) {
					UIUtil.executeBatchByDS(null, new String[] { "delete from " + billvo.getSaveTableName() + " where id = " + billvo.getStringValue("id") });
					listPanel.removeSelectedRow();
				}
			} else {
				MessageBox.show(this, "֪ͨ�Ѿ�����,������ɾ����");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *  ��Ƭ�༭�¼�
	 */

	public void onBillCardValueChanged(BillCardEditEvent evt) {
		try {
			if (evt.getItemKey() != null) {
				if (evt.getItemKey().equals("scorestand")) { //�������˱�׼��Ŀ
					if (evt.getNewObject() != null) {
						RefItemVO itemvo = (RefItemVO) evt.getNewObject(); //ȡ�ò���ֵ
						String[] itemid = itemvo.getId().split(";");
						String in_sql = ""; // in() �����
						for (int i = 1; i < itemid.length; i++) { //��1��ʼ����Ϊ�洢��ʽΪ;XX;XX;
							in_sql = in_sql + itemid[i] + ",";
							if (i == itemid.length - 1)
								in_sql = in_sql + itemid[i];
						}
						String sql = "Select Sum(score) From cmp_score_stand Where Id In (" + in_sql + ") ";
						String score_sum = UIUtil.getStringValueByDS(null, sql); //ȡ����ѡ����Ҫ�۷ֵĺ�
						sql = "select Sum(amount) from cmp_score_stand where Id in (" + in_sql + ")";
						String price_sum = UIUtil.getStringValueByDS(null, sql); //ȡ�÷����ܺ�

						BillCardPanel billcard = (BillCardPanel) evt.getSource(); //�õ���ǰcard
						billcard.setValueAt("scorelost_ref", new StringItemVO(score_sum)); //Ϊ��Ƭ�ϵ����ֵ
						billcard.setValueAt("pricelost_ref", new StringItemVO(price_sum));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * ���¼�����
	 */
	public void onInsertFromEvent() {
		BillListDialog listDialog = new BillListDialog(this, "��ѡ��һ���¼�,�����һ��", "CMP_EVENT_CODE1");
		listDialog.getBilllistPanel().setDataFilterCustCondition(" eventuser is not null ");
		listDialog.getBtn_confirm().setText("��һ��");
		listDialog.setVisible(true);
		if (listDialog.getCloseType() != 1) {
			return;
		}
		BillVO eventVO = listDialog.getReturnBillVOs()[0];
		String eventuser = eventVO.getStringValue("eventuser");
		StringBuffer userName = new StringBuffer();
		HashVO[] users = null;//username
		if (!"".equals(eventuser)) {
			try {
				users = UIUtil.getHashVoArrayByDS(null, "select t1.userid userid,t1.username name,t1.deptid deptid,t1.deptname deptname from v_pub_user_post_1 t1,cmp_wardevent_user t2 where t1.userid = t2.username and t2.id in(" + getTBUtil().getInCondition(eventuser)
						+ ") and t1.id in (select min(id) from v_pub_user_post_1 where userid = t2.username group by userid )");
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			for (int i = 0; i < users.length; i++) {
				if (i == 0) {
					userName.append(";" + users[i].getStringValue("name") + ";");
				} else {
					userName.append(users[i].getStringValue("name") + ";");
				}
			}
		}
		String cmp_cmpfile_id = eventVO.getStringValue("cmp_cmpfile_id"); // �����ļ�
		String cmp_cmpfile_name = eventVO.getStringValue("cmp_cmpfile_name");
		String refrisks = eventVO.getStringValue("refrisks"); // ���յ�
		String eventid = eventVO.getStringValue("id");
		String eventName = eventVO.getStringValue("eventname");
		String eventdescr = eventVO.getStringValue("eventdescr");
		ComBoxItemVO findchannel = eventVO.getComBoxItemVOValue("findchannel");

		StringBuffer riskName = new StringBuffer();
		double scorelost_ref = 0.0d;
		if (refrisks != null && !refrisks.equals("")) {
			String refRiskids = getTBUtil().getInCondition(refrisks);
			try {
				HashVO[] risks = UIUtil.getHashVoArrayByDS(null, "select riskname,scorelost from cmp_risk where id in(" + refRiskids + ")");
				for (int i = 0; i < risks.length; i++) {
					if (i == 0) {
						riskName.append(";" + risks[i].getStringValue("riskname") + ";");
					} else {
						riskName.append(risks[i].getStringValue("riskname") + ";");
					}
					double lost = risks[i].getDoubleValue("scorelost", 0.0d);
					scorelost_ref += lost;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		DecimalFormat format = new DecimalFormat("0.0");
		String scorelost = format.format(scorelost_ref);
		cardPanel = new BillCardPanel("CMP_SCORE_RECORD_CODE1_3");
		cardPanel.insertRow();
		cardPanel.setEditableByInsertInit();
		cardPanel.addBillCardEditListener(this);
		cardPanel.setValueAt("eventid", new RefItemVO(eventuser, "", userName.toString()));
		//cardPanel.setValueAt("userid",new RefItemVO(eventuser,"",userName.toString()));
		cardPanel.setValueAt("cmp_cmpfile_id", new RefItemVO(cmp_cmpfile_id, "", cmp_cmpfile_name)); // �����ļ�ID
		cardPanel.setValueAt("cmp_cmpfile_name", new StringItemVO(cmp_cmpfile_name)); // �����ļ�����
		cardPanel.setValueAt("cmp_risk_ids", new RefItemVO(refrisks, "", riskName.toString()));
		cardPanel.setValueAt("eventid", new RefItemVO(eventid, "", eventName));
		cardPanel.setValueAt("eventname", new StringItemVO(eventName));
		cardPanel.setValueAt("scorelost_ref", new StringItemVO(scorelost));
		cardPanel.setValueAt("eventdescr", new StringItemVO(eventdescr));
		cardPanel.setValueAt("discovery", findchannel);
		cardDialog = new BillCardDialog(this, "���¼�����Υ�����", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);
		cardPanel.setEditable("eventid", false);
		cardPanel.putClientProperty("user", users);
		if (!"".equals(cmp_cmpfile_id)) { //��������ļ�IDΪ�գ�����Ҫ��
			cardPanel.setEditable("cmp_cmpfile_id", false);
			cardPanel.setEditable("cmp_risk_ids", false);
			cardPanel.setEditable("eventid", false);
		}
		cardPanel.setEditable("scorelost_ref", false);
		btn_confirm = cardDialog.getBtn_confirm();
		btn_confirm.setText("���");
		btn_confirm.addActionListener(this);
		cardDialog.getBtn_save().setVisible(false);
		cardDialog.setVisible(true);
		if (cardDialog.getCloseType() == 1) {
			listPanel.QueryDataByCondition(null);
		}
	}

	/**
	 * �����ɺ󣬸��ݴ��¼��е����Υ����Ա��Ŀ���������Ӧ�����ݡ�
	 */
	public void onConfirm() {
		if (cardPanel.checkValidate()) {
			List insertSql = new ArrayList();
			BillVO vo = cardPanel.getBillVO();
			String[] userid = vo.getStringValue("userid").split(";");
			String[] deptid = vo.getStringValue("deptid").split(";");
			for (int i = 0; i < userid.length; i++) {
				String id = null;
				try {
					id = UIUtil.getSequenceNextValByDS(null, "S_CMP_SCORE_RECORD");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				// Υ����Ա������Ϣsql
				if (deptid.length > 1) {
					insertSql.add(getInsertSQL(id, vo, userid[i], deptid[i]));
				} else {
					insertSql.add(getInsertSQL(id, vo, userid[i], deptid[0]));
				}

				// ���Ѿ������˴������Ա���Ϊ�Ѵ���
				String username = "''";
				for (int j = 0; j < userid.length; j++) {
					username = username + ",'" + userid[j] + "'";
					insertSql.add("update cmp_wardevent_user set relstate = '��' where username in (" + username + ") and cmp_wardevent_id = " + vo.getStringValue("eventid"));
					try {
						insertSql.add(createMsgToUser(userid[i], id, vo.getStringValue("scorelost")));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			try {
				cardDialog.setCloseType(1);
				UIUtil.executeBatchByDS(null, insertSql);
				cardDialog.dispose();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/*
	 * ��ȡ����SQL
	 */
	public String getInsertSQL(String keyid, BillVO vo, String userid, String deptid) {
		InsertSQLBuilder sqlBuilder = new InsertSQLBuilder();
		sqlBuilder.setTableName(vo.getSaveTableName()); // ����
		sqlBuilder.putFieldValue("id", keyid); // id
		sqlBuilder.putFieldValue("Code", vo.getStringValue("Code"));// ����
		sqlBuilder.putFieldValue("cmp_cmpfile_id", vo.getStringValue("cmp_cmpfile_id"));// �����ļ�
		sqlBuilder.putFieldValue("cmp_risk_ids", vo.getStringValue("cmp_risk_ids"));// ���յ�
		sqlBuilder.putFieldValue("scoredate", vo.getStringValue("scoredate"));// �۷�����
		sqlBuilder.putFieldValue("discovery", vo.getStringValue("discovery"));// ��������
		sqlBuilder.putFieldValue("userid", userid);// Υ����Ա
		sqlBuilder.putFieldValue("deptid", deptid);// ��������
		sqlBuilder.putFieldValue("eventdescr", vo.getStringValue("eventdescr"));// �¼�����
		sqlBuilder.putFieldValue("scorestand", vo.getStringValue("scorestand"));// ��׼��Ŀ
		sqlBuilder.putFieldValue("scorelost_ref", vo.getStringValue("scorelost_ref"));// Ӧ�۷�
		sqlBuilder.putFieldValue("pricelost_ref", vo.getStringValue("pricelost_ref"));// Ӧ����
		sqlBuilder.putFieldValue("scorelost", vo.getStringValue("scorelost"));// ʵ�۷�
		sqlBuilder.putFieldValue("pricelost", vo.getStringValue("pricelost"));// ʵ����
		sqlBuilder.putFieldValue("sendstate", vo.getStringValue("sendstate"));// ����״̬
		sqlBuilder.putFieldValue("cmp_cmpfile_name", vo.getStringValue("cmp_cmpfile_name"));// �����ļ�����̬
		sqlBuilder.putFieldValue("cmp_risk_names", vo.getStringValue("cmp_risk_names"));// ���յ�����
		sqlBuilder.putFieldValue("eventid", vo.getStringValue("eventid"));// �¼�ID
		sqlBuilder.putFieldValue("eventname", vo.getStringValue("eventname"));// �¼�����
		sqlBuilder.putFieldValue("attachfile", vo.getStringValue("attachfile"));// ����
		sqlBuilder.putFieldValue("state", vo.getStringValue("state"));// ״̬
		sqlBuilder.putFieldValue("createorg", vo.getStringValue("createorg"));// ��������
		sqlBuilder.putFieldValue("creater", vo.getStringValue("creater"));// ������
		sqlBuilder.putFieldValue("createdate", vo.getStringValue("createdate"));// ����ʱ��
		sqlBuilder.putFieldValue("busitype", vo.getStringValue("busitype"));// ҵ������
		sqlBuilder.putFieldValue("billtype", vo.getStringValue("billtype"));// ��������
		sqlBuilder.putFieldValue("wfprinstanceid", vo.getStringValue("wfprinstanceid"));// ������ID
		sqlBuilder.putFieldValue("create_userid", vo.getStringValue("create_userid"));// ������������
		sqlBuilder.putFieldValue("totalscore", vo.getStringValue("totalscore"));// �ܷ�
		return sqlBuilder.getSQL();
	}

	/**
	 * �õ�����sql����Ҫ�ǰ�userid�ֶλ�����
	 * @param vo
	 * @param userid
	 * @return
	 */
	public String getInsertSQL(String keyid, BillVO vo, HashVO user) {
		StringBuffer sb_sql = new StringBuffer();
		sb_sql.append("insert into " + vo.getSaveTableName()); //
		sb_sql.append("(");

		String[] str_keys = vo.getNeedSaveKeys(); //ȡ��������Ҫ���뱣�����!!
		String[] str_itemTypes = vo.getNeedSaveItemType(); // �ؼ�����
		String[] str_columnTypes = vo.getNeedSaveColumnType(); // �����е�����

		//��ƴ�����е�����
		for (int i = 0; i < str_keys.length; i++) {
			sb_sql.append(str_keys[i]);
			if (i != str_keys.length - 1) {
				sb_sql.append(",");
			}
		}

		sb_sql.append(")");
		sb_sql.append(" values ");
		sb_sql.append("(");

		//��ƴ�����е�Valueֵ!!
		String[] str_realValue = vo.getNeedSaveDataRealValue(); //
		for (int i = 0; i < str_keys.length; i++) {
			if (str_keys[i].equalsIgnoreCase("version")) { //���ĳһ�н�version,��ֱ����1.0����֮
				sb_sql.append("'1'"); //����ǰ汾�ŵĻ���ֱ����1.0��ʼ!!
			} else if (str_keys[i].equalsIgnoreCase("id")) {
				sb_sql.append("'" + keyid + "'"); // ������ֵ!!
			} else if (str_keys[i].equalsIgnoreCase("userid")) {
				sb_sql.append("'" + user.getStringValue("userid") + "'"); // ������ֵ!!
			} else if (str_keys[i].equalsIgnoreCase("username")) {
				sb_sql.append("'" + user.getStringValue("name") + "'"); // ������ֵ!!
			} else if (str_keys[i].equalsIgnoreCase("deptid")) {
				sb_sql.append(user.getStringValue("deptid")); // ������ֵ!!
			} else if (str_keys[i].equalsIgnoreCase("deptname")) {
				sb_sql.append("'" + user.getStringValue("deptname") + "'"); // ������ֵ!!
			} else { //������ǰ汾��
				String str_value = convertSQLValue((str_realValue[i]));
				if (str_value == null || str_value.trim().equals("")) {
					sb_sql.append("null"); // ������ֵ!!
				} else {
					if (str_itemTypes[i].equals("����")) {
						if (str_columnTypes[i] != null && str_columnTypes[i].equals("DATE")) {
							sb_sql.append("to_date('" + str_value + "','YYYY-MM-DD')"); // ������ֵ!!
						} else {
							sb_sql.append("'" + str_value + "'"); // ������ֵ!!
						}
					} else if (str_itemTypes[i].equals("ʱ��")) {
						if (str_columnTypes[i] != null && str_columnTypes[i].equals("DATE")) {
							sb_sql.append("to_date('" + str_value + "','YYYY-MM-DD HH24:MI:SS')"); // ������ֵ!!
						} else {
							sb_sql.append("'" + str_value + "'"); // ������ֵ!!
						}
					} else {
						sb_sql.append("'" + str_value + "'"); // ������ֵ!!
					}
				}
			}

			if (i != str_keys.length - 1) {
				sb_sql.append(",");
			}
		}

		sb_sql.append(")"); //
		return sb_sql.toString();
	}

	private String convertSQLValue(String _value) {
		if (_value == null) {
			return null;
		} else {
			_value = replaceAll(_value, "'", "''"); //
			_value = replaceAll(_value, "\\", "\\\\"); //
			return _value; //
		}
	}

	public String replaceAll(String str_par, String old_item, String new_item) {
		String str_return = "";
		String str_remain = str_par;
		boolean bo_1 = true;
		while (bo_1) {
			int li_pos = str_remain.indexOf(old_item);
			if (li_pos < 0) {
				break;
			} // ����Ҳ���,�򷵻�
			String str_prefix = str_remain.substring(0, li_pos);
			str_return = str_return + str_prefix + new_item; // ������ַ�������ԭ��ǰ�
			str_remain = str_remain.substring(li_pos + old_item.length(), str_remain.length());
		}
		str_return = str_return + str_remain; // ��ʣ��ļ���
		return str_return;
	}

	/**
	 * ���¡�
	 */
	public void onUpdate() {
		BillVO selectVO = listPanel.getSelectedBillVO();
		if (selectVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		// ����ط�ȡ��̨��״̬
		String sql_query = "select sendstate from " + selectVO.getSaveTableName() + " where id = " + selectVO.getStringValue("id");
		try {
			String sendstate = UIUtil.getStringValueByDS(null, sql_query);
			if (!sendstate.equals("1")) { //�����֪ͨ���򲻿����޸�
				MessageBox.show(this, "�Ѿ�����֪ͨ,�������޸ģ�");
				return;
			}
			BillCardDialog updatecardDialog = new BillCardDialog(this, "CMP_SCORE_RECORD_CODE1_4", WLTConstants.BILLDATAEDITSTATE_UPDATE);
			updatecardDialog.getCardPanel().setBillVO(selectVO);
			updatecardDialog.setVisible(true);
			if (updatecardDialog.getCloseType() != 1) {
				return;
			}
			// ���״̬Ϊ4������¿۷��ܺ�
			if (selectVO.getStringValue("sendstate").equals("4")) {
				String sql_update = "";
				if ("".equals(selectVO.getStringValue("totalscore")) || selectVO.getStringValue("totalscore") == null) {
					sql_update = " update " + selectVO.getSaveTableName() + " set totalscore = '" + selectVO.getStringValue("scorelost") + " ' " + " where userid = " + selectVO.getStringValue("userid");
				} else {
					sql_update = " update " + selectVO.getSaveTableName() + " set totalscore = totalscore + '" + selectVO.getStringValue("scorelost") + "' " + " where userid = " + selectVO.getStringValue("userid");
				}
				UIUtil.executeUpdateByDS(null, sql_update);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		listPanel.refreshCurrSelectedRow();
	}

	public TBUtil getTBUtil() {
		if (tbutil == null) {
			tbutil = new TBUtil();
		}
		return tbutil;
	}

	/*
	 * ����Ա��Ϣ����
	 */
	public static String createMsgToUser(String userid, String scroeID, String score) throws Exception {
		InsertSQLBuilder insertMsg = new InsertSQLBuilder("msg_alert");
		insertMsg.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "s_msg_alert"));
		insertMsg.putFieldValue("descr", "����һ��Υ��۷�[" + score + "��]��Ҫ�鿴...");
		insertMsg.putFieldValue("billtype", "Υ��۷�");
		insertMsg.putFieldValue("busitype", "�۷�");
		insertMsg.putFieldValue("createdate", UIUtil.getServerCurrDate());
		insertMsg.putFieldValue("receivuser", userid);
		insertMsg.putFieldValue("linkurl", CmpScoreMyScoreWKPanel.class.getName());
		insertMsg.putFieldValue("dataids", scroeID);
		return insertMsg.getSQL();
	}

}
