package cn.com.pushworld.salary.ui.personalcenter.p010;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

/**
 * 个人信息查询,加上个人履职.
 * @author Gwang
 * 2013-8-8 下午02:59:22
 */
public class SelfInfoDispWKPanel extends AbstractWorkPanel {

	private static final long serialVersionUID = -1885707248005902954L;

	@Override
	public void initialize() {
		String loginUserCode = ClientEnvironment.getInstance().getLoginUserCode();
		BillCardPanel billCard = new BillCardPanel("SAL_PERSONINFO_CODE1");
		billCard.queryDataByCondition("code = '" + loginUserCode + "'");
		try {
			BillCellVO cellvo = UIUtil.getMetaDataService().getBillCellVO("员工简历", null, null);
			HashVO hvo[] = UIUtil.getHashVoArrayByDS(null, "select * from v_sal_personinfo where code = '" + ClientEnvironment.getInstance().getLoginUserCode()+"'");
			if (cellvo != null && hvo.length > 0) {
				BillCellVO cvo = SalaryUIUtil.getService().parseCellTempetToWord(cellvo, hvo[0]);
				BillCellPanel cellpanel = new BillCellPanel();
				cellpanel.setEditable(false);
				cellpanel.setToolBarVisiable(false);
				cellpanel.loadBillCellData(cvo);
				WLTTabbedPane tabp = new WLTTabbedPane();
				tabp.addTab("基本信息", UIUtil.getImage("zt_061.gif"), billCard);
//				tabp.addTab("个人简历", UIUtil.getImage("office_123.gif"), cellpanel);
				this.add(tabp);
				return;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		this.add(billCard);
	}

}
