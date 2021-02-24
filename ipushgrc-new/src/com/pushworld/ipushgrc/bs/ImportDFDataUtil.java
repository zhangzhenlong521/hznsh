package com.pushworld.ipushgrc.bs;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JWindow;

import org.jgraph.JGraph;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.SystemOptions;
import cn.com.infostrategy.bs.common.WLTDBConnection;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.bs.workflow.WorkFlowDesignDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.workflow.design.ProcessVO;
import cn.com.infostrategy.to.workflow.design.RiskVO;
import cn.com.infostrategy.ui.workflow.design.WorkFlowDesignWPanel;

/**
 * ���ũ�������ݽӿ��࣬�ͻ�����DB2���͡����/2013-07-23�� 1.��λ�ͻ������Ա�����Ψһ�Ա�ʶ���ͻ����кͺϹ���ж������ݣ�������û������
 * 2.��λ����ֻ����ʵ�ʸ�λ����λ�飨deptid is null�����õ��룬���Ÿ�λ����Ҳֻ����ʵ�ʸ�λ 3.���̷��飬�������ļ���ҵ�����
 * 4.���̱��е����̷���ָ���Ǹ��������������ļ��Ĺ������ҵ�������ҵ�����Ĺ��� 5.���̱��еĲ��ֻ�����ǡ�1����ʾ���̣���2����ʾ����
 * 6.���̱��е�·�������ǡ�.processid.����.processid.activityid.��
 * 7.���̱��е�����ͼ�����ǽ��Ϲ�ϵͳ������ͼ���ͼƬ�Ķ������� 8.�Ϲ���еķ��պͿ��ƴ�ʩ��Ҫ��ֳ�����������¼������ϵ
 * 9.���������ԴӺϹ����ȡpub_comboboxdict��type='��������'������
 * 10.�������ӱ���Ҫ�ϲ������Ϊ��1����ʾ������ʱʱЧ�ԡ�����������������ࡢ�ĺš��䲼��λ���䲼������Ҫ�������ݣ�
 * ���Ϊ��2����ʾ�ӱ��¼������Ҫ������������ݣ�ֻ������⡢���ݡ����ڵ㡢��μ���
 * 11.���̷���������е�processid��lawid�п����ǺϹ��������¼Ҳ�������ӱ��¼ 12.���ƴ�ʩ���еİ汾version
 * ֱ������Ϊ��1.0����ɾ����ʶdelflag����Ϊ��0��//���������Ȳ����ǣ��Ժ�ͻ��϶���Ҫͳһ����
 * 
 * 
 * @author lcj
 * 
 */
public class ImportDFDataUtil {
	private HashMap postmap = new HashMap();// ��id����id�Ķ�Ӧ��ϵ
	private HashMap processmap = new HashMap();
	private HashMap activitymap = new HashMap();
	private HashMap riskprocessmap = new HashMap();
	private HashMap lawitemmap = new HashMap();
	private HashMap lawmap = new HashMap();
	private HashMap lawtype = new HashMap();
	private HashMap map_dept = new HashMap();
	private HashMap map_dfdept = new HashMap();
	private String df_post = "DB2ADMIN.\"POSITIONS\"";// ������ݿ��и�λ��
	private String df_dept = "DB2ADMIN.\"DEPARTMENT\"";// ������ݿ��л�����
	private String df_dept_post = "DB2ADMIN.\"DEPPOSLINK\"";// ������ݿ��в��Ÿ�λ������
	private String df_post_process = "DB2ADMIN.\"LNKPROPOS\"";// ������ݿ��и�λ���̹�����
	private String df_processgroup = "DB2ADMIN.\"PROGROUP\"";// ������ݿ������̷����
	private String df_process = "DB2ADMIN.\"BIZPROCESS\"";// ������ݿ������̱�
	private String df_risk = "DB2ADMIN.\"RISK\"";// ������ݿ��з��ձ�
	private String df_ctrl = "DB2ADMIN.\"CONTROLDEF\"";// ������ݿ��п��ƴ�ʩ��
	private String df_lawtype = "DB2ADMIN.\"LAWTYPE\"";// ������ݿ��з�������

	private String df_law = "DB2ADMIN.\"LAWDEF\"";// ������ݿ��з����
	private String df_process_law = "DB2ADMIN.\"LNKPROLAW\"";// ������ݿ������̷��������
	private String df_risk_ctrl = "DB2ADMIN.\"LNKRISCON\"";// ������ݿ��з��յ���ƴ�ʩ������
	private String df_process_risk = "DB2ADMIN.\"LNKPRORI\"";// ������ݿ������̷��չ�����

	private CommDMO commDMO = new CommDMO();

	private String datasource = null;// ����Ӧ���ǿͻ�ƽ̨ϵͳ������Դ����
	private HashVO[] postVO, dfpostVO = null;// �Ϲ�ϵͳ��λ��VO,�Է�ϵͳ��λVO

