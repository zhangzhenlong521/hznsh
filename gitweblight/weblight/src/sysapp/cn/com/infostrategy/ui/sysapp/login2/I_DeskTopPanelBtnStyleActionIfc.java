package cn.com.infostrategy.ui.sysapp.login2;

import java.io.Serializable;

import javax.swing.JComponent;

import cn.com.infostrategy.to.common.HashVO;

/** 
 * Copyright Pushine
 * @ClassName: cn.com.infostrategy.ui.sysapp.login2.I_DeskTopPanelBtnStyleActionIfc 
 * @Description: ��ҳ��ť������¼�����õ��Զ�����Ľӿڡ�
 * @author haoming
 * @date Mar 14, 2013 9:40:04 AM
 *  
*/ 
public interface I_DeskTopPanelBtnStyleActionIfc extends Serializable {
	public JComponent afterClickComponent(HashVO _configVO);
}
