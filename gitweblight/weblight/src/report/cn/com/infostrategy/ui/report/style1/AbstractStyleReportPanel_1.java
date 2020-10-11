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
 * 风格报表1,即最简单的报表,上面上个查询框,下面是个表格!! 这看起来与单列表很像,但实际上区别是,这里可能不是简单的SQL查询,而是复杂的逻辑处理,但最后返回的是一个列表数据!
 * 将查询框中的条件送到后台,后台输出一HashVO[],然后前台将该数组塞到表格中!!
 * 此风格报表是继承于JPanel而不是AbstractWorkPanel,所以必须还要创建一个AbstractWorkPanel,然后将风格报表加入！
 * 为什么要这么设计呢?因为经常遇到使用一个多页签,将多个风格报表装在一个页面上!
 * @author xch
 *
 */
public abstract class AbstractStyleReportPanel_1 extends JPanel {

	private BillQueryPanel billQueryPanel = null; //
	private BillListPanel billListPanel = null; //

	public abstract String getBillQueryTempletCode(); //查询模板编码

	public abstract String getBillListTempletCode(); //列表模板编码

	public abstract String getBSBuildDataClass(); //BS端构造数据的类名!!,必须实现接口StyleReport_1_BuildDataIFC

	public AbstractStyleReportPanel_1(boolean _isautoinit) {
		if (_isautoinit) {
			initialize(); //
		}
	}

	/**
	 * 构造方法..
	 */
	public AbstractStyleReportPanel_1() {
		initialize(); //
	}

	/**
	 * 初始化页面!
	 */
	protected void initialize() {
		this.setLayout(new BorderLayout()); //
		billQueryPanel = new BillQueryPanel(getBillQueryTempletCode()); //
		billListPanel = new BillListPanel(getBillListTempletCode()); //
		billListPanel.setQuickQueryPanelVisiable(false); //查询框永远隐藏!
		billListPanel.setPagePanelVisible(false); //风格报表1是直接输出数据,不是列表的默认逻辑,所以分页是没有意义的!直接隐藏掉!
		billListPanel.setItemEditable(false); //统统禁用!
		billListPanel.setAllBillListBtnVisiable(false); //先将所有按钮隐藏,在具体类中还可以再放出来！
		billListPanel.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_EXPORTEXCEL)); //导出Excel,默认加一个导出Excel
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
	 * 查询数据
	 */
	private void doQueryData() {
		try {
			final HashMap map_condition = billQueryPanel.getQuickQueryConditionAsMap(true); //取得所有查询条件!!
			if (map_condition == null) {
				return; //
			}

			new SplashWindow(billQueryPanel, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					try {
						ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class); //
						HashVO[] hvs = service.styleReport_1_BuildData(map_condition, getBSBuildDataClass(), ClientEnvironment.getCurrLoginUserVO()); //
						billListPanel.putValue(hvs); //置入数据!!!
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
