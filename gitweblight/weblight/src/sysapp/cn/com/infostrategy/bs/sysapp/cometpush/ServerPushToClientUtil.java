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
 * 服务器端推送消息工具类。
 * 自己在客户端写一个接口。然后实现接口,平台提供了ServerPushToClientIFC接口.
 * 服务器端直接代理该类.
 * @author haoming
 * create by 2013-12-2
 */
public class ServerPushToClientUtil implements Serializable {
	private static final long serialVersionUID = 2236671597950267123L;
	public static final String PUSH_TYPE_ONLINE = "ONLINE";
	public static final String PUSH_TYPE_EXCEPT_ME = "EM"; //除了自己
	public static final String PUSH_TYPE_BY_SESSION = "SESSION"; //通过对方session。
	public static final String PUSH_TYPE_BY_CODE = "CODE"; //通过对方session。

	public static Object lookupClient(Class _class, PushConfigVO configvo) {
		Object serviceobj = Proxy.newProxyInstance(_class.getClassLoader(), _class.getInterfaces(), new RemoteCallClientHandler(_class, configvo));
		return serviceobj;
	}

	private static class RemoteCallClientHandler implements InvocationHandler {
		private Class serviceName = null;
		private PushConfigVO configvo;

		public RemoteCallClientHandler(Class _serviceName, PushConfigVO _configvo) {
			this.serviceName = _serviceName; //远程服务名
			configvo = _configvo;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws WLTRemoteException, Throwable {
			Class[] pars_class = method.getParameterTypes(); //
			LongPushParVO pushvo = new LongPushParVO(serviceName, method, pars_class, args);
			byte b[] = serialize(pushvo);
			byte endb[] = "$end$".getBytes(); //加结束标识。
			byte[] data3 = new byte[b.length + endb.length];
			System.arraycopy(b, 0, data3, 0, b.length);
			System.arraycopy(endb, 0, data3, b.length, endb.length);
			configvo.setMessage(data3);
			ServerPushToClientServlet.send(configvo);
			return ""; //
		}
	}

	/**
	 * 序列化一个对象..
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
