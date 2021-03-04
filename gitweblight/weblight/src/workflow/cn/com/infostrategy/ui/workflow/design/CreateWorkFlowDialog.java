package cn.com.infostrategy.ui.workflow.design;

import java.awt.Container;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.workflow.design.TMO_Process;
import cn.com.infostrategy.ui.mdata.AbstractConfirmCardDialog;

public class CreateWorkFlowDialog extends AbstractConfirmCardDialog {
	private static final long serialVersionUID = -6764176585232903539L;
	private String processCode, processName, deptId;
	private String title;

	public CreateWorkFlowDialog(Container _parent) {
		super(_parent);
		setInitValue("0", "", "");
	}

	public String getTempletCode() {
		return TMO_Process.class.getName();
	}

	public String getTitle() {
		if (title == null) {
			return "�½�������";
		}
		return title;
	}

	public void setTitle(String _title) {
		this.title = _title;
	}

	public int getInitWidth() {//��ǰ��getWidth()��getHeight()��д�˸��෽����������ڱ��󣬻�ÿ�͸߶����䣬��ʹ���������ֿհס����/2012-03-15��
		return 350;
	}

	public int getInitHeight() {
		return 200;//���������˸����ţ����Ը߶�Ҫ��΢����һ�㡾���/2012-03-15��
	}

	public void onCancel() {
	}

	public void onConfirm() {
		BillVO vo = getBilCardPanel().getBillVO();
		processCode = vo.getStringValue("CODE");
		processName = vo.getStringValue("NAME");
		deptId = vo.getStringValue("USERDEF01");
	}

	public String getProcessCode() {
		return processCode;
	}

	public String getProcessName() {
		return processName;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setInitValue(String _processId, String _processCode, String _processName) {
		BillVO vo = getBilCardPanel().getBillVO();
		vo.setObject("ID", new StringItemVO(_processId));
		vo.setObject("CODE", new StringItemVO(_processCode));
		vo.setObject("NAME", new StringItemVO(_processName));
		getBilCardPanel().setBillVO(vo);
		getBilCardPanel().setVisiable("USERDEF01", false);//Ĭ�����ò���ʾ�����/2012-03-15��
	}
}