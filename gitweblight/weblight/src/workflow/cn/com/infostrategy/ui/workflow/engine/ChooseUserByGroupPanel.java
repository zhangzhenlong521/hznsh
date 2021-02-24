package cn.com.infostrategy.ui.workflow.engine;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.mdata.treecomp.BillTreeCheckCellRender;

/**
 * ��Ⱥ��ѡ�˵����
 */
public class ChooseUserByGroupPanel extends JPanel implements BillTreeSelectListener, ActionListener {

	private String owner = null;
	private boolean ishaveuserlist, isgwc = false;
	private BillListPanel billList_user = null;
	private BillListPanel billList_user_shopcar = null;
	private BillTreePanel bt = null;
	private WLTButton btn_addShopCarUser, btn_addAllShopCarUser, btn_delShopCarUser, btn_delAllShopCarUser;
	private WLTButton addGroup, delGroup, editGroup, lookGroup;
	private String sqinf = "";
	private BillVO[] rtnvos = null;
	private BillVO billvo = null;

	public ChooseUserByGroupPanel(boolean _ishaveuserlist, boolean _isgwc, String _owner, BillVO _billvo) {
		super();
		this.ishaveuserlist = _ishaveuserlist;
		this.isgwc = _isgwc;
		this.owner = _owner;
		this.billvo = _billvo;
		init();
	}

