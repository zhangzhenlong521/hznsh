package cn.com.infostrategy.bs.workflow.msg;

import java.util.HashMap;
import java.util.List;

/**
 * ¶ÌÐÅ½Ó¿Ú
 * @author haoming
 * create by 2014-11-25
 */
public abstract class SendSMSIFC {
	public boolean send(String receiveUser, String msg, String billtype, String createuser, HashMap prop) {
		return false;
	}

	public boolean scoreSend(List list) {
		return false;
	}
}
