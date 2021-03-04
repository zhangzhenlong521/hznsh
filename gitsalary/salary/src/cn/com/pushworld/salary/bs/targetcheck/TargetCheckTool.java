package cn.com.pushworld.salary.bs.targetcheck;

import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.SwingUtilities;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.ui.common.LookAndFeel;

public class TargetCheckTool {
	private CommDMO dmo = new CommDMO();

	public HashMap getTargetCheckPlanCellVO(HashMap _param) throws Exception {
		HashMap rtnmap = new HashMap();
		HashVO[] planvo = null;
		if (_param.containsKey("deptid")) {
			if (_param.containsKey("planid")) { // status='已启用' and
				planvo = dmo.getHashVoArrayByDS(null, "select * from sal_target_plan where  deptid=" + _param.get("deptid") + " and id=" + _param.get("planid"));
			} else {
				planvo = dmo.getHashVoArrayByDS(null, "select * from sal_target_plan where  deptid=" + _param.get("deptid") + " order by deptid");
			}
		} else {
			if (_param.containsKey("planid")) {
				planvo = dmo.getHashVoArrayByDS(null, "select * from sal_target_plan where  id=" + _param.get("planid") + " order by deptid");
			} else {
				planvo = dmo.getHashVoArrayByDS(null, "select * from sal_target_plan where 1=1 order by deptid");
			}
		}
		if (planvo != null && planvo.length > 0) {
			List deptids = new ArrayList();
			List planids = new ArrayList();
			HashMap planid_vo = new HashMap();
			for (int i = 0; i < planvo.length; i++) {
				deptids.add(planvo[i].getStringValue("deptid", ""));
				planids.add(planvo[i].getStringValue("id", ""));
				planid_vo.put(planvo[i].getStringValue("id", ""), planvo[i]);
			}
			HashMap corpid_name = dmo.getHashMapBySQLByDS(null, "select id, name from pub_corp_dept where id in(" + TBUtil.getTBUtil().getInCondition(deptids) + ")");
			HashVO[] items = dmo.getHashVoArrayByDS(null, "select * from sal_target_plan_item where planid in (" + TBUtil.getTBUtil().getInCondition(planids) + ") order by planid, seq");
			LinkedHashMap planid_itemid_vo = new LinkedHashMap();
			LinkedHashMap itemid_scorerid_vo = new LinkedHashMap();
			if (items != null && items.length > 0) {
				List itemsids = new ArrayList();
				List targetids = new ArrayList();
				for (int i = 0; i < items.length; i++) {
					itemsids.add(items[i].getStringValue("id", ""));
					targetids.add(items[i].getStringValue("targetid", ""));
					if (planid_itemid_vo.containsKey(items[i].getStringValue("planid", ""))) {
						((LinkedHashMap) planid_itemid_vo.get(items[i].getStringValue("planid", ""))).put(items[i].getStringValue("id", ""), items[i]);
					} else {
						LinkedHashMap itemid_vo = new LinkedHashMap();
						itemid_vo.put(items[i].getStringValue("id", ""), items[i]);
						planid_itemid_vo.put(items[i].getStringValue("planid", ""), itemid_vo);
					}
				}
				HashMap targetid_name = dmo.getHashMapBySQLByDS(null, "select id,name from sal_target_list where id in (" + TBUtil.getTBUtil().getInCondition(targetids) + ")");
				HashVO[] bzvo = dmo.getHashVoArrayByDS(null, "select * from sal_target_evlstandard where targetid in (" + TBUtil.getTBUtil().getInCondition(targetids) + ") order by targetid, point");
				HashMap targetid_bz = new HashMap();
				for (int i = 0; i < bzvo.length; i++) {
					if (targetid_bz.containsKey(bzvo[i].getStringValue("targetid"))) {
						targetid_bz.put(bzvo[i].getStringValue("targetid"), targetid_bz.get(bzvo[i].getStringValue("targetid")) + "\r\n" + bzvo[i].getStringValue("point") + "分:" + bzvo[i].getStringValue("name"));
					} else {
						targetid_bz.put(bzvo[i].getStringValue("targetid"), bzvo[i].getStringValue("point") + ":" + bzvo[i].getStringValue("name"));
					}
				}
				HashVO[] allscorer = dmo.getHashVoArrayByDS(null, "select * from sal_target_plan_score where planitemid in (" + TBUtil.getTBUtil().getInCondition(itemsids) + ") order by planitemid,seq");
				List alluserids = new ArrayList();
				if (allscorer != null && allscorer.length > 0) {
					for (int i = 0; i < allscorer.length; i++) {
						if (itemid_scorerid_vo != null && itemid_scorerid_vo.containsKey(allscorer[i].getStringValue("planitemid", ""))) {
							((LinkedHashMap) itemid_scorerid_vo.get(allscorer[i].getStringValue("planitemid", ""))).put(allscorer[i].getStringValue("id", ""), allscorer[i]);
						} else {
							LinkedHashMap itemid_vo = new LinkedHashMap();
							itemid_vo.put(allscorer[i].getStringValue("id", ""), allscorer[i]);
							itemid_scorerid_vo.put(allscorer[i].getStringValue("planitemid", ""), itemid_vo);
						}
						alluserids.add(allscorer[i].getStringValue("scoreuser", ""));
					}
				}
				HashMap user_name = dmo.getHashMapBySQLByDS(null, "select id,name from sal_personinfo where id in (" + TBUtil.getTBUtil().getInCondition(alluserids) + ")");
				List itemcellvos = new ArrayList();
				String[] allplanids = (String[]) planid_itemid_vo.keySet().toArray(new String[0]);
				List allitemvolist = new ArrayList();
				BillCellItemVO[] ivo0 = new BillCellItemVO[6];
				ivo0[0] = new BillCellItemVO(); // 部门
				ivo0[0].setCellvalue("部门");
				ivo0[0].setBackground("191,213,255");
				ivo0[0].setFontsize("12");
				ivo0[0].setFontstyle("1");
				ivo0[0].setFonttype("新宋体");
				ivo0[1] = new BillCellItemVO(); // 指标
				ivo0[1].setCellvalue("指标");
				ivo0[1].setBackground("191,213,255");
				ivo0[1].setFontsize("12");
				ivo0[1].setFontstyle("1");
				ivo0[1].setFonttype("新宋体");
				ivo0[2] = new BillCellItemVO(); // 标准
				ivo0[2].setCellvalue("标准");
				ivo0[2].setBackground("191,213,255");
				ivo0[2].setFontsize("12");
				ivo0[2].setFontstyle("1");
				ivo0[2].setFonttype("新宋体");
				ivo0[3] = new BillCellItemVO(); // 权重
				ivo0[3].setCellvalue("指标权重");
				ivo0[3].setBackground("191,213,255");
				ivo0[3].setFontsize("12");
				ivo0[3].setFontstyle("1");
				ivo0[3].setFonttype("新宋体");
				ivo0[4] = new BillCellItemVO(); // 评分人
				ivo0[4].setCellvalue("评分人");
				ivo0[4].setBackground("191,213,255");
				ivo0[4].setFontsize("12");
				ivo0[4].setFontstyle("1");
				ivo0[4].setFonttype("新宋体");
				ivo0[5] = new BillCellItemVO(); // 评分人权重
				ivo0[5].setCellvalue("评分人权重");
				ivo0[5].setBackground("191,213,255");
				ivo0[5].setFontsize("12");
				ivo0[5].setFontstyle("1");
				ivo0[5].setFonttype("新宋体");
				allitemvolist.add(ivo0);
				for (int p = 0; p < planid_itemid_vo.size(); p++) {
					String planid = allplanids[p];
					LinkedHashMap itemid_vo = (LinkedHashMap) planid_itemid_vo.get(planid);
					String[] allitemids = (String[]) itemid_vo.keySet().toArray(new String[0]);
					for (int pp = 0; pp < allitemids.length; pp++) {
						HashVO itemvo = (HashVO) itemid_vo.get(allitemids[pp]);
						// 可以找到标准
						//
						if (itemid_scorerid_vo != null && itemid_scorerid_vo.containsKey(allitemids[pp])) {
							LinkedHashMap scorer_vo = (LinkedHashMap) itemid_scorerid_vo.get(allitemids[pp]);
							String[] allscorerids = (String[]) scorer_vo.keySet().toArray(new String[0]);
							for (int ppp = 0; ppp < allscorerids.length; ppp++) {
								HashVO scorervo = (HashVO) scorer_vo.get(allscorerids[ppp]);
								BillCellItemVO[] ivo = new BillCellItemVO[6];
								ivo[0] = new BillCellItemVO(); // 部门
								ivo[0].setCellvalue(corpid_name.get(((HashVO) planid_vo.get(planid)).getStringValue("deptid", "")) + "");
								ivo[0].setSpan("1,1");
								ivo[1] = new BillCellItemVO(); // 指标
								ivo[1].setCellvalue(targetid_name.get(itemvo.getStringValue("targetid")) + "");
								ivo[1].setSpan("1,1");
								ivo[2] = new BillCellItemVO(); // 标准
								if (targetid_bz.containsKey(itemvo.getStringValue("targetid"))) {
									ivo[2].setCellvalue(targetid_bz.get(itemvo.getStringValue("targetid")) + "");
								}
								ivo[2].setCelltype("TEXTAREA");
								ivo[2].setSpan("1,1");
								ivo[3] = new BillCellItemVO(); // 权重
								ivo[3].setCellvalue(itemvo.getStringValue("weights"));
								ivo[3].setSpan("1,1");
								ivo[4] = new BillCellItemVO(); // 评分人
								ivo[4].setCellvalue(user_name.get(scorervo.getStringValue("scoreuser")) + "");
								ivo[4].setSpan("1,1");
								ivo[5] = new BillCellItemVO(); // 评分人权重
								ivo[5].setCellvalue(scorervo.getStringValue("weights"));
								ivo[5].setSpan("1,1");
								allitemvolist.add(ivo);
							}
						} else {
							BillCellItemVO[] ivo = new BillCellItemVO[6];
							ivo[0] = new BillCellItemVO(); // 部门
							ivo[0].setCellvalue(corpid_name.get(((HashVO) planid_vo.get(planid)).getStringValue("deptid", "")) + "");
							ivo[1] = new BillCellItemVO(); // 指标
							ivo[1].setCellvalue(targetid_name.get(itemvo.getStringValue("targetid")) + "");
							ivo[2] = new BillCellItemVO(); // 标准
							if (targetid_bz.containsKey(itemvo.getStringValue("targetid"))) {
								ivo[2].setCellvalue(targetid_bz.get(itemvo.getStringValue("targetid")) + "");
							}
							ivo[3] = new BillCellItemVO(); // 权重
							ivo[3].setCellvalue(itemvo.getStringValue("weights"));
							ivo[4] = new BillCellItemVO(); // 评分人
							ivo[4].setCellvalue("");
							ivo[5] = new BillCellItemVO(); // 评分人权重
							ivo[5].setCellvalue("");
							allitemvolist.add(ivo);
						}
					}
				}
				BillCellVO vo = new BillCellVO();
				vo.setCollength(6);
				vo.setRowlength(allitemvolist.size());
				BillCellItemVO[][] items_ = (BillCellItemVO[][]) allitemvolist.toArray(new BillCellItemVO[0][0]);
				int[] _spanColumns = new int[] { 0, 1, 2, 3 };
				if (_spanColumns != null) {
					for (int i = 0; i < _spanColumns.length; i++) {
						int li_pos = _spanColumns[i];
						if (li_pos >= 0) {
							int li_spancount = 1; //
							int li_spanbeginpos = 0; //
							for (int k = 1; k < items_.length; k++) {
								items_[k][li_pos].setSpan("0,1");
								items_[k - 1][li_pos].setSpan("0,1");
								String str_value = items_[k][li_pos].getCellvalue(); //
								String str_value_front = items_[k - 1][li_pos].getCellvalue(); //
								if (TBUtil.getTBUtil().compareTwoString(str_value_front, str_value)) {
									li_spancount++;
								} else {
									items_[li_spanbeginpos][li_pos].setSpan(li_spancount + ",1"); //
									li_spancount = 1;
									li_spanbeginpos = k;
								}
							}
							items_[li_spanbeginpos][li_pos].setSpan(li_spancount + ",1"); //
						}
					}
				}

				FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(LookAndFeel.font);
				int li_allowMaxColWidth = 175;
				for (int j = 0; j < items_[0].length; j++) {
					int li_maxwidth = 0;
					String str_cellValue = null;
					for (int i = 0; i < items_.length; i++) {
						str_cellValue = items_[i][j].getCellvalue();
						int column = 1;
						if (items_[i][j] != null && items_[i][j].getSpan() != null) {
							column = Integer.parseInt(items_[i][j].getSpan().split(",")[1]);
						}
						if (str_cellValue != null && !str_cellValue.trim().equals("") && column <= 1) {
							int li_width = SwingUtilities.computeStringWidth(fm, str_cellValue);
							if (li_width > li_maxwidth) {
								li_maxwidth = li_width;
							}
						}
					}
					li_maxwidth = li_maxwidth + 13;
					if (li_maxwidth > li_allowMaxColWidth) {
						li_maxwidth = li_allowMaxColWidth;
					}
					for (int i = 0; i < items_.length; i++) {
						str_cellValue = items_[i][j].getCellvalue();
						if (str_cellValue != null && !str_cellValue.trim().equals("")) {
							if (items_[i][j].getSpan() != null && Integer.parseInt(items_[i][j].getSpan().split(",")[0]) > 0 && Integer.parseInt(items_[i][j].getSpan().split(",")[0]) < str_cellValue.split("\r").length) {
								for (int a = 0; a < Integer.parseInt(items_[i][j].getSpan().split(",")[0]); a++) {
									items_[i + a][j].setRowheight(((str_cellValue.split("\r").length * 17) / Integer.parseInt(items_[i][j].getSpan().split(",")[0]) + 1) + "");
								}
							}
							items_[i][j].setColwidth("" + li_maxwidth);
						}
					}
				}

				vo.setCellItemVOs(items_);
				rtnmap.put("vo", vo);
			} else {
				BillCellVO vo = new BillCellVO();
				vo.setCollength(1);
				vo.setRowlength(1);
				BillCellItemVO[][] items_ = new BillCellItemVO[1][1];
				items_[0][0] = new BillCellItemVO();
				items_[0][0].setCellvalue("未找到计划相应指标信息");
				items_[0][0].setColwidth("200");
				vo.setCellItemVOs(items_);
				rtnmap.put("vo", vo);
			}
		} else {
			BillCellVO vo = new BillCellVO();
			vo.setCollength(1);
			vo.setRowlength(1);
			BillCellItemVO[][] items_ = new BillCellItemVO[1][1];
			items_[0][0] = new BillCellItemVO();
			items_[0][0].setCellvalue("未找到相应计划信息");
			items_[0][0].setColwidth("200");
			vo.setCellItemVOs(items_);
			rtnmap.put("vo", vo);
		}
		return rtnmap;
	}
}
