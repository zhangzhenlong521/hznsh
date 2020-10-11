package cn.com.infostrategy.bs.mdata.dataaccessfuns;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.mdata.DataAccessFunctionIFC;
import cn.com.infostrategy.to.common.HashVO;

/**
 * ȡ��ĳ�ű��ĳ���ֶε�ֵ,�������ڸ����ӱ��idȡ���ӱ��name
 * @author xch
 *
 */
public class GetColValueByItemId implements DataAccessFunctionIFC {

	private String str_tablename = null; //����
	private String str_returnField = null; //���ص��ֶ���
	private String str_idField = null; //id�ֶε�ֵ
	private String str_idFieldValue = null; //id�ֶε�ֵ
	private String str_whereCondition = null; //��������

	private HashMap map_data = null; //

	public GetColValueByItemId() {

	}

	public String getFunValue(String[] _pars) {
		if (map_data == null) { //����ǵ�һ��ȡ��,����Ҫ���л��洦��
			map_data = new HashMap(); //

			str_tablename = _pars[0];
			str_returnField = _pars[1];
			str_idField = _pars[2];
			str_idFieldValue = _pars[3];
			str_whereCondition = _pars[4];

			String str_sql = "select " + str_idField + "," + str_returnField + " from " + str_tablename; //
			if (str_whereCondition != null && !str_whereCondition.trim().equals("")) {//���������Ϊ��
				str_sql = str_sql + " where " + str_whereCondition;
			}
			
			try {
				HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, str_sql); //
				for (int i = 0; i < hvs.length; i++) {
					map_data.put(hvs[i].getStringValue(str_idField), hvs[i].getStringValue(str_returnField)); //��Ϊ��ֵע��
				}
			} catch (Exception e) {
			}
		}

		
		return null;
	}

}
