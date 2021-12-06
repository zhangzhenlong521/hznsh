package com.pushworld.ipushlbs.ui.lawreport.p010;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;
import cn.com.infostrategy.to.report.ReportUtil;
/**
 * 格式合同统计
 * @author yinliang
 * @since 2011.12.19
 */
public class FormatPrint1 extends MultiLevelReportDataBuilderAdapter{

	@Override
	public HashVO[] buildReportData(HashMap _condition) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("Select sfile.Name 合同名称 ,dept.Name 创建机构,combo.name 合同性质 ,busi.Name 业务类型 ," + 
				"tfile.Name 合同属性,sfile.filestate 发布状态,sfile.createdate 创建日期 " + 
				"  " +
				"From lbs_stdfile sfile ,contractbusi busi ,lbs_stdfile_type tfile ,pub_corp_dept dept,pub_comboboxdict combo " + 
				"Where 1=1 And sfile.createorg = dept.Id And sfile.Type = busi.Id And sfile.busiid = tfile.Id " +
				"and combo.type= '合同_合同性质2' and combo.id = sfile.property "
				);
		
		if(_condition.get("PROPERTY")!=null && !_condition.get("PROPERTY").equals(""))  //合同性质
			sb.append(" and sfile.property in " + this.getInConditionWtihQuote((String)_condition.get("PROPERTY"))) ;   
		if(_condition.get("TYPE")!=null && !_condition.get("TYPE").equals(""))          //业务类型
			sb.append(" and sfile.type in " +  getInConditionWtihQuote((String) _condition.get("TYPE"))) ;
		if(_condition.get("BUSIID")!=null && !_condition.get("BUSIID").equals(""))      //合同属性
			sb.append(" and sfile.busiid in " +  getInConditionWtihQuote((String) _condition.get("BUSIID"))) ;
		if(_condition.get("FILESTATE")!=null && !_condition.get("FILESTATE").equals(""))//发布状态
			sb.append(" and sfile.filestate in " + getInConditionWtihQuote((String) _condition.get("FILESTATE"))) ;
		sb.append(" order by 创建机构,创建日期");
		HashVO[] hashvo =
			new CommDMO().getHashVoArrayByDS(null,sb.toString());
		new ReportUtil().leftOuterJoin_YSMDFromDateTime(hashvo, "创建日期","创建日期", "季");
		return hashvo;
	}
	// 组区域
	@Override
	public String[] getGroupFieldNames() {
		return new String[]{"创建机构","创建日期","合同性质","发布状态","业务类型"};
	}
	// 结果区域
	@Override
	public String[] getSumFiledNames() {
		return new String[]{"合同名称","数量"};
	}
	//网格预置统计类型
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid(){
		BeforeHandGroupTypeVO[] beforeHandGroupTypeVOs = new BeforeHandGroupTypeVO[3];
		//按合同性质
		beforeHandGroupTypeVOs[0] =  new BeforeHandGroupTypeVO("创建机构-合同性质");
		beforeHandGroupTypeVOs[0].setRowHeaderGroupFields(new String[]{"合同性质"});
		beforeHandGroupTypeVOs[0].setColHeaderGroupFields(new String[]{"创建机构"});
		beforeHandGroupTypeVOs[0].setComputeGroupFields(new String[][]{{"数量",BeforeHandGroupTypeVO.COUNT}});
		//按季度
		beforeHandGroupTypeVOs[1] =  new BeforeHandGroupTypeVO("创建机构-季度");
		beforeHandGroupTypeVOs[1].setRowHeaderGroupFields(new String[]{"创建日期"});
		beforeHandGroupTypeVOs[1].setColHeaderGroupFields(new String[]{"创建机构"});
		beforeHandGroupTypeVOs[1].setComputeGroupFields(new String[][]{{"数量",BeforeHandGroupTypeVO.COUNT}});
		//按发布状态
		beforeHandGroupTypeVOs[2] =  new BeforeHandGroupTypeVO("创建机构-发布状态");
		beforeHandGroupTypeVOs[2].setColHeaderGroupFields(new String[]{"发布状态"});
		beforeHandGroupTypeVOs[2].setRowHeaderGroupFields(new String[]{"创建机构"});	
		beforeHandGroupTypeVOs[2].setComputeGroupFields(new String[][]{{"数量",BeforeHandGroupTypeVO.COUNT}});
		
		return beforeHandGroupTypeVOs;
	}
	//图标类型统计类型
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Chart(){
		BeforeHandGroupTypeVO[] beforeHandGroupTypeVOs = new BeforeHandGroupTypeVO[3];
		//按合同性质
		beforeHandGroupTypeVOs[0] =  new BeforeHandGroupTypeVO("创建机构-合同性质");
		beforeHandGroupTypeVOs[0].setRowHeaderGroupFields(new String[]{"创建机构"});
		beforeHandGroupTypeVOs[0].setColHeaderGroupFields(new String[]{"合同性质"});
		beforeHandGroupTypeVOs[0].setComputeGroupFields(new String[][]{{"数量",BeforeHandGroupTypeVO.COUNT}});
		//按季度
		beforeHandGroupTypeVOs[1] =  new BeforeHandGroupTypeVO("创建机构-季度");
		beforeHandGroupTypeVOs[1].setRowHeaderGroupFields(new String[]{"创建机构"});
		beforeHandGroupTypeVOs[1].setColHeaderGroupFields(new String[]{"创建日期"});
		beforeHandGroupTypeVOs[1].setComputeGroupFields(new String[][]{{"数量",BeforeHandGroupTypeVO.COUNT}});
		//按发布状态
		beforeHandGroupTypeVOs[2] =  new BeforeHandGroupTypeVO("创建机构-发布状态");
		beforeHandGroupTypeVOs[2].setRowHeaderGroupFields(new String[]{"创建机构"});
		beforeHandGroupTypeVOs[2].setColHeaderGroupFields(new String[]{"发布状态"});
		beforeHandGroupTypeVOs[2].setComputeGroupFields(new String[][]{{"数量",BeforeHandGroupTypeVO.COUNT}});
		return beforeHandGroupTypeVOs;
	}
	//饼状
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Splider(){
		BeforeHandGroupTypeVO[] beforeHandGroupTypeVOs = new BeforeHandGroupTypeVO[3];
		//按合同性质
		beforeHandGroupTypeVOs[0] =  new BeforeHandGroupTypeVO("创建机构-合同性质");
		beforeHandGroupTypeVOs[0].setRowHeaderGroupFields(new String[]{"创建机构"});
		beforeHandGroupTypeVOs[0].setColHeaderGroupFields(new String[]{"合同性质"});
		beforeHandGroupTypeVOs[0].setComputeGroupFields(new String[][]{{"数量",BeforeHandGroupTypeVO.COUNT}});
		//按季度
		beforeHandGroupTypeVOs[1] =  new BeforeHandGroupTypeVO("创建机构-季度");
		beforeHandGroupTypeVOs[1].setRowHeaderGroupFields(new String[]{"创建机构"});
		beforeHandGroupTypeVOs[1].setColHeaderGroupFields(new String[]{"创建日期"});
		beforeHandGroupTypeVOs[1].setComputeGroupFields(new String[][]{{"数量",BeforeHandGroupTypeVO.COUNT}});
		//按发布状态
		beforeHandGroupTypeVOs[2] =  new BeforeHandGroupTypeVO("创建机构-发布状态");
		beforeHandGroupTypeVOs[2].setRowHeaderGroupFields(new String[]{"创建机构"});
		beforeHandGroupTypeVOs[2].setColHeaderGroupFields(new String[]{"发布状态"});
		beforeHandGroupTypeVOs[2].setComputeGroupFields(new String[][]{{"数量",BeforeHandGroupTypeVO.COUNT}});
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
