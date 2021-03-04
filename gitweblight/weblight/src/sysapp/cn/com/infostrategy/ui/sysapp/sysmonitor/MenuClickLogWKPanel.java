package cn.com.infostrategy.ui.sysapp.sysmonitor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;

/**
 * 菜单点击日志日志查询与统计
 * 
 * @author xch
 * 
 */
public class MenuClickLogWKPanel extends AbstractWorkPanel implements BillListSelectListener, ActionListener {

	private static final long serialVersionUID = 1L;
	private BillListPanel menulog_panel = null; // 点击菜单日志面板
	private BillListPanel datalog_panel = null; // 数据操作主表
	private BillListPanel datalog_b_panel = null; // 逐句操作子表
	private WLTTabbedPane tabpane = null; // 页签

	private WLTButton btn_delmenulog, btn_deldatalog; // 删除菜单日志、数据日志

	private BillDialog dialog; // 删除条件选择对话框
	private BillQueryPanel queryPanel = null; // 查询面板-放在删除条件对话框中！
	private int currPage = 0; // 当前页面,0是菜单日志，1是数据日志

	@Override
	public void initialize() {
		tabpane = new WLTTabbedPane();
		menulog_panel = new BillListPanel("PUB_MENU_CLICKLOG_CODE1"); //
		datalog_panel = new BillListPanel("PUB_DBTRIGGERLOG_CODE1");
		datalog_b_panel = new BillListPanel("PUB_DBTRIGGERLOG_B_CODE1");
		WLTSplitPane split = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT);
		datalog_panel.addBillListSelectListener(this);
		btn_delmenulog = new WLTButton("删除");
		btn_deldatalog = new WLTButton("删除");
		btn_delmenulog.addActionListener(this);
		btn_deldatalog.addActionListener(this);

		datalog_panel.addBatchBillListButton(new WLTButton[] { btn_deldatalog });
		datalog_panel.repaintBillListButton();
		split.add(datalog_panel, WLTSplitPane.TOP);
		split.add(datalog_b_panel, WLTSplitPane.BOTTOM);

