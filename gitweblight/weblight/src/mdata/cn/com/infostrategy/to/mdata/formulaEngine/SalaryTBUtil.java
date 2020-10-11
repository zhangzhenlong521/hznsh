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
 * ��Ч���˵�tb�����ࡣ
 * @author haoming
 * create by 2013-7-9
 */
public final class SalaryTBUtil implements Serializable {
	private static final long serialVersionUID = 4762720797672926373L;
	public static final String ggCheckTargetType = "�߹ܴ�ֱ"; //���³����˸߹ܵ�ָ��ļ�д���
	private TBUtil tbutil = new TBUtil();

	/*
	 * ����Hashvo�����ĳ���ֶεõ�һ��in���������� 1,2,3,4
	 */
	public String getInCondition(HashVO _hashvo[], String _item) {
		return TBUtil.getTBUtil().getInCondition(getHashVosItemList(_hashvo, _item));
	}

	// ����billVO�����ĳ�еõ�һ��in���������� 1,2,3,4
	public String getInCondition(BillVO billvo[], String _item) {
		return TBUtil.getTBUtil().getInCondition(getBillVosItemList(billvo, _item));
	}

	// ����Hashvo�����ĳ�еõ�һ�����ϡ�
	public List getHashVosItemList(HashVO[] _vos, String _item) {
		List l = new ArrayList();
		for (int i = 0; i < _vos.length; i++) {
			l.add(_vos[i].getStringValue(_item));
		}
		return l;
	}

	// �õ�һ��BIllVOĳ��ֵ�ļ��ϡ�
	public List getBillVosItemList(BillVO[] _vos, String _item) {
		List l = new ArrayList();
		for (int i = 0; i < _vos.length; i++) {
			l.add(_vos[i].getStringValue(_item));
		}
		return l;
	}

