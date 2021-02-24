package cn.com.infostrategy.ui.sysapp.userrole;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.mdata.formatcomp.BillFormatDialog;

/**
 * 机构岗位维护,左边是机构树,右边是岗位表!!!!
 * @author xch
 *
 */
public class WFPanel_CorpDeptPost extends AbstractWorkPanel implements BillTreeSelectListener, ActionListener {

	private BillTreePanel billTreePanel_corpdept = null; //机构树
	private BillListPanel billListPanel_post = null; //机构岗位关系表

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout()); //
		billTreePanel_corpdept = new BillTreePanel("pub_corp_dept_CODE1"); //机构树
		billListPanel_post = new BillListPanel("pub_corpdept_post_CODE1"); //机构岗位
		billListPanel_post.getBillListBtn("corpdept_post_import").addActionListener(this); //

		billTreePanel_corpdept.queryDataByCondition(null); //
		billTreePanel_corpdept.addBillTreeSelectListener(this); //

		WLTSplitPane splitPanel = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTreePanel_corpdept, billListPanel_post);
		splitPanel.setDividerLocation(300); //
		this.add(splitPanel); //
	}

	/**
	 * 选择树时刷新右边的表
	 */
	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		String str_corpid = _event.getCurrSelectedVO().getStringValue("id"); //机构主键
		billListPanel_post.queryDataByCondition("corpdept_id='" + str_corpid + "'", "post_code asc"); //
	}

	/**
	 * 导入.
	 */
	public void actionPerformed(ActionEvent e) {
		try {
			BillVO billVO = billTreePanel_corpdept.getSelectedVO(); //
			if (billVO == null) {
				MessageBox.show(this, "请选择一个机构!"); //
				return; //
			}

			String str_corpid = billVO.getStringValue("id"); //机构主键..
			HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, "select corpdept_id,post_id from pub_corpdept_post where corpdept_id='" + str_corpid + "'"); //  
			ArrayList al_corp = new ArrayList(); //
			for (int i = 0; i < hvs.length; i++) {
				al_corp.add(hvs[i].getStringValue("post_id")); //
			}
			BillFormatDialog formatDialog = new BillFormatDialog(this, "选择岗位", "岗位列表"); //
			formatDialog.setVisible(true); //
			if (formatDialog.getCloseType() == 1) { //如果是确定关闭
				BillListPanel billListPanel = formatDialog.getBillformatPanel().getBillListPanel(); //
				BillVO[] billVOs = billListPanel.getSelectedBillVOs(); //
				ArrayList al_sqls = new ArrayList(); //
				int li_successcount = 0;
				int li_failcount = 0;
				for (int i = 0; i < billVOs.length; i++) { //遍历所有岗位
					if (!al_corp.contains(billVOs[i].getStringValue("id"))) { //如果已有
						String str_newid = UIUtil.getSequenceNextValByDS(null, "s_pub_corpdept_post"); //
						al_sqls.add("insert into pub_corpdept_post (id,corpdept_id,post_id,post_code,post_name) values ('" + str_newid + "','" + str_corpid + "','" + billVOs[i].getStringValue("id") + "','" + billVOs[i].getStringValue("code") + "','" + billVOs[i].getStringValue("name") + "')"); //
						li_successcount++;
					} else {
						li_failcount++; //
					}
				}

				UIUtil.executeBatchByDS(null, al_sqls); //
				billListPanel_post.queryDataByCondition("corpdept_id='" + str_corpid + "'", "post_code asc"); //
				MessageBox.show(this, "共想导入[" + (li_successcount + li_failcount) + "]个岗位,成功导入[" + li_successcount + "]条,[" + li_failcount + "]条已有不导入!!"); //
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}
}
