package cn.com.infostrategy.ui.workflow.engine;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

/**
 * ������ǵĲ��Ƕ������!! ֮���������Զ������,����Ϊ���������߼��Ƚϸ���
 * @author xch
 *
 */
public class WFYiJianBuDengObjRefDialog extends AbstractRefDialog implements ActionListener {

	private BillListPanel billList = null; //
	private WLTButton btn_confirm, btn_cancel; //
	private RefItemVO returnRefVO = null; //

	public WFYiJianBuDengObjRefDialog(Container _parent, String _title, RefItemVO _initRefItemVO, BillPanel _panel) {
		super(_parent, _title, _initRefItemVO, _panel);
	}

	@Override
	public void initialize() {
		billList = new BillListPanel("pub_user_1"); //
		try {
			HashMap map = UIUtil.commMethod("cn.com.infostrategy.bs.workflow.WorkFlowBSUtil", "getBDObj", null); //
			HashVO[] hvs_data = (HashVO[]) map.get("vos"); //
			if (hvs_data != null) {
				billList.putValue(hvs_data); //

			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
		this.getContentPane().add(billList); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout()); //
		btn_confirm = new WLTButton("ȷ��"); //
		btn_cancel = new WLTButton("ȡ��"); //
		btn_confirm.addActionListener(this); //
		btn_cancel.addActionListener(this); //
		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel; //
	}

	@Override
	public RefItemVO getReturnRefItemVO() {
		return returnRefVO;
	}

	/**
	 * ���ȷ����ť
	 */
	private void onConfirm() {
		BillVO billVO = billList.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, "��ѡ��һ����¼���в���!"); //
			return;
		}
		String str_id = billVO.getStringValue("userid"); //
		String str_code = billVO.getStringValue("usercode"); //
		String str_name = billVO.getStringValue("username"); //
		returnRefVO = new RefItemVO(str_id, str_code, str_name); //
		this.setCloseType(BillDialog.CONFIRM); //
		this.dispose();
	}

	/**
	 * ���ȡ����ť
	 */
	private void onCancel() {
		returnRefVO = null; //
		this.setCloseType(BillDialog.CANCEL); //
		this.dispose();
	}

	public void actionPerformed(ActionEvent _evt) {
		if (_evt.getSource() == btn_confirm) {
			onConfirm();
		} else if (_evt.getSource() == btn_cancel) {
			onCancel(); //
		}
	}

	@Override
	public int getInitWidth() {
		return 800;
	}

	@Override
	public int getInitHeight() {
		return 400;
	}

}
