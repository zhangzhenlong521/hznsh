package cn.com.infostrategy.bs.sysapp.mainscreen;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.common.CurrSessionVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;

/**
 * 邮件处理工具,使用Jmail工具包,实现发送与接收邮件功能!
 * 只支持smtp与pop3协议! 不支持微软的 Exchange邮件协议!!
 * @author user
 *
 */
public class MailUtil {
	private CommDMO dmo = new CommDMO();
	private TBUtil tbutil = new TBUtil();

	/**
	 * 发送邮件..
	 * @param _smtpAddr,smtp服务器地址,比如:smtp.163.com
	 * @param _fromUser,发件人邮件地址
	 * @param _toUsers,收件人地址,可以是一个列表
	 */
	public void sendMail(String _smtpAddr, String _fromUser, String psw, String[] _toUsers, String _title, String _message) {
		sendMail(_smtpAddr, _fromUser, psw, _toUsers, null, _title, _message);
	}

	/**
	 * 发送邮件..
	 * @param _smtpAddr,smtp服务器地址,比如:smtp.163.com
	 * @param _fromUser,发件人邮件地址
	 * @param _toUsers,收件人地址,可以是一个列表
	 * @param _ccUsers,抄送人。
	 */
	public void sendMail(String _smtpAddr, String _fromUser, String psw, String[] _toUsers, String[] _ccUsers, String _title, String _message) {
		try {
			if ((_toUsers == null || _toUsers.length == 0) && (_ccUsers == null || _ccUsers.length == 0)) {
				System.out.println(">>>没有收件人" + _title);
				return;
			}
			//邮件发送方式：0-什么都没有，1-只发邮件，2-只存日志，3-邮件、日志都有 Gwang 2016-05-23
			int mailMod = TBUtil.getTBUtil().getSysOptionIntegerValue("邮件发送方式", 3);
			if (mailMod == 0) {
				return;
			}
			InternetAddress ia_tos[] = new InternetAddress[_toUsers.length];
			StringBuffer tousers_sb = new StringBuffer();
			for (int i = 0; i < ia_tos.length; i++) {
				ia_tos[i] = new InternetAddress(_toUsers[i], _toUsers[i]); //
				tousers_sb.append(_toUsers[i] + ";");
			}
			Properties props = new Properties();

			props.put("mail.smtp.host", _smtpAddr);
			props.put("mail.smtp.auth", "true");
			final String username = _fromUser.split("@")[0];
			final String pass = psw;
			System.out.println(">>>发送邮件：username=" + username + ",pwd=" + pass);
			Session session = Session.getDefaultInstance(props, new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, pass);
				}
			});
			Message message = new MimeMessage(session);
			InternetAddress ia_from = new InternetAddress(_fromUser, ServerEnvironment.getProperty("PROJECT_NAME_CN") + " 管理员");
			StringBuffer ccusers_sb = new StringBuffer();
			if (_ccUsers != null && _ccUsers.length > 0) {
				InternetAddress ia_ccs[] = new InternetAddress[_ccUsers.length];
				for (int i = 0; i < ia_ccs.length; i++) {
					ia_ccs[i] = new InternetAddress(_ccUsers[i], _ccUsers[i]);
					ccusers_sb.append(_ccUsers[i] + ";");
				}
				message.setRecipients(Message.RecipientType.CC, ia_ccs);//添加抄送人
			}

			if (mailMod == 2 || mailMod == 3) { //先存日志后发送邮件【李春娟/2016-12-14】
				InsertSQLBuilder insertlog = new InsertSQLBuilder("pub_mail_log");
				insertlog.putFieldValue("id", dmo.getSequenceNextValByDS(null, "S_pub_mail_log"));
				CurrSessionVO sessionvo = dmo.getCurrSession();
				if (sessionvo != null) {//debug时会报空指针异常，故判断一下【李春娟/2016-11-21】
					insertlog.putFieldValue("createuser", dmo.getCurrSession().getLoginUserId());
				}
				insertlog.putFieldValue("mail_from", _fromUser);
				insertlog.putFieldValue("mail_to", tousers_sb.toString());
				insertlog.putFieldValue("mail_cc", ccusers_sb.toString());
				insertlog.putFieldValue("title", _title);
				if (_message.length() > 1500) { //强行设定小了
					_message = _message.substring(0, 1500);
				}
				insertlog.putFieldValue("content", _message);
				insertlog.putFieldValue("createtime", tbutil.getCurrTime());
				dmo.executeUpdateByDS(null, insertlog);
			}
			message.setFrom(ia_from);
			message.setRecipients(Message.RecipientType.TO, ia_tos); //
			message.setSubject(_title); //主题
			message.setText(_message); //内容..
			message.saveChanges(); //先保存下

			if (mailMod == 1 || mailMod == 3) { //发邮件
				Transport transport = session.getTransport("smtp");
				transport.connect();
				transport.send(message);
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	public void sendMail(String _smtpAddr, String _fromUser, String[] _toUsers, String _title, String _message) {
		try {
			Properties props = new Properties();
			props.put("mail.smtp.host", _smtpAddr);
			Session session = Session.getDefaultInstance(props, null);
			Message message = new MimeMessage(session);

			InternetAddress ia_from = new InternetAddress(_fromUser, ServerEnvironment.getProperty("PROJECT_SHORTNAME") + " Sender");
			message.setFrom(ia_from);

			InternetAddress ia_tos[] = new InternetAddress[_toUsers.length];
			for (int i = 0; i < ia_tos.length; i++) {
				ia_tos[i] = new InternetAddress(_toUsers[i], "Receiver"); //
			}

			message.setRecipients(Message.RecipientType.TO, ia_tos); //
			message.setSubject(_title); //主题
			message.setText(_message); //内容..
			message.saveChanges(); //先保存下
			Transport transport = session.getTransport("smtp");
			transport.connect();
			Transport.send(message); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	public void receiveMail(String _popserver, String _user, String _pwd) {
		try {
			//String pop3Host = "pop3.163.com", popUser = "shxch7612", popPassword = "twttawxf"; //指出POP3 服务器的地址,用户名与密码!
			Session session = Session.getDefaultInstance(System.getProperties(), null);
			Store store = session.getStore("pop3");
			try {
				store.connect(_popserver, -1, _user, _pwd);
			} catch (Exception ex) {
				System.out.println("用户名或密码不对!");
				return;//以前执行System.exit(0)后台服务会退出，故直接return即可【李春娟/2016-06-01】
				//System.exit(0);
			}

			// Open the default folder 
			Folder folder = store.getDefaultFolder(); //打开标准的目录
			if (folder == null) {
				throw new NullPointerException("No default mail folder"); //
			}

			folder = folder.getFolder("INBOX");
			if (folder == null) {
				throw new NullPointerException("Unable to get folder: " + folder); //
			} //发现目录但取不下来

			// Get message count 
			folder.open(Folder.READ_WRITE);
			int totalMessages = folder.getMessageCount(); //看目录中总共有几个邮件
			if (totalMessages == 0) //如果总共有0条邮件,即没有邮件
			{
				System.out.println("No messages found in inbox");
				folder.close(false);
				store.close();
				return;
			}

			// Get attributes & flags for all messages 
			Message[] messages = folder.getMessages(); //得到所有的邮件,置入一个 Message 数组中!

			/*
			 FetchProfile fp = new FetchProfile(); 
			 fp.add(FetchProfile.Item.ENVELOPE); 
			 fp.add(FetchProfile.Item.FLAGS); 
			 fp.add("X-Mailer"); 
			 folder.fetch(messages, fp); 
			 */

			// Process each message 
			for (int i = 0; i < messages.length; i++) {
				if (!messages[i].isSet(Flags.Flag.SEEN)) {
					//System.out.println("邮件主题:" + messages[i].getSubject());
					//System.out.println("邮件内容:" + messages[i].getContent()); //
					//process(messages[i]);
				} //依次输出各个邮件的主题,发送日期,大小等信息..

				messages[i].setFlag(Flags.Flag.DELETED, true); //从服务器上删除  
			}

			folder.close(true);
			store.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void demoSend() {
		MailUtil mailUtil = new MailUtil(); //
		String str_title = "weblight测试邮件";
		String str_content = "如果你收到这封邮件,说明weblight平台邮件系统正常!";
		System.out.println("准备发送邮件....");
		//		mailUtil.sendMail("59.60.152.206", "michael.xu@infostrategy.com.cn", new String[] { "lawje.jiang@infostrategy.com.cn" }, str_title, str_content);
		mailUtil.sendMail("email.pushworld.com", "haoming@pushworld.com", "123456", new String[] { "276279308@qq.com" }, str_title, str_content);
		System.out.println("发送邮件成功!!");
	}

	private void demoReceive() {
		MailUtil mailUtil = new MailUtil(); //
		System.out.println("准备接收邮件....");
		mailUtil.receiveMail("192.168.0.10", "test1", "123");
	}

	public static void main(String[] args) {
		new MailUtil().demoSend(); //
		//		new MailUtil().demoReceive(); //
	}

}
