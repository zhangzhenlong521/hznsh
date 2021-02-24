package cn.com.infostrategy.bs.common;

/**
 * 短消息发送接口
 * @author xch
 *
 */
public class SMSDLL {
	static {
		System.loadLibrary("SMSDLL");
	}

	int commport = 1;

	private SMSDLL() {
	}

	public SMSDLL(int _port) {
		this.commport = _port;
	}

	public native String openCom(int _port); //在某个端口打开连接

	public native String closeCom(int _port); //关闭某个端口连接

	public native String sendmsg(int _port, String _msg, String _mbcode, boolean _ischinese); //发送短消息

	public native String receive(int _port); //接收短消息..

	public void sendMsg(String[][] _msgs) {
		openCom(this.commport); //初始化端口
		System.out.println("初始化Com端口[" + commport + "]成功!"); //
		for (int i = 0; i < _msgs.length; i++) {
			sendmsg(this.commport, _msgs[i][1], _msgs[i][0], true);
			System.out.println("往手机[" + _msgs[i][0] + "]发送短消息[" + _msgs[i][1] + "]成功!"); //
		}
		closeCom(this.commport); //关闭端口
		System.out.println("关闭Com端口[" + commport + "]成功!"); //
	}

	public static void main(String[] args) {
		try {
			SMSDLL smsdll = new SMSDLL(4); //在第4个Com端口打开
			smsdll.sendMsg(new String[][] { { "13621752206", "第一条消息" }, { "13621752206", "第二条消息!" } }); //
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
