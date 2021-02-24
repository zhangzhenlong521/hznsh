package cn.com.infostrategy.bs.sysapp.other;

import java.util.ArrayList;
import java.util.HashMap;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.sysapp.other.ExcelImportBean;

/**
 * ����excel�������˴���DMO
 * @author lcj
 * create by 2019-06-11
 */
public class ImportExcelDMO extends AbstractDMO {
	TBUtil tbutil = new TBUtil();

	/*
	 * ͨ��У����ԣ�У�����ݡ�
	 */
	public HashMap checkExcelDataByPolicy(HashVO[] _hashVOs, String _excelID) throws Exception {
		HashMap rtvalue = new HashMap();
		ArrayList errorList = new ArrayList();
		ArrayList warnList = new ArrayList();
		CommDMO dmo = new CommDMO();
		HashVO policyVOS[] = dmo.getHashVoArrayByDS(null, "select * from pub_import_excel_policy where excelid=" + _excelID + " order by seq");

		StringBuffer msg = new StringBuffer();
		boolean checkResult = true;
		for (int i = 0; i < policyVOS.length; i++) { //
			int start_row = policyVOS[i].getIntegerValue("start_row", 1);
			start_row--;//�±���Ҫ��1
			if (start_row < 0) {
				start_row = 0;
			}
			for (int j = start_row; j < _hashVOs.length; j++) {
				HashVO onerow = _hashVOs[j]; //�õ�һ��excel���ݡ�
				String excelcol = policyVOS[i].getStringValue("excelcol"); //�Ƚ���һ��
				String condition = policyVOS[i].getStringValue("conditions"); //����;����;����;����;����;����;������;����
				String checktype = policyVOS[i].getStringValue("checktype");
				String value = onerow.getStringValue(excelcol, "").trim(); //�õ���Ԫ���ֵ
				boolean rowResult = true;
				if (tbutil.isEmpty(value)) {
					if ("����".equals(condition)) {
						rowResult = false;
						msg.append(excelcol + "" + (j + 1) + "����Ϊ��");
					}
				} else if ("����".equals(condition)) {
					rowResult = isNumber(value);
					if (!rowResult) {
						msg.append(excelcol + "" + (j + 1) + "����[" + value + "]��������");
					}
				} else if ("����".equals(condition)) {//�ж����ڸ�ʽ����ʽֻ��Ϊyyyy-mm-dd
					rowResult = isDate(value);
					if (!rowResult) {
						msg.append(excelcol + "" + (j + 1) + "����[" + value + "]��������");
					}
				} else if ("����".equals(condition)) {//
					rowResult = isXuLie(value);
					if (!rowResult) {
						msg.append(excelcol + "" + (j + 1) + "����[" + value + "]��������");
					}
				} else if ("������ʽ".equals(condition)) {//
					String custom_formula = policyVOS[i].getStringValue("custom_formula");
					rowResult = isZhengZe(value, custom_formula);
					if (!rowResult) {
						msg.append(excelcol + "" + (j + 1) + "����[" + value + "]������Ҫ��");
					}
				}
				if (!rowResult) {
					checkResult = false;
					if ("����".equals(checktype)) {
						warnList.add(new ExcelImportBean(j + 1, excelcol, msg.toString(), ExcelImportBean.WARN));
					} else if ("����".equals(checktype)) {
						errorList.add(new ExcelImportBean(j + 1, excelcol, msg.toString(), ExcelImportBean.WARN));
					}
					msg = msg.delete(0, msg.length());
				}
			}
		}
		rtvalue.put("ERROR", errorList);
		rtvalue.put("WARN", warnList);
		rtvalue.put("RESULT", checkResult);
		return rtvalue;
	}

	private boolean isNumber(String _str) {
		if (tbutil.isEmpty(_str)) {
			return true;
		}
		String regex = "-?(0|([1-9]\\d*))\\.?\\d+";
		return _str.matches(regex);
	}

	// �ж�һ���ַ����Ƿ�������!! ��ÿһλ����0-9�е�һ��! 
	public boolean isXuLie(String _str) {
		if (tbutil.isEmpty(_str)) {
			return true;
		}
		String regex = "^([1-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9])|([0])|([0]\\.\\d*)$";
		return _str.matches(regex);
	}

	public boolean isDate(String _str) {
		if (tbutil.isEmpty(_str)) {
			return true;
		}
		String regex = "^((\\d{2}(([02468][048])|([13579][26]))[\\-]{1}((((0?[13578])|(1[02]))[\\-]{1}((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-]{1}((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-]{1}((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-]{1}((((0?[13578])|(1[02]))[\\-]{1}((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-]{1}((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-]{1}((0?[1-9])|(1[0-9])|(2[0-8]))))))$";
		return _str.matches(regex);
	}

	public boolean isZhengZe(String _str, String _regex) {
		if (tbutil.isEmpty(_str) || tbutil.isEmpty(_regex)) {
			return true;
		}
		return _str.matches(_regex);
	}
}
