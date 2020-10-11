package cn.com.infostrategy.to.common;

import java.io.Serializable;
import java.util.HashMap;

/**
 * ��ǰԶ�̷��ʻỰ��VO,��ÿ��Զ�̷���ʱ����������������!�ڷ���������new WLTInitContext().getCurrSessionVO()���Եõ����
 * ��VO�г��˰�����¼�û�����ϢVO��,��������¼����,��¼����,��¼ģʽ��,��һ�㶼�ǵ�¼ҳ���ϴ����һЩ��Ϣ!
 * Ϊ���������,����ֻ�洢4��ֵ,�Ժ�����Ҳһ��Ҫ����!
 * ֮����ÿ�ζ�Ҫ�����ֵ,����Ϊ��ʱ�ڷ�������ĳһ�����ĳһ��������ͻȻ��Ҫ�õ���¼��Ա���������¼����,�������������û�д������,�������Ҫ�޸ķ��������,���÷���
 * ���Ǳ���ķ������õ�,����һ��һ��ĵ��ò�ηǳ���,�޸������������ǳ��޴�!�е�ʱ�������û����!!������Ҫ�����,һ����ȡ�õ�ǰ��¼�û�������!
 * ��ʱҲ��ͻȻ��Ҫ����һ���Զ���Ķ���,��ͬ�������������޸�ԭ�е��෽������,��ʱ����ͨ�������һ��HashMap����!���仰˵����ΪԶ�̵������ṩ����һ��ͨ��!!!��һ��Ҫ��ס�ڿͻ��˵��ý�����һ��Ҫ��յ�!!
 * ����һ��������,��ʱ������Ҫ�ڼ��ع�ʽ����Ҫ��¼��Ա�Ľ�ɫ��,�����û��ȡ����!�����Ժ��������������!!!
 * @author xch
 *
 */
public class CurrSessionVO implements Serializable {

	private static final long serialVersionUID = 4794671097110401213L;

	private String httpsessionid = null; //Ψһ��ʶ�ÿͻ��˽��̵�sessionid,֮���Դ������,����Ϊ��ʱ��ĳЩ��������Ҫ���!����CommDMO����Ҫ��SQL���!!
	private String clientIP1 = null; //�ͻ��˵�IP1
	private String clientIP2 = null; //�ͻ��˵�IP2
	private String loginUserId = null; //��¼��Ա����
	private String loginUserCode = null; //��¼��Ա����
	private String loginUserName = null; //��¼��Ա����
	private String loginDate = null; //
	private String sessionCallInfo = null; //������õ�˵��,���� 
	private long registeTime = -1; //ע��ʱ��,����ע����������˻����ʱ��!!!
	private boolean isLRCall = false; //�Ƿ���LR�ĵ���!!
	private String loginUserPKDept = null; //��¼��Ա����ID pub_user�е�PK_DEPT

	private HashMap custMap = null; //�Զ���Map,��ʱͻȻ��Ҫ�Զ��崫��һ������,���ֲ�ϣ���޸�ԭ����������!��һ��Ҫע���ڿͻ��˵��ý�������ոò���!������õ����ݴ�С! key=ȡ����,value=[sql][��С]

	public String getHttpsessionid() {
		return httpsessionid;
	}

	public void setHttpsessionid(String httpsessionid) {
		this.httpsessionid = httpsessionid;
	}

	public String getClientIP1() {
		return clientIP1;
	}

	public void setClientIP1(String clientIP1) {
		this.clientIP1 = clientIP1;
	}

	public String getClientIP2() {
		return clientIP2;
	}

	public void setClientIP2(String clientIP2) {
		this.clientIP2 = clientIP2;
	}

	public String getLoginUserId() {
		return loginUserId;
	}

	public void setLoginUserId(String loginUserId) {
		this.loginUserId = loginUserId;
	}

	public String getLoginUserCode() {
		return loginUserCode;
	}

	public void setLoginUserCode(String loginUserCode) {
		this.loginUserCode = loginUserCode;
	}

	public String getLoginUserName() {
		return loginUserName;
	}

	public void setLoginUserName(String loginUserName) {
		this.loginUserName = loginUserName;
	}

	public String getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(String loginDate) {
		this.loginDate = loginDate;
	}

	public String getSessionCallInfo() {
		return sessionCallInfo;
	}

	public void setSessionCallInfo(String sessionCallInfo) {
		this.sessionCallInfo = sessionCallInfo;
	}

	public long getRegisteTime() {
		return registeTime;
	}

	public void setRegisteTime(long registeTime) {
		this.registeTime = registeTime;
	}

	public boolean isLRCall() {
		return isLRCall;
	}

	public void setLRCall(boolean isLRCall) {
		this.isLRCall = isLRCall;
	}

	public HashMap getCustMap() {
		if (custMap == null) {
			custMap = new HashMap(); //���û��,�򴴽�,�Ӷ�������ٴ󲿷���������紫��!
		}
		return custMap;
	}

	/**
	 * @return the loginUserPKDept
	 */
	public String getLoginUserPKDept() {
		return loginUserPKDept;
	}

	/**
	 * @param loginUserPKDept the loginUserPKDept to set
	 */
	public void setLoginUserPKDept(String loginUserPKDept) {
		this.loginUserPKDept = loginUserPKDept;
	}
}
