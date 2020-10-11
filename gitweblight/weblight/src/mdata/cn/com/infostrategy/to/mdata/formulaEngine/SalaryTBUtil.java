package cn.com.infostrategy.to.mdata.formulaEngine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;

/**
 * 绩效考核的tb工具类。
 * @author haoming
 * create by 2013-7-9
 */
public final class SalaryTBUtil implements Serializable {
	private static final long serialVersionUID = 4762720797672926373L;
	public static final String ggCheckTargetType = "高管垂直"; //董事长考核高管的指标的简写类别。
	private TBUtil tbutil = new TBUtil();

	/*
	 * 根据Hashvo数组的某个字段得到一个in的条件返回 1,2,3,4
	 */
	public String getInCondition(HashVO _hashvo[], String _item) {
		return TBUtil.getTBUtil().getInCondition(getHashVosItemList(_hashvo, _item));
	}

	// 根据billVO数组的某列得到一个in的条件返回 1,2,3,4
	public String getInCondition(BillVO billvo[], String _item) {
		return TBUtil.getTBUtil().getInCondition(getBillVosItemList(billvo, _item));
	}

	// 根据Hashvo数组的某列得到一个集合。
	public List getHashVosItemList(HashVO[] _vos, String _item) {
		List l = new ArrayList();
		for (int i = 0; i < _vos.length; i++) {
			l.add(_vos[i].getStringValue(_item));
		}
		return l;
	}

	// 得到一个BIllVO某列值的集合。
	public List getBillVosItemList(BillVO[] _vos, String _item) {
		List l = new ArrayList();
		for (int i = 0; i < _vos.length; i++) {
			l.add(_vos[i].getStringValue(_item));
		}
		return l;
	}

	// 得到多个值存储格式;1;3;4;5;
	public String getMutilValueStr(List<String> _list) {
		if (_list == null || _list.size() == 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < _list.size(); i++) {
			String id = _list.get(i);
			if (id != null && !id.equals("")) {
				sb.append(";" + id);
			}
		}
		if (sb.length() > 0) {
			sb.append(";"); // 收尾
			return sb.toString();
		}
		return "";
	}

	public HashVO[] getHashVoArrayByDS(String _dbsource, String _sql) throws Exception {
		HashVO[] getVos = null;
		if (TBUtil.JVMSITE_SERVER == TBUtil.getTBUtil().getJVSite()) { // 服务器
			getVos = new CommDMO().getHashVoArrayByDS(null, _sql.toString());
		} else {
			getVos = UIUtil.getHashVoArrayByDS(null, _sql.toString());
		}
		return getVos;
	}

	public String getStringValueByDS(String _dbsource, String _sql) throws Exception {
		String rtvalue = ""; //返回值
		if (TBUtil.JVMSITE_SERVER == TBUtil.getTBUtil().getJVSite()) { // 服务器
			rtvalue = new CommDMO().getStringValueByDS(null, _sql.toString());
		} else {
			rtvalue = UIUtil.getStringValueByDS(null, _sql.toString());
		}
		return rtvalue;
	}

	public HashMap getHashMapBySQLByDS(String _dbsource, String _sql) throws Exception {
		if (TBUtil.JVMSITE_SERVER == TBUtil.getTBUtil().getJVSite()) { // 服务器
			return new CommDMO().getHashMapBySQLByDS(null, _sql.toString());
		} else {
			return UIUtil.getHashMapBySQLByDS(null, _sql.toString());
		}
	}

	/**
	 * 替换公式中的变量x，并不是
	 * @return
	 */
	public String formulaReplaceX(String str_par, String _old, String replace) {
		if (str_par == null) {
			return null;
		}

		String str_return = "";
		String str_remain = str_par;
		String old_item = _old;
		boolean bo_1 = true;
		while (bo_1) {
			int li_pos = str_remain.indexOf(old_item);
			if (li_pos < 0) {
				break;
			} // 如果找不到,则返回
			boolean needreplace = true;
			//像indexof就不需要替换。
			if (li_pos >= 1) {
				char c = str_remain.charAt(li_pos - 1); //前面的
				if ((c >= 65 && c <= 90) || (c >= 97 && c <= 122)) {
					needreplace = false;
				}
			}
			if (li_pos < str_remain.length() - 1 && needreplace) {
				char c = str_remain.charAt(li_pos + 1); //后面的。
				if ((c >= 65 && c <= 90) || (c >= 97 && c <= 122)) {
					needreplace = false;
				}
			}
			String str_prefix = str_remain.substring(0, li_pos);
			if (needreplace) {
				str_return = str_return + str_prefix + replace; // 将结果字符串加上原来前辍
			} else {
				str_return = str_return + str_prefix + old_item;
			}
			str_remain = str_remain.substring(li_pos + old_item.length(), str_remain.length());
		}
		str_return = str_return + str_remain; // 将剩余的加上
		return str_return;
	}

