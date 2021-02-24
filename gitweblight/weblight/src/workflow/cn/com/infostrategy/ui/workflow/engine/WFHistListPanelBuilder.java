package cn.com.infostrategy.ui.workflow.engine;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.to.mdata.templetvo.TempletItemVOComparator;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.workflow.WorkFlowServiceIfc;

/**
 * ������������ʷ����б�Ĺ�����!
 * ���������������ʷ�Ƿǳ�����ؼ���һ��UI,Ҳ������ν"����������"�ĺ��Ľ���!! �����Ի�Ҫ��ǳ��ߵĵط�! 
 * ����Ҫ������:
 * 1.Ҫ��˳����ʾ������,������,����ʱ��,����ʱ�ĵ�,ÿ���ͻ����ٶ��������б��˳�����������һЩ����,��Ҫ��������OAһ��!
 * 2.��ͬ����Ҫ�з������,������������ɫ����Ҳͦ��!
 * 3.Ҫ����ʾ[�������/����������]����֮����!
 * 4.Ҫ�������ָ���(ӿ��)�еĵ����,����Ĭ�ϸ��ݱ��˻������ͼ����Ĭ�����ĸ���!
 * 
 * ��ǰ������еķ����Ƿ���WorkflowUIUtil�е�,��������������๦�ܷǳ���Ҫ,�Ҵ������ǳ���,���Ե�������һ����!!
 * �����̴�����������̼�ؽ����ж��õ��������!
 * @author XCH
 */
public class WFHistListPanelBuilder implements ActionListener, ItemListener {

	private String prinstanceId = null; //
	private HashVO wfAssignHVO = null; //����������Ϣ��VO

	private HashVO[] hvs; //
	private BillVO billVO; //
	private boolean isHiddenBtnVisiable = false; //
	private BillListPanel histBillList; //
	private BillVO[] initAllBillVOs = null; //��ʼ��������BillVO!

	private WLTButton btn_viewAllMsg = null; //�鿴ȫ�����!
	private JComboBox combox_lastmsg = null; //[�������][������]��������!
	private JCheckBox[] allCheckBoxs = null; //��̬���������[��/ӿ��]������!! 

	private int li_maxDeptLevel = 2; //���ŵ����㼶,��Ϊ�����ý����Ҕ���ɲ���! �ڴ�����������������ɫ��ʾʱ,��ֻ��Ҫ������,������/���Ҷ���ɲ���!
	private TBUtil tbUtil = null; //
	private WorkflowUIUtil wfUIUtil = null; //

	private WFHistListPanelBuilder() {
	}

	public WFHistListPanelBuilder(HashVO[] _hvs, BillVO _cardBillVO, boolean _isHiddenBtnVisiable) {
		this.billVO = _cardBillVO; //
		loadWfAssignVO(); //

		this.hvs = _hvs; //
		this.isHiddenBtnVisiable = _isHiddenBtnVisiable; //
		initData(); //��ʼ������
	}

