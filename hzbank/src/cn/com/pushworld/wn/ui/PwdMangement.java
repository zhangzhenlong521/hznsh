package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.DESKeyTool;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class PwdMangement extends AbstractWorkPanel implements ActionListener{
	private WLTButton pwd_reset = null;
	private WLTButton pwd_decode = null;
	private WLTButton pwd_giveadmin = null;
	private BillListPanel listPanel = null;
	@Override
	public void initialize() {
		listPanel = new BillListPanel("PUB_USER_CODE1");
		pwd_reset = new WLTButton("��������(����)");
		pwd_reset.addActionListener(this);
		pwd_decode = new WLTButton("�������");
		pwd_decode.addActionListener(this);
		pwd_giveadmin = new WLTButton("�������ԱȨ��");
		pwd_giveadmin.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] {pwd_reset,pwd_decode,pwd_giveadmin});
		pwd_giveadmin.setVisible(false);
		listPanel.repaintBillListButton();
		this.add(listPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == pwd_reset){
			pwdReset();
		}
		if(e.getSource() == pwd_decode){
			pwdDecode();
		}
		if(e.getSource() == pwd_giveadmin){
			pwdGiveAdmin();
		}
		
	}

	private void pwdGiveAdmin() {
		// TODO Auto-generated method stub
		
	}

	private void pwdDecode() {
		BillVO billvo = listPanel.getSelectedBillVO();
		if(billvo == null){
			MessageBox.show(listPanel, "����ѡ��һ����¼��ִ�д˲�����");
			return;
		}else{
			String name = billvo.getStringValue("name");
			String code = billvo.getStringValue("code");
			String pwd_bef = billvo.getStringValue("PWD");
			String pwd_aft = new DESKeyTool().decrypt(pwd_bef);
			if(pwd_aft.equals(pwd_bef)){
				MessageBox.show("����ʧ�ܣ�����ϵϵͳ����Ա");
			}else{
				MessageBox.show("����Ϊ["+name+"],����Ϊ["+code+"]�Ľ�������Ϊ["+pwd_aft+"]");
			}
		}
		
	}

	private void pwdReset() {
		BillVO billvo = listPanel.getSelectedBillVO();
		if(billvo == null){
			MessageBox.show(listPanel, "����ѡ��һ����¼��ִ�д˲�����");
			return;
		}else{
			String name = billvo.getStringValue("name");
			String code = billvo.getStringValue("code");
			int count=MessageBox.showConfirmDialog(this,"ȷ��Ҫ������Ϊ["+name+"],����Ϊ["+code+"]����������Ϊ[1]��? ��������ã������ȡ��");
//			if(count==0){
//				try {
//					dmo.executeUpdateByDS(null, "update pub_user set pwd='1' where name='"+name+"' and code='"+code+"'");
//					MessageBox.show("�������óɹ���");
//					listPanel.refreshData();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//            }else{
//            	return;
//            }
			if(count==0){
				UpdateSQLBuilder update = new UpdateSQLBuilder("PUB_USER");
				ArrayList<String> list = new ArrayList<String>();
				update.setWhereCondition("name='"+name+"' and code='"+code+"'");
				update.putFieldValue("pwd", 1);
				list.add(update.getSQL());
				try {
					UIUtil.executeBatchByDS(null, list);
					MessageBox.show(this, "���óɹ���");
					listPanel.refreshData();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				return;
			}	
		}
		
	}

}
