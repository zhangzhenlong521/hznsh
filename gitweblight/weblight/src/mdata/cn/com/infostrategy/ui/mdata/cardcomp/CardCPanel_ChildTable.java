/**************************************************************************
 * $RCSfile: CardCPanel_ChildTable.java,v $  $Revision: 1.19 $  $Date: 2012/10/08 02:22:48 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import cn.com.infostrategy.to.common.TBUtil;
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
 * 引用子表中在卡片的显示样子!!
 * @author xch
 *
 */
public class CardCPanel_ChildTable extends AbstractWLTCompentPanel implements ActionListener {

	private static final long serialVersionUID = -6967496356909878013L;

	private Pub_Templet_1_ItemVO templetItemVO = null;

	private String key = null;
	private String name = null;
	private String refDesc = null; //

	private JLabel label = null;
	private WLTButton btn_insert, btn_delete, btn_edit, btn_select = null; //增,删,改,查
	private boolean isEditable = true; //
	private int li_width_all;

	private BillListPanel billListPanel = null; //列表模板
	private String billtempletcode = null; //
	private String primarykey = null; //
	private String foreignkey = null; //
	private String intercept = null;
	private ArrayList list_sqls = new ArrayList();
	private ArrayList list_sqls_del = new ArrayList();
	private String str_delids = "";
	private HashMap quzhiMap = new HashMap();

	public CardCPanel_ChildTable(Pub_Templet_1_ItemVO _templetVO, BillPanel _billPanel) {
		super();
		this.templetItemVO = _templetVO; //
		this.key = templetItemVO.getItemkey(); //
		this.name = templetItemVO.getItemname(); //
		this.refDesc = templetItemVO.getRefdesc(); //定义,就靠它来创建列表模板
		this.billPanel = _billPanel; //
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
				li_tableheight = templetItemVO.getCardHeight().intValue(); //
			}

			if (li_tableheight < 80) {
				li_tableheight = 80;
			}

			label = createLabel(templetItemVO); //采用父亲提供的方法创建Label
			li_width_all = (int) (label.getPreferredSize().getWidth() + li_tablewidth); ////
			this.setPreferredSize(new Dimension(li_width_all, li_tableheight)); //
			this.add(label, BorderLayout.WEST); //

			CommUCDefineVO uCDfVO = this.templetItemVO.getUCDfVO(); //
			if (uCDfVO == null) {
				this.add(new JLabel("引用子表的定义为空!"), BorderLayout.CENTER); //
				return;
			}

			billtempletcode = uCDfVO.getConfValue("模板编码");
			primarykey = uCDfVO.getConfValue("主键字段名");
			foreignkey = uCDfVO.getConfValue("关联外键字段名");
			intercept = uCDfVO.getConfValue("拦截器"); //
			String quzhi = uCDfVO.getConfValue("取值"); //子表字段=主表字段; 比如，deptid = checkdept;userid=userid;
			if (quzhi != null && !quzhi.trim().equals("")) {
				String[] quzhis = TBUtil.getTBUtil().split(quzhi, ";");
				if (quzhis != null) {
					for (int i = 0; i < quzhis.length; i++) {
						String key = quzhis[i].substring(0, quzhis[i].indexOf("="));
						String value = quzhis[i].substring(quzhis[i].indexOf("=") + 1);
						quzhiMap.put(key, value);
					}
				}
			}
			billListPanel = new BillListPanel(billtempletcode); //
			billListPanel.setBillListOpaque(false); //
			billListPanel.setLoaderChildTable(this); //
			//billListPanel.setAllBillListBtnVisiable(false); //所有按钮都隐藏
			billListPanel.setQuickQueryPanelVisiable(false);//sunfujun/20120719/hm
			//统一系统中的命名和顺序 Gwang 2012-06-26
			btn_insert = new WLTButton("新增"); //			
			btn_edit = new WLTButton("修改"); //
			btn_delete = new WLTButton("删除"); //
			btn_select = new WLTButton("浏览"); //

			btn_insert.addActionListener(this);
			btn_delete.addActionListener(this);
			btn_edit.addActionListener(this);
			btn_select.addActionListener(this);

			btn_insert.setPreferredSize(new Dimension(50, 20));
			btn_delete.setPreferredSize(new Dimension(50, 20));
			btn_edit.setPreferredSize(new Dimension(50, 20));
			btn_select.setPreferredSize(new Dimension(50, 20));

			billListPanel.addCustButton(btn_insert); //
			billListPanel.addCustButton(btn_edit); //
			billListPanel.addCustButton(btn_delete); //			
			billListPanel.addCustButton(btn_select); //

			String str_name = "";
			if (ClientEnvironment.getInstance().isEngligh()) {
				str_name = templetItemVO.getItemname_e();
			} else {
				str_name = templetItemVO.getItemname();
			}
			if (str_name != null && !"".equals(str_name.trim())) {//sunfujun/20120613/由于在卡片上会显示字段名与模板名不太友好_gaofeng
				billListPanel.getTitleLabel().setText("");
			}

