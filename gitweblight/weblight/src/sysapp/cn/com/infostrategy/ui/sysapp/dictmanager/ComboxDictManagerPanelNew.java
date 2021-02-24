package cn.com.infostrategy.ui.sysapp.dictmanager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;
import cn.com.infostrategy.ui.mdata.OneSQLBillListConfirmDialog;

public class ComboxDictManagerPanelNew extends JPanel implements ActionListener {
	private BillListPanel bl = null;
	private String str_ChoosedType = null;
	private WLTButton btn_choose_type, btn_new_type, btn_delete_type, btn_export_type, btn_import_type, btn_new, btn_del, btn_save, btn_refresh, btn_moveup, btn_movedown;
	private boolean editable = true;
	private JTextArea textArea = new JTextArea();
	private String comboxType = "";

	public ComboxDictManagerPanelNew(boolean _editable) {
		this.editable = _editable;
		initialize();
	}

	//��������������͹��� Gwang 2013-06-22
	public ComboxDictManagerPanelNew(String comboxType, boolean _editable) {
		this.comboxType = comboxType;
		this.editable = _editable;
		initialize();
	}

	public void initialize() {
		this.setLayout(new BorderLayout());
		btn_choose_type = new WLTButton("ѡ����������", UIUtil.getImage("zoomnormal.gif")); //
		btn_choose_type.addActionListener(this);
		JPanel btn_Panel = new WLTPanel(WLTPanel.HORIZONTAL_RIGHT_TO_LEFT, new FlowLayout(FlowLayout.LEFT), LookAndFeel.defaultShadeColor1, false); //
		btn_Panel.add(btn_choose_type); //
		if (editable) {
			btn_new_type = new WLTButton("������������", UIUtil.getImage("office_132.gif")); //
			btn_delete_type = new WLTButton("ɾ����������", UIUtil.getImage("del.gif")); //
			btn_export_type = new WLTButton("����xml��ʽ", UIUtil.getImage("office_165.gif")); //����XML��ʽ������
			btn_import_type = new WLTButton("����xml��ʽ", UIUtil.getImage("office_163.gif"));
			btn_new_type.addActionListener(this);
			btn_delete_type.addActionListener(this);
			btn_export_type.addActionListener(this);
			btn_import_type.addActionListener(this);
			btn_Panel.add(btn_new_type); //
			btn_Panel.add(btn_delete_type); //
			btn_Panel.add(btn_export_type); //
			btn_Panel.add(btn_import_type); //
		}
		this.add(btn_Panel, BorderLayout.NORTH); //
		bl = new BillListPanel(new ServerTMODefine("cn.com.infostrategy.bs.sysapp.servertmo.TMO_Pub_ComboBoxDict"));
		btn_new = WLTButton.createButtonByType(WLTButton.COMM, "����");
		btn_del = WLTButton.createButtonByType(WLTButton.COMM, "ɾ��"); //
		btn_save = WLTButton.createButtonByType(WLTButton.COMM, "����"); //
		btn_refresh = WLTButton.createButtonByType(WLTButton.COMM, "ˢ��"); //
		btn_moveup = WLTButton.createButtonByType(WLTButton.COMM, "����");
		btn_movedown = WLTButton.createButtonByType(WLTButton.COMM, "����");

		btn_new.addActionListener(this); //
		btn_del.addActionListener(this); //
		btn_save.addActionListener(this); //
		btn_refresh.addActionListener(this); //
		btn_moveup.addActionListener(this); //
		btn_movedown.addActionListener(this); //

		bl.addBatchBillListButton(new WLTButton[] { btn_new, btn_del, btn_save, btn_save, btn_refresh, btn_moveup, btn_movedown });
		bl.repaintBillListButton();

		this.add(bl, BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_choose_type) {
			onChooseType();
		} else if (e.getSource() == btn_new) {
			onNew(); //
		} else if (e.getSource() == btn_del) {
			onDelete(); //
		} else if (e.getSource() == btn_save) {
			onSave(); //
		} else if (e.getSource() == btn_refresh) {
			onRefresh(); //
		} else if (e.getSource() == btn_moveup) {
			onMoveUp(); //
		} else if (e.getSource() == btn_movedown) {
			onMoveDown(); //
		} else if (e.getSource() == btn_new_type) {
			onNewType();
		} else if (e.getSource() == btn_delete_type) {
			onDelType(); //
		} else if (e.getSource() == btn_export_type) {
			onExportComboboxXML(); //
		} else if (e.getSource() == btn_import_type) {
			onImportComboboxXML(); //
		}
	}

