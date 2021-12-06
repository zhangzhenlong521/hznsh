package com.pushworld.ipushgrc.bs.cmpcheck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter;
import cn.com.infostrategy.bs.report.ReportDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;
import cn.com.infostrategy.to.report.ReportUtil;

/***
 * ���ģ���ͳ�Ʊ���
 * @author Gwang
 *
 */

public class CheckReportBuilderAdapter extends MultiLevelReportDataBuilderAdapter {
	TBUtil tbutil = new TBUtil();

	@Override
	public HashVO[] buildReportData(HashMap condition) throws Exception {
		String[] colheader = (String[]) condition.get("obj_colheader"); // ��ȡ�б�ͷ��ֵ
		StringBuffer con = new StringBuffer(); //sql���
		//����sql��ǰ���������⡾���/2013-06-20��������֮ǰ��ȡʱ���õ�ID��cmp_check_id,����ֶ���ظ�ֵ,���������޸���ͼ��ID�ֶ�[YangQing/2013-09-17]
		con.append("select v.id,v.cmp_check_id,v.checkname ������� , v.findchannel ��������, v.checkcorp deptid ,v.checkbegindate �������,v.eventcorpid �¼�������λ, v.eventType �¼����� from v_report_check2 v left join pub_corp_dept p "
				+ " on v.checkcorp = p.id left join cmp_check e on v.cmp_check_id = e.id where 1=1");

		//����ֻƴ����"checkname"����,���������ѯ����,��������ĸ�����.������/2012-07-18��
		String checkcorp = (String) condition.get("checkcorp");//��鵥λ
		String checkedcorp = (String) condition.get("checkedcorp");//����鵥λ
		RefItemVO checkbegindate = (RefItemVO) condition.get("obj_checkbegindate");//��鿪ʼ����,�������ͱȽ�����,��Ҫȡ�ò��ն���ֵ,Ȼ���滻���е�{itemkey}
		String status = (String) condition.get("status");//״̬
		//��鵥λ
		if (checkcorp != null && !checkcorp.equals("")) {
			con.append(" and e.checkcorp in (" + tbutil.getInCondition(checkcorp) + ")");
		}
		//����鵥λ
		if (checkedcorp != null && !checkedcorp.equals("")) {
			con.append(getMultiOrCondition("checkedcorp", checkedcorp));
		}
		//��鿪ʼ����
		if (checkbegindate != null) {
			String str_cons = checkbegindate.getHashVO().getStringValue("querycondition"); //ȡ�ò�ѯ����itemkey
			str_cons = tbutil.replaceAll(str_cons, "{itemkey}", "e.checkbegindate"); //�滻���е��������!Ϊʵ���ֶ���!
			con.append(" and " + str_cons);
		}
		//״̬
		if (status != null && !status.equals("")) {
			con.append(" and e.status in (" + tbutil.getInCondition(status) + ")");
		}
		if (condition.containsKey("checkname")) {
			String s = (String) condition.get("checkname");
			if (s != null && !s.equals("")) {
				con.append(" and checkname like '%" + s + "%' ");
			}
		}
		con.append(" order by p.linkcode,e.checkbegindate");

		CommDMO comm = new CommDMO();
		HashVO vos[] = comm.getHashVoArrayByDS(null, con.toString());
		ReportUtil util = new ReportUtil();
		util.leftOuterJoin_YSMDFromDateTime(vos, "�������", "�������", "��");
		ReportDMO dmo = new ReportDMO();
		dmo.leftOuterJoin_TreeTableFieldName(vos, "��鵥λ", "pub_corp_dept", "name", "id", "deptid", "id", "parentid", 2, true);

		for (int i = 0; i < colheader.length; i++) {//����ͳ�Ʊ����б�ͷ�����жϣ��Ƿ��и��ݡ��¼�������λ����ͳ�ƣ�����У���ִ�з���������������λ�����ݡ����򣬲�ִ�У���ά�ȡ���鵥λ_������ڡ����˴����������ά�ȣ�����ҲҪ�鿴�Ƿ���Ҫ�޸ģ���[YangQing/2013-0918]
			if (colheader[i].equals("�¼�������λ")) {
				vos = getMutityHashVOByItem(vos, "�¼�������λ");
				break;
			}
		}
		dmo.leftOuterJoin_TreeTableFieldName(vos, "�¼�������λ", "pub_corp_dept", "name", "id", "�¼�������λ", "id", "parentid", 2, true);

		return vos;
	}

