package cn.com.infostrategy.ui.mdata.querycomp;

import java.awt.Container;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.formatcomp.AbstractRegFormatRefDialog;
import cn.com.infostrategy.ui.mdata.formatcomp.RegFormatRefDataCreaterIFC;

/**
 * ע�������յ�Ĭ��ʵ��
 * @author xch
 *
 */
public class RefDialog_QueryRegFormat extends AbstractRegFormatRefDialog {
	private static final long serialVersionUID = -8323170455063789191L;

	private CommUCDefineVO dfvo = null; //

	public RefDialog_QueryRegFormat(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel, CommUCDefineVO _dfvo) {
		super(_parent, _title, refItemVO, panel); //
		this.dfvo = _dfvo; //
	}

	@Override
	public RefItemVO checkBeforeClose() throws Exception {
		if (!dfvo.getConfValue("����������", "").equals("")) { //��������˰���,��ʹ�ýӿ�
			RegFormatRefDataCreaterIFC refitemVOCreater = (RegFormatRefDataCreaterIFC) Class.forName(dfvo.getConfValue("����������")).newInstance(); //
			return refitemVOCreater.createRefItemVO(getBillFormatPanel()); //�ɴ�����ȥ�������ص����ݶ���!!!
		} else {
			if (getRefStyleType() == 0) {
				if (this.getBillFormatPanel().getReturnRefItemVOFrom() != null) { //��������˷��ص������!��ʹ��������
					BillPanel billPanel = this.getBillFormatPanel().getReturnRefItemVOFrom(); //
					if (billPanel instanceof BillListPanel) {
						BillListPanel billList = (BillListPanel) billPanel;
						BillVO billVO = billList.getSelectedBillVO(); //
						if (billVO == null) {
							throw new WLTAppException("��ѡ��һ��[" + billList.getTempletVO().getTempletname() + "]����!"); //
						}
						RefItemVO refItemvo = new RefItemVO(convertHashVO(billVO));
						if (this.getBillFormatPanel().getReturnRefItemVOIDFieldName() != null && !this.getBillFormatPanel().getReturnRefItemVOIDFieldName().equals("")) {
							refItemvo.setId(billVO.getStringValue(this.getBillFormatPanel().getReturnRefItemVOIDFieldName())); //
						}
						if (this.getBillFormatPanel().getReturnRefItemVONameFieldName() != null && !this.getBillFormatPanel().getReturnRefItemVONameFieldName().equals("")) {
							refItemvo.setName(billVO.getStringValue(this.getBillFormatPanel().getReturnRefItemVONameFieldName())); //
						}
						return refItemvo;
					} else if (billPanel instanceof BillTreePanel) {
						BillTreePanel billTree = (BillTreePanel) billPanel;
						BillVO billVO = billTree.getSelectedVO(); //
						if (billVO == null) {
							throw new WLTAppException("��ѡ��һ��[" + billTree.getTempletVO().getTempletname() + "]����!"); //
						}
						RefItemVO refItemvo = new RefItemVO(convertHashVO(billVO));
						if (this.getBillFormatPanel().getReturnRefItemVOIDFieldName() != null && !this.getBillFormatPanel().getReturnRefItemVOIDFieldName().equals("")) {
							refItemvo.setId(billVO.getStringValue(this.getBillFormatPanel().getReturnRefItemVOIDFieldName())); //
						}
						if (this.getBillFormatPanel().getReturnRefItemVONameFieldName() != null && !this.getBillFormatPanel().getReturnRefItemVONameFieldName().equals("")) {
							refItemvo.setName(billVO.getStringValue(this.getBillFormatPanel().getReturnRefItemVONameFieldName())); //
						}
						return refItemvo;
					} else if (billPanel instanceof BillCardPanel) {
						BillCardPanel billCard = (BillCardPanel) billPanel;
						BillVO billVO = billCard.getBillVO(); //
						if (billVO == null) {
							throw new WLTAppException("��ѡ��һ��[" + billCard.getTempletVO().getTempletname() + "]����!"); //
						}
						RefItemVO refItemvo = new RefItemVO(convertHashVO(billVO));
						if (this.getBillFormatPanel().getReturnRefItemVOIDFieldName() != null && !this.getBillFormatPanel().getReturnRefItemVOIDFieldName().equals("")) {
							refItemvo.setId(billVO.getStringValue(this.getBillFormatPanel().getReturnRefItemVOIDFieldName())); //
						}
						if (this.getBillFormatPanel().getReturnRefItemVONameFieldName() != null && !this.getBillFormatPanel().getReturnRefItemVONameFieldName().equals("")) {
							refItemvo.setName(billVO.getStringValue(this.getBillFormatPanel().getReturnRefItemVONameFieldName())); //
						}
						return refItemvo;
					}
				}
			} else if (getRefStyleType() == 1) { //����Ƕ�ѡ
				if (this.getBillFormatPanel().getReturnRefItemVOFrom() != null) { //��������˷��ص������!��ʹ��������
					BillPanel billPanel = this.getBillFormatPanel().getReturnRefItemVOFrom(); //
					BillVO[] billVOs = null;
					if (billPanel instanceof BillListPanel) {
						BillListPanel billList = (BillListPanel) billPanel;
						billVOs = billList.getSelectedBillVOs(); //
					} else if (billPanel instanceof BillTreePanel) {
						BillTreePanel billTree = (BillTreePanel) billPanel;
						billVOs = billTree.getSelectedVOs(); //
					}

					if (billVOs != null) {
						String str_refid = "";
						String str_refname = "";
						String str_refidfield = this.getBillFormatPanel().getReturnRefItemVOIDFieldName();
						String str_refnamefield = this.getBillFormatPanel().getReturnRefItemVONameFieldName();
						for (int i = 0; i < billVOs.length; i++) {
							str_refid = str_refid + billVOs[i].getStringValue(str_refidfield) + ";"; //
							str_refname = str_refname + billVOs[i].getStringValue(str_refnamefield) + ";"; //
						}
						RefItemVO refItemvo = new RefItemVO(str_refid, null, str_refname); //
						return refItemvo;
					}
				} else {
					throw new WLTAppException("û�ж�����ĸ����ȡ������!"); //
				}
			} else if (getRefStyleType() == 2 || getRefStyleType() == 3 || getRefStyleType() == 4) { //��������ҷָ����ҳǩ�Ķ�ѡ..
				BillVO[] billVOs = getSelectedDataListPanel().getAllBillVOs(); //
				String str_refid = "";
				String str_refname = "";

				for (int i = 0; i < billVOs.length; i++) {
					str_refid = str_refid + billVOs[i].getStringValue("RefID") + ";"; //
					str_refname = str_refname + billVOs[i].getStringValue("RefName") + ";"; //
				}
				RefItemVO refItemvo = new RefItemVO(str_refid, null, str_refname); //
				return refItemvo;
			}
		}

		return null;
	}

