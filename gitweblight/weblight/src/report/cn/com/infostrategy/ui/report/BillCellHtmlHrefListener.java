package cn.com.infostrategy.ui.report;

import java.util.EventListener;

/**
 * Excel模板控件上的超链接监听器!!!即BillCellPanel上的超链接!!!
 * 
 * @author xch
 *
 */
public interface BillCellHtmlHrefListener extends EventListener {

	public void onBillCellHtmlHrefClicked(BillCellHtmlHrefEvent _event); //点击动作!!!

}
