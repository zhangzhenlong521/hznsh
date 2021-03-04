package cn.com.infostrategy.bs.sysapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.bs.mdata.JepFormulaParseAtBS;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;

/**
 * 权限权限策略服务器端的处理类!! 数据权限的核心逻辑都在这里!!! 非常关键的类! 也很复杂!!!
 * 有三个关键方法,一个是从权限策略表中取出策略清单进行过滤! 一个是根据公式进行过滤!(工作流中使用),一个是二次下探的方法!!!
 * @author xch
 *
 */
public class DataPolicyDMO {

	private CommDMO commDMO = null; //
	private TBUtil tbUtil = null; //
	private JepFormulaParseAtBS jepParse = null; //
	private WLTInitContext initContext = null; //

	/**
	 * 刘旋飞要的,根据一个机构id,一个策略名称,去系统中找到其所在机构!!!
	 * 它只想要返回机构id,而不是完整的SQL,即忽略后面的
	 * @param _deptId
	 * @param _datapolicy
	 * @param _returnCol
	 * @return
	 * @throws Exception
	 */
	public HashMap getDataPolicyTargetCorpsByCorpId(String _deptId, String _datapolicy, String _returnCol) throws Exception { //
		String[] str_corpInfo = new String[3];
		String[][] str_corpData = getCommDMO().getStringArrayByDS(null, "select id,name,corptype from pub_corp_dept where id='" + _deptId + "'"); //
		if (str_corpData != null && str_corpData.length > 0) {
			str_corpInfo[0] = str_corpData[0][0]; //
			str_corpInfo[1] = str_corpData[0][1]; //
			str_corpInfo[2] = str_corpData[0][2]; //
		}
		return getTargetCorps(_datapolicy, str_corpInfo, null, _returnCol); //
	}

	/**
	 * 刘旋飞要的, 根据一个人员id,一个策略,去找到所有机构!!
	 * @param _loginUserid
	 * @param _datapolicy
	 * @param _returnCol
	 * @return
	 * @throws Exception
	 */
	public HashMap getDataPolicyTargetCorpsByUserId(String _loginUserid, String _datapolicy, String _returnCol) throws Exception { //
		String[] str_corpInfo = getUserCorp(_loginUserid); //
		String[] str_myRoleCodes = getUserAllroles(_loginUserid); //
		return getTargetCorps(_datapolicy, str_corpInfo, str_myRoleCodes, _returnCol); //
	}

	/**
	 * 根据策略获取人员信息
	 * @param _loginUserid
	 * @param _datapolicy
	 * @param _returnCol
	 * @return
	 * @throws Exception
	 */
	public HashMap getDataPolicyTargetCorpsByUserId(HashMap _paramMap) throws Exception {
		String _userid = (String) _paramMap.get("userid");
		String _datapolicy = (String) _paramMap.get("datapolicy");
		String _returnCol = (String) _paramMap.get("returnCol");
		return this.getDataPolicyTargetCorpsByUserId(_userid, _datapolicy, _returnCol);
	}

	public HashMap getDataPolicyTargetUsersByUserId(String _loginUserid, String _datapolicy, String _returnCol) throws Exception {
		String[] str_corpInfo = getUserCorp(_loginUserid);
		String[] str_myRoleCodes = getUserAllroles(_loginUserid);
		String[] str_datapolicyData = getDataPolicyCondition(_datapolicy, "过滤方式=人员过滤;人员字段名=id", str_corpInfo, str_myRoleCodes);
		HashMap map = new HashMap();
		if (str_datapolicyData.length == 5) {
			String str_sql = str_datapolicyData[1];
			if (str_sql != null) {
				if ("id".equals(_returnCol.toLowerCase())) {
					if (str_sql.indexOf("and") > -1) { // 可能有扩展sql 只能查询一把返回 sunfujun/20130130/bug修改
						String[] str_items = getCommDMO().getStringArrayFirstColByDS(null, "select " + _returnCol + " from pub_user where " + str_datapolicyData[1]); //
						map.put("AllUser" + _returnCol.toUpperCase() + "s", str_items);
					} else { //如果只是id in的方式则可以拆解出来id
						String[] items = str_sql.split("or");
						if (items != null && items.length > 0) {
							String item = null;
							StringBuffer sb = new StringBuffer();
							for (int oo = 0; oo < items.length; oo++) {
								item = items[oo];
								if (item != null && !"".equals(item)) {
									item = item.replaceAll(" ", "");
									item = item.replaceAll("in", "");
									item = item.replaceAll("id", "");
									item = item.replaceAll("\\(", "");
									item = item.replaceAll("\\)", "");
									item = item.replaceAll("\'", "");
									if (oo == 0) {
										sb.append(item);
									} else {
										sb.append("," + item);
									}
								}
							}
							String[] ids = sb.toString().split(",");
							map.put("AllUser" + _returnCol.toUpperCase() + "s", ids);
						}
					}
				} else {
					String[] str_items = getCommDMO().getStringArrayFirstColByDS(null, "select " + _returnCol + " from pub_user where " + str_datapolicyData[1]); //
					map.put("AllUser" + _returnCol.toUpperCase() + "s", str_items);
				}
			}
		}
		return map;
	}

	//根据策略,所在机构信息,人员角色信息,计算出所有可看机构!
	private HashMap getTargetCorps(String _datapolicy, String[] str_corpInfo, String[] str_myRoleCodes, String _returnCol) throws Exception {
		String[] str_datapolicyData = getDataPolicyCondition(_datapolicy, "过滤方式=机构过滤;", str_corpInfo, str_myRoleCodes); //
		HashMap map = new HashMap(); //
		map.put("$帮助说明", "按道理应该有两个参数AllCorpIDs,isAllCorp,分别表示所有机构id数组,以及是否是全部机构!"); //
		map.put("AllCorpIDs", new String[0]); //
		map.put("isAllCorp", ""); //
		if (str_datapolicyData.length == 5) { //如果是正常返回,
			String str_virtualIds = str_datapolicyData[2]; //虚拟结点!
			if (str_virtualIds != null && !str_virtualIds.equals("")) {
				str_virtualIds = str_virtualIds.substring(str_virtualIds.indexOf("(") + 1, str_virtualIds.indexOf(")")); //
				String[] str_virtualIdArray = getTBUtil().split(str_virtualIds, ","); //分割!!
				map.put("VirtualCorpIDs", str_virtualIdArray); //加入虚拟结点:
			}

			String str_ids = str_datapolicyData[3]; //
			String str_isAllCorp = str_datapolicyData[4]; //
			if (str_ids != null) {
				String[] str_idArrays = getTBUtil().split(str_ids, ";"); //分割成数组!
				map.put("AllCorpIDs", str_idArrays); //永远加入这个!!
				if (!_returnCol.equalsIgnoreCase("id")) { //如果想返回的字段正好是id,则直接返回,不再查数据库了,从而提高性能!
					if ("Y".equals(str_isAllCorp)) { //如果是所有机构,则使用1=1查询,从而提高性能!
						String[] str_items = getCommDMO().getStringArrayFirstColByDS(null, "select " + _returnCol + " from pub_corp_dept where 1=1"); //
						map.put("AllCorp" + _returnCol.toUpperCase() + "s", str_items);
					} else {
						String[] str_items = getCommDMO().getStringArrayFirstColByDS(null, "select " + _returnCol + " from pub_corp_dept where id in (" + getCommDMO().getSubSQLFromTempSQLTableByIDs(str_idArrays) + ")"); //
						map.put("AllCorp" + _returnCol.toUpperCase() + "s", str_items);
					}
				}
			}
			map.put("isAllCorp", str_isAllCorp); //
		}
		return map; //
	}

	/**
	 * 找出登录人员机构!
	 * @param _loginUserid
	 * @return
	 * @throws Exception
	 */
	private String[] getUserCorp(String _loginUserid) throws Exception {
		//先找出登录人员的机构类型!与角色,如果机构类型与角色匹配上则加入该条件!!
		String str_myCorpId = null; //我本人的所在机构!!可能为空,因为没有为登录人员分配所属机构!!
		String str_myCorpName = null; //本人实际机构名称
		String str_myCorpType = null; //我(登录人员)的机构类型!!!
		String str_myExtcorptype = null; //我的机构扩展类型
		HashVO[] hvs_myCorpInfo = getCommDMO().getHashVoArrayByDS(null, "select t1.userid,t1.userdept,t1.isdefault,t2.corptype,t2.name userdeptname,t2.extcorptype from pub_user_post t1 left join pub_corp_dept t2 on t1.userdept=t2.id where t1.userid='" + _loginUserid + "'"); //找出我的所在机构!!
		if (hvs_myCorpInfo != null && hvs_myCorpInfo.length > 0) { //如果找到机构!!
			for (int i = 0; i < hvs_myCorpInfo.length; i++) {
				if ("Y".equals(hvs_myCorpInfo[i].getStringValue("isdefault"))) {
					str_myCorpId = hvs_myCorpInfo[i].getStringValue("userdept"); //用户的机构!!!
					str_myCorpName = hvs_myCorpInfo[i].getStringValue("userdeptname"); //
					str_myCorpType = hvs_myCorpInfo[i].getStringValue("corptype"); //
					str_myExtcorptype = hvs_myCorpInfo[i].getStringValue("extcorptype"); //
					break; //中断循环!
				}
			}
			if (str_myCorpId == null) { //如果万一没有设置是否默认机构,则直接选择第一个!!
				str_myCorpId = hvs_myCorpInfo[0].getStringValue("userdept"); //
				str_myCorpName = hvs_myCorpInfo[0].getStringValue("userdeptname"); //
				str_myCorpType = hvs_myCorpInfo[0].getStringValue("corptype"); //
				str_myExtcorptype = hvs_myCorpInfo[0].getStringValue("extcorptype"); //
			}
		}
		return new String[] { str_myCorpId, str_myCorpName, str_myCorpType, str_myExtcorptype }; //
	}

	/**
	 * 找出登录人员角色
	 * @param _loginUserid
	 * @return
	 * @throws Exception
	 */
	private String[] getUserAllroles(String _loginUserid) throws Exception {
		//找出登录人员的所有角色编码!!
		String[] str_myRoleCodes = null; //我的所有角色编码!! 比如[总行制度管理员][风评官]
		HashVO[] hvs_myRolesInfo = getCommDMO().getHashVoArrayByDS(null, "select t1.userid,t1.roleid,t2.code rolecode from pub_user_role t1 left join pub_role t2 on t1.roleid=t2.id where t1.userid='" + _loginUserid + "'"); //我的所有角色!!
		if (hvs_myRolesInfo != null && hvs_myRolesInfo.length > 0) {
			HashSet hst_roleCode = new HashSet(); //
			for (int i = 0; i < hvs_myRolesInfo.length; i++) {
				if (hvs_myRolesInfo[i].getStringValue("rolecode") != null) {
					hst_roleCode.add(hvs_myRolesInfo[i].getStringValue("rolecode")); //
				}
			}
			if (hst_roleCode.size() > 0) { //如果有数据!!
				str_myRoleCodes = (String[]) hst_roleCode.toArray(new String[0]); //得到我的所有角色!!!
			}
		}
		return str_myRoleCodes;
	}

