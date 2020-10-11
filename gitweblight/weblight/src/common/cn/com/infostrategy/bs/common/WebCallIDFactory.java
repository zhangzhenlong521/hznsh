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
	 * 在缓存中先置入...
	 * @param _sessionID
	 * @param _parvo
	 */
	public void putWebCallSessionId(String _sessionID, WebCallParVO _parvo) {
		webCallSessionIDMap.put(_sessionID, _parvo); //就送入该MAP
		logger.debug("注册WebCallID=[" + _sessionID + "]");
	}

	/**
	 * 在缓存中先置入...
	 * @param _sessionID
	 * @param _parvo
	 */
	public void putHtmlContentSessionId(String _sessionID, String _htmlContent) {
		htmlContentSessionIDMap.put(_sessionID, _htmlContent); //就送入该MAP
		logger.debug("注册htmlContentSessionID=[" + _sessionID + "]");
	}

	/**
	 * 再清空
	 * @param _sessionID
	 */
	public void clearWebCallSession(String _sessionID) {
		webCallSessionIDMap.remove(_sessionID); //
		logger.info("删除WebCallID=[" + _sessionID + "]");
	}

	/**
	 * 清空
	 * @param _sessionID
	 */
	public void clearHtmlContentSession(String _sessionID) {
		htmlContentSessionIDMap.remove(_sessionID); //
		logger.info("删除htmlContentSessionID=[" + _sessionID + "]"); //
	}

	/**
	 * 从缓存中取得参数
	 * @param _sessionID
	 * @return
	 */
	public WebCallParVO getWebCallParVO(String _sessionID) {
		return (WebCallParVO) webCallSessionIDMap.get(_sessionID); //
	}

	/**
	 * 取得一个Html中的内容!!
	 * @param _htmlContentID
	 * @return
	 */
	public String getHtmlContentByID(String _htmlContentID) {
		return (String) htmlContentSessionIDMap.get(_htmlContentID); //
	}

	/**
	 * 在缓存中先置入...
	 * @param _sessionID
	 * @param _parvo
	 */
	public void putOfficeCallSessionId(String _sessionID, OfficeCompentControlVO _vo) {
		officeCallSessionIDMap.put(_sessionID, _vo); //就送入该MAP
	}

	/**
	 * 再清空
	 * @param _sessionID
	 */
	public void clearOfficeCallSession(String _sessionID) {
		officeCallSessionIDMap.remove(_sessionID); //
	}

	/**
	 * 取得OfficeServlet调用时的参数!
	 * @param _sessionID
	 * @return
	 */
	public OfficeCompentControlVO getOfficeCallParMap(String _sessionID) {
		return (OfficeCompentControlVO) officeCallSessionIDMap.get(_sessionID); //
	}

}
