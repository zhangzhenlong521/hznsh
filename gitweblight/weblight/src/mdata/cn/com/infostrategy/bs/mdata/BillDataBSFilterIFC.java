package cn.com.infostrategy.bs.mdata;

import cn.com.infostrategy.to.common.HashVO;

/**
 * BS端数据权限过滤器!
 * 即不是通过SQL过滤在数据库中实现,而是在中间件中完成的!!!
 * 它与SQL过滤的区别在于:一来可以实现复杂的过滤计算;二来使用SQL实现有时很别扭,即要倒过来设!不易开发!而使用Java计算则好的多!,比如:如果是绝密则不能看,就要倒过来设成SQL条件,
 * 之所以做成抽象类,而不是接口,是因为在该类中要提供大量的方法以方便数据权限之用,比如直接判断登录人员是否肯有某种角色,直接判断登录人员是否属于什么类型的机构中,直接判断是总行还是分行!!
 * @author Administrator
 *
 */
public interface BillDataBSFilterIFC {

	/**
	 * 第一次过滤,为了提高性能是直接对HashVO进行处理!!
	 * 所以设成抽象方法!!
	 * @param _hvs
	 * @throws Exception
	 */
	public void filterBillData(HashVO[] _hvs) throws Exception;

}
