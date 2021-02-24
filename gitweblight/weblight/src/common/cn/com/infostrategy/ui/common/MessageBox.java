/**************************************************************************
 * $RCSfile: MessageBox.java,v $  $Revision: 1.13 $  $Date: 2012/09/14 09:17:30 $
 **************************************************************************/
package cn.com.infostrategy.ui.common;

import java.awt.Color;
import java.awt.Container;
import java.awt.Frame;
import java.awt.HeadlessException;

import javax.swing.JOptionPane;

import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WLTRemoteException;

/**
 * �Ի���..
 * 
 * ����ԭ��: 
 * ����Ҫ����, ��Ҫ˵���߰����һ��, ������Ҳ������ʲô��˼!
 * ��������, ���ð׻�. (�Ͼ�������רҵ�������, ����������վ)
 * ��Ҫ����ǿ���ԵĴ������(����, һ��..)
 * "!"����, "!!!"�����ܳ���!
 * ������ʾ����:
 * ��ȷ��Ҫ[xx]��? (ɾ��, �ƶ�..)
 * ��ѡ��һ��xx, ִ�д˲���.(��¼, ��Ա, �¼�)
 * ��û��ѡ��xx, �Ƿ����?(��Ա, ��λ)
 * �ü�¼�Ѵ���,����ִ�д˲���.
 * �ü�¼״̬Ϊ[xxx], ����ִ�д˲���.
 * 
 * @author xch
 *
 */
public class MessageBox {

	private static org.apache.log4j.Logger logger = WLTLogger.getLogger(MessageBox.class); //

	public static void show(Object _message) {
		init(null, _message, WLTConstants.MESSAGE_INFO, 0, null);
	}

	public static void show(Container _parent, Object _message, int liveSec, String defaultText) {
		init(_parent, _message, WLTConstants.MESSAGE_INFO, liveSec, defaultText);
	}

	public static void show(Container _parent, Object _message) {
		init(_parent, _message, WLTConstants.MESSAGE_INFO, 0, null);
	}

	public static void show(Container _parent, Object _message, int _width, int _height) {
		showTextArea(_parent, "��ʾ", _message, _width, _height);
	}

	public static void showInfo(Container _parent, Object _message) {
		init(_parent, _message, WLTConstants.MESSAGE_INFO, 0, null);
	}

	public static void showTextArea(Container _parent, Object _message) {
		init(_parent, _message, WLTConstants.MESSAGE_INFO, 0, null);
	}

	public static void showTextArea(Container _parent, String _title, Object _message) {
		if (SplashWindow.window != null) {
			SplashWindow.window.closeWindow(); //
		}
		TextAreaDialog dialog = new TextAreaDialog(_parent, _title, _message); //
		dialog.setVisible(true); //
	}

	public static void showTextArea(Container _parent, String _title, Object _message, int _width, int _height) {
		if (SplashWindow.window != null) {
			SplashWindow.window.closeWindow(); //
		}
		showTextArea(_parent, _title, _message, _width, _height, null); //
	}

	public static void showTextArea(Container _parent, String _title, Object _message, int _width, int _height, Color _bgcolor) {
		if (SplashWindow.window != null) {
			SplashWindow.window.closeWindow(); //
		}
		TextAreaDialog dialog = new TextAreaDialog(_parent, _title, _message, _width, _height, _bgcolor); //
		dialog.setVisible(true); //
	}

	public static void showQuestion(Container _parent, String _message) {
		init(_parent, _message, WLTConstants.MESSAGE_QUESTION, 0, null);
	}

	public static void showWarn(Container _parent, String _message) {
		init(_parent, _message, WLTConstants.MESSAGE_WARN, 0, null);
	}

	public static void show(Frame _parent, String _message) {
		init(_parent, _message, WLTConstants.MESSAGE_INFO, 0, null);
	}

	public static void show(String _message, int infolevel) {
		init(null, _message, infolevel, 0, null);
	}

	public static void show(Container _parent, String _message, int infolevel) {
		init(_parent, _message, infolevel, 0, null);
	}

