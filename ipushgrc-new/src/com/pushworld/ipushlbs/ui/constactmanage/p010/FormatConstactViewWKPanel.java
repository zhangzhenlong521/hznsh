package com.pushworld.ipushlbs.ui.constactmanage.p010;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillOfficeDialog;

import com.pushworld.ipushlbs.ui.printcomm.CommonHtmlOfficeConfig;
import com.pushworld.ipushlbs.ui.printcomm.PrintBillOfficeIntercept;

/**
 * 格式合同查询
 * 
 * @author yinliang
 * @since 2011.12.21
 */
public class FormatConstactViewWKPanel extends AbstractWorkPanel implements BillListHtmlHrefListener, ActionListener {
	private BillListPanel billlist;
	private WLTButton btn_print;

	@Override
	public void initialize() {
		billlist = new BillListPanel("V_LBS_STAND_CHECK_FILE_CODE1");
		billlist.addBillListHtmlHrefListener(this); // 加入监听
		btn_print = new WLTButton("打印");
		btn_print.addActionListener(this);
		btn_print.setVisible(true);
		billlist.addBatchBillListButton(new WLTButton[] { WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD), btn_print });
		billlist.repaintBillListButton();
		this.add(billlist);
	}

	// 超链接
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent event) {
		BillListPanel source = event.getBillListPanel();
		BillVO vo = source.getSelectedBillVO();
		String filename = vo.getStringValue("TESTFILE");
		try {
			// 弹出office窗口
			BillOfficeDialog dialog = new BillOfficeDialog(event.getBillListPanel());
			dialog.setTitle(new TBUtil().convertHexStringToStr(filename.substring(filename.lastIndexOf("_") + 1, filename.lastIndexOf("."))));
			// 设置弹出的窗口显示
			CommonHtmlOfficeConfig.OfficeConfig(filename, vo, dialog);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_print)
			onPrint(); // 打印操作
	}

	/**
	 * 弹出打印操作
	 */
	private void onPrint() {
		BillVO billvo = billlist.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}

		// 创建中级的officedialog对象,使用下边的构造方法，然后为其set名字，否则有问题
		BillOfficeDialog officeDialog = new BillOfficeDialog(billlist);

		String filename = billvo.getStringValue("TESTFILE");
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
		officeDialog.initialize(billvo.getStringValue("TESTFILE"), false, false, "officecompfile");

		officeDialog.setVisible(true);
	}

}
