package cn.com.pushworld.salary.ui.person.p021;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.DivComponentLayoutUtil;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.pushworld.salary.to.SalaryTBUtil;
import cn.com.pushworld.salary.ui.SalaryUIUtil;
import cn.com.pushworld.salary.ui.person.p020.PersonMutualWKPanel;

/**
 * 岗责评议打分界面
 * @author haoming
 * create by 2016-1-15
 */
public class PostDutyMutualWKPanel extends AbstractWorkPanel implements ActionListener {
	private static final long serialVersionUID = 2940195240282541876L;
	private String userid = ClientEnvironment.getCurrSessionVO().getLoginUserId(); // 当前登录人ID
	private WLTTabbedPane tabpane = new WLTTabbedPane();// 如果
	private HashVO currLog = null; // 当前计划日志ID。
	private boolean editable = true;
	private HashVO currCheckVOs[]; // 当前人员评分的所有内容
	private TBUtil tbutil = new TBUtil();
	private String input_value_range = TBUtil.getTBUtil().getSysOptionStringValue("薪酬模块人员打分区间", "0-10");
	private static final Logger logger = WLTLogger.getLogger(PersonMutualWKPanel.class);
	private SalaryTBUtil stbutil = new SalaryTBUtil();
	private BillCellPanel cellpanel;
	private int value_col_start_index = 1; // 正真的数据从的第几列开始。
	private int value_row_start_index = 2; // 正真的数据从的第几行开始。
	private boolean issubmited = false; //加载数据后判断是否已经全部提交。
	private String markStyle = tbutil.getSysOptionStringValue("岗责评议打分界面模式", "混合"); //值：混合；多页签
	private JPanel init() {
		HashVO vos[];
		try {
			// 求出登陆人可评的所有计划
			vos = UIUtil.getHashVoArrayByDS(null, "select distinct(t1.id),t1.checkdate,t1.status,t1.name from SAL_TARGET_CHECK_LOG t1,sal_post_duty_target_list t2 where t1.id = t2.logid and t1.status!='考核结束' and t2.scoreuser='" + userid + "'");
			if (vos == null || vos.length == 0) {
				//查最近一个月的。
				HashVO[] planvos = UIUtil.getHashVoArrayByDS(null, "select * from SAL_TARGET_CHECK_LOG where checkdate order by checkdate desc");
				if (planvos.length > 0) {
					planvos = new HashVO[] { planvos[0] };
				}

				JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.CENTER));
				WLTLabel label = new WLTLabel("目前全行没有考评中的计划...");
				label.setFont(new Font("宋体", Font.BOLD, 16));
				panel.add(label);
				return panel;
			}
			if (vos.length > 1) { // 如果有多个用bom显示。
			} else {// 如果现有就一个执行中
				return loadPanelByLogPlanVO();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JPanel();
	}

	/*
	 * 从其他类中调用初始化方法。需要传入页签，当前的计划VO，开始的页签数
	 */
	public void customInit(WLTTabbedPane _tabPanel, HashVO _logVO, String _userid, boolean _editable) {
		tabpane = _tabPanel;
		currLog = _logVO;
		userid = _userid;
		editable = _editable;
		loadPanelByLogPlanVO();
	}

