package cn.com.infostrategy.bs.sysapp.runtime;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;

/**
 * �Լ������ClassLoad,���Ǵ�ClassPath��ȡ��,���Ǵ����ݿ���ȡ��!!!
 * @author Administrator
 *
 */
public class WLTRuntimeClassLoader extends ClassLoader {

	public WLTRuntimeClassLoader(ClassLoader _clsLoader) {
		super(_clsLoader); //
	}

	/**
	 * ȡ��!!!
	 */
	public Class myloadClass(String _actionName) {
		try {
			//Ӧ���Ǵ����ݿ�ȡ!!
			String str_sql = "select classcode from pub_rtaction_class where actionname='" + _actionName + "' order by seq"; //SQL���!!һ��Ҫ����!!!
			HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, str_sql); //��ѯ����!!
			StringBuffer sb_clscode = new StringBuffer(); //
			for (int i = 0; i < hvs.length; i++) {
				sb_clscode.append(hvs[i].getStringValue("classcode")); //����
			}
			byte[] bytes = new TBUtil().convertHexStringToBytes(sb_clscode.toString()); //��16���Ƶ��ַ���ת����ֱ��������
			Class clses = this.defineClass(null, bytes, 0, bytes.length); //�������������Class��!!!!
			return clses; //���������!!!
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}

}
