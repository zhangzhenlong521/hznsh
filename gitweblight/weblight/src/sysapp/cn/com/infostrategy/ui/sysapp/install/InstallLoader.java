package cn.com.infostrategy.ui.sysapp.install;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.FrameWorkCommServiceIfc;
import cn.com.infostrategy.ui.common.IAppletLoader;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc;

/**
 * ��װ����,
 * @author xch
 *
 */
public class InstallLoader implements IAppletLoader, Serializable, ActionListener {

	private static final long serialVersionUID = -2689635210485628183L;
	private JPanel mainPanel = null; //
	private JButton btn_install = null; //��ť!!
	private JTextArea textArea = null; //�ı���!!
	private Vector v_msg = new Vector(); //
	private Font font = new Font("������", Font.PLAIN, 12); //
	private ArrayList al_toggleButtons = new ArrayList(); //���й�ѡ���ѡ��ť!!
	private HashMap initDataHis = new HashMap(); //��ʼ�����ݰ�װ��¼,���氲װ�Ļ���ǰ�档

	public void loadApplet(JApplet _applet, JPanel _mainPanel) throws Exception {
		mainPanel = _mainPanel; //
		mainPanel.removeAll(); //��������пؼ�
		mainPanel.setLayout(new BorderLayout()); //���ò�����

		_mainPanel.add(getNorthPanel(), BorderLayout.NORTH); //��������
		textArea = new JTextArea(); //
		textArea.setFont(font); //
		_mainPanel.add(new JScrollPane(textArea), BorderLayout.CENTER); //

		_mainPanel.updateUI(); //

		//������Ϣ��!!!
		new java.util.Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				dealMsg(); //		
			}
		}, 0, 50);
	}

	/**
	 * ��������!!!
	 * @return
	 */
	private JPanel getNorthPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 5)); //
		try {

			btn_install = new JButton("��װ"); //
			btn_install.setFont(font); //
			btn_install.setFocusable(false); //
			btn_install.setMargin(new Insets(0, 0, 0, 0)); //
			btn_install.setPreferredSize(new Dimension(80, 20)); //
			btn_install.addActionListener(this); //
			panel.add(btn_install); //

			//����ϵͳ������̬��������԰�װ��Щ��Ʒ����Ŀ??
			SysAppServiceIfc services = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
			String[][] str_installPackages = services.getAllInstallPackages(null); //ȡ�����а�װ�İ�!
			//for (int i = 0; i < str_installPackages.length; i++) {
			//System.out.println(str_installPackages[i][0] + "&" + str_installPackages[i][1] + "&" + str_installPackages[i][2]); //������������:��/com/pushworld/ipushgrc/bs/install/��������GRC��Ʒ����name=�Ϲ���չ���;xtdatadir=hegui@name=�ڲ����ƹ���;xtdatadir=neikong@name=ȫ����չ���;xtdatadir=fengxian@name=�������չ���;xtdatadir=caozuo��
			//}
			for (int i = 0; i < str_installPackages.length; i++) { //�������а�װ��!!
				String str_apptext = str_installPackages[i][2]; //�ò�Ʒ�ľ���Ӧ�õ�����!!
				if (str_apptext == null || str_apptext.trim().equals("")) { //���Ϊ��!!���ǹ�ѡ��!
					JCheckBox checkBox = new JCheckBox(str_installPackages[i][1]); //Ĭ�ϱ�����ѡ�е�!
					checkBox.setFont(font); //
					checkBox.setFocusable(false); //
					checkBox.setToolTipText("��װ��·��[" + str_installPackages[i][0] + "]"); //
					checkBox.putClientProperty("package_prefix", str_installPackages[i][0]); //ƽ̨�İ���!
					if (i < 2) { //ƽ̨�ͺϹ��Ʒ,��ǿ�й�ѡ��,�Ҳ��ɱ༭!!! Gwang
						checkBox.setSelected(true); //
						checkBox.setEnabled(false); //
					} else {
						checkBox.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0)); //
						//checkBox.setForeground(Color.BLUE); //
					}
					panel.add(checkBox); //
					al_toggleButtons.add(checkBox); //������������,��װʱ��Ҫ�������!
				} else { //����ж��Ӧ��,����һ�ѵ�ѡ��ť!!!
					JLabel label_1 = new JLabel(str_installPackages[i][1] + "��", SwingConstants.RIGHT); //
					label_1.setFont(font); //
					label_1.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0)); //
					//label_1.setForeground(Color.BLUE); //
					panel.add(label_1); //�ȼ���һ��˵��!!
					String[] str_apps = split(str_apptext, "@"); //�ָ�!���ж��Ӧ��!!!
					JRadioButton[] radioBtns = new JRadioButton[str_apps.length]; //
					ButtonGroup btnGroup = new ButtonGroup(); //
					for (int j = 0; j < str_apps.length; j++) { //
						HashMap parMap = convertStrToMapByExpress(str_apps[j], ";", "="); //������������map
						String str_appname = (String) parMap.get("name"); //����Ӧ�õ�����!!
						String str_xtdatadir = (String) parMap.get("xtdatadir"); //��ʼ�����ݵ���Ŀ¼!!
						radioBtns[j] = new JRadioButton(str_appname); //
						radioBtns[j].setFont(font); //
						radioBtns[j].setFocusable(false); //
						radioBtns[j].setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 2)); //
						radioBtns[j].setMargin(new Insets(0, 0, 0, 0)); //
						radioBtns[j].setToolTipText("��װ��·��[" + str_installPackages[i][0] + "],��ʼ��������·��[" + str_xtdatadir + "]"); //
						radioBtns[j].putClientProperty("package_prefix", str_installPackages[i][0]); //ƽ̨�İ���!
						radioBtns[j].putClientProperty("xtdatadir", str_xtdatadir); //��ʼ�����ݵ���Ŀ¼!!
						btnGroup.add(radioBtns[j]); //
						panel.add(radioBtns[j]); //
						al_toggleButtons.add(radioBtns[j]); //������������,��װʱ��Ҫ�������!
					}
					JLabel label_2 = new JLabel("��", SwingConstants.LEFT); //
					label_2.setFont(font); //
					//label_2.setForeground(Color.BLUE); //
					panel.add(label_2); //
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_install) {
			onInstall(); //
		}
	}

	/**
	 * ִ�а�װ!!
	 */
	private void onInstall() {
		if (btn_install.getText().equals("��װ")) { //����Ƿ���ͣ״̬
			if (JOptionPane.showConfirmDialog(mainPanel, "���Ƿ������ִ�а�װ����ô?\r\n��ע����ȷѡ��װ������Ŀ������Դ!", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}
			btn_install.setText("ֹͣ��װ"); //
			new Thread() {
				public void run() {
					doAction(); //�����¿�һ���̴߳�������,�Ǹ�time���ܲ���Ч��!!!
				}
			}.start(); //
		} else if (btn_install.getText().equals("ֹͣ��װ")) { //ֹͣ
			btn_install.setText("��ֹͣ"); //
			return;
		} else { //ֹͣ
			JOptionPane.showMessageDialog(mainPanel, "��رհ�װ������������"); //
			return; //
		}
	}

	protected void doAction() {
		textArea.setText(""); //���ԭ��������!!
		try {
			long ll_1 = System.currentTimeMillis(); //
			for (int i = 0; i < al_toggleButtons.size(); i++) {
				JToggleButton btn = (JToggleButton) al_toggleButtons.get(i); //
				if (btn.isSelected()) { //�����ǹ�ѡ�ϵĲ�������װ!!
					String str_name = btn.getText(); //
					String str_package = (String) btn.getClientProperty("package_prefix"); //����!
					String str_xtdatadir = (String) btn.getClientProperty("xtdatadir"); //��ʼ�����ݵ���Ŀ¼!
					installOneAppByPackageName(str_package, str_name, str_xtdatadir); //��װ�����!!
				}
				if (btn_install.getText().equals("��ֹͣ")) { //������;����,��������ֹͣ!
					return; //
				}
			}

			//��󹹽���������!!!
			for (int i = 0; i < al_toggleButtons.size(); i++) {
				JToggleButton btn = (JToggleButton) al_toggleButtons.get(i); //
				if (btn.isSelected()) { //�����ǹ�ѡ�ϵĲ�������װ!!
					String str_name = btn.getText(); //
					String str_package = (String) btn.getClientProperty("package_prefix"); //����!
					v_msg.add("��ʼ���򹹽����� [" + str_name + "].....\r\n"); //��ǰ���򹹽������и�bug,�������Ƿ�ѡ��,����Ϊ���б�������,�������ѭ������ִ��!��xch/2012-06-07��
					SysAppServiceIfc services = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
					String str_reverseResult = services.reverseSetSequenceValue(str_package); //������������!
					v_msg.add("���򹹽����н��� [" + str_name + "],���[" + str_reverseResult + "]....\r\n\r\n"); //
				}
			}

			//��ջ���!!
			FrameWorkCommServiceIfc commServices = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class); //
			ClientEnvironment.getInstance().setClientSysOptions(commServices.reLoadDataFromDB(false)); //���¼��ػ���,������������װ��������¼,���office�ؼ�������������,�������˰��죬�ŷ���������ΪTomatû������,������ɵ�!��xch/2012-03-06��
			v_msg.add("���û���ɹ�....\r\n"); //

			long ll_2 = System.currentTimeMillis(); //
			v_msg.add("\r\n��װȫ������,����ʱ[" + ((ll_2 - ll_1) / 1000) + "]��,ǿ�ҽ��������м��������ʽ���ʣ�����\r\n");
			btn_install.setText("��װ����"); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			JOptionPane.showMessageDialog(mainPanel, "��װ�����쳣:[" + ex.getMessage() + "],��������̨�鿴��ϸ!!"); //
		}
	}

	/**
	 * ��װĳһ����Ʒ/ģ��/��Ŀ
	 * @param _packageName
	 * @param _viewName
	 */
	private void installOneAppByPackageName(String _package, String _name, String _xtdatadir) throws Exception {
		SysAppServiceIfc services = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //

		//��װ�����!!
		if (btn_install.getText().equals("��ֹͣ")) {
			return; //
		}
		v_msg.add("׼����װ [" + _name + "] �����,��װ��·��[" + _package + "database/tables.xml].....\r\n"); //
		String[] str_allPlatformTables = services.getAllIntallTablesByPackagePrefix(_package); //���ݰ����������!
		if (str_allPlatformTables != null) { //����������!!
			v_msg.add("��ʼ��װ [" + _name + "] �����,����һ����[" + str_allPlatformTables.length + "]����.....\r\n"); //
			for (int i = 0; i < str_allPlatformTables.length; i++) { //������װ��!!
				if (btn_install.getText().equals("��ֹͣ")) {
					return; //
				}
				String str_result = services.createTableByPackagePrefix(_package, str_allPlatformTables[i]); //��װ�����,��ִ��Create table�ű�!!
				v_msg.add("<" + getNoStr(i, str_allPlatformTables.length) + "/" + str_allPlatformTables.length + ">��װ [" + _name + "] ����� [" + str_allPlatformTables[i] + "] ����,���:" + str_result + "\r\n"); //����,Ȼ��ȴ���һ���߳�ȥ����!!!!
			}
		} else {
			v_msg.add("��ʼ��װ [" + _name + "] �����,û�з���һ����������ļ�!!\r\n"); //	
		}
		v_msg.add("\r\n");

		//��װ��ͼ!!
		if (btn_install.getText().equals("��ֹͣ")) {
			return; //
		}
		v_msg.add("׼����װ [" + _name + "] ��ͼ,��װ��·��[" + _package + "database/views.xml]....\r\n"); //
		String[] str_allPlatformViews = services.getAllIntallViewsByPackagePrefix(_package); //ȡ��������ͼ!!!
		if (str_allPlatformViews != null) {
			v_msg.add("��ʼ��װ [" + _name + "] ��ͼ,����һ����[" + str_allPlatformViews.length + "]����ͼ.....\r\n"); //
			for (int i = 0; i < str_allPlatformViews.length; i++) {
				if (btn_install.getText().equals("��ֹͣ")) {
					return; //
				}
				String str_result = services.createViewByPackagePrefix(_package, str_allPlatformViews[i]);
				v_msg.add("<" + getNoStr(i, str_allPlatformViews.length) + "/" + str_allPlatformViews.length + ">��װ [" + _name + "] ��ͼ [" + str_allPlatformViews[i] + "] ����,���:" + str_result + "\r\n"); //����,Ȼ��ȴ���һ���߳�ȥ����!!!!
			}
		} else {
			v_msg.add("��ʼ��װ [" + _name + "] ��ͼ,û�з���һ����ͼ�����ļ�!!\r\n"); //	
		}
		v_msg.add("\r\n");

		//��װ��ʼ����!!!
		if (btn_install.getText().equals("��ֹͣ")) {
			return; //
		}
		String[] str_xdtataitem = null; //
		if (_xtdatadir != null && !_xtdatadir.trim().equals("")) { //�����Ϊ��,����Ҫ��װ����������,�ٰ�װ�ض���Ŀ¼��!!
			str_xdtataitem = new String[] { null, _xtdatadir }; //
		} else {
			str_xdtataitem = new String[] { null }; //
		}
		for (int r = 0; r < str_xdtataitem.length; r++) {
			if (btn_install.getText().equals("��ֹͣ")) {
				return; //
			}
			if (str_xdtataitem[r] == null) {
				v_msg.add("׼����װ [" + _name + "] ��ʼ����,��װ��·��[" + _package + "xtdata/].....\r\n"); //
			} else {
				v_msg.add("׼����װ [" + _name + "] ��ʼ����,��װ��·��[" + _package + "xtdata/" + str_xdtataitem[r] + "].....\r\n"); //
			}
			String[] str_allPlatformInitDataTables = services.getAllIntallInitDataByPackagePrefix(_package, str_xdtataitem[r]); //ȡ�����������ļ�
			if (str_allPlatformInitDataTables != null) { //�����Ϊ��
				v_msg.add("��ʼ��װ [" + _name + "] ��ʼ����,������[" + str_allPlatformInitDataTables.length + "]���ļ�\r\n"); //

				List list = new ArrayList();
//				StringBuffer tables = new StringBuffer();//��¼�ظ��ı�
				for (int i = 0; i < str_allPlatformInitDataTables.length; i++) { //
					String tableName = str_allPlatformInitDataTables[i];
					if (tableName.contains("_100")) { //law_law_100001.xml����
						tableName = split(tableName, "_100")[0];
					} else {
						tableName = split(tableName, ".")[0]; // bsd_bsact.xml����
					}
					if (initDataHis.containsKey(tableName)) { //�ж�ִ�е��ĸ����ˣ��Ƿ��Ѿ�ɾ�������ݡ�
						if (!(Boolean) initDataHis.get(tableName)) {
							list.add("delete from " + tableName);
							initDataHis.put(tableName, true);//�Ѿ�ɾ��������,ÿ�ű���һ������ֻ�����һ��
//							tables.append(tableName+"��");
						}
					} else {
						initDataHis.put(tableName, true); //�����һ�β������ݿ⣬���������							
					}
				}
				if (list.size() > 0) {
					v_msg.add("׼����� [" + _name + "]�Ѱ�װ���ظ�����,��[" + list.size() + "]�ű�\r\n"); //
					UIUtil.executeBatchByDS(null, list);
				}
				Iterator it = initDataHis.keySet().iterator();
				while (it.hasNext()) { //���Ѿ���װ�����¼ȫ������
					String t_name = (String) it.next();
					initDataHis.put(t_name, false);
				}
				for (int i = 0; i < str_allPlatformInitDataTables.length; i++) {
					if (btn_install.getText().equals("��ֹͣ")) {
						return; //
					}
					String str_result = services.InsertInitDataByPackagePrefix(_package, str_xdtataitem[r], str_allPlatformInitDataTables[i]); //ʵ�ʰ�װ��ʼ������!!!
					v_msg.add("<" + getNoStr(i, str_allPlatformInitDataTables.length) + "/" + str_allPlatformInitDataTables.length + ">��װ [" + _name + "] ��ʼ���� [" + str_allPlatformInitDataTables[i] + "] ����,���:" + str_result + "\r\n");//����,Ȼ��ȴ���һ���߳�ȥ����!!!!
				}
			} else {
				v_msg.add("��ʼ��װ [" + _name + "] ��ʼ����,û�з���һ�������ļ�!!\r\n"); //	
			}
			v_msg.add("\r\n");
		}

		v_msg.add("׼����װ [" + _name + "] ��չ����,��װ��·��[" + _package + "xtdata/].....\r\n"); //������ݲ����ڲ�Ʒjar��,ԭ���ж�:һ����̫��,һ���˾���8.3M,�Ȳ�Ʒ������������,�������Ժ���Ҫ��������!������lib\��ר��Ūһ��weblight_lawdata.jar��xch/2012-03-12��
		String[][] str_ext3DataFiles = services.getExt3DataXmlFiles(_package); //ȡ�������ļ��嵥,��Ҫ��RegisterMenu.xml��ע����<ext3data>
		if (str_ext3DataFiles.length == 1 && str_ext3DataFiles[0][1] == null) { //�����ʧ��,��Ϊ���ص�Sting[][],û�취,ֻ��ʹ�����ⷽʽ��ʾ�ǰ�װʧ��,��ʵҲ����ʹ��AltAppException,�����ﻹҪtry{}catch{},�鷳!!
			v_msg.add("û�а�װ [" + _name + "] ��չ����,ԭ��[" + str_ext3DataFiles[0][0] + "]\r\n"); //
		} else {
			v_msg.add("��ʼ��װ [" + _name + "] ��չ����,һ����[" + str_ext3DataFiles.length + "]���ļ�.....\r\n"); //
			for (int i = 0; i < str_ext3DataFiles.length; i++) {
				String str_installResult = services.installExt3Data(str_ext3DataFiles[i][1]); //��װ!!
				v_msg.add("<" + getNoStr(i, str_ext3DataFiles.length) + "/" + str_ext3DataFiles.length + ">��װ [" + _name + "] ��չ����֮ [" + str_ext3DataFiles[i][0] + "]-[" + str_ext3DataFiles[i][1] + "],���[" + str_installResult + "]\r\n"); //
			}
		}
		v_msg.add("\r\n"); //
	}

	private String getNoStr(int _i, int _length) {
		int li_area = 10000; //
		if (_length < 10) {
			li_area = 10;
		} else if (_length < 100) {
			li_area = 100;
		} else if (_length < 1000) {
			li_area = 1000;
		} else {
			li_area = 10000;
		}
		String str_no = "" + (li_area + _i + 1);
		return str_no.substring(1, str_no.length()); //
	}

	private String[] split(String _par, String _separator) {
		if (_par == null) {
			return null;
		}
		if (_par.trim().equals("")) {
			return new String[0];
		}
		if (_par.indexOf(_separator) < 0) {
			return new String[] { _par };
		}
		ArrayList al_temp = new ArrayList(); //
		String str_remain = _par; //
		int li_pos = str_remain.indexOf(_separator); //
		while (li_pos >= 0) {
			String str_1 = str_remain.substring(0, li_pos); //
			if (str_1 != null && !str_1.trim().equals("")) {
				al_temp.add(str_1); // ����!!!
			}
			str_remain = str_remain.substring(li_pos + _separator.length(), str_remain.length()); //
			li_pos = str_remain.indexOf(_separator); //
		}

		if (str_remain != null && !str_remain.trim().equals("")) {
			al_temp.add(str_remain); //
		}

		return (String[]) al_temp.toArray(new String[0]); // //
	}

	public HashMap convertStrToMapByExpress(String _str, String _split1, String _split2) {
		LinkedHashMap map = new LinkedHashMap(); //��˳��!
		if (_str == null || _str.trim().equals("")) {
			return map;
		}
		String[] str_items = split(_str, _split1); //
		for (int i = 0; i < str_items.length; i++) {
			String[] str_items2 = split(str_items[i], _split2); //
			if (str_items2.length >= 2) {
				map.put(str_items2[0], str_items2[1]); // //�����ϣ��!!!!
			}
		}
		return map;
	}

	/**
	 * ������Ϣ!!
	 */
	protected void dealMsg() {
		if (v_msg.isEmpty()) { //���Ϊ��,����!
			return;
		}
		StringBuilder sb_newText = new StringBuilder(); //
		while (!v_msg.isEmpty()) { //���������!!!
			sb_newText.append((String) v_msg.elementAt(0)); //
			v_msg.removeElementAt(0); //
		}
		textArea.append(sb_newText.toString()); //
		int li_length = textArea.getText().length(); //
		textArea.setSelectionStart(li_length); //
		textArea.setSelectionEnd(li_length); //
	}

}
