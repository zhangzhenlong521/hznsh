package cn.com.pushworld.salary.ui.target.p050;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;
import cn.com.infostrategy.ui.report.style2.DefaultStyleReportPanel_2;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

public class ScoreDetailQueryWKPanel extends AbstractWorkPanel implements ActionListener, ChangeListener {
	private static final long serialVersionUID = 1L;
	private BillListPanel bl = null; //
	private WLTTabbedPane maintab = null;
	private DefaultStyleReportPanel_2 dr = null;
	private String type = null;
	
	private String exportFileName = "���ſ��˵÷�����_";

	public void initialize() {
		maintab = new WLTTabbedPane();
		bl = new BillListPanel("SAL_DEPT_CHECK_SCORE_CODE3");
		// bl.getQuickQueryPanel().addBillQuickActionListener(this);

		// ��������Ĭ��ֵΪ��ǰ�������� Gwang 2013-08-21
		QueryCPanel_UIRefPanel dateRef = (QueryCPanel_UIRefPanel) bl.getQuickQueryPanel().getCompentByKey("checkdate");
		String checkDate = new SalaryUIUtil().getCheckDate();
		dateRef.setValue(checkDate);

		//ûɶ��, ��ȥ��
//		maintab.addTab("�б�չʾ", bl);
//		maintab.addTab("EXCELչʾ", new JPanel(new BorderLayout()));
//		maintab.addChangeListener(this);
//		this.add(maintab, BorderLayout.CENTER);
		this.add(bl);
	}

	public void actionPerformed(ActionEvent e) {
		// if (e.getSource() == bl.getQuickQueryPanel()) {
		// type = "table";
		// } else {
		// type = "excel";
		// }
		
		new SplashWindow(this, "��ѯ��,����EXCELչʾ���ܷ�ҳ,���ݽ϶��������ĵȺ�...", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				onQuery(type);
			}
		}, false);
	}

	private void onQuery(String type) {
		try {
			SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
			String str_sql = bl.getQuickQueryPanel().getQuerySQL();
			if (bl.getOrderCondition() != null) {
				str_sql = str_sql + " order by " + bl.getOrderCondition();
			}
			// if ("table".equals(type)) {
			// if (bl.getQuickQueryPanel().checkValidate()) {
			// bl.clearTable();
			// BillVO[] vos = ifc.getScoreDetail(str_sql,
			// "SAL_DEPT_CHECK_SCORE_CODE3");
			// for (int i = 0; i < vos.length; i++) {
			// bl.insertRow(i, vos[i]);
			// bl.setValueAt(new
			// RowNumberItemVO(WLTConstants.BILLDATAEDITSTATE_INIT, i), i,
			// "_RECORD_ROW_NUMBER");
			// }
			// }
			// } else {
			if (dr.getBillQueryPanel().checkValidate()) {
				BillCellVO cellvo = ifc.getScoreDetailCellVO(str_sql, "SAL_DEPT_CHECK_SCORE_CODE3");
				dr.getBillCellPanel().loadBillCellData(cellvo);
				dr.getBillCellPanel().setEditable(false);
				
				//���õ����ļ����� Gwang 2013-08-31
				String checkDate = bl.getQuickQueryPanel().getCompentByKey("checkdate").getValue();				
				dr.setReportExpName(exportFileName + checkDate);
			}
			// }
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(this, "�����쳣�������Ա��ϵ!");
		}
	}

	public void stateChanged(ChangeEvent arg0) {
		if (maintab.getSelectedIndex() == 1) {
			if (dr == null) {
				new SplashWindow(this, new AbstractAction() {
					public void actionPerformed(ActionEvent arg0) {
						maintab.getComponentAt(1).add(getReportPanel(), BorderLayout.CENTER);
					}
				});
			}
		}
	}

	public DefaultStyleReportPanel_2 getReportPanel() {
		dr = new DefaultStyleReportPanel_2("SAL_DEPT_CHECK_SCORE_CODE3", "");

		// ��������Ĭ��ֵΪ��ǰ�������� Gwang 2013-08-21
		QueryCPanel_UIRefPanel dateRef = (QueryCPanel_UIRefPanel) dr.getBillQueryPanel().getCompentByKey("checkdate");
		String checkDate = new SalaryUIUtil().getCheckDate();
		dateRef.setValue(checkDate);
		
		//���õ����ļ����� Gwang 2013-08-31
		dr.setReportExpName(exportFileName);

		dr.getBillCellPanel().setEditable(false);
		dr.getBillQueryPanel().addBillQuickActionListener(this);
		return dr;
	}
}
