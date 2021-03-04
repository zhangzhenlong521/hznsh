package cn.com.infostrategy.ui.sysapp.other;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.to.sysapp.other.ExcelImportBean;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;
import cn.com.infostrategy.ui.mdata.cardcomp.RefDialog_Date;
import cn.com.infostrategy.ui.report.cellcompent.ExcelUtil;

/**
 * ����EXCEL���������ݿ� �����/2013-06-27��
 */

public class ExcelToDBWKPanel extends AbstractWorkPanel implements BillListHtmlHrefListener, ActionListener {
	private WLTButton btn_insert, btn_edit, btn_delete, btn_upload, btn_import, btn_onlook_check, btn_exit, btn_delete_his, btn_exit_his, btn_exit_look;
	private BillListPanel billListPanel = null;
	private BillListPanel billListPanel_his = null;
	private BillListPanel importListPanel = null;
	private HashMap[] exceldatas = null;
	private ArrayList al_excel = new ArrayList();
	private BillCardPanel panel_time = null;
	private String updaterate = "";
	private BillDialog dialog = null;
	private BillDialog dialog_look = null;

	private boolean fag = TBUtil.getTBUtil().getSysOptionBooleanValue("�����ϴ��Ƿ���ʾѡ���������", false);//zzl[2018-10-26]  ��ǰֻ��ѡ���£�����Ҫ����������
	private boolean tablefag = TBUtil.getTBUtil().getSysOptionBooleanValue("�����ϴ��Ƿ����ͷ", false);//zzl[2018-10-26]  �����ϴ��Ƿ����ͷ
	private boolean zdtable = TBUtil.getTBUtil().getSysOptionBooleanValue("�����ϴ��Ƿ���ָ������", false);//zzl[2018-10-26]  �����ϴ��Ƿ���ָ����
	private String count = TBUtil.getTBUtil().getSysOptionStringValue("Excel����С������λ��", "2");//zzl[2018-10-26]  �����ϴ�������С��λ

	public void initialize() {
		billListPanel = new BillListPanel("EXCEL_TAB_CODE1");

		btn_insert = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT);
		btn_edit = new WLTButton("�޸�");
		btn_edit.addActionListener(this);
		btn_delete = new WLTButton("ɾ��");
		btn_delete.addActionListener(this);
		btn_upload = new WLTButton("�ϴ�����");
		btn_upload.addActionListener(this);
		//2020��7��12��13:12:39  fj  �ǹ���ԱȨ�޿�������������ť
		if(!ClientEnvironment.isAdmin()){
			btn_insert.setVisible(false);
			btn_edit.setVisible(false);
			btn_delete.setVisible(false);
		}

		billListPanel.addBatchBillListButton(new WLTButton[] { btn_insert, btn_edit, btn_delete, btn_upload });
		billListPanel.repaintBillListButton();
		billListPanel.addBillListHtmlHrefListener(this);