	public WFHistListPanelBuilder(String _prinstanceId, BillVO _cardBillVO, boolean _isHiddenBtnVisiable) {
		this.prinstanceId = _prinstanceId; //����ʵ��Id
		this.billVO = _cardBillVO; //
		loadWfAssignVO(); //��������������Ϣ

		try {
			WorkFlowServiceIfc service = (WorkFlowServiceIfc) UIUtil.lookUpRemoteService(WorkFlowServiceIfc.class); //
			hvs = service.getMonitorTransitions(prinstanceId, isJiaMeMsg(), true); // ���Դ��������Ϣ!!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}

		this.isHiddenBtnVisiable = _isHiddenBtnVisiable; //
		initData(); //��ʼ������
	}

	public BillListPanel getBillListPanel() {
		return histBillList; //
	}

	public HashVO[] getHashVODatas() {
		return hvs; //
	}

	//��ʼ������������ϢVO!!
	private void loadWfAssignVO() {
		try {
			if (billVO == null) {
				return; //
			}
			String str_billtype = billVO.getStringValue("billtype"); //
			String str_busitype = billVO.getStringValue("busitype"); //
			HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, "select * from pub_workflowassign where billtypecode='" + str_billtype + "' and busitypecode='" + str_busitype + "'"); //
			if (hvs != null && hvs.length > 0) {
				wfAssignHVO = hvs[0]; //��ֵ!!!
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}

	}

	//��������
	private void initData() {
		try {
			//�Ȱ��б�����!
			Pub_Templet_1VO templetVO = UIUtil.getPub_Templet_1VO(new ServerTMODefine("/cn/com/infostrategy/bs/sysapp/install/templetdata/pub_wf_dealpool_CODE1.xml")); //
			Pub_Templet_1_ItemVO[] templetItemVOs = templetVO.getItemVos(); // �����ӱ�
			//�ʴ���Ŀ������һ��Ҫ�����������,��Ҫ�������������ǰ��!
			String str_wfmsgListOrder = TBUtil.getTBUtil().getSysOptionStringValue("������������б����˳��", null); //
			if (str_wfmsgListOrder != null && !str_wfmsgListOrder.trim().equals("")) { //
				String[] str_orderItems = TBUtil.getTBUtil().split(str_wfmsgListOrder, ";"); //
				Arrays.sort(templetItemVOs, new TempletItemVOComparator(str_orderItems)); //
			}

			histBillList = new BillListPanel(templetVO, false); //
			if (!TBUtil.getTBUtil().getSysOptionBooleanValue("���������Ƿ�֧���ϴ�����", true)) { //�ʴ���Ŀ�������Ҫ�ϴ�����,˵�Ǳ����������������!!! ��Ϊ�������ݵ�������!
				histBillList.setItemVisible("submitmessagefile", false); //�����֧��,������!!!
			}
			if (isHiddenBtnVisiable) {
				histBillList.setHiddenUntitlePanelBtnvVisiable(true); //
			}
			histBillList.initialize(); //�ֹ���ʼ��!
			histBillList.setRowNumberChecked(true); //
			//histBillList.setItemVisible("submitisapprove", false); //���ص�
			histBillList.setItemVisible("submitmessage_viewreason", false); //���ص�

			ArrayList al_temp = new ArrayList(); //
			for (int i = 0; i < hvs.length; i++) { //������������!!!
				if ("Y".equals(hvs[i].getStringValue("issubmit"))) { //ֻ���ύ���Ĳ���ʾ!!!
					al_temp.add(hvs[i]); //
				}
			}
			TBUtil tbUtil = new TBUtil(); //
			HashVO[] submitedHVS = (HashVO[]) al_temp.toArray(new HashVO[0]); //
			tbUtil.sortHashVOs(submitedHVS, new String[][] { { "submittime", "N", "N" } }); //����һ��,�������ύʱ�����������!!!
			LinkedHashSet lh_blcorp = new LinkedHashSet(); //

			//�����ڱ���в�����������!!!!!

			for (int i = 0; i < submitedHVS.length; i++) { //
				int li_row = histBillList.newRow(false); //����һ��!
				histBillList.getRowNumberVO(li_row).setRecordHVO(submitedHVS[i]); //
				histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("id")), li_row, "id"); //��������
				histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("prinstanceid")), li_row, "prinstanceid"); //ʵ��id
				histBillList.setValueAt(new StringItemVO(getWFUIUtil().getWFActivityName(submitedHVS[i])), li_row, "curractivity_wfname"); //��ǰ����!!!

