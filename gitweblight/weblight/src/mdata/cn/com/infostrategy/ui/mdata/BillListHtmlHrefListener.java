package cn.com.infostrategy.ui.mdata;

import java.util.EventListener;

/**
 * 列表超链接监听器
 * 所谓超链接是指列表中有些列像Html中的超链接一样,有个下划线,然后点击这个超链接可以进行一些操作,比如打开一个窗口,查看其详细信息!!!
 * @author xch
 *
 */
public interface BillListHtmlHrefListener extends EventListener {

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event); //点击列表按表动作

}
