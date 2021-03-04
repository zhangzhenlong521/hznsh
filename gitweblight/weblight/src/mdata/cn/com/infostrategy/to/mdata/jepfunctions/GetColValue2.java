/**************************************************************************
 * $RCSfile: GetColValue2.java,v $  $Revision: 1.8 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/

package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;

/**
 * ����ID����һ������ȡName,����GetColValue()������������һ���������ֶ�,������ʵ��ֵ!!
 * ÿ����һ��JepFormulaParseAtBS�ͻᴴ��������һ��ʵ��!!!
 * @author xch
 *
 */
public class GetColValue2 extends PostfixMathCommand {

	private int li_type = -1;

	private HashMap cacheMap_keyValues = new HashMap(); //Ϊ���������,�����������Map,�ؼ�֮�ؼ�!!!
	private HashMap cacheMap_sqlDatas = new HashMap(); //Ϊ���������,�����������Map,�ؼ�֮�ؼ�!!!
	private HashMap cacheMap_sqlValues = new HashMap(); 
	//BS����Ĳ���
	private HashVO[] allDataHVS = null; //��������
	private HashMap colDataTypeMap = null; //һ������!!
	private HashMap rowDataMap = null; //һ������!!

	private CommDMO commDMO = null; //
	private TBUtil tBUtil = null; //

	public GetColValue2(int _type) {
		numberOfParameters = 4;
		li_type = _type;
	}

	public GetColValue2(int _type, HashVO[] _allDatas, HashMap _rowMap) {
		numberOfParameters = 4;
		li_type = _type;
		this.allDataHVS = _allDatas; //
		this.rowDataMap = _rowMap; //
	}

	public void run(Stack inStack) throws ParseException {
		try {
			//checkStack(inStack);
			Object param_1 = inStack.pop();
			Object param_2 = inStack.pop();
			Object param_3 = inStack.pop();
			Object param_4 = inStack.pop();

			String str_table_name = (String) param_4; //����������"pub_user"
			String str_returnfieldname = (String) param_3; //��Ҫȡ��������,����"name"
			String str_wherefieldname = (String) param_2; //where�������ֶ���,����"id"
			String str_wherefieldEqualsLoadFieldName = (String) param_1; //��һ�������ֶε�ͬ�ڼ��ص�������ĸ��ֶ�?? ����"createuser"

			if (li_type == WLTConstants.JEPTYPE_UI) { //�����UI��!!
				//				String str_sql_1 = "select " + str_returnfieldname + " from " + str_table_name + " where " + str_wherefieldname + "='" + str_wherefieldvalue + "'"; //
				//				HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, str_sql_1); //
				//				if (hvs.length > 0) { //���ȡ������
				//					inStack.push(hvs[0].getStringValue(0)); //
				//				} else {
				//					inStack.push(""); //
				//				}
			} else { //�����BS�˵�,����Ҫ��������!!��һ����ȡ����!!�´δӻ�����ȡ,���������Ч�����!!!
				if (!cacheMap_keyValues.containsKey(str_wherefieldEqualsLoadFieldName)) { //���û����ĳһ��,���Ƚ�ĳһ������ֵ����,�Է������ʽ����Ҫȡͬһ���е�ֵ,����������getColValue2��Ҫȡcreateuser��ֵ!!!!
					StringBuffer sb_allPTFieldValues = new StringBuffer(); //
					if (allDataHVS != null && allDataHVS.length > 0) {
						String str_itemValue = null; //
						ArrayList al_allValues = new ArrayList(); //
						HashSet hs_distinct = new HashSet(); //������Ψһ�Թ���!!��Ϊ�����������д�����¼��һ����,��������������й��ݷ���,��ʱƴ��ѯ�ӱ��SQL�Ϳ�����һ��Ψһ�Թ���,����������!!�Ժ󻹿��Կ���ͬһ�ű�,
						//����ͬһ���ֶ�ȡͬһ��������ֶ�,�������߹���Է�������!��������������Ա��ص��ֶ�,����ڶ����ֶε�ֵ����Щ�������ڵ�һ���д��ڹ��ˣ���ڶ�������ֻ��ѯ��������!�������һ���ֵ�����!! ��������Ժ����Ż���!!
						for (int i = 0; i < allDataHVS.length; i++) {
							str_itemValue = allDataHVS[i].getStringValue(str_wherefieldEqualsLoadFieldName); //
							if (str_itemValue != null && !str_itemValue.trim().equals("") && !str_itemValue.trim().equals("null") && !hs_distinct.contains(str_itemValue)) { //�����Ϊ��
								al_allValues.add(str_itemValue); //
								hs_distinct.add(str_itemValue); //
							}
						}
						if (al_allValues.size() > 0) {
							cacheMap_keyValues.put(str_wherefieldEqualsLoadFieldName, (String[]) al_allValues.toArray(new String[0])); //
						} else {
							cacheMap_keyValues.put(str_wherefieldEqualsLoadFieldName, null); //
						}
					}
				} else { //����Ѱ�������ȡ��

				}

				if (cacheMap_keyValues.get(str_wherefieldEqualsLoadFieldName) != null) { //��������� nullǿ������ת��Ϊ"null"�������������ж�һ��
					String str_inSQLCons = null;
					if(cacheMap_sqlValues.containsKey(str_wherefieldEqualsLoadFieldName)){
						str_inSQLCons = cacheMap_sqlValues.get(str_wherefieldEqualsLoadFieldName).toString();
					}else{
						String[] str_values = (String[]) cacheMap_keyValues.get(str_wherefieldEqualsLoadFieldName); //
						str_inSQLCons = getCommDMO().getSubSQLFromTempSQLTableByIDs(str_values); //
						cacheMap_sqlValues.put(str_wherefieldEqualsLoadFieldName, str_inSQLCons);
					}
					String str_sql_1 = "select " + str_wherefieldname + "," + str_returnfieldname + " from " + str_table_name + " where " + str_wherefieldname + " in (" + str_inSQLCons + ")"; //
					if (!cacheMap_sqlDatas.containsKey(str_sql_1)) { //���ûȡ��������ȡһ��,����ֱ�Ӵӻ�����ȡ,���仰˵����ֻ�е�һ��ȡһ�£��ڶ��ζ��Ǵӻ�����ȡ
						HashMap sqlMapData = getCommDMO().getHashMapBySQLByDS(null, str_sql_1, true); //��ֵƴ��һ���ַ���!!
						cacheMap_sqlDatas.put(str_sql_1, sqlMapData); //
					}

					HashMap dataMap = (HashMap) cacheMap_sqlDatas.get(str_sql_1); //�ӻ�����ȡ������!
					String str_thisRowItemValue = getThisRowRealData(str_wherefieldEqualsLoadFieldName); //���е�ʵ������,
					if (str_thisRowItemValue == null || str_thisRowItemValue.equals("")) {
						inStack.push("");
					} else {
						inStack.push((String) dataMap.get(str_thisRowItemValue)); ////ȥ��ϣ����ȡ,�����ݵ�ǰ��IDֵȥȡ��Name��ֵ!!
					}
				} else {
					inStack.push(""); //�����û����,��ֱ�ӷ��ؿ���,��ÿ����¼��createuser���ǿ�ֵ,��ȡnameҲ����Ϊ��!
				}
			}
		} catch (Throwable ex) {
			TBUtil.printStackTrace(ex); //
			inStack.push("");
		}
	}

