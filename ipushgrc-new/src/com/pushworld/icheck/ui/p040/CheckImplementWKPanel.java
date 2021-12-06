package com.pushworld.icheck.ui.p040;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.HashVOStruct;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.report.cellcompent.ExcelUtil;

import com.pushworld.ipushgrc.ui.icheck.p030.CheckListSelectDialog;
import com.pushworld.ipushgrc.ui.icheck.p080.ZZLUIUtil;
import com.pushworld.ipushgrc.ui.icheck.word.WordExport;

/**
 * 网络版检查实施【邵春芸/2016-08-19】
 * 
 * @author shaochunyun
 * 
 */
public class CheckImplementWKPanel extends AbstractWorkPanel implements
		BillListSelectListener, ActionListener, BillListHtmlHrefListener,
		MouseMotionListener, MouseListener, BillTreeSelectListener {

	private BillListPanel schemeList = null; // 检查方案
	private BillListPanel implList = null; // 检查实施主表
	private BillListPanel itemList = null; // 检查实施子表
	private WLTButton btn_selectScheme;// 检查方案-切换方案【李春娟/2016-08-19】
	private WLTButton btn_copy;// 检查实施新增（拷贝）【李春娟/2016-09-29】
	private WLTButton btn_edit;// 检查实施编辑【李春娟/2016-09-29】
	private WLTButton btn_relate;// 检查实施关联客户【李春娟/2016-10-09】
	private WLTButton btn_delete;// 检查实施删除关联客户【zzl/2017-4-17】
	private WLTButton btn_copyxx;// 检查实施复制客户信息【zzl/2017-4-20】
	private WLTButton btn_change_data;// 检查方案-数据交换【李春娟/2016-08-30】
	private WLTButton btn_insert;// 检查实施子表-逐条录入
	private WLTButton btn_query;// 检查实施子表-检查评价指引
	private WLTButton btn_end;// 检查实施子表-底稿录入完成
	private WLTButton btn_copydg;// 检查实施子表-复制底稿【zzl 2017-1-12】
	private WLTButton btn_copywt;// 检查实施子表-复制问题【zzl 2017-3-22】
	private WLTButton btn_deletePro;// 问题列表-删除按钮【李春娟/2016-09-07】
	private WLTButton btn_exp_word;// 网络版导出检查通知书、进驻会谈记录模板、资料调阅清单【张珍龙/2016-09-21】
	private String schemeTempletCode = "V_CK_SCHEME_LCJ_Q01";//这个视图模板在加载的时候特别慢【zzl 2017-11-29】
	private String parentTempletCode = "CK_SCHEME_IMPL_E01";// 以前检查实施只有一张表，修改为将检查实施弄成主子表关系【李春娟/2016-09-23】
	private String parentTempletCode2 = "CK_SCHEME_IMPL_E02";// 信贷检查和票据检查的模板和一般检查不同【李春娟/2016-10-09】
	private String childTempletCode = "V_CK_SCHEME_IMPLEMENT_SCY_E01"; // 检查详细底稿模板code
	private String ProblemTempCode = "CK_PROBLEM_INFO_SCY_E01";
	private String loginUserid = ClientEnvironment.getInstance()
			.getLoginUserID();
	private JPopupMenu changeDataPopMenu, expWordPopMenu, ckimpdigao;// 检查方案-数据交换，检查方案-导出Word
	private JMenuItem menu_impdata, menu_expdata, menu_notice, menu_interview,
			menu_retrival;// 数据交换-导入，数据交换-导出，[zzl]导出Word-导出检查通知书，导出Word-导出进驻会谈记录模板，导出Word-导出资料调阅清单[zzl]复制底稿加入menu_qb,menu_xc,menu_fxc
	private WLTSplitPane splitPane2;
	private String schemeid = null;
	private String implid = null;
	private String SCHEMETYPE = null;
	private boolean isXDJC = false;
	private BillVO impvo2 = null;
	private String impdeptid = null;
	private WLTButton btn_newDept = null;// zzl[增加部门]
	private WLTButton btn_newUser = null;// zzl[导入人员]
	private WLTButton btn_CknewUser = null;// zzl[增加检查人员]
	private WLTButton btn_newCkDG = null;// zzl[修改底稿]
	private WLTButton btn_tongbu = null;// zzl[同步]
	private WLTButton btn_dyqd = null;// zzl[调阅清单]
	private BillListPanel manuscriptPanel = null; // 检查底稿
	private WLTButton btn_delete_DG = null;
	private WLTButton btn_new_DG = null;
	private BillListDialog designlist = null;
	private WLTButton btn_scheme_delete = null;
	private WLTButton btn_scheme_delete2 = null;
	private WLTButton btn_ImpDiGao, btn_qb, btn_xc, btn_fxc = null;

	@Override
	public void initialize() {
		String[][] schemes = null;
		try {
			schemes = UIUtil
					.getStringArrayByDS(
							null,
							"select id,SCHEMETYPE from ck_scheme where id in (select schemeid from ck_scheme_user where userid="
									+ loginUserid + ")");
		} catch (WLTRemoteException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		String schemeid = null;
		if (schemes != null && schemes.length > 0) {
			schemeid = schemes[0][0];
			SCHEMETYPE = schemes[0][1];
			if ("信贷检查".equals(SCHEMETYPE) || "票据检查".equals(SCHEMETYPE)) {// 信贷检查和票据检查单个业务需要检查多笔【李春娟/2016-09-29】
				isXDJC = true;
			}
		}

		btn_selectScheme = new WLTButton("切换方案", "office_051.gif");
		btn_exp_word = new WLTButton("导出Word▼");
		btn_change_data = new WLTButton("数据交换▼");// 【李春娟/2016-08-30】
		btn_tongbu = new WLTButton("同步");
		btn_tongbu.addActionListener(this);
		btn_dyqd = new WLTButton("录入调阅清单");
		btn_dyqd.addActionListener(this);
		btn_copy = new WLTButton("新增");// 【李春娟/2016-09-29】
		btn_edit = new WLTButton("修改");// 【李春娟/2016-09-29】
		btn_relate = new WLTButton("关联");
		btn_delete = new WLTButton("删除关联");
		btn_copyxx = new WLTButton("复制信息");
		btn_newDept = new WLTButton("新增部门");
		btn_CknewUser = new WLTButton("新增检查人员");
		btn_scheme_delete = new WLTButton("删除");
		btn_scheme_delete2 = new WLTButton("删除");
		btn_scheme_delete2.addActionListener(this);
		btn_scheme_delete.addActionListener(this);
		btn_CknewUser.addActionListener(this);
		btn_selectScheme.addActionListener(this);
		btn_exp_word.addActionListener(this);
		btn_change_data.addActionListener(this);
		btn_newDept.addActionListener(this);
		btn_copy.addActionListener(this);
		btn_edit.addActionListener(this);
		btn_relate.addActionListener(this);
		btn_delete.addActionListener(this);
		btn_copyxx.addActionListener(this);
		// 检查方案
		if (isXDJC) {
			schemeList = new BillListPanel(schemeTempletCode);
			schemeList.setQuickQueryPanelVisiable(false);
			schemeList
					.addBatchBillListButton(new WLTButton[] { btn_selectScheme,
							btn_exp_word, btn_change_data, btn_newDept,
							btn_CknewUser, btn_scheme_delete, btn_tongbu });
		} else {
			schemeList = new BillListPanel(parentTempletCode);
			schemeList.setQuickQueryPanelVisiable(false);
			schemeList.addBatchBillListButton(new WLTButton[] {
					btn_selectScheme, btn_exp_word, btn_change_data,
					btn_newDept, btn_CknewUser, btn_scheme_delete, btn_tongbu,
					btn_dyqd });
		}

		schemeList.repaintBillListButton();
		schemeList.addBillListSelectListener(this);

		// 检查实施子表
		itemList = new BillListPanel(childTempletCode);
		btn_insert = new WLTButton("逐条录入", "folder_edit.png");
		btn_query = new WLTButton("检查评价指引");
		btn_end = new WLTButton("底稿录入完成");
		btn_copydg = new WLTButton("复制底稿");
		btn_copywt = new WLTButton("复制问题");
		btn_newCkDG = new WLTButton("修改底稿");
		btn_newCkDG.addActionListener(this);
		btn_ImpDiGao = new WLTButton("导出底稿");
		btn_ImpDiGao.addActionListener(this);
		btn_newUser = new WLTButton("导入");
		btn_newUser.addActionListener(this);
		btn_copywt.addActionListener(this);
		btn_insert.addActionListener(this);
		btn_query.addActionListener(this);
		btn_end.addActionListener(this);
		btn_copydg.addActionListener(this);
		itemList.addBatchBillListButton(new WLTButton[] { btn_insert,
				btn_query, btn_end, btn_copydg, btn_copywt, btn_newCkDG,
				btn_ImpDiGao });
		itemList.repaintBillListButton();
		itemList.addBillListHtmlHrefListener(this);

		if (isXDJC) {
			implList = new BillListPanel(parentTempletCode2);
			implList.addBatchBillListButton(new WLTButton[] { btn_copy,
					btn_edit, btn_relate, btn_delete, btn_copyxx, btn_newUser,
					btn_scheme_delete2 });
			implList.repaintBillListButton();
			if (schemeid != null) {
				schemeList.QueryDataByCondition("schemeid='" + schemeid
						+ "' and (teamusers like '%;" + loginUserid
						+ ";%' or leader2='" + loginUserid + "' or leader ='"
						+ loginUserid + "' or referee like '%;" + loginUserid
						+ ";%')");
				
			}
			WLTSplitPane splitPane = new WLTSplitPane(
					WLTSplitPane.HORIZONTAL_SPLIT, schemeList, implList);
			splitPane.setDividerLocation(540);// 修改分隔条位置【李春娟/2016-10-09】

			splitPane2 = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT,
					splitPane, itemList);
			splitPane2.setDividerLocation(250);// 修改分隔条位置【李春娟/2016-10-09】
		} else {
			implList = new BillListPanel(parentTempletCode);
			implList.addBatchBillListButton(new WLTButton[] {
						btn_selectScheme, btn_exp_word, btn_change_data,
						btn_newDept, btn_CknewUser, btn_scheme_delete,
						btn_tongbu, btn_dyqd });


			implList.repaintBillListButton();
			if (schemeid != null) {
				implList.QueryDataByCondition("schemeid='" + schemeid
						+ "' and (teamusers like '%;" + loginUserid
						+ ";%' or leader2='" + loginUserid + "' or leader ='"
						+ loginUserid + "' or referee like '%;" + loginUserid
						+ ";%')");
			}
			splitPane2 = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT,
					implList, itemList);
			splitPane2.setDividerLocation(250);// 修改分隔条位置【李春娟/2016-10-09】
		}
		implList.addBillListSelectListener(this);
		implList.setQuickQueryPanelVisiable(false);// 设置查询面板隐藏【李春娟/2016-09-27】

		this.add(splitPane2);
	}

	/**
	 * 列表选择事件
	 */
	public void onBillListSelectChanged(BillListSelectionEvent event) {
		if (event.getBillListPanel() == implList) {
			BillVO vo = implList.getSelectedBillVO();
			if (null != vo) {
				implid = vo.getStringValue("id");
				String status = vo.getStringValue("status");
				itemList.QueryDataByCondition("implid = '" + implid + "'");
				if ("已结束".equals(status)) {
					btn_edit.setVisible(false);// 检查实施-修改
					btn_insert.setVisible(false);// 检查实施子表-逐条录入
					btn_end.setVisible(false);// 检查实施子表-底稿录入完成
				} else {
					btn_edit.setVisible(true);
					btn_insert.setVisible(true);
					btn_end.setVisible(true);
				}
			}
		} else if (event.getBillListPanel() == schemeList) {
			impvo2 = schemeList.getSelectedBillVO();
			if (null != impvo2) {
				schemeid = impvo2.getStringValue("schemeid");
				impdeptid = impvo2.getStringValue("deptid");
				implList.QueryDataByCondition("schemeid = '" + schemeid
						+ "' and deptid = '" + impdeptid + "'");
				itemList.clearTable();
			}
		}
	}

	/**
	 * 问题列表 链接点击事件
	 */
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent arg0) {
		ArrayList<String> al_sql = new ArrayList<String>();
		String[] sql = itemList.getUpdateSQLs();
		for (int i = 0; i < sql.length; i++) {
			al_sql.add(sql[i].substring(0, sql[i].indexOf("where"))
					+ "  where id='"
					+ itemList.getAllBillVOs()[i].getStringValue("id") + "'");

		}

		try {
			UIUtil.executeBatchByDS(null, al_sql);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 控制缺陷显示
		final BillListDialog list_dialog = new BillListDialog(this, "问题录入",
				ProblemTempCode, 1000, 500);
		BillListPanel bugListPanel = list_dialog.getBilllistPanel(); // 缺陷记录表
		BillVO billvo = implList.getSelectedBillVO();
		if (billvo != null && !"已结束".equals(billvo.getStringValue("status"))) {
			btn_deletePro = new WLTButton("删除");// 问题删除，需要同时删除违规事件【李春娟/2016-09-07】
			btn_deletePro.addActionListener(this);
			bugListPanel.addBillListButton(WLTButton
					.createButtonByType(WLTButton.LIST_POPEDIT)); // 编辑
			bugListPanel.addBillListButton(btn_deletePro); // 删除
		}
		bugListPanel.addBillListButton(WLTButton
				.createButtonByType(WLTButton.LIST_SHOWCARD)); //
		bugListPanel.repaintBillListButton();
		list_dialog.setTitle("查看问题");
		list_dialog.getBtn_confirm().setVisible(false);
		list_dialog.getBtn_cancel().setText("关闭");
		list_dialog.getBtn_cancel().setIcon(UIUtil.getImage("zt_031.gif"));// zt_050.gif
		// office_187.gif
		list_dialog.getBtn_cancel().setMargin(new Insets(0, 0, 0, 0)); //
		list_dialog.getBtn_cancel().setPreferredSize(
				new Dimension(80, list_dialog.getBtn_cancel().BTN_HEIGHT));
		list_dialog.getBilllistPanel().QueryDataByCondition(
				"parentid='"
						+ arg0.getBillListPanel().getSelectedBillVO()
								.getStringValue("id") + "'");
		list_dialog.setVisible(true);
		itemList.refreshCurrSelectedRow();
	}

	/**
	 * 按钮事件
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_selectScheme) {// 检查方案-切换方案
			onSelectScheme();
		} else if (e.getSource() == btn_change_data) {// 检查方案-数据交换
			onChangeData();
		} else if (e.getSource() == menu_impdata) {// 检查方案-数据交换-导入【李春娟/2016-08-30】
			onImpData();
		} else if (e.getSource() == menu_expdata) {// 检查方案-数据交换-导出【李春娟/2016-08-30】
			onExpData();
		} else if (e.getSource() == btn_exp_word) {// 检查方案-导出Word
			onExpWord();
		} else if (e.getSource() == menu_notice) {// 检查方案-导出Word-导出检查通知书
			new SplashWindow(this, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					impnotice();
				}
			}, 600, 130, 300, 300, false); // 

		} else if (e.getSource() == menu_interview) {// 检查方案-导出Word-导出进驻会谈记录模板
			new SplashWindow(this, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					impinterview();
				}
			}, 600, 130, 300, 300, false); // 

		} else if (e.getSource() == menu_retrival) {// 检查方案-导出Word-导出资料调阅清单
			new SplashWindow(this, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					impRetrival();
				}
			}, 600, 130, 300, 300, false); // 

		} else if (e.getSource() == btn_copy) {// 检查实施新增（拷贝）
			onCopyImpl();
		} else if (e.getSource() == btn_edit) {// 检查实施编辑
			onEditImpl();
		} else if (e.getSource() == btn_relate) {// 检查实施关联客户
			onRelate();
		} else if (e.getSource() == btn_insert) {// 检查实施子表-逐条录入
			onInsert();
		} else if (e.getSource() == btn_query) {// 检查实施子表-检查评价指引
			onCheckHelp();
		} else if (e.getSource() == btn_end) {// 检查实施子表-底稿录入完成
			onEnd();
		} else if (e.getSource() == btn_deletePro) {// 问题列表-删除
			onDeleteProblem();
		} else if (e.getSource() == btn_copydg) {
			new SplashWindow(this, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					BillVO[] vo = implList.getBillVOs();
					String schemetype = vo[0].getStringValue("schemetype");
					if ("信贷检查".equals(schemetype) || "票据检查".equals(schemetype)) {
						onCopyXD();
					} else {
						onCopy();
					}
				}
			}, 600, 130, 300, 300, false);
		} else if (e.getSource() == btn_copywt) {
			new SplashWindow(this, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					BillVO[] vo = implList.getBillVOs();
					String schemetype = vo[0].getStringValue("schemetype");
					if ("信贷检查".equals(schemetype) || "票据检查".equals(schemetype)) {
						onCopyXDWT();
					} else {
						onCopyWT();
					}

				}
			}, 600, 130, 300, 300, false);
		} else if (e.getSource() == btn_delete) {
			onDelete();
		} else if (e.getSource() == btn_copyxx) {
			onCopyxx();
		} else if (e.getSource() == btn_newDept) {
			onNewDept();
		} else if (e.getSource() == btn_newUser) {
			onNewUser();
		} else if (e.getSource() == btn_CknewUser) {
			onCkUser();
		} else if (e.getSource() == btn_newCkDG) {
			onnewCkDG();
		} else if (e.getSource() == btn_delete_DG) {
			ondelete_DG();
		} else if (e.getSource() == btn_new_DG) {
			onnew_DG();
		} else if (e.getSource() == btn_scheme_delete) {
			onscheme_delete();
		} else if (e.getSource() == btn_scheme_delete2) {
			onscheme_delete2();
		} else if (e.getSource() == btn_tongbu) {
			onTongbu();
		} else if (e.getSource() == btn_dyqd) {
			onDiaoYueQingDan();
		} else if (e.getSource() == btn_ImpDiGao) {
					BillVO[] vo = implList.getBillVOs();
					String schemetype = vo[0].getStringValue("schemetype");
					if ("信贷检查".equals(schemetype) || "票据检查".equals(schemetype)) {
						onImpDiGaoXD();
					} else {
						onImpDiGao();
					}		
		}

	}
/**
 * ZZL 导出信贷底稿
 */
	private void onImpDiGaoXD() {
		BillVO schemeListVO = implList.getSelectedBillVO();
		if (schemeListVO == null) {
			if (schemeListVO == null) {
				MessageBox.show(this, "请选择客户导出底稿");
				return;
			}
		}
		BillListDialog listDialog = new BillListDialog(this, "导出底稿",
				"V_CK_IMPXDDIGAO_CODE1");
		try {
			listDialog.getBilllistPanel().QueryDataByCondition(
					"implid='" + schemeListVO.getStringValue("id") + "'");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		listDialog.setVisible(true);
		
	}

	/**
	 * zzl 导出底稿
	 */
	private void onImpDiGao() {		 	              
		BillVO schemeListVO = implList.getSelectedBillVO();
		if (schemeListVO == null) {
			if (schemeListVO == null) {
				MessageBox.show(this, "请选择部门导出底稿");
				return;
			}
		}
		BillListDialog listDialog = new BillListDialog(this, "导出底稿",
				"V_CK_IMPDIGAO_CODE1");
		try {
			listDialog.getBilllistPanel().QueryDataByCondition(
					"implid='" + schemeListVO.getStringValue("id") + "'");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		listDialog.setVisible(true);

	}

	/**
	 * 录入调阅清单 【zzl2017-11-8】
	 * 
	 * CK_RETRIVAL_ZZl_EQ1
	 */
	private void onDiaoYueQingDan() {
		BillVO vo = implList.getSelectedBillVO();
		if (vo == null) {
			MessageBox.show(this, "请选择一条数据进行录入");
			return;
		}
		BillListDialog RetrivalDialog = new BillListDialog(this, "请填写要导出的资料",
				"CK_RETRIVAL_ZZl_EQ1", 900, 500);
		RetrivalDialog.getBilllistPanel().QueryDataByCondition(
				"schemeid='" + vo.getStringValue("schemeid") + "' and deptid='"
						+ vo.getStringValue("deptid") + "'");
		RetrivalDialog.setVisible(true);
		if (RetrivalDialog.ButtonID == 1) {
			final BillVO[] Rvos = RetrivalDialog.getBilllistPanel()
					.getBillVOs();
			if (Rvos.length <= 0) {
				return;
			}
			for (int i = 0; i < Rvos.length; i++) {
				UpdateSQLBuilder update = new UpdateSQLBuilder("CK_RETRIVAL");
				update.setWhereCondition("id=" + Rvos[i].getStringValue("id"));
				update.putFieldValue("schemeid", vo.getStringValue("schemeid"));
				update.putFieldValue("deptid", vo.getStringValue("deptid"));
				try {
					UIUtil.executeUpdateByDS(null, update.getSQL());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
		}

	}

	/**
	 * 同步信息 客户反应一些复制过来的底稿或者问题都导不出 就是问题对应的planid,deptid,schemeid,implid不对应
	 * zzl[2016/6/2]
	 */
	private void onTongbu() {
		BillVO[] vo = implList.getBillVOs();
		String schemetype = vo[0].getStringValue("schemetype");
		StringBuffer sb = new StringBuffer();
		List list = new ArrayList<String>();
		UpdateSQLBuilder update = new UpdateSQLBuilder("ck_problem_info");
		try {
			if ("信贷检查".equals(schemetype) || "票据检查".equals(schemetype)) {
				BillVO schemeListvo = schemeList.getSelectedBillVO();
				if (schemeListvo == null) {
					MessageBox.show(this, "请选择一条数据进行同步");
					return;
				}
				BillVO[] implListvos = implList.getBillVOs();
				for (int i = 0; i < implListvos.length; i++) {
					String[] parentid = UIUtil.getStringArrayFirstColByDS(null,
							"select id from V_CK_SCHEME_IMPLEMENT where 1=1  and (implid = '"
									+ implListvos[i].getStringValue("id")
									+ "')");
					for (int j = 0; j < parentid.length; j++) {
						String implid = UIUtil.getStringValueByDS(null,
								"select id from ck_problem_info where 1=1  and (parentid='"
										+ parentid[j].toString() + "')");
						if (implid != null) {
							update.setWhereCondition("id=" + implid);
							update.putFieldValue("planid", implListvos[i]
									.getStringValue("planid"));
							update.putFieldValue("schemeid", implListvos[i]
									.getStringValue("schemeid"));
							update.putFieldValue("deptid", implListvos[i]
									.getStringValue("deptid"));
							update.putFieldValue("implid", implListvos[i]
									.getStringValue("id"));
							list.add(update.getSQL());
						}
					}

				}
				UIUtil.executeBatchByDS(null, list);
				MessageBox.show(this, "同步成功");
			}
		} catch (Exception e) {
			MessageBox.show(this, "同步失败");
			e.printStackTrace();
		}

	}

	/**
	 * 删除客户信息及底稿【zzl】
	 */
	private void onscheme_delete2() {
		BillVO schemeListVO = schemeList.getSelectedBillVO();
		if (schemeListVO == null) {
			MessageBox.show(this, "请选择一条数据进行操作");
			return;
		}
		BillVO implListVO = implList.getSelectedBillVO();
		if (implListVO == null) {
			MessageBox.show(this, "请选择一条数据进行操作");
			return;
		}
		try {
			UIUtil.executeUpdateByDS(null,
					"delete from CK_SCHEME_IMPL where id='"
							+ implListVO.getStringValue("id") + "'");
			UIUtil.executeUpdateByDS(null,
					"delete from ck_scheme_implement where implid='"
							+ implListVO.getStringValue("id") + "'");
			implList.QueryDataByCondition("schemeid='"
					+ schemeListVO.getStringValue("schemeid")
					+ "' and deptid='" + schemeListVO.getStringValue("deptid")
					+ "'");
			itemList.QueryDataByCondition("implid='"
					+ implListVO.getStringValue("id") + "'");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 删除部门客户信息及底稿【zzl】
	 */
	private void onscheme_delete() {
		BillVO schemeListVO = implList.getSelectedBillVO();
		if (schemeListVO == null) {
			schemeListVO = schemeList.getSelectedBillVO();
			if (schemeListVO == null) {
				MessageBox.show(this, "请选择一条数据进行操作");
				return;
			}
		}
		try {
			UIUtil.executeUpdateByDS(null,
					"delete from CK_SCHEME_IMPL where schemeid='"
							+ schemeListVO.getStringValue("schemeid")
							+ "' and deptid='"
							+ schemeListVO.getStringValue("deptid") + "'");
			UIUtil.executeUpdateByDS(null,
					"delete from ck_scheme_implement where schemeid='"
							+ schemeListVO.getStringValue("schemeid")
							+ "' and deptid='"
							+ schemeListVO.getStringValue("deptid") + "'");
			String[][] checkeddept = UIUtil.getStringArrayByDS(null,
					"select id,checkeddept from CK_MEMBER_WORK where schemeid='"
							+ schemeListVO.getStringValue("schemeid")
							+ "' and checkeddept like '%" + ";"
							+ schemeListVO.getStringValue("deptid") + ";"
							+ "%'");
			if (checkeddept != null && checkeddept.length > 0) {
				String checkeddeptStr = checkeddept[0][1].substring(1,
						checkeddept[0][1].length() - 1);
				String[] splitteamusers = checkeddeptStr.split(";");
				if (splitteamusers.length > 1) {
					UpdateSQLBuilder update = new UpdateSQLBuilder(
							"CK_MEMBER_WORK");
					update.setWhereCondition("id="
							+ checkeddept[0][0].toString());
					update
							.putFieldValue(
									"checkeddept",
									checkeddept[0][1]
											.toString()
											.replace(
													";"
															+ schemeListVO
																	.getStringValue("deptid"),
													""));
					UIUtil.executeUpdateByDS(null, update.getSQL());
				} else {
					UIUtil.executeUpdateByDS(null,
							"delete from CK_MEMBER_WORK where id='"
									+ checkeddept[0][0].toString() + "'");
				}
			}
			if (implList.getTempletVO().getTempletcode().equals(
					"CK_SCHEME_IMPL_E01")) {//
				implList.reload();
				implList.QueryDataByCondition("schemeid='"
						+ schemeListVO.getStringValue("schemeid")
						+ "' and (teamusers like '%;" + loginUserid
						+ ";%' or leader2='" + loginUserid + "' or leader ='"
						+ loginUserid + "' or referee like '%;" + loginUserid
						+ ";%')");
				// implList.QueryDataByCondition("schemeid='"
				// + schemeListVO.getStringValue("schemeid") + "'");
				// itemList.QueryDataByCondition("schemeid='"
				// + schemeListVO.getStringValue("schemeid") + "'");

			} else {
				schemeList.reload();
				schemeList.QueryDataByCondition("schemeid='"
						+ schemeListVO.getStringValue("schemeid")
						+ "' and (teamusers like '%;" + loginUserid
						+ ";%' or leader2='" + loginUserid + "' or leader ='"
						+ loginUserid + "' or referee like '%;" + loginUserid
						+ ";%')");
				// implList.QueryDataByCondition("schemeid='"
				// + schemeListVO.getStringValue("schemeid")
				// + "' and deptid='"
				// + schemeListVO.getStringValue("deptid") + "'");
				// itemList.QueryDataByCondition("schemeid='"
				// + schemeListVO.getStringValue("schemeid")
				// + "' and deptid='"
				// + schemeListVO.getStringValue("deptid") + "'");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * zzl 修改词条【插入词条】
	 */
	private void onnew_DG() {
		BillVO impvo = implList.getSelectedBillVO();
		CheckListSelectDialog selectDialog = new CheckListSelectDialog(
				designlist.getBilllistPanel(), "问题词条");
		selectDialog.setVisible(true);
		List list = new ArrayList<String>();
		Map map = new HashMap<String, String>();
		try {
			TableDataStruct ck_problem_dict = UIUtil.getTableDataStructByDS(
					null, "select * from ck_problem_dict where 1=2");
			String[] dict = ck_problem_dict.getHeaderName();
			TableDataStruct ck_scheme_implement = UIUtil
					.getTableDataStructByDS(null,
							"select * from ck_scheme_implement where 1=2");
			String[] implement = ck_scheme_implement.getHeaderName();
			InsertSQLBuilder designinsert = new InsertSQLBuilder(
					"ck_manuscript_design");
			InsertSQLBuilder implementinsert = new InsertSQLBuilder(
					"ck_scheme_implement");
			String ck_manuscript_designID = null;
			BillVO[] vo = selectDialog.getReturnVOs();
			String[] designGL = UIUtil.getStringArrayFirstColByDS(null,
					"select importid from ck_manuscript_design where 1=1 and schemeid='"
							+ impvo.getStringValue("schemeid")
							+ "' group by importid");
			for (int i = 0; i < designGL.length; i++) {
				map.put(designGL[i], designGL[i]);
			}

			HashVO[] CK_SCHEME_IMPL = UIUtil.getHashVoArrayByDS(null,
					"select * from CK_SCHEME_IMPL where 1=1 and schemeid='"
							+ impvo.getStringValue("schemeid") + "'");
			HashVO[] ck_scheme_implementvo = UIUtil.getHashVoArrayByDS(null,
					"select * from ck_scheme_implement where 1=1 and schemeid='"
							+ impvo.getStringValue("schemeid") + "'");
			for (int i = 0; i < vo.length; i++) {
				ck_manuscript_designID = UIUtil.getSequenceNextValByDS(null,
						"s_ck_manuscript_design");
				HashVO[] vck_problemVO = UIUtil.getHashVoArrayByDS(null,
						"select * from ck_problem_dict where id ='"
								+ vo[i].getStringValue("id") + "'");
				designinsert.putFieldValue("id", ck_manuscript_designID);
				designinsert.putFieldValue("importtype", "问题词条");
				if (map.get(vo[i].getStringValue("id")) != null) {
					MessageBox.show(this, "底稿中已有【"
							+ vo[i].getStringValue("dictname") + "】这个词条请勿重复添加");
					return;
				}
				designinsert.putFieldValue("importid", vo[i]
						.getStringValue("id"));
				designinsert.putFieldValue("planid", impvo
						.getStringValue("planid"));
				designinsert.putFieldValue("schemeid", impvo
						.getStringValue("schemeid"));
				for (int j = 0; j < dict.length; j++) {
					if (dict[j].equals("dictname")) {
						designinsert.putFieldValue("checkPoints",
								vck_problemVO[0].getStringValue(dict[j]
										.toString()));
						designinsert.putFieldValue("dictname", vck_problemVO[0]
								.getStringValue(dict[j].toString()));
					} else if (dict[j].equals("id")) {
						designinsert.putFieldValue("dictid", vck_problemVO[0]
								.getStringValue(dict[j].toString()));
					} else {
						designinsert.putFieldValue(dict[j].toString(),
								vck_problemVO[0].getStringValue(dict[j]
										.toString()));
					}
				}
				list.add(designinsert.getSQL());
				for (int j = 0; j < CK_SCHEME_IMPL.length; j++) {
					for (int k = 0; k < implement.length; k++) {
						if (implement[k].equals("id")) {
							implementinsert.putFieldValue("id", UIUtil
									.getSequenceNextValByDS(null,
											"s_ck_scheme_implement"));
						} else if (implement[k].equals("parentid")) {
							implementinsert.putFieldValue("parentid",
									ck_manuscript_designID);
						} else if (implement[k].equals("implid")) {
							implementinsert.putFieldValue("implid",
									CK_SCHEME_IMPL[j].getStringValue("id"));
						} else {
							implementinsert.putFieldValue(implement[k]
									.toString(), ck_scheme_implementvo[0]
									.getStringValue(implement[k].toString()));
						}
					}
					list.add(implementinsert.getSQL());
				}
			}
			UIUtil.executeBatchByDS(null, list);
			itemList.QueryDataByCondition("implid='"
					+ impvo.getStringValue("id") + "'");
			BillVO[] itemListvo = itemList.getAllBillVOs();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < itemListvo.length; i++) {
				if (i == itemListvo.length - 1) {
					sb.append("'" + itemListvo[i].getStringValue("parentid")
							+ "'");
				} else {
					sb.append("'" + itemListvo[i].getStringValue("parentid")
							+ "'");
					sb.append(",");
				}

			}
			designlist.getBilllistPanel().QueryDataByCondition(
					"id in(" + sb.toString() + ")");
			MessageBox.show(this, "导入成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * zzl 修改词条【删除词条】
	 */
	private void ondelete_DG() {
		BillVO impvo = implList.getSelectedBillVO();
		BillVO designvo = designlist.getBilllistPanel().getSelectedBillVO();
		if (designvo == null) {
			MessageBox.show(this, "请选择一条数据");
			return;
		}
		try {
			UIUtil.executeUpdateByDS(null,
					"delete from ck_scheme_implement where schemeid='"
							+ impvo.getStringValue("schemeid")
							+ "' and parentid='"
							+ designvo.getStringValue("id") + "'");
			UIUtil.executeUpdateByDS(null,
					"delete from ck_manuscript_design where id='"
							+ designvo.getStringValue("id") + "'");
			designlist.getBilllistPanel().removeSelectedRow();
			MessageBox.show(this, "删除成功");
			itemList.reload();
			String name = implList.getTempletVO().getTempletcode();
			if (name.equals(parentTempletCode)) {
				itemList.QueryDataByCondition("implid='"
						+ impvo.getStringValue("id") + "'");
			} else {
				itemList.QueryDataByCondition("implid='"
						+ impvo.getStringValue("id") + "' and deptid='"
						+ impvo.getStringValue("deptid") + "'");
			}

		} catch (WLTRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			MessageBox.show(this, "删除异常");
			e.printStackTrace();
		}

	}

	/**
	 * 修改底稿zzl
	 */
	private void onnewCkDG() {
		BillVO[] vo = itemList.getAllBillVOs();
		if (vo.length <= 0) {
			MessageBox.show(this, "请选择要修改底稿的项目");
			return;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < vo.length; i++) {
			if (i == vo.length - 1) {
				sb.append("'" + vo[i].getStringValue("parentid") + "'");
			} else {
				sb.append("'" + vo[i].getStringValue("parentid") + "'");
				sb.append(",");
			}

		}
		designlist = new BillListDialog(this, "修改底稿",
				"CK_MANUSCRIPT_DESIGN_LCJ_E01", 1000, 600);
		btn_delete_DG = new WLTButton("删除");
		btn_new_DG = new WLTButton("导入词条");
		btn_delete_DG.addActionListener(this);
		btn_new_DG.addActionListener(this);
		designlist.getBilllistPanel().addBatchBillListButton(
				new WLTButton[] { btn_delete_DG, btn_new_DG });
		designlist.getBilllistPanel().repaintBillListButton();
		designlist.getBilllistPanel().QueryDataByCondition(
				"id in(" + sb.toString() + ")");
		designlist.setVisible(true);

	}

	/**
	 * 新增检查人员
	 */
	private void onCkUser() {
		String name = implList.getTempletVO().getTempletcode();
		if (name.equals(parentTempletCode)) {
			BillVO schemeListvo = implList.getSelectedBillVO();
			if (schemeListvo == null) {
				MessageBox.show(this, "请选择要新增人员的部门");
				return;
			}
			try {
				BillListDialog list = new BillListDialog(this, "新增检查人员",
						"V_PUB_USER_POST_ZZL_CODE1", 700, 600);
				list.setVisible(true);
				HashVO[] vo = UIUtil.getHashVoArrayByDS(null,
						"select * from ck_scheme_implement where schemeid='"
								+ schemeListvo.getStringValue("schemeid")
								+ "' and deptid='"
								+ schemeListvo.getStringValue("deptid") + "'");
				BillVO listvo = list.getBilllistPanel().getSelectedBillVO();
				if (listvo != null) {
					String username = UIUtil.getStringValueByDS(null,
							"select id from ck_scheme_implement where schemeid='"
									+ schemeListvo.getStringValue("schemeid")
									+ "' and deptid='"
									+ schemeListvo.getStringValue("deptid")
									+ "' and teamusers like'%" + ";"
									+ listvo.getStringValue("userid") + ";"
									+ "%'");
					if (username != null) {
						MessageBox.show(this, "已有【"
								+ listvo.getStringValue("username")
								+ "】检查人员请勿重复添加");
						return;
					}
					String teamusers = vo[0].getStringValue("teamusers");
					teamusers = teamusers + listvo.getStringValue("userid")
							+ ";";
					UIUtil.executeUpdateByDS(null,
							"update CK_SCHEME_IMPL set teamusers='" + teamusers
									+ "' where schemeid='"
									+ schemeListvo.getStringValue("schemeid")
									+ "' and deptid='"
									+ schemeListvo.getStringValue("deptid")
									+ "'");
					UIUtil.executeUpdateByDS(null,
							"update ck_scheme_implement set teamusers='"
									+ teamusers + "' where schemeid='"
									+ schemeListvo.getStringValue("schemeid")
									+ "' and deptid='"
									+ schemeListvo.getStringValue("deptid")
									+ "'");
					UIUtil.executeUpdateByDS(null,
							"update CK_MEMBER_WORK set teamusers='" + teamusers
									+ "' where schemeid='"
									+ schemeListvo.getStringValue("schemeid")
									+ "' and checkeddept=';"
									+ schemeListvo.getStringValue("deptid")
									+ ";'");
				}
				implList.refreshCurrSelectedRow();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			BillVO schemeListvo = schemeList.getSelectedBillVO();
			if (schemeListvo == null) {
				MessageBox.show(this, "请选择要新增人员的部门");
				return;
			}
			try {
				HashVO[] vo = UIUtil.getHashVoArrayByDS(null,
						"select * from ck_scheme_implement where schemeid='"
								+ schemeListvo.getStringValue("schemeid")
								+ "' and deptid='"
								+ schemeListvo.getStringValue("deptid") + "'");
				if (vo.length <= 1) {
					MessageBox.show(this, "请先导入本部门的人员信息，然后在新增检查人员");
					return;
				}
				BillListDialog list = new BillListDialog(this, "新增检查人员",
						"V_PUB_USER_POST_ZZL_CODE1", 700, 600);
				list.setVisible(true);
				BillVO listvo = list.getBilllistPanel().getSelectedBillVO();
				if (listvo != null) {
					String username = UIUtil.getStringValueByDS(null,
							"select id from ck_scheme_implement where schemeid='"
									+ schemeListvo.getStringValue("schemeid")
									+ "' and deptid='"
									+ schemeListvo.getStringValue("deptid")
									+ "' and teamusers like'%" + ";"
									+ listvo.getStringValue("userid") + ";"
									+ "%'");
					if (username != null) {
						MessageBox.show(this, "已有【"
								+ listvo.getStringValue("username")
								+ "】检查人员请勿重复添加");
						return;
					}
					String teamusers = vo[0].getStringValue("teamusers");
					teamusers = teamusers + listvo.getStringValue("userid")
							+ ";";
					UIUtil.executeUpdateByDS(null,
							"update ck_scheme_implement set teamusers='"
									+ teamusers + "' where schemeid='"
									+ schemeListvo.getStringValue("schemeid")
									+ "' and deptid='"
									+ schemeListvo.getStringValue("deptid")
									+ "'");
					UIUtil.executeUpdateByDS(null,
							"update ck_scheme_impl set teamusers='" + teamusers
									+ "' where schemeid='"
									+ schemeListvo.getStringValue("schemeid")
									+ "' and deptid='"
									+ schemeListvo.getStringValue("deptid")
									+ "'");
					UIUtil.executeUpdateByDS(null,
							"update CK_MEMBER_WORK set teamusers='" + teamusers
									+ "' where schemeid='"
									+ schemeListvo.getStringValue("schemeid")
									+ "' and checkeddept=';"
									+ schemeListvo.getStringValue("deptid")
									+ ";'");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 导入客户信息【zzl】
	 */
	private void onNewUser() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("请导入样本库");
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"(*.xls,*.xlxs)", "xls", "xlsx");
		chooser.addChoosableFileFilter(filter);
		int flag = chooser.showOpenDialog(this);
		if (flag == 1) {// 直接关闭窗口，未选择文件
			return;
		}
		File file = chooser.getSelectedFile();
		if (file == null) {
			return;
		}
		final String[][] datas = new ExcelUtil().getExcelFileData(file
				.getPath());
		// 客户名称、客户编号、借据编号、贷款方式、贷款品种、放款日期、到期日期、借据金额、借据余额、贷款期限(月)、
		// 执行利率(%)、表内欠息、表外欠息、五级形态、经办人、经办机构、结息方式、贷款用途
		if (datas == null || datas.length < 2) {
			MessageBox.show(this, "该文件无可用数据!");
			return;
		}
		BillVO schemeListvo = schemeList.getSelectedBillVO();
		String DeptName = null;
		try {
			DeptName = UIUtil.getStringValueByDS(null,
					"select name from pub_corp_dept where id='"
							+ schemeListvo.getStringValue("deptid") + "'");
		} catch (WLTRemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		TableDataStruct ck_scheme_implement;
		TableDataStruct ck_scheme_impl;
		List list = new ArrayList<String>();
		Map map = new HashMap<String, String>();
		try {
			ck_scheme_implement = UIUtil.getTableDataStructByDS(null,
					"select * from ck_scheme_implement where 1=2");
			String[] str_cols = ck_scheme_implement.getHeaderName();
			ck_scheme_impl = UIUtil.getTableDataStructByDS(null,
					"select * from ck_scheme_impl where 1=2");
			String[] str_colsimpl = ck_scheme_impl.getHeaderName();
			InsertSQLBuilder insertscheme = new InsertSQLBuilder(
					"ck_scheme_implement");
			InsertSQLBuilder insert = new InsertSQLBuilder("ck_scheme_impl");
			String insertSequence = null;// ICheckUIUtil.getSequenceNextVal();
			HashVO[] schemeVO = UIUtil.getHashVoArrayByDS(null,
					"select * from ck_scheme_impl where schemeid='"
							+ schemeListvo.getStringValue("schemeid") + "'");
			HashVO[] implVO = UIUtil.getHashVoArrayByDS(null,
					"select * from ck_scheme_implement where schemeid='"
							+ schemeListvo.getStringValue("schemeid")
							+ "' group by parentid");
			for (int i = 0; i < datas.length; i++) {
				map
						.put(datas[i][15].toString().trim(), datas[i][15]
								.toString());
			}
			if (map.get(DeptName.trim()) == null) {
				MessageBox.show(this, "Excel表格中没有【" + DeptName + "】这个部门的数据");
				return;
			}
			for (int i = 1; i < datas.length; i++) {
				String deptname = datas[i][15];
				if (deptname.trim().equals(DeptName.trim())) {
					for (int j = 0; j < str_colsimpl.length; j++) {
						if (str_colsimpl[j].equals("createdept")) {
							break;
						} else if (str_colsimpl[j].equals("id")) {
							insertSequence = ICheckUIUtil.getSequenceNextVal();
							insert.putFieldValue("id", insertSequence);
						} else if (str_colsimpl[j].equals("deptid")) {
							insert.putFieldValue("deptid", schemeListvo
									.getStringValue("deptid"));
						} else if (str_colsimpl[j].equals("teamusers")) {
							insert.putFieldValue("teamusers", ";" + loginUserid
									+ ";");
						} else {
							insert.putFieldValue(str_colsimpl[j].toString(),
									schemeVO[0].getStringValue(str_colsimpl[j]
											.toString()));
						}
					}
					for (int j = 1; j < 19; j++) {
						insert.putFieldValue("c" + j, datas[i][j - 1]
								.toString().trim());
					}
					for (int m = 0; m < implVO.length; m++) {
						for (int j = 0; j < str_cols.length; j++) {
							if (str_cols[j].equals("id")) {
								insertscheme.putFieldValue("id", ICheckUIUtil
										.getSequenceNextVal());
							} else if (str_cols[j].equals("deptid")) {
								insertscheme.putFieldValue("deptid",
										schemeListvo.getStringValue("deptid"));
							} else if (str_cols[j].equals("implid")) {
								insertscheme.putFieldValue("implid",
										insertSequence);
							} else if (str_cols[j].equals("parentid")) {
								insertscheme.putFieldValue("parentid",
										implVO[m].getStringValue("parentid"));
							} else if (str_cols[j].equals("teamusers")) {
								insertscheme.putFieldValue("teamusers", ";"
										+ loginUserid + ";");
							} else {
								insertscheme
										.putFieldValue(
												str_cols[j].toString(),
												implVO[0]
														.getStringValue(str_cols[j]
																.toString()));
							}
						}
						list.add(insertscheme.getSQL());
					}
					list.add(insert.getSQL());
				} else {

				}
			}
			UIUtil.executeBatchByDS(null, list);
			implList.reload();
			implList.QueryDataByCondition("schemeid='"
					+ schemeListvo.getStringValue("schemeid")
					+ "' and deptid='" + schemeListvo.getStringValue("deptid")
					+ "'");
			MessageBox.show(this, "导入成功");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 新增检查部门【zzl】
	 */
	private void onNewDept() {
		String name = implList.getTempletVO().getTempletcode();
		if (name.equals(parentTempletCode)) {
			BillVO implListvo = implList.getSelectedBillVO();
			if (implListvo == null) {
				MessageBox.show(this, "请点选方案名称下任意一条数据");
				return;
			}
			schemeid = implListvo.getStringValue("schemeid");
			String planid = implListvo.getStringValue("planid");
			List list = new ArrayList<String>();
			Map deptMap = new HashMap<String, String>();
			BillTreePanel tree = new BillTreePanel("PUB_CORP_DEPT_CODE1");
			tree.queryDataByCondition("1=1");
			tree.addBillTreeSelectListener(this);
			BillDialog card = new BillDialog(this, 600, 600);
			card.addConfirmButtonPanel();
			card.add(tree);
			card.setVisible(true);
			BillVO treeVo = tree.getSelectedVO();
			int count = card.intlickThisConfirmBtn();
			if (count > 0) {
				try {
					BillVO[] impVo = implList.getAllBillVOs();
					for (int i = 0; i < impVo.length; i++) {
						deptMap.put(impVo[i].getStringValue("deptid"), impVo[i]
								.getStringValue("deptid"));
					}
					if (deptMap.get(treeVo.getStringValue("id")) == null
							|| deptMap.get(treeVo.getStringValue("id")).equals(
									"")) {
						TableDataStruct tabstrct = UIUtil
								.getTableDataStructByDS(null,
										"select * from CK_SCHEME_IMPL where 1=2");// 得到表中的字段
						String[] str_cols = tabstrct.getHeaderName();
						TableDataStruct implement = UIUtil
								.getTableDataStructByDS(null,
										"select * from ck_scheme_implement where 1=2");// 得到表中的字段
						String[] implementStr = implement.getHeaderName();
						TableDataStruct work = UIUtil.getTableDataStructByDS(
								null, "select * from CK_MEMBER_WORK where 1=2");// 得到表中的字段
						String[] workStr = work.getHeaderName();
						InsertSQLBuilder insert = new InsertSQLBuilder(
								"CK_SCHEME_IMPL");
						InsertSQLBuilder insertimplement = new InsertSQLBuilder(
								"ck_scheme_implement");
						InsertSQLBuilder insertwork = new InsertSQLBuilder(
								"CK_MEMBER_WORK");
						String id = ICheckUIUtil.getSequenceNextVal();
						for (int i = 0; i < str_cols.length; i++) {
							if (str_cols[i].equals("id")) {
								insert.putFieldValue("id", id);
							} else if (str_cols[i].equals("deptid")) {
								insert.putFieldValue("deptid", treeVo
										.getStringValue("id"));
							} else if (str_cols[i].equals("status")) {
								insert.putFieldValue("status", "未执行");
							} else if (str_cols[i].equals("teamusers")) {
								insert.putFieldValue("teamusers", ";"
										+ loginUserid + ";");
							} else {
								insert.putFieldValue(str_cols[i].toString(),
										impVo[0].getStringValue(str_cols[i]
												.toString()));
							}
						}
						list.add(insert.getSQL());
						HashVO[] implementVO = UIUtil.getHashVoArrayByDS(null,
								"select * from ck_scheme_implement where schemeid='"
										+ impVo[0].getStringValue("schemeid")
										+ "' group by parentid");
						for (int i = 0; i < implementVO.length; i++) {
							for (int j = 0; j < implementStr.length; j++) {
								if (implementStr[j].equals("id")) {
									insertimplement.putFieldValue("id",
											ICheckUIUtil.getSequenceNextVal());
								} else if (implementStr[j].equals("implid")) {
									insertimplement.putFieldValue("implid", id);
								} else if (implementStr[j].equals("deptid")) {
									insertimplement.putFieldValue("deptid",
											treeVo.getStringValue("id"));
								} else if (implementStr[j].equals("teamusers")) {
									insertimplement.putFieldValue("teamusers",
											";" + loginUserid + ";");
								} else {
									insertimplement
											.putFieldValue(
													implementStr[j].toString(),
													implementVO[i]
															.getStringValue(implementStr[j]
																	.toString()));
								}

							}
							list.add(insertimplement.getSQL());
						}
						String workid = UIUtil.getSequenceNextValByDS(null,
								"S_CK_MEMBER_WORK");
						for (int i = 0; i < workStr.length; i++) {
							if (workStr[i].equals("id")) {
								insertwork.putFieldValue("id", workid);
							}
							if (workStr[i].equals("schemeid")) {
								insertwork.putFieldValue("schemeid", schemeid);
							}
							if (workStr[i].equals("planid")) {
								insertwork.putFieldValue("planid", planid);
							}
							if (workStr[i].equals("leader")) {
								insertwork.putFieldValue("leader", loginUserid);
							}
							if (workStr[i].equals("teamusers")) {
								insertwork.putFieldValue("teamusers", ";"
										+ loginUserid + ";");
							}
							if (workStr[i].equals("checkeddept")) {
								insertwork.putFieldValue("checkeddept", ";"
										+ treeVo.getStringValue("id") + ";");
							}
							if (workStr[i].equals("createdept")) {
								insertwork.putFieldValue("createdept",
										ClientEnvironment.getInstance()
												.getLoginUserDeptId());
							}
							if (workStr[i].equals("creater")) {
								insertwork
										.putFieldValue("creater", loginUserid);
							}
							if (workStr[i].equals("createdate")) {
								insertwork.putFieldValue("createdate", UIUtil
										.getCurrDate());
							}
						}
						list.add(insertwork.getSQL());
						UIUtil.executeBatchByDS(null, list);
						implList.reload();
						implList.QueryDataByCondition("schemeid='" + schemeid
								+ "' and (teamusers like '%;" + loginUserid
								+ ";%' or leader2='" + loginUserid
								+ "' or leader ='" + loginUserid
								+ "' or referee like '%;" + loginUserid
								+ ";%')");
					} else {
						MessageBox.show(this, "已有【"
								+ treeVo.getStringValue("name")
								+ "】这个部门，请勿重复添加");
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} else {
			BillVO schemeListvo = schemeList.getSelectedBillVO();
			if (schemeListvo == null) {
				MessageBox.show(this, "请点选方案名称下任意一条数据");
				return;
			}
			schemeid = schemeListvo.getStringValue("schemeid");
			String planid = schemeListvo.getStringValue("planid");
			Map deptMap = new HashMap<String, String>();
			BillTreePanel tree = new BillTreePanel("PUB_CORP_DEPT_CODE1");
			tree.queryDataByCondition("1=1");
			tree.addBillTreeSelectListener(this);
			BillDialog card = new BillDialog(this, 600, 600);
			card.addConfirmButtonPanel();
			card.add(tree);
			card.setVisible(true);
			BillVO treeVo = tree.getSelectedVO();
			int count = card.intlickThisConfirmBtn();
			if (count > 0) {
				try {
					HashVO[] impVo = UIUtil.getHashVoArrayByDS(null,
							"select * from ck_scheme_implement where schemeid='"
									+ schemeid + "' group by deptid");
					for (int i = 0; i < impVo.length; i++) {
						deptMap.put(impVo[i].getStringValue("deptid"), impVo[i]
								.getStringValue("deptid"));
					}
					if (deptMap.get(treeVo.getStringValue("id")) == null
							|| deptMap.get(treeVo.getStringValue("id")).equals(
									"")) {
						TableDataStruct tabstrct = UIUtil
								.getTableDataStructByDS(null,
										"select * from ck_scheme_implement where 1=2");// 得到表中的字段
						String[] str_cols = tabstrct.getHeaderName();
						InsertSQLBuilder insert = new InsertSQLBuilder(
								"ck_scheme_implement");
						for (int i = 0; i < str_cols.length; i++) {
							if (str_cols[i].equals("id")) {
								insert.putFieldValue("id", ICheckUIUtil
										.getSequenceNextVal());
							} else if (str_cols[i].equals("implid")) {
								insert
										.putFieldValue(str_cols[i].toString(),
												"");
							} else if (str_cols[i].equals("deptid")) {
								insert.putFieldValue(str_cols[i].toString(),
										treeVo.getStringValue("id"));
							} else if (str_cols[i].equals("teamusers")) {
								insert.putFieldValue("teamusers", ";"
										+ loginUserid + ";");
							} else {
								insert.putFieldValue(str_cols[i].toString(),
										impVo[0].getStringValue(str_cols[i]
												.toString()));
							}
						}
						TableDataStruct work = UIUtil.getTableDataStructByDS(
								null, "select * from CK_MEMBER_WORK where 1=2");// 得到表中的字段
						String[] workStr = work.getHeaderName();
						InsertSQLBuilder insertwork = new InsertSQLBuilder(
								"CK_MEMBER_WORK");
						String workid = UIUtil.getSequenceNextValByDS(null,
								"S_CK_MEMBER_WORK");
						for (int i = 0; i < workStr.length; i++) {
							if (workStr[i].equals("id")) {
								insertwork.putFieldValue("id", workid);
							}
							if (workStr[i].equals("schemeid")) {
								insertwork.putFieldValue("schemeid", schemeid);
							}
							if (workStr[i].equals("planid")) {
								insertwork.putFieldValue("planid", planid);
							}
							if (workStr[i].equals("leader")) {
								insertwork.putFieldValue("leader", loginUserid);
							}
							if (workStr[i].equals("teamusers")) {
								insertwork.putFieldValue("teamusers", ";"
										+ loginUserid + ";");
							}
							if (workStr[i].equals("checkeddept")) {
								insertwork.putFieldValue("checkeddept", ";"
										+ treeVo.getStringValue("id") + ";");
							}
							if (workStr[i].equals("createdept")) {
								insertwork.putFieldValue("createdept",
										ClientEnvironment.getInstance()
												.getLoginUserDeptId());
							}
							if (workStr[i].equals("creater")) {
								insertwork
										.putFieldValue("creater", loginUserid);
							}
							if (workStr[i].equals("createdate")) {
								insertwork.putFieldValue("createdate", UIUtil
										.getCurrDate());
							}
						}
						UIUtil.executeUpdateByDS(null, insertwork.getSQL());
						UIUtil.executeUpdateByDS(null, insert.getSQL());
						schemeList.reload();
						schemeList.QueryDataByCondition("schemeid='" + schemeid
								+ "' and (teamusers like '%;" + loginUserid
								+ ";%' or leader2='" + loginUserid
								+ "' or leader ='" + loginUserid
								+ "' or referee like '%;" + loginUserid
								+ ";%')");

					} else {
						MessageBox.show(this, "已有【"
								+ treeVo.getStringValue("name")
								+ "】这个部门，请勿重复添加");
						return;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 复制客户信息 zzl [2017-04-20]
	 */
	private void onCopyxx() {
		final BillListDialog listDialog = new BillListDialog(this, "请选择要复制的底稿",
				"V_XDDGCOPY_CODE1", 950, 500);
		List cooylist = new ArrayList();
		Map map = new HashMap<String, String>();
		String id = null;
		UpdateSQLBuilder update = new UpdateSQLBuilder("CK_SCHEME_IMPL");
		try {
			id = UIUtil.getStringValueByDS(null,
					"select schemeid from CK_SCHEME_IMPL where id='" + implid
							+ "'");
			listDialog.setVisible(true);
			int ButtonID = listDialog.ButtonID();
			HashVO[] vo = UIUtil.getHashVoArrayByDS(null,
					"select * from CK_SCHEME_IMPL where schemeid='" + schemeid
							+ "' and deptid='" + impdeptid + "'");
			for (int i = 0; i < vo.length; i++) {
				map.put(vo[i].getStringValue("c1").trim(), vo[i]
						.getStringValue("c1"));
			}
			BillVO[] vos = listDialog.getBilllistPanel().getSelectedBillVOs();
			if (vos != null && ButtonID == 1) {
				for (int j = 0; j < vos.length; j++) {
					if (map.get(vos[j].getStringValue("c1").trim()) != null) {
						String impid = UIUtil.getStringValueByDS(null,
								"select * from CK_SCHEME_IMPL where schemeid='"
										+ schemeid + "' and deptid='"
										+ impdeptid + "' and c1='"
										+ vos[j].getStringValue("c1") + "'");
						TableDataStruct tabstrct = UIUtil
								.getTableDataStructByDS(null,
										"select * from CK_SCHEME_IMPL where 1=2");
						String[] str_cols = tabstrct.getHeaderName();
						update.setWhereCondition("id=" + impid);
						HashVO[] hvo = UIUtil
								.getHashVoArrayByDS(
										null,
										"select * from CK_SCHEME_IMPL where id='"
												+ vos[j]
														.getStringValue("implid")
												+ "'");
						int a = 0;
						for (int s = 0; s < str_cols.length; s++) {
							if (str_cols[s].equals("status")) {
								update.putFieldValue(str_cols[s].toString(),
										hvo[0].getStringValue(str_cols[s]
												.toString()));
							}
							if (str_cols[s].equals("usera1")) {
								a = s;
								break;
							}
						}
						for (int k = a; k < str_cols.length; k++) {
							update.putFieldValue(str_cols[k].toString(), hvo[0]
									.getStringValue(str_cols[k].toString()));
						}
						cooylist.add(update.getSQL());
					}
				}
				UIUtil.executeBatchByDS(null, cooylist);
				implList.reload();
				implList.QueryDataByCondition("schemeid='" + id
						+ "' and (teamusers like '%;" + loginUserid
						+ ";%' or leader2='" + loginUserid + "' or leader ='"
						+ loginUserid + "' or referee like '%;" + loginUserid
						+ ";%') and deptid='" + impdeptid + "'");
				MessageBox.show(this, "复制成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 删除关联客户 zzl[2017-4-17]
	 */
	private void onDelete() {
		BillVO vo = implList.getSelectedBillVO();
		UpdateSQLBuilder update = new UpdateSQLBuilder("CK_SCHEME_IMPL");
		update.setWhereCondition("id=" + vo.getStringValue("id"));
		update.putFieldValue("refc1", "");
		update.putFieldValue("refimplid", "");
		try {
			UIUtil.executeUpdateByDS(null, update.getSQL());
			implList.refreshCurrSelectedRow();
		} catch (WLTRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 拷贝问题【zzl 2017-3-22】
	 */
	private void onCopyWT() {
		if (implid == null) {
			MessageBox.show(this, "请选择要复制问题的被检查单位");
			return;
		}
		BillListDialog listDialog = new BillListDialog(this, "请选择要复制的问题",
				"V_CK_SCHEME_IMPLEMENT_ZZL_E01_1", 950, 500);
		List cooylist = new ArrayList();
		Map map = new HashMap<Object, Object>();
		String id = null;
		try {
			id = UIUtil.getStringValueByDS(null,
					"select schemeid from CK_SCHEME_IMPL where id='" + implid
							+ "'");
			HashVO[] implVO = UIUtil.getHashVoArrayByDS(null,
					"select * from CK_SCHEME_IMPL where id='" + implid + "'");
			HashVO[] vo = UIUtil.getHashVoArrayByDS(null,
					"select * from V_CK_SCHEME_IMPLEMENT where implid='"
							+ implid + "'");
			for (int i = 0; i < vo.length; i++) {
				map.put(vo[i].getStringValue("checkpoints"), vo[i]);
			}
			UpdateSQLBuilder update = new UpdateSQLBuilder(
					"ck_scheme_implement");
			InsertSQLBuilder insert = new InsertSQLBuilder("ck_problem_info");
			listDialog.setVisible(true);
			BillVO[] bvo = listDialog.getBilllistPanel().getSelectedBillVOs();
			int ButtonID = listDialog.ButtonID();
			if (bvo != null && ButtonID == 1) {
				for (int j = 0; j < bvo.length; j++) {
					if (map.get(bvo[j].getStringValue("checkpoints")) != null) {
						HashVO vo2 = (HashVO) map.get(bvo[j]
								.getStringValue("checkpoints"));
						String v_ck_id = UIUtil
								.getStringValueByDS(
										null,
										"select * from V_CK_SCHEME_IMPLEMENT where implid='"
												+ implid
												+ "' and checkpoints='"
												+ vo2
														.getStringValue("checkpoints")
												+ "' and thirdname='"
												+ vo2
														.getStringValue("thirdname")
												+ "'");
						update.setWhereCondition("id=" + v_ck_id);
						update.putFieldValue("control", bvo[j]
								.getStringValue("control"));
						update.putFieldValue("result", bvo[j]
								.getStringValue("result"));
						cooylist.add(update.getSQL());
						TableDataStruct tabstrct = UIUtil
								.getTableDataStructByDS(null,
										"select * from ck_problem_info where 1=2");// 得到表中的字段
						String[] str_cols = tabstrct.getHeaderName();
						HashVO[] infoVo = UIUtil.getHashVoArrayByDS(null,
								"select * from ck_problem_info where 1=1  and (parentid='"
										+ bvo[j].getStringValue("id") + "') ");
						for (int f = 0; f < infoVo.length; f++) {
							String seqId = UIUtil.getSequenceNextValByDS(null,
									"s_ck_problem_info");
							for (int k = 0; k < str_cols.length; k++) {
								if (str_cols[k].equals("id")) {
									insert.putFieldValue("id", seqId);
								} else if (str_cols[k].equals("parentid")) {
									insert.putFieldValue("parentid", v_ck_id);
								} else if (str_cols[k].equals("implid")) {
									insert.putFieldValue("implid", implid);
								} else if (str_cols[k].equals("schemeid")) {
									insert.putFieldValue("schemeid", id);
								} else if (str_cols[k].equals("planid")) {
									insert.putFieldValue("planid", implVO[0]
											.getStringValue("planid"));
								} else if (str_cols[k].equals("deptid")) {
									insert.putFieldValue("deptid", implVO[0]
											.getStringValue("deptid"));
								} else {
									insert.putFieldValue(
											str_cols[k].toString(), infoVo[f]
													.getStringValue(str_cols[k]
															.toString()));
								}
							}
							cooylist.add(insert.getSQL());
						}

					}
				}
				UIUtil.executeBatchByDS(null, cooylist);
				UpdateSQLBuilder update_impl = new UpdateSQLBuilder(
						"CK_SCHEME_IMPL");
				update_impl.setWhereCondition("id=" + implid);
				update_impl.putFieldValue("STATUS", "执行中");
				UIUtil.executeUpdateByDS(null, update_impl.getSQL());
				implList.reload();
				implList.QueryDataByCondition("schemeid='" + id
						+ "' and (teamusers like '%;" + loginUserid
						+ ";%' or leader2='" + loginUserid + "' or leader ='"
						+ loginUserid + "' or referee like '%;" + loginUserid
						+ ";%')");
				itemList.reload();
				itemList.QueryDataByCondition("implid='" + implid + "'");
				MessageBox.show(this, "复制问题成功");
			}

		} catch (Exception e) {
			MessageBox.show(this, "复制问题失败");
			e.printStackTrace();
		}

	}

	/**
	 * 拷贝信贷问题【zzl 2017-3-22】
	 */
	private void onCopyXDWT() {
		if (implid == null) {
			MessageBox.show(this, "请选择要复制问题的被检查单位");
			return;
		}
		BillListDialog listDialog = new BillListDialog(this, "请选择要复制的问题",
				"V_XDCOPYWT_ZZL_CODE1", 950, 500);
		listDialog.getBilllistPanel().addBillListHtmlHrefListener(this);
		List cooylist = new ArrayList();
		Map map = new HashMap<Object, Object>();
		String id = null;
		try {
			id = UIUtil.getStringValueByDS(null,
					"select schemeid from CK_SCHEME_IMPL where id='" + implid
							+ "'");
			HashVO[] implVO = UIUtil.getHashVoArrayByDS(null,
					"select * from CK_SCHEME_IMPL where id='" + implid + "'");
			HashVO[] vo = UIUtil.getHashVoArrayByDS(null,
					"select * from V_CK_SCHEME_IMPLEMENT where implid='"
							+ implid + "'");
			for (int i = 0; i < vo.length; i++) {
				map.put(vo[i].getStringValue("checkpoints"), vo[i]);
			}
			UpdateSQLBuilder update = new UpdateSQLBuilder(
					"ck_scheme_implement");
			InsertSQLBuilder insert = new InsertSQLBuilder("ck_problem_info");
			listDialog.setVisible(true);
			BillVO[] bvo = listDialog.getBilllistPanel().getSelectedBillVOs();
			int ButtonID = listDialog.ButtonID();
			if (bvo != null && ButtonID == 1) {
				for (int j = 0; j < bvo.length; j++) {
					if (map.get(bvo[j].getStringValue("checkpoints")) != null) {
						HashVO vo2 = (HashVO) map.get(bvo[j]
								.getStringValue("checkpoints"));
						String v_ck_id = UIUtil
								.getStringValueByDS(
										null,
										"select id from V_CK_SCHEME_IMPLEMENT where implid='"
												+ implid
												+ "' and checkpoints='"
												+ vo2
														.getStringValue("checkpoints")
												+ "' and thirdname='"
												+ vo2
														.getStringValue("thirdname")
												+ "'");
						update.setWhereCondition("id=" + v_ck_id);
						update.putFieldValue("control", bvo[j]
								.getStringValue("control"));
						update.putFieldValue("result", bvo[j]
								.getStringValue("result"));
						cooylist.add(update.getSQL());
						TableDataStruct tabstrct = UIUtil
								.getTableDataStructByDS(null,
										"select * from ck_problem_info where 1=2");// 得到表中的字段
						String[] str_cols = tabstrct.getHeaderName();
						HashVO[] infoVo = UIUtil.getHashVoArrayByDS(null,
								"select * from ck_problem_info where 1=1  and (parentid='"
										+ bvo[j].getStringValue("id") + "') ");
						for (int f = 0; f < infoVo.length; f++) {
							String seqId = UIUtil.getSequenceNextValByDS(null,
									"s_ck_problem_info");
							for (int k = 0; k < str_cols.length; k++) {
								if (str_cols[k].equals("id")) {
									insert.putFieldValue("id", seqId);
								} else if (str_cols[k].equals("parentid")) {
									insert.putFieldValue("parentid", v_ck_id);
								} else if (str_cols[k].equals("implid")) {
									insert.putFieldValue("implid", implid);
								} else if (str_cols[k].equals("schemeid")) {
									insert.putFieldValue("schemeid", id);
								} else if (str_cols[k].equals("planid")) {
									insert.putFieldValue("planid", implVO[0]
											.getStringValue("planid"));
								} else if (str_cols[k].equals("deptid")) {
									insert.putFieldValue("deptid", implVO[0]
											.getStringValue("deptid"));
								} else {
									insert.putFieldValue(
											str_cols[k].toString(), infoVo[f]
													.getStringValue(str_cols[k]
															.toString()));
								}
							}
							cooylist.add(insert.getSQL());
						}

					}
				}
				UIUtil.executeBatchByDS(null, cooylist);
				UpdateSQLBuilder update_impl = new UpdateSQLBuilder(
						"CK_SCHEME_IMPL");
				update_impl.setWhereCondition("id=" + implid);
				update_impl.putFieldValue("STATUS", "执行中");
				UIUtil.executeUpdateByDS(null, update_impl.getSQL());
				implList.reload();
				implList.QueryDataByCondition("schemeid='" + id
						+ "' and deptid='" + impdeptid + "'");
				itemList.reload();
				itemList.QueryDataByCondition("implid='" + implid + "'");
				MessageBox.show(this, "复制问题成功");
			}

		} catch (Exception e) {
			MessageBox.show(this, "复制问题失败");
			e.printStackTrace();
		}

	}

	/**
	 * 拷贝底稿【zzl 2017-2-10】
	 */
	private void onCopy() {
		if (implid == null) {
			MessageBox.show(this, "请选择要复制底稿的被检查单位");
			return;
		}
		final BillListDialog listDialog = new BillListDialog(this, "请选择要复制的底稿",
				parentTempletCode, 950, 500);
		try {
			final String id = UIUtil.getStringValueByDS(null,
					"select schemeid from CK_SCHEME_IMPL where id='" + implid
							+ "'");
			final HashVO[] implVO = UIUtil.getHashVoArrayByDS(null,
					"select * from CK_SCHEME_IMPL where id='" + implid + "'");
			listDialog.getBilllistPanel().QueryDataByCondition(
					"schemeid='" + id
							+ "' and status='执行中' and (teamusers like '%;"
							+ loginUserid + ";%' or leader2='" + loginUserid
							+ "' or leader ='" + loginUserid
							+ "' or referee like '%;" + loginUserid + ";%')");
			listDialog.setVisible(true);
			final BillVO vo = listDialog.getBilllistPanel().getSelectedBillVO();
			int ButtonID = listDialog.ButtonID();
			if (vo != null && ButtonID == 1) {
				final BillDialog dg = new BillDialog(listDialog, "选择要复制底稿的类型",
						300, 200);
				dg.setLayout(null);
				btn_qb = new WLTButton("全部");
				btn_xc = new WLTButton("现场检查");
				btn_fxc = new WLTButton("非现场检查");
				btn_qb.setBounds(90, 0, 100, 30); //
				btn_xc.setBounds(90, 35, 100, 30); //
				btn_fxc.setBounds(90, 70, 100, 30); //
				dg.add(btn_qb);
				dg.add(btn_xc);
				dg.add(btn_fxc);
				btn_qb.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent actionevent) {
						System.out.println(">>>>>>>>全部>>>>>>>>>");
						try {
							getOnCopySql(dg, vo, "", implVO, id);
						} catch (Exception e) {
							MessageBox.show(listDialog, "复制底稿失败");
							e.printStackTrace();
						}

					}
				});
				btn_xc.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent actionevent) {
						System.out.println(">>>>>>>>现场检查>>>>>>>>>");
						try {
							boolean falg=getOnCopySql(dg, vo, "现场检查", implVO, id);
							if(falg){
								MessageBox.show(listDialog, "复制底稿成功");
							}else{
								MessageBox.show(listDialog, "没有现场检查底稿");
							}
							
						} catch (Exception e) {
							MessageBox.show(listDialog, "复制底稿失败");
							e.printStackTrace();
						}

					}
				});
				btn_fxc.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent actionevent) {
						System.out.println(">>>>>>>>非现场检查>>>>>>>>>");
						try {
							boolean falg=getOnCopySql(dg, vo, "非现场检查", implVO, id);
							if(falg){
								MessageBox.show(listDialog, "复制底稿成功");
							}else{
								MessageBox.show(listDialog, "没有非现场检查底稿");
							}
						} catch (Exception e) {
							MessageBox.show(listDialog, "复制底稿失败");
							e.printStackTrace();
						}

					}
				});

				dg.setVisible(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
/**
 * zzl[昌吉提出复制底稿要有选择  可以选择现场，非现场]
 * @param dg
 * @param vo
 * @param str
 * @param implVO
 * @param id
 */
	private boolean getOnCopySql(BillDialog dg, BillVO vo, String str,
			HashVO[] implVO, String id) {
		UpdateSQLBuilder update = new UpdateSQLBuilder("ck_scheme_implement");
		InsertSQLBuilder insert = new InsertSQLBuilder("ck_problem_info");
		List cooylist = new ArrayList();
		System.out.println(">>>>>>>>全部>>>>>>>>>");
		try {
			System.out.println(">>>>>>>>>>>>>>>" + vo.getStringValue("id"));
			HashVO[] countvo = null;
			if (str == null || str.equals(null) || str.equals("") || str.equals("null")) {
				countvo = UIUtil.getHashVoArrayByDS(null,
						"select * from V_CK_SCHEME_IMPLEMENT where implid='"
								+ vo.getStringValue("id") + "'");
			} else {
				countvo = UIUtil.getHashVoArrayByDS(null,
						"select * from V_CK_SCHEME_IMPLEMENT where implid='"
								+ vo.getStringValue("id") + "' and checkMode='"
								+ str + "'");
			}
			if(countvo.length<=0){
				return false;
				
			}

			for (int i = 0; i < countvo.length; i++) {
				String v_ck_id = UIUtil.getStringValueByDS(null,
						"select * from V_CK_SCHEME_IMPLEMENT where implid='"
								+ implid + "' and checkpoints='"
								+ countvo[i].getStringValue("checkpoints")
								+ "' and thirdname='"
								+ countvo[i].getStringValue("thirdname") + "'");
				update.setWhereCondition("id=" + v_ck_id);
				update.putFieldValue("control", countvo[i]
						.getStringValue("control"));
				update.putFieldValue("result", countvo[i]
						.getStringValue("result"));
				cooylist.add(update.getSQL());
				TableDataStruct tabstrct = UIUtil.getTableDataStructByDS(null,
						"select * from ck_problem_info where 1=2");// 得到表中的字段
				String[] str_cols = tabstrct.getHeaderName();
				HashVO[] infoVo = UIUtil.getHashVoArrayByDS(null,
						"select * from ck_problem_info where 1=1  and (parentid='"
								+ countvo[i].getStringValue("id") + "') ");
				for (int f = 0; f < infoVo.length; f++) {
					String seqId = ICheckUIUtil.getSequenceNextVal();
					for (int j = 0; j < str_cols.length; j++) {
						if (str_cols[j].equals("id")) {
							insert.putFieldValue("id", seqId);
						} else if (str_cols[j].equals("parentid")) {
							insert.putFieldValue("parentid", v_ck_id);
						} else if (str_cols[j].equals("implid")) {
							insert.putFieldValue("implid", implid);
						} else if (str_cols[j].equals("schemeid")) {
							insert.putFieldValue("schemeid", id);
						} else if (str_cols[j].equals("planid")) {
							insert.putFieldValue("planid", implVO[0]
									.getStringValue("planid"));
						} else if (str_cols[j].equals("deptid")) {
							insert.putFieldValue("deptid", implVO[0]
									.getStringValue("deptid"));
						}else if(str_cols[j].equals("dictid") || str_cols[j].equals("problemInfo")){
							insert.putFieldValue(str_cols[j].toString(),
									infoVo[f].getStringValue(str_cols[j]
											.toString()));
						}else if(str_cols[j].equals("createuserid")){
							insert.putFieldValue(str_cols[j].toString(),
									infoVo[f].getStringValue(str_cols[j]
											.toString()));
						}else if(str_cols[j].equals("createusername")){
							insert.putFieldValue(str_cols[j].toString(),
									infoVo[f].getStringValue(str_cols[j]
											.toString()));
						}else if(str_cols[j].equals("createdeptid")){
							insert.putFieldValue(str_cols[j].toString(),
									infoVo[f].getStringValue(str_cols[j]
											.toString()));
						}
					}
					cooylist.add(insert.getSQL());
				}
			}
			UIUtil.executeBatchByDS(null, cooylist);
			UpdateSQLBuilder update_impl = new UpdateSQLBuilder(
					"CK_SCHEME_IMPL");
			update_impl.setWhereCondition("id=" + implid);
			update_impl.putFieldValue("STATUS", "执行中");
			UIUtil.executeUpdateByDS(null, update_impl.getSQL());
			implList.reload();
			implList.QueryDataByCondition("schemeid='" + id
					+ "' and (teamusers like '%;" + loginUserid
					+ ";%' or leader2='" + loginUserid + "' or leader ='"
					+ loginUserid + "' or referee like '%;" + loginUserid
					+ ";%')");
			itemList.reload();
			itemList.QueryDataByCondition("implid='" + implid + "'");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		dg.dispose();
		return true;

	}

	/**
	 * 拷贝底稿信贷【zzl 2017-3-22】
	 */
	private void onCopyXD() {
		if (implid == null) {
			MessageBox.show(this, "请选择要复制底稿的客户名称");
			return;
		}
		final BillListDialog listDialog = new BillListDialog(this, "请选择要复制的底稿",
				"V_XDDGCOPY_CODE1", 950, 500);
		List cooylist = new ArrayList();
		String id = null;
		UpdateSQLBuilder update = new UpdateSQLBuilder("ck_scheme_implement");
		InsertSQLBuilder insert = new InsertSQLBuilder("ck_problem_info");
		try {
			id = UIUtil.getStringValueByDS(null,
					"select schemeid from CK_SCHEME_IMPL where id='" + implid
							+ "'");
			HashVO[] implVO = UIUtil.getHashVoArrayByDS(null,
					"select * from CK_SCHEME_IMPL where id='" + implid + "'");
			listDialog.getBilllistPanel().QueryDataByCondition(
					"schemeid='" + id
							+ "' and status='执行中' and (teamusers like '%;"
							+ loginUserid + ";%' or leader2='" + loginUserid
							+ "' or leader ='" + loginUserid
							+ "' or referee like '%;" + loginUserid + ";%')");
			listDialog.setVisible(true);
			int ButtonID = listDialog.ButtonID();
			BillVO vo = listDialog.getBilllistPanel().getSelectedBillVO();
			if (vo != null && ButtonID == 1) {
				HashVO[] countvo = UIUtil.getHashVoArrayByDS(null,
						"select * from V_CK_SCHEME_IMPLEMENT where implid='"
								+ vo.getStringValue("implid") + "'");
				for (int i = 0; i < countvo.length; i++) {
					String v_ck_id = UIUtil.getStringValueByDS(null,
							"select * from V_CK_SCHEME_IMPLEMENT where implid='"
									+ implid + "' and checkpoints='"
									+ countvo[i].getStringValue("checkpoints")
									+ "' and thirdname='"
									+ countvo[i].getStringValue("thirdname")
									+ "'");
					update.setWhereCondition("id=" + v_ck_id);
					update.putFieldValue("control", countvo[i]
							.getStringValue("control"));
					update.putFieldValue("result", countvo[i]
							.getStringValue("result"));
					cooylist.add(update.getSQL());
					TableDataStruct tabstrct = UIUtil.getTableDataStructByDS(
							null, "select * from ck_problem_info where 1=2");// 得到表中的字段
					String[] str_cols = tabstrct.getHeaderName();
					HashVO[] infoVo = UIUtil.getHashVoArrayByDS(null,
							"select * from ck_problem_info where 1=1  and (parentid='"
									+ countvo[i].getStringValue("id") + "') ");
					for (int f = 0; f < infoVo.length; f++) {
						String seqId = UIUtil.getSequenceNextValByDS(null,
								"s_ck_problem_info");
						for (int j = 0; j < str_cols.length; j++) {
							if (str_cols[j].equals("id")) {
								insert.putFieldValue("id", seqId);
							} else if (str_cols[j].equals("parentid")) {
								insert.putFieldValue("parentid", v_ck_id);
							} else if (str_cols[j].equals("implid")) {
								insert.putFieldValue("implid", implid);
							} else if (str_cols[j].equals("schemeid")) {
								insert.putFieldValue("schemeid", id);
							} else if (str_cols[j].equals("planid")) {
								insert.putFieldValue("planid", implVO[0]
										.getStringValue("planid"));
							} else if (str_cols[j].equals("deptid")) {
								insert.putFieldValue("deptid", implVO[0]
										.getStringValue("deptid"));
							}else if(str_cols[j].equals("dictid") || str_cols[j].equals("problemInfo")){
								insert.putFieldValue(str_cols[j].toString(),
										infoVo[f].getStringValue(str_cols[j]
												.toString()));
							}else if(str_cols[j].equals("createuserid")){
								insert.putFieldValue(str_cols[j].toString(),
										infoVo[f].getStringValue(str_cols[j]
												.toString()));
							}else if(str_cols[j].equals("createusername")){
								insert.putFieldValue(str_cols[j].toString(),
										infoVo[f].getStringValue(str_cols[j]
												.toString()));
							}else if(str_cols[j].equals("createdeptid")){
								insert.putFieldValue(str_cols[j].toString(),
										infoVo[f].getStringValue(str_cols[j]
												.toString()));
							}
						}
						cooylist.add(insert.getSQL());
					}
				}
				UIUtil.executeBatchByDS(null, cooylist);
				UpdateSQLBuilder update_impl = new UpdateSQLBuilder(
						"CK_SCHEME_IMPL");
				update_impl.setWhereCondition("id=" + implid);
				update_impl.putFieldValue("STATUS", "执行中");
				UIUtil.executeUpdateByDS(null, update_impl.getSQL());
				implList.reload();
				implList.QueryDataByCondition("schemeid='" + id
						+ "' and deptid='" + impdeptid + "'");
				itemList.reload();
				itemList.QueryDataByCondition("implid='" + implid + "'");
				MessageBox.show(this, "复制底稿成功");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 切换方案【李春娟/2016-08-19】
	 */
	private void onSelectScheme() {
		BillListDialog listDialog = new BillListDialog(this, "请选择一个方案",
				"CK_SCHEME_LCJ_E01", 900, 500);
		//这样的SQL 会在加载页面的时候特别耗时间【zzl 2017-11-28】
//		listDialog.getBilllistPanel().setDataFilterCustCondition(
//				"id in (select schemeid from V_CK_SCHEME where (teamusers like '%;"
//						+ loginUserid + ";%' or leader2='" + loginUserid
//						+ "' or leader ='" + loginUserid
//						+ "' or referee like '%;" + loginUserid + ";%'))");
//		listDialog.getBilllistPanel().queryDataByCondition(null,
//				"createdate desc");// 按创建日期倒序排序
		listDialog.getBilllistPanel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);// 设置只能选一行
		listDialog.setVisible(true);
		if (listDialog.getCloseType() == 1) {// 如果选择了记录，点击确定按钮
			BillVO[] vos = listDialog.getReturnBillVOs();
			if (vos == null || vos.length == 0) {// 一般情況，点击"确定"按钮后这里不为空
				MessageBox.show(this, "请选择一个方案.");
				return;
			} else {
				itemList.clearTable();
				String schemeid = vos[0].getStringValue("id");
				String SCHEMETYPE = vos[0].getStringValue("SCHEMETYPE");
				if ("信贷检查".equals(SCHEMETYPE) || "票据检查".equals(SCHEMETYPE)) {
					if (parentTempletCode2.equalsIgnoreCase(implList
							.getTempletVO().getTempletcode())) {// 如果已经是信贷检查模板
						implList.clearTable();
					} else {
						schemeList = new BillListPanel(schemeTempletCode);
						schemeList.addBillListSelectListener(this);
						schemeList.setQuickQueryPanelVisiable(false);// 设置查询面板隐藏【李春娟/2016-09-27】
						schemeList.addBatchBillListButton(new WLTButton[] {
								btn_selectScheme, btn_exp_word,
								btn_change_data, btn_newDept, btn_CknewUser,
								btn_scheme_delete, btn_tongbu });
						schemeList.repaintBillListButton();
						schemeList.QueryDataByCondition("schemeid='" + schemeid
								+ "' and (teamusers like '%;" + loginUserid
								+ ";%' or leader2='" + loginUserid
								+ "' or leader ='" + loginUserid
								+ "' or referee like '%;" + loginUserid
								+ ";%')");
						implList = new BillListPanel(parentTempletCode2);
						implList.addBillListSelectListener(this);
						implList.setQuickQueryPanelVisiable(false);// 设置查询面板隐藏【李春娟/2016-09-27】
						implList.addBatchBillListButton(new WLTButton[] {
								btn_copy, btn_edit, btn_relate, btn_delete,
								btn_copyxx, btn_newUser, btn_scheme_delete2 });
						implList.repaintBillListButton();
						WLTSplitPane splitPane = new WLTSplitPane(
								WLTSplitPane.HORIZONTAL_SPLIT, schemeList,
								implList);
						splitPane.setDividerLocation(540);// 修改分隔条位置【李春娟/2016-08-24】

						splitPane2 = new WLTSplitPane(
								WLTSplitPane.VERTICAL_SPLIT, splitPane,
								itemList);
						splitPane2.setDividerLocation(250);// 修改分隔条位置【李春娟/2016-08-24】
						this.removeAll();
						this.add(splitPane2);
						this.repaint();
					}
				} else {
					if (parentTempletCode2.equalsIgnoreCase(implList
							.getTempletVO().getTempletcode())) {// 如果已经是信贷检查模板
						implList = new BillListPanel(parentTempletCode);
						implList.addBillListSelectListener(this);
						implList.setQuickQueryPanelVisiable(false);// 设置查询面板隐藏【李春娟/2016-09-27】
						implList.addBatchBillListButton(new WLTButton[] {
								btn_selectScheme, btn_exp_word,
								btn_change_data, btn_newDept, btn_CknewUser,
								btn_scheme_delete, btn_tongbu, btn_dyqd });
						implList.repaintBillListButton();
						splitPane2 = new WLTSplitPane(
								WLTSplitPane.VERTICAL_SPLIT, implList, itemList);
						splitPane2.setDividerLocation(250);// 修改分隔条位置【李春娟/2016-10-09】
						this.removeAll();
						this.add(splitPane2);
						this.repaint();
					}
					implList.QueryDataByCondition("schemeid='" + schemeid
							+ "' and (teamusers like '%;" + loginUserid
							+ ";%' or leader2='" + loginUserid
							+ "' or leader ='" + loginUserid
							+ "' or referee like '%;" + loginUserid + ";%')");
				}
				try {
					String count = UIUtil.getStringValueByDS(null,
							"select count(*) from ck_scheme_user where userid="
									+ loginUserid);
					if (count == null || "".equals(count) || "0".equals(count)) {
						InsertSQLBuilder sb = new InsertSQLBuilder(
								"ck_scheme_user");
						sb.putFieldValue("id", UIUtil.getSequenceNextValByDS(
								null, "S_ck_scheme_user"));
						sb.putFieldValue("userid", loginUserid);
						sb.putFieldValue("schemeid", schemeid);// 以前根据schemeid、deptid、userid记录，后来修改为根据schemeid记录【李春娟/2016-09-23】
						UIUtil.executeUpdateByDS(null, sb.getSQL());
					} else {
						UIUtil.executeUpdateByDS(null,
								"update ck_scheme_user set schemeid='"
										+ schemeid + "' where userid ="
										+ loginUserid);
					}
				} catch (WLTRemoteException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 检查方案-数据交换按钮弹出框
	 */
	private void onChangeData() {
		if (changeDataPopMenu == null) {
			changeDataPopMenu = new JPopupMenu(); //
			menu_impdata = new JMenuItem("导入");
			menu_expdata = new JMenuItem("导出");

			menu_impdata.addActionListener(this);
			menu_expdata.addActionListener(this);

			changeDataPopMenu.add(menu_impdata);
			changeDataPopMenu.add(menu_expdata);
		}
		if (btn_change_data.getMousePosition() == null) {// debug调试时这里经常为空，故判断一下【李春娟/2016-10-14】
			changeDataPopMenu.show(btn_change_data, 30, 20);
		} else {
			changeDataPopMenu
					.show(btn_change_data,
							btn_change_data.getMousePosition().x,
							btn_change_data.getMousePosition().y);
		}
	}

	/**
	 * 检查方案-数据交换-导入检查底稿【李春娟/2016-08-31】
	 */
	private void onImpData() {// ck_scheme_implement,ck_problem_info,cmp_event
		// 单机版新增id 都弄成负值
		int i = new ICheckUIUtil().importDataByPackage(this, "导入检查底稿数据包", 1);
		if (i == 1) {
			if (implList.getRowCount() > 0) {
				implList.refreshData();
			} else {
				implList.clearTable();
			}
			itemList.clearTable();
			MessageBox.show(this, "底稿导入成功!");
		}
	}

	/**
	 * 检查方案-数据交换-导出检查底稿【李春娟/2016-08-30】 网络版导出某个方案某个单位的所有检查底稿 单机版导出可选择具体导出哪个检查底稿
	 * 网络版导出表：ck_scheme_impl,ck_scheme_implement,ck_scheme,ck_member_work,
	 * ck_manuscript_design
	 * ,pub_user,pub_corp_dept,pub_post,pub_user_post,cmp_cmpfile,cmp_risk
	 * 单机版导出表
	 * ：ck_scheme_impl,ck_scheme_implement,ck_problem_info,cmp_wardevent_cust
	 * ,cmp_wardevent_user
	 * ,cmp_event,ck_scheme,ck_member_work,ck_manuscript_design,ck_record
	 * 
	 * 单机->网络版导入表：ck_scheme_impl（单机版可编辑或拷贝检查实施主表）,检查实施表（ck_scheme_implement）,问题表
	 * （ck_problem_info）,违规事件涉及客户（cmp_wardevent_cust）,违规事件涉及员工（
	 * cmp_wardevent_user）,违规事件表（cmp_event）,单机版导出通知书等工作量（ck_record）
	 * 单机->单机版导入表：ck_scheme_impl
	 * ,ck_scheme_implement,ck_problem_info,cmp_wardevent_cust
	 * ,cmp_wardevent_user
	 * ,cmp_event,ck_scheme,ck_member_work,ck_manuscript_design,ck_record
	 * 
	 */
	private void onExpData() {
		BillListDialog listDialog = new BillListDialog(this, "请勾选需要导出的方案",
				"V_CK_SCHEME_IMPL_Q01", 950, 500);// 网络版过滤相同的方案名和被检查机构【李春娟/2016-09-29】
		listDialog.getBilllistPanel().setRowNumberChecked(true);// 设置勾选
		listDialog.getBilllistPanel().setDataFilterCustCondition(
				"(teamusers like '%;" + loginUserid + ";%' or leader2='"
						+ loginUserid + "' or leader ='" + loginUserid
						+ "' or referee like '%;" + loginUserid + ";%')");
		listDialog.setVisible(true);
		if (listDialog.getCloseType() == 1) {// 如果选择了记录，点击确定按钮
			final BillVO[] vos = listDialog.getBilllistPanel()
					.getCheckedBillVOs();
			if (vos == null || vos.length == 0) {// 一般情況，点击"确定"按钮后这里不为空
				MessageBox.show(this, "请至少勾选一个方案.");
				return;
			} else {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("请选择存放目录");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int flag = chooser.showSaveDialog(this);
				if (flag == 1 || chooser.getSelectedFile() == null) {
					return;
				}
				final String str_path = chooser.getSelectedFile()
						.getAbsolutePath(); //
				if (str_path == null) {
					return;
				}
				File f = new File(str_path);
				if (!f.exists()) {
					MessageBox.show(this, "路径:" + str_path + " 不存在!");
					return;
				}
				new SplashWindow(this, new AbstractAction() {
					public void actionPerformed(ActionEvent e) {
						ICheckUIUtil checkUtil = new ICheckUIUtil();
						for (int i = 0; i < vos.length; i++) {
							String schemeid = vos[i].getStringValue("schemeid");
							String deptid = vos[i].getStringValue("deptid");
							String deptname = vos[i]
									.getStringViewValue("deptid");
							if (deptname != null) {
								if (deptname.contains("-")) {// 全路径分隔号
									deptname = deptname.substring(deptname
											.lastIndexOf("-") + 1);
								}
							}
							boolean isexp = checkUtil.exportDataByCondition(
									(SplashWindow) e.getSource(), str_path,
									schemeid, deptid, deptname, "", 0);
							if (isexp) {// 如果导出成功，则更新检查实施主表导出状态【李春娟/2016-10-11】
								try {
									UIUtil.executeUpdateByDS(null,
											"update ck_scheme_impl set expstatus='已导出' where schemeid='"
													+ schemeid
													+ "' and deptid ='"
													+ deptid + "'");
								} catch (WLTRemoteException e1) {
									e1.printStackTrace();
								} catch (Exception e1) {
									e1.printStackTrace();
								}
							}
						}
					}
				}, 600, 130, 300, 300, false); // 
			}
		}
	}

	/**
	 * 检查方案-导出Word 弹出框 zzl
	 */
	private void onExpWord() {
		if (expWordPopMenu == null) {
			expWordPopMenu = new JPopupMenu(); //

			menu_notice = new JMenuItem("导出检查通知书");
			menu_interview = new JMenuItem("导出进驻会谈记录模板");
			menu_retrival = new JMenuItem("导出资料调阅清单");

			menu_notice.addActionListener(this);
			menu_interview.addActionListener(this);
			menu_retrival.addActionListener(this);

			expWordPopMenu.add(menu_notice);
			expWordPopMenu.add(menu_interview);
			expWordPopMenu.add(menu_retrival);
		}
		expWordPopMenu.show(btn_exp_word, btn_exp_word.getMousePosition().x,
				btn_exp_word.getMousePosition().y);
	}

	/**
	 * 检查方案-导出Word-导出检查通知书 zzl
	 */
	private void impnotice() {
		BillListDialog listDialog = new BillListDialog(this, "请勾选需要导出的方案",
				"V_CK_SCHEME_IMPL_Q01", 950, 500);// 网络版过滤相同的方案名和被检查机构【李春娟/2016-09-29】
		listDialog.getBilllistPanel().setRowNumberChecked(true);// 设置勾选
		listDialog.getBilllistPanel().setDataFilterCustCondition(
				"(teamusers like '%;" + loginUserid + ";%' or leader2='"
						+ loginUserid + "' or leader ='" + loginUserid
						+ "' or referee like '%;" + loginUserid + ";%')");
		listDialog.setVisible(true);
		if (listDialog.getCloseType() == 1) {// 如果选择了记录，点击确定按钮
			final BillVO[] vos = listDialog.getBilllistPanel()
					.getCheckedBillVOs();
			if (vos == null || vos.length == 0) {// 一般情況，点击"确定"按钮后这里不为空
				MessageBox.show(this, "请至少勾选一个方案.");
				return;
			} else {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("请选择存放目录");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int flag = chooser.showSaveDialog(this);
				if (flag == 1 || chooser.getSelectedFile() == null) {
					return;
				}
				String str_path = chooser.getSelectedFile().getAbsolutePath(); //
				if (str_path == null) {
					return;
				}
				File f = new File(str_path + "\\检查通知书");
				f.mkdir();
				str_path = str_path + "\\检查通知书";
				if (!f.exists()) {
					MessageBox.show(this, "路径:" + str_path + " 不存在!");
					return;
				}
				Map<Object, Object> dataMap = new HashMap<Object, Object>();
				int a = 0;
				for (int i = 0; i < vos.length; i++) {
					a = a + 1;
					try {
						String id = UIUtil.getStringValueByDS(null,
								"select planid from CK_SCHEME where id='"
										+ vos[i].getStringValue("schemeid")
										+ "'");
						HashVO[] vo = UIUtil.getHashVoArrayByDS(null,
								"select * from CK_PLAN where id ='" + id + "'");
						String planName = UIUtil
								.getStringValueByDS(
										null,
										"select plantype from CK_plan where id='"
												+ vos[i]
														.getStringValue("planid")
												+ "'");
						if (planName.equals("合规检查")) {
							planName = "合规";
						}
						if (planName.equals("审计检查")) {
							planName = "审计";
						}
						if (planName.equals("自律检查")) {
							planName = "自律";
						}
						dataMap.put("titel", planName);
						String year = vo[0].getStringValue("CREATEDATE");
						dataMap.put("code", year.substring(0, 4));
						String code2 = vo[0].getStringValue("CODE");
						code2 = code2 + "-" + a;
						dataMap.put("code2", code2);
						String deptname = UIUtil
								.getStringValueByDS(
										null,
										"select name from pub_corp_dept where id='"
												+ vos[i]
														.getStringValue("deptid")
												+ "'");
						dataMap.put("dept", deptname);
						dataMap.put("planname", vos[i]
								.getStringValue("planname"));
						dataMap.put("time", vo[0]
								.getStringValue("PLANBEGINDATE"));
						String str1 = vo[0].getStringValue("PLANBEGINDATE");
						str1 = str1.replace("-", "");
						int PLANBEGINDATE = Integer.parseInt(str1);
						String str2 = vo[0].getStringValue("planenddate");
						str2 = str2.replace("-", "");
						int planenddate = Integer.parseInt(str2);
						dataMap.put("count", planenddate - PLANBEGINDATE);
						String[][] username = UIUtil.getStringArrayByDS(null,
								"select LEADER,REFEREE from CK_SCHEME where id='"
										+ vos[i].getStringValue("schemeid")
										+ "'");
						String LEADER = ZZLUIUtil.getUserName(username[0][0]);
						String REFEREE = ZZLUIUtil
								.getnamesSplit(username[0][1]);
						dataMap.put("LEADER", LEADER);
						dataMap.put("REFEREE", REFEREE);
						String teamusers = UIUtil
								.getStringValueByDS(
										null,
										"select teamusers from CK_plan where id ='"
												+ vos[i]
														.getStringValue("planid")
												+ "'");
						teamusers = teamusers.replace(";", ",");
						teamusers = teamusers.substring(1,
								teamusers.length() - 1);
						String[] teamusersname = UIUtil
								.getStringArrayFirstColByDS(null,
										"select name from pub_user where id in("
												+ teamusers + ")");
						StringBuffer ckname = splitName(teamusersname);
						dataMap.put("TEAMUSERS", ckname);
						String str = str_path + "\\" + dataMap.get("dept")
								+ "检查通知书.doc";
						WordExport mdoc = new WordExport();
						mdoc.createDoc(dataMap, "WordNotice", str);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				MessageBox.show(this, "导出成功共导出" + vos.length + "个检查通知书");
			}
		}
	}

	private StringBuffer splitName(String[] name) {
		StringBuffer sbname = new StringBuffer();
		for (int i = 0; i < name.length; i++) {
			sbname.append(name[i].toString());
			sbname.append(",");
		}
		return sbname;
	}

	/**
	 * 检查方案-导出Word-导出进驻会谈记录模板 zzl
	 */
	private void impinterview() {
		BillVO[] vos = implList.getBillVOs();
		if (vos.length <= 0) {
			MessageBox.show(null, "请选择一条数据");
			return;
		}
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("请选择存放目录");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int flag = chooser.showSaveDialog(this);
		if (flag == 1 || chooser.getSelectedFile() == null) {
			return;
		}
		final String str_path = chooser.getSelectedFile().getAbsolutePath(); //
		if (str_path == null) {
			return;
		}
		File f = new File(str_path);
		if (!f.exists()) {
			MessageBox.show(this, "路径:" + str_path + " 不存在!");
			return;
		}
		Map<Object, Object> dataMap = new HashMap<Object, Object>();
		try {
			String planName = UIUtil.getStringValueByDS(null,
					"select plantype from CK_plan where id='"
							+ vos[0].getStringValue("planid") + "'");
			if (planName.equals("合规检查")) {
				planName = "合规";
			}
			if (planName.equals("审计检查")) {
				planName = "审计";
			}
			if (planName.equals("自律检查")) {
				planName = "自律";
			}
			dataMap.put("titel", planName);
			String str = str_path + "\\" + dataMap.get("titel")
					+ "检查进驻会谈记录.doc";
			WordExport mdoc = new WordExport();
			mdoc.createDoc(dataMap, "WordInterview", str);
			MessageBox.show(this, "导出成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检查方案-导出Word-导出资料调阅清单 zzl
	 */
	private void impRetrival() {
		BillListDialog listDialog = new BillListDialog(this, "请勾选需要导出的方案",
				"V_CK_SCHEME_IMPL_Q01", 950, 500);// 网络版过滤相同的方案名和被检查机构【李春娟/2016-09-29】
		listDialog.getBilllistPanel().setRowNumberChecked(true);// 设置勾选
		listDialog.getBilllistPanel().setDataFilterCustCondition(
				"(teamusers like '%;" + loginUserid + ";%' or leader2='"
						+ loginUserid + "' or leader ='" + loginUserid
						+ "' or referee like '%;" + loginUserid + ";%')");
		listDialog.setVisible(true);
		final BillVO[] vos = listDialog.getBilllistPanel().getCheckedBillVOs();
		BillListDialog RetrivalDialog = new BillListDialog(this, "请填写要导出的资料",
				"CK_RETRIVAL_ZZl_EQ1", 900, 500);
		RetrivalDialog.setVisible(true);
		final BillVO[] Rvos = RetrivalDialog.getBilllistPanel().getBillVOs();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < vos.length; i++) {
			sb.append(vos[i].getStringValue("deptid"));
			sb.append(",");
		}
		if (Rvos.length <= 0) {
			return;
		}
		for (int i = 0; i < Rvos.length; i++) {
			UpdateSQLBuilder update = new UpdateSQLBuilder("CK_RETRIVAL");
			update.setWhereCondition("id=" + Rvos[i].getStringValue("id"));
			System.out.println(">>>>>>>>>>>>>>>>>>>>"
					+ vos[0].getStringValue("schemeid"));
			update.putFieldValue("schemeid", vos[0].getStringValue("schemeid"));
			update.putFieldValue("deptid", sb.toString());
			try {
				UIUtil.executeUpdateByDS(null, update.getSQL());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("请选择存放目录");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int flag = chooser.showSaveDialog(this);
		if (flag == 1 || chooser.getSelectedFile() == null) {
			return;
		}
		String str_path = chooser.getSelectedFile().getAbsolutePath(); //
		if (str_path == null) {
			return;
		}
		File f = new File(str_path + "\\资料调阅清单");
		f.mkdir();
		str_path = str_path + "\\资料调阅清单";
		if (!f.exists()) {
			MessageBox.show(this, "路径:" + str_path + " 不存在!");
			return;
		}
		for (int i = 0; i < vos.length; i++) {
			String deptname;
			try {
				deptname = UIUtil.getStringValueByDS(null,
						"select name from pub_corp_dept where id='"
								+ vos[i].getStringValue("deptid") + "'");
				HSSFWorkbook wb = new HSSFWorkbook();
				HSSFSheet sheet = wb.createSheet(deptname);
				HSSFCellStyle style = wb.createCellStyle(); // 样式对象
				sheet.addMergedRegion(new Region(0, (short) 0, 0, (short) 5));
				sheet.addMergedRegion(new Region(1, (short) 0, 1, (short) 5));
				sheet.setColumnWidth(0, 3766);
				style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直
				style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平
				style.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 下边框
				style.setBorderLeft(HSSFCellStyle.BORDER_THIN);// 左边框
				style.setBorderTop(HSSFCellStyle.BORDER_THIN);// 上边框
				style.setBorderRight(HSSFCellStyle.BORDER_THIN);// 右边框
				HSSFFont font = wb.createFont();
				font.setFontName("黑体");
				font.setFontHeightInPoints((short) 16);// 设置字体大小
				HSSFFont font2 = wb.createFont();
				font2.setFontName("仿宋_GB2312");
				font2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 粗体显示
				font2.setFontHeightInPoints((short) 12);
				style.setFont(font);// 选择需要用到的字体格式
				HSSFRow row = sheet.createRow((short) 0);
				row.setHeight((short) 666);
				for (int j = 0; j <= 5; j++) {
					HSSFCell cell = row.createCell((short) j);
					cell.setCellValue("新疆昌吉农村商业银行股份有限公司现场检查调阅资料清单");
					cell.setCellStyle(style);
				}
				HSSFRow row1 = sheet.createRow((short) 1);
				row1.setHeight((short) 555);
				for (int j = 0; j <= 5; j++) {
					HSSFCell cell1 = row1.createCell((short) j);
					cell1.setCellValue("被检查单位:" + deptname);
					cell1.setCellStyle(style);
				}
				HSSFRow row2 = sheet.createRow((short) 2);
				row2.setHeight((short) 444);
				for (int j = 0; j <= 5; j++) {
					HSSFCell cell2 = row2.createCell((short) j);
					if (j == 0) {
						sheet.setColumnWidth((short) j, (short) 6000);
						cell2.setCellValue("调阅资料名称");
					} else if (j == 1) {
						sheet.setColumnWidth((short) j, (short) 1500);
						cell2.setCellValue("份数");
					} else if (j == 2) {
						sheet.setColumnWidth((short) j, (short) 4500);
						cell2.setCellValue("调阅时间");
					} else if (j == 3) {
						sheet.setColumnWidth((short) j, (short) 4500);
						cell2.setCellValue("调阅人签名");
					} else if (j == 4) {
						sheet.setColumnWidth((short) j, (short) 4500);
						cell2.setCellValue("交还日期");
					} else if (j == 5) {
						sheet.setColumnWidth((short) j, (short) 4500);
						cell2.setCellValue("收回人签名");
					}
					cell2.setCellStyle(style);
				}
				for (int j = 0; j < 4; j++) {
					int a = 2 + j;
					sheet.addMergedRegion(new Region(Rvos.length + 3
							- Rvos.length, (short) a, Rvos.length + 3 - 1,
							(short) a));
				}

				for (int j = 0; j < Rvos.length; j++) {
					String schemeid = UIUtil.getStringValueByDS(null,
							"select schemeid from ck_retrival where id='"
									+ Rvos[j].getStringValue("id") + "'");
					HSSFRow row3 = sheet.createRow((short) 3 + j);
					row3.setHeight((short) 444);
					HSSFCell cell3 = row3.createCell((short) 0);
					HSSFCell cell4 = row3.createCell((short) 1);
					cell3.setCellValue(Rvos[j].getStringValue("Profilename"));
					cell3.setCellStyle(style);
					cell4.setCellValue(Rvos[j].getStringValue("number"));
					cell4.setCellStyle(style);
					for (int k = 0; k < 4; k++) {
						HSSFRow row5 = sheet.getRow((short) Rvos.length + 3
								- Rvos.length + j);
						HSSFCell cell5 = row5.createCell((short) k + 2);
						if (k == 0) {
							cell5.setCellValue("年 月 日");
						} else if (k == 1) {
							String[][] dept = UIUtil.getStringArrayByDS(null,
									"select leader,teamusers  from CK_MEMBER_WORK where schemeid='"
											+ schemeid
											+ "' and checkeddept like'%"
											+ vos[i].getStringValue("deptid")
											+ "%'");
							StringBuilder deptsb = new StringBuilder();
							String name = UIUtil.getStringValueByDS(null,
									"select name from pub_user where id in("
											+ dept[0][0] + ")");
							deptsb.append(name);
							deptsb.append(",");
							String deptid = dept[0][1].toString().replace(";",
									",");
							deptid = deptid.substring(1, deptid.length() - 1);
							String[] username = UIUtil
									.getStringArrayFirstColByDS(null,
											"select name from pub_user where id in("
													+ deptid + ")");
							for (int q = 0; q < username.length; q++) {
								deptsb.append(username[q].toString());
								deptsb.append(",");
							}
							style.setWrapText(true);
							cell5.setCellValue(deptsb.toString());
						} else if (k == 2) {
							cell5.setCellValue("年 月 日");
						}

						cell5.setCellStyle(style);
					}
				}
				FileOutputStream fileOut;
				fileOut = new FileOutputStream(str_path + "\\" + deptname
						+ "资料调阅清单.xls");
				wb.write(fileOut);
				fileOut.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		MessageBox.show(null, "成功导出" + vos.length + "个资料查阅清单");
	}

	/**
	 * 
	 * 新增检查实施（拷贝当前检查底稿）
	 */
	private void onCopyImpl() {
		if (implList.getRowCount() == 0) {
			MessageBox.show(this, "请在左侧选中一个检查方案.");
			return;
		}
		BillVO billvo = implList.getBillVO(0);
		String id = billvo.getStringValue("id");
		try {
			BillCardDialog cardDialog = new BillCardDialog(this,
					"CK_WLTDUAL_LCJ_E01", WLTConstants.BILLDATAEDITSTATE_UPDATE);
			cardDialog.setTitle("");
			cardDialog.getBillcardPanel().setRealValueAt("c1", "1");
			cardDialog.getBtn_save().setVisible(false);
			cardDialog.setVisible(true);
			if (cardDialog.getCloseType() != 1) {
				return;
			}

			HashVOStruct hvst = UIUtil.getHashVoStructByDS(null,
					"select * from ck_scheme_impl where id =" + id);
			String[] str_keys = hvst.getHeaderName(); // 列名
			HashVO[] hvs = hvst.getHashVOs(); //
			ArrayList sqlList = new ArrayList();
			InsertSQLBuilder isql = new InsertSQLBuilder("ck_scheme_impl");
			if (hvs.length == 0) {
				MessageBox.show(this, "未查询到检查实施主表记录.");
				return;
			}

			HashVOStruct hvst2 = UIUtil.getHashVoStructByDS(null,
					"select * from ck_scheme_implement where implid =" + id);
			String[] str_keys2 = hvst2.getHeaderName(); // 列名
			HashVO[] hvs2 = hvst2.getHashVOs(); //
			InsertSQLBuilder isql2 = new InsertSQLBuilder("ck_scheme_implement");
			if (hvs2.length == 0) {
				MessageBox.show(this, "未查询到检查实施子表记录.");
				return;
			}

			String count = cardDialog.getBillVO().getStringValue("c1");
			int intcount = Integer.parseInt(count);
			if (intcount > 50) {
				intcount = 50;// 最多50笔业务【李春娟/2016-10-08】
			}

			for (int i = 0; i < intcount; i++) { // 遍历各行!!
				// 复制检查实施主表ck_scheme_impl
				for (int j = 0; j < str_keys.length; j++) { // 遍历各列!!
					String str_itemValue = hvs[0].getStringValue(str_keys[j],
							""); // 取得值!!
					isql.putFieldValue(str_keys[j], str_itemValue);
				}
				String implid = UIUtil.getSequenceNextValByDS(null,
						"s_ck_scheme_impl");
				isql.putFieldValue("id", implid);
				for (int j2 = 1; j2 < 19; j2++) {
					isql.putFieldValue("c" + j2, "");// 信贷台账
				}
				isql.putFieldValue("refc1", "");// 信贷台账
				isql.putFieldValue("refimplid", "");// 信贷台账
				isql.putFieldValue("status", "未执行");// 状态

				isql.putFieldValue("createdate", TBUtil.getTBUtil()
						.getCurrDate());// 录入日期
				isql.putFieldValue("creater", loginUserid);// 录入人
				isql.putFieldValue("createdept", ClientEnvironment
						.getInstance().getLoginUserDeptId());// 录入机构
				sqlList.add(isql.getSQL());

				// 复制检查实施子表ck_scheme_implement
				for (int j = 0; j < hvs2.length; j++) {
					for (int m = 0; m < str_keys2.length; m++) { // 遍历各列!!
						String str_itemValue = hvs2[j].getStringValue(
								str_keys2[m], ""); // 取得值!!
						isql2.putFieldValue(str_keys2[m], str_itemValue);
					}
					isql2.putFieldValue("id", UIUtil.getSequenceNextValByDS(
							null, "s_ck_scheme_implement"));
					isql2.putFieldValue("implid", implid);
					isql2.putFieldValue("control", "");// 结果描述
					isql2.putFieldValue("result", "");// 检查结果
					isql2.putFieldValue("descr", "");// 附件
					sqlList.add(isql2.getSQL());
				}
			}
			UIUtil.executeBatchByDS(null, sqlList);
			MessageBox.show(this, "复制成功!");
			implList.refreshData();
			itemList.clearTable();
		} catch (WLTRemoteException e) {
			e.printStackTrace();
			MessageBox.showException(this, e);
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showException(this, e);
		}
	}

	/**
	 * 检查实施-修改【李春娟/2016-09-29】
	 */
	private void onEditImpl() {
		if (implList.getRowCount() == 0) {
			MessageBox.show(this, "请在左侧选中一个检查方案.");
			return;
		}
		BillVO billVO = implList.getSelectedBillVO();
		BillCardPanel cardPanel = new BillCardPanel(implList.templetVO);
		cardPanel.setBillVO(billVO); //
		cardPanel.setRealValueAt("STATUS", "执行中");// 状态
		cardPanel
				.setRealValueAt("createdate", TBUtil.getTBUtil().getCurrDate());// 录入日期
		cardPanel.setRealValueAt("creater", loginUserid);// 录入人
		cardPanel.setRealValueAt("createdept", ClientEnvironment.getInstance()
				.getLoginUserDeptId());// 录入机构
		BillCardDialog dialog = new BillCardDialog(implList, implList.templetVO
				.getTempletname(), cardPanel,
				WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {
			implList.setBillVOAt(implList.getSelectedRow(), dialog.getBillVO(),
					false); //
			implList.setRowStatusAs(implList.getSelectedRow(),
					WLTConstants.BILLDATAEDITSTATE_INIT);
		}
	}

	/**
	 * 信贷检查或票据检查，关联客户
	 */
	private void onRelate() {
		BillVO billvo = implList.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.show(this, "请选中一个检查实施.");
			return;
		}
		String id = billvo.getStringValue("id");
		String schemeid = billvo.getStringValue("schemeid");
		String deptid = billvo.getStringValue("deptid");
		String status = billvo.getStringValue("status");
		BillListDialog listDialog = new BillListDialog(this, "请选择被关联的客户名称",
				"CK_SCHEME_IMPL_E02", "schemeid = '" + schemeid
						+ "' and deptid = '" + deptid + "' and id !=" + id,
				600, 500);
		listDialog.getBilllistPanel().setDataFilterCustCondition(
				"schemeid = '" + schemeid + "' and deptid = '" + deptid
						+ "' and id !=" + id);
		listDialog.getBilllistPanel().setRowNumberChecked(true);
		listDialog.setVisible(true);
		if (listDialog.getCloseType() == 1) {
			BillVO[] billvos = listDialog.getBilllistPanel()
					.getCheckedBillVOs();
			List list = new ArrayList<String>();
			UpdateSQLBuilder update = new UpdateSQLBuilder("CK_SCHEME_IMPL");
			for (int i = 0; i < billvos.length; i++) {
				if (billvos != null && billvos.length > 0) {
					try {
						update.setWhereCondition("id="
								+ billvos[i].getStringValue("id"));
						update.putFieldValue("refimplid", billvo
								.getStringValue("id"));
						update.putFieldValue("refc1", billvo
								.getStringValue("c1"));
						update.putFieldValue("status", status);
						list.add(update.getSQL());
						// UIUtil.executeUpdateByDS(null,
						// "update CK_SCHEME_IMPL set refimplid ='" +
						// billvos[0].getStringValue("id") + "' ,refc1='" +
						// billvos[0].getStringValue("c1", "") + "',status='" +
						// status + "' where id =" +
						// billvo.getStringValue("id"));
						if ("已结束".equals(status)) {
							btn_edit.setVisible(false);// 检查实施-修改
							btn_insert.setVisible(false);// 检查实施子表-逐条录入
							btn_end.setVisible(false);// 检查实施子表-底稿录入完成
						} else {
							btn_edit.setVisible(true);
							btn_insert.setVisible(true);
							btn_end.setVisible(true);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			try {
				UIUtil.executeBatchByDS(null, list);
				implList.refreshData();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 检查实施子表-逐条录入
	 */
	public void onInsert() {
		if (itemList.getRowCount() == 0) {
			MessageBox.show(this, "请选中一个检查实施.");
			return;
		} else {
			BillVO vo = itemList.getSelectedBillVO();
			if (null == vo) {
				itemList.setSelectedRow(0);
			}
		}

		BillCardDialog_CheckIn dialog = new BillCardDialog_CheckIn(implList,
				itemList, childTempletCode, "检查工作底稿");
		dialog.setVisible(true);
	}

	/**
	 * 检查实施子表-检查评价指引
	 */
	private void onCheckHelp() {
		if (itemList.getSelectedBillVO() == null) {
			MessageBox.show(this, "请选择一个检查工作底稿!");
			return;
		}
		BillCardDialog dialog_card = new BillCardDialog(itemList,
				"CK_MANUSCRIPT_DESIGN_SCY_Q01",
				WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog_card.setTitle("检查帮助提示");
		dialog_card.billcardPanel.queryDataByCondition(" id='"
				+ itemList.getSelectedBillVO().getStringValue("id") + "'");
		dialog_card.getBtn_save().setVisible(false);
		dialog_card.getBtn_confirm().setVisible(false);
		dialog_card.setVisible(true);
	}

	/**
	 * 检查实施子表-底稿录入完成【李春娟/2016-10-09】
	 */
	private void onEnd() {
		if (itemList.getRowCount() == 0 || implList.getSelectedBillVO() == null) {
			MessageBox.show(this, "请选中一个检查实施.");
			return;
		}
		String status = implList.getSelectedBillVO().getStringValue("status");
		if ("已结束".equals(status)) {
			MessageBox.show(this, "该检查实施已结束.");
			return;
		}
		String msg = "底稿录入完成后不可修改，是否继续?";
		int rowCount = itemList.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			String checkMode = itemList.getRealValueAtModel(i, "checkMode");// 检查方式：现场检查、非现场检查
			if ("现场检查".equals(checkMode)) {// 网络版需要判断是否有现场检查的记录【李春娟/2016-10-13】
				msg = "该底稿有现场检查的记录，录入完成后不可修改，是否继续?";
				break;
			}
		}
		if (MessageBox.confirm(this, msg)) {
			String implid = itemList.getRealValueAtModel(0, "implid");
			try {
				UIUtil.executeUpdateByDS(null,
						"update CK_SCHEME_IMPL set status='已结束' where id ='"
								+ implid + "' or refimplid ='" + implid + "'");
				implList.refreshData();
				itemList.clearTable();
				btn_edit.setVisible(false);
				btn_insert.setVisible(false);
				btn_end.setVisible(false);
				MessageBox.show(this, "操作成功!");
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 问题删除逻辑，需要同时删掉违规事件及子表记录【李春娟/2016-09-07】
	 */
	private void onDeleteProblem() {
		BillListPanel listPanel = (BillListPanel) btn_deletePro
				.getBillPanelFrom();
		BillVO billvo = listPanel.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(listPanel);
			return;
		}
		if (!MessageBox.confirm(listPanel, "您确定要删除吗?")) {
			return;
		}
		String id = billvo.getStringValue("id");
		ArrayList sqlList = new ArrayList();
		sqlList.add("delete from ck_problem_info where id =" + id);
		sqlList
				.add("delete from cmp_wardevent_cust where cmp_wardevent_id in(select id from cmp_event where problemid ="
						+ id + ")");
		sqlList
				.add("delete from cmp_wardevent_user where cmp_wardevent_id in(select id from cmp_event where problemid ="
						+ id + ")");
		sqlList.add("delete from cmp_event where problemid =" + id);
		try {
			UIUtil.executeBatchByDS(null, sqlList);
			listPanel.removeSelectedRow();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void mouseDragged(MouseEvent arg0) {
		System.out.println("11111111111111111111");

	}

	public void mouseMoved(MouseEvent arg0) {
		System.out.println("222222222222222222222");
	}

	public void mouseClicked(MouseEvent arg0) {
		System.out.println("3333333333333333333");

	}

	public void mouseEntered(MouseEvent arg0) {
		System.out.println("4444444444444444444444");

	}

	public void mouseExited(MouseEvent arg0) {
		System.out.println("555555555555555555555");

	}

	public void mousePressed(MouseEvent arg0) {
		System.out.println("6666666666666666666");

	}

	public void mouseReleased(MouseEvent arg0) {
		System.out.println("777777777777777777");

	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent event) {

	}

}