	public static void show(Frame _parent, String _message, int infolevel) {
		init(_parent, _message, infolevel, 0, null);
	}

	public static boolean confirm(String _message) {
		return confirm(null, _message);
	}

	//ȷ�Ͽ�,��ֱ�ӷ��ز���ֵ!!��ʡ����ȥ�ж���intֵ��!!!
	public static boolean confirm(Container _parent, Object _message) {
		int li_returnValue = showConfirmDialog(_parent, _message); //
		if (li_returnValue == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false; //
		}
	}

	public static boolean confirm(Container _parent, Object _message, int _width, int _height) {
		int li_returnValue = showConfirmDialog(_parent, _message, _width, _height); //
		if (li_returnValue == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false; //
		}
	}

	public static int showConfirmDialog(Container _parentContainer, Object _msg) throws HeadlessException {
		return showConfirmDialog(_parentContainer, _msg, "��ʾ", JOptionPane.YES_NO_OPTION);
	}

	public static int showConfirmDialog(Container _parentContainer, Object _msg, int _width, int _height) throws HeadlessException {
		return showConfirmDialog(_parentContainer, _msg, "��ʾ", JOptionPane.YES_NO_OPTION, WLTConstants.MESSAGE_QUESTION, _width, _height); //
	}

	public static int showConfirmDialog(Container _parentContainer, Object _msg, String _title, int _optionType) throws HeadlessException {
		return showConfirmDialog(_parentContainer, _msg, _title, _optionType, WLTConstants.MESSAGE_QUESTION); //
	}

	public static int showConfirmDialog(Container _parentContainer, Object _msg, String _title, int _optionType, int _msgType) throws HeadlessException {
		return showConfirmDialog(_parentContainer, _msg, _title, _optionType, _msgType, 0, null);
	}

	/**
	 * �������ü����رգ����Զ��ر�ʱ����Ǹ���ť
	 * @param liveSec > 0 ʱִ��
	 * @param defaultText Ĭ�ϵ�һ����ť
	 * @return
	 * @throws HeadlessException
	 */
	public static int showConfirmDialog(Container _parentContainer, Object _msg, String _title, int _optionType, int _msgType, int liveSec, String defaultText) throws HeadlessException {
		TextAreaDialog dialog = new TextAreaDialog(_parentContainer, _title, getImgNameByMessageType(_msgType), _msg, _optionType, liveSec, defaultText); //
		dialog.setVisible(true); //��ʾ����!!!
		return dialog.getReturnValue(); //
	}

	public static int showConfirmDialog(Container _parentContainer, Object _msg, String _title, int _optionType, int _msgType, int _width, int _height) throws HeadlessException {
		return showConfirmDialog(_parentContainer, _msg, _title, _optionType, _msgType, _width, _height, 0, null);
	}

	/**
	 * �������ü����رգ����Զ��ر�ʱ����Ǹ���ť
	 * @param liveSec > 0 ʱִ��
	 * @param defaultText Ĭ�ϵ�һ����ť
	 * @return
	 * @throws HeadlessException
	 */
	public static int showConfirmDialog(Container _parentContainer, Object _msg, String _title, int _optionType, int _msgType, int _width, int _height, int liveSec, String defaultText) throws HeadlessException {
		TextAreaDialog dialog = new TextAreaDialog(_parentContainer, _title, getImgNameByMessageType(_msgType), _msg, _optionType, _width, _height, liveSec, defaultText); //
		dialog.setVisible(true); //��ʾ����!!!
		return dialog.getReturnValue(); //
	}

	public static int showOptionDialog(Container _parentContainer, Object _msg, String _title, String[] _options) throws HeadlessException {
		return showOptionDialog(_parentContainer, _msg, _title, _options, -1); //
	}

	public static int showOptionDialog(Container _parentContainer, Object _msg, String _title, String[] _options, int _width, int _height) throws HeadlessException {
		return showOptionDialog(_parentContainer, _msg, _title, _options, -1, _width, _height); //
	}