	/**
	 * 直接通过API计算!
	 * @param _loginUserid 登录人员id
	 * @param _datapolicy  策略名称
	 * @param _type  计算类型,一共的两种，1-机构计算,2-人员计算
	 * @param _corpFieldName  机构字段名称,比如是blcorp,则最后就返回 blcorp in (....),之所以必须将该字段送进来,而不是直接返回一个id清单，是因为如果存在有“附加SQL条件”的情况时，就无法拼接了!!
	 * @param _userField 人员字段名称,比如createuser,则最后就返回 createuser in ()...
	 * @return 返回一个String[],第一列是详细计算过程说明,第二列是返回的sql条件,比如 blcorp in ('12','15');
	 * @throws Exception
	 */
	public String[] getDataPolicyCondition(String _loginUserid, String _datapolicy, int _type, String _corpFieldName, String _userFieldName) throws Exception {
		String str_formula = ""; //
		if (_type == 1) {
			str_formula = str_formula + "过滤方式=机构过滤;"; //
		} else if (_type == 2) {
			str_formula = str_formula + "过滤方式=人员过滤;"; //
		} else {
			str_formula = str_formula + "过滤方式=机构过滤;"; //
		}

		if (_corpFieldName != null) {
			str_formula = str_formula + "机构字段名=" + _corpFieldName + ";"; //如果重新定义了机构字段名!默认是createcorp
		}

		if (_userFieldName != null) {
			str_formula = str_formula + "人员字段名=" + _userFieldName + ";"; //如果重新定义了人员字段名,默认是createuser
		}

		return getDataPolicyCondition(_loginUserid, _datapolicy, str_formula); //
	}

	/**
	 * 数据权限策略计算过程!! 最复杂,最强大的地方,即根据登录人员,权限策略然后去找出机构,人员清单,然后拼所SQL! 即 blcorpid in ('')
	 * 计算的逻辑是仿照兴业的首次上塑再二次下探即可!! 
	 * 这个方法有480行左右,二次下探有120行左右!总共600行左右!!可以理解成这600行代码非常关键与实用!它将一次性解决所有与机构相关的权限问题!!以后无论是参照,过滤条件,API调用,等等，最后都转调到这里!
	 * @param _loginUserid
	 * @param _datapolicy
	 * @param _datapolicyMap
	 * @return 一个String[],第一列是详细计算过程说明,第二列是返回的sql条件,比如 blcorp in ('12','15');
	 * @throws Exception
	 */
	public String[] getDataPolicyCondition(String _loginUserid, String _datapolicy, String _datapolicyMap) throws Exception {
		String[] str_corpInfo = getUserCorp(_loginUserid); //
		String[] str_myRoleCodes = getUserAllroles(_loginUserid); //
		return getDataPolicyCondition(_datapolicy, _datapolicyMap, str_corpInfo, str_myRoleCodes); //
	}

	public String[] getDataPolicyCondition(String _loginUserid, String _datapolicy, String _datapolicyMap, boolean isAddfieldName) throws Exception {
		String[] str_corpInfo = getUserCorp(_loginUserid); //
		String[] str_myRoleCodes = getUserAllroles(_loginUserid); //
		return getDataPolicyCondition(_datapolicy, _datapolicyMap, str_corpInfo, str_myRoleCodes, isAddfieldName); //
	}

	//此方法为原有平台的
	public String[] getDataPolicyCondition(String _datapolicy, String _datapolicyMap, String[] _loginUserCorpInfo, String[] _loginUserRoles) throws Exception {
		return getDataPolicyCondition(_datapolicy, _datapolicyMap, _loginUserCorpInfo, _loginUserRoles, false);
	}

