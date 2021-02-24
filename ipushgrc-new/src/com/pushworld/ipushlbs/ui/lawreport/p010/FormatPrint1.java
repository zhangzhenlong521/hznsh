package com.pushworld.ipushlbs.ui.lawreport.p010;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;
import cn.com.infostrategy.to.report.ReportUtil;
/**
 * ��ʽ��ͬͳ��
 * @author yinliang
 * @since 2011.12.19
 */
public class FormatPrint1 extends MultiLevelReportDataBuilderAdapter{

	@Override
	public HashVO[] buildReportData(HashMap _condition) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("Select sfile.Name ��ͬ���� ,dept.Name ��������,combo.name ��ͬ���� ,busi.Name ҵ������ ," + 
				"tfile.Name ��ͬ����,sfile.filestate ����״̬,sfile.createdate �������� " + 
				"  " +
				"From lbs_stdfile sfile ,contractbusi busi ,lbs_stdfile_type tfile ,pub_corp_dept dept,pub_comboboxdict combo " + 
				"Where 1=1 And sfile.createorg = dept.Id And sfile.Type = busi.Id And sfile.busiid = tfile.Id " +
				"and combo.type= '��ͬ_��ͬ����2' and combo.id = sfile.property "
				);
		
		if(_condition.get("PROPERTY")!=null && !_condition.get("PROPERTY").equals(""))  //��ͬ����
			sb.append(" and sfile.property in " + this.getInConditionWtihQuote((String)_condition.get("PROPERTY"))) ;   
		if(_condition.get("TYPE")!=null && !_condition.get("TYPE").equals(""))          //ҵ������
			sb.append(" and sfile.type in " +  getInConditionWtihQuote((String) _condition.get("TYPE"))) ;
		if(_condition.get("BUSIID")!=null && !_condition.get("BUSIID").equals(""))      //��ͬ����
			sb.append(" and sfile.busiid in " +  getInConditionWtihQuote((String) _condition.get("BUSIID"))) ;
		if(_condition.get("FILESTATE")!=null && !_condition.get("FILESTATE").equals(""))//����״̬
			sb.append(" and sfile.filestate in " + getInConditionWtihQuote((String) _condition.get("FILESTATE"))) ;
		sb.append(" order by ��������,��������");
		HashVO[] hashvo =
			new CommDMO().getHashVoArrayByDS(null,sb.toString());
		new ReportUtil().leftOuterJoin_YSMDFromDateTime(hashvo, "��������","��������", "��");
		return hashvo;
	}
	// ������
	@Override
	public String[] getGroupFieldNames() {
		return new String[]{"��������","��������","��ͬ����","����״̬","ҵ������"};
	}
	// �������
	@Override
	public String[] getSumFiledNames() {
		return new String[]{"��ͬ����","����"};
	}
	//����Ԥ��ͳ������
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid(){
		BeforeHandGroupTypeVO[] beforeHandGroupTypeVOs = new BeforeHandGroupTypeVO[3];
		//����ͬ����
		beforeHandGroupTypeVOs[0] =  new BeforeHandGroupTypeVO("��������-��ͬ����");
		beforeHandGroupTypeVOs[0].setRowHeaderGroupFields(new String[]{"��ͬ����"});
		beforeHandGroupTypeVOs[0].setColHeaderGroupFields(new String[]{"��������"});
		beforeHandGroupTypeVOs[0].setComputeGroupFields(new String[][]{{"����",BeforeHandGroupTypeVO.COUNT}});
		//������
		beforeHandGroupTypeVOs[1] =  new BeforeHandGroupTypeVO("��������-����");
		beforeHandGroupTypeVOs[1].setRowHeaderGroupFields(new String[]{"��������"});
		beforeHandGroupTypeVOs[1].setColHeaderGroupFields(new String[]{"��������"});
		beforeHandGroupTypeVOs[1].setComputeGroupFields(new String[][]{{"����",BeforeHandGroupTypeVO.COUNT}});
		//������״̬
		beforeHandGroupTypeVOs[2] =  new BeforeHandGroupTypeVO("��������-����״̬");
		beforeHandGroupTypeVOs[2].setColHeaderGroupFields(new String[]{"����״̬"});
		beforeHandGroupTypeVOs[2].setRowHeaderGroupFields(new String[]{"��������"});	
		beforeHandGroupTypeVOs[2].setComputeGroupFields(new String[][]{{"����",BeforeHandGroupTypeVO.COUNT}});
		
		return beforeHandGroupTypeVOs;
	}
	//ͼ������ͳ������
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Chart(){
		BeforeHandGroupTypeVO[] beforeHandGroupTypeVOs = new BeforeHandGroupTypeVO[3];
		//����ͬ����
		beforeHandGroupTypeVOs[0] =  new BeforeHandGroupTypeVO("��������-��ͬ����");
		beforeHandGroupTypeVOs[0].setRowHeaderGroupFields(new String[]{"��������"});
		beforeHandGroupTypeVOs[0].setColHeaderGroupFields(new String[]{"��ͬ����"});
		beforeHandGroupTypeVOs[0].setComputeGroupFields(new String[][]{{"����",BeforeHandGroupTypeVO.COUNT}});
		//������
		beforeHandGroupTypeVOs[1] =  new BeforeHandGroupTypeVO("��������-����");
		beforeHandGroupTypeVOs[1].setRowHeaderGroupFields(new String[]{"��������"});
		beforeHandGroupTypeVOs[1].setColHeaderGroupFields(new String[]{"��������"});
		beforeHandGroupTypeVOs[1].setComputeGroupFields(new String[][]{{"����",BeforeHandGroupTypeVO.COUNT}});
		//������״̬
		beforeHandGroupTypeVOs[2] =  new BeforeHandGroupTypeVO("��������-����״̬");
		beforeHandGroupTypeVOs[2].setRowHeaderGroupFields(new String[]{"��������"});
		beforeHandGroupTypeVOs[2].setColHeaderGroupFields(new String[]{"����״̬"});
		beforeHandGroupTypeVOs[2].setComputeGroupFields(new String[][]{{"����",BeforeHandGroupTypeVO.COUNT}});
		return beforeHandGroupTypeVOs;
	}
	//��״
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Splider(){
		BeforeHandGroupTypeVO[] beforeHandGroupTypeVOs = new BeforeHandGroupTypeVO[3];
		//����ͬ����
		beforeHandGroupTypeVOs[0] =  new BeforeHandGroupTypeVO("��������-��ͬ����");
		beforeHandGroupTypeVOs[0].setRowHeaderGroupFields(new String[]{"��������"});
		beforeHandGroupTypeVOs[0].setColHeaderGroupFields(new String[]{"��ͬ����"});
		beforeHandGroupTypeVOs[0].setComputeGroupFields(new String[][]{{"����",BeforeHandGroupTypeVO.COUNT}});
		//������
		beforeHandGroupTypeVOs[1] =  new BeforeHandGroupTypeVO("��������-����");
		beforeHandGroupTypeVOs[1].setRowHeaderGroupFields(new String[]{"��������"});
		beforeHandGroupTypeVOs[1].setColHeaderGroupFields(new String[]{"��������"});
		beforeHandGroupTypeVOs[1].setComputeGroupFields(new String[][]{{"����",BeforeHandGroupTypeVO.COUNT}});
		//������״̬
		beforeHandGroupTypeVOs[2] =  new BeforeHandGroupTypeVO("��������-����״̬");
		beforeHandGroupTypeVOs[2].setRowHeaderGroupFields(new String[]{"��������"});
		beforeHandGroupTypeVOs[2].setColHeaderGroupFields(new String[]{"����״̬"});
		beforeHandGroupTypeVOs[2].setComputeGroupFields(new String[][]{{"����",BeforeHandGroupTypeVO.COUNT}});
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
