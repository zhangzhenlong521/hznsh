package cn.com.infostrategy.bs.sysapp.cometpush;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import cn.com.infostrategy.to.common.LongPushParVO;
import cn.com.infostrategy.to.common.WLTRemoteException;

/**
 * ��������������Ϣ�����ࡣ
 * �Լ��ڿͻ���дһ���ӿڡ�Ȼ��ʵ�ֽӿ�,ƽ̨�ṩ��ServerPushToClientIFC�ӿ�.
 * ��������ֱ�Ӵ������.
 * @author haoming
 * create by 2013-12-2
 */
public class ServerPushToClientUtil implements Serializable {
	private static final long serialVersionUID = 2236671597950267123L;
	public static final String PUSH_TYPE_ONLINE = "ONLINE";
	public static final String PUSH_TYPE_EXCEPT_ME = "EM"; //�����Լ�
	public static final String PUSH_TYPE_BY_SESSION = "SESSION"; //ͨ���Է�session��
	public static final String PUSH_TYPE_BY_CODE = "CODE"; //ͨ���Է�session��

	public static Object lookupClient(Class _class, PushConfigVO configvo) {
		Object serviceobj = Proxy.newProxyInstance(_class.getClassLoader(), _class.getInterfaces(), new RemoteCallClientHandler(_class, configvo));
		return serviceobj;
	}

	private static class RemoteCallClientHandler implements InvocationHandler {
		private Class serviceName = null;
		private PushConfigVO configvo;

		public RemoteCallClientHandler(Class _serviceName, PushConfigVO _configvo) {
			this.serviceName = _serviceName; //Զ�̷�����
			configvo = _configvo;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws WLTRemoteException, Throwable {
			Class[] pars_class = method.getParameterTypes(); //
			LongPushParVO pushvo = new LongPushParVO(serviceName, method, pars_class, args);
			byte b[] = serialize(pushvo);
			byte endb[] = "$end$".getBytes(); //�ӽ�����ʶ��
			byte[] data3 = new byte[b.length + endb.length];
			System.arraycopy(b, 0, data3, 0, b.length);
			System.arraycopy(endb, 0, data3, b.length, endb.length);
			configvo.setMessage(data3);
			ServerPushToClientServlet.send(configvo);
			return ""; //
		}
	}

	/**
	 * ���л�һ������..
	 * @param _obj
	 * @return
	 */
	private static synchronized byte[] serialize(Object _obj) {
		ByteArrayOutputStream buf = null; //
		ObjectOutputStream out = null; //
		try {
			buf = new ByteArrayOutputStream();
			out = new ObjectOutputStream(buf);
			out.writeObject(_obj);
			byte[] bytes = buf.toByteArray();
			return bytes; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null; //
		} finally {
			try {
				out.close(); //
			} catch (Exception e) {
			}
		}
	}
}
