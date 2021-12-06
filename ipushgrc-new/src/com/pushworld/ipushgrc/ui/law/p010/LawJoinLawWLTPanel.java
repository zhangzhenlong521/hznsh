package com.pushworld.ipushgrc.ui.law.p010;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardFrame;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractWLTCompentPanel;

import com.pushworld.ipushgrc.ui.law.LawShowHtmlDialog;

/**
 * 外规关联相关修订的外规，自定义控件【李春娟/2015-04-20】
 * @author lichunjuan
 *
 */

public class LawJoinLawWLTPanel extends AbstractWLTCompentPanel implements ActionListener, BillListHtmlHrefListener {

	private static final long serialVersionUID = -6967496356909878013L;

	private Pub_Templet_1_ItemVO templetItemVO = null;

	private String key = null;
	private String name = null;
	private JLabel label = null;
	private WLTButton btn_import, btn_delete, btn_show; //增,删,改,查
	private BillPanel billPanel = null; //
	private int li_width_all;

	private BillListPanel billListPanel = null; //列表模板
	private String primarykey = null; //
	private String foreignkey = null; //
	private String primaryname = null; //
	private TBUtil tbutil = new TBUtil();

	public LawJoinLawWLTPanel(Pub_Templet_1_ItemVO _templetVO, BillPanel _billPanel) {
		super();
		this.templetItemVO = _templetVO; //
		this.key = templetItemVO.getItemkey(); //
		this.name = templetItemVO.getItemname(); //
		this.billPanel = _billPanel; //
		primarykey = "id";
		foreignkey = "reflaw";
		primaryname = "lawname";
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

			label = createLabel(templetItemVO); //采用父亲提供的方法创建Label
			li_width_all = (int) (label.getPreferredSize().getWidth() + li_tablewidth); ////
			this.setPreferredSize(new Dimension(li_width_all, li_tableheight)); //
			this.add(label, BorderLayout.WEST); //

			billListPanel = new BillListPanel("LAW_LAW_LCJ_Q01", false); //
			billListPanel.getTempletVO().setIsshowlistpagebar(false); //从模板中修改不显示分页。直接隐藏分页面板会导致数据显示不全。
			billListPanel.initialize(); //加载模板
			billListPanel.getBillListBtnPanel().setVisible(false);
			billListPanel.getTitlePanel().setVisible(false);
			billListPanel.getQuickQueryPanel().setVisible(false);
			billListPanel.setBillListOpaque(false); //
			btn_import = new WLTButton("导入"); //
			btn_delete = new WLTButton("移除"); //
			btn_show = new WLTButton("查看");

			btn_import.addActionListener(this);
			btn_delete.addActionListener(this);
			btn_show.addActionListener(this);
			btn_import.setPreferredSize(new Dimension(50, 20));
			btn_delete.setPreferredSize(new Dimension(50, 20));
			btn_show.setPreferredSize(new Dimension(50, 20));
			billListPanel.addCustButton(btn_import); //
			billListPanel.addCustButton(btn_delete); //
			billListPanel.addCustButton(btn_show);

			String str_name = "";
			if (ClientEnvironment.getInstance().isEngligh()) {
				str_name = templetItemVO.getItemname_e();
			} else {
				str_name = templetItemVO.getItemname();
			}
			if (str_name != null && !"".equals(str_name.trim())) {//sunfujun/20120613/其实这里把查询面板隐藏了,也加上这段代码吧,由于在卡片上会显示字段名与模板名不太友好_gaofeng
				billListPanel.getTitleLabel().setText("");
			}
			billListPanel.addBillListHtmlHrefListener(this);
			this.add(billListPanel, BorderLayout.CENTER); //

		} catch (Exception ex) {
			ex.printStackTrace(); //
			this.removeAll(); //
			this.setLayout(new BorderLayout()); //
			this.setPreferredSize(new Dimension(100, 20)); //
			this.add(new JLabel("加载导入子表发生异常:" + ex.getClass().getName() + ":" + ex.getMessage())); //
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_import) {
			onImport();
		} else if (e.getSource() == btn_delete) {
			onDelete();
		} else if (e.getSource() == btn_show) {
			onShow();
		}
	}

	private void onImport() {
		try {
			ImportLawDialog dialog = new ImportLawDialog(this, "导入", billListPanel);
			dialog.setVisible(true);
			if (dialog.getCloseType() == 1) {
				if (dialog.getReturnRealValue() != null && dialog.getReturnRealValue().length > 0) {
					StringBuffer sb = new StringBuffer(";");
					String value = ((BillCardPanel) billPanel).getRealValueAt(foreignkey);
					for (int i = 0; i < dialog.getReturnRealValue().length; i++) {
						BillVO vo = dialog.getReturnRealValue()[i];
						if (vo.getStringValue("itemid") != null && !"".equals(vo.getStringValue("itemid"))) {
							if (value != null && value.contains(";" + vo.getStringValue("id") + "$" + vo.getStringValue("itemid") + ";")) {
								continue;
							}
							sb.append(vo.getStringValue("id") + "$" + vo.getStringValue("itemid") + ";");
						} else {
							if (value != null && value.contains(";" + vo.getStringValue("id") + ";")) {
								continue;
							}
							sb.append(vo.getStringValue("id") + ";");
						}
						billListPanel.addRow(vo);
					}
					((BillCardPanel) billPanel).setValueAt(foreignkey, value + sb.toString());
					resetHeight(); //重置高度！！
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

			if (JOptionPane.showConfirmDialog(billListPanel, "确定移除该记录吗?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return; //
			}
			billListPanel.removeRows(billListPanel.getSelectedRows()); ////
			((BillCardPanel) billPanel).setValueAt(foreignkey, getObject());
			resetHeight();
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	private void onShow() {
		BillVO billVO = this.billListPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(billListPanel);
			return;
		}
		BillCardPanel cardPanel = new BillCardPanel(billListPanel.templetVO); //
		cardPanel.setBillVO(billVO); //
		String str_recordName = billVO.toString(); //
		BillCardFrame frame = new BillCardFrame(billListPanel, billListPanel.templetVO.getTempletname() + "[" + str_recordName + "]", cardPanel, WLTConstants.BILLDATAEDITSTATE_INIT, true);
		frame.getBtn_confirm().setVisible(false); //
		frame.getBtn_save().setVisible(false); //
		cardPanel.setEditable(false); //
		frame.setVisible(true); //
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
		StringBuffer sb_return = new StringBuffer(";"); //
		for (int i = 0; i < li_rows; i++) {
			sb_return.append(((StringItemVO) billListPanel.getValueAt(i, "id")).getStringValue()); //
			if (billListPanel.getValueAt(i, "itemid") != null && !"".equals(billListPanel.getValueAt(i, "itemid"))) {
				sb_return.append("$" + ((StringItemVO) billListPanel.getValueAt(i, "itemid")).getStringValue()); //
			}
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
		MessageBox.show(this, "setValue=" + _value);
		resetHeight(); //
	}

	public void reset() {
		if (this.billListPanel != null) {
			this.billListPanel.clearTable(); //清空数据
			resetHeight(); //重置高度
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
		billListPanel.removeAllRows();
		if (_obj != null) {
			String value = "";
			if (_obj instanceof RefItemVO) {
				value = ((RefItemVO) _obj).getId();
			} else {
				value = (String) _obj.toString();
			}
			TBUtil tbutil = TBUtil.getTBUtil();
			String alljoin[] = tbutil.split(value, ";");
			ArrayList ids = new ArrayList();
			ArrayList itemids = new ArrayList();
			for (int i = 0; i < alljoin.length; i++) {
				String oneJoinStr = alljoin[i];
				if (oneJoinStr != null && !oneJoinStr.trim().equals("")) {
					if (oneJoinStr.contains("$")) {//如果关联全文，则就一个外规主表id，如 12$232;15;42$99;
						String id_itemid[] = tbutil.split(oneJoinStr, "$");
						ids.add(id_itemid[0]);
						itemids.add(id_itemid[1]);
					} else {
						ids.add(oneJoinStr);
					}
				}
			}
			try {
				HashVO[] lawVO = UIUtil.getHashVoArrayByDS(null, "select * from law_law where id in(" + tbutil.getInCondition(ids) + ")");
				HashVO[] lawitemVO = UIUtil.getHashVoArrayByDS(null, "select id,itemtitle,itemcontent from law_law_item where id in(" + tbutil.getInCondition(itemids) + ")");

				for (int i = 0; i < alljoin.length; i++) {
					String oneJoinStr = alljoin[i];
					if (oneJoinStr != null && !oneJoinStr.trim().equals("")) {
						HashMap map = new HashMap();
						String lawid = oneJoinStr;
						if (oneJoinStr.contains("$")) {//如果关联全文，则就一个外规主表id，如 12$232;15;42$99;
							String id_itemid[] = tbutil.split(oneJoinStr, "$");
							lawid = id_itemid[0];
							StringItemVO itemidVO = new StringItemVO(id_itemid[1]);
							map.put("itemid", itemidVO);
							for (int j = 0; j < lawitemVO.length; j++) {
								if (lawitemVO[j].getStringValue("id", "").equals(id_itemid[1])) {
									map.put("itemtitle", new StringItemVO(lawitemVO[j].getStringValue("itemtitle")));
									map.put("itemcontent", new StringItemVO(lawitemVO[j].getStringValue("itemcontent")));
									break;
								}
							}
						} else {
							map.put("itemtitle", new StringItemVO("全文"));
						}
						map.put("id", new StringItemVO(lawid));
						for (int j = 0; j < lawVO.length; j++) {
							if (lawVO[j].getStringValue("id", "").equals(lawid)) {
								map.put("lawname", new StringItemVO(lawVO[j].getStringValue("lawname")));
								map.put("dispatch_code", new StringItemVO(lawVO[j].getStringValue("dispatch_code")));
								map.put("issuecorp", new RefItemVO(lawVO[j].getStringValue("issuecorp"), null, lawVO[j].getStringValue("issuecorp")));
								map.put("lawtype", new ComBoxItemVO(lawVO[j].getStringValue("lawtype"), null, lawVO[j].getStringValue("lawtype")));
								map.put("state", new ComBoxItemVO(lawVO[j].getStringValue("state"), null, lawVO[j].getStringValue("state")));
								map.put("issue_date", new RefItemVO(lawVO[j].getStringValue("issue_date"), null, lawVO[j].getStringValue("issue_date")));
								map.put("implement_date", new RefItemVO(lawVO[j].getStringValue("implement_date"), null, lawVO[j].getStringValue("implement_date")));
								map.put("remark", new StringItemVO(lawVO[j].getStringValue("remark")));
								break;
							}
						}
						billListPanel.addRow(map);
					}
				}
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		resetHeight(); //重新设置高度
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

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		BillVO vo = _event.getBillListPanel().getSelectedBillVO();
		if (vo.getStringValue("itemid") == null || "".equals(vo.getStringValue("itemid"))) {
			new LawShowHtmlDialog(this, vo.getStringValue("id"));
		} else {
			new LawShowHtmlDialog(this, vo.getStringValue("id"), null, new String[] { vo.getStringValue("itemid") }); // 高亮出来关联的条目标题。
		}
	}
}
