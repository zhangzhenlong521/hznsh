package cn.com.pushworld.wn.bs;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTJobIFC;

/**
 * 
 * @author fj
 * 		ÿ�»���������ݺ󣬽��ñ������е��ظ�����ɾ��
 *
 */

public class DeleteHkRepeat implements WLTJobIFC {
	private CommDMO dmo = new CommDMO();

	@Override
	public String run() throws Exception {
		deleterepeatdata();
		return "ִ�гɹ���";
	}

	private void deleterepeatdata() throws Exception {
		String sql="delete from wnbank.s_loan_hk where to_char(trunc(xd_col4-1),'yyyy-mm-dd')<>to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')";
		int c = dmo.executeUpdateByDS(null, sql);
		System.out.println("ɾ����"+c+"���ظ�����");
		}

}
