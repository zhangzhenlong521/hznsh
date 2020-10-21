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
	private BillListPanel listPanel, sonListPanel,dealListPanel,dkListPanel;// ����ģ��
	private JComboBox comboBox = null;// ��ѡ��
	private WLTButton importButton;// ���ݵ��밴ť
	private WLTButton sonexportButton;// ��ģ�������ݵ�����excel��ť
	private WLTButton exportButton;//
	private WLTButton checkButton;// ���ݴ���
	private WLTButton submitButton;// �ύ��ť
	private String message;// ��ʾ��Ϣ
	private JFileChooser fileChooser;
	private String username=ClientEnvironment.getInstance().getLoginUserName();//��ȡ����ǰ��¼������
	private String usercode=ClientEnvironment.getInstance().getLoginUserCode();//��ȡ����ǰ��¼�˹�Ա��

	@Override
	public void initialize() {// ��ʼ
		listPanel = new BillListPanel("WN_GATHER_MONITOR_RESULT_ZPY_Q01");
		sonListPanel = new BillListPanel("WN_CURRENT_DEAL_DATE_ZPY_Q01");
		dealListPanel=new BillListPanel("WN_DEAL_INFO_CODE_ZPY");
		dkListPanel=new BillListPanel("WN_DK_INFO_ZPY");
		comboBox = new JComboBox();
		listPanel.getTempletVO().getListrowheight();
		importButton = new WLTButton("Ա���쳣��Ϣ����");
		importButton.addActionListener(this);
		exportButton = new WLTButton("���ܽ������");
		exportButton.addActionListener(this);
		sonexportButton = new WLTButton("��ϸ���ݵ���");
		sonexportButton.addActionListener(this);
		checkButton = new WLTButton("Ա���쳣���ݴ���");
		checkButton.addActionListener(this);
		submitButton=new WLTButton("�ύ");
		submitButton.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] { importButton,
				exportButton, checkButton,submitButton});
		listPanel.repaintBillListButton();
		listPanel.addBillListHtmlHrefListener(this);
		listPanel.getQuickQueryPanel().addBillQuickActionListener(this);// ��д���ٲ�ѯ�ķ���
		listPanel.setRowNumberChecked(true);// ��������
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
		if (e.getSource() == importButton) {// Ա���쳣��Ϊ���ݵ���
			try {

				/**
				 * ���ݵ��� ѡ�������ڡ�
				 */
				RefItemVO refItemVO = new RefItemVO();
				RefDialog_Date date = new RefDialog_Date(this, "", refItemVO,
						null);
				date.initialize();
				date.setVisible(true);// ��������ѡ���ɼ�
				if(date.getCloseType()==1){// ����û�ȷ��ѡ������
					final RefItemVO ivo = date.getReturnRefItemVO();
					final String curSelectDate=ivo.getId();// ��ǰѡ������
					final String curSelectMonth=curSelectDate.substring(0,7);// ��ǰѡ����
					final  String curSelectMonthStart=curSelectMonth+"-01";//��ǰѡ�����ڵ��³�����
					final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
							.lookUpRemoteService(WnSalaryServiceIfc.class);
					// �����жϵ�ǰ�����µ������Ƿ����
					String existsSQL="select 1 from WN_GATHER_MONITOR_RESULT where dat_txn='"+curSelectMonth+"'";
					String[] existsArray = UIUtil.getStringArrayFirstColByDS(null, existsSQL);
					if(existsArray!=null&& existsArray.length>0){ //��ǰ�������е������Ѿ�����
						if(MessageBox.confirm(this,"��ǰ�����¡�"+curSelectMonth+"�������Ѿ����ڣ��Ƿ����µ���?")){
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
		} else if (e.getSource() == sonexportButton) {// ���ݵ�����excel����
			try {
				sonListPanel.exportToExcel();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		} else if (e.getSource() == checkButton) {
			try {
				final BillVO[] billVos = listPanel.getCheckedBillVOs();
				if (billVos == null || billVos.length <= 0) {
					MessageBox.show(this, "��ѡ��һ�����߶������ݽ��д���");
					return;
				}
				String dealMessage = "";
//				for (int i = 0; i < billVos.length; i++) {
//					if (!billVos[i].getStringValue("deal_result").equals("δ����")) {
//						dealMessage = dealMessage
//								+ billVos[i].getStringValue("NAME") + ",";
//					}
//				}
				for (int i = 0; i < billVos.length; i++) {
					if(billVos[i].getStringValue("status").equals("���ύ")){
						dealMessage = dealMessage
								+ billVos[i].getStringValue("NAME") + ",";
					}
				}
				if (!"".equals(dealMessage)) {
					dealMessage = dealMessage.substring(0,
							dealMessage.lastIndexOf(","));
					MessageBox.show(this, "��ǰѡ�С�" + dealMessage
							+ "��Ա���쳣�������ύ�������ظ�����");
					return;
				}
				final BillCardDialog cardDialog = new BillCardDialog(this,
						"Ա���쳣��Ϣ����", "WN_CURRENT_CHECK_RESULT_ZPY_Q01", 600, 300);
				cardDialog.getBillcardPanel().setEditable("CHECK_RESULT", true);
				cardDialog.getBillcardPanel().setEditable("CHECK_REASON", true);
				cardDialog.getBtn_save().setVisible(false);
				final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
						.lookUpRemoteService(WnSalaryServiceIfc.class);
				cardDialog.getBtn_confirm().addActionListener(
						new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								Map<String, String> paraMap = new HashMap<String, String>(); // ����ֻ��¼�������
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
								System.out.println("����:"+cardDialog.getCardItemValue("APPTH"));
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
		} else if (e.getSource() == exportButton) {// ���ܽ������
			try {
				String querySQLCondition = listPanel.getQuickQueryPanel()
						.getQuerySQLCondition();// ��ȡ����ǰ��ѯ����
				String sql = "select * from WN_GATHER_MONITOR_RESULT where 1=1 ";
				if (WnUtils.isEmpty(querySQLCondition)) {//
					sql = sql
							+ querySQLCondition.replaceAll(";", "")
									.replaceAll("��", "-").replaceAll("��", "");
				}
				fileChooser = new JFileChooser();
				String templetName = listPanel.getTempletVO().getTempletname();// ��ȡ��ģ������
				fileChooser.setSelectedFile(new File(templetName + ".xls"));
				int showOpenDialog = fileChooser.showOpenDialog(null);
				final String filePath;
				if (showOpenDialog == JFileChooser.APPROVE_OPTION) {// ѡ����ļ�
					filePath = fileChooser.getSelectedFile().getAbsolutePath();
				} else {
					return;
				}
				final String selectSQL = sql;
				new SplashWindow(this, new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						message = createExcel(listPanel, filePath, selectSQL,
								"Ա���쳣��Ϊ�������");
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
						.replaceAll("��", "-").replaceAll("��", "");
				String querySQL = "select * from WN_GATHER_MONITOR_RESULT where 1=1 "
						+ queryCondition;
				listPanel.QueryData(querySQL);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		} else  if(e.getSource() == submitButton){ // �ύ��ť
			// ���ύ����ʱ�����뱣֤��ǰ�����Ѿ�����δ����״̬�µ������ֹ�ύ
			BillVO[] vos = listPanel.getCheckedBillVOs();
			StringBuilder message= new  StringBuilder("");
			for (int i = 0; i < vos.length; i++) {// ѭ��������������
				if("δ����".equals(vos[i].getStringValue("DEAL_RESULT"))){// ��ǰԱ��δ����
					message.append(vos[i].getStringValue("name")+" ");
				}else {
					continue;
				}
			}
			if(message.length()>0){
				MessageBox.show(this,"����Ա����"+message.toString()+"����δ����������ѡ��!!!");
				return;
			}
			// ��ʼ����
			try{
				// ��ȡ����ǰ����
				UpdateSQLBuilder resultUpdate=new  UpdateSQLBuilder("WN_GATHER_MONITOR_RESULT");
				UpdateSQLBuilder monitorUpdate=new UpdateSQLBuilder("WN_DEAL_MONITOR");
				List<String> list=new ArrayList<String>();
				for (int i = 0; i < vos.length; i++) { // �ύ����
					resultUpdate.setWhereCondition("id="+vos[i].getStringValue("id"));
					resultUpdate.putFieldValue("status", "���ύ");
					resultUpdate.putFieldValue("submit_person_code",usercode );
					resultUpdate.putFieldValue("submit_person_name", username);
					list.add(resultUpdate.getSQL());
					monitorUpdate.setWhereCondition("monitor_id="+vos[i].getStringValue("id"));
					monitorUpdate.putFieldValue("status", "���ύ");
					monitorUpdate.putFieldValue("submit_person_code",usercode);
					monitorUpdate.putFieldValue("submit_person_name",username);
					list.add(monitorUpdate.getSQL());
				}
				UIUtil.executeBatchByDS(null, list);//ִ���ύ�������޸�״̬
				listPanel.refreshData();
				MessageBox.show(this,"Ա���쳣��Ϣ�ύ�ɹ�");
			}catch(Exception ex){// �����쳣
				MessageBox.show(this,"Ա���쳣��Ϣ�ύʧ��");
			}
		}
	}

	// excel ��������
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
			
			// ��ȡ����ͷ��Ϣ
			Pub_Templet_1_ItemVO[] templetItemVOs = listPanel
					.getTempletItemVOs();
			List<String> unShowList = new ArrayList<String>();
			Pub_Templet_1_ItemVO pub_Templet_1_ItemVO = null;
		    List<String> colList = new ArrayList<String>();// ��ſ�����ʾ���ֶ�
			for (int i = 0, n = 0; i < templetItemVOs.length; i++) {
				pub_Templet_1_ItemVO = templetItemVOs[i];
				String cellKey = pub_Templet_1_ItemVO.getItemkey();
				String cellName = pub_Templet_1_ItemVO.getItemname();
				firstSheet.setColumnWidth(i,25*256);
				if (pub_Templet_1_ItemVO.isListisshowable()) {
					 firstCell=firstRow.createCell(i-n);
					firstCell.setCellValue(cellName);
					 firstCell.setCellStyle(firstCellStyle);//���ñ���ɫ�;�����ʾ
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
//			//��ѯ: and ((curmonth.curmonth>='2020-05-01' and curmonth.curmonth<='2020-05-31 24:00:00'))
//
//			if(!TBUtil.isEmpty(queryCondition)) {
//				queryConditionSQL=queryConditionSQL+" and  curmonth="+queryCondition.substring(queryCondition.indexOf("=")+1,queryCondition.indexOf("'")+8)+"'";
//			}
			// ��ȡ����ǰģ��Ĳ�ѯ����
			HashVO[] hashVos = UIUtil.getHashVoArrayByDS(null,
					selectSQL);
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
						cellValue = hashVos[i].getStringValue(colList.get(j),"");
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
				filename = "Ա���쳣���ݵ���" + i + ".xls";
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
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent event) {

		if (event.getSource() == listPanel) {
//			BillVO vo = listPanel.getSelectedBillVO();
//			BillListDialog dialog = new BillListDialog(this, "�鿴", sonListPanel);
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
			// ��ȡ����ǰ�û�������ֶ�
			String itemKey=event.getItemkey();
			if("AMT_TXN_SUM".equalsIgnoreCase(itemKey)){ //������ǽ��׽��
				dialog=new BillListDialog(this, "Ա����������", dealListPanel);
				dialog.getBilllistPanel().QueryDataByCondition(" DAT_TXN2 LIKE '"+vo.getStringValue("DAT_TXN")+"%' AND CARDID='"+vo.getStringValue("EXTERNAL_CUSTOMER_IC")+"'");
			}else if("loan_balance".equalsIgnoreCase(itemKey)){//������Ǵ�����
				dialog=new BillListDialog(this, "Ա����������", dkListPanel);
				dialog.getBilllistPanel().QueryDataByCondition(" DKDATE2 LIKE '"+vo.getStringValue("DAT_TXN")+"%' AND CARDID='"+vo.getStringValue("EXTERNAL_CUSTOMER_IC")+"'");
			}else if("cod_drcr_c".equals(itemKey)){// ����
				dialog=new BillListDialog(this, "Ա����������", dealListPanel);
				dialog.getBilllistPanel().QueryDataByCondition(" DAT_TXN2 LIKE '"+vo.getStringValue("DAT_TXN")+"%' AND CARDID='"+vo.getStringValue("EXTERNAL_CUSTOMER_IC")+"' and cod_drcr='C'");
			}else  if("cod_drcr_d".equals(itemKey)){//֧��
				dialog=new BillListDialog(this, "Ա����������", dealListPanel);
				dialog.getBilllistPanel().QueryDataByCondition(" DAT_TXN2 LIKE '"+vo.getStringValue("DAT_TXN")+"%' AND CARDID='"+vo.getStringValue("EXTERNAL_CUSTOMER_IC")+"' and cod_drcr='D'");
			}
			dialog.getBtn_confirm().setVisible(false);
			dialog.setVisible(true);
		}
	}
}
