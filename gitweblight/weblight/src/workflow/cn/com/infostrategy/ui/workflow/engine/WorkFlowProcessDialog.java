package cn.com.infostrategy.ui.workflow.engine;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Toolkit;
import java.util.Map;

import javax.swing.JOptionPane;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 工作流处理框,民生客户提出的需求，想要在一个页面上进行!!
 * @author xch
 *
 */
public class WorkFlowProcessDialog extends BillDialog {

	private static final long serialVersionUID = 7830873483653925134L;
	private WorkFlowProcessPanel wfProcessPanel = null; //
	private Container parent;
	private BillCardPanel cardPanel;
	private BillListPanel listPanel;

	private String taskId = null; //消息任务Id
	private String prDealPoolId = null; //流程任务id
	private String prInstanceId = null; //流程实例Id

	private boolean isOnlyView = false; //以前叫isCCTo(是否抄送),现在叫是否只能查看,即如果没有任务与抄送的合并了!!!
	private String str_OnlyViewReason = null; //只能查看的原因,比如是困为抄送的,还是被人抢占了等等...
	private boolean isyjbd = false;
	private String title;
	private boolean isStart = false;
	private Map<String, Object> map;

	/**
	 * 
	 * @param _parent
	 * @param _title
	 * @param _cardPanel
	 * @param _listPanel
	 */
	public WorkFlowProcessDialog(Container _parent, String _title, BillCardPanel _cardPanel, BillListPanel _listPanel) {
		this(_parent, _title, _cardPanel, _listPanel, null, null, null, false, null); //
	}

	/**
	 * 构造方法..
	 * @param _parent
	 * @param _title
	 */
	public WorkFlowProcessDialog(Container _parent, String _title, BillCardPanel _cardPanel, BillListPanel _listPanel, String _taskId, String _prDealPoolId, String _prInstanceId, boolean _isOnlyView, String _onlyViewReason) {
		this(_parent, _title, _cardPanel, _listPanel, _taskId, _prDealPoolId, _prInstanceId, _isOnlyView, _onlyViewReason, false);
	}

	public WorkFlowProcessDialog(Container _parent, String _title, BillCardPanel _cardPanel, BillListPanel _listPanel, boolean isStart) {
		this(_parent, _title, _cardPanel, _listPanel, null, null, null, false, null, isStart, null); //
	}

	public WorkFlowProcessDialog(Container _parent, String _title, BillCardPanel _cardPanel, BillListPanel _listPanel, String _taskId, String _prDealPoolId, String _prInstanceId, boolean _isOnlyView, String _onlyViewReason, boolean isStart, Map<String, Object> _map) {
		this(_parent, _title, _cardPanel, _listPanel, _taskId, _prDealPoolId, _prInstanceId, _isOnlyView, _onlyViewReason, false, isStart, _map);
	}

	public WorkFlowProcessDialog(Container _parent, String _title, BillCardPanel _cardPanel, BillListPanel _listPanel, String _taskId, String _prDealPoolId, String _prInstanceId, boolean _isOnlyView, String _onlyViewReason, boolean isyjbd_) {
		super(_parent, _title);
		this.parent = _parent;
		this.cardPanel = _cardPanel;
		this.listPanel = _listPanel;
		this.taskId = _taskId; //消息任务Id
		this.prDealPoolId = _prDealPoolId; ///流程任务Id
		this.prInstanceId = _prInstanceId; //流程实例Id
		this.isOnlyView = _isOnlyView;
		this.str_OnlyViewReason = _onlyViewReason; //只能查看的原因!!
		this.isyjbd = isyjbd_;
		init();
	}

	public WorkFlowProcessDialog(Container _parent, String _title, BillCardPanel _cardPanel, BillListPanel _listPanel, String _taskId, String _prDealPoolId, String _prInstanceId, boolean _isOnlyView, String _onlyViewReason, boolean isyjbd_, boolean isStart, Map<String, Object> _map) {
		super(_parent, _title);
		this.parent = _parent;
		this.title = _title;
		this.cardPanel = _cardPanel;
		this.listPanel = _listPanel;
		this.taskId = _taskId; //消息任务Id
		this.prDealPoolId = _prDealPoolId; ///流程任务Id
		this.prInstanceId = _prInstanceId; //流程实例Id
		this.isOnlyView = _isOnlyView;
		this.str_OnlyViewReason = _onlyViewReason; //只能查看的原因!!
		this.isyjbd = isyjbd_;
		this.isStart = isStart;
		this.map = _map;
		init();
	}

	public void init() {
		int li_width = (int) cardPanel.getPreferredSize().getWidth(); //
		int li_height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 60; //

		li_width = 900;
		li_height = li_height + 375; //

		int li_screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 60; //屏幕大小
		int li_screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 60; //屏幕大小

		if (li_width > li_screenWidth) {
			li_width = li_screenWidth;
		}
		if (li_width < 735) { //宽度不要小于715,否则太小气!
			li_width = 735;
		}

		if (li_height > li_screenHeight) { //如果高度超过了屏幕大小,则以屏幕大小为上限
			li_height = li_screenHeight;
		}

		if (li_height < 500) { //如果小于500了,则以500为下限!
			li_height = 500;
		}

		//追加工作流处理多页签方式 改变窗口大小 【杨科/2013-04-08】
		if (new TBUtil().getSysOptionBooleanValue("工作流处理界面是否为多页签", false) && !isyjbd) {
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
		cardPanel.putClientProperty("Container", this);//设置Container【李春娟/2018-12-14】
		wfProcessPanel = new WorkFlowProcessPanel(this, cardPanel, listPanel, taskId, prDealPoolId, prInstanceId, isOnlyView, str_OnlyViewReason, isyjbd); ////
		this.getContentPane().add(wfProcessPanel, BorderLayout.CENTER);
	}

	public int getCloseType() {
		return wfProcessPanel.getCloseType(); //
	}

	public WorkFlowProcessPanel getWordFlowProcessPanel() {
		return wfProcessPanel;
	}

}
