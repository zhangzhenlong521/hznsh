package cn.com.pushworld.salary.ui.posteval.p050;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.infostrategy.ui.report.style2.DefaultStyleReportPanel_2;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

/**
 * ��λ��ֵ��������ͳ�ơ����/2013-10-31��
 * @author lcj
 *
 */
public class PostEvalScheduleWKPanel extends AbstractWorkPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private String exportFileName = "��λ��������_";
	private DefaultStyleReportPanel_2 reportPanel = null;
	private BillQueryPanel queryPanel = null;
	private boolean isquerying = false;
	private SalaryServiceIfc service = null;

	public void initialize() {
		reportPanel = new DefaultStyleReportPanel_2("SAL_POST_EVAL_PLAN_LCJ_Q01", "");//ֻ��ѡ�������л����������ļƻ�����Ϊδ�����ļƻ���û�д������ּ�¼���ʲ鲻����¼
		reportPanel.setReportExpName(exportFileName);//���õ����ļ�����
		queryPanel = reportPanel.getBillQueryPanel();
		reportPanel.getBillCellPanel().setEditable(false);
		queryPanel.addBillQuickActionListener(this);
		this.add(reportPanel);
		try {
			HashVO[] planvos = UIUtil.getHashVoArrayByDS(null, "select * from SAL_POST_EVAL_PLAN where status='������'");
			if (planvos != null && planvos.length > 0) {//Ĭ��ѡ�е�ǰ�������мƻ�������ͳ�Ƴ���
				QueryCPanel_UIRefPanel dateRef = (QueryCPanel_UIRefPanel) queryPanel.getCompentByKey("planid");
				RefItemVO itemvo = new RefItemVO(planvos[0].getStringValue("id"), "", planvos[0].getStringValue("planname"));
				dateRef.setObject(itemvo);
				new Timer().schedule(new TimerTask() {
					public void run() {
						onQuery();
					}
				}, 0);
			} else {//���û�������еļƻ���������������ļƻ�
				planvos = UIUtil.getHashVoArrayByDS(null, "select * from SAL_POST_EVAL_PLAN where status='��������' order by enddate desc");
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
			}

		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
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
				BillCellPanel cellPanel = reportPanel.getBillCellPanel();
				cellPanel.loadBillCellData(getService().getPostEvalScheduleVO(queryPanel.getRealValueAt("planid")));
				cellPanel.setEditable(false);
				if (cellPanel.getRowCount() > 7) {
					cellPanel.setLockedCell(7, 1); //������ͷ
					cellPanel.getTable().getColumnModel().getColumn(0).setPreferredWidth(150);
					cellPanel.getTable().getColumnModel().getColumn(3).setPreferredWidth(100);
					cellPanel.getTable().getColumnModel().getColumn(4).setPreferredWidth(100);
					reportPanel.setReportExpName(exportFileName + queryPanel.getValueAt("planid"));//���õ����ļ�����
					new SalaryUIUtil().formatSpan(cellPanel, new int[] { 0 });
				}
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
