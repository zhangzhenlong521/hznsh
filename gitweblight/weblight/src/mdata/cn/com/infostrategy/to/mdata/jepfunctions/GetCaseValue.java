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
 * ��ǰ����if/else���жϷ�ʽ,�Ƚ��鷳,����ʹ��getCaseValue��򵥵ö�!!
 * setItemValue("status",getCaseValue(getItemValue("status"),"RUN,������...,END=�����ѽ���...,null,����δ����"))
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
			String str_caseitems = (String) inStack.pop(); //�ڶ�������,���嵥
			String str_realitem = (String) inStack.pop(); //��һ������,��ʵ��ֵ
			String[] str_items = getTBUtil().split(str_caseitems, ","); //������8��
			HashMap maps = new HashMap(); //
			for (int i = 0; i < (str_items.length / 2); i++) { //��ż�����,��������һ����ϣ��!!
				maps.put(str_items[i * 2], str_items[i * 2 + 1]); //
			}
			if (maps.containsKey(str_realitem)) { //���������,�򷵻�ת�����,
				inStack.push((String) maps.get(str_realitem)); //
			} else { //���û�����򷵻�ԭ����!!
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
