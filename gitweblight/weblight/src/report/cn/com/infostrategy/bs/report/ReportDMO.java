package cn.com.infostrategy.bs.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.HashVOStruct;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.BillTreeDefaultMutableTreeNode;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.print.PubPrintItemBandVO;
import cn.com.infostrategy.to.print.PubPrintTempletVO;
import cn.com.infostrategy.to.report.BillChartItemVO;
import cn.com.infostrategy.to.report.BillChartVO;
import cn.com.infostrategy.to.report.ReportUtil;

/**
 * 报表数据处理类
 * 关键,要实现BI的一些功能,比如任意拖动维度
 * 还要实现一种任意层层叠加的工具，可以一层又一层的处理HashVO.
 * @author xch
 *
 */
public class ReportDMO extends AbstractDMO {

	org.apache.log4j.Logger logger = WLTLogger.getLogger(ReportDMO.class); //

	private TBUtil tbUtil = new TBUtil(); //

	/**
	 * 分组统计 比如groupHashVOs(hvs, "sum(c1) s1", "code,name"); //
	 * @param _hvs
	 * @param _groupFunc 分组函数,目前包括sum(account),count(*)两个,Max(account),Min(account),以后支持Avg(account)等
	 * @param _groupField.
	 * @return code,name,s1
	 */
	public HashVO[] groupHashVOs(HashVO[] _hvs, String _groupFunc, String _groupFields) {
		//先分析一下参与分组的列
		String[] str_groupFields = tbUtil.split(_groupFields, ","); //
		for (int i = 0; i < str_groupFields.length; i++) {
			str_groupFields[i] = str_groupFields[i].trim(); //去掉空格
		}

		//先分析一个函数语法,看有几个伪列...
		String[] str_items = tbUtil.split(_groupFunc, ","); //根据,分隔..
		String[] str_funnames = new String[str_items.length]; //函数名
		String[] str_funpars = new String[str_items.length]; //函数参数
		String[] str_boguscols = new String[str_items.length]; //伪列名
		for (int i = 0; i < str_items.length; i++) {
			str_items[i] = str_items[i].trim(); //先去掉首尾空格
			int li_pos_1 = str_items[i].indexOf("("); //找左括号
			int li_pos_2 = str_items[i].indexOf(")"); //找右括号
			str_funnames[i] = str_items[i].substring(0, li_pos_1).trim(); //函数名
			str_funpars[i] = str_items[i].substring(li_pos_1 + 1, li_pos_2).trim(); //函数参数
			str_boguscols[i] = str_items[i].substring(li_pos_2 + 1, str_items[i].length()).trim(); //函数参数
			//System.out.println("函数名[" + str_funnames[i] + "],函数参数[" + str_funpars[i] + "],假列[" + str_boguscols[i] + "]"); //
		}

		//遍历数据
		VectorMap map_rowdata = new VectorMap(); //
		for (int i = 0; i < _hvs.length; i++) { //
			StringBuffer sb_rowvalue = new StringBuffer(); //
			for (int j = 0; j < str_groupFields.length; j++) { //
				String str_itemvalue = _hvs[i].getStringValue(str_groupFields[j]); //
				sb_rowvalue.append(str_itemvalue + "#"); //
			}

			if (map_rowdata.containsKey(sb_rowvalue.toString())) { //如果已经有了..
				HashVO hvo_group = (HashVO) map_rowdata.get(sb_rowvalue.toString()); //
				//遍列所有伪列
				for (int j = 0; j < str_boguscols.length; j++) {
					BigDecimal bigDecimal_olddata = hvo_group.getBigDecimalValue(str_boguscols[j]); //原来的数据
					BigDecimal bigDecimal_newdata = null;
					if (str_funnames[j].equalsIgnoreCase("sum")) { //在原有数据上加上该行数据..
						bigDecimal_newdata = bigDecimal_olddata.add(_hvs[i].getBigDecimalValue(str_funpars[j]) == null ? new BigDecimal(0) : _hvs[i].getBigDecimalValue(str_funpars[j])); //
					} else if (str_funnames[j].equalsIgnoreCase("count")) {
						bigDecimal_newdata = bigDecimal_olddata.add(new BigDecimal(1)); //
					} else if (str_funnames[j].equalsIgnoreCase("max")) {
						if (bigDecimal_olddata == null) {
							if (_hvs[i].getBigDecimalValue(str_funpars[j]) == null) {
							} else {
								bigDecimal_newdata = _hvs[i].getBigDecimalValue(str_funpars[j]);
							}
						} else {
							if (_hvs[i].getBigDecimalValue(str_funpars[j]) == null) {
							} else {
								if (_hvs[i].getBigDecimalValue(str_funpars[j]).compareTo(bigDecimal_olddata) > 0) { //如果大前数据大于旧数据,则将当前数据置换..
									bigDecimal_newdata = _hvs[i].getBigDecimalValue(str_funpars[j]); //
								}
							}
						}
					} else if (str_funnames[j].equalsIgnoreCase("min")) {
						if (bigDecimal_olddata == null) {
							if (_hvs[i].getBigDecimalValue(str_funpars[j]) == null) {
							} else {
								bigDecimal_newdata = _hvs[i].getBigDecimalValue(str_funpars[j]);
							}
						} else {
							if (_hvs[i].getBigDecimalValue(str_funpars[j]) == null) {
								bigDecimal_newdata = _hvs[i].getBigDecimalValue(str_funpars[j]); //
							} else {
								if (_hvs[i].getBigDecimalValue(str_funpars[j]).compareTo(bigDecimal_olddata) < 0) { //如果大前数据小于旧数据,则将当前数据置换..
									bigDecimal_newdata = _hvs[i].getBigDecimalValue(str_funpars[j]); //
								}
							}
						}
					} else if (str_funnames[j].equalsIgnoreCase("avg")) { //平均比较麻烦,先搞假数据
						bigDecimal_newdata = new BigDecimal(5); //
					}
					hvo_group.setAttributeValue(str_boguscols[j], bigDecimal_newdata); //将新数据送入
				}
			} else { //如果还没有
				HashVO hvo_group = new HashVO(); //创建结果
				for (int j = 0; j < str_groupFields.length; j++) { //
					String str_itemvalue = _hvs[i].getStringValue(str_groupFields[j]); //
					hvo_group.setAttributeValue(str_groupFields[j], str_itemvalue); //先将分组统计的列送入
				}

				//再加上所有伪列....
				for (int j = 0; j < str_boguscols.length; j++) {
					if (str_funnames[j].equalsIgnoreCase("sum")) {
						hvo_group.setAttributeValue(str_boguscols[j], _hvs[i].getBigDecimalValue(str_funpars[j]) == null ? new BigDecimal(0) : _hvs[i].getBigDecimalValue(str_funpars[j])); //如果是sum求和,则将第一个值直接送入	
					} else if (str_funnames[j].equalsIgnoreCase("count")) {
						hvo_group.setAttributeValue(str_boguscols[j], new BigDecimal(1)); //	
					} else if (str_funnames[j].equalsIgnoreCase("max")) {
						hvo_group.setAttributeValue(str_boguscols[j], _hvs[i].getBigDecimalValue(str_funpars[j])); //	
					} else if (str_funnames[j].equalsIgnoreCase("min")) {
						hvo_group.setAttributeValue(str_boguscols[j], _hvs[i].getBigDecimalValue(str_funpars[j])); //	
					} else if (str_funnames[j].equalsIgnoreCase("avg")) { //平均比较麻烦,先搞假数据
						hvo_group.setAttributeValue(str_boguscols[j], new BigDecimal(5));
					}
				}

				map_rowdata.put(sb_rowvalue.toString(), hvo_group); //
			}
		}

		Object[] objs = map_rowdata.getValues(); //
		HashVO[] returnVOs = new HashVO[objs.length]; //
		for (int i = 0; i < returnVOs.length; i++) {
			returnVOs[i] = (HashVO) objs[i];
		}
		return returnVOs; //
	}

