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
 * ���ŵ�¼��Ϣͳ�ƣ���ȡ�鿴����
 * @author YangQing/2013-12-02
 *
 */
public class DeptLoginInfoWKPanel extends AbstractWorkPanel implements ActionListener {
	private WLTButton btn_show, btn_export, btn_exportAll;
	private BillListPanel panel_deptlogin, panel_userlogin;
	private String deptid = "";
	private String weekofyear = "";//�ڼ���

	public DeptLoginInfoWKPanel(String deptid) {
		this.deptid = deptid;
	}

	public void initialize() {
		panel_deptlogin = new BillListPanel("PUB_DEPTLOGINDATA_YQ_Q01");
		panel_deptlogin.QueryDataByCondition("id=" + deptid);
		String lowerDeptids = "";
		try {
			BillVO[] loginVO = panel_deptlogin.queryBillVOsByCondition("id=" + deptid);
			lowerDeptids = loginVO[0].getStringValue("LOWERDEPTIDS");//��������id
			weekofyear = loginVO[0].getStringValue("WEEKOFYEAR");//�ڼ���
		} catch (Exception e) {
			e.printStackTrace();
		}
		btn_show = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);
		btn_export = WLTButton.createButtonByType(WLTButton.LIST_EXPORTEXCEL);
		btn_export.setPreferredSize(new Dimension(100, 23));
		btn_export.setText("������������Ϣ");
		btn_exportAll = new WLTButton("�������в�����Ϣ", "zt_026.gif");
		btn_exportAll.addActionListener(this);

		panel_deptlogin.addBatchBillListButton(new WLTButton[] { btn_show, btn_export, btn_exportAll });
		panel_deptlogin.repaintBillListButton();

		panel_userlogin = new BillListPanel("USERLOGIN_YQ_Q01");

		if (!TBUtil.isEmpty(lowerDeptids) && !TBUtil.isEmpty(weekofyear)) {
			String in_deptid = new TBUtil().getInCondition(lowerDeptids);
			panel_userlogin.queryDataByCondition("departid in (" + in_deptid + ") and weekofyear='" + weekofyear + "'", "online_millisecond desc");

		}
		WLTSplitPane split = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, panel_deptlogin, panel_userlogin);
		split.setDividerLocation(150); //�����Ϸָ�����
		this.add(split); //

		this.setLayout(new BorderLayout());
		this.add(split, BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_exportAll) {
			JFileChooser fc = new JFileChooser();//�ļ�ѡ���
			fc.setCurrentDirectory(new File(ClientEnvironment.str_downLoadFileDir));
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setDialogTitle("��ѡ��Excel����Ŀ¼");
			int li_result = fc.showSaveDialog(this);
			if (li_result != JFileChooser.APPROVE_OPTION) {
				return;//�����OK��YES,ʲôҲ����
			}
			String str_pathdir = fc.getSelectedFile().getAbsolutePath();//���ر���·��
			if (str_pathdir.endsWith("\\")) {//���·����ĩβ��"\\",ȥ��������һ��
				str_pathdir = str_pathdir.substring(0, str_pathdir.length() - 1);
			}

			String saveFileName = str_pathdir + "/" + weekofyear + "_���ŵ�¼ͳ��.xls";//������ļ���
			File newFile = new File(saveFileName);
			if (newFile.exists()) {//����ô洢Ŀ¼������ͬ�ļ�ʱ����ʾ�Ƿ��滻���ļ�
				boolean isChange = MessageBox.confirm(this, "��Ŀ¼�´�����ͬ�ļ����Ƿ��滻?");
				if (isChange) {
					newFile.delete();//ɾ��ԭ�ظ��ļ�
				} else {//���滻
					saveFileName = saveFileName.substring(0, saveFileName.lastIndexOf("."));
					int count = 1;
					String newSave = "";
					while (true) {
						newSave = saveFileName + "(" + count + ").xls";//���ļ���
						File ff = new File(newSave);
						if (ff.exists()) {//���Ǵ��ڣ�count++
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
				if (JOptionPane.showConfirmDialog(this, "�����ɹ�!!\r\n���Ƿ���������?", "��ʾ", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
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

	//����Excel��ֵ
	private void setExcelData(String excelName) throws Exception {

		HSSFWorkbook xwb = new HSSFWorkbook();
		HSSFRow row = null;
		String sql_loginData = new String("select * from pub_deptloginData where weekofyear='" + weekofyear + "' order by ONLINE_MILLISECOND desc");

		HashVO[] loginVO = UIUtil.getHashVoArrayByDS(null, sql_loginData);
		HSSFCellStyle cstyle1 = xwb.createCellStyle();
		cstyle1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		cstyle1.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);//���ñ�����ɫ
		cstyle1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		cstyle1.setAlignment(HSSFCellStyle.ALIGN_CENTER);//����
		HSSFFont font = xwb.createFont();
		font.setFontName("����");
		font.setFontHeightInPoints((short) 11);//�����С
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);//����
		font.setColor(HSSFColor.BLACK.index);
		cstyle1.setFont(font); //��ͷ�����ʽ

		// �߿�����
		cstyle1.setBorderTop((short) 1);
		cstyle1.setBorderRight((short) 1);

		cstyle1.setBorderBottom((short) 1);
		cstyle1.setBorderLeft((short) 1);
		cstyle1.setTopBorderColor(HSSFColor.BLACK.index);
		cstyle1.setRightBorderColor(HSSFColor.BLACK.index);
		cstyle1.setBottomBorderColor(HSSFColor.BLACK.index);
		cstyle1.setLeftBorderColor(HSSFColor.BLACK.index);

		HSSFCellStyle cstyle2 = xwb.createCellStyle();
		// �߿�����
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
			if (a == 0) {//��һ��
				String dayofweek = loginVO[a].getStringValue("dayofweek", "");//һ��7��
				String moday = dayofweek.substring(0, dayofweek.indexOf(";"));
				String sunday = dayofweek.substring(dayofweek.length() - 11, dayofweek.length() - 1);
				row.createCell(0).setCellValue(weekofyear + " (" + moday + "��" + sunday + ")");
				row.getCell(0).setCellStyle(cstyle1);
				row.setHeight((short) (row.getHeight() * 3));
				continue;
			}
			if (a == 1) {
				row.createCell(0).setCellValue("��������");
				row.createCell(1).setCellValue("��¼����");
				row.createCell(2).setCellValue("��¼��������");
				row.createCell(3).setCellValue("����ʱ��");
				row.createCell(4).setCellValue("����ʱ������");

				row.getCell(0).setCellStyle(cstyle1);
				row.getCell(1).setCellStyle(cstyle1);
				row.getCell(2).setCellStyle(cstyle1);
				row.getCell(3).setCellStyle(cstyle1);
				row.getCell(4).setCellStyle(cstyle1);
				row.setHeight((short) (row.getHeight() * 2));
				continue;
			}

			String deptname = loginVO[a - 2].getStringValue("DEPARTNAME", "");//��������
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