	/**
	 * �����������ݵķ���
	 */
	public void importAllData() throws Exception {
		ArrayList arrayList = new ArrayList();
		// �����͸�λ����ɾ������λ�����������ϵ��ɾ������Ϊÿ�ζ������ӵĸ�λ�����Ĺ�����ϵ
		arrayList.add("delete from " + df_post_process);
		arrayList.add("delete from " + df_processgroup);
		arrayList.add("delete from " + df_process);
		arrayList.add("delete from " + df_risk);
		arrayList.add("delete from " + df_ctrl);
		arrayList.add("delete from " + df_lawtype);
		arrayList.add("delete from " + df_law);
		arrayList.add("delete from " + df_process_law);
		arrayList.add("delete from " + df_risk_ctrl);
		arrayList.add("delete from " + df_process_risk);
		commDMO.executeBatchByDSImmediately(datasource, arrayList);// ִ�е���ǰ��ɾ��һ����Ҫ��յļ�¼����������������ύһ�¡�

		// �����ִ�п϶���˳����Ϊ�еĹ�ϵ����Ҫ��id
		commDMO.executeBatchByDS(datasource, importPost());
		commDMO.executeBatchByDS(datasource, importDetpPost());
		commDMO.executeBatchByDS(datasource, importProcessGroup());
		commDMO.executeBatchByDS(datasource, importProcess());
		commDMO.executeBatchByDS(datasource, importPostProcess());
		commDMO.executeBatchByDS(datasource, importRisk());
		commDMO.executeBatchByDS(datasource, importCtrl());
		commDMO.executeBatchByDS(datasource, importLawtype());
		commDMO.executeBatchByDS(datasource, importLaw());
		commDMO.executeBatchByDS(datasource, importProcessLaw());

	}

	/*************************** ���츺�� ************************/
	/**
	 * ��λ
	 */
	public ArrayList<String> importPost() throws Exception {
		ArrayList<String> sqList = new ArrayList<String>();
		String sql_post = "select id,code,name,descr,deptid from pub_post where deptid is not null and code is not null";// �ų���λ��
		String sql_dfpost = "select id,posnum,posname,posdesc from " + df_post;
		postVO = commDMO.getHashVoArrayByDS(null, sql_post);// �Ϲ�ϵͳ��λ
		dfpostVO = commDMO.getHashVoArrayByDS(datasource, sql_dfpost);// �Է�ϵͳ��λ
		String dfmaxpostid = commDMO.getStringValueByDS(datasource, "select max(id) from " + df_post);// �Է�ϵͳ��λ���id
		if (TBUtil.isEmpty(dfmaxpostid)) {// ���Ϊ�գ�Ĭ��Ϊ1
			dfmaxpostid = "1";
		}

		InsertSQLBuilder insertSQL = new InsertSQLBuilder(df_post);
		HashMap<String, String> map_dfpost = new HashMap<String, String>();// ��ŶԷ���λ����
		for (int i = 0; i < dfpostVO.length; i++) {
			String dfpostname = dfpostVO[i].getStringValue("posname", "").trim();
			map_dfpost.put(dfpostname, "");
		}

		for (int i = 0; i < postVO.length; i++) {
			String postname = postVO[i].getStringValue("name", "").trim();// �Ϲ��λ����
			if (map_dfpost.containsKey(postname)) {// �������������ͬ���򲻵����������;�˴���Ϊ��λ������Ψһ��������ϵͳһ��
				continue;
			}
			// û���ظ���ͬ��name������

			int postid = Integer.parseInt(dfmaxpostid) + (i + 1);// ��Ϊ�Է�ϵͳ��λ�������ݣ�����id�Ӹ�λ�����ID��(i+1)��ʼ
			String postcode = postVO[i].getStringValue("code", "");// ��λ����
			String postdescr = postVO[i].getStringValue("descr", "");// ��λ����
			insertSQL.putFieldValue("id", postid);
			insertSQL.putFieldValue("posnum", postcode);
			insertSQL.putFieldValue("posname", postname);
			insertSQL.putFieldValue("posdesc", postdescr);
			insertSQL.putFieldValue("delflag", "0");
			sqList.add(insertSQL.getSQL());
			postmap.put(postVO[i].getStringValue("id", ""), postid + "");// ����ID����ID-��ID
		}
		return sqList;
	}

