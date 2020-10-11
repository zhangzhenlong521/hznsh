package cn.com.infostrategy.bs.sysapp.database.transfer;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.common.SessionFactory;
import cn.com.infostrategy.bs.common.WLTDBConnection;
import cn.com.infostrategy.bs.common.WebCallBeanIfc;
import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.common.VectorMap;

/**
 * ������Ҫ��ɶ��Ǩ�ƹ��ܣ���������ĳЩ������ݣ�������ṹ������
 * �ڵ�������ʱ������Ѿ�����Щ�������ֶΣ����û��������ϣ��ٵ������ݡ����û����Щ�����½����ٵ������ݣ���������ṹ��֤
 * @author Ԭ����
 *
 */
public class CreateHtmlForDatabasesCompare implements WebCallBeanIfc {

	private CommDMO commDMO = null;

	@SuppressWarnings("unchecked")
	public String getHtmlContent(HashMap map) throws Exception {
		//ȡ�������������
		String type = (String) map.get("type"); //
		String dataSource1 = map.get("datasource1").toString();
		String dataSource2 = map.get("datasource2").toString();
		String databaseType = map.get("databaseType").toString(); //����������ݿ�����    Ԭ����  201304     �ݲ�����һ����oracle��һ����mysql�����
		String tabenamePrefix = map.get("tabenamePrefix").toString(); //��ӱ���ǰ׺���ˣ�����ͬ��̫���޷�����    Ԭ����  201304 

		commDMO = new CommDMO();

		StringBuffer sb_html = new StringBuffer(); //
		StringBuffer sb_blank = new StringBuffer(); //  ֻ���sql�ű�
		sb_html.append("<html>\r\n");
		sb_html.append("<head>\r\n");
		sb_html.append("<META http-equiv=Content-Type content=\"text/html; charset=GBK\">\r\n"); //
		sb_html.append("<style   type=\"text/css\"> \r\n");
		sb_html.append("<!--   \r\n");
		sb_html.append(" table   {  border-collapse:   collapse; }   \r\n");
		sb_html.append("td   {  font-size: 12px; border:solid   1px   #888888;  }   \r\n");
		sb_html.append(" -->   \r\n");
		sb_html.append(" </style>   \r\n");
		sb_html.append("</head>\r\n");
		sb_html.append("<body>\r\n");
		if (null != databaseType && databaseType.equals("Oracle")) {
			if (type.equals("0")) {
				sb_html.append(getOraTabOrColumnHtml(dataSource1, dataSource2)); //
			} else if (type.equals("1")) {
				sb_html.append(getOraTabOrColumnHtml(dataSource2, dataSource1)); //
			} else if (type.equals("2")) {
				sb_html.append(getOraCompareHtml(dataSource1, dataSource2)); //
			}
		} else if (null != databaseType && databaseType.equals("Mysql")) { //�����Mysql   //��Ϊ�����mysql���ݿ⣬���������mysql�ĵ��뵼��
			if (type.equals("1")) {//��ṹ�Ƚ�
				sb_blank.append(getMysCompareHtml(dataSource1, dataSource2, tabenamePrefix)); //
				return "��ִ�е�sql�ű����£�<br>\r\n" + sb_blank.toString();
			} else if (type.equals("2")) {//��������������
				sb_blank.append(importInsertTabData(dataSource1, dataSource2, tabenamePrefix)); //
				return "�������sql�ű����£�<br>\r\n" + sb_blank.toString();
			} else if (type.equals("3")) {//�������б�����
				sb_html.append(importExistTabData(dataSource1, dataSource2, tabenamePrefix)); //
				return "�޸ı��sql�ű����£�<br>\r\n" + sb_html.toString();
			} else if (type.equals("4")) { //������������
				sb_blank.append(importDataFrom1To2(dataSource1, dataSource2, tabenamePrefix)); //
				return "��ִ�е�sql�ű����£�<br>\r\n" + sb_blank.toString();
			}
		}

		sb_html.append("</body>\r\n");
		sb_html.append("</html>\r\n");
		return sb_html.toString(); //
	}

	/**
	 * ���������������
	 * @param dataSource2
	 * @param dataSource1
	 * @param tabenamePrefix
	 * @return
	 */
	private String importInsertTabData(String _dsname_1, String _dsname_2, String _prefix) throws Exception {
		VectorMap map1 = getMap("Mysql", _dsname_1, _prefix); //�Ȼ������Դ1�İ��ո������������ı�ṹ
		VectorMap map2 = getMap("Mysql", _dsname_2, _prefix);//�Ȼ������Դ1�İ��ո������������ı�ṹ
		//ֻ��ȡ��ͬ��sql��䣬Ȼ�����������
		//1����ִ�б�ṹ�Ĳ�ͬ
		StringBuffer sb = new StringBuffer();
		String[] str_alltabs = map1.getKeysAsString(); //�ӱ���Դ1�����б�ʼ!!
		//��Ҫ��¼ִ�й�sql�ű��ı�����ִ����ű����һ���޷�������Ҫ�������ݵı�
		HashSet vec_table = new HashSet();
		for (int i = 0; i < str_alltabs.length; i++) {
			if (map2.containsKey(str_alltabs[i])) {//����иñ���������ֻ�Ƚ������ı�
			} else { //������ѭ�����������
				vec_table.add(str_alltabs[i]);//���б仯�ı��¼����
				VectorMap v_columns = (VectorMap) map1.get(str_alltabs[i]);
				String[] str_allcolumns = v_columns.getKeysAsString(); //
				for (int j = 0; j < str_allcolumns.length; j++) { //����������
					String[] str_colinfo = (String[]) v_columns.get(str_allcolumns[j]); //ȡ������Ϣ
					sb.append(j == 0 ? getCreateTableSQL(str_alltabs[i], v_columns) : ""); //
				}
			}
		}
		CommDMO dmo = new CommDMO();
		String str_1 = new TBUtil().replaceAll(sb.toString(), "<br>", "");//ȥ��br��ʽ���ַ�
		String str_temp = new TBUtil().replaceAll(str_1, "\r\n", "");//ȥ��br��ʽ���ַ�
		String[] sql_str = new TBUtil().split(str_temp.toString(), ";");
		WLTDBConnection conn = SessionFactory.getInstance().getConnection(Thread.currentThread(), _dsname_2); //ȡ�õ�ǰ�̵߳�����!! �ں���һ�ű���ִ��
		Statement stmt = conn.createStatement();
		String[] strs = new String[60];
		for (int kk = 0; kk < 60; kk++) {
			strs[kk] = sql_str[kk];
		}
		dmo.executeBatchByDSImmediately(_dsname_2, strs);

		//stmt.executeBatch();
		//2����1�е����ݵ��뵽2��
		WLTDBConnection conn1 = SessionFactory.getInstance().getConnection(Thread.currentThread(), _dsname_2); //ȡ�õ�ǰ�̵߳�����!! �ں���һ�ű���ִ��
		Statement stmt1 = conn.createStatement();
		DataSourceVO[] dsvs = ServerEnvironment.getInstance().getDataSourceVOs();
		String DBNewUser = new TBUtil().getDBUserFromDSName(dsvs, _dsname_2, "mysql");
		String DBOldUser = new TBUtil().getDBUserFromDSName(dsvs, _dsname_1, "mysql");
		for (int i2 = 0; i2 < str_alltabs.length; i2++) {
			String sql_insert = "insert into " + DBNewUser + "." + str_alltabs[i2] + " select * from " + DBOldUser + "." + str_alltabs[i2];
			stmt1.addBatch(sql_insert);
		}
		//stmt1.executeBatch();
		return sb.toString();
	}

