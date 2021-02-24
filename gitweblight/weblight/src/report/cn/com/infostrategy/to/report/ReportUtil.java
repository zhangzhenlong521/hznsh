package cn.com.infostrategy.to.report;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.ReportDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.HashVOStruct;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.RefItemVO;

/**
 * 报表工具类.. 提供一些报表的工具方法,因为客户端,服务器端都会用到,所以放在这里.
 * 
 * @author xch
 * 
 */
public class ReportUtil implements Serializable {

	private static final long serialVersionUID = 1L;
	private TBUtil tbUtil = new TBUtil(); //

	/**
	 * 根据查询框中的条件拼出in条件!! 即参照返回的是以分号分割的字符串,然后拼成一个where条件.
	 * 
	 * @param _map
	 * @param _queryItemKey
	 * @param _dbItemKey
	 * @return
	 */
	public String getWhereConditionByInType(HashMap _map, String _queryItemKey, String _dbItemKey) {
		String str_queryCondition = (String) _map.get(_queryItemKey); //
		if (str_queryCondition == null) {
			return " 2=2 "; //
		}
		TBUtil tbUtil = new TBUtil(); //
		String[] str_items = tbUtil.split(str_queryCondition, ";"); //
		String str_inCondition = "";
		//		if (str_items.length < 700) {
		//			str_inCondition = tbUtil.getInCondition(str_items); //
		//		} else {
		str_inCondition = this.getInCondition(str_items, _dbItemKey); //
		return str_inCondition;
		//		}
		//		return _dbItemKey + " in (" + str_inCondition + ") "; //
	}

	/**
	 * 
	 * @return
	 */
	public String getInCondition(String[] _sqllist, String _dbItemKey) {
		if (_sqllist == null || _sqllist.length == 0) {
			return "'-99999'"; //
		} else {
			StringBuffer str_return = new StringBuffer();
			// 有一个值的时候
			if (_sqllist.length == 1) {
				if ("".endsWith(_sqllist[0].trim()))
					return str_return.append("2=2").toString();
				else
					return str_return.append(_dbItemKey + " in ( '" + _sqllist[0].trim() + "' )").toString();
			}
			// 多个时候
			str_return.append(_dbItemKey + " in (");
			for (int i = 0; i < _sqllist.length; i++) {
				if (_sqllist[i] != null && !((String) _sqllist[i]).trim().equals("")) {
					if (i == _sqllist.length - 1) {
						str_return.append("'" + (String) _sqllist[i] + "')");
						continue;
					}
					//					if ((i + 1) % 700 == 0)
					//						str_return.append("'" + (String) _sqllist[i] + "') or " + _dbItemKey + " in ( "); // 如果
					//					else
					str_return.append("'" + (String) _sqllist[i] + "',"); // 如果
				}
			}

			return str_return.toString();
		}
	}

	/**
	 * 将查询框中的中的查询项采用ReplaceAll的方式拼出,比如一个日期等.
	 * 在参照中的RefItemVO中有一个HashVO,里面有一个querycondition
	 * ,里面有约定好的itemkey,将其中的itemkey替换成真正的字段!
	 * 
	 * @param _map
	 * @param _queryItemKey
	 * @param _dbItemKey
	 * @return
	 */
	public String getWhereConditionByReplaceTYpe(HashMap _map, String _queryItemKey, String _dbItemKey) {
		RefItemVO refItemVO = (RefItemVO) _map.get("obj_" + _queryItemKey); // 从查询框中取得数据...
		if (refItemVO == null) { // 如果查询框中没定义对应的字段,该怎么办?是一个查不出还是查出所有?这里认为是查出所有!
			return " 2=2 "; //
		}

		HashVO hvo = refItemVO.getHashVO(); //
		if (hvo == null) {
			return " 2=2 "; //
		}

		String str_queryCondition_macro = hvo.getStringValue("querycondition"); //
		if (str_queryCondition_macro == null) {
			return " 2=2 "; //
		}

		String str_queryCondition = new TBUtil().replaceAll(str_queryCondition_macro, "{itemkey}", _dbItemKey); //
		return " " + str_queryCondition + " "; //
	}

	/**
	 * 行分裂,将一个列表中的某项值,如果有分号,则进行分裂!变成多行!
	 * @param _list
	 * @param _colName
	 * @return
	 */
	private ArrayList rowSplit(ArrayList _list, String _colName) {
		ArrayList al_newVOList = new ArrayList(); //新的列表,会越来越庞大！！
		for (int i = 0; i < _list.size(); i++) { //遍历所有结点!
			HashVO itemVO = (HashVO) _list.get(i); //
			String str_value = itemVO.getStringValue(_colName); //
			Set<String> set = new HashSet<String>();//袁江晓20120911添加  主要去除重复
			//解决如下的问题，譬如一条记录的 业务条线二层有三个，如零售业务;信用卡业务;公司信贷业务;  然后生成新的业务条线一层则为授信业务;授信业务;授信业务; 
			//在钻取的时候数字与钻取显示的条数不一致，去重之后只显示授信业务
			String[] str = (null != str_value && !str_value.equals("")) ? str_value.split(";") : null;
			for (int j = 0; j < str.length; j++) {
				set.add(str[j]);
			}
			String str_temp = "";
			for (String s : set) {
				str_temp += s + ";";
			}
			String[] str_items = tbUtil.split(str_temp, ";"); //看有几位!
			for (int j = 0; j < str_items.length; j++) { //遍历各值!
				HashVO cloneHVO = itemVO.clone(); //先克隆一把!每一个分隔符就克隆一下！
				cloneHVO.setAttributeValue(_colName, str_items[j]); //这条克隆的对象的值是分裂后的值!!!
				al_newVOList.add(cloneHVO); //加入
			}
		}
		return al_newVOList; //
	}

