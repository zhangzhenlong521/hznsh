package cn.com.pushworld.salary.ui.chat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTextArea;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;

public class ChatWKPanel extends AbstractWorkPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -36842235325782925L;
	private JList user = new JList();
	private WLTTextArea showarea = new WLTTextArea();

	private WLTTextArea talkarea = new WLTTextArea();

	HashMap<String, HashVO> loginuser = new HashMap<String, HashVO>();

	public static ChatWKPanel panel;

	public ChatWKPanel() {
		panel = this;
	}

	@Override
	public void initialize() {
		if (panel == null) {
			panel = new ChatWKPanel();
		}
		JScrollPane scrollpanel_show = new JScrollPane();
		scrollpanel_show.getViewport().add(showarea);

		JScrollPane scrollpanel_talk = new JScrollPane();
		scrollpanel_talk.getViewport().add(talkarea);
		JPanel talkPanel = new JPanel(new BorderLayout());
		WLTButton btntalk = new WLTButton("发送");
		btntalk.addActionListener(this);
		btntalk.setPreferredSize(new Dimension(80, 80));
		talkPanel.add(scrollpanel_talk, BorderLayout.CENTER);
		talkPanel.add(btntalk, BorderLayout.EAST);
		talkPanel.setMinimumSize(new Dimension(-1, 80));
		talkPanel.setMaximumSize(new Dimension(-1, 80));

		WLTSplitPane up_down = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, scrollpanel_show, talkPanel);
		user.setModel(new DefaultListModel());
		WLTSplitPane left_right = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, user, up_down);

		left_right.setDividerLocation(180);
		this.add(left_right);

		try {
			HashVO[] hvo = getService().getAllInstallUser();
			DefaultListModel dlm = (DefaultListModel) user.getModel();
			HashVO all = new HashVO();
			all.setAttributeValue("name", "所有人");
			all.setAttributeValue("session", "all");
			all.setToStringFieldName("name");
			dlm.addElement(all);
			for (int i = 0; i < hvo.length; i++) {
				dlm.addElement(hvo[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void adduser(String session, String name) {
		DefaultListModel dlm = (DefaultListModel) user.getModel();
		HashVO hvo = new HashVO();
		hvo.setAttributeValue("name", name);
		hvo.setAttributeValue("session", session);
		hvo.setToStringFieldName("name");
		dlm.addElement(hvo);
		showarea.append(UIUtil.getCurrTime() + "->" + name + " 上线了\r\n");
		loginuser.put(session, hvo);
		user.repaint();
	}

	public void removeuser(String session) {
		DefaultListModel dlm = (DefaultListModel) user.getModel();
		Object obj = loginuser.get(session);
		if (obj != null) {
			HashVO hvo = (HashVO) obj;
			showarea.append(UIUtil.getCurrTime() + "->" + hvo.getStringValue("name") + " 下线了\r\n");
			dlm.removeElement(hvo);
			user.repaint();
		}

	}

	public void appendMessage(String _message) {
		showarea.append(_message);
	}

	public void actionPerformed(ActionEvent actionevent) {
		Object[] selectuser = (Object[]) user.getSelectedValues();
		if (selectuser == null || selectuser.length == 0) {
			showarea.append("请选择一个联系人\r\n");
		}
		if (TBUtil.isEmpty(talkarea.getText())) {
			return;
		}
		try {
			List sessions = new ArrayList();
			if (selectuser.length == 1) {
				if ("all".equals(((HashVO) selectuser[0]).getStringValue("session"))) {
					return;
				}
			}
			int i = 0;
			if ("all".equals(((HashVO) selectuser[0]).getStringValue("session"))) {
				i++;
			}
			for (; i < selectuser.length; i++) {
				sessions.add(((HashVO) selectuser[i]).getStringValue("session"));
			}
			getService().sendMessage((String[]) sessions.toArray(new String[0]), talkarea.getText());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private SalaryServiceIfc service;

	private SalaryServiceIfc getService() {
		if (service == null) {
			try {
				service = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return service;
	}
}
