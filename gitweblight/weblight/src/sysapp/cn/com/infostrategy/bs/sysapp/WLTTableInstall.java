package cn.com.infostrategy.bs.sysapp;

/**
 * ��װ,��ǰ�������ֵ��Ǵ��ڱ��ж����Ǵ�����,���������ʵ�����������������!!����ʹ���!!
 * ��ϣ�����������Ҵ����ύ,�µı�����ύ��,Ȼ����Խ�ԭ��ʵ�ʱ����µĶ�����бȽ�,�Ӷ������Ƿ���Ҫ�����޸ı�ṹ..
 * Ҳ����ֱ������������³�ʼ����װϵͳ!!!����װ��ʱ��!!
 * @author xch
 *
 */
public class WLTTableInstall {

	/**
	 * ����һ��XML���ر���... ����cn.com.infostrategy.bs.sysapp.SysTableInstall.xml
	 * @param _defineXMLFileName ���������XML
	 */
	public WLTTableInstall(String _defineXMLFileName) {
		//��ȡһ��XML�ļ�...
		initialize(); //
	}

	private void initialize() {

	}

	/**
	 * ȡ�ñ���...
	 * @return
	 */
	public String getTableName() {
		return null;
	}

	/**
	 * ȡ����������...
	 * @return
	 */
	public String[][] getColumns() {
		return null;
	}

	/**
	 * ȡ�ô����ñ��Create SQL
	 * @return
	 */
	public String getCreateSQL() {
		return null;
	}

	/**
	 * �ö�����ƽ̨��ʵ�ʱ���бȽ�,���ɱȽϵ�SQL,����ǳ��ؼ�..
	 * @return
	 */
	public String getCompareSQL() {
		return null;
	}

}
