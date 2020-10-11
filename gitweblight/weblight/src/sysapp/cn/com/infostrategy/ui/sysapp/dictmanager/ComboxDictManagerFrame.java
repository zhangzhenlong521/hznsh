/**************************************************************************
 * $RCSfile: ComboxDictManagerFrame.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:19:33 $
 **************************************************************************/
package cn.com.infostrategy.ui.sysapp.dictmanager;

import javax.swing.JFrame;

/**
 * 下拉框数据字典维护
 * 
 * @author Administrator
 * 
 */
public class ComboxDictManagerFrame extends JFrame {

	public ComboxDictManagerFrame() {
		this.setTitle("下拉框数据字典维护");
		this.setSize(900, 600);
		this.setLocation(50, 0);
		ComboxDictManagerPanel contentPanel = new ComboxDictManagerPanel(); //
		this.getContentPane().add(contentPanel); //加入!!!
	}

}
