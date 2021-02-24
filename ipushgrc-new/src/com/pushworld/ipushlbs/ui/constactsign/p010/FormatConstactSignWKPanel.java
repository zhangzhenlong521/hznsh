package com.pushworld.ipushlbs.ui.constactsign.p010;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillOfficeDialog;

import com.pushworld.ipushlbs.ui.printcomm.CommonHtmlOfficeConfig;
import com.pushworld.ipushlbs.ui.printcomm.PrintBillOfficeIntercept;

/**
 * 合同签约，打印合同
 * 
 * @author yinliang
 * @since 2011.12.15
 */
public class FormatConstactSignWKPanel extends AbstractWorkPanel implements ActionListener, BillListHtmlHrefListener {
	private BillListPanel billList;
	WLTButton btn_print;

	@Override
	public void initialize() {
		billList = new BillListPanel("FORMAT_DEAL_CHECK_CODE2");
		billList.addBillListHtmlHrefListener(this); // 超链接监听
		btn_print = new WLTButton("打印");
		btn_print.addActionListener(this);
		btn_print.setVisible(true);
		billList.addBatchBillListButton(new WLTButton[] { WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD), btn_print });
		billList.repaintBillListButton();
		this.add(billList);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_print)
			onPrint(); // 打印操作
	}

	/**
	 * 弹出打印操作
	 */
	private void onPrint() {
		BillVO billvo = billList.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		// 从后台取,防止开多个界面,此时即使已经打印过了,那么前台界面也没有刷新，还是能继续打印。
		// 是否已打印
		String sql_query = "select print from " + billvo.getSaveTableName() + " where id = " + billvo.getStringValue("id");
		try {
			String printstate = UIUtil.getStringValueByDS(null, sql_query);
			if (printstate.equals("是")) {
				MessageBox.show(billList, "已经打印，不能再进行打印！");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 创建中级的officedialog对象,使用下边的构造方法，然后为其set名字，否则有问题
		BillOfficeDialog officeDialog = new BillOfficeDialog(billList);

		String filename = billvo.getStringValue("DEAL_CONTENT");
		officeDialog.setTitle(new TBUtil().convertHexStringToStr(filename.substring(filename.lastIndexOf("_") + 1, filename.lastIndexOf("."))));
		// 因为BillOfficeIntercept是抽象类，所以这里建一个类去继承它，然后在这里new一下
		officeDialog.setIfselfdesc(true);
		officeDialog.setEnabled(true);
		officeDialog.addSomeActionListener(new PrintBillOfficeIntercept(billvo, officeDialog));
		//
		officeDialog.setIfshowprint_all(false); // 全打
		officeDialog.setIfshowprint_fen(false); // 分打
		officeDialog.setIfshowsave(false); // 保存按钮
		officeDialog.setIfshowprint_tao(false); // 套打
		officeDialog.setIfshowwater(false); // 水印
		officeDialog.setIfshowprint(true); // 打印
		officeDialog.setIfshowshowcomment(false);
		officeDialog.setIfshowhidecomment(false);
		officeDialog.setIfshowedit(false); // 修订
		officeDialog.setIfshowshowedit(false); // 显示修订
		officeDialog.setIfshowhideedit(false); // 隐藏修订
		officeDialog.setIfshowacceptedit(false); // 接收修订
		officeDialog.initialize(billvo.getStringValue("DEAL_CONTENT"), false, false, "officecompfile");
		//		officeDialog.callWebBrowseJavaScriptFunction();
		//		officeDialog.getBillOfficePanel().getWebBrowser().executeScript("try{TANGER_OCX_OBJ.PrintPreview();} catch(e){alert(e)} ;");
		officeDialog.setVisible(true);
		billList.refreshCurrSelectedRow();
	}

	// 超链接
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent event) {
		BillVO billvo = event.getBillListPanel().getSelectedBillVO();
		String filename = billvo.getStringValue("DEAL_CONTENT");
		// 弹出office窗口
		BillOfficeDialog dialog = new BillOfficeDialog(event.getBillListPanel());
		dialog.setTitle(new TBUtil().convertHexStringToStr(filename.substring(filename.lastIndexOf("_") + 1, filename.lastIndexOf("."))));
		// 设置弹出的窗口显示
		CommonHtmlOfficeConfig.OfficeConfig(filename, billvo, dialog);
		dialog.setVisible(true);
	}

}
