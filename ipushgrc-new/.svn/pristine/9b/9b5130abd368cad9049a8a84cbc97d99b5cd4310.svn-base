package com.pushworld.ipushgrc.ui.cmpevent.p140;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.CommonDate;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;

import com.pushworld.ipushgrc.ui.cmpreport.CommonDialPlotPanel;

/**
 * 成功防范和违规事件仪表盘!!!
 * 
 * @author lichunjuan.
 */
public class EventDialWKPanel extends AbstractWorkPanel implements ActionListener {
	private BillQueryPanel queryPanel_dial1, queryPanel_dial2, queryPanel_dial3;
	private JPanel mainPanel_dial1, mainPanel_dial2, mainPanel_dial3;
	private TBUtil tbutil = new TBUtil();

	public void initialize() {
		WLTTabbedPane pane = new WLTTabbedPane();
		if ("Y".equalsIgnoreCase(this.getMenuConfMapValueAsStr("是否显示成功防范", "Y"))) {
			pane.addTab("成功防范-报告及时率", getDialMainPanel1());
		}
		pane.addTab("违规事件-发现及时率", getDialMainPanel2());
		pane.addTab("违规事件-报告及时率", getDialMainPanel3());
		this.add(pane);
	}

	/**
	 * 有查询面板的仪表盘-成功防范报告及时率
	 * @return
	 */
	public JPanel getDialMainPanel1() {
		queryPanel_dial1 = new BillQueryPanel("CMP_CONTROL_CODE1");
		String curryear = tbutil.getCurrDate().substring(0, 4);
		HashVO hvo = new HashVO();
		hvo.setAttributeValue("querycondition", "(({itemkey}>='" + curryear + "-01-01' and {itemkey}<='" + curryear + "-12-31 24:00:00'))"); //
		RefItemVO refitemvo = new RefItemVO(curryear + ";", "年;", curryear + ";", hvo);
		queryPanel_dial1.setCompentObjectValue("date", refitemvo);//设置查询默认条件

		queryPanel_dial1.addBillQuickActionListener(this);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(queryPanel_dial1, BorderLayout.NORTH);
		mainPanel_dial1 = new JPanel(new BorderLayout());
		mainPanel_dial1.add(new JLabel("请选择查询条件后，点击查询按钮"));
		panel.add(mainPanel_dial1, BorderLayout.CENTER);
		return panel;
	}

	/**
	 * 有查询面板的仪表盘-违规事件发现及时率
	 * @return
	 */
	public JPanel getDialMainPanel2() {
		queryPanel_dial2 = new BillQueryPanel("CMP_CONTROL_CODE1");
		String curryear = tbutil.getCurrDate().substring(0, 4);
		HashVO hvo = new HashVO();
		hvo.setAttributeValue("querycondition", "(({itemkey}>='" + curryear + "-01-01' and {itemkey}<='" + curryear + "-12-31 24:00:00'))"); //
		RefItemVO refitemvo = new RefItemVO(curryear + ";", "年;", curryear + ";", hvo);
		queryPanel_dial2.setCompentObjectValue("date", refitemvo);//设置查询默认条件

		queryPanel_dial2.addBillQuickActionListener(this);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(queryPanel_dial2, BorderLayout.NORTH);
		mainPanel_dial2 = new JPanel(new BorderLayout());
		mainPanel_dial2.add(new JLabel("请选择查询条件后，点击查询按钮"));
		panel.add(mainPanel_dial2, BorderLayout.CENTER);
		return panel;
	}

	/**
	 * 有查询面板的仪表盘-违规事件报告及时率
	 * @return
	 */
	public JPanel getDialMainPanel3() {
		queryPanel_dial3 = new BillQueryPanel("CMP_CONTROL_CODE1");
		String curryear = tbutil.getCurrDate().substring(0, 4);
		HashVO hvo = new HashVO();
		hvo.setAttributeValue("querycondition", "(({itemkey}>='" + curryear + "-01-01' and {itemkey}<='" + curryear + "-12-31 24:00:00'))"); //
		RefItemVO refitemvo = new RefItemVO(curryear + ";", "年;", curryear + ";", hvo);
		queryPanel_dial3.setCompentObjectValue("date", refitemvo);//设置查询默认条件

		queryPanel_dial3.addBillQuickActionListener(this);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(queryPanel_dial3, BorderLayout.NORTH);
		mainPanel_dial3 = new JPanel(new BorderLayout());
		mainPanel_dial3.add(new JLabel("请选择查询条件后，点击查询按钮"));
		panel.add(mainPanel_dial3, BorderLayout.CENTER);
		return panel;
	}

