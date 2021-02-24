/**************************************************************************
 * $RCSfile: JListItemVO.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.cardcomp;

import java.io.Serializable;

public class JListItemVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	private String code;

	private String name;

	public JListItemVO() {
	}

	public JListItemVO(String _id,  String _name, String _code) {
		this.id = _id;
		this.code = _name;
		this.name = _name; //
	}

	public String toString() {
		return this.name;
	}

	public boolean equals(Object _obj) {
		if (_obj instanceof JListItemVO) {
			JListItemVO new_name = (JListItemVO) _obj;
			if (new_name.getId().equals(this.id) && new_name.getCode().equals(this.getCode()) && new_name.getName().equals(this.getName())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
/**************************************************************************
 * $RCSfile: JListItemVO.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 *
 * $Log: JListItemVO.java,v $
 * Revision 1.4  2012/09/14 09:22:57  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:40:59  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:46  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:23:15  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:32:05  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:14  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:02:00  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:58  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:46  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:31  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:22  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:20  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:30  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:42  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:23  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 05:14:30  lujian
 * *** empty log message ***
 *
 *
**************************************************************************/