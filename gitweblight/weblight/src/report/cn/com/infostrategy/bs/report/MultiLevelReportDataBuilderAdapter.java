package cn.com.infostrategy.bs.report;

import java.util.HashMap;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;

/**
 * ���������ݵ�������,
 * ���Զ���demo����
 * @author xch
 *
 */
public abstract class MultiLevelReportDataBuilderAdapter implements MultiLevelReportDataBuilderIFC {

	/**
	 * ����Ҫ�ķ���,��������ϸ���ݣ���
	 * _queryConsMap�������������key,����$SQL����$SQL_1��,�ֱ���ǰ̨ƴ�Ӻõ�SQL,����SQL_1�ǽ�Ȩ�޲���Ҳ�������! ��Ҫע���ѯ�ֶ����Ƿ�Ҫ�����滻?������һ����ѯģ����ܱ�����ط����ã�����ͬ�ط����ֶ�����һ������
	 * ��������,ÿ���ں�̨ƴ��SQL̫������,ֱ�ӽ�ǰ̨ƴ�Ӻ��˵�SQL������������д���������̨�ù���ֱ�Ӽ��ڡ�select * from ���� where 1=1��+��$SQL�� ���ж�ˬ??��Ҫע���ֶ����Ƿ�ƥ��,�����ƥ��,��Ҫ�����ַ����滻!!���Լ��������xch/2012-06-20��
	 */
	public abstract HashVO[] buildReportData(HashMap _queryConsMap) throws Exception; //

	/**
	 * ����Demo����!!!
	 * @return
	 * @throws Exception
	 */
	public HashVO[] buildDemoData() throws Exception {
		return null;
	} //

	/**
	 * ���в���������!
	 */
	public abstract String[] getGroupFieldNames();

	/**
	 * ���в���������
	 */
	public abstract String[] getSumFiledNames();

	//�����Ԥ�õ�ͳ������!!
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid() {
		return null;
	}

	//�����Ԥ�õ�ͳ������!!
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Chart() {
		return null;
	}

	//�����Ԥ���״�ͼ
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Splider() {
		return null;
	}

	/**
	 * ��ȡ�������� 
	 * ���巽ʽ  key=��ͷ���ƣ�value=Ϊ����˳�������
	 * ���磺 key = �ļ�״̬ value=[�ļ���д��][����������][�ѷ���]
	 * @return
	 */
	public HashMap getGroupFieldOrderConfig() {
		return null;
	}

	/**
	 * ��㱨���ƶ���,���д���ά��,������ݿ���ֻ��������¼��,���Ǿ���ʾ����,���ǽ�����û�з�����Ҳ��ʾ��������
	 * ���񡰷ǵ䡱ʱ����㱨����һ��,���û�з���ҲҪ���棡����ʾΪ�㣡������֤����ġ����ӡ���Զ�ǡ��������ģ���
	 * @return
	 */
	public HashMap getZeroReportConfMap() {
		return null;
	}

	/**
	 * ָ����Щά���ǡ�����/ʱ�䡿ά��,ΪʲôҪ�ر�˵����һ����?
	 * ��Ϊ��ͬ��,���ȵȼ���,��ר�����ʱ������!���Ҽ��㷽�����ر�!��ʱ��ά����һ�֡��ǳ��ر𡱵�ά��!!ֻ��ָ������Щά����ʱ��ά��,Ȼ����ܽ������ּ���!
	 * ʱ��ά�ȵ��ֶε��Զ��������Ҳ�������ֶβ�һ��,�����ֶζ���ָ��һ����̬���飡 ��ʱ��ά���ǿ����ҳ�����Сֵ-���ֵ/���߽��졿Ȼ���м�Ķϵ����Զ���ȫ!(������Բ���)
	 * ʱ���ַ�����:��,��,��
	 * @return
	 */
	public HashMap getDateGroupDefineMap() {
		return null;
	}

	/**
	 * ��ȡά���໥�󶨵Ķ���,��������������Ҫ,�ȳ���ά���Ǽ���,Ȼ�󼾶Ⱥ������ȡά�ȱ�Ȼ���·�,
	 * �ٱ���:��������У�������֧�ж��������߼�!!! ����Щά��֮����ȡ���Ⱥ�˳����Ԥ�Ȱ󶨺õģ���
	 */
	public HashMap getDrilGroupBind() {
		return null;
	}

	/*
	 * �Զ�����ȥ���ݵ�ʵ����,���ȼ�1
	 * ��Ҫʵ�֣�BillReportDrillActionIfc
	 */
	public String getDrillActionClassPath() throws Exception {
		return null;
	}

	/*
	 * �Զ���Ĭ����ȡ��ʵ���ݵ����б��ģ�����.���ȼ�2
	 */
	public String getDrillTempletCode() throws Exception {
		return null;
	}

	/**
	 * ȡ�û���Demo����
	 * @param _type
	 * @return
	 */
	public String[] getDemoDataByCorp(int _type) {
		if (_type == 1) {
			return new String[] { "����", "�Ϻ�����", "��������", "���շ���", "�㽭����", "�ӱ�����", "ɽ������" }; //
		} else if (_type == 2) {
			return new String[] { "�ֶ�֧��", "����֧��", "��¥֧��", "��Ž�֧��" }; //
		} else {
			return new String[] { "��������", "�Ϻ�����" };
		}
	}

	/**
	 * ȡ�û���Demo����
	 * @param _type
	 * @return
	 */
	public String[] getDemoDataBySeasonMonth(int _type) {
		if (_type == 1) {
			return new String[] { "2012��1����", "2012��2����", "2012��3����", "2012��4����" }; //
		} else if (_type == 2) {
			return new String[] { "2012��01��", "2012��02��", "2012��03��", "2012��04��", "2012��05��", "2012��06��", "2012��07��", "2012��08��", "2012��09��", "2012��10��", "2012��11��", "2012��12��" }; //
		} else {
			return new String[] { "2012��01��" }; //
		}
	}
}
