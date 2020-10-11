package cn.com.infostrategy.ui.workflow.engine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.to.mdata.templetvo.TempletItemVOComparator;
import cn.com.infostrategy.to.workflow.design.ActivityVO;
import cn.com.infostrategy.to.workflow.engine.MsgVO;
import cn.com.infostrategy.ui.common.BackGroundDrawingUtil;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.RemoteServiceFactory;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTPanelUI;
import cn.com.infostrategy.ui.common.WLTTextArea;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.HFlowLayoutPanel;
import cn.com.infostrategy.ui.workflow.WorkFlowServiceIfc;
import cn.com.infostrategy.ui.workflow.msg.MyMsgUIUtil;

/**
 * �������ͻ��˹���!!!
 * @author xch
 *
 */
public class WorkflowUIUtil implements Serializable {

	private static final long serialVersionUID = 1L;

	private WorkFlowServiceIfc workFlowService; //

	private TBUtil tbUtil = new TBUtil(); //

	/**
	 * ���췽��
	 */
	public WorkflowUIUtil() {
	}

	private TBUtil getTBUtil() {
		if (tbUtil != null) {
			return tbUtil;
		}
		tbUtil = new TBUtil();
		return tbUtil;
	}

	/**
	 * ��Эһ������!!
	 * @param _parent
	 * @param billVO
	 * @return
	 * @throws Exception
	 */
	public String startWorkFlow(Container _parent, BillVO billVO, ActivityVO _startActivityVO) throws Exception {
		if (_startActivityVO != null) {//�����Ѿ���_startActivityVO ���������ˣ��ʲ���Ҫ���ж������ˡ�
			String str_prinstanceid = getWorkFlowService().startWFProcess(_startActivityVO.getProcessid(), billVO, ClientEnvironment.getInstance().getLoginUserID(), _startActivityVO); // ����һ������!!
			return str_prinstanceid;
		}
		if (!billVO.isHaveKey("billtype") || !billVO.isHaveKey("busitype")) {
			MessageBox.show(_parent, "No BillType and Busitype,Cant't be deal!", WLTConstants.MESSAGE_WARN); //
			return null; //
		}

		if (billVO.getEditType().equals(WLTConstants.BILLDATAEDITSTATE_INSERT)) { // ����Ǵ�������״̬,�����ύ������
			MessageBox.show(_parent, "��¼��û�б���,���ȱ���!", WLTConstants.MESSAGE_WARN); //
			return null; //
		}

		String str_billtype = billVO.getStringValue("billtype"); // ��������..
		String str_busitype = billVO.getStringValue("busitype"); // ҵ������..
		try {
			String str_sql = "select * from pub_workflowassign where billtypecode='" + str_billtype + "' and busitypecode='" + str_busitype + "'"; //
			HashVO[] vos = UIUtil.getHashVoArrayByDS(null, str_sql); //
			if (vos.length == 0) {
				MessageBox.show(_parent, "û��ΪBillType[" + str_billtype + "],BusiType[" + str_busitype + "]ָ������,���ܽ������̴���!", WLTConstants.MESSAGE_INFO); //
				return null;
			}

			String str_processId = vos[0].getStringValue("processid"); //
			if (str_processId == null || str_processId.trim().equals("")) {
				MessageBox.show(_parent, "��ΪBillType[" + str_billtype + "],BusiType[" + str_busitype + "]ָ�����̵�����IDΪ��,���ܽ������̴���!", WLTConstants.MESSAGE_INFO); //
				return null;
			}

			if (_startActivityVO == null) {
				MessageBox.show(_parent, "��������Ϊ��,�޷���������!");//
				return null;
			}
			String str_prinstanceid = getWorkFlowService().startWFProcess(str_processId, billVO, ClientEnvironment.getInstance().getLoginUserID(), _startActivityVO); // ����һ������!!
			return str_prinstanceid; //
		} catch (Exception ex) {
			ex.printStackTrace();
			MessageBox.show(_parent, "��������ʧ��,ԭ��:" + ex.getMessage(), WLTConstants.MESSAGE_ERROR); //
			return null;
		}
	}

	public String startWorkFlow(Container _parent, BillVO billVO) throws Exception {
		ActivityVO[] startVOs = getStartActivityVOs(_parent, billVO); //
		if (startVOs != null && startVOs.length > 0) {
			ActivityVO startActivityVO = null; //
			if (startVOs.length == 1) { //���ֻ��һ����������,������֮
				startActivityVO = startVOs[0]; //
			} else { //���򵯳�����ͼ,�������������ĵط���������û�ѡ��һ������!
				WorkFlowChooseStartActivityDialog dialog = new WorkFlowChooseStartActivityDialog(_parent, startVOs); //
				dialog.setVisible(true); //
				if (dialog.getCloseType() == 1) { //�����ȷ������!
					startActivityVO = dialog.getReturnActivityVO(); //
				} else {
					return null; //
				}
			}

			if (startActivityVO != null) {
				return startWorkFlow(_parent, billVO, startActivityVO); //
			} else {
				MessageBox.show(_parent, "��������Ϊ��,�����޷�����!");
				return null;
			}
		} else {
			MessageBox.show(_parent, "û���ҵ�һ����������!�����޷�����!");
			return null;
		}
	}

