package cn.com.infostrategy.ui.sysapp.transferdb;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.FrameWorkCommServiceIfc;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class MWPanel_TransferDB extends AbstractWorkPanel implements ActionListener {

	private JLabel label_source = null; //
	private JComboBox comboxsourceDS = null; // Դ����Դ..
	private JTextField textfield_1 = null; //

	private JButton btn_search = null; //

	private JButton btn_move = null; //xmlת��

	private JButton btn_transfer = null; // ת������

	private JButton btn_validate = null; // ��֤���Ƿ��йؼ���

	private JLabel label_dest = null; //
	private JComboBox comboxdestDS = null; // Ŀ������Դ..

	private JCheckBox check_struct = new JCheckBox("��ṹ", true); //
	private JCheckBox check_data = new JCheckBox("������", true); //
	private JCheckBox check_quick = new JCheckBox("�����ύ", false); //
	private BillListPanel billList_source = null; //

	private JScrollPane scrollPane = null; //
	private JTextArea textArea = null; //

	@Override
	public void initialize() {
		this.setLayout(null); //

		label_source = new JLabel("��Դ��", JLabel.RIGHT); //
		DataSourceVO[] dsVOs = ClientEnvironment.getInstance().getDataSourceVOs(); //
		comboxsourceDS = new JComboBox(); //
		for (int i = 0; i < dsVOs.length; i++) {
			comboxsourceDS.addItem(dsVOs[i]); //
		}
		textfield_1 = new JTextField(); //
		btn_search = new WLTButton("��ѯ"); //
		btn_search.addActionListener(this); //

		btn_transfer = new WLTButton("ת��"); //
		btn_transfer.addActionListener(this); //

		btn_validate = new WLTButton("��֤");
		btn_validate.addActionListener(this);

		btn_move = new WLTButton("��ȫת��");
		btn_move.addActionListener(this);
		label_dest = new JLabel("Ŀ���", JLabel.RIGHT); //
		comboxdestDS = new JComboBox(); //
		for (int i = 0; i < dsVOs.length; i++) {
			comboxdestDS.addItem(dsVOs[i]); //
		}

		billList_source = new BillListPanel(new DefaultTMO("���������", new String[][] { { "����", "150" }, { "����", "70" }, { "˵��", "160" } })); //

		scrollPane = new JScrollPane(); //
		textArea = new JTextArea(); //
		scrollPane.getViewport().add(textArea); //

		label_source.setBounds(10, 10, 60, 20); //
		comboxsourceDS.setBounds(70, 10, 125, 20); //
		textfield_1.setBounds(195, 10, 120, 20); //
		btn_search.setBounds(315, 10, 50, 20); //

		btn_transfer.setBounds(370, 10, 50, 20); //
		btn_validate.setBounds(425, 10, 50, 20);

		label_dest.setBounds(520, 10, 60, 20); //
		comboxdestDS.setBounds(585, 10, 110, 20); //

		check_struct.setBounds(695, 10, 80, 20); //
		check_data.setBounds(775, 10, 80, 20); //
		check_quick.setBounds(860, 10, 80, 20);

		billList_source.setBounds(10, 35, 450, 450); //
		scrollPane.setBounds(465, 35, 500, 450); //

		btn_move.setBounds(480, 10, 60, 20);

		check_quick.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBox check = (JCheckBox) e.getSource();
				if (check.isSelected()) {
					MessageBox.show(MWPanel_TransferDB.this, "�˲�����ÿ500�����ݻ�����ύһ�Σ�ʹ��ͬһ�ű�����ݲ�һ������ͬһ������\r\n������ʹ�ã�");
				}
			}
		});

		this.add(label_source); //
		this.add(comboxsourceDS); //
		this.add(textfield_1); //
		this.add(btn_search); //
		this.add(btn_transfer); //
		this.add(btn_validate); //
		this.add(btn_move);
		this.add(label_dest); //
		this.add(comboxdestDS); //
		this.add(check_struct); //
		this.add(check_data); //
		this.add(check_quick);
		this.add(billList_source); //
		this.add(scrollPane); //

	}

	/**
	 * �����ťִ��
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_search) {
			onQueryTable(); //
		} else if (e.getSource() == btn_transfer) {
			onTransfer(); //
		} else if (e.getSource() == btn_validate) {
			onValidate(); //
		} else if (e.getSource() == btn_move) {
			onMove();
		}
	}

	private void onQueryTable() {
		DataSourceVO dsVO = (DataSourceVO) comboxsourceDS.getSelectedItem(); //
		if (dsVO == null) {
			MessageBox.show(this, "��ѡ��һ������Դ"); //
		}
		try {
			billList_source.clearTable(); //
			FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class); //
			String[][] str_tabdesc = service.getAllSysTableAndDescr(dsVO.getName(), null, true, true); //
			String str_tname = textfield_1.getText(); //
			for (int i = 0; i < str_tabdesc.length; i++) { //����������!!
				if (!str_tname.equals("") && str_tabdesc[i][0].indexOf(str_tname) < 0) { //�����Ϊ��,����ƥ�䲻��!!!
					continue;
				}
				int li_newRow = billList_source.addEmptyRow(false); //
				billList_source.setValueAt(new StringItemVO(str_tabdesc[i][0]), li_newRow, "����"); //
				billList_source.setValueAt(new StringItemVO(str_tabdesc[i][1]), li_newRow, "����"); //
				billList_source.setValueAt(new StringItemVO(str_tabdesc[i][2]), li_newRow, "˵��"); //
			}

		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		}
	}

	/**
	 * ת������
	 */
	private void onTransfer() {
		DataSourceVO sourceDSVO = (DataSourceVO) comboxsourceDS.getSelectedItem(); //
		if (sourceDSVO == null) {
			MessageBox.show(this, "��ѡ����Դ��!"); //
			return;
		}

		DataSourceVO destDSVO = (DataSourceVO) comboxdestDS.getSelectedItem(); //
		if (destDSVO == null) {
			MessageBox.show(this, "��ѡ��Ŀ���!"); //
			return;
		}

		final String str_dsname1 = sourceDSVO.getName(); //
		final String str_dsname2 = destDSVO.getName(); //
		if (str_dsname1.equals(str_dsname2)) { //
			MessageBox.show(this, "��Դ����Ŀ��ⲻ����ͬ!"); //
			return;
		}

		BillVO[] billVOs = billList_source.getSelectedBillVOs(); //
		if (billVOs.length == 0) {
			MessageBox.show(this, "��ѡ��Ҫ����ı�!"); //
			return;
		}

		final String[] str_tablename = new String[billVOs.length]; //
		for (int i = 0; i < str_tablename.length; i++) {
			str_tablename[i] = billVOs[i].getStringValue("����"); //
		}

		if (JOptionPane.showConfirmDialog(this, "��ȷ��Ҫ������[" + str_tablename.length + "]������?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) { // ��ʾ�Ƿ���ĵ���
			return;
		}

		Thread appThread = new Thread() {
			public void run() {
				btn_transfer.setEnabled(false);
				long ll_1 = System.currentTimeMillis(); //
				for (int i = 0; i < str_tablename.length; i++) {
					try {
						int li_insertcount = UIUtil.getMetaDataService().transferDB(str_dsname1, str_dsname2, str_tablename[i], check_struct.isSelected(), check_data.isSelected()); //
						String str_msg = ""; //
						if (check_struct.isSelected()) {
							str_msg = str_msg + "������[" + str_tablename[i] + "]�ɹ�!"; //
						}
						if (check_data.isSelected()) {
							str_msg = str_msg + "�����[" + str_tablename[i] + "]��[" + li_insertcount + "]����¼!"; //
						}

						msg(str_msg + "\r\n"); //
					} catch (Exception ex) {
						ex.printStackTrace(); //
						msg("�����[" + str_tablename[i] + "]ʧ��,��ϸԭ�������̨!\r\n"); //
					}
				}
				long ll_2 = System.currentTimeMillis(); //
				msg("�������б����,����ʱ[" + (ll_2 - ll_1) + "]!\r\n"); //
				btn_transfer.setEnabled(true);
			}
		};
		appThread.start(); //
	}

	/**
	 * ��֤����
	 */
	private void onValidate() {
		DataSourceVO sourceDSVO = (DataSourceVO) comboxsourceDS.getSelectedItem(); //
		if (sourceDSVO == null) {
			MessageBox.show(this, "��ѡ����Դ��!"); //
			return;
		}

		DataSourceVO destDSVO = (DataSourceVO) comboxdestDS.getSelectedItem(); //
		if (destDSVO == null) {
			MessageBox.show(this, "��ѡ��Ŀ���!"); //
			return;
		}

		final String str_dsname1 = sourceDSVO.getName(); //
		final String str_dsname2 = destDSVO.getName(); //
		if (str_dsname1.equals(str_dsname2)) { //
			MessageBox.show(this, "��Դ����Ŀ��ⲻ����ͬ!"); //
			return;
		}

		BillVO[] billVOs = billList_source.getSelectedBillVOs(); //
		if (billVOs.length == 0) {
			MessageBox.show(this, "��ѡ��Ҫ��֤�ı�!"); //
			return;
		}

		final String[] str_tablename = new String[billVOs.length]; //
		for (int i = 0; i < str_tablename.length; i++) {
			str_tablename[i] = billVOs[i].getStringValue("����"); //
		}
		try {
			String primarykey = UIUtil.getMetaDataService().getCollidePKByTableName(str_tablename, destDSVO.getDbtype()); //
			msg(primarykey + "\r\n"); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}

	}

	private void onMove() {
		DataSourceVO sourceDSVO = (DataSourceVO) comboxsourceDS.getSelectedItem(); //
		if (sourceDSVO == null) {
			MessageBox.show(this, "��ѡ����Դ��!"); //
			return;
		}

		DataSourceVO destDSVO = (DataSourceVO) comboxdestDS.getSelectedItem(); //
		if (destDSVO == null) {
			MessageBox.show(this, "��ѡ��Ŀ���!"); //
			return;
		}

		final String str_dsname1 = sourceDSVO.getName(); //
		final String str_dsname2 = destDSVO.getName(); //
		if (str_dsname1.equals(str_dsname2)) { //
			MessageBox.show(this, "��Դ����Ŀ��ⲻ����ͬ!"); //
			return;
		}
		BillVO[] billVOs = billList_source.getSelectedBillVOs(); //
		if (billVOs.length == 0) {
			MessageBox.show(this, "��ѡ��Ҫ����ı�!"); //
			return;
		}
		List tableList = new ArrayList();
		List viewList = new ArrayList();
		for (int i = 0; i < billVOs.length; i++) {
			if (billVOs[i].getStringValue("����").equals("TABLE")) {
				tableList.add(billVOs[i].getStringValue("����")); //	
			} else { //��ͼ
				viewList.add(billVOs[i].getStringValue("����"));
			}
		}
		final String[] str_tablename = (String[]) tableList.toArray(new String[0]);
		final String[] str_viewname = (String[]) viewList.toArray(new String[0]);
		if (JOptionPane.showConfirmDialog(this, "��ȷ��Ҫ������[" + str_tablename.length + "]������?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) { // ��ʾ�Ƿ���ĵ���
			return;
		}
		Thread appThread = new Thread() {
			public void run() {
				FrameWorkCommServiceIfc service = null;
				HashMap tablemap = new HashMap();
				try {
					service = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class);
					String[] db2_tables = service.getAllSysTables(str_dsname2, null); //
					for (int i = 0; i < db2_tables.length; i++) {
						tablemap.put(db2_tables[i].toLowerCase(), null);
					}
				} catch (WLTRemoteException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} //
				btn_move.setEnabled(false);
				long ll_1 = System.currentTimeMillis(); //
				HashMap conditon = new HashMap(); //��������
				conditon.put("check_data", check_data.isSelected()); //�Ƿ�Ǩ������
				conditon.put("check_quick", check_quick.isSelected());
				for (int i = 0; i < str_tablename.length; i++) { //������
					try {
						String str_msg = UIUtil.getMetaDataService().safeMoveData(str_dsname1, str_dsname2, str_tablename[i], tablemap, "TABLE", conditon); //
						msg(str_msg); //
					} catch (Exception ex) {
						ex.printStackTrace(); //
						msg("�����[" + str_tablename[i] + "]ʧ��,��ϸԭ�������̨!\r\n"); //
					}
				}
				long ll_2 = System.currentTimeMillis(); //
				for (int i = 0; i < str_viewname.length; i++) { //������ͼ
					try {
						String str_msg = UIUtil.getMetaDataService().safeMoveData(str_dsname1, str_dsname2, str_viewname[i], null, "VIEW", null);
						msg(str_msg);
					} catch (Exception e) {
						msg("������ͼ[" + str_viewname[i] + "]ʧ��,��ϸԭ�������̨!\r\n"); //
						e.printStackTrace();

					} //
				}
				msg("�������б���ͼ����,����ʱ[" + (ll_2 - ll_1) + "]!\r\n"); //
				btn_move.setEnabled(true);
			}
		};
		appThread.start(); //
	}

	private void msg(final String _text) {
		textArea.append(_text); //
		textArea.select(textArea.getText().length(), textArea.getText().length()); //
	}

}
