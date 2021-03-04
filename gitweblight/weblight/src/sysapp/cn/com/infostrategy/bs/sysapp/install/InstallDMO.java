package cn.com.infostrategy.bs.sysapp.install;

import java.io.InputStream;
import java.util.ArrayList;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.BSUtil;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.mdata.MetaDataDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;

/**
 * 安装程序的DMO 平台的安装路径是写死的就是cn.com.infostrategy.bs.sysapp.install 其他产品与项目的安装路径就需要在 weblight.xml中定义一个变量【INSTALLPACKAGES】,其值是以分号与等号相隔的字符串,比如【PushGRC产品=com.pushworld.pushgrc.bs.install】
 * 然后系统许多地方就根据这个约定,进行相应的处理!!! 第一步来分有两种数据:一种是永远放在系统中的不需要插入数据库的,比如RegisterMenu.xml(注册的菜单),一种是安装时直接插入数据库的,然后让实施人员配置的,比如： /cn/com/infostrategy/bs/sysapp/install/database/tables.xml (创建所有的表)
 * /cn/com/infostrategy/bs/sysapp/install/database/views.xml (创建所有视图) /cn/com/infostrategy/bs/sysapp/install/templetdata/*.xml (初始化所有单据模板数据,由于单据模板非常大,所以专门搞了一个目录)
 * /cn/com/infostrategy/bs/sysapp/install/initdata/*.xml (初始化所有其他表的数据,比如菜单,注册按钮,注册样板,注册参照,下拉框字典,外观,系统参数,人员)
 * 
 * @author xch
 */
public class InstallDMO extends AbstractDMO {

	private TBUtil tbUtil = new TBUtil(); //

	/**
	 * 根据包名前辍取得对应的Xml文件中的所有需要安装的表的清单!!! 平台包是写死的即cn.com.infostrategy.bs.sysapp.install,其他的包必须使用weblight.xml中来配置!!!
	 * 
	 * @param _package_prefix
	 * @return
	 * @throws Exception
	 */
	public String[] getAllIntallTablesByPackagePrefix(String _package_prefix) throws Exception {
		return getInstallListByPackagePrefix(_package_prefix, "tables.xml", "table", "name"); //
	}

	/**
	 * 根据包名前辍取得对应的Xml文件中的所有需要安装的表的清单!!! 平台包是写死的即cn.com.infostrategy.bs.sysapp.install,其他的包必须使用weblight.xml中来配置!!!
	 * 
	 * @param _package_prefix
	 * @return
	 * @throws Exception
	 */
	public String[][] getAllIntallTablesDescr() throws Exception {
		String[][] str_packages = new BSUtil().getAllInstallPackages("/database"); //
		ArrayList al_tmp = new ArrayList(); //
		for (int i = 0; i < str_packages.length; i++) {
			al_tmp.addAll(getTableDescrByStream(str_packages[i][0] + "tables.xml")); // 加入!

		}
		String[][] str_return = new String[al_tmp.size()][3]; // 三个字段
		for (int i = 0; i < al_tmp.size(); i++) {
			str_return[i] = (String[]) al_tmp.get(i); //
		}
		return str_return; //
	}

	// 直接取得一个包名中的所有table定义!为反向构建序列时可以取得指定包中的表,故增加此方法!【xch/2012-06-07】
	public String[][] getAllIntallTablesDescr(String _packageName) throws Exception {
		String str_packagePfefix = _packageName; //
		str_packagePfefix = tbUtil.replaceAll(str_packagePfefix, ".", "/"); //
		if (!str_packagePfefix.startsWith("/")) { // 如果不是开头,则补上!
			str_packagePfefix = "/" + str_packagePfefix; //
		}
		if (!str_packagePfefix.endsWith("/")) { // 如果不是以/结尾,则加上!!
			str_packagePfefix = str_packagePfefix + "/"; //
		}
		ArrayList al_tables = getTableDescrByStream(str_packagePfefix + "database/tables.xml"); //
		String[][] str_return = new String[al_tables.size()][3]; // 三个字段
		for (int i = 0; i < al_tables.size(); i++) {
			str_return[i] = (String[]) al_tables.get(i); //
		}
		return str_return; //
	}

	// 根据xml文件名取得对应的表定义清单!!
	private ArrayList getTableDescrByStream(String str_xmlfileName) throws Exception {
		ArrayList al_tmp = new ArrayList(); //
		InputStream ins = this.getClass().getResourceAsStream(str_xmlfileName); //
		if (ins != null) { // 如果的确有这个文件!
			org.jdom.Document doc = new org.jdom.input.SAXBuilder().build(ins); // 加载XML
			java.util.List list_tables = doc.getRootElement().getChildren("table");
			for (int j = 0; j < list_tables.size(); j++) {
				org.jdom.Element tableNode = (org.jdom.Element) list_tables.get(j);
				String str_name = tableNode.getAttributeValue("name"); // 表名
				String str_descr = tableNode.getAttributeValue("descr"); // 说明
				String str_pkname = tableNode.getAttributeValue("pkname"); // 主键字段名
				al_tmp.add(new String[] { str_name, str_descr, str_pkname }); // 后来发现要有主键字段名
			}
			ins.close(); //
		}
		return al_tmp; //
	}