	/**
	 * @param _hvs
	 * @param _groupFunc 分组函数,比如【count(风险事件) 风险事件,sum(风险损失) 风险损失】
	 * @param _groupField,就是选择的维度!行头,列头上的维度! 比如【业务类型,发生机构,风险等级】           .
	 * @return 返回分组统计后的数组
	 */
	public HashVO[] groupHashVOs(HashVO[] _initHvs, String _groupFunc, String _groupFields) throws Exception {
		try {
			TBUtil tbUtil = new TBUtil(); //
			//System.out.println("_groupFunc=[" + _groupFunc + "],_groupFields[" + _groupFields + "]"); //输出：_groupFunc=[count(风险事件) 风险事件,sum(风险损失) 风险损失],_groupFields[业务类型,风险等级]
			// 先分析一下参与分组的列,比如:[业务类型,风险等级]
			String[] str_groupFields = tbUtil.split(_groupFields, ","); //
			for (int i = 0; i < str_groupFields.length; i++) {
				str_groupFields[i] = str_groupFields[i].trim(); //去掉空格
			}

			//后来存在分组的值中有分号的情况,所以存在一种行分裂的情况,即根据选择的维度中的值是否存在有个维度中的值有分号，如果有,则进行行分裂！如果没有,则还是原来的！
			//这就能智能的解决如果都是选择主表的维度,则自然不分裂,统计结果也是100条,如果选择的是维度有子表,则自然分裂,即是动态智能的!!
			//分裂的原理是先找出这条记录哪几个维度存有从个值,然后遍历这些维度,使用一个List反复的不断扩张！第一次先分裂第一个维度,第二次分裂第二个维度,每次都将原来的List的记录扩张本维度数组位的倍数！！
			ArrayList al_rowSplitVOs = new ArrayList(); //行分裂的
			if (_initHvs != null) {//这里需要判断一下，否则会报空指针异常【李春娟/2014-02-21】
				for (int i = 0; i < _initHvs.length; i++) {
					ArrayList al_needRowSplitGroups = new ArrayList(); //需要进行行分裂的组!
					for (int j = 0; j < str_groupFields.length; j++) { //遍历各个维度
						String str_groupItemValue = _initHvs[i].getStringValue(str_groupFields[j]); //
						if (str_groupItemValue != null) {
							if (tbUtil.split(str_groupItemValue, ";").length > 1) { //如果大于1则要进行分组
								al_needRowSplitGroups.add(str_groupFields[j]); //加入组名,即只对需要分裂的进行分裂!从而提高性能!	
							} else { //如果没有多个,则去掉分号!因为有时其实就一个，但前后都有个分号，比如【;国务院;】【;最高人民法院;】
								_initHvs[i].setAttributeValue(str_groupFields[j], tbUtil.replaceAll(str_groupItemValue, ";", "")); //
							}
						}
					}
					if (al_needRowSplitGroups.size() > 0) { //如果存在分组!
						ArrayList al_tmp = new ArrayList(); //临时的List,用来不断分裂,不断庞大!!
						al_tmp.add(_initHvs[i]); //先加入!
						for (int k = 0; k < al_needRowSplitGroups.size(); k++) { //遍历各个维度,一个个维度进行分裂!
							al_tmp = rowSplit(al_tmp, (String) al_needRowSplitGroups.get(k)); //将VO传进去,然后返回新的List,然后再拿新的List送进去!!反复循环,第一次将第一个维度分裂处理了,第二次将第二个维度处理了,第三次将第三个维度处理了!
							//System.out.println("第[" + (i + 1) + "]个维度分裂后的结果集大小是[" + al_tmp.size() + "]"); //
						}
						//以后如果存在第三张孙子表与子表之间有约束关系,则在分裂后,根据语法规则,剔除掉不存在的情况,比如【事项1<15>,事项2<16>】,将这种约束关系开发一个Map,然后再见遍历一次，凡是不存在这种Map中的就剔除掉！！！
						//时间关系暂时来不及处理，以后再扩展。。。。【xch/2012-08-11】

						al_rowSplitVOs.addAll(al_tmp); //加入到最后要返回的结果集中
						al_tmp.clear(); //清空临时List中的值!
					} else { //如果没有分组!则直接加入！
						al_rowSplitVOs.add(_initHvs[i]); //
					}
				}
			}

			HashVO[] detalVOs = (HashVO[]) al_rowSplitVOs.toArray(new HashVO[0]); //再强转成VO数组!!!这个数组是扩张以后的新数组!!即可能比原来位数多!
			//this.tbUtil.writeHashToHtmlTableFile(detalVOs, "C:/kkk125.htm"); //调试时输出看看

			//先分析一个函数语法,看有几个伪列,比如：count(风险事件) 风险事件,sum(风险损失) 风险损失
			String[] str_items = tbUtil.split(_groupFunc, ","); // 根据,分隔..
			String[] str_funnames1 = new String[str_items.length]; // 函数名
			String[] str_funpars1 = new String[str_items.length]; // 函数参数
			String[] str_boguscolsa = new String[str_items.length]; // 伪列名
			for (int i = 0; i < str_items.length; i++) {
				str_items[i] = str_items[i].trim(); // 先去掉首尾空格
				int li_pos_1 = str_items[i].indexOf("("); // 找左括号
				int li_pos_2 = str_items[i].indexOf(")"); // 找右括号
				str_funnames1[i] = str_items[i].substring(0, li_pos_1).trim(); // 函数名,比如：count,max,sum,avg
				str_funpars1[i] = str_items[i].substring(li_pos_1 + 1, li_pos_2).trim(); //函数参数,比如：风险事件,风险损失
				str_boguscolsa[i] = str_items[i].substring(li_pos_2 + 1, str_items[i].length()).trim(); //函数的伪列名称!
				//System.out.println("函数名[" + str_funnames1[i] + "],函数参数[" + str_funpars1[i] + "],伪列[" + str_boguscolsa[i] + "]"); //
			}

			LinkedHashSet hst_realNumField = new LinkedHashSet(); //因为完全有可能是同一个金额字段进行sum,avg,percentSum计算,为了提高效率,这里进行唯一性计算!
			for (int i = 0; i < str_funpars1.length; i++) {
				hst_realNumField.add(str_funpars1[i]); //
			}
			String[] str_realFieldNumfields = (String[]) hst_realNumField.toArray(new String[0]); //

			//真正的计算!!
			//以前的逻辑不好,新的逻辑改成,永远有Count计算,而且只算一次!而且同时将百分比算出来！如果存在基于金额字段的计算,则不管三七二十一，统统将sum,avg,percent,max,min都算出来！
			LinkedHashMap map_rowdata = new LinkedHashMap(); //这是真正存储分组合并后的数据的Map对象！！
			for (int i = 0; i < detalVOs.length; i++) { //遍历所有明细数据!
				StringBuffer sb_rowvalue = new StringBuffer(); //
				for (int j = 0; j < str_groupFields.length; j++) { //遍历所有组的实际值,比如：业务类型,风险等级的
					String str_itemvalue = detalVOs[i].getStringValue(str_groupFields[j]); //实际值
					//如果有分隔符串
					sb_rowvalue.append(str_itemvalue + "#"); //拼接！！！
				}
				String real_data = detalVOs[i].getStringValue(str_realFieldNumfields[0]); //袁江晓  实际值

				if (map_rowdata.containsKey(sb_rowvalue.toString())) { //如果已经有了..
					HashVO hvo_group = (HashVO) map_rowdata.get(sb_rowvalue.toString()); //   首先取出之前的值

					//遍列所有伪列,也就是计算列
					for (int j = 0; j < str_realFieldNumfields.length; j++) {
						//String str_boguscolname = str_boguscols[j]; //伪列名称
						if (detalVOs[i].getAttributeValue("groupextendfields") != null) { //如果指定了扩容,即如果是查询逻辑是一个主子表关联查询,则主表的一些字段会重复出现多次!即有冗余！这时必须只能算一次！即如果两个字段都是主表中的,则只算一次！
							ArrayList al_extendFields = (ArrayList) detalVOs[i].getAttributeValue("groupextendfields"); //这个关键字只在本类的方法leftOuterJoinNewHashVO()中才置入！！！
							boolean bo_extendcontains_groupfield = true; //判断分组字段是否在扩容字段中,默认认为是!
							for (int q = 0; q < str_groupFields.length; q++) { //
								if (!al_extendFields.contains(str_groupFields[q])) { // 只要有一个不在扩容字段中,则就不算扩容!!
									bo_extendcontains_groupfield = false; // 表示不扩容
									break; // 立即退出,提高效率,因为已经知道不扩容了..
								}
							}
							boolean bo_extendcontains_computefield = al_extendFields.contains(str_realFieldNumfields[j]); //扩容字段中是否包含计算字段
							if (bo_extendcontains_groupfield) {
								if (bo_extendcontains_computefield) {
									continue; // 两者都在扩容字段中,则只算一次
								} else {
									// 如果对主表分类统计子表,则必须要算,否则就是错了.
								}
							} else {
								if (bo_extendcontains_computefield) {
									// 对子表分类,统计主表,则存在争议,
									// 如果算,则总和加起来不等于主表的总和
									// 如果不算,则对子表中的部分记录不公平!因为不知道凭什么将主表的值算在子表的A记录中而不算在B记录中!!
									// 目前我们采用的是算!! 以后可以继续讨论这个问题!!!
								} else {
									// 两者都不在扩容字段中,则必须要算,否则就是错了
								}
							}
						}
						if (null != str_funnames1 && str_funnames1[0].trim().equals("init")) { //袁江晓添加  显示初始值  20130312
							String str_key_count = str_realFieldNumfields[j] + "◆init"; //
							String bdold_count = hvo_group.getStringValue(str_key_count); //原来的数据
							hvo_group.setAttributeValue(str_key_count, bdold_count + ";" + real_data); //旧值和信值拼接在一起
							//sum计算!   后来遇到原始值也要合计的功能于是加上此功能  袁江晓  20130319
							String str_key_sum = str_realFieldNumfields[j] + "◆sum"; //
							BigDecimal bd_thisItem = detalVOs[i].getBigDecimalValue(str_realFieldNumfields[j]); //当前值
							BigDecimal bdold_sum = hvo_group.getBigDecimalValue(str_key_sum); //原来的数据
							hvo_group.setAttributeValue(str_key_sum, bdold_sum.add(bd_thisItem == null ? new BigDecimal(0) : bd_thisItem)); //sum相加!!
						} else {
							BigDecimal bd_thisItem = detalVOs[i].getBigDecimalValue(str_realFieldNumfields[j]); //当前值

							//Count计算!
							String str_key_count = str_realFieldNumfields[j] + "◆count"; //
							BigDecimal bdold_count = hvo_group.getBigDecimalValue(str_key_count); //原来的数据
							hvo_group.setAttributeValue(str_key_count, bdold_count.add(new BigDecimal(1))); //count永远加1

							//sum计算!
							String str_key_sum = str_realFieldNumfields[j] + "◆sum"; //
							BigDecimal bdold_sum = hvo_group.getBigDecimalValue(str_key_sum); //原来的数据
							hvo_group.setAttributeValue(str_key_sum, bdold_sum.add(bd_thisItem == null ? new BigDecimal(0) : bd_thisItem)); //sum相加!!

							//max计算
							String str_key_max = str_realFieldNumfields[j] + "◆max"; //
							BigDecimal bdold_max = hvo_group.getBigDecimalValue(str_key_max); //原来的数据
							if (bdold_max == null) { //如果原来为空
								if (bd_thisItem != null) { //如果新值不为空!
									hvo_group.setAttributeValue(str_key_max, bd_thisItem);
								}
							} else { //如果原来不为空
								if (bd_thisItem != null && bd_thisItem.compareTo(bdold_max) > 0) { //如果新值
									hvo_group.setAttributeValue(str_key_max, bd_thisItem); //置换
								}
							}

							//min计算!
							String str_key_min = str_realFieldNumfields[j] + "◆min"; //
							BigDecimal bdold_min = hvo_group.getBigDecimalValue(str_key_min); //原来的数据
							if (bdold_min == null) {
								if (bd_thisItem != null) { //如果新值不为空!
									hvo_group.setAttributeValue(str_key_min, bd_thisItem);
								}
							} else { //如果旧值不为空,
								if (bd_thisItem != null && bd_thisItem.compareTo(bdold_min) < 0) { // 如果大前数据小于旧数据,则将当前数据置换..
									hvo_group.setAttributeValue(str_key_min, bd_thisItem); //置换
								}

							}
						}
					}

					//郝明加的将id自动装成一个【#value】
					String ids = (String) hvo_group.getAttributeValue("#value");
					if (detalVOs[i].getStringValue("id") != null) {
						if (ids == null) {
							hvo_group.setAttributeValue("#value", detalVOs[i].getStringValue("id"));
						} else {
							hvo_group.setAttributeValue("#value", ids + ";" + detalVOs[i].getStringValue("id"));
						}
					}
				} else { //如果是第一次插入！！
					//TBUtil.getTBUtil().writeHashToHtmlTableFile(detalVOs, "C:/datas.htm"); //输出文件看下处理后的样子到底是什么!!
					HashVO hvo_group = new HashVO(); // 创建结果
					for (int j = 0; j < str_groupFields.length; j++) { //
						String str_itemvalue = detalVOs[i].getStringValue(str_groupFields[j]); //
						hvo_group.setAttributeValue(str_groupFields[j], str_itemvalue); // 先将分组统计的列送入
					}

					//遍历所有伪列....
					for (int j = 0; j < str_realFieldNumfields.length; j++) {
						if (null != str_funnames1 && str_funnames1[0].trim().equals("init")) { //袁江晓添加  显示初始值  20130312
							String bd_thisItem = detalVOs[i].getStringValue(str_realFieldNumfields[j]);
							String str_key_count = str_realFieldNumfields[j] + "◆init"; //
							hvo_group.setAttributeValue(str_key_count, bd_thisItem); //
						} else {
							BigDecimal bd_thisItem = detalVOs[i].getBigDecimalValue(str_realFieldNumfields[j]); //当前值

							String str_key_count = str_realFieldNumfields[j] + "◆count"; //
							hvo_group.setAttributeValue(str_key_count, new BigDecimal(1)); //

							String str_key_sum = str_realFieldNumfields[j] + "◆sum"; //
							hvo_group.setAttributeValue(str_key_sum, bd_thisItem == null ? new BigDecimal(0) : bd_thisItem); //

							String str_key_max = str_realFieldNumfields[j] + "◆max"; ///
							hvo_group.setAttributeValue(str_key_max, bd_thisItem); //

							String str_key_min = str_realFieldNumfields[j] + "◆min"; ////
							hvo_group.setAttributeValue(str_key_min, bd_thisItem); //
						}

					}

					//在这里加上数据ID
					if (detalVOs[i].getStringValue("id") != null) {
						hvo_group.setAttributeValue("#value", detalVOs[i].getStringValue("id")); //如果有id字段,特殊处理!
					}

					map_rowdata.put(sb_rowvalue.toString(), hvo_group); //
					Object[] objs = map_rowdata.values().toArray(); //所有结果对象!!!
					/*for(int k=0;k<objs.length;k++){
						System.out.println("objs====================="+objs[k]);
					}
					
					*/
				}
			}

			Object[] objs = map_rowdata.values().toArray(); //所有结果对象!!!
			HashVO[] returnVOs = new HashVO[objs.length]; //
			for (int i = 0; i < returnVOs.length; i++) {
				returnVOs[i] = (HashVO) objs[i];
			}
			//以前这里有一大段处理所有计算列的逻辑,后来发现许多计算放在后台没法弄,因为必须知道选择的维度是什么!而且有时必须知道维度值本身的个数,所以只能在前台取!
			//所以都移到到前台!【xch/2012-08-24】
			return returnVOs; //
		} catch (java.lang.NumberFormatException ex) {
			throw new WLTAppException("参与计算列有一个不是数字的值,比如你想做求和汇总,但有一项值却是个英文字母!"); //
		} catch (Exception ex) {
			throw ex;
		}
	}

