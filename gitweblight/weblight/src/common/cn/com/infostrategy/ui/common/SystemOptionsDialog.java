package cn.com.infostrategy.ui.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * ϵͳ�в���Խ��Խ��,����ҵ�ĳ�������������޸��ѳ���һ���������ϴ����!
 * ��������ҳ,������,Office�ؼ���,������Ҫ����������,һ����ϵͳ�����ж��ٲ�������˲�֪��!
 * ����û����ʹ�ò����ĵط���ѡ����������ĵļ����������д���! ����ѹ��������в�����������,��ʵ��̫��,û���𵽿������õ�Ч��!!!
 * �����䵼���ʱ�,��õķ�������ĳ�������ĵط�,ֻ�����������Ҫ�Ĳ���!! �漴�޸�!!!
 * ��Ҫ����������,һ���ǲ�ѯ���ݿ��е�,һ�������ж����!������ҳǩ!
 * @author Administrator
 *
 */
public class SystemOptionsDialog extends BillDialog implements ActionListener {

	private String optionType; //��������
	private String[] initOptions; //��ʼ������

	private BillListPanel list_db = null; //
	private WLTButton btn_confirm, btn_reloadCache; //

	/**
	 * 
	 * @param _parent
	 * @param _type  ����,��Ӧ��SystemOptions�е�getAllPlatformOptions�����еĶ�ά�����еĵ�һ������...
	 * @param _optins  ����ֻ����ĳ��������,���Ϊ��,���ǰһ������_type�ж����ͳͳ������!
	 */
	public SystemOptionsDialog(java.awt.Container _parent, String _optionType, String[] _initOptins) {
		super(_parent, "�޸�[" + _optionType + "]���ϵͳ����", 950, 600); //
		if (ClientEnvironment.isAdmin()) {
			this.setTitle(this.getTitle() + " ��SystemOptionsDialog��"); //
		}
		this.optionType = _optionType; //
		this.initOptions = _initOptins; //

		initialize(); //
	}

	/**
	 * �������
	 */
	private void initialize() {
		try {
			String[][] str_options = UIUtil.getCommonService().getAllPlatformOptions(optionType); //ȡ�����в���

			JTabbedPane tabb = new JTabbedPane(); //
			tabb.addTab("�������������ݿ��е�ֵ", getDBPanel()); //���ݿ��е�ֵ!

			//ϵͳ�����ж��屾ģ�����в���,��SystemOptions.getAllPlatformOptions()�ж����
			HashVO[] hvs = new HashVO[str_options.length]; //
			HashSet hst = new HashSet(); // 
			for (int i = 0; i < hvs.length; i++) {
				hvs[i] = new HashVO(); //
				hvs[i].setAttributeValue("������", str_options[i][1]); //
				hvs[i].setAttributeValue("Ĭ��ֵ", str_options[i][2]); //
				hvs[i].setAttributeValue("˵��", str_options[i][3]); //
				hst.add(str_options[i][1]); //
			}
			BillListPanel list_all = new BillListPanel("��ע�⣺��SystemOptions�ж����,���ܻ���Ϊ���������û�в�ȫ", hvs); //
			list_all.getTitleLabel().setForeground(Color.RED); //;
			tabb.addTab("��" + optionType + "�����в���", list_all); //���ж����ֵ!

			//�������ض�����
			if (initOptions != null) { //����б�������!!!
				BillVO[] dbVOs = list_db.getAllBillVOs(); //
				HashSet hst_db = new HashSet(); //
				for (int i = 0; i < dbVOs.length; i++) {
					hst_db.add(dbVOs[i].getStringValue("parkey")); //
				}
				HashVO[] hvs_thisdef = new HashVO[initOptions.length]; //
				for (int i = 0; i < hvs_thisdef.length; i++) {
					hvs_thisdef[i] = new HashVO(); //
					hvs_thisdef[i].setAttributeValue("������", initOptions[i]); //
					if (hst.contains(initOptions[i])) { //������ݿ����Ѷ���
						hvs_thisdef[i].setAttributeValue("ϵͳ�Ƿ���", "Y");
					} else {
						hvs_thisdef[i].setAttributeValue("ϵͳ�Ƿ���", "N,Ҫ��SystemOptions�в�ȫ");
					}

					if (hst_db.contains(initOptions[i])) { //������ݿ����Ѷ���
						hvs_thisdef[i].setAttributeValue("���ݿ����Ƿ���", "Y,ֱ���ڵ�һ��ҳǩ���޸�"); //
					} else { //������ݿ���û����
						hvs_thisdef[i].setAttributeValue("���ݿ����Ƿ���", "N,�ֹ���������һ��ҳǩ������"); //
					}
				}
				BillListPanel li_thisdef = new BillListPanel(hvs_thisdef); //
				tabb.addTab("���������嵥", li_thisdef); //
			}
			this.getContentPane().add(tabb); //
			this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			this.getContentPane().removeAll(); //
			this.getContentPane().setLayout(new BorderLayout()); //
			this.getContentPane().add(new JLabel("������淢���쳣[" + ex.getClass() + "][" + ex.getMessage() + "],��������̨�鿴��ϸԭ��...")); //
		}
	}

	/**
	 * ���ݿ��е�ֵ!
	 * @return
	 * @throws Exception
	 */
	private JPanel getDBPanel() throws Exception {
		JPanel panel = new JPanel(new BorderLayout()); //
		list_db = new BillListPanel(new ServerTMODefine("/cn/com/infostrategy/bs/sysapp/install/templetdata/pub_option_CODE1.xml")); //
		list_db.setQuickQueryPanelVisiable(true); //
		list_db.setAllBillListBtnVisiable(false); //

		WLTButton btn_insert = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT); //
		WLTButton btn_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); //
		WLTButton btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE); //
		list_db.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete }); //
		list_db.repaintBillListButton(); //

		if (initOptions != null) {
			list_db.queryDataByCondition("parkey in (" + TBUtil.getTBUtil().getInCondition(initOptions) + ")", "parkey asc"); //
		}
		panel.add(list_db); //
		return panel; //
	}

	private JPanel getSouthPanel() {
		btn_confirm = new WLTButton("�ر�"); //
		btn_reloadCache = new WLTButton("ˢ�»���"); //

		btn_confirm.addActionListener(this);
		btn_reloadCache.addActionListener(this);

		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout()); //

		panel.add(btn_reloadCache); //
		panel.add(btn_confirm); //

		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			this.setCloseType(1); //
			this.dispose(); //
		} else if (e.getSource() == btn_reloadCache) { //
			try {
				if (!MessageBox.confirm(this, "���Ƿ������ˢ�»�����?�⽫Ӱ�������˵Ĳ���Ч��!\r\nˢ�º���Ҫ���µ�¼���ܿ���UI�˲���Ч��!")) {
					return;
				} //
				String[][] str_result = UIUtil.getCommonService().reLoadDataFromDB(false); //
				if (str_result != null) {
					ClientEnvironment.setClientSysOptions(UIUtil.getCommonService().getAllOptions()); //���µõ����в���
				}
				MessageBox.show(this, "ˢ�²����ɹ�,��Ҫ���µ�¼���ܿ���UI�˲���Ч��!"); //
			} catch (Exception ex) {
				MessageBox.showException(this, ex); //
			}
		}
	}
}
