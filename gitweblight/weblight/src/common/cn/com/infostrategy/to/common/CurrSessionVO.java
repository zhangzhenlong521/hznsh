package cn.com.infostrategy.to.common;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 当前远程访问会话的VO,即每次远程访问时都将带到服务器端!在服务器端由new WLTInitContext().getCurrSessionVO()可以得到这个
 * 该VO中除了包括登录用户的信息VO外,还包括登录日期,登录语言,登录模式等,即一般都是登录页面上带入的一些信息!
 * 为了提高性能,这里只存储4个值,以后增加也一定要慎重!
 * 之所以每次都要传这个值,是因为有时在服务器端某一个类的某一个方法中突然需要用到登录人员的主键或登录日期,但这个方法中又没有传入这个,这里就需要修改方法的入参,而该方法
 * 又是被别的方法调用的,而这一层一层的调用层次非常深,修改起来工作量非常巨大!有的时候根本就没法改!!所以需要这个类,一下子取得当前登录用户的数据!
 * 有时也有突然需要传入一个自定义的对象,而同样遇到不方便修改原有的类方法定义,这时可以通过该类的一个HashMap传入!换句话说就是为远程调用又提供了另一种通道!!!但一定要记住在客户端调用结束后一定要清空掉!!
 * 还有一个问题是,有时可能需要在加载公式中需要登录人员的角色等,这里就没法取到了!所以以后避免这种需求发生!!!
 * @author xch
 *
 */
public class CurrSessionVO implements Serializable {

	private static final long serialVersionUID = 4794671097110401213L;

	private String httpsessionid = null; //唯一标识该客户端进程的sessionid,之所以传入这个,是因为有时在某些方法里需要这个!比如CommDMO中需要做SQL监控!!
	private String clientIP1 = null; //客户端的IP1
	private String clientIP2 = null; //客户端的IP2
	private String loginUserId = null; //登录人员主键
	private String loginUserCode = null; //登录人员工号
	private String loginUserName = null; //登录人员名称
	private String loginDate = null; //
	private String sessionCallInfo = null; //具体调用的说明,即将 
	private long registeTime = -1; //注册时间,就在注册进服务器端缓存的时间!!!
	private boolean isLRCall = false; //是否是LR的调用!!
	private String loginUserPKDept = null; //登录人员部门ID pub_user中的PK_DEPT

	private HashMap custMap = null; //自定义Map,有时突然需要自定义传入一个参数,而又不希望修改原来方法定义!但一定要注意在客户端调用结束后清空该参数!比如调用的数据大小! key=取数量,value=[sql][大小]

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
			custMap = new HashMap(); //如果没有,则创建,从而极大减少大部分情况的网络传输!
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
