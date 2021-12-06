package com.pushworld.ipushgrc.ui.wfrisk.p010;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.wfrisk.WFRiskUIUtil;

/**
 * 编辑流程中的环节相关的风险点
 * @author lcj
 *
 */
public class LookRiskDialog extends BillDialog implements ActionListener {
	private String cmpfileid;
	private String cmpfilename;
	private String cmpfilecode;
	private String processid;
	private String processcode;
	private String processname;
	private String activityid;
	private String activitycode;
	private String activityname;
	private String wftype;//新增风险点类型，包括“流程”和“环节”分别表示流程的相关风险点和环节的相关风险点【李春娟/2012-03-30】
	private boolean editable;
	private WLTButton btn_evaluate, btn_edit, btn_delete, btn_close;
	private BillListPanel billlist_risk;
	private String[][] cmpfilemsg;

	public LookRiskDialog(Container _parent, String _title, String _cmpfileid, String _cmpfilename, String _processid, String _processcode, String _processname, boolean _editable) {
		this(_parent, _title, _cmpfileid, _cmpfilename, _processid, _processcode, _processname, null, null, null, WFGraphEditItemPanel.TYPE_WF, _editable);
	}

	public LookRiskDialog(Container _parent, String _title, String _cmpfileid, String _cmpfilename, String _processid, String _processcode, String _processname, String _activityid, String _activitycode, String _activityname, String _wftype, Boolean _editable) {
		super(_parent, _title, 900, 650);
		if (_title == null) {
			this.setTitle("环节相关风险点");
		}
		this.setLayout(new BorderLayout()); //
		this.cmpfileid = _cmpfileid;
		this.cmpfilename = _cmpfilename;
		this.processid = _processid;
		this.processcode = _processcode;
		this.processname = _processname;
		this.activityid = _activityid;
		this.activitycode = _activitycode;
		this.activityname = _activityname;
		this.wftype = _wftype;
		this.editable = _editable;
		billlist_risk = new BillListPanel("CMP_RISK_CODE1");
		if (cmpfileid == null) {
			billlist_risk.getTempletItemVO("cmpfile_name").setCardisshowable(false);//如果不是流程文件里的关联，则不显示流程文件名称【李春娟/2012-05-11】
			billlist_risk.setItemVisible("cmpfile_name", false);//因为上面的billlist_risk 已经初始化完了，故用setListisshowable(false) 是不起作用的，只能这样设置。
		}
		if (WFGraphEditItemPanel.TYPE_WF.equals(this.wftype)) {
			billlist_risk.setItemVisible("wfactivity_name", false);
			billlist_risk.setDataFilterCustCondition("riskreftype='" + WFGraphEditItemPanel.TYPE_WF + "' and wfprocess_id=" + this.processid);
		} else {
			billlist_risk.setDataFilterCustCondition("wfactivity_id=" + this.activityid);
		}
		billlist_risk.QueryDataByCondition(null);
		if (editable) {
			btn_evaluate = new WLTButton("识别风险点");
			btn_edit = new WLTButton("修改");
			btn_delete = new WLTButton("删除");
			btn_evaluate.addActionListener(this);
			btn_edit.addActionListener(this);
			btn_delete.addActionListener(this);
			billlist_risk.addBatchBillListButton(new WLTButton[] { btn_evaluate, btn_edit, btn_delete });
		}
		WLTButton btn_show = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD); //
		billlist_risk.addBillListButton(btn_show);
		billlist_risk.repaintBillListButton();
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(billlist_risk, BorderLayout.CENTER);
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
	}

	public BillListPanel getBilllist_risk() {
		return billlist_risk;
	}

	/**
	 * 判断是否显示本窗口，如果没有相关信息，则提示是否新增，如果选择【是】，则弹出新增界面，如果选择【否】则不显示本窗口。【李春娟/2012-05-28】
	 * @return
	 */
	public boolean isShowDialog() {
		if (this.editable && billlist_risk.getRowCount() == 0) {
			if (MessageBox.confirm(this, "该" + this.wftype + "没有风险点,是否新增?")) {//如果可编辑并且列表中没有记录，则提示是否新增【李春娟/2012-03-13】
				onEvaluateRisk();
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_evaluate) {
			onEvaluateRisk();
		} else if (e.getSource() == btn_edit) {
			onEditRisk();
		} else if (e.getSource() == btn_delete) {
			onDeleteRisk();
		} else if (e.getSource() == btn_close) {
			onClose();
		}
	}

	private void onClose() {
		this.dispose();
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout()); //
		btn_close = new WLTButton("关闭");
		btn_close.addActionListener(this); //
		panel.add(btn_close); //
		return panel;
	}

	private void onEditRisk() {
		BillVO billVO = billlist_risk.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		billVO.setObject("cmpfile_name", new StringItemVO(this.cmpfilename));//重新设置一下，防止文件名修改了，但风险点里冗余字段没有修改
		BillCardPanel cardPanel = new BillCardPanel(billlist_risk.templetVO);
		cardPanel.setBillVO(billVO); //
		BillCardDialog dialog = new BillCardDialog(this, billlist_risk.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		if (!"".equals(billVO.getStringValue("finalost", "")) || !"".equals(billVO.getStringValue("cmplost", "")) || !"".equals(billVO.getStringValue("honorlost", ""))) {//【李春娟/2012-03-12】
			cardPanel.setEditable("serious", false);
		}
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {
			billlist_risk.setBillVOAt(billlist_risk.getSelectedRow(), dialog.getBillVO(), false); //
			billlist_risk.setRowStatusAs(billlist_risk.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT);
			try {//记录修改风险点日志
				new WFRiskUIUtil().insertEditRiskLog(billVO, dialog.getBillVO());
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}
	}

	private void onEvaluateRisk() {
		try {
			if (cmpfilemsg == null) {
				cmpfilemsg = UIUtil.getStringArrayByDS(null, "select blcorpid,blcorpname,bsactid,bsactname,cmpfilecode from cmp_cmpfile where id=" + this.cmpfileid);
			}
			BillCardPanel cardPanel = new BillCardPanel(billlist_risk.templetVO); //创建一个卡片面板
			cardPanel.setLoaderBillFormatPanel(billlist_risk.getLoaderBillFormatPanel()); //将列表的BillFormatPanel的句柄传给卡片
			cardPanel.insertRow(); //卡片新增一行!
			cardPanel.setEditableByInsertInit(); //设置卡片编辑状态为新增时的设置
			if (this.cmpfilecode == null) {
				if (cmpfilemsg != null && cmpfilemsg.length > 0) {
					if (cmpfilemsg[0][4] == null || "".equals(cmpfilemsg[0][4].trim())) {
						this.cmpfilecode = this.cmpfilename;
					} else {
						this.cmpfilecode = cmpfilemsg[0][4];
					}
				} else {
					cmpfilecode = "";//需要设置一下，否则风险点的编码会出现null【李春娟/2012-04-24】
				}
			}
			String count = UIUtil.getStringValueByDS(null, "select count(id) from cmp_risk where wfprocess_id=" + this.processid);//按流程里的风险点总数计算,否则如果是示范流程则计算出总数永远是1，因为示范流程没有流程文件为载体【李春娟/2012-04-24】
			cardPanel.setRealValueAt("riskcode", this.cmpfilecode + "_风险点" + (Integer.parseInt(count) + 1));//风险编码
			cardPanel.setRealValueAt("cmpfile_id", this.cmpfileid);
			cardPanel.setRealValueAt("cmpfile_name", this.cmpfilename);
			cardPanel.setRealValueAt("wfprocess_id", this.processid);
			cardPanel.setRealValueAt("wfprocess_code", this.processcode);
			cardPanel.setRealValueAt("wfprocess_name", this.processname);

			if (this.activityid == null) {//如果是流程的相关风险点，则只需要设置类型，不需要设置环节的属性【李春娟/2012-03-30】
				cardPanel.setRealValueAt("riskreftype", WFGraphEditItemPanel.TYPE_WF);
			} else {
				cardPanel.setRealValueAt("riskreftype", WFGraphEditItemPanel.TYPE_ACTIVITY);
				cardPanel.setRealValueAt("wfactivity_id", this.activityid);
				cardPanel.setRealValueAt("wfactivity_code", this.activitycode);
				cardPanel.setRealValueAt("wfactivity_name", this.activityname);
			}
			if (cmpfilemsg != null && cmpfilemsg.length > 0) {
				cardPanel.setValueAt("blcorpid", new RefItemVO(cmpfilemsg[0][0], "", cmpfilemsg[0][1]));
				cardPanel.setRealValueAt("blcorpname", cmpfilemsg[0][1]);
				cardPanel.setValueAt("bsactid", new RefItemVO(cmpfilemsg[0][2], "", cmpfilemsg[0][3]));
				cardPanel.setRealValueAt("bsactname", cmpfilemsg[0][3]);
			}
			BillCardDialog dialog = new BillCardDialog(this, billlist_risk.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); //弹出卡片新增框
			dialog.setVisible(true); //显示卡片窗口
			if (dialog.getCloseType() == 1) { //如是是点击确定返回!将则卡片中的数据赋给列表!
				int li_newrow = billlist_risk.newRow(false); //
				billlist_risk.setBillVOAt(li_newrow, dialog.getBillVO(), false);
				billlist_risk.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); //设置列表该行的数据为初始化状态.
				billlist_risk.setSelectedRow(li_newrow); //
				//记录风险点新增日志
				new WFRiskUIUtil().insertAddRiskLog(dialog.getBillVO());
			}
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	private void onDeleteRisk() {
		BillVO billvo = billlist_risk.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if (MessageBox.showConfirmDialog(this, "您确定要删除吗?") != JOptionPane.YES_OPTION) {
			return; //
		}
		try {
			billvo.setObject("cmpfile_name", new StringItemVO(this.cmpfilename));//重新设置一下，防止文件名修改了，但风险点里冗余字段没有修改
			new WFRiskUIUtil().insertDeleteRiskLog(billvo);
			//记录修改风险点日志
			UIUtil.executeUpdateByDS(null, "delete from cmp_risk where id=" + billvo.getStringValue("id"));
			billlist_risk.removeRow(billlist_risk.getSelectedRow()); //
			MessageBox.show(this, "删除成功!");
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

}
