/**************************************************************************
 * $RCSfile: GetSQLValue.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/

package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.HashMap;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.common.UIUtil;

public class GetSQLValue extends PostfixMathCommand {

	private int li_type = -1;

	private HashMap cacheMap = new HashMap(); //为了提高性能,用来做缓存的Map

	public GetSQLValue() {
		//System.out.println("创建函数[GetColValue]"); //
		numberOfParameters = 3;
	}

	public GetSQLValue(int _type) {
		//System.out.println("创建函数[GetColValue]"); //
		numberOfParameters = 3;
		li_type = _type;
	}

	public void run(Stack inStack) throws ParseException {
		try {
			Object param_0 = inStack.pop();
			String str_sql0 = (String) param_0; //SQL

			Object param_1 = inStack.pop();
			String str_sql = (String) param_1; //SQL

			Object param_2 = inStack.pop();
			String str_sql1 = (String) param_2; //SQL

			if (li_type == WLTConstants.JEPTYPE_UI) {
				if (str_sql1.equals("字符串")) {
					String result = UIUtil.getStringValueByDS(null, str_sql); //
					if (result != null) { //如果取到数据
						inStack.push(result); //
					} else {
						inStack.push(""); //
					}
				} else if (str_sql1.equals("数组")) {
					HashVO[] hashvo = null;
					if (str_sql0 != null && !str_sql0.equals("null")) {
						String newsql1 = UIUtil.getInCondition(null, str_sql0); //
						hashvo = UIUtil.getHashVoArrayByDS(null, str_sql + "(" + newsql1 + ")"); //
					} else {
						hashvo = UIUtil.getHashVoArrayByDS(null, str_sql); //
					}
					if (hashvo.length > 0) { //如果取到数据
						String result = "";
						for (int i = 0; i < hashvo.length; i++) {
							if (i == hashvo.length - 1) {
								result += hashvo[i].getStringValue(0);
							} else {
								result += hashvo[i].getStringValue(0) + ",";
							}
						}
						inStack.push(result); //
					} else {
						inStack.push(""); //
					}
				}

			} else {
				String result = "";
				if (str_sql1.equals("字符串")) {
					if (!cacheMap.containsKey(str_sql)) {
						cacheMap.put(str_sql, getData(str_sql)); //
					}
					result = (String) cacheMap.get(str_sql); //从缓存中取得数据!	
					if (result.indexOf(',') != -1) {
						result = result.substring(0, result.indexOf(','));
					}
				} else if (str_sql1.equals("数组")) {
					if (str_sql0 != null && !str_sql0.equals("null")) {
						String newsql1 = UIUtil.getInCondition(null, str_sql0); //
						String str_sql2 = str_sql + "(" + newsql1 + ")";
						if (!cacheMap.containsKey(str_sql2)) {
							result = getData(str_sql2);
							cacheMap.put(str_sql2, result); //
						}
					} else {
						if (!cacheMap.containsKey(str_sql)) {
							result = getData(str_sql);
							cacheMap.put(str_sql, result); //
						}
						
					}
				}
				inStack.push(result); //
			}
		} catch (Throwable ex) {
			ex.printStackTrace();
			inStack.push("");
		}
	}

	private String getData(String _sql) throws Exception {
		String[] str_data = null;
		if (li_type == WLTConstants.JEPTYPE_UI) { //如果是客户端调用
			str_data = UIUtil.getStringArrayFirstColByDS(null, _sql);
		} else if (li_type == WLTConstants.JEPTYPE_BS) { //如果是Server端调用
			str_data = ServerEnvironment.getCommDMO().getStringArrayFirstColByDS(null, _sql);
		}
		if (str_data == null) {
			return "";
		}
		String result = "";
		for (int i = 0; i < str_data.length; i++) {
			result += str_data[i];
			if (i < str_data.length - 1) {
				result += ",";
			}
		}
		return result; //
	}

}
/**************************************************************************
 * $RCSfile: GetSQLValue.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 *
 * $Log: GetSQLValue.java,v $
 * Revision 1.4  2012/09/14 09:22:56  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:40:54  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:43  wanggang
 * restore
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
 * Revision 1.5  2010/02/26 05:23:19  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.4  2010/02/24 08:42:11  sunfujun
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:01:54  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:50  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:30  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/11/11 08:49:16  wangjian
 * *** empty log message ***
 *
 * Revision 1.1  2008/11/11 07:29:29  wangjian
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
