/**************************************************************************
 * $RCSfile: FrameWorkMetaDataServiceImpl.java,v $  $Revision: 1.75 $  $Date: 2012/09/14 09:22:58 $
 **************************************************************************/
package cn.com.infostrategy.bs.mdata;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.infostrategy.bs.common.BSUtil;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.common.XMLIOUtil;
import cn.com.infostrategy.bs.sysapp.DataPolicyDMO;
import cn.com.infostrategy.bs.sysapp.install.database.DataBaseUtilDMO;
import cn.com.infostrategy.bs.sysapp.other.ImportExcelDMO;
import cn.com.infostrategy.bs.workflow.WorkFlowBSUtil;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.BillVOBuilder;
import cn.com.infostrategy.to.mdata.Pub_Templet_1ParVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;

/**
 * 元数据的主要方法,包括取元数据模板配置数据,取得BillCardPanel与BillListPanel中的数据等,注意多数据源的问题!!
 * @author user
 * 
 */
public class FrameWorkMetaDataServiceImpl implements FrameWorkMetaDataServiceIfc {

	private DataBaseUtilDMO dataDMO = null;

	public FrameWorkMetaDataServiceImpl() {
	}

	public void importOneTempletVO(String _tablename, String _templetCode, String _templetName) throws Exception {
		MetaDataDMO metaDMO = ServerEnvironment.getMetaDataDMO();
		metaDMO.importOneTempletVO(_tablename, _templetCode, _templetName); // 
	}

	public Pub_Templet_1VO getPub_Templet_1VO(String _code) throws Exception {
		MetaDataDMO metaDMO = ServerEnvironment.getMetaDataDMO();
		return metaDMO.getPub_Templet_1VO(_code); //
	}

	/**
	 * 根据模板名称从Xml文件中创建模板VO
	 */
	public DefaultTMO getDefaultTMOByCode(String _code, int _fromType) throws Exception {
		MetaDataDMO metaDMO = ServerEnvironment.getMetaDataDMO();
		return metaDMO.getDefaultTMOByCode(_code, _fromType);
	}

	/**
	 * 可以一下子取得多个,省去多次远程访问
	 */
	public Pub_Templet_1VO[] getPub_Templet_1VOs(String[] _codes) throws Exception {
		MetaDataDMO metaDMO = ServerEnvironment.getMetaDataDMO();
		Pub_Templet_1VO[] templetVOs = new Pub_Templet_1VO[_codes.length]; //
		for (int i = 0; i < _codes.length; i++) {
			templetVOs[i] = metaDMO.getPub_Templet_1VO(_codes[i]); //
		}
		return templetVOs; //
	}

	public Pub_Templet_1VO getPub_Templet_1VO(HashVO _parentVO, HashVO[] _childVOs, String _buildFromType, String _buildFromInfo) throws Exception {
		return new MetaDataDMO().getPub_Templet_1VO(_parentVO, _childVOs, _buildFromType, _buildFromInfo); ////
	}

	public Pub_Templet_1VO getPub_Templet_1VO(ServerTMODefine _serverTMO) throws Exception {
		return new MetaDataDMO().getPub_Templet_1VO(_serverTMO); //
	}

	//取得查询面板上的SQL自定义创建器的创建的SQL
	public String getBillQueryPanelSQLCustCreate(String _className, String _itemKey, String _itemValue, HashMap _allItemValues, HashMap _allItemSQLs, String _wholeSQL) throws Exception {
		MetaDataBSUtil metaUtil = new MetaDataBSUtil(); //
		return metaUtil.getBillQueryPanelSQLCustCreate(_className, _itemKey, _itemValue, _allItemValues, _allItemSQLs, _wholeSQL); //
	}

