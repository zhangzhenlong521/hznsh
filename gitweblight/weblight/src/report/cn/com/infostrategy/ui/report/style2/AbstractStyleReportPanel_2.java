package cn.com.infostrategy.ui.report.style2;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.JPanel;

import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.infostrategy.ui.report.ReportServiceIfc;

/**
 * ��񱨱�2,�����ϸ���ѯ��,�����Ǹ�BillCellPanel�����!!
 * ����ѯ���е������͵���̨,��̨���һHashVO[],Ȼ��ǰ̨������������BillCellPanel��!!
 * @author xch
 *
 */
public abstract class AbstractStyleReportPanel_2 extends JPanel implements ActionListener {

	private BillQueryPanel billQueryPanel = null; //��ѯ���
	private BillCellPanel billCellPanel = null; //BillCellPanel
	private WLTButton btn_export_excel, btn_export_html; //������ť
	private String reportExpName = null;

	public abstract String getBillQueryTempletCode(); //��ѯģ�����...

	public abstract String getBSBuildDataClass(); //BS�˹������ݵ�����!!

	private JPanel panel_btn = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 2)); //

	public AbstractStyleReportPanel_2(boolean _isautoinit) {
		if (_isautoinit) {
			initialize(); //
		}
	}

	/**
	 * ���췽��..
	 */
	public AbstractStyleReportPanel_2() {
		initialize(); //
	}

	protected void initialize() {
		this.setLayout(new BorderLayout()); //
		billQueryPanel = new BillQueryPanel(getBillQueryTempletCode()); //
		billCellPanel = new BillCellPanel(); //
		billCellPanel.setToolBarVisiable(false); //
		billCellPanel.setAllowShowPopMenu(false); //
		billCellPanel.setEditable(false); //���ж����ɱ༭
		reportExpName = billQueryPanel.getTempletVO().getTempletname();
		billQueryPanel.addBillQuickActionListener(this); //
		this.add(billQueryPanel, BorderLayout.NORTH); //

		btn_export_excel = new WLTButton("����Excel", UIUtil.getImage("icon_xls.gif")); //
		btn_export_html = new WLTButton("����Html", UIUtil.getImage("zt_064.gif")); //
		btn_export_excel.addActionListener(this);
		btn_export_html.addActionListener(this);
		panel_btn.add(btn_export_excel);
		panel_btn.add(btn_export_html);

		JPanel panel_btn_cell = new JPanel(new BorderLayout()); //
		panel_btn_cell.add(panel_btn, BorderLayout.NORTH);
		panel_btn_cell.add(billCellPanel, BorderLayout.CENTER);
		this.add(panel_btn_cell, BorderLayout.CENTER); //
	}

	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == billQueryPanel) {
				final HashMap map_condition = billQueryPanel.getQuickQueryConditionAsMap(); //ȡ�����в�ѯ����!!
				if (map_condition == null) {
					return; //
				}

				new SplashWindow(billQueryPanel, new AbstractAction() {
					public void actionPerformed(ActionEvent e) {
						try {
							ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class); //
							BillCellVO billCellVO = service.styleReport_2_BuildData(map_condition, getBSBuildDataClass(), ClientEnvironment.getCurrLoginUserVO()); //
							billCellPanel.loadBillCellData(billCellVO); //
							billCellPanel.setEditable(false); //���ж����ɱ༭
						} catch (Exception ex) {
							MessageBox.showException(billQueryPanel, ex); //
						}
					}
				});
			} else if (e.getSource() == btn_export_excel) {
				billCellPanel.exportExcel((reportExpName == null || "".equals(reportExpName.trim())) ? "�������ݵ���" : reportExpName, "StyleReportPanel"); //Ԭ���� 20130911 ����  ��Ҫ����񱨱����ά����ֿ�
			} else if (e.getSource() == btn_export_html) {
				billCellPanel.exportHtml((reportExpName == null || "".equals(reportExpName.trim())) ? "�������ݵ���" : reportExpName); //
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}
	
	public BillQueryPanel getBillQueryPanel() {
		return billQueryPanel;
	}

	public BillCellPanel getBillCellPanel() {
		return billCellPanel;
	}

	public void setReportExpName(String reportExpName) {
		this.reportExpName = reportExpName;
	}
	public JPanel getPanel_btn() {
		return panel_btn;
	}

}