	/**
	 * �������б�����ݣ���Ҫ����֮ǰ�Ļ����ϰ�û�е��ֶθ������Ȼ��������
	 * @param dataSource1
	 * @param dataSource2
	 * @param tabenamePrefix
	 * @return
	 */

	private String importExistTabData(String _dsname_1, String _dsname_2, String _prefix) throws Exception {
		VectorMap map1 = getMap("Mysql", _dsname_1, _prefix); //�Ȼ������Դ1�İ��ո������������ı�ṹ
		VectorMap map2 = getMap("Mysql", _dsname_2, _prefix);//�Ȼ������Դ1�İ��ո������������ı�ṹ
		StringBuffer sb = new StringBuffer();
		String[] str_alltabs = map1.getKeysAsString(); //�ӱ���Դ1�����б�ʼ!!
		ArrayList l_existtab = new ArrayList();
		HashSet vec_table = new HashSet();
		//���޸ı�ṹ
		for (int i = 0; i < str_alltabs.length; i++) {
			if (map2.containsKey(str_alltabs[i])) { //���Դ2���иñ�,��ֻ�Ƚ���..
				l_existtab.add(str_alltabs[i]);
				VectorMap v_columns1 = (VectorMap) map1.get(str_alltabs[i]);
				VectorMap v_columns2 = (VectorMap) map2.get(str_alltabs[i]); //ȡ��Դ2�еĵĸ�����������
				String[] str_allcolumns1 = v_columns1.getKeysAsString(); //
				for (int j = 0; j < str_allcolumns1.length; j++) { //����������
					if (!v_columns2.containsKey(str_allcolumns1[j])) { //���Դ2�иñ���������,�����
						vec_table.add(str_alltabs[i]);//���б仯�ı��¼����
						String[] str_colinfo = (String[]) v_columns1.get(str_allcolumns1[j]); //ȡ������Ϣ
						sb.append("alter table " + str_alltabs[i] + " add " + str_colinfo[0] + " " + str_colinfo[1] + "(" + str_colinfo[2] + ")" + ";" + "<br>"); //
					} else if (v_columns2.containsKey(str_allcolumns1[j]))//�����ͬ��Ҫ�Ƚ��ֶγ����Ƿ����
					{
						String[] collen_1 = (String[]) v_columns1.get(str_allcolumns1[j]); //ȡ������Ϣ
						String[] collen_2 = (String[]) v_columns2.get(str_allcolumns1[j]); //ȡ������Ϣ
						String tt = Integer.parseInt(collen_2[2]) > Integer.parseInt(collen_1[2]) ? collen_2[2] : collen_1[2];//����Ϊ�ɱ���±�����ֵ
						if (collen_1[1].trim().equals(collen_2[1].trim()) && (!collen_1[2].trim().equals(collen_2[2].trim()))) {
							sb.append("alter table " + str_alltabs[i] + " modify column " + collen_2[0] + " " + collen_2[1] + "(" + tt + ")" + ";" + "<br>"); //
						} else if (!collen_1[1].trim().equals(collen_2[1].trim())) {//����������Ͳ���ͬ���򲻱仯�������µ�
							vec_table.add(str_alltabs[i]);//���б仯�ı��¼����
							sb.append("alter table " + str_alltabs[i] + " modify column " + collen_2[0] + " " + collen_2[1] + "(" + collen_2[2] + ")" + ";" + "<br>"); //
						}
					}
				}
			}
		}
		WLTDBConnection conn = SessionFactory.getInstance().getConnection(Thread.currentThread(), _dsname_2); //ȡ�õ�ǰ�̵߳�����!! �ں���һ�ű���ִ��
		Statement stmt = conn.createStatement();
		String str_temp = new TBUtil().replaceAll(sb.toString(), "<br>", "");//ȥ��br��ʽ���ַ�
		String[] sql_str = new TBUtil().split(str_temp.toString(), ";");
		for (int ii = 0; ii < sql_str.length; ii++) {
			stmt.addBatch(sql_str[ii]);
		}
		stmt.executeBatch();

		//		1�е����ݵ��뵽2��
		WLTDBConnection conn1 = SessionFactory.getInstance().getConnection(Thread.currentThread(), _dsname_2); //ȡ�õ�ǰ�̵߳�����!! �ں���һ�ű���ִ��
		Statement stmt1 = conn.createStatement();
		DataSourceVO[] dsvs = ServerEnvironment.getInstance().getDataSourceVOs();
		String DBNewUser = new TBUtil().getDBUserFromDSName(dsvs, _dsname_2, "mysql");
		String DBOldUser = new TBUtil().getDBUserFromDSName(dsvs, _dsname_1, "mysql");

		for (int i2 = 0; i2 < l_existtab.size(); i2++) {
			StringBuffer sql_insert = new StringBuffer("insert into " + DBNewUser + "." + l_existtab.get(i2));
			VectorMap v_columns1 = (VectorMap) map1.get(l_existtab.get(i2)); //��ȡmap1���ֶΣ���Ϊ�Ѿ���Map1�е��ֶθ��ƽ�map2����
			StringBuffer tempsb = new StringBuffer();
			String[] str_allcolumns1 = v_columns1.getKeysAsString(); //ȡ�������ֶν���ƴ��sql
			for (int i3 = 0; i3 < str_allcolumns1.length - 1; i3++) { //����������  ȡ�����һ���ֶ�ǰ�������ֶ�
				String[] str_colinfo = (String[]) v_columns1.get(str_allcolumns1[i3]); //ȡ������Ϣ
				tempsb.append(str_colinfo[0] + ","); //
			}
			tempsb.append(str_allcolumns1[str_allcolumns1.length - 1]);//���һ������Ҫ����
			String sql = sql_insert.append("(").append(tempsb.toString()).append(")").append(" select ").append(tempsb.toString()).append(" from ").append(DBOldUser).append(".").append(l_existtab.get(i2)).toString();
			//StringBuffer sql_select=new  StringBuffer(* from "+DBOldUser+"."+str_alltabs[i2]);	

			//������Ҫɾ��������
			String deldata_sql = "truncate table " + DBNewUser + "." + l_existtab.get(i2) + ";";

			stmt1.addBatch(deldata_sql);
			stmt1.addBatch(sql);
		}

		stmt1.executeBatch();

		return sb.toString();
	}