	/**
	 * ���Ÿ�λ����
	 */
	public ArrayList<String> importDetpPost() throws Exception {
		ArrayList<String> sqList = new ArrayList<String>();
		InsertSQLBuilder insertSQL = new InsertSQLBuilder(df_dept_post);
		HashMap<String, String> map_post = new HashMap<String, String>();// �Ϲ�ϵͳ��λ��MAP
		HashMap<String, String> map_dfpost = new HashMap<String, String>();// �Է�ϵͳ��λ��MAP
		for (int i = 0; i < postVO.length; i++) {
			map_post.put(postVO[i].getStringValue("id", ""), postVO[i].getStringValue("name", ""));
		}
		for (int i = 0; i < dfpostVO.length; i++) {
			map_dfpost.put(dfpostVO[i].getStringValue("posname", ""), dfpostVO[i].getStringValue("id", ""));
		}
		String sql_dept = "select id,name from pub_corp_dept";// �Ϲ�ϵͳ������
		String sql_dfdept = "select depname,id from " + df_dept;// ��Է�ϵͳ������name��ʾ
		map_dept = commDMO.getHashMapBySQLByDS(null, sql_dept);// �Ϲ�ϵͳ������MAP
		map_dfdept = commDMO.getHashMapBySQLByDS(datasource, sql_dfdept);// �Է�ϵͳ��������map
		for (int i = 0; i < postVO.length; i++) {
			String deptid = postVO[i].getStringValue("deptid", "");
			if (!map_dfdept.containsKey(map_dept.get(deptid))) {// ����Է�ϵͳ���޴˻�����code�������룻�˴���Ϊ����CODE��Ψһ��������ϵͳһ��
				continue;
			}
			String postid = postVO[i].getStringValue("id", "");// �Ϲ�ϵͳ��λid
			if (TBUtil.isEmpty(postid)) {
				continue;
			}
			String positionid = "";// �Է�ϵͳ�������λID
			if (postmap.containsKey(postid)) {// ���postmap������ø�λID��˵����������ĸ�λ���ʼ�����Ӧ��������
				positionid = (String) postmap.get(postid);
			} else {// ������λ��¼MAP�����û�У��򲻼�����Ӧ��������
				continue;
			}
			String departmentid = map_dfdept.get(map_dept.get(deptid)).toString();// �õ��Է�ϵͳ��Ӧ����ID
			insertSQL.putFieldValue("id", postid);
			insertSQL.putFieldValue("departmentid", departmentid);
			insertSQL.putFieldValue("positionid", positionid);
			sqList.add(insertSQL.getSQL());
		}
		return sqList;
	}

	/**
	 * ��λ���̹���
	 */
	public ArrayList importPostProcess() throws Exception {
		ArrayList sqList = new ArrayList();
		InsertSQLBuilder insertSQL = new InsertSQLBuilder(df_post_process);
		HashMap<String, String> map_post = new HashMap<String, String>();// �Ϲ�ϵͳ��λ��MAP
		HashMap<String, String> map_dfpost = new HashMap<String, String>();// �Է�ϵͳ��λ��MAP
		for (int i = 0; i < postVO.length; i++) {
			map_post.put(postVO[i].getStringValue("id", ""), postVO[i].getStringValue("name", ""));
		}
		for (int i = 0; i < dfpostVO.length; i++) {
			map_dfpost.put(dfpostVO[i].getStringValue("posname", ""), dfpostVO[i].getStringValue("id", ""));
		}
		String sql_wfpost = "select id,wfactivity_id,operatepost from cmp_cmpfile_wfopereq where wfactivity_id in(select id from pub_wf_activity where processid is not null) and operatepost is not null";// �Ϲ�ϵͳ��λ���̹�����
		HashVO[] wfpostVO = commDMO.getHashVoArrayByDS(null, sql_wfpost);// �Ϲ�ϵͳ��λ���̹�����VO
		for (int i = 0; i < wfpostVO.length; i++) {
			String processid = wfpostVO[i].getStringValue("wfactivity_id", "");// ����ID
			if (activitymap.containsKey(processid)) {// �������MAP�����������ID��˵��ID�Ѹģ��õ���ID
				processid = activitymap.get(processid).toString();// �õ���Ӧ������ID
			}
			String wfpostid = wfpostVO[i].getStringValue("id", "");// ��λ���̹�����ID
			if (TBUtil.isEmpty(wfpostid)) {
				continue;
			}
			String postid = wfpostVO[i].getStringValue("operatepost", "");// �Ϲ�ϵͳ�����������λid
			String positionid = "";// �Է�ϵͳ�������λID

			if (!postmap.containsKey(postid)) {// postmap��û�м�¼�ø�λID�滻���,˵���Է���λ���иø�λ���͵��öԷ��ĸ�λID
				positionid = map_dfpost.get(map_post.get(postid));
			} else {// �õ���ȥ���¸�λID
				positionid = (String) postmap.get(postid);
			}
			if (positionid == null || positionid.equals("")) {
				continue;
			}
			insertSQL.putFieldValue("id", wfpostid);
			insertSQL.putFieldValue("processid", processid);
			insertSQL.putFieldValue("posid", positionid);
			sqList.add(insertSQL.getSQL());
		}
		return sqList;
	}

	/*************************** ��긺�� ************************/

