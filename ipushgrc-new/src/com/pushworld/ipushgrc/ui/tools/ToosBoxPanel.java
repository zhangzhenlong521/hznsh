package com.pushworld.ipushgrc.ui.tools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.report.cellcompent.ExcelUtil;

import cn.com.pushworld.wn.ui.WnSalaryServiceIfc;
import com.pushworld.ipushgrc.ui.IPushGRCServiceIfc;
import com.pushworld.ipushgrc.ui.tools.impexcel.InputInfoPanel;

/**
 * ������
 * @author hm
 *
 */
public class ToosBoxPanel extends AbstractWorkPanel implements ActionListener {
	WLTButton excel_btn = new WLTButton("Excel����");
	WLTButton import_dept_post = new WLTButton("�������λ"); //һ���յ�ϵͳ����������͸�λ��
	WLTButton importUserAndRole = new WLTButton("��Ա�ͽ�ɫ");
	WLTButton importDeptDuty = new WLTButton("��λְ�����");
	WLTButton dataexport = new WLTButton("����Ǩ��");
	WLTButton update_201210 = new WLTButton("10���Զ�����");
	String date = UIUtil.getCurrDate();
	WLTButton inportExcel_btn = new WLTButton("<html>Υ����ֱ�׼����<br>��������</html>");
	WLTButton update_2013_5 = new WLTButton("Υ���������"); //Υ����ּ����׼�۷ֹ���.
	WLTButton inportWGJF_btn = new WLTButton("<html>Υ����ֱ�׼����<br>��������</html>");
	WLTButton compare_sysuser_exceluser = new WLTButton("�Ƚ�ϵͳ������Ա");
	WLTButton user_roles_update = new WLTButton("������������ɫ");
	WLTButton msg = new WLTButton("���Ų���");
	WLTButton problem = new WLTButton("�����������");//zzl[2016/8/31]
	WLTButton hfwg_btn = new WLTButton("���񻮷�");//zzl[2016/8/31]
	WLTButton wg_Update_Btn = new WLTButton("����������ϴ");//zzl[2016/8/31]
	private String str = null;
	public void initialize() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		excel_btn.setPreferredSize(new Dimension(100, 50));
		import_dept_post.setPreferredSize(new Dimension(100, 50));
		importUserAndRole.setPreferredSize(new Dimension(100, 50));
		importDeptDuty.setPreferredSize(new Dimension(100, 50));
		dataexport.setPreferredSize(new Dimension(100, 50));
		update_201210.setPreferredSize(new Dimension(100, 50));
		inportExcel_btn.setPreferredSize(new Dimension(145, 50));
		inportWGJF_btn.setPreferredSize(new Dimension(145, 50));
		hfwg_btn.setPreferredSize(new Dimension(145, 50));
		msg.setPreferredSize(new Dimension(100, 50));
		problem.setPreferredSize(new Dimension(100, 50));
		wg_Update_Btn.setPreferredSize(new Dimension(100, 50));
		this.add(excel_btn);
		this.add(import_dept_post);
		this.add(importUserAndRole);
		this.add(importDeptDuty);
		this.add(problem);
		this.add(hfwg_btn);
		this.add(wg_Update_Btn);
		if (ClientEnvironment.getInstance().getLoginModel() == ClientEnvironment.LOGINMODEL_ADMIN) {
			this.add(dataexport);
		}
		try {
			String version = UIUtil.getStringValueByDS(null, "select version from db_ver where version='2012-10-30'");
			if (version == null || version.equals("")) {
				//				this.add(update_201210);
				update_201210.addActionListener(this);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (ClientEnvironment.isAdmin() && ClientEnvironment.getCurrLoginUserVO().getCode().equalsIgnoreCase("admin")) {
			this.add(inportExcel_btn);
			this.add(inportWGJF_btn);
			inportWGJF_btn.addActionListener(this);
		}
		update_2013_5.setPreferredSize(new Dimension(100, 50));
		try {
			String version = UIUtil.getStringValueByDS(null, "select version from db_ver where version='2013-06-03'");
			if (version == null || version.equals("")) {
				//				this.add(update_2013_5);
				update_2013_5.addActionListener(this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.add(compare_sysuser_exceluser);
		compare_sysuser_exceluser.setPreferredSize(new Dimension(150, 50));
		compare_sysuser_exceluser.addActionListener(this);

		this.add(user_roles_update);
		this.add(msg);
		user_roles_update.setPreferredSize(new Dimension(150, 50));
		user_roles_update.addActionListener(this);
		import_dept_post.addActionListener(this);
		inportExcel_btn.addActionListener(this);
		dataexport.addActionListener(this);
		excel_btn.addActionListener(this);
		importUserAndRole.addActionListener(this);
		importDeptDuty.addActionListener(this);
		msg.addActionListener(this);
		problem.addActionListener(this);
		hfwg_btn.addActionListener(this);
		wg_Update_Btn.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == excel_btn) {
			showImportExcelDialog(); //����Excel
		} else if (e.getSource() == importUserAndRole) { //���߻�������λ����λ ������Ա�����Ľ�ɫ
			onImportUserAndRole();
			//			updateUser();
		} else if (e.getSource() == importDeptDuty) {
			onImportDeptDuty();
			//			copyDuty();
		} else if (e.getSource() == dataexport) {
			onExportData();
		} else if (e.getSource() == update_201210) { //2012-10-30��������
			onUpdate();
		} else if (e.getSource() == inportExcel_btn) {
			importExcelDate();
		} else if (e.getSource() == update_2013_5) {
			onUpdate2013_5_score();
		} else if (compare_sysuser_exceluser == e.getSource()) {
			try {
				onCompare();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (user_roles_update == e.getSource()) {
			onUserRoleUpdate();
		} else if (import_dept_post == e.getSource()) {
			try {
				onImportDeptAndPost();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else if (msg == e.getSource()) {
			String telno = JOptionPane.showInputDialog(this, "�������ֻ���", "13611086523");
			if (tbutil.isEmpty(telno)) {
				return;
			}
			String msg = JOptionPane.showInputDialog(this, "����������", "������Ϣ");
			IPushGRCServiceIfc ifc;
			try {
				if (!tbutil.isEmpty(telno) && !tbutil.isEmpty(msg)) {
					ifc = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
					//					ifc.sendSMS(telno, msg, "����ʹ��");
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		} else if (e.getSource() == inportWGJF_btn) {
			onIportWGJFStand(); //����Υ����ֱ�׼.
		}
		else if(e.getSource() == problem){
			onIportProblem();
		}else if(e.getSource()==hfwg_btn){
			onHfWg();
		}else if(e.getSource()==wg_Update_Btn){
			onWgUpdate();
		}
		
	}

	private void onWgUpdate() {
		try{
			final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
					.lookUpRemoteService(WnSalaryServiceIfc.class);
			new SplashWindow(this, new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent event) {
					str = service.onWgUpdate();
				}
			});
			MessageBox.show(this,str);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * zzl
	 * ���񻮷�
	 * select count(*),floor(count(*)/10000) from s_loan_khxx_202001
	 *
	 * SELECT G,H  FROM (SELECT ROWNUM AS rowno, t.* FROM s_loan_khxx_202001 t WHERE ROWNUM <= 810000) table_alias WHERE table_alias.rowno >=800000;
	 */
	private void onHfWg() {
		try{
			final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
					.lookUpRemoteService(WnSalaryServiceIfc.class);
			new SplashWindow(this, new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent event) {
					str = service.getHfWg();
				}
			});
			MessageBox.show(this,str);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
 * zzl
 * �����������  
 */
	private void onIportProblem() {
		JFileChooser chooser = new JFileChooser();
		int flag = chooser.showOpenDialog(this);
		if (flag == 1) {
			return;
		}
		File file = chooser.getSelectedFile();
		if (file == null) {
			return;
		}
		ExcelUtil util = new ExcelUtil();
		final String data[][] = util.getExcelFileData(file.getAbsolutePath());
		new SplashWindow(this, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
		try {
			String countid=null;
			String countid2=null;
			String countid3=null;
		for(int i=0;i<data.length;i++){			
			String countID=UIUtil.getStringValueByDS(null,"select id from ck_project_list where listname='"+data[i][0]+"'");
			InsertSQLBuilder insert=new InsertSQLBuilder("ck_project_list");
			if(countID==null){
				String listid = UIUtil.getSequenceNextValByDS(null, "S_ck_project_list");
				insert.putFieldValue("id", listid);
				insert.putFieldValue("listname", data[i][0]);
				insert.putFieldValue("leveldesc", "һ��Ŀ¼");
				insert.putFieldValue("firstid", listid);
				UIUtil.executeUpdateByDS(null,insert.getSQL());
				countid=listid;
			}else{
				countid=countID;
			}
			String countID2=UIUtil.getStringValueByDS(null,"select id from ck_project_list where listname='"+data[i][1]+"'");
			String listid2=null;
			if(countID2==null){
				listid2 = UIUtil.getSequenceNextValByDS(null, "S_ck_project_list");
				insert.putFieldValue("id", listid2);
				insert.putFieldValue("listname", data[i][1]);
				insert.putFieldValue("parentid", countid);
				insert.putFieldValue("leveldesc", "����Ŀ¼");
				insert.putFieldValue("firstid", countid);
				insert.putFieldValue("secondid", listid2);
				UIUtil.executeUpdateByDS(null,insert.getSQL());
				countid2=listid2;
			}else{
			    HashVO [] parentid=UIUtil.getHashVoArrayByDS(null,"select * from ck_problem_dict where firstname='"+data[i][0]+"' and secondname='"+data[i][1]+"'");
			    if(parentid.length<=0){
			    	listid2 = UIUtil.getSequenceNextValByDS(null, "S_ck_project_list");
					insert.putFieldValue("id", listid2);
					insert.putFieldValue("listname", data[i][1]);
					insert.putFieldValue("parentid", countid);
					insert.putFieldValue("leveldesc", "����Ŀ¼");
					insert.putFieldValue("firstid",countid);
					insert.putFieldValue("secondid", listid2);
					UIUtil.executeUpdateByDS(null,insert.getSQL());
					countid2=listid2;
			    }else{
			    	countid2=countID2;
			    }
			}
			String countID3=UIUtil.getStringValueByDS(null,"select id from ck_project_list where listname='"+data[i][2]+"'");
			if(countID3==null){
				String listid3 = UIUtil.getSequenceNextValByDS(null, "S_ck_project_list");
				insert.putFieldValue("id", listid3);
				insert.putFieldValue("listname", data[i][2]);
				insert.putFieldValue("parentid", countid2);
				insert.putFieldValue("leveldesc", "����Ŀ¼");
				insert.putFieldValue("firstid",countid);
				insert.putFieldValue("secondid",countid2);
				insert.putFieldValue("thirdid", listid3);
				UIUtil.executeUpdateByDS(null,insert.getSQL());
				countid3=listid3;
			}else{
			    HashVO [] parentid=UIUtil.getHashVoArrayByDS(null,"select * from ck_problem_dict where firstname='"+data[i][0]+"' and secondname='"+data[i][1]+"' and thirdname='"+data[i][2]+"'");
			    if(parentid.length<=0){
			    	String listid3 = UIUtil.getSequenceNextValByDS(null, "S_ck_project_list");					insert.putFieldValue("id", listid3);
					insert.putFieldValue("listname", data[i][2]);
					insert.putFieldValue("parentid",countid2);
					insert.putFieldValue("leveldesc", "����Ŀ¼");
					insert.putFieldValue("firstid",countid);
					insert.putFieldValue("secondid",countid2);
					insert.putFieldValue("thirdid", listid3);
					UIUtil.executeUpdateByDS(null,insert.getSQL());
					countid3=listid3;
			    }else{
			    	countid3=countID3;
			    }

			}
			InsertSQLBuilder insert_dict=new InsertSQLBuilder("ck_problem_dict");
			String dictID= UIUtil.getSequenceNextValByDS(null, "S_ck_problem_dict");
			String count=UIUtil.getStringValueByDS(null,"select id from ck_problem_dict where firstname='"+data[i][0]+"' and secondname='"+data[i][1]+"' and thirdname='"+data[i][2]+"' and dictname='"+data[i][3]+"' and checkmode='"+data[i][4]+"'");
			if(count==null){
				insert_dict.putFieldValue("id", dictID);
				insert_dict.putFieldValue("dictname", data[i][3]);
				insert_dict.putFieldValue("parentid", countid3);
				insert_dict.putFieldValue("firstname", data[i][0]);
				insert_dict.putFieldValue("secondname", data[i][1]);
				insert_dict.putFieldValue("thirdname", data[i][2]);   
				insert_dict.putFieldValue("firstid", countid);
				insert_dict.putFieldValue("secondid", countid2);
				insert_dict.putFieldValue("thirdid", countid3);
				insert_dict.putFieldValue("checkmode", data[i][4]);
				UIUtil.executeUpdateByDS(null,insert_dict.getSQL());
			}else{
				UpdateSQLBuilder update=new UpdateSQLBuilder("ck_problem_dict");
				update.setWhereCondition("id="+count);
				update.putFieldValue("dictname", data[i][3]);
				update.putFieldValue("parentid", countid3);
				update.putFieldValue("firstname", data[i][0]);
				update.putFieldValue("secondname", data[i][1]);
				update.putFieldValue("thirdname", data[i][2]);   
				update.putFieldValue("firstid", countid);
				update.putFieldValue("secondid", countid2);
				update.putFieldValue("thirdid", countid3);
				update.putFieldValue("checkmode", data[i][4]);
				UIUtil.executeUpdateByDS(null,update.getSQL());
			}
		}
		} catch (Exception a) {
			a.printStackTrace();
		}
			}
		}, 366, 366);
		MessageBox.show(this,"����ɹ�");
	}

	private void onIportWGJFStand() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("��ѡ���ļ�Ŀ¼");
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int flag = chooser.showSaveDialog(this);
		if (flag == 1 || chooser.getSelectedFile() == null) {
			return;
		}
		String str_path = chooser.getSelectedFile().getAbsolutePath();
		if (str_path == null) {
			return;
		}

		String[][] excelFileData = new ExcelUtil().getExcelFileData(str_path, 0);
		List sqllist = new ArrayList();
		String parentid = "";
		String secondid = "";
		int firstseq = 0;
		int secondseq = 0;
		int seq = 0;
		try {
			for (int i = 0; i < excelFileData.length; i++) {
				String code = excelFileData[i][0];
				String content = excelFileData[i][1];
				String score = excelFileData[i][2];
				String value = "";
				int havevalueindex = -1;
				int havevaluecolssum = 0;
				if (!tbutil.isEmpty(code) && !code.contains("-")) {
					value = code;
					havevalueindex = 0;
					havevaluecolssum = 1;
				}
				if ((tbutil.isEmpty(code) && !tbutil.isEmpty(content)) || (!tbutil.isEmpty(content) && content.contains("-") || (!tbutil.isEmpty(code) && code.contains("-")))) {
					if (havevalueindex == -1) {
						havevalueindex = 1;
						havevaluecolssum = 1;
					} else {
						havevaluecolssum = 1;
					}
				}
				if (!tbutil.isEmpty(code) && !tbutil.isEmpty(content)) {
					havevaluecolssum = 3;
				}
				if (havevalueindex == 0 && havevaluecolssum == 1) {
					InsertSQLBuilder insert = new InsertSQLBuilder("SCORE_TYPE");
					String scoretypeid = UIUtil.getSequenceNextValByDS(null, "S_SCORE_TYPE");
					parentid = scoretypeid;
					insert.putFieldValue("id", scoretypeid);
					insert.putFieldValue("typename", code);
					firstseq++;
					insert.putFieldValue("typecode", firstseq + "");
					insert.putFieldValue("seq", firstseq + "");
					secondseq = 0;
					sqllist.add(insert);
				} else if (havevalueindex == 1 && havevaluecolssum == 1) {
					InsertSQLBuilder insert = new InsertSQLBuilder("SCORE_TYPE");
					String scoretypeid = UIUtil.getSequenceNextValByDS(null, "S_SCORE_TYPE");
					secondid = scoretypeid;
					insert.putFieldValue("id", scoretypeid);
					String contentname = content;
					if (tbutil.isEmpty(contentname)) {
						contentname = code;
					}
					insert.putFieldValue("typename", contentname);
					String splits[] = contentname.split("��");
					secondseq++;
					insert.putFieldValue("typecode", splits[0] + "");
					insert.putFieldValue("seq", secondseq + "");
					insert.putFieldValue("parentid", parentid + "");
					sqllist.add(insert);
				} else if (havevalueindex == 0 && havevaluecolssum == 3) {
					InsertSQLBuilder insert = new InsertSQLBuilder("SCORE_STANDARD2");
					String id = UIUtil.getSequenceNextValByDS(null, "S_SCORE_STANDARD2");
					insert.putFieldValue("id", id);
					insert.putFieldValue("scoretype", tbutil.isEmpty(secondid) ? parentid : secondid);
					seq++;
					insert.putFieldValue("point", content + "");
					insert.putFieldValue("pointcode", code + "");
					insert.putFieldValue("score", score + "");
					sqllist.add(insert);
				}
			}
			UIUtil.executeBatchByDS(null, sqllist);
			MessageBox.show(this, "����ɹ�!");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	//һ����ϵͳ����������͸�λ
	private void onImportDeptAndPost() throws Exception {
		JFileChooser chooser = new JFileChooser();
		int flag = chooser.showOpenDialog(this);
		if (flag == 1) {
			return;
		}
		File file = chooser.getSelectedFile();
		if (file == null) {
			return;
		}
		ExcelUtil util = new ExcelUtil();
		String data[][] = util.getExcelFileData(file.getAbsolutePath());
		StringBuffer msg = new StringBuffer();
		String[] post = new String[] { "���³�", "���³�", "�г�", "���г�", "���»�����", "�г�����" };
		HashSet set = new HashSet();
		for (int i = 0; i < post.length; i++) {
			set.add(post[i]);
		}

		HashMap<String, String> dept_id_map = new HashMap<String, String>();
		String mainID = UIUtil.getStringValueByDS(null, "select id from pub_corp_dept where corptype='ȫ��'"); //ȫ��
		List sqlList = new ArrayList();
		if (tbutil.isEmpty(mainID)) {
			InsertSQLBuilder insert = new InsertSQLBuilder("pub_corp_dept");
			mainID = UIUtil.getSequenceNextValByDS(null, "S_PUB_CORP_DEPT");
			insert.putFieldValue("id", mainID);
			insert.putFieldValue("code", "ȫ��");
			insert.putFieldValue("name", "ȫ��");
			sqlList.add(insert);
		}
		dept_id_map.put("ȫ��", mainID);
		HashMap beforeItem = new HashMap();
		HashMap<String, Integer> dept_post_seq = new HashMap<String, Integer>();
		StringBuffer errmsg = new StringBuffer();
		int istitle = MessageBox.showConfirmDialog(this, "Excel��һ���Ƿ�Ϊ����", "��ʾ", JOptionPane.YES_NO_OPTION);
		if (istitle == -1) {
			return;
		}
		for (int i = (istitle == 0 ? 1 : 0); i < data.length; i++) {
			for (int j = 0; j < data[j].length; j++) {
				String cellvalue = data[i][j]; //��������
				if (tbutil.isEmpty(cellvalue)) {
					continue;
				}
				beforeItem.remove("level" + j); //�����Ϊ�գ����Ƴ�һ������ټ���
				beforeItem.remove("level" + (j + 1)); //�����Ϊ�գ����Ƴ�һ������ټ���
				beforeItem.remove("level" + (j + 2)); //�����Ϊ�գ����Ƴ�һ������ټ���
				beforeItem.remove("level" + (j + 3)); //�����Ϊ�գ����Ƴ�һ������ټ���
				cellvalue = cellvalue.trim();
				if (set.contains(cellvalue) || cellvalue.contains("��") || cellvalue.endsWith("����")) { //˵���Ǹ�λ���ҵ��ϼ�
					int m = j;
					String deptid = null;
					while (m >= 0) {
						String value = (String) beforeItem.get("level" + (m - 1));
						if (!tbutil.isEmpty(value)) {
							deptid = value;
							break;
						}
						m--; //����һ��
					}
					String postid = UIUtil.getSequenceNextValByDS(null, "S_PUB_POST");
					if (!tbutil.isEmpty(deptid)) {
						String seq = "1";
						if (dept_post_seq.containsKey(deptid)) {
							Integer intseq = dept_post_seq.get(deptid);
							intseq = intseq + 1;
							seq = intseq.toString();
							dept_post_seq.put(deptid, intseq);
						} else {
							dept_post_seq.put(deptid, new Integer(1));
						}
						String postsql = getPostSql(postid, cellvalue, deptid, seq);
						sqlList.add(postsql);
					} else {
						errmsg.append("��" + (i + 1) + "��" + (j + 1) + "�и�λ�Ҳ�����Ӧ����.\r\n");
					}
					continue;
				}
				String parentid = null;
				if (tbutil.isEmpty(cellvalue) || dept_id_map.containsKey(cellvalue)) { //���Ϊ�գ������Ѿ����ڡ�
					continue;
				} else { //�ҵ�ǰһ������
					int m = j;
					while (m >= 0) {
						String value = (String) beforeItem.get("level" + (m - 1));
						if (!tbutil.isEmpty(value)) {
							parentid = value;
							break;
						}
						m--; //����һ��
					}
				}
				try {
					String deptid = UIUtil.getSequenceNextValByDS(null, "S_PUB_CORP_DEPT");
					InsertSQLBuilder deptinsert = new InsertSQLBuilder("pub_corp_dept");
					deptinsert.putFieldValue("id", deptid);
					deptinsert.putFieldValue("code", cellvalue);
					deptinsert.putFieldValue("name", cellvalue);
					deptinsert.putFieldValue("parentid", tbutil.isEmpty(parentid) ? mainID : parentid);
					sqlList.add(deptinsert);
					if (j != data[i].length - 1) {
						beforeItem.put("level" + j, deptid);
					}
					dept_id_map.put(cellvalue, deptid);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		UIUtil.executeBatchByDS(null, sqlList);
	}

	private void onUserRoleUpdate() {
		JFileChooser chooser = new JFileChooser();
		int flag = chooser.showOpenDialog(this);
		if (flag == 1) {
			return;
		}
		File file = chooser.getSelectedFile();
		if (file == null) {
			return;
		}
		ExcelUtil util = new ExcelUtil();
		String data[][] = util.getExcelFileData(file.getAbsolutePath());
		StringBuffer msg = new StringBuffer();
		try {
			HashMap deptmap = UIUtil.getHashMapBySQLByDS(null, "select trim(name),id from pub_corp_dept order by id");
			String[] deptnames = (String[]) deptmap.keySet().toArray(new String[0]);
			HashMap deptpost = UIUtil.getHashMapBySQLByDS(null, "select concat(concat( trim(b.name),'-'),trim(a.name)) deptpost,a.id from pub_post a,pub_corp_dept b where a.deptid = b.id order by b.id");
			HashMap roles = UIUtil.getHashMapBySQLByDS(null, "select trim(name),id from pub_role");
			HashMap existUser = UIUtil.getHashMapBySQLByDS(null, "select trim(name) ,id from pub_user");
			//Ŀǰû��У��  ����ôǿ�ơ��е�����deptname		postname	username	code	role
			String[] title = new String[] { "deptname", "postname", "username", "code", "role" };
			for (int i = 0; i < title.length; i++) {
				if (!"deptname".equalsIgnoreCase(data[0][i])) {
					//					MessageBox.show(this, "�ֶεĵ�"+(i+1)+"�б�����["+title[i]+"]");
					//					return;
				}
			}
			List sqlList = new ArrayList();
			int usercount = 0;
			int ifhavecount = 0;
			for (int i = 1; i < data.length; i++) {
				String deptname = getString(data[i][0]);
				String postname = getString(data[i][1]);
				String username = getString(data[i][2]);
				String code = getString(data[i][3]);
				if (code != null && !code.equals("")) {
					if (code.contains(".")) {
						code = code.substring(0, code.indexOf("."));
					}
				}
				String role = getString(data[i][4]);
				boolean ifhave = false;
				String userid = "";
				if (username == null || username.equals("")) { //���û���˾ͼ���
					continue;
				}
				if (existUser.containsKey(code)) {
					//					msg.append("ϵͳ�а�����Ա����:"+code +" ֻ���������˽�ɫ�͸�λ��\r\n");
					ifhave = true;
					userid = (String) existUser.get(code);
					ifhavecount++;
				} else {
					userid = UIUtil.getSequenceNextValByDS(null, "S_PUB_USER");
				}
				String deptid = "";
				String postid = "";
				if (deptname != null && deptname.contains("-")) {//����ж���Ļ� ȥ�ڶ���
					deptname = deptname.split("-")[1];
				}
				if (deptmap.containsKey(deptname)) { //�����˻���
					deptid = (String) deptmap.get(deptname);
				} else {
					for (int j = 0; j < deptnames.length; j++) {
						if (deptnames[j].contains(deptname)) {
							deptid = (String) deptmap.get(deptnames[j]);
							break;
						}
					}
					if (tbutil.isEmpty(deptid)) {
						MessageBox.show(this, "Excel�е�" + (i + 1) + "�е����������⣬���ݿ���û�иû���[" + deptname + "]!\r\n");
						return;
					}
				}
				if (role != null && !role.equals("")) {
					if (role.contains(";")) {
						String[] d_roles = tbutil.split(role, ";");
						for (int j = 0; j < d_roles.length; j++) {
							if (roles.containsKey(d_roles[j])) {
								String roleid = (String) roles.get(d_roles[j]);
								sqlList.add(getRoleSql(userid, roleid, deptid));
							} else {
								MessageBox.show(this, "Excel�е�" + (i + 1) + "�е����������⣬���ݿ��иý�ɫ[" + d_roles[j] + "]!");
								return;
							}
						}
					} else {
						if (roles.containsKey(role)) {
							String roleid = (String) roles.get(role);
							sqlList.add(getRoleSql(userid, roleid, deptid));
						} else {
							MessageBox.show(this, "Excel�е�" + (i + 1) + "�е����������⣬���ݿ��иý�ɫ[" + role + "]!");
							return;
						}
					}
				}
			}
			//			UIUtil.executeBatchByDS(null, sqlList);
			for (int j = 0; j < sqlList.size(); j++) {
				System.out.println(sqlList.get(j));
			}
			System.out.println(msg.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void onCompare() throws Exception {
		JFileChooser chooser = new JFileChooser();
		int flag = chooser.showOpenDialog(this);
		if (flag == 1) {
			return;
		}
		File file = chooser.getSelectedFile();
		if (file == null) {
			return;
		}
		ExcelUtil util = new ExcelUtil();
		String data[][] = util.getExcelFileData(file.getAbsolutePath());
		StringBuffer msg = new StringBuffer();

		if (true) {
			return;
		}
		String col = JOptionPane.showInputDialog(this, "��������Ա��(��0��ʼ)", "0");
		if (tbutil.isEmpty(col)) {
			return;
		}
		int istitle = MessageBox.showConfirmDialog(this, "Excel��һ���Ƿ�Ϊ����", "��ʾ", JOptionPane.YES_NO_OPTION);
		if (istitle == -1) {
			return;
		}
		int colindex = Integer.parseInt(col);
		HashMap sysuser = UIUtil.getHashMapBySQLByDS(null, "select name,code from pub_user");
		HashMap sysuser2 = (HashMap) tbutil.deepClone(sysuser);
		List nolist = new ArrayList();
		for (int i = (istitle == 0 ? 1 : 0); i < data.length; i++) {
			String text = data[i][colindex];
			if (tbutil.isEmpty(text)) {
				continue;
			}
			if (sysuser2.containsKey(text.trim())) {
				sysuser.remove(text);
			} else {
				nolist.add(data[i]);
			}
		}
		System.out.println("ϵͳ����Excelû��ƥ�䵽" + sysuser.size() + "��:\r\n");
		for (Iterator iterator = sysuser.keySet().iterator(); iterator.hasNext();) {
			String s = (String) iterator.next();
			System.out.print(s + "\t");
		}
		System.out.print("\r\n");
		System.out.println("Excel������ϵͳ��û��ƥ�䵽" + nolist.size() + "��:\r\n");
		for (int i = 0; i < nolist.size(); i++) {
			Object obj = nolist.get(i);
			if (obj instanceof String[]) {
				String[] d = (String[]) obj;
				for (int j = 0; j < d.length; j++) {
					System.out.print(d[j] + "\t");
				}
				System.out.println();
			} else {
				System.out.print(nolist.get(i) + "\t");
				if (i % 10 == 0) {
					System.out.println();
				}
			}
		}
	}

	private void onExportData() {
		ExportDBTableToXml panel = new ExportDBTableToXml();
		panel.initialize();
		BillDialog dialog = new BillDialog(this, "���ݱ���", 800, 600);
		dialog.getContentPane().add(panel);
		dialog.setVisible(true);
	}

	public void updateUser() {
		JFileChooser chooser = new JFileChooser();
		int flag = chooser.showOpenDialog(this);
		if (flag == 1) {
			return;
		}
		File file = chooser.getSelectedFile();
		if (file == null) {
			return;
		}
		ExcelUtil util = new ExcelUtil();
		String data[][] = util.getExcelFileData(file.getAbsolutePath());
		StringBuffer msg = new StringBuffer();
		try {
			HashMap deptmap = UIUtil.getHashMapBySQLByDS(null, "select trim(name),id from pub_corp_dept order by id");
			HashMap deptpost = UIUtil.getHashMapBySQLByDS(null, "select concat(concat( trim(b.name),\"-\"),trim(a.name)) deptpost,a.id from pub_post a,pub_corp_dept b where a.deptid = b.id order by b.id");
			HashMap roles = UIUtil.getHashMapBySQLByDS(null, "select trim(name),id from pub_role");
			HashMap existUser = UIUtil.getHashMapBySQLByDS(null, "select trim(code) ,id from pub_user");
			//Ŀǰû��У��  ����ôǿ�ơ��е�����deptname		postname	username	code	role
			String[] title = new String[] { "deptname", "postname", "username", "code", "role" };
			for (int i = 0; i < title.length; i++) {
				if (!"deptname".equalsIgnoreCase(data[0][i])) {
					//					MessageBox.show(this, "�ֶεĵ�"+(i+1)+"�б�����["+title[i]+"]");
					//					return;
				}
			}
			List sqlList = new ArrayList();
			int usercount = 0;
			int ifhavecount = 0;
			for (int i = 1; i < data.length; i++) {
				String deptname = getString(data[i][0]);
				String postname = getString(data[i][1]);
				String username = getString(data[i][2]);
				if (postname != null && !postname.equals("")) {
					continue;
				}
				String code = getString(data[i][3]);
				if (code != null && !code.equals("")) {
					if (code.contains(".")) {
						code = code.substring(0, code.indexOf("."));
					}
				}
				String role = getString(data[i][4]);
				String userid = "";
				if (existUser.containsKey(code)) {
					userid = (String) existUser.get(code);
					ifhavecount++;
				}
				if (username == null || username.equals("")) { //���û���˾ͼ���
					continue;
				}
				String deptid = "";
				String postid = "";
				if (deptname != null && deptname.contains("-")) {//����ж���Ļ� ȥ�ڶ���
					deptname = deptname.split("-")[1];
				}
				if (deptmap.containsKey(deptname)) { //�����˻���
					deptid = (String) deptmap.get(deptname);
				} else {
					MessageBox.show(this, "Excel�е�" + (i + 1) + "�е����������⣬���ݿ���û�иû���[" + deptname + "]!\r\n");
					return;
				}
				//��ʼ����sql 
				sqlList.add(getUserPostSql(userid, postid, deptid, "Y"));
			}
			UIUtil.executeBatchByDS(null, sqlList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void copyDuty() {
		try {
			HashVO vo1[] = UIUtil.getHashVoArrayByDS(null, "select * from pub_post where deptid = 388 ");
			HashVO vo2[] = UIUtil.getHashVoArrayByDS(null, "select * from pub_corp_dept where name in('���ŷ�����', '��۳Ƿ�����', '���Ϸ�����', '����������', '����������', '�̲�������', '���޷�����', '����������', '���Ƿ�����')");
			List list = new ArrayList();
			for (int i = 0; i < vo2.length; i++) {
				for (int j = 0; j < vo1.length; j++) {
					String code = vo1[j].getStringValue("code");
					String name = vo1[j].getStringValue("name");
					String descr = vo1[j].getStringValue("descr");
					String innercontact = vo1[j].getStringValue("innercontact");
					String education = vo1[j].getStringValue("education");
					String skill = vo1[j].getStringValue("skill");
					String id = UIUtil.getSequenceNextValByDS(null, "S_PUB_POST");
					String deptid = vo2[i].getStringValue("id");
					InsertSQLBuilder sql = new InsertSQLBuilder("pub_post");
					sql.putFieldValue("id", id);
					sql.putFieldValue("name", name);
					sql.putFieldValue("code", code);
					sql.putFieldValue("descr", descr);
					sql.putFieldValue("innercontact", innercontact);
					sql.putFieldValue("education", education);
					sql.putFieldValue("skill", skill);
					sql.putFieldValue("deptid", deptid);
					list.add(sql.getSQL());
				}
			}
			UIUtil.executeBatchByDS(null, list);
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void onImportDeptDuty() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle("ѡ��һ��Ŀ¼!"); //
		int flag = chooser.showOpenDialog(this);
		if (flag == 1) {
			return;
		}
		File file = chooser.getSelectedFile();
		if (file == null) {
			return;
		}
		ExcelUtil util = new ExcelUtil();
		List sqlList = new ArrayList();
		HashMap deptpost = null;
		try {
			if ("oracle".equalsIgnoreCase(tbutil.getDefaultDataSourceType())) {
				deptpost = UIUtil.getHashMapBySQLByDS(null, "select trim(b.name)||'-'||trim(a.name) deptpost,a.id from pub_post a,pub_corp_dept b where a.deptid = b.id order by b.id");
			} else {
				deptpost = UIUtil.getHashMapBySQLByDS(null, "select concat( trim(b.name),\"-\", trim(a.name) ) deptpost,a.id from pub_post a,pub_corp_dept b where a.deptid = b.id order by b.id");
			}

		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		File files[] = file.listFiles();
		StringBuffer sb = new StringBuffer();
		List sql = new ArrayList();
		for (int j = 0; j < files.length; j++) {
			for (int i = 0; i < 100; i++) {//�����ļ�������ҳǩ��һ��ҳǩ�����ᳬ��100������ǰ��20�����²������ʸ�Ϊ100�������/2015-07-08��
				try {
					String[][] str = util.getExcelFileData(files[j].getAbsolutePath(), i);
					String deptname = getString(str[1][1]);
					String postname = getString(str[2][1]);
					if (postname != null) {
						postname = postname.trim();
					}
					String currPostName = "";
					if (deptname.contains("-")) {
						currPostName = deptname.split("-")[1].trim();
					} else {
						currPostName = deptname.trim();
					}
					String descr = getString(str[3][1]);
					String innercontact = getString(str[4][1]);
					String outcontact = getString(str[5][1]);
					String skill = getString(str[6][1]);
					String education = getString(str[7][1]);
					String postid = "";
					if (deptpost.containsKey(currPostName + "-" + postname)) {
						postid = (String) deptpost.get(currPostName + "-" + postname);
						sql.add(getUpdatePostSQL(postid, descr, innercontact, outcontact, skill, education));
					} else {
						sb.append("���ļ�[" + files[j].getName() + "]�Ҳ�����λ:" + deptname + "-" + postname + "\r\n");
					}
				} catch (Exception ex) {
					break;
				}
			}
		}
		try {
			UIUtil.executeBatchByDS(null, sql);
			MessageBox.show(this, "��λ���³ɹ���");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(sb.toString());
	}

	private String getUpdatePostSQL(String postid, String descr, String innercontact, String outcontact, String skill, String education) {
		UpdateSQLBuilder sql = new UpdateSQLBuilder("pub_post");
		sql.setWhereCondition(" id = " + postid);
		sql.putFieldValue("descr", descr);
		sql.putFieldValue("innercontact", innercontact);
		sql.putFieldValue("outcontact", outcontact);
		sql.putFieldValue("skill", skill);
		sql.putFieldValue("education", education);
		return sql.getSQL();
	}

	/*
	 * ����Excel
	 */
	public void showImportExcelDialog() {
		BillDialog dialog = new BillDialog(this);
		dialog.setTitle("����Excel");
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.setSize(550, 230);
		InputInfoPanel p = new InputInfoPanel(); //�����������壡
		p.initialize();
		dialog.getContentPane().add(p);
		dialog.locationToCenterPosition();
		dialog.setVisible(true);
	}

	TBUtil tbutil = new TBUtil();

	public String getString(String str) {
		if (str == null) {
			return null;
		}
		return str.trim();
	}

	public void onImportUserAndRole() {
		JFileChooser chooser = new JFileChooser();
		int flag = chooser.showOpenDialog(this);
		if (flag == 1) {
			return;
		}
		File file = chooser.getSelectedFile();
		if (file == null) {
			return;
		}
		ExcelUtil util = new ExcelUtil();
		String data[][] = util.getExcelFileData(file.getAbsolutePath());
		StringBuffer msg = new StringBuffer();
		try {
			HashMap deptmap = UIUtil.getHashMapBySQLByDS(null, "select trim(name),id from pub_corp_dept order by id");
			HashMap deptpost = UIUtil.getHashMapBySQLByDS(null, "select concat(concat( trim(b.name),'-'),trim(a.name)) deptpost,a.id from pub_post a,pub_corp_dept b where a.deptid = b.id order by b.id");
			HashMap roles = UIUtil.getHashMapBySQLByDS(null, "select trim(name),id from pub_role");
			HashMap existUser = UIUtil.getHashMapBySQLByDS(null, "select trim(code) ,id from pub_user");
			//Ŀǰû��У��  ����ôǿ�ơ��е�����deptname		postname	username	code	role
			String[] title = new String[] { "deptname", "postname", "username", "code", "role" };
			for (int i = 0; i < title.length; i++) {
				if (!"deptname".equalsIgnoreCase(data[0][i])) {
					//					MessageBox.show(this, "�ֶεĵ�"+(i+1)+"�б�����["+title[i]+"]");
					//					return;
				}
			}
			List sqlList = new ArrayList();
			int usercount = 0;
			int ifhavecount = 0;
			for (int i = 1; i < data.length; i++) {
				String deptname = getString(data[i][0]);
				String postname = getString(data[i][1]);
				String username = getString(data[i][2]);
				String code = getString(data[i][3]);
				if (code != null && !code.equals("")) {
					if (code.contains(".")) {
						code = code.substring(0, code.indexOf("."));
					}
				}
				String role = getString(data[i][4]);
				boolean ifhave = false;
				String userid = "";
				if (username == null || username.equals("")) { //���û���˾ͼ���
					continue;
				}
				if (existUser.containsKey(code)) {
					//					msg.append("ϵͳ�а�����Ա����:"+code +" ֻ���������˽�ɫ�͸�λ��\r\n");
					ifhave = true;
					userid = (String) existUser.get(code);
					ifhavecount++;
				} else {
					userid = UIUtil.getSequenceNextValByDS(null, "S_PUB_USER");
				}
				String deptid = "";
				String postid = "";
				if (deptname != null && deptname.contains("-")) {//����ж���Ļ� ȥ�ڶ���
					deptname = deptname.split("-")[1];
				}
				if (deptmap.containsKey(deptname)) { //�����˻���
					deptid = (String) deptmap.get(deptname);
				} else {
					MessageBox.show(this, "Excel�е�" + (i + 1) + "�е����������⣬���ݿ���û�иû���[" + deptname + "]!\r\n");
					return;
				}
				//��ʼ����sql 
				if (!ifhave) { //���û����Ա�ʹ���
					sqlList.add(getInsertUserSql(userid, code, username));
					existUser.put(code, userid);
					usercount++;
				}

				if (postname != null && !postname.equals("")) {
					String postnames[] = null;
					if (postname.indexOf("��") > 0) {
						postnames = postname.split("��");
					} else {
						postnames = new String[] { postname };
					}
					for (int j = 0; j < postnames.length; j++) {
						String _isdefault = "Y";
						if (j > 0) {
							_isdefault = "N";
						}
						postname = postnames[j];
						if (deptpost.containsKey(deptname + "-" + postname)) {
							postid = (String) deptpost.get(deptname + "-" + postname);
							sqlList.add(getUserPostSql(userid, postid, deptid, _isdefault));
						} else {//û�и�λ ����һ��
							postid = UIUtil.getSequenceNextValByDS(null, "S_PUB_POST");
							String postsql = getPostSql(postid, postname, deptid);
							sqlList.add(postsql);
							sqlList.add(getUserPostSql(userid, postid, deptid, _isdefault));
							deptpost.put(deptname + "-" + postname, postid);
							msg.append("û����ϵͳ���ҵ���" + (i + 1) + "�еĸ�λ" + deptname + "-" + postname + "!�Զ�����\r\n");
						}
					}
				} else {
					sqlList.add(getUserPostSql(userid, null, deptid, "Y"));
				}
				if (role != null && !role.equals("")) {
					if (role.contains(";")) {
						String[] d_roles = tbutil.split(role, ";");
						for (int j = 0; j < d_roles.length; j++) {
							if (roles.containsKey(d_roles[j])) {
								String roleid = (String) roles.get(d_roles[j]);
								sqlList.add(getRoleSql(userid, roleid, deptid));
							} else {
								MessageBox.show(this, "Excel�е�" + (i + 1) + "�е����������⣬���ݿ��иý�ɫ[" + d_roles[j] + "]!");
								return;
							}
						}
					} else {
						if (roles.containsKey(role)) {
							String roleid = (String) roles.get(role);
							sqlList.add(getRoleSql(userid, roleid, deptid));
						} else {
							MessageBox.show(this, "Excel�е�" + (i + 1) + "�е����������⣬���ݿ��иý�ɫ[" + role + "]!");
							return;
						}
					}
				}
			}
			//			System.out.println(usercount);
			//			System.out.println(ifhavecount);
			//			System.out.println(sqlList.size());
			//			for (int j = 0; j < sqlList.size(); j++) {
			//				if(j<100){
			//					System.out.println(sqlList.get(j));
			//				}
			//			}
			UIUtil.executeBatchByDS(null, sqlList);
			System.out.println(msg.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getInsertUserSql(String userid, String code, String name) {
		InsertSQLBuilder sql = new InsertSQLBuilder("pub_user");
		sql.putFieldValue("id", userid);
		sql.putFieldValue("code", code);
		sql.putFieldValue("name", name);
		sql.putFieldValue("pwd", "1");
		sql.putFieldValue("islock", "N");
		sql.putFieldValue("creator", "����Ա����");
		sql.putFieldValue("createdate", date);
		sql.putFieldValue("desktopstyle", "A");
		sql.putFieldValue("lookandfeeltype", "0");
		sql.putFieldValue("isfunfilter", "Y");
		return sql.getSQL();
	}

	private String getRoleSql(String userid, String roleid, String userdept) throws Exception {
		InsertSQLBuilder sql = new InsertSQLBuilder("pub_user_role");
		sql.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_pub_user_role"));
		sql.putFieldValue("userid", userid);
		sql.putFieldValue("roleid", roleid);
		sql.putFieldValue("userdept", userdept);
		return sql.getSQL();
	}

	private String getPostSql(String id, String name, String deptid) {
		return getPostSql(id, name, deptid, "");
	}

	private String getPostSql(String id, String name, String deptid, String _seq) {
		InsertSQLBuilder sql = new InsertSQLBuilder("pub_post");
		sql.putFieldValue("id", id);
		sql.putFieldValue("deptid", deptid);
		sql.putFieldValue("code", name);
		sql.putFieldValue("name", name);
		sql.putFieldValue("seq", _seq);
		sql.putFieldValue("descr", name);
		return sql.getSQL();
	}

	private String getUserPostSql(String userid, String postid, String userdept, String _isdefault) throws Exception {
		InsertSQLBuilder sql = new InsertSQLBuilder("pub_user_post");
		sql.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_pub_user_post"));
		sql.putFieldValue("userid", userid);
		sql.putFieldValue("postid", postid);
		sql.putFieldValue("userdept", userdept);
		sql.putFieldValue("isdefault", _isdefault == null ? "Y" : _isdefault);
		return sql.getSQL();

	}

	public static void main(String[] args) {
		JFileChooser chooser1 = new JFileChooser();
		int flag1 = chooser1.showOpenDialog(null);
		if (flag1 == 1) {
			return;
		}
		File file1 = chooser1.getSelectedFile();
		if (file1 == null) {
			return;
		}
		ExcelUtil util1 = new ExcelUtil();
		String data1[][] = util1.getExcelFileData(file1.getAbsolutePath());
		HashMap map1 = new HashMap();
		for (int i = 1; i < data1.length; i++) {
			if (data1[i][3] != null && !data1[i][3].equals("")) {
				data1[i][3] = data1[i][3].trim();
				if (data1[i][3].contains(".")) {
					data1[i][3] = data1[i][3].substring(0, data1[i][3].indexOf("."));
				}
				map1.put(data1[i][3].trim(), null);
			}
		}
		System.out.println(data1.length);
		System.out.println(map1.size());
		JFileChooser chooser = new JFileChooser();
		int flag = chooser.showOpenDialog(null);
		if (flag == 1) {
			return;
		}
		File file = chooser.getSelectedFile();
		if (file == null) {
			return;
		}
		ExcelUtil util = new ExcelUtil();
		String data[][] = util.getExcelFileData(file.getAbsolutePath());
		for (int i = 1; i < data.length; i++) {
			String d = data[i][0];
			if (d != null && !d.equals("")) {
				if (d.contains(".")) {
					d = d.substring(0, d.indexOf("."));
				}
				if (d.trim().length() == 1) {
					d = "000" + d;
				} else if (d.trim().length() == 2) {
					d = "00" + d;
				} else if (d.trim().length() == 3) {
					d = "0" + d;
				}
				if (!map1.containsKey(d)) {
					System.out.println("��������Ա���:" + d + " ������" + data[i][1] + " ����: " + data[i][2]);
				}
			}
		}
	}

	private void onUpdate() {
		String sql_1 = "alter table cmp_event modify eventcorpid varchar(200)";
		String sql_2 = "create or replace view v_risk_process_file as select  t1.id,  t1.id  risk_id,  t1.riskcode  risk_code,  t1.riskname  risk_name," + " t1.risktype risk_risktype," + " t1.rank     risk_rank," + " t1.wfactivity_id," + " t1.wfactivity_code," + " t1.wfactivity_name," + " t1.possible," + " t1.serious," + " t1.ctrlfneffect," + " t1.findchannel," + " t1.riskreftype,"
				+ " t1.identify_date," + " t1.evaluate_date," + " t2.id  wfprocess_id," + " t2.code  wfprocess_code," + " t2.name  wfprocess_name," + " t3.id  cmpfile_id," + " t3.cmpfilename," + " t4.name as cmpfiletype," + " t3.filestate," + " t3.blcorpid," + " t3.blcorpname," + " t3.bsactid," + " t3.bsactname," + " t3.ictypeid," + " t3.ictypename," + " t3.publishdate,"
				+ " t5.name as filestatename " + " from cmp_risk  t1 " + " left join  pub_wf_process  t2 on t1.wfprocess_id =t2.id " + " left join  cmp_cmpfile  t3 on t1.cmpfile_id=t3.id " + " left join  pub_comboboxdict t4 on t3.cmpfiletype = t4.id and t4.type = '�ļ�����' " + "left join  pub_comboboxdict t5 on t3.filestate = t5.id and t5.type = '�ļ�״̬'";

		String sql2_1 = "create or replace view v_process_file as  select  t2.id  wfprocess_id, " + " t2.code  wfprocess_code, " + " t2.name  wfprocess_name, " + " t3.id  cmpfile_id, " + " t3.cmpfilename, " + " t4.name as cmpfiletype, " + " t3.filestate, " + " t3.blcorpid, " + " t3.blcorpname, " + " t3.bsactid, " + " t3.bsactname, " + " t3.ictypeid, " + " t3.ictypename, " + " t3.publishdate, "
				+ " t5.name as filestatename " + " from  pub_wf_process  t2 " + " left join  cmp_cmpfile  t3 on t2.cmpfileid=t3.id " + " left join  pub_comboboxdict t4 on t3.cmpfiletype = t4.id and t4.type = '�ļ�����' " + " left join  pub_comboboxdict t5 on t3.filestate = t5.id and t5.type = '�ļ�״̬'  " + " where  t2.cmpfileid is not null  ";

		//		String sql_2 = "create or replace view v_report_check as select cmp_check_id, checkname, c.checkcorp as checkcorp,eventcorpid,findchannel, checkbegindate, '�ɹ�����' as eventType from cmp_ward w left join cmp_check c on w.cmp_check_id = c.id where w.cmp_check_id is not null union all select cmp_check_id, checkname,c.checkcorp as checkcorp,eventcorpid, findchannel, checkbegindate, 'Υ���¼�' as eventType from cmp_event e left join cmp_check c on e.cmp_check_id = c.id  where e.cmp_check_id is not null    union all    select cmp_check_id, checkname,c.checkcorp as checkcorp,eventcorpid, findchannel, checkbegindate, '��������' as eventType    from cmp_issue e    left join cmp_check c on e.cmp_check_id = c.id    where e.cmp_check_id is not null";
		String sql_3 = "create index in_cmp_cmpfile_rule_2 on cmp_cmpfile_rule(wfactivity_id)";
		String sql_4 = "create index in_cmp_cmpfile_rule_3 on cmp_cmpfile_rule(wfprocess_id)";
		String sql_5 = "create index in_cmp_cmpfile_law_2 on cmp_cmpfile_law(wfactivity_id)";
		String sql_6 = "create index in_cmp_cmpfile_law_3 on cmp_cmpfile_law(wfprocess_id)";
		String sql_7 = "update pub_wf_activity set isneedreport='Y',iscanlookidea='*'";
		String sql_8 = "delete from wltdual";
		String sql_9 = "insert into wltdual value('1')";
		String sql_10 = "insert into db_ver values('2012-10-30')";
		String sql_11 = "delete from pub_wf_process where id = 9301 and code like '%��������%'"; //��������
		String sql_12 = "delete from pub_templet_1"; //���ģ��
		String sql_13 = "delete from pub_templet_1_item";//���ģ��

		String sql_14 = "alter table pub_post add refpostid decimal(22)";
		String sql_15 = "alter table pub_post add seq decimal(5)";
		String sql_16 = "alter table pub_user_post add seq decimal(5)";
		String sql_17 = "alter table pub_desktop_new add capimg varchar(30)";
		String sql_18 = "alter table pub_desktop_new add titlecolor varchar(30)";
		String sql_19 = "alter table pub_desktop_new add newcount decimal(2)";
		String sql_20 = " alter table pub_desktop_new add islazyload varchar(4)";
		String sql_21 = "insert into pub_option (id,modulename,parkey,parvalue) values('-10','ƽ̨_����','��������ʽ�����ı�','pub_corp_dept/����;pub_user/��Ա;pub_role/��ɫ;pub_user_role/��ԱȨ��')"; //������־���
		String sql_22 = "update pub_datapolicy_b set corptypes_m = null where id =  281";
		String sql_23 = "delete from pub_menu where Code in('Bug����','Bugͳ��','��������ͳ��','��ť������־','��¼ϵͳ��־','������Ա�鿴')"; //ɾ����־
		ArrayList list = new ArrayList();
		list.add(sql_1);
		list.add(sql_2);
		list.add(sql2_1);
		list.add(sql_3);
		list.add(sql_4);
		list.add(sql_5);
		list.add(sql_6);
		list.add(sql_7);
		list.add(sql_8);
		list.add(sql_9);
		list.add(sql_10);
		list.add(sql_11);
		list.add(sql_12);
		list.add(sql_13);
		list.add(sql_14);
		list.add(sql_15);
		list.add(sql_16);
		list.add(sql_17);
		list.add(sql_18);
		list.add(sql_19);
		list.add(sql_20);
		list.add(sql_21);
		list.add(sql_22);
		list.add(sql_23);
		try {
			UIUtil.executeBatchByDS(null, list);
			update_201210.setEnabled(false);
			MessageBox.show(this, "�����ɹ���");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void importExcelDate() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("��ѡ���ļ�Ŀ¼");
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int flag = chooser.showSaveDialog(this);
		if (flag == 1 || chooser.getSelectedFile() == null) {
			return;
		}
		String str_path = chooser.getSelectedFile().getAbsolutePath();
		if (str_path == null) {
			return;
		}
		setCheckBoxPanel_Value(str_path);
	}

	private void setCheckBoxPanel_Value(String _str_path) {
		String[][] excelFileData = new ExcelUtil().getExcelFileData(_str_path, 0);
		int idindex = 0;
		int parentindex = 0;
		int codeindex = 0;
		int contentindex = 0;
		int scoreindex = 0;
		for (int i = 0; i < excelFileData[0].length; i++) {
			String excel_0 = excelFileData[0][i];
			if (excel_0.equals("id")) {
				idindex = i;
			}
			if (excel_0.equals("parentid")) {
				parentindex = i;
			}
			if (excel_0.equals("code")) {
				codeindex = i;
			}
			if (excel_0.equals("content")) {
				contentindex = i;
			}
			if (excel_0.equals("score")) {
				scoreindex = i;
			}
		}
		List sqllist = new ArrayList();
		String parentid = "";
		String secondid = "";
		int firstseq = 0;
		int secondseq = 1;
		int seq = 1;
		try {
			for (int i = 1; i < excelFileData.length; i++) {
				InsertSQLBuilder builder = new InsertSQLBuilder("CMP_SCORE_STAND");
				if (excelFileData[i][idindex].equals("") && excelFileData[i][parentindex].equals("")) {
					String id = UIUtil.getSequenceNextValByDS(null, "S_CMP_SCORE_STAND");
					builder.putFieldValue("id", id);
					builder.putFieldValue("parentid", parentid);
					String code = excelFileData[i][codeindex];
					if (code.indexOf(".") > 0) {
						builder.putFieldValue("code", code.substring(0, code.indexOf(".")));
					} else {
						builder.putFieldValue("code", code);
					}
					builder.putFieldValue("content", excelFileData[i][contentindex]);
					if (excelFileData[i][scoreindex].indexOf(".") > 0) {
						builder.putFieldValue("score", excelFileData[i][scoreindex].substring(0, excelFileData[i][scoreindex].indexOf(".")));
					} else {
						builder.putFieldValue("score", excelFileData[i][scoreindex]);
					}
					builder.putFieldValue("seq", ++seq);
					sqllist.add(builder.getSQL());
				} else if (excelFileData[i][parentindex].equals("")) {
					String id = UIUtil.getSequenceNextValByDS(null, "S_CMP_SCORE_STAND");
					builder.putFieldValue("id", id);
					builder.putFieldValue("content", excelFileData[i][idindex]);
					builder.putFieldValue("seq", ++firstseq);
					sqllist.add(builder.getSQL());
					parentid = id;
					String ids = UIUtil.getSequenceNextValByDS(null, "S_CMP_SCORE_STAND");
					builder.putFieldValue("id", ids);
					builder.putFieldValue("parentid", parentid);
					String code = excelFileData[i][codeindex];
					if (code.indexOf(".") > 0) {
						builder.putFieldValue("code", code.substring(0, code.indexOf(".")));
					} else {
						builder.putFieldValue("code", code);
					}
					builder.putFieldValue("content", excelFileData[i][contentindex]);
					if (excelFileData[i][scoreindex].indexOf(".") > 0) {
						builder.putFieldValue("score", excelFileData[i][scoreindex].substring(0, excelFileData[i][scoreindex].indexOf(".")));
					} else {
						builder.putFieldValue("score", excelFileData[i][scoreindex]);
					}
					seq = 1;
					builder.putFieldValue("seq", seq);
					sqllist.add(builder.getSQL());
				} else if (excelFileData[i][idindex].equals("")) {
					String id = UIUtil.getSequenceNextValByDS(null, "S_CMP_SCORE_STAND");
					builder.putFieldValue("id", id);
					builder.putFieldValue("parentid", secondid);
					builder.putFieldValue("content", excelFileData[i][parentindex]);
					builder.putFieldValue("seq", ++secondseq);
					sqllist.add(builder.getSQL());
					parentid = id;
					String ids = UIUtil.getSequenceNextValByDS(null, "S_CMP_SCORE_STAND");
					builder.putFieldValue("id", ids);
					builder.putFieldValue("parentid", parentid);
					String code = excelFileData[i][codeindex];
					if (code.indexOf(".") > 0) {
						builder.putFieldValue("code", code.substring(0, code.indexOf(".")));
					} else {
						builder.putFieldValue("code", code);
					}
					builder.putFieldValue("content", excelFileData[i][contentindex]);
					if (excelFileData[i][scoreindex].indexOf(".") > 0) {
						builder.putFieldValue("score", excelFileData[i][scoreindex].substring(0, excelFileData[i][scoreindex].indexOf(".")));
					} else {
						builder.putFieldValue("score", excelFileData[i][scoreindex]);
					}
					seq = 1;
					builder.putFieldValue("seq", seq);
					sqllist.add(builder.getSQL());
				} else {
					String id = UIUtil.getSequenceNextValByDS(null, "S_CMP_SCORE_STAND");
					builder.putFieldValue("id", id);
					builder.putFieldValue("content", excelFileData[i][idindex]);
					builder.putFieldValue("seq", ++firstseq);
					sqllist.add(builder.getSQL());
					parentid = id;
					secondid = id;
					String ids = UIUtil.getSequenceNextValByDS(null, "S_CMP_SCORE_STAND");
					builder.putFieldValue("id", ids);
					builder.putFieldValue("parentid", parentid);
					builder.putFieldValue("content", excelFileData[i][parentindex]);
					secondseq = 1;
					builder.putFieldValue("seq", secondseq);
					sqllist.add(builder.getSQL());
					parentid = ids;
					String lastid = UIUtil.getSequenceNextValByDS(null, "S_CMP_SCORE_STAND");
					builder.putFieldValue("id", lastid);
					builder.putFieldValue("parentid", parentid);
					String code = excelFileData[i][codeindex];
					if (code.indexOf(".") > 0) {
						builder.putFieldValue("code", code.substring(0, code.indexOf(".")));
					} else {
						builder.putFieldValue("code", code);
					}
					builder.putFieldValue("content", excelFileData[i][contentindex]);
					if (excelFileData[i][scoreindex].indexOf(".") > 0) {
						builder.putFieldValue("score", excelFileData[i][scoreindex].substring(0, excelFileData[i][scoreindex].indexOf(".")));
					} else {
						builder.putFieldValue("score", excelFileData[i][scoreindex]);
					}
					seq = 1;
					builder.putFieldValue("seq", seq);
					sqllist.add(builder.getSQL());
				}
			}
			UIUtil.executeBatchByDS(null, sqllist);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void onUpdate2013_5_score() {
		try {
			cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc sysService = (cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc) UIUtil.lookUpRemoteService(cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc.class);
			sysService.createTableByPackagePrefix("/com/pushworld/ipushgrc/bs/install/", "cmp_score_stand");
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		String str_xml = tbutil.readFromInputStreamToStr(this.getClass().getResourceAsStream("/com/pushworld/ipushgrc/bs/install/xtdata/cmp_score_stand_100001.xml")); //
		try {
			UIUtil.getMetaDataService().importXmlToTable1000Records(null, "cmp_score_stand_100001.xml", str_xml);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//�����˵�
		try {
			String parentMenuID = UIUtil.getStringValueByDS(null, "select id from pub_menu where name = 'Υ�����'");
			InsertSQLBuilder insert = new InsertSQLBuilder("pub_menu");
			String menuID = UIUtil.getSequenceNextValByDS(null, "S_pub_menu");
			insert.putFieldValue("id", menuID);
			insert.putFieldValue("code", "Υ����Ϊ�����������ֱ�׼");
			insert.putFieldValue("name", "Υ����Ϊ�����������ֱ�׼");
			insert.putFieldValue("name", "Υ����Ϊ�����������ֱ�׼");
			insert.putFieldValue("parentmenuid", parentMenuID);
			insert.putFieldValue("seq", menuID);
			insert.putFieldValue("usecmdtype", "1");
			insert.putFieldValue("commandtype", "00");
			insert.putFieldValue("command", "com.pushworld.ipushgrc.ui.cmpscore.p100.CmpScoreStandardWKPanel");
			insert.putFieldValue("isautostart", "N");
			insert.putFieldValue("isextend", "N");
			insert.putFieldValue("extendheight", "700");
			insert.putFieldValue("opentype", "TAB");
			insert.putFieldValue("showintoolbar", "N");
			insert.putFieldValue("opentypeweight", "800");
			insert.putFieldValue("opentypeheight", "600");
			insert.putFieldValue("isalwaysopen", "N");

			InsertSQLBuilder roleMenu = new InsertSQLBuilder("pub_role_menu");
			roleMenu.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_ pub_role_menu"));
			roleMenu.putFieldValue("roleid", "312");//����
			roleMenu.putFieldValue("menuid", menuID);

			String deleteTempletitem = "delete from pub_templet_1_item where 1=1";
			String deleteTemplet = "delete from pub_templet_1 where 1=1";
			String scoreaddstand = "alter table cmp_score_lost add stand varchar(50)";
			String altertable = "alter table cmp_score_lost modify scorelost_ref  varchar(30)";
			String sql_10 = "insert into db_ver values('2013-06-03')";
			String insertSeq = "insert into pub_sequence values('S_CMP_SCORE_STAND','4000')";
			UIUtil.executeBatchByDS(null, new String[] { altertable, scoreaddstand, insert.getSQL(), roleMenu.getSQL(), deleteTempletitem, deleteTemplet, sql_10, insertSeq });
			MessageBox.show(this, "Υ����������ɹ�!");
			update_2013_5.setEnabled(false);
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}