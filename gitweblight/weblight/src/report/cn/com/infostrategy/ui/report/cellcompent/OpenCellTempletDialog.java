package cn.com.infostrategy.ui.report.cellcompent;

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
import cn.com.infostrategy.ui.report.ReportServiceIfc;

public class OpenCellTempletDialog extends AbstractConfirmListDialog {

	private static final long serialVersionUID = 152478771517858180L;

	protected String celltempletcode, celltempletname = null;

	public OpenCellTempletDialog(Container _parent) {
		super(_parent); //
	}

	public void initialize() {
		getBillListPanel().QueryDataByCondition("billno is null"); //
	}

	public AbstractTMO getAbstractTempletVO() {
		return new CellTMO();
	}

	public JButton[] getCustBtns() {
		WLTButton btn_delete = new WLTButton("ɾ��");
		btn_delete.setPreferredSize(new Dimension(75, 20));
		btn_delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onDelete();
			}
		});
		return new JButton[] { btn_delete };
	}

	private void onDelete() {
		try {
			BillVO vo = getBillListPanel().getSelectedBillVO();
			if (vo == null) {
				return;
			}

			String str_id = vo.getStringValue("ID"); //
			String str_code = vo.getStringValue("templetcode"); //
			String str_name = vo.getStringValue("templetname"); //
			if (!MessageBox.confirm(this, "��ȷ��Ҫɾ������ģ��[" + str_code + "/" + str_name + "]��?")) {//�����/2012-04-12��
				return;
			}
			ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class); //
			service.deleteOneBillCellTemplet(str_id); //
			getBillListPanel().removeRow(); //
		} catch (Exception e) {
			MessageBox.showException(this, e); //
		}
	}

	public String getInitTitle() {
		return "ѡ��һ������ģ��";
	}

	public int getInitWidth() {
		return 800; //
	}

	public int getInitHeight() {
		return 600; //��ǰ����Ϊ500���߶��е�ͣ�ÿ�λ��ù��������������޸ĸ߶ȡ����/2018-12-12��
	}

	public void onBeforeConfirm() {
		BillVO billVO = getBillListPanel().getSelectedBillVO();
		if (billVO == null) {
			MessageBox.show(this, "Please select a record"); //
			return; ////
		}
		this.celltempletcode = billVO.getStringValue("templetcode");
		this.celltempletname = billVO.getStringValue("templetname");
	}

	public void onBeforeCancel() {
	}

	public String getCellTempletcode() {
		return celltempletcode;
	}

	public String getCellTempletname() {
		return celltempletname;
	}
}