	/**
	 * ����һ��
	 */
	public void onAdd() {
		BillPanel billPanel = this.getBillFormatPanel().getReturnRefItemVOFrom(); //
		if (billPanel == null) {
			MessageBox.show(this, "û�ж�����ĸ����ȡ��!"); //
			return; //
		}
		String str_refidfield = this.getBillFormatPanel().getReturnRefItemVOIDFieldName();
		String str_refnamefield = this.getBillFormatPanel().getReturnRefItemVONameFieldName();
		BillVO[] selbillVOs = null; //

		if (billPanel instanceof BillListPanel) {
			BillListPanel billList = (BillListPanel) billPanel;
			selbillVOs = billList.getSelectedBillVOs(); //
			if (selbillVOs == null || selbillVOs.length == 0) {
				MessageBox.show("��ѡ��һ��[" + billList.getTempletVO().getTempletname() + "]����!"); //
			}
		} else if (billPanel instanceof BillTreePanel) {
			BillTreePanel billTree = (BillTreePanel) billPanel;
			selbillVOs = billTree.getSelectedVOs(); //
			if (selbillVOs == null || selbillVOs.length == 0) {
				MessageBox.show("��ѡ��һ��[" + billTree.getTempletVO().getTempletname() + "]����!"); //
			}
		}

		if (selbillVOs != null && selbillVOs.length > 0) {
			for (int i = 0; i < selbillVOs.length; i++) {
				String str_refidvalue = selbillVOs[i].getStringValue(str_refidfield);
				String str_refnamevalue = selbillVOs[i].getStringValue(str_refnamefield);
				if (!isExist(getSelectedDataListPanel().getAllBillVOs(), str_refidvalue)) { //����Ѵ����򲻸�!!
					int li_row = getSelectedDataListPanel().addEmptyRow(); //
					getSelectedDataListPanel().setValueAt(new StringItemVO(str_refidvalue), li_row, "RefID"); //��������
					getSelectedDataListPanel().setValueAt(new StringItemVO(str_refnamevalue), li_row, "RefName"); //
				}
			}
		}
	}

