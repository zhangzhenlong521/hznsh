/**************************************************************************
 * $RCSfile: GetCaseValue.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/

package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.HashMap;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.TBUtil;

/**
 * 以前都是if/else的判断方式,比较麻烦,现在使用getCaseValue会简单得多!!
 * setItemValue("status",getCaseValue(getItemValue("status"),"RUN,审批中...,END=流程已结束...,null,流程未启动"))
 * @author xch
 *
 */
public class GetCaseValue extends PostfixMathCommand {

	TBUtil tbUtil = null;

	public GetCaseValue() {
		numberOfParameters = 2;
	}

	public void run(Stack inStack) throws ParseException {
		try {
			checkStack(inStack);
			String str_caseitems = (String) inStack.pop(); //第二个参数,即清单
			String str_realitem = (String) inStack.pop(); //第一个参数,即实际值
			String[] str_items = getTBUtil().split(str_caseitems, ","); //比如有8个
			HashMap maps = new HashMap(); //
			for (int i = 0; i < (str_items.length / 2); i++) { //奇偶数相隔,遍历送入一个哈希表!!
				maps.put(str_items[i * 2], str_items[i * 2 + 1]); //
			}
			if (maps.containsKey(str_realitem)) { //如果定义了,则返回转换后的,
				inStack.push((String) maps.get(str_realitem)); //
			} else { //如果没定义则返回原来的!!
				inStack.push(str_realitem); //
			}
		} catch (Exception ex) {
			getTBUtil().printStackTrace(ex);
		}
	}

	private TBUtil getTBUtil() {
		if (tbUtil == null) {
			return new TBUtil(); //
		}
		return tbUtil;
	}
}
