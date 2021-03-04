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
 * ��װ�����DMO ƽ̨�İ�װ·����д���ľ���cn.com.infostrategy.bs.sysapp.install ������Ʒ����Ŀ�İ�װ·������Ҫ�� weblight.xml�ж���һ��������INSTALLPACKAGES��,��ֵ���Էֺ���Ⱥ�������ַ���,���硾PushGRC��Ʒ=com.pushworld.pushgrc.bs.install��
 * Ȼ��ϵͳ���ط��͸������Լ��,������Ӧ�Ĵ���!!! ��һ����������������:һ������Զ����ϵͳ�еĲ���Ҫ�������ݿ��,����RegisterMenu.xml(ע��Ĳ˵�),һ���ǰ�װʱֱ�Ӳ������ݿ��,Ȼ����ʵʩ��Ա���õ�,���磺 /cn/com/infostrategy/bs/sysapp/install/database/tables.xml (�������еı�)
 * /cn/com/infostrategy/bs/sysapp/install/database/views.xml (����������ͼ) /cn/com/infostrategy/bs/sysapp/install/templetdata/*.xml (��ʼ�����е���ģ������,���ڵ���ģ��ǳ���,����ר�Ÿ���һ��Ŀ¼)
 * /cn/com/infostrategy/bs/sysapp/install/initdata/*.xml (��ʼ�����������������,����˵�,ע�ᰴť,ע������,ע�����,�������ֵ�,���,ϵͳ����,��Ա)
 * 
 * @author xch
 */
public class InstallDMO extends AbstractDMO {

	private TBUtil tbUtil = new TBUtil(); //

	/**
	 * ���ݰ���ǰ�ȡ�ö�Ӧ��Xml�ļ��е�������Ҫ��װ�ı���嵥!!! ƽ̨����д���ļ�cn.com.infostrategy.bs.sysapp.install,�����İ�����ʹ��weblight.xml��������!!!
	 * 
	 * @param _package_prefix
	 * @return
	 * @throws Exception
	 */
	public String[] getAllIntallTablesByPackagePrefix(String _package_prefix) throws Exception {
		return getInstallListByPackagePrefix(_package_prefix, "tables.xml", "table", "name"); //
	}

	/**
	 * ���ݰ���ǰ�ȡ�ö�Ӧ��Xml�ļ��е�������Ҫ��װ�ı���嵥!!! ƽ̨����д���ļ�cn.com.infostrategy.bs.sysapp.install,�����İ�����ʹ��weblight.xml��������!!!
	 * 
	 * @param _package_prefix
	 * @return
	 * @throws Exception
	 */
	public String[][] getAllIntallTablesDescr() throws Exception {
		String[][] str_packages = new BSUtil().getAllInstallPackages("/database"); //
		ArrayList al_tmp = new ArrayList(); //
		for (int i = 0; i < str_packages.length; i++) {
			al_tmp.addAll(getTableDescrByStream(str_packages[i][0] + "tables.xml")); // ����!

		}
		String[][] str_return = new String[al_tmp.size()][3]; // �����ֶ�
		for (int i = 0; i < al_tmp.size(); i++) {
			str_return[i] = (String[]) al_tmp.get(i); //
		}
		return str_return; //
	}

	// ֱ��ȡ��һ�������е�����table����!Ϊ���򹹽�����ʱ����ȡ��ָ�����еı�,�����Ӵ˷���!��xch/2012-06-07��
	public String[][] getAllIntallTablesDescr(String _packageName) throws Exception {
		String str_packagePfefix = _packageName; //
		str_packagePfefix = tbUtil.replaceAll(str_packagePfefix, ".", "/"); //
		if (!str_packagePfefix.startsWith("/")) { // ������ǿ�ͷ,����!
			str_packagePfefix = "/" + str_packagePfefix; //
		}
		if (!str_packagePfefix.endsWith("/")) { // ���������/��β,�����!!
			str_packagePfefix = str_packagePfefix + "/"; //
		}
		ArrayList al_tables = getTableDescrByStream(str_packagePfefix + "database/tables.xml"); //
		String[][] str_return = new String[al_tables.size()][3]; // �����ֶ�
		for (int i = 0; i < al_tables.size(); i++) {
			str_return[i] = (String[]) al_tables.get(i); //
		}
		return str_return; //
	}