	/**
	 * 取得树型结构,返回哈希表,key是唯一性id,value是从根结点至当前结点的名称的一维数组
	 * @param _sql
	 * @param _idName
	 * @param _nameName
	 * @param _parentIdName
	 * @return
	 * @throws Exception
	 */
	public HashMap getTreeStruct(String _sql) throws Exception {
		//String str_sql = "select " + _idName + "," + _nameName + "," + _parentIdName + " from " + _tableName;
		HashVO[] hvsData = new CommDMO().getHashVoArrayByDS(null, _sql); //

		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new RefItemVO("-99999", null, "根结点")); //
		DefaultMutableTreeNode[] node_level_1 = new DefaultMutableTreeNode[hvsData.length]; //创建结点
		HashMap map_parent = new HashMap();
		for (int i = 0; i < hvsData.length; i++) {
			node_level_1[i] = new BillTreeDefaultMutableTreeNode(new RefItemVO(hvsData[i].getStringValue(0), hvsData[i].getStringValue(2), hvsData[i].getStringValue(1))); //SQL语句中的第一列是id,第2列是名称,第三列是parentid
			map_parent.put(hvsData[i].getStringValue(0), node_level_1[i]); //
			rootNode.add(node_level_1[i]); // 加入根结点
		}

		//构建树,就是一个找爸爸的游戏!!!
		RefItemVO nodeVO = null; //
		String str_pk_parentPK = null; //
		for (int i = 0; i < node_level_1.length; i++) {
			nodeVO = (RefItemVO) node_level_1[i].getUserObject();
			str_pk_parentPK = nodeVO.getCode(); // 父亲主键
			if (str_pk_parentPK == null || str_pk_parentPK.trim().equals("")) {
				continue;
			}

			DefaultMutableTreeNode parentnode = (DefaultMutableTreeNode) map_parent.get(str_pk_parentPK); //
			if (parentnode != null) { //如果找到爸爸了..
				try {
					parentnode.add(node_level_1[i]); //在爸爸下面加入我..
				} catch (Exception ex) {
					logger.error("在[" + parentnode + "]上创建子结点[" + node_level_1[i] + "]失败!!");
					ex.printStackTrace(); //
				}
			}
		}

