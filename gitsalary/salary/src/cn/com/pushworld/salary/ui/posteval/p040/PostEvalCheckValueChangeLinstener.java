package cn.com.pushworld.salary.ui.posteval.p040;

import java.util.Timer;
import java.util.TimerTask;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.to.mdata.jepfunctions.ItemValueListener;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;

/**
 * ��λ��ֵ�����������ֵ�任���¼���
 * 
 */
public class PostEvalCheckValueChangeLinstener implements ItemValueListener {

	public synchronized void process(BillPanel panel) {
		if (panel instanceof BillCellPanel) {
			BillCellPanel cellPanel = (BillCellPanel) panel;
			final PostEvalWKPanel wkpanel = (PostEvalWKPanel) cellPanel.getClientProperty("this"); // �õ�this����
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
					if (value.contains(".")) {
						val = Float.parseFloat(value);
					}
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

			final UpdateSQLBuilder sqlBuilder = new UpdateSQLBuilder("sal_post_eval_score");
			try {
				sqlBuilder.putFieldValue("status", "���ύ");
				HashVO hashvo = (HashVO) itemVO.getCustProperty("hashvo");
				if (hashvo != null) {
					if ("��λ����".equals(hashvo.getStringValue("type"))) {
						sqlBuilder.putFieldValue("checkscore", value);
					} else if ("��ְ����".equals(hashvo.getStringValue("type"))) {
						sqlBuilder.putFieldValue("checkscore2", value);
					}
					sqlBuilder.setWhereCondition(" id = '" + hashvo.getStringValue("id") + "'");
				}
				new Timer().schedule(new TimerTask() { // ���߳�ִ��,����ҳ���п�������
							public void run() {
								try {
									UIUtil.executeUpdateByDS(null, sqlBuilder);
									//wkpanel.autoCheckIFCanCommit();
								} catch (WLTRemoteException e) {
									e.printStackTrace();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}, 0);
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

}