	private String getThisRowRealData(String _itemKey) {
		Object obj = rowDataMap.get(_itemKey); //
		if (obj == null) {
			return null;
		}
		String str_value = null;
		if (obj instanceof String) {
			str_value = (String) obj; //
		} else if (obj instanceof StringItemVO) {
			StringItemVO itemVO = (StringItemVO) obj; //
			str_value = itemVO.getStringValue(); //
		} else if (obj instanceof ComBoxItemVO) {
			ComBoxItemVO itemVO = (ComBoxItemVO) obj; //
			str_value = itemVO.getId(); //
		} else if (obj instanceof RefItemVO) {
			RefItemVO itemVO = (RefItemVO) obj; //
			str_value = itemVO.getId(); //
		} else {
			str_value = obj.toString(); //
		}

		return str_value; //
	}

	public void setRowDataMap(HashMap rowDataMap) {
		this.rowDataMap = rowDataMap;
	}

	public void setColDataTypeMap(HashMap colDataTypeMap) {
		this.colDataTypeMap = colDataTypeMap;
	}

	private TBUtil getTBUtil() {
		if (tBUtil == null) {
			tBUtil = new TBUtil();
		}
		return tBUtil;
	}

	private CommDMO getCommDMO() {
		if (commDMO == null) {
			commDMO = new CommDMO();
		}
		return commDMO;
	}
}
/**************************************************************************
 * $RCSfile: GetColValue2.java,v $  $Revision: 1.8 $  $Date: 2012/09/14 09:22:56 $
 *
 * $Log: GetColValue2.java,v $
 * Revision 1.8  2012/09/14 09:22:56  xch123
 * �ʴ��ֳ�����ͳһ�޸�
 *
 * Revision 1.1  2012/08/28 09:40:54  Administrator
 * *** empty log message ***
 *
 * Revision 1.7  2012/02/09 13:13:51  sunfujun
 * *** empty log message ***
 *
 * Revision 1.6  2011/10/10 06:31:43  wanggang
 * restore
 *
 * Revision 1.4  2010/10/29 05:21:03  xch123
 * *** empty log message ***
 *
 * Revision 1.3  2010/08/23 09:32:32  wanglei
 * *** empty log message ***
 *
 * Revision 1.2  2010/08/11 08:44:00  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/17 10:23:08  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:31:56  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:01  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/03/11 12:32:06  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2010/03/11 12:19:08  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/03/08 15:04:51  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:01:53  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:50  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2009/10/14 08:05:41  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:29  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2009/02/09 09:41:05  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:26  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:09  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.6  2008/06/12 01:46:25  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.5  2008/05/13 14:49:59  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.4  2008/05/13 13:30:52  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2008/04/11 01:44:22  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/03/02 07:01:01  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:15  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:23  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/11/13 05:57:58  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:33  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:27  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/03/07 02:25:01  shxch
 * *** empty log message ***
 *
 * Revision 1.1  2007/03/07 02:04:18  shxch
 * *** empty log message ***
 *
 * Revision 1.3  2007/03/02 05:02:50  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 04:59:24  lujian
 * *** empty log message ***
 *
 *
 **************************************************************************/
