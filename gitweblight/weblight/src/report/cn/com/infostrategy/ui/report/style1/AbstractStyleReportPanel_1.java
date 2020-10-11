package cn.com.infostrategy.ui.report.style1;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.report.ReportServiceIfc;

/**
 * ��񱨱�1,����򵥵ı���,�����ϸ���ѯ��,�����Ǹ����!! �⿴�����뵥�б����,��ʵ����������,������ܲ��Ǽ򵥵�SQL��ѯ,���Ǹ��ӵ��߼�����,����󷵻ص���һ���б�����!
 * ����ѯ���е������͵���̨,��̨���һHashVO[],Ȼ��ǰ̨�����������������!!
 * �˷�񱨱��Ǽ̳���JPanel������AbstractWorkPanel,���Ա��뻹Ҫ����һ��AbstractWorkPanel,Ȼ�󽫷�񱨱���룡
 * ΪʲôҪ��ô�����?��Ϊ��������ʹ��һ����ҳǩ,�������񱨱�װ��һ��ҳ����!
 * @author xch
 *
 */
public abstract class AbstractStyleReportPanel_1 extends JPanel {

	private BillQueryPanel billQueryPanel = null; //
	private BillListPanel billListPanel = null; //

	public abstract String getBillQueryTempletCode(); //��ѯģ�����

	public abstract String getBillListTempletCode(); //�б�ģ�����

	public abstract String getBSBuildDataClass(); //BS�˹������ݵ�����!!,����ʵ�ֽӿ�StyleReport_1_BuildDataIFC

	public AbstractStyleReportPanel_1(boolean _isautoinit) {
		if (_isautoinit) {
			initialize(); //
		}
	}

	/**
	 * ���췽��..
	 */
	public AbstractStyleReportPanel_1() {
		initialize(); //
	}

	/**
	 * ��ʼ��ҳ��!
	 */
	protected void initialize() {
		this.setLayout(new BorderLayout()); //
		billQueryPanel = new BillQueryPanel(getBillQueryTempletCode()); //
		billListPanel = new BillListPanel(getBillListTempletCode()); //
		billListPanel.setQuickQueryPanelVisiable(false); //��ѯ����Զ����!
		billListPanel.setPagePanelVisible(false); //��񱨱�1��ֱ���������,�����б��Ĭ���߼�,���Է�ҳ��û�������!ֱ�����ص�!
		billListPanel.setItemEditable(false); //ͳͳ����!
		billListPanel.setAllBillListBtnVisiable(false); //�Ƚ����а�ť����,�ھ������л������ٷų�����
		billListPanel.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_EXPORTEXCEL)); //����Excel,Ĭ�ϼ�һ������Excel
		billListPanel.repaintBillListButton(); //
		billQueryPanel.addBillQuickActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doQueryData();
			}

		}); //

		this.add(billQueryPanel, BorderLayout.NORTH); //
		this.add(billListPanel, BorderLayout.CENTER); //
	}

	/**
	 * ��ѯ����
	 */
	private void doQueryData() {
		try {
			final HashMap map_condition = billQueryPanel.getQuickQueryConditionAsMap(true); //ȡ�����в�ѯ����!!
			if (map_condition == null) {
				return; //
			}

			new SplashWindow(billQueryPanel, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					try {
						ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class); //
						HashVO[] hvs = service.styleReport_1_BuildData(map_condition, getBSBuildDataClass(), ClientEnvironment.getCurrLoginUserVO()); //
						billListPanel.putValue(hvs); //��������!!!
						billListPanel.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT); //
					} catch (Exception ex) {
						MessageBox.showException(billQueryPanel, ex); //
					}
				}
			});
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	public BillQueryPanel getBillQueryPanel() {
		return billQueryPanel;
	}

	public BillListPanel getBillListPanel() {
		return billListPanel;
	}

}
