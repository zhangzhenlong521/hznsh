package cn.com.pushworld.salary.ui.posteval.p030;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.jepfunctions.IClassJepFormulaParseIFC;

public class ScoreRegisterFormula implements IClassJepFormulaParseIFC {

	/**
	 * Υ����ֵǼǵĿ۷����������ӱ���ǰ���õļ��ع�ʽ���£��������������ðѷ���Ҳ��ʾ��������д���ࡾ���/2013-06-06��
	 *setRefItemName("USERIDS",getMultiRefName("pub_user","name","id",replaceall(getSQLValue("����","select userid from score_user where id in("+replaceallandsubString(getItemValue("USERIDS"),"\;",",")+")","null"),",","\;")))
	 */

	public String getForMulaValue(String[] _pars) throws Exception {
		if (_pars == null || _pars.length == 0 || _pars[0] == null || _pars[0].trim().equals("")) {
			return "";
		}
		if (_pars[0].startsWith(";")) {
			_pars[0] = _pars[0].substring(1);
		}
		if (_pars[0].endsWith(";")) {
			_pars[0] = _pars[0].substring(0, _pars[0].length() - 1);
		}

		TBUtil tbUtil = new TBUtil();
		String ids = tbUtil.replaceAll(_pars[0], ";", ",");
		if (ids.trim().equals("")) {
			return "";
		}
		String[][] names = new CommDMO().getStringArrayByDS(null, "select t2.name,t1.score  from score_user t1 left join pub_user t2 on t1.userid=t2.id where t1.id in(" + ids + ") order by t1.id");//��ͬ���ݿ�ƴ�ӷ�����ͬ�����Ȳ�����ٴ���ƴ�ӡ����/2013-09-24��
		if (names.length == 0) {
			return "";
		}
		StringBuffer sb_names = new StringBuffer();
		for (int i = 0; i < names.length; i++) {
			sb_names.append(names[i][0]);
			sb_names.append("-");
			sb_names.append(names[i][1]);
			sb_names.append("��");
			sb_names.append(";");
		}
		return sb_names.substring(0, sb_names.length() - 1);
	}

}