	/**1�����Դ1��Դ2û�еı�������Դ2���������ٵ�������
	 * 2�����Դ1����Դ2��Ҳ����ֻ���Դ2��û�е��ֶΣ��޸ĳ��ȸ�Դ1��һ�£���ɾ��Դ2�е��ֶΣ���������ǰ��Ҫɾ��Դ2�е�����
	 * 3����󽫶�����Դ2���е�sql����������
	 * Ԭ����  ��ӵ�������ݹ��ܰ�����ṹ
	 * @param _dsname_1
	 * @param _dsname_2
	 * @param _prefix
	 * @return
	 */
	private String importDataFrom1To2(String _dsname_1, String _dsname_2, String _prefix) throws Exception {
		VectorMap map1 = getMap("Mysql", _dsname_1, _prefix); //�Ȼ������Դ1�İ��ո������������ı�ṹ
		VectorMap map2 = getMap("Mysql", _dsname_2, _prefix);//�Ȼ������Դ1�İ��ո������������ı�ṹ
		//ֻ��ȡ��ͬ��sql��䣬Ȼ�����������
		//1����ִ�б�ṹ�Ĳ�ͬ
		StringBuffer sb = new StringBuffer();
		String[] str_alltabs = map1.getKeysAsString(); //�ӱ���Դ1�����б�ʼ!!
		//��Ҫ��¼ִ�й�sql�ű��ı�����ִ����ű����һ���޷�������Ҫ�������ݵı�
		HashSet vec_table = new HashSet();
		for (int i = 0; i < str_alltabs.length; i++) {
			if (map2.containsKey(str_alltabs[i])) { //���Դ2���иñ�,��ֻ�Ƚ���..
				VectorMap v_columns1 = (VectorMap) map1.get(str_alltabs[i]);
				VectorMap v_columns2 = (VectorMap) map2.get(str_alltabs[i]); //ȡ��Դ2�еĵĸ�����������
				String[] str_allcolumns1 = v_columns1.getKeysAsString(); //
				for (int j = 0; j < str_allcolumns1.length; j++) { //����������
					if (!v_columns2.containsKey(str_allcolumns1[j])) { //���Դ2�иñ���������,�����
						vec_table.add(str_alltabs[i]);//���б仯�ı��¼����
						String[] str_colinfo = (String[]) v_columns1.get(str_allcolumns1[j]); //ȡ������Ϣ
						sb.append("alter table " + str_alltabs[i] + " add " + str_colinfo[0] + " " + str_colinfo[1] + "(" + str_colinfo[2] + ")" + ";"); //
					} else if (v_columns2.containsKey(str_allcolumns1[j]))//�����ͬ��Ҫ�Ƚ��ֶγ����Ƿ����
					{
						String[] collen_1 = (String[]) v_columns1.get(str_allcolumns1[j]); //ȡ������Ϣ
						String[] collen_2 = (String[]) v_columns2.get(str_allcolumns1[j]); //ȡ������Ϣ
						if ((!collen_1[1].trim().equals(collen_2[1].trim())) || (!collen_1[2].trim().equals(collen_2[2].trim()))) {
							vec_table.add(str_alltabs[i]);//���б仯�ı��¼����
							sb.append("alter table " + str_alltabs[i] + " modify column " + collen_2[0] + " " + collen_2[1] + "(" + collen_2[2] + ")" + ";"); //
						}

					}
				}
			} else { //������ѭ�����������
				vec_table.add(str_alltabs[i]);//���б仯�ı��¼����
				VectorMap v_columns = (VectorMap) map1.get(str_alltabs[i]);
				String[] str_allcolumns = v_columns.getKeysAsString(); //
				for (int j = 0; j < str_allcolumns.length; j++) { //����������
					String[] str_colinfo = (String[]) v_columns.get(str_allcolumns[j]); //ȡ������Ϣ
					sb.append(j == 0 ? getCreateTableSQL(str_alltabs[i], v_columns) : ""); //
				}
			}
		}
		String str_temp = new TBUtil().replaceAll(sb.toString(), "<br>", "");//ȥ��br��ʽ���ַ�
		String[] sql_str = new TBUtil().split(str_temp.toString(), ";");
		WLTDBConnection conn = SessionFactory.getInstance().getConnection(Thread.currentThread(), _dsname_2); //ȡ�õ�ǰ�̵߳�����!! �ں���һ�ű���ִ��
		Statement stmt = conn.createStatement();
		for (int ii = 0; ii < sql_str.length; ii++) {
			stmt.addBatch(sql_str[ii]);
		}
		stmt.executeBatch(); //������ִ��sql�ű�
		//2����ִ������ ��Ҫ��ȡԴ1�еı�����ƴ��values�����ֵ����ȡ����Դ2�еı�ṹ����ƴ��insert into���������
		//������ƴ��  insert into test(id,name,sex) values('1','С��','female');
		System.out.println("��Ҫ�������ݵı�ĸ���Ϊ==============================" + vec_table.size());
		Iterator it = vec_table.iterator();
		CommDMO comm = new CommDMO();
		while (it.hasNext()) {
			String tableName = (String) it.next();
			ArrayList sql_list = new ArrayList();
			//i1������ɾ��Դ2�ı��е�����
			String deldata_sql = "truncate table " + tableName + ";";
			sql_list.add(deldata_sql);//��ɾ��Դ2����Ӧ�������
			comm.executeBatchByDSImmediately(_dsname_2, sql_list);//��ɾ�����е�����
			//i2��ȡԴ2����Ӧ�������  ��ƴ��valuesǰ���sql
			VectorMap v_columns1 = (VectorMap) map1.get(tableName); //��ȡmap1���ֶΣ���Ϊ�Ѿ���Map1�е��ֶθ��ƽ�map2����
			String[] str_allcolumns1 = v_columns1.getKeysAsString(); //ȡ�������ֶν���ƴ��sql
			StringBuffer sb_insert = new StringBuffer("set names gbk; ");
			sb_insert.append("insert into " + tableName + "(");
			for (int j = 0; j < str_allcolumns1.length - 1; j++) { //����������  ȡ�����һ���ֶ�ǰ�������ֶ�
				String[] str_colinfo = (String[]) v_columns1.get(str_allcolumns1[j]); //ȡ������Ϣ
				sb_insert.append(str_colinfo[0] + ","); //
			}
			sb_insert.append(str_allcolumns1[str_allcolumns1.length - 1]).append(") values(");//���һ������Ҫ����
			//i3��ȡԴ2����Ӧ�������  ��ƴ��values�����sql����Ҫ������Դ1�н��в�ѯ	
			//select col1,col2,col3������from t1
			StringBuffer sb_query = new StringBuffer();//��ѯ����Դ1��sqlƴд
			sb_query.append("select ");
			for (int j = 0; j < str_allcolumns1.length - 1; j++) { //����������  ȡ�����һ���ֶ�ǰ�������ֶ�
				String[] str_colinfo = (String[]) v_columns1.get(str_allcolumns1[j]); //ȡ������Ϣ
				sb_query.append(str_colinfo[0] + ","); //
			}
			sb_query.append(str_allcolumns1[str_allcolumns1.length - 1]);
			sb_query.append(" from " + tableName);
			WLTDBConnection conn_1 = SessionFactory.getInstance().getConnection(Thread.currentThread(), _dsname_1); //ȡ�õ�ǰ�̵߳�����!! �ں���һ�ű���ִ�У�һ��Ҫ��Դ1��ִ�в���
			Statement stmt_1 = conn_1.createStatement();//�����ظ�ִ�У������������¶���
			//String _datasourcename, String _initSql, boolean _isDebugLog, boolean _isUpperLimit, int[] _rowArea, boolean _isNeedCount, boolean _isImmediately, boolean _isPrepareSQL
			HashVO[] vos = comm.getHashVoArrayByDS(_dsname_1, sb_query.toString());
			if (null != vos && vos.length <= 5000) { //���С�ڽ����5000��ֱ��ƴ��
				Statement stmt1 = conn.createStatement();
				for (int k = 0; k < vos.length; k++) {
					HashVO vo = vos[k];
					String[] str_results = new String[str_allcolumns1.length];
					StringBuffer tempbuffer = new StringBuffer();
					StringBuffer tempbuffer1 = new StringBuffer();
					for (int k1 = 0; k1 < str_results.length - 1; k1++) {
						str_results[k1] = vo.getStringValue(k1);
						String[] str_colinfo = (String[]) v_columns1.get(str_allcolumns1[k1]); //ȡ������Ϣ
						//������Ҫ�����ж��������� ���ܵ����ĸ���ַ���
						if (str_colinfo[1].equals("INT") || str_colinfo[1].equals("INTEGER") || str_colinfo[1].equals("DOUBLE") || str_colinfo[1].equals("FLOAT") || str_colinfo[1].equals("DECIMAL")) {
							tempbuffer1.append(str_results[k1]).append(",");
						} else {
							tempbuffer1.append(str_results[k1] == null ? "''" : "'" + str_results[k1] + "'").append(",");
						}
					}
					//ȡ�����һ�е���Ϣ
					String[] str_colinfo1 = (String[]) v_columns1.get(str_allcolumns1[str_allcolumns1.length - 1]); //ȡ������Ϣ
					if (str_colinfo1[1].equals("INT") || str_colinfo1[1].equals("INTEGER") || str_colinfo1[1].equals("DOUBLE") || str_colinfo1[1].equals("FLOAT") || str_colinfo1[1].equals("DECIMAL")) {
						tempbuffer.append(sb_insert.toString()).append(tempbuffer1.toString()).append(vo.getStringValue(str_results.length - 1)).append(");");
					} else {
						tempbuffer.append(sb_insert.toString()).append(tempbuffer1.toString()).append(vo.getStringValue(str_results.length - 1) == null ? "''" : "'" + vo.getStringValue(str_results.length - 1) + "'").append(");");
					}

					stmt1.executeUpdate(tempbuffer.toString());
					//stmt1.addBatch(tempbuffer.toString());
				}
				//stmt1.executeBatch();
			} else if (null != vos && vos.length > 5000) {//������������5000������Ҫ�ֶ��ִ��
				int k1 = vos.length / 5000 + 1;
				for (int k2 = 1; k2 < k1; k2++) {//�Ȳ�����������
					Statement stmt2 = conn.createStatement();//�����ظ�ִ�У������������¶���
					for (int k3 = (k2 - 1) * 5000; k3 < k2 * 5000; k3++) {
						HashVO vo = vos[k3];
						String[] str_results = new String[str_allcolumns1.length];
						StringBuffer tempbuffer = new StringBuffer();
						StringBuffer tempbuffer1 = new StringBuffer();
						for (int k4 = 0; k4 < str_results.length - 1; k4++) {
							str_results[k4] = vo.getStringValue(k4);
							String[] str_colinfo = (String[]) v_columns1.get(str_allcolumns1[k1]); //ȡ������Ϣ
							//������Ҫ�����ж��������� ���ܵ����ĸ���ַ���
							if (str_colinfo[1].equals("INT") || str_colinfo[1].equals("INTEGER") || str_colinfo[1].equals("DOUBLE") || str_colinfo[1].equals("FLOAT") || str_colinfo[1].equals("DECIMAL")) {
								tempbuffer1.append(str_results[k4]).append(",");
							} else {
								tempbuffer1.append(str_results[k4] == null ? "''" : "'" + str_results[k4] + "'").append(",");
							}
						}
						String[] str_colinfo1 = (String[]) v_columns1.get(str_allcolumns1[str_allcolumns1.length - 1]); //ȡ������Ϣ
						if (str_colinfo1[1].equals("INT") || str_colinfo1[1].equals("INTEGER") || str_colinfo1[1].equals("DOUBLE") || str_colinfo1[1].equals("FLOAT") || str_colinfo1[1].equals("DECIMAL")) {
							tempbuffer.append(sb_insert.toString()).append(tempbuffer1.toString()).append(vo.getStringValue(str_results.length - 1)).append(");");
						} else {
							tempbuffer.append(sb_insert.toString()).append(tempbuffer1.toString()).append(vo.getStringValue(str_results.length - 1) == null ? "''" : "'" + vo.getStringValue(str_results.length - 1) + "'").append(");");
						}
						stmt2.addBatch(tempbuffer.toString());
					}
					stmt2.executeBatch();
				}
				//ʣ���Ϊ����������
				Statement stmt3 = conn.createStatement();//�����ظ�ִ�У������������¶���
				for (int k5 = 5000 * (k1 - 1); k5 < vos.length; k5++) {//���ʣ��ģ��϶�С��5000��
					HashVO vo = vos[k5];
					String[] str_results = new String[str_allcolumns1.length];
					StringBuffer tempbuffer = new StringBuffer();
					StringBuffer tempbuffer1 = new StringBuffer();
					for (int k6 = 0; k6 < str_results.length - 1; k6++) {
						str_results[k6] = vo.getStringValue(k6);
						String[] str_colinfo = (String[]) v_columns1.get(str_allcolumns1[k1]); //ȡ������Ϣ
						//������Ҫ�����ж��������� ���ܵ����ĸ���ַ���
						if (str_colinfo[1].equals("INT") || str_colinfo[1].equals("INTEGER") || str_colinfo[1].equals("DOUBLE") || str_colinfo[1].equals("FLOAT") || str_colinfo[1].equals("DECIMAL")) {
							tempbuffer1.append(str_results[k6]).append(",");
						} else {
							tempbuffer1.append(str_results[k6] == null ? "''" : "'" + str_results[k6] + "'").append(",");
						}
					}
					String[] str_colinfo1 = (String[]) v_columns1.get(str_allcolumns1[str_allcolumns1.length - 1]); //ȡ������Ϣ
					if (str_colinfo1[1].equals("INT") || str_colinfo1[1].equals("INTEGER") || str_colinfo1[1].equals("DOUBLE") || str_colinfo1[1].equals("FLOAT") || str_colinfo1[1].equals("DECIMAL")) {
						tempbuffer.append(sb_insert.toString()).append(tempbuffer1.toString()).append(vo.getStringValue(str_results.length - 1)).append(");");
					} else {
						tempbuffer.append(sb_insert.toString()).append(tempbuffer1.toString()).append(vo.getStringValue(str_results.length - 1) == null ? "''" : "'" + vo.getStringValue(str_results.length - 1) + "'").append(");");
					}
					stmt3.addBatch(tempbuffer.toString());
				}
				stmt3.executeBatch();
			}

		}

		return sb.toString();
	}

