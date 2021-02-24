package cn.com.infostrategy.bs.mdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;
import org.nfunk.jep.JEP;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;

/**
 * 数据权限之资源过滤
 * @author xch
 *
 */
public class DataAccessResourceFilterDMO extends AbstractDMO {

	private CommDMO commDMO = new CommDMO(); //
	private TBUtil tbUtil = new TBUtil(); //
	private Hashtable ht_funInstance = new Hashtable(); //哈希表..
	private JEP jepParser = new JEP();

	private Logger logger = WLTLogger.getLogger(DataAccessResourceFilterDMO.class); //

	public DataAccessResourceFilterDMO() {
		jepParser.addStandardFunctions(); // 增加所有标准函数!!
		jepParser.addStandardConstants(); // 增加所有变量!!!
	}

	public HashMap fn_1(HashMap _pars) {
		filterByPolicyTree(null, "41", null);
		return null; //
	}

	/**
	 * 过滤数据
	 * @param _resType
	 * @param _policyTreeType 策略树类型
	 * @param _filterType 过滤的类型(不过滤;只进行角色过滤;只进行策略树过滤;同时串连式过滤;同时并连式过滤)
	 * @param _objs
	 * @return
	 */
	public Object[][] filterData(String _resType, String _policyTreeType, String _filterType, Object[][] _objs) {

		return null;
	}

	/**
	 * 根据资源分类进行基于角色的过滤
	 * @param _resType 资源分类
	 * @param _objs 实际数据,即根据查询条件返回的数据
	 * @return
	 */
	public Object[][] filterByRole(String _resType, Object[][] _objs) {
		//先找出登录人员的所有角色

		//再找出该角色可以访问哪些资源!

		//再一个个遍历这些资源的定义公式,将实际数据送入其中一个个执行,返回一个boolean值,如果是true,则通过，否则就杀掉！

		//如果是函数或自定义过滤器，参数是循环中的一条记录中的某一个字段的值！但该函数很可能会根据该参数是数据库中查找，如果每条记录都是查询数据库则必须性能很低！！
		//应该先把数据一下子取出来，然后在内存是使用HashMap查找,所以必须解决好这个问题!!否则性能大有问题!!

		return null; ////
	}

