package cn.com.infostrategy.ui.workflow.engine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.common.WLTTextArea;
import cn.com.infostrategy.ui.report.BillCellPanel;

/**
 * ������Ҫ��һ����! ���������ȫ��,������ӡ!!
 * ��ǰ������һֱ�и�Ӳ�˾����������ʱ�����пͻ��о����������Ի�!
 * ԭ������������������ǳ�ǿ����һ����,ֻ�뿴�ؼ�����,ֻ�뿴�ؼ���ɫ,ֻ�뿴���������һϵ������...
 * ���մ�ӡʱ��Ҫ��������Ҫ�ֶ���������Ҫ�������ɫ�����ƴ��һ�����!! ��Ҫ����ߵĻ��������Զ�ת���ʵ�ʻ�������!
 * ����ͬ���ŵ�������кϲ�...
 * 
 * �����볹�׸㶨��ν������������������֮����,����һ�������ֱ�﷽ʽ:
 * 1.���ı������ 
 * 2.
 * @author Administrator
 *
 */
public class LookWorkflowDealMsgDialog extends BillDialog implements ActionListener {

	private BillVO[] billVOs = null; //���е�����,��ԭ���б��е���������! ����ǹ�ѡ��,�����ѡ�еļ���!
	private String reportTitle = null; //�������
	private String prinstanceId = null; //����ʵ��ID

	private ArrayList list_vos = null; //

	private WLTButton btn_confirm; //�Ժ󻹿�����չ������ť..

	public LookWorkflowDealMsgDialog(Container _parent, BillVO[] _billVOs, String _reportTitle, String _prinstanceId) {
		super(_parent, "���ȫ��", 850, 700);
		if (ClientEnvironment.isAdmin()) {
			//�ڴ��ڱ�����ֱ�Ӵ�ӡ������,��Ϊ�˿�����Ա����ʱ����֪�����ĸ���,Ȼ����Eclipse������ٶ��ҵ��������ڵط�!�Ӷ�����޸�����Ч��!!����ֻ��һ�����ĸ���,����Ϥƽ̨������˹��ҵ�����ط����ܶ�Ҫ����...
			this.setTitle(this.getTitle() + " ��LookWorkflowDealMsgDialog��"); //
		}
		this.billVOs = _billVOs; //
		this.reportTitle = _reportTitle; //
		this.prinstanceId = _prinstanceId; //
		initialize(); //��ʼ��ҳ��
	}