	/**
	 * mysql�ıȽ�ʵ��   Ԭ����  20130425 ���
	 * ����Դ�еĲ�ͬ���ݱȽ�
	 * @param dataSource1
	 * @param dataSource2
	 * @return
	 */
	private String getMysCompareHtml(String _dsname_1, String _dsname_2, String _prefix) throws Exception {
		//Դ1�е�����..
		VectorMap map1 = getMap("Mysql", _dsname_1, _prefix); //�������Դ1��
		VectorMap map2 = getMap("Mysql", _dsname_2, _prefix);

		//�ҳ�ֻ������1��������2�е�����..
		StringBuffer sb_html_t = new StringBuffer(); //�������html���   
		StringBuffer sb_html_c = new StringBuffer(); //��ͬ�е�html���
		sb_html_t.append("<br><font size=5 color=\"red\">�������������£�</font>");
		sb_html_t.append("<table width=\"75%\"   border=\"1\"   cellspacing=\"0\"   cellpadding=\"3\">"); //
		sb_html_t.append("<tr><td align=\"center\">����</td><td align=\"center\">����</td><td align=\"center\">������</td><td align=\"center\">�п��</td><td>SQL���</td></tr>"); //
		sb_html_c.append("<br><font size=5 color=\"red\">�в�ͬ�ı��������£�</font>");
		sb_html_c.append("<br><table width=\"75%\"   border=\"1\"   cellspacing=\"0\"   cellpadding=\"3\">"); //
		sb_html_c.append("<tr><td align=\"center\">����</td><td align=\"center\">����</td><td align=\"center\">��������</td><td align=\"center\">���п��</td><td align=\"center\">��������</td><td align=\"center\">���п��</td><td>SQL���</td></tr>"); //
		String[] str_alltabs = map1.getKeysAsString(); //�ӱ���Դ1�����б�ʼ!!
		int li_count = 0; //
		int li_count_newtab = 0; //ͳ��������ĸ���
		int li_count_newcol = 0; //ͳ���в�ͬ�ı�ĸ���
		for (int i = 0; i < str_alltabs.length; i++) {
			if (map2.containsKey(str_alltabs[i])) { //���Դ2���иñ�,��ֻ�Ƚ���..
				VectorMap v_columns1 = (VectorMap) map1.get(str_alltabs[i]);
				VectorMap v_columns2 = (VectorMap) map2.get(str_alltabs[i]); //ȡ��Դ2�еĵĸ�����������
				String[] str_allcolumns1 = v_columns1.getKeysAsString(); //
				boolean bo_iffind = false; //
				//��Ҫ�ȼ������ͬ�еı�ĸ����������ϱߵ���Ŀ����ʾ
				for (int m = 0; m < str_allcolumns1.length; m++) {
					if (!v_columns2.containsKey(str_allcolumns1[m])) {
						li_count_newcol++;
						break;
					}
				}
				for (int m = 0; m < str_allcolumns1.length; m++) {
					if (!v_columns2.containsKey(str_allcolumns1[m])) {
						bo_iffind = true;
						break;
					} else if (v_columns2.containsKey(str_allcolumns1[m])) {
						String[] collen_1 = (String[]) v_columns1.get(str_allcolumns1[m]); //ȡ������Ϣ
						String[] collen_2 = (String[]) v_columns2.get(str_allcolumns1[m]); //ȡ������Ϣ	
						if ((!collen_1[1].trim().equals(collen_2[1].trim())) || (!collen_1[2].trim().equals(collen_2[2].trim()))) {
							bo_iffind = true;
							break;
						}
					}
				}
				if (bo_iffind) {
					if (li_count != 1) {
						sb_html_c.append("<tr><td colspan=5>&nbsp;<br></td></tr>");
					}
					sb_html_c.append("<tr><td colspan=7 bgcolor=\"cyan\">[" + li_count_newcol + "]����Դ[" + _dsname_1 + "]������Դ[" + _dsname_2 + "]�ڱ�[" + str_alltabs[i] + "]��ͬ����</td></tr>"); //
					for (int j = 0; j < str_allcolumns1.length; j++) { //����������
						if (!v_columns2.containsKey(str_allcolumns1[j])) { //���Դ2�иñ���������,�����
							li_count++;
							String[] str_colinfo = (String[]) v_columns1.get(str_allcolumns1[j]); //ȡ������Ϣ
							sb_html_c.append("<tr><td>" + str_alltabs[i] + "</td><td><font color=\"blue\">" + str_colinfo[0] + "</font></td><td>" + str_colinfo[1] + "</td><td>" + str_colinfo[2] + "</td><td></td><td></td><td>alter table " + str_alltabs[i] + " add " + str_colinfo[0] + " "
									+ str_colinfo[1] + "(" + str_colinfo[2] + ")" + ";</td></tr>"); //
						} else if (v_columns2.containsKey(str_allcolumns1[j])) {//�����������Ҫ�Ƚ��г����Ƿ����
							String[] collen_1 = (String[]) v_columns1.get(str_allcolumns1[j]); //ȡ������Ϣ
							String[] collen_2 = (String[]) v_columns2.get(str_allcolumns1[j]); //ȡ������Ϣ
							if ((!collen_1[1].trim().equals(collen_2[1].trim())) || (!collen_1[2].trim().equals(collen_2[2].trim()))) {//������Ͳ�һ��
								String tt = Integer.parseInt(collen_2[2]) > Integer.parseInt(collen_1[2]) ? collen_2[2] : collen_1[2];
								sb_html_c.append("<tr><td>" + str_alltabs[i] + "</td><td><font color=\"blue\">" + collen_1[0] + "</font></td><td>" + collen_1[1] + "</td><td>" + collen_1[2] + "</td><td>" + collen_2[1] + "</td><td>" + collen_2[2] + "</td><td>alter table " + str_alltabs[i]
										+ " modify column " + collen_1[0] + " " + collen_1[1] + "(" + tt + ");" + "</td></tr>"); //
							}
						}
					}
				}

				/*		for (int j = 0; j < str_allcolumns1.length; j++) { //����������
							if (!v_columns2.containsKey(str_allcolumns1[j])) { //���Դ2�иñ���������,�����
								if (!bo_iffind) {
									li_count++;
									if (li_count != 1) {
										sb_html_c.append("<tr><td colspan=5>&nbsp;<br></td></tr>");
									}
									sb_html_c.append("<tr><td colspan=5 bgcolor=\"cyan\">[" + li_count_newcol+ "]����Դ[" + _dsname_1 + "]������Դ[" + _dsname_2 + "]�ڱ�[" + str_alltabs[i] + "]�������</td></tr>"); //
									bo_iffind = true;
								}
								String[] str_colinfo = (String[]) v_columns1.get(str_allcolumns1[j]); //ȡ������Ϣ
								sb_html_c.append("<tr><td>" + str_alltabs[i] + "</td><td><font color=\"blue\">" + str_colinfo[0] + "</font></td><td>" + str_colinfo[1] + "</td><td>" + str_colinfo[2] + "</td><td>alter table " + str_alltabs[i] + " add " + str_colinfo[0] + " " + str_colinfo[1]+"("+str_colinfo[2]+")" + ";</td></tr>"); //
							}else if(v_columns2.containsKey(str_allcolumns1[j])){//�����������Ҫ�Ƚ��г����Ƿ����
								String[] collen_1 = (String[]) v_columns1.get(str_allcolumns1[j]); //ȡ������Ϣ
								String[] collen_2 = (String[]) v_columns2.get(str_allcolumns1[j]); //ȡ������Ϣ
								if((!collen_1[1].trim().equals(collen_2[1].trim()))||(!collen_1[2].trim().equals(collen_2[2].trim()))){//������Ͳ�һ��
									sb_html_c.append("<tr><td>" + str_alltabs[i] + "</td><td><font color=\"blue\">" + collen_1[0] + "</font></td><td>" + collen_1[1] + "</td><td>" + collen_1[2] + "</td><td>alter table " + str_alltabs[i] + " modify column " + collen_1[0] + " " + collen_1[1]+"("+collen_1[2]+");"+collen_2[1]+":"+collen_2[2] + "</td></tr>"); //
								}
							}
						}*/
			} else { //������ѭ�����������
				li_count++;
				li_count_newtab++; //
				if (li_count != 1) {
					sb_html_t.append("<tr><td colspan=5>&nbsp;<br></td></tr>");
				}
				sb_html_t.append("<tr><td colspan=5 bgcolor=\"cyan\">[" + li_count_newtab + "]����Դ[" + _dsname_1 + "]������Դ[" + _dsname_2 + "]����ı�[" + str_alltabs[i] + "]</td></tr>"); //
				VectorMap v_columns = (VectorMap) map1.get(str_alltabs[i]);
				String[] str_allcolumns = v_columns.getKeysAsString(); //
				for (int j = 0; j < str_allcolumns.length; j++) { //����������
					String[] str_colinfo = (String[]) v_columns.get(str_allcolumns[j]); //ȡ������Ϣ
					sb_html_t.append("<tr><td><font color=\"blue\">" + str_alltabs[i] + "</font></td><td><font color=\"blue\">" + str_colinfo[0] + "</font></td><td>" + str_colinfo[1] + "</td><td>" + str_colinfo[2] + "</td>"
							+ (j == 0 ? "<td rowspan=" + str_allcolumns.length + " valign=\"top\">" + getCreateTableSQL(str_alltabs[i], v_columns) + "</td>" : "") + "</tr>"); //
				}
			}
		}
		sb_html_t.append("</table>"); //
		String str_return = "<font size=6 color=\"red\">����������Դ[" + _dsname_1 + "]����������Դ[" + _dsname_2 + "]�е����:������[" + li_count_newtab + "]��,��ͬ�еı���[" + li_count_newcol + "]��</font><br>" + sb_html_t.toString(); //����������������
		str_return += sb_html_c.toString();//������в�ͬ��
		return str_return; //
	}

