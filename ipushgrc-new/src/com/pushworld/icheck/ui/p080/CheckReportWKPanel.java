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
		impreport = new WLTButton("导出检查报告");
		impadvice = new WLTButton("导出处理建议");
		impdecision = new WLTButton("导出处罚决定");
		imprisk = new WLTButton("导出风险提示书");
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
				MessageBox.show(null,"请选择一条记录");
				return;
			}
			if(vo.getStringValue("SCHEMETYPE").equals("其他")){
				impreport();
			}else{
				impxdreport();
			}
			
		} else if (e.getSource() == impadvice) {
			vo = list.getSelectedBillVO();
			if(vo==null){
				MessageBox.show(null,"请选择一条记录");
				return;
			}
			if(vo.getStringValue("SCHEMETYPE").equals("其他")){
				impadvice();
			}else{
				impxdadvice();
			}
			
		} else if (e.getSource() == impdecision) {
			vo = list.getSelectedBillVO();
			if(vo==null){
				MessageBox.show(null,"请选择一条记录");
				return;
			}
			if(vo.getStringValue("SCHEMETYPE").equals("其他")){
				impdecision();
			}else{
				impxddecision();
			}
			
		} else if (e.getSource() == imprisk) {
			imprisk();
		}

	}

	/**
	 * 风险提示
	 */
	private void imprisk() {
		vo = list.getSelectedBillVO();
		if (vo == null) {
			MessageBox.show(this, "请选择一条数据");
			return;
		}
		final JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("."));
		fc.setDialogTitle("请选择要保存到的目录");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setAcceptAllFileFilterUsed(false);
		int result = fc.showSaveDialog(this);
		if (result != JFileChooser.APPROVE_OPTION) { // 如果不是确定的
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
			savePath = savePath + "\\风险提示书.doc";
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
			MessageBox.show(this, "导出成功");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 *处罚决定
	 */
	private void impdecision() {
		vo = list.getSelectedBillVO();
		if (vo == null) {
			MessageBox.show(this, "请选择一条数据");
			return;
		}
		Date dt = new Date();
		SimpleDateFormat matter1 = new SimpleDateFormat("yyyy年MM月dd日");
		final JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("."));
		fc.setDialogTitle("请选择要保存到的目录");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setAcceptAllFileFilterUsed(false);
		int result = fc.showSaveDialog(this);
		if (result != JFileChooser.APPROVE_OPTION) { // 如果不是确定的
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
			dataMap.put("time", time1[0].toString() + "年" + time1[1].toString()
					+ "月" + time1[2].toString() + "日至" + time2[0].toString()
					+ "年" + time2[1].toString() + "月" + time2[2].toString()
					+ "日");
			String[] deptid = UIUtil.getStringArrayFirstColByDS(null,
					"select deptid from V_CK_SCHEME where 1=1 and  schemeid='"
							+ vo.getStringValue("id") + "'");
			dataMap.put("count", deptid.length + "个网点");
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
				sb.append("（" + numpm + "）\r\r\n");
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
						sb.append("（" + count + "）" + bmname + ":");
						sb.append(problemvo[k].getStringValue("probleminfo")
								+ "\r");
						String usname = ZZLUIUtil.getnamesSplit(problemvo[k]
								.getStringValue("adjustuserid"));
						if(usname!=null){
						usname = usname.replace(",", "");
						sb.append("责任人：" + usname + "\r");
						}
					}
				}
				sb
						.append("上述事实违反了《新疆维吾尔自治区农村信用合作社XXX制度》，依据《新疆昌吉农村商业银行股份有限公司员工违反规章制度处理规定》第X章第x节第XX条相关规定，决定给予责任人XX处罚。");
			}
			dataMap.put("problem", sb);
			dataMap.put("time2", matter1.format(dt));
			savePath = savePath + "\\" + dataMap.get("name") + "-处罚决定.doc";
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
		MessageBox.show(this, "导出成功");

	}
