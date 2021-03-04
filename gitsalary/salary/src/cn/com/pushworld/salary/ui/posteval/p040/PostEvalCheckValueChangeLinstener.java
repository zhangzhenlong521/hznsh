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
 * 岗位价值评估表格内容值变换后事件。
 * 
 */
public class PostEvalCheckValueChangeLinstener implements ItemValueListener {

	public synchronized void process(BillPanel panel) {
		if (panel instanceof BillCellPanel) {
			BillCellPanel cellPanel = (BillCellPanel) panel;
			final PostEvalWKPanel wkpanel = (PostEvalWKPanel) cellPanel.getClientProperty("this"); // 得到this的类
			if (wkpanel == null) {
				MessageBox.show(cellPanel, "请在BillListPanel中设置putClientProperTy(\"this\",class)");
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
				if (val < range[0] || val > range[1]) { // 如果输入的值不再范围之间。
					itemVO.setCellvalue("范围" + range[0] + "-" + range[1]);
					itemVO.setBackground("246,53,89"); // 红色预警
					return;
				} else {
					itemVO.setBackground("255,255,255"); // 设置正常颜色。
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}

			final UpdateSQLBuilder sqlBuilder = new UpdateSQLBuilder("sal_post_eval_score");
			try {
				sqlBuilder.putFieldValue("status", "待提交");
				HashVO hashvo = (HashVO) itemVO.getCustProperty("hashvo");
				if (hashvo != null) {
					if ("岗位评估".equals(hashvo.getStringValue("type"))) {
						sqlBuilder.putFieldValue("checkscore", value);
					} else if ("履职评估".equals(hashvo.getStringValue("type"))) {
						sqlBuilder.putFieldValue("checkscore2", value);
					}
					sqlBuilder.setWhereCondition(" id = '" + hashvo.getStringValue("id") + "'");
				}
				new Timer().schedule(new TimerTask() { // 开线程执行,否则页面有卡顿现象
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
					MessageBox.showWarn(panel, "您的电脑网络可能出现问题.请检查本地连接.");
				} else {
					MessageBox.showException(panel, e);
				}
				e.printStackTrace();
			}
		}
	}

}
