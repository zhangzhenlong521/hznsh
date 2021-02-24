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
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardEditEvent;
import cn.com.infostrategy.ui.mdata.BillCardEditListener;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_Ref;

/**
 * ��ɫ�󶨲˵�Ȩ��
 * @author user
 *
 */
public class RoleAssignMenuWPanel extends AbstractWorkPanel {

	private static final long serialVersionUID = 2856665783569676895L;

	//  ��ǰ��ɫ��id
	private String str_currroleid = null;

	private BillTreePanel treePanel_all = null; //����Ȩ��

	private BillTreePanel treePanel_assign = null; //�����Ȩ��

	private JButton btn_refresh, btn_save, btn_matrix, bnt_1, bnt_2, bnt_3, bnt_4 = null; //

	private boolean ifEdited = false; //

	public void initialize() {
		this.setLayout(new BorderLayout()); //
		this.add(getNorthPanel(), BorderLayout.NORTH); //
		this.add(getCenterPanel(), BorderLayout.CENTER); //
	}

	/**
	 * ��ɫ��������,��ʱ���ܲ�����һЩ��ɫ����!!!
	 * @return
	 */
	public String getRoleFilterCondition() {
		return null; //
	}

	/**
	 * ���˲˵�������,����������,��������ϣ�����еĹ��ܵ㶼����,����ƽ̨�Ĳ˵�,���߹���Ա�Ĳ˵�!!!
	 * @return
	 */
	public String getMenuFilterCondition() {
		return null; //
	}

	private JPanel getNorthPanel() {
		JPanel panel = new JPanel(); //
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		try {
			String str_roleFilterCondition = getRoleFilterCondition(); //��ɫ��������!!!
			CardCPanel_Ref ref = new CardCPanel_Ref("ѡ��һ����ɫ", "ѡ��һ����ɫ", "getTableRef(\"select id ����$,code ��ɫ����,name ��ɫ���� from pub_role where 1=1" + (str_roleFilterCondition == null ? "" : (" and (" + str_roleFilterCondition + ")")) + "\")", WLTConstants.COMP_REFPANEL, null, null); //
			ref.addBillCardEditListener(new BillCardEditListener() {
				public void onBillCardValueChanged(BillCardEditEvent _evt) {
					RefItemVO vo = (RefItemVO) _evt.getNewObject(); //
					if (vo == null) {
						str_currroleid = null;
						getRightPanel().getRootNode().removeAllChildren();
						getRightPanel().updateUI(); //��ǰ��getRightPanel().getJTree().updateUI();��ı�չ��������ͼ�꣬���2012-02-23�޸�
						btn_refresh.setEnabled(false);
						btn_save.setEnabled(false);
						bnt_1.setEnabled(false);
						bnt_2.setEnabled(false);
						bnt_3.setEnabled(false);
						bnt_4.setEnabled(false);
					} else {
						str_currroleid = vo.getId(); //
						refreshRightPanel(str_currroleid); //
						btn_refresh.setEnabled(true);
						btn_save.setEnabled(true);
						bnt_1.setEnabled(true);
						bnt_2.setEnabled(true);
						bnt_3.setEnabled(true);
						bnt_4.setEnabled(true);
					}
				}
			});

			//ˢ��..
			btn_refresh = new JButton("ˢ��"); //
			btn_refresh.setEnabled(false);
			btn_refresh.setPreferredSize(new Dimension(85, 20)); //

			//����..
			btn_save = new JButton("����"); //
			btn_save.setEnabled(false);
			btn_save.setPreferredSize(new Dimension(85, 20)); //

			btn_matrix = new WLTButton("����ʽ����"); //
			ActionListener action = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onDoAction(e.getSource()); //
				}
			};

			btn_refresh.addActionListener(action); //
			btn_save.addActionListener(action); //
			btn_matrix.addActionListener(action); //

			panel.add(ref);
			panel.add(btn_refresh); //
			panel.add(btn_save); //
			panel.add(btn_matrix); //����ʦ����һ����߼��������Ż�Ϊ�ɲ�ѯ�˵���Ӧ�Ľ�ɫ�����ҿ���billcellpanel��ʽ����Ȩ�ޡ����/2015-12-21��

