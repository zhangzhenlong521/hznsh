/**************************************************************************
 * $RCSfile: AbstractCustomerButtonBarPanel.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata;

import java.awt.FlowLayout;

import javax.swing.JPanel;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;


/**
 * 客户自定义按钮栏类,即有一个地方可以让用户放自己的一些按钮或其他控件,一般都是按钮为主!!所以叫按钮栏类
 * 
 * 所有的风格中都有一项可以注册该类的一个子类,然后在所有风格模板中如果发现注册了该类,那么就创建该类实例,
 * 然后调用setParentFrame()方法将风格模板类本身句柄传进来,然后调用抽象方法initialize();初始化该类!!
 * 
 * 在initialize()方法中客户可以自由发挥随便加入自己的什么控件,比如按钮,输入框,然后随便进行自己的事件处理,
 * 因为只要他能够通过getParentFrame()方法得到父界面,并通过父界面从而得到页面上任何一个控件句柄,他就能够干一切他想所干的!!!
 * @author user
 *
 */
public abstract class AbstractCustomerButtonBarPanel extends JPanel {

	private AbstractWorkPanel workPanel = null; //根工作面板

	public AbstractCustomerButtonBarPanel() {
		this.setLayout(new FlowLayout()); //默认是水平布局,当然还可以在initialize()中将布局换掉!!
	}

	public abstract void initialize();

	/**
	 * 取得父亲工作面板!!!
	 * @return
	 */
	public AbstractWorkPanel getParentWorkPanel() {
		return workPanel;
	}

	/**
	 * 设置父亲工作面板!!!
	 * @param _workPanel
	 */
	public void setParentWorkPanel(AbstractWorkPanel _workPanel) {
		this.workPanel = _workPanel;
	}
}
/**************************************************************************
 * $RCSfile: AbstractCustomerButtonBarPanel.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 *
 * $Log: AbstractCustomerButtonBarPanel.java,v $
 * Revision 1.4  2012/09/14 09:22:57  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:40:56  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:45  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:23:14  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:32:04  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:12  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:01:55  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:57  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:39  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2009/02/19 07:30:59  wangjian
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:30  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:19  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:19  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:29  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:39  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:08  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 04:56:13  lujian
 * *** empty log message ***
 *
 *
**************************************************************************/
