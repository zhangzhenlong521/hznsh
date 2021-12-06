package com.pushworld.ipushgrc.bs.cmpevent;

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
 * �ɹ�����ͳ�ƣ�����������
 * 
 * @author hm
 * 
 */
public class WardReportBuilderAdapter extends MultiLevelReportDataBuilderAdapter {

	public HashVO[] buildReportData(HashMap condition) throws Exception {
		TBUtil tbutil = new TBUtil();

		RefItemVO ref_finddate = (RefItemVO) condition.get("obj_finddate"); // ��������

		String reportcorp = (String) condition.get("reportcorp");//���ͻ���
		RefItemVO reportdate = (RefItemVO) condition.get("obj_reportdate"); //��������
		String findchannel = (String) condition.get("findchannel");//��������

		StringBuffer sql = new StringBuffer();

		sql.append("select w.id id,w.findchannel ��������, cd.id �������,w.bsactname ҵ������ , substr(w.reportdate, 1, 7) �������� " + "from cmp_ward w " + "left join pub_corp_dept cd on w.reportcorp = cd.id where 1=1");

		//��������
		if (ref_finddate != null) {
			String str_cons = ref_finddate.getHashVO().getStringValue("querycondition"); //ȡ�ò�ѯ����itemkey
			str_cons = tbutil.replaceAll(str_cons, "{itemkey}", "w.finddate"); //�滻���е��������!Ϊʵ���ֶ���!
			sql.append(" and " + str_cons);
		}

		if (reportcorp != null && !"".equals(reportcorp)) {
			String cordIds = tbutil.getInCondition(reportcorp);
			sql.append(" and w.reportcorp in (" + cordIds + ")");
		}
		//����[��������]��[��������]SQL���� [YangQing/2013-08-27]
		if (reportdate != null && !"".equals(reportdate)) {
			String str_cons = reportdate.getHashVO().getStringValue("querycondition"); //ȡ�ò�ѯ����itemkey
			str_cons = tbutil.replaceAll(str_cons, "{itemkey}", "w.reportdate"); //�滻���е��������!Ϊʵ���ֶ���!
			sql.append(" and " + str_cons);
		}
		//��������
		if (!TBUtil.isEmpty(findchannel)) {
			sql.append(" and w.findchannel = '" + findchannel + "'");
		}
		CommDMO comm = new CommDMO();
		HashVO vos[] = comm.getHashVoArrayByDS(null, sql.toString());

		//�����������ҫ��/2012-03-14��
		ReportDMO reportDMO = new ReportDMO(); //
		reportDMO.addOneFieldFromOtherTree(vos, "�������(��1��)", "�������", "select id,name,parentid from pub_corp_dept", 2, true, 2); //���ϵ�1��
		reportDMO.addOneFieldFromOtherTree(vos, "�������(��2��)", "�������", "select id,name,parentid from pub_corp_dept", 3, true, 2); //���ϵ�2��

		new ReportUtil().leftOuterJoin_YSMDFromDateTime(vos, "��������", "��������", "��"); //��������������ɼ���!

		return vos;
	}

	@Override
	public String[] getGroupFieldNames() {
		return new String[] { "��������", "�������(��1��)", "�������(��2��)", "��������", "ҵ������" };
	}

	@Override
	public String[] getSumFiledNames() {
		return new String[] { "����" };
	}

	@Override
	/**
	 * ��ȡ��ϸʱ��ģ�����!!
	 */
	public String getDrillTempletCode() throws Exception {
		return "CMP_WARD_CODE1"; //
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
			al.add(getBeforehandGroupType("�������(��1��)_��������", "����", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("�������(��2��)_��������", "����", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("�������(��1��)_��������", "����", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("�������(��2��)_��������", "����", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("�������(��1��)_ҵ������", "����", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("�������(��2��)_ҵ������", "����", BeforeHandGroupTypeVO.COUNT));

			BeforeHandGroupTypeVO vo = new BeforeHandGroupTypeVO();
			vo.setName("�������(��1��)_��������_��������");
			vo.setRowHeaderGroupFields(new String[] { "�������(��1��)" });
			vo.setColHeaderGroupFields(new String[] { "��������", "��������" });
			vo.setComputeGroupFields(new String[][] { { "����", BeforeHandGroupTypeVO.COUNT } });
			al.add(vo);

			vo = new BeforeHandGroupTypeVO();
			vo.setName("�������(��2��)_��������_��������");
			vo.setRowHeaderGroupFields(new String[] { "�������(��2��)" });
			vo.setColHeaderGroupFields(new String[] { "��������", "��������" });
			vo.setComputeGroupFields(new String[][] { { "����", BeforeHandGroupTypeVO.COUNT } });
			al.add(vo);

		} else {
			al.add(getBeforehandGroupType("�������(��1��)_��������", "����", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("�������(��2��)_��������", "����", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("�������(��1��)_��������", "����", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("�������(��2��)_��������", "����", BeforeHandGroupTypeVO.COUNT));

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
}
