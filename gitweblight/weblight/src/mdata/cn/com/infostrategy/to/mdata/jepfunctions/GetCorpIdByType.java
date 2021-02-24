/**************************************************************************
 * $RCSfile: GetCorpIdByType.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/

package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.bs.sysapp.ServerCacheDataFactory;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.common.UIUtil;

/**
 * 机构机构ID取得机构类型!!!
 * @author Administrator
 *
 */
public class GetCorpIdByType extends PostfixMathCommand {

	int li_type = -1;

	public GetCorpIdByType(int _type) {
		numberOfParameters = 1; //
		li_type = _type;
	}

	public void run(Stack _inStack) throws ParseException {
		checkStack(_inStack); //
		try {
			String str_corptype = (String) _inStack.pop(); //
			if (li_type == WLTConstants.JEPTYPE_UI) {
				String str_corpTYpe = UIUtil.getStringValueByDS(null, "select id from pub_corp_dept where corptype='" + str_corptype + "'"); //找到机构类型!找到一个就够了,如果有多个的话就取第一个!
				_inStack.push(str_corpTYpe); //返回机构类型
				return;
			} else if (li_type == WLTConstants.JEPTYPE_BS) { //如果是BS端,则必须从缓存取!
				HashVO[] hvs = ServerCacheDataFactory.getInstance().getCorpCacheDataByAutoCreate(); //从缓存中取
				for (int i = 0; i < hvs.length; i++) {
					if (hvs[i].getStringValue("corptype")!=null && hvs[i].getStringValue("corptype").equals(str_corptype)) { //如果某个机构的类型等于指定的,则返回其id
						_inStack.push(hvs[i].getStringValue("id")); //返回id...
						return;
					}
				}
			}
			_inStack.push("-99999");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}

/**************************************************************************
 * $RCSfile: GetCorpIdByType.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 *
 * $Log: GetCorpIdByType.java,v $
 * Revision 1.4  2012/09/14 09:22:56  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:40:54  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:42  wanggang
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
 * Revision 1.3  2010/02/08 11:01:53  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:50  xuchanghua
 * *** empty log message ***
 *
 *
 **************************************************************************/
