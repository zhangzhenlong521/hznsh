package cn.com.pushworld.salary.bs.dmo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.pushworld.salary.to.SalaryTBUtil;

/**
 * 指标DMO
 * 
 * @author haoming create by 2014-5-5
 */

public class TargetDMO extends SalaryAbstractCommDMO {
	public BillCellVO getDeptTargetQueryCellVO(HashMap hashMap) throws Exception {
		String querytype = (String) hashMap.get("querytype");
		if (!getTb().isEmpty(querytype) && querytype.contains("业务")) {
			return getDeptTargetQueryCellVOByCatalog(hashMap);
		}
		return getDeptTargetQueryCellVOByDept(hashMap);
	}

	/*
	 * 根据部门，指标类型查询指标。用billcellpanel展示。
	 */
	public BillCellVO getDeptTargetQueryCellVOByDept(HashMap hashMap) throws Exception {
		// 查出所有的指标
		String state = (String) hashMap.get("state");
		HashVO hvos[] = null;
		if (state == null || state.equals("")) {
			hvos = getDmo().getHashVoArrayByDS(null, "select t1.*,t2.name catalogname from sal_target_list t1 left join sal_target_catalog t2 on t1.catalogid = t2.id  order by t1.type,t1.state desc");
		} else {
			hvos = getDmo().getHashVoArrayByDS(null, "select t1.*,t2.name catalogname from sal_target_list t1 left join sal_target_catalog t2 on t1.catalogid = t2.id  where t1.state ='" + state + "'order by t1.type,t1.state desc");
		}
		BillCellVO cellvo = new BillCellVO();
		if (hvos == null || hvos.length < 1) {
			cellvo.setRowlength(1);
			cellvo.setCollength(1);
			BillCellItemVO[][] items = new BillCellItemVO[1][1];
			items[0][0] = getBillTitleCellItemVO("没有查询到相关信息");
			items[0][0].setColwidth("300");
			cellvo.setCellItemVOs(items);
			return cellvo;
		}
		HashMap<String, HashVO> target_map = new HashMap<String, HashVO>(); // 指标缓存。
		List dltarget = new ArrayList();
		for (int i = 0; i < hvos.length; i++) {
			target_map.put(hvos[i].getStringValue("id"), hvos[i]);
			if ("部门定量指标".equals(hvos[i].getStringValue("type"))) { // 找出定量指标,和定性的考核部门存放字段不一样。单独查
				dltarget.add(hvos[i].getStringValue("id"));
			}
		}
		TBUtil tbutil = new TBUtil();
		// 找出指标对应的被考核部门 用union all 把定量和定性的放一起。
		HashMap targets_deptids = getDmo().getHashMapBySQLByDS(null, "select id targetid,checkeddept deptid from sal_target_list where type='部门定性指标' union all " + "select targetid, deptid from sal_target_checkeddept where  targetid in (" + tbutil.getInCondition(dltarget) + ")", true);
		HashVO[] dept_name = getDmo().getHashVoArrayByDS(null, "select id,name from pub_corp_dept order by linkcode");// 机构缓存
		HashVO[] post_name = getDmo().getHashVoArrayByDS(null, "select id,name from pub_post");// 岗位缓存
		LinkedHashMap<String, LinkedHashSet<String>> dept_target = new LinkedHashMap<String, LinkedHashSet<String>>(); // 存放部门对应的指标
		for (int i = 0; i < hvos.length; i++) {
			HashVO targetvo = hvos[i];
			String targetid = targetvo.getStringValue("id");
			if (targets_deptids.containsKey(targetid)) {
				String deptids = (String) targets_deptids.get(targetid);
				String depts[] = tbutil.split(deptids, ";");
				if (depts.length > 5) { // 被考核机构大于5个认为是通用指标。
					if (dept_target.containsKey("通用")) {
						LinkedHashSet list = dept_target.get("通用");
						list.add(targetid);
					} else {
						LinkedHashSet list = new LinkedHashSet();
						list.add(targetid);
						dept_target.put("通用", list);
					}
					continue;
				}
				for (int j = 0; j < depts.length; j++) {
					if (dept_target.containsKey(depts[j])) {
						LinkedHashSet list = dept_target.get(depts[j]);
						list.add(targetid);
					} else {
						LinkedHashSet list = new LinkedHashSet();
						list.add(targetid);
						dept_target.put(depts[j], list);
					}
				}

			}
		}
		List<BillCellItemVO[]> rows = new ArrayList<BillCellItemVO[]>(); // 表头
		BillCellItemVO item_0 = getBillTitleCellItemVO("部门");
		BillCellItemVO item_1 = getBillTitleCellItemVO("类型");
		BillCellItemVO item_1_1 = getBillTitleCellItemVO("指标分类");
		BillCellItemVO item_2 = getBillTitleCellItemVO("名称");
		BillCellItemVO item_3 = getBillTitleCellItemVO("描述");
		BillCellItemVO item_4 = getBillTitleCellItemVO("评分标准");
		BillCellItemVO item_5 = getBillTitleCellItemVO("权重");
		BillCellItemVO item_6 = getBillTitleCellItemVO("状态");
		BillCellItemVO item_7 = getBillTitleCellItemVO("数据来源部门");
		BillCellItemVO item_8 = getBillTitleCellItemVO("考核频率");
		BillCellItemVO item_9 = getBillTitleCellItemVO("主管领导");
		BillCellItemVO item_10 = getBillTitleCellItemVO("分管领导");
		BillCellItemVO item_11 = getBillTitleCellItemVO("指标来源");

		rows.add(new BillCellItemVO[] { item_0, item_1, item_1_1, item_2, item_3, item_4, item_5, item_6, item_7, item_8, item_9, item_10, item_11 });// 表头

		int index = 0;
		if (true) { // 把通用的弄出来
			LinkedHashSet target = dept_target.get("通用");
			String targetids[] = (String[]) target.toArray(new String[0]);
			for (int j = 0; j < targetids.length; j++) {
				HashVO targetevo = target_map.get(targetids[j]);
				BillCellItemVO bitem_0 = getBillNormalCellItemVO(0, "通用指标");
				BillCellItemVO bitem_1 = getBillNormalCellItemVO(index, targetevo.getStringValue("type").substring(2, 4));
				BillCellItemVO bitem_1_1 = getBillNormalCellItemVO(index, targetevo.getStringValue("catalogname"));
				BillCellItemVO bitem_2 = getBillNormalCellItemVO(index, targetevo.getStringValue("name"));
				BillCellItemVO bitem_3 = getBillNormalCellItemVO(index, targetevo.getStringValue("descr"));
				BillCellItemVO bitem_4 = getBillNormalCellItemVO(index, targetevo.getStringValue("evalstandard"));
				bitem_3.setCelltype("TEXTAREA");
				bitem_3.setRowheight("30");
				BillCellItemVO bitem_5 = getBillNormalCellItemVO(index, targetevo.getStringValue("weights"));
				BillCellItemVO bitem_6 = getBillNormalCellItemVO(index, targetevo.getStringValue("state"));
				BillCellItemVO bitem_7 = getBillNormalCellItemVO(index, getvaluenames(targetevo.getStringValue("dbsource"), dept_name));
				BillCellItemVO bitem_8 = getBillNormalCellItemVO(index, targetevo.getStringValue("checkcycle"));
				BillCellItemVO bitem_9 = getBillNormalCellItemVO(index, getvaluenames(targetevo.getStringValue("rleader"), post_name));
				BillCellItemVO bitem_10 = getBillNormalCellItemVO(index, getvaluenames(targetevo.getStringValue("pleader"), post_name));
				BillCellItemVO bitem_11 = getBillNormalCellItemVO(index, targetevo.getStringValue("targetsource"));
				bitem_11.setCustProperty("id", targetevo.getStringValue("id"));
				bitem_11.setCustProperty("type", targetevo.getStringValue("type"));
				index++;
				rows.add(new BillCellItemVO[] { bitem_0, bitem_1, bitem_1_1, bitem_2, bitem_3, bitem_4, bitem_5, bitem_6, bitem_7, bitem_8, bitem_9, bitem_10, bitem_11 });
			}
		}

		for (int i = 0; i < dept_name.length; i++) { //
			HashVO deptvo = dept_name[i];
			String deptid = deptvo.getStringValue("id");
			String deptname = deptvo.getStringValue("name");
			if (dept_target.containsKey(deptid)) {
				LinkedHashSet target = dept_target.get(deptid);
				String targetids[] = (String[]) target.toArray(new String[0]);
				for (int j = 0; j < targetids.length; j++) {
					HashVO targetevo = target_map.get(targetids[j]);
					BillCellItemVO bitem_0 = getBillNormalCellItemVO(0, deptname);
					BillCellItemVO bitem_1 = getBillNormalCellItemVO(index, targetevo.getStringValue("type").substring(2, 4));
					BillCellItemVO bitem_1_1 = getBillNormalCellItemVO(index, targetevo.getStringValue("catalogname"));
					BillCellItemVO bitem_2 = getBillNormalCellItemVO(index, targetevo.getStringValue("name"));
					BillCellItemVO bitem_3 = getBillNormalCellItemVO(index, targetevo.getStringValue("descr"));
					BillCellItemVO bitem_4 = getBillNormalCellItemVO(index, targetevo.getStringValue("evalstandard"));
					bitem_3.setCelltype("TEXTAREA");
					bitem_3.setRowheight("30");
					BillCellItemVO bitem_5 = getBillNormalCellItemVO(index, targetevo.getStringValue("weights"));
					BillCellItemVO bitem_6 = getBillNormalCellItemVO(index, targetevo.getStringValue("state"));
					String dbsourcedept = targetevo.getStringValue("dbsource");
					StringBuilder dbsourcenames = new StringBuilder();
					BillCellItemVO bitem_7 = getBillNormalCellItemVO(index, getvaluenames(targetevo.getStringValue("dbsource"), dept_name));
					BillCellItemVO bitem_8 = getBillNormalCellItemVO(index, targetevo.getStringValue("checkcycle"));
					BillCellItemVO bitem_9 = getBillNormalCellItemVO(index, getvaluenames(targetevo.getStringValue("rleader"), post_name));
					BillCellItemVO bitem_10 = getBillNormalCellItemVO(index, getvaluenames(targetevo.getStringValue("pleader"), post_name));
					BillCellItemVO bitem_11 = getBillNormalCellItemVO(index, targetevo.getStringValue("targetsource"));
					bitem_11.setCustProperty("id", targetevo.getStringValue("id"));
					bitem_11.setCustProperty("type", targetevo.getStringValue("type"));
					index++;
					rows.add(new BillCellItemVO[] { bitem_0, bitem_1, bitem_1_1, bitem_2, bitem_3, bitem_4, bitem_5, bitem_6, bitem_7, bitem_8, bitem_9, bitem_10, bitem_11 });
				}
			}
		}
		BillCellItemVO[][] cells = rows.toArray(new BillCellItemVO[0][0]);
		formatSpan(cells, new int[] { 0, 1 });
		formatClen(cells);
		cellvo.setCellItemVOs(cells);
		cellvo.setCollength(13);
		cellvo.setRowlength(rows.size());
		return cellvo;
	}

