package cn.com.infostrategy.ui.workflow.engine;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.Box;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;

/**
 * @author sfj
 * ����Ҫ��һ������Ȩ�������˻���Ȼ���ٸ��ݻ����ͽ�ɫ�����˳���Ա
 * ��ʼ��������չ�������͵������ʴ��Ľ�ɫ�Ѿ�������ҵ��ͷ������Բ�����
 * �ʴ�ϣ�����п�����
 * һ�ֿ���һ�ֺ�����
 * ���ֺñ����ֺ�һ�ֲ��ź�����
 * ������
 * �������¼������ֵ�
 * ���������Ȩ���������ٺ��ʲ���
 * �о����ǰ���ɫ��ѡ��
 */
public class WorkFlowSuperChooseUserDialog extends BillDialog implements ActionListener {
	private WLTButton btn_confirm, btn_cancel, btn_search;
	private BillListPanel userList = null;
	private BillTreePanel corpTypeTree, roleTree, appendCorpTypeTree = null;
	private String corpsql = null;
	private BillVO[] vos = null;
	public WorkFlowSuperChooseUserDialog(Container _parent) {
		super(_parent, "�߼�����", 900, 600);
		this.initialize();
	}

	private void initialize() {
		this.setLayout(new BorderLayout());
		this.add(getMainPanel(), BorderLayout.CENTER);
		this.add(getSourthPanel(), BorderLayout.SOUTH);
	}