	public static int showOptionDialog(Container _parentContainer, Object _msg, String _title, String[] _options, int _width, int _height, boolean _isCanCopyText) throws HeadlessException {
		return showOptionDialog(_parentContainer, _msg, _title, _options, -1, _width, _height, _isCanCopyText); //
	}

	public static int showOptionDialog(Container _parentContainer, Object _msg, String _title, String[] _options, int _initValue) throws HeadlessException {
		return showOptionDialog(_parentContainer, _msg, _title, WLTConstants.MESSAGE_QUESTION, _options, _initValue, -1, -1, true, true); //
	}

	public static int showOptionDialog(Container _parentContainer, Object _msg, String _title, String[] _options, int _initValue, boolean _isCanCopyText) throws HeadlessException {
		return showOptionDialog(_parentContainer, _msg, _title, WLTConstants.MESSAGE_QUESTION, _options, _initValue, -1, -1, _isCanCopyText, true); //
	}

	public static int showOptionDialog(Container _parentContainer, Object _msg, String _title, String[] _options, int _initValue, int _width, int _height) throws HeadlessException {
		return showOptionDialog(_parentContainer, _msg, _title, WLTConstants.MESSAGE_QUESTION, _options, _initValue, _width, _height, true, true); //
	}

	public static int showOptionDialog(Container _parentContainer, Object _msg, String _title, String[] _options, int _initValue, int _width, int _height, boolean _isCanCopyText) throws HeadlessException {
		return showOptionDialog(_parentContainer, _msg, _title, WLTConstants.MESSAGE_QUESTION, _options, _initValue, _width, _height, _isCanCopyText, true); //
	}

	public static int showOptionDialog(Container _parentContainer, Object _msg, String _title, int _msgType, String[] _options, int _initValue, int _width, int _height, boolean _isCanCopyText, boolean _isResiable) throws HeadlessException {
		return showOptionDialog(_parentContainer, _msg, _title, _msgType, _options, _initValue, _width, _height, _isCanCopyText, _isResiable, 0, null); //
	}

	/**
	 * �������ü����رգ����Զ��ر�ʱ����Ǹ���ť
	 * @param liveSec > 0 ʱִ��
	 * @param defaultText Ĭ�ϵ�һ����ť
	 * @return
	 * @throws HeadlessException
	 */
	public static int showOptionDialog(Container _parentContainer, Object _msg, String _title, int _msgType, String[] _options, int _initValue, int _width, int _height, boolean _isCanCopyText, boolean _isResiable, int liveSec, String defaultText) throws HeadlessException {
		TextAreaDialog dialog = new TextAreaDialog(_parentContainer, _title, getImgNameByMessageType(_msgType), _msg, _options, _initValue, _width, _height, _isCanCopyText, _isResiable, liveSec, defaultText); //
		dialog.setVisible(true); //
		return dialog.getReturnValue(); //
	}

	/**
	 * ��ʼ������!!
	 * @param _parent
	 * @param _message
	 * @param infolevel
	 * @return
	 */
	public static int init(Container _parent, Object _message, int infolevel, int liveSec, String defaultText) {
		if (SplashWindow.window != null) {
			SplashWindow.window.closeWindow(); //
		}
		if (infolevel == WLTConstants.MESSAGE_CONFIRM) {
			return MessageBox.showConfirmDialog(_parent, _message, WLTConstants.MESSAGE_CONFIRM_TITLE, JOptionPane.YES_NO_OPTION, WLTConstants.MESSAGE_QUESTION, liveSec, defaultText);
		} else {
			TextAreaDialog dialog = new TextAreaDialog(_parent, "��ʾ", getImgNameByMessageType(infolevel), _message, liveSec, defaultText); //
			dialog.setVisible(true); //
			return dialog.getReturnValue(); //
		}
	}