	public void init() {
		this.setLayout(new BorderLayout());
		getMyGroupPanel(owner);
		billList_user = new BillListPanel(new ServerTMODefine("/cn/com/infostrategy/bs/sysapp/install/templetdata/pub_user_1.xml"));
		bt.addBillTreeSelectListener(this);
		if (ishaveuserlist) {//������û��б�
			if (isgwc) {//����ǹ��ﳵ
				billList_user_shopcar = new BillListPanel(billList_user.getTempletVO().deepClone());
				billList_user_shopcar.setTitleLabelText("��ѡ�����Ա");
				btn_addShopCarUser = new WLTButton("���", "office_059.gif");
				btn_delShopCarUser = new WLTButton("ɾ��", "office_081.gif");
				btn_addAllShopCarUser = new WLTButton("ȫ�����", "office_160.gif");
				btn_delAllShopCarUser = new WLTButton("ȫ��ɾ��", "office_125.gif");
				btn_addShopCarUser.setToolTipText("���Ϸ�ѡ�е���Ա��ӽ���");
				btn_delShopCarUser.setToolTipText("���·���ѡ�����Աɾ����");
				btn_addShopCarUser.addActionListener(this);
				btn_delShopCarUser.addActionListener(this);
				btn_addAllShopCarUser.addActionListener(this);
				btn_delAllShopCarUser.addActionListener(this);
				billList_user_shopcar.addBatchBillListButton(new WLTButton[] { btn_addShopCarUser, btn_delShopCarUser, btn_addAllShopCarUser, btn_delAllShopCarUser });
				billList_user_shopcar.repaintBillListButton();
				WLTSplitPane splitPanel_2 = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, billList_user, billList_user_shopcar);
				splitPanel_2.setDividerLocation(365);
				WLTSplitPane splitPanel = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, bt, splitPanel_2);
				splitPanel.setDividerLocation(250);
				this.add(splitPanel, BorderLayout.CENTER);
			} else {
				WLTSplitPane splitPanel = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, bt, billList_user);
				splitPanel.setDividerLocation(250);
				this.add(splitPanel, BorderLayout.CENTER);
			}
		} else {
			this.add(bt, BorderLayout.CENTER);
		}
	}

	public BillVO[] getRtnVOS() {
		return rtnvos;
	}

	public BillTreePanel getMyGroupPanel(String owner) {
		if (bt == null) {
			bt = new BillTreePanel(new ServerTMODefine("/cn/com/infostrategy/bs/sysapp/install/templetdata/PUB_WF_USERGROUP_CODE1.xml"));
			addGroup = new WLTButton(UIUtil.getImage("zt_045.gif"));
			addGroup.setToolTipText("����Ⱥ��");
			addGroup.setPreferredSize(new Dimension(20, 20));
			addGroup.addActionListener(this);
			delGroup = new WLTButton(UIUtil.getImage("zt_031.gif"));
			delGroup.setToolTipText("ɾ��Ⱥ��");
			delGroup.setPreferredSize(new Dimension(20, 20));
			delGroup.addActionListener(this);
			editGroup = new WLTButton(UIUtil.getImage("office_198.gif"));
			editGroup.setToolTipText("ά��Ⱥ��");
			editGroup.setPreferredSize(new Dimension(20, 20));
			editGroup.addActionListener(this);
			lookGroup = new WLTButton(UIUtil.getImage("zoom.gif"));
			lookGroup.setToolTipText("�鿴Ⱥ��");
			lookGroup.setPreferredSize(new Dimension(20, 20));
			lookGroup.addActionListener(this);
			WLTButton upbtn = null;
			WLTButton downbtn = null;
			Component[] all = bt.getToolKitBtnPanel().getComponents();
			if (all != null) {
				for (int i = 0; i < all.length; i++) {
					if (all[i] instanceof WLTButton) {
						if ("����˳��".equals(((WLTButton) all[i]).getToolTipText())) {
							upbtn = (WLTButton) all[i];
						} else if ("����˳��".equals(((WLTButton) all[i]).getToolTipText())) {
							downbtn = (WLTButton) all[i];
						}
					}
				}
			}
			bt.getToolKitBtnPanel().removeAll();
			if (upbtn != null) {
				bt.getToolKitBtnPanel().add(upbtn, FlowLayout.LEFT);
			}
			if (downbtn != null) {
				bt.getToolKitBtnPanel().add(downbtn, FlowLayout.LEFT);
			}
			//bt.getToolKitBtnPanel().add(lookGroup, FlowLayout.LEFT); �鿴��ť����Ҫ��/sunfujun/20121115_��
			bt.getToolKitBtnPanel().add(editGroup, FlowLayout.LEFT);
			bt.getToolKitBtnPanel().add(delGroup, FlowLayout.LEFT);
			bt.getToolKitBtnPanel().add(addGroup, FlowLayout.LEFT);
			JTree tree = bt.getJTree();
			tree.setCellRenderer(new BillTreeCheckCellRender("office_028.gif", "user.gif"));
			bt.queryDataByCondition(" owner = '" + owner + "' ");
		}
		return bt;
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		if (billList_user.getRowCount() > 0) {
			billList_user.clearTable();
		}
		BillVO billVO = bt.getSelectedVO();
		if (billVO != null) {
			HashVO[] hvs = queryUserByGroup(billVO);
			putUserDataByHashVO(hvs);
		}
	}

	private HashVO[] queryUserByGroup(BillVO groupvo) {
		try {
			HashMap parMap = new HashMap();
			BillVO billVO = bt.getSelectedVO();
			String str_billType = billvo.getStringValue("BILLTYPE");
			String str_busiType = billvo.getStringValue("BUSITYPE");
			parMap.put("billtype", str_billType);
			parMap.put("busitype", str_busiType);
			parMap.put("groupVO", billVO);
			HashMap returnMap = UIUtil.commMethod("cn.com.infostrategy.bs.workflow.WorkFlowBSUtil", "getUserCorpAndUsersByGroup", parMap);
			HashVO[] hvs = (HashVO[]) returnMap.get("users");
			if (!ishaveuserlist) {
				sqinf = (String) returnMap.get("sqinf");
			}
			return hvs;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public boolean check(Container parentComponent) {
		if (ishaveuserlist) {
			if (isgwc) {
				BillVO[] vos = billList_user_shopcar.getAllBillVOs();
				if (vos == null || vos.length <= 0) {
					MessageBox.show(parentComponent, "��ѡ��һ����Ա!");
					return false;
				} else {
					rtnvos = vos;
					return true;
				}
			} else {
				BillVO[] vos = billList_user.getSelectedBillVOs();
				if (vos == null || vos.length <= 0) {
					MessageBox.show(parentComponent, "��ѡ��һ����Ա!");
					return false;
				} else {
					rtnvos = vos;
					return true;
				}
			}
		} else {
			if (bt.getSelectedVO() == null) {
				MessageBox.show(parentComponent, "��ѡ��һ��Ⱥ��!");
				return false;
			}
			BillVO[] vos = billList_user.getAllBillVOs();
			if (vos == null || vos.length <= 0) {
				MessageBox.show(parentComponent, "ѡ���Ⱥ��û�������Ա��ȷ��!");
				return false;
			}
			if (sqinf != null && !"".equals(sqinf) && !"null".equals(sqinf)) {
				int a = MessageBox.showConfirmDialog(parentComponent, "Ⱥ������Ա������Ȩ���:\r\n" + sqinf.toString() + "\r\n�Ƿ�ѡ����Ȩ��?", "��Ȩ����", JOptionPane.YES_NO_CANCEL_OPTION);
				if (a == JOptionPane.YES_OPTION) {
					rtnvos = vos;
					return true;
				} else if (a == JOptionPane.NO_OPTION) {
					List temp = new ArrayList();
					for (int i = 0; i < vos.length; i++) {
						if (vos[i].getStringValue("accruserid") == null || "".equals(vos[i].getStringValue("accruserid"))) {
							temp.add(vos[i]);
						}
					}
					if (temp == null || temp.size() <= 0) {
						MessageBox.show(parentComponent, "û��ѡ����Ա��ȷ��!");
						return false;
					} else {
						rtnvos = (BillVO[]) temp.toArray(new BillVO[] {});
						return true;
					}

				} else {
					return false;
				}
			} else {
				rtnvos = vos;
				return true;
			}
		}
	}

	private void putUserDataByHashVO(HashVO[] _hvs) {
		billList_user.clearTable();
		if (_hvs != null && _hvs.length > 0) {
			for (int i = 0; i < _hvs.length; i++) {
				int _newRow = billList_user.newRow(false); //
				billList_user.setValueAt(new StringItemVO(_hvs[i].getStringValue("userid")), _newRow, "userid");
				billList_user.setValueAt(new StringItemVO(_hvs[i].getStringValue("usercode")), _newRow, "usercode");
				billList_user.setValueAt(new StringItemVO(_hvs[i].getStringValue("username")), _newRow, "username");
				billList_user.setValueAt(new StringItemVO(_hvs[i].getStringValue("userroleid")), _newRow, "userroleid");
				billList_user.setValueAt(new StringItemVO(_hvs[i].getStringValue("userrolename")), _newRow, "userrolename");
				billList_user.setValueAt(new StringItemVO(_hvs[i].getStringValue("userdept")), _newRow, "userdept");
				billList_user.setValueAt(new StringItemVO(_hvs[i].getStringValue("userdeptname")), _newRow, "userdeptname");
				billList_user.setValueAt(new StringItemVO(_hvs[i].getStringValue("accruserid")), _newRow, "accruserid");
				billList_user.setValueAt(new StringItemVO(_hvs[i].getStringValue("accrusercode")), _newRow, "accrusercode");
				billList_user.setValueAt(new StringItemVO(_hvs[i].getStringValue("accrusername")), _newRow, "accrusername");
			}
			billList_user.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT);
		}
	}

	public void actionPerformed(ActionEvent _event) {
		if (_event.getSource() == btn_addShopCarUser) {
			onAddShopCarUser();
		} else if (_event.getSource() == btn_delShopCarUser) {
			onDelShopCarUser();
		} else if (_event.getSource() == btn_addAllShopCarUser) {
			onAddAllShopCarUser();
		} else if (_event.getSource() == btn_delAllShopCarUser) {
			onDelAllShopCarUser();
		} else if (_event.getSource() == addGroup) {
			onAddGroup();
		} else if (_event.getSource() == delGroup) {
			onDelGroup();
		} else if (_event.getSource() == editGroup) {
			onEditGroup();
		} else if (_event.getSource() == lookGroup) {
			onLookGroup();
		}
	}

	/**
	 * ����Ⱥ���߼�
	 */
	public void onAddGroup() {
		//���ܽ�������Ⱥ�� �ȵ���һ����Ⱥ�����ƵĿ�
		DefaultTMO tmo = new DefaultTMO("����Ⱥ��", new String[] { "Itemkey", "Itemname", "Cardwidth", "Cardisshowable", "Issave", "Ismustinput" }, new String[][] { { "groupname", "Ⱥ������", "250", "Y", "N", "Y" } });
		BillCardDialog bd = new BillCardDialog(this, "����Ⱥ��", 400, 120, tmo, null);//ֱ����billcardpanel�ܷ����
		bd.getBillcardPanel().setBorderTitle("");
		bd.getBtn_save().setVisible(false);
		bd.setVisible(true);
		if (bd.getCloseType() == 1) {
			AddNewGroupDialog an = new AddNewGroupDialog(this, null, null);
			an.getJt_name().setText(bd.getCardItemValue("groupname"));
			an.setVisible(true);
			if (an.getCloseType() == AddNewGroupDialog.CONFIRM) {
				if (billList_user.getRowCount() > 0) {
					billList_user.clearTable();
				}
				bt.refreshTree();
			}
		}
	}

	/**
	 * Ⱥ��ɾ���߼�
	 */
	public void onDelGroup() {
		BillVO groupvo = bt.getSelectedVO();
		if (groupvo == null) {
			MessageBox.show(this, "��ѡ��Ҫɾ����Ⱥ��!");
			return;
		}
		if (MessageBox.showConfirmDialog(this, "ȷ��Ҫɾ��ѡ��Ⱥ����?") != JOptionPane.YES_OPTION) {
			return;
		}
		try {
			UIUtil.executeBatchByDS(null, new String[] { "delete pub_wf_usergroup where id = '" + groupvo.getPkValue() + "'" });
			bt.refreshTree();
			if (billList_user.getRowCount() > 0) {
				billList_user.clearTable();
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showException(this, e);
		}
	}

	/**
	 * Ⱥ��༭�߼�
	 */
	public void onEditGroup() {
		BillVO groupvo = bt.getSelectedVO();
		DefaultMutableTreeNode node = bt.getSelectedNode();
		if (groupvo == null) {
			MessageBox.show(this, "��ѡ��Ҫά����Ⱥ��!");
			return;
		}
		AddNewGroupDialog an = new AddNewGroupDialog(this, groupvo, "edit");
		an.setVisible(true);
		if (an.getCloseType() == AddNewGroupDialog.CONFIRM) {
			bt.refreshTree();
			DefaultMutableTreeNode currUserNode = bt.findNodeByKey(groupvo.getPkValue());
			if (currUserNode != null) {
				bt.scrollToOneNode(currUserNode);
			}
		}
	}

	public void onLookGroup() {
		BillVO groupvo = bt.getSelectedVO();
		if (groupvo == null) {
			MessageBox.show(this, "��ѡ��Ҫ�鿴��Ⱥ��!");
			return;
		}
		AddNewGroupDialog an = new AddNewGroupDialog(this, groupvo, "init");
		an.setVisible(true);
	}

	private void onAddShopCarUser() {
		BillVO[] billVOs = billList_user.getSelectedBillVOs();
		if (billVOs == null || billVOs.length <= 0) {
			MessageBox.show(this, "����Ϸ���Ա�б���ѡ��һ��������Ա���д˲���!");
			return;
		}
		onAddShopCarUserByBillVOs(billVOs);
	}

	private void onAddAllShopCarUser() {
		BillVO[] billVOs = billList_user.getAllBillVOs();
		if (billVOs == null || billVOs.length <= 0) {
			MessageBox.show(this, "û�п�����ӵ���Ա,�޷����д˲���!");
			return;
		}
		onAddShopCarUserByBillVOs(billVOs);
	}

	private void onAddShopCarUserByBillVOs(BillVO[] _billVOs) {
		HashSet hst = new HashSet();
		BillVO[] billVOs_shopCardUser = billList_user_shopcar.getAllBillVOs();
		if (billVOs_shopCardUser != null && billVOs_shopCardUser.length > 0) {
			for (int i = 0; i < billVOs_shopCardUser.length; i++) {
				String str_userid = billVOs_shopCardUser[i].getStringValue("userid");
				if (str_userid != null) {
					hst.add(str_userid);
				}
			}
		}
		StringBuilder sb_reduplicate_user = new StringBuilder();
		for (int i = 0; i < _billVOs.length; i++) {
			String str_userid = _billVOs[i].getStringValue("userid");
			String str_usercode = _billVOs[i].getStringValue("usercode");
			String str_username = _billVOs[i].getStringValue("username");
			if (hst.contains(str_userid)) {
				sb_reduplicate_user.append("��" + str_usercode + "/" + str_username + "��");
			}
		}
		String str_reduplicate_user = sb_reduplicate_user.toString();
		if (!str_reduplicate_user.equals("")) {
			MessageBox.show(this, "�û�" + str_reduplicate_user + "�Ѿ���ӹ���,�����ظ����,������ѡ��!");
			return;
		}
		billList_user_shopcar.addBillVOs(_billVOs);
	}

	private void onDelShopCarUser() {
		BillVO[] billVOs = billList_user_shopcar.getSelectedBillVOs();
		if (billVOs == null || billVOs.length <= 0) {
			MessageBox.show(this, "����·���Ա�б���ѡ��һ��������Ա���д˲���!");
			return;
		}
		if (!MessageBox.confirm(this, "��ȷ��Ҫɾ��ѡ�е���Ա��?")) {
			return;
		}
		billList_user_shopcar.removeSelectedRows();
	}

	private void onDelAllShopCarUser() {
		int li_count = billList_user_shopcar.getRowCount();
		if (li_count <= 0) {
			MessageBox.show(this, "��ѡ�����ԱΪ��,�޷����д˲���!");
			return;
		}
		if (!MessageBox.confirm(this, "��ȷ��Ҫɾ��������ѡ�����Աô?")) {
			return;
		}
		billList_user_shopcar.clearTable();
	}

}