	/**
	 * ����
	 */
	public ArrayList importProcess() throws Exception {
		ArrayList sqList = new ArrayList();
		// �ȴ����������̼�¼���ñ���id����
		HashVO[] vos = commDMO.getHashVoArrayByDS(null, "select wfpro.id,wfpro.code num,wfpro.name,cmpfile.bsactid progroupid,cmpfile.blcorpid ownerdepid from pub_wf_process wfpro left join cmp_cmpfile cmpfile on wfpro.cmpfileid= cmpfile.id ");
		if (vos != null && vos.length > 0) {
			for (int i = 0; i < vos.length; i++) {
				StringBuffer sb_sql = new StringBuffer("insert into ");
				sb_sql.append(df_process);
				sb_sql.append(" (id,num,name,progroupid,ownerdepid,prolevel,bizpath,propicture,delflag) values (");
				sb_sql.append(vos[i].getStringValue("id") + ",");
				sb_sql.append("'" + vos[i].getStringValue("num") + "',");
				sb_sql.append("'" + vos[i].getStringValue("name") + "',");
				if (vos[i].getStringValue("progroupid") == null || vos[i].getStringValue("progroupid").trim().equals("")) {
					sb_sql.append("null,");
				} else {
					sb_sql.append(vos[i].getStringValue("progroupid") + ",");
				}
				if (vos[i].getStringValue("ownerdepid") == null || vos[i].getStringValue("ownerdepid").trim().equals("")) {
					sb_sql.append("null,");
				} else {
					sb_sql.append(map_dfdept.get(map_dept.get(vos[i].getStringValue("ownerdepid"))) + ",");
				}
				sb_sql.append("1,");// ���
				sb_sql.append("'." + vos[i].getStringValue("id") + ".',");// ·��
				sb_sql.append("empty_blob()");// ����ͼ
				sb_sql.append(",'0')");
				sqList.add(sb_sql.toString());
			}
			commDMO.executeBatchByDSImmediately(this.datasource, sqList);
			sqList.clear();
			// ����ͼƬ�ֶ�
			WLTDBConnection conn = new WLTInitContext().getConn(datasource);
			for (int i = 0; i < vos.length; i++) {
				byte[] aString = getProPicture(vos[i].getStringValue("id"));
				String sqlchange = "select propicture from " + df_process + " where id = " + vos[i].getStringValue("id") + " for update";
				PreparedStatement statement = conn.prepareStatement(sqlchange);
				ResultSet resultSet = statement.executeQuery();
				if (resultSet.next()) {
					com.ibm.db2.jcc.a.be blob = (com.ibm.db2.jcc.a.be) resultSet.getBlob("propicture");
					OutputStream outputStream = blob.setBinaryStream(1);
					outputStream.write(aString, 0, aString.length);
					outputStream.flush();
					outputStream.close();

					statement = conn.prepareStatement("update " + df_process + " set propicture =? where id=?");
					statement.setBlob(1, blob);
					statement.setInt(2, Integer.parseInt(vos[i].getStringValue("id")));
					statement.executeUpdate();
					statement.close();
				}
			}
			new WLTInitContext().commitAllTrans();
		}
		// �ٴ�����������������ͻ�Ļ��ڼ�¼
		HashVO[] vos_activity = commDMO.getHashVoArrayByDS(null, "select id,code num,wfname name,processid parentid from pub_wf_activity where processid in (select id from pub_wf_process) and id not in (select id from pub_wf_process)");
		if (vos_activity != null && vos_activity.length > 0) {
			InsertSQLBuilder sqlBuilder = new InsertSQLBuilder(df_process);
			String[] keys = vos_activity[0].getKeys();
			for (int i = 0; i < vos_activity.length; i++) {
				for (int j = 0; j < keys.length; j++) {
					sqlBuilder.putFieldValue(keys[j], vos_activity[i].getStringValue(keys[j]));

				}
				sqlBuilder.putFieldValue("delflag", "0");// ���
				sqlBuilder.putFieldValue("prolevel", 2);// ���
				sqlBuilder.putFieldValue("bizpath", "." + vos_activity[i].getStringValue("id") + "." + vos_activity[i].getStringValue("parentid") + ".");// ·��
				sqList.add(sqlBuilder.getSQL());
			}

		}
		// ��󴴽�������������ͻ�Ļ��ڼ�¼
		HashVO[] vos_activity2 = commDMO.getHashVoArrayByDS(null, "select id,code num,wfname name,processid parentid from pub_wf_activity where processid in (select id from pub_wf_process) and id in (select id from pub_wf_process)");
		if (vos_activity2 != null && vos_activity2.length > 0) {
			String maxid = commDMO.getStringValueByDS(null, "select max(id) id from pub_wf_process union all select max(id) id from pub_wf_activity order by id desc");
			int int_maxid = Integer.parseInt(maxid);

			InsertSQLBuilder sqlBuilder = new InsertSQLBuilder(df_process);
			String[] keys = vos_activity2[0].getKeys();
			for (int i = 0; i < vos_activity2.length; i++) {
				for (int j = 0; j < keys.length; j++) {
					sqlBuilder.putFieldValue(keys[j], vos_activity2[i].getStringValue(keys[j]));

				}
				int_maxid++;
				activitymap.put(vos_activity2[i].getStringValue("id"), "" + int_maxid);// ���ڵľ�id��Ϊkey����id��Ϊvalue
				sqlBuilder.putFieldValue("id", int_maxid);
				sqlBuilder.putFieldValue("prolevel", 2);// ���
				sqlBuilder.putFieldValue("delflag", "0");// ���
				sqlBuilder.putFieldValue("bizpath", "." + vos_activity2[i].getStringValue("id") + "." + vos_activity2[i].getStringValue("parentid") + ".");// ·��
				sqList.add(sqlBuilder.getSQL());
			}
		}
		return sqList;
	}

