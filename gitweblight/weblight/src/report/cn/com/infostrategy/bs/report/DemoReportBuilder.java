package cn.com.infostrategy.bs.report;

import java.util.HashMap;
import java.util.Random;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;
import cn.com.infostrategy.to.report.ReportUtil;

/*
 * �ҵĲ��Ա�������������..
 */
public class DemoReportBuilder extends MultiLevelReportDataBuilderAdapter {

	private static HashVO[] cacheDatas = null; //Ϊ�˱�֤ÿ�γ����Ľ������һ����!���Ը�һ����̬������Ϊ����!��ֻ�ڵ�һ��ִ��ʱ��������,�����ֱ�Ӵӻ�����ȡ!

	String[] str_citys = new String[] { "�Ϻ�����@������", "�Ͼ�����@������", "���ݷ���@������", "���ݷ���@������", "��������@������", "������@������", "���ͺ��ط���@������", "��³ľ�����@������", "��������@������", "�������@������", "���ݷ���@������", "��������@������", "���ڷ���@������", "��������@������", "��������@������", "��������@������", "����������@������" }; //����
	String[] str_busitypes = new String[] { "�м�ҵ��", "����ҵ��", "���ÿ�ҵ��", "����ҵ��" }; //ҵ������!,
	String[] str_findchannels = new String[] { "���Ҽ��", "�ϼ��Ϲ���", "�ڲ����", "�ⲿ���", "��ܷ���", "ý���ع�" }; //��������!ý���ع�
	String[] str_risklevels = new String[] { "�������", "�߷���", "�еȷ���", "�ͷ���", "��С����" }; //
	String[] str_ctrltypes = new String[] { "���Ʋ���", "���ƻ�����Ч", "������Ч", "���ƹ���" }; //
	String[] str_reason = new String[] { "�ƶȲ���;û���ƶ�", "ִ�в���;û�˹�", "��Ա���ʲ���" }; //
	String[] str_satus = new String[] { "�����", "δ���" }; //
	private Random random = new Random(); //�����!!

	/**
	 * ��������..
	 */
	public HashVO[] buildReportData(HashMap _map) throws Exception {
		String str_sql = (String) _map.get("$SQL"); //��ѯ����,��������,ÿ���ں�̨ƴ��SQL̫������,ֱ�ӽ�ǰ̨ƴ�Ӻ��˵�SQL������������д���������̨�ù���ֱ�Ӽ��ڡ�select * from ���� where 1=1��+��str_sql�� ���ж�ˬ??��Ҫע���ֶ����Ƿ�ƥ��,�����ƥ��,��Ҫ�����ַ����滻!!���Լ��������xch/2012-06-20��
		String str_sql_1 = (String) _map.get("$SQL_1"); //��Ȩ�޲��Ե���������,���й���..
		//System.out.println("SQL:[" + str_sql + "]"); //
		//System.out.println("SQL_1:[" + str_sql_1 + "]"); //
		if (cacheDatas != null) {
			return cacheDatas; //
		}

		ReportUtil reportUtil = new ReportUtil(); //
		HashVO[] hvs = new HashVO[3000]; //�ܹ�������¼
		for (int i = 0; i < hvs.length; i++) { //�������м�¼!!
			hvs[i] = new HashVO(); //��������!!
			hvs[i].setAttributeValue("id", "" + i); //

			String str_city = getRanDomValue(i, str_citys); //
			String str_fh = str_city.substring(0, str_city.indexOf("@")); //����!
			String str_area = str_city.substring(str_city.indexOf("@") + 1, str_city.length()); //����!

			hvs[i].setAttributeValue("����", str_area); //
			hvs[i].setAttributeValue("����", str_fh); //

			hvs[i].setAttributeValue("ҵ������", getRanDomValue(i, str_busitypes)); //ҵ������!
			hvs[i].setAttributeValue("��������", getRanDomValue(i, str_findchannels)); //��������!
			hvs[i].setAttributeValue("���յȼ�", getRanDomValue(i, str_risklevels)); //���յȼ�
			hvs[i].setAttributeValue("����ִ��Ч��", getRanDomValue(i, str_ctrltypes)); //����ִ��Ч��
			hvs[i].setAttributeValue("ԭ�����", getRanDomValue(i, str_reason)); //�¼�ԭ��!
			hvs[i].setAttributeValue("����״̬", getRanDomValue(i, str_satus)); //����״̬!

			String str_month = "" + (100 + (random.nextInt(12) + 1)); //1,12
			str_month = str_month.substring(1, str_month.length()); //ȥ����һλ!!

			String str_date = "" + (100 + (random.nextInt(28) + 1)); //
			str_date = str_date.substring(1, str_date.length()); //ȥ����һλ!!
			hvs[i].setAttributeValue("����ʱ��", (i % 3 == 0 ? "2011" : "2012") + "-" + str_month + "-" + str_date); //ʱ��,Ϊ����ͬ��Ч��,���������1

			hvs[i].setAttributeValue("�����¼�", "1"); //
			hvs[i].setAttributeValue("��ʧ���", random.nextInt(100) + 10); //
			hvs[i].setAttributeValue("�����ʧ", random.nextInt(50) + 10); //
		}

		reportUtil.leftOuterJoin_YSMDFromDateTime(hvs, "����ʱ��(�·�)", "����ʱ��", "��"); //������·�!
		reportUtil.leftOuterJoin_YSMDFromDateTime(hvs, "����ʱ��(����)", "����ʱ��", "��"); //����ɼ���!

		cacheDatas = hvs; //
		return cacheDatas; //
	}

