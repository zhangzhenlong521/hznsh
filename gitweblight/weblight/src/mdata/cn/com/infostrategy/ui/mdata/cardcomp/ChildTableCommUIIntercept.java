package cn.com.infostrategy.ui.mdata.cardcomp;

import cn.com.infostrategy.ui.mdata.BillPanel;
/**
 * 导入子表,引用子表拦截器。需求来自与安徽联社。制度引用外规，子表点击应该直接弹出html预览！
 * 
 * @author hm
 *
 */
public interface ChildTableCommUIIntercept {
	public void afterInitialize(BillPanel _panel) throws Exception; //控件拦截器
}
