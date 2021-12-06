package com.pushworld.ipushlbs.ui.constactcheck.p020;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardEditEvent;
import cn.com.infostrategy.ui.mdata.BillCardEditListener;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.BillOfficeDialog;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;

import com.pushworld.ipushgrc.ui.IPushGRCServiceIfc;
import com.pushworld.ipushlbs.ui.printcomm.CommonHtmlOfficeConfig;

/**
 * 非格式合同审查--拦截器
 * 
 * @author wupeng 非格式合同审查新建问题说明：
 *         如果是引用的非格式范本，那么格式审查中的DEAL_ID是有值的，这就说明是引用的，因为ID是固定隐藏的，所以合同的名字等就任他修改吧
 *         如果不是引用，则格式审查中的DEAL_ID就是没有值的，这个字段设为不必输，这样就可以区分是引用的还是直接创建的了。 modify
 *         YINLiang
 */
public class UnFormatConstactCheckIncp extends AbstractWorkPanel implements ActionListener, BillCardEditListener, BillListHtmlHrefListener, BillListSelectListener {

	BillListPanel docList = null;// 格式文本合同按钮
	WLTButton insert = null;// 列表面板上的新增按钮
	WLTButton edit = null;// 列表面板上的编辑按钮
	WLTButton delete = null;
	WLTButton wf_watchBtn = null;
	WLTButton btn_insert_card, btn_confirm; // 弹出新建卡片中的新增按钮
	BillListDialog billListDialog; // 点击引用合同范本时弹出的窗口
	BillCardPanel insertCard = null;// 列表弹出新增的卡片

