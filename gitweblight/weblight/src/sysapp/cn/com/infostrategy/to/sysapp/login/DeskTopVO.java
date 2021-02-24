/**************************************************************************
 * $RCSfile: DeskTopVO.java,v $  $Revision: 1.6 $  $Date: 2012/09/14 09:19:32 $
 **************************************************************************/
package cn.com.infostrategy.to.sysapp.login;

import java.io.Serializable;

import cn.com.infostrategy.to.common.HashVO;

public class DeskTopVO implements Serializable {

	private static final long serialVersionUID = -5505436033853005047L;

	private String loginTime = null; //

	private HashVO[] menuVOs = null;

	public HashVO[] getMenuVOs() {
		return menuVOs;
	}

	public void setMenuVOs(HashVO[] menuVOs) {
		this.menuVOs = menuVOs;
	}

	public String getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}

}
/**************************************************************************
 * $RCSfile: DeskTopVO.java,v $  $Revision: 1.6 $  $Date: 2012/09/14 09:19:32 $
 *
 * $Log: DeskTopVO.java,v $
 * Revision 1.6  2012/09/14 09:19:32  xch123
 * 邮储现场回来统一更新
 *
 * Revision 1.1  2012/08/28 09:41:12  Administrator
 * *** empty log message ***
 *
 * Revision 1.5  2011/10/10 06:32:09  wanggang
 * restore
 *
 * Revision 1.3  2011/04/26 04:36:15  xch123
 * *** empty log message ***
 *
 * Revision 1.2  2011/01/27 09:55:53  xch123
 * 兴业春节前回来
 *
 * Revision 1.1  2010/05/17 10:23:40  xuchanghua
 * *** empty log message ***
 *
**************************************************************************/
