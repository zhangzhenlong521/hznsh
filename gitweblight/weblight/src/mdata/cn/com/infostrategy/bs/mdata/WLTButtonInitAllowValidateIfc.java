package cn.com.infostrategy.bs.mdata;

import java.util.HashMap;

import cn.com.infostrategy.to.mdata.ButtonDefineVO;

/**
 * ��ťУ���������
 * @author Administrator
 *
 */
public interface WLTButtonInitAllowValidateIfc {

	/**
	 * ��ЧУ��ĳɹ����!!
	 * һ����ť�Ƿ���Чһ����������:
	 * һ�������ĳһ����¼�������!!!��һ������ǲ����¼���,����ֱ�����ɫ����Ա���!!!
	 * �����¼���ʱ�������ڳ�ʼ��ʱ���У���߼�,Ҳ���ڵ��ʱ���У���߼�!!һ���Ҫ���ڳ�ʼ��ʱУ���߼�!!!
	 * ���¼���ʱ,��ʼ��ʱ������Ȩ�޹���!!
	 * @param  _btndfo ��ť�����˵����!!!!
	 * @param _loginUserId ��¼��Աid,�Զ�����ȥ
	 * @param _initSharePoolMap Ϊ��������ܳ�ʼ��ʱ��ҹ��������õ�!!
	 * @return  �Ƿ���Ч,�����true����ť��Ч,�����false,��ť��Ч!!
	 */
	public boolean allowValieDate(ButtonDefineVO _btndfo, String _loginUserId, HashMap _initSharePoolMap); //

	/**
	 * У��ʧ��ʱ��ԭ��˵��,��һ����Ҫʵ��,���ʵ����,���ܸ���Ч�Ĵ�ҳ����ͨ������Ҽ��鿴��ԭ��!!!
	 * @return
	 */
	public String getAllowInfo(); //
}
