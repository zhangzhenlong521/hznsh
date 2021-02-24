/**************************************************************************
 * $RCSfile: AggBillVO.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/
package cn.com.infostrategy.to.mdata;

import java.io.Serializable;
import java.util.Vector;

import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.common.WLTConstants;

public class AggBillVO implements Serializable {

	private static final long serialVersionUID = -3613852473457906627L;

	private BillVO parenVO = null; //主表的BillVO

	private VectorMap childVOMaps = null;

	public BillVO getParentVO() {
		return this.parenVO;
	}

	public void setParentVO(BillVO _vo) {
		this.parenVO = _vo;
	}

	/**
	 * 根据页签的顺序取得新增VO
	 * @param _index,页签位置是1,2,3,4,5
	 * @return
	 */
	public BillVO[] getChildAllVOs(int _index) {
		return (BillVO[]) childVOMaps.get(_index - 1); //取得所有数据
	}

	public void setChildAllVOs(int _index, BillVO[] vos) {
		childVOMaps.set(_index, vos);
	}

	/**
	 * 根据页签的顺序取得新增VO
	 * @param _index,页签位置是1,2,3,4,5
	 * @return
	 */
	public BillVO[] getChildInsertVOs(int _index) {
		BillVO[] billVOs = getChildAllVOs(_index);
		Vector v_tmp = new Vector(); //
		for (int i = 0; i < billVOs.length; i++) {
			if (billVOs[i].getEditType().equals(WLTConstants.BILLDATAEDITSTATE_INSERT)) {
				v_tmp.add(billVOs[i]);
			}
		}
		return (BillVO[]) v_tmp.toArray(new BillVO[0]); //
	}

	/**
	 * 根据页签的顺序取得新增VO
	 * @param _index,页签位置是1,2,3,4,5
	 * @return
	 */
	public BillVO[] getChildDeleteVOs(int _index) {
		BillVO[] billVOs = getChildAllVOs(_index);
		Vector v_tmp = new Vector(); //
		for (int i = 0; i < billVOs.length; i++) {
			if (billVOs[i].getEditType().equals(WLTConstants.BILLDATAEDITSTATE_DELETE)) {
				v_tmp.add(billVOs[i]);
			}
		}
		return (BillVO[]) v_tmp.toArray(new BillVO[0]); //
	}

	/**
	 * 根据页签的顺序取得新增VO
	 * @param _index,页签位置是1,2,3,4,5
	 * @return
	 */
	public BillVO[] getChildUpdateVOs(int _index) {
		BillVO[] billVOs = getChildAllVOs(_index);
		Vector v_tmp = new Vector(); //
		for (int i = 0; i < billVOs.length; i++) {
			if (billVOs[i].getEditType().equals(WLTConstants.BILLDATAEDITSTATE_UPDATE)) {
				v_tmp.add(billVOs[i]);
			}
		}
		return (BillVO[]) v_tmp.toArray(new BillVO[0]); //
	}

	public BillVO[] getChildInsertVOs() {
		return getChildInsertVOs(1);
	}

	public BillVO[] getChildDeleteVOs() {
		return getChildDeleteVOs(1);
	}

	public BillVO[] getChildUpdateVOs() {
		return getChildUpdateVOs(1);
	}

	//
	public void setChildVOMaps(VectorMap childVOMaps) {
		this.childVOMaps = childVOMaps;
	}

	//总共有几个页签!!
	public int getChildCount() {
		return childVOMaps.size(); //总共有几个页签!!
	}

	public String[] getChildCodes() {
		return childVOMaps.getKeysAsString(); //返回各页签的模板编码
	}
}
/**************************************************************************
 * $RCSfile: AggBillVO.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 *
 * $Log: AggBillVO.java,v $
 * Revision 1.4  2012/09/14 09:22:56  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:40:52  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:43  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:23:07  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:31:55  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:00  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.4  2010/02/08 11:01:56  sunfujun
 * *** empty log message ***
 *
 * Revision 1.2  2009/11/12 10:26:52  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:49  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:27  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:26  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:08  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:15  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:22  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:32  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:26  xch
 * *** empty log message ***
 *
 * Revision 1.4  2007/03/30 07:09:47  qilin
 * no message
 *
 * Revision 1.3  2007/02/27 06:57:20  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 04:23:54  lujian
 * *** empty log message ***
 *
 *
**************************************************************************/
