package cn.com.pushworld.salary.ui.tools;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.SecureRandom;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

/**
 * 定性考核随机打分工具
 * @author Gwang
 * 2013-8-14 下午05:27:27
 */
public class AutoSetScoreWKPanel extends AbstractWorkPanel implements ActionListener {

	private WLTButton btnRandom, btnRandomByUser, btnSubmit;
	private WLTButton btnScoreDept, btnScoreDept_, btnScorePerson, btnScorePerson_, btnScorePerson_2, btn_postduty;
	private JTextField txtDate;
	private int rowCount = 1000; //更新记录数
	private int userCount = 20; //更新用户数
	private static final Logger logger = WLTLogger.getLogger(AutoSetScoreWKPanel.class);

	/* (non-Javadoc)
	 * @see cn.com.infostrategy.ui.common.AbstractWorkPanel#initialize()
	 */
	@Override
	public void initialize() {
		JPanel panelMain = new JPanel(new FlowLayout());
		WLTLabel labInfo = new WLTLabel("考核周期：");
		panelMain.add(labInfo);

		txtDate = new JTextField();
		txtDate.setText(new SalaryUIUtil().getCheckDate());
		txtDate.setPreferredSize(new Dimension(100, 22));
		panelMain.add(txtDate);

		btnRandom = new WLTButton("随机打分(" + rowCount + "条)");
		btnRandom.addActionListener(this); //注册点击事件
		panelMain.add(btnRandom);

		btnRandomByUser = new WLTButton("随机打分(" + userCount + "个用户)");
		btnRandomByUser.addActionListener(this); //注册点击事件
		panelMain.add(btnRandomByUser);

		btnScoreDept = new WLTButton("部门考核全部打分(定性)");
		btnScoreDept.addActionListener(this); //注册点击事件
		panelMain.add(btnScoreDept);

		btnScoreDept_ = new WLTButton("部门考核全部打分(定量)");
		btnScoreDept_.addActionListener(this); //注册点击事件
		panelMain.add(btnScoreDept_);

		btnScorePerson = new WLTButton("员工考核全部打分(定性)");
		btnScorePerson.addActionListener(this); //注册点击事件
		panelMain.add(btnScorePerson);

		btnScorePerson_ = new WLTButton("员工考核全部打分(定量)");
		btnScorePerson_.addActionListener(this); //注册点击事件
		panelMain.add(btnScorePerson_);

		btnScorePerson_2 = new WLTButton("高管定性指标");
		btnScorePerson_2.addActionListener(this); //注册点击事件
		panelMain.add(btnScorePerson_2);

		btn_postduty = new WLTButton("岗责评价打分");
		btn_postduty.addActionListener(this);
		panelMain.add(btn_postduty);

		btnSubmit = new WLTButton("提交考核结果");
		btnSubmit.addActionListener(this); //注册点击事件
		panelMain.add(btnSubmit);
		Border border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "定性考核评分", TitledBorder.LEFT, TitledBorder.TOP, LookAndFeel.font); // 创建边框
		panelMain.setBorder(border);
		this.add(panelMain);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == btnRandom) {
			doRandomScore();
		} else if (e.getSource() == btnRandomByUser) {
			doRandomScoreByUser();
		} else if (e.getSource() == btnSubmit) {
			doSubmitResult();
		} else if (e.getSource() == btnScoreDept) {
			doSetScoreDept("部门定性指标");
		} else if (e.getSource() == btnScoreDept_) {
			doSetScoreDept("部门定量指标");
		} else if (e.getSource() == btnScorePerson) {
			doSetScorePerson("员工定性指标");
		} else if (e.getSource() == btnScorePerson_) {
			doSetScorePerson("员工定量指标");
		} else if (e.getSource() == btnScorePerson_2) {
			doSetScorePerson("高管定性指标");
		} else if (e.getSource() == btn_postduty) {
			doPostDutyScore();
		}
	}

	/**
	 * 执行SQL
	 */
	private void executeSQLList(ArrayList<String> sqlList) {
		int n = sqlList.size();
		if (n == 0) {
			MessageBox.show("本月的打分已全部完成！");
		} else {
			try {
				UIUtil.executeBatchByDS(null, sqlList);
			} catch (Exception e) {
				logger.error("出错了", e);
			}
			MessageBox.show("完成打分[" + n + "]条");
		}

		//		for (String sql : sqlList) {
		//			System.out.println(sql);
		//		}
	}

	/**
	 * 随机打分（按数据库记录）
	 * 员工/部门(sal_person_check_score, sal_dept_check_score)
	 */
	private void doRandomScore() {
		ArrayList<String> sqlList = getUpdatePersonSql(txtDate.getText());
		sqlList.addAll(getUpdateDeptSql(txtDate.getText()));
		this.executeSQLList(sqlList);
	}

	//员工评分表（按数据库记录）
	private ArrayList<String> getUpdatePersonSql(String checkDate) {
		ArrayList<String> sqlList = new ArrayList<String>();
		String sqlSel = "select id from sal_person_check_score " + "where  targettype = '员工定性指标' and checkdate = '" + checkDate + "' and checkscore is null and scoretype = '手动打分' limit " + rowCount / 2;
		String sqlUpdate = "";
		String[] idList = null;
		SecureRandom rnd = new SecureRandom();
		String rate = "";
		try {
			idList = UIUtil.getStringArrayFirstColByDS(null, sqlSel);
		} catch (Exception e) {
			logger.error("出错了", e);
		}
		for (String id : idList) {
			int n = rnd.nextInt(4) +7;
			if (n == 10) {
				rate = "1";
			} else {
				rate = "0." + Integer.toString(n);
			}

			sqlUpdate = "update sal_person_check_score set lasteditdate = '2012-10-06', status = '待提交', " + "checkscore = 10*" + rate + " where id = " + id + ";";
			sqlList.add(sqlUpdate);
		}
		return sqlList;
	}

	//部门评分表（按数据库记录）
	private ArrayList<String> getUpdateDeptSql(String checkDate) {
		ArrayList<String> sqlList = new ArrayList<String>();
		String sqlSel = "select id from sal_dept_check_score " + "where checkdate = '" + checkDate + "' and targettype = '部门定性指标' and checkscore is null limit " + rowCount / 2;
		String sqlUpdate = "";
		String[] idList = null;
		SecureRandom rnd = new SecureRandom();
		String rate = "";
		try {
			idList = UIUtil.getStringArrayFirstColByDS(null, sqlSel);
		} catch (Exception e) {
			logger.error("出错了", e);
		}
		for (String id : idList) {
			int n = rnd.nextInt(4) + 7;
			rate = Integer.toString(n * 10);

			sqlUpdate = "update sal_dept_check_score set lasteditdate = '2012-10-06', status = '待提交', " + "checkdratio = " + rate + ", checkscore = (100-" + rate + ")/100*weights where id = " + id + ";";
			sqlList.add(sqlUpdate);
		}
		return sqlList;
	}

	/**
	 * 随机打分（按用户）
	 * 员工/部门(sal_person_check_score, sal_dept_check_score)
	 */
	private void doRandomScoreByUser() {
		// TODO Auto-generated method stub
		ArrayList<String> sqlList = getUpdatePersonSqlByUser(txtDate.getText());
		sqlList.addAll(getUpdateDeptSql(txtDate.getText()));
		this.executeSQLList(sqlList);
	}

	//员工评分表（按用户）
	private ArrayList<String> getUpdatePersonSqlByUser(String checkDate) {
		ArrayList<String> sqlList = new ArrayList<String>();

		//得到没打分完成的用户
		String sqlSel = "select distinct(scoreuser) from sal_person_check_score " + "where checkdate = '" + checkDate + "' and checkscore is null and scoretype = '手动打分' limit " + userCount / 2;
		String userInStr = "";
		try {
			userInStr = UIUtil.getInCondition(null, sqlSel);
		} catch (Exception e) {
			logger.error("出错了", e);
		}

		//得到记录行ID
		sqlSel = "select id from sal_person_check_score " + "where targettype = '员工定性指标' and checkdate = '" + checkDate + "' and checkscore is null and scoretype = '手动打分' and scoreuser in (" + userInStr + ")";
		String sqlUpdate = "";
		String[] idList = null;
		SecureRandom rnd = new SecureRandom();
		String rate = "";
		try {
			idList = UIUtil.getStringArrayFirstColByDS(null, sqlSel);
		} catch (Exception e) {
			logger.error("出错了", e);
		}
		for (String id : idList) {
			int n = rnd.nextInt(4) + 7;
			if (n == 10) {
				rate = "1";
			} else {
				rate = "0." + Integer.toString(n);
			}

			sqlUpdate = "update sal_person_check_score set lasteditdate = '2012-10-06', status = '待提交', " + "checkscore = 10*" + rate + " where id = " + id + ";";
			sqlList.add(sqlUpdate);
		}
		return sqlList;
	}

	/*//部门评分表（按用户）
	private ArrayList<String> getUpdateDeptSqlByUser(String checkDate) {
		ArrayList<String> sqlList = new ArrayList<String>();

		//得到没打分完成的用户
		String sqlSel = "select distinct(scoreuser) from sal_dept_check_score " + "where checkdate = '" + checkDate + "' and targettype = '部门定性指标' and checkscore is null limit " + userCount / 2;
		String userInStr = "";
		try {
			userInStr = UIUtil.getInCondition(null, sqlSel);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//得到记录行ID
		sqlSel = "select id from sal_dept_check_score " + "where checkdate = '" + checkDate + "' and targettype = '部门定性指标' and checkscore is null scoreuser in (" + userInStr + ")";
		String sqlUpdate = "";
		String[] idList = null;
		SecureRandom rnd = new SecureRandom();
		String rate = "";
		try {
			idList = UIUtil.getStringArrayFirstColByDS(null, sqlSel);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (String id : idList) {
			int n = rnd.nextInt(10) + 1;
			rate = Integer.toString(n * 10);

			sqlUpdate = "update sal_dept_check_score set lasteditdate = '2012-10-06', status = '待提交', " + "checkdratio = " + rate + ", checkscore = (100-" + rate + ")/100*weights where id = " + id + ";";
			sqlList.add(sqlUpdate);
		}
		return sqlList;
	}*/

	/**
	 * 提交已全部评分完毕的用户的打分结果
	 */
	private void doSubmitResult() {
		// TODO Auto-generated method stub
		String sqlPerson = "update sal_person_check_score set status = '已提交' where checkscore is not null";
		String sqlDept = "update sal_dept_check_score set status = '已提交' where checkscore is not null";
//		String sqlduty = "update "
		try {
			UIUtil.executeBatchByDS(null, new String[] { sqlPerson, sqlDept });
			MessageBox.show("提交成功!");
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}

	}

	/**
	 * 
	 */
	private void doSetScoreDept(String type) {
		String checkDate = txtDate.getText();
		ArrayList<String> sqlList = new ArrayList<String>();
		String sqlSel = "select id from sal_dept_check_score " + "where checkdate = '" + checkDate + "' and targettype = '" + type + "' and checkscore is null";
		String sqlUpdate = "";
		String[] idList = null;
		SecureRandom rnd = new SecureRandom();
		String rate = "";
		try {
			idList = UIUtil.getStringArrayFirstColByDS(null, sqlSel);
		} catch (Exception e) {
			logger.error("出错了", e);
		}

		for (String id : idList) {
			int n = rnd.nextInt(2) + 1;
			rate = Integer.toString(n * 10);
			sqlUpdate = "update sal_dept_check_score set lasteditdate = '2012-10-06', status = '待提交', " + "checkdratio = " + rate + ", checkscore = (100-" + rate + ")/100*weights where id = " + id;
			sqlList.add(sqlUpdate);
		}
		this.executeSQLList(sqlList);

	}

	/**
	 * 员工评分表, 一次打完全部
	 */
	private void doSetScorePerson(String type) {
		String checkDate = txtDate.getText();
		ArrayList<String> sqlList = new ArrayList<String>();
		String sqlSel = "select id from sal_person_check_score " + "where  targettype = '" + type + "' and checkdate = '" + checkDate + "' and checkscore is null and scoretype = '手动打分'";
		String sqlUpdate = "";
		String[] idList = null;
		SecureRandom rnd = new SecureRandom();
		String rate = "";
		try {
			idList = UIUtil.getStringArrayFirstColByDS(null, sqlSel);
		} catch (Exception e) {
			logger.error("出错了", e);
		}
		for (String id : idList) {
			int n = rnd.nextInt(4) + 7;
			sqlUpdate = "update sal_person_check_score set lasteditdate = '2012-10-06', status = '待提交', " + "checkscore = " + n  + " where id = " + id;
			sqlList.add(sqlUpdate);
		}
		this.executeSQLList(sqlList);
	}

	private void doPostDutyScore() {
		ArrayList<String> sqlList = new ArrayList<String>();
		String checkDate = txtDate.getText();
		String sqlSel = "select id from sal_person_postduty_score " + " where   checkdate = '" + checkDate + "' and checkscore is null and scoretype = '手动打分'";
		String sqlUpdate = "";
		String[] idList = null;
		SecureRandom rnd = new SecureRandom();
		try {
			idList = UIUtil.getStringArrayFirstColByDS(null, sqlSel);
		} catch (Exception e) {
			logger.error("出错了", e);
		}
		for (String id : idList) {
			int n = rnd.nextInt(4) + 7;
			sqlUpdate = "update sal_person_postduty_score set  status = '已提交', " + " checkscore ='" + n + "'  where id = " + id;
			sqlList.add(sqlUpdate);
		}
		this.executeSQLList(sqlList);
	}
}