	/**
	 * ���ĳ�����̵�����ͼƬ
	 * 
	 * @param _processid
	 * @return
	 * @throws Exception
	 */
	public byte[] getProPicture(String _processid) throws Exception {
		// �ڶ�����Զ�̷���һ���Ӳ�����з��յ�
		HashVO[] hvs_risk = commDMO.getHashVoArrayByDS(null, "select wfprocess_id,wfactivity_id,rank from cmp_risk where wfprocess_id='" + _processid + "'"); //

		// ����������������ͼ!
		// ֧������,��ÿ�������˳��һ��!!!
		WorkFlowDesignWPanel wfPanel = new WorkFlowDesignWPanel(false); // ����һ��������!����ʾ�����䡾���/2012-03-08��

		ProcessVO currentProcessVO = new WorkFlowDesignDMO().getWFProcessByWFID(_processid);// ��Ϊ�����ڷ�������ִ�У�����Ҫ��dmoֱ�ӻ��ProcessVO��������Զ�̵��ã�����ᱨ��
		if (currentProcessVO == null) {
			return "".getBytes();
		}
		wfPanel.openMainGraph(currentProcessVO, 0); // ����һ������VO,��һ��ͼ��!!
		wfPanel.setGridVisible(false);// ���ò���ʾ������㡾���/2012-11-16��
		//		wfPanel.showStaff(false);// ���ò���ʾ���
		boolean isExportTitle = SystemOptions.getInstance().getBooleanValue("�����������Ƿ��б���", true); // �ڿ�ϵͳ����һЩ�ͻ�Ҫ�󵼳�ʱ��Ҫ����!
		if (!isExportTitle) {
			wfPanel.setTitleCellForeground(Color.WHITE);// ���ñ�����ɫΪ��ɫ��������ʾ
		}
		wfPanel.reSetAllLayer(false);// ��������һ��,����׶�������µļ�ͷ�п��ܲ���ʾ[���/2012-11-19]
		// �ӷ��յ�
		HashMap riskMap = new HashMap(); //
		for (int j = 0; j < hvs_risk.length; j++) {
			if (hvs_risk[j].getStringValue("wfprocess_id") != null && hvs_risk[j].getStringValue("wfprocess_id").equals(_processid)) { // ���ڱ����̵�
				String str_activity_id = hvs_risk[j].getStringValue("wfactivity_id"); //
				if (str_activity_id != null) {
					int li_1 = 0, li_2 = 0, li_3 = 0;
					if (riskMap.containsKey(str_activity_id)) { // ����Ѿ����˷��յ�
						RiskVO rvo = (RiskVO) riskMap.get(str_activity_id); // //
						li_1 = rvo.getLevel1RiskCount();
						li_2 = rvo.getLevel2RiskCount();
						li_3 = rvo.getLevel3RiskCount();
					}
					String str_rank = hvs_risk[j].getStringValue("rank"); // ���յȼ�
					if (str_rank != null) {
						if (str_rank.equals("�߷���") || str_rank.equals("�������")) {
							li_1++;
						} else if (str_rank.equals("�ͷ���") || str_rank.equals("��С����")) {
							li_3++;
						} else {
							li_2++; // �еȷ���
						}
					} else {
						li_2++; // �еȷ���
					}
					RiskVO rsvo = new RiskVO(li_1, li_2, li_3); //
					riskMap.put(str_activity_id, rsvo); // ��������!!!
				}
			}
		}
		String[] str_keys = (String[]) riskMap.keySet().toArray(new String[0]); //
		for (int k = 0; k < str_keys.length; k++) {
			RiskVO rvo = (RiskVO) riskMap.get(str_keys[k]); // //
			if (rvo != null) {
				wfPanel.setCellAddRisk(str_keys[k], rvo); // //
			}
		}

		JGraph graph = wfPanel.getGraph(); // //
		int li_width = (int) graph.getPreferredSize().getWidth(); //
		int li_height = (int) graph.getPreferredSize().getHeight(); //

		JWindow win = new JWindow(); // ����һ������,��֪��Ϊʲôһ��ҪŪһ��������ʾ����,���ܰ�ͼ����ȥ!!!
		win.setSize(0, 0); //
		win.getContentPane().add(wfPanel); // 
		win.toBack(); //
		win.setVisible(true); //
		if (li_width == 0 || li_height == 0) {// �������û�л���
			li_width = 1;
			li_height = 1;
		}
		BufferedImage image = new BufferedImage(li_width, li_height, BufferedImage.TYPE_INT_RGB); // ����һ���հ׵�ͼƬ!!
		Graphics g = image.createGraphics(); // ΪͼƬ����һ���µĻ���!!
		graph.paint(g); // ���ؼ���ͼ��д�뵽����µĻ�����
		g.dispose();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(image, "JPEG", out);
		byte[] imgBytes = out.toByteArray(); // ���ɶ���������!!!
		win.dispose();
		win = null; // �ڴ��ͷ�
		// return str_64code; //����!!
		return imgBytes;
	}

