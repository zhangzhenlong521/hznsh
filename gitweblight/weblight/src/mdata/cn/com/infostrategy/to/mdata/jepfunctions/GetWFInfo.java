package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.HashMap;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 * ȡ�ù����������Ϣ,ֻ�����ڿͻ���ʹ��!!����������ϵ�wfprinstanceid��ֵȥȡ������ʵ������,Ȼ��ȡ�����̱���,��ǰ����,��ǰ������,��ʷ�ύ��!
 * @author xch
 *
 */
public class GetWFInfo extends PostfixMathCommand {
	private int callType = -1; //

	//UI����Ĳ���
	private BillPanel billPanel = null;
	private boolean isNumber = false;

	//BS����Ĳ���
	private HashMap colDataTypeMap = null; //һ������!!
	private HashMap rowDataMap = null; //һ������!!

	private String CURRPROCESS_CODE = "��ǰ���̱���"; //
	private String CURRPROCESS_NAME = "��ǰ��������"; //
	private String CURRPROCESS_STATU = "��ǰ����״̬"; //
	private String CURRACTIVITY_CODE = "��ǰ���ڱ���"; //
	private String CURRACTIVITY_NAME = "��ǰ��������"; //

	private String CURROWNER_CODE = "��ǰ�����˱���"; //
	private String CURROWNER_NAME = "��ǰ����������"; //

	private String LASTSUBMITER_CODE = "����ύ�˱���"; //
	private String LASTSUBMITER_NAME = "����ύ������"; //

	private String HISTDEALER_CODE = "��ʷ�����˱���"; //
	private String HISTDEALER_NAME = "��ʷ����������"; //

	/**
	 * ȡ��ĳһ���ֵ!!
	 */
	public GetWFInfo(BillPanel _billPanel, boolean _isnumber) {
		numberOfParameters = 1; //ֻ��һ������!!!��ʵ����һ������˵��,���ַ���!!!
		this.billPanel = _billPanel; ////
		this.isNumber = _isnumber; //�Ƿ�������
		callType = WLTConstants.JEPTYPE_UI; //�ͻ��˵���
	}

	/**
	 * �ڷ������˵��õĹ��췽��
	 * @param dataMap
	 */
	public GetWFInfo(HashMap _dataMap) {
		numberOfParameters = 1; //
		this.rowDataMap = _dataMap; //
		callType = WLTConstants.JEPTYPE_BS; //�ͻ��˵���
	}

	/**
	 * �������߼�ִ��!!
	 */
	public void run(Stack inStack) throws ParseException {
		//��ȡ�ò���!!
		checkStack(inStack);
		Object param_1 = inStack.pop();
		String str_type = (String) param_1; ////
		String str_value = "";
		try {
			if (callType == WLTConstants.JEPTYPE_UI) { //����ǿͻ��˵���
				if (billPanel instanceof BillCardPanel) { //����ǿ�Ƭ
					BillCardPanel cardPanel = (BillCardPanel) billPanel; //
					String str_wfpfinstanceid = cardPanel.getRealValueAt("wfprinstanceid"); //ȡ������ʵ��,������ֵ����!!
					if (str_wfpfinstanceid == null || str_wfpfinstanceid.trim().equals("")) { //�������ʵ����Ϊ��!!
						str_value = "����δ����"; //
					} else {
						str_value = getRealValue(str_wfpfinstanceid, str_type); //
					}
				}
			} else if (callType == WLTConstants.JEPTYPE_BS) { ////
				String str_wfpfinstanceid = "" + this.rowDataMap.get("wfprinstanceid"); //
				if (str_wfpfinstanceid.equals("") || str_wfpfinstanceid.equalsIgnoreCase("null")) { //�����Ϊ��
					str_value = "����δ����"; //
				} else {
					str_value = getRealValue(str_wfpfinstanceid, str_type); //
				}
			}
		} catch (Exception e) {
			e.printStackTrace(); //
		}
		inStack.push(str_value); //����ֵ
	}