	//太平项目在最后加了个isAddfieldName参数 
	public String[] getDataPolicyCondition(String _datapolicy, String _datapolicyMap, String[] _loginUserCorpInfo, String[] _loginUserRoles, boolean isAddfieldName) throws Exception {
		StringBuilder sb_returnInfo = new StringBuilder("\r\n"); //
		String str_billFilterType = "机构过滤"; //有[机构过滤/人员过滤之分]
		String str_billCorpField = "createcorp"; //需要解析Map得到createcorp
		String str_billUserField = "createuser"; //需要解析Map得到
		HashMap<String, String> confMap = new HashMap<String, String>(); //
		if (_datapolicyMap != null && !_datapolicyMap.trim().equals("")) { //如果有映射定义,即有时多个表使用同一个策略,但不巧的是的字段名不一样,这时需要一个state=status这样的映射
			int li_pos = _datapolicyMap.indexOf("@"); //
			if (li_pos > 0) {
				String str_1 = _datapolicyMap.substring(0, li_pos); //
				String str_2 = _datapolicyMap.substring(li_pos + 1, _datapolicyMap.length()); //
				confMap.putAll(getTBUtil().convertStrToMapByExpress(str_1)); //后面有aa=123;bb=456;的样子,要转成一个Map
				confMap.putAll(getTBUtil().convertStrToMapByExpress(str_2)); //
			} else {
				confMap.putAll(getTBUtil().convertStrToMapByExpress(_datapolicyMap)); //
			}
		}
		if (confMap.containsKey("过滤方式")) {
			str_billFilterType = (String) confMap.get("过滤方式"); //
		}
		if (confMap.containsKey("机构字段名")) {
			str_billCorpField = (String) confMap.get("机构字段名"); //
		}
		if (confMap.containsKey("人员字段名")) {
			str_billUserField = (String) confMap.get("人员字段名"); //
		}
		boolean isNumberStr = getTBUtil().isStrAllNunbers(_datapolicy); //看是否是主键等id类型的,因为在XML中是使用名称来关联的
		HashVO[] hvs_policy = null; //
		if (isNumberStr) {
			hvs_policy = getCommDMO().getHashVoArrayByDS(null, "select * from pub_datapolicy where id='" + _datapolicy + "'"); //如果是数字则是id查询
		} else {
			hvs_policy = getCommDMO().getHashVoArrayByDS(null, "select * from pub_datapolicy where name='" + _datapolicy + "'"); //如果是字符串,则name查询!!!
		}
		if (hvs_policy == null || hvs_policy.length <= 0) {
			sb_returnInfo.append("没有找到对应的名为[" + _datapolicy + "]的策略,直接认为是全部能看!\r\n"); //
			return new String[] { sb_returnInfo.toString(), "'无策略定义'='无策略定义'" }; //
		}
		String str_dataPolicyId = hvs_policy[0].getStringValue("id"); //
		String str_dataPolicyName = hvs_policy[0].getStringValue("name"); //
		HashVO[] hvs_policy_b = getCommDMO().getHashVoArrayByDS(null, "select * from pub_datapolicy_b where datapolicy_id='" + str_dataPolicyId + "' order by seq,id"); //找明细!!
		if (hvs_policy_b == null || hvs_policy_b.length <= 0) {
			sb_returnInfo.append("名为[" + str_dataPolicyName + "]的策略没有定义一条清单,直接认为是全部能看!\r\n"); //
			return new String[] { sb_returnInfo.toString(), "'无策略清单定义'='无策略清单定义'" }; //
		}
		sb_returnInfo.append("初始判断:采用的权限策略是[" + str_dataPolicyName + "],计算方式是[" + str_billFilterType + "],机构字段是[" + str_billCorpField + "],人员字段是[" + str_billUserField + "]\r\n"); //

		//找出登录人员机构信息与角色信息
		String str_myCorpId = _loginUserCorpInfo[0]; //我本人的所在机构!!可能为空,因为没有为登录人员分配所属机构!!
		String str_myCorpName = _loginUserCorpInfo[1]; //本人实际机构名称
		String str_myCorpType = _loginUserCorpInfo[2]; //我(登录人员)的机构类型!!!
		String str_myExtcorptype = _loginUserCorpInfo[3]; //我的机构扩展类型
		String[] str_myRoleCodes = _loginUserRoles; //
		sb_returnInfo.append("★本人所在机构[" + str_myCorpId + "/" + str_myCorpName + "],机构类型是[" + str_myCorpType + "],");
		if (str_myRoleCodes != null && str_myRoleCodes.length > 0) {
			sb_returnInfo.append("本人所有角色是["); //
			for (int i = 0; i < str_myRoleCodes.length; i++) {
				sb_returnInfo.append(str_myRoleCodes[i] + ";"); //
			}
			sb_returnInfo.append("],另外\"一般用户\",\"普通员工\"这种角色是默认的!\r\n\r\n"); //
		} else {
			sb_returnInfo.append("本人的所有角色为空!但像\"一般用户\",\"普通员工\"这种角色是默认的!\r\n\r\n"); //
		}
		String[] str_appendRoleCodes = new String[] { "一般员工", "一般人员", "一般用户", "所有人员", "普通员工", "普通人员", "普通用户" }; //为了减少配置量,如果一个人不配任何角色,则默认认为自动拥有这几种角色!然后在策略中自动起效果！【xch/2012-08-14】
		if (str_myRoleCodes == null || str_myRoleCodes.length <= 0) {
			str_myRoleCodes = str_appendRoleCodes; //
		} else {
			String[] str_spanRoles = new String[str_myRoleCodes.length + str_appendRoleCodes.length]; //
			System.arraycopy(str_myRoleCodes, 0, str_spanRoles, 0, str_myRoleCodes.length); //
			System.arraycopy(str_appendRoleCodes, 0, str_spanRoles, str_myRoleCodes.length, str_appendRoleCodes.length); //
			str_myRoleCodes = str_spanRoles; //
		}

		//进行匹配主体计算！策略计算的第一步！！即先找出一共有几条策略被我匹配上了,如果一条没匹配上,则直接返回。。。
		sb_returnInfo.append("☆☆☆开始根据本人的所在机构类型与角色,对[" + hvs_policy_b.length + "]条策略明细进行主体匹配计算★★★\r\n"); //
		ArrayList al_matchPolicy = new ArrayList(); //
		for (int i = 0; i < hvs_policy_b.length; i++) {
			hvs_policy_b[i].setAttributeValue("$原来索引号", "" + (i + 1)); //索引号,为了在显示跟踪信息时让人知道到底是第几条配置项计算出来的!!
			String str_corptypes_m = hvs_policy_b[i].getStringValue("corptypes_m"); //主体的机构类型!
			String str_roles_m = hvs_policy_b[i].getStringValue("roles_m"); //主体角色
			String str_extcorptype_m = hvs_policy_b[i].getStringValue("extcorptype_m"); //主体机构扩展分类 by haoming 2016-04-08
			String str_corptype_g1 = hvs_policy_b[i].getStringValue("corptype_g1"); //首次上访问的机构
			String str_corptype_g2 = hvs_policy_b[i].getStringValue("corptype_g2"); //二次下探的机构类型
			String str_extcorptype_g2 = hvs_policy_b[i].getStringValue("extcorptype_g2"); //二次下探的扩展类型
			String str_roles_g = hvs_policy_b[i].getStringValue("roles_g"); //二次下探的扩展类型
			String str_appendsqlcons = hvs_policy_b[i].getStringValue("appendsqlcons"); //SQL条件

			StringBuilder sb_subfix = new StringBuilder("机构条件是[" + (str_corptypes_m == null ? "" : str_corptypes_m) + "],角色条件是[" + (str_roles_m == null ? "" : str_roles_m) + "],"); //
			if (str_extcorptype_m != null) {
				sb_subfix.append("机构扩展类型条件是[" + str_extcorptype_m + "],"); //
			}
			if (str_corptype_g1 != null) {
				sb_subfix.append("首次上溯的机构类型[" + str_corptype_g1 + "],"); //
			}

			if (str_corptype_g2 != null) {
				sb_subfix.append("下探的机构类型[" + str_corptype_g2 + "],"); //
			}
			if (str_extcorptype_g2 != null) {
				sb_subfix.append("下探的扩展类型[" + str_extcorptype_g2 + "],"); //
			}
			if (str_roles_g != null) {
				sb_subfix.append("下探的角色[" + str_roles_g + "],"); //
			}
			if (str_appendsqlcons != null) {
				sb_subfix.append("附加SQL条件[" + str_appendsqlcons + "],"); //
			}

			//System.out.println("主体机构类型[" + str_corptypes_m + "],主体角色[" + str_roles_m + "]"); //
			boolean isCorpMatch = true; //机构是否匹配上
			if (str_corptypes_m != null && !str_corptypes_m.trim().equals("")) { //如果主体机构类型不为空!
				if (str_myCorpType == null || str_myCorpType.trim().equals("")) {
					isCorpMatch = false; //
				} else {
					String[] str_items = getTBUtil().split(str_corptypes_m, ";"); //分割一下,即可能有多个机构类型!
					isCorpMatch = getTBUtil().isExistInArray(str_myCorpType, str_items); //是否本机构类型在其中!
				}
			}

			boolean is_extcorptype_Match = true; //机构扩展类型是否匹配上

			if (!getTBUtil().isEmpty(str_extcorptype_m)) {
				if (getTBUtil().isEmpty(str_myExtcorptype)) {
					is_extcorptype_Match = false;
				} else {
					String exporttypes[] = getTBUtil().split(str_myExtcorptype, ";");
					String str_extcorptype_ms[] = getTBUtil().split(str_extcorptype_m, ";");
					is_extcorptype_Match = getTBUtil().containTwoArrayCompare(str_extcorptype_ms, exporttypes); //
				}
			}

			boolean isRoleMatch = true; //角色是否匹配上
			if (str_roles_m != null && !str_roles_m.trim().equals("")) { //
				if (str_myRoleCodes == null || str_myRoleCodes.length <= 0) {
					isRoleMatch = false; //
				} else {
					String[] str_items = getTBUtil().split(str_roles_m, ";"); //分割,因为可能有多个角色
					isRoleMatch = getTBUtil().containTwoArrayCompare(str_myRoleCodes, str_items); //
				}
			}
			if (isCorpMatch && isRoleMatch && is_extcorptype_Match) { //如果机构与角色都匹配上了,则记录该条件
				al_matchPolicy.add(hvs_policy_b[i]); //
				sb_returnInfo.append("[" + (i + 1) + "★]匹配成功！" + sb_subfix.toString() + "\r\n\r\n"); //
			} else {
				sb_returnInfo.append("[" + (i + 1) + "☆]匹配失败，" + sb_subfix.toString() + "\r\n\r\n"); //
			}
		}
		if (al_matchPolicy.size() <= 0) { //如果没匹配上一个条件,则直接返回!!
			sb_returnInfo.append("没有一条策略清单符合条件,直接决定不能查询一条记录!"); //
			return new String[] { sb_returnInfo.toString(), "'匹配主体结果'='false'" }; //
		}
		//查询出所有机构,这为后面上溯下探计算时使用!!!取所有机构这个逻辑以后可以做成缓存!!!
		//之所以在这查询所有机构,是因为如果前面没有匹配成功一条策略则直接返回了,不做要查询所有机构了,所以提高了性能!!
		//		HashVO[] hvs_allCorps= getCommDMO().getHashVoArrayAsTreeStructByDS(null, "select id,name,parentid,corptype,extcorptype,seq from pub_corp_dept", "id", "parentid", "seq", null); //找出所有机构数据,之所以找出所有,是因为复杂的计算需要在机构树中上上下下多个来回,必须要整个机构数!
		HashVO[] hvs_allCorps = ServerCacheDataFactory.getInstance().getCorpCacheDataByAutoCreate();
		int li_allCorpCount = hvs_allCorps.length; //所有机构树的数量!! 如果是全行,则返回出来的结果数量等于这个数量,则将SQL折算出特殊符号!
		HashMap allCorpMap = new HashMap(); //将key与机构数据存在一个哈希表中,搜索起来快的多!!
		for (int i = 0; i < hvs_allCorps.length; i++) {
			String str_corpId = hvs_allCorps[i].getStringValue("id"); //机构id
			allCorpMap.put(str_corpId, hvs_allCorps[i]); //
		}
		ArrayList al_myParentCorpTypeList = null; //存储我的父亲机构类型的列表!!即登录人员的真系父亲,爷爷...比如【南京分行->无锡分行->南岭支行】
		if (str_myCorpId != null) { //如果我有机构!
			HashVO myCorpHVO = (HashVO) allCorpMap.get(str_myCorpId); //
			String str_parentids = myCorpHVO.getStringValue("$parentpathids"); //找到我的所有父亲路径!
			if (str_parentids != null && !str_parentids.equals("")) { //如果我有父亲!!
				al_myParentCorpTypeList = new ArrayList(); //
				String[] str_myParentIdItems = getTBUtil().split(str_parentids, ";"); //分割!!!
				for (int i = 0; i < str_myParentIdItems.length; i++) { //遍历我的父亲!!
					HashVO parentItemVO = (HashVO) allCorpMap.get(str_myParentIdItems[i]); //父亲VO
					al_myParentCorpTypeList.add(new String[] { parentItemVO.getStringValue("id"), parentItemVO.getStringValue("corptype"), parentItemVO.getStringValue("$parentpathnamelink"), parentItemVO.getStringValue("extcorptype") }); //将机构id与机构类型
				}
			}
		}
		HashVO myCorpVO = (HashVO) allCorpMap.get(str_myCorpId); //父亲VO
		str_myCorpName = myCorpVO.getStringValue("$parentpathnamelink"); //一开始取的我的机构名称是不带路径的,这里重置一下路径!!!
		HashMap corpCacheMap = new HashMap(); //做缓存,因为下面是循环进行二次下探,为了性能先把机构取好,这样就不需要每次计算时取数据库了!!从而提高性能!!
		corpCacheMap.put("AllCorpHashVOs", hvs_allCorps); //送入缓存!
		corpCacheMap.put("AllCorpHashMap", allCorpMap); //送入缓存!

		//开始进行首次上溯->二次下探计算,最核心与复杂的逻辑,大约160行代码！！！
		HashVO[] hvs_matchPolicy = (HashVO[]) al_matchPolicy.toArray(new HashVO[0]); //匹配上的策略清单!!!
		ArrayList al_corp_role_sql = new ArrayList(); //
		HashMap allCorpTypeMap = null; //在下拉字典库中,存储机构类型的映射,即有【$本部门】这些动态机构类型!
		sb_returnInfo.append("\r\n◆◆◆开始对主体匹配成功的[" + hvs_matchPolicy.length + "]条策略明细进行\"上溯下探\"计算★★★"); //
		for (int i = 0; i < hvs_matchPolicy.length; i++) { //遍历匹配上的记录
			//先首次上溯计算!!!即从我的父系路径链中找到第一个满足指定机构类型的机构,如果没有,则从全行中再找一下!
			sb_returnInfo.append("\r\n");
			String str_oldIndexNo = hvs_matchPolicy[i].getStringValue("$原来索引号"); //索引号,为了在显示跟踪信息时让人知道到底是第几条配置项计算出来的!!
			String str_corptype_g1 = hvs_matchPolicy[i].getStringValue("corptype_g1"); //首次上访的机构类型!!如果找不到,则全行范围内寻找! 如果是$本机构,$本部门,$本科室,则根据本人的实际机构类型去系统参数中寻找到实际的需要上访的机构类型!!!
			String str_corptype_g2 = hvs_matchPolicy[i].getStringValue("corptype_g2"); //二次下探的机构类型!!
			String str_extcorptype_g2 = hvs_matchPolicy[i].getStringValue("extcorptype_g2"); //二次下探的扩展机构类型!!
			String str_roles_g = hvs_matchPolicy[i].getStringValue("roles_g"); //二次下探的人员角色!!
			String str_appendsqlcons = hvs_matchPolicy[i].getStringValue("appendsqlcons", ""); //SQL条件
			if (str_corptype_g1 != null && str_corptype_g1.startsWith("$")) { //如果是动态的首次上访的机构类型!则需要再算一把!!即从系统参数中取!!//
				String[] str_convertCorpTye = getDynamicCorpType(str_corptype_g1, allCorpTypeMap, str_myCorpType); //
				String str_infoPrefix = "◆首次上塑计算(转换上塑动态条件):第[" + str_oldIndexNo + "]条策略清单的首次上塑的是动态条件[" + str_corptype_g1 + "],本人实际机构与类型是[" + str_myCorpId + "/" + str_myCorpName + "/" + str_myCorpType + "],定义的映射是[" + str_convertCorpTye[1] + "]"; //
				if (str_convertCorpTye[0] == null) { //如果没找到
					sb_returnInfo.append(str_infoPrefix + ",结果没有成功转换,这说明配置有问题的?\r\n"); //
				} else {
					sb_returnInfo.append(str_infoPrefix + ",成功转换后得到[" + str_convertCorpTye[0] + "]!\r\n"); //
					str_corptype_g1 = str_convertCorpTye[0]; //
				}
			}

			//首次上溯找出实际的根结点!!!
			String str_rootCorpId_1 = null; //
			String str_rootCorpName_1 = null; //
			//如果定义了首次上溯的机构类型,才真正进行上溯!!!
			if (str_corptype_g1 != null && !str_corptype_g1.trim().equals("")) {
				if (al_myParentCorpTypeList != null) { //遍历我的父亲!!
					for (int j = 0; j < al_myParentCorpTypeList.size(); j++) { //遍历!!
						String[] str_parentItem = (String[]) al_myParentCorpTypeList.get(j); //
						if (str_corptype_g1.equals(str_parentItem[1])) { //如果等于指定的需要首次上访问的机构类型,则直接退出!【李春娟/2018-08-24】
							str_rootCorpId_1 = str_parentItem[0]; //
							str_rootCorpName_1 = str_parentItem[2]; //
							break; //
						}
					}
					if (str_rootCorpId_1 == null) { //如果在我的父亲路径中没找到,则全行去找,比如直接设的是[总行/全行]等,本来想搞个开关指定是直接去找还中路径中去找的,但怕导致配置太复杂,故将从路径中找,找不到再全行找,因为一般来可能产生交叉情况则不会有问题!!但如果【上海分行】该设类型的却没设,则很可能找到【南京分行】头上去!这是个隐患!!
						for (int j = 0; j < hvs_allCorps.length; j++) {
							if (hvs_allCorps[j].getStringValue("corptype", "").equals(str_corptype_g1)) {
								str_rootCorpId_1 = hvs_allCorps[j].getStringValue("id"); //
								str_rootCorpName_1 = hvs_allCorps[j].getStringValue("$parentpathnamelink"); //
								break; //
							}
						}
					}
				}
				if (str_rootCorpId_1 == null) {
					sb_returnInfo.append("◆首次上塑计算:第[" + str_oldIndexNo + "]条策略清单根据实际上塑条件[" + str_corptype_g1 + "],没有找到该类型的机构!\r\n"); //
				} else {
					sb_returnInfo.append("◆首次上塑计算:第[" + str_oldIndexNo + "]条策略清单根据实际上塑条件[" + str_corptype_g1 + "],找到机构[" + str_rootCorpId_1 + "/" + str_rootCorpName_1 + "]!\r\n"); //
				}
			}
			//二次下探计算,即首次上溯找到机构后,再向下寻找满足下控条件的所有机构!
			boolean isSqlDefineNull = (str_appendsqlcons == null || str_appendsqlcons.trim().equals("")) ? true : false; //
			boolean isRoleDefineNull = (str_roles_g == null || str_roles_g.trim().equals("")) ? true : false; //
			if (str_rootCorpId_1 != null) { //如果找到了对应的机构!!
				if (str_extcorptype_g2 != null && str_extcorptype_g2.indexOf("$") >= 0) { //如果二次下探也存在动态的,则也要再算一把!即如果有[$等同机构范围],则将其替换成rootid机构的扩展类型!!
					StringBuilder sb_newCorptype_g2 = new StringBuilder(";"); //首先有个分号!
					String[] str_splitItems = getTBUtil().split(str_extcorptype_g2, ";"); //先分割!!
					for (int j = 0; j < str_splitItems.length; j++) { //遍历!!!
						if (str_splitItems[j].startsWith("$")) { //如果是动态的!!
							String[] str_convertCorpTye = getDynamicCorpType(str_splitItems[j], allCorpTypeMap, str_myCorpType); //转换一把!得到实际的机构类型!
							String str_infoPrefix = "★二次下探计算(转换动态扩展类型):第[" + str_oldIndexNo + "]条策略清单二次下探有动态条件[" + str_splitItems[j] + "],本人实际机构与类型是[" + str_myCorpId + "/" + str_myCorpName + "/" + str_myCorpType + "],定义的映射是[" + str_convertCorpTye[1] + "]"; //
							if (str_convertCorpTye[0] == null) {
								sb_returnInfo.append(str_infoPrefix + ",但没有找到对应的机构类型,则忽略该动态扩展机构类型条件!\r\n"); //
							} else { //找到指定我机构类型,然后在我的父亲路径中找到对应的第一个的这个类型的父亲的扩展类型!!
								String str_findCorpId = null, str_findCorpName = null, str_findCorpType = null, str_findExtCorpType = null; ////
								if (al_myParentCorpTypeList != null) { //遍历我的父亲!!
									for (int k = 0; k < al_myParentCorpTypeList.size(); k++) { //遍历我的所有父亲
										String[] str_parentItem = (String[]) al_myParentCorpTypeList.get(k); //
										if (str_parentItem[1].equals(str_convertCorpTye[0])) { //如果等于指定的需要首次上访问的机构类型,则直接退出!
											str_findCorpId = str_parentItem[0]; //
											str_findCorpType = str_parentItem[1]; //
											str_findCorpName = str_parentItem[2]; //
											str_findExtCorpType = str_parentItem[3]; //
											break; //
										}
									}
								}
								if (str_findCorpId == null) { //如果没找到这个父亲,
									sb_returnInfo.append(str_infoPrefix + ",虽然成功转换到了机构类型[" + str_convertCorpTye[0] + "],但是在本人父亲链中没有找到该类型的机构,所以也忽略该动态扩展机构类型条件!\r\n"); //
								} else { //如果找到这个父亲!!
									if (str_findExtCorpType == null || str_findExtCorpType.trim().equals("")) {
										sb_returnInfo.append(str_infoPrefix + ",成功转换到了机构类型[" + str_convertCorpTye[0] + "],也成功在父亲链中找到了对应机构[" + str_findCorpId + "/" + str_findCorpName + "],但该机构的扩展类型为空,所以也忽略该动态扩展机构类型条件!\r\n"); //
									} else {
										if (str_findExtCorpType.startsWith(";")) { //要去头!!!
											str_findExtCorpType = str_findExtCorpType.substring(1, str_findExtCorpType.length()); //
										}
										if (str_findExtCorpType.endsWith(";")) { //要去尾!!
											str_findExtCorpType = str_findExtCorpType.substring(0, str_findExtCorpType.length() - 1); //; //如果不是分号结尾,则加上!!!
										}
										sb_newCorptype_g2.append(str_findExtCorpType + ";"); //
										sb_returnInfo.append(str_infoPrefix + ",成功转换到了机构类型[" + str_convertCorpTye[0] + "],也成功在父亲链中找到了对应机构[" + str_findCorpId + "/" + str_findCorpName + "],且该机构的扩展类型[" + str_findExtCorpType + "]又不为空,则拼接加入!\r\n"); //
									}
								}
							}
						} else {
							sb_newCorptype_g2.append(str_splitItems[j] + ";"); //
						}
					}
					str_extcorptype_g2 = sb_newCorptype_g2.toString(); //必须重新赋植
				}

				//实际开始二次下探!!转调另一个复杂的二次下探计算函数!!!之所以将二次下探封装成另一个函数,是因为工作流中也用到那个!!
				boolean bo_iscontainroot = hvs_matchPolicy[i].getBooleanValue("iscontainroot", false); //下探时是否包含根结点?
				boolean bo_iscontainchildren = hvs_matchPolicy[i].getBooleanValue("iscontainchildren", false); //下探后是否包含子孙结点?
				String str_uniontype = hvs_matchPolicy[i].getStringValue("uniontype", "交集"); //下探时是否包含根结点?

				ArrayList al_returnCorpIdList = secondDownFindAllCorpChildrensByCondition(str_rootCorpId_1, str_corptype_g1, str_corptype_g2, str_extcorptype_g2, null, bo_iscontainroot, bo_iscontainchildren, str_uniontype, corpCacheMap); //

				//这里以后可以考虑将二次下探的详细计算日志也取出来,然后加入本日志中。。。
				String str_secondDownInfoPrefix = "★二次下探计算:第[" + str_oldIndexNo + "]条策略清单在首次上塑机构[" + str_rootCorpId_1 + "/" + str_rootCorpName_1 + "]的范围下,根据实际的下探机构类型条件[" + (str_corptype_g2 == null ? "" : str_corptype_g2) + "],扩展机构类型条件[" + (str_extcorptype_g2 == null ? "" : str_extcorptype_g2) + "],共找到[" + al_returnCorpIdList.size() + "]个子结构"; //
				if (al_returnCorpIdList.size() >= li_allCorpCount) { //如果正好是所有机构数,则直接返回
					if (str_billFilterType.equals("机构过滤")) {
						if (isSqlDefineNull) { //如果SQL条件为空,则直接返回!
							sb_returnInfo.append(str_secondDownInfoPrefix + ",因为个数正好是所有机构数,则直接返回所有机构!\r\n"); //
							return new String[] { sb_returnInfo.toString(), "'全部机构'='全部机构'" }; //直接返回,啥都不算了!!!
						} else {
							al_corp_role_sql.add(new Object[] { al_returnCorpIdList, str_roles_g, str_appendsqlcons, str_oldIndexNo }); ////
						}
					} else if (str_billFilterType.equals("人员过滤")) {
						if (isRoleDefineNull && isSqlDefineNull) { //如果角色也为空,SQL条件也为空!!!则直接返回
							sb_returnInfo.append(str_secondDownInfoPrefix + ",因为个数正好是所有机构数,则直接返回所有人员!\r\n"); //
							return new String[] { sb_returnInfo.toString(), "'全部人员'='全部人员'" }; //直接返回,啥都不算了!!!
						} else {
							al_corp_role_sql.add(new Object[] { al_returnCorpIdList, str_roles_g, str_appendsqlcons, str_oldIndexNo }); ////
						}
					}
				} else { //如果机构数量不是全部!
					al_corp_role_sql.add(new Object[] { al_returnCorpIdList, str_roles_g, str_appendsqlcons, str_oldIndexNo }); ////
				}
				if (str_billFilterType.equals("机构过滤")) {
					sb_returnInfo.append(str_secondDownInfoPrefix + "," + (isSqlDefineNull ? "将参与唯一性机构合并!" : "将不参与唯一性机构合并!") + "\r\n"); //
				} else if (str_billFilterType.equals("人员过滤")) {
					sb_returnInfo.append(str_secondDownInfoPrefix + "," + ((isSqlDefineNull && isRoleDefineNull) ? "将参与唯一性人员合并!" : "将不参与唯一性人员合并!") + "\r\n"); //
				}
			} else { //如果没定义首次上溯的机构类型,或者根本没找到该类型的机构,即无法进行上溯计算,则直接处理SQL附加条件(即使定义了下探也忽略)!!比如有时有这种需求,所有人员都可以查看【密级为非密】的制度,这时就只需要定义SQL条件!根据不需要定义什么上溯的机构类型!
				String str_infoPrefix = "★非二次下探计算:第[" + str_oldIndexNo + "]条策略清单没有找到上塑的机构,所以不进行二次下探"; //
				if (str_billFilterType.equals("机构过滤")) {
					if (isSqlDefineNull) { //如果又没有定义SQL附件条件,则什么都不做,即这种情况下该配置项其实没有意义!即配错了,多余的!
						sb_returnInfo.append(str_infoPrefix + ",又没有SQL条件,则跳过机构处理!\r\n"); //
					} else { //
						if (getTBUtil().isExistInArray(str_appendsqlcons.trim(), new String[] { "99=99", "\"99=99\"", "1=1", "\"1=1\"" })) { //如果是特殊约定的99=99,1=1,则特殊处理!即有时就需要查看所有!比如某种角色的人可以看所有!
							sb_returnInfo.append(str_infoPrefix + ",且SQL条件正好又是特定的" + str_appendsqlcons + ",则直接返回!!\r\n"); //
							return new String[] { sb_returnInfo.toString(), "99=99" }; //直接返回,啥都不算了!!!
						} else {
							al_corp_role_sql.add(new Object[] { null, str_roles_g, str_appendsqlcons, str_oldIndexNo }); ////
							sb_returnInfo.append(str_infoPrefix + ",但有SQL条件,参与机构计算!\r\n"); //
						}
					}
				} else if (str_billFilterType.equals("人员过滤")) {
					if (isSqlDefineNull && isRoleDefineNull) {
						sb_returnInfo.append(str_infoPrefix + ",又没有SQL与角色条件,则跳过人员处理!\r\n"); //
					} else {
						if (getTBUtil().isExistInArray(str_appendsqlcons.trim(), new String[] { "99=99", "\"99=99\"", "1=1", "\"1=1\"" })) { //如果是特殊约定的99=99,1=1,则特殊处理
							sb_returnInfo.append(str_infoPrefix + ",且SQL条件正好又是特定的" + str_appendsqlcons + ",则直接返回所有人员!!\r\n"); //
							return new String[] { sb_returnInfo.toString(), "99=99" }; //直接返回,啥都不算了!!!
						} else {
							al_corp_role_sql.add(new Object[] { null, str_roles_g, str_appendsqlcons, str_oldIndexNo }); ////
							sb_returnInfo.append(str_infoPrefix + ",但有SQL或角色条件,参与人员计算!\r\n"); //
						}
					}
				}

			}
		} //end for,即循环结束!!

		//最后真正进行拼接SQL处理(大约150行代码)!因为前面上溯下探最后计算出来的结果只是一个ArrayList机构清单,有的还有sql附加条件!这时需要进行拼接SQL!
		sb_returnInfo.append("\r\n");
		String str_realsql = ""; //
		String str_virtualCorpSql = null; //虚拟机构id
		String str_allCorpIds = null; //所有机构,那每条策略计算出来的纯机构,不看其他条件(附加SQL)!!因为刘旋飞后来有需求,即不是最终想返回一个SQL,去绑定一个表单!而是直接返回机构!即借用策略配置实现根据某个机构,找到某个上级机构或控制范围!
		String str_isAllCorps = "N"; //是否找出了所有机构?当返回所有机构id数组时,客户端自动去拼接SQL时,如果发现是所有机构,则SQL就是1=1,所有需要这样一个标记!
		if (str_billFilterType.equals("机构过滤")) { //如果是【机构过滤】类型！！！！！
			HashSet hst_allCorpids = new HashSet(); //记录所有机构,用于最后计算出其父亲路径上的虚拟结点!!
			HashSet hst_distinctCorpids = new HashSet(); //用于记录唯一性合并的机构!
			ArrayList al_realsqls = new ArrayList(); //记录各条策略计算出来的sql条件,比如【blcorp in ('15','17','19')】
			String str_spansIndex = ""; //需要参与合并计算的策略明细!即这里有个非常关键的逻辑,即如果每条策略都没有定义附件SQL条件,则这时各条策略计算出来的机构有可能重复!最好最后再来进行一次唯一性合并!
			for (int i = 0; i < al_corp_role_sql.size(); i++) { //for_3
				Object[] rowObjs = (Object[]) al_corp_role_sql.get(i); //
				ArrayList al_corpids = (ArrayList) rowObjs[0]; //第一列,就是机构清单,可能为空!!比如只定义SQL附件条件,并没有定义上溯机构类型!!比如:任何人都可查看【类型='非密'】的制度
				String str_sqlcons = (String) rowObjs[2]; //
				String str_indexNo = (String) rowObjs[3]; //
				if (al_corpids != null) {
					hst_allCorpids.addAll(al_corpids); //加入所有机构变量中
				}
				if (str_sqlcons == null || str_sqlcons.trim().equals("")) { //如果附件SQL条件为空,则说明需要参与唯一性合并计算!
					if (al_corpids != null) { //如果有机构清单!!
						hst_distinctCorpids.addAll(al_corpids); //记录该机构将参加唯一性合并
						str_spansIndex = str_spansIndex + str_indexNo + ","; //跟踪时让人知道,到底合并哪几条策略!
					} else {
						//如果附加SQL为空,且机构清单又为空,则没有意义,所以什么都不做!!即这种情况下一般是配错了!!
					}
				} else { //如果定义了附加SQL条件,则没办法参与唯一性合并,因为它的逻辑是必须先与附加SQL进行"and"计算的!如果也参与唯一性合并,则逻辑就错了!!
					String str_sqlItem = ""; //
					str_sqlcons = convertSqlCons(str_sqlcons, confMap); //对SQL条件进行公式计算
					if (al_corpids == null) { //如果机构没有,即只有SQL条件,比如只有sql条件【类型='非密'】,则直接加入SQL
						str_sqlItem = "(" + str_sqlcons + ")"; //
					} else { //如果有机构,则两者合并!!
						if (al_corpids.size() >= li_allCorpCount) { //如果是所有机构!!
							str_sqlItem = "('全部机构'='全部机构' and (" + str_sqlcons + "))"; //
						} else {
							if (isAddfieldName) {//太平系统升级平台，需要添加此LiGuoli的代码。by haoming 2016-04-14
								str_sqlItem = "(" + getCommDMO().getSubSQLFromTempSQLTableByIDs((String[]) al_corpids.toArray(new String[0]), str_billCorpField) + " and (" + str_sqlcons + "))"; //
							} else {
								str_sqlItem = "(" + str_billCorpField + " in (" + getCommDMO().getSubSQLFromTempSQLTableByIDs((String[]) al_corpids.toArray(new String[0])) + ") and (" + str_sqlcons + "))"; //
							}
						}
					}
					al_realsqls.add(str_sqlItem); //加入实际!
					sb_returnInfo.append("计算SQL:第[" + str_indexNo + "]条策略单独计算出的SQL是[(" + str_sqlItem + ")]\r\n"); //
				}
			} //end for_3

			//最后判断如果发生了因为附加SQL为空,从而需要进行唯一性合并计算机构的情况,则进行唯一性合并计算!!
			if (hst_distinctCorpids.size() > 0) { //如果有值,则说明需要计算!
				String str_sqlItem = null; //
				if (hst_distinctCorpids.size() >= li_allCorpCount) { //如果合并后真巧是全部机构,则
					str_sqlItem = "'全部机构'='全部机构'"; //
				} else {
					if (isAddfieldName) { //太平系统升级平台，需要添加此LiGuoli的代码。by haoming 2016-04-14
						str_sqlItem = getCommDMO().getSubSQLFromTempSQLTableByIDs((String[]) hst_distinctCorpids.toArray(new String[0]), str_billCorpField); //唯一性过滤!!!
					} else {
						str_sqlItem = str_billCorpField + " in (" + getCommDMO().getSubSQLFromTempSQLTableByIDs((String[]) hst_distinctCorpids.toArray(new String[0])) + ")"; //唯一性过滤!!!
					}
				}
				al_realsqls.add(str_sqlItem); //
				sb_returnInfo.append("计算SQL:唯一性合并[" + str_spansIndex + "]条策略计算出的SQL是[" + str_sqlItem + "]\r\n"); //
			}

			//为所有机构计算出其至根结点路径上的所有“虚拟结点”！因为在树型机构展示时,会遇到过滤后的机构“不成形”了！！这时需要将路径还是链接起来,只不过要加一些“虚拟结点”！！
			if (hst_allCorpids.size() > 0) { //如果有机构
				str_virtualCorpSql = getVirtualCorpids(hst_allCorpids, allCorpMap, str_billCorpField); //
				sb_returnInfo.append("计算SQL:计算虚拟结点,返回SQL是[" + str_virtualCorpSql + "]\r\n"); //
				StringBuilder sb_allCorpIdAppend = new StringBuilder(); //
				Iterator it_allcorps = hst_allCorpids.iterator(); //
				while (it_allcorps.hasNext()) {
					sb_allCorpIdAppend.append((String) it_allcorps.next() + ";"); //
				}
				str_allCorpIds = sb_allCorpIdAppend.toString(); //
				if (hst_distinctCorpids.size() >= li_allCorpCount) {
					str_isAllCorps = "Y"; //
				}
			}

			//最后真正返回的SQL,就是将各条策略计算出来的SQL使用OR关系,简单拼接!!! 
			StringBuilder sb_realsql = new StringBuilder();
			for (int i = 0; i < al_realsqls.size(); i++) { //
				sb_realsql.append((String) al_realsqls.get(i)); ////
				if (i != al_realsqls.size() - 1) { //如果不是最后一个,则加上or
					sb_realsql.append(" or "); //
				}
			}
			str_realsql = sb_realsql.toString(); //
		} else if (str_billFilterType.equals("人员过滤")) { //如果是【人员过滤】类型！！！！！
			HashSet hst_corpids = new HashSet(); //
			ArrayList al_realsqls = new ArrayList(); //
			String str_spansIndex = ""; //
			HashMap allRoleCodeIdMap = null; //
			for (int i = 0; i < al_corp_role_sql.size(); i++) { //for_4,循环处理!!
				Object[] rowObjs = (Object[]) al_corp_role_sql.get(i); //
				ArrayList al_corpids = (ArrayList) rowObjs[0]; //
				String str_roles = (String) rowObjs[1]; //所有角色编码!!
				String str_sqlcons = (String) rowObjs[2]; //SQL条件
				String str_indexNo = (String) rowObjs[3]; //索引号
				if ((str_roles == null || str_roles.trim().equals("")) && (str_sqlcons == null || str_sqlcons.trim().equals(""))) { //如果角色为空,SQL条件也为空,则参与机构唯一性合并
					if (al_corpids != null) {
						hst_corpids.addAll(al_corpids); //唯一性加入!!
						str_spansIndex = str_spansIndex + str_indexNo + ","; //
					}
				} else { //如果有角色或SQL条件!则进行And计算!!
					String str_sqlItem = " "; //
					String str_tmpinfo = ""; //
					if (al_corpids == null || al_corpids.size() >= li_allCorpCount) { //如果机构为空,或者就是全部机构,则使用子查询查,否则SQL会超长!!
						str_sqlItem = str_sqlItem + "'所有人员'='所有人员' "; //
						if (str_roles != null && !str_roles.trim().equals("")) { //如果有角色!!
							if (allRoleCodeIdMap == null) {
								allRoleCodeIdMap = getCommDMO().getHashMapBySQLByDS(null, "select code,id from pub_role"); //找出所有角色!!!
							}
							String[] str_roleids = getRoleIdsByCodeInfo(allRoleCodeIdMap, str_roles); //根据角色编码定义反找id数组 
							str_sqlItem = str_sqlItem + "and " + str_billUserField + " in (select userid from pub_user_role where roleid in (" + getTBUtil().getInCondition(str_roleids) + ")) "; //使用子查询查!
							str_tmpinfo = ",因为是所有机构范围所以使用子查询"; //
						}
						str_sqlcons = convertSqlCons(str_sqlcons, confMap); //
						if (str_sqlcons != null && !str_sqlcons.trim().equals("")) { //如果SQL条件!
							str_sqlItem = str_sqlItem + "and (" + str_sqlcons + ") "; // 
						}
					} else { //如果是少量的机构,则先查询出机构中的所有人员,然后再查询人员,这些性能会更高些!因为SQL短,且没有子查询了!但可能以后也会搞成子查询!但但为一个分总加上总行就几百号人,所以应该不会有问题!!!
						String str_corpinCondition = getCommDMO().getSubSQLFromTempSQLTableByIDs((String[]) al_corpids.toArray(new String[0])); //拼接机构的id
						if (str_roles != null && !str_roles.trim().equals("")) { //如果有角色!!则使用角色的子查询!!一条SQL找出满足条件的所有人员!!
							if (allRoleCodeIdMap == null) {
								allRoleCodeIdMap = getCommDMO().getHashMapBySQLByDS(null, "select code,id from pub_role"); //
							}
							String[] str_roleids = getRoleIdsByCodeInfo(allRoleCodeIdMap, str_roles); //根据角色编码定义反找id数组 
							String[] str_userids = getCommDMO().getStringArrayFirstColByDS(null, "select t1.userid from pub_user_post t1,pub_user_role t2 where t1.userid=t2.userid and t1.userdept in (" + str_corpinCondition + ") and t2.roleid in (" + getTBUtil().getInCondition(str_roleids) + ")"); //
							str_sqlItem = str_sqlItem + str_billUserField + " in (" + getCommDMO().getSubSQLFromTempSQLTableByIDs(str_userids) + ") "; //按道理只会有少量人员!
							str_tmpinfo = ",预先进行机构与人员角色关联查询"; //
						} else { //如果没有角色,则直接使用机构查询!!!
							String[] str_userids = getCommDMO().getStringArrayFirstColByDS(null, "select userid from pub_user_post where userdept in (" + str_corpinCondition + ")"); //找出这些机构的id
							str_sqlItem = str_sqlItem + str_billUserField + " in (" + getCommDMO().getSubSQLFromTempSQLTableByIDs(str_userids) + ") "; //按道理只会有少量人员!
							str_tmpinfo = ",预先进行机构与人员关联查询"; //
						}
						str_sqlcons = convertSqlCons(str_sqlcons, confMap); //
						if (str_sqlcons != null && !str_sqlcons.trim().equals("")) { //如果SQL条件,则直接加入!!
							str_sqlItem = str_sqlItem + "and (" + str_sqlcons + ") "; // 
						}
					}
					al_realsqls.add("(" + str_sqlItem + ")"); //
					sb_returnInfo.append("计算SQL:第[" + str_indexNo + "]条策略单独计算出的SQL是[(" + str_sqlItem + ")]" + str_tmpinfo + "\r\n"); //
				}
			} //end for_4
			//最后判断如果发生了因为附加SQL为空,从而需要进行唯一性合并计算机构的情况,则进行唯一性合并计算!!
			if (hst_corpids.size() > 0) { //如果有唯一性过滤
				String str_pp = null; //
				String str_tmpinfo = ""; //
				if (hst_corpids.size() >= li_allCorpCount) { //如果合并后真巧是全部机构,则
					str_pp = "'全部人员'='全部人员'"; //
				} else { //否则通过SQL查询!
					String str_corpinCondition = getCommDMO().getSubSQLFromTempSQLTableByIDs((String[]) hst_corpids.toArray(new String[0])); //
					String[] str_userids = getCommDMO().getStringArrayFirstColByDS(null, "select userid from pub_user_post where userdept in (" + str_corpinCondition + ")"); //找出这些机构的id/
					str_pp = str_billUserField + " in (" + getCommDMO().getSubSQLFromTempSQLTableByIDs(str_userids) + ")"; //
					str_tmpinfo = ",预先进行机构与人员关联查询"; //
				}
				al_realsqls.add(str_pp); //
				sb_returnInfo.append("计算SQL:唯一性合并[" + str_spansIndex + "]条策略计算出的SQL是[" + str_pp + "]" + str_tmpinfo + "\r\n"); //
			}

			//为所有机构计算出其至根结点路径上的所有“虚拟结点”！因为在树型机构展示时,会遇到过滤后的机构“不成形”了！！这时需要将路径还是链接起来,只不过要加一些“虚拟结点”！！

			//最后真正返回的SQL,就是将各条策略计算出来的SQL使用OR关系,简单拼接!!! 
			StringBuilder sb_realsql = new StringBuilder(); // 
			for (int i = 0; i < al_realsqls.size(); i++) { //
				sb_realsql.append((String) al_realsqls.get(i)); ////
				if (i != al_realsqls.size() - 1) { //如果不是最后一个,则加上or
					sb_realsql.append(" or "); //
				}
			}
			str_realsql = sb_realsql.toString(); //
		} //如果是"人员过滤"判断结束!!

		if (str_realsql.trim().equals("")) {
			str_realsql = "'竟然返回空串'='Y'"; //
		}
		return new String[] { sb_returnInfo.toString(), str_realsql, str_virtualCorpSql, str_allCorpIds, str_isAllCorps }; //
	}

