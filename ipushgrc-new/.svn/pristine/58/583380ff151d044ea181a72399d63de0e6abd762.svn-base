package com.pushworld.ipushgrc.ui.cmpcheck.p030;

import java.util.ArrayList;
import java.util.List;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 成功防范，违规事件的新增与修改后插入申请评估申请的通用方法。
 */
public class GeneralInsertIntoRiskEval {
	private String loginUserName = ClientEnvironment.getCurrLoginUserVO().getName(); // 登陆人员名称
	private String type = "录入";
	private BillCardPanel cardPanel = null;
	private BillListPanel billList = null;//成功防范列表panel,传过来用于“风险评估申请”对话框的父窗口[YangQing/2013-09-18]

	/**
	 * 
	 * @param _vo  
	 * @param _tableName  
	 * @param _type  录入，修改
	 */
	public GeneralInsertIntoRiskEval(BillListPanel _billList, BillCardPanel _cardPanel, String _tableName, String _type) {
		BillVO _vo = _cardPanel.getBillVO();
		type = _type == null ? type : _type;
		billList = _billList;
		checkRiskEval(_cardPanel, _vo, _tableName);
	}

	// 通用方法，新增和修改都调用。 抽取插入评估流程数据。
	public void checkRiskEval(BillCardPanel cardPanel, BillVO vo, String tableName) {
		if ("Y".equals(vo.getStringValue("ifsubmit"))) {
			String cmpfile_id = vo.getStringValue("cmp_cmpfile_id"); // 流程文件
			String cmpfile_name = vo.getStringViewValue("cmp_cmpfile_id"); // 流程文件
			String sug_addrisks = vo.getStringValue("sug_addrisks", "").trim(); // 建议新增风险点
			String sug_editrisks = vo.getStringValue("sug_editrisks", "").trim(); // 修改风险点
			String sug_delrisks = vo.getStringValue("sug_delrisks", "").trim(); // 删除风险点
			if (("".equals(sug_addrisks) && "".equals(sug_editrisks) && "".equals(sug_delrisks))) {
				return;
			}
			this.cardPanel = cardPanel;
			//			String str_rolecode = new TBUtil().getSysOptionStringValue("风险评估员角色名", "风险评估员"); //
			//			
			try {
				//				String str_roleid = UIUtil.getStringValueByDS(null, "select id from pub_role where code='" + str_rolecode + "'");
				//				if (str_roleid == null) {
				//					MessageBox.show(cardPanel, "系统必须定义一个编码叫[" + str_rolecode + "]的角色!"); //
				//					return; //
				//				}
				//				String blcorpid = null;
				//				if(cmpfileids!=null && !cmpfileids.equals("")){
				//					blcorpid = UIUtil.getStringValueByDS(null, "select blcorpid from cmp_cmpfile where id = "+ cmpfileids);
				//				}
				//				if (str_roleid != null) {
				//					String userids[] = UIUtil.getStringArrayFirstColByDS(null, "select userid from pub_user_role where roleid= '" + str_roleid + "' and userdept = "+blcorpid);
				//					if (userids.length > 0) {
				String userids[] = new String[] { ClientEnvironment.getCurrSessionVO().getLoginUserId() };
				insertIntoRiskEval(vo, cmpfile_id, cmpfile_name, sug_addrisks, sug_editrisks, sug_delrisks, userids, tableName);
				//					}
				//				}

			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	/*
	 * 加入风险评估去走流程.如果 风险分析有数据那么就给风险评估申请加入一条。
	 */
	public void insertIntoRiskEval(BillVO _vo, String _cmpfileid, String _cmpfilename, String _sug_addrisks, String _sug_editrisks, String _sug_delrisks, String[] _userids, String _tableName) {
		String date = "";
		List sqlList = new ArrayList();
		try {
			date = UIUtil.getServerCurrDate();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String str = new TBUtil().getSysOptionStringValue("合规事件流程", "风险;评估申请"); //默认与风险评估申请的流程一致【李春娟/2012-03-28】
		String[] process = str.split(";");
		InsertSQLBuilder insertSql = new InsertSQLBuilder("cmp_riskeval");
		try {
			StringBuffer ids = new StringBuffer();
			for (int i = 0; i < _userids.length; i++) {
				String id = UIUtil.getSequenceNextValByDS(null, "S_CMP_RISKEVAL");
				insertSql.putFieldValue("id", id);
				if (ids.length() == 0) {
					ids.append(id);
				} else {
					ids.append("," + id);
				}

				if (_tableName.equalsIgnoreCase("cmp_ward")) {
					insertSql.putFieldValue("evalreason", "人员[" + loginUserName + "]" + type + "成功防范[" + _vo.getStringValue("wardname") + "]");
				} else {
					insertSql.putFieldValue("evalreason", "人员[" + loginUserName + "]" + type + "违规事件[" + _vo.getStringValue("eventname") + "]");
				}
				insertSql.putFieldValue("evaldate", date);
				insertSql.putFieldValue("cmpfile_id", _cmpfileid);
				insertSql.putFieldValue("cmpfile_name", _cmpfilename);//这里需要记录文件名称字段【李春娟/2013-08-26】
				insertSql.putFieldValue("sug_addrisks", _sug_addrisks);
				insertSql.putFieldValue("sug_editrisks", _sug_editrisks);
				insertSql.putFieldValue("sug_delrisks", _sug_delrisks);
				insertSql.putFieldValue("billtype", process[0]);
				insertSql.putFieldValue("busitype", process[1]);
				insertSql.putFieldValue("create_userid", _userids[i]);
				insertSql.putFieldValue("fromtable", _tableName);
				insertSql.putFieldValue("fromid", _vo.getStringValue("id"));
				sqlList.add(insertSql.getSQL());
			}
			UIUtil.executeBatchByDS(null, sqlList);
			if (MessageBox.confirm(billList, "是否现在发起风险评估申请？")) {
				BillListPanel panel = new BillListPanel("CMP_RISKEVAL_CODE1");
				BillDialog dialog = new BillDialog(billList, "风险评估申请", 800, 300);//原父窗口是传过来已关闭的cardPanel,当用客户端登录系统时，
				//走完风险评估流程，关闭对话框时，会出现切换到其它程序界面的情况（如跑到打开的EXCEL界面中），现改成为传参过来的billList,从而避免这个问题[YangQing/2013-09-18]
				dialog.getContentPane().add(panel);
				panel.QueryDataByCondition(" id in(" + ids + ")");
				panel.setDataFilterCustCondition(" id in(" + ids + ")");
				panel.addBatchBillListButton(new WLTButton[] { WLTButton.createButtonByType(WLTButton.LIST_POPEDIT), WLTButton.createButtonByType(WLTButton.LIST_WORKFLOWSTART_MONITOR) });
				panel.repaintBillListButton();
				dialog.setVisible(true);
			}
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