	/**
	 * ��ʾ�쳣!!!
	 * @param _parent
	 * @param _ex
	 */
	public static void showException(Container _parent, Throwable _ex) {
		_ex.printStackTrace(); //
		if (_ex instanceof WLTAppException) {
			show(_parent, _ex.getMessage(), WLTConstants.MESSAGE_WARN);
		} else if (_ex instanceof WLTRemoteException) {
			WLTRemoteException wex = (WLTRemoteException) _ex;
			//wex.getServerTargetEx().printStackTrace();
			if (wex.getServerTargetEx() instanceof WLTAppException) { //�����ҵ���쳣!!!
				show(_parent, wex.getMessage(), WLTConstants.MESSAGE_WARN);
			} else {
				new ShowRemoteExDialog(_parent, wex); //����Ƿ�ҵ���쳣!!
			}
			//init(JOptionPane.getFrameForComponent(_parent), novaEx.getMessage() + "\r\n�������˶�ջ:\r\n" + novaEx.getServerStackDetail() + "\r\n\r\n�ͻ��˶�ջ:\r\n" + novaEx.getClientStackDetail(), NovaConstants.MESSAGE_ERROR);
		} else {
			init(JOptionPane.getFrameForComponent(_parent), _ex.getClass().getName() + "[" + _ex.getMessage() + "]", WLTConstants.MESSAGE_ERROR, 0, null);
		}
	}

	/**
	 * ������Ϣ���ͼ���ͼƬ����!!
	 * @param _msgType
	 * @return
	 */
	private static String getImgNameByMessageType(int _msgType) {
		if (_msgType == WLTConstants.MESSAGE_INFO) { //��Ϣ!
			return "info.gif"; //
		} else if (_msgType == WLTConstants.MESSAGE_QUESTION) { //����!!
			return "question.gif"; //
		} else if (_msgType == WLTConstants.MESSAGE_WARN) { //����!!
			return "warn.gif"; //
		} else if (_msgType == WLTConstants.MESSAGE_ERROR) { //����
			return "error.gif"; //
		} else {
			return "info.gif"; //
		}
	}

	/***
	 * Gwang Add 2012-07-30
	 * ��õ���Ϣ��ʾ
	 * @param _parent
	 */
	public static void showSelectOne(Container _parent) {
		init(_parent, "��ѡ��һ����¼, ִ�д˲���.", WLTConstants.MESSAGE_INFO, 0, null);
	}

	public static void showNotAllow(Container _parent, String act) {
		init(_parent, "�ü�¼״̬Ϊ[" + act + "], ����ִ�д˲���.", WLTConstants.MESSAGE_INFO, 0, null);
	}

	public static boolean confirmDel(Container _parent) {
		int li_returnValue = showConfirmDialog(_parent, "��ȷ��Ҫɾ����?");
		if (li_returnValue == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false; //
		}
	}

}
/**************************************************************************
 * $RCSfile: MessageBox.java,v $  $Revision: 1.13 $  $Date: 2012/09/14 09:17:30 $
 *
 * $Log: MessageBox.java,v $
 * Revision 1.13  2012/09/14 09:17:30  xch123
 * *** empty log message ***
 *
 * Revision 1.1  2012/08/28 09:40:50  Administrator
 * *** empty log message ***
 *
 * Revision 1.12  2012/07/30 10:05:54  wanggang
 * *** empty log message ***
 *
 * Revision 1.11  2012/07/30 07:15:19  wanggang
 * *** empty log message ***
 *
 * Revision 1.10  2012/07/30 06:49:27  wanggang
 * *** empty log message ***
 *
 * Revision 1.9  2012/06/08 11:21:10  xch123
 * *** empty log message ***
 *
 * Revision 1.8  2012/02/29 06:58:52  xch123
 * *** empty log message ***
 *
 * Revision 1.7  2012/02/29 06:43:22  xch123
 * *** empty log message ***
 *
 * Revision 1.6  2011/10/10 06:31:35  wanggang
 * restore
 *
 * Revision 1.4  2011/03/09 12:33:05  xch123
 * *** empty log message ***
 *
 * Revision 1.3  2011/02/18 10:21:03  xch123
 * *** empty log message ***
 *
 * Revision 1.2  2010/09/30 06:12:51  xch123
 * *** empty log message ***
 *
 *
 **************************************************************************/
