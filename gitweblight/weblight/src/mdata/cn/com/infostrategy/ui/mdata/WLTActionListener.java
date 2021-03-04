package cn.com.infostrategy.ui.mdata;

import java.util.EventListener;

/**
 * 所有按钮的执行的基类接口
 * @author xch
 *
 */
public interface WLTActionListener extends EventListener {

	public void actionPerformed(WLTActionEvent _event) throws Exception; //

}
