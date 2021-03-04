/**************************************************************************
 * $RCSfile: FillBlank.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/

package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * 在招行项目中遇到SQLServer建全文检索时,中文分词有问题! 所以后来想办法在原来的内容上加空格!
 * 这就需要一个编辑公式来将原来的"制度名称"填充上空格,保存到另一个字段中去,这就需要该公式了!
 * @author xch
 *
 */
public class FillBlank extends PostfixMathCommand {

	public FillBlank() {
		numberOfParameters = 1; //只有一个参数
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		String str = (String) inStack.pop(); //
		if (str == null || str.trim().equals("")) {
			inStack.push(""); //
			return;
		}
		StringBuilder sb_text = new StringBuilder(); //
		for (int i = 0; i < str.length(); i++) { //遍历!
			sb_text.append(str.charAt(i)); //
			sb_text.append(" "); //加上空格!,以后这个参数也可配置才好!
		}
		inStack.push(sb_text.toString()); //
	}
}
/**************************************************************************
 * $RCSfile: FillBlank.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 *
 * $Log: FillBlank.java,v $
 * Revision 1.4  2012/09/14 09:22:56  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:40:53  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:43  wanggang
 * restore
 *
 * Revision 1.1  2010/12/28 10:30:11  xch123
 * 12月28日提交
 *
 * Revision 1.1  2010/05/17 10:23:08  xuchanghua
 * *** empty log message ***
 *
**************************************************************************/
