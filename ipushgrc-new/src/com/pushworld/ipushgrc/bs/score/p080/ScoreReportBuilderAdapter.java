package com.pushworld.ipushgrc.bs.score.p080;

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
 * Υ�����ͳ���߼��ࡾ���/2013-05-16��
 * @com.pushworld.ipushgrc.ui.score.p080.ScoreStatisWKPanel
 * @author lcj
 *
 */
public class ScoreReportBuilderAdapter extends MultiLevelReportDataBuilderAdapter {

	public HashVO[] buildReportData(HashMap condition) throws Exception {
		CommDMO dmo = new CommDMO();
		//���в�ѯ����
		String scoretypeid = (String) condition.get("SCORETYPEID");//Υ������
		String riskrank = (String) condition.get("RISKRANK");//���յȼ�
		String findrank = (String) condition.get("FINDRANK");//��������

		String deptid = (String) condition.get("DEPTID");//Υ�����
		String state = (String) condition.get("state");//״̬
		RefItemVO ref_effectdate = (RefItemVO) condition.get("obj_EFFECTDATE");//��Ч����,�������ͱȽ�����,��Ҫȡ�ò��ն���ֵ,Ȼ���滻���е�{itemkey}

		String punishtype = (String) condition.get("punishtype");//�ͷ�����

		TBUtil tbutil = new TBUtil();
		StringBuffer sb_sql = new StringBuffer("select id, scoretype Υ������,riskrank ���յȼ�,findrank ��������, deptid Υ�����,effectdate ��Ч����,finalscore Υ�����,finalmoney ����,state ״̬,punishtype �ͷ�����  from v_score_user where 1=1 ");//�����ѯ���еļ�¼

		if (scoretypeid != null && !"".equals(scoretypeid)) {
			sb_sql.append(" and scoretypeid in(" + tbutil.getInCondition(scoretypeid) + ") ");
		}
		if (riskrank != null && !"".equals(riskrank)) {
			sb_sql.append(" and riskrank in(" + tbutil.getInCondition(riskrank) + ")");
		}
		if (findrank != null && !"".equals(findrank)) {
			sb_sql.append(" and findrank in(" + tbutil.getInCondition(findrank) + ") ");
		}
		if (deptid != null && !"".equals(deptid)) {
			sb_sql.append(" and deptid in(" + tbutil.getInCondition(deptid) + ") ");
		}
		if (state != null && !"".equals(state)) {
			sb_sql.append(" and state in(" + tbutil.getInCondition(state) + ") ");
		}
		if (ref_effectdate != null) {//��Ч����
			String str_cons = ref_effectdate.getHashVO().getStringValue("querycondition"); //ȡ�ò�ѯ����itemkey
			str_cons = tbutil.replaceAll(str_cons, "{itemkey}", "effectdate"); //�滻���е��������!Ϊʵ���ֶ���!
			sb_sql.append(" and " + str_cons);
		}
		if (punishtype != null && !"".equals(punishtype)) {
			sb_sql.append(" and punishtype in(" + tbutil.getInCondition(punishtype) + ") ");
		}

		sb_sql.append(" order by effectdate desc");

		HashVO[] vos = dmo.getHashVoArrayByDS(null, sb_sql.toString());

		for (int i = 0; i < vos.length; i++) {
			String str_punishtype = vos[i].getStringValue("�ͷ�����");
			if (str_punishtype == null || str_punishtype.equals("")) {
				vos[i].setAttributeValue("�ͷ�����", "�޳ͷ�");
			}
		}

		//�������ͽṹ��ʾ��ͬ�㼶�����/2013-05-16��
		ReportDMO reportDMO = new ReportDMO(); //
		reportDMO.addOneFieldFromOtherTree(vos, "Υ�����(��1��)", "Υ�����", "select id,name,parentid from pub_corp_dept", 2, true, 2); //�ӵ�2�㿪ʼ ���ϵ�2��
		reportDMO.addOneFieldFromOtherTree(vos, "Υ�����(��2��)", "Υ�����", "select id,name,parentid from pub_corp_dept", 3, true, 2); //�ӵ�2�㿪ʼ ���ϵ�3��

		new ReportUtil().leftOuterJoin_YSMDFromDateTime(vos, "��Ч����", "��Ч����", "��");//����ʱ����ʾ��ʽ
		return vos;
	}

	public String[] getGroupFieldNames() {
		return new String[] { "Υ������", "���յȼ�", "��������", "Υ�����(��1��)", "Υ�����(��2��)", "��Ч����", "Υ�����", "����", "״̬", "�ͷ�����" };
	}

	public String[] getSumFiledNames() {
		return new String[] { "����", "Υ�����" };
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
			al.add(getBeforehandGroupType("��Ч����_Υ�����(��1��)", "Υ�����", BeforeHandGroupTypeVO.SUM));
			al.add(getBeforehandGroupType("��Ч����_Υ�����(��2��)", "Υ�����", BeforeHandGroupTypeVO.SUM));
			al.add(getBeforehandGroupType("Υ������_Υ�����(��1��)", "����", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("Υ������_Υ�����(��2��)", "����", BeforeHandGroupTypeVO.COUNT));
			int model = TBUtil.getTBUtil().getSysOptionIntegerValue("Υ����ֿ۷�ģʽ", 1);
			if (model == 1) {//���ʹ�÷��յȼ�ά��
				al.add(getBeforehandGroupType("���յȼ�_Υ�����(��1��)", "����", BeforeHandGroupTypeVO.COUNT));
				al.add(getBeforehandGroupType("���յȼ�_Υ�����(��2��)", "����", BeforeHandGroupTypeVO.COUNT));
			}

			al.add(getBeforehandGroupType("��������_Υ�����(��1��)", "����", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("��������_Υ�����(��2��)", "����", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("�ͷ�����_Υ�����(��1��)", "����", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("�ͷ�����_Υ�����(��2��)", "����", BeforeHandGroupTypeVO.COUNT));
		} else {//ͼ��ķ������������ĸ�����ʮ�����µĲźÿ������/2013-05-16��
			al.add(getBeforehandGroupType("���յȼ�_Υ�����(��1��)", "����", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("��������_Υ�����(��1��)", "����", BeforeHandGroupTypeVO.COUNT));
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

	//���������ֶΡ����/2013-05-16��
	public HashMap getGroupFieldOrderConfig() {
		HashMap map = new HashMap();
		try {
			CommDMO dmo = new CommDMO();
			String[] riskrank = dmo.getStringArrayFirstColByDS(null, "select id from pub_comboboxdict where type='Υ�����_���յȼ�'  order by seq");
			String[] findrank = dmo.getStringArrayFirstColByDS(null, "select id from pub_comboboxdict where type='Υ�����_��������' order by seq");
			String[] punishtype = dmo.getStringArrayFirstColByDS(null, "select '�޳ͷ�' punish from wltdual union all select * from(select punish from SCORE_PUNISH order by score)");
			map.put("���յȼ�", riskrank);
			map.put("��������", findrank);
			map.put("�ͷ�����", punishtype);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	public HashMap getDrilGroupBind() {
		HashMap map = new HashMap(); //
		map.put("Υ�����(��1��)", "Υ�����(��2��)"); //ʵ�ֵ��Υ�����(��1��)���ɵ���Υ�����(��2��)��ͳ�ƽ��
		return map;
	}

	public String getDrillTempletCode() throws Exception {
		return "SCORE_USER_LCJ_Q05";
	}

}
