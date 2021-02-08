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
		pwd_reset = new WLTButton("密码重置(慎用)");
		pwd_reset.addActionListener(this);
		pwd_decode = new WLTButton("密码解密");
		pwd_decode.addActionListener(this);
		pwd_giveadmin = new WLTButton("给予管理员权限");
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
			MessageBox.show(listPanel, "请您选择一条记录再执行此操作！");
			return;
		}else{
			String name = billvo.getStringValue("name");
			String code = billvo.getStringValue("code");
			int count=MessageBox.showConfirmDialog(this,"确定要将姓名为["+name+"],工号为["+code+"]的密码重置为[1]吗? 点击确定重置，点击关闭取消");
			if(count==0){
            }else{
            	return;
            }
		}
		
	}

}