	// �õ����ֵ�洢��ʽ;1;3;4;5;
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
			sb.append(";"); // ��β
			return sb.toString();
		}
		return "";
	}

	public HashVO[] getHashVoArrayByDS(String _dbsource, String _sql) throws Exception {
		HashVO[] getVos = null;
		if (TBUtil.JVMSITE_SERVER == TBUtil.getTBUtil().getJVSite()) { // ������
			getVos = new CommDMO().getHashVoArrayByDS(null, _sql.toString());
		} else {
			getVos = UIUtil.getHashVoArrayByDS(null, _sql.toString());
		}
		return getVos;
	}

	public String getStringValueByDS(String _dbsource, String _sql) throws Exception {
		String rtvalue = ""; //����ֵ
		if (TBUtil.JVMSITE_SERVER == TBUtil.getTBUtil().getJVSite()) { // ������
			rtvalue = new CommDMO().getStringValueByDS(null, _sql.toString());
		} else {
			rtvalue = UIUtil.getStringValueByDS(null, _sql.toString());
		}
		return rtvalue;
	}

	public HashMap getHashMapBySQLByDS(String _dbsource, String _sql) throws Exception {
		if (TBUtil.JVMSITE_SERVER == TBUtil.getTBUtil().getJVSite()) { // ������
			return new CommDMO().getHashMapBySQLByDS(null, _sql.toString());
		} else {
			return UIUtil.getHashMapBySQLByDS(null, _sql.toString());
		}
	}

	/**
	 * �滻��ʽ�еı���x��������
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
			} // ����Ҳ���,�򷵻�
			boolean needreplace = true;
			//��indexof�Ͳ���Ҫ�滻��
			if (li_pos >= 1) {
				char c = str_remain.charAt(li_pos - 1); //ǰ���
				if ((c >= 65 && c <= 90) || (c >= 97 && c <= 122)) {
					needreplace = false;
				}
			}
			if (li_pos < str_remain.length() - 1 && needreplace) {
				char c = str_remain.charAt(li_pos + 1); //����ġ�
				if ((c >= 65 && c <= 90) || (c >= 97 && c <= 122)) {
					needreplace = false;
				}
			}
			String str_prefix = str_remain.substring(0, li_pos);
			if (needreplace) {
				str_return = str_return + str_prefix + replace; // ������ַ�������ԭ��ǰ�
			} else {
				str_return = str_return + str_prefix + old_item;
			}
			str_remain = str_remain.substring(li_pos + old_item.length(), str_remain.length());
		}
		str_return = str_return + str_remain; // ��ʣ��ļ���
		return str_return;
	}

	/*
	 * ����ĳ���˵�ID�õ����ܲ鿴���˵���Ϣ��
	 */
	public String[] getSameDeptUsers(String _userid) {
		try {
			HashVO uservos[] = getSameDeptPersons(_userid);
			LinkedHashSet<String> set = new LinkedHashSet<String>();
			set.add(_userid);
			HashMap<String, String> ggpostmap = UIUtil.getHashMapBySQLByDS(null, "select t1.userid,t2.name  from v_pub_user_post_1 t1 left join pub_post t2 on t1.postid = t2.id where t2.stationkind = '�߹�' or t2.stationkind = '�쵼����'");
			if (ggpostmap != null && "���³�".equals(ggpostmap.get(_userid))) {
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

	//����ĳ�������ĸ�λ�Ƿ���ĳ���������ŵ�һ��λ ���� �򷵻���������������
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
	* ��ɫ�����ı�����/2013-10-31��
	*/
	public BillCellItemVO getBillCellVO_Blue(String value) {
		BillCellItemVO item = new BillCellItemVO();
		item.setCellvalue(value);
		item.setBackground("191,213,255");
		item.setFonttype("������");
		item.setFontsize("12");
		item.setFontstyle("0");
		item.setSpan("1,1");
		item.setIseditable("N");
		item.setCellhelp(null);
		item.setHalign(2); //
		return item;
	}

	/*
	 * ��ɫ�����ı��
	 */
	public BillCellItemVO getBillCellVO_green(String value) {
		BillCellItemVO item = new BillCellItemVO();
		item.setCellvalue(value);
		item.setBackground("210,231,251");//"191,255,213"
		item.setFonttype("������");
		item.setFontsize("12");
		item.setFontstyle("0");
		item.setSpan("1,1");
		item.setIseditable("N");
		item.setCellhelp(null);
		item.setHalign(2); //
		return item;
	}

	/*
	 * ���ϲ���Ԫ���������ɫ�����ı��
	 */
	public BillCellItemVO getBillCellVO_Blue(String value, String span) {
		BillCellItemVO item = new BillCellItemVO();
		item.setCellvalue(value);
		item.setBackground("191,213,255");
		item.setFonttype("������");
		item.setFontsize("12");
		item.setFontstyle("0");
		item.setSpan(span);
		item.setIseditable("N");
		item.setCellhelp(null);
		item.setHalign(2); //
		return item;
	}

	/*
	 * Ĭ�ϰ�ɫ
	 */
	public BillCellItemVO getBillCellVO_Normal(String value) {
		BillCellItemVO item = new BillCellItemVO();
		item.setCellvalue(value);
		item.setFonttype("������");
		item.setFontsize("12");
		item.setFontstyle("0");
		item.setSpan("1,1");
		item.setIseditable("Y");
		item.setCellhelp(null);
		return item;
	}

	/*
	 * ָ�������
	 */
	public BillCellItemVO getBillTargetCellItemVO(String value) {
		BillCellItemVO item = new BillCellItemVO();
		item.setCellvalue(value);
		item.setBackground("191,213,0");
		item.setFonttype("������");
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
			s = s.replaceAll("0+?$", "");//ȥ�������0  
			s = s.replaceAll("[.]$", "");//�����һλ��.��ȥ��  
		}
		return s;
	}

	/*
	 * 2013-12-13
	 * ���ݴ������ֵ�����������Ӧֵ��
	 * Ϊ�˽����ͬ�������ֳ߶Ȳ�ͬ������, �������е������˵÷��ٴ�ӳ��һ��, ӳ�䷽��:
	 * С��4��: 92--100��
	 * 4��: 88.5--100��
	 * ����4��: 85-100��
	 */
	public String getScoreConfigMinVal(String par, int _val) {
		String val = _val + "";
		//������ʽ: <=3(92);4(88);>=5(85)
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

	// ������ת����Ӣ�ı���
	public String convertIntColToEn(int _index) {
		StringBuffer sb = new StringBuffer();
		int in = _index / 26;
		if (in == 0) { // ��������һ��
			char ccc = (char) (65 - 1 + _index);
			sb.append(ccc);
		} else {
			char ccc = (char) (65 - 1 + _index % 26);
			sb.append(ccc);
			sb.insert(0, convertIntColToEn(in));
		}
		return sb.toString();
	}

	//�õ���ǰ��¼��δ��ɵ�����������
	public int getLoginUserUnCheckedScore() throws Exception {
		String userid = ClientEnvironment.getCurrLoginUserVO().getId();
		String counts[] = UIUtil.getStringArrayFirstColByDS(null, "select count(t2.id) from sal_person_check_score t1 left join SAL_TARGET_CHECK_LOG t2 on t1.logid =t2.id where  t2.status='������' and t1.scoreuser='" + userid + "' and t1.checkscore is null"
				+ " union all select count(t1.id)  from sal_dept_check_score t1 left join SAL_TARGET_CHECK_LOG t2 on t1.logid = t2.id where t2.status='������' and t1.scoreuser='" + userid + "' and t1.targettype='���Ŷ���ָ��' and t1.checkscore is null"
				+ " union all select count(t1.id)  from sal_person_postduty_score t1 left join SAL_TARGET_CHECK_LOG t2 on t1.logid = t2.id where t2.status='������' and t1.scoreuser='" + userid + "' and t1.targettype='��������ָ��' and t1.checkscore is null " + "");
		int count = 0;
		for (int i = 0; i < counts.length; i++) {
			count += Integer.parseInt(counts[i]);
		}
		return count;
	}

	/**
	 * ����Ԫ����ͬ���ݵĺϲ�
	 * 
	 * @param cellItemVOs
	 * @param _spanColumns
	 *            �Ǽ�����Ҫ����
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
						// �ϲ�������ɫһ��
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