		HashMap mapReturn = new HashMap(); //
		for (int i = 0; i < node_level_1.length; i++) {
			RefItemVO nodeItemVO = (RefItemVO) node_level_1[i].getUserObject();
			String str_id = nodeItemVO.getId(); //
			TreeNode[] pathNodes = node_level_1[i].getPath(); //
			String[] str_pathNames = new String[pathNodes.length]; //
			for (int j = 0; j < pathNodes.length; j++) {
				str_pathNames[j] = ((RefItemVO) ((DefaultMutableTreeNode) pathNodes[j]).getUserObject()).getName(); //
			}
			mapReturn.put(str_id, str_pathNames); //
		}
		return mapReturn;
	}

	private void myReport(String[] _deptid) throws Exception {
		String str_incondition = new TBUtil().getInCondition(_deptid);
		HashMap map_id_linkcode = new CommDMO().getHashMapBySQLByDS(null, "select id,linkcode from pub_corp_dept"); //
		HashMap map_linkcode_name = new CommDMO().getHashMapBySQLByDS(null, "select linkcode,name from pub_corp_dept"); //
		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, "select * from lbs_cases where deptid in (" + str_incondition + ")"); //
		for (int i = 0; i < hvs.length; i++) {
			String str_deptid = hvs[i].getStringValue("createdeptid"); //
			String str_linkcode = (String) map_id_linkcode.get(str_deptid); //
			hvs[i].setAttributeValue("linkcode", str_linkcode); //增加新的列
			hvs[i].setAttributeValue("linkcode_8", str_linkcode.substring(0, 8)); //增加新的列\
			String str_fnname = (String) map_linkcode_name.get(str_linkcode.substring(0, 8));
			hvs[i].setAttributeValue("linkcode_8_name", str_fnname); //增加新的列
		}

		HashVO[] hvsreturn = groupHashVOs(hvs, "sum(account) s1,count(*) c1,max(a2)", "linkcode_8_name,busitype");
		//广州分行 公司业务 80  5  6
		//广州分行 个人业务 80  5  6
		//苏州分行 90  6
		//上海分行 100

	}

	/**
	 * 对一个HashVO[]增加一个新列_newItemName,新列的数值的计算逻辑是从一个表中(根据sql生成),拿旧列的值品配sql返回结果集中的第1列，返回第2列
	 * 说白了就是根据id返回名称,比如addOneFieldFromOtherTable(hvs,"username","userid","select id,name from pub_user");
	 * @param _hvs
	 * @param _newItemName
	 * @param _fromOldItemName
	 * @param _sql
	 * @throws Exception
	 */
	public void addOneFieldFromOtherTable(HashVO[] _hvs, String _newItemName, String _fromOldItemName, String _sql) throws Exception {
		HashMap custStateMap = getHashMapDataBysql(_sql); //""
		int li_errorcount = 0; //
		for (int i = 0; i < _hvs.length; i++) {
			String str_custids = _hvs[i].getStringValue(_fromOldItemName); //
			if (str_custids == null || str_custids.trim().equals("")) {
				_hvs[i].setAttributeValue(_newItemName, "【空值】"); //findchannel
			} else {
				String str_convertname = ""; //
				String[] str_items = tbUtil.split(str_custids, ";"); //分隔一下!
				for (int j = 0; j < str_items.length; j++) {
					String str_itemName = (String) custStateMap.get(str_items[j]); //
					if (str_itemName != null) {
						str_convertname = str_convertname + str_itemName + ";"; //
					} else {
						li_errorcount++;
						if (li_errorcount < 9) {
							System.err.println("表型关联时,字段【" + _fromOldItemName + "】中值【" + str_custids + "】的子项【" + str_items[j] + "】从SQL【" + _sql + "】中没关联到值！！！"); //
						}
					}
				}
				if (str_convertname.endsWith(";")) {
					str_convertname = str_convertname.substring(0, str_convertname.length() - 1); //
				}
				_hvs[i].setAttributeValue(_newItemName, str_convertname); //增加新的值
			}
		}

		if (li_errorcount > 0) {
			System.err.println("表型关联时一共发生[" + li_errorcount + "]次关联不上,字段【" + _fromOldItemName + "】【" + _sql + "】！！！"); //
		}
	}

	/**
	 * 
	 * @param _hvs 原始vos
	 * @param _fromOldItemName vo中的比较字段
	 * @param newItems 新添加的列 
	 * @param _sql 如果
	 * @param _leftjoinTableKey 表中的比较键。
	 */
	public void addMoreFieldFromOtherTable(HashVO[] _hvs, String _fromOldItemName, String[] newItems, String _sql, String _leftjoinTableKey) throws Exception {
		HashVO t_vos[] = new CommDMO().getHashVoArrayByDS(null, _sql); //查出来的取值加入的vos
		if (t_vos.length == 0) {
			System.err.println("HashVO扩展列,SQL【" + _sql + "】没有查到任何数据！！！"); //
			return;
		}
		HashMap<String, HashVO> map = new HashMap<String, HashVO>();
		for (int i = 0; i < t_vos.length; i++) {
			map.put(t_vos[i].getStringValue(_leftjoinTableKey), t_vos[i]);
		}
		for (int i = 0; i < _hvs.length; i++) {
			String value = _hvs[i].getStringValue(_fromOldItemName);
			if (value == null) {
				for (int j = 0; j < newItems.length; j++) {
					_hvs[i].setAttributeValue(newItems[j], "【空值】");
				}
			} else {
				HashVO q_hashvo = map.get(value);
				if (q_hashvo == null) {
					System.err.println("HashVO扩展列时,根据字段【" + _fromOldItemName + "】的值在SQL【" + _sql + "】结果中匹配项【" + _leftjoinTableKey + "】中没关联到值！！！"); //
					for (int j = 0; j < newItems.length; j++) {
						_hvs[i].setAttributeValue(newItems[j], "【空值】");
					}
				} else {
					for (int j = 0; j < newItems.length; j++) {
						_hvs[i].setAttributeValue(newItems[j], q_hashvo.getStringValue(newItems[j]));
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param _hvs
	 * @param _newItemName
	 * @param _fromOldItemName
	 * @param _sql 比如[select id,name,parentid from bsd_bsact]这个SQL要是三列,且第一列是主键,第二列是用于显示的名称,第三列是勾连主键的parentid,但具体列名倒没有特别要求!只要列的含义是对的!
	 * @param _level
	 * @throws Exception
	 */
	public void addOneFieldFromOtherTree(HashVO[] _hvs, String _newItemName, String _fromOldItemName, String _sql, int _level) throws Exception {
		addOneFieldFromOtherTree(_hvs, _newItemName, _fromOldItemName, _sql, _level, false, 1); //
	}

	/**
	 * 
	 * @param _hvs
	 * @param _newItemName
	 * @param _fromOldItemName
	 * @param _sql 比如[select id,name,parentid from bsd_bsact]这个SQL要是三列,且第一列是主键,第二列是用于显示的名称,第三列是勾连主键的parentid,但具体列名倒没有特别要求!只要列的含义是对的!
	 * @param _level
	 * @param _isLinkName 是否将路径名拼接在一起?? 比如【上海分行-浦东支行】    
	 * @param _linkName_StartLevel 经常需要将名称拼接起来,但因为有的树的第一层永远是一样的,即感觉像废话,所以有时需要从第二层或某一层开始拼接!
	 * @throws Exception
	 */
	public void addOneFieldFromOtherTree(HashVO[] _hvs, String _newItemName, String _fromOldItemName, String _sql, int _level, boolean _isLinkName, int _linkName_StartLevel) throws Exception {
		HashMap mapTemp = getTreeStruct(_sql); //
		int li_linkfailCount = 0; //
		int li_arrayoutCount = 0;//
		for (int i = 0; i < _hvs.length; i++) {
			String str_custids = _hvs[i].getStringValue(_fromOldItemName); //
			if (str_custids == null || str_custids.trim().equals("")) {
				_hvs[i].setAttributeValue(_newItemName, "【空值】"); // findchannel
			} else {
				StringBuilder sb_convertname = new StringBuilder(); //
				String[] str_items = tbUtil.split(str_custids, ";");
				for (int j = 0; j < str_items.length; j++) {
					String[] str_treePathNames = (String[]) mapTemp.get(str_items[j]); //
					if (str_treePathNames == null || str_treePathNames.length == 0) {
						li_linkfailCount++;
					} else {
						//Gwang 2016-05-06 修改为： 如果路径不够长， 有多少返回多！ 原来代码直接返回空！
						if (_isLinkName && _linkName_StartLevel <= _level) {
							StringBuilder sb_kk = new StringBuilder(); //
							for (int k = _linkName_StartLevel; k <= _level; k++) {
								if (str_treePathNames.length == k) {
									break;
								}
								sb_kk.append(str_treePathNames[k]); //
								if (k < _level) {
									sb_kk.append("-"); //
								}
							}
							sb_convertname.append(sb_kk.toString()); //
						} else {
							if (_level >= str_treePathNames.length) {
								sb_convertname.append(str_treePathNames[str_treePathNames.length - 1]); // 直接加入【李春娟/2016-12-15】
							} else {
								sb_convertname.append(str_treePathNames[_level]); // 直接加入
							}

						}
						if (j != str_items.length - 1) { // 如果不是最后一个则加分号!
							sb_convertname.append(";"); //
						}

					}
				}

				String sb = sb_convertname.toString();
				if (sb.endsWith("-")) {
					sb = sb.substring(0, sb.length() - 1);
				}
				_hvs[i].setAttributeValue(_newItemName, sb); // 增加新列
			}
		}
		System.err.println("树型关联时,字段【" + _fromOldItemName + "】从SQL【" + _sql + "】中取第【" + _level + "】层时发生【" + li_linkfailCount + "】次没关联到值！！！"); //
		System.err.println("树型关联时,字段【" + _fromOldItemName + "】从SQL【" + _sql + "】中取第【" + _level + "】层时发生【" + li_arrayoutCount + "】次越界！！！"); //
	}

	/**
	 * 根据SQL语句查询数据，将结果集的第1作为key,第2列作为Value，返回一个哈希表
	 * @param _sql
	 * @return
	 * @throws Exception
	 */
	private HashMap getHashMapDataBysql(String _sql) throws Exception {
		String[][] str_data = new CommDMO().getStringArrayByDS(null, _sql); //
		HashMap returnMap = new HashMap(); //
		for (int i = 0; i < str_data.length; i++) {
			returnMap.put(str_data[i][0], str_data[i][1]); //
		}
		return returnMap;
	}

	/**
	 * 对一个HashVO[]的两个列进行计算,如果其中有分号相隔,则自动计算成两个,这样就不需要使用表关联，而直接针对一个表进行处理!
	 * 然后是根据其中的多选情况自动拆分计算成多个，相当于一对多关联时扩容成多条记录!!
	 * @param _hvs
	 * @param _GroupLevel_1
	 * @param _GroupLevel_2
	 * @return
	 * @throws Exception
	 */
	public HashVO[] computeHashVOTwoFieldRecords(HashVO[] _hvs, String _GroupLevel_1, String _GroupLevel_2) throws Exception {
		HashMap map_compute = new HashMap(); //
		for (int i = 0; i < _hvs.length; i++) { //
			String str_1 = _hvs[i].getStringValue(_GroupLevel_1); //
			String str_2 = _hvs[i].getStringValue(_GroupLevel_2); //
			if (str_1 == null || str_1.trim().equals("")) {
				str_1 = "【空值】";
			}
			if (str_2 == null || str_2.trim().equals("")) {
				str_2 = "【空值】";
			}
			String[] str_oneRecordInfo = getOneRecordCompute(str_1, str_2); //一行记录中根据分号计算的各种情况,正常情况只有一个!核心思想所在!!
			for (int j = 0; j < str_oneRecordInfo.length; j++) {
				if (map_compute.containsKey(str_oneRecordInfo[j])) { //
					Integer oldInteger = (Integer) map_compute.get(str_oneRecordInfo[j]); //
					map_compute.put(str_oneRecordInfo[j], new Integer(oldInteger.intValue() + 1)); //累加
				} else {
					map_compute.put(str_oneRecordInfo[j], new Integer(1)); //
				}
			}
		}
		String[] str_allKeys = (String[]) map_compute.keySet().toArray(new String[0]);
		HashVO[] returnVOs = new HashVO[str_allKeys.length]; //
		for (int i = 0; i < str_allKeys.length; i++) {
			returnVOs[i] = new HashVO(); //
			String[] str_keyItems = tbUtil.split(str_allKeys[i], "#"); //
			returnVOs[i].setAttributeValue(_GroupLevel_1, str_keyItems[0]); //
			returnVOs[i].setAttributeValue(_GroupLevel_2, str_keyItems[1]); //
			returnVOs[i].setAttributeValue("记录数", map_compute.get(str_allKeys[i])); //
		}

		return returnVOs;
	}

	private String[] getOneRecordCompute(String _str1, String _str2) {
		String[] str_array1 = tbUtil.split(_str1, ";");
		String[] str_array2 = tbUtil.split(_str2, ";");

		ArrayList al_1 = new ArrayList(); //
		for (int i = 0; i < str_array1.length; i++) {
			if (str_array1[i] != null && !str_array1[i].trim().equals("")) {
				al_1.add(str_array1[i]); //
			}
		}
		str_array1 = (String[]) al_1.toArray(new String[0]); //

		ArrayList al_2 = new ArrayList(); //
		for (int i = 0; i < str_array2.length; i++) {
			if (str_array2[i] != null && !str_array2[i].trim().equals("")) {
				al_2.add(str_array2[i]); //
			}
		}
		str_array2 = (String[]) al_2.toArray(new String[0]); //

		ArrayList al_temp = new ArrayList(); //
		for (int i = 0; i < str_array1.length; i++) {
			for (int j = 0; j < str_array2.length; j++) {
				String str_key = str_array1[i] + "#" + str_array2[j]; //
				//if (!al_temp.contains(str_key)) { //如果已有了,则不算了,即重复的只算一次!!感觉在不同时候需要不同处理!! 如果是员工性别，则不能过滤,如果是业务分类
				al_temp.add(str_key); //
				//}
			}
		}
		return (String[]) al_temp.toArray(new String[0]); //
	}

	/**
	 * 
	 * @param _oldVOs
	 * @param _newHVO
	 * @param _oldfield
	 * @param _newfield
	 * @return
	 */
	public HashVO[] leftOuterJoinNewHashVO(HashVO[] _oldVOs, String _sql, String _oldfield, String _newfield) throws Exception {
		ReportUtil util = new ReportUtil(); //
		HashVOStruct hvs = new CommDMO().getHashVoStructByDS(null, _sql); //
		return util.leftOuterJoinNewHashVO(_oldVOs, hvs, _oldfield, _newfield); //
	}

	/**
	 * 与一个表关联,取得那个表中的某个字段放入原来的HashVO[]中,
	 * @param _oldVOs 旧数据
	 * @param _newItemName 新增的列名
	 * @param _tableName 物理表名
	 * @param _dbfieldName 想取的列名
	 * @param _whereDBFielKey 表中关联的字段名
	 * @param _fromItemKey 与原来HashVO[]中关联的字段名
	 * @throws Exception
	 */
	public void leftOuterJoin_TableFieldName(HashVO[] _oldVOs, String _newItemName, String _tableName, String _joinedFieldName, String _whereDBFielKey, String _fromItemKey) throws Exception {
		String str_sql = "select " + _whereDBFielKey + "," + _joinedFieldName + " from " + _tableName; //
		HashVO[] hvs_leftTable = new CommDMO().getHashVoArrayByDS(null, str_sql); //
		HashMap map_leftTable = new HashMap(); //
		for (int i = 0; i < hvs_leftTable.length; i++) {
			map_leftTable.put(hvs_leftTable[i].getStringValue(_whereDBFielKey), hvs_leftTable[i].getStringValue(_joinedFieldName)); //
		}

		String str_fromValue = null; //
		String str_leftTableFieldName = null; //
		for (int i = 0; i < _oldVOs.length; i++) {
			str_fromValue = _oldVOs[i].getStringValue(_fromItemKey); //
			if (str_fromValue == null) {
				_oldVOs[i].setAttributeValue(_newItemName, ""); //
			} else {
				str_leftTableFieldName = (String) map_leftTable.get(str_fromValue); //
				_oldVOs[i].setAttributeValue(_newItemName, str_leftTableFieldName); //
				//System.out.println("送入[" + _newItemName + "]=[" + str_leftTableFieldName + "]"); //
			}
		}
	}

	public void leftOuterJoin_TreeTableFieldName(HashVO[] _oldVOs, String _newItemName, String _tableName, String _joinedFieldName, String _whereDBFielKey, String _fromItemKey, String _treeLinkedIdField, String _treeLinkedParentIdField, int _getLevel) throws Exception {
		leftOuterJoin_TreeTableFieldName(_oldVOs, _newItemName, _tableName, _joinedFieldName, _whereDBFielKey, _fromItemKey, _treeLinkedIdField, _treeLinkedParentIdField, _getLevel, false);
	}

	/**
	 * 与一个树形表关联,去取得那个表中的第几层的某个字段的值!!
	 * @param _oldVOs 原来的数据
	 * @param _newItemName 新增的字段名
	 * @param _tableName  查询的物理表名
	 * @param _joinedFieldName 需要加入的字段名
	 * @param _whereDBFielKey 数据库中关联的字段名
	 * @param _fromItemKey 与原来的HashVO中的哪个字段连接
	 * @param _treeLinkedIdField 树本身自连接的主键字段名
	 * @param _treeLinkedParentIdField 树本身自连接的外键字段名
	 * @param _getLevel 想取第几层
	 * @param isReturnFullPath 是否返回树的全路径 Gwang 2012-3-15 增加
	 * @throws Exception 
	 */
	public void leftOuterJoin_TreeTableFieldName(HashVO[] _oldVOs, String _newItemName, String _tableName, String _joinedFieldName, String _whereDBFielKey, String _fromItemKey, String _treeLinkedIdField, String _treeLinkedParentIdField, int _getLevel, boolean isReturnFullPath) throws Exception {
		//先要构造一颗树!!!因为我最后要要取得某个结点的链条上的第几层!!!!所以必须先要构造一颗树!!!
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("root"); //
		String str_sql = "select " + _whereDBFielKey + " id," + _joinedFieldName + " name," + _treeLinkedIdField + " linkid," + _treeLinkedParentIdField + " linkparentid from " + _tableName; //
		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, str_sql); //
		DefaultMutableTreeNode[] node_level_1 = new DefaultMutableTreeNode[hvs.length]; // 创建所有结点数组
		HashMap map_assign = new HashMap(); //
		HashMap map_parent = new HashMap();
		for (int i = 0; i < hvs.length; i++) {
			hvs[i].setToStringFieldName("name"); //
			node_level_1[i] = new DefaultMutableTreeNode(hvs[i]); // 创建各个结点
			rootNode.add(node_level_1[i]); // 加入根结点
			map_assign.put(hvs[i].getStringValue("id"), node_level_1[i]); //
			map_parent.put(hvs[i].getStringValue("linkid"), node_level_1[i]); //记录树的关系
		}

		HashVO nodeVO = null; //
		String str_pk_parentPK = null; //
		HashVO nodeVO_2 = null; //
		String str_pk_2 = null; //
		for (int i = 0; i < node_level_1.length; i++) {
			nodeVO = (HashVO) node_level_1[i].getUserObject();
			str_pk_parentPK = nodeVO.getStringValue("linkparentid"); // 父亲主键
			if (str_pk_parentPK == null || str_pk_parentPK.trim().equals("")) {
				continue;
			}

			DefaultMutableTreeNode parentnode = (DefaultMutableTreeNode) map_parent.get(str_pk_parentPK); //
			if (parentnode != null) {
				try {
					parentnode.add(node_level_1[i]); //
				} catch (Exception ex) {
					logger.error("在[" + parentnode + "]上创建子结点[" + node_level_1[i] + "]失败!!");
					ex.printStackTrace(); //
				}
			}
		}

		String str_fromItemValue = null; //
		//构造树结束后才真正处理数据!!!
		for (int i = 0; i < _oldVOs.length; i++) {
			str_fromItemValue = _oldVOs[i].getStringValue(_fromItemKey); //
			if (str_fromItemValue == null) {
				_oldVOs[i].setAttributeValue(_newItemName, ""); //by haoming 2015-12-29，原代码hvo[i].setAttributeValue...
			} else {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) map_assign.get(str_fromItemValue); //
				if (node != null && _getLevel > 0 && node.getLevel() >= _getLevel) { //如果找到的结点层次大于等于想取的层次!!
					TreeNode[] parentNodes = node.getPath();
					//========Gwang 2012-3-15 增加 =============>/
					//返回树的全路径
					String nodeName = "";
					if (isReturnFullPath) {
						for (int ii = _getLevel, n = parentNodes.length; ii < n; ii++) {
							nodeName += parentNodes[ii].toString() + "-";
						}
						nodeName = nodeName.substring(0, nodeName.length() - 1);
					} else {
						nodeName = parentNodes[_getLevel].toString();
					}
					_oldVOs[i].setAttributeValue(_newItemName, nodeName); //
					//<=========Gwang 2012-3-15 增加 =============/

					//HashVO findHVO = (HashVO) (((DefaultMutableTreeNode) parentNodes[_getLevel]).getUserObject()); //
					//_oldVOs[i].setAttributeValue(_newItemName, findHVO.getStringValue("name")); //
				} else {
					_oldVOs[i].setAttributeValue(_newItemName, ""); //
				}
			}
		}
	}

	/**
	 * 为每个机构计算出所属机构大类
	 * @param _oldVOs
	 * @param _newItem
	 * @param _fromItemKey 
	 * @param _newCorpType 有以下六种取值:
	 * bl_zhonghbm 所属总行部门,一级分类
	 * bl_fengh 所属分行,一级分类
	 * bl_fenghbm 所属分行部门
	 * bl_zhih 所属支行
	 * bl_shiyb 所属事业部,一级分类
	 * bl_shiybfb 所属事业部分部
	 * @throws Exception
	 */
	public void leftOuterJoin_CorpBelongCorpName(HashVO[] _oldVOs, String _newItem, String _fromItemKey, String _belongCorpType) throws Exception {
		leftOuterJoin_CorpBelongCorpName(_oldVOs, new String[] { _newItem }, _fromItemKey, new String[] { _belongCorpType }); //
	}

	/**
	 * 为每个HashVO加上新的机构类型,比如所属分行,所属事业部等,所属总行部门等,但有个问题是有时有些机构只能同时属于一个,这时就会出现记录交叉为空的情况!!
	 * @param _oldVOs
	 * @param _newItems
	 * @param _fromItemKey
	 * @param _newCorpType 有以下六种取值:
	 * bl_zhonghbm 所属总行部门,一级分类
	 * bl_fengh 所属分行,一级分类
	 * bl_fenghbm 所属分行部门
	 * bl_zhih 所属支行
	 * bl_shiyb 所属事业部,一级分类
	 * bl_shiybfb 所属事业部分部
	 */
	public void leftOuterJoin_CorpBelongCorpName(HashVO[] _oldVOs, String[] _newItems, String _fromItemKey, String[] _belongCorpTypes) throws Exception {
		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, "select * from pub_corp_dept"); //先找出所有机构,放入缓存,为了提高性能
		HashMap map_allCorp = new HashMap(); //
		for (int i = 0; i < hvs.length; i++) {
			map_allCorp.put(hvs[i].getStringValue("id"), hvs[i]); //
		}

		String str_bl_corp_id = null; //
		for (int j = 0; j < _oldVOs.length; j++) {
			String str_corpId = _oldVOs[j].getStringValue(_fromItemKey); //机构ID
			if (str_corpId == null || str_corpId.trim().equals("")) {
				for (int k = 0; k < _newItems.length; k++) {
					_oldVOs[j].setAttributeValue(_newItems[k], ""); //
				}
			} else {
				HashVO hvo_corp = (HashVO) map_allCorp.get(str_corpId); //去机构中找!!!
				if (hvo_corp == null) { //如果没找到
					for (int k = 0; k < _newItems.length; k++) {
						_oldVOs[j].setAttributeValue(_newItems[k], ""); //
					}
				} else { //如果找到!!
					for (int k = 0; k < _newItems.length; k++) {
						str_bl_corp_id = hvo_corp.getStringValue(getCorpDBFileName(_belongCorpTypes[k])); //取得所属机构类型的ID值!!!
						if (str_bl_corp_id == null || str_bl_corp_id.trim().equals("")) {
							_oldVOs[j].setAttributeValue(_newItems[k], ""); //
						} else {
							HashVO hvo_bl_corp = (HashVO) map_allCorp.get(str_bl_corp_id); //
							_oldVOs[j].setAttributeValue(_newItems[k], hvo_bl_corp.getStringValue("name", "")); //
						}
					}
				}
			}
		}
	}

	/**
	 * 根据机构类型返回数据库中实际列名
	 * @param _corpType
	 * @return
	 */
	private String getCorpDBFileName(String _corpType) {
		return _corpType; //
	}

	/**
	 * 将一个HashVO数组转换成图表VO
	 * @param _hvs
	 * @return
	 */
	public BillChartVO convertHashVOToChartVO(HashVO[] hvs) {
		String[] str_prodtype = null;
		String[] str_months = null;
		String[][] str_initdata = new String[hvs.length][3]; //
		HashMap hname = new HashMap();
		HashMap hyear = new HashMap();
		for (int i = 0; i < hvs.length; i++) {
			str_initdata[i][1] = hvs[i].getStringValue(0); //
			str_initdata[i][0] = hvs[i].getStringValue(1); //
			str_initdata[i][2] = hvs[i].getStringValue(2); //
			hyear.put(hvs[i].getStringValue(0), null);
			hname.put(hvs[i].getStringValue(1), null);
		}

		str_prodtype = (String[]) hname.keySet().toArray(new String[0]);
		str_months = (String[]) hyear.keySet().toArray(new String[0]);

		new TBUtil().sortStrs(str_prodtype); //排序一把
		new TBUtil().sortStrs(str_months); //排序一把,比如第1季度,第2季度,第3季度...

		BillChartItemVO[][] ld_data = new BillChartItemVO[str_prodtype.length][str_months.length]; //
		for (int i = 0; i < str_prodtype.length; i++) {
			for (int j = 0; j < str_months.length; j++) {
				for (int k = 0; k < str_initdata.length; k++) {
					if (str_prodtype[i].equals(str_initdata[k][0]) && str_months[j].equals(str_initdata[k][1])) {
						Object cellValue = hvs[k].getObjectValue(2); //
						if (cellValue instanceof Double) { //如果是Double类型
							ld_data[i][j] = new BillChartItemVO((Double) cellValue); //
						} else if (cellValue instanceof String) {
							ld_data[i][j] = new BillChartItemVO(Double.parseDouble((String) cellValue)); //
						} else if (cellValue instanceof Integer) {
							ld_data[i][j] = new BillChartItemVO(Double.valueOf("" + cellValue)); //
						} else if (cellValue instanceof BillChartItemVO) {
							ld_data[i][j] = (BillChartItemVO) cellValue; //
						} else {
							ld_data[i][j] = new BillChartItemVO(-77777);
						}
						break; //
					}
				}
			}
		}

		BillChartVO chartVO = new BillChartVO(); //
		chartVO.setXSerial(str_prodtype);
		chartVO.setYSerial(str_months);
		chartVO.setDataVO(ld_data);
		return chartVO; //
	}

	public String getOracleLongInSqlCondition(String[] itemvalues, String itemkey) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		String sessionid = new WLTInitContext().getCurrSession().getHttpsessionid(); //当前的用户Session...
		// 'id1','id2','id3'
		///拼接insert语句
		List<String> sqlList = new ArrayList<String>();
		if (itemvalues != null) {
			for (int i = 0; i < itemvalues.length; i++) {
				StringBuilder sbsql = new StringBuilder("insert into pub_sqlincons (sessionid,itemkey,incolumn) values (");
				sbsql.append("'");
				sbsql.append(sessionid);
				sbsql.append("','");
				sbsql.append(itemkey);
				sbsql.append("','");
				sbsql.append(itemvalues[i]);
				sbsql.append("')");
				sqlList.add(sbsql.toString());
			}
			System.out.println("共 [" + sqlList.size() + " ]");
			commDMO.executeBatchByDS(null, sqlList); //立即提交,否则会发生等待...
		}
		return "select incolumn from pub_sqlincons where sessionid='" + sessionid + "' and itemkey='" + itemkey + "'";
	}

	/**
	 * 
	 * @param _map
	 * @param itemkey
	 * @param _dbItemKey
	 * @return
	 * @throws Exception
	 */
	public String getOracleLongInSqlCondition(HashMap _map, String itemkey, String _dbItemKey) throws Exception {

		String str_queryCondition = (String) _map.get(itemkey); //
		if (str_queryCondition == null) {
			return " 2=2 "; //
		}
		TBUtil tbUtil = new TBUtil(); //
		String[] str_items = tbUtil.split(str_queryCondition, ";"); //
		CommDMO commDMO = new CommDMO(); //
		String sessionid = new WLTInitContext().getCurrSession().getHttpsessionid(); //当前的用户Session...
		// 'id1','id2','id3'
		///拼接insert语句
		List<String> sqlList = new ArrayList<String>();
		if (str_items != null) {
			for (int i = 0; i < str_items.length; i++) {
				StringBuilder sbsql = new StringBuilder("insert into pub_sqlincons (sessionid,itemkey,incolumn) values (");
				sbsql.append("'");
				sbsql.append(sessionid);
				sbsql.append("','");
				sbsql.append(itemkey);
				sbsql.append("','");
				sbsql.append(str_items[i]);
				sbsql.append("')");
				sqlList.add(sbsql.toString());
			}
			System.out.println("共 [" + sqlList.size() + " ]");
			commDMO.executeBatchByDS(null, sqlList); //立即提交,否则会发生等待...
		}
		return _dbItemKey + " in (select incolumn from pub_sqlincons where sessionid='" + sessionid + "' and itemkey='" + itemkey + "')";
	}

	/**
	 * 取得打印模板
	 */
	public PubPrintTempletVO getPubPrintTempletVO(String _templetCode) throws Exception {
		String str_sql_1 = "select * from pub_printtemplet where templetcode='" + _templetCode + "'";
		HashVO[] hvs_1 = new CommDMO().getHashVoArrayByDS(null, str_sql_1); //
		if (hvs_1 == null || hvs_1.length == 0) {
			throw new WLTAppException("没有找到打印模板[" + _templetCode + "]"); //
		}

		PubPrintTempletVO templetVO = new PubPrintTempletVO();
		templetVO.setId(hvs_1[0].getLognValue("id")); //
		templetVO.setTempletcode(hvs_1[0].getStringValue("templetcode")); //
		templetVO.setTempletname(hvs_1[0].getStringValue("templetname")); //

		String str_sql_2 = "select * from pub_printtemplet_itembands where parentid='" + hvs_1[0].getLognValue("id") + "'";
		HashVO[] hvs_2 = new CommDMO().getHashVoArrayByDS(null, str_sql_2); //
		PubPrintItemBandVO[] itemBandVOs = new PubPrintItemBandVO[hvs_2.length]; //
		for (int i = 0; i < itemBandVOs.length; i++) {
			itemBandVOs[i] = new PubPrintItemBandVO(); //
			itemBandVOs[i].setItemkey(hvs_2[i].getStringValue("itemkey")); //
			itemBandVOs[i].setItemname(hvs_2[i].getStringValue("itemname")); //
			itemBandVOs[i].setX(hvs_2[i].getDoubleValue("x").doubleValue()); //
			itemBandVOs[i].setY(hvs_2[i].getDoubleValue("y").doubleValue()); //
			itemBandVOs[i].setWidth(hvs_2[i].getIntegerValue("width").intValue()); //
			itemBandVOs[i].setHeight(hvs_2[i].getIntegerValue("height").intValue()); //

			itemBandVOs[i].setFonttype(hvs_2[i].getStringValue("fonttype") == null ? "System" : hvs_2[i].getStringValue("fonttype")); //
			itemBandVOs[i].setFontsize(hvs_2[i].getIntegerValue("fontsize") == null ? 10 : hvs_2[i].getIntegerValue("fontsize").intValue()); //
			itemBandVOs[i].setFontstyle(hvs_2[i].getIntegerValue("fontstyle") == null ? 0 : hvs_2[i].getIntegerValue("fontstyle").intValue()); //

			itemBandVOs[i].setHalign(hvs_2[i].getIntegerValue("halign") == null ? 1 : hvs_2[i].getIntegerValue("halign").intValue()); //左右位置
			itemBandVOs[i].setValign(hvs_2[i].getIntegerValue("valign") == null ? 1 : hvs_2[i].getIntegerValue("valign").intValue()); //上下位置
			itemBandVOs[i].setLayer(hvs_2[i].getIntegerValue("layer") == null ? 100 : hvs_2[i].getIntegerValue("layer").intValue()); //层次

			itemBandVOs[i].setForeground(hvs_2[i].getStringValue("foreground") == null ? "000000" : hvs_2[i].getStringValue("foreground")); //前景颜色
			itemBandVOs[i].setBackground(hvs_2[i].getStringValue("background") == null ? "FFFFFF" : hvs_2[i].getStringValue("background")); //背景颜色

			itemBandVOs[i].setShowBorder(hvs_2[i].getBooleanValue("isshowborder") == null ? false : hvs_2[i].getBooleanValue("isshowborder").booleanValue()); //
			itemBandVOs[i].setShowBaseline(hvs_2[i].getBooleanValue("isshowbaseline") == null ? false : hvs_2[i].getBooleanValue("isshowbaseline").booleanValue()); //是否显示底线
		}

		templetVO.setItemBandVOs(itemBandVOs); //

		return templetVO; //返回模板VO..
	}

	/**
	 * 导入模板
	 */
	public void importPrintTemplet(String _billtempletCode, String _templetCode) throws Exception {
		String str_sql = "select itemkey,itemname from pub_templet_1_item where pk_pub_templet_1 =(select pk_pub_templet_1 from pub_templet_1 where templetcode='" + _billtempletCode + "') and cardisshowable='Y' order by showorder"; //
		CommDMO commdmo = new CommDMO();
		HashVO[] hvs = commdmo.getHashVoArrayByDS(null, str_sql); //
		if (hvs == null || hvs.length == 0) {
			throw new WLTAppException("没有找到单据模板[" + _billtempletCode + "]"); //
		}
		String templetid = commdmo.getStringValueByDS(null, "select id from pub_printtemplet where templetcode='" + _templetCode + "'");
		Vector v_sqls = new Vector(); //
		v_sqls.add("delete from pub_printtemplet_itembands where parentid =" + templetid);

		for (int i = 0; i < hvs.length; i++) {
			String str_newid = new CommDMO().getSequenceNextValByDS(null, "s_pub_printtemplet_itembands"); //

			int li_row = i / 4; //
			int li_pos = i % 4; //

			StringBuffer sb_sql = new StringBuffer(); //
			sb_sql.append("insert into pub_printtemplet_itembands");
			sb_sql.append("(");
			sb_sql.append("id,");
			sb_sql.append("parentid,");
			sb_sql.append("itemkey,");
			sb_sql.append("itemname,");
			sb_sql.append("x,");
			sb_sql.append("y,");
			sb_sql.append("width,");
			sb_sql.append("height,");
			sb_sql.append("fonttype,");
			sb_sql.append("fontsize,");
			sb_sql.append("fontstyle,");
			sb_sql.append("halign,");
			sb_sql.append("valign,");
			sb_sql.append("layer,");
			sb_sql.append("isshowborder,");
			sb_sql.append("isshowbaseline");
			sb_sql.append(")");
			sb_sql.append(" values ");
			sb_sql.append("(");
			sb_sql.append(str_newid + ",");
			sb_sql.append(templetid + ",");
			sb_sql.append("'" + hvs[i].getStringValue("itemkey") + "',");
			sb_sql.append(hvs[i].getStringValue("itemname") == null ? "null," : ("'" + hvs[i].getStringValue("itemname") + "',"));
			sb_sql.append((li_pos * 200) + ",");
			sb_sql.append((li_row * 20) + ",");
			sb_sql.append("200,");
			sb_sql.append("20,");
			sb_sql.append("'System',");
			sb_sql.append("'10',");
			sb_sql.append("'0',");
			sb_sql.append("2,");
			sb_sql.append("1,");
			sb_sql.append("100,");
			sb_sql.append("'N',");
			sb_sql.append("'N'");
			sb_sql.append(")");
			v_sqls.add(sb_sql.toString()); //
		}

		new CommDMO().executeBatchByDS(null, v_sqls); //
	}

	/**
	 * 保存打印模板定义
	 */
	public void savePrintTempletItemBands(String _templetcode, PubPrintItemBandVO[] _itemBandVOs) throws Exception {
		String str_sql_1 = "select id from pub_printtemplet where templetcode='" + _templetcode + "'";
		HashVO[] hvs_1 = new CommDMO().getHashVoArrayByDS(null, str_sql_1); //
		if (hvs_1 != null && hvs_1.length > 0) {
			String str_parentid = hvs_1[0].getStringValue("id"); //
			Vector v_sqls = new Vector();
			v_sqls.add("delete  from pub_printtemplet_itembands where parentid='" + str_parentid + "'");

			for (int i = 0; i < _itemBandVOs.length; i++) {
				String str_newid = new CommDMO().getSequenceNextValByDS(null, "s_pub_printtemplet_itembands"); //

				StringBuffer sb_sql = new StringBuffer(); //
				sb_sql.append("insert into pub_printtemplet_itembands");
				sb_sql.append("(");
				sb_sql.append("id,");
				sb_sql.append("parentid,");
				sb_sql.append("itemkey,");
				sb_sql.append("itemname,");
				sb_sql.append("x,");
				sb_sql.append("y,");
				sb_sql.append("width,");
				sb_sql.append("height,");
				sb_sql.append("fonttype,");
				sb_sql.append("fontsize,");
				sb_sql.append("fontstyle,");
				sb_sql.append("halign,");
				sb_sql.append("valign,");
				sb_sql.append("layer,");
				sb_sql.append("foreground,"); //前景颜色
				sb_sql.append("background,"); //背景颜色
				sb_sql.append("isshowborder,");
				sb_sql.append("isshowbaseline");
				sb_sql.append(")");
				sb_sql.append(" values ");
				sb_sql.append("(");
				sb_sql.append(str_newid + ",");
				sb_sql.append(str_parentid + ",");
				sb_sql.append("'" + _itemBandVOs[i].getItemkey() + "',");
				sb_sql.append(_itemBandVOs[i].getItemname() == null ? "null," : "'" + _itemBandVOs[i].getItemname() + "',");
				sb_sql.append(_itemBandVOs[i].getX() + ",");
				sb_sql.append(_itemBandVOs[i].getY() + ",");
				sb_sql.append(_itemBandVOs[i].getWidth() + ",");
				sb_sql.append(_itemBandVOs[i].getHeight() + ",");
				sb_sql.append("'" + _itemBandVOs[i].getFonttype() + "',");
				sb_sql.append("'" + _itemBandVOs[i].getFontsize() + "',");
				sb_sql.append("'" + _itemBandVOs[i].getFontstyle() + "',");
				sb_sql.append("'" + _itemBandVOs[i].getHalign() + "',");
				sb_sql.append("'" + _itemBandVOs[i].getValign() + "',");
				sb_sql.append("'" + _itemBandVOs[i].getLayer() + "',");
				sb_sql.append("'" + _itemBandVOs[i].getForeground() + "',"); //前景颜色
				sb_sql.append("'" + _itemBandVOs[i].getBackground() + "',"); //背景颜色
				if (_itemBandVOs[i].isShowBorder()) {
					sb_sql.append("'Y',"); //
				} else {
					sb_sql.append("'N',"); //
				}

				if (_itemBandVOs[i].isShowBaseline()) {
					sb_sql.append("'Y'"); //
				} else {
					sb_sql.append("'N'"); //
				}

				sb_sql.append(")");
				v_sqls.add(sb_sql.toString()); //
			}
			new CommDMO().executeBatchByDS(null, v_sqls); //
		}
	}

	/**
	 * 根据模板主键删除打印模板
	 * @param _id
	 * @throws Exception
	 */
	public void deleteOnePrintTemplet(String _id) throws Exception {
		new CommDMO().executeBatchByDS(null, new String[] { "delete from pub_printtemplet where id=" + _id, "delete  from pub_printtemplet_itembands where parentid=" + _id });
	}
}