	private String getRanDomValue(int _index, String[] _arrays) {
		if (_index % 15 == 0) {
			return _arrays[0]; //
		} else if (_index % 20 == 0) {
			return _arrays[_arrays.length - 1]; //
		} else {
			return _arrays[random.nextInt(_arrays.length)]; //
		}
	}

	//��Ҫ����ͳ�Ƶ�ά��!!
	public String[] getGroupFieldNames() {
		return new String[] { "����", "����", "����ʱ��(����)", "����ʱ��(�·�)", "ҵ������", "��������", "���յȼ�", "����ִ��Ч��", "ԭ�����", "����״̬" };
	}

	public String[] getSumFiledNames() {
		return new String[] { "�����¼�", "��ʧ���", "�����ʧ" };
	}

	@Override
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid() {
		BeforeHandGroupTypeVO[] bhGroupVOs = new BeforeHandGroupTypeVO[8]; //

		bhGroupVOs[0] = new BeforeHandGroupTypeVO(); //
		bhGroupVOs[0].setName("1.����ʱ��(����)-����"); //
		bhGroupVOs[0].setRowHeaderGroupFields(new String[] {}); //
		bhGroupVOs[0].setColHeaderGroupFields(new String[] { "����", "����ʱ��(����)" }); //
		bhGroupVOs[0].setComputeGroupFields(new String[][] { { "�����¼�", "count", "�������={��ƽ����}*2,{��ƽ����}*1.5,{��ƽ����}" }, { "�����¼�-����", "CountChainIncrease", "�������=100" }, { "�����¼�-��������", "CountChainSeries", "�������=3,2,1" } }); // "�Ƿ���ٷֺ�=Y"�Ƿ񾯽�=Y;�������=250
		bhGroupVOs[0].setTotal(false); //
		bhGroupVOs[0].setSortByCpValue(true);  //����������
		
		HashMap filterMap = new HashMap();
		filterMap.put("����ʱ��(����)", new String[] { TBUtil.getTBUtil().getCurrDateSeason() }); //
		//bhGroupVOs[0].setFilterGroupValueMap(filterMap);  //
		bhGroupVOs[0].setType("GRID"); //

		bhGroupVOs[1] = new BeforeHandGroupTypeVO(); //
		bhGroupVOs[1].setName("2.����ʱ��(�·�)-����"); //
		bhGroupVOs[1].setRowHeaderGroupFields(new String[] { "����ʱ��(�·�)" }); //
		bhGroupVOs[1].setColHeaderGroupFields(new String[] { "����" }); //
		bhGroupVOs[1].setComputeGroupFields(new String[][] { { "�����¼�", "count" } }); //
		bhGroupVOs[1].setType("GRID"); //

		bhGroupVOs[2] = new BeforeHandGroupTypeVO(); //
		bhGroupVOs[2].setName("3.ҵ������-����"); //
		bhGroupVOs[2].setRowHeaderGroupFields(new String[] { "ҵ������" }); //
		bhGroupVOs[2].setColHeaderGroupFields(new String[] { "����" }); //
		bhGroupVOs[2].setComputeGroupFields(new String[][] { { "�����¼�", "count" } }); //
		bhGroupVOs[2].setSortByCpValue(true); //���������!!!
		bhGroupVOs[2].setType("GRID"); //

		//������ά�ȹ�ʽ�ģ�����
		bhGroupVOs[3] = new BeforeHandGroupTypeVO(); //
		bhGroupVOs[3].setName("4.���յȼ�-����ִ��Ч��"); //
		bhGroupVOs[3].setRowHeaderGroupFields(new String[] { "���յȼ�" }); //
		bhGroupVOs[3].setColHeaderGroupFields(new String[] { "����ִ��Ч��" }); //
		HashMap forMulamap = new HashMap(); //
		forMulamap.put("���յȼ�", new String[][] { { "��������֮��", "{�߷���}+{�������}", "�Ƿ���ٷֺ�=N" }, { "����͵ı���", "({�߷���}*100)/{��С����}", "�Ƿ���ٷֺ�=Y" } }); //
		bhGroupVOs[3].setRowHeaderFormulaGroupMap(forMulamap); //���յȼ�������Լ�������ʽ��!
		bhGroupVOs[3].setComputeGroupFields(new String[][] { { "�����¼�", "count" } }); //
		bhGroupVOs[3].setType("GRID"); //

		//�����м����й�ʽ�ģ�����
		bhGroupVOs[4] = new BeforeHandGroupTypeVO(); //
		bhGroupVOs[4].setName("5.���յȼ�-����ʱ��(����)"); //
		bhGroupVOs[4].setRowHeaderGroupFields(new String[] { "���յȼ�" }); //
		bhGroupVOs[4].setColHeaderGroupFields(new String[] { "����ʱ��(����)" }); //
		bhGroupVOs[4].setComputeGroupFields(new String[][] { { "��ʧ���", "sum" }, { "�����ʧ", "sum" }, { "���߱���", "FormulaCompute", "��ʽ=(({T1}-{T2})*100)/{T1};�Ƿ���ٷֺ�=Y" } }); //
		bhGroupVOs[4].setType("GRID"); //

		bhGroupVOs[5] = new BeforeHandGroupTypeVO(); //
		bhGroupVOs[5].setName("6.����-����-ҵ������"); //
		bhGroupVOs[5].setRowHeaderGroupFields(new String[] { "ҵ������" }); //
		bhGroupVOs[5].setColHeaderGroupFields(new String[] { "����", "����" }); //
		bhGroupVOs[5].setComputeGroupFields(new String[][] { { "�����¼�", "count" } }); //
		bhGroupVOs[5].setType("GRID"); //

		bhGroupVOs[6] = new BeforeHandGroupTypeVO(); //
		bhGroupVOs[6].setName("7.���յȼ�-����-����ִ��Ч��"); //
		bhGroupVOs[6].setRowHeaderGroupFields(new String[] { "���յȼ�" }); //
		bhGroupVOs[6].setColHeaderGroupFields(new String[] { "����", "����ִ��Ч��" }); //
		bhGroupVOs[6].setComputeGroupFields(new String[][] { { "�����¼�", "count" } }); //
		bhGroupVOs[6].setType("GRID"); //

		bhGroupVOs[7] = new BeforeHandGroupTypeVO(); //
		bhGroupVOs[7].setName("8.����ʱ��(����)-����ʱ��(�·�)-����-���յȼ�"); //
		bhGroupVOs[7].setRowHeaderGroupFields(new String[] { "����ʱ��(����)", "����ʱ��(�·�)" }); //
		bhGroupVOs[7].setColHeaderGroupFields(new String[] { "����", "���յȼ�" }); //
		bhGroupVOs[7].setComputeGroupFields(new String[][] { { "�����¼�", "count" } }); //
		bhGroupVOs[7].setType("GRID"); //

		return bhGroupVOs;
	}

