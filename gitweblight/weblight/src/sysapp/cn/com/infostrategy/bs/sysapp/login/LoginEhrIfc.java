package cn.com.infostrategy.bs.sysapp.login;

/**
 * ���ڴӵ�����ϵͳȥУ���û���������
 * һ����˵���Ǵ��û���Ehrϵͳ��У��,���������ɴ�ͽ�LoginEhrIfc,����Ҳ�ɴ��checkInEhr(),��ʵ����һ���Ƿ�Ҫ��hrϵͳ��У���,��������κ��߼���У��!
 * @author xch
 *
 */
public interface LoginEhrIfc {

	/**
	 * �����û���������ȥУ��,���У�鲻�������쳣!!
	 * @param _usercode
	 * @param _pwd
	 * @throws Exception
	 */
	public void checkInEhr(String _usercode, String _pwd) throws Exception;
}
