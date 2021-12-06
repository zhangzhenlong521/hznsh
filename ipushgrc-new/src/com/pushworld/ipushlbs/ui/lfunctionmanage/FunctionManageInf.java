package com.pushworld.ipushlbs.ui.lfunctionmanage;

import cn.com.infostrategy.to.common.HashVO;
/**
 * 实现功能可配 未完成。
 * @author yinliang
 *
 */
public interface FunctionManageInf{
	
	public void FunctionManage(HashVO[] hashvo);
	
	// 打印时是否加入水印
	public void PrintWaterManage(String isflag);
}
