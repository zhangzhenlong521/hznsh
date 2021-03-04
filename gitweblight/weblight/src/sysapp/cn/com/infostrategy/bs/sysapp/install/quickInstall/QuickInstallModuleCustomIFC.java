package cn.com.infostrategy.bs.sysapp.install.quickInstall;

import java.io.Serializable;

/** 
 * Copyright Pushine
 * @ClassName: cn.com.infostrategy.bs.sysapp.install.quickInstall.QuickInstallModuleCustomIFC 
 * @Description: 快速安装和升级自定义实现接口。
 * @author haoming
 * @date May 9, 2013 2:08:26 PM
 *  
*/ 
public interface QuickInstallModuleCustomIFC extends Serializable {
	//自定义检验是否可以安装或者升级。
	public boolean checkCanInstallOrUpdate() throws Exception;
	
	//所有ddl、dml及其他自动执行后自定义逻辑。
	public String afterAutoInstallOrUpdateDo() throws Exception;
	
}
