package cn.com.infostrategy.ui.mdata;

import java.util.EventListener;

/**
 * 该事件主要为勾选框模式时点击表头的勾选框的监听
 * @author yuanjiangxiao
 * 20131029
 */
public interface BillListCheckedAllListener extends EventListener {
	public void onBillListCheckedAll(BillListCheckedAllEvent _event);//选中的是哪一行
}
