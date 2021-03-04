/**************************************************************************
 * $RCSfile: ShowHideSortTableColumnDialog.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.listcomp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * ��ʾ�����е����ô���!! ������ָ����Щ�����ػ���ʾ!!!
 * @author xch
 *
 */
public class ShowHideSortTableColumnDialog extends BillDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private WLTButton btn_selall, btn_unselall; //
	private BillListPanel billList = null; //
	protected WLTButton btn_confirm, btn_cancel;

	private String[][] str_initdata = null; //

	private int closeType = -1; //

	private String[][] returnData = null; //
	private String str_clickedItemKey = null; //

	/**
	 * 
	 * @param _parent
	 * @param _title
	 * @param _width
	 * @param li_height
	 * @param _data
	 */
	public ShowHideSortTableColumnDialog(Container _parent, String _title, int _width, int li_height, String[][] _data, String _clickedItemKey) {
		super(_parent, _title, _width, li_height);
		this.str_initdata = _data; //
		this.str_clickedItemKey = _clickedItemKey; //
		initialize();
	}

	/**
	 * ��ʼ��ҳ��
	 * 
	 */
	private void initialize() {
		billList = new BillListPanel(getTMO()); //
		if (str_initdata != null) {
			for (int i = 0; i < str_initdata.length; i++) {
				int li_row = billList.addEmptyRow(false); //
				billList.setValueAt(new StringItemVO(str_initdata[i][0]), li_row, "ITEMKEY");
				billList.setValueAt(new StringItemVO(str_initdata[i][1]), li_row, "ITEMNAME");
				billList.setValueAt(new StringItemVO(str_initdata[i][2]), li_row, "ISVISIABLE");
			}

			if (str_clickedItemKey != null) {
				for (int i = 0; i < str_initdata.length; i++) {
					if (str_initdata[i][0].equals(str_clickedItemKey)) {
						billList.setSelectedRow(i); //
						break; //
					}
				}
			}
		}

		billList.setToolbarVisiable(false); //
		this.getContentPane().add(getNorthPanel(), BorderLayout.NORTH); //
		this.getContentPane().add(billList, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	private JPanel getNorthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.RIGHT));
		btn_selall = new WLTButton("ȫ����ʾ");
		btn_unselall = new WLTButton("ȫ������");
		btn_selall.setFocusable(false);
		btn_unselall.setFocusable(false);
		btn_selall.addActionListener(this); //
		btn_unselall.addActionListener(this); //
		panel.add(btn_selall); //
		panel.add(btn_unselall); //
		return panel;
	}

	private JPanel getSouthPanel() {
		JPanel panel =WLTPanel.createDefaultPanel(new FlowLayout());
		btn_confirm = new WLTButton("ȷ��");
		btn_cancel = new WLTButton("ȡ��");
		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel;
	}

	private DefaultTMO getTMO() {
		HashVO hvo_parent = new HashVO(); //
		hvo_parent.setAttributeValue("templetcode", "setShowAndVisiable"); //ģ�����,��������޸�
		hvo_parent.setAttributeValue("templetname", "������ʾ/������"); //ģ������

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
		hvs_child[1].setAttributeValue("listwidth", "155"); //�б��ǿ��
		hvs_child[1].setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		hvs_child[1].setAttributeValue("listiseditable", "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������

		hvs_child[2] = new HashVO(); //
		hvs_child[2].setAttributeValue("itemkey", "ISVISIABLE"); //Ψһ��ʶ,����ȡ���뱣��
		hvs_child[2].setAttributeValue("itemname", "�Ƿ���ʾ"); //��ʾ����
		hvs_child[2].setAttributeValue("itemname_e", "isVisiable"); //��ʾ����
		hvs_child[2].setAttributeValue("itemtype", WLTConstants.COMP_CHECKBOX); //�ؼ�����
		hvs_child[2].setAttributeValue("listwidth", "100"); //�б��ǿ��
		hvs_child[2].setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		hvs_child[2].setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������

		DefaultTMO tmo = new DefaultTMO(hvo_parent, hvs_child); //
		return tmo; //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		} else if (e.getSource() == btn_selall) {
			onSelAll(); //
		} else if (e.getSource() == btn_unselall) {
			onunSelAll(); //
		}
	}

	private void onSelAll() {
		for (int i = 0; i < billList.getRowCount(); i++) {
			billList.setValueAt(new StringItemVO("Y"), i, "ISVISIABLE"); //
		}
	}

	private void onunSelAll() {
		for (int i = 0; i < billList.getRowCount(); i++) {
			billList.setValueAt(new StringItemVO("N"), i, "ISVISIABLE"); //
		}
	}

	private void onConfirm() {
		returnData = new String[billList.getRowCount()][3]; //
		for (int i = 0; i < returnData.length; i++) {
			returnData[i][0] = billList.getRealValueAtModel(i, "ITEMKEY"); //
			returnData[i][1] = billList.getRealValueAtModel(i, "ITEMNAME"); //
			returnData[i][2] = billList.getRealValueAtModel(i, "ISVISIABLE"); //
		}
		closeType = 1;
		this.dispose();
	}

	private void onCancel() {
		returnData = null; //
		closeType = 2;
		this.dispose();
	}

	public int getCloseType() {
		return closeType;
	}

	public String[][] getReturnData() {
		return returnData;
	}
}