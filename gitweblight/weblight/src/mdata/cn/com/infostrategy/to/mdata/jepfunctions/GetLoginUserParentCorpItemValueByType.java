/**************************************************************************
 * $RCSfile: GetLoginUserParentCorpItemValueByType.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/

package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.common.UIUtil;

/**
 * ȡ�õ�¼��Ա�������ϼ�����������Ϊָ�����͵Ļ���ID,����ȡ�ñ��˵������ϼ�����������Ϊ"һ������"�Ļ���!!!
 * Ŀǰ��������ƴ��������,Ȼ��������͹�ϵȡ��·���ϵĽ��!!
 * ���ǵ��Ժ������ҵ���ȶ�ͷ�쵼�Ĺ�ϵ,Ӧ��Ҫ�ӻ������е�parentcorpids�ֶ���ȡ!!!
 * @author Administrator
 *
 */
public class GetLoginUserParentCorpItemValueByType extends PostfixMathCommand {

	int li_type = -1;

	public GetLoginUserParentCorpItemValueByType(int _type) {
		numberOfParameters = -1; //
		li_type = _type;
	}

	public void run(Stack _inStack) throws ParseException {
		checkStack(_inStack); //
		String str_parentCorpType = null; //���׻���������!!!
		String str_nvlCorpType = null; //����һ��ûȡ����,�ڶ���ֱ�Ӵ�ȫ��ȡ�İ취!!,������ʱ�ж�ֱ�Ӹ��ݵ�¼��Աһ���Զ�����Ƿ��л�������!!!
		String str_returnFieldName = "id"; //���ص��ֶ���,Ĭ����id

		if (curNumberOfParameters == 1) {
			str_parentCorpType = (String) _inStack.pop(); //��������!!!
		} else if (curNumberOfParameters == 2) { //���������
			str_returnFieldName = (String) _inStack.pop(); //���ص��ֶ���!!
			str_parentCorpType = (String) _inStack.pop(); //��������!!!
		} else if (curNumberOfParameters == 3) {
			str_returnFieldName = (String) _inStack.pop(); //���ص��ֶ���!!
			str_nvlCorpType = (String) _inStack.pop(); //��һ��ûȡ��ʱ�ڶ���ȡ�İ취!!
			str_parentCorpType = (String) _inStack.pop(); //��������!!!
		}

		try {
			if (li_type == WLTConstants.JEPTYPE_UI) { //�����UI��,���������Զ�̷���,�����Բ��,�Ժ�Ҫ�ĳɿ��ǽ�һ��Զ�̷���,�ڷ�������һ�θ㶨����!! ����Ϊ���ص��������٣�Ŀǰ�Ļ���Ҳ�ܳ���!!
				String str_corpID = UIUtil.getLoginUserParentCorpItemValueByType(str_parentCorpType, str_nvlCorpType, str_returnFieldName); //һ����Զ����!!!
				if (str_corpID != null && !str_corpID.trim().equals("")) { //�����Ϊ�������!!!
					_inStack.push(str_corpID); ////
				} else {
					_inStack.push("-99999");
				}
			} else if (li_type == WLTConstants.JEPTYPE_BS) { //�����BS��,�����ӻ���ȡ!
				String str_corpID = new cn.com.infostrategy.bs.sysapp.SysAppDMO().getLoginUserParentCorpItemValueByType(str_parentCorpType, str_nvlCorpType, str_returnFieldName); //
				if (str_corpID != null && !str_corpID.trim().equals("")) { //�����Ϊ�������!!!
					_inStack.push(str_corpID); ////
				} else {
					_inStack.push("-99999");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}

/**************************************************************************
 * $RCSfile: GetLoginUserParentCorpItemValueByType.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 *
 * $Log: GetLoginUserParentCorpItemValueByType.java,v $
 * Revision 1.4  2012/09/14 09:22:56  xch123
 * �ʴ��ֳ�����ͳһ�޸�
 *
 * Revision 1.1  2012/08/28 09:40:54  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:43  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:23:08  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:31:56  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:01  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:01:53  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:50  xuchanghua
 * *** empty log message ***
 *
 *
 **************************************************************************/
