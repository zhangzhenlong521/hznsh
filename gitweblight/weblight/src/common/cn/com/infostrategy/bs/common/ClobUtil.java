package cn.com.infostrategy.bs.common;

import java.util.ArrayList;
import java.util.HashMap;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;

/**
 * 处理大字段的存取
 */
public class ClobUtil {
	private CommDMO dmo = null;

	/**
	 * 此逻辑要加在commdmo中的取hashvo等的数据库操作中
	 * @param value
	 * @return
	 */
	public String getClob(String value) {
		if (value != null && value.toString().startsWith("pushclob:")) { // 对大字段的处理，如果值是以特殊字符开头的
			try {
				String clobbachid = value.substring(9);
				if (clobbachid != null && !"".equals(clobbachid)) {
					int clobbachid_ = Integer.parseInt(clobbachid);
					HashVO[] hvs = getDMO().getHashVoArrayByDS(null, "select * from pub_clob where batchid='" + clobbachid_ + "' order by seq"); // 需要将大字段拼接起来
					StringBuffer sb_clob = new StringBuffer();
					String str_item = null;
					for (int i = 0; i < hvs.length; i++) {
						for (int j = 0; j < 10; j++) {
							str_item = hvs[i].getStringValue("clob" + j);
							if (str_item != null && !str_item.equals("")) {
								sb_clob.append(str_item.trim());
							} else {
								break;
							}
						}
					}
					return sb_clob.toString();
				}
			} catch (Exception e) {
				return value;
			}
		}
		return value;
	}

	public HashMap saveClob(HashMap map) throws Exception {
		HashMap rtnmap = new HashMap();
		rtnmap.put("value", saveClob((String) map.get("value"), null));
		return rtnmap;
	}

	public CommDMO getDMO() {
		if (dmo == null) {
			dmo = new CommDMO();
		}
		return dmo;
	}

	public HashMap updateClob(HashMap map) throws Exception {
		HashMap rtnmap = new HashMap();
		String value = (String) map.get("value");
		String whereCondition = (String) map.get("whereCondition");
		if (map.get("whereCondition") != null && !"".equals(map.get("whereCondition").toString().trim())) {
			String tablename = (String) map.get("tablename");
			String columnname = (String) map.get("columnname");
			//以前的查询都将pushclob:标识符转换成pub_clob中实际值了，故新增了查询方法，获得不转换的值【李春娟/2018-08-12】
			String[][] str_data = getDMO().getStringArrayByDS(null, "select " + columnname + " from " + tablename + " where " + map.get("whereCondition").toString(), true, false);
			String oldValue;
			if (str_data != null && str_data.length > 0 && str_data[0] != null) {
				oldValue = str_data[0][0]; //
				if (oldValue != null && oldValue.toString().startsWith("pushclob:")) {
					String clobbachid = oldValue.substring(9);
					if (clobbachid != null && !"".equals(clobbachid)) {
						try {
							int clobbachid_ = Integer.parseInt(clobbachid);
							rtnmap.put("value", saveClob(value, clobbachid));
							return rtnmap;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		rtnmap.put("value", saveClob(value, null));
		return rtnmap;
	}

	/**
	 * 此逻辑要加在insertsqlbuilder与updatesqlbuilder中
	 * @param value
	 * @param str_batchid
	 * @return
	 * @throws Exception
	 */
	// 袁江晓 20180125更改 旧的保存逻辑有问题
	public String saveClob(String value, String str_batchid) throws Exception {
		if (str_batchid == null) {
			str_batchid = getDMO().getSequenceNextValByDS(null, "S_PUB_CLOB_BATCHID");
		} else {
			getDMO().executeUpdateByDS(null, "delete from pub_clob where batchid='" + str_batchid + "'");
		}
		if (value != null) {
			ArrayList al_data = TBUtil.getTBUtil().split(value, 10, 1200);//旧的是2000，但是实际中发现有的数据库1个中文占三个字节，实际上4000/3=1333，但是为了防止错误，改成了偶数  20180322 袁江晓更改
			ArrayList al_sql = new ArrayList();
			for (int i = 0; i < al_data.size(); i++) {
				InsertSQLBuilder isql_insert = new InsertSQLBuilder("pub_clob");
				String str_id = getDMO().getSequenceNextValByDS(null, "S_PUB_CLOB");
				isql_insert.putFieldValue("id", str_id);
				isql_insert.putFieldValue("batchid", str_batchid);
				String[] str_rowData = (String[]) al_data.get(i);
				StringBuffer sb_str = new StringBuffer("insert into pub_clob(id,batchid,seq,");
				for (int j = 0; j < str_rowData.length - 1; j++) {
					sb_str.append("clob" + j).append(",");
				}
				sb_str.append("clob" + (str_rowData.length - 1));
				sb_str.append(")values(").append("'").append(str_id).append("',").append("'").append(str_batchid).append("',").append(i + 1);
				for (int j = 0; j < str_rowData.length - 1; j++) {
					sb_str.append(",'").append(str_rowData[j].toString().replace("#", "")).append("'");// 20180316
					// 袁江晓
				}
				sb_str.append(",'").append(str_rowData[str_rowData.length - 1].toString()).append("')");
				System.out.println("clobsql==========" + sb_str);
				al_sql.add(sb_str);
			}
			getDMO().executeBatchByDS(null, al_sql);
		}
		return "pushclob:" + str_batchid;
	}

	public static void main(String[] args) {
		String value = "Clob字段1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。&#xD;1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。&#xD;1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。&#xD;1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。&#xD;1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。&#xD;1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。&#xD;1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。&#xD;1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。&#xD;1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。&#xD;1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。&#xD; bhgvvghv gtn1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。&#xD;1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。&#xD;1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。&#xD;1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。&#xD;1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。&#xD;1.稽核依据：国家财经法律、法规、中国内部审计准则、保险行业监管规章、中国太平保险集团有关管理制度、太平人寿保险有限公司相关业务管理规定、财务管理规定以及上海分公司的有关内部管理规定等。1.稽核依据：国家财经法";

		System.out.println("value=======" + value);
		System.out.println("value.length=======" + value.length());
		ArrayList al_data = TBUtil.getTBUtil().split(value, 10, 1200);
		ArrayList al_sql = new ArrayList();
		System.out.println("al_data.length==" + al_data.size());
		for (int j = 0; j < al_data.size(); j++) {
			InsertSQLBuilder isql_insert = new InsertSQLBuilder("pub_clob");
			isql_insert.putFieldValue("id", j);
			isql_insert.putFieldValue("batchid", "0");
			String[] str_rowData = (String[]) al_data.get(j);
			StringBuffer sb_str = new StringBuffer("insert into pub_clob(id,batchid,seq,");
			for (int k = 0; k < str_rowData.length - 1; k++) {
				sb_str.append("clob" + k).append(",");
			}
			sb_str.append("clob" + (str_rowData.length - 1));
			sb_str.append(")values(").append("'").append(j).append("',").append("'").append("0").append("',").append(j + 1);
			for (int k = 0; k < str_rowData.length - 1; k++) {
				sb_str.append(",'").append(str_rowData[k].toString()).append("'");
			}
			sb_str.append(",'").append(str_rowData[str_rowData.length - 1].toString()).append("')");
			System.out.println("========" + sb_str);
			al_sql.add(sb_str);
		}

	}

}