	/*
	 * 通过分类查询
	 */
	public BillCellVO getDeptTargetQueryCellVOByCatalog(HashMap hashMap) throws Exception {
		String state = (String) hashMap.get("state");
		BillCellVO cellvo = new BillCellVO();
		HashVO hvos[] = null;
		if (state == null || state.equals("")) {
			hvos = getDmo().getHashVoArrayByDS(null, "select t1.*,t2.name catalogname from sal_target_list t1 left join sal_target_catalog t2 on t1.catalogid = t2.id  order by t2.id,t1.type,t1.code,t1.state desc");
		} else {
			hvos = getDmo().getHashVoArrayByDS(null, "select t1.*,t2.name catalogname from sal_target_list t1 left join sal_target_catalog t2 on t1.catalogid = t2.id  where t1.state ='" + state + "'order by t2.id,t1.type,t1.code,t1.state desc");
		}
		if (hvos == null || hvos.length < 1) {
			cellvo.setRowlength(1);
			cellvo.setCollength(1);
			BillCellItemVO[][] items = new BillCellItemVO[1][1];
			items[0][0] = getBillTitleCellItemVO("没有查询到相关信息");
			items[0][0].setColwidth("300");
			cellvo.setCellItemVOs(items);
			return cellvo;
		}
		List dltarget = new ArrayList();
		for (int i = 0; i < hvos.length; i++) {
			if ("部门定量指标".equals(hvos[i].getStringValue("type"))) { // 找出定量指标,和定性的考核部门存放字段不一样。单独查
				dltarget.add(hvos[i].getStringValue("id"));
			}
		}
		HashMap targets_deptids = getDmo().getHashMapBySQLByDS(null, "select id targetid,checkeddept deptid from sal_target_list where type='部门定性指标' union all " + "select targetid, deptid from sal_target_checkeddept where  targetid in (" + TBUtil.getTBUtil().getInCondition(dltarget) + ")", true);

		LinkedHashMap<String, List> map = new LinkedHashMap();
		for (int i = 0; i < hvos.length; i++) {
			String catalog = hvos[i].getStringValue("catalogname", "暂无分类");
			if (map.containsKey(catalog)) {
				List l = map.get(catalog);
				l.add(hvos[i]);
			} else {
				List l = new ArrayList<HashVO>();
				l.add(hvos[i]);
				map.put(catalog, l);
			}
		}
		HashVO[] dept_name = getDmo().getHashVoArrayByDS(null, "select id,name from pub_corp_dept order by linkcode");// 机构缓存
		HashVO[] post_name = getDmo().getHashVoArrayByDS(null, "select id,name from pub_post");// 岗位缓存
		List<BillCellItemVO[]> rows = new ArrayList<BillCellItemVO[]>(); // 表头
		BillCellItemVO item_0 = getBillTitleCellItemVO("指标分类");
		BillCellItemVO item_1 = getBillTitleCellItemVO("类型");
		BillCellItemVO item_1_1 = getBillTitleCellItemVO("编码");
		BillCellItemVO item_2 = getBillTitleCellItemVO("名称");
		BillCellItemVO item_3 = getBillTitleCellItemVO("描述");
		BillCellItemVO item_4 = getBillTitleCellItemVO("评分标准");
		BillCellItemVO item_5 = getBillTitleCellItemVO("权重");
		BillCellItemVO item_5_1 = getBillTitleCellItemVO("被考核部门");
		BillCellItemVO item_6 = getBillTitleCellItemVO("状态");
		BillCellItemVO item_7 = getBillTitleCellItemVO("数据来源部门");
		BillCellItemVO item_8 = getBillTitleCellItemVO("考核频率");
		BillCellItemVO item_9 = getBillTitleCellItemVO("主管领导");
		BillCellItemVO item_10 = getBillTitleCellItemVO("分管领导");
		BillCellItemVO item_11 = getBillTitleCellItemVO("指标来源");

		rows.add(new BillCellItemVO[] { item_0, item_1, item_1_1, item_2, item_3, item_4, item_5, item_5_1, item_6, item_7, item_8, item_9, item_10, item_11 });// 表头
		int index = 0;
		for (int i = 0; i < hvos.length; i++) {
			HashVO targetevo = hvos[i];
			BillCellItemVO bitem_0 = getBillNormalCellItemVO(index, targetevo.getStringValue("catalogname"));
			BillCellItemVO bitem_1 = getBillNormalCellItemVO(index, targetevo.getStringValue("type").substring(2, 4));
			BillCellItemVO bitem_1_1 = getBillNormalCellItemVO(index, targetevo.getStringValue("code"));
			BillCellItemVO bitem_2 = getBillNormalCellItemVO(index, targetevo.getStringValue("name"));
			BillCellItemVO bitem_3 = getBillNormalCellItemVO(index, targetevo.getStringValue("descr"));
			BillCellItemVO bitem_4 = getBillNormalCellItemVO(index, targetevo.getStringValue("evalstandard"));
			bitem_3.setCelltype("TEXTAREA");
			bitem_3.setRowheight("30");
			BillCellItemVO bitem_5 = getBillNormalCellItemVO(index, targetevo.getStringValue("weights"));
			BillCellItemVO bitem_5_1 = getBillNormalCellItemVO(index, getvaluenames((String) targets_deptids.get(targetevo.getStringValue("id")), dept_name));
			BillCellItemVO bitem_6 = getBillNormalCellItemVO(index, targetevo.getStringValue("state"));
			String dbsourcedept = targetevo.getStringValue("dbsource");
			StringBuilder dbsourcenames = new StringBuilder();
			BillCellItemVO bitem_7 = getBillNormalCellItemVO(index, getvaluenames(targetevo.getStringValue("dbsource"), dept_name));
			BillCellItemVO bitem_8 = getBillNormalCellItemVO(index, targetevo.getStringValue("checkcycle"));
			BillCellItemVO bitem_9 = getBillNormalCellItemVO(index, getvaluenames(targetevo.getStringValue("rleader"), post_name));
			BillCellItemVO bitem_10 = getBillNormalCellItemVO(index, getvaluenames(targetevo.getStringValue("pleader"), post_name));
			BillCellItemVO bitem_11 = getBillNormalCellItemVO(index, targetevo.getStringValue("targetsource"));
			bitem_11.setCustProperty("id", targetevo.getStringValue("id"));
			bitem_11.setCustProperty("type", targetevo.getStringValue("type"));
			index++;
			rows.add(new BillCellItemVO[] { bitem_0, bitem_1, bitem_1_1, bitem_2, bitem_3, bitem_4, bitem_5, bitem_5_1, bitem_6, bitem_7, bitem_8, bitem_9, bitem_10, bitem_11 });

		}
		BillCellItemVO[][] cells = rows.toArray(new BillCellItemVO[0][0]);
		formatSpan(cells, new int[] { 0, 1 });
		formatClen(cells);
		cellvo.setCellItemVOs(cells);
		cellvo.setCollength(14);
		cellvo.setRowlength(rows.size());
		return cellvo;
	}