/**
 * 信贷处理决定	
 */
	private void impxddecision(){
		vo = list.getSelectedBillVO();
		if (vo == null) {
			MessageBox.show(this, "请选择一条数据");
			return;
		}
		Date dt = new Date();
		SimpleDateFormat matter1 = new SimpleDateFormat("yyyy年MM月dd日");
		final JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("."));
		fc.setDialogTitle("请选择要保存到的目录");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setAcceptAllFileFilterUsed(false);
		int result = fc.showSaveDialog(this);
		if (result != JFileChooser.APPROVE_OPTION) { // 如果不是确定的
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
			dataMap.put("time", time1[0].toString() + "年" + time1[1].toString()
					+ "月" + time1[2].toString() + "日至" + time2[0].toString()
					+ "年" + time2[1].toString() + "月" + time2[2].toString()
					+ "日");
			String[] deptid = UIUtil.getStringArrayFirstColByDS(null,
					"select deptid from V_CK_SCHEME where 1=1 and  schemeid='"
							+ vo.getStringValue("id") + "'");
			dataMap.put("count", deptid.length + "个网点");
			String[] problemct = UIUtil
			.getStringArrayFirstColByDS(
					null,
					"select b.checkpoints from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"+ vo.getStringValue("id")+"' group by b.checkpoints");
	int countpm = 0;
	StringBuilder sb = new StringBuilder();
	for (int i = 0; i < problemct.length; i++) {
		countpm = countpm + 1;
		String numpm = ZZLUIUtil.numTurnStringNum(countpm);
		sb.append("（" + numpm + "）\r\r\n");
		sb.append(problemct[i].toString() + "\r");
		HashVO [] problemdept = UIUtil
				.getHashVoArrayByDS(
						null,
						"select * from V_CK_SCHEME_IMPLEMENT where schemeid='"+ vo.getStringValue("id")+ "' and checkpoints='"
								+ problemct[i].toString() + "' and result='无效'");
		int imp=0;
		for (int j = 0; j < problemdept.length; j++) {
			HashVO  [] ipvo=UIUtil.getHashVoArrayByDS(null, "select * from CK_SCHEME_IMPL where  id='"+problemdept[j].getStringValue("implid")+"'  or refimplid='"+problemdept[j].getStringValue("implid")+"' order by refimplid");
			String money=UIUtil.getStringValueByDS(null, "select sum(c8)/10000 from CK_SCHEME_IMPL where id='"+problemdept[j].getStringValue("implid")+"' or refimplid='"+problemdept[j].getStringValue("implid")+"' order by refimplid");
			if(ipvo.length>1){
			imp=imp+1;
			sb.append(imp).append("、");
			sb.append("借款人"+ipvo[0].getStringValue("c1")+"联保小组于"+ZZLUIUtil.getTimeString(ipvo[0].getStringValue("c6"))+"在"+ipvo[0].getStringValue("c16"));
			sb.append("办理联保贷款"+money+"万元，用于"+ipvo[0].getStringValue("c18"));
			sb.append("（其中：");
			for(int p=0;p<ipvo.length;p++){
				sb.append(ipvo[p].getStringValue("c1")+"贷款"+Double.parseDouble(ipvo[p].getStringValue("c8"))/10000+"万元、贷款利率：");
				String strdb=ipvo[p].getStringValue("c11");
				strdb=strdb.substring(0,strdb.indexOf("."));
				int idb=Integer.parseInt(strdb);
				if(idb==0){
					Double db=Double.parseDouble(ipvo[p].getStringValue("c11"));
					db=db*1000;
					sb.append(db+"‰；");
				}else{
					sb.append(ipvo[p].getStringValue("c11")+"‰；");
				}
			}
			sb.append("），于"+ZZLUIUtil.getTimeString(ipvo[0].getStringValue("c7"))+"到期，");
			
			if(ipvo[0].getStringValue("usera1")!=null){
				sb.append("调查责任人A角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera1"))+"，");
			}
			if(ipvo[0].getStringValue("userb1")!=null){
				sb.append("调查责任人B角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb1"))+"，");
			}
			if(ipvo[0].getStringValue("usera2")!=null){
				sb.append("审查责任人A角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera2"))+"，");
			}
			if(ipvo[0].getStringValue("userb2")!=null){
				sb.append("审查责任人B角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb2"))+"，");
			}
			if(ipvo[0].getStringValue("usera3")!=null){
				sb.append("审批责任人A角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera3"))+"，");
			}
			if(ipvo[0].getStringValue("userb3")!=null){
				sb.append("审批责任人B角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb3"))+"，");
			}
			if(ipvo[0].getStringValue("usera4")!=null){
				sb.append("经营责任人A角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera4"))+"，");
			}
			if(ipvo[0].getStringValue("userb4")!=null){
				sb.append("经营责任人B角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb4"))+"，");
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
				sb.append("（" + count + "）" + bmname + ":");
				
				sb.append(problemvo[k].getStringValue("probleminfo")
						+ "\r");
				if(problemvo[k].getStringValue("adjustuserid")!=null){
				String usname = ZZLUIUtil.getnamesSplit(problemvo[k]
						.getStringValue("adjustuserid"));
				usname = usname.replace(",", "");
				sb.append("责任人：" + usname + "\r");
				}
			}
			}else{
				int grint=0;
					grint=grint+imp+1;
					sb.append(grint).append("、");
					sb.append("借款人"+ipvo[0].getStringValue("c1")+"于"+ZZLUIUtil.getTimeString(ipvo[0].getStringValue("c6"))+"在"+ipvo[0].getStringValue("c16"));
					sb.append("办理贷款"+money+"万元，用于"+ipvo[0].getStringValue("c18"));
					sb.append("于"+ZZLUIUtil.getTimeString(ipvo[0].getStringValue("c7"))+"到期，贷款利率");
					String strdb=ipvo[0].getStringValue("c11");
					strdb=strdb.substring(0,strdb.indexOf("."));
					int idb=Integer.parseInt(strdb);
					if(idb==0){
						Double db=Double.parseDouble(ipvo[0].getStringValue("c11"));
						db=db*1000;
						sb.append(db+"‰；");
					}else{
						sb.append(ipvo[0].getStringValue("c11")+"‰；");
					}
					imp=grint;
				
				
				if(ipvo[0].getStringValue("usera1")!=null){
					sb.append("调查责任人A角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera1"))+"，");
				}
				if(ipvo[0].getStringValue("userb1")!=null){
					sb.append("调查责任人B角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb1"))+"，");
				}
				if(ipvo[0].getStringValue("usera2")!=null){
					sb.append("审查责任人A角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera2"))+"，");
				}
				if(ipvo[0].getStringValue("userb2")!=null){
					sb.append("审查责任人B角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb2"))+"，");
				}
				if(ipvo[0].getStringValue("usera3")!=null){
					sb.append("审批责任人A角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera3"))+"，");
				}
				if(ipvo[0].getStringValue("userb3")!=null){
					sb.append("审批责任人B角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb3"))+"，");
				}
				if(ipvo[0].getStringValue("usera4")!=null){
					sb.append("经营责任人A角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera4"))+"，");
				}
				if(ipvo[0].getStringValue("userb4")!=null){
					sb.append("经营责任人B角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb4"))+"，");
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
					sb.append("（" + count + "）" + bmname + ":");
					
					sb.append(problemvo[k].getStringValue("probleminfo")
							+ "\r");
					if(problemvo[k].getStringValue("adjustuserid")!=null){
					String usname = ZZLUIUtil.getnamesSplit(problemvo[k]
							.getStringValue("adjustuserid"));
					usname = usname.replace(",", "");
					sb.append("责任人：" + usname + "\r");
					}
				}
				
			}
		}
		sb.append("上述事实违反了《新疆维吾尔自治区农村信用合作社XXX制度》，依据《新疆昌吉农村商业银行股份有限公司员工违反规章制度处理规定》第X章第x节第XX条相关规定，决定给予责任人XX处罚。");
			}
			dataMap.put("problem", sb);
			dataMap.put("time2", matter1.format(dt));
			savePath = savePath + "\\" + dataMap.get("name") + "-处罚决定.doc";
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
		MessageBox.show(this, "导出成功");

	}

	/**
	 * 处理建议
	 */
	private void impadvice() {
		vo = list.getSelectedBillVO();
		if (vo == null) {
			MessageBox.show(this, "请选择一条数据");
			return;
		}
		Date dt = new Date();
		SimpleDateFormat matter1 = new SimpleDateFormat("yyyy年MM月dd日");
		final JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("."));
		fc.setDialogTitle("请选择要保存到的目录");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setAcceptAllFileFilterUsed(false);
		int result = fc.showSaveDialog(this);
		if (result != JFileChooser.APPROVE_OPTION) { // 如果不是确定的
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
			dataMap.put("time", time1[0].toString() + "年" + time1[1].toString()
					+ "月" + time1[2].toString() + "日至" + time2[0].toString()
					+ "年" + time2[1].toString() + "月" + time2[2].toString()
					+ "日");
			String[] deptid = UIUtil.getStringArrayFirstColByDS(null,
					"select deptid from V_CK_SCHEME where 1=1 and  schemeid='"
							+ vo.getStringValue("id") + "'");
			dataMap.put("count", deptid.length + "个网点");
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
				sb.append("（" + numpm + "）\r\r\n");
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
						sb.append("（" + count + "）" + bmname + ":");
						sb.append(problemvo[k].getStringValue("probleminfo")
								+ "\r");
						String usname = ZZLUIUtil.getnamesSplit(problemvo[k]
								.getStringValue("adjustuserid"));
						if(usname!=null){
						usname = usname.replace(",", "");
						sb.append("责任人：" + usname + "\r");
						}
					}
				}
				sb
						.append("上述事实违反了《新疆维吾尔自治区农村信用合作社XXX制度》，依据《新疆昌吉农村商业银行股份有限公司员工违反规章制度处理规定》第X章第x节第XX条相关规定，建议给予责任人XX处罚。");
			}
			dataMap.put("problem", sb);
			dataMap.put("time2", matter1.format(dt));
			savePath = savePath + "\\" + dataMap.get("name") + "-处罚建议.doc";
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
		MessageBox.show(this, "导出成功");
	}
