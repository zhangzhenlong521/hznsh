package cn.com.jsc.ui;

import cn.com.infostrategy.ui.common.WLTRemoteCallServiceIfc;

/**
 * zzl
 * 数据可视化接口
 */
public interface CockpitServiceIfc extends WLTRemoteCallServiceIfc {
    /**
     * zzl 存款总余额统计
     * @return
     */
    public String [] [] getCkStatistical();
    /**\
     * zzl 存款户数统计没有对公
     *
     */
    public  String [] [] getCKHsCount();

    /**
     * zzl 个人活期存款余额较年初
     * @return
     */
    public  String [] [] getCKGeRenCount();

    /**
     * zzl 个人定期存款余额较年初
     * @return
     */
    public  String [] [] getCKGeRenDQCount();

    /**
     * zzl 对公活期存款
     * @return
     */
    public  String [] [] getCKDgHqCount();

    /**
     * zzl 对公定期存款
     * @return
     */
    public  String [] [] getCKDgDqCount();
    /**
     * zpy 本年新增黔农云户数
     * @return
     */
	public int getCurYearQnyhs();
	/**
	 * zpy 计算本年黔农云活跃力
	 * @return
	 */
	public String getCurrYearQnyhyl();
	/**
	 * zpy  计算黔农e贷签约
	 * @return
	 */
	public int getCurrYearQned();
	/**
	 * zpy 计算黔农E贷线上占比
	 * @return
	 */
	public String getCurrYearQnedXszb();
	/**
	 * zzl 客户经理营销存款排名
	 */
	public  String [] [] getCKRanking();
	/**
	 * zzl 全行存款增长目标完成情况
	 */
    public String [] [] getQhCkCompletion();
	/**
	 * zzl 全行存款增长目标完成情况
	 */
	public String [] [] getQhCkCompletion2();
	/**
	 * zzl 各支行存款完成情况
	 */
	public String [] [] getgzhWcqingkuang();
	/**
	 * zzl 全行贷款完成情况
	 *
	 */
	public String [] [] getqhDkWcCount();
	/**
	 * zzl 全行贷款完成情况
	 *
	 */
	public String [] [] getDKgzhWcqingkuang();
	/**
	 * zzl 全行农户贷款覆盖面
	 *
	 */
	public String [] [] getNhFgMian();
	/**
	 * zzl 贷款总余额统计
	 * @return
	 */
	public String [] [] getDkStatistical();
	/**
	 * zzl 贷款总户数统计
	 * @return
	 */
	public String [] [] getDkStatisticalHs();
	/**
	 * zzl 贷款便民快贷户数统计
	 * @return
	 */
	public String [] [] getBmDkStatisticalHs();
	/**
	 * zzl 贷款个体工商户数统计
	 * @return
	 */
	public String [] [] getGtDkStatisticalHs();
	/**
	 * zzl 新动力统计 就是其他贷款
	 * @return
	 */
	public String [] [] getXdlDkStatisticalHs();
	/**
	 * zzl 农户贷款
	 * @return
	 */
	public String [] [] getnhDkStatisticalHs();
	/**
	 * zzl 客户经理营销贷款排名
	 */
	public  String [] [] getDKRanking();
	/**
	 * zzl 60不良贷款总额
	 */
	public  String [] [] getBlDKCount();
	/**
	 * zzl 90不良贷款总额
	 */
	public  String [] [] getBlDKCount2();
	/**
	 * zzl 不良贷款总额
	 */
	public  String [] [] getBlDKLvCount();
	/**
	 * zzl 收回不良贷款
	 */
	public  String [] [] getShBlDKLvCount();
	/**
	 * zzl 本月新增不良贷款
	 */
	public  String [] [] getxzBlDKLvCount();
	/**
	 * zzl 本月收回表外不良贷款
	 */
	public  String [] [] getShBwBlDKLvCount();
	/**
	 * zzl 本月新增表外不良贷款
	 */
	public  String [] [] getxzBwBlDKLvCount();
	/**
	 * zzl 本月客户经理清收不良贷款排名
	 */
	public  String [] [] getqsBlDKLvCount();
	/**
	 * zzl 全行不良贷款情况
	 */
	public  String [] [] getqhBlDKLvCount();
	/**
	 * zzl 各支行不良贷款完成情况
	 */
	public  String [] [] getgzhBlDKLvCount();
	/**
	 * zzl 营销黔农云排名
	 */
	public  String [] [] getQnyDKLvCount();
	/**
	 * zzl 黔农E贷排名
	 */
	public  String [] [] getQnyEDKLvCount();
}
