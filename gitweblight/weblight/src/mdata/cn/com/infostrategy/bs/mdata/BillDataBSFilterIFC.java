package cn.com.infostrategy.bs.mdata;

import cn.com.infostrategy.to.common.HashVO;

/**
 * BS������Ȩ�޹�����!
 * ������ͨ��SQL���������ݿ���ʵ��,�������м������ɵ�!!!
 * ����SQL���˵���������:һ������ʵ�ָ��ӵĹ��˼���;����ʹ��SQLʵ����ʱ�ܱ�Ť,��Ҫ��������!���׿���!��ʹ��Java������õĶ�!,����:����Ǿ������ܿ�,��Ҫ���������SQL����,
 * ֮�������ɳ�����,�����ǽӿ�,����Ϊ�ڸ�����Ҫ�ṩ�����ķ����Է�������Ȩ��֮��,����ֱ���жϵ�¼��Ա�Ƿ����ĳ�ֽ�ɫ,ֱ���жϵ�¼��Ա�Ƿ�����ʲô���͵Ļ�����,ֱ���ж������л��Ƿ���!!
 * @author Administrator
 *
 */
public interface BillDataBSFilterIFC {

	/**
	 * ��һ�ι���,Ϊ�����������ֱ�Ӷ�HashVO���д���!!
	 * ������ɳ��󷽷�!!
	 * @param _hvs
	 * @throws Exception
	 */
	public void filterBillData(HashVO[] _hvs) throws Exception;

}
