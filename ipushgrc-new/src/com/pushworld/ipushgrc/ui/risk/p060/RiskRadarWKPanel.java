package com.pushworld.ipushgrc.ui.risk.p060;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.swing.JLabel;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;

import com.pushworld.ipushgrc.ui.cmpreport.CommonDialPlotPanel;
import com.pushworld.ipushgrc.ui.report.radar.BillRadarPanel;

/**
 * 风险雷达图!!!
 * 
 * @author xch.
 */
public class RiskRadarWKPanel extends AbstractWorkPanel implements ActionListener {
	BillQueryPanel queryPanel_dial, queryPanel_radar;
	JPanel mainPanel_dial, mainPanel_radar;

	public void initialize() {
		WLTTabbedPane pane = new WLTTabbedPane();
		pane.addTab("风险仪表盘", getDialMainPanel());
		pane.addTab("风险雷达图", getRadarMainPanel());
		this.add(pane);
	}

	/**
	 * 有查询面板的仪表盘
	 * @return
	 */
	public JPanel getDialMainPanel() {
		queryPanel_dial = new BillQueryPanel("CMP_CONTROL_CODE1");
		queryPanel_dial.addBillQuickActionListener(this);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(queryPanel_dial, BorderLayout.NORTH);
		mainPanel_dial = new JPanel(new BorderLayout());
		mainPanel_dial.add(new JLabel("请选择查询条件后，点击查询按钮"));
		panel.add(mainPanel_dial, BorderLayout.CENTER);
		return panel;
	}

