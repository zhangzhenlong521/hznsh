package cn.com.infostrategy.ui.sysapp.corpdept;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JSplitPane;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryEvent;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;

/**
 * 岗位组维护页面，岗位组和实际岗位都保存在pub_post表中，实际岗位都会跟机构关联，所以根据deptid是否为空，可以判断是否为岗位组
 * @author lcj
 *
 */
public class RefPostEditPanel extends AbstractWorkPanel implements ActionListener, BillListSelectListener, BillListAfterQueryListener {
	private BillListPanel list_post, list_postduty; //岗位,岗位职责
	private WLTButton btn_insert, btn_seq;

	@Override
	public void initialize() {
		list_post = new BillListPanel("PUB_POST_CODE2"); //岗位
		list_post.setTitleLabelText("岗位组");
		list_post.setItemVisible("post_status", false);//不想新增模板了，为了不影响其他页面，代码设置一下隐藏岗位状态和显示岗位描述
		list_post.setItemVisible("refpostid", false);
		list_post.setItemVisible("descr", true);
		list_post.setQuickQueryPanelVisiable(true);
		list_post.setDataFilterCustCondition("deptid is null");
		list_post.setItemWidth("code", 200);//不想新增模板了，为了不影响其他页面，代码设置一下列宽
		list_post.setItemWidth("name", 200);
		list_post.setItemWidth("descr", 200);
		String panelStyle = this.getMenuConfMapValueAsStr("页面布局");//"页面布局"，有四种（默认为第一种）：1-岗位组维护;2-岗位组查询;3-岗位组职责维护;4-岗位组职责查询;
		if (panelStyle == null || panelStyle.trim().equals("")) {
			panelStyle = "1";
		}
		if ("1".equals(panelStyle)) {//岗位组维护
			list_post.addBatchBillListButton(new WLTButton[] { WLTButton.createButtonByType(WLTButton.LIST_POPINSERT), WLTButton.createButtonByType(WLTButton.LIST_POPEDIT), WLTButton.createButtonByType(WLTButton.LIST_DELETE) });
			list_post.repaintBillListButton();
			this.add(list_post); //
		} else {
			list_post.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));
			list_post.repaintBillListButton();
			if ("2".equals(panelStyle)) {//岗位组查询
				this.add(list_post); //
			} else if ("3".equals(panelStyle)) {//岗位组职责维护
				list_post.addBillListSelectListener(this);
				list_post.addBillListAfterQueryListener(this);//增加岗位查询后事件，清空职责列表【李春娟/2012-08-08】
				list_postduty = new BillListPanel("CMP_POSTDUTY_CODE1"); //岗位职责
				btn_insert = new WLTButton("新增"); //
				btn_insert.addActionListener(this); //
				btn_seq = new WLTButton("排序");//增加职责排序按钮【李春娟/2014-12-16】
				btn_seq.addActionListener(this);

				list_postduty.addBatchBillListButton(new WLTButton[] { btn_insert, WLTButton.createButtonByType(WLTButton.LIST_POPEDIT), WLTButton.createButtonByType(WLTButton.LIST_DELETE), btn_seq }); //批量设置按钮!!!
				list_postduty.repaintBillListButton(); //

				WLTSplitPane split1 = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, list_post, list_postduty); //
				split1.setDividerLocation(250); //

				this.add(split1); //
			} else if ("4".equals(panelStyle)) {//岗位组职责查询
				list_post.addBillListSelectListener(this);
				list_post.addBillListAfterQueryListener(this);//增加岗位查询后事件，清空职责列表【李春娟/2012-08-08】
				list_postduty = new BillListPanel("CMP_POSTDUTY_CODE1"); //岗位岗责
				list_postduty.addBatchBillListButton(new WLTButton[] { WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD) }); //批量设置按钮!!!
				list_postduty.repaintBillListButton(); //
				WLTSplitPane split1 = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, list_post, list_postduty); //
				split1.setDividerLocation(250); //
				this.add(split1); //
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_insert) {
			onInsert(); //新增操作!
		} else if (e.getSource() == btn_seq) {
			onSeqDuty();
		}
	}

	/**
	 * 新增岗位岗责!!
	 */
	private void onInsert() {
		BillVO billVO = list_post.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, "请选择一个岗位进行此操作!"); //
			return;
		}
		HashMap defaultValueMap = new HashMap(); //
		defaultValueMap.put("postid", billVO.getStringValue("id")); //
		list_postduty.doInsert(defaultValueMap); //新增岗位岗责!!!
	}

	/**
	 * 职责排序【李春娟/2014-12-16】
	 */
	private void onSeqDuty() {
		SeqListDialog dialog_post = new SeqListDialog(this, "职责排序", list_postduty.getTempletVO(), list_postduty.getAllBillVOs());
		dialog_post.setVisible(true);
		if (dialog_post.getCloseType() == 1) {//如果点击确定返回，则需要刷新一下页面
			list_postduty.refreshData(); //
		}
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		BillVO billvo = list_post.getSelectedBillVO();
		if (billvo == null) {
			return;
		}
		list_postduty.QueryDataByCondition("postid=" + billvo.getStringValue("id"));
	}

	public void onBillListAfterQuery(BillListAfterQueryEvent _event) {
		list_postduty.clearTable();
	}

}
