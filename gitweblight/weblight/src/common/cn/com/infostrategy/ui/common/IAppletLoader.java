/**************************************************************************
 * $RCSfile: IAppletLoader.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:17:30 $
 **************************************************************************/
package cn.com.infostrategy.ui.common;

public interface IAppletLoader {

	/**
	 * ¼ÓÔØApplet
	 * @param _applet  applet¾ä±ú
	 * @param _mainPanel  Ö÷Ãæ°å
	 * @throws Exception
	 */
	public void loadApplet(javax.swing.JApplet _applet, javax.swing.JPanel _mainPanel) throws Exception;

}

