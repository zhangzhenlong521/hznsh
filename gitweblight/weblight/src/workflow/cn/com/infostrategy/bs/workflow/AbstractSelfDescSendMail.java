package cn.com.infostrategy.bs.workflow;

import java.util.Vector;

import cn.com.infostrategy.to.mdata.BillVO;
/**
 * 
 * @author sfj
 *
 */
public abstract class AbstractSelfDescSendMail {
	/**
	 * 
	 * @param billvo 模板表单的数据
	 * @param option 流程的处理意见
	 * @param sender 系统登录人（发送人）
	 * @param v_sendmail v_sendmail.add(new String[] { str_usermailaddr, str_mailsubject, str_mailcontent }); //给某个人,发什么邮件(地址，主题，内容)
	 */
	public  abstract void sendMail(BillVO billvo,String option ,String sender ,Vector v_sendmail);
}
