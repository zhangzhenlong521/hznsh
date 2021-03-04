package cn.com.infostrategy.ui.sysapp.refdialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

/**
 * 岗位参照
 */
public class CommonPostRefDialog extends AbstractRefDialog implements ActionListener, BillTreeSelectListener {
	private WLTButton btn_confirm;
	private WLTButton btn_cancel;
	private BillTreePanel billTree_dept; // ..
	private BillListPanel billList_post; // ..
	private BillListPanel billList_temp;
	private BillListPanel list_postgroup; // 岗位组列表
	private RefItemVO refItemVO;
	private WLTButton btn_import_1, btn_import_2, btn_show;

	public CommonPostRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
		super(_parent, _title, refItemVO, panel);
		this.refItemVO = refItemVO;
	}

	@Override
	public RefItemVO getReturnRefItemVO() {
		return refItemVO;
	}

	@Override
	public void initialize() {
		try {
			WLTTabbedPane tabPane = new WLTTabbedPane();
			this.setLayout(new BorderLayout());
			this.setSize(800, 600);
			btn_confirm = new WLTButton("确定");
			btn_cancel = new WLTButton("取消");
			btn_confirm.addActionListener(this);
			btn_cancel.addActionListener(this);

			JPanel southPanel = new JPanel();
			southPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			southPanel.add(btn_confirm);
			southPanel.add(btn_cancel);

			Pub_Templet_1VO[] templetvos = UIUtil.getPub_Templet_1VOs(new String[] { "PUB_CORP_DEPT_1", "PUB_POST_CODE1", "PUB_POST_CODE2" });//一次远程调用获得本页面所有模板【李春娟/2012-03-28】

			billTree_dept = new BillTreePanel(templetvos[0]); // 机构
			billTree_dept.queryDataByCondition(null); //
			billTree_dept.addBillTreeSelectListener(this);

			billList_post = new BillListPanel(templetvos[1]);
			billList_post.setItemVisible("post_status", false);//隐藏岗位状态
			billList_post.getQuickQueryPanel().setVisible(false);
			billList_post.getBillListBtnPanel().setVisible(false);

			billList_temp = new BillListPanel(templetvos[1]); // 岗位信息
			billList_temp.setItemVisible("post_status", false);//隐藏岗位状态
			billList_temp.setItemVisible("deptid", true);
			if (refItemVO != null && refItemVO.getId() != null) {
				String ids = refItemVO.getId();
				TBUtil tbutil = new TBUtil();
				billList_temp.queryDataByCondition("id in(" + tbutil.getInCondition(tbutil.split(ids, ";")) + ")", "seq");// 显示已选信息
			}
			billList_temp.setAllBillListBtnVisiable(false);
			btn_import_1 = new WLTButton("加入");
			btn_import_1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onImport(0);
				}

			});
			btn_import_2 = new WLTButton("加入");
			btn_import_2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onImport(1);
				}
			});
			billList_post.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			billList_post.addBatchBillListButton(new WLTButton[] { btn_import_1 });
			billList_post.repaintBillListButton();
			WLTButton list_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETEROW, "删除");
			btn_show = new WLTButton("浏览");
			btn_show.addActionListener(this);
			billList_temp.addBatchBillListButton(new WLTButton[] { list_delete, btn_show });
			billList_temp.repaintBillListButton();
			WLTSplitPane splitPane_2 = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, billTree_dept, billList_post); // 左右的分割条
			splitPane_2.setDividerLocation(220); //
			splitPane_2.setDividerSize(2);
			list_postgroup = new BillListPanel(templetvos[2]);
			list_postgroup.setItemVisible("post_status", false);//隐藏岗位状态
			list_postgroup.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list_postgroup.addBatchBillListButton(new WLTButton[] { btn_import_2 });
			list_postgroup.repaintBillListButton();
			list_postgroup.QueryDataByCondition("  deptid is null ");
			list_postgroup.setDataFilterCustCondition(" deptid is null "); // 机构为空的是岗位组

			tabPane.addTab("机构岗位", splitPane_2);
			tabPane.addTab("岗位组", list_postgroup);
			WLTSplitPane splitPane_3 = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, tabPane, billList_temp); // 上下分割
			splitPane_3.setDividerLocation(350);
			this.getContentPane().add(splitPane_3, BorderLayout.CENTER);
			this.getContentPane().add(southPanel, BorderLayout.SOUTH);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void onImport(int tabIndex) {
		if (billTree_dept.getSelectedVO() == null && tabIndex == 0) {// 如果选中机构岗位
			MessageBox.show(this, "请选择相关部门！");
			return;
		}
		BillVO[] temp_billvo = billList_temp.getAllBillVOs();
		if ((billList_post.getSelectedBillVO() == null && tabIndex == 0) || (list_postgroup.getSelectedBillVO() == null && tabIndex == 1)) { // 判断机构岗位页签和岗位组页签是否选择数据
			MessageBox.show(this, "请选择一条岗位信息再进行此操作！");
			return;
		} else {// 选择岗位
			BillVO billvo = null;
			if (tabIndex == 0) { // 机构岗位页签
				billvo = billList_post.getSelectedBillVO();
			} else {// 岗位组页签
				billvo = list_postgroup.getSelectedBillVO();
			}
			for (int i = 0; i < temp_billvo.length; i++) {
				if (temp_billvo[i].getStringValue("id").equals(billvo.getStringValue("id"))) {
					MessageBox.show(this, "请不要添加重复记录！");
					return;
				}
			}
			int i = billList_temp.newRow();
			billList_temp.setValueAt(new StringItemVO(billvo.getStringValue("id")), i, "id");
			if ("".equals(billvo.getStringValue("deptid", ""))) {
				billList_temp.setValueAt(null, i, "deptid");
			} else {
				billList_temp.setValueAt(new RefItemVO(billvo.getStringValue("deptid"), "", billvo.getStringViewValue("deptid")), i, "deptid");
			}
			billList_temp.setValueAt(new StringItemVO(billvo.getStringValue("code", " ")), i, "code");
			billList_temp.setValueAt(new StringItemVO(billvo.getStringValue("name", " ")), i, "name");
			billList_temp.setValueAt(new StringItemVO(billvo.getStringValue("post_status", " ")), i, "post_status");
			billList_temp.setValueAt(new RefItemVO(billvo.getStringValue("refpostid"), "", billvo.getStringViewValue("refpostid")), i, "refpostid");
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (btn_confirm == e.getSource()) {
			onConfirm();
		} else if (btn_cancel == e.getSource()) {
			onCancel();
		} else if (btn_show == e.getSource()) {
			onShowTemp();
		}
	}

	private void onShowTemp() {
		BillVO billvo = billList_temp.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		BillCardPanel cardPanel = new BillCardPanel(billList_temp.templetVO); //
		cardPanel.setBillVO(billvo); //
		if ("".equals(billvo.getStringValue("deptid", ""))) {//如果机构id为空，说明是岗位组，故卡片需要隐藏所属岗位组字段
			cardPanel.setVisiable("refpostid", false);
			cardPanel.setVisiable("import", false);
		}
		BillCardDialog dialog = new BillCardDialog(billList_temp, billList_temp.templetVO.getTempletname() + "[" + billvo.toString() + "]", cardPanel, WLTConstants.BILLDATAEDITSTATE_INIT);
		dialog.setVisible(true); //
	}

	private void onCancel() {
		this.setCloseType(BillDialog.CANCEL); //
		this.dispose();
	}

	private void onConfirm() {
		BillVO[] billvo = billList_temp.getAllBillVOs();
		if (billvo.length == 0) {
			MessageBox.show(this, "请选择相关信息！");
			return;
		}
		HashVO hashvo = new HashVO();
		StringBuffer sb_postid = new StringBuffer(";");
		StringBuffer sb_postcode = new StringBuffer();
		StringBuffer sb_postname = new StringBuffer();

		for (int i = 0; i < billvo.length; i++) {
			sb_postid.append(billvo[i].getStringValue("id") + ";");
			sb_postcode.append(billvo[i].getStringValue("code") + ";");
			sb_postname.append(billvo[i].getStringValue("name") + ";");
		}
		hashvo.setAttributeValue("id", new StringItemVO(sb_postid.toString()));
		hashvo.setAttributeValue("code", new StringItemVO(sb_postcode.toString()));
		hashvo.setAttributeValue("name", new StringItemVO(sb_postname.toString()));
		refItemVO = new RefItemVO(hashvo); //
		this.setCloseType(BillDialog.CONFIRM); //
		this.dispose(); //
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		billList_post.queryDataByCondition("deptid='" + _event.getCurrSelectedVO().getStringValue("id") + "'", "seq");
	}
}
