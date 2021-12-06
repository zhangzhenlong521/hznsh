package com.pushworld.ipushgrc.ui.law;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Toolkit;
import java.util.HashMap;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.report.BillHtmlPanel;
/**
 * 弹出法规Html预览的对话框，自动弹出显示。
 * @author hm
 *
 */
public class LawShowHtmlDialog {
	public LawShowHtmlDialog(Container _parent,String _lawid ){
		this(_parent,_lawid,null);
	}
	public LawShowHtmlDialog(Container _parent,String _lawid,String [] prikey){
		this(_parent, _lawid, prikey, null);
	}
	public LawShowHtmlDialog(BillListPanel listPanel){
		BillVO vo = listPanel.getSelectedBillVO();
		if(vo == null){
			MessageBox.showSelectOne(listPanel);
			return;
		}
		currShowHtmlDialog(listPanel,vo.getStringValue("id"), null,null);
	}
	public LawShowHtmlDialog(Container _parent,String _lawid,String [] prikey,String[] itemkeys){
		currShowHtmlDialog(_parent,_lawid,prikey,itemkeys);
	}
	private void currShowHtmlDialog(Container _parent,String _lawid,String [] prikey,String []itemKey){
		BillHtmlPanel billHTMLPanel = new BillHtmlPanel();
		HashMap hashMap = new HashMap();
		hashMap.put("id", _lawid);
		hashMap.put("primarykey", prikey);
		hashMap.put("table_main", "law_law");
		hashMap.put("table_main_file", "attachfile");
		hashMap.put("column_name", "lawname");
		hashMap.put("column_title", "itemtitle");
		hashMap.put("table_content", "law_law_item");
		hashMap.put("content", "itemcontent");
		hashMap.put("content_foreignkey", "lawid");
		hashMap.put("content_filekey", "attachfile");
		hashMap.put("url", System.getProperty("CALLURL"));
		hashMap.put("hlightitem", itemKey);//传入需要高亮标记出来的条目IDs。
		billHTMLPanel.loadhtml("com.pushworld.ipushgrc.bs.law.p010.LawPrimaryKeyAllFileInquire", hashMap);
		BillDialog dialog = new BillDialog(_parent, "法规查看", 1000, Toolkit.getDefaultToolkit().getScreenSize().height - 30);
		dialog.setLayout(new BorderLayout());
		dialog.add(billHTMLPanel);
		dialog.setVisible(true);
	}
}
