package cn.com.infostrategy.bs.sysapp.install.quickInstall;

import java.io.File;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.BSUtil;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.common.ServicePoolFactory;
import cn.com.infostrategy.bs.common.ServicePoolableObjectFactory;
import cn.com.infostrategy.bs.sysapp.install.InstallDMO;
import cn.com.infostrategy.bs.sysapp.install.database.DataBaseUtilDMO;
import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.HashVOStruct;
import cn.com.infostrategy.to.common.Queue;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.DeleteSQLBuilder;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;

public class QuickInstallDMO extends AbstractDMO {
	private InstallDMO dmo = null;
	private CommDMO commDMO = null;
	private DataBaseUtilDMO dataUtilDMO = null;
	private static Queue schedule = new Queue(); //���������¼��ǰִ�н��ȣ�ui���ڵ����װ�������������󣬻᲻ͣ�ĵ��ø����ݡ�
	private TBUtil tbutil = TBUtil.getTBUtil();
	public static String MODULE_INSTALL_DEAL_TYPE_VALIDATE = "validate";
	public static String MODULE_INSTALL_DEAL_TYPE_UPDATE = "update";
	public static String MODULE_INSTALL_DEAL_TYPE_INSTALL = "install";
	private static QuickInstallDMO installDMO = new QuickInstallDMO();
	private Logger logger = WLTLogger.getLogger(CommDMO.class); //
	private HashMap alltabname_key = new HashMap(); // ���б���-����,ȫ���õ���
	private HashMap installingDataHis = new HashMap(); //��¼�������ݿ����ʷ��

	//�ҿ��԰�װ�İ�����������setting.xml�ļ�,�ļ���Ҫ����������չ�á���ʵ�ϲ���table.xml���������ļ���,���ǿ��Եġ�����ƫ�á�
	public HashVO[] getAllInstallModuleStatus() throws Exception {
		String[][] modules = new BSUtil().getAllInstallPackages(null);
		if (modules == null || modules.length == 0) {
			return null;
		}
		List moduleList = new ArrayList();
		HashVO logs[] = null; //�Ѿ���װ����������װ��ʷ��
		try {
			logs = getCommDMO().getHashVoArrayByDS(null, "select * from pub_install_update_log"); //
		} catch (Exception ex) {
			if (notExist(ex)) { //204��db2�Ĵ������( �����Ķ���δ��DB2�ж���)
				System.out.println("��pub_install_update_log�������ڣ�ϵͳ�Զ�����");
				getInstallDMO().createTableByPackagePrefix("/cn/com/infostrategy/bs/sysapp/install", "pub_install_update_log");
				logs = new HashVO[0];
			} else {
				throw ex;
			}
		}
		for (int i = 0; i < modules.length; i++) {
			InputStream input = this.getClass().getResourceAsStream(modules[i][0] + "setting.xml"); //��ȡ�ļ�
			if (input != null) { //����ļ�ʵ�ʴ���
				org.jdom.Document doc = new org.jdom.input.SAXBuilder().build(input); // ����XML.
				Element rootNode = doc.getRootElement();
				HashVO module = getXmlNodeAttributeByKeys(rootNode, "name", "code", "sn", "icon");
				List list = doc.getRootElement().getChildren("install");
				boolean validated = false; //У���
				boolean installed = false;//��װ��
				boolean update_do = false;//�Ƿ���Ը���
				String lastVersion = "0";
				List upVersionList = new ArrayList(); //�õ��Ƚϴ�İ汾��
				if (list.size() > 0) {
					Element installNode = (Element) list.get(0);
					HashVO install = getXmlNodeAttributeByKeys(installNode, "version", "date");
					module.setAttributeValue("version", install.getStringValue("version"));
					module.setAttributeValue("date", install.getStringValue("date"));
					module.setAttributeValue("id", moduleList.size()); //����һ�����
					module.setAttributeValue("packagepath", modules[i][0]);
					//�жϸ�ģ���ִ�е�״̬����װ����Ȩ������...�ȵȡ�

					//��һ�����ж���û����Ȩ�ɹ�����
					for (int j = 0; j < logs.length; j++) {
						String code = logs[j].getStringValue("moduleCode"); //ģ�����
						String operateType = logs[j].getStringValue("operateType");//��������install,update,uninstall,validate
						String result = logs[j].getStringValue("result");//��װ�������Ȼ��� Y��N
						String version = logs[j].getStringValue("version"); //�汾
						String input_msg = logs[j].getStringValue("input"); //����

						if (tbutil.isEmpty(code) || tbutil.isEmpty(operateType) || tbutil.isEmpty(result) || tbutil.isEmpty(version)) {
							throw new WLTAppException("ϵͳ��⵽��װ����־���۸�");
						}
						if (code.equals(module.getStringValue("code"))) { //�ҵ��˸�ģ��ļ�¼
							if (operateType.equals("validate")) { //Ч�����ʷ
								if ("Y".equals(result)) { //
									validated = true;
								}
							} else if (operateType.equals("install")) { //��װ����ʷ
								if ("Y".equals(result)) { //��װ�ɹ�
									lastVersion = version;
									installed = true;
								}
							} else if (operateType.equals("update")) { //��������ʷ��
								if ("Y".equals(result)) { //�����ɹ�
									if (lastVersion != null && Float.parseFloat(lastVersion) < Float.parseFloat(version)) {
										lastVersion = version; //�õ����汾���п����������ģ��п����ǰ�װ�ġ�
									}
								}
							}
						}
					}
					if (!validated && "FLAT".equals(module.getStringValue("code"))) {
						validated = true;
					}
					if (!installed && "FLAT".equals(module.getStringValue("code"))) { //���û�а�װ��ƽ̨����Ҫ��ȷ�ж�һ�¡���ѯpub_menu�Ƿ񴴽�������
						TableDataStruct str = null;
						try {
							str = getCommDMO().getTableDataStructByDS(null, "select * from pub_menu");
						} catch (Exception ex) {
							str = null;
						}
						if (str != null) { //�����װ���˱�˵�����ڡ�
							installed = true;
						}
					}
					if (!lastVersion.equals("") && !"0".equals(lastVersion)) {
						module.setAttributeValue("version", lastVersion);
					}
					//					if (Float.parseFloat(lastVersion) < Float.parseFloat(module.getStringValue("version"))) {
					//						update_do = true;
					//						upVersionList.add(module.getStringValue("version")); //��汾���Ȳ����룬Ĭ�Ϲ涨��ֻҪ����ִ��һЩ�ű����ͱ��뷢��һ���汾��ֻ����ı䶯�����Բ������汾��
					//					}
					//����ж��ⰲװ�أ�������Щϵͳ���ð�װ��ֱ�Ӹ��¾Ϳ����ˡ�
				}
				if (installed) { //����Ѿ���װ������Ҫ�ж��Ƿ����
					Element ele = rootNode.getChild("updates"); //�ҵ����½ڵ���
					if (ele != null) {
						List update = ele.getChildren("update"); //�õ����п����������ݡ�
						for (int j = 0; j < update.size(); j++) {
							Element updateNode = (Element) update.get(j); //�õ�һ���������������ݡ�
							String version = updateNode.getAttributeValue("version");
							if (version != null && Float.parseFloat(lastVersion) < Float.parseFloat(version)) { //�Ƚ������ļ��еĺ�������ʷ�Ƚϡ�
								update_do = true;
								upVersionList.add(version); //�ѿ��������İ汾���롣
							}
						}
					}
					if (list.size() > 0 && lastVersion.equals("0")) {
						module.setAttributeValue("version", "-1");
					}
				}

				if (update_do) { //������Ը��£�����Ҫ�Ƿ����¸���
					module.setAttributeValue("control", WLTConstants.MODULE_INSTALL_STATUS_KSJ); //�Ѿ���װ������
					module.setAttributeValue("updates", upVersionList);
				} else if (!update_do && installed) {
					module.setAttributeValue("control", WLTConstants.MODULE_INSTALL_STATUS_YAZ); //�Ѿ���װ��������
				} else if (!update_do && !installed && validated) {
					module.setAttributeValue("control", WLTConstants.MODULE_INSTALL_STATUS_KAZ); //���԰�װ
					Element license = doc.getRootElement().getChild("license"); //��Ȩ
					if (license != null) {
						String lin_text = license.getValue();
						module.setAttributeValue("license", lin_text);
					}
				} else if (!update_do && !installed && !validated) {
					//module.setAttributeValue("control", WLTConstants.MODULE_INSTALL_STATUS_WSQ); //δ��Ȩ
					module.setAttributeValue("control", WLTConstants.MODULE_INSTALL_STATUS_KAZ); //�Ȳ�Ч���ˣ� �Ժ���˵ Gwang-2015-11-29
					//��ʾ���°�װ�汾��
				}

				if (!update_do && !installed && !validated && list.size() == 0)
					continue;
				moduleList.add(module);
			}
		}
		return (HashVO[]) moduleList.toArray(new HashVO[0]);
	}

	//����Ƿ���Խ��в���
	public boolean checkCanOperate(HashVO _selectVO) {
		boolean flag = false;
		if (_selectVO != null) {
			String type = _selectVO.getStringValue("type"); //type=install��װ,update����,anthorize��Ȩ...
			String packagepath = _selectVO.getStringValue("packagepath");
			String version = _selectVO.getStringValue("version");
		}
		return flag;
	}

