package com.pushworld.ipushgrc.bs.cmptarget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;

/**
 * 指标相关的DMO! 整个产品的远程调用接口就一个! 然后转调对应模块的DMO或Util
 * @author xch
 *
 */
public class CmpTargetDMO extends AbstractDMO {

	/**
	 * 根据选中的
	 * @param _parMap
	 * @throws Exception
	 */
	public String createTargetInstance(HashMap _parMap) throws Exception {
		String str_corpId = (String) _parMap.get("corpid"); //机构id
		String str_corpName = (String) _parMap.get("corpname"); //机构名称
		String str_instyear = (String) _parMap.get("instyear"); //年度
		String str_cyclevalue = (String) _parMap.get("cyclevalue"); //周期
		String str_target_id = (String) _parMap.get("target_id"); //指标id
		String str_target_name = (String) _parMap.get("target_name"); //指标名称

		CommDMO commDMO = new CommDMO(); //
		String str_count = commDMO.getStringValueByDS(null, "select count(*) from cmp_target_inst where corpid='" + str_corpId + "' and instyear='" + str_instyear + "' and cyclevalue='" + str_cyclevalue + "' and target_id='" + str_target_id + "'"); //看有没有曾经创建过
		if (!str_count.equals("0")) { //如果不是0
			throw new WLTAppException("该机构已创建了该季度的指标!不允许重复创建!"); //
		}

		ArrayList al_sqls = new ArrayList(); //
		//指标实例主表
		InsertSQLBuilder isql_1 = new InsertSQLBuilder("cmp_target_inst"); //
		String str_inst_id = commDMO.getSequenceNextValByDS(null, "S_CMP_TARGET_INST"); //
		isql_1.putFieldValue("id", str_inst_id); //主键值!!
		isql_1.putFieldValue("corpid", str_corpId); //机构id
		isql_1.putFieldValue("corpname", str_corpName); //机构名称
		isql_1.putFieldValue("instyear", str_instyear); //年度
		isql_1.putFieldValue("cyclevalue", str_cyclevalue); //周期值,比如1季度
		isql_1.putFieldValue("target_id", str_target_id); //指标id
		isql_1.putFieldValue("target_name", str_target_name); //指标名称
		al_sqls.add(isql_1.getSQL()); //

		//指标参数子表
		HashVO[] hvs_par = commDMO.getHashVoArrayByDS(null, "select * from cmp_target_par where target_id='" + str_target_id + "'"); //找出该指标的所有因子
		for (int i = 0; i < hvs_par.length; i++) {
			InsertSQLBuilder isql_2 = new InsertSQLBuilder("cmp_target_instpar"); //
			isql_2.putFieldValue("id", commDMO.getSequenceNextValByDS(null, "S_CMP_TARGET_INSTPAR")); //主键!
			isql_2.putFieldValue("target_instid", str_inst_id); //
			isql_2.putFieldValue("parname", hvs_par[i].getStringValue("parname")); //
			//isql_2.putFieldValue("parvalue", "");  //初始时没有指标值!!
			al_sqls.add(isql_2.getSQL()); //
		}

		commDMO.executeBatchByDS(null, al_sqls); //插入数据库!!
		return str_inst_id; //
	}

	/**
	 * 执行指标演算!
	 * @param _instId
	 * @throws Exception
	 */
	public String execTargetCompute(String _instId) throws Exception {
		Thread.currentThread().sleep(3000); //
		int li_value = 30 + new Random().nextInt(60); //先取随机数!
		CommDMO commDMO = new CommDMO(); //
		commDMO.executeUpdateByDS(null, "update cmp_target_inst set targetvalue='" + li_value + "' where id='" + _instId + "'"); //执行
		return "" + li_value; //
	}

}
