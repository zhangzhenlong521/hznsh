package cn.com.pushworld.salary.ui.personalcenter.p030;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_ComboBox;
import cn.com.pushworld.salary.to.SalaryTBUtil;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

/**
 * ���˼�Ч��ѯ��������չ�������ܡ�
 * @author haoming
 * create by 2013-8-22
 */
public class SelfPerformanceQueryWKPanel extends AbstractWorkPanel implements ActionListener {
	BillListPanel listpanel = new BillListPanel("SAL_PERSON_CHECK_SCORE_CODE2");
	private String queryCL = TBUtil.getTBUtil().getSysOptionStringValue("�����쵼�鿴Ա�����˽������", "1");
	private String deptusers[];
	private String userid = ClientEnvironment.getCurrSessionVO().getLoginUserId();
	private String item = "*";

	public void initialize() {
		try {
			TableDataStruct struct = UIUtil.getTableDataStructByDS(null, "select * from SAL_PERSON_CHECK_SCORE where 1=2"); //�ѱ�ṹ�������
			String[] tableHeadName = struct.getHeaderName();
			StringBuffer itemsql = new StringBuffer();
			for (int i = 0; i < tableHeadName.length; i++) {
				if (!TBUtil.isEmpty(tableHeadName[i]) && !"money".equals(tableHeadName[i])) { //money�Ȳ����
					itemsql.append("t1." + tableHeadName[i] + ",");
				}
			}
			if (itemsql.length() > 0) {
				item = itemsql.substring(0, itemsql.length() - 1);
			}
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		deptusers = new SalaryTBUtil().getSameDeptUsers(userid);
		if (deptusers.length <= 1) {
			listpanel.setItemVisible("checkedusername", false);
		}
		;
		listpanel.setItemVisible("checkdate", false);
		listpanel.getQuickQueryPanel().addBillQuickActionListener(this);
		boolean showavgmoney = TBUtil.getTBUtil().getSysOptionBooleanValue("���˼�Ч��ѯ�Ƿ���ʾƽ������", true);
		// ��������Ĭ��ֵΪ��ǰ�������� Gwang 2013-08-21
		QueryCPanel_ComboBox yearcom = (QueryCPanel_ComboBox) listpanel.getQuickQueryPanel().getCompentByKey("year");
		QueryCPanel_ComboBox monthcom = (QueryCPanel_ComboBox) listpanel.getQuickQueryPanel().getCompentByKey("month");
		String checkDate = new SalaryUIUtil().getCheckDate();
		if (!showavgmoney) {
			listpanel.setItemVisible("avgmoney", false);
		}
		if (checkDate != null) {
			if (yearcom != null) {
				String year = checkDate.substring(0, 4);
				yearcom.setObject(new ComBoxItemVO(year, year, year));
			}
			if (monthcom != null) {
				String month = checkDate.substring(5, checkDate.length());
				monthcom.setObject(new ComBoxItemVO(month, month, month));
			}
		} else {
			String checkdate = UIUtil.getCurrDate().substring(0, 4);
			if (yearcom != null) {
				String year = checkdate.substring(0, 4);
				yearcom.setObject(new ComBoxItemVO(year, year, year));
			}
		}
		this.add(listpanel);
	}

	public void actionPerformed(ActionEvent e) {
		BillQueryPanel queryPanel = listpanel.getQuickQueryPanel();
		if (!queryPanel.checkValidate()) {
			return;
		}
		HashMap map = queryPanel.getQuickQueryConditionAsMap();
		if (listpanel.getQuickQueryPanel().checkQuickQueryConditionIsNull()) {
			if (map != null) {
				String year = (String) map.get("year");
				if (year != null) {
					String month = (String) map.get("month");
					StringBuffer sql = new StringBuffer();

					String avgconditon = "";
					if (month != null) {
						avgconditon = " checkdate='" + year + "-" + month + "' ";
						listpanel.setItemVisible("checkdate", false);
					} else {
						avgconditon = " checkdate like '" + year + "%' ";
						listpanel.setItemVisible("checkdate", true);
					}

					String mainsql = "select " + item + ",t1.money,t2.unitvalue,round(t6.money,2) avgmoney from SAL_PERSON_CHECK_SCORE t1 left join sal_person_check_list t2 on t1.targetid = t2.id left join (select groupid,checkdate,avg(money) money from SAL_PERSON_CHECK_SCORE  where " + avgconditon
							+ " group by groupid,checkdate) t6 on t6.groupid=t1.groupid and t6.checkdate = t1.checkdate  where t1.targettype='Ա������ָ��' and t1.checkeduser = '" + userid + "' and 1=1 ";
					String unionmainsql = "select " + item + ",'****',t2.unitvalue,'' avgmoney  from SAL_PERSON_CHECK_SCORE t1 left join sal_person_check_list t2 on t1.targetid = t2.id where t1.targettype='Ա������ָ��' and t1.checkeduser!='" + userid + "' and 1=1 ";
					sql.append(mainsql);
					if (month != null) {
						sql.append(" and t1.checkdate='" + year + "-" + month + "' ");
						if (!"0".equals(queryCL)) {
							sql.append(" union all ");
							sql.append(unionmainsql);
							sql.append(" and t1.checkdate='" + year + "-" + month + "' ");
						}
					} else {
						sql.append(" and t1.checkdate like '" + year + "%'  ");
						if (!"0".equals(queryCL)) {
							sql.append(" union all ");
							sql.append(unionmainsql);
							sql.append(" and  t1.checkdate like '" + year + "%'  ");
						}
					}
					if (!"0".equals(queryCL)) {
						sql.append(" and  t1.checkeduser in (" + new TBUtil().getInCondition(deptusers) + ")");
					}
					try {
						HashVO vos[] = UIUtil.getHashVoArrayByDS(null, sql.toString());
						if (vos == null || vos.length == 0) {
							listpanel.removeAllRows();
							MessageBox.show(this, "û�в鵽����.");
							return;
						}
						listpanel.queryDataByHashVOs(vos);
					} catch (WLTRemoteException e1) {
						e1.printStackTrace();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				} else {
					listpanel.removeAllRows();
				}
			}
		} else {
			return;
		}
	}
}