				//��ǰ����������,�ʴ���Ŀ������ʹ������м��������!
				if (submitedHVS[i].getStringValue("parentinstanceid", "").equals("")) { //���������Ϊ��,���ʾ��������
					histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("curractivity_belongdeptgroup")), li_row, "curractivity_bldeptname"); //��ǰ����������
				} else { //��������
					histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("prinstanceid_fromparentactivitybldeptgroup")), li_row, "curractivity_bldeptname"); //��ǰ����������
				}
				lh_blcorp.add(histBillList.getRealValueAtModel(li_row, "curractivity_bldeptname")); //��ǰ������������..

				histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("participant_user")), li_row, "participant_user"); //��Աid
				String str_participant_username = submitedHVS[i].getStringValue("participant_username"); //��Ȩ������!!
				String str_realsubmitername = submitedHVS[i].getStringValue("realsubmitername"); //ʵ���ύ������!һֱ�пͻ���,Ҫ֪��������˭�ɵ�!
				if (str_realsubmitername.indexOf("/") > 0) {
					str_realsubmitername = str_realsubmitername.substring(str_realsubmitername.indexOf("/") + 1, str_realsubmitername.length()); //
					submitedHVS[i].setAttributeValue("realsubmitername", str_realsubmitername);
				}

				String str_participant_yjbdusername = submitedHVS[i].getStringValue("participant_yjbdusername"); //�����������
				if (str_participant_yjbdusername != null) {
					if (str_participant_username.equals(str_realsubmitername)) {//˵�����ڴ������񲹵ǵ� �ȸ���Ժ���ܻ��кõ����/sunfujun/20121121
						str_participant_username = str_participant_yjbdusername + "(" + str_realsubmitername + "����)";
					} else {//��������ǲ��ǵ�
						str_participant_username = str_participant_username + "(" + str_realsubmitername + "����)"; //��¼��ʵ����˭���ǵ�!
					}
				}

				histBillList.setValueAt(new StringItemVO(str_participant_username), li_row, "participant_username"); //��Ա����

				String str_userdeptName = submitedHVS[i].getStringValue("realsubmitcorpname"); //
				int li_levelcount = getTBUtil().findCount(str_userdeptName, "-"); //
				if (li_levelcount > li_maxDeptLevel) {
					li_maxDeptLevel = li_levelcount; //
				}
				histBillList.setValueAt(new StringItemVO(str_userdeptName), li_row, "participant_userdeptname"); //��������
				histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("issubmit")), li_row, "issubmit"); //�Ƿ��ύ
				histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("submittime")), li_row, "submittime"); //�ύʱ��

				String str_submitmessage = submitedHVS[i].getStringValue("submitmessage", ""); //���
				String str_submitmessage_real = submitedHVS[i].getStringValue("submitmessage_real", ""); //,������������,�����Ǽ���ǰ��!!
				histBillList.setValueAt(new RefItemVO(str_submitmessage, str_submitmessage_real, str_submitmessage), li_row, "submitmessage"); //���!��ǰ���ı���,������Ϊ�м���,����Ū���˲���VO
				histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("submitmessage_viewreason")), li_row, "submitmessage_viewreason"); //���

				//�ļ�����Ҫ����һ��!
				String str_submitmessagefile = submitedHVS[i].getStringValue("submitmessagefile"); //������
				String str_submitmessagefile_real = submitedHVS[i].getStringValue("submitmessagefile_real"); //����Ǽ�����,�����Ǽ���ǰ��!
				if (submitedHVS[i].getStringValue("submitmessagefile") != null) { //
					if (str_submitmessagefile.trim().indexOf("/") != -1) { //�����·��,��ֻ��ʾ����
						histBillList.setValueAt(new RefItemVO(str_submitmessagefile, str_submitmessagefile_real, getRefFileName(str_submitmessagefile)), li_row, "submitmessagefile"); //
					} else { //���û��Ŀ¼!
						histBillList.setValueAt(new RefItemVO(str_submitmessagefile, str_submitmessagefile_real, str_submitmessagefile), li_row, "submitmessagefile"); //
					}
				}
			} //�����������ݽ���!!!
			initAllBillVOs = histBillList.getAllBillVOs(); //��ҳ����ȡ����������

			//ȡ���������̵�������ϢVO

			//���������һ�Ű�ť!!!
			btn_viewAllMsg = new WLTButton("���ȫ��", "cascade.gif"); //
			btn_viewAllMsg.putClientProperty("BindBillVO", this.billVO); //
			btn_viewAllMsg.addActionListener(this); //

			WLTButton btn_exportExcel = WLTButton.createButtonByType(WLTButton.LIST_EXPORTEXCEL, "�������"); //icon_xls.gif
			btn_exportExcel.setIcon(UIUtil.getImage("icon_xls.gif")); //
			btn_exportExcel.setPreferredSize(new Dimension(90, 23)); //
			histBillList.addBatchBillListButton(new WLTButton[] { btn_viewAllMsg, btn_exportExcel }); //
			histBillList.repaintBillListButton(); //ˢ��

			//���빴ѡ��,���ڿ��ٹ���..
			String str_myCorpType = null; //
			HashVO[] hvs_myCorps = UIUtil.getParentCorpVOByMacro(1, null, "$������"); //
			if (hvs_myCorps != null && hvs_myCorps.length > 0) {
				str_myCorpType = hvs_myCorps[0].getStringValue("corptype"); //��������
			}

			JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
			checkPanel.setOpaque(false); //͸��!
			combox_lastmsg = new JComboBox(); //����������,ֻ��Ҫ��ʾһ�������������!!���Լ�������һ���ж�!!
			combox_lastmsg.addItem("ȫ�����"); //
			combox_lastmsg.addItem("�����������"); //
			combox_lastmsg.setPreferredSize(new Dimension(100, 20)); //
			combox_lastmsg.setFocusable(false); //

			boolean isViewLastMsg = isDefaultViewLastMsg(); //
			if (isViewLastMsg) { //��ʾ�������
				combox_lastmsg.setSelectedIndex(1); //
			} else {
				combox_lastmsg.setSelectedIndex(0); //��ʾ�������
			}
			combox_lastmsg.addItemListener(this); //
			checkPanel.add(combox_lastmsg); //

			boolean isFindMycorp = false; //�Ƿ����ҵĻ���
			if (lh_blcorp.size() > 1) { //ֻ�д����������ϵĲŴ���!
				String[] str_checkNames = (String[]) lh_blcorp.toArray(new String[0]); //
				allCheckBoxs = new JCheckBox[str_checkNames.length]; //
				//�Ƿ�Ĭ�Ϲ�ѡ���ҵĲ���???�ʴ�Ҫ��ѡ��,����ҵ��Ҷ��ʦ������Ϊ��ע�������ѡ����Ϊ�е����û��!!! ����˵ϵͳ������,���Ըɴ�Ĭ�϶��ų���!
				boolean isDefaultChooseMydept = TBUtil.getTBUtil().getSysOptionBooleanValue("��������ʷ�嵥�Ƿ�Ĭ�Ϲ�ѡ�ҵ����л���", true);
				for (int i = 0; i < str_checkNames.length; i++) {
					allCheckBoxs[i] = new JCheckBox(str_checkNames[i]); //
					allCheckBoxs[i].setOpaque(false); //
					allCheckBoxs[i].setToolTipText("��סCtrl/Shift�����Զ�ѡ"); //
					allCheckBoxs[i].setFocusable(false); //
					if (isDefaultChooseMydept && str_myCorpType != null && str_myCorpType.startsWith(str_checkNames[i])) { //����ҵĻ����������������Ϊ��ͷ��!˵������������һ���е�!
						allCheckBoxs[i].setSelected(true); //
						isFindMycorp = true; //
					}
					allCheckBoxs[i].addActionListener(this); //
					checkPanel.add(allCheckBoxs[i]); //
				}
			}
			histBillList.getCustomerNavigationJPanel().add(checkPanel); //

			//�������ȫ�����,����Ĭ�Ϲ�ѡ����ĳ������,��Ҫ����һ�¹���!
			if (combox_lastmsg.getSelectedIndex() != 0 || isFindMycorp) {
				onFilterDealPoolByBlDeptName(histBillList, null, false); //�ٴι���һ��,��Ϊֱ�Ӵ�ҳ����ȡ��,�������ܺܿ�!..
			} else {
				setBackgroundBySameDept(histBillList); //����ͬ�Ĳ��ŵļ�¼�ı���ɫŪ��һ��!�γ�һ�����Ч��!!
				histBillList.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT); //
			}

			histBillList.getMainScrollPane().getVerticalScrollBar().setValue(0); //����������
			histBillList.getMainScrollPane().getHorizontalScrollBar().setValue(0); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * ��ҵ�����и��ӵļ���У���߼�,��һЩ��������Ա,���߹���Ա�ǲ���Ҫ���ܴ����!!!�����������̷�����м���һ������,ָ����Щ����������ҵ��������Ҫ���ܵ�!
	 * 
	 * @param _billVO
	 * @return
	 */
	private boolean isJiaMeMsg() {
		//�������̼�ش���!!
		//�����Ƿ���ܻ�Ҫ��һ���ж�,һ����:���߳�������Ա�ǲ����ܵ�,�������з��ɺϹ沿���������,���еķ��ɹ���Ա�鿴���з�����Ϣ!!
		//�ڶ�����:�е����̾��Ƿ��в���ı���,��������Ҫ���߼�����! 
		String str_reason = ""; //
		boolean isJiaMi = getTBUtil().getSysOptionBooleanValue("��������ʷ��¼���Ƿ����δ������", true); //Ĭ���Ǽ��ܵ�
		if (wfAssignHVO == null) {
			return isJiaMi; //
		}

		try {
			String str_openmsgroles = wfAssignHVO.getStringValue("openmsgroles"); //
			if (str_openmsgroles != null) { //���������!
				String[] str_items = TBUtil.getTBUtil().split(str_openmsgroles, ";"); //�ָ�һ��!
				String[] str_myAllroles = ClientEnvironment.getCurrLoginUserVO().getAllRoleCodes(); //�ҵ����н�ɫ!!

				for (int i = 0; i < str_items.length; i++) {
					if (TBUtil.getTBUtil().isExistInArray(str_items[i], new String[] { "һ���û�", "һ��Ա��", "������Ա", "����Ա��", "�����û�", "������" })) {
						str_reason = "��Ϊ������\"һ���û�/������\"֮���!"; //
						isJiaMi = false; //������!!
						break; //
					}

					boolean isFind = false; //
					for (int j = 0; j < str_myAllroles.length; j++) {
						if (str_myAllroles[j].indexOf(str_items[i]) >= 0) {
							str_reason = "��Ϊ����һ������[" + str_items[i] + "]���ҵ�һ����ɫ[" + str_myAllroles[j] + "]ƥ������!"; //
							isFind = true; //
							break; //
						}
					}
					if (isFind) { //����ҵ�
						isJiaMi = false; //������!!
						break; //
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
		return isJiaMi; //
	}

	/**
	 * Ĭ���Ƿ���ʾ"�����������"
	 * @return
	 */
	private boolean isDefaultViewLastMsg() {
		//�������̼�ش���!!
		//�����Ƿ���ܻ�Ҫ��һ���ж�,һ����:���߳�������Ա�ǲ����ܵ�,�������з��ɺϹ沿���������,���еķ��ɹ���Ա�鿴���з�����Ϣ!!
		//�ڶ�����:�е����̾��Ƿ��в���ı���,��������Ҫ���߼�����! 
		String str_reason = ""; //
		boolean isViewLastMsg = false; //Ĭ������ʾ���������!
		if (wfAssignHVO == null) {
			return isViewLastMsg; //
		}

		try {
			String str_openmsgroles = wfAssignHVO.getStringValue("viewlastmsgroles"); //��ʾ�������������!
			if (str_openmsgroles != null) { //���������!
				boolean isBlackList = false; //�Ƿ��Ǻ�����
				if (str_openmsgroles.startsWith("!")) {
					str_openmsgroles = str_openmsgroles.substring(1, str_openmsgroles.length()); //
					isBlackList = true; //
				}
				String[] str_items = TBUtil.getTBUtil().split(str_openmsgroles, ";"); //�ָ�һ��!
				String[] str_myAllroles = ClientEnvironment.getCurrLoginUserVO().getAllRoleCodes(); //�ҵ����н�ɫ!!

				for (int i = 0; i < str_items.length; i++) {
					if (TBUtil.getTBUtil().isExistInArray(str_items[i], new String[] { "һ���û�", "һ��Ա��", "������Ա", "����Ա��", "�����û�", "������" })) {
						str_reason = "��Ϊ������\"һ���û�/������\"֮���!"; //
						isViewLastMsg = true; //��Щ����ʾ�������!
						break; //
					}

					boolean isFind = false; //
					for (int j = 0; j < str_myAllroles.length; j++) {
						if (str_myAllroles[j].indexOf(str_items[i]) >= 0) {
							str_reason = "��Ϊ����һ������[" + str_items[i] + "]���ҵ�һ����ɫ[" + str_myAllroles[j] + "]ƥ������!"; //
							isFind = true; //
							break; //
						}
					}

					if (isFind) { //����ҵ�
						isViewLastMsg = true; //
						System.out.println("ֻ��ʾ���������ԭ��:" + str_reason); //
						break; //
					}
				}

				if (isBlackList) { //����Ǻ�����,���ٷ�һ��!
					isViewLastMsg = !isViewLastMsg; //
				}

			} else {
				isViewLastMsg = false; //�����ʾ����Ϊ��,��Ĭ�ϱ�ʾ���鿴�������,��Ĭ����ʾ�������!
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
		return isViewLastMsg; //
	}

	/**
	 * ��������ͼ�еĲ������ƹ������̴������
	 * @param histBillList
	 * @param _checkBoxs
	 */
	protected void onFilterDealPoolByBlDeptName(BillListPanel histBillList, JCheckBox _clickedCheckBox, boolean _isCtrlDown) {
		HashSet hst_name = new HashSet(); //
		if (allCheckBoxs != null) {
			if (_clickedCheckBox != null && !_isCtrlDown) { //
				for (int i = 0; i < allCheckBoxs.length; i++) {
					if (allCheckBoxs[i] != _clickedCheckBox) { //
						allCheckBoxs[i].setSelected(false); //
					}
				}
			}
			for (int i = 0; i < allCheckBoxs.length; i++) {
				if (allCheckBoxs[i].isSelected()) { //����Ѿ���ѡ��
					hst_name.add(allCheckBoxs[i].getText()); //
				}
			}
		}

		if (hst_name.size() == 0 || (allCheckBoxs != null && hst_name.size() == allCheckBoxs.length)) { //���ȫ����ѡ��ȫ����ѡ,���������!
			if (combox_lastmsg.getSelectedIndex() == 0) { //���ѡ�е���ȫ�����!!!
				histBillList.clearTable(); //
				histBillList.addBillVOs(initAllBillVOs); //
			} else { //������������
				LinkedHashMap hst_dept = new LinkedHashMap(); //
				for (int i = initAllBillVOs.length - 1; i >= 0; i--) {//����ǵ������еģ�����Ҫ����ȡ��������ȡ��ʱ�����������������/2016-12-20��
					String str_dept = initAllBillVOs[i].getStringValue("participant_userdeptname"); //�����˻�������
					str_dept = trimDeptNameByPos(str_dept); //��������-�о��滮��-�ۺ��о�����ת���ɡ�����-�о��滮����
					hst_dept.put(str_dept, initAllBillVOs[i]);
				}
				BillVO[] filterVOs = (BillVO[]) hst_dept.values().toArray(new BillVO[0]); //
				histBillList.clearTable(); //
				for (int i = 0; i < filterVOs.length; i++) {
					int li_newRow = histBillList.addEmptyRow(false, false); //
					histBillList.setBillVOAt(li_newRow, filterVOs[i]); //
				}
			}
		} else { //
			histBillList.clearTable(); //���������
			ArrayList al_vos = new ArrayList(); //
			for (int i = 0; i < initAllBillVOs.length; i++) { //ѭ������!
				String str_bldept = initAllBillVOs[i].getStringValue("curractivity_bldeptname"); //����
				if (hst_name.contains(str_bldept)) { //��һ���Ǳ���ѡ�ϵ�
					al_vos.add(initAllBillVOs[i]); //����
				}
			}
			BillVO[] filterVOs = null; //
			if (combox_lastmsg.getSelectedIndex() == 0) { //���ѡ�е���ȫ�����!!!
				filterVOs = (BillVO[]) al_vos.toArray(new BillVO[0]);
			} else { //���ֻ��ʾ����������!!!��Ҫ�����ٴι���!
				LinkedHashMap hst_dept = new LinkedHashMap(); //
				for (int i = 0; i < al_vos.size(); i++) {
					BillVO itemVO = (BillVO) al_vos.get(i); //
					String str_dept = itemVO.getStringValue("participant_userdeptname"); //�����˻�������
					str_dept = trimDeptNameByPos(str_dept); //��������-�о��滮��-�ۺ��о�����ת���ɡ�����-�о��滮����
					String str_submittime = itemVO.getStringValue("submittime");
					if (hst_dept.containsKey(str_dept)) { //�������������!
						hst_dept.remove(str_dept); //���Ƶ�!!!
						hst_dept.put(str_dept, itemVO);
					} else {
						hst_dept.put(str_dept, itemVO); //����,��Ϊԭ�����ǰ�ʱ�������,����������Ȼ��֤����Ļ���ǰ���,����Ȼ�洢�������һ��!
					}
				}
				filterVOs = (BillVO[]) hst_dept.values().toArray(new BillVO[0]); //
			}

			for (int i = 0; i < filterVOs.length; i++) {
				int li_newRow = histBillList.addEmptyRow(false, false); //
				histBillList.setBillVOAt(li_newRow, filterVOs[i]); //
			}
		}
		if (combox_lastmsg.getSelectedIndex() == 0) { //ֻ����ʾ�������ʱ�ŷ�����ɫ,������һ����һ����ɫ,û������!!
			setBackgroundBySameDept(histBillList); //����ͬ�Ĳ��ŵļ�¼�ı���ɫŪ��һ��!�γ�һ�����Ч��!!
		} else {
			histBillList.clearItemBackGroundColor(); //
		}
		histBillList.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT); //
	}

	/**
	 * ���ñ�����ɫ!!!
	 */
	private void setBackgroundBySameDept(BillListPanel _billList) {
		String[] str_colors = new String[] { "E1FAFF", "FFE4DD", "E8D7FF", "D7FED3", "F3F3F3", "FFFFD9", "C6FF8C", "FFB38E", "CC9999", "53FF53" }; //
		HashMap colorMap = new HashMap(); //

		for (int i = 0; i < _billList.getRowCount(); i++) {
			String str_deptName = _billList.getRealValueAtModel(i, "participant_userdeptname"); //��������
			str_deptName = trimDeptNameByPos(str_deptName); //��������-�о��滮��-�ۺ��о�����ת���ɡ�����-�о��滮����
			if (str_deptName == null) {
				str_deptName = ""; //
			}
			if (colorMap.containsKey(str_deptName)) { //�����ע�����ɫ
				Color cor = (Color) colorMap.get(str_deptName); //
				_billList.setItemBackGroundColor(cor, i); //
			} else { //���û����ɫ!
				int li_index = colorMap.size() % str_colors.length; //�ĸ���ɫ��0..5
				Color cor = getTBUtil().getColor(str_colors[li_index]); //ȡ����ɫ!!
				_billList.setItemBackGroundColor(cor, i); //
				colorMap.put(str_deptName, cor); //
			}
		}
	}

	//ֻȡǰ��λ,���иָܷ�!!!
	private String trimDeptNameByPos(String _deptname) {
		int li_maxcount = li_maxDeptLevel; //��������Ժ���Ը���������Ŀ��������!,Ĭ����2,������ҵ��������4�����,����ʱ������3��,�����ǵ�2��!
		if (li_maxcount > 0) { //
			int li_count = getTBUtil().findCount(_deptname, "-"); //���ܹ��м����и�???
			if (li_count >= li_maxcount) { //���������!!!
				String str_1 = ""; //
				String str_remain = _deptname; //
				for (int i = 0; i < li_maxcount; i++) {
					str_1 = str_1 + str_remain.substring(0, str_remain.indexOf("-")) + "-"; //�ڼ����и�??
					str_remain = str_remain.substring(str_remain.indexOf("-") + 1, str_remain.length()); //
				}
				str_1 = str_1.substring(0, str_1.length() - 1); //
				return str_1; //
			} else {
				return _deptname; //
			}
		} else {
			return _deptname; //
		}
	}

	public void actionPerformed(ActionEvent _event) {
		if (_event.getSource() == btn_viewAllMsg) { //���鿴ȫ���������ť!!
			onViewAllMsg((WLTButton) _event.getSource()); //
		} else if (_event.getSource() instanceof JCheckBox) {
			boolean isCtrlShift = ((_event.getModifiers() == 17 || _event.getModifiers() == 18) ? true : false);
			JCheckBox clickedCheckBox = (JCheckBox) _event.getSource(); //��������ĸ���ѡ��!
			onFilterDealPoolByBlDeptName(histBillList, clickedCheckBox, isCtrlShift); //
		}
	}

	//������仯ʱ�Ĵ���
	public void itemStateChanged(ItemEvent e) {
		onFilterDealPoolByBlDeptName(histBillList, null, false); //
	}

	//�鿴�������!
	private void onViewAllMsg(WLTButton _button) {
		BillListPanel billList = (BillListPanel) _button.getBillPanelFrom(); //
		if (billList.getRowCount() <= 0) {
			MessageBox.show(billList, "û����ʷ�����¼���Բ鿴!"); //
			return; //
		}

		BillVO bindBillVO = (BillVO) _button.getClientProperty("BindBillVO"); //
		BillVO[] checkBillVOs = billList.getCheckedBillVOs(); //
		if (checkBillVOs == null || checkBillVOs.length == 0) { //���Ϊ�����ʾ����!
			checkBillVOs = billList.getAllBillVOs(); //
		} else { //
			if (!MessageBox.confirm(billList, "�����ֻ�뵼��ѡ�е������?")) { //�����ѡok,���˳�
				return; //
			}
		}

		String str_tile = "�������"; //
		if (bindBillVO != null) {
			str_tile = bindBillVO.getTempletName() + "-�������"; //
		}
		String str_prinstanceid = null; //
		//��ǰ�ܼ�ª,����ר��Ū��һ����,Ȼ��Ū�˶��ַ��,����һ�����ݵĽ���ͻ���������������Ĳ�����...
		BillDialog dialog = new LookWorkflowDealMsgDialog(billList, checkBillVOs, str_tile, str_prinstanceid);
		dialog.setVisible(true); //
	}

	//�ļ���ǰ����Ŀ¼,������16����,��Ҫ����!!!
	private String getRefFileName(String _refId) {
		TBUtil tb = getTBUtil(); //
		StringBuilder sb_name = new StringBuilder(); //
		String[] sr_items = tb.split(_refId, ";"); //
		for (int i = 0; i < sr_items.length; i++) {
			String str_item = sr_items[i].trim(); //
			str_item = str_item.substring(str_item.lastIndexOf("/") + 1, str_item.length()); //ȥ��ǰ���Ŀ¼!
			String str_item_convert = tb.convertHexStringToStr(str_item.substring(str_item.indexOf("_") + 1, str_item.lastIndexOf("."))) + (str_item.substring(str_item.lastIndexOf("."), str_item.length())); //16���Ʒ�ת!
			sb_name.append(str_item_convert + ";"); //
		}
		return sb_name.toString(); //
	}

	private WorkflowUIUtil getWFUIUtil() {
		if (wfUIUtil != null) {
			return wfUIUtil;
		}
		wfUIUtil = new WorkflowUIUtil(); //
		return wfUIUtil; //
	}

	private TBUtil getTBUtil() {
		if (tbUtil != null) {
			return tbUtil;
		}
		tbUtil = new TBUtil();
		return tbUtil;
	}

}