	public BillCellVO getPersonPostTargetQueryCellVO(HashMap hashMap) throws Exception {
		String querytype = (String) hashMap.get("querytype");
		if (!getTb().isEmpty(querytype) && querytype.contains("业务")) {
			return getPersonPostTargetQueryCellVOByCatalogType(hashMap);
		}
		return getPersonPostTargetQueryCellVOByPost(hashMap);
	}

	/*
	 * 搞人员定量指标
	 */
	public BillCellVO getPersonPostTargetQueryCellVOByPost(HashMap hashMap) throws Exception {
		String state = (String) hashMap.get("state");
		HashVO[] dept_name = getDmo().getHashVoArrayByDS(null, "select id,name from pub_corp_dept order by linkcode");// 机构缓存
		HashVO[] un_name = getDmo().getHashVoArrayByDS(null, "select id,name from pub_comboboxdict where type = '薪酬_计划值单位' order by seq");// 计划值单位缓存
		BillCellVO cellvo = new BillCellVO();
		HashVO hvos[] = null;
		if (state == null || state.equals("")) {
			hvos = getDmo().getHashVoArrayByDS(null, "select t1.*,t2.name catalogname,t3.weights weight ,t3.postid postnames from sal_person_check_list t1 left join sal_target_catalog t2 on t1.catalogid = t2.id  left join sal_person_check_post t3 on t3.targetid = t1.id where t1.targettype='员工定量指标' order by t1.type,t1.state desc");
		} else {
			hvos = getDmo().getHashVoArrayByDS(null, "select t1.*,t2.name catalogname,t3.weights weight ,t3.postid postnames from sal_person_check_list t1 left join sal_target_catalog t2 on t1.catalogid = t2.id  left join sal_person_check_post t3 on t3.targetid = t1.id where t1.targettype='员工定量指标' and t1.state = '" + state + "' order by t1.type,t1.state desc");
		}
		if (hvos == null || hvos.length < 1) {
			cellvo.setRowlength(1);
			cellvo.setCollength(1);
			BillCellItemVO[][] items = new BillCellItemVO[1][1];
			items[0][0] = getBillTitleCellItemVO("没有查询到相关信息");
			items[0][0].setColwidth("300");
			cellvo.setCellItemVOs(items);
			return cellvo;
		}
		HashMap<String, HashVO> target_map = new HashMap<String, HashVO>(); // 指标缓存。
		HashMap weightmap = new HashMap();
		List dltarget = new ArrayList();
		for (int i = 0; i < hvos.length; i++) {
			target_map.put(hvos[i].getStringValue("id"), hvos[i]);
		}
		TBUtil tbutil = new TBUtil();
		HashVO[] post_name = getDmo().getHashVoArrayByDS(null, "select id,name from pub_comboboxdict where type = '薪酬_岗位归类' order by seq");// 岗位组缓存

		LinkedHashMap<String, LinkedHashSet<String>> post_target = new LinkedHashMap<String, LinkedHashSet<String>>(); // 存放部门对应的指标
		for (int i = 0; i < hvos.length; i++) {
			HashVO targetvo = hvos[i];
			String targetid = targetvo.getStringValue("id");
			String postids = targetvo.getStringValue("postnames");
			String posts[] = tbutil.split(postids, ";");
			for (int j = 0; j < posts.length; j++) {
				if (post_target.containsKey(posts[j])) {
					LinkedHashSet list = post_target.get(posts[j]);
					list.add(targetid);
					weightmap.put(posts[j] + targetid, targetvo.getStringValue("weight"));
				} else {
					LinkedHashSet list = new LinkedHashSet();
					list.add(targetid);
					post_target.put(posts[j], list);
					weightmap.put(posts[j] + targetid, targetvo.getStringValue("weight"));
				}
			}
		}

		List<BillCellItemVO[]> rows = new ArrayList<BillCellItemVO[]>(); // 表头
		BillCellItemVO item_0 = getBillTitleCellItemVO("岗位分类");
		BillCellItemVO item_1_1 = getBillTitleCellItemVO("分类");
		BillCellItemVO item_2 = getBillTitleCellItemVO("名称");
		BillCellItemVO item_3 = getBillTitleCellItemVO("描述");
		BillCellItemVO item_4 = getBillTitleCellItemVO("评分标准");
		BillCellItemVO item_5 = getBillTitleCellItemVO("权重");
		BillCellItemVO item_6 = getBillTitleCellItemVO("状态");
		BillCellItemVO item_7 = getBillTitleCellItemVO("数据来源部门");
		BillCellItemVO item_8 = getBillTitleCellItemVO("考核频率");
		BillCellItemVO item_9 = getBillTitleCellItemVO("运算符");
		BillCellItemVO item_10 = getBillTitleCellItemVO("单位");

		rows.add(new BillCellItemVO[] { item_0, item_1_1, item_2, item_3, item_4, item_5, item_6, item_7, item_8, item_9, item_10 });// 表头

		int index = 0;
		for (int i = 0; i < post_name.length; i++) { //
			HashVO postvo = post_name[i];
			String postid = postvo.getStringValue("id");
			String postname = postvo.getStringValue("name");
			if (post_target.containsKey(postid)) {
				LinkedHashSet target = post_target.get(postid);
				String targetids[] = (String[]) target.toArray(new String[0]);
				for (int j = 0; j < targetids.length; j++) {
					HashVO targetevo = target_map.get(targetids[j]);
					BillCellItemVO bitem_0 = getBillNormalCellItemVO(0, postname);
					BillCellItemVO bitem_1_1 = getBillNormalCellItemVO(index, targetevo.getStringValue("catalogname"));
					BillCellItemVO bitem_2 = getBillNormalCellItemVO(index, targetevo.getStringValue("name"));
					BillCellItemVO bitem_3 = getBillNormalCellItemVO(index, targetevo.getStringValue("descr"));
					BillCellItemVO bitem_4 = getBillNormalCellItemVO(index, targetevo.getStringValue("evalstandard"));
					bitem_3.setCelltype("TEXTAREA");
					bitem_3.setRowheight("30");
					BillCellItemVO bitem_5 = getBillNormalCellItemVO(index, (String) weightmap.get(postid + targetids[j]));
					BillCellItemVO bitem_6 = getBillNormalCellItemVO(index, targetevo.getStringValue("state"));
					BillCellItemVO bitem_7 = getBillNormalCellItemVO(index, getvaluenames(targetevo.getStringValue("dbsource"), dept_name));
					BillCellItemVO bitem_8 = getBillNormalCellItemVO(index, targetevo.getStringValue("checkcycle"));
					BillCellItemVO bitem_9 = getBillNormalCellItemVO(index, targetevo.getStringValue("operationtype"));
					BillCellItemVO bitem_10 = getBillNormalCellItemVO(index, getvaluenames(targetevo.getStringValue("unitvalue"), un_name));
					bitem_10.setCustProperty("id", targetevo.getStringValue("id"));
					bitem_10.setCustProperty("type", targetevo.getStringValue("targettype"));
					index++;
					rows.add(new BillCellItemVO[] { bitem_0, bitem_1_1, bitem_2, bitem_3, bitem_4, bitem_5, bitem_6, bitem_7, bitem_8, bitem_9, bitem_10 });
				}
			}
		}
		BillCellItemVO[][] cells = rows.toArray(new BillCellItemVO[0][0]);
		formatSpan(cells, new int[] { 0, 1 });
		formatClen(cells);
		cellvo.setCellItemVOs(cells);
		cellvo.setCollength(11);
		cellvo.setRowlength(rows.size());
		return cellvo;
	}

