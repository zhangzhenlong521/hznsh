package cn.com.pushworld.salary.ui.personalcenter.p060;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;
import cn.com.infostrategy.ui.report.style2.DefaultStyleReportPanel_2;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

/**
 * �߹ܵ���������ָ��ĵ÷ֱ� ���³��Ĵ����� 2��ҳǩ
 * 
 * @author Administrator
 * 
 */
public class PostCheckTargetResQueryWKPanel extends AbstractWorkPanel implements ActionListener, ChangeListener {
	private DefaultStyleReportPanel_2 dr = null;
	private DefaultStyleReportPanel_2 dr2 = null;
	private BillQueryPanel bq, bq2 = null;
	private WLTTabbedPane maintab = null;

	private String exportFileName1 = "�ֹ�ָ�꿼�˵÷ֱ�_";
	private String exportFileName2 = "���³����˵÷ֱ�_";
	
	public void initialize() {
		dr = new DefaultStyleReportPanel_2("SAL_TARGET_CHECK_LOG_PERSON", "");
		bq = dr.getBillQueryPanel();
		// ��������Ĭ��ֵΪ��ǰ�������� Gwang 2013-08-21
		QueryCPanel_UIRefPanel dateRef = (QueryCPanel_UIRefPanel) bq.getCompentByKey("checkdate");
		String checkDate = new SalaryUIUtil().getCheckDate();
		dateRef.setValue(checkDate);
		
		//���õ����ļ����� Gwang 2013-08-31
		dr.setReportExpName(exportFileName1);
		

		dr.getBillCellPanel().setEditable(false);
		bq.addBillQuickActionListener(this);
		if ("Y".equals(this.getMenuConfMapValueAsStr("�Ƿ��Ƕ��³�"))) {
			this.add(dr, BorderLayout.CENTER);
		} else {
			maintab = new WLTTabbedPane();
			maintab.addTab("�ֹ�ָ��	", dr);
			maintab.addTab("���³�����", new JPanel(new BorderLayout()));
			maintab.addChangeListener(this);
			this.add(maintab, BorderLayout.CENTER);
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bq) {
			new SplashWindow(this, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					onQuery();
				}
			});
		} else {
			new SplashWindow(this, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					onQuery2();
				}
			});
		}
	}

	private void onQuery() {
		try {
			if (bq.checkValidate()) {
				SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
				dr.getBillCellPanel().loadBillCellData(ifc.getPostTargetCheckResVO(bq.getRealValueAt("checkdate"), ClientEnvironment.getCurrLoginUserVO().getId()));
				dr.getBillCellPanel().setEditable(false);
				
				//���õ����ļ����� Gwang 2013-08-31
				dr.setReportExpName(exportFileName1 + bq.getRealValueAt("checkdate"));
				if (dr.getBillCellPanel().getRowCount() > 2) {
					dr.getBillCellPanel().setLockedCell(2, 1); //������ͷ 
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(this, "�����쳣�������Ա��ϵ!");
		}
	}
	
	private void onQuery2() {
		try {
			if (bq2.checkValidate()) {
				SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
				dr2.getBillCellPanel().loadBillCellData(ifc.getPostCheckResVO(bq2.getRealValueAt("checkdate"), ClientEnvironment.getCurrLoginUserVO().getId()));
				dr2.getBillCellPanel().setEditable(false);
				
				//���õ����ļ����� Gwang 2013-08-31
				dr.setReportExpName(exportFileName2 + bq.getRealValueAt("checkdate"));
				if (dr.getBillCellPanel().getRowCount() > 2) {
					dr.getBillCellPanel().setLockedCell(2, 1); //������ͷ 
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(this, "�����쳣�������Ա��ϵ!");
		}
	}

	public void stateChanged(ChangeEvent e) {
		if (maintab.getSelectedIndex() == 1 && dr2 == null) {
			new SplashWindow(this, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					getSecondPanel();
				}
			});
			//���õ����ļ����� Gwang 2013-08-31
			dr2.setReportExpName(exportFileName2);
		}
	}
	
	public void getSecondPanel() {
		dr2 = new DefaultStyleReportPanel_2("SAL_TARGET_CHECK_LOG_PERSON", "");
		bq2 = dr2.getBillQueryPanel();
		// ��������Ĭ��ֵΪ��ǰ�������� Gwang 2013-08-21
		QueryCPanel_UIRefPanel dateRef = (QueryCPanel_UIRefPanel) bq2.getCompentByKey("checkdate");
		String checkDate = new SalaryUIUtil().getCheckDate();
		dateRef.setValue(checkDate);
		dr2.getBillCellPanel().setEditable(false);
		bq2.addBillQuickActionListener(this);
		maintab.getComponentAt(1).add(dr2);
	}

}