	/**
	 * �����
	 * @return
	 */
	private JPanel getMainPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new BorderLayout());
		try {
			Box mainbox = Box.createHorizontalBox();
			userList = new BillListPanel(new ServerTMODefine("/cn/com/infostrategy/bs/sysapp/install/templetdata/pub_user_1.xml"));
			userList.setRowNumberChecked(true);
			corpTypeTree = new BillTreePanel(new DefaultTMO("��������", new String[][]{{"��������", "100"}}));
			corpTypeTree.reSetTreeChecked(true);
			corpTypeTree.getTempletVO().setTreeviewfield("��������");
			corpTypeTree.queryData(" select name �������� from pub_comboboxdict where type in ('��������','��������') and id not like '$%' order by seq ");
			corpTypeTree.getBillTreeBtnPanel().setVisible(false);
			corpTypeTree.getQuickLocatePanel().setVisible(false);
			roleTree = new BillTreePanel(new DefaultTMO("��ɫ", new String[][]{{"��ɫ", "100"}}));
			roleTree.reSetTreeChecked(true);
			roleTree.getTempletVO().setTreeviewfield("��ɫ");
			roleTree.queryData(" select name ��ɫ from pub_role order by name desc ");
			roleTree.getBillTreeBtnPanel().setVisible(false);
			roleTree.getQuickLocatePanel().setVisible(false);
			btn_search = WLTButton.createButtonByType(WLTButton.COMM, "����");
			btn_search.addActionListener(this);
			WLTSplitPane wlsp = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, corpTypeTree, roleTree);
			wlsp.setDividerLocation(200);
			Box btn = Box.createHorizontalBox();
			btn.add(btn_search);
			btn.add(userList);
			boolean isshowcorptype = TBUtil.getTBUtil().getSysOptionBooleanValue("�������߼������Ƿ���ʾ������������", false);
			if (isshowcorptype) {
				WLTSplitPane wlsp2 = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, wlsp, btn);
				panel.add(wlsp2, BorderLayout.CENTER);
			} else {
				WLTSplitPane wlsp2 = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, roleTree, btn);
				panel.add(wlsp2, BorderLayout.CENTER);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return panel;
	}
 
	private JPanel getSourthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout());
		btn_confirm = new WLTButton("ȷ��");
		btn_cancel = new WLTButton("ȡ��");
		btn_cancel.addActionListener(this);
		btn_confirm.addActionListener(this);
		panel.add(btn_confirm);
		panel.add(btn_cancel);
		return panel;
	}

	public void actionPerformed(ActionEvent _event) {
		if (_event.getSource() == btn_confirm) {
			onConfirm();
		} else if (_event.getSource() == btn_cancel) {
			onCancel();
		} else if (_event.getSource() == btn_search) {
			onSearch();
		}
	}
	
	public void onSearch() {
		BillVO[] corpTypeVos= corpTypeTree.getCheckedVOs();
//		if (corpTypeVos == null || corpTypeVos.length < 1 || (corpTypeVos.length == 1 && corpTypeVos[0] == null)) {
//			MessageBox.show(btn_search, "��ѡ��������ͽ��й���!");
//			return;
//		}
		BillVO[] roleVos= roleTree.getCheckedVOs();
		if (roleVos == null || roleVos.length < 1 || (roleVos.length == 1 && roleVos[0] == null)) {
			MessageBox.show(btn_search, "��ѡ���ɫ���й���!");
			return;
		}
		
		List corptype = new ArrayList();
		List role = new ArrayList();
		if (corpTypeVos != null) {
			for (int i = 0 ; i < corpTypeVos.length; i ++ ) {
				if (corpTypeVos[i] != null) {
					corptype.add(corpTypeVos[i].toString());
				}
			}
		}
		
		if (roleVos != null) {
			for (int i = 0 ; i < roleVos.length; i ++ ) {
				if (roleVos[i] != null) {
					role.add(roleVos[i].toString());
				}
			}
		}
		
		try {
			HashMap parMap = new HashMap();
			parMap.put("corptype", corptype);
			parMap.put("role", role);
			HashMap returnMap = UIUtil.commMethod("cn.com.infostrategy.bs.workflow.WorkFlowBSUtil", "getUserCorpAndUsersBycorpTypeAndRole", parMap);
			HashVO[] hvs = (HashVO[]) returnMap.get("users");
			putUserDataByHashVO(hvs);
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}
	
	private void putUserDataByHashVO(HashVO[] _hvs) {
		userList.clearTable(); //������ұ߱��!!
		if (_hvs != null && _hvs.length > 0) {
			for (int i = 0; i < _hvs.length; i++) {
				int _newRow = userList.newRow(false); //
				userList.setValueAt(new StringItemVO(_hvs[i].getStringValue("userid")), _newRow, "userid"); //
				userList.setValueAt(new StringItemVO(_hvs[i].getStringValue("usercode")), _newRow, "usercode"); //
				userList.setValueAt(new StringItemVO(_hvs[i].getStringValue("username")), _newRow, "username"); //
				userList.setValueAt(new StringItemVO(_hvs[i].getStringValue("userroleid")), _newRow, "userroleid"); //
				userList.setValueAt(new StringItemVO(_hvs[i].getStringValue("userrolename")), _newRow, "userrolename"); //
				userList.setValueAt(new StringItemVO(_hvs[i].getStringValue("userdept")), _newRow, "userdept"); //����id
				userList.setValueAt(new StringItemVO(_hvs[i].getStringValue("userdeptname")), _newRow, "userdeptname"); //��������
				userList.setValueAt(new StringItemVO(_hvs[i].getStringValue("accruserid")), _newRow, "accruserid"); //��Ȩ��id
				userList.setValueAt(new StringItemVO(_hvs[i].getStringValue("accrusercode")), _newRow, "accrusercode"); //��Ȩ�˱���
				userList.setValueAt(new StringItemVO(_hvs[i].getStringValue("accrusername")), _newRow, "accrusername"); //��Ȩ������!!

			}
			userList.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT); //
		}
	}
	
	public void onConfirm() {
		BillVO[] vos = userList.getCheckedBillVOs();
		if (vos == null || vos.length < 1) {
			MessageBox.show(userList, "�빴ѡ��Ҫѡ�����Ա!");
			return;
		}
		this.vos = vos;
		closeType = 1;
		this.dispose();
	}

	public void onCancel() {
		closeType = 2;
		this.dispose();
	}

	public int getCloseType() {
		return closeType;
	}

	public BillVO[] getVos() {
		return vos;
	}
	
}
