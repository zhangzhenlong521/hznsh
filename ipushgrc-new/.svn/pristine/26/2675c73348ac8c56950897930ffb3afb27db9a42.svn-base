package com.pushworld.ipushgrc.ui.login.p010;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.ss.usermodel.Font;
import org.jdesktop.jdic.desktop.Desktop;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 部门登录信息统计，钻取查看界面
 * @author YangQing/2013-12-02
 *
 */
public class DeptLoginInfoWKPanel extends AbstractWorkPanel implements ActionListener {
	private WLTButton btn_show, btn_export, btn_exportAll;
	private BillListPanel panel_deptlogin, panel_userlogin;
	private String deptid = "";
	private String weekofyear = "";//第几周

	public DeptLoginInfoWKPanel(String deptid) {
		this.deptid = deptid;
	}

	public void initialize() {
		panel_deptlogin = new BillListPanel("PUB_DEPTLOGINDATA_YQ_Q01");
		panel_deptlogin.QueryDataByCondition("id=" + deptid);
		String lowerDeptids = "";
		try {
			BillVO[] loginVO = panel_deptlogin.queryBillVOsByCondition("id=" + deptid);
			lowerDeptids = loginVO[0].getStringValue("LOWERDEPTIDS");//下属机构id
			weekofyear = loginVO[0].getStringValue("WEEKOFYEAR");//第几周
		} catch (Exception e) {
			e.printStackTrace();
		}
		btn_show = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);
		btn_export = WLTButton.createButtonByType(WLTButton.LIST_EXPORTEXCEL);
		btn_export.setPreferredSize(new Dimension(100, 23));
		btn_export.setText("导出本部门信息");
		btn_exportAll = new WLTButton("导出所有部门信息", "zt_026.gif");
		btn_exportAll.addActionListener(this);

		panel_deptlogin.addBatchBillListButton(new WLTButton[] { btn_show, btn_export, btn_exportAll });
		panel_deptlogin.repaintBillListButton();

		panel_userlogin = new BillListPanel("USERLOGIN_YQ_Q01");

