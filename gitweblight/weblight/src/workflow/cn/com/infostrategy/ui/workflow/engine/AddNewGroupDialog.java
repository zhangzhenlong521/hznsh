package cn.com.infostrategy.ui.workflow.engine;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

/**
 * 群组维护界面
 * @author sfj
 *
 */
public class AddNewGroupDialog extends BillDialog implements ActionListener, BillTreeSelectListener {
	private WLTButton btn_confirm, btn_cancel;
	private JTextField jt_name = null;
	private BillTreePanel corptree = null;//机构树
	private BillListPanel billList_user = null;//选机构出来人的列表
	private BillListPanel billList_user_shopcar = null;//购物车列表
	private WLTButton btn_addShopCarUser, btn_addAllShopCarUser, btn_delShopCarUser, btn_delAllShopCarUser;
	private String editType = null;//编辑状态
	private BillVO groupvo = null;
	private int limit = 100;//群组中人数的限制，如果有需要可以搞成平台参数

	public AddNewGroupDialog(Container _parent, BillVO _groupvo, String _editType) {
		super(_parent, "群组", 900, 600);
		this.groupvo = _groupvo;
		this.editType = _editType;
		this.initialize();
	}

	private void initialize() {
		this.setLayout(new BorderLayout());
		this.add(getNorthPanel(), BorderLayout.NORTH);
		this.add(getMainPanel(), BorderLayout.CENTER);
		this.add(getSourthPanel(), BorderLayout.SOUTH);
	}