	/**
	 *  得到仪表盘
	 */
	public JPanel getDial() {
		String[] str_counts = null;
		try {
			String str_cons = queryPanel_dial.getQuerySQLConditionByItemKeyMapping(new String[][] { { "deptid", "blcorpid" }, { "date", "identify_date" } });//
			StringBuffer sql = new StringBuffer();
			sql.append("select count(id) from v_risk_process_file where filestate='3' and ctrlfneffect like '%有效%' ");
			if (str_cons != null && !str_cons.equals("")) {
				sql.append(str_cons);
			}
			sql.append(" union all ");
			sql.append(" select count(id) from v_risk_process_file where filestate='3' ");
			if (str_cons != null && !str_cons.equals("")) {
				sql.append(str_cons);
			}
			str_counts = UIUtil.getStringArrayFirstColByDS(null, sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		double percent = 0d;
		String d_percent = "0d"; //最终的保留小数的
		if (str_counts.length != 0) {
			if (!"0".equals(str_counts[1])) {
				percent = Double.parseDouble(str_counts[0]) * 100 / Double.parseDouble(str_counts[1]);// 风险控制率
				DecimalFormat format = new DecimalFormat("0.0");
				d_percent = format.format(percent);
			}
		}
		HashVO vo = new HashVO();
		vo.setAttributeValue("标题", "所有业务的风险控制率"); // 先找出所有风险点总数!!
		// 再找出控制类型为控制基本有效和控制有效的总数!!
		// 两个总数相除,换成百分比!!
		vo.setAttributeValue("X轴", "百分比"); //
		vo.setAttributeValue("实际值", d_percent); //
		vo.setAttributeValue("最小值", 0); //
		vo.setAttributeValue("警界值", getMenuConfMapValueAsStr("仪表盘警界值", "20")); //用菜单参数传入【李春娟/2012-03-30】
		vo.setAttributeValue("正常值", getMenuConfMapValueAsStr("仪表盘正常值", "50")); //
		vo.setAttributeValue("最大值", 100); //
		vo.setAttributeValue("背景色", "62FFFF"); //
		vo.setAttributeValue("提示", "控制适当的风险点数量在所有风险点中的占比"); //
		try {
			return CommonDialPlotPanel.getDialPlotPanelByHashVO(vo, false);//风险控制率的警界值小于正常值【李春娟/2012-03-15】
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JPanel();
	}

	/**
	 * 得到风险雷达图主面板 包含查询面板。
	 */
	public JPanel getRadarMainPanel() {
		queryPanel_radar = new BillQueryPanel("CMP_CONTROL_CODE1");
		queryPanel_radar.addBillQuickActionListener(this);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(queryPanel_radar, BorderLayout.NORTH);
		mainPanel_radar = new JPanel(new BorderLayout());
		mainPanel_radar.add(new JLabel("请选择查询条件后，点击查询按钮"));
		panel.add(mainPanel_radar, BorderLayout.CENTER);
		return panel;
	}

	public JPanel getRadar() {
		HashVO[] str_counts = null;
		BillRadarPanel radar = null;
		try {
			String str_cons = queryPanel_radar.getQuerySQLConditionByItemKeyMapping(new String[][] { { "deptid", "blcorpid" }, { "date", "identify_date" } });//
			StringBuffer sql = new StringBuffer();
			sql.append("select bsactname 业务类型,'局部' 类型 ,count(bsactname) 数目 from v_risk_process_file where filestate='3' and ctrlfneffect like '%有效%' ");
			if (str_cons != null && !str_cons.equals("")) {
				sql.append(str_cons);
			}
			sql.append(" group by bsactname ");
			sql.append(" union all ");
			sql.append(" select bsactname 业务类型,'全部' 类型  ,count(bsactname) 数目 from v_risk_process_file where filestate='3' ");
			if (str_cons != null && !str_cons.equals("")) {
				sql.append(str_cons);
			}
			sql.append(" group by bsactname ");
			str_counts = UIUtil.getHashVoArrayByDS(null, sql.toString());
			LinkedHashMap map_1 = new LinkedHashMap();
			LinkedHashMap map_2 = new LinkedHashMap();
			if (str_counts.length > 0) {
				for (int i = 0; i < str_counts.length; i++) {
					String bsactname = str_counts[i].getStringValue("业务类型");
					if ("局部".equals(str_counts[i].getStringValue("类型"))) {
						map_1.put(bsactname, str_counts[i].getStringValue("数目"));
					} else {
						map_2.put(bsactname, str_counts[i].getStringValue("数目"));
					}
				}
			}
			HashVO[] dataVO = new HashVO[map_2.size()];
			if (str_counts.length > 0) {
				Iterator it = map_2.entrySet().iterator();

				int index = 0;
				while (it.hasNext()) {
					HashVO vo = new HashVO();
					Entry entry = (Entry) it.next();
					String key_2 = (String) entry.getKey();
					String value_2 = (String) entry.getValue();
					String value_1 = (String) map_1.get(key_2);
					if (value_1 != null) {
						int x_1 = Integer.parseInt(value_1);
						int x_2 = Integer.parseInt(value_2);
						System.out.println(">>" + value_1 + ">" + value_2);
						if (x_2 != 0) {
							vo.setAttributeValue("百分比", "百分比%");
							vo.setAttributeValue("业务类型", key_2);
							vo.setAttributeValue("数目", x_1 * 100 / x_2);
						} else {
							vo.setAttributeValue("百分比", "百分比%");
							vo.setAttributeValue("业务类型", key_2);
							vo.setAttributeValue("数目", 0);
						}
					} else {
						vo.setAttributeValue("百分比", "百分比%");
						vo.setAttributeValue("业务类型", key_2);
						vo.setAttributeValue("数目", 0);
					}
					dataVO[index] = vo;
					index++;
				}
			}
			radar = new BillRadarPanel("各业务条线的风险控制率", dataVO, new String[] { "百分比" }, new String[] { "业务类型" }, "数目", true, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return radar;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == queryPanel_dial) {
			if (!queryPanel_dial.checkValidate()) {
				return;
			}
			mainPanel_dial.removeAll();
			mainPanel_dial.add(getDial(), BorderLayout.CENTER);
			mainPanel_dial.revalidate();
		} else if (e.getSource() == queryPanel_radar) {
			if (!queryPanel_radar.checkValidate()) {
				return;
			}
			mainPanel_radar.removeAll();
			mainPanel_radar.add(getRadar(), BorderLayout.CENTER);
			mainPanel_radar.revalidate();
		}
	}

	// **********************************//
	// 以下代码是5.13注释掉的代码 //
	// 原因：改为风险仪表盘，控制率 //
	// **********************************//
	// public void initialize() {
	// bom = new BillBomPanel("radar"); //
	// this.add(bom); //
	// StringBuffer sb_sql = new StringBuffer("select '风险' ,bsactname 业务活动 ,
	// count(bsactname) 数量 from v_risk_process_file where filestate='3' group
	// by bsactname order by publishdate desc");
	// HashVO[] vos = null;
	// try {
	// vos = UIUtil.getHashVoArrayByDS(null, sb_sql.toString());
	// } catch (WLTRemoteException e) {
	// e.printStackTrace();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// BillRadarPanel radarPanel = new BillRadarPanel("",vos,new
	// String[]{"风险"},new String[]{"业务活动"},"数量",true);
	// this.setLayout(new BorderLayout());
	// this.add(radarPanel);
	// }

}