	/**
	 * 这是工作流中使用的方法!因为与策略很相似,所以放在一个类中!!
	 * 根据公式进行上塑下探的数据权限过滤,工作流中使用的,它与上面方式的区别在于,上面是从权限策略表中过滤,根据主体条件,决定进行何种首次上塑与二次下探,而这个则是直接去上塑与下探,而且是公式!!
	 * 非常重要的方法,必须绝对健壮与稳定!! 以后以后主要就靠这一个方法了! 该方法与上面方法有些部分可以再合并!! 总之这两个方法一定要健壮与稳定!!
	 * 取得某一个人员的某种机构类型的上级机构!!!
	 * 凡是根据登录人员还是创建人都将最终调用该方法!!
	 * 而且取本部门也将使用本方法!! 办法是先取得该人所在的机构的机构类型,然后将其机构类型作为参数送入本方法!! 
	 * 在送之前,如果发现该人的所在机构是以"下属机构"结尾的,则自动截掉! 这样灵活性更强!! 即必须将机构类型的名称之间的某种约定直接作为一种配置而发挥其功能!!
	 * 而且后来增加了直接根据部门名称来计算!!! 即可以省去配置扩展类型的麻烦! 更为简化了!!! 
	 * @param _userId
	 * @param _up1RootCorpType
	 * @param _down2CorpType
	 * @param _down2ExtCorpType
	 * @param _isDdown2ContainChildren
	 * @return
	 * @throws Exception
	 */
	public String[] getOnerUserSomeTypeParentCorpID(String _userId, String _up1RootCorpType, String _down2CorpType, String _down2ExtCorpType, String _down2CorpName, boolean _isDdown2ContainChildren, HashMap _cacheMap) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		TBUtil tbUtil = new TBUtil(); //
		HashVO[] hvs_wfcreaterCorps = commDMO.getHashVoArrayByDS(null, "select t1.userdept,t2.corptype,t1.isdefault from pub_user_post t1,pub_corp_dept t2 where t1.userdept=t2.id and t1.userid='" + _userId + "'"); //必须是默认机构!!
		if (hvs_wfcreaterCorps == null || hvs_wfcreaterCorps.length == 0) { //如果创建者的机构为空,则直接返回!!
			getInitContext().addCurrSessionCustStrInfoByKey("$环节参与者计算过程", "◆根据登录人/创建人[" + _userId + "]寻找其所在机构时,发现没有机构,所以返回空!<br>"); //
			return null;
		}
		if (_up1RootCorpType == null || _up1RootCorpType.trim().equals("")) { //如果创建者的机构为空,则直接返回!!
			getInitContext().addCurrSessionCustStrInfoByKey("$环节参与者计算过程", "◆没有指定首次上溯的机构类型,所以返回空!<br>"); //
			return null;
		}