	public void onAddAll() {
		BillPanel billPanel = this.getBillFormatPanel().getReturnRefItemVOFrom(); //
		if (billPanel == null) {
			MessageBox.show(this, "û�ж�����ĸ����ȡ��!"); //
			return; //
		}
		String str_refidfield = this.getBillFormatPanel().getReturnRefItemVOIDFieldName();
		String str_refnamefield = this.getBillFormatPanel().getReturnRefItemVONameFieldName();
		BillVO[] selbillVOs = null; //

		if (billPanel instanceof BillListPanel) {
			BillListPanel billList = (BillListPanel) billPanel;
			selbillVOs = billList.getAllBillVOs(); //
			if (selbillVOs == null || selbillVOs.length == 0) {
				MessageBox.show("��ѡ��һ��[" + billList.getTempletVO().getTempletname() + "]����!"); //
			}
		} else if (billPanel instanceof BillTreePanel) {
			BillTreePanel billTree = (BillTreePanel) billPanel;
			selbillVOs = billTree.getAllBillVOs(); //
			if (selbillVOs == null || selbillVOs.length == 0) {
				MessageBox.show("��ѡ��һ��[" + billTree.getTempletVO().getTempletname() + "]����!"); //
			}
		}

		if (selbillVOs != null && selbillVOs.length > 0) {
			for (int i = 0; i < selbillVOs.length; i++) {
				String str_refidvalue = selbillVOs[i].getStringValue(str_refidfield); //
				String str_refnamevalue = selbillVOs[i].getStringValue(str_refnamefield); //
				if (!isExist(getSelectedDataListPanel().getAllBillVOs(), str_refidvalue)) { //����Ѵ����򲻸�!!
					int li_row = getSelectedDataListPanel().addEmptyRow(); //
					getSelectedDataListPanel().setValueAt(new StringItemVO(str_refidvalue), li_row, "RefID"); //��������
					getSelectedDataListPanel().setValueAt(new StringItemVO(str_refnamevalue), li_row, "RefName"); //
				}
			}
		}
	}

	private boolean isExist(BillVO[] allBillVOs, String str_refidvalue) {
		for (int i = 0; i < allBillVOs.length; i++) {
			if (allBillVOs[i].getStringValue("RefID") != null && allBillVOs[i].getStringValue("RefID").equals(str_refidvalue)) {
				return true;
			}
		}
		return false;
	}

	public void onRemove() {
		getSelectedDataListPanel().removeSelectedRows(); //
	}

	public void onRemoveAll() {
		getSelectedDataListPanel().clearTable(); //
	}

	@Override
	public String getRegFormatCode() {
		return dfvo.getConfValue("ע�����");
	}

	@Override
	public int getRefStyleType() {
		try {
			return Integer.parseInt(dfvo.getConfValue("���")); //���շ������
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return 0; //
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

}
