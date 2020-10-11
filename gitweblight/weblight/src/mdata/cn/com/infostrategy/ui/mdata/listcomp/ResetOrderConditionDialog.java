/**************************************************************************
 * $RCSfile: ResetOrderConditionDialog.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.listcomp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * ���¶������������Ĵ���!! ˫����ͷ����ֻ�Ե�ǰҳ����! �����Ϻ������м������ͻ����������������ҳ����������!
 * ��Ϊ����Ҫ��ҳ(�������ܲ��Կ϶�ͨ����,�������Ѿ���֤��),����ֻ�������ݿ��SQL�����з�ҳ!!Ҳ����˵�ù���ֻ�Ƕ����������������¶���!!!
 * @author xch
 *
 */
public class ResetOrderConditionDialog extends BillDialog implements ActionListener {

	private static final long serialVersionUID = 1L; //

	private BillListPanel billList = null; //
	private WLTButton btn_additem, btn_delitem, btn_moveup, btn_movedown; //����,ɾ������!
	private WLTButton btn_confirm, btn_cancel; //ȷ��/ȡ����ť!

	private String[][] str_allViewColumns = null; //
	private String str_initOrderCons = null; //��ʼ��������!!
	private String returnCons = null; //���ص�����!!

	private TBUtil tbUtil = new TBUtil(); //

	/**
	 * 
	 * @param _parent
	 * @param str_orderCOndition 
	 * @param _allViewColumnNames 
	 * @param _title
	 * @param _width
	 * @param li_height
	 * @param _templetItemVOs
	 * @param str_filterkeys
	 */
	public ResetOrderConditionDialog(Container _parent, String[][] _allViewColumnNames, String _orderCondition) {
		super(_parent, "������������", 300, 350); //
		this.str_allViewColumns = _allViewColumnNames; //
		this.str_initOrderCons = _orderCondition; //
		initialize(); //
	}

	/**
	 * ��ʼ��ҳ��
	 * 
	 */
	private void initialize() {
		billList = new BillListPanel(getTMO()); //
		if (str_initOrderCons != null && !str_initOrderCons.trim().equals("")) { //����г�ʼ��������!
			String[] str_items = tbUtil.split(str_initOrderCons, ","); //�Զ��ŷָ�!!
			for (int i = 0; i < str_items.length; i++) { //������������!!
				String str_itemcon = str_items[i].trim(); //
				str_itemcon = tbUtil.replaceAll(str_itemcon, "    ", " "); //�п�����4���ո�,���滻��һ��
				str_itemcon = tbUtil.replaceAll(str_itemcon, "   ", " "); //�п�����3���ո�,���滻��һ��
				str_itemcon = tbUtil.replaceAll(str_itemcon, "  ", " "); //�п�����2���ո�,���滻��һ��
				String[] str_key_asc = tbUtil.split(str_itemcon, " "); //�Կո����!
				String str_key = str_key_asc[0]; //
				String str_isdesc = "N"; //�Ƿ���?
				if (str_key_asc.length > 1 && str_key_asc[1].trim().equalsIgnoreCase("desc")) { //���������,�Һ�����desc���!!!���ʾ�ǵ���!!!
					str_isdesc = "Y"; //
				}
				String str_name = findItemName(this.str_allViewColumns, str_key); //
				int li_newRow = billList.addEmptyRow(false); //
				billList.setValueAt(new StringItemVO(str_key), li_newRow, "ITEMKEY"); //key
				billList.setValueAt(new StringItemVO(str_name), li_newRow, "ITEMNAME"); //name
				billList.setValueAt(new StringItemVO(str_isdesc), li_newRow, "ISDESC"); //�Ƿ���
			}
		}
		this.getContentPane().add(billList, BorderLayout.CENTER); //
		this.getContentPane().add(getNorthPanel(), BorderLayout.NORTH); //�����������ť
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //�����������ť
	}

	//
	private JPanel getNorthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
		btn_additem = new WLTButton("����");
		btn_delitem = new WLTButton("ɾ��");
		btn_moveup = new WLTButton("����"); //
		btn_movedown = new WLTButton("����"); //

