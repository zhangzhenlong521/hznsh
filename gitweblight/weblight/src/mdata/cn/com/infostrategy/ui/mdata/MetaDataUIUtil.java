package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillItemVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.to.mdata.templetvo.TMO_Pub_Templet_1_item;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTComBoBoxUI;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTPanelUI;

public class MetaDataUIUtil {
	private TBUtil tbutil = null;

	/**
	 * ��ֱ��ģ��༭����ʱ,�����µĻ���Ҫ���и��ӵļ���! ������XML��DB��ʵ�ʴ���������������δ���!!
	 * 
	 * @param _container
	 * @param _buildFromType
	 * @param _buildFromInfo
	 * @param _templetCode
	 * @return
	 * @throws Exception
	 */
	public boolean checkTempletIsCanConfig(java.awt.Container _container, String _buildFromType, String _buildFromInfo, String _templetCode, String _templetName, boolean _isquiet) throws Exception {
		return this.checkTempletIsCanConfig(_container, _buildFromType, _buildFromInfo, _templetCode, _templetName, _isquiet, null);
	}

	/**
	 * ��ģ��༭���󵽴�����
	 */
	public boolean modifyTemplet(java.awt.Container _container, String _buildFromType, String _buildFromInfo, String _templetCode, String _templetName, boolean _isquiet, String _colKey) throws Exception {
		return this.checkTempletIsCanConfig(_container, _buildFromType, _buildFromInfo, _templetCode, _templetName, _isquiet, _colKey);
	}

	public boolean modifyTemplet2(java.awt.Container _container, String _buildFromType, String _buildFromInfo, String _templetCode, String _templetName, boolean _isquiet, String _colKey) throws Exception {
		return this.checkTempletIsCanConfig(_container, _buildFromType, _buildFromInfo, _templetCode, _templetName, _isquiet, _colKey);
	}

