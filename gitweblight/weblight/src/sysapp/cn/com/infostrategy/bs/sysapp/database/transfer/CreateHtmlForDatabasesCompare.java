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
 * 该类主要完成多表迁移功能，包括导出某些表的数据，包括表结构和数据
 * 在导入数据时，如果已经有这些表，则检查字段，如果没有则添加上，再导入数据。如果没有这些表，则新建表再导入数据，都会做表结构验证
 * @author 袁江晓
 *
 */
public class CreateHtmlForDatabasesCompare implements WebCallBeanIfc {

	private CommDMO commDMO = null;

	@SuppressWarnings("unchecked")
	public String getHtmlContent(HashMap map) throws Exception {
		//取得两个表的数据
		String type = (String) map.get("type"); //
		String dataSource1 = map.get("datasource1").toString();
		String dataSource2 = map.get("datasource2").toString();
		String databaseType = map.get("databaseType").toString(); //添加区分数据库类型    袁江晓  201304     暂不考虑一种是oracle，一种是mysql的情况
		String tabenamePrefix = map.get("tabenamePrefix").toString(); //添加表名前缀过滤，否则不同表太多无法过滤    袁江晓  201304 

		commDMO = new CommDMO();

		StringBuffer sb_html = new StringBuffer(); //
		StringBuffer sb_blank = new StringBuffer(); //  只输出sql脚本
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
		} else if (null != databaseType && databaseType.equals("Mysql")) { //如果是Mysql   //因为这次是mysql数据库，所以先完成mysql的导入导出
			if (type.equals("1")) {//表结构比较
				sb_blank.append(getMysCompareHtml(dataSource1, dataSource2, tabenamePrefix)); //
				return "所执行的sql脚本如下：<br>\r\n" + sb_blank.toString();
			} else if (type.equals("2")) {//导入新增表数据
				sb_blank.append(importInsertTabData(dataSource1, dataSource2, tabenamePrefix)); //
				return "新增表的sql脚本如下：<br>\r\n" + sb_blank.toString();
			} else if (type.equals("3")) {//导入已有表数据
				sb_html.append(importExistTabData(dataSource1, dataSource2, tabenamePrefix)); //
				return "修改表的sql脚本如下：<br>\r\n" + sb_html.toString();
			} else if (type.equals("4")) { //导入所有数据
				sb_blank.append(importDataFrom1To2(dataSource1, dataSource2, tabenamePrefix)); //
				return "所执行的sql脚本如下：<br>\r\n" + sb_blank.toString();
			}
		}