	/**
	 * ȡ�����п��������Ļ���
	 * @return
	 */
	public ActivityVO[] getStartActivityVOs(Container _parent, BillVO billVO) throws Exception {
		if (billVO.getEditType().equals(WLTConstants.BILLDATAEDITSTATE_INSERT)) { // ����Ǵ�������״̬,�����ύ������
			MessageBox.show(_parent, "��¼��û�б���,���ȱ���!", WLTConstants.MESSAGE_WARN); //
			return null; //
		}
		String str_billtype = billVO.getStringValue("billtype"); // ��������..
		String str_busitype = billVO.getStringValue("busitype"); // ҵ������..
		String str_processId = null;
		String str_processCode = null;
		String str_processName = null;
		if ("".equals(str_billtype)) {//�����������Ϊ�գ���ֱ��ѡ��������ļ��ڵ����̽��з������/2012-05-18��
			BillListDialog listdialog = new BillListDialog(_parent, "�ñ�δ���õ�������,��ѡ������", "PUB_WF_PROCESS_CODE3"); //);
			BillListPanel listpanel_wf = listdialog.getBilllistPanel();
			listpanel_wf.setQuickQueryPanelVisiable(true);
			listpanel_wf.setDataFilterCustCondition("cmpfileid is null");//
			listdialog.setVisible(true);
			if (listdialog.getCloseType() == 1) {
				str_processId = listdialog.getReturnBillVOs()[0].getStringValue("id");
				str_processCode = listdialog.getReturnBillVOs()[0].getStringValue("code");
				str_processName = listdialog.getReturnBillVOs()[0].getStringValue("name");
			} else {
				return null;
			}
		} else if ("".equals(str_busitype)) {//����������Ͳ�Ϊ�գ���ҵ������Ϊ�գ����ҳ����иõ����������õ�����
			BillListDialog listdialog = new BillListDialog(_parent, "�ñ�δ����ҵ������,��ѡ������", "PUB_WF_PROCESS_CODE3"); //);
			BillListPanel listpanel_wf = listdialog.getBilllistPanel();
			listpanel_wf.setQuickQueryPanelVisiable(true);
			listpanel_wf.setDataFilterCustCondition("id in(select processid from pub_workflowassign where billtypecode='" + str_billtype + "')");///////////
			listdialog.setVisible(true);
			if (listdialog.getCloseType() == 1) {
				str_processId = listdialog.getReturnBillVOs()[0].getStringValue("id");
				str_processCode = listdialog.getReturnBillVOs()[0].getStringValue("code");
				str_processName = listdialog.getReturnBillVOs()[0].getStringValue("name");
			} else {
				return null;
			}
		} else {//����������ͺ�ҵ�����Ͷ���Ϊ�գ�������̷����в��Ҷ�Ӧ������
			String str_sql = "select pub_workflowassign.processid,pub_wf_process.code processcode,pub_wf_process.name processname  from pub_workflowassign left outer join pub_wf_process on pub_workflowassign.processid=pub_wf_process.id where pub_workflowassign.billtypecode='" + str_billtype + "' and pub_workflowassign.busitypecode='" + str_busitype + "'"; //
			HashVO[] vos = UIUtil.getHashVoArrayByDS(null, str_sql); //
			if (vos.length == 0) {
				MessageBox.show(_parent, "û��ΪBillType[" + str_billtype + "],BusiType[" + str_busitype + "]ָ������,���ܽ������̴���!", WLTConstants.MESSAGE_INFO); //
				return null;
			}

			str_processId = vos[0].getStringValue("processid"); //
			str_processCode = vos[0].getStringValue("processcode"); //
			str_processName = vos[0].getStringValue("processname"); //

			if (str_processId == null || str_processId.trim().equals("")) {
				MessageBox.show(_parent, "��ΪBillType[" + str_billtype + "],BusiType[" + str_busitype + "]ָ�����̵�����IDΪ��,���ܽ������̴���!", WLTConstants.MESSAGE_INFO); //
				return null;
			}
		}

		WorkFlowServiceIfc workFlowservice = (WorkFlowServiceIfc) UIUtil.lookUpRemoteService(WorkFlowServiceIfc.class); //
		ActivityVO[] returnActivityVOS = workFlowservice.getStartActivityVOs(str_processId, ClientEnvironment.getInstance().getLoginUserID()); //
		if (returnActivityVOS == null || returnActivityVOS.length == 0) {
			throw new WLTAppException("���ݵ�������[" + str_billtype + "],ҵ������[" + str_busitype + "]�ɹ��ҵ�Ʒ�������,���̱�����:[" + str_processCode + "],����������:[" + str_processName + "].\r\n��û���ڸ��������ҵ�һ�����������Ļ���!����Ҫ�����������϶���[���������Ľ�ɫ]������,����ڸ��������ϼ�����Ϊ[������Ա]�Ľ�ɫ���ʾ�����˶���������������!"); //
		}
		return returnActivityVOS; //
	}

	/**
	 * ȡ�ù�����F����!!!
	 */
	private WorkFlowServiceIfc getWorkFlowService() throws Exception {
		if (workFlowService != null) {
			return workFlowService;
		}

		workFlowService = (WorkFlowServiceIfc) RemoteServiceFactory.getInstance().lookUpService(WorkFlowServiceIfc.class); //
		return workFlowService;
	}

	/**
	 * ������ͨ�ô���!
	 * @param _type ��[deal][reject][monitor]
	 * @param _parent
	 * @param _billList
	 */
	public void dealWorkFlowAcion(String _type, BillListPanel _billList) {
		if (_type.equalsIgnoreCase("deal")) { //����Ǵ���

		} else if (_type.equalsIgnoreCase("reject")) { //������˻�!

		} else if (_type.equalsIgnoreCase("monitor")) { //����Ǽ��

		}
	}

	public BillListPanel getWFHistoryDealPoolBillList(HashVO[] _hvs, BillVO _cardBillVO) throws Exception {
		return getWFHistoryDealPoolBillList(_hvs, _cardBillVO, false); //
	}

