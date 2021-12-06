package com.pushworld.ipushgrc.ui.icheck.p030;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractWLTCompentPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_ChildTable;

/**
 * 检查方案新增或修改窗口【李春娟/2016-08-18】
 * 可修改检查方案或修改底稿设计
 * 底稿设计包括：直接新增、导入检查题纲、导入问题词条、导入以往问题、导入以往底稿、修改、删除、上移、下移
 * @author lcj
 * 
 */
public class CheckSchemeEditDialog extends BillDialog implements ActionListener {
	private BillListPanel schemeListPanel = null;// 检查方案列表
	private BillCardPanel schemeCardPanel = null; // 检查方案卡片
	private BillListPanel manuscriptPanel = null; // 检查底稿
	private WLTButton btn_confirm, btn_cancel;//确定和取消按钮
	private WLTButton btn_add, btn_import, btn_editmanu, btn_deleteManuscript, btn_moveup, btn_movedown, btn_setcode;
	private JPopupMenu importPopMenu;
	private JMenuItem menu_outline, menu_problem_dict, menu_problem, menu_manuscript;//导入项
	private String currPlanid, currSchemeid;//当前计划和方案id
	private int state;//表单状态 0-新增；1-修改；2-浏览
	private HashMap codeMap = null;

	/**
	 * 
	 * @param _type  类型：问题词条；检查提纲
	 * state 表单状态 0-新增；1-修改；2-浏览
	 */
	public CheckSchemeEditDialog(BillListPanel _schemeListPanel, BillCardPanel _schemeCardPanel, int _state, String _currPlanid, String _currSchemeid) {
		super(_schemeListPanel, 900, 750);
		this.schemeListPanel = _schemeListPanel;
		this.schemeCardPanel = _schemeCardPanel;
		this.currPlanid = _currPlanid;
		this.currSchemeid = _currSchemeid;
		manuscriptPanel = new BillListPanel("CK_MANUSCRIPT_DESIGN_LCJ_E01");//检查底稿设计
		this.state = _state;
		if (state == 0) {
			this.setTitle("新增");
		} else if (state == 1) {
			this.setTitle("修改");
			manuscriptPanel.QueryDataByCondition("schemeid = " + currSchemeid);//修改方案时，加载该方案的检查底稿
		} else if (state == 2) {
			manuscriptPanel.QueryDataByCondition("schemeid = " + currSchemeid);//修改方案时，加载该方案的检查底稿
		}
		JPanel southPanel = WLTPanel.createDefaultPanel();
		if (state != 2) {
			//底稿设计列表所有按钮
			btn_add = new WLTButton("直接新增");
			btn_import = new WLTButton("导入▼");
			btn_editmanu = new WLTButton("修改");
			btn_deleteManuscript = new WLTButton("删除");
			btn_moveup = new WLTButton("上移");
			btn_movedown = new WLTButton("下移");
			btn_setcode = new WLTButton("生成编码");

			//底稿设计列表按钮添加事件
			btn_add.addActionListener(this);
			btn_import.addActionListener(this);
			btn_editmanu.addActionListener(this);
			btn_deleteManuscript.addActionListener(this);
			btn_moveup.addActionListener(this);
			btn_movedown.addActionListener(this);
			btn_setcode.addActionListener(this);
			//底稿设计列表添加按钮
			manuscriptPanel.addBatchBillListButton(new WLTButton[] { btn_add, btn_import, btn_editmanu, btn_deleteManuscript, btn_moveup, btn_movedown, btn_setcode });
			manuscriptPanel.repaintBillListButton();

			//窗口下方按钮
			btn_confirm = new WLTButton("确定");
			btn_cancel = new WLTButton("取消");
			btn_confirm.addActionListener(this);
			btn_cancel.addActionListener(this);
			southPanel.add(btn_confirm);
			southPanel.add(btn_cancel);
		} else {
			btn_cancel = new WLTButton("关闭");
			btn_cancel.addActionListener(this);
			southPanel.add(btn_cancel);
		}
		WLTTabbedPane tabPane = new WLTTabbedPane();//页签
		tabPane.addTab("第一步：填写方案基本信息", schemeCardPanel);
		tabPane.addTab("第二步：检查底稿设计", manuscriptPanel);//

		this.add(tabPane);
		this.add(southPanel, BorderLayout.SOUTH);
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_confirm) {//窗口确定按钮逻辑
			onConfirm();
		} else if (obj == btn_cancel) {//窗口取消按钮逻辑
			onCancel();
		} else if (obj == btn_add) {// 直接新增底稿记录
			onAdd();
		} else if (obj == btn_import) {//导入
			onImport();
		} else if (obj == menu_outline) {//导入检查提纲
			onOutline();
		} else if (obj == menu_problem_dict) {//导入问题词条
			onProblem_dict();
		} else if (obj == menu_manuscript) {//导入以往底稿
			onManuscript();
		} else if (obj == menu_problem) {//导入以往问题
			onProblem();
		} else if (obj == btn_editmanu) {//修改底稿
			onEditManu();
		} else if (obj == btn_deleteManuscript) {//删除底稿
			onDeleteManuscript();
		} else if (obj == btn_moveup) {//上移底稿
			onMoveUp();
		} else if (obj == btn_movedown) {//下移底稿
			onMoveDown();
		} else if (obj == btn_setcode) {//生成编码
			onSetCode();
		}
	}

	/**
	 * 重写BillDialog的方法，关闭时判断是否保存过
	 */
	public void closeMe() {
		if (state != 2) {
			schemeCardPanel.dealChildTable(false);//如果直接关闭，则取消子表操作
			onSetCode();//直接关闭也需要设置一下编码，因为上下移动后是自动保存的，直接关闭无法撤销
		}
		this.dispose();
	}

	/**
	 * 方案新增或编辑窗口的【确定】按钮逻辑
	 */
	public void onConfirm() {
		try {
			if (!schemeCardPanel.checkValidate()) {
				return;
			}
			AbstractWLTCompentPanel cp = schemeCardPanel.getCompentByKey("MEMBERWORK");
			if (cp != null) {
				CardCPanel_ChildTable ct = (CardCPanel_ChildTable) cp;
				int row = ct.getBillListPanel().getRowCount();
				ArrayList deptlist = new ArrayList();
				HashMap deptmap = new HashMap();
				for (int i = 0; i < row; i++) {
					String checkeddept = ct.getBillListPanel().getRealValueAtModel(i, "CHECKEDDEPT");
					String[] depts = TBUtil.getTBUtil().split(checkeddept, ";");
					if (depts != null && depts.length > 0) {
						for (int j = 0; j < depts.length; j++) {
							if (deptmap.containsKey(depts[j])) {
								deptlist.add(depts[j]);//重复的机构
							} else {
								deptmap.put(depts[j], depts[j]);
							}
						}
					}
				}
				if (deptlist.size() > 0) {
					String[] deptname = UIUtil.getStringArrayFirstColByDS(null, "select distinct(name) from pub_corp_dept where id in(" + TBUtil.getTBUtil().getInCondition(deptlist) + ")");

					if (deptname != null && deptname.length > 0) {
						StringBuffer sb = new StringBuffer();
						for (int i = 0; i < deptname.length; i++) {
							sb.append(deptname[i] + "、\n");
						}
						MessageBox.show(this, "以下被检查单位重复，请重新分配：\n" + sb.toString().substring(0, sb.toString().length() - 2));
						return;
					}
				}
			}
			schemeCardPanel.updateData();
			schemeCardPanel.dealChildTable(true);
			if (state == 0) {//新增
				int li_newrow = schemeListPanel.newRow(false); //
				schemeListPanel.setBillVOAt(li_newrow, schemeCardPanel.getBillVO(), false);
				schemeListPanel.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); //设置列表该行的数据为初始化状态.
				schemeListPanel.setSelectedRow(li_newrow); //
			} else if (state == 1) {//修改
				schemeListPanel.refreshCurrSelectedRow();
			}
			onSetCode();//设置编码
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showException(this, e);
		}
		this.dispose();
	}

	/**
	 * 设置编码
	 */
	private void onSetCode() {
		try {
			if (codeMap == null) {
				codeMap = UIUtil.getHashMapBySQLByDS(null, "select id,code from CK_PROJECT_LIST");
			}
			int count = manuscriptPanel.getRowCount();
			int seq = 1;//自动生成的流水号
			for (int i = 0; i < count; i++) {//进行底稿排序
				String parentid = manuscriptPanel.getRealValueAtModel(i, "parentid");//目录
				String code = "";
				manuscriptPanel.setRowStatusAs(i, WLTConstants.BILLDATAEDITSTATE_UPDATE); //设置列表该行的数据为修改状态
				manuscriptPanel.setRealValueAt("" + (i + 1), i, "seq");//设置序号
				if (codeMap.containsKey(parentid)) {
					code = (String) codeMap.get(parentid);
					if (code != null && !"".equals(code)) {
						if (i == 0) {//第一行直接添加01
							manuscriptPanel.setRealValueAt(code + "01", i, "code");
							seq = 1;
						} else {//除第一行，其他记录都跟前面编码比较一下
							if (parentid != null && parentid.equals(manuscriptPanel.getRealValueAtModel(i - 1, "parentid"))) {//如果当前行和前面一行属于同一个三级目录，则流水号递增
								seq++;
								if (seq > 9) {
									manuscriptPanel.setRealValueAt(code + seq, i, "code");
								} else {
									manuscriptPanel.setRealValueAt(code + "0" + seq, i, "code");
								}
							} else {
								seq = 1;
								manuscriptPanel.setRealValueAt(code + "01", i, "code");
							}
						}
					} else {
						manuscriptPanel.setRealValueAt("", i, "code");
					}
				} else {
					manuscriptPanel.setRealValueAt("", i, "code");
				}

			}
			manuscriptPanel.saveData();
		} catch (WLTRemoteException e) {
			e.printStackTrace();
			MessageBox.showException(this, e);
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showException(this, e);
		}
	}

	/**
	 * 方案新增或编辑窗口的【取消】按钮逻辑
	 */
	public void onCancel() {
		if (state == 0) {//新增
			ArrayList sqlList = new ArrayList();
			sqlList.add("delete from CK_MEMBER_WORK where SCHEMEID =" + currSchemeid);//删除人员分工子表记录
			sqlList.add("delete from ck_manuscript_design where SCHEMEID =" + currSchemeid);//删除检查底稿设计记录
			try {
				UIUtil.executeBatchByDS(null, sqlList);
			} catch (Exception e) {
				e.printStackTrace();
				MessageBox.showException(this, e);
			}
		} else if (state == 1) {//处理人员分工子表新增或删除，暂不处理修改 和 检查底稿设计？
			schemeCardPanel.dealChildTable(false);
		}
		this.dispose();
	}

	/**
	 * 直接新增底稿记录
	 */
	private void onAdd() {
		BillCardPanel cardPanel = new BillCardPanel(manuscriptPanel.templetVO); //创建一个卡片面板
		BillCardDialog dialog = new BillCardDialog(manuscriptPanel, manuscriptPanel.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); //弹出卡片新增框
		cardPanel.setLoaderBillFormatPanel(manuscriptPanel.getLoaderBillFormatPanel()); //将列表的BillFormatPanel的句柄传给卡片
		cardPanel.insertRow(); //卡片新增一行!
		cardPanel.setRealValueAt("importtype", "直接新增");
		cardPanel.setRealValueAt("planid", currPlanid);
		cardPanel.setRealValueAt("schemeid", currSchemeid);
		cardPanel.setRealValueAt("importid", cardPanel.getRealValueAt("id"));
		String seq = "1";
		if (manuscriptPanel.getRowCount() > 0) {
			seq = manuscriptPanel.getRealValueAtModel(manuscriptPanel.getRowCount() - 1, "seq");
			if (seq == null || "".equals(seq)) {
				seq = "1";
			} else {
				int int_seq = Integer.parseInt(seq);
				int_seq++;
				seq = int_seq + "";
			}
		}
		cardPanel.setRealValueAt("seq", seq);
		dialog.setVisible(true); //显示卡片窗口
		if (dialog.getCloseType() == 1) { //如是是点击确定返回!将则卡片中的数据赋给列表!
			if (manuscriptPanel.getRowCount() > 0) {
				manuscriptPanel.setSelectedRow(manuscriptPanel.getRowCount() - 1);//最后一行加入
			}
			int li_newrow = manuscriptPanel.newRow(false, false); //
			manuscriptPanel.setBillVOAt(li_newrow, dialog.getBillVO(), false);
			manuscriptPanel.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); //设置列表该行的数据为初始化状态.
			manuscriptPanel.setSelectedRow(li_newrow); //
		}

	}

	/**
	 * 导入
	 */
	private void onImport() {
		if (importPopMenu == null) {
			importPopMenu = new JPopupMenu(); //
			menu_outline = new JMenuItem("导入检查题纲");
			menu_problem_dict = new JMenuItem("导入问题词条");
			menu_manuscript = new JMenuItem("导入以往底稿");
			//menu_problem = new JMenuItem("导入以往问题");//因以往问题是针对某个检查单位的问题，不是所有检查单位，故暂不关联【李春娟/2016-09-19】

			menu_outline.addActionListener(this);
			menu_problem_dict.addActionListener(this);
			menu_manuscript.addActionListener(this);
			//menu_problem.addActionListener(this);

			importPopMenu.add(menu_outline);
			importPopMenu.add(menu_problem_dict);
			importPopMenu.add(menu_manuscript);
			//importPopMenu.add(menu_problem);
		}
		importPopMenu.show(btn_import, btn_import.getMousePosition().x, btn_import.getMousePosition().y);
	}

	/**
	 * 导入检查提纲
	 */
	private void onOutline() {
		importOutlineOrProblem_dict("检查提纲");
	}

	/**
	 * 导入问题词条
	 */
	private void onProblem_dict() {
		importOutlineOrProblem_dict("问题词条");
	}

	/**
	 * 导入检查提纲或问题词条
	 * @param _type
	 */
	private void importOutlineOrProblem_dict(String _type) {
		CheckListSelectDialog selectDialog = new CheckListSelectDialog(manuscriptPanel, _type);
		selectDialog.setVisible(true);
		BillVO[] billvos = selectDialog.getReturnVOs();
		if (billvos != null) {
			BillVO[] allBillVOs = manuscriptPanel.getAllBillVOs();
			StringBuffer sb_msg = new StringBuffer();
			int num = 1;

			String seq = "1";
			if (manuscriptPanel.getRowCount() > 0) {
				seq = manuscriptPanel.getRealValueAtModel(manuscriptPanel.getRowCount() - 1, "seq");
				if (seq == null || "".equals(seq)) {
					seq = "1";
				} else {
					int int_seq = Integer.parseInt(seq);
					int_seq++;
					seq = int_seq + "";
				}
			}
			int int_seq = Integer.parseInt(seq);

			for (int i = 0; i < billvos.length; i++) {
				BillVO billvo = billvos[i];
				boolean isfind = false;
				for (int j = 0; j < allBillVOs.length; j++) {//遍历是否有重复记录，如果修改后则认为不重复
					BillVO listVO = allBillVOs[j];
					String isedit = listVO.getStringValue("isedit");
					if (!"Y".equals(isedit)) {
						String importtype = listVO.getStringValue("importtype");
						String importid = listVO.getStringValue("importid", "");
						if (_type.equals(importtype) && importid.equals(billvo.getStringValue("id"))) {
							isfind = true;
							if (sb_msg.length() == 0) {
								sb_msg.append("以下重复记录未导入：\n");
							}
							if ("问题词条".equals(_type)) {
								sb_msg.append(num + "、" + billvo.getStringValue("dictname", "") + "\n");
							} else {
								sb_msg.append(num + "、" + billvo.getStringValue("checkPoints", "") + "\n");
							}
							num++;
						}
					}
				}
				if (isfind) {
					continue;
				}
				if (manuscriptPanel.getRowCount() > 0) {
					manuscriptPanel.setSelectedRow(manuscriptPanel.getRowCount() - 1);//最后一行加入
				}
				int li_newrow = manuscriptPanel.newRow(false, false); //
				billvos[i].setObject("seq", new StringItemVO(int_seq + ""));
				int_seq++;
				manuscriptPanel.setBillVOAt(li_newrow, billvos[i], false);
				try {
					manuscriptPanel.setRealValueAt(UIUtil.getSequenceNextValByDS(null, "S_CK_MANUSCRIPT_DESIGN"), li_newrow, "id");
					manuscriptPanel.setRealValueAt(_type, li_newrow, "importtype");
					manuscriptPanel.setRealValueAt(billvos[i].getStringValue("id"), li_newrow, "importid");
					manuscriptPanel.setRealValueAt(currPlanid, li_newrow, "planid");
					manuscriptPanel.setRealValueAt(currSchemeid, li_newrow, "schemeid");
					if ("问题词条".equals(_type)) {
						manuscriptPanel.setRealValueAt(billvos[i].getStringValue("id"), li_newrow, "dictid");//检查底稿记录问题词条id，以便后面实施时录入问题可以关联问题词条【李春娟/2016-09-28】
						manuscriptPanel.setRealValueAt(billvos[i].getStringValue("dictname"), li_newrow, "checkPoints");
					}
					manuscriptPanel.setRealValueAt(billvos[i].getStringValue("checkMode"), li_newrow, "checkMode");//增加检查方式（现场检查、非现场检查），默认为现场检查【李春娟/2016-10-08】
					manuscriptPanel.setRealValueAt("N", li_newrow, "isedit");
					manuscriptPanel.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INSERT); //设置列表该行的数据为新增状态
				} catch (WLTRemoteException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			manuscriptPanel.saveData();
			manuscriptPanel.clearSelection();
			if (sb_msg.length() > 0) {
				MessageBox.show(this, sb_msg.toString());
			}
		}
	}

	/**
	 * 导入以往底稿
	 */
	private void onManuscript() {
		BillListDialog billlistDialog = new BillListDialog(this, "导入以往底稿，请选择一个方案，点击下一步", "CK_SCHEME_LCJ_E01");
		billlistDialog.getBtn_confirm().setText("下一步");
		billlistDialog.getBilllistPanel().QueryDataByCondition(null);
		billlistDialog.getBilllistPanel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		billlistDialog.getBilllistPanel().addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));
		billlistDialog.getBilllistPanel().repaintBillListButton();
		billlistDialog.setVisible(true);
		if (billlistDialog.getCloseType() != 1) {
			return;
		}
		BillVO schemeVo = billlistDialog.getReturnBillVOs()[0];

		BillListDialog listDialog = new BillListDialog(this, "请勾选需要导入的底稿", "CK_MANUSCRIPT_DESIGN_LCJ_E01", "schemeid = " + schemeVo.getStringValue("id"), 900, 800, false);
		listDialog.getBilllistPanel().setRowNumberChecked(true);//设置为勾选【李春娟/2016-08-26】
		listDialog.setVisible(true);
		if (listDialog.getCloseType() == 1) {
			BillVO[] billvos = listDialog.getBilllistPanel().getCheckedBillVOs();//返回需要导入的以往底稿 【李春娟/2016-08-26】
			if (billvos != null) {
				StringBuffer sb_msg = new StringBuffer();
				int num = 1;

				String seq = "1";
				if (manuscriptPanel.getRowCount() > 0) {
					seq = manuscriptPanel.getRealValueAtModel(manuscriptPanel.getRowCount() - 1, "seq");
					if (seq == null || "".equals(seq)) {
						seq = "1";
					} else {
						int int_seq = Integer.parseInt(seq);
						int_seq++;
						seq = int_seq + "";
					}
				}
				int int_seq = Integer.parseInt(seq);

				BillVO[] allBillVOs = manuscriptPanel.getAllBillVOs();//列表上已有的底稿
				for (int i = 0; i < billvos.length; i++) {//返回需要导入的以往底稿
					BillVO billvo = billvos[i];
					boolean isfind = false;
					for (int j = 0; j < allBillVOs.length; j++) {//遍历是否有重复记录，如果修改后则认为不重复
						BillVO listVO = allBillVOs[j];
						String isedit = listVO.getStringValue("isedit");
						if (!"Y".equals(isedit)) {
							String importtype = listVO.getStringValue("importtype");
							String importid = listVO.getStringValue("importid", "");
							if ("以往底稿".equals(importtype) && importid.equals(billvo.getStringValue("id"))) {
								isfind = true;
								if (sb_msg.length() == 0) {
									sb_msg.append("以下重复记录未导入：\n");
								}
								sb_msg.append(num + "、" + billvo.getStringValue("checkPoints", "") + "\n");
								num++;
							}
						}
					}
					if (isfind) {
						continue;
					}
					if (manuscriptPanel.getRowCount() > 0) {
						manuscriptPanel.setSelectedRow(manuscriptPanel.getRowCount() - 1);//最后一行加入
					}
					int li_newrow = manuscriptPanel.newRow(false, false); //
					billvos[i].setObject("seq", new StringItemVO(int_seq + ""));
					int_seq++;
					manuscriptPanel.setBillVOAt(li_newrow, billvos[i], false);
					try {
						manuscriptPanel.setRealValueAt(UIUtil.getSequenceNextValByDS(null, "S_CK_MANUSCRIPT_DESIGN"), li_newrow, "id");
						manuscriptPanel.setRealValueAt("以往底稿", li_newrow, "importtype");
						manuscriptPanel.setRealValueAt(billvos[i].getStringValue("id"), li_newrow, "importid");
						manuscriptPanel.setRealValueAt(billvos[i].getStringValue("dictid"), li_newrow, "dictid");//问题词条id【李春娟/2016-09-28】
						manuscriptPanel.setRealValueAt(billvos[i].getStringValue("dictname"), li_newrow, "dictname");//问题词条描述
						manuscriptPanel.setRealValueAt(currPlanid, li_newrow, "planid");
						manuscriptPanel.setRealValueAt(currSchemeid, li_newrow, "schemeid");
						manuscriptPanel.setRealValueAt(billvos[i].getStringValue("checkMode"), li_newrow, "checkMode");//增加检查方式（现场检查、非现场检查）【李春娟/2016-10-08】
						manuscriptPanel.setRealValueAt("N", li_newrow, "isedit");
						manuscriptPanel.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INSERT); //设置列表该行的数据为新增状态
					} catch (WLTRemoteException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				manuscriptPanel.saveData();
				manuscriptPanel.clearSelection();
				if (sb_msg.length() > 0) {
					MessageBox.show(this, sb_msg.toString());
				}
			}
		}

	}

	/**
	 * 导入以往问题
	 */
	private void onProblem() {
		BillListDialog billlistDialog = new BillListDialog(this, "导入以往问题，请选择一个方案，点击下一步", "CK_SCHEME_LCJ_E01");
		billlistDialog.getBtn_confirm().setText("下一步");
		billlistDialog.getBilllistPanel().QueryDataByCondition(null);
		billlistDialog.getBilllistPanel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		billlistDialog.getBilllistPanel().addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));
		billlistDialog.getBilllistPanel().repaintBillListButton();
		billlistDialog.setVisible(true);
		if (billlistDialog.getCloseType() != 1) {
			return;
		}
		BillVO schemeVo = billlistDialog.getReturnBillVOs()[0];

		BillListDialog listDialog = new BillListDialog(this, "请勾选需要导入的问题", "CK_PROBLEM_INFO_SCY_E01", "schemeid = " + schemeVo.getStringValue("id"), 900, 800);
		listDialog.getBilllistPanel().setRowNumberChecked(true);////设置为勾选【李春娟/2016-08-26】
		listDialog.setVisible(true);
		if (listDialog.getCloseType() == 1) {
			BillVO[] billvos = listDialog.getBilllistPanel().getCheckedBillVOs();//返回需要导入的问题
			if (billvos != null) {
				StringBuffer sb_msg = new StringBuffer();
				int num = 1;
				String seq = "1";
				if (manuscriptPanel.getRowCount() > 0) {
					seq = manuscriptPanel.getRealValueAtModel(manuscriptPanel.getRowCount() - 1, "seq");
					if (seq == null || "".equals(seq)) {
						seq = "1";
					} else {
						int int_seq = Integer.parseInt(seq);
						int_seq++;
						seq = int_seq + "";
					}
				}
				int int_seq = Integer.parseInt(seq);

				BillVO[] allBillVOs = manuscriptPanel.getAllBillVOs();
				for (int i = 0; i < billvos.length; i++) {//返回需要导入的问题
					BillVO billvo = billvos[i];
					boolean isfind = false;
					for (int j = 0; j < allBillVOs.length; j++) {//遍历是否有重复记录，如果修改后则认为不重复
						BillVO listVO = allBillVOs[j];
						String isedit = listVO.getStringValue("isedit");
						if (!"Y".equals(isedit)) {
							String importtype = listVO.getStringValue("importtype");
							String importid = listVO.getStringValue("importid", "");
							if ("以往问题".equals(importtype) && importid.equals(billvo.getStringValue("id"))) {
								isfind = true;
								if (sb_msg.length() == 0) {
									sb_msg.append("以下重复记录未导入：\n");
								}
								sb_msg.append(num + "、" + billvo.getStringValue("problemInfo", "") + "\n");//问题描述
								num++;
							}
						}
					}
					if (isfind) {
						continue;
					}
					if (manuscriptPanel.getRowCount() > 0) {
						manuscriptPanel.setSelectedRow(manuscriptPanel.getRowCount() - 1);//最后一行加入
					}
					int li_newrow = manuscriptPanel.newRow(false, false); //
					billvos[i].setObject("seq", new StringItemVO(int_seq + ""));
					int_seq++;
					manuscriptPanel.setBillVOAt(li_newrow, billvos[i], false);
					try {
						manuscriptPanel.setRealValueAt(UIUtil.getSequenceNextValByDS(null, "S_CK_MANUSCRIPT_DESIGN"), li_newrow, "id");
						manuscriptPanel.setRealValueAt("以往问题", li_newrow, "importtype");
						manuscriptPanel.setRealValueAt(billvos[i].getStringValue("id"), li_newrow, "importid");
						manuscriptPanel.setRealValueAt(currPlanid, li_newrow, "planid");
						manuscriptPanel.setRealValueAt(currSchemeid, li_newrow, "schemeid");
						manuscriptPanel.setRealValueAt(billvos[i].getStringValue("dictid"), li_newrow, "dictid");//问题词条id【李春娟/2016-09-28】
						manuscriptPanel.setRealValueAt(billvos[i].getStringValue("dictname"), li_newrow, "dictname");//问题词条描述
						manuscriptPanel.setRealValueAt(billvos[i].getStringValue("checkmode"), li_newrow, "checkmode");//增加检查方式（现场检查、非现场检查）【李春娟/2016-10-08】
						manuscriptPanel.setRealValueAt("N", li_newrow, "isedit");
						manuscriptPanel.setRealValueAt(billvos[i].getStringValue("thirdid"), li_newrow, "parentid");//目录id
						manuscriptPanel.setRealValueAt(billvos[i].getStringValue("problemInfo"), li_newrow, "checkPoints");
						manuscriptPanel.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INSERT); //设置列表该行的数据为新增状态
					} catch (WLTRemoteException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				manuscriptPanel.saveData();
				manuscriptPanel.clearSelection();
				if (sb_msg.length() > 0) {
					MessageBox.show(this, sb_msg.toString());
				}
			}
		}
	}

	/**
	 * 修改底稿
	 */
	private void onEditManu() {
		BillVO billVO = manuscriptPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		BillCardPanel cardPanel = new BillCardPanel(manuscriptPanel.templetVO);
		cardPanel.setBillVO(billVO); //
		cardPanel.setRealValueAt("isedit", "Y");
		BillCardDialog dialog = new BillCardDialog(manuscriptPanel, manuscriptPanel.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {
			if (manuscriptPanel.getSelectedRow() != -1) {//主要解决刷新模板时候可能出现的错
				manuscriptPanel.setBillVOAt(manuscriptPanel.getSelectedRow(), dialog.getBillVO());
				manuscriptPanel.setRowStatusAs(manuscriptPanel.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT);
			}
		}
	}

	/**
	 * 删除底稿
	 */
	private void onDeleteManuscript() {
		manuscriptPanel.doDelete(false);
	}

	/**
	 * 上移底稿
	 */
	private void onMoveUp() {
		BillVO billVO = manuscriptPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		BillListPanel billList = manuscriptPanel; //
		billList.moveUpRow(); //上移
		int li_rowcount = billList.getRowCount();
		String seqfild = billList.getTempletVO().getTreeseqfield();//如果配置了树面板排序列，则用该字段排序，否则默认为seq
		if (seqfild == null || seqfild.trim().equals("")) {
			seqfild = "seq";
		}
		for (int i = 0; i < li_rowcount; i++) {
			//以前遇到过，一个表进行行上移或下移操作并保存后，发现数据并没有变，因为以前这里没有处理如果seq为空的情况。并且处理了seqfild值非数字的情况，
			//注意第二个判断用billList.getRealValueAtModel()得到的是字符串，而billList.getValueAt()得到的是StringItemVO对象
			if (billList.getValueAt(i, seqfild) == null || !((i + 1) + "").equals(billList.getRealValueAtModel(i, seqfild))) {
				billList.setValueAt(new StringItemVO("" + (i + 1)), i, seqfild); //
				if (WLTConstants.BILLDATAEDITSTATE_INIT.equalsIgnoreCase(billList.getRowNumberEditState(i))) {//如果是初始状态再设置更新，否则新增状态的数据执行update不能保存【李春娟/2014-10-31】
					billList.setRowStatusAs(i, WLTConstants.BILLDATAEDITSTATE_UPDATE); //
				}
			}
		}
		billList.saveData();
	}

	/**
	 * 下移底稿
	 */
	private void onMoveDown() {
		BillVO billVO = manuscriptPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		BillListPanel billList = manuscriptPanel; //
		billList.moveDownRow(); //下移
		int li_rowcount = billList.getRowCount();
		String seqfild = billList.getTempletVO().getTreeseqfield();//如果配置了树面板排序列，则用该字段排序，否则默认为seq
		if (seqfild == null || seqfild.trim().equals("")) {
			seqfild = "seq";
		}
		for (int i = 0; i < li_rowcount; i++) {
			//以前遇到过，一个表进行行上移或下移操作并保存后，发现数据并没有变，因为以前这里没有处理如果seq为空的情况
			if (billList.getValueAt(i, seqfild) == null || !((i + 1) + "").equals(billList.getRealValueAtModel(i, seqfild))) {
				billList.setValueAt(new StringItemVO("" + (i + 1)), i, seqfild); //
				if (WLTConstants.BILLDATAEDITSTATE_INIT.equalsIgnoreCase(billList.getRowNumberEditState(i))) {//如果是初始状态再设置更新，否则新增状态的数据执行update不能保存【李春娟/2014-10-31】
					billList.setRowStatusAs(i, WLTConstants.BILLDATAEDITSTATE_UPDATE); //
				}
			}
		}
		billList.saveData();
	}
}
