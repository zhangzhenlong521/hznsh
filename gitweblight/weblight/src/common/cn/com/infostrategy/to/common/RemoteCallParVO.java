/**************************************************************************
 * $RCSfile: RemoteCallParVO.java,v $  $Revision: 1.9 $  $Date: 2012/09/14 09:17:31 $
 **************************************************************************/
package cn.com.infostrategy.to.common;

import java.io.Serializable;

public class RemoteCallParVO implements Serializable {

	private static final long serialVersionUID = 8108429561075237846L;

	private String clientIP = null;
	private String serviceName = null; //
	private String methodName = null; //

	private Class[] parClasses = null;
	private Object[] parObjs = null;

	private CurrSessionVO currSessionVO = null; //当前会话VO
	private String currVersion = null; //当前版本
	private String currCallMenuName = null; //当前调用的菜单名称!用于性能跟踪定位!!
	private String currThreadCallStack = null; //当前线程调用堆栈!用于性能跟踪定位!!

	private int connTime = 0; //创建连接握手的时间!!! 高阳总在美国访问时速度很慢,怀疑是这个点的问题!!即路由太多了!!!

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Class[] getParClasses() {
		return parClasses;
	}

	public void setParClasses(Class[] parClasses) {
		this.parClasses = parClasses;
	}

	public Object[] getParObjs() {
		return parObjs;
	}

	public void setParObjs(Object[] parObjs) {
		this.parObjs = parObjs;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getClientIP() {
		return clientIP;
	}

	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}

	/**
	 * 取得当前会话VO
	 * @return
	 */
	public CurrSessionVO getCurrSessionVO() {
		return currSessionVO;
	}

	/**
	 * 设置当前会话VO
	 * @param currSessionVO
	 */
	public void setCurrSessionVO(CurrSessionVO currSessionVO) {
		this.currSessionVO = currSessionVO;
	}

	public String getCurrVersion() {
		return currVersion;
	}

	public void setCurrVersion(String currVersion) {
		this.currVersion = currVersion;
	}

	public String getCurrCallMenuName() {
		return currCallMenuName;
	}

	public void setCurrCallMenuName(String currCallMenuName) {
		this.currCallMenuName = currCallMenuName;
	}

	public String getCurrThreadCallStack() {
		return currThreadCallStack;
	}

	public void setCurrThreadCallStack(String currThreadCallStack) {
		this.currThreadCallStack = currThreadCallStack;
	}

	public int getConnTime() {
		return connTime;
	}

	public void setConnTime(int connTime) {
		this.connTime = connTime;
	}

}

/**************************************************************************
 * $RCSfile: RemoteCallParVO.java,v $  $Revision: 1.9 $  $Date: 2012/09/14 09:17:31 $
 *
 * $Log: RemoteCallParVO.java,v $
 * Revision 1.9  2012/09/14 09:17:31  xch123
 * *** empty log message ***
 *
 * Revision 1.1  2012/08/28 09:40:49  Administrator
 * *** empty log message ***
 *
 * Revision 1.8  2011/10/10 06:31:36  wanggang
 * restore
 *
 * Revision 1.6  2011/06/27 10:32:47  xch123
 * *** empty log message ***
 *
 * Revision 1.5  2010/12/28 10:29:11  xch123
 * 12月28日提交
 *
 * Revision 1.4  2010/11/09 11:11:29  xch123
 * *** empty log message ***
 *
 * Revision 1.3  2010/11/09 10:42:39  xch123
 * *** empty log message ***
 *
 * Revision 1.2  2010/05/26 10:17:00  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/17 10:23:00  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:31:49  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:32:48  xuchanghua
 * *** empty log message ***
 *
 *
 **************************************************************************/
