package cn.com.infostrategy.bs.sysapp.database.compare;

/**
 * 数据字典工厂的接口定义,
 * 实际这个类以后一般来说会有3个类!即平台,产品,项目,其中:
 * 平台的由我负责维护,产品的由赵负责,项目由项目经理负责维护
 * 陈负责定期检查产品与项目的实际表是否品配于定义的!
 */

public interface DictFactoryIFC {
	public WLTTableDf[] getAllPlatformTablesDefine(); //
}