	/**
	 * 为hashVO[]后面附加一些内容，用于补完某个分组的维度，并限定顺序
	 * 
	 * @param _hvs
	 * @param _itemkey
	 * @param _types
	 * @return
	 */
	public HashVO[] superadd(HashVO[] _hvs, String _itemkey, String[] _types) {
		ArrayList<HashVO> hashVOList = new ArrayList<HashVO>();

		for (int i = 0; i < _hvs.length; i++) {
			hashVOList.add(_hvs[i]);
		}

		HashVO newHashVO = null;
		for (int i = 0; i < _types.length; i++) {
			newHashVO = new HashVO();
			newHashVO.setAttributeValue(_itemkey, _types[i]);
			hashVOList.add(newHashVO);
		}

		return hashVOList.toArray(new HashVO[hashVOList.size()]);

	}

	/**
	 * 将一个HashVO数组转换成图表VO 这个HashVO必须只有三列,前两列是两个维度,第三列是值.
	 * 
	 * @param _hvs
	 * @return
	 */
	public BillChartVO convertHashVOToChartVO(HashVO[] _hvs, HashMap sortMap, String[][] computeFunAndFields) {//袁江晓20121108添加第三个参数，表示图表按照何种方式来计算，之前的只支持按照count来计算
		if (_hvs == null || _hvs.length == 0) {
			return null;
		}
		String[] str_key = null;
		for (int i = 0; i < _hvs.length; i++) {
			str_key = _hvs[i].getKeys(); //
			if (_hvs[i].getStringValue(str_key[0]) == null) {
				_hvs[i].setAttributeValue(str_key[0], ""); //
			}

			if (_hvs[i].getStringValue(str_key[1]) == null) {
				_hvs[i].setAttributeValue(str_key[1], ""); //
			}
		}
		if (str_key.length == 2) { // 找出两个维度的唯一性值..
			String[] str_XSerial = null; //
			HashMap map_x = new HashMap();
			for (int i = 0; i < _hvs.length; i++) {
				map_x.put(_hvs[i].getStringValue(0), null); //

			}
			str_XSerial = (String[]) map_x.keySet().toArray(new String[0]);
			new TBUtil().sortStrs(str_XSerial); // 默认按字母顺序..
			BillChartItemVO[][] ld_data = new BillChartItemVO[str_XSerial.length][1]; //
			for (int i = 0; i < str_XSerial.length; i++) {
				for (int k = 0; k < _hvs.length; k++) { // 去数据中找位置
					if (str_XSerial[i].equals(_hvs[k].getStringValue(0))) { //
						Object cellValue = _hvs[k].getObjectValue(1); //
						if (cellValue instanceof Double) { // 如果是Double类型
							ld_data[i][0] = new BillChartItemVO((Double) cellValue); //
						} else if (cellValue instanceof String) {
							ld_data[i][0] = new BillChartItemVO(Double.parseDouble((String) cellValue)); //
						} else if (cellValue instanceof Integer) {
							ld_data[i][0] = new BillChartItemVO(Double.valueOf("" + cellValue)); //
						} else if (cellValue instanceof BillChartItemVO) {
							ld_data[i][0] = (BillChartItemVO) cellValue; //
						} else if (cellValue instanceof BigDecimal) {
							ld_data[i][0] = new BillChartItemVO(((BigDecimal) cellValue).doubleValue());
						} else {
							ld_data[i][0] = new BillChartItemVO(-77777);
						}
						ld_data[i][0].setCustProperty("#value", _hvs[k].getStringValue("#value")); //
						break;
					}
				}
			}

			BillChartVO chartVO = new BillChartVO(); //
			chartVO.setXSerial(str_XSerial); //
			chartVO.setYSerial(new String[] { " " }); //
			chartVO.setDataVO(ld_data);
			return chartVO; //
		} else if (str_key.length > 2) {
			// 首先将所有空值改成空字符串,否则下面容易出现空指针异常!

			// 找出两个维度的唯一性值..
			String[] str_XSerial = null; //
			String[] str_YSerial = null; //
			HashMap map_x = new HashMap();
			HashMap map_y = new HashMap();
			for (int i = 0; i < _hvs.length; i++) {
				map_x.put(_hvs[i].getStringValue(0), null); //
				map_y.put(_hvs[i].getStringValue(1), null); //
			}
			str_XSerial = (String[]) map_x.keySet().toArray(new String[0]);
			str_YSerial = (String[]) map_y.keySet().toArray(new String[0]);
			if (sortMap != null && sortMap.size() > 0) {
				String keys[] = _hvs[0].getKeys(); //key1 key2 是行列
				String[] sort1 = (String[]) sortMap.get(keys[0]);
				String[] sort2 = (String[]) sortMap.get(keys[1]);
				if (sort1 != null) {
					new TBUtil().sortStrsByOrders(str_XSerial, sort1); //
				} else {
					new TBUtil().sortStrs(str_XSerial); //
				}
				if (sort2 != null) {
					new TBUtil().sortStrsByOrders(str_YSerial, sort2); //	
				} else { //没有配置执行默认排序
					new TBUtil().sortStrs(str_YSerial);
				}
			} else {
				// 默认排序..
				new TBUtil().sortStrs(str_XSerial); //
				new TBUtil().sortStrs(str_YSerial); //	
			}
			BillChartItemVO[][] ld_data = new BillChartItemVO[str_XSerial.length][str_YSerial.length]; //
			for (int i = 0; i < str_XSerial.length; i++) {
				for (int j = 0; j < str_YSerial.length; j++) {
					for (int k = 0; k < _hvs.length; k++) { // 去数据中找位置
						if (str_XSerial[i].equals(_hvs[k].getStringValue(0)) && str_YSerial[j].equals(_hvs[k].getStringValue(1))) { //
							Object cellValue = null;
							if (null == computeFunAndFields) {
								cellValue = _hvs[k].getObjectValue(2); // 20121108袁江晓更改，这是之前的逻辑
							} else {
								cellValue = _hvs[k].getObjectValue(computeFunAndFields[0][2] + "◆" + computeFunAndFields[0][0].toLowerCase()); //袁江晓20121108添加第三个参数，表示图表按照何种方式来计算，之前的只支持按照count来计算
							}
							if (cellValue instanceof Double) { // 如果是Double类型
								ld_data[i][j] = new BillChartItemVO((Double) cellValue); //
							} else if (cellValue instanceof String) {
								ld_data[i][j] = new BillChartItemVO(Double.parseDouble((String) cellValue)); //
							} else if (cellValue instanceof Integer) {
								ld_data[i][j] = new BillChartItemVO(Double.valueOf("" + cellValue)); //
							} else if (cellValue instanceof BillChartItemVO) {
								ld_data[i][j] = (BillChartItemVO) cellValue; //
							} else if (cellValue instanceof BigDecimal) {
								ld_data[i][j] = new BillChartItemVO(((BigDecimal) cellValue).doubleValue());
							} else {
								ld_data[i][j] = new BillChartItemVO(-77777);
							}
							ld_data[i][j].setCustProperty("#value", _hvs[k].getStringValue("#value")); //
							break; // 找到就返回.
						}
					}
				}
			}

			BillChartVO chartVO = new BillChartVO(); //
			chartVO.setXSerial(str_XSerial); //
			chartVO.setYSerial(str_YSerial); //
			chartVO.setDataVO(ld_data);
			return chartVO; //
		}
		return null;
	}

