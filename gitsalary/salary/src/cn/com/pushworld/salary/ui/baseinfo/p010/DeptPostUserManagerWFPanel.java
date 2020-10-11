package cn.com.pushworld.salary.ui.baseinfo.p010;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;

import cn.com.infostrategy.to.common.DESKeyTool;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.to.mdata.templetvo.BillTreeNodeVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.FrameWorkCommServiceIfc;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedEvent;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.sysapp.corpdept.CorpDeptBillTreePanel;
import cn.com.infostrategy.ui.sysapp.corpdept.SeqListDialog;
import cn.com.infostrategy.ui.sysapp.corpdept.TransUserDialog;
import cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc;

/**
 *Ϊ�˲���ƽ̨���࣬ ������һ�ݳ�����Ŀ��ʹ�ã���ͬ���У� 1. ��չ����Ա��Ϣ 2. ������ ��λ�� ��Ա ���ڴ˹�����ά���� ��Ҫ�������� 3.
 * �����ˣ�����Ϊ����λ������ 4. �����˸�λ�����ļ�¼��zzl 2017-11-6��
 */
public class DeptPostUserManagerWFPanel extends AbstractWorkPanel implements
		BillTreeSelectListener, BillListSelectListener, ActionListener,
		BillListMouseDoubleClickedListener {

	private BillTreePanel billTreePanel_Dept = null; // ������
	private BillListPanel billListPanel_Post = null; // ��λ��
	private BillListPanel billListPanel_User_Post = null; // ��Ա��λ��.
	private BillListPanel billListPanel_User_Role = null; // ��Ա��ɫ��ϵ��.
	private BillListPanel billListPanel_user_flat = null; // ��Ա��ƽ�鿴

	private WLTTabbedPane tabbedPane = null; // ҳǩ������˵����������ÿ��Ա�ƽ�鿴������ά����������Ա�б�����Ҫ��ҳǩ��ʽ��ʾ

	private WLTButton btn_post_insert, btn_post_update, btn_post_delete,
			btn_post_seq; // ��λ�б��ϵ�ϵ�а�ť
	private WLTButton btn_user_import, btn_user_add, btn_user_update,
			btn_user_trans, btn_user_del, btn_user_seq, btn_lookdeptinfo1; // ��Ա�б��ϵ�ϵ�а�ť
	private WLTButton btn_role_add, btn_role_del; // ��ɫ�б��ϵ�ϵ�а�ť

	private WLTButton btn_gotodept, btn_edituser, btn_lookdeptinfo2;// ��ƽ�鿴��ϵ�а�ť

	private String str_currdeptid, str_currdeptname = null; // ��ǰ����ID����������

	private int li_granuType = 3; // ��������,1-��ֵ�,2-��õ�,3-��ϸ��,�����е�
	private boolean isFilter = false; // �Ƿ�Ȩ�޹���,���Ƿ���ݵ�¼��Ա����Ȩ�޹���!!!
	private String panelStyle = "5";// ҳ�沼�ַ�񣬼��ݾ�ϵͳ��Ĭ���ǻ�������λ����Ա����ɫ ����
	private boolean canSearch = true;// �Ƿ��б�ƽ�鿴ҳǩ

	private WLTButton btn_setdefault;

	private WLTButton btn_tree_insert, btn_tree_update, btn_tree_delete;

	public DeptPostUserManagerWFPanel() {
	}

	private final static String MSG_NEED_DEPT = "��ѡ��һ������!";
	private final static String MSG_NEED_USER = "��ѡ��һ����Ա!";

	/**
	 * �����������Ĺ��췽��!!!
	 * 
	 * @param _granuType
	 * @param _isfilter
	 */
	public DeptPostUserManagerWFPanel(String _granuType, String _isfilter) {
		li_granuType = Integer.parseInt(_granuType); //
		isFilter = Boolean.parseBoolean(_isfilter); //
	}

	@Override
	public void initialize() {
		HashMap confmap = this.getMenuConfMap();// �˵�����
		if (confmap.get("ҳ�沼��") != null) {// ҳ�沼�֣������֣�1-��������λ;2-��������λ����Ա;3-��������Ա;4-��������Ա����ɫ;5-��������λ����Ա����ɫ
			panelStyle = (String) confmap.get("ҳ�沼��");
		}
		if (confmap.get("�Ƿ���Ա�ƽ�鿴") != null) {
			String tmp_str = (String) confmap.get("�Ƿ���Ա�ƽ�鿴");
			if (tmp_str.equalsIgnoreCase("N")
					|| tmp_str.equalsIgnoreCase("false")
					|| tmp_str.equalsIgnoreCase("��")) {
				canSearch = false;
			}
		}

		this.setLayout(new BorderLayout()); //
		billTreePanel_Dept = new CorpDeptBillTreePanel(getCorpTempletCode(),
				getGranuType(), isFilter()); // ������,�����Ƿ���Ҫ����Ȩ�޹���!!

		btn_tree_insert = WLTButton.createButtonByType(WLTButton.TREE_INSERT);
		btn_tree_insert.addActionListener(this);
		btn_tree_update = WLTButton.createButtonByType(WLTButton.TREE_EDIT);
		btn_tree_update.addActionListener(this);
		btn_tree_delete = new WLTButton("ɾ��");
		btn_tree_delete.addActionListener(this);
		billTreePanel_Dept.addBatchBillTreeButton(new WLTButton[] {
				btn_tree_insert, btn_tree_update, btn_tree_delete });
		billTreePanel_Dept.queryDataByCondition(null); // �鿴���л���
		billTreePanel_Dept.addBillTreeSelectListener(this); //
		// billTreePanel_Dept.getBtnPanel().setVisible(true);
		billTreePanel_Dept.getQuickLocatePanel().setVisible(true);
		billTreePanel_Dept.repaintBillTreeButton();
		WLTSplitPane splitPanel_all = null;

		if ("1".equals(panelStyle)) {// ά��ҳ���Ƿ��ǵ�һ�ַ��ֻ�� ��������λ
			splitPanel_all = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,
					billTreePanel_Dept, this.getPostPanel()); //
			splitPanel_all.setDividerLocation(300); //
		} else if ("2".equals(panelStyle)) {// �Ƿ��ǵڶ��ַ��ֻ�� ��������λ����Ա
			WLTSplitPane splitPanel_post_user = new WLTSplitPane(
					WLTSplitPane.VERTICAL_SPLIT, this.getPostPanel(), this
							.getUserPanel()); //
			splitPanel_post_user.setDividerLocation(175); //

			splitPanel_all = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,
					billTreePanel_Dept, splitPanel_post_user); //
			splitPanel_all.setDividerLocation(300); //
		} else if ("3".equals(panelStyle)) {// �Ƿ��ǵ����ַ��ֻ�� ��������Ա
			splitPanel_all = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,
					billTreePanel_Dept, this.getUserPanel()); //
			splitPanel_all.setDividerLocation(300); //
		} else if ("4".equals(panelStyle)) {// �Ƿ��ǵ����ַ��ֻ�� ��������Ա����ɫ
			WLTSplitPane splitPanel_user_role = new WLTSplitPane(
					WLTSplitPane.VERTICAL_SPLIT, this.getUserPanel(), this
							.getRolePanel()); //
			splitPanel_user_role.setDividerLocation(300); //

			splitPanel_all = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,
					billTreePanel_Dept, splitPanel_user_role); //
			splitPanel_all.setDividerLocation(280); //
		} else {// �����ַ��Ҳ���� ��������λ����Ա����ɫ ����
			WLTSplitPane splitPanel_user_role = new WLTSplitPane(
					WLTSplitPane.HORIZONTAL_SPLIT, this.getUserPanel(), this
							.getRolePanel()); //
			splitPanel_user_role.setDividerLocation(360); //

			WLTSplitPane splitPanel_post_user = new WLTSplitPane(
					WLTSplitPane.VERTICAL_SPLIT, this.getPostPanel(),
					splitPanel_user_role); //
			splitPanel_post_user.setDividerLocation(175); //

			splitPanel_all = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,
					billTreePanel_Dept, splitPanel_post_user); //
			splitPanel_all.setDividerLocation(175); //
		}

		if (canSearch) {// �������Ϊ���Ա�ƽ�鿴����������
			tabbedPane = new WLTTabbedPane(); //
			tabbedPane.addTab("��Ϣά��", UIUtil.getImage("office_134.gif"),
					splitPanel_all); //
			tabbedPane.addTab("��Ա����", UIUtil.getImage("office_194.gif"), this
					.getUserFlatPanel()); //
			this.add(tabbedPane); //
		} else {// û�б�ƽ�鿴��ֱ�Ӽ���ά������
			this.add(splitPanel_all); //
		}
	}

	/**
	 * ��û���ģ��
	 * 
	 * @return
	 */
	public String getCorpTempletCode() {
		return "PUB_CORP_DEPT_CODE1"; // ��򵥵�
	}

	/**
	 * ��������,1-��ֵ�,2-��õ�,3-��ϸ��,�����е�
	 * 
	 * @return
	 */
	public int getGranuType() {
		return li_granuType; //
	}

	/**
	 * �Ƿ�Ȩ�޹���,���Ƿ���ݵ�¼��Ա����Ȩ�޹���!!!
	 * 
	 * @return
	 */
	public boolean isFilter() {
		return isFilter;
	}

	/**
	 * ��ø�λ�б�
	 * 
	 * @return
	 */
	private BillListPanel getPostPanel() {
		if (this.billListPanel_Post == null) {
			billListPanel_Post = new BillListPanel("PUB_POST_CODE1_SALARY"); // ��λ
			// billListPanel_Post.setItemVisible("refpostid", true);//�ڴ���ʾ��λ����Ϣ
			btn_post_insert = new WLTButton("����"); // ������λ!!
			btn_post_update = WLTButton
					.createButtonByType(WLTButton.LIST_POPEDIT); // �޸ĸ�λ
			btn_post_delete = WLTButton
					.createButtonByType(WLTButton.LIST_DELETE); // ɾ����λ!!
			btn_post_seq = new WLTButton("����");// ��λ����
			btn_post_insert.addActionListener(this); //
			btn_post_seq.addActionListener(this);
			btn_post_delete.addActionListener(this);
			billListPanel_Post.addBatchBillListButton(new WLTButton[] {
					btn_post_insert, btn_post_update, btn_post_delete,
					btn_post_seq }); //
			billListPanel_Post.repaintBillListButton(); //
			// �������Ա�������Ӹ�λ��ѡ���¼�
			if ("2".equals(panelStyle) || "5".equals(panelStyle)) {//
				billListPanel_Post.addBillListSelectListener(this); // ��λѡ��仯ʱ.
			}
		}
		return billListPanel_Post;
	}

	/**
	 * �����Ա�б�
	 * 
	 * @return
	 */
	private BillListPanel getUserPanel() {
		if (this.billListPanel_User_Post == null) {
			billListPanel_User_Post = new BillListPanel("PUB_USER_POST_DEFAULT"); // ��Ա_��λ�Ĺ�����!PUB_USER_POST_DEFAULT
			btn_user_import = new WLTButton("����");
			btn_user_add = new WLTButton("����");
			btn_user_update = new WLTButton("�޸�");
			btn_user_trans = new WLTButton("Ǩ��");
			btn_user_del = new WLTButton("ɾ��");
			btn_user_seq = new WLTButton("����");// ��λ����
			btn_lookdeptinfo1 = new WLTButton("��ְ���"); //
			btn_setdefault = new WLTButton("��Ϊ����λ");
			btn_user_import.addActionListener(this);
			btn_user_add.addActionListener(this);
			btn_user_update.addActionListener(this);
			btn_user_trans.addActionListener(this);
			btn_user_del.addActionListener(this);
			btn_user_seq.addActionListener(this);
			btn_lookdeptinfo1.addActionListener(this); //
			btn_setdefault.addActionListener(this);
			billListPanel_User_Post.addBatchBillListButton(new WLTButton[] {
					btn_user_import, btn_user_add, btn_user_update,
					btn_user_trans, btn_user_del, btn_lookdeptinfo1,
					btn_setdefault }); // Ϊ����_��Ա_��λ��������а�ť!!
			billListPanel_User_Post.repaintBillListButton(); //
			// ����н�ɫ����������Ա��ѡ���¼�
			if ("4".equals(panelStyle) || "5".equals(panelStyle)) {//
				billListPanel_User_Post.addBillListSelectListener(this); // ��Աѡ��仯ʱ.
			}

			billListPanel_User_Post.addBillListMouseDoubleClickedListener(this);
		}
		return billListPanel_User_Post;
	}

	/**
	 * ��ý�ɫ�б�
	 * 
	 * @return
	 */
	private BillListPanel getRolePanel() {
		if (this.billListPanel_User_Role == null) {
			billListPanel_User_Role = new BillListPanel("PUB_USER_ROLE_CODE1"); // ��Ա_��ɫ�Ĺ�����!
			btn_role_add = new WLTButton("����");
			btn_role_del = new WLTButton("ɾ��");
			btn_role_add.addActionListener(this); // ������ɫ
			btn_role_del.addActionListener(this); // ɾ����ɫ!
			billListPanel_User_Role.addBatchBillListButton(new WLTButton[] {
					btn_role_add, btn_role_del }); //
			billListPanel_User_Role.repaintBillListButton(); //
		}
		return billListPanel_User_Role;
	}

	/**
	 * �����Ա��ƽ�鿴�б�
	 * 
	 * @return
	 */
	private BillListPanel getUserFlatPanel() {
		if (this.billListPanel_user_flat == null) {
			billListPanel_user_flat = new BillListPanel("PUB_USER_ICN"); // ��ƽ�鿴!
			btn_gotodept = new WLTButton("��ȷ��λ"); //
			btn_edituser = new WLTButton("�༭"); //
			btn_lookdeptinfo2 = new WLTButton("��ְ���"); //
			btn_gotodept.addActionListener(this); //
			btn_edituser.addActionListener(this); //
			btn_lookdeptinfo2.addActionListener(this); //
			billListPanel_user_flat.addBatchBillListButton(new WLTButton[] {
					btn_gotodept, btn_edituser, btn_lookdeptinfo2 }); // //
			billListPanel_user_flat.repaintBillListButton(); // ���°�ť����!!!
			billListPanel_user_flat.getQuickQueryPanel().setVisible(true); //
		}
		return billListPanel_user_flat;
	}

	/**
	 * ��������ѡ���¼�
	 */
	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		str_currdeptid = _event.getCurrSelectedVO().getStringValue("id"); //
		str_currdeptname = _event.getCurrSelectedVO().getStringValue("name"); //
		if (billListPanel_Post != null) {
			billListPanel_Post.queryDataByCondition("deptid='" + str_currdeptid
					+ "'", "seq,code"); //
		}
		if (billListPanel_User_Post != null) {
			billListPanel_User_Post.queryDataByCondition("deptid='"
					+ str_currdeptid + "'", "seq,userseq"); //
		}
		if (billListPanel_User_Role != null) {
			billListPanel_User_Role.clearTable(); //
		}
	}

	/**
	 * �б��ѡ���¼���ѡ���λˢ����Ա��ѡ����Աˢ�½�ɫ
	 */
	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		if (_event.getSource() == billListPanel_Post
				&& billListPanel_User_Post != null) {// ���������Ǹ�λ�б�
														// ��������Ա�б���ˢ����Ա�б�
			String str_postid = _event.getCurrSelectedVO().getStringValue("id"); //
			billListPanel_User_Post.queryDataByCondition("postid='"
					+ str_postid + "'", "seq"); //
			if (billListPanel_User_Role != null) {
				billListPanel_User_Role.clearTable(); //
			}
		} else if (_event.getSource() == billListPanel_User_Post
				&& billListPanel_User_Role != null) {// ������������Ա�б�
														// �����н�ɫ�б���ˢ�½�ɫ�б�
			String str_useridid = _event.getCurrSelectedVO().getStringValue(
					"userid"); //
			billListPanel_User_Role.QueryDataByCondition("userid='"
					+ str_useridid + "'"); //
		}

	}

	/**
	 * ���а�ť�ĵ���¼�
	 */
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == btn_post_insert) {
				onInsertPost(); // ������λ!!
			} else if (e.getSource() == btn_post_seq) {
				onSeqPost();// ��λ����
			} else if (e.getSource() == btn_user_import) {
				onImportUser(); // ������Ա
			} else if (e.getSource() == btn_user_add) {
				onAddUser(); // ������Ա
			} else if (e.getSource() == btn_user_update) {
				onUpdateUser(); // �༭��Ա
			} else if (e.getSource() == btn_user_trans) {
				onTransUser(); // Ǩ����Ա
			} else if (e.getSource() == btn_user_del) {
				onDelUser(); // ɾ����Ա
			} else if (e.getSource() == btn_user_seq) {
				onSeqUser();// ��Ա����
			} else if (e.getSource() == btn_lookdeptinfo1) {
				onLookUserDeptInfos(1); // ά��ҳǩ���鿴��Ա��ְ���
			} else if (e.getSource() == btn_role_add) {
				onAddRole(); // ������ɫ
			} else if (e.getSource() == btn_role_del) {
				onDelRole(); // ɾ����ɫ
			} else if (e.getSource() == btn_gotodept) {
				onGoToDept(); // ��ƽ�鿴ҳǩ����ȷ��λ
			} else if (e.getSource() == btn_edituser) {
				onEditUser(); // ��ƽ�鿴ҳǩ���༭��Ա
			} else if (e.getSource() == btn_lookdeptinfo2) {
				onLookUserDeptInfos(2); // ��ƽ�鿴ҳҳǩ���鿴��Ա��ְ���
			} else if (e.getSource() == btn_setdefault) {
				onSetDefaultPost();
			} else if (e.getSource() == btn_tree_delete) {
				onDeptDelete();
			} else if (e.getSource() == btn_post_delete) {
				onPostDelete();
			} else if (e.getSource() == btn_tree_insert) {
				onDeptAdd();
			} else if (e.getSource() == btn_tree_update) {
				onDeptUpdate();
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * ������
	 * 
	 * @throws Exception
	 */
	private void onDeptAdd() throws Exception {
		if (billTreePanel_Dept.getSelectedNode() == null) {
			MessageBox.show(this, "��ѡ��һ������������������!"); //
			return;
		}

		BillVO billVO = billTreePanel_Dept.getSelectedVO();
		BillCardPanel insertCardPanel = new BillCardPanel(billTreePanel_Dept
				.getTempletVO()); //
		if (billVO != null) { // ���ѡ�еĲ��Ǹ����
			insertCardPanel.insertRow(); // ����һ��
			insertCardPanel.setEditableByInsertInit(); //
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) billTreePanel_Dept
					.getSelectedNode();
			if (!parentNode.isRoot()) { //
				BillVO parentVO = billVO; //
				String parent_id = ((StringItemVO) parentVO
						.getObject(billTreePanel_Dept.getTempletVO()
								.getTreepk())).getStringValue(); //
				insertCardPanel.setCompentObjectValue(billTreePanel_Dept
						.getTempletVO().getTreeparentpk(), new StringItemVO(
						parent_id)); //
			}
		} else { // ���ѡ�е��Ǹ����
			insertCardPanel.insertRow(); //
			insertCardPanel.setEditableByInsertInit(); //
		}

		BillCardDialog dialog = new BillCardDialog(billTreePanel_Dept, "����",
				insertCardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); //
		dialog.setVisible(true); //
		if (dialog.getCloseType() == BillDialog.CONFIRM) {
			BillVO newVO = insertCardPanel.getBillVO(); //
			newVO.setToStringFieldName(billTreePanel_Dept.getTempletVO()
					.getTreeviewfield()); //
			billTreePanel_Dept.addNode(newVO); //
			reloaCorpCacheData(); // ˢ�� ����
		}
	}

	/**
	 * ���༭..
	 * 
	 * @throws Exception
	 */
	private void onDeptUpdate() throws Exception {
		if (billTreePanel_Dept.getSelectedNode() == null) {
			MessageBox.show(this, "��ѡ��һ�������б༭����!"); //
			return;
		}

		BillVO billVO = billTreePanel_Dept.getSelectedVO();
		BillCardPanel editCardPanel = new BillCardPanel(billTreePanel_Dept
				.getTempletVO()); //

		if (billVO != null) { // ���ѡ�еĲ��Ǹ����
			editCardPanel.queryDataByCondition("id='" + billVO.getPkValue()
					+ "'"); //
			editCardPanel.setEditableByEditInit(); //
		} else { // ���ѡ�е��Ǹ����
			MessageBox.show(this, "����㲻�ɱ༭!!"); //
			return; // ���û��ѡ��һ�������ֱ�ӷ���
		}

		BillCardDialog dialog = new BillCardDialog(billTreePanel_Dept, "�༭",
				editCardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE); //
		dialog.setVisible(true); //
		if (dialog.getCloseType() == BillDialog.CONFIRM) {
			BillVO newVO = editCardPanel.getBillVO(); //
			newVO.setToStringFieldName(billTreePanel_Dept.getTempletVO()
					.getTreeviewfield()); //
			billTreePanel_Dept.setBillVOForCurrSelNode(newVO); // �����л�д����
			billTreePanel_Dept.updateUI(); // ��ǰ��billTree.getJTree().updateUI();��ı�չ��������ͼ�꣬���2012-02-23�޸�
			reloaCorpCacheData();
		}
	}

	/*
	 * ��λɾ��������ɾ��ǰ�ж��Ƿ�����Ա�������
	 */
	private void onPostDelete() {
		if (billListPanel_User_Post.getAllBillVOs().length > 0) {
			MessageBox.show(this, "��������ԱǨ�ƻ�ɾ�����ܽ���ø�λ����Ա�Ĺ�ϵ");
			return;
		}
		billListPanel_Post.doDelete(false);
	}

	/**
	 * ����ѡ�и�λΪĬ�ϸ�λ Gwang 2013-07-30
	 */
	private void onSetDefaultPost() throws Exception {
		BillVO vo = billListPanel_User_Post.getSelectedBillVO();
		if (vo == null) {
			MessageBox.showSelectOne(billListPanel_User_Post);
			return;
		}
		String userid = vo.getStringValue("userid");
		String pk = vo.getStringValue("id");
		String sql_1 = "update pub_user_post set isdefault = null where userid = "
				+ userid;
		String sql_2 = "update pub_user_post set isdefault = 'Y' where id = "
				+ pk;
		String sql_3 = "update pub_user set pk_dept = " + str_currdeptid
				+ " where id = " + userid;
		UIUtil.executeBatchByDS(null, new String[] { sql_1, sql_2, sql_3 });

		// ˢ���б�����
		BillVO postVO = billListPanel_Post.getSelectedBillVO();
		if (postVO != null) {
			billListPanel_User_Post.refreshCurrSelectedRow();
		} else {
			billListPanel_User_Post.QueryDataByCondition("deptid = "
					+ str_currdeptid);
		}
		checkandcreateduties(userid);
	}

	/**
	 * ������λ
	 */
	private void onInsertPost() {
		if (str_currdeptid == null) {
			MessageBox.show(this, MSG_NEED_DEPT); //
			return;
		}
		HashMap initValueMap = new HashMap(); //
		initValueMap.put("deptid", new RefItemVO(str_currdeptid, "",
				str_currdeptname)); // �����ʼֵ!�����Ҫ������ظ�λ����Ҫ������������Ϊ�˲��գ�������Ҫ�޸����Ϊ����vo�����/2012-03-29��
		billListPanel_Post.doInsert(initValueMap); // ����������!!!
	}

	/**
	 * ��λ����
	 */
	private void onSeqPost() {
		// һ��ʼ�õ�billListPanel_Post.getBillVOs(),����ɾ���˵ļ�¼Ҳ������,ע��billListPanel_Post.getAllBillVOs()
		// �ǻ����ʾ��billvo��������ɾ����billvo��
		SeqListDialog dialog_post = new SeqListDialog(this, "��λ����",
				billListPanel_Post.getTempletVO(), billListPanel_Post
						.getAllBillVOs());
		dialog_post.setVisible(true);
		if (dialog_post.getCloseType() == 1) {// ������ȷ�����أ�����Ҫˢ��һ��ҳ��
			billListPanel_Post.queryDataByCondition("deptid='" + str_currdeptid
					+ "'", "seq"); //
		}
	}

	/**
	 * ������Ա
	 */
	private void onImportUser() throws Exception {
		if (str_currdeptid == null) {
			MessageBox.show(this, MSG_NEED_DEPT); //
			return;
		}

		String str_postid = null; //
		String str_postname = null; //

		if (billListPanel_Post != null) {// �Ƿ��и�λ�б�����в���ʾ
			BillVO billVO_Post = billListPanel_Post.getSelectedBillVO(); //
			if (billVO_Post == null) {
				MessageBox.show(this, "��ѡ��һ����λ");
				return; //
			} else {
				str_postid = billVO_Post.getStringValue("id"); // ��λID
				str_postname = billVO_Post.getStringValue("name"); // ��λID
			}
		}

		BillListDialog dialog = new BillListDialog(this, "ѡ����Ա",
				"PUB_USER_ICN", 700, 500); //
		dialog.getBilllistPanel().setAllBillListBtnVisiable(false);
		dialog.getBilllistPanel().setItemEditable(false); //
		dialog.setVisible(true); //
		if (dialog.getCloseType() != 1) {
			return;
		}

		BillVO[] billVOS = dialog.getReturnBillVOs(); //
		String str_userid = billVOS[0].getStringValue("id"); //
		String str_username = billVOS[0].getStringValue("name"); //

		boolean bo_checked = checkUserDeptPost(str_currdeptid,
				str_currdeptname, str_postid, str_postname, str_userid,
				str_username); // �жϸ���Ա�Ƿ��ڴ˻����͸�λ�£�����ڣ��Ͳ��õ�����
		if (!bo_checked) {
			return;
		}

		HashVO[] hvs = UIUtil.getHashVoArrayByDS(null,
				"select deptname,postname from v_pub_user_post_1 where userid='"
						+ str_userid + "'"); //
		if (hvs.length == 0) {
			if (JOptionPane.showConfirmDialog(this, "ȷ��Ҫ�ѡ�" + str_username
					+ "�����뵽��" + str_currdeptname + "��������?", "��ʾ",
					JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return; //
			}

			String str_newpk = UIUtil.getSequenceNextValByDS(null,
					"S_PUB_USER_POST"); //
			String str_sql = getSQL_User_Post(str_newpk, str_userid,
					str_currdeptid, str_postid);
			// ���Ӷ�pub_user pk_dept ��ͬ������ Gwang 2012-4-17
			String sql = "update pub_user set pk_dept = " + str_currdeptid
					+ " where id = " + str_userid;
			UIUtil.executeBatchByDS(null, new String[] { str_sql, sql });
			billListPanel_User_Post.addBillVOs(billListPanel_User_Post
					.queryBillVOsByCondition("id='" + str_newpk + " '")); //
			remove_user_link_account(str_userid);
			MessageBox.show(this, "����ɹ�!"); 
			//zzl ���ӵ�����¼
			HashVO [] vo=UIUtil.getHashVoArrayByDS(null, "select * from v_pub_user_post_1 where userid='"
						+ str_userid + "'");
			String []data={"type;����",
					"old_deptid;"+vo[0].getStringValue("deptid"),
					"old_deptname;"+vo[0].getStringValue("deptname"),
					"old_postid;"+vo[0].getStringValue("postid"),
					"old_postname;"+vo[0].getStringValue("postname"),
					"old_userid;"+vo[0].getStringValue("userid"),
					"old_username;"+vo[0].getStringValue("username"),
					"tzusercode;"+ClientEnvironment.getInstance().getLoginUserCode(),
					"tzdeptid;"+ClientEnvironment.getInstance().getLoginUserDeptId(),
					"datetime;"+UIUtil.getCurrTime()};
			UIUtil.executeUpdateByDS(null, getSQL_record_Post(data));
		} else {
			String str_alreadyDeptNames = "";
			for (int i = 0; i < hvs.length; i++) {
				str_alreadyDeptNames = str_alreadyDeptNames
						+ "������"
						+ hvs[i].getStringValue("deptname")
						+ "��,��λ��"
						+ (hvs[i].getStringValue("postname") == null ? ""
								: hvs[i].getStringValue("postname")) + "��\r\n";
			}

			int li_result = -1; //
			if (str_postid != null) {
				li_result = JOptionPane.showOptionDialog(this, "����Ա��ǰ����:\r\n"
						+ str_alreadyDeptNames + "��ϣ��������Ա��β���?", //
						"��ʾ", JOptionPane.DEFAULT_OPTION, //
						JOptionPane.QUESTION_MESSAGE, null, //
						new String[] { "Ǩ�Ƶ���������λ", "���Ƶ���������λ", "ȡ��" }, //
						"ȡ��"); //
			} else {
				li_result = JOptionPane.showOptionDialog(this, "����Ա��ǰ����:\r\n"
						+ str_alreadyDeptNames + "��ϣ��������Ա��β���?", //
						"��ʾ", JOptionPane.DEFAULT_OPTION, //
						JOptionPane.QUESTION_MESSAGE, null, //
						new String[] { "Ǩ�Ƶ�������", "���Ƶ�������", "ȡ��" }, //
						"ȡ��"); //
			}

			if (li_result == 0) { // Ǩ�Ƶ�������, (ɾ��������Ա�͸�λ��ϵ, ��������һ����Ա�͸�λ�Ķ�Ӧ��ϵ,
									// ������ΪĬ�ϸ�λ Gwang 2013-07-30)
				// zzl[2017-10-30]���Ӽ�¼���и�λ������¼
				HashVO [] vo=UIUtil.getHashVoArrayByDS(null, "select * from v_pub_user_post_1 where userid='"
						+ str_userid + "'");
				String str_newpk = UIUtil.getSequenceNextValByDS(null,
						"S_PUB_USER_POST"); //
				String str_sql = getSQL_User_Post(str_newpk, str_userid,
						str_currdeptid, str_postid);
				// ���Ӷ�pub_user pk_dept ��ͬ������ Gwang 2012-4-17
				String sql = "update pub_user set pk_dept = " + str_currdeptid
						+ " where id = " + str_userid;
				UIUtil.executeBatchByDS(null, new String[] {
						"delete from pub_user_post where userid=" + str_userid,
						str_sql, sql }); //
				billListPanel_User_Post.addBillVOs(billListPanel_User_Post
						.queryBillVOsByCondition("id='" + str_newpk + " '")); //
				HashVO [] voh=UIUtil.getHashVoArrayByDS(null, "select * from v_pub_user_post_1 where userid='"
						+ str_userid + "'");
			    String []data={"type;Ǩ��",
						"old_deptid;"+vo[0].getStringValue("deptid"),
						"old_deptname;"+vo[0].getStringValue("deptname"),
						"old_postid;"+vo[0].getStringValue("postid"),
						"old_postname;"+vo[0].getStringValue("postname"),
						"old_userid;"+vo[0].getStringValue("userid"),
						"old_username;"+vo[0].getStringValue("username"),
						"deptid;"+voh[0].getStringValue("deptid"),
						"deptname;"+voh[0].getStringValue("deptname"),
						"postid;"+voh[0].getStringValue("postid"),
						"postname;"+voh[0].getStringValue("postname"),
						"userid;"+voh[0].getStringValue("userid"),
						"username;"+voh[0].getStringValue("username"),
						"tzusercode;"+ClientEnvironment.getInstance().getLoginUserCode(),
						"tzdeptid;"+ClientEnvironment.getInstance().getLoginUserDeptId(),
						"datetime;"+UIUtil.getCurrTime()};
				UIUtil.executeUpdateByDS(null, getSQL_record_Post(data));
			} else if (li_result == 1) { // ���Ƶ�������(������Ա�͸�λ�Ķ�Ӧ��ϵ, ԭ����Ĭ�ϸ�λ���� Gwang
											// 2013-07-30)
				// zzl[2017-10-30]���Ӽ�¼���и�λ������¼
				HashVO [] vo=UIUtil.getHashVoArrayByDS(null, "select * from v_pub_user_post_1 where userid='"
						+ str_userid + "'");
				String str_newpk = UIUtil.getSequenceNextValByDS(null,
						"S_PUB_USER_POST"); //
				String str_sql = getSQL_User_Post(str_newpk, str_userid,
						str_currdeptid, str_postid);
				String sql_update = "update pub_user_post set isdefault = null where id="
						+ str_newpk;

				UIUtil.executeBatchByDS(null, new String[] { str_sql,
						sql_update }); //
				billListPanel_User_Post.addBillVOs(billListPanel_User_Post
						.queryBillVOsByCondition("id='" + str_newpk + " '")); //
				HashVO [] voh=UIUtil.getHashVoArrayByDS(null, "select * from v_pub_user_post_1 where deptid='"
						+ str_currdeptid + "'");
			    String []data={"type;��ְ",
						"old_deptid;"+vo[0].getStringValue("deptid"),
						"old_deptname;"+vo[0].getStringValue("deptname"),
						"old_postid;"+vo[0].getStringValue("postid"),
						"old_postname;"+vo[0].getStringValue("postname"),
						"old_userid;"+vo[0].getStringValue("userid"),
						"old_username;"+vo[0].getStringValue("username"),
						"deptid;"+voh[0].getStringValue("deptid"),
						"deptname;"+voh[0].getStringValue("deptname"),
						"postid;"+voh[0].getStringValue("postid"),
						"postname;"+voh[0].getStringValue("postname"),
						"userid;"+voh[0].getStringValue("userid"),
						"username;"+voh[0].getStringValue("username"),
						"tzusercode;"+ClientEnvironment.getInstance().getLoginUserCode(),
						"tzdeptid;"+ClientEnvironment.getInstance().getLoginUserDeptId(),
						"datetime;"+UIUtil.getCurrTime()};
				UIUtil.executeUpdateByDS(null, getSQL_record_Post(data));
			}
			checkandcreateduties(str_userid);
			remove_user_link_account(str_userid);
		}
	}

	/**
	 * ������Ա��sql
	 * 
	 * @param str_newpk
	 *            ��Ա��λ��ϵ��id
	 * @param str_userid
	 *            ��Աid
	 * @param str_currdeptid2
	 *            ��ǰ����id
	 * @param str_postid
	 *            ��λid
	 * @return
	 */
	private String getSQL_User_Post(String str_newpk, String str_userid,
			String str_currdeptid2, String str_postid) {
		InsertSQLBuilder isql = new InsertSQLBuilder("pub_user_post");
		isql.putFieldValue("id", str_newpk);
		isql.putFieldValue("userid", str_userid);
		isql.putFieldValue("postid", str_postid);
		isql.putFieldValue("userdept", str_currdeptid);
		isql.putFieldValue("isdefault", "Y");
		return isql.toString();
	}

	/**
	 * 
	 * @param str_newpk
	 * @param str_userid
	 * @param str_currdeptid2
	 * @param str_postid
	 * @return ��¼��λ������sql zzl��2017-10-30��
	 */
	private String getSQL_record_Post(String [] data) {
		InsertSQLBuilder isql = new InsertSQLBuilder("hr_recordpost");
		try {
			isql.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_HR_RECORDPOST"));
			for(int i=0;i<data.length;i++){
				String [] str=data[i].toString().split(";");
				isql.putFieldValue(str[0].toString(),str[1].toString());
			}
		} catch (WLTRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		isql.putFieldValue("id", str_newpk);
//		isql.putFieldValue("deptid", deptid);
//		isql.putFieldValue("deptname", deptname);
//		isql.putFieldValue("postid", postid);
//		isql.putFieldValue("postname", postname);
//		isql.putFieldValue("userid", userid);
//		isql.putFieldValue("usercode", usercode);
//		isql.putFieldValue("username", username);
//		isql.putFieldValue("datetime", date);
//		isql.putFieldValue("type", date);//��������
//		isql.putFieldValue("old_deptid", deptid);
//		isql.putFieldValue("old_deptname", deptname);
//		isql.putFieldValue("old_postid", postid);
//		isql.putFieldValue("old_postname", postname);
//		isql.putFieldValue("old_userid", userid);
//		isql.putFieldValue("old_usercode", usercode);
//		isql.putFieldValue("old_username", username);
//		isql.putFieldValue("tzusercode", username);
//		isql.putFieldValue("tzdeptid", username);
		return isql.toString();
	}

	/**
	 * ������Ա
	 */
	private void onAddUser() throws Exception {
		if (str_currdeptid == null) {
			MessageBox.show(this, MSG_NEED_DEPT); //
			return;
		}

		String str_postid = null; //
		if (billListPanel_Post != null) {// �Ƿ��и�λ�б�����в���ʾ
			BillVO billVO_Post = billListPanel_Post.getSelectedBillVO(); //
			if (billVO_Post == null) {
				MessageBox.show(this, "��ѡ��һ����λ");
				return;
			} else {
				str_postid = billVO_Post.getStringValue("id"); // ��λID
			}
		}

		AddOrEditUserDialog dialog = new AddOrEditUserDialog(this, "", null,
				null, WLTConstants.BILLDATAEDITSTATE_INSERT);
		dialog.addInit();
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {
			String str_newid = dialog.getReturnNewID(); //
			BillVO postvos[] = billListPanel_User_Post
					.queryBillVOsByCondition("id='" + str_newid + " '");
			billListPanel_User_Post.addBillVOs(postvos); //
			checkandcreateduties(dialog.getBillcardPanel().getValueAt("id")
					.toString());
			if (postvos != null && postvos.length > 0) {
				adduser_link_account(postvos[0].getStringValue("userid"));
			}
		    // zzl[2017-10-30]���Ӽ�¼���и�λ������¼
			HashVO [] vo=UIUtil.getHashVoArrayByDS(null, "select * from v_pub_user_post_1 where postid='"
					+ str_postid + "'");
		    String []data={"type;����",
				"old_deptid;"+vo[0].getStringValue("deptid"),
				"old_deptname;"+vo[0].getStringValue("deptname"),
				"old_postid;"+vo[0].getStringValue("postid"),
				"old_postname;"+vo[0].getStringValue("postname"),
				"old_userid;"+vo[0].getStringValue("userid"),
				"old_username;"+vo[0].getStringValue("username"),
				"tzusercode;"+ClientEnvironment.getInstance().getLoginUserCode(),
				"tzdeptid;"+ClientEnvironment.getInstance().getLoginUserDeptId(),
				"datetime;"+UIUtil.getCurrTime()};
		    UIUtil.executeUpdateByDS(null, getSQL_record_Post(data));
		}

	}

	/*
	 * �жϲ�ִ��������Ա���׹�ϵ
	 */
	private void adduser_link_account(String _userid) {
		try {
			String username = UIUtil.getStringValueByDS(null,
					"select name from pub_user where id = " + _userid);
			HashVO vos[] = UIUtil.getHashVoArrayByDS(null,
					"select * from sal_account_set where isdefault='Y'");
			if (vos.length == 0) {
				MessageBox.show(this,
						"ϵͳû�з���Ĭ��н�����ף����ڹ���н�궨�幦���趨Ĭ��ֵ,���ֹ��Ѵ���Ա��ӵ���Ա������.");
			} else {
				String name = vos[0].getStringValue("name");
				String accountid = vos[0].getStringValue("id");
				if (MessageBox.confirm(this, "�Ƿ񽫡�" + username + "���󶨵�н�����ס�"
						+ name + "��?")) {
					InsertSQLBuilder insert = new InsertSQLBuilder(
							"sal_account_personinfo");
					insert.putFieldValue("id", UIUtil.getSequenceNextValByDS(
							null, "S_SAL_ACCOUNT_PERSONINFO"));
					insert.putFieldValue("accountid", accountid);
					insert.putFieldValue("personinfoid", _userid);
					UIUtil.executeBatchByDS(null, new String[] { insert
							.getSQL() });
				}
			}
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * �жϲ�ִ���Ƴ���Ա���׹�ϵ
	 */
	private void remove_user_link_account(String str_userid) {
		TBUtil tbutil = new TBUtil();
		String value = tbutil.getSysOptionStringValue("���μ�н�����ľ����λ",
				"��ְ;����;��ְ");
		if (TBUtil.getTBUtil().isEmpty(value)) {
			return;
		}
		String[] dontJoinMoneyPosts = tbutil.split(value, ";"); // ���μӼ���н��ĸ�λ
		try {
			HashVO[] posts = UIUtil.getHashVoArrayByDS(null,
					"select username,postname from v_pub_user_post_1 where userid = "
							+ str_userid);
			boolean flag = false;
			String post = "";
			String name = "";
			for (int i = 0; i < posts.length; i++) {
				for (int j = 0; j < dontJoinMoneyPosts.length; j++) {
					if (dontJoinMoneyPosts[j] != null
							&& dontJoinMoneyPosts[j].trim().equals(
									posts[i].getStringValue("postname", "")
											.trim())) {
						flag = true;
						post = dontJoinMoneyPosts[j];
						name = posts[i].getStringValue("username", "");
						break;
					}
				}
			}
			if (flag) {
				if (MessageBox.confirm(this, "�Ƿ�����" + name + "����н��̨�ʵĹ�ϵ?")) {
					String delsql = "delete from sal_account_personinfo where personinfoid ="
							+ str_userid;
					UIUtil.executeBatchByDS(null, new String[] { delsql });
				}
			}
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ά��ҳǩ���޸���Ա��Ϣ
	 */
	private void onUpdateUser() throws Exception {

		BillVO billVO = billListPanel_User_Post.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.showSelectOne(billListPanel_User_Post);
			return; //
		}

		String str_id = billVO.getStringValue("id"); //  
		String str_userid = billVO.getStringValue("userid"); // 

		AddOrEditUserDialog editDialog = new AddOrEditUserDialog(this,
				str_userid, billVO.getStringValue("deptid"), billVO
						.getStringValue("postid"),
				WLTConstants.BILLDATAEDITSTATE_UPDATE);
		String oldposition = editDialog.getBillcardPanel().getBillVO()
				.getStringValue("position");
		editDialog.setVisible(true);
		if (editDialog.getCloseType() == 1) {
			billListPanel_User_Post.setBillVOAt(billListPanel_User_Post
					.getSelectedRow(), billListPanel_User_Post
					.queryBillVOsByCondition("id='" + str_id + "'")[0]);
			editDialog.getBillcardPanel().updateData();
			BillVO newuservo = editDialog.getBillcardPanel().getBillVO();
			// ���ְ�����仯 ,��ʾ�Ƿ����ְ
			if (!newuservo.getStringValue("position", "").equals(oldposition)) {
				if (MessageBox.confirm(this, newuservo.getStringValue("name")
						+ "��ְ���[" + oldposition + "]�޸�Ϊ["
						+ newuservo.getStringValue("position", "")
						+ "],�Ƿ��������ְ��Ϣ?")) {
					checkandcreateduties(newuservo.getStringValue("id"));
				}
			}

		}
	}

	/**
	 * Ǩ����Ա
	 */
	private void onTransUser() {
		if (str_currdeptid == null) {
			MessageBox.show(this, MSG_NEED_DEPT); //
			return;
		}

		BillVO billVO = billListPanel_User_Post.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, MSG_NEED_USER); //
			return; //
		}

		String str_id = billVO.getStringValue("id"); //
		String str_userid = billVO.getStringValue("userid"); //
		String str_username = billVO.getStringValue("username"); //
		try {
			HashVO [] vo=UIUtil.getHashVoArrayByDS(null, "select * from v_pub_user_post_1 where userid='"
						+ str_userid + "'");
			TransUserDialog dialog = new TransUserDialog(this, str_id,
					str_currdeptid, str_currdeptname, str_userid, str_username,
					getGranuType(), isFilter());
			dialog.setVisible(true); //
			
			if (dialog.getCloseType() == 1) {
				// ˢ��һ��
				if (billListPanel_User_Post != null) {
					billListPanel_User_Post.queryDataByCondition("deptid='"
							+ str_currdeptid + "'", "seq,userseq"); //
				}
	
				if (billListPanel_User_Role != null) {// ����н�ɫ�б������б�
					billListPanel_User_Role.clearTable();
				}
				checkandcreateduties(str_userid);
				remove_user_link_account(str_userid);
			}
			HashVO [] voh=UIUtil.getHashVoArrayByDS(null, "select * from v_pub_user_post_1 where userid='"
					+ str_userid + "'");
		    String []data={"type;Ǩ��",
					"old_deptid;"+vo[0].getStringValue("deptid"),
					"old_deptname;"+vo[0].getStringValue("deptname"),
					"old_postid;"+vo[0].getStringValue("postid"),
					"old_postname;"+vo[0].getStringValue("postname"),
					"old_userid;"+vo[0].getStringValue("userid"),
					"old_username;"+vo[0].getStringValue("username"),
					"deptid;"+voh[0].getStringValue("deptid"),
					"deptname;"+voh[0].getStringValue("deptname"),
					"postid;"+voh[0].getStringValue("postid"),
					"postname;"+voh[0].getStringValue("postname"),
					"userid;"+voh[0].getStringValue("userid"),
					"username;"+voh[0].getStringValue("username"),
					"tzusercode;"+ClientEnvironment.getInstance().getLoginUserCode(),
					"tzdeptid;"+ClientEnvironment.getInstance().getLoginUserDeptId(),
					"datetime;"+UIUtil.getCurrTime()};
			UIUtil.executeUpdateByDS(null, getSQL_record_Post(data));
		} catch (WLTRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * ������Ա�Ƿ���Ҫ������ְ���ݡ������Ҫ���ͽ���һ����
	 */
	private void checkandcreateduties(String userid) {
		try {
			HashVO[] hvo = UIUtil.getHashVoArrayByDS(null,
					"select *from v_sal_personinfo where id = " + userid);
			HashVO[] duties = UIUtil.getHashVoArrayByDS(null,
					"select * from sal_personduties_his where userid ='"
							+ userid + "' order by startdate desc");
			if (hvo.length == 0) {
				return;
			}
			HashVO personVO = hvo[0];
			String currDate = UIUtil.getServerCurrDate(); // �õ���ǰ����
			List sqlList = new ArrayList();
			if (duties.length == 0) { // ���û�и���Ա��,�жϸ���Ա�ǲ�����Ա��
				String joinselfbankdate = personVO
						.getStringValue("joinselfbankdate"); // ȡ�ø���Ա����ʱ��.
				int joindays = compareTwoDate(currDate, joinselfbankdate); // ����ʱ��
				InsertSQLBuilder insert = new InsertSQLBuilder(
						"SAL_PERSONDUTIES_HIS");
				insert.putFieldValue("id", UIUtil.getSequenceNextValByDS(null,
						"S_SAL_PERSONDUTIES_HIS"));
				insert.putFieldValue("username", personVO
						.getStringValue("name"));
				insert.putFieldValue("userid", userid);
				insert.putFieldValue("postid", personVO
						.getStringValue("mainstationid"));
				String postdescr = personVO.getStringValue("mainstation");
				if (!TBUtil.isEmpty(personVO.getStringValue("position"))) {
					postdescr = personVO.getStringValue("position", "");
				}
				insert.putFieldValue("postname", postdescr);
				insert.putFieldValue("deptid", personVO
						.getStringValue("deptid"));
				insert.putFieldValue("deptname", personVO
						.getStringValue("deptname"));
				insert.putFieldValue("createuser", ClientEnvironment
						.getCurrSessionVO().getLoginUserName());
				insert.putFieldValue("createdate", currDate);
				insert.putFieldValue("startdate",
						(joindays < 10 && joindays >= 0) ? joinselfbankdate
								: currDate);// ����ְ��ȡ����ʱ��.
				sqlList.add(insert);
				// }
			} else { // �������ʷ��¼
				String lastupdateDate = duties[0].getStringValue("createdate");
				if (currDate.equals(lastupdateDate)) {// ����ǽ���ĵġ�ֱ�Ӹ���ԭʼ��¼���ɡ�
					UpdateSQLBuilder update = new UpdateSQLBuilder(
							"SAL_PERSONDUTIES_HIS");
					update.setWhereCondition(" id = "
							+ duties[0].getStringValue("id"));
					update.putFieldValue("postid", personVO
							.getStringValue("mainstationid"));
					String postdescr = personVO.getStringValue("mainstation");
					if (!TBUtil.isEmpty(personVO.getStringValue("position"))) {
						postdescr = personVO.getStringValue("position", "");
					}
					update.putFieldValue("postname", postdescr);
					update.putFieldValue("deptid", personVO
							.getStringValue("deptid"));
					update.putFieldValue("deptname", personVO
							.getStringValue("deptname"));
					sqlList.add(update);
				} else if (!personVO.getStringValue("mainstationid").equals(
						duties[0].getStringValue("postid"))) { // ������ǽ���ĵġ�
																// �ж�ԭ�е����һ���������ڵ�ְλ�Ƿ�һ����
					// �������ְλ����ô����һ����¼�����ҰѾɵļ�¼enddate����
					InsertSQLBuilder insert = new InsertSQLBuilder(
							"SAL_PERSONDUTIES_HIS");
					insert.putFieldValue("id", UIUtil.getSequenceNextValByDS(
							null, "S_SAL_PERSONDUTIES_HIS"));
					insert.putFieldValue("username", personVO
							.getStringValue("name"));
					insert.putFieldValue("userid", userid);
					insert.putFieldValue("postid", personVO
							.getStringValue("mainstationid"));
					String postdescr = personVO.getStringValue("mainstation");
					if (!TBUtil.isEmpty(personVO.getStringValue("position"))) {
						postdescr = personVO.getStringValue("position", "");
					}
					insert.putFieldValue("postname", postdescr);
					insert.putFieldValue("deptid", personVO
							.getStringValue("deptid"));
					insert.putFieldValue("deptname", personVO
							.getStringValue("deptname"));
					insert.putFieldValue("createuser", ClientEnvironment
							.getCurrSessionVO().getLoginUserName());
					insert.putFieldValue("createdate", currDate);
					insert.putFieldValue("startdate", currDate);
					sqlList.add(insert);
					UpdateSQLBuilder update = new UpdateSQLBuilder(
							"SAL_PERSONDUTIES_HIS");
					update.setWhereCondition(" id = "
							+ duties[0].getStringValue("id"));
					update.putFieldValue("enddate", currDate);// ����ǰһ����¼��
					update.putFieldValue("createuser", ClientEnvironment
							.getCurrSessionVO().getLoginUserName());
					sqlList.add(update);
				}
			}
			if (sqlList.size() > 0) {
				UpdateSQLBuilder sql = new UpdateSQLBuilder("sal_personinfo");
				sql.putFieldValue("stationdate", currDate);
				sql.setWhereCondition("id = " + personVO.getStringValue("id"));
				sqlList.add(sql);
				UIUtil.executeBatchByDS(null, sqlList); // ִ������
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int compareTwoDate(String firstDate, String secondDate)
			throws Exception {
		if (TBUtil.isEmpty(secondDate) || TBUtil.isEmpty(firstDate)) {
			return -1;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = sdf.parse(firstDate);
		Date date2 = sdf.parse(secondDate);
		long mills = date1.getTime() - date2.getTime();
		return (int) (mills / (long) (1000 * 60 * 60 * 24));
	}

	/**
	 * ɾ����Ա
	 * 
	 * @throws Exception
	 */
	private void onDelUser() throws Exception {
		if (str_currdeptid == null) {
			MessageBox.show(this, MSG_NEED_DEPT); //
			return;
		}

		BillVO billVO = billListPanel_User_Post.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, MSG_NEED_USER); //
			return; //
		}

		String str_userid = billVO.getStringValue("userid"); //
		String str_usercode=billVO.getStringValue("usercode");
		

		int li_result = JOptionPane.showOptionDialog(this, "������β���?", // 
				"��ʾ", //
				JOptionPane.DEFAULT_OPTION, //
				JOptionPane.QUESTION_MESSAGE, null, new String[] {
						"�Ƴ���" + str_currdeptname + "��", "����ɾ����Ա", "ȡ��" }, "ȡ��");

		// �Ƴ���Ա, �Ƚϸ��ӣ� Ҫ�����Ƿ���Ĭ�ϸ�λ�� �Ƿ��ж����λ�� Gwang 2013-07-30
		if (li_result == 0) {
			String isDefault = billVO.getStringValue("isdefault");
			String sql[] = null;
			String sql_update = "";
			String sql_del = "delete from pub_user_post where id="
					+ billVO.getStringValue("id");
			if ("Y".equals(isDefault)) { // �Ƴ�������Ҫ��λ
				String otherDefaultDeptID = this.getOtherDefaultDeptID(
						str_userid, str_currdeptid);
				if (otherDefaultDeptID.equals("")) { // ����ֻ��һ����λ
					HashVO [] voh=UIUtil.getHashVoArrayByDS(null, "select * from v_pub_user_post_1 where usercode='"
							+ str_usercode + "'");
				    String []data={"type;�Ƴ�",
							"deptid;"+voh[0].getStringValue("deptid"),
							"deptname;"+voh[0].getStringValue("deptname"),
							"postid;"+voh[0].getStringValue("postid"),
							"postname;"+voh[0].getStringValue("postname"),
							"userid;"+voh[0].getStringValue("userid"),
							"username;"+voh[0].getStringValue("username"),
							"tzusercode;"+ClientEnvironment.getInstance().getLoginUserCode(),
							"tzdeptid;"+ClientEnvironment.getInstance().getLoginUserDeptId(),
							"datetime;"+UIUtil.getCurrTime()};
					UIUtil.executeUpdateByDS(null, getSQL_record_Post(data));
					sql_update = "update pub_user set pk_dept = null where id = "
							+ str_userid;
					sql = new String[] { sql_del, sql_update };
				} else { // ����ж����λ
					HashVO [] voh=UIUtil.getHashVoArrayByDS(null, "select * from v_pub_user_post_1 where deptid='"
							+ str_currdeptid + "'");
				    String []data={"type;�Ƴ�",
							"deptid;"+voh[0].getStringValue("deptid"),
							"deptname;"+voh[0].getStringValue("deptname"),
							"postid;"+voh[0].getStringValue("postid"),
							"postname;"+voh[0].getStringValue("postname"),
							"userid;"+voh[0].getStringValue("userid"),
							"username;"+voh[0].getStringValue("username"),
							"tzusercode;"+ClientEnvironment.getInstance().getLoginUserCode(),
							"tzdeptid;"+ClientEnvironment.getInstance().getLoginUserDeptId(),
							"datetime;"+UIUtil.getCurrTime()};
					UIUtil.executeUpdateByDS(null, getSQL_record_Post(data));
					String pk = "";
					if (otherDefaultDeptID.equals("many")) { // �ж���2����λ,
																// ���û�ѡ���µ���Ҫ��λ;
						BillListDialog dialog = new BillListDialog(this,
								"��ѡ���µ���Ҫ��λ", "PUB_USER_POST_DEFAULT", 700, 500); //
						dialog.getBilllistPanel().setAllBillListBtnVisiable(
								false);
						dialog.getBilllistPanel().setItemEditable(false);
						dialog.getBilllistPanel().QueryDataByCondition(
								"userid = " + str_userid + " and deptid <> "
										+ str_currdeptid);
						dialog.setVisible(true); //
						if (dialog.getCloseType() != 1) {
							return;
						}
						BillVO returnVO = dialog.getReturnBillVOs()[0];
						pk = returnVO.getStringValue("id");
						otherDefaultDeptID = returnVO.getStringValue("deptid");
					} else { // ������2����λ, �Զ�����һ����Ϊ��Ҫ��λ;
						pk = otherDefaultDeptID.split(",")[0];
						otherDefaultDeptID = otherDefaultDeptID.split(",")[1];
					}
					sql_update = "update pub_user set pk_dept = "
							+ otherDefaultDeptID + " where id = " + str_userid;
					String sql_update2 = "update pub_user_post set isdefault = 'Y' where id = "
							+ pk;
					sql = new String[] { sql_del, sql_update, sql_update2 };
				}

			} else {
				HashVO [] voh=UIUtil.getHashVoArrayByDS(null, "select * from v_pub_user_post_1 where usercode='"
						+ str_usercode + "'");
			    String []data={"type;�Ƴ�",
						"deptid;"+voh[0].getStringValue("deptid"),
						"deptname;"+voh[0].getStringValue("deptname"),
						"postid;"+voh[0].getStringValue("postid"),
						"postname;"+voh[0].getStringValue("postname"),
						"userid;"+voh[0].getStringValue("userid"),
						"username;"+voh[0].getStringValue("username"),
						"tzusercode;"+ClientEnvironment.getInstance().getLoginUserCode(),
						"tzdeptid;"+ClientEnvironment.getInstance().getLoginUserDeptId(),
						"datetime;"+UIUtil.getCurrTime()};
				UIUtil.executeUpdateByDS(null, getSQL_record_Post(data));
				sql = new String[] { sql_del };
			}
			// for (String s : sql) {
			// System.out.println(s);
			// }

			UIUtil.executeBatchByDS(null, sql);

			// //׷��Ĭ�ϲ��Ÿ��ġ����/2013-05-20��
			// String sql_update = "";
			// HashVO[] hvos_user_post = UIUtil.getHashVoArrayByDS(null,
			// "select * from pub_user_post where userid='" + str_userid +
			// "' and userdept<>'" + str_currdeptid + "' order by isdefault");
			// if(hvos_user_post.length>0){
			// if(!hvos_user_post[0].getStringValue("isdefault",
			// "").equals("Y")){
			// if(!hvos_user_post[0].getStringValue("id", "").equals("")){
			// sql_update =
			// "update pub_user_post set isdefault = 'Y' where id = " +
			// hvos_user_post[0].getStringValue("id", "");
			// }
			// }
			// }
			//			
			// String str_sql = "delete from pub_user_post where id='" +
			// billVO.getStringValue("id") + "'"; //
			// //���Ӷ�pub_user pk_dept ��ͬ������ Gwang 2012-4-17
			// String sql = "update pub_user set pk_dept = null where id = " +
			// str_userid + " and pk_dept = " + str_currdeptid;
			// if(sql_update.equals("")){
			// UIUtil.executeBatchByDS(null, new String[] { str_sql, sql });
			// }else{
			// UIUtil.executeBatchByDS(null, new String[] { str_sql, sql,
			// sql_update });
			// }
			billListPanel_User_Post.removeSelectedRow(); //
			if (billListPanel_User_Role != null) {// ����н�ɫ�б������б�
				billListPanel_User_Role.clearTable();
			}
		} else if (li_result == 1) { // ����ɾ����Ա
			ArrayList al_sqls = new ArrayList(); //
			HashVO [] voh=UIUtil.getHashVoArrayByDS(null, "select * from v_pub_user_post_1 where usercode='"
					+ str_usercode + "'");
		    String []data={"type;ɾ��",
					"deptid;"+voh[0].getStringValue("deptid"),
					"deptname;"+voh[0].getStringValue("deptname"),
					"postid;"+voh[0].getStringValue("postid"),
					"postname;"+voh[0].getStringValue("postname"),
					"userid;"+voh[0].getStringValue("userid"),
					"username;"+voh[0].getStringValue("username"),
					"tzusercode;"+ClientEnvironment.getInstance().getLoginUserCode(),
					"tzdeptid;"+ClientEnvironment.getInstance().getLoginUserDeptId(),
					"datetime;"+UIUtil.getCurrTime()};
			UIUtil.executeUpdateByDS(null, getSQL_record_Post(data));
			al_sqls.add("delete from pub_user_post where userid='" + str_userid
					+ "'"); //
			al_sqls.add("delete from pub_user_role where userid='" + str_userid
					+ "'"); //
			al_sqls.add("delete from pub_user_menu where userid='" + str_userid
					+ "'"); //
			al_sqls.add("delete from pub_user where id='" + str_userid
					+ "'"); //
			al_sqls.add("delete from sal_personinfo where id='"
					+ str_userid + "'"); // ����Ա��ϸ��ϢҲɾ������
			al_sqls
					.add("delete from sal_account_personinfo where personinfoid ='"
							+ str_userid + "'"); // ����Ա��ϸ��ϢҲɾ������
			UIUtil.executeBatchByDS(null, al_sqls); //
			billListPanel_User_Post.removeSelectedRow(); //
			if (billListPanel_User_Role != null) {// ����н�ɫ�б������б�
				billListPanel_User_Role.clearTable();
			}
		}
	}

	/**
	 * �õ���Ա������λ���
	 * 
	 * @param userID
	 * @param deptID
	 * @return
	 */
	private String getOtherDefaultDeptID(String userID, String deptID) {
		String ret = null;
		String sql = "select id, userdept from pub_user_post where userid = "
				+ userID + " and userdept <> " + deptID;
		try {
			String[][] vals = UIUtil.getStringArrayByDS(null, sql);
			if (vals.length == 0) {
				ret = "";
			} else if (vals.length == 1) {
				ret = vals[0][0] + "," + vals[0][1];
			} else {
				ret = "many";
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * ��Ա����
	 */
	private void onSeqUser() {
		SeqListDialog dialog_user = new SeqListDialog(this, "��Ա����",
				billListPanel_User_Post.getTempletVO(), billListPanel_User_Post
						.getAllBillVOs());
		dialog_user.setVisible(true);
		if (dialog_user.getCloseType() == 1) {// ������ȷ�����أ�����Ҫˢ��һ��ҳ��
			billListPanel_User_Post.queryDataByCondition("deptid='"
					+ str_currdeptid + "'", "seq"); //
		}
	}

	/**
	 * �鿴��Ա��ְ��Ϣ
	 */
	private void onLookUserDeptInfos(int _type) throws Exception {
		BillVO billVO = null;
		String userID = null; //
		String str_name = null; //
		if (_type == 1) {// ��Աά����Ĳ鿴
			billVO = billListPanel_User_Post.getSelectedBillVO();
			if (billVO == null) {
				MessageBox.show(this, MSG_NEED_USER); //
				return;
			}
			userID = billVO.getStringValue("userid"); //
			str_name = billVO.getStringValue("username"); //
		} else {// ��ƽ�鿴��Ĳ鿴
			billVO = billListPanel_user_flat.getSelectedBillVO();
			if (billVO == null) {
				MessageBox.show(this, MSG_NEED_USER); //
				return;
			}
			userID = billVO.getStringValue("id"); //
			str_name = billVO.getStringValue("name"); //
		}

		String str_sql_1 = "select deptcode,deptname,postcode,postname,isdefault from v_pub_user_post_1 where userid="
				+ userID + " order by isdefault desc";
		String str_sql_2 = "select rolecode,rolename from v_pub_user_role_1 where userid="
				+ userID; //
		Vector v_return = UIUtil.getHashVoArrayReturnVectorByMark(null,
				new String[] { str_sql_1, str_sql_2 }); //
		HashVO[] hsv_userdepts = (HashVO[]) v_return.get(0); //
		HashVO[] hsv_userroles = (HashVO[]) v_return.get(1); //

		StringBuilder sb_msg = new StringBuilder(); //
		sb_msg.append("��" + str_name + "�����и�λ��Ϣ:\r\n"); //

		for (int i = 0; i < hsv_userdepts.length; i++) {
			// sb_msg.append("" + (i + 1) + ")��������/����=[" +
			// (hsv_userdepts[i].getStringValue("deptcode") == null ? "" :
			// hsv_userdepts[i].getStringValue("deptcode")));
			// sb_msg.append("/" + (hsv_userdepts[i].getStringValue("deptname")
			// == null ? "" : hsv_userdepts[i].getStringValue("deptname")) +
			// "],");
			// sb_msg.append("��λ����/����=[" +
			// (hsv_userdepts[i].getStringValue("postcode") == null ? "" :
			// hsv_userdepts[i].getStringValue("postcode")));
			// sb_msg.append("/" + (hsv_userdepts[i].getStringValue("postname")
			// == null ? "" : hsv_userdepts[i].getStringValue("postname")) +
			// "]\r\n"); //
			sb_msg
					.append(""
							+ (i + 1)
							+ ")"
							+ (hsv_userdepts[i].getStringValue("isdefault") == null ? ""
									: "[��Ҫ��λ] "));
			sb_msg.append(""
					+ (hsv_userdepts[i].getStringValue("deptname") == null ? ""
							: hsv_userdepts[i].getStringValue("deptname"))
					+ "--");
			sb_msg.append(""
					+ (hsv_userdepts[i].getStringValue("postname") == null ? ""
							: hsv_userdepts[i].getStringValue("postname"))
					+ "\r\n"); //			
		}

		sb_msg.append("\r\n\r\n"); //

		sb_msg.append("��" + str_name + "�����н�ɫ��Ϣ:\r\n"); //
		for (int i = 0; i < hsv_userroles.length; i++) {
			// sb_msg.append("" + (i + 1) + ")��ɫ����/����=[" +
			// hsv_userroles[i].getStringValue("rolecode") + "/" +
			// hsv_userroles[i].getStringValue("rolename") + "]\r\n"); //
			sb_msg.append("" + (i + 1) + ")"
					+ hsv_userroles[i].getStringValue("rolename") + "\r\n");
		}
		MessageBox.showTextArea(this, "��" + str_name + "��", sb_msg.toString(),
				600, 300);
	}

	/**
	 * ���ӽ�ɫ
	 */
	private void onAddRole() throws Exception {
		if (str_currdeptid == null) {
			MessageBox.show(this, MSG_NEED_DEPT); //
			return;
		}

		BillVO billVO = billListPanel_User_Post.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, MSG_NEED_USER); //
			return; //
		}

		String str_userid = billVO.getStringValue("userid"); //

		BillListDialog dialog = new BillListDialog(this, "��ѡ��һ����ɫ",
				"PUB_ROLE_1", 700, 500); //
		dialog.getBilllistPanel().setItemEditable(false); //
		dialog.getBilllistPanel().QueryDataByCondition(null); //
		dialog.setVisible(true); //
		if (dialog.getCloseType() != 1) {
			return;
		}
		// Ԭ���� 20120917 ��� �����������ӵĹ��� begin
		BillVO[] billVOS = dialog.getReturnBillVOs(); //
		HashVO[] hvs = UIUtil.getHashVoArrayByDS(null,
				"select userid,roleid,userdept from pub_user_role where userid='"
						+ str_userid + "' and userdept='" + str_currdeptid
						+ "'"); // �Ȼ����userid��userdept��ϲ�ѯ������
		String str_roles = ";";// ���÷ֺ���Ҫ��Ϊ�˲��õ�һ����ӵĺͺ�����ظ����������ж�Indexof��ʱ����жϲ�����
		for (int j = 0; j < hvs.length; j++) { // �Ȳ�ѯ��֮ǰ�Ѿ����е�roleid������������������û��������roleid
			String old_roleid = hvs[j].getStringValue("roleid");
			str_roles += old_roleid + ";";
		}
		for (int i = 0; i < billVOS.length; i++) {
			String str_roleid = billVOS[i].getStringValue("id"); // ���id
			String str_rolename = billVOS[i].getStringValue("name"); // ���name
			if (str_roles.indexOf(str_roleid) > 0) {// ��ʾ����ӵĸ�roleid�Ѿ�������֮ǰ��ӵ�Ȩ��id��
				continue;
			} else {
				String str_newpk = UIUtil.getSequenceNextValByDS(null,
						"S_PUB_USER_ROLE"); //
				StringBuilder sb_sql = new StringBuilder(); //
				sb_sql.append("insert into pub_user_role");
				sb_sql.append("(");
				sb_sql.append("id,"); // id (id)
				sb_sql.append("userid,"); // userid (userid)
				sb_sql.append("roleid,"); // roleid (roleid)
				sb_sql.append("userdept"); // userdept (userdept)
				sb_sql.append(")");
				sb_sql.append(" values ");
				sb_sql.append("(");
				sb_sql.append(str_newpk + ","); // id (id)
				sb_sql.append("'" + str_userid + "',"); // userid (userid)
				sb_sql.append("'" + str_roleid + "',"); // roleid (roleid)
				sb_sql.append("'" + str_currdeptid + "'"); // userdept
															// (userdept)
				sb_sql.append(")");
				UIUtil.executeUpdateByDS(null, sb_sql.toString()); // //
			}
		}
		MessageBox.show(this, "��ɫ���ӳɹ�!"); //
		billListPanel_User_Role.QueryDataByCondition("userid='" + str_userid
				+ "'"); // ˢ���б�
		// Ԭ���� 20120917 ��� �����������ӵĹ��� end
	}

	/**
	 * ɾ����ɫ
	 * 
	 * @throws Exception
	 */
	private void onDelRole() throws Exception {
		BillVO billVO = billListPanel_User_Post.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, MSG_NEED_USER); //
			return; //
		}

		BillVO[] billVOS = billListPanel_User_Role.getSelectedBillVOs(); //
		if (billVOS == null || billVOS.length == 0) {
			MessageBox.show(this, "��ѡ��һ����ɫ!"); //
			return;
		}

		// String str_rolenames = "";
		// ArrayList al_sql = new ArrayList(); //
		// for (int i = 0; i < billVOS.length; i++) {
		// String str_id = billVOS[i].getStringValue("id"); //
		// String str_rolename = billVOS[i].getStringValue("rolename"); //
		// str_rolenames += str_rolename + ",";
		// al_sql.add("delete from pub_user_role where id='" + str_id + "'");
		// }
		// str_rolenames = str_rolenames.substring(0, str_rolenames.length()-1);

		// ��Ϊ������ɫɾ��(Ϊ�˺�������ťͳһ) Gwang 2012-4-17
		String rolename = billVOS[0].getStringValue("rolename");
		String roleid = billVOS[0].getStringValue("id");
		String sql = "delete from pub_user_role where id='" + roleid + "'";
		if (JOptionPane.showConfirmDialog(this, "ȷ��Ҫ�ѽ�ɫ��" + rolename + "��ɾ����?",
				"��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return;
		}
		UIUtil.executeUpdateByDS(null, sql);
		billListPanel_User_Role.removeSelectedRow();
		if (billListPanel_User_Role.getRowCount() > 1) {// ������Ҫ�ж�һ�£������ֻ��һ����ɫ����ɾ�����û�н�ɫ����ѡ���ˡ����/2012-05-24��
			billListPanel_User_Role.getTable().removeRowSelectionInterval(0,
					billListPanel_User_Role.getRowCount() - 1);
		}
		MessageBox.show(this, "��ɫɾ���ɹ�!"); //
	}

	/**
	 * �ж���Ա�Ƿ��ڴ˻����£�������ڲŵ���
	 * 
	 * @param _deptid
	 *            ����id
	 * @param _deptName
	 *            ��������
	 * @param _postid
	 *            ��λid
	 * @param _postName
	 *            ��λ����
	 * @param _userid
	 *            ��Աid
	 * @param _userName
	 *            ��Ա����
	 * @return
	 * @throws Exception
	 */
	private boolean checkUserDeptPost(String _deptid, String _deptName,
			String _postid, String _postName, String _userid, String _userName)
			throws Exception {
		if (_postid == null) {
			HashVO[] hvs = UIUtil.getHashVoArrayByDS(null,
					"select postname c1 from v_pub_user_post_1 where deptid='"
							+ _deptid + "' and userid='" + _userid + "'");
			if (hvs.length > 0) {
				MessageBox.show(this, "����Ա�Ѵ���,�����ظ�����!"); //
				return false;
			}
		} else {
			HashVO[] hvs = UIUtil.getHashVoArrayByDS(null,
					"select postname c1 from v_pub_user_post_1 where deptid='"
							+ _deptid + "' and postid='" + _postid
							+ "' and  userid='" + _userid + "'");
			if (hvs.length > 0) {
				MessageBox.show(this, "����Ա�Ѵ���,�����ظ�����!"); //
				return false;
			}
		}
		return true;
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

		DefaultMutableTreeNode node = billTreePanel_Dept
				.findNodeByKey(str_deptid); //
		if (node == null) {
			MessageBox.show(this, "��" + str_name + "�����ڻ�����" + str_deptname
					+ "��,��û��Ȩ�޲���!"); //
			return; //
		}

		billTreePanel_Dept.expandOneNode(node); //
		if (billListPanel_User_Post != null) {
			int li_row = billListPanel_User_Post.findRow("userid", str_id); //
			if (li_row >= 0) {
				billListPanel_User_Post.setSelectedRow(li_row); //
			}
		}
		tabbedPane.setSelectedIndex(0);
	}

	/**
	 * ��ƽ�鿴ҳǩ���༭��ť���߼�
	 * 
	 * @throws Exception
	 */
	private void onEditUser() throws Exception {
		BillVO billVO = billListPanel_user_flat.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.show(this, MSG_NEED_USER); //
			return;
		}

		String[] str_allControlDeptIDs = UIUtil.getLoginUserBLDeptIDs(); //
		if (str_allControlDeptIDs != null) { // ������ǿ�ȫ�е�Ȩ��,����ҪУ���Ƿ���Ȩ�޸�
			String str_id = billVO.getStringValue("id"); //
			String str_name = billVO.getStringValue("name"); //			
			String[][] str_userdepts = new String[0][0];
			if (str_id == null || str_id.equals("")) {

			} else {
				str_userdepts = UIUtil.getStringArrayByDS(null,
						"select deptid,deptname from v_pub_user_post_1 where userid="
								+ str_id); //

			}
			String[] str_thisuserBLDeptIDs = new String[str_userdepts.length]; //
			String str_deptnames = ""; //
			for (int i = 0; i < str_thisuserBLDeptIDs.length; i++) { //
				str_thisuserBLDeptIDs[i] = str_userdepts[i][0]; //
				str_deptnames += str_userdepts[i][1] + ","; //
			}
			str_deptnames = str_deptnames.substring(0,
					str_deptnames.length() - 1);
			if (!new TBUtil().containTwoArrayCompare(str_allControlDeptIDs,
					str_thisuserBLDeptIDs)) { // �����¼��Ա���Ƶķ�Χ�����������!����Ȩ�޸�,���򱨴���ʾ!
				MessageBox.show(this, "��" + str_name + "�����ڻ�����" + str_deptnames
						+ "��,��û��Ȩ�޲���!"); //
				return; //
			}
		}
		AddOrEditUserDialog editDialog = new AddOrEditUserDialog(this, billVO
				.getStringValue("id"), null, null,
				WLTConstants.BILLDATAEDITSTATE_UPDATE);
		String oldposition = editDialog.getBillcardPanel().getBillVO()
				.getStringValue("position");
		editDialog.setVisible(true);
		if (editDialog.getCloseType() == 1) {
			billListPanel_user_flat.refreshCurrData();
			BillVO newuservo = editDialog.getBillcardPanel().getBillVO();
			// ���ְ�����仯 ,��ʾ�Ƿ����ְ
			if (!newuservo.getStringValue("position", "").equals(oldposition)) {
				if (MessageBox.confirm(this, newuservo.getStringValue("name")
						+ "��ְ���[" + oldposition + "]�޸�Ϊ["
						+ newuservo.getStringValue("position", "")
						+ "],�Ƿ��������ְ��Ϣ?")) {
					checkandcreateduties(newuservo.getStringValue("id"));
				}
			}
		}
	}

	private void onDeptDelete() {
		BillTreeNodeVO treeNodeVO = billTreePanel_Dept.getSelectedTreeNodeVO();
		BillVO billVO = billTreePanel_Dept.getSelectedVO();
		if (billVO == null) {
			MessageBox.show(this, "��ѡ��һ��������ɾ������!"); //
			return;
		}

		if (billVO != null) {
			BillVO[] childVOs = billTreePanel_Dept
					.getSelectedChildPathBillVOs(); // ȡ������ѡ�е�
			if (MessageBox.showConfirmDialog(this, "��ȷ��Ҫɾ����¼��"
					+ treeNodeVO.toString() + "����?\r\n�⽫һ��ɾ�����¹���"
					+ childVOs.length + "���������¼,����ؽ�������!", "��ʾ",
					JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}
			try {
				Vector v_sqls = new Vector(); // �˵�ɾ��
				for (int i = 0; i < childVOs.length; i++) {
					v_sqls.add("delete from " + childVOs[i].getSaveTableName()
							+ " where " + childVOs[i].getPkName() + "='"
							+ childVOs[i].getPkValue() + "'"); //
				}
				UIUtil.executeBatchByDS(null, v_sqls); // ִ�����ݿ����!!
				billTreePanel_Dept.delCurrNode(); //
				billTreePanel_Dept.getJTree().repaint(); //
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		reloaCorpCacheData();
	}

	private void reloaCorpCacheData() {
		try {
			SysAppServiceIfc service = (SysAppServiceIfc) UIUtil
					.lookUpRemoteService(SysAppServiceIfc.class); //
			service.registeCorpCacheData(); // ����ע��һ�»�����������!!
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	class AddOrEditUserDialog extends BillCardDialog {
		private String returnNewID; // �µĻ�����Ա��λ��ID
		private String newuserid;
		private String str_deptid, str_postid, pwdwidth; //
		private DESKeyTool des = new DESKeyTool();
		private String editoldCode;
		private boolean haveUserNotHaveInfo = false; // pubuser�������ݣ�����ﲻ����

		public AddOrEditUserDialog(Container _parent, String _userid,
				String _deptid, String _postId, String _edittype) {
			super(_parent, "SAL_PERSONINFO_CODE2", _edittype);
			str_deptid = _deptid;
			str_postid = _postId;
			if (_edittype == null) {
				return;
			} else if (_edittype
					.equalsIgnoreCase(WLTConstants.BILLDATAEDITSTATE_INSERT)) {
				addInit();
			} else if (_edittype
					.equalsIgnoreCase(WLTConstants.BILLDATAEDITSTATE_UPDATE)) {
				editInit(_userid);
			}
		}

		private void addInit() {
			try {
				billcardPanel.setEditableByInsertInit();
				billcardPanel
						.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT); //
				if (TBUtil.getTBUtil().isEmpty(str_deptid)) {
					BillVO deptVO = billTreePanel_Dept.getSelectedVO();
					str_deptid = deptVO.getPkValue();
				}
				if (TBUtil.getTBUtil().isEmpty(str_postid)) {
					BillVO postVO = billListPanel_Post.getSelectedBillVO();
					str_postid = postVO.getPkValue();
				}
				newuserid = UIUtil.getSequenceNextValByDS(null, "S_PUB_USER");
				billcardPanel.setValueAt("id", new StringItemVO(newuserid));
				String deptName = UIUtil.getStringValueByDS(null,
						"select name from pub_corp_dept where id = "
								+ str_deptid);
				billcardPanel.setValueAt("maindeptid", new RefItemVO(
						str_deptid, "", deptName));
				String postName = UIUtil.getStringValueByDS(null,
						"select name from pub_post where id = " + str_postid);
				if (postName != null) {
					billcardPanel.setValueAt("mainstation", new StringItemVO(
							postName));
				}
				HashMap menuMap = getMenuConfMap();
				if (menuMap != null && menuMap.containsKey("���")) {
					pwdwidth = (String) menuMap.get("���");
				} else {
					pwdwidth = new TBUtil().getSysOptionStringValue(
							"��ʼ����Ա������", null);
				}
				if (pwdwidth != null && !pwdwidth.equals("")
						&& !pwdwidth.equals("0")) {
					this.getCardPanel().getCompentByKey("PWD").getLabel()
							.setText("*����(" + pwdwidth + ")λ");
				}
				btn_save.setVisible(false); //
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		private void editInit(String userid) {
			try {
				HashVO[] userVO = UIUtil.getHashVoArrayByDS(null,
						"select *from pub_user where id = " + userid);
				if (userVO.length > 0) { // �ҵ���Աvo
					billcardPanel.queryDataByCondition(" id =" + userid);
					String infoid = billcardPanel.getBillVO().getStringValue(
							"id");
					if (infoid == null || infoid.equals("")) { // pub_user
																// �Ѿ����ڸ���Ա�����ǣ�����û�������ݡ�
						billcardPanel.setEditableByInsertInit();
						billcardPanel
								.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
						haveUserNotHaveInfo = true;
						billcardPanel.setRealValueAt("id", userid);
						billcardPanel.setRealValueAt("name", userVO[0]
								.getStringValue("NAME"));
						billcardPanel.setRealValueAt("code", userVO[0]
								.getStringValue("CODE"));
						if (TBUtil.getTBUtil().isEmpty(str_deptid)
								|| TBUtil.getTBUtil().isEmpty(str_postid)) {
							try {
								HashVO[] all = UIUtil
										.getHashVoArrayByDS(
												null,
												"select userdept, isdefault, postid from pub_user_post where userid='"
														+ userid
														+ "' and userdept in (select id from pub_corp_dept) order by id asc");
								if (all != null && all.length > 0) {
									String maindeptid = all[0]
											.getStringValue("userdept");
									for (int i = 0; i < all.length; i++) {
										if ("Y".equals(all[i]
												.getStringValue("isdefault"))) {
											maindeptid = all[i]
													.getStringValue("userdept");
											break;
										}
									}
									List postid = new ArrayList();
									for (int i = 0; i < all.length; i++) {
										if (all[i].getStringValue("postid") != null
												&& !""
														.equals(all[i]
																.getStringValue("postid"))) {
											postid.add(all[i]
													.getStringValue("postid"));
										}
									}
									FrameWorkCommServiceIfc commService = (FrameWorkCommServiceIfc) UIUtil
											.lookUpRemoteService(FrameWorkCommServiceIfc.class);
									HashMap returnMap = commService
											.getTreePathNameByRecords(null,
													"pub_corp_dept", "id",
													"name", "parentid",
													new String[] { maindeptid });
									billcardPanel.setValueAt("maindeptid",
											new RefItemVO(maindeptid, "",
													returnMap.get(maindeptid)
															+ ""));
									String[][] mainpost = UIUtil
											.getStringArrayByDS(
													null,
													"select name,stationkind from pub_post where deptid='"
															+ maindeptid
															+ "' and name is not null and id in ("
															+ TBUtil
																	.getTBUtil()
																	.getInCondition(
																			postid)
															+ ") order by seq, id ");
									if (mainpost != null && mainpost.length > 0) {
										billcardPanel.setValueAt("mainstation",
												mainpost[0][0]);
										billcardPanel.setRealValueAt(
												"stationkind", mainpost[0][1]);
									}
								}
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						} else {
							String deptName = UIUtil.getStringValueByDS(null,
									"select name from pub_corp_dept where id = "
											+ str_deptid);
							billcardPanel.setValueAt("maindeptid",
									new RefItemVO(str_deptid, "", deptName));
							String post[][] = UIUtil.getStringArrayByDS(null,
									"select name,stationkind from pub_post where id = "
											+ str_postid);
							if (post.length > 0) {
								billcardPanel.setValueAt("mainstation",
										new RefItemVO(post[0][0], "",
												post[0][0]));
								billcardPanel.setValueAt("stationkind",
										new ComBoxItemVO(post[0][1], "",
												post[0][1]));
							}
						}

					} else {
						billcardPanel.setEditableByEditInit();
						billcardPanel
								.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
					}
					String pwd = des.decrypt(userVO[0].getStringValue("pwd"));
					if (pwd == null || pwd.equals("")) {
						pwd = userVO[0].getStringValue("pwd");
					}
					billcardPanel.setValueAt("pwd", new StringItemVO(pwd));
					editoldCode = userVO[0].getStringValue("code");
					billcardPanel.setValueAt("code", new StringItemVO(
							editoldCode));
				}
				btn_save.setVisible(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void showInit() {

		}

		public void onConfirm() {
			try {
				if (!billcardPanel.checkValidate()) {
					return;
				}

				String str_userid = billcardPanel.getRealValueAt("id"); //
				String str_usercode = billcardPanel.getRealValueAt("code")
						.trim(); // ��Ա����,���У��һ�¸���Ա����
				if (pwdwidth != null) {
					if (!checkPwd()) {
						return;
					}
				}
				if (billcardPanel.getEditState().equals(
						WLTConstants.BILLDATAEDITSTATE_UPDATE)
						|| haveUserNotHaveInfo) {
					updateUser();
					this.setCloseType(1);
					this.dispose(); //
					return;
				}

				// У�����Ա�Ƿ��Ѵ���!!!
				HashVO[] hvos = UIUtil.getHashVoArrayByDS(null,
						"select count(code) c1 from pub_user where code='"
								+ str_usercode + "'"); //
				int li_count = hvos[0].getIntegerValue("c1").intValue(); //
				if (li_count > 0) {
					MessageBox.show(null, "ϵͳ���Ѿ�����һ������С�" + str_usercode
							+ "�����û�,�����ظ�¼��,��ʹ�õ���������䵼�뱾����!"); //
					return;
				}
				// У�����Ա�ڸû���,�ø�λ�Ƿ����!!
				String str_insertsql = billcardPanel.getUpdateDataSQL(); //

				// ���Ӷ�pub_user pk_dept ��ͬ������ Gwang 2012-4-17
				if (this.getCardPanel().getRealValueAt("PK_DEPT") != null
						&& !"".equals(this.getCardPanel().getRealValueAt(
								"PK_DEPT"))) {
					str_deptid = this.getCardPanel().getRealValueAt("PK_DEPT");
				}
				InsertSQLBuilder insertPubUserSql = new InsertSQLBuilder(
						"pub_user"); // ����pub_user��
				insertPubUserSql.putFieldValue("id", newuserid);
				insertPubUserSql.putFieldValue("code", str_usercode);
				insertPubUserSql.putFieldValue("pwd", des.encrypt(billcardPanel
						.getRealValueAt("pwd"))); //
				insertPubUserSql.putFieldValue("name", billcardPanel
						.getRealValueAt("name").trim());
				insertPubUserSql.putFieldValue("createdate", UIUtil
						.getServerCurrDate());
				insertPubUserSql.putFieldValue("pk_dept", str_deptid);
				insertPubUserSql.putFieldValue("ISFUNFILTER", "Y");
				insertPubUserSql.putFieldValue("creator", ClientEnvironment
						.getCurrSessionVO().getLoginUserName());

				String str_newpk = UIUtil.getSequenceNextValByDS(null,
						"S_PUB_USER_POST"); //
				UIUtil.executeBatchByDS(null, new String[] {
						insertPubUserSql.getSQL(),
						str_insertsql,
						getSQL_User_Post(str_newpk, str_userid, str_deptid,
								str_postid) }); // //
				returnNewID = str_newpk;
				this.setCloseType(1);
				this.dispose(); //
			} catch (Exception e) {
				MessageBox.showException(this, e);
			} //
		}

		// ִ���޸���Ա
		private void updateUser() {
			// editoldCode
			try {
				String str_usercode = billcardPanel.getRealValueAt("code")
						.trim(); // ��Ա����,���У��һ�¸���Ա����
				if (!editoldCode.trim().equals(str_usercode)) {
					HashVO[] hvos = UIUtil.getHashVoArrayByDS(null,
							"select count(code) c1 from pub_user where code='"
									+ str_usercode + "'");
					int li_count = hvos[0].getIntegerValue("c1").intValue(); //
					if (li_count > 0) {
						MessageBox.show(null, "ϵͳ���Ѿ�����һ������С�" + str_usercode
								+ "�����û�,�����ظ�!"); //
						return;
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			} //
			String updateInfoSql = billcardPanel.getUpdateDataSQL(); //
			UpdateSQLBuilder insertPubUserSql = new UpdateSQLBuilder("pub_user"); // ����pub_user��
			insertPubUserSql.putFieldValue("code", billcardPanel
					.getRealValueAt("code"));
			insertPubUserSql.putFieldValue("pwd", des.encrypt(billcardPanel
					.getRealValueAt("pwd")));
			insertPubUserSql.putFieldValue("name", billcardPanel
					.getRealValueAt("name"));
			insertPubUserSql.setWhereCondition("id = "
					+ billcardPanel.getRealValueAt("id"));
			try {
				UIUtil.executeBatchByDS(null, new String[] { updateInfoSql,
						insertPubUserSql.getSQL() });
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/*
		 * �ڳ�ʼ���û�ʱ�� ���������볤��У�顣̫ƽ�����
		 */
		private boolean checkPwd() {
			String sr_newpwd = this.getCardPanel().getBillVO().getStringValue(
					"PWD");
			if (pwdwidth != null && pwdwidth.contains("-")) {
				String[] str_pwdlength = pwdwidth.split("-");
				if (sr_newpwd.length() < Integer.parseInt(str_pwdlength[0])
						|| sr_newpwd.length() > Integer
								.parseInt(str_pwdlength[1])) { // 10-16λ֮��
					MessageBox.show(this, "�����볤�ȱ�����" + pwdwidth + "λ֮��,���޸�!"); //
					return false;
				}

			} else if (pwdwidth != null
					&& sr_newpwd.length() != Integer.parseInt(pwdwidth)
					&& !pwdwidth.equals("0")) {
				MessageBox.show(this, "�����볤�ȱ���Ϊ" + pwdwidth + "λ,���޸�!"); //
				return false;
			}
			return true;
		}

		public String getReturnNewID() {
			return returnNewID; //
		}
	}

	/*
	 * ��Ա�б�˫���¼� (non-Javadoc)
	 * 
	 * @seecn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedListener#
	 * onMouseDoubleClicked
	 * (cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedEvent)
	 */
	public void onMouseDoubleClicked(BillListMouseDoubleClickedEvent event) {

		BillVO billVO = billListPanel_User_Post.getSelectedBillVO();
		String userID = billVO.getStringValue("userid");
		BillCardPanel billCard = new BillCardPanel("SAL_PERSONINFO_CODE1");
		billCard.queryDataByCondition("id = '" + userID + "'");
		BillCardDialog dialog = new BillCardDialog(billListPanel_User_Post,
				"��Ա��Ϣ", billCard, WLTConstants.BILLDATAEDITSTATE_INIT);
		dialog.setVisible(true);

	}

}
