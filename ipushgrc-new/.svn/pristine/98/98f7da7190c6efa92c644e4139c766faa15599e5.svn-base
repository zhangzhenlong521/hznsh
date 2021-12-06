package com.pushworld.ipushgrc.ui.HR.p030;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 
 * @author zzl[2017-10-27] ÂÖ¸ÚÔ¤¾¯
 */
public class RotatingWarningWorkPanel extends AbstractWorkPanel implements
		ActionListener {
	private BillListPanel listpanel = null;
	private JButton btn_Query = null;
	private Object time = null;
	private TBUtil tbUtil = new TBUtil();
	private String colunm = tbUtil.getTBUtil().getSysOptionStringValue(
			"ÂÖ¸ÚÔ¤¾¯ÏÔÊ¾ÁÐ", null);
	private HashVO[] vo = null;

	@Override
	public void initialize() {
		listpanel = new BillListPanel("SAL_PERSONINFO_ZZL_CODE3");
		btn_Query = listpanel.getQuickQueryPanel().getQuickQueryButton();
		btn_Query.addActionListener(this);
		this.add(listpanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_Query) {
			time = listpanel.getQuickQueryPanel().getRealValueAt("createdate");
			time = time.toString().replace(";", "");
			autoGetTargetData();
			listpanel.setTableCellRendererHE(new RowColorRendererHE());
		}

	}

	class RowColorRendererHE extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable t, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			String temp = "";
			temp = listpanel.getRealValueAtModel(row, "yornyear");
			if ("Y".equals(temp)) {
				setBackground(Color.yellow);
			} else {
				setBackground(Color.WHITE);
			}

			return super.getTableCellRendererComponent(t, value, isSelected,
					hasFocus, row, column);
		}
	}

	private void autoGetTargetData() {
		String lgyear = null;
		String workingtime = null;
		try {
			UpdateSQLBuilder update = new UpdateSQLBuilder("sal_personinfo");
			ArrayList<String> list = new ArrayList<String>();
			String qname=listpanel.getQuickQueryPanel().getRealValueAt("name");
			if(qname!=null && !qname.equals(null) && !qname.equals("") && !qname.equals(" ") && !qname.equals("null")){
				vo = UIUtil
				.getHashVoArrayByDS(null,
						"select * from v_sal_personinfo where 1=1 and name='"+listpanel.getQuickQueryPanel().getRealValueAt("name")+"'");
			}else{
				vo = UIUtil
				.getHashVoArrayByDS(null,"select * from v_sal_personinfo where 1=1");
			}
			
			for (int i = 0; i < vo.length; i++) {
				lgyear = UIUtil.getStringValueByDS(null,
						"select lgyear from pub_post where id='"
								+ vo[i].getStringValue("mainstationid") + "'");
				String date=UIUtil.getStringValueByDS(null,"select hr.date from sal_personinfo sal left join hr_recordpost  hr on sal.id=hr.userid where sal.id='"+vo[i].getStringValue("id")+"'");
				if(date!=null && !date.equals(null) && !date.equals("") && !date.equals(" ")){
					Date date1 = new SimpleDateFormat("yyyy-mm-dd").parse(date);
					Date date2 = new SimpleDateFormat("yyyy-mm-dd").parse(UIUtil.getCurrDate());
					long db=((date2.getTime()-date1.getTime())/(24*60*60*1000))/365;
					update.setWhereCondition("id="
							+ vo[i].getStringValue("id"));
					update.putFieldValue("workingtime", db);
					UIUtil.executeUpdateByDS(null,update.getSQL());
				}
				workingtime = vo[i].getStringValue("workingtime");
				if (lgyear != null && !lgyear.equals("null")
						&& !lgyear.equals(null) && !lgyear.equals("") && !lgyear.equals(" ") && !lgyear.equals("0")
						&& workingtime != null && !workingtime.equals("null")
						&& !workingtime.equals(null) && !workingtime.equals("") && !workingtime.equals(" ") && !workingtime.equals("0")) {
					if (Integer.parseInt(lgyear) <= Integer.parseInt(vo[i]
							.getStringValue("workingtime"))) {
						update.setWhereCondition("id="
								+ vo[i].getStringValue("id"));
						update.putFieldValue("yornyear", "Y");
						UIUtil.executeUpdateByDS(null,update.getSQL());
					}
				}else{
					update.setWhereCondition("id="
							+ vo[i].getStringValue("id"));
					update.putFieldValue("yornyear", "N");
					UIUtil.executeUpdateByDS(null,update.getSQL());
				}
			}
		} catch (WLTRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(">>>>>>>>>>>>>>>>>>>" + lgyear);
			System.out.println(">>>>>>>>>>>>>>>>>>>" + workingtime);
			e.printStackTrace();
		}
	}

}
