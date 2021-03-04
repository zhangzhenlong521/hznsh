package cn.com.infostrategy.ui.mdata.cardcomp;

import java.io.File;

import cn.com.infostrategy.ui.mdata.BillPanel;

public abstract class AbstractRefFileIntercept {
	/**
	 * �ϴ��ļ�ǰ ���ڵ����ļ�ѡ���ǰ
	 * @param billpanel
	 * @return
	 */
	abstract public boolean beforeUpLoad(BillPanel billpanel);
	
	/**
	 * �ϴ��ļ���
	 * @param billpanel
	 * @return
	 */
	abstract public boolean afterUpLoad(BillPanel billpanel,File[] allfiles);
	
	public boolean afterSelectFilesAndBeforUpload(BillPanel billPanel,File[] allfiles){
		return true;
	}
		
}
