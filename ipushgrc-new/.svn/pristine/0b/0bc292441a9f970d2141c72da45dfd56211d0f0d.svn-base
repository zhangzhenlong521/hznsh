package com.pushworld.ipushgrc.ui.bsd.product;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.styletemplet.t03.AbstractStyleWorkPanel_03;

public class ProductWKPanel extends AbstractStyleWorkPanel_03{

	@Override
	public String getTempletcode() {
		return "BSD_PRODUCT_CODE1";
	}
	
	public void onInsert(){
		BillTreePanel treePanel = getBillTreePanel();
		if(treePanel.getSelectedPath() == null){
			MessageBox.showSelectOne(this);
			return;
		}
		BillVO treeVO = treePanel.getSelectedVO();
		int level = treePanel.getSelectedNode().getLevel();
		BillCardPanel cardPanel = new BillCardPanel("BSD_PRODUCT_CODE1");
		cardPanel.insertRow(); //新增行！
		cardPanel.setEditableByInsertInit();
		cardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
		if(treeVO != null){ //非根节点!
			String parentid = treeVO.getStringValue("id");
			cardPanel.setValueAt("parentid", new StringItemVO(parentid));
		}
		cardPanel.setValueAt("seq",new StringItemVO(treePanel.getSelectedNode().getChildCount()+""));
		if(level <= 2){
			cardPanel.setValueAt("datatype", new ComBoxItemVO("业务类型","业务类型","业务类型"));	
		}else{
			cardPanel.setValueAt("datatype", new ComBoxItemVO("产品","产品","产品"));
		}
		BillCardDialog cardDialog = new BillCardDialog(this,"新增",cardPanel,WLTConstants.BILLDATAEDITSTATE_INSERT);
		cardDialog.setVisible(true);
		if(cardDialog.getCloseType() == 1){
			BillVO newvo = cardDialog.getBillVO();
			treePanel.addNode(newvo);
		}
	}
	public void actionAfterInsert(BillCardPanel billcardpanel) throws Exception {
		
	}

	public void actionAfterUpdate(BillCardPanel billcardpanel, String s)
			throws Exception {
		
	}

	public void actionBeforeDelete(BillCardPanel billcardpanel)
			throws Exception {
		
	}

	public void afterInitialize(
			AbstractStyleWorkPanel_03 abstractstyleworkpanel_03)
			throws Exception {
		
	}

	public void dealCommitAfterDelete(
			AbstractStyleWorkPanel_03 abstractstyleworkpanel_03, BillVO billvo) {
		
	}

	public void dealCommitAfterInsert(
			AbstractStyleWorkPanel_03 abstractstyleworkpanel_03, BillVO billvo) {
		
	}

	public void dealCommitAfterUpdate(
			AbstractStyleWorkPanel_03 abstractstyleworkpanel_03, BillVO billvo) {
		
	}

	public void dealCommitBeforeDelete(
			AbstractStyleWorkPanel_03 abstractstyleworkpanel_03, BillVO billvo)
			throws Exception {
		
	}

	public void dealCommitBeforeInsert(
			AbstractStyleWorkPanel_03 abstractstyleworkpanel_03, BillVO billvo)
			throws Exception {
		
	}

	public void dealCommitBeforeUpdate(
			AbstractStyleWorkPanel_03 abstractstyleworkpanel_03, BillVO billvo)
			throws Exception {
		
	}
}
