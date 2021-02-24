package cn.com.infostrategy.ui.workflow.engine;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTHashMap;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;

/**
 * 工作流引擎UI端拦截器!!!
 * 以前的工作流拦截器有点乱,一共有6个,即平台原来有两个(UI端环节处理,BS端),后来高峰加了两个(UI端流程处理,UI端流程结束后),刘旋飞又加了两个(UI端流程处理,UI端环节处理)!
 * 后来觉得，总是感觉需要新加方法，如果是新加拦截器类则觉得有点乱,而且维护不方便,也没必要!!! 所以应该是继承抽象类更好!!! 也可以是先搞一个接口,再弄一个默认适配器的抽象类,但怕二次开发人员不用抽象类,所以干脆只弄一个抽象类!!
 * 以前拦截器是接口,但后来发现,项目中的类继承接口后,如果这时平台再加新方法,则项目代码都编译不过了,所以应该弄成抽象类!!!
 * 这个方法以后可以不断增加新的方法!!!而又不影响项目中的代码!!!
 * 
 * 这里面有多个方法,但每个方法只会被注册在某唯一的地方才会被调用!!即相互相交叉错开的!!
 * 一般来说现在有三个地方可以注册,一是流程,一是环节,一是连线!!
 * 如果流程与环节都注册了,且实现了各自方法与逻辑,则会依次调用两个方法!!!
 * 
 * 为什么要将流程拦截器弄成一个类中的各个方法,而不是各个类呢??? 原因是扩展时会降低代码改动量! 如果搞成各个类,则必须多一个注册的地方,则需要数据库加字段,修改XML模板,修改加载与保存地方的逻辑,总之改动一大堆地方!!!
 * 而弄成一个类,然后还是抽象类,这样扩展时非常方便!!!
 * @author xch
 *
 */
public abstract class WorkFlowEngineUIIntercept {

	/**
	 * 打开处理面后,只有注册在流程中,这个方法才会被调用!!!
	 * @param _processPanel
	 * @throws Exception
	 */
	public void afterOpenWFProcessPanel(cn.com.infostrategy.ui.workflow.engine.WorkFlowProcessPanel _processPanel, String _billtype, String _busitype, BillVO _billVO, HashVO _currActivity, WLTHashMap _otherParMap) throws Exception {
		//为什么要在这输出这段提示?一是提醒这个函数在什么时候被执行了,二是提醒开发人员重构或防止重构方法改名了(无论是基类改了,还是子类改了),强烈建议子类重构方法上的@overwrite不要去掉,否则非常容易出现改方法名却不知情的问题!!
		//换句话说就是,如果开发人员定义了拦截器,却没有重构正确的方法,则就控制台就会输出这种警告,提醒开发人员...
		System.out.println("你已定义了流程UI端拦截器[" + this.getClass().getName() + "],系统执行了基类[WorkFlowEngineUIIntercept]的afterOpenWFProcessPanel()方法,理论上你应该重构该方法!!或者重构的方法被改名了??"); //
	}

	/**
	 * 点击提交按钮前的校验,针对于整个流程的!
	 * @param _processPanel
	 * @param _currActivity 
	 * @param _billvo 
	 * @throws Exception
	 */
	public int beforeSubmitWFProcessPanel(cn.com.infostrategy.ui.workflow.engine.WorkFlowProcessPanel _processPanel, String _billtype, String _busitype, BillVO _billvo, HashVO _currActivity, WLTHashMap _otherParMap) throws Exception {
		System.out.println("你已定义了流程UI端拦截器[" + this.getClass().getName() + "],系统执行了基类[WorkFlowEngineUIIntercept]beforeSubmitWFProcessPanel()方法,理论上你应该重构该方法!!或者重构的方法被改名了??"); //
		return 0; //默认是正常返回!
	}

