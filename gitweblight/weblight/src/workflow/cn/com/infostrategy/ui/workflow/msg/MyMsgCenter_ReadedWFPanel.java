package cn.com.infostrategy.ui.workflow.msg;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.sysapp.login.taskcenter.TaskAndMsgCenterPanel;

/**
 *
 */
public class MyMsgCenter_ReadedWFPanel extends JPanel implements ActionListener, BillListHtmlHrefListener {
	private BillListPanel bl = null;
	private String sql = null;
	private WLTButton send, delete, select = null;
	private MyMsgUIUtil util = new MyMsgUIUtil();
	private MyMsgCenterWFPanel msgpanel = null;
	private TaskAndMsgCenterPanel taskpanel = null;

	public MyMsgCenter_ReadedWFPanel(JPanel _parent, String _sql) {
		this.sql = _sql;
		if(_parent instanceof MyMsgCenterWFPanel) {
			this.msgpanel = (MyMsgCenterWFPanel)_parent;
		}
		if(_parent instanceof TaskAndMsgCenterPanel) {
			this.taskpanel = (TaskAndMsgCenterPanel)_parent;
		}
		initialize();
	}
	
	public void refresh() {
		query();
		if(msgpanel != null) {
			msgpanel.refresh();
		}
		if(taskpanel != null) {
			taskpanel.onRefreshMsgOnly();
		}
	}
	
	public int getCount() {
		return bl.getLi_TotalRecordCount();
	}
	
	public void query() {
		bl.QueryDataByCondition(sql);
	}

	public void initialize() {
		this.setLayout(new BorderLayout());
		bl = new BillListPanel("PUB_MSGCENTER_CODE1");
		bl.setTitleLabelText("已读消息");
		bl.setCanShowCardInfo(false);
		bl.addBillListHtmlHrefListener(this);
		bl.QueryDataByCondition(sql);
		send = WLTButton.createButtonByType(WLTButton.COMM, "发信息");
		delete = WLTButton.createButtonByType(WLTButton.COMM, "删除");
		send.addActionListener(this);
		delete.addActionListener(this);
		if(TBUtil.getTBUtil().getSysOptionBooleanValue("工作流任务中心消息中心是否显示发信息按钮", true)) {
			bl.addBillListButton(send);
		}
		bl.addBillListButton(delete);
		select = WLTButton.createButtonByType(WLTButton.COMM, "浏览");
		select.addActionListener(this);
		bl.addBillListButton(select);
		bl.repaintBillListButton();
		this.add(bl);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == delete) {
			util.deleteMsg(this, bl);
		} else if (e.getSource() == send) {
			util.sendMsg(this);
		} else if (e.getSource() == select) {
			if(bl.getSelectedBillVO() == null) {
				MessageBox.show(bl, "请选择需要浏览的消息!");
				return;
			} else {
				try {
					util.readMsg(bl.getSelectedBillVO(), this, 1);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public BillListPanel getBl() {
		return bl;
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		try {
			util.readMsg(_event.getBillListPanel().getBillVO(_event.getRow()), this, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public MyMsgCenterWFPanel getMsgpanel() {
		return msgpanel;
	}

	public WLTButton getSend() {
		return send;
	}

	public void setSend(WLTButton send) {
		this.send = send;
	}
}
