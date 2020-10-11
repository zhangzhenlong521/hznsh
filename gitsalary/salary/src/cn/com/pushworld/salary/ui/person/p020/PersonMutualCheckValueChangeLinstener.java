package cn.com.pushworld.salary.ui.person.p020;

import java.util.Timer;
import java.util.TimerTask;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.to.mdata.jepfunctions.ItemValueListener;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.pushworld.salary.to.SalaryTBUtil;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

/**
 * ��Ա�������ֱ������ֵ�任���¼���
 * 
 * @author haoming create by 2013-7-16
 */
public class PersonMutualCheckValueChangeLinstener implements ItemValueListener {

	public synchronized void process(BillPanel panel) {
		if (panel instanceof BillCellPanel) {
			BillCellPanel cellPanel = (BillCellPanel) panel;
			final PersonMutualWKPanel wkpanel = (PersonMutualWKPanel) cellPanel.getClientProperty("this"); // �õ�this����
			if (wkpanel == null) {
				MessageBox.show(cellPanel, "����BillListPanel������putClientProperTy(\"this\",class)");
				return;
			}
			int row = cellPanel.getTable().getSelectedRow();
			int col = cellPanel.getTable().getSelectedColumn();
			BillCellItemVO itemVO = cellPanel.getBillCellItemVOAt(row, col);
			String value = itemVO.getCellvalue();
			int[] range = wkpanel.getInputNumRange();
			try {
				float val = 0;
				if (value != null && !"".equals(value)) {
					val = Float.parseFloat(value);
				}
				if (val < range[0] || val > range[1]) { // ��������ֵ���ٷ�Χ֮�䡣
					itemVO.setCellvalue("��Χ" + range[0] + "-" + range[1]);
					itemVO.setBackground("246,53,89"); // ��ɫԤ��
					return;
				} else {
					itemVO.setBackground("255,255,255"); // ����������ɫ��
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}

			final UpdateSQLBuilder sqlBuilder = new UpdateSQLBuilder("sal_person_check_score");
			sqlBuilder.putFieldValue("checkscore", value);
			try {
				sqlBuilder.putFieldValue("lasteditdate", UIUtil.getServerCurrDate());
				sqlBuilder.putFieldValue("status", "���ύ");
				HashVO hashvo = (HashVO) itemVO.getCustProperty("hashvo");
				if (hashvo != null) {
					sqlBuilder.setWhereCondition(" id = '" + hashvo.getStringValue("id") + "'");
				} else {
					String cellindex = new SalaryTBUtil().convertIntColToEn(col + 1,false);
					String cell = cellindex + "" + row;
					MessageBox.showWarn(panel, "���[" + cell + "]���ݴ��������ش���,�뾡����ϵ����Ա��");
					InsertSQLBuilder insert = new SalaryUIUtil().getErrLogSql("Ա����ֱ����ûȡ��[hashvo]ֵ", "���ݴ���", "��������PersonMutualCheckValueChangeLinstener��,���[" + cell + "]ִ��getCustProperty����ȡ����hashvoֵ��", "����");
					if (insert != null) {
						UIUtil.executeUpdateByDS(null, insert);
					}
					return;
				}
				new Timer().schedule(new TimerTask() { // ���߳�ִ��,����ҳ���п�������
							public void run() {
								try {
									UIUtil.executeUpdateByDS(null, sqlBuilder);
									wkpanel.autoCheckIFCanCommit();
								} catch (WLTRemoteException e) {
									e.printStackTrace();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}, 0);
				wkpanel.resetColSum(cellPanel, col);
			} catch (Exception e) {
				if (e.getMessage().contains("connect")) {
					MessageBox.showWarn(panel, "���ĵ���������ܳ�������.���鱾������.");
				} else {
					MessageBox.showException(panel, e);
				}
				e.printStackTrace();
			}
		}
	}
	//
	//	public int findColumnIndex(TableColumnModel colModel, String _key) {
	//		int li_columncount = colModel.getColumnCount();
	//		for (int i = 0; i < li_columncount; i++) {
	//			TableColumn column = colModel.getColumn(i);
	//			if (((String) column.getIdentifier()).equalsIgnoreCase(_key)) {
	//				return i;
	//			}
	//		}
	//		return -1;
	//	}
}
