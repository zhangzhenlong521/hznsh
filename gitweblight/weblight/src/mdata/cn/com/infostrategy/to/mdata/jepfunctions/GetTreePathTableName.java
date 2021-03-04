package cn.com.infostrategy.to.mdata.jepfunctions;

/**
 * �õ�����ȫ·����!!
 * �﷨�� getTreePathColValue("pub_corp_dept[����]","name[Ҫ���ص��ֶ�]","id[���ͽṹ�����������ֶ�]","parentid[�����ֶ�Ҫ������parent�ֶ���]","id[where�ֶ�]","12345[whereֵ]");  
 * ����getTreePathColValue("pub_corp_dept","name","id","parentid","id",getItemValue("createcorpid")); ����:��������;���ݷ���;���ݷ��й���;
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

	private HashMap bsDataCacheMap = new HashMap(); //BS�˵����ݻ���!!!
	private TBUtil tbUtil = null; //

	public GetTreePathTableName(int _type) {
		this.jepType = _type;
		numberOfParameters = -1; //��������!!
	}

	public void run(Stack _stack) throws ParseException {
		checkStack(_stack); //
		HashMap parsMap = null; //�洢���в�����Map...
		HashMap trunMap = null; //��ȡ�Ĳ������ã�
		try {
			String str_whereCondition = null;
			String str_whereFieldName = null;
			String str_linkedParentFieldName = null;
			String str_linkedIDFieldName = null;
			String str_returnfieldName = null;
			String str_tableName = null;

			if (curNumberOfParameters == 6) {
				str_whereCondition = (String) _stack.pop(); //where������
				str_whereFieldName = (String) _stack.pop(); //where���ֶ���
				str_linkedParentFieldName = (String) _stack.pop(); //
				str_linkedIDFieldName = (String) _stack.pop(); //
				str_returnfieldName = (String) _stack.pop(); //
				str_tableName = (String) _stack.pop(); //����
			} else if (curNumberOfParameters == 7) {
				String str_pars = (String) _stack.pop(); //�Ȱ����в���������
				parsMap = getTBUtil().convertStrToMapByExpress(str_pars, "/", "="); //
				str_whereCondition = (String) _stack.pop(); //where������
				str_whereFieldName = (String) _stack.pop(); //where���ֶ���
				str_linkedParentFieldName = (String) _stack.pop(); //
				str_linkedIDFieldName = (String) _stack.pop(); //
				str_returnfieldName = (String) _stack.pop(); //
				str_tableName = (String) _stack.pop(); //����
			} else if (curNumberOfParameters == 8) {
				String str_truncation = (String) _stack.pop(); //�ѽ�ȡ�Ĳ���������  [2012-08-16]����
				trunMap = getTBUtil().convertStrToMapByExpress(str_truncation, "/", "=");
				String str_pars = (String) _stack.pop(); //�Ȱ����в���������
				parsMap = getTBUtil().convertStrToMapByExpress(str_pars, "/", "="); //
				str_whereCondition = (String) _stack.pop(); //where������
				str_whereFieldName = (String) _stack.pop(); //where���ֶ���
				str_linkedParentFieldName = (String) _stack.pop(); //
				str_linkedIDFieldName = (String) _stack.pop(); //
				str_returnfieldName = (String) _stack.pop(); //
				str_tableName = (String) _stack.pop(); //����
			}

			if (str_whereCondition == null || str_whereCondition.trim().equals("")) { //���ֵΪ��,��ֱ�ӷ���!!!
				_stack.push(""); //
				return;
			}

			if (this.jepType == WLTConstants.JEPTYPE_UI) { //����ǿͻ���
				FrameWorkCommServiceIfc commService = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class); //
				if (str_whereCondition.indexOf(";") >= 0) { //����ж��,��ǰUI���и�bug����֧�ֶ��!
					String[] str_idValues = getTBUtil().split(str_whereCondition, ";"); //
					HashMap returnMap = commService.getTreePathNameByRecords(null, str_tableName, str_linkedIDFieldName, str_returnfieldName, str_linkedParentFieldName, str_idValues); //
					StringBuilder sb_name = new StringBuilder(); //
					for (int i = 0; i < str_idValues.length; i++) {
						sb_name.append(returnMap.get(str_idValues[i]) + ";"); //
					}
					_stack.push(sb_name.toString()); //
					return; //
				} else { //����ǵ���!!!
					HashVO[] parentHVs = commService.getTreePathVOsByOneRecord(null, str_tableName, str_linkedIDFieldName, str_linkedParentFieldName, str_whereFieldName, str_whereCondition); //ȡ�����и��׽�������
					boolean isTrimLevel1 = true; //
					String trunofnum = null; //ͨ�������ȡ����Ӧ�Ĳ����ǡ��Ƿ�ص���һ�㡱���÷�ʽ���Ƿ�ص���һ��=1245��ʾ��ȡ��1245�㣬ֻ��3
					if (parsMap != null && "N".equals(parsMap.get("�Ƿ�ص���һ��"))) { //���ָ���˲����زŲ�����!!!
						isTrimLevel1 = false; //
					}
					if (parsMap != null && parsMap.get("�Ƿ�ص���һ��") != null && !"YN".contains((String) parsMap.get("�Ƿ�ص���һ��"))) { //�������ֵ����Y��N  ��ôҪ����12345������
						trunofnum = (String) parsMap.get("�Ƿ�ص���һ��");
					}
					String trun_itemname = null;
					String[] trun_itemvalue = null;
					if (trunMap != null) {
						trun_itemname = (String) trunMap.get("�ص����ֶ�����");
						String s1 = (String) trunMap.get("�ص����ֶ�ֵ");
						if (trun_itemname != null && s1 != null) {
							trun_itemvalue = getTBUtil().split(s1, ","); //����ֵ��Ҫ��Ӣ�Ķ��Ÿ���
						}
					}

					StringBuilder sb_names = new StringBuilder(); //
					if (parentHVs != null && parentHVs.length > 0) {
						for (int i = (isTrimLevel1 && trunofnum == null ? 1 : 0); i < parentHVs.length; i++) { //�������и��׽��!!!!
							if (trunofnum != null && trunofnum.contains(i + "")) {
								continue;
							}
							if (trun_itemname != null && trun_itemvalue != null && trun_itemvalue.length > 0) { //�������������Ľ�ȡ��ʽ���ص�ĳ���ڵ��������ӽڵ㡣
								boolean flag = false;//��ʾ
								String value = parentHVs[i].getStringValue(trun_itemname);//�������õ��ֶ�ȥ���������ϵĸ��ֶ�ֵ
								for (int j = 0; j < trun_itemvalue.length; j++) {//ѭ���Ƚ��Զ����ֵ
									if (trun_itemvalue[j].equals(value)) {
										flag = true;
										break;
									}
								}
								if (flag)
									break;
							}
							sb_names.append(parentHVs[i].getStringValue(str_returnfieldName, "") + "-"); //ƴ����!!
						}
						if (sb_names.length() > 0) {
							_stack.push(sb_names.substring(0, sb_names.length() - 1)); //��ǰ�Ǽ�2��,�������ҵ��Ŀ�з������ǽ�ȡ�����һλ!!Ӧ���Ǽ�1	
						} else {
							_stack.push("");
						}
						return;
					}
				}
			} else if (this.jepType == WLTConstants.JEPTYPE_BS) { //�����BS��,���ܹ�����UI�˵Ķ��ȡ���ݿ�İ취!!
				HashVO[] hvsAll = null; //
				if (str_tableName.equalsIgnoreCase("pub_corp_dept")) { //����ǻ���,�����⴦��,�ӻ���ȡ!!
					hvsAll = ServerCacheDataFactory.getInstance().getCorpCacheDataByAutoCreate(); //
				} else { //�������ͨ��,��ȡ�����ݿⲢ���뻺��!!
					String str_sql = ("select * from " + str_tableName).toLowerCase(); //
					if (!bsDataCacheMap.containsKey(str_sql)) { //���������û��,���ڵ�һ��Ҫ����!!
						cn.com.infostrategy.bs.common.CommDMO commDMO = new cn.com.infostrategy.bs.common.CommDMO(); //
						bsDataCacheMap.put(str_sql, commDMO.getHashVoArrayAsTreeStructByDS(null, str_sql, str_linkedIDFieldName, str_linkedParentFieldName, null, null)); //һ�����ҳ����н��!���Զ�����һ����
					}
					hvsAll = (HashVO[]) bsDataCacheMap.get(str_sql); ////
				}
				String str_cacheKey = "$" + str_tableName.toLowerCase() + "_" + str_linkedIDFieldName.toLowerCase(); //�ù�˾�л�Ҫע�Ỻ��!!
				HashMap thisHVOMap = getMapFromThisCache(str_cacheKey, hvsAll, str_linkedIDFieldName); //����Ҫע���»���,��ΪƵ��ͨ��Forѭ���һ���������!!!
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
	 * _trunMap���������磺�ص����ֶ�����=corptype/�ص����ֶ�ֵ=���в��Ŵ���,һ�����в��Ŵ���,�������в��Ŵ��� ���ҵ�������� �ֶ�corptype='���в��Ŵ���'��'һ�����в��Ŵ���'���Ѿ��ڵ�����ȫ���ص���
	 */
	private String getTreePathItemValueFromHashVOs(HashMap _allHVOMap, String str_linkedIDFieldName, String _returnfieldName, String _whereFieldName, String _whereCondition, HashMap _parsMap, HashMap _trunMap) {
		//long ll_1 = System.currentTimeMillis(); //
		boolean isReturnPathLinkName = true; //Ĭ���Ƿ���·����!!
		boolean isTrimLevel1 = true; //
		String trun_itemname = null; //�Զ����ȡ�����ֶ����� ����������� corptype
		String[] trun_itemvalue = null;//�Զ����ȡ�����ֶ�ֵ ��: ���в��Ŵ��� ���в��Ŵ���  
		String trunofnum = null; //ͨ�������ȡ����Ӧ�Ĳ����ǡ��Ƿ�ص���һ�㡱���÷�ʽ���Ƿ�ص���һ��=1245��ʾ��ȡ��1245�㣬ֻ��3
		if (_parsMap != null && "N".equals(_parsMap.get("����·������"))) { //���ָ���˲����زŲ�����!!!
			isReturnPathLinkName = false; //
		}
		if (_parsMap != null && "N".equals(_parsMap.get("�Ƿ�ص���һ��"))) { //���ָ���˲����زŲ�����!!!
			isTrimLevel1 = false; //
		}
		if (_parsMap != null && _parsMap.get("�Ƿ�ص���һ��") != null && !"YN".contains((String) _parsMap.get("�Ƿ�ص���һ��"))) {
			trunofnum = (String) _parsMap.get("�Ƿ�ص���һ��");
		}
		if (_trunMap != null) {
			trun_itemname = (String) _trunMap.get("�ص����ֶ�����");
			String s1 = (String) _trunMap.get("�ص����ֶ�ֵ");
			if (trun_itemname != null && s1 != null) {
				trun_itemvalue = getTBUtil().split(s1, ",");
			}
		}
		String[] str_whereConditions = getTBUtil().split(_whereCondition, ";"); //�����ж��id,��ͬʱ���ݵ�ѡ���ѡ!!
		StringBuilder sb_tmp = new StringBuilder(""); //
		for (int w = 0; w < str_whereConditions.length; w++) { //��������,����ǵ�ѡ,��ֻѭ��һ��!
			HashVO hvoitem = (HashVO) _allHVOMap.get(str_whereConditions[w]); //����!
			if (hvoitem != null) { //����ҵ���
				if (isReturnPathLinkName) { //����Ƿ���·����!!!
					String str_parentids = hvoitem.getStringValue("$parentpathids"); //����·��
					if (str_parentids != null) { //����и��׼�¼
						String[] str_ids = getTBUtil().split(str_parentids, ";"); //�õ�����·��,�ָ�һ��!!
						StringBuilder sb_retrunPathLinkName = new StringBuilder(); //
						for (int i = (isTrimLevel1 && trunofnum == null ? 1 : 0); i < str_ids.length; i++) { //��������,����������ĸ������Զ����ҵ����,������Ժ�Ӧ���Ǹ�����,Ĭ���ǲ��������!!
							if (trunofnum != null && trunofnum.contains(i + "")) {
								continue;
							}
							HashVO linkparentVO = (HashVO) _allHVOMap.get(str_ids[i]); //�õ����׵ĸ����ڵ�VO
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
							sb_retrunPathLinkName.append(str_itemName); //ƴ����!!
							if (i != str_ids.length - 1) {
								sb_retrunPathLinkName.append("-"); //ƴ��!
							}
						}
						int length = sb_retrunPathLinkName.length();
						if (length > 0) {
							String str = sb_retrunPathLinkName.substring(length - 1, length); //������������Ľ�ȡ�����һ�����ܻ����"-",��Ҫ�ص�
							if ("-".equals(str)) {
								sb_retrunPathLinkName.replace(length - 1, length, "");
							}
						}
						sb_tmp.append(sb_retrunPathLinkName.toString()); //
						if (str_whereConditions.length > 1) { //����>1�Ŷ� [2012-05-24����]
							sb_tmp.append(";"); //����Ƕ�ѡ,��Ҫ���Ϸֺ�!!!
						}
					}
				} else { //���������·��!!
					String str_itemName = hvoitem.getStringValue(_returnfieldName); //
					sb_tmp.append(str_itemName); //
					if (str_whereConditions.length > 1) {
						sb_tmp.append(";"); //����Ƕ�ѡ,��Ҫ���Ϸֺ�!!!
					}
				}
			}
		}
		return sb_tmp.toString(); //����!!!
	}

	private HashMap getMapFromThisCache(String _cacheKey, HashVO[] _hvs, String _id) {
		if (bsDataCacheMap.containsKey(_cacheKey)) {
			return (HashMap) bsDataCacheMap.get(_cacheKey); //
		} else {
			HashMap tempMap = new HashMap(); //������ϣ��
			for (int i = 0; i < _hvs.length; i++) {
				tempMap.put(_hvs[i].getStringValue(_id), _hvs[i]); //
			}
			bsDataCacheMap.put(_cacheKey, tempMap); //
			return tempMap; //ֱ�ӷ���
		}
	}

	private TBUtil getTBUtil() {
		if (this.tbUtil == null) {
			tbUtil = new TBUtil(); //
		}
		return tbUtil; //
	}
}