	//Զ�̵��õ�ͳһ�ж���ڣ���װ��������ж�صȡ�
	public String installOrUpdateOperateAction(HashVO _install_updateConfig, String _operateType) throws Exception {
		schedule = new Queue();
		installingDataHis = new HashMap(); //���û���
		alltabname_key = new HashMap();
		boolean flag = false;
		if (MODULE_INSTALL_DEAL_TYPE_INSTALL.equalsIgnoreCase(_operateType)) { //��װ
			flag = installModuleAction(_install_updateConfig);
		} else if (MODULE_INSTALL_DEAL_TYPE_UPDATE.equals(_operateType)) {//����
			flag = updateModuleAction(_install_updateConfig);
		} else if (MODULE_INSTALL_DEAL_TYPE_VALIDATE.equals(_operateType)) { //��ȨУ��
			flag = validateSN(_install_updateConfig);
		}
		setCurrSchedule(null, 100, 100, 1, 1);
		System.gc();
		return flag ? "success" : "fail";
	}

	private boolean validateSN(HashVO _install_updateConfig) throws Exception {
		
		//�Ȳ�Ч���ˣ� �Ժ���˵ Gwang-2015-11-29
		if (true) {
			return true;
		}
		
		boolean flag = false;
		if (_install_updateConfig != null) {
			String inputsn = _install_updateConfig.getStringValue("sn");
			String code = _install_updateConfig.getStringValue("code");
			String weblightSN = ServerEnvironment.getProperty("SN");
			String validatepwd = weblightSN + code;
			String str_md5 = md5(validatepwd);
			String str_sha = sha(str_md5);
			if (inputsn != null && inputsn.trim().equals(str_sha)) {
				flag = true;
			}
			buildLog(_install_updateConfig, MODULE_INSTALL_DEAL_TYPE_VALIDATE, flag, flag ? "Ч��ɹ������԰�װ" : "��������Ч����", inputsn);
			setCurrSchedule(flag ? "Ч��ɹ������԰�װ" : "��������Ч����,���ʶ��š�" + str_md5 + "������ϵͳ��Ӧ����������֤��");
		}
		return flag;
	}

	private void buildLog(HashVO _install_updateConfig, String _deal_type, boolean flag, String _log, String _input) throws Exception {
		boolean ff = checkTableExistAndCreate("wltdual");
		if (ff) {
			List list = new ArrayList();
			list.add("delete from wltdual");
			list.add("insert into wltdual values('1')");
			setCurrSchedule("�Զ���ʼ��wltdual�ؼ���");
			getCommDMO().executeBatchByDSImmediately(null, list);
		}
		checkTableExistAndCreate("pub_sequence");
		InsertSQLBuilder insql = new InsertSQLBuilder("pub_install_update_log");
		insql.putFieldValue("id", getCommDMO().getSequenceNextValByDS(null, "pub_install_update_log"));
		insql.putFieldValue("moduleCode", _install_updateConfig.getStringValue("code"));
		insql.putFieldValue("modulename", _install_updateConfig.getStringValue("name"));
		insql.putFieldValue("moduleDescr", "ʡ��");
		insql.putFieldValue("operateType", _deal_type);
		insql.putFieldValue("log", _log);
		insql.putFieldValue("userid", getCommDMO().getCurrSession().getLoginUserId());
		insql.putFieldValue("time", tbutil.getCurrTime());
		insql.putFieldValue("result", flag ? "Y" : "N");
		insql.putFieldValue("input", _input);
		insql.putFieldValue("version", _install_updateConfig.getStringValue("version"));
		currExecuteSql(null, insql.getSQL());
	}

	//��װ���� 
	private boolean installModuleAction(HashVO _install_updateConfig) throws Exception {
		try {
			setCurrSchedule("������ʼ��װ");
			String path = _install_updateConfig.getStringValue("packagepath");
			//ִ�б�ṹ
			setCurrSchedule("���ڴ�����:");
			installTable(path);

			//ִ����ͼ
			setCurrSchedule("���ڴ�����ͼ:");
			installView(path);

			//ִ�г�ʼ������
			installData(path);
			//����˵�
			installMenu(path);
			System.gc();
			//on-offִ�У����뵽���ݿ��У��������ظ�ģ�����п�����Ϣ��
			installOff_On(_install_updateConfig);
			//ִ���Զ����෽��			
			executeCustomClass(path);
			buildLog(_install_updateConfig, MODULE_INSTALL_DEAL_TYPE_INSTALL, true, "��װ�ɹ�", "");
			setCurrSchedule("��װ���");
		} catch (Exception e) {
			buildLog(_install_updateConfig, MODULE_INSTALL_DEAL_TYPE_INSTALL, false, "��װʧ��\r\n������Ϣ��" + e.getMessage(), "");
			setCurrSchedule("��װʧ��");
			setCurrSchedule("��ʼ�����Ѱ�װ������");
			String str = unInstall();
			setCurrSchedule(str);
			//��ʼ����SQL
			throw e;
		}
		return true;
	}

	//��������
	private boolean updateModuleAction(HashVO _install_updateConfig) {
		return false;
	}

	protected String installTable(String _package_prefix) throws Exception {
		TBUtil tbUtil = new TBUtil(); //
		String str_xmlfileName = tbUtil.replaceAll(_package_prefix, ".", "/"); //���滻!!!
		if (!str_xmlfileName.startsWith("/")) {
			str_xmlfileName = "/" + str_xmlfileName; //
		}
		if (!str_xmlfileName.endsWith("/")) {
			str_xmlfileName = str_xmlfileName + "/"; //
		}
		str_xmlfileName = str_xmlfileName + "database/tables.xml"; //

		org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
		org.jdom.Document doc = builder.build(this.getClass().getResourceAsStream(str_xmlfileName)); // ����XML
		java.util.List list_tables = doc.getRootElement().getChildren("table"); //�õ���װ���õ����б�
		for (int i = 0; i < list_tables.size(); i++) {
			org.jdom.Element param = (org.jdom.Element) list_tables.get(i);
			String str_tname = param.getAttributeValue("name"); //����
			if ("Y".equals(param.getAttributeValue("ignore"))) {
				continue;
			}
			//����Ѿ����ڸñ�,��Ҫ���б�ṹ�Ƚϡ�
			compareAndDealTableAction(str_tname, str_xmlfileName);
			setCurrSchedule(null, 0, 20, i, list_tables.size());
			if (str_tname.equalsIgnoreCase("wltdual")) { //�������ö�ط����õ���������һ�����ݡ�
				List list = new ArrayList();
				list.add("delete from wltdual");
				list.add("insert into wltdual values('1')");
				setCurrSchedule("�Զ���ʼ��wltdual�ؼ���");
				getCommDMO().executeBatchByDSImmediately(null, list);
			}

		} //end for i
		return "��װ�ɹ�!";
	}

	//
	private void compareAndDealTableAction(String tableName, String str_xmlfileName) throws Exception {
		String compareResult[][] = getDataUtilDMO().compareTableDictByDB(null, tableName, str_xmlfileName); //�����Ƚ�
		if (compareResult == null) { //����һ����ȫ��ͬ�ı�
			setCurrSchedule("��[" + tableName + "]�Ѿ����ڣ����ҽṹһ��");
			return;
		}
		List sqllist = new ArrayList(); //
		StringBuffer schedule = new StringBuffer();
		int createCol = 0;
		int alterCol = 0;
		for (int i = 0; i < compareResult.length; i++) { //��������в��졣���д���
			if ("������".equals(compareResult[i][1])) {
				String sqls[] = compareResult[i][3].split(";");
				for (int j = 0; j < sqls.length; j++) {
					if (sqls[j] != null && !sqls[j].trim().equals("") && !sqls[j].trim().equals("\r\n")) {
						sqllist.add(sqls[j]);
					}
				}
				setCurrSchedule("������[" + tableName + "]" + (sqllist.size() > 1 ? "����" + (sqllist.size() - 1) + "������" : ""));
				break;
			} else if ("������".equals(compareResult[i][1])) {
				createCol++;
				sqllist.add(compareResult[i][3]);
			} else if ("�޸���".equals(compareResult[i][1])) { //
				alterCol++;
				sqllist.add(compareResult[i][3]);
			}
		}
		if (sqllist.size() == 0) {
			return;
		}

		if ((createCol + alterCol) > 0) {//������ֱ��Ѿ����ڣ���Ҫ�޸�
			StringBuffer msg = new StringBuffer();
			msg.append(createCol > 0 ? "��[" + tableName + "]����[" + createCol + "]���ֶ�" : "��[" + tableName + "]");
			if (alterCol > 0 && alterCol > 0) {
				msg.append(",");
			}
			msg.append(alterCol > 0 ? "�޸�[" + alterCol + "]���ֶ�" : "");
			setCurrSchedule(msg.toString());
		}
		for (int i = 0; i < sqllist.size(); i++) {
			currExecuteSql(null, (String) sqllist.get(i));
		}

	}

