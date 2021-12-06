package com.pushworld.ipushgrc.bs.wfrisk.p130;

import java.util.ArrayList;
import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;

public class RiskChangeReportBuilderAdapter extends MultiLevelReportDataBuilderAdapter {

	public HashVO[] buildReportData(HashMap condition) throws Exception {
		CommDMO dmo = new CommDMO();
		//现有查询条件
		ComBoxItemVO combox_type = (ComBoxItemVO) condition.get("obj_cmpfiletype");//文件分类,存的id，但显示name
		String cmpfiletype = null;
		if (combox_type != null) {
			cmpfiletype = combox_type.getName();
		}
		String blcorpid = (String) condition.get("blcorpid");//所属机构
		String bsactid = (String) condition.get("bsactid");//业务活动分类
		String publishdate = (String) condition.get("publishdate");//发布日期

		//可以直接在模板查询面板中添加
		String ictypeid = (String) condition.get("ictypeid");//内控要素
		String userblcorpid = (String) condition.get("userblcorpid");//用户所属机构
		String lookcontent = (String) condition.get("lookcontent");//查看內容
		String clicktime = (String) condition.get("clicktime");//查看日期

		TBUtil tbutil = new TBUtil();
		StringBuffer sb_sql = new StringBuffer("select cmpfiletype 文件分类, blcorpname 文件所属机构,userblcorpname 用户所属机构,bsactname 业务活动,ictypename 内控要素  from v_cmpfile_clicklog where 1=1 ");

		if (cmpfiletype != null && !"".equals(cmpfiletype)) {
			sb_sql.append(" and cmpfiletype = '" + cmpfiletype + "' ");
		}
		if (blcorpid != null && !"".equals(blcorpid)) {
			sb_sql.append(" and blcorpid in(" + tbutil.getInCondition(blcorpid) + ") ");
		}
		if (bsactid != null && !"".equals(bsactid)) {
			sb_sql.append(" and bsactid in(" + tbutil.getInCondition(bsactid) + ") ");
		}
		if (publishdate != null) {
			String str_dates = tbutil.convertComp_dateTimeFormat(publishdate);
			String[] dates = str_dates.split(";");
			sb_sql.append(" and publishdate>='" + dates[0] + "' and publishdate<='" + dates[1] + "' ");
		}
		//查询过滤逻辑已实现，可以直接在模板查询面板中添加
		if (ictypeid != null && !"".equals(ictypeid)) {
			sb_sql.append(" and ictypeid in(" + tbutil.getInCondition(ictypeid) + ") ");
		}
		if (userblcorpid != null && !"".equals(userblcorpid)) {
			sb_sql.append(" and userblcorpid in(" + tbutil.getInCondition(userblcorpid) + ") ");
		}
		if (lookcontent != null && !"".equals(lookcontent)) {
			sb_sql.append(" and " + lookcontent + "='Y' ");
		}
		if (clicktime != null) {
			String str_dates = tbutil.convertComp_dateTimeFormat(clicktime);
			String[] dates = str_dates.split(";");
			sb_sql.append(" and clicktime>='" + dates[0] + "' and clicktime<='" + dates[1] + "' ");
		}
		sb_sql.append(" order by publishdate desc");

		String str_sql = "select (select count(id) from v_cmp_risk_editlog where edittype='新增风险点' and filestate=3) 新增风险点,(select count(id) from v_cmp_risk_editlog where edittype='删除风险点' and filestate=3) 删除风险点,(select count(id) from v_cmp_risk_editlog  where rankcode >rankcode2 and edittype='修改风险点' and filestate=3) 风险等级变高,(select count(id) from v_cmp_risk_editlog  where rankcode <rankcode2 and edittype='修改风险点' and filestate=3) 风险等级变低,(select count(id) from v_cmp_risk_editlog  where ctrlcode>ctrlcode2 and edittype='修改风险点' and filestate=3) 控制有效性变高,(select count(id) from v_cmp_risk_editlog  where ctrlcode<ctrlcode2 and edittype='修改风险点' and filestate=3) 控制有效性变低,(select count(id) from v_cmp_risk_editlog  where rankcode=rankcode2 and (ctrlcode=ctrlcode2 or (ctrlcode is null and ctrlcode is null))and edittype='修改风险点' and filestate=3) 其他修改 from wltdual";
		//		try {
		//			String[][] counts = UIUtil.getStringArrayByDS(null, str_sql);
		//			double[] count = new double[] { Double.parseDouble(counts[0][0]), Double.parseDouble(counts[0][1]), Double.parseDouble(counts[0][2]), Double.parseDouble(counts[0][3]), Double.parseDouble(counts[0][4]), Double.parseDouble(counts[0][5]), Double.parseDouble(counts[0][6]) };
		//			String[] colname = new String[] { "新增风险点", "删除风险点", "风险等级变高", "风险等级变低", "控制有效性变高", "控制有效性变低", "其他修改" };
		//			BillChartPanel chartpanel = new BillChartPanel("风险迁徙图", colname, count);
		//			this.add(chartpanel);
		//		} catch (Exception e) {
		//			e.printStackTrace();
		//		}
		String[][] values = dmo.getStringArrayByDS(null, str_sql);
		HashVO[] hashvo = new HashVO[6];
		hashvo[0] = new HashVO();
		hashvo[0].setAttributeValue("类型", "新增风险点");
		hashvo[0].setAttributeValue("数量", values[0][0]);
		hashvo[1] = new HashVO();
		hashvo[1].setAttributeValue("类型", "删除风险点");
		hashvo[1].setAttributeValue("数量", values[0][1]);
		hashvo[2] = new HashVO();
		hashvo[2].setAttributeValue("类型", "风险等级变高");
		hashvo[2].setAttributeValue("数量", values[0][2]);
		hashvo[3] = new HashVO();
		hashvo[3].setAttributeValue("类型", "风险等级变低");
		hashvo[3].setAttributeValue("数量", values[0][3]);
		hashvo[4] = new HashVO();
		hashvo[4].setAttributeValue("类型", "控制有效性变高");
		hashvo[4].setAttributeValue("数量", values[0][4]);
		hashvo[5] = new HashVO();
		hashvo[5].setAttributeValue("类型", "控制有效性变低");
		hashvo[5].setAttributeValue("数量", values[0][5]);

		return hashvo;
	}