	/*
	 * 根据某个人的ID得到他能查看的人的信息。
	 */
	public String[] getSameDeptUsers(String _userid) {
		try {
			HashVO uservos[] = getSameDeptPersons(_userid);
			LinkedHashSet<String> set = new LinkedHashSet<String>();
			set.add(_userid);
			HashMap<String, String> ggpostmap = UIUtil.getHashMapBySQLByDS(null, "select t1.userid,t2.name  from v_pub_user_post_1 t1 left join pub_post t2 on t1.postid = t2.id where t2.stationkind = '高管' or t2.stationkind = '领导班子'");
			if (ggpostmap != null && "董事长".equals(ggpostmap.get(_userid))) {
				return ggpostmap.keySet().toArray(new String[0]);
			}
			if (uservos != null) {
				for (int i = 0; i < uservos.length; i++) {
					String userid = uservos[i].getStringValue("id");
					if (!TBUtil.isEmpty(userid)) {
						if ((ggpostmap != null && !ggpostmap.containsKey(userid))) {
							set.add(userid);
						}
					}
				}
			}
			return set.toArray(new String[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String[] { _userid };
	}

	//根据某人所属的岗位是否是某人所属部门第一岗位 若是 则返回所属部门所有人
	public HashVO[] getSameDeptPersons(String userid) throws Exception {
		String deptids = "";
		HashVO[] hvos_user = getHashVoArrayByDS(null, "select deptid,postid from v_pub_user_post_1 where userid='" + userid + "'");
		if (hvos_user != null || hvos_user.length > 0) {
			for (int i = 0; i < hvos_user.length; i++) {
				String deptid = hvos_user[i].getStringValue("deptid", "");
				String postid = hvos_user[i].getStringValue("postid", "");
				HashVO[] hvos_post = getHashVoArrayByDS(null, "select id from pub_post where  deptid='" + deptid + "' order by seq,code");
				if (hvos_post != null && hvos_post.length > 0) {
					if (postid.equals(hvos_post[0].getStringValue("id", ""))) {
						deptids += ",'" + deptid + "'";
					}
				}
			}
		}
		if (deptids.equals("")) {
			return null;
		} else {
			deptids = deptids.substring(1, deptids.length());
		}

		return getHashVoArrayByDS(null, "select * from v_sal_personinfo where deptid in(" + deptids + ") order by postseq");
	}

	/*
	* 蓝色背景的表格【李春娟/2013-10-31】
	*/
	public BillCellItemVO getBillCellVO_Blue(String value) {
		BillCellItemVO item = new BillCellItemVO();
		item.setCellvalue(value);
		item.setBackground("191,213,255");
		item.setFonttype("新宋体");
		item.setFontsize("12");
		item.setFontstyle("0");
		item.setSpan("1,1");
		item.setIseditable("N");
		item.setCellhelp(null);
		item.setHalign(2); //
		return item;
	}

	/*
	 * 绿色背景的表格
	 */
	public BillCellItemVO getBillCellVO_green(String value) {
		BillCellItemVO item = new BillCellItemVO();
		item.setCellvalue(value);
		item.setBackground("210,231,251");//"191,255,213"
		item.setFonttype("新宋体");
		item.setFontsize("12");
		item.setFontstyle("0");
		item.setSpan("1,1");
		item.setIseditable("N");
		item.setCellhelp(null);
		item.setHalign(2); //
		return item;
	}

	/*
	 * 带合并单元格参数的蓝色背景的表格
	 */
	public BillCellItemVO getBillCellVO_Blue(String value, String span) {
		BillCellItemVO item = new BillCellItemVO();
		item.setCellvalue(value);
		item.setBackground("191,213,255");
		item.setFonttype("新宋体");
		item.setFontsize("12");
		item.setFontstyle("0");
		item.setSpan(span);
		item.setIseditable("N");
		item.setCellhelp(null);
		item.setHalign(2); //
		return item;
	}

	/*
	 * 默认白色
	 */
	public BillCellItemVO getBillCellVO_Normal(String value) {
		BillCellItemVO item = new BillCellItemVO();
		item.setCellvalue(value);
		item.setFonttype("新宋体");
		item.setFontsize("12");
		item.setFontstyle("0");
		item.setSpan("1,1");
		item.setIseditable("Y");
		item.setCellhelp(null);
		return item;
	}

	/*
	 * 指标等类型
	 */
	public BillCellItemVO getBillTargetCellItemVO(String value) {
		BillCellItemVO item = new BillCellItemVO();
		item.setCellvalue(value);
		item.setBackground("191,213,0");
		item.setFonttype("新宋体");
		item.setFontsize("12");
		item.setFontstyle("1");
		item.setSpan("1,1");
		item.setIseditable("N");
		item.setCellhelp(null);
		item.setHalign(2);
		return item;
	}

	public String subZeroAndDot(String s) {
		if (s.indexOf(".") > 0) {
			s = s.replaceAll("0+?$", "");//去掉多余的0  
			s = s.replaceAll("[.]$", "");//如最后一位是.则去掉  
		}
		return s;
	}

	/*
	 * 2013-12-13
	 * 根据传入的数值，返回区间对应值。
	 * 为了解决不同部门评分尺度不同的问题, 将部门中的所有人得分再次映射一次, 映射方案:
	 * 小于4人: 92--100分
	 * 4人: 88.5--100分
	 * 大于4人: 85-100分
	 */
	public String getScoreConfigMinVal(String par, int _val) {
		String val = _val + "";
		//正则表达式: <=3(92);4(88);>=5(85)
		//		String par = "[1-3]=92;[4]=88.5;[5-9]=85;^\\d{2,4}=85";
		String key = "";

		HashMap<String, String> parammap = new TBUtil().convertStrToMapByExpress(par);
		for (Map.Entry<String, String> entry : parammap.entrySet()) {
			key = entry.getKey();
			if (val.matches(key)) {
				return entry.getValue();
			}
		}

		return null;
	}

	// 把数字转换成英文编码
	public String convertIntColToEn(int _index) {
		StringBuffer sb = new StringBuffer();
		int in = _index / 26;
		if (in == 0) { // 如果是最后一层
			char ccc = (char) (65 - 1 + _index);
			sb.append(ccc);
		} else {
			char ccc = (char) (65 - 1 + _index % 26);
			sb.append(ccc);
			sb.insert(0, convertIntColToEn(in));
		}
		return sb.toString();
	}

	//得到当前登录人未完成的评分数量。
	public int getLoginUserUnCheckedScore() throws Exception {
		String userid = ClientEnvironment.getCurrLoginUserVO().getId();
		String counts[] = UIUtil.getStringArrayFirstColByDS(null, "select count(t2.id) from sal_person_check_score t1 left join SAL_TARGET_CHECK_LOG t2 on t1.logid =t2.id where  t2.status='考核中' and t1.scoreuser='" + userid + "' and t1.checkscore is null"
				+ " union all select count(t1.id)  from sal_dept_check_score t1 left join SAL_TARGET_CHECK_LOG t2 on t1.logid = t2.id where t2.status='考核中' and t1.scoreuser='" + userid + "' and t1.targettype='部门定性指标' and t1.checkscore is null"
				+ " union all select count(t1.id)  from sal_person_postduty_score t1 left join SAL_TARGET_CHECK_LOG t2 on t1.logid = t2.id where t2.status='考核中' and t1.scoreuser='" + userid + "' and t1.targettype='岗责评价指标' and t1.checkscore is null " + "");
		int count = 0;
		for (int i = 0; i < counts.length; i++) {
			count += Integer.parseInt(counts[i]);
		}
		return count;
	}

	/**
	 * 将单元格相同内容的合并
	 * 
	 * @param cellItemVOs
	 * @param _spanColumns
	 *            那几列需要处理
	 */
	public void formatSpan(BillCellItemVO[][] cellItemVOs, int[] _spanColumns) {
		if (_spanColumns != null) {
			HashMap temp = new HashMap();
			for (int i = 0; i < _spanColumns.length; i++) {
				int li_pos = _spanColumns[i];
				if (li_pos >= 0) {
					int li_spancount = 1;
					int li_spanbeginpos = 1;
					for (int k = 2; k < cellItemVOs.length; k++) {
						String str_value = cellItemVOs[k][li_pos].getCellvalue();
						// 合并的列颜色一样
						cellItemVOs[k][li_pos].setBackground("234,240,248");
						String str_value_front = cellItemVOs[k - 1][li_pos].getCellvalue();
						if (tbutil.compareTwoString(str_value_front, str_value)) {
							if (i >= 1) {
								String str_value0 = cellItemVOs[k][_spanColumns[i - 1]].getCellvalue();
								String str_value_front0 = cellItemVOs[k - 1][_spanColumns[i - 1]].getCellvalue();
								if (tbutil.compareTwoString(str_value0, str_value_front0)) {
									li_spancount++;
								} else {
									cellItemVOs[li_spanbeginpos][li_pos].setSpan(li_spancount + ",1");
									li_spancount = 1;
									li_spanbeginpos = k;
								}
							} else {
								li_spancount++;
							}

						} else {
							cellItemVOs[li_spanbeginpos][li_pos].setSpan(li_spancount + ",1");
							li_spancount = 1;
							li_spanbeginpos = k;
						}
					}
					cellItemVOs[li_spanbeginpos][li_pos].setSpan(li_spancount + ",1");
				}
			}
		}
	}
}