	/*
	 * 搞人员定量指标
	 */
	public BillCellVO getPersonPostTargetQueryCellVOByCatalogType(HashMap hashMap) throws Exception {
		String state = (String) hashMap.get("state");
		HashVO[] dept_name = getDmo().getHashVoArrayByDS(null, "select id,name from pub_corp_dept order by linkcode");// 机构缓存
		HashVO[] un_name = getDmo().getHashVoArrayByDS(null, "select id,name from pub_comboboxdict where type = '薪酬_计划值单位' order by seq");// 计划值单位缓存
		BillCellVO cellvo = new BillCellVO();
		HashVO hvos[] = null;
		if (state == null || state.equals("")) {
			hvos = getDmo().getHashVoArrayByDS(null, "select t1.*,t2.name catalogname,t3.weights weight ,t3.postid postnames from sal_person_check_list t1 left join sal_target_catalog t2 on t1.catalogid = t2.id  left join sal_person_check_post t3 on t3.targetid = t1.id where t1.targettype='员工定量指标' order by t2.id,t1.type,t1.state desc");
		} else {
			hvos = getDmo().getHashVoArrayByDS(null, "select t1.*,t2.name catalogname,t3.weights weight ,t3.postid postnames from sal_person_check_list t1 left join sal_target_catalog t2 on t1.catalogid = t2.id  left join sal_person_check_post t3 on t3.targetid = t1.id where t1.targettype='员工定量指标' and t1.state = '" + state + "' order by t2.id,t1.type,t1.state desc");
		}
		if (hvos == null || hvos.length < 1) {
			cellvo.setRowlength(1);
			cellvo.setCollength(1);
			BillCellItemVO[][] items = new BillCellItemVO[1][1];
			items[0][0] = getBillTitleCellItemVO("没有查询到相关信息");
			items[0][0].setColwidth("300");
			cellvo.setCellItemVOs(items);
			return cellvo;
		}
		HashMap<String, HashVO> target_map = new HashMap<String, HashVO>(); // 指标缓存。
		HashMap weightmap = new HashMap();
		List dltarget = new ArrayList();
		for (int i = 0; i < hvos.length; i++) {
			target_map.put(hvos[i].getStringValue("id"), hvos[i]);
		}
		TBUtil tbutil = new TBUtil();
		HashVO[] post_name = getDmo().getHashVoArrayByDS(null, "select id,name from pub_comboboxdict where type = '薪酬_岗位归类' order by seq");// 岗位组缓存

		LinkedHashMap<String, LinkedHashSet<String>> post_target = new LinkedHashMap<String, LinkedHashSet<String>>(); // 存放部门对应的指标
		for (int i = 0; i < hvos.length; i++) {
			HashVO targetvo = hvos[i];
			String targetid = targetvo.getStringValue("id");
			String postids = targetvo.getStringValue("postnames");
			String posts[] = tbutil.split(postids, ";");
			for (int j = 0; j < posts.length; j++) {
				if (post_target.containsKey(posts[j])) {
					LinkedHashSet list = post_target.get(posts[j]);
					list.add(targetid);
					weightmap.put(posts[j] + targetid, targetvo.getStringValue("weight"));
				} else {
					LinkedHashSet list = new LinkedHashSet();
					list.add(targetid);
					post_target.put(posts[j], list);
					weightmap.put(posts[j] + targetid, targetvo.getStringValue("weight"));
				}
			}
		}

		List<BillCellItemVO[]> rows = new ArrayList<BillCellItemVO[]>(); // 表头
		BillCellItemVO item_0 = getBillTitleCellItemVO("分类");
		BillCellItemVO item_1 = getBillTitleCellItemVO("编码");
		BillCellItemVO item_2 = getBillTitleCellItemVO("名称");
		BillCellItemVO item_3 = getBillTitleCellItemVO("描述");
		BillCellItemVO item_4 = getBillTitleCellItemVO("评分标准");
		BillCellItemVO item_5 = getBillTitleCellItemVO("权重");
		BillCellItemVO item_5_1 = getBillTitleCellItemVO("被考核岗位");
		BillCellItemVO item_6 = getBillTitleCellItemVO("状态");
		BillCellItemVO item_7 = getBillTitleCellItemVO("数据来源部门");
		BillCellItemVO item_8 = getBillTitleCellItemVO("考核频率");
		BillCellItemVO item_9 = getBillTitleCellItemVO("运算符");
		BillCellItemVO item_10 = getBillTitleCellItemVO("单位");

		rows.add(new BillCellItemVO[] { item_0, item_1, item_2, item_3, item_4, item_5, item_5_1, item_6, item_7, item_8, item_9, item_10 });// 表头

		int index = 0;
		for (int j = 0; j < hvos.length; j++) {
			HashVO targetevo = hvos[j];
			BillCellItemVO bitem_0 = getBillNormalCellItemVO(index, targetevo.getStringValue("catalogname"));
			BillCellItemVO bitem_1 = getBillNormalCellItemVO(index, targetevo.getStringValue("code"));
			BillCellItemVO bitem_2 = getBillNormalCellItemVO(index, targetevo.getStringValue("name"));
			BillCellItemVO bitem_3 = getBillNormalCellItemVO(index, targetevo.getStringValue("descr"));
			BillCellItemVO bitem_4 = getBillNormalCellItemVO(index, targetevo.getStringValue("evalstandard"));
			bitem_3.setCelltype("TEXTAREA");
			bitem_3.setRowheight("30");
			BillCellItemVO bitem_5 = getBillNormalCellItemVO(index, targetevo.getStringValue("weight"));
			BillCellItemVO bitem_5_1 = getBillNormalCellItemVO(index, targetevo.getStringValue("postnames"));
			BillCellItemVO bitem_6 = getBillNormalCellItemVO(index, targetevo.getStringValue("state"));
			BillCellItemVO bitem_7 = getBillNormalCellItemVO(index, getvaluenames(targetevo.getStringValue("dbsource"), dept_name));
			BillCellItemVO bitem_8 = getBillNormalCellItemVO(index, targetevo.getStringValue("checkcycle"));
			BillCellItemVO bitem_9 = getBillNormalCellItemVO(index, targetevo.getStringValue("operationtype"));
			BillCellItemVO bitem_10 = getBillNormalCellItemVO(index, getvaluenames(targetevo.getStringValue("unitvalue"), un_name));
			bitem_10.setCustProperty("id", targetevo.getStringValue("id"));
			bitem_10.setCustProperty("type", targetevo.getStringValue("targettype"));
			index++;
			rows.add(new BillCellItemVO[] { bitem_0, bitem_1, bitem_2, bitem_3, bitem_4, bitem_5, bitem_5_1, bitem_6, bitem_7, bitem_8, bitem_9, bitem_10 });
		}
		BillCellItemVO[][] cells = rows.toArray(new BillCellItemVO[0][0]);
		formatSpan(cells, new int[] { 0, 1 });
		formatClen(cells);
		cellvo.setCellItemVOs(cells);
		cellvo.setCollength(12);
		cellvo.setRowlength(rows.size());
		return cellvo;
	}

