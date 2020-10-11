package cn.com.infostrategy.ui.mdata.querycomp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

/**
 * һ��ͨ����ע��������ת����һ���Զ�����յĻ���!!
 * ��һ��һ���Զ�����ն�����ͨ��һ��ע�����������ҳ��,Ȼ������������һ��ȷ����ȡ����Ť�Ӷ��γ�һ������!!!
 * @author xch
 *
 */
public class RefDialog_QueryTableModel extends AbstractRefDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	private WLTButton btn_confirm, btn_cancel;

	private RefItemVO returnRefItemVO = null; //
	private BillListPanel billListPanel = null;

	private CommUCDefineVO dfvo = null; //
	private int refStyleType = 0; //Ĭ�ϵ�ѡ

	public RefDialog_QueryTableModel(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel, CommUCDefineVO _dfvo) {
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
		billListPanel = new BillListPanel(dfvo.getConfValue("ģ�����")); //ͨ��ע��������һ����ʽ���
		billListPanel.setAllBillListBtnVisiable(false); //
		billListPanel.setQuickQueryPanelVisiable(!dfvo.getConfBooleanValue("��ѯ�������", false));//sunfujun/20120817/bug
		billListPanel.setItemEditable(false); //
		billListPanel.setDataFilterCustCondition(dfvo.getConfValue("����SQL����")); //����ʹ�ö����SQL�������Զ���ѯ���ܣ���ǰ�ڶ����������ˣ���ûд�߼�!�����/2012-06-27��
		if (dfvo.getConfBooleanValue("�Զ���ѯ", false)) {
			billListPanel.QueryDataByCondition(null);
		}
		this.add(billListPanel, BorderLayout.CENTER); //
		this.add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		btn_confirm = new WLTButton("ȷ��");
		btn_cancel = new WLTButton("ȡ��");
		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) { //�����ȷ���򷵻�����
			String str_id_field = dfvo.getConfValue("ID�ֶ�");
			String str_name_field = dfvo.getConfValue("NAME�ֶ�"); //
			if (!dfvo.getConfBooleanValue("���Զ�ѡ", false)) { //����ǵ�ѡ!!
				BillVO billvo = billListPanel.getSelectedBillVO();
				if (billvo == null) { ////
					MessageBox.showSelectOne(this); ////
					return; //
				}

				HashVO hvo = convertHashVO(billvo); //
				returnRefItemVO = new RefItemVO(hvo); //
				if (str_id_field != null) { //���������id������Դ
					if (billvo.containsKey(dfvo.getConfValue("ID�ֶ�"))) {
						returnRefItemVO.setId(billvo.getStringValue(str_id_field)); //
					} else {
						MessageBox.show(this, "��ʽ������ָ���ӡ�" + str_id_field + "���ֶη��ز���ID,��ģ����û�и��"); //
						return; //
					}
				}

				if (str_name_field != null) { //���������id������Դ
					if (billvo.containsKey(str_name_field)) {
						returnRefItemVO.setName(billvo.getStringValue(str_name_field)); //
					} else {
						MessageBox.show(this, "��ʽ������ָ���ӡ�" + str_name_field + "���ֶη��ز���Name,��ģ����û�и��"); //
						return; //
					}
				}
			} else {
				BillVO[] billVOs = billListPanel.getSelectedBillVOs(); //
				if (billVOs == null || billVOs.length == 0) {
					MessageBox.show(this, "����ѡ��һ������!"); //
					return; //
				}
				StringBuffer sb_ids = new StringBuffer(); //
				StringBuffer sb_names = new StringBuffer(); //
				String[] str_keys = billVOs[0].getKeys(); //
				for (int i = 0; i < billVOs.length; i++) {
					if (str_id_field != null) { //���������id������Դ
						if (billVOs[i].containsKey(str_id_field)) {
							sb_ids.append(billVOs[i].getStringValue(str_id_field) + ";"); ////
						} else {
							MessageBox.show(this, "��ʽ������ָ���ӡ�" + str_id_field + "���ֶη��ز���ID,��ģ����û�и��"); //
							return; //
						}
					} else { //
						if (billVOs[i].getStringValue(str_keys[1]) != null) {
							sb_names.append(billVOs[i].getStringValue(str_keys[1]) + ";"); //
						}
					}

					if (str_name_field != null) { //���������id������Դ
						if (billVOs[i].containsKey(str_name_field)) {
							sb_names.append(billVOs[i].getStringValue(str_name_field) + ";"); ////
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
}
