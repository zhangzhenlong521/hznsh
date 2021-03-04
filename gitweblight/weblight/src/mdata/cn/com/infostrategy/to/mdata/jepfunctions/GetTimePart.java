/**************************************************************************
 * $RCSfile: GetTimePart.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/

package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * 得到时间中的一部分,比如年度,季度,月份,天
 * @author xch
 *
 */
public class GetTimePart extends PostfixMathCommand {

	public static int YEAY = 1;
	public static int SEASON = 2;
	public static int MONTH = 3;
	public static int DATE = 4;

	int li_type = 1;

	private GetTimePart() { //这个参数必须要有,TNND，搞了半天，真是太不爽了.
		numberOfParameters = 1;
	}

	public GetTimePart(int _type) { //
		numberOfParameters = 1;
		li_type = _type; //
	}

	public void run(Stack _inStack) throws ParseException {
		checkStack(_inStack); //
		String str_datetime = (String) _inStack.pop(); //
		if (str_datetime == null || str_datetime.trim().equals("")) {
			_inStack.push("");
			return; //
		}

		String str_value = null; //
		if (li_type == GetTimePart.YEAY) { //如果是年度.
			str_value = str_datetime.substring(0, 4); //
		} else if (li_type == GetTimePart.SEASON) {
			String str_1 = str_datetime.substring(0, 4); //
			String str_2 = str_datetime.substring(5, 7); //
			str_value = str_1 + "年" + getSeason(str_2) + "季度"; //
		} else if (li_type == GetTimePart.MONTH) {
			String str_1 = str_datetime.substring(0, 4); //
			String str_2 = str_datetime.substring(5, 7); //
			str_value = str_1 + "年" + str_2 + "月"; //
		} else if (li_type == GetTimePart.DATE) {
			str_value = str_datetime.substring(0, 10); //
		}

		if (str_value == null) {
			str_value = "";
		}
		_inStack.push(str_value); //
	}

	private String getSeason(String _month) {
		if (_month.equals("01") || _month.equals("02") || _month.equals("03")) {
			return "1";
		} else if (_month.equals("04") || _month.equals("05") || _month.equals("06")) {
			return "2";
		} else if (_month.equals("07") || _month.equals("08") || _month.equals("09")) {
			return "3";
		} else if (_month.equals("10") || _month.equals("11") || _month.equals("12")) {
			return "4";
		} else {
			return "";
		}
	}

}
