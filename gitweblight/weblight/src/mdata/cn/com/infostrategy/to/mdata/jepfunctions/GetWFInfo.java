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
 * 取得工作流相关信息,只限于在客户端使用!!即根据面板上的wfprinstanceid的值去取得流程实例主键,然后取得流程编码,当前环节,当前操作人,历史提交人!
 * @author xch
 *
 */
public class GetWFInfo extends PostfixMathCommand {
	private int callType = -1; //

	//UI传入的参数
	private BillPanel billPanel = null;
	private boolean isNumber = false;

	//BS传入的参数
	private HashMap colDataTypeMap = null; //一行数据!!
	private HashMap rowDataMap = null; //一行数据!!

	private String CURRPROCESS_CODE = "当前流程编码"; //
	private String CURRPROCESS_NAME = "当前流程名称"; //
	private String CURRPROCESS_STATU = "当前流程状态"; //
	private String CURRACTIVITY_CODE = "当前环节编码"; //
	private String CURRACTIVITY_NAME = "当前环节名称"; //

	private String CURROWNER_CODE = "当前处理人编码"; //
	private String CURROWNER_NAME = "当前处理人名称"; //

	private String LASTSUBMITER_CODE = "最后提交人编码"; //
	private String LASTSUBMITER_NAME = "最后提交人名称"; //

	private String HISTDEALER_CODE = "历史处理人编码"; //
	private String HISTDEALER_NAME = "历史处理人名称"; //

	/**
	 * 取得某一项的值!!
	 */
	public GetWFInfo(BillPanel _billPanel, boolean _isnumber) {
		numberOfParameters = 1; //只有一个参数!!!其实就是一个类型说明,是字符串!!!
		this.billPanel = _billPanel; ////
		this.isNumber = _isnumber; //是否是数字
		callType = WLTConstants.JEPTYPE_UI; //客户端调用
	}

	/**
	 * 在服务器端调用的构造方法
	 * @param dataMap
	 */
	public GetWFInfo(HashMap _dataMap) {
		numberOfParameters = 1; //
		this.rowDataMap = _dataMap; //
		callType = WLTConstants.JEPTYPE_BS; //客户端调用
	}

	/**
	 * 真正的逻辑执行!!
	 */
	public void run(Stack inStack) throws ParseException {
		//先取得参数!!
		checkStack(inStack);
		Object param_1 = inStack.pop();
		String str_type = (String) param_1; ////
		String str_value = "";
		try {
			if (callType == WLTConstants.JEPTYPE_UI) { //如果是客户端调用
				if (billPanel instanceof BillCardPanel) { //如果是卡片
					BillCardPanel cardPanel = (BillCardPanel) billPanel; //
					String str_wfpfinstanceid = cardPanel.getRealValueAt("wfprinstanceid"); //取得流程实例,必须有值才行!!
					if (str_wfpfinstanceid == null || str_wfpfinstanceid.trim().equals("")) { //如果流程实例不为空!!
						str_value = "流程未启动"; //
					} else {
						str_value = getRealValue(str_wfpfinstanceid, str_type); //
					}
				}
			} else if (callType == WLTConstants.JEPTYPE_BS) { ////
				String str_wfpfinstanceid = "" + this.rowDataMap.get("wfprinstanceid"); //
				if (str_wfpfinstanceid.equals("") || str_wfpfinstanceid.equalsIgnoreCase("null")) { //如果不为空
					str_value = "流程未启动"; //
				} else {
					str_value = getRealValue(str_wfpfinstanceid, str_type); //
				}
			}
		} catch (Exception e) {
			e.printStackTrace(); //
		}
		inStack.push(str_value); //送入值
	}

