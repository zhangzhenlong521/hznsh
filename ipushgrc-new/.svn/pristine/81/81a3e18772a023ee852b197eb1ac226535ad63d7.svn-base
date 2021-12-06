package com.pushworld.ipushgrc.ui.cmpscore.p030;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;

import com.pushworld.ipushgrc.ui.keywordreplace.TemplateToWordUIUtil;

/**
 * 违规处理
 * 
 * @author hm
 * 
 */
public class CmpScorePunishWKPanel extends AbstractWorkPanel implements ActionListener {
	// 查看积分记录, 查看惩罚记录, 生成违规惩罚单
	private WLTButton btn_showscore, btn_showpunish, btn_createpunish, btn_exportword,btn_refresh;
	private BillListPanel listPanel;
	private String loginUserID = ClientEnvironment.getCurrLoginUserVO().getName();
	// private String sql = "";
	private String year = new TBUtil().getCurrDate().substring(0, 4);
	private HashVO[] cmp_score_rule = null;

	public void initialize() {
		// todo: 年度要可以选择!
		try {
			cmp_score_rule = UIUtil.getHashVoArrayByDS(null, " select * from cmp_score_rule");
		} catch (Exception e) {
			e.printStackTrace();
		}
		listPanel = new BillListPanel("CMP_SCORE_PUNISH");
		btn_showscore = new WLTButton("查看积分记录");
		btn_showpunish = new WLTButton("查看惩罚记录");
		btn_createpunish = new WLTButton("生成违规惩罚单");
		btn_exportword = new WLTButton("惩罚单预览");
		btn_refresh = new WLTButton("刷新");  //重新计算可处理的员工
		btn_refresh.addActionListener(this);
		listPanel.getQuickQueryPanel().addBillQuickActionListener(this);
		btn_showpunish.addActionListener(this);
		btn_showscore.addActionListener(this);
		btn_createpunish.addActionListener(this);
		btn_exportword.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] { btn_showscore, btn_showpunish, btn_exportword, btn_createpunish,btn_refresh });
		listPanel.repaintBillListButton();
		try {
			loadData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.add(listPanel);
	}

	private void loadData() throws Exception {
		listPanel.clearTable();
		String year = new TBUtil().getCurrDate();
		year = year.substring(0, 4);
		int curryear = Integer.parseInt(year);
		//修改bug（新的一年不能处理前年未处理的违规积分，目前只支持前两年。）
		HashVO[] score_lost_1 = UIUtil.getHashVoArrayByDS(null, "select userid,sum(scorelost)as scorenow ,max(scoredate) as scoredate from cmp_score_lost where scoredate >'" + (curryear - 2) + "' and  scoredate <'" + (curryear - 1) + "' group by userid "
				+ " union all select userid,sum(scorelost)as scorenow ,max(scoredate) as scoredate from cmp_score_lost where scoredate >'" + (curryear - 1) + "'  and  scoredate <'" + (curryear) + "' group by userid "
				+ " union all select userid,sum(scorelost)as scorenow ,max(scoredate) as scoredate from cmp_score_lost where scoredate >'" + (curryear) + "'  group by userid ");
		HashVO[] score_punish_1 = UIUtil.getHashVoArrayByDS(null, "select userid, max(scoretotal)as lastscore,'" + (curryear - 2) + "' as cyear from cmp_score_punish where lastdate > '" + (curryear - 2) + "' and lastdate < '" + (curryear - 1) + "' group by userid "
				+ " union all select userid, max(scoretotal)as lastscore ,'" + (curryear - 1) + "' as cyear from cmp_score_punish where lastdate > '" + (curryear - 1) + "' and lastdate < '" + (curryear) + "'  group by userid " + " union all select userid, max(scoretotal)as lastscore,'" + (curryear)
				+ "' as cyear from cmp_score_punish where lastdate > '" + (curryear) + "' group by userid ");
		HashMap user = UIUtil.getHashMapBySQLByDS(null, "select id ,name from pub_user");
		List list = new ArrayList();
		for (int i = 0; i < score_lost_1.length; i++) {
			String userid = score_lost_1[i].getStringValue("userid");
			String scorenow = score_lost_1[i].getStringValue("scorenow");
			String scoredate = score_lost_1[i].getStringValue("scoredate");
			float scorenow_1 = Float.parseFloat(scorenow);
			// 如果上次扣过了，那么需要比较一下。
			boolean ifpunish = false;
			String lastscore = null;
			for (int j = 0; j < score_punish_1.length; j++) {
				String p_userid = score_punish_1[j].getStringValue("userid");
				String p_lastscore = score_punish_1[j].getStringValue("lastscore");
				String p_cyear = score_punish_1[j].getStringValue("cyear");
				if (scoredate.contains(p_cyear) && userid.equals(p_userid)) { //
					ifpunish = true;
					lastscore = p_lastscore;
					break;
				}
			}
			if (ifpunish) {
				float lastscore_1 = Float.parseFloat(lastscore);
				if (lastscore_1 < scorenow_1) {
					for (int k = 0; k < cmp_score_rule.length; k++) {
						int rulescore = cmp_score_rule[k].getIntegerValue("score");
						if (k != cmp_score_rule.length - 1) {
							if (rulescore <= scorenow_1 && rulescore > lastscore_1 && cmp_score_rule[k + 1].getIntegerValue("score") > scorenow_1) {
								BillVO vo = new BillVO();
								vo.setDatas(new Object[7]);
								vo.setKeys(new String[] { "userid", "username", "scorenow", "scorenext", "punish", "lastscore", "date" });
								vo.setObject("userid", new StringItemVO(userid));
								vo.setObject("username", new StringItemVO((String) user.get(userid)));
								vo.setObject("scorenow", new StringItemVO(scorenow));
								vo.setObject("scorenext", new StringItemVO(cmp_score_rule[k].getStringValue("score")));
								vo.setObject("punish", new StringItemVO(cmp_score_rule[k].getStringValue("punish")));
								vo.setObject("lastscore", new StringItemVO(lastscore));
								vo.setObject("date",new RefItemVO(scoredate,"",scoredate));
								list.add(vo);
								break;
							}
						} else {
							if (rulescore <= scorenow_1 && rulescore > lastscore_1) {
								BillVO vo = new BillVO();
								vo.setDatas(new Object[7]);
								vo.setKeys(new String[] { "userid", "username", "scorenow", "scorenext", "punish", "lastscore", "date" });
								vo.setObject("userid", new StringItemVO(userid));
								vo.setObject("username", new StringItemVO((String) user.get(userid)));
								vo.setObject("scorenow", new StringItemVO(scorenow));
								vo.setObject("scorenext", new StringItemVO(cmp_score_rule[k].getStringValue("score")));
								vo.setObject("punish", new StringItemVO(cmp_score_rule[k].getStringValue("punish")));
								vo.setObject("lastscore", new StringItemVO(lastscore));
								vo.setObject("date", new RefItemVO(scoredate,"",scoredate));
								list.add(vo);
								break;
							}
						}
					}
					/**
					 * 当前 上次 5 12 10 15
					 * 
					 * 
					 */
				}
			} else {// 如果没有扣除记录
				for (int k = 0; k < cmp_score_rule.length; k++) {
					int rulescore = cmp_score_rule[k].getIntegerValue("score");
					if (k != cmp_score_rule.length - 1) {// 如果不是最后一个。。
						if (k == 0 && scorenow_1 < rulescore) {
							break;
						} else if (rulescore <= scorenow_1 && cmp_score_rule[k + 1].getIntegerValue("score") > scorenow_1) {
							BillVO vo = new BillVO();
							vo.setDatas(new Object[7]);
							vo.setKeys(new String[] { "userid", "username", "scorenow", "scorenext", "punish", "lastscore", "date" });
							vo.setObject("userid", new StringItemVO(userid));
							vo.setObject("username", new StringItemVO((String) user.get(userid)));
							vo.setObject("scorenow", new StringItemVO(scorenow));
							vo.setObject("scorenext", new StringItemVO(cmp_score_rule[k].getStringValue("score")));
							vo.setObject("punish", new StringItemVO(cmp_score_rule[k].getStringValue("punish")));
							vo.setObject("lastscore", new StringItemVO("0"));
							vo.setObject("date", new RefItemVO(scoredate,"",scoredate));
							list.add(vo);
							break;
						}
					} else {
						// 直接扣最大的。。
						BillVO vo = new BillVO();
						vo.setDatas(new Object[7]);
						vo.setKeys(new String[] { "userid", "username", "scorenow", "scorenext", "punish", "lastscore", "date" });
						vo.setObject("userid", new StringItemVO(userid));
						vo.setObject("username", new StringItemVO((String) user.get(userid)));
						vo.setObject("scorenow", new StringItemVO(scorenow));
						vo.setObject("scorenext", new StringItemVO(cmp_score_rule[k].getStringValue("score")));
						vo.setObject("punish", new StringItemVO(cmp_score_rule[k].getStringValue("punish")));
						vo.setObject("lastscore", new StringItemVO("0"));
						vo.setObject("date", new RefItemVO(scoredate,"",scoredate));
						list.add(vo);
						break;
					}
				}
			}
		}
		listPanel.addBillVOs((BillVO[]) list.toArray(new BillVO[0]));

	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_showscore) {
			onShowScore();
		} else if (obj == btn_showpunish) {
			onShowPunish();
		} else if (obj == btn_createpunish) {
			onCreatePunish();
		} else if (obj == btn_exportword) {
			onExportWord();
		} else if (obj instanceof BillQueryPanel) {
			try {
				loadData();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}else if(obj == btn_refresh){
			try {
				loadData();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	private void onExportWord() {
		BillVO[] checkItemVOs = listPanel.getSelectedBillVOs();
		if (checkItemVOs == null || checkItemVOs.length == 0) {
			MessageBox.showSelectOne(this);
			return;
		}
		int fileNum = checkItemVOs.length;
		HashMap[] maps = new HashMap[fileNum];
		String[] fileNames = new String[fileNum];
		for (int i = 0; i < fileNum; i++) {
			String user_name = checkItemVOs[i].getStringValue("username");
			String last_score = checkItemVOs[i].getStringValue("lastscore");
			String punish = checkItemVOs[i].getStringValue("punish");
			String scorenext = checkItemVOs[i].getStringValue("scorenext");
			String scorenow = checkItemVOs[i].getStringValue("scorenow");
			Vector v = new Vector();
			try {
				String[][] deptInf = UIUtil.getStringArrayByDS(null, "select b.parentid,b.name  from   pub_user_post a  left join pub_corp_dept b on a.userdept=b.id where a.userid=" + Integer.parseInt(checkItemVOs[i].getStringValue("userid")));
				v.add(deptInf[0][1]);
				while (!deptInf[0][0].trim().equals("")) {
					deptInf = UIUtil.getStringArrayByDS(null, "select parentid,name from pub_corp_dept where id=" + Integer.parseInt(deptInf[0][0]));
					v.add(deptInf[0][1]);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			StringBuffer sb = new StringBuffer();
			for (int j = v.size() - 1; j >= 0; j--) {
				sb.append(v.get(j));
				if (j != 0)
					sb.append("/");
			}
			String depart = sb.toString();
			maps[i] = new HashMap();
			maps[i].put("user_name", user_name);
			maps[i].put("last_score", last_score);
			maps[i].put("punish", punish);
			maps[i].put("scorenext", scorenext);
			maps[i].put("total_score", scorenow);
			maps[i].put("depart", depart);
			fileNames[i] = user_name + "-" + "违规惩罚单" + "-" + scorenext;
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		TemplateToWordUIUtil tem_word_util = new TemplateToWordUIUtil();

		// 获取模板文件时，需要用到编码，编码在模板上传时必须确定！getTemplateName中的第二个参数只需与模板编码对应即可，没可以自定义，不一定用templatecode
		String template_name = TemplateToWordUIUtil.getTemplateName(this, listPanel.getTempletVO().getTempletcode());//
		if (template_name == null) {
			return;
		}
		try {
			tem_word_util.createWordByMap(maps, fileNames, template_name, this);
		} catch (Exception e) {
			if(e.getMessage().contains("java.io.FileNotFoundException")){
				MessageBox.showException(this, new Throwable("请联系管理员在[基础数据-打印模板维护]功能点维护编码为[CMP_SCORE_PUNISH]附件为[违规积分.xml]的数据!"));
			}else{
				MessageBox.showException(this, e);
			}
		}
	}

	public void onShowScore() {
		BillVO vo = listPanel.getSelectedBillVO();
		if (vo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		BillListDialog listDialog = new BillListDialog(this, "积分查询", "CMP_SCORE_LOST_CODE1");
		WLTButton btn_view = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);
		listDialog.getBilllistPanel().addBatchBillListButton(new WLTButton[]{btn_view});
		listDialog.getBilllistPanel().repaintBillListButton();
		listDialog.getBilllistPanel().getQuickQueryPanel().setVisible(false);
		listDialog.getBilllistPanel().QueryDataByCondition("userid = '" + vo.getStringValue("userid") + "'");
		listDialog.getBtn_confirm().setVisible(false);
		listDialog.setVisible(true);
	}

	public void onShowPunish() {
		BillVO vo = listPanel.getSelectedBillVO();
		if (vo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		BillListDialog listDialog = new BillListDialog(this, "惩罚记录", "CMP_SCORE_PUNISH_CODE1");
		listDialog.getBilllistPanel().QueryDataByCondition(" userid = '" + vo.getStringValue("userid") + "'");
		listDialog.getBilllistPanel().getQuickQueryPanel().setVisible(false);
		listDialog.setVisible(true);
	}

	public void onCreatePunish() {
		BillVO vo = listPanel.getSelectedBillVO();
		if (vo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		String s_userid = vo.getStringValue("userid"); // 选中的人员ID
		List insertPunishSql = new ArrayList();
		try {
			HashVO[] userVO = UIUtil.getHashVoArrayByDS(null, "select * from v_pub_user_post_1 where userid = '" + s_userid + "'");
			String punishdate = UIUtil.getServerCurrDate();
			String c_userid = vo.getStringValue("userid");
			String c_score = vo.getStringValue("scorenow"); // 列表中有的下次扣分
															// 一定要小于选中的下次扣分
			if (!s_userid.equals("") && s_userid.equals(c_userid)) {
				if (!"".equals(c_score)) {
					InsertSQLBuilder sql = new InsertSQLBuilder("cmp_score_punish");
					sql.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "s_cmp_score_punish"));
					sql.putFieldValue("userid", s_userid);
					sql.putFieldValue("username", vo.getStringValue("username"));
					if (userVO.length > 0) {
						sql.putFieldValue("deptid", userVO[0].getStringValue("deptid"));
						sql.putFieldValue("deptname", userVO[0].getStringValue("deptname"));
						sql.putFieldValue("postname", userVO[0].getStringValue("postname"));
					}
					sql.putFieldValue("scoretotal", c_score);
					sql.putFieldValue("punish", vo.getStringValue("punish"));
					sql.putFieldValue("createdate", punishdate);
					sql.putFieldValue("creater", loginUserID);
					sql.putFieldValue("LASTDATE", vo.getStringValue("date"));
					insertPunishSql.add(sql.getSQL());
				}
			}
			UIUtil.executeBatchByDS(null, insertPunishSql);
			loadData();
		} catch (Exception ex) {
			MessageBox.showException(this, new Throwable(ex));
			ex.printStackTrace();

		}
	}
}