	public boolean checkTempletIsCanConfig(java.awt.Container _container, String _buildFromType, String _buildFromInfo, String _templetCode, String _templetName, boolean _isquiet, String _colKey) throws Exception {

		String tableName = null;
		if (_container instanceof BillCardPanel) {
			tableName = ((BillCardPanel) _container).getTempletVO().getSavedtablename();
		} else if (_container instanceof BillListPanel) {
			tableName = ((BillListPanel) _container).getTempletVO().getSavedtablename();
		} else if (_container instanceof BillTreePanel) {
			tableName = ((BillTreePanel) _container).getTempletVO().getSavedtablename();
		} else if (_container instanceof BillPropPanel) {
		}

		if (_buildFromType.equals("CLASS")) {
			MessageBox.show(_container, "ֱ���������[" + _buildFromInfo + "]����,�����Ա༭!\r\nֻ���ɿ�����Աͨ���޸Ĵ������޸�!"); //
			return false; //
		} else if (_buildFromType.equals("XML")) { // ֱ������ָ����XML�д�����!!!
			// ���繤������һЩ��!!
			MessageBox.show(_container, "ֱ��ָ����XML[" + _buildFromInfo + "]����,�����Ա༭!\r\nֻ���ɿ�����Աͨ���޸Ĵ������޸�!"); //
			return false; //
		} else if (_buildFromType.equals("XML2") || _buildFromType.equals("DB")) { // �ȴ�DB��û�ҵ�,ת��XML�д�����!
			HashMap returnBooleans = isDBAndXMLHaveTempletByCode(_templetCode); // ���ݿ����Ƿ���!
			boolean isDBHave = (Boolean) returnBooleans.get("ISDBHAVE"); // ���ݿ����Ƿ���
			boolean isXMLHave = (Boolean) returnBooleans.get("ISXMLHAVE"); // XML���Ƿ���?
			_buildFromInfo = (String) returnBooleans.get("XMLINFO");//���������ʾ��Ϣ�������м������к���ģ�塾���/2012-07-18��
			String str_xmlpath = (String) returnBooleans.get("XMLPATH");//������xml��·��

			if (isDBHave) { // ������ݿ�����
				if (isXMLHave) { // ���XML�ļ�����
					if (_isquiet) { // ����ǰ���ģʽ!
						return true; //
					} else {
						int li_result = MessageBox.showOptionDialog(_container, "��ģ��Ĵ���������[" + _buildFromType + "],������Դ��[" + _buildFromInfo + "],ģ�������[" + _templetCode + "]\r\n�������ݿ���ϵͳXMLע���ж��и�ģ��,����������²���:", "��ʾ", new String[] { "�鿴XML�е�", "�Ƚ�����", "ɾ�����ݿ��е�", "�༭���ݿ��е�", "�鿴����ģ��", " ȡ  �� " }, 570, 150); //
						if (li_result == 0) { //
							viewXmlTempletByCode(_container, _templetCode, _colKey);
							return false; //
						} else if (li_result == 1) { //
							compareTempletFromXmlAndDatabase(_container, _templetCode);
							return false; //
						} else if (li_result == 2) { //
							if (MessageBox.confirm(_container, "��ȷ��Ҫɾ�����ݿ��еļ�¼��")) {//������Ա������ɾ����������������ʾ�����/2018-06-15��
								deleteOneTempletByCode(_templetCode); // ɾ��
								MessageBox.show(_container, "ɾ�������ݿ��еļ�¼,�����´�ҳ���ʹ��XML�е�!"); //
							}
							return false; //
						} else if (li_result == 3) { //
							editDbTempletByCode(_container, _templetCode, _colKey);
							return true; // ֱ�ӱ༭���ݿ��е�
						} else if (li_result == 4) { //�鿴����ģ��
							viewRefTemplet(_container, tableName);
							return true; //
						} else if (li_result == 5) { //
							return false; //
						} else {
							return false;
						}
					}
				} else { // ���XML��û��
					if (_isquiet) {
						return true; //
					} else {
						editDbTempletByCode(_container, _templetCode, _colKey);
						return true;
					}
				}
			} else { // ������ݿ���û��!!
				if (isXMLHave) { // ���XML����
					if (_isquiet) { // ����ǰ���ģʽ!
						MessageBox.show(_container, "��ģ��Ĵ���������[" + _buildFromType + "],������Դ��[" + _buildFromInfo + "],ģ�������[" + _templetCode + "]!\r\n�������ݿ���û��,XMLע������,���븴�ƹ������ܽ��иô���!"); //
						return false; //
					} else {
						int li_result = MessageBox.showOptionDialog(_container, "��ģ��Ĵ���������[" + _buildFromType + "],������Դ��[" + _buildFromInfo + "],ģ�������[" + _templetCode + "]!\r\n�������ݿ���û��,XMLע������,�������༭���޸ı���Ҫ�ȸ��Ƶ����ݿ��в���!", "��ʾ", new String[] { "�鿴XML�е�", "���Ƶ����ݿ���", " ȡ  �� " }, 550, 150); //
						if (li_result == 0) {
							viewXmlTempletByCode(_container, _templetCode, _colKey);
							// MessageBox.show(_container, "ֱ�Ӳ鿴XM�е�!������....");
							// //
							return false; //
						} else if (li_result == 1) {
							importXMLToDB(_container, str_xmlpath, _templetCode, _templetName, _colKey); //
							return false;
						} else if (li_result == 2) {
							return false;
						} else {
							return false; //
						}
					}
				} else {
					MessageBox.show(_container, "��ģ��Ĵ���������[" + _buildFromType + "],������Դ��[" + _buildFromInfo + "],ģ�������[" + _templetCode + "]!\r\n������XML�����ݿ��ж�û�ж�����Ϣ,���ǲ�������,���ܱ�����ɾ����,����ϵͳ��������ϵ!"); //
					return false; //
				}
			}
		} else {
			MessageBox.show(_container, "δ֪�Ĵ�����ʽ[" + _buildFromType + "],���ܱ༭"); //
			return false; //
		}
	}

	/**
	 * ȥ���ݿ���ʵ�ʲ����Ƿ��и�ģ��!!!
	 * 
	 * @param _code
	 * @return
	 * @throws Exception
	 */
	private HashMap isDBAndXMLHaveTempletByCode(String _code) throws Exception {
		HashMap parMap = new HashMap(); //
		parMap.put("templetcode", _code); // ģ�����
		HashMap returnMap = UIUtil.commMethod("cn.com.infostrategy.bs.mdata.MetaDataBSUtil", "checkDBAndRegistXMLHaveTemplet", parMap); // Զ�̵���!!!
		//		boolean isDBHave = (Boolean) returnMap.get("ISDBHAVE"); //
		//		boolean isXMLHave = (Boolean) returnMap.get("ISXMLHAVE"); //
		return returnMap; //

	}

