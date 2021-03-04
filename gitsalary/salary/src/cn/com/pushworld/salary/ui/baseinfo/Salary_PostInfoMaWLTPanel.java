package cn.com.pushworld.salary.ui.baseinfo;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardFrame;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

/**
 * 岗位管理
 * @author Administrator
 *
 */
public class Salary_PostInfoMaWLTPanel extends AbstractWorkPanel implements
		BillTreeSelectListener, ActionListener {
	private BillTreePanel corp_tree = null; // 机构树
	private BillListPanel post_list = null; // 岗位列表
	private WLTSplitPane main_spit = null; // 主分隔面板
	private WLTButton editBtn; 

	public void initialize() {
		corp_tree = new BillTreePanel("PUB_CORP_DEPT_SELF");
		corp_tree.queryDataByCondition(" 1=1 ");
		corp_tree.addBillTreeSelectListener(this);
		post_list = new BillListPanel("PUB_POST_CODE_SALARY"); // PUB_USER_POST_DEFAULT
		post_list.setItemVisible("PK_DEPT", false);
		post_list.setBillListTitleName("岗位系数");
		initBtn();
		main_spit = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, corp_tree, post_list);
		main_spit.setDividerLocation(200);
		this.add(main_spit, BorderLayout.CENTER);
	}
	
	public void initBtn() {
		editBtn = new WLTButton("编辑", "user_edit.png");
		editBtn.addActionListener(this);
		post_list.addBatchBillListButton(new WLTButton[] {editBtn});
		post_list.repaintBillListButton();
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent event) {
		if (event.getCurrSelectedVO() != null && event.getCurrSelectedVO().getPkValue() != null) {
			String str_currdeptid = event.getCurrSelectedVO().getStringValue("id");
			post_list.queryDataByCondition("deptid='" + str_currdeptid + "'", "seq");
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == editBtn) {
			onEdit(true); 
		}
	}
	
	public void onEdit(boolean iseditable) {
		BillVO postvo = post_list.getSelectedBillVO();
		if (postvo == null) {
			MessageBox.show(post_list, "请选择一条岗位信息来进行此操作!");
			return;
		}
		String str_postid = postvo.getPkValue();
		BillCardPanel post_card = new BillCardPanel("PUB_POST_CODE_SALARY");
		post_card.queryDataByCondition("id=" + str_postid);
		String edittype = WLTConstants.BILLDATAEDITSTATE_INIT;
		if (iseditable) {
			post_card.setEditableByEditInit();
			edittype = WLTConstants.BILLDATAEDITSTATE_UPDATE;
			post_card.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
			BillCardDialog bd = new BillCardDialog(post_list, "岗位信息编辑", post_card, edittype);
			bd.setVisible(true);
			post_list.refreshCurrSelectedRow();
		} else {
			BillCardFrame bf = new BillCardFrame(post_list, "岗位信息查看", post_card, edittype);
			bf.setVisible(true);
		}
	}
}
