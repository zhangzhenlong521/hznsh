package com.pushworld.ipushgrc.ui.HR.p060;

import java.util.ArrayList;
import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter;
import cn.com.infostrategy.bs.report.ReportDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;

/**
 * 
 * @author longlonggo521
 * Ա���������ͳ��
 */
public class StaffmemberCountWorkPanel extends MultiLevelReportDataBuilderAdapter {
	TBUtil tbutil = new TBUtil();
	CommDMO dmo = new CommDMO();

	public HashVO[] buildReportData(HashMap condition) throws Exception {
		String age = (String) condition.get("age");//����
		String politicalstatus = (String) condition.get("politicalstatus"); //������ò
		String degree = (String) condition.get("degree");//ѧ��
		String stationkind = (String) condition.get("stationkind");//��λ����
		String posttitle = (String) condition.get("posttitle");//ְ��
		String workage = (String) condition.get("workage");//����

		StringBuffer sql_sb = new StringBuffer();
		sql_sb.append("select id id, age ����,politicalstatus ������ò,degree ѧ��,stationkind ��λ����, posttitle ְ�� ,workage ����  from v_sal_personinfo where 1=1");

		//���ܲ���
		if (age != null && !age.equals("")) {
			String [] str=getSplit(age);
			sql_sb.append(" and (age>='"+str[0]+"' and age<='"+str[1]+"')");
		}

		//�ƶȷ����ѯ����
		if (politicalstatus != null && !politicalstatus.equals("")) {
			sql_sb.append(" and politicalstatus in ('" + politicalstatus+ "')");
		}

		//ҵ�����Ͳ�ѯ����,���Ͷ�ѡ!!
		if (degree != null && !degree.equals("")) { //ҵ������
			sql_sb.append(" and degree in ('" +degree + "')");
		}

		//�ڿ����Ͳ�ѯ����!!���Ͷ�ѡ!!
		if (stationkind != null && !stationkind.equals("")) { //�ڿ�����!
			sql_sb.append(" and stationkind in ('" + stationkind+ "')");
		}

		if (posttitle != null && !posttitle.equals("")) {//��Ʒ����
			sql_sb.append(" and posttitle in ('" + posttitle + "')");
		}
		if (workage != null && !workage.equals("")) {
			String [] str=getSplit(workage);
			sql_sb.append("and (workage>='"+str[0]+"' and workage<='"+str[1]+"')");
		}
		sql_sb.append(" order by linkcode"); //�������ʱ������ͳ�Ƴ����ļ������ҵ�
		HashVO[] vos = dmo.getHashVoArrayByDS(null, sql_sb.toString());

		ReportDMO reportDMO = new ReportDMO(); 
//		//�������ܲ���
//		reportDMO.addOneFieldFromOtherTree(vos, "������ò", "������ò", "select politicalstatus,politicalstatus from v_sal_personinfo", 2, false, 2); //���ϵ�1��
//
//		//����ҵ�����͵�1�����2��!!
//		reportDMO.addOneFieldFromOtherTree(vos, "ѧ��", "ѧ��", "select degree,degree from v_sal_personinfo", 1, false, 1); //���ϵ�1��
//
//		//�����ڿط���֮��һ����ڶ���
//		reportDMO.addOneFieldFromOtherTree(vos, "��λ����", "��λ����", "select stationkind from v_sal_personinfo", 1, false, 1); //���ϵ�1��
//		//��Ʒ����֮��һ����ڶ���
//		reportDMO.addOneFieldFromOtherTree(vos, "ְ��", "ְ��", "select posttitle from v_sal_personinfo", 1, false, 1); //���ϵ�1��
//		new ReportUtil().leftOuterJoin_YSMDFromDateTime(vos, "��������", "��������", "��"); //��������������ɼ���!
		return vos;
	}

	public String[] getGroupFieldNames() {
		return new String[] {"����","������ò","ѧ��","��λ����","ְ��","����"};
	}

	public String[] getSumFiledNames() {
		return new String[] { "����" };
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid() {
		return getBeforehandGroupType();
	}

	@Override
	/**
	 * ��ȡ��ϸʱ��ģ�����!!
	 */
	public String getDrillTempletCode() throws Exception {
		return "SAL_PERSONINFO_ZZL_CODE4"; //
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Chart() {
		return getBeforehandGroupType();
	}

	/**
	 * �״�ͼ
	 */
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Splider() {
		ArrayList al = new ArrayList(); //
		al.add(getBeforehandGroupType("������ò_��λ����", "����", BeforeHandGroupTypeVO.COUNT));
		al.add(getBeforehandGroupType("ѧ��_������ò", "����", BeforeHandGroupTypeVO.COUNT));
		return (BeforeHandGroupTypeVO[]) al.toArray(new BeforeHandGroupTypeVO[0]);
	}

	/**
	 * �����ͼ��!!!
	 * @return
	 */
	public BeforeHandGroupTypeVO[] getBeforehandGroupType() {
		ArrayList al = new ArrayList(); //
		al.add(getBeforehandGroupType("������ò_ѧ��", "����", BeforeHandGroupTypeVO.COUNT)); //
		al.add(getBeforehandGroupType("��λ����_ѧ��", "����", BeforeHandGroupTypeVO.COUNT)); //
		al.add(getBeforehandGroupType("ְ��_ѧ��", "����", BeforeHandGroupTypeVO.COUNT)); //
		al.add(getBeforehandGroupType("������ò_��λ����", "����", BeforeHandGroupTypeVO.COUNT)); //
		al.add(getBeforehandGroupType("������ò_ְ��", "����", BeforeHandGroupTypeVO.COUNT)); //
		al.add(getBeforehandGroupType("��λ����_ְ��", "����", BeforeHandGroupTypeVO.COUNT)); //
//
//		al.add(getBeforehandGroupType("��������_ҵ������(��1��)", "����", BeforeHandGroupTypeVO.COUNT)); //
//		al.add(getBeforehandGroupType("��������_ҵ������(��2��)", "����", BeforeHandGroupTypeVO.COUNT)); //
//
//		al.add(getBeforehandGroupType("��������_�ڿط���(��1��)", "����", BeforeHandGroupTypeVO.COUNT)); //
//		al.add(getBeforehandGroupType("��������_�ڿط���(��2��)", "����", BeforeHandGroupTypeVO.COUNT)); //
//
//		al.add(getBeforehandGroupType("��������_�ƶȷ���", "����", BeforeHandGroupTypeVO.COUNT));
//		al.add(getBeforehandGroupType("��������_״̬", "����", BeforeHandGroupTypeVO.COUNT));
//		al.add(getBeforehandGroupType("��Ʒ����_״̬", "����", BeforeHandGroupTypeVO.COUNT));
//		al.add(getBeforehandGroupType("ҵ������(��2��)_״̬", "����", BeforeHandGroupTypeVO.COUNT));
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

	public HashMap getGroupFieldOrderConfig() {
		HashMap map = new HashMap();
		try {
			map.put("������ò", dmo.getStringArrayFirstColByDS(null, "select name from pub_comboboxdict where type='н��_������ò' order by seq"));
			map.put("ѧ��", dmo.getStringArrayFirstColByDS(null, "select name from pub_comboboxdict where type='н��_ѧ��' order by seq"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	public String [] getSplit(String str){
		String [] split=str.split(";");
		return split;
		
	}

}

