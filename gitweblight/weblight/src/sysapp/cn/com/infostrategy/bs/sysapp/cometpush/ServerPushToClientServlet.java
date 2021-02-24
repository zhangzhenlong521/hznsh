package cn.com.infostrategy.bs.sysapp.cometpush;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.CometEvent;
import org.apache.catalina.CometProcessor;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.ui.common.ServerPushToClientIFC;

public class ServerPushToClientServlet extends HttpServlet implements CometProcessor {

	private static final long serialVersionUID = -3667180332947986301L;

	private CommDMO commdmo = new CommDMO();
	// <�û�,������>
	protected static Map<String, HttpServletResponse> connections = new HashMap<String, HttpServletResponse>();
	protected static HashMap<String, HashSet> user_sessions = new HashMap<String, HashSet>(); //key=code,value=hashset

	// ��Ϣ�����߳�
	protected static MessageSender messageSender = null;

	public void init() throws ServletException {
		// ������Ϣ�����߳�
		messageSender = new MessageSender();
		//Thread messageSenderThread = new Thread(messageSender, "MessageSender[" + getServletContext().getContextPath() + "]");
		Thread messageSenderThread = new Thread(messageSender, "MessageSender");
		messageSenderThread.setDaemon(true);
		messageSenderThread.start();
	}

	public void destroy() {
		connections.clear();
		messageSender.stop();
		user_sessions.clear();
		messageSender = null;
	}