	// 取得XML中所有列的说明!用于在创建模板名快速生成所有列的中文说明,因为在XML中已经有了,没必要再录一遍! 从而大大提高效率!!
	public String[][] getAllIntallTabColumnsDescr() throws Exception {
		return getAllIntallTabColumnsDescr(null); //
	}

	public String[][] getAllIntallTabColumnsDescr(String _tabName) throws Exception {
		String[][] str_packages = new BSUtil().getAllInstallPackages("/database"); //
		ArrayList al_tmp = new ArrayList(); //
		ArrayList onetable_tmp = new ArrayList(); // 如果传入_tabName不为空，传回此表的结构.haoming2013-7-4
		boolean isFinded = false; //
		for (int i = 0; i < str_packages.length; i++) { //
			String str_xmlfileName = str_packages[i][0] + "tables.xml"; //
			InputStream ins = this.getClass().getResourceAsStream(str_xmlfileName); //
			if (ins != null) { // 如果的确有这个文件!
				org.jdom.Document doc = new org.jdom.input.SAXBuilder().build(ins); // 加载XML
				java.util.List list_tables = doc.getRootElement().getChildren("table");
				for (int j = 0; j < list_tables.size(); j++) { // 遍历所有表
					onetable_tmp = new ArrayList();
					org.jdom.Element tableNode = (org.jdom.Element) list_tables.get(j);
					String str_tabname = tableNode.getAttributeValue("name"); // 表名
					java.util.List list_cols = ((org.jdom.Element) tableNode.getChildren("columns").get(0)).getChildren("col"); // 取得所有列!!
					for (int k = 0; k < list_cols.size(); k++) {
						org.jdom.Element colNode = (org.jdom.Element) list_cols.get(k); //
						String str_colName = colNode.getAttributeValue("name"); //
						String str_colDescr = colNode.getAttributeValue("descr"); //
						al_tmp.add(new String[] { str_tabname.toUpperCase() + "." + str_colName.toUpperCase(), str_colDescr }); //
						onetable_tmp.add(new String[] { str_tabname.toUpperCase() + "." + str_colName.toUpperCase(), str_colDescr }); //
					}
					if (_tabName != null && str_tabname.equalsIgnoreCase(_tabName)) {
						isFinded = true; //
						ins.close(); //
						ins = null; //
						break; //
					}
				}
				if (ins != null) {
					ins.close(); //
				}
			}
			if (isFinded) {
				break; //
			}
		}
		if (_tabName != null && !_tabName.equals("") && !isFinded) { // 如果没有找到传入表的信息。返回null
			return null;
		}
		String[][] str_return = null;
		if (isFinded) { // 如果找到，把onetable的加入返回即可。haoming 2013-7-4
			str_return = new String[onetable_tmp.size()][2]; //
			for (int i = 0; i < onetable_tmp.size(); i++) {
				str_return[i] = (String[]) onetable_tmp.get(i); //
			}
		} else {
			str_return = new String[al_tmp.size()][2]; //
			for (int i = 0; i < al_tmp.size(); i++) {
				str_return[i] = (String[]) al_tmp.get(i); //
			}
		}

		return str_return; //
	}

	// 取得所有需要安装表的列说明,一条记录是一个HashVO
	public HashVO[] getAllIntallTabColumnsAsHashVO(boolean _isOnlyRefField) throws Exception {
		// 需要进行缓存处理!!

		ArrayList al_hvs = new ArrayList(); //
		String[][] str_packages = new BSUtil().getAllInstallPackages("/database"); //
		for (int i = 0; i < str_packages.length; i++) { //
			String str_xmlfileName = str_packages[i][0] + "tables.xml"; //
			InputStream ins = this.getClass().getResourceAsStream(str_xmlfileName); //
			if (ins != null) { // 如果的确有这个文件!
				org.jdom.Document doc = new org.jdom.input.SAXBuilder().build(ins); // 加载XML
				java.util.List list_tables = doc.getRootElement().getChildren("table"); // 所有表清单
				for (int j = 0; j < list_tables.size(); j++) { // 遍历所有表
					org.jdom.Element tableNode = (org.jdom.Element) list_tables.get(j); //
					String str_tabname = tableNode.getAttributeValue("name"); // 表名
					java.util.List list_cols = ((org.jdom.Element) tableNode.getChildren("columns").get(0)).getChildren("col"); // 取得所有列!!
					for (int k = 0; k < list_cols.size(); k++) { // 遍历各列
						HashVO hvo = new HashVO(); //
						hvo.setAttributeValue("tabname", str_tabname); //
						org.jdom.Element colNode = (org.jdom.Element) list_cols.get(k); //
						java.util.List list_colattrs = colNode.getAttributes(); // 所有属性!!
						for (int r = 0; r < list_colattrs.size(); r++) { // 遍历所有属性!!
							org.jdom.Attribute attribute = (org.jdom.Attribute) list_colattrs.get(r); // 取得这个属性!!
							String str_attrname = attribute.getName(); // 属性名称!
							String str_attrvalue = attribute.getValue(); // 属性值!!
							hvo.setAttributeValue(str_attrname, str_attrvalue); // 设置属性值!!
						} // end for [r]
						if (_isOnlyRefField) { // 如果指定了只输出有reffield的
							if (hvo.containsKeyIgnoreCasel("reffield")) {
								al_hvs.add(hvo); //
							}
						} else {
							al_hvs.add(hvo); //
						}
					} // end for [k]
				} // end for 【j】
			}
		}
		HashVO[] hvs = (HashVO[]) al_hvs.toArray(new HashVO[0]); // 强转!!
		return hvs; // 返回!!
	}