	private boolean checkTableExistAndCreate(String _tableName) {
		try {
			getCommDMO().getTableDataStructByDS(null, "select * from " + _tableName + " where 1=2");
			return true;
		} catch (Exception e) {
			//���������
			try {
				getInstallDMO().createTableByPackagePrefix("/cn/com/infostrategy/bs/sysapp/install", _tableName);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return false;
	}

	//��װ��ͼ
	protected String installView(String _package_prefix) throws Exception {
		TBUtil tbUtil = new TBUtil(); //
		String str_xmlfileName = tbUtil.replaceAll(_package_prefix, ".", "/"); //���滻!!!
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
			String str_dbtype = ServerEnvironment.getDefaultDataSourceType(); //
			String str_sql = ((org.jdom.Element) param.getChildren("sql").get(0)).getText(); //
			str_sql = str_sql.trim(); //
			str_sql = tbUtil.replaceAll(str_sql, "\r", ""); //
			str_sql = tbUtil.replaceAll(str_sql, "\n", ""); //
			if (str_sql.endsWith(";")) { ////
				str_sql = str_sql.substring(0, str_sql.length() - 1); //
			}
			if (str_dbtype.equalsIgnoreCase("DB2")) {
				str_sql = str_sql.toUpperCase(); //
			} else if (str_dbtype.equalsIgnoreCase("sqlserver")) {
				if (str_sql.contains("replace")) {
					str_sql = str_sql.substring(0, str_sql.toLowerCase().indexOf("or")) + str_sql.substring(str_sql.toLowerCase().indexOf("replace") + 7);
				}
			}

			currExecuteSql(null, str_sql);
			setCurrSchedule("��ͼ[" + param.getAttributeValue("name") + "]�����ɹ�", 20, 40, i, list_tables.size());
		}
		return "��ͼ�ɹ�";
	}

	//����˵����˵�Ĭ��ȫ�����뵽���ڵ��¡�
	protected void installMenu(String _package_prefix) throws Exception {
		String xmlPath = _package_prefix + "RegisterMenu.xml";
		List al_menus = getInstallDMO().getRegistMenuFromXML(xmlPath);
		if (al_menus == null) { //û���ҵ��˵������ļ����ɱ�!
			return;
		}
		LinkedHashMap map_allDirNode = new LinkedHashMap(); //
		TBUtil tbUtil = new TBUtil(); //
		for (int i = 0; i < al_menus.size(); i++) {
			String[] str_menuInfo = (String[]) al_menus.get(i); //
			String str_xmlfile = str_menuInfo[0];
			String str_menuName = str_menuInfo[1];
			String str_command = str_menuInfo[2];
			String str_descr = str_menuInfo[3];
			String str_conf = str_menuInfo[4];
			String str_icon = str_menuInfo[5]; //��·��������ͼ��
			String str_commandtype = str_menuInfo[6]; //ִ������
			HashVO hvo = new HashVO(); //
			hvo.setAttributeValue("xmlfile", str_xmlfile); //
			hvo.setAttributeValue("menuname", str_menuName); //
			hvo.setAttributeValue("command", str_command); //
			hvo.setAttributeValue("descr", str_descr); //
			hvo.setAttributeValue("conf", str_conf); //
			hvo.setAttributeValue("commandtype", str_commandtype);
			if (str_icon != null) {
				if (str_icon.contains("[") && str_icon.contains("]")) {//�ж��Ƿ���� []
					hvo.setAttributeValue("icon", str_icon.substring(str_icon.lastIndexOf("[") + 1, str_icon.lastIndexOf("]"))); //�����һ��������ĩ�ڵ�ͼ��
				}
			}
			String str_viewName = str_menuName; //
			if (str_viewName.indexOf(".") > 0) {
				str_viewName = str_viewName.substring(str_viewName.lastIndexOf(".") + 1, str_viewName.length()); //
			}
			hvo.setAttributeValue("menunviewame", str_viewName); //
			hvo.setToStringFieldName("menunviewame"); //

			DefaultMutableTreeNode itmNode = new DefaultMutableTreeNode(hvo); //
			map_allDirNode.put(str_menuName, itmNode); //������

			String[] str_nameItems = tbUtil.split(str_menuName, "."); //
			String[] icons = null; //���е�ͼ������
			if (str_icon != null) {
				icons = str_icon.split("]");
			}
			for (int j = 0; j < str_nameItems.length; j++) {
				String str_thisOneLevelParentPath = ""; //
				for (int k = 0; k <= j; k++) {
					str_thisOneLevelParentPath = str_thisOneLevelParentPath + str_nameItems[k] + ".";
				}
				str_thisOneLevelParentPath = str_thisOneLevelParentPath.substring(0, str_thisOneLevelParentPath.length() - 1); //����ĳһ���ϼ���ȫ·��
				if (!map_allDirNode.containsKey(str_thisOneLevelParentPath)) { //���û��,�����!!!
					HashVO parentNodeVO = new HashVO(); //
					parentNodeVO.setAttributeValue("menuname", str_thisOneLevelParentPath); //
					String str_parentNodeViewName = str_thisOneLevelParentPath; //
					if (str_parentNodeViewName.indexOf(".") > 0) {
						str_parentNodeViewName = str_parentNodeViewName.substring(str_parentNodeViewName.lastIndexOf(".") + 1, str_parentNodeViewName.length()); //
					}
					parentNodeVO.setAttributeValue("menunviewame", str_parentNodeViewName); //
					if (icons != null && icons.length >= j) {
						String ic = tbUtil.replaceAll(icons[j], "[", "");
						if (ic != null && !ic.trim().equals("")) {
							parentNodeVO.setAttributeValue("icon", ic); //����ͼ��
						}
					}
					parentNodeVO.setToStringFieldName("menunviewame"); //
					DefaultMutableTreeNode tmpNode = new DefaultMutableTreeNode(parentNodeVO); //
					map_allDirNode.put(str_thisOneLevelParentPath, tmpNode); ////����
				}
			}
		}
		DefaultMutableTreeNode[] allNodes = (DefaultMutableTreeNode[]) map_allDirNode.values().toArray(new DefaultMutableTreeNode[0]); //
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("���ڵ�");
		for (int i = 0; i < allNodes.length; i++) {
			HashVO hvo_item = (HashVO) allNodes[i].getUserObject(); //
			String str_text = hvo_item.getStringValue("menuname"); //
			if (tbUtil.findCount(str_text, ".") <= 0) { //����ǵ�һ��,��ֱ�Ӽ�������!!
				rootNode.add(allNodes[i]); //
			} else {
				String str_parentText = str_text.substring(0, str_text.lastIndexOf(".")); //
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) map_allDirNode.get(str_parentText); //
				if (parentNode != null) {
					parentNode.add(allNodes[i]); //
				}
			}
		}
		String str_addtoMenuId = "0";//ֱ�Ӽ�����ڵ�
		HashMap mapMenuNameNewID = new HashMap(); //
		ArrayList al_sqls = new ArrayList(); //

		Vector vc = new Vector();
		visitAllNodes(vc, rootNode);
		if (vc.size() > 0) {
			if (((DefaultMutableTreeNode) vc.get(0)).isRoot()) {
				vc.remove(0);
			}
		}
		DefaultMutableTreeNode allMenuNodes[] = (DefaultMutableTreeNode[]) vc.toArray(new DefaultMutableTreeNode[0]);
		setCurrSchedule("��ʼ�����˵�");
		for (int i = 0; i < allMenuNodes.length; i++) {
			DefaultMutableTreeNode itemNode = allMenuNodes[i]; //
			HashVO itemVO = (HashVO) itemNode.getUserObject(); //
			String str_itemMenuName = itemVO.getStringValue("menuname"); //�˵�����
			boolean isParentIn = isMyParentAlreadyIn(itemNode, allMenuNodes); //�Ƿ��׽��Ҳ������!
			String str_newid = getCommDMO().getSequenceNextValByDS(null, "S_PUB_MENU"); //�µ�����!!!
			mapMenuNameNewID.put(str_itemMenuName, str_newid); //
			MyInsertSQlBuilder isql = new MyInsertSQlBuilder("pub_menu"); //
			isql.setPK_Item_Value("id", str_newid); // �����������趨����ֵ��
			isql.putFieldValue("code", itemVO.getStringValue("menunviewame")); //
			isql.putFieldValue("name", itemVO.getStringValue("menunviewame")); //
			isql.putFieldValue("ename", itemVO.getStringValue("menunviewame")); //��Ӣ������Ҳ���롾���/2016-05-10��
			if (isParentIn) { //����ҵĸ����Ѿ�����,��Ҫ�ҳ��Ҹ��׵��µ�id
				String str_myparentMenuName = str_itemMenuName.substring(0, str_itemMenuName.lastIndexOf(".")); //�Ҹ��׵Ľ������!!
				isql.putFieldValue("parentmenuid", (String) mapMenuNameNewID.get(str_myparentMenuName)); ////
			} else { //����ҵĸ��ײ���,���ұ�����ǵ�һ����!!
				if (str_addtoMenuId.equals("ROOT")) { //���������Ǹ����!!����
				} else {
					isql.putFieldValue("parentmenuid", str_addtoMenuId); //
				}
			}
			isql.putFieldValue("seq", str_newid); //
			isql.putFieldValue("isautostart", "N"); //
			isql.putFieldValue("isalwaysopen", "N"); //������Զ����
			if (itemNode.isLeaf()) { //�����������Ҷ�ӽ��,���������²���!!!
				isql.putFieldValue("usecmdtype", "1"); ////
				if ("0A".equals(itemVO.getStringValue("commandtype")) || "ST".equals(itemVO.getStringValue("commandtype"))) {
					isql.putFieldValue("commandtype", itemVO.getStringValue("commandtype")); //�Զ���WorkpPanel����
				} else {
					isql.putFieldValue("commandtype", "00"); //�Զ���WorkpPanel����
				}
				isql.putFieldValue("command", itemVO.getStringValue("command")); //�˵�·�����á�
				isql.putFieldValue("comments", str_itemMenuName + ";" + itemVO.getStringValue("xmlfile") + "\n" + itemVO.getStringValue("descr", ""));//˵���м���XMLע�Ṧ�ܵ����Ϣ������˵���˵����Ϣ[����2012-03-28]
				isql.putFieldValue("conf", itemVO.getStringValue("conf")); //����˵�����������Ϣ����ǰΪ�˰Ѳ˵������ٵ��룬�ظ�ȥ�����ͰѴ�����Ϣ�ŵ���˵���С�[2012-07-11]����				
			}
			isql.putFieldValue("icon", itemVO.getStringValue("icon")); //ͼ��
			al_sqls.add(isql); ////
			setCurrSchedule(null, 70, 90, i, allMenuNodes.length);
		}
		currExecuteInsertSql(null, al_sqls); //ִ��
	}

