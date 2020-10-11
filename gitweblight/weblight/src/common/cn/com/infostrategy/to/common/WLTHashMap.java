package cn.com.infostrategy.to.common;

import java.util.HashMap;

/**
 * �����������Ŀ��!
 * ֮���Ը��������Ϊ�����ܼ��ʱ��,��Ϊ����ʱ������Щ������û�б��ͷŻ������˶����ڴ�!
 * ���ʹ��HashMap,��������������ȥ��,�м���ʵ��,û����λ,������Ҫһ������������,��ʵ���ϻ���һ��HashMap!
 * ��Vector,ArrayList,HashSet�Ժ���Ҫ������һ��!!!
 * ����һ��Ҫ���׸������ĳ���,��Ҫ����Ī������ĸ����ɶ�����ɣ�Ȼ�����!!!
 * @author xch
 *
 */
public class WLTHashMap extends HashMap {

	private static final long serialVersionUID = -4324805170543160150L;
	public String descInfo = "";

	public Object put(Object key, Object value, String _desc) {
		descInfo = descInfo + "[" + key + "]=[" + _desc + "]\r\n"; //���˵��
		return super.put(key, value);
	}

	public String getDescInfo() {
		return descInfo; //
	}

}