	public HashVO[] getInstallTempletsAsHashVO(String[] _templetCode) throws Exception {
		String[][] str_packages = new BSUtil().getAllInstallPackages("/templetdata"); // 先取得所有包名
		ArrayList al_hvs = new ArrayList(); //
		for (int i = 0; i < _templetCode.length; i++) {
			HashVO hvo = getInstallTempletAsHashVO(str_packages, _templetCode[i]); //
			if (hvo != null) {
				al_hvs.add(hvo); //
			}
		}
		HashVO[] hvs = (HashVO[]) al_hvs.toArray(new HashVO[0]); //
		return hvs; //
	}

	// 根据模板编码取得模板数据以HashVO格式返回!!
	public HashVO getInstallTempletAsHashVO(String[][] _allPackages, String _templetCode) throws Exception {
		String[][] str_packages = null; //
		if (_allPackages == null) {
			str_packages = new BSUtil().getAllInstallPackages("/templetdata"); // 先取得所有包名
		} else {
			str_packages = _allPackages; //
		}
		for (int i = 0; i < str_packages.length; i++) {
			String str_xmlfileName = str_packages[i][0] + _templetCode + ".xml"; // xml文件全名!!
			InputStream ins = this.getClass().getResourceAsStream(str_xmlfileName); //
			if (ins != null) { // 必须要的这个文件!!
				org.jdom.Document doc = new org.jdom.input.SAXBuilder().build(ins); // 加载XML
				java.util.List list_tables = doc.getRootElement().getChildren("record"); // 所有表清单
				org.jdom.Element tableNode = (org.jdom.Element) list_tables.get(0); // 第一个表就是主表!!
				java.util.List list_cols = tableNode.getChildren("col"); // 取得所有列!!
				HashVO hvo = new HashVO(); //
				for (int k = 0; k < list_cols.size(); k++) { // 遍历各列
					org.jdom.Element colNode = (org.jdom.Element) list_cols.get(k); //
					String str_colName = colNode.getAttributeValue("name"); // 列名!!!!
					String str_colValue = colNode.getValue(); // 列的值!!!
					hvo.setAttributeValue(str_colName, str_colValue); // 置入!!
				}
				return hvo; //
			}
		}
		return null; //
	}

