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
 * Ⱥ��ά������
 * @author sfj
 *
 */
public class AddNewGroupDialog extends BillDialog implements ActionListener, BillTreeSelectListener {
	private WLTButton btn_confirm, btn_cancel;
	private JTextField jt_name = null;
	private BillTreePanel corptree = null;//������
	private BillListPanel billList_user = null;//ѡ���������˵��б�
	private BillListPanel billList_user_shopcar = null;//���ﳵ�б�
	private WLTButton btn_addShopCarUser, btn_addAllShopCarUser, btn_delShopCarUser, btn_delAllShopCarUser;
	private String editType = null;//�༭״̬
	private BillVO groupvo = null;
	private int limit = 100;//Ⱥ�������������ƣ��������Ҫ���Ը��ƽ̨����

	public AddNewGroupDialog(Container _parent, BillVO _groupvo, String _editType) {
		super(_parent, "Ⱥ��", 900, 600);
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
	 * �����
	 * @return
	 */
	private JPanel getMainPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new BorderLayout());
		try {
			corptree = new BillTreePanel(new ServerTMODefine("/cn/com/infostrategy/bs/sysapp/install/templetdata/PUB_CORP_DEPT_1.xml"));
			billList_user = new BillListPanel(new ServerTMODefine("/cn/com/infostrategy/bs/sysapp/install/templetdata/pub_user_1.xml"));
			billList_user_shopcar = new BillListPanel(billList_user.getTempletVO().deepClone());
			billList_user_shopcar.setTitleLabelText("Ⱥ����Ա");
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
				HashVO[] hvs_mycorp = UIUtil.getParentCorpVOByMacro(1, null, "$������");
				String str_zhid = null;
				if (TBUtil.getTBUtil().getSysOptionBooleanValue("�������ύʱ��ӽ������Ƿ���Զ��������", false)) {
					str_zhid = UIUtil.getStringValueByDS(null, "select id from pub_corp_dept where corptype='����'");
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
				btn_addShopCarUser = new WLTButton("���", "office_059.gif");
				btn_delShopCarUser = new WLTButton("ɾ��", "office_081.gif");
				btn_addAllShopCarUser = new WLTButton("ȫ�����", "office_160.gif");
				btn_delAllShopCarUser = new WLTButton("ȫ��ɾ��", "office_125.gif");
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
				//���ܽ����ɷָ����/sunfujun/20121115
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
	 * ����һ��Ⱥ������
	 * @return
	 */
	private JPanel getNorthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel jl = new WLTLabel("Ⱥ������:");
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
		btn_confirm = new WLTButton("ȷ��");
		btn_cancel = new WLTButton("ȡ��");
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
	 * ��Ӱ�ť�߼�
	 */
	private void onAddShopCarUser() {
		BillVO[] billVOs = billList_user.getSelectedBillVOs();
		if (billVOs == null || billVOs.length <= 0) {
			MessageBox.show(this, "����Ϸ���Ա�б���ѡ��һ��������Ա���д˲���!");
			return;
		}
		onAddShopCarUserByBillVOs(billVOs);
	}

	/**
	 * ȫ����Ӱ�ť���߼�
	 */
	private void onAddAllShopCarUser() {
		BillVO[] billVOs = billList_user.getAllBillVOs();
		if (billVOs == null || billVOs.length <= 0) {
			MessageBox.show(this, "û�п�����ӵ���Ա,�޷����д˲���!");
			return;
		}
		onAddShopCarUserByBillVOs(billVOs);
	}

	/**
	 * ��Ⱥ���������Ա���߼�����Ӱ�ť��ȫ����Ӱ�ť��Ҫ���ô��߼�
	 * ��Ҫ�ж��Ƿ��ظ����Լ��Ƿ񳬹����������QQһ��
	 * @param _billVOs
	 */
	private void onAddShopCarUserByBillVOs(BillVO[] _billVOs) {
		HashSet hst = new HashSet();
		BillVO[] billVOs_shopCardUser = billList_user_shopcar.getAllBillVOs();
		if (billVOs_shopCardUser != null && billVOs_shopCardUser.length > 0) {
			if (billVOs_shopCardUser.length >= limit) {
				MessageBox.show(this, "Ⱥ���Ѵ��������!");
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
				sb_reduplicate_user.append("��" + str_usercode + "/" + str_username + "��");
			} else {
				canadd.add(_billVOs[i]);
			}
		}
		//		String str_reduplicate_user = sb_reduplicate_user.toString();
		//		if (!str_reduplicate_user.equals("")) {
		//			MessageBox.show(this, "�û�" + str_reduplicate_user + "�Ѿ���ӹ���,�����ظ����!");
		////			return;
		//		}
		//�ظ��˲��ӽ����ͺ��˿��Բ�����ʾ��Ҫ��ѡ����̫��/sunfujun/20121115
		if (billVOs_shopCardUser.length + canadd.size() > limit) {//���������������ȫ�����룬���������ȥ��
			MessageBox.show(this, "Ⱥ���Ѵ��������!�޷�ȫ�����!");
			List canadd_limit = new ArrayList();
			for (int i = 0; i < limit - billVOs_shopCardUser.length; i++) {
				canadd_limit.add(canadd.get(i));
			}
			billList_user_shopcar.addBillVOs((BillVO[]) canadd_limit.toArray(new BillVO[0]));
		} else {//���������û����������ƾ�ֱ�Ӽӽ���
			billList_user_shopcar.addBillVOs((BillVO[]) canadd.toArray(new BillVO[0]));
		}
	}

	/**
	 * �Ƴ�Ⱥ����ѡ�е���Ա
	 */
	private void onDelShopCarUser() {
		BillVO[] billVOs = billList_user_shopcar.getSelectedBillVOs();
		if (billVOs == null || billVOs.length <= 0) {
			MessageBox.show(this, "���Ⱥ����Ա�б���ѡ��һ��������Ա���д˲���!");
			return;
		}
		if (!MessageBox.confirm(this, "��ȷ��Ҫɾ��ѡ�е���Ա��?")) {
			return;
		}
		billList_user_shopcar.removeSelectedRows();
	}

	/**
	 * �Ƴ�Ⱥ���е�������Ա
	 */
	private void onDelAllShopCarUser() {
		int li_count = billList_user_shopcar.getRowCount();
		if (li_count <= 0) {
			MessageBox.show(this, "Ⱥ����ԱΪ��,�޷����д˲���!");
			return;
		}
		if (!MessageBox.confirm(this, "��ȷ��Ҫɾ��Ⱥ����������Ա��?")) {
			return;
		}
		billList_user_shopcar.clearTable();
	}

	/**
	 * ȷ��
	 */
	public void onConfirm() {
		try {
			if (jt_name.getText() == null || "".equals(jt_name.getText().trim())) {
				MessageBox.show(this, "Ⱥ�����Ʋ���Ϊ��!");
				jt_name.grabFocus();
				return;
			}

			BillVO[] vos = billList_user_shopcar.getAllBillVOs();
			if (vos == null || vos.length <= 0) {
				MessageBox.show(this, "�������Ա!");
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
	 * ��������ѡ���¼�
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
	 * ����Ⱥ��ѡ�˵�ʱ�򰴲��Ų��˵�ʱ����˸о�����Ҫ������Ȩ����Ա
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
	 * ��hashvo���鸳ֵ���б�
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
