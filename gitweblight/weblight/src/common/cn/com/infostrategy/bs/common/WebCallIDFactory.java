package cn.com.infostrategy.bs.common;

import java.util.HashMap;

import org.apache.log4j.Logger;

import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WebCallParVO;
import cn.com.infostrategy.to.mdata.OfficeCompentControlVO;

public class WebCallIDFactory {

	private static WebCallIDFactory factory = new WebCallIDFactory(); //

	public static HashMap webCallSessionIDMap = new HashMap(); //
	public static HashMap htmlContentSessionIDMap = new HashMap(); //

	public static HashMap officeCallSessionIDMap = new HashMap(); //

	private Logger logger = WLTLogger.getLogger(WebCallIDFactory.class);

	private WebCallIDFactory() {
	}

	public static WebCallIDFactory getInstance() {
		return factory; //
	}

	/**
	 * �ڻ�����������...
	 * @param _sessionID
	 * @param _parvo
	 */
	public void putWebCallSessionId(String _sessionID, WebCallParVO _parvo) {
		webCallSessionIDMap.put(_sessionID, _parvo); //�������MAP
		logger.debug("ע��WebCallID=[" + _sessionID + "]");
	}

	/**
	 * �ڻ�����������...
	 * @param _sessionID
	 * @param _parvo
	 */
	public void putHtmlContentSessionId(String _sessionID, String _htmlContent) {
		htmlContentSessionIDMap.put(_sessionID, _htmlContent); //�������MAP
		logger.debug("ע��htmlContentSessionID=[" + _sessionID + "]");
	}

	/**
	 * �����
	 * @param _sessionID
	 */
	public void clearWebCallSession(String _sessionID) {
		webCallSessionIDMap.remove(_sessionID); //
		logger.info("ɾ��WebCallID=[" + _sessionID + "]");
	}

	/**
	 * ���
	 * @param _sessionID
	 */
	public void clearHtmlContentSession(String _sessionID) {
		htmlContentSessionIDMap.remove(_sessionID); //
		logger.info("ɾ��htmlContentSessionID=[" + _sessionID + "]"); //
	}

	/**
	 * �ӻ�����ȡ�ò���
	 * @param _sessionID
	 * @return
	 */
	public WebCallParVO getWebCallParVO(String _sessionID) {
		return (WebCallParVO) webCallSessionIDMap.get(_sessionID); //
	}

	/**
	 * ȡ��һ��Html�е�����!!
	 * @param _htmlContentID
	 * @return
	 */
	public String getHtmlContentByID(String _htmlContentID) {
		return (String) htmlContentSessionIDMap.get(_htmlContentID); //
	}

	/**
	 * �ڻ�����������...
	 * @param _sessionID
	 * @param _parvo
	 */
	public void putOfficeCallSessionId(String _sessionID, OfficeCompentControlVO _vo) {
		officeCallSessionIDMap.put(_sessionID, _vo); //�������MAP
	}

	/**
	 * �����
	 * @param _sessionID
	 */
	public void clearOfficeCallSession(String _sessionID) {
		officeCallSessionIDMap.remove(_sessionID); //
	}

	/**
	 * ȡ��OfficeServlet����ʱ�Ĳ���!
	 * @param _sessionID
	 * @return
	 */
	public OfficeCompentControlVO getOfficeCallParMap(String _sessionID) {
		return (OfficeCompentControlVO) officeCallSessionIDMap.get(_sessionID); //
	}

}