	/**
	 * 根据指定的xml前辍去找到对应的xml文件中的某个表名,然后动态创建SQL,然后执行之!!!
	 * 
	 * @param _package_prefix
	 * @param _tabName
	 * @return
	 * @throws Exception
	 */
	public String createTableByPackagePrefix(String _package_prefix, String _tabName) throws Exception {
		try {
			TBUtil tbUtil = new TBUtil(); //
			String str_xmlfileName = tbUtil.replaceAll(_package_prefix, ".", "/"); // 先替换!!!
			if (!str_xmlfileName.startsWith("/")) {
				str_xmlfileName = "/" + str_xmlfileName; //
			}
			if (!str_xmlfileName.endsWith("/")) {
				str_xmlfileName = str_xmlfileName + "/"; //
			}
			str_xmlfileName = str_xmlfileName + "database/tables.xml"; //

			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
			org.jdom.Document doc = builder.build(this.getClass().getResourceAsStream(str_xmlfileName)); // 加载XML
			java.util.List list_tables = doc.getRootElement().getChildren("table");
			for (int i = 0; i < list_tables.size(); i++) {
				org.jdom.Element param = (org.jdom.Element) list_tables.get(i);
				String str_tname = param.getAttributeValue("name"); // 表名
				String str_pkName = param.getAttributeValue("pkname"); // 主键字段名!!
				if ("Y".equals(param.getAttributeValue("ignore"))) {
					continue;
				}
				if (_tabName.equalsIgnoreCase(str_tname)) { // 如果找到了!!
					CommDMO commDMO = new CommDMO();
					// try {
					// commDMO.executeUpdateByDS(null, "drop table " + _tabName); //
					// } catch (Exception ex) {
					// System.err.println("删除表[" + _tabName + "]失败!"); //
					// }
					String str_dbtype = ServerEnvironment.getDefaultDataSourceType(); // 数据源类型!关键
					java.util.List list_cols = ((org.jdom.Element) param.getChildren("columns").get(0)).getChildren("col"); // 取得所有列!!
					StringBuilder sb_sql = new StringBuilder(); //
					sb_sql.append("create table " + str_tname + "("); //
					for (int j = 0; j < list_cols.size(); j++) {
						org.jdom.Element colNode = (org.jdom.Element) list_cols.get(j); //
						String str_colName = colNode.getAttributeValue("name"); //
						String str_colType = colNode.getAttributeValue("type"); // 类型,比如varchar,decimal
						String str_colLength = colNode.getAttributeValue("length"); //
						sb_sql.append(str_colName + " " + convertRealColType(str_colType, str_dbtype) + "(" + str_colLength + ")"); //
						if (str_colName.equalsIgnoreCase(str_pkName)) { // 如果是主键,则必须是not null
							sb_sql.append(" NOT NULL"); // //
						}
						if (j != list_cols.size() - 1) { // 如果没有定义主键,且又是最后一个
							sb_sql.append(","); // //
						} else {
							if (str_pkName != null && !str_pkName.trim().equals("")) { // 如果是最后一个,但主键不为空,即后面还有主键呢
								sb_sql.append(","); // //
							}
						}
					}
					// 搞主键
					if (str_pkName != null && !str_pkName.trim().equals("")) {
						String pk = "pk_" + str_tname;
						if (pk.length() > 30) {
							pk = pk.substring(0, 30);
						}
						String str_pk_cons = "constraint " + pk + " primary key (" + str_pkName + ")"; //
						sb_sql.append(str_pk_cons); // //
					}
					sb_sql.append(")"); //
					if (str_dbtype.equalsIgnoreCase("MYSQL")) {
						sb_sql.append(" DEFAULT CHARSET = GBK"); //
					}
					if (str_dbtype.equalsIgnoreCase("DB2")) {
						sb_sql.append(" IN PUSHSPACE"); //
					}

					String str_createsql = sb_sql.toString(); //
					if (str_dbtype.equalsIgnoreCase("DB2")) {
						str_createsql = str_createsql.toUpperCase(); //
					}
					commDMO.executeUpdateByDS(null, str_createsql); //

					// 创建索引!!!
					java.util.List list_allindexs = param.getChildren("indexs"); //
					if (list_allindexs != null && list_allindexs.size() > 0) { // 可能没定义索引
						java.util.List list_indexs = ((org.jdom.Element) list_allindexs.get(0)).getChildren("index"); // 取得所有列!!
						if (list_indexs != null && list_indexs.size() > 0) {
							for (int j = 0; j < list_indexs.size(); j++) {
								org.jdom.Element indexNode = (org.jdom.Element) list_indexs.get(j); //
								String str_indexName = indexNode.getAttributeValue("name"); //
								String str_indexCols = indexNode.getAttributeValue("cols"); //
								String str_indexsql = "create index " + str_indexName + " on " + str_tname + "(" + str_indexCols + ")"; //
								str_indexsql = str_indexsql.trim(); //
								if (str_dbtype.equalsIgnoreCase("DB2")) {
									str_indexsql = str_indexsql.toUpperCase(); // 转大写!!
								}
								commDMO.executeUpdateByDS(null, str_indexsql); //
							}
						}
					}
					return "成功"; //
				}

			} // end for i
			return "在xml文件中没有找到这个表!"; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return "安装失败,原因:" + ex.getMessage(); //
		}
	}

	/**
	 * 取得所有视图的名称清单!!
	 * 
	 * @param _package_prefix
	 * @return
	 * @throws Exception
	 */
	public String[] getAllIntallViewsByPackagePrefix(String _package_prefix) throws Exception {
		return getInstallListByPackagePrefix(_package_prefix, "views.xml", "view", "name"); //
	}

	/**
	 * 根据指定的xml前辍去找到对应的xml文件中的某个表名,然后动态创建SQL,然后执行之!!!
	 * 
	 * @param _package_prefix
	 * @param _tabName
	 * @return
	 * @throws Exception
	 */
	public String createViewByPackagePrefix(String _package_prefix, String _viewName) throws Exception {
		try {
			TBUtil tbUtil = new TBUtil(); //
			String str_xmlfileName = tbUtil.replaceAll(_package_prefix, ".", "/"); // 先替换!!!
			if (!str_xmlfileName.startsWith("/")) {
				str_xmlfileName = "/" + str_xmlfileName; //
			}
			if (!str_xmlfileName.endsWith("/")) {
				str_xmlfileName = str_xmlfileName + "/"; //
			}
			str_xmlfileName = str_xmlfileName + "database/views.xml"; //

			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
			org.jdom.Document doc = builder.build(this.getClass().getResourceAsStream(str_xmlfileName)); // 加载XML
			java.util.List list_tables = doc.getRootElement().getChildren("view");
			for (int i = 0; i < list_tables.size(); i++) {
				org.jdom.Element param = (org.jdom.Element) list_tables.get(i);
				String str_viewname = param.getAttributeValue("name"); // 表名
				if (_viewName.equalsIgnoreCase(str_viewname)) { // 如果找到了!!
					CommDMO commDMO = new CommDMO();
					// try {
					// commDMO.executeUpdateByDS(null, "drop view " + _viewName); //
					// } catch (Exception ex) {
					// System.err.println("删除视图[" + _viewName + "]失败!"); //
					// }
					String str_dbtype = ServerEnvironment.getDefaultDataSourceType(); //
					String str_sql = ((org.jdom.Element) param.getChildren("sql").get(0)).getText(); //
					// System.out.println(str_sql); //
					str_sql = str_sql.trim(); //
					str_sql = tbUtil.replaceAll(str_sql, "\r", ""); //
					str_sql = tbUtil.replaceAll(str_sql, "\n", ""); //
					if (str_sql.endsWith(";")) { // //
						str_sql = str_sql.substring(0, str_sql.length() - 1); //
					}
					if (str_dbtype.equalsIgnoreCase("DB2")) {
						str_sql = str_sql.toUpperCase(); //
					}
					commDMO.executeUpdateByDS(null, str_sql, false); //
					return "成功"; //
				}
			}
			return "在xml文件中没有找到这个视图!"; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return "安装失败,原因:" + ex.getMessage(); //
		}
	}

