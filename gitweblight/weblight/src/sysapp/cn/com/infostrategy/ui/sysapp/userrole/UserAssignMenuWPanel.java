package cn.com.infostrategy.ui.sysapp.userrole;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.sysapp.login.TMO_Pub_Menu;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardEditEvent;
import cn.com.infostrategy.ui.mdata.BillCardEditListener;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_Ref;

public class UserAssignMenuWPanel extends AbstractWorkPanel {

	private static final long serialVersionUID = 1L;

	private String str_curruserid = null;

	private BillTreePanel treePanel_all = null; //所有权限

	private BillTreePanel treePanel_assign = null; //赋予的权限

	private JButton btn_refresh, btn_save, bnt_1, bnt_2, bnt_3, bnt_4 = null;

	private boolean ifEdited = false;

	public void initialize() {
		this.setLayout(new BorderLayout()); //
		this.add(getNorthPanel(), BorderLayout.NORTH); //
		this.add(getCenterPanel(), BorderLayout.CENTER); //
	}

	private JPanel getNorthPanel() {
		JPanel panel = new JPanel(); //
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		try {
			CardCPanel_Ref ref = new CardCPanel_Ref("user", "选择一个用户", "getTableRef(\"select id id$,code 登录号,name 姓名 from pub_user where 1=1\")", WLTConstants.COMP_REFPANEL, null, null); //
			ref.addBillCardEditListener(new BillCardEditListener() {
				public void onBillCardValueChanged(BillCardEditEvent _evt) {
					RefItemVO vo = (RefItemVO) _evt.getNewObject(); //
					if (vo == null) {
						str_curruserid = null;
						getRightPanel().getRootNode().removeAllChildren();
						getRightPanel().updateUI(); //
						btn_refresh.setEnabled(false);
						btn_save.setEnabled(false);
						bnt_1.setEnabled(false);
						bnt_2.setEnabled(false);
						bnt_3.setEnabled(false);
						bnt_4.setEnabled(false);
					} else {
						str_curruserid = vo.getId(); //
						refreshRightPanel(str_curruserid); //
						btn_refresh.setEnabled(true);
						btn_save.setEnabled(true);
						bnt_1.setEnabled(true);
						bnt_2.setEnabled(true);
						bnt_3.setEnabled(true);
						bnt_4.setEnabled(true);
					}
				}
			});

			btn_refresh = new JButton("刷新"); //
			btn_refresh.setEnabled(false);
			btn_refresh.setPreferredSize(new Dimension(85, 20)); //
			btn_refresh.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (str_curruserid != null) {
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

	private JPanel getCenterPanel() {
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
		box.add(getLeftPanel());
		box.add(Box.createHorizontalStrut(20)); //
		box.add(rightAndLeftBox); //
		box.add(getRightPanel());
		box.add(Box.createHorizontalStrut(20)); //

		panel.add(box, BorderLayout.CENTER);
		return panel;
	}

	private BillTreePanel getLeftPanel() {
		if (treePanel_all == null) {
			treePanel_all = new BillTreePanel(new TMO_Pub_Menu()); //
			treePanel_all.queryDataByCondition("1=1");
		}
		return treePanel_all;
	}

	private BillTreePanel getRightPanel() {
		if (treePanel_assign == null) {
			treePanel_assign = new BillTreePanel(new TMO_Pub_Menu()); //
		}
		return treePanel_assign;
	}

	/**
	 * 全部授权
	 *
	 */
	private void onClicked_1() {
		getRightPanel().getRootNode().removeAllChildren();
		getRightPanel().updateUI(); //以前是getRightPanel().getJTree().updateUI();会改变展开和收缩图标，李春娟2012-02-23修改
		BillVO[] vos = getLeftPanel().getAllBillVOs(); //
		getRightPanel().addNodes(vos); //
		getRightPanel().updateUI(); //以前是getRightPanel().getJTree().updateUI();会改变展开和收缩图标，李春娟2012-02-23修改
		ifEdited = true; //
	}

	/**
	 * 选择授权
	 *
	 */
	private void onClicked_2() {
		if (treePanel_all.getSelectedVO() == null) {
			return;
		}

		String str_pk_value = treePanel_all.getSelectedVO().getPkValue();
		BillVO[] vos = treePanel_all.getSelectedParentAndChildPathBillVOs();
		//		for (int i = 0; i < vos.length; i++) {
		//			System.out.println(vos[i].getObject("name")); //
		//		}
		treePanel_assign.addNodes(vos); //
		treePanel_assign.expandOneNodeByKey(str_pk_value); //展开
		ifEdited = true; //
	}

	private void onClicked_3() {
		if (getRightPanel().getSelectedNode() == null) {
			return;
		}
		getRightPanel().getSelectedNode().removeFromParent(); //
		getRightPanel().updateUI(); //  //
		ifEdited = true; //
	}

	/**
	 * 全部取消授权
	 *
	 */
	private void onClicked_4() {
		getRightPanel().getRootNode().removeAllChildren();
		getRightPanel().updateUI(); //
		ifEdited = true; //
	}

	/*
	 * 刷新右边的框
	 */
	private void refreshRightPanel(String _userid) {
		getRightPanel().queryDataByCondition("id in (select menuid from pub_user_menu where userid='" + this.str_curruserid + "') "); //
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
			v_sqls.add("delete from pub_user_menu where userid = '" + str_curruserid + "'"); //
			BillVO[] vos = treePanel_assign.getAllBillVOs();

			for (int i = 0; i < vos.length; i++) {
				String newId = UIUtil.getSequenceNextValByDS(null, "S_PUB_MENU"); //
				v_sqls.add("insert into pub_user_menu (id,userid,menuid) values (" + newId + ",'" + str_curruserid + "','" + vos[i].getPkValue() + "')"); //
			}

			//		for (int i = 0; i < v_sqls.size(); i++) {
			//			System.out.println(v_sqls.get(i)); //
			//		}

			UIUtil.executeBatchByDS(null, v_sqls);
			MessageBox.show(this, "保存权限数据成功!");
		} catch (Exception _ex) {
			_ex.printStackTrace(); //
			MessageBox.showException(this, _ex);
		} //
	}

	private String getMenuTreeViewFieldName() {
		if (ClientEnvironment.getInstance().getDefaultLanguageType().equals(WLTConstants.ENGLISH)) {
			return "ename";
		} else {
			return "name";
		}
	}
}