/**
 * 信贷处理建议	
 */
	private void impxdadvice(){
		vo = list.getSelectedBillVO();
		if (vo == null) {
			MessageBox.show(this, "请选择一条数据");
			return;
		}
		Date dt = new Date();
		SimpleDateFormat matter1 = new SimpleDateFormat("yyyy年MM月dd日");
		final JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("."));
		fc.setDialogTitle("请选择要保存到的目录");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setAcceptAllFileFilterUsed(false);
		int result = fc.showSaveDialog(this);
		if (result != JFileChooser.APPROVE_OPTION) { // 如果不是确定的
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
			dataMap.put("time", time1[0].toString() + "年" + time1[1].toString()
					+ "月" + time1[2].toString() + "日至" + time2[0].toString()
					+ "年" + time2[1].toString() + "月" + time2[2].toString()
					+ "日");
			String[] deptid = UIUtil.getStringArrayFirstColByDS(null,
					"select deptid from V_CK_SCHEME where 1=1 and  schemeid='"
							+ vo.getStringValue("id") + "'");
			dataMap.put("count", deptid.length + "个网点");
			String[] problemct = UIUtil
			.getStringArrayFirstColByDS(
					null,
					"select b.checkpoints from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"+ vo.getStringValue("id")+"' group by b.checkpoints");
	int countpm = 0;
	StringBuilder sb = new StringBuilder();
	for (int i = 0; i < problemct.length; i++) {
		countpm = countpm + 1;
		String numpm = ZZLUIUtil.numTurnStringNum(countpm);
		sb.append("（" + numpm + "）\r\r\n");
		sb.append(problemct[i].toString() + "\r");
		HashVO [] problemdept = UIUtil
				.getHashVoArrayByDS(
						null,
						"select * from V_CK_SCHEME_IMPLEMENT where schemeid='"+ vo.getStringValue("id")+ "' and checkpoints='"
								+ problemct[i].toString() + "' and result='无效'");
		int imp=0;
		for (int j = 0; j < problemdept.length; j++) {
			HashVO  [] ipvo=UIUtil.getHashVoArrayByDS(null, "select * from CK_SCHEME_IMPL where  id='"+problemdept[j].getStringValue("implid")+"'  or refimplid='"+problemdept[j].getStringValue("implid")+"' order by refimplid");
			String money=UIUtil.getStringValueByDS(null, "select sum(c8)/10000 from CK_SCHEME_IMPL where id='"+problemdept[j].getStringValue("implid")+"' or refimplid='"+problemdept[j].getStringValue("implid")+"' order by refimplid");
			if(ipvo.length>1){
			imp=imp+1;
			sb.append(imp).append("、");
			sb.append("借款人"+ipvo[0].getStringValue("c1")+"联保小组于"+ZZLUIUtil.getTimeString(ipvo[0].getStringValue("c6"))+"在"+ipvo[0].getStringValue("c16"));
			sb.append("办理联保贷款"+money+"万元，用于"+ipvo[0].getStringValue("c18"));
			sb.append("（其中：");
			for(int p=0;p<ipvo.length;p++){
				sb.append(ipvo[p].getStringValue("c1")+"贷款"+Double.parseDouble(ipvo[p].getStringValue("c8"))/10000+"万元、贷款利率：");
				String strdb=ipvo[p].getStringValue("c11");
				strdb=strdb.substring(0,strdb.indexOf("."));
				int idb=Integer.parseInt(strdb);
				if(idb==0){
					Double db=Double.parseDouble(ipvo[p].getStringValue("c11"));
					db=db*1000;
					sb.append(db+"‰；");
				}else{
					sb.append(ipvo[p].getStringValue("c11")+"‰；");
				}
			}
			sb.append("），于"+ZZLUIUtil.getTimeString(ipvo[0].getStringValue("c7"))+"到期，");
			
			if(ipvo[0].getStringValue("usera1")!=null){
				sb.append("调查责任人A角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera1"))+"，");
			}
			if(ipvo[0].getStringValue("userb1")!=null){
				sb.append("调查责任人B角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb1"))+"，");
			}
			if(ipvo[0].getStringValue("usera2")!=null){
				sb.append("审查责任人A角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera2"))+"，");
			}
			if(ipvo[0].getStringValue("userb2")!=null){
				sb.append("审查责任人B角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb2"))+"，");
			}
			if(ipvo[0].getStringValue("usera3")!=null){
				sb.append("审批责任人A角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera3"))+"，");
			}
			if(ipvo[0].getStringValue("userb3")!=null){
				sb.append("审批责任人B角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb3"))+"，");
			}
			if(ipvo[0].getStringValue("usera4")!=null){
				sb.append("经营责任人A角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera4"))+"，");
			}
			if(ipvo[0].getStringValue("userb4")!=null){
				sb.append("经营责任人B角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb4"))+"，");
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
				sb.append("（" + count + "）" + bmname + ":");
				
				sb.append(problemvo[k].getStringValue("probleminfo")
						+ "\r");
				if(problemvo[k].getStringValue("adjustuserid")!=null){
				String usname = ZZLUIUtil.getnamesSplit(problemvo[k]
						.getStringValue("adjustuserid"));
				usname = usname.replace(",", "");
				sb.append("责任人：" + usname + "\r");
				}
			}
			}else{
				int grint=0;
					grint=grint+imp+1;
					sb.append(grint).append("、");
					sb.append("借款人"+ipvo[0].getStringValue("c1")+"于"+ZZLUIUtil.getTimeString(ipvo[0].getStringValue("c6"))+"在"+ipvo[0].getStringValue("c16"));
					sb.append("办理贷款"+money+"万元，用于"+ipvo[0].getStringValue("c18"));
					sb.append("于"+ZZLUIUtil.getTimeString(ipvo[0].getStringValue("c7"))+"到期，贷款利率");
					String strdb=ipvo[0].getStringValue("c11");
					strdb=strdb.substring(0,strdb.indexOf("."));
					int idb=Integer.parseInt(strdb);
					if(idb==0){
						Double db=Double.parseDouble(ipvo[0].getStringValue("c11"));
						db=db*1000;
						sb.append(db+"‰；");
					}else{
						sb.append(ipvo[0].getStringValue("c11")+"‰；");
					}
					imp=grint;
				
				
				if(ipvo[0].getStringValue("usera1")!=null){
					sb.append("调查责任人A角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera1"))+"，");
				}
				if(ipvo[0].getStringValue("userb1")!=null){
					sb.append("调查责任人B角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb1"))+"，");
				}
				if(ipvo[0].getStringValue("usera2")!=null){
					sb.append("审查责任人A角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera2"))+"，");
				}
				if(ipvo[0].getStringValue("userb2")!=null){
					sb.append("审查责任人B角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb2"))+"，");
				}
				if(ipvo[0].getStringValue("usera3")!=null){
					sb.append("审批责任人A角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera3"))+"，");
				}
				if(ipvo[0].getStringValue("userb3")!=null){
					sb.append("审批责任人B角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb3"))+"，");
				}
				if(ipvo[0].getStringValue("usera4")!=null){
					sb.append("经营责任人A角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera4"))+"，");
				}
				if(ipvo[0].getStringValue("userb4")!=null){
					sb.append("经营责任人B角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb4"))+"，");
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
					sb.append("（" + count + "）" + bmname + ":");
					
					sb.append(problemvo[k].getStringValue("probleminfo")
							+ "\r");
					if(problemvo[k].getStringValue("adjustuserid")!=null){
					String usname = ZZLUIUtil.getnamesSplit(problemvo[k]
							.getStringValue("adjustuserid"));
					usname = usname.replace(",", "");
					sb.append("责任人：" + usname + "\r");
					}
				}
				
			}
		}
		sb.append("上述事实违反了《新疆维吾尔自治区农村信用合作社XXX制度》，依据《新疆昌吉农村商业银行股份有限公司员工违反规章制度处理规定》第X章第x节第XX条相关规定，建议给予责任人XX处罚。");
			}
			dataMap.put("problem", sb);
			dataMap.put("time2", matter1.format(dt));
			savePath = savePath + "\\" + dataMap.get("name") + "-处罚建议.doc";
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
		MessageBox.show(this, "导出成功");
	}

	/**
	 * 报告
	 */
	private void impreport() {
		vo = list.getSelectedBillVO();
		if (vo == null) {
			MessageBox.show(this, "请选择一条数据");
			return;
		}
		BillCardDialog listDialog = new BillCardDialog(this,
				"为导出后续整改落实情况请选择上次检查的项目名称", "CK_PROBLEM_INFO_ZZL_EQ1_2", 500,
				200);
		listDialog.setVisible(true);
		BillVO vos = listDialog.getBillcardPanel().getBillVO();
		String scschemeid = vos.getStringValue("schemeid");
		Date dt = new Date();
		SimpleDateFormat matter1 = new SimpleDateFormat("yyyy年MM月dd日");
		final JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("."));
		fc.setDialogTitle("请选择要保存到的目录");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setAcceptAllFileFilterUsed(false);
		int result = fc.showSaveDialog(this);
		if (result != JFileChooser.APPROVE_OPTION) { // 如果不是确定的
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
				leader=leader.replace("。", "");
				String a = String.valueOf(numa);
				dataMap.put("name" + a, leader);
				dataMap.put("position" + a, "第" + count + "小组 组长");
				String[] teamusers = ZZLUIUtil.getTeamNames(fzvo[i]
						.getStringValue("teamusers"));
				for (int j = 0; j < teamusers.length; j++) {
					users = numa + j + 1;
					String b = String.valueOf(users);
					dataMap.put("name" + b, teamusers[j].toString());
					dataMap.put("position" + b, "第" + count + "小组 组员");
				}
				numa = users;
				String depts = ZZLUIUtil.getdeptsSplit(fzvo[i]
						.getStringValue("checkeddept"));
				dataMap.put("depts" + a, "负责检查：" + depts);
			}
			HashVO[] planvo = UIUtil.getHashVoArrayByDS(null,
					"select * from CK_PLAN where id='"
							+ vo.getStringValue("planid") + "'");
			String[] time1 = planvo[0].getStringValue("PLANBEGINDATE")
					.toString().split("-");
			String[] time2 = planvo[0].getStringValue("PLANENDDATE").toString()
					.split("-");
			dataMap.put("time2", time1[0].toString() + "年"
					+ time1[1].toString() + "月" + time1[2].toString() + "日至"
					+ time2[0].toString() + "年" + time2[1].toString() + "月"
					+ time2[2].toString() + "日");
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
				sb.append("（" + numpm + "）\r\n");
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
						sb.append("（" + count + "）" + bmname + ":");
						sb.append(problemvo[k].getStringValue("probleminfo")
								+ "\r");
						String usname = ZZLUIUtil.getnamesSplit(problemvo[k]
								.getStringValue("adjustuserid"));
						if(usname!=null){
						usname = usname.replace(",", "");
						sb.append("责任人：" + usname + "\r");
						}
					}

				}
				if (scschemeid != null) {
					if (i == problemct.length - 1) {
						int countzg = problemct.length + 1;
						String numzg = ZZLUIUtil.numTurnStringNum(countzg);
						sb.append("（" + numzg + "） 检查整改落实。");
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
												+ ") and a.trackresult='已整改'");
						HashVO[] bzgvo = UIUtil
								.getHashVoArrayByDS(
										null,
										"select * from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
												+ scschemeid
												+ "' and a.deptid in("
												+ deptids
												+ ") and a.trackresult='部分整改'");
						HashVO[] vzgvo = UIUtil
								.getHashVoArrayByDS(
										null,
										"select * from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
												+ scschemeid
												+ "' and a.deptid in("
												+ deptids
												+ ") and a.trackresult='未整改'");
						String planname = UIUtil.getStringValueByDS(null,
								"select NAME from CK_SCHEME where id='"
										+ scschemeid + "'");
						sb.append("本次检查的" + zgdeptid.length + "个支行在" + planname
								+ "检查发现问题" + zgvo.length + "项，已整改"
								+ yzgvo.length + "项，部分整改" + bzgvo.length
								+ "项，未整改" + vzgvo.length + "项.\r");
						sb.append("部分整改事项为：");
						String[] bzgct = UIUtil
								.getStringArrayFirstColByDS(
										null,
										"select distinct(b.checkpoints) from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
												+ scschemeid
												+ "' and a.deptid in("
												+ deptids
												+ ") and a.trackresult='部分整改'");
						int bzgctpm = 0;
						for (int b = 0; b < bzgct.length; b++) {
							bzgctpm = bzgctpm + 1;
							String bzgpm = ZZLUIUtil.numTurnStringNum(bzgctpm);
							sb.append("（" + bzgpm + "）\r\r\n");
							sb.append(bzgct[b].toString() + "\r");
							int bcount = 0;
							String[] bproblemdept = UIUtil
									.getStringArrayFirstColByDS(
											null,
											"select distinct(a.deptid) from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
													+ scschemeid
													+ "' and a.deptid in("
													+ deptids
													+ ") and a.trackresult='部分整改' and b.checkpoints='"
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
														+ "' and a.trackresult='部分整改'");
								for (int k = 0; k < bproblemvo.length; k++) {
									bcount = bcount + 1;
									String bmname = ZZLUIUtil
											.getdeptsSplit(bproblemvo[k]
													.getStringValue("deptid"));
									sb
											.append("（" + bcount + "）" + bmname
													+ ":");
									sb.append(bproblemvo[k]
											.getStringValue("probleminfo")
											+ "\r");
									String usname = ZZLUIUtil
											.getnamesSplit(bproblemvo[k]
													.getStringValue("adjustuserid"));
									usname = usname.replace(",", "");
									sb.append("责任人：" + usname + "\r"
											+ "部分整改。\r");
								}

							}

						}
						sb.append("未整改事项为：");
						String[] vzgct = UIUtil
								.getStringArrayFirstColByDS(
										null,
										"select distinct(b.checkpoints) from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
												+ scschemeid
												+ "' and a.deptid in("
												+ deptids
												+ ") and a.trackresult='未整改'");
						int vzgctpm = 0;
						for (int b = 0; b < vzgct.length; b++) {
							vzgctpm = vzgctpm + 1;
							String vzgpm = ZZLUIUtil.numTurnStringNum(vzgctpm);
							sb.append("（" + vzgpm + "）\r\r\n");
							sb.append(vzgct[b].toString() + "\r");
							int vcount = 0;
							String[] vproblemdept = UIUtil
									.getStringArrayFirstColByDS(
											null,
											"select distinct(a.deptid) from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
													+ scschemeid
													+ "' and a.deptid in("
													+ deptids
													+ ") and a.trackresult='未整改' and b.checkpoints='"
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
														+ "' and a.trackresult='未整改'");
								for (int k = 0; k < vproblemvo.length; k++) {
									vcount = vcount + 1;
									String bmname = ZZLUIUtil
											.getdeptsSplit(vproblemvo[k]
													.getStringValue("deptid"));
									sb
											.append("（" + vcount + "）" + bmname
													+ ":");
									sb.append(vproblemvo[k]
											.getStringValue("probleminfo")
											+ "\r");
									String usname = ZZLUIUtil
											.getnamesSplit(vproblemvo[k]
													.getStringValue("adjustuserid"));
									usname = usname.replace(",", "");
									sb.append("责任人：" + usname + "\r"
											+ "未整改。\r");
								}

							}

						}
					}
				}
			}
			dataMap.put("problem", sb);
			savePath = savePath + "\\" + dataMap.get("name") + "-检查报告.doc";
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
			MessageBox.show(this, "导出成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 信贷报告
	 */
private void impxdreport(){
	vo = list.getSelectedBillVO();
	if (vo == null) {
		MessageBox.show(this, "请选择一条数据");
		return;
	}
	BillCardDialog listDialog = new BillCardDialog(this,
			"为导出后续整改落实情况请选择上次检查的项目名称", "CK_PROBLEM_INFO_ZZL_EQ1_2", 500,
			200);
	listDialog.setVisible(true);
	BillVO vos = listDialog.getBillcardPanel().getBillVO();
	String scschemeid = vos.getStringValue("schemeid");
	Date dt = new Date();
	SimpleDateFormat matter1 = new SimpleDateFormat("yyyy年MM月dd日");
	final JFileChooser fc = new JFileChooser();
	fc.setCurrentDirectory(new File("."));
	fc.setDialogTitle("请选择要保存到的目录");
	fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	fc.setAcceptAllFileFilterUsed(false);
	int result = fc.showSaveDialog(this);
	if (result != JFileChooser.APPROVE_OPTION) { // 如果不是确定的
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
			leader=leader.replace("。", "");
			String a = String.valueOf(numa);
			dataMap.put("name" + a, leader);
			dataMap.put("position" + a, "第" + count + "小组 组长");
			String[] teamusers = ZZLUIUtil.getTeamNames(fzvo[i]
					.getStringValue("teamusers"));
			for (int j = 0; j < teamusers.length; j++) {
				users = numa + j + 1;
				String b = String.valueOf(users);
				dataMap.put("name" + b, teamusers[j].toString());
				dataMap.put("position" + b, "第" + count + "小组 组员");
			}
			numa = users;
			String depts = ZZLUIUtil.getdeptsSplit(fzvo[i]
					.getStringValue("checkeddept"));
			dataMap.put("depts" + a, "负责检查：" + depts);
		}
		HashVO[] planvo = UIUtil.getHashVoArrayByDS(null,
				"select * from CK_PLAN where id='"
						+ vo.getStringValue("planid") + "'");
		String[] time1 = planvo[0].getStringValue("PLANBEGINDATE")
				.toString().split("-");
		String[] time2 = planvo[0].getStringValue("PLANENDDATE").toString()
				.split("-");
		dataMap.put("time2", time1[0].toString() + "年"
				+ time1[1].toString() + "月" + time1[2].toString() + "日至"
				+ time2[0].toString() + "年" + time2[1].toString() + "月"
				+ time2[2].toString() + "日");
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
			sb.append("（" + numpm + "）\r\r\n");
			sb.append(problemct[i].toString() + "\r");
			HashVO [] problemdept = UIUtil
					.getHashVoArrayByDS(
							null,
							"select * from V_CK_SCHEME_IMPLEMENT where schemeid='"+ vo.getStringValue("id")+ "' and checkpoints='"
									+ problemct[i].toString() + "' and result='无效'");
			int imp=0;
			for (int j = 0; j < problemdept.length; j++) {
				HashVO  [] ipvo=UIUtil.getHashVoArrayByDS(null, "select * from CK_SCHEME_IMPL where  id='"+problemdept[j].getStringValue("implid")+"'  or refimplid='"+problemdept[j].getStringValue("implid")+"' order by refimplid");
				String money=UIUtil.getStringValueByDS(null, "select sum(c8)/10000 from CK_SCHEME_IMPL where id='"+problemdept[j].getStringValue("implid")+"' or refimplid='"+problemdept[j].getStringValue("implid")+"' order by refimplid");
				if(ipvo.length>1){
				imp=imp+1;
				sb.append(imp).append("、");
				sb.append("借款人"+ipvo[0].getStringValue("c1")+"联保小组于"+ZZLUIUtil.getTimeString(ipvo[0].getStringValue("c6"))+"在"+ipvo[0].getStringValue("c16"));
				sb.append("办理联保贷款"+money+"万元，用于"+ipvo[0].getStringValue("c18"));
				sb.append("（其中：");
				for(int p=0;p<ipvo.length;p++){
					sb.append(ipvo[p].getStringValue("c1")+"贷款"+Double.parseDouble(ipvo[p].getStringValue("c8"))/10000+"万元、贷款利率：");
					String strdb=ipvo[p].getStringValue("c11");
					strdb=strdb.substring(0,strdb.indexOf("."));
					int idb=Integer.parseInt(strdb);
					if(idb==0){
						Double db=Double.parseDouble(ipvo[p].getStringValue("c11"));
						db=db*1000;
						sb.append(db+"‰；");
					}else{
						sb.append(ipvo[p].getStringValue("c11")+"‰；");
					}
				}
				sb.append("），于"+ZZLUIUtil.getTimeString(ipvo[0].getStringValue("c7"))+"到期，");
				
				if(ipvo[0].getStringValue("usera1")!=null){
					sb.append("调查责任人A角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera1"))+"，");
				}
				if(ipvo[0].getStringValue("userb1")!=null){
					sb.append("调查责任人B角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb1"))+"，");
				}
				if(ipvo[0].getStringValue("usera2")!=null){
					sb.append("审查责任人A角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera2"))+"，");
				}
				if(ipvo[0].getStringValue("userb2")!=null){
					sb.append("审查责任人B角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb2"))+"，");
				}
				if(ipvo[0].getStringValue("usera3")!=null){
					sb.append("审批责任人A角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera3"))+"，");
				}
				if(ipvo[0].getStringValue("userb3")!=null){
					sb.append("审批责任人B角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb3"))+"，");
				}
				if(ipvo[0].getStringValue("usera4")!=null){
					sb.append("经营责任人A角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera4"))+"，");
				}
				if(ipvo[0].getStringValue("userb4")!=null){
					sb.append("经营责任人B角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb4"))+"，");
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
					sb.append("（" + count + "）" + bmname + ":");
					
					sb.append(problemvo[k].getStringValue("probleminfo")
							+ "\r");
					if(problemvo[k].getStringValue("adjustuserid")!=null){
					String usname = ZZLUIUtil.getnamesSplit(problemvo[k]
							.getStringValue("adjustuserid"));
					usname = usname.replace(",", "");
					sb.append("责任人：" + usname + "\r");
					}
				}
				}else{
					int grint=0;
						grint=grint+imp+1;
						sb.append(grint).append("、");
						if(ipvo[0].getStringValue("c6")!=null){
						sb.append("借款人"+ipvo[0].getStringValue("c1")+"于"+ZZLUIUtil.getTimeString(ipvo[0].getStringValue("c6"))+"在"+ipvo[0].getStringValue("c16"));
						}
						sb.append("办理贷款"+money+"万元，用于"+ipvo[0].getStringValue("c18"));
						if(ipvo[0].getStringValue("c7")!=null){
						sb.append("于"+ZZLUIUtil.getTimeString(ipvo[0].getStringValue("c7"))+"到期，贷款利率");
						}
						String strdb=null;
						if(ipvo[0].getStringValue("c11")!=null){
						strdb=ipvo[0].getStringValue("c11");
						strdb=strdb.substring(0,strdb.indexOf("."));
						
						int idb=Integer.parseInt(strdb);
						if(idb==0){
							Double db=Double.parseDouble(ipvo[0].getStringValue("c11"));
							db=db*1000;
							sb.append(db+"‰；");
						}else{
							sb.append(ipvo[0].getStringValue("c11")+"‰；");
						}
						imp=grint;
						}
					
					if(ipvo[0].getStringValue("usera1")!=null){
						sb.append("调查责任人A角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera1"))+"，");
					}
					if(ipvo[0].getStringValue("userb1")!=null){
						sb.append("调查责任人B角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb1"))+"，");
					}
					if(ipvo[0].getStringValue("usera2")!=null){
						sb.append("审查责任人A角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera2"))+"，");
					}
					if(ipvo[0].getStringValue("userb2")!=null){
						sb.append("审查责任人B角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb2"))+"，");
					}
					if(ipvo[0].getStringValue("usera3")!=null){
						sb.append("审批责任人A角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera3"))+"，");
					}
					if(ipvo[0].getStringValue("userb3")!=null){
						sb.append("审批责任人B角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb3"))+"，");
					}
					if(ipvo[0].getStringValue("usera4")!=null){
						sb.append("经营责任人A角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("usera4"))+"，");
					}
					if(ipvo[0].getStringValue("userb4")!=null){
						sb.append("经营责任人B角："+ZZLUIUtil.getnamesSplit(ipvo[0].getStringValue("userb4"))+"，");
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
						sb.append("（" + count + "）" + bmname + ":");
						
						sb.append(problemvo[k].getStringValue("probleminfo")
								+ "\r");
						if(problemvo[k].getStringValue("adjustuserid")!=null){
						String usname = ZZLUIUtil.getnamesSplit(problemvo[k]
								.getStringValue("adjustuserid"));
						usname = usname.replace(",", "");
						sb.append("责任人：" + usname + "\r");
						}
					}
					
				}
			}
			if (scschemeid != null) {
				if (i == problemct.length - 1) {
					int countzg = problemct.length + 1;
					String numzg = ZZLUIUtil.numTurnStringNum(countzg);
					sb.append("（" + numzg + "） 检查整改落实。");
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
											+ ") and a.trackresult='已整改'");
					HashVO[] bzgvo = UIUtil
							.getHashVoArrayByDS(
									null,
									"select * from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
											+ scschemeid
											+ "' and a.deptid in("
											+ deptids
											+ ") and a.trackresult='部分整改'");
					HashVO[] vzgvo = UIUtil
							.getHashVoArrayByDS(
									null,
									"select * from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
											+ scschemeid
											+ "' and a.deptid in("
											+ deptids
											+ ") and a.trackresult='未整改'");
					String planname = UIUtil.getStringValueByDS(null,
							"select NAME from CK_SCHEME where id='"
									+ scschemeid + "'");
					sb.append("本次检查的" + zgdeptid.length + "个支行在" + planname
							+ "检查发现问题" + zgvo.length + "项，已整改"
							+ yzgvo.length + "项，部分整改" + bzgvo.length
							+ "项，未整改" + vzgvo.length + "项.\r");
					sb.append("部分整改事项为：");
					String[] bzgct = UIUtil
							.getStringArrayFirstColByDS(
									null,
									"select distinct(b.checkpoints) from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
											+ scschemeid
											+ "' and a.deptid in("
											+ deptids
											+ ") and a.trackresult='部分整改'");
					int bzgctpm = 0;
					for (int b = 0; b < bzgct.length; b++) {
						bzgctpm = bzgctpm + 1;
						String bzgpm = ZZLUIUtil.numTurnStringNum(bzgctpm);
						sb.append("（" + bzgpm + "）\r\r\n");
						sb.append(bzgct[b].toString() + "\r");
						int bcount = 0;
						String[] bproblemdept = UIUtil
								.getStringArrayFirstColByDS(
										null,
										"select distinct(a.deptid) from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
												+ scschemeid
												+ "' and a.deptid in("
												+ deptids
												+ ") and a.trackresult='部分整改' and b.checkpoints='"
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
													+ "' and a.trackresult='部分整改'");
							for (int k = 0; k < bproblemvo.length; k++) {
								bcount = bcount + 1;
								String bmname = ZZLUIUtil
										.getdeptsSplit(bproblemvo[k]
												.getStringValue("deptid"));
								sb
										.append("（" + bcount + "）" + bmname
												+ ":");
								sb.append(bproblemvo[k]
										.getStringValue("probleminfo")
										+ "\r");
								String usname = ZZLUIUtil
										.getnamesSplit(bproblemvo[k]
												.getStringValue("adjustuserid"));
								usname = usname.replace(",", "");
								sb.append("责任人：" + usname + "\r"
										+ "部分整改。\r");
							}

						}

					}
					sb.append("未整改事项为：");
					String[] vzgct = UIUtil
							.getStringArrayFirstColByDS(
									null,
									"select distinct(b.checkpoints) from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
											+ scschemeid
											+ "' and a.deptid in("
											+ deptids
											+ ") and a.trackresult='未整改'");
					int vzgctpm = 0;
					for (int b = 0; b < vzgct.length; b++) {
						vzgctpm = vzgctpm + 1;
						String vzgpm = ZZLUIUtil.numTurnStringNum(vzgctpm);
						sb.append("（" + vzgpm + "）\r\r\n");
						sb.append(vzgct[b].toString() + "\r");
						int vcount = 0;
						String[] vproblemdept = UIUtil
								.getStringArrayFirstColByDS(
										null,
										"select distinct(a.deptid) from ck_problem_info a left join V_CK_SCHEME_IMPLEMENT b on a.parentid=b.id where a.schemeid='"
												+ scschemeid
												+ "' and a.deptid in("
												+ deptids
												+ ") and a.trackresult='未整改' and b.checkpoints='"
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
													+ "' and a.trackresult='未整改'");
							for (int k = 0; k < vproblemvo.length; k++) {
								vcount = vcount + 1;
								String bmname = ZZLUIUtil
										.getdeptsSplit(vproblemvo[k]
												.getStringValue("deptid"));
								sb
										.append("（" + vcount + "）" + bmname
												+ ":");
								sb.append(vproblemvo[k]
										.getStringValue("probleminfo")
										+ "\r");
								String usname = ZZLUIUtil
										.getnamesSplit(vproblemvo[k]
												.getStringValue("adjustuserid"));
								usname = usname.replace(",", "");
								sb.append("责任人：" + usname + "\r"
										+ "未整改。\r");
							}

						}

					}
				}
			}
		}
		dataMap.put("problem", sb);
		savePath = savePath + "\\" + dataMap.get("name") + "-检查报告.doc";
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
		MessageBox.show(this, "导出成功");
	} catch (Exception e) {
		e.printStackTrace();
	}
}
    public String title(String id){
    	String count=null;
    	try{
    		count=UIUtil.getStringValueByDS(null, "select PLANTYPE from CK_PLAN where id='"+id+"'");
    		if(count.equals("审计检查")){
    			count="审计检查报告书";
    		}else if(count.equals("自律检查")){
    			count="自律监管检查报告书";
    		}else{
    			count="检查报告书";
    		}
    		
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    			return count;
    		

    	
    }


}