	protected void installOff_On(HashVO _install_updateConfig) throws Exception {

		String path = _install_updateConfig.getStringValue("packagepath");
		String modulecode = _install_updateConfig.getStringValue("code"); //ģ������
		String modulename = _install_updateConfig.getStringValue("name"); //ģ������
		List xmlOffList = new ArrayList();
		org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
		org.jdom.Document doc = builder.build(this.getClass().getResourceAsStream(path + "setting.xml")); // ����XML
		List on_offNode = doc.getRootElement().getChildren("on-off");
		if (on_offNode != null) {
			for (int i = 0; i < on_offNode.size(); i++) {
				Element on_off = (Element) on_offNode.get(i);
				if (on_off != null) {
					List allconfig = on_off.getChildren("config"); //�õ���������
					if (allconfig != null) {
						for (Object object : allconfig) {
							HashVO onVO = getXmlNodeAttributeByKeys((Element) object, "type", "key", "value"); //�õ�һ��������
							xmlOffList.add(onVO);
						}
					}
				}
			}
		}
		if (xmlOffList.size() > 0) {
			HashVO history[] = getCommDMO().getHashVoArrayByDS(null, "select *from pub_module_on_off where modulecode = '" + modulecode + "'"); //������û���ظ��ġ�
			List sqlInsertList = new ArrayList();
			for (int i = 0; i < xmlOffList.size(); i++) { //ѭ��xml��
				HashVO xmlOnVO = (HashVO) xmlOffList.get(i);
				String xml_key = xmlOnVO.getStringValue("key");
				String xml_type = xmlOnVO.getStringValue("type");
				String xml_value = xmlOnVO.getStringValue("value");
				if (xml_key == null || "".equals(xml_key) || xml_type == null || "".equals(xml_type))
					continue; //����п�ֵ������
				xml_type = xml_type.toUpperCase();
				xml_key = xmlOnVO.getStringValue("key").toUpperCase();
				boolean exist = false;
				for (int j = 0; j < history.length; j++) { //�������ݿ���ʷ
					HashVO hisVO = history[j];
					if (xml_type.equalsIgnoreCase(hisVO.getStringValue("modulecode")) && xml_key.equalsIgnoreCase(hisVO.getStringValue("mkey"))) { //��������key����������ôֱ������
						exist = true;
						break;
					}
				}
				if (!exist) { //���������ݿ�
					MyInsertSQlBuilder insql = new MyInsertSQlBuilder("pub_module_on_off");
					insql.setPK_Item_Value("id", getCommDMO().getSequenceNextValByDS(null, "S_pub_module_on_off"));
					insql.putFieldValue("modulename", modulename);
					insql.putFieldValue("modulecode", modulecode);
					insql.putFieldValue("type", xml_type);
					insql.putFieldValue("mkey", xml_key);
					insql.putFieldValue("mvalue", xml_value);
					sqlInsertList.add(insql);
				}
			}
			if (sqlInsertList.size() > 0) {
				currExecuteInsertSql(null, sqlInsertList);
			}
		}

	}

	private void currExecuteSql(String _prefix, String _Sql) throws Exception {
		setCurrSchedule(_prefix);
		if (_Sql != null && !_Sql.trim().equals("")) {
			getCommDMO().executeBatchByDSImmediately(null, new String[] { _Sql });
		}
	}

	private void currExecuteSql(String _prefix, List list) throws Exception {
		if (list != null && list.size() > 0) {
			int splitcount = list.size() / 500 + (list.size() % 500 == 0 ? 0 : 1);
			for (int i = 0; i < splitcount; i++) {
				List per1000list = new ArrayList();
				if (i == (splitcount - 1)) {
					per1000list = list.subList(500 * i, list.size());
				} else {
					per1000list = list.subList(500 * i, 500 * (i + 1));
				}
				getCommDMO().executeBatchByDSImmediately(null, per1000list);
			}
		}
		setCurrSchedule(_prefix); //ֻд���ݣ���д����
	}

	private void currExecuteInsertSql(String _prefix, List<MyInsertSQlBuilder> list) throws Exception {
		if (list != null && list.size() > 0) {
			int splitcount = list.size() / 500 + (list.size() % 500 == 0 ? 0 : 1);
			for (int i = 0; i < splitcount; i++) {
				List per1000list = new ArrayList();
				if (i == (splitcount - 1)) {
					per1000list = list.subList(500 * i, list.size());
				} else {
					per1000list = list.subList(500 * i, 500 * (i + 1));
				}
				getCommDMO().executeBatchByDSImmediately(null, per1000list); //ִ�гɹ��󣬷����Ѿ���װ������
				/**�����Ǵ�����ʷinsertSQL**/
				String saveTable = null;
				List idsList = new ArrayList();
				for (int j = 0; j < per1000list.size(); j++) {
					MyInsertSQlBuilder insertSql = (MyInsertSQlBuilder) per1000list.get(j);
					if (saveTable == null) {
						idsList = new ArrayList();
						saveTable = insertSql.getTableName();
						idsList.add(insertSql.getPk_value());
					} else if (insertSql.getTableName().equalsIgnoreCase(saveTable)) {
						idsList.add(insertSql.getPk_value());
					} else {
						putTableInsertDataHistByIds(saveTable, idsList);
						idsList = new ArrayList();
						saveTable = insertSql.getTableName();
						idsList.add(insertSql.getPk_value());
					}
					if (j == per1000list.size() - 1) { //���һ��ʱ,����һ��
						putTableInsertDataHistByIds(saveTable, idsList);
					}
				}
			}
		}
		setCurrSchedule(_prefix); //ֻд���ݣ���д����
	}

