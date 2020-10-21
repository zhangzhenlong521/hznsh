package cn.com.pushworld.wn.bs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.RemoteCallServlet;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.common.HashVO;

import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.to.mdata.jepfunctions.GetDateDifference;
import cn.com.infostrategy.to.mdata.jepfunctions.GetStringItemVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.pushworld.wn.to.WnUtils;
import cn.com.pushworld.wn.ui.WnSalaryServiceIfc;
import org.apache.log4j.Logger;


//import org.springframework.web.servlet.mvc.LastModified;

import freemarker.template.SimpleDate;

public class WnSalaryServiceImpl implements WnSalaryServiceIfc {
	private CommDMO dmo = new CommDMO();
	private ImportDataDMO importDmo = new ImportDataDMO();
	private Logger logger = WLTLogger.getLogger(WnSalaryServiceImpl.class);

	/**
	 * zzl[2019-3-28] �ݲ�ʹ�� ��Ա����������������
	 */
	public String getSqlInsert(String time, int num) {
		System.out.println(time + "��ǰʱ��");
		String str = null;
		InsertSQLBuilder insert = new InsertSQLBuilder("wn_gypf_table");
		List list = new ArrayList<String>();
		String[][] date = getTowWeiDate();
		try {
			HashVO[] vos = dmo
					.getHashVoArrayByDS(null,
							"select * from V_PUB_USER_POST_1 where POSTNAME like '%��Ա%'");
			String d = new SimpleDateFormat("yyyy.MM.dd").format(new Date());
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) + 6);
			Date t = cal.getTime();
			String day7 = new SimpleDateFormat("yyyy.MM.dd").format(t);
			String timezone = d + "~" + day7;
			for (int i = 0; i < vos.length; i++) {
				for (int j = 0; j < date.length; j++) {
					String id = dmo.getSequenceNextValByDS(null,
							"WN_GYPF_TABLE");
					insert.putFieldValue("id", id);
					insert.putFieldValue("username",
							vos[i].getStringValue("USERNAME"));
					insert.putFieldValue("usercode",
							vos[i].getStringValue("usercode"));
					insert.putFieldValue("userdept",
							vos[i].getStringValue("deptid"));
					insert.putFieldValue("xiangmu", date[j][0]);
					insert.putFieldValue("zhibiao", date[j][1]);
					insert.putFieldValue("fenzhi", date[j][2]);
					insert.putFieldValue("khsm", date[j][4]);
					insert.putFieldValue("pftime", time);
					insert.putFieldValue("state", "������");
					insert.putFieldValue("seq", j + 1);
					insert.putFieldValue("timezone", timezone);
					list.add(insert.getSQL());
				}
			}
			dmo.executeBatchByDS(null, list);
			str = "��Ա���������������ֿ�ʼ�ɹ�";
		} catch (Exception e) {
			str = "��Ա���������������ֿ�ʼʧ��";
			e.printStackTrace();
		}
		return str;
	}

	@Override
	public String getHfWg() {
		String str1="";
		try {
			String [][] count=dmo.getStringArrayByDS(null,"select count(*),floor(count(*)/10000) from s_loan_khxx_202001 where J is null and K is null and H like'%��%' and H like'%��%' and H like'%��%'");
			int zj=Integer.parseInt(count[0][0]);
			int xj=Integer.parseInt(count[0][1]);
			int a=1;
			List list=new ArrayList();
			UpdateSQLBuilder update=new UpdateSQLBuilder("s_loan_khxx_202001");
			for(int i=0;i<xj+1;i++){
				HashVO [] vos=null;
				if(i==xj){
					vos=dmo.getHashVoArrayByDS(null,"SELECT G,H  FROM (SELECT ROWNUM AS rowno, t.* FROM s_loan_khxx_202001 t WHERE ROWNUM <= '"+zj+"' and J is null and K is null and H like'%��%' and H like'%��%' and H like'%��%') table_alias WHERE table_alias.rowno >='"+i*10000+"'");
				}else{
					vos=dmo.getHashVoArrayByDS(null,"SELECT G,H  FROM (SELECT ROWNUM AS rowno, t.* FROM s_loan_khxx_202001 t WHERE ROWNUM <= '"+a*10000+"' and J is null and K is null and H like'%��%' and H like'%��%' and H like'%��%') table_alias WHERE table_alias.rowno >='"+i*10000+"'");

				}
				for(int j=0;j<vos.length;j++){
					String str=vos[j].getStringValue("H");
					String strx="";
					String strc="";
//					if(str.contains("��") && str.contains("��") && str.contains("��")){
//						strx=str.substring(str.indexOf("��")+1,str.indexOf("��")+1);
//						strc=str.substring(str.indexOf("��")+1,str.indexOf("��")+1);
//					}else if(str.contains("��") && str.contains("��")){
//						strx=str.substring(str.indexOf("��")+1,str.indexOf("��")+1);
//						strc=str.substring(str.indexOf("��")+1,str.indexOf("��")+1);
//					}
//					if(str.contains("��") && str.contains("��") && str.contains("·")) {
//						strx = str.substring(str.indexOf("��") + 1, str.indexOf("��") + 1);
//						strc = str.substring(str.indexOf("��") + 1, str.indexOf("·") + 1);
//					}else if(str.contains("��") && str.contains("��") && str.contains("��")){
//						strx = str.substring(str.indexOf("��") + 1, str.indexOf("��") + 1);
//						strc = str.substring(str.indexOf("��") + 1, str.indexOf("��") + 1);
//					}else
						if(str.contains("��") && str.contains("��") && str.contains("��")){
						strx = str.substring(str.indexOf("��") + 1, str.indexOf("��") + 1);
						strc = str.substring(str.indexOf("��") + 1, str.indexOf("��") + 1);
					}
					if(!strx.equals("") || !strc.equals("")){
						update.setWhereCondition("G='"+vos[j].getStringValue("G")+"'");
						update.putFieldValue("J",strx);
						update.putFieldValue("K",strc);
						list.add(update.getSQL());
					}
				}
				if(list.size()>1000){
					dmo.executeBatchByDS(null,list);
					list.clear();
				}else{
					dmo.executeBatchByDS(null,list);
					list.clear();
				}
				a++;
			}
			str1="����ɹ�";
		} catch (Exception e) {
			str1="����ʧ��";
			e.printStackTrace();
		}

		return str1;
	}

	// zpy
	@Override
	public String getSqlInsert(String time) {
		System.out.println(time + "��ǰʱ��");
		String str = null;
		InsertSQLBuilder insert = new InsertSQLBuilder("wn_gypf_table");
		List list = new ArrayList<String>();
		String[][] date = getTowWeiDate();
		try {
			String unCheckCode = "SELECT CODE FROM V_SAL_PERSONINFO WHERE ISUNCHECK='Y'";
			HashVO[] vos = dmo
					.getHashVoArrayByDS(
							null,
							"select * from V_PUB_USER_POST_1 where POSTNAME like '%��Ա%' and usercode  not in ("
									+ unCheckCode + ")");
			String d = new SimpleDateFormat("yyyy.MM.dd").format(new Date());
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) + 6);
			Date t = cal.getTime();
			String day7 = new SimpleDateFormat("yyyy.MM.dd").format(t);
			String timezone = d + "~" + day7;
			for (int i = 0; i < vos.length; i++) {
				for (int j = 0; j < date.length; j++) {
					String id = dmo.getSequenceNextValByDS(null,
							"WN_GYPF_TABLE");
					insert.putFieldValue("id", id);
					insert.putFieldValue("username",
							vos[i].getStringValue("USERNAME"));
					insert.putFieldValue("usercode",
							vos[i].getStringValue("usercode"));
					insert.putFieldValue("userdept",
							vos[i].getStringValue("deptid"));
					insert.putFieldValue("xiangmu", date[j][0]);
					insert.putFieldValue("zhibiao", date[j][1]);
					insert.putFieldValue("fenzhi", date[j][2]);
					insert.putFieldValue("khsm", date[j][4]);
					insert.putFieldValue("pftime", time);
					insert.putFieldValue("state", "������");
					insert.putFieldValue("seq", j + 1);
					insert.putFieldValue("timezone", timezone);
					insert.putFieldValue("FHRESULT", "δ����");// ������״ֱ̬�ӷ��뵽���ɱ�Ĺ�����
					if ("�ܷ�".equals(date[j][0])) {
						insert.putFieldValue("KOUOFEN", "100");
					} else {
						insert.putFieldValue("KOUOFEN", "0");
					}
					list.add(insert.getSQL());
				}
			}
			dmo.executeBatchByDS(null, list);
			str = "��Ա���������������ֿ�ʼ�ɹ�";
		} catch (Exception e) {
			str = "��Ա���������������ֿ�ʼʧ��";
			e.printStackTrace();
		}
		return str;
	}

	public String[][] getTowWeiDate() {
		String[][] date = new String[][] {
				{ "ְҵ����", "��װ", "5", "", "��Ҫ����װ������ͳһ���淶������" },
				{ "ְҵ����", "����", "5", "", "վ�ˡ����ˡ����˶�ׯ��" },
				{ "Ӫҵ׼��", "��ʱ", "3", "", "׼������Ҫ��Ӫҵǰ���" },
				{
						"����ڷŹ�λ����",
						"����",
						"3",
						"",
						"�����ֻ����⣬��Ʒ�ڷű��������淶�����򣬲���������ã�����ƾ֤��ӡ�¡�ӡ�ࡢӡ���ᡢ��������ˮ�������Ŵ��������ֽ�β�䡢�ǼǱ���˽����Ʒ�Ȳ��÷����������ϣ�����ҵ����Ҫʹ�õģ�ʹ����Ϻ�������ڹ����»�����У�δ��λ�ڷš����ҵȲ��÷�" },
				{
						"����ڷŹ�λ����",
						"����",
						"3",
						"",
						"�����ֻ����⣬��Ʒ�ڷű��������淶�����򣬲���������ã�����ƾ֤��ӡ�¡�ӡ�ࡢӡ���ᡢ��������ˮ�������Ŵ��������ֽ�β�䡢�ǼǱ���˽����Ʒ�Ȳ��÷����������ϣ�����ҵ����Ҫʹ�õģ�ʹ����Ϻ�������ڹ����»�����У�δ��λ�ڷš����ҵȲ��÷�" },
				{
						"����ڷŹ�λ����",
						"Ǯ��",
						"3",
						"",
						"�����ֻ����⣬��Ʒ�ڷű��������淶�����򣬲���������ã�����ƾ֤��ӡ�¡�ӡ�ࡢӡ���ᡢ��������ˮ�������Ŵ��������ֽ�β�䡢�ǼǱ���˽����Ʒ�Ȳ��÷����������ϣ�����ҵ����Ҫʹ�õģ�ʹ����Ϻ�������ڹ����»�����У�δ��λ�ڷš����ҵȲ��÷�" },
				{
						"����ڷŹ�λ����",
						"����",
						"3",
						"",
						"�����ֻ����⣬��Ʒ�ڷű��������淶�����򣬲���������ã�����ƾ֤��ӡ�¡�ӡ�ࡢӡ���ᡢ��������ˮ�������Ŵ��������ֽ�β�䡢�ǼǱ���˽����Ʒ�Ȳ��÷����������ϣ�����ҵ����Ҫʹ�õģ�ʹ����Ϻ�������ڹ����»�����У�δ��λ�ڷš����ҵȲ��÷�" },
				{ "��������", "�㳮��", "3", "", "�л��ҡ����ա�ֽм�Ȳ��÷�" },
				{ "��������", "�����", "3", "", "�л��ҡ����ա�ֽм�Ȳ��÷�" },
				{ "��������", "����", "3", "", "�л��ҡ����ա�ֽм�Ȳ��÷�" },
				{ "��������", "����", "3", "", "�л��ҡ����ա�ֽм�Ȳ��÷�" },
				{ "��������", "����", "3", "", "�л��ҡ����ա�ֽм�Ȳ��÷�" },
				{ "����", "����", "2", "", "����" },
				{ "����", "����", "2", "", "��������" },
				{ "����", "����", "2", "", "����������" },
				{ "����", "΢Ц", "2", "", "��ʱ΢Ц" },
				{ "�ʺ�ӭ��", "����", "3", "", "����ʾ��" },
				{ "�ʺ�ӭ��", "�ʺ�", "3", "", "���ʡ����á�/�ճ���ν���������" },
				{ "�Ӵ�����", "����", "5", "",
						"����������ڣ�����ҵ��ִ��һ���Իظ����Է�����Χ��ҵ������������֪�������䵽��ش��ڰ���" },
				{ "�Ӵ�����", "����", "10", "",
						"�������ġ����ġ����ģ��Ծ�ʹ�á��롢���á�лл���Բ����ټ�����������Ҫ����ʲôҵ�����Եȡ������鿴�������������˶Ժ�������ǩ�֡�����������" },
				{ "�Ӵ�����", "����", "5", "", "˫�ֵݽӡ����ĳ���" },
				{ "�Ӵ�����", "����", "5", "",
						"��ʱ������ͻ������������۲�Ʒ���ݣ���������ҵ�񣬴������ۻ��ᣬ����ͻ�Э����ɷ�����������ۡ�" },
				{ "�ͱ����", "����", "3", "", "����������Ҫ��������ҵ����/�����ߣ��ټ�" },
				{ "�������", "1.��ҵ���ڼ䲻������̸", "1", "", "��Щ����ڷ���ʱ���÷�" },
				{ "�������", "2.ָ���������Ƴ���", "1", "", "��Щ����ڷ���ʱ���÷�" },
				{ "�������", "3.���ҵ��ʱδ��˿�����ʾ���Ǹ��", "1", "", "��Щ����ڷ���ʱ���÷�" },
				{ "�������", "4.�ֻ�δ����", "1", "", "��Щ����ڷ���ʱ���÷�" },
				{ "�������", "5.�����λʱ����δ��λ", "1", "", "��Щ����ڷ���ʱ���÷�" },
				{ "�������", "6.����Ҫ��֫�嶯������", "1", "", "��Щ����ڷ���ʱ���÷�" },
				{ "�������", "7.��ͣҵ��ʱδ����ʾ����", "1", "", "��Щ����ڷ���ʱ���÷�" },
				{ "�������", "8.��ʱ��ҵ��δ���������ͻ�����", "1", "", "��Щ����ڷ���ʱ���÷�" },
				{ "�������", "9.��������Ͷ�����", "5", "", "��Щ����ڷ���ʱ���÷�" },
				{ "Ӫҵ����", "��������", "5", "", "��Щ����ڷ���ʱ���÷�" },
				{ "�ܷ�", "", "", "", "" } };
		return date;
	}

	/**
	 * ���ſ��˼ƻ����� planid:�ƻ�id
	 */
	@Override
	public String getBMsql(String planid) {
		String str = null;
		try {
			InsertSQLBuilder insert = new InsertSQLBuilder("wn_BMPF_table");
			HashVO[] vos = dmo
					.getHashVoArrayByDS(
							null,
							"select * from sal_target_list where type='���Ŷ���ָ��' AND (name LIKE '�����ͻ�����%' OR name LIKE '��������%' OR name LIKE '��������%' OR name LIKE '��ȫ����%')");
			HashMap deptMap = getdeptName();
			InsertSQLBuilder insertSQLBuilder = new InsertSQLBuilder(
					"wn_BMPF_table");
			List<String> list = new ArrayList<String>();
			List<String> wmsumList = new ArrayList<String>();
			List<String> djsumList = new ArrayList<String>();
			List<String> nksumList = new ArrayList<String>();
			List<String> aqsumList = new ArrayList<String>();
			// ������ʱ������޸�
			String[] date = dmo.getStringArrayFirstColByDS(null,
					"select PFTIME from WN_BMPF_TABLE where state='���ֽ���'");
			String pftime = "";
			if (date == null || date.length == 0) {
				int month = Integer.parseInt(new SimpleDateFormat("MM")
						.format(new Date()));
				int currentQuarter = getQuarter2(month);// ���ݵ�ǰʱ���ȡ����ǰ����
				pftime = new SimpleDateFormat("yyyy").format(new Date()) + "-"
						+ getQuarterEnd(currentQuarter);// ��ȡ����ǰ�������һ��
			} else {
				String maxTime = dmo
						.getStringValueByDS(null,
								"SELECT max(PFTIME) PFTINE FROM WN_BMPF_TABLE WHERE STATE='���ֽ���'");
				int year = Integer.parseInt(new SimpleDateFormat("yyyy")
						.format(new Date()));// ��ȡ����ǰ��
				int nextQuarter = getQuarter(maxTime) + 1;
				if (nextQuarter >= 5) {
					nextQuarter = 1;
					year = year + 1;
				}
				pftime = String.valueOf(year) + "-"
						+ getQuarterEnd(nextQuarter);
			}
			for (int i = 0; i < vos.length; i++) {// ÿһ��ָ��
				String deptid = vos[i].getStringValue("checkeddept");// ��ȡ�������˲��ŵĻ�����
				deptid = deptid.replaceAll(";", ",").substring(1,
						deptid.length() - 1);
				String[] deptcodes = deptid.split(",");
				String xiangmu = vos[i].getStringValue("name");// ��Ŀ����
				String evalstandard = vos[i].getStringValue("evalstandard");// ��Ŀ�۷�����
				String weights = vos[i].getStringValue("weights");// ��ĿȨ��
				String koufen = "0.0";// �۷����(Ĭ����0.0)
				String state = "������";// ��ǰ�������״̬:������
				// String pftime = new SimpleDateFormat("yyyy-MM-dd").format(new
				// Date());//��������
				// Ϊÿ����������ÿһ���
				for (int j = 0; j < deptcodes.length; j++) {
					if ("964".equals(deptcodes[j])
							|| "965".equals(deptcodes[j])) {
						continue;
					}
					// ÿһ���ָ�궼��Ϊÿһ�����˲�����һ�����˼ƻ�
					String deptName = deptMap.get(deptcodes[j]).toString();// ��ȡ����������
					insert.putFieldValue("PLANID", planid);
					insert.putFieldValue("deptcode", deptcodes[j]);
					insert.putFieldValue("deptname", deptName);
					insert.putFieldValue("xiangmu", xiangmu);
					insert.putFieldValue("evalstandard", evalstandard);
					insert.putFieldValue("fenzhi", weights);
					insert.putFieldValue("koufen", koufen);
					insert.putFieldValue("state", state);
					insert.putFieldValue("pftime", pftime);
					insert.putFieldValue("id",
							dmo.getSequenceNextValByDS(null, "S_WN_BMPF_TABLE"));
					list.add(insert.getSQL());
					// Ϊÿһ�����ŵ�ÿһ�����������ܷ�
					String name = xiangmu.substring(0, xiangmu.indexOf("-"));
					if ("�����ͻ�����".equals(name)) {
						if (!wmsumList.contains(deptcodes[j])) {
							insert.putFieldValue("PLANID", planid);
							insert.putFieldValue("deptcode", deptcodes[j]);
							insert.putFieldValue("xiangmu", name);
							insert.putFieldValue("koufen", "100.0");
							insert.putFieldValue("state", state);
							insert.putFieldValue("pftime", pftime);
							insert.putFieldValue("fenzhi", "");
							insert.putFieldValue("evalstandard", "");
							insert.putFieldValue("id", dmo
									.getSequenceNextValByDS(null,
											"S_WN_BMPF_TABLE"));
							wmsumList.add(deptcodes[j]);
							list.add(insert.getSQL());
						}
					} else if ("��������".equals(name)) {
						if (!djsumList.contains(deptcodes[j])) {
							insert.putFieldValue("PLANID", planid);
							insert.putFieldValue("deptcode", deptcodes[j]);
							insert.putFieldValue("xiangmu", name);
							insert.putFieldValue("koufen", "100.0");
							insert.putFieldValue("fenzhi", "");
							insert.putFieldValue("state", state);
							insert.putFieldValue("pftime", pftime);
							insert.putFieldValue("evalstandard", "");
							insert.putFieldValue("id", dmo
									.getSequenceNextValByDS(null,
											"S_WN_BMPF_TABLE"));
							list.add(insert.getSQL());
							djsumList.add(deptcodes[j]);
						}
					} else if ("��ȫ����".equals(name)) {
						if (!aqsumList.contains(deptcodes[j])) {
							insert.putFieldValue("PLANID", planid);
							insert.putFieldValue("deptcode", deptcodes[j]);
							insert.putFieldValue("xiangmu", name);
							insert.putFieldValue("koufen", "100.0");
							insert.putFieldValue("state", state);
							insert.putFieldValue("pftime", pftime);
							insert.putFieldValue("fenzhi", "");
							insert.putFieldValue("evalstandard", "");
							insert.putFieldValue("id", dmo
									.getSequenceNextValByDS(null,
											"S_WN_BMPF_TABLE"));
							list.add(insert.getSQL());
							aqsumList.add(deptcodes[j]);
						}
					} else if ("��������".equals(name)) {
						if (!nksumList.contains(deptcodes[j])) {
							insert.putFieldValue("PLANID", planid);
							insert.putFieldValue("deptcode", deptcodes[j]);
							insert.putFieldValue("xiangmu", name);
							insert.putFieldValue("koufen", "100.0");
							insert.putFieldValue("state", state);
							insert.putFieldValue("fenzhi", "");
							insert.putFieldValue("pftime", pftime);
							insert.putFieldValue("evalstandard", "");
							insert.putFieldValue("id", dmo
									.getSequenceNextValByDS(null,
											"S_WN_BMPF_TABLE"));
							list.add(insert.getSQL());
							nksumList.add(deptcodes[j]);
						}
					}
				}
			}
			dmo.executeBatchByDS(null, list);
			list.clear();
			str = "���ſ��˼ƻ����ɳɹ�";
		} catch (Exception e) {
			e.printStackTrace();
			str = "���ſ��˼ƻ����ɳɹ�";
		}
		return str;
	}

	// ��ȡ�����ű��
	public HashMap getdeptName() {
		HashMap hash = null;
		try {
			hash = dmo.getHashMapBySQLByDS(null,
					"SELECT id,NAME FROM pub_corp_dept");
		} catch (Exception e) {
			hash = new HashMap();
			e.printStackTrace();
		}
		return hash;
	}

	@Override
	public String gradeBMScoreEnd() {// ������ּƻ� ���ܷ� ��״̬
		try {
			String[] csName = dmo
					.getStringArrayFirstColByDS(null,
							"SELECT DISTINCT(xiangmu) FROM WN_BMPF_TABLE WHERE fenzhi IS NULL");
			HashMap codeVos = dmo
					.getHashMapBySQLByDS(
							null,
							"SELECT DISTINCT(SUBSTR(name,0,INSTR(NAME,'-')-1)) name,CHECKEDDEPT FROM sal_target_list  WHERE TYPE ='���Ŷ���ָ��'");
			UpdateSQLBuilder update = new UpdateSQLBuilder("WN_BMPF_TABLE");
			UpdateSQLBuilder update2 = new UpdateSQLBuilder("WN_BMPF_TABLE");
			List<String> list = new ArrayList<String>();

			for (int i = 0; i < csName.length; i++) {// ����ÿһ�����ִ������ ���� ���� ��ȫ��
				String deptcodes = codeVos.get(csName[i]).toString();
				deptcodes = deptcodes.substring(1, deptcodes.lastIndexOf(";"));
				String[] codes = deptcodes.split(";");// ������
				for (int j = 0; j < codes.length; j++) {// ÿ������ÿ������ÿ��С��
														// �޸�״̬�������ܷ�
					if ("964".equals(codes[j]) || "965".equals(codes[j])) {
						continue;
					}
					String sql = "select * from WN_BMPF_TABLE where xiangmu like '"
							+ csName[i]
							+ "%' and deptcode='"
							+ codes[j]
							+ "' order by fenzhi";
					HashVO[] vos = dmo.getHashVoArrayByDS(null, sql);
					double result = 0.0;
					String KOUFEN = "";
					String FENZHI = "";
					for (int k = 0; k < vos.length; k++) {
						if (csName[i].equals(vos[k].getStringValue("xiangmu"))) {
							continue;
						}
						FENZHI = vos[i].getStringValue("fenzhi");
						KOUFEN = vos[i].getStringValue("koufen");
						if (KOUFEN == null || KOUFEN.isEmpty()
								|| "".equals(KOUFEN)) {
							KOUFEN = "0.0";
						}
						if (Double.parseDouble(KOUFEN) > Double
								.parseDouble(FENZHI)) {
							KOUFEN = FENZHI;
						}
						result = result + Double.parseDouble(KOUFEN);
						update.setWhereCondition("1=1 and deptcode='"
								+ codes[j] + "' and xiangmu like '" + csName[i]
								+ "%' and state='������'");
						update.putFieldValue("state", "���ֽ���");
						update.putFieldValue("KOUFEN", KOUFEN);
						list.add(update.getSQL());
					}
					update2.setWhereCondition("1=1 and deptcode='" + codes[j]
							+ "' and xiangmu='" + csName[i]
							+ "' and fenzhi is null");
					update2.putFieldValue("koufen", 100.0 - result);
					list.add(update2.getSQL());
					dmo.executeBatchByDS(null, list);
					list.clear();
				}
			}
			UpdateSQLBuilder update3 = new UpdateSQLBuilder("WN_BMPFPLAN");
			update3.setWhereCondition("1=1 and state='������'");
			update3.putFieldValue("state", "���ֽ���");
			dmo.executeUpdateByDS(null, update3.getSQL());
			return "��ǰ�ƻ������ɹ�";
		} catch (Exception e) {
			e.printStackTrace();
			return "��ǰ�ƻ�����ʧ��";
		}

	}

	public String getQuarterEnd(int num) {
		switch (num) {
		case 1:
			return "03-31";
		case 2:
			return "06-30";
		case 3:
			return "09-30";
		case 4:
			return "12-31";
		default:
			return "12-31";
		}
	}

	// date��ʽ:2019-01-01
	public int getQuarter(String date) {// ����date��ȡ����ǰ����
		date = date.substring(5);
		if ("03-31".equals(date)) {
			return 1;
		} else if ("06-30".equals(date)) {
			return 2;
		} else if ("09-30".equals(date)) {
			return 3;
		} else if ("12-31".equals(date)) {
			return 4;
		} else {// ����һ�����벻��������
			return 4;
		}

	}

	public int getQuarter2(int month) {
		switch (month) {
		case 1:
		case 2:
		case 3:
			return 1;
		case 4:
		case 5:
		case 6:
			return 2;
		case 7:
		case 8:
		case 9:
			return 3;
		case 10:
		case 11:
		case 12:
			return 4;
		default:
			return 4;
		}
	}

	/**
	 * ȫ����������
	 */
	@Override
	public String ImportAll() {// ȫ�����ݵ���
		return importDmo.ImportAll();
	}

	/**
	 * ����һ�������
	 */
	@Override
	public String ImportDay(String date) {

		return importDmo.ImportDay(date);
	}

	@Override
	public String ImportOne(String filePath) {
		return importDmo.importOne(filePath);
	}

	@Override
	/**
	 * zzl[2019-4-28]
	 * ÿ���µĿͻ������Ӧ�Ĵ�����Ҫ����������Ҫ�޸����µĻ�����
	 */
	public String getChange(String date1, String date2) {
		String xx = null;
		Date date = new Date();
		Calendar scal = Calendar.getInstance();// ʹ��Ĭ��ʱ�������Ի������һ��������
		scal.setTime(date);
		scal.add(Calendar.MONTH, -2);// ȡ��ǰ���ڵĺ�һ��.
		scal.set(Calendar.DAY_OF_MONTH, scal.getActualMaximum(Calendar.DATE));
		SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
		String smonth = df.format(scal.getTime());// ���µ�����
		Calendar cal = Calendar.getInstance();// ʹ��Ĭ��ʱ�������Ի������һ��������
		cal.setTime(date);
		cal.add(Calendar.MONTH, -1);// ȡ��ǰ���ڵĺ�һ��.
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
		String kmonth = df.format(cal.getTime());// ���˵���ĩ����
		StringBuffer sb = new StringBuffer();
		StringBuffer sqlsb = new StringBuffer();
		try {
			UpdateSQLBuilder update = new UpdateSQLBuilder("");
			List list = new ArrayList<String>();
			// �ͻ��������Ϣ��map
			HashMap<String, String> map = dmo.getHashMapBySQLByDS(null,
					"select xd_col1,xd_col2 from wnbank.s_loan_ryb");
			// �����µĿͻ�֤���Ϳͻ������map
			HashMap<String, String> kmap = dmo
					.getHashMapBySQLByDS(
							null,
							"select distinct(dk.xd_col1),dk.XD_COL81 from wnbank.s_loan_dk dk where to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ date1 + "'");
			// ���µĿͻ�֤���Ϳͻ������map
			HashMap<String, String> smap = dmo
					.getHashMapBySQLByDS(
							null,
							"select distinct(dk.xd_col1),dk.XD_COL81 from wnbank.s_loan_dk dk where to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ date2 + "'");
			int a = 0;
			for (String str : kmap.keySet()) {
				if (kmap.get(str).equals(smap.get(str))) {
					continue;
				} else {
					a = a + 1;
					update.setWhereCondition("to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
							+ date2 + "' and xd_col1='" + str + "'");
					update.putFieldValue("XD_COL81", kmap.get(str));
					sb.append(smonth + "�����Ϊ[" + str + "]�ͻ�����Ϊ["
							+ map.get(smap.get(str))
							+ "]�뿼���µĿͻ�������Ϣ���������޸Ŀͻ�����Ϊ["
							+ map.get(kmap.get(str)) + "]"
							+ System.getProperty("line.separator"));
					list.add(update.getSQL());
					if (a > 5000) {
						txtWriteFile(sb, "DK");
						sb.delete(0, sb.length());
						a = 0;
					}
				}
				if (list.size() > 5000) {// zzl 1000 һ�ύ
					dmo.executeBatchByDS(null, list);
					for (int i = 0; i < list.size(); i++) {
						sqlsb.append(list.get(i).toString()
								+ System.getProperty("line.separator"));
					}
					txtWriteFile(sqlsb, "DK_SQL");
					sqlsb.delete(0, sqlsb.length());
					list.clear();
				}
			}
			if (list.size() > 0) {
				dmo.executeBatchByDS(null, list);
				for (int i = 0; i < list.size(); i++) {
					sqlsb.append(list.get(i).toString()
							+ System.getProperty("line.separator"));
				}
				txtWriteFile(sqlsb, "DK_SQL");
				sqlsb.delete(0, sqlsb.length());
				list.clear();
			}
			if (sb.length() > 0) {
				txtWriteFile(sb, "DK");
			}
			xx = "�ͻ�������Ϣ����ɹ�";
		} catch (Exception e) {
			xx = "�ͻ�������Ϣ���ʧ��";
			e.printStackTrace();
		}
		return xx;
	}

	public static void main(String[] args) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		try {
			cal.setTime(format.parse("2019-07-11"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cal.add(Calendar.YEAR, -1);
		Date otherDate = cal.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
		System.out
				.println(">>>>>>.>" + dateFormat.format(otherDate) + "-12-31");
		// /* д��Txt�ļ� */
		// try{
		// Date date = new Date();
		// SimpleDateFormat dfMonth = new SimpleDateFormat("yyyyMM");
		// SimpleDateFormat dfDay = new SimpleDateFormat("yyyyMMdd");
		// String filePath = System.getProperty("WLTUPLOADFILEDIR");
		// filePath=filePath+"\\"+dfMonth+"\\"+dfDay;
		// File writepath = new
		// File("C:\\Users\\longlonggo521\\Desktop\\"+dfMonth.format(date).toString()+"\\"+dfDay.format(date).toString());
		// // ���·�������û����Ҫ����һ���µ�output��txt�ļ�
		// if(!writepath.exists()){
		// writepath.mkdirs();
		// }
		// File writename = new
		// File("C:\\Users\\longlonggo521\\Desktop\\"+dfMonth.format(date).toString()+"\\"+dfDay.format(date).toString()+"\\niubi.txt");
		// // ���·�������û����Ҫ����һ���µ�output��txt�ļ�
		// if(!writename.exists()){
		// writename.createNewFile(); // �������ļ�
		// }
		// BufferedWriter out = new BufferedWriter(new
		// FileWriter(writename,true));
		// out.write("�ú�ѧϰ"); // \r\n��Ϊ����
		// out.flush(); // �ѻ���������ѹ���ļ�
		// out.close(); // ���ǵùر��ļ�
		// }catch (Exception e){
		// e.printStackTrace();
		// }
	}

	/**
	 * zzl �ѵ�������־д��txt
	 */
	public void txtWriteFile(StringBuffer sb, String name) {
		try {
			Date date = new Date();
			SimpleDateFormat dfMonth = new SimpleDateFormat("yyyyMM");
			SimpleDateFormat dfDay = new SimpleDateFormat("yyyyMMdd");
			String filePath = ServerEnvironment.getProperty("WLTUPLOADFILEDIR");
			filePath = filePath + "\\ManagerLog\\"
					+ dfMonth.format(date).toString() + "\\"
					+ dfDay.format(date).toString();
			File writepath = new File(filePath); // ���·�������û����Ҫ����һ���µ�output��txt�ļ�
			if (!writepath.exists()) {
				writepath.mkdirs();
			}
			File writename = new File(filePath + "\\" + name + "_"
					+ dfDay.format(date).toString() + ".txt"); // ���·�������û����Ҫ����һ���µ�output��txt�ļ�
			if (!writename.exists()) {
				writename.createNewFile(); // �������ļ�
			}
			BufferedWriter out = new BufferedWriter(new FileWriter(writename,
					true));
			out.write(sb.toString()); // \r\n��Ϊ����
			out.flush(); // �ѻ���������ѹ���ļ�
			out.close(); // ���ǵùر��ļ�
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * zzl
	 * 
	 * @param date
	 * @return ��������ʱ��
	 */
	public String getSMonthDate(String date) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		try {
			cal.setTime(format.parse(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
		Date otherDate = cal.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(otherDate);
	}

	/**
	 * zzl
	 * 
	 * @param date
	 * @return ���������ĩʱ��
	 */
	public String getYearDate(String date) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		try {
			cal.setTime(format.parse(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cal.add(Calendar.YEAR, -1);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
		Date otherDate = cal.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
		return dateFormat.format(otherDate) + "-12-31";
	}

	/**
	 * zzl[���ͻ�������Ϣ���]
	 * 
	 */
	@Override
	public String getCKChange(String date1, String date2) {
		String xx = null;
		Date date = new Date();
		Calendar scal = Calendar.getInstance();// ʹ��Ĭ��ʱ�������Ի������һ��������
		scal.setTime(date);
		scal.add(Calendar.MONTH, -2);// ȡ��ǰ���ڵĺ�һ��.
		scal.set(Calendar.DAY_OF_MONTH, scal.getActualMaximum(Calendar.DATE));
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String smonth = df.format(scal.getTime());// ���µ�����
		Calendar cal = Calendar.getInstance();// ʹ��Ĭ��ʱ�������Ի������һ��������
		cal.setTime(date);
		cal.add(Calendar.MONTH, -1);// ȡ��ǰ���ڵĺ�һ��.
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
		String kmonth = df.format(cal.getTime());// �����µ�����
		StringBuffer sb = new StringBuffer();
		StringBuffer sqlsb = new StringBuffer();
		try {
			UpdateSQLBuilder update = new UpdateSQLBuilder("wnbank.s_loan_khxx");
			List list = new ArrayList<String>();
			// �ͻ��������Ϣ��map
			HashMap<String, String> map = dmo.getHashMapBySQLByDS(null,
					"select xd_col1,xd_col2 from wnbank.s_loan_ryb");
			// �����µĿͻ�֤���Ϳͻ������map
			HashMap<String, String> kmap = dmo
					.getHashMapBySQLByDS(
							null,
							"select XD_COL1,XD_COL96 from wnbank.S_LOAN_KHXX where to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ date1
									+ "' and XD_COL7 is not null and XD_COL96 is not null");
			// ���µĿͻ�֤���Ϳͻ������map
			HashMap<String, String> smap = dmo
					.getHashMapBySQLByDS(
							null,
							"select XD_COL1,XD_COL96 from wnbank.S_LOAN_KHXX where to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ date2
									+ "' and XD_COL7 is not null and XD_COL96 is not null");
			int a = 0;
			for (String str : kmap.keySet()) {
				if (kmap.get(str).equals(smap.get(str))) {
					continue;
				} else {
					a = a + 1;
					update.setWhereCondition("to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
							+ date2 + "' and xd_col1='" + str + "'");
					update.putFieldValue("XD_COL81", kmap.get(str));
					list.add(update.getSQL());
					sb.append("2018-12-31�ͻ���Ϊ[" + str + "]�ͻ�����Ϊ["
							+ map.get(smap.get(str))
							+ "]�뿼���µĿͻ�������Ϣ���������޸Ŀͻ�����Ϊ["
							+ map.get(kmap.get(str)) + "]  \n");
					if (a > 5000) {
						txtWriteFile(sb, "CK");
						sb.delete(0, sb.length());
						a = 0;
					}
				}
				if (list.size() > 5000) {// zzl 1000 һ�ύ
					dmo.executeBatchByDS(null, list);
					for (int i = 0; i < list.size(); i++) {
						sqlsb.append(list.get(i).toString()
								+ System.getProperty("line.separator"));
					}
					txtWriteFile(sqlsb, "CK_SQL");
					sqlsb.delete(0, sqlsb.length());
					list.clear();
				}
			}
			if (list.size() > 0) {
				dmo.executeBatchByDS(null, list);
				for (int i = 0; i < list.size(); i++) {
					sqlsb.append(list.get(i).toString()
							+ System.getProperty("line.separator"));
				}
				txtWriteFile(sqlsb, "CK_SQL");
				sqlsb.delete(0, sqlsb.length());
				list.clear();
			}
			if (sb.length() > 0) {
				txtWriteFile(sb, "CK");
			}
			xx = "�ͻ�������Ϣ����ɹ�";
		} catch (Exception e) {
			xx = "�ͻ�������Ϣ���ʧ��";
			e.printStackTrace();
		}
		return xx;
	}

	/**
	 * zzl ������Ϣ���
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public String getWgChange(String date1, String date2) {
		String xx = null;
		try {
			String dy = date1.replace("-", "").substring(0, 6);
			String sy = date2.replace("-", "").substring(0, 6);
			UpdateSQLBuilder update = new UpdateSQLBuilder(
					"wnsalarydb.s_loan_khxx_" + sy);
			List list = new ArrayList<String>();
//			String[][] str = dmo
//					.getStringArrayByDS(
//							null,
//							"select dy.b,dy.w,dy.x,dy.v from wnsalarydb.s_loan_khxx_"
//									+ dy
//									+ " dy left join  wnsalarydb.s_loan_khxx_"
//									+ sy
//									+ " sy on dy.i=sy.i where dy.w||dy.x!=sy.w||sy.x group by dy.b,dy.w,dy.x,dy.v");
			String[][] str = dmo
				     .getStringArrayByDS(
				       null,
				       "select dy.b,dy.w,dy.x,dy.v from wnsalarydb.s_loan_khxx_"
				         + dy
				         + " dy left join  wnsalarydb.s_loan_khxx_"
				         + sy
				         + " sy on dy.b=sy.b where dy.w||dy.x!=sy.w||sy.x  or  dy.v!=dy.v");
			for (int i = 0; i < str.length; i++) {
				update.setWhereCondition("B='" + str[i][0] + "'");
				update.putFieldValue("W", str[i][1]);
				update.putFieldValue("X", str[i][2]);
				update.putFieldValue("V", str[i][3]);
				list.add(update.getSQL());
				logger.info(">>>>>" + i + ">>>" + update.getSQL());
			}
			dmo.executeBatchByDS(null, list);
			xx = "������Ϣ����ɹ�";
		} catch (Exception e) {
			xx = "������Ϣ���ʧ��";
			e.printStackTrace();
		}
		return xx;
	}

	/**
	 * zpy[2019-05-22] Ϊÿ����Ա���ɶ��Կ��˼ƻ�
	 */
	@Override
	public String gradeDXscore(String id) {
		String result = "";
		String planName = "";
		try {
			/**
			 * ��2019-11-26�� Ӧ�ͻ�Ҫ�󣬽����в����뿼�˵��˲����ɴ�ֱ����ʾ
			 */
			String unCheckCode = "SELECT CODE FROM V_SAL_PERSONINFO WHERE ISUNCHECK='Y' ";
			// ��ȡ����Ա�����Ϣ
			HashVO[] vos = dmo
					.getHashVoArrayByDS(
							null,
							"select * from V_PUB_USER_POST_1 where POSTNAME like '%��Ա%'  and usercode not in ("
									+ unCheckCode + ")");
			// ��ȡ����Ա���Կ���ָ��
			HashVO[] dxzbVos = dmo
					.getHashVoArrayByDS(
							null,
							"select * from sal_person_check_list where 1=1  and (type='43')  and (1=1)  order by seq");
			List<String> _sqlList = new ArrayList<String>();
			String sql = "";
			String maxTime = dmo.getStringValueByDS(null,
					"select max(plantime) from WN_GYDXPLAN");
			planName = dmo.getStringValueByDS(null,
					"select planname from WN_GYDXPLAN where plantime='"
							+ maxTime + "'");
			String pftime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
					.format(new Date());
			for (int i = 0; i < vos.length; i++) {
				String gyName = vos[i].getStringValue("username");// ��ȡ����Ա����
				String gyDeptCode = vos[i].getStringValue("deptcode");// ��ȡ����ǰ�û��Ļ�����
				String gyUserCode = vos[i].getStringValue("usercode");// ��ȡ����ǰ��Ա�Ĺ�Ա��
				for (int j = 0; j < dxzbVos.length; j++) {
					String xiangmu = dxzbVos[j].getStringValue("CATALOG");// ������Ŀ
					String khsm = dxzbVos[j].getStringValue("name");// ����˵��
					String fenzhi = dxzbVos[j].getStringValue("weights");

					sql = "insert into wn_gydx_table(id,username,userdept,usercode,xiangmu,khsm,fenzhi,koufen,state,pftime)"
							+ "  values('"
							+ id
							+ "','"
							+ gyName
							+ "','"
							+ gyDeptCode
							+ "','"
							+ gyUserCode
							+ "','"
							+ xiangmu
							+ "','"
							+ khsm
							+ "','"
							+ fenzhi
							+ "','0.0','������','"
							+ pftime + "')";
					_sqlList.add(sql);
				}
				sql = "insert into wn_gydx_table(id,username,userdept,usercode,xiangmu,khsm,fenzhi,state,pftime) values('"
						+ id
						+ "','"
						+ gyName
						+ "','"
						+ gyDeptCode
						+ "','"
						+ gyUserCode
						+ "','�ܷ�','�ܷ�','0.0','������','"
						+ pftime
						+ "')";
				_sqlList.add(sql);
				if (_sqlList.size() == 5000) {
					dmo.executeBatchByDS(null, _sqlList);
					_sqlList.clear();
				}
			}
			dmo.executeBatchByDS(null, _sqlList);
			sql = "update WN_GYDXPLAN set state='������' where id='" + id + "'";
			dmo.executeUpdateByDS(null, sql);
			result = "��ǰ���˼ƻ���" + planName + "�������ɹ�";
		} catch (Exception e) {
			result = "��ǰ���˼ƻ���" + planName + "������ʧ��,����";
			e.printStackTrace();
		} finally {
			return result;
		}
	}

	/**
	 * �������Կ��˵ķ��������仯
	 */
	@Override
	public String gradeDXEnd(String planid) {// ������ǰ���˼ƻ�:1.�����ֵ;2.�޸�״̬;
		String result = "";
		try {
			// ��ȡ����ǰ���й�Ա
			HashVO[] vos = dmo
					.getHashVoArrayByDS(null,
							"select * from V_PUB_USER_POST_1 where POSTNAME like '%��Ա%'");
			// HashMap<String,String> gyMap = dmo.getHashMapBySQLByDS(null,
			// "select usercode,deptcode from V_PUB_USER_POST_1 where POSTNAME like '%��Ա%'");
			// ��Ҫ��ʾ�ͻ����Ƿ���Ҫ���н���
			HashVO[] hvos = dmo
					.getHashVoArrayByDS(
							null,
							"SELECT * FROM wn_gydx_table WHERE STATE='������' OR FHRESULT IS NULL OR FHRESULT='����δͨ��'");
			if (hvos.length > 0) {// ������δ��ɵ�����½���
				double koufen = 0.0;
				double fenzhi = 0.0;
				String xiangmu = "";
				String khsm = "";
				UpdateSQLBuilder update = new UpdateSQLBuilder("wn_gydx_table");
				UpdateSQLBuilder update2 = new UpdateSQLBuilder("wn_gydx_table");
				List<String> sqlList = new ArrayList<String>();
				for (int i = 0; i < vos.length; i++) {// ���չ�Ա���б���
					double sum = 100.0;
					String maxTime = dmo.getStringValueByDS(null,
							"select max(pftime) from wn_gydx_table where usercode='"
									+ vos[i].getStringValue("usercode") + "'");
					String sql = "select * from  wn_gydx_table where usercode='"
							+ vos[i].getStringValue("usercode")
							+ "' and (state='������' or fhresult is null  or fhresult='����δͨ��') and pftime='"
							+ maxTime + "'";
					HashVO[] gyVos = dmo.getHashVoArrayByDS(null, sql);
					if (gyVos.length <= 0) {
						continue;
					}

					for (int j = 0; j < gyVos.length; j++) {// ��Ա���Ե÷ֽ���
						xiangmu = gyVos[j].getStringValue("xiangmu");
						khsm = gyVos[j].getStringValue("khsm");
						if ("�ܷ�".equals(xiangmu)) {
							continue;
						}
						fenzhi = Double.parseDouble(gyVos[j]
								.getStringValue("fenzhi"));
						// if(gyVos[j].getStringValue("koufen")==null ||
						// gyVos[j].getStringValue("koufen").isEmpty()){
						// koufen=fenzhi;
						// }
						koufen = fenzhi;
						sum -= koufen;
						update.setWhereCondition("usercode='"
								+ vos[i].getStringValue("usercode")
								+ "' and khsm='"
								+ khsm
								+ "' and (state='������' or fhresult is null  or fhresult='����δͨ��') ");
						update.putFieldValue("koufen", koufen);
						update.putFieldValue("state", "���ֽ���");
						sqlList.add(update.getSQL());
					}
					update2.setWhereCondition("usercode='"
							+ vos[i].getStringValue("usercode")
							+ "' and khsm='�ܷ�' and (state='������' or fhresult is null  or fhresult='����δͨ��')");
					update2.putFieldValue("fenzhi", sum);
					update2.putFieldValue("koufen", "");
					update2.putFieldValue("state", "���ֽ���");
					sqlList.add(update2.getSQL());
					if (sqlList.size() >= 5000) {
						dmo.executeBatchByDS(null, sqlList);
						sqlList.clear();
					}
				}
				dmo.executeBatchByDS(null, sqlList);
				result = "�����ּƻ������ɹ�";
			} else {// ֱ�ӽ���
				result = "�����ּƻ������ɹ�";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return result;
		}
	}

	// �޸ĵ�ǰ�����˵�ǰ�����������(δ���ֵ�״̬��)
	public void updatePF(String usercode, String khsm, double fenzhi,
			String pfusercode) {
		try {
			String sql = "update  wn_gydx_table set pfusercode='" + pfusercode
					+ "',koufen='" + fenzhi + ",state='���ֽ���'' where usercode='"
					+ usercode + "' and state='������' and khsm='" + khsm + "'";
			dmo.executeUpdateByDS(null, sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// δ���˵�״̬���޸�����
	public void updateFH(String usercode, String khsm, double fenzhi,
			String fhusercode) {
		try {
			String sql = "update  wn_gydx_table set fhusercode='" + fhusercode
					+ "',koufen='" + fenzhi + ",state='���ֽ���'' where usercode='"
					+ usercode + "' and state='������' and khsm='" + khsm + "'";
			dmo.executeUpdateByDS(null, sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String gradeManagerDXscore(String id) {
		String result = "";
		try {
			// ��ȡ��������ʱ��
			SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
			SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
			int month = Integer.parseInt(monthFormat.format(new Date()));
			int year = Integer.parseInt(yearFormat.format(new Date()));
			String lastMonth = "";
			if (month == 1) {
				month = 12;
				year = year - 1;
			} else {
				month = month - 1;
			}
			String monthStr = String.valueOf(month);
			lastMonth = year + "-"
					+ (monthStr.length() == 1 ? "0" + monthStr : monthStr);
			// ��ȡ���ͻ��������Ϣ
			HashVO[] managerVos = dmo
					.getHashVoArrayByDS(null,
							"SELECT * FROM V_PUB_USER_POST_1 WHERE POSTNAME LIKE '%�ͻ�����%'");
			// ��ȡ���ͻ������Կ��˼ƻ�
			HashVO[] vos = dmo
					.getHashVoArrayByDS(
							null,
							"select * from sal_person_check_list where 1=1  and (type='61')  and (1=1)  order by seq");
			List<String> _sqlList = new ArrayList<String>();
			for (int i = 0; i < managerVos.length; i++) {
				String username = managerVos[i].getStringValue("username");
				String usercode = managerVos[i].getStringValue("usercode");
				String deptcode = managerVos[i].getStringValue("deptcode");
				InsertSQLBuilder insert = new InsertSQLBuilder(
						"wn_managerdx_table");
				for (int j = 0; j < vos.length; j++) {// ��ȡ��ÿһ��˼ƻ�ָ��
					String weight = vos[j].getStringValue("WEIGHTS");// ��ȡ��ָ��Ŀ���Ȩ��
					String khsm = vos[j].getStringValue("name");
					String xiangmu = vos[j].getStringValue("CATALOG");
					double koufen = 0.0;
					insert.putFieldValue("planid", id);
					insert.putFieldValue("username", username);
					insert.putFieldValue("usercode", usercode);
					insert.putFieldValue("userdept", deptcode);
					insert.putFieldValue("xiangmu", xiangmu);
					insert.putFieldValue("khsm", khsm);
					insert.putFieldValue("fenzhi", weight);
					insert.putFieldValue("koufen", koufen);
					insert.putFieldValue("state", "������");
					insert.putFieldValue("pftime", new SimpleDateFormat(
							"yyyy-MM-dd hh:mm:ss").format(new Date()));
					insert.putFieldValue("khMonth", lastMonth);
					_sqlList.add(insert.getSQL());
				}
				InsertSQLBuilder insert2 = new InsertSQLBuilder(
						"wn_managerdx_table");
				insert2.putFieldValue("planid", id);
				insert2.putFieldValue("username", username);
				insert2.putFieldValue("usercode", usercode);
				insert2.putFieldValue("userdept", deptcode);
				insert2.putFieldValue("fenzhi", 100.0);
				insert2.putFieldValue("state", "������");
				insert2.putFieldValue("xiangmu", "�ܷ�");
				insert2.putFieldValue("pftime", new SimpleDateFormat(
						"yyyy-MM-dd hh:mm:ss").format(new Date()));
				insert2.putFieldValue("khsm", "�ܷ�");
				insert2.putFieldValue("khMonth", lastMonth);
				_sqlList.add(insert2.getSQL());
			}
			dmo.executeBatchByDS(null, _sqlList);
			result = "��ǰ���˼ƻ����ɳɹ�";
		} catch (Exception e) {
			result = "��ǰ���˼ƻ�����ʧ��";
			e.printStackTrace();
		} finally {
			return result;
		}

	}

	/**
	 * �ͻ�����������˼ƻ�
	 */
	@Override
	public String endManagerDXscore(String id) {
		String result = "";
		try {
			// ��ѯ����δ�������ֵĿͻ�����
			HashVO[] vos = dmo.getHashVoArrayByDS(null,
					"select * from wn_managerdx_table where  state='������'");
			if (vos.length <= 0) {
				result = "��ǰ���˼ƻ������ɹ�";
			} else {// �ͻ�Ҫ�󣬵�����δ��ɶԿͻ�����Ŀ���ʱ�������Կͻ����������
				UpdateSQLBuilder update = new UpdateSQLBuilder(
						"wn_managerdx_table");
				List<String> _sqllist = new ArrayList<String>();
				Map<String, Double> map = new HashMap<String, Double>();
				for (int i = 0; i < vos.length; i++) {
					String xiangmu = vos[i].getStringValue("xiangmu");
					String fenzhi = vos[i].getStringValue("fenzhi");
					String khsm = vos[i].getStringValue("khsm");
					String usercode = vos[i].getStringValue("usercode");
					String koufen = vos[i].getStringValue("koufen");
					// if(koufen==null || "".isEmpty() ||
					// Double.parseDouble(fenzhi)<Double.parseDouble(koufen)){
					// koufen=fenzhi;
					// }
					if (koufen == null || koufen.isEmpty()) {
						koufen = "0.0";
					} else if (Double.parseDouble(fenzhi) < Double
							.parseDouble(koufen)) {
						koufen = fenzhi;
					}
					// ����ÿһλ�ͻ�������ܷ�
					if (!map.containsKey(usercode)) {
						map.put(usercode, Double.parseDouble(koufen));
					} else {
						map.put(usercode,
								map.get(usercode) + Double.parseDouble(koufen));
					}
					update.setWhereCondition("usercode='" + usercode
							+ "' and khsm='" + khsm + "' and state='������'");
					update.putFieldValue("koufen", koufen);
					update.putFieldValue("state", "���ֽ���");
					_sqllist.add(update.getSQL());
				}

				Set<String> userCodeSet = map.keySet();
				for (String usercode : userCodeSet) {
					String sql = "update wn_managerdx_table set fenzhi='"
							+ map.get(usercode)
							+ "' where state='������'  and usercode='" + usercode
							+ "' and xiangmu='�ܷ�'";
					_sqllist.add(sql);
				}
				dmo.executeBatchByDS(null, _sqllist);
				result = "��ǰ���˼ƻ������ɹ�";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return result;
		}

	}

	/**
	 * zzl ����ܻ�����
	 */
	@Override
	public String getDKFinishB(String date) {
		String jv = null;
		InsertSQLBuilder insert = new InsertSQLBuilder("wn_loans_wcb");
		List list = new ArrayList<String>();
		try {
			// �õ��ͻ������Map
			HashMap<String, String> userMap = dmo
					.getHashMapBySQLByDS(
							null,
							"select name,name from v_sal_personinfo where stationkind in('�����ͻ�����','����ͻ�����','�����μ�ְ�ͻ�����','�������㸱����','�������㸱����')");
			// �õ��ͻ������������
			HashMap<String, String> rwMap = dmo.getHashMapBySQLByDS(
					null,
					"select A,sum(D) from EXCEL_TAB_53 where year='"
							+ date.substring(0, 4) + "' group by A");
			if (rwMap.size() == 0) {
				return "��ǰʱ�䡾" + date + "��û���ϴ�������";
			}
			HashMap<String, String> nhMap = getDKNH(date);
			HashMap<String, String> nbzgMap = getDKNBZG(date);
			HashMap<String, String> ajMap = getDKAJ(date);
			HashMap<String, String> ybzrrMap = getDKYBZRR(date);
			HashMap<String, String> qyMap = getDKQY(date);
			for (String str : userMap.keySet()) {
				int count, nh, nbzg, aj, yb, qy = 0;
				if (nhMap.get(str) == null) {
					nh = 0;
				} else {
					nh = Integer.parseInt(nhMap.get(str));
				}
				if (nbzgMap.get(str) == null) {
					nbzg = 0;
				} else {
					nbzg = Integer.parseInt(nbzgMap.get(str));
				}
				if (ajMap.get(str) == null) {
					aj = 0;
				} else {
					aj = Integer.parseInt(ajMap.get(str));
				}
				if (ybzrrMap.get(str) == null) {
					yb = 0;
				} else {
					yb = Integer.parseInt(ybzrrMap.get(str));
				}
				if (qyMap.get(str) == null) {
					qy = 0;
				} else {
					qy = Integer.parseInt(qyMap.get(str));
				}
				count = nh + nbzg + aj + yb + qy;
				insert.putFieldValue("name", str);
				insert.putFieldValue("passed", count);
				insert.putFieldValue("task", rwMap.get(str));
				insert.putFieldValue("date_time", date);
				list.add(insert.getSQL());
			}
			dmo.executeBatchByDS(null, list);
			jv = "��ѯ���";
		} catch (Exception e) {
			jv = "��ѯʧ��";
			e.printStackTrace();
		}

		return jv;
	}

	/**
	 * zzl ����ܻ���-ũ��Map
	 * 
	 * @return
	 */
	public HashMap<String, String> getDKNH(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select rr.xd_col2 xd_col2,dgxj.countxj countxj from (select dg.xd_col81 xd_col81,count(dg.xd_col81) countxj from( select distinct(xd_col16) xd_col16,xd_col81 xd_col81 from wnbank.s_loan_dk dk where xd_col7<>0 and xd_col22<>'05' and xd_col2 not like '%��˾%' and xd_col166<>'81320101' and xd_col72 in('16','24','15','25','30','z01','38','45','46','47','48') and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ date
									+ "') dg group by dg.xd_col81) dgxj left join wnbank.s_loan_ryb rr on dgxj.xd_col81=rr.xd_col1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * zzl ����ܻ���-�ڲ�ְ��Map
	 * 
	 * @return
	 */
	public HashMap<String, String> getDKNBZG(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select rr.xd_col2 xd_col2,dgxj.countxj countxj from (select dg.xd_col81 xd_col81,count(dg.xd_col81) countxj from( select distinct(xd_col16) xd_col16,xd_col81 xd_col81 from wnbank.s_loan_dk dk where xd_col7<>0 and xd_col22<>'05' and xd_col2 not like '%��˾%' and xd_col166<>'81320101' and xd_col72 in('26','19') and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ date
									+ "') dg group by dg.xd_col81) dgxj left join wnbank.s_loan_ryb rr on dgxj.xd_col81=rr.xd_col1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * zzl ����ܻ���-���ҿͻ�Map
	 * 
	 * @return
	 */
	public HashMap<String, String> getDKAJ(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select rr.xd_col2 xd_col2,dgxj.countxj countxj from (select dg.xd_col81 xd_col81,count(dg.xd_col81) countxj from( select distinct(xd_col16) xd_col16,xd_col81 xd_col81 from wnbank.s_loan_dk dk where xd_col7<>0 and xd_col22<>'05' and xd_col2 not like '%��˾%' and xd_col166<>'81320101' and xd_col72 in('20','29','28','37','h01','h02') and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ date
									+ "') dg group by dg.xd_col81) dgxj left join wnbank.s_loan_ryb rr on dgxj.xd_col81=rr.xd_col1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * zzl ����ܻ���-һ����Ȼ��Map
	 * 
	 * @return
	 */
	public HashMap<String, String> getDKYBZRR(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select rr.xd_col2 xd_col2,dgxj.countxj countxj from (select dg.xd_col81 xd_col81,count(dg.xd_col81) countxj from( select distinct(xd_col16) xd_col16,xd_col81 xd_col81 from wnbank.s_loan_dk dk where xd_col7<>0 and xd_col22<>'05' and xd_col2 not like '%��˾%' and xd_col166<>'81320101' and xd_col72 not in ('20','29','28','37','h01','h02','26','19','16','24','15','25','30','z01','38','45','46','47','48') and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ date
									+ "') dg group by dg.xd_col81) dgxj left join wnbank.s_loan_ryb rr on dgxj.xd_col81=rr.xd_col1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * zzl ����ܻ���-��ҵMap
	 * 
	 * @return
	 */
	public HashMap<String, String> getDKQY(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select dg.cus_manager cus_manager,count(dg.cus_manager) countxj from (select distinct(xx.cert_code),dg.cus_manager  from wnbank.s_cmis_acc_loan dg left join wnbank.s_cmis_cus_base xx on dg.cus_id=xx.cus_id  where to_char(to_date(dg.load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ date
									+ "'and dg.cla<>'05' and account_status='1' and loan_balance<>'0')dg group by dg.cus_manager");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * ZPY ǭũE��ǩԼ��ɱȼ���
	 */
	@Override
	public String getQnedRate(String date_time) {
		try {
			date_time = date_time.replace("��", "-").replace("��", "");
			HashMap<String, String> rwMap = dmo.getHashMapBySQLByDS(null,
					"select A,sum(P) from EXCEL_TAB_53 where year='"
							+ date_time.substring(0, 4) + "' group by A");
			if (rwMap.size() <= 0) {
				return "���¡�" + date_time + "����û������,�������ѯʱ��";
			}
			// ǭũE���������
			InsertSQLBuilder insert = new InsertSQLBuilder("wn_qned_result");
			HashMap<String, String> userMap = dmo
					.getHashMapBySQLByDS(
							null,
							"select name,deptname from v_sal_personinfo where stationkind in('�����ͻ�����','����ͻ�����','�����μ�ְ�ͻ�����','�������㸱����','�������㸱����')");
			HashMap<String, String> jlMap = dmo
					.getHashMapBySQLByDS(
							null,
							"select aa.e name,count(aa.e) num from (select distinct(a) a,e  from wnsalarydb.excel_tab_1 where year='"
									+ date_time.substring(0, 4)
									+ "') aa group by aa.e");
			Set<String> keys = userMap.keySet();
			List<String> sqllist = new ArrayList<String>();
			DecimalFormat format = new DecimalFormat("#.000");
			for (String key : keys) {
				insert.putFieldValue("username", key);
				insert.putFieldValue("date_time", getLastMonth(date_time));
				insert.putFieldValue("deptname", userMap.get(key));
				double task = Double.parseDouble(rwMap.get(key) == null ? "0"
						: rwMap.get(key));
				double passed = Double.parseDouble(jlMap.get(key) == null ? "0"
						: jlMap.get(key));
				insert.putFieldValue("passed", passed);
				insert.putFieldValue("task", task);
				if (task == 0 || passed == 0) {
					insert.putFieldValue("rate", "0");
				} else {
					insert.putFieldValue("rate", format.format(passed / task));
				}
				sqllist.add(insert.getSQL());
			}
			dmo.executeBatchByDS(null, sqllist);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "���ִ�гɹ�";
	}

	/**
	 * ZPY ǭũE�����������ɱ� date_time:2019-01-01
	 */
	@Override
	public String getQnedtdRate(String date_time) {
		String result = "";
		try {
			// �Բ�ѯ���ڽ��д���
			HashMap<String, String> rwMap = dmo.getHashMapBySQLByDS(null,
					"select A,sum(Q) from EXCEL_TAB_53 where year='"
							+ date_time.substring(0, 4) + "'  group by A");
			if (rwMap.size() <= 0) {
				return "��ǰ�����¡�" + date_time + "��ȱ��ҵ������,��������ڽ��в�ѯ";
			}
			HashMap<String, String> userMap = dmo
					.getHashMapBySQLByDS(
							null,
							"select name,deptname from v_sal_personinfo where stationkind in('�����ͻ�����','����ͻ�����','�����μ�ְ�ͻ�����','�������㸱����','�������㸱����')");
			Set<String> keys = userMap.keySet();
			String lastMonth = getSMonthDate(date_time);
			String yearC = getYearC(date_time);
			HashMap<String, String> currMonthData = dmo
					.getHashMapBySQLByDS(
							null,
							"SELECT a.mname mname, count(a.CARDNO) CARDNO   FROM (SELECT DISTINCT(CARDNO) CARDNO,MNAME FROM wn_qnedtd WHERE  date_time  LIKE '"
									+ date_time + "%') a GROUP BY  a.mname");// ��ǰ�����������ļ�Ч
			HashMap<String, String> lastMonthData = dmo
					.getHashMapBySQLByDS(
							null,
							"SELECT a.mname mname, count(a.CARDNO) CARDNO   FROM (SELECT DISTINCT(CARDNO) CARDNO,MNAME FROM wn_qnedtd WHERE  date_time  = '"
									+ yearC + "') a GROUP BY  a.mname");// ���
			InsertSQLBuilder insert = new InsertSQLBuilder("wn_qnedtd_result");
			DecimalFormat format = new DecimalFormat("0.000");
			List<String> sqllist = new ArrayList<String>();
			for (String key : keys) {
				insert.putFieldValue("username", key);
				insert.putFieldValue("deptname", userMap.get(key));
				double task = Double.parseDouble(rwMap.get(key) == null ? "0"
						: rwMap.get(key));
				insert.putFieldValue("task", task);
				double curretData = Double
						.parseDouble(currMonthData.get(key) == null ? "0"
								: currMonthData.get(key));
				double lastData = Double
						.parseDouble(lastMonthData.get(key) == null ? "0"
								: lastMonthData.get(key));
				insert.putFieldValue("passed", (curretData - lastData));
				if (task == 0 || (curretData - lastData) <= 0) {
					insert.putFieldValue("rate", "0");
				} else {
					insert.putFieldValue("rate",
							format.format((curretData - lastData) / task));
				}
				insert.putFieldValue("date_time", getLastMonth(date_time));
				sqllist.add(insert.getSQL());
			}
			dmo.executeBatchByDS(null, sqllist);
			result = "��ѯ�ɹ�";
		} catch (Exception e) {
			result = "��ѯʧ��";
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * �ֻ����п�����ɱȼ���
	 */
	@Override
	public String getsjRate(String date_time) {
		String result = "";
		try {
			HashMap<String, String> rwMap = dmo.getHashMapBySQLByDS(null,
					"select A,sum(O) from EXCEL_TAB_53 where year||'-'||month='"
							+ date_time + "' group by A");
			if (rwMap.size() <= 0) {
				return "�ڵ�ǰ�¡�" + date_time + "��ȱ��ҵ������,��������ڽ��в�ѯ";
			}
			HashMap<String, String> userMap = dmo
					.getHashMapBySQLByDS(
							null,
							"select name,deptname from v_sal_personinfo where stationkind in('�����ͻ�����','����ͻ�����','�����μ�ְ�ͻ�����','�������㸱����','�������㸱����')");
			HashMap<String, String> jlMap = dmo.getHashMapBySQLByDS(null,
					"SELECT XD_COL2,count(A) FROM V_sjyh WHERE DATE_TIME='"
							+ date_time + "' GROUP BY XD_COL2");
			Set<String> keys = userMap.keySet();
			InsertSQLBuilder insert = new InsertSQLBuilder("wn_sjyh_result");
			List<String> list = new ArrayList<String>();
			for (String key : keys) {
				insert.putFieldValue("username", key);
				insert.putFieldValue("date_time", getLastMonth(date_time));
				insert.putFieldValue("deptname", userMap.get(key));
				double task = Double.parseDouble(rwMap.get(key) == null
						|| rwMap.get(key).length() == 0 ? "0" : rwMap.get(key));
				insert.putFieldValue("task", task);
				double passed = Double.parseDouble(jlMap.get(key) == null ? "0"
						: jlMap.get(key));
				insert.putFieldValue("passed", passed);
				DecimalFormat format = new DecimalFormat("0.00");
				if (task == 0 || passed == 0) {
					insert.putFieldValue("rate", "0");
				} else {
					insert.putFieldValue("rate", format.format(passed / task));
				}
				list.add(insert.getSQL());
			}
			dmo.executeBatchByDS(null, list);
			result = "��ѯ�ɹ�";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * ��ũ�̻�ά����ɱ�
	 */
	@Override
	public String getZNRate(String date_time) {
		String result = "";
		try {
			HashMap<String, String> rwMap = dmo.getHashMapBySQLByDS(null,
					"select A,sum(L) from EXCEL_TAB_53 where year='"
							+ date_time.substring(0, 4) + "' group by A");
			if (rwMap.size() <= 0) {
				return "�ڵ�ǰ�¡�" + date_time + "��ȱ��ҵ������,��������ڽ��в�ѯ";
			}
			HashMap<String, String> jlMap = dmo
					.getHashMapBySQLByDS(
							null,
							"select aaaa.b name,count(aaaa.c) num from (select aaa.b,aaa.c from (select a.b b,a.c c,b.num num from (select b,c from excel_tab_10 where year||'-'||month='"
									+ date_time
									+ "' ) a left join (select c,count(b) num from excel_tab_11 where year||'-'||month='"
									+ date_time
									+ "' group by c) b on b.c=a.c) aaa left join (select aa.b b,aa.c c,count(bb.a) num from (select a,b,c from excel_tab_10 where year||'-'||month='"
									+ date_time
									+ "' ) aa left join (select a.a,a.num,b.c,b.e from (select a,(b+c) num  from excel_tab_14 where year||'-'||month='"
									+ date_time
									+ "') a left join (select a,b ,c,d,e from excel_tab_11 where year ||'-'|| month='"
									+ date_time
									+ "' and d !='��') b on a.a=b.b where a.num>=b.e) bb on aa.c=bb.c  group by aa.b,aa.c)  bbb  on   aaa.b=bbb.b and aaa.c=bbb.c where aaa.num=bbb.num) aaaa group by aaaa.b");
			InsertSQLBuilder insert = new InsertSQLBuilder("wn_znsh_result");
			HashMap<String, String> userMap = dmo
					.getHashMapBySQLByDS(
							null,
							"select name,deptname from v_sal_personinfo where stationkind in('�����ͻ�����','����ͻ�����','�����μ�ְ�ͻ�����','�������㸱����','�������㸱����')");
			HashVO[] userVos = dmo
					.getHashVoArrayByDS(
							null,
							"select name,deptname,stationkind from v_sal_personinfo where stationkind in('�����ͻ�����','����ͻ�����','�����μ�ְ�ͻ�����','�������㸱����','�������㸱����')");
			Set<String> keys = userMap.keySet();
			List<String> sqllist = new ArrayList<String>();

			DecimalFormat format = new DecimalFormat("#.00");
			for (int i = 0; i < userVos.length; i++) {
				String manager_name = userVos[i].getStringValue("name");
				String manager_type = userVos[i].getStringValue("stationkind");
				insert.putFieldValue("username", manager_name);
				insert.putFieldValue("deptname",
						userVos[i].getStringValue("deptname"));
				insert.putFieldValue("date_time", getLastMonth(date_time));
				if ("�����ͻ�����".equals(manager_type)) {
					insert.putFieldValue("task", 0.0);
					insert.putFieldValue("passed", 0.0);
					insert.putFieldValue("rate", 0.0);
				} else {
					double passed = Double
							.parseDouble(jlMap.get(manager_name) == null ? "0"
									: jlMap.get(manager_name));
					double task = Double
							.parseDouble(rwMap.get(manager_name) == null
									|| rwMap.get(manager_name).length() == 0 ? "0"
									: rwMap.get(manager_name));
					insert.putFieldValue("passed", passed);
					insert.putFieldValue("task", task);
					if (passed == 0 || task == 0) {
						insert.putFieldValue("rate", 0.0);
					} else {
						insert.putFieldValue("rate", Double.parseDouble(format
								.format(passed / task)));
					}
				}
				sqllist.add(insert.getSQL());
			}
			dmo.executeBatchByDS(null, sqllist);
			result = "��ѯ�ɹ�";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * ZPY ��Լ��С΢�̻���ɱȼ���
	 */
	@Override
	public String getTyxwRate(String date_time) {
		String result = "";
		try {
			HashMap<String, String> rwMap = dmo.getHashMapBySQLByDS(null,
					"select A,sum(N) from EXCEL_TAB_53 where year||'-'||month='"
							+ date_time + "' group by A");
			if (rwMap.size() <= 0) {
				return "�ڵ�ǰ�¡�" + date_time + "��ȱ��ҵ������,��������ڽ��в�ѯ";
			}
			List<String> sqlList = new ArrayList<String>();
			HashMap<String, String> userMap = dmo
					.getHashMapBySQLByDS(null,
							"select name,deptname from v_sal_personinfo where stationkind  like '%�ͻ�����%'");
			InsertSQLBuilder insert = new InsertSQLBuilder("wn_tyxw_result");
			Set<String> keys = userMap.keySet();
			Map<String, Integer> tyxw = getTyxw(date_time);
			DecimalFormat format = new DecimalFormat("0.000");
			String task = "";
			for (String manager_name : keys) {// ��ԼС΢�̻���ɱȲ�ѯ
				int num = tyxw.get(manager_name);
				insert.putFieldValue("passed", num);
				task = rwMap.get(manager_name) == null ? "0" : rwMap
						.get(manager_name);
				insert.putFieldValue("task", task);
				insert.putFieldValue("username", manager_name);
				if ("0".equals(task)) {
					insert.putFieldValue("rate", "0.0");
				} else {
					insert.putFieldValue("rate",
							format.format(num / Double.parseDouble(task)));
				}
				insert.putFieldValue("date_time", getLastMonth(date_time));
				insert.putFieldValue("deptname", userMap.get(manager_name));
				sqlList.add(insert.getSQL());
			}
			dmo.executeBatchByDS(null, sqlList);
			result = "��ѯ�ɹ�";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private Map<String, Integer> getTyxw(String date_time) {
		HashMap<String, Integer> resultMap = new HashMap<String, Integer>();
		try {
			HashMap<String, String> xwMap = dmo
					.getHashMapBySQLByDS(
							null,
							" select aa.b name,count(bb.a) num from (select a.a a ,b.b b from (select * from excel_tab_13 where  c='С΢�̻�' and year||'-'||month='"
									+ date_time
									+ "') a left join (select  * from excel_tab_16 where year||'-'||month='"
									+ date_time
									+ "') b on a.d=b.a)  aa left join (select a,c from excel_tab_59 where year||'-'||month='"
									+ date_time
									+ "' and b='С΢' AND c>=5) bb on bb.a=aa.a where bb.a is  not null group by aa.b");
			HashMap<String, String> tyMap = dmo
					.getHashMapBySQLByDS(
							null,
							"select aa.b name,count(bb.a) num from (select a.a a ,b.b b from (select * from excel_tab_13 where  c='��Լ�̻�' and year||'-'||month='"
									+ date_time
									+ "') a left join (select  * from excel_tab_16 where year||'-'||month='"
									+ date_time
									+ "') b on a.d=b.a)  aa left join  (select a,b,c from excel_tab_59 where year||'-'||month='"
									+ date_time
									+ "' and b='��Լ'  and c>=10) bb on bb.a=aa.a where bb.a is  not null group by aa.b");
			String[] manager_names = dmo
					.getStringArrayFirstColByDS(null,
							"SELECT name FROM v_sal_personinfo WHERE STATIONKIND LIKE '%�ͻ�����%'");
			for (int i = 0; i < manager_names.length; i++) {
				Integer xwNum = Integer
						.parseInt(xwMap.get(manager_names[i]) == null ? "0"
								: xwMap.get(manager_names[i]));
				Integer tyNum = Integer
						.parseInt(tyMap.get(manager_names[i]) == null ? "0"
								: tyMap.get(manager_names[i]));
				resultMap.put(manager_names[i], xwNum + tyNum);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	public String getLastMonth(String date) {
		String result = "";
		try {
			SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM");
			int year = Integer.parseInt(new SimpleDateFormat("yyyy")
					.format(simple.parse(date)));
			int month = Integer.parseInt(new SimpleDateFormat("MM")
					.format(simple.parse(date)));
			String day = "";
			switch (month) {
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				day = "31";
				break;
			case 4:
			case 6:
			case 9:
			case 11:
				day = "30";
				break;
			case 2:
				if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
					day = "29";
				} else {
					day = "28";
				}
				break;
			default:
				break;
			}
			String month2 = String.valueOf(month);
			month2 = month2.length() == 1 ? "0" + month2 : month2;
			result = year + "-" + month2 + "-" + day;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return result;
		}

	}

	/**
	 * zzl �������������ɱ�
	 */
	@Override
	public String getDKBalanceXZ(String date) {
		String jl = null;
		try {
			InsertSQLBuilder insert = new InsertSQLBuilder("wn_loan_balance");
			List list = new ArrayList<String>();
			// �õ��ͻ������Map
			HashMap<String, String> userMap = dmo
					.getHashMapBySQLByDS(
							null,
							"select name,name from v_sal_personinfo where stationkind in('�����ͻ�����','����ͻ�����','�����μ�ְ�ͻ�����','�������㸱����','�������㸱����')");
			// �õ��ͻ������������
			HashMap<String, String> rwMap = dmo.getHashMapBySQLByDS(
					null,
					"select A,sum(E) from EXCEL_TAB_53 where year='"
							+ date.substring(0, 4) + "' group by A");
			if (rwMap.size() == 0) {
				return "��ǰʱ�䡾" + date + "��û���ϴ�������";
			}
			// �õ����������map
			HashMap<String, String> deptCodeMap = dmo
					.getHashMapBySQLByDS(
							null,
							"select sal.name,dept.code from v_sal_personinfo sal left join pub_corp_dept dept on sal.deptid=dept.id");
			// �õ��������͵�map
			HashMap<String, String> deptTypeMap = dmo
					.getHashMapBySQLByDS(
							null,
							"select sal.name,dept.corptype from v_sal_personinfo sal left join pub_corp_dept dept on sal.deptid=dept.id");
			// Ӫҵ�������´������-ũ��Map
			HashMap<String, String> YEKDKNHMap = getKDKNHSalesOffice(date);
			HashMap<String, String> YESDKNHMap = getSDKNHSalesOffice(date);
			HashMap<String, String> YEKDKQTMap = getKDKQTSalesOffice(date);
			HashMap<String, String> YESDKQTMap = getSDKQTSalesOffice(date);
			HashMap<String, String> YEKDKDGMap = getKDKDGSalesOffice(date);
			HashMap<String, String> YESDKDGMap = getSDKDGSalesOffice(date);
			HashMap<String, String> KCQNHMap = getKDKCQNHSalesOffice(date);
			HashMap<String, String> SCQNHMap = getSDKCQNHSalesOffice(date);
			HashMap<String, String> KCQQTMap = getKDKCQQTSalesOffice(date);
			HashMap<String, String> SCQQTMap = getSDKCQQTSalesOffice(date);
			HashMap<String, String> KCQDGMap = getKDKDG100SalesOffice(date);
			HashMap<String, String> SCQDGMap = getSDKDG100SalesOffice(date);
			HashMap<String, String> KDGNHMap = getKDKNHSales(date);
			HashMap<String, String> SDGNHMap = getSDKNHSales(date);
			HashMap<String, String> KDGQTMap = getKDKQTSales(date);
			HashMap<String, String> SDGQTMap = getSDKQTSales(date);
			for (String str : userMap.keySet()) {
				Double yyb1, yyb2, yyb3, yyb4, yyb5, yyb6 = 0.0;
				Double count = 0.0;
				if (deptCodeMap.get(str).equals("2820100")) {
					if (YEKDKNHMap.get(str) == null) {
						yyb1 = 0.0;
					} else {
						yyb1 = Double.parseDouble(YEKDKNHMap.get(str));
					}
					if (YESDKNHMap.get(str) == null) {
						yyb2 = 0.0;
					} else {
						yyb2 = Double.parseDouble(YESDKNHMap.get(str));
					}
					if (YEKDKQTMap.get(str) == null) {
						yyb3 = 0.0;
					} else {
						yyb3 = Double.parseDouble(YEKDKQTMap.get(str));
					}
					if (YESDKQTMap.get(str) == null) {
						yyb4 = 0.0;
					} else {
						yyb4 = Double.parseDouble(YESDKQTMap.get(str));
					}
					if (YEKDKDGMap.get(str) == null) {
						yyb5 = 0.0;
					} else {
						yyb5 = Double.parseDouble(YEKDKDGMap.get(str));
					}
					if (YESDKDGMap.get(str) == null) {
						yyb6 = 0.0;
					} else {
						yyb6 = Double.parseDouble(YESDKDGMap.get(str));
					}
					count = (yyb1 - yyb2) + (yyb3 - yyb4) + (yyb5 - yyb6);
					insert.putFieldValue("name", str);
					insert.putFieldValue("passed", count);
					insert.putFieldValue("task", rwMap.get(str));
					insert.putFieldValue("date_time", date);
					list.add(insert.getSQL());
				} else if (deptTypeMap.get(str).equals("��������")) {
					if (KCQNHMap.get(str) == null) {
						yyb1 = 0.0;
					} else {
						yyb1 = Double.parseDouble(KCQNHMap.get(str));
					}
					if (SCQNHMap.get(str) == null) {
						yyb2 = 0.0;
					} else {
						yyb2 = Double.parseDouble(SCQNHMap.get(str));
					}
					if (KCQQTMap.get(str) == null) {
						yyb3 = 0.0;
					} else {
						yyb3 = Double.parseDouble(KCQQTMap.get(str));
					}
					if (SCQQTMap.get(str) == null) {
						yyb4 = 0.0;
					} else {
						yyb4 = Double.parseDouble(SCQQTMap.get(str));
					}
					if (KCQDGMap.get(str) == null) {
						yyb5 = 0.0;
					} else {
						yyb5 = Double.parseDouble(KCQDGMap.get(str));
					}
					if (SCQDGMap.get(str) == null) {
						yyb6 = 0.0;
					} else {
						yyb6 = Double.parseDouble(SCQDGMap.get(str));
					}
					count = (yyb1 - yyb2) + (yyb3 - yyb4) + (yyb5 - yyb6);
					insert.putFieldValue("name", str);
					insert.putFieldValue("passed", count);
					insert.putFieldValue("task", rwMap.get(str));
					insert.putFieldValue("date_time", date);
					list.add(insert.getSQL());
				} else {
					if (KDGNHMap.get(str) == null) {
						yyb1 = 0.0;
					} else {
						yyb1 = Double.parseDouble(KDGNHMap.get(str));
					}
					if (SDGNHMap.get(str) == null) {
						yyb2 = 0.0;
					} else {
						yyb2 = Double.parseDouble(SDGNHMap.get(str));
					}
					if (KDGQTMap.get(str) == null) {
						yyb3 = 0.0;
					} else {
						yyb3 = Double.parseDouble(KDGQTMap.get(str));
					}
					if (SDGQTMap.get(str) == null) {
						yyb4 = 0.0;
					} else {
						yyb4 = Double.parseDouble(SDGQTMap.get(str));
					}
					count = (yyb1 - yyb2) + (yyb3 - yyb4);
					insert.putFieldValue("name", str);
					insert.putFieldValue("passed", count);
					insert.putFieldValue("task", rwMap.get(str));
					insert.putFieldValue("date_time", date);
					list.add(insert.getSQL());
				}
			}
			dmo.executeBatchByDS(null, list);
			jl = "��ѯ�ɹ�";
		} catch (Exception e) {
			jl = "��ѯʧ��";
			e.printStackTrace();
		}
		return jl;
	}

	/**
	 * zzl Ӫҵ�������´������-ũ��Map
	 * 
	 * @return
	 */
	public HashMap<String, String> getKDKNHSalesOffice(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select bb.xd_col2 as xd_col2 ,sum(aa.xd_col7)/10000 xd_col7 from (select xd_col81,sum(xd_col7) as xd_col7,sum(xd_col6) xd_col6  from wnbank.s_loan_dk where to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ date
									+ "'  and xd_col2 not like '��˾' and XD_COL22<>'05' and XD_COL166 not in('81320101')  and XD_COL7<>0  and xd_col72 in('16','24','15','25','30','z01','38','45','46','47','48') group by XD_COL14,xd_col81) aa left join wnbank.s_loan_ryb bb on aa.xd_col81=bb.xd_col1 where aa.xd_col6<5000000 group by bb.xd_col2");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * zzl Ӫҵ������������-ũ��Map
	 * 
	 * @return
	 */
	public HashMap<String, String> getSDKNHSalesOffice(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select bb.xd_col2 as xd_col2 ,sum(aa.xd_col7)/10000 xd_col7 from (select xd_col81,sum(xd_col7) as xd_col7,sum(xd_col6) xd_col6  from wnbank.s_loan_dk where to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ getYearDate(date)
									+ "'  and xd_col2 not like '��˾' and XD_COL22<>'05' and XD_COL166 not in('81320101')  and XD_COL7<>0  and xd_col72 in('16','24','15','25','30','z01','38','45','46','47','48') group by XD_COL14,xd_col81) aa left join wnbank.s_loan_ryb bb on aa.xd_col81=bb.xd_col1 where aa.xd_col6<5000000 group by bb.xd_col2");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * zzl Ӫҵ�������´������-����Map
	 * 
	 * @return
	 */
	public HashMap<String, String> getKDKQTSalesOffice(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select bb.xd_col2 as xd_col2 ,sum(aa.xd_col7)/10000 xd_col7 from (select xd_col81,sum(xd_col7) as xd_col7,sum(xd_col6) xd_col6  from wnbank.s_loan_dk where to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ date
									+ "'  and xd_col2 not like '��˾' and XD_COL22<>'05' and XD_COL166 not in('81320101')  and XD_COL7<>0 and xd_col72 not in('16','24','15','25','30','z01','38','45','46','47','48') group by XD_COL14,xd_col81) aa left join wnbank.s_loan_ryb bb on aa.xd_col81=bb.xd_col1 where aa.xd_col6<5000000 group by bb.xd_col2");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * zzl Ӫҵ������������-����Map
	 * 
	 * @return
	 */
	public HashMap<String, String> getSDKQTSalesOffice(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select bb.xd_col2 as xd_col2 ,sum(aa.xd_col7)/10000 xd_col7 from (select xd_col81,sum(xd_col7) as xd_col7,sum(xd_col6) xd_col6  from wnbank.s_loan_dk where to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ getYearDate(date)
									+ "'  and xd_col2 not like '��˾' and XD_COL22<>'05' and XD_COL166 not in('81320101')  and XD_COL7<>0 and xd_col72 not in('16','24','15','25','30','z01','38','45','46','47','48') group by XD_COL14,xd_col81) aa left join wnbank.s_loan_ryb bb on aa.xd_col81=bb.xd_col1 where aa.xd_col6<5000000 group by bb.xd_col2");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * zzl Ӫҵ���Թ��������Map
	 * 
	 * @return
	 */
	public HashMap<String, String> getKDKDGSalesOffice(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select dg.cus_manager cus_manager,sum(LOAN_BALANCE)/10000 tj from (select dg.cus_manager cus_manager,sum(LOAN_AMOUNT) tj,sum(LOAN_BALANCE) LOAN_BALANCE  from wnbank.s_cmis_acc_loan dg where to_char(to_date(dg.load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ date
									+ "' and dg.cla<>'05' and account_status='1' and loan_balance<>'0' group by CUS_ID,dg.cus_manager) dg where dg.tj<10000000 group by dg.cus_manager");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * zzl Ӫҵ������Թ��������Map
	 * 
	 * @return
	 */
	public HashMap<String, String> getSDKDGSalesOffice(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select dg.cus_manager cus_manager,sum(LOAN_BALANCE)/10000 tj from (select dg.cus_manager cus_manager,sum(LOAN_AMOUNT) tj,sum(LOAN_BALANCE) LOAN_BALANCE  from wnbank.s_cmis_acc_loan dg where to_char(to_date(dg.load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ getYearDate(date)
									+ "' and dg.cla<>'05' and account_status='1' and loan_balance<>'0' group by CUS_ID,dg.cus_manager) dg where dg.tj<10000000 group by dg.cus_manager");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * zzl ��������ũ�������������Map
	 * 
	 * @return
	 */
	public HashMap<String, String> getKDKCQNHSalesOffice(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select bb.xd_col2 as xd_col2 ,sum(aa.xd_col7)/10000 xd_col7 from (select xd_col81,sum(xd_col7) as xd_col7,sum(xd_col6) xd_col6  from wnbank.s_loan_dk where to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ date
									+ "' and xd_col72 in ('16','24','15','25','30','z01','38','45','46','47','48') and XD_COL22<>'05' and XD_COL166 not in('81320101') and XD_COL7<>0 and xd_col2 not like '%��˾%' group by XD_COL14,xd_col81) aa left join wnbank.s_loan_ryb bb on aa.xd_col81=bb.xd_col1 and aa.xd_col6<1000000 group by bb.xd_col2");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * zzl �����������ũ�������������Map
	 * 
	 * @return
	 */
	public HashMap<String, String> getSDKCQNHSalesOffice(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select bb.xd_col2 as xd_col2 ,sum(aa.xd_col7)/10000 xd_col7 from (select xd_col81,sum(xd_col7) as xd_col7,sum(xd_col6) xd_col6  from wnbank.s_loan_dk where to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ getYearDate(date)
									+ "' and xd_col72 in ('16','24','15','25','30','z01','38','45','46','47','48') and XD_COL22<>'05' and XD_COL166 not in('81320101') and XD_COL7<>0 and xd_col2 not like '%��˾%' group by XD_COL14,xd_col81) aa left join wnbank.s_loan_ryb bb on aa.xd_col81=bb.xd_col1 and aa.xd_col6<1000000 group by bb.xd_col2");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * zzl �����������������������Map
	 * 
	 * @return
	 */
	public HashMap<String, String> getKDKCQQTSalesOffice(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select bb.xd_col2 as xd_col2 ,sum(aa.xd_col7)/10000 xd_col7 from (select xd_col81,sum(xd_col6) as xd_col6,sum(xd_col7) xd_col7  from wnbank.s_loan_dk where to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ date
									+ "' and xd_col72 not in ('16','24','15','25','30','z01','38','45','46','47','48') and XD_COL22<>'05' and XD_COL166 not in('81320101') and XD_COL7<>0 and xd_col2 not like '%��˾%' group by XD_COL14,xd_col81 ) aa left join wnbank.s_loan_ryb bb on aa.xd_col81=bb.xd_col1 and aa.xd_col6<1000000 group by bb.xd_col2");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * zzl ��������������������������Map
	 * 
	 * @return
	 */
	public HashMap<String, String> getSDKCQQTSalesOffice(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select bb.xd_col2 as xd_col2 ,sum(aa.xd_col7)/10000 xd_col7 from (select xd_col81,sum(xd_col6) as xd_col6,sum(xd_col7) xd_col7  from wnbank.s_loan_dk where to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ getYearDate(date)
									+ "' and xd_col72 not in ('16','24','15','25','30','z01','38','45','46','47','48') and XD_COL22<>'05' and XD_COL166 not in('81320101') and XD_COL7<>0 and xd_col2 not like '%��˾%' group by XD_COL14,xd_col81 ) aa left join wnbank.s_loan_ryb bb on aa.xd_col81=bb.xd_col1 and aa.xd_col6<1000000 group by bb.xd_col2");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * zzl �Թ��������100������Map
	 * 
	 * @return
	 */
	public HashMap<String, String> getKDKDG100SalesOffice(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select dg.cus_manager cus_manager,sum(LOAN_BALANCE)/10000 tj from (select dg.cus_manager cus_manager,sum(LOAN_AMOUNT) tj,sum(LOAN_BALANCE) LOAN_BALANCE  from wnbank.s_cmis_acc_loan dg where to_char(to_date(dg.load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ date
									+ "' and dg.cla<>'05' and account_status='1' and loan_balance<>'0' group by CUS_ID,dg.cus_manager) dg where dg.tj<1000000 group by dg.cus_manager");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * zzl �Թ�����������100������Map
	 * 
	 * @return
	 */
	public HashMap<String, String> getSDKDG100SalesOffice(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select dg.cus_manager cus_manager,sum(LOAN_BALANCE)/10000 tj from (select dg.cus_manager cus_manager,sum(LOAN_AMOUNT) tj,sum(LOAN_BALANCE) LOAN_BALANCE  from wnbank.s_cmis_acc_loan dg where to_char(to_date(dg.load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ getYearDate(date)
									+ "' and dg.cla<>'05' and account_status='1' and loan_balance<>'0' group by CUS_ID,dg.cus_manager) dg where dg.tj<1000000 group by dg.cus_manager");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * zzl �����´������-ũ��Map
	 * 
	 * @return
	 */
	public HashMap<String, String> getKDKNHSales(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select bb.xd_col2 as xd_col2 ,aa.xd_col7 xd_col7 from (select xd_col81,sum(xd_col7)/10000 as xd_col7,sum(xd_col6)/10000 xd_col6  from wnbank.s_loan_dk where to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ date
									+ "' and xd_col72 in ('16','24','15','25','30','z01','38','45','46','47','48') and XD_COL22<>'05' and XD_COL166 not in('81320101')  and XD_COL7<>0 and xd_col2 not like '%��˾%' group by xd_col81) aa left join wnbank.s_loan_ryb bb on aa.xd_col81=bb.xd_col1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * zzl ����������-ũ��Map
	 * 
	 * @return
	 */
	public HashMap<String, String> getSDKNHSales(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select bb.xd_col2 as xd_col2 ,aa.xd_col7 xd_col7 from (select xd_col81,sum(xd_col7)/10000 as xd_col7,sum(xd_col6)/10000 xd_col6  from wnbank.s_loan_dk where to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ getYearDate(date)
									+ "' and xd_col72 in ('16','24','15','25','30','z01','38','45','46','47','48') and XD_COL22<>'05' and XD_COL166 not in('81320101')  and XD_COL7<>0 and xd_col2 not like '%��˾%' group by xd_col81) aa left join wnbank.s_loan_ryb bb on aa.xd_col81=bb.xd_col1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * zzl �����´������-����Map
	 * 
	 * @return
	 */
	public HashMap<String, String> getKDKQTSales(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select bb.xd_col2 as xd_col2 ,aa.xd_col7 xd_col7 from (select xd_col81,sum(xd_col7)/10000 as xd_col7  from wnbank.s_loan_dk where to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ date
									+ "' and xd_col72 not in ('16','24','15','25','30','z01','38','45','46','47','48') and XD_COL22<>'05' and XD_COL166 not in('81320101')  and XD_COL7<>0 and xd_col2 not like '%��˾%' group by xd_col81) aa left join wnbank.s_loan_ryb bb on aa.xd_col81=bb.xd_col1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * zzl ����������-����Map
	 * 
	 * @return
	 */
	public HashMap<String, String> getSDKQTSales(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select bb.xd_col2 as xd_col2 ,aa.xd_col7 xd_col7 from (select xd_col81,sum(xd_col7)/10000 as xd_col7  from wnbank.s_loan_dk where to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ getYearDate(date)
									+ "' and xd_col72 not in ('16','24','15','25','30','z01','38','45','46','47','48') and XD_COL22<>'05' and XD_COL166 not in('81320101')  and XD_COL7<>0 and xd_col2 not like '%��˾%' group by xd_col81) aa left join wnbank.s_loan_ryb bb on aa.xd_col81=bb.xd_col1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * zzl ���������
	 */
	@Override
	public String getDKHouseholdsXZ(String date) {
		String jl = null;
		try {
			InsertSQLBuilder insert = new InsertSQLBuilder("WN_LOANS_newly");
			List list = new ArrayList<String>();
			// �õ��ͻ������Map
			HashMap<String, String> userMap = dmo
					.getHashMapBySQLByDS(
							null,
							"select name,name from v_sal_personinfo where stationkind in('�����ͻ�����','����ͻ�����','�����μ�ְ�ͻ�����','�������㸱����','�������㸱����')");
			// �õ��ͻ������������
			HashMap<String, String> rwMap = dmo.getHashMapBySQLByDS(
					null,
					"select A,sum(F) from EXCEL_TAB_53 where year='"
							+ date.substring(0, 4) + "' group by A");
			if (rwMap.size() == 0) {
				return "��ǰʱ�䡾" + date + "��û���ϴ�������";
			}
			HashMap<String, String> knhMap = getKDKNHNewly(date);
			HashMap<String, String> snhMap = getSDKNHNewly(date);
			HashMap<String, String> kqtMap = getKDKQTNewly(date);
			HashMap<String, String> sqtMap = getSDKQTNewly(date);
			HashMap<String, String> kdgMap = getKDKDGNewly(date);
			HashMap<String, String> sdgMap = getSDKDGNewly(date);
			for (String str : userMap.keySet()) {
				int count = 0;
				int hs1, hs2, hs3, hs4, hs5, hs6 = 0;
				if (knhMap.get(str) == null) {
					hs1 = 0;
				} else {
					hs1 = Integer.parseInt(knhMap.get(str));
				}
				if (snhMap.get(str) == null) {
					hs2 = 0;
				} else {
					hs2 = Integer.parseInt(snhMap.get(str));
				}
				if (kqtMap.get(str) == null) {
					hs3 = 0;
				} else {
					hs3 = Integer.parseInt(kqtMap.get(str));
				}
				if (sqtMap.get(str) == null) {
					hs4 = 0;
				} else {
					hs4 = Integer.parseInt(sqtMap.get(str));
				}
				if (kdgMap.get(str) == null) {
					hs5 = 0;
				} else {
					hs5 = Integer.parseInt(kdgMap.get(str));
				}
				if (sdgMap.get(str) == null) {
					hs6 = 0;
				} else {
					hs6 = Integer.parseInt(sdgMap.get(str));
				}
				count = (hs1 - hs2) + (hs3 - hs4) + (hs5 - hs6);
				insert.putFieldValue("name", str);
				insert.putFieldValue("passed", count);
				insert.putFieldValue("task", rwMap.get(str));
				insert.putFieldValue("date_time", date);
				list.add(insert.getSQL());
			}
			dmo.executeBatchByDS(null, list);
			jl = "��ѯ�ɹ�";
		} catch (Exception e) {
			jl = "��ѯʧ��";
			e.printStackTrace();
		}
		return jl;
	}

	/**
	 * zzl �����´����-ũ��Map
	 * 
	 * @return
	 */
	public HashMap<String, String> getKDKNHNewly(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select rr.xd_col2 xd_col2,dgxj.countxj countxj from (select dg.xd_col81 xd_col81,count(dg.xd_col81) countxj from( select distinct(xd_col16) xd_col16,xd_col81 xd_col81 from wnbank.s_loan_dk dk where xd_col7<>0 and xd_col22<>'05' and xd_col2 not like '%��˾%' and xd_col166<>'81320101' and xd_col72 in('16','24','15','25','30','z01','38','45','46','47','48') and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ date
									+ "') dg group by dg.xd_col81) dgxj left join wnbank.s_loan_ryb rr on dgxj.xd_col81=rr.xd_col1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * zzl ��������-ũ��Map
	 * 
	 * @return
	 */
	public HashMap<String, String> getSDKNHNewly(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select rr.xd_col2 xd_col2,dgxj.countxj countxj from (select dg.xd_col81 xd_col81,count(dg.xd_col81) countxj from( select distinct(xd_col16) xd_col16,xd_col81 xd_col81 from wnbank.s_loan_dk dk where xd_col7<>0 and xd_col22<>'05' and xd_col2 not like '%��˾%' and xd_col166<>'81320101' and xd_col72 in('16','24','15','25','30','z01','38','45','46','47','48') and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ getYearDate(date)
									+ "') dg group by dg.xd_col81) dgxj left join wnbank.s_loan_ryb rr on dgxj.xd_col81=rr.xd_col1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * zzl �����´����-����Map
	 * 
	 * @return
	 */
	public HashMap<String, String> getKDKQTNewly(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select rr.xd_col2 xd_col2,dgxj.countxj countxj from (select dg.xd_col81 xd_col81,count(dg.xd_col81) countxj from( select distinct(xd_col16) xd_col16,xd_col81 xd_col81 from wnbank.s_loan_dk dk where xd_col7<>0 and xd_col22<>'05' and xd_col2 not like '%��˾%' and xd_col166<>'81320101' and xd_col72 not in('16','24','15','25','30','z01','38','45','46','47','48') and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ date
									+ "') dg group by dg.xd_col81) dgxj left join wnbank.s_loan_ryb rr on dgxj.xd_col81=rr.xd_col1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * zzl ��������-����Map
	 * 
	 * @return
	 */
	public HashMap<String, String> getSDKQTNewly(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select rr.xd_col2 xd_col2,dgxj.countxj countxj from (select dg.xd_col81 xd_col81,count(dg.xd_col81) countxj from( select distinct(xd_col16) xd_col16,xd_col81 xd_col81 from wnbank.s_loan_dk dk where xd_col7<>0 and xd_col22<>'05' and xd_col2 not like '%��˾%' and xd_col166<>'81320101' and xd_col72 not in('16','24','15','25','30','z01','38','45','46','47','48') and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ getYearDate(date)
									+ "') dg group by dg.xd_col81) dgxj left join wnbank.s_loan_ryb rr on dgxj.xd_col81=rr.xd_col1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * zzl �����´����-��ҵMap
	 * 
	 * @return
	 */
	public HashMap<String, String> getKDKDGNewly(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select dg.cus_manager cus_manager,count(dg.cus_manager) countxj from (select distinct(xx.cert_code),dg.cus_manager  from wnbank.s_cmis_acc_loan dg left join wnbank.s_cmis_cus_base xx on dg.cus_id=xx.cus_id  where to_char(to_date(dg.load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ date
									+ "'and dg.cla<>'05' and account_status='1' and loan_balance<>'0')dg group by dg.cus_manager");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * zzl ��������-��ҵMap
	 * 
	 * @return
	 */
	public HashMap<String, String> getSDKDGNewly(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select dg.cus_manager cus_manager,count(dg.cus_manager) countxj from (select distinct(xx.cert_code),dg.cus_manager  from wnbank.s_cmis_acc_loan dg left join wnbank.s_cmis_cus_base xx on dg.cus_id=xx.cus_id  where to_char(to_date(dg.load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ getYearDate(date)
									+ "'and dg.cla<>'05' and account_status='1' and loan_balance<>'0')dg group by dg.cus_manager");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * zzl ���ⲻ��������ɱ�
	 * 
	 * @param date
	 * @return
	 */
	@Override
	public String getBadLoans(String date) {
		String jl = null;
		try {
			InsertSQLBuilder insert = new InsertSQLBuilder("WN_OFFDK_BAB");
			List list = new ArrayList<String>();
			// �õ��ͻ������Map
			HashMap<String, String> userMap = dmo
					.getHashMapBySQLByDS(
							null,
							"select name,name from v_sal_personinfo where stationkind in('�����ͻ�����','����ͻ�����','�����μ�ְ�ͻ�����','�������㸱����','�������㸱����')");
			// �õ��ͻ������������
			HashMap<String, String> rwMap = dmo.getHashMapBySQLByDS(
					null,
					"select A,sum(K) from EXCEL_TAB_53 where year='"
							+ date.substring(0, 4) + "' group by A");
			if (rwMap.size() == 0) {
				return "��ǰʱ�䡾" + date + "��û���ϴ�������";
			}
			HashMap<String, String> bab2017Map = get2017DBadLoans(date);
			HashMap<String, String> bab2018Map = get2018DBadLoans(date);
			for (String str : userMap.keySet()) {
				Double count = 0.0;
				Double hj1, hj2 = 0.0;
				if (bab2017Map.get(str) == null) {
					hj1 = 0.0;
				} else {
					hj1 = Double.parseDouble(bab2017Map.get(str));
				}
				if (bab2018Map.get(str) == null) {
					hj2 = 0.0;
				} else {
					hj2 = Double.parseDouble(bab2018Map.get(str));
				}
				count = hj1 + hj2;
				insert.putFieldValue("name", str);
				insert.putFieldValue("passed", count);
				insert.putFieldValue("task", rwMap.get(str));
				insert.putFieldValue("date_time", date);
				list.add(insert.getSQL());
			}
			dmo.executeBatchByDS(null, list);
			jl = "��ѯ�ɹ�";
		} catch (Exception e) {
			jl = "��ѯʧ��";
			e.printStackTrace();
		}
		return jl;
	}

	/**
	 * zzl �ֽ��ջر��ⲻ�����Ϣ���2017Map
	 * 
	 * @return
	 */
	public HashMap<String, String> get2017DBadLoans(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select yb.xd_col2 xd_col2,sum(bwbl.sumtj)/10000 tj from(select hk.xd_col1 xd_col1,hk.sumtj sumtj,dk.xd_col81 xd_col81 from(select XD_COL1 XD_COL1,sum(XD_COL5+XD_COL11) sumtj from  wnbank.S_LOAN_HK hk where to_char(cast (cast (XD_COL4 as timestamp) as date),'yyyy-mm-dd')>='"
									+ getMonthC(date)
									+ "' and to_char(cast (cast (XD_COL4 as timestamp) as date),'yyyy-mm-dd')<='"
									+ date
									+ "' and  XD_COL16='05'  and XD_COL20<>'81320101' group by XD_COL1) hk  left join wnbank.s_loan_dk dk on hk.xd_col1=dk.xd_col1  where to_char(to_date(dk.load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ date
									+ "' and  XD_COL5<='2017-12-31') bwbl left join wnbank.S_LOAN_RYB yb on bwbl.XD_COL81=yb.xd_col1 group by yb.xd_col2");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * zzl �ֽ��ջر��ⲻ�����Ϣ���(2018)Map
	 * 
	 * @return
	 */
	public HashMap<String, String> get2018DBadLoans(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select yb.xd_col2,sum(bwbl.sumtj)/10000 from(select hk.xd_col1 xd_col1,hk.sumtj sumtj,dk.xd_col81 xd_col81 from(select XD_COL1 XD_COL1,sum(XD_COL5+XD_COL11) sumtj from  wnbank.S_LOAN_HK hk where to_char(cast (cast (XD_COL4 as timestamp) as date),'yyyy-mm-dd')>='"
									+ getMonthC(date)
									+ "' and to_char(cast (cast (XD_COL4 as timestamp) as date),'yyyy-mm-dd')<='"
									+ date
									+ "' and  XD_COL16='05'  and XD_COL20<>'81320101' group by XD_COL1) hk left join wnbank.s_loan_dk dk on hk.xd_col1=dk.xd_col1  where to_char(to_date(dk.load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ date
									+ "' and  XD_COL5<='2018-12-31' and XD_COL5>='2018-01-01') bwbl left join wnbank.S_LOAN_RYB yb on bwbl.XD_COL81=yb.xd_col1 group by yb.xd_col2");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * zzl �����³�������
	 * 
	 * @param date
	 * @return
	 */
	public String getMonthC(String date) {
		date = date.substring(0, 7);
		return date + "-01";
	}

	/**
	 * zzl �������������
	 * 
	 * @param date
	 * @return
	 */
	public String getYearC(String date) {
		date = date.substring(0, 4);
		date = String.valueOf(Integer.valueOf(date) - 1);
		return date + "-12-31";
	}

	/**
	 * zzl[�ջش�������������ɱ�&��������ѹ��]
	 */
	@Override
	public String getTheStockOfLoan(String date) {
		String jl = null;
		try {
			InsertSQLBuilder insert = new InsertSQLBuilder("WN_Stock_Loan");
			List list = new ArrayList<String>();
			// �õ��ͻ������Map
			HashMap<String, String> userMap = dmo
					.getHashMapBySQLByDS(
							null,
							"select name,name from v_sal_personinfo where stationkind in('�����ͻ�����','����ͻ�����','�����μ�ְ�ͻ�����','�������㸱����','�������㸱����')");
			// �õ��ͻ������������
			HashMap<String, String> rwMap = dmo.getHashMapBySQLByDS(
					null,
					"select A,sum(G) from EXCEL_TAB_53 where year='"
							+ date.substring(0, 4) + "' group by A");
			if (rwMap.size() == 0) {
				return "��ǰʱ�䡾" + date + "��û���ϴ�������";
			}
			HashMap<String, String> slMap = getStockLoans(date);
			for (String str : userMap.keySet()) {
				Double count = 0.0;
				if (slMap.get(str) == null) {
					count = 0.0;
				} else {
					count = Double.parseDouble(slMap.get(str));
				}
				insert.putFieldValue("name", str);
				insert.putFieldValue("passed", count);
				insert.putFieldValue("task", rwMap.get(str));
				insert.putFieldValue("date_time", date);
				list.add(insert.getSQL());
			}
			dmo.executeBatchByDS(null, list);
			jl = "��ѯ�ɹ�";
		} catch (Exception e) {
			jl = "��ѯʧ��";
			e.printStackTrace();
		}

		return jl;
	}

	/**
	 * zzl �ջش������������
	 * 
	 * @return
	 */
	public HashMap<String, String> getStockLoans(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select yb.xd_col2,sum(zjj.XD_COL5)/10000 from(select zj.XD_COL81 XD_COL81,sum(zj.XD_COL5) XD_COL5 from(select hk.xd_col14,hk.XD_COL5,hk.XD_COL81,dk.xd_col1 from (select xdk.xd_col1,xhk.XD_COL4,xhk.XD_COL5,xdk.XD_COL2,xdk.XD_COL81,xdk.XD_COL14 from(select * from wnbank.S_LOAN_HK where Xd_Col16 in('03','04') and to_char(cast (cast (XD_COL4 as timestamp) as date),'yyyy-mm-dd')<='"
									+ date
									+ "' and to_char(cast (cast (XD_COL4 as timestamp) as date),'yyyy-mm-dd')>='"
									+ getMonthC(date)
									+ "') xhk left join (select * from wnbank.S_LOAN_DK where to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ date
									+ "' and xd_col22 in('03','04')) xdk on xdk.xd_col1=xhk.xd_col1) hk left join (select * from wnbank.S_LOAN_DK where to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ getYearC(date)
									+ "' and xd_col22 in('03','04')) dk on dk.xd_col1=hk.xd_col1) zj where zj.xd_col1 is not null group by zj.xd_col14,zj.XD_COL81) zjj left join wnbank.S_LOAN_RYB yb on zjj.XD_COL81=yb.xd_col1 group by yb.xd_col2");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * Ϊÿ��ί�ɻ�����ɴ��
	 */
	@Override
	public String getKJDXScore(String id) {
		String result = "";
		try {
			// // ��ѯ����δ�������ֵĿͻ�����
			// HashVO[] vos = dmo.getHashVoArrayByDS(null,
			// "select * from wn_managerdx_table where  state='������'");
			// if (vos.length <= 0) {
			// result = "��ǰ���˼ƻ������ɹ�";
			// } else {// ��δ�����Ŀ�����Ա,ֱ�ӽ�������Ա�ķ������ó�0��
			// UpdateSQLBuilder update = new UpdateSQLBuilder(
			// "wn_managerdx_table");
			// List<String> _sqllist = new ArrayList<String>();
			// Map<String, Double> map=new HashMap<String, Double>();
			// for (int i = 0; i < vos.length; i++) {
			//
			// String xiangmu = vos[i].getStringValue("xiangmu");
			// // if("�ܷ�".equals(xiangmu)){continue;}
			// String fenzhi = vos[i].getStringValue("fenzhi");
			// String koufen = vos[i].getStringValue("koufen");
			// String khsm = vos[i].getStringValue("khsm");
			// String usercode = vos[i].getStringValue("usercode");
			// if(koufen==null || "".isEmpty()||
			// Integer.parseInt(koufen)>=Integer.parseInt(fenzhi)){
			// koufen=fenzhi;
			// }
			// if(!map.containsKey(usercode)){
			// map.put(usercode, Double.valueOf(koufen));
			// }else {
			// map.put(usercode, map.get(usercode)+Double.valueOf(koufen));
			// }
			// update.setWhereCondition("usercode='" + usercode
			// + "' and khsm='" + khsm + "' and state='������'");
			// update.putFieldValue("koufen", koufen);
			// update.putFieldValue("state", "���ֽ���");
			// _sqllist.add(update.getSQL());
			// }
			// Set<String> usercodeKey = map.keySet();
			// String sql="";
			// for (String usercode : usercodeKey) {
			// sql="update wn_managerdx_table set fenzhi='"+map.get(usercode)+"' where usercode='"+usercode+"' and state='������'";
			// _sqllist.add(sql);
			// }
			// dmo.executeBatchByDS(null, _sqllist);
			// result = "��ǰ���˼ƻ������ɹ�";
			// ���ȣ���ȡ����Ҫ���뿼�˵�ί�ɻ����Ϣ
			HashVO[] kjVos = dmo.getHashVoArrayByDS(null,
					"SELECT * FROM V_PUB_USER_POST_1 WHERE POSTNAME='ί�ɻ��'");
			// ��ȡ��ί�ɻ�ƵĴ����
			HashVO[] pfVos = dmo
					.getHashVoArrayByDS(
							null,
							"   select * from sal_person_check_list where 1=1  and (type='101')  and (1=1)  order by seq");
			String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(new Date());
			List<String> sqlList = new ArrayList<String>();
			InsertSQLBuilder insert = new InsertSQLBuilder("wn_kjscore_table");
			for (int i = 0; i < kjVos.length; i++) {// Ϊÿһ��������ɴ�ּƻ�
				for (int j = 0; j < pfVos.length; j++) {
					insert.putFieldValue("PLANID", id);
					insert.putFieldValue("USERNAME",
							kjVos[i].getStringValue("username"));
					insert.putFieldValue("USERCODE",
							kjVos[i].getStringValue("usercode"));
					insert.putFieldValue("USERDEPT",
							kjVos[i].getStringValue("deptcode"));
					insert.putFieldValue("xiangmu",
							pfVos[j].getStringValue("catalog"));
					insert.putFieldValue("khsm",
							pfVos[j].getStringValue("name"));
					insert.putFieldValue("fenzhi",
							pfVos[j].getStringValue("weights"));
					insert.putFieldValue("koufen", 0.0);
					insert.putFieldValue("pftime", date);
					insert.putFieldValue("state", "������");
				}
				sqlList.add(insert.getSQL());
			}
			dmo.executeBatchByDS(null, sqlList);
			result = "ִ�гɹ�";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String getKJDXEnd(String id) {
		String result = "";
		try {
			// ���Ȼ�ȡ��ǰ��δ�������˵�ί�ɻ��
			HashVO[] kjVos = dmo.getHashVoArrayByDS(null,
					"SELECT * FROM V_PUB_USER_POST_1 WHERE POSTNAME='ί�ɻ��'");
			HashVO[] pfVos = dmo.getHashVoArrayByDS(null,
					"select * from wn_kjscore_table where state='������' and planid='"
							+ id + "'");
			double fenzhi = 0.0;
			UpdateSQLBuilder update = new UpdateSQLBuilder("wn_kjscore_table");
			List<String> sqlList = new ArrayList<String>();
			for (int i = 0; i < pfVos.length; i++) {
				fenzhi = Double.parseDouble(pfVos[i].getStringValue("fenzhi"));
				update.setWhereCondition("planid='" + id + "' and usercode='"
						+ pfVos[i].getStringValue("USERCODE") + "'");
				update.putFieldValue("koufen", fenzhi);
				update.putFieldValue("state", "���ֽ���");
				sqlList.add(update.getSQL());
			}
			dmo.executeBatchByDS(null, sqlList);
			result = "�ƻ������ɹ�";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * fj ũ������������ɱ�
	 * 
	 * @return
	 */

	@Override
	public String getNhjdHs(String date) {
		Double month = Double.parseDouble(date.substring(5, 7));
		String result = null;
		try {
			InsertSQLBuilder insert = new InsertSQLBuilder("WN_NHJD_WCB");
			List list = new ArrayList<String>();
			// �õ��ͻ������map
			HashMap<String, String> userMap = dmo
					.getHashMapBySQLByDS(
							null,
							"select name,name from v_sal_personinfo where stationkind in('�����ͻ�����','����ͻ�����','�����μ�ְ�ͻ�����','�������㸱����','�������㸱����')");
			// �õ��ͻ������������
			String sql = "select A,sum(R)*'" + month
					+ "' from EXCEL_TAB_53 where year||'-'||month='" + date
					+ "' group by A";
			HashMap<String, String> rwMap = dmo.getHashMapBySQLByDS(null, sql);
			if (rwMap.size() == 0) {
				return "��ǰָ��û���ϴ���������";
			}
			HashMap<String, String> map = getNhjdMap(date);
			for (String str : userMap.keySet()) {
				Double count = 0.0;
				Double rwcount = 0.0;
				if (map.get(str) == null || map.get(str).equals("")) {
					continue;
				} else {
					count = Double.parseDouble(map.get(str));
				}
				if (rwMap.get(str).equals("0") || rwMap.get(str) == null
						|| rwMap.get(str).equals("")) {
					continue;
				} else {
					rwcount = Double.parseDouble(rwMap.get(str));
				}
				insert.putFieldValue("name", str);
				System.out.println(str);
				insert.putFieldValue("passed", count);
				System.out.println(count);
				insert.putFieldValue("task", rwcount);
				System.out.println(rwcount);
				insert.putFieldValue("date_time", date);
				list.add(insert.getSQL());
			}
			dmo.executeBatchByDS(null, list);
			result = "��ѯ�ɹ���";
		} catch (Exception e) {
			e.printStackTrace();
			result = "��ѯʧ�ܣ�����ϵ����Ա��";
		}
		return result;
	}

	/**
	 * �½�ũ����������
	 * 
	 * @param date
	 * @return
	 */

	private HashMap<String, String> getNhjdMap(String date) {
		String year = date.substring(0, 4);
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select yb.xd_col2 xd_col2,zc.tj tj  from(select xx.xd_col96 xd_col96,count(xx.xd_col96) tj from (select XD_COL1,XD_COL96 from wnbank.s_loan_khxx where to_char(cast (cast (xd_col3 as timestamp) as date),'yyyy-mm')>='"
									+ year
									+ "-01' and  to_char(cast (cast (xd_col3 as timestamp) as date),'yyyy-mm')<='"
									+ date
									+ "'  and xd_col10 not in('δ����','����','!$') and XD_COL4='905') xx left join wnbank.S_LOAN_KHXXZCQK zc on xx.xd_col1=zc.xd_col1 where zc.XD_COL6='1' group by xx.xd_col96)zc left join wnbank.s_loan_ryb yb on zc.xd_col96=yb.xd_col1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Override
	public String getQnedXstd(String date) {
		String result = null;
		try {
			InsertSQLBuilder insert = new InsertSQLBuilder("WN_QNED_XSTD");
			List list = new ArrayList<String>();
			// �õ��ͻ������map
			HashMap<String, String> userMap = dmo
					.getHashMapBySQLByDS(
							null,
							"select name,name from v_sal_personinfo where stationkind in('�����ͻ�����','����ͻ�����','�����μ�ְ�ͻ�����','�������㸱����','�������㸱����')");
			// �õ��ͻ������������
			HashMap<String, String> rwMap = dmo.getHashMapBySQLByDS(null,
					"select A,sum(Q) from EXCEL_TAB_53 where year||'-'||month='"
							+ date.substring(0, 7) + "' group by A");
			if (rwMap.size() == 0) {
				return "��ǰʱ�䡾" + date + "��û���ϴ�������";
			}
			HashMap<String, String> khMap = getKhxsCount(date);
			HashMap<String, String> lastMap = getLastxsCount(date);
			for (String str : userMap.keySet()) {
				Double count = 0.0;
				Double khcount = 0.0;
				Double lastcount = 0.0;
				if (khMap.get(str) == null) {
					khcount = 0.0;
				} else {
					khcount = Double.parseDouble(khMap.get(str));
				}
				if (lastMap.get(str) == null) {
					lastcount = 0.0;
				} else {
					lastcount = Double.parseDouble(lastMap.get(str));
				}
				count = khcount - lastcount;
				insert.putFieldValue("name", str);
				insert.putFieldValue("last_passed", lastcount);
				insert.putFieldValue("passed", khcount);
				insert.putFieldValue("task", rwMap.get(str));
				insert.putFieldValue("time", date);
				list.add(insert.getSQL());
			}
			dmo.executeBatchByDS(null, list);
			result = "��ѯ�ɹ�";
		} catch (Exception e) {
			e.printStackTrace();
			result = "��ѯʧ��";
		}
		return result;
	}

	/**
	 * ǭũe���������ϻ���
	 * 
	 * @param date
	 * @return
	 */
	private HashMap<String, String> getLastxsCount(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select b.xd_col2 name,count(a.xd_col16) num from (select distinct(xd_col16) xd_col16,xd_col81 from wnbank1.s_loan_dk where  xd_col7>0 and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ getMonthC(getSMonthDate(date))
									+ "' and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ getSMonthDate(date)
									+ "' and xd_col86='x_wd') a left join wnbank1.s_loan_ryb b on b.xd_col1=a.xd_col81 group by b.xd_col2");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * ǭũe�����������ϻ���
	 * 
	 * @param date
	 * @return
	 */
	private HashMap<String, String> getKhxsCount(String date) {

		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select b.xd_col2 name,count(a.xd_col16) num from (select distinct(xd_col16) xd_col16,xd_col81 from wnbank1.s_loan_dk where  xd_col7>0 and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ getMonthC(date)
									+ "' and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ date
									+ "' and xd_col86='x_wd') a left join wnbank1.s_loan_ryb b on b.xd_col1=a.xd_col81 group by b.xd_col2");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;

	}

	/**
	 * fj [��λְ��С΢��ҵ����������ɱ�]
	 */

	@Override
	public String getDwzgXwqyRatio(String date) {
		String result = null;
		InsertSQLBuilder insert = new InsertSQLBuilder("wn_zgxw_wcb");
		List list = new ArrayList<String>();
		try {
			HashMap<String, String> userMap = dmo
					.getHashMapBySQLByDS(
							null,
							"select name,name from v_sal_personinfo where stationkind in('�����ͻ�����','����ͻ�����','�����μ�ְ�ͻ�����','�������㸱����','�������㸱����')");
			// �õ��ͻ������������
			HashMap<String, String> rwMap = dmo.getHashMapBySQLByDS(null,
					"select A,sum(R) from EXCEL_TAB_53 where year||'-'||month='"
							+ date.substring(0, 7) + "' group by A");
			if (rwMap.size() == 0) {
				return "��ǰʱ�䡾" + date + "��û���ϴ�������";
			}
			HashMap<String, String> dwzgMap = getDwzgCount(date);
			HashMap<String, String> xwqyMap = getXwqyCount(date);
			for (String str : userMap.keySet()) {
				Double dwzgcount = 0.0;
				Double xwqycount = 0.0;
				Double count = 0.0;
				if (dwzgMap.get(str) == null) {
					dwzgcount = 0.0;
				} else {
					dwzgcount = Double.parseDouble(dwzgMap.get(str));
				}
				if (xwqyMap.get(str) == null) {
					xwqycount = 0.0;
				} else {
					xwqycount = Double.parseDouble(xwqyMap.get(str));
				}
				count = dwzgcount + xwqycount;
				insert.putFieldValue("username", str);
				insert.putFieldValue("dwzgcount", dwzgcount);
				insert.putFieldValue("xwqycount", xwqycount);
				insert.putFieldValue("passed", count);
				insert.putFieldValue("task", rwMap.get(str));
				insert.putFieldValue("date_time", date);
				list.add(insert.getSQL());
			}
			dmo.executeBatchByDS(null, list);
			result = "��ѯ�ɹ�";
		} catch (Exception e) {
			e.printStackTrace();
			result = "��ѯʧ��";
		}
		return result;
	}

	/**
	 * �ͻ�����С΢��ҵ��������
	 * 
	 * @param date
	 * @return
	 */
	private HashMap<String, String> getXwqyCount(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select yb.xd_col2 xd_col2,xx.tj tj from (select XD_COL96 XD_COL96,count(XD_COL96) tj from wnbank.s_loan_khxx where to_char(cast (cast (xd_col3 as timestamp) as date),'yyyy-mm-dd')>='"
									+ getMonthC(date)
									+ "' and to_char(cast (cast (xd_col3 as timestamp) as date),'yyyy-mm-dd')<='"
									+ date
									+ "' and xd_col10 not in('δ����','����','!$') and XD_COL4='206' and XD_COL163 not in('05','03','06','01') group by XD_COL96)xx left join wnbank.s_loan_ryb yb on xx.xd_col96=yb.xd_col1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * �ͻ�����λְ����������
	 * 
	 * @param date
	 * @return
	 */

	private HashMap<String, String> getDwzgCount(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select yb.xd_col2 xd_col2,xx.tj tj from (select XD_COL96 XD_COL96,count(XD_COL96) tj from wnbank.s_loan_khxx where to_char(cast (cast (xd_col3 as timestamp) as date),'yyyy-mm-dd')>='"
									+ getMonthC(date)
									+ "'and to_char(cast (cast (xd_col3 as timestamp) as date),'yyyy-mm-dd')<='"
									+ date
									+ "'and xd_col10 not in('δ����','����','!$') and XD_COL4='907' group by XD_COL96)xx left join wnbank.s_loan_ryb yb on xx.xd_col96=yb.xd_col1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	@Override
	public String getGyClass(String date) throws Exception {
		List list = new ArrayList<String>();
		InsertSQLBuilder insert = new InsertSQLBuilder("WN_GYPJ");
		String result = null;
		String year = date.substring(0, 4);
		String month = date.substring(5, 7);
		String str = getAnnual(year, month);
		HashVO[] vos = dmo.getHashVoArrayByDS(null,
				"select * from V_WN_GYPJ where pjtime='" + str + "'");
		if (vos.length > 0) {
			result = "�ð�����ѿ��ˣ�������ѯ�鿴��";
		} else if (str == null) {
			result = "��ǰʱ�䲻�ڿ���ʱ�䷶Χ�ڣ�";
		} else {
			try {
				HashMap<String, String> userMap = dmo
						.getHashMapBySQLByDS(
								null,
								"select username,username from (select username,count(username) as c from WN_GYDX_TABLE where pftime>='"+year+"-01-01' and pftime<='"+year+"-06-31' and xiangmu='�ܷ�' group by username) where c=6");
				String userCount = dmo
						.getStringValueByDS(
								null,
								"select count(*) from (select username,username from (select username,count(username) as c from WN_GYDX_TABLE where pftime>='2020-01-01' and xiangmu='�ܷ�' group by username) where c=6)");
				HashMap<String, String> userDeptMap = dmo
						.getHashMapBySQLByDS(null,
								"select name,deptname from v_sal_personinfo where stationkind like '%��Ա%'");
				HashMap<String, String> wmgfMap = getWmgf(str, year);
				HashMap<String, String> gzzlMap = getGzzl(str, year);
				HashMap<String, String> wdpjzbMap = getWdpjzb(str, year);
				HashMap<String, String> qxpjzbMap = getQxpjzb(str, year);
				HashMap<String, String> ckzfMap = getCkzfzb(str, year);
				HashMap<String, String> dztdlMap = getDztdl(str, year);
				HashMap<String, String> wgjfMap = getWgjf(str, year);
				for (String user : userMap.keySet()) {
					Double wmgfcount = 0.0;
					Double gzzlcount = 0.0;
					Double wdpjcount = 0.0;
					Double qxpjcount = 0.0;
					Double ckzfcount = 0.0;
					Double dztdlcount = 0.0;
					Double wgjfcount = 0.0;
					Double count = 0.0;
					if (wmgfMap.get(user) == null) {
						wmgfcount = 0.0;
					} else {
						wmgfcount = Double.parseDouble(wmgfMap.get(user));
					}
					if (gzzlMap.get(user) == null) {
						gzzlcount = 0.0;
					} else {
						gzzlcount = Double.parseDouble(gzzlMap.get(user));
					}
					if (wdpjzbMap.get(user) == null) {
						wdpjcount = 0.0;
					} else {
						wdpjcount = Double.parseDouble(wdpjzbMap.get(user));
					}
					if (qxpjzbMap.get(user) == null) {
						qxpjcount = 0.0;
					} else {
						qxpjcount = Double.parseDouble(qxpjzbMap.get(user));
					}
					if (ckzfMap.get(user) == null
							|| ckzfMap.get(user).equals("")) {
						ckzfcount = 0.0;
					} else {
						ckzfcount = Double.parseDouble(ckzfMap.get(user));
					}
					if (dztdlMap.get(user) == null) {
						dztdlcount = 0.0;
					} else {
						dztdlcount = Double.parseDouble(dztdlMap.get(user));
					}
					if (wgjfMap.get(user) == null) {
						wgjfcount = 0.0;
					} else {
						wgjfcount = Double.parseDouble(wgjfMap.get(user));
					}
					count = wmgfcount + gzzlcount + wdpjcount
							+ qxpjcount + ckzfcount + dztdlcount + wgjfcount;
					insert.putFieldValue("name", user);
					insert.putFieldValue("wdmc", userDeptMap.get(user));
					insert.putFieldValue("pjtime", str);
					insert.putFieldValue("wmgf", wmgfcount);
					insert.putFieldValue("gzzl", gzzlcount);
					insert.putFieldValue("szwd", wdpjcount);
					insert.putFieldValue("qxgy", qxpjcount);
					insert.putFieldValue("ckzf", ckzfcount);
					insert.putFieldValue("dztdl", dztdlcount);
					insert.putFieldValue("wgjf", wgjfcount);
					insert.putFieldValue("hjdf", count);
					insert.putFieldValue("khrs", userCount);
					list.add(insert.getSQL());
				}
				dmo.executeBatchByDS(null, list);
				result = "��Ա�����ɹ�,������ѯ�鿴��";
			} catch (Exception e) {
				e.printStackTrace();
				result = "��Ա����ʧ�ܣ�����ϵϵͳ����Ա��";
			}
		}
		return result;
	}

	@Override
	public String getWpkjClass(String date) throws Exception {
		List list = new ArrayList<String>();
		InsertSQLBuilder insert = new InsertSQLBuilder("wn_wpkj_pj");
		String result = null;
		String year = date.substring(0, 4);
		String month = date.substring(5, 7);
		String str = getAnnual(year, month);
		HashVO[] vos = dmo.getHashVoArrayByDS(null,
				"select * from V_WN_WPKJ_PJ where pjtime='" + str + "'");
		if (vos.length > 0) {
			result = "�ð�����ѿ��ˣ�������ѯ�鿴��";
		} else if (str == null) {
			result = "��ǰʱ�䲻�ڿ���ʱ�䷶Χ�ڣ�";
		}
		try {
			HashMap<String, String> userMap = dmo.
					getHashMapBySQLByDS(null,
					"select name,name from (select * from (select a.name,b.c from (select name from v_sal_personinfo where stationkind like '%ί�ɻ��%' and deptname not in ('Ӫҵ��')) a left join (select b,count(b) as c from (select * from  excel_tab_115 where d like '%ί�ɻ��%') group by b) b on a.name=b.b) where c>=2)");
			HashMap<String, String> bcyuserMap = dmo.
					getHashMapBySQLByDS(null, 
					"select name,name from (select * from (select name from (select a.name,b.c from (select name from v_sal_personinfo where stationkind like '%ί�ɻ��%' and deptname not in ('Ӫҵ��')) a left join (select b,count(b) as c from (select * from  excel_tab_115 where d like '%ί�ɻ��%') group by b) b on a.name=b.b) where c<2 or c is null) union all ( select name from v_sal_personinfo where stationkind like '%ί�ɻ��%' and deptname in ('Ӫҵ��')))");
			String userCount = dmo.
					getStringValueByDS(null,
					"select count(*) from (select a.name,b.c from (select name from v_sal_personinfo where stationkind like '%ί�ɻ��%' and deptname not in ('Ӫҵ��')) a left join (select b,count(b) as c from (select * from  excel_tab_115 where d like '%ί�ɻ��%') group by b) b on a.name=b.b) where c>=2");// ��ȡ����������
			HashMap<String, String> userDeptMap = dmo
					.getHashMapBySQLByDS(null,
					"select username,deptname from v_pub_user_post_1 where postname='ί�ɻ��'");
			HashMap<String, String> xcdfMap = dmo.getHashMapBySQLByDS(null, "select a.name,b.b/100*50 from (select name,deptname from v_sal_personinfo where stationkind like '%ί�ɻ��%' and  isuncheck = 'N') a, excel_tab_119 b where a.deptname=b.a");
			HashMap<String, String> ypjfMap = dmo.getHashMapBySQLByDS(null, "select a.name,b.c/6/100*30 as c from (select name,deptname from v_sal_personinfo where stationkind like '%ί�ɻ��%' and  isuncheck='N') a,(select deptname,sum(b) c from (select a.name deptname,b.b from (select name,shortname from pub_corp_dept) a,excel_tab_118 b where instr(b.a,a.shortname)>0) group by deptname) b where a.deptname=b.deptname");
			HashMap<String, String> jzksMap = dmo.getHashMapBySQLByDS(null, "select a,b/100*20 from excel_tab_120");
			HashMap<String, String> wgjfMap = dmo.getHashMapBySQLByDS(null, "select c,e from excel_tab_112 ");
			for (String user : userMap.keySet()) {
				Double xcdf = 0.0;// �ֳ����
				Double ypjf = 0.0;// ��ƽ�����
				Double jzks = 0.0;// ���п���
				Double wgjf = 0.0;// Υ�����
				Double count = 0.0;
				String ifsort = null;
				if (xcdfMap.get(user) == null) {
					xcdf = 0.0;
				} else {
					xcdf = Double.parseDouble(xcdfMap.get(user));
				}
				if (ypjfMap.get(user) == null) {
					ypjf = 0.0;
				} else {
					ypjf = Double.parseDouble(ypjfMap.get(user));
				}
				if (jzksMap.get(user) == null) {
					jzks = 0.0;
				} else {
					jzks = Double.parseDouble(jzksMap.get(user));
				}
				if (wgjfMap.get(user) == null) {
					wgjf = 0.0;
				} else {
					wgjf = Double.parseDouble(wgjfMap.get(user));
				}
				count = xcdf + ypjf + jzks + wgjf;
				BigDecimal bxcdf = new BigDecimal(count);
				count = bxcdf.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
				insert.putFieldValue("name", user);
				insert.putFieldValue("wdmc", userDeptMap.get(user));
				insert.putFieldValue("pjtime", str);
				insert.putFieldValue("xcdf", xcdf);
				insert.putFieldValue("ypjf", ypjf);
				insert.putFieldValue("jzks", jzks);
				insert.putFieldValue("wgjf", wgjf);
				insert.putFieldValue("count",count );
				insert.putFieldValue("khrs", userCount);
				list.add(insert.getSQL());
			}
			for (String user : bcyuserMap.keySet()) {
				Double xcdf = 0.0;// �ֳ����
				Double ypjf = 0.0;// ��ƽ�����
				Double jzks = 0.0;// ���п���
				Double wgjf = 0.0;
				Double count = 0.0;
				String ifsort = null;
				if (xcdfMap.get(user) == null) {
					xcdf = 0.0;
				} else {
					xcdf = Double.parseDouble(xcdfMap.get(user));
				}
				if (ypjfMap.get(user) == null) {
					ypjf = 0.0;
				} else {
					ypjf = Double.parseDouble(ypjfMap.get(user));
				}
				if (jzksMap.get(user) == null) {
					jzks = 0.0;
				} else {
					jzks = Double.parseDouble(jzksMap.get(user));
				}
				if (wgjfMap.get(user) == null) {
					wgjf = 0.0;
				} else {
					wgjf = Double.parseDouble(wgjfMap.get(user));
				}
				insert.putFieldValue("name", user);
				insert.putFieldValue("wdmc", userDeptMap.get(user));
				insert.putFieldValue("pjtime", str);
				insert.putFieldValue("xcdf", xcdf);
				insert.putFieldValue("ypjf", ypjf);
				insert.putFieldValue("jzks", jzks);
				insert.putFieldValue("wgjf", wgjf);
				if(userDeptMap.get(user).equals("Ӫҵ��")){
					insert.putFieldValue("count", "Ӫҵ��");
				}else{
					insert.putFieldValue("count", "����������");					
				}

				insert.putFieldValue("khrs", "--");
				list.add(insert.getSQL());
			}
			dmo.executeBatchByDS(null, list);
			result = "�����ɹ�!������ѯ�鿴";
		} catch (Exception e) {
			e.printStackTrace();
			result = "����ʧ�ܣ�����ϵ����Ա��";
		}
		return result;
	}

	/**
	 * ��Ա�ۼ�Υ�����
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	private HashMap<String, String> getWgjf(String str, String year) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			if(str.contains("��")){
				map = dmo.getHashMapBySQLByDS(null, "select c,ROUND((0-sum(e)),2) from excel_tab_112 where year||'-'||month='"+year+"-03' or year||'-'||month='"+year+"-06' group by c");	
			}else if(str.contains("��")){
				map = dmo.getHashMapBySQLByDS(null, "select C,ROUND((0-sum(e)),2) from excel_tab_112 where year||'-'||month='"+String.valueOf((Integer.valueOf(year) - 1))+"-09' or year||'-'||month='"+String.valueOf((Integer.valueOf(year) - 1))+"-12' group by c");		
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * ��Ա����������������
	 * 
	 * @param str
	 * @param year
	 * @return
	 */
	private HashMap<String, String> getDztdl(String str, String year) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			if(str.contains("��")){
				map = dmo
						.getHashMapBySQLByDS(
								null,
								"select a.name,ROUND(b.m/100*20,2) as rate from (select a.name,b.shortname from (select name,deptname from v_sal_personinfo  where stationkind like '%��Ա%' and isuncheck='N' and name in (select username from (select username,count(username) as c from WN_GYDX_TABLE where pftime>='"+year+"-01-01' and pftime<='"+year+"-06-31' and xiangmu='�ܷ�' group by username) where c=6)) a,(select name,shortname from pub_corp_dept where  shortname is not null) b where a.deptname=b.name) a,(select b,m from excel_tab_111 where year||'-'||month='"+year+"-06') b where instr(b.b,a.shortname)>0");
			}else if(str.contains("��")){
				map = dmo
						.getHashMapBySQLByDS(
								null,
								"select a.name,ROUND(b.m/100*20,2) as rate from (select a.name,b.shortname from (select name,deptname from v_sal_personinfo  where stationkind like '%��Ա%' and isuncheck='N' and name in (select username from (select username,count(username) as c from WN_GYDX_TABLE where pftime>='"+String.valueOf((Integer.valueOf(year) - 1))+"-07-01' and pftime<='"+String.valueOf((Integer.valueOf(year) - 1))+"-12-31' and xiangmu='�ܷ�' group by username) where c=6)) a,(select name,shortname from pub_corp_dept where  shortname is not null) b where a.deptname=b.name) a,(select b,m from excel_tab_111 where year||'-'||month='"+String.valueOf((Integer.valueOf(year) - 1))+"-12') b where instr(b.b,a.shortname)>0");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * �������������������
	 * 
	 * @param str
	 * @param year
	 * @return
	 * 
	 */
	private HashMap<String, String> getCkzfzb(String str, String year) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			if (str.contains("��")) {
				map = dmo
						.getHashMapBySQLByDS(
								null,
								"select name,case when rate>20 then 20 when rate <0 then 0 else rate end as rate from (select a.name,ROUND(b.c*20,2) as rate from (select a.name,b.shortname from (select name,deptname from v_sal_personinfo where stationkind like '%��Ա%' and isuncheck='N' and name in (select username from (select username,count(username) as c from WN_GYDX_TABLE where pftime>='"+year+"-01-01' and pftime<='"+year+"-06-31' and xiangmu='�ܷ�' group by username) where c=6)) a ,(select name,shortname from pub_corp_dept where shortname is not null) b where a.deptname=b.name) a,(select A,F/B as c from excel_tab_9 where year||'-'||month='"+year+"-06' order by A) b where instr(b.a,a.shortname)>0)");
			} else if (str.contains("��")) {
				map = dmo
						.getHashMapBySQLByDS(
								null,
								"select name,case when rate>20 then 20 when rate <0 then 0 else rate end as rate from (select a.name,ROUND(b.c*20,2) as rate from (select a.name,b.shortname from (select name,deptname from v_sal_personinfo where stationkind like '%��Ա%' and isuncheck='N' and name in (select username from (select username,count(username) as c from WN_GYDX_TABLE where pftime>='"+String.valueOf((Integer.valueOf(year) - 1))+"-07-01' and pftime<='"+String.valueOf((Integer.valueOf(year) - 1))+"-12-31' and xiangmu='�ܷ�' group by username) where c=6)) a ,(select name,shortname from pub_corp_dept where shortname is not null) b where a.deptname=b.name) a,(select  A.A,(A.C-B.C)/B.C as c from (select A,C from excel_tab_9 where year||'-'||month='"+String.valueOf((Integer.valueOf(year) - 1))+"-06') b left join (select A,C from excel_tab_9 where year||'-'||month='"+String.valueOf((Integer.valueOf(year) - 1))+"-12') a on a.A=b.A) b where instr(b.a,a.shortname)>0)");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return map;
	}

	/**
	 * ��ƽ������ռȫϽ��Աƽ����������ռ��
	 * 
	 * @param date
	 * @param year
	 * @return
	 */
	private HashMap<String, String> getQxpjzb(String str, String year) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			if(str.contains("��")){
				map = dmo
						.getHashMapBySQLByDS(
								null,
								"select b name,case when rate>15 then 15  else rate end as rate from (select a.b,ROUND(a.c/b.avg*15,2) as rate from (select b,sum(d)/6 as c from excel_tab_57 where year='"+year+"' and month in ('01','02','03','04','05','06') and b in (select username from (select username,count(username) as c from WN_GYDX_TABLE where pftime>='"+year+"-01-01' and pftime<='"+year+"-06-31' and xiangmu='�ܷ�' group by username) where c=6) group by b) a,(select a.c/b.c as avg  from (select sum(d)/6 as c from excel_tab_57 where year='"+year+"' and month in ('01','02','03','04','05','06') and b in (select username from (select username,count(username) as c from WN_GYDX_TABLE where pftime>='"+year+"-01-01' and pftime<='"+year+"-06-31' and xiangmu='�ܷ�' group by username) where c=6))a,(select count(*) as c from (select username,count(username) as c from WN_GYDX_TABLE where pftime>='"+year+"-01-01' and pftime<='"+year+"-06-31' and xiangmu='�ܷ�' group by username) where c=6) b) b)");
			}else if(str.contains("��")){
				map = dmo
						.getHashMapBySQLByDS(
								null,
								"select b name,case when rate>15 then 15  else rate end as rate from (select a.b,ROUND(a.c/b.avg*15,2) as rate from (select b,sum(d)/6 as c from excel_tab_57 where year='"+String.valueOf((Integer.valueOf(year) - 1))+"' and month in ('07','08','09','10','11','12') and b in (select username from (select username,count(username) as c from WN_GYDX_TABLE where pftime>='"+String.valueOf((Integer.valueOf(year) - 1))+"-07-01' and pftime<='"+String.valueOf((Integer.valueOf(year) - 1))+"-12-31' and xiangmu='�ܷ�' group by username) where c=6) group by b) a,(select a.c/b.c as avg  from (select sum(d)/6 as c from excel_tab_57 where year='"+String.valueOf((Integer.valueOf(year) - 1))+"' and month in ('07','08','09','10','11','12') and b in (select username from (select username,count(username) as c from WN_GYDX_TABLE where pftime>='"+String.valueOf((Integer.valueOf(year) - 1))+"-07-01' and pftime<='"+String.valueOf((Integer.valueOf(year) - 1))+"-12-31' and xiangmu='�ܷ�' group by username) where c=6))a,(select count(*) as c from (select username,count(username) as c from WN_GYDX_TABLE where pftime>='"+String.valueOf((Integer.valueOf(year) - 1))+"-07-01' and pftime<='"+String.valueOf((Integer.valueOf(year) - 1))+"-12-31' and xiangmu='�ܷ�' group by username) where c=6) b) b)");
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return map;
	}

	/**
	 * ��ƽ������ռ���������Աƽ���������˹���ռ��
	 * 
	 * @param date
	 * @param year
	 * @return
	 */
	private HashMap<String, String> getWdpjzb(String str, String year) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			if (str.contains("��")) {
				map = dmo
						.getHashMapBySQLByDS(
								null,
								"select B,case when rate>15 then 15 else rate end as rate from (select a.b,ROUND((a.avg/b.s)*15,2) as rate from (select a.b,b.deptname,a.avg from (select b,sum(d)/6 as avg from excel_tab_57 where year='"+year+"' and month in ('01','02','03','04','05','06') and b in (select username from (select username,count(username) as c from WN_GYDX_TABLE where pftime>='"+year+"-01-01' and pftime<='"+year+"-06-31' and xiangmu='�ܷ�' group by username) where c=6) group by b order by sum(d)/6) a,(select name,deptname from v_sal_personinfo where stationkind like '%��Ա%' and isuncheck='N') b where a.b=b.name) a,(select a.c,a.sum/b.count as s from (select c,sum(d) as sum from excel_tab_57 where year='"+year+"' and month in ('01','02','03','04','05','06') and b in (select username from (select username,count(username) as c from WN_GYDX_TABLE where pftime>='"+year+"-01-01' and pftime<='"+year+"-06-31' and xiangmu='�ܷ�' group by username) where c=6) group by c) a left join (select c,count(c) as count from excel_tab_57 where year='"+year+"' and month in ('01','02','03','04','05','06') and b in (select username from (select username,count(username) as c from WN_GYDX_TABLE where pftime>='"+year+"-01-01' and pftime<='"+year+"-06-31' and xiangmu='�ܷ�' group by username) where c=6) group by c) b on a.c=b.c) b where a.deptname=b.c order by a.b)");
			}else if(str.contains("��")){
				map = dmo
						.getHashMapBySQLByDS(
								null,
								"select B,case when rate>15 then 15 else rate end as rate from (select a.b,ROUND((a.avg/b.s)*15,2) as rate from (select a.b,b.deptname,a.avg from (select b,sum(d)/6 as avg from excel_tab_57 where year='"+String.valueOf((Integer.valueOf(year) - 1))+"' and month in ('07','08','09','10','11','12') and b in (select username from (select username,count(username) as c from WN_GYDX_TABLE where pftime>='"+String.valueOf((Integer.valueOf(year) - 1))+"-07-01' and pftime<='"+String.valueOf((Integer.valueOf(year) - 1))+"-12-31' and xiangmu='�ܷ�' group by username) where c=6) group by b order by sum(d)/6) a,(select name,deptname from v_sal_personinfo where stationkind like '%��Ա%' and isuncheck='N') b where a.b=b.name) a,(select a.c,a.sum/b.count as s from (select c,sum(d) as sum from excel_tab_57 where year='"+String.valueOf((Integer.valueOf(year) - 1))+"' and month in ('07','08','09','10','11','12') and b in (select username from (select username,count(username) as c from WN_GYDX_TABLE where pftime>='"+String.valueOf((Integer.valueOf(year) - 1))+"-07-01' and pftime<='"+String.valueOf((Integer.valueOf(year) - 1))+"-12-31' and xiangmu='�ܷ�' group by username) where c=6) group by c) a left join (select c,count(c) as count from excel_tab_57 where year='"+String.valueOf((Integer.valueOf(year) - 1))+"' and month in ('07','08','09','10','11','12') and b in (select username from (select username,count(username) as c from WN_GYDX_TABLE where pftime>='"+String.valueOf((Integer.valueOf(year) - 1))+"-07-01' and pftime<='"+String.valueOf((Integer.valueOf(year) - 1))+"-12-31' and xiangmu='�ܷ�' group by username) where c=6) group by c) b on a.c=b.c) b where a.deptname=b.c order by a.b)");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return map;
	}

	/**
	 * ��Ա���������÷�ƽ����
	 * 
	 * @param str
	 * @param year
	 * @return
	 * over
	 */
	private HashMap<String, String> getGzzl(String str, String year) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			if (str.contains("��")) {
				map = dmo.getHashMapBySQLByDS(null,
						"select name,ROUND(sum(score)/6/100*10,2) as c from (select username name,fenzhi score from WN_GYDX_TABLE where xiangmu='�ܷ�' and pftime>='"+year+"-01-01' and pftime<='"+year+"-06-31') group by name");

			} else if (str.contains("��")) {
				map = dmo.getHashMapBySQLByDS(
						null,
						"select name,ROUND(sum(score)/6/100*10,2) as c from (select username name,fenzhi score from WN_GYDX_TABLE where xiangmu='�ܷ�' and pftime>='"+String.valueOf((Integer.valueOf(year) - 1))+"-07-01' and pftime<='"+String.valueOf((Integer.valueOf(year) - 1))+"-12-31') group by name");
			}
		} catch (Exception e) {

		}
		return map;
	}

	/**
	 * ��Ա���������÷�ƽ����
	 * 
	 * @param str
	 * @param year
	 * @return
	 */

//	private HashMap<String, String> getFwzl(String str, String year) {
//		HashMap<String, String> map = new HashMap<String, String>();
//		try {
//			if (str.contains("��")) {
//				// map =
//				// dmo.getHashMapBySQLByDS(null,"select a.B,(a.C+b.C)/24/100*10 as c from (select B,sum(F) as c from (select b,f from excel_tab_39 where year||'-'||month='"+year+"-01' or year||'-'||month='"+year+"-02' or year||'-'||month='"+year+"-03' or year||'-'||month='"+year+"-04' or year||'-'||month='"+year+"-05' or year||'-'||month='"+year+"-06') group by B) a,(select username username,sum(kouofen) as c from WN_GYPF_TABLE  where xiangmu ='�ܷ�' and pftime>='"+year+"-01-01' and pftime<='"+year+"-06-30' group by username) b where a.B=b.username");
//				map = dmo.getHashMapBySQLByDS(null,
//						"select B,sum(f)/6/100*10 as c from excel_tab_39 where year||'-'||month='"
//								+ year + "-01' or year||'-'||month='" + year
//								+ "-02' or year||'-'||month='" + year
//								+ "-03' or year||'-'||month='" + year
//								+ "-04' or year||'-'||month='" + year
//								+ "-05' or year||'-'||month='" + year
//								+ "-06' group by B");
//			} else if (str.contains("��")) {
//				// map =
//				// dmo.getHashMapBySQLByDS(null,"select a.B,(a.C+b.C)/24/100*10 as c from (select B,sum(F) as c from (select b,f from excel_tab_39 where year||'-'||month='"+String.valueOf((Integer.valueOf(year)-1))+"-07' or year||'-'||month='"+String.valueOf((Integer.valueOf(year)-1))+"-08' or year||'-'||month='"+String.valueOf((Integer.valueOf(year)-1))+"-09' or year||'-'||month='"+String.valueOf((Integer.valueOf(year)-1))+"-10' or year||'-'||month='"+String.valueOf((Integer.valueOf(year)-1))+"-11' or year||'-'||month='"+String.valueOf((Integer.valueOf(year)-1))+"-12') group by B) a,(select username username,sum(kouofen) as c from WN_GYPF_TABLE  where xiangmu ='�ܷ�' and pftime>='"+String.valueOf((Integer.valueOf(year)-1))+"-07-01' and pftime<='"+String.valueOf((Integer.valueOf(year)-1))+"-12-31' group by username) b where a.B=b.username");
//				map = dmo.getHashMapBySQLByDS(
//						null,
//						"select B,sum(f)/6/100*10 as c from excel_tab_39 where year||'-'||month='"
//								+ String.valueOf((Integer.valueOf(year) - 1))
//								+ "-07' or year||'-'||month='"
//								+ String.valueOf((Integer.valueOf(year) - 1))
//								+ "-08' or year||'-'||month='"
//								+ String.valueOf((Integer.valueOf(year) - 1))
//								+ "-09' or year||'-'||month='"
//								+ String.valueOf((Integer.valueOf(year) - 1))
//								+ "-10' or year||'-'||month='"
//								+ String.valueOf((Integer.valueOf(year) - 1))
//								+ "-11' or year||'-'||month='"
//								+ String.valueOf((Integer.valueOf(year) - 1))
//								+ "-12' group by B");
//			}
//		} catch (Exception e) {
//
//		}
//		return map;
//	}

	/**
	 * �������������淶���������ƽ����
	 * 
	 * @param str
	 * @param year
	 * @return
	 * over
	 */

	private HashMap<String, String> getWmgf(String str, String year) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			if (str.contains("��")) {
				map = dmo
						.getHashMapBySQLByDS(
								null,
								"select a.name,ROUND(b.c/100*20,2) as c from (select name,deptname from v_sal_personinfo where stationkind like '%��Ա%' and isuncheck='N') a,(select a.name,b.c from pub_corp_dept a, (select b,sum(c)/2 as c from excel_tab_116 where year||'-'||month='"+year+"-03' or year||'-'||month='"+year+"-06' group by b) b where instr(b.b,a.shortname)>0) b where a.deptname=b.name");
			} else if (str.contains("��")) {
				map = dmo
						.getHashMapBySQLByDS(
								null,
								"select a.name,ROUND(b.c/100*20,2) as c from (select name,deptname from v_sal_personinfo where stationkind like '%��Ա%' and isuncheck='N') a,(select a.name,b.c from pub_corp_dept a, (select b,sum(c)/2 as c from excel_tab_116 where year||'-'||month='"+String.valueOf((Integer.valueOf(year) - 1))+"-09' or year||'-'||month='"+String.valueOf((Integer.valueOf(year) - 1))+"-12' group by b) b where instr(b.b,a.shortname)>0) b where a.deptname=b.name");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * ��ȡ���
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	private String getAnnual(String year, String month) {
		String result = null;
		Integer years;
		String year1;
		if (month.equals("07")) {
			result = year + "���ϰ���";
		} else if (month == "01") {
			years = Integer.valueOf(year) - 1;
			year1 = String.valueOf(years);
			result = year1 + "���°���";
		} else {
			result = null;
		}
		return result;
	}

	/**
	 * ZPY �ͻ�����ȼ���������(�ͻ��������һ����)
	 * 
	 * @return
	 */
	@Override
	public String managerLevelCompute(int dateNum) {
		try {
			InsertSQLBuilder insert = new InsertSQLBuilder("WN_MANAGERDXSCORE");
			HashVO[] managerInfos = UIUtil
					.getHashVoArrayByDS(
							null,
							"SELECT CODE,NAME,STATIONKIND,DEPTNAME FROM V_SAL_PERSONINFO WHERE STATIONKIND LIKE '%�ͻ�����%'");
			// ��ȡ�ͻ�����ÿһ��˵÷����
			HashMap<String, Double> dqdkshlMap = getDqdkshl(dateNum);// ���ڴ����ջ��ʿ���
			HashMap<String, Double> dnxzbldkMap = getDnxzbldk(dateNum);// ����������������
			HashMap<String, Double> shclMap = getshcl(dateNum);// �ջش�����������
			HashMap<String, Double> dKsjyhMap = getDKsjyh(dateNum);// �����ֻ����пͻ�������
																	// ����ok
			HashMap<String, Double> znshMap = getZnsh(dateNum);// ��ũ�̻�ά�� ����ok
			HashMap<String, Double> cKyxhsMap = getCKyxhs(dateNum);// �����Ч��������
																	// ����ok
			HashMap<String, Double> cKyxhsyeMap = getCKyxhsye(dateNum);// �����Ч�����������
																		// ����ok
			HashMap<String, Double> dKyexzMap = getDKyexz(dateNum);// �����������
																	// ����ok
			HashMap<String, Double> dKhsxzMap = getDKhsxz(dateNum);// ���������
																	// ����ok
			HashMap<String, Double> jdRateMap = jdRate(dateNum);// �½����� ����ok
			HashMap<String, Double> qnedRateMap = qnedRate(dateNum);// ǭũE��ǩԼ
																	// ����ok
			HashMap<String, Double> qnedtdScoreMap = qnedtdScore(dateNum);// ǭũE���������
																			// ����ok
			HashMap<String, Double> nmgCountScoreMap = nmgCountScore(dateNum);// ũ����Ϣ�ɼ�
																				// ����ok
			double sum = 0;
			DecimalFormat decimal = new DecimalFormat("#.00");
			List<String> _sqlList = new ArrayList<String>();
			double dqdkshl, dnxzbldk, shcl, dKsjyh, znsh, cKyxhs, cKyxhsye, dKyexz, dKhsxz, jdRate, qnedRate, qnedtdScore, nmgCountScore;
			for (int i = 0; i < managerInfos.length; i++) {
				String manager_name = managerInfos[i].getStringValue("name");// ��ȡ���ͻ���������
				dqdkshl = dqdkshlMap.get(manager_name) == null ? 0 : dqdkshlMap
						.get(manager_name);// ���ڴ����ջ���
				dnxzbldk = dnxzbldkMap.get(manager_name) == null ? 0
						: dnxzbldkMap.get(manager_name);// �����������������
				shcl = shclMap.get(manager_name) == null ? 0.0 : shclMap
						.get(manager_name);// �ջش���
				dKsjyh = dKsjyhMap.get(manager_name) == null ? 0 : dKsjyhMap
						.get(manager_name);// �����ֻ����пͻ�������
				znsh = znshMap.get(manager_name) == null ? 0 : znshMap
						.get(manager_name);// ��ũ�̻�ά��
				cKyxhs = cKyxhsMap.get(manager_name) == null ? 0 : cKyxhsMap
						.get(manager_name);// �����Ч��������
				cKyxhsye = cKyxhsyeMap.get(manager_name) == null ? 0
						: cKyxhsyeMap.get(manager_name);// �����Ч�����������
				dKyexz = dKyexzMap.get(manager_name) == null ? 0 : dKyexzMap
						.get(manager_name);// �����������
				dKhsxz = dKhsxzMap.get(manager_name) == null ? 0 : dKhsxzMap
						.get(manager_name);// ���������
				jdRate = jdRateMap.get(manager_name) == null ? 0 : jdRateMap
						.get(manager_name);// ��������
				qnedRate = qnedRateMap.get(manager_name) == null ? 0
						: qnedRateMap.get(manager_name);// ǭũE��ǩԼ
				qnedtdScore = qnedtdScoreMap.get(manager_name) == null ? 0
						: qnedtdScoreMap.get(manager_name);
				nmgCountScore = nmgCountScoreMap.get(manager_name) == null ? 0
						: nmgCountScoreMap.get(manager_name);
				sum = dqdkshl + dnxzbldk + shcl + dKsjyh + znsh + cKyxhs
						+ cKyxhsye + dKyexz + dKhsxz + jdRate + qnedRate
						+ qnedtdScore + nmgCountScore;
				insert.putFieldValue("MANAEGER_NAME", manager_name);
				insert.putFieldValue("DQDKSHL", dqdkshl);// ���ڴ����ջ���
				insert.putFieldValue("DNXZBLDK", dnxzbldk);// ����������������
				insert.putFieldValue("SHCLBLDK", shcl);// �ջش�����������
				insert.putFieldValue("DKKHSJFGL", dKsjyh);// �����ֻ����пͻ�������
				insert.putFieldValue("ZNSHWH", znsh);// ��ũ�̻�ά��
				insert.putFieldValue("CKYXHS", cKyxhs);// �����Ч��������
				insert.putFieldValue("CKYXHSTS", cKyxhsye);
				insert.putFieldValue("DKYEXZ", dKyexz);// �����������
				insert.putFieldValue("DKHSXZ", dKhsxz);// ���������
				insert.putFieldValue("XJDA", jdRate);// ������������
				insert.putFieldValue("QNEDQY", qnedRate);// ǭũE��ǩԼ
				insert.putFieldValue("QNEDXSTD", qnedtdScore);// ǭũE���������
				insert.putFieldValue("MANAGER_LEVEL",
						nmgCountScoreMap.get(manager_name));// ǭũE���������
				insert.putFieldValue("SCORESUM",
						Double.parseDouble(decimal.format(sum)));// �ܷ�
				insert.putFieldValue("DATE_TIME", new SimpleDateFormat(
						"yyyy-MM-dd").format(new Date()));
				insert.putFieldValue("WN_CORP",
						managerInfos[i].getStringValue("deptname"));
				_sqlList.add(insert.getSQL());
			}
			UIUtil.executeBatchByDS(null, _sqlList);
		} catch (Exception e) {
			e.printStackTrace();
			return "�ͻ�����ȼ�������ʧ��";
		}
		return "�ͻ�����ȼ�����ɹ�";
	}

	private HashMap<String, Double> nmgCountScore(int dateNum) {
		HashMap<String, Double> resultMap = new HashMap<String, Double>();
		try {
			HashMap<String, String> manager_names = UIUtil
					.getHashMapBySQLByDS(
							null,
							"SELECT NAME,STATIONKIND FROM V_SAL_PERSONINFO  WHERE STATIONKIND LIKE '%�ͻ�����%'");
			String date_time = getYearStartAndEnd(dateNum).get(1);
			HashMap<String, String> taskNums = UIUtil.getHashMapBySQLByDS(null,
					"select a,sum(s) from excel_tab_53 where year='"
							+ date_time.substring(0, 4) + "' group by a");
			HashMap<String, String> numMap = UIUtil
					.getHashMapBySQLByDS(
							null,
							"select j,count(a) from excel_tab_66 where  year='"
									+ date_time.substring(0, 4)
									+ "' and  b is not null and  d is not null and e is not null and  g is not null and h is not null  group by j ");
			Set<String> managerSet = manager_names.keySet();
			DecimalFormat format = new DecimalFormat("#.00");
			double score = 0.0;
			for (String manager_name : managerSet) {
				String manager_type = manager_names.get(manager_name);
				if ("�����ͻ�����".equals(manager_type)) {
					resultMap.put(manager_name, 0.0);
					continue;
				}
				double task = Double
						.parseDouble(taskNums.get(manager_name) == null ? "0"
								: taskNums.get(manager_name));
				double num = Double
						.parseDouble(numMap.get(manager_name) == null ? "0"
								: numMap.get(manager_name));
				System.out.println("�ͻ�����:" + manager_name + ",ũ����Ϣ�ɼ�:ʵ�����"
						+ num + ",��������:" + task);
				if (task <= 0 || num <= 0) {
					resultMap.put(manager_name, 0.0);
				} else {
					score = Double.parseDouble(format.format(num / task * 1.0));
					resultMap.put(manager_name, score >= 1.0 ? 1.0 : score);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	// ���ڴ����ջ��ʿ���
	/**
	 * �ͻ�����֮���ڴ����ջ��ʿ��� Ϊ�˷�����ԣ�dmoִ�����׳��ֿ�ָ���쳣����ʱʹ��dmo�� ����������:5
	 * 
	 * @param dateNum
	 * @return
	 */
	public HashMap<String, Double> getDqdkshl(int dateNum) {
		HashMap<String, Double> resultMap = new HashMap<String, Double>();
		try {
			// ���ڴ����ջ��ʿ������Ȼ�ȡ������ʱ��
			String yearStartDate = getYearStartAndEnd(dateNum).get(0);// ��ȡ���������
			String checkDate = getYearStartAndEnd(dateNum).get(1);// ��ȡ����ĩ��������
			// 1-3�·��ۼ����ջ�
			HashMap<String, String> dsMonth1_3Map = UIUtil.getHashMapBySQLByDS(
					null, "select B,D from excel_tab_44");
			// ��ȡ����˽�����ջص��ڴ����
			HashMap<String, String> dsBackUpMoneyMap = UIUtil
					.getHashMapBySQLByDS(
							null,
							"select bb.xd_col2 xd_col2,zj.hksum zh from(select ye.xd_col81,sum(ye.hksum) hksum from(select dk.xd_col1 xd_col1,dk.xd_col81 xd_col81,sum(dk.XD_COL6) XD_COL6,sum(hk.hksum) hksum from(select XD_COL1 hkcode,sum(XD_COL5) hksum from wnbank.S_LOAN_hk where to_char(cast (cast (XD_COL4 as timestamp) as date),'yyyy-mm-dd')>='2019-04-01' and to_char(cast (cast (XD_COL4 as timestamp) as date),'yyyy-mm-dd')<='"
									+ checkDate
									+ "' group by XD_COL1) hk  left join (select XD_COL1,XD_COL81,XD_COL5,XD_COL6 from wnbank.S_LOAN_DK where to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ checkDate
									+ "')dk on hk.hkcode=dk.XD_COL1  where dk.XD_COL5>='"
									+ yearStartDate
									+ "' and dk.XD_COL5<='"
									+ checkDate
									+ "' group by dk.xd_col1,dk.xd_col81) ye  group by ye.xd_col81) zj left join wnbank.s_loan_ryb bb on zj.xd_col81=bb.xd_col1");

			// �ۼ�δ�ջؽ��
			HashMap<String, String> dsMoneyMap = UIUtil
					.getHashMapBySQLByDS(
							null,
							"select bb.xd_col2 xd_col2,dk.hj XD_COL7 from (select dkxj.xd_col81 xd_col81,sum(dkxj.xd_col7) hj from(select XD_COL1 XD_COL1,XD_COL81 XD_COL81,sum(XD_COL6) XD_COL6,sum(XD_COL7) XD_COL7 from wnbank.S_LOAN_DK where  XD_COL5>='"
									+ yearStartDate
									+ "' and XD_COL5<='"
									+ checkDate
									+ "' and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ checkDate
									+ "' group by XD_COL1,XD_COL81) dkxj group by dkxj.XD_COL81 )dk left join wnbank.s_loan_ryb bb on dk.xd_col81=bb.xd_col1");

			// ��ȡ���Թ��ۼ��ջص��ڴ����
			HashMap<String, String> dgBackUpMoneyMap = UIUtil
					.getHashMapBySQLByDS(
							null,
							"SELECT b.name,a.AMOUNTREPAID FROM (select dg.cus_manager cus_manager,sum(hk.AMOUNTREPAID) AMOUNTREPAID from(select cus_manager cus_manager,CUS_ID CUS_ID from wnbank.s_cmis_acc_loan where to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ checkDate
									+ "' and cla<>'05' and account_status='1' and loan_balance<>'0'  group by cus_manager,CUS_ID) dg left join  wnbank.S_CMIS_ACC_BILL_REPAY hk on hk.CUS_ID=dg.CUS_ID where to_char(to_date(DATEOFREPAYMENT,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ yearStartDate
									+ "' and to_char(to_date(DATEOFREPAYMENT,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ checkDate
									+ "' group by dg.cus_manager) a LEFT JOIN (V_SAL_PERSONINFO) b ON a.cus_manager=b.code");
			HashMap<String, String> dgMoneyMap = UIUtil
					.getHashMapBySQLByDS(
							null,
							"SELECT b.NAME,a.LOAN_BALANCE FROM (select dg.cus_manager cus_manager,sum(dg.LOAN_BALANCE) LOAN_BALANCE from (select cus_manager cus_manager,sum(LOAN_BALANCE) LOAN_BALANCE from wnbank.s_cmis_acc_loan where to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ checkDate
									+ "' and cla<>'05' and loan_balance<>'0' and account_status='1' and to_char(to_date(ORIG_EXPI_DATE,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ yearStartDate
									+ "' and to_char(to_date(ORIG_EXPI_DATE,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ checkDate
									+ "' group by CUS_ID,cus_manager) dg  group by dg.cus_manager) a LEFT JOIN V_SAL_PERSONINFO b ON a.cus_manager=b.code");
			String[] managers_names = UIUtil
					.getStringArrayFirstColByDS(null,
							"SELECT NAME FROM V_SAL_PERSONINFO WHERE STATIONKIND LIKE '%�ͻ�����%'");
			DecimalFormat format = new DecimalFormat("0.0000");
			for (int i = 0; i < managers_names.length; i++) {
				double dsBackUpMoney = Double
						.parseDouble(dsBackUpMoneyMap.get(managers_names[i]) == null
								|| dsBackUpMoneyMap.get(managers_names[i])
										.equals("") ? "0.0" : dsBackUpMoneyMap
								.get(managers_names[i]));
				double dsMoney = Double.parseDouble(dsMoneyMap
						.get(managers_names[i]) == null
						|| dsMoneyMap.get(managers_names[i]).equals("") ? "0.0"
						: dsMoneyMap.get(managers_names[i]));
				double dgBackUpMoney = Double
						.parseDouble(dgBackUpMoneyMap.get(managers_names[i]) == null
								|| dgBackUpMoneyMap.get(managers_names[i])
										.equals("") ? "0.0" : dgBackUpMoneyMap
								.get(managers_names[i]));
				double dgMoney = Double.parseDouble(dgMoneyMap
						.get(managers_names[i]) == null
						|| dgMoneyMap.get(managers_names[i]).equals("") ? "0.0"
						: dgMoneyMap.get(managers_names[i]));
				double dsMonth1_3 = Double
						.parseDouble(dsMonth1_3Map.get(managers_names[i]) == null
								|| dsMonth1_3Map.get(managers_names[i]).equals(
										"") ? "0.0" : dsMonth1_3Map
								.get(managers_names[i]));
				double dgdsMoney = dsMoney + dsBackUpMoney + dsMonth1_3
						+ dgMoney;// Ӧ�ջر���
				double backUpMoney = dsBackUpMoney + dgBackUpMoney + dsMonth1_3;// ʵ���ջ�
				System.out.println("�ͻ�����:" + managers_names[i] + ",��˽�ۼ�δ�ջ�="
						+ dsBackUpMoney + ",��˽Ӧ��=" + dsMoney + ",�Թ��ջ�="
						+ dgBackUpMoney + ",�Թ�Ӧ��=" + dgMoney + "1-3�·��ۼ��ջ�:"
						+ dsMonth1_3);
				if (backUpMoney == 0) {
					resultMap.put(managers_names[i], 0.0);
					continue;
				}
				if (dgdsMoney == 0) {
					resultMap.put(managers_names[i], 0.0);
					continue;
				}
				double rate = Double.parseDouble(format.format(backUpMoney
						/ dgdsMoney));
				if (rate < 0.97) {
					resultMap.put(managers_names[i], 0.0);
				} else if (rate == 0.97) {
					resultMap.put(managers_names[i], 2.0);
				} else {
					double score = Double.parseDouble(format
							.format(2 + (rate - 0.97) / 0.001 * 0.1));
					resultMap
							.put(managers_names[i], score >= 5.0 ? 5.0 : score);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	/**
	 * �ͻ�����Ч����֮���������������� Ϊ�˷�����ԣ�ʹ��dmo��ѯ���ֿ�ָ���쳣����ʱʹ��dmo��ѯ���Ժ�����쳣���ڽ��и���
	 * 
	 * @param dateNum
	 *            ����ͳ���:3��
	 * @return
	 */
	public HashMap<String, Double> getDnxzbldk(int dateNum) {
		HashMap<String, Double> resultMap = new HashMap<String, Double>();
		try {
			// ��ȡ������Ϳ�������
			String checkDate = getYearStartAndEnd(dateNum).get(1);
			// �������������������̶��
			HashMap<String, String> tolerateMoneyMap = UIUtil
					.getHashMapBySQLByDS(
							null,
							"SELECT b.xd_col2,a.money FROM (SELECT xd_col81,sum(xd_col7) money FROM WNBANK.S_LOAN_DK WHERE TO_CHAR(TO_DATE(LOAD_DATES,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ checkDate
									+ "' AND xd_col7>0 GROUP BY xd_col81) a LEFT JOIN (WNBANK.S_LOAN_RYB) b ON a.xd_col81=b.xd_col1");
			// �������������������
			HashMap<String, String> addTolerateMoneyMap = UIUtil
					.getHashMapBySQLByDS(
							null,
							"SELECT bb.xd_col2,aa.money FROM (SELECT a.xd_col81,sum(a.xd_col7) money FROM (SELECT xd_col1,xd_col7,xd_col81 FROM WNBANK.s_loan_dk WHERE TO_CHAR(TO_DATE(LOAD_DATES,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ checkDate
									+ "' AND XD_COL22 IN ('03','04') AND xd_col7>0) a LEFT JOIN (SELECT xd_col1,xd_col7,XD_COL81 FROM WNBANK.S_LOAN_DK WHERE TO_CHAR(TO_DATE(LOAD_DATES,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ WnUtils.getYearEnd(checkDate)
									+ "' AND xd_col22 IN ('01','02') AND xd_col7>0) b ON a.xd_col1=b.xd_col1 WHERE b.xd_col1 IS NOT NULL GROUP BY a.xd_col81) aa LEFT JOIN (WNBANK.S_LOAN_RYB) bb ON aa.xd_col81=bb.xd_col1");
			String[] manager_names = UIUtil
					.getStringArrayFirstColByDS(null,
							"SELECT NAME FROM V_SAL_PERSONINFO WHERE STATIONKIND LIKE '%�ͻ�����%'");
			double rate = 0.0;
			if (checkDate.indexOf("06-30") != -1) {
				rate = 0.004;
			} else if (checkDate.indexOf("12-31") != -1) {
				rate = 0.008;
			}
			DecimalFormat format = new DecimalFormat("0.0000");
			for (int i = 0; i < manager_names.length; i++) {
				double tolerateMoney = Double.parseDouble(tolerateMoneyMap
						.get(manager_names[i]) == null ? "0.0"
						: tolerateMoneyMap.get(manager_names[i]))
						* rate;// ���������������̶��
				if (tolerateMoney <= 0) {
					resultMap.put(manager_names[i], 0.0);
					continue;
				}
				double addTolerateMoney = Double
						.parseDouble(addTolerateMoneyMap.get(manager_names[i]) == null ? "0.0"
								: addTolerateMoneyMap.get(manager_names[i]));
				System.out.println("�ͻ�����:" + manager_names[i] + ",���̶�:"
						+ tolerateMoney + ",������������:" + addTolerateMoney);
				if (addTolerateMoney <= 0) {
					resultMap.put(manager_names[i], 3.0);
					continue;
				}
				double score = Double.parseDouble(format
						.format((tolerateMoney - addTolerateMoney)
								/ tolerateMoney * 3.0));
				if (score <= 0.0) {
					resultMap.put(manager_names[i], 0.0);
				} else if (score >= 3.0) {
					resultMap.put(manager_names[i], 3.0);
				} else {
					resultMap.put(manager_names[i],
							Double.parseDouble(format.format(score)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	// �ջش�����������
	/**
	 * �ͻ�����ȼ�����֮�ջش����������� Ϊ�˷�����ԣ�dmo��ѯ���Ϊ�գ���ʱʹ��dmo��sql���в�ѯ������Ժ��������⣬�ڽ��и���
	 * ����������:5��
	 * 
	 * @param dateNum
	 * @return
	 */
	public HashMap<String, Double> getshcl(int dateNum) {
		HashMap<String, Double> resultMap = new HashMap<String, Double>();
		try {
			String yearStartDate = getYearStartAndEnd(dateNum).get(0);
			String check_date = getYearStartAndEnd(dateNum).get(1);
			String yearLastEnd = getYearStartAndEnd(dateNum).get(2);
			// ʵ���ջز�������
			HashMap<String, String> backUpMoneyMap = UIUtil
					.getHashMapBySQLByDS(
							null,
							"SELECT bb.xd_col2,aa.money FROM (SELECT b.xd_col81 xd_col81,sum(a.xd_col5) money FROM (SELECT xd_col1,XD_COL5 FROM WNBANK.S_LOAN_HK WHERE TO_CHAR(TO_DATE(LOAD_DATES,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ check_date
									+ "'  AND TO_CHAR(TO_DATE(LOAD_DATES,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ yearStartDate
									+ "'  AND XD_COL16 IN ('03','04')  AND xd_col5>0) a LEFT JOIN (SELECT xd_col1,XD_COL81 FROM WNBANK.S_LOAN_DK WHERE TO_CHAR(TO_DATE(LOAD_DATES,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ check_date
									+ "') b ON a.xd_col1=b.xd_col1 GROUP BY b.xd_col81) aa LEFT JOIN (WNBANK.S_LOAN_RYB) bb ON aa.xd_col81=bb.xd_col1");
			// ��������������
			HashMap<String, String> clMoneyMap = UIUtil
					.getHashMapBySQLByDS(
							null,
							"SELECT B.XD_COL2, A.XD_COL7 FROM (SELECT XD_COL81,SUM(XD_COL7) XD_COL7 FROM WNBANK.S_LOAN_DK WHERE TO_CHAR(TO_DATE(LOAD_DATES,'YYYY-MM-DD'),'YYYY-MM-DD')='"
									+ yearLastEnd
									+ "' AND XD_COL7>0 AND XD_COL22 IN ('03','04') GROUP BY XD_COL81) A LEFT JOIN (WNBANK.S_LOAN_RYB ) B ON A.XD_COL81=B.XD_COL1");
			String[] manager_names = UIUtil
					.getStringArrayFirstColByDS(null,
							"SELECT NAME FROM V_SAL_PERSONINFO WHERE STATIONKIND LIKE '%�ͻ�����%'");
			double rate = 0.0;
			if (check_date.indexOf("06-30") != -1) {
				rate = 0.1;
			} else {
				rate = 0.2;
			}
			DecimalFormat format = new DecimalFormat("0.0000");
			for (int i = 0; i < manager_names.length; i++) {
				double backUpMoney = Double.parseDouble(backUpMoneyMap
						.get(manager_names[i]) == null ? "0.0" : backUpMoneyMap
						.get(manager_names[i]));
				if (backUpMoney <= 0) {
					resultMap.put(manager_names[i], 0.0);
					continue;
				}
				double clMoney = Double.parseDouble(clMoneyMap
						.get(manager_names[i]) == null ? "0.0" : clMoneyMap
						.get(manager_names[i]));
				System.out.println("�ͻ�����:" + manager_names[i] + ",�ջش�����"
						+ backUpMoney + ",��������:" + clMoney);
				if (clMoney <= 0.0) {
					resultMap.put(manager_names[i], 0.0);
					continue;
				}
				double score = Double.parseDouble(format.format(backUpMoney
						/ (clMoney * rate)));
				if (score <= 0) {
					resultMap.put(manager_names[i], 0.0);
				} else if (score >= 5.0) {
					resultMap.put(manager_names[i], 5.0);
				} else {
					resultMap.put(manager_names[i], score);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	/**
	 * �ͻ�����ȼ�����֮����ͻ��ֻ����п��������� ����������:3��
	 * 
	 * @param dateNum
	 * @return
	 */
	public HashMap<String, Double> getDKsjyh(int dateNum) {
		HashMap<String, Double> resultMap = new HashMap<String, Double>();
		try {

			String yearStartDate = getYearStartAndEnd(dateNum).get(0);
			String checkDate = getYearStartAndEnd(dateNum).get(1);
			// ���ȼ��㽨����ϸ
			String gtjmSQL = "select yb.xd_col2 xd_col2,xx.tj tj from (select XD_COL96 XD_COL96,count(XD_COL96) tj from wnbank.s_loan_khxx where to_char(cast (cast (xd_col3 as timestamp) as date),'yyyy-mm-dd')>='"
					+ yearStartDate
					+ "' and to_char(cast (cast (xd_col3 as timestamp) as date),'yyyy-mm-dd')<='"
					+ checkDate
					+ "' and xd_col10 not in('δ����','����','!$') and XD_COL4 in('206','908') group by XD_COL96)xx left join wnbank.s_loan_ryb yb on xx.xd_col96=yb.xd_col1";// ���幤�̻������񽨵�����
			String dwzgSQL = "select yb.xd_col2 xd_col2,xx.tj tj from (select XD_COL96 XD_COL96,count(XD_COL96) tj from wnbank.s_loan_khxx where to_char(cast (cast (xd_col3 as timestamp) as date),'yyyy-mm-dd')>='"
					+ yearStartDate
					+ "'and to_char(cast (cast (xd_col3 as timestamp) as date),'yyyy-mm-dd')<='"
					+ checkDate
					+ "'and xd_col10 not in('δ����','����','!$') and XD_COL4='907' group by XD_COL96)xx left join wnbank.s_loan_ryb yb on xx.xd_col96=yb.xd_col1";// ��λְ��
			String nhSQL = "select yb.xd_col2 xd_col2,zc.tj tj  from(select xx.xd_col96 xd_col96,count(xx.xd_col96) tj from (select XD_COL1,XD_COL96 from wnbank.s_loan_khxx where to_char(cast (cast (xd_col3 as timestamp) as date),'yyyy-mm-dd')>='"
					+ yearStartDate
					+ "' and to_char(cast (cast (xd_col3 as timestamp) as date),'yyyy-mm-dd')<='"
					+ checkDate
					+ "' and xd_col10 not in('δ����','����','!$') and XD_COL4='905') xx left join wnbank.S_LOAN_KHXXZCQK zc on xx.xd_col1=zc.xd_col1 where zc.XD_COL6!='0' group by xx.xd_col96)zc left join wnbank.s_loan_ryb yb on zc.xd_col96=yb.xd_col1";// ũ������
			String xwSQL = "select yb.xd_col2 xd_col2,xx.tj tj from (select XD_COL96 XD_COL96,count(XD_COL96) tj from wnbank.s_loan_khxx where to_char(cast (cast (xd_col3 as timestamp) as date),'yyyy-mm-dd')>='"
					+ yearStartDate
					+ "' and to_char(cast (cast (xd_col3 as timestamp) as date),'yyyy-mm-dd')<='"
					+ checkDate
					+ "' and xd_col10 not in('δ����','����','!$') and XD_COL4='206' and XD_COL163 not in('05','03','06','01') group by XD_COL96)xx left join wnbank.s_loan_ryb yb on xx.xd_col96=yb.xd_col1";// С΢��ҵ����

			String sjyh = "select b.xd_col2 name,a.card_no num from (select bbbbb.xd_col96,count(aaaaa.card_no) card_no from (select aaa.card_no card_no,aaa.manager_no manager_no from (select distinct(aa.card_no) card_no,aa.manager_no manager_no from (select a.a card_no,a.b manager_no,a.c c,b.b d from  (select * from excel_tab_12 where c!='��' and year||'-'||month='"
					+ checkDate.substring(0, checkDate.lastIndexOf("-"))
					+ "') a  left join (select * from excel_tab_49  where  year||'-'||month='"
					+ checkDate.substring(0, checkDate.lastIndexOf("-"))
					+ "') b on a.a=b.b where b.a is not null order by a.a) aa) aaa  where (to_char(sysdate,'yyyy')-substr(aaa.card_no,7,4))<=45) aaaaa left join (select xd_col7,xd_col96 from wnbank.s_loan_khxx where to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
					+ checkDate
					+ "')  bbbbb on bbbbb.xd_col7=aaaaa.card_no  group by bbbbb.xd_col96) a left join wnbank.s_loan_ryb b on b.xd_col1=a.xd_col96";
			HashMap<String, String> gtjmMap = UIUtil.getHashMapBySQLByDS(null,
					gtjmSQL);
			HashMap<String, String> dwzgMap = UIUtil.getHashMapBySQLByDS(null,
					dwzgSQL);
			HashMap<String, String> nhMap = UIUtil.getHashMapBySQLByDS(null,
					nhSQL);
			HashMap<String, String> xwMap = UIUtil.getHashMapBySQLByDS(null,
					xwSQL);
			HashMap<String, String> shyhMap = UIUtil.getHashMapBySQLByDS(null,
					sjyh);
			HashVO[] managerVos = UIUtil
					.getHashVoArrayByDS(
							null,
							"SELECT CODE,NAME,STATIONKIND FROM V_SAL_PERSONINFO WHERE STATIONKIND LIKE '%�ͻ�����%'");
			// ��ȡ���ֻ����п�������
			DecimalFormat format = new DecimalFormat("#.000");
			DecimalFormat scoreformat = new DecimalFormat("#.00");
			double score = 0.0;
			for (int i = 0; i < managerVos.length; i++) {
				String manager_name = managerVos[i].getStringValue("name");// ��ȡ���ͻ���������
				double jdSum = 0;
				jdSum = Double
						.parseDouble(gtjmMap.get(manager_name) == null ? "0"
								: gtjmMap.get(manager_name))
						+ Double.parseDouble(dwzgMap.get(manager_name) == null ? "0"
								: dwzgMap.get(manager_name))
						+ Double.parseDouble(nhMap.get(manager_name) == null ? "0"
								: nhMap.get(manager_name))
						+ Double.parseDouble(xwMap.get(manager_name) == null ? "0"
								: xwMap.get(manager_name));
				if (jdSum == 0) {
					resultMap.put(manager_name, 0.0);
					continue;
				}

				if (shyhMap.get(manager_name) == null) {
					resultMap.put(manager_name, 0.0);
				}

				// �ֻ����п�����
				double sjyhNum = Double
						.parseDouble(shyhMap.get(manager_name) == null ? "0"
								: shyhMap.get(manager_name));
				double route = Double.parseDouble(format
						.format(sjyhNum / jdSum));
				System.out.println("�ͻ���������:" + manager_name + ",��������:" + jdSum
						+ ",�ֻ����п���:" + sjyhNum + ",��ɱ���:" + route);
				if (route == 0.2) {
					resultMap.put(manager_name, 2.0);
				} else if (route > 0.2) {
					score = 2 + (route - 0.20) / 0.001 * 0.1 > 3 ? 3.0
							: 2 + (route - 0.20) / 0.001 * 0.1;
					resultMap.put(manager_name,
							Double.parseDouble(scoreformat.format(score)));
				} else {
					score = 2 - (0.20 - route) / 0.001 * 0.2 < 0 ? 0.00
							: 2 - (0.20 - route) / 0.001 * 0.2;
					resultMap.put(manager_name,
							Double.parseDouble(scoreformat.format(score)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	/**
	 * WN_ZNSH_RESULT �ͻ�����ȼ�����֮��ũ�̻�ά������
	 * ����:��Ϊ���Թ����У�dmo�еķ����޷�ִ�гɹ�����ʱʹ��dmo�еķ������պ�������⣬�ǵø����� ����:3�֣����������˴���
	 * 
	 * @param dateNum
	 * @return
	 */
	public HashMap<String, Double> getZnsh(int dateNum) {
		HashMap<String, Double> resultMap = new HashMap<String, Double>();
		try {
			HashMap<String, String> managerNamesMap = UIUtil
					.getHashMapBySQLByDS(
							null,
							"SELECT NAME,STATIONKIND FROM V_SAL_PERSONINFO  WHERE STATIONKIND LIKE '%�ͻ�����%'");
			Set<String> managerNames = managerNamesMap.keySet();
			String yearStartDate = getYearStartAndEnd(dateNum).get(0);
			String date_time = getYearStartAndEnd(dateNum).get(1);// ����������

			HashMap<String, String> managerRateMap = UIUtil
					.getHashMapBySQLByDS(
							null,
							"select aaaa.b name,count(aaaa.c) num from (select aaa.b,aaa.c from (select a.b b,a.c c,b.num num from (select b,c from excel_tab_10 where year||'-'||month='"
									+ date_time.substring(0, 7)
									+ "' ) a left join (select c,count(b) num from excel_tab_11 where year||'-'||month='"
									+ date_time.substring(0, 7)
									+ "' group by c) b on b.c=a.c) aaa left join (select aa.b b,aa.c c,count(bb.a) num from (select a,b,c from excel_tab_10 where year||'-'||month='"
									+ date_time.substring(0, 7)
									+ "' ) aa left join (select a.a,a.num,b.c,b.e from (select a,(b+c) num  from excel_tab_14 where year||'-'||month='"
									+ date_time.substring(0, 7)
									+ "') a left join (select a,b ,c,d,e from excel_tab_11 where year ||'-'|| month='"
									+ date_time.substring(0, 7)
									+ "' and d !='��') b on a.a=b.b where a.num>=b.e) bb on aa.c=bb.c  group by aa.b,aa.c)  bbb  on   aaa.b=bbb.b and aaa.c=bbb.c where aaa.num=bbb.num) aaaa group by aaaa.b");
			HashMap<String, String> gridNumMap = UIUtil.getHashMapBySQLByDS(
					null,
					"SELECT b,COUNT(C) FROM EXCEL_TAB_10 WHERE year||'-'||month='"
							+ date_time.substring(0, 7) + "' GROUP BY b");
			DecimalFormat format = new DecimalFormat("#.00");
			for (String manager_name : managerNames) {
				String manager_type = managerNamesMap.get(manager_name);
				if ("�����ͻ�����".equals(manager_type)) {
					resultMap.put(manager_name, 0.0);
					continue;
				}
				double managerRate = Double.parseDouble(managerRateMap
						.get(manager_name) == null ? "0" : managerRateMap
						.get(manager_name));// �ͻ�������������
				double gridNum = Double.parseDouble(gridNumMap
						.get(manager_name) == null ? "0" : gridNumMap
						.get(manager_name));// �ͻ������ۼ�������
				if (gridNum == 0) {
					resultMap.put(manager_name, gridNum);
					continue;
				}
				System.out.println("�ͻ�����:" + manager_name + ",���������:"
						+ managerRate + ",��������:" + gridNum + ",��ɱ�:"
						+ (managerRate / gridNum));
				double score = (managerRate / gridNum) * 3.0;
				resultMap.put(manager_name,
						Double.parseDouble(format.format(score)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	/**
	 * �����Ч����������� V_WN_DEPOSIT_BALANCE
	 * Ϊ�˷�����ԣ�dmo��ѯ����sqlΪnull����ʱʹ��dmo��ѯsql���Ժ�����bug�ڸ��� ����4�֣�����3��
	 * 
	 * @param dateNum
	 * @return
	 */
	public HashMap<String, Double> getCKyxhsye(int dateNum) {
		HashMap<String, Double> resultMap = new HashMap<String, Double>();
		try {
			String yearStartDate = getYearStartAndEnd(dateNum).get(0);
			String check_date = getYearStartAndEnd(dateNum).get(1);
			HashMap<String, String> manager_names = UIUtil
					.getHashMapBySQLByDS(null,
							"SELECT name,STATIONKIND FROM V_SAL_PERSONINFO WHERE STATIONKIND LIKE '%�ͻ�����%'");
			HashMap<String, String> ckyeMap = UIUtil.getHashMapBySQLByDS(null,
					"SELECT  name,sum(bfb) FROM V_WN_DEPOSIT_BALANCE WHERE  date_time='"
							+ check_date + "' GROUP BY name");
			Set<String> managerSet = manager_names.keySet();
			DecimalFormat format = new DecimalFormat("#.00");
			double fullscore = 0.0;
			for (String manager_name : managerSet) {
				String manager_type = manager_names.get(manager_name);
				double rate = Double
						.parseDouble(ckyeMap.get(manager_name) == null
								|| ckyeMap.get(manager_name).equals("") ? "0.0"
								: ckyeMap.get(manager_name));
				System.out.println("�ͻ�����:" + manager_name + ",������������ɱ�:"
						+ rate);
				if ("�����ͻ�����".equals(manager_type)) {
					fullscore = 4.0;
				} else {
					fullscore = 3.0;
				}
				if (rate <= 0) {
					resultMap.put(manager_name, 0.0);
				} else if (rate >= 1) {
					resultMap.put(manager_name, fullscore);
				} else {
					resultMap
							.put(manager_name,
									Double.parseDouble(format.format(fullscore
											* rate)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	/**
	 * �ͻ���������֮�����Ч�������� Ϊ�˷�����ԣ�dmo��ѯ���Ľ��Ϊnullֵ����ʱʹ��dmo��sql���в�ѯ���Ժ�����쳣�ڸ���
	 * ����5�֣�����4��
	 * 
	 * @param dateNum
	 * @return
	 */
	public HashMap<String, Double> getCKyxhs(int dateNum) {
		HashMap<String, Double> resultMap = new HashMap<String, Double>();
		try {
			// ��ȡ����ǰ����
			String yearStart = getYearStartAndEnd(dateNum).get(0);// ��ȡ�����ʱ��
			String check_date = getYearStartAndEnd(dateNum).get(1);// ��ȡ������ʱ��
			Map<String, String> ckhsMap = UIUtil.getHashMapBySQLByDS(null,
					"SELECT name,bfb FROM V_WN_DEPOSIT_NUMBER  WHERE  date_time='"
							+ check_date + "'");
			HashMap<String, String> manager_names = UIUtil
					.getHashMapBySQLByDS(null,
							"SELECT name,STATIONKIND FROM V_SAL_PERSONINFO WHERE STATIONKIND LIKE '%�ͻ�����%'");
			Set<String> manager_sets = manager_names.keySet();
			DecimalFormat format = new DecimalFormat("#.00");
			double score = 0;
			double fullscore = 0.0;
			for (String manager_name : manager_sets) {
				String manager_type = manager_names.get(manager_name);

				if (manager_type.equals("�����ͻ�����")) {
					fullscore = 4.0;
				} else {
					fullscore = 5.0;
				}
				double rate = Double
						.parseDouble(ckhsMap.get(manager_name) == null
								|| ckhsMap.get(manager_name).equals("") ? "0.0"
								: ckhsMap.get(manager_name));
				System.out.println("�ͻ���������:" + manager_name + ",����������ɱ�:"
						+ rate);
				if (rate >= 1.0) {
					resultMap.put(manager_name, score);
				} else if (rate <= 0) {
					resultMap.put(manager_name, 0.0);
				} else {
					score = Double.parseDouble(format.format(fullscore * rate));
					resultMap.put(manager_name, score >= fullscore ? fullscore
							: score);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	/**
	 * ����������� [V_WN_LOAN_BALANCE] Ϊ�˷������,dmo��ѯ���Ľ��Ϊ��ֵ����ʱʹ��dmo���в��ԣ��Ժ������쳣�ڽ��и�����
	 * ����������:3��
	 * 
	 * @return
	 */
	public HashMap<String, Double> getDKyexz(int dateNum) {
		HashMap<String, Double> resultMap = new HashMap<String, Double>();
		try {
			String yearStartDate = getYearStartAndEnd(dateNum).get(0);
			String check_date = getYearStartAndEnd(dateNum).get(1);
			String[] managers = UIUtil
					.getStringArrayFirstColByDS(null,
							"SELECT name FROM V_SAL_PERSONINFO WHERE STATIONKIND LIKE '%�ͻ�����%'");
			HashMap<String, String> ckyeMap = UIUtil.getHashMapBySQLByDS(null,
					"SELECT  name,bfb FROM V_WN_LOAN_BALANCE WHERE date_time='"
							+ check_date + "'");
			DecimalFormat format = new DecimalFormat("#.00");
			for (int i = 0; i < managers.length; i++) {
				double rate = Double
						.parseDouble(ckyeMap.get(managers[i]) == null
								|| ckyeMap.get(managers[i]).equals("") ? "0.0"
								: ckyeMap.get(managers[i]));
				System.out
						.println("�ͻ�����:" + managers[i] + ",�������������ɱ�:" + rate);
				if (rate <= 0) {
					resultMap.put(managers[i], 0.0);
				} else if (rate >= 1) {
					resultMap.put(managers[i], 3.0);
				} else {
					resultMap.put(managers[i],
							Double.parseDouble(format.format(3.0 * rate)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	/**
	 * ��������� [V_WN_DEPOSIT_NUMBER]
	 * Ϊ�˷�����ԣ�dmo�ڽ��в�ѯʱ����ֿ�ָ���쳣����ʱʹ��dmo�����в�ѯ���Ժ����bug�ڸ��� ����5��:����:3��
	 * 
	 * @return
	 */
	public HashMap<String, Double> getDKhsxz(int dateNum) {
		HashMap<String, Double> resultMap = new HashMap<String, Double>();
		try {
			String yearStartDate = getYearStartAndEnd(dateNum).get(0);
			String check_date = getYearStartAndEnd(dateNum).get(1);
			HashMap<String, String> manager_names = UIUtil
					.getHashMapBySQLByDS(null,
							"SELECT name,STATIONKIND FROM V_SAL_PERSONINFO WHERE STATIONKIND LIKE '%�ͻ�����%'");
			HashMap<String, String> dkhsMap = UIUtil.getHashMapBySQLByDS(null,
					"SELECT name,BFB FROM V_WN_LOANS_NEWLY WHERE  DATE_TIME='"
							+ check_date + "'");
			Set<String> managerSet = manager_names.keySet();
			DecimalFormat format = new DecimalFormat("#.00");
			double score = 0.0;
			double fullScore = 0.0;
			for (String manager_name : managerSet) {
				String manager_type = manager_names.get(manager_name);
				if ("�����ͻ�����".equals(manager_type)) {
					fullScore = 5.0;
				} else {
					fullScore = 3.0;
				}
				double rate = Double
						.parseDouble(dkhsMap.get(manager_name) == null
								|| dkhsMap.get(manager_name).equals("") ? "0.0"
								: dkhsMap.get(manager_name));
				System.out.println("�ͻ�����:" + manager_name + ",�������ɱ�:" + rate);
				if (rate <= 0) {
					resultMap.put(manager_name, 0.0);
				} else if (rate >= 1) {
					resultMap.put(manager_name, fullScore);
				} else {
					score = fullScore * rate >= fullScore ? fullScore
							: fullScore * rate;
					resultMap.put(manager_name,
							Double.parseDouble(format.format(score)));
				}
			}
			;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	/**
	 * �ͻ���������֮�½����� ����:4�֣�����2��
	 * Ϊ�˷�����ԣ�dmoִ��sql��ѯʱ������ֿ�ָ���쳣���������ʱʹ��dmo����ʱ�޷����ԣ�ԭ��Ϊ:Ŀ���������ȱ�ٽ�������
	 * 
	 * @return
	 */
	public HashMap<String, Double> jdRate(int dateNum) {
		HashMap<String, Double> resultMap = new HashMap<String, Double>();
		try {
			// ��ȡ�����ʱ��
			String yearStartDate = getYearStartAndEnd(dateNum).get(0);
			String checkDate = getYearStartAndEnd(dateNum).get(1);
			// ////��ȡ������������������
			String nhSQL = "select yb.xd_col2 xd_col2,zc.tj tj  from(select xx.xd_col96 xd_col96,count(xx.xd_col96) tj from (select XD_COL1,XD_COL96 from wnbank.s_loan_khxx where   to_char(cast (cast (xd_col3 as timestamp) as date),'yyyy-mm-dd')<='"
					+ checkDate
					+ "'  and to_char(cast (cast (xd_col3 as timestamp) as date),'yyyy-mm-dd')>='"
					+ yearStartDate
					+ "' and xd_col10 not in('δ����','����','!$') and XD_COL4='905') xx left join wnbank.S_LOAN_KHXXZCQK zc on xx.xd_col1=zc.xd_col1 where zc.XD_COL6!='0' group by xx.xd_col96)zc left join wnbank.s_loan_ryb yb on zc.xd_col96=yb.xd_col1";// ũ������
			// //���幤�̻������񽨵�
			String gjSQL = "select yb.xd_col2 xd_col2,xx.tj tj from (select XD_COL96 XD_COL96,count(XD_COL96) tj from wnbank.s_loan_khxx where  to_char(cast (cast (xd_col3 as timestamp) as date),'yyyy-mm-dd')<='"
					+ checkDate
					+ "' and to_char(cast (cast (xd_col3 as timestamp) as date),'yyyy-mm-dd')>='"
					+ yearStartDate
					+ "' and    xd_col10 not in('δ����','����','!$') and XD_COL4 in('206','908') group by XD_COL96)xx left join wnbank.s_loan_ryb yb on xx.xd_col96=yb.xd_col1";
			// //��λְ������
			String dwSQL = "select yb.xd_col2 xd_col2,xx.tj tj from (select XD_COL96 XD_COL96,count(XD_COL96) tj from wnbank.s_loan_khxx where  to_char(cast (cast (xd_col3 as timestamp) as date),'yyyy-mm-dd')<='"
					+ checkDate
					+ "'and to_char(cast (cast (xd_col3 as timestamp) as date),'yyyy-mm-dd')>='"
					+ yearStartDate
					+ "' and  xd_col10 not in('δ����','����','!$') and XD_COL4='907' group by XD_COL96)xx left join wnbank.s_loan_ryb yb on xx.xd_col96=yb.xd_col1";
			// //С΢��ҵ����
			String xwSQL = "select yb.xd_col2 xd_col2,xx.tj tj from (select XD_COL96 XD_COL96,count(XD_COL96) tj from wnbank.s_loan_khxx where to_char(cast (cast (xd_col3 as timestamp) as date),'yyyy-mm-dd')>='[�����³�����]' and to_char(cast (cast (xd_col3 as timestamp) as date),'yyyy-mm-dd')<='"
					+ checkDate
					+ "' and to_char(cast (cast (xd_col3 as timestamp) as date),'yyyy-mm-dd')>='"
					+ yearStartDate
					+ "' and xd_col10 not in('δ����','����','!$') and XD_COL4='206' and XD_COL163 not in('05','03','06','01') group by XD_COL96)xx left join wnbank.s_loan_ryb yb on xx.xd_col96=yb.xd_col1";
			HashMap<String, String> nhMap = UIUtil.getHashMapBySQLByDS(null,
					nhSQL);
			HashMap<String, String> gjMap = UIUtil.getHashMapBySQLByDS(null,
					gjSQL);
			HashMap<String, String> dwMap = UIUtil.getHashMapBySQLByDS(null,
					dwSQL);
			HashMap<String, String> xwMap = UIUtil.getHashMapBySQLByDS(null,
					xwSQL);
			// ��ȡ����Ӧ����Ա��Ϣ
			HashVO[] managerVos = UIUtil
					.getHashVoArrayByDS(null,
							"SELECT NAME,STATIONKIND FROM V_SAL_PERSONINFO WHERE STATIONKIND LIKE '%�ͻ�����%'");
			HashMap<String, String> jobNums = UIUtil.getHashMapBySQLByDS(null,
					"SELECT A,sum(R) FROM EXCEL_TAB_53 WHERE year='"
							+ checkDate.substring(0, 4) + "' GROUP BY A");// ���տ��˵Ĺ���������
			DecimalFormat format = new DecimalFormat("#.00");
			double score = 0.0;
			double fullScore = 0.0;
			for (int i = 0; i < managerVos.length; i++) {
				String manager_type = managerVos[i]
						.getStringValue("STATIONKIND");
				String manager_name = managerVos[i].getStringValue("NAME");
				double jobNum = Double
						.parseDouble(jobNums.get(manager_name) == null ? "0.0"
								: jobNums.get(manager_name));
				if (jobNum == 0) {
					resultMap.put(manager_name, 0.0);
					continue;
				}
				if (manager_type.equals("�����ͻ�����")) {// ���н�������Ҫ����
					double sum = Double
							.parseDouble(nhMap.get(manager_name) == null ? "0"
									: nhMap.get(manager_name))
							+ Double.parseDouble(gjMap.get(manager_name) == null ? "0"
									: gjMap.get(manager_name))
							+ Double.parseDouble(dwMap.get(manager_name) == null ? "0"
									: dwMap.get(manager_name))
							+ Double.parseDouble(xwMap.get(manager_name) == null ? "0"
									: xwMap.get(manager_name));
					fullScore = 4.0;
					score = sum / jobNum * fullScore;
					System.out.println("�ͻ�����:" + manager_name + ",��������:" + sum);
				} else {
					double nhNum = Double
							.parseDouble(nhMap.get(manager_name) == null ? "0"
									: nhMap.get(manager_name));
					fullScore = 2.0;
					score = nhNum / jobNum * fullScore;
					System.out.println("�ͻ�����:" + manager_name + ",ũ����������:"
							+ nhNum);
				}
				System.out.println("�ͻ�����:" + manager_name + ",���˵÷�:" + score);
				if (score <= 0) {
					resultMap.put(manager_name, 0.0);
				} else if (score >= fullScore) {
					resultMap.put(manager_name, fullScore);
				} else {
					resultMap.put(manager_name,
							Double.parseDouble(format.format(score)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	/**
	 * �ͻ���������֮ǭũE��ǩԼ WN_QNED_RESULT �����������ֵ:2��
	 * 
	 * @return
	 */
	public HashMap<String, Double> qnedRate(int dateNum) {
		HashMap<String, Double> resultMap = new HashMap<String, Double>();
		try {
			String yearStartDate = getYearStartAndEnd(dateNum).get(0);
			String checkDate = getYearStartAndEnd(dateNum).get(1);
			String[] manager_names = UIUtil
					.getStringArrayFirstColByDS(null,
							"SELECT NAME FROM V_SAL_PERSONINFO WHERE STATIONKIND LIKE '%�ͻ�����%'");
			HashMap<String, String> qnedMap = UIUtil.getHashMapBySQLByDS(null,
					"select c,count(b)  from excel_tab_65 where year='"
							+ checkDate.substring(0, 4) + "' group by c");
			HashMap<String, String> taskNum = UIUtil.getHashMapBySQLByDS(null,
					"select a,sum(p) from excel_tab_53 where year ='"
							+ checkDate.substring(0, 4) + "' group by  a");
			DecimalFormat decimal = new DecimalFormat("#.00");
			double score = 0.0;
			double num, tasknum, rate = 0;
			for (int i = 0; i < manager_names.length; i++) {
				num = Double
						.parseDouble(qnedMap.get(manager_names[i]) == null ? "0.0"
								: qnedMap.get(manager_names[i]));
				tasknum = Double
						.parseDouble(taskNum.get(manager_names[i]) == null ? "0.0"
								: taskNum.get(manager_names[i]));
				System.out.println("�ͻ�����:" + manager_names[i] + ",���������:"
						+ qnedMap.get(manager_names[i]) + ",��������:" + tasknum);
				if (num == 0 || tasknum == 0) {
					resultMap.put(manager_names[i], 0.0);
				} else {
					score = num / tasknum * 2.0 >= 2.0 ? 2.0 : num / tasknum
							* 2.0;
					resultMap.put(manager_names[i],
							Double.parseDouble(decimal.format(score)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	/**
	 * ZPY �ͻ�����֮ǭũE��������� Ϊ�˷�����ԣ�dmoִ��sql���ֿ�ָ���쳣����ʱʹ��dmoִ��sql��ѯ���Ժ�����쳣�ڸ�����
	 * 
	 * @return
	 */
	public HashMap<String, Double> qnedtdScore(int dateNum) {
		HashMap<String, Double> resultMap = new HashMap<String, Double>();
		try {
			String yearStartDate = getYearStartAndEnd(dateNum).get(0);
			String checkDate = getYearStartAndEnd(dateNum).get(1);
			// ��ȡ���ڿͻ�����Ŀ���
			String[] manager_names = UIUtil
					.getStringArrayFirstColByDS(null,
							"SELECT NAME FROM V_SAL_PERSONINFO WHERE STATIONKIND LIKE '%�ͻ�����%'");
			HashMap<String, String> qnedtd = UIUtil.getHashMapBySQLByDS(null,
					"SELECT USERNAME,RATE FROM WN_QNEDTD_RESULT WHERE DATE_TIME='"
							+ checkDate + "'");
			DecimalFormat format = new DecimalFormat("#.00");
			for (int i = 0; i < manager_names.length; i++) {
				double rate = Double
						.parseDouble(qnedtd.get(manager_names[i]) == null ? "0.0"
								: qnedtd.get(manager_names[i]));
				if (rate <= 0.0) {
					resultMap.put(manager_names[i], 0.0);
				} else {
					resultMap.put(manager_names[i], Double.parseDouble(format
							.format(rate * 2.0 > 2 ? 2.0 : rate * 2.0)));
				}
				System.out.println("�ͻ�����:" + manager_names[i] + ",ǭũE�����������ɱ�:"
						+ rate);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	// ��ȡ���ʱ��
	public String getYearStart() {
		SimpleDateFormat simple = new SimpleDateFormat("yyyy");
		String yearStartDate = "";
		try {
			yearStartDate = simple.format(new Date()) + "-01-01";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return yearStartDate;
	}

	// ��ȡ������ʱ��
	public String getYearCenterDate() {
		String result = "";
		try {
			SimpleDateFormat simple = new SimpleDateFormat("yyyy");
			String year = simple.format(new Date());
			result = year + "-" + "06-30";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// ��ȡ����ĩʱ��
	public String getYearEndDate() {
		String result = "";
		try {
			SimpleDateFormat simple = new SimpleDateFormat("yyyy");
			result = simple.format(new Date()) + "-12-31";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * ZPY ��ȡ����ǰ�����е�������
	 * 
	 * @return
	 */
	public String[] getCurrentDate() {
		String[] results = new String[3];
		try {
			Date currentDate = new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String date = format.format(currentDate);
			results = date.split("-");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}

	public List<String> getYearStartAndEnd(int dateNum) {
		List<String> list = new ArrayList<String>();
		String[] currentDate = getCurrentDate();
		String year = "";
		String month = "";
		String day = "";
		if (dateNum == 0) {// �ϰ���
			year = currentDate[0];
			month = "06";
			day = "30";
		} else {
			year = String.valueOf(Integer.parseInt(currentDate[0]) - 1);
			month = "12";
			day = "31";
		}
		String yearStartDate = year + "-" + "01-01";// ��ȡ����������������
		String checkDate = year + "-" + month + "-" + day;// ��ȡ����ĩ����
		list.add(yearStartDate);
		list.add(checkDate);
		list.add(String.valueOf(Integer.parseInt(year) - 1) + "-12-31");
		return list;
	}

	/**
	 * ���ල��ѯ������뵽������� ZPY ��2019-07-25��
	 * 
	 * @param sql
	 */
	@Override
	public void insertMonitorResult(String sql, Map<String, String> conditionMap) {
		try {
			sql = sql
					+ " and DAT_TXN like '"
					+ conditionMap.get("DAT_TXN").substring(0,
							conditionMap.get("DAT_TXN").lastIndexOf(";"))
					+ "%'";
			if (conditionMap.containsKey("AMT_TXN2")) {
				// String[] dateNum = conditionMap.get("AMT_TXN2").split(";");
				//
				// if (dateNum.length==2) {
				// sql = sql + " and AMT_TXN<="
				// + Double.parseDouble(dateNum[1]) * 10000
				// + " and AMT_TXN>="
				// + Double.parseDouble(dateNum[0]) * 10000;
				// } else {
				// sql = sql + " and AMT_TXN>='"
				// +
				// Double.parseDouble(conditionMap.get("AMT_TXN2").replace(";",
				// ""))
				// * 10000 + "'";
				// }
				sql = sql + " and AMT_TXN>="
						+ Double.parseDouble(conditionMap.get("AMT_TXN2"))
						* 10000;
			}
			if (conditionMap.containsKey("COD_ACCT_TITLE")) {
				sql = sql + " and COD_ACCT_TITLE like '%"
						+ conditionMap.get("COD_ACCT_TITLE") + "%'";
			}
			HashVO[] monitorResult = monitorResult = dmo.getHashVoArrayByDS(
					null, sql);
			String[] results = dmo.getStringArrayFirstColByDS(
					null,
					"select COD_ACCT_NO from WN_CURRENT_DEAL_result where  dat_txn  like '"
							+ conditionMap.get("DAT_TXN").substring(
									0,
									conditionMap.get("DAT_TXN")
											.lastIndexOf(";")) + "%'");// Ա�������˺�
			List<String> resList = Arrays.asList(results);
			InsertSQLBuilder insert = new InsertSQLBuilder(
					"WN_CURRENT_DEAL_result");
			DecimalFormat format = new DecimalFormat("#.0000");
			List<String> list = new ArrayList<String>();
			for (HashVO vo : monitorResult) {//
				String codAcctNo = vo.getStringValue("COD_ACCT_NO");// ��ȡ����ǰ�Ľ����˺�
				if (resList.contains(codAcctNo)) {
					continue;
				}
				insert.putFieldValue("dat_txn", vo.getStringValue("dat_txn"));// ��������
				insert.putFieldValue("COD_ACCT_NO", codAcctNo);// �����˺�
				insert.putFieldValue("AMT_TXN", vo.getStringValue("AMT_TXN"));// ��ȡ����ǰ�Ľ��׽��
				insert.putFieldValue("AMT_TXN2", format.format(Double
						.parseDouble(vo.getStringValue("AMT_TXN")) / 10000));// ��ȡ����ǰ���׽��2
				insert.putFieldValue("COD_ACCT_TITLE",
						vo.getStringValue("COD_ACCT_TITLE"));
				insert.putFieldValue("COD_CUST", vo.getStringValue("COD_CUST"));
				insert.putFieldValue("FLG_IC_TYP",
						vo.getStringValue("FLG_IC_TYP"));
				insert.putFieldValue("EXTERNAL_CUSTOMER_IC",
						vo.getStringValue("EXTERNAL_CUSTOMER_IC"));
				insert.putFieldValue("deal_result", "δ����");
				list.add(insert.getSQL());
				if (list.size() >= 5000) {
					dmo.executeBatchByDS(null, list);
					list.clear();
				}
			}
			if (list.size() > 0) {
				dmo.executeBatchByDS(null, list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public String updateCheckState(BillVO[] checkUsers,
			Map<String, String> paraMap) {
		String result = "";
		try {
			UpdateSQLBuilder update = new UpdateSQLBuilder(
					"WN_CURRENT_DEAL_RESULT");// �ල��
			InsertSQLBuilder insert = new InsertSQLBuilder(
					"wn_current_check_result");// �����
			List<String> list = new ArrayList<String>();
			for (BillVO vo : checkUsers) {// ���������ݲ��뵽������У�ͬʱ�Լල���е����ݽ����޸�
				update.setWhereCondition("COD_ACCT_NO='"
						+ vo.getStringValue("COD_ACCT_NO") + "' and DAT_TXN= '"
						+ vo.getStringValue("DAT_TXN") + "'");
				update.putFieldValue("DEAL_RESULT", paraMap.get("CHECK_RESULT"));// �Բ�ѯ������д���
				list.add(update.getSQL());
				insert.putFieldValue("CHECK_RESULT",
						paraMap.get("CHECK_RESULT"));
				insert.putFieldValue("CHECK_REASON",
						paraMap.get("CHECK_REASON"));
				insert.putFieldValue("CHECK_USERCODE",
						paraMap.get("CHECK_USERCODE"));
				insert.putFieldValue("CHECK_USERNAME",
						paraMap.get("CHECK_USERNAME"));
				insert.putFieldValue("CHECK_DATE", paraMap.get("CHECK_DATE"));
				insert.putFieldValue("cod_acct_no",
						vo.getStringValue("cod_acct_no"));
				list.add(insert.getSQL());
				if (list.size() >= 5000) {
					dmo.executeBatchByDS(null, list);
				}
			}
			if (list.size() >= 0) {
				dmo.executeBatchByDS(null, list);
			}
			result = "�����ɹ�";
		} catch (Exception e) {
			result = "����ʧ��";
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * fjũ�񹤹���ָ��
	 * 
	 */
	@Override
	public String getNmggl(String date) {
		Double month = Double.parseDouble(date.substring(5, 7));
		String result = null;
		try {
			InsertSQLBuilder insert = new InsertSQLBuilder("wn_nmggl");
			List list = new ArrayList<String>();
			// �����
			HashMap<String, String> resultMap = dmo
					.getHashMapBySQLByDS(
							null,
							"select  j, count(*) num from wnsalarydb.excel_tab_2 where year||'-'||month='"
									+ date
									+ "'  and b is not null and  d is not null and e is not null and  g is not null and h is not null group by j");
			// �ͻ�����
			HashMap<String, String> userMap = dmo
					.getHashMapBySQLByDS(
							null,
							"select name,name from v_sal_personinfo where stationkind in('�����ͻ�����','����ͻ�����','�����μ�ְ�ͻ�����','�������㸱����','�������㸱����')");
			// ������
			HashMap<String, String> taskMap = dmo.getHashMapBySQLByDS(null,
					"select A,sum(R)*'" + month
							+ "' from EXCEL_TAB_53 where year||'-'||month='"
							+ date + "' group by A");
			if (taskMap.size() <= 0) {
				return "��ѡ�������û���ϴ���ص���������������ѡ��";
			}
			for (String user : userMap.keySet()) {
				Double count, rwcount;
				System.out.println(user);
				System.out.println(resultMap.get(user));
				if (resultMap.get(user) == null
						|| resultMap.get(user).equals("0")
						|| resultMap.get(user).equals("")) {
					continue;
				} else {
					count = Double.parseDouble(resultMap.get(user));
				}
				if (taskMap.get(user) == null || taskMap.get(user).equals("0")
						|| taskMap.get(user).equals("")) {
					continue;
				} else {
					rwcount = Double.parseDouble(taskMap.get(user));
				}
				insert.putFieldValue("name", user);
				insert.putFieldValue("result", count);
				insert.putFieldValue("task", rwcount);
				insert.putFieldValue("date_time", date);
				list.add(insert.getSQL());
			}
			dmo.executeBatchByDS(null, list);
			result = "��ѯ�ɹ���";
		} catch (Exception e) {
			e.printStackTrace();
			result = "��ѯʧ�ܣ�����ϵ����Ա��";
		}
		return result;
	}

	/**
	 * ��Ա��ϵ������������в�������
	 */
	@Override
	public void insertStaffRadio(String handleDate) {
		try {
			String[] existsData = dmo.getStringArrayFirstColByDS(null,
					"SELECT 1 FROM wn_workerStadio WHERE CREATETIME='"
							+ handleDate + "'");
			if (existsData.length > 0) {// ɾ������
				dmo.executeUpdateByDS(null,
						"delete from wn_workerStadio where createtime='"
								+ handleDate + "'");
			}
			HashVO[] staffRadios = dmo.getHashVoArrayByDS(null,
					"select * from V_WN_MISSIONARY where 1=1");// ʹ�õı�������
			InsertSQLBuilder insert = new InsertSQLBuilder("wn_workerStadio");
			List<String> list = new ArrayList<String>();
			for (int i = 0; i < staffRadios.length; i++) {
				insert.putFieldValue("CREATEtime", handleDate);
				insert.putFieldValue("usercode",
						staffRadios[i].getStringValue("GYCODE"));
				insert.putFieldValue("username",
						staffRadios[i].getStringValue("GYNAME"));
				insert.putFieldValue("cardno",
						staffRadios[i].getStringValue("CARDID"));
				insert.putFieldValue("userpost",
						staffRadios[i].getStringValue("POSITION"));
				insert.putFieldValue("userpositionname",
						staffRadios[i].getStringValue("deptname"));
				insert.putFieldValue("radiochangebefore",
						staffRadios[i].getStringValue("STATIONRATIO"));
				insert.putFieldValue("radiochangeafter",
						staffRadios[i].getStringValue("STATIONRATIO"));
				list.add(insert.getSQL());
				if (list.size() >= 5000) {
					dmo.executeBatchByDS(null, list);
				}
			}
			if (list.size() > 0) {
				dmo.executeBatchByDS(null, list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Ա���쳣��Ϊ������ݵ���
	 */
	@Override
	public String importMonitorData(String curSelectDate,
			String curSelectMonthStart, String curSelectMonth, boolean flag) {
		String message = "";
		try {
			// String[] existsData = dmo.getStringArrayFirstColByDS(null,
			// "SELECT 1 FROM wn_show_monitor");
			// HashVO[] monitorData = null;
			// if (existsData == null || existsData.length <= 0) {//
			// ��ʾ����û�����ݣ�ֱ�Ӳ���
			// monitorData = dmo
			// .getHashVoArrayByDS(null,
			// "SELECT * FROM WN_CURRENT_DEAL_DATE where dat_txn>='2019-01-01'");
			// } else {// ��ʾ�����Ѿ��������ݣ������Ѿ����ڵ����ݲ���Ҫ���⵼��
			// String maxDate = dmo.getStringValueByDS(null,
			// "SELECT max(DAT_TXN) FROM wn_show_monitor");
			// monitorData = dmo.getHashVoArrayByDS(null,
			// "SELECT * FROM WN_CURRENT_DEAL_DATE WHERE  DAT_TXN>'"
			// + maxDate + "'");
			// }
			// List<String> list = new ArrayList<String>();
			// InsertSQLBuilder insert = new
			// InsertSQLBuilder("wn_show_monitor");
			// for (int i = 0; i < monitorData.length; i++) {// �������ݱ��в�������
			// insert.putFieldValue("dat_txn",
			// monitorData[i].getStringValue("dat_txn"));// ����ʱ��
			// insert.putFieldValue("cod_acct_no",
			// monitorData[i].getStringValue("cod_acct_no"));// �����˺�
			// insert.putFieldValue("txt_txn_desc",
			// monitorData[i].getStringValue("txt_txn_desc"));// ��������
			// insert.putFieldValue("amt_txn",
			// monitorData[i].getStringValue("amt_txn"));// ���׽��
			// insert.putFieldValue("amt_txn2",
			// monitorData[i].getStringValue("amt_txn2"));// ����֮��Ľ��׽��
			// insert.putFieldValue("cod_acct_title",
			// monitorData[i].getStringValue("cod_acct_title"));// Ա������
			// insert.putFieldValue("cod_cust",
			// monitorData[i].getStringValue("cod_cust"));
			// insert.putFieldValue("flg_ic_typ",
			// monitorData[i].getStringValue("flg_ic_typ"));// ֤������
			// insert.putFieldValue("EXTERNAL_CUSTOMER_IC",
			// monitorData[i].getStringValue("EXTERNAL_CUSTOMER_IC"));// ֤����
			// insert.putFieldValue("mainstation",
			// monitorData[i].getStringValue("mainstation"));// Ա����λ
			// insert.putFieldValue("deptname",
			// monitorData[i].getStringValue("deptname"));// ���ڻ���
			// insert.putFieldValue("deal_result", "δ����");
			// list.add(insert.getSQL());
			// if(list.size()>=5000){
			// dmo.executeBatchByDS(null, list);
			// list.clear();
			// }
			// }

			// List<String> list = new ArrayList<String>();
			// // �Ի��ܱ���д���
			// String[] existsData = existsData =
			// dmo.getStringArrayFirstColByDS(
			// null, "select 1 from wn_gather_monitor_result");
			// SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM");
			// String currentMonth = simple.format(new Date());// ��ȡ����ǰ������
			// String gatherSQL =
			// "select * from V_wn_gather_monitor_result where amt_txn_2>=5";//
			// �����ݽ��л���
			// String loanSQL = "";
			// if (existsData == null || existsData.length <= 0) {//
			// gatherSQL = gatherSQL + " and dat_txn<'" + currentMonth + "'";
			// loanSQL =
			// "SELECT a.xd_col16, sum(a.xd_col7) FROM WNBANK.s_loan_dk  a LEFT JOIN (WNSALARYDB.V_SAL_PERSONINFO) b ON a.xd_col16=b.CARDID WHERE b.Cardid IS NOT NULL AND TO_CHAR(TO_DATE(a.LOAD_DATES,'yyyy-mm-dd'),'yyyy-mm-dd')<'"
			// + currentMonth + "'   GROUP BY a.xd_col16,a.LOAD_DATES";
			// } else {
			// String maxDate = dmo.getStringValueByDS(null,
			// "select max(dat_txn) from wn_gather_monitor_result");
			// gatherSQL = gatherSQL + " and dat_txn<'" + currentMonth
			// + "' and dat_txn>='" + maxDate + "'";
			// loanSQL =
			// "SELECT a.xd_col16, sum(a.xd_col7) FROM WNBANK.s_loan_dk  a LEFT JOIN (WNSALARYDB.V_SAL_PERSONINFO) b ON a.xd_col16=b.CARDID WHERE b.Cardid IS NOT NULL AND TO_CHAR(TO_DATE(a.LOAD_DATES,'yyyy-mm-dd'),'yyyy-mm-dd')<'"
			// + currentMonth
			// +
			// "' AND TO_CHAR(TO_DATE(a.LOAD_DATES, 'yyyy-mm-dd'),'yyyy-mm-dd')>'"
			// + maxDate + "'  GROUP BY a.xd_col16,a.LOAD_DATES";
			// }
			// HashVO[] gatherDataVos = dmo.getHashVoArrayByDS(null, gatherSQL);
			// HashMap<String, String> loanVos = dmo.getHashMapBySQLByDS(null,
			// loanSQL);// ZPY ��2019-08-21�����Ӽ��Ա��������� ��������ˬ
			// InsertSQLBuilder gatherDataInsert = new InsertSQLBuilder(
			// "wn_gather_monitor_result");
			// for (int i = 0; i < gatherDataVos.length; i++) {// ��ȡ����ǰ���еĻ�������
			// gatherDataInsert.putFieldValue("dat_txn",
			// gatherDataVos[i].getStringValue("dat_txn"));
			// gatherDataInsert.putFieldValue("amt_txn_sum",
			// gatherDataVos[i].getStringValue("amt_txn"));
			// gatherDataInsert.putFieldValue("amt_txn_sum2",
			// gatherDataVos[i].getStringValue("amt_txn_2"));
			// gatherDataInsert.putFieldValue("EXTERNAL_CUSTOMER_IC",
			// gatherDataVos[i].getStringValue("cardid"));
			// gatherDataInsert.putFieldValue("name",
			// gatherDataVos[i].getStringValue("name"));
			// gatherDataInsert.putFieldValue("mainstation",
			// gatherDataVos[i].getStringValue("mainstation"));
			// gatherDataInsert.putFieldValue("deptname",
			// gatherDataVos[i].getStringValue("deptname"));
			// gatherDataInsert.putFieldValue("deal_result", "δ����");
			// gatherDataInsert.putFieldValue("ck", "�鿴");
			// // ��ȡ��ǰԱ���Ĵ������ ZPY[2019-08-21]
			// String loan_balance = loanVos.get(gatherDataVos[i]
			// .getStringValue("cardid")) == null ? "0" : loanVos
			// .get(gatherDataVos[i].getStringValue("cardid"));
			// gatherDataInsert.putFieldValue("loan_balance", loan_balance);
			// list.add(gatherDataInsert.getSQL());
			// if (list.size() >= 5000) {
			// dmo.executeBatchByDS(null, list);
			// list.clear();
			// }
			// }
			// if (list.size() > 0) {
			// dmo.executeBatchByDS(null, list);
			// }

			List<String> list = new ArrayList<String>();
			if (flag) {// ��ʾ��ǰѡ�������Ѿ����ڣ���Ҫɾ��
				dmo.executeUpdateByDS(null,
						"delete from WN_GATHER_MONITOR_RESULT where dat_txn='"
								+ curSelectMonth + "'"); // ɾ�����׻�������
				dmo.executeUpdateByDS(null,
						"delete from wn_deal_info where dat_txn2 like'"
								+ curSelectMonth + "%'");// ɾ��������ϸ
				dmo.executeUpdateByDS(null,
						"delete from wn_dk_info where dkdate2 like '"
								+ curSelectMonth + "%' ");
			}
			/**
			 * ִ��SQL����ѯ��ÿ�������Ա�Ľ������ݺʹ�������
			 */
			HashVO[] dealVo = dmo
					.getHashVoArrayByDS(
							null,
							"SELECT mainstation,EXTERNAL_CUSTOMER_IC,NAM_CUST_FULL,code,deptname,sum(CASE COD_DRCR WHEN 'C' THEN money ELSE 0 END) c_money , sum(CASE COD_DRCR WHEN 'D' THEN money ELSE 0 END) d_money , ROUND(sum(CASE COD_DRCR WHEN 'C' THEN money ELSE 0 END)+sum(CASE COD_DRCR WHEN 'D' THEN money ELSE 0 END),2)  summoney FROM  ("
									+ "SELECT mainstation,EXTERNAL_CUSTOMER_IC,NAM_CUST_FULL,code,deptname,ROUND(sum(amt_txn),2) money,cod_drcr  FROM  ("
									+ "   SELECT * FROM ("
									+ "   (select cod_acct_no, sum(amt_txn)/10000 amt_txn,COD_DRCR from (select distinct  to_char(to_date(substr(dat_txn,1,10),'yyyy-mm-dd'),'yyyy-mm-dd') dat_txn,dat_txn dat_txn2,  amt_txn ,txt_txn_desc,cod_acct_no,COD_DRCR from wnbank.s_ofcr_ch_nobook ) where  dat_txn >='"
									+ curSelectMonthStart
									+ "' and dat_txn<='"
									+ curSelectDate
									+ "' group by cod_acct_no,COD_DRCR)  deal "
									+ "    JOIN (SELECT COD_ACCT_NO,COD_CUST,COD_ACCT_TITLE FROM wnbank.S_OFCR_CH_ACCT_MAST_"
									+ curSelectMonth.replace("-", "")
									+ ") cod ON deal.cod_acct_no=cod.COD_ACCT_NO "
									+ "    JOIN (SELECT COD_CUST_ID,EXTERNAL_CUSTOMER_IC,NAM_CUST_FULL FROM wnbank.S_OFCR_CI_CUSTMAST_"
									+ curSelectMonth.replace("-", "")
									+ ") cust ON cod.COD_CUST=cust.COD_CUST_ID "
									+ "    JOIN (SELECT code,name,cardid,mainstation, deptname FROM WNSALARYDB.V_SAL_PERSONINFO) sal ON sal.cardid=cust.EXTERNAL_CUSTOMER_IC )) GROUP BY EXTERNAL_CUSTOMER_IC,NAM_CUST_FULL,code,mainstation,deptname,COD_DRCR"
									+ ") GROUP BY mainstation,EXTERNAL_CUSTOMER_IC,NAM_CUST_FULL,code,deptname");
			HashMap<String, String> dkMap = dmo
					.getHashMapBySQLByDS(
							null,
							"select xd_col16,(sum(xd_col7)/10000) xd_col7 from ( "
									+ "(select distinct xd_col7,xd_col16,to_char(cast (cast (xd_col3 as timestamp) as date), 'yyyy-mm-dd') xd_col3 from wnbank.s_loan_dk where  xd_col7 >0) dk "
									+ "join (select cardid from wnsalarydb.v_sal_personinfo) sal on sal.cardid=dk.xd_col16 "
									+ ") where dk.xd_col3>='"
									+ curSelectMonthStart
									+ "' and dk.xd_col3<='" + curSelectDate
									+ "'  group by xd_col16");

			InsertSQLBuilder insert = new InsertSQLBuilder(
					"wn_gather_monitor_result");
			for (int i = 0; i < dealVo.length; i++) {// ��������ϸ�ʹ�����Ϣ���뵽���ݿ���
				insert.putFieldValue("dat_txn", curSelectMonth);// ����������
				insert.putFieldValue("amt_txn_sum",
						dealVo[i].getStringValue("summoney"));// ���׽��
				insert.putFieldValue("external_customer_ic",
						dealVo[i].getStringValue("EXTERNAL_CUSTOMER_IC"));// ���֤��
				insert.putFieldValue("name",
						dealVo[i].getStringValue("NAM_CUST_FULL"));// Ա������
				insert.putFieldValue("ck", "�鿴");
				insert.putFieldValue("deal_result", "δ����");
				insert.putFieldValue("deptname",
						dealVo[i].getStringValue("deptname"));// ������
				insert.putFieldValue("mainstation",
						dealVo[i].getStringValue("mainstation"));// ����λ
				insert.putFieldValue(
						"loan_balance",
						dkMap.get(dealVo[i]
								.getStringValue("EXTERNAL_CUSTOMER_IC")) == null ? "0.00"
								: dkMap.get(dealVo[i]
										.getStringValue("EXTERNAL_CUSTOMER_IC")));// �����������
				insert.putFieldValue("code", dealVo[i].getStringValue("code"));// ����Ա��code
				insert.putFieldValue("cod_drcr_c",
						dealVo[i].getStringValue("c_money"));
				insert.putFieldValue("cod_drcr_d",
						dealVo[i].getStringValue("d_money"));
				insert.putFieldValue("id", dmo.getSequenceNextValByDS(null,
						"S_WN_GATHER_MONITOR_RESULT")); // id�ֶ�
				insert.putFieldValue("status", "δ�ύ");// �ύ״̬ �����ύ ��ͨ�� ���˻�
				list.add(insert.getSQL());
			}
			dmo.executeBatchByDS(null, list);
			list.clear();
			// �һ���Ҫ������ϸ���ݣ���������ϸ����������鿴������ϸ ����
			HashVO[] dealInfo = dmo
					.getHashVoArrayByDS(
							null,
							"SELECT * FROM ( "
									+ " (SELECT  * FROM ("
									+ " select distinct to_char(to_date(substr(dat_txn,1,10),'yyyy-mm-dd'),'yyyy-mm-dd') dat_txn2,dat_txn, amt_txn ,txt_txn_desc,cod_acct_no,cod_drcr from wnbank.s_ofcr_ch_nobook "
									+ ") WHERE dat_txn2 >='"
									+ curSelectMonthStart
									+ "' and dat_txn2<='"
									+ curSelectDate
									+ "') deal "
									+ " JOIN (SELECT COD_ACCT_NO,COD_CUST,COD_ACCT_TITLE FROM wnbank.S_OFCR_CH_ACCT_MAST_"
									+ curSelectMonth.replaceAll("-", "")
									+ ") cod ON deal.cod_acct_no=cod.COD_ACCT_NO "
									+ " JOIN (SELECT COD_CUST_ID,EXTERNAL_CUSTOMER_IC,NAM_CUST_FULL FROM wnbank.S_OFCR_CI_CUSTMAST_"
									+ curSelectMonth.replaceAll("-", "")
									+ ") cust ON cod.COD_CUST=cust.COD_CUST_ID "
									+ " JOIN (SELECT code,name,cardid,mainstation, deptcode,deptname FROM WNSALARYDB.V_SAL_PERSONINFO) sal ON sal.cardid=cust.EXTERNAL_CUSTOMER_IC "
									+ ")");
			InsertSQLBuilder infoInsert = new InsertSQLBuilder("wn_deal_info"); // ������ϸ��
			for (int i = 0; i < dealInfo.length; i++) {
				infoInsert.putFieldValue("id",
						dmo.getSequenceNextValByDS(null, "S_WN_DEAL_INFO"));
				infoInsert.putFieldValue("dat_txn",
						dealInfo[i].getStringValue("dat_txn")); // ��ȷ��ʱ���������
				infoInsert.putFieldValue("dat_txn2",
						dealInfo[i].getStringValue("dat_txn2"));// �����յ�����
				infoInsert.putFieldValue("amt_txn",
						dealInfo[i].getStringValue("amt_txn"));// ������ϸ
				infoInsert.putFieldValue("txt_txn_desc",
						dealInfo[i].getStringValue("txt_txn_desc"));// ��������
				infoInsert.putFieldValue("cod_acct_no",
						dealInfo[i].getStringValue("cod_acct_no"));// �����˺�
				infoInsert.putFieldValue("name",
						dealInfo[i].getStringValue("name"));// Ա������
				infoInsert.putFieldValue("cardid",
						dealInfo[i].getStringValue("cardid"));// ���֤��
				infoInsert.putFieldValue("mainstation",
						dealInfo[i].getStringValue("mainstation"));// ����λ
				infoInsert.putFieldValue("cod_drcr",
						dealInfo[i].getStringValue("cod_drcr"));// �������� C ����D
				list.add(infoInsert.getSQL());
				if (list.size() >= 5000) {
					dmo.executeBatchByDS(null, list);
					list.clear();
				}
			}
			// if(list.size()>0){
			// dmo.executeBatchByDS(null, list);
			// list.clear();
			// }
			// ������ϸ�� �����д��ڽϵ�
			HashVO[] dkInfo = dmo
					.getHashVoArrayByDS(
							null,
							"select dkdate,dkdate2,dkmoney,jqmoney,cardid,name,code,deptname from ( "
									+ " (select distinct xd_col3 dkdate, to_char(cast (cast (xd_col3 as timestamp) as date), 'yyyy-mm-dd') dkdate2,xd_col6 dkmoney,xd_col7 jqmoney,xd_col16  from wnbank.s_loan_dk ) dk "
									+ "join (select cardid,name,code,deptname from wnsalarydb.v_sal_personinfo) sal on sal.cardid=dk.xd_col16 "
									+ ") where dkdate2>='"
									+ curSelectMonthStart + "' and dkdate2<='"
									+ curSelectDate + "' and jqmoney>0");
			InsertSQLBuilder dkInfoInsert = new InsertSQLBuilder("wn_dk_info");
			for (int i = 0; i < dkInfo.length; i++) {
				dkInfoInsert.putFieldValue("id",
						dmo.getSequenceNextValByDS(null, "S_WN_DK_INFO"));
				dkInfoInsert.putFieldValue("dkdate",
						dkInfo[i].getStringValue("dkdate"));// �������� ��ȷ��ʱ����
				dkInfoInsert.putFieldValue("dkdate2",
						dkInfo[i].getStringValue("dkdate2"));// �������� ��ȷ��������
				dkInfoInsert.putFieldValue("cardid",
						dkInfo[i].getStringValue("cardid"));// ���֤��
				dkInfoInsert.putFieldValue("dkmoney",
						dkInfo[i].getStringValue("dkmoney"));// ������
				dkInfoInsert.putFieldValue("jqmoney",
						dkInfo[i].getStringValue("jqmoney"));// ��Ƿ���
				dkInfoInsert.putFieldValue("name",
						dkInfo[i].getStringValue("name"));// ����
				dkInfoInsert.putFieldValue("code",
						dkInfo[i].getStringValue("code"));// Ա����
				dkInfoInsert.putFieldValue("deptname",
						dkInfo[i].getStringValue("deptname"));// ����
				list.add(dkInfoInsert.getSQL());
				if (list.size() >= 5000) {
					dmo.executeBatchByDS(null, list);
					list.clear();
				}
			}
			if (list.size() > 0) {
				dmo.executeBatchByDS(null, list);
				list.clear();
			}
			message = "���ݵ���ɹ�";
		} catch (Exception e) {
			message = "���ݵ���ʧ��";
			e.printStackTrace();
		}
		return message;
	}

	@Override
	public String dealExceptionData(BillVO[] billVos,
			Map<String, String> paraMap) {
		String message = "";
		try {// Ա���쳣��Ϊ����
			InsertSQLBuilder insert = new InsertSQLBuilder("wn_deal_monitor");
			UpdateSQLBuilder update = new UpdateSQLBuilder(
					"WN_GATHER_MONITOR_RESULT");
			UpdateSQLBuilder updateMonitor = new UpdateSQLBuilder(
					"wn_deal_monitor");
			List<String> list = new ArrayList<String>();
			// ��ǰ����һ��forѭ����������ȡ��ǰ�����Ƿ��Ѿ������
			StringBuilder existsSQL = new StringBuilder(
					"select monitor_id,id from wn_deal_monitor where monitor_id in (");
			for (int i = 0; i < billVos.length; i++) {
				if (i == billVos.length - 1) {
					existsSQL.append(billVos[i].getStringValue("id") + ")");
				} else {
					existsSQL.append(billVos[i].getStringValue("id") + ",");
				}
			}
			HashMap<String, String> existsMap = dmo.getHashMapBySQLByDS(null,
					existsSQL.toString());

			for (int i = 0; i < billVos.length; i++) {
				// ���Ȼ�ȡ��id�ֶ�
				String moniorId = billVos[i].getStringValue("id");
				if (existsMap.get(moniorId) == null) {// ��ʾ��ǰ��δ��������Ա�����µ��쳣���ݣ�����ֱ������
					insert.putFieldValue("dat_txn",
							billVos[i].getStringValue("dat_txn"));// ��������
					insert.putFieldValue("amt_txn",
							billVos[i].getStringValue("AMT_TXN_SUM"));
					insert.putFieldValue("amt_txn2",
							billVos[i].getStringValue("AMT_TXN_SUM2"));
					insert.putFieldValue("cod_acct_title",
							billVos[i].getStringValue("NAME"));
					insert.putFieldValue("EXTERNAL_CUSTOMER_IC",
							billVos[i].getStringValue("EXTERNAL_CUSTOMER_IC"));
					insert.putFieldValue("mainstation",
							billVos[i].getStringValue("mainstation"));
					insert.putFieldValue("deptname",
							billVos[i].getStringValue("deptname"));
					insert.putFieldValue("deal_result",
							paraMap.get("CHECK_RESULT"));
					insert.putFieldValue("deal_usercode",
							paraMap.get("CHECK_USERCODE"));
					insert.putFieldValue("deal_username",
							paraMap.get("CHECK_USERNAME"));
					insert.putFieldValue("deal_time", paraMap.get("CHECK_DATE"));
					insert.putFieldValue("deal_reason",
							paraMap.get("CHECK_REASON"));
					insert.putFieldValue("APPTH", paraMap.get("APPTH"));
					insert.putFieldValue("cod_drcr_c",
							billVos[i].getStringValue("cod_drcr_c"));
					insert.putFieldValue("cod_drcr_d",
							billVos[i].getStringValue("cod_drcr_d"));
					insert.putFieldValue("status", "δ�ύ");
					insert.putFieldValue("monitor_id",
							billVos[i].getStringValue("id"));
					insert.putFieldValue("id", dmo.getSequenceNextValByDS(null,
							"S_WN_DEAL_MONITOR"));// ����id
					// update.setWhereCondition("dat_txn='"
					// + billVos[i].getStringValue("dat_txn")
					// + "' and EXTERNAL_CUSTOMER_IC='"
					// + billVos[i].getStringValue("EXTERNAL_CUSTOMER_IC")
					// + "'");
					list.add(insert.getSQL());
				} else {
					updateMonitor.setWhereCondition("monitor_id="
							+ billVos[i].getStringValue("id"));
					updateMonitor.putFieldValue("deal_usercode",
							paraMap.get("CHECK_USERCODE"));
					updateMonitor.putFieldValue("deal_username",
							paraMap.get("CHECK_USERNAME"));
					updateMonitor.putFieldValue("deal_time",
							paraMap.get("CHECK_DATE"));
					updateMonitor.putFieldValue("deal_reason",
							paraMap.get("CHECK_REASON"));
					updateMonitor.putFieldValue("deal_result",
							paraMap.get("CHECK_RESULT"));
					updateMonitor.putFieldValue("APPTH", paraMap.get("APPTH"));
					list.add(updateMonitor.getSQL());
				}
				update.setWhereCondition("id="
						+ billVos[i].getStringValue("id"));
				update.putFieldValue("deal_result", paraMap.get("CHECK_RESULT"));
				update.putFieldValue("appth", paraMap.get("APPTH"));// ���Ӹ���

				list.add(update.getSQL());
			}
			if (list.size() > 0) {
				dmo.executeBatchByDS(null, list);
			}
			message = "����ɹ�";
		} catch (Exception e) {
			message = "����ʧ��";
			e.printStackTrace();
		}
		return message;
	}

	/**
	 * ������Ա�����������
	 */
	@Override
	public String finishGradeScore(BillVO[] bos, String pfUserName,
			String pfUserCode, String pfUserDept) {
		String message = "";
		try {
			StringBuffer msg = new StringBuffer("");
			for (int i = 0; i < bos.length; i++) {
				if ("�ܷ�".equals(bos[i].getStringValue("XIANGMU"))) {
					continue;
				}
				if (isEmpty(bos[i].getStringValue("KOUOFEN"))) {
					msg.append("��" + (i + 1) + "��������ĿΪ["
							+ bos[i].getStringValue("XIANGMU") + "],ָ��Ϊ["
							+ bos[i].getStringValue("ZHIBIAO") + "]�۷�������Ϊ��\n");
				}
				if (Double.parseDouble(bos[i].getStringValue("KOUOFEN")) > Double
						.parseDouble(bos[i].getStringValue("FENZHI"))) {
					msg.append("��" + (i + 1) + "��������ĿΪ["
							+ bos[i].getStringValue("XIANGMU") + "],ָ��Ϊ["
							+ bos[i].getStringValue("ZHIBIAO") + "]�۷�����ڷ�ֵ  \n");
				}
			}
			if (msg.length() > 0) {// �����쳣�����ݣ�Ϊ�ջ��߿۷�����ֵ����ֱ����ʾ�û���
				msg.append("���޸ĺ��������!!!");
				return msg.toString();
			}
			UpdateSQLBuilder update = new UpdateSQLBuilder("WN_GYPF_TABLE");
			String xiangmu = "�ܷ�";

			List<String> _sqllist = new ArrayList<String>();
			double result = 100.0;
			for (int i = 0; i < bos.length; i++) {
				if (xiangmu.equals(bos[i].getStringValue("XIANGMU"))) {
					update.setWhereCondition("USERCODE='"
							+ bos[i].getStringValue("USERCODE")
							+ "' AND PFTIME='"
							+ bos[i].getStringValue("PFTIME")
							+ "' AND XIANGMU= '" + xiangmu + "'");
					update.putFieldValue("KOUOFEN", result);
				} else {
					update.setWhereCondition("USERCODE='"
							+ bos[i].getStringValue("USERCODE")
							+ "' AND PFTIME='"
							+ bos[i].getStringValue("PFTIME") + "' AND KHSM= '"
							+ bos[i].getStringValue("KHSM")
							+ "' AND ZHIBIAO = '"
							+ bos[i].getStringValue("ZHIBIAO") + "'");
					// ����Ҫ��ÿ�ο۷֣�Ҫ��ȫ�����꣬Ҫô����
					if (Double.parseDouble(bos[i].getStringValue("KOUOFEN")) > 0) {
						result -= Double.parseDouble(bos[i]
								.getStringValue("FENZHI"));
						update.putFieldValue("KOUOFEN",
								bos[i].getStringValue("FENZHI"));
					} else {
						update.putFieldValue("KOUOFEN",
								bos[i].getStringValue("KOUOFEN"));
					}
				}
				update.putFieldValue("PFSUERCODE", pfUserCode);
				update.putFieldValue("PFUSERNAME", pfUserName);
				update.putFieldValue("PFUSERDEPT", pfUserDept);
				update.putFieldValue("STATE", "���ֽ���");
				update.putFieldValue("FHRESULT", "δ����");
				_sqllist.add(update.getSQL());
			}
			dmo.executeBatchByDS(null, _sqllist);
			message = "�����ɹ�";
		} catch (Exception e) {
			message = "�����쳣������";
			e.printStackTrace();
		}
		return message;
	}

	/**
	 * �����Ա�����������
	 */
	@Override
	public String saveGradeScore(BillVO[] bos) {// ����÷�
		String message = "";
		try {

			StringBuffer msg = new StringBuffer("");
			for (int i = 0; i < bos.length; i++) {
				if ("�ܷ�".equals(bos[i].getStringValue("XIANGMU"))) {
					continue;
				}
				if (isEmpty(bos[i].getStringValue("KOUOFEN"))) {
					msg.append("��" + (i + 1) + "��������ĿΪ["
							+ bos[i].getStringValue("XIANGMU") + "],ָ��Ϊ["
							+ bos[i].getStringValue("ZHIBIAO") + "]�۷�������Ϊ��\n");
				}
				if (Double.parseDouble(bos[i].getStringValue("KOUOFEN")) > Double
						.parseDouble(bos[i].getStringValue("FENZHI"))) {
					msg.append("��" + (i + 1) + "��������ĿΪ["
							+ bos[i].getStringValue("XIANGMU") + "],ָ��Ϊ["
							+ bos[i].getStringValue("ZHIBIAO") + "]�۷�����ڷ�ֵ  \n");
				}
			}
			if (msg.length() > 0) {// �����쳣�����ݣ�Ϊ�ջ��߿۷�����ֵ����ֱ����ʾ�û���
				msg.append("���޸ĺ��������!!!");
				return msg.toString();
			}
			// ��������Ǵ����ֵ������������1.�����ܷ֣�������ֵ��
			UpdateSQLBuilder update = new UpdateSQLBuilder("WN_GYPF_TABLE");
			String xiangmu = "�ܷ�";
			List<String> _sqllist = new ArrayList<String>();
			double result = 100.0;
			for (int i = 0; i < bos.length; i++) {
				if (xiangmu.equals(bos[i].getStringValue("XIANGMU"))) {
					update.setWhereCondition("USERCODE='"
							+ bos[i].getStringValue("USERCODE")
							+ "' AND PFTIME='"
							+ bos[i].getStringValue("PFTIME")
							+ "' AND XIANGMU= '" + xiangmu + "'");
					update.putFieldValue("KOUOFEN", result);
				} else {
					update.setWhereCondition("USERCODE='"
							+ bos[i].getStringValue("USERCODE")
							+ "' AND PFTIME='"
							+ bos[i].getStringValue("PFTIME") + "' AND KHSM= '"
							+ bos[i].getStringValue("KHSM")
							+ "' AND ZHIBIAO = '"
							+ bos[i].getStringValue("ZHIBIAO") + "'");
					// ����Ҫ��ÿ�ο۷֣�Ҫ��ȫ�����꣬Ҫô����
					if (Double.parseDouble(bos[i].getStringValue("KOUOFEN")) > 0) {
						result -= Double.parseDouble(bos[i]
								.getStringValue("FENZHI"));
						update.putFieldValue("KOUOFEN",
								bos[i].getStringValue("FENZHI"));
					} else {
						update.putFieldValue("KOUOFEN",
								bos[i].getStringValue("KOUOFEN"));
					}

				}
				_sqllist.add(update.getSQL());
			}
			dmo.executeBatchByDS(null, _sqllist);
			message = "����ɹ�";
		} catch (Exception e) {
			message = "����ʧ�ܣ���͹���Ա��ϵ�����쳣";
			e.printStackTrace();
		}
		return message;
	}

	private boolean isEmpty(String val) {
		if (val == null || "".equals(val)) {
			return true;
		}
		return false;
	}

	/**
	 * ���������������Ϣ��� ������� ������һ ������ �ͻ����� ���� ��ʽ: 202006
	 */
	@Override
	public String getNewChange(String lastDate, String curDate) {
		String message = "";
		try {
			// String diffSQL =
			// "SELECT dkl.b lastb, dkl.bu lastbu,dkl.bv lastbv,dkl.ba lastba,dk.b curb,dk.bu curbu,dk.bv curbv,dk.ba curba FROM ((select b,bu,BV,BA from wnsalarydb.s_loan_dk_"
			// + lastDate
			// + ") dkl"
			// + " LEFT JOIN (select b,bu,BV,BA from wnsalarydb.s_loan_dk_"
			// + curDate
			// + ") dk  ON dkl.b=dk.b "
			// + ") WHERE  dkl.bv!=dk.bv OR dkl.bu!=dk.bu OR dkl.ba!=dk.ba";
			// HashVO[] diffHashVo = dmo.getHashVoArrayByDS(null, diffSQL);
			// if (diffHashVo == null || diffHashVo.length <= 0) {
			// message = "��ǰ�ͻ�������Ϣһ�£�����Ҫ����";
			// return message;
			// }
			// UpdateSQLBuilder update = new UpdateSQLBuilder(
			// "wnsalarydb.s_loan_dk_" + lastDate);
			// List<String> list = new ArrayList<String>();
			// for (int i = 0; i < diffHashVo.length; i++) {
			// update.setWhereCondition("b='"
			// + diffHashVo[i].getStringValue("lastb") + "'");// �޸�ʱ�Ĳ�ѯ�������ͻ���
			// update.putFieldValue("bu",
			// diffHashVo[i].getStringValue("curbu"));
			// }
			// ���ȶ���SQL����ȡ�������´��ڲ��������
			lastDate=lastDate.replaceAll("-","").substring(0,6);
			curDate=curDate.replaceAll("-", "").substring(0,6);
			String sql = "SELECT  lastMonth.b lastb,lastMonth.ba lastba,lastMonth.bu lastbu,lastMonth.bv lastbv,lastMonth.bx lastbx,lastMonth.\"BY\" lastby,curMonth.b curb,curMonth.ba curba,curMonth.bu curbu,curMonth.bv curbv,curMonth.bx curbx,curMonth.\"BY\" curby FROM ("
					+ "(SELECT b,ba,bu,bv,bx,\"BY\" FROM WNSALARYDB.S_LOAN_DK_"
					+ lastDate
					+ ") lastMonth"
					+ " LEFT JOIN "
					+ "(SELECT b,ba,bu,bv,bx,\"BY\" FROM WNSALARYDB.S_LOAN_DK_"
					+ curDate
					+ ") curMonth"
					+ " ON lastMonth.b=curMonth.b"
					+ ") WHERE lastMonth.ba!=curMonth.ba OR  lastMonth.bu!=curMonth.bu OR lastMonth.bv!=curMonth.bv OR  lastMonth.bx !=curMonth.bx OR lastMonth.\"BY\"!=curMonth.\"BY\"";
			String[][] diff = dmo.getStringArrayByDS(null, sql);
			if(diff.length<=0){
				message="��"+lastDate+"������������";
			}else {
				/**
				 * ע��: ����ũ���Ч��Ҫ�����񻯣����Կͻ�����һ�Ϳͻ�����������ͳ�Ƶġ�
				 */
				UpdateSQLBuilder update=new  UpdateSQLBuilder("s_loan_dk_"+lastDate);
				List<String> list=new ArrayList<String>();
				for (int i = 0; i < diff.length; i++) {
					update.setWhereCondition("B='"+diff[i][0]+"'");// ��ѯ����
					update.putFieldValue("ba", diff[i][7]);//��ǰ����ͻ�������Ϣ���
					update.putFieldValue("bu", diff[i][8]);//����һ���
					update.putFieldValue("bv", diff[i][9]);//���������
					update.putFieldValue("bx",diff[i][10] );// �ͻ�����һ���
					update.putFieldValue("\"BY\"", diff[i][11]);//�ͻ����������
					list.add(update.getSQL());
					if(list.size()>=5000){
						dmo.executeBatchByDS(null, list);
						list.clear();
					}
				}
				if(list.size()>0){
					dmo.executeBatchByDS(null, list);
					list.clear();
				}
				message="��"+lastDate+"�����������Ѿ�������";
			}
		} catch (Exception e) {
			message = "������Ϣ���ʧ��";
			e.printStackTrace();
		}
		return message;
	}

	/**
	 * ��ũ�̻�ά��ָ�� ����ͳ��
	 * 
	 * @param curSelectMonthStart
	 *            :ѡ���µ��¿�ʼʱ�� 2020-05-01
	 * @param curSelectDate
	 *            : �û�ѡ��ʱ�� 2020-05-08
	 * @param curSelectMonth
	 *            :�û�ѡ���� 2020-05
	 * @param b
	 *            :�Ƿ����¼���
	 * @return
	 */
	@Override
	public String znCount(String curSelectMonthStart, String curSelectDate,
			String curSelectMonth, String dateInterval, boolean b) {
		String message = "";
		try {
			// �����ǰ����Ľ��
			if (b) {
				dmo.executeUpdateByDS(null,
						"delete from wn_znshcount_result where CURMONTH='"
								+ curSelectMonth + "'");
			}
			// ִ�м������������������������ݿ���
			String sql = "select xx.a mer_id  ,case when  znqk.num  is null then 0 else znqk.num end  znqk ,case when znzz.num is null then 0 else znzz.num end znzz ,case when  znsb.num is null then 0 else znsb.num end znsb ,case when znyl.num is null then 0 else znyl.num end znyl ,case when zndf.num is null then 0 else zndf.num end  zndf, case when dxqy.num is null then 0 else dxqy.num end  dxqy,(case when  znqk.num  is null then 0 else znqk.num end +case when znzz.num is null then 0 else znzz.num end +case when  znsb.num is null then 0 else znsb.num end +case when znyl.num is null then 0 else znyl.num end +case when zndf.num is null then 0 else zndf.num end+case when dxqy.num is null then 0 else dxqy.num end) total   from  ( select a,b,c from  wnsalarydb.excel_tab_83 where year||'-'||month='2020-04') xx left join (select mer_id,sum (num) num from (select mer_id,case when count(mer_id)>=5 then 5 else count(mer_id) end num  from  wnbank.t_dis_ifsp_intgr_txn_dtl where to_char(to_date(biz_dt,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
					+ curSelectMonthStart
					+ "' and to_char(to_date(biz_dt,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
					+ curSelectDate
					+ "' and mcht_prop='��ũ'   and txn_sub_type_desc='��ũȡ��' and txn_order_amt>=10 and txn_state='���׳ɹ�'  group by txn_acc_no,mer_id) group by mer_id) znqk on xx.a=znqk.mer_id left join (select mer_id,sum(num) num  from (select mer_id,case when  count(mer_id)>=5 then 5 else count(mer_id) end num  from  wnbank.t_dis_ifsp_intgr_txn_dtl where to_char(to_date(biz_dt,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
					+ curSelectMonthStart
					+ "' and to_char(to_date(biz_dt,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
					+ curSelectDate
					+ "' and mcht_prop='��ũ'   and txn_sub_type_desc='��ũת��' and txn_order_amt>=10 and txn_state='���׳ɹ�' group by oppo_acct_no,mer_id) group by mer_id) znzz on xx.a=znzz.mer_id left join ( "
					+ " select mer_id, count(mer_id)  num from  wnbank.t_dis_ifsp_intgr_txn_dtl where to_char(to_date(biz_dt,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
					+ curSelectMonthStart
					+ "' and to_char(to_date(biz_dt,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
					+ curSelectDate
					+ "' and mcht_prop='��ũ' and txn_type_desc='�籣˰�ѽɷѽ���' and txn_sub_type_desc='����ҽ�Ʊ��սɷѿۿ�'  and txn_state='���׳ɹ�'  group by  mer_id "
					+ ") znsb on xx.a=znsb.mer_id left join (select mer_id, count(mer_id)  num from  wnbank.t_dis_ifsp_intgr_txn_dtl where to_char(to_date(biz_dt,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
					+ curSelectMonthStart
					+ "' and to_char(to_date(biz_dt,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
					+ curSelectDate
					+ "' and mcht_prop='��ũ' and txn_type_desc='�籣˰�ѽɷѽ���' and txn_sub_type_desc='�������ϱ��սɷѿۿ�'  and txn_state='���׳ɹ�'  group by  mer_id) znyl on xx.a=znyl.mer_id left join (select mer_id, count(mer_id)  num from  wnbank.t_dis_ifsp_intgr_txn_dtl where to_char(to_date(biz_dt,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
					+ curSelectMonthStart
					+ "' and to_char(to_date(biz_dt,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
					+ curSelectDate
					+ "' and mcht_prop='��ũ' and txn_type_desc='��ѽɷѽ���' and txn_sub_type_desc in ('����ɷѣ���ѽɷѣ�','����ɷѣ����Ԥ�ɷѣ�')  and txn_state='���׳ɹ�'  group by  mer_id) zndf on xx.a=zndf.mer_id "
					+ " left join (select mer_id, count(mer_id)  num from wnbank.t_dis_ifsp_intgr_txn_dtl where to_char(to_date(biz_dt,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
					+ curSelectMonthStart
					+ "' and to_char(to_date(biz_dt,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
					+ curSelectDate
					+ "'  and  mcht_prop='��ũ' and txn_type_desc='���ܽ���' and  txn_sub_type_desc='����ǩԼ�����ǩԼ����Լ�������' and  txn_state='���׳ɹ�' group by mer_id) dxqy on dxqy.mer_id=xx.a";
			HashVO[] zn = dmo.getHashVoArrayByDS(null, sql);
			InsertSQLBuilder insert = new InsertSQLBuilder(
					"WN_ZNSHCOUNT_RESULT");
			List<String> list = new ArrayList<String>();
			for (int i = 0; i < zn.length; i++) {
				insert.putFieldValue("mer_id", zn[i].getStringValue("mer_id"));
				insert.putFieldValue("znqk", zn[i].getStringValue("znqk"));
				insert.putFieldValue("znzz", zn[i].getStringValue("znzz"));
				insert.putFieldValue("znyl", zn[i].getStringValue("znyl"));
				insert.putFieldValue("znsb", zn[i].getStringValue("znsb"));
				insert.putFieldValue("zndf", zn[i].getStringValue("zndf"));
				insert.putFieldValue("zndx", zn[i].getStringValue("dxqy"));
				insert.putFieldValue("total", zn[i].getStringValue("total"));
				insert.putFieldValue("curmonth", curSelectMonth);
				insert.putFieldValue("id", dmo.getSequenceNextValByDS(null,
						"S_WN_ZNSHCOUNT_RESULT"));
				insert.putFieldValue("dateInterval", dateInterval);
				list.add(insert.getSQL());
				if (list.size() >= 0) {
					dmo.executeBatchByDS(null, list);
					list.clear();
				}
			}
			if (!list.isEmpty()) {
				dmo.executeBatchByDS(null, list);
				list.clear();
			}
			message = "����ũ�̻�ά��������ɹ�";
		} catch (Exception e) {
			message = "����ũ�̻�ά��������ʧ�ܣ�����ϵ����Ա";
			e.printStackTrace();
		}
		return message;
	}
}