package cn.com.infostrategy.to.report;

import java.io.Serializable;

import cn.com.infostrategy.to.mdata.BillCellVO;

/**
 * 使用Excel控件展示的报表中的存储的数据对象!!关键!!
 * 曾经想直接使用HashVO[]或BillCellVO作为报表对象的数据类，但后来发现他们不能满足多行头与多列头合并的要求。所以感觉有必要重新构造一个新的专门的报表数据类！
 * 该对象主要由标题栏，行头,列头,内容表格四部分组成。其中行头与列头可以是多层，内容表格可以任意横向,纵向相同数据合并。
 * 
 * @author xch
 *
 */
public class ReportCellVO implements Serializable {

	private String title = null; //标题名,永远在第一行显示.

	private String[][] rowHeaders = null; //行头,背景颜色自动设为浅蓝色
	private String[][] colHeaders = null; //列头,背景颜色自动设为浅蓝色

	private BillCellVO contentCellVO = null; //数据内容网络
}
