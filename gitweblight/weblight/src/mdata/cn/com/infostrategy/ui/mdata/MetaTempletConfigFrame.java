/**************************************************************************
 * $RCSfile: MetaTempletConfigFrame.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class MetaTempletConfigFrame extends JFrame {
	private static final long serialVersionUID = -4655480510003085745L;

	private MetaTempletConfigPanel configPanel = null;

	public MetaTempletConfigFrame() {
		this.setTitle("ÔªÔ­Ä£°åÅäÖÃ");
		this.setLocation(0, 0);
		this.setSize(1000, 700);
		initialize();
	}

	private void initialize() {
		this.getContentPane().setLayout(new BorderLayout());
		configPanel = new MetaTempletConfigPanel();  //
		configPanel.initialize();
		this.getContentPane().add(configPanel, BorderLayout.CENTER);
		this.setVisible(true); //
	}
}