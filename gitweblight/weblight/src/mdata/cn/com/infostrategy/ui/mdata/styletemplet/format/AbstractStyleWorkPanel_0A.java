/**************************************************************************
 * $RCSfile: AbstractStyleWorkPanel_0A.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.format;

import java.awt.BorderLayout;
import java.lang.reflect.Constructor;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTRadioPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillButtonPanel;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillFormatPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.infostrategy.ui.report.chart.BillChartPanel;
import cn.com.infostrategy.ui.workflow.design.WorkFlowDesignWPanel;
import cn.com.infostrategy.ui.workflow.pbom.BillBomPanel;

/**
 * 风格模板1的抽象面板1
 * @author xch
 *
 */
public abstract class AbstractStyleWorkPanel_0A extends AbstractWorkPanel {

	private BillFormatPanel mainBillFormat; //

	private IUIIntercept_0A uiIntercept = null;

	/**
	 * 定义公式
	 */
	public abstract String getFormatFormula(); //

	public abstract String getUIInterceptName(); //

	@Override
	public void initialize() {
		if (getUIInterceptName() != null && !getUIInterceptName().trim().equals("")) {
			try {
				//可能拦截器名称包括参数,比如("billno",)
				String str_interceptname = getUIInterceptName(); //
				if (str_interceptname.indexOf("(") > 0) { //如果有参数则调用对应参数的构造方法!!!
					TBUtil tbutil = new TBUtil(); //
					String str_csname = str_interceptname.substring(0, str_interceptname.indexOf("(")); //
					String str_par = str_interceptname.substring(str_interceptname.indexOf("(") + 1, str_interceptname.indexOf(")")); //
					str_par = tbutil.replaceAll(str_par, "\"", ""); //把双引号去掉!!!
					String[] str_pars = tbutil.split(str_par, ","); //以逗号为分割除,创建数组
					Class cp[] = new Class[str_pars.length];
					for (int i = 0; i < cp.length; i++) {
						cp[i] = java.lang.String.class;
					}

					Class panelclass = Class.forName(str_csname); //
					Constructor constructor = panelclass.getConstructor(cp); //
					uiIntercept = (IUIIntercept_0A) constructor.newInstance(str_pars); ////..
				} else {
					uiIntercept = (IUIIntercept_0A) Class.forName(str_interceptname).newInstance();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} //
		}

		if (uiIntercept != null) {
			//uiIntercept.afterSysInitialize(); //以后是否考虑在初始页面之前可以做点什么???
		}

		this.setLayout(new BorderLayout()); //
		mainBillFormat = new BillFormatPanel(getFormatFormula()); //生成生成BillFormatPanel

		this.add(mainBillFormat); //

		if (uiIntercept != null) {
			try {
				uiIntercept.afterSysInitialize(this); //
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public BillFormatPanel getBillFormatPanel() {
		return mainBillFormat;
	}

	public IUIIntercept_0A getUiIntercept() {
		return uiIntercept;
	}

	public BillCardPanel getBillCardPanel() {
		return mainBillFormat.getBillCardPanel();
	}

	public BillCardPanel getBillCardPanelByTempletCode(String _templetcode) {
		return mainBillFormat.getBillCardPanelByTempletCode(_templetcode);
	}

	public BillCardPanel getBillCardPanelByTempletCode(String _templetcode, int _index) {
		return mainBillFormat.getBillCardPanelByTempletCode(_templetcode, _index);
	}

	public BillListPanel getBillListPanel() {
		return mainBillFormat.getBillListPanel();
	}

	public BillChartPanel getBillChartPanel() {
		return null;//mainBillFormat.get();
	}

	public BillListPanel getBillListPanelByTempletCode(String _templetcode) {
		return mainBillFormat.getBillListPanelByTempletCode(_templetcode);
	}

	public BillListPanel getBillListPanelByTempletCode(String _templetcode, int _index) {
		return mainBillFormat.getBillListPanelByTempletCode(_templetcode, _index);
	}

	public BillTreePanel getBillTreePanel() {
		return mainBillFormat.getBillTreePanel();
	}

	public BillTreePanel getBillTreePanelByTempletCode(String _templetcode) {
		return mainBillFormat.getBillTreePanelByTempletCode(_templetcode);
	}

	public BillTreePanel getBillTreePanelByTempletCode(String _templetcode, int _index) {
		return mainBillFormat.getBillTreePanelByTempletCode(_templetcode, _index);
	}

	public BillQueryPanel getBillQueryPanel() {
		return mainBillFormat.getBillQueryPanel();
	}

	public BillQueryPanel getBillQueryPanelByTempletCode(String _templetcode) {
		return mainBillFormat.getBillQueryPanelByTempletCode(_templetcode);
	}

	public BillCellPanel getBillCellPanel() {
		return mainBillFormat.getBillCellPanel();
	}

	public BillBomPanel getBillBomPanel() {
		return mainBillFormat.getBillBomPanel();
	}

	public BillCellPanel getBillCellPanelByTempletCode(String _templetcode) {
		return mainBillFormat.getBillCellPanelByTempletCode(_templetcode);
	}

	public JPanel getJPanelByClassName(String _className) {
		return mainBillFormat.getJPanelByClassName(_className);
	}

	public WorkFlowDesignWPanel getWorkFlowPanel() {
		return mainBillFormat.getWorkFlowPanel();
	}

	public WLTTabbedPane getTabbedPane() {
		return mainBillFormat.getTabbedPane();
	}

	public WLTRadioPane getRadioTabbedPane() {
		return mainBillFormat.getRadioTabbedPane();
	}

	public BillButtonPanel getBillButtonPanel() {
		return mainBillFormat.getBillButtonPanel();
	}

	public JPanel getLabelPanel() {
		return mainBillFormat.getLabelPanel();
	}

	public JPanel getLabelPanelByCode(String _code) {
		return mainBillFormat.getLabelPanelByCode(_code);
	}

	//取得分割器控件
	public JSplitPane getSplitPane() {
		return mainBillFormat.getSplitPane(); //
	}

	public BillFormatPanel getMainBillFormat() {
		return mainBillFormat;
	}
}
