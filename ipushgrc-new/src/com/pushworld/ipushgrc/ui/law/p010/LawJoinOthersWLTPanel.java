package com.pushworld.ipushgrc.ui.law.p010;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractWLTCompentPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.ChildTableCommUIIntercept;
import cn.com.infostrategy.ui.mdata.cardcomp.RefDialog_ChildTableImport;

/** 
 * Copyright Pushine
 * @ClassName: com.pushworld.ipushgrc.ui.law.p010.LawJoinOthersWLTPanel 
 * @Description: ������������Զ���ؼ���һ���б��������ƶ����ƣ����ƶ�״̬��
 * @author haoming
 * @date Jun 5, 2013 5:09:00 PM
 *  
*/
public class LawJoinOthersWLTPanel extends AbstractWLTCompentPanel implements ActionListener {

	private static final long serialVersionUID = -6967496356909878013L;

	private Pub_Templet_1_ItemVO templetItemVO = null;

	private String key = null;
	private String name = null;
	private JLabel label = null;
	private WLTButton btn_import, btn_delete; //��,ɾ,��,��
	private BillPanel billPanel = null; //
	private int li_width_all;

	private BillListPanel billListPanel = null; //�б�ģ��
	private String primarykey = null; //
	private String foreignkey = null; //
	private String primaryname = null; //
	private String intercept = null;
	private TBUtil tbutil = new TBUtil();

	public LawJoinOthersWLTPanel(Pub_Templet_1_ItemVO _templetVO, BillPanel _billPanel) {
		super();
		this.templetItemVO = _templetVO; //
		this.key = templetItemVO.getItemkey(); //
		this.name = templetItemVO.getItemname(); //
		this.billPanel = _billPanel; //
		primarykey = "id";
		foreignkey = "law_ref";
		primaryname = "lawname";
		intercept="com.pushworld.ipushgrc.ui.rule.p010.ImportChildTableIntercept";
		initialize();
	}

