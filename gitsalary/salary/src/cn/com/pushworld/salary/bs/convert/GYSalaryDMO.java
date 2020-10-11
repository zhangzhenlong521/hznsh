package cn.com.pushworld.salary.bs.convert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.pushworld.salary.bs.dmo.SalaryAbstractCommDMO;

/**
 * 涡阳通用
 * @author haoming
 * create by 2014-8-8
 */
public class GYSalaryDMO extends SalaryAbstractCommDMO {
	//得到电子银行的各项平均值
	CommDMO dmo = new CommDMO();

	/**
	 * 
	 * @param _checkdate 计算日期
	 * @param datatype 季度，当月，上季度，上月,null
	 * @param _item 字段 A,B,C
	 * @param city_or_country 城区社、农村社、null
	 * @return
	 * @throws Exception
	 */
	public static void main(String[] args) {
		System.out.println(Integer.parseInt("03"));
	}

	public float getebankAVG(String _checkdate, String datatype, String _item, String city_or_country) throws Exception {
		try {
			StringBuffer sb = new StringBuffer("select * from excel_tab_9 where 1=1 and ");

			//2014-08. 可以配置计算月度，季度，上季度等。
			if ("季度".equals(datatype)) {
				String year = _checkdate.substring(0, 4);
				String month = _checkdate.substring(5, 7);
				int month_int = Integer.parseInt(month);
				if (1 <= month_int && month_int <= 3) {
					sb.append(" year='" + year + "' and (month='01' or month='02' or month='03') ");
				} else if (4 <= month_int && month_int <= 6) {
					sb.append(" year='" + year + "' and (month='04' or month='05' or month='06') ");
				} else if (7 <= month_int && month_int <= 9) {
					sb.append(" year='" + year + "' and (month='07' or month='08' or month='09') ");
				} else if (10 <= month_int && month_int <= 12) {
					sb.append(" year='" + year + "' and (month='10' or month='11' or month='12') ");
				}
			} else if ("上季度".equals(datatype)) {
				String year = _checkdate.substring(0, 4);
				String month = _checkdate.substring(5, 7);
				int month_int = Integer.parseInt(month);
				if (1 <= month_int && month_int <= 3) {
					sb.append(" year='" + (Integer.parseInt(year) - 1) + "' and (month='10' or month='11' or month='12') ");
				} else if (4 <= month_int && month_int <= 6) {
					sb.append(" year='" + year + "' and (month='01' or month='02' or month='03') ");
				} else if (7 <= month_int && month_int <= 9) {
					sb.append(" year='" + year + "' and (month='04' or month='05' or month='06') ");
				} else if (10 <= month_int && month_int <= 12) {
					sb.append(" year='" + year + "' and (month='07' or month='08' or month='09') ");
				}
			} else if ("上月".equals(datatype)) {
				sb.append("concat(year,'-',month)='" + getBackMonth(_checkdate, 1) + "' ");
			} else {
				sb.append("concat(year,'-',month)='" + _checkdate + "' ");
			}
			HashVO datas[] = dmo.getHashVoArrayByDS(null, sb.toString()); //得到考核期数据
			HashVO hvo[] = dmo.getHashVoArrayAsTreeStructByDS(null, "select * from pub_corp_dept", "id", "parentid", "seq", null); //去的所有机构，按照数排序
			LinkedHashMap<String, List> level2 = new LinkedHashMap<String, List>(); //把部门和分行做Key，List是他本身以及储蓄所
			HashMap<String, HashVO> deptMap = new HashMap<String, HashVO>(); //所有机构ID，Hashvo做缓存
			HashMap<String, String> childFindParentMap = new HashMap<String, String>();//如果是储蓄所节点，把储蓄所和他的父亲做Map缓存。
			String parentid = null; //如果是支行或者部门，赋值id
			for (int i = 0; i < hvo.length; i++) { //遍历所有机构
				HashVO dept = hvo[i];
				if (dept.getIntegerValue("$level") < 3) {//如果小于3,就不是部门或者支行
					continue;
				}
				deptMap.put(dept.getStringValue("id"), dept); //缓存
				if (dept.getStringValue("$level").equals("3")) { //等于三才是支行和部门
					parentid = null; //先重置一把，否则会逻辑错误。
					if (!getTb().isEmpty(city_or_country)) {//判断用不用区分城区社，和农村社
						String depttype = dept.getStringValue("corpstyle"); //口占机构类型
						if (!getTb().isEmpty(depttype) && depttype.equals(city_or_country)) { //
							parentid = dept.getStringValue("id");
							List l = new ArrayList();
							l.add(dept);
							level2.put(parentid, l); //把自己也加入到孩子中。用的时候方便
						}
					} else {
						parentid = dept.getStringValue("id"); //赋值
						List l = new ArrayList();
						l.add(dept);
						level2.put(parentid, l);
					}
				} else { //子节点
					if (parentid == null) {//这种情况只有区分了城区农村，才会执行
						continue;
					}
					List l = level2.get(parentid);
					if (l != null) {
						l.add(dept);
					}
					childFindParentMap.put(dept.getStringValue("id"), parentid); //把储蓄所和父亲缓存起来。
				}
			}
			String[] level2dept = level2.keySet().toArray(new String[0]); // 得到二层机构
			List<HashVO> alldept = new ArrayList<HashVO>(); //所有二级机构。（城区，农村，所有都支持）
			for (int i = 0; i < level2dept.length; i++) {
				List l = level2.get(level2dept[i]);
				alldept.addAll(l);
			}
			float sum = 0; //总和
			HashSet set = new LinkedHashSet();
			for (int i = 0; i < datas.length; i++) {
				String deptname = datas[i].getStringValue("A");//excel中机构名称
				if (getTb().isEmpty(deptname)) {
					continue;
				}
				for (int j = 0; j < alldept.size(); j++) {
					String dname = ((HashVO) alldept.get(j)).getStringValue("shortname", "");
					if (!getTb().isEmpty(dname) && (deptname.contains(dname) || dname.contains(deptname))) { //双包含
						String deptid = (String) (alldept.get(j).getStringValue("id"));
						if (!childFindParentMap.containsKey(deptid)) { //
							set.add(deptid); //唯一总数
						}
						try {
							sum += Float.parseFloat(datas[i].getStringValue(_item,"0")); //
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						continue;
					}
				}

			}
			System.out.println("计算电子银行均量公式" + sum + "/" + set.size());
			if (set.size() > 0) {
				return sum / set.size();
			}
			return -1;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}
}