	//��ʼ��ҳ��..
	private void initialize() {
		WLTTabbedPane tabb = new WLTTabbedPane(); //��ҳǩ!

		tabb.addTab("�������֮�ı����", UIUtil.getImage("office_172.gif"), getTextPanel()); //
		tabb.addTab("������֮�ı����", UIUtil.getImage("office_174.gif"), getTextPanel2()); //
		tabb.addTab("�������֮�����", UIUtil.getImage("office_070.gif"), getCellPanel()); //
		tabb.addTab("������֮�����", UIUtil.getImage("office_071.gif"), getCellPanel2()); //
		//tabb.addTab("Html���", UIUtil.getImage("office_062.gif"), getHtmlPanel()); //�Ժ���Ū..

		this.getContentPane().add(tabb, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	private JPanel getTextPanel() {
		return getTextPanel(getDealMsgList()); //
	}

	private JPanel getTextPanel2() {
		return getTextPanel(getDealMsgDistinctList()); //
	}

	/**
	 * �ı��򵼳�!!!
	 * @return
	 */
	private JPanel getTextPanel(ArrayList list) {
		StringBuilder sb_text = new StringBuilder(); ////
		for (int i = 0; i < list.size(); i++) {
			ArrayList al_row = (ArrayList) list.get(i); //

			BillVO firstVO = (BillVO) al_row.get(0); //
			String str_deptName = firstVO.getStringValue("participant_userdeptname"); //��������
			sb_text.append("-------------------- ��" + str_deptName + "�� -------------------\r\n"); //

			for (int j = 0; j < al_row.size(); j++) { //
				BillVO billVO = (BillVO) al_row.get(j); //
				String str_userName = billVO.getStringValue("participant_username"); //
				sb_text.append("������:" + str_userName); //

				int li_textLength = TBUtil.getTBUtil().getStrUnicodeLength(str_userName); //
				int li_appendlength = 26 - li_textLength; //
				for (int k = 0; k < li_appendlength; k++) { //��������Ϊ��ʱ�����...
					sb_text.append(" ");
				}
				sb_text.append("����ʱ��:" + billVO.getStringValue("submittime") + "\r\n"); //
				sb_text.append("�������:" + billVO.getStringValue("submitmessage", "") + "\r\n\r\n"); //
			}

			sb_text.append("\r\n\r\n"); //
		}

		JPanel panel = new JPanel(new BorderLayout()); //
		JTextArea textArea = new WLTTextArea(sb_text.toString()); //
		textArea.setBackground(new Color(250, 253, 250)); //
		textArea.setLineWrap(true); //
		textArea.setEditable(false); //

		panel.add(new JScrollPane(textArea)); //
		return panel; //
	}

	private JPanel getCellPanel() {
		return getCellPanel(getDealMsgList()); //
	}

	private JPanel getCellPanel2() {
		return getCellPanel(getDealMsgDistinctList()); //Ψһ�Դ���...
	}

	/**
	 * ʹ��BillCellPanel����������...
	 * ����ؼ������˷���ϲ�����,���˸о�������,�������Ե���Excel��Html
	 */
	private JPanel getCellPanel(ArrayList list) {
		int li_count = 0; //
		for (int i = 0; i < list.size(); i++) {
			ArrayList al_row = (ArrayList) list.get(i); //
			li_count = li_count + al_row.size(); //
		}

		//
		BillCellItemVO[][] itemVOs = new BillCellItemVO[li_count * 2 + 1][5]; //
		for (int i = 0; i < itemVOs.length; i++) {
			for (int j = 0; j < 5; j++) {
				itemVOs[i][j] = new BillCellItemVO(); //
				itemVOs[i][j].setCelltype("TEXT"); //
				itemVOs[i][j].setCellvalue(""); //
				itemVOs[i][j].setRowheight("25"); //
			}
		}

		//����
		itemVOs[0][0].setCellvalue(this.reportTitle); //
		itemVOs[0][0].setHalign(2); //
		itemVOs[0][0].setCelltype("TEXT"); //
		itemVOs[0][0].setSpan("1,5"); //
		itemVOs[0][0].setRowheight("35"); //
		itemVOs[0][0].setBackground("255,255,220"); //
		itemVOs[0][0].setForeground("0,0,255"); //

		//���������
		String str_label_width = "100"; //
		String str_text_width = "150"; //
		int li_col1_pos = 1; //
		String[] str_spanDeptColor = new String[] { "255,240,255", "233,233,243" }; //
		for (int i = 0; i < list.size(); i++) {
			ArrayList list_row = (ArrayList) list.get(i); //
			BillVO firstVO = (BillVO) list_row.get(0); //

			int li_rowSpan = list_row.size() * 2; //
			itemVOs[li_col1_pos][0].setSpan(li_rowSpan + ",1"); //
			itemVOs[li_col1_pos][0].setColwidth("100"); //
			itemVOs[li_col1_pos][0].setBackground(str_spanDeptColor[i % 2]); //
			itemVOs[li_col1_pos][0].setCelltype("TEXTAREA"); //
			itemVOs[li_col1_pos][0].setHalign(2); //
			itemVOs[li_col1_pos][0].setValign(2); //

			String str_deptName = firstVO.getStringValue("participant_userdeptname"); //
			str_deptName = TBUtil.getTBUtil().replaceAll(str_deptName, "-", "\r\n"); //
			itemVOs[li_col1_pos][0].setCellvalue(str_deptName); //

			//��������VO
			for (int j = 0; j < list_row.size(); j++) { //
				BillVO billVO = (BillVO) list_row.get(j); //

				//������:
				itemVOs[li_col1_pos + j * 2][1].setCellvalue("������"); //
				itemVOs[li_col1_pos + j * 2][1].setHalign(3); //
				itemVOs[li_col1_pos + j * 2][1].setColwidth(str_label_width); //
				itemVOs[li_col1_pos + j * 2][1].setBackground("217,255,217"); //

				//dealuser content
				itemVOs[li_col1_pos + j * 2][2].setColwidth(str_text_width); //
				itemVOs[li_col1_pos + j * 2][2].setCellvalue(billVO.getStringValue("participant_username")); //
				itemVOs[li_col1_pos + j * 2][2].setBackground("252,252,252"); //

				//����ʱ��
				itemVOs[li_col1_pos + j * 2][3].setCellvalue("����ʱ��"); //
				itemVOs[li_col1_pos + j * 2][3].setHalign(3); //
				itemVOs[li_col1_pos + j * 2][3].setColwidth(str_label_width); //
				itemVOs[li_col1_pos + j * 2][3].setBackground("217,255,217"); //

				//
				itemVOs[li_col1_pos + j * 2][4].setColwidth(str_text_width); //
				itemVOs[li_col1_pos + j * 2][4].setCellvalue(billVO.getStringValue("submittime")); //
				itemVOs[li_col1_pos + j * 2][4].setBackground("252,252,252"); //

				//�������:
				itemVOs[li_col1_pos + j * 2 + 1][1].setCelltype("TEXTAREA"); //
				itemVOs[li_col1_pos + j * 2 + 1][1].setHalign(1); //
				itemVOs[li_col1_pos + j * 2 + 1][1].setValign(1); //
				itemVOs[li_col1_pos + j * 2 + 1][1].setSpan("1,4"); //
				itemVOs[li_col1_pos + j * 2 + 1][1].setRowheight("70"); //
				itemVOs[li_col1_pos + j * 2 + 1][1].setColwidth("20"); //
				itemVOs[li_col1_pos + j * 2 + 1][1].setCellvalue("���������" + billVO.getStringValue("submitmessage", "")); //
			}

			li_col1_pos = li_col1_pos + li_rowSpan; //��һ�ε�λ��!
		}

		BillCellVO cellVO = new BillCellVO(); //
		cellVO.setCellItemVOs(itemVOs); //
		cellVO.setRowlength(itemVOs.length); //
		cellVO.setCollength(5); //

		BillCellPanel cellPanel = new BillCellPanel(cellVO); //
		cellPanel.setCrystal(true); //

		JPanel contentPanel = WLTPanel.createDefaultPanel(new BorderLayout()); //
		contentPanel.add(cellPanel); //

		WLTButton btn_exportExcel = new WLTButton("����Excel", "icon_xls.gif"); //
		WLTButton btn_exportHtml = new WLTButton("����Html", "office_136.gif"); //

		btn_exportExcel.putClientProperty("BindCellPanel", cellPanel); //
		btn_exportHtml.putClientProperty("BindCellPanel", cellPanel); //

		btn_exportExcel.addActionListener(this); //
		btn_exportHtml.addActionListener(this); //

		JPanel btnPanel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.LEFT)); //
		btnPanel.add(btn_exportExcel); //
		btnPanel.add(btn_exportHtml); //
		contentPanel.add(btnPanel, BorderLayout.NORTH); //
		return contentPanel; //
	}

