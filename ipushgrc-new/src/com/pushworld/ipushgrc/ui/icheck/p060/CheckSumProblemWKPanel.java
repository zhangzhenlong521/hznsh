package com.pushworld.ipushgrc.ui.icheck.p060;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.report.cellcompent.ExcelUtil;

import com.pushworld.ipushgrc.ui.icheck.p040.ICheckUIUtil;
import com.pushworld.ipushgrc.ui.icheck.p080.ZZLUIUtil;
import com.pushworld.ipushgrc.ui.icheck.word.WordExport;

/**
 * 
 * @author zzl ������� �������ʵʩȷ����
 * 
 */
public class CheckSumProblemWKPanel extends AbstractWorkPanel implements
		BillListSelectListener, ActionListener, BillListHtmlHrefListener {

	BillListPanel list = new BillListPanel("CK_PROBLEM_INFO_ZZL_EQ1");
	WLTButton impss_btn = new WLTButton("�����ֳ������ʵȷ����");
	WLTButton impfss_btn = new WLTButton("�������ֳ������ʵȷ����");
	WLTButton impzg_btn = new WLTButton("��������֪ͨ��");
	WLTButton impDg_btn = new WLTButton("�����׸�");
	private BillQueryPanel billQueryPanel = null;
	private String loginUserid = ClientEnvironment.getInstance()
			.getLoginUserID();
	private HashVO[] vo = null;
	private Object deptid = null;
	private Object schemeid = null;
	private String SCHEMETYPE = null;
	private int count;

	public void initialize() {
		String type = this.getMenuConfMapValueAsStr("�����", "Y");
		impss_btn.addActionListener(this);
		impzg_btn.addActionListener(this);
		impfss_btn.addActionListener(this);
		impDg_btn.addActionListener(this);
		list.addBillListButton(impss_btn);
		list.addBillListButton(impfss_btn);
		list.addBillListButton(impzg_btn);
		System.out.println(">>>>>>>>>>>>>>>>>>>"+type);
		if(type.equals("Y")){
			list.addBillListButton(impDg_btn);
		}
		list.repaintBillListButton();
		this.add(list);
	}

	public void onBillListSelectChanged(BillListSelectionEvent e) {

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == impss_btn) {
			deptid = (Object) list.getQuickQueryPanel()
					.getRealValueAt("deptid");
			schemeid = (Object) list.getQuickQueryPanel().getRealValueAt(
					"schemeid");
			try {
				SCHEMETYPE = UIUtil.getStringValueByDS(null,
						"select SCHEMETYPE from CK_SCHEME where id='"
								+ schemeid + "'");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (SCHEMETYPE == null) {
				MessageBox.show(this, "��ѡ����Ŀ����");
				return;
			} else if (SCHEMETYPE.equals("����")) {
				impword();
			} else {
				impXdword();
			}

		} else if (e.getSource() == impzg_btn) {
			deptid = (Object) list.getQuickQueryPanel()
					.getRealValueAt("deptid");
			schemeid = (Object) list.getQuickQueryPanel().getRealValueAt(
					"schemeid");
			try {
				SCHEMETYPE = UIUtil.getStringValueByDS(null,
						"select SCHEMETYPE from CK_SCHEME where id='"
								+ schemeid + "'");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (SCHEMETYPE == null) {
				MessageBox.show(this, "��ѡ����Ŀ����");
				return;
			} else if (SCHEMETYPE.equals("����")) {
				impzgword();
			} else {
				impxdzgword();
			}

		} else if (e.getSource() == impfss_btn) {
			deptid = (Object) list.getQuickQueryPanel()
					.getRealValueAt("deptid");
			schemeid = (Object) list.getQuickQueryPanel().getRealValueAt(
					"schemeid");
			impfss_btnword();
		} else if (e.getSource() == impDg_btn) {
			deptid = (Object) list.getQuickQueryPanel()
					.getRealValueAt("deptid");
			schemeid = (Object) list.getQuickQueryPanel().getRealValueAt(
					"schemeid");
			try {
				SCHEMETYPE = UIUtil.getStringValueByDS(null,
						"select SCHEMETYPE from CK_SCHEME where id='"
								+ schemeid + "'");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (SCHEMETYPE == null) {
				MessageBox.show(this, "��ѡ����Ŀ����");
				return;
			} else if (SCHEMETYPE.equals("����")) {
				impDg();
			} else {
				impXdDg();
			}
		}
	}

	/**
	 * �����Ŵ����׸�
	 */
	private void impXdDg() {
		try{
		String name = UIUtil.getStringValueByDS(null,
				"select name from CK_SCHEME where id='" + schemeid
						+ "'");
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("��ѡ����Ŀ¼");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int flag = chooser.showSaveDialog(this);
		if (flag == 1 || chooser.getSelectedFile() == null) {
			return;
		}
		final String str_path = chooser.getSelectedFile().getAbsolutePath(); //
		if (str_path == null) {
			return;
		}
		File f = new File(str_path + "\\" + name);
		f.mkdir();
		final String str_path2 = str_path + "\\" + name;
		if (!f.exists()) {
			MessageBox.show(this, "·��:" + str_path + " ������!");
			return;
		}
		new SplashWindow(this, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					String[][] impdg = UIUtil.getStringArrayByDS(null,
							"select id,deptid from ck_problem_info where schemeid='"
									+ schemeid + "' group by deptid");
					HashVO [] dgvo = UIUtil.getHashVoArrayByDS(null,
							"select * from CK_SCHEME where id='"+schemeid+ "'");
					String time = ZZLUIUtil.getTimeString(dgvo[0]
						              						.getStringValue("PLANBEGINDATE"))
						              						+ "��"
						              						+ ZZLUIUtil.getTimeString(dgvo[0]
						              								.getStringValue("PLANENDDATE"));
					count=impdg.length;
					for(int im=0;im<impdg.length;im++){
						Map<Object, Object> dataMap = new HashMap<Object, Object>();
						HashVO[] implvo=null;
						if(impdg[im][1]!=null && !impdg[im][1].equals(null) && !impdg[im][1].equals("") && !impdg[im][1].equals("null")){
							implvo = UIUtil.getHashVoArrayByDS(null,
									"select * from ck_problem_info where   schemeid='"
											+ schemeid + "' and deptid='" + impdg[im][1]+ "' and checkmode='�ֳ����'  group by implid");
						}else{
							continue;
						}
		     			String dept=null;
							dept = UIUtil.getStringValueByDS(null,
									"select name from pub_corp_dept where id='"
											+ implvo[0].getStringValue("deptid") + "'");						
						dataMap.put("dept", dept);
						String[] timea = implvo[0].getStringValue("createdate").split("-");
						dataMap.put("time", time);
						dataMap.put("time2",ZZLUIUtil.getTimeString(dgvo[0].getStringValue("PLANENDDATE")));
						String planname = UIUtil.getStringValueByDS(null,
								"select name from CK_SCHEME where id='"
										+ implvo[0].getStringValue("schemeid") + "'");
						dataMap.put("planname", planname);
						StringBuilder sb = new StringBuilder();
						int imp = 0;
						for (int vo = 0; vo < implvo.length; vo++) {
							HashVO[] ipvo = UIUtil.getHashVoArrayByDS(null,
									"select * from CK_SCHEME_IMPL where id='"
											+ implvo[vo].getStringValue("implid")
											+ "' or refimplid='"
											+ implvo[vo].getStringValue("implid")
											+ "' order by refimplid");
							String money = UIUtil.getStringValueByDS(null,
									"select sum(c8)/10000 from CK_SCHEME_IMPL where id='"
											+ implvo[vo].getStringValue("implid")
											+ "' or refimplid='"
											+ implvo[vo].getStringValue("implid")
											+ "' order by refimplid");
							imp = imp + 1;
							String impnum = stringNumber(imp);
							sb.append(impnum).append("��");
							if (ipvo.length > 1) {
								sb.append("�����"
										+ ipvo[0].getStringValue("c1")
										+ "����С����"
										+ ZZLUIUtil.getTimeString(ipvo[0]
												.getStringValue("c6")) + "��"
										+ ipvo[0].getStringValue("c16"));
								sb.append("������������" + money + "��Ԫ������"
										+ ipvo[0].getStringValue("c18"));
								sb.append("�����У�");
								for (int p = 0; p < ipvo.length; p++) {
									sb.append(ipvo[p].getStringValue("c1")
											+ "����"
											+ Double.parseDouble(ipvo[p]
													.getStringValue("c8")) / 10000
											+ "��Ԫ���������ʣ�");
									String strdb = ipvo[p].getStringValue("c11");
									strdb = strdb.substring(0, strdb.indexOf("."));
									int idb = Integer.parseInt(strdb);
									if (idb == 0) {
										Double db = Double.parseDouble(ipvo[p]
												.getStringValue("c11"));
										db = db * 1000;
										sb.append(db + "�룻");
									} else {
										sb.append(ipvo[p].getStringValue("c11") + "�룻");
									}

								}
								sb.append("������"
										+ ZZLUIUtil.getTimeString(ipvo[0]
												.getStringValue("c7")) + "����");
							} else if (ipvo.length > 0) {
								if (ipvo[0].getStringValue("c6") != null) {
									sb.append("�����"
											+ ipvo[0].getStringValue("c1")
											+ "��"
											+ ZZLUIUtil.getTimeString(ipvo[0]
													.getStringValue("c6")) + "��"
											+ ipvo[0].getStringValue("c16"));
								}
								sb.append("��������" + money + "��Ԫ������"
										+ ipvo[0].getStringValue("c18"));
								if (ipvo[0].getStringValue("c7") != null) {
									sb.append("��"
											+ ZZLUIUtil.getTimeString(ipvo[0]
													.getStringValue("c7")) + "���ڣ���������");
								}
								String strdb = null;
								if (ipvo[0].getStringValue("c11") != null) {
									strdb = ipvo[0].getStringValue("c11");
									strdb = strdb.substring(0, strdb.indexOf("."));

									int idb = Integer.parseInt(strdb);
									if (idb == 0) {
										Double db = Double.parseDouble(ipvo[0]
												.getStringValue("c11"));
										db = db * 1000;
										sb.append(db + "�룻");
									} else {
										sb.append(ipvo[0].getStringValue("c11") + "�룻");
									}
								}

							}
							if (ipvo.length > 0) {
								if (ipvo[0].getStringValue("usera1") != null) {
									sb.append("����������A�ǣ�"
											+ ZZLUIUtil.getnamesSplit(ipvo[0]
													.getStringValue("usera1")) + "��");
								}
								if (ipvo[0].getStringValue("userb1") != null) {
									sb.append("����������B�ǣ�"
											+ ZZLUIUtil.getnamesSplit(ipvo[0]
													.getStringValue("userb1")) + "��");
								}
								if (ipvo[0].getStringValue("usera2") != null) {
									sb.append("���������A�ǣ�"
											+ ZZLUIUtil.getnamesSplit(ipvo[0]
													.getStringValue("usera2")) + "��");
								}
								if (ipvo[0].getStringValue("userb2") != null) {
									sb.append("���������B�ǣ�"
											+ ZZLUIUtil.getnamesSplit(ipvo[0]
													.getStringValue("userb2")) + "��");
								}
								if (ipvo[0].getStringValue("usera3") != null) {
									sb.append("����������A�ǣ�"
											+ ZZLUIUtil.getnamesSplit(ipvo[0]
													.getStringValue("usera3")) + "��");
								}
								if (ipvo[0].getStringValue("userb3") != null) {
									sb.append("����������B�ǣ�"
											+ ZZLUIUtil.getnamesSplit(ipvo[0]
													.getStringValue("userb3")) + "��");
								}
								if (ipvo[0].getStringValue("usera4") != null) {
									sb.append("��Ӫ������A�ǣ�"
											+ ZZLUIUtil.getnamesSplit(ipvo[0]
													.getStringValue("usera4")) + "��");
								}
								if (ipvo[0].getStringValue("userb4") != null) {
									sb.append("��Ӫ������B�ǣ�"
											+ ZZLUIUtil.getnamesSplit(ipvo[0]
													.getStringValue("userb4")) + "��");
								}
							}
							HashVO[] voIMPLEMENT = UIUtil.getHashVoArrayByDS(null,
									"select * from V_CK_SCHEME_IMPLEMENT where 1=1  and implid = '"
											+ implvo[vo].getStringValue("implid")
											+ "' and deptid='"
											+ implvo[vo].getStringValue("deptid")
											+ "' group by thirdname order by id");
							int a = 0;
							for (int i = 0; i < voIMPLEMENT.length; i++) {
								HashVO[] voct = UIUtil
										.getHashVoArrayByDS(
												null,
												"select * from V_CK_SCHEME_IMPLEMENT where 1=1  and implid = '"
														+ implvo[vo]
																.getStringValue("implid")
														+ "' and deptid='"
														+ implvo[vo]
																.getStringValue("deptid")
														+ "' and thirdname='"
														+ voIMPLEMENT[i]
																.getStringValue("thirdname")
														+ "' and result is not null and result='��Ч'");
								if (voct.length > 0) {
									a = a + 1;
									String num = stringNumber(a);
									sb.append("��" + num + "��").append("\r");
									sb.append(voIMPLEMENT[i].getStringValue("thirdname")
											+ "\r\n");
									int b = 0;
									for (int c = 0; c < voct.length; c++) {
										b = b + 1;
										sb.append(b + "��");
										sb.append(voct[c].getStringValue("dictname"));
										HashVO[] problem = UIUtil.getHashVoArrayByDS(null,
												"select * from ck_problem_info where parentid='"
														+ voct[c].getStringValue("id")
														+ "'");
										String str = String.valueOf(b);
										Double b1 = Double.parseDouble(str);
										DecimalFormat df = new DecimalFormat("######.0");
										for (int j = 0; j < problem.length; j++) {
											b1 = b1 + 0.1;
											String str1 = df.format(b1);
											sb.append("��" + str1 + "����");
											sb.append(problem[j]
													.getStringValue("problemInfo")
													+ "��");
											if (problem[j].getStringValue("adjustuserid") != null) {
												String zrname = ZZLUIUtil
														.getnamesSplit(problem[j]
																.getStringValue("adjustuserid"));
												sb.append("�����ˣ�" + zrname + "��");
											}
											if (problem[j]
													.getStringValue("mainadjustuserid") != null) {
												String zrname = ZZLUIUtil
														.getUserName(problem[j]
																.getStringValue("mainadjustuserid"));
												sb.append("���������ˣ�" + zrname + "��");
											}
										}
									}
								}
							}
						}
						dataMap.put("problem", sb);
						String[][] names = UIUtil.getStringArrayByDS(null,
								"select leader2,teamusers from V_CK_SCHEME where deptid = '"
										+ implvo[0].getStringValue("deptid")
										+ "' and schemeid = '"
										+ implvo[0].getStringValue("schemeid") + "'");
						String leader2ID = names[0][0].toString();
						String teamusersID = names[0][1].toString();
						StringBuilder sb2 = new StringBuilder();
						String leader2 = UIUtil.getStringValueByDS(null,
								"select name from pub_user where id='" + leader2ID + "'");
						sb2.append(leader2 + "\r");
						teamusersID = teamusersID.replace(";", ",");
						teamusersID = teamusersID.substring(1, teamusersID.length() - 1);
						String[] teamusers = UIUtil.getStringArrayFirstColByDS(null,
								"select name from pub_user where id in(" + teamusersID
										+ ")");
						for (int i = 0; i < teamusers.length; i++) {
							sb2.append(teamusers[i].toString() + "\r");
						}
						dataMap.put("names", sb2);
						String[] timeb = implvo[0].getStringValue("createdate").split("-");
						dataMap.put("time2", timeb[0].toString() + "��"
								+ timeb[1].toString() + "��" + timeb[2].toString() + "��");
						String str_path3 = str_path2 + "\\" + dataMap.get("dept") + "_" + planname
								+ "_���׸�.doc";
						WordExport we = new WordExport();
						we.createDoc(dataMap, "WordFactModel2", str_path3);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		MessageBox.show(this, "������"+count+"�����ŵ׸�");
		}catch(Exception  e){
			MessageBox.show(this, "����ʧ��");
			e.printStackTrace();
		}

	}

	/**
	 * �������������׸�
	 */
	private void impDg() {
		try {
			String name = UIUtil.getStringValueByDS(null,
					"select name from CK_SCHEME where id='" + schemeid
							+ "'");
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("��ѡ����Ŀ¼");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int flag = chooser.showSaveDialog(this);
			if (flag == 1 || chooser.getSelectedFile() == null) {
				return;
			}
			String str_path = chooser.getSelectedFile().getAbsolutePath(); //
			if (str_path == null) {
				return;
			}
			File f = new File(str_path + "\\" + name);
			f.mkdir();
			final String str_path2 = str_path + "\\" + name;
			if (!f.exists()) {
				MessageBox.show(this, "·��:" + str_path + " ������!");
				return;
			}
			new SplashWindow(this, new AbstractAction() {			
				@Override
				public void actionPerformed(ActionEvent arg0) {
					ExcelUtil excel = new ExcelUtil();
					String[][] _data = null;
					String[] title=new String[]{"����鲿��","һ��Ŀ¼","����Ŀ¼","����Ŀ¼","��������/���Ҫ��","��鷽��","��鷽ʽ","�������","�����","�������","��������","Ӱ��̶�","����Ƶ��","�Ƿ���ɲ�����ʧ","������Ҫ�Է���","����","ֱ��������","����������","����"};
					try{
					String[][] imp = UIUtil.getStringArrayByDS(null,
							"select implid,deptid from ck_problem_info where schemeid='"
									+ schemeid + "' group by deptid");
					count=imp.length;
					for (int i = 0; i < imp.length; i++) {
						String[][] impdate = UIUtil
								.getStringArrayByDS(
										null,
										"select deptid,firstname,secondname,thirdname,checkpoints,checkmethod,checkMode,control,result,dictid,probleminfo,affect,rate,isloss,importance,suggest,adjustuserid,mainadjustuserid,limitdate from v_ck_impdigao where implid='"
												+ imp[i][0] + "'");
						_data=new String [impdate.length+1][impdate[0].length];
						for(int j=0;j<impdate.length;j++){
							for(int t=0;t<title.length;t++){
								if(j==0){
									_data[j][t]=title[t].toString();
								}
							}
							for(int k=0;k<impdate[j].length;k++){
								if(k==0){
									if(impdate[j][k]!=null && !impdate[j][k].equals(null) && !impdate.equals("")){
										_data[j+1][k]=ZZLUIUtil.getDeptName(impdate[j][k].toString());
									}else{
										_data[j+1][k]=impdate[j][k];
									}
								}else	if(k==9){
										_data[j+1][k]=impdate[j][4];
								}else 	if(k==16){
									if(impdate[j][k]!=null && !impdate[j][k].equals(null) && !impdate[j][k].equals("") && !impdate[j][k].equals("null")){
										_data[j+1][k]=ZZLUIUtil.getnamesSplit(impdate[j][k].toString());
									}else{
										_data[j+1][k]=impdate[j][k];
									}
								}else if(k==17){
									if(impdate[j][k]!=null && !impdate[j][k].toString().equals(null) && !impdate[j][k].toString().equals("") && !impdate[j][k].toString().equals("null")){
										_data[j+1][k]=ZZLUIUtil.getUserName(impdate[j][k].toString());
									}else{						
										_data[j+1][k]=impdate[j][k];
									}
								}else {
									_data[j+1][k]=impdate[j][k];
								}
								
							}
						}
						 excel.setDataToExcelFile(_data,str_path2+"\\"+ZZLUIUtil.getDeptName(imp[i][1].toString())+"_���׸�.xls");

					}
					}catch(Exception  e){
						e.printStackTrace();
					}
				}
			});
			MessageBox.show(this,"������"+count+"�����ŵ׸�");
		} catch (Exception e1) {
			MessageBox.show(this,"����ʧ��");
			e1.printStackTrace();
		}

	}

	/**
	 * ��������֪ͨ��
	 */
	private void impzgword() {
		BillVO[] bo = list.getAllBillVOs();
		if (bo.length <= 0) {
			MessageBox.show(this, "���Ȳ�ѯ");
			return;
		}
		if (deptid == null) {
			MessageBox.show(this, "��ѡ���Ų�ѯ����");
			return;
		}
		if (schemeid == null) {
			MessageBox.show(this, "��ѡ����Ŀ���Ʋ�ѯ����");
			return;
		}
		BillCardDialog listDialog = new BillCardDialog(this,
				"Ϊ��������������ʵ�����ѡ���ϴμ�����Ŀ����", "CK_PROBLEM_INFO_ZZL_EQ1_2", 500,
				200);
		listDialog.setVisible(true);
		BillVO vos = listDialog.getBillcardPanel().getBillVO();
		String scschemeid = vos.getStringValue("schemeid");

		StringBuffer sbID = new StringBuffer();
		for (int i = 0; i < bo.length; i++) {
			sbID.append(bo[i].getStringValue("id"));
			sbID.append(",");
		}
		String infoID = sbID.toString().substring(0, sbID.length() - 1);
		try {
			vo = UIUtil.getHashVoArrayByDS(null,
					"select * from ck_problem_info where 1=1 and id in("
							+ infoID + ") and schemeid='" + schemeid
							+ "' group by parentid");
			final JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new File("."));
			fc.setDialogTitle("��ѡ��Ҫ���浽��Ŀ¼");
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setAcceptAllFileFilterUsed(false);
			int result = fc.showSaveDialog(this);
			if (result != JFileChooser.APPROVE_OPTION) { // �������ȷ����
				return;
			}
			String savePath = fc.getSelectedFile().getPath();
			Map<Object, Object> dataMap = new HashMap<Object, Object>();
			HashVO[] SCHEME = UIUtil.getHashVoArrayByDS(null,
					"select * from CK_SCHEME where id ='" + schemeid + "'");
			String ftl = null;
			String title0 = SCHEME[0].getStringValue("PLANTYPE");
			if (title0.equals("��Ƽ��")) {
				ftl = "WordAbarbeitungModel";
			} else {
				ftl = "WordAbarbeitungModel2";
				if (title0.equals("���ɼ��")) {
					dataMap.put("title", "���ɼ��");
				} else {
					dataMap.put("title", "�Ϲ�");
				}
			}
			dataMap.put("yaer", SCHEME[0].getStringValue("CREATEDATE")
					.substring(0, 4));
			dataMap.put("code", SCHEME[0].getStringValue("code"));
			String deptname = UIUtil.getStringValueByDS(null,
					"select name from pub_corp_dept where id='" + deptid + "'");
			dataMap.put("dept", deptname);
			StringBuilder sb = new StringBuilder();
			int a = 0;
			for (int i = 0; i < vo.length; i++) {
				a = a + 1;
				String num = stringNumber(a);
				String title = UIUtil.getStringValueByDS(null,
						"select checkpoints from V_CK_SCHEME_IMPLEMENT where id ='"
								+ vo[i].getStringValue("parentid") + "'");
				String parentid = UIUtil.getStringValueByDS(null,
						"select id from V_CK_SCHEME_IMPLEMENT where id ='"
								+ vo[i].getStringValue("parentid") + "'");
				sb.append("��" + num + "��").append("\r");
				sb.append(title + "\r\n");
				String[][] problem = UIUtil
						.getStringArrayByDS(
								null,
								"select problemInfo,adjustuserid,suggest,mainadjustuserid from ck_problem_info where parentid='"
										+ parentid + "'");
				int b = 0;
				for (int j = 0; j < problem.length; j++) {
					b = b + 1;
					sb.append(b + ".");
					sb.append(problem[j][0].toString() + "\r");
					if (problem[j][1] == null || problem[j][1].equals(null)
							|| problem[j][1].equals(" ")
							|| problem[j][1].equals("")
							|| problem[j][1].equals("null")) {
					} else {
						String zrname = ZZLUIUtil.getnamesSplit(problem[j][1]
								.toString());
						sb.append("�����ˣ�" + zrname + "��" + "\n");
					}
					if (problem[j][3] == null || problem[j][3].equals(null)
							|| problem[j][3].equals(" ")
							|| problem[j][3].equals("")
							|| problem[j][3].equals("null")) {
					} else {
						String zrname = ZZLUIUtil.getUserName(problem[j][3]
								.toString());
						sb.append("���������ˣ�" + zrname + "��" + "\n");
					}
					if (problem[j][2] == null || problem[j][2].equals(null)
							|| problem[j][2].equals(" ")
							|| problem[j][2].equals("")
							|| problem[j][2].equals("null")) {

					} else {
						sb.append("���Ľ��飺" + problem[j][2].toString() + "��");
					}
					if (scschemeid != null) {
						if (i == vo.length - 1) {
							HashVO[] wzgproblemInfo = UIUtil
									.getHashVoArrayByDS(null,
											"select * from ck_problem_info where  1=1  and trackresult='δ����' and deptid='"
													+ deptid
													+ "' and schemeid='"
													+ scschemeid + "'");
							HashVO[] yzgproblemInfo = UIUtil
									.getHashVoArrayByDS(null,
											"select * from ck_problem_info where  1=1  and trackresult='������' and deptid='"
													+ deptid
													+ "' and schemeid='"
													+ scschemeid + "'");
							HashVO[] bfzgproblemInfo = UIUtil
									.getHashVoArrayByDS(null,
											"select * from ck_problem_info where  1=1 and trackresult='��������' and deptid='"
													+ deptid
													+ "' and schemeid='"
													+ scschemeid + "'");
							HashVO[] allproblemInfo = UIUtil
									.getHashVoArrayByDS(null,
											"select * from ck_problem_info where  deptid='"
													+ deptid
													+ "' and schemeid='"
													+ scschemeid + "'");
							if (wzgproblemInfo.length > 0
									|| bfzgproblemInfo.length > 0) {
								String num2 = stringNumber(vo.length + 1);
								sb.append("��" + num2 + "�� ����������ʵ�����").append(
										"\r\n");
								String[] time = allproblemInfo[0]
										.getStringValue("createdate")
										.split("-");
								sb.append(time[0].toString() + "��"
										+ time[1].toString() + "��"
										+ time[2].toString() + "�ռ��������⹲��"
										+ allproblemInfo.length + "��,");
								sb.append("������" + yzgproblemInfo.length + "��,");
								sb.append("δ����" + wzgproblemInfo.length + "��,");
								sb.append("��������" + bfzgproblemInfo.length
										+ "�");
								int wnum = 0;
								for (int w = 0; w < wzgproblemInfo.length; w++) {
									wnum = wnum + 1;
									String wtitle = UIUtil
											.getStringValueByDS(
													null,
													"select checkpoints from V_CK_SCHEME_IMPLEMENT where id ='"
															+ wzgproblemInfo[w]
																	.getStringValue("parentid")
															+ "'");
									String wname = ZZLUIUtil
											.getnamesSplit(wzgproblemInfo[w]
													.getStringValue("adjustuserid"));
									// String
									// wname=UIUtil.getStringValueByDS(null,"select name from pub_user where id='"+wzgproblemInfo[w].getStringValue("adjustuserid")+"'");
									sb.append(wnum + ". " + wtitle
											+ ": ��δ���ġ�������:" + wname + "��\r");
								}
								for (int f = 0; f < bfzgproblemInfo.length; f++) {
									wnum = wnum + 1;
									String ftitle = UIUtil
											.getStringValueByDS(
													null,
													"select checkpoints from V_CK_SCHEME_IMPLEMENT where id ='"
															+ bfzgproblemInfo[f]
																	.getStringValue("parentid")
															+ "'");
									// String
									// fname=UIUtil.getStringValueByDS(null,"select name from pub_user where id='"+bfzgproblemInfo[f].getStringValue("adjustuserid")+"'");
									String fname = ZZLUIUtil
											.getnamesSplit(bfzgproblemInfo[f]
													.getStringValue("adjustuserid"));
									sb.append(wnum + ". " + ftitle + ": "
											+ "��������" + "��������:" + fname + "��");
								}

							}
						}
					}
				}
			}
			dataMap.put("problem", sb);
			String name = UIUtil.getStringValueByDS(null,
					"select leader from CK_MEMBER_WORK where schemeid='"
							+ schemeid + "' and checkeddept like '%;" + deptid
							+ ";%'");
			String username = UIUtil.getStringValueByDS(null,
					"select name from pub_user where id='" + name + "'");
			dataMap.put("name", username);
			String dept2 = UIUtil.getStringValueByDS(null,
					"select CHECKDEPT from CK_PLAN where id ='"
							+ SCHEME[0].getStringValue("planid") + "'");
			String deptname2 = UIUtil.getStringValueByDS(null,
					"select name from pub_corp_dept where id='" + dept2 + "'");
			dataMap.put("dept2", deptname2);
			savePath = savePath + "\\" + dataMap.get("dept") + "_"
					+ SCHEME[0].getStringValue("name") + "-��������֪ͨ��.doc";
			WordExport we = new WordExport();
			we.createDoc(dataMap, ftl, savePath);
			MessageBox.show(this, "�����ɹ�");
			String[][] ck_record = UIUtil.getStringArrayByDS(null,
					"select id,abarbeitungname from ck_record where userid='"
							+ loginUserid + "' and deptid='"
							+ vo[0].getStringValue("deptid")
							+ "' and schemeid='"
							+ vo[0].getStringValue("schemeid") + "'");
			String implid = UIUtil.getStringValueByDS(null,
					"select implid from V_CK_SCHEME_IMPLEMENT where deptid='"
							+ vo[0].getStringValue("deptid")
							+ "' and schemeid='"
							+ vo[0].getStringValue("schemeid") + "'");
			if (ck_record.length <= 0) {
				InsertSQLBuilder insert = new InsertSQLBuilder("ck_record");
				String id = ICheckUIUtil.getSequenceNextVal();
				insert.putFieldValue("id", id);
				insert.putFieldValue("deptid", vo[0].getStringValue("deptid"));
				insert.putFieldValue("userid", loginUserid);
				insert.putFieldValue("userid", loginUserid);
				insert.putFieldValue("implid", implid);
				insert.putFieldValue("schemeid", vo[0]
						.getStringValue("schemeid"));
				UIUtil.executeUpdateByDS(null, insert.getSQL());
			} else if (ck_record.length == 1 || ck_record[0][1] == null
					|| ck_record[0][1].equals("")
					|| ck_record[0][1].equals(null)
					|| ck_record[0][1].equals("null")) {
				UpdateSQLBuilder update = new UpdateSQLBuilder("ck_record");
				update.setWhereCondition("id=" + ck_record[0][0]);
				update.putFieldValue("abarbeitungname", "Y");
				update.putFieldValue("userid", loginUserid);
				update.putFieldValue("implid", implid);
				update.putFieldValue("schemeid", vo[0]
						.getStringValue("schemeid"));
				UIUtil.executeUpdateByDS(null, update.getSQL());
			}
			String[] count = UIUtil
					.getStringArrayFirstColByDS(
							null,
							"select * from ck_problem_info where probleminfo is null and trackresult is null");
			if (count.length > 0) {
				UIUtil
						.executeUpdateByDS(null,
								"delete from ck_problem_info where probleminfo is null and trackresult is null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * �Ŵ�����֪ͨ��
	 */
	private void impxdzgword() {
		BillVO[] bo = list.getAllBillVOs();
		if (bo.length <= 0) {
			MessageBox.show(this, "���Ȳ�ѯ");
			return;
		}
		if (deptid == null) {
			MessageBox.show(this, "��ѡ���Ų�ѯ����");
			return;
		}
		if (schemeid == null) {
			MessageBox.show(this, "��ѡ����Ŀ���Ʋ�ѯ����");
			return;
		}
		BillCardDialog listDialog = new BillCardDialog(this,
				"Ϊ��������������ʵ�����ѡ���ϴμ�����Ŀ����", "CK_PROBLEM_INFO_ZZL_EQ1_2", 500,
				200);
		listDialog.setVisible(true);
		BillVO vos = listDialog.getBillcardPanel().getBillVO();
		String scschemeid = vos.getStringValue("schemeid");

		StringBuffer sbID = new StringBuffer();
		for (int i = 0; i < bo.length; i++) {
			sbID.append(bo[i].getStringValue("id"));
			sbID.append(",");
		}
		String infoID = sbID.toString().substring(0, sbID.length() - 1);
		try {
			vo = UIUtil.getHashVoArrayByDS(null,
					"select * from ck_problem_info where 1=1 and id in("
							+ infoID + ") and schemeid='" + schemeid
							+ "' group by parentid");
			final JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new File("."));
			fc.setDialogTitle("��ѡ��Ҫ���浽��Ŀ¼");
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setAcceptAllFileFilterUsed(false);
			int result = fc.showSaveDialog(this);
			if (result != JFileChooser.APPROVE_OPTION) { // �������ȷ����
				return;
			}
			String savePath = fc.getSelectedFile().getPath();
			Map<Object, Object> dataMap = new HashMap<Object, Object>();
			HashVO[] SCHEME = UIUtil.getHashVoArrayByDS(null,
					"select * from CK_SCHEME where id ='" + schemeid + "'");
			dataMap.put("yaer", SCHEME[0].getStringValue("CREATEDATE")
					.substring(0, 4));
			dataMap.put("code", SCHEME[0].getStringValue("code"));
			String deptname = UIUtil.getStringValueByDS(null,
					"select name from pub_corp_dept where id='" + deptid + "'");
			dataMap.put("dept", deptname);
			StringBuilder sb = new StringBuilder();
			HashVO[] implvo = UIUtil.getHashVoArrayByDS(null,
					"select * from ck_problem_info where   schemeid='"
							+ schemeid + "' and deptid='" + deptid
							+ "' and checkmode='�ֳ����'  group by implid");
			int imp = 0;
			for (int vo = 0; vo < implvo.length; vo++) {
				HashVO[] ipvo = UIUtil.getHashVoArrayByDS(null,
						"select * from CK_SCHEME_IMPL where id='"
								+ implvo[vo].getStringValue("implid")
								+ "' or refimplid='"
								+ implvo[vo].getStringValue("implid")
								+ "' order by refimplid");
				String money = UIUtil.getStringValueByDS(null,
						"select sum(c8)/10000 from CK_SCHEME_IMPL where id='"
								+ implvo[vo].getStringValue("implid")
								+ "' or refimplid='"
								+ implvo[vo].getStringValue("implid")
								+ "' order by refimplid");
				imp = imp + 1;
				String impnum = stringNumber(imp);
				sb.append(impnum).append("��");
				if (ipvo.length != 1) {
					sb.append("�����"
							+ ipvo[0].getStringValue("c1")
							+ "����С����"
							+ ZZLUIUtil.getTimeString(ipvo[0]
									.getStringValue("c6")) + "��"
							+ ipvo[0].getStringValue("c16"));
					sb.append("������������" + money + "��Ԫ������"
							+ ipvo[0].getStringValue("c18"));
					sb.append("�����У�");
					for (int p = 0; p < ipvo.length; p++) {
						sb.append(ipvo[p].getStringValue("c1")
								+ "����"
								+ Double.parseDouble(ipvo[p]
										.getStringValue("c8")) / 10000
								+ "��Ԫ���������ʣ�");
						String strdb = ipvo[p].getStringValue("c11");
						strdb = strdb.substring(0, strdb.indexOf("."));
						int idb = Integer.parseInt(strdb);
						if (idb == 0) {
							Double db = Double.parseDouble(ipvo[p]
									.getStringValue("c11"));
							db = db * 1000;
							sb.append(db + "�룻");
						} else {
							sb.append(ipvo[p].getStringValue("c11") + "�룻");
						}
					}
					sb.append("������"
							+ ZZLUIUtil.getTimeString(ipvo[0]
									.getStringValue("c7")) + "����");
				} else {
					sb.append("�����"
							+ ipvo[0].getStringValue("c1")
							+ "��"
							+ ZZLUIUtil.getTimeString(ipvo[0]
									.getStringValue("c6")) + "��"
							+ ipvo[0].getStringValue("c16"));
					sb.append("��������" + money + "��Ԫ������"
							+ ipvo[0].getStringValue("c18"));
					sb.append("��"
							+ ZZLUIUtil.getTimeString(ipvo[0]
									.getStringValue("c7")) + "���ڣ���������");
					String strdb = ipvo[0].getStringValue("c11");
					strdb = strdb.substring(0, strdb.indexOf("."));
					int idb = Integer.parseInt(strdb);
					if (idb == 0) {
						Double db = Double.parseDouble(ipvo[0]
								.getStringValue("c11"));
						db = db * 1000;
						sb.append(db + "�룻");
					} else {
						sb.append(ipvo[0].getStringValue("c11") + "�룻");
					}

				}
				if (ipvo[0].getStringValue("usera1") != null) {
					sb.append("����������A�ǣ���"
							+ ZZLUIUtil.getnamesSplit(ipvo[0]
									.getStringValue("usera1")) + "��");
				}
				if (ipvo[0].getStringValue("userb1") != null) {
					sb.append("����������B�ǣ���"
							+ ZZLUIUtil.getnamesSplit(ipvo[0]
									.getStringValue("userb1")) + "��");
				}
				if (ipvo[0].getStringValue("usera2") != null) {
					sb.append("���������A�ǣ���"
							+ ZZLUIUtil.getnamesSplit(ipvo[0]
									.getStringValue("usera2")) + "��");
				}
				if (ipvo[0].getStringValue("userb2") != null) {
					sb.append("���������B�ǣ���"
							+ ZZLUIUtil.getnamesSplit(ipvo[0]
									.getStringValue("userb2")) + "��");
				}
				if (ipvo[0].getStringValue("usera3") != null) {
					sb.append("����������A�ǣ�"
							+ ZZLUIUtil.getnamesSplit(ipvo[0]
									.getStringValue("usera3")) + "��");
				}
				if (ipvo[0].getStringValue("userb3") != null) {
					sb.append("����������B�ǣ���"
							+ ZZLUIUtil.getnamesSplit(ipvo[0]
									.getStringValue("userb3")) + "��");
				}
				if (ipvo[0].getStringValue("usera4") != null) {
					sb.append("��Ӫ������A�ǣ���"
							+ ZZLUIUtil.getnamesSplit(ipvo[0]
									.getStringValue("usera4")) + "��");
				}
				if (ipvo[0].getStringValue("userb4") != null) {
					sb.append("��Ӫ������B�ǣ���"
							+ ZZLUIUtil.getnamesSplit(ipvo[0]
									.getStringValue("userb4")) + "��");
				}
				HashVO[] voIMPLEMENT = UIUtil.getHashVoArrayByDS(null,
						"select * from V_CK_SCHEME_IMPLEMENT where 1=1  and implid = '"
								+ implvo[vo].getStringValue("implid")
								+ "' and deptid='"
								+ implvo[vo].getStringValue("deptid")
								+ "' group by thirdname order by id");
				int a = 0;
				for (int i = 0; i < voIMPLEMENT.length; i++) {
					HashVO[] voct = UIUtil
							.getHashVoArrayByDS(
									null,
									"select * from V_CK_SCHEME_IMPLEMENT where 1=1  and implid = '"
											+ implvo[vo]
													.getStringValue("implid")
											+ "' and deptid='"
											+ implvo[vo]
													.getStringValue("deptid")
											+ "' and thirdname='"
											+ voIMPLEMENT[i]
													.getStringValue("thirdname")
											+ "' and result is not null and result='��Ч'");
					if (voct.length > 0) {
						a = a + 1;
						String num = stringNumber(a);
						sb.append("��" + num + "��").append("\r");
						sb.append(voIMPLEMENT[i].getStringValue("thirdname")
								+ "\r\n");
						int b = 0;
						for (int c = 0; c < voct.length; c++) {
							b = b + 1;
							sb.append(b + "��");
							sb.append(voct[c].getStringValue("dictname"));
							HashVO[] problem = UIUtil.getHashVoArrayByDS(null,
									"select * from ck_problem_info where parentid='"
											+ voct[c].getStringValue("id")
											+ "'");
							String str = String.valueOf(b);
							Double b1 = Double.parseDouble(str);
							DecimalFormat df = new DecimalFormat("######.0");
							for (int j = 0; j < problem.length; j++) {
								b1 = b1 + 0.1;
								String str1 = df.format(b1);
								sb.append("��" + str1 + "����");
								sb.append(problem[j]
										.getStringValue("problemInfo")
										+ "��");
								if (problem[j].getStringValue("adjustuserid") != null) {
									String zrname = ZZLUIUtil
											.getnamesSplit(problem[j]
													.getStringValue("adjustuserid"));
									sb.append("�����ˣ�" + zrname + "��");

								}
								if (problem[j]
										.getStringValue("mainadjustuserid") != null) {
									String zrname = ZZLUIUtil
											.getUserName(problem[j]
													.getStringValue("mainadjustuserid"));
									sb.append("���������ˣ�" + zrname + "��");

								}
							}
						}
					}
				}
				if (scschemeid != null) {
					if (vo == implvo.length - 1) {
						HashVO[] wzgproblemInfo = UIUtil.getHashVoArrayByDS(
								null,
								"select * from ck_problem_info where  1=1  and trackresult='δ����' and deptid='"
										+ deptid + "' and schemeid='"
										+ scschemeid + "'");
						HashVO[] yzgproblemInfo = UIUtil.getHashVoArrayByDS(
								null,
								"select * from ck_problem_info where  1=1  and trackresult='������' and deptid='"
										+ deptid + "' and schemeid='"
										+ scschemeid + "'");
						HashVO[] bfzgproblemInfo = UIUtil.getHashVoArrayByDS(
								null,
								"select * from ck_problem_info where  1=1 and trackresult='��������' and deptid='"
										+ deptid + "' and schemeid='"
										+ scschemeid + "'");
						HashVO[] allproblemInfo = UIUtil.getHashVoArrayByDS(
								null,
								"select * from ck_problem_info where  deptid='"
										+ deptid + "' and schemeid='"
										+ scschemeid + "'");
						if (wzgproblemInfo.length > 0
								|| bfzgproblemInfo.length > 0) {
							String num2 = stringNumber(implvo.length + 1);
							sb.append(num2 + "������������ʵ�����").append("\r\n");
							if (yzgproblemInfo.length > 0) {
								String[] time = yzgproblemInfo[0]
										.getStringValue("createdate")
										.split("-");
								sb.append(time[0].toString() + "��"
										+ time[1].toString() + "��"
										+ time[2].toString() + "�ռ��������⹲��"
										+ allproblemInfo.length + "��,");
							}
							sb.append("������" + yzgproblemInfo.length + "��,");
							sb.append("δ����" + wzgproblemInfo.length + "��,");
							sb.append("��������" + bfzgproblemInfo.length + "�");
							int wnum = 0;
							for (int w = 0; w < wzgproblemInfo.length; w++) {
								wnum = wnum + 1;
								String wtitle = UIUtil
										.getStringValueByDS(
												null,
												"select checkpoints from V_CK_SCHEME_IMPLEMENT where id ='"
														+ wzgproblemInfo[w]
																.getStringValue("parentid")
														+ "'");
								// String
								// wname=UIUtil.getStringValueByDS(null,"select name from pub_user where id='"+wzgproblemInfo[w].getStringValue("adjustuserid")+"'");
								String wname = ZZLUIUtil
										.getnamesSplit(wzgproblemInfo[w]
												.getStringValue("adjustuserid"));
								sb.append(wnum + ". " + wtitle + ": ��δ���ġ�������:"
										+ wname + "��\r");
							}
							for (int f = 0; f < bfzgproblemInfo.length; f++) {
								wnum = wnum + 1;
								String ftitle = UIUtil
										.getStringValueByDS(
												null,
												"select checkpoints from V_CK_SCHEME_IMPLEMENT where id ='"
														+ bfzgproblemInfo[f]
																.getStringValue("parentid")
														+ "'");
								// String
								// fname=UIUtil.getStringValueByDS(null,"select name from pub_user where id='"+bfzgproblemInfo[f].getStringValue("adjustuserid")+"'");
								String fname = ZZLUIUtil
										.getnamesSplit(bfzgproblemInfo[f]
												.getStringValue("adjustuserid"));
								sb.append(wnum + ". " + ftitle + ": " + "��������"
										+ "��������:" + fname + "��");
							}

						}
					}
				}
			}
			dataMap.put("problem", sb);
			String name = UIUtil.getStringValueByDS(null,
					"select leader from CK_MEMBER_WORK where schemeid='"
							+ schemeid + "' and checkeddept like '%;" + deptid
							+ ";%'");
			String username = UIUtil.getStringValueByDS(null,
					"select name from pub_user where id='" + name + "'");
			dataMap.put("name", username);
			String dept2 = UIUtil.getStringValueByDS(null,
					"select CHECKDEPT from CK_PLAN where id ='"
							+ SCHEME[0].getStringValue("planid") + "'");
			String deptname2 = UIUtil.getStringValueByDS(null,
					"select name from pub_corp_dept where id='" + dept2 + "'");
			dataMap.put("dept2", deptname2);
			savePath = savePath + "\\" + dataMap.get("dept") + "_"
					+ SCHEME[0].getStringValue("name") + "-��������֪ͨ��.doc";
			WordExport we = new WordExport();
			we.createDoc(dataMap, "WordAbarbeitungModel", savePath);
			MessageBox.show(this, "�����ɹ�");
			String[][] ck_record = UIUtil.getStringArrayByDS(null,
					"select id,abarbeitungname from ck_record where userid='"
							+ loginUserid + "' and deptid='"
							+ vo[0].getStringValue("deptid")
							+ "' and schemeid='"
							+ vo[0].getStringValue("schemeid") + "'");
			String implid = UIUtil.getStringValueByDS(null,
					"select implid from V_CK_SCHEME_IMPLEMENT where deptid='"
							+ vo[0].getStringValue("deptid")
							+ "' and schemeid='"
							+ vo[0].getStringValue("schemeid") + "'");
			if (ck_record.length <= 0) {
				InsertSQLBuilder insert = new InsertSQLBuilder("ck_record");
				String id = ICheckUIUtil.getSequenceNextVal();
				insert.putFieldValue("id", id);
				insert.putFieldValue("deptid", vo[0].getStringValue("deptid"));
				insert.putFieldValue("userid", loginUserid);
				insert.putFieldValue("abarbeitungname", "Y");
				insert.putFieldValue("implid", implid);
				insert.putFieldValue("schemeid", vo[0]
						.getStringValue("schemeid"));
				UIUtil.executeUpdateByDS(null, insert.getSQL());
			} else if (ck_record.length == 1 || ck_record[0][1] == null
					|| ck_record[0][1].equals("")
					|| ck_record[0][1].equals(null)
					|| ck_record[0][1].equals("null")) {
				UpdateSQLBuilder update = new UpdateSQLBuilder("ck_record");
				update.setWhereCondition("id=" + ck_record[0][0]);
				update.putFieldValue("abarbeitungname", "Y");
				update.putFieldValue("userid", loginUserid);
				update.putFieldValue("implid", implid);
				update.putFieldValue("schemeid", vo[0]
						.getStringValue("schemeid"));
				UIUtil.executeUpdateByDS(null, update.getSQL());
			}
			String[] count = UIUtil
					.getStringArrayFirstColByDS(
							null,
							"select * from ck_problem_info where probleminfo is null and trackresult is null");
			if (count.length > 0) {
				UIUtil
						.executeUpdateByDS(null,
								"delete from ck_problem_info where probleminfo is null and trackresult is null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * �����ֳ������ʵȷ����
	 */
	private void impword() {
		BillVO[] bo = list.getAllBillVOs();
		if (bo.length <= 0) {
			MessageBox.show(this, "���Ȳ�ѯ");
			return;
		}
		if (deptid == null) {
			MessageBox.show(this, "��ѡ���Ų�ѯ����");
			return;
		}
		if (schemeid == null) {
			MessageBox.show(this, "��ѡ����Ŀ���Ʋ�ѯ����");
			return;
		}
		BillCardDialog listDialog = new BillCardDialog(this,
				"Ϊ��������������ʵ�����ѡ���ϴμ�����Ŀ����", "CK_PROBLEM_INFO_ZZL_EQ1_2", 500,
				200);
		listDialog.setVisible(true);
		BillVO vos = listDialog.getBillcardPanel().getBillVO();
		String scschemeid = vos.getStringValue("schemeid");
		StringBuffer sbID = new StringBuffer();
		for (int i = 0; i < bo.length; i++) {
			sbID.append(bo[i].getStringValue("id"));
			sbID.append(",");
		}
		String infoID = sbID.toString().substring(0, sbID.length() - 1);
		try {
			vo = UIUtil.getHashVoArrayByDS(null,
					"select * from ck_problem_info where 1=1 and id in("
							+ infoID + ") and schemeid='" + schemeid
							+ "' and checkmode='" + "�ֳ����"
							+ "' group by parentid");
			final JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new File("."));
			fc.setDialogTitle("��ѡ��Ҫ���浽��Ŀ¼");
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setAcceptAllFileFilterUsed(false);
			int result = fc.showSaveDialog(this);
			if (result != JFileChooser.APPROVE_OPTION) { // �������ȷ����
				return;
			}
			HashVO[] zlvo = null;
			String state = null;
			if (vo[0].getStringValue("schemeid") != null) {
				zlvo = UIUtil.getHashVoArrayByDS(null,
						"select * from CK_SCHEME where id='"
								+ vo[0].getStringValue("schemeid") + "'");
				if (zlvo.length > 0
						&& zlvo[0].getStringValue("PLANTYPE") != null) {
					if (zlvo[0].getStringValue("PLANTYPE").equals("���ɼ��")) {
						state = "WordZlConfirmModel";
					} else {
						state = "WordFactModel";
					}
				} else {
					state = "WordFactModel";
				}
			}
			String savePath = fc.getSelectedFile().getPath();
			Map<Object, Object> dataMap = new HashMap<Object, Object>();
			String dept = UIUtil.getStringValueByDS(null,
					"select name from pub_corp_dept where id='"
							+ vo[0].getStringValue("deptid") + "'");
			dataMap.put("dept", dept);
			String[] timea = vo[0].getStringValue("createdate").split("-");
			dataMap.put("time", timea[0].toString() + "��" + timea[1].toString()
					+ "��" + timea[2].toString() + "��");
			String planname = UIUtil.getStringValueByDS(null,
					"select name from CK_SCHEME where id='"
							+ vo[0].getStringValue("schemeid") + "'");
			String zltime = null;
			if (zlvo.length > 0
					&& zlvo[0].getStringValue("PLANBEGINDATE") != null
					&& zlvo[0].getStringValue("PLANENDDATE") != null) {
				zltime = ZZLUIUtil.getTimeString(zlvo[0]
						.getStringValue("PLANBEGINDATE"))
						+ "��"
						+ ZZLUIUtil.getTimeString(zlvo[0]
								.getStringValue("PLANENDDATE"));
			}
			dataMap.put("zltime", zltime);
			String txdept = null;
			if (zlvo.length > 0 && zlvo[0].getStringValue("CHECKDEPT") != null) {
				txdept = ZZLUIUtil.getdeptsSplit(zlvo[0]
						.getStringValue("CHECKDEPT"));
			}
			dataMap.put("txdept", txdept);
			dataMap.put("planname", planname);
			StringBuilder sb = new StringBuilder();
			int a = 0;
			for (int i = 0; i < vo.length; i++) {
				a = a + 1;
				String num = stringNumber(a);
				String title = UIUtil.getStringValueByDS(null,
						"select checkpoints from V_CK_SCHEME_IMPLEMENT where id ='"
								+ vo[i].getStringValue("parentid") + "'");
				String parentid = UIUtil.getStringValueByDS(null,
						"select id from V_CK_SCHEME_IMPLEMENT where id ='"
								+ vo[i].getStringValue("parentid") + "'");
				sb.append("��" + num + "��").append("\r");
				sb.append(title + "\r\n");
				String[][] problem = UIUtil
						.getStringArrayByDS(
								null,
								"select problemInfo,adjustuserid,mainadjustuserid from ck_problem_info where parentid='"
										+ parentid + "'");
				int b = 0;
				for (int j = 0; j < problem.length; j++) {
					b = b + 1;
					sb.append(b + ".");
					sb.append(problem[j][0].toString() + "\n");
					if (problem[j][1] == null || problem[j][1].equals(null)
							|| problem[j][1].equals(" ")
							|| problem[j][1].equals("")
							|| problem[j][1].equals("null")) {
					} else {
						String zrname = ZZLUIUtil.getnamesSplit(problem[j][1]
								.toString());
						sb.append("�����ˣ�" + zrname + "��" + "\n");
					}
					if (problem[j][2] == null || problem[j][2].equals(null)
							|| problem[j][2].equals(" ")
							|| problem[j][2].equals("")
							|| problem[j][2].equals("null")) {
					} else {
						String zrname = ZZLUIUtil.getUserName(problem[j][2]
								.toString());
						sb.append("���������ˣ�" + zrname + "��" + "\n");
					}

				}
				if (scschemeid != null) {
					if (i == vo.length - 1) {
						HashVO[] wzgproblemInfo = UIUtil.getHashVoArrayByDS(
								null,
								"select * from ck_problem_info where  1=1  and trackresult='δ����' and deptid='"
										+ deptid + "' and schemeid='"
										+ scschemeid + "'");
						HashVO[] yzgproblemInfo = UIUtil.getHashVoArrayByDS(
								null,
								"select * from ck_problem_info where  1=1  and trackresult='������' and deptid='"
										+ deptid + "' and schemeid='"
										+ scschemeid + "'");
						HashVO[] bfzgproblemInfo = UIUtil.getHashVoArrayByDS(
								null,
								"select * from ck_problem_info where  1=1 and trackresult='��������' and deptid='"
										+ deptid + "' and schemeid='"
										+ scschemeid + "'");
						HashVO[] allproblemInfo = UIUtil.getHashVoArrayByDS(
								null,
								"select * from ck_problem_info where  deptid='"
										+ deptid + "' and schemeid='"
										+ scschemeid + "'");
						if (wzgproblemInfo.length > 0
								|| bfzgproblemInfo.length > 0) {
							String num2 = stringNumber(vo.length + 1);
							sb.append("��" + num2 + "�� ����������ʵ�����")
									.append("\r\n");
							String[] time = allproblemInfo[0].getStringValue(
									"createdate").split("-");
							sb.append(time[0].toString() + "��"
									+ time[1].toString() + "��"
									+ time[2].toString() + "�ռ��������⹲��"
									+ allproblemInfo.length + "��,");
							sb.append("������" + yzgproblemInfo.length + "��,");
							sb.append("δ����" + wzgproblemInfo.length + "��,");
							sb.append("��������" + bfzgproblemInfo.length + "�");
							int wnum = 0;
							for (int w = 0; w < wzgproblemInfo.length; w++) {
								wnum = wnum + 1;
								String wtitle = UIUtil
										.getStringValueByDS(
												null,
												"select checkpoints from V_CK_SCHEME_IMPLEMENT where id ='"
														+ wzgproblemInfo[w]
																.getStringValue("parentid")
														+ "'");
								// String
								// wname=UIUtil.getStringValueByDS(null,"select name from pub_user where id='"+wzgproblemInfo[w].getStringValue("adjustuserid")+"'");
								String wname = ZZLUIUtil
										.getnamesSplit(wzgproblemInfo[w]
												.getStringValue("adjustuserid"));
								sb.append(wnum + ". " + wtitle + ": ��δ���ġ�������:"
										+ wname + "��\r");
							}
							for (int f = 0; f < bfzgproblemInfo.length; f++) {
								wnum = wnum + 1;
								String ftitle = UIUtil
										.getStringValueByDS(
												null,
												"select checkpoints from V_CK_SCHEME_IMPLEMENT where id ='"
														+ bfzgproblemInfo[f]
																.getStringValue("parentid")
														+ "'");
								// String
								// fname=UIUtil.getStringValueByDS(null,"select name from pub_user where id='"+bfzgproblemInfo[f].getStringValue("adjustuserid")+"'");
								String fname = ZZLUIUtil
										.getnamesSplit(bfzgproblemInfo[f]
												.getStringValue("adjustuserid"));
								sb.append(wnum + ". " + ftitle + ": " + "��������"
										+ "��������:" + fname + "��");
							}

						}
					}
				}
			}

			dataMap.put("problem", sb);
			String[][] names = UIUtil.getStringArrayByDS(null,
					"select leader2,teamusers from V_CK_SCHEME where deptid = '"
							+ vo[0].getStringValue("deptid")
							+ "' and schemeid = '"
							+ vo[0].getStringValue("schemeid") + "'");
			String leader2ID = names[0][0].toString();
			String teamusersID = names[0][1].toString();
			StringBuilder sb2 = new StringBuilder();
			String leader2 = UIUtil.getStringValueByDS(null,
					"select name from pub_user where id='" + leader2ID + "'");
			sb2.append(leader2 + "\r");
			teamusersID = teamusersID.replace(";", ",");
			teamusersID = teamusersID.substring(1, teamusersID.length() - 1);
			String[] teamusers = UIUtil.getStringArrayFirstColByDS(null,
					"select name from pub_user where id in(" + teamusersID
							+ ")");
			StringBuilder zlsb = new StringBuilder();
			for (int i = 0; i < teamusers.length; i++) {
				sb2.append(teamusers[i].toString() + "\r");
				zlsb.append(teamusers[i].toString() + "\r");
			}
			dataMap.put("names", sb2);
			dataMap.put("name1", leader2);
			dataMap.put("name2", zlsb);
			String[] timeb = vo[0].getStringValue("createdate").split("-");
			dataMap.put("time2", timeb[0].toString() + "��"
					+ timeb[1].toString() + "��" + timeb[2].toString() + "��");
			savePath = savePath + "\\" + dataMap.get("dept") + "-" + planname
					+ "_�ֳ������ʵȷ����.doc";
			WordExport we = new WordExport();
			we.createDoc(dataMap, state, savePath);
			MessageBox.show(this, "�����ɹ�");
			String[][] ck_record = UIUtil.getStringArrayByDS(null,
					"select id,confirmname from ck_record where deptid='"
							+ vo[0].getStringValue("deptid")
							+ "' and schemeid='"
							+ vo[0].getStringValue("schemeid") + "'");
			String implid = UIUtil.getStringValueByDS(null,
					"select implid from V_CK_SCHEME_IMPLEMENT where deptid='"
							+ vo[0].getStringValue("deptid")
							+ "' and schemeid='"
							+ vo[0].getStringValue("schemeid") + "'");
			if (ck_record.length <= 0) {
				InsertSQLBuilder insert = new InsertSQLBuilder("ck_record");
				String id = ICheckUIUtil.getSequenceNextVal();
				insert.putFieldValue("id", id);
				insert.putFieldValue("deptid", vo[0].getStringValue("deptid"));
				insert.putFieldValue("userid", loginUserid);
				insert.putFieldValue("confirmname", "Y");
				insert.putFieldValue("schemeid", vo[0]
						.getStringValue("schemeid"));
				insert.putFieldValue("implid", implid);
				UIUtil.executeUpdateByDS(null, insert.getSQL());
			} else if (ck_record.length == 1 || ck_record[0][1] == null
					|| ck_record[0][1].equals("")
					|| ck_record[0][1].equals(null)
					|| ck_record[0][1].equals("null")) {
				UpdateSQLBuilder update = new UpdateSQLBuilder("ck_record");
				update.setWhereCondition("id=" + ck_record[0][0]);
				update.putFieldValue("confirmname", "Y");
				update.putFieldValue("implid", implid);
				update.putFieldValue("userid", loginUserid);
				update.putFieldValue("schemeid", vo[0]
						.getStringValue("schemeid"));
				UIUtil.executeUpdateByDS(null, update.getSQL());
			}
			String[] count = UIUtil
					.getStringArrayFirstColByDS(
							null,
							"select * from ck_problem_info where probleminfo is null and trackresult is null");
			if (count.length > 0) {
				UIUtil
						.executeUpdateByDS(null,
								"delete from ck_problem_info where probleminfo is null and trackresult is null");

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * �Ŵ�ȷ����
	 */
	private void impXdword() {
		BillVO[] bo = list.getAllBillVOs();
		if (bo.length <= 0) {
			MessageBox.show(this, "���Ȳ�ѯ");
			return;
		}
		if (deptid == null) {
			MessageBox.show(this, "��ѡ���Ų�ѯ����");
			return;
		}
		if (schemeid == null) {
			MessageBox.show(this, "��ѡ����Ŀ���Ʋ�ѯ����");
			return;
		}
		BillCardDialog listDialog = new BillCardDialog(this,
				"Ϊ��������������ʵ�����ѡ���ϴμ�����Ŀ����", "CK_PROBLEM_INFO_ZZL_EQ1_2", 500,
				200);
		listDialog.setVisible(true);
		BillVO vos = listDialog.getBillcardPanel().getBillVO();
		String scschemeid = vos.getStringValue("schemeid");
		StringBuffer sbID = new StringBuffer();
		for (int i = 0; i < bo.length; i++) {
			sbID.append(bo[i].getStringValue("id"));
			sbID.append(",");
		}
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>." + bo.length);
		String infoID = sbID.toString().substring(0, sbID.length() - 1);
		try {
			vo = UIUtil.getHashVoArrayByDS(null,
					"select * from ck_problem_info where 1=1 and id in("
							+ infoID + ") and schemeid='" + schemeid
							+ "' and checkmode='" + "�ֳ����"
							+ "' group by parentid");
			final JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new File("."));
			fc.setDialogTitle("��ѡ��Ҫ���浽��Ŀ¼");
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setAcceptAllFileFilterUsed(false);
			int result = fc.showSaveDialog(this);
			if (result != JFileChooser.APPROVE_OPTION) { // �������ȷ����
				return;
			}
			String savePath = fc.getSelectedFile().getPath();
			Map<Object, Object> dataMap = new HashMap<Object, Object>();
			HashVO[] implvo = UIUtil.getHashVoArrayByDS(null,
					"select * from ck_problem_info where   schemeid='"
							+ schemeid + "' and deptid='" + deptid
							+ "' and checkmode='�ֳ����'  group by implid");
			String dept = UIUtil.getStringValueByDS(null,
					"select name from pub_corp_dept where id='"
							+ implvo[0].getStringValue("deptid") + "'");
			dataMap.put("dept", dept);
			String[] timea = implvo[0].getStringValue("createdate").split("-");
			dataMap.put("time", timea[0].toString() + "��" + timea[1].toString()
					+ "��" + timea[2].toString() + "��");
			String planname = UIUtil.getStringValueByDS(null,
					"select name from CK_SCHEME where id='"
							+ implvo[0].getStringValue("schemeid") + "'");
			dataMap.put("planname", planname);
			StringBuilder sb = new StringBuilder();
			int imp = 0;
			for (int vo = 0; vo < implvo.length; vo++) {
				HashVO[] ipvo = UIUtil.getHashVoArrayByDS(null,
						"select * from CK_SCHEME_IMPL where id='"
								+ implvo[vo].getStringValue("implid")
								+ "' or refimplid='"
								+ implvo[vo].getStringValue("implid")
								+ "' order by refimplid");
				String money = UIUtil.getStringValueByDS(null,
						"select sum(c8)/10000 from CK_SCHEME_IMPL where id='"
								+ implvo[vo].getStringValue("implid")
								+ "' or refimplid='"
								+ implvo[vo].getStringValue("implid")
								+ "' order by refimplid");
				imp = imp + 1;
				String impnum = stringNumber(imp);
				sb.append(impnum).append("��");
				if (ipvo.length > 1) {
					sb.append("�����"
							+ ipvo[0].getStringValue("c1")
							+ "����С����"
							+ ZZLUIUtil.getTimeString(ipvo[0]
									.getStringValue("c6")) + "��"
							+ ipvo[0].getStringValue("c16"));
					sb.append("������������" + money + "��Ԫ������"
							+ ipvo[0].getStringValue("c18"));
					sb.append("�����У�");
					for (int p = 0; p < ipvo.length; p++) {
						sb.append(ipvo[p].getStringValue("c1")
								+ "����"
								+ Double.parseDouble(ipvo[p]
										.getStringValue("c8")) / 10000
								+ "��Ԫ���������ʣ�");
						String strdb = ipvo[p].getStringValue("c11");
						if(strdb!=null){
							if(strdb.indexOf(".")>0){
								strdb = strdb.substring(0, strdb.indexOf("."));
								int idb = Integer.parseInt(strdb);
								if (idb == 0) {
									Double db = Double.parseDouble(ipvo[p]
											.getStringValue("c11"));
									db = db * 1000;
									sb.append(db + "�룻");
								} else {
									sb.append(ipvo[p].getStringValue("c11") + "�룻");
								}
							}
						}


					}
					sb.append("������"
							+ ZZLUIUtil.getTimeString(ipvo[0]
									.getStringValue("c7")) + "����");
				} else if (ipvo.length > 0) {
					if (ipvo[0].getStringValue("c6") != null) {
						sb.append("�����"
								+ ipvo[0].getStringValue("c1")
								+ "��"
								+ ZZLUIUtil.getTimeString(ipvo[0]
										.getStringValue("c6")) + "��"
								+ ipvo[0].getStringValue("c16"));
					}
					sb.append("��������" + money + "��Ԫ������"
							+ ipvo[0].getStringValue("c18"));
					if (ipvo[0].getStringValue("c7") != null) {
						sb.append("��"
								+ ZZLUIUtil.getTimeString(ipvo[0]
										.getStringValue("c7")) + "���ڣ���������");
					}
					String strdb = null;
					if (ipvo[0].getStringValue("c11") != null) {
						strdb = ipvo[0].getStringValue("c11");
						if(strdb.indexOf(".")>0){
							strdb = strdb.substring(0, strdb.indexOf("."));
							int idb = Integer.parseInt(strdb);
							if (idb == 0) {
								Double db = Double.parseDouble(ipvo[0]
										.getStringValue("c11"));
								db = db * 1000;
								sb.append(db + "�룻");
							} else {
								sb.append(ipvo[0].getStringValue("c11") + "�룻");
							}
						}
						
					}

				}
				if (ipvo.length > 0) {
					if (ipvo[0].getStringValue("usera1") != null) {
						sb.append("����������A�ǣ�"
								+ ZZLUIUtil.getnamesSplit(ipvo[0]
										.getStringValue("usera1")) + "��");
					}
					if (ipvo[0].getStringValue("userb1") != null) {
						sb.append("����������B�ǣ�"
								+ ZZLUIUtil.getnamesSplit(ipvo[0]
										.getStringValue("userb1")) + "��");
					}
					if (ipvo[0].getStringValue("usera2") != null) {
						sb.append("���������A�ǣ�"
								+ ZZLUIUtil.getnamesSplit(ipvo[0]
										.getStringValue("usera2")) + "��");
					}
					if (ipvo[0].getStringValue("userb2") != null) {
						sb.append("���������B�ǣ�"
								+ ZZLUIUtil.getnamesSplit(ipvo[0]
										.getStringValue("userb2")) + "��");
					}
					if (ipvo[0].getStringValue("usera3") != null) {
						sb.append("����������A�ǣ�"
								+ ZZLUIUtil.getnamesSplit(ipvo[0]
										.getStringValue("usera3")) + "��");
					}
					if (ipvo[0].getStringValue("userb3") != null) {
						sb.append("����������B�ǣ�"
								+ ZZLUIUtil.getnamesSplit(ipvo[0]
										.getStringValue("userb3")) + "��");
					}
					if (ipvo[0].getStringValue("usera4") != null) {
						sb.append("��Ӫ������A�ǣ�"
								+ ZZLUIUtil.getnamesSplit(ipvo[0]
										.getStringValue("usera4")) + "��");
					}
					if (ipvo[0].getStringValue("userb4") != null) {
						sb.append("��Ӫ������B�ǣ�"
								+ ZZLUIUtil.getnamesSplit(ipvo[0]
										.getStringValue("userb4")) + "��");
					}
				}
				HashVO[] voIMPLEMENT = UIUtil.getHashVoArrayByDS(null,
						"select * from V_CK_SCHEME_IMPLEMENT where 1=1  and implid = '"
								+ implvo[vo].getStringValue("implid")
								+ "' and deptid='"
								+ implvo[vo].getStringValue("deptid")
								+ "' group by thirdname order by id");
				int a = 0;
				for (int i = 0; i < voIMPLEMENT.length; i++) {
					HashVO[] voct = UIUtil
							.getHashVoArrayByDS(
									null,
									"select * from V_CK_SCHEME_IMPLEMENT where 1=1  and implid = '"
											+ implvo[vo]
													.getStringValue("implid")
											+ "' and deptid='"
											+ implvo[vo]
													.getStringValue("deptid")
											+ "' and thirdname='"
											+ voIMPLEMENT[i]
													.getStringValue("thirdname")
											+ "' and result is not null and result='��Ч'");
					if (voct.length > 0) {
						a = a + 1;
						String num = stringNumber(a);
						sb.append("��" + num + "��").append("\r");
						sb.append(voIMPLEMENT[i].getStringValue("thirdname")
								+ "\r\n");
						int b = 0;
						for (int c = 0; c < voct.length; c++) {
							b = b + 1;
							sb.append(b + "��");
							sb.append(voct[c].getStringValue("dictname"));
							HashVO[] problem = UIUtil.getHashVoArrayByDS(null,
									"select * from ck_problem_info where parentid='"
											+ voct[c].getStringValue("id")
											+ "'");
							String str = String.valueOf(b);
							Double b1 = Double.parseDouble(str);
							DecimalFormat df = new DecimalFormat("######.0");
							for (int j = 0; j < problem.length; j++) {
								b1 = b1 + 0.1;
								String str1 = df.format(b1);
								sb.append("��" + str1 + "����");
								sb.append(problem[j]
										.getStringValue("problemInfo")
										+ "��");
								if (problem[j].getStringValue("adjustuserid") != null) {
									String zrname = ZZLUIUtil
											.getnamesSplit(problem[j]
													.getStringValue("adjustuserid"));
									sb.append("�����ˣ�" + zrname + "��");
								}
								if (problem[j]
										.getStringValue("mainadjustuserid") != null) {
									String zrname = ZZLUIUtil
											.getUserName(problem[j]
													.getStringValue("mainadjustuserid"));
									sb.append("���������ˣ�" + zrname + "��");
								}
							}
						}
					}
				}
				if (scschemeid != null) {
					if (vo == implvo.length - 1) {
						HashVO[] wzgproblemInfo = UIUtil.getHashVoArrayByDS(
								null,
								"select * from ck_problem_info where  1=1  and trackresult='δ����' and deptid='"
										+ deptid + "' and schemeid='"
										+ scschemeid + "'");
						HashVO[] yzgproblemInfo = UIUtil.getHashVoArrayByDS(
								null,
								"select * from ck_problem_info where  1=1  and trackresult='������' and deptid='"
										+ deptid + "' and schemeid='"
										+ scschemeid + "'");
						HashVO[] bfzgproblemInfo = UIUtil.getHashVoArrayByDS(
								null,
								"select * from ck_problem_info where  1=1 and trackresult='��������' and deptid='"
										+ deptid + "' and schemeid='"
										+ scschemeid + "'");
						HashVO[] allproblemInfo = UIUtil.getHashVoArrayByDS(
								null,
								"select * from ck_problem_info where  deptid='"
										+ deptid + "' and schemeid='"
										+ scschemeid + "'");
						if (wzgproblemInfo.length > 0
								|| bfzgproblemInfo.length > 0) {
							String num2 = stringNumber(implvo.length + 1);
							sb.append(num2 + "������������ʵ�����").append("\r\n");
							if (yzgproblemInfo.length > 0) {
								String[] time = yzgproblemInfo[0]
										.getStringValue("createdate")
										.split("-");
								sb.append(time[0].toString() + "��"
										+ time[1].toString() + "��"
										+ time[2].toString() + "�ռ��������⹲��"
										+ allproblemInfo.length + "��,");
							}
							sb.append("������" + yzgproblemInfo.length + "��,");
							sb.append("δ����" + wzgproblemInfo.length + "��,");
							sb.append("��������" + bfzgproblemInfo.length + "�");
							int wnum = 0;
							for (int w = 0; w < wzgproblemInfo.length; w++) {
								wnum = wnum + 1;
								String wtitle = UIUtil
										.getStringValueByDS(
												null,
												"select checkpoints from V_CK_SCHEME_IMPLEMENT where id ='"
														+ wzgproblemInfo[w]
																.getStringValue("parentid")
														+ "'");
								// String
								// wname=UIUtil.getStringValueByDS(null,"select name from pub_user where id='"+wzgproblemInfo[w].getStringValue("adjustuserid")+"'");
								String wname = ZZLUIUtil
										.getnamesSplit(wzgproblemInfo[w]
												.getStringValue("adjustuserid"));
								sb.append(wnum + ". " + wtitle + ": ��δ���ġ�������:"
										+ wname + "��\r");
							}
							for (int f = 0; f < bfzgproblemInfo.length; f++) {
								wnum = wnum + 1;
								String ftitle = UIUtil
										.getStringValueByDS(
												null,
												"select checkpoints from V_CK_SCHEME_IMPLEMENT where id ='"
														+ bfzgproblemInfo[f]
																.getStringValue("parentid")
														+ "'");
								// String
								// fname=UIUtil.getStringValueByDS(null,"select name from pub_user where id='"+bfzgproblemInfo[f].getStringValue("adjustuserid")+"'");
								String fname = ZZLUIUtil
										.getnamesSplit(bfzgproblemInfo[f]
												.getStringValue("adjustuserid"));
								sb.append(wnum + ". " + ftitle + ": " + "��������"
										+ "��������:" + fname + "��");
							}

						}
					}
				}
			}
			dataMap.put("problem", sb);
			String[][] names = UIUtil.getStringArrayByDS(null,
					"select leader2,teamusers from V_CK_SCHEME where deptid = '"
							+ implvo[0].getStringValue("deptid")
							+ "' and schemeid = '"
							+ implvo[0].getStringValue("schemeid") + "'");
			String leader2ID = names[0][0].toString();
			String teamusersID = names[0][1].toString();
			StringBuilder sb2 = new StringBuilder();
			String leader2 = UIUtil.getStringValueByDS(null,
					"select name from pub_user where id='" + leader2ID + "'");
			sb2.append(leader2 + "\r");
			teamusersID = teamusersID.replace(";", ",");
			teamusersID = teamusersID.substring(1, teamusersID.length() - 1);
			String[] teamusers = UIUtil.getStringArrayFirstColByDS(null,
					"select name from pub_user where id in(" + teamusersID
							+ ")");
			for (int i = 0; i < teamusers.length; i++) {
				sb2.append(teamusers[i].toString() + "\r");
			}
			dataMap.put("names", sb2);
			String[] timeb = implvo[0].getStringValue("createdate").split("-");
			dataMap.put("time2", timeb[0].toString() + "��"
					+ timeb[1].toString() + "��" + timeb[2].toString() + "��");
			savePath = savePath + "\\" + dataMap.get("dept") + "_" + planname
					+ "_�ֳ������ʵȷ����.doc";
			WordExport we = new WordExport();
			we.createDoc(dataMap, "WordFactModel", savePath);
			MessageBox.show(this, "�����ɹ�");
			String[][] ck_record = UIUtil.getStringArrayByDS(null,
					"select id,confirmname from ck_record where deptid='"
							+ implvo[0].getStringValue("deptid")
							+ "' and schemeid='"
							+ implvo[0].getStringValue("schemeid") + "'");
			String impid = UIUtil.getStringValueByDS(null,
					"select implid from V_CK_SCHEME_IMPLEMENT where deptid='"
							+ implvo[0].getStringValue("deptid")
							+ "' and schemeid='"
							+ implvo[0].getStringValue("schemeid") + "'");
			if (ck_record.length <= 0) {
				InsertSQLBuilder insert = new InsertSQLBuilder("ck_record");
				String id = ICheckUIUtil.getSequenceNextVal();
				insert.putFieldValue("id", id);
				insert.putFieldValue("deptid", vo[0].getStringValue("deptid"));
				insert.putFieldValue("userid", loginUserid);
				insert.putFieldValue("confirmname", "Y");
				insert.putFieldValue("schemeid", vo[0]
						.getStringValue("schemeid"));
				insert.putFieldValue("implid", impid);
				UIUtil.executeUpdateByDS(null, insert.getSQL());
			} else if (ck_record.length == 1 || ck_record[0][1] == null
					|| ck_record[0][1].equals("")
					|| ck_record[0][1].equals(null)
					|| ck_record[0][1].equals("null")) {
				UpdateSQLBuilder update = new UpdateSQLBuilder("ck_record");
				update.setWhereCondition("id=" + ck_record[0][0]);
				update.putFieldValue("confirmname", "Y");
				update.putFieldValue("userid", loginUserid);
				update.putFieldValue("schemeid", vo[0]
						.getStringValue("schemeid"));
				update.putFieldValue("implid", impid);
				UIUtil.executeUpdateByDS(null, update.getSQL());
			}
			String[] count = UIUtil
					.getStringArrayFirstColByDS(
							null,
							"select * from ck_problem_info where probleminfo is null and trackresult is null");
			if (count.length > 0) {
				UIUtil
						.executeUpdateByDS(null,
								"delete from ck_problem_info where probleminfo is null and trackresult is null");

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * �������ֳ������ʵȷ����
	 */
	private void impfss_btnword() {
		BillVO[] bo = list.getAllBillVOs();
		if (bo.length <= 0) {
			MessageBox.show(this, "���Ȳ�ѯ");
			return;
		}
		if (deptid == null) {
			MessageBox.show(this, "��ѡ���Ų�ѯ����");
			return;
		}
		if (schemeid == null) {
			MessageBox.show(this, "��ѡ����Ŀ���Ʋ�ѯ����");
			return;
		}
		StringBuffer sbID = new StringBuffer();
		for (int i = 0; i < bo.length; i++) {
			sbID.append(bo[i].getStringValue("id"));
			sbID.append(",");
		}
		String infoID = sbID.toString().substring(0, sbID.length() - 1);
		try {
			vo = UIUtil.getHashVoArrayByDS(null,
					"select * from ck_problem_info where 1=1 and id in("
							+ infoID + ") and schemeid='" + schemeid
							+ "' and checkmode='" + "���ֳ����"
							+ "' group by parentid");
			if (vo.length <= 0) {
				MessageBox.show(this, "û�з��ֳ����ĵ׸�");
				return;
			}
			final JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new File("."));
			fc.setDialogTitle("��ѡ��Ҫ���浽��Ŀ¼");
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setAcceptAllFileFilterUsed(false);
			int result = fc.showSaveDialog(this);
			if (result != JFileChooser.APPROVE_OPTION) { // �������ȷ����
				return;
			}
			String savePath = fc.getSelectedFile().getPath();
			Map<Object, Object> dataMap = new HashMap<Object, Object>();

			String dept = UIUtil.getStringValueByDS(null,
					"select name from pub_corp_dept where id='"
							+ vo[0].getStringValue("deptid") + "'");
			dataMap.put("dept", dept);
			String[] timea = vo[0].getStringValue("createdate").split("-");
			dataMap.put("time", timea[0].toString() + "��" + timea[1].toString()
					+ "��" + timea[2].toString() + "��");
			String planname = UIUtil.getStringValueByDS(null,
					"select name from CK_SCHEME where id='"
							+ vo[0].getStringValue("schemeid") + "'");
			dataMap.put("planname", planname);
			StringBuilder sb = new StringBuilder();
			int a = 0;
			for (int i = 0; i < vo.length; i++) {
				a = a + 1;
				String num = stringNumber(a);
				String title = UIUtil.getStringValueByDS(null,
						"select checkpoints from V_CK_SCHEME_IMPLEMENT where id ='"
								+ vo[i].getStringValue("parentid") + "'");
				String parentid = UIUtil.getStringValueByDS(null,
						"select id from V_CK_SCHEME_IMPLEMENT where id ='"
								+ vo[i].getStringValue("parentid") + "'");
				sb.append("��" + num + "��").append("\r");
				sb.append(title + "\r\n");
				String[][] problem = UIUtil
						.getStringArrayByDS(
								null,
								"select problemInfo,adjustuserid,mainadjustuserid from ck_problem_info where parentid='"
										+ parentid + "'");
				int b = 0;
				for (int j = 0; j < problem.length; j++) {
					b = b + 1;
					sb.append(b + ".");
					sb.append(problem[j][0].toString() + "\n");
					if (problem[j][1] == null || problem[j][1].equals(null)
							|| problem[j][1].equals(" ")
							|| problem[j][1].equals("")
							|| problem[j][1].equals("null")) {
					} else {
						String zrname = ZZLUIUtil.getnamesSplit(problem[j][1]
								.toString());
						sb.append("�����ˣ�" + zrname + "��" + "\n");
					}
					if (problem[j][2] == null || problem[j][2].equals(null)
							|| problem[j][2].equals(" ")
							|| problem[j][2].equals("")
							|| problem[j][2].equals("null")) {
					} else {
						String zrname = ZZLUIUtil.getUserName(problem[j][2]
								.toString());
						sb.append("���������ˣ�" + zrname + "��" + "\n");
					}

				}
			}
			dataMap.put("problem", sb);
			String[][] names = UIUtil.getStringArrayByDS(null,
					"select leader2,teamusers from V_CK_SCHEME where deptid = '"
							+ vo[0].getStringValue("deptid")
							+ "' and schemeid = '"
							+ vo[0].getStringValue("schemeid") + "'");
			String leader2ID = names[0][0].toString();
			String teamusersID = names[0][1].toString();
			StringBuilder sb2 = new StringBuilder();
			String leader2 = UIUtil.getStringValueByDS(null,
					"select name from pub_user where id='" + leader2ID + "'");
			sb2.append(leader2 + "\r");
			teamusersID = teamusersID.replace(";", ",");
			teamusersID = teamusersID.substring(1, teamusersID.length() - 1);
			String[] teamusers = UIUtil.getStringArrayFirstColByDS(null,
					"select name from pub_user where id in(" + teamusersID
							+ ")");
			for (int i = 0; i < teamusers.length; i++) {
				sb2.append(teamusers[i].toString() + "\r");
			}
			dataMap.put("names", sb2);
			String[] timeb = vo[0].getStringValue("createdate").split("-");
			dataMap.put("time2", timeb[0].toString() + "��"
					+ timeb[1].toString() + "��" + timeb[2].toString() + "��");
			savePath = savePath + "\\" + dataMap.get("dept") + "_" + planname
					+ "-���ֳ������ʵȷ����.doc";
			WordExport we = new WordExport();
			we.createDoc(dataMap, "WordNOFactModel", savePath);
			MessageBox.show(this, "�����ɹ�");
			String[][] ck_record = UIUtil.getStringArrayByDS(null,
					"select id,confirmname2 from ck_record where deptid='"
							+ vo[0].getStringValue("deptid")
							+ "' and schemeid='"
							+ vo[0].getStringValue("schemeid") + "'");
			String implid = UIUtil.getStringValueByDS(null,
					"select implid from V_CK_SCHEME_IMPLEMENT where deptid='"
							+ vo[0].getStringValue("deptid")
							+ "' and schemeid='"
							+ vo[0].getStringValue("schemeid") + "'");
			if (ck_record.length <= 0) {
				InsertSQLBuilder insert = new InsertSQLBuilder("ck_record");
				String id = ICheckUIUtil.getSequenceNextVal();
				insert.putFieldValue("id", id);
				insert.putFieldValue("deptid", vo[0].getStringValue("deptid"));
				insert.putFieldValue("userid", loginUserid);
				insert.putFieldValue("confirmname2", "Y");
				insert.putFieldValue("schemeid", vo[0]
						.getStringValue("schemeid"));
				insert.putFieldValue("implid", implid);
				UIUtil.executeUpdateByDS(null, insert.getSQL());
			} else if (ck_record.length == 1 || ck_record[0][1] == null
					|| ck_record[0][1].equals("")
					|| ck_record[0][1].equals(null)
					|| ck_record[0][1].equals("null")) {
				UpdateSQLBuilder update = new UpdateSQLBuilder("ck_record");
				update.setWhereCondition("id=" + ck_record[0][0]);
				update.putFieldValue("confirmname2", "Y");
				update.putFieldValue("userid", loginUserid);
				update.putFieldValue("schemeid", vo[0]
						.getStringValue("schemeid"));
				update.putFieldValue("implid", implid);
				UIUtil.executeUpdateByDS(null, update.getSQL());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String stringNumber(int a) {
		String num = null;
		String i = String.valueOf(a);
		num = transition(i);
		return num;
	}

	private String transition(String si) {
		String num = null;
		String[] aa = { "", "ʮ", "��", "ǧ", "��", "ʮ��", "����", "ǧ��", "��", "ʮ��" };
		String[] bb = { "һ", "��", "��", "��", "��", "��", "��", "��", "��" };
		char[] ch = si.toCharArray();
		int maxindex = ch.length;
		if (maxindex == 2) {
			for (int i = maxindex - 1, j = 0; i >= 0; i--, j++) {
				if (ch[j] != 48) {
					if (j == 0 && ch[j] == 49) {
						num = aa[i];
						System.out.print(aa[i]);
					} else {
						num = bb[ch[j] - 49] + aa[i];
						System.out.print(bb[ch[j] - 49] + aa[i]);
					}
				}
			}
		} else {
			for (int i = maxindex - 1, j = 0; i >= 0; i--, j++) {
				if (ch[j] != 48) {
					num = bb[ch[j] - 49] + aa[i];
					System.out.print(bb[ch[j] - 49] + aa[i]);
				}
			}
		}
		return num;
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent arg0) {

	}

}