	/**
	 * ���գ���������չ���
	 */
	public ArrayList importRisk() throws Exception {
		ArrayList sqList = new ArrayList();
		// �ȴ������з��գ�id����
		HashVO[] vos = commDMO.getHashVoArrayByDS(null, "select id,riskcode,riskname,riskdescr riskdesc,wfprocess_id,wfactivity_id from cmp_risk");
		if (vos != null && vos.length > 0) {
			InsertSQLBuilder sqlBuilder = new InsertSQLBuilder(df_risk);

			InsertSQLBuilder sqlBuilder2 = new InsertSQLBuilder(df_process_risk);
			int processriskID = 0;
			for (int i = 0; i < vos.length; i++) {
				sqlBuilder.putFieldValue("id", vos[i].getStringValue("id"));
				sqlBuilder.putFieldValue("riskcode", vos[i].getStringValue("riskcode"));
				sqlBuilder.putFieldValue("riskname", vos[i].getStringValue("riskname"));
				sqlBuilder.putFieldValue("riskdesc", vos[i].getStringValue("riskdesc"));
				sqlBuilder.putFieldValue("delflag", "0");
				sqList.add(sqlBuilder.getSQL());// ���յ����м�¼

				// ��������չ���
				String activityid = vos[i].getStringValue("wfactivity_id", "");
				String processid = vos[i].getStringValue("wfprocess_id", "");
				if (activityid.equals("")) {// �жϷ����Ƿ����ڻ��ڣ���������������/���ڵĹ�ϵ
					if (!processid.equals("")) {
						processriskID++;
						riskprocessmap.put(vos[i].getStringValue("id"), processid);// ��¼���������ĸ�����
						sqlBuilder2.putFieldValue("id", processriskID);
						sqlBuilder2.putFieldValue("processid", processid);
						sqlBuilder2.putFieldValue("riskid", vos[i].getStringValue("id"));
						sqList.add(sqlBuilder2.getSQL());
					}
				} else {
					processriskID++;
					sqlBuilder2.putFieldValue("id", processriskID);
					sqlBuilder2.putFieldValue("riskid", vos[i].getStringValue("id"));
					String realprocessid = activityid;
					if (activitymap.containsKey(activityid)) {
						realprocessid = (String) activitymap.get(activityid);
					}
					sqlBuilder2.putFieldValue("processid", realprocessid);// ��¼���������ĸ�����
					riskprocessmap.put(vos[i].getStringValue("id"), realprocessid);// ��¼���������ĸ�����
					sqList.add(sqlBuilder2.getSQL());
				}
			}
		}
		return sqList;
	}

	/**
	 * ���ƴ�ʩ����������ƴ�ʩ����
	 */
	public ArrayList importCtrl() throws Exception {
		ArrayList sqList = new ArrayList();
		// �ȴ������п��ƴ�ʩ��id����
		HashVO[] vos = commDMO.getHashVoArrayByDS(null, "select id,ctrltarget conname,ctrlfn3 condesc from cmp_risk where ctrltarget is not null or ctrlfn3 is not null");
		if (vos != null && vos.length > 0) {
			InsertSQLBuilder sqlBuilder = new InsertSQLBuilder(df_ctrl);
			InsertSQLBuilder sqlBuilder2 = new InsertSQLBuilder(df_risk_ctrl);
			String[] keys = vos[0].getKeys();
			int riskctrlID = 0;
			for (int i = 0; i < vos.length; i++) {
				if ((vos[i].getStringValue("conname") == null || vos[i].getStringValue("conname").equals("")) && (vos[i].getStringValue("condesc") == null || vos[i].getStringValue("condesc").equals(""))) {
					continue;
				}
				for (int j = 0; j < keys.length; j++) {
					sqlBuilder.putFieldValue(keys[j], vos[i].getStringValue(keys[j]));
				}
				sqlBuilder.putFieldValue("version", "1.0.0");
				sqlBuilder.putFieldValue("delflag", "0");
				sqList.add(sqlBuilder.getSQL());

				String processid = "";
				String riskid = vos[i].getStringValue("id");
				if (riskprocessmap.containsKey(riskid)) {
					processid = (String) riskprocessmap.get(riskid);
				}
				riskctrlID++;
				sqlBuilder2.putFieldValue("id", riskctrlID);
				sqlBuilder2.putFieldValue("riskid", riskid);
				sqlBuilder2.putFieldValue("conid", riskid);// ���ｫ���ձ���Ϊ���պͿ��ƣ���������¼idһ����
				sqlBuilder2.putFieldValue("proid", processid);

				sqList.add(sqlBuilder2.getSQL());
			}
		}
		return sqList;
	}

	/*************************** ��Ӫ������ ************************/

	/**
	 * �������
	 */
	public ArrayList importLawtype() throws Exception {
		ArrayList sqList = new ArrayList();
		String sql = "select id ,name from pub_comboboxdict where type='��������'";
		HashVO[] hashVOs = commDMO.getHashVoArrayByDS(null, sql);
		if (hashVOs != null) {
			InsertSQLBuilder builder = new InsertSQLBuilder(df_lawtype);
			for (int i = 0; i < hashVOs.length; i++) {
				builder.putFieldValue("id", i);
				builder.putFieldValue("delflag", "0");
				builder.putFieldValue("name", hashVOs[i].getStringValue("name"));
				lawtype.put(hashVOs[i].getStringValue("name"), i);
				sqList.add(builder.getSQL());
			}
		}
		return sqList;
	}

	/**
	 * ����
	 */

