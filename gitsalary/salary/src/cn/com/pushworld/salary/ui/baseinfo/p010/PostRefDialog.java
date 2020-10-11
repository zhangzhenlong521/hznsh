package cn.com.pushworld.salary.ui.baseinfo.p010;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
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
 * 岗位参照
 */
public class PostRefDialog extends AbstractRefDialog implements ActionListener, BillTreeSelectListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5643896117311505086L;
	private WLTButton btn_confirm;
	private WLTButton btn_cancel;
	private BillTreePanel billTree_dept; // ..
	private BillListPanel billList_post; // ..
	private RefItemVO refItemVO;

	public PostRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
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

			Pub_Templet_1VO[] templetvos = UIUtil.getPub_Templet_1VOs(new String[] { "PUB_CORP_DEPT_1", "PUB_POST_CODE1_SALARY" });// 一次远程调用获得本页面所有模板【李春娟/2012-03-28】

			billTree_dept = new BillTreePanel(templetvos[0]); // 机构
			billTree_dept.queryDataByCondition(null); //
			billTree_dept.addBillTreeSelectListener(this);

			billList_post = new BillListPanel(templetvos[1]);
			billList_post.getQuickQueryPanel().setVisible(false);
			billList_post.getBillListBtnPanel().setVisible(false);
			billList_post.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			WLTSplitPane splitPanel = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTree_dept, billList_post);
			this.getContentPane().add(splitPanel, BorderLayout.CENTER);
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
		BillVO billvo[] = billList_post.getSelectedBillVOs();
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

		try {
			HashVO deptvo[] = UIUtil.getHashVoArrayByDS(null, "select t1.id,t2.code,t2.name from  pub_post t1 left join pub_corp_dept t2 on  t1.deptid = t2.id where t1.id in(" + TBUtil.getTBUtil().getInCondition(sb_postid.toString()) + ")");
			StringBuffer sb_deptid = new StringBuffer(";");
			StringBuffer sb_deptcode = new StringBuffer();
			StringBuffer sb_deptname = new StringBuffer();
			for (int i = 0; i < deptvo.length; i++) {
				sb_deptid.append(deptvo[i].getStringValue("id") + ";");
				sb_deptcode.append(deptvo[i].getStringValue("code") + ";");
				sb_deptname.append(deptvo[i].getStringValue("name") + ";");
			}
			if (getBillPanel() instanceof BillCardPanel) {
				BillCardPanel cardPanel = (BillCardPanel) getBillPanel();
				cardPanel.setValueAt("detpid", new RefItemVO(sb_deptid.toString(), "", sb_deptname.toString()));
				cardPanel.setValueAt("deptname", new StringItemVO(sb_deptname.toString()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		refItemVO = new RefItemVO(hashvo); //
		this.setCloseType(BillDialog.CONFIRM); //
		this.dispose(); //
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		billList_post.queryDataByCondition("deptid='" + _event.getCurrSelectedVO().getStringValue("id") + "'", "seq");
	}
}
