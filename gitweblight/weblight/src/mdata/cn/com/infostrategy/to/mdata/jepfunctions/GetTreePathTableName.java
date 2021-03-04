package cn.com.infostrategy.to.mdata.jepfunctions;

/**
 * 得到树的全路径名!!
 * 语法是 getTreePathColValue("pub_corp_dept[表名]","name[要返回的字段]","id[树型结构勾连的主键字段]","parentid[树型字段要勾连的parent字段名]","id[where字段]","12345[where值]");  
 * 比如getTreePathColValue("pub_corp_dept","name","id","parentid","id",getItemValue("createcorpid")); 返回:民生银行;广州分行;广州分行工会;
 */
import java.util.HashMap;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.bs.sysapp.ServerCacheDataFactory;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.common.FrameWorkCommServiceIfc;
import cn.com.infostrategy.ui.common.UIUtil;

public class GetTreePathTableName extends PostfixMathCommand {

	private int jepType = 0;

	private HashMap bsDataCacheMap = new HashMap(); //BS端的数据缓存!!!
	private TBUtil tbUtil = null; //

	public GetTreePathTableName(int _type) {
		this.jepType = _type;
		numberOfParameters = -1; //六个属性!!
	}

	public void run(Stack _stack) throws ParseException {
		checkStack(_stack); //
		HashMap parsMap = null; //存储所有参数的Map...
		HashMap trunMap = null; //截取的参数配置；
		try {
			String str_whereCondition = null;
			String str_whereFieldName = null;
			String str_linkedParentFieldName = null;
			String str_linkedIDFieldName = null;
			String str_returnfieldName = null;
			String str_tableName = null;

			if (curNumberOfParameters == 6) {
				str_whereCondition = (String) _stack.pop(); //where的条件
				str_whereFieldName = (String) _stack.pop(); //where的字段名
				str_linkedParentFieldName = (String) _stack.pop(); //
				str_linkedIDFieldName = (String) _stack.pop(); //
				str_returnfieldName = (String) _stack.pop(); //
				str_tableName = (String) _stack.pop(); //表名
			} else if (curNumberOfParameters == 7) {
				String str_pars = (String) _stack.pop(); //先把所有参数存起来
				parsMap = getTBUtil().convertStrToMapByExpress(str_pars, "/", "="); //
				str_whereCondition = (String) _stack.pop(); //where的条件
				str_whereFieldName = (String) _stack.pop(); //where的字段名
				str_linkedParentFieldName = (String) _stack.pop(); //
				str_linkedIDFieldName = (String) _stack.pop(); //
				str_returnfieldName = (String) _stack.pop(); //
				str_tableName = (String) _stack.pop(); //表名
			} else if (curNumberOfParameters == 8) {
				String str_truncation = (String) _stack.pop(); //把截取的参数存起来  [2012-08-16]郝明
				trunMap = getTBUtil().convertStrToMapByExpress(str_truncation, "/", "=");
				String str_pars = (String) _stack.pop(); //先把所有参数存起来
				parsMap = getTBUtil().convertStrToMapByExpress(str_pars, "/", "="); //
				str_whereCondition = (String) _stack.pop(); //where的条件
				str_whereFieldName = (String) _stack.pop(); //where的字段名
				str_linkedParentFieldName = (String) _stack.pop(); //
				str_linkedIDFieldName = (String) _stack.pop(); //
				str_returnfieldName = (String) _stack.pop(); //
				str_tableName = (String) _stack.pop(); //表名
			}

			if (str_whereCondition == null || str_whereCondition.trim().equals("")) { //如果值为空,则直接返回!!!
				_stack.push(""); //
				return;
			}

			if (this.jepType == WLTConstants.JEPTYPE_UI) { //如果是客户端
				FrameWorkCommServiceIfc commService = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class); //
				if (str_whereCondition.indexOf(";") >= 0) { //如果有多个,以前UI端有个bug即不支持多个!
					String[] str_idValues = getTBUtil().split(str_whereCondition, ";"); //
					HashMap returnMap = commService.getTreePathNameByRecords(null, str_tableName, str_linkedIDFieldName, str_returnfieldName, str_linkedParentFieldName, str_idValues); //
					StringBuilder sb_name = new StringBuilder(); //
					for (int i = 0; i < str_idValues.length; i++) {
						sb_name.append(returnMap.get(str_idValues[i]) + ";"); //
					}
					_stack.push(sb_name.toString()); //
					return; //
				} else { //如果是单个!!!
					HashVO[] parentHVs = commService.getTreePathVOsByOneRecord(null, str_tableName, str_linkedIDFieldName, str_linkedParentFieldName, str_whereFieldName, str_whereCondition); //取得所有父亲结点的数据
					boolean isTrimLevel1 = true; //
					String trunofnum = null; //通过数组截取，对应的参数是“是否截掉第一层”配置方式：是否截掉第一层=1245表示截取掉1245层，只留3
					if (parsMap != null && "N".equals(parsMap.get("是否截掉第一层"))) { //如果指定了不返回才不返回!!!
						isTrimLevel1 = false; //
					}
					if (parsMap != null && parsMap.get("是否截掉第一层") != null && !"YN".contains((String) parsMap.get("是否截掉第一层"))) { //如果参数值不是Y或N  那么要求是12345整数。
						trunofnum = (String) parsMap.get("是否截掉第一层");
					}
					String trun_itemname = null;
					String[] trun_itemvalue = null;
					if (trunMap != null) {
						trun_itemname = (String) trunMap.get("截掉的字段名称");
						String s1 = (String) trunMap.get("截掉的字段值");
						if (trun_itemname != null && s1 != null) {
							trun_itemvalue = getTBUtil().split(s1, ","); //参数值需要用英文逗号隔开
						}
					}

					StringBuilder sb_names = new StringBuilder(); //
					if (parentHVs != null && parentHVs.length > 0) {
						for (int i = (isTrimLevel1 && trunofnum == null ? 1 : 0); i < parentHVs.length; i++) { //遍历所有父亲结点!!!!
							if (trunofnum != null && trunofnum.contains(i + "")) {
								continue;
							}
							if (trun_itemname != null && trun_itemvalue != null && trun_itemvalue.length > 0) { //如果设置了特殊的截取方式，截掉某个节点后的所有子节点。
								boolean flag = false;//标示
								String value = parentHVs[i].getStringValue(trun_itemname);//根据配置的字段去父亲链链上的该字段值
								for (int j = 0; j < trun_itemvalue.length; j++) {//循环比较自定义的值
									if (trun_itemvalue[j].equals(value)) {
										flag = true;
										break;
									}
								}
								if (flag)
									break;
							}
							sb_names.append(parentHVs[i].getStringValue(str_returnfieldName, "") + "-"); //拼起来!!
						}
						if (sb_names.length() > 0) {
							_stack.push(sb_names.substring(0, sb_names.length() - 1)); //以前是减2的,结果在兴业项目中发现老是截取最后少一位!!应该是减1	
						} else {
							_stack.push("");
						}
						return;
					}
				}
			} else if (this.jepType == WLTConstants.JEPTYPE_BS) { //如果是BS端,则不能够采用UI端的多次取数据库的办法!!
				HashVO[] hvsAll = null; //
				if (str_tableName.equalsIgnoreCase("pub_corp_dept")) { //如果是机构,则特殊处理,从缓存取!!
					hvsAll = ServerCacheDataFactory.getInstance().getCorpCacheDataByAutoCreate(); //
				} else { //如果是普通表,则取下数据库并送入缓存!!
					String str_sql = ("select * from " + str_tableName).toLowerCase(); //
					if (!bsDataCacheMap.containsKey(str_sql)) { //如果缓存中没有,则在第一次要创建!!
						cn.com.infostrategy.bs.common.CommDMO commDMO = new cn.com.infostrategy.bs.common.CommDMO(); //
						bsDataCacheMap.put(str_sql, commDMO.getHashVoArrayAsTreeStructByDS(null, str_sql, str_linkedIDFieldName, str_linkedParentFieldName, null, null)); //一下子找出所有结点!并自动创建一个树
					}
					hvsAll = (HashVO[]) bsDataCacheMap.get(str_sql); ////
				}
				String str_cacheKey = "$" + str_tableName.toLowerCase() + "_" + str_linkedIDFieldName.toLowerCase(); //该公司中还要注册缓存!!
				HashMap thisHVOMap = getMapFromThisCache(str_cacheKey, hvsAll, str_linkedIDFieldName); //还是要注册下缓存,因为频繁通过For循环找还是性能慢!!!
				String str_return = getTreePathItemValueFromHashVOs(thisHVOMap, str_linkedIDFieldName, str_returnfieldName, str_whereFieldName, str_whereCondition, parsMap, trunMap); ////
				_stack.push(str_return); //
				return;

			}
			_stack.push(""); //
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	/*
	 *
	 * _trunMap参数配置如：截掉的字段名称=corptype/截掉的字段值=总行部门处室,一级分行部门处室,二级分行部门处室 会找到这个树中 字段corptype='总行部门处室'或'一级分行部门处室'的已经节点后面的全部截掉。
	 */
	private String getTreePathItemValueFromHashVOs(HashMap _allHVOMap, String str_linkedIDFieldName, String _returnfieldName, String _whereFieldName, String _whereCondition, HashMap _parsMap, HashMap _trunMap) {
		//long ll_1 = System.currentTimeMillis(); //
		boolean isReturnPathLinkName = true; //默认是返回路径的!!
		boolean isTrimLevel1 = true; //
		String trun_itemname = null; //自定义截取链的字段名称 例如机构类型 corptype
		String[] trun_itemvalue = null;//自定义截取链的字段值 如: 总行部门处室 分行部门处室  
		String trunofnum = null; //通过数组截取，对应的参数是“是否截掉第一层”配置方式：是否截掉第一层=1245表示截取掉1245层，只留3
		if (_parsMap != null && "N".equals(_parsMap.get("返回路径链名"))) { //如果指定了不返回才不返回!!!
			isReturnPathLinkName = false; //
		}
		if (_parsMap != null && "N".equals(_parsMap.get("是否截掉第一层"))) { //如果指定了不返回才不返回!!!
			isTrimLevel1 = false; //
		}
		if (_parsMap != null && _parsMap.get("是否截掉第一层") != null && !"YN".contains((String) _parsMap.get("是否截掉第一层"))) {
			trunofnum = (String) _parsMap.get("是否截掉第一层");
		}
		if (_trunMap != null) {
			trun_itemname = (String) _trunMap.get("截掉的字段名称");
			String s1 = (String) _trunMap.get("截掉的字段值");
			if (trun_itemname != null && s1 != null) {
				trun_itemvalue = getTBUtil().split(s1, ",");
			}
		}
		String[] str_whereConditions = getTBUtil().split(_whereCondition, ";"); //可能有多个id,即同时兼容单选与多选!!
		StringBuilder sb_tmp = new StringBuilder(""); //
		for (int w = 0; w < str_whereConditions.length; w++) { //遍历各个,如果是单选,则只循环一次!
			HashVO hvoitem = (HashVO) _allHVOMap.get(str_whereConditions[w]); //找下!
			if (hvoitem != null) { //如果找到了
				if (isReturnPathLinkName) { //如果是返回路径的!!!
					String str_parentids = hvoitem.getStringValue("$parentpathids"); //父亲路径
					if (str_parentids != null) { //如果有父亲记录
						String[] str_ids = getTBUtil().split(str_parentids, ";"); //得到父亲路径,分隔一下!!
						StringBuilder sb_retrunPathLinkName = new StringBuilder(); //
						for (int i = (isTrimLevel1 && trunofnum == null ? 1 : 0); i < str_ids.length; i++) { //不算根结点,比如机构树的根结点永远是兴业银行,但这个以后应该是个参数,默认是不带根结点!!
							if (trunofnum != null && trunofnum.contains(i + "")) {
								continue;
							}
							HashVO linkparentVO = (HashVO) _allHVOMap.get(str_ids[i]); //得到父亲的各个节点VO
							String str_itemName = linkparentVO.getStringValue(_returnfieldName); //
							boolean flag = false;
							if (trun_itemname != null && trun_itemvalue != null && trun_itemvalue.length > 0) {
								for (int j = 0; j < trun_itemvalue.length; j++) {
									String value = linkparentVO.getStringValue(trun_itemname);
									if (value != null && value.trim().equals(trun_itemvalue[j].trim())) {
										flag = true;
										break;
									}
								}
								if (flag) {
									break;
								}
							}
							sb_retrunPathLinkName.append(str_itemName); //拼起来!!
							if (i != str_ids.length - 1) {
								sb_retrunPathLinkName.append("-"); //拼接!
							}
						}
						int length = sb_retrunPathLinkName.length();
						if (length > 0) {
							String str = sb_retrunPathLinkName.substring(length - 1, length); //由于做了特殊的截取，最后一个可能会出来"-",需要截掉
							if ("-".equals(str)) {
								sb_retrunPathLinkName.replace(length - 1, length, "");
							}
						}
						sb_tmp.append(sb_retrunPathLinkName.toString()); //
						if (str_whereConditions.length > 1) { //长度>1才对 [2012-05-24郝明]
							sb_tmp.append(";"); //如果是多选,则还要加上分号!!!
						}
					}
				} else { //如果不返回路径!!
					String str_itemName = hvoitem.getStringValue(_returnfieldName); //
					sb_tmp.append(str_itemName); //
					if (str_whereConditions.length > 1) {
						sb_tmp.append(";"); //如果是多选,则还要加上分号!!!
					}
				}
			}
		}
		return sb_tmp.toString(); //返回!!!
	}

	private HashMap getMapFromThisCache(String _cacheKey, HashVO[] _hvs, String _id) {
		if (bsDataCacheMap.containsKey(_cacheKey)) {
			return (HashMap) bsDataCacheMap.get(_cacheKey); //
		} else {
			HashMap tempMap = new HashMap(); //创建哈希表
			for (int i = 0; i < _hvs.length; i++) {
				tempMap.put(_hvs[i].getStringValue(_id), _hvs[i]); //
			}
			bsDataCacheMap.put(_cacheKey, tempMap); //
			return tempMap; //直接返回
		}
	}

	private TBUtil getTBUtil() {
		if (this.tbUtil == null) {
			tbUtil = new TBUtil(); //
		}
		return tbUtil; //
	}
}
