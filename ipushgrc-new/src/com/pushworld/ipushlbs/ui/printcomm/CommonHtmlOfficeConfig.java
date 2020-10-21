package com.pushworld.ipushlbs.ui.printcomm;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.mdata.BillOfficeDialog;
/**
 * ���ó����� office�ؼ�
 * @author yinliang
 * @since 2012.01.17
 *
 */
public class CommonHtmlOfficeConfig {
	public static void OfficeConfig(String filename,BillVO billvo,BillOfficeDialog officeDialog){
		
		officeDialog.addSomeActionListener(new PrintBillOfficeIntercept(billvo,officeDialog));
		officeDialog.setIfselfdesc(true);  //
		officeDialog.setIfshowprint_all(false);  //ȫ��
		officeDialog.setIfshowprint_fen(false); //�ִ�
		officeDialog.setIfshowsave(false); // ���水ť
		officeDialog.setIfshowprint_tao(false); // �״�
		officeDialog.setIfshowwater(false);  //ˮӡ
		officeDialog.setIfshowprint(false);  //��ӡ
		officeDialog.setIfshowshowcomment(false);
		officeDialog.setIfshowhidecomment(false);
		officeDialog.setIfshowedit(false); // �޶�
		officeDialog.setIfshowshowedit(false); // ��ʾ�޶�
		officeDialog.setIfshowhideedit(false); // �����޶�
		officeDialog.setIfshowacceptedit(false); // �����޶�

		officeDialog.initialize(filename, false, false, "officecompfile");
	}
}
