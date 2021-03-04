package cn.com.infostrategy.to.mdata.formulaEngine;

import bsh.TargetError;
import cn.com.infostrategy.to.common.HashVO;

/**
 * ���ӹ�ʽ����Ϊ[�������]����ִ�й��ߡ�
 * @author haoming
 * create by 2013-7-4
 */
public class BshFomulaParse extends AbstractFomulaParse {

	@Override
	public Object parse(SalaryFomulaParseUtil util, HashVO _factorHashVO, HashVO _baseDataHashVO, int _level, StringBuffer rtStr) throws Exception {
		String sourceType = _factorHashVO.getStringValue("sourcetype"); // �õ�������Դ���͡�
		String value = _factorHashVO.getStringValue("value");// �趨��ֵ�������ǹ�ʽ��
		String conditions = _factorHashVO.getStringValue("conditions"); // ����
		String extend = _factorHashVO.getStringValue("extend");
		String name = _factorHashVO.getStringValue("name");
		Object sendTOBsh = null;
		if (conditions != null && !conditions.equals("")) {
			sendTOBsh = util.getReflectOtherFactor(conditions, _baseDataHashVO, rtStr, _level);
			sendTOBsh = util.execFormula(sendTOBsh.toString(), _baseDataHashVO);
		} else {
			sendTOBsh = _baseDataHashVO;
		}
		try {
			BshTool tool = new BshTool();
			return tool.getValueByBshFormula(value, sendTOBsh);
		} catch (Exception ex) {
			StringBuffer expsb = new StringBuffer();
			expsb.append("ϵͳ����[" + name + "],����[" + sourceType + "]�����ڼ䷢���쳣");
			rtStr.append("����[" + name + "]�����쳣");
			if (_baseDataHashVO != null) {
				expsb.append("����������������ݣ�" + _baseDataHashVO.getSBStr());
			}
			if (ex instanceof TargetError) {
				if (((TargetError) ex).getTarget() != null) {
					expsb.append("\r\n" + ((TargetError) ex).getTarget().getMessage());
				}
				throw new Exception(expsb.toString());
			} else {
				throw new Exception(expsb.toString() + ex.getMessage());
			}
		}
	}
}
