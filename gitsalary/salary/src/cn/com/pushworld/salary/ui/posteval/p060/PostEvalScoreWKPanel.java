package cn.com.pushworld.salary.ui.posteval.p060;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;
import cn.com.infostrategy.ui.report.BillCellHtmlHrefEvent;
import cn.com.infostrategy.ui.report.BillCellHtmlHrefListener;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.infostrategy.ui.report.style2.DefaultStyleReportPanel_2;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

/**
 * ��λ��ֵ�����÷�ͳ�ơ����/2013-10-31��
 * @author lcj
 *
 */
public class PostEvalScoreWKPanel extends AbstractWorkPanel implements BillCellHtmlHrefListener, ActionListener {
	private static final long serialVersionUID = 1L;
	private String exportFileName = "��λ�����÷�_";
	private DefaultStyleReportPanel_2 reportPanel = null;
	private BillQueryPanel queryPanel = null;
	private BillCellPanel cellPanel = null;
	private boolean isquerying = false;
	private SalaryServiceIfc service;

	public void initialize() {
		reportPanel = new DefaultStyleReportPanel_2("SAL_POST_EVAL_PLAN_LCJ_Q03", "");//ֻ��ѡ�����������ļƻ�
		reportPanel.setReportExpName(exportFileName);//���õ����ļ�����
		queryPanel = reportPanel.getBillQueryPanel();
		cellPanel = reportPanel.getBillCellPanel();

		cellPanel.setEditable(false);
		cellPanel.addBillCellHtmlHrefListener(this);
		queryPanel.addBillQuickActionListener(this);
		this.add(reportPanel);
		
		//��ͷ������һ��getRootPane�� ԭ�����Ҳ���rootPane ��Ϊ��û�ӵ�������� 
		try {
			HashVO[] planvos = UIUtil.getHashVoArrayByDS(null, "select * from SAL_POST_EVAL_PLAN where status='��������' order by enddate desc");
			if (planvos != null && planvos.length > 0) {//Ĭ��ѡ���������ļƻ�������ͳ�Ƴ���
				QueryCPanel_UIRefPanel dateRef = (QueryCPanel_UIRefPanel) queryPanel.getCompentByKey("planid");
				RefItemVO itemvo = new RefItemVO(planvos[0].getStringValue("id"), "", planvos[0].getStringValue("planname"));
				dateRef.setObject(itemvo);
				new Timer().schedule(new TimerTask() {
					public void run() {
						onQuery();
					}
				}, 0);
			}
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onBillCellHtmlHrefClicked(BillCellHtmlHrefEvent _event) {
		String keyString = _event.getCellItemKey();
		if (keyString == null || !keyString.contains("-")) {
			return;
		}
		String planid = keyString.substring(0, keyString.indexOf("-"));
		String postid = keyString.substring(keyString.indexOf("-") + 1);
		try {
			BillCellPanel cellPanel_1 = new BillCellPanel();
			cellPanel_1.loadBillCellData(getService().getPostEvalScoreDetailVO(planid, postid));
			cellPanel_1.setToolBarVisiable(false); //
			cellPanel_1.setAllowShowPopMenu(false); //
			cellPanel_1.setEditable(false);
			String str_targetlevel = UIUtil.getStringValueByDS(null, "select max(length(linkcode)) from sal_post_eval_target_copy where planid=" + planid);
			int titlelevel = Integer.parseInt(str_targetlevel) / 4 + 2;//���һ��Ȩ�أ�һ�и�λ����ְ����
			if (cellPanel_1.getRowCount() > titlelevel) {
				cellPanel_1.setLockedCell(titlelevel, 1);
			}

			new SalaryUIUtil().formatSpan(cellPanel_1, new int[] { 0 });

			BillCellItemVO postItemVO = cellPanel.getBillCellItemVOAt(_event.getRow(), 1);
			String postname = postItemVO.getCellvalue();
			BillDialog billDialog = new BillDialog(this, postname, 800, 600);
			billDialog.getContentPane().add(cellPanel_1);
			billDialog.addOptionButtonPanel(new String[] { "�ر�" });
			billDialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showException(this, e);
		}
	}

	public void actionPerformed(ActionEvent e) {
		onQuery();
	}

	private void onQuery() {
		if (isquerying) {
			return;
		}
		isquerying = true;
		try {
			if (queryPanel.checkValidate()) {
				cellPanel.loadBillCellData(getService().getPostEvalScoreVO(queryPanel.getRealValueAt("planid")));
				cellPanel.setEditable(false);
				if (cellPanel.getRowCount() > 2) {
					cellPanel.setLockedCell(2, 2); //������ͷ
				}
				cellPanel.getTable().getColumnModel().getColumn(0).setPreferredWidth(120);
				cellPanel.getTable().getColumnModel().getColumn(1).setPreferredWidth(120);
				cellPanel.getTable().getColumnModel().getColumn(3).setPreferredWidth(100);
				cellPanel.getTable().getColumnModel().getColumn(4).setPreferredWidth(100);
				cellPanel.getTable().getColumnModel().getColumn(5).setPreferredWidth(110);

				reportPanel.setReportExpName(exportFileName + queryPanel.getValueAt("planid"));//���õ����ļ�����
				new SalaryUIUtil().formatSpan(cellPanel, new int[] { 0 });
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			MessageBox.showException(this, ex);
		}
		isquerying = false;
	}

	private SalaryServiceIfc getService() throws Exception {
		if (service == null) {
			service = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
		}
		return service;
	}
}
