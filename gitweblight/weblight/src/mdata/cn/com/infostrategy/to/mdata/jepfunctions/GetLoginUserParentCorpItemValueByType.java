/**************************************************************************
 * $RCSfile: GetLoginUserParentCorpItemValueByType.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/

package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.common.UIUtil;

/**
 * 取得登录人员的所有上级机构中类型为指定类型的机构ID,比如取得本人的所有上级机构中类型为"一级分行"的机构!!!
 * 目前的做法是拼出机构树,然后根据树型关系取得路径上的结点!!
 * 考虑到以后会有事业部等多头领导的关系,应该要从机构表中的parentcorpids字段中取!!!
 * @author Administrator
 *
 */
public class GetLoginUserParentCorpItemValueByType extends PostfixMathCommand {

	int li_type = -1;

	public GetLoginUserParentCorpItemValueByType(int _type) {
		numberOfParameters = -1; //
		li_type = _type;
	}

	public void run(Stack _inStack) throws ParseException {
		checkStack(_inStack); //
		String str_parentCorpType = null; //父亲机构的类型!!!
		String str_nvlCorpType = null; //当第一个没取到到,第二个直接从全行取的办法!!,用于有时判断直接根据登录人员一次自动算出是分行还是总行!!!
		String str_returnFieldName = "id"; //返回的字段名,默认是id

		if (curNumberOfParameters == 1) {
			str_parentCorpType = (String) _inStack.pop(); //机构类型!!!
		} else if (curNumberOfParameters == 2) { //如果是两个
			str_returnFieldName = (String) _inStack.pop(); //返回的字段名!!
			str_parentCorpType = (String) _inStack.pop(); //机构类型!!!
		} else if (curNumberOfParameters == 3) {
			str_returnFieldName = (String) _inStack.pop(); //返回的字段名!!
			str_nvlCorpType = (String) _inStack.pop(); //第一个没取到时第二个取的办法!!
			str_parentCorpType = (String) _inStack.pop(); //机构类型!!!
		}

		try {
			if (li_type == WLTConstants.JEPTYPE_UI) { //如果是UI端,则进行两次远程访问,性能稍差点,以后要改成考虑建一个远程方法,在服务器端一次搞定返回!! 但因为返回的数据量少，目前的机制也能承受!!
				String str_corpID = UIUtil.getLoginUserParentCorpItemValueByType(str_parentCorpType, str_nvlCorpType, str_returnFieldName); //一次永远调用!!!
				if (str_corpID != null && !str_corpID.trim().equals("")) { //如果不为空则插入!!!
					_inStack.push(str_corpID); ////
				} else {
					_inStack.push("-99999");
				}
			} else if (li_type == WLTConstants.JEPTYPE_BS) { //如果是BS端,则必须从缓存取!
				String str_corpID = new cn.com.infostrategy.bs.sysapp.SysAppDMO().getLoginUserParentCorpItemValueByType(str_parentCorpType, str_nvlCorpType, str_returnFieldName); //
				if (str_corpID != null && !str_corpID.trim().equals("")) { //如果不为空则插入!!!
					_inStack.push(str_corpID); ////
				} else {
					_inStack.push("-99999");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}

/**************************************************************************
 * $RCSfile: GetLoginUserParentCorpItemValueByType.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 *
 * $Log: GetLoginUserParentCorpItemValueByType.java,v $
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
 * Revision 1.3  2010/02/08 11:01:53  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:50  xuchanghua
 * *** empty log message ***
 *
 *
 **************************************************************************/
