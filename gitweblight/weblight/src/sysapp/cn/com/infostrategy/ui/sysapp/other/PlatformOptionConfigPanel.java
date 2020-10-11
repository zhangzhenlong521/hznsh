package cn.com.infostrategy.ui.sysapp.other;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 
 * ƽ̨��������
 * @author lcj
 *
 */
public class PlatformOptionConfigPanel extends AbstractWorkPanel implements ActionListener {
	private BillListPanel platformOptionPanel = null;
	private WLTButton btn_reloadCache, btn_addNewOptions = null; //

	public void initialize() {		
		/***
		 * ���Ӳ�����ģ�����ƹ��� Gwang 2013-06-22
		 * �û�ֻ���ñ�ģ���������, ���Ҫ��������Ҫȥƽ̨�����в���
		 * ͨ���˵�����"ģ������"ָ��, ��==��ʽ��ѯ
		 */
		String moduleName = this.getMenuConfMapValueAsStr("ģ������", "");
		
		this.setLayout(new BorderLayout());
		platformOptionPanel = new BillListPanel("pub_option_CODE1");
		/*
		*ƽ̨�����������������������߼��������ò������ݿ���ȡ�ˣ�������weblight�����á�PROJECT_MU��ֵΪ��50,3����16�����룬����35302C33����
		*��ʾ�ڷ���3�������˻����ͬʱ��50������ͬ�˺����¼�����������ﵽ50�������ʾ�ﵽ�����������ޣ����ܵ�¼�������/2012-06-07�� 
		*/
		WLTButton btn_insert = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT); //����
		WLTButton btn_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); //�޸�
		WLTButton btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE); //ɾ��
		btn_reloadCache = new WLTButton("ˢ�²�������"); //
		btn_addNewOptions = new WLTButton("�Ƚϲ���Ӳ���"); //
		btn_reloadCache.addActionListener(this); //
		btn_addNewOptions.addActionListener(this); //		
				
		String filter = " parkey not in('ע��ʱ�Ƿ���ʾ�����������') ";
		//���Ӳ�����ģ�����ƹ��� Gwang 2013-06-22
		if (!"".equals(moduleName)) {
			filter += "and modulename = '" + moduleName + "' ";
			platformOptionPanel.getQuickQueryPanel().setVisible(false);
			platformOptionPanel.addBatchBillListButton(new WLTButton[] { btn_update, btn_reloadCache }); //���������ģ������, ��������
		}else {
			platformOptionPanel.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_reloadCache, btn_addNewOptions }); //
		}
		
		platformOptionPanel.repaintBillListButton();
		platformOptionPanel.setDataFilterCustCondition(filter);
		platformOptionPanel.QueryDataByCondition(null);

		this.add(platformOptionPanel);
	}

	private void onReloadCache() {
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

	/**
	 * ��������!!!
	 */
	private void insertBatchOption() {
		try {
			String[][] allPlatformOptions = UIUtil.getCommonService().getAllPlatformOptions(); //����ע��Ĳ���!!!
			HashMap allDBOptions = UIUtil.getHashMapBySQLByDS(null, "select parkey,parvalue from pub_option"); //

			StringBuilder sb_info = new StringBuilder(); //
			ArrayList al_news = new ArrayList(); //
			for (int i = 0; i < allPlatformOptions.length; i++) {
				if (!allDBOptions.containsKey(allPlatformOptions[i][1])) { //������ݿ����û��,�����!!
					sb_info.append("��" + allPlatformOptions[i][0] + "����" + allPlatformOptions[i][1] + "��=��" + allPlatformOptions[i][2] + "��\r\n"); ///
					al_news.add(allPlatformOptions[i]); //
				}
			}
			if (al_news.size() == 0) {
				MessageBox.show(this, "û�з�����Ҫ�����Ĳ���!"); //
				return; //
			}
			if (!MessageBox.confirm(this, "ϵͳ����[" + al_news.size() + "]���²�������:\r\n���Ƿ�������������?\r\n�����ȫ������,������ǡ�!!\r\n���ֻҪ�����������м���,�򿽱�����,������񡿺��ֹ����!\r\n\r\n�²����嵥:\r\n" + sb_info.toString())) {
				return; //
			}

			String[] str_sqls = new String[al_news.size()]; //
			for (int i = 0; i < al_news.size(); i++) {
				String[] str_item = (String[]) al_news.get(i); //
				InsertSQLBuilder isql = new InsertSQLBuilder("pub_option"); //
				isql.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_PUB_OPTION")); //
				isql.putFieldValue("modulename", str_item[0]); //
				isql.putFieldValue("parkey", str_item[1]); //
				isql.putFieldValue("parvalue", str_item[2]); //
				isql.putFieldValue("pardescr", str_item[3]); //
				str_sqls[i] = isql.getSQL(); //
			}
			UIUtil.executeBatchByDS(null, str_sqls); //
			MessageBox.show(this, "���[" + al_news.size() + "]���²����ɹ�,������ѯ��ť���½��в鿴!"); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_reloadCache) {
			onReloadCache(); //
		} else if (e.getSource() == btn_addNewOptions) {
			insertBatchOption(); //
		}
	}

}
