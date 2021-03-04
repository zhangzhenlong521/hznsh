package cn.com.pushworld.salary.ui.personalcenter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JLabel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.RefDialog_Date;
import cn.com.pushworld.salary.ui.paymanage.RefDialog_Month;

public class SelfPortfolioQPanel extends AbstractWorkPanel implements ActionListener {
	private static final long serialVersionUID = 8103608997676890710L;
	private WLTButton change_btn;
	private HashMap<Integer, BillListPanel> panel_map;
	private HashMap<Integer, String> sql_map;
	private HashMap<Integer, String> datemap;
	private String oldselectdate = null;
	private WLTTabbedPane wltTabbedPane = new WLTTabbedPane();
	private String olddate;
	private String querytype = null;
	private JLabel info_lable = null;
	private String currdate = null;
	private TBUtil util = new TBUtil();
	private HashMap<String, SelfPortfolioBuildIFC> ifcmap = new HashMap<String, SelfPortfolioBuildIFC>();

	@Override
	public void initialize() {
		try {
			panel_map = new HashMap<Integer, BillListPanel>();
			sql_map = new HashMap<Integer, String>();
			datemap = new HashMap<Integer, String>();
			new HashMap<WLTButton, String>();
			String station = UIUtil.getStringValueByDS(null, "select stationkind from v_sal_personinfo where id = " + ClientEnvironment.getCurrSessionVO().getLoginUserId());
			HashVO configs[] = UIUtil.getHashVoArrayByDS(null, "select * from SAL_SELFPORTFOLIO_CONFIG where post like '%;" + station + ";%' order by seq"); //得到配置
			if (configs.length == 0) {
				this.add(new WLTLabel("您当前岗位没有可实时查询的业务量."));
				return;
			}
			for (int i = 0; i < configs.length; i++) {
				currdate = util.getCurrDate();
				querytype = configs[i].getStringValue("querytype");
				if (querytype != null && querytype.equals("按月查")) {
					currdate = currdate.substring(0, 7);
					olddate = currdate;
				} else {
					olddate = currdate;
				}

				String sql = configs[i].getStringValue("execsql", "").trim().toLowerCase();
				String classpath = configs[i].getStringValue("classpath", "").trim();
				if (!util.isEmpty(sql)) {
					int orderindex = sql.indexOf("order by");
					if (orderindex >= 0 && sql.indexOf(" where ") < 0) {
						sql = sql.replace("order", " where 1=1 order");
					} else if (sql.indexOf(" where ") < 0) {
						sql = sql + " where 1=1 ";
					}
					StringBuffer newsql = new StringBuffer();
					String logtablename = getTableNameAsName(sql, "sal_data_interface_log");
					if (orderindex >= 0) {
						newsql.append(sql.substring(0, orderindex));
					} else {
						newsql.append(sql);
					}
					if (logtablename != null) {
						if (querytype != null && querytype.equals("按月查")) {
							newsql.append(" and " + logtablename + ".datadate like '" + olddate + "%' ");
						} else {
							newsql.append(" and " + logtablename + ".datadate='" + olddate + "' ");
						}
					}
					String persontable = getTableNameAsName(sql, "v_sal_personinfo");
					if (persontable != null) {
						newsql.append(" and " + persontable + ".code='" + ClientEnvironment.getCurrSessionVO().getLoginUserCode() + "' ");
					}
					String converttablename = getTableNameAsName(sql, "sal_convert_ifcdata_log");
					if (converttablename != null) {
						if (querytype != null && querytype.equals("按月查")) {
							newsql.append(" and " + converttablename + ".datadate like '" + olddate + "%' ");
						} else {
							newsql.append(" and " + converttablename + ".datadate='" + olddate + "' ");
						}
					}
					if (orderindex >= 0) {
						newsql.append(" " + sql.substring(orderindex));
					}
					change_btn = new WLTButton("选择日期");
					BillListPanel listpane = new BillListPanel(null, newsql.toString());

					info_lable = new JLabel("当前日期：" + currdate);
					listpane.putClientProperty("label", info_lable);
					listpane.repaintBillListButton();
					listpane.getBillListBtnPanel().getPanel_flow().add(change_btn);
					listpane.getBillListBtnPanel().getPanel_flow().add(info_lable);
					change_btn.addActionListener(this);
					wltTabbedPane.addTab(configs[i].getStringValue("name"), listpane);
					panel_map.put(i, listpane);
					sql_map.put(i, newsql.toString());
					datemap.put(i, olddate);
				} else if (!util.isEmpty(classpath)) {
					Object obj = Class.forName(classpath).newInstance();
					if (obj != null) {
						SelfPortfolioBuildIFC ifc = (SelfPortfolioBuildIFC) obj;
						ifc.initialize(wltTabbedPane, configs[i], i, currdate);
						ifcmap.put(i + "", ifc);
					}
				}
			}
			this.add(wltTabbedPane);
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 通过一个sql自动判断一个表被命名为哪个表
	 */
	public String getTableNameAsName(String sql, String tablename) {
		int logtableindex = sql.indexOf(tablename); //找到日志表位置
		String asname = tablename;
		if (logtableindex >= 0) {
			String afterlogsql = sql.substring(logtableindex + tablename.length()).trim(); // log后的sql
			//select from table1 as t1 left join b as t2 on 
			// table t1 on
			if (afterlogsql.indexOf("as ") == 0) {
				String str = afterlogsql.substring(afterlogsql.indexOf("as ") + 3);
				asname = str.substring(0, str.indexOf(" ")).trim();
				return asname;
			}
			String space_1 = afterlogsql.substring(afterlogsql.indexOf(" ")).trim();
			if (space_1 != null && space_1.indexOf("as ") == 0) {
				asname = space_1.substring(space_1.indexOf(1));
				return asname;
			} else if (space_1 != null && space_1.indexOf(" ") == 0 && space_1.length() > 0) {
				asname = space_1;
				return asname;
			} else if ("where_left_inner_right_on".contains(space_1.substring(0, space_1.indexOf(" ")))) {
				asname = afterlogsql.substring(0, afterlogsql.indexOf(" "));
				return asname;
			} else if (space_1.indexOf("on") == 0) {
				asname = afterlogsql.substring(0, afterlogsql.indexOf(" "));
				return asname;
			}
		} else {
			return null;
		}
		return asname;
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		int index = wltTabbedPane.getSelectedIndex();
		try {
			if (querytype != null && querytype.equals("按月查")) {
				RefDialog_Month month = new RefDialog_Month(wltTabbedPane, "请选择一个日期", new RefItemVO(oldselectdate, oldselectdate, oldselectdate), null);
				month.initialize();
				month.setVisible(true);
				if (month.getCloseType() == 1) {
					RefItemVO select = month.getReturnRefItemVO();
					if (select != null) {
						oldselectdate = select.getId();
					}
				}

			} else {
				RefDialog_Date date = new RefDialog_Date(wltTabbedPane, "请选择一个日期", new RefItemVO(oldselectdate, oldselectdate, oldselectdate), null);
				date.initialize();
				date.setVisible(true);
				if (date.getCloseType() == 1) {
					RefItemVO select = date.getReturnRefItemVO();
					if (select != null) {
						oldselectdate = select.getId();
					}
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		dorepaintPanel(index, oldselectdate);
	}

	private void dorepaintPanel(int index, String datetime) {
		if (ifcmap.containsKey(index + "")) {
			ifcmap.get(index + "").onQuery(datetime);
		} else {
			String oldsql = sql_map.get(index);
			String newsql = null;
			String oldtime = datemap.get(index);
			if (querytype != null && querytype.equals("按月查")) {
				datetime = datetime.substring(0, 7);
				newsql = oldsql.replaceAll("like '" + oldtime + "%'", "like '" + datetime + "%'");
			} else {
				newsql = oldsql.replaceAll(".datadate='" + oldtime + "'", ".datadate='" + datetime + "'");
			}
			((JLabel) panel_map.get(index).getClientProperty("label")).setText("当前日期：" + datetime);
			panel_map.get(index).QueryData(newsql);
			sql_map.put(index, newsql);
			datemap.put(index, datetime);
		}
	}

}