			this.add(billListPanel, BorderLayout.CENTER); //
			if (intercept != null && !intercept.equals("")) {//郝明-引用子表加入拦截器
				ChildTableCommUIIntercept listener = (ChildTableCommUIIntercept) Class.forName(intercept).newInstance(); //				
				listener.afterInitialize(billListPanel);
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
			this.removeAll(); //
			this.setLayout(new BorderLayout()); //
			this.setPreferredSize(new Dimension(100, 20)); //
			this.add(new JLabel("加载页面发生异常:" + ex.getClass().getName() + ":" + ex.getMessage())); //
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_insert) {
			onInsert();
		} else if (e.getSource() == btn_delete) {
			onDelete();
		} else if (e.getSource() == btn_edit) {
			onEdit();
		} else if (e.getSource() == btn_select) {
			onSelect();
		}
	}

	public void onInsert() {
		try {
			BillCardPanel cardPanel = new BillCardPanel(this.billListPanel.templetVO); //创建一个卡片面板
			cardPanel.insertRow(); //卡片新增一行!
			BillCardPanel mainCardPanel = ((BillCardPanel) this.billPanel);
			String str_cardprimarykey = mainCardPanel.getRealValueAt(this.primarykey); //
			cardPanel.setCompentObjectValue(foreignkey, new StringItemVO(str_cardprimarykey)); //

			//经常遇到引用子表新增或修改时需要获得主卡片中的值【李春娟/2016-03-20】
			if (quzhiMap.size() > 0) {
				String[] keys = (String[]) quzhiMap.keySet().toArray(new String[0]);
				for (int i = 0; i < keys.length; i++) {
					cardPanel.setCompentObjectValue(keys[i], mainCardPanel.getBillVO().getObject((String) quzhiMap.get(keys[i]))); //
				}
			}

			cardPanel.setEditableByInsertInit(); //设置卡片编辑状态为新增时的设置
			//弹出卡片新增框，如果模板itemname中有换行则会出现窗口中名称垂直靠上的情况，故需要将换行替换一下【李春娟/2012-03-28】
			BillCardDialog dialog = new BillCardDialog(this, "【新增】" + this.templetItemVO.getItemname().replaceAll("\n", ""), cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);
			dialog.setVisible(true); //

			if (dialog.getCloseType() == 1 || dialog.getCloseType() == 100) { //如是是点击确定返回!将则卡片中的数据赋给列表!
				int li_newrow = this.billListPanel.newRow(); //
				billListPanel.setBillVOAt(li_newrow, dialog.getBillVO());
				billListPanel.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); //设置列表该行的数据为初始化状态.
				resetHeight(); //重置高度

				//子表新增时追加删除sql 【杨科/2013-03-26】
				String str_sql = "delete from " + billListPanel.templetVO.getSavedtablename() + " where " + billListPanel.templetVO.getPkname() + "='" + dialog.getBillVO().getStringValue(billListPanel.templetVO.getPkname()) + "'";
				list_sqls.add(str_sql);
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	public ArrayList getList_sqls(boolean isdel) {
		if (isdel) {
			return list_sqls_del;
		} else {
			return list_sqls;
		}
	}

	public void clearList_sqls(boolean isdel) {
		if (isdel) {
			list_sqls_del.clear();
		} else {
			list_sqls.clear();
		}

	}

	private void onDelete() {
		try {
			BillVO billVO = this.billListPanel.getSelectedBillVO();
			if (billVO == null) {
				MessageBox.showSelectOne(billListPanel);
				return;
			}

			if (JOptionPane.showConfirmDialog(billListPanel, "确定删除该记录吗?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return; //
			}

			String str_sql = "delete from " + billListPanel.templetVO.getSavedtablename() + " where " + billListPanel.templetVO.getPkname() + "='" + billVO.getStringValue(billListPanel.templetVO.getPkname()) + "'"; //
			//UIUtil.executeUpdateByDS(billListPanel.getDataSourceName(), str_sql); //

			//子表删除时追加删除sql补充修改 【杨科/2013-06-07】
			str_delids += "'" + billVO.getStringValue(billListPanel.templetVO.getPkname()) + "',";

			//子表删除时追加删除sql 【杨科/2013-03-26】
			list_sqls_del.add(str_sql);
			billListPanel.removeRow(billListPanel.getSelectedRow()); //
			resetHeight();
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	private void onEdit() {
		try {
			BillVO billVO = billListPanel.getSelectedBillVO();
			if (billVO == null) {
				MessageBox.showSelectOne(billListPanel);
				return;
			}

			BillCardPanel cardPanel = new BillCardPanel(this.billListPanel.templetVO); //创建一个卡片面板
			cardPanel.setBillVO(billVO); //
			//经常遇到引用子表新增或修改时需要获得主卡片中的值【李春娟/2016-03-20】
			//需要在设置billVO执行，否则值会冲掉
			int selectrow = billListPanel.getSelectedRow();//下面设置值会去掉选择，为了后面根据选择赋值，故记录选择行
			if (quzhiMap.size() > 0) {
				String[] keys = (String[]) quzhiMap.keySet().toArray(new String[0]);
				BillCardPanel mainCardPanel = ((BillCardPanel) this.billPanel);
				for (int i = 0; i < keys.length; i++) {
					cardPanel.setCompentObjectValue(keys[i], mainCardPanel.getBillVO().getObject((String) quzhiMap.get(keys[i]))); //
				}
			}
			cardPanel.setEditableByEditInit(); //设置卡片编辑状态为新增时的设置
			BillCardDialog dialog = new BillCardDialog(this, this.templetItemVO.getPub_Templet_1VO().getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE); //弹出卡片新增框
			dialog.setVisible(true); //
			billListPanel.setSelectedRow(selectrow);
			if (dialog.getCloseType() == 1) {
				billListPanel.setBillVOAt(selectrow, dialog.getBillVO());
				billListPanel.setRowStatusAs(selectrow, WLTConstants.BILLDATAEDITSTATE_INIT);
			}
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

			BillCardPanel cardPanel = new BillCardPanel(this.billListPanel.templetVO); //创建一个卡片面板
			cardPanel.setBillVO(billVO); //
			cardPanel.setEditable(false); //设置卡片编辑状态为新增时的设置
			BillCardDialog dialog = new BillCardDialog(this, this.templetItemVO.getPub_Templet_1VO().getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_INIT); //弹出卡片新增框
			dialog.setVisible(true); //
			this.getPropertyChangeListeners();
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	public void resetHeight() {
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
		String str_return = ""; //
		for (int i = 0; i < li_rows; i++) {
			str_return = str_return + billListPanel.getValueAt(i, billListPanel.getTempletVO().getPkname()) + ";"; //
		}
		return str_return; //
	}

	public void setValue(String _value) {
		resetHeight(); //
	}

	public void reset() {
		if (this.billListPanel != null) {
			this.billListPanel.clearTable(); //清空数据
			resetHeight(); //重置高度
		}
	}

	public void setItemEditable(boolean _bo) {
		if (btn_insert != null) {
			this.btn_insert.setEnabled(_bo);
			this.btn_delete.setEnabled(_bo); //
			this.btn_edit.setEnabled(_bo); //
			this.btn_select.setEnabled(true); //
		}
	}

	@Override
	public boolean isItemEditable() {
		return true;
	}

	public void setOnlyUpdate() {
		if (btn_insert != null) {
			this.btn_insert.setEnabled(false);
			this.btn_delete.setEnabled(false); //
			this.btn_edit.setEnabled(true); //
			this.btn_select.setEnabled(true); //
		}
	}

	public void setOnlyLookat() {
		if (btn_insert != null) {
			this.btn_insert.setEnabled(false);
			this.btn_delete.setEnabled(false); //
			this.btn_edit.setEnabled(false); //
			this.btn_select.setEnabled(true); //
		}
	}

	public void setItemVisiable(boolean _bo) {
		this.setVisible(_bo); //
	}

	public Object getObject() {//[sunfujun/20120524/bug]
		int li_rows = this.billListPanel.getRowCount(); //
		String str_return = ""; //
		String str_return_name = "";
		for (int i = 0; i < li_rows; i++) {
			str_return = str_return + billListPanel.getValueAt(i, billListPanel.getTempletVO().getPkname()) + ";"; //
			str_return_name = str_return_name + billListPanel.getBillVO(i).toString() + ";";
		}
		return new RefItemVO(str_return, null, str_return_name);
	}

	public void setObject(Object _obj) {
		if (this.primarykey == null) {
			return;
		}
		String str_cardprimarykey = ((BillCardPanel) this.billPanel).getRealValueAt(this.primarykey); //
		String str_pkname = billListPanel.templetVO.getPkname();
		String str_seq = billListPanel.templetVO.getOrdercondition();
		if (str_seq == null || "".equals(str_seq.trim())) {
			str_seq = str_pkname;
		}
		//子表删除时追加删除sql补充修改 并追加order by id 李春娟需要 【杨科/2013-06-07】 如果模版中有配置排序字段则按配置的来  【李春娟/2013-11-22】
		if (str_delids.equals("")) {
			billListPanel.queryDataByCondition(this.foreignkey + "='" + str_cardprimarykey + "'", str_seq);
		} else {
			String str_delids_ = str_delids.substring(0, str_delids.length() - 1);
			billListPanel.queryDataByCondition(this.foreignkey + "='" + str_cardprimarykey + "'" + " and " + str_pkname + " not in (" + str_delids_ + ")", str_seq);
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

	public WLTButton getBtn_insert() {
		return btn_insert;
	}

	public WLTButton getBtn_delete() {
		return btn_delete;
	}

	public WLTButton getBtn_edit() {
		return btn_edit;
	}

	public WLTButton getBtn_select() {
		return btn_select;
	}

}
