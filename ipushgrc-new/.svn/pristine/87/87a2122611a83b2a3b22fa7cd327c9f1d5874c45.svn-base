package com.pushworld.ipushgrc.ui.score.p020;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.ClassFileVO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.to.report.ReportExportWord;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.FrameWorkCommServiceIfc;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;

import com.pushworld.ipushgrc.to.score.ScoreWordTBUtil;
import com.pushworld.ipushgrc.ui.IPushGRCServiceIfc;
import com.pushworld.ipushgrc.ui.score.ScoreUIUtil;

/**
 * 违规积分-》违规积分登记【李春娟/2013-05-10】
 * @author lcj
 *
 */
public class RegisterEditWKPanel extends AbstractWorkPanel implements ActionListener {

	private BillListPanel listPanel;
	private WLTButton btn_add, btn_edit, btn_delete, btn_publish, btn_effect, btn_viewprint, btn_import, btn_templet;
	private Pub_Templet_1_ItemVO[] parantItemVo = null, childrenItemVo = null;
	private String str_userDefinedCls;//自定义违规积分导入类，平台参数配置【李春娟/2015-02-27】

	@Override
	public void initialize() {
		listPanel = new BillListPanel("SCORE_REGISTER_LCJ_E01");
		btn_add = new WLTButton("新增");
		btn_edit = new WLTButton("修改");
		btn_delete = new WLTButton("删除");
		btn_publish = new WLTButton("认定通知", "office_092.gif");
		btn_viewprint = new WLTButton("预览打印", "office_013.gif");
		btn_import = new WLTButton("导入");
		btn_templet = new WLTButton("下载模板");
		btn_add.addActionListener(this);
		btn_edit.addActionListener(this);
		btn_delete.addActionListener(this);
		btn_publish.addActionListener(this);
		btn_viewprint.addActionListener(this);
		btn_import.addActionListener(this);
		btn_templet.addActionListener(this);
		String showeffect = this.getMenuConfMapValueAsStr("是否可直接生效", "N");//【李春娟/2014-11-04】
		boolean showPublish = false;//是否显示【认定通知】按钮

		String role = TBUtil.getTBUtil().getSysOptionStringValue("违规积分可进行认定通知的角色", "");
		if (role == null || role.trim().equals("")) {
			showPublish = true;
		} else {
			String[] rolecodes = ClientEnvironment.getInstance().getLoginUserRoleCodes();
			if (rolecodes == null || rolecodes.length == 0) {
				showPublish = false;
			} else if (role.contains(",")) {//是否有多个角色
				String[] roles = TBUtil.getTBUtil().split(role, ",");
				for (int i = 0; i < roles.length; i++) {
					for (int j = 0; j < rolecodes.length; j++) {
						String rolecode = rolecodes[j];
						if (rolecode != null && !"".equals(rolecode) && rolecode.equalsIgnoreCase(roles[i])) {
							showPublish = true;
							break;
						}
					}
				}
			} else {
				role = role.trim();
				for (int i = 0; i < rolecodes.length; i++) {
					String rolecode = rolecodes[i];
					if (rolecode != null && !"".equals(rolecode) && rolecode.equalsIgnoreCase(role)) {
						showPublish = true;
						break;
					}
				}
			}
		}

		//总行合规经办人

		str_userDefinedCls = TBUtil.getTBUtil().getSysOptionStringValue("自定义违规积分导入类", null);
		if (showeffect != null && ("Y".equalsIgnoreCase(showeffect) || "是".equalsIgnoreCase(showeffect))) {
			btn_effect = new WLTButton("直接生效");
			btn_effect.setToolTipText("不下发认定通知，使积分直接生效");
			btn_effect.addActionListener(this);
			if (str_userDefinedCls != null && !str_userDefinedCls.trim().equals("")) {// 如果配置了自定义违规积分导入类，则加入【导入】和【下载模板】
				if (showPublish) {
					listPanel.addBatchBillListButton(new WLTButton[] { btn_add, btn_edit, btn_delete, btn_publish, btn_effect, btn_import, btn_templet });
				} else {
					listPanel.addBatchBillListButton(new WLTButton[] { btn_add, btn_edit, btn_delete, btn_effect, btn_import, btn_templet });
				}

			} else {
				if (showPublish) {
					listPanel.addBatchBillListButton(new WLTButton[] { btn_add, btn_edit, btn_delete, btn_publish, btn_effect });
				} else {
					listPanel.addBatchBillListButton(new WLTButton[] { btn_add, btn_edit, btn_delete, btn_effect });
				}
			}

		} else if (str_userDefinedCls != null && !str_userDefinedCls.trim().equals("")) {// 如果配置了自定义违规积分导入类，则加入【导入】和【下载模板】
			if (showPublish) {
				listPanel.addBatchBillListButton(new WLTButton[] { btn_add, btn_edit, btn_delete, btn_publish, btn_viewprint, btn_import, btn_templet });
			} else {
				listPanel.addBatchBillListButton(new WLTButton[] { btn_add, btn_edit, btn_delete, btn_viewprint, btn_import, btn_templet });
			}
		} else {
			if (showPublish) {
				listPanel.addBatchBillListButton(new WLTButton[] { btn_add, btn_edit, btn_delete, btn_publish, btn_viewprint });
			} else {
				listPanel.addBatchBillListButton(new WLTButton[] { btn_add, btn_edit, btn_delete, btn_viewprint });
			}
		}

		listPanel.repaintBillListButton();
		String curryear = TBUtil.getTBUtil().getCurrDate().substring(0, 4);
		listPanel.QueryDataByCondition("FINDDATE like '" + curryear + "%'");//查询本年度的记录

		try {
			Pub_Templet_1VO templet_1VO = UIUtil.getPub_Templet_1VO("SCORE_USER_LCJ_E01");//子模板
			childrenItemVo = templet_1VO.getItemVos();
		} catch (Exception e) {
			e.printStackTrace();
		}
		parantItemVo = listPanel.getTempletVO().getItemVos();

		this.setLayout(new BorderLayout());
		this.add(listPanel);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_add) {
			onAdd();
		} else if (e.getSource() == btn_edit) {
			onEdit();
		} else if (e.getSource() == btn_delete) {
			onDelete();
		} else if (e.getSource() == btn_publish) {
			try {
				if ("WORD".equalsIgnoreCase(viewtype)) {
					onPublish();
				} else {
					onPublish2();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		} else if (e.getSource() == btn_effect) {
			onEffect();
		} else if (e.getSource() == btn_viewprint) {
			onViewAndPrint();
		} else if (e.getSource() == btn_import) {
			onImport();
		} else if (e.getSource() == btn_templet) {
			onDownLoadTemplet();
		}
	}

	private String viewtype = TBUtil.getTBUtil().getSysOptionStringValue("违规积分通知单查看方式", "WORD");

	private void onViewAndPrint() {
		final BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		try {
			String count = UIUtil.getStringValueByDS(null, "select count(id) from SCORE_USER where registerid=" + billVO.getStringValue("id") + " and state!='已生效'");
			if (Integer.parseInt(count) > 0) {
				if (!MessageBox.confirm(this, "此记录中有" + count + "人没有认定生效，是否继续打印预览?")) {
					return;
				}
			}
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!"WORD".equals(viewtype)) {
			onViewAndPrintByCellPanel();
			return;
		}

		//这里点击认定通知按钮就直接打开预览界面，预览界面下发可以点击认定下发按钮【李春娟/2013-06-04】
		new SplashWindow(listPanel, "正在处理中,请稍等...", new AbstractAction() {
			public void actionPerformed(ActionEvent actionevent) {
				new ScoreRegisterTool().viewLastFileAsWord(listPanel, "认定通知书", billVO, parantItemVo, childrenItemVo);
			}
		}, false);
	}

	private ScoreWordTBUtil wordutil = new ScoreWordTBUtil();//word合并及替换工具

	private void onViewAndPrintByCellPanel() {
		String str_ClientCodeCache = System.getProperty("ClientCodeCache");// 得到客户端缓存位置。
		if (str_ClientCodeCache.indexOf("\\") >= 0) {// 变换客户端的\\为/  
			str_ClientCodeCache = UIUtil.replaceAll(str_ClientCodeCache, "\\", "/"); //
		}
		if (str_ClientCodeCache.endsWith("/")) {// 如果客户端路径最后一位为/则去掉
			str_ClientCodeCache = str_ClientCodeCache.substring(0, str_ClientCodeCache.length() - 1);
		}
		String tmpfilepath = str_ClientCodeCache + "/score";//创建临时目录 C:\Documents and Settings\Administrator\WEBLIGHT_CODECACHE\score
		File tmpfile = new File(tmpfilepath);
		if (!tmpfile.exists()) {//如果客户端没有该文件夹，则创建之
			tmpfile.mkdirs();
		}

		final BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		BillCellVO cellvo = null;
		try {
			cellvo = UIUtil.getMetaDataService().getBillCellVO("违规积分认定通知书模板", null, null);
		} catch (Exception ex) {
			WLTLogger.getLogger(RegisterEditWKPanel.class).error("没有找到[违规积分认定通知书模板]模版.");
		}
		try {
			if (cellvo == null) {
				BillListPanel listpanel = new BillListPanel(new cn.com.infostrategy.ui.report.cellcompent.CellTMO());
				listpanel.QueryDataByCondition(" templetcode like '%认定%'");
				BillListDialog listdialog = new BillListDialog(this, "请选择想对应的模版", listpanel);
				listdialog.setVisible(true);
				if (listdialog.getCloseType() != 1) {
					return;
				}
				BillVO rtvos[] = listdialog.getReturnBillVOs();
				if (rtvos.length > 0) {
					cellvo = UIUtil.getMetaDataService().getBillCellVO(rtvos[0].getStringValue("templetcode"), null, null);
				} else {
					return;
				}
			}
			TBUtil tbUtil = new TBUtil();
			BillVO vo = listPanel.getSelectedBillVO();

			HashVO hvo = new HashVO();
			String keys[] = vo.getKeys();
			String names[] = vo.getNames();
			String currdate = UIUtil.getServerCurrDate();
			hvo.setAttributeValue("认定日期", currdate);
			for (int i = 0; i < keys.length; i++) {
				hvo.setAttributeValue(keys[i], vo.getStringViewValue(keys[i]));
				if ("PUBLISHDATE".equals(keys[i])) {
					String date = vo.getStringViewValue(keys[i]);
					if (!tbUtil.isEmpty(date)) {
						hvo.setAttributeValue(names[i].trim(), vo.getStringViewValue(keys[i]));
					}
				} else {
					hvo.setAttributeValue(names[i].trim(), vo.getStringViewValue(keys[i]));
				}
			}
			HashVO scoreuser[] = UIUtil.getHashVoArrayByDS(null, "select score_user.*,pub_user.name as username from SCORE_USER left join pub_user on score_user.userid = pub_user.id where registerid=" + vo.getPkValue());
			Pub_Templet_1VO templet_1VO = UIUtil.getPub_Templet_1VO("SCORE_USER_LCJ_E01");//子模板
			Pub_Templet_1_ItemVO itemvos[] = childrenItemVo = templet_1VO.getItemVos();
			WLTTabbedPane tabpane = new WLTTabbedPane();
			List filelist = new ArrayList();
			ReportExportWord word = new ReportExportWord();
			List cellvos = new ArrayList<BillCellVO>();
			for (int k = 0; k < scoreuser.length; k++) {
				for (int i = 0; i < itemvos.length; i++) {
					if (!itemvos[i].getItemkey().equalsIgnoreCase("id") && !itemvos[i].getItemkey().equalsIgnoreCase("username")) {
						hvo.setAttributeValue(itemvos[i].getItemkey(), scoreuser[k].getStringValue(itemvos[i].getItemkey()));
						hvo.setAttributeValue(itemvos[i].getItemname(), scoreuser[k].getStringValue(itemvos[i].getItemkey()));
					} else if (itemvos[i].getItemkey().equalsIgnoreCase("username")) {

					}
				}
				hvo.setAttributeValue("违规人", scoreuser[k].getStringValue(("username")));
				hvo.setAttributeValue("username", scoreuser[k].getStringValue(("username")));
				BillCellVO cellvo_new = UIUtil.getCommonService().parseCellTempetToWord(cellvo, hvo);
				cellvos.add(cellvo_new);
			}
			String filename = System.currentTimeMillis() + "";
			word.exportWordFile((BillCellVO[]) cellvos.toArray(new BillCellVO[0]), tmpfilepath + "/", filename);
			new ScoreRegisterTool().print(this, tmpfilepath + "/" + filename + ".doc");
			deleteTmpFiles(new String[] { tmpfilepath + "/" + filename + ".doc" });
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void deleteTmpFiles(String[] _reffilepath) {
		for (int j = 0; j < _reffilepath.length; j++) {
			File file = new File(_reffilepath[j]);
			file.deleteOnExit();//java虚拟机退出时，删除客户端的临时文件
		}
	}

	/**
	 * 新增的按钮逻辑【李春娟/2014-11-04】
	 * 增加了默认的复议截止日期（即生效日期），可手动设置
	 */
	private void onAdd() {
		HashMap defaultValueMap = new HashMap(); //
		String defaultdate = new ScoreUIUtil().getEffectDate();
		defaultValueMap.put("EFFECTDATE", new RefItemVO(defaultdate, "", defaultdate));
		listPanel.doInsert(defaultValueMap); //
	}

	/**
	 * 修改通知的按钮逻辑【李春娟/2014-11-04】
	 */
	private void onEdit() {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if ("已认定".equals(billVO.getStringValue("state"))) {
			MessageBox.show(this, "该记录已认定,不能编辑!");
			return;
		}
		listPanel.doEdit();
	}

	/**
	 * 删除的按钮逻辑【李春娟/2014-11-04】
	 */
	private void onDelete() {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		} else if (!"未认定".equals(billVO.getStringValue("state"))) {
			if ("admin".equalsIgnoreCase(ClientEnvironment.getCurrLoginUserVO().getCode())) {
				if (!MessageBox.confirm(this, "该记录已" + billVO.getStringValue("state") + "，是否强制删除?")) {
					return;
				}
			} else {
				MessageBox.show(this, "该记录已" + billVO.getStringValue("state") + ",不能删除!");
				return;
			}

		} else if (!MessageBox.confirmDel(this)) {
			return;
		}
		String id = billVO.getStringValue("id");
		ArrayList sqlList = new ArrayList();
		sqlList.add("delete from SCORE_REGISTER where id = " + id);
		sqlList.add("delete from SCORE_USER where REGISTERID = " + id);//需要同时删除子表【李春娟/2015-02-06】
		listPanel.removeSelectedRow();
		try {
			UIUtil.executeBatchByDS(null, sqlList);
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	//复议2
	private void onPublish2() throws Exception {
		final BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if ("已认定".equals(billVO.getStringValue("state"))) {
			MessageBox.show(this, "该记录已认定,不能重复认定!");
			return;
		}

		BillCellVO cellvo = null;
		try {
			cellvo = UIUtil.getMetaDataService().getBillCellVO("违规积分认定通知书模板", null, null);
		} catch (Exception ex) {
			WLTLogger.getLogger(RegisterEditWKPanel.class).error("没有找到[违规积分认定通知书模板]模版.");
		}
		if (cellvo == null) {
			BillListPanel listpanel = new BillListPanel(new cn.com.infostrategy.ui.report.cellcompent.CellTMO());
			listpanel.QueryDataByCondition(" templetcode like '%认定%'");
			BillListDialog listdialog = new BillListDialog(this, "请选择想对应的模版", listpanel);
			listdialog.setVisible(true);
			if (listdialog.getCloseType() != 1) {
				return;
			}
			BillVO rtvos[] = listdialog.getReturnBillVOs();
			if (rtvos.length > 0) {
				cellvo = UIUtil.getMetaDataService().getBillCellVO(rtvos[0].getStringValue("templetcode"), null, null);
			} else {
				return;
			}
		}
		TBUtil tbUtil = new TBUtil();
		int daycount = tbUtil.getSysOptionIntegerValue("违规积分自动生效时间", 5);
		String effectdate = billVO.getStringValue("EFFECTDATE", "");//【李春娟/2014-11-04】
		if (effectdate == null || "".equals(effectdate)) {//兼容以前的逻辑，如果积分登记主表没有新增的字段EFFECTDATE，则按以前的逻辑
			daycount = 5;
			effectdate = new ScoreUIUtil().getEffectDate();
		} else {//如果新增的复议截止日期设置的值，则计算到期还有几天
			daycount = new ScoreUIUtil().getDateDifference(effectdate, tbUtil.getCurrDate());//计算时间间隔【李春娟/2014-11-04】
		}
		final BillDialog dialog = new BillDialog(this, "预览", 800, 800);
		BillVO vo = listPanel.getSelectedBillVO();

		HashVO hvo = new HashVO();
		String keys[] = vo.getKeys();
		String names[] = vo.getNames();
		String currdate = UIUtil.getServerCurrDate();
		hvo.setAttributeValue("认定日期", currdate);
		for (int i = 0; i < keys.length; i++) {
			hvo.setAttributeValue(keys[i], vo.getStringViewValue(keys[i]));
			if ("PUBLISHDATE".equals(keys[i])) {
				String date = vo.getStringViewValue(keys[i]);
				if (!tbUtil.isEmpty(date)) {
					hvo.setAttributeValue(names[i].trim(), vo.getStringViewValue(keys[i]));
				}
			} else {
				hvo.setAttributeValue(names[i].trim(), vo.getStringViewValue(keys[i]));
			}
		}
		HashVO scoreuser[] = UIUtil.getHashVoArrayByDS(null, "select score_user.*,pub_user.name as username from SCORE_USER left join pub_user on score_user.userid = pub_user.id where registerid=" + vo.getPkValue());
		Pub_Templet_1VO templet_1VO = UIUtil.getPub_Templet_1VO("SCORE_USER_LCJ_E01");//子模板
		Pub_Templet_1_ItemVO itemvos[] = childrenItemVo = templet_1VO.getItemVos();
		WLTTabbedPane tabpane = new WLTTabbedPane();
		LinkedHashMap hashmap = new LinkedHashMap();
		HashMap scoreuserNum = new HashMap(); //违规积分条数
		for (int k = 0; k < scoreuser.length; k++) {
			for (int i = 0; i < itemvos.length; i++) {
				if (!itemvos[i].getItemkey().equalsIgnoreCase("id") && !itemvos[i].getItemkey().equalsIgnoreCase("username")) {
					hvo.setAttributeValue(itemvos[i].getItemkey(), scoreuser[k].getStringValue(itemvos[i].getItemkey()));
					hvo.setAttributeValue(itemvos[i].getItemname(), scoreuser[k].getStringValue(itemvos[i].getItemkey()));
				} else if (itemvos[i].getItemkey().equalsIgnoreCase("username")) {

				}
			}
			hvo.setAttributeValue("违规人", scoreuser[k].getStringValue(("username")));
			hvo.setAttributeValue("username", scoreuser[k].getStringValue(("username")));
			BillCellVO cellvo_new = UIUtil.getCommonService().parseCellTempetToWord(cellvo, hvo);
			BillCellPanel cellPanel = new BillCellPanel(cellvo_new, true, false);
			tabpane.addTab(scoreuser[k].getStringValue("username"), cellPanel);
			hashmap.put(scoreuser[k].getStringValue("id"), cellvo_new);
			if (scoreuserNum.containsKey(scoreuser[k].getStringValue("userid"))) {
				Integer num = Integer.parseInt(String.valueOf(scoreuserNum.get(scoreuser[k].getStringValue("userid"))));
				num++;
				scoreuserNum.put(scoreuser[k].getStringValue("userid"), num + "");
			} else {
				scoreuserNum.put(scoreuser[k].getStringValue("userid"), "1");
			}
		}
		dialog.add(tabpane, BorderLayout.CENTER);
		JPanel southpanel = new JPanel(new FlowLayout());//dialog下方的按钮面板
		WLTButton btn_confirm = new WLTButton("认定下发", "office_092.gif"); //
		btn_confirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (MessageBox.confirm(dialog, "认定下发后将不可编辑或删除,是否继续?")) {
					dialog.setCloseType(BillDialog.CONFIRM);
				} else {
					dialog.setCloseType(BillDialog.CANCEL);
				}
				dialog.dispose(); //
			}
		});

		WLTButton btn_close = new WLTButton("关闭"); //
		btn_close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.setCloseType(BillDialog.CANCEL);
				dialog.dispose(); //
			}
		});
		southpanel.add(btn_confirm);
		southpanel.add(btn_close);
		dialog.add(southpanel, BorderLayout.SOUTH);
		dialog.setVisible(true);
		if (dialog.getCloseType() == 1) { //确定下发
			List sqllist = new ArrayList();
			UpdateSQLBuilder sqlBuilder = new UpdateSQLBuilder(listPanel.getTempletVO().getSavedtablename(), "id=" + billVO.getStringValue("id"));
			sqlBuilder.putFieldValue("state", "已认定");//设置认定状态
			sqlBuilder.putFieldValue("PUBLISHDATE", TBUtil.getTBUtil().getCurrDate());//设置认定日期
			sqllist.add(sqlBuilder.getSQL());
			//修改子表生效日期
			UpdateSQLBuilder childSqlBuilder = new UpdateSQLBuilder("score_user", "REGISTERID=" + billVO.getStringValue("id"));
			//下面生效日期即复议截止日期，在主表可修改，故这里不自动生成了new ScoreUIUtil().getEffectDate()
			childSqlBuilder.putFieldValue("EFFECTDATE", effectdate);//设置生效日期，但这里还未生效，state的状态仍为未复议【李春娟/2013-06-03】
			sqllist.add(childSqlBuilder.getSQL());
			UIUtil.executeBatchByDS(null, sqllist);

			List userids_hm = new ArrayList();
			for (int i = 0; i < scoreuser.length; i++) {
				userids_hm.add(scoreuser[i].getStringValue("userid"));
			}
			HashVO[] smsUser = UIUtil.getHashVoArrayByDS(null, "select * from pub_user where id in(" + tbUtil.getInCondition(userids_hm) + ")");
			IPushGRCServiceIfc ifc = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
			String msg = tbUtil.getSysOptionStringValue("违规积分短信通知内容", "您好，您有{数量}违规积分请及时登录系统进行处理【" + UIUtil.getProjectName() + "】");
			List tell_msg = new ArrayList();
			for (int i = 0; i < smsUser.length; i++) {
				String telno = smsUser[i].getStringValue("MOBILE");
				if (!TBUtil.isEmpty(telno) && telno.length() >= 11) {
					tell_msg.add(new String[] { telno, TBUtil.getTBUtil().replaceAll(msg, "{数量}", "" + scoreuserNum.get(smsUser[i].getStringValue("id")) + "条"), "违规积分" });
				}
			}
			try {
				ifc.sendSMS(tell_msg);
			} catch (Exception ex) {
				MessageBox.showException(this, ex);
			}
			listPanel.refreshCurrSelectedRow();
		}
	}

	/**
	 * 认定通知的按钮逻辑【李春娟/2014-11-04】
	 */
	private void onPublish() {
		final BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if ("已认定".equals(billVO.getStringValue("state"))) {
			MessageBox.show(this, "该记录已认定,不能重复认定!");
			return;
		}
		//这里点击认定通知按钮就直接打开预览界面，预览界面下发可以点击认定下发按钮【李春娟/2013-06-04】
		new SplashWindow(listPanel, "正在处理中,请稍等...", new AbstractAction() {
			public void actionPerformed(ActionEvent actionevent) {
				new ScoreRegisterTool().openOneFileAsWord(listPanel, "认定通知书", billVO, parantItemVo, childrenItemVo);
			}
		}, false);
	}

	/**
	 * 直接生效的按钮逻辑【李春娟/2014-11-04】
	 */
	private void onEffect() {

		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		String state = billVO.getStringValue("state");
		if (!"未认定".equals(state)) {
			MessageBox.show(this, "该记录" + state + ",不能直接生效!");
			return;
		}
		try {
			IPushGRCServiceIfc server = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
			server.effectScoreByRegisterId(billVO.getStringValue("id"));
			listPanel.refreshCurrSelectedRow();
		} catch (WLTRemoteException e) {
			e.printStackTrace();
			MessageBox.showException(this, e);
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showException(this, e);
		}
	}

	/**
	 *导入违规积分记录【李春娟/2015-02-05】
	 */
	private void onImport() {
		HashMap hashmap = new HashMap();
		hashmap.put("mainpanel", this);// 将本类句柄传入
		hashmap.put("listpanel", listPanel);// 将列表句柄传入
		TBUtil.getTBUtil().reflectCallCommMethod(str_userDefinedCls + ".importScore()", hashmap);//配置的类必须有importScore(HashMap _map)方法，并且返回HashMap
	}

	/**
	 * 下载导入模板【李春娟/2015-02-06】
	 */
	private void onDownLoadTemplet() {
		try {
			FrameWorkCommServiceIfc service;
			service = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class);
			String cmpfilename = "score_register.xls";
			String filepath = service.getServerFile(cmpfilename);
			ClassFileVO filevo = UIUtil.downloadToClientByAbsolutePath(filepath);

			JFileChooser chooser = new JFileChooser();
			chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
				public boolean accept(File file) {
					if (file.isDirectory()) {
						return true;
					} else {
						String filename = file.getName();
						return filename.endsWith(".xls");
					}
				}

				public String getDescription() {
					return "*.xls";
				}
			});

			File file = new File(new File("C:\\" + cmpfilename).getCanonicalPath());
			chooser.setSelectedFile(file);
			int li_rewult = chooser.showSaveDialog(this);
			if (li_rewult == JFileChooser.APPROVE_OPTION) {
				File curFile = chooser.getSelectedFile(); //
				if (curFile != null) {
					curFile.createNewFile();
					FileOutputStream out = new FileOutputStream(curFile, false);
					out.write(filevo.getByteCodes());
					out.close();
					MessageBox.show(listPanel, curFile.getName() + "导出成功！");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
