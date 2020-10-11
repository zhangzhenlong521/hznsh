package cn.com.infostrategy.bs.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.bs.sysapp.DataPolicyDMO;
import cn.com.infostrategy.bs.sysapp.SysAppDMO;
import cn.com.infostrategy.to.common.HashVO;

/**
 * 计算机构的公式,是非常关键的计算!!!
 * 以后还非常容易扩展!!
 * @author xch
 *
 */
public class WorkFlowEngineGetCorpUtil {

	private WLTInitContext initContext = null; //

	/**
	 * 得到所有机构!!!!
	 * @param _maps
	 * @return
	 */
	public String[] getCorps(HashMap[] _maps, HashMap _cacheMap) {
		try {
			getInitContext().addCurrSessionCustStrInfoByKey("$环节参与者计算过程", "机构公式详细计算过程:<br>"); //
			ArrayList al_temp = new ArrayList(); //
			for (int i = 0; i < _maps.length; i++) { //遍历各个
				String str_type = (String) _maps[i].get("type"); //先取类型,这个必不可少!!!
				if (str_type == null) {
					getInitContext().addCurrSessionCustStrInfoByKey("$环节参与者计算过程", "◆定义的type为空,这是不允许的,请注意公式配置是否正确,比如不是以\"getWFCorp(\"开始的!<br>"); //
					break; //
				}
				if (str_type.equals("某种类型的机构")) { //与人无关,直接根据机构类型判断!!!
					al_temp.addAll(getOneCorpTypeCorp(_maps[i], _cacheMap)); //
				} else if (str_type.equals("流程创建人所在机构")) { //
					al_temp.addAll(getWFCreaterCorp(_maps[i], _cacheMap)); //
				} else if (str_type.equals("流程创建人所在机构的范围")) { //
					al_temp.addAll(getWFCreaterCorpArea(_maps[i], _cacheMap)); //
				} else if (str_type.equals("流程创建人某类型的上级机构")) { //流程创建人的某个上级机构!!即有的时候会根据流程创建人来计算更简洁!!★★★★★
					al_temp.addAll(getWFCreaterSomeTypeParentCorp(_maps[i], _cacheMap)); //
				} else if (str_type.equals("登录人所在机构") || str_type.equals("本机构") || str_type.equals("本机构之所有")) { //为了兼容旧的配置,以前叫"本机构""本机构之所有"的仍然有效!!
					al_temp.addAll(getLoginUserCorp(_maps[i], _cacheMap)); //
				} else if (str_type.equals("登录人所在机构的范围") || str_type.equals("本机构范围") || str_type.equals("本机构范围之所有")) { //为了兼容旧的配置,以前叫 [本机构范围] [本机构范围之所有] 的仍然有效!!
					al_temp.addAll(getLoginUserCorpArea(_maps[i], _cacheMap)); //
				} else if (str_type.equals("登录人某类型的上级机构") || str_type.equals("本人某类型的上级机构")) { //流程创建人的某个上级机构!!即有的时候会根据流程创建人来计算更简洁!!
					al_temp.addAll(getLoginUserSomeTypeParentCorp(_maps[i], _cacheMap)); //
				} else if (str_type.equals("自定义类")) {
					al_temp.addAll(getCustClassCorp(_maps[i], _cacheMap)); //自定义类!!!
				} else { //最好能有个特别提示,说明定义的类型不对!!
					getInitContext().addCurrSessionCustStrInfoByKey("$环节参与者计算过程", "◆定义的type[" + str_type + "]不对,极有可能是因为版本升级导致以前的type值无效了,只需重新配置下即可!<br>"); //
					break; //
				}
			}

			//做下唯一性过滤,因为根据不同类型计算出来的值,肯定会有重复的,否则数据量太大!!
			HashSet hst = new HashSet(); //
			for (int i = 0; i < al_temp.size(); i++) {
				hst.add(al_temp.get(i)); //
			}
			return (String[]) hst.toArray(new String[0]); //返回!!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		}
	}

	/**
	 * 取得某种类型的机构
	 * @return
	 */
	private ArrayList getOneCorpTypeCorp(HashMap _parMap, HashMap _cacheMap) throws Exception {
		String str_corptype = (String) _parMap.get("类型"); //
		String str_down2CorpType = (String) _parMap.get("二次下探的子机构类型"); //机构的扩展类型!!
		String str_down2ExtCorpType = (String) _parMap.get("二次下探的子机构扩展类型"); //机构的扩展类型!!
		String str_down2CorpName = (String) _parMap.get("二次下探的子机构名称"); //机构的名称!!
		String str_isdown2ContainChildren = (String) _parMap.get("二次下探是否包含子孙"); ////
		boolean isDown2ContainChildren = true; //默认是包含子结点的
		if ("N".equalsIgnoreCase(str_isdown2ContainChildren)) {
			isDown2ContainChildren = false; //
		}
		SysAppDMO sysDMO = new SysAppDMO(); //
		return sysDMO.getOneCorpTypeAllCorps(str_corptype, str_down2CorpType, str_down2ExtCorpType, str_down2CorpName, isDown2ContainChildren, _cacheMap); //
	}

	//取得流程创建人所在机构
	private ArrayList getWFCreaterCorp(HashMap _parMap, HashMap _cacheMap) throws Exception {
		String str_wfcreater = (String) _parMap.get("流程创建者"); //流程创建人的id!!
		String[] str_corpIdType = getCorpIdAndTypebyUerId(str_wfcreater); //
		if (str_corpIdType == null) {
			return new ArrayList(); //
		}
		String str_corpId = str_corpIdType[0]; //取得该人员的所在机构类型
		String str_corpType = str_corpIdType[1]; //取得该人员的所在机构类型
		getInitContext().addCurrSessionCustStrInfoByKey("$环节参与者计算过程", "◆直接从流程创建人的所在机构[" + str_corpId + "]进行二次下探!<br>"); //
		return getOnerCorpAllChildrenCorps(str_corpId, str_corpType, _parMap, _cacheMap); //默认不包含子结点!!!
	}

	//取得流程创建人所在机构的范围
	private ArrayList getWFCreaterCorpArea(HashMap _parMap, HashMap _cacheMap) throws Exception {
		String str_wfcreater = (String) _parMap.get("流程创建者"); //流程创建人的id!!
		String[] str_corpIdType = getCorpIdAndTypebyUerId(str_wfcreater); //
		if (str_corpIdType == null) {
			return new ArrayList(); //
		}
		String str_corpType = str_corpIdType[1]; //取得该人员的所在机构类型
		if (str_corpType.endsWith("下属机构")) {
			str_corpType = str_corpType.substring(0, str_corpType.length() - 4); //
		}
		return getOnerUserOneUp1CorpAllChildrenCorps(str_wfcreater, str_corpType, _parMap, _cacheMap); //
	}

	//取得流程创建人某种类型的上级机构!
	private ArrayList getWFCreaterSomeTypeParentCorp(HashMap _parMap, HashMap _cacheMap) throws Exception {
		String str_wfcreater = (String) _parMap.get("流程创建者"); //流程创建人的id!!
		String str_up1RootCorptype = (String) _parMap.get("首次上溯到的根机构"); //机构类型!!
		return getOnerUserOneUp1CorpAllChildrenCorps(str_wfcreater, str_up1RootCorptype, _parMap, _cacheMap); //
	}

	//取得流程创建人所在机构
	private ArrayList getLoginUserCorp(HashMap _parMap, HashMap _cacheMap) throws Exception {
		String str_loginUserID = new WLTInitContext().getCurrSession().getLoginUserId(); //这个以后要换成流程处理人,即流程任务记录中的"participant_user"字段值!!!因为在授权时,如果是代理人登录处理,这里他代表的其实是XX领导,计算逻辑权限应该按领导的计算!!!
		String[] str_corpIdType = getCorpIdAndTypebyUerId(str_loginUserID); //
		if (str_corpIdType == null) {
			return new ArrayList(); //
		}
		String str_corpId = str_corpIdType[0]; //取得该人员的所在机构
		String str_corpType = str_corpIdType[1]; //取得该人员的所在机构类型
		getInitContext().addCurrSessionCustStrInfoByKey("$环节参与者计算过程", "◆直接从登录人的所在机构[" + str_corpId + "]进行二次下探!<br>"); //
		return getOnerCorpAllChildrenCorps(str_corpId, str_corpType, _parMap, _cacheMap); //默认不包含子结点!!!
	}

	//取得登录人员所在机构的范围!!
	private ArrayList getLoginUserCorpArea(HashMap _parMap, HashMap _cacheMap) throws Exception {
		String str_loginUserID = new WLTInitContext().getCurrSession().getLoginUserId(); //这个以后要换成流程处理人,即流程任务记录中的"participant_user"字段值!!!
		String[] str_corpIdType = getCorpIdAndTypebyUerId(str_loginUserID); //
		if (str_corpIdType == null) {
			return new ArrayList(); //
		}
		String str_corpType = str_corpIdType[1]; //取得该人员的所在机构类型
		if (str_corpType.endsWith("下属机构")) {
			str_corpType = str_corpType.substring(0, str_corpType.length() - 4); //
		}
		return getOnerUserOneUp1CorpAllChildrenCorps(str_loginUserID, str_corpType, _parMap, _cacheMap); //
	}

	//取得登录人员某种类型的上级机构!!
	private ArrayList getLoginUserSomeTypeParentCorp(HashMap _parMap, HashMap _cacheMap) throws Exception {
		String str_loginUserID = new WLTInitContext().getCurrSession().getLoginUserId(); //这个以后要换成流程处理人,即流程任务记录中的"participant_user"字段值!!
		String str_up1RootCorptype = (String) _parMap.get("首次上溯到的根机构"); //机构类型!!
		return getOnerUserOneUp1CorpAllChildrenCorps(str_loginUserID, str_up1RootCorptype, _parMap, _cacheMap); //
	}

	/**
	 * 取得某个人的某种上级机构类型的的范围下的所有子机构!!!
	 * @param _userId
	 * @param _up1CorpCondition
	 * @param _parMap
	 * @return
	 * @throws Exception
	 */
	private ArrayList getOnerUserOneUp1CorpAllChildrenCorps(String _userId, String _up1CorpCondition, HashMap _parMap, HashMap _cacheMap) throws Exception {
		String str_down2CorpType = (String) _parMap.get("二次下探的子机构类型"); //机构的扩展类型!!
		String str_down2ExtCorpType = (String) _parMap.get("二次下探的子机构扩展类型"); //机构的扩展类型!!
		String str_down2CorpName = (String) _parMap.get("二次下探的子机构名称"); //机构的名称!!
		String str_isdown2ContainChildren = (String) _parMap.get("二次下探是否包含子孙"); ////
		boolean isDown2ContainChildren = true; //
		if ("N".equalsIgnoreCase(str_isdown2ContainChildren)) {
			isDown2ContainChildren = false; //
		}

		SysAppDMO sysDMO = new SysAppDMO(); //
		if (str_down2CorpName != null && str_down2CorpName.startsWith("$")) { //如果是特殊宏代码,则替换成实际名称,比如【$本部门】=>【会计结算部】
			HashVO[] hvs = sysDMO.getParentCorpVOByMacro(2, _userId, str_down2CorpName); //
			if (hvs != null && hvs.length > 0) { //
				//System.out.println("将宏代码的机构类型[" + str_down2CorpName + "]进行寻找到实际机构名称=[" + hvs[0].getStringValue("name") + "]..."); //
				str_down2CorpName = hvs[0].getStringValue("name"); //
			} else {
				str_down2CorpName = null; //
			}
		}

		return sysDMO.getOnerUserSomeTypeParentCorp(_userId, _up1CorpCondition, str_down2CorpType, str_down2ExtCorpType, str_down2CorpName, isDown2ContainChildren, _cacheMap); //找出创建人所在机构的!!!
	}

	/**
	 * 直接根据机构取得取得所有子结点!!适用于本机构!!
	 * @param _rootCorpId
	 * @param _parMap
	 * @return
	 * @throws Exception
	 */
	private ArrayList getOnerCorpAllChildrenCorps(String _rootCorpId, String _rootCorpType, HashMap _parMap, HashMap _cacheMap) throws Exception {
		String str_down2CorpType = (String) _parMap.get("二次下探的子机构类型"); //机构的扩展类型!!
		String str_down2ExtCorpType = (String) _parMap.get("二次下探的子机构扩展类型"); //机构的扩展类型!!
		String str_down2CorpName = (String) _parMap.get("二次下探的子机构名称"); //机构的名称!!
		String str_isdown2ContainChildren = (String) _parMap.get("二次下探是否包含子孙"); ////
		boolean isDown2ContainChildren = true; //
		if ("N".equalsIgnoreCase(str_isdown2ContainChildren)) {
			isDown2ContainChildren = false; //
		}
		return new DataPolicyDMO().secondDownFindAllCorpChildrensByCondition(_rootCorpId, _rootCorpType, str_down2CorpType, str_down2ExtCorpType, str_down2CorpName, false, isDown2ContainChildren, "交集", _cacheMap); //二次下探!
	}

	//根据人员id找到某人所在的机构与其类型!!
	private String[] getCorpIdAndTypebyUerId(String _userId) throws Exception {
		HashVO[] hvs_wfcreaterCorp = new CommDMO().getHashVoArrayByDS(null, "select t1.userdept,t2.corptype,t1.isdefault from pub_user_post t1,pub_corp_dept t2 where t1.userdept=t2.id and t1.userid='" + _userId + "'"); //必须是默认机构!!
		if (hvs_wfcreaterCorp == null || hvs_wfcreaterCorp.length == 0) { //如果没有
			return null; //
		}
		for (int i = 0; i < hvs_wfcreaterCorp.length; i++) {
			if ("Y".equalsIgnoreCase(hvs_wfcreaterCorp[i].getStringValue("isdefault"))) { //如果是默认部门!
				return new String[] { hvs_wfcreaterCorp[i].getStringValue("userdept"), hvs_wfcreaterCorp[i].getStringValue("corptype") }; //
			}
		}
		return new String[] { hvs_wfcreaterCorp[0].getStringValue("userdept"), hvs_wfcreaterCorp[0].getStringValue("corptype") }; //
	}

	/**
	 * 自定义类!!!
	 * @return
	 */
	private ArrayList getCustClassCorp(HashMap _parMap, HashMap _cacheMap) {
		return null; //
	}

	private WLTInitContext getInitContext() {
		if (initContext != null) {
			return initContext; //
		}
		initContext = new WLTInitContext();
		return initContext;
	}
}