	private void viewRefTemplet(Container _parent, String tableName) {
		if (tableName == null) {
			MessageBox.show(_parent, "�������Ϊ��!");
			return;
		}
		BillDialog bd = new BillDialog(_parent, "����ģ��", 1020, 500);//sunfujun/20120621/̫���и���ťû��ʾ����
		MetaTempletConfigPanel cp = new MetaTempletConfigPanel();
		cp.initialize();
		cp.getSearch_box().setSelectedItem("ALL");
		cp.getSearch_table().setText(tableName);
		cp.onSearch();
		bd.add(cp);
		bd.setVisible(true);
	}

	private boolean importXMLToDB(Container _parent, String _xmlFileName, String templetcode, String _templetName, String _colKey) throws Exception {
		ShowCopyTempleteDialog showCopyTempleteDialog = new ShowCopyTempleteDialog(_parent, _templetName, templetcode, _templetName);
		showCopyTempleteDialog.getTemplet_code_text().setText(templetcode);//������Ҫ����һ�£�ʹ���Ƶ����ݿ��е�ģ�����͸��Ƶ�xmlģ�����һ�£������Ժ��ڵ���xml�Ϳ����滻�ˡ����/2012-07-16��
		showCopyTempleteDialog.setVisible(true);
		if (showCopyTempleteDialog.getCloseType() == 0) {
			FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //
			HashMap param = new HashMap();
			param.put("pub_templet_1_templetcode", showCopyTempleteDialog.getTempleteCode());
			param.put("pub_templet_1_templetname", showCopyTempleteDialog.getTempleteName());
			service.importRecordsXMLToTable(new String[] { _xmlFileName }, null, true, param); // ������������!!!
			if (MessageBox.confirm(_parent, "���Ƶ����ݿ��гɹ�!�������Ƿ��������༭?")) { //
				return editDbTempletByCode(_parent, showCopyTempleteDialog.getTempleteCode(), _colKey); //
			} else {
				return false; //
			}
		} else {
			return false;
		}
	}

	/**
	 * ����ģ�����ɾ��ĳ��ģ������!!
	 * 
	 * @param _code
	 */
	private void deleteOneTempletByCode(String _code) throws Exception {
		HashMap parMap = new HashMap(); //
		parMap.put("templetcode", _code); // ģ�����!!!
		UIUtil.commMethod("cn.com.infostrategy.bs.mdata.MetaDataBSUtil", "deleteOneTempletByCode", parMap); // Զ�̵���!!
	}

