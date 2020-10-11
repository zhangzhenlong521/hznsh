package cn.com.infostrategy.ui.sysapp.login;

import java.awt.BorderLayout;
import java.io.Serializable;

import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.ConsoleMsgVO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.sysapp.login.LoginOneOffVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientConsolePrintStream;
import cn.com.infostrategy.ui.common.IAppletLoader;

/**
 * 加载任意一个面板的标准AppletLoader! 
 * 即我们现在是默认加载登录界面!!!但实际上在与其他系统做集成时(比如中铁建),可能直接打开某一个面板!!或者是某一个菜单功能点!!
 * 它在LoadWltPanelServlet中调用!!! 
 * @author xch
 *
 */
public class LoadWltPanelLoader implements IAppletLoader, Serializable {

	private static final long serialVersionUID = 1L;
	private LoginUtil loginUtil = new LoginUtil(); //

	//构造方法!!
	public LoadWltPanelLoader() {
		ClientConsolePrintStream mySystemOut = new ClientConsolePrintStream(ConsoleMsgVO.SYSTEM_OUT, System.out); //重定向控制台
		System.setOut(mySystemOut);
		ClientConsolePrintStream mySystemErr = new ClientConsolePrintStream(ConsoleMsgVO.SYSTEM_ERROR, System.err); //错误的.
		System.setErr(mySystemErr); //
	}

	public void loadApplet(JApplet _applet, JPanel _mainPanel) throws Exception {
		try {
			String str_skiptype = _applet.getParameter("REQ_skiptype"); //skiptype=menu&menuid
			String str_menuid = _applet.getParameter("REQ_menuid");
			String str_clsname = _applet.getParameter("REQ_clsname"); //直接是类名
			//_panel.add(new JLabel("skiptype=[" + str_skiptype + "],menuid=[" + str_menuid + "]")); //

			String str_usercode = _applet.getParameter("REQ_usercode"); //看是否有用户编码,如果有,则还要登录一下!!!
			if (str_usercode != null) { //如果有用户编码!!!则登录一下,给客户端环境变量赋值! 因为可能菜单功能点中需要用到这些环境变量!!!
				LoginAppletLoader loader = new LoginAppletLoader(false); //
				LoginOneOffVO oneOffVO = loader.doLogin(str_usercode, null, null, null, false, true, null); //登录一下!!也取得了相应的菜单树等数据!!!包括角色等信息!!!
				loginUtil.setWLTLookAndFeel(oneOffVO.getCurrLoginUserVO().getAllLookAndFeels(), _mainPanel); //重新设置!!!
				_mainPanel.updateUI(); //
			} else {
				loginUtil.setDefaultLookAndFeel(_mainPanel); //默认风格!!
				_mainPanel.updateUI(); //
			}

			if (str_menuid != null) {
				AbstractWorkPanel workItemPanel = loginUtil.getWorkPanelByMenuVO(str_menuid); //
				workItemPanel.initialize(); //初始化,必须要执行!!
				_mainPanel.setLayout(new BorderLayout()); ////
				_mainPanel.add(workItemPanel); //
			} else if (str_clsname != null) { //如果类名不为空
				HashVO hvo = new HashVO(); //
				hvo.setAttributeValue("name", "直接Skip出的页面"); //
				hvo.setAttributeValue("usecmdtype", "1"); //使用的类型
				hvo.setAttributeValue("commandtype", "00"); //页面类型,自定义面板!!
				hvo.setAttributeValue("command", str_clsname); //类名
				AbstractWorkPanel workItemPanel = loginUtil.getWorkPanelByMenuVO(hvo); //
				workItemPanel.initialize(); //
				_mainPanel.setLayout(new BorderLayout()); ////
				_mainPanel.add(workItemPanel); //
			} else {
				_mainPanel.setLayout(new BorderLayout()); ////
				_mainPanel.add(new JLabel("既没有定义[menuid],也没有定义[clsname],无法加载任何页面!!")); //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
			_mainPanel.removeAll(); //
			_mainPanel.setLayout(new BorderLayout()); ////
			_mainPanel.add(new JLabel("加载页面发生异常:[" + ex.getMessage() + "]")); //
		} finally {
			loginUtil.startClientRefreshThread(); //启动客户端刷新线程!!
		}

	}
}
