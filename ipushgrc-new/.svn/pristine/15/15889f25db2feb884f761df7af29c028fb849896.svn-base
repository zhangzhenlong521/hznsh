package com.pushworld.ipushgrc.ui.risk.p110;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.CommonDate;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;

import com.pushworld.ipushgrc.ui.cmpreport.CommonDialPlotPanel;
import com.pushworld.ipushgrc.ui.report.radar.BillRadarPanel;

/*******************************************************************************
 * 风险预警!!!!
 * 
 * @author xch
 * 
 */
public class RiskAlarmWKPanel extends AbstractWorkPanel implements ActionListener {

	public void initialize() {
		this.setLayout(new BorderLayout()); //
		WLTTabbedPane tabPane = new WLTTabbedPane();
		tabPane.addTab("仪表盘预警", getPlotPanel());
		tabPane.addTab("雷达图", getSpliderPanel());
		this.add(tabPane);
	}
	BillQueryPanel queryPanel_1 ;
	BillQueryPanel queryPanel_2 ;
	public JPanel getPlotPanel() {
		CommonDialPlotPanel cdpp_single = null;
		CommonDialPlotPanel cdpp_double = null;
		JPanel plotPanel = new JPanel(new FlowLayout());
		queryPanel_1 = new BillQueryPanel("TARGETREPORT_CODE1");
		queryPanel_1.addBillQuickActionListener(this);
//		queryPanel_1.setPreferredSize(new Dimension(400,70));
		queryPanel_2 = new BillQueryPanel("TARGETREPORT_CODE1");
//		queryPanel_2.setPreferredSize(new Dimension(400,70));
		queryPanel_2.addBillQuickActionListener(this);
		HashVO[] targetVO = null;
		try {
			targetVO = UIUtil.getHashVoArrayByDS(null, "select * from cmp_target where name ='事件发生发现时间间隔' and showtype='仪表盘'");
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (targetVO.length > 0) {
			double eventDate = 0d; // 时间间隔 实际值
			String type = targetVO[0].getStringValue("cycletype");  //季度，年，月
			if(type.equals("year")){  //  年度指标
				queryPanel_1.getCompentByKey("周期");
				queryPanel_1.setRealValueAt("类型","年");
			}else if(type.equals("season")){  //季度指标  
				queryPanel_1.setRealValueAt("类型","季");
			}else if(type.equals("month")){  //月度指标
				queryPanel_1.setRealValueAt("类型","月");
			}
			try {
				HashVO[] eventVO = UIUtil.getHashVoArrayByDS(null, "select id,finddate,happendate from cmp_event where 1 = 1");
				CommonDate finddate = null;
				CommonDate happendate = null;
				int days = 0;
				for (int i = 0; i < eventVO.length; i++) {
					finddate = new CommonDate(eventVO[i].getDateValue("finddate"));
					happendate = new CommonDate(eventVO[i].getDateValue("happendate"));
					days += CommonDate.getDaysBetween(happendate, finddate);
				}
				if(eventVO.length != 0){
					eventDate = (double) days / eventVO.length;	
				}
				
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			double d_warnvalue = Double.parseDouble(targetVO[0].getStringValue("warnvalue")); // 预警值
			double d_normalvalue = Double.parseDouble(targetVO[0].getStringValue("normalvalue")); // 正常值
			double d_average = Double.parseDouble(targetVO[0].getStringValue("avgvalue")); // 平均值
			double d_currvalue = eventDate;// 实际值
			cdpp_single = new CommonDialPlotPanel(d_currvalue, d_average, 50D, 0D, 50D, 0D, d_warnvalue, d_normalvalue, 5D, 5D, "事件发生时间间隔", "   ", "统一");

		}
		// ***** 发现渠道仪表盘 ********//
		HashVO[] channelVO = null; // 渠道
		try {
			channelVO = UIUtil.getHashVoArrayByDS(null, "select * from cmp_target where name ='事件发现渠道指数' and showtype='仪表盘'");
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (channelVO.length > 0) {
			double channelNum = 0d; // 时间间隔 实际值
			try {
				HashVO[] eventVO = UIUtil.getHashVoArrayByDS(null, "select cmp_event.id, dict.seq from cmp_event,pub_comboboxdict dict where cmp_event.findchannel = dict.id");
				double valueSum = 0d;
				for (int i = 0; i < eventVO.length; i++) {
					valueSum += eventVO[i].getIntegerValue("seq");
				}
				if(eventVO.length != 0){
					channelNum = valueSum / eventVO.length;
				}
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			double c_warnvalue = Double.parseDouble(channelVO[0].getStringValue("warnvalue")); // 预警值
			double c_normalvalue = Double.parseDouble(channelVO[0].getStringValue("normalvalue")); // 正常值
			double c_average = Double.parseDouble(channelVO[0].getStringValue("avgvalue")); // 平均值
			double c_currvalue = channelNum; // 实际值
			cdpp_double = new CommonDialPlotPanel(c_currvalue, c_average, 10, 0D, 10D, 0D, c_warnvalue, c_normalvalue, 1D, 1D, "事件发现渠道指数", "   ", "统一");
		}
		WLTPanel panel = new WLTPanel();
		if (cdpp_single != null) {
			JPanel firstpanel = new JPanel(new BorderLayout());
			firstpanel.add(queryPanel_1,BorderLayout.NORTH);
			panel.setPreferredSize(new Dimension(400, 400));
			panel.add(cdpp_single);
			firstpanel.add(panel,BorderLayout.CENTER);
			plotPanel.add(firstpanel);
		}

		WLTPanel panel_2 = new WLTPanel();
		if (cdpp_double != null) {
			panel_2.setPreferredSize(new Dimension(400, 400));
			panel_2.add(cdpp_double);
			JPanel secondpanel = new JPanel(new BorderLayout());
			secondpanel.add(queryPanel_2,BorderLayout.NORTH);
			secondpanel.add(panel_2,BorderLayout.CENTER);
			plotPanel.add(secondpanel);
		}
		if (cdpp_single == null && cdpp_double == null) {
			plotPanel.add(new JLabel("没有指标展现方式为仪表盘！"));
		}
		return plotPanel;
	}

	public JPanel getSpliderPanel() {
		HashVO[] vos = null;
		try {
			vos = UIUtil.getHashVoArrayByDS(null, "select '正常值' as 类型,name 指标名称,normalvalue 数值 from cmp_target where showtype='雷达图'");
		} catch (Exception e) {
			e.printStackTrace();
		}
		BillRadarPanel spliderPanel = new BillRadarPanel("风险预警", vos, new String[] { "类型" }, new String[] { "指标名称" }, "数值", true);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(spliderPanel);
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == queryPanel_1){
			HashMap conditionMap = queryPanel_1.getQuickQueryConditionAsMap();
			Object  obj_1 = conditionMap.get("机构");
			Object  obj_2 = conditionMap.get("周期");
			StringBuffer condition = new StringBuffer();
			if(obj_1!=null && !obj_1.equals("")){
				String corp = obj_1.toString();
				condition.append(" and eventcorpid = '" + corp +"'");
			}
			if(obj_2!=null && !obj_2.equals("")){
				condition.append(" and  ");
			}
			
		}else if(e.getSource() == queryPanel_2){
			
		}
			
	}

}
