package com.pushworld.ipushgrc.ui.login.p010;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.report.BillCellHtmlHrefEvent;
import cn.com.infostrategy.ui.report.BillCellHtmlHrefListener;
import cn.com.infostrategy.ui.report.BillCellPanel;

public class UserLoginStatisWKPanel extends AbstractWorkPanel implements ActionListener, BillCellHtmlHrefListener {
	private BillCellPanel billCellPanel;//报表界面
	private BillQueryPanel queryPanel;//查询面板
	private BillDialog billDialog;//钻取显示窗口
	private WLTPanel btnPanel;//放置Excel导出按钮的面板
	private WLTButton btn_export;//Excel导出按钮
	private JLabel label;
	private String date = "";//记录查询面板中查询框选择的日期
	private Calendar calendar;//日历
	private HashVO[] alluser = null;//所有人员的信息
	private HashVO[] user = null;//有记录的人员的登录信息
	private HashVO[] valuesvo;//记录人员的登录信息
	private BillCellVO cellVO;//
	private BillListPanel billListPanel;//
	private String[] dates;//存放一周的日期
	private String str_weekofyear;//本年中的第几周
	private SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private RefItemVO returnRefItemVO = null; //
	private String today;

	@Override
	public void initialize() {
		returnRefItemVO = new RefItemVO();
		today = UIUtil.getCurrDate();
		returnRefItemVO.setId(today); //
		returnRefItemVO.setName(today); //
		billCellPanel = new BillCellPanel();
		btn_export = new WLTButton("导出Excel");
		label = new JLabel("在线时长评估标准：一周在线时间大于等于20小时为合格； 登录次数评估标准：每周至少登录五次为合格");
		label.setForeground(Color.RED);
		queryPanel = new BillQueryPanel("DEPTLOGINREPORT_YQ_Q01");
		queryPanel.setCompentObjectValue("date", returnRefItemVO);
		btnPanel = new WLTPanel(new FlowLayout(FlowLayout.LEFT));
		btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		btn_export.addActionListener(this);
		btnPanel.add(btn_export, FlowLayout.LEFT);
		btnPanel.add(label, FlowLayout.CENTER);
		billCellPanel.add(btnPanel, BorderLayout.NORTH);
		billCellPanel.setEditable(false);
		billCellPanel.setToolBarVisiable(false);
		billCellPanel.addBillCellHtmlHrefListener(this);//添加html点击链接
		queryPanel.addBillQuickActionListener(this);
		this.add(queryPanel, BorderLayout.NORTH);
		this.add(billCellPanel, BorderLayout.CENTER);
	}

