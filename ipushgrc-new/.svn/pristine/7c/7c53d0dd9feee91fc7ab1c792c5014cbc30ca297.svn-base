package com.pushworld.ipushgrc.ui.wfrisk.p010;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.IPushGRCServiceIfc;

/**
 * 流程清单维护【李春娟/2013-09-22】
 * 企业内控Bom版演示用
 * @author lcj
 * 
 */

public class WFAndRiskBillEditWKPanel extends AbstractWorkPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private IPushGRCServiceIfc service;//产品服务接口
	private BillListPanel billList_cmpfile; // 流程文件列表!
	private WLTButton btn_add;// 按钮【新建文件】
	private WLTButton btn_edit;// 按钮【编辑文件】
	private WLTButton btn_delete;// 按钮【删除文件】
	private boolean editable = true;//是否可编辑
	private boolean stateeditable;//文件状态是否可编辑
	private TBUtil tbutil;
	private String templetcode = "CMP_CMPFILE_CODE2_IC";

	public BillListPanel getBillList_cmpfile() {
		return billList_cmpfile;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public String getTempletcode() {
		return templetcode;
	}

	public void setTempletcode(String templetcode) {
		this.templetcode = templetcode;
	}

	public boolean isStateeditable() {
		return stateeditable;
	}

	/**
	 * 工作面板初始化方法
	 */
	public void initialize() {

		this.setLayout(new BorderLayout()); //
		try {
			service = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
		tbutil = new TBUtil();
		stateeditable = tbutil.getSysOptionBooleanValue("流程文件维护时文件状态是否可编辑", true);//默认在新增和编辑文件时文件状态是可编辑的
		if (this.editable) {//如果可编辑，则添加按钮
			billList_cmpfile = new BillListPanel(templetcode); //
			btn_add = new WLTButton("新建");
			btn_edit = new WLTButton("编辑文件");
			btn_delete = new WLTButton("删除");

			btn_add.addActionListener(this);
			btn_edit.addActionListener(this);
			btn_delete.addActionListener(this);
			billList_cmpfile.addBatchBillListButton(new WLTButton[] { btn_add, btn_delete, btn_edit });// 流程文件添加按钮
		} else {
			billList_cmpfile = new BillListPanel(templetcode); //
			billList_cmpfile.setDataFilterCustCondition("versionno is not null");
		}
		billList_cmpfile.setItemsVisible(new String[] { "publishdate", "versionno" }, false);
		billList_cmpfile.setTitleLabelText("流程清单维护");
		billList_cmpfile.repaintBillListButton();// 必须重新绘制按钮
		this.add(billList_cmpfile); //	
	}

	/**
	 * 列表按钮的点击事件
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_add) {
			onAddFile();
		} else if (e.getSource() == btn_edit) {
			onEditFile();
		} else if (e.getSource() == btn_delete) {
			onDeleteFile();
		}
	}

	/**
	 * 按钮【新建】的逻辑,因正文有保存，卡片也有保存，客户经常混淆，认为正文里保存了，该流程文件记录也就保存了，就直接关闭窗口了，故将正文隐藏，在编辑文件时才显示
	 */
	private void onAddFile() {
		BillCardPanel cardPanel = new BillCardPanel(billList_cmpfile.templetVO); // 创建一个卡片面板
		cardPanel.insertRow(); // 卡片新增一行!
		cardPanel.setEditableByInsertInit(); // 设置卡片编辑状态为新增时的设置
		BillCardDialog dialog = new BillCardDialog(billList_cmpfile, billList_cmpfile.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); // 弹出卡片新增框
		cardPanel.setEditable("filestate", stateeditable);//设置文件状态是否可编辑,必须放在上句后面
		dialog.setVisible(true); // 显示卡片窗口
		if (dialog.getCloseType() == 1) { // 如是是点击确定返回!将则卡片中的数据赋给列表!
			int li_newrow = billList_cmpfile.newRow(false); //
			billList_cmpfile.setBillVOAt(li_newrow, dialog.getBillVO(), false);
			billList_cmpfile.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); // 设置列表该行的数据为初始化状态.
			billList_cmpfile.setSelectedRow(li_newrow); //
			billList_cmpfile.refreshCurrSelectedRow();//需要刷新一下，否则文件状态会变为黑色【李春娟/2012-03-19】
		}
	}

	/**
	 * 按钮【编辑文件】的逻辑
	 */
	private void onEditFile() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		billList_cmpfile.refreshCurrSelectedRow();//刷新一下，防止发生被别人修改了文件状态而不同步的问题
		billVO = billList_cmpfile.getSelectedBillVO();
		String cmpfileid = billVO.getStringValue("id");
		String filestate = billVO.getStringValue("filestate");
		String view_filestate = billVO.getStringViewValue("filestate");
		if ((("2".equals(filestate) || "4".equals(filestate)) && !this.hasReject(billVO)) || "5".equals(filestate)) {//1- 编辑中, 2- 发布申请中, 3- 有效, 4- 废止申请中, 5- 失效
			if (MessageBox.showConfirmDialog(this, "该文件的状态为[" + view_filestate + "],不能进行编辑,是否需要查看？") != JOptionPane.YES_OPTION) {
				return;
			} else {
				onLookFile();
				return;
			}
		} else if ("3".equals(filestate)) {
			int li_result = MessageBox.showOptionDialog(this, "该流程文件已经[发布], 如果选择[编辑]状态将变为[编辑中],\r\n则需要重新发布!你可以有如下操作:", "提示", new String[] { "查看", "编辑", "取消" }, 450, 150); //
			if (li_result == 0) { //
				onLookFile();
				return;
			} else if (li_result == 1) { //
				try {
					UIUtil.executeUpdateByDS(null, "update cmp_cmpfile set filestate='1' where id=" + cmpfileid);
					billList_cmpfile.refreshCurrSelectedRow();
				} catch (Exception e) {
					MessageBox.showException(this, e);
				}
			} else {
				return;
			}
		}
		billVO = billList_cmpfile.getSelectedBillVO();//必须要设置一下
		BillCardPanel cardPanel = new BillCardPanel(billList_cmpfile.templetVO); // 创建一个卡片面板
		cardPanel.setBillVO(billVO); //
		BillCardDialog dialog = new BillCardDialog(billList_cmpfile, billList_cmpfile.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE); // 弹出卡片编辑框
		cardPanel.setEditable("filestate", stateeditable);//设置文件状态是否可编辑,必须放在上句后面
		dialog.setVisible(true); // 显示卡片窗口
		if (dialog.getCloseType() == 1) { // 如是是点击确定返回!将则卡片中的数据赋给列表!
			BillVO return_BillVO = dialog.getBillVO();
			return_BillVO.setObject("blcorpname", new StringItemVO(return_BillVO.getStringViewValue("blcorpid")));
			return_BillVO.setObject("bsactname", new StringItemVO(return_BillVO.getStringViewValue("bsactid")));
			return_BillVO.setObject("ictypename", new StringItemVO(return_BillVO.getStringViewValue("ictypeid")));

			billList_cmpfile.setBillVOAt(billList_cmpfile.getSelectedRow(), return_BillVO, false); //
			billList_cmpfile.setRowStatusAs(billList_cmpfile.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT);
			String return_blcorpid = return_BillVO.getStringValue("blcorpid");
			String return_blcorpname = return_BillVO.getStringViewValue("blcorpid");

			//这里需要判断一下如果机构名称变了，也需要更新一下！否则在风险地图里显示个数有问题！【李春娟/2012-03-14】
			try {
				if (!billList_cmpfile.getTempletItemVO("bsactid").isCardisshowable() && billList_cmpfile.getTempletItemVO("ictypeid").isCardisshowable()) {//如果模板中不显示业务活动但显示内控体系，则将风险点中原记录业务活动字段记录内控体系，这样可在风险表少加一个字段，内控产品中用到【李春娟/2012-07-17】
					String return_ictypeid = return_BillVO.getStringValue("ictypeid");
					String return_ictypename = return_BillVO.getStringViewValue("ictypeid");
					UIUtil.executeUpdateByDS(null, "update cmp_risk set blcorpid =" + return_blcorpid + ",blcorpname='" + return_blcorpname + "',bsactid=" + return_ictypeid + ",bsactname='" + return_ictypename + "' where cmpfile_id = " + cmpfileid);
				} else {
					String return_bsactid = return_BillVO.getStringValue("bsactid");
					String return_bsactname = return_BillVO.getStringViewValue("bsactid");
					//更改文件的所属机构和业务活动要同步更新风险点上的所属机构和业务活动
					UIUtil.executeUpdateByDS(null, "update cmp_risk set blcorpid =" + return_blcorpid + ",blcorpname='" + return_blcorpname + "',bsactid=" + return_bsactid + ",bsactname='" + return_bsactname + "' where cmpfile_id = " + cmpfileid);
				}
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
			billList_cmpfile.refreshCurrSelectedRow();//需要刷新一下，否则文件状态会变为黑色【李春娟/2012-03-19】
		}
	}

	/**
	 * 按钮【删除文件】的逻辑，只是对没有版本号的流程文件进行删除，并级联删除流程及相关信息
	 */
	private void onDeleteFile() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		billList_cmpfile.refreshCurrSelectedRow();//重新查一下，因为页面的数据可能是十分钟或很久以前的数据。
		billVO = billList_cmpfile.getSelectedBillVO();
		if (billVO.getStringValue("versionno") != null && !"".equals(billVO.getStringValue("versionno"))) {
			MessageBox.show(this, "该文件已有版本不能删除!"); //
			return;
		}
		if ("2".equals(billVO.getStringValue("filestate"))) {//没有版本，但是已在[发布申请中]的文件也不允许删除
			MessageBox.show(this, "该文件的状态为[" + billVO.getStringViewValue("filestate") + "],不能删除!"); //
			return;
		}
		if (MessageBox.showConfirmDialog(this, "此操作会删除所有相关的流程,是否删除?") != JOptionPane.YES_OPTION) {
			return; //
		}
		String cmpfileid = billVO.getStringValue("id");
		// 删除流程文件要记录日志,后台处理，先查出所有流程id，不用子查询
		try {
			service.deleteCmpFileById(cmpfileid);
			billList_cmpfile.removeSelectedRow();// 页面删除
		} catch (Exception e1) {
			MessageBox.showException(this, e1);
		}
	}

	/**
	 * 按钮【浏览】是直接由WLTButton创建的，组显示和隐藏等逻辑是在卡片初始化时处理的，故不用这段逻辑，但编辑文件，如果该文件的状态是不可编辑的，则需要调用这段逻辑来浏览文件
	 */
	private void onLookFile() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		BillCardPanel cardPanel = new BillCardPanel(billList_cmpfile.templetVO); // 创建一个卡片面板
		cardPanel.setBillVO(billVO);
		cardPanel.setVisiable("btn_temp", false);//浏览时隐藏按钮【下载模板】
		cardPanel.setEditable(false);
		BillCardDialog dialog = new BillCardDialog(billList_cmpfile, billList_cmpfile.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_INIT); // 弹出卡片浏览框
		dialog.setVisible(true); // 显示卡片窗口
	}

	private boolean hasReject(BillVO _billvo) {
		String processid = _billvo.getStringValue("WFPRINSTANCEID");
		try {
			if (processid == null || "".equals(processid.trim())) {//由于文件发布和废止如果走流程，第一步点提交了，但没选下一环节的接收人，就直接取消提交了，这时文件状态已变成“申请发布中”或“申请废止中”，编辑时再重置回来
				if ("2".equals(_billvo.getStringValue("filestate"))) {
					UIUtil.executeUpdateByDS(null, "update cmp_cmpfile set filestate='1' where id=" + _billvo.getStringValue("id"));
				} else if ("4".equals(_billvo.getStringValue("filestate"))) {
					UIUtil.executeUpdateByDS(null, "update cmp_cmpfile set filestate='3' where id=" + _billvo.getStringValue("id"));
				}
				billList_cmpfile.refreshCurrSelectedRow();
				return true;
			}

			String count = UIUtil.getStringValueByDS(null, "select count(id) from pub_wf_dealpool where rootinstanceid='" + processid + "' and submitisapprove='N'");//是否有退回
			if ("0".equals(count)) {
				return false;
			}
			return true;
		} catch (Exception e) {
			MessageBox.showException(this, e);
			return false;
		}
	}

}
