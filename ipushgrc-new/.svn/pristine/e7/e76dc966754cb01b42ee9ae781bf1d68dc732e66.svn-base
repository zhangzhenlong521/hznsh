package com.pushworld.ipushgrc.ui.icheck.p050;

import java.awt.Color;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_Button;

import com.pushworld.ipushgrc.ui.icheck.p040.ICheckUIUtil;

/**
 * 单机版问题录入-主键生成机制不同
 * 与 类 com.pushworld.ipushgrc.ui.icheck.p040.CheckProblemDialog 类似
 * 如需修改，同时修改
 * 【李春娟/2016-09-05】
 * @author lcj
 *
 */
public class CheckProblemAloneDialog implements WLTActionListener {

	private BillCardPanel card_panel;
	String improveTempCode = "CK_PROBLEM_INFO_LCJ_E01";
	BillVO billvo = null;

	public void actionPerformed(WLTActionEvent event) throws Exception {
		card_panel = (BillCardPanel) event.getBillPanelFrom();
		if (WLTConstants.BILLDATAEDITSTATE_INIT.equalsIgnoreCase(card_panel.getEditState())) {
			MessageBox.show(card_panel, "浏览界面，不可录入！");//【李春娟/2016-08-24】
			return;
		}
		BillVO cardVO = card_panel.getBillVO();
//		if (cardVO.getStringValue("control") == null || "".equals(cardVO.getStringValue("control"))) {
//			MessageBox.show(card_panel, "请您先填写底稿信息！");
//			return;
//		}//客户提出不想录入底稿信息【2016/10/17 zzl】
		if (cardVO.getStringValue("result") == null || "".equals(cardVO.getStringValue("result"))) {
			MessageBox.show(card_panel, "请选择" + card_panel.getTempletItemVO("result").getItemname() + "!");
			return;
		}

		BillCardDialog dialog = new BillCardDialog(card_panel, improveTempCode, WLTConstants.BILLDATAEDITSTATE_INSERT);
		dialog.setTitle("问题录入");
		dialog.setSize(860, 700);
		dialog.locationToCenterPosition();

		billvo = card_panel.getBillVO();
		dialog.billcardPanel.setValueAt("planid", new StringItemVO(billvo.getStringValue("planid"))); //计划
		dialog.billcardPanel.setValueAt("schemeid", new StringItemVO(billvo.getStringValue("schemeid"))); //方案
		dialog.billcardPanel.setValueAt("deptid", new StringItemVO(billvo.getStringValue("deptid"))); //被检查单位
		dialog.billcardPanel.setValueAt("implid", new StringItemVO(billvo.getStringValue("implid"))); //实施主表【李春娟/2016-09-26】
		dialog.billcardPanel.setValueAt("parentid", new StringItemVO(billvo.getStringValue("id"))); //检查实施子表【李春娟/2016-09-26】
		dialog.billcardPanel.setValueAt("firstid", new StringItemVO(billvo.getStringValue("firstid"))); //一级目录
		dialog.billcardPanel.setValueAt("secondid", new StringItemVO(billvo.getStringValue("secondid"))); //二级目录
		dialog.billcardPanel.setValueAt("thirdid", new StringItemVO(billvo.getStringValue("thirdid"))); // 三级目录
		dialog.billcardPanel.setValueAt("firstname", new StringItemVO(billvo.getStringValue("firstname"))); //一级目录
		dialog.billcardPanel.setValueAt("secondname", new StringItemVO(billvo.getStringValue("secondname"))); //二级目录
		dialog.billcardPanel.setValueAt("thirdname", new StringItemVO(billvo.getStringValue("thirdname"))); // 三级目录
		dialog.billcardPanel.setValueAt("tag_law", new StringItemVO(billvo.getStringValue("tag_law"))); // 相关法规
		dialog.billcardPanel.setValueAt("tag_rule", new StringItemVO(billvo.getStringValue("tag_rule"))); //相关制度
		dialog.billcardPanel.setValueAt("tag_risk", new StringItemVO(billvo.getStringValue("tag_risk"))); //相关风险点
		dialog.billcardPanel.setValueAt("tag_flow", new StringItemVO(billvo.getStringValue("tag_flow"))); //相关流程
		dialog.billcardPanel.setValueAt("tag_ctrldict", new StringItemVO(billvo.getStringValue("tag_ctrldict")));//相关控制点
		dialog.billcardPanel.setValueAt("checkMethod", new StringItemVO(billvo.getStringValue("checkMethod"))); //检查方法
		dialog.billcardPanel.setValueAt("importance", new StringItemVO(billvo.getStringValue("important")));
		dialog.billcardPanel.setValueAt("checkMode", new StringItemVO(billvo.getStringValue("checkMode"))); //检查方式:现场检查、非现场检查【李春娟/2016-10-08】
		dialog.billcardPanel.setValueAt("id", new StringItemVO(ICheckUIUtil.getSequenceNextVal()));//单机版id自动生成机制【李春娟/2016-09-05】
		dialog.billcardPanel.setValueAt("dictid", new RefItemVO(billvo.getStringValue("dictid"), "", billvo.getStringValue("dictname"))); //问题词条【李春娟/2016-09-28】
		dialog.billcardPanel.setValueAt("dictname", new StringItemVO(billvo.getStringValue("dictname"))); //问题词条描述【李春娟/2016-09-28】
		dialog.billcardPanel.setEditableByInsertInit();
		dialog.billcardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT); //
		dialog.setVisible(true);

		if (dialog.getCloseType() == 1) {
			try {
				UIUtil.executeUpdateByDS(null, card_panel.getUpdateSQL());
				card_panel.updateUI();
				setBtnText();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 查询问题数量
	private void setBtnText() {
		String schemeid = billvo.getStringValue("schemeid");
		String deptid = billvo.getStringValue("deptid");
		String parentid = billvo.getStringValue("id");

		try {
			String count = UIUtil.getStringValueByDS(null, "select count(id)  from  ck_problem_info where schemeid = '" + schemeid + "' and deptid = '" + deptid + "' and parentid = '" + parentid + "'");
			if (null != count && !"0".equals(count)) {
				((CardCPanel_Button) card_panel.getCompentByKey("btn_insert")).getButtontn().setForeground(Color.RED);
				((CardCPanel_Button) card_panel.getCompentByKey("btn_insert")).getButtontn().setText("问题录入(" + count + ")");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
