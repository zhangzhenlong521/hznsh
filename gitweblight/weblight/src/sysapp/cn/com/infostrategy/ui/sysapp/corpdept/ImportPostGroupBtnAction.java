package cn.com.infostrategy.ui.sysapp.corpdept;

import javax.swing.ListSelectionModel;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;

/**
 * 岗位维护，导入标准岗位按钮事件！！
 * @author hj
 * Feb 2, 2012 5:06:06 PM
 */
public class ImportPostGroupBtnAction implements WLTActionListener {
	private BillCardPanel cardPanel = null;

	public void actionPerformed(WLTActionEvent _event) throws Exception {
		cardPanel = (BillCardPanel) _event.getBillPanelFrom();
		if (cardPanel.getEditState().equals(WLTConstants.BILLDATAEDITSTATE_INIT)) { //如果面板是浏览状态，该按钮失效！
			MessageBox.show(cardPanel, "浏览状态不允许执行此操作！");
			return;
		}
		showPostGroupsDialog();
	}

	private void showPostGroupsDialog() {
		BillListDialog listdialog = new BillListDialog(cardPanel, "关联标准岗位", "PUB_POST_CODE1", " deptid is null ", 800, 500);//机构为空的岗位是岗位组
		listdialog.getBilllistPanel().setDataFilterCustCondition("deptid is null");//加上数据过滤，解决点击查询时数据不对【李春娟/2014-03-13】
		listdialog.getBilllistPanel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listdialog.setVisible(true);
		if (listdialog.getCloseType() == 1) {//所选岗位组相关内容会自动加载到岗位表中
			BillVO billvo = listdialog.getReturnBillVOs()[0]; //取得返回的BillVO
			cardPanel.setValueAt("name", billvo.getObject("name"));
			cardPanel.setValueAt("postlevel", billvo.getObject("postlevel"));
			cardPanel.setValueAt("descr", billvo.getObject("descr"));
			cardPanel.setValueAt("intent", billvo.getObject("intent"));
			cardPanel.setValueAt("innercontact", billvo.getObject("innercontact"));
			cardPanel.setValueAt("outcontact", billvo.getObject("outcontact"));
			cardPanel.setValueAt("education", billvo.getObject("education"));
			cardPanel.setValueAt("skill", billvo.getObject("skill"));
			cardPanel.setValueAt("refpostid", new RefItemVO(billvo.getStringValue("id"), "", billvo.getStringValue("name")));
		}
	}
}
