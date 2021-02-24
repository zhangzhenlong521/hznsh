package cn.com.infostrategy.to.mdata.formulaEngine;

import cn.com.infostrategy.to.common.HashVO;

/**
 * 因子公式类型为[Excel]解析器。
 * 暂时弃用。被excel公式替换。
 * @author haoming
 * create by 2013-7-4
 */
public class ExcelFomulaParse extends AbstractFomulaParse {

	@Override
	public Object parse(SalaryFomulaParseUtil util, HashVO _factorHashVO, HashVO _baseDataHashVO, int _level, StringBuffer rtStr) throws Exception {
		String sourceType = _factorHashVO.getStringValue("sourcetype"); // 得到数据来源类型。
		String value = _factorHashVO.getStringValue("value");// 设定的值，可以是公式。
		value = (String) util.getReflectOtherFactor(value, _baseDataHashVO, rtStr, _level);
		if (value.indexOf(".") < 0) { // 是excel对象
			HashVO excelFactorVO = util.getFoctorHashVO(value); //获取excel对象的因子配置信息。主要是获取过滤条件。
			String conditions = excelFactorVO.getStringValue("conditions"); //传入条件。
			String excel_tableName = salaryTBUtil.getStringValueByDS(null, "select  TABLENAME from EXCEL_TAB where EXCELNAME = '" + value + "' ");
			if (excel_tableName == null || "".equals(excel_tableName)) {
				throw new Exception("公式中要调用【" + value + "】不存在，请检查公式，或者数据上传功能点！");
			}
			HashVO vos[] = salaryTBUtil.getHashVoArrayByDS(null, "select *from " + excel_tableName + " order by id");
			return vos;
		}
		return null;
	}

}
