package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.pushworld.wn.to.WnUtils;

public class StaffMonitorNewShowWKPanel extends AbstractWorkPanel implements
		ActionListener,BillListHtmlHrefListener {
	private BillListPanel listPanel,dealListPanel;
	private WLTButton importButton;
	private WLTButton dealButton;// 审批按钮
	private JFileChooser fileChooser;
	private String message;
	private JComboBox comboBox = null;// 复选框
	private String username = ClientEnvironment.getInstance()
			.getLoginUserName();// 获取到当前登录人姓名
	private String usercode = ClientEnvironment.getInstance()
			.getLoginUserCode();// 获取到当前登录人柜员号

	@Override
	public void initialize() {
		listPanel = new BillListPanel("WN_DEAL_MONITOR_NEW_ZPY_Q01");
		importButton = new WLTButton("异常处理结果导出");
		importButton.addActionListener(this);
		dealButton = new WLTButton("审批");
		dealButton.addActionListener(this);
		listPanel.setRowNumberChecked(true);
		listPanel.addBatchBillListButton(new WLTButton[] { importButton,
				dealButton });
		dealListPanel=new BillListPanel("WN_DEAL_INFO_CODE_ZPY");
		listPanel.addBillListHtmlHrefListener(this);// 增加监听事件
		listPanel.repaintBillListButton();
		this.add(listPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == importButton) {
			try {
				String querySQLCondition = listPanel.getQuickQueryPanel()
						.getQuerySQLCondition();// 获取到当前查询条件
				String sql = "select * from wn_deal_monitor where 1=1 ";
				if (WnUtils.isEmpty(querySQLCondition)) {//
					sql = sql + querySQLCondition;
				}
				MessageBox.show(this, sql);
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
								"员工异常行为处理结果");
					}
				});
				MessageBox.show(this, message);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		} else if (e.getSource() == dealButton) { // 审批按钮
			BillVO[] vos = listPanel.getCheckedBillVOs();
			if (vos == null || vos.length <= 0) {
				MessageBox.show(this, "请选中一条员工异常数据进行处理！！！");
				return;
			}
			/**
			 * 除了修改状态意外，还要增加审批人信息
			 */
			// 是否退回
			int option = MessageBox.showOptionDialog(listPanel, "请处理当前选中任务",
					"提示", new String[] { "完成", "退回" });
			UpdateSQLBuilder resultUpdate = new UpdateSQLBuilder(
					"WN_GATHER_MONITOR_RESULT");
			UpdateSQLBuilder monitorUpdate = new UpdateSQLBuilder(
					"WN_DEAL_MONITOR");
			List<String> list = new ArrayList<String>();
			try {
				if (option == 0) {// 完成
					for (int i = 0; i < vos.length; i++) {
						resultUpdate.setWhereCondition("id="
								+ vos[i].getStringValue("MONITOR_ID"));
						resultUpdate.putFieldValue("status", "已完成");
						list.add(resultUpdate.getSQL());
						monitorUpdate.setWhereCondition("id="
								+ vos[i].getStringValue("id"));
						monitorUpdate.putFieldValue("status", "已完成");
						monitorUpdate.putFieldValue("approve_person_code",
								usercode);
						monitorUpdate.putFieldValue("approve_person_name",
								username);
						list.add(monitorUpdate.getSQL());
					}
				} else if (option == 1) {// 退回
					for (int i = 0; i < vos.length; i++) {
						resultUpdate.setWhereCondition("id="
								+ vos[i].getStringValue("monitor_id"));
						resultUpdate.putFieldValue("status", "已退回");
						list.add(resultUpdate.getSQL());
						monitorUpdate.setWhereCondition("id="
								+ vos[i].getStringValue("id"));
						monitorUpdate.putFieldValue("status", "已退回");
						monitorUpdate.putFieldValue("approve_person_code",
								usercode);
						monitorUpdate.putFieldValue("approve_person_name",
								username);
						list.add(monitorUpdate.getSQL());
					}
				}
				UIUtil.executeBatchByDS(null, list);
				MessageBox.show(this, "员工异常信息审核成功");
			} catch (Exception e2) {
				MessageBox.show(this, "员工异常信息审核失败");
			}
			listPanel.refreshData();
		}
	}

	// excel 导出操作
	public String createExcel(BillListPanel listPanel, String filePath,
			String selectSQL, String sheetName) {
		String result = "";
		try {
			String templetName = listPanel.getTempletVO().getTempletname();// 获取到模板名称
			Workbook monitorResult = new SXSSFWorkbook(100);
			Sheet firstSheet = monitorResult.createSheet(sheetName);// 创建sheet
			Row firstRow = firstSheet.createRow(0);// 创建首行
			// 获取到表头信息
			Pub_Templet_1_ItemVO[] templetItemVOs = listPanel
					.getTempletItemVOs();
			List<String> unShowList = new ArrayList<String>();
			for (int i = 0, n = 0; i < templetItemVOs.length; i++) {
				Pub_Templet_1_ItemVO pub_Templet_1_ItemVO = templetItemVOs[i];
				String cellKey = pub_Templet_1_ItemVO.getItemkey();
				String cellName = pub_Templet_1_ItemVO.getItemname();
				if (pub_Templet_1_ItemVO.isListisshowable()) {
					firstRow.createCell(i - n).setCellValue(cellName);
				} else {
					unShowList.add(cellKey);
					n++;
				}
			}
			HashVO[] hashVos = UIUtil.getHashVoArrayByDS(null, selectSQL);
			Row nextRow = null;
			for (int i = 0; i < hashVos.length; i++) {
				nextRow = firstSheet.createRow(i + 1);// 创建下一行数据
				String[] keys = hashVos[i].getKeys();
				for (int j = 0, n = 0; j < keys.length; j++) {// 对每一行数据进行处理
					if (unShowList.contains(keys[j].toUpperCase())) {// 判断当前列是否已经隐藏
						n++;
						continue;
					}
					nextRow.createCell(j - n).setCellValue(
							hashVos[i].getStringValue(keys[j]));
				}
			}
			String fileName = filePath.substring(
					filePath.lastIndexOf("\\") + 1, filePath.lastIndexOf("."));
			filePath = filePath.substring(0, filePath.lastIndexOf("\\"));
			File file = new File(filePath + "/" + fileName + ".xls");
			int i = 1;
			while (file.exists()) {
				fileName = templetName + i + ".xls";
				file = new File(filePath + "/" + fileName);
				i++;
			}
			file.createNewFile();
			FileOutputStream fout = new FileOutputStream(file.getAbsolutePath());
			monitorResult.write(fout);
			fout.close();
			result = "导出成功";
		} catch (Exception e) {
			result = "导出失败";
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent event) {

		BillListDialog dialog=null;
		BillVO vo = listPanel.getSelectedBillVO();
		// 获取到当前用户点击的字段
		String itemKey=event.getItemkey();
		if("AMT_TXN".equalsIgnoreCase(itemKey)){ //点击的是交易金额
			dialog=new BillListDialog(this, "员工交易数据", dealListPanel);
			dialog.getBilllistPanel().QueryDataByCondition(" DAT_TXN2 LIKE '"+vo.getStringValue("DAT_TXN")+"%' AND CARDID='"+vo.getStringValue("EXTERNAL_CUSTOMER_IC")+"'");// 查询条件是交易月份和身份证号
		}
		else if("cod_drcr_c".equals(itemKey)){// 收入
			dialog=new BillListDialog(this, "员工交易数据", dealListPanel);
			dialog.getBilllistPanel().QueryDataByCondition(" DAT_TXN2 LIKE '"+vo.getStringValue("DAT_TXN")+"%' AND CARDID='"+vo.getStringValue("EXTERNAL_CUSTOMER_IC")+"' and cod_drcr='C'");
		}else  if("cod_drcr_d".equals(itemKey)){
			dialog=new BillListDialog(this, "员工交易数据", dealListPanel);
			dialog.getBilllistPanel().QueryDataByCondition(" DAT_TXN2 LIKE '"+vo.getStringValue("DAT_TXN")+"%' AND CARDID='"+vo.getStringValue("EXTERNAL_CUSTOMER_IC")+"' and cod_drcr='D'");
		}
		dialog.getBtn_confirm().setVisible(false);
		dialog.setVisible(true);
	}
}