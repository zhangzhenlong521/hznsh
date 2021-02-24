package cn.com.infostrategy.ui.workflow.design;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.AbstractConfirmListDialog;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.workflow.WorkFlowServiceIfc;

/**
 * ѡ��һ���������Ĵ���..
 * @author xch
 *
 */
public class OpenProcessDialog extends AbstractConfirmListDialog {

	private static final long serialVersionUID = 5617791666562892394L;
	protected String processID;
	protected String processCode;

	private WLTButton btn_delete, btn_copy = null; // 

	private String str_condition = null; //

	public OpenProcessDialog(Container _parent) {
		super(_parent);
	}

	public OpenProcessDialog(Container _parent, String _condition) {
		super(_parent);
		this.str_condition = _condition; //
	}

	public void loadData() {
		getBillListPanel().QueryDataByCondition(str_condition); //
	}

	public JButton[] getCustBtns() {
		btn_delete = new WLTButton("ɾ��");
		btn_copy = new WLTButton("��������"); //
		btn_delete.setPreferredSize(new Dimension(75, 20));
		btn_copy.setPreferredSize(new Dimension(75, 20));
		btn_delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onDelete(); //
			}
		}); //
		btn_copy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCopy(); //			
			}
		}); //
		return new JButton[] { btn_delete, btn_copy };
	}

	private void onDelete() {
		getBillListPanel().doDelete(false); //
	}

	//��������!
	private void onCopy() {
		if (!MessageBox.confirm(this, "������븴��������")) {
			return; //
		}
		try {
			BillVO billVO = getBillListPanel().getSelectedBillVO(); //
			if (billVO == null) {
				MessageBox.show(this, "��ѡ��һ����¼!"); //
				return; //
			}
			String str_id = billVO.getStringValue("id"); //
			String str_oldcode = billVO.getStringValue("code"); //
			String str_oldname = billVO.getStringValue("name"); //

			BillCardDialog dialog = new BillCardDialog(this, "��������ͼ", new String[] { "*�����̱���", "*����������" }, 430, 160); //
			dialog.setCardItemValue("�����̱���", str_oldcode + "_copy"); //
			dialog.setCardItemValue("����������", str_oldname + "_copy"); //
			dialog.setVisible(true); //

			if (dialog.getCloseType() == 1) {
				String str_newCode = dialog.getCardItemValue("�����̱���"); //
				String str_newName = dialog.getCardItemValue("����������"); //
				String str_count = UIUtil.getStringValueByDS(null, "select count(*) from pub_wf_process where code='" + str_newCode + "'"); //
				if (Integer.parseInt(str_count) > 0) {
					MessageBox.show(this, "����Ϊ[" + str_newCode + "]�������Ѵ���,��ʹ����������!!"); //
					return; //
				}
				WorkFlowServiceIfc wfservice = (WorkFlowServiceIfc) UIUtil.lookUpRemoteService(WorkFlowServiceIfc.class); //
				wfservice.copyWorkFlowProcess(str_id, str_newCode, str_newName); //
				MessageBox.show(this, "�������̳ɹ�,�µ����̱�����[" + str_newCode + "],�����²�ѯ!"); //
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	@Override
	public String getBillListTempletCode() {
		return "cn.com.infostrategy.bs.workflow.tmo.TMO_Pub_WF_Process_1"; //
	}

	@Override
	public AbstractTMO getAbstractTempletVO() {
		return null;
	}

	public String getInitTitle() {
		return "ѡ��һ������";
	}

	public int getInitWidth() {
		return 700;
	}

	public int getInitHeight() {
		return 500; //�߶�
	}

	@Override
	public void onBeforeConfirm() throws Exception {
		BillVO vo = getBillListPanel().getSelectedBillVO();
		if (vo != null) {
			processID = vo.getStringValue("ID"); //
			processCode = vo.getStringValue("CODE"); //
		}
	}

	//�ر�ǰ���Ķ���!!
	@Override
	public void onBeforeCancel() throws Exception {
	}

	public boolean isShowPageNavigation() {
		return false;
	}

	public boolean isShowOperatorNavigation() {
		return false;
	}

	public String getProcessID() {
		return processID;
	}

	public String getProcessCode() {
		return processCode;
	}

	public void setProcessCode(String processCode) {
		this.processCode = processCode;
	}

}