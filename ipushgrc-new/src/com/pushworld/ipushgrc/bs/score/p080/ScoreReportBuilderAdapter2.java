package com.pushworld.ipushgrc.bs.score.p080;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter;
import cn.com.infostrategy.bs.report.ReportDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;
import cn.com.infostrategy.to.report.ReportUtil;

/**
 * Υ�����ͳ���߼��ࡾ���/2013-05-16��& ����
 * @com.pushworld.ipushgrc.ui.score.p080.ScoreStatisWKPanel
 * @author lcj&hm
 *
 */
public class ScoreReportBuilderAdapter2 extends MultiLevelReportDataBuilderAdapter {
	private CommDMO dmo = new CommDMO();
	private boolean reportUserByCode = TBUtil.getTBUtil().getSysOptionBooleanValue("Υ�����ͳ����Ա�Ƿ���ʾ����", true);

	public HashVO[] buildReportData(HashMap condition) throws Exception {
		//���в�ѯ����
		String scoretypeid = (String) condition.get("SCORETYPEID");//Υ������
		String riskrank = (String) condition.get("RISKRANK");//���յȼ�
		String findrank = (String) condition.get("FINDRANK");//��������

		String deptid = (String) condition.get("DEPTID");//Υ�����
		String state = (String) condition.get("state");//״̬
		RefItemVO ref_effectdate = (RefItemVO) condition.get("obj_EFFECTDATE");//��Ч����,�������ͱȽ�����,��Ҫȡ�ò��ն���ֵ,Ȼ���滻���е�{itemkey}
		String punishtype = (String) condition.get("punishtype");//�ͷ�����

		TBUtil tbutil = new TBUtil();
		StringBuffer sb_sql = new StringBuffer(
				"select t1.id,t1.HAPPENDATE ��������, t1.scoretype Υ������,t1.findrank ��������,t1.deptid, t1.deptid ����,t1.effectdate ��Ч����,t1.finalscore �۷�,t1.finalmoney ����,t1.state ״̬,t1.punishtype �ͷ�����,t1.scorestandardid,t1.scorestandard2 Υ����Ϊ����,t1.userid,t1.username ��Ա,t1.usercode ��¼�� , t1.CREATEDEPT from v_score_user t1 left join pub_corp_dept t2 on t1.deptid = t2.id left join (select userid,max(seq) seq from v_pub_user_post_1 group by userid) t3 on t3.userid = t1.userid where 1=1 ");//�����ѯ���еļ�¼

		StringBuffer sb_sql2 = new StringBuffer("select t1.deptid,t1.userid,t1.usercode ��¼��,sum(t1.finalscore) score from v_score_user t1 left join pub_corp_dept t2 on t1.deptid = t2.id where 1=1");

		if (scoretypeid != null && !"".equals(scoretypeid)) {
			sb_sql.append(" and t1.scoretypeid in(" + tbutil.getInCondition(scoretypeid) + ") ");
			sb_sql2.append(" and t1.scoretypeid in(" + tbutil.getInCondition(scoretypeid) + ") ");
		}
		if (riskrank != null && !"".equals(riskrank)) {
			sb_sql.append(" and t1.riskrank in(" + tbutil.getInCondition(riskrank) + ")");

			sb_sql2.append(" and t1.riskrank in(" + tbutil.getInCondition(riskrank) + ")");

		}
		if (findrank != null && !"".equals(findrank)) {
			sb_sql.append(" and t1.findrank in(" + tbutil.getInCondition(findrank) + ") ");

			sb_sql2.append(" and t1.findrank in(" + tbutil.getInCondition(findrank) + ") ");
		}
		if (deptid != null && !"".equals(deptid)) {
			sb_sql.append(" and t1.deptid in(" + tbutil.getInCondition(deptid) + ") ");

			sb_sql2.append(" and t1.deptid in(" + tbutil.getInCondition(deptid) + ") ");
		}
		if (state != null && !"".equals(state)) {
			sb_sql.append(" and t1.state in(" + tbutil.getInCondition(state) + ") ");
			sb_sql2.append(" and t1.state in(" + tbutil.getInCondition(state) + ") ");
		}
		if (ref_effectdate != null) {//��Ч����
			String str_cons = ref_effectdate.getHashVO().getStringValue("querycondition"); //ȡ�ò�ѯ����itemkey
			str_cons = tbutil.replaceAll(str_cons, "{itemkey}", "t1.effectdate"); //�滻���е��������!Ϊʵ���ֶ���!
			sb_sql.append(" and " + str_cons);
			sb_sql2.append(" and " + str_cons);
		}
		if (punishtype != null && !"".equals(punishtype)) {
			sb_sql.append(" and t1.punishtype in(" + tbutil.getInCondition(punishtype) + ") ");
			sb_sql2.append(" and t1.punishtype in(" + tbutil.getInCondition(punishtype) + ") ");
		}
		if (ref_effectdate != null) {//��Ч����
			String str_cons = ref_effectdate.getHashVO().getStringValue("querycondition"); //ȡ�ò�ѯ����itemkey
			str_cons = tbutil.replaceAll(str_cons, "{itemkey}", "t1.effectdate"); //�滻���е��������!Ϊʵ���ֶ���!
			if (str_cons.contains("" + tbutil.getCurrDate().substring(0, 4))) { //����е���ľͲ�����ʱ�������ˡ�ͨ����������
				sb_sql.append(" order by t2.linkcode");
			} else {
				sb_sql.append(" order by t1.effectdate desc,t2.linkcode");
			}
		} else {
			sb_sql.append(" order by t1.effectdate desc,t2.linkcode");
		}
		
		sb_sql.append(",case when t3.seq is null then 999 else t3.seq end ");
		
		sb_sql2.append(" group by deptid,userid,usercode");
		HashVO[] vos = dmo.getHashVoArrayByDS(null, sb_sql.toString());
		ReportUtil util = new ReportUtil();
		util.leftOuterJoin_YSMDFromDateTime(vos, "��������(��)", "��������", "��");
		HashVO hvo[] = dmo.getHashVoArrayByDS(null, sb_sql2.toString());
		//�������ͽṹ��ʾ��ͬ�㼶�����/2013-05-16��
		ReportDMO reportDMO = new ReportDMO(); //
		util.leftOuterJoin_YSMDFromDateTime(vos, "��Ч����(��)", "��Ч����", "��");
		reportDMO.leftOuterJoin_TreeTableFieldName(vos, "����", "pub_corp_dept", "name", "id", "����", "id", "parentid", 2);
		reportDMO.leftOuterJoin_TreeTableFieldName(vos, "����_��һ��", "pub_corp_dept", "name", "id", "����", "id", "parentid", 3);
		reportDMO.leftOuterJoin_TableFieldName(vos, "����", "pub_corp_dept", "name", "id", "����");
		reportDMO.leftOuterJoin_TableFieldName(vos, "�Ǽǻ���", "pub_corp_dept", "name", "id", "CREATEDEPT");
		HashMap<String, String> totle = new HashMap<String, String>();
		for (int i = 0; i < hvo.length; i++) {
			totle.put(hvo[i].getStringValue("deptid") + "_" + hvo[i].getStringValue("userid"), hvo[i].getStringValue("score"));
		}
		for (int i = 0; i < vos.length; i++) {
			String str_punishtype = vos[i].getStringValue("�ͷ�����");
			if (str_punishtype == null || str_punishtype.equals("")) {
				vos[i].setAttributeValue("�ͷ�����", "�޳ͷ�");
			}
			String userid = vos[i].getStringValue("userid");
			String deptid_ = vos[i].getStringValue("deptid");
			if (!tbutil.isEmpty(userid) && totle.containsKey(deptid_ + "_" + userid)) {
				String score = totle.get(deptid_ + "_" + userid);
				if (tbutil.isEmpty(score)) {
					score = "0";
				}
				vos[i].setAttributeValue("�ܻ���", score);
			} else {
				vos[i].setAttributeValue("�ܻ���", "δ֪");
			}
			if (tbutil.isEmpty(vos[i].getStringValue("����_��һ��"))) {
				vos[i].setAttributeValue("����_��һ��", "-");
			}
		}

		boolean needCon = false;
		boolean needAddItem = false; //�Ƿ���Ҫ��Ŀ������Ŀ
		if (condition.get("obj_colheader") != null) {
			String[] headers = (String[]) condition.get("obj_colheader");
			for (int i = 0; i < headers.length; i++) {
				if (!needCon && headers[i] != null && headers[i].contains("Υ����Ϊ����")) {
					needCon = true;
				}
				if (!needAddItem && headers[i] != null && headers[i].contains("��Ŀ")) {
					needAddItem = true;
				}
			}
			if (!needCon) {
				String[] colders = (String[]) condition.get("obj_colheader");
				for (int i = 0; i < colders.length; i++) {
					if (!needCon && colders[i] != null && colders[i].contains("Υ����Ϊ����")) {
						needCon = true;
					}
					if (!needAddItem && colders[i] != null && colders[i].contains("��Ŀ")) {
						needAddItem = true;
					}
				}
			}
		}
		if (needAddItem || condition.get("obj_colheader") == null) {
			leftJoin(vos, "��Ŀ", "����Ŀ", "scorestandardid");
		}
		return vos;
	}

