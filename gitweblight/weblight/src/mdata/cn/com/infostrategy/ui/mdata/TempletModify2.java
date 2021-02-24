/**************************************************************************
 * $RCSfile: TempletModify2.java,v $  $Revision: 1.18 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.DefaultCellEditor;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.to.mdata.templetvo.TMO_Pub_Templet_1;
import cn.com.infostrategy.to.mdata.templetvo.TMO_Pub_Templet_1_item;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;

public class TempletModify2 extends BillDialog implements ActionListener {
	private static final long serialVersionUID = 1L;

	private String str_templetcode = null;
	private String xml_url = null;
	private BillCardPanel cardPanel = null;
	private BillListPanel listPanel = null;
	private WLTButton btn_exportXml, btn_insertrow, btn_delrow, btn_moveup, btn_movedown, btn_refresh, btn_save, btn_confirm, btn_cancel, btn_importOther; //
	Container parent = null; //袁江晓 20130314 添加  用于实现刷新父模板

	private boolean isAppConf = false; //是否是实施人员配置!!!

	private boolean isViewXml = false;

	public TempletModify2(Container _parent, String _templetcode) {
		this(_parent, _templetcode, false); //
	}

	public TempletModify2(Container _parent, String _templetcode, boolean _isAppConf) {
		super(_parent, "模板编辑", 1024, 740);
		this.parent = _parent;
		str_templetcode = _templetcode;
		isAppConf = _isAppConf; //是否实施人员配置!
		initialize();
		this.setVisible(true); //
	}

	public TempletModify2(Container _parent, String _templetcode, boolean _isAppConf, boolean _isViewXml) {
		super(_parent, "模板查看", 1024, 740);
		str_templetcode = _templetcode;
		isAppConf = _isAppConf; //是否实施人员配置!
		isViewXml = _isViewXml;
		initialize();
		this.setVisible(true); //
	}

	//追加直接从xml路径取 【杨科/2013-03-25】
	public TempletModify2(Container _parent, String _templetcode, String _xml_url, boolean _isAppConf, boolean _isViewXml) {
		super(_parent, "模板查看", 1024, 740);
		str_templetcode = _templetcode;
		xml_url = _xml_url;
		isAppConf = _isAppConf; //是否实施人员配置!
		isViewXml = _isViewXml;
		initialize();
		this.setVisible(true); //
	}

	private void initialize() {
		this.getContentPane().setLayout(new BorderLayout()); //
		cardPanel = new BillCardPanel(new TMO_Pub_Templet_1(isAppConf));
		cardPanel.updateCurrRow(); //  
		cardPanel.setEditable("PK_PUB_TEMPLET_1", false); //
		cardPanel.setEditable("TEMPLETCODE", false); //

		listPanel = new BillListPanel(new TMO_Pub_Templet_1_item(isAppConf), false);
		listPanel.setBillListOpaque(false); //透明的
		listPanel.initialize();

		if (isAppConf) { //实施模式是否允许修改这两列的!
			listPanel.setItemEditable("itemkey", false); //
			listPanel.setItemEditable("itemtype", false); //
		} else {
			listPanel.setItemEditable(true); //
		}

		WLTSplitPane split = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, cardPanel, listPanel); //
		if (isAppConf) {
			split.setDividerLocation(100); //
		} else {
			split.setDividerLocation(300); //
		}
		this.getContentPane().add(split, BorderLayout.CENTER); //

		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //加入所有按钮
		onRefresh(); //刷新数据
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 2)); //

		btn_exportXml = new WLTButton("导出XML"); //
		btn_insertrow = new WLTButton("新增"); //
		btn_delrow = new WLTButton("删除"); //
		btn_moveup = new WLTButton("上移"); //
		btn_movedown = new WLTButton("下移"); //
		btn_refresh = new WLTButton("刷新"); //
		btn_save = new WLTButton("保存"); //
		btn_confirm = new WLTButton("确定"); //
		btn_cancel = new WLTButton("取消"); //
		btn_importOther = new WLTButton("快速复制");
		btn_importOther.setToolTipText("从其他模板复制部分字段");
		btn_exportXml.addActionListener(this); //
		btn_insertrow.addActionListener(this); //
		btn_delrow.addActionListener(this); //
		btn_moveup.addActionListener(this); //
		btn_movedown.addActionListener(this); //
		btn_refresh.addActionListener(this); //
		btn_save.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		btn_cancel.addActionListener(this); //
		btn_importOther.addActionListener(this);
		if (!isViewXml) { //如果是不是查看xml中模板
			if (isAppConf) { //实施模式,只能调顺序!!!
				panel.add(btn_exportXml);
				panel.add(btn_moveup);
				panel.add(btn_movedown);
				panel.add(btn_refresh);
				panel.add(btn_save);
				panel.add(btn_confirm);
			} else { //开发模式!!每个都要
				panel.add(btn_exportXml);
				panel.add(btn_importOther);
				panel.add(btn_insertrow);
				panel.add(btn_delrow);
				panel.add(btn_moveup);
				panel.add(btn_movedown);
				panel.add(btn_refresh);
				panel.add(btn_save);
				panel.add(btn_confirm);
			}
		}
		panel.add(btn_cancel);
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_exportXml) {
			onExportXML(); //导出XML
		} else if (e.getSource() == btn_insertrow) {
			onInsert();
		} else if (e.getSource() == btn_delrow) {
			onDelete();
		} else if (e.getSource() == btn_moveup) {
			onMoveup();
		} else if (e.getSource() == btn_movedown) {
			onMovedown();
		} else if (e.getSource() == btn_refresh) {
			onRefresh();
		} else if (e.getSource() == btn_save) {
			onSave();
		} else if (e.getSource() == btn_confirm) {
			onSaveAndExit();
		} else if (e.getSource() == btn_cancel) {
			onExit();
		} else if (e.getSource() == btn_importOther) {
			onImportOther();
		}
	}

	private void onImportOther() {
		MetaTempletQueryDialog dialog = new MetaTempletQueryDialog(this);
		dialog.setSize(800, 600);
		dialog.locationToCenterPosition();
		dialog.setVisible(true);
		String templetCode = dialog.getSelectTempletCode();
		if (dialog.getCloseType() == 0 && templetCode != null) {
			try {
				FrameWorkMetaDataServiceIfc service = UIUtil.getMetaDataService();
				String xml_url = dialog.getSelectTempletFrom();
				boolean isxml = false;
				if ("数据库".equals(xml_url)) {

				} else {
					if (xml_url != null && xml_url.contains("XML_")) {
						xml_url = xml_url.substring(xml_url.indexOf("XML_"));
						isxml = true;
					}
				}
				BillListPanel copylistPanel = new BillListPanel(new TMO_Pub_Templet_1_item(isAppConf), false);
				copylistPanel.initialize();
				DefaultTMO tmo = null;
				if (isxml) {
					tmo = service.getDefaultTMOByCode(templetCode, 1); //取到
					HashVO templet_1_item_vos[] = tmo.getPub_templet_1_itemData();
					copylistPanel.queryDataByHashVOs(templet_1_item_vos);
				} else {
					String str_pkvalue = UIUtil.getStringValueByDS(null, "select PK_PUB_TEMPLET_1 from PUB_TEMPLET_1 where TEMPLETCODE='" + templetCode + "'"); //
					copylistPanel.QueryData("select * from PUB_TEMPLET_1_ITEM where 1=1  and PK_PUB_TEMPLET_1=" + str_pkvalue + " order by showorder asc");
				}

				copylistPanel.setRowNumberChecked(true);
				BillListDialog listd = new BillListDialog(this, "", copylistPanel);
				listd.setVisible(true);
				if (listd.getCloseType() == 1) {
					BillVO rtvos[] = listd.getBilllistPanel().getCheckedBillVOs();
					int selectRow = listPanel.getSelectedRow();
					for (int i = 0; i < rtvos.length; i++) {
						onInsert();
						selectRow++;
						listPanel.stopEditing();
						String keys[] = rtvos[i].getKeys();
						for (int j = 0; j < keys.length; j++) {
							String key = keys[j];
							if (!key.equalsIgnoreCase("_RECORD_ROW_NUMBER") && !key.equalsIgnoreCase("PK_PUB_TEMPLET_1_ITEM") && !key.equalsIgnoreCase("PK_PUB_TEMPLET_1")) {
								listPanel.setValueAt(rtvos[i].getObject(key), selectRow, j);
							}
						}

					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	//新增行!!
	private void onInsert() {
		int li_row = listPanel.newRow(); //新增一行
		String str_parentid = cardPanel.getRealValueAt("PK_PUB_TEMPLET_1");
		listPanel.setValueAt(new StringItemVO(str_parentid), li_row, "PK_PUB_TEMPLET_1"); //

		//listPanel.setValueAt(str_parentid, li_row, "PK_PUB_TEMPLET_1"); //

		listPanel.setValueAt(new ComBoxItemVO("文本框", "文本框", "文本框"), li_row, "ITEMTYPE"); //

		listPanel.setValueAt("Y", li_row, "CARDISSHOWABLE"); //
		listPanel.setValueAt(new ComBoxItemVO("1", "001", "全部可编辑"), li_row, "CARDISEDITABLE"); //

		listPanel.setValueAt("Y", li_row, "LISTISSHOWABLE"); //

		//列表中是否可编辑改为全部禁用 【杨科/2013-03-13】
		listPanel.setValueAt(new ComBoxItemVO("4", "004", "全部禁用"), li_row, "LISTISEDITABLE");

		//修改卡片默认宽度为140 【杨科/2013-03-13】
		listPanel.setValueAt(new StringItemVO("140"), li_row, "CARDWIDTH");

		listPanel.setValueAt(new StringItemVO("125"), li_row, "LISTWIDTH"); //

		listPanel.setValueAt(new StringItemVO("" + (li_row + 1)), li_row, "SHOWORDER"); //

		//是否参与保存改为Y 【杨科/2013-03-13】
		listPanel.setValueAt("Y", li_row, "ISSAVE");

		//listPanel.setValueAt("N", li_row, "ISMUSTINPUT"); //

		listPanel.setValueAt(new ComBoxItemVO("2", "002", "通用查询"), li_row, "ISDEFAULTQUERY"); //

		//属性框是否显示改为Y-属性框是否可编辑改为Y 【杨科/2013-03-13】
		listPanel.setValueAt("Y", li_row, "PROPISSHOWABLE"); //属性框是否显示!
		listPanel.setValueAt("Y", li_row, "PROPISEDITABLE"); //属性框是否可编辑!

		listPanel.setValueAt("N", li_row, "ISWRAP"); //

		//追加必输项类型-列表中是否参与导出 【杨科/2013-03-13】
		listPanel.setValueAt(new ComBoxItemVO("N", "N", "自由项"), li_row, "ISMUSTINPUT");
		listPanel.setValueAt("Y", li_row, "LISTISEXPORT");

		resetShowOrder(); //重新排一下顺序

		listPanel.getTable().getCellEditor(li_row, 0).cancelCellEditing();
		listPanel.getTable().editCellAt(li_row, 0); //
		JTextField textField = ((JTextField) ((DefaultCellEditor) listPanel.getTable().getCellEditor(li_row, 0)).getComponent());
		textField.requestFocus();
	}

	//删除行!!
	private void onDelete() {
		listPanel.removeSelectedRows(); //删除所有选择的行
		resetShowOrder(); //重新排一下顺序
	}

	//保存
	private boolean onSave() {
		if (parent instanceof BillCardPanel) {//如果是从打开的页面点右键修改模板
			((BillCardPanel) parent).setCanRefreshParent(true);//
		} else if (parent instanceof BillListPanel) {//如果是从打开的页面点右键修改模板
			((BillListPanel) parent).setCanRefreshParent(true);
		} else if (parent instanceof BillQueryPanel) {//因为如果为billquery则只可能从页面点右键修改模板
		}
		stopEditing();
		String str_sql_1 = cardPanel.getUpdateSQL();
		String[] str_sqls_2 = listPanel.getOperatorSQLs();
		ArrayList list = new ArrayList();
		list.add(str_sql_1);
		list.addAll(Arrays.asList(str_sqls_2)); //
		try {
			UIUtil.executeBatchByDS(null, list); //
			listPanel.setAllRowStatusAs("INIT");
			//JOptionPane.showMessageDialog(this, "保存数据成功!!!"); 界面直接刷新就不要提示了 Gwang 2013-06-22
			return true;
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
			return false;
		}

	}

	private void onMoveup() {
		if (listPanel.moveUpRow()) {
			resetShowOrder();
		}
	}

	private void onMovedown() {
		if (listPanel.moveDownRow()) {
			resetShowOrder(); //	
		}
	}

	//刷新
	private void onRefresh() {
		stopEditing(); //
		if (isViewXml) { //如果是查看xml。
			try {
				DefaultTMO tmo = null;
				FrameWorkMetaDataServiceIfc service = UIUtil.getMetaDataService();

				//直接从xml路径取 【杨科/2013-03-25】
				if (xml_url != null && !xml_url.equals("")) {
					tmo = service.getDefaultTMOByCode(xml_url, 3); //取到
				} else {
					tmo = service.getDefaultTMOByCode(str_templetcode, 1); //取到
				}

				HashVO templet_1_vos = tmo.getPub_templet_1Data();
				HashVO templet_1_item_vos[] = tmo.getPub_templet_1_itemData();
				Object obj[][] = service.getBillListDataByHashVOs(cardPanel.getTempletVO().getParPub_Templet_1VO(), new HashVO[] { templet_1_vos }); //直接根据hashvo得到控件内容
				if (obj != null && obj.length > 0) {
					cardPanel.setValue(obj[0]); //
				}
				listPanel.queryDataByHashVOs(templet_1_item_vos);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				cardPanel.refreshData("TEMPLETCODE='" + str_templetcode + "'");
				String str_pkvalue = UIUtil.getStringValueByDS(null, "select PK_PUB_TEMPLET_1 from PUB_TEMPLET_1 where TEMPLETCODE='" + str_templetcode + "'"); //
				listPanel.QueryData("select * from PUB_TEMPLET_1_ITEM where 1=1  and PK_PUB_TEMPLET_1=" + str_pkvalue + " order by showorder asc");
				//listPanel.queryDataByHashVOs(_hashVOs);  //
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}
		}
	}

	//导出XML
	private void onExportXML() {
		try {
			FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //
			String str_xml = service.getXMlFromTableRecords(null, new String[] { "select * from pub_templet_1 where templetcode='" + str_templetcode + "'", //主表的所有数据!这是一个通用的万能的导出所有表结构的
					"select * from pub_templet_1_item where pk_pub_templet_1 in (select pk_pub_templet_1 from pub_templet_1 where templetcode='" + str_templetcode + "') order by showorder" }, //子表数据 
					new String[][] { { "pub_templet_1", "pk_pub_templet_1", "S_PUB_TEMPLET_1" }, { "pub_templet_1_item", "pk_pub_templet_1_item", "S_PUB_TEMPLET_1_ITEM" } }, //主键字段约束!! 
					new String[][] { { "pub_templet_1_item.pk_pub_templet_1", "pub_templet_1.pk_pub_templet_1" } }, null //外键约束!!
					);
			JFileChooser fc = new JFileChooser();
			fc.setSelectedFile(new File("C:\\" + str_templetcode + ".xml")); //设置默认文件名!!!
			int li_result = fc.showSaveDialog(this); //
			if (li_result == JFileChooser.APPROVE_OPTION) { //如果是确定的
				File saveToFile = fc.getSelectedFile(); //
				new TBUtil().writeBytesToOutputStream(new FileOutputStream(saveToFile, false), str_xml.getBytes("UTF-8")); //写文件!!
				MessageBox.show(this, "将模板导出的XML写入文件[" + saveToFile.getAbsolutePath() + "]成功!"); //
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	//确认,即保存并退出!!
	private void onSaveAndExit() {
		if (onSave()) { //先保存
			this.dispose(); //
			setCloseType(1); //
			refreshPraent(this.parent);
		}
	}

	//退出!!!
	private void onExit() {
		setCloseType(2); //
		this.dispose(); //
		//不重新打开菜单实现刷新  袁江晓  20130313
		refreshPraent(this.parent);
	}

	//重写父亲的关闭页面方法
	@Override
	public void dispose() {
		super.dispose();
		refreshPraent(this.parent); //关闭页面前必须刷新父窗口模板
	}

	// 不重新打开菜单实现刷新 袁江晓 20130313 确定按钮事件
	public void refreshPraent(Container _parent) {
		if (_parent instanceof BillCardPanel) {
			BillCardPanel bcp = (BillCardPanel) _parent;
			BillVO billvo = bcp.getBillVO();// 先获得页面的值需要后面重新加载
			bcp.reload(bcp.getTempletVO().getTempletcode());
			bcp.setBillVO(billvo);
			bcp.updateUI();
			String edittype = billvo.getEditType();//设置刷新后的页面状态
			if (edittype != null && edittype.equals(WLTConstants.BILLDATAEDITSTATE_INIT)) {
			} else if (edittype != null && edittype.equals(WLTConstants.BILLDATAEDITSTATE_UPDATE)) {
				bcp.setEditableByEditInit();
				bcp.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
			} else if (edittype != null && edittype.equals(WLTConstants.BILLDATAEDITSTATE_INSERT)) {
				bcp.setEditableByInsertInit();
				bcp.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
			}
		} else if (_parent instanceof BillListPanel) { // 如果是billlistpanel则需要刷新
			//袁江晓 20130313
			//思路：1、先更新查询面板的模板  2、再更新billlist的模板     3、再统一刷新billist的数据，默认为自动加载    这三个是分别独立的	
			BillListPanel billlist = (BillListPanel) _parent;
			String templete = billlist.getTempletVO().getTempletcode();
			//这里需要进行判断，因为有的系统模板不能够加载，所以需要先判断该模板是否存在,为了方便不另外单独搞个函数来处理   20130410   袁江晓
			int pageNo = billlist.getLi_currpage();
			int selectRow = billlist.getSelectedRow();
			if (null != templete && !templete.equals("") && billlist.getStr_realsql() != null && (!("1").equals(billlist.getIsRefreshParent()))) {//最后一个参数表示如果该billist不需要刷新，将isRefreshParent设置为1即可
				billlist.getQuickQueryPanel().removeAll(); // 快速查询 重新加载
				billlist.getQuickQueryPanel().reload(templete);
				billlist.reload(); //billlist重新加载
				/*下面代码是用来刷新按钮的  但是很多是写在代码中的所以这段先去掉	
				billlist.getBillListBtnPanel().removeAllButtons();  //先把之前所有的button去掉重新加载
				WLTButton []buttons=new WLTButton[billlist.getTempletVO().getListcustbtns().length];   
				//先获得保存时候的所有按钮
				for(int i=0;i<buttons.length;i++){
					buttons[i]=new WLTButton(billlist.getTempletVO().getListcustbtns()[i]);
				}
				billlist.insertBatchBillListButton(buttons);
				billlist.repaintBillListButton();
				*/
				billlist.refreshData(); // 自动加载数据
				if (billlist.getPageScrollable() && billlist.getLi_TotalRecordCount() != 0) {//如果只有0条记录则不进行跳转
					billlist.goToPage(pageNo);
				}
				billlist.setSelectedRow(selectRow);
			}
			/*if (billlist.getTempletVO() != null && billlist.getTempletVO().getAutoLoads() != 0) {
				billlist.refreshData(); // 自动加载数据
			}*/
		} else if (_parent instanceof BillQueryPanel) { // 如果右击billquerypanel的右键--编辑整个模板
			BillQueryPanel bqp = (BillQueryPanel) _parent;
			BillListPanel blp = bqp.getBillListPanel();
			String templeteCode = bqp.getTempletVO().getTempletcode(); // 先获得模板编码
			if (blp != null) {//如果是报表则billlistpanel为空
				blp.getQuickQueryPanel().removeAll();
				blp.getQuickQueryPanel().reload(templeteCode);
				blp.reload();
				blp.refreshData(); // 自动加载数据
				/*if (bqp.getTempletVO() != null && bqp.getTempletVO().getAutoLoads() != 0) {
					blp.refreshData(); // 自动加载数据
				}*/
			} else {//如果为报表
				bqp.removeAll();
				bqp.reload(templeteCode);
				bqp.updateUI();
			}
		} else if (_parent instanceof BillTreePanel) {

		} else if (_parent instanceof BillPropPanel) {
		}
	}

	//重置顺序!!!
	private void resetShowOrder() {
		int li_rowcount = listPanel.getRowCount();
		for (int i = 0; i < li_rowcount; i++) {
			if (listPanel.getValueAt(i, "SHOWORDER") != null && Integer.parseInt("" + listPanel.getValueAt(i, "SHOWORDER")) != (i + 1)) {
				listPanel.setValueAt(new StringItemVO("" + (i + 1)), i, "SHOWORDER"); //
				if (!WLTConstants.BILLDATAEDITSTATE_INSERT.equals(listPanel.getRowNumberEditState(i))) {//只有是INIT、UPDATE状态的数据在排序后可以设置为update状态。insert的设置为update就会出现数据进不去的bug。
					listPanel.setRowStatusAs(i, WLTConstants.BILLDATAEDITSTATE_UPDATE); //设置为修改状态.	
				}
			}
		}
	}

	//停止编辑!!!
	private void stopEditing() {
		try {
			if (listPanel.getTable().getCellEditor() != null) {
				listPanel.getTable().getCellEditor().stopCellEditing(); //
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
