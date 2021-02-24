package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.RefDialog_Date;
import cn.com.pushworld.wn.bs.TyxwcontentCount;
import freemarker.template.SimpleDate;

public class TyxwcontentWKPanel extends AbstractWorkPanel implements
		ActionListener, ChangeListener {

	private BillListPanel panel;
	private BillListPanel panel2;
	private WLTButton btn_count, btn_export, btn_zn_export, btn_zn_count;
	private String message = null;
	private JFileChooser chooser;
	private String result;
	private String queryConditionSQL;
	private WLTTabbedPane tabPanel = null;

	@Override
	public void initialize() {
		tabPanel = new WLTTabbedPane();
		panel = new BillListPanel("WN_TYXWCOUNT_RESULT_CODE");
		btn_count = new WLTButton("户数统计");
		btn_count.addActionListener(this);
		btn_export = new WLTButton("导出");
		btn_export.addActionListener(this);
		panel.addBatchBillListButton(new WLTButton[] { btn_count, btn_export });
		panel.getQuickQueryPanel().addBillQuickActionListener(this);
		panel.repaintBillListButton();
		panel2 = new BillListPanel("WN_ZNSHCOUNT_RESULT_CODE1");
		btn_zn_export = new WLTButton("导出");// 助农商户维护导出excel
		btn_zn_export.addActionListener(this);
		btn_zn_count = new WLTButton("助农统计");
		btn_zn_count.addActionListener(this);
		// 助农商户维护户数统计
		panel2.addBatchBillListButton(new WLTButton[] { btn_zn_count,
				btn_zn_export });
		panel2.repaintBillListButton();
		panel2.getQuickQueryPanel().addBillQuickActionListener(this);
		tabPanel.addTab("特约小微商户户数统计", panel);
		tabPanel.addTab("助农商户户数统计", panel2);
		tabPanel.addChangeListener(this);
		this.add(tabPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_count) {// 特约小微户数统计
			try {
				RefItemVO refItemVO = new RefItemVO();
				// refItemVO.setId(report_date_new);
				RefDialog_Date date = new RefDialog_Date(this, "", refItemVO,
						null);
				/**
				 * 获取到用户选中的日期， 每次执行SQL时，日期区间从选中日期的当前月月初到当前选中日期
				 * 如:用户选中2020-04-08，则日期区间定义到2020-04-01~2020-04-08
				 */
				date.initialize();
				date.setVisible(true);// 设置日期选择框可见
				if (date.getCloseType() == 1) {
					final RefItemVO ivo = date.getReturnRefItemVO();
					final String curSelectDate = ivo.getId();// 当前选中日期
					final String curSelectMonth = curSelectDate.substring(0, 7);
					String exisSQL = "select 1 from wn_tyxwcount_result where curmonth ='"
							+ curSelectMonth + "'";
					String[] exists = UIUtil.getStringArrayFirstColByDS(null,
							exisSQL);
					final TyxwcontentCount tyxw = new TyxwcontentCount();
					
					if (exists != null && exists.length > 0) {// 已经存在是否重新计算
						
						if (MessageBox.confirm(this, "日期【" + curSelectDate
								+ "】小微特约商户统计信息已经存在，确定重复计算吗？")) {
							final String dateInterval = getDateInterVal(curSelectMonth
									+ "-01", curSelectDate, curSelectMonth);
							new SplashWindow(this, new AbstractAction() {
								public void actionPerformed(ActionEvent e) {
									// 特约小微商户计算
									try {

										message = tyxw.count(curSelectMonth
												+ "-01", curSelectDate,
												curSelectMonth,dateInterval ,true);
									} catch (Exception e1) {
										e1.printStackTrace();
									}
								}
							});
							panel.QueryData("select * from wn_tyxwcount_result where CURMONTH='"
									+ curSelectMonth + "' ");
						}
					} else {// 当前日期区间没有数据，不需要删除
						final String dateInterval = getDateInterVal(curSelectMonth
								+ "-01", curSelectDate, curSelectMonth);
						new SplashWindow(this, new AbstractAction() {
							public void actionPerformed(ActionEvent e) {
								// 特约小微商户计算
								try {
									message = tyxw.count(
											curSelectMonth + "-01",
											curSelectDate, curSelectMonth,dateInterval,
											false);
								} catch (Exception e1) {
									e1.printStackTrace();
								}
							}
						});
						panel.QueryData("select * from wn_tyxwcount_result where CURMONTH='"
								+ curSelectMonth + "' ");
					}
					MessageBox.show(this, message);
					panel.refreshData();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (e.getSource() == panel.getQuickQueryPanel()) {// 获取到对应的数据
			String curMonth = panel.getQuickQueryPanel().getRealValueAt(
					"CURMONTH");
			String sql = "select * from wn_tyxwcount_result where  1=1 ";
			if (!TBUtil.isEmpty(curMonth)) {
				sql = sql + " and curMonth ='"
						+ curMonth.replace("年", "-").replace("月;", "") + "'";
			}
			sql = sql + " order by curmonth desc ";
			panel.queryDataByDS(null, sql);
		} else if (e.getSource() == btn_export) {
			try {
				// 设置弹出文件选择对话框
				chooser = new JFileChooser();
				final String templeName = panel.getTempletVO().getTempletname();// 获取到模板的名称
				chooser.setSelectedFile(new File(templeName + ".xls"));
				int showOpenDialog = chooser.showOpenDialog(null);
				final String filePath;
				if (showOpenDialog == JFileChooser.APPROVE_OPTION) {// 选择打开的文件
					filePath = chooser.getSelectedFile().getAbsolutePath();
				} else {
					return;
				}
				new SplashWindow(this, new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						result = ImportExcel(panel, filePath, templeName,
								"特约小微商户户数");
					}
				});
				MessageBox.show(this, result);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (e.getSource() == btn_zn_count) { // 助农商户户数统计
			try {
				RefItemVO refItemVO = new RefItemVO();
				// refItemVO.setId(report_date_new);
				RefDialog_Date date = new RefDialog_Date(this, "", refItemVO,
						null);
				date.initialize();
				date.setVisible(true);// 设置日期选择框可见
				final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
						.lookUpRemoteService(WnSalaryServiceIfc.class);
				if (date.getCloseType() == 1) {//
					final RefItemVO ivo = date.getReturnRefItemVO();
					/**
					 * 获取到当前选中日期
					 */
					final String curSelectDate = ivo.getId();// 当前选中日期
					final String curSelectMonth = curSelectDate.substring(0, 7);
					final String curSelectMonthStart = curSelectMonth + "-01";
					String existsSQL = "select 1 from wn_znshcount_result where curmonth='"
							+ curSelectMonth + "'";
					String[] existsNum = UIUtil.getStringArrayFirstColByDS(
							null, existsSQL);
			       
					if (existsNum.length <= 0) {// 结果表中没有数据，直接计算
//						 final String dateInterVal = getDateInterVal(curSelectMonthStart, curSelectDate, curSelectMonthStart);
						new SplashWindow(this, new AbstractAction() {
							@Override
							public void actionPerformed(ActionEvent e) { // 执行助农商户维护计算
								String dateInterVal = getDateInterVal(curSelectMonthStart, curSelectDate, curSelectMonth);
								message = service.znCount(curSelectMonthStart,
										curSelectDate, curSelectMonth,dateInterVal,false);
							}
						});
						panel2.QueryData("select * from wn_znshcount_result where CURMONTH='"
								+ curSelectMonth + "' ");
						MessageBox.show(this, message);
					} else {// 当前考核月的数据已经存在，请用户重新计算
						if (MessageBox.confirm(this, "日期【" + curSelectDate
								+ "】助农商户统计信息已经存在，确定重复计算吗？")) {
//							 final String dateInterVal = getDateInterVal(curSelectMonthStart, curSelectDate, curSelectMonthStart);
							new SplashWindow(this, new AbstractAction() {
								@Override
								public void actionPerformed(ActionEvent e) { // 执行助农商户维护计算
									// message=tyxw.znCount(curSelectMonth+"-01",
									// curSelectDate, curSelectMonth, true);
									String dateInterVal = getDateInterVal(curSelectMonthStart, curSelectDate, curSelectMonth);
									message = service.znCount(
											curSelectMonthStart, curSelectDate,
											curSelectMonth,dateInterVal ,true);
								}
							});
							panel2.QueryData("select * from wn_znshcount_result where CURMONTH='"
									+ curSelectMonth + "' ");
							MessageBox.show(this, message);
						} else {
							return;
						}
					}
				}
				panel2.refreshCurrData();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		} else if (e.getSource() == btn_zn_export) {
			chooser = new JFileChooser();
			final String templeName = panel2.getTempletVO().getTempletname();// 获取到模板的名称
			chooser.setSelectedFile(new File(templeName + ".xls"));
			int showOpenDialog = chooser.showOpenDialog(null);
			final String filePath;
			if (showOpenDialog == JFileChooser.APPROVE_OPTION) {// 选择打开的文件
				filePath = chooser.getSelectedFile().getAbsolutePath();
			} else {
				return;
			}
			new SplashWindow(this, new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					result = ImportExcel(panel2, filePath, templeName, "助农商户维护");
				}
			});
			MessageBox.show(this, "【助农商户维护】导出成功");
		} else if (e.getSource() == panel2.getQuickQueryPanel()) {
			String curMonth = panel2.getQuickQueryPanel().getRealValueAt(
					"curmonth");
			String sql = "select * from WN_ZNSHCOUNT_RESULT where  1=1 ";
			if (!TBUtil.isEmpty(curMonth)) {
				sql = sql + " and curmonth ='"
						+ curMonth.replace("年", "-").replace("月;", "") + "'";
			}
			sql = sql + " order by curmonth desc ";
			panel2.queryDataByDS(null, sql);
		}
	}

	public String ImportExcel(BillListPanel panel, String filePath,
			String templeName, String sheetName) {
		String result = "";
		try {
			Workbook monitorBook = new SXSSFWorkbook(100);
			Sheet firstSheet = monitorBook.createSheet(sheetName);
			Row firstRow = firstSheet.createRow(0);
			Cell firstCell = null;
			CellStyle firstCellStyle = monitorBook.createCellStyle();
			firstCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			// 获取到表头信息
			Pub_Templet_1_ItemVO[] templetItemVOs = panel.getTempletItemVOs();
			List<String> unShowList = new ArrayList<String>();
			Pub_Templet_1_ItemVO pub_Templet_1_ItemVO = null;
			List<String> colList = new ArrayList<String>();// 存放可以显示的字段
			for (int i = 0, n = 0; i < templetItemVOs.length; i++) {
				pub_Templet_1_ItemVO = templetItemVOs[i];
				String cellKey = pub_Templet_1_ItemVO.getItemkey();
				String cellName = pub_Templet_1_ItemVO.getItemname();
				firstSheet.setColumnWidth(i, 25 * 256);
				if (pub_Templet_1_ItemVO.isListisshowable()) {
					firstCell = firstRow.createCell(i - n);
					firstCell.setCellValue(cellName);
					firstCell.setCellStyle(firstCellStyle);// 设置背景色和居中显示
					colList.add(cellKey);
				} else {
					unShowList.add(cellKey.toUpperCase());
					n++;
				}
			}
			if (queryConditionSQL == null || "".equals(queryConditionSQL)) {
				queryConditionSQL = "select * from "
						+ panel.getTempletVO().getSavedtablename()
						+ " where 1=1 ";
			}
			String queryCondition = panel.getQuickQueryPanel()
					.getQuerySQLCondition("curmonth");
			// 查询: and ((curmonth.curmonth>='2020-05-01' and
			// curmonth.curmonth<='2020-05-31 24:00:00'))

			if (!TBUtil.isEmpty(queryCondition)) {
				queryConditionSQL = queryConditionSQL
						+ " and  curmonth="
						+ queryCondition.substring(
								queryCondition.indexOf("=") + 1,
								queryCondition.indexOf("'") + 8) + "'";
			}
			// 获取到当前模板的查询条件
			HashVO[] hashVos = UIUtil.getHashVoArrayByDS(null,
					queryConditionSQL);
			if (hashVos == null || hashVos.length == 0) {
				return "当前无数据需要导出";
			}
			Row nextRow = null;

			String cellValue = "";
			// 对除首行以外的数据行进行处理
			for (int i = 0; i < hashVos.length; i++) {
				nextRow = firstSheet.createRow(i + 1);
				String[] keys = hashVos[i].getKeys();// 这个是按照数据库中的顺序来进行排列的
				System.out.println(Arrays.toString(keys));
				for (int j = 0, n = 0; j < colList.size(); j++) {// 对当前行中没有一行数据进行处理
					if (unShowList.contains(colList.get(j).toUpperCase())) {
						n++;
						continue;
					}
					// 加工处理每一行数据(有一部分数据在数据表中存储的是id，需要转化成name来输出)

					// 其他(名称 描述等)
					cellValue = hashVos[i].getStringValue(colList.get(j));
					nextRow.createCell(j - n).setCellValue(cellValue);
				}
			}
			// 对数据进行处理
			String filename = filePath.substring(
					filePath.lastIndexOf("\\") + 1, filePath.lastIndexOf("."));
			filePath = filePath.substring(0, filePath.lastIndexOf("\\"));
			File file = new File(filePath + "/" + filename + ".xls");
			int i = 1;
			while (file.exists()) {
				filename = templeName + i + ".xls";
				file = new File(filePath + "/" + filename);
				i++;
			}
			file.createNewFile();
			FileOutputStream fout = new FileOutputStream(file.getAbsolutePath());
			monitorBook.write(fout);
			fout.close();
			result = "数据导出成功";
		} catch (Exception e) {
			result = "数据导出失败";
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void stateChanged(ChangeEvent e) {

	}

	/**
	 * 获取到当前表中的日期区间
	 * 
	 * @return
	 */
	private String getDateInterVal(String curSelectMonthStart,
			String curSelectDate, String curSelectMonth) {
		String result = "";
		// 查询当前表中当前考核月最大的数据
		try {
			String maxDate = UIUtil
					.getStringValueByDS(
							null,
							"select max(to_char(to_date(biz_dt,'yyyy-mm-dd'),'yyyy-mm-dd')) from wnbank.t_dis_ifsp_intgr_txn_dtl where to_char(to_date(biz_dt,'yyyy-mm-dd'),'yyyy-mm-dd') like '"
									+ curSelectMonth + "%'");// 获取到当前考核月最大的日期
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			long maxMi = format.parse(maxDate).getTime();
			long curMi = format.parse(curSelectDate).getTime();
			if (maxMi < curMi) {// 如果当前选中日期，大于数据表中的最大日期，则日期区间定位 在月初-数据表中最大日期
				result = curSelectMonthStart + " ~ " + maxDate;
			} else {
				result = curSelectMonthStart + " ~ " + curSelectDate;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}