	public String[] getGroupFieldNames() {
		return new String[] { "��������", "��Ч����", "��������(��)", "��Ч����(��)", "��¼��", "��Ա", "��������", "����", "����", "Υ����Ϊ����", "��Ŀ", "����Ŀ", "�Ǽǻ���", "�ܻ���" };
	}

	public String[] getSumFiledNames() {
		return new String[] { "�۷�" };
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid() {
		ArrayList al = new ArrayList();
		BeforeHandGroupTypeVO vo = null;
		vo = new BeforeHandGroupTypeVO();
		vo.setName("��Ч����_����(��1��)_��������");
		vo.setRowHeaderGroupFields(new String[] { "��Ч����(��)", "��������" });
		vo.setColHeaderGroupFields(new String[] { "����" });
		vo.setComputeGroupFields(new String[][] { { "�۷�", BeforeHandGroupTypeVO.SUM } });
		vo.setRowGroupSubTotal(false);
		vo.setColGroupSubTotal(false);
		al.add(vo);

		vo = new BeforeHandGroupTypeVO();
		vo.setName("��Ч����_����_����_" + (reportUserByCode ? "��¼��_" : "") + "��Ա_�ܻ���_�ǻ�����");
		vo.setRowHeaderGroupFields(new String[] { "��Ч����(��)" });
		if (reportUserByCode) {
			vo.setColHeaderGroupFields(new String[] { "����", "����", "��¼��", "��Ա", "�ܻ���", "�Ǽǻ���" });
		} else {
			vo.setColHeaderGroupFields(new String[] { "����", "����", "��Ա", "�ܻ���", "�Ǽǻ���" });
		}
		vo.setComputeGroupFields(new String[][] { { "�۷�", BeforeHandGroupTypeVO.SUM } });
		vo.setRowGroupSubTotal(false);
		vo.setColGroupSubTotal(false);
		al.add(vo);

		vo = new BeforeHandGroupTypeVO();
		vo.setName("��Ч����_����_����_" + (reportUserByCode ? "��¼��_" : "") + "��Ա");
		vo.setRowHeaderGroupFields(new String[] { "��Ч����(��)" });
		if (reportUserByCode) {
			vo.setColHeaderGroupFields(new String[] { "����", "��¼��", "����", "��Ա" });
		} else {
			vo.setColHeaderGroupFields(new String[] { "����", "����", "��Ա" });
		}
		vo.setComputeGroupFields(new String[][] { { "�۷�", BeforeHandGroupTypeVO.SUM } });
		vo.setRowGroupSubTotal(false);
		vo.setColGroupSubTotal(false);
		al.add(vo);

		vo = new BeforeHandGroupTypeVO();
		vo.setName("��Ч����_����_��һ��_����_" + (reportUserByCode ? "��¼��_" : "") + "��Ա");
		vo.setRowHeaderGroupFields(new String[] { "��Ч����(��)" });
		if (reportUserByCode) {
			vo.setColHeaderGroupFields(new String[] { "����", "����_��һ��", "����", "��¼��", "��Ա" });
		} else {
			vo.setColHeaderGroupFields(new String[] { "����", "����_��һ��", "����", "��Ա" });
		}
		vo.setComputeGroupFields(new String[][] { { "�۷�", BeforeHandGroupTypeVO.SUM } });
		vo.setRowGroupSubTotal(false);
		vo.setColGroupSubTotal(false);
		al.add(vo);

		vo = new BeforeHandGroupTypeVO();
		vo.setName("��Ч����_����_����_��������");
		vo.setRowHeaderGroupFields(new String[] { "��Ч����(��)", "��������" });
		vo.setColHeaderGroupFields(new String[] { "����", "����" });
		vo.setComputeGroupFields(new String[][] { { "�۷�", BeforeHandGroupTypeVO.SUM } });
		vo.setRowGroupSubTotal(false);
		vo.setColGroupSubTotal(false);
		al.add(vo);

		vo = new BeforeHandGroupTypeVO();
		vo.setName("��Ч����_Υ����Ϊ����_Ƶ��");
		vo.setRowHeaderGroupFields(new String[] { "��Ч����(��)" });
		vo.setColHeaderGroupFields(new String[] { "Υ����Ϊ����" });
		vo.setComputeGroupFields(new String[][] { { "����Ƶ��", BeforeHandGroupTypeVO.COUNT } });
		vo.setRowGroupSubTotal(false);
		vo.setColGroupSubTotal(false);
		al.add(vo);

		vo = new BeforeHandGroupTypeVO();
		vo.setName("��Ч����_Υ����Ϊ����_�۷�");
		vo.setRowHeaderGroupFields(new String[] { "��Ч����(��)" });
		vo.setColHeaderGroupFields(new String[] { "Υ����Ϊ����" });
		vo.setComputeGroupFields(new String[][] { { "�۷�", BeforeHandGroupTypeVO.SUM } });
		vo.setRowGroupSubTotal(false);
		vo.setColGroupSubTotal(false);
		al.add(vo);

		vo = new BeforeHandGroupTypeVO();
		vo.setName("��Ч����_��Ŀ_Υ����Ϊ����_Ƶ��");
		vo.setRowHeaderGroupFields(new String[] { "��Ч����(��)" });
		vo.setColHeaderGroupFields(new String[] { "��Ŀ", "����Ŀ", "Υ����Ϊ����" });
		vo.setComputeGroupFields(new String[][] { { "����Ƶ��", BeforeHandGroupTypeVO.COUNT } });
		vo.setRowGroupSubTotal(false);
		vo.setColGroupSubTotal(false);
		al.add(vo);
		return (BeforeHandGroupTypeVO[]) al.toArray(new BeforeHandGroupTypeVO[0]);
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
			String[] punishtype = dmo.getStringArrayFirstColByDS(null, "select '�޳ͷ�' punish from wltdual union all select * from(select punish from SCORE_PUNISH order by score) as b");
			map.put("���յȼ�", riskrank);
			map.put("��������", findrank);
			map.put("�ͷ�����", punishtype);
			int year = Integer.parseInt(TBUtil.getTBUtil().getCurrDate().substring(0, 4));
			String y_descrs[] = new String[12];
			for (int i = 0; i < 3; i++) {
				y_descrs[i * 4 + 0] = (year - i) + "��" + 4 + "����";
				y_descrs[i * 4 + 1] = (year - i) + "��" + 3 + "����";
				y_descrs[i * 4 + 2] = (year - i) + "��" + 2 + "����";
				y_descrs[i * 4 + 3] = (year - i) + "��" + 1 + "����";
			}
			map.put("��Ч����(��)", y_descrs);

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

	public void leftJoin(HashVO[] _hvs, String _newItemName1, String _newItemName2, String _joinedFieldName) throws Exception {

		String _sql = "select id,typename,parentid from SCORE_TYPE";
		//		HashMap standMap = new CommDMO().getHashMapBySQLByDS(null, "select id,content from cmp_score_stand");
		HashVO vos[] = new CommDMO().getHashVoArrayAsTreeStructByDS(null, _sql, "id", "parentid", "seq", "");

		HashMap standMap = new HashMap(); //������ 
		for (int i = 0; i < vos.length; i++) {
			standMap.put(vos[i].getStringValue("id"), vos[i]);
		}
		HashVO standard2[] = dmo.getHashVoArrayByDS(null, "select id,scoretype,point from SCORE_STANDARD2");
		HashMap standMap_2 = new HashMap(); //����������
		for (int i = 0; i < standard2.length; i++) {
			standMap_2.put(standard2[i].getStringValue("id"), standard2[i]);
		}
		ReportDMO dmo = new ReportDMO();
		TBUtil tbUtil = new TBUtil();
		for (int i = 0; i < _hvs.length; i++) {
			String str_custids = _hvs[i].getStringValue(_joinedFieldName);
			if (str_custids == null || str_custids.trim().equals("")) {
				continue;
			}
			if (!standMap_2.containsKey(str_custids)) {
				continue;
			}
			HashVO vo = (HashVO) standMap_2.get(str_custids);
			String scoretype = vo.getStringValue("scoretype");
			if (scoretype == null || scoretype.equals("")) {
				continue;
			}

			Vector p_stand = new Vector();
			if (scoretype != null && !scoretype.equals("")) {
				if (standMap.containsKey(scoretype)) {
					HashVO currStandVO = (HashVO) standMap.get(scoretype);
					String pids = currStandVO.getStringValue("$parentpathids");
					String parent_standid[] = tbUtil.split(pids, ";");
					for (int j = 0; j < parent_standid.length; j++) {
						if (parent_standid[j] != null && !parent_standid[j].equals("") && !parent_standid[j].equals(str_custids) && standMap.containsKey(parent_standid[j])) {
							HashVO pvo = (HashVO) standMap.get(parent_standid[j]);
							p_stand.add(pvo.getStringValue("typename"));
						}
					}
				}
			}
			if (p_stand.size() == 1) {
				_hvs[i].setAttributeValue(_newItemName1, p_stand.get(0)); //��������
				_hvs[i].setAttributeValue(_newItemName2, "-"); //��������
			} else if (p_stand.size() == 2) {
				_hvs[i].setAttributeValue(_newItemName1, p_stand.get(0)); //��������
				_hvs[i].setAttributeValue(_newItemName2, p_stand.get(1)); //��������
			} else {
				_hvs[i].setAttributeValue(_newItemName1, "-"); //��������
				_hvs[i].setAttributeValue(_newItemName2, "-"); //��������
			}
			//			_hvs[i].setAttributeValue(_joinedFieldName, vo.getStringValue("point")); //��������
		}
	}

	public String getDrillTempletCode() throws Exception {
		return "SCORE_USER_LCJ_Q05";
	}
}