		HashVO hvs_wfcreaterCorp = null; //
		for (int i = 0; i < hvs_wfcreaterCorps.length; i++) { //
			if ("Y".equals(hvs_wfcreaterCorps[i].getStringValue("isdefault"))) {
				hvs_wfcreaterCorp = hvs_wfcreaterCorps[i]; //
				break; //
			}
		}
		if (hvs_wfcreaterCorp == null) {
			hvs_wfcreaterCorp = hvs_wfcreaterCorps[0]; //
		}

		String str_createrCorpId = hvs_wfcreaterCorp.getStringValue("userdept"); //该人员所在的机构
		String str_createrCorpType = hvs_wfcreaterCorp.getStringValue("corptype"); //该人所在机构的机构类型
		if (str_createrCorpType.endsWith("下属机构")) { //截掉最后4个字!!因为实际情况中下属机构基本上都是算成其父亲路径的
			str_createrCorpType = str_createrCorpType.substring(0, str_createrCorpType.length() - 4); //去掉最后四个字!
		}
		String str_realCorpType = null; //实际机构类型,即根据类型条件找出实际想上访的机构类型!!
		String[] str_parCorpTypes = tbUtil.split(_up1RootCorpType, ";"); //首次上访的机构类型进行分隔
		if (str_parCorpTypes.length == 1) {
			if (str_parCorpTypes[0].indexOf("=>") > 0) { //如果有=>
				String str_corpTypecontition = str_parCorpTypes[0].substring(0, str_parCorpTypes[0].indexOf("=>")); //条件
				if (str_corpTypecontition.equalsIgnoreCase("*") || str_corpTypecontition.equals(str_createrCorpType)) {
					str_realCorpType = str_parCorpTypes[0].substring(str_parCorpTypes[0].indexOf("=>") + 2, str_parCorpTypes[0].length()); //取后面的
				}
			} else { //如果没有条件!
				str_realCorpType = str_parCorpTypes[0]; //如果只有一项,则表示直接定义的!! 但可能是*号,如果*号则后面不要计算了!
			}
		} else { //如果有多个,则表示是有条件的,即有=>
			for (int i = 0; i < str_parCorpTypes.length; i++) { //遍历!!!
				int li_pos = str_parCorpTypes[i].indexOf("=>"); //
				String str_key = str_parCorpTypes[i].substring(0, li_pos); //
				String str_value = str_parCorpTypes[i].substring(li_pos + 2, str_parCorpTypes[i].length()); //
				String[] str_key_items = tbUtil.split(str_key, "/"); //
				boolean isfind = false; //
				for (int j = 0; j < str_key_items.length; j++) {
					if (str_key_items[j].equals(str_createrCorpType)) {
						str_realCorpType = str_value; //
						isfind = true;
						break;
					}
				}
				if (isfind) { //如果发现了,则直接退出,否则继续下一个=>中找!!!
					break;
				}
			}
		}