	/*
	 * 根据选择的计划，获取评分面板。
	 */
	private JPanel loadPanelByLogPlanVO() {
		try {
			cellpanel = new BillCellPanel(getBillCellVo());
			cellpanel.putClientProperty("this", this);
			JPanel mainPanel = new WLTPanel(new BorderLayout());
			mainPanel.setOpaque(false);
			StringBuffer showWarnInfo = new StringBuffer();
			showWarnInfo.append("&nbsp;&nbsp;&nbsp;&nbsp;满意度评分标准: 非常满意 9-10 分; 满意 7-8 分; 合格 5-6 分; 基本合格 3-4 分; 不合格 0-2分.");
			WLTButton btn_submit = new WLTButton("提交", UIUtil.getImage("zt_050.gif"));
			btn_submit.setToolTipText("提交结果, 提交后不能修改");
			JLabel info_label = new JLabel("<html><font color='blue'>" + showWarnInfo.toString() + "</font></html>");
			info_label.setPreferredSize(new Dimension(550 + LookAndFeel.getFONT_REVISE_SIZE() * showWarnInfo.length(), 18));
			info_label.setVerticalTextPosition(JLabel.CENTER);
			btn_submit.addActionListener(this);
			WLTPanel btnPanel = new WLTPanel(new BorderLayout());
			btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			btnPanel.add(btn_submit, BorderLayout.WEST);
			btnPanel.add(info_label, BorderLayout.CENTER);
			mainPanel.add(btnPanel, BorderLayout.NORTH);
			mainPanel.add(cellpanel, BorderLayout.CENTER);
			tabpane.addTab("岗位评议", UIUtil.getImage("zt_023.gif"), mainPanel);
			cellpanel.setLockedCell(2, 1);
			if (issubmited) {
				btn_submit.setVisible(false);
				setCurrCellPanelStatus(cellpanel, "已 提 交");
				cellpanel.setEditable(false);
			}
			return tabpane;
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JPanel();
	}

	/*
	 * 蓝色背景的表格
	 */
	private BillCellItemVO getBillCellVO_Blue(String value) {
		BillCellItemVO item = new BillCellItemVO();
		item.setCellvalue(value);
		item.setBackground("191,213,255");
		item.setFonttype("新宋体");
		item.setFontsize("12");
		item.setFontstyle("0");
		item.setSpan("1,1");
		item.setIseditable("N");
		item.setCellhelp(null);
		return item;
	}

	/*
	 * 默认白色
	 */
	private BillCellItemVO getBillCellVO_Normal(String value) {
		BillCellItemVO item = new BillCellItemVO();
		item.setCellvalue(value);
		item.setFonttype("新宋体");
		item.setFontsize("12");
		item.setFontstyle("0");
		item.setSpan("1,1");
		item.setCellhelp(null);
		return item;
	}

	/*
	 * 标题
	 */
	private BillCellItemVO getBillTitleCellItemVO(String value) {
		BillCellItemVO item = new BillCellItemVO();
		item.setCellvalue(value);
		item.setBackground("191,213,0");
		item.setFonttype("新宋体");
		item.setFontsize("12");
		item.setFontstyle("1");
		item.setSpan("1,1");
		item.setIseditable("N");
		item.setCellhelp(null);
		return item;
	}

	public void initialize() {
		this.add(init());
	}

	// 得到可以录入值的范围,两个长度的数组.
	public int[] getInputNumRange() {
		String[] values = TBUtil.getTBUtil().split(input_value_range, "-");
		int begin = 0;
		int end = 10;
		try {
			if (values.length == 2) {
				begin = Integer.parseInt(values[0]);
				end = Integer.parseInt(values[1]);
				if (begin >= end) { // 如果值前后顺序有问题。
					begin = 0;
					end = 10;
				}
			}
		} catch (Exception _ex) {
			logger.error("", _ex);
		}
		return new int[] { begin, end };
	}

	private boolean autoCheckCommit = true;

	public void autoCheckIFCanCommit() {
		if (!autoCheckCommit) {// 是否需要自动检测
			return;
		}
		int lastColNum = cellpanel.getColumnCount();
		int lastRowNum = cellpanel.getRowCount();
		int alldatanum = 0;
		int range[] = getInputNumRange();
		try {
			for (int i = value_row_start_index; i < lastRowNum; i++) {
				for (int j = value_col_start_index; j < lastColNum; j++) {
					String cellValue = cellpanel.getValueAt(i, j);
					BillCellItemVO itemvo = cellpanel.getBillCellItemVOAt(i, j);
					if (itemvo != null && "Y".equals(itemvo.getCustProperty("nohave"))) {
						continue;
					}
					if (cellValue == null || "".equals(cellValue)) {
						return;
					} else {
						try {
							Float value = Float.parseFloat(cellValue); //
							if (value < range[0] || value > range[1]) {// 应该不会存在
								return;
							}
							alldatanum++;
						} catch (Exception _ex) {
							_ex.printStackTrace();
							return;
						}

					}
				}
			}
			// 全部填写完了。
			StringBuffer msgsb = new StringBuffer();
			new Timer().schedule(new TimerTask() {
				public void run() {
					String msg = "此页面已评分完毕,是否提交结果?";
					int index = MessageBox.showOptionDialog(cellpanel, msg, "系统提示", new String[] { "立即提交", "稍后提交" });
					if (index != 0) {
						autoCheckCommit = false;// 如果不喜欢自动提示。设置为false。
						return;
					}
					onCellPanelSubmit(cellpanel);
				}
			}, 300); // 延迟一会儿再弹出来.感受好一些。
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public BillCellVO getBillCellVo() throws Exception {
		StringBuffer sqlsb = new StringBuffer();
		sqlsb.append("select t1.* from  sal_person_postduty_score  t1 left join sal_post_duty_target_list t2 on t1.targetid=t2.id left join v_pub_user_post_2 u  on " + "u.userid = t1.checkeduser and u.isdefault='Y' left join pub_corp_Dept u2 on u2.id = u.deptid where t1.scoreuser='" + userid + "' and t1.logid='" + currLog.getStringValue("id") + "' and t1.targettype ='岗责评价指标' ");
		sqlsb.append(" order by t2.seq,u2.seq,u.seq");
		currCheckVOs = UIUtil.getHashVoArrayByDS(null, sqlsb.toString()); // 根据部门人员排序。
		LinkedHashMap<String, String> targetids = new LinkedHashMap<String, String>(); //合并所有指标
		LinkedHashMap<String, LinkedHashMap> checkedusers_targetlist = new LinkedHashMap<String, LinkedHashMap>();
		int submitcount = 0;
		for (int i = 0; i < currCheckVOs.length; i++) {
			String targetid = currCheckVOs[i].getStringValue("targetid");
			String targetname = currCheckVOs[i].getStringValue("targetname");
			String checkeduser = currCheckVOs[i].getStringValue("checkeduser", "");
			targetids.put(targetid, targetname);
			if (checkedusers_targetlist.containsKey(checkeduser)) {
				LinkedHashMap map = checkedusers_targetlist.get(checkeduser);
				map.put(targetid, currCheckVOs[i]);
			} else {
				LinkedHashMap map = new LinkedHashMap();
				map.put(targetid, currCheckVOs[i]);
				checkedusers_targetlist.put(checkeduser, map);
			}
			if ("已提交".equals(currCheckVOs[i].getStringValue("status"))) {
				submitcount++;
			}
		}
		if (submitcount == currCheckVOs.length) {
			issubmited = true;
		}
		HashMap<String, String> usernamemap = UIUtil.getHashMapBySQLByDS(null, "select id,name from v_sal_personinfo where id in(" + TBUtil.getTBUtil().getInCondition((String[]) (checkedusers_targetlist.keySet().toArray(new String[0]))) + ")");
		BillCellVO cellvo = new BillCellVO();
		String[] alltarget = (String[]) targetids.keySet().toArray(new String[0]);
		Iterator it = checkedusers_targetlist.entrySet().iterator();
		List cellrows = new ArrayList<BillCellItemVO[]>();

		List<BillCellItemVO> titleRow = new ArrayList<BillCellItemVO>();
		BillCellItemVO cell_title_1 = getBillCellVO_Blue(currLog.getStringValue("name")); // 计划名称
		cell_title_1.setHalign(2); //
		cell_title_1.setFontsize("15");
		cell_title_1.setFontstyle("1");
		cell_title_1.setSpan("1," + (alltarget.length + 1));
		titleRow.add(cell_title_1);
		for (int i = 0; i < alltarget.length; i++) {
			titleRow.add(getBillCellVO_Normal(""));
		}
		//查出所有指标的内容与评分标准
		HashVO alltargetVOS[] = UIUtil.getHashVoArrayByDS(null, "select * from sal_post_duty_target_list where id in(" + tbutil.getInCondition(targetids.keySet().toArray(new String[0])) + ")");

		List<BillCellItemVO> firstRow = new ArrayList<BillCellItemVO>();
		BillCellItemVO cell_1_1 = getBillTitleCellItemVO("考核内容");
		cell_1_1.setHalign(2); //
		cell_1_1.setFontstyle("1");
		firstRow.add(cell_1_1);

		for (int i = 0; i < alltarget.length; i++) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < alltargetVOS.length; j++) {
				String tid = alltargetVOS[j].getStringValue("id");
				if (tid != null && tid.equals(alltarget[i])) {//鼠标移动到指标名称，显示指标描述和打分标准
					sb.append("<html>");
					String descr = alltargetVOS[j].getStringValue("descr");
					if (!tbutil.isEmpty(descr)) {
						sb.append("<B>指标描述：</B>");
						sb.append("<br>");
						sb.append("<font   size=\"4\" >");
						sb.append(tbutil.replaceAll(descr, "\n", "<br>"));
						sb.append("</font>");
					}
					String evalstandard = alltargetVOS[j].getStringValue("evalstandard");
					if (!tbutil.isEmpty(evalstandard)) {
						sb.append("<br><B>评分标准</B>");
						sb.append("<br>");
						sb.append("<font   size=\"4\" >");
						sb.append(tbutil.replaceAll(evalstandard, "\n", "<br>"));
						sb.append("</font>");
					}
					sb.append("</html>");
					break;
				}
			}
			BillCellItemVO cell_title = getBillTitleCellItemVO(targetids.get(alltarget[i]));
			cell_title.setCelldesc(sb.toString());
			cell_title.setCellhelp(sb.toString());
			int length = tbutil.getStrWidth(cell_title.getCellvalue());
			cell_title.setHalign(2); //
			cell_title.setColwidth((length + 50) + "");
			firstRow.add(cell_title);
		}
		cellrows.add(titleRow.toArray(new BillCellItemVO[0]));
		cellrows.add(firstRow.toArray(new BillCellItemVO[0]));
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			String checkeduser = (String) entry.getKey();
			LinkedHashMap user_target = (LinkedHashMap) entry.getValue();
			List itemvolist = new ArrayList();
			itemvolist.add(getBillCellVO_Blue(usernamemap.get(checkeduser)));
			for (int i = 0; i < alltarget.length; i++) {
				if (user_target != null && user_target.containsKey(alltarget[i])) {
					HashVO targetVO = (HashVO) user_target.get(alltarget[i]);
					BillCellItemVO cell_N = getBillCellVO_Normal(targetVO.getStringValue("checkscore"));// 显示分数
					cell_N.setHalign(2);
					cell_N.setCustProperty("hashvo", targetVO);
					cell_N.setEditformula("AddItemValueChangedListener(\"cn.com.pushworld.salary.ui.target.p070.PostDutyMutualCheckValueChangeLinstener\",\"Cell执行\")");
					cell_N.setCelltype("NUMBERTEXT");
					itemvolist.add(cell_N);
				} else {
					BillCellItemVO cell_N = getBillCellVO_Normal("-");// 显示分数
					cell_N.setCustProperty("nohave", "Y");//没有内容
					cell_N.setIseditable("N");
					cell_N.setHalign(2);
					cell_N.setBackground("220,220,220");
					itemvolist.add(cell_N);
				}
			}
			cellrows.add(itemvolist.toArray(new BillCellItemVO[0]));
		}
		cellvo.setCellItemVOs((BillCellItemVO[][]) cellrows.toArray(new BillCellItemVO[0][0]));
		cellvo.setRowlength(cellrows.size());
		cellvo.setCollength(alltarget.length + 1);
		return cellvo;
	}

