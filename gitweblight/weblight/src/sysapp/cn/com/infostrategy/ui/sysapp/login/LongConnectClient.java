package cn.com.infostrategy.ui.sysapp.login;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import cn.com.infostrategy.to.common.LongPushParVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;

public class LongConnectClient {
	private static LongConnectClient connect;

	private LongConnectClient() {

	}

	public synchronized final static LongConnectClient initialize() {
		new Timer().schedule(new TimerTask() {
			public void run() {
				if (connect == null) {
					connect = new LongConnectClient();
					HttpClient httpClient = new HttpClient();
					String str_url = System.getProperty("CALLURL") + "/push";
					//����GET������ʵ��
					GetMethod getMethod = new GetMethod(str_url + "?session=" + ClientEnvironment.getCurrSessionVO().getHttpsessionid());
					getMethod.setRequestHeader("Content-type", "application/x-compress");
					getMethod.setRequestHeader("Connection", "Keep-Alive");
					//ʹ��ϵͳ�ṩ��Ĭ�ϵĻָ�����
					getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
					try {
						//ִ��getMethod
						int statusCode = httpClient.executeMethod(getMethod);
						if (statusCode != HttpStatus.SC_OK) {
							System.err.println("Method failed: " + getMethod.getStatusLine());
						}
						//��ȡ���� 
						InputStream inputStream = getMethod.getResponseBodyAsStream();
						ByteArrayOutputStream baos = null;
						try {
							baos = new ByteArrayOutputStream();
							byte[] buf = new byte[2048]; //����ʱ��Ҫ��!���ȷ��������µ�Ҫ��һ��ź�,��������Ƿ��������µĿ���ͻ��˳����,����ܳ��������벢�������������!!
							int len = -1;
							while ((len = inputStream.read(buf)) != -1) {
								baos.write(buf, 0, len);
								byte[] b = baos.toByteArray();
								if (b.length >= 5) {
									int length = b.length;
									byte lastbytes[] = new byte[5];
									System.arraycopy(b, length - 5, lastbytes, 0, lastbytes.length);
									String last = new String(lastbytes);
									if ("$end$".equals(last)) {
										byte[] byteCodes = baos.toByteArray();
										Object obj = deserialize(byteCodes);
										if (obj instanceof LongPushParVO) {
											LongPushParVO parvo = (LongPushParVO) obj;
											String classname = parvo.getClassName();
											String method = parvo.getMothodName();
											Class cls = Class.forName(classname);
											if (cls != null) {
												Object oo = cls.newInstance();
												oo.getClass().getMethods();
												Method mtehod = oo.getClass().getMethod(method, parvo.getParClasses());
												Object rtvo = mtehod.invoke(oo, parvo.getParObjs()); //���ö�Ӧ��������ֵ!!!
											}
										}
										baos = new ByteArrayOutputStream();
									}
								}
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}

					} catch (HttpException e) {
						//�����������쳣��������Э�鲻�Ի��߷��ص�����������
						System.out.println("Please check your provided http address!");
						e.printStackTrace();
					} catch (IOException e) {
						//���������쳣
						System.out.println(">>//���������쳣");
						e.printStackTrace();
					} finally {
						//�ͷ�����
						getMethod.releaseConnection();
					}
				}
			}
		}, 0);
		return connect;
	}

	/**
	 * �������л���һ������
	 * @return
	 */
	private static Object deserialize(byte[] _bytes) {
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new ByteArrayInputStream(_bytes));
			//			in = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(_bytes)));
			Object obj = in.readObject();
			in.close();
			return obj; //
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			try {
				in.close(); //
			} catch (Exception ex) {
			}
		}
	}
}