	public BillChartVO convertHashVOToChartVO(HashVO[] _hvs) {
		return this.convertHashVOToChartVO(_hvs, null, null);//袁江晓20121108添加第三个参数，表示图表按照何种方式来计算，之前的只支持按照count来计算
	}

	public BillChartVO convertHashVOToChartVO(HashVO[] _hvs, String _XSerial, String _YSerial, String _needSelectCol, String[] _xSortItems, String[] _ySortItems) {
		return convertHashVOToChartVO(_hvs, _XSerial, _YSerial, _needSelectCol, _xSortItems, _ySortItems, false, false); //
	}

	/**
	 * 将一个HashVO数组转换成图表VO
	 * 
	 * @param _hvs
	 * @param _XSerial
	 *            ,表格横向的数据类型,比如季度
	 * @param _YSerial
	 *            ,表格纵向的数据类型,比如分行
	 * @param _needSelectCol
	 *            需要统计的列
	 * @param _isZeroReportType
	 *            是否零汇报机制,所谓零汇报机制就是排序字段中存在的都必须输出，不管是否在实际数据中存在过，即如果没有发生也要以零数量的方式输出
	 *            !!
	 * @return
	 */
	public BillChartVO convertHashVOToChartVO(HashVO[] _hvs, String _XSerial, String _YSerial, String _needSelectCol, String[] _xSortItems, String[] _ySortItems, boolean _isXZeroReportType, boolean _isYZeroReportType) {
		if (_hvs == null || _hvs.length == 0) {
			return null;
		}

		// 找出两个维度的唯一性值..
		HashMap map_x = new HashMap();
		HashMap map_y = new HashMap();// 按实际数据生成维度2
		for (int i = 0; i < _hvs.length; i++) {
			if (_hvs[i].getStringValue(_XSerial) == null) {
				_hvs[i].setAttributeValue(_XSerial, "空值."); //
			}
			if (_hvs[i].getStringValue(_YSerial) == null) {
				_hvs[i].setAttributeValue(_YSerial, "空值."); //
			}
			map_x.put(_hvs[i].getStringValue(_XSerial), ""); //
			map_y.put(_hvs[i].getStringValue(_YSerial), ""); //
		}

		String[] str_XSerial = (String[]) map_x.keySet().toArray(new String[0]); //
		String[] str_YSerial = (String[]) map_y.keySet().toArray(new String[0]); //

		// 默认排序..
		TBUtil tbUtil = new TBUtil();
		if (_xSortItems == null) { // 如果没有指定排序条件,则只对实际数据进行按字母排序
			tbUtil.sortStrs(str_XSerial); //
		} else {
			if (_isXZeroReportType) { // 如果是零汇报机制,则进行合并操作
				str_XSerial = spanArrayByZeroReport(str_XSerial, _xSortItems); //
			} else {
				tbUtil.sortStrsByOrders(str_XSerial, _xSortItems); //
			}
		}

		if (_ySortItems == null) {
			tbUtil.sortStrs(str_YSerial); //
		} else {
			if (_isYZeroReportType) { // 如果是零汇报机制,则进行合并操作
				str_YSerial = spanArrayByZeroReport(str_YSerial, _ySortItems); //
			} else {
				tbUtil.sortStrsByOrders(str_YSerial, _ySortItems); //
			}
		}

		BillChartItemVO[][] ld_data = new BillChartItemVO[str_XSerial.length][str_YSerial.length]; //
		for (int i = 0; i < str_XSerial.length; i++) {
			for (int j = 0; j < str_YSerial.length; j++) {
				for (int k = 0; k < _hvs.length; k++) { // 去数据中找位置
					if (str_XSerial[i].equals(_hvs[k].getStringValue(_XSerial)) && str_YSerial[j].equals(_hvs[k].getStringValue(_YSerial))) { //
						if (_needSelectCol != null && _hvs[k].getObjectValue(_needSelectCol) != null) {
							Object cellValue = _hvs[k].getObjectValue(_needSelectCol); //
							if (cellValue instanceof Double) { // 如果是Double类型
								ld_data[i][j] = new BillChartItemVO((Double) cellValue); //
							} else if (cellValue instanceof String) {
								ld_data[i][j] = new BillChartItemVO(Double.parseDouble((String) cellValue)); //
							} else if (cellValue instanceof Integer) {
								ld_data[i][j] = new BillChartItemVO(Double.valueOf("" + cellValue)); //
							} else if (cellValue instanceof BillChartItemVO) {
								ld_data[i][j] = (BillChartItemVO) cellValue; //
							} else if (cellValue instanceof BigDecimal) {
								ld_data[i][j] = new BillChartItemVO(((BigDecimal) cellValue).doubleValue());
							} else {
								ld_data[i][j] = new BillChartItemVO(-77777);
							}
						} else {
							ld_data[i][j] = new BillChartItemVO(0); // 如果没找到,当认为是0
						}
						break; // 找到就返回了,在空值的情况下,可能会有问题,因为必须两个唯度值相同的情况下只有一条记录才是对的!!....
					}
				}
			}
		}

		BillChartVO chartVO = new BillChartVO(); //
		chartVO.setXSerial(str_XSerial); //
		chartVO.setYSerial(str_YSerial); //
		chartVO.setDataVO(ld_data);
		return chartVO; //
	}