	/**
	 * ȡ�����̴�����ʷ�б����,����WorkFlowProcessPanel�����̼������ж��õ������,Ϊ���������Է�������!!!
	 * @param _hvs
	 * @return
	 */
	public BillListPanel getWFHistoryDealPoolBillList(HashVO[] _hvs, BillVO _cardBillVO, boolean _isHiddenBtnVisiable) throws Exception {
		ArrayList al_temp = new ArrayList(); //
		for (int i = 0; i < _hvs.length; i++) { //������������!!!
			if ("Y".equals(_hvs[i].getStringValue("issubmit"))) { //ֻ���ύ���Ĳ���ʾ!!!
				al_temp.add(_hvs[i]); //
			}
		}
		TBUtil tbUtil = new TBUtil(); //
		HashVO[] submitedHVS = (HashVO[]) al_temp.toArray(new HashVO[0]); //
		tbUtil.sortHashVOs(submitedHVS, new String[][] { { "submittime", "N", "N" } }); //����һ��,�������ύʱ�����������!!!

		Pub_Templet_1VO templetVO = UIUtil.getPub_Templet_1VO(new ServerTMODefine("/cn/com/infostrategy/bs/sysapp/install/templetdata/pub_wf_dealpool_CODE1.xml")); //
		Pub_Templet_1_ItemVO[] templetItemVOs = templetVO.getItemVos(); // �����ӱ�

		//�ʴ���Ŀ������һ��Ҫ�����������,��Ҫ�������������ǰ��!
		String str_wfmsgListOrder = TBUtil.getTBUtil().getSysOptionStringValue("������������б����˳��", null); //
		if (str_wfmsgListOrder != null && !str_wfmsgListOrder.trim().equals("")) { //
			String[] str_orderItems = TBUtil.getTBUtil().split(str_wfmsgListOrder, ";"); //
			Arrays.sort(templetItemVOs, new TempletItemVOComparator(str_orderItems)); //
		}

		final BillListPanel histBillList = new BillListPanel(templetVO, false); //
		if (!TBUtil.getTBUtil().getSysOptionBooleanValue("���������Ƿ�֧���ϴ�����", true)) { //�ʴ���Ŀ�������Ҫ�ϴ�����,˵�Ǳ����������������!!! ��Ϊ�������ݵ�������!
			histBillList.setItemVisible("submitmessagefile", false); //�����֧��,������!!!
		}
		if (_isHiddenBtnVisiable) {
			histBillList.setHiddenUntitlePanelBtnvVisiable(true); //
		}
		histBillList.initialize(); //�ֹ���ʼ��!
		histBillList.setRowNumberChecked(true); //
		//histBillList.setItemVisible("submitisapprove", false); //���ص�
		histBillList.setItemVisible("submitmessage_viewreason", false); //���ص�

		LinkedHashSet lh_blcorp = new LinkedHashSet(); //
		for (int i = 0; i < submitedHVS.length; i++) { //������������!!!
			int li_row = histBillList.newRow(false); //
			histBillList.getRowNumberVO(li_row).setRecordHVO(submitedHVS[i]); //
			histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("id")), li_row, "id"); //��������
			histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("prinstanceid")), li_row, "prinstanceid"); //ʵ��id
			histBillList.setValueAt(new StringItemVO(getWFActivityName(submitedHVS[i])), li_row, "curractivity_wfname"); //��ǰ����!!!

			//��ǰ����������,�ʴ���Ŀ������ʹ������м��������!
			if (submitedHVS[i].getStringValue("parentinstanceid", "").equals("")) { //���������Ϊ��,���ʾ��������
				histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("curractivity_belongdeptgroup")), li_row, "curractivity_bldeptname"); //��ǰ����������
			} else { //��������
				histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("prinstanceid_fromparentactivitybldeptgroup")), li_row, "curractivity_bldeptname"); //��ǰ����������
			}
			lh_blcorp.add(histBillList.getRealValueAtModel(li_row, "curractivity_bldeptname")); //��ǰ������������..

			histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("participant_user")), li_row, "participant_user"); //��Աid
			String str_realsubmitername = submitedHVS[i].getStringValue("realsubmitername"); //ʵ���ύ������!һֱ�пͻ���,Ҫ֪��������˭�ɵ�!
			String str_participant_accrusername = submitedHVS[i].getStringValue("participant_accrusername"); //��Ȩ������!!
			if (str_participant_accrusername != null) { //�������Ȩ
				if (str_participant_accrusername.indexOf("/") > 0) {
					str_participant_accrusername = str_participant_accrusername.substring(str_participant_accrusername.indexOf("/") + 1, str_participant_accrusername.length()); //
				}
				String str_realsubmitername_real = null; //
				if (str_realsubmitername.indexOf("/") > 0) {
					str_realsubmitername_real = str_realsubmitername.substring(str_realsubmitername.indexOf("/") + 1, str_realsubmitername.length()); //
				} else {
					str_realsubmitername_real = str_realsubmitername;
				}
				if (!str_realsubmitername_real.equals(str_participant_accrusername)) { //������߲���!
					str_realsubmitername = str_participant_accrusername + "(��Ȩ" + str_realsubmitername + ")"; //ʼ����ʾ ��Ȩ��(��Ȩ����Ȩ��)����ʽ
				}
			}

			//�ʴ��������Ҫǰ��Ĺ���
			if (!TBUtil.getTBUtil().getSysOptionBooleanValue("��������������б��е���Ա�Ƿ���ʾ����", true)) {
				if (str_realsubmitername.indexOf("/") > 0) {
					str_realsubmitername = str_realsubmitername.substring(str_realsubmitername.indexOf("/") + 1, str_realsubmitername.length()); //
				}
			}
			histBillList.setValueAt(new StringItemVO(str_realsubmitername), li_row, "participant_username"); //��Ա����
			histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("realsubmitcorpname")), li_row, "participant_userdeptname"); //��������
			histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("issubmit")), li_row, "issubmit"); //�Ƿ��ύ
			histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("submittime")), li_row, "submittime"); //�ύʱ��

			//�ύ/�˼�,�����ص�,�Ժ�����һ�����������Ƿ���ʾ!!
			//			String str_isapprove = submitedHVS[i].getStringValue("submitisapprove", ""); //
			//			StringItemVO sivo_isapprove = null; //
			//			if (str_isapprove.equals("N")) {
			//				sivo_isapprove = new StringItemVO("�˻�"); //
			//				sivo_isapprove.setForeGroundColor("FF0000"); //
			//			} else {
			//				sivo_isapprove = new StringItemVO("�ύ"); //
			//			}			
			//			histBillList.setValueAt(sivo_isapprove, li_row, "submitisapprove"); //��������
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
		}
		setBackgroundBySameDept(histBillList); //����ͬ�Ĳ��ŵļ�¼�ı���ɫŪ��һ��!�γ�һ�����Ч��!!
		histBillList.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT); //

		WLTButton btn_viewAllMsg = new WLTButton("���ȫ��", "cascade.gif"); //
		btn_viewAllMsg.putClientProperty("BindBillVO", _cardBillVO); //
		btn_viewAllMsg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onViewAllMsg((WLTButton) e.getSource()); //
			}
		}); //

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
		if (lh_blcorp.size() > 1) { //ֻ�д����������ϵĲŴ���!
			JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
			checkPanel.setOpaque(false); //
			String[] str_checkNames = (String[]) lh_blcorp.toArray(new String[0]); //
			final JCheckBox[] checkBoxs = new JCheckBox[str_checkNames.length]; //
			boolean isFindMycorp = false; //�Ƿ����ҵĻ���
			for (int i = 0; i < str_checkNames.length; i++) {
				checkBoxs[i] = new JCheckBox(str_checkNames[i]); //
				checkBoxs[i].setOpaque(false); //
				checkBoxs[i].setFocusable(false); //
				if (str_myCorpType != null && str_myCorpType.startsWith(str_checkNames[i])) { //����ҵĻ����������������Ϊ��ͷ��!˵������������һ���е�!
					checkBoxs[i].setSelected(true); //
					isFindMycorp = true; //
				}
				checkBoxs[i].addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent _event) {
						onFilterDealPoolByBlDeptName(histBillList, checkBoxs); //
					}
				}); //
				checkPanel.add(checkBoxs[i]); //
			}
			histBillList.getCustomerNavigationJPanel().add(checkPanel); //
			if (isFindMycorp) { //���ƥ�����ҵ�������,��ֱ�Ӽ���!
				onFilterDealPoolByBlDeptName(histBillList, checkBoxs); //
			}
		}
		return histBillList; //
	}

	/**
	 * ��������ͼ�еĲ������ƹ������̴������
	 * @param histBillList
	 * @param _checkBoxs
	 */
	protected void onFilterDealPoolByBlDeptName(BillListPanel histBillList, JCheckBox[] _checkBoxs) {
		HashSet hst_name = new HashSet(); //
		for (int i = 0; i < _checkBoxs.length; i++) {
			if (_checkBoxs[i].isSelected()) { //����Ѿ���ѡ��
				hst_name.add(_checkBoxs[i].getText()); //
			}
		}
		BillVO[] oldBillVOs = (BillVO[]) histBillList.getClientProperty("OldAllBillVOs"); //
		if (oldBillVOs == null) { //
			oldBillVOs = histBillList.getAllBillVOs(); //
			histBillList.putClientProperty("OldAllBillVOs", oldBillVOs); //
		}
		if (hst_name.size() == 0 || hst_name.size() == _checkBoxs.length) { //���ȫ����ѡ��ȫ����ѡ,���������!
			histBillList.clearTable(); //
			histBillList.addBillVOs(oldBillVOs); //
		} else { //
			histBillList.clearTable(); //���������
			for (int i = 0; i < oldBillVOs.length; i++) { //ѭ������!
				String str_bldept = oldBillVOs[i].getStringValue("curractivity_bldeptname"); //
				if (hst_name.contains(str_bldept)) { //��һ���Ǳ���ѡ�ϵ�
					int li_newRow = histBillList.addEmptyRow(false, false); //
					histBillList.setBillVOAt(li_newRow, oldBillVOs[i]); //
				}
			}
		}
		setBackgroundBySameDept(histBillList); //����ͬ�Ĳ��ŵļ�¼�ı���ɫŪ��һ��!�γ�һ�����Ч��!!
		histBillList.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT); //
	}

	//curractivity_wfname
	private void setBackgroundBySameDept(BillListPanel _billList) {
		String[] str_colors = new String[] { "E1FAFF", "FFE4DD", "E8D7FF", "D7FED3", "F3F3F3", "FFFFD9", "C6FF8C", "FFB38E", "CC9999", "53FF53" }; //
		HashMap colorMap = new HashMap(); //
		for (int i = 0; i < _billList.getRowCount(); i++) {
			String str_deptName = _billList.getRealValueAtModel(i, "participant_userdeptname"); //��������
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

	/**
	 * �߶���  ��ȱ��һ��ҳǩ���Ҷ��ڱ����̶����¼ �Ҷ��������Ҫ������ʾ
	 * @return
	 */
	public void dealCDB(Container _parent, final String processName, String wf_instanceid) throws Exception {
		if (wf_instanceid == null || "".equals(wf_instanceid)) {
			MessageBox.show(_parent, "����δ����û����Ҫ�߶������Ա!");
			return;
		}
		wf_instanceid = UIUtil.getStringValueByDS(null, " select rootinstanceid from pub_wf_prinstance where id = '" + wf_instanceid + "' ");
		if (wf_instanceid == null || "".equals(wf_instanceid)) {
			MessageBox.show(_parent, "���������Ϣ�ѱ�ɾ��!����ȷ��!");
			return;
		}
		String[][] wfi = UIUtil.getStringArrayByDS(null, " select status, currowner, curractivityname from pub_wf_prinstance where id = '" + wf_instanceid + "' ");
		if (wfi == null || wfi.length < 1) {
			MessageBox.show(_parent, "���������Ϣ�ѱ�ɾ��!����ȷ��!");
			return;
		}
		String[][] undealBill = null;
		if ("END".equals(wfi[0][0])) {//�������Ѿ����� ҲӦ�õ��� ���Բ鿴�߶���ļ�¼
			MessageBox.show(_parent, "��������Ѿ�����!����ȷ��!");
			return;
		} else {
			undealBill = UIUtil.getStringArrayByDS(null, " select t1.dealuser,t1.dealusername,t1.dealusercorpname, t3.curractivityname, t3.curractivity from pub_task_deal t1 left join pub_wf_dealpool t2 on t2.id = t1.prdealpoolid left join pub_wf_prinstance t3 on t1.prinstanceid=t3.id where t1.rootinstanceid='" + wf_instanceid
					+ "' and t2.issubmit='N' and t1.isccto='N' and t2.isprocess='N'  order by t3.curractivityname desc ");
		}
		final BillDialog bd = new BillDialog(_parent, "�߶���");
		DefaultTMO tmo = new DefaultTMO("δ����������Ա", new String[][] { { "��������", "��������", "250", "Y" }, { "δ������", "δ������", "100", "Y" }, { "δ������ID", "δ������ID", "100", "N" }, { "��������", "��������", "100", "Y" }, { "�ֻ���", "�ֻ���", "100", "Y" } });
		final BillListPanel bl = new BillListPanel(tmo);
		bl.setCanShowCardInfo(false);
		bl.setRowNumberChecked(true);
		bl.getTitleLabel().setFont(LookAndFeel.font);
		if (undealBill != null && undealBill.length > 0) {
			for (int i = 0; i < undealBill.length; i++) {
				if (undealBill[i][0] != null && !"".equals(undealBill[i][0])) {
					String mobile = UIUtil.getStringValueByDS(null, " select mobile from pub_user where id = '" + undealBill[i][0] + "' ");
					HashMap _values = new HashMap();
					if (undealBill[i][3] == null || "".equals(undealBill[i][3])) {
						String[][] activityname = UIUtil.getStringArrayByDS(null, "select wfname, belongdeptgroup from pub_wf_activity where id='" + undealBill[i][4] + "' ");
						if (activityname != null && activityname.length > 0) {
							if (activityname[0][1] != null && !"".equals(activityname[0][1])) {
								_values.put("��������", activityname[0][1] + "-" + activityname[0][0]);
							} else {
								_values.put("��������", activityname[0][0]);
							}
						}
					} else {
						_values.put("��������", undealBill[i][3]);
					}
					_values.put("δ������", undealBill[i][1]);
					_values.put("δ������ID", undealBill[i][0]);
					if (undealBill[i][2] == null || "".equals(undealBill[i][2])) {
						String corpname = getUserCorp(undealBill[i][0]);
						if (corpname != null && !"".equals(corpname)) {
							_values.put("��������", corpname);
						}
					} else {
						_values.put("��������", undealBill[i][2]);
					}
					_values.put("�ֻ���", mobile);
					bl.insertRowWithInitStatus(-1, _values);
					bl.setCheckedRow(bl.getRowCount() - 1, true);
				}
			}
		}
		JPanel tmpPanel = new JPanel(new BorderLayout());
		tmpPanel.setBackground(LookAndFeel.defaultShadeColor1);
		tmpPanel.setUI(new WLTPanelUI(BackGroundDrawingUtil.HORIZONTAL_FROM_MIDDLE, Color.WHITE, false));
		JPanel panel = new WLTPanel(new BorderLayout(0, 5));
		panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 15, 0)); //
		panel.setBackground(LookAndFeel.cardbgcolor);
		panel.setOpaque(false);
		tmpPanel.add(bl, BorderLayout.NORTH);
		JLabel label = new JLabel("���������", JLabel.LEFT);
		label.setFont(new Font("System", Font.PLAIN, 12));
		label.setForeground(new Color(149, 149, 255));
		label.setOpaque(false);
		panel.add(label, BorderLayout.NORTH);
		final WLTTextArea textarea_msg = new WLTTextArea(10, 50);
		textarea_msg.setText(processName + "������δ��������������ʱ����");
		textarea_msg.setLineWrap(true);
		textarea_msg.setForeground(LookAndFeel.inputforecolor_enable);
		textarea_msg.setBackground(LookAndFeel.inputbgcolor_enable);
		panel.add(new JScrollPane(textarea_msg), BorderLayout.CENTER);
		panel.setPreferredSize(new Dimension(600, 200));
		JLabel ifsendMsg = new JLabel("���Ƿ��Ͷ���", JLabel.LEFT);
		ifsendMsg.setFont(new Font("System", Font.PLAIN, 12));
		ifsendMsg.setForeground(new Color(149, 149, 255));
		ifsendMsg.setOpaque(false);
		final JCheckBox jb = new JCheckBox();
		final boolean ischeck = getTBUtil().getSysOptionBooleanValue("�߶����Ƿ��з����Ź�ѡ��", false);
		if (ischeck) {
			jb.setSelected(true);
			jb.setOpaque(false);
			panel.add(new HFlowLayoutPanel(new JComponent[] { ifsendMsg, jb }), BorderLayout.SOUTH);
		}
		HFlowLayoutPanel hflow = new HFlowLayoutPanel(new JComponent[] { panel });
		hflow.setOpaque(false);
		tmpPanel.add(hflow, BorderLayout.CENTER);
		JPanel btnpanel = new JPanel(new FlowLayout());
		btnpanel.setOpaque(false);
		WLTButton confirm = new WLTButton("ȷ��");
		WLTButton cancel = new WLTButton("ȡ��");
		btnpanel.add(confirm);
		btnpanel.add(cancel);
		confirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BillVO[] checkedvos = bl.getCheckedBillVOs();
				String senduserids = "";
				boolean ifhavenullMb = false;
				if (checkedvos == null || checkedvos.length <= 0) {
					MessageBox.show(bd, "��ѡ����Ҫ�������Ա!");
					return;
				} else {
					StringBuffer receiveids = new StringBuffer();
					for (int i = 0; i < checkedvos.length; i++) {
						if (checkedvos[i].getStringValue("δ������ID") != null && !"".equals(checkedvos[i].getStringValue("δ������ID"))) {
							receiveids.append(checkedvos[i].getStringValue("δ������ID") + ";");
						}
						if (checkedvos[i].getStringValue("�ֻ���") == null || "".equals(checkedvos[i].getStringValue("�ֻ���"))) {
							ifhavenullMb = true;
						}
					}
					if (receiveids.length() > 0) {
						senduserids = ";" + receiveids.toString();
					}
				}
				if (textarea_msg.getText() == null || "".equals(textarea_msg.getText().trim())) {
					MessageBox.show(bd, "����д�������!");
					return;
				} else {
					try {
						String zfj = getTBUtil().getSysOptionStringValue("���ݿ��ַ���", "GBK");//��ǰֻ�ж�GBK�������UTF-8��һ������ռ�����ֽڣ���У�����ͨ�������������ݿⱨ�������øò��������/2016-04-26��
						int li_length = textarea_msg.getText().getBytes(zfj).length;
						if (li_length > 3000) {
							if ("UTF-8".equalsIgnoreCase(zfj)) {//����Ӧ�ý�����ռ���ֽڵ��ַ����оٳ���
								MessageBox.show(bd, "����������ܳ���1000����!");
							} else {
								MessageBox.show(bd, "����������ܳ���1500����!");
							}
							return;
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				ArrayList msgs = new ArrayList();
				int msgtype = -1;
				if (ifhavenullMb && ischeck && jb.isSelected()) {
					msgtype = MessageBox.showOptionDialog(bd, "ѡ�����Ա�����ֻ���Ϊ�յ����,��ȷ��������?", "��ʾ", new String[] { "ȷ��", "ȡ��" }, 0);
				} else {
					msgtype = MessageBox.showOptionDialog(bd, "��ȷ��������?", "��ʾ", new String[] { "ȷ��", "ȡ��" }, 0);
				}
				if (msgtype == -1 || msgtype == 1) {
					return;
				}
				try {
					MsgVO msg = new MsgVO();
					msg.setMsgtype("�߶���");
					msg.setSender(ClientEnvironment.getCurrSessionVO().getLoginUserId());
					msg.setSendercorp(ClientEnvironment.getCurrLoginUserVO().getBlDeptId());
					msg.setIsdelete("N");
					msg.setFunctiontype("sysmsg");
					msg.setMsgcontent(textarea_msg.getText());
					msg.setMsgtitle(processName + "-�������߶���");
					msg.setReceiver(senduserids);
					msg.setCreatetime(getTBUtil().getCurrTime());
					if (ischeck && !jb.isSelected()) {
						msg.setState("��������");
					}
					msgs.add(msg);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				if (new MyMsgUIUtil().commSendMsg(msgs)) {
					if (ischeck && jb.isSelected()) {
						MessageBox.show(bd, "���ͳɹ�!�ڽ������������ĵ�δ����Ϣ�������ѷ���Ϣ�п��Բ鿴����������Ϣ!ϵͳͬʱ��������˷��Ͷ�����Ϣ!");
					} else {
						MessageBox.show(bd, "���ͳɹ�!�ڽ������������ĵ�δ����Ϣ�������ѷ���Ϣ�п��Բ鿴����������Ϣ!");
					}
					bd.dispose();
				} else {
					MessageBox.show(bd, "����ʧ��!");
					bd.dispose();
				}
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bd.dispose();
			}
		});
		tmpPanel.add(btnpanel, BorderLayout.SOUTH);
		bd.setSize(620, 500);
		bd.add(tmpPanel);
		bd.locationToCenterPosition();
		bd.setVisible(true);
	}

	private String getUserCorp(String _loginUserid) throws Exception {
		String str_myCorpId = null;
		String str_myCorpName = null;
		HashVO[] hvs_myCorpInfo = UIUtil.getHashVoArrayByDS(null, "select t1.userid,t1.userdept,t1.isdefault,t2.corptype,t2.DISPATCHNAME userdeptname,t2.extcorptype from pub_user_post t1 left join pub_corp_dept t2 on t1.userdept=t2.id where t1.userid='" + _loginUserid + "'"); //�ҳ��ҵ����ڻ���!!
		if (hvs_myCorpInfo != null && hvs_myCorpInfo.length > 0) {
			for (int i = 0; i < hvs_myCorpInfo.length; i++) {
				if ("Y".equals(hvs_myCorpInfo[i].getStringValue("isdefault"))) {
					str_myCorpId = hvs_myCorpInfo[i].getStringValue("userdept");
					str_myCorpName = hvs_myCorpInfo[i].getStringValue("userdeptname");
					break;
				}
			}
			if (str_myCorpId == null) {
				str_myCorpId = hvs_myCorpInfo[0].getStringValue("userdept");
				str_myCorpName = hvs_myCorpInfo[0].getStringValue("userdeptname");
			}
		}
		HashMap a = UIUtil.getCommonService().getTreePathNameByRecords(null, "pub_corp_dept", "id", "name", "parentid", new String[] { str_myCorpId });
		if (a != null && a.size() > 0) {
			return (String) a.get(str_myCorpId);
		}
		return "";
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

	//���㵱ǰ��������,�����漰�����ž���,�����̻���,Ҫ������Щ��Ϣ!!
	public String getWFActivityName(HashVO _hvo) {
		String str_FromParentWFActivityName = _hvo.getStringValue("prinstanceid_fromparentactivityName"); //�����̵Ļ�������
		String str_curractivityBLDeptName = _hvo.getStringValue("curractivity_belongdeptgroup"); //��ǰ���ڵľ���
		String str_curractivityName = _hvo.getStringValue("curractivity_wfname"); //��������
		StringBuilder sb_text = new StringBuilder(); //
		//sb_text.append("[");
		if (str_FromParentWFActivityName != null && !str_FromParentWFActivityName.trim().equals("")) { //��������̵Ļ��ڲ�Ϊ��,�����Ǹ�������,����Ҫ���¸����̵���Դ��������!
			sb_text.append(str_FromParentWFActivityName + "-"); //
		}
		if (str_curractivityBLDeptName != null && !str_curractivityBLDeptName.trim().equals("")) { //��������Ĳ��ž���Ϊ��!
			sb_text.append(str_curractivityBLDeptName + "-"); //
		}
		sb_text.append(str_curractivityName); //Ӧ��ҵ��ʯ褵�ǿ��Ҫ����ϵ�ǰ����,������֪��������һ��!!��������Ҳ���е����! ֻ���������е���!
		//sb_text.append("]");
		String str_return = sb_text.toString();
		str_return = getTBUtil().replaceAll(str_return, "\r", ""); //
		str_return = getTBUtil().replaceAll(str_return, "\n", ""); //
		return str_return; //
	}

	/**
	 * ���ƹ�����
	 * @param _hvo
	 * @param _old_flowid
	 * @param _type
	 * @throws Exception
	 */
	public void CopyFlow(HashVO _hvo, String _old_flowid, int _type) throws Exception {
		getWorkFlowService().CopyFlow(_hvo, _old_flowid, _type);
	}

	//�ݸ�������Ҫ���ص��ֶ�,��ʵ�������е��ֶ�(13��)!
	public String[] getDraftTaskHiddenFields() {
		return new String[] { "task_curractivityname", "task_creatername", "task_createtime", "task_createrdealmsg", "task_realdealusername", "task_dealtime", "task_dealmsg", "prins_curractivityname", "prins_lastsubmitername", "prins_lastsubmittime", "prins_lastsubmitmsg", "prins_mylastsubmittime", "prins_mylastsubmitmsg" }; //
	}

	//����������������ص��ֶ�!
	public String[] getDealTaskHiddenFields() {
		return new String[] { "task_realdealusername", "task_dealtime", "task_dealmsg", "prins_curractivityname", "prins_lastsubmitername", "prins_lastsubmittime", "prins_lastsubmitmsg", "prins_mylastsubmittime", "prins_mylastsubmitmsg" };
	}

	//����������Ҫ��ʾ���ֶ�!
	public String[] getDealTaskShowFields() {
		String[][] str_data = getDealTaskShowFieldNames(); //
		String[] str_rt = new String[str_data.length]; //
		for (int i = 0; i < str_rt.length; i++) {
			str_rt[i] = str_data[i][0];
		}
		return str_rt; //
	}

	//����������Ҫ��ʾ���ֶ�!
	public String[][] getDealTaskShowFieldNames() {
		return new String[][] { { "task_curractivityname", "��ǰ����", "�ı���", "120", "400", "Y" }, //
				{ "task_creatername", "�ύ��", "�ı���", "75", "140", "Y" },//
				{ "task_createtime", "�ύʱ��", "�ı���", "120", "138", "N" }, // 
				{ "task_createrdealmsg", "�ύ���", "�����ı���", "135", "400*100", "Y" } }; //
	}

	//����������������ص��ֶ�!
	public String[] getOffTaskHiddenFields() {
		return new String[] { "task_creatername", "task_createtime", "task_createrdealmsg", "prins_curractivityname", "prins_lastsubmitername", "prins_lastsubmittime", "prins_lastsubmitmsg", "prins_mylastsubmittime", "prins_mylastsubmitmsg" };
	}

	//����������Ҫ��ʾ���ֶ�!
	public String[] getOffTaskShowFields() {
		String[][] str_data = getOffTaskShowFieldNames(); //
		String[] str_rt = new String[str_data.length]; //
		for (int i = 0; i < str_rt.length; i++) {
			str_rt[i] = str_data[i][0];
		}
		return str_rt; //
	}

	//����������Ҫ��ʾ���ֶ�!
	public String[][] getOffTaskShowFieldNames() {
		return new String[][] { { "task_curractivityname", "��ǰ����", "�ı���", "120", "400", "Y" }, //
				{ "task_realdealusername", "������", "�ı���", "75", "140", "Y" }, //
				{ "task_dealtime", "����ʱ��", "�ı���", "120", "138", "N" }, //
				{ "task_dealmsg", "�������", "�����ı���", "135", "400*100", "Y" } }; //
	}

	/**
	 * ���ɴ��������SQL,��Ϊ��ҳ����ģ���ж����õ�,���Է�װ������!!
	 * �Ժ����ﻹҪ��������ʵ����pub_wf_prinstance,��ѯ����ǰ������һ�׶�,�����ĸ���!��ǰ������״̬!!!
	 * @param _tableName
	 * @param _pkName
	 * @param _loginUserId
	 * @param _appendCondition
	 * @return
	 */
	public String getDealTaskSQL(String _templetCode, String _queryTableName, String _savedTableName, String _pkName, String _loginUserId, String _appendCondition, boolean _isSuperShow) {
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("select "); //
		sb_sql.append("t1.*,"); //getCommColumns(1)��Ϊ���������Ѱ����е��������һ����,�������˸������õķ���
		sb_sql.append("t2.* ");
		sb_sql.append("from v_pub_task_deal t1 left join " + _queryTableName + " t2 on t2." + _pkName + "=t1.task_pkvalue "); //�Ӳ�ѯ��ȡ
		sb_sql.append("where 1=1 ");
		sb_sql.append("and t1.task_templetcode='" + _templetCode + "' "); //�����Ƕ�Ӧ��ģ��,��Ϊ�������������ܵ�,�����һ�ű�,��ģ�岻һ����
		sb_sql.append("and t1.task_tabname='" + _savedTableName + "' "); //���������Ǳ����!
		if (!_isSuperShow) {
			sb_sql.append("and (t1.task_dealuser='" + _loginUserId + "' or t1.task_accruserid='" + _loginUserId + "') "); //�����Ǳ��˵���Ϣ,���ܻ�������Ȩ��!!
		}
		if (_appendCondition != null) {
			sb_sql.append(_appendCondition); //
		}
		sb_sql.append(" order by t1.task_createtime desc");
		return sb_sql.toString(); //
	}

	/**
	 * �����Ѱ������SQL,��Ϊ��ҳ����ģ���ж����õ�,���Է�װ������!!
	 * �Ժ����ﻹҪ��������ʵ����pub_wf_prinstance,��ѯ����ǰ������һ�׶�,�����ĸ���!��ǰ������״̬!!!
	 * @param _queryTableName
	 * @param _savedTableName
	 * @param _pkName
	 * @param _loginUserId
	 * @param _appendCondition
	 * @return
	 */
	public String getOffTaskSQL(String _templetCode, String _queryTableName, String _savedTableName, String _pkName, String _loginUserId, String _appendCondition, boolean _isSuperShow) {
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("select "); //
		//sb_sql.append("t1.task_taskoffid,"); //�Ѱ�������
		sb_sql.append("t1.*,"); //getCommColumns(2)��Ϊ���������Ѱ����е��������һ����,�������˸������õķ���
		sb_sql.append("t2.* ");
		sb_sql.append("from v_pub_task_off t1 left join " + _queryTableName + " t2 on t2." + _pkName + "=t1.task_pkvalue "); //�Ӳ�ѯ��ȡ

		//�Ѱ�������ͬ����ϲ� �����/2013-03-08��
		if (tbUtil.getSysOptionBooleanValue("�Ѱ�����ͬһ�����Ƿ�ֻ��ʾ���һ��", false)) {
			sb_sql.append(" inner join ( select task_pkvalue, max(task_dealtime) task_dealtime from v_pub_task_off where 1=1 ");
			if (!_isSuperShow) {
				sb_sql.append(" and (task_realdealuser='" + _loginUserId + "' or task_accruserid='" + _loginUserId + "') "); //ʵ�ʴ����˱����Ǳ��˴����,��������ϼ�����Ȩ��Ҳ���Բ鿴!
			}
			sb_sql.append(" group by task_pkvalue ) t3 on t1.task_pkvalue=t3.task_pkvalue and t1.task_dealtime=t3.task_dealtime ");
		}

		sb_sql.append("where 1=1 "); //
		sb_sql.append("and t1.task_templetcode='" + _templetCode + "' "); //�����Ƕ�Ӧ��ģ��,��Ϊ�������������ܵ�,�����һ�ű�,��ģ�岻һ����
		sb_sql.append("and t1.task_tabname='" + _savedTableName + "' "); //���������Ǳ����
		if (!_isSuperShow) {
			sb_sql.append("and (t1.task_realdealuser='" + _loginUserId + "' or t1.task_accruserid='" + _loginUserId + "') "); //ʵ�ʴ����˱����Ǳ��˴����,��������ϼ�����Ȩ��Ҳ���Բ鿴!
		}
		if (_appendCondition != null) {
			sb_sql.append(_appendCondition); //
		}
		sb_sql.append(" order by t1.task_dealtime desc"); //
		return sb_sql.toString(); //
	}

	public String getBDTaskSQL(String _templetCode, String _queryTableName, String _savedTableName, String _pkName, String dbinsql, String _appendCondition, boolean _isSuperShow) {
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("select "); //
		sb_sql.append("t1.*,"); //getCommColumns(1)��Ϊ���������Ѱ����е��������һ����,�������˸������õķ���
		sb_sql.append("t2.* ");
		sb_sql.append("from v_pub_task_deal t1 left join " + _queryTableName + " t2 on t2." + _pkName + "=t1.task_pkvalue "); //�Ӳ�ѯ��ȡ
		sb_sql.append("where 1=1 ");
		sb_sql.append("and t1.task_templetcode='" + _templetCode + "' "); //�����Ƕ�Ӧ��ģ��,��Ϊ�������������ܵ�,�����һ�ű�,��ģ�岻һ����
		sb_sql.append("and t1.task_tabname='" + _savedTableName + "' "); //���������Ǳ����!
		if (!_isSuperShow) {
			sb_sql.append("and (t1.task_dealuser in (" + dbinsql + ") ) "); //ʵ�ʴ����˱����Ǳ��˴����,��������ϼ�����Ȩ��Ҳ���Բ鿴!// or t1.task_accruserid in (" + dbinsql + ")
		}
		if (_appendCondition != null) {
			sb_sql.append(_appendCondition); //
		}
		sb_sql.append(" order by t1.task_createtime desc"); //
		return sb_sql.toString(); //
	}

	/**
	 * ȡ�����̷����ж����html��word��������,�︻���������������! 
	 */
	public VectorMap getAllReport(String str_billtype, String str_busitype, String tablename) {
		String str_sql = "select id,processid,htmlreport,wordreport from pub_workflowassign where billtypecode='" + str_billtype + "' and busitypecode='" + str_busitype + "'"; //
		VectorMap reportl = new VectorMap();
		String[] allRoles = ClientEnvironment.getCurrLoginUserVO().getAllRoleCodes();
		try {
			HashVO[] hvo_report = UIUtil.getHashVoArrayByDS(null, str_sql);//����������ֻ��һ����¼
			if (hvo_report != null && hvo_report.length > 0) {
				String htmlrdesc = hvo_report[0].getStringValue("htmlreport"); //html��ʽ
				String wordrdesc = hvo_report[0].getStringValue("wordreport"); //word��ʽ
				if (htmlrdesc != null && !"".equals(htmlrdesc.trim())) {
					if (htmlrdesc.indexOf("=>") >= 0) {//����
						HashMap param = new HashMap();
						param.put("��������", "ȫ���������");
						param.put("html�ļ���", htmlrdesc);
						param.put("showStyle", "2");
						param.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
						HashMap tmpMap = getTBUtil().convertStrToMapByExpress(htmlrdesc, ";", "=>", true); //
						String str_savetableName = tablename; //����ı���!
						if (tmpMap.containsKey(str_savetableName.toLowerCase())) { //����ҵ�
							param.put("html�ļ���", (String) tmpMap.get(str_savetableName.toLowerCase()));
						} else if (tmpMap.containsKey("*")) {
							param.put("html�ļ���", (String) tmpMap.get("*"));
						} else {
							param.put("html�ļ���", "");
						}
						reportl.put("ȫ���������", param);
					} else if (htmlrdesc.indexOf("=>") < 0 && htmlrdesc.indexOf("=") >= 0) {//���߼�
						String[] reports = getTBUtil().split(htmlrdesc, ";");
						if (reports != null && reports.length > 0) {
							for (int i = 0; i < reports.length; i++) {
								HashMap key_value = getTBUtil().convertStrToMapByExpress(reports[i], ",", "=");
								if (!key_value.containsKey("����") || key_value.get("����").toString().toUpperCase().equals(tablename.toUpperCase())) {
									if (key_value.get("�����ɫ") != null) {
										//Ȩ�޶���
										String rrights = key_value.get("�����ɫ").toString();
										HashMap norightmap = new HashMap();
										if (!"".equals(rrights.trim())) {
											String[] keys = getTBUtil().split(rrights.trim(), "&");
											if (keys != null && keys.length > 0) {
												if (getTBUtil().getInterSectionFromTwoArray(keys, allRoles).length <= 0) {
													key_value.put("�Ƿ�����", "N");
												}
											}
										}
									}
									reportl.put(key_value.get("��������"), key_value);
								}
							}
						}
					} else { //
						String[] reports = getTBUtil().split(htmlrdesc, ";");
						HashMap param = new HashMap();
						param.put("��������", "ȫ���������");
						param.put("html�ļ���", reports[0]);
						param.put("showStyle", "2");
						param.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
						reportl.put("ȫ���������", param);
					}
				} else if (wordrdesc != null && !"".equals(wordrdesc.trim())) {//word���߼�����
					HashMap param = new HashMap();
					param.put("��������", "ȫ���������");
					param.put("word�ļ���", wordrdesc);
					param.put("showStyle", "2");
					param.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
					reportl.put("ȫ���������", param);
				} else {
					HashMap param = new HashMap();
					param.put("��������", "ȫ���������");
					param.put("showStyle", "2");
					param.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
					reportl.put("ȫ���������", param);
				}
			}
			if (ClientEnvironment.getCurrLoginUserVO().getCode().equals("admin")) {
				HashMap param = new HashMap();
				param.put("showType", "3");
				param.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
				if (reportl.containsKey("ȫ���������")) {
					param.put("html�ļ���", ((HashMap) reportl.get("ȫ���������")).get("html�ļ���"));
					param.put("word�ļ���", ((HashMap) reportl.get("ȫ���������")).get("word�ļ���"));
				}
				String name = UIUtil.getLanguage("���������ʷ�汾");
				param.put("��������", name);
				reportl.put(name, param);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reportl;
	}

}
