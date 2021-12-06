package com.pushworld.ipushgrc.ui.icheck.ref;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillFormatPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_Ref;

/**
 * ��߻��������ұ���Ա�б�����ѡ�����/2016-09-09��
 * @author lcj
 *
 */
public class RefDialog_Corp_Users extends AbstractRefDialog implements ActionListener, BillTreeSelectListener {

	
	private BillFormatPanel billFormatPanel = null; //
	private BillTreePanel billTreePanel = null; //
	private BillListPanel billListPanel = null; //
	private BillListPanel billListPanel_add = null; //
	private BillCardPanel billCardPanel = null;
	private WLTButton btn_confirm, btn_cancel, btn_adduser, btn_deleteuser;
	private RefItemVO returnRefItemVO = null; //
	private Container parent;
	private String itemKey;
	private WLTTabbedPane tabbedPane = null; 
	private WLTSplitPane splitPanel_all = null;//[zzl 2017-11-23]�������������Ա��λ������ܹʼ���
	private Boolean canSearch=TBUtil.getTBUtil().getSysOptionBooleanValue(
			"��Ա��ƽ�鿴�Ƿ���ʾ", true);;
	private BillListPanel billListPanel_user_flat = null; // ��Ա��ƽ�鿴
	private WLTButton btn_gotodept, btn_edituser, btn_lookdeptinfo2;// ��ƽ�鿴��ϵ�а�ť
	private final static String MSG_NEED_USER = "��ѡ��һ����Ա!";
	

	public RefDialog_Corp_Users(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
		super(_parent, _title, refItemVO, panel);
		billCardPanel = (BillCardPanel) panel;
		parent = _parent;
		CardCPanel_Ref ref = (CardCPanel_Ref) parent;
		itemKey = ref.getItemKey();
		BillCardPanel cardPanel = (BillCardPanel) panel;
	}

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout()); //
		billFormatPanel = new BillFormatPanel("getSplit(getTree(\"PUB_CORP_DEPT_CODE1\"),getSplit(getList(\"PUB_USER_POST_DEFAULT\"),getList(\"PUB_USER_POST_DEFAULT_1\"),\"����\",200),\"����\",225)"); //
		billTreePanel = billFormatPanel.getBillTreePanel(); //
		billListPanel = billFormatPanel.getBillListPanelByTempletCode("PUB_USER_POST_DEFAULT"); //
		billListPanel_add = billFormatPanel.getBillListPanelByTempletCode("PUB_USER_POST_DEFAULT_1"); //
		RefItemVO refItemVO = (RefItemVO) billCardPanel.getValueAt(itemKey);
		if (refItemVO != null) {
			String teamuser = refItemVO.getId();
			if (teamuser != null && !"".equals(teamuser)) {
				String teamusers[] = teamuser.split(";");
				for (int i = 0; i < teamusers.length; i++) {
					try {
						if (teamusers[i] != null && !"".equals(teamusers[i])) {
							String user_id = teamusers[i];
							BillVO[] hashvo_user = UIUtil.getBillVOsByDS(null, "select * from v_pub_user_post_1 where userid=" + user_id + "", billListPanel_add.getTempletVO());
							billListPanel_add.addRow(hashvo_user[0]);
						}
					} catch (WLTRemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
		btn_adduser = new WLTButton("����");
		btn_deleteuser = new WLTButton("ɾ��");
		btn_deleteuser.addActionListener(this);
		btn_adduser.addActionListener(this);
		billListPanel_add.addBatchBillListButton(new WLTButton[] { btn_adduser, btn_deleteuser }); //
		billListPanel_add.repaintBillListButton(); // �ػ水ť!
		billListPanel.getQuickQueryPanel().setVisible(false); //
		billListPanel.setAllBillListBtnVisiable(false); //

		billTreePanel.queryDataByCondition("1=1");
		billTreePanel.addBillTreeSelectListener(this); //
		splitPanel_all = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT,
				 billListPanel,billListPanel_add); //
		splitPanel_all.setDividerLocation(175); //
		WLTSplitPane splitPanel_all2 = new WLTSplitPane(
				WLTSplitPane.HORIZONTAL_SPLIT,billTreePanel,
				splitPanel_all); //
		splitPanel_all2.setDividerLocation(175); //
		if (canSearch) {//�������Ϊ���Ա�ƽ�鿴����������
			tabbedPane = new WLTTabbedPane(); //
			tabbedPane.addTab("��Ϣά��", UIUtil.getImage("office_134.gif"), splitPanel_all2); //
			tabbedPane.addTab("��Ա����", UIUtil.getImage("office_194.gif"), this.getUserFlatPanel()); //
			this.add(tabbedPane); //
		} else {//û�б�ƽ�鿴��ֱ�Ӽ���ά������
			this.add(splitPanel_all2); //

		}
		this.add(getSouthPanel(), BorderLayout.SOUTH); //
		
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

	@Override
	public RefItemVO getReturnRefItemVO() {
		return returnRefItemVO;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			BillVO[] billVO_list = billListPanel_add.getAllBillVOs();
			if (billVO_list == null || billVO_list.length < 1) {
				MessageBox.show(this, "�������Ա!"); //
				return;
			}
			StringBuilder userids = new StringBuilder(";");
			StringBuilder useridnames = new StringBuilder(";");
			for (int i = 0; i < billVO_list.length; i++) {
				HashVO hvo = billVO_list[i].convertToHashVO(); //
				userids.append(hvo.getStringValue("userid") + ";");
				useridnames.append(hvo.getStringValue("username") + ";");
			}
			returnRefItemVO = new RefItemVO(); //
			returnRefItemVO.setId(userids.toString()); //
			returnRefItemVO.setCode(null);
			returnRefItemVO.setName(useridnames.toString()); //
			this.setCloseType(BillDialog.CONFIRM); //
			this.dispose(); //
		} else if (e.getSource() == btn_cancel) {
			returnRefItemVO = null; //
			this.setCloseType(BillDialog.CANCEL); //
			this.dispose(); //
		} else if (e.getSource() == btn_adduser) {
			BillVO[] billVO_list = billListPanel.getSelectedBillVOs();
			if (billVO_list == null || billVO_list.length < 1) {
				MessageBox.show(this, "��ѡ����Ա!"); //
				return;
			}
			BillVO[] billVO_addlist = billListPanel_add.getAllBillVOs();
			if (billVO_addlist == null || billVO_addlist.length < 1) {
				for (int i = 0; i < billVO_list.length; i++) {
					billListPanel_add.addRow(billVO_list[i]);
				}
			} else {
				for (int i = 0; i < billVO_list.length; i++) {
					String userid = billVO_list[i].getStringValue("userid");
					Boolean add = true;
					for (int j = 0; j < billVO_addlist.length; j++) {
						if (userid.equals(billVO_addlist[j].getStringValue("userid"))) {
							add = false;
							break;
						}
					}
					if (add) {
						billListPanel_add.addRow(billVO_list[i]);
					}
				}
			}
		} else if (e.getSource() == btn_deleteuser) {
			BillVO[] billVO_addlist = billListPanel_add.getSelectedBillVOs(); //
			if (billVO_addlist == null) {
				MessageBox.show(this, "��ѡ����Ա!"); //
				return;
			}
			billListPanel_add.removeSelectedRows();
		}else if(e.getSource() == btn_gotodept){
			try {
				onGoToDept();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		BillVO billVO_tree = billTreePanel.getSelectedVO(); //
		billListPanel.QueryDataByCondition("deptid='" + billVO_tree.getStringValue("id") + "'"); //
	}
}
