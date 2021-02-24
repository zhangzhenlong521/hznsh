package cn.com.infostrategy.ui.mdata.cardcomp;

import java.io.File;

import cn.com.infostrategy.ui.mdata.BillPanel;

public abstract class AbstractRefFileIntercept {
	/**
	 * 上传文件前 即在弹出文件选择框前
	 * @param billpanel
	 * @return
	 */
	abstract public boolean beforeUpLoad(BillPanel billpanel);
	
	/**
	 * 上传文件后
	 * @param billpanel
	 * @return
	 */
	abstract public boolean afterUpLoad(BillPanel billpanel,File[] allfiles);
	
	public boolean afterSelectFilesAndBeforUpload(BillPanel billPanel,File[] allfiles){
		return true;
	}
		
}
