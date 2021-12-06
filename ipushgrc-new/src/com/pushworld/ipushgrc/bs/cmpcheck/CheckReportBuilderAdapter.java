package com.pushworld.ipushgrc.bs.cmpcheck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter;
import cn.com.infostrategy.bs.report.ReportDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;
import cn.com.infostrategy.to.report.ReportUtil;

/***
 * 检查模块的统计报表
 * @author Gwang
 *
 */

public class CheckReportBuilderAdapter extends MultiLevelReportDataBuilderAdapter {
	TBUtil tbutil = new TBUtil();

	@Override
	public HashVO[] buildReportData(HashMap condition) throws Exception {
		String[] colheader = (String[]) condition.get("obj_colheader"); // 获取列表头的值
		StringBuffer con = new StringBuffer(); //sql语句
		//下面sql先前关联有问题【李春娟/2013-06-20】，由于之前钻取时，用的ID是cmp_check_id,会出现多个重复值,这里重新修改视图和ID字段[YangQing/2013-09-17]
		con.append("select v.id,v.cmp_check_id,v.checkname 检查名称 , v.findchannel 发现渠道, v.checkcorp deptid ,v.checkbegindate 检查日期,v.eventcorpid 事件发生单位, v.eventType 事件类型 from v_report_check2 v left join pub_corp_dept p "
				+ " on v.checkcorp = p.id left join cmp_check e on v.cmp_check_id = e.id where 1=1");

		//上面只拼接了"checkname"条件,不能满足查询条件,下面加上四个条件.【杨庆/2012-07-18】
		String checkcorp = (String) condition.get("checkcorp");//检查单位
		String checkedcorp = (String) condition.get("checkedcorp");//被检查单位
		RefItemVO checkbegindate = (RefItemVO) condition.get("obj_checkbegindate");//检查开始日期,日期类型比较特殊,需要取得参照对象值,然后替换其中的{itemkey}
		String status = (String) condition.get("status");//状态
		//检查单位
		if (checkcorp != null && !checkcorp.equals("")) {
			con.append(" and e.checkcorp in (" + tbutil.getInCondition(checkcorp) + ")");
		}
		//被检查单位
		if (checkedcorp != null && !checkedcorp.equals("")) {
			con.append(getMultiOrCondition("checkedcorp", checkedcorp));
		}
		//检查开始日期
		if (checkbegindate != null) {
			String str_cons = checkbegindate.getHashVO().getStringValue("querycondition"); //取得查询条件itemkey
			str_cons = tbutil.replaceAll(str_cons, "{itemkey}", "e.checkbegindate"); //替换其中的特殊符号!为实际字段名!
			con.append(" and " + str_cons);
		}
		//状态
		if (status != null && !status.equals("")) {
			con.append(" and e.status in (" + tbutil.getInCondition(status) + ")");
		}
		if (condition.containsKey("checkname")) {
			String s = (String) condition.get("checkname");
			if (s != null && !s.equals("")) {
				con.append(" and checkname like '%" + s + "%' ");
			}
		}
		con.append(" order by p.linkcode,e.checkbegindate");

		CommDMO comm = new CommDMO();
		HashVO vos[] = comm.getHashVoArrayByDS(null, con.toString());
		ReportUtil util = new ReportUtil();
		util.leftOuterJoin_YSMDFromDateTime(vos, "检查日期", "检查日期", "季");
		ReportDMO dmo = new ReportDMO();
		dmo.leftOuterJoin_TreeTableFieldName(vos, "检查单位", "pub_corp_dept", "name", "id", "deptid", "id", "parentid", 2, true);

		for (int i = 0; i < colheader.length; i++) {//根据统计表格的列表头，来判断，是否有根据“事件发生单位”来统计，如果有，就执行方法处理多个发生单位的数据。否则，不执行，如维度“检查单位_检查日期”。此处如果更改了维度，这里也要查看是否需要修改！！[YangQing/2013-0918]
			if (colheader[i].equals("事件发生单位")) {
				vos = getMutityHashVOByItem(vos, "事件发生单位");
				break;
			}
		}
		dmo.leftOuterJoin_TreeTableFieldName(vos, "事件发生单位", "pub_corp_dept", "name", "id", "事件发生单位", "id", "parentid", 2, true);

		return vos;
	}

