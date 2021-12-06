package com.pushworld.icheck.ui.p030;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedEvent;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.report.cellcompent.ExcelUtil;

import com.pushworld.ipushgrc.ui.icheck.word.WordExport;

/**
 * ��鷽��ά�� (CK_SCHEME)�����/2016-08-10��
 * ����״̬��δִ�У�ִ���У��ѽ���
 * 
 */
public class CheckSchemeWKPanel extends AbstractWorkPanel implements ActionListener, BillListSelectListener, BillListMouseDoubleClickedListener {

	private static final long serialVersionUID = -1514385022019530158L;
	private BillListPanel billList_scheme = null;
	private WLTButton btn_insertFromPlan, btn_insert, btn_edit, btn_delete, btn_export, btn_start, btn_end;
	private String status1 = "δִ��", status2 = "ִ����", status3 = "�ѽ���";//��鷽��״̬
	private BillVO vo = null;
	private static String id=null;
	private WLTButton btn_new=null;
	@Override
	public void initialize() {
		billList_scheme = new BillListPanel("CK_SCHEME_LCJ_E01");

		String type = this.getMenuConfMapValueAsStr("�����", "Y");
		if ("N".equalsIgnoreCase(type)) {//������ֻ��ʾ����������ť�����/2016-09-12��
			btn_end = new WLTButton("����");
			btn_end.addActionListener(this);
			billList_scheme.addBatchBillListButton(new WLTButton[] { btn_end });
		} else {
			btn_insertFromPlan = new WLTButton("�Ӽƻ�����");
//			btn_insert = new WLTButton("ֱ�Ӵ���");
			btn_edit = new WLTButton("�޸�");
			btn_delete = new WLTButton("ɾ��");
			btn_export = new WLTButton("��������");
			btn_start = new WLTButton("��ʼ");
			btn_end = new WLTButton("����");
			btn_new=new WLTButton("���������Ա");

			btn_insertFromPlan.addActionListener(this);
//			btn_insert.addActionListener(this);
			btn_edit.addActionListener(this);
			btn_delete.addActionListener(this);
			btn_export.addActionListener(this);
			btn_start.addActionListener(this);
			btn_end.addActionListener(this);
			btn_new.addActionListener(this);
			billList_scheme.addBatchBillListButton(new WLTButton[] { btn_insertFromPlan, btn_edit, btn_delete, btn_export, btn_start, btn_end ,btn_new});
		}

		billList_scheme.repaintBillListButton();
		billList_scheme.addBillListSelectListener(this);
		billList_scheme.addBillListMouseDoubleClickedListener(this);
		this.add(billList_scheme);
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_insertFromPlan) {
			onInsertFromPlan();
		} else if (obj == btn_insert) {
			onInsert();
		} else if (obj == btn_edit) {
			onEdit();
		} else if (obj == btn_delete) {
			onDelete();
		} else if (obj == btn_export) {
			new SplashWindow(this, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					onExport();
				}
			});
		} else if (obj == btn_start) {
			onStart();
		} else if (obj == btn_end) {
			onEnd();
		}else if(obj == btn_new){
			onNew();
		}
	}
