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
 * 登录人员所属直接机构树.
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
				label.setText("登录人员的所属机构为空!");//
				return;
			}

			String[] str_alldeptids = service.getOneDeptDirtDepts(str_loginuserdeptid); //取得所有机构
			billTreePanel.queryDataByCondition("id in (" + new TBUtil().getInCondition(str_alldeptids) + ")"); //查询所有机构树.
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}

		billTreePanel.myExpandAll(); //全部展开
	}

	@Override
	public RefItemVO getReturnRefItemVO() {
		return currRefItemVO;
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");
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
			MessageBox.show(this, "请选择一个机构!"); //
			return; //
		}

		String str_corptype = billVO.getStringValue("corptype");
		if (str_corptype == null || str_corptype.trim().equals("")) {
			MessageBox.show(this, "选中机构的机构类型为空,请与总行管理员联系!"); //
			return;
		}

		if (str_corptype.endsWith("_下属机构")) {
			if (JOptionPane.showConfirmDialog(this, "选中的是一个科室级的机构,从统计角度讲应该至少选择一个部门级的机构,否则统计数据就可能不对!\r\n请确认是否真的选择该机构??", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return; //
			}
		}

		HashVO hvo = billVO.convertToHashVO(); //将BillVO转换成一个HashVO
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
