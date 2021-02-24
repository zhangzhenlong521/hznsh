package cn.com.delotte.gy.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JComboBox;

import com.lowagie.text.List;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.pushworld.wn.ui.WnSalaryServiceIfc;

/**
 * 柜员授权业务被拒绝
 * @author ZPY
 *
 */
public class GyEmpowerRefuseWKPanel extends AbstractWorkPanel implements ActionListener {

	private BillListPanel panel=null;
	private  String deptName=ClientEnvironment.getInstance().getLoginUserDeptName();// 获取到登录人的机构名称
	private  String code=ClientEnvironment.getInstance().getLoginUserCode();//获取到当前登录人柜员号
	private String name=ClientEnvironment.getInstance().getLoginUserName();// 获取到当前登录人姓名
	private  WLTButton gy_confirm,cw_confirm;
	private JComboBox comboBox = null;// 增加复选框
	private String message=null;
	
	@Override
	public void initialize() {
		panel=new BillListPanel("EXCEL_TAB_148_CODE");
		if(deptName.contains("财务")){// 判断当前登录人的机构 ，如果是财务部
			cw_confirm=new WLTButton("审核");
			cw_confirm.addActionListener(this);
			panel.addBillListButton(cw_confirm);
			panel.QueryDataByCondition("cw_confirm is null  and gy_confirm ='有异议'");
		}else  {
			gy_confirm=new WLTButton("确认");
			gy_confirm.addActionListener(this);
			panel.addBillListButton(gy_confirm);
			panel.QueryDataByCondition("(M LIKE '%"+code+"%' or N LIKE '%"+code+"%') and  GY_CONFIRM IS NULL");
		}
		panel.repaintBillListButton();
		panel.setRowNumberChecked(true);// 设置启动
		this.add(panel);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()== gy_confirm){ // 柜员确认
			message="已关闭";
			// 柜员确认，对被拒绝的业务进行确认: 确认 有异议,如果有异议，请说明原因
			final BillVO[] vos = panel.getCheckedBillVOs();
			if(vos== null || vos.length<=0){
				MessageBox.show(this,"请选中一条或者多条数据进行处理");
				return;
			}
			final BillCardDialog dialog=new BillCardDialog(this,"信息确认", "CONFIRM_TABLE_CODE",600,300);
			dialog.getBtn_save().setVisible(false); // 保存按钮不可见
			dialog.getBtn_confirm().addActionListener(new  ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) { //当用户点击确认时，触发的操作
					try {
						message=conirm(vos, dialog,"gy");
						dialog.closeMe();
					} catch (Exception e1) {
						message="操作失败，失败原因:\n"+e1.getMessage()+"\n 请与管理员联系";
					}finally{
						MessageBox.show(message);
					}
				}
			});
			dialog.setVisible(true);// 设置可见
			panel.refreshData();
			
		}else if(e.getSource() == cw_confirm){ // 财务部确认
			message="已关闭";
			// 对柜员提交的有异议的业务进行审批: 同意，拒绝；
			final BillVO[] vos = panel.getCheckedBillVOs();
			if(vos== null || vos.length<=0){
				MessageBox.show(this,"请选中一条或者多条数据进行处理");
				return;
			}
			final BillCardDialog dialog=new BillCardDialog(this,"信息确认", "CONFIRM_TABLE_CODE_CW",600,300);
			dialog.getBtn_save().setVisible(false); // 保存按钮不可见
			dialog.getBtn_confirm().addActionListener(new  ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) { //当用户点击确认时，触发的操作
					try {
						message=conirm(vos, dialog,"cw");
						dialog.closeMe();
					} catch (Exception e1) {
						message="操作失败，失败原因:\n"+e1.getMessage()+"\n 请与管理员联系";
					}finally{
						MessageBox.show(message);
					}
				}
			});
			dialog.setVisible(true);// 设置可见
			panel.refreshData();
		}
	}
	private String conirm(BillVO[] vos, BillCardDialog dialog,String type) throws Exception{
		
		final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
				.lookUpRemoteService(WnSalaryServiceIfc.class);
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String curDate=format.format(new Date());//获取到当前时间
		HashMap<String, String> map=new HashMap<String, String>();
	    String state=	dialog.getCardItemValue("state");
	    map.put(type+"_confirm",state);
	    String confirmComment =dialog.getCardItemValue("confirm_comment");//获取到评论内容
	    map.put(type+"_confirm_comment", confirmComment);
	    map.put(type+"_confirm_name", name);
	    map.put(type+"_confirm_time", curDate);
	    message=service.confirm(vos,map);
	    return message;
	}
	
}
