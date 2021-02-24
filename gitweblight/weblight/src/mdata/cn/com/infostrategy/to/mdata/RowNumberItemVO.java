/**************************************************************************
 * $RCSfile: RowNumberItemVO.java,v $  $Revision: 1.8 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/
package cn.com.infostrategy.to.mdata;

import java.io.Serializable;

import cn.com.infostrategy.to.common.HashVO;

/**
 * 行号记录的内容,行号有非常大的作用!!!包括: 1.记录数据编辑状态,比如是新增,删除,修改等
 * 2.记录Rowid,对那些没有主键的中间表,只能通过RowId来进行修改!!! 3.
 * 
 * @author user
 * 
 */
public class RowNumberItemVO implements Serializable {

	private static final long serialVersionUID = 7602704513142399035L;

	public static String INIT = "INIT"; //
	public static String INSERT = "INSERT"; //
	public static String UPDATE = "UPDATE"; //
	private String state = null; // 状态
	private int recordIndex; // 真正的记录号
	private int totalRecordCount = 0; //实际的记录数!!因为后来有分页,需要将总共的记录数记录下来!因为返回的是Object[][],所以无地方保存,只好放在行号里!!
	private boolean isChecked = false;// 是否勾选中!!!

	private HashVO recordHVO = null; //有时会根据一个SQL查询,但在模板中却又没有定义SQL有的列,但在程序中却又需要这个列的数据,为了不麻烦在模板中增加新的列,所以有了这个设计!!! 比如新的工作流机制就存要这个问题,即显示是业务单据表,但进行逻辑处理的又需要pub_task_deal表中的信息!!

	public RowNumberItemVO() {
	}

	public RowNumberItemVO(String _state, int _recordIndex) {
		this.state = _state;
		this.recordIndex = _recordIndex;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String toString() {
		return "[" + getState() + "]"; //
	}

	public int getRecordIndex() {
		return recordIndex;
	}

	public void setRecordIndex(int recordIndex) {
		this.recordIndex = recordIndex;
	}

	public int getTotalRecordCount() {
		return totalRecordCount;
	}

	public void setTotalRecordCount(int totalRecordCount) {
		this.totalRecordCount = totalRecordCount;
	}

	public HashVO getRecordHVO() {
		return recordHVO;
	}

	public void setRecordHVO(HashVO recordHVO) {
		this.recordHVO = recordHVO;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

}

/**************************************************************************
 * $RCSfile: RowNumberItemVO.java,v $  $Revision: 1.8 $  $Date: 2012/09/14 09:22:56 $
 *
 * $Log: RowNumberItemVO.java,v $
 * Revision 1.8  2012/09/14 09:22:56  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:40:53  Administrator
 * *** empty log message ***
 *
 * Revision 1.7  2011/10/10 06:31:43  wanggang
 * restore
 *
 * Revision 1.5  2011/08/10 14:18:07  xch123
 * *** empty log message ***
 *
 * Revision 1.4  2010/09/05 13:00:44  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/09/05 12:18:02  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2010/08/17 15:23:06  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/17 10:23:07  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:31:56  xuchanghua
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
 * Revision 1.1  2009/02/20 06:10:28  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:26  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/06/28 02:57:59  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:09  xuchanghua
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
**************************************************************************/