	@Override
	public void initialize() {
		docList = new BillListPanel("UNFORMAT_DEAL_CHECK_CODE1");
		docList.addBillListHtmlHrefListener(this);
		docList.addBillListSelectListener(this);
		insert = new WLTButton("新建");
		insert.addActionListener(this);
		edit = new WLTButton("编辑");
		edit.addActionListener(this);
		delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
		delete.addActionListener(this);
		wf_watchBtn = WLTButton.createButtonByType(WLTButton.LIST_WORKFLOWSTART_MONITOR);
		wf_watchBtn.addActionListener(this);

		docList.addBatchBillListButton(new WLTButton[] { insert, edit, delete, WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD), wf_watchBtn });
		docList.repaintBillListButton();
		this.add(docList);
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == insert)// 自定义新增
			doInsert();
		else if (obj == edit)// 自定义编辑
			doEdit();
		else if (obj == wf_watchBtn)// 流程发起/监控
			wfStartOrMonitor();
		else if (obj == delete)
			onDelete();
		else if (obj == btn_insert_card)
			doQuoted(); // 引用合同范本
		else if (obj == btn_confirm)
			doInsertContact();

	}

	// 自定义新增
	private void doInsert() {
		BillListPanel list = this.getbilListPanel();
		insertCard = new BillCardPanel(list.templetVO);
		btn_insert_card = new WLTButton("引用非格式合同范本");
		insertCard.addBillCardButton(btn_insert_card);
		insertCard.repaintBillCardButton(); // 将 引用合同 按钮加上
		btn_insert_card.addActionListener(this);

		BillCardDialog dialog = new BillCardDialog(list, "新增" + list.templetVO.getTempletname(), insertCard, WLTConstants.BILLDATAEDITSTATE_INSERT);
		dialog.getBillcardPanel().insertRow();// 设定指定的id
		dialog.setVisible(true);// 显示

		if (dialog.getCloseType() == 1) {// 将新增的数据显示出来
			int newRow = list.newRow();
			list.setBillVOAt(newRow, dialog.getBillVO());
			list.setSelectedRow(newRow);
			list.refreshCurrSelectedRow();
		} else { // 没有保存这条记录，那么复制的这个服务器端文件的副本也没用了，删除
			this.deleteFile(dialog.getBillVO(), null, true);
		}
	}

	// 引用合同范本
	private void doQuoted() {
		billListDialog = new BillListDialog(insertCard, "", "LBS_UNSTDFILE_CODE3"); // 加载合同范本库
		BillListPanel billListPanel = billListDialog.getBilllistPanel();
		billListPanel.addBillListHtmlHrefListener(this);
		billListDialog.setVisible(true);
		if (billListDialog.getCloseType() != 1) {
			return;
		}
		doInsertContact();
	}

	// 通过选定的合同范本 自动插入合同信息
	@SuppressWarnings("unchecked")
	private void doInsertContact() {
		BillListPanel panel_insert = billListDialog.getBilllistPanel(); // 当前panel
		BillVO billvo_insert = panel_insert.getSelectedBillVO(); // 选择的行billvo
		HashVO hashvo;
		try {
			IPushGRCServiceIfc server = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
			HashMap oldfileMap = new HashMap();
			hashvo = UIUtil.getHashVoArrayByDS(null, "Select lb.id,lb.code,lb.Name,lb.testfile,lb.Type,co.Name typename,lb.busiid,st.Name businame,lb.property,pu.name proname " + "From lbs_unstdfile lb,CONTRACTBUSI co,LBS_STDFILE_TYPE st,PUB_COMBOBOXDICT pu  "
					+ "Where lb.Type=co.Id And lb.busiid=st.Id and pu.Type ='合同_合同性质2' and pu.id=lb.property " + "and lb.id = " + billvo_insert.getStringValue("id"))[0];

			String officeFileName = hashvo.getStringValue("testfile"); // 合同文件名称
			String dealId = hashvo.getStringValue("id"); // 合同范本ID
			String dealCode = hashvo.getStringValue("code"); // 合同范本编码
			String dealName = hashvo.getStringValue("name"); // 合同范本名称
			if (officeFileName != null && !officeFileName.equals("")) {
				oldfileMap.put("office", officeFileName);
			}
			// 拷贝
			HashMap newFiles = server.bargainCopyFile(oldfileMap);
			String newOfficeName = (String) newFiles.get("office");
			// 赋值
			insertCard.setValueAt("DEAL_ID", new StringItemVO(dealId));
			insertCard.setValueAt("DEALDOC_NAME", new StringItemVO(dealName));
			insertCard.setValueAt("DEAL_CODE", new StringItemVO("非格式合同/" + UIUtil.getCurrDate() + "/" + dealCode + "/" + UIUtil.getSequenceNextValByDS(null, "S_UNFORMAT_DEAL_CHECK")));
			insertCard.setValueAt("TYPE", new RefItemVO(hashvo.getStringValue("type"), "", hashvo.getStringValue("typename")));
			insertCard.setValueAt("BUSIID", new RefItemVO(hashvo.getStringValue("busiid"), "", hashvo.getStringValue("businame")));
			insertCard.setValueAt("PROPERTY", new ComBoxItemVO(hashvo.getStringValue("property"), "", hashvo.getStringValue("proname")));
			if (newOfficeName != null && !newOfficeName.equals("")) {
				String chineseName = new TBUtil().convertHexStringToStr(newOfficeName.substring(newOfficeName.lastIndexOf("_") + 1, newOfficeName.lastIndexOf("."))) + newOfficeName.substring(newOfficeName.lastIndexOf("."));// 加上后缀名
				insertCard.setValueAt("DEAL_CONTENT", new RefItemVO(newOfficeName, "", chineseName));// 设置“合同正文”一项的值,这一项是个RefItemVO
			} else {
				MessageBox.show(insertCard, "该合同没有范本!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void onDelete() {
		BillListPanel billList = (BillListPanel) this.getbilListPanel(); //
		int li_selRow = billList.getSelectedRow();
		if (li_selRow < 0) {
			MessageBox.showSelectOne(billList); //
			return;
		}
		if (!MessageBox.confirmDel(billList)) {
			return; //
		}
		this.deleteFile(billList.getSelectedBillVO(), null, true);// 删除附件及文件
		billList.doDelete(true); // 真正进行删除操作!!!
	}

	/**
	 * 流程发起、、、监控
	 */
	private void wfStartOrMonitor() {
		BillListPanel list = this.getbilListPanel();
		BillVO vo = list.getSelectedBillVO();
		if (vo == null) {
			MessageBox.showSelectOne(list);
			return;
		}

		if (!vo.containsKey("wfprinstanceid")) {
			MessageBox.show(list, "选中的记录中没有定义工作流字段(wfprinstanceid)!"); //
			return; //
		}

		String str_wfprinstanceid = vo.getStringValue("wfprinstanceid"); //
		if (str_wfprinstanceid == null || str_wfprinstanceid.trim().equals("")) {// 如果流程未发起，则发起流程，否则监控流程
			try {
				String curYear = UIUtil.getCurrDate().substring(0, 4);
				new cn.com.infostrategy.ui.workflow.WorkFlowDealActionFactory().dealAction("deal", list, null); // 处理动作!f发起流程
				// 发起工作流的同时，将工作流状态改为审批中，并且为其创建审查号
				// 注：重新建了一张表，单独放当前年的最大值，从新的一年开始，编号从1开始 表中type 1为格式合同 2为非格式合同
				String prinstanceid = UIUtil.getStringValueByDS(null, "select wfprinstanceid from  " + list.templetVO.getTablename() + " where id = " + vo.getStringValue("id"));
				String process_code_max = UIUtil.getStringValueByDS(null, "select maxcode from lbs_process_id where type='2' and year = '" + curYear + "'"); // 取得当前年最大的合同审批号
				if (prinstanceid == null) // 如果流程并未发起
					return;
				if (prinstanceid.equals(""))
					return;
				else {
					List<String> sqlList = new ArrayList<String>();
					String process_code; // 审批号
					String sql_up_maxcode; // 更新表code
					// 更新审批状态及审批号
					if ("".equals(process_code_max) || process_code_max == null) { // 如果此前没有数据
						process_code = curYear + "非格式【审】 - " + 1;
						sql_up_maxcode = "insert into lbs_process_id values ('2','" + curYear + "'," + 1 + ") ";
					} else {
						process_code = curYear + "非格式【审】 - " + (Integer.parseInt(process_code_max) + 1);
						sql_up_maxcode = "update lbs_process_id set maxcode = " + (Integer.parseInt(process_code_max) + 1) + " where type = '2' and year = '" + curYear + "' ";

					}
					process_code = curYear + "非格式【审】 - " + UIUtil.getSequenceNextValByDS(null, "s_process_code");
					String sql_up = "update " + vo.getSaveTableName() + "  set endtype = '审批中',process_code = '" + process_code + "' where id = " + vo.getStringValue("id");
					sqlList.add(sql_up_maxcode);
					sqlList.add(sql_up);
					UIUtil.executeBatchByDS(null, sqlList); // 执行上边两条sql

					list.refreshData();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {// 流程监控
			cn.com.infostrategy.ui.workflow.engine.WorkflowMonitorDialog wfMonitorDialog = new cn.com.infostrategy.ui.workflow.engine.WorkflowMonitorDialog(list, str_wfprinstanceid, vo); //
			wfMonitorDialog.setMaxWindowMenuBar();
			wfMonitorDialog.setVisible(true);
		}

	}

	// 自定义编辑
	private void doEdit() {
		BillListPanel list = this.getbilListPanel();
		if (list.getSelectedBillVO() == null) {
			MessageBox.showSelectOne(list);
			return;
		}
		String oldFile = list.getSelectedBillVO().getStringValue("DEAL_CONTENT");// 得到老的word文件
		BillCardPanel editCard = new BillCardPanel(list.templetVO);
		editCard.addBillCardEditListener(this);
		editCard.setBillVO(list.getSelectedBillVO());

		BillCardDialog dialog = new BillCardDialog(list, "编辑" + list.getTempletVO().getTempletname(), editCard, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.setVisible(true);

		if (dialog.getCloseType() == 1) {// 如果点击了保存按钮
			list.setBillVOAt(list.getSelectedRow(), dialog.getBillVO());
			list.refreshCurrSelectedRow();
		} else {
			return;
		}

		String newFile = dialog.getBillcardPanel().getBillVO().getStringValue("DEAL_CONTENT");// 得到新的文件
		if (oldFile != null) {// 老文件不为空
			if (newFile == null || oldFile.equals(newFile)) {// 如果新文件是空或者新文件与老文件不同，责删除老文件
				this.deleteFile(null, oldFile, true);
			}
		}

	}

	// 删除文件.可以通过billvo删除，也可以直接通过文件名删除
	private void deleteFile(BillVO vo, String path, boolean isOfficeFile) {
		try {
			FrameWorkMetaDataServiceIfc serives = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class);

			if (vo != null) {// 通过billvo删除文件
				String officefile = vo.getStringValue("DEAL_CONTENT");
				String tachfile = vo.getStringValue("FILES");
				if (officefile != null && !officefile.equals("")) {
					serives.deleteOfficeFileName(officefile);
				}
				if (tachfile != null && !tachfile.equals("")) {
					String files[] = tachfile.split(";");
					for (int i = 0; i < files.length; i++) {
						if (files[i] != null && !files[i].equals("")) {
							serives.deleteZipFileName(files[i]); // 删除附件
						}
					}
				}
			}

			if (path != null) {// 通过路径删除文件
				if (isOfficeFile)
					serives.deleteOfficeFileName(path);
				else
					serives.deleteOfficeFileName(path);
			}

		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 卡片编辑监听器
	@SuppressWarnings("unchecked")
	public void onBillCardValueChanged(BillCardEditEvent event) {
		String itemkey = event.getItemKey();
		BillCardPanel card = (BillCardPanel) event.getSource();

		if (itemkey != null) {
			if (itemkey.equalsIgnoreCase("DEALDOC_NAME")) {// 如果发生变化的是 合同文本名称
				// 的列
				RefItemVO refvo = (RefItemVO) event.getNewObject();
				if (refvo == null) {
					card.reset("DEAL_CONTENT");
					return;
				}
				String id = refvo.getId();
				String dealCode = null;// 合同编码
				try {
					IPushGRCServiceIfc server = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
					HashMap oldfileMap = new HashMap();
					String officeFileName = null;// office文件

					// 从数据库中查处office文件的名字和合同编码
					if (getbilListPanel().getTempletVO().getTempletcode().equalsIgnoreCase("FORMAT_DEAL_CHECK_CODE1")) {
						HashVO hashvo = UIUtil.getHashVoArrayByDS(null, "select testfile ,code from v_lbs_stand_check_file where id = " + id)[0];
						officeFileName = hashvo.getStringValue("testfile"); // 合同文件名称
						dealCode = hashvo.getStringValue("code"); // 合同范本编码

					} else if (getbilListPanel().getTempletVO().getTempletcode().equalsIgnoreCase("UNFORMAT_DEAL_CHECK_CODE1")) {
						officeFileName = UIUtil.getStringValueByDS(getbilListPanel().getDataSourceName(), "select testfile from LBS_UNSTDFILE where id =" + id);// 得到文件，名字
						dealCode = UIUtil.getStringValueByDS(getbilListPanel().getDataSourceName(), "select code from V_LBS_STAND_CHECK_FILE where id =" + id);// 得到合同编码
					}

					if (officeFileName != null && !officeFileName.equals("")) {
						oldfileMap.put("office", officeFileName);
					}

					// 拷贝
					HashMap newFiles = server.bargainCopyFile(oldfileMap);
					String newOfficeName = (String) newFiles.get("office");
					// 赋值
					card.setValueAt("deal_code", new StringItemVO(dealCode));
					if (newOfficeName != null && !newOfficeName.equals("")) {

						String chineseName = new TBUtil().convertHexStringToStr(newOfficeName.substring(newOfficeName.lastIndexOf("_") + 1, newOfficeName.lastIndexOf("."))) + newOfficeName.substring(newOfficeName.lastIndexOf("."));// 加上后缀名

						RefItemVO ref = new RefItemVO();// 新建一个refitemvo
						ref.setId(newOfficeName);// 文件名
						ref.setCode(ref.getCode());// code
						ref.setName(chineseName);// 显示名称
						card.setValueAt("DEAL_CONTENT", ref);// 设置“合同正文”一项的值,这一项是个RefItemVO
					} else {
						MessageBox.show(card, "该合同没有范本!");
					}
				} catch (WLTRemoteException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}

	// 点击超链接
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent event) {
		BillListPanel source = event.getBillListPanel();
		BillVO vo = source.getSelectedBillVO();

		try {
			BillOfficeDialog officeDialog = new BillOfficeDialog(source);
			if (event.getItemkey().equalsIgnoreCase("DEAL_CONTENT")) {
				String en = vo.getStringValue("DEAL_CONTENT");
				String cn = new TBUtil().convertHexStringToStr(en);
				cn = cn.substring(3, cn.length() - 1) + en.substring(en.lastIndexOf("."));
				officeDialog.setTitle(cn);
				CommonHtmlOfficeConfig.OfficeConfig(vo.getStringValue("DEAL_CONTENT"), vo, officeDialog);
				officeDialog.setVisible(true);
			} else if (event.getItemkey().equalsIgnoreCase("TESTFILE")) {
				String en = vo.getStringValue("TESTFILE");
				String cn = new TBUtil().convertHexStringToStr(en);
				cn = cn.substring(3, cn.length() - 1) + en.substring(en.lastIndexOf("."));
				officeDialog.setTitle(cn);
				CommonHtmlOfficeConfig.OfficeConfig(vo.getStringValue("TESTFILE"), vo, officeDialog);
				officeDialog.setVisible(true);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private BillListPanel getbilListPanel() {
		return docList;
	}

	public void onBillListSelectChanged(BillListSelectionEvent event) {
		BillVO vo = event.getCurrSelectedVO();
		if (vo != null) {
			if (!vo.containsKey("wfprinstanceid")) {// 字段中不包含工作流字段
				return; //
			}
			String wf_id = vo.getStringValue("wfprinstanceid");
			if (wf_id == null || wf_id.trim().isEmpty()) {// 没有对应的工作流,修改和删除按钮可用
				edit.setEnabled(true);
				delete.setEnabled(true);
			} else {
				edit.setEnabled(false);
				delete.setEnabled(false);
			}
		}
	}

}
