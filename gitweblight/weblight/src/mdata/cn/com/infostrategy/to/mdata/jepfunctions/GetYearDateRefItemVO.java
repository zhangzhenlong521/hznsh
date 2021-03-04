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
 * ȡ��������ڵĲ������ݶ���!!!
 * ͳ�ƻ��ѯ���������Ĭ�ϲ�ѯ��ǰ��ȣ�����Ҫ�ڲ�ѯĬ�����������ø�RefItemVO����ͨ��RefItemVOû��HashVO������������ѯ��䣬����������ʽ�����/2016-03-20��
 * ��֤��ǰ��һ����λ���ֵ���ȿ��ã��ֽ����Ż���
 * ���û�д�ֵ����ȡ��ǰ��ȣ�
 * �����һ��ֵ��������һ�����ڣ���ȡ������������ȣ�Ҳ������һ�����ͣ���ȡ��ǰ�����������͵��������䣻
 * ���������ֵ��һ�����ڣ�һ�����ͣ��꣬�����£��죩����ȡ�������������͵��������䣻�����/2016-04-13��
 * @author lcj
 *
 */
public class GetYearDateRefItemVO extends PostfixMathCommand {
	private int callType = -1; //

	//UI����Ĳ���
	private BillPanel billPanel = null;

	/**
	 * ȡ��ĳһ���ֵ!!
	 */
	public GetYearDateRefItemVO(BillPanel _billPanel) {
		numberOfParameters = 1; //һ������������Ĭ����ȣ���2016
		this.billPanel = _billPanel;
		callType = WLTConstants.JEPTYPE_UI; //�ͻ��˵���
	}

	/**
	 * �������߼�ִ��!!
	 */
	public void run(Stack inStack) throws ParseException {
		checkStack(inStack); //����ջ...
		HashVO hvo = new HashVO(); // 
		String currdate = TBUtil.getTBUtil().getCurrDate();
		String date = currdate;
		String type = "��";
		if (inStack.size() == 1) {//���ֻ��һ��ֵ����ȡ��ǰ��
			Object param_1 = inStack.pop(); //
			String str_1 = (String) param_1;
			if (str_1.length() >= 4) {//����Ǵ��������ĳ�����ڡ����/2016-04-13��
				date = str_1;
			} else if ("��".equals(str_1) || "��".equals(str_1) || "��".equals(str_1)) {//����Ǵ�����ȡֵ����
				type = str_1;
			}
		} else if (inStack.size() == 2) {//���������ֵ����һ�������ڣ�һ�������ͣ����꣬�����£���
			Object param_1 = inStack.pop(); //
			date = (String) param_1;
			Object param_2 = inStack.pop(); //
			type = (String) param_2;
		}

		if ("��".equals(type)) {
			String str_1 = date.substring(0, 4); //
			String str_2 = date.substring(5, 7); //
			String season = getSeason(str_2);
			String str_id = str_1 + "��" + season + "����;"; //
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
			inStack.push(new RefItemVO(str_id, "��;", str_id, hvo));
		} else if ("��".equals(type)) {
			String str_id = date.substring(0, 4) + "��" + date.substring(5, 7) + "��;"; ////
			String sb_querycondition = "( {itemkey}>='" + date.substring(0, 7) + "-01' and {itemkey}<='" + date.substring(0, 7) + "-31 24:00:00' )"; //
			hvo.setAttributeValue("querycondition", sb_querycondition); //
			inStack.push(new RefItemVO(str_id, "��;", str_id, hvo));
		} else if ("��".equals(type)) {
			String str_id = date + ";"; ////
			String sb_querycondition = "( {itemkey}>='" + date + "' and {itemkey}<='" + date + " 24:00:00' )"; //
			hvo.setAttributeValue("querycondition", sb_querycondition); //
			inStack.push(new RefItemVO(str_id, "��;", str_id, hvo));
		} else {//Ĭ��Ϊ��
			date = date.substring(0, 4);
			String str_id = date + "��;"; ////
			String sb_querycondition = "( {itemkey}>='" + date + "-01-01' and {itemkey}<='" + date + "-12-32' )"; //
			hvo.setAttributeValue("querycondition", sb_querycondition); //
			inStack.push(new RefItemVO(str_id, "��;", str_id, hvo));
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
