package cn.com.infostrategy.to.common;

import java.util.HashMap;

/**
 * 这个类有两个目的!
 * 之所以搞这个类是为了性能监控时用,因为我有时想监控有些缓存有没有被释放或消耗了多少内存!
 * 如果使用HashMap,会造成如果根据类去找,有几个实例,没法定位,所以需要一个特殊的类情况,但实际上还是一个HashMap!
 * 像Vector,ArrayList,HashSet以后都需要这样搞一个!!!
 * 所以一定要明白搞这个类的初衷,不要觉得莫名其妙的搞个类啥都不干，然后奇怪!!!
 * @author xch
 *
 */
public class WLTHashMap extends HashMap {

	private static final long serialVersionUID = -4324805170543160150L;
	public String descInfo = "";

	public Object put(Object key, Object value, String _desc) {
		descInfo = descInfo + "[" + key + "]=[" + _desc + "]\r\n"; //添加说明
		return super.put(key, value);
	}

	public String getDescInfo() {
		return descInfo; //
	}

}
