/**************************************************************************
 * $RCSfile: ComboxDictManagerFrame.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:19:33 $
 **************************************************************************/
package cn.com.infostrategy.ui.sysapp.dictmanager;

import javax.swing.JFrame;

/**
 * �����������ֵ�ά��
 * 
 * @author Administrator
 * 
 */
public class ComboxDictManagerFrame extends JFrame {

	public ComboxDictManagerFrame() {
		this.setTitle("�����������ֵ�ά��");
		this.setSize(900, 600);
		this.setLocation(50, 0);
		ComboxDictManagerPanel contentPanel = new ComboxDictManagerPanel(); //
		this.getContentPane().add(contentPanel); //����!!!
	}

}
