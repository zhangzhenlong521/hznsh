package cn.com.infostrategy.ui.sysapp.corpdept;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.to.sysapp.login.RoleVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

/**
 * ����-��λ-��Ա-��ɫά�������Ը��ݲ˵���������Ϊ���ֲ�ͬ���1-��������λ;2-��������λ����Ա;3-��������Ա;4-��������Ա����ɫ;5-��������λ
 * ����Ա����ɫ
 * 
 * ��������õĲ˵����������²˵����ã�"ҳ�沼��","5","�Ƿ���Ա�ƽ�鿴","Y", �Ժ��⽫��Ϊ����Ҫ��,������Ψһ�Ի���,��λ,��Աά������!!
 * 
 * @author xch
 * 
 */
public class DeptPostUserManagerWFPanel extends AbstractWorkPanel implements BillTreeSelectListener, BillListSelectListener, ActionListener {

	private BillTreePanel billTreePanel_Dept = null; // ������
	private BillListPanel billListPanel_Post = null; // ��λ��
	private BillListPanel billListPanel_User_Post = null; // ��Ա��λ��.
	private BillListPanel billListPanel_User_Role = null; // ��Ա��ɫ��ϵ��.
	private BillListPanel billListPanel_user_flat = null; // ��Ա��ƽ�鿴

	private WLTTabbedPane tabbedPane = null; // ҳǩ������˵����������ÿ��Ա�ƽ�鿴������ά����������Ա�б�����Ҫ��ҳǩ��ʽ��ʾ

	private WLTButton btn_post_insert, btn_post_update, btn_post_delete, btn_post_seq; // ��λ�б��ϵ�ϵ�а�ť
	private WLTButton btn_user_import, btn_user_add, btn_user_update, btn_user_trans, btn_user_del, btn_user_seq, btn_lookdeptinfo1; // ��Ա�б��ϵ�ϵ�а�ť
	private WLTButton btn_role_add, btn_role_del; // ��ɫ�б��ϵ�ϵ�а�ť

	private WLTButton btn_gotodept, btn_edituser, btn_lookdeptinfo2;// ��ƽ�鿴��ϵ�а�ť

	private String str_currdeptid, str_currdeptname = null; // ��ǰ����ID����������

	private int li_granuType = 3; // ��������,1-��ֵ�,2-��õ�,3-��ϸ��,�����е�
	private boolean isFilter = false; // �Ƿ�Ȩ�޹���,���Ƿ���ݵ�¼��Ա����Ȩ�޹���!!!
	private String panelStyle = "5";// ҳ�沼�ַ�񣬼��ݾ�ϵͳ��Ĭ���ǻ�������λ����Ա����ɫ ����
	private boolean canSearch = true;// �Ƿ��б�ƽ�鿴ҳǩ

