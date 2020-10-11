package cn.com.pushworld.salary.ui;

import cn.com.infostrategy.ui.common.ServerPushToClientIFC;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.pushworld.salary.ui.chat.ChatWKPanel;

public class UIShowBox implements ServerPushToClientIFC {
	public void onExecute(Object obj) {
		Object objpanel = ChatWKPanel.panel;
		if (objpanel != null && objpanel instanceof ChatWKPanel) {
			ChatWKPanel panel = (ChatWKPanel) objpanel;
			if (obj instanceof String) {
				panel.removeuser(String.valueOf(obj));
			} else if (obj instanceof Object[]) {
				Object[] message = (Object[]) obj;
				if (message.length == 2) {
					panel.adduser((String) message[0], (String) message[1]);
				} else if (message.length == 3) {
					panel.appendMessage(message[2] + " " + UIUtil.getCurrTime() + "\r\n");
					panel.appendMessage("\t " + message[1] + "\r\n");
				}
			}

		}
	}
}
