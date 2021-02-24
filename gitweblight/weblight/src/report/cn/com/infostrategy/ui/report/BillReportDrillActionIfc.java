package cn.com.infostrategy.ui.report;

import java.awt.Container;
import java.util.HashMap;

/**
 * 万维报表实现钻去真实数据的接口。客户端实现该接口中的方法。
 * 当写服务器端报表数据构造器时 可以重写MultiLevelReportDataBuilderAdapter中的getDrillActionClassPath()方法，返回一个本类的实现类,可以在写面板的时候实现该接口
 * 要求在服务器端写报表数据Hashvo数组中 必须有id这个字段。
 * 实现该接口的类的路径需要在MultiLevelReportDataBuilderAdapter中传回客户端。为什么这么搞？关于报表的类很多入口类BillReportPanel,里面会多层调用才能到ReportCellPanel面板上，
 * 整体都加方法或者构造方法，不方便。而且可以根据服务器端有无返回值决定是否显示右键。
 * @author hm
 * 
 * zzl[2018-02-01]
 * query
 * 加入查询map  方便以后查询
 *
 */
public interface BillReportDrillActionIfc {
	/*
	 * ids是cell格中的id串。
	 */
	public void drillAction(String ids, Object _itemVO, Container _parent,HashMap<String, String> query);
	
	
}
