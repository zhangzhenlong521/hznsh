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
	private JComboBox comboBox = null;// ���Ӹ�ѡ��
	private String message="";
	@Override
	public void initialize() {
		listPanel=new BillListPanel("V_XJCL_CODE_1");
		dealBtn=new WLTButton("����");
		dealBtn.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[]{dealBtn});
		listPanel.repaintBillListButton();
		listPanel.setRowNumberChecked(true);// ��������
		this.add(listPanel);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		message="�ѹر�";
		if(event.getSource()==dealBtn){// �����ݽ��д���
			// ��ȡ��ѡ������
		    final BillVO[] vos = listPanel.getCheckedBillVOs();
			if( vos == null || vos.length == 0){
				MessageBox.show(this,"��ǰδѡ�����ݣ���ѡ��һ����������ݴ���");
				return;
			}
			// ���������û�����һ����Щ����
		   final	BillCardDialog dialog=new BillCardDialog(this, "�ֽ�������","CONFIRM_TABLE_XJGL",600,300);
			// ���水ť���ɼ�
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
			dialog.setVisible(true);// ���ÿɼ�
			MessageBox.show(this,message);
			listPanel.refreshData();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

}