	//取得实际数据
	private String getRealValue(String _prinstanceid, String _parType) throws Exception {
		String str_returnValue = null; //返回值.
		HashVO[] hvs = getHashVOsBySQL(getSQL(_prinstanceid)); //
		if (hvs.length > 0) {
			if (_parType.equals(CURRPROCESS_CODE)) { //如果是流程编码
				HashVO[] hvs_process = getHashVOsBySQL("select code from pub_wf_process where id='" + hvs[0].getStringValue("processid") + "'"); ////
				if (hvs_process.length > 0) {
					str_returnValue = hvs_process[0].getStringValue("code"); //
				}
			} else if (_parType.equals(CURRPROCESS_NAME)) { //如果是当前流程名称
				HashVO[] hvs_process = getHashVOsBySQL("select name from pub_wf_process where id='" + hvs[0].getStringValue("processid") + "'"); ////
				if (hvs_process.length > 0) {
					str_returnValue = hvs_process[0].getStringValue("name"); //
				}
			} else if (_parType.equals(CURRPROCESS_STATU)) { //如果是当前流程状态
				str_returnValue = hvs[0].getStringValue("status"); //当前状态!!!
				if (str_returnValue.equalsIgnoreCase("START")) {
					str_returnValue = "流程已启动"; //
				} else if (str_returnValue.equalsIgnoreCase("RUN")) {
					str_returnValue = "审批中..."; //
				} else if (str_returnValue.equalsIgnoreCase("END")) {
					str_returnValue = "流程已结束"; //
				}
			} else if (_parType.equals(CURRACTIVITY_CODE)) { //如果是当前环节编码
				if (hvs[0].getStringValue("curractivity") != null && !hvs[0].getStringValue("curractivity").trim().equals("")) {
					HashVO[] hvs_activity = getHashVOsBySQL("select code from pub_wf_activity where id='" + hvs[0].getStringValue("curractivity") + "'"); //
					if (hvs_activity.length > 0) {
						str_returnValue = hvs_activity[0].getStringValue("code"); //
					}
				}
			} else if (_parType.equals(CURRACTIVITY_NAME)) { //如果是当前环节编码
				if (hvs[0].getStringValue("curractivity") != null && !hvs[0].getStringValue("curractivity").trim().equals("")) {
					HashVO[] hvs_activity = getHashVOsBySQL("select uiname from pub_wf_activity where id='" + hvs[0].getStringValue("curractivity") + "'"); //
					if (hvs_activity.length > 0) {
						str_returnValue = hvs_activity[0].getStringValue("uiname"); //
					}
				}
			} else if (_parType.equals(CURROWNER_CODE)) { //如果是当前所属者编码
				if (hvs[0].getStringValue("currowner") != null && !hvs[0].getStringValue("currowner").trim().equals("")) {
					TBUtil tBUtil = new TBUtil(); //
					String[] str_userids = tBUtil.split(hvs[0].getStringValue("currowner"), ";"); ////
					HashVO[] hvs_user = getHashVOsBySQL("select code from pub_user where id in (" + tBUtil.getInCondition(str_userids) + ")"); //取得用户!!!
					String str_usercode = "";
					if (hvs_user.length > 0) {
						for (int i = 0; i < hvs_user.length; i++) {
							str_usercode = str_usercode + hvs_user[i].getStringValue("code") + ";"; //将员工编码拼出来!
						}
						str_returnValue = str_usercode; //
					}
				}
			} else if (_parType.equals(CURROWNER_NAME)) { //如果是当前所属者名称
				if (hvs[0].getStringValue("currowner") != null && !hvs[0].getStringValue("currowner").trim().equals("")) {
					TBUtil tBUtil = new TBUtil(); //
					String[] str_userids = tBUtil.split(hvs[0].getStringValue("currowner"), ";"); ////
					HashVO[] hvs_user = getHashVOsBySQL("select name from pub_user where id in (" + tBUtil.getInCondition(str_userids) + ")"); //取得用户!!!
					String str_username = "";
					if (hvs_user.length > 0) {
						for (int i = 0; i < hvs_user.length; i++) {
							str_username = str_username + hvs_user[i].getStringValue("name") + ";"; //将员工名称拼出来!
						}
						str_returnValue = str_username; //
					}
				}
			} else if (_parType.equals(LASTSUBMITER_CODE)) { //最后提交者编码
				if (hvs[0].getStringValue("submiterhist") != null && !hvs[0].getStringValue("submiterhist").trim().equals("")) {
					TBUtil tBUtil = new TBUtil(); //
					String[] str_userids = tBUtil.split(hvs[0].getStringValue("submiterhist"), ";"); ////
					HashVO[] hvs_user = getHashVOsBySQL("select code from pub_user where id ='" + str_userids[str_userids.length - 1] + "'"); //取得最后一个!用户!!!
					if (hvs_user.length > 0) {
						str_returnValue = hvs_user[0].getStringValue("code"); ////
					}
				}
			} else if (_parType.equals(LASTSUBMITER_NAME)) { //最后提交者名称
				if (hvs[0].getStringValue("submiterhist") != null && !hvs[0].getStringValue("submiterhist").trim().equals("")) {
					TBUtil tBUtil = new TBUtil(); //
					String[] str_userids = tBUtil.split(hvs[0].getStringValue("submiterhist"), ";"); ////
					HashVO[] hvs_user = getHashVOsBySQL("select name from pub_user where id ='" + str_userids[str_userids.length - 1] + "'"); //取得最后一个!用户!!!
					if (hvs_user.length > 0) {
						str_returnValue = hvs_user[0].getStringValue("name"); ////
					}
				}
			} else if (_parType.equals(HISTDEALER_CODE)) { //历史提交者编码
				if (hvs[0].getStringValue("submiterhist") != null && !hvs[0].getStringValue("submiterhist").trim().equals("")) {
					TBUtil tBUtil = new TBUtil(); //
					String[] str_userids = tBUtil.split(hvs[0].getStringValue("submiterhist"), ";"); ////
					HashVO[] hvs_user = getHashVOsBySQL("select code from pub_user where id in (" + tBUtil.getInCondition(str_userids) + ")"); //取得用户!!!
					String str_username = "";
					if (hvs_user.length > 0) {
						for (int i = 0; i < hvs_user.length; i++) {
							str_username = str_username + hvs_user[i].getStringValue("code") + ";"; //将员工编码拼出来!
						}
						str_returnValue = str_username; //
					}
				}
			} else if (_parType.equals(HISTDEALER_NAME)) { //历史提交者名称
				if (hvs[0].getStringValue("submiterhist") != null && !hvs[0].getStringValue("submiterhist").trim().equals("")) {
					TBUtil tBUtil = new TBUtil(); //
					String[] str_userids = tBUtil.split(hvs[0].getStringValue("submiterhist"), ";"); ////
					HashVO[] hvs_user = getHashVOsBySQL("select name from pub_user where id in (" + tBUtil.getInCondition(str_userids) + ")"); //取得用户!!!
					String str_username = ""; ////
					if (hvs_user.length > 0) {
						for (int i = 0; i < hvs_user.length; i++) {
							str_username = str_username + hvs_user[i].getStringValue("name") + ";"; //将员工名称拼出来!
						}
						str_returnValue = str_username; ////
					}
				}
			}
		}
		return str_returnValue; //返回数据!!
	}

	private HashVO[] getHashVOsBySQL(String _sql) throws Exception {
		if (callType == WLTConstants.JEPTYPE_UI) { //如果是UI端调用..
			return UIUtil.getHashVoArrayByDS(null, _sql); //
		} else if (callType == WLTConstants.JEPTYPE_BS) { //如果是BS端调用.
			return new cn.com.infostrategy.bs.common.CommDMO().getHashVoArrayByDS(null, _sql); ///如果是服务器端,有可能在频繁取数据库时会产生性能问题!!
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
