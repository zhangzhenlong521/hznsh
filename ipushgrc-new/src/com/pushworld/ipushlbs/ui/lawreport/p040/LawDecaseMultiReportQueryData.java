package com.pushworld.ipushlbs.ui.lawreport.p040;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;
import cn.com.infostrategy.to.report.ReportUtil;

/**
 * ���߰���--��ά����--��֯����
 * 
 * @author wupeng
 * 
 */
public class LawDecaseMultiReportQueryData extends MultiLevelReportDataBuilderAdapter {

	@SuppressWarnings("unchecked")
	@Override
	public HashVO[] buildReportData(HashMap condition) throws Exception {
		ReportUtil util = new ReportUtil();
		TBUtil tb = new TBUtil();
		StringBuffer sb = new StringBuffer("Select Law.Sendproceeding ��������,Law.Casetype ���շ���, pro.Name �漰ҵ������, Dept.Name ��������,dict.Name ��������,law.CREATEDATE ���,law.CREATEDATE ����" + "  From Lbs_Case_decase Law, Pub_Corp_Dept Dept,pub_comboboxdict pro,pub_comboboxdict dict"
				+ "  Where Dept.Id = Law.charged_entity and pro.id=law.busitypename and pro.type='����_ҵ������' and dict.id=law.lawsuittype and dict.type='����_��������'");

		if (condition != null) {
			if (condition.get("CHARGED_ENTITY") != null && !condition.get("CHARGED_ENTITY").equals(""))
				sb.append("  and law.charged_entity in ").append(this.getInConditionWtihQuote((String) condition.get("CHARGED_ENTITY")));

			if (condition.get("ENDTYPE") != null && !condition.get("ENDTYPE").equals(""))
				sb.append("  and law.endtype in").append(this.getInConditionWtihQuote((String) condition.get("ENDTYPE")));
			else
				sb.append("  and law.endtype != '").append("δ����").append("'");
			if (condition.get("CREATEDATE") != null && !condition.get("CREATEDATE").equals("")) {
				String date[] = tb.convertComp_dateTimeFormat((String) condition.get("CREATEDATE")).split(";");
				sb.append(" and law.CREATEDATE >='").append(date[0]).append("'  and law.CREATEDATE<='").append(date[1]).append("'");
			}
		}
		HashVO[] vos = new CommDMO().getHashVoArrayByDS(null, sb.toString());
		util.leftOuterJoin_YSMDFromDateTime(vos, "���", "���", "��");
		util.leftOuterJoin_YSMDFromDateTime(vos, "����", "����", "��");

		return vos;
	}

	@Override
	public String[] getGroupFieldNames() {

		return new String[] { "�漰ҵ������" };
	}

	@Override
	public String[] getSumFiledNames() {

		return new String[] { "��������", "����" };
	}

	/**
	 * ����ʲô�������ɱ���
	 */
	// @Override
	// public BeforeHandGroupTypeVO[] getBeforehandGroupType_Chart() {
	// BeforeHandGroupTypeVO[] vos=new BeforeHandGroupTypeVO[3];
	// vos[0]=new
	// BeforeHandGroupTypeVO("�漰ҵ������-���շ���");//Ĭ�ϵĺ������ǡ��漰ҵ�����͡����������ǡ�������������������,x/y��ת���󣬺����ǡ����շ��ࡰ�������ǡ�������������������
	// vos[0].setColHeaderGroupFields(new String[]{"�漰ҵ������"});
	// vos[0].setRowHeaderGroupFields(new String[]{"���շ���"});
	// vos[0].setComputeGroupFields(new
	// String[][]{{"��������",BeforeHandGroupTypeVO.COUNT}});
	//		
	// vos[1]=new BeforeHandGroupTypeVO("��������-��������");
	// vos[1].setColHeaderGroupFields(new String[]{"��������"});
	// vos[1].setRowHeaderGroupFields(new String[]{"��������"});
	// vos[1].setComputeGroupFields(new
	// String[][]{{"��������",BeforeHandGroupTypeVO.COUNT}});
	//		
	// vos[2]=new BeforeHandGroupTypeVO("�漰ҵ������-��������");
	// vos[2].setColHeaderGroupFields(new String[]{"��������"});
	// vos[2].setRowHeaderGroupFields(new String[]{"�漰ҵ������"});
	// vos[2].setComputeGroupFields(new
	// String[][]{{"��������",BeforeHandGroupTypeVO.COUNT}});
	//		
	// return vos;
	// }
	@Override
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid() {
		BeforeHandGroupTypeVO[] vos = new BeforeHandGroupTypeVO[5];
		vos[0] = new BeforeHandGroupTypeVO("�漰ҵ������-���շ���");// ��ı����ǡ����շ��࡮,���ı����ǡ��漰ҵ�����͡�,�м���ʾ�������ǡ�������������������
		vos[0].setColHeaderGroupFields(new String[] { "�漰ҵ������" });// y��
		vos[0].setRowHeaderGroupFields(new String[] { "���շ���" });// x��
		vos[0].setComputeGroupFields(new String[][] { { "����", BeforeHandGroupTypeVO.COUNT } });

		vos[1] = new BeforeHandGroupTypeVO("��������-��������");
		vos[1].setColHeaderGroupFields(new String[] { "��������" });
		vos[1].setRowHeaderGroupFields(new String[] { "��������" });
		vos[1].setComputeGroupFields(new String[][] { { "����", BeforeHandGroupTypeVO.COUNT } });

		vos[2] = new BeforeHandGroupTypeVO("�漰ҵ������-��������");
		vos[2].setColHeaderGroupFields(new String[] { "��������" });
		vos[2].setRowHeaderGroupFields(new String[] { "�漰ҵ������" });
		vos[2].setComputeGroupFields(new String[][] { { "����", BeforeHandGroupTypeVO.COUNT } });

		vos[3] = new BeforeHandGroupTypeVO("��������-����");
		vos[3].setColHeaderGroupFields(new String[] { "��������" });
		vos[3].setRowHeaderGroupFields(new String[] { "����" });
		vos[3].setComputeGroupFields(new String[][] { { "����", BeforeHandGroupTypeVO.COUNT } });

		vos[4] = new BeforeHandGroupTypeVO("�漰ҵ������-�걨");
		vos[4].setColHeaderGroupFields(new String[] { "��������" });
		vos[4].setRowHeaderGroupFields(new String[] { "���" });
		vos[4].setComputeGroupFields(new String[][] { { "����", BeforeHandGroupTypeVO.COUNT } });
		return vos;
	}

	private String getInConditionWtihQuote(String valuewhitreg) {
		String returnStr = null;

		if (valuewhitreg != null && !valuewhitreg.equals("")) {
			if (valuewhitreg.endsWith(";")) {
				valuewhitreg = valuewhitreg.substring(0, valuewhitreg.length() - 1);
			}
			if (valuewhitreg.startsWith(";")) {
				valuewhitreg = valuewhitreg.substring(1, valuewhitreg.length());
			}
			String[] temp = valuewhitreg.split(";");
			returnStr = "(";
			for (String str : temp) {
				returnStr = returnStr + "'" + str + "',";
			}
			if (returnStr.endsWith(","))
				returnStr = returnStr.substring(0, returnStr.length() - 1);
			if (returnStr.startsWith(","))
				returnStr = returnStr.substring(1, returnStr.length());
		}
		returnStr += ")";
		return returnStr;
	}

}
