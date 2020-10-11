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
 * ����ͨ��
 * @author haoming
 * create by 2014-8-8
 */
public class GYSalaryDMO extends SalaryAbstractCommDMO {
	//�õ��������еĸ���ƽ��ֵ
	CommDMO dmo = new CommDMO();

	/**
	 * 
	 * @param _checkdate ��������
	 * @param datatype ���ȣ����£��ϼ��ȣ�����,null
	 * @param _item �ֶ� A,B,C
	 * @param city_or_country �����硢ũ���硢null
	 * @return
	 * @throws Exception
	 */
	public static void main(String[] args) {
		System.out.println(Integer.parseInt("03"));
	}

	public float getebankAVG(String _checkdate, String datatype, String _item, String city_or_country) throws Exception {
		try {
			StringBuffer sb = new StringBuffer("select * from excel_tab_9 where 1=1 and ");

			//2014-08. �������ü����¶ȣ����ȣ��ϼ��ȵȡ�
			if ("����".equals(datatype)) {
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
			} else if ("�ϼ���".equals(datatype)) {
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
			} else if ("����".equals(datatype)) {
				sb.append("concat(year,'-',month)='" + getBackMonth(_checkdate, 1) + "' ");
			} else {
				sb.append("concat(year,'-',month)='" + _checkdate + "' ");
			}
			HashVO datas[] = dmo.getHashVoArrayByDS(null, sb.toString()); //�õ�����������
			HashVO hvo[] = dmo.getHashVoArrayAsTreeStructByDS(null, "select * from pub_corp_dept", "id", "parentid", "seq", null); //ȥ�����л���������������
			LinkedHashMap<String, List> level2 = new LinkedHashMap<String, List>(); //�Ѳ��źͷ�����Key��List���������Լ�������
			HashMap<String, HashVO> deptMap = new HashMap<String, HashVO>(); //���л���ID��Hashvo������
			HashMap<String, String> childFindParentMap = new HashMap<String, String>();//����Ǵ������ڵ㣬�Ѵ����������ĸ�����Map���档
			String parentid = null; //�����֧�л��߲��ţ���ֵid
			for (int i = 0; i < hvo.length; i++) { //�������л���
				HashVO dept = hvo[i];
				if (dept.getIntegerValue("$level") < 3) {//���С��3,�Ͳ��ǲ��Ż���֧��
					continue;
				}
				deptMap.put(dept.getStringValue("id"), dept); //����
				if (dept.getStringValue("$level").equals("3")) { //����������֧�кͲ���
					parentid = null; //������һ�ѣ�������߼�����
					if (!getTb().isEmpty(city_or_country)) {//�ж��ò������ֳ����磬��ũ����
						String depttype = dept.getStringValue("corpstyle"); //��ռ��������
						if (!getTb().isEmpty(depttype) && depttype.equals(city_or_country)) { //
							parentid = dept.getStringValue("id");
							List l = new ArrayList();
							l.add(dept);
							level2.put(parentid, l); //���Լ�Ҳ���뵽�����С��õ�ʱ�򷽱�
						}
					} else {
						parentid = dept.getStringValue("id"); //��ֵ
						List l = new ArrayList();
						l.add(dept);
						level2.put(parentid, l);
					}
				} else { //�ӽڵ�
					if (parentid == null) {//�������ֻ�������˳���ũ�壬�Ż�ִ��
						continue;
					}
					List l = level2.get(parentid);
					if (l != null) {
						l.add(dept);
					}
					childFindParentMap.put(dept.getStringValue("id"), parentid); //�Ѵ������͸��׻���������
				}
			}
			String[] level2dept = level2.keySet().toArray(new String[0]); // �õ��������
			List<HashVO> alldept = new ArrayList<HashVO>(); //���ж�����������������ũ�壬���ж�֧�֣�
			for (int i = 0; i < level2dept.length; i++) {
				List l = level2.get(level2dept[i]);
				alldept.addAll(l);
			}
			float sum = 0; //�ܺ�
			HashSet set = new LinkedHashSet();
			for (int i = 0; i < datas.length; i++) {
				String deptname = datas[i].getStringValue("A");//excel�л�������
				if (getTb().isEmpty(deptname)) {
					continue;
				}
				for (int j = 0; j < alldept.size(); j++) {
					String dname = ((HashVO) alldept.get(j)).getStringValue("shortname", "");
					if (!getTb().isEmpty(dname) && (deptname.contains(dname) || dname.contains(deptname))) { //˫����
						String deptid = (String) (alldept.get(j).getStringValue("id"));
						if (!childFindParentMap.containsKey(deptid)) { //
							set.add(deptid); //Ψһ����
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
			System.out.println("����������о�����ʽ" + sum + "/" + set.size());
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