	public ArrayList importLaw() throws Exception {
		ArrayList sqList = new ArrayList();
		String maxids = "select max(id) id from law_law union all select max(id) id from law_law_item order by id desc";
		String maxlawid = commDMO.getStringValueByDS(null, maxids);
		int maxid = Integer.parseInt(maxlawid);
		// ����������ȫ�����뵽�����������ݿ���
		String lawsql = "select id ,lawname title,state status,remark descrp,lawtype lawtypeid,dispatch_code filenum,issuecorp publishorg,issue_date publishdate from law_law";
		HashVO[] hashVOs = commDMO.getHashVoArrayByDS(null, lawsql);
		if (hashVOs != null && hashVOs.length > 0) {
			String[] keys = hashVOs[0].getKeys();
			for (int i = 0; i < hashVOs.length; i++) {
				InsertSQLBuilder builder = new InsertSQLBuilder(df_law);
				for (int j = 0; j < keys.length; j++) {
					if (keys[j].equals("lawtypeid")) {
						builder.putFieldValue(keys[j], lawtype.get(hashVOs[i].getStringValue(keys[j])).toString());
					} else if (keys[j].equals("publishorg")) {
						if (postmap.get(hashVOs[i].getStringValue(keys[j])) != null) {
							builder.putFieldValue(keys[j], postmap.get(hashVOs[i].getStringValue(keys[j])).toString());
						} else {
							builder.putFieldValue(keys[j], hashVOs[i].getStringValue(keys[j]));
						}
					} else if (keys[j].equals("publishdate")) {
						String date = hashVOs[i].getStringValue(keys[j]);// ���ǿ���ʱ�䶼���ַ�������ʽΪ2013-05-10�����ͻ����ݿ���ʱ�����ʱ�����ͣ���ʽΪ2013-5-10����������Ҫ����һ��
						if (date == null || date.equals("") || date.length() != 10) {
							builder.putFieldValue(keys[j], date);
						} else if ("0".equals(date.substring(5, 6))) {
							date = date.substring(0, 5) + date.substring(6, 10);
							if ("0".equals(date.substring(7, 8))) {// �Ѿ������2013-5-10�ĸ�ʽ���ʳ���Ҳ����
								date = date.substring(0, 7) + date.substring(8, 9);
							}
						} else if ("0".equals(date.substring(8, 9))) {
							date = date.substring(0, 8) + date.substring(9, 10);
						}
						builder.putFieldValue(keys[j], date);
					} else {
						builder.putFieldValue(keys[j], hashVOs[i].getStringValue(keys[j]));
					}
				}
				builder.putFieldValue("lawlevel", "1");
				builder.putFieldValue("delflag", "0");
				if (sqList.size() > 0 && sqList.size() % 1000 == 0) {
					commDMO.executeBatchByDSImmediately(this.datasource, sqList);
					sqList.clear();
				}
				sqList.add(builder.getSQL());
			}
		}

		// �����ӱ��е����ݣ��ӱ�id�������еĳ�ͻ��
		String lawsql0 = "select id,itemtitle title,itemcontent content,parentid ,lawid ,seq orderseq from law_law_item where id in(select id from law_law)";
		HashVO[] itemhashVO = commDMO.getHashVoArrayByDS(null, lawsql0);
		if (itemhashVO != null) {
			String[] itemkeys = itemhashVO[0].getKeys();
			for (int i = 0; i < itemhashVO.length; i++) {
				maxid++;
				InsertSQLBuilder builder01 = new InsertSQLBuilder(df_law);
				lawitemmap.put(itemhashVO[i].getStringValue("id"), maxid);
				for (int j = 0; j < itemkeys.length; j++) {
					if (itemkeys[j].equals("id")) {
						builder01.putFieldValue(itemkeys[j], maxid);
					} else if (itemkeys[j].equals("parentid")) {
						if (lawitemmap.get(itemkeys[j]) != null && !lawitemmap.get(itemkeys[j]).equals("")) {
							builder01.putFieldValue(itemkeys[j], lawitemmap.get(itemhashVO[i].getStringValue(itemkeys[j])).toString());
						} else if (itemhashVO[i].getStringValue(itemkeys[j]) != null && !itemhashVO[i].getStringValue(itemkeys[j]).equals("")) {
							builder01.putFieldValue(itemkeys[j], itemhashVO[i].getStringValue(itemkeys[j]));
						} else {
							builder01.putFieldValue(itemkeys[j], itemhashVO[i].getStringValue("lawid"));
						}
					} else if (!itemkeys[j].equals("lawid")) {
						builder01.putFieldValue(itemkeys[j], itemhashVO[i].getStringValue(itemkeys[j]));
					}
				}
				builder01.putFieldValue("lawlevel", "2");
				builder01.putFieldValue("delflag", "0");
				if (sqList.size() > 0 && sqList.size() % 1000 == 0) {
					commDMO.executeBatchByDSImmediately(this.datasource, sqList);
					sqList.clear();
				}
				sqList.add(builder01.getSQL());
			}
		}

		// �����ӱ��е����ݣ��ӱ�id���������еĳ�ͻ��
		String lawsql1 = "select id,itemtitle title,itemcontent content,parentid ,lawid from law_law_item where id not in(select id from law_law)";
		HashVO[] itemhashVO1 = commDMO.getHashVoArrayByDS(null, lawsql1);
		if (itemhashVO1 != null) {
			String[] itemkeys = itemhashVO1[0].getKeys();
			for (int i = 0; i < itemhashVO1.length; i++) {
				InsertSQLBuilder builder = new InsertSQLBuilder(df_law);
				for (int j = 0; j < itemkeys.length; j++) {
					if (itemkeys[j].equals("parentid")) {
						if (lawitemmap.get(itemkeys[j]) != null && !lawitemmap.get(itemkeys[j]).equals("")) {
							builder.putFieldValue(itemkeys[j], lawitemmap.get(itemhashVO1[i].getStringValue(itemkeys[j])).toString());
						} else if (itemhashVO1[i].getStringValue(itemkeys[j]) != null && !itemhashVO1[i].getStringValue(itemkeys[j]).equals("")) {
							builder.putFieldValue(itemkeys[j], itemhashVO1[i].getStringValue(itemkeys[j]));
						} else {
							builder.putFieldValue(itemkeys[j], itemhashVO1[i].getStringValue("lawid"));
						}
					} else if (!itemkeys[j].equals("lawid")) {
						builder.putFieldValue(itemkeys[j], itemhashVO1[i].getStringValue(itemkeys[j]));
					}
				}
				builder.putFieldValue("lawlevel", "2");
				builder.putFieldValue("delflag", "0");
				if (sqList.size() > 0 && sqList.size() % 1000 == 0) {
					commDMO.executeBatchByDSImmediately(this.datasource, sqList);
					sqList.clear();
				}
				sqList.add(builder.getSQL());
			}
		}
		return sqList;
	}