	/**
	 * �������ݿ����ͺ�����Դ��� list��VO���飬�������б��һЩ��Ϣ,���������Ͱ�  Ԭ����  20130425 ���
	 * @param databaseType
	 * @param _ds
	 * @return
	 */
	private ArrayList getTabDesc(String databaseType, String _ds) throws Exception {
		ArrayList al_tnames = new ArrayList(); //��������..
		if (null != databaseType && databaseType.equals("Mysql")) {
			String str_schema = ServerEnvironment.getInstance().getDataSourceVO(_ds).getUser(); //�������Դ1���û�
			WLTDBConnection conn = SessionFactory.getInstance().getConnection(Thread.currentThread(), _ds); //ȡ�õ�ǰ�̵߳�����!! getConn(_dsname_1); ////
			DatabaseMetaData dbmd = conn.getMetaData(); //ȡ���������ݿ��ԭ���ݶ���!
			ResultSet resultSet = null; //�����!!
			resultSet = dbmd.getTables(null, (str_schema == null ? null : str_schema.toUpperCase()), "%", new String[] { "TABLE" });//��ʱ�ȿ��Ǳ���������ͼ����ΪĿǰ����ͼ�򱨴���ͼ�Ļ��ֶ��Ƚ�
			while (resultSet.next()) {
				HashVO hvo = new HashVO(); //
				String str_tableName = resultSet.getString("TABLE_NAME"); //����
				if (str_tableName.toUpperCase().startsWith("BIN$")) {//oracle��ɾ���ı���ڻ���վ��������"BIN$"��ͷ������Ҫ��������ջ���վ��ִ��һ��
					continue;
				}
				String str_tabType = resultSet.getString("TABLE_TYPE"); //
				String str_tabRemark = resultSet.getString("REMARKS"); //
				hvo.setAttributeValue("tabname", str_tableName); //
				hvo.setAttributeValue("tabtype", str_tabType); //
				hvo.setAttributeValue("tabdescr", str_tabRemark); //
				al_tnames.add(hvo); //
			}
		}
		return al_tnames;
	}

