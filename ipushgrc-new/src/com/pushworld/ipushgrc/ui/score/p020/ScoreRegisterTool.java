package com.pushworld.ipushgrc.ui.score.p020;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.ClassFileVO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.OfficeCompentControlVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillOfficeDialog;

import com.pushworld.ipushgrc.to.score.ScoreWordTBUtil;
import com.pushworld.ipushgrc.ui.IPushGRCServiceIfc;
import com.pushworld.ipushgrc.ui.score.ScoreUIUtil;

/**
 * 违规积分认定处理
 * @author 张营闯[2013-05-16]
 * */
public class ScoreRegisterTool {
	private TBUtil tbUtil = new TBUtil();
	private ScoreWordTBUtil wordutil = new ScoreWordTBUtil();//word合并及替换工具

	public void openOneFileAsWord(BillListPanel _listPanel, String type, BillVO billVO, Pub_Templet_1_ItemVO[] parantItemVo, Pub_Templet_1_ItemVO[] childrenItemVo) {
		String cmpfilename = "";//文件名
		HashVO userVO[] = null;//违规人员相关信息的VO
		String userids[] = null;//违规人员id
		HashVO hashVO[] = null;
		String userid = billVO.getStringValue("userids");
		userids = tbUtil.split(userid, ";");
		try {
			userVO = UIUtil.getHashVoArrayByDS(null, "select * from SCORE_USER where id in (" + tbUtil.getInCondition(userids) + ")");//取得违规人员的相关信息
			hashVO = UIUtil.getHashVoArrayByDS(null, "select ATTACHFILE from SCORE_TEMPLET where TEMPLETTYPE ='" + type + "'");//取得认定通知单模板
			if (hashVO != null && hashVO.length > 0) {
				String paths = hashVO[0].getStringValue("ATTACHFILE");
				if (paths == null) {
					MessageBox.show(_listPanel, "请在【通知书模板定义】菜单中上传模板文件!");//这里需要考虑模板文件没有上传的情况【李春娟/2013-09-18】
					return;
				} else if (paths.contains(";")) {
					cmpfilename = paths.substring(0, paths.indexOf(";"));//当有多个模板时，默认取第一个模板
				} else {
					cmpfilename = paths;
				}
			} else {
				MessageBox.show(_listPanel, "请在【通知书模板定义】菜单中上传模板文件!");
				return;
			}
		} catch (Exception e2) {
			MessageBox.showException(_listPanel, e2);
			return;
		}
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
		String reffilepath[] = new String[userVO.length];//为了记录相关人员的认定通知单的文件名
		for (int i = 0; i < userVO.length; i++) {
			//第二次远程调用，下载流程文件正文到客户端，进行文档相关内容的替换。
			if (i == 0) {//第一次，从服务器上下载模板。后面的模板根据这个模板复制
				try {
					reffilepath[i] = UIUtil.downLoadFile(System.getProperty("WLTUPLOADFILEDIR") + "/upload", cmpfilename.substring(1, cmpfilename.length()), true, tmpfilepath, "N" + UIUtil.getSequenceNextValByDS(null, "S_PUB_FILEUPLOAD") + "_" + userVO[i].getStringValue("id") + "_" + userVO[i].getStringValue("userid") + ".doc", true);
				} catch (Exception e) {
					MessageBox.show(_listPanel, "无法找到该文件对应的模板,不能预览!");
					e.printStackTrace();
					return;
				}
			} else {
				try {
					reffilepath[i] = copyFile(reffilepath[i - 1], userVO[i].getStringValue("userid"), userVO[i]);//拷贝模板
				} catch (Exception e) {
					MessageBox.showException(_listPanel, e);
					return;
				}
			}
		}
		int daycount = tbUtil.getSysOptionIntegerValue("违规积分自动生效时间", 5);
		String effectdate = billVO.getStringValue("EFFECTDATE", "");//【李春娟/2014-11-04】
		if (effectdate == null || "".equals(effectdate)) {//兼容以前的逻辑，如果积分登记主表没有新增的字段EFFECTDATE，则按以前的逻辑
			daycount = 5;
			effectdate = new ScoreUIUtil().getEffectDate();
		} else {//如果新增的复议截止日期设置的值，则计算到期还有几天
			daycount = new ScoreUIUtil().getDateDifference(effectdate, tbUtil.getCurrDate());//计算时间间隔【李春娟/2014-11-04】
		}
		List fileList = new ArrayList();
		HashMap scoreuserNum = new HashMap(); //违规积分条数
		for (int i = 0; i < userVO.length; i++) {
			String username = null;
			try {
				username = UIUtil.getStringValueByDS(null, "select name from pub_user where id = '" + userVO[i].getStringValue("userid") + "'");//取得违规人员姓名
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			HashMap textmap = new HashMap();//要替换的文本map,如key="$发布日期$",value="2011-05-24"
			textmap.put("$自动生效时间$", daycount + "");//【李春娟/2013-06-05】
			for (int j = 0; j < parantItemVo.length; j++) {
				if (!parantItemVo[j].getItemname().equals("违规人")) {
					if (parantItemVo[j].getItemname().equals("认定日期")) {//在认定时该字段才被赋值,此时预览生成word通知书的时候该字段还为空，所以此处取系统当前时间
						textmap.put("$" + parantItemVo[j].getItemname() + "$", tbUtil.getCurrDate());
					} else {
						textmap.put("$" + parantItemVo[j].getItemname() + "$", billVO.getStringViewValue(parantItemVo[j].getItemkey()));
					}
				}
			}
			for (int j = 0; j < childrenItemVo.length; j++) {
				if (!childrenItemVo[j].getItemname().equals("违规人")) {
					textmap.put("$" + childrenItemVo[j].getItemname() + "$", userVO[i].getStringValue(childrenItemVo[j].getItemkey(), ""));
				} else {
					textmap.put("$" + childrenItemVo[j].getItemname() + "$", username);
				}
			}
			wordutil.replaceScoreFile(reffilepath[i], textmap);//替换文本
			fileList.add(reffilepath[i]);//将文件名存储到list中，方便后面合并使用
			if (scoreuserNum.containsKey(userVO[i].getStringValue("userid"))) {
				Integer num = Integer.parseInt(String.valueOf(scoreuserNum.get(userVO[i].getStringValue("userid"))));
				num++;
				scoreuserNum.put(userVO[i].getStringValue("userid"), num + "");
			} else {
				scoreuserNum.put(userVO[i].getStringValue("userid"), "1");
			}
		}
		try {
			String firstfile = copyFile(fileList.get(0).toString(), userVO[0].getStringValue("userid"), userVO[0]);//拷贝第一个通知单，后面合并是在第一个通知单的基础上合并的，所以提前多拷贝出来个
			String filename = wordutil.mergeScoreWord(fileList);//合并文档
			if (filename != null) {
				uploadofficefileTOBS(filename);//上传到服务器端office控件默认的存放地址
				OfficeCompentControlVO officeVO = new OfficeCompentControlVO(false, false, false, null); //这里需要这样写，以前用了其他项目包中类CommonHtmlOfficeConfig【李春娟/2013-05-22】
				officeVO.setIfshowsave(false);
				officeVO.setIfshowprint_all(false);
				officeVO.setIfshowprint_fen(false);
				officeVO.setIfshowprint_tao(false);
				officeVO.setIfshowedit(false);
				officeVO.setToolbar(false);
				officeVO.setIfshowclose(false);
				officeVO.setPrintable(false);
				officeVO.setMenubar(false);
				officeVO.setMenutoolbar(false);
				officeVO.setIfshowhidecomment(false);
				officeVO.setTitlebar(false);
				officeVO.setIfshowprint(false);
				officeVO.setIfshowhidecomment(false);
				officeVO.setIfshowshowcomment(false);
				officeVO.setIfshowacceptedit(false);
				officeVO.setIfshowshowedit(false);
				officeVO.setIfshowhideedit(false);
				officeVO.setIfshowwater(false);
				officeVO.setIfShowResult(false); //不显示结果区域显示。
				officeVO.setIfselfdesc(true); //关键
				officeVO.setSubdir("upload");//必须设置一下
				final BillOfficeDialog officeDialog = new BillOfficeDialog(_listPanel, "/score/tempfile/" + filename.substring(filename.lastIndexOf("/") + 1, filename.length()), officeVO);//千航插件窗口

				JPanel southpanel = new JPanel(new FlowLayout());//dialog下方的按钮面板
				WLTButton btn_confirm = new WLTButton("认定下发", "office_092.gif"); //
				btn_confirm.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (MessageBox.confirm(officeDialog, "认定下发后将不可编辑或删除,是否继续?")) {
							officeDialog.setCloseType(BillDialog.CONFIRM);
						} else {
							officeDialog.setCloseType(BillDialog.CANCEL);
						}
						officeDialog.callWebBrowseJavaScriptFunction("closedoc"); //
						officeDialog.dispose(); //
					}
				});

				WLTButton btn_close = new WLTButton("关闭"); //
				btn_close.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						officeDialog.setCloseType(BillDialog.CANCEL);
						officeDialog.callWebBrowseJavaScriptFunction("closedoc"); //
						officeDialog.dispose(); //
					}
				});

				southpanel.add(btn_confirm);
				southpanel.add(btn_close);

				officeDialog.getContentPane().add(southpanel, BorderLayout.SOUTH);
				if (SplashWindow.window != null) {
					SplashWindow.window.dispose();//打开前先将SplashWindow关闭，否则一直在后面存在【李春娟/2013-06-04】
				}
				officeDialog.setVisible(true);
				if (officeDialog.getCloseType() == BillDialog.CONFIRM) {
					List sqllist = new ArrayList();
					//修改主表状态和认定日期
					UpdateSQLBuilder sqlBuilder = new UpdateSQLBuilder(_listPanel.getTempletVO().getSavedtablename(), "id=" + billVO.getStringValue("id"));
					sqlBuilder.putFieldValue("state", "已认定");//设置认定状态
					sqlBuilder.putFieldValue("PUBLISHDATE", tbUtil.getCurrDate());//设置认定日期
					sqllist.add(sqlBuilder.getSQL());
					//修改子表生效日期
					UpdateSQLBuilder childSqlBuilder = new UpdateSQLBuilder("score_user", "REGISTERID=" + billVO.getStringValue("id"));
					//下面生效日期即复议截止日期，在主表可修改，故这里不自动生成了new ScoreUIUtil().getEffectDate()
					childSqlBuilder.putFieldValue("EFFECTDATE", effectdate);//设置生效日期，但这里还未生效，state的状态仍为未复议【李春娟/2013-06-03】
					sqllist.add(childSqlBuilder.getSQL());
					String sql = null;
					for (int i = 1; i < fileList.size(); i++) {//合并后的文件已经传到office默认的文件夹下了，这里就不传了
						sql = uploadfileTOBS(_listPanel, fileList.get(i).toString());//上传文件到服务器
						if (sql == null) {//如果只要有一个报错，则需要退出【李春娟/2013-05-22】
							MessageBox.show(_listPanel, "认定失败!");
							deleteTmpFiles(reffilepath);
							return;
						}
						sqllist.add(sql);
					}
					sql = uploadfileTOBS(_listPanel, firstfile);//将第一个文件也上传到服务器
					sqllist.add(sql);
					UIUtil.executeBatchByDS(null, sqllist);
					MessageBox.show(_listPanel, "认定成功!");
					//
					List userids_hm = new ArrayList();
					for (int i = 0; i < userVO.length; i++) {
						userids_hm.add(userVO[i].getStringValue("userid"));
					}
					HashVO[] smsUser = UIUtil.getHashVoArrayByDS(null, "select * from pub_user where id in(" + tbUtil.getInCondition(userids_hm) + ")");
					IPushGRCServiceIfc ifc = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
					List tell_msg = new ArrayList();
					String msg = tbUtil.getSysOptionStringValue("违规积分短信通知内容", "您好，您有{数量}条违规积分请及时登录系统进行处理【" + UIUtil.getProjectName() + "】");
					for (int i = 0; i < smsUser.length; i++) {
						String telno = smsUser[i].getStringValue("MOBILE");
						if (!TBUtil.isEmpty(telno) && telno.length() >= 11) {
							String newmsg = msg;
							newmsg = TBUtil.getTBUtil().replaceAll(newmsg, "{数量}", "" + scoreuserNum.get(smsUser[i].getStringValue("id")) + "条");
							tell_msg.add(new String[] { telno, newmsg, "违规积分" });
						}
					}
					try {
						ifc.sendSMS(tell_msg);
					} catch (Exception ex) {
						MessageBox.showException(_listPanel, ex);
					}
					_listPanel.refreshCurrSelectedRow();
				}
				deleteTmpFiles(reffilepath);
			}
		} catch (Exception e) {
			e.printStackTrace();//客户端继续输出，以便以后查看报错日志【李春娟/2013-06-03】
			MessageBox.showException(_listPanel, e);
		}
	}

	//最终认定后的文件整体预览，可以打印
	public void viewLastFileAsWord(BillListPanel _listPanel, String type, BillVO billVO, Pub_Templet_1_ItemVO[] parantItemVo, Pub_Templet_1_ItemVO[] childrenItemVo) {
		String cmpfilename = "";//文件名
		HashVO userVO[] = null;//违规人员相关信息的VO
		String userids[] = null;//违规人员id
		HashVO hashVO[] = null;
		String userid = billVO.getStringValue("userids");
		userids = tbUtil.split(userid, ";");
		try {
			userVO = UIUtil.getHashVoArrayByDS(null, "select * from SCORE_USER where id in (" + tbUtil.getInCondition(userids) + ")");//取得违规人员的相关信息
			hashVO = UIUtil.getHashVoArrayByDS(null, "select ATTACHFILE from SCORE_TEMPLET where TEMPLETTYPE ='" + type + "'");//取得认定通知单模板
			if (hashVO != null && hashVO.length > 0) {
				String paths = hashVO[0].getStringValue("ATTACHFILE");
				if (paths == null) {
					MessageBox.show(_listPanel, "请在【通知书模板定义】菜单中上传模板文件!");//这里需要考虑模板文件没有上传的情况【李春娟/2013-09-18】
					return;
				} else if (paths.contains(";")) {
					cmpfilename = paths.substring(0, paths.indexOf(";"));//当有多个模板时，默认取第一个模板
				} else {
					cmpfilename = paths;
				}
			} else {
				MessageBox.show(_listPanel, "请在【通知书模板定义】菜单中上传模板文件!");
				return;
			}
		} catch (Exception e2) {
			MessageBox.showException(_listPanel, e2);
			return;
		}
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
		String reffilepath[] = new String[userVO.length];//为了记录相关人员的认定通知单的文件名
		for (int i = 0; i < userVO.length; i++) {
			//第二次远程调用，下载流程文件正文到客户端，进行文档相关内容的替换。
			if (i == 0) {//第一次，从服务器上下载模板。后面的模板根据这个模板复制
				try {
					reffilepath[i] = UIUtil.downLoadFile(System.getProperty("WLTUPLOADFILEDIR") + "/upload", cmpfilename.substring(1, cmpfilename.length()), true, tmpfilepath, "N" + UIUtil.getSequenceNextValByDS(null, "S_PUB_FILEUPLOAD") + "_" + userVO[i].getStringValue("id") + "_" + userVO[i].getStringValue("userid") + ".doc", true);
				} catch (Exception e) {
					MessageBox.show(_listPanel, "无法找到该文件对应的模板,不能预览!");
					e.printStackTrace();
					return;
				}
			} else {
				try {
					reffilepath[i] = copyFile(reffilepath[i - 1], userVO[i].getStringValue("userid"), userVO[i]);//拷贝模板
				} catch (Exception e) {
					MessageBox.showException(_listPanel, e);
					return;
				}
			}
		}
		int daycount = tbUtil.getSysOptionIntegerValue("违规积分自动生效时间", 5);
		String effectdate = billVO.getStringValue("EFFECTDATE", "");//【李春娟/2014-11-04】
		if (effectdate == null || "".equals(effectdate)) {//兼容以前的逻辑，如果积分登记主表没有新增的字段EFFECTDATE，则按以前的逻辑
			daycount = 5;
			effectdate = new ScoreUIUtil().getEffectDate();
		} else {//如果新增的复议截止日期设置的值，则计算到期还有几天
			daycount = new ScoreUIUtil().getDateDifference(effectdate, tbUtil.getCurrDate());//计算时间间隔【李春娟/2014-11-04】
		}
		List fileList = new ArrayList();
		for (int i = 0; i < userVO.length; i++) {
			String username = null;
			try {
				username = UIUtil.getStringValueByDS(null, "select name from pub_user where id = '" + userVO[i].getStringValue("userid") + "'");//取得违规人员姓名
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			HashMap textmap = new HashMap();//要替换的文本map,如key="$发布日期$",value="2011-05-24"
			textmap.put("$自动生效时间$", daycount + "");//【李春娟/2013-06-05】
			for (int j = 0; j < parantItemVo.length; j++) {
				if (!parantItemVo[j].getItemname().equals("违规人")) {
					if (parantItemVo[j].getItemname().equals("认定日期")) {//在认定时该字段才被赋值,此时预览生成word通知书的时候该字段还为空，所以此处取系统当前时间
						textmap.put("$" + parantItemVo[j].getItemname() + "$", userVO[i].getStringValue("effectdate", ""));
					} else {
						textmap.put("$" + parantItemVo[j].getItemname() + "$", billVO.getStringViewValue(parantItemVo[j].getItemkey()));
					}
				}
			}
			boolean isre = false; // 是否复议过
			String rescore = userVO[i].getStringValue("rescore");
			if (!tbUtil.isEmpty(rescore)) {
				isre = true;//复议过。
			}
			String[] reScoreItems = new String[] { "finalscore", "finalmoney", "redate", "reconsider", "rescoredesc" };
			for (int j = 0; j < childrenItemVo.length; j++) {
				if (!childrenItemVo[j].getItemname().equals("违规人")) {
					if (!isre) { //如果没有复议过。
						boolean ishave = false;
						for (int j2 = 0; j2 < reScoreItems.length; j2++) {
							if (childrenItemVo[j].getItemkey().equalsIgnoreCase(reScoreItems[j2])) {
								ishave = true;
								break;
							}
						}
						if (ishave) {
							textmap.put("$" + childrenItemVo[j].getItemname() + "$", ""); //如果没有复议过，把其他多余的$$替换为空
							continue;
						}
					}

					if (childrenItemVo[j].getItemname().equals("应计分值")) {
						textmap.put("$" + childrenItemVo[j].getItemname() + "$", userVO[i].getStringValue("finalscore", "")); //
					} else {
						textmap.put("$" + childrenItemVo[j].getItemname() + "$", userVO[i].getStringValue(childrenItemVo[j].getItemkey(), ""));
					}
				} else {
					textmap.put("$" + childrenItemVo[j].getItemname() + "$", username);
				}
			}
			wordutil.replaceScoreFile(reffilepath[i], textmap);//替换文本
			fileList.add(reffilepath[i]);//将文件名存储到list中，方便后面合并使用
		}
		try {
			String firstfile = copyFile(fileList.get(0).toString(), userVO[0].getStringValue("userid"), userVO[0]);//拷贝第一个通知单，后面合并是在第一个通知单的基础上合并的，所以提前多拷贝出来个
			String filename = wordutil.mergeScoreWord(fileList);//合并文档
			if (filename != null) {
				uploadofficefileTOBS(filename);//上传到服务器端office控件默认的存放地址
				OfficeCompentControlVO officeVO = new OfficeCompentControlVO(false, false, false, null); //这里需要这样写，以前用了其他项目包中类CommonHtmlOfficeConfig【李春娟/2013-05-22】
				officeVO.setIfshowsave(false);
				officeVO.setIfshowprint_all(false);
				officeVO.setIfshowprint_fen(false);
				officeVO.setIfshowprint_tao(false);
				officeVO.setIfshowedit(false);
				officeVO.setToolbar(false);
				officeVO.setIfshowclose(false);
				officeVO.setPrintable(true);
				officeVO.setMenubar(false);
				officeVO.setMenutoolbar(false);
				officeVO.setIfshowhidecomment(false);
				officeVO.setTitlebar(false);
				officeVO.setIfshowprint(false);
				officeVO.setIfshowhidecomment(false);
				officeVO.setIfshowshowcomment(false);
				officeVO.setIfshowacceptedit(false);
				officeVO.setIfshowshowedit(false);
				officeVO.setIfshowhideedit(false);
				officeVO.setIfshowwater(false);
				officeVO.setIfShowResult(false); //不显示结果区域显示。
				officeVO.setIfselfdesc(true); //关键
				officeVO.setSubdir("upload");//必须设置一下
				final BillOfficeDialog officeDialog = new BillOfficeDialog(_listPanel, "/score/tempfile/" + filename.substring(filename.lastIndexOf("/") + 1, filename.length()), officeVO);//千航插件窗口
				officeDialog.setIfselfdesc(true);
				officeDialog.setIfshowprint(true);
				officeDialog.addSomeActionListener(new PrintBillOfficeIntercept(officeDialog));
				JPanel southpanel = new JPanel(new FlowLayout());//dialog下方的按钮面板
				WLTButton btn_confirm = new WLTButton("打印", "zt_014.gif"); //
				btn_confirm.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						officeDialog.callSwingFunctionByWebBrowse("button_printall_click");
					}
				});

				WLTButton btn_close = new WLTButton("关闭"); //
				btn_close.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						officeDialog.setCloseType(BillDialog.CANCEL);
						officeDialog.callWebBrowseJavaScriptFunction("closedoc"); //
						officeDialog.dispose(); //
					}
				});

				southpanel.add(btn_confirm);
				southpanel.add(btn_close);

				officeDialog.getContentPane().add(southpanel, BorderLayout.SOUTH);
				if (SplashWindow.window != null) {
					SplashWindow.window.dispose();//打开前先将SplashWindow关闭，否则一直在后面存在【李春娟/2013-06-04】
				}
				officeDialog.setVisible(true);
				deleteTmpFiles(reffilepath);
			}
		} catch (Exception e) {
			e.printStackTrace();//客户端继续输出，以便以后查看报错日志【李春娟/2013-06-03】
			MessageBox.showException(_listPanel, e);
		}
	}

	public void print(Container _parent, String filename) throws Exception {
		if (filename != null) {
			uploadofficefileTOBS(filename);//上传到服务器端office控件默认的存放地址
			OfficeCompentControlVO officeVO = new OfficeCompentControlVO(false, false, false, null); //这里需要这样写，以前用了其他项目包中类CommonHtmlOfficeConfig【李春娟/2013-05-22】
			officeVO.setIfshowsave(false);
			officeVO.setIfshowprint_all(false);
			officeVO.setIfshowprint_fen(false);
			officeVO.setIfshowprint_tao(false);
			officeVO.setIfshowedit(false);
			officeVO.setToolbar(false);
			officeVO.setIfshowclose(false);
			officeVO.setPrintable(true);
			officeVO.setMenubar(false);
			officeVO.setMenutoolbar(false);
			officeVO.setIfshowhidecomment(false);
			officeVO.setTitlebar(false);
			officeVO.setIfshowprint(false);
			officeVO.setIfshowhidecomment(false);
			officeVO.setIfshowshowcomment(false);
			officeVO.setIfshowacceptedit(false);
			officeVO.setIfshowshowedit(false);
			officeVO.setIfshowhideedit(false);
			officeVO.setIfshowwater(false);
			officeVO.setIfShowResult(false); //不显示结果区域显示。
			officeVO.setIfselfdesc(true); //关键
			officeVO.setSubdir("upload/score/tempfile/");//必须设置一下
			final BillOfficeDialog officeDialog = new BillOfficeDialog(_parent, filename.substring(filename.lastIndexOf("/") + 1, filename.length()), officeVO);//千航插件窗口
			officeDialog.setIfselfdesc(true);
			officeDialog.setIfshowprint(true);
			officeDialog.addSomeActionListener(new PrintBillOfficeIntercept(officeDialog));
			JPanel southpanel = new JPanel(new FlowLayout());//dialog下方的按钮面板
			WLTButton btn_confirm = new WLTButton("打印", "zt_014.gif"); //
			btn_confirm.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					officeDialog.callSwingFunctionByWebBrowse("button_printall_click");
				}
			});

			WLTButton btn_close = new WLTButton("关闭"); //
			btn_close.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					officeDialog.setCloseType(BillDialog.CANCEL);
					officeDialog.callWebBrowseJavaScriptFunction("closedoc"); //
					officeDialog.dispose(); //
				}
			});

			southpanel.add(btn_confirm);
			southpanel.add(btn_close);

			officeDialog.getContentPane().add(southpanel, BorderLayout.SOUTH);
			if (SplashWindow.window != null) {
				SplashWindow.window.dispose();//打开前先将SplashWindow关闭，否则一直在后面存在【李春娟/2013-06-04】
			}
			officeDialog.setVisible(true);
			UIUtil.getMetaDataService().deleteZipFileName("score/tempfile/" + filename.substring(filename.lastIndexOf("/") + 1, filename.length()));
		}
	}

	private void deleteTmpFiles(String[] _reffilepath) {
		for (int j = 0; j < _reffilepath.length; j++) {
			File file = new File(_reffilepath[j]);
			file.deleteOnExit();//java虚拟机退出时，删除客户端的临时文件
		}
	}

	/**
	 * 上传文件逻辑处理
	 * */
	private String uploadfileTOBS(Container _listPanel, String filename) throws Exception {
		File file = new File(filename);
		FileInputStream fins = null; //
		try {
			int filelength = new Long(file.length()).intValue(); //文件大小!
			byte[] filecontent = new byte[filelength]; //一下子读进文件!!!
			fins = new FileInputStream(file);
			fins.read(filecontent);
			ClassFileVO filevo = new ClassFileVO(); //
			filevo.setByteCodes(filecontent); //设置字节!
			String str_newFileName = "/score" + filename.substring(filename.lastIndexOf("/"));
			filevo.setClassFileName(str_newFileName); //文件名
			UIUtil.uploadFileFromClient(filevo, false);//上传
			fins.close(); //
			String userid = filename.substring(filename.lastIndexOf("_") + 1, filename.lastIndexOf("."));//截取得到违规人员的id
			String modelstr = filename.substring(0, filename.lastIndexOf("_"));//辅助字符串，
			String id = modelstr.substring(modelstr.lastIndexOf("_") + 1);//取得违规人员相关信息记录的id
			UpdateSQLBuilder updateSQLBuilder = new UpdateSQLBuilder("SCORE_USER");//将改文件的地址传给相关记录的相应字段
			updateSQLBuilder.putFieldValue("PUBLISHFILEPATH", str_newFileName);
			updateSQLBuilder.setWhereCondition(" userid = '" + userid + "' and id ='" + id + "'");
			return updateSQLBuilder.getSQL();
		} catch (Exception ex) {
			ex.printStackTrace();//客户端继续输出，以便以后查看报错日志【李春娟/2013-06-03】
			MessageBox.showException(_listPanel, ex);
			return null;
		} finally {
			try {
				if (fins != null) {
					fins.close(); //
					file.deleteOnExit();
				}
			} catch (Exception e) {
				e.printStackTrace(); //
			}
		}
	}

	/**
	 * 千航 office文件上传，为了认定时在千航插件中预览用
	 * */
	private void uploadofficefileTOBS(String filename) throws Exception {
		File file = new File(filename);
		FileInputStream fins = null; //
		try {
			int filelength = new Long(file.length()).intValue(); //文件大小!
			byte[] filecontent = new byte[filelength]; //一下子读进文件!!!
			fins = new FileInputStream(file);
			fins.read(filecontent);
			ClassFileVO filevo = new ClassFileVO(); //
			filevo.setByteCodes(filecontent); //设置字节!
			String str_newFileName = filename.substring(filename.lastIndexOf("/") + 1);
			//下面第一个参数为什么设置为了"/officecompfile"？【李春娟/2013-05-22】
			UIUtil.upLoadFile("/upload/score/tempfile", str_newFileName, true, file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("\\")), file.getName(), true, false, false);//上传
			fins.close(); //

		} catch (Exception ex) {
			System.out.println(ex);
		} finally {
			try {
				if (fins != null) {
					fins.close(); //
					file.deleteOnExit();
				}
			} catch (Exception e) {
				e.printStackTrace(); //
			}
		}
	}

	/**
	 * 复制文件逻辑处理
	 * */
	private String copyFile(String _oldFilePath, String _newFilename, HashVO hashVO) throws Exception {
		File file = new File(_oldFilePath);
		InputStream input = new FileInputStream(file);
		byte[] by = tbUtil.readFromInputStreamToBytes(input);
		_newFilename = "N" + UIUtil.getSequenceNextValByDS(null, "S_PUB_FILEUPLOAD") + "_" + hashVO.getStringValue("id") + "_" + _newFilename;//新的文件名
		String newFilePath = _oldFilePath.substring(0, _oldFilePath.lastIndexOf("/") + 1) + _newFilename + ".doc";
		FileOutputStream output = new FileOutputStream(newFilePath);
		output.write(by);
		input.close();
		output.close();
		return newFilePath;
	}
}
