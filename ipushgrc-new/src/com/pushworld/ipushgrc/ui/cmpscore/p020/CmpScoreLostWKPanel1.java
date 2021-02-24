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
 * 违规积分录入
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
		btn_insert = new WLTButton("直接创建");
		btn_insertfromevent = new WLTButton("从事件创建");
		btn_update = new WLTButton("修改");
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
				onConfirm(); //从事件创建 点击完成 按钮
			}
		} else if (obj == btn_delete) {
			onDelete();
		} else if (obj == btn_conf) {
			onConf();
		}

	}

	/*
	 * 直接创建
	 */
	public void onInsert() {
		cardPanelInsert = new BillCardPanel("CMP_SCORE_RECORD_CODE1");
		cardPanelInsert.setEditable("cmp_risk_ids", false);
		cardPanelInsert.setEditableByInsertInit();
		cardPanelInsert.addBillCardEditListener(this); //加入卡片监听
		cardPanelInsert.insertRow();
		cardDialogInsert = new BillCardDialog(this, "直接新增违规积分", cardPanelInsert, WLTConstants.BILLDATAEDITSTATE_INSERT);
		btn_conf = cardDialogInsert.getBtn_confirm();
		btn_conf.setText("完成");
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
					// 违规人员数据信息sql
					insertSql.add(getInsertSQL(id, vo, userid[i], deptid));
					String username = "''";
					for (int j = 0; j < userid.length; j++) {
						username = username + ",'" + userid[j] + "'";
						insertSql.add("update cmp_wardevent_user set relstate = '是' where username in (" + username + ") and cmp_wardevent_id = " + vo.getStringValue("eventid"));
						insertSql.add(createMsgToUser(userid[j], id, vo.getStringValue("scorelost")));
					}
				}
				cardDialogInsert.setCloseType(1);
				UIUtil.executeBatchByDS(null, insertSql);
				cardDialogInsert.dispose();

			} else {
				MessageBox.show(this, "【事件确认单编号】不满足唯一性，请输入其他数据!");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// 删除操作
	private void onDelete() {
		BillVO billvo = listPanel.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		// 这个地方取后台的状态
		String sql_query = "select sendstate from " + billvo.getSaveTableName() + " where id = " + billvo.getStringValue("id");
		try {
			String sendstate = UIUtil.getStringValueByDS(null, sql_query);
			if (sendstate.equals("1")) {
				if (MessageBox.confirmDel(this)) {
					UIUtil.executeBatchByDS(null, new String[] { "delete from " + billvo.getSaveTableName() + " where id = " + billvo.getStringValue("id") });
					listPanel.removeSelectedRow();
				}
			} else {
				MessageBox.show(this, "通知已经发送,不可以删除！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *  卡片编辑事件
	 */

	public void onBillCardValueChanged(BillCardEditEvent evt) {
		try {
			if (evt.getItemKey() != null) {
				if (evt.getItemKey().equals("scorestand")) { //如果点击了标准条目
					if (evt.getNewObject() != null) {
						RefItemVO itemvo = (RefItemVO) evt.getNewObject(); //取得参照值
						String[] itemid = itemvo.getId().split(";");
						String in_sql = ""; // in() 中语句
						for (int i = 1; i < itemid.length; i++) { //从1开始，因为存储方式为;XX;XX;
							in_sql = in_sql + itemid[i] + ",";
							if (i == itemid.length - 1)
								in_sql = in_sql + itemid[i];
						}
						String sql = "Select Sum(score) From cmp_score_stand Where Id In (" + in_sql + ") ";
						String score_sum = UIUtil.getStringValueByDS(null, sql); //取得所选参照要扣分的和
						sql = "select Sum(amount) from cmp_score_stand where Id in (" + in_sql + ")";
						String price_sum = UIUtil.getStringValueByDS(null, sql); //取得罚款总和

						BillCardPanel billcard = (BillCardPanel) evt.getSource(); //得到当前card
						billcard.setValueAt("scorelost_ref", new StringItemVO(score_sum)); //为卡片上的这项赋值
						billcard.setValueAt("pricelost_ref", new StringItemVO(price_sum));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 从事件创建
	 */
	public void onInsertFromEvent() {
		BillListDialog listDialog = new BillListDialog(this, "请选择一个事件,点击下一步", "CMP_EVENT_CODE1");
		listDialog.getBilllistPanel().setDataFilterCustCondition(" eventuser is not null ");
		listDialog.getBtn_confirm().setText("下一步");
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
		String cmp_cmpfile_id = eventVO.getStringValue("cmp_cmpfile_id"); // 流程文件
		String cmp_cmpfile_name = eventVO.getStringValue("cmp_cmpfile_name");
		String refrisks = eventVO.getStringValue("refrisks"); // 风险点
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
		cardPanel.setValueAt("cmp_cmpfile_id", new RefItemVO(cmp_cmpfile_id, "", cmp_cmpfile_name)); // 流程文件ID
		cardPanel.setValueAt("cmp_cmpfile_name", new StringItemVO(cmp_cmpfile_name)); // 流程文件名称
		cardPanel.setValueAt("cmp_risk_ids", new RefItemVO(refrisks, "", riskName.toString()));
		cardPanel.setValueAt("eventid", new RefItemVO(eventid, "", eventName));
		cardPanel.setValueAt("eventname", new StringItemVO(eventName));
		cardPanel.setValueAt("scorelost_ref", new StringItemVO(scorelost));
		cardPanel.setValueAt("eventdescr", new StringItemVO(eventdescr));
		cardPanel.setValueAt("discovery", findchannel);
		cardDialog = new BillCardDialog(this, "从事件创建违规积分", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);
		cardPanel.setEditable("eventid", false);
		cardPanel.putClientProperty("user", users);
		if (!"".equals(cmp_cmpfile_id)) { //如果流程文件ID为空，则不需要掉
			cardPanel.setEditable("cmp_cmpfile_id", false);
			cardPanel.setEditable("cmp_risk_ids", false);
			cardPanel.setEditable("eventid", false);
		}
		cardPanel.setEditable("scorelost_ref", false);
		btn_confirm = cardDialog.getBtn_confirm();
		btn_confirm.setText("完成");
		btn_confirm.addActionListener(this);
		cardDialog.getBtn_save().setVisible(false);
		cardDialog.setVisible(true);
		if (cardDialog.getCloseType() == 1) {
			listPanel.QueryDataByCondition(null);
		}
	}

	/**
	 * 点击完成后，根据从事件中导入的违规人员数目，插入相对应的数据。
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
				// 违规人员数据信息sql
				if (deptid.length > 1) {
					insertSql.add(getInsertSQL(id, vo, userid[i], deptid[i]));
				} else {
					insertSql.add(getInsertSQL(id, vo, userid[i], deptid[0]));
				}

				// 将已经进行了处理的人员标记为已处理
				String username = "''";
				for (int j = 0; j < userid.length; j++) {
					username = username + ",'" + userid[j] + "'";
					insertSql.add("update cmp_wardevent_user set relstate = '是' where username in (" + username + ") and cmp_wardevent_id = " + vo.getStringValue("eventid"));
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
	 * 获取插入SQL
	 */
	public String getInsertSQL(String keyid, BillVO vo, String userid, String deptid) {
		InsertSQLBuilder sqlBuilder = new InsertSQLBuilder();
		sqlBuilder.setTableName(vo.getSaveTableName()); // 表名
		sqlBuilder.putFieldValue("id", keyid); // id
		sqlBuilder.putFieldValue("Code", vo.getStringValue("Code"));// 编码
		sqlBuilder.putFieldValue("cmp_cmpfile_id", vo.getStringValue("cmp_cmpfile_id"));// 流程文件
		sqlBuilder.putFieldValue("cmp_risk_ids", vo.getStringValue("cmp_risk_ids"));// 风险点
		sqlBuilder.putFieldValue("scoredate", vo.getStringValue("scoredate"));// 扣分日期
		sqlBuilder.putFieldValue("discovery", vo.getStringValue("discovery"));// 发现渠道
		sqlBuilder.putFieldValue("userid", userid);// 违规人员
		sqlBuilder.putFieldValue("deptid", deptid);// 所属机构
		sqlBuilder.putFieldValue("eventdescr", vo.getStringValue("eventdescr"));// 事件描述
		sqlBuilder.putFieldValue("scorestand", vo.getStringValue("scorestand"));// 标准科目
		sqlBuilder.putFieldValue("scorelost_ref", vo.getStringValue("scorelost_ref"));// 应扣分
		sqlBuilder.putFieldValue("pricelost_ref", vo.getStringValue("pricelost_ref"));// 应罚款
		sqlBuilder.putFieldValue("scorelost", vo.getStringValue("scorelost"));// 实扣分
		sqlBuilder.putFieldValue("pricelost", vo.getStringValue("pricelost"));// 实罚款
		sqlBuilder.putFieldValue("sendstate", vo.getStringValue("sendstate"));// 发送状态
		sqlBuilder.putFieldValue("cmp_cmpfile_name", vo.getStringValue("cmp_cmpfile_name"));// 流程文件名称态
		sqlBuilder.putFieldValue("cmp_risk_names", vo.getStringValue("cmp_risk_names"));// 风险点名称
		sqlBuilder.putFieldValue("eventid", vo.getStringValue("eventid"));// 事件ID
		sqlBuilder.putFieldValue("eventname", vo.getStringValue("eventname"));// 事件名称
		sqlBuilder.putFieldValue("attachfile", vo.getStringValue("attachfile"));// 附件
		sqlBuilder.putFieldValue("state", vo.getStringValue("state"));// 状态
		sqlBuilder.putFieldValue("createorg", vo.getStringValue("createorg"));// 创建机构
		sqlBuilder.putFieldValue("creater", vo.getStringValue("creater"));// 创建人
		sqlBuilder.putFieldValue("createdate", vo.getStringValue("createdate"));// 创建时间
		sqlBuilder.putFieldValue("busitype", vo.getStringValue("busitype"));// 业务类型
		sqlBuilder.putFieldValue("billtype", vo.getStringValue("billtype"));// 单据类型
		sqlBuilder.putFieldValue("wfprinstanceid", vo.getStringValue("wfprinstanceid"));// 工作流ID
		sqlBuilder.putFieldValue("create_userid", vo.getStringValue("create_userid"));// 工作流创建人
		sqlBuilder.putFieldValue("totalscore", vo.getStringValue("totalscore"));// 总分
		return sqlBuilder.getSQL();
	}

	/**
	 * 得到插入sql。主要是把userid字段换掉。
	 * @param vo
	 * @param userid
	 * @return
	 */
	public String getInsertSQL(String keyid, BillVO vo, HashVO user) {
		StringBuffer sb_sql = new StringBuffer();
		sb_sql.append("insert into " + vo.getSaveTableName()); //
		sb_sql.append("(");

		String[] str_keys = vo.getNeedSaveKeys(); //取得所有需要参与保存的列!!
		String[] str_itemTypes = vo.getNeedSaveItemType(); // 控件类型
		String[] str_columnTypes = vo.getNeedSaveColumnType(); // 数据列的类型

		//先拼出所有的列名
		for (int i = 0; i < str_keys.length; i++) {
			sb_sql.append(str_keys[i]);
			if (i != str_keys.length - 1) {
				sb_sql.append(",");
			}
		}

		sb_sql.append(")");
		sb_sql.append(" values ");
		sb_sql.append("(");

		//再拼出所有的Value值!!
		String[] str_realValue = vo.getNeedSaveDataRealValue(); //
		for (int i = 0; i < str_keys.length; i++) {
			if (str_keys[i].equalsIgnoreCase("version")) { //如果某一列叫version,则直接用1.0处理之
				sb_sql.append("'1'"); //如果是版本号的话则直接用1.0开始!!
			} else if (str_keys[i].equalsIgnoreCase("id")) {
				sb_sql.append("'" + keyid + "'"); // 真正的值!!
			} else if (str_keys[i].equalsIgnoreCase("userid")) {
				sb_sql.append("'" + user.getStringValue("userid") + "'"); // 真正的值!!
			} else if (str_keys[i].equalsIgnoreCase("username")) {
				sb_sql.append("'" + user.getStringValue("name") + "'"); // 真正的值!!
			} else if (str_keys[i].equalsIgnoreCase("deptid")) {
				sb_sql.append(user.getStringValue("deptid")); // 真正的值!!
			} else if (str_keys[i].equalsIgnoreCase("deptname")) {
				sb_sql.append("'" + user.getStringValue("deptname") + "'"); // 真正的值!!
			} else { //如果不是版本号
				String str_value = convertSQLValue((str_realValue[i]));
				if (str_value == null || str_value.trim().equals("")) {
					sb_sql.append("null"); // 真正的值!!
				} else {
					if (str_itemTypes[i].equals("日历")) {
						if (str_columnTypes[i] != null && str_columnTypes[i].equals("DATE")) {
							sb_sql.append("to_date('" + str_value + "','YYYY-MM-DD')"); // 真正的值!!
						} else {
							sb_sql.append("'" + str_value + "'"); // 真正的值!!
						}
					} else if (str_itemTypes[i].equals("时间")) {
						if (str_columnTypes[i] != null && str_columnTypes[i].equals("DATE")) {
							sb_sql.append("to_date('" + str_value + "','YYYY-MM-DD HH24:MI:SS')"); // 真正的值!!
						} else {
							sb_sql.append("'" + str_value + "'"); // 真正的值!!
						}
					} else {
						sb_sql.append("'" + str_value + "'"); // 真正的值!!
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
			} // 如果找不到,则返回
			String str_prefix = str_remain.substring(0, li_pos);
			str_return = str_return + str_prefix + new_item; // 将结果字符串加上原来前辍
			str_remain = str_remain.substring(li_pos + old_item.length(), str_remain.length());
		}
		str_return = str_return + str_remain; // 将剩余的加上
		return str_return;
	}

	/**
	 * 更新。
	 */
	public void onUpdate() {
		BillVO selectVO = listPanel.getSelectedBillVO();
		if (selectVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		// 这个地方取后台的状态
		String sql_query = "select sendstate from " + selectVO.getSaveTableName() + " where id = " + selectVO.getStringValue("id");
		try {
			String sendstate = UIUtil.getStringValueByDS(null, sql_query);
			if (!sendstate.equals("1")) { //如果已通知，则不可再修改
				MessageBox.show(this, "已经发送通知,不可再修改！");
				return;
			}
			BillCardDialog updatecardDialog = new BillCardDialog(this, "CMP_SCORE_RECORD_CODE1_4", WLTConstants.BILLDATAEDITSTATE_UPDATE);
			updatecardDialog.getCardPanel().setBillVO(selectVO);
			updatecardDialog.setVisible(true);
			if (updatecardDialog.getCloseType() != 1) {
				return;
			}
			// 如果状态为4，则更新扣分总和
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
	 * 给人员消息提醒
	 */
	public static String createMsgToUser(String userid, String scroeID, String score) throws Exception {
		InsertSQLBuilder insertMsg = new InsertSQLBuilder("msg_alert");
		insertMsg.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "s_msg_alert"));
		insertMsg.putFieldValue("descr", "你有一个违规扣分[" + score + "分]需要查看...");
		insertMsg.putFieldValue("billtype", "违规扣分");
		insertMsg.putFieldValue("busitype", "扣分");
		insertMsg.putFieldValue("createdate", UIUtil.getServerCurrDate());
		insertMsg.putFieldValue("receivuser", userid);
		insertMsg.putFieldValue("linkurl", CmpScoreMyScoreWKPanel.class.getName());
		insertMsg.putFieldValue("dataids", scroeID);
		return insertMsg.getSQL();
	}

}
