/**************************************************************************
 * $RCSfile: GetCurrentDBDate.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/

package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.common.UIUtil;

//ȡ��������ʱ��,��ǰ��ȡ����ҽ��,����һֱ��getDBDate
public class GetCurrentDBDate extends PostfixMathCommand {

	private int li_type = -1;
	private TBUtil tbUtil = null; //

	private GetCurrentDBDate() {// �����������Ҫ�У�����TNND�����˰��죬����̫��ˬ��.
		numberOfParameters = 0;
	}

	public GetCurrentDBDate(int _type) {
		numberOfParameters = 0;
		li_type = _type;
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		String str_currDate = ""; //
		try {
			if (li_type == WLTConstants.JEPTYPE_UI) { //����ǿͻ��˵��øù�ʽ!!��ҪԶ�̵���!!
				str_currDate = UIUtil.getServerCurrDate(); //
			} else if (li_type == WLTConstants.JEPTYPE_BS) { //����Ƿ������˵��øù�ʽ!!
				str_currDate = getTBUtil().getCurrDate(); //
			} else {
				str_currDate = getTBUtil().getCurrDate(); //
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		inStack.push(str_currDate); //
	}

	private TBUtil getTBUtil() {
		if (tbUtil != null) {
			return tbUtil; //
		}
		tbUtil = new TBUtil(); //
		return tbUtil; //
	}
}