	/**
	 *  得到仪表盘-成功防范报告及时率
	 */
	public JPanel getDial1() {
		String str_cons = queryPanel_dial1.getQuerySQLConditionByItemKeyMapping(new String[][] { { "deptid", "reportcorp" }, { "date", "finddate" } });//
		StringBuffer sql = new StringBuffer();
		sql.append("select id,finddate,reportdate from cmp_ward where 1=1 ");
		if (str_cons != null && !str_cons.equals("")) {
			sql.append(str_cons);
		}
		HashVO[] eventVO = null;
		try {
			eventVO = UIUtil.getHashVoArrayByDS(null, sql.toString());
		} catch (Exception e1) {
			MessageBox.showException(this, e1);
			return new JPanel();
		}
		CommonDate finddate = null;
		CommonDate reportdate = null;
		int days = 0;
		for (int i = 0; i < eventVO.length; i++) {
			finddate = new CommonDate(eventVO[i].getDateValue("finddate"));
			reportdate = new CommonDate(eventVO[i].getDateValue("reportdate"));
			days += CommonDate.getDaysBetween(finddate, reportdate);
		}
		DecimalFormat df = new DecimalFormat("0");
		String eventDate = "0";
		if (eventVO.length != 0) {
			eventDate = df.format((double) days / eventVO.length);// 时间间隔 实际值（平均值）
		}

		HashVO vo = new HashVO();
		vo.setAttributeValue("标题", "成功防范报告及时率"); //成功防范报告及时率
		vo.setAttributeValue("X轴", "天数"); //
		vo.setAttributeValue("实际值", eventDate); //
		vo.setAttributeValue("最小值", 0); //
		vo.setAttributeValue("正常值", getMenuConfMapValueAsStr("仪表盘正常值", "30")); //仪表盘正常值、警界值和最大值都由菜单参数传入【李春娟/2012-03-30】
		vo.setAttributeValue("警界值", getMenuConfMapValueAsStr("仪表盘警界值", "60")); //
		vo.setAttributeValue("最大值", getMenuConfMapValueAsStr("仪表盘最大值", "90")); //
		vo.setAttributeValue("背景色", "62FFFF");//徐老师建议两个仪表盘的背景色要有差别，故修改之【李春娟/2014-02-27】
		vo.setAttributeValue("提示", "发现时间与报告时间相差天数的平均数"); //
		try {
			return CommonDialPlotPanel.getDialPlotPanelByHashVO(vo, true);//警界值大于正常值
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
		return new JPanel();
	}

	/**
	 *  得到仪表盘-违规事件发现及时率
	 */
	public JPanel getDial2() {
		String str_cons = queryPanel_dial2.getQuerySQLConditionByItemKeyMapping(new String[][] { { "deptid", "reportcorp" }, { "date", "finddate" } });//
		StringBuffer sql = new StringBuffer();
		sql.append("select id,finddate,happendate from cmp_event where 1=1 ");
		if (str_cons != null && !str_cons.equals("")) {
			sql.append(str_cons);
		}
		HashVO[] eventVO = null;
		try {
			eventVO = UIUtil.getHashVoArrayByDS(null, sql.toString());
		} catch (Exception e1) {
			MessageBox.showException(this, e1);
			return new JPanel();
		}
		CommonDate finddate = null;
		CommonDate happendate = null;
		int days = 0;
		for (int i = 0; i < eventVO.length; i++) {
			finddate = new CommonDate(eventVO[i].getDateValue("finddate"));
			happendate = new CommonDate(eventVO[i].getDateValue("happendate"));
			days += CommonDate.getDaysBetween(happendate, finddate);
		}
		DecimalFormat df = new DecimalFormat("0");
		String eventDate = "0";
		if (eventVO.length != 0) {
			eventDate = df.format((double) days / eventVO.length);// 时间间隔 实际值（平均值）
		}

		HashVO vo = new HashVO();
		vo.setAttributeValue("标题", "违规事件发现及时率"); //违规事件发现及时率
		vo.setAttributeValue("X轴", "天数"); //
		vo.setAttributeValue("实际值", eventDate); //
		vo.setAttributeValue("最小值", 0); //
		vo.setAttributeValue("正常值", getMenuConfMapValueAsStr("仪表盘正常值", "30")); //
		vo.setAttributeValue("警界值", getMenuConfMapValueAsStr("仪表盘警界值", "60")); //
		vo.setAttributeValue("最大值", getMenuConfMapValueAsStr("仪表盘最大值", "90")); //
		vo.setAttributeValue("背景色", "FFBD9D");
		vo.setAttributeValue("提示", "发现时间与报告时间相差天数的平均数"); //
		try {
			return CommonDialPlotPanel.getDialPlotPanelByHashVO(vo, true);//警界值大于正常值
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
		return new JPanel();
	}

	/**
	 *  得到仪表盘-违规事件报告及时率
	 */
	public JPanel getDial3() {
		String str_cons = queryPanel_dial3.getQuerySQLConditionByItemKeyMapping(new String[][] { { "deptid", "reportcorp" }, { "date", "finddate" } });//
		StringBuffer sql = new StringBuffer();
		sql.append("select id,finddate,reportdate from cmp_event where 1=1 ");
		if (str_cons != null && !str_cons.equals("")) {
			sql.append(str_cons);
		}
		HashVO[] eventVO = null;
		try {
			eventVO = UIUtil.getHashVoArrayByDS(null, sql.toString());
		} catch (Exception e1) {
			MessageBox.showException(this, e1);
			return new JPanel();
		}
		CommonDate finddate = null;
		CommonDate reportdate = null;
		int days = 0;
		for (int i = 0; i < eventVO.length; i++) {
			finddate = new CommonDate(eventVO[i].getDateValue("finddate"));
			reportdate = new CommonDate(eventVO[i].getDateValue("reportdate"));
			days += CommonDate.getDaysBetween(finddate, reportdate);
		}
		DecimalFormat df = new DecimalFormat("0");
		String eventDate = "0";
		if (eventVO.length != 0) {
			eventDate = df.format((double) days / eventVO.length);// 时间间隔 实际值（平均值）
		}

		HashVO vo = new HashVO();
		vo.setAttributeValue("标题", "违规事件报告及时率"); //违规事件报告及时率
		vo.setAttributeValue("X轴", "天数"); //
		vo.setAttributeValue("实际值", eventDate); //
		vo.setAttributeValue("最小值", 0); //
		vo.setAttributeValue("正常值", getMenuConfMapValueAsStr("仪表盘正常值", "30")); //
		vo.setAttributeValue("警界值", getMenuConfMapValueAsStr("仪表盘警界值", "60")); //
		vo.setAttributeValue("最大值", getMenuConfMapValueAsStr("仪表盘最大值", "90")); //
		vo.setAttributeValue("背景色", "FFBD9D");
		vo.setAttributeValue("提示", "发现时间与报告时间相差天数的平均数"); //
		try {
			return CommonDialPlotPanel.getDialPlotPanelByHashVO(vo, true);//警界值大于正常值
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
		return new JPanel();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == queryPanel_dial1) {
			if (!queryPanel_dial1.checkValidate()) {
				return;
			}
			mainPanel_dial1.removeAll();
			mainPanel_dial1.add(getDial1(), BorderLayout.CENTER);
			mainPanel_dial1.revalidate();
		} else if (e.getSource() == queryPanel_dial2) {
			if (!queryPanel_dial2.checkValidate()) {
				return;
			}
			mainPanel_dial2.removeAll();
			mainPanel_dial2.add(getDial2(), BorderLayout.CENTER);
			mainPanel_dial2.revalidate();
		} else if (e.getSource() == queryPanel_dial3) {
			if (!queryPanel_dial3.checkValidate()) {
				return;
			}
			mainPanel_dial3.removeAll();
			mainPanel_dial3.add(getDial3(), BorderLayout.CENTER);
			mainPanel_dial3.revalidate();
		}
	}
}