	/**
	 *  ��ñ�ṹ��Ϣ����Ҫ��������������Ϣ  Ԭ����  20130425 ���
	 * @param databaseType
	 * @param _ds
	 * @return
	 * @throws Exception
	 */
	private VectorMap getMap(String databaseType, String _ds, String _prefix) throws Exception {
		VectorMap map = new VectorMap(); //
		if (null != databaseType && databaseType.equals("Mysql")) {
			ArrayList list1 = getTabDesc("Mysql", _ds); //�Ȼ������Դ1�ı���Ϣ������Ա���б���
			for (int i = 0; i < list1.size(); i++) {
				HashVO hvo = (HashVO) list1.get(i); //
				String str_tabcode = hvo.getStringValue("tabname").toLowerCase(); //����
				TableDataStruct tdst = commDMO.getTableDataStructByDS(_ds, "select * from " + str_tabcode + " where 1=2"); //��ṹ!!
				String[] str_names = tdst.getHeaderName(); ////�����������
				String[] str_type = tdst.getHeaderTypeName(); //�������������
				int[] li_length = tdst.getPrecision(); //�п������
				//���沿����ȡ��һ���������
				WLTDBConnection conn = SessionFactory.getInstance().getConnection(Thread.currentThread(), _ds); //ȡ�õ�ǰ�̵߳�����!! �ں���һ�ű���ִ�У�һ��Ҫ��Դ1��ִ�в���
				DatabaseMetaData dbmd = conn.getMetaData();
				ResultSet rs = dbmd.getPrimaryKeys(null, null, str_tabcode);
				String pkName = "";
				while (rs.next()) {
					pkName = rs.getString("column_name");//��ȡ�������������ݵ����ã�����ɿ�Ϊ���������¿�����ȻΪ����
				}
				for (int j = 0; j < str_names.length; j++) {
					String str_colname = str_names[j];//����
					String str_coltype = str_type[j];//������
					String str_coldesc = String.valueOf(li_length[j]);//�п��
					if (_prefix.trim().equals("")) {//���ǰ׺Ϊ��  �����еı����
						if (map.containsKey(str_tabcode)) {
							VectorMap v_columns = (VectorMap) map.get(str_tabcode); //
							v_columns.put(str_colname.toLowerCase(), new String[] { str_colname.toLowerCase(), str_coltype, str_coldesc, pkName }); //
						} else {
							VectorMap v_columns = new VectorMap(); //
							v_columns.put(str_colname.toLowerCase(), new String[] { str_colname.toLowerCase(), str_coltype, str_coldesc, pkName }); //
							map.put(str_tabcode, v_columns); //
						}
					} else {//���ǰ׺��Ϊ�գ�����й���
						if (str_tabcode.equals("")) {
						}
						if (map.containsKey(str_tabcode)) { //�����
							VectorMap v_columns = (VectorMap) map.get(str_tabcode); //
							v_columns.put(str_colname.toLowerCase(), new String[] { str_colname.toLowerCase(), str_coltype, str_coldesc, pkName }); //
						} else {//��ӱ�
							VectorMap v_columns = new VectorMap(); //
							v_columns.put(str_colname.toLowerCase(), new String[] { str_colname.toLowerCase(), str_coltype, str_coldesc, pkName }); //
							if (str_tabcode.indexOf(_prefix) > -1) { //�����жϣ����ǰ׺Ϊprefix����ŵ�map��
								map.put(str_tabcode, v_columns); //
							}
						}
					}

				}
			}
		}
		return map;
	}

	/**������mysql��   Ԭ����  20130425 ���
	 * �Ƚ��в�һ����
	 * @return
	 * @throws Excpetion
	 */
	private String getMysTabOrColumnHtml(String _dsname_1, String _dsname_2, String _pre) throws Exception {
		//Դ1�е�����..
		VectorMap map1 = getMap("Mysql", _dsname_1, _pre); //ֻ��������й���  ȥ��������ͷΪpre�ı�
		VectorMap map2 = getMap("Mysql", _dsname_2, _pre); //ֻ��������й���  ȥ��������ͷΪpre�ı�

		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<table width=\"75%\"   border=\"0\"   cellspacing=\"0\"   cellpadding=\"3\">\r\n"); //
		sb_html.append("<tr><td align=\"center\">����</td><td align=\"center\">����</td><td align=\"center\">����Դ[" + _dsname_1 + "]������</td><td align=\"center\">����Դ[" + _dsname_2 + "]������</td></tr>\r\n"); //