	/**
	 * ���̷������
	 */

	public ArrayList importProcessLaw() throws Exception {
		ArrayList sqList = new ArrayList();
		int idindex = 0;
		// ���̹����ķ���
		String sql1 = "select wfprocess_id processid,law_id lawid,lawitem_id from cmp_cmpfile_law where relationtype='����' ";
		HashVO[] hashVOs = commDMO.getHashVoArrayByDS(null, sql1);
		if (hashVOs != null) {
			for (int i = 0; i < hashVOs.length; i++) {
				idindex++;
				InsertSQLBuilder builder = new InsertSQLBuilder(df_process_law);
				builder.putFieldValue("id", idindex);
				builder.putFieldValue("processid", hashVOs[i].getStringValue("processid"));
				if (hashVOs[i].getStringValue("lawitem_id") == null || hashVOs[i].getStringValue("lawitem_id").equals("")) {
					builder.putFieldValue("lawid", hashVOs[i].getStringValue("lawid"));
				} else if (lawitemmap.containsKey(hashVOs[i].getStringValue("lawitem_id"))) {
					builder.putFieldValue("lawid", lawitemmap.get(hashVOs[i].getStringValue("lawitem_id")).toString());
				} else {
					builder.putFieldValue("lawid", hashVOs[i].getStringValue("lawitem_id"));
				}
				sqList.add(builder.getSQL());
			}
		}
		// ���ڹ����ķ���
		String sql2 = "select wfactivity_id processid,law_id lawid,lawitem_id from cmp_cmpfile_law where relationtype='����'";
		HashVO[] hashVOs2 = commDMO.getHashVoArrayByDS(null, sql2);
		if (hashVOs2 != null) {
			for (int i = 0; i < hashVOs2.length; i++) {
				idindex++;
				InsertSQLBuilder builder = new InsertSQLBuilder(df_process_law);
				builder.putFieldValue("id", idindex);
				if (processmap.containsKey(hashVOs2[i].getStringValue("processid"))) {
					builder.putFieldValue("processid", processmap.get(hashVOs2[i].getStringValue("processid")).toString());
				} else {
					builder.putFieldValue("processid", hashVOs2[i].getStringValue("processid"));
				}
				if (hashVOs2[i].getStringValue("lawitem_id") == null || hashVOs2[i].getStringValue("lawitem_id").equals("")) {
					builder.putFieldValue("lawid", hashVOs2[i].getStringValue("lawid"));
				} else if (lawitemmap.containsKey(hashVOs2[i].getStringValue("lawitem_id"))) {
					builder.putFieldValue("lawid", lawitemmap.get(hashVOs2[i].getStringValue("lawitem_id")).toString());
				} else {
					builder.putFieldValue("lawid", hashVOs2[i].getStringValue("lawitem_id"));
				}
				sqList.add(builder.getSQL());
			}
		}

		return sqList;
	}

	/**
	 * ���̷���
	 */
	public ArrayList importProcessGroup() throws Exception {
		ArrayList sqList = new ArrayList();
		String sql = "select id ,name from bsd_bsact";
		HashVO[] hashVOs = commDMO.getHashVoArrayByDS(null, sql);
		if (hashVOs != null) {
			for (int i = 0; i < hashVOs.length; i++) {
				InsertSQLBuilder builder = new InsertSQLBuilder(df_processgroup);
				builder.putFieldValue("id", hashVOs[i].getStringValue("id"));
				builder.putFieldValue("delflag", "0");
				builder.putFieldValue("pgname", hashVOs[i].getStringValue("name"));
				sqList.add(builder.getSQL());
			}
		}
		return sqList;
	}
}
