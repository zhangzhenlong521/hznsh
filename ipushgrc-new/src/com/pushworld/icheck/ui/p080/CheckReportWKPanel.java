package com.pushworld.icheck.ui.p080;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.icheck.word.WordExport;

public class CheckReportWKPanel extends AbstractWorkPanel implements
		ActionListener {

	private BillListPanel list = null;
	private WLTButton impreport = null;
	private WLTButton impadvice = null;
	private WLTButton impdecision = null;
	private WLTButton imprisk = null;
	private String loginUserid = ClientEnvironment.getInstance()
			.getLoginUserID();
	private String loginDeptid = ClientEnvironment.getInstance()
			.getLoginUserDeptId();
	private BillVO vo = null;

	public void initialize() {
		list = new BillListPanel("CK_SCHEME_ZZL_E01_1");
		impreport = new WLTButton("������鱨��");
		impadvice = new WLTButton("������������");
		impdecision = new WLTButton("������������");
		imprisk = new WLTButton("����������ʾ��");
		impreport.addActionListener(this);
		impadvice.addActionListener(this);
		impdecision.addActionListener(this);
		imprisk.addActionListener(this);
		list.addBillListButton(impreport);
		list.addBillListButton(impadvice);
		list.addBillListButton(impdecision);
		list.addBillListButton(imprisk);
		list.repaintBillListButton();
		this.add(list);

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == impreport) {
			vo = list.getSelectedBillVO();
			if(vo==null){
				MessageBox.show(null,"��ѡ��һ����¼");
				return;
			}
			if(vo.getStringValue("SCHEMETYPE").equals("����")){
				impreport();
			}else{
				impxdreport();
			}
			
		} else if (e.getSource() == impadvice) {
			vo = list.getSelectedBillVO();
			if(vo==null){
				MessageBox.show(null,"��ѡ��һ����¼");
				return;
			}
			if(vo.getStringValue("SCHEMETYPE").equals("����")){
				impadvice();
			}else{
				impxdadvice();
			}
			
		} else if (e.getSource() == impdecision) {
			vo = list.getSelectedBillVO();
			if(vo==null){
				MessageBox.show(null,"��ѡ��һ����¼");
				return;
			}
			if(vo.getStringValue("SCHEMETYPE").equals("����")){
				impdecision();
			}else{
				impxddecision();
			}
			
		} else if (e.getSource() == imprisk) {
			imprisk();
		}

	}

	/**
	 * ������ʾ
	 */
	private void imprisk() {
		vo = list.getSelectedBillVO();
		if (vo == null) {
			MessageBox.show(this, "��ѡ��һ������");
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
		try {
			dataMap
					.put("yaer", vo.getStringValue("createdate")
							.substring(0, 4));
			dataMap.put("yaer2", vo.getStringValue("createdate")
					.substring(0, 4));
			savePath = savePath + "\\������ʾ��.doc";
			WordExport we = new WordExport();
			we.createDoc(dataMap, "WordRiskModel", savePath);
			String[][] ck_record = UIUtil.getStringArrayByDS(null,
					"select id,risk from ck_wl_record where schemeid='"+ vo.getStringValue("id") + "'");
			if (ck_record.length <= 0) {
				InsertSQLBuilder insert = new InsertSQLBuilder("ck_wl_record");
				String id = UIUtil.getSequenceNextValByDS(null,
						"s_ck_wl_record");
				insert.putFieldValue("id", id);
				insert.putFieldValue("userid", loginUserid);
				insert.putFieldValue("risk", "Y");
				insert.putFieldValue("schemeid", vo.getStringValue("id"));
				UIUtil.executeUpdateByDS(null, insert.getSQL());
			} else if (ck_record.length == 1 || ck_record[0][1] == null
					|| ck_record[0][1].equals("")
					|| ck_record[0][1].equals(null)
					|| ck_record[0][1].equals("null")) {
				UpdateSQLBuilder update = new UpdateSQLBuilder("ck_wl_record");
				update.setWhereCondition("id=" + ck_record[0][0]);
				update.putFieldValue("risk", "Y");
				update.putFieldValue("schemeid", vo.getStringValue("id"));
				UIUtil.executeUpdateByDS(null, update.getSQL());
			}
			MessageBox.show(this, "�����ɹ�");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 *��������
	 */
	private void impdecision() {
		vo = list.getSelectedBillVO();
		if (vo == null) {
			MessageBox.show(this, "��ѡ��һ������");
			return;
		}
		Date dt = new Date();
		SimpleDateFormat matter1 = new SimpleDateFormat("yyyy��MM��dd��");
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
		dataMap.put("name", vo.getStringValue("name"));
		HashVO[] planvo;
		try {
			planvo = UIUtil.getHashVoArrayByDS(null,
					"select * from CK_PLAN where id='"
							+ vo.getStringValue("planid") + "'");
			String[] time1 = planvo[0].getStringValue("PLANBEGINDATE")
					.toString().split("-");
			String[] time2 = planvo[0].getStringValue("PLANENDDATE").toString()
					.split("-");
			dataMap.put("time", time1[0].toString() + "��" + time1[1].toString()
					+ "��" + time1[2].toString() + "����" + time2[0].toString()
					+ "��" + time2[1].toString() + "��" + time2[2].toString()
					+ "��");
			String[] deptid = UIUtil.getStringArrayFirstColByDS(null,
					"select deptid from V_CK_SCHEME where 1=1 and  schemeid='"
							+ vo.getStringValue("id") + "'");
			dataMap.put("count", deptid.length + "������");
			String[] problemct = UIUtil
					.getStringArrayFirstColByDS(
							null,
							"select distinct(b.checkpoints) from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
									+ vo.getStringValue("id") + "'");
			int countpm = 0;
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < problemct.length; i++) {
				countpm = countpm + 1;
				String numpm = ZZLUIUtil.numTurnStringNum(countpm);
				sb.append("��" + numpm + "��\r\r\n");
				sb.append(problemct[i].toString() + "\r");
				int count = 0;
				String[] problemdept = UIUtil
						.getStringArrayFirstColByDS(
								null,
								"select distinct(a.deptid) from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
										+ vo.getStringValue("id")
										+ "' and b.checkpoints='"
										+ problemct[i].toString() + "'");
				for (int j = 0; j < problemdept.length; j++) {
					HashVO[] problemvo = UIUtil
							.getHashVoArrayByDS(
									null,
									"select * from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
											+ vo.getStringValue("id")
											+ "' and b.checkpoints='"
											+ problemct[i].toString()
											+ "' and a.deptid='"
											+ problemdept[j].toString() + "'");
					for (int k = 0; k < problemvo.length; k++) {
						count = count + 1;
						String bmname = ZZLUIUtil.getdeptsSplit(problemvo[k]
								.getStringValue("deptid"));
						sb.append("��" + count + "��" + bmname + ":");
						sb.append(problemvo[k].getStringValue("probleminfo")
								+ "\r");
						String usname = ZZLUIUtil.getnamesSplit(problemvo[k]
								.getStringValue("adjustuserid"));
						if(usname!=null){
						usname = usname.replace(",", "");
						sb.append("�����ˣ�" + usname + "\r");
						}
					}
				}
				sb
						.append("������ʵΥ���ˡ��½�ά���������ũ�����ú�����XXX�ƶȡ������ݡ��½�����ũ����ҵ���йɷ����޹�˾Ա��Υ�������ƶȴ����涨����X�µ�x�ڵ�XX����ع涨����������������XX������");
			}
			dataMap.put("problem", sb);
			dataMap.put("time2", matter1.format(dt));
			savePath = savePath + "\\" + dataMap.get("name") + "-��������.doc";
			WordExport we = new WordExport();
			we.createDoc(dataMap, "WordDecidionModel", savePath);
			String[][] ck_record = UIUtil.getStringArrayByDS(null,
					"select id,decision from ck_wl_record where schemeid='"+ vo.getStringValue("id") + "'");
			if (ck_record.length <= 0) {
				InsertSQLBuilder insert = new InsertSQLBuilder("ck_wl_record");
				String id = UIUtil.getSequenceNextValByDS(null,
						"s_ck_wl_record");
				insert.putFieldValue("id", id);
				insert.putFieldValue("userid", loginUserid);
				insert.putFieldValue("decision", "Y");
				insert.putFieldValue("schemeid", vo.getStringValue("id"));
				UIUtil.executeUpdateByDS(null, insert.getSQL());
			} else if (ck_record.length == 1 || ck_record[0][1] == null
					|| ck_record[0][1].equals("")
					|| ck_record[0][1].equals(null)
					|| ck_record[0][1].equals("null")) {
				UpdateSQLBuilder update = new UpdateSQLBuilder("ck_wl_record");
				update.setWhereCondition("id=" + ck_record[0][0]);
				update.putFieldValue("decision", "Y");
				update.putFieldValue("schemeid", vo.getStringValue("id"));
				UIUtil.executeUpdateByDS(null, update.getSQL());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MessageBox.show(this, "�����ɹ�");

	}
/**
 * �Ŵ���������	
 */
	private void impxddecision(){
		vo = list.getSelectedBillVO();
		if (vo == null) {
			MessageBox.show(this, "��ѡ��һ������");
			return;
		}
		Date dt = new Date();
		SimpleDateFormat matter1 = new SimpleDateFormat("yyyy��MM��dd��");
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
		dataMap.put("name", vo.getStringValue("name"));
		HashVO[] planvo;
		try {
			planvo = UIUtil.getHashVoArrayByDS(null,
					"select * from CK_PLAN where id='"
							+ vo.getStringValue("planid") + "'");
			String[] time1 = planvo[0].getStringValue("PLANBEGINDATE")
					.toString().split("-");
			String[] time2 = planvo[0].getStringValue("PLANENDDATE").toString()
					.split("-");
			dataMap.put("time", time1[0].toString() + "��" + time1[1].toString()
					+ "��" + time1[2].toString() + "����" + time2[0].toString()
					+ "��" + time2[1].toString() + "��" + time2[2].toString()
					+ "��");
			String[] deptid = UIUtil.getStringArrayFirstColByDS(null,
					"select deptid from V_CK_SCHEME where 1=1 and  schemeid='"
							+ vo.getStringValue("id") + "'");
			dataMap.put("count", deptid.length + "������");
			String[] problemct = UIUtil
			.getStringArrayFirstColByDS(
					null,
					"select b.checkpoints from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"+ vo.getStringValue("id")+"' group by b.checkpoints");
	int countpm = 0;
	StringBuilder sb = new StringBuilder();
	for (int i = 0; i < problemct.length; i++) {
		countpm = countpm + 1;
		String numpm = ZZLUIUtil.numTurnStringNum(countpm);
		sb.append("��" + numpm + "��\r\r\n");
		sb.append(problemct[i].toString() + "\r");
		HashVO [] problemdept = UIUtil
				.getHashVoArrayByDS(
						null,
						"select * from V_CK_SCHEME_IMPLEMENT where schemeid='"+ vo.getStringValue("id")+ "' and checkpoints='"
								+ problemct[i].toString() + "' and result='��Ч'");
		int imp=0;
		for (int j = 0; j < problemdept.length; j++) {
			HashVO  [] ipvo=UIUtil.getHashVoArrayByDS(null, "select * from CK_SCHEME_IMPL where  id='"+problemdept[j].getStringValue("implid")+"'  or refimplid='"+problemdept[j].getStringValue("implid")+"' order by refimplid");
			String money=UIUtil.getStringValueByDS(null, "select sum(c8)/10000 from CK_SCHEME_IMPL where id='"+problemdept[j].getStringValue("implid")+"' or refimplid='"+problemdept[j].getStringValue("implid")+"' order by refimplid");
			if(ipvo.length>1){
			imp=imp+1;
			sb.append(imp).append("��");
			sb.append("�����"+ipvo[0].getStringValue("c1")+"����С����"+ZZLUIUtil.getTimeString(ipvo[0].getStringValue("c6"))+"��"+ipvo[0].getStringValue("c16"));
			sb.append("������������"+money+"��Ԫ������"+ipvo[0].getStringValue("c18"));
			sb.append("�����У�");
			for(int p=0;p<ipvo.length;p++){
				sb.append(ipvo[p].getStringValue("c1")+"����"+Double.parseDouble(ipvo[p].getStringValue("c8"))/10000+"��Ԫ���������ʣ�");
				String strdb=ipvo[p].getStringValue("c11");
				strdb=strdb.substring(0,strdb.indexOf("."));
				int idb=Integer.parseInt(strdb);
				if(idb==0){
					Double db=Double.parseDouble(ipvo[p].getStringValue("c11"));
					db=db*1000;
					sb.append(db+"�룻");
				}else{
					sb.append(ipvo[p].getStringValue("c11")+"�룻");
				}
			}
			sb.append("������"+ZZLUIUtil.getTimeString(ipvo[0].getStringValue("c7"))+"���ڣ�");
			
			if(ipvo[0].getStringValue("usera1")!=null){
				sb.append("����������A�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera1"))+"��");
			}
			if(ipvo[0].getStringValue("userb1")!=null){
				sb.append("����������B�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb1"))+"��");
			}
			if(ipvo[0].getStringValue("usera2")!=null){
				sb.append("���������A�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera2"))+"��");
			}
			if(ipvo[0].getStringValue("userb2")!=null){
				sb.append("���������B�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb2"))+"��");
			}
			if(ipvo[0].getStringValue("usera3")!=null){
				sb.append("����������A�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera3"))+"��");
			}
			if(ipvo[0].getStringValue("userb3")!=null){
				sb.append("����������B�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb3"))+"��");
			}
			if(ipvo[0].getStringValue("usera4")!=null){
				sb.append("��Ӫ������A�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera4"))+"��");
			}
			if(ipvo[0].getStringValue("userb4")!=null){
				sb.append("��Ӫ������B�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb4"))+"��");
			}
			HashVO[] problemvo = UIUtil
					.getHashVoArrayByDS(
							null,
							"select * from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
									+ vo.getStringValue("id")
									+ "' and b.checkpoints='"
									+ problemct[i].toString()
									+ "' and a.deptid='"
									+ problemdept[j].getStringValue("deptid") + "' and b.implid='"+problemdept[j].getStringValue("implid")+"'");
			int count = 0;
			for (int k = 0; k < problemvo.length; k++) {
				count = count + 1;
				String bmname = ZZLUIUtil.getdeptsSplit(problemvo[k]
						.getStringValue("deptid"));
				sb.append("��" + count + "��" + bmname + ":");
				
				sb.append(problemvo[k].getStringValue("probleminfo")
						+ "\r");
				if(problemvo[k].getStringValue("adjustuserid")!=null){
				String usname = ZZLUIUtil.getnamesSplit(problemvo[k]
						.getStringValue("adjustuserid"));
				usname = usname.replace(",", "");
				sb.append("�����ˣ�" + usname + "\r");
				}
			}
			}else{
				int grint=0;
					grint=grint+imp+1;
					sb.append(grint).append("��");
					sb.append("�����"+ipvo[0].getStringValue("c1")+"��"+ZZLUIUtil.getTimeString(ipvo[0].getStringValue("c6"))+"��"+ipvo[0].getStringValue("c16"));
					sb.append("��������"+money+"��Ԫ������"+ipvo[0].getStringValue("c18"));
					sb.append("��"+ZZLUIUtil.getTimeString(ipvo[0].getStringValue("c7"))+"���ڣ���������");
					String strdb=ipvo[0].getStringValue("c11");
					strdb=strdb.substring(0,strdb.indexOf("."));
					int idb=Integer.parseInt(strdb);
					if(idb==0){
						Double db=Double.parseDouble(ipvo[0].getStringValue("c11"));
						db=db*1000;
						sb.append(db+"�룻");
					}else{
						sb.append(ipvo[0].getStringValue("c11")+"�룻");
					}
					imp=grint;
				
				
				if(ipvo[0].getStringValue("usera1")!=null){
					sb.append("����������A�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera1"))+"��");
				}
				if(ipvo[0].getStringValue("userb1")!=null){
					sb.append("����������B�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb1"))+"��");
				}
				if(ipvo[0].getStringValue("usera2")!=null){
					sb.append("���������A�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera2"))+"��");
				}
				if(ipvo[0].getStringValue("userb2")!=null){
					sb.append("���������B�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb2"))+"��");
				}
				if(ipvo[0].getStringValue("usera3")!=null){
					sb.append("����������A�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera3"))+"��");
				}
				if(ipvo[0].getStringValue("userb3")!=null){
					sb.append("����������B�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb3"))+"��");
				}
				if(ipvo[0].getStringValue("usera4")!=null){
					sb.append("��Ӫ������A�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera4"))+"��");
				}
				if(ipvo[0].getStringValue("userb4")!=null){
					sb.append("��Ӫ������B�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb4"))+"��");
				}
				HashVO[] problemvo = UIUtil
						.getHashVoArrayByDS(
								null,
								"select * from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
										+ vo.getStringValue("id")
										+ "' and b.checkpoints='"
										+ problemct[i].toString()
										+ "' and a.deptid='"
										+ problemdept[j].getStringValue("deptid") + "' and b.implid='"+problemdept[j].getStringValue("implid")+"'");
				int count = 0;
				for (int k = 0; k < problemvo.length; k++) {
					count = count + 1;
					String bmname = ZZLUIUtil.getdeptsSplit(problemvo[k]
							.getStringValue("deptid"));
					sb.append("��" + count + "��" + bmname + ":");
					
					sb.append(problemvo[k].getStringValue("probleminfo")
							+ "\r");
					if(problemvo[k].getStringValue("adjustuserid")!=null){
					String usname = ZZLUIUtil.getnamesSplit(problemvo[k]
							.getStringValue("adjustuserid"));
					usname = usname.replace(",", "");
					sb.append("�����ˣ�" + usname + "\r");
					}
				}
				
			}
		}
		sb.append("������ʵΥ���ˡ��½�ά���������ũ�����ú�����XXX�ƶȡ������ݡ��½�����ũ����ҵ���йɷ����޹�˾Ա��Υ�������ƶȴ����涨����X�µ�x�ڵ�XX����ع涨����������������XX������");
			}
			dataMap.put("problem", sb);
			dataMap.put("time2", matter1.format(dt));
			savePath = savePath + "\\" + dataMap.get("name") + "-��������.doc";
			WordExport we = new WordExport();
			we.createDoc(dataMap, "WordDecidionModel", savePath);
			String[][] ck_record = UIUtil.getStringArrayByDS(null,
					"select id,decision from ck_wl_record where schemeid='"+ vo.getStringValue("id") + "'");
			if (ck_record.length <= 0) {
				InsertSQLBuilder insert = new InsertSQLBuilder("ck_wl_record");
				String id = UIUtil.getSequenceNextValByDS(null,
						"s_ck_wl_record");
				insert.putFieldValue("id", id);
				insert.putFieldValue("userid", loginUserid);
				insert.putFieldValue("decision", "Y");
				insert.putFieldValue("schemeid", vo.getStringValue("id"));
				UIUtil.executeUpdateByDS(null, insert.getSQL());
			} else if (ck_record.length == 1 || ck_record[0][1] == null
					|| ck_record[0][1].equals("")
					|| ck_record[0][1].equals(null)
					|| ck_record[0][1].equals("null")) {
				UpdateSQLBuilder update = new UpdateSQLBuilder("ck_wl_record");
				update.setWhereCondition("id=" + ck_record[0][0]);
				update.putFieldValue("decision", "Y");
				update.putFieldValue("schemeid", vo.getStringValue("id"));
				UIUtil.executeUpdateByDS(null, update.getSQL());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MessageBox.show(this, "�����ɹ�");

	}

	/**
	 * ��������
	 */
	private void impadvice() {
		vo = list.getSelectedBillVO();
		if (vo == null) {
			MessageBox.show(this, "��ѡ��һ������");
			return;
		}
		Date dt = new Date();
		SimpleDateFormat matter1 = new SimpleDateFormat("yyyy��MM��dd��");
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
		dataMap.put("name", vo.getStringValue("name"));
		HashVO[] planvo;
		try {
			planvo = UIUtil.getHashVoArrayByDS(null,
					"select * from CK_PLAN where id='"
							+ vo.getStringValue("planid") + "'");
			String[] time1 = planvo[0].getStringValue("PLANBEGINDATE")
					.toString().split("-");
			String[] time2 = planvo[0].getStringValue("PLANENDDATE").toString()
					.split("-");
			dataMap.put("time", time1[0].toString() + "��" + time1[1].toString()
					+ "��" + time1[2].toString() + "����" + time2[0].toString()
					+ "��" + time2[1].toString() + "��" + time2[2].toString()
					+ "��");
			String[] deptid = UIUtil.getStringArrayFirstColByDS(null,
					"select deptid from V_CK_SCHEME where 1=1 and  schemeid='"
							+ vo.getStringValue("id") + "'");
			dataMap.put("count", deptid.length + "������");
			String[] problemct = UIUtil
					.getStringArrayFirstColByDS(
							null,
							"select distinct(b.checkpoints) from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
									+ vo.getStringValue("id") + "'");
			int countpm = 0;
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < problemct.length; i++) {
				countpm = countpm + 1;
				String numpm = ZZLUIUtil.numTurnStringNum(countpm);
				sb.append("��" + numpm + "��\r\r\n");
				sb.append(problemct[i].toString() + "\r");
				int count = 0;
				String[] problemdept = UIUtil
						.getStringArrayFirstColByDS(
								null,
								"select distinct(a.deptid) from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
										+ vo.getStringValue("id")
										+ "' and b.checkpoints='"
										+ problemct[i].toString() + "'");
				for (int j = 0; j < problemdept.length; j++) {
					HashVO[] problemvo = UIUtil
							.getHashVoArrayByDS(
									null,
									"select * from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
											+ vo.getStringValue("id")
											+ "' and b.checkpoints='"
											+ problemct[i].toString()
											+ "' and a.deptid='"
											+ problemdept[j].toString() + "'");
					for (int k = 0; k < problemvo.length; k++) {
						count = count + 1;
						String bmname = ZZLUIUtil.getdeptsSplit(problemvo[k]
								.getStringValue("deptid"));
						sb.append("��" + count + "��" + bmname + ":");
						sb.append(problemvo[k].getStringValue("probleminfo")
								+ "\r");
						String usname = ZZLUIUtil.getnamesSplit(problemvo[k]
								.getStringValue("adjustuserid"));
						if(usname!=null){
						usname = usname.replace(",", "");
						sb.append("�����ˣ�" + usname + "\r");
						}
					}
				}
				sb
						.append("������ʵΥ���ˡ��½�ά���������ũ�����ú�����XXX�ƶȡ������ݡ��½�����ũ����ҵ���йɷ����޹�˾Ա��Υ�������ƶȴ����涨����X�µ�x�ڵ�XX����ع涨���������������XX������");
			}
			dataMap.put("problem", sb);
			dataMap.put("time2", matter1.format(dt));
			savePath = savePath + "\\" + dataMap.get("name") + "-��������.doc";
			WordExport we = new WordExport();
			we.createDoc(dataMap, "WordAdviceModel", savePath);
			String[][] ck_record = UIUtil.getStringArrayByDS(null,
					"select id,advice from ck_wl_record where  schemeid='"+ vo.getStringValue("id") + "'");
			if (ck_record.length <= 0) {
				InsertSQLBuilder insert = new InsertSQLBuilder("ck_wl_record");
				String id = UIUtil.getSequenceNextValByDS(null,
						"s_ck_wl_record");
				insert.putFieldValue("id", id);
				insert.putFieldValue("userid", loginUserid);
				insert.putFieldValue("advice", "Y");
				insert.putFieldValue("schemeid", vo.getStringValue("id"));
				UIUtil.executeUpdateByDS(null, insert.getSQL());
			} else if (ck_record.length == 1 || ck_record[0][1] == null
					|| ck_record[0][1].equals("")
					|| ck_record[0][1].equals(null)
					|| ck_record[0][1].equals("null")) {
				UpdateSQLBuilder update = new UpdateSQLBuilder("ck_wl_record");
				update.setWhereCondition("id=" + ck_record[0][0]);
				update.putFieldValue("advice", "Y");
				update.putFieldValue("schemeid", vo.getStringValue("id"));
				UIUtil.executeUpdateByDS(null, update.getSQL());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MessageBox.show(this, "�����ɹ�");
	}
/**
 * �Ŵ���������	
 */
	private void impxdadvice(){
		vo = list.getSelectedBillVO();
		if (vo == null) {
			MessageBox.show(this, "��ѡ��һ������");
			return;
		}
		Date dt = new Date();
		SimpleDateFormat matter1 = new SimpleDateFormat("yyyy��MM��dd��");
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
		dataMap.put("name", vo.getStringValue("name"));
		HashVO[] planvo;
		try {
			planvo = UIUtil.getHashVoArrayByDS(null,
					"select * from CK_PLAN where id='"
							+ vo.getStringValue("planid") + "'");
			String[] time1 = planvo[0].getStringValue("PLANBEGINDATE")
					.toString().split("-");
			String[] time2 = planvo[0].getStringValue("PLANENDDATE").toString()
					.split("-");
			dataMap.put("time", time1[0].toString() + "��" + time1[1].toString()
					+ "��" + time1[2].toString() + "����" + time2[0].toString()
					+ "��" + time2[1].toString() + "��" + time2[2].toString()
					+ "��");
			String[] deptid = UIUtil.getStringArrayFirstColByDS(null,
					"select deptid from V_CK_SCHEME where 1=1 and  schemeid='"
							+ vo.getStringValue("id") + "'");
			dataMap.put("count", deptid.length + "������");
			String[] problemct = UIUtil
			.getStringArrayFirstColByDS(
					null,
					"select b.checkpoints from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"+ vo.getStringValue("id")+"' group by b.checkpoints");
	int countpm = 0;
	StringBuilder sb = new StringBuilder();
	for (int i = 0; i < problemct.length; i++) {
		countpm = countpm + 1;
		String numpm = ZZLUIUtil.numTurnStringNum(countpm);
		sb.append("��" + numpm + "��\r\r\n");
		sb.append(problemct[i].toString() + "\r");
		HashVO [] problemdept = UIUtil
				.getHashVoArrayByDS(
						null,
						"select * from V_CK_SCHEME_IMPLEMENT where schemeid='"+ vo.getStringValue("id")+ "' and checkpoints='"
								+ problemct[i].toString() + "' and result='��Ч'");
		int imp=0;
		for (int j = 0; j < problemdept.length; j++) {
			HashVO  [] ipvo=UIUtil.getHashVoArrayByDS(null, "select * from CK_SCHEME_IMPL where  id='"+problemdept[j].getStringValue("implid")+"'  or refimplid='"+problemdept[j].getStringValue("implid")+"' order by refimplid");
			String money=UIUtil.getStringValueByDS(null, "select sum(c8)/10000 from CK_SCHEME_IMPL where id='"+problemdept[j].getStringValue("implid")+"' or refimplid='"+problemdept[j].getStringValue("implid")+"' order by refimplid");
			if(ipvo.length>1){
			imp=imp+1;
			sb.append(imp).append("��");
			sb.append("�����"+ipvo[0].getStringValue("c1")+"����С����"+ZZLUIUtil.getTimeString(ipvo[0].getStringValue("c6"))+"��"+ipvo[0].getStringValue("c16"));
			sb.append("������������"+money+"��Ԫ������"+ipvo[0].getStringValue("c18"));
			sb.append("�����У�");
			for(int p=0;p<ipvo.length;p++){
				sb.append(ipvo[p].getStringValue("c1")+"����"+Double.parseDouble(ipvo[p].getStringValue("c8"))/10000+"��Ԫ���������ʣ�");
				String strdb=ipvo[p].getStringValue("c11");
				strdb=strdb.substring(0,strdb.indexOf("."));
				int idb=Integer.parseInt(strdb);
				if(idb==0){
					Double db=Double.parseDouble(ipvo[p].getStringValue("c11"));
					db=db*1000;
					sb.append(db+"�룻");
				}else{
					sb.append(ipvo[p].getStringValue("c11")+"�룻");
				}
			}
			sb.append("������"+ZZLUIUtil.getTimeString(ipvo[0].getStringValue("c7"))+"���ڣ�");
			
			if(ipvo[0].getStringValue("usera1")!=null){
				sb.append("����������A�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera1"))+"��");
			}
			if(ipvo[0].getStringValue("userb1")!=null){
				sb.append("����������B�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb1"))+"��");
			}
			if(ipvo[0].getStringValue("usera2")!=null){
				sb.append("���������A�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera2"))+"��");
			}
			if(ipvo[0].getStringValue("userb2")!=null){
				sb.append("���������B�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb2"))+"��");
			}
			if(ipvo[0].getStringValue("usera3")!=null){
				sb.append("����������A�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera3"))+"��");
			}
			if(ipvo[0].getStringValue("userb3")!=null){
				sb.append("����������B�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb3"))+"��");
			}
			if(ipvo[0].getStringValue("usera4")!=null){
				sb.append("��Ӫ������A�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera4"))+"��");
			}
			if(ipvo[0].getStringValue("userb4")!=null){
				sb.append("��Ӫ������B�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb4"))+"��");
			}
			HashVO[] problemvo = UIUtil
					.getHashVoArrayByDS(
							null,
							"select * from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
									+ vo.getStringValue("id")
									+ "' and b.checkpoints='"
									+ problemct[i].toString()
									+ "' and a.deptid='"
									+ problemdept[j].getStringValue("deptid") + "' and b.implid='"+problemdept[j].getStringValue("implid")+"'");
			int count = 0;
			for (int k = 0; k < problemvo.length; k++) {
				count = count + 1;
				String bmname = ZZLUIUtil.getdeptsSplit(problemvo[k]
						.getStringValue("deptid"));
				sb.append("��" + count + "��" + bmname + ":");
				
				sb.append(problemvo[k].getStringValue("probleminfo")
						+ "\r");
				if(problemvo[k].getStringValue("adjustuserid")!=null){
				String usname = ZZLUIUtil.getnamesSplit(problemvo[k]
						.getStringValue("adjustuserid"));
				usname = usname.replace(",", "");
				sb.append("�����ˣ�" + usname + "\r");
				}
			}
			}else{
				int grint=0;
					grint=grint+imp+1;
					sb.append(grint).append("��");
					sb.append("�����"+ipvo[0].getStringValue("c1")+"��"+ZZLUIUtil.getTimeString(ipvo[0].getStringValue("c6"))+"��"+ipvo[0].getStringValue("c16"));
					sb.append("��������"+money+"��Ԫ������"+ipvo[0].getStringValue("c18"));
					sb.append("��"+ZZLUIUtil.getTimeString(ipvo[0].getStringValue("c7"))+"���ڣ���������");
					String strdb=ipvo[0].getStringValue("c11");
					strdb=strdb.substring(0,strdb.indexOf("."));
					int idb=Integer.parseInt(strdb);
					if(idb==0){
						Double db=Double.parseDouble(ipvo[0].getStringValue("c11"));
						db=db*1000;
						sb.append(db+"�룻");
					}else{
						sb.append(ipvo[0].getStringValue("c11")+"�룻");
					}
					imp=grint;
				
				
				if(ipvo[0].getStringValue("usera1")!=null){
					sb.append("����������A�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera1"))+"��");
				}
				if(ipvo[0].getStringValue("userb1")!=null){
					sb.append("����������B�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb1"))+"��");
				}
				if(ipvo[0].getStringValue("usera2")!=null){
					sb.append("���������A�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera2"))+"��");
				}
				if(ipvo[0].getStringValue("userb2")!=null){
					sb.append("���������B�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb2"))+"��");
				}
				if(ipvo[0].getStringValue("usera3")!=null){
					sb.append("����������A�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera3"))+"��");
				}
				if(ipvo[0].getStringValue("userb3")!=null){
					sb.append("����������B�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb3"))+"��");
				}
				if(ipvo[0].getStringValue("usera4")!=null){
					sb.append("��Ӫ������A�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera4"))+"��");
				}
				if(ipvo[0].getStringValue("userb4")!=null){
					sb.append("��Ӫ������B�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb4"))+"��");
				}
				HashVO[] problemvo = UIUtil
						.getHashVoArrayByDS(
								null,
								"select * from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
										+ vo.getStringValue("id")
										+ "' and b.checkpoints='"
										+ problemct[i].toString()
										+ "' and a.deptid='"
										+ problemdept[j].getStringValue("deptid") + "' and b.implid='"+problemdept[j].getStringValue("implid")+"'");
				int count = 0;
				for (int k = 0; k < problemvo.length; k++) {
					count = count + 1;
					String bmname = ZZLUIUtil.getdeptsSplit(problemvo[k]
							.getStringValue("deptid"));
					sb.append("��" + count + "��" + bmname + ":");
					
					sb.append(problemvo[k].getStringValue("probleminfo")
							+ "\r");
					if(problemvo[k].getStringValue("adjustuserid")!=null){
					String usname = ZZLUIUtil.getnamesSplit(problemvo[k]
							.getStringValue("adjustuserid"));
					usname = usname.replace(",", "");
					sb.append("�����ˣ�" + usname + "\r");
					}
				}
				
			}
		}
		sb.append("������ʵΥ���ˡ��½�ά���������ũ�����ú�����XXX�ƶȡ������ݡ��½�����ũ����ҵ���йɷ����޹�˾Ա��Υ�������ƶȴ����涨����X�µ�x�ڵ�XX����ع涨���������������XX������");
			}
			dataMap.put("problem", sb);
			dataMap.put("time2", matter1.format(dt));
			savePath = savePath + "\\" + dataMap.get("name") + "-��������.doc";
			WordExport we = new WordExport();
			we.createDoc(dataMap, "WordAdviceModel", savePath);
			String[][] ck_record = UIUtil.getStringArrayByDS(null,
					"select id,advice from ck_wl_record where  schemeid='"+ vo.getStringValue("id") + "'");
			if (ck_record.length <= 0) {
				InsertSQLBuilder insert = new InsertSQLBuilder("ck_wl_record");
				String id = UIUtil.getSequenceNextValByDS(null,
						"s_ck_wl_record");
				insert.putFieldValue("id", id);
				insert.putFieldValue("userid", loginUserid);
				insert.putFieldValue("advice", "Y");
				insert.putFieldValue("schemeid", vo.getStringValue("id"));
				UIUtil.executeUpdateByDS(null, insert.getSQL());
			} else if (ck_record.length == 1 || ck_record[0][1] == null
					|| ck_record[0][1].equals("")
					|| ck_record[0][1].equals(null)
					|| ck_record[0][1].equals("null")) {
				UpdateSQLBuilder update = new UpdateSQLBuilder("ck_wl_record");
				update.setWhereCondition("id=" + ck_record[0][0]);
				update.putFieldValue("advice", "Y");
				update.putFieldValue("schemeid", vo.getStringValue("id"));
				UIUtil.executeUpdateByDS(null, update.getSQL());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MessageBox.show(this, "�����ɹ�");
	}

	/**
	 * ����
	 */
	private void impreport() {
		vo = list.getSelectedBillVO();
		if (vo == null) {
			MessageBox.show(this, "��ѡ��һ������");
			return;
		}
		BillCardDialog listDialog = new BillCardDialog(this,
				"Ϊ��������������ʵ�����ѡ���ϴμ�����Ŀ����", "CK_PROBLEM_INFO_ZZL_EQ1_2", 500,
				200);
		listDialog.setVisible(true);
		BillVO vos = listDialog.getBillcardPanel().getBillVO();
		String scschemeid = vos.getStringValue("schemeid");
		Date dt = new Date();
		SimpleDateFormat matter1 = new SimpleDateFormat("yyyy��MM��dd��");
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
		try {
			String[] sc = UIUtil
					.getStringArrayFirstColByDS(
							null,
							"select * from ck_problem_info where probleminfo is null and trackresult is null");
			if (sc.length > 0) {
				UIUtil
						.executeUpdateByDS(null,
								"delete from ck_problem_info where probleminfo is null and trackresult is null");

			}
			String title=title(vo.getStringValue("planid"));
			dataMap.put("title", title);
			dataMap
					.put("yaer", vo.getStringValue("createdate")
							.substring(0, 4));
			dataMap.put("code", vo.getStringValue("code"));
			dataMap.put("name", vo.getStringValue("name"));
			String[] deptid = UIUtil.getStringArrayFirstColByDS(null,
					"select deptid from V_CK_SCHEME  where schemeid='"
							+ vo.getStringValue("id") + "'");
			StringBuilder deptsb = new StringBuilder();
			for (int i = 0; i < deptid.length; i++) {
				String deptname = UIUtil.getStringValueByDS(null,
						"select name from pub_corp_dept where id='"
								+ deptid[i].toString() + "'");
				deptsb.append(deptname + ",");
			}
			dataMap.put("dept", deptsb);
			String ckdeptid = UIUtil.getStringValueByDS(null,
					"select CHECKDEPT from CK_PLAN where id='"
							+ vo.getStringValue("planid") + "'");
			String deptname = UIUtil.getStringValueByDS(null,
					"select name from pub_corp_dept where id='" + ckdeptid
							+ "'");
			dataMap.put("ckdept", deptname);
			dataMap.put("time", matter1.format(dt));
			String LEADER=ZZLUIUtil.getUserName(vo.getStringValue("LEADER"));
			dataMap.put("LEADER", LEADER);
			String REFEREE = ZZLUIUtil.getnamesSplit(vo
					.getStringValue("REFEREE"));
			dataMap.put("REFEREE", REFEREE);
			HashVO[] fzvo = UIUtil.getHashVoArrayByDS(null,
					"select * from CK_MEMBER_WORK where schemeid ='"
							+ vo.getStringValue("id") + "'");
			int numa = 0;
			int users = 0;
			for (int i = 0; i < fzvo.length; i++) {
				numa = numa + i;
				int countnum = i + 1;
				String count = ZZLUIUtil.numTurnStringNum(countnum);
				String leader = ZZLUIUtil.getnamesSplit(fzvo[i]
						.getStringValue("leader"));
				leader=leader.replace("��", "");
				String a = String.valueOf(numa);
				dataMap.put("name" + a, leader);
				dataMap.put("position" + a, "��" + count + "С�� �鳤");
				String[] teamusers = ZZLUIUtil.getTeamNames(fzvo[i]
						.getStringValue("teamusers"));
				for (int j = 0; j < teamusers.length; j++) {
					users = numa + j + 1;
					String b = String.valueOf(users);
					dataMap.put("name" + b, teamusers[j].toString());
					dataMap.put("position" + b, "��" + count + "С�� ��Ա");
				}
				numa = users;
				String depts = ZZLUIUtil.getdeptsSplit(fzvo[i]
						.getStringValue("checkeddept"));
				dataMap.put("depts" + a, "�����飺" + depts);
			}
			HashVO[] planvo = UIUtil.getHashVoArrayByDS(null,
					"select * from CK_PLAN where id='"
							+ vo.getStringValue("planid") + "'");
			String[] time1 = planvo[0].getStringValue("PLANBEGINDATE")
					.toString().split("-");
			String[] time2 = planvo[0].getStringValue("PLANENDDATE").toString()
					.split("-");
			dataMap.put("time2", time1[0].toString() + "��"
					+ time1[1].toString() + "��" + time1[2].toString() + "����"
					+ time2[0].toString() + "��" + time2[1].toString() + "��"
					+ time2[2].toString() + "��");
			String username = ZZLUIUtil.getnamesSplit(loginUserid);
			dataMap.put("username", username);
			String[] problemct = UIUtil
					.getStringArrayFirstColByDS(
							null,
							"select distinct(b.checkpoints) from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
									+ vo.getStringValue("id") + "'");
			int countpm = 0;
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < problemct.length; i++) {
				countpm = countpm + 1;
				String numpm = ZZLUIUtil.numTurnStringNum(countpm);
				sb.append("��" + numpm + "��\r\n");
				sb.append(problemct[i].toString() + "\r");
				int count = 0;
				String[] problemdept = UIUtil
						.getStringArrayFirstColByDS(
								null,
								"select distinct(a.deptid) from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
										+ vo.getStringValue("id")
										+ "' and b.checkpoints='"
										+ problemct[i].toString() + "'");
				for (int j = 0; j < problemdept.length; j++) {
					HashVO[] problemvo = UIUtil
							.getHashVoArrayByDS(
									null,
									"select * from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
											+ vo.getStringValue("id")
											+ "' and b.checkpoints='"
											+ problemct[i].toString()
											+ "' and a.deptid='"
											+ problemdept[j].toString() + "'");
					for (int k = 0; k < problemvo.length; k++) {
						count = count + 1;
						String bmname = ZZLUIUtil.getdeptsSplit(problemvo[k]
								.getStringValue("deptid"));
						sb.append("��" + count + "��" + bmname + ":");
						sb.append(problemvo[k].getStringValue("probleminfo")
								+ "\r");
						String usname = ZZLUIUtil.getnamesSplit(problemvo[k]
								.getStringValue("adjustuserid"));
						if(usname!=null){
						usname = usname.replace(",", "");
						sb.append("�����ˣ�" + usname + "\r");
						}
					}

				}
				if (scschemeid != null) {
					if (i == problemct.length - 1) {
						int countzg = problemct.length + 1;
						String numzg = ZZLUIUtil.numTurnStringNum(countzg);
						sb.append("��" + numzg + "�� ���������ʵ��");
						String[] zgdeptid = UIUtil.getStringArrayFirstColByDS(
								null,
								"select deptid from V_CK_SCHEME where 1=1 and  schemeid='"
										+ vo.getStringValue("id") + "'");
						StringBuilder zgsb = new StringBuilder();
						for (int s = 0; s < zgdeptid.length; s++) {
							zgsb.append(zgdeptid[s].toString());
							zgsb.append(",");
						}
						String deptids = zgsb.toString();
						deptids = deptids.substring(0, deptids.length() - 1);
						HashVO[] zgvo = UIUtil
								.getHashVoArrayByDS(
										null,
										"select * from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
												+ scschemeid
												+ "' and a.deptid in("
												+ deptids + ")");
						HashVO[] yzgvo = UIUtil
								.getHashVoArrayByDS(
										null,
										"select * from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
												+ scschemeid
												+ "' and a.deptid in("
												+ deptids
												+ ") and a.trackresult='������'");
						HashVO[] bzgvo = UIUtil
								.getHashVoArrayByDS(
										null,
										"select * from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
												+ scschemeid
												+ "' and a.deptid in("
												+ deptids
												+ ") and a.trackresult='��������'");
						HashVO[] vzgvo = UIUtil
								.getHashVoArrayByDS(
										null,
										"select * from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
												+ scschemeid
												+ "' and a.deptid in("
												+ deptids
												+ ") and a.trackresult='δ����'");
						String planname = UIUtil.getStringValueByDS(null,
								"select NAME from CK_SCHEME where id='"
										+ scschemeid + "'");
						sb.append("���μ���" + zgdeptid.length + "��֧����" + planname
								+ "��鷢������" + zgvo.length + "�������"
								+ yzgvo.length + "���������" + bzgvo.length
								+ "�δ����" + vzgvo.length + "��.\r");
						sb.append("������������Ϊ��");
						String[] bzgct = UIUtil
								.getStringArrayFirstColByDS(
										null,
										"select distinct(b.checkpoints) from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
												+ scschemeid
												+ "' and a.deptid in("
												+ deptids
												+ ") and a.trackresult='��������'");
						int bzgctpm = 0;
						for (int b = 0; b < bzgct.length; b++) {
							bzgctpm = bzgctpm + 1;
							String bzgpm = ZZLUIUtil.numTurnStringNum(bzgctpm);
							sb.append("��" + bzgpm + "��\r\r\n");
							sb.append(bzgct[b].toString() + "\r");
							int bcount = 0;
							String[] bproblemdept = UIUtil
									.getStringArrayFirstColByDS(
											null,
											"select distinct(a.deptid) from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
													+ scschemeid
													+ "' and a.deptid in("
													+ deptids
													+ ") and a.trackresult='��������' and b.checkpoints='"
													+ bzgct[b].toString() + "'");
							for (int j = 0; j < bproblemdept.length; j++) {
								HashVO[] bproblemvo = UIUtil
										.getHashVoArrayByDS(
												null,
												"select * from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
														+ scschemeid
														+ "' and b.checkpoints='"
														+ bzgct[b].toString()
														+ "' and a.deptid='"
														+ bproblemdept[j]
																.toString()
														+ "' and a.trackresult='��������'");
								for (int k = 0; k < bproblemvo.length; k++) {
									bcount = bcount + 1;
									String bmname = ZZLUIUtil
											.getdeptsSplit(bproblemvo[k]
													.getStringValue("deptid"));
									sb
											.append("��" + bcount + "��" + bmname
													+ ":");
									sb.append(bproblemvo[k]
											.getStringValue("probleminfo")
											+ "\r");
									String usname = ZZLUIUtil
											.getnamesSplit(bproblemvo[k]
													.getStringValue("adjustuserid"));
									usname = usname.replace(",", "");
									sb.append("�����ˣ�" + usname + "\r"
											+ "�������ġ�\r");
								}

							}

						}
						sb.append("δ��������Ϊ��");
						String[] vzgct = UIUtil
								.getStringArrayFirstColByDS(
										null,
										"select distinct(b.checkpoints) from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
												+ scschemeid
												+ "' and a.deptid in("
												+ deptids
												+ ") and a.trackresult='δ����'");
						int vzgctpm = 0;
						for (int b = 0; b < vzgct.length; b++) {
							vzgctpm = vzgctpm + 1;
							String vzgpm = ZZLUIUtil.numTurnStringNum(vzgctpm);
							sb.append("��" + vzgpm + "��\r\r\n");
							sb.append(vzgct[b].toString() + "\r");
							int vcount = 0;
							String[] vproblemdept = UIUtil
									.getStringArrayFirstColByDS(
											null,
											"select distinct(a.deptid) from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
													+ scschemeid
													+ "' and a.deptid in("
													+ deptids
													+ ") and a.trackresult='δ����' and b.checkpoints='"
													+ vzgct[b].toString() + "'");
							for (int j = 0; j < vproblemdept.length; j++) {
								HashVO[] vproblemvo = UIUtil
										.getHashVoArrayByDS(
												null,
												"select * from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
														+ scschemeid
														+ "' and b.checkpoints='"
														+ vzgct[b].toString()
														+ "' and a.deptid='"
														+ vproblemdept[j]
																.toString()
														+ "' and a.trackresult='δ����'");
								for (int k = 0; k < vproblemvo.length; k++) {
									vcount = vcount + 1;
									String bmname = ZZLUIUtil
											.getdeptsSplit(vproblemvo[k]
													.getStringValue("deptid"));
									sb
											.append("��" + vcount + "��" + bmname
													+ ":");
									sb.append(vproblemvo[k]
											.getStringValue("probleminfo")
											+ "\r");
									String usname = ZZLUIUtil
											.getnamesSplit(vproblemvo[k]
													.getStringValue("adjustuserid"));
									usname = usname.replace(",", "");
									sb.append("�����ˣ�" + usname + "\r"
											+ "δ���ġ�\r");
								}

							}

						}
					}
				}
			}
			dataMap.put("problem", sb);
			savePath = savePath + "\\" + dataMap.get("name") + "-��鱨��.doc";
			WordExport we = new WordExport();
			we.createDoc(dataMap, "WordReportMdole", savePath);
			String[][] ck_record = UIUtil.getStringArrayByDS(null,
					"select id,report from ck_wl_record where schemeid='"+ vo.getStringValue("id") + "'");
			if (ck_record.length <= 0) {
				InsertSQLBuilder insert = new InsertSQLBuilder("ck_wl_record");
				String id = UIUtil.getSequenceNextValByDS(null,
						"s_ck_wl_record");
				insert.putFieldValue("id", id);
				insert.putFieldValue("userid", loginUserid);
				insert.putFieldValue("report", "Y");
				insert.putFieldValue("schemeid", vo.getStringValue("id"));
				UIUtil.executeUpdateByDS(null, insert.getSQL());
			} else if (ck_record.length == 1 || ck_record[0][1] == null
					|| ck_record[0][1].equals("")
					|| ck_record[0][1].equals(null)
					|| ck_record[0][1].equals("null")) {
				UpdateSQLBuilder update = new UpdateSQLBuilder("ck_wl_record");
				update.setWhereCondition("id=" + ck_record[0][0]);
				update.putFieldValue("report", "Y");
				update.putFieldValue("schemeid", vo.getStringValue("id"));
				UIUtil.executeUpdateByDS(null, update.getSQL());
			}
			MessageBox.show(this, "�����ɹ�");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * �Ŵ�����
	 */
private void impxdreport(){
	vo = list.getSelectedBillVO();
	if (vo == null) {
		MessageBox.show(this, "��ѡ��һ������");
		return;
	}
	BillCardDialog listDialog = new BillCardDialog(this,
			"Ϊ��������������ʵ�����ѡ���ϴμ�����Ŀ����", "CK_PROBLEM_INFO_ZZL_EQ1_2", 500,
			200);
	listDialog.setVisible(true);
	BillVO vos = listDialog.getBillcardPanel().getBillVO();
	String scschemeid = vos.getStringValue("schemeid");
	Date dt = new Date();
	SimpleDateFormat matter1 = new SimpleDateFormat("yyyy��MM��dd��");
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
	try {
		String[] sc = UIUtil
				.getStringArrayFirstColByDS(
						null,
						"select * from ck_problem_info where probleminfo is null and trackresult is null");
		if (sc.length > 0) {
			UIUtil
					.executeUpdateByDS(null,
							"delete from ck_problem_info where probleminfo is null and trackresult is null");

		}
		String title=title(vo.getStringValue("plantype"));
		dataMap.put("title", title);
		dataMap
				.put("yaer", vo.getStringValue("createdate")
						.substring(0, 4));
		dataMap.put("code", vo.getStringValue("code"));
		dataMap.put("name", vo.getStringValue("name"));
		String[] deptid = UIUtil.getStringArrayFirstColByDS(null,
				"select deptid from V_CK_SCHEME  where schemeid='"
						+ vo.getStringValue("id") + "'");
		StringBuilder deptsb = new StringBuilder();
		for (int i = 0; i < deptid.length; i++) {
			String deptname = UIUtil.getStringValueByDS(null,
					"select name from pub_corp_dept where id='"
							+ deptid[i].toString() + "'");
			deptsb.append(deptname + ",");
		}
		dataMap.put("dept", deptsb);
		String ckdeptid = UIUtil.getStringValueByDS(null,
				"select CHECKDEPT from CK_PLAN where id='"
						+ vo.getStringValue("planid") + "'");
		String deptname = UIUtil.getStringValueByDS(null,
				"select name from pub_corp_dept where id='" + ckdeptid
						+ "'");
		dataMap.put("ckdept", deptname);
		dataMap.put("time", matter1.format(dt));
		String LEADER=ZZLUIUtil.getUserName(vo.getStringValue("LEADER"));
		dataMap.put("LEADER", LEADER);
		String REFEREE = ZZLUIUtil.getnamesSplit(vo
				.getStringValue("REFEREE"));
		dataMap.put("REFEREE", REFEREE);
		HashVO[] fzvo = UIUtil.getHashVoArrayByDS(null,
				"select * from CK_MEMBER_WORK where schemeid ='"
						+ vo.getStringValue("id") + "'");
		int numa = 0;
		int users = 0;
		for (int i = 0; i < fzvo.length; i++) {
			numa = numa + i;
			int countnum = i + 1;
			String count = ZZLUIUtil.numTurnStringNum(countnum);
			String leader = ZZLUIUtil.getnamesSplit(fzvo[i]
					.getStringValue("leader"));
			leader=leader.replace("��", "");
			String a = String.valueOf(numa);
			dataMap.put("name" + a, leader);
			dataMap.put("position" + a, "��" + count + "С�� �鳤");
			String[] teamusers = ZZLUIUtil.getTeamNames(fzvo[i]
					.getStringValue("teamusers"));
			for (int j = 0; j < teamusers.length; j++) {
				users = numa + j + 1;
				String b = String.valueOf(users);
				dataMap.put("name" + b, teamusers[j].toString());
				dataMap.put("position" + b, "��" + count + "С�� ��Ա");
			}
			numa = users;
			String depts = ZZLUIUtil.getdeptsSplit(fzvo[i]
					.getStringValue("checkeddept"));
			dataMap.put("depts" + a, "�����飺" + depts);
		}
		HashVO[] planvo = UIUtil.getHashVoArrayByDS(null,
				"select * from CK_PLAN where id='"
						+ vo.getStringValue("planid") + "'");
		String[] time1 = planvo[0].getStringValue("PLANBEGINDATE")
				.toString().split("-");
		String[] time2 = planvo[0].getStringValue("PLANENDDATE").toString()
				.split("-");
		dataMap.put("time2", time1[0].toString() + "��"
				+ time1[1].toString() + "��" + time1[2].toString() + "����"
				+ time2[0].toString() + "��" + time2[1].toString() + "��"
				+ time2[2].toString() + "��");
		String username = ZZLUIUtil.getnamesSplit(loginUserid);
		dataMap.put("username", username);
		String[] problemct = UIUtil
				.getStringArrayFirstColByDS(
						null,
						"select b.checkpoints from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"+ vo.getStringValue("id")+"' group by b.checkpoints");
		int countpm = 0;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < problemct.length; i++) {
			countpm = countpm + 1;
			String numpm = ZZLUIUtil.numTurnStringNum(countpm);
			sb.append("��" + numpm + "��\r\r\n");
			sb.append(problemct[i].toString() + "\r");
			HashVO [] problemdept = UIUtil
					.getHashVoArrayByDS(
							null,
							"select * from V_CK_SCHEME_IMPLEMENT where schemeid='"+ vo.getStringValue("id")+ "' and checkpoints='"
									+ problemct[i].toString() + "' and result='��Ч'");
			int imp=0;
			for (int j = 0; j < problemdept.length; j++) {
				HashVO  [] ipvo=UIUtil.getHashVoArrayByDS(null, "select * from CK_SCHEME_IMPL where  id='"+problemdept[j].getStringValue("implid")+"'  or refimplid='"+problemdept[j].getStringValue("implid")+"' order by refimplid");
				String money=UIUtil.getStringValueByDS(null, "select sum(c8)/10000 from CK_SCHEME_IMPL where id='"+problemdept[j].getStringValue("implid")+"' or refimplid='"+problemdept[j].getStringValue("implid")+"' order by refimplid");
				if(ipvo.length>1){
				imp=imp+1;
				sb.append(imp).append("��");
				sb.append("�����"+ipvo[0].getStringValue("c1")+"����С����"+ZZLUIUtil.getTimeString(ipvo[0].getStringValue("c6"))+"��"+ipvo[0].getStringValue("c16"));
				sb.append("������������"+money+"��Ԫ������"+ipvo[0].getStringValue("c18"));
				sb.append("�����У�");
				for(int p=0;p<ipvo.length;p++){
					sb.append(ipvo[p].getStringValue("c1")+"����"+Double.parseDouble(ipvo[p].getStringValue("c8"))/10000+"��Ԫ���������ʣ�");
					String strdb=ipvo[p].getStringValue("c11");
					strdb=strdb.substring(0,strdb.indexOf("."));
					int idb=Integer.parseInt(strdb);
					if(idb==0){
						Double db=Double.parseDouble(ipvo[p].getStringValue("c11"));
						db=db*1000;
						sb.append(db+"�룻");
					}else{
						sb.append(ipvo[p].getStringValue("c11")+"�룻");
					}
				}
				sb.append("������"+ZZLUIUtil.getTimeString(ipvo[0].getStringValue("c7"))+"���ڣ�");
				
				if(ipvo[0].getStringValue("usera1")!=null){
					sb.append("����������A�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera1"))+"��");
				}
				if(ipvo[0].getStringValue("userb1")!=null){
					sb.append("����������B�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb1"))+"��");
				}
				if(ipvo[0].getStringValue("usera2")!=null){
					sb.append("���������A�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera2"))+"��");
				}
				if(ipvo[0].getStringValue("userb2")!=null){
					sb.append("���������B�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb2"))+"��");
				}
				if(ipvo[0].getStringValue("usera3")!=null){
					sb.append("����������A�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera3"))+"��");
				}
				if(ipvo[0].getStringValue("userb3")!=null){
					sb.append("����������B�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb3"))+"��");
				}
				if(ipvo[0].getStringValue("usera4")!=null){
					sb.append("��Ӫ������A�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera4"))+"��");
				}
				if(ipvo[0].getStringValue("userb4")!=null){
					sb.append("��Ӫ������B�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb4"))+"��");
				}
				HashVO[] problemvo = UIUtil
						.getHashVoArrayByDS(
								null,
								"select * from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
										+ vo.getStringValue("id")
										+ "' and b.checkpoints='"
										+ problemct[i].toString()
										+ "' and a.deptid='"
										+ problemdept[j].getStringValue("deptid") + "' and b.implid='"+problemdept[j].getStringValue("implid")+"'");
				int count = 0;
				for (int k = 0; k < problemvo.length; k++) {
					count = count + 1;
					String bmname = ZZLUIUtil.getdeptsSplit(problemvo[k]
							.getStringValue("deptid"));
					sb.append("��" + count + "��" + bmname + ":");
					
					sb.append(problemvo[k].getStringValue("probleminfo")
							+ "\r");
					if(problemvo[k].getStringValue("adjustuserid")!=null){
					String usname = ZZLUIUtil.getnamesSplit(problemvo[k]
							.getStringValue("adjustuserid"));
					usname = usname.replace(",", "");
					sb.append("�����ˣ�" + usname + "\r");
					}
				}
				}else{
					int grint=0;
						grint=grint+imp+1;
						sb.append(grint).append("��");
						if(ipvo[0].getStringValue("c6")!=null){
						sb.append("�����"+ipvo[0].getStringValue("c1")+"��"+ZZLUIUtil.getTimeString(ipvo[0].getStringValue("c6"))+"��"+ipvo[0].getStringValue("c16"));
						}
						sb.append("��������"+money+"��Ԫ������"+ipvo[0].getStringValue("c18"));
						if(ipvo[0].getStringValue("c7")!=null){
						sb.append("��"+ZZLUIUtil.getTimeString(ipvo[0].getStringValue("c7"))+"���ڣ���������");
						}
						String strdb=null;
						if(ipvo[0].getStringValue("c11")!=null){
						strdb=ipvo[0].getStringValue("c11");
						strdb=strdb.substring(0,strdb.indexOf("."));
						
						int idb=Integer.parseInt(strdb);
						if(idb==0){
							Double db=Double.parseDouble(ipvo[0].getStringValue("c11"));
							db=db*1000;
							sb.append(db+"�룻");
						}else{
							sb.append(ipvo[0].getStringValue("c11")+"�룻");
						}
						imp=grint;
						}
					
					if(ipvo[0].getStringValue("usera1")!=null){
						sb.append("����������A�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera1"))+"��");
					}
					if(ipvo[0].getStringValue("userb1")!=null){
						sb.append("����������B�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb1"))+"��");
					}
					if(ipvo[0].getStringValue("usera2")!=null){
						sb.append("���������A�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera2"))+"��");
					}
					if(ipvo[0].getStringValue("userb2")!=null){
						sb.append("���������B�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb2"))+"��");
					}
					if(ipvo[0].getStringValue("usera3")!=null){
						sb.append("����������A�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera3"))+"��");
					}
					if(ipvo[0].getStringValue("userb3")!=null){
						sb.append("����������B�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb3"))+"��");
					}
					if(ipvo[0].getStringValue("usera4")!=null){
						sb.append("��Ӫ������A�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera4"))+"��");
					}
					if(ipvo[0].getStringValue("userb4")!=null){
						sb.append("��Ӫ������B�ǣ�"+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb4"))+"��");
					}
					HashVO[] problemvo = UIUtil
							.getHashVoArrayByDS(
									null,
									"select * from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
											+ vo.getStringValue("id")
											+ "' and b.checkpoints='"
											+ problemct[i].toString()
											+ "' and a.deptid='"
											+ problemdept[j].getStringValue("deptid") + "' and b.implid='"+problemdept[j].getStringValue("implid")+"'");
					int count = 0;
					for (int k = 0; k < problemvo.length; k++) {
						count = count + 1;
						String bmname = ZZLUIUtil.getdeptsSplit(problemvo[k]
								.getStringValue("deptid"));
						sb.append("��" + count + "��" + bmname + ":");
						
						sb.append(problemvo[k].getStringValue("probleminfo")
								+ "\r");
						if(problemvo[k].getStringValue("adjustuserid")!=null){
						String usname = ZZLUIUtil.getnamesSplit(problemvo[k]
								.getStringValue("adjustuserid"));
						usname = usname.replace(",", "");
						sb.append("�����ˣ�" + usname + "\r");
						}
					}
					
				}
			}
			if (scschemeid != null) {
				if (i == problemct.length - 1) {
					int countzg = problemct.length + 1;
					String numzg = ZZLUIUtil.numTurnStringNum(countzg);
					sb.append("��" + numzg + "�� ���������ʵ��");
					String[] zgdeptid = UIUtil.getStringArrayFirstColByDS(
							null,
							"select deptid from V_CK_SCHEME where 1=1 and  schemeid='"
									+ vo.getStringValue("id") + "'");
					StringBuilder zgsb = new StringBuilder();
					for (int s = 0; s < zgdeptid.length; s++) {
						zgsb.append(zgdeptid[s].toString());
						zgsb.append(",");
					}
					String deptids = zgsb.toString();
					deptids = deptids.substring(0, deptids.length() - 1);
					HashVO[] zgvo = UIUtil
							.getHashVoArrayByDS(
									null,
									"select * from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
											+ scschemeid
											+ "' and a.deptid in("
											+ deptids + ")");
					HashVO[] yzgvo = UIUtil
							.getHashVoArrayByDS(
									null,
									"select * from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
											+ scschemeid
											+ "' and a.deptid in("
											+ deptids
											+ ") and a.trackresult='������'");
					HashVO[] bzgvo = UIUtil
							.getHashVoArrayByDS(
									null,
									"select * from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
											+ scschemeid
											+ "' and a.deptid in("
											+ deptids
											+ ") and a.trackresult='��������'");
					HashVO[] vzgvo = UIUtil
							.getHashVoArrayByDS(
									null,
									"select * from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
											+ scschemeid
											+ "' and a.deptid in("
											+ deptids
											+ ") and a.trackresult='δ����'");
					String planname = UIUtil.getStringValueByDS(null,
							"select NAME from CK_SCHEME where id='"
									+ scschemeid + "'");
					sb.append("���μ���" + zgdeptid.length + "��֧����" + planname
							+ "��鷢������" + zgvo.length + "�������"
							+ yzgvo.length + "���������" + bzgvo.length
							+ "�δ����" + vzgvo.length + "��.\r");
					sb.append("������������Ϊ��");
					String[] bzgct = UIUtil
							.getStringArrayFirstColByDS(
									null,
									"select distinct(b.checkpoints) from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
											+ scschemeid
											+ "' and a.deptid in("
											+ deptids
											+ ") and a.trackresult='��������'");
					int bzgctpm = 0;
					for (int b = 0; b < bzgct.length; b++) {
						bzgctpm = bzgctpm + 1;
						String bzgpm = ZZLUIUtil.numTurnStringNum(bzgctpm);
						sb.append("��" + bzgpm + "��\r\r\n");
						sb.append(bzgct[b].toString() + "\r");
						int bcount = 0;
						String[] bproblemdept = UIUtil
								.getStringArrayFirstColByDS(
										null,
										"select distinct(a.deptid) from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
												+ scschemeid
												+ "' and a.deptid in("
												+ deptids
												+ ") and a.trackresult='��������' and b.checkpoints='"
												+ bzgct[b].toString() + "'");
						for (int j = 0; j < bproblemdept.length; j++) {
							HashVO[] bproblemvo = UIUtil
									.getHashVoArrayByDS(
											null,
											"select * from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
													+ scschemeid
													+ "' and b.checkpoints='"
													+ bzgct[b].toString()
													+ "' and a.deptid='"
													+ bproblemdept[j]
															.toString()
													+ "' and a.trackresult='��������'");
							for (int k = 0; k < bproblemvo.length; k++) {
								bcount = bcount + 1;
								String bmname = ZZLUIUtil
										.getdeptsSplit(bproblemvo[k]
												.getStringValue("deptid"));
								sb
										.append("��" + bcount + "��" + bmname
												+ ":");
								sb.append(bproblemvo[k]
										.getStringValue("probleminfo")
										+ "\r");
								String usname = ZZLUIUtil
										.getnamesSplit(bproblemvo[k]
												.getStringValue("adjustuserid"));
								usname = usname.replace(",", "");
								sb.append("�����ˣ�" + usname + "\r"
										+ "�������ġ�\r");
							}

						}

					}
					sb.append("δ��������Ϊ��");
					String[] vzgct = UIUtil
							.getStringArrayFirstColByDS(
									null,
									"select distinct(b.checkpoints) from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
											+ scschemeid
											+ "' and a.deptid in("
											+ deptids
											+ ") and a.trackresult='δ����'");
					int vzgctpm = 0;
					for (int b = 0; b < vzgct.length; b++) {
						vzgctpm = vzgctpm + 1;
						String vzgpm = ZZLUIUtil.numTurnStringNum(vzgctpm);
						sb.append("��" + vzgpm + "��\r\r\n");
						sb.append(vzgct[b].toString() + "\r");
						int vcount = 0;
						String[] vproblemdept = UIUtil
								.getStringArrayFirstColByDS(
										null,
										"select distinct(a.deptid) from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
												+ scschemeid
												+ "' and a.deptid in("
												+ deptids
												+ ") and a.trackresult='δ����' and b.checkpoints='"
												+ vzgct[b].toString() + "'");
						for (int j = 0; j < vproblemdept.length; j++) {
							HashVO[] vproblemvo = UIUtil
									.getHashVoArrayByDS(
											null,
											"select * from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
													+ scschemeid
													+ "' and b.checkpoints='"
													+ vzgct[b].toString()
													+ "' and a.deptid='"
													+ vproblemdept[j]
															.toString()
													+ "' and a.trackresult='δ����'");
							for (int k = 0; k < vproblemvo.length; k++) {
								vcount = vcount + 1;
								String bmname = ZZLUIUtil
										.getdeptsSplit(vproblemvo[k]
												.getStringValue("deptid"));
								sb
										.append("��" + vcount + "��" + bmname
												+ ":");
								sb.append(vproblemvo[k]
										.getStringValue("probleminfo")
										+ "\r");
								String usname = ZZLUIUtil
										.getnamesSplit(vproblemvo[k]
												.getStringValue("adjustuserid"));
								usname = usname.replace(",", "");
								sb.append("�����ˣ�" + usname + "\r"
										+ "δ���ġ�\r");
							}

						}

					}
				}
			}
		}
		dataMap.put("problem", sb);
		savePath = savePath + "\\" + dataMap.get("name") + "-��鱨��.doc";
		WordExport we = new WordExport();
		we.createDoc(dataMap, "WordReportMdole", savePath);
		String[][] ck_record = UIUtil.getStringArrayByDS(null,
				"select id,report from ck_wl_record where schemeid='"+ vo.getStringValue("id") + "'");
		if (ck_record.length <= 0) {
			InsertSQLBuilder insert = new InsertSQLBuilder("ck_wl_record");
			String id = UIUtil.getSequenceNextValByDS(null,
					"s_ck_wl_record");
			insert.putFieldValue("id", id);
			insert.putFieldValue("userid", loginUserid);
			insert.putFieldValue("report", "Y");
			insert.putFieldValue("schemeid", vo.getStringValue("id"));
			UIUtil.executeUpdateByDS(null, insert.getSQL());
		} else if (ck_record.length == 1 || ck_record[0][1] == null
				|| ck_record[0][1].equals("")
				|| ck_record[0][1].equals(null)
				|| ck_record[0][1].equals("null")) {
			UpdateSQLBuilder update = new UpdateSQLBuilder("ck_wl_record");
			update.setWhereCondition("id=" + ck_record[0][0]);
			update.putFieldValue("report", "Y");
			update.putFieldValue("schemeid", vo.getStringValue("id"));
			UIUtil.executeUpdateByDS(null, update.getSQL());
		}
		MessageBox.show(this, "�����ɹ�");
	} catch (Exception e) {
		e.printStackTrace();
	}
}
    public String title(String id){
    	String count=null;
    	try{
    		count=UIUtil.getStringValueByDS(null, "select PLANTYPE from CK_PLAN where id='"+id+"'");
    		if(count.equals("��Ƽ��")){
    			count="��Ƽ�鱨����";
    		}else if(count.equals("���ɼ��")){
    			count="���ɼ�ܼ�鱨����";
    		}else{
    			count="��鱨����";
    		}
    		
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    			return count;
    		

    	
    }


}