	private void onQuery() {
		if (queryPanel.checkValidate()) {
			try {
				date = queryPanel.getCompentRealValue("date");//取得查询框中的值
				billCellPanel.loadBillCellData(getBillcellVO(date));//加载网格数据
				billCellPanel.setEditable(false);//设置不可编辑
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private BillCellVO getBillcellVO(String date) throws ParseException {
		calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setTime(dateFormat1.parse(date));
		dates = getDayofWeek(date);//取得一周的日期
		int weekofyear = calendar.get(Calendar.WEEK_OF_YEAR);//本周属一年中第几周，注意，如果本周跨年了，则认为本周是次年的第1周
		//如果跨年，处理属于哪一年
		String year1 = dates[0].substring(0, 4);
		String year2 = dates[6].substring(0, 4);
		str_weekofyear = year1 + "年第" + weekofyear + "周( " + dates[0] + " 到 " + dates[6] + " )";//要存入记录表中的值
		if (!year1.equals(year2)) {//跨年了，用次年年号
			str_weekofyear = year2 + "年第" + weekofyear + "周( " + dates[0] + " 到 " + dates[6] + " )";
		}
		try {
			user = UIUtil.getHashVoArrayByDS(null, "select * from pub_sysdeallog where substr(dealtime,1,10) >='" + dates[0] + "' and substr(dealtime,1,10) <='" + dates[6] + "' and DEALUSERID is not null  order by DEALUSERID ,dealtime asc ");
			alluser = UIUtil
					.getHashVoArrayByDS(null,
							"select t1.userdept deptid,t3.name deptname,t2.id  id,t2.name name  from pub_user_post t1 left join pub_user t2 on t1.userid =t2.id left join PUB_CORP_DEPT t3 on t1.userdept = t3.id where t1.isdefault= 'Y' and t2.name not like 'Admin' and t3.name is not null order by t3.LINKCODE asc");
			valuesvo = new HashVO[alluser.length];
			for (int k = 0; k < alluser.length; k++) {
				boolean haslogin = false;//判读是否登录过系统
				boolean end = false;//判读是否退出系统
				boolean newday = true;//判断是否是新的一天开始
				String name = alluser[k].getStringValue("id");
				String starttime = "";//登录系统时间
				String endtime = "";//退出系统时间
				long onlinesecond = 0;//记录在线时间
				List list = new ArrayList();//存放登录日期,为了计算一周内登录的次数
				valuesvo[k] = new HashVO();
				valuesvo[k].setAttributeValue("所在部门", alluser[k].getStringValue("deptname"));
				valuesvo[k].setAttributeValue("用户名", alluser[k].getStringValue("name"));
				int size = 0;//对比list的大小来判断是否开始了新的一天
				for (int index = 0; index < user.length; index++) {
					String names = user[index].getStringValue("DEALUSERID");
					String type = user[index].getStringValue("DEALTYPE");
					String dealtime = user[index].getStringValue("DEALTIME").substring(0, 10);
					if (names.equals(name)) {//判断人员是否登录
						if (!haslogin) {
							haslogin = true;
						}
						size = list.size();//记录上次循环中list的大小
						if (!list.contains(dealtime)) {//判断list中是否存在了该日期
							list.add(dealtime);
						}
						if (size != list.size()) {//如何size的值与list的大小不同,说明开始计算新的一天
							newday = true;
						} else {
							newday = false;
						}
						if (newday) {
							if (!starttime.equals("") && !end && "登录系统".equals(user[index - 1].getStringValue("DEALTYPE"))) {//判断是否上一天登录系统后没有退出
								endtime = starttime.substring(0, 10) + " 24:00:00";//默认上一天的退出时间为24：00：00
								onlinesecond += dateFormat2.parse(endtime).getTime() - dateFormat2.parse(starttime).getTime();//计算在线时长
							}
							if ("登录系统".equals(type)) {
								starttime = user[index].getStringValue("DEALTIME");
								end = false;
							}
						} else {
							if (!end && "退出系统".equals(type)) {
								endtime = user[index].getStringValue("DEALTIME");
								onlinesecond += dateFormat2.parse(endtime).getTime() - dateFormat2.parse(starttime).getTime();//计算在线时长
								end = true;
							} else if (end && "登录系统".equals(type)) {
								starttime = user[index].getStringValue("DEALTIME");
								end = false;
							}
						}

					}
				}
				long hour = onlinesecond / (1000 * 60 * 60);//计算小时
				long m = (onlinesecond - hour * 1000 * 60 * 60) / (60 * 1000);//记录分钟
				long s = (onlinesecond - hour * 1000 * 60 * 60 - m * 60 * 1000) / 1000;//记录秒
				String onlinehours = hour + "小时" + m + "分" + s + "秒";
				valuesvo[k].setAttributeValue("在线时长", onlinehours);
				String online_ok = onlinesecond / 1000 % (24 * 3600) / 3600 >= 20 ? "合格" : "不合格";//人员在线时长是否合格
				valuesvo[k].setAttributeValue("在线时长评判", online_ok);
				valuesvo[k].setAttributeValue("登陆次数", list.size());
				if (list.size() > 5) {
					System.out.println(list);
				}
				String count_ok = list.size() >= 5 ? "合格" : "不合格";//人员登录次数是否合格
				valuesvo[k].setAttributeValue("登陆次数评判", count_ok);
				if (!haslogin) {
					valuesvo[k].setAttributeValue("登陆次数", "0");
					valuesvo[k].setAttributeValue("登陆次数评判", "不合格");
					valuesvo[k].setAttributeValue("在线时长", "0");
					valuesvo[k].setAttributeValue("在线时长评判", "不合格");
				}
			}
			BillCellItemVO[][] billCellItemVO = new BillCellItemVO[alluser.length + 2][6];
			for (int row = 0; row < billCellItemVO.length; row++) {
				String titlename[] = new String[] { "所在部门", "用户名", "在线时长", "在线时长评估", "登录次数", "登录次数评估" };
				for (int col = 0; col < billCellItemVO[0].length; col++) {
					billCellItemVO[row][col] = new BillCellItemVO();
					billCellItemVO[row][col].setCellrow(row);
					billCellItemVO[row][col].setCellcol(col);
					if (col == 0) {
						billCellItemVO[row][col].setColwidth("150"); //
					} else {
						billCellItemVO[row][col].setColwidth("100"); //
					}
					if (row > 1) {
						billCellItemVO[row][col].setRowheight("20");
						billCellItemVO[row][col].setFontsize("12");
						billCellItemVO[row][col].setCellvalue(valuesvo[row - 2].getStringValue(col)); //
						if (col == 2) {//给在线时长一列加上html链接
							billCellItemVO[row][col].setIshtmlhref("Y");
							billCellItemVO[row][col].setForeground("0,0,255");
							billCellItemVO[row][col].setCellkey(valuesvo[row - 2].getStringValue(1));//设置key值，方便点击链接后弹出的窗口中数据的过滤
						}
					} else if (row == 1) {//表头显示处理
						billCellItemVO[row][col].setHalign(2);
						billCellItemVO[row][col].setRowheight("35");
						billCellItemVO[row][col].setFontsize("14");
						billCellItemVO[row][col].setBackground("200,255,230");
						billCellItemVO[row][col].setCellvalue(titlename[col]); //
					} else if (row == 0 && col == 0) {//大标题显示
						billCellItemVO[0][0].setCellvalue(str_weekofyear); //
						billCellItemVO[0][0].setHalign(2);
						billCellItemVO[0][0].setFonttype("宋体");
						billCellItemVO[0][0].setFontsize("14"); //
						billCellItemVO[0][0].setFontstyle("1"); //
						billCellItemVO[0][0].setRowheight("40"); //
						billCellItemVO[0][0].setBackground("200,255,230");
						billCellItemVO[0][0].setSpan("1,6"); //
					}
				}
			}
			cellVO = new BillCellVO();
			cellVO.setRowlength(alluser.length + 2); //
			cellVO.setCollength(6); //
			cellVO.setCellItemVOs(billCellItemVO);

		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return cellVO;

	}

	/* 得到一个日期所在一周的七天
	*/
	private String[] getDayofWeek(String str_date) throws ParseException {
		String[] week = new String[7];
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateFormat1.parse(str_date));
		int weekNum = cal.get(Calendar.DAY_OF_WEEK);
		if (weekNum == 1) {//如果是周日，减一天，再得本周日期
			cal.add(cal.DATE, -1);
		}
		for (int i = 1; i <= 7; i++) {//外国认为1周的第一天是上周的星期日,周一第2天，
			cal.set(Calendar.DAY_OF_WEEK, i);//i值代表一周的第几天
			cal.add(Calendar.DAY_OF_YEAR, 1);//为得到的日期都加上一天，这样就得到中国理解的一周
			week[i - 1] = dateFormat1.format(cal.getTime());
		}
		return week;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_export) {
			billCellPanel.exportExcel();//导出Excel
		} else {
			onQuery();//查询
		}
	}

	public void onBillCellHtmlHrefClicked(BillCellHtmlHrefEvent e) {
		if (e.getCellItemValue().equals("0")) {
			MessageBox.show(this, "该人员在这周没有登录系统.");
			return;
		}
		billDialog = new BillDialog(this, str_weekofyear + "人员登录信息详情", 600, 650);
		billListPanel = new BillListPanel("PUB_SYSDEALLOG_CODE1_ZYC");
		billListPanel.QueryDataByCondition(" substr(dealtime,1,10) >='" + dates[0] + "' and substr(dealtime,1,10) <='" + dates[6] + "' and DEALUSERNAME like '%/" + e.getCellItemKey() + "'");//过滤数据
		billDialog.add(billListPanel, BorderLayout.CENTER);
		billDialog.addConfirmButtonPanel(1);//添加确定按钮
		billDialog.setVisible(true);
	}

}
