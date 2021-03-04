package cn.com.infostrategy.ui.workflow.engine;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;

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
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

/**
 * ��������ѡ�����ʱ��,�����ֹ�����µ���Ա!! �����������µĲ�����!!
 * ������ܷǳ���Ҫ,��Ϊ�Ժ���������̾Ϳ������ʵ��!! ��ǰ���Ƕ���Լ������,����������������������Ҳ�����Ӧ�Ĳ�����!!Ȼ�������ʵʩ�����޴����ս!!
 * ʵ����,ֻ��������������̵Ĺ��ܺ�,�󲿷����̶�����ҪԼ����������,���ǵĿ�����ʵʩ����������Ϊ����!! �ټ�����ѭ������,���ܼ����̻���!! �Ӷ�ʹ������Ӧ�ñ��Խ��Խ����!!
 * ����˵,��������,��ѭ��,�ټ��ϣ��˹�ѡ����������(����ҵ������),ʡȥ����������ҵ������ά��,ֱ����ģ���з�������,������ʾ�����ĸ��ֶ�,��ҳ��������Ľ�׳,�ȵ���Щ���ܶ�����Ϊ�����̿�����ʵʩ!  
 * @author Administrator
 *
 */
public class AddNewUserDialog extends BillDialog implements BillTreeSelectListener, ActionListener, ChangeListener {

	private static final long serialVersionUID = -2457287774661601679L;
	private BillTreePanel billTree_corp = null; //������!
	private BillListPanel billList_user = null; //��Ա�б�,��Ҫ�ֶ���,userid,username,��Ա��ɫid,��Ա��ɫ����,��ʾ�ľ�����
	private BillListPanel billList_user_shopcar = null; //���ﳵ�е���Ա!!
	private WLTButton btn_addShopCarUser, btn_addAllShopCarUser, btn_delShopCarUser, btn_delAllShopCarUser, btn_SuperSearch; //���ӹ��ﳵ��Ա,ɾ�����ﳵ��Ա!
	private WLTTabbedPane tab = null;
	private BillVO billVO = null; //ҵ�񵥾��ϵ�BillVO����!!
	private WLTButton btn_confirm, btn_cancel; //
	private int closeType = -1; //
	private ChooseUserByGroupPanel cubgp = null;
	private BillVO returnCorpVO = null; //���صĻ���VO
	private BillVO[] returnUserVOs = null; //
	private boolean ishavegroup = false;

	boolean isShopCarUI = false; //�Ƿ��ǹ��ﳵ�Ľ�����!! ��Ϊ�Ͼ����˿ͻ�����Ҫ��������OAһ��ʹ��"���ﳵ"���һ�������������Ա!!

	//������Ա!!
	public AddNewUserDialog(Container _parent, BillVO _billVO) {
		super(_parent, 900, 500); //
		this.billVO = _billVO; //
		isShopCarUI = new TBUtil().getSysOptionBooleanValue("�����������Ա�Ƿ��ﳵ���", false); //��ϵͳ����ȡ!!
		this.initialize(); //
		if (isShopCarUI) { //����ǹ��ﳵ������,��߶�Ҫ����!!
			this.setSize(910, 650); //
			this.locationToCenterPosition(); //�������!
		}

	}

