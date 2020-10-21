package com.pushworld.icheck.ui.p090;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_Ref;
/**
 * 
 * @author longlonggo521
 * ZZL[2017-1-19]
 * 昌吉客户提出计划中选择了检查人员，方案中只显示计划中的人员
 */

public class CheckCommSelectUserRefDialog extends AbstractRefDialog implements ActionListener{
	private BillListPanel list=null;
	private WLTButton btn_confirm, btn_cancel, btn_adduser, btn_deleteuser;
	private RefItemVO returnRefItemVO = null;
	private BillCardPanel billCardPanel = null;
	private Container parent;
	private String itemKey;
	private String id;

	public CheckCommSelectUserRefDialog(Container parent, String title,
			RefItemVO initRefItemVO, BillPanel billPanel) {
		super(parent, title, initRefItemVO, billPanel);
		billCardPanel = (BillCardPanel) billPanel;
		parent = parent;
		CardCPanel_Ref ref = (CardCPanel_Ref) parent;
		itemKey = ref.getItemKey();
		BillCardPanel cardPanel = (BillCardPanel) billPanel;
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public RefItemVO getReturnRefItemVO() {
		// TODO Auto-generated method stub
		return returnRefItemVO;
	}

	@Override
	public void initialize() {
		RefItemVO refItemVO = (RefItemVO) billCardPanel.getValueAt(itemKey);
		String id=billCardPanel.getBillVO().getStringValue("planid");
		String teamusers=null;
		try {
			teamusers=UIUtil.getStringValueByDS(null,"select teamusers from CK_PLAN where id='"+id+"'");
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		teamusers=teamusers.replace(";", ",");
		teamusers=teamusers.substring(1,teamusers.length()-1);
		list=new BillListPanel("PUB_USER_CODE1");
		list.QueryData("select * from pub_user where id in("+teamusers+")");
		this.add(list,BorderLayout.CENTER);
		this.add(getSouthPanel(), BorderLayout.SOUTH); 
		
	}
	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
	    btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");

		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //

		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==btn_confirm){
			BillVO billVO_list = list.getSelectedBillVO();
			if (billVO_list.getStringValue("id") == null) {
				MessageBox.show(this, "请加入人员!"); //
				return;
			}
			returnRefItemVO = new RefItemVO(); //
			returnRefItemVO.setId(billVO_list.getStringValue("id").toString()); //
			returnRefItemVO.setCode(null);
			returnRefItemVO.setName(billVO_list.getStringValue("name").toString()); //
			this.setCloseType(BillDialog.CONFIRM); //
			this.dispose(); //
		}else if(e.getSource()==btn_cancel){
			onCancel();
		}
		
		
	}
	private void onCancel() {
		this.setCloseType(BillDialog.CANCEL); //
		this.dispose(); //
	}

}