	public Object[] getBillCardDataByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		MetaDataDMO metaDMO = ServerEnvironment.getMetaDataDMO();
		return metaDMO.getBillCardDataByDS(_datasourcename, _sql, _templetVO);
	}

	//生成BillVO构造器,为了提高性能
	public BillVOBuilder getBillVOBuilder(String _datasourcename, String _sql, String _templetCode) throws Exception { //
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		return new MetaDataDMO().getBillVOBuilder(_datasourcename, _sql, _templetCode); //
	}

	//生成BillVO构造器,为了提高性能
	public BillVOBuilder getBillVOBuilder(String _datasourcename, String _sql, Pub_Templet_1VO _templetVO) throws Exception { //
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		return new MetaDataDMO().getBillVOBuilder(_datasourcename, _sql, _templetVO); //
	}

	public BillVO[] getBillVOsByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO) throws Exception {
		return getBillVOsByDS(_datasourcename, _sql, _templetVO, true); //
	}

	public BillVO[] getBillVOsByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO, boolean _isExecLoadFormula) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		return ServerEnvironment.getMetaDataDMO().getBillVOsByDS(_datasourcename, _sql, _templetVO, _isExecLoadFormula); // 取得BillVO数组!!!
	}

	public Object[][] getBillListDataByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO) throws Exception {
		return getBillListDataByDS(_datasourcename, _sql, _templetVO, true); //
	}

	public Object[][] getBillListDataByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO, boolean _isExecLoadFormula) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		MetaDataDMO metaDMO = ServerEnvironment.getMetaDataDMO();
		return metaDMO.getBillListDataByDS(_datasourcename, _sql, _templetVO, _isExecLoadFormula);
	}

	public Object[][] getBillListDataByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO, boolean _isExecLoadFormula, int[] _rowArea) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		return new MetaDataDMO().getBillListDataByDS(_datasourcename, _sql, _templetVO, _isExecLoadFormula, _rowArea); //
	}

	public Object[][] getBillListDataByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO, boolean _isExecLoadFormula, int[] _rowArea, boolean _isRegHVOinRowNumberVO) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		return new MetaDataDMO().getBillListDataByDS(_datasourcename, _sql, _templetVO, _isExecLoadFormula, _rowArea, _isRegHVOinRowNumberVO); //
	}

	//后来遇到不是通过SQL查询数据,而是直接通过HashVO[]查询数据!!
	public Object[][] getBillListDataByHashVOs(Pub_Templet_1ParVO _templetVO, HashVO[] _hashVOs) throws Exception { //
		return new MetaDataDMO().getBillListDataByHashVOs(_templetVO, _hashVOs); //
	}

	public TableDataStruct getBillTreeData(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		return new MetaDataDMO().getBillTreeData(_datasourcename, _sql, _templetVO); //
	}

	//重置树型结构的linkName
	public String resetTreeLinkName(String _datasourcename, String _tableName, String _pkId, String _nameField, String _parentId, String _linkName) throws Exception {
		long ll_1 = System.currentTimeMillis(); //
		CommDMO dmo = new CommDMO(); //
		HashVO[] hvs = dmo.getHashVoArrayAsTreeStructByDS(_datasourcename, "select " + _pkId + "," + _nameField + "," + _linkName + "," + _parentId + " from " + _tableName, _pkId, _parentId, null, null); //
		ArrayList al_sqls = new ArrayList(); //
		for (int i = 0; i < hvs.length; i++) { //
			String str_pkValue = hvs[i].getStringValue(_pkId); //
			String str_linkName = hvs[i].getStringValue("$parentpathnamelink1"); //
			String str_dbLinkName = hvs[i].getStringValue(_linkName); //
			str_linkName = (str_linkName == null ? "" : str_linkName).trim(); //
			str_dbLinkName = (str_dbLinkName == null ? "" : str_dbLinkName).trim(); //
			if (!str_linkName.equals(str_dbLinkName)) { //如果两者不等,则重置!!
				al_sqls.add("update pub_corp_dept set " + _linkName + "='" + str_linkName + "' where " + _pkId + "='" + str_pkValue + "'"); //
			}
		}

		if (al_sqls.size() > 0) {
			dmo.executeBatchByDS(_datasourcename, al_sqls); //
		}
		long ll_2 = System.currentTimeMillis(); //
		return "一共更新了[" + al_sqls.size() + "]条机构的LinkName,服务器端计算耗时[" + (ll_2 - ll_1) + "]"; //
	}

	/**
	 * 检查一个树型结构的某个字段是否满足某种规则!比如机构树中的机构类型字段,必须符合一定的规则!比如二级支行不能直接挂在一级分行下面!【xch/2012-04-24】
	 */
	public String[] checkTreeOneFieldRule(String _datasourcename, String _table, String _pkfield, String _nameField, String _parentPKfield, String _seqField, String _checkField, String[] _rules) throws Exception {
		CommDMO dmo = new CommDMO(); //
		HashVO[] hvs = dmo.getHashVoArrayAsTreeStructByDS(_datasourcename, "select " + _pkfield + "," + _nameField + "," + _parentPKfield + "," + _checkField + " from " + _table, _pkfield, _parentPKfield, _seqField, null); //
		HashMap tempMap = new HashMap(); //
		for (int i = 0; i < hvs.length; i++) { //先建索引!!
			tempMap.put(hvs[i].getStringValue(_pkfield), hvs[i]); //建索引,为了后面寻找时快速!
		}
		TBUtil tbUtil = new TBUtil(); //
		String str_pkValue = null; //
		String str_nameValue = null; //
		String str_parentIds = null;//
		String str_parentNames = null; //

		StringBuffer sb_unLawByNulls = new StringBuffer(); //
		StringBuffer sb_sql_null = new StringBuffer(); //
		StringBuffer sb_sql_unlaw = new StringBuffer(); //
		StringBuffer sb_sql_caiche = new StringBuffer(); //
		StringBuffer sb_sql_caiche2 = new StringBuffer(); //
		int li_count1 = 0, li_count2 = 0, li_count3 = 0; //
		for (int i = 0; i < hvs.length; i++) { //遍历!
			str_pkValue = hvs[i].getStringValue(_pkfield); //主键值!
			str_nameValue = hvs[i].getStringValue(_nameField); //主键值!
			str_parentIds = hvs[i].getStringValue("$parentpathids"); //该机构的所有父亲机构
			str_parentNames = hvs[i].getStringValue("$parentpathnamelink"); //机构全称

			String str_checkFieldValue = hvs[i].getStringValue(_checkField); //被检查字段的值!!

			if (str_checkFieldValue == null || str_checkFieldValue.trim().equals("")) { //如果类型字段为空或空串
				li_count1++; //
				sb_sql_null.append("update " + _table + " set " + _checkField + " ='' where " + _pkfield + "='" + str_pkValue + "' or '机构全名'='" + str_parentNames + "';\r\n");
			}

			if (str_parentIds != null && !str_parentIds.trim().equals("")) {
				String[] str_parentIdArray = tbUtil.split(str_parentIds, ";"); //分割
				String[] str_parentName = new String[str_parentIdArray.length]; //
				String[] str_parentType = new String[str_parentIdArray.length]; //
				StringBuilder sb_types = new StringBuilder(); //
				String str_myRealParentTypeValue = null; // 
				for (int j = 0; j < str_parentIdArray.length; j++) { //遍历我的所有父亲
					HashVO parentVO = (HashVO) tempMap.get(str_parentIdArray[j]); //
					str_parentName[j] = parentVO.getStringValue(_nameField); //实际名称
					str_parentType[j] = parentVO.getStringValue(_checkField); //
					sb_types.append("" + parentVO.getStringValue(_checkField)); //
					if (j != str_parentIdArray.length - 1) {
						sb_types.append("-"); //
					}
					if (j == str_parentIdArray.length - 2) {
						str_myRealParentTypeValue = parentVO.getStringValue(_checkField); //我的直接父亲的类型值!
					}
				}

				String str_myCorpType = sb_types.toString(); //
				boolean isMatch = false; //是否匹配上?
				for (int k = 0; k < _rules.length; k++) {
					if (_rules[k].startsWith(str_myCorpType)) { //在各个规则中,如果有一个规则是以我的路径开头的,则说明我是合法的!
						isMatch = true; //
						break; //
					}
				}
				if (!isMatch) { //如果没匹配上!
					if (str_myCorpType.indexOf("null") > 0) { //可能是因为空造成的!也就是说如果把第一个为空的改好了这些可能一下子也好了!!!
						li_count2++;
						sb_unLawByNulls.append(li_count2 + "[" + str_parentNames + "]  父路径值 [" + str_myCorpType + "]不对!\r\n"); //
					} else {
						li_count3++;
						sb_sql_unlaw.append("update " + _table + " set " + _checkField + " ='" + str_myRealParentTypeValue + "' where " + _pkfield + "='" + str_pkValue + "' or '" + str_parentNames + "'='" + str_myCorpType + "';\r\n"); //
					}
				}

				//智能计算!!!,如果检查的正好是机构类型,则有这个智能判断!
				if (_table.equalsIgnoreCase("pub_corp_dept") && _checkField.equalsIgnoreCase("corptype")) {
					if (str_parentName.length >= 3 && !str_parentName[1].equals("总行")) {
						if (str_parentName.length == 3) { //即一级支行/二级分行/一级分行部门!
							if (str_parentName[2].endsWith("分行")) {
								if (!"二级分行".equals(str_checkFieldValue)) { //如果不是二级分行
									//sb_sql_zzz.append("[" + str_parentNames + "]的类型怀疑应是[二级分行],原来是[" + str_checkFieldValue + "]\r\n"); //
									sb_sql_caiche.append("update " + _table + " set " + _checkField + " ='二级分行' where " + _pkfield + "='" + str_pkValue + "' or '" + str_parentNames + "'='" + str_myCorpType + "';\r\n"); //
								}
							} else if (str_parentName[2].endsWith("支行")) {
								if (!"一级支行".equals(str_checkFieldValue)) { //如果不是二级分行
									//sb_sql_zzz.append("[" + str_parentNames + "]的类型怀疑应是[一级支行],原来是[" + str_checkFieldValue + "]\r\n"); //
									sb_sql_caiche.append("update " + _table + " set " + _checkField + " ='一级支行' where " + _pkfield + "='" + str_pkValue + "' or '" + str_parentNames + "'='" + str_myCorpType + "';\r\n"); //
								}
							} else {
								if (!"一级分行部门".equals(str_checkFieldValue)) { //如果不是二级分行
									//sb_sql_zzz.append("[" + str_parentNames + "]的类型怀疑应是[一级分行部门],原来是[" + str_checkFieldValue + "]\r\n"); //
									sb_sql_caiche.append("update " + _table + " set " + _checkField + " ='一级分行部门' where " + _pkfield + "='" + str_pkValue + "' or '" + str_parentNames + "'='" + str_myCorpType + "';\r\n"); //
								}
							}
						} else if (str_parentName.length == 4) { //即一级支行/二级分行/一级分行部门!
							if (str_parentName[2].endsWith("分行")) {
								if (str_parentName[3].endsWith("支行")) { //分行下面是支行,则是级支行!
									if (!"二级支行".equals(str_checkFieldValue)) { //如果不是二级分行
										//sb_sql_zzz.append("[" + str_parentNames + "]的类型怀疑应是[二级支行],原来是[" + str_checkFieldValue + "]\r\n"); //
										sb_sql_caiche.append("update " + _table + " set " + _checkField + " ='二级支行' where " + _pkfield + "='" + str_pkValue + "' or '" + str_parentNames + "'='" + str_myCorpType + "';\r\n"); //
									}
								} else {
									if (!"二级分行部门".equals(str_checkFieldValue)) { //如果不是二级分行
										//sb_sql_zzz.append("[" + str_parentNames + "]的类型怀疑应是[二级分行部门],原来是[" + str_checkFieldValue + "]\r\n"); //
										sb_sql_caiche.append("update " + _table + " set " + _checkField + " ='二级分行部门' where " + _pkfield + "='" + str_pkValue + "' or '" + str_parentNames + "'='" + str_myCorpType + "';\r\n"); //
									}
								}
							} else if (str_parentName[2].endsWith("支行")) {
								if (!"一级支行部门".equals(str_checkFieldValue)) { //如果不是二级分行
									//sb_sql_zzz.append("[" + str_parentNames + "]的类型怀疑应是[一级支行部门],原来是[" + str_checkFieldValue + "]\r\n"); //
									sb_sql_caiche.append("update " + _table + " set " + _checkField + " ='一级支行部门' where " + _pkfield + "='" + str_pkValue + "' or '" + str_parentNames + "'='" + str_myCorpType + "';\r\n"); //
								}
							} else {
								if (!"一级分行部门".equals(str_checkFieldValue)) { //如果不是二级分行
									//sb_sql_zzz.append("[" + str_parentNames + "]的类型怀疑应是[一级分行部门],原来是[" + str_checkFieldValue + "]\r\n"); //
									sb_sql_caiche.append("update " + _table + " set " + _checkField + " ='一级分行部门' where " + _pkfield + "='" + str_pkValue + "' or '" + str_parentNames + "'='" + str_myCorpType + "';\r\n"); //
								}
							}
						} else if (str_parentName.length >= 5) {
							if (str_parentName[2].endsWith("分行")) {
								if (str_parentName[3].endsWith("支行")) { //分行下面是支行,则是级支行!
									if (!"二级支行部门".equals(str_checkFieldValue)) { //如果不是二级分行
										//sb_sql_zzz.append("[" + str_parentNames + "]的类型怀疑应是[二级支行部门],原来是[" + str_checkFieldValue + "]\r\n"); //
										sb_sql_caiche.append("update " + _table + " set " + _checkField + " ='二级支行部门' where " + _pkfield + "='" + str_pkValue + "' or '" + str_parentNames + "'='" + str_myCorpType + "';\r\n"); //
									}
								} else {
									if (!"二级分行部门".equals(str_checkFieldValue)) { //如果不是二级分行
										//sb_sql_zzz.append("[" + str_parentNames + "]的类型怀疑应是[二级分行部门],原来是[" + str_checkFieldValue + "]\r\n"); //
										sb_sql_caiche.append("update " + _table + " set " + _checkField + " ='二级分行部门' where " + _pkfield + "='" + str_pkValue + "' or '" + str_parentNames + "'='" + str_myCorpType + "';\r\n"); //
									}
								}
							} else if (str_parentName[2].endsWith("支行")) {
								if (!"一级支行部门".equals(str_checkFieldValue)) { //如果不是二级分行
									//sb_sql_zzz.append("[" + str_parentNames + "]的类型怀疑应是[一级支行部门],原来是[" + str_checkFieldValue + "]\r\n"); //
									sb_sql_caiche.append("update " + _table + " set " + _checkField + " ='一级支行部门' where " + _pkfield + "='" + str_pkValue + "' or '" + str_parentNames + "'='" + str_myCorpType + "';\r\n"); //
								}
							} else {
								if (!"一级分行部门".equals(str_checkFieldValue)) { //如果不是二级分行
									//sb_sql_zzz.append("[" + str_parentNames + "]的类型怀疑应是[一级分行部门],原来是[" + str_checkFieldValue + "]\r\n"); //
									sb_sql_caiche.append("update " + _table + " set " + _checkField + " ='一级分行部门' where " + _pkfield + "='" + str_pkValue + "' or '" + str_parentNames + "'='" + str_myCorpType + "';\r\n"); //
								}
							}
						}
					}

					//根据名字猜测
					if (str_nameValue != null && str_nameValue.endsWith("行") && str_checkFieldValue != null && !str_checkFieldValue.endsWith("行")) {
						sb_sql_caiche2.append("A[" + str_parentNames + "]机构的类型可能应该是[***行],现在实际值是[" + str_myCorpType + "]\r\n"); //
					} else if (str_nameValue != null && str_nameValue.endsWith("部") && str_checkFieldValue != null && str_checkFieldValue.endsWith("行")) {
						sb_sql_caiche2.append("B[" + str_parentNames + "]机构的类型可能应该是[***部],现在实际值是[" + str_myCorpType + "]\r\n"); //
					}
				}
			}
		}
		return new String[] { "总共" + li_count1 + "条:\r\n" + sb_sql_null.toString(), "总共" + li_count2 + "条:\r\n" + sb_unLawByNulls.toString(), "总共" + li_count3 + "条:\r\n" + sb_sql_unlaw.toString(), sb_sql_caiche.toString() + "\r\n\r\n" + sb_sql_caiche2.toString() }; //
	}

	public Object[][] getQueryDataByDS(String _datasourcename, String _sql, Pub_Templet_1VO _templetVO) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		MetaDataDMO metaDMO = ServerEnvironment.getMetaDataDMO();
		return metaDMO.getQueryDataByDS(_datasourcename, _sql, _templetVO); //
	}

	/**
	 * 提交一批BillVO,非常关键!!!
	 */
	public void commitBillVOByDS(String _datasourcename, BillVO[] _deleteVOs, BillVO[] _insertVOs, BillVO[] _updateVOs) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		new MetaDataDMO().commitBillVO(_datasourcename, _deleteVOs, _insertVOs, _updateVOs); //
	}

	//刘旋飞机要的直接送入一个机构,返回其所在分行!通过策略计算,不是写公式了!
	public HashMap getDataPolicyTargetCorpsByCorpId(String _deptId, String _datapolicy, String _returnCol) throws Exception {
		return new DataPolicyDMO().getDataPolicyTargetCorpsByCorpId(_deptId, _datapolicy, _returnCol); //
	}

	//刘旋飞机要的直接送入一个人员,返回其所在分行!通过策略计算,不是写公式了!
	public HashMap getDataPolicyTargetCorpsByUserId(String _loginUserid, String _datapolicy, String _returnCol) throws Exception { //
		return new DataPolicyDMO().getDataPolicyTargetCorpsByUserId(_loginUserid, _datapolicy, _returnCol); //
	}

	//根据资源策略中的配置,动态计算出一个人员有权限控制的所有“机构清单”或“人员清单”
	public String[] getDataPolicyCondition(String _loginUserid, String _datapolicy, int _type, String _corpFieldName, String _userFieldName) throws Exception {
		return new DataPolicyDMO().getDataPolicyCondition(_loginUserid, _datapolicy, _type, _corpFieldName, _userFieldName); //
	}

	//得到资源约束条件!
	public String[] getDataPolicyCondition(String _loginUserid, String _datapolicy, String _datapolicyMap) throws Exception {
		return new DataPolicyDMO().getDataPolicyCondition(_loginUserid, _datapolicy, _datapolicyMap); //
	}

	/**
	 * 取得BillCellVO
	 * 
	 * @param _templetCode
	 * @param _billNo
	 * @return
	 * @throws Exception
	 */
	public BillCellVO getBillCellVO(String _templetCode, String _billNo, String _descr) throws Exception {
		return new MetaDataDMO().getBillCellVO(_templetCode, _billNo, _descr); //
	}

	/**
	 * 保存BillCellPanel上的数据!!
	 */
	public void saveBillCellVO(String _datasourcename, BillCellVO _cellVO) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		new MetaDataDMO().saveBillCellVO(_datasourcename, _cellVO); //

	}

	public void copyBillCellData(String _templetcode, String _billNO, String _descr) throws Exception {
		new MetaDataDMO().copyBillCellData(_templetcode, _billNO, _descr); //
	}

	public void createTenDemoRecords(String _datasourcename, String _tablename) throws Exception {
		new MetaDataDMO().createTenDemoRecords(_datasourcename, _tablename); //
	}

	// 通过模板修改表
	public void compareTemplateAndTable(String _datasourcename, String[] _tablename, String[] _template) throws Exception {
		new MetaDataDMO().compareTemplateAndTable(_datasourcename, _tablename, _template); //

	}

	// 往BillCellPanel里保存数据的方法
	public void getCellSaveDate(String templethid, String _billno, HashMap hashmap) throws Exception {
		new MetaDataDMO().getCellSaveDate(templethid, _billno, hashmap);

	}

	// 往BillCellPanel里读取数据的方法
	public HashVO[] getCellLoadDate(String templethid, String _billno) throws Exception {
		return new MetaDataDMO().getCellLoadDate(templethid, _billno);

	}

	public String getXMlFromTableRecords(String _sourcedsname, String[] _sqls) throws Exception {
		return new MetaDataDMO().getXMlFromTableRecords(_sourcedsname, _sqls); //
	}

	public String getXMlFromTableRecords(String _sourcedsname, String[] _sqls, String[][] _pkConstraint, String[][] _foreignPKConstraint, String _xmlFile) throws Exception {
		return new MetaDataDMO().getXMlFromTableRecords(_sourcedsname, _sqls, _pkConstraint, _foreignPKConstraint, _xmlFile); //
	}

	public List<Object> getAllTemplate(String templetcode, String tablename, String type) throws Exception {
		return new MetaDataDMO().getAllTemplate(templetcode, tablename, type);
	}

	public HashMap getXMlFromTable1000Records(String _dsName, String _tableName, String _pkName, int _beginNo) throws Exception {
		return new MetaDataDMO().getXMlFromTable1000Records(_dsName, _tableName, _pkName, _beginNo); //
	}

	public String getXMLDataBySQLAsResetId(String _dsName, String _sql, String _tabName, String _resetIdField) throws Exception {
		return new MetaDataDMO().getXMLDataBySQLAsResetId(_dsName, _sql, _tabName, _resetIdField); //
	}

	public String importRecordsXMLToTable(String[] _xmlFileNams, String _dsName, boolean _isReCreateId, HashMap param) throws Exception {
		return new MetaDataDMO().importRecordsXMLToTable(_xmlFileNams, _dsName, _isReCreateId, param); //
	}

	public void importXmlToTable1000Records(String _dsName, String _fileName, String _xml) throws Exception {
		new MetaDataDMO().importXmlToTable1000Records(_dsName, _fileName, _xml); //
	}

	public String getExportBillTempletSQL(String _code, String _dbtype, boolean _iswrap) throws Exception {
		return new MetaDataDMO().getExportBillTempletSQL(_code, _dbtype, _iswrap);
	}

	/**
	 * 导出一批表的结构与数据SQL(增量)
	 */
	public String getTableStructDataSQL(String[] _tabnames, String _dbtype, int _exportarea, String _sqlviewtype) throws Exception {
		StringBuffer sb_return = new StringBuffer(); //
		MetaDataDMO metadmo = new MetaDataDMO();
		for (int i = 0; i < _tabnames.length; i++) {
			if (_exportarea == 1 || _exportarea == 3) {
				sb_return.append(metadmo.getCreateSQL(_tabnames[i], _dbtype, _sqlviewtype)); //
			}
			if (_exportarea == 2 || _exportarea == 3) {
				sb_return.append(metadmo.getInserTableSQL(_tabnames[i], _dbtype, _sqlviewtype)); //
			}
		}
		return sb_return.toString();
	}

	/**
	 * 导出一批表的结构与数据SQL(全部)
	 */
	public String getTableStructDataSQLAll(String[] _tabnames, String _dbtype, int _exportarea, String _sqlviewtype) throws Exception {
		StringBuffer sb_return = new StringBuffer(); //
		MetaDataDMO metadmo = new MetaDataDMO();
		for (int i = 0; i < _tabnames.length; i++) {
			if (_exportarea == 1 || _exportarea == 3) {
				sb_return.append(metadmo.getCreateSQL(_tabnames[i], _dbtype, _sqlviewtype)); //
			}

			if (_exportarea == 2 || _exportarea == 3) {
				sb_return.append(metadmo.getInserTableSQLAll(_tabnames[i], _dbtype, _sqlviewtype)); //
			}
		}
		return sb_return.toString();
	}

	public String getRegFormatPanelSQL(String[] _regformatcode, String _dbtype, String _sqlviewtype) throws Exception {
		MetaDataDMO metadmo = new MetaDataDMO();
		StringBuffer sb_return = new StringBuffer(); //
		for (int i = 0; i < _regformatcode.length; i++) {
			sb_return.append(metadmo.getOneRegFormatPanelSQL(_regformatcode[i], _dbtype, _sqlviewtype)); //
		}

		return sb_return.toString(); //
	}

	/**
	 * 取得
	 * 
	 * @param _tabname
	 * @param _dbtype
	 * @param _sqlviewtype
	 * @return
	 */
	public String getCreateSQL(String _tabname, String _dbtype, String _sqlviewtype) throws Exception {
		return new MetaDataDMO().getCreateSQL(_tabname, _dbtype, _sqlviewtype); //
	}

	public String getInserTableSQL(String _tabname, String _dbtype, String _sqlviewtype) throws Exception {
		return new MetaDataDMO().getInserTableSQL(_tabname, _dbtype, _sqlviewtype); //
	}

	/**
	 * 转移数据
	 */
	public int transferDB(String _sourcedsname, String _destdsname, String _table, boolean _iscreate, boolean _isinsert) throws Exception {
		return new MetaDataDMO().transferDB(_sourcedsname, _destdsname, _table, _iscreate, _isinsert); //
	}

	public String safeMoveData(String _sourcedsname, String _destdsname, String tablename, HashMap tableMap, String type, HashMap conditon) throws Exception {
		return new MetaDataDMO().safeMoveData(_sourcedsname, _destdsname, tablename, tableMap, type, conditon); //
	}

	/**
	 * 根据表名得到碰撞的字符串，返回一个字符串
	 * 
	 * @param _tablename
	 *            表名
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getCollidePKByTableName(String[] _tablename, String _databasetype) throws Exception {
		return new DataBaseValidate().getCollidePKByTableName(_tablename, _databasetype);
	}

	/**
	 * 得到当前系统表所有和关键字碰撞的字段
	 * 
	 * @param _databasetype
	 * @return
	 * @throws Exception
	 */
	public String getAllCollidePK(String _databasetype) throws Exception {
		return new DataBaseValidate().getAllCollidePK(_databasetype);
	}

	/**
	 * 转移视图
	 */
	public void transferDBView(String _sourcedsname, String _destdsname, String _viewname) throws Exception {
		new MetaDataDMO().transferDBView(_sourcedsname, _destdsname, _viewname); //
	}

	/**
	 * 导入XML格式模版
	 */
	public void importXMLTemplet(String str_dataSource, String textarea) throws Exception {
		new XMLIOUtil().importXMLTemplet(str_dataSource, textarea);
	}

	public String exportXMLTemplet(String[] str_templete_code, int[] selectrows) throws Exception {
		return exportXMLTemplet(str_templete_code, selectrows, null);
	}

	/**
	 * 导出某个模板成xml,以前的机制都是硬写代码,所以不够通用!!! 关键是随着模板表结构的变化,这里还需要修改代码! 扩展性与灵活性不够!!
	 * 最好的办法是要能做到以下几点:
	 * 1.模板表结构变化了,这里不需代码!!
	 * 2.导出的XML与导入的实际表结构之间发生差异时,永远不报错,即导入时先计算下目标库中有几个字段! 然后交叉匹配!!
	 * 3.主键能自动处理,这是最重要也是最难的! 单表还是很好处理的(比如指定一个序列名),多表关联时,需要一个很好的机制! 表明之间的关联,主要就是子表的外键如何表达?
	 * 4.既支持纯导入,也支持动态导入
	 * 5.支持反复导入(可以先不考虑) 
	 * @param str_templete_code
	 * @param selectrows
	 * @param str_sourceDS
	 * @return
	 * @throws Exception
	 */
	public String exportXMLTemplet(String[] str_templete_code, int[] selectrows, String str_sourceDS) throws Exception {
		return new XMLIOUtil().exportXMLTemplet(str_templete_code, selectrows, str_sourceDS);
	}

	/**
	 * 导出XML格式注册按钮
	 */
	public String exportXMLRegButton(String[] str_refbutton_code, int[] selectrows) throws Exception {
		return new XMLIOUtil().exportXMLRegButton(str_refbutton_code, selectrows);
	}

	/**
	 * 导出XML格式注册参照
	 */
	public String exportXMLRegRef(String[] str_refref_code, int[] selectrows) throws Exception {
		return new XMLIOUtil().exportXMLRegRef(str_refref_code, selectrows);
	}

	/**
	 * 导出XML格式流程
	 */
	public String exportXMLProcess(String[] str_process_code, int[] selectrows) throws Exception {
		return new XMLIOUtil().exportXMLProcess(str_process_code, selectrows);
	}

	/**
	 * 导出XML格式下拉框表
	 */
	public String exportXMLCombobox(String[] str_combobox_code, int[] selectrows) throws Exception {
		return new XMLIOUtil().exportXMLCombobox(str_combobox_code, selectrows);
	}

	/**
	 * 导入XML格式注册按钮
	 */
	public void importXMLRegButton(String textarea) throws Exception {
		new XMLIOUtil().importXMLRegButton(textarea);
	}

	/**
	 * 导入XML格式注册样板
	 */
	public void importXMLRegFormat(String textarea) throws Exception {
		new XMLIOUtil().importXMLRegFormat(textarea);
	}

	/**
	 * 导入XML格式注册参照
	 */
	public void importXMLRegRef(String textarea) throws Exception {
		new XMLIOUtil().importXMLRegRef(textarea);
	}

	/**
	 * 导入XML格式流程
	 */
	public void importXMLProcess(String textarea) throws Exception {
		new XMLIOUtil().importXMLProcess(textarea);
	}

	/**
	 * 导入迪博xml格式的流程
	 */
	public String importDBXMLProcess(String wf_code, String wf_name, String str_xml, String userdef01, String wf_securitylevel) throws Exception {
		return new XMLIOUtil().importDBXMLProcess(wf_code, wf_name, str_xml, userdef01, wf_securitylevel);
	}

	/**
	 * 导入XML格式流程,不删除原有，其实就是复制功能
	 */
	public void importXMLProcess_Copy(String textarea) throws Exception {
		new XMLIOUtil().importXMLProcess_Copy(textarea);
	}

	/**
	 * 复制流程图
	 */
	public String copyProcessById(String _processid, String _processcode, String _processname) throws Exception {
		return new WorkFlowBSUtil().copyProcessById(_processid, _processcode, _processname);
	}

	/**
	 * 导出XML格式的Excel
	 * @param templetcode
	 * @return
	 */
	public String exportXMLForExcel(String templetcode) {
		return new XMLIOUtil().exportXMLForExcel(templetcode);
	}

	/**
	 * 通用导入XML程序 武坤萌 2008-11-21
	 */
	public void importXML(String text) throws Exception {
		new XMLIOUtil().importXML(text);
	}

	/**
	 * 导入XML格式下拉框
	 */
	public void importXMLCombobox(String textarea) throws Exception {
		new XMLIOUtil().importXMLCombobox(textarea);
	}

	/**
	 * 导出XML格式注册样板
	 */
	public String exportXMLRegFormat(String[] ids) throws Exception {
		return new XMLIOUtil().exportXMLRegFormat(ids);
	}

	/**
	 * 保留修改痕迹.
	 */
	public void saveKeepTrace(String _tablename, String _pkname, String _pkvalue, HashMap _fieldvalues, String _tracer) throws Exception {
		new CommDMO().saveKeepTrace(_tablename, _pkname, _pkvalue, _fieldvalues, _tracer);
	}

	/**
	 * 批量下载
	 */
	public String getZipFileName(String[] filenames) throws Exception {
		return new BSUtil().getZipFileName(filenames);
	}

	/**
	 * 根据文件名从服务器删除office控件上传的文件
	 * @param filename 文件名 文件名可能为相对路径
	 * @return
	 * @throws Exception
	 */
	public boolean deleteOfficeFileName(String _filename) throws Exception {
		String str_fileName = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/officecompfile/" + _filename; //
		File file = new File(str_fileName); //
		if (file.exists()) {
			boolean del_result = file.delete(); //删除文件
			if (del_result) {
				System.out.println("从服务器删除文件[" + str_fileName + "]成功！");
			} else {
				System.err.println("从服务器删除文件[" + str_fileName + "]失败,可能不存该文件,或该文件被锁定！");
			}
			return del_result; //
		} else {
			System.out.println("从服务器删除文件[" + str_fileName + "]时发现不存在该文件！");
			return false;
		}
	}

	/**
	 * 根据文件名删除附件上传的文件
	 * @param filename 文件名 文件名可能为相对路径
	 * @return
	 * @throws Exception
	 */
	public boolean deleteZipFileName(String filename) throws Exception {
		return new BSUtil().deleteZipFileName(filename);
	}

	/**
	 * 根据文件名判断附件上传路径下文件是否存在
	 * @param filename文件名
	 * @return
	 * @throws Exception
	 */
	public boolean fileExist(String filename) throws Exception {
		return new BSUtil().fileExist(filename);
	}

	/**
	 * 根据文件名数组判断附件上传路径下是否含全部文件
	 * @param filenames文件名 数组
	 * @return
	 * @throws Exception
	 */
	public boolean filesExist(String[] filenames) throws Exception {
		return new BSUtil().filesExist(filenames);
	}

	/**
	 * 往模板子表中中插入数据
	 */
	public String insertTempletItem(String _templetId, String[][] _items) throws Exception {
		return new XMLIOUtil().insertTempletItem(_templetId, _items);
	}

	/**
	 * 在两个数据源中迁移模板!! 
	 */
	public void transportTempletToDataSource(String[] templete_codes, String str_sourceDS, Object[] _destDSs) throws Exception {
		new XMLIOUtil().transportTempletToDataSource(templete_codes, str_sourceDS, _destDSs);
	}

	/**
	 * 保存富多行文本框中的内容!!即打断存储在pub_stylepaddoc表中
	 */
	public String saveStylePadDocument(String _batchid, String _doc64code, String _text, String[] _sqls) throws Exception {
		return new MetaDataDMO().saveStylePadDocument(_batchid, _doc64code, _text, _sqls); //
	}

	/**
	 * 保存【图片上传】控件中的图片内容到数据库中!
	 */
	public String saveImageUploadDocument(String _batchid, String _doc64code, String _tabName, String _pkName, String _pkValue) throws Exception {
		return new MetaDataDMO().saveImageUploadDocument(_batchid, _doc64code, _tabName, _pkName, _pkValue); //
	}

	/**
	 * 解析自动生成数据配置信息，自动插入数据。大于500条 每500条执行一次。
	 */
	public void buildDemoData(String table, BillVO[] _vos, int _num) throws Exception {
		new BuildDemoDataUtil(table, _vos, _num);
	}

	/**
	 * 根据模板中ITEM的导出定义（不导出，半列导出，全列导出），把billvo导出html
	 * @param temp_1_itemvo
	 * @param vo
	 * @return
	 */
	public String getCardReportHtml(Pub_Templet_1VO template, BillVO vo) {
		MetaDataBSUtil util = new MetaDataBSUtil();
		return util.getReportHtml(template, vo);
	}

	/**
	 * 根据模板中ITEM的导出定义（不导出，半列导出，全列导出），把billvo导出word
	 * @param temp_1_itemvo
	 * @param vo
	 * @return
	 */
	public byte[] getCardReportWord(Pub_Templet_1VO templetVO, BillVO billVO) throws Exception {
		MetaDataBSUtil util = new MetaDataBSUtil();
		return util.getCardReportWord(templetVO, billVO);
	}

	public String getAddColumnSql(String _dbtype, String _tabName, String _cName, String _cType, String _cLength) throws Exception {
		return getDataBaseUtilDMO().getAlterSQLByTabDefineName(_dbtype, _tabName, _cName, _cType, _cLength);
	}

	public String getDropColumnSql(String _dbtype, String _tabName, String _cName) throws Exception {
		return getDataBaseUtilDMO().getAlterDeleteSQLByTabDefineName(_dbtype, _tabName, _cName);
	}

	/**
	* 导出数据库中所有表的数据字典，反向生成tables.xml【李春娟/2012-08-27】
	*/
	public String exportAllTables() throws Exception {
		return new XMLIOUtil().exportAllTables();
	}

	public DataBaseUtilDMO getDataBaseUtilDMO() {
		if (dataDMO == null) {
			dataDMO = new DataBaseUtilDMO();
		}
		return dataDMO;
	}

	//excel数据上传规则校验【李春娟/2019-06-10】
	public HashMap checkExcelDataByPolicy(HashVO[] _hashVOs, String _excelID) throws Exception {
		return (new ImportExcelDMO()).checkExcelDataByPolicy(_hashVOs, _excelID);
	}

}
