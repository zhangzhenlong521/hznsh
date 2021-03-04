package cn.com.pushworld.salary.ui;

import java.util.ArrayList;
import java.util.List;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;

/**
 * @author Gwang
 * 2013-8-21 上午11:29:38
 */
public class SalaryUIUtil {
	private static SalaryServiceIfc service;

	//取得默认考核日期
	public String getCheckDate() {
		String checkDate = "";
		String sql = "select checkdate from sal_target_check_log where status = '考核中'" + "union all " + "select checkdate from sal_target_check_log order by checkdate desc";
		try {
			checkDate = UIUtil.getStringValueByDS(null, sql);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return checkDate;
	}

	public static SalaryServiceIfc getService() {
		if (service == null) {
			try {
				service = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return service;
	}

	/**
	 * 列表行上移【李春娟/2013-10-24】
	 * @throws Exception
	 */
	public void billListRowUp(BillListPanel billList) throws Exception {
		billList.moveUpRow();
		int li_rowcount = billList.getRowCount();
		String seqfild = billList.getTempletVO().getTreeseqfield();
		if (seqfild == null || seqfild.trim().equals("")) {
			seqfild = "seq";
		}
		List sqls = new ArrayList();
		for (int i = 0; i < li_rowcount; i++) {
			if (billList.getValueAt(i, seqfild) == null || !((i + 1) + "").equals(billList.getRealValueAtModel(i, seqfild))) {
				billList.setValueAt(new StringItemVO("" + (i + 1)), i, seqfild);
				sqls.add("update " + billList.templetVO.getSavedtablename() + " set " + seqfild + " ='" + (i + 1) + "' where id=" + billList.getBillVO(i).getPkValue());
			}
		}
		UIUtil.executeBatchByDS(null, sqls);
	}

	/**
	 * 列表行下移【李春娟/2013-10-24】
	 * @throws Exception
	 */
	public void billListRowDown(BillListPanel billList) throws Exception {
		billList.moveDownRow();
		int li_rowcount = billList.getRowCount();
		String seqfild = billList.getTempletVO().getTreeseqfield();
		if (seqfild == null || seqfild.trim().equals("")) {
			seqfild = "seq";
		}
		List sqls = new ArrayList();
		for (int i = 0; i < li_rowcount; i++) {
			if (billList.getValueAt(i, seqfild) == null || !((i + 1) + "").equals(billList.getRealValueAtModel(i, seqfild))) {
				billList.setValueAt(new StringItemVO("" + (i + 1)), i, seqfild);
				sqls.add("update " + billList.templetVO.getSavedtablename() + " set " + seqfild + " ='" + (i + 1) + "' where id=" + billList.getBillVO(i).getPkValue());
			}
		}
		UIUtil.executeBatchByDS(null, sqls);
	}

	/**
	 * 将单元格相同内容的合并【李春娟/2013-10-24】
	 * @param cellPanel
	 * @param _spanColumns
	 *            那几列需要处理
	 */
	public void formatSpan(BillCellPanel cellPanel, int[] _spanColumns) {
		if (_spanColumns != null) {
			BillCellItemVO[][] cellItemVOs = cellPanel.getBillCellVO().getCellItemVOs();
			for (int i = 0; i < _spanColumns.length; i++) {
				int li_pos = _spanColumns[i];
				if (li_pos >= 0) {
					int li_spancount = 1;
					int li_spanbeginpos = 1;
					for (int k = 1; k < cellItemVOs.length; k++) {
						String str_value = cellItemVOs[k][li_pos].getCellvalue();
						String str_value_front = cellItemVOs[k - 1][li_pos].getCellvalue();
						if (TBUtil.getTBUtil().compareTwoString(str_value_front, str_value)) {
							if (i >= 1) {
								String str_value0 = cellItemVOs[k][_spanColumns[i - 1]].getCellvalue();
								String str_value_front0 = cellItemVOs[k - 1][_spanColumns[i - 1]].getCellvalue();
								if (TBUtil.getTBUtil().compareTwoString(str_value0, str_value_front0)) {
									li_spancount++;
								} else {
									cellItemVOs[li_spanbeginpos][li_pos].setSpan(li_spancount + ",1");
									cellPanel.span(li_spanbeginpos, li_pos, li_spancount, 1);
									li_spancount = 1;
									li_spanbeginpos = k;
								}
							} else {
								li_spancount++;
							}

						} else {
							cellItemVOs[li_spanbeginpos][li_pos].setSpan(li_spancount + ",1");
							cellPanel.span(li_spanbeginpos, li_pos, li_spancount, 1);
							li_spancount = 1;
							li_spanbeginpos = k;
						}
					}
					cellItemVOs[li_spanbeginpos][li_pos].setSpan(li_spancount + ",1");
					cellPanel.span(li_spanbeginpos, li_pos, li_spancount, 1);
				}
			}
		}
	}

	public InsertSQLBuilder getErrLogSql(String _name, String _type, String _descr, String _level) {
		try {
			InsertSQLBuilder insert = new InsertSQLBuilder("sal_sys_log");
			insert.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_SAL_SYS_LOG"));
			insert.putFieldValue("name", _name);
			insert.putFieldValue("type", _type);
			insert.putFieldValue("opeuserid", ClientEnvironment.getCurrLoginUserVO().getId());
			insert.putFieldValue("opeuser", ClientEnvironment.getCurrLoginUserVO().getCode() + "/" + ClientEnvironment.getCurrLoginUserVO().getName());
			insert.putFieldValue("deptid", ClientEnvironment.getCurrSessionVO().getLoginUserPKDept());
			insert.putFieldValue("deptname", ClientEnvironment.getCurrLoginUserVO().getDeptname());
			insert.putFieldValue("opetime", UIUtil.getCurrTime());
			insert.putFieldValue("descr", _descr);
			insert.putFieldValue("effectlevel", _level);
			return insert;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;

	}
	public static void main(String[] args) {
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	}
}
