package cn.com.pushworld.wn.bs;

import java.util.ArrayList;
import java.util.List;


import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.DeleteSQLBuilder;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.ui.common.UIUtil;

public class TyxwcontentCount  extends AbstractDMO{
//	private CommDMO dmo = new CommDMO();
	private HashVO[] vos =null;
	/**
	 * ����С΢��Լ�̻���Ч����
	 * @param startDate: ��ʼʱ��(�³�)
	 * @param endDate:����ʱ��(�û�ѡ�е�����)
	 * @return
	 */
	public  String count(String startDate,String endDate,String curMonth,String dateInterval,boolean exists){
		InsertSQLBuilder insert=new  InsertSQLBuilder();
		insert.setTableName("wn_tyxwcount_result");
		DeleteSQLBuilder delete=new DeleteSQLBuilder();
//		String sql="select mcht_prop,mer_id, count(mer_id) num,sum(txn_amt) money from wnbank.t_dis_ifsp_intgr_txn_dtl where to_char(to_date(biz_dt,'yyyy-mm-dd'),'yyyy-mm-dd')>='"+startDate+"' and to_char(to_date(biz_dt,'yyyy-mm-dd'),'yyyy-mm-dd')<='"+endDate+"' and txn_sub_type_desc in ('����ɷѣ���ѽɷѣ�','����ɷѣ����Ԥ�ɷѣ�','�������汾��','����ǩԼ�����ǩԼ����Լ�������','�������ϱ��սɷѿۿ�','����ҽ�Ʊ��սɷѿۿ�','�������','΢�Ź��ں�֧��','΢�Ź��ں�֧��-(����һ�븶)','΢��ˢ��֧��','����','������ά�뱻ɨc2b����','������ά����ɨ_���ѽӿڣ��տ�֪ͨ��','Ԥ��Ȩ���','֧������ɨ֧��','֧��������Ԥ������ɨ��֧����','֧��������Ԥ������ɨ��֧��-����һ�븶��','��ũȡ��','��ũת��','΢����ɨ֧�������룩','΢�ű�ɨ֧��','΢����ɨ֧�������룩','֧������ɨ֧�������룩','֧������ɨ֧�������룩','������ά�뱻ɨC2B����') and txn_state='���׳ɹ�' group by mcht_prop,mer_id";
		String sql="select mcht_prop,mer_id, count(mer_id) num,sum(txn_amt) money from wnbank.t_dis_ifsp_intgr_txn_dtl where to_char(to_date(biz_dt,'yyyy-mm-dd'),'yyyy-mm-dd')>='"+startDate+"' and to_char(to_date(biz_dt,'yyyy-mm-dd'),'yyyy-mm-dd')<='"+endDate+"' and txn_sub_type_desc in (select a from wn_data_116) and txn_state='���׳ɹ�' group by mcht_prop,mer_id";
		try{
			if(exists){//������ڵ�ǰѡ�����ڵ�����
				delete.setTableName("wn_tyxwcount_result");
				delete.setWhereCondition("curmonth='"+curMonth+"'");
				UIUtil.executeUpdateByDS(null, delete.getSQL());
			}
			vos = UIUtil.getHashVoArrayByDS(null, sql);
			List<String> list=new ArrayList<String>();
			for (HashVO vo : vos) {
				insert.putFieldValue("mcht_prop", vo.getStringValue("mcht_prop"));
				insert.putFieldValue("mer_id", vo.getStringValue("mer_id"));
				insert.putFieldValue("num", vo.getStringValue("num"));
				insert.putFieldValue("money", vo.getStringValue("money"));
				insert.putFieldValue("curmonth", curMonth);
				insert.putFieldValue("dateInterval", dateInterval);
			    list.add(insert.getSQL());
			    if(list.size()>=5000){
			    	UIUtil.executeBatchByDS(null, list);
			    	list.clear();
			    }
			}
			if(list.size()>0){
				UIUtil.executeBatchByDS(null, list);
			}
			return "��"+curMonth+"����ԼС΢���ݼ���ɹ�";
		}catch(Exception e){
			return "��"+curMonth+"����ԼС΢���ݼ���ʧ�ܣ��������Ա��ϵ";
		}
	}
}