	/*
	 * Excel表单填写提交.
	 */
	private void onCellPanelSubmit(BillCellPanel cellPanel) {
		cellPanel.stopEditing();
		int lastRowNum = cellPanel.getRowCount();
		int lastColNum = cellPanel.getColumnCount();
		int alldatanum = 0;
		if (issubmited) {
			MessageBox.show(cellPanel, "已经提交!");
			return;
		}
		try {
			int range[] = getInputNumRange();
			for (int i = value_row_start_index; i < lastRowNum; i++) {
				for (int j = value_col_start_index; j < lastColNum; j++) {
					String cellValue = cellPanel.getValueAt(i, j);
					BillCellItemVO itemvo = cellPanel.getBillCellItemVOAt(i, j);
					if ("N".equals(itemvo.getIseditable())) {
						continue;
					}
					if (cellValue == null || "".equals(cellValue)) {
						MessageBox.show(cellPanel, "部分数据没有填写!");
						cellPanel.getTable().scrollRectToVisible(cellPanel.getTable().getCellRect(i, j, true));
						cellPanel.getTable().setColumnSelectionInterval(j, j);
						cellPanel.getTable().setRowSelectionInterval(i, i);
						return;
					} else {
						try {
							Float value = Float.parseFloat(cellValue); //
							if (value < range[0] || value > range[1]) {// 应该不会存在
								MessageBox.show(cellPanel, "评分值范围" + input_value_range + "之间");
								cellPanel.getTable().scrollRectToVisible(cellPanel.getTable().getCellRect(i, j, true));
								cellPanel.getTable().setColumnSelectionInterval(j, j);
								cellPanel.getTable().setRowSelectionInterval(i, i);
								return;
							}
							alldatanum++;
						} catch (Exception _ex) {
							_ex.printStackTrace();
							MessageBox.show(cellPanel, "填写值范围为" + input_value_range + ",错误数据已用红色标识,请修改后再提交.");
							cellPanel.getTable().scrollRectToVisible(cellPanel.getTable().getCellRect(i, j, true));
							cellPanel.getTable().setColumnSelectionInterval(j, j);
							cellPanel.getTable().setRowSelectionInterval(i, i);
							return;
						}

					}
				}
			}
			//			if ((Boolean) curTabVO[5]) {
			//				MessageBox.show(cellPanel, "已经提交!");
			//				return;
			//			}
			StringBuffer msgsb = new StringBuffer();
			// 需要检测服务器端和客户端是否一直.测试时发现有些数据录入了,但是没有更新到数据库中.
			HashMap undo = UIUtil.getHashMapBySQLByDS(null, "select t1.id,checkscore from  sal_person_postduty_score t1  where t1.scoreuser='" + userid + "' and t1.logid='" + currLog.getStringValue("id") + "' and t1.targettype ='岗责评价指标' and (t1.checkscore is null or t1.checkscore='') ");
			if (undo.size() > 0) { // 数据库中有空值.页面有数据.需要重新同步页面数据到服务器.
				List updateSqls = new ArrayList();
				for (int i = value_row_start_index; i < lastRowNum; i++) {
					for (int j = value_col_start_index; j < lastColNum; j++) {
						BillCellItemVO itemVO = cellPanel.getBillCellItemVOAt(i, j);
						if ("Y".equals(itemVO.getCustProperty("nohave"))) {
							continue;
						}
						HashVO hashvo = (HashVO) itemVO.getCustProperty("hashvo");
						if (!undo.containsKey(hashvo.getStringValue("id"))) {
							continue;
						}
						String score = cellPanel.getValueAt(i, j);
						UpdateSQLBuilder sqlBuilder = new UpdateSQLBuilder("sal_person_postduty_score"); // 重新update一次
						if (hashvo != null) {
							sqlBuilder.setWhereCondition(" id = '" + hashvo.getStringValue("id") + "'");
						} else {
							MessageBox.showWarn(this, "表格[" + convertIntColToEn(j + 1) + "" + (i + 1) + "]数据处理发生严重错误,请尽快联系管理员");
							InsertSQLBuilder insert = new SalaryUIUtil().getErrLogSql("岗位职责评议表格中没取到[hashvo]值", "数据错误", "错误发生在PostDutyMutualWKPanel类,表格[" + convertIntColToEn(j + 1) + "" + (i + 1) + "]执行getCustProperty方法取不到hashvo值。", "严重");
							if (insert != null) {
								UIUtil.executeUpdateByDS(null, insert);
							}
							return;
						}
						sqlBuilder.putFieldValue("lasteditdate", UIUtil.getServerCurrDate());
						sqlBuilder.putFieldValue("checkscore", score);
						sqlBuilder.putFieldValue("status", "待提交");
						updateSqls.add(sqlBuilder);
					}
				}
				UIUtil.executeBatchByDS(null, updateSqls, true, false);
				// 再重新查一次 数据库
				String strtotle = UIUtil.getStringValueByDS(null, "select count(t1.id) from  sal_person_postduty_score t1  where t1.scoreuser='" + userid + "' and t1.logid='" + currLog.getStringValue("id") + "' and t1.targettype ='岗责评价指标' and (t1.checkscore is null or t1.checkscore='')");
				if (!"0".equals(strtotle)) { // 上面把数据库中的空值补上后，再查一遍，在不一样就不管了。直接刷新页面数据。用户自己搞。
					currCheckVOs = UIUtil.getHashVoArrayByDS(null, "select t1.* from  sal_person_postduty_score t1 left join sal_post_duty_target_list t2 on t1.targetid=t2.id where t1.scoreuser='" + userid + "' and t1.logid='" + currLog.getStringValue("id") + "' and t1.targettype ='岗责评价指标' order by t1.targetid,t1.id ");
					cellPanel.loadBillCellData(getBillCellVo());// 重新加载服务器端数据
					MessageBox.show(cellPanel, "页面数据未能及时同步到服务器,数据已经刷新,请重新核实遗漏项.");
					return;
				}
			}
			if (!MessageBox.confirm(cellPanel, "提交后不能修改，确定提交吗？")) {
				return;
			}
			UpdateSQLBuilder updateSql = new UpdateSQLBuilder("sal_person_postduty_score");
			updateSql.putFieldValue("status", "已提交");
			updateSql.setWhereCondition(" logid='" + currLog.getStringValue("id") + "' and   scoreuser='" + userid + "'");
			int num = UIUtil.executeUpdateByDS(null, updateSql);
			if (num == alldatanum) {
				cellPanel.setEditable(false);
				issubmited = true;
				setCurrCellPanelStatus(cellPanel, "已 提 交");
				String[][] result = UIUtil.getStringArrayByDS(null, "select count(id),checktype from sal_person_check_score where scoreuser = " + userid + " and (status <> '已提交' or status is null) and logid=" + currLog.getStringValue("id") + " group by checktype" + " union all select count(id),targettype checktype from sal_dept_check_score where  scoreuser = " + userid
						+ " and targettype='部门定性指标' and (status <> '已提交' or status is null) and logid=" + currLog.getStringValue("id") + " group by targettype");
				StringBuffer msg = new StringBuffer("您还没有对 ");
				boolean have = false;
				for (int i = 0; i < result.length; i++) {
					if (!"0".equals(result[i][0])) {
						msg.append(result[i][1] + "、");
						have = true;
					}
				}
				if (have) {
					msg = new StringBuffer(msg.substring(0, msg.length() - 1));
				}
				if (have) {
					msg.append(" 进行评分或者提交，请点击【左上方页签】完成操作.");
					MessageBox.show(cellPanel, msg.toString());
				} else {
					MessageBox.show(cellPanel, "您已经完成了本次所有打分内容,谢谢！");
				}
			}
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void setCurrCellPanelStatus(final JComponent parent, final String _status) {
		new Timer().schedule(new TimerTask() {
			public void run() {
				JPanel jp = new JPanel() {
					protected void paintComponent(Graphics g) {
						// 盖章
						if (parent != null && parent.isShowing()) { // 搞到bufferedimage中特别不清楚。
							super.paintComponent(g);
							Graphics2D g2d = (Graphics2D) g.create();
							g2d.translate(15, 8);
							g2d.setComposite(AlphaComposite.SrcOver.derive(0.6f));
							g2d.setColor(Color.red.brighter());
							g2d.rotate(0.4);
							g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
							g2d.setStroke(new BasicStroke(3.5f));
							g2d.drawRect(1, 1, 150, 30);
							g2d.setFont(new Font("黑体", Font.BOLD, 25));
							g2d.drawString(_status, 15, 25);
						}
					}
				};
				jp.setOpaque(false);
				int i = 0;
				while (!parent.isShowing()) {
					i++;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (i > 300) { //30秒再出不来，直接返回。应该不会这么慢。
						return;
					}
				}
				Point p = parent.getLocationOnScreen();
				SwingUtilities.convertPointFromScreen(p, parent.getRootPane().getLayeredPane());
				BillCellPanel cellPanel = (BillCellPanel) parent;
				int tablewidth = (int) cellPanel.getTable().getVisibleRect().getWidth();
				int tableheight = (int) cellPanel.getTable().getVisibleRect().getHeight();
				Rectangle rect = new Rectangle((tablewidth / 2 - 40), tableheight / 2, 180, 130);
				DivComponentLayoutUtil.getInstanse().addComponentOnDiv(cellPanel, jp, rect, null); // 添加浮动层
			}
		}, 100);
	}

	private String convertIntColToEn(int _index) {
		return stbutil.convertIntColToEn(_index,false);
	}

	private String getColor(int index) {
		String[] beautifulColors = new String[] { "255,0,0", "255,60,0", "255,30,0",//
				"254,0,254", "0,254,254", "128,0,254", "0,128,0", "254,254,128", "128,0,128", "64,0,128", "128,128,0", "128,128,192", "254,128,192", "0,128,254", "128,64,0",//
				"225,213,218", "74,63,228", "200,234,210", "224,242,26", "231,192,229", "98,69,77", "192,188,226", "73,129,90", "241,244,210",//
				"225,39,214", "195,229,226", "169,143,80", "225,48,94", "100,99,108", "42,231,99", "153,159,87", "96,69,94", "74,143,138", "244,178,18" }; // 一系列好看的颜色,一共25个,依次交错排序
		return beautifulColors[index % 30];
	}

	public void actionPerformed(ActionEvent arg0) {
		onCellPanelSubmit(cellpanel);
	}

}
