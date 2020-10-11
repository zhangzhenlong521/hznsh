/**************************************************************************
 * $RCSfile: SelectTableDialog.java,v $  $Revision: 1.10 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JTextField;

import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.FrameWorkCommServiceIfc;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.RefDialog_Table;

public class SelectTableDialog extends BillDialog implements ActionListener {

	private static final long serialVersionUID = -3133752516499403815L;

	private JLabel label_tabName = null;
	private JLabel label_templetCode = null;
	private JLabel label_templetName = null;

	private JTextField text_tabName = null;
	private JTextField text_templetCode = null;
	private JTextField text_templetName = null;

	private WLTButton btn_chooseTable, btn_confirm, btn_cancel = null; //ѡ���,ȷ��,ȡ��!

	private String str_templete_code; //

	public SelectTableDialog(Container _parent, String _name) {
		super(_parent, _name, 455, 170); //
		initialize(); //
	}

	private void initialize() {
		label_tabName = new JLabel("����", JLabel.RIGHT);
		label_templetCode = new JLabel("ģ�����", JLabel.RIGHT);
		label_templetName = new JLabel("ģ������", JLabel.RIGHT);

		label_tabName.setBounds(10, 10, 80, 20); //
		label_templetCode.setBounds(10, 40, 80, 20); //
		label_templetName.setBounds(10, 70, 80, 20); //

		text_tabName = new JTextField(); //
		text_templetCode = new JTextField(); //
		text_templetName = new JTextField(); //

		text_tabName.setBounds(95, 10, 200, 20); //
		text_templetCode.setBounds(95, 40, 300, 20); //
		text_templetName.setBounds(95, 70, 300, 20); //

		btn_chooseTable = new WLTButton("ѡ���"); //
		btn_confirm = new WLTButton("ȷ��"); //
		btn_cancel = new WLTButton("ȡ��"); //

		btn_chooseTable.addActionListener(this);
		btn_confirm.addActionListener(this);
		btn_cancel.addActionListener(this);

		btn_chooseTable.setBounds(300, 10, 95, 20); //
		btn_confirm.setBounds(120, 100, 80, 20); //
		btn_cancel.setBounds(205, 100, 80, 20); //

		WLTPanel panel_content = new WLTPanel(WLTPanel.INCLINE_NW_TO_SE, null, LookAndFeel.defaultShadeColor1, false); //
		panel_content.add(label_tabName); //
		panel_content.add(label_templetCode); //
		panel_content.add(label_templetName); //
		panel_content.add(text_tabName); //
		panel_content.add(text_templetCode); //
		panel_content.add(text_templetName); //
		panel_content.add(btn_chooseTable); //
		panel_content.add(btn_confirm); //
		panel_content.add(btn_cancel); //

		this.getContentPane().add(panel_content); //

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_chooseTable) {
			onReference(); //
		} else if (e.getSource() == btn_confirm) {
			onConfirm(); //
		} else if (e.getSource() == btn_cancel) {
			onCancel(); //
		}

	}

	private void onReference() {
		try {
			FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class); //
			String[][] str_allTables = service.getAllSysTableAndDescr(null, null, true, true); //
			TableDataStruct struct = new TableDataStruct(); //
			struct.setHeaderName(new String[] { "��/��ͼ����", "����", "˵��" }); //
			String[][] str_bodydata = new String[str_allTables.length][3]; // 
			for (int i = 0; i < str_allTables.length; i++) {
				str_bodydata[i][0] = str_allTables[i][0]; //
				str_bodydata[i][1] = str_allTables[i][1]; //
				str_bodydata[i][2] = str_allTables[i][2]; //
			}
			struct.setBodyData(str_bodydata); //

			//��ѡ���ĶԻ���
			RefDialog_Table refDialog = new RefDialog_Table(this, "���б�����ͼ", null, null, struct); //
			refDialog.initialize();
			refDialog.setVisible(true); //

			//ȡ�÷���ֵ!!
			if (refDialog.getCloseType() == 1) {
				String str_chooseTabName = refDialog.getReturnRefItemVO().getId(); //
				String str_chooseTabDescr = refDialog.getReturnRefItemVO().getName(); //
				if (str_chooseTabDescr == null || str_chooseTabDescr.trim().equals("")) {
					str_chooseTabDescr = str_chooseTabName; //
				}
				text_tabName.setText(str_chooseTabName); //
				text_templetCode.setText(str_chooseTabName.toUpperCase() + "_CODE1"); //
				text_templetName.setText(str_chooseTabDescr); //
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	private void onConfirm() {
		String str_table_name = text_tabName.getText();
		str_templete_code = text_templetCode.getText();
		String str_templete_name = text_templetName.getText(); //
		if (str_table_name == null || str_table_name.trim().equals("") || str_templete_code == null || str_templete_code.trim().equals("")) {
			MessageBox.show(this, "������ģ����붼����Ϊ��!");
			return; //
		}

		if (!MessageBox.confirm(this, "��ȷ��Ҫ���ñ�[" + str_table_name + "]�Ľṹ����ģ��[" + str_templete_code + "]��?")) {
			return;
		}

		try {
			//����ƽ̨���ִ�Сд�ˣ�������Ҫ�޸�һ�£�Ҫ��ѯ��ģ�����û��ת����Сд����ǰ��ģ���ֶ�TEMPLETCODEҲ����lower����ת�������������ظ���ģ����롾���/2012-03-22��
			String[][] str_getArray = UIUtil.getStringArrayByDS(null, "select * from PUB_TEMPLET_1 where TEMPLETCODE = '" + str_templete_code + "'");
			if (str_getArray.length > 0) {
				MessageBox.show(this, "��ģ������Ѵ���!");
				return; //
			}
			FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class);
			service.importOneTempletVO(str_table_name, str_templete_code, str_templete_name); //����һ��ģ��!!!
			MessageBox.show(this, "����ģ��[" + str_templete_code + "]�ɹ�!");
			this.setCloseType(1); //
			this.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//ȡ�÷��ص�ģ�����!!!
	public String getReturnTempletCode() {
		return str_templete_code; //
	}

	/**
	 * �ر�
	 */
	private void onCancel() {
		this.setCloseType(2); //
		this.dispose();
	}

}
