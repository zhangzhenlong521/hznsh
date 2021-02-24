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
	 * 计算小微特约商户有效户数
	 * @param startDate: 开始时间(月初)
	 * @param endDate:结束时间(用户选中的日期)
	 * @return
	 */
	public  String count(String startDate,String endDate,String curMonth,String dateInterval,boolean exists){
		InsertSQLBuilder insert=new  InsertSQLBuilder();
		insert.setTableName("wn_tyxwcount_result");
		DeleteSQLBuilder delete=new DeleteSQLBuilder();
//		String sql="select mcht_prop,mer_id, count(mer_id) num,sum(txn_amt) money from wnbank.t_dis_ifsp_intgr_txn_dtl where to_char(to_date(biz_dt,'yyyy-mm-dd'),'yyyy-mm-dd')>='"+startDate+"' and to_char(to_date(biz_dt,'yyyy-mm-dd'),'yyyy-mm-dd')<='"+endDate+"' and txn_sub_type_desc in ('便民缴费（电费缴费）','便民缴费（电费预缴费）','部分利随本清','短信签约变更（签约、解约、变更）','个人养老保险缴费扣款','个人医疗保险缴费扣款','结清贷款','微信公众号支付','微信公众号支付-(订单一码付)','微信刷卡支付','消费','银联二维码被扫c2b消费','银联二维码主扫_消费接口（收款通知）','预授权完成','支付宝被扫支付','支付宝交易预创建（扫码支付）','支付宝交易预创建（扫码支付-订单一码付）','助农取款','助农转账','微信主扫支付（静码）','微信被扫支付','微信主扫支付（动码）','支付宝主扫支付（静码）','支付宝主扫支付（动码）','银联二维码被扫C2B消费') and txn_state='交易成功' group by mcht_prop,mer_id";
		String sql="select mcht_prop,mer_id, count(mer_id) num,sum(txn_amt) money from wnbank.t_dis_ifsp_intgr_txn_dtl where to_char(to_date(biz_dt,'yyyy-mm-dd'),'yyyy-mm-dd')>='"+startDate+"' and to_char(to_date(biz_dt,'yyyy-mm-dd'),'yyyy-mm-dd')<='"+endDate+"' and txn_sub_type_desc in (select a from wn_data_116) and txn_state='交易成功' group by mcht_prop,mer_id";
		try{
			if(exists){//代表存在当前选中日期的数据
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
			return "【"+curMonth+"】特约小微数据计算成功";
		}catch(Exception e){
			return "【"+curMonth+"】特约小微数据计算失败，请与管理员联系";
		}
	}
}