	private void initialize() {
		try {
			this.setLayout(new BorderLayout()); //
			this.setBackground(LookAndFeel.cardbgcolor); //
			int li_tablewidth = 300; //
			if (templetItemVO.getCardwidth() != null) {
				li_tablewidth = templetItemVO.getCardwidth().intValue(); //
			}

			int li_tableheight = 80; //
			if (templetItemVO.getCardHeight() != null) {
				li_tableheight = templetItemVO.getCardHeight().intValue() + 20; //
			}
			if (li_tableheight < 80) {
				li_tableheight = 80;
			}

			label = createLabel(templetItemVO); //���ø����ṩ�ķ�������Label
			li_width_all = (int) (label.getPreferredSize().getWidth() + li_tablewidth); ////
			this.setPreferredSize(new Dimension(li_width_all, li_tableheight)); //
			this.add(label, BorderLayout.WEST); //

			billListPanel = new BillListPanel("LAW_JOINOTHER_CODE1", false); //
			billListPanel.getTempletVO().setIsshowlistpagebar(false); //��ģ�����޸Ĳ���ʾ��ҳ��ֱ�����ط�ҳ���ᵼ��������ʾ��ȫ��
			billListPanel.initialize(); //����ģ��
			billListPanel.getBillListBtnPanel().setVisible(false);
			billListPanel.getTitlePanel().setVisible(false);
			billListPanel.getQuickQueryPanel().setVisible(false);
			billListPanel.setBillListOpaque(false); //
			btn_import = new WLTButton("����"); //
			btn_delete = new WLTButton("�Ƴ�"); //

			btn_import.addActionListener(this);
			btn_delete.addActionListener(this);
			btn_import.setPreferredSize(new Dimension(50, 20));
			btn_delete.setPreferredSize(new Dimension(50, 20));
			billListPanel.addCustButton(btn_import); //
			billListPanel.addCustButton(btn_delete); //

			String str_name = "";
			if (ClientEnvironment.getInstance().isEngligh()) {
				str_name = templetItemVO.getItemname_e();
			} else {
				str_name = templetItemVO.getItemname();
			}
			if (str_name != null && !"".equals(str_name.trim())) {//sunfujun/20120613/��ʵ����Ѳ�ѯ���������,Ҳ������δ����,�����ڿ�Ƭ�ϻ���ʾ�ֶ�����ģ������̫�Ѻ�_gaofeng
				billListPanel.getTitleLabel().setText("");
			}

			this.add(billListPanel, BorderLayout.CENTER); //
			if (intercept != null && !intercept.equals("")) {//����-�����ӱ�����������
				ChildTableCommUIIntercept listener = (ChildTableCommUIIntercept) Class.forName(intercept).newInstance(); //				
				listener.afterInitialize(billListPanel);
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
			this.removeAll(); //
			this.setLayout(new BorderLayout()); //
			this.setPreferredSize(new Dimension(100, 20)); //
			this.add(new JLabel("���ص����ӱ������쳣:" + ex.getClass().getName() + ":" + ex.getMessage())); //
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_import) {
			onImport();
		} else if (e.getSource() == btn_delete) {
			onDelete();
		}
	}

	private void onImport() {
		try {
			CommUCDefineVO commUcVO = new CommUCDefineVO();
			commUcVO.setConfValue("ģ�����", "LAW_LAW_CODE5");
			commUcVO.setConfValue("�����ֶ���", "ID");
			commUcVO.setConfValue("�����ֶ���ʾ��", "LAWNAME");
			commUcVO.setConfValue("������", "com.pushworld.ipushgrc.ui.rule.p010.ImportChildTableIntercept");
			RefDialog_ChildTableImport importDialog = new RefDialog_ChildTableImport(this, "����", (RefItemVO) getCustomObject(), billPanel, commUcVO);
			importDialog.setVisible(true);
			if (importDialog.getCloseType() == 1) {
				if (importDialog.getReturnRealValue() != null && importDialog.getReturnRealValue().length() > 0) {
					HashVO vos[] = UIUtil.getHashVoArrayByDS(null, "select id,lawname,state from law_law where id in(" + tbutil.getInCondition(importDialog.getReturnRefItemVO().getId()) + ")");
					StringBuffer value = new StringBuffer();
					for (int i = 0; i < vos.length; i++) {
						value.append(vos[i].getStringValue("id") + "$#$" + vos[i].getStringValue("lawname") + "$#$" + vos[i].getStringValue("state") + ";");
					}
					((BillCardPanel) billPanel).setValueAt(foreignkey, value.toString());
					resetHeight(); //���ø߶ȣ���
				}
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	private void onDelete() {
		try {
			BillVO billVO = this.billListPanel.getSelectedBillVO();
			if (billVO == null) {
				MessageBox.showSelectOne(billListPanel);
				return;
			}

			if (JOptionPane.showConfirmDialog(billListPanel, "ȷ��ɾ���ü�¼��?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return; //
			}
			billListPanel.removeRows(billListPanel.getSelectedRows()); ////
			((BillCardPanel) billPanel).setValueAt(foreignkey, getObject());
			resetHeight();
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	private void resetHeight() {
		int li_rows = this.billListPanel.getRowCount(); //
		int li_height = 60; //
		int li_minHeight = 85; //
		if (this.billListPanel.getTitlePanel().isVisible()) {
			li_height = li_height + 22;
			li_minHeight = li_minHeight + 22;
		}

		for (int i = 0; i < li_rows; i++) {
			li_height = li_height + billListPanel.getTable().getRowHeight(i); //
		}

		if (li_height < li_minHeight) {
			li_height = li_minHeight; //
		}

		int li_width = (int) this.getPreferredSize().getWidth(); //
		this.setPreferredSize(new Dimension(li_width, li_height)); //
		this.updateUI(); //
	}

	public JLabel getLabel() {
		return label;
	}

	public String getItemKey() {
		return key;
	}

	public String getItemName() {
		return name;
	}

	public String getOnlyIDs() {
		int li_rows = this.billListPanel.getRowCount(); //
		if (li_rows == 0) {
			return "";
		}
		StringBuffer sb_return = new StringBuffer(";"); //
		for (int i = 0; i < li_rows; i++) {
			sb_return.append(((StringItemVO) billListPanel.getValueAt(i, this.primarykey)).getStringValue()); //
			sb_return.append(";");
		}
		return sb_return.toString(); //
	}

	public String getValue() {
		int li_rows = this.billListPanel.getRowCount(); //
		if (li_rows == 0) {
			return "";
		}
		StringBuffer sb_return = new StringBuffer(""); //
		for (int i = 0; i < li_rows; i++) {
			sb_return.append(((StringItemVO) billListPanel.getValueAt(i, "id")).getStringValue() + "$#$" + ((StringItemVO) billListPanel.getValueAt(i, "lawname")).getStringValue() + "$#$" + ((ComBoxItemVO) billListPanel.getValueAt(i, "state")).getId()); //
			sb_return.append(";");
		}
		return sb_return.toString(); //
	}

	public String getName() {
		int li_rows = this.billListPanel.getRowCount(); //
		String str_return = ""; //
		for (int i = 0; i < li_rows; i++) {
			str_return = str_return + ((StringItemVO) billListPanel.getValueAt(i, this.primaryname)).getStringValue() + ";"; //
		}
		return str_return; //
	}

	public void setValue(String _value) {
		resetHeight(); //
	}

	public void reset() {
		if (this.billListPanel != null) {
			this.billListPanel.clearTable(); //�������
			resetHeight(); //���ø߶�
		}
	}

	public void setItemEditable(boolean _bo) {
		if (btn_import != null) {
			this.btn_import.setEnabled(_bo);
			this.btn_delete.setEnabled(_bo); //
		}
	}

	@Override
	public boolean isItemEditable() {
		return true;
	}

	public void setOnlyUpdate() {
		if (btn_import != null) {
			this.btn_import.setEnabled(false);
			this.btn_delete.setEnabled(false); //
		}
	}

	public void setItemVisiable(boolean _bo) {
		this.setVisible(_bo); //
	}

	public Object getCustomObject() {
		return new RefItemVO(getOnlyIDs(), null, getName());
	}

	public Object getObject() {
		return new RefItemVO(getValue(), null, getName());
	}

	public void setObject(Object _obj) {
		if (this.primarykey == null) {
			return;
		}
		if (_obj == null) {
			billListPanel.removeAllRows();
		} else {
			billListPanel.removeAllRows();
			String value = "";
			if (_obj instanceof RefItemVO) {
				value = ((RefItemVO) _obj).getId();
			} else {
				value = (String) _obj.toString();
			}
			String alljoin[] = value.split(";");
			for (int i = 0; i < alljoin.length; i++) {
				String oneJoinStr = alljoin[i];
				if (oneJoinStr != null && !oneJoinStr.trim().equals("")) {
					String lawid_name_state[] = tbutil.split(oneJoinStr, "$#$");
					StringItemVO idvo = new StringItemVO(lawid_name_state[0]);
					StringItemVO nameVO = new StringItemVO(lawid_name_state[1]);
					ComBoxItemVO stateCombox = new ComBoxItemVO(lawid_name_state[2], "", lawid_name_state[2]);
					HashMap map = new HashMap();
					map.put("id", idvo);
					map.put("lawname", nameVO);
					map.put("state", stateCombox);
					billListPanel.addRow(map);
				}

			}
		}
		resetHeight(); //�������ø߶�
	}

	public void focus() {
	}

	public int getAllWidth() {
		return li_width_all;
	}

	public BillListPanel getBillListPanel() {
		return billListPanel;
	}

	public WLTButton getBtn_import() {
		return btn_import;
	}

	public WLTButton getBtn_delete() {
		return btn_delete;
	}

}