	/**
	 * 取得所有需要初始化的表的清单!!!
	 * 
	 * @param _package_prefix
	 * @return
	 * @throws Exception
	 */
	public String[] getAllIntallInitDataByPackagePrefix(String _package_prefix, String _xtdatadir) throws Exception {
		String str_xmlfileName = tbUtil.replaceAll(_package_prefix, ".", "/"); // 先替换!!!
		if (!str_xmlfileName.startsWith("/")) {
			str_xmlfileName = "/" + str_xmlfileName; //
		}
		if (!str_xmlfileName.endsWith("/")) {
			str_xmlfileName = str_xmlfileName + "/"; //
		}
		str_xmlfileName = str_xmlfileName + "xtdata/"; //
		if (_xtdatadir != null && !_xtdatadir.trim().equals("")) { // 如果安装数据有指定的子目录!!
			str_xmlfileName = str_xmlfileName + _xtdatadir; // 加上子目录,比如hegui
			if (!str_xmlfileName.endsWith("/")) {
				str_xmlfileName = str_xmlfileName + "/"; //
			}
		}
		str_xmlfileName = str_xmlfileName + "_filelist.ini"; //
		return getFileList(str_xmlfileName); //
	}

	/**
	 * 取得文件_filelist.ini中的所有文件名列表!
	 * 
	 * @param _text
	 * @return
	 */
	private String[] getFileList(String _fileName) {
		TBUtil tbUtil = new TBUtil(); //
		InputStream ins = this.getClass().getResourceAsStream(_fileName); //
		if (ins == null) {
			return null;
		}
		String str_text = tbUtil.readFromInputStreamToStr(ins); // 读文件!!这里面自动关闭了文件流!!!
		String[] str_fileNames = tbUtil.split(str_text, "\n"); //
		ArrayList al_list = new ArrayList(); //
		for (int i = 0; i < str_fileNames.length; i++) {
			str_fileNames[i] = str_fileNames[i].trim(); //
			str_fileNames[i] = tbUtil.replaceAll(str_fileNames[i], "\r", ""); //
			str_fileNames[i] = tbUtil.replaceAll(str_fileNames[i], " ", ""); //
			if (!str_fileNames[i].equals("") && !str_fileNames[i].startsWith("#")) {
				al_list.add(str_fileNames[i]); //
			}
		}
		return (String[]) al_list.toArray(new String[0]); //
	}

	/**
	 * 插入初始化数据
	 * 
	 * @param _package_prefix
	 * @param _viewName
	 * @return
	 * @throws Exception
	 */
	public String InsertInitDataByPackagePrefix(String _package_prefix, String _xtdatadir, String _fileName) throws Exception {
		try {
			String str_xmlfileName = tbUtil.replaceAll(_package_prefix, ".", "/"); // 先替换!!!
			if (!str_xmlfileName.startsWith("/")) {
				str_xmlfileName = "/" + str_xmlfileName; //
			}
			if (!str_xmlfileName.endsWith("/")) {
				str_xmlfileName = str_xmlfileName + "/"; //
			}
			str_xmlfileName = str_xmlfileName + "xtdata/";
			if (_xtdatadir != null && !_xtdatadir.trim().equals("")) { // 如果安装数据有指定的子目录!!
				str_xmlfileName = str_xmlfileName + _xtdatadir; // 加上子目录,比如hegui
				if (!str_xmlfileName.endsWith("/")) {
					str_xmlfileName = str_xmlfileName + "/"; //
				}
			}
			str_xmlfileName = str_xmlfileName + _fileName; //
			InputStream ins = this.getClass().getResourceAsStream(str_xmlfileName); // 取得文件流!!!
			if (ins == null) {
				return "没有取得文件[" + str_xmlfileName + "]"; //
			}
			String str_xmlText = tbUtil.readFromInputStreamToStr(ins); // 取得文件内容
			new MetaDataDMO().importXmlToTable1000Records(null, _fileName, str_xmlText); // 插入这一批数据!!!
			return "成功!"; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return "安装失败,原因:" + ex.getMessage(); //
		}
	}

