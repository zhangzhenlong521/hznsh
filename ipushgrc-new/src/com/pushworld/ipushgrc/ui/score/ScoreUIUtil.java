package com.pushworld.ipushgrc.ui.score;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.report.ReportExportWord;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.to.score.ScoreWordTBUtil;
import com.pushworld.ipushgrc.ui.score.p020.RegisterEditWKPanel;
import com.pushworld.ipushgrc.ui.score.p020.ScoreRegisterTool;

/**
 * 违规积分模块客户端工具【李春娟/2013-05-17】
 * @author lcj
 *
 */
public class ScoreUIUtil {
	private TBUtil util = new TBUtil();
	private ScoreWordTBUtil wordutil = new ScoreWordTBUtil();//word合并及替换工具

	/**
	 * 显示某个部门的积分
	 * @param _parent
	 * @param _title
	 * @param _deptid
	 * @param _deptname
	 */
	public void showDeptScore(Container _parent, String _title, String _deptid, String _deptname) {
		String effectdate = util.getCurrDate();
		String sql1 = "select sum(finalscore) from v_score_user where deptid =" + _deptid + " and EFFECTDATE like '" + effectdate.substring(0, 4) + "%' and state='已生效'";
		String sql2 = "select sum(realscore) from score_reduce where corpid = " + _deptid + " and REALSCORE is not null";
		try {
			String str_score = UIUtil.getStringValueByDS(null, sql1);
			if (str_score == null || str_score.equals("")) {
				str_score = "0";
			}
			Float score = Float.parseFloat(str_score);
			String str_reducescore = UIUtil.getStringValueByDS(null, sql2);
			if (str_reducescore == null || str_reducescore.equals("")) {
				str_reducescore = "0";
			}
			Float reducescore = Float.parseFloat(str_reducescore);
			Float totalscore = score - reducescore;
			StringBuffer scoreBuffer = new StringBuffer("部门【" + _deptname + "】的积分如下：\r\n\r\n");
			scoreBuffer.append("总违规积分：");

			if (score.intValue() == score.floatValue()) {//判断该分值小数部分是否为0，如果为0则舍去小数点，如2.0变为2【李春娟/2013-06-06】
				scoreBuffer.append(score.intValue());
			} else {
				scoreBuffer.append(score);
			}
			scoreBuffer.append(" 分\r\n总减免积分：");
			if (reducescore.intValue() == reducescore.floatValue()) {//判断该分值小数部分是否为0，如果为0则舍去小数点，如2.0变为2【李春娟/2013-06-06】
				scoreBuffer.append(reducescore.intValue());
			} else {
				scoreBuffer.append(reducescore);
			}
			scoreBuffer.append(" 分\r\n    总积分：");
			if (totalscore.intValue() == totalscore.floatValue()) {//判断该分值小数部分是否为0，如果为0则舍去小数点，如2.0变为2【李春娟/2013-06-06】
				scoreBuffer.append(totalscore.intValue());
			} else {
				scoreBuffer.append(totalscore);
			}
			scoreBuffer.append(" 分\r\n\r\n总积分=总违规积分-总减免积分");
			scoreBuffer.append("\r\n(上述积分截止到" + effectdate + ")");
			if (_title == null) {
				_title = "部门【" + _deptname + "】本年度的积分";
			}
			MessageBox.showTextArea(_parent, _title, scoreBuffer.toString());
		} catch (Exception e) {
			MessageBox.showException(_parent, e);
			e.printStackTrace();
		}
	}

