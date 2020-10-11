package cn.com.pushworld.salary.ui.target.p011;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.AbstractAction;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.report.BillCellHtmlHrefEvent;
import cn.com.infostrategy.ui.report.BillCellHtmlHrefListener;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.infostrategy.ui.report.style2.DefaultStyleReportPanel_2;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;

public class PostTargetQueryCellReport extends AbstractWorkPanel implements ActionListener, BillCellHtmlHrefListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1689429974311206559L;
	private DefaultStyleReportPanel_2 dr = null;
	private BillQueryPanel bq = null;
	private String fileName = null;
	private WLTButton view = new WLTButton("��ϸ", UIUtil.getImage("office_089.gif"));
	private HashMap<String, Pub_Templet_1VO> templetMap = new HashMap<String, Pub_Templet_1VO>();
	private String billtemplet = "SAL_PERSON_CHECK_LIST_QUANTIFY_Q1";

	public void initialize() {
		dr = new DefaultStyleReportPanel_2(billtemplet, "");
		bq = dr.getBillQueryPanel();
		dr.getPanel_btn().add(view);
		view.addActionListener(this);
		dr.setReportExpName("Ա������ָ��");
		fileName = "Ա������ָ��";
		dr.getBillCellPanel().setEditable(false);
		bq.addBillQuickActionListener(this);
		dr.getBillCellPanel().addBillCellHtmlHrefListener(this);
		this.add(dr, BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == view) {
			BillCellPanel cellpanel = dr.getBillCellPanel();
			int row = cellpanel.getTable().getSelectedRow();
			if (row < 0) {
				MessageBox.show(this, "��ѡ��һ����¼.");
				return;
			}
			BillCellItemVO itemVO = cellpanel.getBillCellItemVOAt(row, cellpanel.getColumnCount() - 1);
			if (itemVO != null) {
				String targetid = (String) itemVO.getCustProperty("id");
				if (TBUtil.isEmpty(targetid)) {
					MessageBox.show(this, "��ѡ��һ����¼.");
					return;
				}
				String type = (String) itemVO.getCustProperty("type");
				BillCardPanel cardpanel = new BillCardPanel(billtemplet);
				BillCardDialog carddialog = new BillCardDialog(this, "���", cardpanel, WLTConstants.BILLDATAEDITSTATE_INIT);
				carddialog.getBillcardPanel().queryDataByCondition(" id = " + targetid);
				carddialog.setCardEditable(false);
				carddialog.setVisible(true);
			}

		} else {
			new SplashWindow(this, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					onQuery();
				}
			});
		}
	}

	private void onQuery() {
		try {
			HashMap hashMap = bq.getQuickQueryConditionAsMap();
			if (bq.checkValidate()) {
				SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
				dr.getBillCellPanel().loadBillCellData(ifc.getPersonPostTargetQueryCellVO(hashMap));
				dr.getBillCellPanel().setEditable(false);
				if (dr.getBillCellPanel().getRowCount() > 2) {
					dr.getBillCellPanel().setLockedCell(1, 1); // ������ͷ ���
					// 2013-10-10
				}

				// ���õ����ļ����� Gwang 2013-08-31
				dr.setReportExpName(fileName + bq.getRealValueAt("checkdate"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(this, "�����쳣�������Ա��ϵ!");
		}
	}

	public void onBillCellHtmlHrefClicked(BillCellHtmlHrefEvent event) {
		// TODO Auto-generated method stub

	}
}
