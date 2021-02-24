package com.pushworld.ipushlbs.ui.lawconsult.p010;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryEvent;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.styletemplet.format.AbstractStyleWorkPanel_0A;

/**
 * 法律咨询--咨询与答复
 * 
 * @author wupeng
 * 
 */
public class LawConsultWKPanel extends AbstractWorkPanel implements ActionListener, BillListSelectListener, BillListAfterQueryListener {

	private BillListPanel top = null;
	private WLTButton addQuestion = null;// 提问按钮
	private WLTButton editQuestion = null;// 编辑问题按钮
	private WLTButton deleteQuestion = null;// 删除问题
	private BillListPanel bottom = null;
	private WLTButton setStandard = null;// 设为标准答案按钮
	private WLTButton cancelStandard = null;//取消标准答案按钮
	private WLTButton addCommon = null;// 加入常见问题库按钮
	private WLTButton insert = null;// ”回复“按钮
	private WLTButton edit = null;// 编辑回复按钮
	private WLTButton delete = null;//删除回复
	private String loginUserid = null;

	@Override
	public void initialize() {
		loginUserid = ClientEnvironment.getInstance().getLoginUserID();
		top = new BillListPanel("LAW_CONSULT_CODE1");
		top.getBillListBtn("comm_listinsert").setText("提问");
		top.repaintBillListButton();
		top.addBillListSelectListener(this);
		top.addBillListAfterQueryListener(this);//增加查询后事件，清空子表【李春娟/2012-08-08】
		addQuestion = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT);
		addQuestion.setText("提问");
		editQuestion = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT);
		deleteQuestion = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
		top.addBatchBillListButton(new WLTButton[] { addQuestion, editQuestion, deleteQuestion, WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD) });
		top.repaintBillListButton();

		bottom = new BillListPanel("CONSULT_REPLY_CODE1");
		bottom.addBillListSelectListener(this);
		setStandard = new WLTButton("设为标准答案");
		cancelStandard = new WLTButton("取消标准答案");
		addCommon = new WLTButton("加入到常见问题库");
		setStandard.addActionListener(this);
		cancelStandard.addActionListener(this);
		addCommon.addActionListener(this);
		insert = new WLTButton("回复");
		edit = new WLTButton("修改");
		delete = new WLTButton("删除");
		if (insert != null)
			insert.addActionListener(this);
		if (edit != null)
			edit.addActionListener(this);
		if (delete != null)
			delete.addActionListener(this);

		bottom.addBatchBillListButton(new WLTButton[] { insert, edit, delete, setStandard, cancelStandard, addCommon, WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD) });
		bottom.repaintBillListButton();

		WLTSplitPane split = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, top, bottom);
		split.setDividerLocation(300);
		this.add(split);
	}

	public void afterSysInitialize(AbstractStyleWorkPanel_0A panel) throws Exception {

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == setStandard)// 设为标准答案
			setStandardAnswer();
		if (e.getSource() == cancelStandard)//取消标准答案
			cancelStandard();
		if (e.getSource() == addCommon)// 加入常见问题库
			addToComm();
		if (e.getSource() == insert)// 新建一条回复
			doInsert();
		if (e.getSource() == edit)// 编辑一条回复
			doEdit();
		if (e.getSource() == delete)
			doDelete();
	}

	//删除时，提醒设为标准答案的不能删.【杨庆/2012-08-07】
	private void doDelete() {
		BillVO vo = bottom.getSelectedBillVO();
		if (vo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if (getTopBillListPanel().getSelectedBillVO() == null) {
			MessageBox.show(bottom, "请选择该条回复的问题!");
			return;
		}

		String flag = vo.getStringValue("IS_STANDARD");
		if ("是".equals(flag)) {// 要修改的记录是标准答案
			MessageBox.show(bottom, "该回复已被设为标准答案,请勿删除!");
			return;
		}
		bottom.doDelete(false);
	}

	// 编辑
	private void doEdit() {
		BillVO vo = bottom.getSelectedBillVO();
		if (vo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if (getTopBillListPanel().getSelectedBillVO() == null) {
			MessageBox.show(bottom, "请选择该条回复的问题!");
			return;
		}

		String flag = vo.getStringValue("IS_STANDARD");
		if (flag.equals("是")) {// 要修改的记录是标准答案
			MessageBox.show(bottom, "该回复已被设为标准答案,请勿修改!");
			return;
		}
		bottom.doEdit();
	}

	// 新建回复
	private void doInsert() {
		BillVO vo = top.getSelectedBillVO();
		if (vo == null) {
			MessageBox.show(bottom, "请选择问题!");
			return;
		}
		String id = vo.getStringValue("id");
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("CONSULT_ID", id);
		String title = "回复：" + vo.getStringValue("TITLE");
		map.put("RE_TITLE", title);
		bottom.doInsert(map);
		bottom.clearTable();
		bottom.QueryDataByCondition("  CONSULT_ID = " + id);
	}

	// 加入到常见问题库
	private void addToComm() {
		BillVO quesstionvo = top.getSelectedBillVO();// 分割器上方面板选择的vo
		BillVO answervo = bottom.getSelectedBillVO();// 分割器下方面板选择的vo
		BillVO standardvo = null;// 标准答案vo

		if (quesstionvo == null) {
			MessageBox.show(bottom, "请选择要加入库中的问题!");
			return;
		}

		String quesstionId = quesstionvo.getStringValue("id");
		boolean exist = false;// 常见问题库中已经存在该条问题

		// 遍历··看该条问题是否已经加入到常见问题库
		try {
			HashVO[] hashvos = UIUtil.getHashVoArrayByDS(null, "select * from comm_quesstion");
			for (HashVO vo : hashvos) {
				if (quesstionId.equals(vo.getStringValue("quesstion_id"))) {
					exist = true;
					break;
				}
			}

		} catch (WLTRemoteException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (exist) {
			MessageBox.show(bottom, "该问题已加入到常见问题库,请勿重复操作!");
			return;
		}

		BillVO[] answers = bottom.getBillVOs();
		for (BillVO vo : answers) {
			if ("是".equals(vo.getStringValue("IS_STANDARD"))) {
				standardvo = vo;
				break;
			}
		}

		if (standardvo == null && answervo == null) {
			MessageBox.show(bottom, "该问题无标准答案,请先设置标准答案再加入到问题库!");
			return;
		}

		String sql = null;
		if (standardvo != null) {// 如果有标准答案，则优先使用标准答案
			sql = this.getInsertSql(quesstionvo, standardvo, "S_COMM_QUESSTION");
		} else {// 如果没有标准答案
			sql = this.getInsertSql(quesstionvo, answervo, "S_COMM_QUESSTION");
		}

		if (sql != null) {
			try {
				UIUtil.executeUpdateByDS(null, sql);
				MessageBox.show(bottom, "已加入!");

			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String getInsertSql(BillVO quesstionvo, BillVO standardvo, String sequenceName) {
		StringBuffer sb = new StringBuffer("Insert Into comm_quesstion(Id,title,quesstion_id,detail,busitype," + "create_people,create_dept,create_time,query_file,reply,re_file,re_people,re_time,re_dept)Values");
		String id = null;
		try {
			id = UIUtil.getSequenceNextValByDS(null, sequenceName);
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (id == null)
			return null;

		sb.append("(");
		sb.append(id);// id

		sb.append(",'" + quesstionvo.getStringValue("title") + "'");// 标题
		sb.append(",'" + quesstionvo.getStringValue("id") + "'");// 关联的问题
		if (quesstionvo.getStringValue("detail") != null)
			sb.append(",'" + quesstionvo.getStringValue("detail") + "'");// 问题详情
		else
			sb.append(",''");// 问题详情
		sb.append(",'" + quesstionvo.getStringValue("busitype") + "'");// 业务类型
		sb.append(",'" + quesstionvo.getStringValue("create_user") + "'");// 创建人
		sb.append(",'" + quesstionvo.getStringValue("create_user_dept") + "'");// 创建机构
		sb.append(",'" + quesstionvo.getStringValue("create_date") + "'");// 创建时间
		if (quesstionvo.getStringValue("files") != null)
			sb.append(",'" + quesstionvo.getStringValue("files") + "'");// 提问附件
		else
			sb.append(",''");

		if (standardvo.getStringValue("re_detail") != null)
			sb.append(",'" + standardvo.getStringValue("re_detail") + "'");// 回答详情
		else
			sb.append(",''");// 回答详情
		if (standardvo.getStringValue("re_files") != null)
			sb.append(",'" + standardvo.getStringValue("re_files") + "'");// 回答附件
		else
			sb.append(",''");
		sb.append(",'" + standardvo.getStringValue("re_person") + "'");// 回复人
		sb.append(",'" + standardvo.getStringValue("re_time") + "'");// 回复时间
		sb.append(",'" + standardvo.getStringValue("re_person_dept") + "'");// 回复机构
		sb.append(")");

		return sb.toString();
	}

	// 设为标准答案
	private void setStandardAnswer() {
		BillVO question = top.getSelectedBillVO();
		BillVO vo = bottom.getSelectedBillVO();
		BillVO standard = null;

		if (vo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if (getTopBillListPanel().getSelectedBillVO() == null) {
			MessageBox.show(bottom, "请选择该条回复的问题!");
			return;
		}

		if ("是".equals(vo.getStringValue("IS_STANDARD"))) {
			MessageBox.show(bottom, "此回复已是标准答案,请勿重复操作!");
			return;
		}
		BillVO[] vos = bottom.getBillVOs();
		boolean flag = false;// 是否有标准答案，如果有，则为true
		for (BillVO v : vos) {
			if ("是".equals(v.getStringValue("IS_STANDARD"))) {
				standard = v;
				flag = true;
				break;
			}
		}

		if (flag) {
			if (!MessageBox.confirm(bottom, "此问题已有标准答案,是否继续?"))
				return;
		}

		String id = vo.getStringValue("id");
		String sql1 = "update consult_reply set IS_STANDARD = '是' where id = " + id;
		try {
			if (standard == null) {
				UIUtil.executeUpdateByDS(null, sql1);
			} else {
				String sql2 = "update consult_reply set IS_STANDARD = '否' where id = " + standard.getStringValue("id");

				//重设标准答案后，更新问题库中的答复信息。【杨庆/2012-08-08】
				StringBuilder updateStandard = new StringBuilder();
				String reply = vo.getStringValue("re_detail");// 回复
				String replyfile = vo.getStringValue("re_files");//回复附件

				updateStandard.append("update comm_quesstion set ");
				if (reply != null) {
					updateStandard.append(" reply='" + reply + "',");
				}
				if (replyfile != null) {
					updateStandard.append(" re_file='" + replyfile + "',");
				}
				updateStandard.append(" re_people='" + vo.getStringValue("re_person") + "',");// 回复人
				updateStandard.append(" re_time='" + vo.getStringValue("re_time") + "',");// 回复时间
				updateStandard.append(" re_dept='" + vo.getStringValue("re_person_dept") + "' ");// 回复机构
				updateStandard.append(" where quesstion_id = " + question.getStringValue("id"));

				UIUtil.executeBatchByDS(null, new String[] { sql1, sql2, updateStandard.toString() });
			}
			MessageBox.show(bottom, "设置成功!");
			bottom.refreshData();
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 取消标准答案--为方便更改标准答案，加这一按钮【杨庆/2012-08-08】
	 */
	private void cancelStandard() {

		if (getTopBillListPanel().getSelectedBillVO() == null) {
			MessageBox.show(bottom, "请选择该条回复的问题!");
			return;
		}

		BillVO quesstionvo = top.getSelectedBillVO();// 分割器上方面板选择的vo
		String quesstionId = quesstionvo.getStringValue("id");
		boolean exist = false;// 常见问题库中已经存在该条问题
		// 遍历··看该条问题是否已经加入到常见问题库
		try {
			HashVO[] hashvos = UIUtil.getHashVoArrayByDS(null, "select * from comm_quesstion");
			for (HashVO voo : hashvos) {
				if (quesstionId.equals(voo.getStringValue("quesstion_id"))) {
					exist = true;
					break;
				}
			}

		} catch (WLTRemoteException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (exist) {
			MessageBox.show(bottom, "该问题已加入常见问题库，可设置其他回复为标准答案，但不可取消标准答案!");
			return;
		}

		BillVO[] upStandard = bottom.getBillVOs();
		BillVO standard = null;
		boolean flag = false;// 是否有标准答案，如果有，则为true
		for (BillVO v : upStandard) {
			if ("是".equals(v.getStringValue("IS_STANDARD"))) {
				standard = v;
				flag = true;
				break;
			}
		}

		if ((!exist) && flag) {
			try {
				String sql = "update consult_reply set IS_STANDARD = '否' where id = " + standard.getStringValue("id");
				UIUtil.executeUpdateByDS(null, sql);
				bottom.refreshData();
			} catch (Exception e) {
				e.printStackTrace();
			}
			MessageBox.show(bottom, "取消成功!");
			return;
		}
		if (!exist && !flag) {
			MessageBox.show(bottom, "该问题尚未设置标准答案，不必执行此操作!");
			return;
		}

	}

	// 显示相应的回复信息
	public void onBillListSelectChanged(BillListSelectionEvent e) {
		if (e.getSource() == top) {
			onClickQuesstionBillVO(e);
		} else if (e.getSource() == bottom) {
			onClickAnserBillVO(e);
		}
	}

	private void onClickAnserBillVO(BillListSelectionEvent e) {//不是本人回复的，不能删除、编辑
		BillVO selvo = e.getCurrSelectedVO();//
		String answerUserid = selvo.getStringValue("RE_PERSON");
		if (loginUserid.equals(answerUserid)) {
			delete.setEnabled(true);
			edit.setEnabled(true);
		} else {
			delete.setEnabled(false);
			edit.setEnabled(false);
		}

	}

	private void onClickQuesstionBillVO(BillListSelectionEvent e) {//选择了问题
		bottom.clearTable();
		BillVO vo = top.getSelectedBillVO();
		if (vo == null)
			return;
		String id = vo.getStringValue("id");

		bottom.QueryDataByCondition(" CONSULT_ID = " + id);
		bottom.refreshCurrData();
		if (loginUserid.equals(vo.getStringValue("CREATE_USER"))) {//如果选择的问题的提问人是当前登录用户，责可以删除
			deleteQuestion.setEnabled(true);
			editQuestion.setEnabled(true);
			setStandard.setEnabled(true);
		} else {
			deleteQuestion.setEnabled(false);
			editQuestion.setEnabled(false);
			setStandard.setEnabled(false);
			return;
		}
		if (bottom.getBillVOs().length != 0) {
			editQuestion.setEnabled(false);
			deleteQuestion.setEnabled(false);
		} else {
			editQuestion.setEnabled(true);
			deleteQuestion.setEnabled(true);
		}
	}

	private BillListPanel getTopBillListPanel() {
		return top;
	}

	//增加查询后事件，清空子表【李春娟/2012-08-08】
	public void onBillListAfterQuery(BillListAfterQueryEvent _event) {
		bottom.clearTable();
	}

}
