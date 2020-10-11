package cn.com.infostrategy.ui.sysapp.login;

import java.awt.BorderLayout;
import java.io.Serializable;

import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.ConsoleMsgVO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.sysapp.login.LoginOneOffVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientConsolePrintStream;
import cn.com.infostrategy.ui.common.IAppletLoader;

/**
 * ��������һ�����ı�׼AppletLoader! 
 * ������������Ĭ�ϼ��ص�¼����!!!��ʵ������������ϵͳ������ʱ(����������),����ֱ�Ӵ�ĳһ�����!!������ĳһ���˵����ܵ�!!
 * ����LoadWltPanelServlet�е���!!! 
 * @author xch
 *
 */
public class LoadWltPanelLoader implements IAppletLoader, Serializable {

	private static final long serialVersionUID = 1L;
	private LoginUtil loginUtil = new LoginUtil(); //

	//���췽��!!
	public LoadWltPanelLoader() {
		ClientConsolePrintStream mySystemOut = new ClientConsolePrintStream(ConsoleMsgVO.SYSTEM_OUT, System.out); //�ض������̨
		System.setOut(mySystemOut);
		ClientConsolePrintStream mySystemErr = new ClientConsolePrintStream(ConsoleMsgVO.SYSTEM_ERROR, System.err); //�����.
		System.setErr(mySystemErr); //
	}

	public void loadApplet(JApplet _applet, JPanel _mainPanel) throws Exception {
		try {
			String str_skiptype = _applet.getParameter("REQ_skiptype"); //skiptype=menu&menuid
			String str_menuid = _applet.getParameter("REQ_menuid");
			String str_clsname = _applet.getParameter("REQ_clsname"); //ֱ��������
			//_panel.add(new JLabel("skiptype=[" + str_skiptype + "],menuid=[" + str_menuid + "]")); //

			String str_usercode = _applet.getParameter("REQ_usercode"); //���Ƿ����û�����,�����,��Ҫ��¼һ��!!!
			if (str_usercode != null) { //������û�����!!!���¼һ��,���ͻ��˻���������ֵ! ��Ϊ���ܲ˵����ܵ�����Ҫ�õ���Щ��������!!!
				LoginAppletLoader loader = new LoginAppletLoader(false); //
				LoginOneOffVO oneOffVO = loader.doLogin(str_usercode, null, null, null, false, true, null); //��¼һ��!!Ҳȡ������Ӧ�Ĳ˵���������!!!������ɫ����Ϣ!!!
				loginUtil.setWLTLookAndFeel(oneOffVO.getCurrLoginUserVO().getAllLookAndFeels(), _mainPanel); //��������!!!
				_mainPanel.updateUI(); //
			} else {
				loginUtil.setDefaultLookAndFeel(_mainPanel); //Ĭ�Ϸ��!!
				_mainPanel.updateUI(); //
			}

			if (str_menuid != null) {
				AbstractWorkPanel workItemPanel = loginUtil.getWorkPanelByMenuVO(str_menuid); //
				workItemPanel.initialize(); //��ʼ��,����Ҫִ��!!
				_mainPanel.setLayout(new BorderLayout()); ////
				_mainPanel.add(workItemPanel); //
			} else if (str_clsname != null) { //���������Ϊ��
				HashVO hvo = new HashVO(); //
				hvo.setAttributeValue("name", "ֱ��Skip����ҳ��"); //
				hvo.setAttributeValue("usecmdtype", "1"); //ʹ�õ�����
				hvo.setAttributeValue("commandtype", "00"); //ҳ������,�Զ������!!
				hvo.setAttributeValue("command", str_clsname); //����
				AbstractWorkPanel workItemPanel = loginUtil.getWorkPanelByMenuVO(hvo); //
				workItemPanel.initialize(); //
				_mainPanel.setLayout(new BorderLayout()); ////
				_mainPanel.add(workItemPanel); //
			} else {
				_mainPanel.setLayout(new BorderLayout()); ////
				_mainPanel.add(new JLabel("��û�ж���[menuid],Ҳû�ж���[clsname],�޷������κ�ҳ��!!")); //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
			_mainPanel.removeAll(); //
			_mainPanel.setLayout(new BorderLayout()); ////
			_mainPanel.add(new JLabel("����ҳ�淢���쳣:[" + ex.getMessage() + "]")); //
		} finally {
			loginUtil.startClientRefreshThread(); //�����ͻ���ˢ���߳�!!
		}

	}
}