	/**
	 * 显示某用户各个机构的积分，包括违规积分、减免积分和总积分
	 * @param _parent
	 * @param _title
	 * @param _deptid
	 * @param _userid
	 * @param _username
	 */
	public void showOneUserScore(Container _parent, String _title, String _deptid, String _userid, String _username) {
		String effectdate = util.getCurrDate();
		try {
			StringBuffer sb_sql = new StringBuffer("select t3.name 机构名称,t1.finalscore 总违规积分,t2.realscore 总减免积分, t1.finalscore-t2.realscore 总积分,t3.id 机构主键 from");
			sb_sql.append(" (select deptid,sum(finalscore) finalscore from v_score_user where userid=" + _userid + " and EFFECTDATE like '" + effectdate.substring(0, 4) + "%' and state='已生效' group by deptid) t1 ");
			sb_sql.append(" left join (select corpid,sum(realscore) realscore from score_reduce where userid=" + _userid + " and realscore is not null group by corpid) t2 on t1.deptid=t2.corpid ");
			sb_sql.append(" left join pub_corp_dept t3 on t1.deptid=t3.id ");
			sb_sql.append(" union all select '合计' 机构名称,t1.finalscore 总违规积分,t2.realscore 总减免积分, t1.finalscore-t2.realscore 总积分,0 机构主键 from ");
			sb_sql.append(" (select sum(finalscore) finalscore from v_score_user where userid=" + _userid + " and EFFECTDATE like '" + effectdate.substring(0, 4) + "%' and state='已生效') t1 ");
			sb_sql.append(" left join (select sum(realscore) realscore from score_reduce where userid=" + _userid + " and realscore is not null) t2 on 1=1");

			BillListPanel listPanel = new BillListPanel(null, sb_sql.toString());
			int rowcount = listPanel.getRowCount();
			if (rowcount == 0) {
				MessageBox.show(_parent, "该用户暂无积分!");
				return;
			}
			for (int i = 0; i < rowcount; i++) {
				String finalscore = listPanel.getRealValueAtModel(i, 2);
				if (finalscore == null || "".equals(finalscore)) {
					listPanel.setRealValueAt("0", i, "总违规积分");
				}
				String reduceScore = listPanel.getRealValueAtModel(i, 3);
				if (reduceScore == null || "".equals(reduceScore)) {
					listPanel.setRealValueAt("0", i, "总减免积分");
					listPanel.setRealValueAt(listPanel.getRealValueAtModel(i, 2), i, "总积分");
				}
			}
			listPanel.getTitlePanel().setVisible(true);
			listPanel.getTitleLabel().setForeground(Color.RED);

			if (_deptid != null && !_deptid.equals("")) {
				listPanel.getTitleLabel().setText("※申请减免分值应不大于该减免部门的总积分（总积分=总违规积分-总减免积分）");
				for (int i = 0; i < rowcount; i++) {
					String deptid = listPanel.getRealValueAtModel(i, 5);
					if (_deptid.equals(deptid)) {
						listPanel.setItemBackGroundColor(Color.YELLOW, i);
						listPanel.setItemForeGroundColor("FF0000", i, "总积分");
						break;
					}
				}
			} else {
				listPanel.getTitleLabel().setText("※总积分=总违规积分-总减免积分");
				listPanel.setItemBackGroundColor(Color.YELLOW, rowcount - 1);//合计行
				listPanel.setItemForeGroundColor("FF0000", rowcount - 1, "总积分");
			}
			listPanel.setItemVisible("机构主键", false);
			if (_title == null) {
				_title = "用户【" + _username + "】本年度的积分(截止到" + effectdate + ")";
			}
			final BillDialog dialog = new BillDialog(_parent, _title, 600, 300);

			WLTButton btn_close = new WLTButton("关闭");
			btn_close.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					dialog.setCloseType(BillDialog.CANCEL);
					dialog.dispose();
				}
			});
			JPanel southPanel = WLTPanel.createDefaultPanel();
			southPanel.add(btn_close);
			dialog.getContentPane().add(listPanel, BorderLayout.CENTER);
			dialog.getContentPane().add(southPanel, BorderLayout.SOUTH);
			dialog.setVisible(true);
		} catch (Exception e) {
			MessageBox.showException(_parent, e);
			e.printStackTrace();
		}
	}

	/**
	 * 违规积分认定时获得生效日期【李春娟/2013-06-03】
	 * @return
	 */
	public String getEffectDate() {
		int daycount = util.getSysOptionIntegerValue("违规积分自动生效时间", 5);//默认5天
		long ll_diff = util.getDiffServerTime(); //取得客户端与服务器端时间的差异
		SimpleDateFormat sdf_curr = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date(System.currentTimeMillis() - ll_diff);
		GregorianCalendar calendar = new GregorianCalendar(); //
		calendar.setTime(date); //设置日期
		calendar.add(Calendar.DAY_OF_MONTH, daycount); //增加
		return sdf_curr.format(calendar.getTime());
	}

	/**
	 * 计算两个日期的时间间隔【李春娟/2014-11-04】
	 * @param str_olddate
	 * @param str_newdate
	 * @return
	 */
	public int getDateDifference(String str_olddate, String str_newdate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //
		if (str_newdate == null || str_newdate.trim().equals("") || str_olddate == null || str_olddate.trim().equals("")) {
			return 0;
		}
		int str_return = 0;
		try {
			Date date = sdf.parse(str_olddate);
			Date newdate = sdf.parse(str_newdate);
			int difference_year = date.getYear() - newdate.getYear();
			int difference_month = 0;
			int difference_day = 0;
			if (difference_year >= 0) {
				difference_month = difference_year * 12 + date.getMonth() - newdate.getMonth();
				difference_day = date.getDate() - newdate.getDate();
				str_return = difference_month * 30 + difference_day;
			} else {
				difference_month = -1;
				str_return = difference_month;
			}
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		return str_return;
	}

	/**
	 * 减免通知书
	 * 通过cellvo机制，服务器端生产word在客户端预览并且支持打印
	 */
	public void viewAndPrintJMBYCellPanel(Container _parent, String id) {
		if (TBUtil.isEmpty(id)) {
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
		BillVO bvo[] = null;
		try {
			bvo = UIUtil.getBillVOsByDS(null, "select * from SCORE_REDUCE where id = " + id, UIUtil.getPub_Templet_1VO("SCORE_REDUCE_ZYC_E02"));
		} catch (WLTRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (bvo == null || bvo.length == 0) {
			return;
		}
		BillVO vo = bvo[0];
		BillCellVO cellvo = null;
		try {
			cellvo = UIUtil.getMetaDataService().getBillCellVO("违规积分减免模板", null, null);
		} catch (Exception ex) {
			WLTLogger.getLogger(RegisterEditWKPanel.class).error("没有找到[违规积分减免模板]模版.");
		}
		try {
			if (cellvo == null) {
				BillListPanel listpanel = new BillListPanel(new cn.com.infostrategy.ui.report.cellcompent.CellTMO());
				listpanel.QueryDataByCondition(" templetcode like '%减免%'");
				BillListDialog listdialog = new BillListDialog(_parent, "请选择想对应的模版", listpanel);
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
			HashVO hvo = new HashVO();
			String keys[] = vo.getKeys();
			String names[] = vo.getNames();
			for (int i = 0; i < keys.length; i++) {
				hvo.setAttributeValue(keys[i], vo.getStringViewValue(keys[i]));
				hvo.setAttributeValue(names[i].trim(), vo.getStringViewValue(keys[i]));
			}
			ReportExportWord word = new ReportExportWord();
			BillCellVO cellvo_new = UIUtil.getCommonService().parseCellTempetToWord(cellvo, hvo);
			String filename = System.currentTimeMillis() + "_" + vo.getStringValue("userid");
			word.exportWordFile(cellvo_new, tmpfilepath + "/", filename);
			new ScoreRegisterTool().print(_parent, tmpfilepath + "/" + filename + ".doc");
			deleteTmpFiles(new String[] { tmpfilepath + "/" + filename + ".doc" });
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	
	
	/**
	 * 惩罚通知书
	 * 通过cellvo机制，服务器端生产word在客户端预览并且支持打印
	 */
	public void viewAndPrintCFBYCellPanel(Container _parent, String id) {
		if (TBUtil.isEmpty(id)) {
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
		BillVO bvo[] = null;
		try {
			bvo = UIUtil.getBillVOsByDS(null, "select * from v_score_user where id = " + id, UIUtil.getPub_Templet_1VO("SCORE_USER_LCJ_Q04"));
		} catch (WLTRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (bvo == null || bvo.length == 0) {
			return;
		}
		BillVO vo = bvo[0];
		BillCellVO cellvo = null;
		try {
			cellvo = UIUtil.getMetaDataService().getBillCellVO("违规积分惩罚通知书模板", null, null);
		} catch (Exception ex) {
			WLTLogger.getLogger(RegisterEditWKPanel.class).error("没有找到[违规积分惩罚通知书模板]模版.");
		}
		try {
			if (cellvo == null) {
				BillListPanel listpanel = new BillListPanel(new cn.com.infostrategy.ui.report.cellcompent.CellTMO());
				listpanel.QueryDataByCondition(" templetcode like '%惩罚%'");
				BillListDialog listdialog = new BillListDialog(_parent, "请选择想对应的模版", listpanel);
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
			HashVO hvo = new HashVO();
			String keys[] = vo.getKeys();
			String names[] = vo.getNames();
			for (int i = 0; i < keys.length; i++) {
				hvo.setAttributeValue(keys[i], vo.getStringViewValue(keys[i]));
				hvo.setAttributeValue(names[i].trim(), vo.getStringViewValue(keys[i]));
			}
			ReportExportWord word = new ReportExportWord();
			BillCellVO cellvo_new = UIUtil.getCommonService().parseCellTempetToWord(cellvo, hvo);
			String filename = System.currentTimeMillis() + "_" + vo.getStringValue("userid");
			word.exportWordFile(cellvo_new, tmpfilepath + "/", filename);
			new ScoreRegisterTool().print(_parent, tmpfilepath + "/" + filename + ".doc");
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
}