	//��ȡ�������ӽڵ�
	private void visitAllNodes(Vector _vector, TreeNode node) {
		_vector.add(node); // ����ý��
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) e.nextElement(); // �ҵ��ö���!!
				visitAllNodes(_vector, childNode); // �������Ҹö���
			}
		}
	}

	private boolean isMyParentAlreadyIn(DefaultMutableTreeNode _thisNode, DefaultMutableTreeNode[] _allNodes) {
		String str_menuName = ((HashVO) _thisNode.getUserObject()).getStringValue("menuname"); //�˵�����
		if (str_menuName.indexOf(".") < 0) {
			return false; //����Լ����ǵ�һ��,��϶������и��׽����!!!
		}
		String str_myparentMenuName = str_menuName.substring(0, str_menuName.lastIndexOf(".")); //�Ҹ��׵Ľ������!!
		for (int i = 0; i < _allNodes.length; i++) {
			HashVO itemVO = (HashVO) _allNodes[i].getUserObject(); ////
			String str_itemMenuName = itemVO.getStringValue("menuname"); //
			if (str_itemMenuName.equals(str_myparentMenuName)) {
				return true; //
			}
		}
		return false; //
	}

	/*
	 * ��ʼ�����ݴ����Ϊ����
	 * 1��ֱ�Ӳ������ݿ�
	 * 2���������������롣������������
	 */
	protected void installData(String _package_prefix) throws Exception {

		_package_prefix = tbutil.replaceAll(_package_prefix, ".", "/");
		if (!_package_prefix.startsWith("/")) {
			_package_prefix = "/" + _package_prefix;
		}
		if (!_package_prefix.endsWith("/")) {
			_package_prefix = _package_prefix + "/";
		}
		String newid_package_prefix = _package_prefix + "xtdata2/"; // ��̬����
		String oldid_package_prefix = _package_prefix + "xtdata/"; // ��̬����
		//		String table_xmlfileName = _package_prefix + "database/tables.xml"; // ���б�

		HashMap tabname_deffkmap = new HashMap(); // ����������ı� ����-userid=pub_user.id;deptid=pub_corp_dept.id;
		String[][] modules = new BSUtil().getAllInstallPackages(null);
		if (modules == null || modules.length == 0) {
			return;
		}
		for (int i = 0; i < modules.length; i++) {
			String package_prefix = modules[i][0];
			String table_xmlfileName = package_prefix + "database/tables.xml"; // ���б�
			getTable_PKAndFK(table_xmlfileName, alltabname_key, tabname_deffkmap); // ��ȡ���б�Ͷ�������ı�  --2013-5-28������Ϊϵͳ����tables.xml�����б�
		}

		//��ִ��ԭ���ݲ������ݿ�
		List initsqls_1 = new ArrayList();
		List seqsqls_1 = new ArrayList();
		HashMap needUpdateSeqTab = new HashMap(); // ��װ����Щ��̬��
		importXmlToTableRecords(null, oldid_package_prefix, alltabname_key, initsqls_1, seqsqls_1, null, null, needUpdateSeqTab, false);
		currExecuteInsertSql("ԭ���ݳ�ʼ��--���ݲ���", initsqls_1);
		//���ݲ���������������л��档����pub_sequence��
		getUpdateSeqSql(needUpdateSeqTab, seqsqls_1);
		currExecuteSql("ԭ���ݳ�ʼ��--��������", seqsqls_1);
		System.gc(); //֪ͨ�ͷŶ�ջ

		//��ִ�й��������в���
		List initsqls_2 = new ArrayList(); // ��̬���ݳ�ʼ��sql
		List initsqls_addcolumn_2 = new ArrayList(); // ��̬��������α��sql
		List initsqls_update_2 = new ArrayList(); // ��̬���ݸ������sql
		List initsqls_remcolumn_2 = new ArrayList(); // ��̬����ɾ��α��sql
		HashMap allChangeTab = new HashMap(); // ��װ����Щ��̬��
		importXmlToTableRecords(null, newid_package_prefix, alltabname_key, initsqls_2, null, initsqls_addcolumn_2, initsqls_remcolumn_2, allChangeTab, true);
		if (initsqls_addcolumn_2 != null && initsqls_addcolumn_2.size() > 0) {
			if (initsqls_addcolumn_2.size() > 0) {
				setCurrSchedule("��̬���ݳ�ʼ������α�У���" + initsqls_addcolumn_2.size() + "�ű�");
			}
			for (int i = 0; i < initsqls_addcolumn_2.size(); i++) {
				currExecuteSql(null, (String) initsqls_addcolumn_2.get(i)); //һ��һ�Ŵ�����������
			}
			currExecuteInsertSql("��̬���ݳ�ʼ��", initsqls_2);
			getUpdateSqls(tabname_deffkmap, alltabname_key, initsqls_update_2, allChangeTab, needUpdateSeqTab);
			currExecuteSql("��̬�����������", initsqls_update_2);
			if (initsqls_remcolumn_2.size() > 0) {
				setCurrSchedule("��̬���ݳ�ʼ��ɾ��α��:��" + initsqls_remcolumn_2.size() + "�ű�");
			}
			for (int i = 0; i < initsqls_remcolumn_2.size(); i++) {
				currExecuteSql(null, (String) initsqls_remcolumn_2.get(i));
			}
		}
	}

	/**
	 * ���и���
	 * @param needUpdateSeqTab
	 * @param updatesql
	 * @throws Exception
	 */
	private void getUpdateSeqSql(HashMap needUpdateSeqTab, List updatesql) throws Exception {
		CommDMO dmo = getCommDMO();
		String[] tabname = (String[]) needUpdateSeqTab.keySet().toArray(new String[0]);
		if (tabname != null && tabname.length > 0) {
			HashMap existSeq = getCommDMO().getHashMapBySQLByDS(null, "select sename,currvalue from pub_sequence");
			for (int j = 0; j < tabname.length; j++) {
				String maxid = dmo.getStringValueByDS(null, "select max(" + needUpdateSeqTab.get(tabname[j]) + ") from " + tabname[j]);
				if (existSeq.containsKey("S_" + tabname[j].toUpperCase())) {
					updatesql.add(" update pub_sequence set currvalue='" + (Integer.parseInt(maxid) + 10) + "' where sename='S_" + tabname[j].toUpperCase() + "'");
				} else {
					updatesql.add(" insert into pub_sequence (sename,currvalue) values('S_" + tabname[j].toUpperCase() + "','" + (Integer.parseInt(maxid) + 10) + "')");
				}
				//				dmo.getSequenceNextValByDS(null, "S_" + tabname[j].toUpperCase()); // 

			}
		}
	}

	private void getTable_PKAndFK(String tablesxml, HashMap table_primarykey, HashMap table_fk) {
		try {
			String table_xmlfileName = tablesxml;
			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder();
			org.jdom.Document doc = builder.build(this.getClass().getResourceAsStream(table_xmlfileName));
			List list_tables = doc.getRootElement().getChildren("table");
			for (int i = 0; i < list_tables.size(); i++) {
				org.jdom.Element param = (org.jdom.Element) list_tables.get(i);
				String str_tname = param.getAttributeValue("name");
				String str_tkey = param.getAttributeValue("pkname");
				if (str_tkey != null && !"".equals(str_tkey.trim())) {
					table_primarykey.put(str_tname.toLowerCase(), str_tkey.toLowerCase()); // ����-������
				} else {
					System.out.println("��" + str_tname + "��table.xml��δ��������!�����߼��ῴ�����Ƿ����id�ֶΡ�");
				}
				List table_column = param.getChildren("columns");
				if (table_column != null && table_column.size() > 0) {
					org.jdom.Element column_param = (org.jdom.Element) table_column.get(0);
					List column_item = column_param.getChildren("col");
					if (column_item != null && column_item.size() > 0) {
						for (int item = 0; item < column_item.size(); item++) {
							org.jdom.Element itemele = (org.jdom.Element) column_item.get(item);
							String columnname = itemele.getAttributeValue("name");
							String reffield = itemele.getAttributeValue("reffield");
							if (!table_primarykey.containsKey(str_tname.toLowerCase()) && "id".equals(columnname.toLowerCase())) { // ���xml��δ������������������id�ֶ�����ΪidΪ����
								table_primarykey.put(str_tname.toLowerCase(), columnname.toLowerCase());
								System.out.println("��" + str_tname + "��table.xml��δ��������!������id�ֶ���Ĭ��idΪ������");
							}
							if (reffield != null && !"".equals(reffield.trim()) && reffield.indexOf(".") > 0) { // pub_user.id����ʽ
								String reftablename = tbutil.split(reffield, ".")[0];
								String refcolumname = tbutil.split(reffield, ".")[1];
								if (table_fk.containsKey(str_tname.toLowerCase())) {
									table_fk.put(str_tname.toLowerCase(), table_fk.get(str_tname.toLowerCase()) + columnname + "=" + reffield + ";");
								} else {
									table_fk.put(str_tname.toLowerCase(), columnname + "=" + reffield + ";");
								}
							}
						}
						if (!table_fk.containsKey(str_tname.toLowerCase())) {
							table_fk.put(str_tname.toLowerCase(), "");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void importXmlToTableRecords(String _dsName, String packname, HashMap alltabname_key, List<MyInsertSQlBuilder> al_sqls, List sequpdatesql, List addColumnSqls, List remvColumnSqls, HashMap allchangetab, boolean isCreateId) {
		try {
			String dbType = ServerEnvironment.getDefaultDataSourceType();
			List allxmlname = getAllXmlNameByPackName(packname);
			if (allxmlname != null && allxmlname.size() > 0) {
				for (int o = 0; o < allxmlname.size(); o++) { //ÿһ��xmlֻ�ܴ��һ�ű�����ݡ���
					org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder();
					org.jdom.Document doc = builder.build(this.getClass().getResourceAsStream(packname + allxmlname.get(o)));
					java.util.List recordNodeList = doc.getRootElement().getChildren("record"); //�������м�¼!
					if (recordNodeList != null && recordNodeList.size() > 0) {
						String str_tableName = "";
						TableDataStruct tabstrct = null;
						HashSet<String> hstCols = null; // ��ŶԱ��ֶ�,������ٲ������ݿ�
						for (int i = 0; i < recordNodeList.size(); i++) { //����������¼!!!
							org.jdom.Element recordNode = (org.jdom.Element) recordNodeList.get(i);
							String tableName = recordNode.getAttributeValue("tabname"); //����
							if (!tableName.equalsIgnoreCase(str_tableName)) { //�����жϣ�����ÿ�ζ�ȥȡ
								str_tableName = tableName;
								tabstrct = commDMO.getTableDataStructByDS(_dsName, "select * from " + str_tableName + " where 1=2", false); //
								String[] str_cols = tabstrct.getHeaderName(); //
								hstCols = new HashSet<String>();
								for (int k = 0; k < str_cols.length; k++) {
									hstCols.add(str_cols[k].toLowerCase()); //
								}
							}

							if (str_tableName != null && alltabname_key.containsKey(str_tableName.toLowerCase())) { // ��������tables.xml�ﶨ��
								MyInsertSQlBuilder isql = new MyInsertSQlBuilder(str_tableName); //
								java.util.List colNodeList = recordNode.getChildren("col"); //
								for (int j = 0; j < colNodeList.size(); j++) { //��������Col�ӽ��!!������!!
									org.jdom.Element colNode = (org.jdom.Element) colNodeList.get(j);
									String str_colName = colNode.getAttributeValue("name"); //����!
									String str_colValue = colNode.getText(); //��ֵ!
									if (!hstCols.contains(str_colName.toLowerCase())) { //���û�д�ֵ��������
										continue;
									}
									if ((alltabname_key.get(str_tableName.toLowerCase()).toString().toLowerCase()).equals(str_colName.toLowerCase()) && !isCreateId) {
										allchangetab.put(str_tableName.toLowerCase(), str_colName.toLowerCase()); //
									}
									if ((alltabname_key.get(str_tableName.toLowerCase()).toString().toLowerCase()).equals(str_colName.toLowerCase())) { // �������Ϊ����
										if (isCreateId) {
											if (!allchangetab.containsKey(str_tableName.toLowerCase())) {
												allchangetab.put(str_tableName.toLowerCase(), str_colName.toLowerCase());
												addColumnSqls.add(" alter table " + str_tableName + " add column " + str_colName.toLowerCase() + "_oldid " + (dbType.equalsIgnoreCase("oracle") ? "varchar2" : "varchar") + "(100);");
												remvColumnSqls.add(" alter table " + str_tableName + " drop column " + str_colName.toLowerCase() + "_oldid;");
											}
											String str_newValue = getCommDMO().getSequenceNextValByDS(_dsName, "S_" + str_tableName.toUpperCase()); //������ȡ���µ�����ֵ!!!
											isql.setPK_Item_Value(str_colName, str_newValue);
											isql.putFieldValue(str_colName.toLowerCase() + "_oldid", str_colValue); // ����α�д洢�ɵ�ID
										} else {
											isql.setPK_Item_Value(str_colName, str_colValue);
										}
									} else {
										isql.putFieldValue(str_colName, str_colValue);
									}
								}
								al_sqls.add(isql);
							}
						}
						setCurrSchedule("���ڽ����ļ�[" + allxmlname.get(o).toString().toLowerCase() + "]�е�����", !isCreateId ? 40 : 50, !isCreateId ? 50 : 60, 0, allxmlname.size());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void putTableInsertDataHistByIds(String _tableName, List _idList) {
		if (installingDataHis.containsKey(_tableName.toUpperCase())) {
			List list = (List) installingDataHis.get(_tableName.toUpperCase());
			list.addAll(_idList);
		} else {
			List idsList = new ArrayList();
			idsList.addAll(_idList);
			installingDataHis.put(_tableName.toUpperCase(), idsList);
		}
	}

	private void putTableInsertDataHis(String _tableName, String id) {
		if (installingDataHis.containsKey(_tableName.toUpperCase())) {
			List list = (List) installingDataHis.get(_tableName.toUpperCase());
			list.add(id);
		} else {
			List idsList = new ArrayList();
			idsList.add(id);
			installingDataHis.put(_tableName.toUpperCase(), idsList);
		}
	}

	/**
	 * ��ȡ�����������xml�ļ���
	 * @param packName
	 * @return
	 */
	private List getAllXmlNameByPackName(String packName) {
		ClassLoader l = Thread.currentThread().getContextClassLoader();
		List allnewidtable = new ArrayList();
		if (packName.startsWith("/")) {
			packName = packName.substring(1);
		}
		URL url = l.getResource(packName);
		try {
			if (url != null) {
				String protocol = url.getProtocol();
				if ("jar".equals(protocol)) { // �������jar��
					JarURLConnection con = (JarURLConnection) url.openConnection();
					JarFile file = con.getJarFile();
					Enumeration enu = file.entries();
					String entryName = null;
					String className = null;
					while (enu.hasMoreElements()) {
						entryName = ((JarEntry) enu.nextElement()).getName();
						className = entryName.substring(entryName.lastIndexOf("/") + 1);
						if (!className.equals("") && entryName.equals(packName + className)) {
							if (entryName != null && entryName.toLowerCase().endsWith(".xml")) {
								allnewidtable.add(className);
							}
						}
					}
				} else { // �������class��
					File file = new File(new URI(url.toExternalForm()));
					File[] files = file.listFiles();
					String name = null;
					for (int j = 0; j < files.length; j++) {
						if (!files[j].isDirectory()) {
							name = files[j].getName();
							if (name != null && name.toLowerCase().endsWith(".xml")) {
								//								String tablename = name.substring(0, name.lastIndexOf("."));
								allnewidtable.add(name);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return allnewidtable;
	}

	/**
	 * @param refmap ����id���������漰����Ҫ���µı� ����-refid=pub_user.id;
	 * @param table_primarykey �������map
	 * @return
	 */
	private void getUpdateSqls(HashMap refmap, HashMap table_primarykey, List updatesql, HashMap allChangeTab, HashMap allNeedUpdateSeqTab) {
		if (refmap.size() > 0 && allChangeTab.size() > 0) { // �������������ϵ
			String[] tabls = (String[]) refmap.keySet().toArray(new String[0]);
			String refdes = null;
			String tab = null;
			String key = null;
			for (int i = 0; i < tabls.length; i++) {
				if (allChangeTab.containsKey(tabls[i].toLowerCase()) || allNeedUpdateSeqTab.containsKey(tabls[i].toLowerCase())) { // ����˱��ڰ�װ��Χ��
					refdes = (String) refmap.get(tabls[i]);
					HashMap refdesmap = tbutil.convertStrToMapByExpress(refdes, ";", "=");
					if (refdesmap != null && refdesmap.size() > 0) {
						String[] refcolumn = (String[]) refdesmap.keySet().toArray(new String[0]);
						for (int j = 0; j < refcolumn.length; j++) {
							String ref = (String) refdesmap.get(refcolumn[j]);
							if (ref.indexOf(".") > 0) {
								tab = tbutil.split(ref, ".")[0].trim().toLowerCase();
								key = tbutil.split(ref, ".")[1].trim().toLowerCase();
								if (key.equals(table_primarykey.get(tab)) && allChangeTab.containsKey(tab.toLowerCase())) { // ����������������ֶ�������ͬʱ���Ƕ�̬��װ
									updatesql.add(" update " + tabls[i] + " t set t." + refcolumn[j] + " = ( select " + key + " from (select * from " + tab + " ) as x where x." + key + "_oldid = t." + refcolumn[j] + ") ");
								}
							}
						}
					}
				}
				setCurrSchedule(null, 60, 70, i, tabls.length);
			}
		}
	}

	protected void executeCustomClass(String _classPath) throws Exception {
		try {
			Class c = Class.forName(_classPath);
			QuickInstallModuleCustomIFC ifc = (QuickInstallModuleCustomIFC) c.newInstance();
			ifc.afterAutoInstallOrUpdateDo();
		} catch (Exception e) {
			System.out.println("û���ҵ��Զ�����");
		}
		for (int i = 0; i < 20; i++) {
			setCurrSchedule(null, 90, 100, i, 20);
		}
	}

	//����xml��ǩ�е�����key��ȡһ��hashvo
	private HashVO getXmlNodeAttributeByKeys(Element _node, String... _key) {
		if (_node == null || _key == null || _key.length == 0) {
			return null;
		}
		HashVO hvo = new HashVO();
		for (int i = 0; i < _key.length; i++) {
			if (_key[i] != null) {
				String value = _node.getAttributeValue(_key[i]);
				hvo.setAttributeValue(_key[i], value);
			}
		}
		return hvo;
	}

	//����һ��·����ȡģ�����Ϣ
	public String[] getModuleInfo(String _path) throws Exception {
		return null;
	}

	//��ȡdmo
	private CommDMO getCommDMO() {
		if (commDMO == null) {
			commDMO = new CommDMO();
		}
		return commDMO;
	}

	//��ȡdmo
	private InstallDMO getInstallDMO() {
		if (dmo == null) {
			dmo = new InstallDMO();
		}
		return dmo;
	}

	private DataBaseUtilDMO getDataUtilDMO() {
		if (dataUtilDMO == null) {
			dataUtilDMO = new DataBaseUtilDMO();
		}
		return dataUtilDMO;
	}

	public static void setCurrSchedule(String str) {
		if (str == null || "".equals(str)) {
			return;
		}
		schedule.push(new Object[] { str, -1f }); //-1��Ӱ�����
	}

	public synchronized static void setCurrSchedule(String str, int _begin, int _end, int index, int _totle) {
		//		System.out.println("schedule2��"+str);
		float currPer = ((float) _end - (float) _begin) / (float) _totle * (float) index + _begin;
		schedule.push(new Object[] { str, currPer });
	}

	public synchronized static List getCurrSchedule() {
		if (schedule.isEmpty()) {
			return null;
		}
		int currSize = schedule.size();
		List<Object[]> str = new ArrayList<Object[]>();
		for (int i = 0; i < currSize; i++) {
			str.add((Object[]) schedule.pop());
		}
		return str;
	}

	private static final String MODULEONOFFTYPETABLE = "TABLE"; //����ֶ���ʾ����
	private static final String MODULEONOFFTYPETEMPLET = "TEMPLET"; //ģ���ֶ���ʾ����
	private static final String MODULEONOFFTYPETBUTTON = "BUTTON"; //��ť��ʾ����

	private boolean checkModuleOn_OFF(String key) throws Exception {
		if (key == null || key.equals("")) {
			return true;
		}
		if (ServerEnvironment.getInstance().get("moduleonoff") == null) {
			try {
				HashVO[] onoffvo = getCommDMO().getHashVoArrayByDS(null, "select * from pub_module_on_off");
				HashMap map = new HashMap();
				for (int i = 0; i < onoffvo.length; i++) {
					String onoff_key = onoffvo[i].getStringValue("type").toUpperCase() + "_" + onoffvo[i].getStringValue("mkey").toUpperCase(); //���صļ�
					String onoff_value = onoffvo[i].getStringValue("mvalue").toUpperCase();
					if ("N".equalsIgnoreCase(onoff_value)) { //Ĭ�Ͼ���Y����Ҫ����
						map.put(onoff_key, onoff_value); //
					}
				}
				String v = (String) map.get(key.toUpperCase());
				ServerEnvironment.getInstance().put("moduleonoff", map);
				if ("N".equals(v)) {
					return false;
				}
			} catch (Exception ex) {
				if (notExist(ex)) {
					System.out.println("��pub_module_on_off�������ڣ�ϵͳ�Զ�����");
					getInstallDMO().createTableByPackagePrefix("/cn/com/infostrategy/bs/sysapp/install", "pub_module_on_off");
				} else {
					throw ex;
				}
			}
			return true;
		} else {
			HashMap map = (HashMap) ServerEnvironment.getInstance().get("moduleonoff");
			String v = (String) map.get(key.toUpperCase());
			ServerEnvironment.getInstance().put("moduleonoff", map);
			if ("N".equals(v)) {
				return false;
			}
			return true;
		}
		//����
	}

	private boolean notExist(Exception ex) {
		if (ex != null && (ex.getMessage().contains("doesn't exist") || ex.getMessage().contains("������") || ex.getMessage().contains("204") || ex.getMessage().contains("208"))) {
			return true;
		}
		return false;
	}

	//����IDֵˢ�·������˿��ػ���
	public void refreshModuleOn_OffByIds(List _onoffIds) throws Exception {
		if (_onoffIds != null && _onoffIds.size() > 0) {
			HashMap map = (HashMap) ServerEnvironment.getInstance().get("moduleonoff");
			if (map == null) {
				checkModuleOn_OFF("-test");
				map = (HashMap) ServerEnvironment.getInstance().get("moduleonoff");
			}
			HashVO[] onoffvo = getCommDMO().getHashVoArrayByDS(null, "select * from pub_module_on_off where id in (" + tbutil.getInCondition(_onoffIds) + ")");
			for (int i = 0; i < onoffvo.length; i++) {
				String onoff_key = onoffvo[i].getStringValue("type").toUpperCase() + "_" + onoffvo[i].getStringValue("mkey").toUpperCase(); //���صļ�
				String onoff_value = onoffvo[i].getStringValue("mvalue").toUpperCase();
				if ("N".equalsIgnoreCase(onoff_value)) { //Ĭ�Ͼ���Y����Ҫ����
					map.put(onoff_key, onoff_value); //
				} else {
					if (map.containsKey(onoff_key)) {
						map.remove(onoff_key); //�����Y�����Ұ�����ֱ�����ߡ�
					}
				}
			}
		}
	}

	public synchronized static boolean checkTempletItemVisible(String _templetCode, String _saveTable, String _itemCode) throws Exception {
		boolean o1 = installDMO.checkModuleOn_OFF(MODULEONOFFTYPETABLE + "_" + _saveTable + "." + _itemCode);
		if (!o1) {
			return false;
		}
		boolean o2 = installDMO.checkModuleOn_OFF(MODULEONOFFTYPETEMPLET + "_" + _templetCode + "." + _itemCode);
		if (!o2) {
			return false;
		}
		return true;
	}

	//����������ʱ��Ĭ��������ʼ�����ݣ���bootservlet�з�����á�
	public void initBootServlet() {
		initServicePool();
	}

	private void initServicePool() {
		try {
			long ll_1 = System.currentTimeMillis();
			// debugMsg("��ʼ��ʼ��SOA�����...");
			String[][] modules = new BSUtil().getAllInstallPackages(null);
			if (modules == null || modules.length == 0) {
				return;
			}
			List moduleList = new ArrayList();
			HashVO logs[] = null; //�Ѿ���װ����������װ��ʷ��
			SAXBuilder builder = new SAXBuilder();
			for (int i = 0; i < modules.length; i++) {
				InputStream input = this.getClass().getResourceAsStream(modules[i][0] + "setting.xml"); //��ȡ�ļ�
				if (input == null) {
					continue;
				}
				Document dom = builder.build(input);
				if (dom != null) {
					String moduleName = dom.getRootElement().getAttributeValue("name"); //ģ������
					Element weblight = dom.getRootElement().getChild("weblight"); //��settting������weblight�в�����
					if (weblight != null) {
						List moduleService = weblight.getChildren("service"); //�ҵ�ģ��ķ���
						if (moduleService != null && moduleService.size() > 0) {
							for (int m = 0; m < moduleService.size(); m++) {
								Element service = (Element) moduleService.get(m); //����
								String str_servicename = service.getAttributeValue("name"); // �õ�����
								String str_implclass = service.getChild("implclass").getText(); //
								String str_initsize = service.getChild("initsize").getText(); //
								String str_maxsize = service.getChild("poolsize").getText(); //
								if (ServicePoolFactory.getInstance().getPool(str_servicename) != null) {
									logger.info("ģ��[" + moduleName + "]��ʼ��SOA�����[" + str_servicename + "]�Ѿ����ڡ�");
									continue;
								}
								str_implclass = str_implclass.trim(); //
								int li_initsize = Integer.parseInt(str_initsize); //
								int li_maxsize = Integer.parseInt(str_maxsize); //
								ServicePoolableObjectFactory factory = new ServicePoolableObjectFactory(str_servicename, str_implclass); //
								GenericObjectPool pool = new GenericObjectPool(factory); // ���������,���100��ʵ��!!!!
								pool.setMaxActive(li_maxsize); // �����
								pool.setMaxIdle(li_maxsize); // �������
								try {
									for (int j = 0; j < li_initsize; j++) { // �ȴ���3��ʵ��!!
										pool.addObject(); // �ȴ���һ��ʵ��
									}
								} catch (Exception ex) {
									ex.printStackTrace();
								}
								ServicePoolFactory.getInstance().registPool(str_servicename, str_implclass, pool); // ע��һ����!!
								logger.info("��ʼ��SOA�����[" + str_servicename + "][" + str_implclass + "]�ɹ�,������[" + str_initsize + "]��ʵ��,ִ��ģ��[" + moduleName + "]");
							}

						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public HashMap getAllModuleTablesTree(HashMap _default) throws Exception {
		String[][] modules = new BSUtil().getAllInstallPackages(null);
		if (modules == null || modules.length == 0) {
			return null;
		}
		HashVO rootNodeVO = new HashVO();
		rootNodeVO.setAttributeValue("id", "-999999");
		rootNodeVO.setToStringFieldName("name");
		rootNodeVO.setAttributeValue("name", "���ϵ");
		rootNodeVO.setAttributeValue("parentid", "-9987");
		rootNodeVO.setAttributeValue("control", "");
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootNodeVO);
		for (int i = 0; i < modules.length; i++) {
			String _package_prefix = modules[i][0];
			InputStream input = this.getClass().getResourceAsStream(modules[i][0] + "setting.xml"); //��ȡ�ļ�
			DefaultMutableTreeNode moduleNode = null;
			if (input != null) { //����ļ�ʵ�ʴ���
				org.jdom.Document doc = new org.jdom.input.SAXBuilder().build(input); // ����XML.
				Element root = doc.getRootElement();
				HashVO module = getXmlNodeAttributeByKeys(root, "name", "code", "sn", "icon");
				module.setToStringFieldName("name");
				module.setAttributeValue("id", (-1 - i) + "");
				module.setAttributeValue("parentid", "-999999");
				module.setAttributeValue("control", "");
				moduleNode = new DefaultMutableTreeNode(module);
			} else {
				moduleNode = new DefaultMutableTreeNode("δ�ҵ�ģ������");
			}
			String table_xmlfileName = _package_prefix + "database/tables.xml"; // ���б�
			HashMap tabname_deffkmap = new HashMap(); // ����������ı� ����-userid=pub_user.id;deptid=pub_corp_dept.id;
			HashMap alltabname_key = new HashMap(); // ���б���-����
			getTable_PKAndFK(table_xmlfileName, tabname_deffkmap, alltabname_key);

			DefaultMutableTreeNode nodes[] = new DefaultMutableTreeNode[alltabname_key.size()];
			Iterator it = alltabname_key.entrySet().iterator();
			int index = 0;
			HashMap allNodeMap = new HashMap();
			Vector moreChildCopy = new Vector();//�еı����̫�ࡣ��Ҫ����һ�� 
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				HashVO vo = new HashVO();
				vo.setAttributeValue("id", (i * 1000 + index) + "");
				vo.setToStringFieldName("name");
				vo.setAttributeValue("name", key);
				if (tabname_deffkmap.containsKey(key)) {
					vo.setAttributeValue("code", tabname_deffkmap.get(key));//pk����
				}
				vo.setAttributeValue("foreign", value);
				vo.setAttributeValue("control", value);
				vo.setAttributeValue("parentid", "-999999");
				nodes[index] = new DefaultMutableTreeNode(vo);
				allNodeMap.put(key, nodes[index]);
				index++;
			}
			for (int j = 0; j < nodes.length; j++) {
				HashVO vo = (HashVO) nodes[j].getUserObject();
				String key = (String) vo.getStringValue("name");
				String value = (String) vo.getStringValue("foreign");
				if (value != null && !value.equals("")) {
					String[] foreigns = tbutil.split(value, ";"); //�������
					if (foreigns != null && foreigns.length > 0) { //����ж���������ô�͸��Ƴ�һ��
						String one_foreign = foreigns[0]; //������ô�Ķ��ѵ�һ����Ϊ������Ҫ�ġ���Ҫ��map��ע�ᡣ�������һЩ���Ƴ����Ĵ˽ڵ㣬ֻ����Ϊ���ӡ�
						String str[] = tbutil.split(one_foreign, "."); //2����������
						String t = str[0];
						String tableName = t.split("=")[1];
						if (allNodeMap.containsKey(tableName)) {
							DefaultMutableTreeNode p_node = (DefaultMutableTreeNode) allNodeMap.get(tableName);
							try {
								HashVO parentVO = (HashVO) p_node.getUserObject();
								String pid = parentVO.getStringValue("id");
								vo.setAttributeValue("parentid", pid);
								vo.setAttributeValue("foreign", one_foreign);
								vo.setAttributeValue("control", one_foreign);
								p_node.add(nodes[j]);
							} catch (Exception e) {

							}
						}
						for (int k = 1; k < foreigns.length; k++) {
							String one_foreign_1 = foreigns[k]; // �õ�һ������� 
							String str_1[] = tbutil.split(one_foreign_1, "."); //2����������
							String t_1 = str_1[0];
							String tableName_1 = t_1.split("=")[1];
							if (allNodeMap.containsKey(tableName_1)) {
								DefaultMutableTreeNode p_node = (DefaultMutableTreeNode) allNodeMap.get(tableName_1);
								try {
									HashVO parentVO = (HashVO) p_node.getUserObject();
									String pid = parentVO.getStringValue("id");
									HashVO vo_1 = vo.clone();
									vo_1.setAttributeValue("foreign", one_foreign_1);
									vo_1.setAttributeValue("control", one_foreign_1);
									vo_1.setAttributeValue("parentid", pid);
									DefaultMutableTreeNode node_copy = new DefaultMutableTreeNode(vo_1);
									p_node.add(node_copy);
								} catch (Exception e) {

								}
							}
						}
					}
				}
			}
			for (int j = 0; j < nodes.length; j++) {
				if (nodes[j].getParent() == null) {
					HashVO vo = (HashVO) nodes[j].getUserObject();
					vo.setAttributeValue("parentid", (-1 - i) + "");
					moduleNode.add(nodes[j]);
				}
			}
			rootNode.add(moduleNode);
		}
		HashMap value = new HashMap();
		value.put("moduletables", rootNode);
		return value;
	}

	//�õ�java��������������
	//�Ժ���Լ��������������ui���룬ui������÷������˴��롣
	public void getJavaSourceImportOtherPackage() throws Exception {
		Class cls = Class.forName("cn.com.infostrategy.bs.sysapp.install.quickInstall.QuickInstallDMO");
	}

	public static void main(String[] args) {

		try {
			new QuickInstallDMO().getJavaSourceImportOtherPackage();
			System.out.println(new QuickInstallDMO().sha("i73jorcjzq0g7uoe1o7out485y0souyg"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String md5(String _str) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'z', 'x', 'c' }; // �����Լ���Կ��
		try {
			MessageDigest mdTemp = MessageDigest.getInstance("MD5"); //
			mdTemp.update(_str.getBytes());
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2]; //
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i]; //
				str[k++] = hexDigits[byte0 >>> 4 & 0x1F]; //
				str[k++] = hexDigits[byte0 & 0x1F]; //
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}

	private String sha(String _str) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'z', 'x', 'c' }; // �����Լ���Կ��
		try {
			MessageDigest mdTemp = MessageDigest.getInstance("SHA");
			mdTemp.update(_str.getBytes());
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0x1F];
				str[k++] = hexDigits[byte0 & 0x1F];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}

	//�ͻ��˷���ר��
	public HashMap getXMlFromTable500RecordsByCondition(HashMap _map) throws Exception {
		String dotype = (String) _map.get("dotype"); //���С��500����ֱ�Ӳ�ȫ���Ϳ����ˡ�500����ƽ̨���ơ�dotype������ֵ:0�ô�������sql��1��ƽ̨������sql��
		String dsname = (String) _map.get("dsname");
		String table = (String) _map.get("table");
		String beginno = (String) _map.get("beginNo");
		String joinSql = (String) _map.get("joinsql");
		String condition = (String) _map.get("condition");
		String fullSql = (String) _map.get("fullsql");
		if ("0".equals(dotype)) {
			return getXMLMapDataBySQL(dsname, fullSql, table, -1);
		} else {
			return getXMlFromTable500RecordsByCondition(dsname, table, Integer.parseInt(beginno), joinSql, condition);
		}
	}

	//����500����¼��������ipushgrc�Ϲ���Ǩ������
	public HashMap getXMlFromTable500RecordsByCondition(String _dsName, String table, int _beginNo, String joinSql, String _condition) throws Exception {
		String _tableName = table;
		int li_batchRecords = 500; // һ��ȡ500����¼!!!
		int li_endNo = _beginNo + li_batchRecords; // [>=1 and <=1000][>=1001// and <=2000][>=2001 and// <=3000]
		String dbType = ServerEnvironment.getDefaultDataSourceType();
		StringBuffer sql_sb = new StringBuffer();
		if (_dsName != null && !_dsName.equals("null")) {
			DataSourceVO vo = ServerEnvironment.getInstance().getDataSourceVO(_dsName);
			if (vo != null) {
				dbType = vo.getDbtype();
			}
		}
		if (_condition != null && !_condition.equals("")) {
			if (_condition.trim().indexOf("where") != 0) {
				_condition = " where " + _condition;
			} else {
				_condition = " " + _condition;
			}
		} else {
			_condition = " ";
		}
		if (joinSql == null || joinSql.equals("")) {
			joinSql = " ";
		}
		if (dbType.equalsIgnoreCase("MYSQL")) {
			sql_sb.append("select " + _tableName + ".*  from " + _tableName + " " + joinSql + " " + _condition + " order by 1 limit " + _beginNo + "," + li_batchRecords);
		} else if (dbType.equalsIgnoreCase("ORACLE")) {
			sql_sb.append("select " + _tableName + ".* from (select " + _tableName + ".*,Rownum RN from (select " + _tableName + ".* from " + _tableName + " " + joinSql + " " + _condition + "  order by 1 )" + _tableName);
			sql_sb.append("  where Rownum <=" + (_beginNo + li_batchRecords) + ") " + _tableName);
			sql_sb.append(" where RN > " + _beginNo);
		} else if (dbType.equals("SQLSERVER")) {
			StringBuilder sb_sql_new = new StringBuilder(); //
			sb_sql_new.append("with _t1 as "); //
			sb_sql_new.append("("); //
			sb_sql_new.append("select row_number() over (order by 1 asc) _rownum,");
			sb_sql_new.append(" * from " + _tableName + " " + joinSql + " " + _condition); // ��ԭ����select���濪ʼ�����ݽ�����!
			sb_sql_new.append(") ");
			sb_sql_new.append("select top " + (li_batchRecords) + " * from _t1 where _rownum >= " + _beginNo + ""); // ��ҳ!!!
			sql_sb.append(sb_sql_new.toString()); //
		}
		return getXMLMapDataBySQL(_dsName, sql_sb.toString(), _tableName, (_beginNo + li_batchRecords)); //
	}

	// ȡ����
	private HashMap<String, Object> getXMLMapDataBySQL(String _dsName, String _sql, String _tabName, int _lastrow) throws Exception { // 
		StringBuilder sb_xml = new StringBuilder(); //
		sb_xml.append("<?xml version=\"1.0\" encoding=\"GBK\"?>\r\n"); //
		sb_xml.append("<root tablename=\"" + _tabName + "\">\r\n"); //
		HashVOStruct hvst = getCommDMO().getHashVoStructByDS(_dsName, _sql); //
		String[] str_keys = hvst.getHeaderName(); // ����
		HashVO[] hvs = hvst.getHashVOs(); //
		String str_itemValue = null; //
		int li_returnRecordCount = 0; //
		for (int i = 0; i < hvs.length; i++) { // ��������!!
			sb_xml.append("<!--" + (i + 1) + "-->\r\n"); //
			sb_xml.append("<record tabname=\"" + _tabName + "\">\r\n"); //
			for (int j = 0; j < str_keys.length; j++) { // ��������!!
				if (str_keys[j].equalsIgnoreCase("RN"))
					continue; // ������RN�����������ķ�Χ���ˣ����Զ��RN�����С�
				str_itemValue = hvs[i].getStringValue(str_keys[j], ""); // ȡ��ֵ!!
				if (str_itemValue == null || str_itemValue.trim().equals(""))
					continue;
				if (str_itemValue.indexOf("<") >= 0 || str_itemValue.indexOf(">") >= 0 || str_itemValue.indexOf("&") >= 0) { // ��������<>
					str_itemValue = "<![CDATA[" + str_itemValue + "]]>"; //
				}
				sb_xml.append("  <col name=\"" + str_keys[j] + "\">" + str_itemValue + "</col>\r\n"); // //
			}
			sb_xml.append("</record>\r\n"); //
			sb_xml.append("\r\n"); //
			li_returnRecordCount++; //
		}
		sb_xml.append("</root>\r\n"); //

		HashMap<String, Object> returnMap = new HashMap<String, Object>(); //
		returnMap.put("��¼��", new Integer(li_returnRecordCount)); //
		returnMap.put("������", new Integer(_lastrow)); //
		returnMap.put("����", sb_xml.toString()); // x
		return returnMap; // �����µ�idֵ!
	}

	//���ΰ�װ�����ݽ��г�����Ŀǰֻ��insert���ݽ��й��� 
	public String unInstall() throws Exception {
		if (installingDataHis.size() > 0) {
			List deleteSqlList = new ArrayList();
			Iterator it = installingDataHis.entrySet().iterator();
			while (it.hasNext()) {
				Entry type = (Entry) it.next();
				String tableName = (String) type.getKey();
				List values = (List) type.getValue();
				DeleteSQLBuilder delete = new DeleteSQLBuilder(tableName);
				String pkitem = (String) alltabname_key.get(tableName.toLowerCase());
				delete.setWhereCondition(pkitem + " in (" + tbutil.getInCondition(values) + ")");
				deleteSqlList.add(delete);
				System.out.println(delete.getSQL());
				setCurrSchedule("������װ[" + tableName + "]������");
			}
			getCommDMO().executeBatchByDSImmediately(null, deleteSqlList);
		}
		return "���ΰ�װ�Ѿ�����";
	}

	class MyInsertSQlBuilder extends InsertSQLBuilder {
		private String pk_item = "id";
		private String pk_value = "";

		public MyInsertSQlBuilder(String _tableName) {
			super(_tableName);
		}

		public String getPk_value() {
			return pk_value;
		}

		//		public void setPk_value(String pk_value) {
		//			this.pk_value = pk_value;
		//		}

		public String getPk_item() {
			return pk_item;
		}

		public void setPk_item(String pk_item) {
			this.pk_item = pk_item.toLowerCase();
		}

		public void setPK_Item_Value(String _pkItem, String _value) {
			pk_item = _pkItem.toUpperCase();
			pk_value = _value;
			putFieldValue(_pkItem, _value);
		}
	}
}