	/**
	 * 根据策略树过滤..
	 * 策略树是一个树型结构,计算起来非常讲究技巧,应该是一层层的计算,即从奇数的条件层去计算,然后返回下一循环的奇数层(或者是过滤层)!!
	 * @param _resType
	 * @param _policyTreeType
	 * @return
	 */
	public Object[][] filterByPolicyTree(String _resType, String _policyTreeType, Object[][] _objs) {
		String str_sql = "select * from pub_dataaccess_policytree where typeid='" + _policyTreeType + "'"; //查询的SQL..
		try {
			HashVO[] hvs = commDMO.getHashVoArrayAsTreeStructByDS(null, str_sql, "id", "parentid", "seq", ""); //
			for (int i = 0; i < hvs.length; i++) {
				System.out.println(hvs[i].getStringValue("nodename") + ",级层:" + hvs[i].getStringValue("$level"));
			}

			ArrayList firstLevelConditionList = new ArrayList(); //
			DefaultMutableTreeNode rootNode = null; //commDMO.getTreeNode(); //
			for (int i = 0; i < rootNode.getChildCount(); i++) {
				DefaultMutableTreeNode firstLevelNode = (DefaultMutableTreeNode) rootNode.getChildAt(i); //
				HashVO nodeVO = (HashVO) firstLevelNode.getUserObject(); //取得数据
				if (nodeVO.getStringValue("nodetype", "").equals("条件")) {
					firstLevelConditionList.add(firstLevelNode); //加入
				} else {
					logger.error("第一层结点[" + nodeVO.getStringValue("nodename") + "]的类型不是[条件],不符合约定,跳过之."); //
				}
			}
			ArrayList filterList = new ArrayList(); //真正需要的!!
			getNextLevelCondition(firstLevelConditionList, filterList); //

			//真正遍历所有过滤器,去过滤器中找到定义的过滤公式与条件,并执行之
			if (filterList.size() > 0) {
				String[] treeIds = (String[]) filterList.toArray(new String[0]); //
				String resSqlStr = "select resid from pub_dataaccess_policy_resmap where policytree_id in (" + tbUtil.getInCondition(treeIds) + ") and restypeid='" + _resType + "'"; //取出所有可以访问的资源清单
				HashVO[] hvs_reslist = commDMO.getHashVoArrayByDS(null, resSqlStr); //
				HashSet hs_distinct = new HashSet(); //
				for (int i = 0; i < hvs_reslist.length; i++) { //作唯一性过滤,因为可能不同的结点同时定义访问一个资源!
					hs_distinct.add(hvs_reslist[i].getAttributeValue("resid")); //置入
				}
				String[] str_resList = (String[]) hs_distinct.toArray(new String[0]); //
				String str_resformulaSQL = "select * from pub_dataaccess_resformula where resid in (" + tbUtil.getInCondition(str_resList) + ") order by resid,seq"; //先一下子取出所有公式,这是为了提高性能!
				HashVO[] hvs_resFormula = commDMO.getHashVoArrayByDS(null, str_resformulaSQL); //真正的资源定义!即执行公式,它与条件里的执行应该是一套逻辑!!
				for (int i = 0; i < str_resList.length; i++) {
					ArrayList resFormulaList = new ArrayList(); //
					for (int j = 0; i < hvs_resFormula.length; j++) {
						if (hvs_resFormula[j].getStringValue("resid").equals(str_resList[i])) {
							resFormulaList.add(hvs_resFormula[j]); //
						}
					}
					HashVO[] hvs_resFormula_one = (HashVO[]) resFormulaList.toArray(new HashVO[0]); //某一个资源的具体公式定义
					long[] ll_indexs = getPassedIndex(_objs, hvs_resFormula_one); //根据资源公式定义返回
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
		return null;
	}

	/**
	 * 根据某一层的条件,取得下一层的条件(或者过滤层)
	 * @param _listCondition
	 * @param _filterList
	 * @return
	 */
	private void getNextLevelCondition(ArrayList _listCondition, ArrayList _filterList) {
		if (_listCondition.size() == 0) { //走到找不到条件就返回
			return;
		}

		ArrayList returnList = new ArrayList(); //
		for (int i = 0; i < _listCondition.size(); i++) { //遍历每一个条件
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) _listCondition.get(i); //取得结点
			HashVO nodeVO = (HashVO) node.getUserObject(); //结点的数据..
			String str_result = computeCondition(nodeVO); //计算条件的值.

			//遍历条件下的所有子结点,即条件返回值或过滤结点.
			DefaultMutableTreeNode[] childNodes = getOneNodeChildNodes(node); //
			boolean isMatch = false; //是否对上一个值,用于others的处理
			for (int j = 0; j < childNodes.length; j++) {
				HashVO childNodeVO = (HashVO) childNodes[j].getUserObject(); //子结点的数据..
				if (childNodeVO.getStringValue("nodetype").equals("条件返回值")) { //如果类型不是"条件返回值"..
					logger.error("结点[" + childNodeVO.getStringValue("nodename") + "]的类型不是[条件返回值],不符合约定,跳过之."); //
					continue; //跳过
				}
				String str_caseValue = childNodeVO.getStringValue("casevalue"); //实际值.
				if (compareResultAndCaseValue(str_result, str_caseValue)) { //如果对上了!!!!!!!!!!!!!!!!!!!!至关重要,应该两者都是可以带分号的一维数组!即包含的方式!
					if (!str_caseValue.equals("@全部")) { //如果不是特殊字符串@全部,则标记是真正对上了!!!
						isMatch = true; //马上标记表示找到了一个!
					}
					DefaultMutableTreeNode[] subChildNodes = getOneNodeChildNodes(childNodes[j]); //找出所有孙子结点,即下一层的所有条件或者过滤器
					for (int k = 0; k < subChildNodes.length; k++) {
						HashVO hvo_subchild = (HashVO) subChildNodes[k].getUserObject(); //
						String str_notetype = hvo_subchild.getStringValue("nodetype"); //取得该结点的类型
						if (str_notetype.equals("过滤器")) {
							_filterList.add(hvo_subchild.getStringValue("id")); //如果是过滤器,则直接加入过滤器列表!
						} else if (str_notetype.equals("条件")) { //如果是条件,则在条件列表中加入!
							returnList.add(subChildNodes[k]); //
						} else {
							logger.error("结点[" + hvo_subchild.getStringValue("nodename") + "]的类型既不是[条件]也不是[过滤器],不符合约定,跳过之."); //
						}
					}
					String str_isbreak = childNodeVO.getStringValue("isbreak"); //是否break,如果是break则不继续往下找了
					if (str_isbreak.equals("Y")) {
						break; //
					}
				}

				if (j == childNodes.length - 1 && !isMatch && (str_caseValue.equals("@其他") || str_caseValue.equals("@其它"))) { //如果是最后一个结点,且前面的又都没对上,且该结点的值又叫"@其他"

				}
			}
		}
		getNextLevelCondition(returnList, _filterList); //再次调用自己,形成递归,直至并没有找到新的条件然后退出!!
	}

	/**
	 * 比较条件返回值与case的值!!!!
	 * @param _conResult
	 * @param _caseValue
	 * @return
	 */
	private boolean compareResultAndCaseValue(String _conResult, String _caseValue) {
		if (_caseValue.equals("@全部")) { //如果是特殊字符@全部,则直接返回true.
			return true; ////
		}
		String[] str_items_1 = tbUtil.split(_conResult, ";"); //
		String[] str_items_2 = tbUtil.split(_caseValue, ";"); //
		boolean isContain = tbUtil.containTwoArrayCompare(str_items_1, str_items_2); //是否包含..
		return isContain; //
	}

	/**
	 * 取得某一个结点的所有子结点
	 * @param _node
	 * @return
	 */
	private DefaultMutableTreeNode[] getOneNodeChildNodes(DefaultMutableTreeNode _node) {
		int li_count = _node.getChildCount(); //
		DefaultMutableTreeNode[] childNodes = new DefaultMutableTreeNode[li_count]; //
		for (int i = 0; i < childNodes.length; i++) {
			childNodes[i] = (DefaultMutableTreeNode) _node.getChildAt(i); //
		}
		return childNodes;
	}

	/**
	 * 计算条件的返回结果,大批量的计算逻辑!!
	 * @return
	 */
	private String computeCondition(HashVO _conditionVO) {
		try {
			String str_conType = _conditionVO.getStringValue("isconditiontype"); //
			if (str_conType.equals("N")) {
				String str_switchfunname = _conditionVO.getStringValue("switchfunname"); //
				String str_fnValue = getFunctionReturnValue(str_switchfunname); //如果是条件,一定是函数或自定义,且没有宏代码!!所以直接计算!!!
			} else {
				HashVO[] hvs_cons = commDMO.getHashVoArrayByDS(null, "select * from pub_dataaccess_policytree_con where policytree_id='" + _conditionVO.getStringValue("id") + "' order by seq"); //
				for (int i = 0; i < hvs_cons.length; i++) { //遍历!!!

				}
			}
			return "是"; //
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据资源公式计算,一条条判断各条数据是否通过
	 * @param _objs
	 * @param _resFormula
	 * @return
	 */
	private long[] getPassedIndex(Object[][] _objs, HashVO[] _resFormula) {
		Hashtable ht_index = null; //存储二维数组itemKey与索引位置的哈希表!!
		for (int i = 0; i < _objs.length; i++) { //遍历一条条记录
			boolean[] bo_passed = new boolean[_resFormula.length]; //
			for (int j = 0; j < _resFormula.length; j++) {
				String str_funName = _resFormula[i].getStringValue("custfunname"); //函数名
				String str_comparetype = _resFormula[i].getStringValue("comparetype"); //比较符
				String str_comparevalue = _resFormula[i].getStringValue("comparevalue"); //比较值

				if (str_funName.indexOf("{") > 0) { //如果函数名有宏代码,则要进行替换处理
					str_funName = convertFunByReplaceMac(str_funName, ht_index, _objs[i]); //
				}
				if (str_comparevalue.indexOf("{") > 0) { //如果函数名有宏代码,则要进行替换处理
					str_comparevalue = convertFunByReplaceMac(str_comparevalue, ht_index, _objs[i]); //
				}

				bo_passed[j] = dealPassCompare(str_funName, str_comparetype, str_comparevalue); //将函数名与比较值进行比较!!
			}

			//进行组合计算
			StringBuffer sb_span = new StringBuffer(); //
			for (int j = 0; j < _resFormula.length; j++) {
				if (_resFormula[j].getStringValue("andor") != null) {
					sb_span.append(_resFormula[j].getStringValue("andor") + " "); //
				}

				if (_resFormula[j].getStringValue("leftbrack") != null) { //左括号
					sb_span.append(_resFormula[j].getStringValue("leftbrack") + " "); //
				}

				sb_span.append(bo_passed[j] + " "); //加入布尔值

				if (_resFormula[j].getStringValue("rightbrack") != null) { //右括号
					sb_span.append(_resFormula[j].getStringValue("rightbrack") + " "); //
				}
			}

			jepParser.parseExpression(sb_span.toString()); // 执行公式
			Object obj = jepParser.getValueAsObject(); //
		}

		return null; //

	}

	/**
	 * 转换函数名,
	 * @param _oldFunName
	 * @param _mapIndex
	 * @param _obj
	 * @return
	 */
	private String convertFunByReplaceMac(String _oldFunName, Hashtable _mapIndex, Object[] _obj) {
		String str_funName = _oldFunName; //
		try {
			String[] str_maclist = tbUtil.getFormulaMacPars(str_funName, "{", "}"); //取得所有宏代码..
			for (int k = 0; k < str_maclist.length; k++) { //遍历所有宏代码
				int li_realIndex = (Integer) _mapIndex.get(str_maclist[k]); //取得实际位置
				String str_realValue = "" + _obj[li_realIndex]; //实际值
				str_funName = tbUtil.replaceAll(str_funName, "{" + str_maclist[k] + "}", str_realValue); //
			}
			return str_funName; //
		} catch (Exception e) {
			logger.error(e.getMessage()); //
			return null;
		}
	}

	/**
	 * 真正的进行
	 * @param str_funName
	 * @param str_comparetype
	 * @param str_comparevalue
	 * @return
	 */
	private boolean dealPassCompare(String str_funName, String str_comparetype, String str_comparevalue) {
		String str_1 = getFunctionReturnValue(str_funName); //函数的返回值
		String str_2 = getFunctionReturnValue(str_comparevalue); //比较值的返回值
		if (str_1 != null && str_2 != null) { //两者不为空
			if (str_comparetype.equals("=")) {
				return str_1.equals(str_2); //比较两者!!
			} else if (str_comparetype.equals("!=")) {
				return !str_1.equals(str_2); //比较两者!!
			} else if (str_comparetype.equals(">=")) {
				return str_1.equals(str_2); //比较两者!!
			} else if (str_comparetype.equals("<=")) {
				return str_1.equals(str_2); //比较两者!!
			} else if (str_comparetype.equals("like")) {
				if (str_1.indexOf(str_2) >= 0) {
					return true;
				}
			} else if (str_comparetype.equals("in")) {
				String[] str_items_2 = tbUtil.split(str_2, ";"); //
				for (int i = 0; i < str_items_2.length; i++) {
					if (str_items_2[i].equals(str_1)) {
						return true; //
					}
				}
			} else if (str_comparetype.equals("not in")) { //不存在于
				String[] str_items_2 = tbUtil.split(str_2, ";"); //
				for (int i = 0; i < str_items_2.length; i++) {
					if (str_items_2[i].equals(str_1)) {
						return false; //
					}
				}
				return true; //如果一个没对上,则返回true!!
			} else if (str_comparetype.equals("contain")) { //两者都可能是数组
				String[] str_items_1 = tbUtil.split(str_1, ";"); //
				String[] str_items_2 = tbUtil.split(str_2, ";"); //
				return tbUtil.containTwoArrayCompare(str_items_1, str_items_2);
			}
		}

		return false;
	}

	/**
	 * 取得一个函数的返回值.....
	 * @return
	 */
	private String getFunctionReturnValue(String _fnName) {
		String str_funName = _fnName; //
		if (str_funName.indexOf("(") > 0 && str_funName.trim().endsWith(")")) { //如果有括号则说明是函数或直接类名!!
			String str_fn = str_funName.substring(0, str_funName.indexOf("(")); //真正的函数名..
			String str_pars = str_funName.substring(str_funName.indexOf("(") + 1, str_funName.indexOf(")")).trim(); //找出()内的内容,即所有参数!!!
			String[] str_parItems = tbUtil.split(str_pars, ","); //以逗号分隔,找出所有参数!!
			for (int i = 0; i < str_parItems.length; i++) {
				str_parItems[i] = str_parItems[i].trim(); //
				if (str_parItems[i].startsWith("\"")) { //如果是双引号开头,则删除之!
					str_parItems[i] = str_parItems[i].substring(1, str_parItems[i].length()); //
				}
				if (str_parItems[i].endsWith("\"")) { //如果是双引号结尾,则删除之!
					str_parItems[i] = str_parItems[i].substring(0, str_parItems[i].length() - 1); //
				}
			}

			String str_className = null; //
			if (str_fn.indexOf(".") > 0) { //如果有逗号,则说明是直接定义的类名
				str_className = str_fn; //
			} else { //反之是函数名,即注册过的函数!!
				str_className = getClassNameByFnName(str_fn); //搜索到Class名
			}

			DataAccessFunctionIFC funObject = null; //函数对象
			try {
				if (ht_funInstance.containsKey(str_className)) { //如果缓存中已有了,
					funObject = (DataAccessFunctionIFC) ht_funInstance.get(str_className); //
				} else {
					funObject = (DataAccessFunctionIFC) Class.forName(str_className).newInstance(); //
					ht_funInstance.put(str_className, funObject); //置入缓存!!!
				}
			} catch (Exception e) {
				logger.error("创建数据过滤函数类[" + str_className + "]失败,原因:" + e.getMessage()); //////
			}

			if (funObject != null) { //如果成功的创建了对象!!!
				String str_fnReturnValue = funObject.getFunValue(str_parItems); ////
				return str_fnReturnValue;
			} else {
				return null;
			}
		} else {
			return _fnName;
		}
	}

	/**
	 * 从注册中找到某函数真正实现的类名!!
	 * @param _fnName
	 * @return
	 */
	private String getClassNameByFnName(String _fnName) {
		for (int i = 0; i < DataAccessFunctionIFC.regFunctions.length; i++) {
			if (DataAccessFunctionIFC.regFunctions[i][0].equals(_fnName)) {
				return DataAccessFunctionIFC.regFunctions[i][1]; //返回真正的类名
			}
		}
		return null; //
	}

	private void execFormula() {
		jepParser.parseExpression("if(((1==1) && (1==2)),\"true\",\"false\")"); // 执行公式
		Object obj = jepParser.getValueAsObject(); //
		System.out.println(obj);  //
	}

	public static void main(String[] _args) {
		new DataAccessResourceFilterDMO().execFormula();
	}

}