		//这里要与上一个方法一样支持$本机构,$本部门的动态计算!!!
		if (str_realCorpType == null || str_realCorpType.equals("*")) { //如果一个没品配上,直接用创建者的机构类型!即如果没定义,则默认找本人的机构!!
			str_realCorpType = str_createrCorpType; //
			getInitContext().addCurrSessionCustStrInfoByKey("$环节参与者计算过程", "◆对上溯条件[" + _up1RootCorpType + "]进行计算(一定要注意该条件是否正确,尤其可能因为type不对导致计算不符),找到满足本人条件的机构类型=[" + str_realCorpType + "](由于没品配上,故直接用自己的类型)<br>"); //
		} else {
			getInitContext().addCurrSessionCustStrInfoByKey("$环节参与者计算过程", "◆对上溯条件[" + _up1RootCorpType + "]进行计算(一定要注意该条件是否正确,尤其可能因为type不对导致计算不符),找到满足本人条件的机构类型=[" + str_realCorpType + "]<br>"); //
		}

		//所出所有机构,以后这儿要用缓存!
		//现在的机制有性能问题,以后这个地方要优化,应该先找出本人机构的blparentcorpids,然后拼接where id in (blparentcorpids),然后循环找出这些机构的corptype等于匹配的机构类型!
		HashVO[] hvs_allCorps = null;//
		HashMap allCorpsMap = null; //
		if (_cacheMap != null && _cacheMap.containsKey("AllCorpHashVOs") && _cacheMap.containsKey("AllCorpHashMap")) {
			hvs_allCorps = (HashVO[]) _cacheMap.get("AllCorpHashVOs"); //
			allCorpsMap = (HashMap) _cacheMap.get("AllCorpHashMap"); //
		} else {
			hvs_allCorps = commDMO.getHashVoArrayAsTreeStructByDS(null, "select id,name,parentid,blparentcorpids,corptype,extcorptype,seq from pub_corp_dept", "id", "parentid", "seq", null); //找出所有机构,并且返回树型结构!!!
			allCorpsMap = new HashMap(); //将机构用哈希表存起来,因为下面要多次搜索!!
			for (int i = 0; i < hvs_allCorps.length; i++) {
				allCorpsMap.put(hvs_allCorps[i].getStringValue("id"), hvs_allCorps[i]); //先注册一下!为了下面根据id找机构时要非常快!!
			}
			_cacheMap = new HashMap(); //
			_cacheMap.put("AllCorpHashVOs", hvs_allCorps); //送入缓存!
			_cacheMap.put("AllCorpHashMap", allCorpsMap); //送入缓存!
		}

