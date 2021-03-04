package cn.com.infostrategy.ui.mdata.formatcomp;

import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.mdata.BillFormatPanel;

/**
 * 注册面板参照,数据生成器,即根据BillFormat面板生成一个RefItemVO返回!!!
 * @author xch
 *
 */
public interface RegFormatRefDataCreaterIFC {

	//生成参数数据返回!!
	public RefItemVO createRefItemVO(BillFormatPanel _formatPanel) throws Exception;

}
