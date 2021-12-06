package com.pushworld.ipushgrc.ui.law.p050;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.BillTreeNodeVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

/**
 * 	外规解读【法律条文标题】的参照说明！
 * @author hm
 *
 */
public class LawExpositItemRefDialog extends AbstractRefDialog implements BillTreeSelectListener, ActionListener {

	private BillTreePanel billTree_item = new BillTreePanel("LAW_LAW_ITEM_CODE1"); // 外规条目树
	private BillCardPanel billCard_item = new BillCardPanel("LAW_LAW_ITEM_CODE1");
	private WLTPanel wltPanel;
	private WLTButton btn_ok, btn_cancel;

	private String lawid = null;
	private String lawName = null;

	public LawExpositItemRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
		super(_parent, _title, refItemVO, panel);
		BillCardPanel cardpanel = (BillCardPanel) panel;
		lawid = cardpanel.getBillVO().getStringValue("lawid");
		lawName = cardpanel.getBillVO().getStringValue("lawname");
		setSize(new Dimension(800, 550));//弹出框右边的卡片太小了，出现滚动条就不好看了，故调整了窗口大小【李春娟/2012-03-26】
		locationToCenterPosition();
	}

	public RefItemVO getReturnRefItemVO() {
		String id = "";
		String itemtitle = "";
		String itemcontent = "";
		if (billTree_item.getSelectedNode() != null && billTree_item.getSelectedNode().isRoot()) {
			id = "0";
			itemtitle = "《全文》";
			itemcontent = "《全文》";
		} else {
			BillVO vo = billTree_item.getSelectedVO();
			id = vo.getStringValue("id");
			itemtitle = vo.getStringValue("itemtitle");
			itemcontent = vo.getStringValue("itemcontent");
		}
		return new RefItemVO(id, itemcontent, itemtitle);
	}

	public void initialize() {
		this.setLayout(new BorderLayout());
		wltPanel = new WLTPanel(WLTPanel.HORIZONTAL_LEFT_TO_RIGHT, new FlowLayout(), LookAndFeel.defaultShadeColor1, false);
		btn_ok = new WLTButton("确定");
		btn_ok.addActionListener(this);
		btn_cancel = new WLTButton("取消");
		btn_cancel.addActionListener(this);
		billTree_item.setMoveUpDownBtnVisiable(false);
		if (lawName != null && !lawName.equals("")) {
			BillTreeNodeVO vo = new BillTreeNodeVO(-1, lawName);
			billTree_item.getRootNode().setUserObject(vo);
		}
		billTree_item.queryData(" select * from law_law_item where lawid = '" + lawid + "' order by abs(id)");
		billTree_item.addBillTreeSelectListener(this);
		WLTSplitPane splitPane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTree_item, billCard_item);
		splitPane.setDividerLocation(250);
		wltPanel.add(btn_ok);
		wltPanel.add(btn_cancel);
		this.getContentPane().add(splitPane, BorderLayout.CENTER);
		this.getContentPane().add(wltPanel, BorderLayout.SOUTH);
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		if (billTree_item.getSelectedNode() == null) {
			return;
		}
		if (billTree_item.getSelectedNode().isRoot()) {
			billCard_item.clear();
			billCard_item.setValueAt("itemtitle", new StringItemVO("《全文》"));
			billCard_item.setValueAt("itemcontent", new StringItemVO("《全文》"));
			return;
		}
		billCard_item.setBillVO(billTree_item.getSelectedVO());
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_ok) {
			if (billTree_item.getSelectedVO() == null && !billTree_item.getSelectedNode().isRoot()) {
				MessageBox.showSelectOne(this.getContentPane());
				return;
			}
			setCloseType(1);
			this.dispose();
		} else {
			setCloseType(2);
			this.dispose();
		}

	}

}
