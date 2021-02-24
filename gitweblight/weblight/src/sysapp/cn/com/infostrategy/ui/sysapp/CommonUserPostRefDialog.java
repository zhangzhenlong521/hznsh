package cn.com.infostrategy.ui.sysapp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

import org.apache.log4j.Logger;

import cn.com.infostrategy.bs.common.WLTStringUtils;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTLogger;
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
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

/**
 *  人员/岗位 参照
 *   根据岗位返回人员的参照
 */
public class CommonUserPostRefDialog extends AbstractRefDialog implements ActionListener, BillTreeSelectListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3930566885612254921L;
	
	private Logger log = WLTLogger.getLogger(CommonUserPostRefDialog.class);
	private WLTButton btn_confirm;
	private WLTButton btn_cancel;
	private BillTreePanel billTree_dept; // ..
	private BillListPanel billList_post; // ..
	private RefItemVO refItemVO;
//	private WLTButton  btn_show;
	private BillCardPanel card_panel;
	private BillQueryPanel query_panel;
	
	private String deptid;
	private String postid;
	private String filter;
	private String corpType;
	private int ignore;
	
	/**
	 * 
	 * @param _parent
	 * @param _title
	 * @param refItemVO
	 * @param panel
	 * @param deptid 机构字段名称
	 * @param postid 岗位字段名称
	 */
	public CommonUserPostRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel, String deptid, String postid) {
		super(_parent, _title, refItemVO, panel);
		this.refItemVO = refItemVO;
		
		this.deptid = deptid;
		this.postid = postid;
		if(panel instanceof BillCardPanel){
			this.card_panel=(BillCardPanel) panel;
		}else if(query_panel instanceof BillQueryPanel){
			query_panel = (BillQueryPanel) panel;
		}
	}
	
	public CommonUserPostRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel, String deptid, String postid, String filter) {
		super(_parent, _title, refItemVO, panel);
		this.refItemVO = refItemVO;
		
		this.deptid = deptid;
		this.postid = postid;
		this.filter = filter;
		if(panel instanceof BillCardPanel){
			this.card_panel=(BillCardPanel) panel;
		}else if(query_panel instanceof BillQueryPanel){
			query_panel = (BillQueryPanel) panel;
		}
	}
	
	public CommonUserPostRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel, String deptid, String postid, String filter, String corpType) {
		super(_parent, _title, refItemVO, panel);
		this.refItemVO = refItemVO;
		
		this.deptid = deptid;
		this.postid = postid;
		this.filter = filter;
		this.corpType = corpType;
		if(panel instanceof BillCardPanel){
			this.card_panel=(BillCardPanel) panel;
		}else if(query_panel instanceof BillQueryPanel){
			query_panel = (BillQueryPanel) panel;
		}
	}
	
	/**
	 * 
	 * @param _parent
	 * @param _title
	 * @param refItemVO
	 * @param panel
	 * @param deptid
	 * @param postid
	 * @param filter
	 * @param corpType
	 * @param ignore 1:忽略 filter;2:忽略 corpType;3:忽略 filter,corpType;4:不忽略
	 */
	public CommonUserPostRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel, String deptid, String postid, String filter, String corpType, int ignore) {
		super(_parent, _title, refItemVO, panel);
		this.refItemVO = refItemVO;
		
		this.deptid = deptid;
		this.postid = postid;
		this.filter = filter;
		this.corpType = corpType;
		this.ignore = ignore;
		if(panel instanceof BillCardPanel){
			this.card_panel=(BillCardPanel) panel;
		}else if(query_panel instanceof BillQueryPanel){
			query_panel = (BillQueryPanel) panel;
		}
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

			Pub_Templet_1VO[] templetvos = UIUtil.getPub_Templet_1VOs(new String[] { "PUB_CORP_DEPT_1", "PUB_USER_POST_DEFAULT_1001" });//

			billTree_dept = new BillTreePanel(templetvos[0]); // 机构
			if("总行部门".equals(corpType)){
				billTree_dept.queryDataByCondition("corptype='高管层'"); //
			}else{
				if(WLTStringUtils.hasText(filter)){
					billTree_dept.queryDataByCondition(filter); //
				}else{
					billTree_dept.queryDataByCondition(null); //
				}
			}
			billTree_dept.addBillTreeSelectListener(this);

			billList_post = new BillListPanel(templetvos[1]);
			billList_post.setItemVisible("post_status", false);//隐藏岗位状态
			billList_post.getQuickQueryPanel().setVisible(false);
			billList_post.getBillListBtnPanel().setVisible(false);
			billList_post.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			billList_post.repaintBillListButton();
//			btn_show = new WLTButton("浏览");
//			btn_show.addActionListener(this);
			WLTSplitPane splitPane_2 = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, billTree_dept, billList_post); // 左右的分割条
			splitPane_2.setDividerLocation(220); //
			splitPane_2.setDividerSize(2);

			this.getContentPane().add(splitPane_2, BorderLayout.CENTER);
			this.getContentPane().add(southPanel, BorderLayout.SOUTH);
		} catch (Exception e) {
			log.error(e);
		}

	}

//	protected void onImport(int tabIndex) {}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (btn_confirm == e.getSource()) {
			onConfirm();
		} else if (btn_cancel == e.getSource()) {
			onCancel();
		}/* else if (btn_show == e.getSource()) {
			onShowTemp();
		}*/
	}

//	private void onShowTemp() {}

	private void onCancel() {
		this.setCloseType(BillDialog.CANCEL); //
		this.dispose();
	}

	private void onConfirm() {
		BillVO billvo = billList_post.getSelectedBillVO();
		if (billList_post.getSelectedBillVO()==null) {
			MessageBox.show(this, "请选择相关信息！");
			return;
		}
		HashVO hashvo = new HashVO();
		hashvo.setAttributeValue("id", new StringItemVO(billvo.getStringValue("userid")));
		hashvo.setAttributeValue("code", new StringItemVO(billvo.getStringValue("usercode")));
		hashvo.setAttributeValue("name", new StringItemVO(billvo.getStringValue("username")));
		refItemVO = new RefItemVO(hashvo); //
		
		if(null!=card_panel){
			if(WLTStringUtils.hasLength(deptid)){
				card_panel.setValueAt(deptid, new RefItemVO(billvo.getStringValue("deptid"),"",billvo.getStringValue("deptname")));
			}
			if(WLTStringUtils.hasLength(postid)){
				card_panel.setValueAt(postid, new RefItemVO(billvo.getStringValue("postid"),"",billvo.getStringValue("postname")));
			}
		}
		this.setCloseType(BillDialog.CONFIRM); //
		this.dispose(); //
	}

	@Override
	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		billList_post.queryDataByCondition("deptid='" + _event.getCurrSelectedVO().getStringValue("id") + "'", "seq");
	}
}