	public HashVO[] getMutityHashVOByItem(HashVO[] _vos, String _item) {
		TBUtil tbutil = new TBUtil();
		List list = new ArrayList();
		for (int i = 0; i < _vos.length; i++) {
			HashVO vo = _vos[i];
			String str = vo.getStringValue(_item);
			if (str != null && !str.trim().equals("")) {
				String[] values = tbutil.split(str, ";");
				for (int j = 0; j < values.length; j++) {
					HashVO vonew = vo.deepClone();
					vonew.setAttributeValue(_item, values[j]);
					list.add(vonew);
				}
			} else {
				list.add(vo);
			}
		}
		return (HashVO[]) list.toArray(new HashVO[0]);
	}

	@Override
	public String[] getGroupFieldNames() {
		return new String[] { "检查名称", "发现渠道", "检查单位", "检查日期", "事件类型", "事件发生单位" };
	}

	@Override
	public String[] getSumFiledNames() {
		return new String[] { "问题数量" };
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid() {
		ArrayList al = new ArrayList();
		BeforeHandGroupTypeVO vo = null;
		vo = new BeforeHandGroupTypeVO();
		vo.setName("检查单位_检查日期");
		vo.setRowHeaderGroupFields(new String[] { "检查日期" });
		vo.setColHeaderGroupFields(new String[] { "检查单位" });
		vo.setComputeGroupFields(new String[][] { { "问题数量", BeforeHandGroupTypeVO.COUNT } });
		al.add(vo);

		vo = new BeforeHandGroupTypeVO();
		vo.setName("事件发生单位_事件类型");
		vo.setRowHeaderGroupFields(new String[] { "事件类型" });
		vo.setColHeaderGroupFields(new String[] { "事件发生单位" });
		vo.setComputeGroupFields(new String[][] { { "问题数量", BeforeHandGroupTypeVO.COUNT } });
		al.add(vo);

		vo = new BeforeHandGroupTypeVO();
		vo.setName("事件发生单位_检查日期_事件类型");
		vo.setRowHeaderGroupFields(new String[] { "检查日期", "事件类型" });
		vo.setColHeaderGroupFields(new String[] { "事件发生单位" });
		vo.setComputeGroupFields(new String[][] { { "问题数量", BeforeHandGroupTypeVO.COUNT } });
		al.add(vo);

		vo = new BeforeHandGroupTypeVO();
		vo.setName("事件发生单位_检查日期_发现渠道");
		vo.setRowHeaderGroupFields(new String[] { "检查日期", "发现渠道" });
		vo.setColHeaderGroupFields(new String[] { "事件发生单位" });
		vo.setComputeGroupFields(new String[][] { { "问题数量", BeforeHandGroupTypeVO.COUNT } });
		al.add(vo);

		return (BeforeHandGroupTypeVO[]) al.toArray(new BeforeHandGroupTypeVO[0]);
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Chart() {
		ArrayList al = new ArrayList();
		BeforeHandGroupTypeVO vo = null;

		vo = new BeforeHandGroupTypeVO();
		vo.setName("事件发生单位_事件类型");
		vo.setRowHeaderGroupFields(new String[] { "事件类型" });
		vo.setColHeaderGroupFields(new String[] { "事件发生单位" });
		vo.setComputeGroupFields(new String[][] { { "问题数量", BeforeHandGroupTypeVO.COUNT } });
		al.add(vo);

		vo = new BeforeHandGroupTypeVO();
		vo.setName("事件发生单位_检查日期");
		vo.setRowHeaderGroupFields(new String[] { "检查日期" });
		vo.setColHeaderGroupFields(new String[] { "事件发生单位" });
		vo.setComputeGroupFields(new String[][] { { "问题数量", BeforeHandGroupTypeVO.COUNT } });
		al.add(vo);

		return (BeforeHandGroupTypeVO[]) al.toArray(new BeforeHandGroupTypeVO[0]);

	}

	public String getMultiOrCondition(String key, String _condition) {
		StringBuffer sb_sql = new StringBuffer();
		String[] tempid = tbutil.split(_condition, ";"); // str_realvalue.split(";");
		if (tempid != null && tempid.length > 0) {
			sb_sql.append(" and (");
			for (int j = 0; j < tempid.length; j++) {
				sb_sql.append(key + " like '%;" + tempid[j] + ";%'"); // 
				if (j != tempid.length - 1) { //
					sb_sql.append(" or ");
				}
			}
			sb_sql.append(") "); //
		}
		return sb_sql.toString();
	}

	/**
	 * 钻取显示详细数据[YangQing/2013-09-17]
	 */
	public String getDrillActionClassPath() throws Exception {
		return "com.pushworld.ipushgrc.ui.cmpcheck.p060.CheckReportDrill";
	}
}
