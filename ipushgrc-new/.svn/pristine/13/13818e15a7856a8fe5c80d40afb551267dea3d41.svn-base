package com.pushworld.ipushgrc.ui.tools.impexcel;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFileChooser;

import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;
/**
 * 
 * @author wdl
 *
 */
public class ChooseAddress extends AbstractRefDialog {

	private BillCardPanel billCardPanel = null;
	private RefItemVO returnRefItemVO = null; //
	public ChooseAddress(Container _parent, String _title, RefItemVO refItemVO,
			BillPanel panel) {
		super(_parent, _title, refItemVO, panel);
		billCardPanel = (BillCardPanel) panel;
	}

	private static final long serialVersionUID = -2972930686602447006L;

	@Override
	public RefItemVO getReturnRefItemVO() {
		return null;
	}

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout()); //
		JFileChooser chooser=new JFileChooser();
		chooser.setMultiSelectionEnabled(false); 
		int result = chooser.showOpenDialog(this.billCardPanel == null ? this : this.billCardPanel); //防止在最右下角弹出!
//		if (result == 0 && chooser.getSelectedFile() != null) {
//			setUploadDir(chooser.getSelectedFile().getParent());
//		} else {
//			return;
//		}
	   MessageBox.show(chooser.getSelectedFile().getParent());
	
	}

}