		sb_html.append("</body>\r\n");
		sb_html.append("</html>\r\n");
		return sb_html.toString(); //
	}

	/**
	 * 导入新增表的数据
	 * @param dataSource2
	 * @param dataSource1
	 * @param tabenamePrefix
	 * @return
	 */
	private String importInsertTabData(String _dsname_1, String _dsname_2, String _prefix) throws Exception {
		VectorMap map1 = getMap("Mysql", _dsname_1, _prefix); //先获得数据源1的按照给定过滤条件的表结构
		VectorMap map2 = getMap("Mysql", _dsname_2, _prefix);//先获得数据源1的按照给定过滤条件的表结构
		//只获取不同的sql语句，然后进行批处理
		//1、先执行表结构的不同
		StringBuffer sb = new StringBuffer();
		String[] str_alltabs = map1.getKeysAsString(); //从遍历源1中所有表开始!!
		//需要记录执行过sql脚本的表，否则执行完脚本后表一致无法记忆需要导入数据的表
		HashSet vec_table = new HashSet();
		for (int i = 0; i < str_alltabs.length; i++) {
			if (map2.containsKey(str_alltabs[i])) {//如果有该表则跳过，只比较新增的表
			} else { //否则死循环输出所有列
				vec_table.add(str_alltabs[i]);//将有变化的表记录下来
				VectorMap v_columns = (VectorMap) map1.get(str_alltabs[i]);
				String[] str_allcolumns = v_columns.getKeysAsString(); //
				for (int j = 0; j < str_allcolumns.length; j++) { //遍历所有列
					String[] str_colinfo = (String[]) v_columns.get(str_allcolumns[j]); //取得列信息
					sb.append(j == 0 ? getCreateTableSQL(str_alltabs[i], v_columns) : ""); //
				}
			}
		}
		CommDMO dmo = new CommDMO();
		String str_1 = new TBUtil().replaceAll(sb.toString(), "<br>", "");//去掉br样式的字符
		String str_temp = new TBUtil().replaceAll(str_1, "\r\n", "");//去掉br样式的字符
		String[] sql_str = new TBUtil().split(str_temp.toString(), ";");
		WLTDBConnection conn = SessionFactory.getInstance().getConnection(Thread.currentThread(), _dsname_2); //取得当前线程的连接!! 在后面一张表中执行
		Statement stmt = conn.createStatement();
		String[] strs = new String[60];
		for (int kk = 0; kk < 60; kk++) {
			strs[kk] = sql_str[kk];
		}
		dmo.executeBatchByDSImmediately(_dsname_2, strs);

		//stmt.executeBatch();
		//2、将1中的数据导入到2中
		WLTDBConnection conn1 = SessionFactory.getInstance().getConnection(Thread.currentThread(), _dsname_2); //取得当前线程的连接!! 在后面一张表中执行
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
	 * 导入已有表的数据，主要是在之前的基础上把没有的字段搞过来，然后导入数据
	 * @param dataSource1
	 * @param dataSource2
	 * @param tabenamePrefix
	 * @return
	 */

	private String importExistTabData(String _dsname_1, String _dsname_2, String _prefix) throws Exception {
		VectorMap map1 = getMap("Mysql", _dsname_1, _prefix); //先获得数据源1的按照给定过滤条件的表结构
		VectorMap map2 = getMap("Mysql", _dsname_2, _prefix);//先获得数据源1的按照给定过滤条件的表结构
		StringBuffer sb = new StringBuffer();
		String[] str_alltabs = map1.getKeysAsString(); //从遍历源1中所有表开始!!
		ArrayList l_existtab = new ArrayList();
		HashSet vec_table = new HashSet();
		//先修改表结构
		for (int i = 0; i < str_alltabs.length; i++) {
			if (map2.containsKey(str_alltabs[i])) { //如果源2中有该表,则只比较列..
				l_existtab.add(str_alltabs[i]);
				VectorMap v_columns1 = (VectorMap) map1.get(str_alltabs[i]);
				VectorMap v_columns2 = (VectorMap) map2.get(str_alltabs[i]); //取得源2中的的该所有所有列
				String[] str_allcolumns1 = v_columns1.getKeysAsString(); //
				for (int j = 0; j < str_allcolumns1.length; j++) { //遍历所有列
					if (!v_columns2.containsKey(str_allcolumns1[j])) { //如果源2中该表不包含该列,则输出
						vec_table.add(str_alltabs[i]);//将有变化的表记录下来
						String[] str_colinfo = (String[]) v_columns1.get(str_allcolumns1[j]); //取得列信息
						sb.append("alter table " + str_alltabs[i] + " add " + str_colinfo[0] + " " + str_colinfo[1] + "(" + str_colinfo[2] + ")" + ";" + "<br>"); //
					} else if (v_columns2.containsKey(str_allcolumns1[j]))//如果相同需要比较字段长度是否相等
					{
						String[] collen_1 = (String[]) v_columns1.get(str_allcolumns1[j]); //取得列信息
						String[] collen_2 = (String[]) v_columns2.get(str_allcolumns1[j]); //取得列信息
						String tt = Integer.parseInt(collen_2[2]) > Integer.parseInt(collen_1[2]) ? collen_2[2] : collen_1[2];//设置为旧表和新表的最大值
						if (collen_1[1].trim().equals(collen_2[1].trim()) && (!collen_1[2].trim().equals(collen_2[2].trim()))) {
							sb.append("alter table " + str_alltabs[i] + " modify column " + collen_2[0] + " " + collen_2[1] + "(" + tt + ")" + ";" + "<br>"); //
						} else if (!collen_1[1].trim().equals(collen_2[1].trim())) {//如果二者类型不相同，则不变化，保持新的
							vec_table.add(str_alltabs[i]);//将有变化的表记录下来
							sb.append("alter table " + str_alltabs[i] + " modify column " + collen_2[0] + " " + collen_2[1] + "(" + collen_2[2] + ")" + ";" + "<br>"); //
						}
					}
				}
			}
		}
		WLTDBConnection conn = SessionFactory.getInstance().getConnection(Thread.currentThread(), _dsname_2); //取得当前线程的连接!! 在后面一张表中执行
		Statement stmt = conn.createStatement();
		String str_temp = new TBUtil().replaceAll(sb.toString(), "<br>", "");//去掉br样式的字符
		String[] sql_str = new TBUtil().split(str_temp.toString(), ";");
		for (int ii = 0; ii < sql_str.length; ii++) {
			stmt.addBatch(sql_str[ii]);
		}
		stmt.executeBatch();

		//		1中的数据导入到2中
		WLTDBConnection conn1 = SessionFactory.getInstance().getConnection(Thread.currentThread(), _dsname_2); //取得当前线程的连接!! 在后面一张表中执行
		Statement stmt1 = conn.createStatement();
		DataSourceVO[] dsvs = ServerEnvironment.getInstance().getDataSourceVOs();
		String DBNewUser = new TBUtil().getDBUserFromDSName(dsvs, _dsname_2, "mysql");
		String DBOldUser = new TBUtil().getDBUserFromDSName(dsvs, _dsname_1, "mysql");

		for (int i2 = 0; i2 < l_existtab.size(); i2++) {
			StringBuffer sql_insert = new StringBuffer("insert into " + DBNewUser + "." + l_existtab.get(i2));
			VectorMap v_columns1 = (VectorMap) map1.get(l_existtab.get(i2)); //先取map1的字段，因为已经将Map1中的字段复制进map2中了
			StringBuffer tempsb = new StringBuffer();
			String[] str_allcolumns1 = v_columns1.getKeysAsString(); //取得所有字段进行拼接sql
			for (int i3 = 0; i3 < str_allcolumns1.length - 1; i3++) { //遍历所有列  取得最后一个字段前的所有字段
				String[] str_colinfo = (String[]) v_columns1.get(str_allcolumns1[i3]); //取得列信息
				tempsb.append(str_colinfo[0] + ","); //
			}
			tempsb.append(str_allcolumns1[str_allcolumns1.length - 1]);//最后一个不需要逗号
			String sql = sql_insert.append("(").append(tempsb.toString()).append(")").append(" select ").append(tempsb.toString()).append(" from ").append(DBOldUser).append(".").append(l_existtab.get(i2)).toString();
			//StringBuffer sql_select=new  StringBuffer(* from "+DBOldUser+"."+str_alltabs[i2]);	

			//首先需要删除旧数据
			String deldata_sql = "truncate table " + DBNewUser + "." + l_existtab.get(i2) + ";";

			stmt1.addBatch(deldata_sql);
			stmt1.addBatch(sql);
		}

		stmt1.executeBatch();

		return sb.toString();
	}

	/**1、如果源1有源2没有的表则先在源2中新增表再导入数据
	 * 2、如果源1中有源2中也有则只添加源2中没有的字段，修改长度跟源1中一致，不删除源2中的字段，导入数据前需要删除源2中的数据
	 * 3、最后将对数据源2进行的sql语句操作返回
	 * 袁江晓  添加导入表数据功能包括表结构
	 * @param _dsname_1
	 * @param _dsname_2
	 * @param _prefix
	 * @return
	 */
	private String importDataFrom1To2(String _dsname_1, String _dsname_2, String _prefix) throws Exception {
		VectorMap map1 = getMap("Mysql", _dsname_1, _prefix); //先获得数据源1的按照给定过滤条件的表结构
		VectorMap map2 = getMap("Mysql", _dsname_2, _prefix);//先获得数据源1的按照给定过滤条件的表结构
		//只获取不同的sql语句，然后进行批处理
		//1、先执行表结构的不同
		StringBuffer sb = new StringBuffer();
		String[] str_alltabs = map1.getKeysAsString(); //从遍历源1中所有表开始!!
		//需要记录执行过sql脚本的表，否则执行完脚本后表一致无法记忆需要导入数据的表
		HashSet vec_table = new HashSet();
		for (int i = 0; i < str_alltabs.length; i++) {
			if (map2.containsKey(str_alltabs[i])) { //如果源2中有该表,则只比较列..
				VectorMap v_columns1 = (VectorMap) map1.get(str_alltabs[i]);
				VectorMap v_columns2 = (VectorMap) map2.get(str_alltabs[i]); //取得源2中的的该所有所有列
				String[] str_allcolumns1 = v_columns1.getKeysAsString(); //
				for (int j = 0; j < str_allcolumns1.length; j++) { //遍历所有列
					if (!v_columns2.containsKey(str_allcolumns1[j])) { //如果源2中该表不包含该列,则输出
						vec_table.add(str_alltabs[i]);//将有变化的表记录下来
						String[] str_colinfo = (String[]) v_columns1.get(str_allcolumns1[j]); //取得列信息
						sb.append("alter table " + str_alltabs[i] + " add " + str_colinfo[0] + " " + str_colinfo[1] + "(" + str_colinfo[2] + ")" + ";"); //
					} else if (v_columns2.containsKey(str_allcolumns1[j]))//如果相同需要比较字段长度是否相等
					{
						String[] collen_1 = (String[]) v_columns1.get(str_allcolumns1[j]); //取得列信息
						String[] collen_2 = (String[]) v_columns2.get(str_allcolumns1[j]); //取得列信息
						if ((!collen_1[1].trim().equals(collen_2[1].trim())) || (!collen_1[2].trim().equals(collen_2[2].trim()))) {
							vec_table.add(str_alltabs[i]);//将有变化的表记录下来
							sb.append("alter table " + str_alltabs[i] + " modify column " + collen_2[0] + " " + collen_2[1] + "(" + collen_2[2] + ")" + ";"); //
						}

					}
				}
			} else { //否则死循环输出所有列
				vec_table.add(str_alltabs[i]);//将有变化的表记录下来
				VectorMap v_columns = (VectorMap) map1.get(str_alltabs[i]);
				String[] str_allcolumns = v_columns.getKeysAsString(); //
				for (int j = 0; j < str_allcolumns.length; j++) { //遍历所有列
					String[] str_colinfo = (String[]) v_columns.get(str_allcolumns[j]); //取得列信息
					sb.append(j == 0 ? getCreateTableSQL(str_alltabs[i], v_columns) : ""); //
				}
			}
		}
		String str_temp = new TBUtil().replaceAll(sb.toString(), "<br>", "");//去掉br样式的字符
		String[] sql_str = new TBUtil().split(str_temp.toString(), ";");
		WLTDBConnection conn = SessionFactory.getInstance().getConnection(Thread.currentThread(), _dsname_2); //取得当前线程的连接!! 在后面一张表中执行
		Statement stmt = conn.createStatement();
		for (int ii = 0; ii < sql_str.length; ii++) {
			stmt.addBatch(sql_str[ii]);
		}
		stmt.executeBatch(); //批处理执行sql脚本
		//2、再执行数据 需要读取源1中的表数据拼成values后面的值，读取数据源2中的表结构数据拼成insert into后面的内容
		//最终需拼成  insert into test(id,name,sex) values('1','小张','female');
		System.out.println("需要导入数据的表的个数为==============================" + vec_table.size());
		Iterator it = vec_table.iterator();
		CommDMO comm = new CommDMO();
		while (it.hasNext()) {
			String tableName = (String) it.next();
			ArrayList sql_list = new ArrayList();
			//i1、首先删除源2的表中的数据
			String deldata_sql = "truncate table " + tableName + ";";
			sql_list.add(deldata_sql);//先删除源2中相应表的数据
			comm.executeBatchByDSImmediately(_dsname_2, sql_list);//先删除表中的数据
			//i2、取源2中相应表的数据  先拼接values前面的sql
			VectorMap v_columns1 = (VectorMap) map1.get(tableName); //先取map1的字段，因为已经将Map1中的字段复制进map2中了
			String[] str_allcolumns1 = v_columns1.getKeysAsString(); //取得所有字段进行拼接sql
			StringBuffer sb_insert = new StringBuffer("set names gbk; ");
			sb_insert.append("insert into " + tableName + "(");
			for (int j = 0; j < str_allcolumns1.length - 1; j++) { //遍历所有列  取得最后一个字段前的所有字段
				String[] str_colinfo = (String[]) v_columns1.get(str_allcolumns1[j]); //取得列信息
				sb_insert.append(str_colinfo[0] + ","); //
			}
			sb_insert.append(str_allcolumns1[str_allcolumns1.length - 1]).append(") values(");//最后一个不需要逗号
			//i3、取源2中相应表的数据  再拼接values后面的sql，需要在数据源1中进行查询	
			//select col1,col2,col3、、、from t1
			StringBuffer sb_query = new StringBuffer();//查询数据源1的sql拼写
			sb_query.append("select ");
			for (int j = 0; j < str_allcolumns1.length - 1; j++) { //遍历所有列  取得最后一个字段前的所有字段
				String[] str_colinfo = (String[]) v_columns1.get(str_allcolumns1[j]); //取得列信息
				sb_query.append(str_colinfo[0] + ","); //
			}
			sb_query.append(str_allcolumns1[str_allcolumns1.length - 1]);
			sb_query.append(" from " + tableName);
			WLTDBConnection conn_1 = SessionFactory.getInstance().getConnection(Thread.currentThread(), _dsname_1); //取得当前线程的连接!! 在后面一张表中执行，一定要在源1中执行操作
			Statement stmt_1 = conn_1.createStatement();//可能重复执行，所以这里重新定义
			//String _datasourcename, String _initSql, boolean _isDebugLog, boolean _isUpperLimit, int[] _rowArea, boolean _isNeedCount, boolean _isImmediately, boolean _isPrepareSQL
			HashVO[] vos = comm.getHashVoArrayByDS(_dsname_1, sb_query.toString());
			if (null != vos && vos.length <= 5000) { //如果小于结果集5000则直接拼接
				Statement stmt1 = conn.createStatement();
				for (int k = 0; k < vos.length; k++) {
					HashVO vo = vos[k];
					String[] str_results = new String[str_allcolumns1.length];
					StringBuffer tempbuffer = new StringBuffer();
					StringBuffer tempbuffer1 = new StringBuffer();
					for (int k1 = 0; k1 < str_results.length - 1; k1++) {
						str_results[k1] = vo.getStringValue(k1);
						String[] str_colinfo = (String[]) v_columns1.get(str_allcolumns1[k1]); //取得列信息
						//这里需要进行判断数据类型 不能单纯的搞成字符型
						if (str_colinfo[1].equals("INT") || str_colinfo[1].equals("INTEGER") || str_colinfo[1].equals("DOUBLE") || str_colinfo[1].equals("FLOAT") || str_colinfo[1].equals("DECIMAL")) {
							tempbuffer1.append(str_results[k1]).append(",");
						} else {
							tempbuffer1.append(str_results[k1] == null ? "''" : "'" + str_results[k1] + "'").append(",");
						}
					}
					//取得最后一列的信息
					String[] str_colinfo1 = (String[]) v_columns1.get(str_allcolumns1[str_allcolumns1.length - 1]); //取得列信息
					if (str_colinfo1[1].equals("INT") || str_colinfo1[1].equals("INTEGER") || str_colinfo1[1].equals("DOUBLE") || str_colinfo1[1].equals("FLOAT") || str_colinfo1[1].equals("DECIMAL")) {
						tempbuffer.append(sb_insert.toString()).append(tempbuffer1.toString()).append(vo.getStringValue(str_results.length - 1)).append(");");
					} else {
						tempbuffer.append(sb_insert.toString()).append(tempbuffer1.toString()).append(vo.getStringValue(str_results.length - 1) == null ? "''" : "'" + vo.getStringValue(str_results.length - 1) + "'").append(");");
					}

					stmt1.executeUpdate(tempbuffer.toString());
					//stmt1.addBatch(tempbuffer.toString());
				}
				//stmt1.executeBatch();
			} else if (null != vos && vos.length > 5000) {//如果结果集大于5000，则需要分多次执行
				int k1 = vos.length / 5000 + 1;
				for (int k2 = 1; k2 < k1; k2++) {//先操作能整除的
					Statement stmt2 = conn.createStatement();//可能重复执行，所以这里重新定义
					for (int k3 = (k2 - 1) * 5000; k3 < k2 * 5000; k3++) {
						HashVO vo = vos[k3];
						String[] str_results = new String[str_allcolumns1.length];
						StringBuffer tempbuffer = new StringBuffer();
						StringBuffer tempbuffer1 = new StringBuffer();
						for (int k4 = 0; k4 < str_results.length - 1; k4++) {
							str_results[k4] = vo.getStringValue(k4);
							String[] str_colinfo = (String[]) v_columns1.get(str_allcolumns1[k1]); //取得列信息
							//这里需要进行判断数据类型 不能单纯的搞成字符型
							if (str_colinfo[1].equals("INT") || str_colinfo[1].equals("INTEGER") || str_colinfo[1].equals("DOUBLE") || str_colinfo[1].equals("FLOAT") || str_colinfo[1].equals("DECIMAL")) {
								tempbuffer1.append(str_results[k4]).append(",");
							} else {
								tempbuffer1.append(str_results[k4] == null ? "''" : "'" + str_results[k4] + "'").append(",");
							}
						}
						String[] str_colinfo1 = (String[]) v_columns1.get(str_allcolumns1[str_allcolumns1.length - 1]); //取得列信息
						if (str_colinfo1[1].equals("INT") || str_colinfo1[1].equals("INTEGER") || str_colinfo1[1].equals("DOUBLE") || str_colinfo1[1].equals("FLOAT") || str_colinfo1[1].equals("DECIMAL")) {
							tempbuffer.append(sb_insert.toString()).append(tempbuffer1.toString()).append(vo.getStringValue(str_results.length - 1)).append(");");
						} else {
							tempbuffer.append(sb_insert.toString()).append(tempbuffer1.toString()).append(vo.getStringValue(str_results.length - 1) == null ? "''" : "'" + vo.getStringValue(str_results.length - 1) + "'").append(");");
						}
						stmt2.addBatch(tempbuffer.toString());
					}
					stmt2.executeBatch();
				}
				//剩余的为不能整除的
				Statement stmt3 = conn.createStatement();//可能重复执行，所以这里重新定义
				for (int k5 = 5000 * (k1 - 1); k5 < vos.length; k5++) {//最后剩余的，肯定小于5000条
					HashVO vo = vos[k5];
					String[] str_results = new String[str_allcolumns1.length];
					StringBuffer tempbuffer = new StringBuffer();
					StringBuffer tempbuffer1 = new StringBuffer();
					for (int k6 = 0; k6 < str_results.length - 1; k6++) {
						str_results[k6] = vo.getStringValue(k6);
						String[] str_colinfo = (String[]) v_columns1.get(str_allcolumns1[k1]); //取得列信息
						//这里需要进行判断数据类型 不能单纯的搞成字符型
						if (str_colinfo[1].equals("INT") || str_colinfo[1].equals("INTEGER") || str_colinfo[1].equals("DOUBLE") || str_colinfo[1].equals("FLOAT") || str_colinfo[1].equals("DECIMAL")) {
							tempbuffer1.append(str_results[k6]).append(",");
						} else {
							tempbuffer1.append(str_results[k6] == null ? "''" : "'" + str_results[k6] + "'").append(",");
						}
					}
					String[] str_colinfo1 = (String[]) v_columns1.get(str_allcolumns1[str_allcolumns1.length - 1]); //取得列信息
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
	 * mysql的比较实现   袁江晓  20130425 添加
	 * 两个源中的不同数据比较
	 * @param dataSource1
	 * @param dataSource2
	 * @return
	 */
	private String getMysCompareHtml(String _dsname_1, String _dsname_2, String _prefix) throws Exception {
		//源1中的数据..
		VectorMap map1 = getMap("Mysql", _dsname_1, _prefix); //获得数据源1的
		VectorMap map2 = getMap("Mysql", _dsname_2, _prefix);

		//找出只存在于1而不存于2中的数据..
		StringBuffer sb_html_t = new StringBuffer(); //新增表的html语句   
		StringBuffer sb_html_c = new StringBuffer(); //不同列的html语句
		sb_html_t.append("<br><font size=5 color=\"red\">新增表描述如下：</font>");
		sb_html_t.append("<table width=\"75%\"   border=\"1\"   cellspacing=\"0\"   cellpadding=\"3\">"); //
		sb_html_t.append("<tr><td align=\"center\">表名</td><td align=\"center\">列名</td><td align=\"center\">列类型</td><td align=\"center\">列宽度</td><td>SQL语句</td></tr>"); //
		sb_html_c.append("<br><font size=5 color=\"red\">列不同的表描述如下：</font>");
		sb_html_c.append("<br><table width=\"75%\"   border=\"1\"   cellspacing=\"0\"   cellpadding=\"3\">"); //
		sb_html_c.append("<tr><td align=\"center\">表名</td><td align=\"center\">列名</td><td align=\"center\">旧列类型</td><td align=\"center\">旧列宽度</td><td align=\"center\">新列类型</td><td align=\"center\">新列宽度</td><td>SQL语句</td></tr>"); //
		String[] str_alltabs = map1.getKeysAsString(); //从遍历源1中所有表开始!!
		int li_count = 0; //
		int li_count_newtab = 0; //统计新增表的个数
		int li_count_newcol = 0; //统计列不同的表的个数
		for (int i = 0; i < str_alltabs.length; i++) {
			if (map2.containsKey(str_alltabs[i])) { //如果源2中有该表,则只比较列..
				VectorMap v_columns1 = (VectorMap) map1.get(str_alltabs[i]);
				VectorMap v_columns2 = (VectorMap) map2.get(str_alltabs[i]); //取得源2中的的该所有所有列
				String[] str_allcolumns1 = v_columns1.getKeysAsString(); //
				boolean bo_iffind = false; //
				//需要先计算出不同列的表的个数用于最上边的数目的显示
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
						String[] collen_1 = (String[]) v_columns1.get(str_allcolumns1[m]); //取得列信息
						String[] collen_2 = (String[]) v_columns2.get(str_allcolumns1[m]); //取得列信息	
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
					sb_html_c.append("<tr><td colspan=7 bgcolor=\"cyan\">[" + li_count_newcol + "]数据源[" + _dsname_1 + "]比数据源[" + _dsname_2 + "]在表[" + str_alltabs[i] + "]不同的列</td></tr>"); //
					for (int j = 0; j < str_allcolumns1.length; j++) { //遍历所有列
						if (!v_columns2.containsKey(str_allcolumns1[j])) { //如果源2中该表不包含该列,则输出
							li_count++;
							String[] str_colinfo = (String[]) v_columns1.get(str_allcolumns1[j]); //取得列信息
							sb_html_c.append("<tr><td>" + str_alltabs[i] + "</td><td><font color=\"blue\">" + str_colinfo[0] + "</font></td><td>" + str_colinfo[1] + "</td><td>" + str_colinfo[2] + "</td><td></td><td></td><td>alter table " + str_alltabs[i] + " add " + str_colinfo[0] + " "
									+ str_colinfo[1] + "(" + str_colinfo[2] + ")" + ";</td></tr>"); //
						} else if (v_columns2.containsKey(str_allcolumns1[j])) {//如果保函则需要比较列长度是否相等
							String[] collen_1 = (String[]) v_columns1.get(str_allcolumns1[j]); //取得列信息
							String[] collen_2 = (String[]) v_columns2.get(str_allcolumns1[j]); //取得列信息
							if ((!collen_1[1].trim().equals(collen_2[1].trim())) || (!collen_1[2].trim().equals(collen_2[2].trim()))) {//如果类型不一样
								String tt = Integer.parseInt(collen_2[2]) > Integer.parseInt(collen_1[2]) ? collen_2[2] : collen_1[2];
								sb_html_c.append("<tr><td>" + str_alltabs[i] + "</td><td><font color=\"blue\">" + collen_1[0] + "</font></td><td>" + collen_1[1] + "</td><td>" + collen_1[2] + "</td><td>" + collen_2[1] + "</td><td>" + collen_2[2] + "</td><td>alter table " + str_alltabs[i]
										+ " modify column " + collen_1[0] + " " + collen_1[1] + "(" + tt + ");" + "</td></tr>"); //
							}
						}
					}
				}

				/*		for (int j = 0; j < str_allcolumns1.length; j++) { //遍历所有列
							if (!v_columns2.containsKey(str_allcolumns1[j])) { //如果源2中该表不包含该列,则输出
								if (!bo_iffind) {
									li_count++;
									if (li_count != 1) {
										sb_html_c.append("<tr><td colspan=5>&nbsp;<br></td></tr>");
									}
									sb_html_c.append("<tr><td colspan=5 bgcolor=\"cyan\">[" + li_count_newcol+ "]数据源[" + _dsname_1 + "]比数据源[" + _dsname_2 + "]在表[" + str_alltabs[i] + "]多出的列</td></tr>"); //
									bo_iffind = true;
								}
								String[] str_colinfo = (String[]) v_columns1.get(str_allcolumns1[j]); //取得列信息
								sb_html_c.append("<tr><td>" + str_alltabs[i] + "</td><td><font color=\"blue\">" + str_colinfo[0] + "</font></td><td>" + str_colinfo[1] + "</td><td>" + str_colinfo[2] + "</td><td>alter table " + str_alltabs[i] + " add " + str_colinfo[0] + " " + str_colinfo[1]+"("+str_colinfo[2]+")" + ";</td></tr>"); //
							}else if(v_columns2.containsKey(str_allcolumns1[j])){//如果保函则需要比较列长度是否相等
								String[] collen_1 = (String[]) v_columns1.get(str_allcolumns1[j]); //取得列信息
								String[] collen_2 = (String[]) v_columns2.get(str_allcolumns1[j]); //取得列信息
								if((!collen_1[1].trim().equals(collen_2[1].trim()))||(!collen_1[2].trim().equals(collen_2[2].trim()))){//如果类型不一样
									sb_html_c.append("<tr><td>" + str_alltabs[i] + "</td><td><font color=\"blue\">" + collen_1[0] + "</font></td><td>" + collen_1[1] + "</td><td>" + collen_1[2] + "</td><td>alter table " + str_alltabs[i] + " modify column " + collen_1[0] + " " + collen_1[1]+"("+collen_1[2]+");"+collen_2[1]+":"+collen_2[2] + "</td></tr>"); //
								}
							}
						}*/
			} else { //否则死循环输出所有列
				li_count++;
				li_count_newtab++; //
				if (li_count != 1) {
					sb_html_t.append("<tr><td colspan=5>&nbsp;<br></td></tr>");
				}
				sb_html_t.append("<tr><td colspan=5 bgcolor=\"cyan\">[" + li_count_newtab + "]数据源[" + _dsname_1 + "]比数据源[" + _dsname_2 + "]多出的表[" + str_alltabs[i] + "]</td></tr>"); //
				VectorMap v_columns = (VectorMap) map1.get(str_alltabs[i]);
				String[] str_allcolumns = v_columns.getKeysAsString(); //
				for (int j = 0; j < str_allcolumns.length; j++) { //遍历所有列
					String[] str_colinfo = (String[]) v_columns.get(str_allcolumns[j]); //取得列信息
					sb_html_t.append("<tr><td><font color=\"blue\">" + str_alltabs[i] + "</font></td><td><font color=\"blue\">" + str_colinfo[0] + "</font></td><td>" + str_colinfo[1] + "</td><td>" + str_colinfo[2] + "</td>"
							+ (j == 0 ? "<td rowspan=" + str_allcolumns.length + " valign=\"top\">" + getCreateTableSQL(str_alltabs[i], v_columns) + "</td>" : "") + "</tr>"); //
				}
			}
		}
		sb_html_t.append("</table>"); //
		String str_return = "<font size=6 color=\"red\">存在于数据源[" + _dsname_1 + "]而不在数据源[" + _dsname_2 + "]中的情况:新增表[" + li_count_newtab + "]个,不同列的表共有[" + li_count_newcol + "]个</font><br>" + sb_html_t.toString(); //先输出新增表的描述
		str_return += sb_html_c.toString();//再输出列不同的
		return str_return; //
	}

	/**
	 * 根据数据库类型和数据源获得 list的VO数组，包括所有表的一些信息,先区分类型吧  袁江晓  20130425 添加
	 * @param databaseType
	 * @param _ds
	 * @return
	 */
	private ArrayList getTabDesc(String databaseType, String _ds) throws Exception {
		ArrayList al_tnames = new ArrayList(); //创建对象..
		if (null != databaseType && databaseType.equals("Mysql")) {
			String str_schema = ServerEnvironment.getInstance().getDataSourceVO(_ds).getUser(); //获得数据源1的用户
			WLTDBConnection conn = SessionFactory.getInstance().getConnection(Thread.currentThread(), _ds); //取得当前线程的连接!! getConn(_dsname_1); ////
			DatabaseMetaData dbmd = conn.getMetaData(); //取得整个数据库的原数据定义!
			ResultSet resultSet = null; //结果集!!
			resultSet = dbmd.getTables(null, (str_schema == null ? null : str_schema.toUpperCase()), "%", new String[] { "TABLE" });//暂时先考虑表，不考虑视图，因为目前的视图则报错，视图的话手动比较
			while (resultSet.next()) {
				HashVO hvo = new HashVO(); //
				String str_tableName = resultSet.getString("TABLE_NAME"); //表名
				if (str_tableName.toUpperCase().startsWith("BIN$")) {//oracle中删除的表存在回收站，表名以"BIN$"开头，不需要导出，清空回收站可执行一下
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
	 *  获得表结构信息，主要包括表名及列信息  袁江晓  20130425 添加
	 * @param databaseType
	 * @param _ds
	 * @return
	 * @throws Exception
	 */
	private VectorMap getMap(String databaseType, String _ds, String _prefix) throws Exception {
		VectorMap map = new VectorMap(); //
		if (null != databaseType && databaseType.equals("Mysql")) {
			ArrayList list1 = getTabDesc("Mysql", _ds); //先获得数据源1的表信息，方便对表进行遍历
			for (int i = 0; i < list1.size(); i++) {
				HashVO hvo = (HashVO) list1.get(i); //
				String str_tabcode = hvo.getStringValue("tabname").toLowerCase(); //表名
				TableDataStruct tdst = commDMO.getTableDataStructByDS(_ds, "select * from " + str_tabcode + " where 1=2"); //表结构!!
				String[] str_names = tdst.getHeaderName(); ////获得列名数组
				String[] str_type = tdst.getHeaderTypeName(); //获得列类型数组
				int[] li_length = tdst.getPrecision(); //列宽度数组
				//下面部分是取得一个表的主键
				WLTDBConnection conn = SessionFactory.getInstance().getConnection(Thread.currentThread(), _ds); //取得当前线程的连接!! 在后面一张表中执行，一定要在源1中执行操作
				DatabaseMetaData dbmd = conn.getMetaData();
				ResultSet rs = dbmd.getPrimaryKeys(null, null, str_tabcode);
				String pkName = "";
				while (rs.next()) {
					pkName = rs.getString("column_name");//获取主键，便于数据导出用，如果旧库为主键，则新库中依然为主键
				}
				for (int j = 0; j < str_names.length; j++) {
					String str_colname = str_names[j];//列名
					String str_coltype = str_type[j];//列类型
					String str_coldesc = String.valueOf(li_length[j]);//列宽度
					if (_prefix.trim().equals("")) {//如果前缀为空  将所有的表都添加
						if (map.containsKey(str_tabcode)) {
							VectorMap v_columns = (VectorMap) map.get(str_tabcode); //
							v_columns.put(str_colname.toLowerCase(), new String[] { str_colname.toLowerCase(), str_coltype, str_coldesc, pkName }); //
						} else {
							VectorMap v_columns = new VectorMap(); //
							v_columns.put(str_colname.toLowerCase(), new String[] { str_colname.toLowerCase(), str_coltype, str_coldesc, pkName }); //
							map.put(str_tabcode, v_columns); //
						}
					} else {//如果前缀不为空，则进行过滤
						if (str_tabcode.equals("")) {
						}
						if (map.containsKey(str_tabcode)) { //添加列
							VectorMap v_columns = (VectorMap) map.get(str_tabcode); //
							v_columns.put(str_colname.toLowerCase(), new String[] { str_colname.toLowerCase(), str_coltype, str_coldesc, pkName }); //
						} else {//添加表
							VectorMap v_columns = new VectorMap(); //
							v_columns.put(str_colname.toLowerCase(), new String[] { str_colname.toLowerCase(), str_coltype, str_coldesc, pkName }); //
							if (str_tabcode.indexOf(_prefix) > -1) { //进行判断，如果前缀为prefix的则放到map中
								map.put(str_tabcode, v_columns); //
							}
						}
					}

				}
			}
		}
		return map;
	}

	/**适用于mysql的   袁江晓  20130425 添加
	 * 比较列不一样的
	 * @return
	 * @throws Excpetion
	 */
	private String getMysTabOrColumnHtml(String _dsname_1, String _dsname_2, String _pre) throws Exception {
		//源1中的数据..
		VectorMap map1 = getMap("Mysql", _dsname_1, _pre); //只在这里进行过滤  去除表名开头为pre的表
		VectorMap map2 = getMap("Mysql", _dsname_2, _pre); //只在这里进行过滤  去除表名开头为pre的表

		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<table width=\"75%\"   border=\"0\"   cellspacing=\"0\"   cellpadding=\"3\">\r\n"); //
		sb_html.append("<tr><td align=\"center\">表名</td><td align=\"center\">列名</td><td align=\"center\">数据源[" + _dsname_1 + "]列类型</td><td align=\"center\">数据源[" + _dsname_2 + "]列类型</td></tr>\r\n"); //

		String[] str_alltabs = map1.getKeysAsString(); //从遍历源1中所有表开始!!
		String[] str_alltabs11 = map2.getKeysAsString(); //从遍历源1中所有表开始!!
		int li_count = 0;
		for (int i = 0; i < str_alltabs.length; i++) {
			if (map2.containsKey(str_alltabs[i])) { //如果源2中有该表,
				VectorMap v_columns1 = (VectorMap) map1.get(str_alltabs[i]);
				VectorMap v_columns2 = (VectorMap) map2.get(str_alltabs[i]); //取得源2中的的该所有所有列
				String[] str_allcolumns1 = v_columns1.getKeysAsString(); //
				boolean bo_iffind = false; //
				for (int j = 0; j < str_allcolumns1.length; j++) { //遍历所有列
					if (v_columns2.containsKey(str_allcolumns1[j])) { //如果源2也包含该列
						String[] str_colinfo1 = (String[]) v_columns1.get(str_allcolumns1[j]); //取得列信息
						String[] str_colinfo2 = (String[]) v_columns2.get(str_allcolumns1[j]); //取得列信息
						if (!str_colinfo1[1].equals(str_colinfo2[1])) {
							if (!bo_iffind) {
								if (!bo_iffind) {
									li_count++;
									if (li_count != 1) {
										sb_html.append("<tr><td colspan=4>&nbsp;<br><br></td></tr>");
									}
									sb_html.append("<tr><td colspan=4 bgcolor=\"cyan\">[" + li_count + "]两数据源在表[" + str_alltabs[i] + "]的以下列的数据类型不一样</td></tr>\r\n"); //
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

	/**适用于Oracle的
	 * 比较只存于1,不存于2中的数据
	 * @param _dsname_1
	 * @param _dsname_2
	 * @return
	 * @throws Exception
	 */
	private String getOraTabOrColumnHtml(String _dsname_1, String _dsname_2) throws Exception {
		//源1中的数据..
		VectorMap map1 = new VectorMap(); //
		HashVO[] hvs_ds1 = commDMO.getHashVoArrayByDS(_dsname_1, "select * from v_pub_syscolumns order by tabname,colcode");
		for (int i = 0; i < hvs_ds1.length; i++) {
			String str_tabcode = hvs_ds1[i].getStringValue("tabname").toLowerCase(); //表名
			String str_colname = hvs_ds1[i].getStringValue("colname").toLowerCase(); //列名
			String str_coltype = hvs_ds1[i].getStringValue("datatype").toLowerCase(); //列名
			String str_coldesc = hvs_ds1[i].getStringValue("coldesc").toLowerCase(); //列说明
			if (map1.containsKey(str_tabcode)) {
				VectorMap v_columns = (VectorMap) map1.get(str_tabcode); //
				v_columns.put(str_colname, new String[] { str_colname, str_coltype, str_coldesc }); //
			} else {
				VectorMap v_columns = new VectorMap(); //
				v_columns.put(str_colname, new String[] { str_colname, str_coltype, str_coldesc }); //
				map1.put(str_tabcode, v_columns); //
			}
		}

		//源2中的数据
		VectorMap map2 = new VectorMap(); //
		HashVO[] hvs_ds2 = commDMO.getHashVoArrayByDS(_dsname_2, "select * from v_pub_syscolumns order by tabname,colcode");
		for (int i = 0; i < hvs_ds2.length; i++) {
			String str_tabcode = hvs_ds2[i].getStringValue("tabname").toLowerCase(); //表名
			String str_colname = hvs_ds2[i].getStringValue("colname").toLowerCase(); //列名
			String str_coltype = hvs_ds2[i].getStringValue("datatype".toLowerCase()); //列名
			String str_coldesc = hvs_ds2[i].getStringValue("coldesc").toLowerCase(); //列说明
			if (map2.containsKey(str_tabcode)) {
				VectorMap v_columns = (VectorMap) map2.get(str_tabcode); //
				v_columns.put(str_colname, new String[] { str_colname, str_coltype, str_coldesc }); //
			} else {
				VectorMap v_columns = new VectorMap(); //
				v_columns.put(str_colname, new String[] { str_colname, str_coltype, str_coldesc }); //
				map2.put(str_tabcode, v_columns); //
			}
		}

		//找出只存在于1而不存于2中的数据..
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<table width=\"75%\"   border=\"0\"   cellspacing=\"0\"   cellpadding=\"3\">\r\n"); //
		sb_html.append("<tr><td align=\"center\">表名</td><td align=\"center\">列名</td><td align=\"center\">列类型</td><td>SQL语句</td></tr>\r\n"); //
		String[] str_alltabs = map1.getKeysAsString(); //从遍历源1中所有表开始!!
		int li_count = 0; //
		int li_count_newtab = 0; //
		for (int i = 0; i < str_alltabs.length; i++) {
			if (map2.containsKey(str_alltabs[i])) { //如果源2中有该表,则只比较列..
				VectorMap v_columns1 = (VectorMap) map1.get(str_alltabs[i]);
				VectorMap v_columns2 = (VectorMap) map2.get(str_alltabs[i]); //取得源2中的的该所有所有列
				String[] str_allcolumns1 = v_columns1.getKeysAsString(); //
				boolean bo_iffind = false; //
				for (int j = 0; j < str_allcolumns1.length; j++) { //遍历所有列
					if (!v_columns2.containsKey(str_allcolumns1[j])) { //如果源2中该表不包含该列,则输出
						if (!bo_iffind) {
							li_count++;
							if (li_count != 1) {
								sb_html.append("<tr><td colspan=4>&nbsp;<br><br></td></tr>");
							}
							sb_html.append("<tr><td colspan=4 bgcolor=\"cyan\">[" + li_count + "]数据源[" + _dsname_1 + "]比数据源[" + _dsname_2 + "]在表[" + str_alltabs[i] + "]多出的列</td></tr>\r\n"); //
							bo_iffind = true;
						}
						String[] str_colinfo = (String[]) v_columns1.get(str_allcolumns1[j]); //取得列信息
						sb_html.append("<tr><td>" + str_alltabs[i] + "</td><td><font color=\"blue\">" + str_colinfo[0] + "</font></td><td>" + str_colinfo[1] + "</td><td>alter table " + str_alltabs[i] + " add " + str_colinfo[0] + " " + str_colinfo[1] + "(" + str_colinfo[2] + ")" + ";</td></tr>\r\n"); //
					}
				}
			} else { //否则死循环输出所有列
				li_count++;
				if (li_count != 1) {
					sb_html.append("<tr><td colspan=4>&nbsp;<br><br></td></tr>");
				}
				sb_html.append("<tr><td colspan=4 bgcolor=\"cyan\">[" + li_count + "]数据源[" + _dsname_1 + "]比数据源[" + _dsname_2 + "]多出的表[" + str_alltabs[i] + "]</td></tr>\r\n"); //

				li_count_newtab++; //
				VectorMap v_columns = (VectorMap) map1.get(str_alltabs[i]);
				String[] str_allcolumns = v_columns.getKeysAsString(); //
				for (int j = 0; j < str_allcolumns.length; j++) { //遍历所有列
					String[] str_colinfo = (String[]) v_columns.get(str_allcolumns[j]); //取得列信息
					sb_html.append("<tr><td><font color=\"blue\">" + str_alltabs[i] + "</font></td><td><font color=\"blue\">" + str_colinfo[0] + "</font></td><td>" + str_colinfo[1] + "</td>"
							+ (j == 0 ? "<td rowspan=" + str_allcolumns.length + " valign=\"top\">" + getCreateTableSQL(str_alltabs[i], v_columns) + "</td>" : "") + "</tr>\r\n"); //
				}
			}
		}
		sb_html.append("</table>\r\n"); //
		String str_return = "<font size=2 color=\"red\">存在于数据源[" + _dsname_1 + "]而不在数据源[" + _dsname_2 + "]中的情况:新增表[" + li_count_newtab + "]个</font><br>" + sb_html.toString(); //
		return str_return; //
	}

	private String getCreateTableSQL(String _tablename, VectorMap _allcolumns) {
		StringBuffer sb_sql = new StringBuffer(); //
		if (_tablename.startsWith("v_")) {
			sb_sql.append("<font color=\"red\">好象是视图,请慎重执行SQL!</font><br>"); //
		}

		sb_sql.append("create table " + _tablename + "<br>\r\n"); //
		sb_sql.append("(<br>\r\n");
		String[] all_keys = _allcolumns.getKeysAsString(); //
		for (int i = 0; i < all_keys.length - 1; i++) {
			String[] str_colinfo = (String[]) _allcolumns.get(all_keys[i]); //
			sb_sql.append(str_colinfo[0] + " " + str_colinfo[1] + "(" + str_colinfo[2] + ")" + ",<br>\r\n"); //添加字段长度   袁江晓  20130426更改
		}
		String[] str_colinfo = (String[]) _allcolumns.get(all_keys[all_keys.length - 1]); //
		sb_sql.append(str_colinfo[0] + " " + str_colinfo[1] + "(" + str_colinfo[2] + ")" + "<br>\r\n");
		if (null != str_colinfo[3] && !str_colinfo[3].trim().equals("")) {
			sb_sql.append(",constraint pk_" + _tablename + " primary key (" + str_colinfo[3] + ")<br>\r\n"); //先注释掉，主要是因为如果有主键则导入数据会有重复
		}
		sb_sql.append(")DEFAULT CHARSET=gb2312;<br><br>\r\n");
		return sb_sql.toString();
	}

	/**适用于Oracle的
	 * 比较列不一样的
	 * @return
	 * @throws Excpetion
	 */
	private String getOraCompareHtml(String _dsname_1, String _dsname_2) throws Exception {
		//源1中的数据..
		VectorMap map1 = new VectorMap(); //
		HashVO[] hvs_ds1 = commDMO.getHashVoArrayByDS(_dsname_1, "select * from v_pub_syscolumns order by tabname,colcode");
		for (int i = 0; i < hvs_ds1.length; i++) {
			String str_tabcode = hvs_ds1[i].getStringValue("tabname").toLowerCase(); //表名
			String str_colname = hvs_ds1[i].getStringValue("colname").toLowerCase(); //列名
			String str_coltype = hvs_ds1[i].getStringValue("datatype").toLowerCase(); //列名
			String str_coldesc = hvs_ds1[i].getStringValue("coldesc").toLowerCase(); //列说明
			if (map1.containsKey(str_tabcode)) {
				VectorMap v_columns = (VectorMap) map1.get(str_tabcode); //
				v_columns.put(str_colname, new String[] { str_colname, str_coltype, str_coldesc }); //
			} else {
				VectorMap v_columns = new VectorMap(); //
				v_columns.put(str_colname, new String[] { str_colname, str_coltype, str_coldesc }); //
				map1.put(str_tabcode, v_columns); //
			}
		}

		//源2中的数据
		VectorMap map2 = new VectorMap(); //
		HashVO[] hvs_ds2 = commDMO.getHashVoArrayByDS(_dsname_2, "select * from v_pub_syscolumns order by tabname,colcode");
		for (int i = 0; i < hvs_ds2.length; i++) {
			String str_tabcode = hvs_ds2[i].getStringValue("tabname").toLowerCase(); //表名
			String str_colname = hvs_ds2[i].getStringValue("colname").toLowerCase(); //列名
			String str_coltype = hvs_ds2[i].getStringValue("datatype").toLowerCase(); //列名
			String str_coldesc = hvs_ds2[i].getStringValue("coldesc").toLowerCase(); //列说明
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
		sb_html.append("<tr><td align=\"center\">表名</td><td align=\"center\">列名</td><td align=\"center\">数据源[" + _dsname_1 + "]列类型</td><td align=\"center\">数据源[" + _dsname_2 + "]列类型</td></tr>\r\n"); //

		String[] str_alltabs = map1.getKeysAsString(); //从遍历源1中所有表开始!!
		int li_count = 0;
		for (int i = 0; i < str_alltabs.length; i++) {
			if (map2.containsKey(str_alltabs[i])) { //如果源2中有该表,
				VectorMap v_columns1 = (VectorMap) map1.get(str_alltabs[i]);
				VectorMap v_columns2 = (VectorMap) map2.get(str_alltabs[i]); //取得源2中的的该所有所有列
				String[] str_allcolumns1 = v_columns1.getKeysAsString(); //
				boolean bo_iffind = false; //
				for (int j = 0; j < str_allcolumns1.length; j++) { //遍历所有列
					if (v_columns2.containsKey(str_allcolumns1[j])) { //如果源2也包含该列
						String[] str_colinfo1 = (String[]) v_columns1.get(str_allcolumns1[j]); //取得列信息
						String[] str_colinfo2 = (String[]) v_columns2.get(str_allcolumns1[j]); //取得列信息
						if (!str_colinfo1[1].equals(str_colinfo2[1])) {
							if (!bo_iffind) {
								if (!bo_iffind) {
									li_count++;
									if (li_count != 1) {
										sb_html.append("<tr><td colspan=4>&nbsp;<br><br></td></tr>");
									}
									sb_html.append("<tr><td colspan=4 bgcolor=\"cyan\">[" + li_count + "]两数据源在表[" + str_alltabs[i] + "]的以下列的数据类型不一样</td></tr>\r\n"); //
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