		HashVO hvo_creatercorp = (HashVO) allCorpsMap.get(str_createrCorpId); //
		if (hvo_creatercorp == null) { //如果机构不在基本机构表中,则直接返回
			return null;
		}
		String str_parentPathids = hvo_creatercorp.getStringValue("$parentpathids"); //所出本人的所有父亲路径!!!
		if (str_parentPathids == null || str_parentPathids.trim().equals("")) { //如果没找到,或父亲路径为空,直接返回!!!
			return null;
		}

		//先往上追塑找到我的那个父亲
		String str_matchedParentCorpId = null; //
		String str_matchedParentCorpName = null; //
		String str_matchedParentCorpBlparentcorpids = null; //
		String[] str_partentIds = tbUtil.split(str_parentPathids, ";"); //
		for (int i = 0; i < str_partentIds.length; i++) { //遍历我的所有父亲,从上往下,找互第一个类型等于指定主类型的机构
			HashVO hvo_item = (HashVO) allCorpsMap.get(str_partentIds[i]); //
			if (hvo_item.getStringValue("corptype", "").equals(str_realCorpType)) { //如果这个父亲的类型等于指定类型
				str_matchedParentCorpId = hvo_item.getStringValue("id"); //找到品配的父亲机构!!!
				break; ////
			}
		}

