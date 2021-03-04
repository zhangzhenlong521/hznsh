package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTTextArea;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.VFlowLayoutPanel;
import cn.com.infostrategy.ui.mdata.listcomp.ListCPanel_Ref;

/**
 * 模板对比参照！
 * @author hm
 *
 */
public class TempletComPareRefDialog extends AbstractRefDialog implements ActionListener{
	private static final long serialVersionUID = 1L;
	private WLTButton btn_confirm,btn_cancel;
	private Container parent;
	private RefItemVO refItemVO;
	private BillPanel panel ;
	private WLTTextArea text_1 = new WLTTextArea();   //设定数据库中文本框
	private String key1 = "数据库中值:", key2 = "Xml中值:";
	public TempletComPareRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
		super(_parent, _title, refItemVO, panel);
		parent = _parent;
		this.refItemVO = refItemVO;
		this.panel = panel;
	}
	
	public TempletComPareRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel, String _key1, String _key2) {
		super(_parent, _title, refItemVO, panel);
		parent = _parent;
		this.refItemVO = refItemVO;
		this.panel = panel;
		this.key1 = _key1;
		this.key2 = _key2;
	}
	
	public JPanel getSouthPanel(){
		WLTPanel panel = new WLTPanel(WLTPanel.HORIZONTAL_LEFT_TO_RIGHT,new FlowLayout());
		panel.add(btn_confirm);
		panel.add(btn_cancel);
		return panel;
	}

	public RefItemVO getReturnRefItemVO() {
		return refItemVO;
	}

	public void initialize() {
		WLTLabel lable_1 = new WLTLabel(key1);
		WLTLabel lable_2 = new WLTLabel(key2);
		text_1.setPreferredSize(new Dimension(400, 150));
		WLTTextArea text_2 = new WLTTextArea(); //设定xml数据文本框
		text_2.setPreferredSize(new Dimension(400, 150));
		btn_confirm = new WLTButton("确定");
		btn_confirm.addActionListener(this);
		btn_cancel = new WLTButton("取消");
		btn_cancel.addActionListener(this);
		String itemKey = null;
		HashVO hashvo = null;
		if(panel instanceof BillListPanel){
			ListCPanel_Ref ref = (ListCPanel_Ref) parent;
			itemKey = ref.getItemKey();
			BillListPanel listPanel = (BillListPanel) panel;
			HashVO[] vos = (HashVO[]) listPanel.getClientProperty("currvalue");
			int selectrow= listPanel.getSelectedRow();
			hashvo = vos[selectrow];
		}else if(panel instanceof BillCardPanel){
			CardCPanel_Ref ref = (CardCPanel_Ref) parent;
			itemKey = ref.getItemKey();
			BillCardPanel cardPanel = (BillCardPanel) panel;
			hashvo =(HashVO) cardPanel.getClientProperty("currvalue");
		}
		Object obj = hashvo.getUserObject(itemKey.toLowerCase());
		List list = new ArrayList();
		list.add(lable_1);
		list.add(text_1);
		if(obj instanceof String[]){//不同值
			String [] value = (String[]) obj;
			text_1.setText(value[0]);
			text_2.setText(value[1]);
			list.add(lable_2);
			list.add(text_2);
		}else if(obj instanceof String){
			text_1.setText(hashvo.getStringValue(itemKey));
		}else if(obj == null && refItemVO !=null){
			text_1.setText(refItemVO.getName());
			text_2.setVisible(false);
		}
		VFlowLayoutPanel vpanel = new VFlowLayoutPanel(list);
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(vpanel,BorderLayout.CENTER);
		this.getContentPane().add(getSouthPanel(),BorderLayout.SOUTH);
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btn_confirm){
			onConfirm();
		}else if(e.getSource() == btn_cancel){
			onCancel();
		}
	}
	private void onConfirm(){
		String text =text_1.getText();
		if(refItemVO == null){
			refItemVO = new RefItemVO(text,null,text);
		}else if(refItemVO.getId()!=null && !refItemVO.getId().equals(text)){
			refItemVO = new RefItemVO(text,null,text);	
		}
		this.setCloseType(1);
		this.dispose();
	}
	private void onCancel(){
		this.dispose();
	}

}
