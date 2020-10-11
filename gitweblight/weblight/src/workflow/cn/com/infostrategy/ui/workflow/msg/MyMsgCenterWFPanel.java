package cn.com.infostrategy.ui.workflow.msg;

import java.awt.BorderLayout;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;

/**
 * 消息中心
 * 2个页签收件箱、发件箱（以后可考虑草稿箱、垃圾箱）
 * 根据徐老师在任务中心类中"
 * 后来觉得还是需要一张专门的消息中心表,像抄送的流程任务,或者流程结束时后的给发起人的提醒! 或者种系统消息,比如系统于什么时候重启过,系统发布了新版本,某某人员加入系统等!!
 * 这个消息表叫pub_msgcenter,这种表里有三种重要字段：接收人,接收角色,接收机构范围!! 即有时不是直接给某个人,而给某些角色,或某个机构范围下的某些角色!
 * 由于存在给某些角色的情况,所以还需要一张表记录某条消息是否已被某人阅读过,即有"未读消息"与"已读消息"之分!! 已读消息表名叫 pub_msgcenter_readed
 *"这句话来开发，只不过扩展了一个规则表pub_msgcenter_rule，即可实现发送逻辑与查看逻辑的扩展
 */
public class MyMsgCenterWFPanel extends AbstractWorkPanel implements ChangeListener {
	private JTabbedPane wt = null;
	private JPanel unReadPanel = null;//未读消息
	private JPanel readedPanel = null;//已读消息
	private JPanel sendedPanel = null;//已发消息
	private int unread, readed, sended = 0;
	private String unsql, resql, sesql = null;

	public MyMsgCenterWFPanel() {
		loadData();
		initialize();
	}

	public void loadData() {
		try {
			HashMap param = new HashMap();
			param.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
			param.put("corpid", ClientEnvironment.getCurrLoginUserVO().getBlDeptId());
			param.put("roleids", ClientEnvironment.getCurrLoginUserVO().getAllRoleIds());
			HashMap countmap = UIUtil.commMethod("cn.com.infostrategy.bs.workflow.msg.MsgBsUtil", "getMsgCountMap", param);
			if (countmap != null && countmap.size() > 0) {
				unread = (Integer) countmap.get("未读消息");
				readed = (Integer) countmap.get("已读消息");
				sended = (Integer) countmap.get("已发消息");
				unsql = (String) countmap.get("未读SQL");
				resql = (String) countmap.get("已读SQL");
				sesql = (String) countmap.get("已发SQL");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void refresh() {
		loadData();
		if (wt.getSelectedIndex() == 0) {
			((MyMsgCenter_UnReadWFPanel) unReadPanel).refresh();
			wt.setTitleAt(1, "已读消息(" + readed + ")");
			wt.setTitleAt(2, "已发消息(" + sended + ")");
		} else if (wt.getSelectedIndex() == 1) {
			wt.setTitleAt(0, "未读消息(" + unread + ")");
			((MyMsgCenter_ReadedWFPanel) readedPanel).refresh();
			wt.setTitleAt(2, "已发消息(" + sended + ")");
		} else if (wt.getSelectedIndex() == 2) {
			wt.setTitleAt(0, "未读消息(" + unread + ")");
			wt.setTitleAt(1, "已读消息(" + readed + ")");
			((MyMsgCenter_SendedWFPanel) sendedPanel).refresh();
		}
	}

	public void refresh(int c) {
		if (wt.getSelectedIndex() == 0) {
			((MyMsgCenter_UnReadWFPanel) unReadPanel).refresh();
		} else if (wt.getSelectedIndex() == 1) {
			((MyMsgCenter_ReadedWFPanel) readedPanel).refresh();
		} else if (wt.getSelectedIndex() == 2) {
			((MyMsgCenter_SendedWFPanel) sendedPanel).refresh();
		}
	}

	public void initialize() {
		this.setLayout(new BorderLayout());
		wt = new JTabbedPane(2);
		wt.addTab("未读消息(" + unread + ")", UIUtil.getImage("zt_062.gif"), getUnReadPanel());//PUB_MSGCENTER_CODE1
		wt.addTab("已读消息(" + readed + ")", UIUtil.getImage("zt_063.gif"), new JPanel());
		wt.addTab("已发消息(" + sended + ")", UIUtil.getImage("office_039.gif"), new JPanel());
		wt.addChangeListener(this);//office_168.gif 039 167
		this.add(wt, BorderLayout.CENTER);
	}

	public JPanel getUnReadPanel() {
		if (unReadPanel == null) {
			unReadPanel = new MyMsgCenter_UnReadWFPanel(this, unsql);
		}
		return unReadPanel;
	}

	public JPanel getReadedPanel() {
		if (readedPanel == null) {
			readedPanel = new MyMsgCenter_ReadedWFPanel(this, resql);
		}
		return readedPanel;
	}

	public JPanel getSendedPanel() {
		if (sendedPanel == null) {
			sendedPanel = new MyMsgCenter_SendedWFPanel(this, sesql);
		}
		return sendedPanel;
	}

	public void stateChanged(ChangeEvent changeevent) {
		changeRadioPanel((JTabbedPane) changeevent.getSource());
	}

	public void changeRadioPanel(JTabbedPane _tabPane) {
		int selectkey = _tabPane.getSelectedIndex();
		if (selectkey == 0) {
			if (unReadPanel == null) {
				unReadPanel = new MyMsgCenter_UnReadWFPanel(this, unsql);
				JPanel jp = (JPanel) _tabPane.getSelectedComponent();
				jp.removeAll();
				jp.setLayout(new BorderLayout());
				jp.add(unReadPanel);
				jp.updateUI();
			} else {
				if (unread != ((MyMsgCenter_UnReadWFPanel) unReadPanel).getBl().getLi_TotalRecordCount()) {
					((MyMsgCenter_UnReadWFPanel) unReadPanel).refresh();
				}
			}
		} else if (selectkey == 1) {
			if (readedPanel == null) {
				readedPanel = new MyMsgCenter_ReadedWFPanel(this, resql);
				JPanel jp1 = (JPanel) _tabPane.getSelectedComponent();
				jp1.removeAll();
				jp1.setLayout(new BorderLayout());
				jp1.add(readedPanel);
				jp1.updateUI();
			} else {
				if (readed != ((MyMsgCenter_ReadedWFPanel) readedPanel).getBl().getLi_TotalRecordCount()) {
					((MyMsgCenter_ReadedWFPanel) readedPanel).refresh();
				}
			}
		} else if (selectkey == 2) {
			if (sendedPanel == null) {
				sendedPanel = new MyMsgCenter_SendedWFPanel(this, sesql);
				JPanel jp2 = (JPanel) _tabPane.getSelectedComponent();
				jp2.removeAll();
				jp2.setLayout(new BorderLayout());
				jp2.add(sendedPanel);
				jp2.updateUI();
			} else {
				if (sended != ((MyMsgCenter_SendedWFPanel) sendedPanel).getBl().getLi_TotalRecordCount()) {
					((MyMsgCenter_SendedWFPanel) sendedPanel).refresh();
				}
			}
		}
	}

	public JTabbedPane getWt() {
		return wt;
	}

	public int getUnread() {
		return unread;
	}

	public void setUnread(int unread) {
		this.unread = unread;
	}

	public int getReaded() {
		return readed;
	}

	public void setReaded(int readed) {
		this.readed = readed;
	}

	public int getSended() {
		return sended;
	}

	public void setSended(int sended) {
		this.sended = sended;
	}

}
