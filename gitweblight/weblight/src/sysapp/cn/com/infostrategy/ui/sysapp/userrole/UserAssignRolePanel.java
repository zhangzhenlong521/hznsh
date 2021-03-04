package cn.com.infostrategy.ui.sysapp.userrole;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardEditEvent;
import cn.com.infostrategy.ui.mdata.BillCardEditListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_Ref;

public class UserAssignRolePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private String str_curruserid = null;

	private Pub_Templet_1VO templetVO1, templetVO2 = null; //
	private BillListPanel billListPanel_all = null; //所有权限
	private BillListPanel billListPanel_assign = null; //赋予的权限

	private JButton btn_refresh, btn_save, bnt_1, bnt_2, bnt_3, bnt_4 = null;

	private boolean ifEdited = false;

	public UserAssignRolePanel() {
		initialize(); //
	}

	private void initialize() {
		try {
			this.setLayout(new BorderLayout()); //
			this.add(getNorthPanel(), BorderLayout.NORTH); //
			this.add(getCenterPanel(), BorderLayout.CENTER); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	private JPanel getNorthPanel() {
		JPanel panel = new JPanel(); //
		panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		try {
			CardCPanel_Ref ref = new CardCPanel_Ref("user", "请选择一个用户", "getTableRef(\"select id 主键$,code 登录号, name 姓名,code1 人力资源ID from pub_user\")", WLTConstants.COMP_REFPANEL, null, null); //
			ref.addBillCardEditListener(new BillCardEditListener() {
				public void onBillCardValueChanged(BillCardEditEvent _evt) {
					RefItemVO vo = (RefItemVO) _evt.getNewObject(); //
					str_curruserid = vo.getId(); //
					refreshLeftPanel(str_curruserid);
					refreshRightPanel(str_curruserid); //
					btn_refresh.setEnabled(true);
					btn_save.setEnabled(true);
					bnt_1.setEnabled(true);
					bnt_2.setEnabled(true);
					bnt_3.setEnabled(true);
					bnt_4.setEnabled(true);
				}
			});

			btn_refresh = new JButton("刷新"); //
			btn_refresh.setEnabled(false);
			btn_refresh.setPreferredSize(new Dimension(85, 20)); //
			btn_refresh.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (str_curruserid != null) {
						refreshLeftPanel(str_curruserid);
						refreshRightPanel(str_curruserid); //
					}
				}
			});

			btn_save = new JButton("保存"); //
			btn_save.setEnabled(false);
			btn_save.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onSave();
				}
			}); //
			btn_save.setPreferredSize(new Dimension(85, 20)); //

			panel.add(ref);
			panel.add(btn_refresh); //
			panel.add(btn_save); //
			return panel;
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return panel;
		}
	}

	private JPanel getCenterPanel() throws Exception {
		bnt_1 = new JButton(" >>");
		bnt_2 = new JButton(" > ");
		bnt_3 = new JButton(" < ");
		bnt_4 = new JButton(" <<");
		bnt_1.setEnabled(false);
		bnt_2.setEnabled(false);
		bnt_3.setEnabled(false);
		bnt_4.setEnabled(false);

		bnt_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClicked_1(); //
			}
		});//

		bnt_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClicked_2(); //
			}
		});//

		bnt_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClicked_3(); //
			}
		});//

		bnt_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClicked_4(); //
			}
		});//

		bnt_1.setPreferredSize(new Dimension(100, 20));
		bnt_2.setPreferredSize(new Dimension(100, 20));
		bnt_3.setPreferredSize(new Dimension(100, 20));
		bnt_4.setPreferredSize(new Dimension(85, 20));

		JPanel panel = new JPanel(); //
		panel.setLayout(new BorderLayout());
		Box rightAndLeftBox = Box.createVerticalBox();
		rightAndLeftBox.add(Box.createGlue());
		rightAndLeftBox.add(bnt_1); //
		rightAndLeftBox.add(Box.createVerticalStrut(10));
		rightAndLeftBox.add(bnt_2); //
		rightAndLeftBox.add(Box.createVerticalStrut(10));
		rightAndLeftBox.add(bnt_3); //
		rightAndLeftBox.add(Box.createVerticalStrut(10));
		rightAndLeftBox.add(bnt_4); //
		rightAndLeftBox.add(Box.createGlue());

		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalStrut(20));

		templetVO1 = UIUtil.getPub_Templet_1VO(new ServerTMODefine("cn.com.infostrategy.bs.sysapp.servertmo.TMO_UserAssignRole_Role")); //

		JLabel label_left = new JLabel("所有可分配的角色:", SwingConstants.LEFT);
		label_left.setForeground(java.awt.Color.GRAY);
		JPanel left_panel = new JPanel();
		left_panel.setLayout(new BorderLayout()); //
		left_panel.add(label_left, BorderLayout.NORTH); //
		left_panel.add(getLeftPanel(templetVO1), BorderLayout.CENTER); //

		box.add(left_panel);

		box.add(Box.createHorizontalStrut(20)); //
		box.add(rightAndLeftBox); //

		templetVO2 = templetVO1.deepClone(); //克隆一个!
		JLabel label_right = new JLabel("已分配的角色:", SwingConstants.LEFT);
		label_right.setForeground(java.awt.Color.GRAY);
		JPanel right_panel = new JPanel();
		right_panel.setLayout(new BorderLayout()); //
		right_panel.add(label_right, BorderLayout.NORTH); //
		right_panel.add(getRightPanel(templetVO2), BorderLayout.CENTER); //

		box.add(right_panel);
		box.add(Box.createHorizontalStrut(20)); //

		panel.add(box, BorderLayout.CENTER);
		return panel;
	}

	private BillListPanel getLeftPanel(Pub_Templet_1VO _templetVO) {
		if (billListPanel_all == null) {
			billListPanel_all = new BillListPanel(_templetVO); //
			billListPanel_all.setItemEditable(false);
			billListPanel_all.setToolbarVisiable(false);
			billListPanel_all.getTitlePanel().setVisible(false); //
			billListPanel_all.QueryData("select id,code,name,descr,0 assigncount from pub_role where 1=1"); //
		}
		return billListPanel_all;
	}

	private BillListPanel getRightPanel(Pub_Templet_1VO _templetVO) {
		if (billListPanel_assign == null) {
			billListPanel_assign = new BillListPanel(_templetVO); //
			billListPanel_assign.setToolbarVisiable(false);
			billListPanel_assign.getTitlePanel().setVisible(false); //
			billListPanel_assign.setItemEditable(false);
		}
		return billListPanel_assign;
	}

	/**
	 * 分配所有的角色
	 *
	 */
	private void onClicked_1() {
		BillVO[] vos = billListPanel_all.getAllBillVOs(); //
		BillVO[] vos_right = billListPanel_assign.getAllBillVOs();

		for (int i = 0; i < vos.length; i++) {
			boolean iffind = false;
			String str_id = vos[i].getPkValue(); //
			for (int j = 0; j < vos_right.length; j++) {
				if (vos_right[j].getPkValue().equals(str_id)) {
					iffind = true;
					break;
				}
			}
			if (!iffind) {
				billListPanel_assign.addRow(vos[i]); //
			}
		}
		ifEdited = true; //
	}

	/**
	 * 分配选中的角色
	 *
	 */
	private void onClicked_2() {
		BillVO[] vos = billListPanel_all.getSelectedBillVOs(); //
		BillVO[] vos_right = billListPanel_assign.getAllBillVOs();

		for (int i = 0; i < vos.length; i++) {
			boolean iffind = false;
			String str_id = vos[i].getPkValue(); //
			for (int j = 0; j < vos_right.length; j++) {
				if (vos_right[j].getPkValue().equals(str_id)) {
					iffind = true;
					break;
				}
			}

			if (!iffind) {
				billListPanel_assign.addRow(vos[i]); //
			}
		}

		ifEdited = true; //
	}

	private void onClicked_3() {
		billListPanel_assign.removeSelectedRows(); //
		ifEdited = true; //
	}

	private void onClicked_4() {
		billListPanel_assign.clearTable(); //
		ifEdited = true; //
	}

	private void refreshLeftPanel(String _userid) {
		billListPanel_all.QueryData("Select id,code,name,descr from pub_role"); //
	}

	/*
	 * 刷新右边的框
	 */
	private void refreshRightPanel(String _userid) {
		billListPanel_assign.QueryData("select * from pub_role where id in (select roleid from pub_user_role where userid='" + this.str_curruserid + "')"); //
		ifEdited = false;
	}

	private void onSave() {
		if (!ifEdited) {
			if (JOptionPane.showConfirmDialog(this, "数据没有变化,你是否重新保存数据?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}
		}

		if (str_curruserid == null) {
			MessageBox.show(this, "请选择一个用户!");
			return;
		}

		try {
			Vector v_sqls = new Vector();
			v_sqls.add("delete from pub_user_role where userid = '" + str_curruserid + "'"); //先删除该用户所拥有的所有角色

			BillVO[] vos = billListPanel_assign.getAllBillVOs();
			for (int i = 0; i < vos.length; i++) {
				String str_newid = UIUtil.getSequenceNextValByDS(null, "s_pub_user_role"); //
				v_sqls.add("insert into pub_user_role (id,userid,roleid) values ('" + str_newid + "','" + str_curruserid + "','" + vos[i].getPkValue() + "')"); //
			}

			UIUtil.executeBatchByDS(null, v_sqls);
			refreshLeftPanel(this.str_curruserid); //刷新左边的表
			refreshRightPanel(this.str_curruserid); //刷新右边的表
			MessageBox.show(this, "保存权限数据成功!");
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex);
		} //
	}

}