	private String getvaluenames(String source, HashVO hashVO[]) {
		StringBuilder stringBuilder = new StringBuilder();
		if (source != null && !source.equals("")) {
			String dbdept[] = source.split(";");
			for (int k = 0; k < dbdept.length; k++) {
				for (int n = 0; n < hashVO.length; n++) {
					if (dbdept[k].equals(hashVO[n].getStringValue("id"))) {
						if (!stringBuilder.toString().equals("")) {
							stringBuilder.append(";");
						}
						stringBuilder.append(hashVO[n].getStringValue("name"));
					}
				}
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * 
	 * @param logid
	 * @param deptids
	 * @param _noJoinStationkind 不参与的岗位
	 * @return
	 * @throws Exception
	 */
	public BillCellVO getJJTargetDeptJXMoney(String logid, String[] deptids, String[] _noJoinStationkind) throws Exception {
		StringBuffer sqlsb = new StringBuffer("select t1.checkeddept,t3.reportgroup,round(sum(t1.checkscore),2) from sal_dept_check_score t1 left join pub_corp_dept t2 on t1.checkeddept=t2.id left join sal_target_list t3 on t1.targetid = t3.id where t1.targettype='部门计价指标' and t1.logid = " + logid + " ");
		if (deptids != null && deptids.length > 0) {
			sqlsb.append(" and t1.checkeddept in(" + getTb().getInCondition(deptids) + ") ");
		}
		sqlsb.append(" group by t1.checkeddept,t3.reportgroup order by t2.linkcode,t1.targetcode ");

		StringBuffer sqlsb2 = new StringBuffer("select t1.checkeddept,round(sum(t1.checkscore),2) from sal_dept_check_score t1 left join pub_corp_dept t2 on t1.checkeddept=t2.id  where t1.targettype='部门计价指标' and t1.logid = " + logid + " ");
		if (deptids != null && deptids.length > 0) {
			sqlsb2.append(" and t1.checkeddeptt in(" + getTb().getInCondition(deptids) + ") ");
		}
		sqlsb2.append(" group by t1.checkeddept order by t2.linkcode ");

		String hvo[][] = getDmo().getStringArrayByDS(null, sqlsb.toString());
		HashMap dept_name = getDmo().getHashMapBySQLByDS(null, "select id,name from pub_corp_dept");
		HashMap dept_users = getDmo().getHashMapBySQLByDS(null, "select deptid,id from v_sal_personinfo where (isuncheck is null or isuncheck='N') and stationkind not in(" + getTb().getInCondition(_noJoinStationkind) + ") order by deptseq+0,postseq+0", true);
		HashMap userid_name = getDmo().getHashMapBySQLByDS(null, "select id,name from v_sal_personinfo ");
		HashVO oldhvo[] = getDmo().getHashVoArrayByDS(null, "select id,userid, reportgroup,money from sal_person_allot_money where logid = " + logid); //得到数据库中数据
		HashMap<String, String> oldData = new HashMap<String, String>(); //旧数据
		for (int i = 0; i < oldhvo.length; i++) {
			String key = oldhvo[i].getStringValue("reportgroup") + "_" + oldhvo[i].getStringValue("userid");
			oldData.put(key, oldhvo[i].getStringValue("money"));
		}

		HashMap<String, String> dept_score = getDmo().getHashMapBySQLByDS(null, sqlsb2.toString());

		LinkedHashSet reportGroup = new LinkedHashSet();
		HashMap dept_reportgroup_money = new HashMap();
		LinkedHashSet dept = new LinkedHashSet();
		for (int i = 0; i < hvo.length; i++) {
			reportGroup.add(hvo[i][1]);
			dept_reportgroup_money.put(hvo[i][0] + "_" + hvo[i][1], hvo[i][2]);
			dept.add(hvo[i][0]);
		}
		String[] titlegroup = (String[]) reportGroup.toArray(new String[0]);
		BillCellVO cellvo = new BillCellVO();
		int row = 0;
		List cs = new ArrayList();
		List allcellVO = new ArrayList();
		for (Iterator iterator = dept.iterator(); iterator.hasNext();) {
			String object = (String) iterator.next();//部门ID //
			String deptid = object;
			String deptname = (String) dept_name.get(deptid);
			List rowCellList = new ArrayList();
			//制作 表头
			BillCellItemVO itemvo_1 = getBillTitleCellItemVO("机构");
			itemvo_1.setCustProperty("deptname", deptname); //设置一个点
			BillCellItemVO itemvo_2 = getBillTitleCellItemVO("总金额");
			BillCellItemVO itemvo_3 = getBillTitleCellItemVO("姓名");
			rowCellList.add(itemvo_1);
			rowCellList.add(itemvo_2);
			rowCellList.add(itemvo_3);
			for (int i = 0; i < titlegroup.length; i++) {
				String targetname = titlegroup[i];
				String totlemoney = (String) dept_reportgroup_money.get(deptid + "_" + targetname);
				if (TBUtil.isEmpty(totlemoney)) {
					totlemoney = "0";
				}
				int length = getTb().getStrWidth(targetname + "(" + totlemoney + ")");
				BillCellItemVO itemvo_N = getBillTitleCellItemVO(targetname + "(" + totlemoney + ")");
				itemvo_N.setColwidth(((length + 20) + ""));
				rowCellList.add(itemvo_N);
			}
			allcellVO.add(rowCellList.toArray(new BillCellItemVO[0]));
			String allusers = (String) dept_users.get(deptid);
			if (!getTb().isEmpty(allusers)) {
				String userrids[] = getTb().split(allusers, ";");
				String totleMoney = dept_score.get(deptid);
				for (int j = 0; j < userrids.length; j++) {
					rowCellList = new ArrayList();
					if (!getTb().isEmpty(userrids[j])) {
						String username = (String) userid_name.get(userrids[j]);
						if (!getTb().isEmpty(username)) {
							BillCellItemVO itemvo_in_1 = getBillNormalCellItemVO(row, deptname);
							BillCellItemVO itemvo_in_2 = getBillNormalCellItemVO(row, getTb().isEmpty(totleMoney) ? "0" : totleMoney);
							BillCellItemVO itemvo_in_3 = getBillNormalCellItemVO(row, username);
							rowCellList.add(itemvo_in_1);
							rowCellList.add(itemvo_in_2);
							rowCellList.add(itemvo_in_3);
							for (int i = 0; i < titlegroup.length; i++) {
								//
								String key = titlegroup[i] + "_" + userrids[j];
								String content = "";
								if (oldData.containsKey(key)) {
									content = oldData.get(key);
								}
								BillCellItemVO itemvo_in_N = getBillNormalCellItemVO(row, content);
								rowCellList.add(itemvo_in_N);
							}
							row++;
						}

					}
					allcellVO.add((BillCellItemVO[]) rowCellList.toArray(new BillCellItemVO[0]));
				}
			}
		}

		BillCellItemVO vos[][] = (BillCellItemVO[][]) allcellVO.toArray(new BillCellItemVO[0][0]);
		if (vos.length > 1) {
			new SalaryTBUtil().formatSpan(vos, new int[] { 0, 1 });
		}
		cellvo.setCellItemVOs(vos);
		cellvo.setRowlength(allcellVO.size());
		cellvo.setCollength(3 + titlegroup.length);
		return cellvo;
	}
	/*public BillCellVO getJJTargetDeptJXMoney(String logid, String[] deptids) throws Exception {
		StringBuffer sqlsb = new StringBuffer("select t1.checkeddept,sum(t1.checkscore) from sal_dept_check_score t1 left join pub_corp_dept t2 on t1.checkeddept=t2.id  where t1.targettype='部门计价指标' and t1.logid = " + logid + " ");
		if (deptids != null && deptids.length > 0) {
			sqlsb.append(" and t1.checkeddeptt in(" + tbutil.getInCondition(deptids) + ") ");
		}
		sqlsb.append(" group by t1.checkeddept order by t2.linkcode ");
		String hvo[][] = dmo.getStringArrayByDS(null, sqlsb.toString());
		HashMap dept_name = dmo.getHashMapBySQLByDS(null, "select id,name from pub_corp_dept");
		HashMap dept_users = dmo.getHashMapBySQLByDS(null, "select deptid,id from v_sal_personinfo where isuncheck is null or isuncheck='N' order by deptseq+0,postseq+0", true);
		HashMap userid_name = dmo.getHashMapBySQLByDS(null, "select id,name from v_sal_personinfo ");

		BillCellVO cellvo = new BillCellVO();

		List cs = new ArrayList();

		List firstRow = new ArrayList();

		BillCellItemVO itemvo_1 = getBillTitleCellItemVO("机构");
		BillCellItemVO itemvo_2 = getBillTitleCellItemVO("可分配金额");
		BillCellItemVO itemvo_3 = getBillTitleCellItemVO("姓名");
		BillCellItemVO itemvo_4 = getBillTitleCellItemVO("效益薪酬");
		firstRow.add(itemvo_1);
		firstRow.add(itemvo_2);
		firstRow.add(itemvo_3);
		firstRow.add(itemvo_4);
		List allcellVO = new ArrayList();
		allcellVO.add((BillCellItemVO[]) firstRow.toArray(new BillCellItemVO[0]));
		int row = 0;
		for (int i = 0; i < hvo.length; i++) {
			String deptid = hvo[i][0];
			String score = hvo[i][1];
			String allusers = (String) dept_users.get(deptid);
			if (!tbutil.isEmpty(allusers)) {
				String userrids[] = tbutil.split(allusers, ";");
				String deptname = (String) dept_name.get(deptid);
				for (int j = 0; j < userrids.length; j++) {
					if (!tbutil.isEmpty(userrids[j])) {
						String username = (String) userid_name.get(userrids[j]);
						if (!tbutil.isEmpty(username)) {
							BillCellItemVO itemvo_in_1 = getBillNormalCellItemVO(row, deptname);
							BillCellItemVO itemvo_in_2 = getBillNormalCellItemVO(row, score);
							BillCellItemVO itemvo_in_3 = getBillNormalCellItemVO(row, username);
							BillCellItemVO itemvo_in_4 = getBillNormalCellItemVO(row, "");
							allcellVO.add(new BillCellItemVO[] { itemvo_in_1, itemvo_in_2, itemvo_in_3, itemvo_in_4 });
							row++;
						}

					}
				}
			}
		}
		BillCellItemVO vos[][] = (BillCellItemVO[][]) allcellVO.toArray(new BillCellItemVO[0][0]);
		if (vos.length > 1) {
			new SalaryTBUtil().formatSpan(vos, new int[] { 0, 1 });
		}
		cellvo.setCellItemVOs(vos);
		cellvo.setRowlength(allcellVO.size());
		cellvo.setCollength(4);
		return cellvo;
	}*/
}
