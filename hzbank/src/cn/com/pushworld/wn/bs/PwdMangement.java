package cn.com.pushworld.wn.bs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.mdata.BillVO;
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
		// TODO Auto-generated method stub
		
	}

	private void pwdReset() {
		BillVO billvo = listPanel.getSelectedBillVO();
		if(billvo == null){
			MessageBox.show(listPanel, "����ѡ��һ����¼��ִ�д˲�����");
			return;
		}else{
			String name = billvo.getStringValue("name");
			String code = billvo.getStringValue("code");
			int count=MessageBox.showConfirmDialog(this,"ȷ��Ҫ������Ϊ["+name+"],����Ϊ["+code+"]����������Ϊ[1]��? ���ȷ�����ã�����ر�ȡ��");
			if(count==0){
            }else{
            	return;
            }
		}
		
	}

}
