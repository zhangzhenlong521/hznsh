package cn.com.infostrategy.ui.mdata.formatcomp;

import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.mdata.BillFormatPanel;

/**
 * ע��������,����������,������BillFormat�������һ��RefItemVO����!!!
 * @author xch
 *
 */
public interface RegFormatRefDataCreaterIFC {

	//���ɲ������ݷ���!!
	public RefItemVO createRefItemVO(BillFormatPanel _formatPanel) throws Exception;

}
