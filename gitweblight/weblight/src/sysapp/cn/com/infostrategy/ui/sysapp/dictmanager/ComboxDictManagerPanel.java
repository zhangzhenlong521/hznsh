package cn.com.infostrategy.ui.sysapp.dictmanager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;

public class ComboxDictManagerPanel extends JPanel implements BillListSelectListener, ActionListener {

	private static final long serialVersionUID = 1L; ////

	private BillListPanel billListPanel_1 = null; //
	private BillListPanel billListPanel_2 = null; //
	private JButton btn_new, btn_del, btn_save, btn_refresh, btn_moveup, btn_movedown;
	private JTextArea textArea = new JTextArea(); //

	public ComboxDictManagerPanel() {
		JPanel panel_1 = new WLTPanel(WLTPanel.HORIZONTAL_FROM_MIDDLE, new FlowLayout(FlowLayout.LEFT), LookAndFeel.defaultShadeColor1, false); //
		JButton btn_1 = new WLTButton("��������"); //
		JButton btn_2 = new WLTButton("ˢ������"); //
		JButton btn_deltype = new WLTButton("ɾ������"); //
		JButton btn_3 = new WLTButton("����XML��ʽ������"); //
		JButton btn_4 = new WLTButton("����XML��ʽ������"); //
		btn_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onNewType(); //
			}
		}); //
		btn_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onExportComboboxXML(); //
			}
		}); //
		btn_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onImportComboboxXML(); //
			}
		}); //

		btn_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshType(); //
			}
		}); //

		btn_deltype.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onDelType(); //
			}
		}); //

		panel_1.add(btn_1); //
		panel_1.add(btn_deltype); //
		panel_1.add(btn_3); //
		panel_1.add(btn_4); //
		//panel_1.add(btn_2); //

		JPanel panel_left = new JPanel(new BorderLayout()); //
		panel_left.add(panel_1, BorderLayout.NORTH); //
		billListPanel_1 = new BillListPanel(new TMO_ComboBoxDict()); //
		billListPanel_1.addBillListSelectListener(this); //
		panel_left.add(billListPanel_1, BorderLayout.CENTER); //

		JPanel panel_2 = new WLTPanel(WLTPanel.HORIZONTAL_FROM_MIDDLE, new FlowLayout(FlowLayout.LEFT), LookAndFeel.defaultShadeColor1, false); //
		btn_new = new WLTButton("����"); //
		btn_del = new WLTButton("ɾ��"); //
		btn_save = new WLTButton("����"); //
		btn_refresh = new WLTButton("ˢ��"); //
		btn_moveup = new WLTButton("����");
		btn_movedown = new WLTButton("����");

		btn_new.addActionListener(this); //
		btn_del.addActionListener(this); //
		btn_save.addActionListener(this); //
		btn_refresh.addActionListener(this); //
		btn_moveup.addActionListener(this); //
		btn_movedown.addActionListener(this); //

		panel_2.add(btn_new); //
		panel_2.add(btn_del); //
		panel_2.add(btn_save); //
		panel_2.add(btn_refresh); //
		panel_2.add(btn_moveup); //
		panel_2.add(btn_movedown); //

		JPanel panel_right = new JPanel(new BorderLayout()); //
		panel_right.add(panel_2, BorderLayout.NORTH); //

		billListPanel_2 = new BillListPanel("pub_comboboxdict_CODE1"); //
		panel_right.add(billListPanel_2, BorderLayout.CENTER); //

		JSplitPane splitPanel = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel_left, panel_right); //
		splitPanel.setDividerLocation(350); //

		this.setLayout(new BorderLayout()); //
		this.add(splitPanel); //
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
						MessageBox.show("��������Ҫ�����������!");
						return;
					}
					FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //
					service.importXMLCombobox(textArea.getText());

					MessageBox.show(billListPanel_1, "����XML��ʽ������ɹ�!");

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

	protected void onExportComboboxXML() {//����XML��ʽ������
		try {

			int li_count = billListPanel_1.getTable().getSelectedRowCount();
			if (li_count <= 0) {
				MessageBox.showSelectOne(this);
				return;
			}
			FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //

			int[] selected_rows = billListPanel_1.getTable().getSelectedRows();
			String[] tempcode = new String[selected_rows.length];
			for (int i = 0; i < selected_rows.length; i++) {
				tempcode[i] = billListPanel_1.getValueAt(selected_rows[i], "TYPE").toString(); //
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

		} catch (Exception e) {
			e.printStackTrace(); //
		}

	}

	private void refreshType() {
		billListPanel_1.clearTable(); //
		billListPanel_2.clearTable(); //
		billListPanel_1.queryDataByCondition(null, "type"); //
	}

	/**
	 * ɾ������..
	 */
	private void onDelType() {
		try {
			BillVO billVO = billListPanel_1.getSelectedBillVO();
			if (billVO != null) {
				if (JOptionPane.showConfirmDialog(this, "��ȷ��Ҫɾ����������?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
					return;
				}

				String str_type = billVO.getStringValue("type"); //
				String str_sql = "delete from pub_comboboxdict where type='" + str_type + "'"; //
				UIUtil.executeUpdateByDS(null, str_sql); //
				billListPanel_2.clearTable(); //
				billListPanel_1.removeRow(); //
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	//�Ķ���ť�¼�
	private void onNewType() {
		try {
			JPanel panel = new JPanel(); //
			panel.setLayout(null); //

			JLabel label = new JLabel("�����ֵ����� ", JLabel.RIGHT);
			JTextField textField = new JTextField(); //
			label.setBounds(10, 10, 80, 20);
			textField.setBounds(90, 10, 150, 20);

			panel.add(label);
			panel.add(textField); //
			panel.setPreferredSize(new Dimension(240, 30)); //

			if (JOptionPane.showConfirmDialog(this, panel, "��ȷ��Ҫ����������?", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return; //
			}

			if (textField.getText() == null || textField.getText().trim().equals("")) {
				MessageBox.show(this, "����ֵ����Ϊ��!"); //
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
			sb_sql.append("'" + textField.getText() + "',"); //type  (null)
			sb_sql.append("1,"); //id  (null)
			sb_sql.append("'001',"); //code  (null)
			sb_sql.append("'name1',"); //name  (null)
			sb_sql.append("1"); //seq  (null)
			sb_sql.append(")");

			UIUtil.executeUpdateByDS(null, sb_sql.toString());

			int li_row = billListPanel_1.newRow();
			billListPanel_1.setValueAt(new StringItemVO(textField.getText()), li_row, "type"); //
			billListPanel_1.setQuickQueryPanelItemRealValue("TYPE", textField.getText()); //
			billListPanel_2.queryDataByCondition("type='" + textField.getText() + "'", "seq asc"); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		String str_type = _event.getCurrSelectedVO().getStringValue("type");
		billListPanel_2.queryDataByCondition("type='" + str_type + "'", "seq asc"); //
	}

	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == btn_new) {
			onNew(); //
		} else if (evt.getSource() == btn_del) {
			onDelete(); //
		} else if (evt.getSource() == btn_save) {
			onSave(); //
		} else if (evt.getSource() == btn_refresh) {
			onRefresh(); //
		} else if (evt.getSource() == btn_moveup) {
			onMoveUp(); //
		} else if (evt.getSource() == btn_movedown) {
			onMoveDown(); //
		}
	}

	private void onNew() {
		try {
			BillVO billVO = billListPanel_1.getSelectedBillVO(); //
			if (billVO == null) {
				MessageBox.show(this, "����ѡ��һ������!"); //
				return;
			}

			String str_type = billVO.getStringValue("type"); //
			int li_row = billListPanel_2.newRow();
			String str_id = UIUtil.getSequenceNextValByDS(null, "s_pub_comboboxdict"); //
			billListPanel_2.setValueAt(new StringItemVO(str_id), li_row, "pk_pub_comboboxdict"); //
			billListPanel_2.setValueAt(new StringItemVO(str_type), li_row, "type"); //
			billListPanel_2.setValueAt(new StringItemVO("" + (li_row + 1)), li_row, "seq"); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	private void onSave() {
		billListPanel_2.saveData(); //
		//billListPanel_2.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT); //
		MessageBox.show(this, "�������ݳɹ�!"); //
	}

	private void onDelete() {
		billListPanel_2.removeRow(); //
	}

	private void onRefresh() {
		BillVO billVO = billListPanel_1.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, "����ѡ��һ������!"); //
			return;
		}
		String str_type = billVO.getStringValue("type"); //
		billListPanel_2.queryDataByCondition("type='" + str_type + "'", "seq asc"); //
	}

	/**
	 * 
	 */
	private void onMoveUp() {
		if (billListPanel_2.moveUpRow()) {
			resetShowOrder();
		}
	}

	private void resetShowOrder() {
		int li_rowcount = billListPanel_2.getRowCount();
		for (int i = 0; i < li_rowcount; i++) {
			if (billListPanel_2.getValueAt(i, "SEQ") == null || Integer.parseInt("" + billListPanel_2.getValueAt(i, "SEQ")) != (i + 1)) {
				billListPanel_2.setValueAt("" + (i + 1), i, "SEQ"); //
			}
		}
	}

	private void onMoveDown() {
		if (billListPanel_2.moveDownRow()) {
			resetShowOrder(); //	
		}
	}

	class TMO_ComboBoxDict extends AbstractTMO {
		private static final long serialVersionUID = 8057184541083294474L;

		public HashVO getPub_templet_1Data() {
			HashVO vo = new HashVO(); //
			vo.setAttributeValue("templetcode", "pub_comboboxdict"); //ģ�����,��������޸�
			vo.setAttributeValue("templetname", "�ֵ�����"); //ģ������
			vo.setAttributeValue("templetname_e", "ComBoboxdict"); //ģ������
			vo.setAttributeValue("tablename", "v_pub_comboboxdict_1"); //��ѯ���ݵı�(��ͼ)��
			vo.setAttributeValue("pkname", "ID"); //������
			vo.setAttributeValue("pksequencename", null); //������
			vo.setAttributeValue("savedtablename", null); //�������ݵı���
			vo.setAttributeValue("CardWidth", "577"); //��Ƭ���
			vo.setAttributeValue("Isshowlistpagebar", "N"); //�б��Ƿ���ʾ��ҳ��
			vo.setAttributeValue("Isshowlistopebar", "N"); //�б��Ƿ���ʾ������ť��
			vo.setAttributeValue("ISSHOWLISTQUICKQUERY", "Y"); //�б��Ƿ���ʾ���ٲ�ѯ��
			vo.setAttributeValue("listcustpanel", null); //�б��Զ������
			vo.setAttributeValue("cardcustpanel", null); //��Ƭ�Զ������
			return vo;
		}

		public HashVO[] getPub_templet_1_itemData() {
			Vector vector = new Vector();
			HashVO itemVO = null;

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "TYPE"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "����"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "type"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "1"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ

			itemVO.setAttributeValue("listwidth", "200"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "50,120"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "N"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("isQuickQueryShowable", "Y"); //
			itemVO.setAttributeValue("isQuickQueryEditable", "Y"); //
			itemVO.setAttributeValue("querywidth", "60,100"); //
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);

			return (HashVO[]) vector.toArray(new HashVO[0]);
		}
	}
}
