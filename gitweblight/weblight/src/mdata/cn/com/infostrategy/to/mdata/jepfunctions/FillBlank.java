/**************************************************************************
 * $RCSfile: FillBlank.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/

package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * ��������Ŀ������SQLServer��ȫ�ļ���ʱ,���ķִ�������! ���Ժ�����취��ԭ���������ϼӿո�!
 * �����Ҫһ���༭��ʽ����ԭ����"�ƶ�����"����Ͽո�,���浽��һ���ֶ���ȥ,�����Ҫ�ù�ʽ��!
 * @author xch
 *
 */
public class FillBlank extends PostfixMathCommand {

	public FillBlank() {
		numberOfParameters = 1; //ֻ��һ������
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		String str = (String) inStack.pop(); //
		if (str == null || str.trim().equals("")) {
			inStack.push(""); //
			return;
		}
		StringBuilder sb_text = new StringBuilder(); //
		for (int i = 0; i < str.length(); i++) { //����!
			sb_text.append(str.charAt(i)); //
			sb_text.append(" "); //���Ͽո�!,�Ժ��������Ҳ�����òź�!
		}
		inStack.push(sb_text.toString()); //
	}
}
/**************************************************************************
 * $RCSfile: FillBlank.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 *
 * $Log: FillBlank.java,v $
 * Revision 1.4  2012/09/14 09:22:56  xch123
 * �ʴ��ֳ�����ͳһ�޸�
 *
 * Revision 1.1  2012/08/28 09:40:53  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:43  wanggang
 * restore
 *
 * Revision 1.1  2010/12/28 10:30:11  xch123
 * 12��28���ύ
 *
 * Revision 1.1  2010/05/17 10:23:08  xuchanghua
 * *** empty log message ***
 *
**************************************************************************/
