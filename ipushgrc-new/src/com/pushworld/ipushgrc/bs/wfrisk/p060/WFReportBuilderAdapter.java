package com.pushworld.ipushgrc.bs.wfrisk.p060;

import java.util.ArrayList;
import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter;
import cn.com.infostrategy.bs.report.ReportDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;
import cn.com.infostrategy.to.report.ReportUtil;

public class WFReportBuilderAdapter extends MultiLevelReportDataBuilderAdapter {

	public HashVO[] buildReportData(HashMap condition) throws Exception {
		CommDMO dmo = new CommDMO();
		//现有查询条件
		String filestate = (String) condition.get("filestate");//文件状态,多选参照,值为“3;5;”
		String blcorpid = (String) condition.get("blcorpid");//所属机构
		String bsactid = (String) condition.get("bsactid");//业务活动分类
		String ictypeid = (String) condition.get("ictypeid");//内控要素

		ComBoxItemVO combox_type = (ComBoxItemVO) condition.get("obj_cmpfiletype");//文件分类,存的id，但显示name
		String cmpfiletype = null;
		if (combox_type != null) {
			cmpfiletype = combox_type.getName();
		}

		TBUtil tbutil = new TBUtil();
		StringBuffer sb_sql = new StringBuffer("select WFPROCESS_ID id, cmpfiletype 文件分类,filestatename 文件状态, blcorpid 所属机构,bsactid 业务活动,ictypeid 内控要素, publishdate 发布日期,wfprocess_id from v_process_file where 1=1 ");

		if (filestate != null && !"".equals(filestate)) {
			sb_sql.append(" and filestate in(" + tbutil.getInCondition(filestate) + ") ");
		}
		if (blcorpid != null && !"".equals(blcorpid)) {
			sb_sql.append(" and blcorpid in(" + tbutil.getInCondition(blcorpid) + ") ");
		}
		if (bsactid != null && !"".equals(bsactid)) {
			sb_sql.append(" and bsactid in(" + tbutil.getInCondition(bsactid) + ") ");
		}
		if (ictypeid != null && !"".equals(ictypeid)) {
			sb_sql.append(" and ictypeid in(" + tbutil.getInCondition(ictypeid) + ") ");
		}
		if (cmpfiletype != null && !"".equals(cmpfiletype)) {
			sb_sql.append(" and cmpfiletype in (" + tbutil.getInCondition(cmpfiletype) + ") ");//修改文件分类为多选下拉框【李春娟/2012-03-19】
		}
		sb_sql.append(" order by publishdate desc");

		HashVO[] vos = dmo.getHashVoArrayByDS(null, sb_sql.toString());

		//处理树型结构显示不同层级【李春娟/2012-03-12】
		ReportDMO reportDMO = new ReportDMO(); //
		reportDMO.addOneFieldFromOtherTree(vos, "业务活动(第1层)", "业务活动", "select id,name,parentid from bsd_bsact", 1, true, 1); //加上第1层
		reportDMO.addOneFieldFromOtherTree(vos, "业务活动(第2层)", "业务活动", "select id,name,parentid from bsd_bsact", 2, true, 1); //加上第2层

		reportDMO.addOneFieldFromOtherTree(vos, "内控要素(第1层)", "内控要素", "select id,name,parentid from bsd_icsys", 1, true, 1); //加上第1层
		reportDMO.addOneFieldFromOtherTree(vos, "内控要素(第2层)", "内控要素", "select id,name,parentid from bsd_icsys", 2, true, 1); //加上第2层

		reportDMO.addOneFieldFromOtherTree(vos, "所属机构(第1层)", "所属机构", "select id,name,parentid from pub_corp_dept", 2, true, 2); //从第2层开始 加上第2层
		reportDMO.addOneFieldFromOtherTree(vos, "所属机构(第2层)", "所属机构", "select id,name,parentid from pub_corp_dept", 3, true, 2); //从第2层开始 加上第3层

		new ReportUtil().leftOuterJoin_YSMDFromDateTime(vos, "发布日期", "发布日期", "季");//处理时间显示格式

		return vos;
	}

	public String[] getGroupFieldNames() {
		return new String[] { "文件分类", "文件状态", "所属机构(第1层)", "所属机构(第2层)", "业务活动(第1层)", "业务活动(第2层)", "内控要素(第1层)", "内控要素(第2层)", "发布日期" };
	}

	public String[] getSumFiledNames() {
		return new String[] { "数量" };
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid() {
		return getBeforehandGroupType(1);
	}

	@Override
	/**
	 * 钻取明细时的模板编码!!
	 */
	public String getDrillTempletCode() throws Exception {
		return "PUB_WF_PROCESS_CODE2"; //
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Chart() {
		return getBeforehandGroupType(2);
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType(int _type) {
		ArrayList al = new ArrayList(); //
		if (_type == 1) {
			al.add(getBeforehandGroupType("文件分类_所属机构(第1层)", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("文件分类_所属机构(第2层)", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("文件状态_所属机构(第1层)", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("文件状态_所属机构(第2层)", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("业务活动(第1层)_所属机构(第1层)", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("业务活动(第2层)_所属机构(第2层)", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("内控要素(第1层)_所属机构(第1层)", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("内控要素(第2层)_所属机构(第2层)", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("业务活动(第1层)_内控要素(第1层)", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("业务活动(第2层)_内控要素(第2层)", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("业务活动(第1层)_发布日期", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("业务活动(第2层)_发布日期", "数量", BeforeHandGroupTypeVO.COUNT));
		} else {//图表的分类个数最好是四个以上十个以下的才好看【李春娟/2012-03-12】
			al.add(getBeforehandGroupType("文件分类_所属机构(第1层)", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("文件状态_所属机构(第1层)", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("内控要素(第1层)_所属机构(第1层)", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("业务活动(第1层)_文件分类", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("内控要素(第1层)_文件分类", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("发布日期_所属机构(第1层)", "数量", BeforeHandGroupTypeVO.COUNT));
		}
		return (BeforeHandGroupTypeVO[]) al.toArray(new BeforeHandGroupTypeVO[0]);
	}

	public BeforeHandGroupTypeVO getBeforehandGroupType(String _name, String typeName, String _type) {
		String[] names = _name.split("_");
		BeforeHandGroupTypeVO typeVo = new BeforeHandGroupTypeVO();
		typeVo.setName(_name);
		typeVo.setRowHeaderGroupFields(new String[] { names[0] });
		typeVo.setColHeaderGroupFields(new String[] { names[1] });
		typeVo.setComputeGroupFields(new String[][] { { typeName, _type } });
		return typeVo;
	}

	//增加排序字段【李春娟/2012-03-29】
	public HashMap getGroupFieldOrderConfig() {
		HashMap map = new HashMap();
		try {
			CommDMO dmo = new CommDMO();
			String[] cmpfiletype = dmo.getStringArrayFirstColByDS(null, "select name from pub_comboboxdict where type='文件分类' order by seq");
			String[] filestate = dmo.getStringArrayFirstColByDS(null, "select name  from pub_comboboxdict where type='文件状态' order by seq");
			map.put("文件分类", cmpfiletype);
			map.put("文件状态", filestate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}