	/**
	 * 取得第三方的安装数据包!比如外规!!!外规数据因为太大,所以单独定义成一种外规扩展数据安装,在lib/下单独放一个外规安装包,然后在RegisterMenu中注册结点ext3data 【xch/2012-03-12】
	 * 
	 * @param _package_prefix
	 * @return
	 * @throws Exception
	 */
	public String[][] getExt3DataXmlFiles(String _package_prefix) throws Exception {
		String str_xmlfileName = tbUtil.replaceAll(_package_prefix, ".", "/"); // 先替换!!!
		if (!str_xmlfileName.startsWith("/")) {
			str_xmlfileName = "/" + str_xmlfileName; //
		}
		if (!str_xmlfileName.endsWith("/")) {
			str_xmlfileName = str_xmlfileName + "/"; //
		}
		org.jdom.input.SAXBuilder sax1 = new org.jdom.input.SAXBuilder(); //
		MetaDataDMO metaDMO = new MetaDataDMO(); //
		InputStream ins_regxml = this.getClass().getResourceAsStream(str_xmlfileName + "RegisterMenu.xml"); //
		org.jdom.Document doc = sax1.build(ins_regxml); // 加载XML
		ins_regxml.close(); //
		java.util.List list_extNodes = doc.getRootElement().getChildren("ext3data"); //
		if (list_extNodes.size() > 0) { //
			ArrayList al_fileNames = new ArrayList(); //
			for (int i = 0; i < list_extNodes.size(); i++) { // 遍历!!!
				org.jdom.Element itemNode = (org.jdom.Element) list_extNodes.get(i); //
				String str_nodeName = itemNode.getAttributeValue("name"); // 名称
				String str_package = itemNode.getAttributeValue("package"); // 包名前辍
				str_package = tbUtil.replaceAll(str_package, ".", "/"); // 先替换!!!
				if (!str_package.startsWith("/")) {
					str_package = "/" + str_package; //
				}
				if (!str_package.endsWith("/")) {
					str_package = str_package + "/"; //
				}
				String[] str_dataxmls = getFileList(str_package + "_filelist.ini"); // 取得文件列表中定义的xml数据文件名!!!
				if (str_dataxmls == null) {
					return new String[][] { { "没有定义文件[" + str_package + "_filelist.ini]", null } };
				}
				for (int j = 0; j < str_dataxmls.length; j++) { // 遍历安装xml
					al_fileNames.add(new String[] { str_nodeName, str_package + str_dataxmls[j] }); // 名称与实际xml路径
				}
			}
			return (String[][]) al_fileNames.toArray(new String[][] { {} }); // 返回!!
		} else {
			return new String[][] { { "没有注册定义需要安装的扩展数据!", null } }; //
		}
	}

	/**
	 * 安装第三方数据包!!
	 * 
	 * @param _xmlFileName
	 * @return
	 * @throws Exception
	 */
	public String installExt3Data(String _xmlFileName) throws Exception {
		InputStream ins_xml = this.getClass().getResourceAsStream(_xmlFileName); // 取得文件流!!!
		if (ins_xml != null) { // 如果有这个XML文件!
			String str_xmlText = tbUtil.readFromInputStreamToStr(ins_xml); // 取得文件内容
			new MetaDataDMO().importXmlToTable1000Records(null, _xmlFileName, str_xmlText); // 插入这个XML中的数据!!!
			ins_xml.close(); // 关闭流!!
			return "成功";
		} else {
			return "文件不存在";
		}

	}

	/**
	 * 为取得表,视图,初始化数据三者清单的重用方法!!
	 * 
	 * @param _package_prefix
	 * @param _xmlfile
	 * @param _firstNodeName
	 * @param _firstNodeAttr
	 * @return
	 * @throws Exception
	 */
	private String[] getInstallListByPackagePrefix(String _package_prefix, String _xmlfile, String _firstNodeName, String _firstNodeAttr) throws Exception {
		TBUtil tbUtil = new TBUtil(); //
		String str_xmlfileName = tbUtil.replaceAll(_package_prefix, ".", "/"); //
		if (!str_xmlfileName.endsWith("/")) { // 如果不是以/结尾,则加上!!
			str_xmlfileName = str_xmlfileName + "/"; //
		}
		str_xmlfileName = str_xmlfileName + "database/" + _xmlfile; //
		if (!str_xmlfileName.startsWith("/")) {
			str_xmlfileName = "/" + str_xmlfileName; //
		}
		org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
		java.io.InputStream fileIns = this.getClass().getResourceAsStream(str_xmlfileName); //
		if (fileIns == null) {
			return null;
		}
		org.jdom.Document doc = builder.build(fileIns); // 加载XML
		java.util.List list_tables = doc.getRootElement().getChildren(_firstNodeName);
		String[] str_tableNames = new String[list_tables.size()]; //
		for (int i = 0; i < list_tables.size(); i++) {
			org.jdom.Element param = (org.jdom.Element) list_tables.get(i);
			String str_tname = param.getAttributeValue(_firstNodeAttr);
			str_tableNames[i] = str_tname; //
		}
		return str_tableNames; // //
	}

