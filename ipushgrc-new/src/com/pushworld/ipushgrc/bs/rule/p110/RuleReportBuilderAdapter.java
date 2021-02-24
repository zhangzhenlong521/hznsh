package com.pushworld.ipushgrc.bs.rule.p110;

import java.util.ArrayList;
import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter;
import cn.com.infostrategy.bs.report.ReportDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;
import cn.com.infostrategy.to.report.ReportUtil;

/**
 * �ƶ�ͳ��!!
 * @author xch
 *
 */
public class RuleReportBuilderAdapter extends MultiLevelReportDataBuilderAdapter {
	TBUtil tbutil = new TBUtil();
	CommDMO dmo = new CommDMO();

	public HashVO[] buildReportData(HashMap condition) throws Exception {
		String str_blcorp = (String) condition.get("blcorp");//���ܲ���
		String str_ruletype = (String) condition.get("ruletype"); //�ƶȷ���
		String str_busitype = (String) condition.get("busitype");//ҵ������
		String str_ictype = (String) condition.get("ictype");//�ڿط���
		String str_producttype = (String) condition.get("producttype");//�ڿط���

		RefItemVO ref_publishdate = (RefItemVO) condition.get("obj_publishdate");//��������,�������ͱȽ�����,��Ҫȡ�ò��ն���ֵ,Ȼ���滻���е�{itemkey},����˲�֪�����!
		String state = (String) condition.get("state"); //״̬

		StringBuffer sql_sb = new StringBuffer();
		sql_sb.append("select id id, blcorp ���ܲ���,ruletype �ƶȷ���,publishdate ��������,busitype ҵ������, ictype �ڿط��� ,producttype ��Ʒ���� ,state ״̬ from rule_rule where 1=1");

		//���ܲ���
		if (str_blcorp != null && !str_blcorp.equals("")) {
			sql_sb.append(" and blcorp in (" + tbutil.getInCondition(str_blcorp) + ")");
		}

		//�ƶȷ����ѯ����
		if (str_ruletype != null && !str_ruletype.equals("")) {
			sql_sb.append(" and ruletype in (" + tbutil.getInCondition(str_ruletype) + ")");
		}

		//ҵ�����Ͳ�ѯ����,���Ͷ�ѡ!!
		if (str_busitype != null && !str_busitype.equals("")) { //ҵ������
			sql_sb.append(" and busitype in (" + tbutil.getInCondition(str_busitype) + ")");
		}

		//�ڿ����Ͳ�ѯ����!!���Ͷ�ѡ!!
		if (str_ictype != null && !str_ictype.equals("")) { //�ڿ�����!
			sql_sb.append(" and ictype in (" + tbutil.getInCondition(str_ictype) + ")");
		}

		if (str_producttype != null && !str_producttype.equals("")) {//��Ʒ����
			sql_sb.append(" and str_producttype in (" + tbutil.getInCondition(str_producttype) + ")");
		}
		//��������!!!
		if (ref_publishdate != null) {
			String str_cons = ref_publishdate.getHashVO().getStringValue("querycondition"); //ȡ�ò�ѯ����itemkey
			str_cons = tbutil.replaceAll(str_cons, "{itemkey}", "publishdate"); //�滻���е��������!Ϊʵ���ֶ���!
			sql_sb.append(" and " + str_cons);
		}

		if (state != null && !state.equals("")) {
			sql_sb.append(" and state in (" + tbutil.getInCondition(state) + ")");
		}
		sql_sb.append(" order by publishdate"); //�������ʱ������ͳ�Ƴ����ļ������ҵ�
		HashVO[] vos = dmo.getHashVoArrayByDS(null, sql_sb.toString());

		ReportDMO reportDMO = new ReportDMO(); //
		//�������ܲ���
		reportDMO.addOneFieldFromOtherTree(vos, "���ܲ���(��1��)", "���ܲ���", "select id,name,parentid from pub_corp_dept", 2, true, 2); //���ϵ�1��
		reportDMO.addOneFieldFromOtherTree(vos, "���ܲ���(��2��)", "���ܲ���", "select id,name,parentid from pub_corp_dept", 3, true, 2); //���ϵ�2��

		//����ҵ�����͵�1�����2��!!
		reportDMO.addOneFieldFromOtherTree(vos, "ҵ������(��1��)", "ҵ������", "select id,name,parentid from bsd_bsact", 1, true, 1); //���ϵ�1��
		reportDMO.addOneFieldFromOtherTree(vos, "ҵ������(��2��)", "ҵ������", "select id,name,parentid from bsd_bsact", 2, true, 1); //���ϵ�2��

		//�����ڿط���֮��һ����ڶ���
		reportDMO.addOneFieldFromOtherTree(vos, "�ڿط���(��1��)", "�ڿط���", "select id,name,parentid from bsd_icsys", 1, true, 1); //���ϵ�1��
		reportDMO.addOneFieldFromOtherTree(vos, "�ڿط���(��2��)", "�ڿط���", "select id,name,parentid from bsd_icsys", 2, true, 1); //���ϵ�2��
		//��Ʒ����֮��һ����ڶ���
		reportDMO.addOneFieldFromOtherTree(vos, "��Ʒ����(��1��)", "��Ʒ����", "select id,name,parentid from bsd_product", 1, true, 1); //���ϵ�1��
		reportDMO.addOneFieldFromOtherTree(vos, "��Ʒ����(��2��)", "��Ʒ����", "select id,name,parentid from bsd_product", 2, true, 1); //���ϵ�2��
		new ReportUtil().leftOuterJoin_YSMDFromDateTime(vos, "��������", "��������", "��"); //��������������ɼ���!
		return vos;
	}