	@Override
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Chart() {
		BeforeHandGroupTypeVO[] bhGroupVOs = new BeforeHandGroupTypeVO[5]; //
		bhGroupVOs[0] = new BeforeHandGroupTypeVO(); //
		bhGroupVOs[0].setName("����-����ʱ��(����)"); //
		bhGroupVOs[0].setRowHeaderGroupFields(new String[] { "����" }); //
		bhGroupVOs[0].setColHeaderGroupFields(new String[] { "����ʱ��(����)" }); //
		bhGroupVOs[0].setComputeGroupFields(new String[][] { { "�����¼�", "count" } }); //
		bhGroupVOs[0].setType("CHART"); //

		bhGroupVOs[1] = new BeforeHandGroupTypeVO(); //
		bhGroupVOs[1].setName("ҵ������-��������"); //
		bhGroupVOs[1].setRowHeaderGroupFields(new String[] { "ҵ������" }); //
		bhGroupVOs[1].setColHeaderGroupFields(new String[] { "��������" }); //
		bhGroupVOs[1].setComputeGroupFields(new String[][] { { "�����¼�", "count" } }); //
		bhGroupVOs[1].setType("CHART"); //

		bhGroupVOs[2] = new BeforeHandGroupTypeVO(); //
		bhGroupVOs[2].setName("���յȼ�-����ִ��Ч��"); //
		bhGroupVOs[2].setRowHeaderGroupFields(new String[] { "���յȼ�" }); //
		bhGroupVOs[2].setColHeaderGroupFields(new String[] { "����ִ��Ч��" }); //
		bhGroupVOs[2].setComputeGroupFields(new String[][] { { "�����¼�", "count" } }); //
		bhGroupVOs[2].setType("CHART"); //

		bhGroupVOs[3] = new BeforeHandGroupTypeVO(); //
		bhGroupVOs[3].setName("���յȼ�-����ʱ��(����)"); //
		bhGroupVOs[3].setRowHeaderGroupFields(new String[] { "���յȼ�" }); //
		bhGroupVOs[3].setColHeaderGroupFields(new String[] { "����ʱ��(����)" }); //
		bhGroupVOs[3].setComputeGroupFields(new String[][] { { "�����¼�", "count" } }); //
		bhGroupVOs[3].setType("CHART"); //

		bhGroupVOs[4] = new BeforeHandGroupTypeVO(); //
		bhGroupVOs[4].setName("ҵ������-����ʱ��(�·�)"); //
		bhGroupVOs[4].setRowHeaderGroupFields(new String[] { "ҵ������" }); //
		bhGroupVOs[4].setColHeaderGroupFields(new String[] { "����ʱ��(�·�)" }); //
		bhGroupVOs[4].setComputeGroupFields(new String[][] { { "�����¼�", "count" } }); //
		bhGroupVOs[4].setType("CHART"); //

		return bhGroupVOs;
	}