		this.add(billListPanel, BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_edit) {
			onBillListPopEdit();
			billListPanel.refreshCurrSelectedRow();
		} else if (e.getSource() == btn_edit) {
			onBillListPopEdit();
			billListPanel.refreshCurrSelectedRow();
		} else if (e.getSource() == btn_delete) {
			onDelete();
		} else if (e.getSource() == btn_delete_his) {
			onDelete_his();
		} else if (e.getSource() == btn_upload) {
			onUpload();
		} else if (e.getSource() == btn_import) {
			HashMap hm_excelname = new HashMap();

			try {
				HashVO[] hvos = UIUtil.getHashVoArrayByDS(null, "select excelname,updaterate from excel_tab");
				for (int i = 0; i < hvos.length; i++) {
					String excelname = hvos[i].getStringValue("excelname", "");
					String updaterate = hvos[i].getStringValue("updaterate", "");
					if (!("".equals(excelname))) {
						hm_excelname.put(excelname, updaterate);
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			String error = "";
			String error_rate = "";
			for (int s = 0; s < exceldatas.length; s++) {
				String sheetname = exceldatas[s].get("sheetname") + "";
				if (!hm_excelname.containsKey(sheetname)) {
					error += "[" + sheetname + "]";
				}

				if (hm_excelname.containsKey(sheetname)) {
					String rate = (String) hm_excelname.get(sheetname);
					if (!rate.equals(updaterate)) {
						error_rate += "[" + sheetname + "]";
					}
				}
			}

			if (!error.equals("")) {
				MessageBox.show(this, "Excel��sheet��Ϊ" + error + "�����ݲ�����!���½����ٵ���!");
				return;
			}

			if (!error_rate.equals("")) {
				MessageBox.show(this, "Excel��sheet��Ϊ" + error_rate + "�����ݵĸ���Ƶ�ʲ���[" + updaterate + "]!�����ٵ���!");
				return;
			}
			if (!fag) {
				BillVO billVO = panel_time.getBillVO();
				String year = billVO.getStringValue("year");
				String month = billVO.getStringValue("month");

				if (updaterate.equals("���")) {
					if (year == null || year.equals("") || year.equals("---��ѡ��---")) {
						MessageBox.show(this, "��ѡ�����!");
						return;
					}
				} else {
					if (year == null || year.equals("") || month == null || month.equals("") || year.equals("---��ѡ��---") || month.equals("---��ѡ��---")) {
						MessageBox.show(this, "��ѡ����ݺ��·�!");
						return;
					}
				}
			}

			//ǰ��У��û�������������У�����ݸ�ʽ�����/2019-06-12��
			boolean canImport = oncheck(importListPanel, true);
			if (!canImport) {
				MessageBox.show(importListPanel, "����У�鷢���д���������޸���ȷ�ٵ��롣");
				return;
			}

			if (MessageBox.confirm(this, "��ȷ��Ҫ�����б���������?")) {
				new SplashWindow(this, new AbstractAction() {
					public void actionPerformed(ActionEvent e) {
						doImport((SplashWindow) e.getSource());
					}
				}, 366, 366).setSysMsgVisible(false);
			}

			dialog.dispose();
		} else if (e.getSource() == btn_exit) {
			dialog.dispose();
		} else if (e.getSource() == btn_exit_his) {
			dialog.dispose();
		} else if (e.getSource() == btn_exit_look) {
			dialog_look.dispose();
		} else if (e.getSource() == btn_onlook_check) {
			oncheck(billListPanel_show, false);
		}
	}

	/**
	 * ʵ��У�鹦�ܡ����/2019-06-10��
	 */
	private boolean oncheck(BillListPanel _listPanel, boolean _isImport) {
		BillVO vos[] = _listPanel.getAllBillVOs();
		HashVO hvos[] = new HashVO[vos.length];
		for (int i = 0; i < hvos.length; i++) {
			hvos[i] = vos[i].convertToHashVO();
		}
		try {
			FrameWorkMetaDataServiceIfc ifc = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class);
			HashMap rtvalue = ifc.checkExcelDataByPolicy(hvos, billListPanel.getSelectedBillVO().getPkValue());
			Boolean checkResult = (Boolean) rtvalue.get("RESULT");
			if (checkResult) {
				if (!_isImport) {
					MessageBox.show(_listPanel, "У��ɹ�!");
				}
				return true;
			}
			List error = (List) rtvalue.get("ERROR");
			List warn = (List) rtvalue.get("WARN");
			WLTTabbedPane tabpane = new WLTTabbedPane();
			List hashvolist = new ArrayList();
			for (int i = 0; i < error.size(); i++) {
				ExcelImportBean bean = (ExcelImportBean) error.get(i);
				HashVO vo = new HashVO();
				vo.setAttributeValue("position", bean.getCol() + bean.getRow());
				vo.setAttributeValue("descr", bean.getMsg());
				hashvolist.add(vo);
			}
			PUB_TMO tmo = new PUB_TMO();
			if (hashvolist.size() > 0) {
				BillListPanel listpanel = new BillListPanel(tmo);
				listpanel.queryDataByHashVOs((HashVO[]) hashvolist.toArray(new HashVO[0]));
				tabpane.addTab("����", listpanel);
			}

			hashvolist.clear();
			for (int i = 0; i < warn.size(); i++) {
				ExcelImportBean bean = (ExcelImportBean) warn.get(i);
				HashVO vo = new HashVO();
				vo.setAttributeValue("position", bean.getCol() + bean.getRow());
				vo.setAttributeValue("descr", bean.getMsg());
				hashvolist.add(vo);
			}
			if (hashvolist.size() > 0) {
				BillListPanel listpanel = new BillListPanel(tmo);
				listpanel.queryDataByHashVOs((HashVO[]) hashvolist.toArray(new HashVO[0]));
				tabpane.addTab("����(�ɺ���)", listpanel);
			}
			BillDialog dialog = new BillDialog(_listPanel, "����У�����޴�����ſɵ���", 800, 600);
			dialog.getContentPane().add(tabpane);
			dialog.locationToCenterPosition();
			dialog.setVisible(true);
			if (error.size() == 0) {
				return true;//���û�д������Ϳɵ���
			}
			return false;
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void setTableHeader(Boolean header) {
		this.tablefag = header;
	}

	private void onDelete() {
		int li_selRow = billListPanel.getSelectedRow();
		if (li_selRow < 0) {
			MessageBox.showSelectOne(billListPanel);
			return;
		}

		ArrayList list_sqls = new ArrayList();

		BillVO billVO = billListPanel.getSelectedBillVO();
		String id = billVO.getStringValue("id");
		String tablename = billVO.getStringValue("tablename");
		String del_sql = "delete from excel_tab where id='" + id + "'";
		list_sqls.add(del_sql);

		if (tablename != null && !tablename.equals("")) {
			String drop_sql = "drop table " + tablename + ""; // ��Ҳɾ��
			list_sqls.add(drop_sql);
		}

		try {
			if (MessageBox.confirm(this, "��ȷ��Ҫɾ��������¼?")) {
				UIUtil.executeBatchByDS(null, list_sqls);
				billListPanel.refreshData();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void onUpload() {
		int li_selRow = billListPanel.getSelectedRow();
		if (li_selRow < 0) {
			MessageBox.showSelectOne(billListPanel);
			return;
		}

		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setDialogTitle("��ѡ��һ��Excel�ļ�");
		chooser.setApproveButtonText("ѡ��");

		FileFilter filter = new FileNameExtensionFilter("Microsoft Office Excel ������", "xls", "xlsx");
		chooser.setFileFilter(filter);
		int flag = chooser.showOpenDialog(this);
		if (flag != JFileChooser.APPROVE_OPTION || chooser.getSelectedFile() == null) {
			return;
		}
		final String str_path = chooser.getSelectedFile().getAbsolutePath();

		if (!(str_path.toLowerCase().endsWith(".xls") || str_path.toLowerCase().endsWith(".xlsx"))) {
			MessageBox.show(this, "��ѡ��һ��Excel�ļ�!");
			return;
		}
		String[][] data = new ExcelUtil().getExcelFileData(str_path);
		new SplashWindow(this, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				exceldatas = getExcelFileData(str_path);
			}
		}, 366, 366).setSysMsgVisible(false);

		if (exceldatas == null || exceldatas.length <= 0 ) {
			MessageBox.show(this, "Excel����Ϊ��");
			return;
		}

		al_excel = gethavsAL(exceldatas);

		if (al_excel == null || al_excel.size() <= 0 ) {
			MessageBox.show(this, "Excel����Ϊ��");
			return;
		}

		BillVO billVO = billListPanel.getSelectedBillVO();
		updaterate = billVO.getStringValue("updaterate");
		panel_time = getTimePanel(updaterate);
		panel_time.setAutoscrolls(false);
		dialog = new BillDialog(this, "����Ԥ��-" + str_path.substring(str_path.lastIndexOf("\\") + 1, str_path.length()), 800, 600);
		dialog.getContentPane().setLayout(new BorderLayout());
		if (!fag) {
			dialog.getContentPane().add(panel_time, "North");
		}
		dialog.getContentPane().add(getDataTabPanel(), "Center");
		dialog.getContentPane().add(getBtnPanel(), "South");
		dialog.setVisible(true);

	}

	private void doImport(SplashWindow sw) {
		try {
			BillVO vo = billListPanel.getSelectedBillVO();
			ArrayList list_sqls = new ArrayList();
			ArrayList list_del_sqls = new ArrayList();
			StringBuffer sb_del = new StringBuffer(); // �����ظ���ʾ
			sb_del.append("");
			StringBuffer sb_add = new StringBuffer(); // �����ظ���ʾ
			sb_add.append("");
			String year = null;
			String month = null;
			String specifictime = null;
			if (fag) {
				RefDialog_Date d = new RefDialog_Date(dialog, "", new RefItemVO(), null);
				d.initialize();
				d.setVisible(true);
				RefItemVO ivo = d.getReturnRefItemVO();
				specifictime = ivo.getId();
			} else {
				BillVO billVO = panel_time.getBillVO();
				year = billVO.getStringValue("year");
				month = billVO.getStringValue("month");

				if (year != null && year.equals("---��ѡ��---")) {
					year = "";
				}
				if (month != null && month.equals("---��ѡ��---")) {
					month = "";
				}
			}

			/*			BillVO billVO_ = billListPanel.getSelectedBillVO();
						String str_id = billVO_.getStringValue("id");
						String str_excelname = billVO_.getStringValue("excelname");
						String table_name = billVO_.getStringValue("tablename");*/

			String creattime = UIUtil.getServerCurrTime();

			String del_sql = "";
			int maxid = getMaxID();
			for (int s = 0; s < exceldatas.length; s++) {
				//for (int s = 0; s < 1; s++) { // ֻȡ��һ��sheet
				HashMap exceldata = exceldatas[s];

				int rownum = (Integer) exceldata.get("rownum");
				int colnum = (Integer) exceldata.get("colnum");
				if (rownum <= 0 || colnum <= 0) {
					continue; // ���ڿ�sheet ����
				}

				String[] strs = getColname(colnum); // excel��ͷ��ĸ��
				HashMap hm_fl = getFiledLength(exceldata); // excelÿ�����ݳ���

				//String filename = exceldata.get("filename")+"";
				String sheetname = exceldata.get("sheetname") + "";
				String table_name = getTablename(sheetname); //excel sheet��Ӧ�ı���

				// String table_name = getTablename(str_excelname);
				if (table_name == null || table_name.equals("")) {
					if (zdtable) {//zzl[2018-11-14] ָ����������excel
						table_name = vo.getStringValue("code");
						UpdateSQLBuilder sb_excel_tab = new UpdateSQLBuilder("excel_tab", "excelname='" + sheetname + "'");
						sb_excel_tab.putFieldValue("tablename", table_name);
						sb_excel_tab.putFieldValue("updatetime", creattime);
						list_sqls.add(sb_excel_tab.getSQL());

						// �����±�
						String table_sql = creatTable(strs, hm_fl, null, table_name); // �������
						list_sqls.add(table_sql);
					} else {
						String new_id = "" + maxid;
						maxid++;
						// ����excel_tab�б�����
						UpdateSQLBuilder sb_excel_tab = new UpdateSQLBuilder("excel_tab", "excelname='" + sheetname + "'");
						sb_excel_tab.putFieldValue("tablename", "excel_tab_" + new_id);
						sb_excel_tab.putFieldValue("updatetime", creattime);
						list_sqls.add(sb_excel_tab.getSQL());

						// �����±�
						String table_sql = creatTable(strs, hm_fl, new_id, null); // �������
						list_sqls.add(table_sql);

						table_name = "excel_tab_" + new_id;
					}
				} else {
					// �ж������Ƿ����
					String str_where = getWhere(year, month, specifictime);
					if (!str_where.equals("")) {
						String sql = "select count(*) cou from " + table_name + " where " + str_where;
						HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, sql);
						if (hvs != null && hvs.length > 0) {
							int cou = hvs[0].getIntegerValue("cou", 0);
							if (cou > 0) {
								if (fag) {
									sb_del.append("����[" + sheetname + "]" + getStr(year, month, specifictime) + "�������Ѵ���,�Ƿ񸲸�?\r\n");
									//sb_add.append("�ñ���" + getStr(year, month) + "�������Ѵ��ڣ��Ƿ��½�����?\r\n");
									del_sql = "delete from " + table_name + " where " + str_where;
								} else {
									sb_del.append("����[" + sheetname + "]" + getStr(year, month, specifictime) + "�������Ѵ���,�Ƿ񸲸�?\r\n");
									//sb_add.append("�ñ���" + getStr(year, month) + "�������Ѵ��ڣ��Ƿ��½�����?\r\n");
									del_sql = "delete from " + table_name + " where " + str_where;
								}

								list_del_sqls.add(del_sql);
							}
						}
					}

					// �������ݿ��ֶμ�����
					HashMap hm = getTableCol(table_name); // ���ݿ��ֶμ�����
					final FrameWorkMetaDataServiceIfc service = UIUtil.getMetaDataService();
					for (int i = 0; i < strs.length; i++) {
						int l = (Integer) hm_fl.get(strs[i]);
						if (hm.containsKey(strs[i])) {
							int ll = (Integer) hm.get(strs[i]);
							if (l > ll) {
								String upd_sql = getAlterModifySQL(table_name, strs[i], "varchar(" + l + ")", null); // �ֶθ������
								list_sqls.add(upd_sql);
							}
						} else {
							String add_sql = service.getAddColumnSql(null, table_name, strs[i], "varchar", "" + l); // ׷���ֶ�
							if (add_sql.endsWith(";")) {
								list_sqls.add(add_sql.substring(0, add_sql.length() - 1));
							} else {
								list_sqls.add(add_sql);
							}
						}
					}

					// ����excel_tab�б�����
					UpdateSQLBuilder sb_excel_tab = new UpdateSQLBuilder("excel_tab", "excelname='" + sheetname + "'");
					sb_excel_tab.putFieldValue("updatetime", creattime);
					list_sqls.add(sb_excel_tab.getSQL());
				}

				// ��������sql
				for (int i = 0; i < rownum; i++) {
					InsertSQLBuilder sb_excel_data = new InsertSQLBuilder(table_name);
					sb_excel_data.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_" + table_name.toUpperCase()));
					for (int j = 0; j < colnum; j++) {
						String data = "" + exceldata.get(i + "_" + j);
						if (!(data.equals("") || data.equals("null"))) {
							sb_excel_data.putFieldValue(strs[j], data);
						}
						if (fag) {
							if (specifictime != null && !specifictime.equals("")) {
								sb_excel_data.putFieldValue("specifictime", specifictime);
							}
						} else {
							if (year != null && !year.equals("")) {
								sb_excel_data.putFieldValue("year", year);
							}
							if (month != null && !month.equals("")) {
								sb_excel_data.putFieldValue("month", month);
							}
						}
						sb_excel_data.putFieldValue("creattime", creattime);
					}
					list_sqls.add(sb_excel_data.getSQL());
				}
			}

			if (!sb_del.toString().equals("")) {
				sw.closeSplashWindow();
				if (MessageBox.confirm(this, sb_del.toString())) {
					for (int i = 0; i < list_sqls.size(); i++) {
						list_del_sqls.add(list_sqls.get(i));
					}
					UIUtil.executeBatchByDS(null, list_del_sqls); // ����
					try {
						doIFC("���ǵ���ɹ�", year, month);
					} catch (Exception e) {
						MessageBox.show(this, "���ݵ���ɹ�!��չ��ִ��ʧ��!");
					}
					MessageBox.show(this, "���ݵ���ɹ�!");
					//billListPanel.refreshCurrSelectedRow();
					billListPanel.refreshData();
				}
				/*
				 * }else{ if(MessageBox.confirm(this, sb_add.toString())){
				 * UIUtil.executeBatchByDS(null, list_sqls); //�½�����
				 * MessageBox.show(this, "���ݵ���ɹ�!"); } }
				 */
			} else {
				UIUtil.executeBatchByDS(null, list_sqls);
				try {
					doIFC("��������ɹ�", year, month);
				} catch (Exception e) {
					MessageBox.show(this, "���ݵ���ɹ�!��չ��ִ��ʧ��!");
				}
				sw.closeSplashWindow();
				MessageBox.show(this, "���ݵ���ɹ�!");
				//billListPanel.refreshCurrSelectedRow();
				billListPanel.refreshData();
			}
		} catch (Exception e) {
			sw.closeSplashWindow();
			MessageBox.show(this, "���ݵ���ʧ��!");
			e.printStackTrace();
		}
	}

	private void doIFC(String state, String year, String month) throws Exception {
		BillVO billVO_ = billListPanel.getSelectedBillVO();
		String tablename[] = UIUtil.getStringArrayFirstColByDS(null, "select tablename from excel_tab where id=" + billVO_.getStringValue("id"));
		String classname = billVO_.getStringValue("classname");
		if (classname != null && !classname.equals("")) {
			Object obj = Class.forName(classname).newInstance();
			ExcelToDBIfc ifc = (ExcelToDBIfc) obj;
			ifc.Action(state, year, month, tablename[0]);
		}
	}

	// �ֶθ������
	private String getAlterModifySQL(String tableName, String key, String typeL, String _dbtype) {
		if (_dbtype == null) {
			_dbtype = ClientEnvironment.getInstance().getDefaultDataSourceType();
		}
		StringBuffer sb_sql = new StringBuffer();
		if ("sqlserver".equalsIgnoreCase(_dbtype)) {
			sb_sql.append(" alter table " + tableName + " alter column  " + key + " " + typeL);
		} else {
			sb_sql.append(" alter table " + tableName + " modify  " + key + " " + typeL);
		}
		return sb_sql.toString();
	}

	// ��ȡ���ݿ��ֶμ�����
	private HashMap getTableCol(String _table_name) {
		HashMap hm = new HashMap();
		try {
			TableDataStruct struct = UIUtil.getTableDataStructByDS(null, "select * from " + _table_name + " where 1=2");
			String[] headnames = struct.getHeaderName();
			int[] headerLength = struct.getHeaderLength();
			for (int i = 0; i < headnames.length; i++) {
				hm.put(headnames[i], headerLength[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hm;
	}

	// ��ȡ�����մ��ظ���ʾ
	private String getStr(String year, String month, String specifictime) {
		String str = "";
		if (year != null && !year.equals("")) {
			str += "[" + year + "]��";
		}
		if (month != null && !month.equals("")) {
			str += "[" + month + "]��";
		}
		if (specifictime != null && !specifictime.equals("")) {
			str += "[" + specifictime + "]";
		}

		return str;
	}

	// ��ȡ�����մ��ж�����
	private String getWhere(String year, String month, String specifictime) {
		String sql_where = "";
		if (year != null && !year.equals("")) {
			sql_where += " and year='" + year + "' ";
		}
		if (month != null && !month.equals("")) {
			sql_where += " and month='" + month + "' ";
		}
		if (fag) {
			if (specifictime != null && !specifictime.equals("")) {
				sql_where += " and specifictime='" + specifictime + "' ";
			}
		}

		if (!sql_where.equals("")) {
			sql_where = sql_where.substring(4, sql_where.length());
		}

		return sql_where;
	}

	// �������
	private String creatTable(String[] strs, HashMap hm, String seq, String tablename) {
		StringBuffer sb = new StringBuffer();
		String dbtype = ClientEnvironment.getInstance().getDefaultDataSourceType();
		if (zdtable) {
			sb.append("create table " + tablename + " (");
		} else {
			sb.append("create table excel_tab_" + seq + "(");
		}
		if ("Oracle".equalsIgnoreCase(dbtype)) {
			sb.append("id number primary key, ");
		} else {
			sb.append("id int primary key, ");
		}

		sb.append("year varchar(20), ");
		sb.append("month varchar(20), ");
		sb.append("creattime varchar(20), ");
		if (fag) {
			sb.append("specifictime varchar(20), ");
		}
		for (int i = 0; i < strs.length; i++) {
			String l = "" + hm.get(strs[i]);
			if ((i + 1) == strs.length) {
				sb.append(strs[i] + " varchar(" + l + ") ");
			} else {
				sb.append(strs[i] + " varchar(" + l + "), ");
			}
		}
		sb.append(")");

		return sb.toString();
	}

	// ��ȡexcel sheet��Ӧ�ı���
	private String getTablename(String _excelname) {
		String table_name = "";
		try {
			String sql = "select tablename from excel_tab where excelname='" + _excelname + "'";
			HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, sql);
			if (hvs != null && hvs.length > 0) {
				table_name = hvs[0].getStringValue("tablename", "");
			}
		} catch (Exception e) {
		}
		return table_name;
	}

	// ��ȡexcel sheet��Ӧ�ı���
	private int getMaxID() {
		String table_name = "";
		int id = 0;
		try {
			String sql = "select tablename from excel_tab where tablename is not null order by id desc";
			HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, sql);
			if (hvs != null && hvs.length > 0) {
				for (int i = 0; i < hvs.length; i++) {
					table_name = hvs[i].getStringValue("tablename", "");
					if (!table_name.equals("")) {
						int temp_id = Integer.parseInt(table_name.substring(table_name.lastIndexOf("_") + 1, table_name.length()));
						if (temp_id > id) {
							id = temp_id;
						}
					}
				}
			}
		} catch (Exception e) {
		}

		/*		String id = "0";
				if (!table_name.equals("")) {
					id = table_name.substring(table_name.lastIndexOf("_") + 1, table_name.length());
				}*/

		return id + 1;
	}

	// ����Ԥ��ҳ��
	private JPanel getBtnPanel() {
		btn_import = new WLTButton("����");
		btn_exit = new WLTButton("ȡ��");
		btn_import.addActionListener(this);
		btn_exit.addActionListener(this);
		WLTPanel btnPanel = new WLTPanel(1, new FlowLayout(1));
		btnPanel.add(btn_import);
		btnPanel.add(btn_exit);
		return btnPanel;
	}

	// ����tab
	private JTabbedPane getDataTabPanel() {
		JTabbedPane tabbedPane = new JTabbedPane();

		for (int i = 0; i < al_excel.size(); i++) {
			HashVO[] hvos = (HashVO[]) al_excel.get(i);
			String[][] strs_header = new String[hvos[0].getKeys().length][2];
			for (int j = 0; j < hvos[0].getKeys().length; j++) {
				strs_header[j] = new String[] { getColumnName2(j), "85" };
			}
			BillListPanel billListPanel = new BillListPanel(new DefaultTMO("", strs_header));
			billListPanel.putValue(hvos);
			if (i == 0) {
				importListPanel = billListPanel;
			}
			tabbedPane.addTab("" + exceldatas[i].get("sheetname"), billListPanel);
		}
		return tabbedPane;
	}

	// ����ѡ��ҳ��
	private BillCardPanel getTimePanel(String str) {
		BillCardPanel cardPanel = new BillCardPanel("EXCEL_TAB_CODE1");
		Pub_Templet_1VO templetVO = cardPanel.getTempletVO();
		templetVO.setTempletname("��������");
		templetVO.getItemVo("year").setCardisshowable(true);
		if (str.equals("�¶�")) {
			templetVO.getItemVo("month").setCardisshowable(true);
		}

		BillCardPanel cardPanel_new = new BillCardPanel(templetVO);
		cardPanel_new.setEditableByEditInit();
		cardPanel_new.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
		cardPanel_new.setVisiable("excelname", false);
		cardPanel_new.setVisiable("updaterate", false);
		cardPanel_new.setVisiable("classname", false);

		String currDate = UIUtil.getCurrDate();
		cardPanel_new.setRealValueAt("year", currDate.substring(0, 4));
		if (str.equals("�¶�")) {
			cardPanel_new.setRealValueAt("month", "" + currDate.substring(currDate.indexOf("-") + 1, currDate.lastIndexOf("-")));
		}

		return cardPanel_new;
	}

	// ��excel����sheetֵ ת��Ϊ hvos��
	private ArrayList gethavsAL(HashMap[] hms) {
		ArrayList al_excel = new ArrayList();
		for (int s = 0; s < hms.length; s++) {
			HashMap exceldata = hms[s];
			int rownum = (Integer) exceldata.get("rownum");
			int colnum = (Integer) exceldata.get("colnum");
			String[] strs = getColname(colnum);
			HashVO[] hvos = new HashVO[rownum];
			for (int i = 0; i < rownum; i++) {
				hvos[i] = new HashVO();
				for (int j = 0; j < colnum; j++) {
					String data = "" + exceldata.get(i + "_" + j);
					if (data.equals("null")) {
						data = "";
					}
					hvos[i].setAttributeValue(strs[j], data);
				}
			}

			if (hvos != null && hvos.length > 0) {
				al_excel.add(hvos);
			}
		}

		return al_excel;
	}

	// ��ȡexcelÿ�����ݳ���
	private HashMap getFiledLength(HashMap exceldata) {
		HashMap filed = new HashMap();

		int rownum = (Integer) exceldata.get("rownum");
		int colnum = (Integer) exceldata.get("colnum");
		String[] strs = getColname(colnum);
		for (int i = 0; i < rownum; i++) {
			if (i == 0) {
				for (int j = 0; j < colnum; j++) {
					filed.put(strs[j], 50);
				}
			}
			for (int j = 0; j < colnum; j++) {
				String excel_value = "" + exceldata.get(i + "_" + j);
				int l = (Integer) filed.get(strs[j]);

				int length = 0;
				try {
					length = excel_value.toString().getBytes("GBK").length;
				} catch (Exception e) {
					length = new TBUtil().getStrUnicodeLength(excel_value.toString());
				}

				if (length > l) {
					filed.put(strs[j], length);
				}
			}
		}
		return filed;
	}

	// ��ȡexcel����sheetֵ
	public HashMap[] getExcelFileData(String _filename) {
		if (_filename.endsWith("xls")) {
			return getExcelFileData_xls(_filename);
		}
		return getExcelFileData_xlsx(_filename);
	}

	// ��ȡexcel����sheetֵ xls
	private HashMap[] getExcelFileData_xls(String _filename) {
		HashMap[] contents = null;
		FileInputStream in = null;

		try {
			in = new FileInputStream(_filename);
			POIFSFileSystem fs = new POIFSFileSystem(in);
			HSSFWorkbook wb = new HSSFWorkbook(fs);

			int sheetnum = wb.getNumberOfSheets();
			contents = new HashMap[sheetnum];

			for (int s = 0; s < sheetnum; s++) {
				HashMap content = new HashMap();
				HSSFSheet sheet = wb.getSheetAt(s);

				int li_lastrow = sheet.getLastRowNum() + 1;
				int li_rowmax = 0;
				int li_colmax = 0;
				int count = 0;
				if (tablefag) {//zzl[2018-11-14]�����Ƿ����ͷ����
					count = 1;
				}
				for (int i = count; i < li_lastrow; i++) {
					HSSFRow row = sheet.getRow(i);
					if (row != null) {
						int li_lastcol = row.getLastCellNum();
						for (int j = 0; j < li_lastcol; j++) {
							HSSFCell cell = row.getCell((int) j);
							if (cell != null) {

							}//2020��7��12��13:05:41  fj  ��ӷǿ��жϣ���������пո�Ļ��ᱨ��ָ��
							else if(cell == null){
								cell = row.createCell(j);
								cell = row.getCell(j);
							}

							String str_value = getCellValue_xls(cell, _filename, s, i, j);
							if (str_value != null && !str_value.equals("")) {

							}//zzl[2018-12-6] ȥ����Ϊ�յ��ж�
							if (tablefag) {//zzl[2018-11-14]�����Ƿ����ͷ����
								content.put(i - 1 + "_" + j, str_value);
								if (i >= li_rowmax) {
									li_rowmax = i - 1;
								}
							} else {
								content.put(i + "_" + j, str_value);
								if (i >= li_rowmax) {
									li_rowmax = i;
								}
							}
							if (j >= li_colmax) {
								li_colmax = j;
							}

						}
					}
				}
				content.put("rownum", li_rowmax + 1);
				content.put("colnum", li_colmax + 1);
				content.put("sheetindex", s);
				content.put("sheetname", sheet.getSheetName());
				content.put("filename", _filename.substring(_filename.lastIndexOf("\\") + 1, _filename.lastIndexOf(".")));
				contents[s] = content;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return contents;
	}

	// ��ȡexcel����sheetֵ xlsx
	private HashMap[] getExcelFileData_xlsx(String _filename) {
		HashMap[] contents = null;
		FileInputStream in = null;

		try {
			in = new FileInputStream(_filename);
			XSSFWorkbook xwb = new XSSFWorkbook(in);

			int sheetnum = xwb.getNumberOfSheets();
			contents = new HashMap[sheetnum];

			for (int s = 0; s < sheetnum; s++) {
				HashMap content = new HashMap();
				XSSFSheet sheet = xwb.getSheetAt(s);

				int li_lastrow = sheet.getPhysicalNumberOfRows();
				int li_rowmax = 0;
				int li_colmax = 0;
				int count = 0;
				if (tablefag) {//zzl[2018-11-14]�����Ƿ����ͷ����
					count = 1;
				}
				for (int i = count; i < li_lastrow; i++) {//�޸�
					XSSFRow row = sheet.getRow(i);
					if (row != null) {
						int li_lastcol = row.getLastCellNum();
						for (int j = 0; j < li_lastcol; j++) {
							XSSFCell cell = row.getCell(j);
							if (cell != null) {

							}//2020��7��12��13:05:41  fj  ��ӷǿ��жϣ���������пո�Ļ��ᱨ��ָ��
							else if(cell == null){
								cell = row.createCell(j);
								cell = row.getCell(j);
							}
							// String str_value = row.getCell(j).toString();
							String str_value = getCellValue_xlsx(cell, _filename, s, i, j);
							if (str_value != null && !str_value.equals("")) {

							}
							if (tablefag) {//zzl[2018-11-14]�����Ƿ����ͷ����
								content.put(i - 1 + "_" + j, str_value);
								if (i >= li_rowmax) {
									li_rowmax = i - 1;
								}
							} else {
								content.put(i + "_" + j, str_value);
								if (i >= li_rowmax) {
									li_rowmax = i;
								}
							}
							if (j >= li_colmax) {
								li_colmax = j;
							}

						}
					}
				}
				content.put("rownum", li_rowmax + 1);
				content.put("colnum", li_colmax + 1);
				content.put("sheetindex", s);
				content.put("sheetname", sheet.getSheetName());
				content.put("filename", _filename.substring(_filename.lastIndexOf("\\") + 1, _filename.lastIndexOf(".")));
				contents[s] = content;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return contents;
	}

	// ��ȡxls��Ԫ��ֵ
	private String getCellValue_xls(HSSFCell cell, String _filename, int _sheetIndex, int row, int col) {
		String str_value = "";
		int li_cellType = cell.getCellType();
		if (li_cellType == HSSFCell.CELL_TYPE_STRING) {
			str_value = cell.getStringCellValue();
		} else if (li_cellType == HSSFCell.CELL_TYPE_NUMERIC) {
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				Date d = cell.getDateCellValue();
				DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
				str_value = formater.format(d);
				if (str_value.substring(0, 4).equals("1900")) {
					str_value = String.valueOf((long) cell.getNumericCellValue());
				}
			} else {
				str_value = getNumber(cell.getNumericCellValue());
			}
		} else if (li_cellType == HSSFCell.CELL_TYPE_FORMULA) {
			try {
				str_value = "" + cell.getStringCellValue();
			} catch (Exception ex) {
				System.err.println("�ļ�[" + _filename + "]��[" + _sheetIndex + "]sheet[" + "]��[" + row + "]��[" + col + "]�еĸ��ӵĹ�ʽֵת�ַ�����!");

				try {
					str_value = getNumber(cell.getNumericCellValue());
				} catch (Exception exx) {
					System.err.println("�ļ�[" + _filename + "]��[" + _sheetIndex + "]sheet[" + "]��[" + row + "]��[" + col + "]�еĸ��ӵĹ�ʽֵת���ֳ���!");

					/*
					 * cell.setCellType(Cell.CELL_TYPE_STRING); //ǿ��ת��Ϊ�ַ� �������
					 * str_value = cell.getStringCellValue();
					 */
				}

			}
		} else if (li_cellType == HSSFCell.CELL_TYPE_ERROR) {
			//str_value = "" + cell.getErrorCellValue();
			cell.setCellType(Cell.CELL_TYPE_STRING);
			str_value = cell.getStringCellValue();
		} else {
			str_value = cell.getStringCellValue();
		}

		return str_value;
	}

	// ��ȡxlsx��Ԫ��ֵ
	private String getCellValue_xlsx(XSSFCell cell, String _filename, int _sheetIndex, int row, int col) {
		String str_value = "";

		int li_cellType = cell.getCellType();
		if (li_cellType == XSSFCell.CELL_TYPE_STRING) {
			str_value = cell.getStringCellValue();
		} else if (li_cellType == XSSFCell.CELL_TYPE_NUMERIC) {
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				Date d = cell.getDateCellValue();
				DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
				str_value = formater.format(d);
				if (str_value.substring(0, 4).equals("1900")) {
					str_value = String.valueOf((long) cell.getNumericCellValue());
				}
			} else {
				str_value = getNumber(cell.getNumericCellValue());
			}
		} else if (li_cellType == XSSFCell.CELL_TYPE_FORMULA) {
			try {
				str_value = "" + cell.getStringCellValue();
			} catch (Exception ex) {
				System.err.println("�ļ�[" + _filename + "]��[" + _sheetIndex + "]sheet[" + "]��[" + row + "]��[" + col + "]�еĸ��ӵĹ�ʽֵת�ַ�����!");

				try {
					str_value = getNumber(cell.getNumericCellValue());
				} catch (Exception exx) {
					System.err.println("�ļ�[" + _filename + "]��[" + _sheetIndex + "]sheet[" + "]��[" + row + "]��[" + col + "]�еĸ��ӵĹ�ʽֵת���ֳ���!");

					/*
					 * cell.setCellType(Cell.CELL_TYPE_STRING); //ǿ��ת��Ϊ�ַ� �������
					 * str_value = cell.getStringCellValue();
					 */
				}

			}
		} else if (li_cellType == XSSFCell.CELL_TYPE_ERROR) {
			//str_value = "" + cell.getErrorCellValue();
			cell.setCellType(Cell.CELL_TYPE_STRING);
			str_value = cell.getStringCellValue();
		} else {
			str_value = cell.getStringCellValue();
		}

		return str_value;
	}

	private String getNumber(double num) {
		String value = "";
		if (("" + num).indexOf("E") > 0 || ("" + num).endsWith(".0")) {
			DecimalFormat df = new DecimalFormat("0");
			value = "" + df.format(num);
		} else {
			try {
				value = "" + Integer.parseInt("" + num);
			} catch (NumberFormatException e) {
				String v = num + "";
				if (v.indexOf(".") > 0 && v.substring(v.indexOf(".") + 1, v.length()).length() > 6) {
					value = new BigDecimal(num).setScale(Integer.parseInt(count.trim()), BigDecimal.ROUND_HALF_UP).toString();
				} else {
					value = num + "";
				}
			}
		}
		return value;
	}

	// ��ȡexcel��ͷ��ĸ��
	private String[] getColname(int l) {
		String[] strs = new String[l];
		for (int i = 0; i < l; i++) {
			if (getColumnName2(i).equals("AS")) {
				strs[i] = "ASS";
			} else {
				strs[i] = getColumnName2(i); // 0��ͷ
			}

		}
		return strs;
	}

	// ��һ������ת��Ϊ��ĸ 1-A
	private String getColumnName(int columnNum) {
		String result = "";

		int first;
		int last;
		if (columnNum > 256)
			columnNum = 256;
		first = columnNum / 27;
		last = columnNum - (first * 26);

		if (first > 0)
			result = String.valueOf((char) (first + 64));

		if (last > 0)
			result = result + String.valueOf((char) (last + 64));

		return result.toUpperCase();
	}

	//zzl[2019-3-18]getColumnName�����е����⣬����д��һ����
	private String getColumnName2(int index) {
		String colCode = "";
		char key = 'A';
		int loop = index / 26;
		if (loop > 0) {
			colCode += getColumnName2(loop - 1);
		}
		key = (char) (key + index % 26);
		colCode += key;
		return colCode;
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		if (_event.getItemkey().equals("UPDATETIME")) {
			billListPanel_his = new BillListPanel(new DefaultTMO("", new String[][] { { "�ϴ�ʱ��", "150" }, { "��������", "70" }, { "�鿴", "70" }, { "ɾ��", "70" } }));
			billListPanel_his.putValue(getHvos());
			// billListPanel_his = new BillListPanel(getHvos());
			billListPanel_his.getTempletItemVO("�鿴").setListishtmlhref(true);
			billListPanel_his.getTempletItemVO("ɾ��").setListishtmlhref(true);
			//billListPanel_his.removeRow(0);

			btn_delete_his = new WLTButton("ɾ��");
			btn_delete_his.addActionListener(this);

			/*
			 * billListPanel_his.addBatchBillListButton(new WLTButton[] {
			 * btn_delete_his }); billListPanel_his.repaintBillListButton();
			 */

			btn_exit_his = new WLTButton("�ر�");
			btn_exit_his.addActionListener(this);

			billListPanel_his.addBillListHtmlHrefListener(this);

			WLTPanel btnPanel = new WLTPanel(1, new FlowLayout(1));
			btnPanel.add(btn_exit_his);

			dialog = new BillDialog(this, "�����ϴ���ʷ", 500, 400);
			dialog.getContentPane().setLayout(new BorderLayout());
			dialog.getContentPane().add(billListPanel_his, "Center");
			dialog.getContentPane().add(btnPanel, "South");
			dialog.setVisible(true);
		} else if (_event.getItemkey().equals("�鿴")) {
			onLook_his();
		} else if (_event.getItemkey().equals("ɾ��")) {
			onDelete_his();
		}
	}

	private BillListPanel billListPanel_show;

	private void onLook_his() {
		int li_selRow = billListPanel_his.getSelectedRow();
		if (li_selRow < 0) {
			MessageBox.showSelectOne(billListPanel_his);
			return;
		}

		BillVO billVO_ = billListPanel.getSelectedBillVO();
		String table_name = billVO_.getStringValue("tablename");

		BillVO billVO = billListPanel_his.getSelectedBillVO();
		String upload_time = billVO.getStringValue("�ϴ�ʱ��");
		String data_time = billVO.getStringValue("��������");
		String year = null;
		if (!fag) {
			year = data_time.substring(0, data_time.indexOf("��"));
		}
		String specifictime = billVO.getStringValue("��������");
		String month = "";
		if (data_time.indexOf("��") > 0) {
			month = data_time.substring(data_time.indexOf("��") + 1, data_time.indexOf("��"));
		}

		HashVO[] hvs = null;
		String sql = null;
		if (fag) {
			sql = "select * from " + table_name + " where creattime='" + upload_time + "' and " + getWhere(year, month, specifictime);

			try {
				hvs = UIUtil.getHashVoArrayByDS(null, sql);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (hvs != null && hvs.length > 0) {
				String[][] strs_header = new String[hvs[0].getKeys().length][2];
				strs_header[0] = new String[] { "id", "85" };
				strs_header[1] = new String[] { "year", "85" };
				strs_header[2] = new String[] { "month", "85" };
				strs_header[3] = new String[] { "creattime", "85" };
				strs_header[4] = new String[] { "specifictime", "85" };
				for (int i = 5; i < hvs[0].getKeys().length; i++) {
					strs_header[i] = new String[] { getColumnName2(i + 1 - 5), "85" };
				}

				billListPanel_show = new BillListPanel(new DefaultTMO("", strs_header));
				billListPanel_show.putValue(hvs);

				//BillListPanel billListPanel = new BillListPanel(hvs);
				billListPanel_show.getTempletItemVO("year").setItemname("���");
				billListPanel_show.getTempletItemVO("month").setItemname("�·�");
				billListPanel_show.getTempletItemVO("creattime").setItemname("�ϴ�ʱ��");
				billListPanel_show.getTempletItemVO("specifictime").setItemname("��������");

				billListPanel_show.getTable().getColumn("year").setHeaderValue("���");
				billListPanel_show.getTable().getColumn("month").setHeaderValue("�·�");
				billListPanel_show.getTable().getColumn("creattime").setHeaderValue("�ϴ�ʱ��");
				billListPanel_show.getTable().getColumn("specifictime").setHeaderValue("��������");
				billListPanel_show.getTempletItemVO("id").setCardisshowable(false);
				if (!(data_time.indexOf("��") > 0)) {
					billListPanel_show.getTempletItemVO("month").setCardisshowable(false);
				}

				billListPanel_show.setItemVisible("id", false);
				billListPanel_show.setItemVisible("year", false);
				if (!(data_time.indexOf("��") > 0)) {
					billListPanel_show.setItemVisible("month", false);
				}

				btn_exit_look = new WLTButton("�ر�");
				btn_exit_look.addActionListener(this);
				btn_onlook_check = new WLTButton("У��");
				btn_onlook_check.addActionListener(this);
				WLTPanel btnPanel = new WLTPanel(1, new FlowLayout(1));
				btnPanel.add(btn_onlook_check);
				btnPanel.add(btn_exit_look);

				dialog_look = new BillDialog(this, "���ݲ鿴", 800, 600);
				dialog_look.getContentPane().setLayout(new BorderLayout());
				dialog_look.getContentPane().add(billListPanel_show, "Center");
				dialog_look.getContentPane().add(btnPanel, "South");
				dialog_look.setVisible(true);
			} else {
				MessageBox.show(this, "�����ѱ�ɾ��!");
			}
		} else {
			sql = "select * from " + table_name + " where creattime='" + upload_time + "' and " + getWhere(year, month, specifictime);

			try {
				hvs = UIUtil.getHashVoArrayByDS(null, sql);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (hvs != null && hvs.length > 0) {
				String[][] strs_header = new String[hvs[0].getKeys().length][2];
				strs_header[0] = new String[] { "id", "85" };
				strs_header[1] = new String[] { "year", "85" };
				strs_header[2] = new String[] { "month", "85" };
				strs_header[3] = new String[] { "creattime", "85" };
				for (int i = 4; i < hvs[0].getKeys().length; i++) {

					strs_header[i] = new String[] { getColumnName2(i - 4), "85" };

					strs_header[i] = new String[] { getColumnName2(i - 4), "85" };//��ǰΪi + 1 - 4 ����������A�У�Ϊ��ͳһУ�����ݣ�����ȥ�����ء����/2019-06-12��
				}

				billListPanel_show = new BillListPanel(new DefaultTMO("", strs_header));
				billListPanel_show.putValue(hvs);

				//BillListPanel billListPanel = new BillListPanel(hvs);
				billListPanel_show.getTempletItemVO("year").setItemname("���");
				billListPanel_show.getTempletItemVO("month").setItemname("�·�");
				billListPanel_show.getTempletItemVO("creattime").setItemname("�ϴ�ʱ��");

				billListPanel_show.getTable().getColumn("year").setHeaderValue("���");
				billListPanel_show.getTable().getColumn("month").setHeaderValue("�·�");
				billListPanel_show.getTable().getColumn("creattime").setHeaderValue("�ϴ�ʱ��");

				billListPanel_show.getTempletItemVO("id").setCardisshowable(false);
				if (!(data_time.indexOf("��") > 0)) {
					billListPanel_show.getTempletItemVO("month").setCardisshowable(false);
				}

				billListPanel_show.setItemVisible("id", false);
				if (!(data_time.indexOf("��") > 0)) {
					billListPanel_show.setItemVisible("month", false);
				}

				btn_exit_look = new WLTButton("�ر�");
				btn_exit_look.addActionListener(this);
				btn_onlook_check = new WLTButton("У��");
				btn_onlook_check.addActionListener(this);
				WLTPanel btnPanel = new WLTPanel(1, new FlowLayout(1));
				btnPanel.add(btn_onlook_check);
				btnPanel.add(btn_exit_look);

				dialog_look = new BillDialog(this, "���ݲ鿴", 800, 600);
				dialog_look.getContentPane().setLayout(new BorderLayout());
				dialog_look.getContentPane().add(billListPanel_show, "Center");
				dialog_look.getContentPane().add(btnPanel, "South");
				dialog_look.setVisible(true);
			} else {
				MessageBox.show(this, "�����ѱ�ɾ��!");
			}
		}
	}

	private void onDelete_his() {
		int li_selRow = billListPanel_his.getSelectedRow();
		if (li_selRow < 0) {
			MessageBox.showSelectOne(billListPanel_his);
			return;
		}

		BillVO billVO_ = billListPanel.getSelectedBillVO();
		String table_name = billVO_.getStringValue("tablename");

		BillVO billVO = billListPanel_his.getSelectedBillVO();
		String upload_time = billVO.getStringValue("�ϴ�ʱ��");
		String data_time = billVO.getStringValue("��������");
		String year = null;
		if (!fag) {
			year = data_time.substring(0, data_time.indexOf("��"));
		}
		String specifictime = billVO.getStringValue("��������");
		String month = "";
		if (data_time.indexOf("��") > 0) {
			month = data_time.substring(data_time.indexOf("��") + 1, data_time.indexOf("��"));
		}
		String del_sql = null;
		del_sql = "delete from " + table_name + " where creattime='" + upload_time + "' and " + getWhere(year, month, specifictime);
		try {
			if (MessageBox.confirm(this, "��ȷ��Ҫɾ�������ϴ�������?")) {
				UIUtil.executeUpdateByDS(null, del_sql);
				billListPanel_his.removeSelectedRows(); // ɾ��һ������
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private HashVO[] getHvos() {
		BillVO billVO_ = billListPanel.getSelectedBillVO();
		String table_name = billVO_.getStringValue("tablename");

		HashVO[] hvs = null;
		try {
			if (fag) {
				String sql = "select SPECIFICTIME, creattime from " + table_name + " group by SPECIFICTIME,creattime"; //
				hvs = UIUtil.getHashVoArrayByDS(null, sql);
				for (int i = 0; i < hvs.length; i++) {
					hvs[i].setAttributeValue("�ϴ�ʱ��", hvs[i].getStringValue("creattime", ""));
					hvs[i].setAttributeValue("��������", hvs[i].getStringValue("SPECIFICTIME", ""));
					hvs[i].setAttributeValue("�鿴", "�鿴");
					hvs[i].setAttributeValue("ɾ��", "ɾ��");
				}
			} else {
				int curryear = Integer.parseInt(TBUtil.getTBUtil().getCurrDate().substring(0, 4)); //�õ���ǰ�ꡣ
				String sql = "select distinct year, month, creattime from " + table_name + " where year > " + (curryear - 3) + " order by creattime desc"; //
				hvs = UIUtil.getHashVoArrayByDS(null, sql);
				for (int i = 0; i < hvs.length; i++) {
					hvs[i].setAttributeValue("�ϴ�ʱ��", hvs[i].getStringValue("creattime", ""));
					String year = hvs[i].getStringValue("year", "");
					String month = hvs[i].getStringValue("month", "");
					if (month.equals("")) {
						hvs[i].setAttributeValue("��������", year + "��");
					} else {
						hvs[i].setAttributeValue("��������", year + "��" + month + "��");
					}
					hvs[i].setAttributeValue("�鿴", "�鿴");
					hvs[i].setAttributeValue("ɾ��", "ɾ��");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return hvs;
	}

	private void onBillListPopEdit() {
		BillListPanel billList = billListPanel;

		BillVO billVO = billList.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}

		BillCardPanel cardPanel = new BillCardPanel(billList.templetVO);
		cardPanel.setLoaderBillFormatPanel(billList.getLoaderBillFormatPanel());
		cardPanel.setBillVO(billVO);

		BillCardDialog dialog = new BillCardDialog(billList, billList.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);

		String tablename = billVO.getStringValue("TABLENAME", "");
		if (!tablename.equals("")) {
			try {
				HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, "select count(id) cou from " + tablename);
				int cou = hvs[0].getIntegerValue("cou", 0);
				if (cou > 0) {
					cardPanel.setEditable("UPDATERATE", false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		dialog.setVisible(true);
	}
}

class PUB_TMO extends AbstractTMO {
	private static final long serialVersionUID = 8057184541083294474L;

	public HashVO getPub_templet_1Data() {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", "PUB_EXCEL_CHECK"); //ģ�����,��������޸�
		vo.setAttributeValue("templetname", "ExcelУ��"); //ģ������
		vo.setAttributeValue("templetname_e", "Excel"); //ģ������
		vo.setAttributeValue("tablename", "VI_MENU"); //��ѯ���ݵı�(��ͼ)��
		vo.setAttributeValue("pkname", "ID"); //������
		vo.setAttributeValue("pksequencename", "S_PUB_MENU"); //������
		vo.setAttributeValue("savedtablename", "PUB_MENU"); //�������ݵı���
		vo.setAttributeValue("CardWidth", "577"); //��Ƭ���
		vo.setAttributeValue("Isshowlistpagebar", "N"); //�б��Ƿ���ʾ��ҳ��
		vo.setAttributeValue("Isshowlistopebar", "N"); //�б��Ƿ���ʾ������ť��
		vo.setAttributeValue("listcustpanel", null); //�б��Զ������
		vo.setAttributeValue("cardcustpanel", null); //��Ƭ�Զ������

		vo.setAttributeValue("TREEPK", "id"); //�б��Ƿ���ʾ������ť��
		vo.setAttributeValue("TREEPARENTPK", "parentmenuid"); //�б��Ƿ���ʾ������ť��
		vo.setAttributeValue("Treeviewfield", "name"); //�б��Ƿ���ʾ������ť��
		vo.setAttributeValue("Treeisshowtoolbar", "Y"); //������ʾ������
		vo.setAttributeValue("Treeisonlyone", "N"); //������ʾ������
		vo.setAttributeValue("Treeseqfield", "seq"); //�б��Ƿ���ʾ������ť��
		vo.setAttributeValue("Treeisshowroot", "Y"); //�б��Ƿ���ʾ������ť��
		return vo;
	}

	public HashVO[] getPub_templet_1_itemData() {
		Vector vector = new Vector();
		HashVO itemVO = null;
		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "POSITION"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "λ��"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "Code"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "1"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", "getStringItemVO(getItemValue(\"ID\"))"); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "70"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "325"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "N");
		vector.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "DESCR"); //Ψһ��ʶ,����ȡ���뱣��
		itemVO.setAttributeValue("itemname", "��Ϣ"); //��ʾ����
		itemVO.setAttributeValue("itemname_e", "Code"); //��ʾ����
		itemVO.setAttributeValue("itemtype", "�����ı���"); //�ؼ�����
		itemVO.setAttributeValue("comboxdesc", null); //��������
		itemVO.setAttributeValue("refdesc", null); //���ն���
		itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "1"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
		itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
		itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
		itemVO.setAttributeValue("editformula", null); //�༭��ʽ
		itemVO.setAttributeValue("defaultvalueformula", "getStringItemVO(getItemValue(\"ID\"))"); //Ĭ��ֵ��ʽ
		itemVO.setAttributeValue("listwidth", "400"); //�б��ǿ��
		itemVO.setAttributeValue("cardwidth", "325"); //��Ƭʱ���
		itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
		itemVO.setAttributeValue("cardiseditable", "4"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
		itemVO.setAttributeValue("iswrap", "Y");
		vector.add(itemVO);

		return (HashVO[]) vector.toArray(new HashVO[0]);
	}

}
