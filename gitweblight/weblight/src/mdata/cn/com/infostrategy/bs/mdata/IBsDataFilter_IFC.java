package cn.com.infostrategy.bs.mdata;

import cn.com.infostrategy.to.mdata.BillVO;

/**
 * 服务器端查询时数据过滤公式!!!很关键
 * @author xch
 *
 */
public interface IBsDataFilter_IFC  {

	/**
	 * 过滤一行数据
	 * @return
	 */
	public boolean filterBillVO(BillVO _billVO) throws Exception; //

}
