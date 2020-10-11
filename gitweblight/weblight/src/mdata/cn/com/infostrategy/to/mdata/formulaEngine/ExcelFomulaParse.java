package cn.com.infostrategy.to.mdata.formulaEngine;

import cn.com.infostrategy.to.common.HashVO;

/**
 * ���ӹ�ʽ����Ϊ[Excel]��������
 * ��ʱ���á���excel��ʽ�滻��
 * @author haoming
 * create by 2013-7-4
 */
public class ExcelFomulaParse extends AbstractFomulaParse {

	@Override
	public Object parse(SalaryFomulaParseUtil util, HashVO _factorHashVO, HashVO _baseDataHashVO, int _level, StringBuffer rtStr) throws Exception {
		String sourceType = _factorHashVO.getStringValue("sourcetype"); // �õ�������Դ���͡�
		String value = _factorHashVO.getStringValue("value");// �趨��ֵ�������ǹ�ʽ��
		value = (String) util.getReflectOtherFactor(value, _baseDataHashVO, rtStr, _level);
		if (value.indexOf(".") < 0) { // ��excel����
			HashVO excelFactorVO = util.getFoctorHashVO(value); //��ȡexcel���������������Ϣ����Ҫ�ǻ�ȡ����������
			String conditions = excelFactorVO.getStringValue("conditions"); //����������
			String excel_tableName = salaryTBUtil.getStringValueByDS(null, "select  TABLENAME from EXCEL_TAB where EXCELNAME = '" + value + "' ");
			if (excel_tableName == null || "".equals(excel_tableName)) {
				throw new Exception("��ʽ��Ҫ���á�" + value + "�������ڣ����鹫ʽ�����������ϴ����ܵ㣡");
			}
			HashVO vos[] = salaryTBUtil.getHashVoArrayByDS(null, "select *from " + excel_tableName + " order by id");
			return vos;
		}
		return null;
	}

}
