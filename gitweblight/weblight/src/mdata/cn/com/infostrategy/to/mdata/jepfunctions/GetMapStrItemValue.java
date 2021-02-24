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
 * ��һ����ϣ�����͵��ַ������ҵ�key����ָ��ֵ��value!
 * ��ν��ϣ�������ַ����ǡ�par1=Y;par2=aaa;par3=bbb;par4=1,2,3��������,�������Էֺŷָ�,Ȼ�����ԵȺŷָ�,�Ⱥ������key,�ұ���Value!
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
		String str_allText = (String) inStack.pop(); //���е��ı�!!!
		HashMap map = tbUtil.parseStrAsMap(str_allText); //���ַ���ת�����ı�!!
		String str_value = (String) map.get(str_key); //
		inStack.push(str_value == null ? "" : str_value); //
	}

}
