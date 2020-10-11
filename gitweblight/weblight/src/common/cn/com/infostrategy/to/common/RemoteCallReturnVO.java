/**************************************************************************
 * $RCSfile: RemoteCallReturnVO.java,v $  $Revision: 1.9 $  $Date: 2012/09/14 09:17:31 $
 **************************************************************************/
package cn.com.infostrategy.to.common;

import java.io.Serializable;

public class RemoteCallReturnVO implements Serializable {

	private static final long serialVersionUID = 7116778489819827914L;

	private String serviceName = null; //
	private String serviceImplName = null; //

	private Object returnObject = null;
	private long dealtime = 0; //
	private int callDBCount = 0;
	private String sessionID = null; //会话的唯一性标识,解决一个用户在两台机器上访问,其Session是不一样的!

	private long jVM1 = 0; //访问前的JVM耗用!因为后来经常发生内存溢出导致系统死机制问题,所以虚拟机的内存开销成了一个关键指标!!!甚至需要客户端也要知道!!!而且要在客户端的日志中也要看到!!
	private long jVM2 = 0; //访问后的JVM耗时!
	
	private String callTrackMsg = null;  //此次调用的日志!

	public Object getReturnObject() {
		return returnObject;
	}

	public void setReturnObject(Object returnObject) {
		this.returnObject = returnObject;
	}

	public String getServiceImplName() {
		return serviceImplName;
	}

	public void setServiceImplName(String serviceImplName) {
		this.serviceImplName = serviceImplName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public long getDealtime() {
		return dealtime;
	}

	public void setDealtime(long dealtime) {
		this.dealtime = dealtime;
	}

	public int getCallDBCount() {
		return callDBCount;
	}

	public void setCallDBCount(int callDBCount) {
		this.callDBCount = callDBCount;
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public long getJVM1() {
		return jVM1;
	}

	public void setJVM1(long jvm1) {
		jVM1 = jvm1;
	}

	public long getJVM2() {
		return jVM2;
	}

	public void setJVM2(long jvm2) {
		jVM2 = jvm2;
	}

	public String getCallTrackMsg() {
		return callTrackMsg;
	}

	public void setCallTrackMsg(String _callTrackMsg) {
		this.callTrackMsg = _callTrackMsg;
	}

}
