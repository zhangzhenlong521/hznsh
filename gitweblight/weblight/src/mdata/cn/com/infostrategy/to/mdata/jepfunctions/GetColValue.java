/**************************************************************************
 * $RCSfile: GetColValue.java,v $  $Revision: 1.11 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/

package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.HashMap;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.sysapp.ServerCacheDataFactory;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTHashMap;
import cn.com.infostrategy.ui.common.UIUtil;

/**
 * �����õ�һ����ʽ֮һ,������ID����һ������ȡName!!!!
 * @author xch
 *
 */
public class GetColValue extends PostfixMathCommand {

	private int li_type = -1;

	private HashMap cacheMap = new WLTHashMap(); //Ϊ���������,�����������Map

	public GetColValue() {
		//System.out.println("��������[GetColValue]"); //
		numberOfParameters = 4;
	}

	public GetColValue(int _type) {
		//System.out.println("��������[GetColValue]"); //
		numberOfParameters = 4;
		li_type = _type;
	}

	public void run(Stack inStack) throws ParseException {
		try {
			//checkStack(inStack);
			Object param_1 = inStack.pop();
			Object param_2 = inStack.pop();
			Object param_3 = inStack.pop();
			Object param_4 = inStack.pop();

			String str_table_name = (String) param_4; //����
			String str_returnfieldname = (String) param_3; //��Ҫȡ��������
			String str_wherefieldname = (String) param_2;
			String str_wherefieldvalue = (String) param_1;

			if (str_wherefieldvalue == null || str_wherefieldvalue.trim().equals("") || str_wherefieldvalue.trim().toLowerCase().equals("null")) {
				inStack.push(""); //���whereֵ��null,�Ͳ�ȡ���ݿ���!!ֱ������ֵ,����Ч�ʻ�����!!
			} else {
				if (li_type == WLTConstants.JEPTYPE_UI) { //
					String str_sql_1 = "select " + str_returnfieldname + " from " + str_table_name + " where " + str_wherefieldname + "='" + str_wherefieldvalue + "'"; //
					HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, str_sql_1); //
					if (hvs.length > 0) { //���ȡ������
						inStack.push(hvs[0].getStringValue(0)); //
					} else {
						inStack.push(""); //
					}
				} else { //����Ƿ�������
					if (str_table_name.equalsIgnoreCase("pub_corp_dept")) { //����ǻ��������Ա,���⴦��,��ȡ���ݿ�,ֱ�Ӵӷ������˻���ȡ,���ܹ�������!!�������������ڴ����!!
						//����ǰ�ӻ���ȡ������ѭ������,����Idȡ��Name��,���ܽϵ�,�����ĳɴ洢��HashMap��,���ܴ�Ϊ���!��������ʵ֤��,�ӹ�ϣ��������һ���ؼ�key��ֵ,���ܱ�����һ��һά�����б���Ѱ��Ҫ��Ķ�!!!
						String str_mapkey = "$pub_corp_dept" + str_wherefieldname.toLowerCase() + "_" + str_returnfieldname.toLowerCase(); //
						if (!cacheMap.containsKey(str_mapkey)) { //����Ѱ���
							HashMap map_id_name = new HashMap(); //
							HashVO[] hvs_cacheCorp = ServerCacheDataFactory.getInstance().getCorpCacheDataByAutoCreate(); //Ҫ��ɹ�ϣ��!!
							for (int i = 0; i < hvs_cacheCorp.length; i++) {
								map_id_name.put(hvs_cacheCorp[i].getStringValue(str_wherefieldname), hvs_cacheCorp[i].getStringValue(str_returnfieldname)); ////
							}
							cacheMap.put(str_mapkey, map_id_name); //
						}
						HashMap map_id_name = (HashMap) cacheMap.get(str_mapkey); //
						inStack.push((String) map_id_name.get(str_wherefieldvalue)); //����ʵ�ʵ�����ֵ,ȡ��Nameֵ!!
					} else if (str_table_name.equalsIgnoreCase("pub_user")) {
						String str_mapkey = "$pub_user_" + str_wherefieldname.toLowerCase() + "_" + str_returnfieldname.toLowerCase(); //
						if (!cacheMap.containsKey(str_mapkey)) { //����Ѱ���
							HashMap map_id_name = new HashMap(); //
							HashVO[] hvs_cacheUsers = ServerCacheDataFactory.getInstance().getUserCacheDataByAutoCreate(); //Ҫ��ɹ�ϣ��!!
							for (int i = 0; i < hvs_cacheUsers.length; i++) {
								map_id_name.put(hvs_cacheUsers[i].getStringValue(str_wherefieldname), hvs_cacheUsers[i].getStringValue(str_returnfieldname)); ////
							}
							cacheMap.put(str_mapkey, map_id_name); //
						}

						HashMap map_id_name = (HashMap) cacheMap.get(str_mapkey); //
						inStack.push((String) map_id_name.get(str_wherefieldvalue)); //����ʵ�ʵ�����ֵ,ȡ��Nameֵ!!
					} else {
						String str_sql_1 = "select " + str_wherefieldname + "," + str_returnfieldname + " from " + str_table_name; //
						if (!cacheMap.containsKey(str_sql_1)) {
							cacheMap.put(str_sql_1, getData(str_sql_1)); //
						}
						HashMap dataMap = (HashMap) cacheMap.get(str_sql_1); //�ӻ�����ȡ������!
						inStack.push((String) dataMap.get(str_wherefieldvalue)); //
					}
				}
			}
		} catch (Throwable ex) {
			TBUtil.printStackTrace(ex); //
			inStack.push("");
		}
	}

	private HashMap getData(String _sql) throws Exception {
		String[][] str_data = null;
		if (li_type == WLTConstants.JEPTYPE_UI) { //����ǿͻ��˵���
			str_data = UIUtil.getStringArrayByDS(null, _sql);
		} else if (li_type == WLTConstants.JEPTYPE_BS) { //�����Server�˵���
			str_data = ServerEnvironment.getCommDMO().getStringArrayByDS(null, _sql);
		}

		HashMap map = new HashMap(); //
		if (str_data != null) {
			for (int i = 0; i < str_data.length; i++) {
				map.put(str_data[i][0], str_data[i][1]); //��1��,��2��..
			}
		}
		return map; //
	}

}
/**************************************************************************
 * $RCSfile: GetColValue.java,v $  $Revision: 1.11 $  $Date: 2012/09/14 09:22:56 $
 *
 * $Log: GetColValue.java,v $
 * Revision 1.11  2012/09/14 09:22:56  xch123
 * �ʴ��ֳ�����ͳһ�޸�
 *
 * Revision 1.1  2012/08/28 09:40:54  Administrator
 * *** empty log message ***
 *
 * Revision 1.10  2011/10/10 06:31:42  wanggang
 * restore
 *
 * Revision 1.8  2010/08/17 09:23:50  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.7  2010/08/17 09:23:04  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.6  2010/08/17 09:20:20  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.5  2010/06/07 13:01:11  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.4  2010/06/07 09:53:43  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/06/07 09:10:35  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2010/06/04 07:30:35  xuchanghua
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
 * Revision 1.4  2010/03/08 15:04:51  xuchanghua
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