		String[] str_alltabs = map1.getKeysAsString(); //�ӱ���Դ1�����б�ʼ!!
		String[] str_alltabs11 = map2.getKeysAsString(); //�ӱ���Դ1�����б�ʼ!!
		int li_count = 0;
		for (int i = 0; i < str_alltabs.length; i++) {
			if (map2.containsKey(str_alltabs[i])) { //���Դ2���иñ�,
				VectorMap v_columns1 = (VectorMap) map1.get(str_alltabs[i]);
				VectorMap v_columns2 = (VectorMap) map2.get(str_alltabs[i]); //ȡ��Դ2�еĵĸ�����������
				String[] str_allcolumns1 = v_columns1.getKeysAsString(); //
				boolean bo_iffind = false; //
				for (int j = 0; j < str_allcolumns1.length; j++) { //����������
					if (v_columns2.containsKey(str_allcolumns1[j])) { //���Դ2Ҳ��������
						String[] str_colinfo1 = (String[]) v_columns1.get(str_allcolumns1[j]); //ȡ������Ϣ
						String[] str_colinfo2 = (String[]) v_columns2.get(str_allcolumns1[j]); //ȡ������Ϣ
						if (!str_colinfo1[1].equals(str_colinfo2[1])) {
							if (!bo_iffind) {
								if (!bo_iffind) {
									li_count++;
									if (li_count != 1) {
										sb_html.append("<tr><td colspan=4>&nbsp;<br><br></td></tr>");
									}
									sb_html.append("<tr><td colspan=4 bgcolor=\"cyan\">[" + li_count + "]������Դ�ڱ�[" + str_alltabs[i] + "]�������е��������Ͳ�һ��</td></tr>\r\n"); //
									bo_iffind = true;
								}
							}
							sb_html.append("<tr><td><font color=\"blue\">" + str_alltabs[i] + "</font></td><td><font color=\"blue\">" + str_allcolumns1[j] + "</font></td><td>" + str_colinfo1[1] + "</td><td>" + str_colinfo2[1] + "</td></tr>\r\n"); //
						}
					}
				}
			}
		}
		sb_html.append("</table>\r\n"); //
		return sb_html.toString(); //
	}

	/**������Oracle��
	 * �Ƚ�ֻ����1,������2�е�����
	 * @param _dsname_1
	 * @param _dsname_2
	 * @return
	 * @throws Exception
	 */
	private String getOraTabOrColumnHtml(String _dsname_1, String _dsname_2) throws Exception {
		//Դ1�е�����..
		VectorMap map1 = new VectorMap(); //
		HashVO[] hvs_ds1 = commDMO.getHashVoArrayByDS(_dsname_1, "select * from v_pub_syscolumns order by tabname,colcode");
		for (int i = 0; i < hvs_ds1.length; i++) {
			String str_tabcode = hvs_ds1[i].getStringValue("tabname").toLowerCase(); //����
			String str_colname = hvs_ds1[i].getStringValue("colname").toLowerCase(); //����
			String str_coltype = hvs_ds1[i].getStringValue("datatype").toLowerCase(); //����
			String str_coldesc = hvs_ds1[i].getStringValue("coldesc").toLowerCase(); //��˵��
			if (map1.containsKey(str_tabcode)) {
				VectorMap v_columns = (VectorMap) map1.get(str_tabcode); //
				v_columns.put(str_colname, new String[] { str_colname, str_coltype, str_coldesc }); //
			} else {
				VectorMap v_columns = new VectorMap(); //
				v_columns.put(str_colname, new String[] { str_colname, str_coltype, str_coldesc }); //
				map1.put(str_tabcode, v_columns); //
			}
		}

		//Դ2�е�����
		VectorMap map2 = new VectorMap(); //
		HashVO[] hvs_ds2 = commDMO.getHashVoArrayByDS(_dsname_2, "select * from v_pub_syscolumns order by tabname,colcode");
		for (int i = 0; i < hvs_ds2.length; i++) {
			String str_tabcode = hvs_ds2[i].getStringValue("tabname").toLowerCase(); //����
			String str_colname = hvs_ds2[i].getStringValue("colname").toLowerCase(); //����
			String str_coltype = hvs_ds2[i].getStringValue("datatype".toLowerCase()); //����
			String str_coldesc = hvs_ds2[i].getStringValue("coldesc").toLowerCase(); //��˵��
			if (map2.containsKey(str_tabcode)) {
				VectorMap v_columns = (VectorMap) map2.get(str_tabcode); //
				v_columns.put(str_colname, new String[] { str_colname, str_coltype, str_coldesc }); //
			} else {
				VectorMap v_columns = new VectorMap(); //
				v_columns.put(str_colname, new String[] { str_colname, str_coltype, str_coldesc }); //
				map2.put(str_tabcode, v_columns); //
			}
		}

		//�ҳ�ֻ������1��������2�е�����..
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<table width=\"75%\"   border=\"0\"   cellspacing=\"0\"   cellpadding=\"3\">\r\n"); //
		sb_html.append("<tr><td align=\"center\">����</td><td align=\"center\">����</td><td align=\"center\">������</td><td>SQL���</td></tr>\r\n"); //
		String[] str_alltabs = map1.getKeysAsString(); //�ӱ���Դ1�����б�ʼ!!
		int li_count = 0; //
		int li_count_newtab = 0; //
		for (int i = 0; i < str_alltabs.length; i++) {
			if (map2.containsKey(str_alltabs[i])) { //���Դ2���иñ�,��ֻ�Ƚ���..
				VectorMap v_columns1 = (VectorMap) map1.get(str_alltabs[i]);
				VectorMap v_columns2 = (VectorMap) map2.get(str_alltabs[i]); //ȡ��Դ2�еĵĸ�����������
				String[] str_allcolumns1 = v_columns1.getKeysAsString(); //
				boolean bo_iffind = false; //
				for (int j = 0; j < str_allcolumns1.length; j++) { //����������
					if (!v_columns2.containsKey(str_allcolumns1[j])) { //���Դ2�иñ���������,�����
						if (!bo_iffind) {
							li_count++;
							if (li_count != 1) {
								sb_html.append("<tr><td colspan=4>&nbsp;<br><br></td></tr>");
							}
							sb_html.append("<tr><td colspan=4 bgcolor=\"cyan\">[" + li_count + "]����Դ[" + _dsname_1 + "]������Դ[" + _dsname_2 + "]�ڱ�[" + str_alltabs[i] + "]�������</td></tr>\r\n"); //
							bo_iffind = true;
						}
						String[] str_colinfo = (String[]) v_columns1.get(str_allcolumns1[j]); //ȡ������Ϣ
						sb_html.append("<tr><td>" + str_alltabs[i] + "</td><td><font color=\"blue\">" + str_colinfo[0] + "</font></td><td>" + str_colinfo[1] + "</td><td>alter table " + str_alltabs[i] + " add " + str_colinfo[0] + " " + str_colinfo[1] + "(" + str_colinfo[2] + ")" + ";</td></tr>\r\n"); //
					}
				}
			} else { //������ѭ�����������
				li_count++;
				if (li_count != 1) {
					sb_html.append("<tr><td colspan=4>&nbsp;<br><br></td></tr>");
				}
				sb_html.append("<tr><td colspan=4 bgcolor=\"cyan\">[" + li_count + "]����Դ[" + _dsname_1 + "]������Դ[" + _dsname_2 + "]����ı�[" + str_alltabs[i] + "]</td></tr>\r\n"); //

				li_count_newtab++; //
				VectorMap v_columns = (VectorMap) map1.get(str_alltabs[i]);
				String[] str_allcolumns = v_columns.getKeysAsString(); //
				for (int j = 0; j < str_allcolumns.length; j++) { //����������
					String[] str_colinfo = (String[]) v_columns.get(str_allcolumns[j]); //ȡ������Ϣ
					sb_html.append("<tr><td><font color=\"blue\">" + str_alltabs[i] + "</font></td><td><font color=\"blue\">" + str_colinfo[0] + "</font></td><td>" + str_colinfo[1] + "</td>"
							+ (j == 0 ? "<td rowspan=" + str_allcolumns.length + " valign=\"top\">" + getCreateTableSQL(str_alltabs[i], v_columns) + "</td>" : "") + "</tr>\r\n"); //
				}
			}
		}
		sb_html.append("</table>\r\n"); //
		String str_return = "<font size=2 color=\"red\">����������Դ[" + _dsname_1 + "]����������Դ[" + _dsname_2 + "]�е����:������[" + li_count_newtab + "]��</font><br>" + sb_html.toString(); //
		return str_return; //
	}

	private String getCreateTableSQL(String _tablename, VectorMap _allcolumns) {
		StringBuffer sb_sql = new StringBuffer(); //
		if (_tablename.startsWith("v_")) {
			sb_sql.append("<font color=\"red\">��������ͼ,������ִ��SQL!</font><br>"); //
		}

		sb_sql.append("create table " + _tablename + "<br>\r\n"); //
		sb_sql.append("(<br>\r\n");
		String[] all_keys = _allcolumns.getKeysAsString(); //
		for (int i = 0; i < all_keys.length - 1; i++) {
			String[] str_colinfo = (String[]) _allcolumns.get(all_keys[i]); //
			sb_sql.append(str_colinfo[0] + " " + str_colinfo[1] + "(" + str_colinfo[2] + ")" + ",<br>\r\n"); //����ֶγ���   Ԭ����  20130426����
		}
		String[] str_colinfo = (String[]) _allcolumns.get(all_keys[all_keys.length - 1]); //
		sb_sql.append(str_colinfo[0] + " " + str_colinfo[1] + "(" + str_colinfo[2] + ")" + "<br>\r\n");
		if (null != str_colinfo[3] && !str_colinfo[3].trim().equals("")) {
			sb_sql.append(",constraint pk_" + _tablename + " primary key (" + str_colinfo[3] + ")<br>\r\n"); //��ע�͵�����Ҫ����Ϊ����������������ݻ����ظ�
		}
		sb_sql.append(")DEFAULT CHARSET=gb2312;<br><br>\r\n");
		return sb_sql.toString();
	}

	/**������Oracle��
	 * �Ƚ��в�һ����
	 * @return
	 * @throws Excpetion
	 */
	private String getOraCompareHtml(String _dsname_1, String _dsname_2) throws Exception {
		//Դ1�е�����..
		VectorMap map1 = new VectorMap(); //
		HashVO[] hvs_ds1 = commDMO.getHashVoArrayByDS(_dsname_1, "select * from v_pub_syscolumns order by tabname,colcode");
		for (int i = 0; i < hvs_ds1.length; i++) {
			String str_tabcode = hvs_ds1[i].getStringValue("tabname").toLowerCase(); //����
			String str_colname = hvs_ds1[i].getStringValue("colname").toLowerCase(); //����
			String str_coltype = hvs_ds1[i].getStringValue("datatype").toLowerCase(); //����
			String str_coldesc = hvs_ds1[i].getStringValue("coldesc").toLowerCase(); //��˵��
			if (map1.containsKey(str_tabcode)) {
				VectorMap v_columns = (VectorMap) map1.get(str_tabcode); //
				v_columns.put(str_colname, new String[] { str_colname, str_coltype, str_coldesc }); //
			} else {
				VectorMap v_columns = new VectorMap(); //
				v_columns.put(str_colname, new String[] { str_colname, str_coltype, str_coldesc }); //
				map1.put(str_tabcode, v_columns); //
			}
		}

		//Դ2�е�����
		VectorMap map2 = new VectorMap(); //
		HashVO[] hvs_ds2 = commDMO.getHashVoArrayByDS(_dsname_2, "select * from v_pub_syscolumns order by tabname,colcode");
		for (int i = 0; i < hvs_ds2.length; i++) {
			String str_tabcode = hvs_ds2[i].getStringValue("tabname").toLowerCase(); //����
			String str_colname = hvs_ds2[i].getStringValue("colname").toLowerCase(); //����
			String str_coltype = hvs_ds2[i].getStringValue("datatype").toLowerCase(); //����
			String str_coldesc = hvs_ds2[i].getStringValue("coldesc").toLowerCase(); //��˵��
			if (map2.containsKey(str_tabcode)) {
				VectorMap v_columns = (VectorMap) map2.get(str_tabcode); //
				v_columns.put(str_colname, new String[] { str_colname, str_coltype, str_coldesc }); //
			} else {
				VectorMap v_columns = new VectorMap(); //
				v_columns.put(str_colname, new String[] { str_colname, str_coltype, str_coldesc }); //
				map2.put(str_tabcode, v_columns); //
			}
		}

		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<table width=\"75%\"   border=\"0\"   cellspacing=\"0\"   cellpadding=\"3\">\r\n"); //
		sb_html.append("<tr><td align=\"center\">����</td><td align=\"center\">����</td><td align=\"center\">����Դ[" + _dsname_1 + "]������</td><td align=\"center\">����Դ[" + _dsname_2 + "]������</td></tr>\r\n"); //

		String[] str_alltabs = map1.getKeysAsString(); //�ӱ���Դ1�����б�ʼ!!
		int li_count = 0;
		for (int i = 0; i < str_alltabs.length; i++) {
			if (map2.containsKey(str_alltabs[i])) { //���Դ2���иñ�,
				VectorMap v_columns1 = (VectorMap) map1.get(str_alltabs[i]);
				VectorMap v_columns2 = (VectorMap) map2.get(str_alltabs[i]); //ȡ��Դ2�еĵĸ�����������
				String[] str_allcolumns1 = v_columns1.getKeysAsString(); //
				boolean bo_iffind = false; //
				for (int j = 0; j < str_allcolumns1.length; j++) { //����������
					if (v_columns2.containsKey(str_allcolumns1[j])) { //���Դ2Ҳ��������
						String[] str_colinfo1 = (String[]) v_columns1.get(str_allcolumns1[j]); //ȡ������Ϣ
						String[] str_colinfo2 = (String[]) v_columns2.get(str_allcolumns1[j]); //ȡ������Ϣ
						if (!str_colinfo1[1].equals(str_colinfo2[1])) {
							if (!bo_iffind) {
								if (!bo_iffind) {
									li_count++;
									if (li_count != 1) {
										sb_html.append("<tr><td colspan=4>&nbsp;<br><br></td></tr>");
									}
									sb_html.append("<tr><td colspan=4 bgcolor=\"cyan\">[" + li_count + "]������Դ�ڱ�[" + str_alltabs[i] + "]�������е��������Ͳ�һ��</td></tr>\r\n"); //
									bo_iffind = true;
								}
							}
							sb_html.append("<tr><td><font color=\"blue\">" + str_alltabs[i] + "</font></td><td><font color=\"blue\">" + str_allcolumns1[j] + "</font></td><td>" + str_colinfo1[1] + "</td><td>" + str_colinfo2[1] + "</td></tr>\r\n"); //
						}
					}
				}

			}
		}

		sb_html.append("</table>\r\n"); //
		return sb_html.toString(); //
	}

}