	// ����xml�ļ���ȡ�ö�Ӧ�ı����嵥!!
	private ArrayList getTableDescrByStream(String str_xmlfileName) throws Exception {
		ArrayList al_tmp = new ArrayList(); //
		InputStream ins = this.getClass().getResourceAsStream(str_xmlfileName); //
		if (ins != null) { // �����ȷ������ļ�!
			org.jdom.Document doc = new org.jdom.input.SAXBuilder().build(ins); // ����XML
			java.util.List list_tables = doc.getRootElement().getChildren("table");
			for (int j = 0; j < list_tables.size(); j++) {
				org.jdom.Element tableNode = (org.jdom.Element) list_tables.get(j);
				String str_name = tableNode.getAttributeValue("name"); // ����
				String str_descr = tableNode.getAttributeValue("descr"); // ˵��
				String str_pkname = tableNode.getAttributeValue("pkname"); // �����ֶ���
				al_tmp.add(new String[] { str_name, str_descr, str_pkname }); // ��������Ҫ�������ֶ���
			}
			ins.close(); //
		}
		return al_tmp; //
	}

	// ȡ��XML�������е�˵��!�����ڴ���ģ�����������������е�����˵��,��Ϊ��XML���Ѿ�����,û��Ҫ��¼һ��! �Ӷ�������Ч��!!
	public String[][] getAllIntallTabColumnsDescr() throws Exception {
		return getAllIntallTabColumnsDescr(null); //
	}