	/**
	 * ȡ�ÿ�¡�Ķ���!!��Ϊ��UIRefPanel��QueryCPanel_UIRefPanel���õ������,���Է��ڹ�ʽ����!!
	 * 
	 * @return
	 */
	public CommUCDefineVO cloneCommUCDefineVO(CommUCDefineVO _olddfVO, BillPanel billPanel) {
		if (billPanel == null) {
			return _olddfVO;
		}
		try {
			String[] str_allKeys = _olddfVO.getAllConfKeys(); //
			if (str_allKeys == null || str_allKeys.length <= 0) {
				return _olddfVO; // ���û�в���,��ֱ�ӷ���ԭ����!!!
			}
			TBUtil tbUtil = new TBUtil(); //
			CommUCDefineVO cloneUCDfVO = (CommUCDefineVO) tbUtil.deepClone(_olddfVO); // ��¡һ��!!!�ؼ�
			boolean isConverted = false;
			for (int i = 0; i < str_allKeys.length; i++) {
				String str_value = cloneUCDfVO.getConfValue(str_allKeys[i]); // ԭ���Ĺ�ʽ!!
				String[] str_macro = tbUtil.getFormulaMacPars(str_value, "${", "}"); //
				if (str_macro != null && str_macro.length > 0) { // �������Ҫ�����滻��!!
					for (int j = 0; j < str_macro.length; j++) { // ���������滻!!!
						String str_billValue = null;
						if (billPanel instanceof BillCardPanel) { // ����ǿ�Ƭ
							str_billValue = ((BillCardPanel) billPanel).getBillVO().getRealValue(str_macro[j]); //
						} else if (billPanel instanceof BillListPanel) { // ������б�!!
							BillVO billVO = ((BillListPanel) billPanel).getSelectedBillVO(); //
							if (billVO != null) {
								str_billValue = billVO.getRealValue(str_macro[j]); //
							}
						} else if (billPanel instanceof BillQueryPanel) {
							str_billValue = ((BillQueryPanel) billPanel).getRealValueAt(str_macro[j]); //
						}
						/*
						 * �Ժ���ܸ��ݲ���,����ֱ�ӵ�����ʾ��,������¼����һ���ֶε�ֵ!!!
						 * ���������ϵͳ�������Ǹÿؼ��Ĳ�����? �����о�Ӧ���Ǹ�ϵͳ����!!! String
						 * str_itemName =
						 * this.templetItemVO.getPub_Templet_1VO().getItemVo(str_macro[j]).getItemname();
						 * MessageBox.show(this, "����¼��[" + str_itemName+"]��ֵ!"); //
						 * return null;
						 */
						if (str_billValue == null || str_billValue.trim().equals("")) {
							str_value = tbUtil.replaceAll(str_value, "${" + str_macro[j] + "}", "-99999"); // �������滻-9999,������ʾ!!!
						} else { // �Ƿ����ֱ�ӱ���,���ѱ���¼��ĳ��ֵ!
							str_value = tbUtil.replaceAll(str_value, "${" + str_macro[j] + "}", str_billValue); // �滻һ��!!!
						}
					}
					cloneUCDfVO.setConfValue(str_allKeys[i], str_value); // ��������
					isConverted = true; // �滻����!!
				}
			}
			if (isConverted) { // ����������滻������,�򷵻�
				return cloneUCDfVO; // ���ؿ�¡�Ķ���!!
			} else { // ���û�з���ת��,�򷵻�ԭ����! ��Ϊ�󲿷����������,��Ϊ�¿�¡��������,Ϊ�˱�����ԭ����!!
				return _olddfVO; //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
			MessageBox.show(billPanel, "��¡���滻��ʽ�е�${itemkey}�ĺ����ʱ�����쳣!!\r\n��������ǿ�鿴��ϸ��Ϣ!"); //
			return null;
		}
	}

	/*
	 * ����ģ������xml�е�ģ�� ���_colKeyֵ�������ǵ�һ�ֱ༭��ʽ���༭�С�
	 */
	private void viewXmlTempletByCode(Container _parent, String _templetCode, String _colKey) {
		if (_colKey != null && !_colKey.equals("")) {
			this.TempletModify1(_parent, _templetCode, _colKey);
		} else {
			TempletModify2 mfdialog = new TempletModify2(_parent, _templetCode, false, true);
		}
	}

	private boolean editDbTempletByCode(Container _parent, String _templetCode, String _colKey) {
		if (_colKey != null && !"".equals(_colKey.trim())) {
			try {
				String str_pk_templet = UIUtil.getStringValueByDS(null, "select pk_pub_templet_1 from pub_templet_1 where templetcode='" + _templetCode + "'"); //
				String str_id = UIUtil.getStringValueByDS(null, "select pk_pub_templet_1_item from pub_templet_1_item where pk_pub_templet_1=" + str_pk_templet + " and itemkey='" + _colKey + "'"); //����!!!ȥ���ݿ�����ȡһ��!!
				if (str_id == null) {
					MessageBox.showTextArea(_parent, "��ǰģ���ӱ��м�¼������Ϊ��,������ֱ�����ഴ��,����!");
				}
				int li_return = 1;
				if (!getTBUtil().getSysOptionBooleanValue("������Աģʽ", false)) {
					li_return = MessageBox.showOptionDialog(_parent, "��ȷ��ʹ������ģʽ��������?", "��ʾ", new String[] { "ʵʩ��Աģʽ", "������Աģʽ", "ȡ��" }, 0, 325, 120); //	
				}
				if (li_return == 0 || li_return == 1) { //�����ʵʩ��Ա/������Աģʽ!
					BillCardDialog dialog = new BillCardDialog(_parent, "�б༭", 900, 700, new TMO_Pub_Templet_1_item(li_return == 0 ? true : false), "pk_pub_templet_1_item='" + str_id + "'"); //
					dialog.setVisible(true); //
					if (dialog.getCloseType() == 1) {
						return true;
					}
				}
			} catch (Exception ex) {
				MessageBox.showException(_parent, ex); //
			}
		} else {
			int li_return = 1;
			if (!getTBUtil().getSysOptionBooleanValue("������Աģʽ", false)) {
				li_return = MessageBox.showOptionDialog(_parent, "��ȷ��ʹ������ģʽ��������?", "��ʾ", new String[] { "ʵʩ��Աģʽ", "������Աģʽ", "ȡ��" }, 0, 325, 120); //	
			}
			if (li_return == 0 || li_return == 1) { //ʵʩ��Աģʽ!
				TempletModify2 m2 = new TempletModify2(_parent, _templetCode, li_return == 0 ? true : false);
				if (m2.getCloseType() == 1) {
					return true;
				}
			}
		}
		return false;
	}

	// ��һ�ֱ༭��ʽ �鿴xml��[����2012-04-26]
	private void TempletModify1(Container _parent, String _templetCode, String _colKey) {
		BillCardPanel cardPanel = new BillCardPanel(new TMO_Pub_Templet_1_item());
		cardPanel.setEditable(true);
		FrameWorkMetaDataServiceIfc service;
		try {
			service = UIUtil.getMetaDataService();
			DefaultTMO tmo = service.getDefaultTMOByCode(_templetCode, 1); // ȡ��
			HashVO[] vos = tmo.getPub_templet_1_itemData();
			Object obj[][] = null;
			for (int i = 0; i < vos.length; i++) {
				if (vos[i].getStringValue("itemkey").equals(_colKey)) {
					obj = service.getBillListDataByHashVOs(cardPanel.getTempletVO().getParPub_Templet_1VO(), new HashVO[] { vos[i] }); // ֱ�Ӹ���hashvo�õ��ؼ�����
				}
			}
			if (obj != null && obj.length > 0) {
				cardPanel.setValue(obj[0]); //
			}

		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		final BillDialog dialog = new BillDialog(_parent, 900, 700);
		dialog.setLayout(new BorderLayout());
		dialog.add(cardPanel, BorderLayout.CENTER);
		WLTPanel btn_panel = new WLTPanel(WLTPanel.HORIZONTAL_LEFT_TO_RIGHT, new FlowLayout());
		WLTButton btn = new WLTButton("�ر�");
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
		btn_panel.add(btn);
		dialog.add(btn_panel, BorderLayout.SOUTH);
		dialog.setVisible(true);
	}

	/*
	 * �Ա�ģ�� xml�к�ģ���У�
	 */
	private void compareTempletFromXmlAndDatabase(Container _parent, String _templetCode) throws Exception {
		new TempletCompareDialog(_parent, _templetCode);
	}

	/**��sunfujun/20120508/��ģ������ɾ���ֶε��Ż�_xch��
	 * @param _parentContainer
	 * @param str_tablename
	 * @param str_itemkey
	 * @param str_itemname
	 */
	public void showDropColumnPanel(Container _parentContainer, String str_tablename, String str_itemkey, String str_itemname) {
		try {
			FrameWorkMetaDataServiceIfc service = UIUtil.getMetaDataService();
			String dropSql = service.getDropColumnSql(null, str_tablename, str_itemkey).trim();
			//��ô���ORACEL ִ��SQLʱ���������;�����ˣ�
			dropSql = dropSql.replaceAll(";", " ");
			JLabel sqlLabel = new JLabel("����ִ������SQL:");
			sqlLabel.setFont(LookAndFeel.font); //
			sqlLabel.setPreferredSize(new Dimension(200, 22));
			JTextField sqlArea = new JTextField();
			sqlArea.setPreferredSize(new Dimension(420, 22));
			sqlArea.setFont(LookAndFeel.font); //
			sqlArea.setText(dropSql);
			VFlowLayoutPanel vf = new VFlowLayoutPanel(new JComponent[] { sqlLabel, sqlArea });
			vf.setUI(new WLTPanelUI());
			if (JOptionPane.showConfirmDialog(_parentContainer, vf, "��ȷ��ɾ���ֶ���", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, UIUtil.getImage("info.gif")) == JOptionPane.OK_OPTION) {
				if (dropSql != null && !"".equals(dropSql)) {
					UIUtil.executeUpdateByDS(null, dropSql);
					MessageBox.show("�ڱ�[" + str_tablename + "]��ɾ��[" + str_itemname + "]�гɹ�!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(_parentContainer, e.getMessage());
		}
	}

	/**
	 * ��sunfujun/20120508/��ģ������ɾ���ֶε��Ż�_xch��
	 * @param _parentContainer
	 * @param str_tablename
	 * @param str_itemkey
	 * @param str_itemname
	 * @param str_itemtype
	 */
	public void showAddColumnPanel(Container _parentContainer, String str_tablename, String str_itemkey, String str_itemname, String str_itemtype) {
		try {
			final FrameWorkMetaDataServiceIfc service = UIUtil.getMetaDataService();
			JLabel keyLabel = new JLabel("�ֶ��� [" + str_itemkey + "]", JLabel.RIGHT);
			keyLabel.setFont(LookAndFeel.font);
			keyLabel.setPreferredSize(new Dimension(keyLabel.getPreferredSize().width, 22));
			JLabel typeLabel = new JLabel("  ���� ", JLabel.RIGHT);
			typeLabel.setFont(LookAndFeel.font);
			typeLabel.setPreferredSize(new Dimension(60, 22));
			final JComboBox typeCom = new JComboBox(new String[] { "�ַ�", "����" });
			typeCom.setUI(new WLTComBoBoxUI());
			typeCom.setPreferredSize(new Dimension(100, 22));
			typeCom.setSelectedIndex(0);
			JLabel lengthLabel = new JLabel(" ���� ", JLabel.RIGHT);
			lengthLabel.setFont(LookAndFeel.font);
			lengthLabel.setPreferredSize(new Dimension(80, 22));
			final JTextField lengthFied = new JFormattedTextField();
			lengthFied.setHorizontalAlignment(JTextField.RIGHT); //
			lengthFied.setDocument(new NumberFormatdocument());
			lengthFied.setText("50");
			if (WLTConstants.COMP_DATE.equals(str_itemtype)) {
				lengthFied.setText("10");
			} else if (WLTConstants.COMP_DATETIME.equals(str_itemtype)) {
				lengthFied.setText("19");
			} else if (WLTConstants.COMP_BIGAREA.equals(str_itemtype)) {
				lengthFied.setText("100");
			} else if (WLTConstants.COMP_TEXTAREA.equals(str_itemtype)) {
				lengthFied.setText("100");
			} else if (WLTConstants.COMP_CHECKBOX.equals(str_itemtype)) {
				lengthFied.setText("1");
			} else if (WLTConstants.COMP_NUMBERFIELD.equals(str_itemtype)) {
				lengthFied.setText("22");
				typeCom.setSelectedIndex(1);
			}
			lengthFied.setPreferredSize(new Dimension(50, 22));
			JLabel sqlLabel = new JLabel("����ִ������SQL:");
			sqlLabel.setFont(LookAndFeel.font);
			final JTextField sqlArea = new JTextField();
			sqlArea.setPreferredSize(new Dimension(420, 22));
			sqlArea.setFont(LookAndFeel.font);
			final String tablename = str_tablename;
			final String itemkey = str_itemkey;
			typeCom.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					try {
						sqlArea.setText(service.getAddColumnSql(null, tablename, itemkey, typeCom.getSelectedItem().toString().equals("����") ? "decimal" : "varchar", lengthFied.getText()).replaceAll(";", " ").trim());
						//�ǲ��ǲ�̫���أ����ǵ����������д˲������Բ�����д���룬ÿ�ζ�Զ�̵���
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});
			lengthFied.addKeyListener(new KeyAdapter() {
				public void keyReleased(KeyEvent e) {
					try {
						sqlArea.setText(service.getAddColumnSql(null, tablename, itemkey, typeCom.getSelectedItem().toString().equals("����") ? "decimal" : "varchar", lengthFied.getText()).replaceAll(";", " ").trim());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});
			sqlArea.setText(service.getAddColumnSql(null, tablename, itemkey, typeCom.getSelectedItem().toString().equals("����") ? "decimal" : "varchar", lengthFied.getText()).replaceAll(";", " ").trim());
			VFlowLayoutPanel vf = new VFlowLayoutPanel(new JComponent[] { new HFlowLayoutPanel(new JComponent[] { keyLabel, typeLabel, typeCom, lengthLabel, lengthFied }), sqlLabel, sqlArea });
			vf.setUI(new WLTPanelUI());
			if (JOptionPane.showConfirmDialog(_parentContainer, vf, "��ȷ�������ֶ���", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, UIUtil.getImage("info.gif")) == JOptionPane.OK_OPTION) {
				String sql = sqlArea.getText();
				if (sql != null && !"".equals(sql)) {
					UIUtil.executeUpdateByDS(null, sql);
					MessageBox.show("�ڱ�[" + str_tablename + "]�ϴ���[" + str_itemname + "]�гɹ�!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(_parentContainer, e.getMessage());
		}
	}

	public void showItemState(Container parent, Pub_Templet_1_ItemVO[] templetItemVOs) {
		WLTPanel p = new WLTPanel(0);
		p.setLayout(new BorderLayout());
		if (templetItemVOs != null && templetItemVOs.length > 0) {
			BillListPanel bl = new BillListPanel(new DefaultTMO("����״̬", new String[][] { { "��������", "100" }, { "������Ƿ����", "150" }, { "�Ƿ���뱣��", "150" }, { "��ѯ���Ƿ����", "150" } }));
			WLTLabel fi = new WLTLabel("�����ɫ������뱣�浫�޷�����", -1);
			fi.setPreferredSize(new Dimension(190, 20));
			fi.setForeground(Color.RED);
			WLTLabel s = new WLTLabel("���������򾯸���", -1);
			s.setPreferredSize(new Dimension(120, 20));
			s.addStrItemColor("��", Color.RED);
			WLTLabel t = new WLTLabel("������޷���ѯ", -1);
			t.setPreferredSize(new Dimension(98, 20));
			t.setForeground(Color.BLUE);
			WLTLabel fo = new WLTLabel("��������뱣��", -1);
			fo.setPreferredSize(new Dimension(110, 20));
			fo.setForeground(Color.GREEN);
			bl.getBillListBtnPanel().getPanel_flow().add(fi);
			bl.getBillListBtnPanel().getPanel_flow().add(s);
			bl.getBillListBtnPanel().getPanel_flow().add(t);
			bl.getBillListBtnPanel().getPanel_flow().add(fo);
			bl.getTable().getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
				public Component getTableCellRendererComponent(JTable jtable, Object obj, boolean flag, boolean flag1, int i, int j) {
					JLabel label = (JLabel) super.getTableCellRendererComponent(jtable, obj, flag, flag1, i, j);
					WLTLabel a = new WLTLabel(label.getText(), 0);
					a.addStrItemColor("��", Color.RED);
					a.addStrItemColor("��", Color.BLUE);
					a.addStrItemColor("��", Color.GREEN);
					if (flag1) {
						a.setBorder(BorderFactory.createLineBorder(new Color(99, 130, 191), 0)); //
					} else {
						a.setBorder(BorderFactory.createEmptyBorder()); //
					}
					if (flag) {
						a.setBackground(LookAndFeel.tablerowselectbgcolor);
					} else {
						if (i % 2 == 0) {
							a.setBackground(LookAndFeel.table_bgcolor_odd); //
						} else {
							a.setBackground(LookAndFeel.tablebgcolor); //
						}
					}
					if (obj != null && obj instanceof BillItemVO) {
						try {
							String str_foreColor = ((BillItemVO) obj).getForeGroundColor(); //���д�����߼���ǰ��֪��ô���˸���!!������ɫ��ʽ����������!
							if (str_foreColor != null) { //���ǰ����ɫ��Ϊ��
								a.addStrItemColor(str_foreColor.split(";")[0], getTBUtil().getColor(str_foreColor.split(";")[1]));
							}
						} catch (Exception exx) {
							exx.printStackTrace(); //
						}
					}
					return a;
				}
			});
			bl.getTable().getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
				public Component getTableCellRendererComponent(JTable jtable, Object obj, boolean flag, boolean flag1, int i, int j) {
					JLabel a = (JLabel) super.getTableCellRendererComponent(jtable, obj, flag, flag1, i, j);
					return getTableCellRendererComponent_(a, jtable, obj, flag, flag1, i, j);
				}
			});
			bl.getTable().getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
				public Component getTableCellRendererComponent(JTable jtable, Object obj, boolean flag, boolean flag1, int i, int j) {
					JLabel a = (JLabel) super.getTableCellRendererComponent(jtable, obj, flag, flag1, i, j);
					return getTableCellRendererComponent_(a, jtable, obj, flag, flag1, i, j);
				}
			});
			bl.getTable().getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
				public Component getTableCellRendererComponent(JTable jtable, Object obj, boolean flag, boolean flag1, int i, int j) {
					JLabel a = (JLabel) super.getTableCellRendererComponent(jtable, obj, flag, flag1, i, j);
					return getTableCellRendererComponent_(a, jtable, obj, flag, flag1, i, j);
				}
			});
			for (int i = 0; i < templetItemVOs.length; i++) {
				if (templetItemVOs[i].isCardisshowable() || templetItemVOs[i].isListisshowable()) {
					StringItemVO aa = new StringItemVO(templetItemVOs[i].getItemname());
					if (!templetItemVOs[i].isCanSave() && templetItemVOs[i].isNeedSave()) {
						aa.setForeGroundColor(aa.getStringValue() + ";FF0000");
					}
					if ("Y".equalsIgnoreCase(templetItemVOs[i].getIsmustinput2()) || "W".equalsIgnoreCase(templetItemVOs[i].getIsmustinput2())) {
						aa.setStringValue(aa.getStringValue() + "��");
					}
					if (!templetItemVOs[i].isViewColumn()) {
						aa.setStringValue(aa.getStringValue() + "��");
					}
					if (!templetItemVOs[i].isNeedSave()) {
						aa.setStringValue(aa.getStringValue() + "��");
					}
					bl.insertRow(bl.getRowCount(), new Object[] { (bl.getRowCount() + 1) + "", aa, new StringItemVO(templetItemVOs[i].isCanSave() ? "��" : "��"), new StringItemVO(templetItemVOs[i].isNeedSave() ? "��" : "��"), new StringItemVO(templetItemVOs[i].isViewColumn() ? "��" : "��") });

					bl.setRowStatusAs(bl.getRowCount() - 1, WLTConstants.BILLDATAEDITSTATE_INIT);
				}
			}
			p.add(bl);
		}
		BillDialog bd = new BillDialog(parent, "ģ������״̬�鿴", 800, 500);
		bd.add(p);
		bd.addConfirmButtonPanel(2);
		bd.setVisible(true);
	}

	public JLabel getTableCellRendererComponent_(JLabel a, JTable jtable, Object obj, boolean flag, boolean flag1, int i, int j) {
		if ("��".equals(a.getText())) {
			a.setIcon(UIUtil.getImage("zt_028.gif"));
			a.setText("");
		} else if ("��".equals(a.getText())) {
			a.setIcon(UIUtil.getImage("zt_031.gif"));
			a.setText("");
		}
		if (flag1) {
			a.setBorder(BorderFactory.createLineBorder(new Color(99, 130, 191), 0)); //
		} else {
			a.setBorder(BorderFactory.createEmptyBorder()); //
		}
		if (flag) {
			a.setBackground(LookAndFeel.tablerowselectbgcolor);
		} else {
			if (i % 2 == 0) {
				a.setBackground(LookAndFeel.table_bgcolor_odd); //
			} else {
				a.setBackground(LookAndFeel.tablebgcolor); //
			}
		}
		return a;
	}

	public TBUtil getTBUtil() {
		if (tbutil == null) {
			tbutil = new TBUtil();
		}
		return tbutil;
	}
}
