package cn.com.pushworld.salary.ui.report.p050;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.AbstractAction;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_ComboBox;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;
import cn.com.infostrategy.ui.report.BillCellHtmlHrefEvent;
import cn.com.infostrategy.ui.report.BillCellHtmlHrefListener;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.infostrategy.ui.report.style2.DefaultStyleReportPanel_2;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

/**
 * Ա��н��������
 * @author haoming
 * create by 2014-5-5
 */
public class PersonSalaryDifReport extends AbstractWorkPanel implements ActionListener, BillCellHtmlHrefListener {
	private static final long serialVersionUID = -1689429974311206559L;
	private DefaultStyleReportPanel_2 dr = null;
	private BillQueryPanel bq = null;
	private String fileName = null;
	private WLTButton view = new WLTButton("��ϸ", UIUtil.getImage("office_089.gif"));
	private HashMap<String, Pub_Templet_1VO> templetMap = new HashMap<String, Pub_Templet_1VO>();

	public void initialize() {
		dr = new DefaultStyleReportPanel_2("SAL_TARGET_CHECK_LOG_MONEY_DIF", "");
		bq = dr.getBillQueryPanel();
		dr.getPanel_btn().add(view);
		QueryCPanel_UIRefPanel dateRef = (QueryCPanel_UIRefPanel) bq.getCompentByKey("checkdate");
		String checkDate = new SalaryUIUtil().getCheckDate();
		dateRef.setValue(checkDate);
		QueryCPanel_ComboBox box = (QueryCPanel_ComboBox) bq.getCompentByKey("factor");
		HashMap<String, String> viewName_factorid = null;
		try {
			viewName_factorid = UIUtil.getHashMapBySQLByDS(null, "select viewname,factorid from sal_account_factor t1 left join sal_account_set t2 on t1.accountid = t2.id where t2.isdefault='Y' ");
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		String menu = this.getMenuConfMapValueAsStr("��ͳ����");
		if (!TBUtil.isEmpty(menu)) {
			String fac[] = TBUtil.getTBUtil().split(menu, ";");
			ArrayList<ComBoxItemVO> item = new ArrayList<ComBoxItemVO>();
			for (int i = 0; i < fac.length; i++) {
				if (viewName_factorid != null && viewName_factorid.containsKey(fac[i])) {
					item.add(new ComBoxItemVO(viewName_factorid.get(fac[i]), "", fac[i]));
				}
			}
			box.setItemVOs(item.toArray(new ComBoxItemVO[0]));
		}
		//				box.set
		view.addActionListener(this);
		dr.setReportExpName("����ָ��");
		fileName = "����ָ��";
		dr.getBillCellPanel().setEditable(false);
		bq.addBillQuickActionListener(this);
		dr.getBillCellPanel().addBillCellHtmlHrefListener(this);
		this.add(dr, BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == view) {
			BillCellPanel cellpanel = dr.getBillCellPanel();
			int row = cellpanel.getTable().getSelectedRow();
			if (row < 0) {
				MessageBox.show(this, "��ѡ��һ����¼.");
				return;
			}
			BillCellItemVO itemVO = cellpanel.getBillCellItemVOAt(row, cellpanel.getColumnCount() - 1);
			if (itemVO != null) {
				String targetid = (String) itemVO.getCustProperty("id");
				if (TBUtil.isEmpty(targetid)) {
					MessageBox.show(this, "��ѡ��һ����¼.");
					return;
				}
				String type = (String) itemVO.getCustProperty("type");
				String billtemplet = null;
				if ("���Ŷ���ָ��".equals(type)) {
					billtemplet = "SAL_TARGET_LIST_CODE_QUANTIFY";
				} else if ("���Ŷ���ָ��".equals(type)) {
					billtemplet = "SAL_TARGET_LIST_CODE1";
				}
				BillCardPanel cardpanel = new BillCardPanel(getTempletVO(billtemplet));
				BillCardDialog carddialog = new BillCardDialog(this, "���", cardpanel, WLTConstants.BILLDATAEDITSTATE_INIT);
				carddialog.getBillcardPanel().queryDataByCondition(" id = " + targetid);
				carddialog.setCardEditable(false);
				carddialog.setVisible(true);
			}

		} else {
			new SplashWindow(this, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					onQuery();
				}
			});
		}
	}

	/*
	 * ͳһȡģ��
	 */
	private Pub_Templet_1VO getTempletVO(String _code) {
		if (!templetMap.containsKey(_code)) {
			try {
				templetMap.put(_code, UIUtil.getPub_Templet_1VO(_code));
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}
		return templetMap.get(_code);
	}

	private void onQuery() {
		try {
			if (bq.checkValidate()) {
				SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
				String[] factorids = null;
				String fa = bq.getRealValueAt("factor");
				factorids = TBUtil.getTBUtil().split(fa, ";");
				if (factorids != null) {
					dr.getBillCellPanel().loadBillCellData(ifc.getPostMoneyDif(bq.getRealValueAt("checkdate"), factorids));
				}
				dr.getBillCellPanel().setEditable(false);
				if (dr.getBillCellPanel().getRowCount() > 2) {
					dr.getBillCellPanel().setLockedCell(1, 1); //������ͷ ��� 2013-10-10
				}

				//���õ����ļ����� Gwang 2013-08-31
				dr.setReportExpName(fileName + bq.getRealValueAt("checkdate"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(this, "�����쳣�������Ա��ϵ!");
		}
	}

	public void onBillCellHtmlHrefClicked(BillCellHtmlHrefEvent event) {
		try {
			BillCellItemVO selectvo = event.getCellItemVO();
			HashVO factor = (HashVO) selectvo.getCustProperty("factor");
			String post = (String) selectvo.getCustProperty("post");
			String bill = (String) selectvo.getCustProperty("bill");
			String _checkdate = (String) selectvo.getCustProperty("checkdate");
			String max = (String) selectvo.getCustProperty("max");
			String min = (String) selectvo.getCustProperty("min");

			HashVO[] maxvalue = UIUtil.getHashVoArrayByDS(null, "select t1.* from v_sal_personinfo t1 left join sal_salarybill_detail t2 on t1.id = t2.userid where t2.factorid = '" + factor.getStringValue("factorid") + "' and t2.salarybillid = '" + bill + "' and t1.stationkind = '" + post + "' and t2.factorvalue+0=" + Float.parseFloat(max));
			HashVO[] minvalue = UIUtil.getHashVoArrayByDS(null, "select t1.* from v_sal_personinfo t1 left join sal_salarybill_detail t2 on t1.id = t2.userid where t2.factorid = '" + factor.getStringValue("factorid") + "' and t2.salarybillid = '" + bill + "' and t1.stationkind = '" + post + "' and t2.factorvalue+0=" + Float.parseFloat(min));
			StringBuffer sb = new StringBuffer();
			if (maxvalue != null && maxvalue.length > 0) {
				sb.append("���ֵ����ԱΪ��" + maxvalue[0].getStringValue("name") + ",��λϵ��Ϊ:" + maxvalue[0].getStringValue("stationratio") + ";\n");
			}
			if (minvalue != null && minvalue.length > 0) {
				sb.append("��Сֵ����ԱΪ��" + minvalue[0].getStringValue("name") + ",��λϵ��Ϊ:" + minvalue[0].getStringValue("stationratio") + ";\n");
			}
			String xyjs = "Ч�湤�ʻ���";
			String gwjs = "��λ���ʻ���";

			HashMap<String, String> js = UIUtil.getHashMapBySQLByDS(null, "select name,value from sal_factor_def where name in('" + xyjs + "','" + gwjs + "')");
			if (factor.getStringValue("viewname", "").contains("Ч��") || factor.getStringValue("viewname", "").contains("��Ч")) {
				BigDecimal chamoney = new BigDecimal(maxvalue[0].getStringValue("stationratio")).subtract(new BigDecimal(minvalue[0].getStringValue("stationratio"))).multiply(new BigDecimal(js.get(xyjs))).setScale(2, BigDecimal.ROUND_HALF_UP);
				sb.append("һ����λϵ��Ч�湤�ʻ���Ϊ:" + js.get(xyjs) + ",ϵ�����" + factor.getStringValue("viewname") + "���" + chamoney.toString() + ",�Ż���ʵ�ʲ�" + new BigDecimal(selectvo.getCellvalue()).subtract(chamoney)).toString();
			} else if (factor.getStringValue("viewname", "").contains("��λ") || factor.getStringValue("viewname", "").contains("��λ")) {
				BigDecimal chamoney = new BigDecimal(maxvalue[0].getStringValue("stationratio")).subtract(new BigDecimal(minvalue[0].getStringValue("stationratio"))).multiply(new BigDecimal(js.get(gwjs))).setScale(2, BigDecimal.ROUND_HALF_UP);
				sb.append("һ����λϵ����λ���ʻ���Ϊ:" + js.get(gwjs) + ",ϵ�����" + factor.getStringValue("viewname") + "���" + chamoney.toString() + ",�Ż���ʵ�ʲ�" + new BigDecimal(selectvo.getCellvalue()).subtract(chamoney)).toString();
			} else {
				BigDecimal jxchamoney = new BigDecimal(maxvalue[0].getStringValue("stationratio")).subtract(new BigDecimal(minvalue[0].getStringValue("stationratio"))).multiply(new BigDecimal(js.get(xyjs))).setScale(2, BigDecimal.ROUND_HALF_UP);
				BigDecimal gwchamoney = new BigDecimal(maxvalue[0].getStringValue("stationratio")).subtract(new BigDecimal(minvalue[0].getStringValue("stationratio"))).multiply(new BigDecimal(js.get(gwjs))).setScale(2, BigDecimal.ROUND_HALF_UP);
				sb.append("һ����λϵ��Ч�湤�ʻ���Ϊ:" + js.get(xyjs) + ",ϵ�����" + factor.getStringValue("viewname") + "���" + jxchamoney.toString() + ";\n");
				sb.append("һ����λϵ����λ���ʻ���Ϊ:" + js.get(gwjs) + ",ϵ�����" + factor.getStringValue("viewname") + "���" + gwchamoney.toString() + ";\n");
				BigDecimal totle = (jxchamoney.add(gwchamoney));
				sb.append("������ܲ�" + totle + ",��Ҫ�ۺϿ���.�Ż���ʵ�ʲ�Ϊ:" + new BigDecimal(selectvo.getCellvalue()).subtract(totle)).toString();
			}

			MessageBox.show(this, "" + sb.toString());
		} catch (Exception ex) {

		}
	}
}