	/**
	 * ��ʼ������!!!
	 * ����Ǹ�������,�ұ��Ǹ���Ա�嵥(������ɫid,��ɫ����,��ɫ����!)
	 */
	private void initialize() {
		try {
			billList_user = new BillListPanel(new ServerTMODefine("/cn/com/infostrategy/bs/sysapp/install/templetdata/pub_user_1.xml")); //��Ա�б�,�Զ�������ɫ!
			billTree_corp = new BillTreePanel(new ServerTMODefine("/cn/com/infostrategy/bs/sysapp/install/templetdata/PUB_CORP_DEPT_1.xml")); //����������
			if (TBUtil.getTBUtil().getSysOptionBooleanValue("�������ύʱ��ӽ������Ƿ���ʾ�߼����Ұ�ť", true)) {
				btn_SuperSearch = WLTButton.createButtonByType(WLTButton.COMM, "�߼�����");
				btn_SuperSearch.addActionListener(this);
				billTree_corp.addBillTreeButton(btn_SuperSearch);
				billTree_corp.repaintBillTreeButton();
			}

			HashVO[] hvs_mycorp = UIUtil.getParentCorpVOByMacro(1, null, "$������"); //�ҵ������� Ĭ��������Ȼ������������һ��������ѯ�İ�ť���Ը��ݻ������͡���չ���͡���ɫ�������ܵķ�Χ������Ȩ��
			String str_zhid = null; //��ʱ��Ҫ��Զ�����д�������
			boolean isq = false;
			if (TBUtil.getTBUtil().getSysOptionBooleanValue("�������ύʱ��ӽ����߻����Ƿ�������Ȩ�޲���", false)) {
				try {
					HashMap _parMap = new HashMap();
					_parMap.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
					_parMap.put("datapolicy", "������ѡ�˻�������Ȩ��");
					_parMap.put("returnCol", "id");
					HashMap corpmap = UIUtil.commMethod("cn.com.infostrategy.bs.sysapp.DataPolicyDMO", "getDataPolicyTargetCorpsByUserId", _parMap);
					String[] ids = (String[]) corpmap.get("AllCorpIDs");
					if (ids != null && ids.length > 0) {
						billTree_corp.queryDataByDS(null, "select id,code,name,parentid,seq from pub_corp_dept where id in (" + TBUtil.getTBUtil().getInCondition(ids) + ")");
						isq = true;
					}
				} catch (Exception ee) {
					ee.printStackTrace();
					isq = false;
				}
			}
			if (!isq) {
				if (TBUtil.getTBUtil().getSysOptionBooleanValue("�������ύʱ��ӽ������Ƿ���Զ��������", false)) { //
					str_zhid = UIUtil.getStringValueByDS(null, "select id from pub_corp_dept where corptype='����'"); //����ID
				}
				if (hvs_mycorp != null) { //����ҵ�������!!
					String str_myParentCorpId = hvs_mycorp[0].getStringValue("id"); //
					String str_condition = null; //���ι�������������
					if (str_zhid == null) { //�����������!!
						str_condition = "id = " + str_myParentCorpId + ""; //
					} else { //���������!!
						str_condition = "id in (" + str_myParentCorpId + "," + str_zhid + ")"; //
					}
					HashVO[] hvs_myAllChildrens = UIUtil.getHashVoArrayAsTreeStructByDS(null, "select id,code,name,parentid,seq from pub_corp_dept", "id", "parentid", "seq", str_condition); //
					billTree_corp.queryDataByDS(hvs_myAllChildrens, -1); //
				} else { //����Ҳ���������!
					billTree_corp.queryDataByDS(null, "select id,code,name,parentid,seq from pub_corp_dept"); //��ѯ���л���������������������һ�£���	
				}
			}
			String str_billType = null; //
			String str_busiType = null; //
			HashMap parMap = new HashMap(); //
			str_billType = billVO.getStringValue("BILLTYPE"); //��������!
			str_busiType = billVO.getStringValue("BUSITYPE"); //ҵ������!
			parMap.put("billtype", str_billType); //��������
			parMap.put("busitype", str_busiType); //ҵ������
			HashMap returnMap = UIUtil.commMethod("cn.com.infostrategy.bs.workflow.WorkFlowBSUtil", "getUserCorpAndUsers", parMap); //ȥ��������ȡ�õ�¼��Ա�Ļ���,�Լ��û����µ�����������Ա!!������Ȩ��ǰ�и�Bug,��ǰû�и��ݵ���������ҵ���������¼���һ������ģʽ,����������ύ��������Ȩ����,���ڵ��"������Ա"ʱȴû��!!
			if (returnMap != null) { //�����ֵ
				String str_loginUserDeptid = (String) returnMap.get("userCorp"); //ȡ�û���!
				if (str_loginUserDeptid != null) { //�����¼��Ա������������Ϊ��,���Զ���������ǰ��Ա�����ڻ���,ͬʱ������ѯ���ұߵ���Ա!!
					DefaultMutableTreeNode currUserNode = billTree_corp.findNodeByKey(str_loginUserDeptid); //
					if (currUserNode != null) {
						billTree_corp.scrollToOneNode(currUserNode); //
					}
				}
				HashVO[] hvsUser = (HashVO[]) returnMap.get("sameCorpUsers"); //��������Զ�̷���ȡ�õ���Ա�б�
				if (hvsUser != null && hvsUser.length > 0) { //���������,���������!!!
					putUserDataByHashVO(hvsUser); //����Ա����
				}
			}
			billTree_corp.addBillTreeSelectListener(this); //����������ѡ��仯�¼�!!!
			ishavegroup = TBUtil.getTBUtil().getSysOptionBooleanValue("�����������Ա�Ƿ�Ⱥ��ѡ��", true);
			if (!isShopCarUI) { //������ǹ��ﳵ���
				WLTSplitPane splitPanel = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTree_corp, billList_user); //�ָ����ǻ���,�ұ�����Ա!! �Ͼ����˿ͻ�����Ҫ��ý���Ҫ���"���ﳵ"������!!
				splitPanel.setDividerLocation(250); //
				if (ishavegroup) {
					tab = new WLTTabbedPane();
					tab.addTab("������ѡ��", UIUtil.getImage("office_014.gif"), splitPanel);
					tab.addTab("��Ⱥ��ѡ��", UIUtil.getImage("user.gif"), new JPanel());
					tab.addChangeListener(this);
					this.getContentPane().add(tab, BorderLayout.CENTER);
				} else {
					this.getContentPane().add(splitPanel, BorderLayout.CENTER);
				}
			} else { //����ǹ��ﳵ���,����������!!!
				billList_user_shopcar = new BillListPanel(billList_user.getTempletVO().deepClone()); //ֱ��ʹ����Ա���ģ�崴��,��������һ��Զ�̷���!!
				billList_user_shopcar.setTitleLabelText("��ѡ�����Ա"); //
				btn_addShopCarUser = new WLTButton("���", "office_059.gif"); //
				btn_delShopCarUser = new WLTButton("ɾ��", "office_081.gif"); //
				btn_addAllShopCarUser = new WLTButton("ȫ�����", "office_160.gif"); //
				btn_delAllShopCarUser = new WLTButton("ȫ��ɾ��", "office_125.gif"); //
				btn_addShopCarUser.setToolTipText("���Ϸ�ѡ�е���Ա��ӽ���"); //
				btn_delShopCarUser.setToolTipText("���·���ѡ�����Աɾ����"); //
				btn_addShopCarUser.addActionListener(this); //
				btn_delShopCarUser.addActionListener(this); //
				btn_addAllShopCarUser.addActionListener(this); //ȫ�����!
				btn_delAllShopCarUser.addActionListener(this); //ȫ��ɾ��!
				billList_user_shopcar.addBatchBillListButton(new WLTButton[] { btn_addShopCarUser, btn_delShopCarUser, btn_addAllShopCarUser, btn_delAllShopCarUser }); //
				billList_user_shopcar.repaintBillListButton(); //ˢ�°�ť!!!
				WLTSplitPane splitPanel_2 = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, billList_user, billList_user_shopcar); //���·ָ�,������ĳ����������Ա,�����ǹ��ﳵ�е���Ա!!
				splitPanel_2.setDividerLocation(365); //
				WLTSplitPane splitPanel = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTree_corp, splitPanel_2); //�ָ����ǻ���,�ұ�����Ա!! �Ͼ����˿ͻ�����Ҫ��ý���Ҫ���"���ﳵ"������!!
				splitPanel.setDividerLocation(250); //
				if (ishavegroup) {
					tab = new WLTTabbedPane();
					tab.addTab("������ѡ��", UIUtil.getImage("office_014.gif"), splitPanel);
					tab.addTab("��Ⱥ��ѡ��", UIUtil.getImage("user.gif"), new JPanel());
					tab.addChangeListener(this);
					this.getContentPane().add(tab, BorderLayout.CENTER);
				} else {
					this.getContentPane().add(splitPanel, BorderLayout.CENTER);
				}
			}

			this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); ////
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.CENTER)); //
		btn_confirm = new WLTButton("ȷ��"); //
		btn_cancel = new WLTButton("ȡ��"); //

		btn_confirm.addActionListener(this); //
		btn_cancel.addActionListener(this); //
		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel; //
	}

	/**
	 * ������ѡ��仯
	 */
	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		if (billList_user.getRowCount() > 0) {
			billList_user.clearTable(); //������ұ߱��!!
		}
		if (_event.getSource() == billTree_corp) {
			BillVO billVO = _event.getCurrSelectedVO(); //
			if (billVO != null) {
				String str_corpId = billVO.getStringValue("id"); //����id
				HashVO[] hvs = queryUserByCorpId(str_corpId); // 
				putUserDataByHashVO(hvs); //��������!!!
			}
		}
	}

	/**
	 * ���ݻ���idˢ����Ա�嵥!!
	 */
	private HashVO[] queryUserByCorpId(String _corpId) {
		try {
			HashMap parMap = new HashMap(); //
			parMap.put("corpid", _corpId); //
			String str_billType = billVO.getStringValue("BILLTYPE"); //��������!
			String str_busiType = billVO.getStringValue("BUSITYPE"); //ҵ������!
			parMap.put("billtype", str_billType); //��������
			parMap.put("busitype", str_busiType); //ҵ������
			HashMap returnMap = UIUtil.commMethod("cn.com.infostrategy.bs.workflow.WorkFlowBSUtil", "getUserCorpAndUsersBycorpId", parMap);
			HashVO[] hvs = (HashVO[]) returnMap.get("sameCorpUsers"); //����Զ�̵���,�ҵ�ĳ�������µ�������Ա(������ɫ)
			return hvs; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		}
	}

	//���ݲ�ѯ������,���뵽ҳ����!!!
	private void putUserDataByHashVO(HashVO[] _hvs) {
		billList_user.clearTable(); //������ұ߱��!!
		if (_hvs != null && _hvs.length > 0) {
			for (int i = 0; i < _hvs.length; i++) {
				int _newRow = billList_user.newRow(false); //
				billList_user.setValueAt(new StringItemVO(_hvs[i].getStringValue("userid")), _newRow, "userid"); //
				billList_user.setValueAt(new StringItemVO(_hvs[i].getStringValue("usercode")), _newRow, "usercode"); //
				billList_user.setValueAt(new StringItemVO(_hvs[i].getStringValue("username")), _newRow, "username"); //
				billList_user.setValueAt(new StringItemVO(_hvs[i].getStringValue("userroleid")), _newRow, "userroleid"); //
				billList_user.setValueAt(new StringItemVO(_hvs[i].getStringValue("userrolename")), _newRow, "userrolename"); //
				billList_user.setValueAt(new StringItemVO(_hvs[i].getStringValue("userdept")), _newRow, "userdept"); //����id
				billList_user.setValueAt(new StringItemVO(_hvs[i].getStringValue("userdeptname")), _newRow, "userdeptname"); //��������

				billList_user.setValueAt(new StringItemVO(_hvs[i].getStringValue("accruserid")), _newRow, "accruserid"); //��Ȩ��id
				billList_user.setValueAt(new StringItemVO(_hvs[i].getStringValue("accrusercode")), _newRow, "accrusercode"); //��Ȩ�˱���
				billList_user.setValueAt(new StringItemVO(_hvs[i].getStringValue("accrusername")), _newRow, "accrusername"); //��Ȩ������!!

			}
			billList_user.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT); //
		}
	}

	public void actionPerformed(ActionEvent _event) {
		if (_event.getSource() == btn_confirm) {
			onConfirm(); //
		} else if (_event.getSource() == btn_cancel) {
			onCancel(); //
		} else if (_event.getSource() == btn_addShopCarUser) { //�����ﳵ�����Ա!!
			onAddShopCarUser(); //
		} else if (_event.getSource() == btn_delShopCarUser) { //�ӹ��ﳵ�Ƴ���Ա!!
			onDelShopCarUser(); //
		} else if (_event.getSource() == btn_addAllShopCarUser) { //ȫ�����
			onAddAllShopCarUser(); //
		} else if (_event.getSource() == btn_delAllShopCarUser) { //ȫ��ɾ��
			onDelAllShopCarUser(); //
		} else if (_event.getSource() == btn_SuperSearch) {
			onSuperSearch();
		}
	}

	/**
	 * ȷ��!!
	 */
	private void onConfirm() {

		if (ishavegroup && tab.getSelectedIndex() == 1) {
			if (cubgp.check(this)) {
				returnUserVOs = cubgp.getRtnVOS();
				closeType = 1;
				this.dispose();
			}
		} else {
			BillVO[] selVOS = null; //ѡ�е���Ա!!!!
			if (!isShopCarUI) { //������ǹ��ﳵ���!!
				selVOS = billList_user.getSelectedBillVOs(); //��ӵ�һ����Ա����ȡ
			} else { //����ǹ��ﳵ���!!
				selVOS = billList_user_shopcar.getAllBillVOs(); //��ӵڶ�����Ա����ȡ!!!
			}
			if (selVOS == null || selVOS.length == 0) {
				MessageBox.show(this, "��ѡ��һ����Ա!"); //
				return; //
			}

			HashSet hst_allaccrUser = new HashSet(); //
			for (int i = 0; i < billList_user.getRowCount(); i++) {
				String str_accruser = billList_user.getRealValueAtModel(i, "accruserid"); //
				if (str_accruser != null) {
					hst_allaccrUser.add(str_accruser); //
				}
			}

			StringBuilder sb_allAccrUserNames = new StringBuilder(); //
			for (int i = 0; i < selVOS.length; i++) {
				String str_userid = selVOS[i].getStringValue("userid"); //
				String str_userName = selVOS[i].getStringValue("username"); //
				String str_accruserid = selVOS[i].getStringValue("accruserid"); //��Ȩ��
				if (hst_allaccrUser.contains(str_userid) || hst_allaccrUser.contains(str_accruserid)) { //
					sb_allAccrUserNames.append("��" + str_userName + "��"); //
				}
			}
			if (!sb_allAccrUserNames.toString().equals("")) { //��������ñ���Ȩ��Ҳ�ܽ���,����ѡ��***(����Ȩ***)���ӵ���Ա
				if (JOptionPane.showConfirmDialog(this, "��ѡ��������û�����Ȩ����:" + sb_allAccrUserNames.toString() + "\r\n��ȷ���ύ�Ƿ���ȷ?\r\n", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
					return;
				}
			}

			returnCorpVO = billTree_corp.getSelectedVO(); //����������!
			returnUserVOs = selVOS; //ѡ�е���Ա!!
			closeType = 1; //
			this.dispose(); //
		}
	}

	/**
	 * ȡ��
	 */
	private void onCancel() {
		returnCorpVO = null; //���صĻ���VO
		returnUserVOs = null; //���ص���ԱVO
		closeType = 2; //
		this.dispose(); //
	}

	/**
	 * �����ﳵ�����Ա!!
	 */
	private void onAddShopCarUser() {
		BillVO[] billVOs = billList_user.getSelectedBillVOs(); //
		if (billVOs == null || billVOs.length <= 0) {
			MessageBox.show(this, "����Ϸ���Ա�б���ѡ��һ��������Ա���д˲���!"); //
			return; //
		}
		onAddShopCarUserByBillVOs(billVOs); //
	}

	/**
	 * ȫ�����
	 */
	private void onAddAllShopCarUser() {
		BillVO[] billVOs = billList_user.getAllBillVOs(); //
		if (billVOs == null || billVOs.length <= 0) {
			MessageBox.show(this, "û�п�����ӵ���Ա,�޷����д˲���!"); //
			return; //
		}
		onAddShopCarUserByBillVOs(billVOs); //
	}

	/**
	 * �����ﳵ�����Ա!!
	 */
	private void onAddShopCarUserByBillVOs(BillVO[] _billVOs) {
		//���Ѽ������Աͳ��һ��,��ֹ�ظ�����!!
		HashSet hst = new HashSet(); //����Ѿ������,�򲻱��ظ�����!!
		BillVO[] billVOs_shopCardUser = billList_user_shopcar.getAllBillVOs(); //
		if (billVOs_shopCardUser != null && billVOs_shopCardUser.length > 0) { //userid
			for (int i = 0; i < billVOs_shopCardUser.length; i++) {
				String str_userid = billVOs_shopCardUser[i].getStringValue("userid"); //
				if (str_userid != null) {
					hst.add(str_userid); //��Աid
				}
			}
		}
		StringBuilder sb_reduplicate_user = new StringBuilder(); //
		for (int i = 0; i < _billVOs.length; i++) {
			String str_userid = _billVOs[i].getStringValue("userid"); //
			String str_usercode = _billVOs[i].getStringValue("usercode"); //��Ա����!!
			String str_username = _billVOs[i].getStringValue("username"); //��Ա����!!
			if (hst.contains(str_userid)) { //
				sb_reduplicate_user.append("��" + str_usercode + "/" + str_username + "��"); //
			}
		}
		String str_reduplicate_user = sb_reduplicate_user.toString(); //
		if (!str_reduplicate_user.equals("")) {
			MessageBox.show(this, "�û�" + str_reduplicate_user + "�Ѿ���ӹ���,�����ظ����,������ѡ��!"); //
			return; //
		}
		billList_user_shopcar.addBillVOs(_billVOs); //
	}

	/**
	 * �ӹ��ﳵ�Ƴ���Ա!!
	 */
	private void onDelShopCarUser() {
		BillVO[] billVOs = billList_user_shopcar.getSelectedBillVOs(); //
		if (billVOs == null || billVOs.length <= 0) {
			MessageBox.show(this, "����·���Ա�б���ѡ��һ��������Ա���д˲���!"); //
			return; //
		}
		if (!MessageBox.confirm(this, "��ȷ��Ҫɾ��ѡ�е���Ա��?")) {
			return;
		}
		billList_user_shopcar.removeSelectedRows(); //ɾ������ѡ���е���!!
	}

	/**
	 * �߼���ѯ
	 */
	public void onSuperSearch() {
		WorkFlowSuperChooseUserDialog scud = new WorkFlowSuperChooseUserDialog(this);
		scud.setVisible(true);
		if (scud.getCloseType() == 1) {
			onAddShopCarUserByBillVOs(scud.getVos());
		}
	}

	/**
	 * ɾ��������Ա!!
	 */
	private void onDelAllShopCarUser() {
		int li_count = billList_user_shopcar.getRowCount(); //
		if (li_count <= 0) {
			MessageBox.show(this, "��ѡ�����ԱΪ��,�޷����д˲���!"); //
			return; //
		}
		if (!MessageBox.confirm(this, "��ȷ��Ҫɾ��������ѡ�����Աô?")) {
			return;
		}
		billList_user_shopcar.clearTable(); //����������		
	}

	public BillVO getReturnCorpVO() {
		return returnCorpVO;
	}

	public BillVO[] getReturnUserVOs() {
		return returnUserVOs;
	}

	public int getCloseType() {
		return closeType;
	}

	public void stateChanged(ChangeEvent e) {
		if (tab.getSelectedIndex() == 1) {
			JPanel jp = (JPanel) tab.getComponentAt(1);
			if (jp.getClientProperty("aa") == null) {
				jp.setLayout(new BorderLayout());
				jp.removeAll();
				cubgp = new ChooseUserByGroupPanel(true, true, ClientEnvironment.getCurrLoginUserVO().getId(), this.billVO);//����в����������������������ƽ̨�������ã��Ƿ����û��б��Ƿ��ﳵ��������ѡ��һ����������
				jp.add(cubgp);
				jp.putClientProperty("aa", "aa");
			}
		}
	}

}