		if (!TBUtil.isEmpty(lowerDeptids) && !TBUtil.isEmpty(weekofyear)) {
			String in_deptid = new TBUtil().getInCondition(lowerDeptids);
			panel_userlogin.queryDataByCondition("departid in (" + in_deptid + ") and weekofyear='" + weekofyear + "'", "online_millisecond desc");

		}
		WLTSplitPane split = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, panel_deptlogin, panel_userlogin);
		split.setDividerLocation(150); //设置上分隔距离
		this.add(split); //

		this.setLayout(new BorderLayout());
		this.add(split, BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_exportAll) {
			JFileChooser fc = new JFileChooser();//文件选择框
			fc.setCurrentDirectory(new File(ClientEnvironment.str_downLoadFileDir));
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setDialogTitle("请选择Excel保存目录");
			int li_result = fc.showSaveDialog(this);
			if (li_result != JFileChooser.APPROVE_OPTION) {
				return;//不点击OK或YES,什么也不做
			}
			String str_pathdir = fc.getSelectedFile().getAbsolutePath();//返回保存路径
			if (str_pathdir.endsWith("\\")) {//如果路径中末尾是"\\",去掉，保持一致
				str_pathdir = str_pathdir.substring(0, str_pathdir.length() - 1);
			}

			String saveFileName = str_pathdir + "/" + weekofyear + "_部门登录统计.xls";//保存的文件名
			File newFile = new File(saveFileName);
			if (newFile.exists()) {//如果该存储目录下有相同文件时，提示是否替换该文件
				boolean isChange = MessageBox.confirm(this, "该目录下存在相同文件，是否替换?");
				if (isChange) {
					newFile.delete();//删除原重复文件
				} else {//不替换
					saveFileName = saveFileName.substring(0, saveFileName.lastIndexOf("."));
					int count = 1;
					String newSave = "";
					while (true) {
						newSave = saveFileName + "(" + count + ").xls";//新文件名
						File ff = new File(newSave);
						if (ff.exists()) {//还是存在，count++
							count++;
						} else {
							break;
						}
					}
					saveFileName = newSave;
				}
			}

			try {
				setExcelData(saveFileName);
				if (JOptionPane.showConfirmDialog(this, "导出成功!!\r\n你是否想立即打开?", "提示", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					try {
						Desktop.open(new File(saveFileName)); //
					} catch (Exception ex) {
						ex.printStackTrace(); //
						try {
							Runtime.getRuntime().exec("explorer.exe \"" + saveFileName + "\"");
						} catch (Exception exx) {
							exx.printStackTrace(); //
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	//导出Excel赋值
	private void setExcelData(String excelName) throws Exception {

		HSSFWorkbook xwb = new HSSFWorkbook();
		HSSFRow row = null;
		String sql_loginData = new String("select * from pub_deptloginData where weekofyear='" + weekofyear + "' order by ONLINE_MILLISECOND desc");

		HashVO[] loginVO = UIUtil.getHashVoArrayByDS(null, sql_loginData);
		HSSFCellStyle cstyle1 = xwb.createCellStyle();
		cstyle1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		cstyle1.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);//设置背景颜色
		cstyle1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		cstyle1.setAlignment(HSSFCellStyle.ALIGN_CENTER);//居中
		HSSFFont font = xwb.createFont();
		font.setFontName("宋体");
		font.setFontHeightInPoints((short) 11);//字体大小
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);//粗体
		font.setColor(HSSFColor.BLACK.index);
		cstyle1.setFont(font); //表头字体格式

		// 边框设置
		cstyle1.setBorderTop((short) 1);
		cstyle1.setBorderRight((short) 1);

		cstyle1.setBorderBottom((short) 1);
		cstyle1.setBorderLeft((short) 1);
		cstyle1.setTopBorderColor(HSSFColor.BLACK.index);
		cstyle1.setRightBorderColor(HSSFColor.BLACK.index);
		cstyle1.setBottomBorderColor(HSSFColor.BLACK.index);
		cstyle1.setLeftBorderColor(HSSFColor.BLACK.index);

		HSSFCellStyle cstyle2 = xwb.createCellStyle();
		// 边框设置
		cstyle2.setBorderTop((short) 1);
		cstyle2.setBorderRight((short) 1);

		cstyle2.setBorderBottom((short) 1);
		cstyle2.setBorderLeft((short) 1);
		cstyle2.setTopBorderColor(HSSFColor.BLACK.index);
		cstyle2.setRightBorderColor(HSSFColor.BLACK.index);
		cstyle2.setBottomBorderColor(HSSFColor.BLACK.index);
		cstyle2.setLeftBorderColor(HSSFColor.BLACK.index);

		HSSFSheet sheet = xwb.createSheet();
		sheet.setColumnWidth(0, sheet.getColumnWidth(0) * 5);
		sheet.setColumnWidth(1, sheet.getColumnWidth(3) * 2);
		sheet.setColumnWidth(2, sheet.getColumnWidth(3) * 2);
		sheet.setColumnWidth(3, sheet.getColumnWidth(3) * 2);
		sheet.setColumnWidth(4, sheet.getColumnWidth(3) * 2);
		for (int a = 0; a < loginVO.length + 2; a++) {
			row = sheet.createRow(a);
			if (a == 0) {//第一行
				String dayofweek = loginVO[a].getStringValue("dayofweek", "");//一周7天
				String moday = dayofweek.substring(0, dayofweek.indexOf(";"));
				String sunday = dayofweek.substring(dayofweek.length() - 11, dayofweek.length() - 1);
				row.createCell(0).setCellValue(weekofyear + " (" + moday + "到" + sunday + ")");
				row.getCell(0).setCellStyle(cstyle1);
				row.setHeight((short) (row.getHeight() * 3));
				continue;
			}
			if (a == 1) {
				row.createCell(0).setCellValue("部门名称");
				row.createCell(1).setCellValue("登录次数");
				row.createCell(2).setCellValue("登录次数评判");
				row.createCell(3).setCellValue("在线时长");
				row.createCell(4).setCellValue("在线时长评判");

				row.getCell(0).setCellStyle(cstyle1);
				row.getCell(1).setCellStyle(cstyle1);
				row.getCell(2).setCellStyle(cstyle1);
				row.getCell(3).setCellStyle(cstyle1);
				row.getCell(4).setCellStyle(cstyle1);
				row.setHeight((short) (row.getHeight() * 2));
				continue;
			}

			String deptname = loginVO[a - 2].getStringValue("DEPARTNAME", "");//部门名称
			String logincount = loginVO[a - 2].getStringValue("LOGINCOUNT", "");//
			String num_judge = loginVO[a - 2].getStringValue("NUM_JUDGE", "");//
			String onlinehours = loginVO[a - 2].getStringValue("ONLINEHOURS", "");//
			String online_judge = loginVO[a - 2].getStringValue("ONLINE_JUDGE", "");//

			row.createCell(0).setCellValue(deptname);
			row.createCell(1).setCellValue(logincount);
			row.createCell(2).setCellValue(num_judge);
			row.createCell(3).setCellValue(onlinehours);
			row.createCell(4).setCellValue(online_judge);

			row.getCell(0).setCellStyle(cstyle2);
			row.getCell(1).setCellStyle(cstyle2);
			row.getCell(2).setCellStyle(cstyle2);
			row.getCell(3).setCellStyle(cstyle2);
			row.getCell(4).setCellStyle(cstyle2);

			row.setHeight((short) (row.getHeight() * 2));
		}
		Region region = new Region(0, (short) 0, 0, (short) 4);
		sheet.addMergedRegion(region);

		FileOutputStream out = new FileOutputStream(excelName);
		xwb.write(out);
		out.close();
	}
}
