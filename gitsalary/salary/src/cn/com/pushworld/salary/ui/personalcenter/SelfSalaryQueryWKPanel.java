package cn.com.pushworld.salary.ui.personalcenter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.ui.common.*;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;
import cn.com.infostrategy.ui.report.BillCellHtmlHrefEvent;
import cn.com.infostrategy.ui.report.BillCellHtmlHrefListener;
import cn.com.infostrategy.ui.report.style2.DefaultStyleReportPanel_2;
import cn.com.pushworld.salary.ui.SalaryUIUtil;
import cn.com.pushworld.salary.ui.personalcenter.p070.WGSelfSalaryQueryWKPanel;

/**
 * 个人工资自助查询
 * @author Administrator
 *
 */
public class SelfSalaryQueryWKPanel extends AbstractWorkPanel implements ActionListener, BillCellHtmlHrefListener {
	private static final long serialVersionUID = 1L;
	private DefaultStyleReportPanel_2 dr = null;
	private BillQueryPanel bq = null;
	private HashMap detailid_desc = null;
	private String exportFileName = "工资单_";
	private WLTTabbedPane pane = null;//zzl[2020-5-19] 增加网格工资单的页签

	public void initialize() {
		Boolean wgflg= new TBUtil().getSysOptionBooleanValue("是否启动网格指标计算模式", false);//zzl[2020-5-11]
		dr = new DefaultStyleReportPanel_2("SAL_SALARYBILL_CODE1", "");
		bq = dr.getBillQueryPanel();
		//设置日期默认值为当前考核日期  Gwang 2013-08-21
		QueryCPanel_UIRefPanel dateRef = (QueryCPanel_UIRefPanel) bq.getCompentByKey("monthly");
		String checkDate = new SalaryUIUtil().getCheckDate();
		dateRef.setValue(checkDate);

		//设置导出文件名称 Gwang 2013-08-22
		String loginUserName = ClientEnvironment.getInstance().getLoginUserName();
		exportFileName += loginUserName;
		dr.setReportExpName(exportFileName);
		//zzl[2020-5-19] 得到登录人Code 根据code得到网格id
		String loginCorpdistinct = ClientEnvironment.getInstance().getLoginUserDeptCorpdistinct();
		dr.getBillCellPanel().setEditable(false);
		dr.getBillCellPanel().addBillCellHtmlHrefListener(this);
		JLabel info = new JLabel("点击数字可查看计算过程");
		info.setOpaque(false);
		info.setForeground(Color.RED);
		dr.getPanel_btn().add(info);
		bq.addBillQuickActionListener(this);
//		if( ClientEnvironment.getInstance().getLoginUserName().equals("admin")){
//			WGSelfSalaryQueryWKPanel wgPanel=new WGSelfSalaryQueryWKPanel();
//			pane = new WLTTabbedPane(); //
//			pane.addTab("绩效工资查询", UIUtil.getImage("32_32_05.gif"),
//					dr); //
//			pane.addTab("网格工资查询", UIUtil.getImage("office_070.gif"),wgPanel.getDr()); //
//			this.add(pane, BorderLayout.CENTER);
//		}else if(loginCorpdistinct==null || loginCorpdistinct.equals("")){
//			this.add(dr, BorderLayout.CENTER);
//		}else if(wgflg && loginCorpdistinct.contains("涉农")){
//			WGSelfSalaryQueryWKPanel wgPanel=new WGSelfSalaryQueryWKPanel();
//			pane = new WLTTabbedPane(); //
//			pane.addTab("绩效工资查询", UIUtil.getImage("32_32_05.gif"),dr); //
//			pane.addTab("网格工资查询", UIUtil.getImage("office_070.gif"),wgPanel.getDr()); //
//			this.add(pane, BorderLayout.CENTER);
//		}else{
//			this.add(dr, BorderLayout.CENTER);
//		}
		WGSelfSalaryQueryWKPanel wgPanel=new WGSelfSalaryQueryWKPanel();
		pane = new WLTTabbedPane(); //
		pane.addTab("绩效工资查询", UIUtil.getImage("32_32_05.gif"),
				dr); //
		pane.addTab("网格工资查询", UIUtil.getImage("office_070.gif"),wgPanel.getDr()); //
		this.add(pane, BorderLayout.CENTER);

	}