/**
 * zzl[]���������Ա
 */
	private void onNew() {
		BillVO vo=billList_scheme.getSelectedBillVO();
		BillCardDialog log=new BillCardDialog(this,"���������Ա","CK_SCHEME_ZZL_E01_1",800,400);
		log.getBillcardPanel().queryDataByCondition("id="+vo.getStringValue("id"));
		log.setVisible(true);
	}

	/**
	 * �Ӽƻ���������
	 */
	private void onInsertFromPlan() {
		BillListDialog billlistDialog = new BillListDialog(this, "ѡ��һ���ƻ��������һ��", "CK_PLAN_LCJ_E01");
		billlistDialog.getBtn_confirm().setText("��һ��");
		billlistDialog.getBilllistPanel().setDataFilterCustCondition("status='" + status2 + "'");
		billlistDialog.getBilllistPanel().QueryDataByCondition(null);
		billlistDialog.getBilllistPanel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		billlistDialog.getBilllistPanel().addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));
		billlistDialog.getBilllistPanel().repaintBillListButton();
		billlistDialog.setVisible(true);
		if (billlistDialog.getCloseType() != 1) {
			return;
		}
		BillVO planVo = billlistDialog.getReturnBillVOs()[0];
		BillCardPanel schemePanel = new BillCardPanel(billList_scheme.getTempletVO());//����������Ϣ
		schemePanel.insertRow(); //��Ƭ����һ��!
		schemePanel.setEditableByInsertInit();
		schemePanel.setValueAt("PLANID", new RefItemVO(planVo.getStringValue("id"), "", planVo.getStringValue("planname")));
		schemePanel.setValueAt("CODE", new StringItemVO(planVo.getStringValue("CODE") + "-"));//��������
		schemePanel.setValueAt("PLANNAME", new StringItemVO(planVo.getStringValue("PLANNAME")));//�����Ŀ����
		schemePanel.setValueAt("NAME", new StringItemVO(planVo.getStringValue("PLANNAME")));//��������
		schemePanel.setValueAt("GOAL", new StringItemVO(planVo.getStringValue("GOAL")));
		schemePanel.setValueAt("CKSCOPE", new StringItemVO(planVo.getStringValue("CKSCOPE")));

		schemePanel.setRealValueAt("CHECKDEPT", planVo.getStringValue("CHECKDEPT"));//������벿��
		schemePanel.setRealValueAt("PLANTYPE", planVo.getStringValue("PLANTYPE"));//�ƻ�����
		schemePanel.setValueAt("PLANBEGINDATE", planVo.getStringValue("PLANBEGINDATE"));//�ƻ���ʼ����
		schemePanel.setValueAt("PLANENDDATE", planVo.getStringValue("PLANENDDATE"));//�ƻ���������

		String currPlanid = planVo.getStringValue("id");
		String currSchemeid = schemePanel.getRealValueAt("id");

		CheckSchemeEditDialog editDialog = new CheckSchemeEditDialog(billList_scheme, schemePanel, 0, currPlanid, currSchemeid);
		editDialog.setVisible(true);
	}

	/**
	 * ֱ�Ӵ�������
	 */
	private void onInsert() {
		BillCardPanel schemePanel = new BillCardPanel(billList_scheme.getTempletVO());//����������Ϣ
		schemePanel.insertRow(); //��Ƭ����һ��!
		schemePanel.setEditableByInsertInit();
		schemePanel.setVisiable("planid", false);
		String currPlanid = "";//���õ�ǰ�ƻ�
		String currSchemeid = schemePanel.getRealValueAt("id");//���õ�ǰ����

		CheckSchemeEditDialog editDialog = new CheckSchemeEditDialog(billList_scheme, schemePanel, 0, currPlanid, currSchemeid);
		editDialog.setVisible(true);
	}

	/**
	 * �޸ķ����������ϵͳ����Ա��ݣ��������޸ģ�����ֻ���޸�δȷ�ϵķ���
	 */
	private void onEdit() {
		BillVO selectedRow = billList_scheme.getSelectedBillVO();
		if (selectedRow == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		String status = selectedRow.getStringValue("status");
		if (status1.equals(status)) {//�����δִ�еĲſ��޸ģ���ִ���л��ѽ����ķ����Ѿ�������ʵʩ��¼�����޸�Ҳûʲô���壬����ԱҲ��Ҫ�޸���
			BillCardPanel schemePanel = new BillCardPanel(billList_scheme.getTempletVO());//����������Ϣ
			schemePanel.setBillVO(selectedRow);
			schemePanel.setEditableByEditInit();
			schemePanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
			String currPlanid = selectedRow.getStringValue("planid");//���õ�ǰ�ƻ�
			String currSchemeid = selectedRow.getStringValue("id");//���õ�ǰ����

			CheckSchemeEditDialog editDialog = new CheckSchemeEditDialog(billList_scheme, schemePanel, 1, currPlanid, currSchemeid);
			editDialog.setVisible(true);
		} else {
			MessageBox.show(this, "�÷���" + status + "�������޸ġ�");
		}
	}

	/**
	 * ɾ��������ɾ��ǰ��Ҫ�жϷ���״̬�������ϵͳ����Ա��ݣ�������ɾ��������ֻ��ɾ��δִ�еķ���
	 */
	private void onDelete() {
		BillVO selectedRow = billList_scheme.getSelectedBillVO();
		if (selectedRow == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		billList_scheme.refreshCurrSelectedRow();//��ˢ��һ����ȡ״̬
		selectedRow = billList_scheme.getSelectedBillVO();
		String id = selectedRow.getStringValue("id");
		String status = selectedRow.getStringValue("status");
		try {
			if (!status1.equals(status)) {//�������δִ��״̬����ֻ�������Ա���ɾ��������ɾ�������Ϣ �����/2016-08-25��
				if (ClientEnvironment.getInstance().isAdmin()) {
					if (!MessageBox.confirm(this, "�ò�����ɾ��������������Ϣ�������������׸塢����ȣ����Ƿ������")) {
						return;
					}
				} else {
					MessageBox.show(this, "�÷���" + status + "������ɾ����");
					return;
				}
			} else if (!MessageBox.confirmDel(this)) {
				return;
			}
			ArrayList sqlList = new ArrayList();
			sqlList.add("delete from CK_SCHEME where id=" + id);//����
			sqlList.add("delete from CK_MEMBER_WORK where schemeid=" + id);//������Ա���ֹ�
			sqlList.add("delete from ck_manuscript_design where schemeid=" + id);//�׸����
			sqlList.add("delete from ck_scheme_impl where schemeid=" + id);//���ʵʩ��
			sqlList.add("delete from ck_scheme_implement where schemeid=" + id);//���ʵʩ��
			sqlList.add("delete from ck_problem_info where schemeid=" + id);//�����
			//�Ժ����ﻹ�漰ɾ�����ķ��������ĸ��ٵ���Ϣ
			UIUtil.executeBatchByDS(null, sqlList);
			billList_scheme.removeSelectedRow();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * ��������ȷ����
	 * [zzl]
	 */
	private void onExport() {
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
			String DeptName = UIUtil.getStringValueByDS(null, "select name from pub_corp_dept where id=1");
			String planName = UIUtil.getStringValueByDS(null, "select PLANTYPE from CK_PLAN where id='" + vo.getStringValue("planid") + "'");
			if (planName.equals("�Ϲ���")) {
				planName = "�Ϲ�";
			}
			if (planName.equals("��Ƽ��")) {
				planName = "���";
			}
			if (planName.equals("���ɼ��")) {
				planName = "����";
			}
			dataMap.put("title", DeptName + planName + "��鷽��");
			dataMap.put("titletime", vo.getStringValue("CREATEDATE"));
			dataMap.put("code", vo.getStringValue("CODE"));
			dataMap.put("projectnmae", vo.getStringValue("NAME"));
			HashVO [] accvo=UIUtil.getHashVoArrayByDS(null,"select * from ck_manuscript_design where schemeid ='"+vo.getStringValue("id")+"'");
			Map<Object,Object> accmap=new HashMap<Object, Object>();
			for(int i=0;i<accvo.length;i++){
				String tag_law=accvo[i].getStringValue("tag_law");
				String tag_rule=accvo[i].getStringValue("tag_rule");
				String tag_flow=accvo[i].getStringValue("tag_flow");
				if(tag_law!=null){
				accmap.put(tag_law, tag_law);
				}
				if(tag_rule!=null){
				accmap.put(tag_rule, tag_rule);
				}
				if(tag_flow!=null){
				accmap.put(tag_flow, tag_flow);	
				}
			}
			StringBuffer accsb=new StringBuffer();
			for (Object s : accmap.keySet()) {
				accsb.append(s);
			}
			UpdateSQLBuilder update=new UpdateSQLBuilder("CK_SCHEME");
			update.setWhereCondition("id=" + vo.getStringValue("id"));
			update.putFieldValue("CHECKGIST", accsb.toString());
			UIUtil.executeUpdateByDS(null, update.getSQL());
			billList_scheme.refreshCurrSelectedRow();
			dataMap.put("according",accsb);
			dataMap.put("purpose", vo.getStringValue("GOAL"));
			dataMap.put("scope", vo.getStringValue("CKSCOPE"));
			dataMap.put("leader", vo.getStringViewValue("LEADER"));
			dataMap.put("leaderP", vo.getStringViewValue("REFEREE"));//���鳤�����ڿ��ǵ��Ŵ����Ū�ɶ�ѡ�����/2016-09-23��
			String ids = vo.getStringValue("MEMBERWORK");
			ids = ids.replace(";", ",");
			ids = ids.substring(0, ids.length() - 1);
			HashVO[] hv = UIUtil.getHashVoArrayByDS(null, "select * from CK_MEMBER_WORK where id in(" + ids + ")");
			StringBuffer sb = new StringBuffer();
			int a = 0;
			for (int i = 0; i < hv.length; i++) {
				a = a + 1;
				sb.append("��" + a + "С��" + "\n");
				String zzname = UIUtil.getStringValueByDS(null, "select name from pub_user where id='" + hv[i].getStringValue("LEADER") + "'");
				sb.append("�鳤:" + zzname + "\r");
				String[] names = UIUtil.getStringArrayFirstColByDS(null, "select name from pub_user where id in(" + filterid(hv[i].getStringValue("TEAMUSERS")) + ")");
				StringBuffer sb2 = new StringBuffer();
				sb2 = splitName(names);
				sb.append("��Ա:" + sb2 + "\r");
				String[] depts = UIUtil.getStringArrayFirstColByDS(null, "select name from pub_corp_dept where id in(" + filterid(hv[i].getStringValue("CHECKEDDEPT")) + ")");
				StringBuffer sb3 = new StringBuffer();
				sb3 = splitName(depts);
				sb.append("������" + sb3 + "\n");
			}
			dataMap.put("names", sb);
			dataMap.put("time", vo.getStringValue("SITETIME"));
			dataMap.put("methods", vo.getStringValue("CONTENTMETHOD"));
			savePath = savePath + "\\" + dataMap.get("title") + ".doc";
			WordExport mdoc = new WordExport();
			mdoc.createDoc(dataMap, "WordPlanModel", savePath);
			MessageBox.show(this, "�����ɹ�");
		} catch (Exception a) {
			a.printStackTrace();
		}

	}

	/**
	 * ��ʼʵʩ����
	 */
	private void onStart() {
		BillVO billvo = billList_scheme.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		String status = billvo.getStringValue("status");
		if (status == null || "".equals(status) || status1.equals(status) || (ClientEnvironment.isAdmin() && status3.equals(status))) {//��������Ա��ݿɽ�����״̬�ķ������¿�ʼ����ֹ��δ�����ļ�¼����Ҫ���¿������������/2016-09-12��
			try {
				String schemeid = billvo.getStringValue("id");
				String memberwork = billvo.getStringValue("MEMBERWORK");
				String count = UIUtil.getStringValueByDS(null, "select count(*) from ck_scheme_impl where schemeid=" + schemeid);
				if (count != null && !count.equals("0")) {
					MessageBox.show(this, "�÷�������ʵʩ��¼�����������ɡ�");
					UIUtil.executeUpdateByDS(null, "update CK_SCHEME set status='" + status2 + "' where id =" + billvo.getStringValue("id"));
					billList_scheme.refreshCurrSelectedRow();
					return;
				}
				final String[][] works = UIUtil.getStringArrayByDS(null, "select id,leader,teamusers,checkeddept from CK_MEMBER_WORK where schemeid=" + schemeid);
				if (works == null || works.length == 0) {
					MessageBox.show(this, "���޸ķ�������Ӽ�����Ա���ֹ��ٿ�ʼ��");
					return;
				}
				final String[] manuscriptids = UIUtil.getStringArrayFirstColByDS(null, "select id from ck_manuscript_design where schemeid = " + schemeid);
				if (manuscriptids == null || manuscriptids.length == 0) {
					MessageBox.show(this, "���޸ķ�������ӵ׸�����ٿ�ʼ��");
					return;
				}

				final ArrayList sqlList = new ArrayList();
				final InsertSQLBuilder isqlBuilder1 = new InsertSQLBuilder("ck_scheme_impl");
				final InsertSQLBuilder isqlBuilder = new InsertSQLBuilder("ck_scheme_implement");
				//���ʵʩ�ӱ����ü��ƻ�����鷽���ȹ�����ϵ��������Ϣ
				isqlBuilder1.putFieldValue("schemeid", schemeid);
				isqlBuilder1.putFieldValue("planid", billvo.getStringValue("planid", ""));
				isqlBuilder1.putFieldValue("planname", billvo.getStringValue("planname", ""));
				isqlBuilder1.putFieldValue("code", billvo.getStringValue("code", ""));
				isqlBuilder1.putFieldValue("schemetype", billvo.getStringValue("schemetype", ""));
				isqlBuilder1.putFieldValue("name", billvo.getStringValue("name", ""));
				isqlBuilder1.putFieldValue("leader", billvo.getStringValue("leader", ""));
				isqlBuilder1.putFieldValue("referee", billvo.getStringValue("referee", ""));
				isqlBuilder1.putFieldValue("checkgist", billvo.getStringValue("checkgist", ""));
				isqlBuilder1.putFieldValue("goal", billvo.getStringValue("goal", ""));
				isqlBuilder1.putFieldValue("ckscope", billvo.getStringValue("ckscope", ""));
				isqlBuilder1.putFieldValue("sitetime", billvo.getStringValue("sitetime", ""));
				isqlBuilder1.putFieldValue("contentmethod", billvo.getStringValue("contentmethod", ""));
				isqlBuilder1.putFieldValue("status", status1);//����ʵʩ״̬"δִ��"���ڼ��ʵʩ������¼����Ϣ����Ϊ"ִ����"����������ʱ��Ϊ"�ѽ���"�����/2016-09-29��
				isqlBuilder1.putFieldValue("createdate", billvo.getStringValue("createdate", ""));//����������÷�����ʼ��ʱ��㣬ǰ�����Ƿ�����Ϣ���о��ֵֹģ������෽����Ϣ�����/2016-09-26��
				isqlBuilder1.putFieldValue("creater", billvo.getStringValue("creater", ""));
				isqlBuilder1.putFieldValue("createdept", billvo.getStringValue("createdept", ""));
				isqlBuilder1.putFieldValue("expstatus", "δ����");//����״̬
				//���ʵʩ�ӱ����ü��ƻ�����鷽����ϵ
				isqlBuilder.putFieldValue("planid", billvo.getStringValue("planid", ""));
				isqlBuilder.putFieldValue("schemeid", schemeid);
				String SCHEMETYPE = billvo.getStringValue("SCHEMETYPE");//������ͣ��Ŵ���顢Ʊ�ݼ��
				if (SCHEMETYPE != null && ("�Ŵ����".equals(SCHEMETYPE) || "Ʊ�ݼ��".equals(SCHEMETYPE))) {//�Ŵ���Ʊ�ݼ����뵼�������⡾���/2016-10-14��
					JFileChooser chooser = new JFileChooser();
					chooser.setDialogTitle("�뵼��������");
					FileNameExtensionFilter filter = new FileNameExtensionFilter("(*.xls,*.xlxs)", "xls", "xlsx");
					chooser.addChoosableFileFilter(filter);
					int flag = chooser.showOpenDialog(this);
					if (flag == 1) {//ֱ�ӹرմ��ڣ�δѡ���ļ�
						return;
					}
					File file = chooser.getSelectedFile();
					if (file == null) {
						return;
					}
					final String[][] datas = new ExcelUtil().getExcelFileData(file.getPath());
					//�ͻ����ơ��ͻ���š���ݱ�š����ʽ������Ʒ�֡��ſ����ڡ��������ڡ���ݽ��������������(��)��
					//ִ������(%)������ǷϢ������ǷϢ���弶��̬�������ˡ������������Ϣ��ʽ��������;
					if (datas == null || datas.length < 2) {
						MessageBox.show(this, "���ļ��޿�������!");
						return;
					}
					new SplashWindow(this, new AbstractAction() {//������������ʱʱ��Ƚϳ���������һ�������������/2016-10-14��
								public void actionPerformed(ActionEvent e) {
									try {
										HashMap deptMap = new HashMap();
										for (int m = 1; m < datas.length; m++) {//�ӵڶ��п�ʼ��������һ������ͷ
											String deptname = datas[m][15];//�������
											if (deptname != null && !"".equals(deptname.trim())) {
												deptname = deptname.trim();
												ArrayList deptList = null;
												if (deptMap.containsKey(deptname)) {
													deptList = (ArrayList) deptMap.get(deptname);//��¼��Щ�������ļ���
												} else {
													deptList = new ArrayList();
												}
												deptList.add(m);
												deptMap.put(deptname, deptList);
											}
										}
										String[] deptnames = (String[]) deptMap.keySet().toArray(new String[0]);
										//��ѯ�������������id
										HashMap deptIdNameMap = UIUtil.getHashMapBySQLByDS(null, "select id,name from pub_corp_dept where name in(" + TBUtil.getTBUtil().getInCondition(deptnames) + ")");//�ָ罨����shortname����Ϊ�˺ͺϹ��Ʒͳһ����ʱ����name�����/2016-09-30��
										for (int i = 0; i < works.length; i++) {//������Ա���ֹ�
											String checkeddept = works[i][3];
											String[] checkeddepts = TBUtil.getTBUtil().split(checkeddept, ";");

											//���ʵʩ�������ü���Ա
											isqlBuilder1.putFieldValue("memberid", works[i][0]);
											isqlBuilder1.putFieldValue("leader2", works[i][1]);
											isqlBuilder1.putFieldValue("teamusers", works[i][2]);
											//���ʵʩ�ӱ����ü���Ա
											isqlBuilder.putFieldValue("memberid", works[i][0]);
											isqlBuilder.putFieldValue("leader2", works[i][1]);
											isqlBuilder.putFieldValue("teamusers", works[i][2]);

											for (int j = 0; j < checkeddepts.length; j++) {//С���еı�������
												checkeddept = checkeddepts[j];//������������
												if (deptIdNameMap.containsKey(checkeddept)) {//�����������������иñ���������¼���򰴼�¼�����������ӱ�
													ArrayList deptList = (ArrayList) deptMap.get(deptIdNameMap.get(checkeddept));//��¼��Щ�������ļ���
													for (int k = 0; k < deptList.size(); k++) {//�ñ��������������ݵ��к�
														//���ʵʩ����������������������
														String implid = UIUtil.getSequenceNextValByDS(null, "S_CK_SCHEME_IMPL");
														isqlBuilder1.putFieldValue("id", implid);
														isqlBuilder1.putFieldValue("deptid", checkeddept);
														//���õ�������
														for (int n = 1; n < 19; n++) {
															isqlBuilder1.putFieldValue("c" + n, datas[(Integer) deptList.get(k)][n - 1]);
														}
														sqlList.add(isqlBuilder1.getSQL());
														//���ʵʩ�ӱ��������������ϵ����������
														isqlBuilder.putFieldValue("implid", implid);
														isqlBuilder.putFieldValue("deptid", checkeddept);//��������

														for (int j2 = 0; j2 < manuscriptids.length; j2++) {
															isqlBuilder.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_CK_SCHEME_IMPLEMENT"));
															isqlBuilder.putFieldValue("parentid", manuscriptids[j2]);//���׸�id
															sqlList.add(isqlBuilder.getSQL());
														}
													}
												} else {//���������û�иñ�����������Ĭ������
													//���ʵʩ����������������������
													String implid = UIUtil.getSequenceNextValByDS(null, "S_CK_SCHEME_IMPL");

													isqlBuilder1.putFieldValue("id", implid);
													isqlBuilder1.putFieldValue("deptid", checkeddept);
													sqlList.add(isqlBuilder1.getSQL());
													//���ʵʩ�ӱ��������������ϵ����������
													isqlBuilder.putFieldValue("implid", implid);
													isqlBuilder.putFieldValue("deptid", checkeddept);//��������

													for (int j2 = 0; j2 < manuscriptids.length; j2++) {
														isqlBuilder.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_CK_SCHEME_IMPLEMENT"));
														isqlBuilder.putFieldValue("parentid", manuscriptids[j2]);//���׸�id
														sqlList.add(isqlBuilder.getSQL());
													}
												}
											}
										}
									} catch (WLTRemoteException e1) {
										e1.printStackTrace();
									} catch (Exception e1) {
										e1.printStackTrace();
									}
								}
							}, 600, 130, 300, 300, false); // 
				} else {//��������Ŵ�����Ʊ�ݼ�飬��ÿ����λ��һ�����ʵʩ����
					for (int i = 0; i < works.length; i++) {//������Ա���ֹ�
						String checkeddept = works[i][3];
						String[] checkeddepts = TBUtil.getTBUtil().split(checkeddept, ";");

						//���ʵʩ�������ü���Ա
						isqlBuilder1.putFieldValue("memberid", works[i][0]);
						isqlBuilder1.putFieldValue("leader2", works[i][1]);
						isqlBuilder1.putFieldValue("teamusers", works[i][2]);
						//���ʵʩ�ӱ����ü���Ա
						isqlBuilder.putFieldValue("memberid", works[i][0]);
						isqlBuilder.putFieldValue("leader2", works[i][1]);
						isqlBuilder.putFieldValue("teamusers", works[i][2]);

						for (int j = 0; j < checkeddepts.length; j++) {//С���еı�������
							checkeddept = checkeddepts[j];//������������
							//���ʵʩ����������������������
							String implid = UIUtil.getSequenceNextValByDS(null, "S_CK_SCHEME_IMPL");
							isqlBuilder1.putFieldValue("id", implid);
							isqlBuilder1.putFieldValue("deptid", checkeddept);
							sqlList.add(isqlBuilder1.getSQL());
							//���ʵʩ�ӱ��������������ϵ����������
							isqlBuilder.putFieldValue("implid", implid);
							isqlBuilder.putFieldValue("deptid", checkeddept);//��������

							for (int j2 = 0; j2 < manuscriptids.length; j2++) {
								isqlBuilder.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_CK_SCHEME_IMPLEMENT"));
								isqlBuilder.putFieldValue("parentid", manuscriptids[j2]);//���׸�id
								sqlList.add(isqlBuilder.getSQL());
							}
						}
					}
				}
				sqlList.add("update CK_SCHEME set status='" + status2 + "' where id =" + schemeid);
				UIUtil.executeBatchByDS(null, sqlList);
				billList_scheme.refreshCurrSelectedRow();
				MessageBox.show(this, "�����ɹ���");
			} catch (Exception e) {
				e.printStackTrace();
				MessageBox.showException(this, e);
			}
		} else {
			MessageBox.show(this, "�÷���" + status + "�������ٿ�ʼִ�С�");
		}
	}

	/**
	 * ��������
	 */
	private void onEnd() {
		BillVO billvo = billList_scheme.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		billList_scheme.refreshCurrSelectedRow();//ˢ��һ�£���ֹ��ѯ�����˸Ĺ�״̬��
		billvo = billList_scheme.getSelectedBillVO();
		String status = billvo.getStringValue("status");
		if (status3.equals(status)) {
			MessageBox.show(this, "�÷���" + status + "�������ٽ�����");
			return;
		} else if (!status2.equals(status)) {
			MessageBox.show(this, "ֻ��" + status2 + "�ķ����ɽ��д˲�����");
			return;
		}
		//������Ҫ�ж��Ƿ���δʵʩ�ļ��׸壬���У�����ʾ
		String schemeid = billvo.getStringValue("id");
		try {
			String[][] impls = UIUtil.getStringArrayByDS(null, "select t2.name username,t3.name deptname from ck_scheme_impl t1 left join pub_user t2 on t1.leader2=t2.id left join pub_corp_dept t3 on t1.deptid = t3.id where t1.schemeid='" + schemeid + "' and t1.status!='�ѽ���' group by t1.leader2 ,t1.deptid  order by t1.leader2");
			if (impls != null && impls.length > 0) {//������ݿ��в�ѯ��δ����ļ�����������ʾ�����/2016-09-12��
				StringBuffer sb = new StringBuffer("���µ�λδ�����ϣ�\n");
				String tempname = "";
				for (int i = 0; i < impls.length; i++) {
					if (tempname.equals(impls[i][0])) {
						sb.append("��" + impls[i][1]);
					} else {
						tempname = impls[i][0];
						if (i != 0) {
							sb.append("\n");
						}
						sb.append(impls[i][0] + "�飺" + impls[i][1]);
					}
				}
				if (MessageBox.confirm(this, sb.toString() + "\n�������޷��ٽ��м��ʵʩ���Ƿ������")) {
					UIUtil.executeBatchByDS(null, new String[] { "update CK_SCHEME set status='" + status3 + "' where id =" + schemeid, "update ck_scheme_impl set status='" + status3 + "' where schemeid =" + schemeid, });//�����/2016-09-29��
					billList_scheme.refreshCurrSelectedRow();
					MessageBox.show(this, "�����ɹ���");
				}
			}

		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onBillListSelectChanged(BillListSelectionEvent e) {
		vo = e.getBillListPanel().getSelectedBillVO();
	}

	/**
	 * �б�˫���������Ҫ��ʾ�׸���Ϣ�����/2016-09-03��
	 */
	public void onMouseDoubleClicked(BillListMouseDoubleClickedEvent event) {
		BillVO selectedRow = event.getCurrSelectedVO();
		if (selectedRow == null) {
			return;
		}
		BillCardPanel schemePanel = new BillCardPanel(billList_scheme.getTempletVO());//����������Ϣ
		schemePanel.setBillVO(selectedRow);
		schemePanel.setEditable(false);
		schemePanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT);

		String currPlanid = selectedRow.getStringValue("planid");//���õ�ǰ�ƻ�
		String currSchemeid = selectedRow.getStringValue("id");//���õ�ǰ����

		CheckSchemeEditDialog editDialog = new CheckSchemeEditDialog(billList_scheme, schemePanel, 2, currPlanid, currSchemeid);
		editDialog.setVisible(true);
	}

	private String filterid(String ids) {
		ids = ids.replace(";", ",");
		ids = ids.substring(1, ids.length() - 1);
		return ids;

	}

	private StringBuffer splitName(String[] name) {
		StringBuffer sbname = new StringBuffer();
		for (int i = 0; i < name.length; i++) {
			sbname.append(name[i].toString());
			sbname.append(",");
		}
		return sbname;
	}
	public static String getPlanId(){
		return id;
		
	}

}