	public HashVO[] getMutityHashVOByItem(HashVO[] _vos, String _item) {
		TBUtil tbutil = new TBUtil();
		List list = new ArrayList();
		for (int i = 0; i < _vos.length; i++) {
			HashVO vo = _vos[i];
			String str = vo.getStringValue(_item);
			if (str != null && !str.trim().equals("")) {
				String[] values = tbutil.split(str, ";");
				for (int j = 0; j < values.length; j++) {
					HashVO vonew = vo.deepClone();
					vonew.setAttributeValue(_item, values[j]);
					list.add(vonew);
				}
			} else {
				list.add(vo);
			}
		}
		return (HashVO[]) list.toArray(new HashVO[0]);
	}

	@Override
	public String[] getGroupFieldNames() {
		return new String[] { "�������", "��������", "��鵥λ", "�������", "�¼�����", "�¼�������λ" };
	}

	@Override
	public String[] getSumFiledNames() {
		return new String[] { "��������" };
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid() {
		ArrayList al = new ArrayList();
		BeforeHandGroupTypeVO vo = null;
		vo = new BeforeHandGroupTypeVO();
		vo.setName("��鵥λ_�������");
		vo.setRowHeaderGroupFields(new String[] { "�������" });
		vo.setColHeaderGroupFields(new String[] { "��鵥λ" });
		vo.setComputeGroupFields(new String[][] { { "��������", BeforeHandGroupTypeVO.COUNT } });
		al.add(vo);

		vo = new BeforeHandGroupTypeVO();
		vo.setName("�¼�������λ_�¼�����");
		vo.setRowHeaderGroupFields(new String[] { "�¼�����" });
		vo.setColHeaderGroupFields(new String[] { "�¼�������λ" });
		vo.setComputeGroupFields(new String[][] { { "��������", BeforeHandGroupTypeVO.COUNT } });
		al.add(vo);

		vo = new BeforeHandGroupTypeVO();
		vo.setName("�¼�������λ_�������_�¼�����");
		vo.setRowHeaderGroupFields(new String[] { "�������", "�¼�����" });
		vo.setColHeaderGroupFields(new String[] { "�¼�������λ" });
		vo.setComputeGroupFields(new String[][] { { "��������", BeforeHandGroupTypeVO.COUNT } });
		al.add(vo);

		vo = new BeforeHandGroupTypeVO();
		vo.setName("�¼�������λ_�������_��������");
		vo.setRowHeaderGroupFields(new String[] { "�������", "��������" });
		vo.setColHeaderGroupFields(new String[] { "�¼�������λ" });
		vo.setComputeGroupFields(new String[][] { { "��������", BeforeHandGroupTypeVO.COUNT } });
		al.add(vo);

		return (BeforeHandGroupTypeVO[]) al.toArray(new BeforeHandGroupTypeVO[0]);
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Chart() {
		ArrayList al = new ArrayList();
		BeforeHandGroupTypeVO vo = null;

		vo = new BeforeHandGroupTypeVO();
		vo.setName("�¼�������λ_�¼�����");
		vo.setRowHeaderGroupFields(new String[] { "�¼�����" });
		vo.setColHeaderGroupFields(new String[] { "�¼�������λ" });
		vo.setComputeGroupFields(new String[][] { { "��������", BeforeHandGroupTypeVO.COUNT } });
		al.add(vo);

		vo = new BeforeHandGroupTypeVO();
		vo.setName("�¼�������λ_�������");
		vo.setRowHeaderGroupFields(new String[] { "�������" });
		vo.setColHeaderGroupFields(new String[] { "�¼�������λ" });
		vo.setComputeGroupFields(new String[][] { { "��������", BeforeHandGroupTypeVO.COUNT } });
		al.add(vo);

		return (BeforeHandGroupTypeVO[]) al.toArray(new BeforeHandGroupTypeVO[0]);

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

	/**
	 * ��ȡ��ʾ��ϸ����[YangQing/2013-09-17]
	 */
	public String getDrillActionClassPath() throws Exception {
		return "com.pushworld.ipushgrc.ui.cmpcheck.p060.CheckReportDrill";
	}
}
