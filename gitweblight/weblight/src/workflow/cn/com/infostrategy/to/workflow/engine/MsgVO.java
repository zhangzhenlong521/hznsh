package cn.com.infostrategy.to.workflow.engine;

import java.io.Serializable;

import cn.com.infostrategy.to.mdata.BillVO;

/**
 * ��ϢVO
 * @author yangke
 * @date 2012-08-15
 */

public class MsgVO  implements Serializable { 
	String msgtype; //��Ϣ����  ϵͳ��Ϣ/������Ϣ
	String receiver; //������  ;1548;1448;
	String recvcorp; //���ջ���  ;2119;2213;
	String recvrole; //���ս�ɫ  ;382;378
	String msgtitle; //����  ����
	String msgcontent; //����  ����
	String msgfile; //����  
	String sender; //������  21
	String sendercorp; //���ͻ���  1994
	String senddate; //����ʱ��  2012-08-10 16:23:51
	String isdelete; //�Ƿ�ɾ��  N
	String state; //״̬
	String functiontype; //��������  sysmsg
	String createtime;
	
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	BillVO billVO; //���̼��ʹ��
	
	public String getMsgtype() {
		return msgtype;
	}
	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public String getRecvcorp() {
		return recvcorp;
	}
	public void setRecvcorp(String recvcorp) {
		this.recvcorp = recvcorp;
	}
	public String getRecvrole() {
		return recvrole;
	}
	public void setRecvrole(String recvrole) {
		this.recvrole = recvrole;
	}
	public String getMsgtitle() {
		return msgtitle;
	}
	public void setMsgtitle(String msgtitle) {
		this.msgtitle = msgtitle;
	}
	public String getMsgcontent() {
		return msgcontent;
	}
	public void setMsgcontent(String msgcontent) {
		this.msgcontent = msgcontent;
	}
	public String getMsgfile() {
		return msgfile;
	}
	public void setMsgfile(String msgfile) {
		this.msgfile = msgfile;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getSendercorp() {
		return sendercorp;
	}
	public void setSendercorp(String sendercorp) {
		this.sendercorp = sendercorp;
	}
	public String getSenddate() {
		return senddate;
	}
	public void setSenddate(String senddate) {
		this.senddate = senddate;
	}
	public String getIsdelete() {
		return isdelete;
	}
	public void setIsdelete(String isdelete) {
		this.isdelete = isdelete;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getFunctiontype() {
		return functiontype;
	}
	public void setFunctiontype(String functiontype) {
		this.functiontype = functiontype;
	}
	public BillVO getBillVO() {
		return billVO;
	}
	public void setBillVO(BillVO billVO) {
		this.billVO = billVO;
	}

}
