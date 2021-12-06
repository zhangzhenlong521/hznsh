package com.pushworld.ipushgrc.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.tree.DefaultMutableTreeNode;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillTreeDefaultMutableTreeNode;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillCardEditEvent;
import cn.com.infostrategy.ui.mdata.BillCardEditListener;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillFormatPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.BillTreeCheckEditEvent;
import cn.com.infostrategy.ui.mdata.BillTreeCheckEditListener;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

/**
 * ���Ӳ��� ��߻��������ұ��������ѡ�л�������Ա ����ѡ���������Ա�����ҿ��Կ�������Ա������������
 * ���������˹��췽������������ѡ����Ա�Ƿ�ѡ��Ĭ�϶�ѡ�����/2012-07-16��
 * ������Ա��ȷ��λϵͳ���������á�zzl 2017-11-23��
 * 
 * @author hm
 */
public class CommSelectUserRefDialog extends AbstractRefDialog implements ActionListener, BillTreeCheckEditListener, BillCardEditListener, BillTreeSelectListener {
	private static final long serialVersionUID = 1L;
	private BillFormatPanel billFormatPanel = null; //
	private BillTreePanel billTreePanel = null; //
	private BillListPanel billListPanel = null; //
	private BillListPanel billListPanel_add = null; //
	private BillQueryPanel billQueryPanel = null;
	private BillCardPanel billCardPanel = null;
	private WLTButton btn_confirm, btn_cancel, btn_adduser, btn_deleteuser;
	private RefItemVO returnRefItemVO = null; //
	private HashMap haveSelected = new HashMap();
	private String depttempletCode = "PUB_CORP_DEPT_CODE1_IC";
	private StringBuilder roleids = null;//��¼���˵Ľ�ɫid
	private StringBuilder ids = null;//��¼ѡ��Ĳ���id
	private boolean isSingleSelect = false;//�Ƿ��ǵ�ѡ
	private boolean isrolelinked = false;//�Ƿ���н�ɫ����
	private int count;//���õ���Ա��ʾ����
	private DefaultMutableTreeNode[] billVO_oldtree;
	private String roles;
	private String roleid;
	private Boolean canSearch=TBUtil.getTBUtil().getSysOptionBooleanValue(
			"��Ա��ƽ�鿴�Ƿ���ʾ", true);
	private WLTTabbedPane tabbedPane = null; 
	private BillListPanel billListPanel_user_flat = null; // ��Ա��ƽ�鿴
	private WLTButton btn_gotodept, btn_edituser, btn_lookdeptinfo2;// ��ƽ�鿴��ϵ�а�ť
	private final static String MSG_NEED_USER = "��ѡ��һ����Ա!";
	public CommSelectUserRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
		this(_parent, _title, refItemVO, panel, null);
	}

	/*
	 * type�����������������ڵ���ʾ���Ĳ㡣
	 * type:��������Ĭ�ϡ�
	 */
	public CommSelectUserRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel, String _depttempletCode) {
		super(_parent, _title, refItemVO, panel);
		if (_depttempletCode != null) {
			depttempletCode = _depttempletCode; //���û���ģ�壬��������������Ȩ�޹��ˡ�
		}
		returnRefItemVO = refItemVO;
	}

	/*
	 * type�����������������ڵ���ʾ���Ĳ㡣
	 * type:��������Ĭ�ϡ�
	 */
	public CommSelectUserRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel, String _depttempletCode, String _singleSelect) {
		super(_parent, _title, refItemVO, panel);
		if (_depttempletCode != null) {
			depttempletCode = _depttempletCode; //���û���ģ�壬��������������Ȩ�޹��ˡ�
		}
		returnRefItemVO = refItemVO;
		if ("Y".equalsIgnoreCase(_singleSelect)) {
			isSingleSelect = true;
		}
	}

	public CommSelectUserRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel, String _depttempletCode, String _singleSelect, String _isrolelinked) {
		super(_parent, _title, refItemVO, panel);
		if (_depttempletCode != null) {
			depttempletCode = _depttempletCode; //���û���ģ�壬��������������Ȩ�޹��ˡ�
		}
		returnRefItemVO = refItemVO;
		if ("Y".equalsIgnoreCase(_singleSelect)) {
			isSingleSelect = true;
		}
		if ("Y".equalsIgnoreCase(_isrolelinked)) {
			isrolelinked = true;
		}
	}

	public void initialize() {
		this.setLayout(new BorderLayout()); //
		count = new TBUtil().getSysOptionIntegerValue("������Ա������ʾ��������", 100);
		if (isSingleSelect) {
			billFormatPanel = new BillFormatPanel("getSplit(getTree(\"" + depttempletCode + "\"),getList(\"PUB_USER_POST_DEFAULT\"),\"����\",225)"); //
			billTreePanel = billFormatPanel.getBillTreePanel(); // ������
			billTreePanel.reSetTreeChecked(false);
			billListPanel = billFormatPanel.getBillListPanelByTempletCode("PUB_USER_POST_DEFAULT", 1); // ��Ա
			billListPanel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			billTreePanel.addBillTreeSelectListener(this);
		} else {
			if (isrolelinked) {
				billFormatPanel = new BillFormatPanel("getSplit(getTree(\"" + depttempletCode + "\"),getSplit(getList(\"PUB_USER_POST_DEFAULT_code1_IC\"),getList(\"PUB_USER_POST_DEFAULT\"),\"����\",250),\"����\",225)"); //
				billListPanel = billFormatPanel.getBillListPanelByTempletCode("PUB_USER_POST_DEFAULT_code1_IC", 1); // ��Ա
				billListPanel_add = billFormatPanel.getBillListPanelByTempletCode("PUB_USER_POST_DEFAULT", 1); //
				billQueryPanel = billListPanel.getQuickQueryPanel();
				billQueryPanel.removeAll();
				billQueryPanel.setLayout(new BorderLayout());
				billCardPanel = new BillCardPanel("PUB_ROLE_1_IC");
				billCardPanel.setVisible(true);
				billCardPanel.setEditable(true);
				billCardPanel.addBillCardEditListener(this);
				billQueryPanel.add(billCardPanel);
				billQueryPanel.updateUI();
				billListPanel.setQuickQueryPanelVisiable(true);
			} else {
				billFormatPanel = new BillFormatPanel("getSplit(getTree(\"" + depttempletCode + "\"),getSplit(getList(\"PUB_USER_POST_DEFAULT\"),getList(\"PUB_USER_POST_DEFAULT\"),\"����\",250),\"����\",225)"); //
				billListPanel = billFormatPanel.getBillListPanelByTempletCode("PUB_USER_POST_DEFAULT", 1); // ��Ա
				billListPanel_add = billFormatPanel.getBillListPanelByTempletCode("PUB_USER_POST_DEFAULT", 2); //
			}
			billTreePanel = billFormatPanel.getBillTreePanel(); // ������
			billTreePanel.reSetTreeChecked(true);
			billTreePanel.addBillTreeCheckEditListener(this); //
			btn_adduser = new WLTButton("����");
			btn_deleteuser = new WLTButton("ɾ��");
			btn_deleteuser.addActionListener(this);
			btn_adduser.addActionListener(this);
			billListPanel_add.addBatchBillListButton(new WLTButton[] { btn_adduser, btn_deleteuser }); //
			billListPanel_add.repaintBillListButton(); // �ػ水ť!
			if (billListPanel_add.getQuickQueryPanel() != null) {
				billListPanel_add.getQuickQueryPanel().setVisible(false); //
			}
			if (returnRefItemVO != null) {
				String id = returnRefItemVO.getId();
				if (id != null && !id.equals("")) {
					String sql = billListPanel_add.getSQL("userid in (" + new TBUtil().getInCondition(id) + ")");
					BillVO billVOs[] = null;
					try {
						billVOs = UIUtil.getBillVOsByDS(null, sql, billListPanel_add.templetVO);
					} catch (WLTRemoteException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
					for (int i = 0; i < billVOs.length; i++) {
						if (haveSelected.containsKey(billVOs[i].getStringValue("userid"))) {

						} else {
							billListPanel_add.addRow(billVOs[i]);
							haveSelected.put(billVOs[i].getStringValue("userid"), null);
						}
					}
				}
			}
		}
		billTreePanel.getBtnPanel().setVisible(false);
		billTreePanel.queryDataByCondition(null);
		if(canSearch){
			tabbedPane = new WLTTabbedPane(); //
			tabbedPane.addTab("��Ϣά��", UIUtil.getImage("office_134.gif"), billFormatPanel); //
			tabbedPane.addTab("��Ա����", UIUtil.getImage("office_194.gif"), this.getUserFlatPanel()); //
		}else{
			this.add(billFormatPanel, BorderLayout.CENTER); //
		}
		this.add(tabbedPane);
		this.add(getSouthPanel(), BorderLayout.SOUTH); //
		this.setSize(700, 600);
	}
	/**
	 * zzl ����Ա��λ������
	 * @return
	 */
	private BillListPanel getUserFlatPanel() {
		if (this.billListPanel_user_flat == null) {
			billListPanel_user_flat = new BillListPanel("PUB_USER_ICN"); // ��ƽ�鿴!
			btn_gotodept = new WLTButton("��ȷ��λ"); //
			btn_lookdeptinfo2 = new WLTButton("��ְ���"); //
			btn_gotodept.addActionListener(this); //
			btn_lookdeptinfo2.addActionListener(this); //
			boolean showEditUser = TBUtil.getTBUtil().getSysOptionBooleanValue(
					"��Ա��ƽ�鿴�Ƿ�ɱ༭", true);// �ڽ������з��֣���֧��ϵͳ����Ա��Ȼ���ɿ������л��������ڱ�ƽ�鿴���ܲ�ѯ��������Ա�������޸����룬�����Ӳ������á����/2016-06-28��
			if (showEditUser) {
				btn_edituser = new WLTButton("�༭"); //
				btn_edituser.addActionListener(this); //
				billListPanel_user_flat.addBatchBillListButton(new WLTButton[] {
						btn_gotodept}); // //
			} else {
				billListPanel_user_flat.addBatchBillListButton(new WLTButton[] {
						btn_gotodept}); // //
			}
			billListPanel_user_flat.repaintBillListButton(); // ���°�ť����!!!
			billListPanel_user_flat.getQuickQueryPanel().setVisible(true); //
		}
		return billListPanel_user_flat;
	}
	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		btn_confirm = new WLTButton("ȷ��");
		btn_cancel = new WLTButton("ȡ��");
		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel;
	}

	public RefItemVO getReturnRefItemVO() {
		return returnRefItemVO;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_adduser) {
			onAddUser();
		} else if (e.getSource() == btn_deleteuser) {
			onDeleteUser();
		} else if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		}else if(e.getSource() == btn_gotodept){
			try {
				onGoToDept();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	/**
	 * ��ƽ�鿴ҳǩ����һ����Ա��λ�������
	 * 
	 * @throws Exception
	 */
	private void onGoToDept() throws Exception {
		BillVO billVO = billListPanel_user_flat.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.show(this, MSG_NEED_USER); //
			return;
		}

		String str_id = billVO.getStringValue("id"); //
		String str_name = billVO.getStringValue("name"); //

		HashVO[] hvo = UIUtil.getHashVoArrayByDS(null,
				"select userid,username,deptid,deptname from v_pub_user_post_1 where userid='"
						+ str_id + "'"); //
		if (hvo.length == 0) {
			MessageBox.show(this, "�Ҳ�����" + str_name + "�������Ļ���!"); //
			return;
		}

		String str_deptid = null;
		String str_deptname = null;
		if (hvo.length == 1) {
			str_deptid = hvo[0].getStringValueForDay("deptid"); //
			str_deptname = hvo[0].getStringValueForDay("deptname"); //

		} else {
			ComBoxItemVO[] itemVO = new ComBoxItemVO[hvo.length]; // //
			for (int i = 0; i < hvo.length; i++) {
				itemVO[i] = new ComBoxItemVO(hvo[i].getStringValue("deptid"),
						null, "" + hvo[i].getStringValue("deptname")); //
			}

			JList list = new JList(itemVO); //
			list.setBackground(Color.WHITE); //
			JScrollPane scrollPane = new JScrollPane(list); //
			scrollPane.setPreferredSize(new Dimension(255, 150)); //
			if (JOptionPane.showConfirmDialog(this, scrollPane, "��" + str_name
					+ "����ְ��[" + hvo.length + "]������,���붨λ����һ��?",
					JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return; //
			}

			ComBoxItemVO selectedItemVO = (ComBoxItemVO) list
					.getSelectedValue(); //
			str_deptid = selectedItemVO.getId(); //
			str_deptname = selectedItemVO.getName(); //
		}

		if (str_deptid == null || str_deptid.trim().equals("")
				|| str_deptid.trim().equalsIgnoreCase("NULL")) {
			MessageBox.show(this, "��" + str_name + "�������Ļ���IDΪ��!"); //
			return;
		}

		DefaultMutableTreeNode node = billTreePanel
				.findNodeByKey(str_deptid); //
		if (node == null) {
			MessageBox.show(this, "��" + str_name + "�����ڻ�����" + str_deptname
					+ "��,��û��Ȩ�޲���!"); //
			return; //
		}

		billTreePanel.expandOneNode(node); //
		if (billListPanel != null) {
			int li_row = billListPanel.findRow("userid", str_id); //
			if (li_row >= 0) {
				billListPanel.setSelectedRow(li_row); //
			}
		}
		tabbedPane.setSelectedIndex(0);
	}

	private void onAddUser() {
		BillVO[] billVO_list = billListPanel.getSelectedBillVOs(); //
		if (billVO_list.length == 0) {
			MessageBox.show(this, "������ѡ��һ����Ա!"); //
			return;
		}
		// ��ѡ��ȡ��
		JTable table = billListPanel_add.getTable();
		if (table.getRowCount() > 0) {
			table.removeRowSelectionInterval(0, table.getRowCount() - 1);
		}
		for (int i = 0; i < billVO_list.length; i++) {
			String userid = billVO_list[i].getStringValue("userid");
			if (!haveSelected.containsKey(userid)) {
				billListPanel_add.addRow(billVO_list[i]);
				billListPanel_add.addSelectedRow(billListPanel_add.getRowCount() - 1);
				haveSelected.put(userid, null);
			} else {
				int rowNum = billListPanel_add.findRow("userid", userid);
				if (rowNum >= 0) {
					billListPanel_add.addSelectedRow(rowNum);
				}
			}
		}
	}

	private void onDeleteUser() {
		BillVO[] billVO_addlist = billListPanel_add.getSelectedBillVOs(); //
		if (billVO_addlist == null) {
			MessageBox.show(this, "��ѡ��һ����Ա!"); //
			return;
		}
		for (int i = 0; i < billVO_addlist.length; i++) {
			haveSelected.remove(billVO_addlist[i].getStringValue("userid"));
		}
		billListPanel_add.removeSelectedRows();
	}

	private void onConfirm() {
		if (isSingleSelect) {
			BillVO billvo = billListPanel.getSelectedBillVO();
			if (billvo == null) {
				MessageBox.show(this, "��ѡ��һ����Ա!"); //
				return;
			}
			returnRefItemVO = new RefItemVO(billvo.getStringValue("userid"), "", billvo.getStringValue("username")); //
		} else {
			BillVO[] billVO_list = billListPanel_add.getAllBillVOs(); //
			if (billVO_list.length == 0) {
				MessageBox.show(this, "�����ټ���һ����Ա!"); //
				return;
			}
			StringBuilder userids = new StringBuilder(";");
			StringBuilder useridnames = new StringBuilder(";");
			for (int i = 0; i < billVO_list.length; i++) {
				userids.append(billVO_list[i].getStringValue("userid") + ";");
				useridnames.append(billVO_list[i].getStringValue("username") + ";");
			}
			returnRefItemVO = new RefItemVO(userids.toString(), "", useridnames.toString()); //
		}
		this.setCloseType(BillDialog.CONFIRM); //
		this.dispose(); //
	}

	private void onCancel() {
		this.setCloseType(BillDialog.CANCEL); //
		this.dispose(); //
	}

	public void onBillTreeCheckEditChanged(BillTreeCheckEditEvent _event) {
		BillVO billVO_tree[] = billTreePanel.getCheckedVOs();
		DefaultMutableTreeNode[] billVO_trees = billTreePanel.getCheckedNodes();
		ids = new StringBuilder();
		if (billVO_tree != null) {
			for (int i = 0; i < billVO_tree.length; i++) {
				if (i == billVO_tree.length - 1) {
					ids.append(billVO_tree[i].getStringValue("id"));
				} else {
					ids.append(billVO_tree[i].getStringValue("id"));
					ids.append(",");
				}
			}
			if (ids.length() > 0) {
				String sql = null;
				if (isSingleSelect) {//�ж��ǵ�ѡ�Ļ��Ƕ�ѡ��
					sql = " deptid in( " + ids + ")";
				} else {
					String role = null;
					if (isrolelinked) {
						role = billCardPanel.getBillVO().getStringValue("rolechoose");
					}
					if (role != null && !role.equals("")) {//�ж��Ƿ�ѡ���ɫ����
						roleids = new StringBuilder(new TBUtil().getInCondition(role));
					} else {
						roleids = null;
					}
					if (roleids == null || roleids.equals("")) {
						sql = " deptid in( " + ids + ")";
					} else {
						sql = "deptid in( " + ids + ") and userid in( select userid from pub_user_role where roleid in  (" + roleids.toString() + "))";
					}
				}

				BillVO billVOs[];
				try {
					billVOs = billListPanel.queryBillVOsByCondition(sql);
					if (billVOs.length < count) {//�ж��Ƿ񳬹���Ա��ʾ����
						billListPanel.QueryDataByCondition(sql);
						billVO_oldtree = billTreePanel.getCheckedNodes();
					} else {
						billTreePanel.clearSelection();
						MessageBox.show(this, "��ѡ��������ѳ���" + count + "�ˣ�������ѡ����");
						ids = new StringBuilder();
						if (billVO_oldtree != null && !billVO_oldtree.equals("")) {
							for (int k = 0; k < billVO_trees.length; k++) {
								boolean ishave = true;
								for (int j = 0; j < billVO_oldtree.length; j++) {
									if (billVO_trees[k].equals(billVO_oldtree[j])) {
										((BillTreeDefaultMutableTreeNode) billVO_trees[k]).setChecked(true);
										ishave = false;
										ids.append(billVO_tree[k].getStringValue("id") + ",");
										break;
									}
								}
								if (ishave) {
									((BillTreeDefaultMutableTreeNode) billVO_trees[k]).setChecked(false);
								}
							}
						} else {
							for (int k = 0; k < billVO_tree.length; k++) {
								((BillTreeDefaultMutableTreeNode) billVO_trees[k]).setChecked(false);
							}
						}
						if (ids.length() > 0) {
							ids.append("-99999");
						}
						billTreePanel.repaint();
					}
				} catch (Exception e) {
					MessageBox.showException(this, e);
					return;
				}
			} else {
				billListPanel.removeAllRows();//�����Ա�б�����
				billVO_oldtree = billTreePanel.getCheckedNodes();
			}
		}
	}

	public void onBillCardValueChanged(BillCardEditEvent _evt) {
		String role = null;
		if (isrolelinked) {
			role = billCardPanel.getBillVO().getStringValue("rolechoose");
		}

		String sql = null;
		if (role != null && !role.equals("")) {//�ж��Ƿ�ѡ���ɫ����
			if (ids != null && ids.length() > 0) {//�ж��Ƿ�ѡ����
				roleids = new StringBuilder(new TBUtil().getInCondition(role));
				sql = "deptid in( " + ids + ") and userid in( select userid from pub_user_role where roleid in (" + roleids.toString() + "))";
				try {
					BillVO billVO[] = billListPanel.queryBillVOsByCondition(sql);
					if (billVO.length < count) {
						billListPanel.QueryDataByCondition(sql); //
						roles = billCardPanel.getBillVO().getStringViewValue("rolechoose");
						roleid = role;
					} else {
						MessageBox.show(this, "��ѡ��������ѳ���" + count + "�ˣ�������ѡ����");
						RefItemVO itemvo = new RefItemVO(roleid, null, roles);
						billCardPanel.setValueAt("rolechoose", itemvo);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				roles = billCardPanel.getBillVO().getStringViewValue("rolechoose");
				roleid = role;
			}
		} else {
			sql = "deptid in( " + ids + ")";
			try {
				BillVO billVO[] = billListPanel.queryBillVOsByCondition(sql);
				if (billVO.length < count) {//��ս�ɫ����ʱ���ж���ѡ�����������Ա�Ƿ񳬹����õ���ʾ����
					billListPanel.QueryDataByCondition(sql);
				} else {
					MessageBox.show(this, "��ѡ��������ѳ���" + count + "�ˣ�������ѡ����");
					if (isrolelinked) {
						RefItemVO itemvo = new RefItemVO(roleid, null, roles);
						billCardPanel.setValueAt("rolechoose", itemvo);
					}

				}
			} catch (Exception e) {
				return;
			}
		}
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		if (billTreePanel.getSelectedNode().isRoot()) {
			return;
		}
		BillVO billvo = billTreePanel.getSelectedVO();
		if (billvo == null) {
			return;
		}
		billListPanel.QueryDataByCondition("deptid=" + billvo.getStringValue("id"));
	}
}