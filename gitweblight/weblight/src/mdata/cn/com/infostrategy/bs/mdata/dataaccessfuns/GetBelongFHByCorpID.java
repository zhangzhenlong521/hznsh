package cn.com.infostrategy.bs.mdata.dataaccessfuns;

import java.util.HashMap;

import cn.com.infostrategy.bs.mdata.DataAccessFunctionIFC;
import cn.com.infostrategy.to.common.HashVO;

/**
 * 根据机构id取得其所属分行的机构id
 * @author xch
 *
 */
public class GetBelongFHByCorpID implements DataAccessFunctionIFC {

	private HashVO[] allCorpVOs = null; //存储所有机构数据的类变量

	private HashMap map_index = new HashMap(); //存储索引的哈希表,用于搜索时快速!!

	/**
	 * 构造方法,先一下子从数据库中取出所有机构的关系,然后赋给类变量
	 */
	public GetBelongFHByCorpID() {
		//从数据库中取出所有机构数据,赋给类变量,且自动是树型结构,采用平台的方法!!!
	}

	public String getFunValue(String[] _pars) {
		String str_corpid = _pars[0];  //第一个参数是实际机构id
		//从类变量是搜索,逐步往上层查找,找到一个父结点类型为分行的机构,就是返回值!
		return null;
	}

}
