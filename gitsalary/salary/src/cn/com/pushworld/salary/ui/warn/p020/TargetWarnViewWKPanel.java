package cn.com.pushworld.salary.ui.warn.p020;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTextArea;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;
import cn.com.infostrategy.ui.report.CommonDialPlotPanel;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

/**
 * 指标预警预览界面。
 * @author haoming
 * create by 2013-11-17
 */
public class TargetWarnViewWKPanel extends AbstractWorkPanel implements ActionListener {
	private static final long serialVersionUID = 5959631487735732598L;
	private JPanel mainPanel = new JPanel(new ModifiedFlowLayout(FlowLayout.LEFT));
	private BillQueryPanel query = new BillQueryPanel("SAL_TARGET_CHECK_LOG_PERSON");;
	private HashVO warnvos[];
	private SalaryServiceIfc service;

	@Override
	public void initialize() {
		query.addBillQuickActionListener(this);
		QueryCPanel_UIRefPanel dateRef = (QueryCPanel_UIRefPanel) query.getCompentByKey("checkdate");
		String checkDate = new SalaryUIUtil().getCheckDate();
		dateRef.setValue(checkDate);
		WLTSplitPane splitpanel = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT);
		splitpanel.setOpaque(false);
		splitpanel.setDividerLocation(40);
		splitpanel.add(query, WLTSplitPane.TOP);
		JScrollPane scroll = new JScrollPane();
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.getVerticalScrollBar().setUnitIncrement(20);
		scroll.getViewport().add(mainPanel);
		splitpanel.add(scroll, WLTSplitPane.BOTTOM);
		this.add(splitpanel);
		if (!TBUtil.isEmpty(checkDate)) {
			JLabel label = new JLabel(UIUtil.getImage("process.gif"));
			mainPanel.add(label, BorderLayout.NORTH);
			java.util.Timer timer = new java.util.Timer();
			timer.schedule(new TimerTask() {
				public void run() {
					onQuery();
				}
			}, 50);
		} else {
			JLabel label = new JLabel("请选择考核日期.");
			mainPanel.add(label, BorderLayout.NORTH);
		}

	}

	public void onQuery() {
		String checkdate = (String) query.getQuickQueryConditionAsMap().get("checkdate");
		if (checkdate == null) {
			mainPanel.removeAll();
			return;
		}
		try {
			warnvos = UIUtil.getHashVoArrayByDS(null, "select t1.*,t2.name from sal_target_warn t1 left join sal_target_list t2 on t1.targetid = t2.id order by t1.seq");
		} catch (WLTRemoteException e1) {
			MessageBox.showException(this, e1);
			return;
		} catch (Exception e1) {
			MessageBox.showException(this, e1);
			return;
		}
		if (warnvos == null || warnvos.length == 0) {
			mainPanel.removeAll();
			mainPanel.add(new JLabel("系统中没有指标预警配置项,请到[预警配置管理]进行添加."));
			mainPanel.updateUI();
			return;
		}
		try {
			mainPanel.removeAll();
			String logid = UIUtil.getStringValueByDS(null, "select *from sal_target_check_log where checkdate='" + checkdate + "'");
			if (TBUtil.isEmpty(logid)) {
				MessageBox.show(this, "该日期没有进行考核.");
				return;
			}
			for (int j = 0; j < warnvos.length; j++) {
				try {
					HashVO warvo = getService().getWarnVoById(warnvos[j].getStringValue("id"), logid);
					JPanel panel = getDial1(warvo);
					mainPanel.add(panel);
					mainPanel.updateUI();
				} catch (Exception ex) {
					ex.printStackTrace();
					WLTTextArea area = new WLTTextArea();
					area.setPreferredSize(new Dimension(350, 350));
					area.setEditable(false);
					area.setText(ex.getMessage());
					mainPanel.add(area);
					mainPanel.updateUI();
				}
			}
		} catch (WLTRemoteException e) {
			MessageBox.showException(this, e);
			return;
		} catch (Exception e) {
			MessageBox.showException(this, e);
			return;
		}
	}

	public void actionPerformed(ActionEvent actionevent) {
		onQuery();
	}

	public JPanel getDial1(HashVO warnvo) {
		double w2 = warnvo.getDoubleValue("warn2", 0d);
		double w3 = warnvo.getDoubleValue("warn3", 0d);
		try {
			if (w2 <= w3) {
				JPanel jp = CommonDialPlotPanel.getDialPlotPanelByHashVO(warnvo, true, 300, 300, 302);//警界值大于正常值
				jp.setPreferredSize(new Dimension(350, 350));
				return jp;
			} else {
				JPanel jp = CommonDialPlotPanel.getDialPlotPanelByHashVO(warnvo, false, 300, 300, 302);//警界值大于正常值
				jp.setPreferredSize(new Dimension(350, 350));
				return jp;
			}
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
		return new JPanel();
	}

	private SalaryServiceIfc getService() {
		if (service == null) {
			try {
				service = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return service;
	}

	class ModifiedFlowLayout extends FlowLayout {

		public ModifiedFlowLayout() {
			super();
		}

		public ModifiedFlowLayout(int align) {
			super(align);
		}

		public ModifiedFlowLayout(int align, int hgap, int vgap) {
			super(align, hgap, vgap);
		}

		public Dimension minimumLayoutSize(Container target) {
			return computeSize(target, false);
		}

		public Dimension preferredLayoutSize(Container target) {
			return computeSize(target, false);
		}

		private Dimension computeSize(Container target, boolean minimum) {
			synchronized (target.getTreeLock()) {
				int hgap = getHgap();
				int vgap = getVgap();
				int w = target.getWidth();

				if (w == 0) {
					w = Integer.MAX_VALUE;
				}

				Insets insets = target.getInsets();
				if (insets == null) {
					insets = new Insets(0, 0, 0, 0);
				}
				int reqdWidth = 0;

				int maxwidth = w - (insets.left + insets.right + hgap * 2);
				int n = target.getComponentCount();
				int x = 0;
				int y = insets.top;
				int rowHeight = 0;

				for (int i = 0; i < n; i++) {
					Component c = target.getComponent(i);
					if (c.isVisible()) {
						Dimension d = minimum ? c.getMinimumSize() : c.getPreferredSize();
						if ((x == 0) || ((x + d.width) <= maxwidth)) {
							if (x > 0) {
								x += hgap;
							}
							x += d.width;
							rowHeight = Math.max(rowHeight, d.height);
						} else {
							x = d.width;
							y += vgap + rowHeight;
							rowHeight = d.height;
						}
						reqdWidth = Math.max(reqdWidth, x);
					}
				}
				y += rowHeight;
				return new Dimension(reqdWidth + insets.left + insets.right, y);
			}
		}
	}
}
