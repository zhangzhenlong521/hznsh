package cn.com.infostrategy.bs.sysapp.install.quickInstall;

import java.io.Serializable;

/** 
 * Copyright Pushine
 * @ClassName: cn.com.infostrategy.bs.sysapp.install.quickInstall.QuickInstallModuleCustomIFC 
 * @Description: ���ٰ�װ�������Զ���ʵ�ֽӿڡ�
 * @author haoming
 * @date May 9, 2013 2:08:26 PM
 *  
*/ 
public interface QuickInstallModuleCustomIFC extends Serializable {
	//�Զ�������Ƿ���԰�װ����������
	public boolean checkCanInstallOrUpdate() throws Exception;
	
	//����ddl��dml�������Զ�ִ�к��Զ����߼���
	public String afterAutoInstallOrUpdateDo() throws Exception;
	
}
