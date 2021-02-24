package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.HashMap;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.bs.sysapp.ServerCacheDataFactory;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 *��һ����ѡ�����е�idת��������,�������ݿ��д洢���ǡ�15;17;19��,��Ҫת���ɡ�����;����;���塿,Ϊ�˼��ٱ�(��һ��������)���������ط����ö�ѡ����,������ʱҲ����Java�м��㣬������ʹ��SQL���㣡��������Ӧ�ú��м�ֵ
 *��ʵ���˶�ѡ������,��ʱͨ�����ع�ʽ������һ���ӱ��������֮��������������������,�Ѹ��м�¼��ĳ���ֶ�ƴ��һ���ԷֺŸ������ַ���Ҳ�Ǿ���ʹ�õ�һ��Ӧ��!!
 *����һ����Ա��,�����Ҽ�һ���ֶν�"����Ա�Ľ�ɫ",Ȼ���Զ����ؽ�����˵����н�ɫȡ����ƴ��һ���ַ���,����:ϵͳ����Ա;���ɸ�;�Ϲ��;��!��ʵ�����൱�������ӱ�ֻ��������������ƴ��һ��������Ӧ��ֻ�������б�/��Ƭ��ѯ,�Ƿ����ֱ�ӷ�װ����һ�������ӱ��ʽ(�������ʽ)��ֵ����ȶ�ģ���
 * 
 */
public class GetMultiRefName extends PostfixMathCommand {

	private int callType = -1; //
	private BillPanel billPanel = null;

	//BS����Ĳ���
	private HashMap colDataTypeMap = null; //һ������!!
	private HashMap rowDataMap = null; //һ������!!

	/*-----------�������ӿ�ʼ-------------*/
	private HashMap cacheMap = new HashMap();

	/*-----------�������ӽ���-------------*/

	private cn.com.infostrategy.bs.common.CommDMO commDMO = null; //
	private TBUtil tbUtil = null; //

	public GetMultiRefName() {
		numberOfParameters = 4; //
	}

	/**
	 * ȡ��ĳһ���ֵ!!
	 */
	public GetMultiRefName(BillPanel _billPanel) {
		numberOfParameters = 4; //4������,��һ���Ǳ���,�ڶ�����Ҫ��ѯ������,�������ǹ���������,���ĸ��Ǵ���Ĺ���������ֵ
		this.billPanel = _billPanel;
		callType = WLTConstants.JEPTYPE_UI; //�ͻ��˵���
	}

	/**
	 * �ڷ������˵��õĹ��췽��
	 * @param dataMap
	 */
	public GetMultiRefName(HashMap _dataMap) {
		numberOfParameters = 4; //
		this.rowDataMap = _dataMap; //
		callType = WLTConstants.JEPTYPE_BS; //�ͻ��˵���
	}

	/**
	 * �������߼�ִ��!!
	 */
	public void run(Stack inStack) throws ParseException {
		//��ȡ�ò���!!
		try {
			checkStack(inStack);
			Object param_1 = inStack.pop();
			Object param_2 = inStack.pop();
			Object param_3 = inStack.pop();
			Object param_4 = inStack.pop();
			String str_tablename = (String) param_4; ////
			String str_selectlistname = (String) param_3; ////
			String str_unionlistname = (String) param_2; ////�����ֶ�,һ�����ӱ������,����ID
			String str_unionlistvalue = (String) param_1; ////����ı�����¼��ֵ!!(������ֶ�ֵ�ͺ���!)

			if (str_unionlistvalue == null || str_unionlistvalue.trim().equals("")) { //�������ĸ��ֶε�ֵΪ��,����Ҫ����ֱ�ӷ���
				inStack.push(""); //
				return;
			}

			String str_inCon = getTBUtil().getInCondition(str_unionlistvalue); //
			StringBuilder sb_return = new StringBuilder(); //
			HashMap map_id_name = null;
			if (callType == WLTConstants.JEPTYPE_UI) { //�����UI����ܺð�
				String str_sql = "select " + str_unionlistname + "," + str_selectlistname + " from " + str_tablename + " where " + str_unionlistname + " in (" + str_inCon + ")"; //
				map_id_name = UIUtil.getHashMapBySQLByDS(null, str_sql); //

			} else if (callType == WLTConstants.JEPTYPE_BS) { //�����BS��,��������,��һ���ӽ������ӱ�����ݶ�ȡ����,Ȼ�����м����ȡ!!�����ᱣֻ֤����һ�����ݿ�,����������ʱҲ����!
				if (str_tablename.equalsIgnoreCase("pub_corp_dept")) {//�������,�ӻ���ȡ!!
					HashVO[] hvs_corpCache = ServerCacheDataFactory.getInstance().getCorpCacheDataByAutoCreate(); //������������
					map_id_name = getTBUtil().getHashMapFromHashVOs(hvs_corpCache, str_unionlistname, str_selectlistname); //��id,name�������Ϊһ����ϣ��,�Ա��ѯ����!!
				} else {
					String str_sql = "select " + str_unionlistname + "," + str_selectlistname + " from " + str_tablename + " where " + str_unionlistname + " in (" + str_inCon + ")"; //SQL ����鴦���е�Ӧ�üӸ�������UI��һ�� ��Ϣ���ľͼ������ݾͺ������������ԭ��/sunfujun/20121122
					map_id_name = getCacheMapData(str_sql); //
				}
			}
			if (map_id_name == null || map_id_name.size() == 0) {
				inStack.push(""); //
				return;
			}
			//����Ҫidֵ������һһ��Ӧ��sql���ÿ��ƣ��������ﴦ��һ�¡����/2016-03-21��
			String[] str_ids = getTBUtil().split(str_unionlistvalue, ";");
			for (int i = 0; i < str_ids.length; i++) {
				String str_name = (String) map_id_name.get(str_ids[i]); //
				str_name = (str_name == null ? "" : str_name); ////
				sb_return.append(str_name + ";"); //
			}
			inStack.push(sb_return.toString()); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			inStack.push(""); //
		}
	}

	private cn.com.infostrategy.bs.common.CommDMO getCommDMO() {
		if (commDMO == null) {
			commDMO = new cn.com.infostrategy.bs.common.CommDMO();
		}
		return commDMO;
	}

	private HashMap getCacheMapData(String _sql) throws Exception {
		if (cacheMap.containsKey(_sql)) {
			return (HashMap) cacheMap.get(_sql); //
		} else {
			HashMap mapData = getCommDMO().getHashMapBySQLByDS(null, _sql); //
			cacheMap.put(_sql, mapData); //
			return mapData;
		}
	}

	private TBUtil getTBUtil() {
		if (tbUtil == null) {
			tbUtil = new TBUtil();
		}
		return tbUtil;
	}

	public void setRowDataMap(HashMap rowDataMap) {
		this.rowDataMap = rowDataMap;
	}

	public void setColDataTypeMap(HashMap colDataTypeMap) {
		this.colDataTypeMap = colDataTypeMap;
	}

}