	//ȡ��ʵ������
	private String getRealValue(String _prinstanceid, String _parType) throws Exception {
		String str_returnValue = null; //����ֵ.
		HashVO[] hvs = getHashVOsBySQL(getSQL(_prinstanceid)); //
		if (hvs.length > 0) {
			if (_parType.equals(CURRPROCESS_CODE)) { //��������̱���
				HashVO[] hvs_process = getHashVOsBySQL("select code from pub_wf_process where id='" + hvs[0].getStringValue("processid") + "'"); ////
				if (hvs_process.length > 0) {
					str_returnValue = hvs_process[0].getStringValue("code"); //
				}
			} else if (_parType.equals(CURRPROCESS_NAME)) { //����ǵ�ǰ��������
				HashVO[] hvs_process = getHashVOsBySQL("select name from pub_wf_process where id='" + hvs[0].getStringValue("processid") + "'"); ////
				if (hvs_process.length > 0) {
					str_returnValue = hvs_process[0].getStringValue("name"); //
				}
			} else if (_parType.equals(CURRPROCESS_STATU)) { //����ǵ�ǰ����״̬
				str_returnValue = hvs[0].getStringValue("status"); //��ǰ״̬!!!
				if (str_returnValue.equalsIgnoreCase("START")) {
					str_returnValue = "����������"; //
				} else if (str_returnValue.equalsIgnoreCase("RUN")) {
					str_returnValue = "������..."; //
				} else if (str_returnValue.equalsIgnoreCase("END")) {
					str_returnValue = "�����ѽ���"; //
				}
			} else if (_parType.equals(CURRACTIVITY_CODE)) { //����ǵ�ǰ���ڱ���
				if (hvs[0].getStringValue("curractivity") != null && !hvs[0].getStringValue("curractivity").trim().equals("")) {
					HashVO[] hvs_activity = getHashVOsBySQL("select code from pub_wf_activity where id='" + hvs[0].getStringValue("curractivity") + "'"); //
					if (hvs_activity.length > 0) {
						str_returnValue = hvs_activity[0].getStringValue("code"); //
					}
				}
			} else if (_parType.equals(CURRACTIVITY_NAME)) { //����ǵ�ǰ���ڱ���
				if (hvs[0].getStringValue("curractivity") != null && !hvs[0].getStringValue("curractivity").trim().equals("")) {
					HashVO[] hvs_activity = getHashVOsBySQL("select uiname from pub_wf_activity where id='" + hvs[0].getStringValue("curractivity") + "'"); //
					if (hvs_activity.length > 0) {
						str_returnValue = hvs_activity[0].getStringValue("uiname"); //
					}
				}
			} else if (_parType.equals(CURROWNER_CODE)) { //����ǵ�ǰ�����߱���
				if (hvs[0].getStringValue("currowner") != null && !hvs[0].getStringValue("currowner").trim().equals("")) {
					TBUtil tBUtil = new TBUtil(); //
					String[] str_userids = tBUtil.split(hvs[0].getStringValue("currowner"), ";"); ////
					HashVO[] hvs_user = getHashVOsBySQL("select code from pub_user where id in (" + tBUtil.getInCondition(str_userids) + ")"); //ȡ���û�!!!
					String str_usercode = "";
					if (hvs_user.length > 0) {
						for (int i = 0; i < hvs_user.length; i++) {
							str_usercode = str_usercode + hvs_user[i].getStringValue("code") + ";"; //��Ա������ƴ����!
						}
						str_returnValue = str_usercode; //
					}
				}
			} else if (_parType.equals(CURROWNER_NAME)) { //����ǵ�ǰ����������
				if (hvs[0].getStringValue("currowner") != null && !hvs[0].getStringValue("currowner").trim().equals("")) {
					TBUtil tBUtil = new TBUtil(); //
					String[] str_userids = tBUtil.split(hvs[0].getStringValue("currowner"), ";"); ////
					HashVO[] hvs_user = getHashVOsBySQL("select name from pub_user where id in (" + tBUtil.getInCondition(str_userids) + ")"); //ȡ���û�!!!
					String str_username = "";
					if (hvs_user.length > 0) {
						for (int i = 0; i < hvs_user.length; i++) {
							str_username = str_username + hvs_user[i].getStringValue("name") + ";"; //��Ա������ƴ����!
						}
						str_returnValue = str_username; //
					}
				}
			} else if (_parType.equals(LASTSUBMITER_CODE)) { //����ύ�߱���
				if (hvs[0].getStringValue("submiterhist") != null && !hvs[0].getStringValue("submiterhist").trim().equals("")) {
					TBUtil tBUtil = new TBUtil(); //
					String[] str_userids = tBUtil.split(hvs[0].getStringValue("submiterhist"), ";"); ////
					HashVO[] hvs_user = getHashVOsBySQL("select code from pub_user where id ='" + str_userids[str_userids.length - 1] + "'"); //ȡ�����һ��!�û�!!!
					if (hvs_user.length > 0) {
						str_returnValue = hvs_user[0].getStringValue("code"); ////
					}
				}
			} else if (_parType.equals(LASTSUBMITER_NAME)) { //����ύ������
				if (hvs[0].getStringValue("submiterhist") != null && !hvs[0].getStringValue("submiterhist").trim().equals("")) {
					TBUtil tBUtil = new TBUtil(); //
					String[] str_userids = tBUtil.split(hvs[0].getStringValue("submiterhist"), ";"); ////
					HashVO[] hvs_user = getHashVOsBySQL("select name from pub_user where id ='" + str_userids[str_userids.length - 1] + "'"); //ȡ�����һ��!�û�!!!
					if (hvs_user.length > 0) {
						str_returnValue = hvs_user[0].getStringValue("name"); ////
					}
				}
			} else if (_parType.equals(HISTDEALER_CODE)) { //��ʷ�ύ�߱���
				if (hvs[0].getStringValue("submiterhist") != null && !hvs[0].getStringValue("submiterhist").trim().equals("")) {
					TBUtil tBUtil = new TBUtil(); //
					String[] str_userids = tBUtil.split(hvs[0].getStringValue("submiterhist"), ";"); ////
					HashVO[] hvs_user = getHashVOsBySQL("select code from pub_user where id in (" + tBUtil.getInCondition(str_userids) + ")"); //ȡ���û�!!!
					String str_username = "";
					if (hvs_user.length > 0) {
						for (int i = 0; i < hvs_user.length; i++) {
							str_username = str_username + hvs_user[i].getStringValue("code") + ";"; //��Ա������ƴ����!
						}
						str_returnValue = str_username; //
					}
				}
			} else if (_parType.equals(HISTDEALER_NAME)) { //��ʷ�ύ������
				if (hvs[0].getStringValue("submiterhist") != null && !hvs[0].getStringValue("submiterhist").trim().equals("")) {
					TBUtil tBUtil = new TBUtil(); //
					String[] str_userids = tBUtil.split(hvs[0].getStringValue("submiterhist"), ";"); ////
					HashVO[] hvs_user = getHashVOsBySQL("select name from pub_user where id in (" + tBUtil.getInCondition(str_userids) + ")"); //ȡ���û�!!!
					String str_username = ""; ////
					if (hvs_user.length > 0) {
						for (int i = 0; i < hvs_user.length; i++) {
							str_username = str_username + hvs_user[i].getStringValue("name") + ";"; //��Ա������ƴ����!
						}
						str_returnValue = str_username; ////
					}
				}
			}
		}
		return str_returnValue; //��������!!
	}

	private HashVO[] getHashVOsBySQL(String _sql) throws Exception {
		if (callType == WLTConstants.JEPTYPE_UI) { //�����UI�˵���..
			return UIUtil.getHashVoArrayByDS(null, _sql); //
		} else if (callType == WLTConstants.JEPTYPE_BS) { //�����BS�˵���.
			return new cn.com.infostrategy.bs.common.CommDMO().getHashVoArrayByDS(null, _sql); ///����Ƿ�������,�п�����Ƶ��ȡ���ݿ�ʱ�������������!!
		} else {
			return null;
		}
	}

	private String getSQL(String _id) {
		return "select id,processid,curractivity,currowner,status,submiterhist from pub_wf_prinstance where id='" + _id + "'"; //
	}

	public void setRowDataMap(HashMap rowDataMap) {
		this.rowDataMap = rowDataMap;
	}

	public void setColDataTypeMap(HashMap colDataTypeMap) {
		this.colDataTypeMap = colDataTypeMap;
	}
}