	/**
	 * ����
	 */
	private void onNew() {
		try {
			if (str_ChoosedType == null) {
				MessageBox.show(this, "����ѡ��һ����������!"); //
				return;
			}

			int li_row = bl.newRow();
			String str_id = UIUtil.getSequenceNextValByDS(null, "s_pub_comboboxdict"); //
			bl.setValueAt(new StringItemVO(str_id), li_row, "pk_pub_comboboxdict"); //
			bl.setValueAt(new StringItemVO(str_ChoosedType), li_row, "type"); //
			bl.setValueAt(new StringItemVO("" + (li_row + 1)), li_row, "seq"); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * ����
	 */
	private void onSave() {
		if (str_ChoosedType == null) {
			MessageBox.show(this, "����ѡ��һ����������!"); //
			return;
		}
		bl.saveData(); //
		if ("��������".equals(str_ChoosedType) || "��������".equals(str_ChoosedType)) {//���������������л��棬����Ҫˢ�»��桾���/2016-05-25��
			try {
				UIUtil.getCommonService().refreshCorptypeFromDB();
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		MessageBox.show(this, "�������ݳɹ�!"); //
	}

	/**
	 * �h��
	 */
	private void onDelete() {
		bl.stopEditing();
		int li_currrow = bl.getTable().getSelectedRow(); // ȡ�õ�ǰ��
		if (li_currrow < 0) {
			MessageBox.showSelectOne(this);
			return;
		}
		bl.removeSelectedRow();
	}

	/**
	 * ˢ��
	 */
	private void onRefresh() {
		if (str_ChoosedType == null) {
			MessageBox.show(this, "����ѡ��һ����������!"); //
			return;
		}
		bl.queryDataByCondition("type='" + str_ChoosedType + "'", "seq asc"); //
	}

	/**
	 * ����
	 */
	private void onMoveUp() {
		if (str_ChoosedType == null) {
			MessageBox.show(this, "����ѡ��һ����������!"); //
			return;
		}
		if (bl.moveUpRow()) {
			resetShowOrder();
		}
	}

	/**
	 * ����˳��
	 */
	private void resetShowOrder() {
		int li_rowcount = bl.getRowCount();
		for (int i = 0; i < li_rowcount; i++) {
			if (bl.getValueAt(i, "SEQ") == null || Integer.parseInt("" + bl.getValueAt(i, "SEQ")) != (i + 1)) {
				bl.setValueAt("" + (i + 1), i, "SEQ"); //
			}
		}
	}

	/**
	 * ����
	 */
	private void onMoveDown() {
		if (str_ChoosedType == null) {
			MessageBox.show(this, "����ѡ��һ����������!"); //
			return;
		}
		if (bl.moveDownRow()) {
			resetShowOrder(); //	
		}
	}

	/**
	 * ѡ������
	 */
	private void onChooseType() {
		//��������������͹��� Gwang 2013-06-22
		String sql = "";
		if ("".equals(this.comboxType)) {
			sql = "select distinct type as �������� from PUB_COMBOBOXDICT order by type";
		} else {
			sql = "select distinct type as �������� from PUB_COMBOBOXDICT where type like '" + this.comboxType + "%' order by type";
		}
		OneSQLBillListConfirmDialog bd = new OneSQLBillListConfirmDialog(this, sql, "��ѡ����������", 600, 400, true, true);
		bd.setVisible(true);
		if (bd.getCloseType() == BillDialog.CONFIRM) {
			if (bd.getSelectedBillVOs() != null && bd.getSelectedBillVOs().length > 0) {
				this.str_ChoosedType = bd.getSelectedBillVOs()[0].getRealValue("��������");
				bl.QueryDataByCondition(" type ='" + str_ChoosedType + "'");
				bl.setBillListTitleName(str_ChoosedType); //
			}
		}
	}

	/**
	 * ������������
	 */
	private void onNewType() {
		try {
			JPanel panel = new JPanel(); //
			panel.setLayout(null); //

			JLabel label = new JLabel("������������ ", JLabel.RIGHT);
			JTextField textField = new JTextField(); //
			label.setBounds(10, 10, 80, 20);
			textField.setBounds(90, 10, 150, 20);

			panel.add(label);
			panel.add(textField); //
			panel.setPreferredSize(new Dimension(240, 30)); //
			if (JOptionPane.showConfirmDialog(this, panel, "��ȷ��Ҫ��������������?", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return; //
			}
			if (textField.getText() == null || textField.getText().trim().equals("")) {
				MessageBox.show(this, "����ֵ����Ϊ��!"); //
				return;
			}

			String[][] ifhava = UIUtil.getStringArrayByDS(null, "select * from pub_comboboxdict where type='" + textField.getText().trim() + "'");
			if (ifhava != null && ifhava.length > 0) {
				MessageBox.show(this, "�Ѿ����ڸ����͵�������ȷ��!"); //
				return;
			}

			StringBuffer sb_sql = new StringBuffer();
			sb_sql.append("insert into pub_comboboxdict");
			sb_sql.append("(");
			sb_sql.append("pk_pub_comboboxdict,"); //pk_pub_comboboxdict  (null)
			sb_sql.append("type,"); //type  (null)
			sb_sql.append("id,"); //id  (null)
			sb_sql.append("code,"); //code  (null)
			sb_sql.append("name,"); //name  (null)
			sb_sql.append("seq"); //seq  (null)
			sb_sql.append(")");
			sb_sql.append(" values ");
			sb_sql.append("(");
			sb_sql.append("'" + UIUtil.getSequenceNextValByDS(null, "s_pub_comboboxdict") + "',"); //pk_pub_comboboxdict  (null)
			sb_sql.append("'" + textField.getText().trim() + "',"); //type  (null)
			sb_sql.append("1,"); //id  (null)
			sb_sql.append("'001',"); //code  (null)
			sb_sql.append("'name1',"); //name  (null)
			sb_sql.append("1"); //seq  (null)
			sb_sql.append(")");

			UIUtil.executeUpdateByDS(null, sb_sql.toString());
			this.str_ChoosedType = textField.getText();
			bl.queryDataByCondition("type='" + str_ChoosedType + "'", "seq asc"); //
			bl.setBillListTitleName(str_ChoosedType); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * ɾ������..
	 */
	private void onDelType() {
		try {
			OneSQLBillListConfirmDialog bd = new OneSQLBillListConfirmDialog(this, " select distinct type �������� from PUB_COMBOBOXDICT order by type ", "��ѡ��Ҫɾ������������", 600, 400, true, true);
			bd.setVisible(true);
			if (bd.getCloseType() == BillDialog.CONFIRM) {
				BillVO[] billVO = bd.getSelectedBillVOs();
				if (billVO != null && billVO.length > 0) {
					if (JOptionPane.showConfirmDialog(this, "��ȷ��Ҫɾ����������?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
						return;
					}
					List sqls = new ArrayList();
					for (int i = 0; i < billVO.length; i++) {
						sqls.add("delete from pub_comboboxdict where type='" + billVO[i].getStringValue("��������") + "'");
					}
					if (sqls.size() > 0) {
						UIUtil.executeBatchByDS(null, sqls);
					}
					bl.clearTable(); //
					bl.setBillListTitleName("������������");
					this.str_ChoosedType = null;
					MessageBox.show(bl, "ɾ���ɹ�!");
				}
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	protected void onExportComboboxXML() {//����XML��ʽ������
		try {
			OneSQLBillListConfirmDialog bd = new OneSQLBillListConfirmDialog(this, " select distinct type �������� from PUB_COMBOBOXDICT order by type ", "��ѡ��Ҫ��������������", 600, 400, true, true);
			bd.setVisible(true);
			if (bd.getCloseType() == BillDialog.CONFIRM) {
				BillVO[] billVO = bd.getSelectedBillVOs();
				if (billVO != null && billVO.length > 0) {
					FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //
					int[] selected_rows = new int[billVO.length];
					String[] tempcode = new String[billVO.length];
					for (int i = 0; i < billVO.length; i++) {
						tempcode[i] = billVO[i].getRealValue("��������"); //
					}
					String sb_xml = service.exportXMLCombobox(tempcode, selected_rows);
					BillDialog dialog = new BillDialog(this, "����XML", 1000, 700);
					JTextArea textArea = new JTextArea(sb_xml); //
					textArea.setBackground(new Color(240, 240, 240)); //
					textArea.setForeground(Color.BLACK); //
					textArea.setFont(new Font("����", Font.PLAIN, 12));
					textArea.select(0, 0); //
					dialog.getContentPane().add(new JScrollPane(textArea)); //
					dialog.setVisible(true); //
				}
			}
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	protected void onImportComboboxXML() {//����XML��ʽ������

		final BillDialog dialog = new BillDialog(this, "����XML", 1000, 700);
		dialog.setLayout(new BorderLayout());
		textArea.setBackground(Color.WHITE); //
		textArea.setForeground(Color.BLUE); //
		textArea.setFont(new Font("����", Font.PLAIN, 12));
		textArea.select(0, 0); //

		JPanel jp = new JPanel();
		jp.setLayout(new FlowLayout(FlowLayout.CENTER));
		WLTButton confirm = new WLTButton("ȷ��");
		WLTButton cancel = new WLTButton("ȡ��");
		confirm.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					if (textArea == null || textArea.getText().trim().equals("") || textArea.getText() == null) {
						MessageBox.show("��������Ҫ�����xml��ʽ��������!");
						return;
					}
					FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //
					service.importXMLCombobox(textArea.getText());

					MessageBox.show(bl, "����XML��ʽ�������ͳɹ�!");

				} catch (Exception ex) {
					ex.printStackTrace(); //
				}
				textArea.setText("");
				dialog.dispose();
			}
		});
		cancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				dialog.dispose();

			}
		});
		jp.add(confirm);
		jp.add(cancel);

		dialog.getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER); //
		dialog.getContentPane().add(jp, BorderLayout.SOUTH);
		dialog.setVisible(true); //

	}
}
