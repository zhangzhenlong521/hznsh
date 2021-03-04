package cn.com.infostrategy.ui.sysapp.login.index;

import javax.swing.JPanel;

import cn.com.infostrategy.to.sysapp.login.DeskTopNewGroupVO;
import cn.com.infostrategy.ui.sysapp.login.DeskTopPanel;
/**
 * 首页信息块自定义实现。
 * 为了方便扩展，写抽象类
 * @author haoming
 * create by 2016-5-23
 */
public abstract class IndexPanelCustCommIfc {
	public abstract void init(JPanel _container,DeskTopPanel _deskTopPanel,DeskTopNewGroupVO _groupVO);
}
