package cn.com.infostrategy.ui.workflow.engine;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Toolkit;

import javax.swing.JOptionPane;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.BillFrame;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 工作流处理框,民生客户提出的需求，想要在一个页面上进行!!
 * @author xch
 *
 */
public class WorkFlowProcessFrame extends BillFrame {

	private static final long serialVersionUID = 1L;
	private WorkFlowProcessPanel wfProcessPanel = null; // 
	private Container parent;
	private BillCardPanel cardPanel;
	private BillListPanel listPanel;

	private String taskId = null; //任务Id
	private String prDealPoolId = null; //流程任务id
	private String prInstanceId = null; //流程实例Id

	private boolean isOnlyView = false; //是否只能查看而不能处理!!
	private String str_OnlyViewReason = null; //只能查看的原因,比如是困为抄送的,还是被人抢占了等等...

	/**
	 * 构造方法..
	 * @param _parent
	 * @param _title
	 */
	public WorkFlowProcessFrame(Container _parent, String _title, BillCardPanel _cardPanel, BillListPanel _listPanel) {
		this(_parent, _title, _cardPanel, _listPanel, null, null, null, false, null); //
	}

	public WorkFlowProcessFrame(Container _parent, String _title, BillCardPanel _cardPanel, BillListPanel _listPanel, String _taskId, String _prDealPoolId, String _prInstanceId, boolean _isOnlyView, String _onlyViewReason) {
		super(_parent, _title);
		this.parent = _parent;
		this.cardPanel = _cardPanel;
		this.listPanel = _listPanel;
		this.taskId = _taskId; //任务Id
		this.prDealPoolId = _prDealPoolId; //流程任务的主键!!
		this.prInstanceId = _prInstanceId; //流程实例Id
		this.isOnlyView = _isOnlyView;
		this.str_OnlyViewReason = _onlyViewReason; //只能查看的原因!!!
		init();

	}

	public void init() {
		int li_width = (int) cardPanel.getPreferredSize().getWidth(); //
		int li_height = (int) cardPanel.getPreferredSize().getHeight(); //卡片的高度
		if (li_width < 727) { //因为历史处理意见的宽度就是727,必须大于它!
			li_width = 727;
		}

		li_width = li_width + 100;
		li_height = li_height + 375; //

		int li_screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 60; //屏幕大小
		int li_screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 60; //屏幕大小

		if (li_width > li_screenWidth) {
			li_width = li_screenWidth;
		}
		if (li_width < 715) { //宽度不要小于715,否则太小气!
			li_width = 715;
		}

		if (li_height > li_screenHeight) { //如果高度超过了屏幕大小,则以屏幕大小为上限
			li_height = li_screenHeight;
		}
		if (li_height < 500) { //如果小于500了,则以500为下限!
			li_height = 500;
		}
		
		//追加工作流处理多页签方式 改变窗口大小 【杨科/2013-04-08】
		if (new TBUtil().getSysOptionBooleanValue("工作流处理界面是否为多页签", false)&&isOnlyView) {
			li_width = 700;
			li_height = 600; 
		}

		this.setSize(li_width, li_height); //

		Frame frame = JOptionPane.getFrameForComponent(parent);
		double ld_width = frame.getSize().getWidth();
		double ld_height = frame.getSize().getHeight();
		double ld_x = frame.getLocation().getX();
		double ld_y = frame.getLocation().getY();

		int ld_thisX = (int) (ld_x + ld_width / 2 - li_width / 2);
		int ld_thisY = (int) (ld_y + ld_height / 2 - li_height / 2);

		if (ld_thisX < 0) {
			ld_thisX = 0; //
		}
		if (ld_thisY < 0) {
			ld_thisY = 0; //
		}

		this.setLocation(ld_thisX, ld_thisY); //
		this.getContentPane().setLayout(new BorderLayout()); //
		wfProcessPanel = new WorkFlowProcessPanel(this, cardPanel, listPanel, taskId, prDealPoolId, prInstanceId, isOnlyView, str_OnlyViewReason); //
		//高峰曾经在这弄了个拦截器IWorkflowProcessFrameIfc,但后来发现与processpanel中的重叠,所以去掉了!! xch 2012-02-22
		this.getContentPane().add(wfProcessPanel);
	}

	
	@Override
	public int getCloseType() {
		return wfProcessPanel.getCloseType(); //
	}

	public WorkFlowProcessPanel getWfProcessPanel() {
		return wfProcessPanel;
	}

}
