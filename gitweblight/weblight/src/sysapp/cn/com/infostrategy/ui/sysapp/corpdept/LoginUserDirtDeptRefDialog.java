package cn.com.infostrategy.ui.sysapp.corpdept;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;
import cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc;

/**
 * ��¼��Ա����ֱ�ӻ�����.
 * @author xch
 *
 */
public class LoginUserDirtDeptRefDialog extends AbstractRefDialog implements ActionListener {

	private BillTreePanel billTreePanel = null; //
	private WLTButton btn_confirm, btn_cancel;

	private RefItemVO currRefItemVO = null; //

	private JLabel label = new JLabel(); //

	public LoginUserDirtDeptRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
		super(_parent, _title, refItemVO, panel);
		currRefItemVO = refItemVO; //
	}

	@Override
	public void initialize() {
		this.getContentPane().setLayout(new BorderLayout()); //
		billTreePanel = new BillTreePanel("pub_corp_dept_CODE1"); //

		this.getContentPane().add(label, BorderLayout.NORTH); //
		this.getContentPane().add(billTreePanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //

		label.setVisible(false);
		try {
			SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
			String str_loginuserdeptid = ClientEnvironment.getCurrLoginUserVO().getBlDeptId(); //
			if (str_loginuserdeptid == null || str_loginuserdeptid.trim().equals("")) {
				label.setVisible(true);
				label.setForeground(Color.RED); //
				label.setText("��¼��Ա����������Ϊ��!");//
				return;
			}

			String[] str_alldeptids = service.getOneDeptDirtDepts(str_loginuserdeptid); //ȡ�����л���
			billTreePanel.queryDataByCondition("id in (" + new TBUtil().getInCondition(str_alldeptids) + ")"); //��ѯ���л�����.
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}

		billTreePanel.myExpandAll(); //ȫ��չ��
	}

	@Override
	public RefItemVO getReturnRefItemVO() {
		return currRefItemVO;
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		btn_confirm = new WLTButton("ȷ��");
		btn_cancel = new WLTButton("ȡ��");
		btn_confirm.addActionListener(this); //
		btn_cancel.addActionListener(this); //
		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		}
	}

	@Override
	public int getInitHeight() {
		return 275;
	}

	@Override
	public int getInitWidth() {
		return 300;
	}

	private void onConfirm() {
		BillVO billVO = billTreePanel.getSelectedVO(); //
		if (billVO == null) {
			MessageBox.show(this, "��ѡ��һ������!"); //
			return; //
		}

		String str_corptype = billVO.getStringValue("corptype");
		if (str_corptype == null || str_corptype.trim().equals("")) {
			MessageBox.show(this, "ѡ�л����Ļ�������Ϊ��,�������й���Ա��ϵ!"); //
			return;
		}

		if (str_corptype.endsWith("_��������")) {
			if (JOptionPane.showConfirmDialog(this, "ѡ�е���һ�����Ҽ��Ļ���,��ͳ�ƽǶȽ�Ӧ������ѡ��һ�����ż��Ļ���,����ͳ�����ݾͿ��ܲ���!\r\n��ȷ���Ƿ����ѡ��û���??", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return; //
			}
		}

		HashVO hvo = billVO.convertToHashVO(); //��BillVOת����һ��HashVO
		RefItemVO refVO = new RefItemVO(hvo); //
		refVO.setId(billVO.getStringValue("id")); //
		refVO.setCode(billVO.getStringValue("code")); //
		refVO.setName(billVO.getStringValue("name")); //
		currRefItemVO = refVO; //
		setCloseType(BillDialog.CONFIRM); //
		this.dispose();
	}

	private void onCancel() {
		setCloseType(BillDialog.CANCEL); //
		this.dispose(); //
	}

}