			return panel;
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return panel; //
		}
	}

	protected void onDoAction(Object _source) {
		if (_source == btn_refresh) {
			if (str_currroleid != null) {
				refreshRightPanel(str_currroleid); //
			}
		} else if (_source == btn_save) {
			onSave();
		} else if (_source == btn_matrix) {
			onMatrixConfig(); //
		}
	}

	//�����������!!!
	private void onMatrixConfig() {
		RoleMenuMatrixChooseDialog chooseDialog = new RoleMenuMatrixChooseDialog(this); //
		chooseDialog.setVisible(true); //

		RoleMenuMatrixPanel configPanel = new RoleMenuMatrixPanel(); //
		//�����ȵ���һ���˵����ɫ�ĶԻ���!!!Ȼ����ݶԻ���ѡ���Ӧ�Ĳ˵����ɫ����!
		BillDialog dialog = new BillDialog(this, "����ʽ����", 1000, 700); //
		dialog.getContentPane().add(configPanel); //
		dialog.setVisible(true); //
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
			String str_menuFilterCondition = getMenuFilterCondition(); //
			treePanel_all.queryDataByCondition("1=1" + (str_menuFilterCondition == null ? "" : " and (" + str_menuFilterCondition + ")")); //
		}
		return treePanel_all;
	}

	private BillTreePanel getRightPanel() {
		if (treePanel_assign == null) {
			treePanel_assign = new BillTreePanel(new TMO_Pub_Menu()); //
		}
		return treePanel_assign;
	}

	/*
	 * ˢ���ұߵĿ�
	 */
	private void refreshRightPanel(String _roleid) {
		getRightPanel().queryDataByCondition("id in (select menuid from pub_role_menu where roleid='" + _roleid + "') "); //
		ifEdited = false;
	}

	/**
	 * ȫ����Ȩ
	 *
	 */
	private void onClicked_1() {
		getRightPanel().getRootNode().removeAllChildren();
		getRightPanel().updateUI(); //��ǰ��getRightPanel().getJTree().updateUI();��ı�չ��������ͼ�꣬���2012-02-23�޸�
		BillVO[] vos = getLeftPanel().getAllBillVOs(); //
		getRightPanel().addNodes(vos); //
		getRightPanel().updateUI(); //��ǰ��getRightPanel().getJTree().updateUI();��ı�չ��������ͼ�꣬���2012-02-23�޸�
		ifEdited = true; //
	}

	private void onClicked_2() {
		String str_pk_value = treePanel_all.getSelectedVO().getPkValue();
		BillVO[] vos = treePanel_all.getSelectedParentAndChildPathBillVOs();
		//		for (int i = 0; i < vos.length; i++) {
		//			System.out.println(vos[i].getObject("name")); //
		//		}
		treePanel_assign.addNodes(vos); //
		treePanel_assign.expandOneNodeByKey(str_pk_value); //չ��
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
	 * ȫ��ȡ����Ȩ
	 *
	 */
	private void onClicked_4() {
		getRightPanel().getRootNode().removeAllChildren();
		getRightPanel().updateUI(); //
		ifEdited = true; //
	}

	private void onSave() {
		if (!ifEdited) {
			if (JOptionPane.showConfirmDialog(this, "����û�б仯,���Ƿ����±�������?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}
		}

		if (str_currroleid == null) {
			MessageBox.show(this, "��ѡ��һ���û�!");
			return;
		}

		try {
			Vector v_sqls = new Vector();
			v_sqls.add("delete from pub_role_menu where roleid = '" + str_currroleid + "'"); //
			BillVO[] vos = treePanel_assign.getAllBillVOs();
			for (int i = 0; i < vos.length; i++) {
				String str_newid = UIUtil.getSequenceNextValByDS(null, "s_pub_role_menu"); //
				v_sqls.add("insert into pub_role_menu (id,roleid,menuid) values ('" + str_newid + "','" + str_currroleid + "','" + vos[i].getPkValue() + "')"); //
			}

			UIUtil.executeBatchByDS(null, v_sqls);
			MessageBox.show(this, "����Ȩ�����ݳɹ�!");
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex);
		} //
	}

	private String getMenuTreeViewFieldName() {
		if (ClientEnvironment.getInstance().getDefaultLanguageType().equalsIgnoreCase(WLTConstants.ENGLISH)) {
			return "ename";
		} else {
			return "name";
		}
	}

}