	public String convertRealColType(String _type, String _dbType) {
		if (_type.equalsIgnoreCase("decimal") || _type.equalsIgnoreCase("number")) { // 如果是数字类型
			if (_dbType.equalsIgnoreCase("ORACLE")) {
				return "number"; //
			} else {
				return "decimal"; //
			}
		} else if (_type.equalsIgnoreCase("varchar")) {
			if (_dbType.equalsIgnoreCase("ORACLE")) {
				return "varchar2"; //
			} else {
				return "varchar"; //
			}
		} else if (_type.equalsIgnoreCase("char")) {
			return "char"; //
		} else {
			return _type; //
		}
	}

	// 取得所有级联删除的外键定义的VO
	public HashVO[] getAllCascadeRefFieldHVO() throws Exception {
		if (ServerEnvironment.allCascadeRefFieldHVO != null) { // 做缓存处理!!
			return ServerEnvironment.allCascadeRefFieldHVO; //
		}
		ServerEnvironment.allCascadeRefFieldHVO = getAllIntallTabColumnsAsHashVO(true); // 取得所有列!!!
		return ServerEnvironment.allCascadeRefFieldHVO; //
	}

	// 根据基础表的删除动作,批量计算出与之关联的所有子表的级联删除的动作!!
	public String[] getCascadeDeleteSQL(String _table, String _field, String _value) throws Exception {
		HashVO[] hvs_allcols = getAllCascadeRefFieldHVO(); //
		ArrayList al_sqls = new ArrayList(); //
		for (int i = 0; i < hvs_allcols.length; i++) { // 遍历所有列!!
			String str_reffield = hvs_allcols[i].getStringValue("reffield"); // 引用字段
			if (str_reffield != null && str_reffield.equalsIgnoreCase(_table + "." + _field)) { // 如果正好是本基础数据表的字段名!
				String str_tabname = hvs_allcols[i].getStringValue("tabname"); // 业务表名!
				String str_colname = hvs_allcols[i].getStringValue("name"); // 业务表字段名!
				String str_isMulti = hvs_allcols[i].getStringValue("ismulti"); //
				if (str_isMulti == null || str_isMulti.trim().equals("") || !tbUtil.isExistInArray(str_isMulti, new String[] { "N", "N1", "N2", "Y" })) { // 如果为空,或者不是四个字符其中之一!!!
					str_isMulti = "N1"; //
				}
				String str_sql = null; //
				if (str_isMulti.equals("Y")) { // 如果是多选,则强行做update
					str_sql = "update " + str_tabname + " set " + str_colname + "=replace(" + str_colname + ",';" + _value + ";',';') where " + str_colname + " like '%;" + _value + ";%'"; // 拼接SQL!
				} else if (str_isMulti.equals("N") || str_isMulti.equals("N1")) { // 如果是单选,且又是级联删除!则拼接Delete
					str_sql = "delete from " + str_tabname + " where " + str_colname + "='" + _value + "'"; // 拼接SQL!
				} else if (str_isMulti.equals("N2")) {
					str_sql = "update " + str_tabname + " set " + str_colname + "=null where " + str_colname + "='" + _value + "'"; // 拼接SQL!
				}
				if (str_sql != null) {
					al_sqls.add(str_sql); // //
				}
			}
		}
		return (String[]) al_sqls.toArray(new String[0]); //
	}

	// 根据基础表的修改动作,级联修改所有子表的SQL
	public String[] getCascadeUpdateSQL(String _table, String _field, String _oldvalue, String _newValue) throws Exception {
		HashVO[] hvs_allcols = getAllCascadeRefFieldHVO(); //
		ArrayList al_sqls = new ArrayList(); //
		for (int i = 0; i < hvs_allcols.length; i++) { // 遍历所有列!!
			String str_reffield = hvs_allcols[i].getStringValue("reffield"); // 引用字段
			if (str_reffield != null && str_reffield.equalsIgnoreCase(_table + "." + _field)) { // 如果正好是本基础数据表的字段名!
				String str_tabname = hvs_allcols[i].getStringValue("tabname"); // 业务表名!
				String str_colname = hvs_allcols[i].getStringValue("name"); // 业务表字段名!
				String str_isMulti = hvs_allcols[i].getStringValue("ismulti"); //
				if (str_isMulti == null || str_isMulti.trim().equals("") || !tbUtil.isExistInArray(str_isMulti, new String[] { "N", "N1", "N2", "Y" })) { // 如果为空,或者不是四个字符其中之一!!!
					str_isMulti = "N1"; //
				}
				String str_sql = null; //
				if (str_isMulti.equals("Y")) { // 如果是多选,则强行做update
					str_sql = "update " + str_tabname + " set " + str_colname + "=replace(" + str_colname + ",';" + _oldvalue + ";',';" + _newValue + ";') where " + str_colname + " like '%;" + _oldvalue + ";%'"; // 拼接SQL!
				} else if (str_isMulti.equals("N") || str_isMulti.equals("N1") || str_isMulti.equals("N2")) { // 如果是单选,则直接修改成新值!!!
					str_sql = "update " + str_tabname + " set " + str_colname + "='" + _newValue + "' where " + str_colname + "='" + _oldvalue + "'"; // 拼接SQL!
				}
				if (str_sql != null) {
					al_sqls.add(str_sql); // //
				}
			}
		}
		return (String[]) al_sqls.toArray(new String[0]); //
	}

