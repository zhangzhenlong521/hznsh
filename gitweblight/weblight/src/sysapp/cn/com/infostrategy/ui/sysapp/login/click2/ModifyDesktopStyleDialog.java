package cn.com.infostrategy.ui.sysapp.login.click2;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.sysapp.login.DeskTopPanel;
import cn.com.infostrategy.ui.sysapp.login.LoginAppletLoader;

/**
 * �����ʯ��ť�����л��û���񴰿�!!!
 * @author xch
 *
 */
public class ModifyDesktopStyleDialog extends BillDialog {

	private static final long serialVersionUID = -3984901966368699695L;
	private LoginAppletLoader loader = null;
	private int li_desktopLayout = 1; //
	private int li_indexLayout = 1; //
	private String[][] str_deskIndexDefine = null; //

	private JRadioButton[] btns = null;
	private JComboBox blDept = null;
	private JComboBox comboxLookAndFeel = null;

	private JCheckBox checkIsDefaultDept = null;
	private HashVO[] items = null;

	public ModifyDesktopStyleDialog(Container _parent, LoginAppletLoader _loader, int _desktopLayout, int _indexLayout, String[][] _deskTopIndexDefine) {
		super(_parent, "��¼����", 700, 160); //
		this.loader = _loader; //
		this.li_desktopLayout = _desktopLayout; //���沼��
		this.li_indexLayout = _indexLayout; //��ҳ����
		this.str_deskIndexDefine = _deskTopIndexDefine; //
		initialize(); //
	}

	public static void openMe(DeskTopPanel _parent, LoginAppletLoader _loader, Integer _desktopLayout, Integer _indexLayout, String[][] _deskTopIndexDefine) {
		JDialog dialog = new ModifyDesktopStyleDialog(_parent, _loader, _desktopLayout, _indexLayout, _deskTopIndexDefine); //
		dialog.setVisible(true); //
	}

	private void initialize() {
		JLabel label = new JLabel("��������: ", SwingConstants.RIGHT); //
		btns = new JRadioButton[5]; //
		btns[0] = new JRadioButton("������");
		btns[1] = new JRadioButton("�˵������");
		btns[2] = new JRadioButton("��̬��ť���");
		btns[3] = new JRadioButton("Tab���");
		btns[4] = new JRadioButton("�˵�ƽ��");
		ButtonGroup group = new ButtonGroup();
		for (int i = 0; i < btns.length; i++) {
			btns[i].setOpaque(false); //
			btns[i].setFocusable(false); //
			group.add(btns[i]); //
		}
		btns[li_desktopLayout - 1].setSelected(true); //

		TBUtil tbUtil = new TBUtil(); //
		btns[0].setEnabled(tbUtil.getSysOptionBooleanValue("�Ƿ���ó�����", true)); //
		btns[1].setEnabled(tbUtil.getSysOptionBooleanValue("�Ƿ���ò˵������", true)); //
		btns[2].setEnabled(tbUtil.getSysOptionBooleanValue("�Ƿ���ð�ť�����", true)); //Ԭ����20121109����  ��Ҫ�����ҵ���У�������ҵ����Ҫ����ô˰�ť

		comboxLookAndFeel = new JComboBox(); //
		comboxLookAndFeel.setPreferredSize(new Dimension(85, 20));
		try {
			comboxLookAndFeel.addItem(new ComBoxItemVO("0", null, "Ĭ�Ϸ��")); //
			HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, "select * from pub_lookandfeel where code='����'"); //
			if (hvs.length > 0) {
				comboxLookAndFeel.addItem(new ComBoxItemVO("1", null, hvs[0].getStringValue("style1")));
				comboxLookAndFeel.addItem(new ComBoxItemVO("2", null, hvs[0].getStringValue("style2")));
				comboxLookAndFeel.addItem(new ComBoxItemVO("3", null, hvs[0].getStringValue("style3")));
				comboxLookAndFeel.addItem(new ComBoxItemVO("4", null, hvs[0].getStringValue("style4")));
				comboxLookAndFeel.addItem(new ComBoxItemVO("5", null, hvs[0].getStringValue("style5")));
				comboxLookAndFeel.addItem(new ComBoxItemVO("6", null, hvs[0].getStringValue("style6")));

				int li_currLookAndFeelType = ClientEnvironment.getCurrLoginUserVO().getLookAndFeelType(); //
				comboxLookAndFeel.setSelectedItem(new ComBoxItemVO("" + li_currLookAndFeelType, null, hvs[0].getStringValue("style" + li_currLookAndFeelType))); //
				comboxLookAndFeel.setEnabled(tbUtil.getSysOptionBooleanValue("�Ƿ����Ƥ�����", true));
			}
		} catch (Exception e) {
			e.printStackTrace(); //
		}