	private String[] spanArrayByZeroReport(String[] _realDatas, String[] _sortDatas) {
		ArrayList al_data = new ArrayList(); //
		for (int i = 0; i < _sortDatas.length; i++) {
			if (_sortDatas[i] != null) {
				al_data.add(_sortDatas[i]); //
			}
		}

		for (int i = 0; i < _realDatas.length; i++) {
			if (_realDatas[i] != null && !isItemExistInArray(_realDatas[i], _sortDatas)) { // 如果实际数据中的某一项，已在指定的排序组中存在过了,则跳过,反之则加上去,进行Span操作!!!
				al_data.add(_realDatas[i]); //
			}
		}
		return (String[]) al_data.toArray(new String[0]); //
	}

	private boolean isItemExistInArray(String _item, String[] _array) {
		for (int i = 0; i < _array.length; i++) {
			if (_array[i] != null && _array[i].equalsIgnoreCase(_item)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 在HashVO[]中移除一些列!
	 * 
	 * @param _hvs
	 * @param _itemkey
	 */
	public void removeHashVOItems(HashVO[] _hvs, String _itemkey) {
		new TBUtil().removeHashVOItems(_hvs, _itemkey); //
	}

	/**
	 * 在HashVO[]中移除一些列!
	 * 
	 * @param _hvs
	 * @param _itemkey
	 */
	public void removeHashVOItems(HashVO[] _hvs, String[] _itemkeys) {
		new TBUtil().removeHashVOItems(_hvs, _itemkeys); //
	}

	/**
	 * 替换HashVO[]中的某一列的值..
	 * 
	 * @param _hvs
	 * @param _fromItemKey
	 * @param _oldValue
	 *            旧值
	 * @param _newValue
	 *            新值
	 */
	public void replaceAllHashVOsItemValue(HashVO[] _hvs, String _fromItemKey, String _oldValue, String _newValue, String _others) {
		new TBUtil().replaceAllHashVOsItemValue(_hvs, _fromItemKey, _oldValue, _newValue, _others); //
	}

	/**
	 * 替换HashVO[]中的值..
	 * 
	 * @param _hvs
	 * @param _fromItemKey
	 * @param _replaceValue
	 *            ,是n行2列的字符串数组. 比如 new String[][]{{"Y","民报送"},{"N","未报送"}}
	 */
	public void replaceAllHashVOsItemValue(HashVO[] _hvs, String _fromItemKey, String[][] _replaceValue) {
		new TBUtil().replaceAllHashVOsItemValue(_hvs, _fromItemKey, _replaceValue); //
	}

	/**
	 * 外连接另一个SQL生成的HashVO[],然后将新表的列接在后面.. 非常有用,做报表时需要关联时就靠他了
	 * 
	 * @param _oldVOs
	 * @param _sql
	 * @param _oldfield
	 * @param _newfield
	 * @return
	 */
	public HashVO[] leftOuterJoinNewHashVO(HashVO[] _oldVOs, HashVOStruct _newHVO, String _oldfield, String _newfield) {
		ArrayList al_result = new ArrayList(); //
		String[] str_newVOKeys = _newHVO.getHeaderName(); // 得到所有key
		HashVO[] init_newVOs = _newHVO.getHashVOs(); //

		// 为了提高性能,要将目标HashVO过滤一下,即只需要能与源数据勾上的那些记录,勾不上的统统扔掉!!为了做到这一点,先将源数据中的Distinct一下,找出唯一性的值,这样性能又会更高些!
		HashMap map_olddistinct = new HashMap(); //
		for (int i = 0; i < _oldVOs.length; i++) {
			if (_oldVOs[i].getStringValue(_oldfield) != null && !_oldVOs[i].getStringValue(_oldfield).equals("")) {
				map_olddistinct.put(_oldVOs[i].getStringValue(_oldfield), null); //
			}
		}

		// 再遍历新值,找出对上的,对不上的统统扔掉!
		ArrayList al_filter = new ArrayList(); //
		for (int i = 0; i < init_newVOs.length; i++) {
			String str_newitemvalue = init_newVOs[i].getStringValue(_newfield);
			if (str_newitemvalue != null && map_olddistinct.containsKey(str_newitemvalue)) { // 如果旧数据的唯一性过滤中包含该项,则加入,
				al_filter.add(init_newVOs[i]); //
			}
		}
		HashVO[] filter_newVOs = (HashVO[]) al_filter.toArray(new HashVO[0]); //
		for (int i = 0; i < _oldVOs.length; i++) { // 从遍历旧的开始
			if (!_oldVOs[i].containsKey("groupextendfields")) {
				_oldVOs[i].setAttributeValue("groupextendfields", null); // 首先补上,否则生成html报表时出不出来
			}

			String[] str_oldallkeys = _oldVOs[i].getKeys(); // 旧数据的所有key
			String str_oldvalue = _oldVOs[i].getStringValue(_oldfield); //
			if (str_oldvalue == null) {
				str_oldvalue = "";
				_oldVOs[i].setAttributeValue(_oldfield, "");
			}

			boolean isLinkSuccess = false;
			int li_linkedchildcount = 0; //
			for (int j = 0; j < filter_newVOs.length; j++) { // 遍历新的VO过滤后的,这个结果经过上面的过滤已经很少了,比如单据表中的创建者只有两个人,而新表虽然是人员表有2万条记录,但经过过滤后这里只有两条了.
				String str_newvalue = filter_newVOs[j].getStringValue(_newfield); //
				if (str_newvalue == null) {
					str_newvalue = ""; //
					filter_newVOs[j].setAttributeValue(_newfield, "");
				}

				if (str_oldvalue.equals(str_newvalue)) { // 如果能勾上
					li_linkedchildcount++; // 加上品配数据,如果关联子表勾上多个,则说明是扩容的!!
					isLinkSuccess = true; // 表示已勾上...

					// 先克隆一个,必须要进行克隆操作,因为Java是内存引用的,否则会出错!!
					HashVO hvo_clone = _oldVOs[i].deepClone(); // 先克隆一个,即只要找到一个就将主表克隆一个,就样就会出现将主表记录扩容的情况!!

					// 首先判断是否是第2条开始的扩容的,如果是则在该记录上先记录下所有扩容的字段
					if (li_linkedchildcount > 1) { // 如果是第二个以上关联上的,则说明是扩容的!!则要在该记录上指定前面的所有项是扩容的!!这样后面进行count,sum时才不会出错!!这一步很重要!!尤其有时根据子表的类型汇总,而统计主表字段!
						ArrayList al_extendKeys = new ArrayList(); //
						al_extendKeys.addAll(Arrays.asList(str_oldallkeys)); // 将所有旧数据的key加入
						hvo_clone.setAttributeValue("groupextendfields", al_extendKeys); // 搞一个很怪的名称记录下所有扩容字段
					}

					for (int k = 0; k < str_newVOKeys.length; k++) {
						hvo_clone.setAttributeValue(str_newVOKeys[k], filter_newVOs[j].getStringValue(str_newVOKeys[k])); // 补上
					}
					al_result.add(hvo_clone); //
				}
			}

			if (!isLinkSuccess) { // 如果在子表中没品配上,即兜了一圈没发现记录,则循环在旧VO上补上空的新的各项,这样就能保证,返回的HashVO是一个完整的表格,而不会出现有的记录会少列!!
				HashVO hvo_clone = _oldVOs[i].deepClone(); // 先克隆一个,即只要找到一个就将主表克隆一个,就样就会出现将主表记录扩容的情况!!
				for (int k = 0; k < str_newVOKeys.length; k++) {
					hvo_clone.setAttributeValue(str_newVOKeys[k], ""); // 补上
				}
				al_result.add(hvo_clone); // 塞入克隆的值!
			}
		}

		HashVO[] hvs_return = (HashVO[]) al_result.toArray(new HashVO[0]); //
		return hvs_return; //
	}

	/**
	 * 对一个HashVO数组增加一个新列,新列是基于某个旧列进行日期处理,返回该日期是年,季,月
	 * 
	 * @param _hvs
	 * @param _itemkey
	 * @param _newitemkey
	 * @param _datetype
	 */
	public void leftOuterJoin_YSMDFromDateTime(HashVO[] _hvs, String _newitemkey, String _itemkey, String _datetype) {
		try {
			for (int i = 0; i < _hvs.length; i++) {
				String str_datetime = _hvs[i].getStringValue(_itemkey, ""); //
				if (null == str_datetime || "".equals(str_datetime) || "null".equals(str_datetime)) {//这里需要判断空串并且只是空值【李春娟/2012-03-13】 加入了判断不为null并且不等于null字符串  【袁江晓/2012-09-12】
					_hvs[i].setAttributeValue(_newitemkey, ""); //
				} else {

					String str_newdatetypeVaue = str_datetime; //
					if (_datetype.equals("年")) {
						str_newdatetypeVaue = str_datetime.substring(0, 4) + "年"; //
					} else if (_datetype.equals("季")) {
						str_newdatetypeVaue = str_datetime.substring(0, 4) + "年" + getSeason(str_datetime.substring(5, 7)) + "季度"; //
					} else if (_datetype.equals("月")) {
						str_newdatetypeVaue = str_datetime.substring(0, 4) + "年" + str_datetime.substring(5, 7) + "月"; //
					} else if (_datetype.equals("天")) {
						str_newdatetypeVaue = str_datetime.substring(0, 10);
					}
					_hvs[i].setAttributeValue(_newitemkey, str_newdatetypeVaue); //
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * 对一个HashVO加上一个新列,新列是在基于原来两个列上的两个日期的相差天数!!
	 * 
	 * @param _hvs
	 * @param _beginItemKey
	 * @param _endItemKey
	 * @param _newItemName
	 */
	public void leftOuterJoin_DaysBetweenTwoDate(HashVO[] _hvs, String _newItemName, String _beginItemKey, String _endItemKey) {
		try {
			for (int i = 0; i < _hvs.length; i++) {
				Date date_begin = _hvs[i].getDateValue(_beginItemKey); //
				Date date_end = _hvs[i].getDateValue(_endItemKey); //
				if (date_begin == null || date_end == null) {
					_hvs[i].setAttributeValue(_newItemName, ""); //
				} else {
					long ll_betweenDays = (date_end.getTime() - date_begin.getTime()) / (24 * 60 * 60 * 1000);
					_hvs[i].setAttributeValue(_newItemName, "" + ll_betweenDays); //
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * 根据一个数字,计算出各种范围,比如值可能是3,5,100,300,最后要将其解析成<10,10-100,200-500,>500的样子
	 * 
	 * @param _hvs
	 *            HashVO[]数组
	 * @param _newItemName
	 *            新增的列
	 * @param _fromItemName
	 *            从哪个列取数
	 * @param _numberArea
	 *            数据
	 */
	public void leftOuterJoin_NumberAreaFromDouble(HashVO[] _hvs, String _newItemName, String _fromItemName, String[] _numberArea) {
		for (int i = 0; i < _hvs.length; i++) {
			Double number = _hvs[i].getDoubleValue(_fromItemName); //
			if (number != null) {
				_hvs[i].setAttributeValue(_newItemName, parseNumber(number, _numberArea));
			} else {
				_hvs[i].setAttributeValue(_newItemName, "空值");
			}
		}
	}

	private String parseNumber(Double number, String[] area) {
		String condition = "";
		for (int i = 0; i < area.length; i++) {
			try {
				if (area[i] == null) {
					continue;
				}
				if (area[i].startsWith("<=")) {
					Double scope = Double.parseDouble(area[i].substring(area[i].indexOf("<=") + 1));
					if (number <= scope) {
						condition = area[i];
						break;
					}
				} else if (area[i].startsWith("<")) {
					Double scope = Double.parseDouble(area[i].substring(area[i].indexOf("<") + 1));
					if (number < scope) {
						condition = area[i];
						break;
					}
				} else if (area[i].startsWith(">=")) {
					Double scope = Double.parseDouble(area[i].substring(area[i].indexOf(">=") + 1));
					if (number >= scope) {
						condition = area[i];
						break;
					}
				} else if (area[i].startsWith(">")) {
					Double scope = Double.parseDouble(area[i].substring(area[i].indexOf(">") + 1));
					if (number > scope) {
						condition = area[i];
						break;
					}
				} else if (area[i].indexOf("-") > 0 && !area[i].startsWith("-") && !area[i].endsWith("-")) {
					String[] scopes = area[i].split("-");
					Double begin = Double.parseDouble(scopes[0]);
					Double end = Double.parseDouble(scopes[1]);
					if (number >= begin && number <= end) {
						condition = area[i];
						break;
					}
				}
			} catch (Exception e) {
				WLTLogger.getLogger(ReportUtil.class).error("解析字符串为数字时发生问题", e); //
				// e.printStackTrace(); //
				continue;
			}
		}
		return condition;
	}

	/**
	 * 对两个HashVO进行列列合并,
	 * 
	 * @param hvs
	 * @param hvs_sum_1
	 * @param string
	 * @param string2
	 * @param string3
	 * @param string4
	 */
	public void leftOuterJoin_ColumnSpan(HashVO[] _oldHashVO, String _newItemKey, HashVO[] _newHashVO, String _oldLinkField, String _newLinkField, String _fromItemKey) {
		HashMap map_newData = new HashMap(); //
		for (int i = 0; i < _newHashVO.length; i++) {
			map_newData.put(_newHashVO[i].getStringValue(_newLinkField), _newHashVO[i].getStringValue(_fromItemKey)); // 以关联字段的值作为索引,内容是想取的值
		}

		String str_oldLinkFieldValue = null; //
		String str_newGetFieldBalue = null; //
		for (int i = 0; i < _oldHashVO.length; i++) {
			str_oldLinkFieldValue = _oldHashVO[i].getStringValue(_oldLinkField); //
			if (str_oldLinkFieldValue == null) {
				_oldHashVO[i].setAttributeValue(_newItemKey, ""); //
			} else {
				str_newGetFieldBalue = (String) map_newData.get(str_oldLinkFieldValue); // 去新的表中取,看有没有,如果有,则将其加入,但以新的列名
				_oldHashVO[i].setAttributeValue(_newItemKey, str_newGetFieldBalue); //
			}
		}
	}

	/**
	 * 对一个HashVO重新排序与定义显示列
	 */
	public void reOrderAndShowColumns(HashVO[] _oldHashVO, String[] _reOrderColumns) {
		if (_oldHashVO == null) {
			return;
		}

		HashVO[] newHashVOs = new HashVO[_oldHashVO.length]; //
		for (int i = 0; i < newHashVOs.length; i++) { //
			newHashVOs[i] = new HashVO(); //
			for (int j = 0; j < _reOrderColumns.length; j++) {
				newHashVOs[i].setAttributeValue(_reOrderColumns[j], _oldHashVO[i].getObjectValue(_reOrderColumns[j])); //
			}
		}

		// 一定要重新克隆一遍,否则没效果!!!
		for (int i = 0; i < _oldHashVO.length; i++) { //
			_oldHashVO[i] = newHashVOs[i].deepClone(); //
		}
	}

	private String getSeason(String _month) {
		if (_month.equals("01") || _month.equals("02") || _month.equals("03")) {
			return "1";
		} else if (_month.equals("04") || _month.equals("05") || _month.equals("06")) {
			return "2";
		} else if (_month.equals("07") || _month.equals("08") || _month.equals("09")) {
			return "3";
		} else if (_month.equals("10") || _month.equals("11") || _month.equals("12")) {
			return "4";
		}
		return "";
	}

	/**
	 * 总是拿SQL中的第2列更新第1列品配上的
	 * 
	 * @param _hvs
	 * @param _newItemName
	 * @param _fromOldItemName
	 * @param _sql
	 * @throws Exception
	 */
	public void allOneFieldNameFromOtherTable(HashVO[] _hvs, String _newItemName, String _fromOldItemName, String _sql) throws Exception {
		HashMap custStateMap = getHashMapDataBysql(_sql); // ""
		for (int i = 0; i < _hvs.length; i++) {
			String str_custids = _hvs[i].getStringValue(_fromOldItemName); //
			if (str_custids == null || str_custids.trim().equals("")) {
				_hvs[i].setAttributeValue(_newItemName, "空值"); // findchannel
			} else {
				String str_convertname = ""; //
				String[] str_items = new TBUtil().split(str_custids, ";"); // 分隔一下!
				for (int j = 0; j < str_items.length; j++) {
					String str_itemName = (String) custStateMap.get(str_items[j]); //
					if (str_itemName != null) {
						str_convertname = str_convertname + str_itemName + ";"; //
					} else {
						System.err.println("表型关联时,字段【" + _fromOldItemName + "】中值【" + str_custids + "】的子项【" + str_items[j] + "】从SQL【" + _sql + "】中没关联到值！！！"); //
					}
				}
				String str_temp = "";
				if (str_convertname.lastIndexOf(";") >= 0) {//袁江晓修改  20120911    不进行判断如果为空会报数组越界
					str_temp = str_convertname.substring(0, str_convertname.lastIndexOf(";"));
				} else {
					str_temp = "";
				}
				_hvs[i].setAttributeValue(_newItemName, str_temp); // findchannel
			}
		}
	}

	/**
	 * 树型结构,取第几层的名称
	 * 
	 * @param _hvs
	 * @param _newItemName
	 * @param _fromOldItemName
	 * @param _sql
	 * @param _id
	 * @param _name
	 * @param _parentid
	 * @param _level
	 * @throws Exception
	 */
	public void allOneFieldNameFromOtherTree(HashVO[] _hvs, String _newItemName, String _fromOldItemName, String _sql, int _level) throws Exception {
		HashMap mapTemp = new ReportDMO().getTreeStruct(_sql); //
		for (int i = 0; i < _hvs.length; i++) {
			String str_custids = _hvs[i].getStringValue(_fromOldItemName); //
			if (str_custids == null || str_custids.trim().equals("")) {
				_hvs[i].setAttributeValue(_newItemName, "空值"); // findchannel
			} else {
				String str_convertname = ""; //
				String[] str_items = new TBUtil().split(str_custids, ";"); //
				for (int j = 0; j < str_items.length; j++) {
					String[] str_treePathNames = (String[]) mapTemp.get(str_items[j]); //
					if (str_treePathNames == null || str_treePathNames.length == 0) {
						System.err.println("树型关联时,字段【" + _fromOldItemName + "】中值【" + str_custids + "】的子项【" + str_items[j] + "】从SQL【" + _sql + "】中取第【" + _level + "】层时没关联到值！！！"); //
					} else {
						if (str_treePathNames.length > _level) {
							// if
							// (str_convertname.indexOf(str_treePathNames[_level]
							// + ";") < 0) { //
							str_convertname = str_convertname + str_treePathNames[_level] + ";"; //
							// }
						} else {
							System.err.println("树型关联时,字段【" + _fromOldItemName + "】中值【" + str_custids + "】的子项【" + str_items[j] + "】从SQL【" + _sql + "】中取第【" + _level + "】层时越界！！！"); //
						}
					}
				}
				//有的层级可能不统一，但为了不报错，这里判断一下【李春娟/2016-03-21】
				if (str_convertname.contains(";")) {
					_hvs[i].setAttributeValue(_newItemName, str_convertname.substring(0, str_convertname.lastIndexOf(";"))); // 
				} else {
					_hvs[i].setAttributeValue(_newItemName, str_convertname); // 
				}
			}
		}
	}

	private HashMap getHashMapDataBysql(String _sql) throws Exception {
		String[][] str_data = new CommDMO().getStringArrayByDS(null, _sql); //
		HashMap returnMap = new HashMap(); //
		for (int i = 0; i < str_data.length; i++) {
			returnMap.put(str_data[i][0], str_data[i][1]); //
		}
		return returnMap;
	}

	/**
	 * 创建内存Demo数据!我们在做报表时首先要创建内存数据!将报表的样子拉出来!
	 * @param _valueMap 可以是数组,也可是字符串,比如:
	 * map.put("业务类型",new String[]{"个人业务","公司业务","中间业务"});
	 * map.put("发生时间之季度","2012年季度;2012年2季度");
	 * @param _records 插入多少条记录,比如1000
	 * @return
	 */
	public HashVO[] createDemoMemoryData(HashMap _valueMap, int _records) {
		String[] str_keys = (String[]) _valueMap.keySet().toArray(new String[0]); //
		java.util.Random rans = new java.util.Random(); //随机数生成器!
		TBUtil tbUtil = new TBUtil(); //
		ArrayList hvoList = new ArrayList(); //
		for (int i = 0; i < _records; i++) { //遍历所有记录!
			HashVO hvoItem = new HashVO(); //创建这条VO
			hvoItem.setAttributeValue("id", i + 1); //
			for (int j = 0; j < str_keys.length; j++) {
				Object obj = _valueMap.get(str_keys[j]); //取得值,可能是数组,或者字符串!
				if (obj != null) { //如果不为空!
					String[] str_items = null; //
					if (obj.getClass().isArray()) { //如果本身是个数组!则强转
						str_items = (String[]) obj; //		
					} else { //如果不是数组,则进行字符串分隔
						str_items = tbUtil.split(((String) obj), ";"); //分割
					}
					String str_value = str_items[rans.nextInt(str_items.length)]; //随机值!
					hvoItem.setAttributeValue(str_keys[j], str_value); //
				} else {
					hvoItem.setAttributeValue(str_keys[j], "【空值】"); //
				}
			}
			hvoList.add(hvoItem); //
		}
		return (HashVO[]) hvoList.toArray(new HashVO[0]); //返回
	}

}
