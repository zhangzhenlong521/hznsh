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
 * 注册面板参照的默认实现
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
		if (!dfvo.getConfValue("数据生成器", "").equals("")) { //如果定义了按口,则使用接口
			RegFormatRefDataCreaterIFC refitemVOCreater = (RegFormatRefDataCreaterIFC) Class.forName(dfvo.getConfValue("数据生成器")).newInstance(); //
			return refitemVOCreater.createRefItemVO(getBillFormatPanel()); //由创建器去创建返回的数据对象!!!
		} else {
			if (getRefStyleType() == 0) {
				if (this.getBillFormatPanel().getReturnRefItemVOFrom() != null) { //如果定义了返回的面板类!则使用它试试
					BillPanel billPanel = this.getBillFormatPanel().getReturnRefItemVOFrom(); //
					if (billPanel instanceof BillListPanel) {
						BillListPanel billList = (BillListPanel) billPanel;
						BillVO billVO = billList.getSelectedBillVO(); //
						if (billVO == null) {
							throw new WLTAppException("请选择一条[" + billList.getTempletVO().getTempletname() + "]数据!"); //
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
							throw new WLTAppException("请选择一条[" + billTree.getTempletVO().getTempletname() + "]数据!"); //
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
							throw new WLTAppException("请选择一条[" + billCard.getTempletVO().getTempletname() + "]数据!"); //
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
			} else if (getRefStyleType() == 1) { //如果是多选
				if (this.getBillFormatPanel().getReturnRefItemVOFrom() != null) { //如果定义了返回的面板类!则使用它试试
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
					throw new WLTAppException("没有定义从哪个面板取数数据!"); //
				}
			} else if (getRefStyleType() == 2 || getRefStyleType() == 3 || getRefStyleType() == 4) { //如果是左右分割或两页签的多选..
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
	 * 增加一个
	 */
	public void onAdd() {
		BillPanel billPanel = this.getBillFormatPanel().getReturnRefItemVOFrom(); //
		if (billPanel == null) {
			MessageBox.show(this, "没有定义从哪个面板取数!"); //
			return; //
		}
		String str_refidfield = this.getBillFormatPanel().getReturnRefItemVOIDFieldName();
		String str_refnamefield = this.getBillFormatPanel().getReturnRefItemVONameFieldName();
		BillVO[] selbillVOs = null; //

		if (billPanel instanceof BillListPanel) {
			BillListPanel billList = (BillListPanel) billPanel;
			selbillVOs = billList.getSelectedBillVOs(); //
			if (selbillVOs == null || selbillVOs.length == 0) {
				MessageBox.show("请选择一条[" + billList.getTempletVO().getTempletname() + "]数据!"); //
			}
		} else if (billPanel instanceof BillTreePanel) {
			BillTreePanel billTree = (BillTreePanel) billPanel;
			selbillVOs = billTree.getSelectedVOs(); //
			if (selbillVOs == null || selbillVOs.length == 0) {
				MessageBox.show("请选择一条[" + billTree.getTempletVO().getTempletname() + "]数据!"); //
			}
		}

		if (selbillVOs != null && selbillVOs.length > 0) {
			for (int i = 0; i < selbillVOs.length; i++) {
				String str_refidvalue = selbillVOs[i].getStringValue(str_refidfield);
				String str_refnamevalue = selbillVOs[i].getStringValue(str_refnamefield);
				if (!isExist(getSelectedDataListPanel().getAllBillVOs(), str_refidvalue)) { //如果已存在则不干!!
					int li_row = getSelectedDataListPanel().addEmptyRow(); //
					getSelectedDataListPanel().setValueAt(new StringItemVO(str_refidvalue), li_row, "RefID"); //插入数据
					getSelectedDataListPanel().setValueAt(new StringItemVO(str_refnamevalue), li_row, "RefName"); //
				}
			}
		}
	}

	public void onAddAll() {
		BillPanel billPanel = this.getBillFormatPanel().getReturnRefItemVOFrom(); //
		if (billPanel == null) {
			MessageBox.show(this, "没有定义从哪个面板取数!"); //
			return; //
		}
		String str_refidfield = this.getBillFormatPanel().getReturnRefItemVOIDFieldName();
		String str_refnamefield = this.getBillFormatPanel().getReturnRefItemVONameFieldName();
		BillVO[] selbillVOs = null; //

		if (billPanel instanceof BillListPanel) {
			BillListPanel billList = (BillListPanel) billPanel;
			selbillVOs = billList.getAllBillVOs(); //
			if (selbillVOs == null || selbillVOs.length == 0) {
				MessageBox.show("请选择一条[" + billList.getTempletVO().getTempletname() + "]数据!"); //
			}
		} else if (billPanel instanceof BillTreePanel) {
			BillTreePanel billTree = (BillTreePanel) billPanel;
			selbillVOs = billTree.getAllBillVOs(); //
			if (selbillVOs == null || selbillVOs.length == 0) {
				MessageBox.show("请选择一条[" + billTree.getTempletVO().getTempletname() + "]数据!"); //
			}
		}

		if (selbillVOs != null && selbillVOs.length > 0) {
			for (int i = 0; i < selbillVOs.length; i++) {
				String str_refidvalue = selbillVOs[i].getStringValue(str_refidfield); //
				String str_refnamevalue = selbillVOs[i].getStringValue(str_refnamefield); //
				if (!isExist(getSelectedDataListPanel().getAllBillVOs(), str_refidvalue)) { //如果已存在则不干!!
					int li_row = getSelectedDataListPanel().addEmptyRow(); //
					getSelectedDataListPanel().setValueAt(new StringItemVO(str_refidvalue), li_row, "RefID"); //插入数据
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
		return dfvo.getConfValue("注册编码");
	}

	@Override
	public int getRefStyleType() {
		try {
			return Integer.parseInt(dfvo.getConfValue("风格")); //参照风格类型
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
