/**************************************************************************
 * $RCSfile: VectorMap.java,v $  $Revision: 1.6 $  $Date: 2012/09/14 09:17:30 $
 **************************************************************************/
package cn.com.infostrategy.to.common;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * ������Ĺ�ϣ��,�����ʱ�Ǹ��ݲ�����Ⱥ�˳�������!������ͳ��HashMap���ʱ�����˳��!!
 * ��Ϊ����Vector��˳�����ϣ��ļ�ֵ,���Խ�VectorMap
 * @author xch
 *
 */
public class VectorMap extends LinkedHashMap implements Serializable {

	private static final long serialVersionUID = 8406115438094288557L;

	//ȡ�õڼ���λ���ϵ�ֵ!!��ǰֻ�ܸ���key��ȡ!!������Ϊ�������,����Ҳ�ܸ���λ����ȡ!
	public Object get(int _index) {
		Object str_key = getKeys()[_index]; //ȡ��Key��!!
		return get(str_key); //
	}

	//���õڼ�λ�ϵ�ֵ,��ǰֻ�ܸ���key����,�÷�������put,����Ϊ�����е�put������object
	public void set(int _index, Object obj) {
		Object str_key = getKeys()[_index]; //ȡ��Key��!!
		put(str_key, obj); //
	}

	//ȡ�����е�key,����һά����
	public Object[] getKeys() {
		return keySet().toArray(); //
	}

	public String[] getKeysAsString() {
		return (String[]) keySet().toArray(new String[0]); //
	}

	public Object[] getValues() {
		return this.values().toArray();
	}

	public String[] getValuesAsString() {
		return (String[]) this.values().toArray(new String[0]);
	}

	public Object[][] getAllData() {
		Object[] keys = getKeys();
		Object[][] objs = new Object[keys.length][2];
		for (int i = 0; i < keys.length; i++) {
			objs[i][0] = keys[i];
			objs[i][1] = get(keys[i]);
		}
		return objs;
	}

	public String[][] getAllDataAsString() {
		Object[] keys = getKeys();
		String[][] objs = new String[keys.length][2];
		for (int i = 0; i < keys.length; i++) {
			objs[i][0] = (String) keys[i];
			objs[i][1] = (String) get(keys[i]);
		}
		return objs;
	}

}

/**************************************************************************
 * $RCSfile: VectorMap.java,v $  $Revision: 1.6 $  $Date: 2012/09/14 09:17:30 $
 *
 * $Log: VectorMap.java,v $
 * Revision 1.6  2012/09/14 09:17:30  xch123
 * *** empty log message ***
 *
 * Revision 1.1  2012/08/28 09:40:49  Administrator
 * *** empty log message ***
 *
 * Revision 1.5  2011/10/10 06:31:36  wanggang
 * restore
 *
 * Revision 1.3  2010/08/24 10:09:30  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2010/08/23 12:23:32  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/17 10:23:00  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:31:49  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:32:49  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.5  2010/02/08 11:01:56  sunfujun
 * *** empty log message ***
 *
 * Revision 1.3  2009/11/12 10:31:12  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2009/11/12 06:41:37  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:42  xuchanghua
 *
**************************************************************************/
