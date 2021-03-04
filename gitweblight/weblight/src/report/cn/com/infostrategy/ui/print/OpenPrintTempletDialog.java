package cn.com.infostrategy.ui.print;

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

public class OpenPrintTempletDialog extends AbstractConfirmListDialog {

	private static final long serialVersionUID = 152478771517858180L;

	protected String printtempletcode;

	public OpenPrintTempletDialog(Container _parent) {
		super(_parent); //
	}

	public void initialize() {
		getBillListPanel().QueryDataByCondition(null); //
	}

	public AbstractTMO getAbstractTempletVO() {
		return null;
	}

	public String getBillListTempletCode() {
		return "pub_printtemplet_CODE1";
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

			String str_id = vo.getStringValue("id"); //
			String str_code = vo.getStringValue("templetcode"); //
			String str_name = vo.getStringValue("templetname"); //
			if (!MessageBox.confirm(this, "��ȷ��Ҫɾ����ӡģ��[" + str_code + "/" + str_name + "]��?")) {
				return;
			}
			ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class); //
			service.deleteOnePrintTemplet(str_id); //
			getBillListPanel().removeRow(); //
		} catch (Exception e) {
			MessageBox.showException(this, e); //
		}
	}

	public String getInitTitle() {
		return "ѡ��һ����ӡģ��";
	}

	public int getInitWidth() {
		return 800; //
	}

	public int getInitHeight() {
		return 500; //
	}

	public String getPrinttempletcode() {
		return printtempletcode;
	}

	public void onBeforeConfirm() {
		BillVO billVO = getBillListPanel().getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this); //
			return; ////
		}
		this.printtempletcode = billVO.getStringValue("templetcode");
	}

	public void onBeforeCancel() throws Exception {
	}
}
