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
		btn_count = new WLTButton("����ͳ��");
		btn_count.addActionListener(this);
		btn_export = new WLTButton("����");
		btn_export.addActionListener(this);
		panel.addBatchBillListButton(new WLTButton[] { btn_count, btn_export });
		panel.getQuickQueryPanel().addBillQuickActionListener(this);
		panel.repaintBillListButton();
		panel2 = new BillListPanel("WN_ZNSHCOUNT_RESULT_CODE1");
		btn_zn_export = new WLTButton("����");// ��ũ�̻�ά������excel
		btn_zn_export.addActionListener(this);
		btn_zn_count = new WLTButton("��ũͳ��");
		btn_zn_count.addActionListener(this);
		// ��ũ�̻�ά������ͳ��
		panel2.addBatchBillListButton(new WLTButton[] { btn_zn_count,
				btn_zn_export });
		panel2.repaintBillListButton();
		panel2.getQuickQueryPanel().addBillQuickActionListener(this);
		tabPanel.addTab("��ԼС΢�̻�����ͳ��", panel);
		tabPanel.addTab("��ũ�̻�����ͳ��", panel2);
		tabPanel.addChangeListener(this);
		this.add(tabPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_count) {// ��ԼС΢����ͳ��
			try {
				RefItemVO refItemVO = new RefItemVO();
				// refItemVO.setId(report_date_new);
				RefDialog_Date date = new RefDialog_Date(this, "", refItemVO,
						null);
				/**
				 * ��ȡ���û�ѡ�е����ڣ� ÿ��ִ��SQLʱ�����������ѡ�����ڵĵ�ǰ���³�����ǰѡ������
				 * ��:�û�ѡ��2020-04-08�����������䶨�嵽2020-04-01~2020-04-08
				 */
				date.initialize();
				date.setVisible(true);// ��������ѡ���ɼ�
				if (date.getCloseType() == 1) {
					final RefItemVO ivo = date.getReturnRefItemVO();
					final String curSelectDate = ivo.getId();// ��ǰѡ������
					final String curSelectMonth = curSelectDate.substring(0, 7);
					String exisSQL = "select 1 from wn_tyxwcount_result where curmonth ='"
							+ curSelectMonth + "'";
					String[] exists = UIUtil.getStringArrayFirstColByDS(null,
							exisSQL);
					final TyxwcontentCount tyxw = new TyxwcontentCount();
					
					if (exists != null && exists.length > 0) {// �Ѿ������Ƿ����¼���
						
						if (MessageBox.confirm(this, "���ڡ�" + curSelectDate
								+ "��С΢��Լ�̻�ͳ����Ϣ�Ѿ����ڣ�ȷ���ظ�������")) {
							final String dateInterval = getDateInterVal(curSelectMonth
									+ "-01", curSelectDate, curSelectMonth);
							new SplashWindow(this, new AbstractAction() {
								public void actionPerformed(ActionEvent e) {
									// ��ԼС΢�̻�����
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
					} else {// ��ǰ��������û�����ݣ�����Ҫɾ��
						final String dateInterval = getDateInterVal(curSelectMonth
								+ "-01", curSelectDate, curSelectMonth);
						new SplashWindow(this, new AbstractAction() {
							public void actionPerformed(ActionEvent e) {
								// ��ԼС΢�̻�����
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
		} else if (e.getSource() == panel.getQuickQueryPanel()) {// ��ȡ����Ӧ������
			String curMonth = panel.getQuickQueryPanel().getRealValueAt(
					"CURMONTH");
			String sql = "select * from wn_tyxwcount_result where  1=1 ";
			if (!TBUtil.isEmpty(curMonth)) {
				sql = sql + " and curMonth ='"
						+ curMonth.replace("��", "-").replace("��;", "") + "'";
			}
			sql = sql + " order by curmonth desc ";
			panel.queryDataByDS(null, sql);
		} else if (e.getSource() == btn_export) {
			try {
				// ���õ����ļ�ѡ��Ի���
				chooser = new JFileChooser();
				final String templeName = panel.getTempletVO().getTempletname();// ��ȡ��ģ�������
				chooser.setSelectedFile(new File(templeName + ".xls"));
				int showOpenDialog = chooser.showOpenDialog(null);
				final String filePath;
				if (showOpenDialog == JFileChooser.APPROVE_OPTION) {// ѡ��򿪵��ļ�
					filePath = chooser.getSelectedFile().getAbsolutePath();
				} else {
					return;
				}
				new SplashWindow(this, new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						result = ImportExcel(panel, filePath, templeName,
								"��ԼС΢�̻�����");
					}
				});
				MessageBox.show(this, result);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (e.getSource() == btn_zn_count) { // ��ũ�̻�����ͳ��
			try {
				RefItemVO refItemVO = new RefItemVO();
				// refItemVO.setId(report_date_new);
				RefDialog_Date date = new RefDialog_Date(this, "", refItemVO,
						null);
				date.initialize();
				date.setVisible(true);// ��������ѡ���ɼ�
				final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
						.lookUpRemoteService(WnSalaryServiceIfc.class);
				if (date.getCloseType() == 1) {//
					final RefItemVO ivo = date.getReturnRefItemVO();
					/**
					 * ��ȡ����ǰѡ������
					 */
					final String curSelectDate = ivo.getId();// ��ǰѡ������
					final String curSelectMonth = curSelectDate.substring(0, 7);
					final String curSelectMonthStart = curSelectMonth + "-01";
					String existsSQL = "select 1 from wn_znshcount_result where curmonth='"
							+ curSelectMonth + "'";
					String[] existsNum = UIUtil.getStringArrayFirstColByDS(
							null, existsSQL);
			       
					if (existsNum.length <= 0) {// �������û�����ݣ�ֱ�Ӽ���
//						 final String dateInterVal = getDateInterVal(curSelectMonthStart, curSelectDate, curSelectMonthStart);
						new SplashWindow(this, new AbstractAction() {
							@Override
							public void actionPerformed(ActionEvent e) { // ִ����ũ�̻�ά������
								String dateInterVal = getDateInterVal(curSelectMonthStart, curSelectDate, curSelectMonth);
								message = service.znCount(curSelectMonthStart,
										curSelectDate, curSelectMonth,dateInterVal,false);
							}
						});
						panel2.QueryData("select * from wn_znshcount_result where CURMONTH='"
								+ curSelectMonth + "' ");
						MessageBox.show(this, message);
					} else {// ��ǰ�����µ������Ѿ����ڣ����û����¼���
						if (MessageBox.confirm(this, "���ڡ�" + curSelectDate
								+ "����ũ�̻�ͳ����Ϣ�Ѿ����ڣ�ȷ���ظ�������")) {
//							 final String dateInterVal = getDateInterVal(curSelectMonthStart, curSelectDate, curSelectMonthStart);
							new SplashWindow(this, new AbstractAction() {
								@Override
								public void actionPerformed(ActionEvent e) { // ִ����ũ�̻�ά������
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
			final String templeName = panel2.getTempletVO().getTempletname();// ��ȡ��ģ�������
			chooser.setSelectedFile(new File(templeName + ".xls"));
			int showOpenDialog = chooser.showOpenDialog(null);
			final String filePath;
			if (showOpenDialog == JFileChooser.APPROVE_OPTION) {// ѡ��򿪵��ļ�
				filePath = chooser.getSelectedFile().getAbsolutePath();
			} else {
				return;
			}
			new SplashWindow(this, new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					result = ImportExcel(panel2, filePath, templeName, "��ũ�̻�ά��");
				}
			});
			MessageBox.show(this, "����ũ�̻�ά���������ɹ�");
		} else if (e.getSource() == panel2.getQuickQueryPanel()) {
			String curMonth = panel2.getQuickQueryPanel().getRealValueAt(
					"curmonth");
			String sql = "select * from WN_ZNSHCOUNT_RESULT where  1=1 ";
			if (!TBUtil.isEmpty(curMonth)) {
				sql = sql + " and curmonth ='"
						+ curMonth.replace("��", "-").replace("��;", "") + "'";
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

			// ��ȡ����ͷ��Ϣ
			Pub_Templet_1_ItemVO[] templetItemVOs = panel.getTempletItemVOs();
			List<String> unShowList = new ArrayList<String>();
			Pub_Templet_1_ItemVO pub_Templet_1_ItemVO = null;
			List<String> colList = new ArrayList<String>();// ��ſ�����ʾ���ֶ�
			for (int i = 0, n = 0; i < templetItemVOs.length; i++) {
				pub_Templet_1_ItemVO = templetItemVOs[i];
				String cellKey = pub_Templet_1_ItemVO.getItemkey();
				String cellName = pub_Templet_1_ItemVO.getItemname();
				firstSheet.setColumnWidth(i, 25 * 256);
				if (pub_Templet_1_ItemVO.isListisshowable()) {
					firstCell = firstRow.createCell(i - n);
					firstCell.setCellValue(cellName);
					firstCell.setCellStyle(firstCellStyle);// ���ñ���ɫ�;�����ʾ
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
			// ��ѯ: and ((curmonth.curmonth>='2020-05-01' and
			// curmonth.curmonth<='2020-05-31 24:00:00'))

			if (!TBUtil.isEmpty(queryCondition)) {
				queryConditionSQL = queryConditionSQL
						+ " and  curmonth="
						+ queryCondition.substring(
								queryCondition.indexOf("=") + 1,
								queryCondition.indexOf("'") + 8) + "'";
			}
			// ��ȡ����ǰģ��Ĳ�ѯ����
			HashVO[] hashVos = UIUtil.getHashVoArrayByDS(null,
					queryConditionSQL);
			if (hashVos == null || hashVos.length == 0) {
				return "��ǰ��������Ҫ����";
			}
			Row nextRow = null;

			String cellValue = "";
			// �Գ���������������н��д���
			for (int i = 0; i < hashVos.length; i++) {
				nextRow = firstSheet.createRow(i + 1);
				String[] keys = hashVos[i].getKeys();// ����ǰ������ݿ��е�˳�����������е�
				System.out.println(Arrays.toString(keys));
				for (int j = 0, n = 0; j < colList.size(); j++) {// �Ե�ǰ����û��һ�����ݽ��д���
					if (unShowList.contains(colList.get(j).toUpperCase())) {
						n++;
						continue;
					}
					// �ӹ�����ÿһ������(��һ�������������ݱ��д洢����id����Ҫת����name�����)

					// ����(���� ������)
					cellValue = hashVos[i].getStringValue(colList.get(j));
					nextRow.createCell(j - n).setCellValue(cellValue);
				}
			}
			// �����ݽ��д���
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
			result = "���ݵ����ɹ�";
		} catch (Exception e) {
			result = "���ݵ���ʧ��";
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void stateChanged(ChangeEvent e) {

	}

	/**
	 * ��ȡ����ǰ���е���������
	 * 
	 * @return
	 */
	private String getDateInterVal(String curSelectMonthStart,
			String curSelectDate, String curSelectMonth) {
		String result = "";
		// ��ѯ��ǰ���е�ǰ��������������
		try {
			String maxDate = UIUtil
					.getStringValueByDS(
							null,
							"select max(to_char(to_date(biz_dt,'yyyy-mm-dd'),'yyyy-mm-dd')) from wnbank.t_dis_ifsp_intgr_txn_dtl where to_char(to_date(biz_dt,'yyyy-mm-dd'),'yyyy-mm-dd') like '"
									+ curSelectMonth + "%'");// ��ȡ����ǰ��������������
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			long maxMi = format.parse(maxDate).getTime();
			long curMi = format.parse(curSelectDate).getTime();
			if (maxMi < curMi) {// �����ǰѡ�����ڣ��������ݱ��е�������ڣ����������䶨λ ���³�-���ݱ����������
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