	// 取得级联删除的警告SQL,即自动生成有关系时的各种又关联不上的SQL
	public String[] getCascadeWarnSQL(boolean _isPreSelect) throws Exception {
		ArrayList al_sqls = new ArrayList(); //
		HashVO[] hvs_allcols = getAllCascadeRefFieldHVO(); //
		for (int i = 0; i < hvs_allcols.length; i++) { // 遍历所有列!!
			String str_reffield = hvs_allcols[i].getStringValue("reffield"); // 引用字段
			if (str_reffield != null) { // 如果正好是本基础数据表的字段名!
				if (!str_reffield.contains(".")) {// 如果配置错了，没有点号，则继续循环，否则后面会报错【李春娟/2012-08-01】
					continue;
				}
				String str_base_tabname = str_reffield.substring(0, str_reffield.indexOf(".")); // 基础表的表名!!
				String str_base_colname = str_reffield.substring(str_reffield.indexOf(".") + 1, str_reffield.length()); // 基础表的列名!!
				String str_tabname = hvs_allcols[i].getStringValue("tabname"); // 业务表名!
				String str_colname = hvs_allcols[i].getStringValue("name"); // 业务表字段名!
				String str_isMulti = hvs_allcols[i].getStringValue("ismulti"); //
				String str_sql = null; //
				if (str_isMulti == null || str_isMulti.trim().equals("") || str_isMulti.startsWith("N")) { // 如果是单选
					if (_isPreSelect) {
						str_sql = "select count(*) from " + str_tabname + " where " + str_colname + " not in (select " + str_base_colname + " from " + str_base_tabname + ")"; //
					} else {
						str_sql = "delete from " + str_tabname + " where " + str_colname + " not in (select " + str_base_colname + " from " + str_base_tabname + ")"; //
					}
					al_sqls.add(str_sql); // //
				}
			}
		}
		return (String[]) al_sqls.toArray(new String[0]); //
	}

	/**
	 * 取得所有注册的菜单,即从平台取,再见根据 INSTALLAPPS 参数从对应的各个包中取!!! 最终返回 包名,文件名,路径名,最主要的是路径名!!!
	 * 
	 * @return
	 * @throws Exception
	 */
	public ArrayList getAllRegistMenu() throws Exception {
		ArrayList al_return = new ArrayList(); //
		String[][] str_installPackages = new BSUtil().getAllInstallPackages(""); //
		for (int i = 0; i < str_installPackages.length; i++) {
			String str_fileName = str_installPackages[i][0] + "RegisterMenu.xml"; //
			ArrayList al_menu = getRegistMenuFromXML(str_fileName); //
			if (al_menu != null) {
				al_return.addAll(al_menu); // 取得菜单资源内容!!!
			}
		}
		return al_return; // 返回!!!
	}

	public ArrayList getRegistMenuFromXML(String _xmlFile) throws Exception {
		try {
			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
			InputStream ins = this.getClass().getResourceAsStream(_xmlFile); //
			if (ins == null) {
				System.err.println("文件[" + _xmlFile + "]不存在!!!"); //
				return null;
			}
			org.jdom.Document doc = builder.build(ins); // 加载XML
			java.util.List list_tables = doc.getRootElement().getChildren("menu"); // 找到所有菜单!!
			ArrayList al_return = new ArrayList(); //
			for (int i = 0; i < list_tables.size(); i++) {
				org.jdom.Element param = (org.jdom.Element) list_tables.get(i); //
				String str_menuName = param.getAttributeValue("name"); //
				String str_menuCommand = param.getAttributeValue("command"); //
				String str_menuDescr = param.getAttributeValue("descr"); //
				String str_menuConf = param.getAttributeValue("conf"); //
				String str_menuicon = param.getAttributeValue("icon"); // 获取到xml中图标配置
				String str_menucommandtype = param.getAttributeValue("commandtype");
				al_return.add(new String[] { _xmlFile, str_menuName, str_menuCommand, str_menuDescr, str_menuConf, str_menuicon, str_menucommandtype }); //
			}
			return al_return; //
		} catch (Exception ex) {
			System.err.println("解析XML文件[" + _xmlFile + "]发生异常:[" + ex.getMessage() + "]"); //
			throw null;
		}
	}

	/**
	 * 取个某个注册菜单的命令参数!!!
	 * 
	 * @param _xmlFile
	 * @param _menuName
	 * @return
	 * @throws Exception
	 */
	public String[] getOneRegMenuCommand(String _xmlFile, String _menuName) throws Exception {
		org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
		org.jdom.Document doc = builder.build(this.getClass().getResourceAsStream(_xmlFile)); // 加载XML
		java.util.List list_tables = doc.getRootElement().getChildren("menu"); // 找到所有菜单!!
		for (int i = 0; i < list_tables.size(); i++) {
			org.jdom.Element param = (org.jdom.Element) list_tables.get(i); //
			String str_menuName = param.getAttributeValue("name"); //
			if (str_menuName.equals(_menuName)) {
				return new String[] { param.getAttributeValue("command"), param.getAttributeValue("conf"), param.getAttributeValue("commandtype") }; // 返回其命令!!!
			}
		}
		return null; //
	}

}
