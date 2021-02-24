package cn.com.infostrategy.bs.workflow;

import cn.com.infostrategy.to.common.WLTHashMap;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;

/**
 * 工作流引擎BS端执行的拦截器!!!
 * 这里面有多个方法,但每个方法只会被注册在某唯一的地方才会被调用!!即相互相交叉错开的!!
 * 一般来说现在有三个地方可以注册,一是流程,一是环节,一是连线!!
 * 如果流程与环节都注册了,且实现了各自方法与逻辑,则会依次调用两个方法!!!
 * 
 * 为什么要将流程拦截器弄成一个类中的各个方法,而不是各个类呢??? 原因是扩展时会降低代码改动量! 如果搞成各个类,则必须多一个注册的地方,则需要数据库加字段,修改XML模板,修改加载与保存地方的逻辑,总之改动一大堆地方!!!
 * 而弄成一个类,然后还是抽象类,这样扩展时非常方便!!!
 * @author xch
 *
 */
public abstract class WorkFlowEngineBSIntercept {

	/**
	 * 某个环节执行前的处理,只有注册在环节中,该方法才会被调用！！
	 * @param _billType
	 * @param _busiType
	 * @param _secondCallVO
	 * @param _loginuserid
	 * @param _billVO
	 * @param _dealtype
	 * @throws Exception
	 */
	public void beforeActivityAction(String _billType, String _busiType, WFParVO _secondCallVO, String _loginuserid, BillVO _billVO, String _dealtype, WLTHashMap _parMap) throws Exception {
		//为什么要在这输出这段提示?一是提醒这个函数在什么时候被执行了,二是提醒开发人员重构或防止重构方法改名了(无论是基类改了,还是子类改了),强烈建议子类重构方法上的@overwrite不要去掉,否则非常容易出现改方法名却不知情的问题!!
		//换句话说就是,如果开发人员定义了拦截器,却没有重构正确的方法,则就控制台就会输出这种警告,提醒开发人员...
		System.out.println("你已定义了流程BS端拦截器[" + this.getClass().getName() + "],系统执行了基类[WorkFlowEngineBSIntercept]的beforeActivityAction()方法,理论上你应该重构该方法!!或者重构的方法被改名了??"); //
	}

	/**
	 * 某个一切执行后的处理,只有注册在环节中,该方法才会被调用！！
	 * @param _billType
	 * @param _busiType
	 * @param _secondCallVO
	 * @param _loginuserid
	 * @param _billVO
	 * @param _dealtype
	 * @throws Exception
	 */
	public void afterActivityAction(String _billType, String _busiType, WFParVO _secondCallVO, String _loginuserid, BillVO _billVO, String _dealtype, WLTHashMap _parMap) throws Exception {
		System.out.println("你已定义了流程BS端拦截器[" + this.getClass().getName() + "],系统执行了基类[WorkFlowEngineBSIntercept]的afterActivityAction()方法,理论上你应该重构该方法!!或者重构的方法被改名了??"); //
	}

	/**
	 * 某个连线执行的处理! 只有注册在连线中,该方法才会被调用！！
	 * @param _billType
	 * @param _busiType
	 * @param _secondCallVO
	 * @param _loginuserid
	 * @param _billVO
	 * @param _dealtype
	 * @throws Exception
	 */
	public void afterTransitionAction(String _billType, String _busiType, WFParVO _secondCallVO, String _loginuserid, BillVO _billVO, String _dealtype, WLTHashMap _parMap) throws Exception {
		System.out.println("你已定义了流程BS端拦截器[" + this.getClass().getName() + "],系统执行了基类[WorkFlowEngineBSIntercept]的afterTransitionAction()方法,理论上你应该重构该方法!!或者重构的方法被改名了??"); //
	}

	/**
	 * 整个流程结束后执行的逻辑,只有注册在流程中该方法才会被调用！！
	 * @param _billType
	 * @param _busiType
	 * @param _secondCallVO
	 * @param _loginuserid
	 * @param _billVO
	 * @param _dealtype
	 * @throws Exception
	 */
	public void afterWorkFlowEnd(String _billType, String _busiType, WFParVO _secondCallVO, String _loginuserid, BillVO _billVO, String _dealtype, WLTHashMap _parMap) throws Exception {
		System.out.println("你已定义了流程BS端拦截器[" + this.getClass().getName() + "],系统执行了基类[WorkFlowEngineBSIntercept]的afterWorkFlowEnd()方法,理论上你应该重构该方法!!或者重构的方法被改名了??"); //
	}

	

}