	public String[] getGroupFieldNames() {
		return null;
	}

	public String[] getSumFiledNames() {
		return new String[] { "数量" };
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid() {
		return getBeforehandGroupType(1);
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Chart() {
		return getBeforehandGroupType(2);
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType(int _type) {
		ArrayList al = new ArrayList(); //
		if (_type == 1) {
			al.add(getBeforehandGroupType("类型_类型", "数量", BeforeHandGroupTypeVO.COUNT));
			//al.add(getBeforehandGroupType("文件分类_用户所属机构", "数量", BeforeHandGroupTypeVO.COUNT));
			//al.add(getBeforehandGroupType("文件分类_业务活动", "数量", BeforeHandGroupTypeVO.COUNT));
			//al.add(getBeforehandGroupType("业务活动_内控要素", "数量", BeforeHandGroupTypeVO.COUNT));
		} else {
			al.add(getBeforehandGroupType("类型_类型", "数量", BeforeHandGroupTypeVO.COUNT));
			//al.add(getBeforehandGroupType("文件分类_业务活动", "数量", BeforeHandGroupTypeVO.COUNT));
			//al.add(getBeforehandGroupType("文件分类_内控要素", "数量", BeforeHandGroupTypeVO.COUNT));
			//al.add(getBeforehandGroupType("业务活动_文件所属机构", "数量", BeforeHandGroupTypeVO.COUNT));
			//al.add(getBeforehandGroupType("业务活动_用户所属机构", "数量", BeforeHandGroupTypeVO.COUNT));
		}
		return (BeforeHandGroupTypeVO[]) al.toArray(new BeforeHandGroupTypeVO[0]);
	}

	public BeforeHandGroupTypeVO getBeforehandGroupType(String _name, String typeName, String _type) {
		String[] names = _name.split("_");
		BeforeHandGroupTypeVO typeVo = new BeforeHandGroupTypeVO();
		typeVo.setName(_name);
		typeVo.setRowHeaderGroupFields(new String[] { names[0] });
		//typeVo.setColHeaderGroupFields(new String[] { names[1] });
		typeVo.setComputeGroupFields(new String[][] { { typeName, _type } });
		return typeVo;
	}
}
