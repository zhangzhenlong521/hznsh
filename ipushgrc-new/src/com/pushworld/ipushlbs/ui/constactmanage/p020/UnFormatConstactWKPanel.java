package com.pushworld.ipushlbs.ui.constactmanage.p020;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillOfficeDialog;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;

import com.pushworld.ipushgrc.ui.IPushGRCServiceIfc;
import com.pushworld.ipushlbs.ui.printcomm.CommonHtmlOfficeConfig;

public class UnFormatConstactWKPanel extends AbstractWorkPanel implements BillListHtmlHrefListener, ActionListener {
	private BillListPanel listpanel;
	private WLTButton btn_delete, btn_change; // 删除、转为格式
	private FrameWorkMetaDataServiceIfc serives = null;

	@Override
	public void initialize() {
		listpanel = new BillListPanel("LBS_UNSTDFILE_CODE1");
		listpanel.addBillListHtmlHrefListener(this);
		// 点击删除按钮事件
		btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
		btn_delete.addActionListener(this);
		// 点击转为格式合同
		btn_change = new WLTButton("转为格式合同");
		btn_change.addActionListener(this);

		listpanel.addBatchBillListButton(new WLTButton[] { WLTButton.createButtonByType(WLTButton.LIST_POPINSERT), WLTButton.createButtonByType(WLTButton.LIST_POPEDIT), btn_delete, btn_change });
		listpanel.repaintBillListButton();
		this.add(listpanel);
	}

	private FrameWorkMetaDataServiceIfc getserives() {
		if (serives == null) {
			try {
				serives = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return serives;
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent event) {
		String str_filetest = event.getBillListPanel().getSelectedBillVO().getStringValue("testfile");
		if (str_filetest == null) {
			MessageBox.show(listpanel, "该合同未填写word文本!");
		} else {
			BillListPanel panelh = event.getBillListPanel();
			BillVO billvo = panelh.getSelectedBillVO();
			// 弹出office窗口
			BillOfficeDialog dialog = new BillOfficeDialog(event.getBillListPanel());
			dialog.setTitle(new TBUtil().convertHexStringToStr(str_filetest.substring(str_filetest.lastIndexOf("_") + 1, str_filetest.lastIndexOf("."))));
			// 设置弹出的窗口显示
			CommonHtmlOfficeConfig.OfficeConfig(str_filetest, billvo, dialog);
			dialog.setVisible(true);
		}
	}

	// 删除数据
	private void onDelete() {
		BillVO billvo = listpanel.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if (MessageBox.confirm(listpanel, "您确定删除该记录吗?")) { // 确定要删除
			this.deleteFile(listpanel.getSelectedBillVO(), null, true);// 删除附件及文件
			listpanel.doDelete(true); // 删除当前行数据
		}
	}

	// 删除文件.可以通过billvo删除，也可以直接通过文件名删除
	private void deleteFile(BillVO vo, String path, boolean isOfficeFile) {
		try {
			serives = getserives();
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_delete) // 删除时同时删除相关文件
			onDelete();
		else if (e.getSource() == btn_change) // 废除
			onChange();
	}

	/**
	 * 转为格式合同
	 */
	private void onChange() {
		BillVO billvo = listpanel.getSelectedBillVO();
		StringBuilder sql = new StringBuilder();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		try {
			if (MessageBox.showConfirmDialog(this, "确定要将此非格式合同转为格式合同吗?") == 0) {
				sql.append("select Code from LBS_STDFILE where Code = '" + billvo.getStringValue("Code").toString() + "'");
				HashVO[] hashVO = UIUtil.getHashVoArrayByDS(null, sql.toString());
				if (hashVO == null || hashVO.length == 0) {
					UIUtil.executeUpdateByDS(null, getInsertSql(billvo));
					MessageBox.show(this, "转格式合同成功,新转格式合同为未发布状态,如需使用请发布!");
				} else {
					MessageBox.show(this, "已经存在，请不要重复转化");

				}

			} else {
				return;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getInsertSql(BillVO billvo) {
		String seq_number1;
		// 使用InsertSQLBuilder方法插入
		InsertSQLBuilder sqlBuilder = new InsertSQLBuilder();
		try {

			HashMap<String, String> oldfileMap = new HashMap<String, String>();
			String officeFileName = billvo.getStringValue("testfile"); // office文件
			if (officeFileName != null && !officeFileName.equals("")) {
				oldfileMap.put("office", officeFileName);
			}
			String annex = billvo.getStringValue("adjunct"); // 文本附件
			if (annex == null || annex.equals("null") || annex.equals("")) // 如果为空，则不进行复制操作了
				annex = "";
			// 如果不为空，复制附件
			else {
				oldfileMap.put("adjunt", annex);
			}
			// 拷贝
			IPushGRCServiceIfc server = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
			HashMap newFiles = server.bargainCopyFile(oldfileMap);
			String filename = (String) newFiles.get("office"); // office文件的复制版本
			annex = (String) newFiles.get("adjunt"); // 附件的复制版本

			seq_number1 = UIUtil.getSequenceNextValByDS(null, "s_lbs_stdfile"); // 表序列
			sqlBuilder.setTableName("lbs_stdfile"); // 表名
			sqlBuilder.putFieldValue("id", seq_number1); // id
			sqlBuilder.putFieldValue("Code", billvo.getStringValue("Code"));// 编码
			sqlBuilder.putFieldValue("Name", billvo.getStringValue("Name"));// 名称
			sqlBuilder.putFieldValue("Type", billvo.getStringValue("Type"));// 业务类型
			sqlBuilder.putFieldValue("busiid", billvo.getStringValue("busiid"));// 合同属性
			sqlBuilder.putFieldValue("useway", billvo.getStringValue("useway"));// 合同属性
			sqlBuilder.putFieldValue("Reldate", UIUtil.getCurrDate());// 创建日期
			sqlBuilder.putFieldValue("Createorg", billvo.getStringValue("Createorg"));// 创建机构
			sqlBuilder.putFieldValue("adjunct", annex);// 附件
			sqlBuilder.putFieldValue("editon", "");// 版本
			sqlBuilder.putFieldValue("isforeign", billvo.getStringValue("isforeign"));// 是否外文
			sqlBuilder.putFieldValue("filestate", "未发布");// 是否发布
			sqlBuilder.putFieldValue("creater", billvo.getStringValue("creater"));// 创建人员
			sqlBuilder.putFieldValue("phone", billvo.getStringValue("phone"));// 联系电话
			sqlBuilder.putFieldValue("faxes", billvo.getStringValue("faxes"));// 传真
			sqlBuilder.putFieldValue("testfile", filename);// 合同文本
			sqlBuilder.putFieldValue("filedis", billvo.getStringValue("filedis"));// 合同描述
			sqlBuilder.putFieldValue("waigui", billvo.getStringValue("waigui"));// 外规
			sqlBuilder.putFieldValue("neigui", billvo.getStringValue("neigui"));// 内规
			sqlBuilder.putFieldValue("property", billvo.getStringValue("property"));// 合同性质
			sqlBuilder.putFieldValue("effect", "");// 是否有效
			sqlBuilder.putFieldValue("secret", billvo.getStringValue("secret"));// 机密程度
			sqlBuilder.putFieldValue("fortruth", billvo.getStringValue("fortruth"));// 是否可信
			return sqlBuilder.getSQL();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

}
