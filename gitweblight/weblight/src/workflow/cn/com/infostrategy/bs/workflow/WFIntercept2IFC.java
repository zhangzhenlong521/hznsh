package cn.com.infostrategy.bs.workflow;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;

/**
 * 拦截器2，即第二次真正进行处理时的拦截器
 * 这两个方法都是在后台执行
 * @author xch
 *
 */
public interface WFIntercept2IFC {

	public void beforeAction(WFParVO _secondCallVO, String _loginuserid, BillVO _billVO, String _dealtype) throws Exception;

	public void afterAction(WFParVO _secondCallVO, String _loginuserid, BillVO _billVO, String _dealtype) throws Exception;

}
