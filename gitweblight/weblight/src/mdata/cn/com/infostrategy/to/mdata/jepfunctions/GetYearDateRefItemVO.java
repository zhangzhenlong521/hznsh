package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 * 取得年度日期的参照数据对象!!!
 * 统计或查询经常会出现默认查询当前年度，故需要在查询默认条件中设置该RefItemVO，普通的RefItemVO没有HashVO，即不包括查询语句，故新增本公式【李春娟/2016-03-20】
 * 保证以前传一个四位数字的年度可用，又进行优化：
 * 如果没有传值，则取当前年度；
 * 如果传一个值，可以是一个日期，则取该日期所在年度，也可以是一个类型，则取当前日期所在类型的日期区间；
 * 如果传两个值，一个日期，一个类型（年，季，月，天），则取该日期所在类型的日期区间；【李春娟/2016-04-13】
 * @author lcj
 *
 */
public class GetYearDateRefItemVO extends PostfixMathCommand {
	private int callType = -1; //

	//UI传入的参数
	private BillPanel billPanel = null;

	/**
	 * 取得某一项的值!!
	 */
	public GetYearDateRefItemVO(BillPanel _billPanel) {
		numberOfParameters = 1; //一个参数，传入默认年度，如2016
		this.billPanel = _billPanel;
		callType = WLTConstants.JEPTYPE_UI; //客户端调用
	}

	/**
	 * 真正的逻辑执行!!
	 */
	public void run(Stack inStack) throws ParseException {
		checkStack(inStack); //检查堆栈...
		HashVO hvo = new HashVO(); // 
		String currdate = TBUtil.getTBUtil().getCurrDate();
		String date = currdate;
		String type = "年";
		if (inStack.size() == 1) {//如果只传一个值，则取当前年
			Object param_1 = inStack.pop(); //
			String str_1 = (String) param_1;
			if (str_1.length() >= 4) {//如果是传入了年或某个日期【李春娟/2016-04-13】
				date = str_1;
			} else if ("季".equals(str_1) || "月".equals(str_1) || "天".equals(str_1)) {//如果是传入了取值类型
				type = str_1;
			}
		} else if (inStack.size() == 2) {//如果两个传值，则一个是日期，一个是类型，如年，季，月，天
			Object param_1 = inStack.pop(); //
			date = (String) param_1;
			Object param_2 = inStack.pop(); //
			type = (String) param_2;
		}

		if ("季".equals(type)) {
			String str_1 = date.substring(0, 4); //
			String str_2 = date.substring(5, 7); //
			String season = getSeason(str_2);
			String str_id = str_1 + "年" + season + "季度;"; //
			String sb_querycondition = ""; //
			if ("1".equals(season)) {
				sb_querycondition = "( {itemkey}>='" + str_1 + "-01-01' and {itemkey}<='" + str_1 + "-03-31 24:00:00' )"; //
			} else if ("2".equals(season)) {
				sb_querycondition = "( {itemkey}>='" + str_1 + "-04-01' and {itemkey}<='" + str_1 + "-06-31 24:00:00' )"; //
			} else if ("3".equals(season)) {
				sb_querycondition = "( {itemkey}>='" + str_1 + "-07-01' and {itemkey}<='" + str_1 + "-09-31 24:00:00' )"; //
			} else if ("4".equals(season)) {
				sb_querycondition = "( {itemkey}>='" + str_1 + "-10-01' and {itemkey}<='" + str_1 + "-12-31 24:00:00' )"; //
			}
			hvo.setAttributeValue("querycondition", sb_querycondition); //
			inStack.push(new RefItemVO(str_id, "季;", str_id, hvo));
		} else if ("月".equals(type)) {
			String str_id = date.substring(0, 4) + "年" + date.substring(5, 7) + "月;"; ////
			String sb_querycondition = "( {itemkey}>='" + date.substring(0, 7) + "-01' and {itemkey}<='" + date.substring(0, 7) + "-31 24:00:00' )"; //
			hvo.setAttributeValue("querycondition", sb_querycondition); //
			inStack.push(new RefItemVO(str_id, "月;", str_id, hvo));
		} else if ("天".equals(type)) {
			String str_id = date + ";"; ////
			String sb_querycondition = "( {itemkey}>='" + date + "' and {itemkey}<='" + date + " 24:00:00' )"; //
			hvo.setAttributeValue("querycondition", sb_querycondition); //
			inStack.push(new RefItemVO(str_id, "天;", str_id, hvo));
		} else {//默认为年
			date = date.substring(0, 4);
			String str_id = date + "年;"; ////
			String sb_querycondition = "( {itemkey}>='" + date + "-01-01' and {itemkey}<='" + date + "-12-32' )"; //
			hvo.setAttributeValue("querycondition", sb_querycondition); //
			inStack.push(new RefItemVO(str_id, "年;", str_id, hvo));
		}
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
