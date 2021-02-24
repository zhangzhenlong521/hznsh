/**************************************************************************
 * $RCSfile: CardCPanel_ChildTableImport.java,v $  $Revision: 1.18 $  $Date: 2012/10/08 02:22:48 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 * �����ӱ����ڿ�Ƭ����ʾ����!!
 * @author sfj
 *
 */
public class CardCPanel_ChildTableImport extends AbstractWLTCompentPanel implements ActionListener {

	private static final long serialVersionUID = -6967496356909878013L;

	private Pub_Templet_1_ItemVO templetItemVO = null;

	private String key = null;
	private String name = null;
	private String refDesc = null; //

	private JLabel label = null;
	private WLTButton btn_import, btn_delete, btn_select = null; //��,ɾ,��,��
	private BillPanel billPanel = null; //
	private int li_width_all;

	private BillListPanel billListPanel = null; //�б�ģ��
	private String billtempletcode = null; //
	private String primarykey = null; //
	private String foreignkey = null; //
	private String primaryname = null; //
	private String intercept = null;

	public CardCPanel_ChildTableImport(Pub_Templet_1_ItemVO _templetVO, BillPanel _billPanel) {
		super();
		this.templetItemVO = _templetVO; //
		this.key = templetItemVO.getItemkey(); //
		this.name = templetItemVO.getItemname(); //
		this.refDesc = templetItemVO.getRefdesc(); //����,�Ϳ����������б�ģ��
		this.billPanel = _billPanel; //
		setBillPanel(_billPanel);
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

			CommUCDefineVO uCDfVO = this.templetItemVO.getUCDfVO(); //�ؼ������VO!!!
			if (uCDfVO == null) {
				this.add(new JLabel("�����ӱ�Ķ���Ϊ��!"), BorderLayout.CENTER); //
				return; //
			}

			billtempletcode = uCDfVO.getConfValue("ģ�����"); //
			primarykey = uCDfVO.getConfValue("�����ֶ���"); //
			primaryname = uCDfVO.getConfValue("�����ֶ���ʾ��"); //
			foreignkey = uCDfVO.getConfValue("��������ֶ���"); //
			intercept = uCDfVO.getConfValue("������"); //��ȡ������ʵ����
			billListPanel = new BillListPanel(billtempletcode, false); //
			billListPanel.getTempletVO().setIsshowlistpagebar(false); //��ģ�����޸Ĳ���ʾ��ҳ��ֱ�����ط�ҳ���ᵼ��������ʾ��ȫ��
			billListPanel.initialize(); //����ģ��
			billListPanel.getBillListBtnPanel().setVisible(false);
			billListPanel.getTitlePanel().setVisible(false);
			billListPanel.getQuickQueryPanel().setVisible(false);
			billListPanel.setBillListOpaque(false); //
			btn_import = new WLTButton("����"); //
			btn_delete = new WLTButton("�Ƴ�"); //
			btn_select = new WLTButton("�鿴"); //

			btn_import.addActionListener(this);
			btn_delete.addActionListener(this);
			btn_select.addActionListener(this);
			btn_import.setPreferredSize(new Dimension(50, 20));
			btn_delete.setPreferredSize(new Dimension(50, 20));
			btn_select.setPreferredSize(new Dimension(50, 20));
			billListPanel.addCustButton(btn_import); //
			billListPanel.addCustButton(btn_delete); //
			billListPanel.addCustButton(btn_select); //

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
			if (intercept != null && !intercept.equals("")) {//����-�����ӱ����������
				ChildTableCommUIIntercept listener = (ChildTableCommUIIntercept) Class.forName(intercept).newInstance(); //				
				listener.afterInitialize(billListPanel);
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
			this.removeAll(); //
			this.setLayout(new BorderLayout()); //
			this.setPreferredSize(new Dimension(100, 20)); //
			this.add(new JLabel("���ص����ӱ����쳣:" + ex.getClass().getName() + ":" + ex.getMessage())); //
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_import) {
			onImport();
		} else if (e.getSource() == btn_delete) {
			onDelete();
		} else if (e.getSource() == btn_select) {
			onSelect();
		}
	}

	private void onImport() {
		try {
			RefDialog_ChildTableImport importDialog = new RefDialog_ChildTableImport(this, "����", (RefItemVO) getObject(), billPanel, this.templetItemVO.getUCDfVO());
			importDialog.setVisible(true);
			if (importDialog.getCloseType() == 1) {
				if (importDialog.getReturnRealValue() != null && importDialog.getReturnRealValue().length() > 0) {
					billListPanel.QueryDataByCondition(getCondition(importDialog.getReturnRefItemVO()));
					((BillCardPanel) billPanel).setValueAt(foreignkey, importDialog.getReturnRefItemVO());
					resetHeight(); //���ø߶ȣ���
				}
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	private String getCondition(RefItemVO value) {
		String condition = "";
		if (value != null && value.getId() != null && value.getId().length() > 0) {
			String l_id = tBUtil.getInCondition(value.getId()); //��ѡ����ת��Ϊ�����ӱ�ԭʼ���ݼ��ر���ԭ�� ;1;2;3; -> id in(,1,2,3)����TBUtil��������⡣ 
			condition = primarykey + " in (" + l_id + ")";
		}
		return condition;
	}

	private void onDelete() {
		try {
			BillVO billVO = this.billListPanel.getSelectedBillVO();
			if (billVO == null) {
				MessageBox.showSelectOne(billListPanel);
				return;
			}

			if (JOptionPane.showConfirmDialog(billListPanel, "ȷ���Ƴ��ü�¼��?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {//��ʾҪ�밴ť����һ�£���ǰ����ʾɾ�������/2013-06-13��
				return; //
			}
			billListPanel.removeRows(billListPanel.getSelectedRows()); ////
			((BillCardPanel) billPanel).setValueAt(foreignkey, getObject());
			resetHeight();
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	private void onSelect() {
		try {
			BillVO billVO = billListPanel.getSelectedBillVO();
			if (billVO == null) {
				MessageBox.showSelectOne(billListPanel);
				return;
			}

			BillCardPanel cardPanel = new BillCardPanel(this.billListPanel.templetVO); //����һ����Ƭ���
			cardPanel.setBillVO(billVO); //
			cardPanel.setEditable(false); //���ÿ�Ƭ�༭״̬Ϊ����ʱ������
			BillCardDialog dialog = new BillCardDialog(this, this.templetItemVO.getPub_Templet_1VO().getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_INIT); //������Ƭ������
			dialog.setVisible(true); //
			this.getPropertyChangeListeners();
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

	public String getValue() {
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
			this.btn_select.setEnabled(true); //
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
			this.btn_select.setEnabled(true); //
		}
	}

	public void setItemVisiable(boolean _bo) {
		this.setVisible(_bo); //
	}

	public Object getObject() {
		return new RefItemVO(getValue(), null, getName());
	}

	public void setObject(Object _obj) {
		if (this.primarykey == null) {
			return;
		}
		if (_obj != null && ((RefItemVO) _obj).getId() != null && !"null".equals(((RefItemVO) _obj).getId()) && ((RefItemVO) _obj).getId().length() > 0) {
			billListPanel.QueryDataByCondition(getCondition((RefItemVO) _obj)); //
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

	public WLTButton getBtn_select() {
		return btn_select;
	}

}
