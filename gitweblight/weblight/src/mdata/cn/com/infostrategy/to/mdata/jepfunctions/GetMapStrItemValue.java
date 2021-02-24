/**************************************************************************
 * $RCSfile: GetMapStrItemValue.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/

package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.HashMap;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.TBUtil;

/**
 * 从一个哈希表类型的字符串中找到key等于指定值的value!
 * 所谓哈希表类型字符就是【par1=Y;par2=aaa;par3=bbb;par4=1,2,3】的样子,即先是以分号分隔,然后再以等号分隔,等号左边是key,右边是Value!
 * @author xch
 *
 */
public class GetMapStrItemValue extends PostfixMathCommand {

	private TBUtil tbUtil = new TBUtil(); //

	public GetMapStrItemValue() {
		numberOfParameters = 2;
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack); //
		String str_key = (String) inStack.pop(); //
		String str_allText = (String) inStack.pop(); //所有的文本!!!
		HashMap map = tbUtil.parseStrAsMap(str_allText); //将字符串转换成文本!!
		String str_value = (String) map.get(str_key); //
		inStack.push(str_value == null ? "" : str_value); //
	}

}