		if (str_matchedParentCorpId != null) {
			str_matchedParentCorpName = ((HashVO) allCorpsMap.get(str_matchedParentCorpId)).getStringValue("name"); //机构名称!
			str_matchedParentCorpBlparentcorpids = ((HashVO) allCorpsMap.get(str_matchedParentCorpId)).getStringValue("blparentcorpids"); //所属机构!
		}
		return new String[] { str_matchedParentCorpId, str_matchedParentCorpName, str_matchedParentCorpBlparentcorpids, str_realCorpType }; //
	}

	/**
	 * 二次下探取机构,取得一个机构的所有下级机构,当然是根据指定的条件(子机构类型/扩展类型机构名称)!! 即二次下探!!!
	 * @param _down2CorpType
	 * @param _down2ExtCorpType
	 * @param _down2CorpsName 直接根据名称模糊匹配,暂未实现,以后可能要实现! 比如直接找叫[办公室/风险部]的,这样就不需要设置扩展类型了,更直接!!
	 * @param _isDdown2ContainChildren 是否包含子结点
	 * @return
	 */
	public ArrayList secondDownFindAllCorpChildrensByCondition(String _rootCorpId, String _rootCorpType, String _down2CorpType, String _down2ExtCorpType, String _down2CorpsName, boolean _isContainRootBeforeDown2, boolean _isDdown2ContainChildren, String str_uniontype, HashMap _cacheMap) throws Exception {
		ArrayList al_return = new ArrayList(); //
		HashVO[] hvs_allCorps = null; //
		if (_cacheMap != null && _cacheMap.containsKey("AllCorpHashVOs")) { //如果缓存为空,或者缓存没有
			hvs_allCorps = (HashVO[]) _cacheMap.get("AllCorpHashVOs"); //
		} else {
			hvs_allCorps = new CommDMO().getHashVoArrayAsTreeStructByDS(null, "select id,name,parentid,corptype,extcorptype,seq from pub_corp_dept", "id", "parentid", "seq", null);
		}

		boolean isHaveCorpType = (_down2CorpType != null && !_down2CorpType.trim().equals("")) ? true : false; //是否定义了二次下探之机构类型?
		boolean isHaveExtType = (_down2ExtCorpType != null && !_down2ExtCorpType.equals("")) ? true : false; //是否定义了二次下探之扩展类型?
		boolean isHaveCorpName = (_down2CorpsName != null && !_down2CorpsName.equals("")) ? true : false; //是否定义了二次下探之扩展类型?

		//二次下探搜索!!
		//如果没有定义2次下探的机构类型与扩展类型,则直接全部加入!!比如一个【一级分行的管理员】可以直接控制整个分行!根本不需要再进行二次下探!!
		if (!isHaveCorpType && !isHaveExtType && !isHaveCorpName) { //如果什么都没定义
			if (_isDdown2ContainChildren) { //如果是包含所有子结点!
				al_return.add(_rootCorpId); //先加入根结点!以前没这个,但后来觉得,如果没有二次下探时,应该是自动包含根结点的!!
				for (int i = 0; i < hvs_allCorps.length; i++) { //遍历所有机构,如果使用linkcode则直接查找数据库!!是否效率更高?但经过反复测试,即使遍历3000个机构也是非常快的!
					if (hvs_allCorps[i].getStringValue("$parentpathids", "").indexOf(";" + _rootCorpId + ";") >= 0) { //如果这条记录的父亲包含我!!则说明是我的子孙!
						al_return.add(hvs_allCorps[i].getStringValue("id")); //加入返回清单!!
					}
				}
				getInitContext().addCurrSessionCustStrInfoByKey("$环节参与者计算过程", "◆进行二次下探,由于下探的机构类型与扩展条件都为空,且需要列出所有子孙,所以共找到[" + al_return.size() + "]个子机构<br>"); //
			} else { //如果不找子结构!!!则只加入自己!
				al_return.add(_rootCorpId); //只加入自己!!!
				getInitContext().addCurrSessionCustStrInfoByKey("$环节参与者计算过程", "◆进行二次下探,由于下探的机构类型与扩展条件都为空,且不需要列出所有子孙,所以直接列出机构[" + _rootCorpId + "]<br>"); //
			}
		} else { //如果有2次下探的机构类型与2次下探的扩展类型!则找出这些机构后,还要再一次找出这些机构的所有子结构!!!
			HashSet<String> hst_corptype = new HashSet<String>(); //记录所有满足机构类型条件的机构!!!
			HashSet<String> hst_exttype = new HashSet<String>(); //记录所有满足扩展类型条件的机构!!!
			HashSet<String> hst_corpname = new HashSet<String>(); //记录所有满足扩展类型条件的机构!!!
			int li_findCount_1 = 0, li_findCount_2 = 0, li_findCount_3 = 0;
			//遍历所有机构!!!一个个找!
			for (int i = 0; i < hvs_allCorps.length; i++) { //遍历699
				if (hvs_allCorps[i].getStringValue("$parentpathids", "").indexOf(";" + _rootCorpId + ";") < 0) { //如果这条记录不是我的子孙,则忽略跳过!!
					continue; //
				}

				//如果这个机构是我的子孙,如果定义了二次下探的机构类型!!!则计算该机构是否满足条件?
				if (isHaveCorpType) {
					String str_thisCorpType = hvs_allCorps[i].getStringValue("corptype"); //遍历游标机构之机构类型!!!
					if (str_thisCorpType != null && !str_thisCorpType.equals("")) {
						boolean isThisMatched = ifFormulaMatchThisItemByCondition(_down2CorpType, str_thisCorpType, _rootCorpType); //关键逻辑!
						if (isThisMatched) {
							hst_corptype.add(hvs_allCorps[i].getStringValue("id")); //
							li_findCount_1++; //
						}
					}
				}

				//如果定义了二次下探的扩展机构类型!!!则计算该机构是否满足条件?
				if (isHaveExtType) {
					String str_thisExtCorpType = hvs_allCorps[i].getStringValue("extcorptype"); //
					if (str_thisExtCorpType != null && !str_thisExtCorpType.equals("")) { //必须不为空!
						boolean isThisMatched = ifFormulaMatchThisItemByCondition(_down2ExtCorpType, str_thisExtCorpType, _rootCorpType); //关键逻辑!
						if (isThisMatched) { //如果直接相等,则直接加入!!因为大部分是这种情况,所以会提高性能!!!
							hst_exttype.add(hvs_allCorps[i].getStringValue("id")); //
							li_findCount_2++; //
						}
					}
				}

				if (isHaveCorpName) { //
					String str_thisCorpName = hvs_allCorps[i].getStringValue("name"); //机构名称!
					if (str_thisCorpName != null && !str_thisCorpName.equals("")) { //必须不为空!
						if (matchTwoCorpName(_down2CorpsName, str_thisCorpName)) { //看机构名称是否匹配
							hst_corpname.add(hvs_allCorps[i].getStringValue("id")); //
							li_findCount_3++; //
						}
					}
				}

			} //遍历699结束!!

			//进行交集/并集计算!即根据{机构类型}与{扩展类型}找出来的结果集两者可以进行交/并集计算!!交集与并集这两种需求都可能存在!比如想找出类型是"一级分行部门"并且扩展类型是"风险部"的!反之,并集也有道理!
			//无论是交集还是并集,其实都做了唯一性过滤!!这是因为使用了HashSet对象!!
			HashSet<String> hst_two_type = new HashSet<String>(); //两种类型进行合并!
			//boolean isIntersection = true; //是否交集? 这个变量以后也要做成是参数可配置的!!!
			if (isHaveCorpType && (isHaveExtType || isHaveCorpName)) { //如果两个条件都有定义,则进行交集合并!
				if ("交集".equals(str_uniontype)) { //如果是交集
					String[] str_allKeys = (String[]) hst_corptype.toArray(new String[0]); //首先是机构类型!
					for (int i = 0; i < str_allKeys.length; i++) { //遍历所有根据机构类型找到的!
						//如果扩展类型或机构名称匹配上,则加入!
						boolean issuccessExtType = true;
						if (isHaveExtType) { //如果定义了
							issuccessExtType = hst_exttype.contains(str_allKeys[i]); //
						}
						boolean issuccessCorpName = true;
						if (isHaveCorpName) { //
							issuccessCorpName = hst_corpname.contains(str_allKeys[i]); //
						}
						if (issuccessExtType && issuccessCorpName) { //必须两个都满足!!
							hst_two_type.add(str_allKeys[i]);
						}
					}
				} else { //如果是并集!!!
					hst_two_type.addAll(hst_corptype); //并上机构类型的
					hst_two_type.addAll(hst_exttype); //并上扩展类型的!
					hst_two_type.addAll(hst_corpname); //并上机构名称找到的
				}
			} else { //如果只有一个条件,则自动将两者合并,即这种情况下hst_corptype与hst_exttype只有一个有值,另一个为空!则直接将两者合并,怎么都没错!
				hst_two_type.addAll(hst_corptype); //并上机构类型的
				hst_two_type.addAll(hst_exttype); //并上扩展类型的!
				hst_two_type.addAll(hst_corpname); //并上机构名称找到的
			}

			StringBuilder sb_infoMsg = new StringBuilder(); //
			sb_infoMsg.append("◆进行二次下探,在根机构[" + _rootCorpId + "]中,");
			sb_infoMsg.append("下探机构类型为[" + (_down2CorpType == null ? "" : _down2CorpType) + "]的共找到[" + li_findCount_1 + "]个,"); //
			sb_infoMsg.append("下探扩展类型为[" + (_down2ExtCorpType == null ? "" : _down2ExtCorpType) + "]的共找到[" + li_findCount_2 + "]个,"); //
			sb_infoMsg.append("下探机构名称为[" + (_down2CorpsName == null ? "" : _down2CorpsName) + "]的共找到[" + li_findCount_3 + "]个,"); //
			sb_infoMsg.append("进行交/并集与唯一性计算后,共有[" + hst_two_type.size() + "]个!这些机构的Id是:["); //
			String[] str_allCorpIds = (String[]) hst_two_type.toArray(new String[0]); //
			for (int i = 0; i < str_allCorpIds.length; i++) {
				sb_infoMsg.append(str_allCorpIds[i]);
				if (i != str_allCorpIds.length - 1) {
					sb_infoMsg.append(","); //
				}
			}
			sb_infoMsg.append("]<br>"); //
			getInitContext().addCurrSessionCustStrInfoByKey("$环节参与者计算过程", sb_infoMsg.toString()); /////

			//如何定我要还要找出这些机构的所有下属机构,则还要再遍历一次!
			if (_isDdown2ContainChildren) { //如果二次下探时需要自动包含所有子结点,则需要再遍历一次
				for (int i = 0; i < hvs_allCorps.length; i++) { //遍历所有
					String str_parentids = hvs_allCorps[i].getStringValue("$parentpathids"); //
					if (str_parentids != null && !str_parentids.trim().equals("")) {
						String[] str_ids = getTBUtil().split(str_parentids, ";"); //
						for (int j = 0; j < str_ids.length; j++) { //
							if (hst_two_type.contains(str_ids[j])) { //如果这个机构的某个父亲在我上面计算的机构清单中,则说明是我的这个机构的子孙,则要加入!
								al_return.add(hvs_allCorps[i].getStringValue("id")); //
							}
						}
					}
				}

				getInitContext().addCurrSessionCustStrInfoByKey("$环节参与者计算过程", "◆二次下探后,需要同时列出满足条件机构的所有子孙结点,共有[" + al_return.size() + "]个!<br>"); //
			} else { //如果有二次下探,且找到了一批,且找到后不需要再继续再找子结点!!则直接列出返回!!
				al_return.addAll(Arrays.asList(hst_two_type.toArray(new String[0])));
			}

			//如果指定下探时自动包含根结点,则加入自己!!!
			if (_isContainRootBeforeDown2) {
				al_return.add(0, _rootCorpId);
			}
		}
		return al_return; //
	}

	//计算动态机构类型的逻辑!!
	private String[] getDynamicCorpType(String _dynamicCorpType, HashMap _allCorpTypeMap, String _myCorpType) throws Exception {
		if (_dynamicCorpType.equals("$本人绝对实际所在")) { //这是特殊的处理,直接返回自己机构的!!
			return new String[] { _myCorpType, "特殊指定的,无需转换" }; //
		}
		String str_realcorptype = _dynamicCorpType.trim(); //实际机构类型,去掉第一个$符,从第一位往后截取!!!
		if (_allCorpTypeMap == null) { //先找出所有机构的分类定义,其中编码有讲究的!即在编码中指定了对某人个类型的机构类型来说,其某个动态类型的计算方式是什么??
			_allCorpTypeMap = getCommDMO().getHashMapBySQLByDS(null, "select id,code from pub_comboboxdict where type in ('机构分类','机构类型') order by seq"); //  
		}
		String str_dyncorptype_mapping = (String) _allCorpTypeMap.get(_myCorpType); //根据我的实际机构的类型找出其映射!
		if (str_dyncorptype_mapping != null && !str_dyncorptype_mapping.trim().equals("")) { //如果找到,并且的确有映射,则将其解析成一个HashMap!
			HashMap mappingMap = getTBUtil().convertStrToMapByExpress(str_dyncorptype_mapping, ";", "="); ////
			if (mappingMap.containsKey(str_realcorptype)) { //如果定义的包含
				return new String[] { (String) mappingMap.get(str_realcorptype), str_dyncorptype_mapping }; //赋值!!
			}
		}
		return new String[] { null, str_dyncorptype_mapping };
	}

	/**
	 * 寻找一批机构结点如果到根结点的虚拟结点!!
	 * @param allCorpMap 
	 * @param _allCorps 
	 * @param _ids
	 * @return
	 */
	private String getVirtualCorpids(HashSet _hstCorpIds, HashMap _allCorpMap, String _corpFieldName) {
		Iterator items = _hstCorpIds.iterator(); //
		HashVO hvo = null; //
		String str_parentIds = null; //
		String[] str_parentIdItems = null; //
		HashSet hst_virtualCorp = new HashSet(); //存储所有虚拟结点的容器!!
		while (items.hasNext()) { //遍历所有机构!!
			String str_corpId = (String) items.next(); //当前机构!
			hvo = (HashVO) _allCorpMap.get(str_corpId); //
			str_parentIds = hvo.getStringValue("$parentpathids"); //
			if (str_parentIds != null && !str_parentIds.trim().equals("")) { //如果有值
				str_parentIdItems = getTBUtil().split(str_parentIds, ";"); //分割!!
				for (int i = 0; i < str_parentIdItems.length; i++) { //遍历!!
					if (!_hstCorpIds.contains(str_parentIdItems[i])) { //如果原来列表中不包含这个父亲id
						hst_virtualCorp.add(str_parentIdItems[i]); //
					}
				}
			}
		}

		if (hst_virtualCorp.size() <= 0) { //如果无需找到一个虚拟结点,则返回1=2
			return null;
		} else {
			StringBuilder sb_sql = new StringBuilder(); //
			Iterator itVirtualCorps = hst_virtualCorp.iterator(); //
			while (itVirtualCorps.hasNext()) { //如果还有!!
				sb_sql.append((String) itVirtualCorps.next()); //
				if (itVirtualCorps.hasNext()) { //如果不是最后一个,即后面还有
					sb_sql.append(","); //
				}
			}
			return " " + _corpFieldName + " in (" + sb_sql.toString() + ") "; //
		}
	}

	//根据角色编码描述,返回id数组!!!
	private String[] getRoleIdsByCodeInfo(HashMap _allRoleCodeIdMap, String _rolecodes) {
		String[] str_items = _rolecodes.split(";"); //
		String str_id = null; //
		ArrayList al_ids = new ArrayList(); //
		for (int i = 0; i < str_items.length; i++) {
			str_id = (String) _allRoleCodeIdMap.get(str_items[i]); //
			if (str_id != null) {
				al_ids.add(str_id); //
			}
		}
		return (String[]) al_ids.toArray(new String[0]); //
	}

	/**
	 * 因为SQL中支持公式,所以要计算!
	 * @param str_sqlcons
	 * @return
	 */
	private String convertSqlCons(String str_sqlcons, HashMap confMap) {
		String[] keys = (String[]) confMap.keySet().toArray(new String[0]);
		for (int i = 0; i < keys.length; i++) {
			str_sqlcons = getTBUtil().replaceAll(str_sqlcons, "{" + keys[i] + "}", (String) confMap.get(keys[i])); //
		}
		str_sqlcons = getTBUtil().replaceAll(str_sqlcons, "{机构字段名}", "createcorp"); //confMap中可能没有配置机构字段名或人员字段名，这里需要设置一下默认值
		str_sqlcons = getTBUtil().replaceAll(str_sqlcons, "{人员字段名}", "createuser"); //
		String str_value = (String) getJepParse().execFormula(str_sqlcons); //
		if (str_value != null) {
			return str_value;
		} else {
			return str_sqlcons;
		}
	}

	/**
	 * 匹配两个机构名称是否一致？
	 * @param _conditionName
	 * @param _itemCorpName
	 * @return
	 */
	private boolean matchTwoCorpName(String _conditionName, String _itemCorpName) {
		String str_conditionName = _conditionName; //
		String str_itemCorpName = _itemCorpName; //
		str_conditionName = str_conditionName.trim(); //
		str_itemCorpName = str_itemCorpName.trim(); //

		//str_conditionName = trimEndStr(str_conditionName); //
		//str_itemCorpName = trimEndStr(str_itemCorpName); //

		if (str_conditionName.equals(str_itemCorpName)) { //如果直接相等则肯定100%返回!
			return true; //
		}

		//以后这里要有个智能的判断,根据一个系统参数,将【风险管理部】与【法律合规部】等价于一个部门
		return false; //
	}

	private String trimEndStr(String _corpName) {
		String str_corpName = _corpName; //
		if (str_corpName.endsWith("管理部")) {
			str_corpName = str_corpName.substring(0, str_corpName.length() - 3); //
		}
		if (str_corpName.endsWith("部")) {
			str_corpName = str_corpName.substring(0, str_corpName.length() - 1); //
		}
		if (str_corpName.endsWith("室")) {
			str_corpName = str_corpName.substring(0, str_corpName.length() - 1); //
		}
		return str_corpName; //
	}

	/**
	 * 二次下探时计算是否匹配公式
	 * @param _formula
	 * @param _thisItem
	 * @param _conditionCorpType
	 * @return
	 */
	private boolean ifFormulaMatchThisItemByCondition(String _formula, String _thisItem, String _conditionCorpType) {
		if (_thisItem != null && _thisItem.equals(_formula)) { //如果直接相等就直接返回!!!
			return true;
		}

		if (_formula.indexOf("=>") > 0) { //如果有条件!!!在工作流定义中是公式,其实是多个!!
			String[] str_formulaItems = getTBUtil().split(_formula, ";"); //分号相隔!!兼容原来的!!
			for (int i = 0; i < str_formulaItems.length; i++) { //遍历各个条件!!
				int li_pos = str_formulaItems[i].indexOf("=>"); //
				if (li_pos > 0) {
					String str_prefix = str_formulaItems[i].substring(0, li_pos); //
					String str_subfix = str_formulaItems[i].substring(li_pos + 2, str_formulaItems[i].length()); //
					if (str_prefix.equals(_conditionCorpType)) { //如果满足了条件!!!则必须计算出个所以然来!!!
						String[] str_thisItems = getTBUtil().split(_thisItem, ";"); //我的条件值,比如我的机构类型,扩展类型等!!
						String[] str_valueItems = getTBUtil().split(str_subfix, "/"); //
						return getTBUtil().containTwoArrayCompare(str_thisItems, str_valueItems); //如果两者有一个品配上,则返回!!!
					}
				}
			}
			return false;
		} else { //如果没条件!!!
			String[] str_thisItems = getTBUtil().split(_thisItem, ";"); //
			String[] str_formulaItems = getTBUtil().split(_formula, ";"); //分号相隔!!兼容原来的!!
			return getTBUtil().containTwoArrayCompare(str_thisItems, str_formulaItems); //如果两者有一个品配上,则返回!!!
		}
	}

	private CommDMO getCommDMO() {
		if (commDMO != null) {
			return commDMO; //
		}
		commDMO = new CommDMO(); //
		return commDMO;
	}

	private TBUtil getTBUtil() {
		if (tbUtil != null) {
			return tbUtil;
		}
		tbUtil = new TBUtil(); //
		return tbUtil;
	}

	private JepFormulaParseAtBS getJepParse() {
		if (jepParse != null) {
			return jepParse;
		}
		jepParse = new JepFormulaParseAtBS(); //
		return jepParse; //
	}

	private WLTInitContext getInitContext() {
		if (initContext != null) {
			return initContext;
		}
		initContext = new WLTInitContext(); //
		return initContext;
	}

}
