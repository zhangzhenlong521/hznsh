package com.pushworld.ipushlbs.ui.lfunctionmanage;

import cn.com.infostrategy.to.common.HashVO;
/**
 * 实现功能可配 未完成。
 * @author yinliang
 *
 */
public class FunctionManageImp implements FunctionManageInf {
	
	public String isPrintWater ;
	
	public static FunctionManageImp manageimp = null ;
	private FunctionManageImp(){
		
	}
	public static synchronized FunctionManageImp GetFuntionManageImp(){
		if(manageimp == null)
			manageimp = new FunctionManageImp();
		return manageimp ;
	}
	
	public void FunctionManage(HashVO[] hashvo){
		HashVO vo = null ;
		for(int i = 0 ; i < hashvo.length ; i ++){
			vo = hashvo[i];
			this.setIsPrintWater(vo.getStringValue("attvalue"));
		}
	}
	
	public void PrintWaterManage(String flag) {
		
	}

	public String getIsPrintWater() {
		return isPrintWater;
	}
	
	public void setIsPrintWater(String isPrintWater) {
		this.isPrintWater = isPrintWater;
	}
	
	
	
}
