package cn.com.pushworld.salary.ui.report;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.pushworld.salary.to.SalaryReportVO;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

public class PersonExcelReportWKPanel extends AbstractWorkPanel implements ActionListener {
	private BillQueryPanel billQueryPanel = null;
	private BillCellPanel billCellPanel = null;
	private WLTButton btn_export_excel, btn_export_html;
	private String[] title = new String[] { "序号", "系统登录号", "姓名" };
	private String[] field = new String[] { "序号", "A", "B" };
	private String[] field_len = new String[] { "序号", "100", "100" };
	private String[] types = new String[] {  };
	private String[] types_nocou = new String[] {  };
	private String[] types_field = new String[] {  };
	private String[] types_field_nocou = new String[] {  };
	private String reportname = "股权激励基金";
	private String[] v_count_type = new String[] { }; //"合计","平均"
	private String[] h_count_type = new String[] { }; //"合计","平均"
	private int titlerows = 1;
	private String excel_code = "A";

	public PersonExcelReportWKPanel(SalaryReportVO salaryrvo_) {
		this.title = salaryrvo_.getTitle();
		this.field = salaryrvo_.getField();
		this.field_len = salaryrvo_.getField_len();
		this.types = salaryrvo_.getTypes();
		this.types_nocou = salaryrvo_.getTypes_nocou();
		this.types_field = salaryrvo_.getTypes_field();
		this.types_field_nocou = salaryrvo_.getTypes_field_nocou();
		this.reportname = salaryrvo_.getReportname();
		this.v_count_type = salaryrvo_.getV_count_type();
		this.h_count_type = salaryrvo_.getH_count_type();
		this.titlerows = salaryrvo_.getTitlerows();
		this.excel_code = salaryrvo_.getExcel_code();
	}

	public void initialize() {
		this.setLayout(new BorderLayout());
		billQueryPanel = new BillQueryPanel("SAL_SALARYBILL_CODE1");
		billQueryPanel.addBillQuickActionListener(this);

		QueryCPanel_UIRefPanel monthlyRef = (QueryCPanel_UIRefPanel) billQueryPanel.getCompentByKey("monthly");
		String checkDate = new SalaryUIUtil().getCheckDate();
		monthlyRef.setValue(checkDate);

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

	public BillQueryPanel getBillQueryPanel() {
		return billQueryPanel;
	}

	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == billQueryPanel) {
				String monthly = billQueryPanel.getRealValueAt("monthly");

				if (monthly == null || monthly.equals("")) {
					MessageBox.show(this, "请选择月度！");
					return;
				}
				
				String[] tablename = UIUtil.getStringArrayFirstColByDS(null, "select tablename from excel_tab where excelname='" + reportname + "'");

				if (!(tablename != null && tablename.length > 0)) {
					MessageBox.show(this, "没有查询到结果！");
					return;
				}

				String[] checkdates = new String[] { };

				String sql = "select * from " + tablename[0] + " where concat(year,'-',month)='"+monthly+"'";
				HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, sql);
				
				if (hvs == null || !(hvs.length > 0)) {
					MessageBox.show(this, "没有查询到结果！");
					return;
				}
				
				title = new String[hvs[0].getKeys().length-3];
				field = new String[hvs[0].getKeys().length-3];
				field_len = new String[hvs[0].getKeys().length-3];
				title[0] = "序号";
				field[0] = "序号";
				field_len[0] = "序号";
				
				for (int i = 4; i < hvs[0].getKeys().length; i++) {
					title[i + 1 - 4] = getColumnName(i + 1 - 4);
					field[i + 1 - 4] = getColumnName(i + 1 - 4);
					field_len[i + 1 - 4] = "85";
				}

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
	
	private String getColumnName(int columnNum) {
		String result = "";

		int first;
		int last;
		if (columnNum > 256)
			columnNum = 256;
		first = columnNum / 27;
		last = columnNum - (first * 26);

		if (first > 0)
			result = String.valueOf((char) (first + 64));

		if (last > 0)
			result = result + String.valueOf((char) (last + 64));

		return result.toUpperCase();
	}

	public void query(String monthly, String userid) {
		try {
			SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
			HashVO[] hvs = ifc.getPersonStyle(reportname, monthly, userid, excel_code, types_field, types_field_nocou);

			if (hvs == null || !(hvs.length > 0)) {
				MessageBox.show(this, "没有查询到结果！");
				return;
			}
			String[] checkdates = new String[] { monthly };
			billCellPanel.setIfSetRowHeight(true);
			billCellPanel.loadBillCellData(getBillCellItemVOs(hvs, checkdates));
			billCellPanel.setEditable(false);
			if (billCellPanel.getRowCount() > 1) {
				billCellPanel.setLockedCell(titlerows, 1); //锁定表头
			}
		} catch (Exception e) {
			e.printStackTrace();
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
				WLTLogger.getLogger(PersonExcelReportWKPanel.class).error("", e);
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
				WLTLogger.getLogger(PersonExcelReportWKPanel.class).error("", e);
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
