package com.pushworld.ipushgrc.ui.HR.p060;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.LinkedHashMap;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_ComboBox;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;

public class JIBenSalaryCountWorkPanel extends AbstractWorkPanel implements ActionListener {

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
//	private BillQueryPanel billQueryPanel = null;
//	private BillCellPanel billCellPanel = null;
//	private WLTButton btn_export_excel, btn_export_html;
//	private String[] title = new String[] { "序号", "姓名", "主部门", "岗位归类" };
//	private String[] field = new String[] { "序号", "username", "corpname", "stationkind" };
//	private String[] field_len = new String[] { "序号", "80", "150", "100" };
//	private String[] types = null;
//	private String[] types_id = null;
//	private String reportname = "员工工资汇总";
//	private String[] v_count_type = new String[] { "合计", "平均" };
//	private String[] h_count_type = new String[] { "合计", "平均" };
//
//	public void initialize() {
//		this.setLayout(new BorderLayout());
//		billQueryPanel = new BillQueryPanel("REPORTQUERY_ZZL_CODE1");
//		billQueryPanel.addBillQuickActionListener(this);
//
//		QueryCPanel_UIRefPanel month_endRef = (QueryCPanel_UIRefPanel) billQueryPanel.getCompentByKey("month_end");
//		String checkDate = new SalaryUIUtil().getCheckDate();
//		month_endRef.setValue(checkDate);
//
//		QueryCPanel_UIRefPanel month_startRef = (QueryCPanel_UIRefPanel) billQueryPanel.getCompentByKey("month_start");
//		if (checkDate != null && checkDate.length() > 4) {
//			month_startRef.setValue(checkDate.substring(0, 4) + "-01");
//		}
//
//		QueryCPanel_ComboBox counttypeRef = (QueryCPanel_ComboBox) billQueryPanel.getCompentByKey("counttype");
//		counttypeRef.setValue("按员工");
//
//		billCellPanel = new BillCellPanel();
//		billCellPanel.setToolBarVisiable(false); //隐藏工具栏
//		billCellPanel.setAllowShowPopMenu(false);
//		billCellPanel.setEditable(false);
//
//		btn_export_excel = new WLTButton("导出Excel", UIUtil.getImage("icon_xls.gif"));
//		btn_export_excel.addActionListener(this);
//
//		btn_export_html = new WLTButton("导出Html", UIUtil.getImage("zt_064.gif"));
//		btn_export_html.addActionListener(this);
//
//		JPanel panel_btn = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 2));
//		panel_btn.add(btn_export_excel);
//		panel_btn.add(btn_export_html);
//
//		JPanel panel = new JPanel(new BorderLayout());
//		panel.add(panel_btn, BorderLayout.NORTH);
//		panel.add(billCellPanel, BorderLayout.CENTER);
//
//		this.add(billQueryPanel, BorderLayout.NORTH);
//		this.add(panel, BorderLayout.CENTER);
//	}
//
//	public void actionPerformed(ActionEvent e) {
//		try {
//			if (e.getSource() == billQueryPanel) {
//				String counttype = billQueryPanel.getRealValueAt("counttype");
//				String salaryitems = billQueryPanel.getRealValueAt("salaryitems");
//				String month_start = billQueryPanel.getRealValueAt("month_start");
//				String month_end = billQueryPanel.getRealValueAt("month_end");
//
//				if (counttype == null || counttype.equals("")) {
//					MessageBox.show(this, "请选择统计类型！");
//					return;
//				}
//
//				if (salaryitems == null || salaryitems.equals("")) {
//					MessageBox.show(this, "请选择工资条目！");
//					return;
//				}
//
//				if (month_start == null || month_start.equals("") || month_end == null || month_end.equals("")) {
//					MessageBox.show(this, "请选择开始月份与结束月份！");
//					return;
//				}
//
//				String account_set = new TBUtil().getSysOptionStringValue("综合报表基本工资统计帐套", "普通账套");
//				HashVO[] hvs_log = UIUtil.getHashVoArrayByDS(null, "select id, monthly from sal_salarybill where " + "sal_account_setid in (select id from sal_account_set where name in('" + account_set.replace(";", "','") + "')) " + "and monthly>='" + month_start + "' and monthly<='" + month_end + "' order by monthly");
//
//				if (hvs_log == null || !(hvs_log.length > 0)) {
//					MessageBox.show(this, "没有查询到结果！");
//					return;
//				}
//
//				HashVO[] hvs_items = UIUtil.getHashVoArrayByDS(null, "select factorid, viewname from sal_account_factor " + "where accountid in(select id from sal_account_set where name in('" + account_set.replace(";", "','") + "')) " + "and factorid in('" + salaryitems.replace(";", "','") + "') order by seq");
//
//				if (hvs_items == null || !(hvs_items.length > 0)) {
//					MessageBox.show(this, "没有查询到结果！");
//					return;
//				}
//
//				LinkedHashMap hm = new LinkedHashMap();
//				for (int i = 0; i < hvs_log.length; i++) {
//					String id = hvs_log[i].getStringValue("id", "");
//					String monthly = hvs_log[i].getStringValue("monthly", "");
//					if (hm.containsKey(monthly)) {
//						hm.put(monthly, id + "," + hm.get(monthly));
//					} else {
//						hm.put(monthly, id);
//					}
//				}
//
//				String[] str_keys = (String[]) hm.keySet().toArray(new String[0]);
//				String[] checkdates = new String[str_keys.length];
//				String[] checkids = new String[str_keys.length];
//				for (int i = 0; i < str_keys.length; i++) {
//					checkids[i] = (String) hm.get(str_keys[i]);
//					checkdates[i] = str_keys[i];
//				}
//
//				types = new String[hvs_items.length];
//				types_id = new String[hvs_items.length];
//				for (int i = 0; i < hvs_items.length; i++) {
//					types_id[i] = hvs_items[i].getStringValue("factorid", "");
//					types[i] = hvs_items[i].getStringValue("viewname", "");
//				}
//
//				SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
//				HashVO[] hvs = null;
//				if (counttype.equals("按员工")) {
//					hvs = ifc.getPersonSalary(checkids, types_id);
//				} else if (counttype.equals("按部门")) {
//					title = new String[] { "序号", "部门名称" };
//					field = new String[] { "序号", "corpname" };
//					field_len = new String[] { "序号", "130" };
//					hvs = ifc.getDeptSalary(checkids, types_id);
//				} else if (counttype.equals("按岗位归类")) {
//					title = new String[] { "序号", "岗位归类" };
//					field = new String[] { "序号", "stationkind" };
//					field_len = new String[] { "序号", "100" };
//					hvs = ifc.getPostSalary(checkids, types_id);
//				}
//
//				if (hvs == null || !(hvs.length > 0)) {
//					MessageBox.show(this, "没有查询到结果！");
//					return;
//				}
//
//				billCellPanel.setIfSetRowHeight(true);
//				billCellPanel.loadBillCellData(getBillCellItemVOs(hvs, checkdates));
//				billCellPanel.setEditable(false);
//				if (billCellPanel.getRowCount() > 2) {
//					billCellPanel.setLockedCell(2, 1); //锁定表头
//				}
//			} else if (e.getSource() == btn_export_excel) {
//				billCellPanel.exportExcel(reportname);
//			} else if (e.getSource() == btn_export_html) {
//				billCellPanel.exportHtml(reportname);
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//	}
//
//	private BillCellVO getBillCellItemVOs(HashVO[] _hashVOs, String[] checkdates) {
//		int title_len = title.length;
//
//		BillCellVO cellVO = new BillCellVO();
//		int li_rows = 2 + h_count_type.length; //表头+表尾
//		if (_hashVOs != null && _hashVOs.length > 0) {
//			li_rows = _hashVOs.length + 2 + h_count_type.length; //表头+表尾
//		}
//		int li_cols = title_len + (checkdates.length + v_count_type.length) * types.length; //序号+列数
//		cellVO.setRowlength(li_rows);
//		cellVO.setCollength(li_cols);
//
//		BillCellItemVO[][] cellItemVOs = new BillCellItemVO[li_rows][li_cols];
//		setBilCellVOStyle(cellItemVOs); //样式
//		setColtitle(cellItemVOs, checkdates); //列头
//
//		if (_hashVOs == null || !(_hashVOs.length > 0)) {
//			cellVO.setCellItemVOs(cellItemVOs);
//			return cellVO;
//		}
//
//		for (int m = 0; m < _hashVOs.length; m++) {
//			HashVO hvo = _hashVOs[m];
//			cellItemVOs[m + 2][0].setCellvalue("" + (m + 1));
//			for (int i = 1; i < field.length; i++) {
//				cellItemVOs[m + 2][i].setCellvalue(hvo.getStringValue(field[i], ""));
//			}
//		}
//
//		if (h_count_type.length > 0) {
//			for (int k = 0; k < h_count_type.length; k++) {
//				cellItemVOs[_hashVOs.length + 2 + k][title_len - 1].setCellvalue(h_count_type[k]);
//			}
//		}
//
//		for (int j = 0; j < types.length; j++) {
//			String[][] hj_h = new String[checkdates.length][_hashVOs.length];
//			for (int m = 0; m < _hashVOs.length; m++) {
//				HashVO hvo = _hashVOs[m];
//
//				String[] hj_a = new String[checkdates.length];
//				for (int i = 0; i < checkdates.length; i++) {
//					hj_a[i] = hvo.getStringValue("result_a" + i + "_" + j, "");
//					hj_h[i][m] = hj_a[i];
//					cellItemVOs[m + 2][title_len + i + (checkdates.length + v_count_type.length) * j].setCellvalue(hj_a[i]);
//				}
//
//				if (v_count_type.length > 0) {
//					for (int k = 0; k < v_count_type.length; k++) {
//						if (v_count_type[k].equals("合计")) {
//							cellItemVOs[m + 2][title_len + checkdates.length + k + (checkdates.length + v_count_type.length) * j].setCellvalue(getHJ(hj_a, 2));
//							cellItemVOs[m + 2][title_len + checkdates.length + k + (checkdates.length + v_count_type.length) * j].setBackground("232,255,255");
//						}
//						if (v_count_type[k].equals("平均")) {
//							cellItemVOs[m + 2][title_len + checkdates.length + k + (checkdates.length + v_count_type.length) * j].setCellvalue(getPJ(hj_a, 2));
//							cellItemVOs[m + 2][title_len + checkdates.length + k + (checkdates.length + v_count_type.length) * j].setBackground("232,255,255");
//						}
//					}
//				}
//			}
//
//			if (h_count_type.length > 0) {
//				String[] hj_aa = new String[checkdates.length];
//				for (int i = 0; i < checkdates.length; i++) {
//					for (int k = 0; k < h_count_type.length; k++) {
//						if (h_count_type[k].equals("合计")) {
//							hj_aa[i] = getHJ(hj_h[i], 2);
//							cellItemVOs[_hashVOs.length + 2 + k][title_len + i + (checkdates.length + v_count_type.length) * j].setCellvalue(hj_aa[i]);
//						}
//						if (h_count_type[k].equals("平均")) {
//							cellItemVOs[_hashVOs.length + 2 + k][title_len + i + (checkdates.length + v_count_type.length) * j].setCellvalue(getPJ(hj_h[i], 2));
//						}
//					}
//				}
//
//				for (int k = 0; k < h_count_type.length; k++) {
//					if (h_count_type[k].equals("合计")) {
//						for (int kk = 0; kk < v_count_type.length; kk++) {
//							if (v_count_type[kk].equals("合计")) {
//								cellItemVOs[_hashVOs.length + 2 + k][title_len + checkdates.length + kk + (checkdates.length + v_count_type.length) * j].setCellvalue(getHJ(hj_aa, 2));
//							}
//						}
//					}
//				}
//			}
//		}
//
//		cellVO.setCellItemVOs(cellItemVOs);
//		return cellVO;
//	}
//
//	public String getHJ(String[] strs, int l) {
//		BigDecimal sum = new BigDecimal("0");
//		int mark = 0;
//		for (int i = 0; i < strs.length; i++) {
//			if (strs[i].equals("")) {
//				continue;
//			}
//
//			try {
//				sum = sum.add(new BigDecimal(strs[i]));
//				mark++;
//			} catch (Exception e) {
//				WLTLogger.getLogger(JIBenSalaryCountWorkPanel.class).error("", e);
//			}
//		}
//
//		if (mark == 0) {
//			return "--";
//		}
//
//		return sum.setScale(l, BigDecimal.ROUND_HALF_UP).toString();
//	}
//
//	public String getPJ(String[] strs, int l) {
//		BigDecimal sum = new BigDecimal("0");
//		int mark = 0;
//		for (int i = 0; i < strs.length; i++) {
//			if (strs[i].equals("")) {
//				continue;
//			}
//
//			try {
//				sum = sum.add(new BigDecimal(strs[i]));
//				mark++;
//			} catch (Exception e) {
//				WLTLogger.getLogger(JIBenSalaryCountWorkPanel.class).error("", e);
//			}
//		}
//
//		if (mark == 0) {
//			return "--";
//		}
//
//		return sum.divide(new BigDecimal(mark), l, BigDecimal.ROUND_HALF_UP).toString();
//	}
//
//	//BillCellVO 样式 长度、宽度等
//	private void setBilCellVOStyle(BillCellItemVO[][] cellItemVOs) {
//		for (int i = 0; i < cellItemVOs.length; i++) {
//			for (int j = 0; j < cellItemVOs[i].length; j++) {
//				cellItemVOs[i][j] = new BillCellItemVO();
//				cellItemVOs[i][j].setCellkey(i + "," + j);
//				cellItemVOs[i][j].setCelltype("TEXTAREA");
//				cellItemVOs[i][j].setCellrow(i);
//				cellItemVOs[i][j].setCellcol(j);
//				cellItemVOs[i][j].setHalign(2);
//				cellItemVOs[i][j].setCellvalue("");
//				cellItemVOs[i][j].setSpan("1,1");
//				cellItemVOs[i][j].setRowheight("" + 25);
//				cellItemVOs[i][j].setFonttype("宋体");
//				cellItemVOs[i][j].setFontsize("12");
//				cellItemVOs[i][j].setFontstyle(Font.PLAIN + "");
//
//				if (j == 0) {
//					cellItemVOs[i][j].setColwidth("35"); //设置序号宽度
//					if (i > 1 && i < cellItemVOs.length) {
//						cellItemVOs[i][j].setBackground("232,255,255"); //设置序号颜色
//					}
//				} else if (j > 0 && j < field_len.length) {
//					cellItemVOs[i][j].setColwidth(field_len[j]);
//				} else {
//					cellItemVOs[i][j].setColwidth("80"); //设置其他列的宽度
//				}
//
//				if (i == 0 || i == 1) {
//					cellItemVOs[i][j].setHalign(2); //设置表头列字段居中
//					cellItemVOs[i][j].setBackground("232,255,255"); //设置表头列字段颜色
//				} else {
//					cellItemVOs[i][j].setHalign(1);//1表示水平左对齐 
//				}
//
//				if (h_count_type.length > 0) {
//					for (int k = 0; k < h_count_type.length; k++) {
//						if (i == cellItemVOs.length - (k + 1)) {
//							cellItemVOs[i][j].setBackground("232,255,255");
//						}
//					}
//				}
//
//				cellItemVOs[i][j].setValign(2); //2表示垂直居中 
//			}
//		}
//	}
//
//	//列头信息 
//	private void setColtitle(BillCellItemVO[][] cellItemVOs, String[] checkdates) {
//		int title_len = title.length;
//		for (int i = 0; i < title_len; i++) {
//			cellItemVOs[0][i].setCellvalue(title[i]);
//			cellItemVOs[0][i].setSpan("2,1");
//		}
//
//		for (int i = 0; i < types.length; i++) {
//			cellItemVOs[0][title_len + (checkdates.length + v_count_type.length) * i].setCellvalue(types[i]);
//			cellItemVOs[0][title_len + (checkdates.length + v_count_type.length) * i].setSpan("1," + (checkdates.length + v_count_type.length));
//		}
//
//		for (int i = 0; i < checkdates.length; i++) {
//			for (int j = 0; j < types.length; j++) {
//				cellItemVOs[1][title_len + i + (checkdates.length + v_count_type.length) * j].setCellvalue(checkdates[i]);
//			}
//		}
//
//		if (v_count_type.length > 0) {
//			for (int k = 0; k < v_count_type.length; k++) {
//				for (int i = 0; i < types.length; i++) {
//					cellItemVOs[1][title_len + checkdates.length + (checkdates.length + v_count_type.length) * i + k].setCellvalue(v_count_type[k]);
//				}
//			}
//		}
//	}

}

