package com.pushworld.ipushgrc.ui.tools.impexcel;


import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.UIRefPanel;

/**
 * 
 * @author wdl
 */
public class InputInfoPanel extends AbstractWorkPanel implements ActionListener {
	private BillCardPanel billcardpanel=null;
	private UIRefPanel ap=null;
	private RefItemVO refItemVO = null; //
	private String file="";
	private static final long serialVersionUID = 6931658108772459572L;
	@Override
	public void initialize() {
		billcardpanel=new BillCardPanel("INPUT_INFO_CODE1");
		billcardpanel.setEditableByEditInit();
		this.add(billcardpanel);
        ap = (UIRefPanel) billcardpanel.getCompentByKey("papers_url");
		JButton btn = ap.getBtn_ref();
		ActionListener[] actions=btn.getActionListeners();
		if(actions!=null){
			for(int i=0;i<actions.length;i++){
				btn.removeActionListener( actions[i]);
			}
		}
		btn.addActionListener(this);
	}
	public void actionPerformed(ActionEvent e) {
				JFileChooser chooser=new JFileChooser();
				chooser.setMultiSelectionEnabled(false); 
				int result = chooser.showOpenDialog((Component) (billcardpanel == null ? (ap == null ? this : ap) : billcardpanel)); //防止在最右下角弹出!
				if (result == 0 && chooser.getSelectedFile() != null) {
					 file=chooser.getSelectedFile().getPath();
				}else{
					return;
				}
				new SplashWindow(this, new AbstractAction() {
					public void actionPerformed(ActionEvent e) {
						try {
							refItemVO = new RefItemVO(); //
							refItemVO.setId(file); //
							refItemVO.setCode(null);
							refItemVO.setName(file); //
							billcardpanel.setCompentObjectValue("papers_url", refItemVO);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}, 366, 366);
	}

}
