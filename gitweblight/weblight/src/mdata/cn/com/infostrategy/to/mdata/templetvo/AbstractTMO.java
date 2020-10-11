/**************************************************************************
 * $RCSfile: AbstractTMO.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:58 $
 **************************************************************************/
package cn.com.infostrategy.to.mdata.templetvo;

import java.io.Serializable;

import cn.com.infostrategy.to.common.HashVO;

/**
 * 所有模板元数据VO的抽象类!!!!
 * @author user
 *
 */
public abstract class AbstractTMO implements Serializable {

	/**
	 * 主表VO
	 * @return
	 */
	public abstract HashVO getPub_templet_1Data();

	/**
	 * 子表VO
	 * @return
	 */
	public abstract HashVO[] getPub_templet_1_itemData();

}
/**************************************************************************
 * $RCSfile: AbstractTMO.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:58 $
 *
 * $Log: AbstractTMO.java,v $
 * Revision 1.4  2012/09/14 09:22:58  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:40:56  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:47  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:23:08  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:31:57  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:02  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:01:54  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:50  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:32  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:26  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:11  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:15  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:23  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:34  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:31  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/02/27 06:03:01  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 04:12:30  lujian
 * *** empty log message ***
 *
 *
**************************************************************************/
