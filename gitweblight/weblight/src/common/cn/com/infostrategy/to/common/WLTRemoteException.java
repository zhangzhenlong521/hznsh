/**************************************************************************
 * $RCSfile: WLTRemoteException.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:17:30 $
 **************************************************************************/
package cn.com.infostrategy.to.common;

import java.rmi.RemoteException;

public class WLTRemoteException extends RemoteException {

	private static final long serialVersionUID = -1357463874090659084L;

	private Throwable serverTargetEx = null;

	public WLTRemoteException(String _message) {
		super(_message);
	}

	public String getClientStackDetail() {
		String str_mess = ""; //
		StackTraceElement[] stackItems = this.getStackTrace();
		for (int i = 0; i < stackItems.length; i++) {
			str_mess = str_mess + "\t" + stackItems[i] + "\r\n"; //把实际堆栈搞进来!
		}
		return str_mess;
	}

	public String getServerStackDetail() {
		String str_mess = ""; //
		if (getServerTargetEx() != null) {
			StackTraceElement[] stackItems = getServerTargetEx().getStackTrace();
			for (int i = 0; i < stackItems.length; i++) {
				str_mess = str_mess + "\t" + stackItems[i] + "\r\n"; //把实际堆栈搞进来!
			}
		}
		return str_mess;
	}

	public Throwable getServerTargetEx() {
		return serverTargetEx;
	}

	public void setServerTargetEx(Throwable serverTargetEx) {
		this.serverTargetEx = serverTargetEx;
	}

	@Override
	public void printStackTrace() {
		System.err.println("发生远程调用异常,客户端堆栈：");
		super.printStackTrace(); //
		if (getServerTargetEx() != null) {
			String str_serverStack = getServerStackDetail(); //
			if (str_serverStack != null && !str_serverStack.equals("")) {
				System.err.println("该远程异常实际原因的服务器端堆栈：\r\n" + getServerTargetEx().getClass().getName() + ": " + getServerTargetEx().getMessage() + "\r\n" + str_serverStack); //
			}
		}
	}

}
/**************************************************************************
 * $RCSfile: WLTRemoteException.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:17:30 $
 *
 * $Log: WLTRemoteException.java,v $
 * Revision 1.4  2012/09/14 09:17:30  xch123
 * *** empty log message ***
 *
 * Revision 1.1  2012/08/28 09:40:49  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:36  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:23:00  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:31:49  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:32:49  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:01:56  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:42  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.5  2009/10/14 04:09:44  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.4  2009/10/14 03:59:39  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2009/10/14 03:54:44  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2009/10/14 03:47:59  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:16  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/28 06:17:38  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:09  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:14  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:08  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:27  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/03/07 08:36:38  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/03/07 08:32:20  shxch
 * *** empty log message ***
 *
 * Revision 1.1  2007/03/07 02:01:56  shxch
 * *** empty log message ***
 *
 * Revision 1.5  2007/02/10 08:51:58  shxch
 * *** empty log message ***
 *
 * Revision 1.4  2007/02/10 08:33:32  shxch
 * *** empty log message ***
 *
 * Revision 1.3  2007/02/10 07:13:05  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 03:38:02  lujian
 * *** empty log message ***
 *
 *
 **************************************************************************/