	/**
	 * 主面板
	 * @return
	 */
	private JPanel getMainPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new BorderLayout());
		try {
			corptree = new BillTreePanel(new ServerTMODefine("/cn/com/infostrategy/bs/sysapp/install/templetdata/PUB_CORP_DEPT_1.xml"));
			billList_user = new BillListPanel(new ServerTMODefine("/cn/com/infostrategy/bs/sysapp/install/templetdata/pub_user_1.xml"));
			billList_user_shopcar = new BillListPanel(billList_user.getTempletVO().deepClone());
			billList_user_shopcar.setTitleLabelText("群组人员");
			if (this.groupvo != null) {
				HashMap parMap = new HashMap();
				parMap.put("groupVO", this.groupvo);
				parMap.put("isaddaccr", false);
				HashMap returnMap = UIUtil.commMethod("cn.com.infostrategy.bs.workflow.WorkFlowBSUtil", "getUserCorpAndUsersByGroup", parMap);
				HashVO[] hvs = (HashVO[]) returnMap.get("users");
				putUserDataByHashVO(billList_user_shopcar, hvs);
			}

			if ("init".equals(this.editType)) {
				panel.add(billList_user_shopcar, BorderLayout.CENTER);
			} else {
				HashVO[] hvs_mycorp = UIUtil.getParentCorpVOByMacro(1, null, "$本机构");
				String str_zhid = null;
				if (TBUtil.getTBUtil().getSysOptionBooleanValue("工作流提交时添加接收者是否永远包括总行", false)) {
					str_zhid = UIUtil.getStringValueByDS(null, "select id from pub_corp_dept where corptype='总行'");
				}
				if (hvs_mycorp != null) {
					String str_myParentCorpId = hvs_mycorp[0].getStringValue("id"); //
					String str_condition = null;
					if (str_zhid == null) {
						str_condition = "id = " + str_myParentCorpId + "";
					} else {
						str_condition = "id in (" + str_myParentCorpId + "," + str_zhid + ")";
					}
					HashVO[] hvs_myAllChildrens = UIUtil.getHashVoArrayAsTreeStructByDS(null, "select id,code,name,parentid,seq from pub_corp_dept", "id", "parentid", "seq", str_condition); //
					corptree.queryDataByDS(hvs_myAllChildrens, -1);
				} else {
					corptree.queryDataByDS(null, "select id,code,name,parentid,seq from pub_corp_dept");
				}
				corptree.addBillTreeSelectListener(this);
				btn_addShopCarUser = new WLTButton("添加", "office_059.gif");
				btn_delShopCarUser = new WLTButton("删除", "office_081.gif");
				btn_addAllShopCarUser = new WLTButton("全部添加", "office_160.gif");
				btn_delAllShopCarUser = new WLTButton("全部删除", "office_125.gif");
				btn_addShopCarUser.addActionListener(this);
				btn_delShopCarUser.addActionListener(this);
				btn_addAllShopCarUser.addActionListener(this);
				btn_delAllShopCarUser.addActionListener(this);
				//				JPanel jp = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
				//				jp.add(btn_addShopCarUser);
				//				jp.add(btn_addAllShopCarUser);
				//				jp.add(btn_delShopCarUser);
				//				jp.add(btn_delAllShopCarUser);
				//
				//				Box box = Box.createVerticalBox();
				//				box.add(billList_user);
				//				box.add(Box.createHorizontalStrut(20));
				//				box.add(jp);
				//				box.add(Box.createHorizontalStrut(20));
				//				box.add(billList_user_shopcar);
				//				box.add(Box.createHorizontalStrut(20));
				//徐总建议搞成分割面板/sunfujun/20121115
				billList_user_shopcar.addBatchBillListButton(new WLTButton[] { btn_addShopCarUser, btn_delShopCarUser, btn_addAllShopCarUser, btn_delAllShopCarUser });
				billList_user_shopcar.repaintBillListButton();
				WLTSplitPane splitPanel_right = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, billList_user, billList_user_shopcar);
				WLTSplitPane splitPanel = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, corptree, splitPanel_right);
				splitPanel.setDividerLocation(250);
				panel.add(splitPanel, BorderLayout.CENTER);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return panel;
	}

	/**
	 * 就是一个群组名称
	 * @return
	 */
	private JPanel getNorthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel jl = new WLTLabel("群组名称:");
		panel.add(jl);
		jt_name = new JTextField(30);
		if (this.groupvo != null) {
			jt_name.setText(groupvo.getStringValue("name"));
			if ("init".equals(this.editType)) {
				jt_name.setEditable(false);
			} else {
				jt_name.setEditable(true);
			}
			panel.add(jt_name);
		} else {
			jt_name.setEditable(true);
			panel.add(jt_name);
		}
		return panel;
	}

	private JPanel getSourthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout());
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");
		btn_cancel.addActionListener(this);
		btn_confirm.addActionListener(this);
		if (this.groupvo == null || "edit".equals(this.editType)) {
			panel.add(btn_confirm);
		}
		panel.add(btn_cancel);
		return panel;
	}

	public void actionPerformed(ActionEvent _event) {
		if (_event.getSource() == btn_confirm) {
			onConfirm();
		} else if (_event.getSource() == btn_cancel) {
			onCancel();
		} else if (_event.getSource() == btn_addShopCarUser) {
			onAddShopCarUser();
		} else if (_event.getSource() == btn_delShopCarUser) {
			onDelShopCarUser();
		} else if (_event.getSource() == btn_addAllShopCarUser) {
			onAddAllShopCarUser();
		} else if (_event.getSource() == btn_delAllShopCarUser) {
			onDelAllShopCarUser();
		}
	}

	/**
	 * 添加按钮逻辑
	 */
	private void onAddShopCarUser() {
		BillVO[] billVOs = billList_user.getSelectedBillVOs();
		if (billVOs == null || billVOs.length <= 0) {
			MessageBox.show(this, "请从上方人员列表中选择一个或多个人员进行此操作!");
			return;
		}
		onAddShopCarUserByBillVOs(billVOs);
	}

	/**
	 * 全部添加按钮的逻辑
	 */
	private void onAddAllShopCarUser() {
		BillVO[] billVOs = billList_user.getAllBillVOs();
		if (billVOs == null || billVOs.length <= 0) {
			MessageBox.show(this, "没有可以添加的人员,无法进行此操作!");
			return;
		}
		onAddShopCarUserByBillVOs(billVOs);
	}

	/**
	 * 向群组中添加人员的逻辑，添加按钮与全部添加按钮需要调用此逻辑
	 * 需要判断是否重复，以及是否超过最大人数像QQ一样
	 * @param _billVOs
	 */
	private void onAddShopCarUserByBillVOs(BillVO[] _billVOs) {
		HashSet hst = new HashSet();
		BillVO[] billVOs_shopCardUser = billList_user_shopcar.getAllBillVOs();
		if (billVOs_shopCardUser != null && billVOs_shopCardUser.length > 0) {
			if (billVOs_shopCardUser.length >= limit) {
				MessageBox.show(this, "群组已达最大人数!");
				return;
			}
			for (int i = 0; i < billVOs_shopCardUser.length; i++) {
				String str_userid = billVOs_shopCardUser[i].getStringValue("userid");
				if (str_userid != null) {
					hst.add(str_userid);
				}
			}
		}
		List canadd = new ArrayList();
		StringBuilder sb_reduplicate_user = new StringBuilder();
		for (int i = 0; i < _billVOs.length; i++) {
			String str_userid = _billVOs[i].getStringValue("userid");
			String str_usercode = _billVOs[i].getStringValue("usercode");
			String str_username = _billVOs[i].getStringValue("username");
			if (hst.contains(str_userid)) {
				sb_reduplicate_user.append("【" + str_usercode + "/" + str_username + "】");
			} else {
				canadd.add(_billVOs[i]);
			}
		}
		//		String str_reduplicate_user = sb_reduplicate_user.toString();
		//		if (!str_reduplicate_user.equals("")) {
		//			MessageBox.show(this, "用户" + str_reduplicate_user + "已经添加过了,不能重复添加!");
		////			return;
		//		}
		//重复了不加进来就好了可以不必提示还要重选更不太好/sunfujun/20121115
		if (billVOs_shopCardUser.length + canadd.size() > limit) {//超过最大人数不能全部加入，将多余的人去掉
			MessageBox.show(this, "群组已达最大人数!无法全部添加!");
			List canadd_limit = new ArrayList();
			for (int i = 0; i < limit - billVOs_shopCardUser.length; i++) {
				canadd_limit.add(canadd.get(i));
			}
			billList_user_shopcar.addBillVOs((BillVO[]) canadd_limit.toArray(new BillVO[0]));
		} else {//如果加起来没超过最大限制就直接加进来
			billList_user_shopcar.addBillVOs((BillVO[]) canadd.toArray(new BillVO[0]));
		}
	}

	/**
	 * 移除群组中选中的人员
	 */
	private void onDelShopCarUser() {
		BillVO[] billVOs = billList_user_shopcar.getSelectedBillVOs();
		if (billVOs == null || billVOs.length <= 0) {
			MessageBox.show(this, "请从群组人员列表中选择一个或多个人员进行此操作!");
			return;
		}
		if (!MessageBox.confirm(this, "您确定要删除选中的人员吗?")) {
			return;
		}
		billList_user_shopcar.removeSelectedRows();
	}

	/**
	 * 移除群组中的所有人员
	 */
	private void onDelAllShopCarUser() {
		int li_count = billList_user_shopcar.getRowCount();
		if (li_count <= 0) {
			MessageBox.show(this, "群组人员为空,无法进行此操作!");
			return;
		}
		if (!MessageBox.confirm(this, "您确定要删除群组中所有人员吗?")) {
			return;
		}
		billList_user_shopcar.clearTable();
	}

	/**
	 * 确定
	 */
	public void onConfirm() {
		try {
			if (jt_name.getText() == null || "".equals(jt_name.getText().trim())) {
				MessageBox.show(this, "群组名称不能为空!");
				jt_name.grabFocus();
				return;
			}

			BillVO[] vos = billList_user_shopcar.getAllBillVOs();
			if (vos == null || vos.length <= 0) {
				MessageBox.show(this, "请添加人员!");
				return;
			}
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < vos.length; i++) {
				sb.append(vos[i].getStringValue("userid") + ";");
			}
			if (this.groupvo == null) {
				String id = UIUtil.getSequenceNextValByDS(null, "S_PUB_WF_USERGROUP");
				String seq = UIUtil.getStringValueByDS(null, "select max(seq) from PUB_WF_USERGROUP where owner='" + ClientEnvironment.getCurrLoginUserVO().getId() + "' ");
				if (seq == null || "".equals(seq)) {
					seq = "0";
				} else {
					try {
						seq = Integer.parseInt(seq) + 1 + "";
					} catch (Exception e) {
						e.printStackTrace();
						seq = "0";
					}
				}
				UIUtil.executeBatchByDS(null, new String[] { "insert into PUB_WF_USERGROUP(id,name,owner,userids,seq) values " + "('" + id + "','" + jt_name.getText() + "','" + ClientEnvironment.getCurrLoginUserVO().getId() + "','" + sb.toString() + "','" + seq + "'" + ")" });
			} else {
				UIUtil.executeBatchByDS(null, new String[] { "update PUB_WF_USERGROUP set name='" + jt_name.getText() + "',userids='" + sb.toString() + "' where id='" + groupvo.getPkValue() + "'" });
			}
			closeType = 1;
			this.dispose();
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	public void onCancel() {
		closeType = 2;
		this.dispose();
	}

	public int getCloseType() {
		return closeType;
	}

	/**
	 * 机构数的选择事件
	 */
	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		if (billList_user.getRowCount() > 0) {
			billList_user.clearTable();
		}
		BillVO billVO = _event.getCurrSelectedVO();
		if (billVO != null) {
			String str_corpId = billVO.getStringValue("id");
			HashVO[] hvs = queryUserByCorpId(str_corpId);
			putUserDataByHashVO(billList_user, hvs);
		}
	}

	/**
	 * 新增群组选人的时候按部门查人的时候个人感觉不需要加上授权的人员
	 * @param _corpId
	 * @return
	 */
	private HashVO[] queryUserByCorpId(String _corpId) {
		try {
			HashMap parMap = new HashMap();
			parMap.put("corpid", _corpId);
			parMap.put("isaddaccr", false);
			HashMap returnMap = UIUtil.commMethod("cn.com.infostrategy.bs.workflow.WorkFlowBSUtil", "getUserCorpAndUsersBycorpId", parMap);
			HashVO[] hvs = (HashVO[]) returnMap.get("sameCorpUsers");
			return hvs; //
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * 将hashvo数组赋值给列表
	 * @param bl
	 * @param _hvs
	 */
	private void putUserDataByHashVO(BillListPanel bl, HashVO[] _hvs) {
		bl.clearTable();
		if (_hvs != null && _hvs.length > 0) {
			for (int i = 0; i < _hvs.length; i++) {
				int _newRow = bl.newRow(false);
				bl.setValueAt(new StringItemVO(_hvs[i].getStringValue("userid")), _newRow, "userid");
				bl.setValueAt(new StringItemVO(_hvs[i].getStringValue("usercode")), _newRow, "usercode");
				bl.setValueAt(new StringItemVO(_hvs[i].getStringValue("username")), _newRow, "username");
				bl.setValueAt(new StringItemVO(_hvs[i].getStringValue("userroleid")), _newRow, "userroleid");
				bl.setValueAt(new StringItemVO(_hvs[i].getStringValue("userrolename")), _newRow, "userrolename");
				bl.setValueAt(new StringItemVO(_hvs[i].getStringValue("userdept")), _newRow, "userdept");
				bl.setValueAt(new StringItemVO(_hvs[i].getStringValue("userdeptname")), _newRow, "userdeptname");
				bl.setValueAt(new StringItemVO(_hvs[i].getStringValue("accruserid")), _newRow, "accruserid");
				bl.setValueAt(new StringItemVO(_hvs[i].getStringValue("accrusercode")), _newRow, "accrusercode");
				bl.setValueAt(new StringItemVO(_hvs[i].getStringValue("accrusername")), _newRow, "accrusername");
			}
			bl.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT);
		}
	}

	public JTextField getJt_name() {
		return jt_name;
	}

	public void setJt_name(JTextField jt_name) {
		this.jt_name = jt_name;
	}
}