	private WLTButton btn_setdefault;

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
			if (tmp_str.equalsIgnoreCase("N") || tmp_str.equalsIgnoreCase("false") || tmp_str.equalsIgnoreCase("��")) {
				canSearch = false;
			}
		}
		this.setLayout(new BorderLayout()); //
		// ��ǰ�Ļ��������޷������֧��ϵͳ����Ա�����Լ�����֧�еĻ�������Ա�����пɹ������л�������Ա�����󣬹�Ϊ����չ���������ò˵��������ڻ���ģ�������û�����ѯ���Լ��ɡ����/2016-03-18��
		if (confmap.get("����ģ��") != null) {
			String treeTempletCode = (String) confmap.get("����ģ��");
			billTreePanel_Dept = new CorpDeptBillTreePanel(treeTempletCode, getGranuType(), isFilter()); // ������,�����Ƿ���Ҫ����Ȩ�޹���!!
		} else {
			billTreePanel_Dept = new CorpDeptBillTreePanel(getCorpTempletCode(), getGranuType(), isFilter()); // ������,�����Ƿ���Ҫ����Ȩ�޹���!!
		}
		billTreePanel_Dept.queryDataByCondition(null); // �鿴���л���
		billTreePanel_Dept.addBillTreeSelectListener(this); //

		WLTSplitPane splitPanel_all = null;

		if ("1".equals(panelStyle)) {// ά��ҳ���Ƿ��ǵ�һ�ַ��ֻ�� ��������λ
			splitPanel_all = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTreePanel_Dept, this.getPostPanel()); //
			splitPanel_all.setDividerLocation(300); //
		} else if ("2".equals(panelStyle)) {// �Ƿ��ǵڶ��ַ��ֻ�� ��������λ����Ա
			WLTSplitPane splitPanel_post_user = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, this.getPostPanel(), this.getUserPanel()); //
			splitPanel_post_user.setDividerLocation(175); //

			splitPanel_all = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTreePanel_Dept, splitPanel_post_user); //
			splitPanel_all.setDividerLocation(300); //
		} else if ("3".equals(panelStyle)) {// �Ƿ��ǵ����ַ��ֻ�� ��������Ա
			splitPanel_all = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTreePanel_Dept, this.getUserPanel()); //
			splitPanel_all.setDividerLocation(300); //
		} else if ("4".equals(panelStyle)) {// �Ƿ��ǵ����ַ��ֻ�� ��������Ա����ɫ
			WLTSplitPane splitPanel_user_role = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, this.getUserPanel(), this.getRolePanel()); //
			splitPanel_user_role.setDividerLocation(300); //

			splitPanel_all = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTreePanel_Dept, splitPanel_user_role); //
			splitPanel_all.setDividerLocation(280); //
		} else {// �����ַ��Ҳ���� ��������λ����Ա����ɫ ����
			WLTSplitPane splitPanel_user_role = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, this.getUserPanel(), this.getRolePanel()); //
			splitPanel_user_role.setDividerLocation(360); //

			WLTSplitPane splitPanel_post_user = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, this.getPostPanel(), splitPanel_user_role); //
			splitPanel_post_user.setDividerLocation(175); //

			splitPanel_all = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTreePanel_Dept, splitPanel_post_user); //
			splitPanel_all.setDividerLocation(175); //
		}

		if (canSearch) {// �������Ϊ���Ա�ƽ�鿴����������
			tabbedPane = new WLTTabbedPane(); //
			tabbedPane.addTab("��Ϣά��", UIUtil.getImage("office_050.gif"), splitPanel_all); //
			tabbedPane.addTab("��ƽ�鿴", UIUtil.getImage("office_070.gif"), this.getUserFlatPanel()); //
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
		return "PUB_CORP_DEPT_1"; // ��򵥵�
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
			billListPanel_Post = new BillListPanel("PUB_POST_CODE1"); // ��λ
			billListPanel_Post.setItemVisible("refpostid", true);// �ڴ���ʾ��λ����Ϣ
			btn_post_insert = new WLTButton("����"); // ������λ!!
			btn_post_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); // �޸ĸ�λ
			btn_post_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE); // ɾ����λ!!
			btn_post_seq = new WLTButton("����");// ��λ����
			btn_post_insert.addActionListener(this); //
			btn_post_seq.addActionListener(this);
			billListPanel_Post.addBatchBillListButton(new WLTButton[] { btn_post_insert, btn_post_update, btn_post_delete, btn_post_seq }); //
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
			billListPanel_User_Post = new BillListPanel("PUB_USER_POST_DEFAULT"); // ��Ա_��λ�Ĺ�����!
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
			// ��֧��ϵͳ����Ա��ά������֧���û�����Ϊ��ֹ��֧����ɾ�û���������ƽ̨��������������Щ��ɫ��ɾ���û���һ��Ӧ����ȫ��ϵͳ����Ա�����/2016-07-13��
			String deleteRoles = TBUtil.getTBUtil().getSysOptionStringValue("��ɾ���û��Ľ�ɫ", "");// ��֧�ֶ����ɫ���������Ӣ�Ķ��ŷָ�
			boolean canDeleteUser = false;
			if (deleteRoles == null || "".equals(deleteRoles.trim())) {// ���û�����û�����Ϊ�գ����ɾ���û������/2016-07-13��
				canDeleteUser = true;
			} else {
				RoleVO[] roles = ClientEnvironment.getInstance().getCurrLoginUserVO().getRoleVOs();// ��¼��Ա���н�ɫ
				if (roles != null && roles.length > 0) {
					String[] str_deleteRoles = TBUtil.getTBUtil().split(deleteRoles, ",");
					for (int i = 0; i < roles.length; i++) {
						for (int j = 0; j < str_deleteRoles.length; j++) {
							if (str_deleteRoles[j].equalsIgnoreCase(roles[i].getName())) {
								canDeleteUser = true;
								break;
							}
						}
						if (canDeleteUser) {
							break;
						}
					}
				}
			}
			if (TBUtil.getTBUtil().getSysOptionBooleanValue("ϵͳ�Ƿ����ε��û���������", false)) {// ̫ƽ��Ҫ�����������ܡ����/2017-04-16��
				if (canDeleteUser) {
					billListPanel_User_Post.addBatchBillListButton(new WLTButton[] { btn_user_import, btn_user_update, btn_user_trans, btn_user_del, btn_user_seq, btn_lookdeptinfo1, btn_setdefault }); // Ϊ����_��Ա_��λ��������а�ť!!
				} else {
					billListPanel_User_Post.addBatchBillListButton(new WLTButton[] { btn_user_import, btn_user_update, btn_user_trans, btn_user_seq, btn_lookdeptinfo1, btn_setdefault }); // Ϊ����_��Ա_��λ��������а�ť!!
				}
			} else {
				if (canDeleteUser) {
					billListPanel_User_Post.addBatchBillListButton(new WLTButton[] { btn_user_import, btn_user_add, btn_user_update, btn_user_trans, btn_user_del, btn_user_seq, btn_lookdeptinfo1, btn_setdefault }); // Ϊ����_��Ա_��λ��������а�ť!!
				} else {
					billListPanel_User_Post.addBatchBillListButton(new WLTButton[] { btn_user_import, btn_user_add, btn_user_update, btn_user_trans, btn_user_seq, btn_lookdeptinfo1, btn_setdefault }); // Ϊ����_��Ա_��λ��������а�ť!!
				}
			}
			billListPanel_User_Post.repaintBillListButton(); //
			// ����н�ɫ����������Ա��ѡ���¼�
			if ("4".equals(panelStyle) || "5".equals(panelStyle)) {//
				billListPanel_User_Post.addBillListSelectListener(this); // ��Աѡ��仯ʱ.
			}
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
			billListPanel_User_Role.addBatchBillListButton(new WLTButton[] { btn_role_add, btn_role_del }); //
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
			btn_lookdeptinfo2 = new WLTButton("��ְ���"); //
			btn_gotodept.addActionListener(this); //
			btn_lookdeptinfo2.addActionListener(this); //
			boolean showEditUser = TBUtil.getTBUtil().getSysOptionBooleanValue("��Ա��ƽ�鿴�Ƿ�ɱ༭", true);// �ڽ������з��֣���֧��ϵͳ����Ա��Ȼ���ɿ������л��������ڱ�ƽ�鿴���ܲ�ѯ��������Ա�������޸����룬�����Ӳ������á����/2016-06-28��
			if (showEditUser) {
				btn_edituser = new WLTButton("�༭"); //
				btn_edituser.addActionListener(this); //
				billListPanel_user_flat.addBatchBillListButton(new WLTButton[] { btn_gotodept, btn_edituser, btn_lookdeptinfo2 }); // //
			} else {
				billListPanel_user_flat.addBatchBillListButton(new WLTButton[] { btn_gotodept, btn_lookdeptinfo2 }); // //
			}
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
			billListPanel_Post.queryDataByCondition("deptid='" + str_currdeptid + "'", "seq,code"); //
		}
		if (billListPanel_User_Post != null) {
			billListPanel_User_Post.queryDataByCondition("deptid='" + str_currdeptid + "'", "seq,usercode"); //
		}
		if (billListPanel_User_Role != null) {
			billListPanel_User_Role.clearTable(); //
		}
	}

	/**
	 * �б��ѡ���¼���ѡ���λˢ����Ա��ѡ����Աˢ�½�ɫ
	 */
	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		if (_event.getSource() == billListPanel_Post && billListPanel_User_Post != null) {// ���������Ǹ�λ�б�
			// ��������Ա�б���ˢ����Ա�б�
			String str_postid = _event.getCurrSelectedVO().getStringValue("id"); //
			//��λ�����������õ�idΪnull�����ж�һ�£������е�oracle���ݿ�ִ��postid='null'�ᱨ�����/2018-08-11��
			if (str_postid == null) {
				billListPanel_User_Post.clearTable();//
			} else {
				billListPanel_User_Post.queryDataByCondition("postid='" + str_postid + "'", "seq"); //
			}
			if (billListPanel_User_Role != null) {
				billListPanel_User_Role.clearTable(); //
			}
		} else if (_event.getSource() == billListPanel_User_Post && billListPanel_User_Role != null) {// ������������Ա�б�
			// �����н�ɫ�б���ˢ�½�ɫ�б�
			String str_useridid = _event.getCurrSelectedVO().getStringValue("userid"); //
			billListPanel_User_Role.QueryDataByCondition("userid='" + str_useridid + "'"); //
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
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
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
		String sql_1 = "update pub_user_post set isdefault = null where userid = " + userid;
		String sql_2 = "update pub_user_post set isdefault = 'Y' where id = " + pk;
		String sql_3 = "update pub_user set pk_dept = " + str_currdeptid + " where id = " + userid;
		UIUtil.executeBatchByDS(null, new String[] { sql_1, sql_2, sql_3 });

		if (billListPanel_Post == null) {// ˢ���б����ݡ����/2014-02-28��
			billListPanel_User_Post.QueryDataByCondition("deptid = " + str_currdeptid);
		} else {
			BillVO postVO = billListPanel_Post.getSelectedBillVO();
			if (postVO != null) {
				billListPanel_User_Post.refreshCurrSelectedRow();
			} else {
				billListPanel_User_Post.QueryDataByCondition("deptid = " + str_currdeptid);
			}
		}
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
		initValueMap.put("deptid", new RefItemVO(str_currdeptid, "", str_currdeptname)); // �����ʼֵ!�����Ҫ������ظ�λ����Ҫ������������Ϊ�˲��գ�������Ҫ�޸����Ϊ����vo�����/2012-03-29��
		billListPanel_Post.doInsert(initValueMap); // ����������!!!
	}

	/**
	 * ��λ����
	 */
	private void onSeqPost() {
		// һ��ʼ�õ�billListPanel_Post.getBillVOs(),����ɾ���˵ļ�¼Ҳ������,ע��billListPanel_Post.getAllBillVOs()
		// �ǻ����ʾ��billvo��������ɾ����billvo��
		SeqListDialog dialog_post = new SeqListDialog(this, "��λ����", billListPanel_Post.getTempletVO(), billListPanel_Post.getAllBillVOs());
		dialog_post.setVisible(true);
		if (dialog_post.getCloseType() == 1) {// ������ȷ�����أ�����Ҫˢ��һ��ҳ��
			billListPanel_Post.queryDataByCondition("deptid='" + str_currdeptid + "'", "seq,code"); //
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
				if (JOptionPane.showConfirmDialog(this, "��û��ѡ���λ, �Ƿ����?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
					return; //
				}
			} else {
				str_postid = billVO_Post.getStringValue("id"); // ��λID
				str_postname = billVO_Post.getStringValue("name"); // ��λID
			}
		}

		BillListDialog dialog = new BillListDialog(this, "ѡ����Ա", "PUB_USER_ICN", 700, 500); //
		dialog.getBilllistPanel().setAllBillListBtnVisiable(false);
		dialog.getBilllistPanel().setItemEditable(false); //
		dialog.setVisible(true); //
		if (dialog.getCloseType() != 1) {
			return;
		}

		BillVO[] billVOS = dialog.getReturnBillVOs(); //
		String str_userid = billVOS[0].getStringValue("id"); //
		String str_username = billVOS[0].getStringValue("name"); //

		boolean bo_checked = checkUserDeptPost(str_currdeptid, str_currdeptname, str_postid, str_postname, str_userid, str_username); // �жϸ���Ա�Ƿ��ڴ˻����͸�λ�£�����ڣ��Ͳ��õ�����
		if (!bo_checked) {
			return;
		}

		HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, "select deptname,postname from v_pub_user_post_1 where userid='" + str_userid + "'"); //
		if (hvs.length == 0) {
			if (JOptionPane.showConfirmDialog(this, "ȷ��Ҫ�ѡ�" + str_username + "�����뵽��" + str_currdeptname + "��������?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return; //
			}

			String str_newpk = UIUtil.getSequenceNextValByDS(null, "S_PUB_USER_POST"); //
			String str_sql = getSQL_User_Post(str_newpk, str_userid, str_currdeptid, str_postid);
			// ���Ӷ�pub_user pk_dept ��ͬ������ Gwang 2012-4-17
			String sql = "update pub_user set pk_dept = " + str_currdeptid + " where id = " + str_userid;
			UIUtil.executeBatchByDS(null, new String[] { str_sql, sql });
			billListPanel_User_Post.addBillVOs(billListPanel_User_Post.queryBillVOsByCondition("id='" + str_newpk + " '")); //
			MessageBox.show(this, "����ɹ�!"); //
		} else {
			String str_alreadyDeptNames = "";
			for (int i = 0; i < hvs.length; i++) {
				str_alreadyDeptNames = str_alreadyDeptNames + "������" + hvs[i].getStringValue("deptname") + "��,��λ��" + (hvs[i].getStringValue("postname") == null ? "" : hvs[i].getStringValue("postname")) + "��\r\n";
			}

			int li_result = -1; //
			if (str_postid != null) {
				li_result = JOptionPane.showOptionDialog(this, "����Ա��ǰ����:\r\n" + str_alreadyDeptNames + "��ϣ��������Ա��β���?", //
						"��ʾ", JOptionPane.DEFAULT_OPTION, //
						JOptionPane.QUESTION_MESSAGE, null, //
						new String[] { "Ǩ�Ƶ���������λ", "���Ƶ���������λ", "ȡ��" }, //
						"ȡ��"); //
			} else {
				li_result = JOptionPane.showOptionDialog(this, "����Ա��ǰ����:\r\n" + str_alreadyDeptNames + "��ϣ��������Ա��β���?", //
						"��ʾ", JOptionPane.DEFAULT_OPTION, //
						JOptionPane.QUESTION_MESSAGE, null, //
						new String[] { "Ǩ�Ƶ�������", "���Ƶ�������", "ȡ��" }, //
						"ȡ��"); //
			}

			if (li_result == 0) { // Ǩ�Ƶ�������, (ɾ��������Ա�͸�λ��ϵ, ��������һ����Ա�͸�λ�Ķ�Ӧ��ϵ,
				// ������ΪĬ�ϸ�λ Gwang 2013-07-30)
				String str_newpk = UIUtil.getSequenceNextValByDS(null, "S_PUB_USER_POST"); //
				String str_sql = getSQL_User_Post(str_newpk, str_userid, str_currdeptid, str_postid);
				// ���Ӷ�pub_user pk_dept ��ͬ������ Gwang 2012-4-17
				String sql = "update pub_user set pk_dept = " + str_currdeptid + " where id = " + str_userid;
				UIUtil.executeBatchByDS(null, new String[] { "delete from pub_user_post where userid=" + str_userid, str_sql, sql }); //
				billListPanel_User_Post.addBillVOs(billListPanel_User_Post.queryBillVOsByCondition("id='" + str_newpk + " '")); //
			} else if (li_result == 1) { // ���Ƶ�������(������Ա�͸�λ�Ķ�Ӧ��ϵ, ԭ����Ĭ�ϸ�λ����
				// Gwang 2013-07-30)
				String str_newpk = UIUtil.getSequenceNextValByDS(null, "S_PUB_USER_POST"); //
				String str_sql = getSQL_User_Post(str_newpk, str_userid, str_currdeptid, str_postid);
				String sql_update = "update pub_user_post set isdefault = null where id=" + str_newpk;
				UIUtil.executeBatchByDS(null, new String[] { str_sql, sql_update }); //
				billListPanel_User_Post.addBillVOs(billListPanel_User_Post.queryBillVOsByCondition("id='" + str_newpk + " '")); //

			}
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
	private String getSQL_User_Post(String str_newpk, String str_userid, String str_currdeptid2, String str_postid) {
		InsertSQLBuilder isql = new InsertSQLBuilder("pub_user_post");
		isql.putFieldValue("id", str_newpk);
		isql.putFieldValue("userid", str_userid);
		isql.putFieldValue("postid", str_postid);
		isql.putFieldValue("userdept", str_currdeptid);
		isql.putFieldValue("isdefault", "Y");
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
				if (JOptionPane.showConfirmDialog(this, "��û��ѡ���λ,�Ƿ����?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
					return;
				}
			} else {
				str_postid = billVO_Post.getStringValue("id"); // ��λID
			}
		}

		AddUserDialog dialog = new AddUserDialog(this, str_currdeptid, str_postid, getMenuConfMap()); //
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {
			String str_newid = dialog.getReturnNewID(); //
			billListPanel_User_Post.addBillVOs(billListPanel_User_Post.queryBillVOsByCondition("id='" + str_newid + " '")); //		
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
		boolean falg = editUserDialog(str_userid);
		if (falg) {
			billListPanel_User_Post.setBillVOAt(billListPanel_User_Post.getSelectedRow(), billListPanel_User_Post.queryBillVOsByCondition("id='" + str_id + "'")[0]);
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

		TransUserDialog dialog = new TransUserDialog(this, str_id, str_currdeptid, str_currdeptname, str_userid, str_username, getGranuType(), isFilter());
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {

			// ˢ��һ��
			if (billListPanel_User_Post != null) {
				billListPanel_User_Post.queryDataByCondition("deptid='" + str_currdeptid + "'", "seq,postcode"); //
			}

			if (billListPanel_User_Role != null) {// ����н�ɫ�б������б�
				billListPanel_User_Role.clearTable();
			}
		}
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

		int li_result = JOptionPane.showOptionDialog(this, "������β���?", // 
				"��ʾ", //
				JOptionPane.DEFAULT_OPTION, //
				JOptionPane.QUESTION_MESSAGE, null, new String[] { "�Ƴ���" + str_currdeptname + "��", "����ɾ����Ա", "ȡ��" }, "ȡ��");
		boolean candelete = TBUtil.getTBUtil().getSysOptionBooleanValue("�Ƿ���Գ���ɾ����Ա", true);//̫ƽ���Ų�����������ϵͳ�г���ɾ����Ա�������Ӹò��������/2017-101-9��
		int li_result1 = 0;
		if (candelete) {//���Գ���ɾ��
			li_result1 = JOptionPane.showOptionDialog(this, "������β���?", // 
					"��ʾ", //
					JOptionPane.DEFAULT_OPTION, //
					JOptionPane.QUESTION_MESSAGE, null, new String[] { "�Ƴ���" + str_currdeptname + "��", "����ɾ����Ա", "ȡ��" }, "ȡ��");

		} else {
			li_result1 = JOptionPane.showOptionDialog(this, "������β���?", // 
					"��ʾ", //
					JOptionPane.DEFAULT_OPTION, //
					JOptionPane.QUESTION_MESSAGE, null, new String[] { "�Ƴ���" + str_currdeptname + "��", "ȡ��" }, "ȡ��");

		}
		// �Ƴ���Ա, �Ƚϸ��ӣ� Ҫ�����Ƿ���Ĭ�ϸ�λ�� �Ƿ��ж����λ�� Gwang 2013-07-30
		if (li_result1 == 0) {

			String isDefault = billVO.getStringValue("isdefault");
			String sql[] = null;
			String sql_update = "";
			String sql_del = "delete from pub_user_post where id=" + billVO.getStringValue("id");

			if ("Y".equals(isDefault)) { // �Ƴ�������Ҫ��λ
				String otherDefaultDeptID = this.getOtherDefaultDeptID(str_userid, str_currdeptid);
				if (otherDefaultDeptID.equals("")) { // ����ֻ��һ����λ
					sql_update = "update pub_user set pk_dept = null where id = " + str_userid;
					sql = new String[] { sql_del, sql_update };
				} else { // ����ж����λ
					String pk = "";
					if (otherDefaultDeptID.equals("many")) { // �ж���2����λ,
						// ���û�ѡ���µ���Ҫ��λ;
						BillListDialog dialog = new BillListDialog(this, "��ѡ���µ���Ҫ��λ", "PUB_USER_POST_DEFAULT", 700, 500); //
						dialog.getBilllistPanel().setAllBillListBtnVisiable(false);
						dialog.getBilllistPanel().setItemEditable(false);
						dialog.getBilllistPanel().QueryDataByCondition("userid = " + str_userid + " and deptid <> " + str_currdeptid);
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
					sql_update = "update pub_user set pk_dept = " + otherDefaultDeptID + " where id = " + str_userid;
					String sql_update2 = "update pub_user_post set isdefault = 'Y' where id = " + pk;
					sql = new String[] { sql_del, sql_update, sql_update2 };
				}

			} else {
				sql = new String[] { sql_del };
			}
			// for (String s : sql) {
			// System.out.println(s);
			// }

			UIUtil.executeBatchByDS(null, sql);

			// //׷��Ĭ�ϲ��Ÿ��ġ����/2013-05-20��
			// String sql_update = "";
			// HashVO[] hvos_user_post = UIUtil.getHashVoArrayByDS(null, "select
			// * from pub_user_post where userid='" + str_userid + "' and
			// userdept<>'" + str_currdeptid + "' order by isdefault");
			// if(hvos_user_post.length>0){
			// if(!hvos_user_post[0].getStringValue("isdefault",
			// "").equals("Y")){
			// if(!hvos_user_post[0].getStringValue("id", "").equals("")){
			// sql_update = "update pub_user_post set isdefault = 'Y' where id =
			// " + hvos_user_post[0].getStringValue("id", "");
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
		} else if (candelete && li_result1 == 1) { // ����ɾ����Ա
			ArrayList al_sqls = new ArrayList(); //
			al_sqls.add("delete from pub_user_post where userid='" + str_userid + "'"); //
			al_sqls.add("delete from pub_user_role where userid='" + str_userid + "'"); //
			al_sqls.add("delete from pub_user_menu where userid='" + str_userid + "'"); //
			al_sqls.add("delete from pub_user      where id='" + str_userid + "'"); //
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
		String sql = "select id, userdept from pub_user_post where userid = " + userID + " and userdept <> " + deptID;
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
		SeqListDialog dialog_user = new SeqListDialog(this, "��Ա����", billListPanel_User_Post.getTempletVO(), billListPanel_User_Post.getAllBillVOs());
		dialog_user.setVisible(true);
		if (dialog_user.getCloseType() == 1) {// ������ȷ�����أ�����Ҫˢ��һ��ҳ��
			billListPanel_User_Post.queryDataByCondition("deptid='" + str_currdeptid + "'", "seq,usercode"); //
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

		String str_sql_1 = "select deptcode,deptname,postcode,postname,isdefault from v_pub_user_post_1 where userid=" + userID + " order by isdefault desc";
		String str_sql_2 = "select rolecode,rolename from v_pub_user_role_1 where userid=" + userID; //
		Vector v_return = UIUtil.getHashVoArrayReturnVectorByMark(null, new String[] { str_sql_1, str_sql_2 }); //
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
			sb_msg.append("" + (i + 1) + ")" + (hsv_userdepts[i].getStringValue("isdefault") == null ? "" : "[��Ҫ��λ] "));
			sb_msg.append("" + (hsv_userdepts[i].getStringValue("deptname") == null ? "" : hsv_userdepts[i].getStringValue("deptname")) + "--");
			sb_msg.append("" + (hsv_userdepts[i].getStringValue("postname") == null ? "" : hsv_userdepts[i].getStringValue("postname")) + "\r\n"); //			
		}

		sb_msg.append("\r\n\r\n"); //

		sb_msg.append("��" + str_name + "�����н�ɫ��Ϣ:\r\n"); //
		for (int i = 0; i < hsv_userroles.length; i++) {
			// sb_msg.append("" + (i + 1) + ")��ɫ����/����=[" +
			// hsv_userroles[i].getStringValue("rolecode") + "/" +
			// hsv_userroles[i].getStringValue("rolename") + "]\r\n"); //
			sb_msg.append("" + (i + 1) + ")" + hsv_userroles[i].getStringValue("rolename") + "\r\n");
		}
		MessageBox.showTextArea(this, "��" + str_name + "��", sb_msg.toString(), 600, 300);
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

		BillListDialog dialog = new BillListDialog(this, "��ѡ��һ����ɫ", "PUB_ROLE_1", 700, 500); //
		dialog.getBilllistPanel().setItemEditable(false); //
		dialog.getBilllistPanel().QueryDataByCondition(null); //
		dialog.setVisible(true); //
		if (dialog.getCloseType() != 1) {
			return;
		}
		// Ԭ���� 20120917 ��� �����������ӵĹ��� begin
		BillVO[] billVOS = dialog.getReturnBillVOs(); //
		HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, "select userid,roleid,userdept from pub_user_role where userid='" + str_userid + "' and userdept='" + str_currdeptid + "'"); // �Ȼ����userid��userdept��ϲ�ѯ������
		String str_roles = ";";// ���÷ֺ���Ҫ��Ϊ�˲��õ�һ����ӵĺͺ�����ظ����������ж�contains��ʱ����жϲ�����
		for (int j = 0; j < hvs.length; j++) { // �Ȳ�ѯ��֮ǰ�Ѿ����е�roleid������������������û��������roleid
			String old_roleid = hvs[j].getStringValue("roleid");
			str_roles += old_roleid + ";";
		}
		for (int i = 0; i < billVOS.length; i++) {
			String str_roleid = billVOS[i].getStringValue("id"); // ���id
			if (str_roles.contains(";" + str_roleid + ";")) {// ��ʾ����ӵĸ�roleid�Ѿ�������֮ǰ��ӵ�Ȩ��id��;��contains()������indexOf()�������˾��������˵�һ���ַ�ƥ���ʱ�򷵻�ֵΪ0�����/2017-04-28��
				continue;
			} else {
				String str_newpk = UIUtil.getSequenceNextValByDS(null, "S_PUB_USER_ROLE"); //
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
		billListPanel_User_Role.QueryDataByCondition("userid='" + str_userid + "'"); // ˢ���б�
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
		if (JOptionPane.showConfirmDialog(this, "ȷ��Ҫ�ѽ�ɫ��" + rolename + "��ɾ����?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return;
		}
		UIUtil.executeUpdateByDS(null, sql);
		billListPanel_User_Role.removeSelectedRow();
		if (billListPanel_User_Role.getRowCount() > 1) {// ������Ҫ�ж�һ�£������ֻ��һ����ɫ����ɾ�����û�н�ɫ����ѡ���ˡ����/2012-05-24��
			billListPanel_User_Role.getTable().removeRowSelectionInterval(0, billListPanel_User_Role.getRowCount() - 1);
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
	private boolean checkUserDeptPost(String _deptid, String _deptName, String _postid, String _postName, String _userid, String _userName) throws Exception {
		if (_postid == null) {
			HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, "select postname c1 from v_pub_user_post_1 where deptid='" + _deptid + "' and userid='" + _userid + "'");
			if (hvs.length > 0) {
				MessageBox.show(this, "����Ա�Ѵ���,�����ظ�����!"); //
				return false;
			}
		} else {
			HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, "select postname c1 from v_pub_user_post_1 where deptid='" + _deptid + "' and postid='" + _postid + "' and  userid='" + _userid + "'");
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

		HashVO[] hvo = UIUtil.getHashVoArrayByDS(null, "select userid,username,deptid,deptname from v_pub_user_post_1 where userid='" + str_id + "'"); //
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
				itemVO[i] = new ComBoxItemVO(hvo[i].getStringValue("deptid"), null, "" + hvo[i].getStringValue("deptname")); //
			}

			JList list = new JList(itemVO); //
			list.setBackground(Color.WHITE); //
			JScrollPane scrollPane = new JScrollPane(list); //
			scrollPane.setPreferredSize(new Dimension(255, 150)); //
			if (JOptionPane.showConfirmDialog(this, scrollPane, "��" + str_name + "����ְ��[" + hvo.length + "]������,���붨λ����һ��?", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return; //
			}

			ComBoxItemVO selectedItemVO = (ComBoxItemVO) list.getSelectedValue(); //
			str_deptid = selectedItemVO.getId(); //
			str_deptname = selectedItemVO.getName(); //
		}

		if (str_deptid == null || str_deptid.trim().equals("") || str_deptid.trim().equalsIgnoreCase("NULL")) {
			MessageBox.show(this, "��" + str_name + "�������Ļ���IDΪ��!"); //
			return;
		}

		DefaultMutableTreeNode node = billTreePanel_Dept.findNodeByKey(str_deptid); //
		if (node == null) {
			MessageBox.show(this, "��" + str_name + "�����ڻ�����" + str_deptname + "��,��û��Ȩ�޲���!"); //
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
				str_userdepts = UIUtil.getStringArrayByDS(null, "select deptid,deptname from v_pub_user_post_1 where userid=" + str_id); //

			}
			String[] str_thisuserBLDeptIDs = new String[str_userdepts.length]; //
			String str_deptnames = ""; //
			for (int i = 0; i < str_thisuserBLDeptIDs.length; i++) { //
				str_thisuserBLDeptIDs[i] = str_userdepts[i][0]; //
				str_deptnames += str_userdepts[i][1] + ","; //
			}
			str_deptnames = str_deptnames.substring(0, str_deptnames.length() - 1);
			if (!new TBUtil().containTwoArrayCompare(str_allControlDeptIDs, str_thisuserBLDeptIDs)) { // �����¼��Ա���Ƶķ�Χ�����������!����Ȩ�޸�,���򱨴���ʾ!
				MessageBox.show(this, "��" + str_name + "�����ڻ�����" + str_deptnames + "��,��û��Ȩ�޲���!"); //
				return; //
			}
		}

		boolean flag = editUserDialog(billVO.getStringValue("id"));
		if (flag) {
			billListPanel_user_flat.refreshCurrData();
		}
	}

	/**
	 * ����ҳǩ�ı༭��Ա������
	 * 
	 * @param userid
	 *            ��Աid
	 * @return
	 */
	private boolean editUserDialog(String userid) {
		BillCardPanel cardPanel = new BillCardPanel("PUB_USER_CODE1") {
			public boolean checkValidate() {
				boolean flag = super.checkValidate();
				if (!flag) {
					return false;
				}
				String pwdlength = new TBUtil().getSysOptionStringValue("��ʼ����Ա������", "0");
				String pwd = this.getBillVO().getStringValue("pwd");

				// �Ƿ�������븴�����ж�
				String pwdOption = new TBUtil().getSysOptionStringValue("���븴�����ж�", null);

				// ׷�ӹ���Ա��������У����� �����/2013-04-27��
				String pwd_admin = new TBUtil().getSysOptionStringValue("����Ա�������", ""); // admin,10-16,2
				if (!pwd_admin.equals("") && pwd_admin.contains(",")) {
					String[] pwd_admins = pwd_admin.split(",");
					String str_user = this.getBillVO().getStringValue("code");

					if (pwd_admins[0] != null && !pwd_admins[0].equals("") && pwd_admins[0].contains(";")) {
						String[] str_users = pwd_admins[0].split(";");
						for (int i = 0; i < str_users.length; i++) {
							if (str_user.equals(str_users[i])) {
								pwdlength = pwd_admins[1];
								pwdOption = pwd_admins[2];
							}
						}
					} else {
						if (str_user.equals(pwd_admins[0])) {
							pwdlength = pwd_admins[1];
							pwdOption = pwd_admins[2];
						}
					}
				}

				if (pwdlength != null && pwdlength.contains("-")) {
					String[] str_pwdlength = pwdlength.split("-");
					if (pwd.length() < Integer.parseInt(str_pwdlength[0]) || pwd.length() > Integer.parseInt(str_pwdlength[1])) { // 10-16λ֮��
						MessageBox.show(this, "�����������볤����" + pwdlength + "λ֮��!"); //
						return false;
					}

				} else if (pwdlength != null && pwd.length() != Integer.parseInt(pwdlength) && !pwdlength.equals("0")) {
					MessageBox.show(this, "�����������볤��Ϊ" + pwdlength + "λ!"); //
					return false;
				}

				// �Ƿ�������븴�����ж�
				if (pwdOption != null) {
					HashMap parmap = new HashMap();
					parmap.put("pwd", pwd);
					String isPass = "";
					String msg = "";
					if (pwdOption.contains(".")) {
						HashMap hashmap = new TBUtil().reflectCallCommMethod(pwdOption, parmap);
						isPass = hashmap.get("isPass").toString();
						msg = hashmap.get("msg") != null ? hashmap.get("msg").toString() : null;
					} else { // ����ϵͳĬ�ϵĻ���
						int level = 0;
						try {
							level = Integer.parseInt(pwdOption);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						boolean flag_ = new TBUtil().checkPasswordComplex(pwd, level);
						if (flag_) {
							return true;
						} else {
							MessageBox.show(this, "��������ɴ�д��ĸ��Сд��ĸ�����֡������ַ��е�" + level + "�����!");
							return false;
						}
					}
					if (isPass.equals("0")) {
						if (msg == null || msg.equals("")) {
							msg = "�����������ĸ�����֡������ַ��е�2�����!";
						}
						MessageBox.show(this, msg);
						return false;
					}
				}

				return true;
			}

		};
		cardPanel.queryDataByCondition(" id = " + userid);
		cardPanel.setEditableByEditInit();
		String pwdlength = new TBUtil().getSysOptionStringValue("��ʼ����Ա������", "0");
		if (pwdlength != null && !pwdlength.equals("") && !pwdlength.equals("0")) {
			cardPanel.getCompentByKey("PWD").getLabel().setText("*����(" + pwdlength + "λ)");
		}
		BillCardDialog dialog = new BillCardDialog(this, "��Ա��Ϣ�޸�", cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.setVisible(true);
		if (dialog.getCloseType() != 1) {
			return false;
		}
		return true;
	}
}
