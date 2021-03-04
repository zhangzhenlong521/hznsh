/**************************************************************************
 * $RCSfile: StyleConfigPanel_00.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.config;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.styletemplet.AbstractTempletRefPars;

public class StyleConfigPanel_00 extends AbstractTempletRefPars {

	private static final long serialVersionUID = -6147862007359706108L;

	private JTextField text = null;

	public StyleConfigPanel_00(String _text) {
		this.setLayout(new BorderLayout());
		this.add(getCenterPanel(_text));

	}

	private JPanel getCenterPanel(String _text) {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout());  //

		JLabel label = new JLabel("×Ô¶¨ÒåWorkPanel:", SwingConstants.RIGHT); //
		label.setPreferredSize(new Dimension(125, 20));

		text = new JTextField(_text);
		text.setPreferredSize(new Dimension(500, 20));

		panel.add(label); //
		panel.add(text); //
		return panel;
	}

	public VectorMap getParameters() {
		VectorMap map = new VectorMap(); //		
		map.put("frame", text.getText().trim());
		return map;
	}

	public void stopEdit() {

	}

	protected String bsInformation() {
		return null;
	}

	protected String uiInformation() {
		return null;
	}


}
