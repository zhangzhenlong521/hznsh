package cn.com.infostrategy.bs.sysapp.sysboard;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.sysapp.login.DeskTopNewsDataBuilderIFC;
import cn.com.infostrategy.to.common.HashVO;

/**
 * 系统公告的首页数据构造器!!!以后在首页配置时应该将类型可以作为参数传进来!!!
 * @author xch
 *
 */
public class SysboardDataBuilder implements DeskTopNewsDataBuilderIFC {

	/**
	 * 取得新闻数据!!
	 */
	public HashVO[] getNewData(String _userCode) throws Exception {
		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, "select * from pub_sysboard where msgtype='系统消息' order by seq,createtime"); //
		for (int i = 0; i < hvs.length; i++) {
			hvs[i].setToStringFieldName("title"); //标题!!!
		}
		return hvs;
	}

}
