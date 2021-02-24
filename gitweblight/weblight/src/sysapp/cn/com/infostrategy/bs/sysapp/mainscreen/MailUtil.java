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
 * �ʼ�������,ʹ��Jmail���߰�,ʵ�ַ���������ʼ�����!
 * ֻ֧��smtp��pop3Э��! ��֧��΢��� Exchange�ʼ�Э��!!
 * @author user
 *
 */
public class MailUtil {
	private CommDMO dmo = new CommDMO();
	private TBUtil tbutil = new TBUtil();

	/**
	 * �����ʼ�..
	 * @param _smtpAddr,smtp��������ַ,����:smtp.163.com
	 * @param _fromUser,�������ʼ���ַ
	 * @param _toUsers,�ռ��˵�ַ,������һ���б�
	 */
	public void sendMail(String _smtpAddr, String _fromUser, String psw, String[] _toUsers, String _title, String _message) {
		sendMail(_smtpAddr, _fromUser, psw, _toUsers, null, _title, _message);
	}

	/**
	 * �����ʼ�..
	 * @param _smtpAddr,smtp��������ַ,����:smtp.163.com
	 * @param _fromUser,�������ʼ���ַ
	 * @param _toUsers,�ռ��˵�ַ,������һ���б�
	 * @param _ccUsers,�����ˡ�
	 */
	public void sendMail(String _smtpAddr, String _fromUser, String psw, String[] _toUsers, String[] _ccUsers, String _title, String _message) {
		try {
			if ((_toUsers == null || _toUsers.length == 0) && (_ccUsers == null || _ccUsers.length == 0)) {
				System.out.println(">>>û���ռ���" + _title);
				return;
			}
			//�ʼ����ͷ�ʽ��0-ʲô��û�У�1-ֻ���ʼ���2-ֻ����־��3-�ʼ�����־���� Gwang 2016-05-23
			int mailMod = TBUtil.getTBUtil().getSysOptionIntegerValue("�ʼ����ͷ�ʽ", 3);
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
			System.out.println(">>>�����ʼ���username=" + username + ",pwd=" + pass);
			Session session = Session.getDefaultInstance(props, new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, pass);
				}
			});
			Message message = new MimeMessage(session);
			InternetAddress ia_from = new InternetAddress(_fromUser, ServerEnvironment.getProperty("PROJECT_NAME_CN") + " ����Ա");
			StringBuffer ccusers_sb = new StringBuffer();
			if (_ccUsers != null && _ccUsers.length > 0) {
				InternetAddress ia_ccs[] = new InternetAddress[_ccUsers.length];
				for (int i = 0; i < ia_ccs.length; i++) {
					ia_ccs[i] = new InternetAddress(_ccUsers[i], _ccUsers[i]);
					ccusers_sb.append(_ccUsers[i] + ";");
				}
				message.setRecipients(Message.RecipientType.CC, ia_ccs);//��ӳ�����
			}

			if (mailMod == 2 || mailMod == 3) { //�ȴ���־�����ʼ������/2016-12-14��
				InsertSQLBuilder insertlog = new InsertSQLBuilder("pub_mail_log");
				insertlog.putFieldValue("id", dmo.getSequenceNextValByDS(null, "S_pub_mail_log"));
				CurrSessionVO sessionvo = dmo.getCurrSession();
				if (sessionvo != null) {//debugʱ�ᱨ��ָ���쳣�����ж�һ�¡����/2016-11-21��
					insertlog.putFieldValue("createuser", dmo.getCurrSession().getLoginUserId());
				}
				insertlog.putFieldValue("mail_from", _fromUser);
				insertlog.putFieldValue("mail_to", tousers_sb.toString());
				insertlog.putFieldValue("mail_cc", ccusers_sb.toString());
				insertlog.putFieldValue("title", _title);
				if (_message.length() > 1500) { //ǿ���趨С��
					_message = _message.substring(0, 1500);
				}
				insertlog.putFieldValue("content", _message);
				insertlog.putFieldValue("createtime", tbutil.getCurrTime());
				dmo.executeUpdateByDS(null, insertlog);
			}
			message.setFrom(ia_from);
			message.setRecipients(Message.RecipientType.TO, ia_tos); //
			message.setSubject(_title); //����
			message.setText(_message); //����..
			message.saveChanges(); //�ȱ�����

			if (mailMod == 1 || mailMod == 3) { //���ʼ�
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
			message.setSubject(_title); //����
			message.setText(_message); //����..
			message.saveChanges(); //�ȱ�����
			Transport transport = session.getTransport("smtp");
			transport.connect();
			Transport.send(message); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	public void receiveMail(String _popserver, String _user, String _pwd) {
		try {
			//String pop3Host = "pop3.163.com", popUser = "shxch7612", popPassword = "twttawxf"; //ָ��POP3 �������ĵ�ַ,�û���������!
			Session session = Session.getDefaultInstance(System.getProperties(), null);
			Store store = session.getStore("pop3");
			try {
				store.connect(_popserver, -1, _user, _pwd);
			} catch (Exception ex) {
				System.out.println("�û��������벻��!");
				return;//��ǰִ��System.exit(0)��̨������˳�����ֱ��return���ɡ����/2016-06-01��
				//System.exit(0);
			}

			// Open the default folder 
			Folder folder = store.getDefaultFolder(); //�򿪱�׼��Ŀ¼
			if (folder == null) {
				throw new NullPointerException("No default mail folder"); //
			}

			folder = folder.getFolder("INBOX");
			if (folder == null) {
				throw new NullPointerException("Unable to get folder: " + folder); //
			} //����Ŀ¼��ȡ������

			// Get message count 
			folder.open(Folder.READ_WRITE);
			int totalMessages = folder.getMessageCount(); //��Ŀ¼���ܹ��м����ʼ�
			if (totalMessages == 0) //����ܹ���0���ʼ�,��û���ʼ�
			{
				System.out.println("No messages found in inbox");
				folder.close(false);
				store.close();
				return;
			}

			// Get attributes & flags for all messages 
			Message[] messages = folder.getMessages(); //�õ����е��ʼ�,����һ�� Message ������!

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
					//System.out.println("�ʼ�����:" + messages[i].getSubject());
					//System.out.println("�ʼ�����:" + messages[i].getContent()); //
					//process(messages[i]);
				} //������������ʼ�������,��������,��С����Ϣ..

				messages[i].setFlag(Flags.Flag.DELETED, true); //�ӷ�������ɾ��  
			}

			folder.close(true);
			store.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void demoSend() {
		MailUtil mailUtil = new MailUtil(); //
		String str_title = "weblight�����ʼ�";
		String str_content = "������յ�����ʼ�,˵��weblightƽ̨�ʼ�ϵͳ����!";
		System.out.println("׼�������ʼ�....");
		//		mailUtil.sendMail("59.60.152.206", "michael.xu@infostrategy.com.cn", new String[] { "lawje.jiang@infostrategy.com.cn" }, str_title, str_content);
		mailUtil.sendMail("email.pushworld.com", "haoming@pushworld.com", "123456", new String[] { "276279308@qq.com" }, str_title, str_content);
		System.out.println("�����ʼ��ɹ�!!");
	}

	private void demoReceive() {
		MailUtil mailUtil = new MailUtil(); //
		System.out.println("׼�������ʼ�....");
		mailUtil.receiveMail("192.168.0.10", "test1", "123");
	}

	public static void main(String[] args) {
		new MailUtil().demoSend(); //
		//		new MailUtil().demoReceive(); //
	}

}
