package cn.com.infostrategy.bs.sysapp;

/**
 * 表安装,以前的数据字典是存在表中而不是代码中,结果并不能实现随代码升级而升级!!方向就错了!!
 * 我希望的是随着我代码提交,新的表定义就提交了,然后可以将原来实际表与新的定义进行比较,从而提醒是否需要重新修改表结构..
 * 也可以直接拿这个类重新初始化安装系统!!!做安装盘时用!!
 * @author xch
 *
 */
public class WLTTableInstall {

	/**
	 * 根据一个XML加载表定义... 比如cn.com.infostrategy.bs.sysapp.SysTableInstall.xml
	 * @param _defineXMLFileName 定义表名的XML
	 */
	public WLTTableInstall(String _defineXMLFileName) {
		//读取一个XML文件...
		initialize(); //
	}

	private void initialize() {

	}

	/**
	 * 取得表名...
	 * @return
	 */
	public String getTableName() {
		return null;
	}

	/**
	 * 取得所有列名...
	 * @return
	 */
	public String[][] getColumns() {
		return null;
	}

	/**
	 * 取得创建该表的Create SQL
	 * @return
	 */
	public String getCreateSQL() {
		return null;
	}

	/**
	 * 拿定义与平台的实际表进行比较,生成比较的SQL,这个非常关键..
	 * @return
	 */
	public String getCompareSQL() {
		return null;
	}

}
