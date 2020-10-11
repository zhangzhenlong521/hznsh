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
 * ������λά��,����ǻ�����,�ұ��Ǹ�λ��!!!!
 * @author xch
 *
 */
public class WFPanel_CorpDeptPost extends AbstractWorkPanel implements BillTreeSelectListener, ActionListener {

	private BillTreePanel billTreePanel_corpdept = null; //������
	private BillListPanel billListPanel_post = null; //������λ��ϵ��

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout()); //
		billTreePanel_corpdept = new BillTreePanel("pub_corp_dept_CODE1"); //������
		billListPanel_post = new BillListPanel("pub_corpdept_post_CODE1"); //������λ
		billListPanel_post.getBillListBtn("corpdept_post_import").addActionListener(this); //

		billTreePanel_corpdept.queryDataByCondition(null); //
		billTreePanel_corpdept.addBillTreeSelectListener(this); //

		WLTSplitPane splitPanel = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTreePanel_corpdept, billListPanel_post);
		splitPanel.setDividerLocation(300); //
		this.add(splitPanel); //
	}

	/**
	 * ѡ����ʱˢ���ұߵı�
	 */
	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		String str_corpid = _event.getCurrSelectedVO().getStringValue("id"); //��������
		billListPanel_post.queryDataByCondition("corpdept_id='" + str_corpid + "'", "post_code asc"); //
	}

	/**
	 * ����.
	 */
	public void actionPerformed(ActionEvent e) {
		try {
			BillVO billVO = billTreePanel_corpdept.getSelectedVO(); //
			if (billVO == null) {
				MessageBox.show(this, "��ѡ��һ������!"); //
				return; //
			}

			String str_corpid = billVO.getStringValue("id"); //��������..
			HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, "select corpdept_id,post_id from pub_corpdept_post where corpdept_id='" + str_corpid + "'"); //  
			ArrayList al_corp = new ArrayList(); //
			for (int i = 0; i < hvs.length; i++) {
				al_corp.add(hvs[i].getStringValue("post_id")); //
			}
			BillFormatDialog formatDialog = new BillFormatDialog(this, "ѡ���λ", "��λ�б�"); //
			formatDialog.setVisible(true); //
			if (formatDialog.getCloseType() == 1) { //�����ȷ���ر�
				BillListPanel billListPanel = formatDialog.getBillformatPanel().getBillListPanel(); //
				BillVO[] billVOs = billListPanel.getSelectedBillVOs(); //
				ArrayList al_sqls = new ArrayList(); //
				int li_successcount = 0;
				int li_failcount = 0;
				for (int i = 0; i < billVOs.length; i++) { //�������и�λ
					if (!al_corp.contains(billVOs[i].getStringValue("id"))) { //�������
						String str_newid = UIUtil.getSequenceNextValByDS(null, "s_pub_corpdept_post"); //
						al_sqls.add("insert into pub_corpdept_post (id,corpdept_id,post_id,post_code,post_name) values ('" + str_newid + "','" + str_corpid + "','" + billVOs[i].getStringValue("id") + "','" + billVOs[i].getStringValue("code") + "','" + billVOs[i].getStringValue("name") + "')"); //
						li_successcount++;
					} else {
						li_failcount++; //
					}
				}

				UIUtil.executeBatchByDS(null, al_sqls); //
				billListPanel_post.queryDataByCondition("corpdept_id='" + str_corpid + "'", "post_code asc"); //
				MessageBox.show(this, "���뵼��[" + (li_successcount + li_failcount) + "]����λ,�ɹ�����[" + li_successcount + "]��,[" + li_failcount + "]�����в�����!!"); //
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}
}
