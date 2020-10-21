package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class NewaddFarmerCollect extends AbstractWorkPanel implements
		ActionListener {

	/**
	 * @author FJ[农民工管理指标完成比] 2019年6月4日14:36:09
	 */
	private static final long serialVersionUID = 1L;
	private BillListPanel billListPanel;

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==billListPanel.getQuickQueryPanel()){
			String[][] data;
			String name1,time1,deptname1;
			String time = billListPanel.getQuickQueryPanel().getCompentRealValue("DATE_TIME").replace("年", "-").replace("月;","");
			String name = billListPanel.getQuickQueryPanel().getCompentRealValue("name");
			String deptname =  billListPanel.getQuickQueryPanel().getCompentRealValue("deptname");
			if(name.equals("")){
				name1="1=1";
			}else{
				name1="name like '%"+name+"%'";
			}if(time.equals("")){
				time1="and 1=1";
			}else{
				time1=" and date_time='"+time+"'";
			}if(deptname.equals("")){
				deptname1="and 1=1";
			}else{
				deptname1=" and deptname like '%"+deptname+"%'";
			}
			String sqlCondition=name1+time1;
			String sql = "select * from V_WN_NMGGL  where "+sqlCondition;
			try {
				data = UIUtil.getStringArrayByDS(null,sql);
				if(data.length<=0){
					WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil.lookUpRemoteService(WnSalaryServiceIfc.class);
					String str=service.getNmggl(billListPanel.getQuickQueryPanel().getCompentRealValue("DATE_TIME").replace("年", "-").replace("月;",""));
					MessageBox.show(this,str);
					billListPanel.QueryData(sql);
				}else{
					billListPanel.QueryData(sql);
				}
			} catch (WLTRemoteException e1) {
				e1.printStackTrace();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

	}

	
		
		


	

	@Override
	public void initialize() {
		billListPanel = new BillListPanel("V_WN_NMGGL_CODE1");
		billListPanel.getQuickQueryPanel().addBillQuickActionListener(this);
		this.add(billListPanel);

	}

}
