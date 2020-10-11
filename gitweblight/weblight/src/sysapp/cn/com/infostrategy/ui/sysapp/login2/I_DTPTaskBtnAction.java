package cn.com.infostrategy.ui.sysapp.login2;

import java.lang.reflect.Method;

import javax.swing.JComponent;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.ui.sysapp.login.DeskTopPanel;

/** 
 * Copyright Pushine
 * @ClassName: cn.com.infostrategy.ui.sysapp.login2.I_DTPTaskBtnAction 
 * @Description: 点击首页任务中心按钮后调用的实现类
 * @author haoming
 * @date Mar 14, 2013 9:48:19 AM
 *  
*/
public class I_DTPTaskBtnAction implements I_DeskTopPanelBtnStyleActionIfc {

	public JComponent afterClickComponent(HashVO _configvo) {
		JPanel jp = null;
		try {
			Method method = DeskTopPanel.class.getDeclaredMethod("getTaskAndMsgCenterPanel");
			method.setAccessible(true);
			jp = (JPanel) method.invoke(DeskTopPanel.getDeskTopPanel(), new Object[] {});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jp;
	}

}