	@Override
	public HashMap getDrilGroupBind() {
		HashMap map = new HashMap(); //
		map.put("����", "����"); //
		map.put("����ʱ��(����)", "����ʱ��(�·�)"); //
		return map;
	}

	@Override
	/**
	 * ��ȡ��ϸʱ��ģ�����!!
	 */
	public String getDrillTempletCode() throws Exception {
		return null; //
	}

	/**
	 * ��������!!!
	 */
	@Override
	public HashMap getGroupFieldOrderConfig() {
		HashMap map = new HashMap(); //
		map.put("���յȼ�", new String[] { "�������", "�߷���", "�еȷ���", "�ͷ���", "��С����" }); //
		map.put("ҵ������", new String[] { "����ҵ��", "�м�ҵ��", "����ҵ��" }); //
		map.put("����", new String[] { "������", "������", "������", "������", "������" }); //
		map.put("ԭ�����", str_reason); //
		return map;
	}

	@Override
	public HashMap getZeroReportConfMap() {
		HashMap map = new HashMap(); //
		map.put("���յȼ�", new String[] { "�������", "�߷���", "�еȷ���", "�ͷ���", "��С����", "��������" }); //
		return map; //
	}

	@Override
	public HashMap getDateGroupDefineMap() {
		HashMap map = new HashMap(); //
		map.put("����ʱ��(����)", "����"); //
		map.put("����ʱ��(�·�)", "�¶�"); //
		return map; //
	}

}
