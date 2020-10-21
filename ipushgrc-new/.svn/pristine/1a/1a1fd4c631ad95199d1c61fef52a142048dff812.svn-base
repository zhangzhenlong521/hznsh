package com.pushworld.ipushlbs.ui.constactmanage.p020;

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
 * 非标准合同查询功能
 * 
 * @author yinliang
 * @since 2011.12.13
 */
public class UnFormatConstactViewWKPanel extends AbstractWorkPanel implements BillListHtmlHrefListener, ActionListener {
	private WLTButton btn_print;
	private BillListPanel billlist;

	/**
	 * 将增加、编辑、删除按钮设为不可用
	 */
	@Override
	public void initialize() {
		billlist = new BillListPanel("LBS_UNSTDFILE_CODE3"); // 非标准合同文件模板
		billlist.addBillListHtmlHrefListener(this); // 加入超链接监听
		btn_print = new WLTButton("打印");
		btn_print.addActionListener(this);
		btn_print.setVisible(true);
		billlist.addBillListButton(btn_print);
		billlist.repaintBillListButton();
		this.add(billlist);
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		String str_filetest = _event.getBillListPanel().getSelectedBillVO().getStringValue("testfile");
		if (str_filetest == null) {
			MessageBox.show(billlist, "该合同未填写word文本！");
		} else {
			BillOfficeDialog offdialog = new BillOfficeDialog(_event.getBillListPanel());
			offdialog.setTitle(new TBUtil().convertHexStringToStr(str_filetest.substring(str_filetest.lastIndexOf("_") + 1, str_filetest.lastIndexOf("."))));
			CommonHtmlOfficeConfig.OfficeConfig(str_filetest, billlist.getSelectedBillVO(), offdialog);
			offdialog.setVisible(true);
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
