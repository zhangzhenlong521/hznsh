package cn.com.infostrategy.ui.sysapp.login.click2;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

import org.jdesktop.jdic.browser.WebBrowser;
import org.jdesktop.jdic.browser.WebBrowserEvent;
import org.jdesktop.jdic.browser.WebBrowserListener;

/**
 *��ǰͨ������IE�˳�ʱ����Java����д��ճ�����ر�JavaSwing,���������鷢��������,��Ϊ��ȫԭ�򴥷�����,���رմ��ں�Ȳ���ͨ��self.location=webpush20100601://localjava?������,Ҳ����ͨ����������������!
 * ���Ժ����������˰취�����˳�ʱ��IE��дCookieֵ!Ȼ����Swing��ͨ��JDIC�ں�̨����һ��Html,���html�в���ˢ��cooike�е�ֵ,����IE�˳�ʱд��ֵ,������ͨ���޸Ĵ��ڱ�����֪ͨSwing,Swing�ӵ�����˳�Java!!
 * ��������Ǹ�������Frame!!! ���FrameҪһֱ��������״̬!!!
 * ���붨���˲���PushMonitorIEExit,������ʹ��IE��ʽ(���������¼,skip/skip2/����IE)������²Żᴴ���������!!!
 * �Ժ�������ڿ�����Ϊһ��ͨ����ר�ż���Cookie�Ļ�����,��Ҫ����չ֧������ͨ��Cookie��Ӧ��,�Ӷ�ʵ����IE֮����ͨѶ,����ע��/����/����/�������յ��!!��������ע���cookieƴ��һ��,�÷ֺ���ȺŸ���,Ȼ��������ת��һ����ϣ��,Ȼ��ȥ����ͬ�߼�!!
 * @author xch
 *
 */
public class MonitorIECookieDialog extends JDialog implements WebBrowserListener {

	private static final long serialVersionUID = -4587721446225016107L;
	private static MonitorIECookieDialog dialog = null; //
	private static String str_reason = null; //
	private WebBrowser wb = null; //

	private MonitorIECookieDialog() {
		super(); //��ģʽ!
		this.setModal(false); //
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE); //Ĭ��������!
		this.setTitle("Cookie��������"); //
		this.setSize(650, 250);
		this.setLocation(-2000, -2000); //
		initialize(); //
		this.toBack(); //�ں���!!!��ֹ��Ļ��!
		this.setVisible(true); //��������ʾ��������,���򴥷�����!
		this.setVisible(false); //���ص�!
	}

	public static JDialog createInstance() {
		if (dialog == null) {
			dialog = new MonitorIECookieDialog(); //
		}
		return dialog; //
	}

	public static JDialog getInstance() {
		return dialog; //
	}

	private void initialize() {
		try {
			String str_url = System.getProperty("CALLURL") + "/login?logintype=monitoriecookie"; //���ʵ�ַ!
			wb = new WebBrowser(new java.net.URL(str_url)); //
			this.getContentPane().add(wb, BorderLayout.CENTER); //
			wb.addWebBrowserListener(this); //�����¼�!!!
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * ����������仯��,��Swing�е���!
	 * @param _newTitle
	 */
	private void callSwing(String _newTitle) {
		String str_cookieKey = System.getProperty("PushMonitorIEExit"); //
		if (_newTitle != null && _newTitle.equals(str_cookieKey + "@exit")) { //������˳����
			System.out.println("MonitorIECookieDialog��������IE��Cookie��ȡ���˳����[" + _newTitle + "],����ֱ���˳�ϵͳ!!"); //
			System.exit(0); //ֱ���˳�ϵͳ!!!
		}
	}

	//���ò����ص�ԭ��!!���������淽ʽ!û�����ò�����!!
	public static void setNotLoadReason(String _reason) {
		str_reason = _reason; //
	}

	public static String getNotLoadReason() {
		return str_reason;
	}

	//����仯��!
	public void titleChange(WebBrowserEvent arg0) {
		callSwing(arg0.getData()); //
	}

	public void documentCompleted(WebBrowserEvent arg0) {
	}

	public void downloadCompleted(WebBrowserEvent arg0) {
	}

	public void downloadError(WebBrowserEvent arg0) {
	}

	public void downloadProgress(WebBrowserEvent arg0) {
	}

	public void downloadStarted(WebBrowserEvent arg0) {
	}

	public void statusTextChange(WebBrowserEvent arg0) {
	}

	public void windowClose(WebBrowserEvent arg0) {
	}

}