	public String[][] getAllIntallTabColumnsDescr(String _tabName) throws Exception {
		String[][] str_packages = new BSUtil().getAllInstallPackages("/database"); //
		ArrayList al_tmp = new ArrayList(); //
		ArrayList onetable_tmp = new ArrayList(); // �������_tabName��Ϊ�գ����ش˱�Ľṹ.haoming2013-7-4
		boolean isFinded = false; //
		for (int i = 0; i < str_packages.length; i++) { //
			String str_xmlfileName = str_packages[i][0] + "tables.xml"; //
			InputStream ins = this.getClass().getResourceAsStream(str_xmlfileName); //
			if (ins != null) { // �����ȷ������ļ�!
				org.jdom.Document doc = new org.jdom.input.SAXBuilder().build(ins); // ����XML
				java.util.List list_tables = doc.getRootElement().getChildren("table");
				for (int j = 0; j < list_tables.size(); j++) { // �������б�
					onetable_tmp = new ArrayList();
					org.jdom.Element tableNode = (org.jdom.Element) list_tables.get(j);
					String str_tabname = tableNode.getAttributeValue("name"); // ����
					java.util.List list_cols = ((org.jdom.Element) tableNode.getChildren("columns").get(0)).getChildren("col"); // ȡ��������!!
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
		if (_tabName != null && !_tabName.equals("") && !isFinded) { // ���û���ҵ���������Ϣ������null
			return null;
		}
		String[][] str_return = null;
		if (isFinded) { // ����ҵ�����onetable�ļ��뷵�ؼ��ɡ�haoming 2013-7-4
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

	// ȡ��������Ҫ��װ�����˵��,һ����¼��һ��HashVO
	public HashVO[] getAllIntallTabColumnsAsHashVO(boolean _isOnlyRefField) throws Exception {
		// ��Ҫ���л��洦��!!

		ArrayList al_hvs = new ArrayList(); //
		String[][] str_packages = new BSUtil().getAllInstallPackages("/database"); //
		for (int i = 0; i < str_packages.length; i++) { //
			String str_xmlfileName = str_packages[i][0] + "tables.xml"; //
			InputStream ins = this.getClass().getResourceAsStream(str_xmlfileName); //
			if (ins != null) { // �����ȷ������ļ�!
				org.jdom.Document doc = new org.jdom.input.SAXBuilder().build(ins); // ����XML
				java.util.List list_tables = doc.getRootElement().getChildren("table"); // ���б��嵥
				for (int j = 0; j < list_tables.size(); j++) { // �������б�
					org.jdom.Element tableNode = (org.jdom.Element) list_tables.get(j); //
					String str_tabname = tableNode.getAttributeValue("name"); // ����
					java.util.List list_cols = ((org.jdom.Element) tableNode.getChildren("columns").get(0)).getChildren("col"); // ȡ��������!!
					for (int k = 0; k < list_cols.size(); k++) { // ��������
						HashVO hvo = new HashVO(); //
						hvo.setAttributeValue("tabname", str_tabname); //
						org.jdom.Element colNode = (org.jdom.Element) list_cols.get(k); //
						java.util.List list_colattrs = colNode.getAttributes(); // ��������!!
						for (int r = 0; r < list_colattrs.size(); r++) { // ������������!!
							org.jdom.Attribute attribute = (org.jdom.Attribute) list_colattrs.get(r); // ȡ���������!!
							String str_attrname = attribute.getName(); // ��������!
							String str_attrvalue = attribute.getValue(); // ����ֵ!!
							hvo.setAttributeValue(str_attrname, str_attrvalue); // ��������ֵ!!
						} // end for [r]
						if (_isOnlyRefField) { // ���ָ����ֻ�����reffield��
							if (hvo.containsKeyIgnoreCasel("reffield")) {
								al_hvs.add(hvo); //
							}
						} else {
							al_hvs.add(hvo); //
						}
					} // end for [k]
				} // end for ��j��
			}
		}
		HashVO[] hvs = (HashVO[]) al_hvs.toArray(new HashVO[0]); // ǿת!!
		return hvs; // ����!!
	}

	public HashVO[] getInstallTempletsAsHashVO(String[] _templetCode) throws Exception {
		String[][] str_packages = new BSUtil().getAllInstallPackages("/templetdata"); // ��ȡ�����а���
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

	// ����ģ�����ȡ��ģ��������HashVO��ʽ����!!
	public HashVO getInstallTempletAsHashVO(String[][] _allPackages, String _templetCode) throws Exception {
		String[][] str_packages = null; //
		if (_allPackages == null) {
			str_packages = new BSUtil().getAllInstallPackages("/templetdata"); // ��ȡ�����а���
		} else {
			str_packages = _allPackages; //
		}
		for (int i = 0; i < str_packages.length; i++) {
			String str_xmlfileName = str_packages[i][0] + _templetCode + ".xml"; // xml�ļ�ȫ��!!
			InputStream ins = this.getClass().getResourceAsStream(str_xmlfileName); //
			if (ins != null) { // ����Ҫ������ļ�!!
				org.jdom.Document doc = new org.jdom.input.SAXBuilder().build(ins); // ����XML
				java.util.List list_tables = doc.getRootElement().getChildren("record"); // ���б��嵥
				org.jdom.Element tableNode = (org.jdom.Element) list_tables.get(0); // ��һ�����������!!
				java.util.List list_cols = tableNode.getChildren("col"); // ȡ��������!!
				HashVO hvo = new HashVO(); //
				for (int k = 0; k < list_cols.size(); k++) { // ��������
					org.jdom.Element colNode = (org.jdom.Element) list_cols.get(k); //
					String str_colName = colNode.getAttributeValue("name"); // ����!!!!
					String str_colValue = colNode.getValue(); // �е�ֵ!!!
					hvo.setAttributeValue(str_colName, str_colValue); // ����!!
				}
				return hvo; //
			}
		}
		return null; //
	}

	/**
	 * ����ָ����xmlǰ�ȥ�ҵ���Ӧ��xml�ļ��е�ĳ������,Ȼ��̬����SQL,Ȼ��ִ��֮!!!
	 * 
	 * @param _package_prefix
	 * @param _tabName
	 * @return
	 * @throws Exception
	 */
	public String createTableByPackagePrefix(String _package_prefix, String _tabName) throws Exception {
		try {
			TBUtil tbUtil = new TBUtil(); //
			String str_xmlfileName = tbUtil.replaceAll(_package_prefix, ".", "/"); // ���滻!!!
			if (!str_xmlfileName.startsWith("/")) {
				str_xmlfileName = "/" + str_xmlfileName; //
			}
			if (!str_xmlfileName.endsWith("/")) {
				str_xmlfileName = str_xmlfileName + "/"; //
			}
			str_xmlfileName = str_xmlfileName + "database/tables.xml"; //

			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
			org.jdom.Document doc = builder.build(this.getClass().getResourceAsStream(str_xmlfileName)); // ����XML
			java.util.List list_tables = doc.getRootElement().getChildren("table");
			for (int i = 0; i < list_tables.size(); i++) {
				org.jdom.Element param = (org.jdom.Element) list_tables.get(i);
				String str_tname = param.getAttributeValue("name"); // ����
				String str_pkName = param.getAttributeValue("pkname"); // �����ֶ���!!
				if ("Y".equals(param.getAttributeValue("ignore"))) {
					continue;
				}
				if (_tabName.equalsIgnoreCase(str_tname)) { // ����ҵ���!!
					CommDMO commDMO = new CommDMO();
					// try {
					// commDMO.executeUpdateByDS(null, "drop table " + _tabName); //
					// } catch (Exception ex) {
					// System.err.println("ɾ����[" + _tabName + "]ʧ��!"); //
					// }
					String str_dbtype = ServerEnvironment.getDefaultDataSourceType(); // ����Դ����!�ؼ�
					java.util.List list_cols = ((org.jdom.Element) param.getChildren("columns").get(0)).getChildren("col"); // ȡ��������!!
					StringBuilder sb_sql = new StringBuilder(); //
					sb_sql.append("create table " + str_tname + "("); //
					for (int j = 0; j < list_cols.size(); j++) {
						org.jdom.Element colNode = (org.jdom.Element) list_cols.get(j); //
						String str_colName = colNode.getAttributeValue("name"); //
						String str_colType = colNode.getAttributeValue("type"); // ����,����varchar,decimal
						String str_colLength = colNode.getAttributeValue("length"); //
						sb_sql.append(str_colName + " " + convertRealColType(str_colType, str_dbtype) + "(" + str_colLength + ")"); //
						if (str_colName.equalsIgnoreCase(str_pkName)) { // ���������,�������not null
							sb_sql.append(" NOT NULL"); // //
						}
						if (j != list_cols.size() - 1) { // ���û�ж�������,���������һ��
							sb_sql.append(","); // //
						} else {
							if (str_pkName != null && !str_pkName.trim().equals("")) { // ��������һ��,��������Ϊ��,�����滹��������
								sb_sql.append(","); // //
							}
						}
					}
					// ������
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

					// ��������!!!
					java.util.List list_allindexs = param.getChildren("indexs"); //
					if (list_allindexs != null && list_allindexs.size() > 0) { // ����û��������
						java.util.List list_indexs = ((org.jdom.Element) list_allindexs.get(0)).getChildren("index"); // ȡ��������!!
						if (list_indexs != null && list_indexs.size() > 0) {
							for (int j = 0; j < list_indexs.size(); j++) {
								org.jdom.Element indexNode = (org.jdom.Element) list_indexs.get(j); //
								String str_indexName = indexNode.getAttributeValue("name"); //
								String str_indexCols = indexNode.getAttributeValue("cols"); //
								String str_indexsql = "create index " + str_indexName + " on " + str_tname + "(" + str_indexCols + ")"; //
								str_indexsql = str_indexsql.trim(); //
								if (str_dbtype.equalsIgnoreCase("DB2")) {
									str_indexsql = str_indexsql.toUpperCase(); // ת��д!!
								}
								commDMO.executeUpdateByDS(null, str_indexsql); //
							}
						}
					}
					return "�ɹ�"; //
				}

			} // end for i
			return "��xml�ļ���û���ҵ������!"; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return "��װʧ��,ԭ��:" + ex.getMessage(); //
		}
	}

	/**
	 * ȡ��������ͼ�������嵥!!
	 * 
	 * @param _package_prefix
	 * @return
	 * @throws Exception
	 */
	public String[] getAllIntallViewsByPackagePrefix(String _package_prefix) throws Exception {
		return getInstallListByPackagePrefix(_package_prefix, "views.xml", "view", "name"); //
	}

	/**
	 * ����ָ����xmlǰ�ȥ�ҵ���Ӧ��xml�ļ��е�ĳ������,Ȼ��̬����SQL,Ȼ��ִ��֮!!!
	 * 
	 * @param _package_prefix
	 * @param _tabName
	 * @return
	 * @throws Exception
	 */
	public String createViewByPackagePrefix(String _package_prefix, String _viewName) throws Exception {
		try {
			TBUtil tbUtil = new TBUtil(); //
			String str_xmlfileName = tbUtil.replaceAll(_package_prefix, ".", "/"); // ���滻!!!
			if (!str_xmlfileName.startsWith("/")) {
				str_xmlfileName = "/" + str_xmlfileName; //
			}
			if (!str_xmlfileName.endsWith("/")) {
				str_xmlfileName = str_xmlfileName + "/"; //
			}
			str_xmlfileName = str_xmlfileName + "database/views.xml"; //

			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
			org.jdom.Document doc = builder.build(this.getClass().getResourceAsStream(str_xmlfileName)); // ����XML
			java.util.List list_tables = doc.getRootElement().getChildren("view");
			for (int i = 0; i < list_tables.size(); i++) {
				org.jdom.Element param = (org.jdom.Element) list_tables.get(i);
				String str_viewname = param.getAttributeValue("name"); // ����
				if (_viewName.equalsIgnoreCase(str_viewname)) { // ����ҵ���!!
					CommDMO commDMO = new CommDMO();
					// try {
					// commDMO.executeUpdateByDS(null, "drop view " + _viewName); //
					// } catch (Exception ex) {
					// System.err.println("ɾ����ͼ[" + _viewName + "]ʧ��!"); //
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
					return "�ɹ�"; //
				}
			}
			return "��xml�ļ���û���ҵ������ͼ!"; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return "��װʧ��,ԭ��:" + ex.getMessage(); //
		}
	}

	/**
	 * ȡ��������Ҫ��ʼ���ı���嵥!!!
	 * 
	 * @param _package_prefix
	 * @return
	 * @throws Exception
	 */
	public String[] getAllIntallInitDataByPackagePrefix(String _package_prefix, String _xtdatadir) throws Exception {
		String str_xmlfileName = tbUtil.replaceAll(_package_prefix, ".", "/"); // ���滻!!!
		if (!str_xmlfileName.startsWith("/")) {
			str_xmlfileName = "/" + str_xmlfileName; //
		}
		if (!str_xmlfileName.endsWith("/")) {
			str_xmlfileName = str_xmlfileName + "/"; //
		}
		str_xmlfileName = str_xmlfileName + "xtdata/"; //
		if (_xtdatadir != null && !_xtdatadir.trim().equals("")) { // �����װ������ָ������Ŀ¼!!
			str_xmlfileName = str_xmlfileName + _xtdatadir; // ������Ŀ¼,����hegui
			if (!str_xmlfileName.endsWith("/")) {
				str_xmlfileName = str_xmlfileName + "/"; //
			}
		}
		str_xmlfileName = str_xmlfileName + "_filelist.ini"; //
		return getFileList(str_xmlfileName); //
	}

	/**
	 * ȡ���ļ�_filelist.ini�е������ļ����б�!
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
		String str_text = tbUtil.readFromInputStreamToStr(ins); // ���ļ�!!�������Զ��ر����ļ���!!!
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
	 * �����ʼ������
	 * 
	 * @param _package_prefix
	 * @param _viewName
	 * @return
	 * @throws Exception
	 */
	public String InsertInitDataByPackagePrefix(String _package_prefix, String _xtdatadir, String _fileName) throws Exception {
		try {
			String str_xmlfileName = tbUtil.replaceAll(_package_prefix, ".", "/"); // ���滻!!!
			if (!str_xmlfileName.startsWith("/")) {
				str_xmlfileName = "/" + str_xmlfileName; //
			}
			if (!str_xmlfileName.endsWith("/")) {
				str_xmlfileName = str_xmlfileName + "/"; //
			}
			str_xmlfileName = str_xmlfileName + "xtdata/";
			if (_xtdatadir != null && !_xtdatadir.trim().equals("")) { // �����װ������ָ������Ŀ¼!!
				str_xmlfileName = str_xmlfileName + _xtdatadir; // ������Ŀ¼,����hegui
				if (!str_xmlfileName.endsWith("/")) {
					str_xmlfileName = str_xmlfileName + "/"; //
				}
			}
			str_xmlfileName = str_xmlfileName + _fileName; //
			InputStream ins = this.getClass().getResourceAsStream(str_xmlfileName); // ȡ���ļ���!!!
			if (ins == null) {
				return "û��ȡ���ļ�[" + str_xmlfileName + "]"; //
			}
			String str_xmlText = tbUtil.readFromInputStreamToStr(ins); // ȡ���ļ�����
			new MetaDataDMO().importXmlToTable1000Records(null, _fileName, str_xmlText); // ������һ������!!!
			return "�ɹ�!"; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return "��װʧ��,ԭ��:" + ex.getMessage(); //
		}
	}

	/**
	 * ȡ�õ������İ�װ���ݰ�!�������!!!���������Ϊ̫��,���Ե��������һ�������չ���ݰ�װ,��lib/�µ�����һ����氲װ��,Ȼ����RegisterMenu��ע����ext3data ��xch/2012-03-12��
	 * 
	 * @param _package_prefix
	 * @return
	 * @throws Exception
	 */
	public String[][] getExt3DataXmlFiles(String _package_prefix) throws Exception {
		String str_xmlfileName = tbUtil.replaceAll(_package_prefix, ".", "/"); // ���滻!!!
		if (!str_xmlfileName.startsWith("/")) {
			str_xmlfileName = "/" + str_xmlfileName; //
		}
		if (!str_xmlfileName.endsWith("/")) {
			str_xmlfileName = str_xmlfileName + "/"; //
		}
		org.jdom.input.SAXBuilder sax1 = new org.jdom.input.SAXBuilder(); //
		MetaDataDMO metaDMO = new MetaDataDMO(); //
		InputStream ins_regxml = this.getClass().getResourceAsStream(str_xmlfileName + "RegisterMenu.xml"); //
		org.jdom.Document doc = sax1.build(ins_regxml); // ����XML
		ins_regxml.close(); //
		java.util.List list_extNodes = doc.getRootElement().getChildren("ext3data"); //
		if (list_extNodes.size() > 0) { //
			ArrayList al_fileNames = new ArrayList(); //
			for (int i = 0; i < list_extNodes.size(); i++) { // ����!!!
				org.jdom.Element itemNode = (org.jdom.Element) list_extNodes.get(i); //
				String str_nodeName = itemNode.getAttributeValue("name"); // ����
				String str_package = itemNode.getAttributeValue("package"); // ����ǰ�
				str_package = tbUtil.replaceAll(str_package, ".", "/"); // ���滻!!!
				if (!str_package.startsWith("/")) {
					str_package = "/" + str_package; //
				}
				if (!str_package.endsWith("/")) {
					str_package = str_package + "/"; //
				}
				String[] str_dataxmls = getFileList(str_package + "_filelist.ini"); // ȡ���ļ��б��ж����xml�����ļ���!!!
				if (str_dataxmls == null) {
					return new String[][] { { "û�ж����ļ�[" + str_package + "_filelist.ini]", null } };
				}
				for (int j = 0; j < str_dataxmls.length; j++) { // ������װxml
					al_fileNames.add(new String[] { str_nodeName, str_package + str_dataxmls[j] }); // ������ʵ��xml·��
				}
			}
			return (String[][]) al_fileNames.toArray(new String[][] { {} }); // ����!!
		} else {
			return new String[][] { { "û��ע�ᶨ����Ҫ��װ����չ����!", null } }; //
		}
	}

	/**
	 * ��װ���������ݰ�!!
	 * 
	 * @param _xmlFileName
	 * @return
	 * @throws Exception
	 */
	public String installExt3Data(String _xmlFileName) throws Exception {
		InputStream ins_xml = this.getClass().getResourceAsStream(_xmlFileName); // ȡ���ļ���!!!
		if (ins_xml != null) { // ��������XML�ļ�!
			String str_xmlText = tbUtil.readFromInputStreamToStr(ins_xml); // ȡ���ļ�����
			new MetaDataDMO().importXmlToTable1000Records(null, _xmlFileName, str_xmlText); // �������XML�е�����!!!
			ins_xml.close(); // �ر���!!
			return "�ɹ�";
		} else {
			return "�ļ�������";
		}

	}

	/**
	 * Ϊȡ�ñ�,��ͼ,��ʼ�����������嵥�����÷���!!
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
		if (!str_xmlfileName.endsWith("/")) { // ���������/��β,�����!!
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
		org.jdom.Document doc = builder.build(fileIns); // ����XML
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
		if (_type.equalsIgnoreCase("decimal") || _type.equalsIgnoreCase("number")) { // �������������
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

	// ȡ�����м���ɾ������������VO
	public HashVO[] getAllCascadeRefFieldHVO() throws Exception {
		if (ServerEnvironment.allCascadeRefFieldHVO != null) { // �����洦��!!
			return ServerEnvironment.allCascadeRefFieldHVO; //
		}
		ServerEnvironment.allCascadeRefFieldHVO = getAllIntallTabColumnsAsHashVO(true); // ȡ��������!!!
		return ServerEnvironment.allCascadeRefFieldHVO; //
	}

	// ���ݻ������ɾ������,�����������֮�����������ӱ�ļ���ɾ���Ķ���!!
	public String[] getCascadeDeleteSQL(String _table, String _field, String _value) throws Exception {
		HashVO[] hvs_allcols = getAllCascadeRefFieldHVO(); //
		ArrayList al_sqls = new ArrayList(); //
		for (int i = 0; i < hvs_allcols.length; i++) { // ����������!!
			String str_reffield = hvs_allcols[i].getStringValue("reffield"); // �����ֶ�
			if (str_reffield != null && str_reffield.equalsIgnoreCase(_table + "." + _field)) { // ��������Ǳ��������ݱ���ֶ���!
				String str_tabname = hvs_allcols[i].getStringValue("tabname"); // ҵ�����!
				String str_colname = hvs_allcols[i].getStringValue("name"); // ҵ����ֶ���!
				String str_isMulti = hvs_allcols[i].getStringValue("ismulti"); //
				if (str_isMulti == null || str_isMulti.trim().equals("") || !tbUtil.isExistInArray(str_isMulti, new String[] { "N", "N1", "N2", "Y" })) { // ���Ϊ��,���߲����ĸ��ַ�����֮һ!!!
					str_isMulti = "N1"; //
				}
				String str_sql = null; //
				if (str_isMulti.equals("Y")) { // ����Ƕ�ѡ,��ǿ����update
					str_sql = "update " + str_tabname + " set " + str_colname + "=replace(" + str_colname + ",';" + _value + ";',';') where " + str_colname + " like '%;" + _value + ";%'"; // ƴ��SQL!
				} else if (str_isMulti.equals("N") || str_isMulti.equals("N1")) { // ����ǵ�ѡ,�����Ǽ���ɾ��!��ƴ��Delete
					str_sql = "delete from " + str_tabname + " where " + str_colname + "='" + _value + "'"; // ƴ��SQL!
				} else if (str_isMulti.equals("N2")) {
					str_sql = "update " + str_tabname + " set " + str_colname + "=null where " + str_colname + "='" + _value + "'"; // ƴ��SQL!
				}
				if (str_sql != null) {
					al_sqls.add(str_sql); // //
				}
			}
		}
		return (String[]) al_sqls.toArray(new String[0]); //
	}

	// ���ݻ�������޸Ķ���,�����޸������ӱ��SQL
	public String[] getCascadeUpdateSQL(String _table, String _field, String _oldvalue, String _newValue) throws Exception {
		HashVO[] hvs_allcols = getAllCascadeRefFieldHVO(); //
		ArrayList al_sqls = new ArrayList(); //
		for (int i = 0; i < hvs_allcols.length; i++) { // ����������!!
			String str_reffield = hvs_allcols[i].getStringValue("reffield"); // �����ֶ�
			if (str_reffield != null && str_reffield.equalsIgnoreCase(_table + "." + _field)) { // ��������Ǳ��������ݱ���ֶ���!
				String str_tabname = hvs_allcols[i].getStringValue("tabname"); // ҵ�����!
				String str_colname = hvs_allcols[i].getStringValue("name"); // ҵ����ֶ���!
				String str_isMulti = hvs_allcols[i].getStringValue("ismulti"); //
				if (str_isMulti == null || str_isMulti.trim().equals("") || !tbUtil.isExistInArray(str_isMulti, new String[] { "N", "N1", "N2", "Y" })) { // ���Ϊ��,���߲����ĸ��ַ�����֮һ!!!
					str_isMulti = "N1"; //
				}
				String str_sql = null; //
				if (str_isMulti.equals("Y")) { // ����Ƕ�ѡ,��ǿ����update
					str_sql = "update " + str_tabname + " set " + str_colname + "=replace(" + str_colname + ",';" + _oldvalue + ";',';" + _newValue + ";') where " + str_colname + " like '%;" + _oldvalue + ";%'"; // ƴ��SQL!
				} else if (str_isMulti.equals("N") || str_isMulti.equals("N1") || str_isMulti.equals("N2")) { // ����ǵ�ѡ,��ֱ���޸ĳ���ֵ!!!
					str_sql = "update " + str_tabname + " set " + str_colname + "='" + _newValue + "' where " + str_colname + "='" + _oldvalue + "'"; // ƴ��SQL!
				}
				if (str_sql != null) {
					al_sqls.add(str_sql); // //
				}
			}
		}
		return (String[]) al_sqls.toArray(new String[0]); //
	}

	// ȡ�ü���ɾ���ľ���SQL,���Զ������й�ϵʱ�ĸ����ֹ������ϵ�SQL
	public String[] getCascadeWarnSQL(boolean _isPreSelect) throws Exception {
		ArrayList al_sqls = new ArrayList(); //
		HashVO[] hvs_allcols = getAllCascadeRefFieldHVO(); //
		for (int i = 0; i < hvs_allcols.length; i++) { // ����������!!
			String str_reffield = hvs_allcols[i].getStringValue("reffield"); // �����ֶ�
			if (str_reffield != null) { // ��������Ǳ��������ݱ���ֶ���!
				if (!str_reffield.contains(".")) {// ������ô��ˣ�û�е�ţ������ѭ�����������ᱨ�����/2012-08-01��
					continue;
				}
				String str_base_tabname = str_reffield.substring(0, str_reffield.indexOf(".")); // ������ı���!!
				String str_base_colname = str_reffield.substring(str_reffield.indexOf(".") + 1, str_reffield.length()); // �����������!!
				String str_tabname = hvs_allcols[i].getStringValue("tabname"); // ҵ�����!
				String str_colname = hvs_allcols[i].getStringValue("name"); // ҵ����ֶ���!
				String str_isMulti = hvs_allcols[i].getStringValue("ismulti"); //
				String str_sql = null; //
				if (str_isMulti == null || str_isMulti.trim().equals("") || str_isMulti.startsWith("N")) { // ����ǵ�ѡ
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
	 * ȡ������ע��Ĳ˵�,����ƽ̨ȡ,�ټ����� INSTALLAPPS �����Ӷ�Ӧ�ĸ�������ȡ!!! ���շ��� ����,�ļ���,·����,����Ҫ����·����!!!
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
				al_return.addAll(al_menu); // ȡ�ò˵���Դ����!!!
			}
		}
		return al_return; // ����!!!
	}

	public ArrayList getRegistMenuFromXML(String _xmlFile) throws Exception {
		try {
			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
			InputStream ins = this.getClass().getResourceAsStream(_xmlFile); //
			if (ins == null) {
				System.err.println("�ļ�[" + _xmlFile + "]������!!!"); //
				return null;
			}
			org.jdom.Document doc = builder.build(ins); // ����XML
			java.util.List list_tables = doc.getRootElement().getChildren("menu"); // �ҵ����в˵�!!
			ArrayList al_return = new ArrayList(); //
			for (int i = 0; i < list_tables.size(); i++) {
				org.jdom.Element param = (org.jdom.Element) list_tables.get(i); //
				String str_menuName = param.getAttributeValue("name"); //
				String str_menuCommand = param.getAttributeValue("command"); //
				String str_menuDescr = param.getAttributeValue("descr"); //
				String str_menuConf = param.getAttributeValue("conf"); //
				String str_menuicon = param.getAttributeValue("icon"); // ��ȡ��xml��ͼ������
				String str_menucommandtype = param.getAttributeValue("commandtype");
				al_return.add(new String[] { _xmlFile, str_menuName, str_menuCommand, str_menuDescr, str_menuConf, str_menuicon, str_menucommandtype }); //
			}
			return al_return; //
		} catch (Exception ex) {
			System.err.println("����XML�ļ�[" + _xmlFile + "]�����쳣:[" + ex.getMessage() + "]"); //
			throw null;
		}
	}

	/**
	 * ȡ��ĳ��ע��˵����������!!!
	 * 
	 * @param _xmlFile
	 * @param _menuName
	 * @return
	 * @throws Exception
	 */
	public String[] getOneRegMenuCommand(String _xmlFile, String _menuName) throws Exception {
		org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
		org.jdom.Document doc = builder.build(this.getClass().getResourceAsStream(_xmlFile)); // ����XML
		java.util.List list_tables = doc.getRootElement().getChildren("menu"); // �ҵ����в˵�!!
		for (int i = 0; i < list_tables.size(); i++) {
			org.jdom.Element param = (org.jdom.Element) list_tables.get(i); //
			String str_menuName = param.getAttributeValue("name"); //
			if (str_menuName.equals(_menuName)) {
				return new String[] { param.getAttributeValue("command"), param.getAttributeValue("conf"), param.getAttributeValue("commandtype") }; // ����������!!!
			}
		}
		return null; //
	}

}
