package cn.com.pushworld.salary.ui.report;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.TileObserver;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_ComboBox;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.pushworld.salary.to.SalaryReportVO;
import cn.com.pushworld.salary.to.SalaryTBUtil;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

public class PersonGqjlReportWKPanel extends AbstractWorkPanel implements ActionListener {
	private BillQueryPanel billQueryPanel = null;
	private BillCellPanel billCellPanel = null;
	private WLTButton btn_export_excel, btn_export_html;
	private String[] title = new String[] {};
	private String[] field = new String[] {};
	private String[] field_len = new String[] {};
	private String[] types = new String[] {};
	private String[] types_nocou = new String[] {};
	private String[] types_field = new String[] {};
	private String[] types_field_nocou = new String[] {};
	private String reportname = "股权激励基金";
	private String[] v_count_type = new String[] {}; //"合计","平均"
	private String[] h_count_type = new String[] {}; //"合计","平均"
	private int titlerows = 1;
	private String excel_code = "A";

	private String condition = null;

	public PersonGqjlReportWKPanel(String _reportname) {
		this.reportname = _reportname;
	}

	//设置后面的sql
	public void setCondition(String _condition) {
		condition = _condition;
	}

	public void initialize() {
		this.setLayout(new BorderLayout());
		billQueryPanel = new BillQueryPanel("REPORTQUERY_CODE11");
		billQueryPanel.addBillQuickActionListener(this);

		String checkDate = new SalaryUIUtil().getCheckDate();
		String year = "";
		if (checkDate != null && checkDate.length() > 4) {
			year = checkDate.substring(0, 4);
		}

		QueryCPanel_ComboBox year_startRef = (QueryCPanel_ComboBox) billQueryPanel.getCompentByKey("year_start");
		year_startRef.setValue(year);

		QueryCPanel_ComboBox year_endRef = (QueryCPanel_ComboBox) billQueryPanel.getCompentByKey("year_end");
		year_endRef.setValue(year);

		billCellPanel = new BillCellPanel();
		billCellPanel.setToolBarVisiable(false); //隐藏工具栏
		billCellPanel.setAllowShowPopMenu(false);
		billCellPanel.setEditable(false);

		btn_export_excel = new WLTButton("导出Excel", UIUtil.getImage("icon_xls.gif"));
		btn_export_excel.addActionListener(this);

		btn_export_html = new WLTButton("导出Html", UIUtil.getImage("zt_064.gif"));
		btn_export_html.addActionListener(this);

		JPanel panel_btn = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 2));
		panel_btn.add(btn_export_excel);
		panel_btn.add(btn_export_html);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(panel_btn, BorderLayout.NORTH);
		panel.add(billCellPanel, BorderLayout.CENTER);

		this.add(billQueryPanel, BorderLayout.NORTH);
		this.add(panel, BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == billQueryPanel) {
				String year_start = billQueryPanel.getRealValueAt("year_start");
				String year_end = billQueryPanel.getRealValueAt("year_end");

				if (year_start == null || year_start.equals("") || year_end == null || year_end.equals("")) {
					MessageBox.show(this, "请选择开始年份与结束年份！");
					return;
				}

				String[] tablename = UIUtil.getStringArrayFirstColByDS(null, "select tablename from excel_tab where excelname='" + reportname + "'");

				if (!(tablename != null && tablename.length > 0)) {
					MessageBox.show(this, "没有查询到结果！");
					return;
				}

				String[] checkdates = new String[] {};

				String sql = "select distinct year from " + tablename[0] + " where year>='" + year_start + "' and year<='" + year_end + "' order by year";
				HashVO[] hvs_year = UIUtil.getHashVoArrayByDS(null, sql);

				HashVO excel_self_itemname[] = UIUtil.getHashVoArrayByDS(null, "select * from " + tablename[0] + " where year>='" + year_start + "' and year<='" + year_end + "' and A='姓名' order by year");
				if (hvs_year == null || !(hvs_year.length > 0)) {
					MessageBox.show(this, "没有查询到结果！");
					return;
				}
				List title_list = new ArrayList();
				List field_list = new ArrayList();
				List field_len_list = new ArrayList();
				title_list.add("序号");
				field_list.add("序号");
				field_len_list.add("50");

				title_list.add("姓名");
				field_list.add("姓名");
				field_len_list.add("100");

				title_list.add("主部门");
				field_list.add("deptname");
				field_len_list.add("150");

				title_list.add("岗位归类");
				field_list.add("stationkind");
				field_len_list.add("100");

				int[] years = new int[hvs_year.length];
				SalaryTBUtil stbutil = new SalaryTBUtil();
				String last_col = getMenuConfMapValueAsStr("股权激励报表显示的最后一列"); //默认为空,如果为F,那么F以后的列都不显示。不配置的话，就原样子显示
				String last_col_if_sum = getMenuConfMapValueAsStr("股权激励报表最后一列是否求和", "N"); //默认为N
				if (excel_self_itemname.length > 0) {
					for (int i = 0; i < excel_self_itemname.length; i++) {
						String year = excel_self_itemname[i].getStringValue("year");
						for (int j = 2; j <= 9; j++) {
							String itemcol = stbutil.convertIntColToEn(j,false);
							String itemvalue = excel_self_itemname[i].getStringValue(itemcol);
							if (!TBUtil.getTBUtil().isEmpty(itemvalue)) {
								if (itemcol.equalsIgnoreCase(last_col)) {
									if ((i == excel_self_itemname.length - 1)) {
										title_list.add(itemvalue);
										field_list.add("xcgq");
										field_len_list.add("100");
										break;
									} else {
										continue;
									}
								}
								title_list.add(itemvalue);
								field_list.add(year + "_" + itemcol);
								field_len_list.add("100");
							}
						}
					}
				}

				for (int i = 0; i < hvs_year.length; i++) {
					int year = hvs_year[i].getIntegerValue("year", 0);
					years[i] = year;
				}
				title = (String[]) title_list.toArray(new String[0]);
				field = (String[]) field_list.toArray(new String[0]);
				field_len = (String[]) field_len_list.toArray(new String[0]);
				SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
				HashMap parm = new HashMap();
				parm.put("menu", getMenuConfMap());
				parm.put("condition", condition);
				HashVO[] hvs = ifc.getPersonGqjl(tablename[0], years, excel_code, parm);
				billCellPanel.setIfSetRowHeight(true);
				billCellPanel.loadBillCellData(getBillCellItemVOs(hvs, checkdates));
				billCellPanel.setEditable(false);
				if (billCellPanel.getRowCount() > 1) {
					billCellPanel.setLockedCell(titlerows, 1); //锁定表头
				}
			} else if (e.getSource() == btn_export_excel) {
				billCellPanel.exportExcel(reportname);
			} else if (e.getSource() == btn_export_html) {
				billCellPanel.exportHtml(reportname);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private BillCellVO getBillCellItemVOs(HashVO[] _hashVOs, String[] checkdates) {
		int title_len = title.length;

		BillCellVO cellVO = new BillCellVO();
		int li_rows = titlerows + h_count_type.length; //表头+表尾
		if (_hashVOs != null && _hashVOs.length > 0) {
			li_rows = _hashVOs.length + titlerows + h_count_type.length; //表头+表尾
		}
		int li_cols = title_len + (checkdates.length + v_count_type.length) * types.length + checkdates.length * types_nocou.length; //序号+列数
		cellVO.setRowlength(li_rows);
		cellVO.setCollength(li_cols);

		BillCellItemVO[][] cellItemVOs = new BillCellItemVO[li_rows][li_cols];
		setBilCellVOStyle(cellItemVOs); //样式
		setColtitle(cellItemVOs, checkdates); //列头

		if (_hashVOs == null || !(_hashVOs.length > 0)) {
			cellVO.setCellItemVOs(cellItemVOs);
			return cellVO;
		}

		for (int m = 0; m < _hashVOs.length; m++) {
			HashVO hvo = _hashVOs[m];
			cellItemVOs[m + titlerows][0].setCellvalue("" + (m + 1));
			for (int i = 1; i < field.length; i++) {
				cellItemVOs[m + titlerows][i].setCellvalue(hvo.getStringValue(field[i], ""));
			}
		}

		if (h_count_type.length > 0) {
			for (int k = 0; k < h_count_type.length; k++) {
				cellItemVOs[_hashVOs.length + titlerows + k][title_len - 1].setCellvalue(h_count_type[k]);
			}
		}

		for (int j = 0; j < types.length; j++) {
			String[][] hj_h = new String[checkdates.length][_hashVOs.length];
			for (int m = 0; m < _hashVOs.length; m++) {
				HashVO hvo = _hashVOs[m];

				String[] hj_a = new String[checkdates.length];
				for (int i = 0; i < checkdates.length; i++) {
					hj_a[i] = hvo.getStringValue("result_a" + i + "_" + j, "");
					hj_h[i][m] = hj_a[i];
					cellItemVOs[m + titlerows][title_len + i + (checkdates.length + v_count_type.length) * j].setCellvalue(hj_a[i]);
				}

				if (v_count_type.length > 0) {
					for (int k = 0; k < v_count_type.length; k++) {
						if (v_count_type[k].equals("合计")) {
							cellItemVOs[m + titlerows][title_len + checkdates.length + k + (checkdates.length + v_count_type.length) * j].setCellvalue(getHJ(hj_a, 2));
							cellItemVOs[m + titlerows][title_len + checkdates.length + k + (checkdates.length + v_count_type.length) * j].setBackground("232,255,255");
						}
						if (v_count_type[k].equals("平均")) {
							cellItemVOs[m + titlerows][title_len + checkdates.length + k + (checkdates.length + v_count_type.length) * j].setCellvalue(getPJ(hj_a, 2));
							cellItemVOs[m + titlerows][title_len + checkdates.length + k + (checkdates.length + v_count_type.length) * j].setBackground("232,255,255");
						}
					}
				}
			}

			if (h_count_type.length > 0) {
				String[] hj_aa = new String[checkdates.length];
				for (int i = 0; i < checkdates.length; i++) {
					for (int k = 0; k < h_count_type.length; k++) {
						if (h_count_type[k].equals("合计")) {
							hj_aa[i] = getHJ(hj_h[i], 2);
							cellItemVOs[_hashVOs.length + titlerows + k][title_len + i + (checkdates.length + v_count_type.length) * j].setCellvalue(hj_aa[i]);
						}
						if (h_count_type[k].equals("平均")) {
							cellItemVOs[_hashVOs.length + titlerows + k][title_len + i + (checkdates.length + v_count_type.length) * j].setCellvalue(getPJ(hj_h[i], 2));
						}
					}
				}

				for (int k = 0; k < h_count_type.length; k++) {
					if (h_count_type[k].equals("合计")) {
						for (int kk = 0; kk < v_count_type.length; kk++) {
							if (v_count_type[kk].equals("合计")) {
								cellItemVOs[_hashVOs.length + titlerows + k][title_len + checkdates.length + kk + (checkdates.length + v_count_type.length) * j].setCellvalue(getHJ(hj_aa, 2));
							}
						}
					}
				}
			}
		}

		int types_all = (checkdates.length + v_count_type.length) * types.length;
		for (int j = 0; j < types_nocou.length; j++) {
			for (int m = 0; m < _hashVOs.length; m++) {
				HashVO hvo = _hashVOs[m];
				for (int i = 0; i < checkdates.length; i++) {
					cellItemVOs[m + titlerows][title_len + i + types_all + checkdates.length * j].setCellvalue(hvo.getStringValue("result_b" + i + "_" + j, ""));
				}
			}
		}

		cellVO.setCellItemVOs(cellItemVOs);
		return cellVO;
	}

	public String getHJ(String[] strs, int l) {
		BigDecimal sum = new BigDecimal("0");
		int mark = 0;
		for (int i = 0; i < strs.length; i++) {
			if (strs[i].equals("")) {
				continue;
			}

			try {
				sum = sum.add(new BigDecimal(strs[i]));
				mark++;
			} catch (Exception e) {
				WLTLogger.getLogger(PersonGqjlReportWKPanel.class).error("", e);
			}
		}

		if (mark == 0) {
			return "--";
		}

		return sum.setScale(l, BigDecimal.ROUND_HALF_UP).toString();
	}

	public String getPJ(String[] strs, int l) {
		BigDecimal sum = new BigDecimal("0");
		int mark = 0;
		for (int i = 0; i < strs.length; i++) {
			if (strs[i].equals("")) {
				continue;
			}

			try {
				sum = sum.add(new BigDecimal(strs[i]));
				mark++;
			} catch (Exception e) {
				WLTLogger.getLogger(PersonGqjlReportWKPanel.class).error("", e);
			}
		}

		if (mark == 0) {
			return "--";
		}

		return sum.divide(new BigDecimal(mark), l, BigDecimal.ROUND_HALF_UP).toString();
	}

	//BillCellVO 样式 长度、宽度等
	private void setBilCellVOStyle(BillCellItemVO[][] cellItemVOs) {
		for (int i = 0; i < cellItemVOs.length; i++) {
			for (int j = 0; j < cellItemVOs[i].length; j++) {
				cellItemVOs[i][j] = new BillCellItemVO();
				cellItemVOs[i][j].setCellkey(i + "," + j);
				cellItemVOs[i][j].setCelltype("TEXTAREA");
				cellItemVOs[i][j].setCellrow(i);
				cellItemVOs[i][j].setCellcol(j);
				cellItemVOs[i][j].setHalign(2);
				cellItemVOs[i][j].setCellvalue("");
				cellItemVOs[i][j].setSpan("1,1");
				cellItemVOs[i][j].setRowheight("" + 25);
				cellItemVOs[i][j].setFonttype("宋体");
				cellItemVOs[i][j].setFontsize("12");
				cellItemVOs[i][j].setFontstyle(Font.PLAIN + "");

				if (j == 0) {
					cellItemVOs[i][j].setColwidth("35"); //设置序号宽度
					if (i >= titlerows && i < cellItemVOs.length) {
						cellItemVOs[i][j].setBackground("232,255,255"); //设置序号颜色
					}
				} else if (j > 0 && j < field_len.length) {
					cellItemVOs[i][j].setColwidth(field_len[j]);
				} else {
					cellItemVOs[i][j].setColwidth("80"); //设置其他列的宽度
				}

				if (i < titlerows) {
					cellItemVOs[i][j].setHalign(2); //设置表头列字段居中
					cellItemVOs[i][j].setBackground("232,255,255"); //设置表头列字段颜色
				} else {
					cellItemVOs[i][j].setHalign(1);//1表示水平左对齐 
				}

				if (h_count_type.length > 0) {
					for (int k = 0; k < h_count_type.length; k++) {
						if (i == cellItemVOs.length - (k + 1)) {
							cellItemVOs[i][j].setBackground("232,255,255");
						}
					}
				}

				cellItemVOs[i][j].setValign(2); //2表示垂直居中 
			}
		}
	}

	//列头信息 
	private void setColtitle(BillCellItemVO[][] cellItemVOs, String[] checkdates) {
		int title_len = title.length;
		for (int i = 0; i < title_len; i++) {
			cellItemVOs[0][i].setCellvalue(title[i]);
			if (titlerows == 2) {
				cellItemVOs[0][i].setSpan("2,1");
			}
		}

		for (int i = 0; i < types.length; i++) {
			cellItemVOs[0][title_len + (checkdates.length + v_count_type.length) * i].setCellvalue(types[i]);
			if (titlerows == 2) {
				cellItemVOs[0][title_len + (checkdates.length + v_count_type.length) * i].setSpan("1," + (checkdates.length + v_count_type.length));
			}
		}

		if (titlerows == 2) {
			for (int i = 0; i < checkdates.length; i++) {
				for (int j = 0; j < types.length; j++) {
					cellItemVOs[1][title_len + i + (checkdates.length + v_count_type.length) * j].setCellvalue(checkdates[i]);
				}
			}

			if (v_count_type.length > 0) {
				for (int k = 0; k < v_count_type.length; k++) {
					for (int i = 0; i < types.length; i++) {
						cellItemVOs[1][title_len + checkdates.length + (checkdates.length + v_count_type.length) * i + k].setCellvalue(v_count_type[k]);
					}
				}
			}
		}

		int types_all = (checkdates.length + v_count_type.length) * types.length;
		for (int i = 0; i < types_nocou.length; i++) {
			cellItemVOs[0][title_len + types_all + checkdates.length * i].setCellvalue(types_nocou[i]);
			cellItemVOs[0][title_len + types_all + checkdates.length * i].setSpan("1," + checkdates.length);
		}

		if (titlerows == 2) {
			for (int i = 0; i < checkdates.length; i++) {
				for (int j = 0; j < types_nocou.length; j++) {
					cellItemVOs[1][title_len + i + types_all + checkdates.length * j].setCellvalue(checkdates[i]);
				}
			}
		}
	}

}