	public void actionPerformed(ActionEvent arg0) {
		final HashMap map_condition = bq.getQuickQueryConditionAsMap();
		if (map_condition == null) {
			return;
		}
		new SplashWindow(bq, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				try {
					BillCellVO vo = getCellVO(map_condition);
					if (vo != null && vo.getCellItemVOs() != null && vo.getCellItemVOs().length > 0) {
						dr.getBillCellPanel().loadBillCellData(vo);
					}
					if (map_condition == null || map_condition.get("monthly") == null) {
						dr.setReportExpName(exportFileName + "_全部");
					} else {
						dr.setReportExpName(exportFileName + "_" + map_condition.get("monthly").toString());
					}
					dr.getBillCellPanel().setEditable(false);
				} catch (Exception ex) {
					MessageBox.showException(bq, ex);
				}
			}
		});
	}

	public BillCellVO getCellVO(HashMap condition) {
		BillCellVO cell = new BillCellVO();
		if (condition != null && condition.containsKey("monthly")) {
			String monthly = " 1=1 ";
			if (condition.get("monthly") != null && !"".equals(condition.get("monthly").toString().trim())) {
				monthly = " b.monthly='" + condition.get("monthly").toString().trim() + "' ";
			}
			try {
				HashVO[] vos = UIUtil.getHashVoArrayByDS(null, "select d.*,b.monthly,b.name billname from sal_salarybill_detail d left join sal_salarybill b on d.salarybillid=b.id where d.userid=" + ClientEnvironment.getCurrLoginUserVO().getId() + " and " + monthly + " and d.viewname is not null  and d.factorid is not null and b.state='已开放'  order by b.monthly,d.salarybillid, d.seq");
				if (vos != null && vos.length > 0) {
					LinkedHashMap<String, LinkedHashMap> map = new LinkedHashMap<String, LinkedHashMap>();
					HashMap id_name = new HashMap();
					HashMap salaryid_name = new HashMap();
					detailid_desc = new HashMap();
					int maxlength = 0;
					int j1 = 0;
					for (int i = 0; i < vos.length; i++) {
						id_name.put(vos[i].getStringValue("id"), vos[i].getStringValue("viewname").trim());
						detailid_desc.put(vos[i].getStringValue("id"), vos[i].getStringValue("computedesc"));
						salaryid_name.put(vos[i].getStringValue("salarybillid"), "" + vos[i].getStringValue("billname") + "");
						if (map.containsKey(vos[i].getStringValue("salarybillid"))) {
							j1 = j1 + 1;
							if (j1 > maxlength) {
								maxlength = j1;
							}
							((LinkedHashMap) map.get(vos[i].getStringValue("salarybillid"))).put(vos[i].getStringValue("id"), vos[i].getStringValue("factorvalue"));
						} else {
							LinkedHashMap item = new LinkedHashMap();
							item.put(vos[i].getStringValue("id"), vos[i].getStringValue("factorvalue"));
							map.put(vos[i].getStringValue("salarybillid"), item);
							if (j1 > maxlength) {
								maxlength = j1;
							}
							j1 = 1;
						}
					}
					BillCellItemVO[][] items = new BillCellItemVO[map.size() * 4 - 1][maxlength];
					String[] allkeys = map.keySet().toArray(new String[0]);
					for (int i = 0; i < map.size(); i++) {
						LinkedHashMap column_calue = (LinkedHashMap) map.get(allkeys[i]);
						String[] allcolumns = (String[]) column_calue.keySet().toArray(new String[0]);
						int rowbg = 0;
						for (int p = 0; p < maxlength; p++) {
							rowbg = i * 4;
							if (p < allcolumns.length) {
								items[rowbg][p] = new BillCellItemVO();
								items[rowbg][p].setSpan("1," + maxlength);
								items[rowbg][p].setCellkey(salaryid_name.get(allkeys[i]) + "");
								items[rowbg][p].setCellvalue(salaryid_name.get(allkeys[i]) + "");
								items[rowbg][p].setBackground("191,213,255");
								items[rowbg][p].setFonttype("新宋体");
								items[rowbg][p].setFontsize("12");
								items[rowbg][p].setFontstyle("1");
								items[rowbg + 1][p] = new BillCellItemVO();
								items[rowbg + 1][p].setCellkey(id_name.get(allcolumns[p]) + "");
								items[rowbg + 1][p].setCellvalue(id_name.get(allcolumns[p]) + "");
								items[rowbg + 1][p].setBackground("191,213,255");
								items[rowbg + 1][p].setFonttype("新宋体");
								items[rowbg + 1][p].setFontsize("12");
								items[rowbg + 1][p].setFontstyle("1");
								items[rowbg + 1][p].setBackground("191,213,255");
								items[rowbg + 2][p] = new BillCellItemVO();
								items[rowbg + 2][p].setCellkey(allcolumns[p]);
								items[rowbg + 2][p].setCellvalue(column_calue.get(allcolumns[p]) + "");
								items[rowbg + 2][p].setBackground("191,213,255");
								items[rowbg + 2][p].setIshtmlhref("Y");
								items[rowbg + 2][p].setCelldesc("点击查看计算明细");
								items[rowbg + 2][p].setCellhelp("点击查看计算明细");
								if (i < map.size() - 1) {
									items[rowbg + 3][p] = new BillCellItemVO();
									items[rowbg + 3][p].setSpan("1," + allcolumns.length);
								}
							} else {
								items[rowbg][p] = new BillCellItemVO();
								items[rowbg][p].setBackground("191,213,255");
								items[rowbg + 1][p] = new BillCellItemVO();
								items[rowbg + 1][p].setBackground("191,213,255");
								items[rowbg + 2][p] = new BillCellItemVO();
								items[rowbg + 2][p].setBackground("191,213,255");
								if (i < map.size() - 1) {
									items[rowbg + 3][p] = new BillCellItemVO();
								}
							}
						}
					}
					// 进行表格长度调整
					FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(LookAndFeel.font);
					int li_allowMaxColWidth = 175;
					for (int j = 0; j < items[0].length; j++) {
						int li_maxwidth = 0;
						String str_cellValue = null;
						for (int i = 0; i < items.length; i++) {
							str_cellValue = items[i][j].getCellvalue();
							if (str_cellValue == null || "null".equals(str_cellValue)) {
								items[i][j].setCellvalue("");
							}
							int column = 1;
							if (items[i][j] != null && items[i][j].getSpan() != null) {
								column = Integer.parseInt(items[i][j].getSpan().split(",")[1]);
							}
							if (str_cellValue != null && !str_cellValue.trim().equals("") && column <= 1) {
								int li_width = SwingUtilities.computeStringWidth(fm, str_cellValue);
								if (li_width > li_maxwidth) {
									li_maxwidth = li_width;
								}
							}
						}
						li_maxwidth = li_maxwidth + 13;
						if (li_maxwidth > li_allowMaxColWidth) {
							li_maxwidth = li_allowMaxColWidth;
						}
						for (int i = 0; i < items.length; i++) {
							str_cellValue = items[i][j].getCellvalue();
							if (str_cellValue != null && !str_cellValue.trim().equals("")) {
								//								int li_width = SwingUtilities.computeStringWidth(fm, str_cellValue);
								//								if (li_width > 0) {
								//									int li_length = (li_width / li_maxwidth) + 1;
								//									int li_itemRowHeight = li_length * 17 + 5;
								//									if (i == 1) {
								//										if (li_itemRowHeight > 35) {
								//											items[i][j].setRowheight("" + li_itemRowHeight);
								//										} else {
								//											items[i][j].setRowheight("35");
								//										}
								//									} else {
								//										items[i][j].setRowheight("" + li_itemRowHeight);
								//									}
								//									items[i][j].setColwidth("" + li_maxwidth);
								//								}
								items[i][j].setColwidth("" + li_maxwidth);
							}
						}
					}
					cell.setCellItemVOs(items);
					cell.setRowlength(items.length);
					cell.setCollength(maxlength);
				} else {
					cell.setRowlength(1);
					cell.setCollength(1);
					BillCellItemVO[][] items = new BillCellItemVO[1][1];
					items[0][0] = new BillCellItemVO();
					items[0][0].setCellvalue("未查询到相应信息");
					items[0][0].setBackground("191,213,255");
					items[0][0].setFonttype("新宋体");
					items[0][0].setFontsize("12");
					items[0][0].setFontstyle("1");
					items[0][0].setColwidth("300");
					cell.setCellItemVOs(items);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return cell;
	}

	public void onBillCellHtmlHrefClicked(BillCellHtmlHrefEvent event) {
		onWatchComputeDesc(event.getCellItemKey());
	}

	public void onWatchComputeDesc(String key) {
		if (detailid_desc.containsKey(key)) {
			MessageBox.show(this, detailid_desc.get(key) == null ? "无" : detailid_desc.get(key));
		}
	}
}
