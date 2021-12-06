package com.pushworld.ipushgrc.bs.wfrisk.p060;

import java.util.ArrayList;
import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter;
import cn.com.infostrategy.bs.report.ReportDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;
import cn.com.infostrategy.to.report.ReportUtil;

public class WFReportBuilderAdapter extends MultiLevelReportDataBuilderAdapter {

	public HashVO[] buildReportData(HashMap condition) throws Exception {
		CommDMO dmo = new CommDMO();
		//���в�ѯ����
		String filestate = (String) condition.get("filestate");//�ļ�״̬,��ѡ����,ֵΪ��3;5;��
		String blcorpid = (String) condition.get("blcorpid");//��������
		String bsactid = (String) condition.get("bsactid");//ҵ������
		String ictypeid = (String) condition.get("ictypeid");//�ڿ�Ҫ��

		ComBoxItemVO combox_type = (ComBoxItemVO) condition.get("obj_cmpfiletype");//�ļ�����,���id������ʾname
		String cmpfiletype = null;
		if (combox_type != null) {
			cmpfiletype = combox_type.getName();
		}

		TBUtil tbutil = new TBUtil();
		StringBuffer sb_sql = new StringBuffer("select WFPROCESS_ID id, cmpfiletype �ļ�����,filestatename �ļ�״̬, blcorpid ��������,bsactid ҵ��,ictypeid �ڿ�Ҫ��, publishdate ��������,wfprocess_id from v_process_file where 1=1 ");

		if (filestate != null && !"".equals(filestate)) {
			sb_sql.append(" and filestate in(" + tbutil.getInCondition(filestate) + ") ");
		}
		if (blcorpid != null && !"".equals(blcorpid)) {
			sb_sql.append(" and blcorpid in(" + tbutil.getInCondition(blcorpid) + ") ");
		}
		if (bsactid != null && !"".equals(bsactid)) {
			sb_sql.append(" and bsactid in(" + tbutil.getInCondition(bsactid) + ") ");
		}
		if (ictypeid != null && !"".equals(ictypeid)) {
			sb_sql.append(" and ictypeid in(" + tbutil.getInCondition(ictypeid) + ") ");
		}
		if (cmpfiletype != null && !"".equals(cmpfiletype)) {
			sb_sql.append(" and cmpfiletype in (" + tbutil.getInCondition(cmpfiletype) + ") ");//�޸��ļ�����Ϊ��ѡ���������/2012-03-19��
		}
		sb_sql.append(" order by publishdate desc");

		HashVO[] vos = dmo.getHashVoArrayByDS(null, sb_sql.toString());

		//�������ͽṹ��ʾ��ͬ�㼶�����/2012-03-12��
		ReportDMO reportDMO = new ReportDMO(); //
		reportDMO.addOneFieldFromOtherTree(vos, "ҵ��(��1��)", "ҵ��", "select id,name,parentid from bsd_bsact", 1, true, 1); //���ϵ�1��
		reportDMO.addOneFieldFromOtherTree(vos, "ҵ��(��2��)", "ҵ��", "select id,name,parentid from bsd_bsact", 2, true, 1); //���ϵ�2��

		reportDMO.addOneFieldFromOtherTree(vos, "�ڿ�Ҫ��(��1��)", "�ڿ�Ҫ��", "select id,name,parentid from bsd_icsys", 1, true, 1); //���ϵ�1��
		reportDMO.addOneFieldFromOtherTree(vos, "�ڿ�Ҫ��(��2��)", "�ڿ�Ҫ��", "select id,name,parentid from bsd_icsys", 2, true, 1); //���ϵ�2��

		reportDMO.addOneFieldFromOtherTree(vos, "��������(��1��)", "��������", "select id,name,parentid from pub_corp_dept", 2, true, 2); //�ӵ�2�㿪ʼ ���ϵ�2��
		reportDMO.addOneFieldFromOtherTree(vos, "��������(��2��)", "��������", "select id,name,parentid from pub_corp_dept", 3, true, 2); //�ӵ�2�㿪ʼ ���ϵ�3��

		new ReportUtil().leftOuterJoin_YSMDFromDateTime(vos, "��������", "��������", "��");//����ʱ����ʾ��ʽ

		return vos;
	}

	public String[] getGroupFieldNames() {
		return new String[] { "�ļ�����", "�ļ�״̬", "��������(��1��)", "��������(��2��)", "ҵ��(��1��)", "ҵ��(��2��)", "�ڿ�Ҫ��(��1��)", "�ڿ�Ҫ��(��2��)", "��������" };
	}

	public String[] getSumFiledNames() {
		return new String[] { "����" };
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid() {
		return getBeforehandGroupType(1);
	}

	@Override
	/**
	 * ��ȡ��ϸʱ��ģ�����!!
	 */
	public String getDrillTempletCode() throws Exception {
		return "PUB_WF_PROCESS_CODE2"; //
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Chart() {
		return getBeforehandGroupType(2);
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType(int _type) {
		ArrayList al = new ArrayList(); //
		if (_type == 1) {
			al.add(getBeforehandGroupType("�ļ�����_��������(��1��)", "����", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("�ļ�����_��������(��2��)", "����", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("�ļ�״̬_��������(��1��)", "����", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("�ļ�״̬_��������(��2��)", "����", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("ҵ��(��1��)_��������(��1��)", "����", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("ҵ��(��2��)_��������(��2��)", "����", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("�ڿ�Ҫ��(��1��)_��������(��1��)", "����", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("�ڿ�Ҫ��(��2��)_��������(��2��)", "����", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("ҵ��(��1��)_�ڿ�Ҫ��(��1��)", "����", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("ҵ��(��2��)_�ڿ�Ҫ��(��2��)", "����", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("ҵ��(��1��)_��������", "����", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("ҵ��(��2��)_��������", "����", BeforeHandGroupTypeVO.COUNT));
		} else {//ͼ��ķ������������ĸ�����ʮ�����µĲźÿ������/2012-03-12��
			al.add(getBeforehandGroupType("�ļ�����_��������(��1��)", "����", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("�ļ�״̬_��������(��1��)", "����", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("�ڿ�Ҫ��(��1��)_��������(��1��)", "����", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("ҵ��(��1��)_�ļ�����", "����", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("�ڿ�Ҫ��(��1��)_�ļ�����", "����", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("��������_��������(��1��)", "����", BeforeHandGroupTypeVO.COUNT));
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

	//���������ֶΡ����/2012-03-29��
	public HashMap getGroupFieldOrderConfig() {
		HashMap map = new HashMap();
		try {
			CommDMO dmo = new CommDMO();
			String[] cmpfiletype = dmo.getStringArrayFirstColByDS(null, "select name from pub_comboboxdict where type='�ļ�����' order by seq");
			String[] filestate = dmo.getStringArrayFirstColByDS(null, "select name  from pub_comboboxdict where type='�ļ�״̬' order by seq");
			map.put("�ļ�����", cmpfiletype);
			map.put("�ļ�״̬", filestate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}
