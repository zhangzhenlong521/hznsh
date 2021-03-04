package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 * һ��ͨ����һ���б�ģ��ֱ�ӱ��һ�����գ����䳣�ã�
 * ��һ��һ���Զ�����ն�����ͨ��һ��ע�����������ҳ��,Ȼ������������һ��ȷ����ȡ����Ť�Ӷ��γ�һ������!!! һ���б�ģ�����Ҫʵ�����¼����������
 * 1.����ָ����ѡ���ѡ 2.����ָ�����ز��յ�id,name���ֶ���
 * 3.����ָ���Ƿ��в�ѯ�򣬻������Ϊ�Ƿ�Ĭ�ϲ�ѯ�����ݣ�һ���������Ͳ�ѯ���������ݣ�һ�����ٵ����ѯ��ť��ѯ���� 4.�Ƿ���Զ�ѡ,��ѡ��������ݷ���!!
 * 5.����ָ����ѯ����,����ģ��Ĳ�ѯ�����Ļ������ټ����µ��Զ�������!ģ�屾��Ĳ�ѯ����һ�㶼�����¼��Ա/����/��ɫ��أ�������Ĳ�ѯ�����������ҳ���ϵ�Ԫ�����!
 * 
 * @author xch
 * 
 */
public class RefDialog_ListTemplet extends AbstractRefDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	private WLTButton btn_confirm, btn_cancel;

	private RefItemVO returnRefItemVO = null; //
	private BillListPanel billListPanel = null;

	private CommUCDefineVO dfvo = null; //
	private int refStyleType = 0; // Ĭ�ϵ�ѡ

	public RefDialog_ListTemplet(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel, CommUCDefineVO _dfvo) {
		super(_parent, _title, refItemVO, panel);
		dfvo = _dfvo;
	}

	@Override
	public RefItemVO getReturnRefItemVO() {
		return returnRefItemVO;
	}

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout()); //
		billListPanel = new BillListPanel(dfvo.getConfValue("ģ�����")); // ͨ��ע��������һ����ʽ���
		if (dfvo.getConfBooleanValue("�Զ���ѯ", false)) {
			billListPanel.QueryDataByCondition(dfvo.getConfValue("����SQL����")); //ʹ�ö����SQL����!
		}
		billListPanel.setQuickQueryPanelVisiable(!dfvo.getConfBooleanValue("��ѯ�������", false)); //Ĭ������ʾ��,����ʱ�ǲ�����չ����ѯ�ģ���ǰ�õ�billListPanel.getQuickQueryPanel().setVisible(false); ֻ�ǽ���ѯ�����������޸�
		if (dfvo.getConfBooleanValue("���Զ�ѡ", false)) {
			billListPanel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // ���Զ�ѡ
		} else {
			billListPanel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // ��ѡ
		}
		billListPanel.setDataFilterCustCondition(dfvo.getConfValue("����SQL����")); // ���Ӳ�ѯ����
		billListPanel.setItemEditable(false); //
		this.add(billListPanel, BorderLayout.CENTER); //
		this.add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout());
		btn_confirm = new WLTButton("ȷ��");
		btn_cancel = new WLTButton("ȡ��");
		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel;
	}

	/**
	 * �����ťִ��!
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) { // �����ȷ���򷵻�����
			String str_id_field = dfvo.getConfValue("ID�ֶ�");
			String str_name_field = dfvo.getConfValue("NAME�ֶ�"); //
			if (!dfvo.getConfBooleanValue("���Զ�ѡ", false)) { // ����ǵ�ѡ!!
				BillVO billvo = billListPanel.getSelectedBillVO();
				if (billvo == null) {
					MessageBox.showSelectOne(this); //
					return; //
				}

				HashVO hvo = convertHashVO(billvo); //
				returnRefItemVO = new RefItemVO(hvo); //
				if (str_id_field != null) { // ���������id������Դ
					if (billvo.containsKey(str_id_field)) {
						returnRefItemVO.setId(billvo.getStringValue(str_id_field)); //
					} else {
						MessageBox.show(this, "��ʽ������ָ���ӡ�" + str_id_field + "���ֶη��ز���ID,��ģ����û�и��"); //
						return; //
					}
				}

				if (str_name_field != null) { // ���������id������Դ
					if (billvo.containsKey(str_name_field)) {
						returnRefItemVO.setName(billvo.getStringValue(str_name_field)); //
					} else {
						MessageBox.show(this, "��ʽ������ָ���ӡ�" + str_name_field + "���ֶη��ز���Name,��ģ����û�и��"); //
						return; //
					}
				}
			} else { // ��ѡ!!!
				BillVO[] billVOs = billListPanel.getSelectedBillVOs(); //
				if (billVOs == null || billVOs.length == 0) {
					MessageBox.showSelectOne(this); //
					return; //
				}
				StringBuffer sb_ids = new StringBuffer(";"); //
				StringBuffer sb_names = new StringBuffer(); //
				String[] str_keys = billVOs[0].getKeys(); //
				for (int i = 0; i < billVOs.length; i++) {
					if (str_id_field != null) { // ���������id������Դ
						if (billVOs[i].containsKey(str_id_field)) {
							sb_ids.append(billVOs[i].getStringValue(str_id_field) + ";"); // //
						} else {
							MessageBox.show(this, "��ʽ������ָ���ӡ�" + str_id_field + "���ֶη��ز���ID,��ģ����û�и��"); //
							return; //
						}
					} else {
						if (billVOs[i].getStringValue(str_keys[1]) != null) {
							sb_names.append(billVOs[i].getStringValue(str_keys[1]) + ";"); //
						}
					}

					if (str_name_field != null) { // ���������id������Դ
						if (billVOs[i].containsKey(str_name_field)) {
							sb_names.append(billVOs[i].getStringValue(str_name_field) + ";"); // //
						} else {
							MessageBox.show(this, "��ʽ������ָ���ӡ�" + str_name_field + "���ֶη��ز���Name,��ģ����û�и��"); //
							return; //
						}
					} else {
						if (billVOs[i].getStringValue(str_keys[3]) != null) {
							sb_names.append(billVOs[i].getStringValue(str_keys[3]) + ";"); //
						}
					}
				}
				returnRefItemVO = new RefItemVO(); //
				returnRefItemVO.setId(sb_ids.toString()); //
				returnRefItemVO.setName(sb_names.toString()); //
			}
			this.setCloseType(BillDialog.CONFIRM);
			this.dispose(); //
		} else if (e.getSource() == btn_cancel) {
			this.setCloseType(BillDialog.CANCEL);
			this.dispose(); //
		}
	}

	private HashVO convertHashVO(BillVO _billvo) {
		String[] strkeys = _billvo.getKeys(); //
		HashVO hvo = new HashVO();
		for (int i = 1; i < strkeys.length; i++) {
			hvo.setAttributeValue(strkeys[i], _billvo.getStringValue(strkeys[i])); //
		}
		return hvo;
	}

	public int getRefStyleType() {
		return refStyleType;
	}

	public void setRefStyleType(int refStyleType) {
		this.refStyleType = refStyleType;
	}

	/**
	 * ��ʼ���
	 * @return
	 */
	public int getInitWidth() {
		return 800;
	}

	public int getInitHeight() {
		return 500;
	}

	public BillListPanel getBillListPanel() {
		return billListPanel;
	}

	public void setBillListPanel(BillListPanel billListPanel) {
		this.billListPanel = billListPanel;
	}
}