		menulog_panel.addBatchBillListButton(new WLTButton[] { WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD), btn_delmenulog });
		menulog_panel.repaintBillListButton();
		tabpane.addTab("菜单点击日志", menulog_panel);
		tabpane.addTab("数据操作日志", split);

		this.add(tabpane);
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		BillVO vo = _event.getCurrSelectedVO();
		if (vo == null) {
			return;
		}
		datalog_b_panel.QueryDataByCondition(" TRIGGERLOG_ID = " + vo.getStringValue("id"));
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_delmenulog) {
			currPage = 0;
			onDeleteMenuLog();
		} else if (e.getSource() == btn_deldatalog) {
			currPage = 1;
			onDeleteDataLog();
		} else if (e.getActionCommand().equals("返回")) {
			if (dialog != null) {
				dialog.setVisible(false);
				dialog.dispose();
			}
		} else if (e.getActionCommand().equals("删除日志")) {
			onCurrDelete(); // 真正的逻辑地方！
		}
	}

	public void onDeleteMenuLog() {
		queryPanel = new BillQueryPanel("PUB_DBTRIGGERLOG_CODE1", false);
		queryPanel.setVisiable("TABDESC", false);
		queryPanel.setVisiable("OPETYPE", false);
		queryPanel.setVisiable("OPEUSER", false);
		dialog = new BillDialog(menulog_panel);
		dialog.setLayout(new BorderLayout());
		JPanel btnPanel = new JPanel(new FlowLayout());
		WLTButton del_btn = new WLTButton("删除日志");
		del_btn.addActionListener(this);
		WLTButton exit_btn = new WLTButton("返回");
		exit_btn.addActionListener(this);
		btnPanel.add(del_btn);
		btnPanel.add(exit_btn);
		dialog.setTitle("删除日志");
		dialog.setSize(340, 180);
		dialog.add(new JLabel(" "), BorderLayout.NORTH);
		dialog.add(queryPanel, BorderLayout.CENTER);
		dialog.add(btnPanel, BorderLayout.SOUTH);
		dialog.locationToCenterPosition();
		dialog.setVisible(true);
	}
	/*
	 * 删除数据操作日志
	 */
	public void onDeleteDataLog() {
		Pub_Templet_1VO templetVO = null;
		try {
			templetVO = UIUtil.getPub_Templet_1VO("PUB_DBTRIGGERLOG_CODE1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		templetVO.getItemVo("OPETYPE").setIsQuickQueryWrap(true);
		queryPanel = new BillQueryPanel(templetVO, false);

		queryPanel.setVisiable("OPEUSER", false);
		dialog = new BillDialog(menulog_panel);
		dialog.setLayout(new BorderLayout());
		JPanel btnPanel = new JPanel(new FlowLayout());
		WLTButton del_btn = new WLTButton("删除日志");
		del_btn.addActionListener(this);
		WLTButton exit_btn = new WLTButton("返回");
		exit_btn.addActionListener(this);
		btnPanel.add(del_btn);
		btnPanel.add(exit_btn);
		dialog.setTitle("删除日志");
		dialog.setSize(340, 180);
		dialog.add(new JLabel(" "), BorderLayout.NORTH);
		dialog.add(queryPanel, BorderLayout.CENTER);
		dialog.add(btnPanel, BorderLayout.SOUTH);
		dialog.locationToCenterPosition();
		dialog.setVisible(true);
	}
	
	/*
	 * 真正的数据删除逻辑！平台升级后省略了好多好多代码比科工。忒优化了！
	 */
	public void onCurrDelete() {
		if (queryPanel.getValueAt("opetime") == null || queryPanel.getValueAt("opetime").equals("")) {
			MessageBox.show(queryPanel, "请选择一个日期或者日期段");
			return;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String[] date = convertComp_dateTimeFormat(((RefItemVO) queryPanel.getValueAt("opetime")).getId()).split(";");
		System.out.println(queryPanel.getRealValueAt("opetime"));
		String begin = date[0];
		String end = date[1];
		String now = null;
		String less60 = null;
		try {
			now = UIUtil.getServerCurrDate();
			Calendar cal = Calendar.getInstance(); // Calendar.getInstance()
			// 默认是取当前时间
			cal.setTime(format.parse(now)); // 设置时间 ，你直接传你要算的那个Date对象就行
			cal.add(Calendar.DAY_OF_MONTH, -60);
			less60 = format.format(new Date(cal.getTime().getTime()));
			if (format.parse(less60).getTime() < format.parse(end).getTime()) {
				// 如果当前时间减去60天 小于选择的结束时间
				if (MessageBox.showConfirmDialog(dialog, "日志需要保留60天，目前结束时间为:" + less60 + "。\n\r您要继续删除操作吗？", "删除日志提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
					end = less60;
				} else {
					return;
				}
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		try {
			StringBuffer str_sql = new StringBuffer();
			if (currPage == 0) {
				// 执行 "菜单操作日志" 删除操作，这个方法应该记录日志的！给数据操作日志，且该日志不可删，目前没有做
				str_sql.append("delete from PUB_MENU_CLICKLOG where 1=1 ");
				HashMap map = new HashMap();
				map.put("opetime", "clicktime");
				String condition = queryPanel.getQuerySQLConditionByItemKeyMapping(map);
				str_sql.append(condition);
				if(end.trim().length() == 10) {
					str_sql.append(" and clicktime<='" + end +" 24:00:00'");
				}
				int num = UIUtil.executeUpdateByDS(null, str_sql.toString());
				if (num > 0) {
					MessageBox.show(dialog, "成功删除[" + begin + "]-[" + end + "]菜单操作日志，总计" + num + "条");
					dialog.dispose();
					menulog_panel.refreshData();
					return;
				} else {
					MessageBox.show(dialog, "没有符合条件的菜单操作日志，删除0条");
					return;
				}
			} else if (currPage == 1) {
				// 执行 "数据库日志" 删除操作
				String sql_delete_dbtriggerlog_b = null;
				str_sql.append("delete from pub_dbtriggerlog where 1=1  ");
				StringBuffer ids = new StringBuffer("select id from pub_dbtriggerlog where 1=1 ");
				String condition = queryPanel.getQuerySQLCondition();
				str_sql.append(condition);
				ids.append(condition);
				if(end.trim().length() == 10) {
					str_sql.append(" and opetime<='" + end +" 24:00:00'");
					ids.append(" and opetime<='" + end +" 24:00:00'");
				}
				sql_delete_dbtriggerlog_b = "delete from pub_dbtriggerlog_b where 1=1  and (itemname not like '%pwd')  and (triggerlog_id in (" + ids.toString() + "))";
				UIUtil.executeUpdateByDS(null, sql_delete_dbtriggerlog_b);
				int num = UIUtil.executeUpdateByDS(null, str_sql.toString());
				if (num > 0) {
					MessageBox.show(dialog, "成功删除[" + begin + "]-[" + end + "]数据操作日志，总计" + num + "条");
					dialog.dispose();
					datalog_panel.refreshData();
					datalog_b_panel.refreshData();
					return;
				} else {
					MessageBox.show(dialog, "没有符合条件的数据操作日志，删除0条");
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String convertComp_dateTimeFormat(String _time) {
		String begin = "";
		String end = "";
		if (_time == null || "".equals(_time)) {
			return null;
		} else if (_time.endsWith("年;")) { // 格式："2008年;"
			begin = _time.substring(0, 4) + "-01-01";
			end = _time.substring(0, 4) + "-12-31";
		} else if (_time.endsWith("季度;")) { // 格式："2008年1季度;"
			String quarter = _time.substring(5, 6);
			if (quarter.equals("1")) {
				begin = _time.substring(0, 4) + "-01-01";
				end = _time.substring(0, 4) + "-03-31";
			} else if (quarter.equals("2")) {
				begin = _time.substring(0, 4) + "-04-01";
				end = _time.substring(0, 4) + "-06-30";
			} else if (quarter.equals("3")) {
				begin = _time.substring(0, 4) + "-07-01";
				end = _time.substring(0, 4) + "-09-30";
			} else if (quarter.equals("4")) {
				begin = _time.substring(0, 4) + "-10-01";
				end = _time.substring(0, 4) + "-12-31";
			}
		} else if (_time.endsWith("月;")) { // 格式："2011年02月;"
			String month1 = _time.substring(5, 7);
			if (month1.equals("01") || month1.equals("03") || month1.equals("05") || month1.equals("07") || month1.equals("08") || month1.equals("10") || month1.equals("12")) {
				begin = _time.substring(0, 4) + "-" + month1 + "-01";
				end = _time.substring(0, 4) + "-" + month1 + "-31";
			} else if (month1.equals("02")) {
				int year = Integer.parseInt(_time.substring(0, 4));
				if ((year % 100 == 0) && (year * 400 == 0) || ((year % 100 != 0) && (year % 4 == 0))) {
					begin = _time.substring(0, 4) + "-" + month1 + "-01";
					end = _time.substring(0, 4) + "-" + month1 + "-29";
				} else {
					begin = _time.substring(0, 4) + "-" + month1 + "-01";
					end = _time.substring(0, 4) + "-" + month1 + "-28";
				}
			} else {
				begin = _time.substring(0, 4) + "-" + month1 + "-01";
				end = _time.substring(0, 4) + "-" + month1 + "-30";
			}
		} else if (_time.endsWith(";")) { // 格式为："2008-01-01;"
			begin = _time.substring(0, 10);
			end = begin;
		} else { // 格式为："2008-01-01;2008-02-01"
			return _time;
		}
		return begin + ";" + end + " 24:00:00";
	}
}
