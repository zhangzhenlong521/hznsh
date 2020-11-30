package cn.com.deloitte.wpkj;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.pushworld.wn.ui.WnSalaryServiceIfc;

public class CashManagementWKPanel extends AbstractWorkPanel implements ActionListener {

	private BillListPanel listPanel;
	private WLTButton dealBtn;
	private JComboBox comboBox = null;// 增加复选框
	private String message="";
	@Override
	public void initialize() {
		listPanel=new BillListPanel("V_XJCL_CODE_1");
		dealBtn=new WLTButton("处理");
		dealBtn.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[]{dealBtn});
		listPanel.repaintBillListButton();
		listPanel.setRowNumberChecked(true);// 设置启动
		this.add(listPanel);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		message="已关闭";
		if(event.getSource()==dealBtn){// 对内容进行处理
			// 获取到选中数据
		    final BillVO[] vos = listPanel.getCheckedBillVOs();
			if( vos == null || vos.length == 0){
				MessageBox.show(this,"当前未选中数据，请选中一条或多条数据处理");
				return;
			}
			// 弹个框，让用户处理一下这些数据
		   final	BillCardDialog dialog=new BillCardDialog(this, "现金管理审查","CONFIRM_TABLE_XJGL",600,300);
			// 保存按钮不可见
			dialog.getBtn_save().setVisible(false);
			try{
				final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
						.lookUpRemoteService(WnSalaryServiceIfc.class);
				
			dialog.getBtn_confirm().addActionListener(new  ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String state = dialog.getCardItemValue("state");
					String msg=dialog.getCardItemValue("confirm_comment");
				    message=service.dealCashManageMent(vos,state,msg);
				    dialog.closeMe();
				}

			});
			dialog.setVisible(true);// 设置可见
			MessageBox.show(this,message);
			listPanel.refreshData();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

}