	/**
	 * 打开界面后,当前环节处理的逻辑!!!只有注册在环节中,这个方法才会被调用!!
	 * @param _processPanel
	 * @param _currActivity 
	 * @param _billvo 
	 * @throws Exception
	 */
	public void afterOpenWFProcessPanelByCurrActivity(cn.com.infostrategy.ui.workflow.engine.WorkFlowProcessPanel _processPanel, String _billtype, String _busitype, BillVO _billvo, HashVO _currActivity, WLTHashMap _otherParMap) throws Exception {
		System.out.println("你已定义了流程UI端拦截器[" + this.getClass().getName() + "],系统执行了基类[WorkFlowEngineUIIntercept]的afterOpenWFProcessPanelByCurrActivity()方法,理论上你应该重构该方法!!或者重构的方法被改名了??"); //
	}

	/**
	 * 点击提交按钮前的校验,比如校验是否某一项内容不对?
	 * @param _processPanel
	 * @param _currActivity 
	 * @param _billvo 
	 * @throws Exception
	 */
	public int beforeSubmitWFProcessPanelByCurrActivity(cn.com.infostrategy.ui.workflow.engine.WorkFlowProcessPanel _processPanel, String _billtype, String _busitype, BillVO _billvo, HashVO _currActivity, WLTHashMap _otherParMap) throws Exception {
		System.out.println("你已定义了流程UI端拦截器[" + this.getClass().getName() + "],系统执行了基类[WorkFlowEngineUIIntercept]的beforeSubmitWFProcessPanelByCurrActivity()方法,理论上你应该重构该方法!!或者重构的方法被改名了??"); //
		return 0; //默认是正常返回!
	}

	/**
	 * 流程结束后处理!!!，只有注册在流程中,这个方法才会被调用!!
	 * @param _processPanel
	 * @param _billvo
	 * @throws Exception
	 */
	public void afterWorkFlowEnd(WorkFlowProcessPanel _processPanel, String _billtype, String _busitype, BillVO _billvo, HashVO _currActivity, WLTHashMap _otherParMap) throws Exception {
		System.out.println("你已定义了流程UI端拦截器[" + this.getClass().getName() + "],系统执行了基类[WorkFlowEngineUIIntercept]的afterWorkFlowEnd()方法,理论上你应该重构该方法!!或者重构的方法被改名了??"); //
	}

	/**
	 * 自定义流程退回接收人。在整个流程中都执行
	 * @param _processPanel
	 * @param _billtype
	 * @param _busitype
	 * @param _billvo
	 * @param _currActivity
	 * @param _otherParMap
	 * @return
	 */
	public WFParVO onRejectCustSelectWRPar(cn.com.infostrategy.ui.workflow.engine.WorkFlowProcessPanel _processPanel, String _billtype, String _busitype, BillVO _billvo, HashVO _currActivity, WFParVO _firstTaskVO, WLTHashMap _otherParMap) {
		System.out.println("你已定义了流程UI端拦截器[" + this.getClass().getName() + "],系统执行了基类[WorkFlowEngineUIIntercept]的onRejectCustSelectWRPar()方法,理论上你应该重构该方法!!或者重构的方法被改名了??"); //
		return null;
	}

	/**
	 * 自定义流程打开选择人员面板后执行逻辑。在整个流程中都执行【李春娟/2016-04-20】
	 * @param _processPanel
	 * @param _billtype
	 * @param _busitype
	 * @param _billvo
	 * @param _currActivity
	 * @param _firstTaskVO
	 * @param _otherParMap
	 * @return
	 */
	public void afterOpenParticipateUserPanel(ParticipateUserPanel _userPanel, BillVO _billvo, HashVO _currActivity, WFParVO _firstTaskVO, WLTHashMap _otherParMap) {
		System.out.println("你已定义了流程UI端拦截器[" + this.getClass().getName() + "],系统执行了基类[WorkFlowEngineUIIntercept]的afterOpenParticipateUserPanel()方法,理论上你应该重构该方法!!或者重构的方法被改名了??"); //
	}

}
