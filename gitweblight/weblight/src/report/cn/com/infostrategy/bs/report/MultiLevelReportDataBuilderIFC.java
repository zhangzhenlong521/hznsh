package cn.com.infostrategy.bs.report;

import java.util.HashMap;

import cn.com.infostrategy.to.common.HashVO;

/**
 * ��ά��������������,�ǳ�����!!�Ժ��������������!!
 * �ýӿ��ڷ�������,����������,�ֱ�������ά�������ͳ�Ƶ��ֶ�,Ȼ����һ�������������ݵķ���
 * �������ݵķ������ص���һ��HashVO[],����������ϸ����,�������һ��HashMap,���HashMap�Ǵ�ǰ̨�����,һ����ͨ��һ����ѯ����ʵ�ֵ�!!!
 * �����ǽ�,���е�ͳ�Ʊ���,ǰ̨��������һ������!!!Ȼ��ͨ��һ��Զ�̷���,��ȡ��ά�ȶ���,���ɶԻ���,���û�ѡ���ĸ�ά��,Ȼ���������ѯ����,
 * �����ѯ������ƽ̨�ٵ��ø����ʵ�����ݷ���,��������,Ȼ���ٵ��ù�����,����ѡ���ά�Ƚ��з���ͳ��,���ɽ������,�ý��Ӧ��ֻ��ά������ͳ����!!
 * �ؼ��Ǹýӿ�ʵ����ֻ��������������ϸ��,����Ҫ�����κλ��ܴ���..���ԶԿ�����ԱҪ��ܼ�.
 * ǰ̨ͨ������һ��ģ������һ���ýӿ�ʵ���������,�Ϳ�����,Ȼ��ʣ�µ�ֻ�Ǵ���ýӿ�ʵ������������ݷ���!
 * ��������ʱ����ҪŪһ��LeftOuterJoin
 * @author xch
 *
 */
public interface MultiLevelReportDataBuilderIFC {

	/**
	 * ���챨������...
	 * _queryConsMap�������������key,����$SQL����$SQL_1��,�ֱ���ǰ̨ƴ�Ӻõ�SQL,����SQL_1�ǽ�Ȩ�޲���Ҳ�������! ��Ҫע���ѯ�ֶ����Ƿ�Ҫ�����滻?������һ����ѯģ����ܱ�����ط����ã�����ͬ�ط����ֶ�����һ������
	 * ��������,ÿ���ں�̨ƴ��SQL̫������,ֱ�ӽ�ǰ̨ƴ�Ӻ��˵�SQL������������д���������̨�ù���ֱ�Ӽ��ڡ�select * from ���� where 1=1��+��$SQL�� ���ж�ˬ??��Ҫע���ֶ����Ƿ�ƥ��,�����ƥ��,��Ҫ�����ַ����滻!!���Լ��������xch/2012-06-20��
	 * @param _queryCondition
	 * @return
	 * @throws Exception
	 */
	public HashVO[] buildReportData(HashMap _queryCondition) throws Exception; //

	/**
	 * ������Բ������ͳ�Ƶ���
	 * @return
	 */
	public String[] getGroupFieldNames(); //

	/**
	 * ������Բ�����ܵ���
	 * @return
	 */
	public String[] getSumFiledNames(); //

	/**
	 * ���������������
	 * @return
	 * @throws Exception
	 */
	public HashMap getGroupFieldOrderConfig() throws Exception;

}
