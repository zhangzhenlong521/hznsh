package cn.com.infostrategy.ui.sysapp.dataaccess;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTTextArea;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;
import cn.com.infostrategy.ui.mdata.styletemplet.t06.AbstractStyleWorkPanel_06;

/**
 * ����Ȩ������!!!
 * @author xch
 *
 */
public class DataPolicyConfigWKPanel extends AbstractStyleWorkPanel_06 {

	private static final long serialVersionUID = 1L;

	@Override
	public String getParentTempletCode() {
		return "PUB_DATAPOLICY_CODE1";
	}

	@Override
	public String getChildTempletCode() {
		return "PUB_DATAPOLICY_B_CODE1";
	}

	@Override
	public String getParentAssocField() {
		return "id";
	}

	@Override
	public String getChildAssocField() {
		return "datapolicy_id";
	}

	@Override
	public void afterInitialize() throws Exception {
		//����Ӱ�ť!
		BillListPanel parentList = getParentBillListPanel(); //
		WLTButton btn_testCorp = new WLTButton("��������"); //
		btn_testCorp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onTestCorpPolicy(); //
			}
		}); //
		parentList.addBatchBillListButton(new WLTButton[] { btn_testCorp }); //
		parentList.repaintBillListButton(); //

		//�ӱ�Ӱ�ť!!
		BillListPanel billList = getChildBillListPanel(); //
		WLTButton btn_moveup = WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEUP); //
		WLTButton btn_movedown = WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEDOWN); //
		WLTButton btn_save = WLTButton.createButtonByType(WLTButton.LIST_SAVE, "����˳��"); //
		billList.addBatchBillListButton(new WLTButton[] { btn_moveup, btn_movedown, btn_save }); //
		billList.repaintBillListButton(); //

	}

	protected void onTestCorpPolicy() {
		try {
			BillListPanel parentList = getParentBillListPanel(); //
			BillVO billVO = parentList.getSelectedBillVO(); //
			if (billVO == null) {
				MessageBox.show(this, "��ѡ��һ�����Խ��д˲���!"); //
				return; //
			}
			String str_policyName = billVO.getStringValue("name"); //��������!!

			JLabel label_1 = new JLabel("��Ա����:", SwingConstants.RIGHT); //
			JTextField textfield_1 = new JTextField(ClientEnvironment.getCurrLoginUserVO().getCode()); //
			JLabel label_2 = new JLabel("�Ƿ���㸽��SQL:", SwingConstants.RIGHT); //
			JCheckBox checkBox = new JCheckBox(); //
			checkBox.setSelected(true); //
			String str_toolTip = "�������SQL�е��ֶβ��ǻ������е�,�ᱨ��!��ֻ�иò��Ծ�����Ի����ĲŻ�ɹ�!"; //
			label_2.setToolTipText(str_toolTip); //
			checkBox.setToolTipText(str_toolTip); //

			label_1.setBounds(5, 5, 100, 20); //
			textfield_1.setBounds(110, 5, 100, 20); //
			label_2.setBounds(5, 30, 100, 20); //
			checkBox.setBounds(110, 30, 40, 20); //

			JPanel panel = new JPanel(null); //
			panel.add(label_1); //
			panel.add(textfield_1); //
			panel.add(label_2); //
			panel.add(checkBox);
			panel.setPreferredSize(new Dimension(225, 60)); //

			if (JOptionPane.showConfirmDialog(this, panel, "������һ����Ա����,��������Ļ���Ȩ��", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.YES_OPTION) {
				return; //
			}
			String str_userCode = textfield_1.getText(); //008834

			String[][] str_userIDs = UIUtil.getStringArrayByDS(null, "select id,name from pub_user where code='" + str_userCode + "'"); //
			if (str_userIDs == null || str_userIDs.length <= 0) {
				MessageBox.show(this, "������Ա����[" + str_userCode + "]û���ҵ�һ����Ա,���ܼ�������!"); //
				return; //
			}

			if (str_userIDs.length > 1) {
				MessageBox.show(this, "������Ա����[" + str_userCode + "]���ҵ���" + str_userIDs.length + "������Ա,���ܼ�������!"); //
				return; //
			}

			TBUtil tbUtil = new TBUtil(); //
			String str_userId = str_userIDs[0][0]; //��Աid
			String str_userName = str_userIDs[0][1]; //��Ա����!
			String str_title = "��Ա[" + str_userId + "/" + str_userCode + "/" + str_userName + "]ִ�в��ԡ�" + str_policyName + "���Ľ��,�Ƿ���㸽��SQL(����ʵ����������ֵ����)=[" + checkBox.isSelected() + "]"; //

			FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class);
			String[] str_result = service.getDataPolicyCondition(str_userId, str_policyName, 1, "id", null); // 

			Pub_Templet_1VO templetVO = UIUtil.getPub_Templet_1VO("PUB_CORP_DEPT_CODE1"); // ȡ��Ԫԭģ������
			templetVO.setDatapolicy(null); //��������
			templetVO.setBsdatafilterclass(null); // 
			templetVO.setDataconstraint(null); //
			BillTreePanel treePanel = new BillTreePanel(templetVO); //
			treePanel.getBtnPanel().setVisible(false); //
			if (str_result.length == 5) {
				String str_info = str_result[0]; //
				String str_realSql = str_result[1]; //
				String str_virtualCorpIds = str_result[2]; //������!
				String str_allCorpIds = str_result[3]; //
				String str_isAllCorp = str_result[4]; //

				if (str_virtualCorpIds != null) { //�����������!
					str_virtualCorpIds = str_virtualCorpIds.substring(str_virtualCorpIds.indexOf("(") + 1, str_virtualCorpIds.indexOf(")")); //
				}

				String str_treeSql = null; // 
				if (checkBox.isSelected()) { //����ǹ�ѡ�ϵ�
					if (str_virtualCorpIds != null) { //�����������!
						str_treeSql = "(" + str_realSql + ") or id in (" + str_virtualCorpIds + ")"; //
					} else {
						str_treeSql = str_realSql; //
					}
					if (str_virtualCorpIds != null) { //�����������
						String[] str_virtualIds = tbUtil.split(str_virtualCorpIds, ","); //
						treePanel.getVirtualCorpIdsHst().addAll(Arrays.asList(str_virtualIds)); //�ȼ���!
					}
				} else {

					if (str_isAllCorp.equals("Y")) { //�������������!
						str_treeSql = "1=1"; //
					} else { //���������������
						String[] str_idArray = tbUtil.split(str_allCorpIds, ";"); //
						if (str_virtualCorpIds != null) { //�����������!
							str_treeSql = " id in (" + tbUtil.getInCondition(str_idArray) + ") or id in (" + str_virtualCorpIds + ")"; //
						} else {
							str_treeSql = " id in (" + tbUtil.getInCondition(str_idArray) + ")"; //
						}
					}

					if (str_virtualCorpIds != null) { //�����������
						String[] str_virtualIds = tbUtil.split(str_virtualCorpIds, ","); //
						treePanel.getVirtualCorpIdsHst().addAll(Arrays.asList(str_virtualIds)); //�ȼ���!
					}
				}

				JTabbedPane tabbedPanel = new JTabbedPane();
				try {
					treePanel.queryDataByCondition(str_treeSql); //������ѯ!!!
					tabbedPanel.addTab("��������", treePanel); //
				} catch (Exception ex) {
					ex.printStackTrace(); //
					tabbedPanel.addTab("��������", new JLabel("��ѯ�����쳣:" + ex.getMessage() + ",�����̨�鿴��ϸ!")); //
				}

				JTextArea textArea_1 = getTextArea(); //
				textArea_1.setText(str_info);
				tabbedPanel.addTab("��ϸ�������", new JScrollPane(textArea_1)); //

				JTextArea textArea_2 = getTextArea(); //

				textArea_2.setText("��������ʵ�ʷ���SQL,�����ܼ��Ϻ���ĸ���SQL����Ӧһ��ҵ���,����һ����Ӧ����:\r\n" + str_realSql + "\r\n\r\n��������ʵ��ִ�е�SQL" + (str_virtualCorpIds == null ? "" : "���Զ����������������㡿") + "��\r\n" + str_treeSql);
				tabbedPanel.addTab("ʵ�ʷ���SQL", new JScrollPane(textArea_2)); //

				JTextArea textArea_3 = getTextArea(); //
				textArea_3.setText("���ص�������:��" + str_virtualCorpIds + "��,�Ƿ������л�����" + str_isAllCorp + "��,���صĻ���id�嵥:\r\n" + str_allCorpIds);
				tabbedPanel.addTab("���صĻ���Id�嵥", new JScrollPane(textArea_3)); //

				BillDialog dialog = new BillDialog(this, str_title, 900, 550); //
				dialog.getContentPane().add(tabbedPanel); //
				dialog.setVisible(true); //��ʾ����!!
			} else {
				if (str_result[1].equals("'ȫ������'='ȫ������'") || str_result[1].equals("99=99")) {
					treePanel.queryDataByCondition(str_result[1]); //������ѯ!!!

					JTabbedPane tabbedPanel = new JTabbedPane(); //
					tabbedPanel.addTab("��������", treePanel); //

					JTextArea textArea_1 = getTextArea(); //
					textArea_1.setText(str_result[0]);
					tabbedPanel.addTab("��ϸ�������", new JScrollPane(textArea_1)); //

					JTextArea textArea_2 = getTextArea(); //
					textArea_2.setText("��������ʵ�ʷ���SQL:\r\n" + str_result[1] + "\r\n\r\n��������ʵ��ִ�е�SQL��\r\n" + str_result[1]);
					tabbedPanel.addTab("ʵ�ʷ���SQL", new JScrollPane(textArea_2)); //

					BillDialog dialog = new BillDialog(this, str_title, 900, 550); //
					dialog.getContentPane().add(tabbedPanel); //
					dialog.setVisible(true); //��ʾ����!!
				} else {
					MessageBox.show(this, "û�з���ֵ,���ܸò���û��������ϸ��û��ƥ����һ������!\r\n����ִ�н����:\r\n" + str_result[0] + "\r\n\r\n���ص�SQL������:\r\n" + str_result[1]); //
				}
			}
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		}
	}

	private JTextArea getTextArea() {
		JTextArea textArea = new WLTTextArea(); //
		textArea.setOpaque(true); //
		textArea.setBackground(LookAndFeel.systembgcolor); //
		textArea.setWrapStyleWord(true); //
		textArea.setEditable(false); //
		return textArea;
	}
}