		JLabel labelId = new JLabel("��¼���: ", SwingConstants.RIGHT);
		items = getAllDepts();
		blDept = new JComboBox(items);
		blDept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				HashVO hvs = (HashVO) blDept.getSelectedItem();
				if ("Y".equals(hvs.getStringValue("isdefault"))) {
					checkIsDefaultDept.setSelected(true);
				} else {
					checkIsDefaultDept.setSelected(false);
				}
			}
		});
		JLabel yr = new JLabel("�Ƿ�Ĭ�ϵ�¼: ", SwingConstants.RIGHT);
		checkIsDefaultDept = new JCheckBox(); //
		checkIsDefaultDept.setOpaque(false); //͸��!!!
		for (int i = 0; i < items.length; i++) {
			String str_bldeptid = ClientEnvironment.getCurrLoginUserVO().getBlDeptId(); //
			if (str_bldeptid != null && str_bldeptid.equals(items[i].getStringValue("deptid"))) {
				blDept.setSelectedIndex(i);
				if ("Y".equals(items[i].getStringValue("isdefault"))) {
					checkIsDefaultDept.setSelected(true);
				} else {
					checkIsDefaultDept.setSelected(false);
				}
			}
		}

		JPanel panel_1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel_1.setOpaque(false); //
		panel_1.add(label);
		for (int i = 0; i < btns.length; i++) {
			panel_1.add(btns[i]);
		}
		panel_1.add(new JLabel("    Ƥ�����:", JLabel.RIGHT)); //
		panel_1.add(comboxLookAndFeel); //

		JPanel panel_2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel_2.setOpaque(false);
		panel_2.add(labelId);
		panel_2.add(blDept);
		panel_2.add(yr);
		panel_2.add(checkIsDefaultDept);

		JPanel panel_3 = new JPanel(new BorderLayout());
		panel_3.setOpaque(false); //
		panel_3.add(panel_1, BorderLayout.NORTH); //
		panel_3.add(panel_2, BorderLayout.CENTER); //

		JPanel contentPanel = WLTPanel.createDefaultPanel(new BorderLayout()); //
		contentPanel.add(panel_3, BorderLayout.CENTER);
		contentPanel.add(getSouthPanel(), BorderLayout.SOUTH);

		this.getContentPane().setLayout(new BorderLayout()); //
		this.getContentPane().add(contentPanel); //
	}

	/**
	 * ȡ���û����м�ְ����
	 * 
	 * @return
	 */
	private HashVO[] getAllDepts() {
		HashVO[] hvs = null;
		try {

			StringBuffer sb_sql = new StringBuffer();
			sb_sql.append("select ");
			sb_sql.append("t1.id,");
			sb_sql.append("t1.userid userid,");
			sb_sql.append("t1.postid postid,");
			sb_sql.append("t1.isdefault isdefault,"); // �Ƿ�Ĭ�ϸ�λ
			sb_sql.append("t2.code postcode,"); // ��λ����
			sb_sql.append("t2.name postname,"); // ��λ����
			sb_sql.append("t1.userdept deptid,"); // �û���������ID
			sb_sql.append("t3.code     deptcode,"); // �û�������������
			sb_sql.append("t3.name     deptname,"); // �û�������������
			sb_sql.append("t3.corptype         userdept_corptype,"); // ������������
			sb_sql.append("t3.bl_zhonghbm      userdept_bl_zhonghbm,"); // ��������֮�������в���
			sb_sql.append("t3.bl_zhonghbm_name userdept_bl_zhonghbm_name,"); // ��������֮�������в�������
			sb_sql.append("t3.bl_fengh         userdept_bl_fengh,"); // ��������֮��������
			sb_sql.append("t3.bl_fengh_name    userdept_bl_fengh_name,"); // ��������֮������������
			sb_sql.append("t3.bl_fenghbm       userdept_bl_fenghbm,"); // ��������֮�������в���
			sb_sql.append("t3.bl_fenghbm_name  userdept_bl_fenghbm_name,"); // ��������֮�������в�������
			sb_sql.append("t3.bl_zhih          userdept_bl_zhih,"); // ��������֮����֧��
			sb_sql.append("t3.bl_zhih_name     userdept_bl_zhih_name,"); // ��������֮����֧������
			sb_sql.append("t3.bl_shiyb         userdept_bl_shiyb,"); // ��������֮������ҵ��
			sb_sql.append("t3.bl_shiyb_name    userdept_bl_shiyb_name,"); // ��������֮������ҵ������
			sb_sql.append("t3.bl_shiybfb       userdept_bl_shiybfb,"); // ��������֮������ҵ���ֲ�
			sb_sql.append("t3.bl_shiybfb_name  userdept_bl_shiybfb_name "); // ��������֮������ҵ���ֲ�����
			sb_sql.append("from pub_user_post t1 "); // ��ϵ��..
			sb_sql.append("left join pub_post t2 on t1.postid=t2.id "); //
			sb_sql.append("left join pub_corp_dept t3 on t1.userdept=t3.id "); //
			sb_sql.append("where t1.userid='" + ClientEnvironment.getInstance().getLoginUserID() + "' "); //
			hvs = UIUtil.getHashVoArrayByDS(null, sb_sql.toString());
			for (int i = 0; i < hvs.length; i++) {
				hvs[i].setAttributeValue("displayname", hvs[i].getStringValue("postname") == null ? hvs[i].getStringValue("deptname") : hvs[i].getStringValue("deptname") + "_" + hvs[i].getStringValue("postname")); //
				hvs[i].setToStringFieldName("displayname"); //
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hvs;
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10)); //
		panel.setOpaque(false); //͸��!!!
		panel.setLayout(new FlowLayout()); //
		JButton btn_1 = new JButton("ȷ��"); //
		JButton btn_2 = new JButton("ȡ��"); //
		btn_1.setPreferredSize(new Dimension(75, 20));
		btn_2.setPreferredSize(new Dimension(75, 20));
		btn_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onConfirm();
			}
		});
		btn_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		panel.add(btn_1);
		panel.add(btn_2);
		return panel;
	}

	private String getDeskTopAndIndexStyle(int _deskLayout, int _indexLayout) {
		String[][] str_df = this.str_deskIndexDefine; //
		try {
			return str_df[_deskLayout - 1][_indexLayout - 1]; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return str_df[0][0]; //
		}
	}

	private void onConfirm() {
		try {
			int li_choose = 1; //
			for (int i = 0; i < btns.length; i++) {
				if (btns[i].isSelected()) {
					li_choose = (i + 1); //
					break; //
				}
			}
			String str_style = getDeskTopAndIndexStyle(li_choose, this.li_indexLayout); //��������!
			String str_userId = ClientEnvironment.getInstance().getLoginUserID(); //
			ArrayList al_sqls = new ArrayList(); //
			al_sqls.add("update pub_user set desktopstyle='" + str_style + "' where id='" + str_userId + "'"); //
			ComBoxItemVO itemVO = (ComBoxItemVO) comboxLookAndFeel.getSelectedItem(); //
			if (itemVO != null) {
				al_sqls.add("update pub_user set lookandfeeltype='" + itemVO.getId() + "' where id='" + str_userId + "'");
			}
			UIUtil.executeBatchByDS(null, al_sqls);
			ClientEnvironment.getCurrLoginUserVO().setDeskTopStyle(str_style); //�޸��ڴ����

			this.dispose(); //�ر��Լ�!!!
			loader.dealLogin(true, ClientEnvironment.getInstance().getLoginUserCode(), null, null, ClientEnvironment.chooseISys, ClientEnvironment.getInstance().isAdmin(), true, ClientEnvironment.getInstance().getDefaultLanguageType(), null); //����һ��!
			if (blDept.getSelectedItem() != null) {
				HashVO dept = (HashVO) blDept.getSelectedItem();
				ClientEnvironment.getCurrLoginUserVO().setPKDept(dept.getStringValue("deptid"));
				ClientEnvironment.getCurrSessionVO().setLoginUserPKDept(dept.getStringValue("deptid"));//sunfujun/20121126/ȷ������˿��Եõ���¼����
				ClientEnvironment.getCurrLoginUserVO().setDeptcode(dept.getStringValue("deptcode"));
				ClientEnvironment.getCurrLoginUserVO().setDeptname(dept.getStringValue("deptname"));

				ClientEnvironment.getCurrLoginUserVO().setBlDeptId(dept.getStringValue("deptid")); //
				ClientEnvironment.getCurrLoginUserVO().setBlDeptCode(dept.getStringValue("deptcode")); //
				ClientEnvironment.getCurrLoginUserVO().setBlDeptName(dept.getStringValue("deptname")); //

				ClientEnvironment.getCurrLoginUserVO().setBlPostId(dept.getStringValue("postid")); //
				ClientEnvironment.getCurrLoginUserVO().setBlPostCode(dept.getStringValue("postcode")); //
				ClientEnvironment.getCurrLoginUserVO().setBlPostName(dept.getStringValue("postname")); //

				ClientEnvironment.getCurrLoginUserVO().setBlDept_bl_fengh(dept.getStringValue("userdept_bl_fengh"));
				ClientEnvironment.getCurrLoginUserVO().setBlDept_bl_fengh_name(dept.getStringValue("userdept_bl_fengh_name"));
				ClientEnvironment.getCurrLoginUserVO().setBlDept_bl_fenghbm(dept.getStringValue("userdept_bl_fenghbm"));
				ClientEnvironment.getCurrLoginUserVO().setBlDept_bl_fenghbm_name(dept.getStringValue("userdept_bl_fenghbm_name"));
				ClientEnvironment.getCurrLoginUserVO().setBlDept_bl_shiyb(dept.getStringValue("userdept_bl_shiyb"));
				ClientEnvironment.getCurrLoginUserVO().setBlDept_bl_shiyb_name(dept.getStringValue("userdept_bl_shiyb_name"));
				ClientEnvironment.getCurrLoginUserVO().setBlDept_bl_shiybfb(dept.getStringValue("userdept_bl_shiybfb"));
				ClientEnvironment.getCurrLoginUserVO().setBlDept_bl_shiybfb_name(dept.getStringValue("userdept_bl_shiybfb_name"));
				ClientEnvironment.getCurrLoginUserVO().setBlDept_bl_zhih(dept.getStringValue("userdept_bl_zhih"));
				ClientEnvironment.getCurrLoginUserVO().setBlDept_bl_zhih_name(dept.getStringValue("userdept_bl_zhih_name"));
				ClientEnvironment.getCurrLoginUserVO().setBlDept_bl_zhonghbm(dept.getStringValue("userdept_bl_zhonghbm"));
				ClientEnvironment.getCurrLoginUserVO().setBlDept_bl_zhonghbm_name(dept.getStringValue("userdept_bl_zhonghbm_name"));
				ClientEnvironment.getCurrLoginUserVO().setBlDept_corptype(dept.getStringValue("userdept_corptype"));

				boolean yorn = checkIsDefaultDept.isSelected();
				if (yorn) {
					// ����洢
					List list = new ArrayList();
					for (int i = 0; i < items.length; i++) {
						StringBuffer sql = new StringBuffer();
						if (items[i].getStringValue("id").equals(dept.getStringValue("id"))) {
							sql.append("update pub_user_post set ");
							sql.append(" isdefault='Y'");
							sql.append(" where id=" + dept.getStringValue("id") + "");
						} else {
							sql.append("update pub_user_post set ");
							sql.append(" isdefault=''");
							sql.append(" where id=" + items[i].getStringValue("id") + "");
						}
						list.add(sql.toString());
					}
					UIUtil.executeBatchByDS(null, list);
				} else {
				}
			}
		} catch (Exception e) {
			MessageBox.showException(this, e); //
		}
	}

	private void onCancel() {
		this.dispose(); //
	}
}
