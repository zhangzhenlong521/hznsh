package com.pushworld.ipushgrc.ui.HR.p050;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
/**
 * 
 * @author longlonggo521
 *  职称津贴维护
 */
public class TheTitleWorkPanel extends AbstractWorkPanel implements
ActionListener{

	BillListPanel list=null;
	WLTButton onnew=null;

	@Override
	public void initialize() {
		list=new BillListPanel("PUB_COMBOBOXDICT_ZZL_CODE2");
		onnew=new WLTButton("新增");
		onnew.addActionListener(this);
		list.addBatchBillListButton(new WLTButton[]{onnew});
		list.repaintBillListButton();
		this.add(list);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==onnew){
			final BillCardDialog db=new BillCardDialog(this,"职称津贴维护","PUB_COMBOBOXDICT_ZZL_CODE1",600,400);
			db.setSaveBtnVisiable(false);
			db.getBtn_confirm().addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					try {
						String str_id = UIUtil.getSequenceNextValByDS(null, "s_pub_comboboxdict");
						BillVO vo=db.getBillcardPanel().getBillVO();
						list.insertRow(list.getRowCount(), vo);
						InsertSQLBuilder sql=new InsertSQLBuilder("pub_comboboxdict");
						sql.putFieldValue("id", str_id);
						sql.putFieldValue("type", vo.getStringValue("type"));
						sql.putFieldValue("name", vo.getStringValue("name"));
						sql.putFieldValue("code", vo.getStringValue("code"));
						sql.putFieldValue("descr", vo.getStringValue("descr"));
						sql.putFieldValue("seq", vo.getStringValue("seq"));
						UIUtil.executeUpdateByDSPS(null, sql.getSQL());
						db.dispose();
					} catch (WLTRemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			});
			db.setVisible(true);


		}
		
	}

}

