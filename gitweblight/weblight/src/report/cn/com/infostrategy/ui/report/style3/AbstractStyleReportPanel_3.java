package cn.com.infostrategy.ui.report.style3;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.JPanel;

import cn.com.infostrategy.to.report.BillChartVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.infostrategy.ui.report.ReportServiceIfc;
import cn.com.infostrategy.ui.report.chart.BillChartPanel;

/**
 * ��񱨱�3,�����ϸ���ѯ��,�����Ǹ�BillChartPanel
 * ����ѯ���е������͵���̨,��̨���һHashVO[],Ȼ��ǰ̨������������BillChartPanel��!!
 * ������ͳ�ƿ�����ʾ
 * @author xch
 * 
 */
public abstract class AbstractStyleReportPanel_3 extends JPanel implements ActionListener {

	private BillQueryPanel billQueryPanel = null; // ��ѯ���
	private BillChartPanel billChartPanel = null; // BilChartPanel
	private JPanel contentPanel = new JPanel(); // ������
	private boolean isShowTotalColumn = true;

	public abstract String getBillQueryTempletCode(); // ��ѯģ�����...

	public abstract String getBSBuildDataClass(); // BS�˹������ݵ�����!!

	public AbstractStyleReportPanel_3(boolean _isautoinit) {
		if (_isautoinit) {
			initialize(); //
		}
	}

	/**
	 * ���췽��..
	 */
	public AbstractStyleReportPanel_3() {
		initialize(); //
	}

	protected void initialize() {
		this.setLayout(new BorderLayout(5,10)); //
		billQueryPanel = new BillQueryPanel(getBillQueryTempletCode()); //
		billQueryPanel.addBillQuickActionListener(this); //	

		contentPanel.setLayout(new BorderLayout()); //
		BillCellPanel cellPanel = new BillCellPanel(null, true, false, true); //
		cellPanel.span(0, 2, 2, 4); //
		cellPanel.setValueAt("�������ѯ����������ѯ��ť!", 0, 2); //
		cellPanel.setHalign(new int[] { 0 }, new int[] { 2 }, 2); //
		contentPanel.add(cellPanel); // Ԥ����м�Ϊ�գ�Ȼ������ѯ����CHART

		this.add(billQueryPanel, BorderLayout.NORTH); //
		this.add(contentPanel, BorderLayout.CENTER); //
	}

	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == billQueryPanel) {
				final HashMap map_condition = billQueryPanel.getQuickQueryConditionAsMap(); // ȡ�����в�ѯ����!!
				if (map_condition == null) {
					return; //
				}

				new SplashWindow(billQueryPanel, new AbstractAction() {
					public void actionPerformed(ActionEvent e) {
						try {
							ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class); // ͨ��Զ�̷���������BillChartVO
							BillChartVO billChartVO = service.styleReport_3_BuildData(map_condition, getBSBuildDataClass(), ClientEnvironment.getCurrLoginUserVO()); //
							billChartPanel = new BillChartPanel(billChartVO.getTitle(), billChartVO.getXHeadName(), billChartVO.getYHeadName(), billChartVO, isShowTotalColumn);
							contentPanel.removeAll(); //
							contentPanel.setLayout(new BorderLayout()); //
							contentPanel.add(billChartPanel, BorderLayout.CENTER); //
							contentPanel.updateUI(); //

						} catch (Exception ex) {
							MessageBox.showException(billQueryPanel, ex); //
						}
					}
				});
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	public boolean isShowTotalColumn() {
		return isShowTotalColumn;
	}

	public void setShowTotalColumn(boolean newvalue) {
		this.isShowTotalColumn = newvalue;
	}

	public BillQueryPanel getBillQueryPanel() {
		return billQueryPanel;
	}

	public BillChartPanel getBillChartPanel() {
		return billChartPanel;
	}

}
