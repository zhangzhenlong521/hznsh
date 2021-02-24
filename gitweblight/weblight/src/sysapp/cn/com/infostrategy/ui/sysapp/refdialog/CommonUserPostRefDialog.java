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
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

/**
 * 左边机构树，右边人员岗位列表
 * 可根据传参，返回人员、机构和岗位信息【李春娟/2016-04-14】
 */
public class CommonUserPostRefDialog extends AbstractRefDialog implements ActionListener, BillTreeSelectListener {
	private WLTButton btn_confirm;
	private WLTButton btn_cancel;
	private BillTreePanel billTree_dept; //机构树
	private BillListPanel billList_user_post; //人员岗位列表
	private RefItemVO refItemVO;
	private BillCardPanel card_panel;
	private String deptid;
	private String postid;
	private String filter;
	private String deptTempletCode = "PUB_CORP_DEPT_1";
	private String userTempletCode = "PUB_USER_POST_DEFAULT";

	public CommonUserPostRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
		this(_parent, _title, refItemVO, panel, null, null, null, null, null);
	}

	public CommonUserPostRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel, String deptid) {
		this(_parent, _title, refItemVO, panel, deptid, null, null, null, null);
	}

	public CommonUserPostRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel, String deptid, String postid) {
		this(_parent, _title, refItemVO, panel, deptid, postid, null, null, null);
	}

	/**
	 * 
	 * @param _parent
	 * @param _title
	 * @param refItemVO
	 * @param panel
	 * @param deptid  机构字段名称
	 * @param postid  岗位字段名称
	 * @param templetcode1 机构模板
	 * @param templetcode2 人员岗位模板
	 * @param filter  机构过滤条件
	 */
	public CommonUserPostRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel, String deptid, String postid, String templetcode1, String templetcode2, String filter) {
		super(_parent, _title, refItemVO, panel);
		this.refItemVO = refItemVO;
		this.deptid = deptid;
		this.postid = postid;
		if (templetcode1 != null && !"".equals(templetcode1.trim())) {
			this.deptTempletCode = templetcode1;
		}
		if (templetcode2 != null && !"".equals(templetcode2.trim())) {
			this.userTempletCode = templetcode2;
		}

		this.filter = filter;
		if (panel instanceof BillCardPanel) {
			this.card_panel = (BillCardPanel) panel;
		}
	}

	public RefItemVO getReturnRefItemVO() {
		return refItemVO;
	}

	public void initialize() {
		try {
			this.setLayout(new BorderLayout());
			this.setSize(800, 600);
			Pub_Templet_1VO[] templetvos = UIUtil.getPub_Templet_1VOs(new String[] { deptTempletCode, userTempletCode });//
			billTree_dept = new BillTreePanel(templetvos[0]); // 机构
			if (filter != null && !"".equals(filter.trim())) {
				billTree_dept.queryDataByCondition(filter); //
			} else {
				billTree_dept.queryDataByCondition(null); //
			}
			billTree_dept.addBillTreeSelectListener(this);
			billList_user_post = new BillListPanel(templetvos[1]);
			billList_user_post.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			WLTSplitPane splitPane_2 = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, billTree_dept, billList_user_post); // 左右的分割条
			splitPane_2.setDividerLocation(230); //
			splitPane_2.setDividerSize(2);

			JPanel southPanel = new JPanel();
			southPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			btn_confirm = new WLTButton("确定");
			btn_cancel = new WLTButton("取消");
			btn_confirm.addActionListener(this);
			btn_cancel.addActionListener(this);
			southPanel.add(btn_confirm);
			southPanel.add(btn_cancel);

			this.getContentPane().add(splitPane_2, BorderLayout.CENTER);
			this.getContentPane().add(southPanel, BorderLayout.SOUTH);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void actionPerformed(ActionEvent e) {
		if (btn_confirm == e.getSource()) {
			onConfirm();
		} else if (btn_cancel == e.getSource()) {
			onCancel();
		}
	}

	private void onCancel() {
		this.setCloseType(BillDialog.CANCEL); //
		this.dispose();
	}

	private void onConfirm() {
		BillVO billvo = billList_user_post.getSelectedBillVO();
		if (billList_user_post.getSelectedBillVO() == null) {
			MessageBox.show(this, "请选择人员信息！");
			return;
		}
		HashVO hashvo = new HashVO();
		hashvo.setAttributeValue("id", new StringItemVO(billvo.getStringValue("userid")));
		hashvo.setAttributeValue("code", new StringItemVO(billvo.getStringValue("usercode")));
		hashvo.setAttributeValue("name", new StringItemVO(billvo.getStringValue("username")));
		refItemVO = new RefItemVO(hashvo); //

		if (card_panel != null) {
			if (deptid != null && !"".equals(deptid.trim())) {
				card_panel.setValueAt(deptid, new RefItemVO(billvo.getStringValue("deptid"), "", billvo.getStringValue("deptname")));
			}
			if (postid != null && !"".equals(postid.trim())) {
				card_panel.setValueAt(postid, new RefItemVO(billvo.getStringValue("postid"), "", billvo.getStringValue("postname")));
			}
		}
		this.setCloseType(BillDialog.CONFIRM); //
		this.dispose(); //
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		billList_user_post.queryDataByCondition("deptid='" + _event.getCurrSelectedVO().getStringValue("id") + "'", "seq");
	}
}
