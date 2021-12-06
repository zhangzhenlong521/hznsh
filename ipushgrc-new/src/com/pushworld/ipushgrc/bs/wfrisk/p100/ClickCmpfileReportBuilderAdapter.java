package com.pushworld.ipushgrc.bs.wfrisk.p100;

import java.util.ArrayList;
import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;

public class ClickCmpfileReportBuilderAdapter extends MultiLevelReportDataBuilderAdapter {

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
		RefItemVO ref_publishdate = (RefItemVO) condition.get("obj_publishdate");//发布日期,日期类型比较特殊,需要取得参照对象值,然后替换其中的{itemkey}

		//可以直接在模板查询面板中添加
		String ictypeid = (String) condition.get("ictypeid");//内控要素
		String userblcorpid = (String) condition.get("userblcorpid");//用户所属机构
		String lookcontent = (String) condition.get("lookcontent");//查看內容
		RefItemVO ref_clicktime = (RefItemVO) condition.get("obj_clicktime");//查看日期

		TBUtil tbutil = new TBUtil();
		StringBuffer sb_sql = new StringBuffer("select id,cmpfiletype 文件分类, blcorpname 文件所属机构,userblcorpname 用户所属机构,bsactname 业务活动,ictypename 内控要素  from v_cmpfile_clicklog where 1=1 ");

		if (cmpfiletype != null && !"".equals(cmpfiletype)) {
			sb_sql.append(" and cmpfiletype = '" + cmpfiletype + "' ");
		}
		if (blcorpid != null && !"".equals(blcorpid)) {
			sb_sql.append(" and blcorpid in(" + tbutil.getInCondition(blcorpid) + ") ");
		}
		if (bsactid != null && !"".equals(bsactid)) {
			sb_sql.append(" and bsactid in(" + tbutil.getInCondition(bsactid) + ") ");
		}
		//发布日期!!!
		if (ref_publishdate != null) {
			String str_cons = ref_publishdate.getHashVO().getStringValue("querycondition"); //取得查询条件itemkey
			str_cons = tbutil.replaceAll(str_cons, "{itemkey}", "publishdate"); //替换其中的特殊符号!为实际字段名!
			sb_sql.append(" and " + str_cons);
		}
		//查询过滤逻辑已实现，可以直接在模板查询面板中添加
		if (ictypeid != null && !"".equals(ictypeid)) {
			sb_sql.append(" and ictypeid in(" + tbutil.getInCondition(ictypeid) + ") ");
		}
		if (userblcorpid != null && !"".equals(userblcorpid)) {
			sb_sql.append(" and userblcorpid in(" + tbutil.getInCondition(userblcorpid) + ") ");
		}
		if (lookcontent != null && !"".equals(lookcontent)) {
			String[] contents = tbutil.split(lookcontent, ";");
			sb_sql.append(" and (" + contents[0] + "='Y' ");
			for (int i = 1; i < contents.length; i++) {
				sb_sql.append("or " + contents[i] + "='Y' ");
			}
			sb_sql.append(") ");
		}

		//查看日期!以前取得日期的方法有问题，多选或报错【李春娟/2012-03-19】
		if (ref_clicktime != null) {
			String str_cons = ref_clicktime.getHashVO().getStringValue("querycondition"); //取得查询条件itemkey
			str_cons = tbutil.replaceAll(str_cons, "{itemkey}", "clicktime"); //替换其中的特殊符号!为实际字段名!
			sb_sql.append(" and " + str_cons);
		}
		sb_sql.append(" order by publishdate desc");
		return dmo.getHashVoArrayByDS(null, sb_sql.toString());
	}

	public String[] getGroupFieldNames() {
		return new String[] { "文件分类", "文件所属机构", "用户所属机构", "业务活动", "内控要素" };
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
			al.add(getBeforehandGroupType("文件分类_文件所属机构", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("文件分类_用户所属机构", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("文件分类_业务活动", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("业务活动_内控要素", "数量", BeforeHandGroupTypeVO.COUNT));
		} else {
			al.add(getBeforehandGroupType("文件分类_业务活动", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("文件分类_内控要素", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("业务活动_文件所属机构", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("业务活动_用户所属机构", "数量", BeforeHandGroupTypeVO.COUNT));
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
}
