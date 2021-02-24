package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import org.apache.axis.encoding.ser.ArraySerializer;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.RefDialog_Date;
import cn.com.pushworld.wn.to.WnUtils;

public class StaffMonitorNewWKPanel extends AbstractWorkPanel implements
		ActionListener, BillListHtmlHrefListener {
	private BillListPanel listPanel, sonListPanel,dealListPanel,dkListPanel;// 创建模板
	private JComboBox comboBox = null;// 复选框
	private WLTButton importButton;// 数据导入按钮
	private WLTButton sonexportButton;// 子模板上数据导出成excel按钮
	private WLTButton exportButton;//
	private WLTButton checkButton;// 数据处理
	private WLTButton submitButton;// 提交按钮
	private String message;// 提示信息
	private JFileChooser fileChooser;
	private String username=ClientEnvironment.getInstance().getLoginUserName();//获取到当前登录人姓名
	private String usercode=ClientEnvironment.getInstance().getLoginUserCode();//获取到当前登录人柜员号

	@Override
	public void initialize() {// 初始
		listPanel = new BillListPanel("WN_GATHER_MONITOR_RESULT_ZPY_Q01");
		sonListPanel = new BillListPanel("WN_CURRENT_DEAL_DATE_ZPY_Q01");
		dealListPanel=new BillListPanel("WN_DEAL_INFO_CODE_ZPY");
		dkListPanel=new BillListPanel("WN_DK_INFO_ZPY");
		comboBox = new JComboBox();
		listPanel.getTempletVO().getListrowheight();
		importButton = new WLTButton("员工异常信息导入");
		importButton.addActionListener(this);
		exportButton = new WLTButton("汇总结果导出");
		exportButton.addActionListener(this);
		sonexportButton = new WLTButton("明细数据导出");
		sonexportButton.addActionListener(this);
		checkButton = new WLTButton("员工异常数据处理");
		checkButton.addActionListener(this);
		submitButton=new WLTButton("提交");
		submitButton.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] { importButton,
				exportButton, checkButton,submitButton});
		listPanel.repaintBillListButton();
		listPanel.addBillListHtmlHrefListener(this);
		listPanel.getQuickQueryPanel().addBillQuickActionListener(this);// 重写快速查询的方法
		listPanel.setRowNumberChecked(true);// 设置启动
		sonListPanel
				.addBatchBillListButton(new WLTButton[] { sonexportButton });
		dealListPanel.addBatchBillListButton(new WLTButton[]{sonexportButton});
		dealListPanel.repaintBillListButton();
		dkListPanel.addBatchBillListButton(new WLTButton[]{sonexportButton});
		dkListPanel.repaintBillListButton();
		sonListPanel.repaintBillListButton();
		this.add(listPanel);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == importButton) {// 员工异常行为数据导入
			try {

				/**
				 * 数据导入 选择导入日期。
				 */
				RefItemVO refItemVO = new RefItemVO();
				RefDialog_Date date = new RefDialog_Date(this, "", refItemVO,
						null);
				date.initialize();
				date.setVisible(true);// 设置日期选择框可见
				if(date.getCloseType()==1){// 如果用户确定选中日期
					final RefItemVO ivo = date.getReturnRefItemVO();
					final String curSelectDate=ivo.getId();// 当前选中日期
					final String curSelectMonth=curSelectDate.substring(0,7);// 当前选中月
					final  String curSelectMonthStart=curSelectMonth+"-01";//当前选中日期的月初日期
					final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
							.lookUpRemoteService(WnSalaryServiceIfc.class);
					// 首先判断当前考核月的数据是否存在
					String existsSQL="select 1 from WN_GATHER_MONITOR_RESULT where dat_txn='"+curSelectMonth+"'";
					String[] existsArray = UIUtil.getStringArrayFirstColByDS(null, existsSQL);
					if(existsArray!=null&& existsArray.length>0){ //当前考核月中的数据已经存在
						if(MessageBox.confirm(this,"当前考核月【"+curSelectMonth+"】数据已经存在，是否重新导入?")){
							new SplashWindow(this, new AbstractAction() {
								@Override
								public void actionPerformed(ActionEvent e) {
									message = service.importMonitorData(curSelectDate,curSelectMonthStart,curSelectMonth,true);
								}
							});
						}else {
							return;
						}
					}else {
						new SplashWindow(this, new AbstractAction() {
							@Override
							public void actionPerformed(ActionEvent e) {
								message = service.importMonitorData(curSelectDate,curSelectMonthStart,curSelectMonth,false);
							}
						});
					}
					
				}
				listPanel.refreshCurrData();
				
				MessageBox.show(this, message);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		} else if (e.getSource() == sonexportButton) {// 数据导出成excel操作
			try {
				sonListPanel.exportToExcel();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		} else if (e.getSource() == checkButton) {
			try {
				final BillVO[] billVos = listPanel.getCheckedBillVOs();
				if (billVos == null || billVos.length <= 0) {
					MessageBox.show(this, "请选中一条或者多条数据进行处理");
					return;
				}
				String dealMessage = "";
//				for (int i = 0; i < billVos.length; i++) {
//					if (!billVos[i].getStringValue("deal_result").equals("未处理")) {
//						dealMessage = dealMessage
//								+ billVos[i].getStringValue("NAME") + ",";
//					}
//				}
				for (int i = 0; i < billVos.length; i++) {
					if(billVos[i].getStringValue("status").equals("已提交")){
						dealMessage = dealMessage
								+ billVos[i].getStringValue("NAME") + ",";
					}
				}
				if (!"".equals(dealMessage)) {
					dealMessage = dealMessage.substring(0,
							dealMessage.lastIndexOf(","));
					MessageBox.show(this, "当前选中【" + dealMessage
							+ "】员工异常数据已提交，请勿重复操作");
					return;
				}
				final BillCardDialog cardDialog = new BillCardDialog(this,
						"员工异常信息处理", "WN_CURRENT_CHECK_RESULT_ZPY_Q01", 600, 300);
				cardDialog.getBillcardPanel().setEditable("CHECK_RESULT", true);
				cardDialog.getBillcardPanel().setEditable("CHECK_REASON", true);
				cardDialog.getBtn_save().setVisible(false);
				final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
						.lookUpRemoteService(WnSalaryServiceIfc.class);
				cardDialog.getBtn_confirm().addActionListener(
						new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								Map<String, String> paraMap = new HashMap<String, String>(); // 这里只记录处理情况
								paraMap.put("CHECK_RESULT", cardDialog
										.getCardItemValue("CHECK_RESULT"));
								paraMap.put("CHECK_REASON", cardDialog
										.getCardItemValue("CHECK_REASON"));
								paraMap.put("CHECK_USERCODE", cardDialog
										.getCardItemValue("CHECK_USERCODE"));
								paraMap.put("CHECK_USERNAME", cardDialog
										.getCardItemValue("CHECK_USERNAME"));
								paraMap.put("CHECK_DATE", cardDialog
										.getCardItemValue("CHECK_DATE"));
								System.out.println("附件:"+cardDialog.getCardItemValue("APPTH"));
								paraMap.put("APPTH", cardDialog.getCardItemValue("APPTH"));
								message = service.dealExceptionData(billVos,
										paraMap);
								cardDialog.closeMe();
							}
						});
				cardDialog.setVisible(true);
				MessageBox.show(this, message);
				listPanel.refreshData();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		} else if (e.getSource() == exportButton) {// 汇总结果导出
			try {
				String querySQLCondition = listPanel.getQuickQueryPanel()
						.getQuerySQLCondition();// 获取到当前查询条件
				String sql = "select * from WN_GATHER_MONITOR_RESULT where 1=1 ";
				if (WnUtils.isEmpty(querySQLCondition)) {//
					sql = sql
							+ querySQLCondition.replaceAll(";", "")
									.replaceAll("年", "-").replaceAll("月", "");
				}
				fileChooser = new JFileChooser();
				String templetName = listPanel.getTempletVO().getTempletname();// 获取到模板名称
				fileChooser.setSelectedFile(new File(templetName + ".xls"));
				int showOpenDialog = fileChooser.showOpenDialog(null);
				final String filePath;
				if (showOpenDialog == JFileChooser.APPROVE_OPTION) {// 选择打开文件
					filePath = fileChooser.getSelectedFile().getAbsolutePath();
				} else {
					return;
				}
				final String selectSQL = sql;
				new SplashWindow(this, new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						message = createExcel(listPanel, filePath, selectSQL,
								"员工异常行为监测数据");
					}
				});
				MessageBox.show(this, message);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		} else if (e.getSource() == listPanel.getQuickQueryPanel()) {
			try {
				String queryCondition = listPanel.getQuickQueryPanel()
						.getQuerySQLCondition().replaceAll(";", "")
						.replaceAll("年", "-").replaceAll("月", "");
				String querySQL = "select * from WN_GATHER_MONITOR_RESULT where 1=1 "
						+ queryCondition;
				listPanel.QueryData(querySQL);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		} else  if(e.getSource() == submitButton){ // 提交按钮
			// 在提交任务时，必须保证当前任务已经处理，未处理状态下的任务禁止提交
			BillVO[] vos = listPanel.getCheckedBillVOs();
			StringBuilder message= new  StringBuilder("");
			for (int i = 0; i < vos.length; i++) {// 循环遍历整个任务
				if("未处理".equals(vos[i].getStringValue("DEAL_RESULT"))){// 当前员工未处理
					message.append(vos[i].getStringValue("name")+" ");
				}else {
					continue;
				}
			}
			if(message.length()>0){
				MessageBox.show(this,"存在员工【"+message.toString()+"】尚未处理，请重新选择!!!");
				return;
			}
			// 开始处理
			try{
				// 获取到当前数据
				UpdateSQLBuilder resultUpdate=new  UpdateSQLBuilder("WN_GATHER_MONITOR_RESULT");
				UpdateSQLBuilder monitorUpdate=new UpdateSQLBuilder("WN_DEAL_MONITOR");
				List<String> list=new ArrayList<String>();
				for (int i = 0; i < vos.length; i++) { // 提交任务
					resultUpdate.setWhereCondition("id="+vos[i].getStringValue("id"));
					resultUpdate.putFieldValue("status", "已提交");
					resultUpdate.putFieldValue("submit_person_code",usercode );
					resultUpdate.putFieldValue("submit_person_name", username);
					list.add(resultUpdate.getSQL());
					monitorUpdate.setWhereCondition("monitor_id="+vos[i].getStringValue("id"));
					monitorUpdate.putFieldValue("status", "已提交");
					monitorUpdate.putFieldValue("submit_person_code",usercode);
					monitorUpdate.putFieldValue("submit_person_name",username);
					list.add(monitorUpdate.getSQL());
				}
				UIUtil.executeBatchByDS(null, list);//执行提交操作，修改状态
				listPanel.refreshData();
				MessageBox.show(this,"员工异常信息提交成功");
			}catch(Exception ex){// 出现异常
				MessageBox.show(this,"员工异常信息提交失败");
			}
		}
	}

	// excel 导出操作
	public String createExcel(BillListPanel listPanel, String filePath,
			String selectSQL, String sheetName) {
		String result = "";
		try {
			Workbook monitorBook = new SXSSFWorkbook(100);
			Sheet firstSheet = monitorBook.createSheet(sheetName);
			Row firstRow = firstSheet.createRow(0);
			 Cell firstCell = null;
			 CellStyle firstCellStyle = monitorBook.createCellStyle();
			 firstCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			
			// 获取到表头信息
			Pub_Templet_1_ItemVO[] templetItemVOs = listPanel
					.getTempletItemVOs();
			List<String> unShowList = new ArrayList<String>();
			Pub_Templet_1_ItemVO pub_Templet_1_ItemVO = null;
		    List<String> colList = new ArrayList<String>();// 存放可以显示的字段
			for (int i = 0, n = 0; i < templetItemVOs.length; i++) {
				pub_Templet_1_ItemVO = templetItemVOs[i];
				String cellKey = pub_Templet_1_ItemVO.getItemkey();
				String cellName = pub_Templet_1_ItemVO.getItemname();
				firstSheet.setColumnWidth(i,25*256);
				if (pub_Templet_1_ItemVO.isListisshowable()) {
					 firstCell=firstRow.createCell(i-n);
					firstCell.setCellValue(cellName);
					 firstCell.setCellStyle(firstCellStyle);//设置背景色和居中显示
					colList.add(cellKey);
				} else {
					unShowList.add(cellKey.toUpperCase());
					n++;
				}
			}
//			if (queryConditionSQL == null || "".equals(queryConditionSQL)) {
//				queryConditionSQL = "select * from "+panel.getTempletVO().getSavedtablename()+" where 1=1 ";
//			}
//			String queryCondition=panel.getQuickQueryPanel().getQuerySQLCondition("curmonth");
//			//查询: and ((curmonth.curmonth>='2020-05-01' and curmonth.curmonth<='2020-05-31 24:00:00'))
//
//			if(!TBUtil.isEmpty(queryCondition)) {
//				queryConditionSQL=queryConditionSQL+" and  curmonth="+queryCondition.substring(queryCondition.indexOf("=")+1,queryCondition.indexOf("'")+8)+"'";
//			}
			// 获取到当前模板的查询条件
			HashVO[] hashVos = UIUtil.getHashVoArrayByDS(null,
					selectSQL);
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
						cellValue = hashVos[i].getStringValue(colList.get(j),"");
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
				filename = "员工异常数据导出" + i + ".xls";
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
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent event) {

		if (event.getSource() == listPanel) {
//			BillVO vo = listPanel.getSelectedBillVO();
//			BillListDialog dialog = new BillListDialog(this, "查看", sonListPanel);
//
//			dialog.getBilllistPanel().QueryDataByCondition(
//					"EXTERNAL_CUSTOMER_IC='"
//							+ vo.getStringValue("EXTERNAL_CUSTOMER_IC")
//							+ "' and DAT_TXN LIKE '"
//							+ vo.getStringValue("DAT_TXN") + "%'");
//			dialog.getBtn_confirm().setVisible(false);
//			dialog.setVisible(true);
			BillListDialog dialog=null;
			BillVO vo = listPanel.getSelectedBillVO();
			// 获取到当前用户点击的字段
			String itemKey=event.getItemkey();
			if("AMT_TXN_SUM".equalsIgnoreCase(itemKey)){ //点击的是交易金额
				dialog=new BillListDialog(this, "员工交易数据", dealListPanel);
				dialog.getBilllistPanel().QueryDataByCondition(" DAT_TXN2 LIKE '"+vo.getStringValue("DAT_TXN")+"%' AND CARDID='"+vo.getStringValue("EXTERNAL_CUSTOMER_IC")+"'");
			}else if("loan_balance".equalsIgnoreCase(itemKey)){//点击的是贷款金额
				dialog=new BillListDialog(this, "员工贷款数据", dkListPanel);
				dialog.getBilllistPanel().QueryDataByCondition(" DKDATE2 LIKE '"+vo.getStringValue("DAT_TXN")+"%' AND CARDID='"+vo.getStringValue("EXTERNAL_CUSTOMER_IC")+"'");
			}else if("cod_drcr_c".equals(itemKey)){// 收入
				dialog=new BillListDialog(this, "员工交易数据", dealListPanel);
				dialog.getBilllistPanel().QueryDataByCondition(" DAT_TXN2 LIKE '"+vo.getStringValue("DAT_TXN")+"%' AND CARDID='"+vo.getStringValue("EXTERNAL_CUSTOMER_IC")+"' and cod_drcr='C'");
			}else  if("cod_drcr_d".equals(itemKey)){//支出
				dialog=new BillListDialog(this, "员工交易数据", dealListPanel);
				dialog.getBilllistPanel().QueryDataByCondition(" DAT_TXN2 LIKE '"+vo.getStringValue("DAT_TXN")+"%' AND CARDID='"+vo.getStringValue("EXTERNAL_CUSTOMER_IC")+"' and cod_drcr='D'");
			}
			dialog.getBtn_confirm().setVisible(false);
			dialog.setVisible(true);
		}
	}
}
