package cn.com.infostrategy.bs.sysapp.sysboard;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.sysapp.login.DeskTopNewsDataBuilderIFC;
import cn.com.infostrategy.to.common.HashVO;

/**
 * ϵͳ�������ҳ���ݹ�����!!!�Ժ�����ҳ����ʱӦ�ý����Ϳ�����Ϊ����������!!!
 * @author xch
 *
 */
public class SysboardDataBuilder implements DeskTopNewsDataBuilderIFC {

	/**
	 * ȡ����������!!
	 */
	public HashVO[] getNewData(String _userCode) throws Exception {
		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, "select * from pub_sysboard where msgtype='ϵͳ��Ϣ' order by seq,createtime"); //
		for (int i = 0; i < hvs.length; i++) {
			hvs[i].setToStringFieldName("title"); //����!!!
		}
		return hvs;
	}

}
