package cn.com.infostrategy.bs.sysapp.login;

import cn.com.infostrategy.to.common.HashVO;

/**
 * ��ҳ�����������ɶ���..���ڷ�������ʵ��
 * ������ҳ����һ��������,ÿ��������һ������..
 * @author xch
 *
 */
public interface DeskTopNewsDataBuilderIFC {

	/**
	 * ��������,���ݵ�¼��Ա,��ͬ��ģ��ȥʵ�ֲ�ͬ��ģ��..
	 * @param _loginUserCode
	 * @return
	 * @throws Exception
	 */
	public HashVO[] getNewData(String _loginUserCode) throws Exception;

}

