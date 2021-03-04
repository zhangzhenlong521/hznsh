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
 * ���Կ��������ֹ���
 * @author Gwang
 * 2013-8-14 ����05:27:27
 */
public class AutoSetScoreWKPanel extends AbstractWorkPanel implements ActionListener {

	private WLTButton btnRandom, btnRandomByUser, btnSubmit;
	private WLTButton btnScoreDept, btnScoreDept_, btnScorePerson, btnScorePerson_, btnScorePerson_2, btn_postduty;
	private JTextField txtDate;
	private int rowCount = 1000; //���¼�¼��
	private int userCount = 20; //�����û���
	private static final Logger logger = WLTLogger.getLogger(AutoSetScoreWKPanel.class);

	/* (non-Javadoc)
	 * @see cn.com.infostrategy.ui.common.AbstractWorkPanel#initialize()
	 */
	@Override
	public void initialize() {
		JPanel panelMain = new JPanel(new FlowLayout());
		WLTLabel labInfo = new WLTLabel("�������ڣ�");
		panelMain.add(labInfo);

		txtDate = new JTextField();
		txtDate.setText(new SalaryUIUtil().getCheckDate());
		txtDate.setPreferredSize(new Dimension(100, 22));
		panelMain.add(txtDate);

		btnRandom = new WLTButton("������(" + rowCount + "��)");
		btnRandom.addActionListener(this); //ע�����¼�
		panelMain.add(btnRandom);

		btnRandomByUser = new WLTButton("������(" + userCount + "���û�)");
		btnRandomByUser.addActionListener(this); //ע�����¼�
		panelMain.add(btnRandomByUser);

		btnScoreDept = new WLTButton("���ſ���ȫ�����(����)");
		btnScoreDept.addActionListener(this); //ע�����¼�
		panelMain.add(btnScoreDept);

		btnScoreDept_ = new WLTButton("���ſ���ȫ�����(����)");
		btnScoreDept_.addActionListener(this); //ע�����¼�
		panelMain.add(btnScoreDept_);

		btnScorePerson = new WLTButton("Ա������ȫ�����(����)");
		btnScorePerson.addActionListener(this); //ע�����¼�
		panelMain.add(btnScorePerson);

		btnScorePerson_ = new WLTButton("Ա������ȫ�����(����)");
		btnScorePerson_.addActionListener(this); //ע�����¼�
		panelMain.add(btnScorePerson_);

		btnScorePerson_2 = new WLTButton("�߹ܶ���ָ��");
		btnScorePerson_2.addActionListener(this); //ע�����¼�
		panelMain.add(btnScorePerson_2);

		btn_postduty = new WLTButton("�������۴��");
		btn_postduty.addActionListener(this);
		panelMain.add(btn_postduty);

		btnSubmit = new WLTButton("�ύ���˽��");
		btnSubmit.addActionListener(this); //ע�����¼�
		panelMain.add(btnSubmit);
		Border border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "���Կ�������", TitledBorder.LEFT, TitledBorder.TOP, LookAndFeel.font); // �����߿�
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
			doSetScoreDept("���Ŷ���ָ��");
		} else if (e.getSource() == btnScoreDept_) {
			doSetScoreDept("���Ŷ���ָ��");
		} else if (e.getSource() == btnScorePerson) {
			doSetScorePerson("Ա������ָ��");
		} else if (e.getSource() == btnScorePerson_) {
			doSetScorePerson("Ա������ָ��");
		} else if (e.getSource() == btnScorePerson_2) {
			doSetScorePerson("�߹ܶ���ָ��");
		} else if (e.getSource() == btn_postduty) {
			doPostDutyScore();
		}
	}

	/**
	 * ִ��SQL
	 */
	private void executeSQLList(ArrayList<String> sqlList) {
		int n = sqlList.size();
		if (n == 0) {
			MessageBox.show("���µĴ����ȫ����ɣ�");
		} else {
			try {
				UIUtil.executeBatchByDS(null, sqlList);
			} catch (Exception e) {
				logger.error("������", e);
			}
			MessageBox.show("��ɴ��[" + n + "]��");
		}

		//		for (String sql : sqlList) {
		//			System.out.println(sql);
		//		}
	}

	/**
	 * �����֣������ݿ��¼��
	 * Ա��/����(sal_person_check_score, sal_dept_check_score)
	 */
	private void doRandomScore() {
		ArrayList<String> sqlList = getUpdatePersonSql(txtDate.getText());
		sqlList.addAll(getUpdateDeptSql(txtDate.getText()));
		this.executeSQLList(sqlList);
	}

	//Ա�����ֱ������ݿ��¼��
	private ArrayList<String> getUpdatePersonSql(String checkDate) {
		ArrayList<String> sqlList = new ArrayList<String>();
		String sqlSel = "select id from sal_person_check_score " + "where  targettype = 'Ա������ָ��' and checkdate = '" + checkDate + "' and checkscore is null and scoretype = '�ֶ����' limit " + rowCount / 2;
		String sqlUpdate = "";
		String[] idList = null;
		SecureRandom rnd = new SecureRandom();
		String rate = "";
		try {
			idList = UIUtil.getStringArrayFirstColByDS(null, sqlSel);
		} catch (Exception e) {
			logger.error("������", e);
		}
		for (String id : idList) {
			int n = rnd.nextInt(4) +7;
			if (n == 10) {
				rate = "1";
			} else {
				rate = "0." + Integer.toString(n);
			}

			sqlUpdate = "update sal_person_check_score set lasteditdate = '2012-10-06', status = '���ύ', " + "checkscore = 10*" + rate + " where id = " + id + ";";
			sqlList.add(sqlUpdate);
		}
		return sqlList;
	}

	//�������ֱ������ݿ��¼��
	private ArrayList<String> getUpdateDeptSql(String checkDate) {
		ArrayList<String> sqlList = new ArrayList<String>();
		String sqlSel = "select id from sal_dept_check_score " + "where checkdate = '" + checkDate + "' and targettype = '���Ŷ���ָ��' and checkscore is null limit " + rowCount / 2;
		String sqlUpdate = "";
		String[] idList = null;
		SecureRandom rnd = new SecureRandom();
		String rate = "";
		try {
			idList = UIUtil.getStringArrayFirstColByDS(null, sqlSel);
		} catch (Exception e) {
			logger.error("������", e);
		}
		for (String id : idList) {
			int n = rnd.nextInt(4) + 7;
			rate = Integer.toString(n * 10);

			sqlUpdate = "update sal_dept_check_score set lasteditdate = '2012-10-06', status = '���ύ', " + "checkdratio = " + rate + ", checkscore = (100-" + rate + ")/100*weights where id = " + id + ";";
			sqlList.add(sqlUpdate);
		}
		return sqlList;
	}

	/**
	 * �����֣����û���
	 * Ա��/����(sal_person_check_score, sal_dept_check_score)
	 */
	private void doRandomScoreByUser() {
		// TODO Auto-generated method stub
		ArrayList<String> sqlList = getUpdatePersonSqlByUser(txtDate.getText());
		sqlList.addAll(getUpdateDeptSql(txtDate.getText()));
		this.executeSQLList(sqlList);
	}

	//Ա�����ֱ����û���
	private ArrayList<String> getUpdatePersonSqlByUser(String checkDate) {
		ArrayList<String> sqlList = new ArrayList<String>();

		//�õ�û�����ɵ��û�
		String sqlSel = "select distinct(scoreuser) from sal_person_check_score " + "where checkdate = '" + checkDate + "' and checkscore is null and scoretype = '�ֶ����' limit " + userCount / 2;
		String userInStr = "";
		try {
			userInStr = UIUtil.getInCondition(null, sqlSel);
		} catch (Exception e) {
			logger.error("������", e);
		}

		//�õ���¼��ID
		sqlSel = "select id from sal_person_check_score " + "where targettype = 'Ա������ָ��' and checkdate = '" + checkDate + "' and checkscore is null and scoretype = '�ֶ����' and scoreuser in (" + userInStr + ")";
		String sqlUpdate = "";
		String[] idList = null;
		SecureRandom rnd = new SecureRandom();
		String rate = "";
		try {
			idList = UIUtil.getStringArrayFirstColByDS(null, sqlSel);
		} catch (Exception e) {
			logger.error("������", e);
		}
		for (String id : idList) {
			int n = rnd.nextInt(4) + 7;
			if (n == 10) {
				rate = "1";
			} else {
				rate = "0." + Integer.toString(n);
			}

			sqlUpdate = "update sal_person_check_score set lasteditdate = '2012-10-06', status = '���ύ', " + "checkscore = 10*" + rate + " where id = " + id + ";";
			sqlList.add(sqlUpdate);
		}
		return sqlList;
	}

	/*//�������ֱ����û���
	private ArrayList<String> getUpdateDeptSqlByUser(String checkDate) {
		ArrayList<String> sqlList = new ArrayList<String>();

		//�õ�û�����ɵ��û�
		String sqlSel = "select distinct(scoreuser) from sal_dept_check_score " + "where checkdate = '" + checkDate + "' and targettype = '���Ŷ���ָ��' and checkscore is null limit " + userCount / 2;
		String userInStr = "";
		try {
			userInStr = UIUtil.getInCondition(null, sqlSel);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//�õ���¼��ID
		sqlSel = "select id from sal_dept_check_score " + "where checkdate = '" + checkDate + "' and targettype = '���Ŷ���ָ��' and checkscore is null scoreuser in (" + userInStr + ")";
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

			sqlUpdate = "update sal_dept_check_score set lasteditdate = '2012-10-06', status = '���ύ', " + "checkdratio = " + rate + ", checkscore = (100-" + rate + ")/100*weights where id = " + id + ";";
			sqlList.add(sqlUpdate);
		}
		return sqlList;
	}*/

	/**
	 * �ύ��ȫ��������ϵ��û��Ĵ�ֽ��
	 */
	private void doSubmitResult() {
		// TODO Auto-generated method stub
		String sqlPerson = "update sal_person_check_score set status = '���ύ' where checkscore is not null";
		String sqlDept = "update sal_dept_check_score set status = '���ύ' where checkscore is not null";
//		String sqlduty = "update "
		try {
			UIUtil.executeBatchByDS(null, new String[] { sqlPerson, sqlDept });
			MessageBox.show("�ύ�ɹ�!");
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
			logger.error("������", e);
		}

		for (String id : idList) {
			int n = rnd.nextInt(2) + 1;
			rate = Integer.toString(n * 10);
			sqlUpdate = "update sal_dept_check_score set lasteditdate = '2012-10-06', status = '���ύ', " + "checkdratio = " + rate + ", checkscore = (100-" + rate + ")/100*weights where id = " + id;
			sqlList.add(sqlUpdate);
		}
		this.executeSQLList(sqlList);

	}

	/**
	 * Ա�����ֱ�, һ�δ���ȫ��
	 */
	private void doSetScorePerson(String type) {
		String checkDate = txtDate.getText();
		ArrayList<String> sqlList = new ArrayList<String>();
		String sqlSel = "select id from sal_person_check_score " + "where  targettype = '" + type + "' and checkdate = '" + checkDate + "' and checkscore is null and scoretype = '�ֶ����'";
		String sqlUpdate = "";
		String[] idList = null;
		SecureRandom rnd = new SecureRandom();
		String rate = "";
		try {
			idList = UIUtil.getStringArrayFirstColByDS(null, sqlSel);
		} catch (Exception e) {
			logger.error("������", e);
		}
		for (String id : idList) {
			int n = rnd.nextInt(4) + 7;
			sqlUpdate = "update sal_person_check_score set lasteditdate = '2012-10-06', status = '���ύ', " + "checkscore = " + n  + " where id = " + id;
			sqlList.add(sqlUpdate);
		}
		this.executeSQLList(sqlList);
	}

	private void doPostDutyScore() {
		ArrayList<String> sqlList = new ArrayList<String>();
		String checkDate = txtDate.getText();
		String sqlSel = "select id from sal_person_postduty_score " + " where   checkdate = '" + checkDate + "' and checkscore is null and scoretype = '�ֶ����'";
		String sqlUpdate = "";
		String[] idList = null;
		SecureRandom rnd = new SecureRandom();
		try {
			idList = UIUtil.getStringArrayFirstColByDS(null, sqlSel);
		} catch (Exception e) {
			logger.error("������", e);
		}
		for (String id : idList) {
			int n = rnd.nextInt(4) + 7;
			sqlUpdate = "update sal_person_postduty_score set  status = '���ύ', " + " checkscore ='" + n + "'  where id = " + id;
			sqlList.add(sqlUpdate);
		}
		this.executeSQLList(sqlList);
	}
}