		btn_additem.setFocusable(false);
		btn_delitem.setFocusable(false);
		btn_moveup.setFocusable(false);
		btn_movedown.setFocusable(false);

		btn_additem.addActionListener(this); //
		btn_delitem.addActionListener(this); //
		btn_moveup.addActionListener(this); //
		btn_movedown.addActionListener(this); //
		panel.add(btn_additem); //
		panel.add(btn_delitem); //
		panel.add(btn_moveup); //
		panel.add(btn_movedown); //
		return panel;
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout());
		btn_confirm = new WLTButton("ȷ��"); //
		btn_cancel = new WLTButton("ȡ��"); //
		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_additem) {
			onAddItem();
		} else if (e.getSource() == btn_delitem) {
			onDelItem();
		} else if (e.getSource() == btn_moveup) {
			billList.moveUpRow(); //����
		} else if (e.getSource() == btn_movedown) {
			billList.moveDownRow(); //����
		} else if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		}
	}

	//�����µ�����!!
	private void onAddItem() {
		AddNewOrderConditionDialog addDialog = new AddNewOrderConditionDialog(this, this.str_allViewColumns); //
		addDialog.setVisible(true); //
		if (addDialog.getCloseType() == 1) { //�����ȷ�����ص�!!
			String[][] str_newCons = addDialog.getReturnCons(); //
			for (int i = 0; i < str_newCons.length; i++) {
				int li_row = billList.addEmptyRow(false); //
				billList.setValueAt(new StringItemVO(str_newCons[i][0]), li_row, "ITEMKEY"); //
				billList.setValueAt(new StringItemVO(str_newCons[i][1]), li_row, "ITEMNAME"); //
				billList.setValueAt(new StringItemVO("N"), li_row, "ISDESC"); //
			}
		}
	}

	private void onDelItem() {
		if (billList.getSelectedRow() < 0) {
			MessageBox.show(this, "����ѡ��һ����¼���ܽ��д˲���!"); //
			return; //
		}
		billList.removeSelectedRows(); //ɾ������ѡ�е���!!
	}

	//ȷ��
	private void onConfirm() {
		BillVO[] billVOs = billList.getAllBillVOs(); //
		if (billVOs == null || billVOs.length <= 0) {
			returnCons = null; //û������
		} else {
			StringBuilder sb_cons = new StringBuilder(); //
			for (int i = 0; i < billVOs.length; i++) {
				String str_key = billVOs[i].getStringValue("ITEMKEY"); //����!
				String str_isdesc = billVOs[i].getStringValue("ISDESC"); //�Ƿ���?
				sb_cons.append(str_key); //
				if ("Y".equals(str_isdesc)) { //����ǵ���
					sb_cons.append(" desc"); //
				}
				if (i != billVOs.length - 1) {
					sb_cons.append(","); //����������һ��,����϶���!!
				}
			}
			returnCons = sb_cons.toString(); //
		}
		setCloseType(1);
		this.dispose(); //
	}

	private void onCancel() {
		setCloseType(2); //
		this.dispose(); //
	}

	public String getReturnCons() {
		return returnCons;
	}

	//
	private String findItemName(String[][] _allViewColumns, String _key) {
		for (int i = 0; i < _allViewColumns.length; i++) { //
			if (_allViewColumns[i][0].equalsIgnoreCase(_key)) {
				return _allViewColumns[i][1]; //
			}
		}
		return null;
	}

	/**
	 * �����б�
	 * @return
	 */
	private DefaultTMO getTMO() {
		HashVO hvo_parent = new HashVO(); //
		hvo_parent.setAttributeValue("templetcode", "ResetSortCondition"); //ģ�����,��������޸�
		hvo_parent.setAttributeValue("templetname", "������������"); //ģ������

		HashVO[] hvs_child = new HashVO[3]; //
		hvs_child[0] = new HashVO(); //
		hvs_child[0].setAttributeValue("itemkey", "ITEMKEY"); //Ψһ��ʶ,����ȡ���뱣��
		hvs_child[0].setAttributeValue("itemname", "����"); //��ʾ����
		hvs_child[0].setAttributeValue("itemname_e", "Id"); //��ʾ����
		hvs_child[0].setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		hvs_child[0].setAttributeValue("listwidth", "145"); //�б��ǿ��
		hvs_child[0].setAttributeValue("listisshowable", "N"); //�б�ʱ�Ƿ���ʾ(Y,N)
		hvs_child[0].setAttributeValue("listiseditable", "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������

		hvs_child[1] = new HashVO(); //
		hvs_child[1].setAttributeValue("itemkey", "ITEMNAME"); //Ψһ��ʶ,����ȡ���뱣��
		hvs_child[1].setAttributeValue("itemname", "����"); //��ʾ����
		hvs_child[1].setAttributeValue("itemname_e", "ItemName"); //��ʾ����
		hvs_child[1].setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		hvs_child[1].setAttributeValue("listwidth", "125"); //�б��ǿ��
		hvs_child[1].setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		hvs_child[1].setAttributeValue("listiseditable", "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������

		hvs_child[2] = new HashVO(); //
		hvs_child[2].setAttributeValue("itemkey", "ISDESC"); //Ψһ��ʶ,����ȡ���뱣��
		hvs_child[2].setAttributeValue("itemname", "�Ƿ���"); //��ʾ����
		hvs_child[2].setAttributeValue("itemname_e", "isDesc"); //��ʾ����
		hvs_child[2].setAttributeValue("itemtype", WLTConstants.COMP_CHECKBOX); //�ؼ�����
		hvs_child[2].setAttributeValue("listwidth", "80"); //�б��ǿ��
		hvs_child[2].setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		hvs_child[2].setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������

		DefaultTMO tmo = new DefaultTMO(hvo_parent, hvs_child); //
		return tmo; //
	}

	/**
	 * �����Ĵ���!!!
	 * @author xch
	 *
	 */
	class AddNewOrderConditionDialog extends BillDialog implements ActionListener {

		private static final long serialVersionUID = 1L;
		private WLTButton btn_addConfirm, btn_addCancel; //
		private JList list = null; //
		private String[][] str_returnCons = null; //

		public AddNewOrderConditionDialog(Container _parent, String[][] _allColumns) {
			super(_parent, "��������", 250, 250);
			ComBoxItemVO[] itemVOs = new ComBoxItemVO[_allColumns.length]; //
			for (int i = 0; i < itemVOs.length; i++) {
				itemVOs[i] = new ComBoxItemVO(_allColumns[i][0], null, _allColumns[i][1]); //
			}
			list = new JList(itemVOs); //
			list.setBackground(Color.WHITE); //
			JScrollPane scroll = new JScrollPane(list); //
			this.getContentPane().add(scroll); //

			btn_addConfirm = new WLTButton("ȷ��"); //
			btn_addCancel = new WLTButton("ȡ��"); //
			btn_addConfirm.addActionListener(this); //
			btn_addCancel.addActionListener(this); //
			JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout()); //
			panel.add(btn_addConfirm); //
			panel.add(btn_addCancel); //
			this.getContentPane().add(panel, BorderLayout.SOUTH); //
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btn_addConfirm) {
				Object[] objs = list.getSelectedValues(); //
				if (objs == null || objs.length <= 0) {
					MessageBox.show(this, "����ѡ��һ����¼���д˲���"); //
					return; //
				}
				str_returnCons = new String[objs.length][2]; //
				for (int i = 0; i < objs.length; i++) {
					ComBoxItemVO selVO = (ComBoxItemVO) objs[i]; //
					str_returnCons[i][0] = selVO.getId(); //
					str_returnCons[i][1] = selVO.getName(); //
				}
				setCloseType(1); //
				this.dispose(); //
			} else if (e.getSource() == btn_addCancel) {
				str_returnCons = null; //
				setCloseType(2); //
				this.dispose(); //
			}

		}

		public String[][] getReturnCons() {
			return this.str_returnCons; //�����ķ�������!!!
		}
	}

}