	public void event(CometEvent event) throws IOException, ServletException {
		HttpServletRequest request = event.getHttpServletRequest();
		HttpServletResponse response = event.getHttpServletResponse();
		// �ǳ�
		String session = request.getParameter("session"); //
		if (session == null) {
			event.close();
			return;
		}
		WLTInitContext initContext = new WLTInitContext(); //
		try {
			if (event.getEventType() == CometEvent.EventType.BEGIN) {
				HashMap mapUser = ServerEnvironment.getInstance().getLoginUserMap();
				HashVO uservo = null;
				if (mapUser.containsKey(session)) {
					String[] userconfig = (String[]) mapUser.get(session);
					String usercode = userconfig[3];
					try {
						HashVO[] vo = commdmo.getHashVoArrayByDS(null, "select id,name,code from pub_user where code='" + usercode + "'");
						if (vo.length > 0) {
							uservo = vo[0];
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (uservo == null) {
					//					event.close();
					return;
				}
				// Http���ӿ��г�ʱ
				event.setTimeout(Integer.MAX_VALUE);
				log("Begin for session: " + request.getSession(true).getId() + " ƽ̨session��" + session);
				// ����Comet Iframe
				// ֪ͨ�����û������û���½
				if (!connections.containsKey(session)) {
					messageSender.login(session, uservo.getStringValue("name", ""));
				}
				// �����Ѿ���½���û���Ϣ
				synchronized (connections) {
					synchronized (user_sessions) {
						connections.put(session, response);
						String code = uservo.getStringValue("code", "");
						if (user_sessions.containsKey(code)) {
							HashSet user_sessionMap = user_sessions.get(code); //ͬһ���˲�ͬIP��¼
							user_sessionMap.add(session);
						} else {
							HashSet user_sessionMap = new HashSet();
							user_sessionMap.add(session);
							user_sessions.put(code, user_sessionMap);
						}
					}
				}
			} else if (event.getEventType() == CometEvent.EventType.ERROR) {
				log("��������:session: " + session);
				synchronized (connections) {
					connections.remove(session);
				}
				event.close();
			} else if (event.getEventType() == CometEvent.EventType.END) {
				log("�û�����:session: " + request.getSession(true).getId());
				messageSender.logout(session);
				synchronized (connections) {
					synchronized (user_sessions) {
						connections.remove(session);
						HashMap mapUser = ServerEnvironment.getInstance().getLoginUserMap();
						HashVO uservo = null;
						if (mapUser.containsKey(session)) {
							String[] userconfig = (String[]) mapUser.get(session);
							String usercode = userconfig[3];
							if (user_sessions.containsKey(usercode)) {
								HashSet sessionmap = user_sessions.get(usercode);
								synchronized (sessionmap) {
									sessionmap.remove(session);
									messageSender.logout(session);
								}
							}
						}
					}
				}
				event.close();
			} else if (event.getEventType() == CometEvent.EventType.READ) {
				InputStream is = request.getInputStream();
				byte[] buf = new byte[512];
				do {
					int n = is.read(buf); // can throw an IOException
					if (n > 0) {
						log("Read " + n + " bytes: " + new String(buf, 0, n) + " for session: " + request.getSession(true).getId());
					} else if (n < 0) {
						return;
					}
				} while (is.available() > 0);
			}
		} catch (Exception ex) {
			event.close();
			ex.printStackTrace();
		} finally {
			initContext.closeAllConn();
			initContext.release(); //
		}

	}

	// ��ĳ�����ӷ�����Ϣ
	protected static void send(PushConfigVO _configvo) {
		messageSender.send(_configvo);
	}

	/*
	 * ��ȡ�������ߵ�session.
	 */
	public static String[] getAllUser() {
		return connections.keySet().toArray(new String[0]);
	}

	/**
	 * ��Ϣ�����̡߳�
	 * @author haoming
	 * create by 2013-12-3
	 */
	private class MessageSender implements Runnable {

		protected boolean running = true;
		//		protected Map<Object, byte[]> messages = new HashMap<Object, byte[]>();

		protected Vector<PushConfigVO> messages = new Vector<PushConfigVO>();

		private ServerPushToClientUtil pushutil = new ServerPushToClientUtil();

		public MessageSender() {

		}

		public void stop() {
			running = false;
		}

		// ���û���½
		public void login(String session, String name) {
//			ServerPushToClientIFC box;
//			try {
//				box = (ServerPushToClientIFC) pushutil.lookupClient(UIShowBox.class, new PushConfigVO(ServerPushToClientUtil.PUSH_TYPE_EXCEPT_ME, session));
//				box.onExecute(new Object[] { session, name });
//			} catch (Exception e) {
//				e.printStackTrace();
//			}

		}

		// �û�����
		public void logout(String session) {
//			ServerPushToClientIFC box;
//			try {
//				box = (ServerPushToClientIFC) pushutil.lookupClient(UIShowBox.class, new PushConfigVO(ServerPushToClientUtil.PUSH_TYPE_EXCEPT_ME, session));
//				box.onExecute(session);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}

		}

		// ������Ϣ
		protected void send(PushConfigVO _configvo) {
			synchronized (messages) {
				//�������ֽڣ����н�β��ʶ��
				messages.add(_configvo);
				messages.notify();
			}
		}

		public void run() {
			while (running) {
				if (messages.size() == 0) {
					try {
						synchronized (messages) {
							messages.wait();
						}
					} catch (InterruptedException e) {
						// Ignore
					}
				}
				synchronized (connections) {
					synchronized (messages) {
						// ������Ϣ�����е���Ϣ
						for (PushConfigVO message : messages) {
							byte[] bytes = message.getMessage();
							String key = message.getPushtype();
							if (ServerPushToClientUtil.PUSH_TYPE_ONLINE.equals(key)) {
								for (HttpServletResponse response : connections.values()) {
									try {
										OutputStream outStream = response.getOutputStream(); //
										outStream.write(bytes);
										outStream.flush();
									} catch (IOException e) {
										log("IOExeption execute command", e);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
							} else if (ServerPushToClientUtil.PUSH_TYPE_EXCEPT_ME.equals(key)) {
								String sroucesession = message.getFromSession();
								for (Entry entry : connections.entrySet()) {
									try {
										String session = (String) entry.getKey();
										if (session != null && session.equals(sroucesession)) {
											continue;
										}
										OutputStream outStream = ((HttpServletResponse) entry.getValue()).getOutputStream(); //
										outStream.write(bytes);
										outStream.flush();
									} catch (IOException e) {
										log("IOExeption execute command", e);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
							} else if (ServerPushToClientUtil.PUSH_TYPE_BY_SESSION.equals(key)) {
								String tosessions[] = message.getToSession();
								for (int i = 0; i < tosessions.length; i++) {
									HttpServletResponse response = connections.get(tosessions[i]);
									if (response == null) {
										continue;
									}
									try {
										OutputStream outStream = response.getOutputStream(); //
										outStream.write(bytes);
										outStream.flush();
									} catch (IOException e) {
										log("IOExeption execute command", e);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
							} else if (ServerPushToClientUtil.PUSH_TYPE_BY_CODE.equals(key)) {
								String usercodes[] = (String[]) message.getUsercode();
								for (int i = 0; i < usercodes.length; i++) {
									if (usercodes[i] != null) {
										HashSet set = user_sessions.get(usercodes[i]);
										String sessions[] = (String[]) set.toArray(new String[0]);
										for (int j = 0; j < sessions.length; j++) {
											if (sessions[j] != null) {
												HttpServletResponse response = connections.get(sessions[j]);
												try {
													OutputStream outStream = response.getOutputStream(); //
													outStream.write(bytes);
													outStream.flush();
												} catch (IOException e) {
													log("IOExeption execute command", e);
												} catch (Exception ex) {
													ex.printStackTrace();
												}
											}
										}
									}
								}
							}
							// ����Ϣ������ɾ����Ϣ
						}
						messages.removeAllElements();
					}
				}
			}
		}
	}
}