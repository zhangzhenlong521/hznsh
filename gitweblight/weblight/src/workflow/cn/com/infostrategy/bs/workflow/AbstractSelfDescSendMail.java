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
	 * @param billvo ģ���������
	 * @param option ���̵Ĵ������
	 * @param sender ϵͳ��¼�ˣ������ˣ�
	 * @param v_sendmail v_sendmail.add(new String[] { str_usermailaddr, str_mailsubject, str_mailcontent }); //��ĳ����,��ʲô�ʼ�(��ַ�����⣬����)
	 */
	public  abstract void sendMail(BillVO billvo,String option ,String sender ,Vector v_sendmail);
}
