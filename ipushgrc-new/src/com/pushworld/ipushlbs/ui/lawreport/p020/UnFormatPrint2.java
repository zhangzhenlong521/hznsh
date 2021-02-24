package com.pushworld.ipushlbs.ui.lawreport.p020;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;
import cn.com.infostrategy.to.report.ReportUtil;

/**
 * �Ǹ�ʽ��ͬ��ӡͳ��
 * 
 * @author yinliang
 * @since 2011.12.19
 */
public class UnFormatPrint2 extends MultiLevelReportDataBuilderAdapter {

	@Override
	public HashVO[] buildReportData(HashMap _condition) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("Select de.Name ��������,ls.Name ��ͬ����,ld.Type �������,ld.pdate ��ӡ����,ld.pcount ��ӡ���� " + "From lbs_dept_print_contact ld ,pub_corp_dept de , lbs_unstdfile ls "
				+ "Where ld.dept_id = de.Id And ld.contact_id = ls.Id and ld.type = '2'" // ֱ��д���ˣ����á���
		);
		if (_condition.get("DEALDOC_NAME") != null && !_condition.get("DEALDOC_NAME").equals(""))
			sb.append(" and ld.contact_id in " +  getInConditionWtihQuote((String) _condition.get("DEALDOC_NAME")));
		if (_condition.get("SENDORG") != null && !_condition.get("SENDORG").equals(""))
			sb.append(" and ld.dept_id in " +getInConditionWtihQuote( (String) _condition.get("SENDORG")));
		sb.append(" order by ��������,��ӡ����");
		HashVO[] hashvo = new CommDMO().getHashVoArrayByDS(null, sb.toString());
		new ReportUtil().leftOuterJoin_YSMDFromDateTime(hashvo, "��ӡʱ��", "��ӡ����", "��");
		return hashvo;
	}

	// ������
	@Override
	public String[] getGroupFieldNames() {
		return new String[] { "��������", "��ͬ����", "�������", "��ӡ����" };
	}

	// �������
	@Override
	public String[] getSumFiledNames() {
		return new String[] { "��ͬ����", "��ӡ����" };
	}

	// ����Ԥ��ͳ������
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid() {
		BeforeHandGroupTypeVO[] beforeHandGroupTypeVOs = new BeforeHandGroupTypeVO[2];
		// ����ͬ����
		beforeHandGroupTypeVOs[0] = new BeforeHandGroupTypeVO("��������-��ͬ����");
		beforeHandGroupTypeVOs[0].setRowHeaderGroupFields(new String[] { "��������" });
		beforeHandGroupTypeVOs[0].setColHeaderGroupFields(new String[] { "��ͬ����" });
		beforeHandGroupTypeVOs[0].setComputeGroupFields(new String[][] { { "��ӡ����", BeforeHandGroupTypeVO.SUM } });
		// ������
		beforeHandGroupTypeVOs[1] = new BeforeHandGroupTypeVO("��ͬ����-����");
		beforeHandGroupTypeVOs[1].setColHeaderGroupFields(new String[] { "��ͬ����" });
		beforeHandGroupTypeVOs[1].setRowHeaderGroupFields(new String[] { "��ӡʱ��" });
		beforeHandGroupTypeVOs[1].setComputeGroupFields(new String[][] { { "��ӡ����", BeforeHandGroupTypeVO.SUM } });

		return beforeHandGroupTypeVOs;
	}

	// ͼ������ͳ������
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Chart() {
		BeforeHandGroupTypeVO[] beforeHandGroupTypeVOs = new BeforeHandGroupTypeVO[2];
		// ����ͬ����
		beforeHandGroupTypeVOs[0] = new BeforeHandGroupTypeVO("��������-��ͬ����");
		beforeHandGroupTypeVOs[0].setRowHeaderGroupFields(new String[] { "��������" });
		beforeHandGroupTypeVOs[0].setColHeaderGroupFields(new String[] { "��ͬ����" });
		beforeHandGroupTypeVOs[0].setComputeGroupFields(new String[][] { { "��ӡ����", BeforeHandGroupTypeVO.SUM } });
		// ������
		beforeHandGroupTypeVOs[1] = new BeforeHandGroupTypeVO("��ͬ����-����");
		beforeHandGroupTypeVOs[1].setRowHeaderGroupFields(new String[] { "��ͬ����" });
		beforeHandGroupTypeVOs[1].setColHeaderGroupFields(new String[] { "��ӡʱ��" });
		beforeHandGroupTypeVOs[1].setComputeGroupFields(new String[][] { { "��ӡ����", BeforeHandGroupTypeVO.SUM } });
		return beforeHandGroupTypeVOs;
	}

	// ��״
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Splider() {
		BeforeHandGroupTypeVO[] beforeHandGroupTypeVOs = new BeforeHandGroupTypeVO[2];
		// ����ͬ����
		beforeHandGroupTypeVOs[0] = new BeforeHandGroupTypeVO("��������-��ͬ����");
		beforeHandGroupTypeVOs[0].setRowHeaderGroupFields(new String[] { "��������" });
		beforeHandGroupTypeVOs[0].setColHeaderGroupFields(new String[] { "��ͬ����" });
		beforeHandGroupTypeVOs[0].setComputeGroupFields(new String[][] { { "��ӡ����", BeforeHandGroupTypeVO.SUM } });
		// ������
		beforeHandGroupTypeVOs[1] = new BeforeHandGroupTypeVO("��ͬ����-����");
		beforeHandGroupTypeVOs[1].setRowHeaderGroupFields(new String[] { "��ͬ����" });
		beforeHandGroupTypeVOs[1].setColHeaderGroupFields(new String[] { "��ӡʱ��" });
		beforeHandGroupTypeVOs[1].setComputeGroupFields(new String[][] { { "��ӡ����", BeforeHandGroupTypeVO.SUM } });
		return beforeHandGroupTypeVOs;
	}
	
	private String getInConditionWtihQuote(String valuewhitreg){
		String returnStr=null;
		
		if(valuewhitreg!=null&&!valuewhitreg.equals("")){
			if(valuewhitreg.endsWith(";")){
				valuewhitreg=valuewhitreg.substring(0,valuewhitreg.length()-1);
			}
			if(valuewhitreg.startsWith(";")){
				valuewhitreg=valuewhitreg.substring(1,valuewhitreg.length());
			}
			String[] temp=valuewhitreg.split(";");
			returnStr="(";
			for(String str:temp){
				returnStr=returnStr+"'"+str+"',";
			}
			if(returnStr.endsWith(","))
				returnStr=returnStr.substring(0,returnStr.length()-1);
			if(returnStr.startsWith(","))
				returnStr=returnStr.substring(1,returnStr.length());
		}
		returnStr+=")";
		return returnStr;
	}
}
