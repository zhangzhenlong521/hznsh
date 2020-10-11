/**************************************************************************
 * $RCSfile: RefItemVO.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/
package cn.com.infostrategy.to.mdata;

import java.io.Serializable;

import cn.com.infostrategy.to.common.HashVO;

public class RefItemVO extends BillItemVO implements Serializable {

	private static final long serialVersionUID = 2978402575358156826L;

	private String id;

	private String code;

	private String name;

	private HashVO hashVO = null; //直接的数据

	public RefItemVO() {

	}

	public RefItemVO(HashVO _vo) {
		this.hashVO = _vo;
		this.id = hashVO.getStringValue(0);
		this.code = hashVO.getStringValue(1);
		this.name = hashVO.getStringValue(2);
	}

	public RefItemVO(String _id, String _code, String _name) {
		this.id = _id;
		this.code = _code;
		this.name = _name;

		this.hashVO = new HashVO();
		hashVO.setAttributeValue("ID", _id);
		hashVO.setAttributeValue("CODE", _code);
		hashVO.setAttributeValue("NAME", _name);
	}

	/**
	 * id,code,name与HashVO不一样!!
	 * @param _id
	 * @param _code
	 * @param _name
	 * @param _vo
	 */
	public RefItemVO(String _id, String _code, String _name, HashVO _vo) {
		this.id = _id;
		this.code = _code;
		this.name = _name;
		this.hashVO = _vo; //
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

	public HashVO getHashVO() {
		return hashVO;
	}

	public void setHashVO(HashVO hashVO) {
		this.hashVO = hashVO;
	}

	/**
	 * 得到某一项的值
	 * @param _itemkey
	 * @return
	 */
	public String getItemValue(String _itemkey) {
		if (hashVO != null) {
			return hashVO.getStringValue(_itemkey); //...
		}
		return null;
	}

	public String toString() {
		return this.name;
	}

	public boolean equals(Object _obj) {
		if (_obj == null) {
			if (this.id == null || this.id.equals("")) {
				return true; //如果目标是空,而本人的id为空或空字符串,则认为是相同的!!
			}
		}

		if (_obj instanceof RefItemVO) {
			RefItemVO new_vo = (RefItemVO) _obj;
			if (new_vo == null) {
				return false;
			} else {
				if (new_vo.getId() != null && new_vo.getId().equals(this.id)) {
					return true;
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
	}

	public Object clone() {
		RefItemVO cloneVO = new RefItemVO(this.getId(), this.getCode(), this.getName(), this.getHashVO().deepClone()); //
		return cloneVO;
	}

}
/**************************************************************************
 * $RCSfile: RefItemVO.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 *
 * $Log: RefItemVO.java,v $
 * Revision 1.4  2012/09/14 09:22:56  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:40:53  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:43  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:23:07  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:31:56  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2010/04/23 08:14:14  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:00  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:01:56  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:49  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2009/08/17 07:47:43  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:28  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:26  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:09  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/06/18 09:44:31  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:15  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:23  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:32  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:26  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/02/27 06:03:02  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 04:16:43  lujian
 * *** empty log message ***
 *
 *
**************************************************************************/
