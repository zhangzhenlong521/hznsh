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
 * Ԫ���ݵ���Ҫ����,����ȡԪ����ģ����������,ȡ��BillCardPanel��BillListPanel�е����ݵ�,ע�������Դ������!!
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
	 * ����ģ�����ƴ�Xml�ļ��д���ģ��VO
	 */
	public DefaultTMO getDefaultTMOByCode(String _code, int _fromType) throws Exception {
		MetaDataDMO metaDMO = ServerEnvironment.getMetaDataDMO();
		return metaDMO.getDefaultTMOByCode(_code, _fromType);
	}

	/**
	 * ����һ����ȡ�ö��,ʡȥ���Զ�̷���
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

	//ȡ�ò�ѯ����ϵ�SQL�Զ��崴�����Ĵ�����SQL
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

	//����BillVO������,Ϊ���������
	public BillVOBuilder getBillVOBuilder(String _datasourcename, String _sql, String _templetCode) throws Exception { //
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		return new MetaDataDMO().getBillVOBuilder(_datasourcename, _sql, _templetCode); //
	}

	//����BillVO������,Ϊ���������
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
		return ServerEnvironment.getMetaDataDMO().getBillVOsByDS(_datasourcename, _sql, _templetVO, _isExecLoadFormula); // ȡ��BillVO����!!!
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

	//������������ͨ��SQL��ѯ����,����ֱ��ͨ��HashVO[]��ѯ����!!
	public Object[][] getBillListDataByHashVOs(Pub_Templet_1ParVO _templetVO, HashVO[] _hashVOs) throws Exception { //
		return new MetaDataDMO().getBillListDataByHashVOs(_templetVO, _hashVOs); //
	}

	public TableDataStruct getBillTreeData(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		return new MetaDataDMO().getBillTreeData(_datasourcename, _sql, _templetVO); //
	}

	//�������ͽṹ��linkName
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
			if (!str_linkName.equals(str_dbLinkName)) { //������߲���,������!!
				al_sqls.add("update pub_corp_dept set " + _linkName + "='" + str_linkName + "' where " + _pkId + "='" + str_pkValue + "'"); //
			}
		}

		if (al_sqls.size() > 0) {
			dmo.executeBatchByDS(_datasourcename, al_sqls); //
		}
		long ll_2 = System.currentTimeMillis(); //
		return "һ��������[" + al_sqls.size() + "]��������LinkName,�������˼����ʱ[" + (ll_2 - ll_1) + "]"; //
	}

	/**
	 * ���һ�����ͽṹ��ĳ���ֶ��Ƿ�����ĳ�ֹ���!����������еĻ��������ֶ�,�������һ���Ĺ���!�������֧�в���ֱ�ӹ���һ����������!��xch/2012-04-24��
	 */
	public String[] checkTreeOneFieldRule(String _datasourcename, String _table, String _pkfield, String _nameField, String _parentPKfield, String _seqField, String _checkField, String[] _rules) throws Exception {
		CommDMO dmo = new CommDMO(); //
		HashVO[] hvs = dmo.getHashVoArrayAsTreeStructByDS(_datasourcename, "select " + _pkfield + "," + _nameField + "," + _parentPKfield + "," + _checkField + " from " + _table, _pkfield, _parentPKfield, _seqField, null); //
		HashMap tempMap = new HashMap(); //
		for (int i = 0; i < hvs.length; i++) { //�Ƚ�����!!
			tempMap.put(hvs[i].getStringValue(_pkfield), hvs[i]); //������,Ϊ�˺���Ѱ��ʱ����!
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
		for (int i = 0; i < hvs.length; i++) { //����!
			str_pkValue = hvs[i].getStringValue(_pkfield); //����ֵ!
			str_nameValue = hvs[i].getStringValue(_nameField); //����ֵ!
			str_parentIds = hvs[i].getStringValue("$parentpathids"); //�û��������и��׻���
			str_parentNames = hvs[i].getStringValue("$parentpathnamelink"); //����ȫ��

			String str_checkFieldValue = hvs[i].getStringValue(_checkField); //������ֶε�ֵ!!

			if (str_checkFieldValue == null || str_checkFieldValue.trim().equals("")) { //��������ֶ�Ϊ�ջ�մ�
				li_count1++; //
				sb_sql_null.append("update " + _table + " set " + _checkField + " ='' where " + _pkfield + "='" + str_pkValue + "' or '����ȫ��'='" + str_parentNames + "';\r\n");
			}

			if (str_parentIds != null && !str_parentIds.trim().equals("")) {
				String[] str_parentIdArray = tbUtil.split(str_parentIds, ";"); //�ָ�
				String[] str_parentName = new String[str_parentIdArray.length]; //
				String[] str_parentType = new String[str_parentIdArray.length]; //
				StringBuilder sb_types = new StringBuilder(); //
				String str_myRealParentTypeValue = null; // 
				for (int j = 0; j < str_parentIdArray.length; j++) { //�����ҵ����и���
					HashVO parentVO = (HashVO) tempMap.get(str_parentIdArray[j]); //
					str_parentName[j] = parentVO.getStringValue(_nameField); //ʵ������
					str_parentType[j] = parentVO.getStringValue(_checkField); //
					sb_types.append("" + parentVO.getStringValue(_checkField)); //
					if (j != str_parentIdArray.length - 1) {
						sb_types.append("-"); //
					}
					if (j == str_parentIdArray.length - 2) {
						str_myRealParentTypeValue = parentVO.getStringValue(_checkField); //�ҵ�ֱ�Ӹ��׵�����ֵ!
					}
				}

				String str_myCorpType = sb_types.toString(); //
				boolean isMatch = false; //�Ƿ�ƥ����?
				for (int k = 0; k < _rules.length; k++) {
					if (_rules[k].startsWith(str_myCorpType)) { //�ڸ���������,�����һ�����������ҵ�·����ͷ��,��˵�����ǺϷ���!
						isMatch = true; //
						break; //
					}
				}
				if (!isMatch) { //���ûƥ����!
					if (str_myCorpType.indexOf("null") > 0) { //��������Ϊ����ɵ�!Ҳ����˵����ѵ�һ��Ϊ�յĸĺ�����Щ����һ����Ҳ����!!!
						li_count2++;
						sb_unLawByNulls.append(li_count2 + "[" + str_parentNames + "]  ��·��ֵ [" + str_myCorpType + "]����!\r\n"); //
					} else {
						li_count3++;
						sb_sql_unlaw.append("update " + _table + " set " + _checkField + " ='" + str_myRealParentTypeValue + "' where " + _pkfield + "='" + str_pkValue + "' or '" + str_parentNames + "'='" + str_myCorpType + "';\r\n"); //
					}
				}

				//���ܼ���!!!,������������ǻ�������,������������ж�!
				if (_table.equalsIgnoreCase("pub_corp_dept") && _checkField.equalsIgnoreCase("corptype")) {
					if (str_parentName.length >= 3 && !str_parentName[1].equals("����")) {
						if (str_parentName.length == 3) { //��һ��֧��/��������/һ�����в���!
							if (str_parentName[2].endsWith("����")) {
								if (!"��������".equals(str_checkFieldValue)) { //������Ƕ�������
									//sb_sql_zzz.append("[" + str_parentNames + "]�����ͻ���Ӧ��[��������],ԭ����[" + str_checkFieldValue + "]\r\n"); //
									sb_sql_caiche.append("update " + _table + " set " + _checkField + " ='��������' where " + _pkfield + "='" + str_pkValue + "' or '" + str_parentNames + "'='" + str_myCorpType + "';\r\n"); //
								}
							} else if (str_parentName[2].endsWith("֧��")) {
								if (!"һ��֧��".equals(str_checkFieldValue)) { //������Ƕ�������
									//sb_sql_zzz.append("[" + str_parentNames + "]�����ͻ���Ӧ��[һ��֧��],ԭ����[" + str_checkFieldValue + "]\r\n"); //
									sb_sql_caiche.append("update " + _table + " set " + _checkField + " ='һ��֧��' where " + _pkfield + "='" + str_pkValue + "' or '" + str_parentNames + "'='" + str_myCorpType + "';\r\n"); //
								}
							} else {
								if (!"һ�����в���".equals(str_checkFieldValue)) { //������Ƕ�������
									//sb_sql_zzz.append("[" + str_parentNames + "]�����ͻ���Ӧ��[һ�����в���],ԭ����[" + str_checkFieldValue + "]\r\n"); //
									sb_sql_caiche.append("update " + _table + " set " + _checkField + " ='һ�����в���' where " + _pkfield + "='" + str_pkValue + "' or '" + str_parentNames + "'='" + str_myCorpType + "';\r\n"); //
								}
							}
						} else if (str_parentName.length == 4) { //��һ��֧��/��������/һ�����в���!
							if (str_parentName[2].endsWith("����")) {
								if (str_parentName[3].endsWith("֧��")) { //����������֧��,���Ǽ�֧��!
									if (!"����֧��".equals(str_checkFieldValue)) { //������Ƕ�������
										//sb_sql_zzz.append("[" + str_parentNames + "]�����ͻ���Ӧ��[����֧��],ԭ����[" + str_checkFieldValue + "]\r\n"); //
										sb_sql_caiche.append("update " + _table + " set " + _checkField + " ='����֧��' where " + _pkfield + "='" + str_pkValue + "' or '" + str_parentNames + "'='" + str_myCorpType + "';\r\n"); //
									}
								} else {
									if (!"�������в���".equals(str_checkFieldValue)) { //������Ƕ�������
										//sb_sql_zzz.append("[" + str_parentNames + "]�����ͻ���Ӧ��[�������в���],ԭ����[" + str_checkFieldValue + "]\r\n"); //
										sb_sql_caiche.append("update " + _table + " set " + _checkField + " ='�������в���' where " + _pkfield + "='" + str_pkValue + "' or '" + str_parentNames + "'='" + str_myCorpType + "';\r\n"); //
									}
								}
							} else if (str_parentName[2].endsWith("֧��")) {
								if (!"һ��֧�в���".equals(str_checkFieldValue)) { //������Ƕ�������
									//sb_sql_zzz.append("[" + str_parentNames + "]�����ͻ���Ӧ��[һ��֧�в���],ԭ����[" + str_checkFieldValue + "]\r\n"); //
									sb_sql_caiche.append("update " + _table + " set " + _checkField + " ='һ��֧�в���' where " + _pkfield + "='" + str_pkValue + "' or '" + str_parentNames + "'='" + str_myCorpType + "';\r\n"); //
								}
							} else {
								if (!"һ�����в���".equals(str_checkFieldValue)) { //������Ƕ�������
									//sb_sql_zzz.append("[" + str_parentNames + "]�����ͻ���Ӧ��[һ�����в���],ԭ����[" + str_checkFieldValue + "]\r\n"); //
									sb_sql_caiche.append("update " + _table + " set " + _checkField + " ='һ�����в���' where " + _pkfield + "='" + str_pkValue + "' or '" + str_parentNames + "'='" + str_myCorpType + "';\r\n"); //
								}
							}
						} else if (str_parentName.length >= 5) {
							if (str_parentName[2].endsWith("����")) {
								if (str_parentName[3].endsWith("֧��")) { //����������֧��,���Ǽ�֧��!
									if (!"����֧�в���".equals(str_checkFieldValue)) { //������Ƕ�������
										//sb_sql_zzz.append("[" + str_parentNames + "]�����ͻ���Ӧ��[����֧�в���],ԭ����[" + str_checkFieldValue + "]\r\n"); //
										sb_sql_caiche.append("update " + _table + " set " + _checkField + " ='����֧�в���' where " + _pkfield + "='" + str_pkValue + "' or '" + str_parentNames + "'='" + str_myCorpType + "';\r\n"); //
									}
								} else {
									if (!"�������в���".equals(str_checkFieldValue)) { //������Ƕ�������
										//sb_sql_zzz.append("[" + str_parentNames + "]�����ͻ���Ӧ��[�������в���],ԭ����[" + str_checkFieldValue + "]\r\n"); //
										sb_sql_caiche.append("update " + _table + " set " + _checkField + " ='�������в���' where " + _pkfield + "='" + str_pkValue + "' or '" + str_parentNames + "'='" + str_myCorpType + "';\r\n"); //
									}
								}
							} else if (str_parentName[2].endsWith("֧��")) {
								if (!"һ��֧�в���".equals(str_checkFieldValue)) { //������Ƕ�������
									//sb_sql_zzz.append("[" + str_parentNames + "]�����ͻ���Ӧ��[һ��֧�в���],ԭ����[" + str_checkFieldValue + "]\r\n"); //
									sb_sql_caiche.append("update " + _table + " set " + _checkField + " ='һ��֧�в���' where " + _pkfield + "='" + str_pkValue + "' or '" + str_parentNames + "'='" + str_myCorpType + "';\r\n"); //
								}
							} else {
								if (!"һ�����в���".equals(str_checkFieldValue)) { //������Ƕ�������
									//sb_sql_zzz.append("[" + str_parentNames + "]�����ͻ���Ӧ��[һ�����в���],ԭ����[" + str_checkFieldValue + "]\r\n"); //
									sb_sql_caiche.append("update " + _table + " set " + _checkField + " ='һ�����в���' where " + _pkfield + "='" + str_pkValue + "' or '" + str_parentNames + "'='" + str_myCorpType + "';\r\n"); //
								}
							}
						}
					}

					//�������ֲ²�
					if (str_nameValue != null && str_nameValue.endsWith("��") && str_checkFieldValue != null && !str_checkFieldValue.endsWith("��")) {
						sb_sql_caiche2.append("A[" + str_parentNames + "]���������Ϳ���Ӧ����[***��],����ʵ��ֵ��[" + str_myCorpType + "]\r\n"); //
					} else if (str_nameValue != null && str_nameValue.endsWith("��") && str_checkFieldValue != null && str_checkFieldValue.endsWith("��")) {
						sb_sql_caiche2.append("B[" + str_parentNames + "]���������Ϳ���Ӧ����[***��],����ʵ��ֵ��[" + str_myCorpType + "]\r\n"); //
					}
				}
			}
		}
		return new String[] { "�ܹ�" + li_count1 + "��:\r\n" + sb_sql_null.toString(), "�ܹ�" + li_count2 + "��:\r\n" + sb_unLawByNulls.toString(), "�ܹ�" + li_count3 + "��:\r\n" + sb_sql_unlaw.toString(), sb_sql_caiche.toString() + "\r\n\r\n" + sb_sql_caiche2.toString() }; //
	}

	public Object[][] getQueryDataByDS(String _datasourcename, String _sql, Pub_Templet_1VO _templetVO) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		MetaDataDMO metaDMO = ServerEnvironment.getMetaDataDMO();
		return metaDMO.getQueryDataByDS(_datasourcename, _sql, _templetVO); //
	}

	/**
	 * �ύһ��BillVO,�ǳ��ؼ�!!!
	 */
	public void commitBillVOByDS(String _datasourcename, BillVO[] _deleteVOs, BillVO[] _insertVOs, BillVO[] _updateVOs) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		new MetaDataDMO().commitBillVO(_datasourcename, _deleteVOs, _insertVOs, _updateVOs); //
	}

	//�����ɻ�Ҫ��ֱ������һ������,���������ڷ���!ͨ�����Լ���,����д��ʽ��!
	public HashMap getDataPolicyTargetCorpsByCorpId(String _deptId, String _datapolicy, String _returnCol) throws Exception {
		return new DataPolicyDMO().getDataPolicyTargetCorpsByCorpId(_deptId, _datapolicy, _returnCol); //
	}

	//�����ɻ�Ҫ��ֱ������һ����Ա,���������ڷ���!ͨ�����Լ���,����д��ʽ��!
	public HashMap getDataPolicyTargetCorpsByUserId(String _loginUserid, String _datapolicy, String _returnCol) throws Exception { //
		return new DataPolicyDMO().getDataPolicyTargetCorpsByUserId(_loginUserid, _datapolicy, _returnCol); //
	}

	//������Դ�����е�����,��̬�����һ����Ա��Ȩ�޿��Ƶ����С������嵥������Ա�嵥��
	public String[] getDataPolicyCondition(String _loginUserid, String _datapolicy, int _type, String _corpFieldName, String _userFieldName) throws Exception {
		return new DataPolicyDMO().getDataPolicyCondition(_loginUserid, _datapolicy, _type, _corpFieldName, _userFieldName); //
	}

	//�õ���ԴԼ������!
	public String[] getDataPolicyCondition(String _loginUserid, String _datapolicy, String _datapolicyMap) throws Exception {
		return new DataPolicyDMO().getDataPolicyCondition(_loginUserid, _datapolicy, _datapolicyMap); //
	}

	/**
	 * ȡ��BillCellVO
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
	 * ����BillCellPanel�ϵ�����!!
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

	// ͨ��ģ���޸ı�
	public void compareTemplateAndTable(String _datasourcename, String[] _tablename, String[] _template) throws Exception {
		new MetaDataDMO().compareTemplateAndTable(_datasourcename, _tablename, _template); //

	}

	// ��BillCellPanel�ﱣ�����ݵķ���
	public void getCellSaveDate(String templethid, String _billno, HashMap hashmap) throws Exception {
		new MetaDataDMO().getCellSaveDate(templethid, _billno, hashmap);

	}

	// ��BillCellPanel���ȡ���ݵķ���
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
	 * ����һ����Ľṹ������SQL(����)
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
	 * ����һ����Ľṹ������SQL(ȫ��)
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
	 * ȡ��
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
	 * ת������
	 */
	public int transferDB(String _sourcedsname, String _destdsname, String _table, boolean _iscreate, boolean _isinsert) throws Exception {
		return new MetaDataDMO().transferDB(_sourcedsname, _destdsname, _table, _iscreate, _isinsert); //
	}

	public String safeMoveData(String _sourcedsname, String _destdsname, String tablename, HashMap tableMap, String type, HashMap conditon) throws Exception {
		return new MetaDataDMO().safeMoveData(_sourcedsname, _destdsname, tablename, tableMap, type, conditon); //
	}

	/**
	 * ���ݱ����õ���ײ���ַ���������һ���ַ���
	 * 
	 * @param _tablename
	 *            ����
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getCollidePKByTableName(String[] _tablename, String _databasetype) throws Exception {
		return new DataBaseValidate().getCollidePKByTableName(_tablename, _databasetype);
	}

	/**
	 * �õ���ǰϵͳ�����к͹ؼ�����ײ���ֶ�
	 * 
	 * @param _databasetype
	 * @return
	 * @throws Exception
	 */
	public String getAllCollidePK(String _databasetype) throws Exception {
		return new DataBaseValidate().getAllCollidePK(_databasetype);
	}

	/**
	 * ת����ͼ
	 */
	public void transferDBView(String _sourcedsname, String _destdsname, String _viewname) throws Exception {
		new MetaDataDMO().transferDBView(_sourcedsname, _destdsname, _viewname); //
	}

	/**
	 * ����XML��ʽģ��
	 */
	public void importXMLTemplet(String str_dataSource, String textarea) throws Exception {
		new XMLIOUtil().importXMLTemplet(str_dataSource, textarea);
	}

	public String exportXMLTemplet(String[] str_templete_code, int[] selectrows) throws Exception {
		return exportXMLTemplet(str_templete_code, selectrows, null);
	}

	/**
	 * ����ĳ��ģ���xml,��ǰ�Ļ��ƶ���Ӳд����,���Բ���ͨ��!!! �ؼ�������ģ���ṹ�ı仯,���ﻹ��Ҫ�޸Ĵ���! ��չ��������Բ���!!
	 * ��õİ취��Ҫ���������¼���:
	 * 1.ģ���ṹ�仯��,���ﲻ�����!!
	 * 2.������XML�뵼���ʵ�ʱ�ṹ֮�䷢������ʱ,��Զ������,������ʱ�ȼ�����Ŀ������м����ֶ�! Ȼ�󽻲�ƥ��!!
	 * 3.�������Զ�����,��������ҪҲ�����ѵ�! �����Ǻܺô����(����ָ��һ��������),������ʱ,��Ҫһ���ܺõĻ���! ����֮��Ĺ���,��Ҫ�����ӱ�������α��?
	 * 4.��֧�ִ�����,Ҳ֧�ֶ�̬����
	 * 5.֧�ַ�������(�����Ȳ�����) 
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
	 * ����XML��ʽע�ᰴť
	 */
	public String exportXMLRegButton(String[] str_refbutton_code, int[] selectrows) throws Exception {
		return new XMLIOUtil().exportXMLRegButton(str_refbutton_code, selectrows);
	}

	/**
	 * ����XML��ʽע�����
	 */
	public String exportXMLRegRef(String[] str_refref_code, int[] selectrows) throws Exception {
		return new XMLIOUtil().exportXMLRegRef(str_refref_code, selectrows);
	}

	/**
	 * ����XML��ʽ����
	 */
	public String exportXMLProcess(String[] str_process_code, int[] selectrows) throws Exception {
		return new XMLIOUtil().exportXMLProcess(str_process_code, selectrows);
	}

	/**
	 * ����XML��ʽ�������
	 */
	public String exportXMLCombobox(String[] str_combobox_code, int[] selectrows) throws Exception {
		return new XMLIOUtil().exportXMLCombobox(str_combobox_code, selectrows);
	}

	/**
	 * ����XML��ʽע�ᰴť
	 */
	public void importXMLRegButton(String textarea) throws Exception {
		new XMLIOUtil().importXMLRegButton(textarea);
	}

	/**
	 * ����XML��ʽע������
	 */
	public void importXMLRegFormat(String textarea) throws Exception {
		new XMLIOUtil().importXMLRegFormat(textarea);
	}

	/**
	 * ����XML��ʽע�����
	 */
	public void importXMLRegRef(String textarea) throws Exception {
		new XMLIOUtil().importXMLRegRef(textarea);
	}

	/**
	 * ����XML��ʽ����
	 */
	public void importXMLProcess(String textarea) throws Exception {
		new XMLIOUtil().importXMLProcess(textarea);
	}

	/**
	 * ����ϲ�xml��ʽ������
	 */
	public String importDBXMLProcess(String wf_code, String wf_name, String str_xml, String userdef01, String wf_securitylevel) throws Exception {
		return new XMLIOUtil().importDBXMLProcess(wf_code, wf_name, str_xml, userdef01, wf_securitylevel);
	}

	/**
	 * ����XML��ʽ����,��ɾ��ԭ�У���ʵ���Ǹ��ƹ���
	 */
	public void importXMLProcess_Copy(String textarea) throws Exception {
		new XMLIOUtil().importXMLProcess_Copy(textarea);
	}

	/**
	 * ��������ͼ
	 */
	public String copyProcessById(String _processid, String _processcode, String _processname) throws Exception {
		return new WorkFlowBSUtil().copyProcessById(_processid, _processcode, _processname);
	}

	/**
	 * ����XML��ʽ��Excel
	 * @param templetcode
	 * @return
	 */
	public String exportXMLForExcel(String templetcode) {
		return new XMLIOUtil().exportXMLForExcel(templetcode);
	}

	/**
	 * ͨ�õ���XML���� ������ 2008-11-21
	 */
	public void importXML(String text) throws Exception {
		new XMLIOUtil().importXML(text);
	}

	/**
	 * ����XML��ʽ������
	 */
	public void importXMLCombobox(String textarea) throws Exception {
		new XMLIOUtil().importXMLCombobox(textarea);
	}

	/**
	 * ����XML��ʽע������
	 */
	public String exportXMLRegFormat(String[] ids) throws Exception {
		return new XMLIOUtil().exportXMLRegFormat(ids);
	}

	/**
	 * �����޸ĺۼ�.
	 */
	public void saveKeepTrace(String _tablename, String _pkname, String _pkvalue, HashMap _fieldvalues, String _tracer) throws Exception {
		new CommDMO().saveKeepTrace(_tablename, _pkname, _pkvalue, _fieldvalues, _tracer);
	}

	/**
	 * ��������
	 */
	public String getZipFileName(String[] filenames) throws Exception {
		return new BSUtil().getZipFileName(filenames);
	}

	/**
	 * �����ļ����ӷ�����ɾ��office�ؼ��ϴ����ļ�
	 * @param filename �ļ��� �ļ�������Ϊ���·��
	 * @return
	 * @throws Exception
	 */
	public boolean deleteOfficeFileName(String _filename) throws Exception {
		String str_fileName = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/officecompfile/" + _filename; //
		File file = new File(str_fileName); //
		if (file.exists()) {
			boolean del_result = file.delete(); //ɾ���ļ�
			if (del_result) {
				System.out.println("�ӷ�����ɾ���ļ�[" + str_fileName + "]�ɹ���");
			} else {
				System.err.println("�ӷ�����ɾ���ļ�[" + str_fileName + "]ʧ��,���ܲ�����ļ�,����ļ���������");
			}
			return del_result; //
		} else {
			System.out.println("�ӷ�����ɾ���ļ�[" + str_fileName + "]ʱ���ֲ����ڸ��ļ���");
			return false;
		}
	}

	/**
	 * �����ļ���ɾ�������ϴ����ļ�
	 * @param filename �ļ��� �ļ�������Ϊ���·��
	 * @return
	 * @throws Exception
	 */
	public boolean deleteZipFileName(String filename) throws Exception {
		return new BSUtil().deleteZipFileName(filename);
	}

	/**
	 * �����ļ����жϸ����ϴ�·�����ļ��Ƿ����
	 * @param filename�ļ���
	 * @return
	 * @throws Exception
	 */
	public boolean fileExist(String filename) throws Exception {
		return new BSUtil().fileExist(filename);
	}

	/**
	 * �����ļ��������жϸ����ϴ�·�����Ƿ�ȫ���ļ�
	 * @param filenames�ļ��� ����
	 * @return
	 * @throws Exception
	 */
	public boolean filesExist(String[] filenames) throws Exception {
		return new BSUtil().filesExist(filenames);
	}

	/**
	 * ��ģ���ӱ����в�������
	 */
	public String insertTempletItem(String _templetId, String[][] _items) throws Exception {
		return new XMLIOUtil().insertTempletItem(_templetId, _items);
	}

	/**
	 * ����������Դ��Ǩ��ģ��!! 
	 */
	public void transportTempletToDataSource(String[] templete_codes, String str_sourceDS, Object[] _destDSs) throws Exception {
		new XMLIOUtil().transportTempletToDataSource(templete_codes, str_sourceDS, _destDSs);
	}

	/**
	 * ���渻�����ı����е�����!!����ϴ洢��pub_stylepaddoc����
	 */
	public String saveStylePadDocument(String _batchid, String _doc64code, String _text, String[] _sqls) throws Exception {
		return new MetaDataDMO().saveStylePadDocument(_batchid, _doc64code, _text, _sqls); //
	}

	/**
	 * ���桾ͼƬ�ϴ����ؼ��е�ͼƬ���ݵ����ݿ���!
	 */
	public String saveImageUploadDocument(String _batchid, String _doc64code, String _tabName, String _pkName, String _pkValue) throws Exception {
		return new MetaDataDMO().saveImageUploadDocument(_batchid, _doc64code, _tabName, _pkName, _pkValue); //
	}

	/**
	 * �����Զ���������������Ϣ���Զ��������ݡ�����500�� ÿ500��ִ��һ�Ρ�
	 */
	public void buildDemoData(String table, BillVO[] _vos, int _num) throws Exception {
		new BuildDemoDataUtil(table, _vos, _num);
	}

	/**
	 * ����ģ����ITEM�ĵ������壨�����������е�����ȫ�е���������billvo����html
	 * @param temp_1_itemvo
	 * @param vo
	 * @return
	 */
	public String getCardReportHtml(Pub_Templet_1VO template, BillVO vo) {
		MetaDataBSUtil util = new MetaDataBSUtil();
		return util.getReportHtml(template, vo);
	}

	/**
	 * ����ģ����ITEM�ĵ������壨�����������е�����ȫ�е���������billvo����word
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
	* �������ݿ������б�������ֵ䣬��������tables.xml�����/2012-08-27��
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

	//excel�����ϴ�����У�顾���/2019-06-10��
	public HashMap checkExcelDataByPolicy(HashVO[] _hashVOs, String _excelID) throws Exception {
		return (new ImportExcelDMO()).checkExcelDataByPolicy(_hashVOs, _excelID);
	}

}