	/**
	 * ��������嵥...
	 * һ������Ҫ�ϲ���,ÿ������һ��List
	 * @return
	 */
	private ArrayList getDealMsgList() {
		if (list_vos != null) {
			return list_vos; //
		}
		list_vos = new ArrayList(); //
		for (int i = 0; i < billVOs.length; i++) {
			if (i == 0) { //����ǵ�һ��,��ֱ������
				ArrayList list_row = new ArrayList(); //
				list_row.add(billVOs[i]); //
				list_vos.add(list_row); //
			} else {
				String str_thisActName = billVOs[i].getStringValue("participant_userdeptname"); //
				String str_lastActName = billVOs[i - 1].getStringValue("participant_userdeptname"); //ǰһ��!
				if (str_thisActName != null && str_thisActName.equals(str_lastActName)) { //�����ǰ��һ��!
					ArrayList list_row = (ArrayList) list_vos.get(list_vos.size() - 1); //
					list_row.add(billVOs[i]); //
				} else { //���������һ��,���½�!
					ArrayList list_row = new ArrayList(); //
					list_row.add(billVOs[i]); //
					list_vos.add(list_row); //
				}
			}
		}
		return list_vos; //
	}

	/**
	 * ȡ��Ψһ�Ե��嵥...��һ������ֻ�����һ��,һ�������ڲ���һ����ԱҲֻ�����һ��!
	 * @return
	 */
	private ArrayList getDealMsgDistinctList() {
		LinkedHashMap deptMap = new LinkedHashMap(); //�����ж��Ƿ�ֻ���ֹ�һ��!
		for (int i = 0; i < billVOs.length; i++) { //
			String str_deptname = billVOs[i].getStringValue("participant_userdeptname"); //��������!!!
			String str_userName = billVOs[i].getStringValue("participant_username"); //��Ա����!!!
			if (deptMap.containsKey(str_deptname)) { //�����ע��
				LinkedHashMap userMap = (LinkedHashMap) deptMap.get(str_deptname); //
				if (!userMap.containsKey(str_userName)) { //��������Ա������
					userMap.put(str_userName, billVOs[i]); //�����,�������,��ʲô������
				}
			} else { //���������������!
				LinkedHashMap userMap = new LinkedHashMap(); //
				userMap.put(str_userName, billVOs[i]); //
				deptMap.put(str_deptname, userMap); //
			}
		}

		ArrayList list_dist = new ArrayList(); //
		LinkedHashMap[] list_Maps = (LinkedHashMap[]) deptMap.values().toArray(new LinkedHashMap[0]); //
		for (int i = 0; i < list_Maps.length; i++) {
			BillVO[] vos = (BillVO[]) list_Maps[i].values().toArray(new BillVO[0]); //����VO
			ArrayList al_row = new ArrayList(); //
			for (int j = 0; j < vos.length; j++) {
				al_row.add(vos[j]); //
			}
			list_dist.add(al_row); //
		}

		return list_dist; //
	}

	/**
	 * ʹ�ñ�׼�Ĺ������������,����Html...
	 * @return
	 */
	private JPanel getHtmlPanel() {
		return new JPanel(); //
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout()); //
		btn_confirm = new WLTButton("ȷ��"); //
		btn_confirm.addActionListener(this); //
		panel.add(btn_confirm); //
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			this.setCloseType(1); //
			this.dispose(); //
		} else {
			WLTButton btn = (WLTButton) e.getSource();
			BillCellPanel cellPanel = (BillCellPanel) btn.getClientProperty("BindCellPanel"); //
			if (cellPanel != null) {
				if (btn.getText().equals("����Excel")) {
					cellPanel.exportExcel("���̴������"); //
				} else if (btn.getText().equals("����Html")) {
					cellPanel.exportHtml("���̴������"); //
				}
			}
		}
	}

}
