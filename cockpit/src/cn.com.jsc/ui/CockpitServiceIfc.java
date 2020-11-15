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

}
