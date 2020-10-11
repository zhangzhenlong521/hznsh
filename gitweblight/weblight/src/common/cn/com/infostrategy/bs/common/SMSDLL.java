package cn.com.infostrategy.bs.common;

/**
 * ����Ϣ���ͽӿ�
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

	public native String openCom(int _port); //��ĳ���˿ڴ�����

	public native String closeCom(int _port); //�ر�ĳ���˿�����

	public native String sendmsg(int _port, String _msg, String _mbcode, boolean _ischinese); //���Ͷ���Ϣ

	public native String receive(int _port); //���ն���Ϣ..

	public void sendMsg(String[][] _msgs) {
		openCom(this.commport); //��ʼ���˿�
		System.out.println("��ʼ��Com�˿�[" + commport + "]�ɹ�!"); //
		for (int i = 0; i < _msgs.length; i++) {
			sendmsg(this.commport, _msgs[i][1], _msgs[i][0], true);
			System.out.println("���ֻ�[" + _msgs[i][0] + "]���Ͷ���Ϣ[" + _msgs[i][1] + "]�ɹ�!"); //
		}
		closeCom(this.commport); //�رն˿�
		System.out.println("�ر�Com�˿�[" + commport + "]�ɹ�!"); //
	}

	public static void main(String[] args) {
		try {
			SMSDLL smsdll = new SMSDLL(4); //�ڵ�4��Com�˿ڴ�
			smsdll.sendMsg(new String[][] { { "13621752206", "��һ����Ϣ" }, { "13621752206", "�ڶ�����Ϣ!" } }); //
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