	public String[] getGroupFieldNames() {
		return new String[] { "�ƶȷ���", "��������", "���ܲ���(��1��)", "���ܲ���(��2��)", "ҵ������(��1��)", "ҵ������(��2��)", "�ڿط���(��1��)", "�ڿط���(��2��)", "��Ʒ����(��1��)", "��Ʒ����(��2��)", "״̬" };
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
		return "RULE_RULE_CODE6"; //
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Chart() {
		return getBeforehandGroupType();
	}

	/**
	 * �״�ͼ
	 */
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Splider() {
		ArrayList al = new ArrayList(); //
		al.add(getBeforehandGroupType("�ƶȷ���_ҵ������(��2��)", "����", BeforeHandGroupTypeVO.COUNT));
		al.add(getBeforehandGroupType("��������_ҵ������(��2��)", "����", BeforeHandGroupTypeVO.COUNT));
		return (BeforeHandGroupTypeVO[]) al.toArray(new BeforeHandGroupTypeVO[0]);
	}

	/**
	 * �����ͼ��!!!
	 * @return
	 */
	public BeforeHandGroupTypeVO[] getBeforehandGroupType() {
		ArrayList al = new ArrayList(); //
		al.add(getBeforehandGroupType("���ܲ���(��1��)_ҵ������(��1��)", "����", BeforeHandGroupTypeVO.COUNT)); //
		al.add(getBeforehandGroupType("���ܲ���(��2��)_ҵ������(��2��)", "����", BeforeHandGroupTypeVO.COUNT)); //

		al.add(getBeforehandGroupType("�ƶȷ���_ҵ������(��1��)", "����", BeforeHandGroupTypeVO.COUNT)); //
		al.add(getBeforehandGroupType("�ƶȷ���_ҵ������(��2��)", "����", BeforeHandGroupTypeVO.COUNT)); //

		al.add(getBeforehandGroupType("��������_ҵ������(��1��)", "����", BeforeHandGroupTypeVO.COUNT)); //
		al.add(getBeforehandGroupType("��������_ҵ������(��2��)", "����", BeforeHandGroupTypeVO.COUNT)); //

		al.add(getBeforehandGroupType("��������_�ڿط���(��1��)", "����", BeforeHandGroupTypeVO.COUNT)); //
		al.add(getBeforehandGroupType("��������_�ڿط���(��2��)", "����", BeforeHandGroupTypeVO.COUNT)); //

		al.add(getBeforehandGroupType("��������_�ƶȷ���", "����", BeforeHandGroupTypeVO.COUNT));
		al.add(getBeforehandGroupType("��������_״̬", "����", BeforeHandGroupTypeVO.COUNT));
		al.add(getBeforehandGroupType("��Ʒ����_״̬", "����", BeforeHandGroupTypeVO.COUNT));
		al.add(getBeforehandGroupType("ҵ������(��2��)_״̬", "����", BeforeHandGroupTypeVO.COUNT));
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
			map.put("״̬", dmo.getStringArrayFirstColByDS(null, "select name from pub_comboboxdict where type='����״̬' order by seq"));
			map.put("�ƶȷ���", dmo.getStringArrayFirstColByDS(null, "select name from pub_comboboxdict where type='�ƶ�����' order by seq"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

}
