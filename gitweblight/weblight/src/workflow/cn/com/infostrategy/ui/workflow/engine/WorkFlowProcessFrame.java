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
 * �����������,�����ͻ������������Ҫ��һ��ҳ���Ͻ���!!
 * @author xch
 *
 */
public class WorkFlowProcessFrame extends BillFrame {

	private static final long serialVersionUID = 1L;
	private WorkFlowProcessPanel wfProcessPanel = null; // 
	private Container parent;
	private BillCardPanel cardPanel;
	private BillListPanel listPanel;

	private String taskId = null; //����Id
	private String prDealPoolId = null; //��������id
	private String prInstanceId = null; //����ʵ��Id

	private boolean isOnlyView = false; //�Ƿ�ֻ�ܲ鿴�����ܴ���!!
	private String str_OnlyViewReason = null; //ֻ�ܲ鿴��ԭ��,��������Ϊ���͵�,���Ǳ�����ռ�˵ȵ�...

	/**
	 * ���췽��..
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
		this.taskId = _taskId; //����Id
		this.prDealPoolId = _prDealPoolId; //�������������!!
		this.prInstanceId = _prInstanceId; //����ʵ��Id
		this.isOnlyView = _isOnlyView;
		this.str_OnlyViewReason = _onlyViewReason; //ֻ�ܲ鿴��ԭ��!!!
		init();

	}

	public void init() {
		int li_width = (int) cardPanel.getPreferredSize().getWidth(); //
		int li_height = (int) cardPanel.getPreferredSize().getHeight(); //��Ƭ�ĸ߶�
		if (li_width < 727) { //��Ϊ��ʷ��������Ŀ�Ⱦ���727,���������!
			li_width = 727;
		}

		li_width = li_width + 100;
		li_height = li_height + 375; //

		int li_screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 60; //��Ļ��С
		int li_screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 60; //��Ļ��С

		if (li_width > li_screenWidth) {
			li_width = li_screenWidth;
		}
		if (li_width < 715) { //��Ȳ�ҪС��715,����̫С��!
			li_width = 715;
		}

		if (li_height > li_screenHeight) { //����߶ȳ�������Ļ��С,������Ļ��СΪ����
			li_height = li_screenHeight;
		}
		if (li_height < 500) { //���С��500��,����500Ϊ����!
			li_height = 500;
		}
		
		//׷�ӹ����������ҳǩ��ʽ �ı䴰�ڴ�С �����/2013-04-08��
		if (new TBUtil().getSysOptionBooleanValue("��������������Ƿ�Ϊ��ҳǩ", false)&&isOnlyView) {
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
		//�߷���������Ū�˸�������IWorkflowProcessFrameIfc,������������processpanel�е��ص�,����ȥ����!! xch 2012-02-22
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
