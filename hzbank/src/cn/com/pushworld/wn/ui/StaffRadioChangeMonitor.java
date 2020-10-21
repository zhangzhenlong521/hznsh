package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.pushworld.wn.bs.WnSalaryServiceImpl;
import cn.com.pushworld.wn.to.WnUtils;

public class StaffRadioChangeMonitor extends AbstractWorkPanel implements
		ActionListener {
	private BillListPanel listPanel, list,list2;
	private WLTButton bmButton, updateBatch, updateView,importRadio;// 部门系数查看，批量修改，修改监测
	private JComboBox comboBox = null;
	private String loginUserName=ClientEnvironment.getInstance().getLoginUserName();

	@Override
	public void initialize() {
		listPanel = new BillListPanel("V_WN_STAFFRADIO_ZPY_Q01");
		list = new BillListPanel("V_BMRADIO_ZPY_Q01");
		list2 = new BillListPanel("WN_RADIOTABLE_ZPY_Q01");
		bmButton = new WLTButton("部门系数查看");
		bmButton.addActionListener(this);
		updateBatch = new WLTButton("批量修改");
		updateBatch.addActionListener(this);
		updateView = new WLTButton("调整结果查看");
		updateView.addActionListener(this);
		importRadio=new WLTButton("员工系数导入");
		importRadio.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] {importRadio,bmButton,
				updateBatch, updateView });
		listPanel.setRowNumberChecked(true);// 设置启动
		listPanel.repaintBillListButton();
		listPanel.getQuickQueryPanel().addBillQuickActionListener(this);
		this.add(listPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bmButton) {// 部门系数查看
			BillListDialog dialog = new BillListDialog(listPanel, "部门系数查看",
					list);
			dialog.getBtn_confirm().setVisible(false);
			String maxTime = "";
			try {
				maxTime = UIUtil.getStringValueByDS(null,
						"select max(createtime) from wn_workerStadio");
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			dialog.getBilllistPanel().QueryDataByCondition(
					"createtime='" + maxTime + "'");
			dialog.setVisible(true);
		} else if (e.getSource() == listPanel.getQuickQueryPanel()) {// 重写快速查询
			String querySQL = "select * from V_WN_STAFFRADIO where 1=1 ";
			String queryCondition = listPanel.getQuickQueryPanel()
					.getQuerySQLCondition();
			if (WnUtils.isEmpty(queryCondition)) {
				querySQL = querySQL + " " + queryCondition;
			}
			System.out.println("当前执行的SQL为:" + querySQL);
			listPanel.queryDataByDS(null, querySQL);
		} else if (e.getSource() == updateBatch) {// 批量修改(需要操作两张表)
			try {//wn_workerStadio  sal_personinfo
				BillVO[] billVos = listPanel.getCheckedBillVOs();//获取到当前选中记录
				if(billVos==null|| billVos.length<=0){
					MessageBox.show(this,"请选中一条记录进行操作");
					return;
				}
				String stationratio = JOptionPane
						.showInputDialog("请输入调整后的岗位系数(只允许输入数字):");
				if(stationratio == null
						||"".equals(stationratio) ){
					return;
				}
				if(!stationratio.matches("[0-9]*\\.?[0-9]*")){
					MessageBox.show(this, "输入的岗位系数有误,请检查");
					return;
				}
				//只能修改最近的，即当前考核月的数据
				String maxTime=UIUtil.getStringValueByDS(null, "SELECT max(CREATETIME) FROM wn_workerStadio");
				UpdateSQLBuilder updateSystem = new UpdateSQLBuilder("sal_personinfo");
				UpdateSQLBuilder update=new UpdateSQLBuilder("wn_workerStadio");
				InsertSQLBuilder insert=new InsertSQLBuilder("wn_radioTable");
				List<String> list=new ArrayList<String>();
				SimpleDateFormat simple=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				for (int i = 0; i < billVos.length; i++) {
					String usercode=billVos[i].getStringValue("usercode");
					updateSystem.setWhereCondition("code='"+usercode+"'");
					updateSystem.putFieldValue("STATIONRATIO", stationratio);
					update.setWhereCondition("usercode='"+usercode+"' and createtime='"+maxTime+"'");
					update.putFieldValue("RADIOCHANGEBEFORE", billVos[i].getStringValue("RADIOCHANGEAFTER"));
					update.putFieldValue("RADIOCHANGEAFTER",stationratio );
					update.putFieldValue("updateuser", loginUserName);
					update.putFieldValue("UPDATETIME", simple.format(new Date()));
					System.out.println("系统表中:"+updateSystem.getSQL()+",创建表中:"+update.getSQL());
					insert.putFieldValue("USERNAME", loginUserName);
					insert.putFieldValue("UPDATEBEFORE",billVos[i].getStringValue("RADIOCHANGEAFTER"));
					insert.putFieldValue("UPDATEAFTER",stationratio );
					insert.putFieldValue("UPDATEUSERNAME", billVos[i].getStringValue("username"));
					insert.putFieldValue("UPDATETIME", simple.format(new Date()));
					list.add(updateSystem.getSQL());
					list.add(update.getSQL());
					list.add(insert.getSQL());
				}
				if(list.size()>0){
					UIUtil.executeBatchByDS(null, list);
				}
				MessageBox.show(this,"员工岗位系数修改成功");
				listPanel.refreshData();
			} catch (Exception e2) {
				MessageBox.show(this,"员工岗位系数修改失败");
				e2.printStackTrace();
			}
		}else if(e.getSource()==updateView){
			BillListDialog dialog = new BillListDialog(listPanel, "部门系数调整情况查看",
					list2);
			dialog.getBtn_confirm().setVisible(false);
			dialog.getBilllistPanel().QueryDataByCondition("1=1");
			dialog.setVisible(true);
		}else if(e.getSource()==importRadio){//员工岗位系数信息导入
			try {
				//判断岗位系数是否存在
				SimpleDateFormat simple=new SimpleDateFormat("yyyy-MM");
				String handleDate= getLastMonth(simple.format(new Date()));
				String[] existsDate = UIUtil.getStringArrayFirstColByDS(null, "SELECT 1 FROM wn_workerStadio WHERE CREATETIME='"+handleDate+"'");
				if(existsDate.length>0){//当前数据已经存在，是否需要重新导入
					int result = MessageBox.showOptionDialog(this, "当前员工系数已经存在，是否需要重新导入", "提示",
							new String[] { "是", "否" }, 1);
					if(result!=1){//重新导入
						WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
								.lookUpRemoteService(WnSalaryServiceIfc.class);
						service.insertStaffRadio(handleDate);
						MessageBox.show(this,"数据重新导入成功");
					}else {
						return;
					}
				}else {
					WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
							.lookUpRemoteService(WnSalaryServiceIfc.class);
					service.insertStaffRadio(handleDate);
					MessageBox.show(this,"数据导入成功");
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
public String	getLastMonth(String currentMonth){
	String lastMonth="";
	try {
		 int month = Integer.parseInt(currentMonth.split("-")[1]);
		 int year  =Integer.parseInt(currentMonth.split("-")[0]);
		 String monthStr="";
		 if(month==1){
			 lastMonth=(year-1)+"-"+"12";
		 }else{
			 monthStr=String.valueOf(--month) ;
			 lastMonth=year+"-"+(month<=9?"0"+monthStr:monthStr);
		 }
	} catch (Exception e) {
		e.printStackTrace();
	}
	return lastMonth;
}
}