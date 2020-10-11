package cn.com.pushworld.salary.ui.dinterface.p010;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.report.cellcompent.ExcelUtil;
import cn.com.infostrategy.ui.sysapp.other.ExcelToDBWKPanel;
import cn.com.pushworld.salary.to.SalaryTBUtil;

/**
 * 绩效模块/工具/系数管理 菜单
 * @author 张营闯 2014-07-11
 * **/
public class BaseManagementWKPanel extends AbstractWorkPanel implements BillListSelectListener, ActionListener {
	private BillListPanel parentpanel;
	private BillListPanel childrenPanel;
	private WLTButton add_btn, edit_btn, delete_btn, view_btn, import_btn;
	private DateCreateDmo dmo;
	private String[] titlename;
	private HashMap titlemap;
	private BillQueryPanel billQueryPanel;
	private WLTSplitPane splitPane;
	private SalaryTBUtil salaryTBUtil = new SalaryTBUtil();
	private WLTButton btn_data_save, btn_data_up, btn_data_down, btn_data_del, btn_data_insert;
	private BillVO old_vo=null;//zzl 记录调整前的数据
	private int row=0;//zzl 记录调整的行数

	@Override
	public void initialize() {
		parentpanel = new BillListPanel("SAL_DATA_BASE_TABLEBASE_CODE1");
		add_btn = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT);
		edit_btn = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT);
		delete_btn = new WLTButton("删除");
		delete_btn.addActionListener(this);
		view_btn = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);
		import_btn = new WLTButton("数据导入");
		import_btn.addActionListener(this);
		parentpanel.addBatchBillListButton(new WLTButton[] { add_btn, edit_btn, delete_btn, view_btn, import_btn });
		parentpanel.repaintBillListButton();
		parentpanel.addBillListSelectListener(this);
		billQueryPanel = parentpanel.getQuickQueryPanel();
		billQueryPanel.addBillQuickActionListener(this);
		childrenPanel = new BillListPanel("SAL_WLTDUAL_CODE1");
		childrenPanel.addBillListSelectListener(this);
		splitPane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, parentpanel, childrenPanel);
		splitPane.setDividerLocation(330);
		btn_data_insert = WLTButton.createButtonByType(WLTButton.LIST_ROWINSERT);
		btn_data_save = new WLTButton("保存",UIUtil.getImage("zt_028.gif"));
		btn_data_del = WLTButton.createButtonByType(WLTButton.LIST_DELETEROW);
		btn_data_up = WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEUP);
		btn_data_down = WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEDOWN);
		btn_data_save.addActionListener(this);
		this.add(splitPane);
		this.setVisible(true);

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == delete_btn) {
			doDelete();
		} else if (e.getSource() == import_btn) {
			doImportDate();
		} else if (e.getSource() == billQueryPanel) {
			parentpanel.QueryData(billQueryPanel.getQuerySQL());
			childrenPanel.clearTable();
		} else if (e.getSource() == btn_data_save) {
			onSave();
		}

	}

	//保存
	private void onSave() {
		BillVO vos[] = childrenPanel.getAllBillVOs();
		Pub_Templet_1_ItemVO itemvos[] = childrenPanel.getTempletItemVOs();//子表
		List sqllist = new ArrayList();
		sqllist.add("delete from " + childrenPanel.getTempletVO().getSavedtablename());
		for (int i = 0; i < vos.length; i++) {
			InsertSQLBuilder insert = new InsertSQLBuilder(childrenPanel.getTempletVO().getSavedtablename());
			for (int j = 0; j < itemvos.length; j++) {
				insert.putFieldValue(itemvos[j].getItemkey(), vos[i].getStringValue(itemvos[j].getItemkey())); //需要处理是否加密处理!!
			}
			insert.putFieldValue("id", (i + 1));
			sqllist.add(insert);
		}
		setAdjust(itemvos);
		try {
			UIUtil.executeBatchByDS(null, sqllist);
			childrenPanel.refreshData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * zzl【2019-7-3】
	 * 增加单价调整记录
	 */
    public void setAdjust(Pub_Templet_1_ItemVO itemvos[]){
    	 BillVO zvo=parentpanel.getSelectedBillVO();    	 
    	 if(zvo==null){
    		 return;
    	 }
    	 try {
	    	 InsertSQLBuilder insert=new InsertSQLBuilder("LEASEPRICE_QUGZ");
	    	 StringBuffer sb=new StringBuffer();//调整前的记录
	    	 StringBuffer sbh=new StringBuffer();//调整后的记录
	    	 for(int i=1;i<itemvos.length;i++){
	    		 sb.append(itemvos[i].getItemname()+"为【"+old_vo.getStringValue(itemvos[i].getItemkey())+"】,");
	    		 sbh.append(itemvos[i].getItemname()+"为【"+childrenPanel.getRealValueAtModel(row-1,itemvos[i].getItemkey())+"】,");
	    	 }
	    	 insert.putFieldValue("ID",UIUtil.getSequenceNextValByDS(null,"S_LEASEPRICE_QUGZ"));
	    	 insert.putFieldValue("ADJUSTQ","单价条目为【"+zvo.getStringValue("basename")+"】,"+sb.toString());
	    	 insert.putFieldValue("USERCODE",ClientEnvironment.getInstance().getLoginUserCode());
	    	 insert.putFieldValue("DEPTID",ClientEnvironment.getInstance().getLoginUserDeptId());
	    	 insert.putFieldValue("DATETIME",UIUtil.getCurrTime());
	    	 insert.putFieldValue("ADJUSTH","单价条目为【"+zvo.getStringValue("basename")+"】,"+sbh.toString());
	
			 UIUtil.executeUpdateByDS(null, insert.getSQL());
		} catch (WLTRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		// TODO Auto-generated method stub
		BillVO billVO = parentpanel.getSelectedBillVO();
		String tablename = billVO.getStringValue("tablename");
		String descr = billVO.getStringValue("descr");
		String titles[] = null;
		titlemap = new HashMap();
		if (descr != null && !descr.equals("")) {
			titles = descr.split(";");
			titlename = new String[titles.length];
			for (int i = 0; i < titles.length; i++) {
				String[] title = titles[i].split("=");
				titlemap.put(title[1], title[0]);
				titlename[i] = title[1];
			}
			dmo = new DateCreateDmo(tablename, titlename, titlemap, false, ClientEnvironment.isAdmin());
			childrenPanel = new BillListPanel(dmo);
			childrenPanel.QueryDataByCondition("1=1");
			childrenPanel.addBillListSelectListener(new BillListSelectListener() {
				
				@Override
				public void onBillListSelectChanged(BillListSelectionEvent event) {
					old_vo=event.getBillListPanel().getSelectedBillVO();
					row=event.getBillListPanel().getRowCount();
				}
			});
		} else {
			childrenPanel = new BillListPanel("SAL_WLTDUAL_CODE1");
		}
		if (ClientEnvironment.isAdmin()) {
			childrenPanel.addBatchBillListButton(new WLTButton[] {  btn_data_insert, btn_data_del, btn_data_up, btn_data_down,btn_data_save });
		}
		childrenPanel.repaintBillListButton();
		splitPane.setRightComponent(childrenPanel);
		splitPane.setDividerLocation(330);

	}

	private void doDelete() {
		BillVO billVO = parentpanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(parentpanel);
			return;
		}
		if (MessageBox.confirm(parentpanel, "您确定要删除该数据吗？")) {
			try {
				UIUtil.executeUpdateByDS(null, "drop table " + billVO.getStringValue("tablename"));
			} catch (Exception e) {
			}
			parentpanel.doDelete(true);
			childrenPanel.clearTable();
		}

	}

	private void doImportDate() {
		BillVO billVO = parentpanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(parentpanel);
			return;
		}

		String tablename = billVO.getStringValue("tablename");
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileFilter filter = new FileNameExtensionFilter("Microsoft Office Excel 工作表", "xls", "xlsx");
		chooser.setFileFilter(filter);
		int flag = chooser.showOpenDialog(this);
		if (flag == 1 || chooser.getSelectedFile() == null) {
			return;
		}
		String str_path = chooser.getSelectedFile().getAbsolutePath();
		if (str_path == null) {
			return;
		}
		try {
			UIUtil.getHashVoArrayByDS(null, "select * from " + tablename + " where 1=2");
			if (MessageBox.confirm(parentpanel, "此操作将清空原来的数据,确定执行此操作吗？")) {
				UIUtil.executeUpdateByDS(null, "drop table " + tablename);
				chackdate(tablename, str_path);
			} else {
				return;
			}
		} catch (Exception e1) {
			chackdate(tablename, str_path);
		}

	}

	private void chackdate(String tablename, String str_path) {
		ExcelUtil eexcel= new ExcelUtil();
		eexcel.setDecimals(12);
		String[][] excelFileData =eexcel.getExcelFileData(str_path, 0);
		ExcelToDBWKPanel toDBWKPanel = new ExcelToDBWKPanel();
		toDBWKPanel.getExcelFileData(str_path);

		try {
			importDate(tablename, excelFileData);
			MessageBox.show(this, "导入成功!");
			dmo = new DateCreateDmo(tablename, titlename, titlemap, false, ClientEnvironment.isAdmin());
			parentpanel.refreshCurrSelectedRow();
			childrenPanel = new BillListPanel(dmo);
			childrenPanel.QueryDataByCondition("1=1");
			splitPane.setRightComponent(childrenPanel);
			if (ClientEnvironment.isAdmin()) {
				childrenPanel.addBatchBillListButton(new WLTButton[] {  btn_data_insert, btn_data_del,btn_data_up, btn_data_down, btn_data_save });
			}
			childrenPanel.repaintBillListButton();
			splitPane.setDividerLocation(330);
		} catch (Exception e) {
			MessageBox.show(this, "导入失败");
		}
	}

	private void importDate(String tablename, String[][] excelFileData) throws Exception {
		ArrayList sqllist = new ArrayList();
		ArrayList createlist = new ArrayList();
		titlemap = new HashMap();
		StringBuilder descr = new StringBuilder();
		if (excelFileData.length > 1) {
			titlename = new String[excelFileData[0].length];
			if (excelFileData[0].length > 0) {
				for (int i = 1; i < excelFileData.length; i++) {
					InsertSQLBuilder builder = new InsertSQLBuilder(tablename);
					builder.putFieldValue("id", i);
					int count_null = 0;
					for (int j = 0; j < excelFileData[i].length; j++) {
						titlename[j] = excelFileData[0][j];
						String code = salaryTBUtil.convertIntColToEn(j + 1,false);
						titlemap.put(titlename[j], code);
						if (i == 1) {
							descr.append(code + "=" + titlename[j] + ";");
						}
						String value = excelFileData[i][j];
						if (value != null && !value.trim().equals("")) {
							if (value.contains(".") && titlename[j].contains("码")) {
								String[] item = TBUtil.getTBUtil().split(value, ".");
								if (item.length > 1 && Integer.parseInt(item[1]) != 5) {
									value = item[0];
								}
							}
							builder.putFieldValue(code, value.trim().replaceAll(" ", ""));
						} else {
							count_null++;
						}
					}
					if (count_null == excelFileData[i].length) {
						continue;
					}
					sqllist.add(builder.getSQL());
				}
				String updatesql = "update sal_data_base_tablebase set descr ='" + descr.toString() + "' where tablename ='" + tablename + "'";
				sqllist.add(updatesql);
				createlist.add(getCreateTableSql(tablename, titlename, titlemap));
				UIUtil.executeBatchByDS(null, createlist);
				UIUtil.executeBatchByDS(null, sqllist);
			}
		}
	}

	private String getCreateTableSql(String tablename, String titlename[], HashMap titlemap) {
		String str_dbtype = ClientEnvironment.getInstance().getDefaultDataSourceType(); // 数据源类型!关键
		StringBuffer sb_sql = new StringBuffer();
		sb_sql.append("create table " + tablename + "("); //
		sb_sql.append(" id decimal(22),");
		List indexItem = new ArrayList();
		StringBuffer msg = new StringBuffer();
		for (int i = 0; i < titlename.length; i++) {
			sb_sql.append(titlemap.get(titlename[i]) + " varchar(200)"); //
			if (i != titlename.length - 1) {
				sb_sql.append(","); //
			}
		}
		sb_sql.append(")"); //
		if (str_dbtype.equalsIgnoreCase("MYSQL")) {
			sb_sql.append(" DEFAULT CHARSET = GBK"); //
		}
		if (str_dbtype.equalsIgnoreCase("DB2")) {
			sb_sql.append(" IN PUSHSPACE"); //
		}

		String str_createsql = sb_sql.toString(); //
		if (str_dbtype.equalsIgnoreCase("DB2")) {
			str_createsql = str_createsql.toUpperCase(); //
		}
		return str_createsql;
	}

}
