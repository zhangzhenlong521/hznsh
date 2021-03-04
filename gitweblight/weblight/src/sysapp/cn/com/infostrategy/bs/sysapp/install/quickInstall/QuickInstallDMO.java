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
	private static Queue schedule = new Queue(); //这个用来记录当前执行进度，ui端在点击安装或者升级操作后，会不停的调用该内容。
	private TBUtil tbutil = TBUtil.getTBUtil();
	public static String MODULE_INSTALL_DEAL_TYPE_VALIDATE = "validate";
	public static String MODULE_INSTALL_DEAL_TYPE_UPDATE = "update";
	public static String MODULE_INSTALL_DEAL_TYPE_INSTALL = "install";
	private static QuickInstallDMO installDMO = new QuickInstallDMO();
	private Logger logger = WLTLogger.getLogger(CommDMO.class); //
	private HashMap alltabname_key = new HashMap(); // 所有表名-主键,全局用到。
	private HashMap installingDataHis = new HashMap(); //记录插入数据库的历史。

	//找可以安装的包，必须配置setting.xml文件,文件主要用来后续扩展用。其实合并到table.xml或者其他文件中,都是可以的。个人偏好。
	public HashVO[] getAllInstallModuleStatus() throws Exception {
		String[][] modules = new BSUtil().getAllInstallPackages(null);
		if (modules == null || modules.length == 0) {
			return null;
		}
		List moduleList = new ArrayList();
		HashVO logs[] = null; //已经安装或者其他安装历史。
		try {
			logs = getCommDMO().getHashVoArrayByDS(null, "select * from pub_install_update_log"); //
		} catch (Exception ex) {
			if (notExist(ex)) { //204是db2的错误编码( 命名的对象未在DB2中定义)
				System.out.println("表【pub_install_update_log】不存在，系统自动创建");
				getInstallDMO().createTableByPackagePrefix("/cn/com/infostrategy/bs/sysapp/install", "pub_install_update_log");
				logs = new HashVO[0];
			} else {
				throw ex;
			}
		}
		for (int i = 0; i < modules.length; i++) {
			InputStream input = this.getClass().getResourceAsStream(modules[i][0] + "setting.xml"); //获取文件
			if (input != null) { //如果文件实际存在
				org.jdom.Document doc = new org.jdom.input.SAXBuilder().build(input); // 加载XML.
				Element rootNode = doc.getRootElement();
				HashVO module = getXmlNodeAttributeByKeys(rootNode, "name", "code", "sn", "icon");
				List list = doc.getRootElement().getChildren("install");
				boolean validated = false; //校验过
				boolean installed = false;//安装过
				boolean update_do = false;//是否可以更新
				String lastVersion = "0";
				List upVersionList = new ArrayList(); //得到比较大的版本。
				if (list.size() > 0) {
					Element installNode = (Element) list.get(0);
					HashVO install = getXmlNodeAttributeByKeys(installNode, "version", "date");
					module.setAttributeValue("version", install.getStringValue("version"));
					module.setAttributeValue("date", install.getStringValue("date"));
					module.setAttributeValue("id", moduleList.size()); //随便搞一个序号
					module.setAttributeValue("packagepath", modules[i][0]);
					//判断该模块可执行的状态，安装，授权，更新...等等。

					//第一步：判断有没有授权成功过，
					for (int j = 0; j < logs.length; j++) {
						String code = logs[j].getStringValue("moduleCode"); //模块编码
						String operateType = logs[j].getStringValue("operateType");//操作类型install,update,uninstall,validate
						String result = logs[j].getStringValue("result");//安装、升级等机构 Y、N
						String version = logs[j].getStringValue("version"); //版本
						String input_msg = logs[j].getStringValue("input"); //输入

						if (tbutil.isEmpty(code) || tbutil.isEmpty(operateType) || tbutil.isEmpty(result) || tbutil.isEmpty(version)) {
							throw new WLTAppException("系统检测到安装的日志表被篡改");
						}
						if (code.equals(module.getStringValue("code"))) { //找到了该模块的记录
							if (operateType.equals("validate")) { //效验的历史
								if ("Y".equals(result)) { //
									validated = true;
								}
							} else if (operateType.equals("install")) { //安装的历史
								if ("Y".equals(result)) { //安装成功
									lastVersion = version;
									installed = true;
								}
							} else if (operateType.equals("update")) { //升级的历史。
								if ("Y".equals(result)) { //升级成功
									if (lastVersion != null && Float.parseFloat(lastVersion) < Float.parseFloat(version)) {
										lastVersion = version; //得到最大版本，有可能是升级的，有可能是安装的。
									}
								}
							}
						}
					}
					if (!validated && "FLAT".equals(module.getStringValue("code"))) {
						validated = true;
					}
					if (!installed && "FLAT".equals(module.getStringValue("code"))) { //如果没有安装过平台，需要精确判断一下。查询pub_menu是否创建过。。
						TableDataStruct str = null;
						try {
							str = getCommDMO().getTableDataStructByDS(null, "select * from pub_menu");
						} catch (Exception ex) {
							str = null;
						}
						if (str != null) { //如果安装过此表，说明存在。
							installed = true;
						}
					}
					if (!lastVersion.equals("") && !"0".equals(lastVersion)) {
						module.setAttributeValue("version", lastVersion);
					}
					//					if (Float.parseFloat(lastVersion) < Float.parseFloat(module.getStringValue("version"))) {
					//						update_do = true;
					//						upVersionList.add(module.getStringValue("version")); //大版本。先不加入，默认规定，只要想让执行一些脚本，就必须发布一个版本。只代码的变动，可以不发布版本。
					//					}
					//如何判断免安装呢，可能有些系统不用安装，直接更新就可以了。
				}
				if (installed) { //如果已经安装过，需要判断是否更新
					Element ele = rootNode.getChild("updates"); //找到更新节点组
					if (ele != null) {
						List update = ele.getChildren("update"); //得到所有可升级的内容。
						for (int j = 0; j < update.size(); j++) {
							Element updateNode = (Element) update.get(j); //得到一条升级的配置内容。
							String version = updateNode.getAttributeValue("version");
							if (version != null && Float.parseFloat(lastVersion) < Float.parseFloat(version)) { //比较配置文件中的和升级历史比较。
								update_do = true;
								upVersionList.add(version); //把可以升级的版本加入。
							}
						}
					}
					if (list.size() > 0 && lastVersion.equals("0")) {
						module.setAttributeValue("version", "-1");
					}
				}

				if (update_do) { //如果可以更新，还需要是否有新更新
					module.setAttributeValue("control", WLTConstants.MODULE_INSTALL_STATUS_KSJ); //已经安装可升级
					module.setAttributeValue("updates", upVersionList);
				} else if (!update_do && installed) {
					module.setAttributeValue("control", WLTConstants.MODULE_INSTALL_STATUS_YAZ); //已经安装不用升级
				} else if (!update_do && !installed && validated) {
					module.setAttributeValue("control", WLTConstants.MODULE_INSTALL_STATUS_KAZ); //可以安装
					Element license = doc.getRootElement().getChild("license"); //授权
					if (license != null) {
						String lin_text = license.getValue();
						module.setAttributeValue("license", lin_text);
					}
				} else if (!update_do && !installed && !validated) {
					//module.setAttributeValue("control", WLTConstants.MODULE_INSTALL_STATUS_WSQ); //未授权
					module.setAttributeValue("control", WLTConstants.MODULE_INSTALL_STATUS_KAZ); //先不效验了， 以后再说 Gwang-2015-11-29
					//显示最新安装版本。
				}

				if (!update_do && !installed && !validated && list.size() == 0)
					continue;
				moduleList.add(module);
			}
		}
		return (HashVO[]) moduleList.toArray(new HashVO[0]);
	}

	//检查是否可以进行操作
	public boolean checkCanOperate(HashVO _selectVO) {
		boolean flag = false;
		if (_selectVO != null) {
			String type = _selectVO.getStringValue("type"); //type=install安装,update更新,anthorize授权...
			String packagepath = _selectVO.getStringValue("packagepath");
			String version = _selectVO.getStringValue("version");
		}
		return flag;
	}

	//远程调用的统一判断入口，安装、升级、卸载等。
	public String installOrUpdateOperateAction(HashVO _install_updateConfig, String _operateType) throws Exception {
		schedule = new Queue();
		installingDataHis = new HashMap(); //重置缓存
		alltabname_key = new HashMap();
		boolean flag = false;
		if (MODULE_INSTALL_DEAL_TYPE_INSTALL.equalsIgnoreCase(_operateType)) { //安装
			flag = installModuleAction(_install_updateConfig);
		} else if (MODULE_INSTALL_DEAL_TYPE_UPDATE.equals(_operateType)) {//更新
			flag = updateModuleAction(_install_updateConfig);
		} else if (MODULE_INSTALL_DEAL_TYPE_VALIDATE.equals(_operateType)) { //授权校验
			flag = validateSN(_install_updateConfig);
		}
		setCurrSchedule(null, 100, 100, 1, 1);
		System.gc();
		return flag ? "success" : "fail";
	}

	private boolean validateSN(HashVO _install_updateConfig) throws Exception {
		
		//先不效验了， 以后再说 Gwang-2015-11-29
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
			buildLog(_install_updateConfig, MODULE_INSTALL_DEAL_TYPE_VALIDATE, flag, flag ? "效验成功，可以安装" : "输入错误的效验码", inputsn);
			setCurrSchedule(flag ? "效验成功，可以安装" : "输入错误的效验码,请把识别号【" + str_md5 + "】发给系统供应商以生成验证码");
		}
		return flag;
	}

	private void buildLog(HashVO _install_updateConfig, String _deal_type, boolean flag, String _log, String _input) throws Exception {
		boolean ff = checkTableExistAndCreate("wltdual");
		if (ff) {
			List list = new ArrayList();
			list.add("delete from wltdual");
			list.add("insert into wltdual values('1')");
			setCurrSchedule("自动初始化wltdual关键表！");
			getCommDMO().executeBatchByDSImmediately(null, list);
		}
		checkTableExistAndCreate("pub_sequence");
		InsertSQLBuilder insql = new InsertSQLBuilder("pub_install_update_log");
		insql.putFieldValue("id", getCommDMO().getSequenceNextValByDS(null, "pub_install_update_log"));
		insql.putFieldValue("moduleCode", _install_updateConfig.getStringValue("code"));
		insql.putFieldValue("modulename", _install_updateConfig.getStringValue("name"));
		insql.putFieldValue("moduleDescr", "省略");
		insql.putFieldValue("operateType", _deal_type);
		insql.putFieldValue("log", _log);
		insql.putFieldValue("userid", getCommDMO().getCurrSession().getLoginUserId());
		insql.putFieldValue("time", tbutil.getCurrTime());
		insql.putFieldValue("result", flag ? "Y" : "N");
		insql.putFieldValue("input", _input);
		insql.putFieldValue("version", _install_updateConfig.getStringValue("version"));
		currExecuteSql(null, insql.getSQL());
	}

	//安装操作 
	private boolean installModuleAction(HashVO _install_updateConfig) throws Exception {
		try {
			setCurrSchedule("即将开始安装");
			String path = _install_updateConfig.getStringValue("packagepath");
			//执行表结构
			setCurrSchedule("正在创建表:");
			installTable(path);

			//执行试图
			setCurrSchedule("正在创建试图:");
			installView(path);

			//执行初始化数据
			installData(path);
			//加入菜单
			installMenu(path);
			System.gc();
			//on-off执行，加入到数据库中，用来开关该模块所有开关信息。
			installOff_On(_install_updateConfig);
			//执行自定义类方法			
			executeCustomClass(path);
			buildLog(_install_updateConfig, MODULE_INSTALL_DEAL_TYPE_INSTALL, true, "安装成功", "");
			setCurrSchedule("安装完毕");
		} catch (Exception e) {
			buildLog(_install_updateConfig, MODULE_INSTALL_DEAL_TYPE_INSTALL, false, "安装失败\r\n报错信息：" + e.getMessage(), "");
			setCurrSchedule("安装失败");
			setCurrSchedule("开始撤销已安装的数据");
			String str = unInstall();
			setCurrSchedule(str);
			//开始撤销SQL
			throw e;
		}
		return true;
	}

	//升级操作
	private boolean updateModuleAction(HashVO _install_updateConfig) {
		return false;
	}

	protected String installTable(String _package_prefix) throws Exception {
		TBUtil tbUtil = new TBUtil(); //
		String str_xmlfileName = tbUtil.replaceAll(_package_prefix, ".", "/"); //先替换!!!
		if (!str_xmlfileName.startsWith("/")) {
			str_xmlfileName = "/" + str_xmlfileName; //
		}
		if (!str_xmlfileName.endsWith("/")) {
			str_xmlfileName = str_xmlfileName + "/"; //
		}
		str_xmlfileName = str_xmlfileName + "database/tables.xml"; //

		org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
		org.jdom.Document doc = builder.build(this.getClass().getResourceAsStream(str_xmlfileName)); // 加载XML
		java.util.List list_tables = doc.getRootElement().getChildren("table"); //得到安装配置的所有表。
		for (int i = 0; i < list_tables.size(); i++) {
			org.jdom.Element param = (org.jdom.Element) list_tables.get(i);
			String str_tname = param.getAttributeValue("name"); //表名
			if ("Y".equals(param.getAttributeValue("ignore"))) {
				continue;
			}
			//如果已经存在该表,需要进行表结构比较。
			compareAndDealTableAction(str_tname, str_xmlfileName);
			setCurrSchedule(null, 0, 20, i, list_tables.size());
			if (str_tname.equalsIgnoreCase("wltdual")) { //这个神表，好多地方都用到，必须有一条数据。
				List list = new ArrayList();
				list.add("delete from wltdual");
				list.add("insert into wltdual values('1')");
				setCurrSchedule("自动初始化wltdual关键表！");
				getCommDMO().executeBatchByDSImmediately(null, list);
			}

		} //end for i
		return "表安装成功!";
	}

	//
	private void compareAndDealTableAction(String tableName, String str_xmlfileName) throws Exception {
		String compareResult[][] = getDataUtilDMO().compareTableDictByDB(null, tableName, str_xmlfileName); //用来比较
		if (compareResult == null) { //存在一张完全相同的表
			setCurrSchedule("表[" + tableName + "]已经存在，并且结构一致");
			return;
		}
		List sqllist = new ArrayList(); //
		StringBuffer schedule = new StringBuffer();
		int createCol = 0;
		int alterCol = 0;
		for (int i = 0; i < compareResult.length; i++) { //如果发现有差异。进行处理
			if ("创建表".equals(compareResult[i][1])) {
				String sqls[] = compareResult[i][3].split(";");
				for (int j = 0; j < sqls.length; j++) {
					if (sqls[j] != null && !sqls[j].trim().equals("") && !sqls[j].trim().equals("\r\n")) {
						sqllist.add(sqls[j]);
					}
				}
				setCurrSchedule("创建表[" + tableName + "]" + (sqllist.size() > 1 ? "包含" + (sqllist.size() - 1) + "个索引" : ""));
				break;
			} else if ("创建列".equals(compareResult[i][1])) {
				createCol++;
				sqllist.add(compareResult[i][3]);
			} else if ("修改列".equals(compareResult[i][1])) { //
				alterCol++;
				sqllist.add(compareResult[i][3]);
			}
		}
		if (sqllist.size() == 0) {
			return;
		}

		if ((createCol + alterCol) > 0) {//如果发现表已经存在，需要修改
			StringBuffer msg = new StringBuffer();
			msg.append(createCol > 0 ? "表[" + tableName + "]新增[" + createCol + "]个字段" : "表[" + tableName + "]");
			if (alterCol > 0 && alterCol > 0) {
				msg.append(",");
			}
			msg.append(alterCol > 0 ? "修改[" + alterCol + "]个字段" : "");
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
			//如果不存在
			try {
				getInstallDMO().createTableByPackagePrefix("/cn/com/infostrategy/bs/sysapp/install", _tableName);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return false;
	}

	//安装试图
	protected String installView(String _package_prefix) throws Exception {
		TBUtil tbUtil = new TBUtil(); //
		String str_xmlfileName = tbUtil.replaceAll(_package_prefix, ".", "/"); //先替换!!!
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
			setCurrSchedule("视图[" + param.getAttributeValue("name") + "]创建成功", 20, 40, i, list_tables.size());
		}
		return "试图成功";
	}

	//加入菜单。菜单默认全部加入到根节点下。
	protected void installMenu(String _package_prefix) throws Exception {
		String xmlPath = _package_prefix + "RegisterMenu.xml";
		List al_menus = getInstallDMO().getRegistMenuFromXML(xmlPath);
		if (al_menus == null) { //没有找到菜单配置文件。可悲!
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
			String str_icon = str_menuInfo[5]; //该路径的所有图标
			String str_commandtype = str_menuInfo[6]; //执行类型
			HashVO hvo = new HashVO(); //
			hvo.setAttributeValue("xmlfile", str_xmlfile); //
			hvo.setAttributeValue("menuname", str_menuName); //
			hvo.setAttributeValue("command", str_command); //
			hvo.setAttributeValue("descr", str_descr); //
			hvo.setAttributeValue("conf", str_conf); //
			hvo.setAttributeValue("commandtype", str_commandtype);
			if (str_icon != null) {
				if (str_icon.contains("[") && str_icon.contains("]")) {//判断是否包含 []
					hvo.setAttributeValue("icon", str_icon.substring(str_icon.lastIndexOf("[") + 1, str_icon.lastIndexOf("]"))); //截最后一个。就是末节点图标
				}
			}
			String str_viewName = str_menuName; //
			if (str_viewName.indexOf(".") > 0) {
				str_viewName = str_viewName.substring(str_viewName.lastIndexOf(".") + 1, str_viewName.length()); //
			}
			hvo.setAttributeValue("menunviewame", str_viewName); //
			hvo.setToStringFieldName("menunviewame"); //

			DefaultMutableTreeNode itmNode = new DefaultMutableTreeNode(hvo); //
			map_allDirNode.put(str_menuName, itmNode); //先置入

			String[] str_nameItems = tbUtil.split(str_menuName, "."); //
			String[] icons = null; //所有的图标数组
			if (str_icon != null) {
				icons = str_icon.split("]");
			}
			for (int j = 0; j < str_nameItems.length; j++) {
				String str_thisOneLevelParentPath = ""; //
				for (int k = 0; k <= j; k++) {
					str_thisOneLevelParentPath = str_thisOneLevelParentPath + str_nameItems[k] + ".";
				}
				str_thisOneLevelParentPath = str_thisOneLevelParentPath.substring(0, str_thisOneLevelParentPath.length() - 1); //本人某一层上级的全路径
				if (!map_allDirNode.containsKey(str_thisOneLevelParentPath)) { //如果没有,则加入!!!
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
							parentNodeVO.setAttributeValue("icon", ic); //加入图标
						}
					}
					parentNodeVO.setToStringFieldName("menunviewame"); //
					DefaultMutableTreeNode tmpNode = new DefaultMutableTreeNode(parentNodeVO); //
					map_allDirNode.put(str_thisOneLevelParentPath, tmpNode); ////置入
				}
			}
		}
		DefaultMutableTreeNode[] allNodes = (DefaultMutableTreeNode[]) map_allDirNode.values().toArray(new DefaultMutableTreeNode[0]); //
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("根节点");
		for (int i = 0; i < allNodes.length; i++) {
			HashVO hvo_item = (HashVO) allNodes[i].getUserObject(); //
			String str_text = hvo_item.getStringValue("menuname"); //
			if (tbUtil.findCount(str_text, ".") <= 0) { //如果是第一层,则直接加入根结点!!
				rootNode.add(allNodes[i]); //
			} else {
				String str_parentText = str_text.substring(0, str_text.lastIndexOf(".")); //
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) map_allDirNode.get(str_parentText); //
				if (parentNode != null) {
					parentNode.add(allNodes[i]); //
				}
			}
		}
		String str_addtoMenuId = "0";//直接加入根节点
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
		setCurrSchedule("开始创建菜单");
		for (int i = 0; i < allMenuNodes.length; i++) {
			DefaultMutableTreeNode itemNode = allMenuNodes[i]; //
			HashVO itemVO = (HashVO) itemNode.getUserObject(); //
			String str_itemMenuName = itemVO.getStringValue("menuname"); //菜单名称
			boolean isParentIn = isMyParentAlreadyIn(itemNode, allMenuNodes); //是否父亲结点也在里面!
			String str_newid = getCommDMO().getSequenceNextValByDS(null, "S_PUB_MENU"); //新的主键!!!
			mapMenuNameNewID.put(str_itemMenuName, str_newid); //
			MyInsertSQlBuilder isql = new MyInsertSQlBuilder("pub_menu"); //
			isql.setPK_Item_Value("id", str_newid); // 设置主键。设定主键值。
			isql.putFieldValue("code", itemVO.getStringValue("menunviewame")); //
			isql.putFieldValue("name", itemVO.getStringValue("menunviewame")); //
			isql.putFieldValue("ename", itemVO.getStringValue("menunviewame")); //将英文名称也加入【李春娟/2016-05-10】
			if (isParentIn) { //如果我的父亲已经在了,则要找出我父亲的新的id
				String str_myparentMenuName = str_itemMenuName.substring(0, str_itemMenuName.lastIndexOf(".")); //我父亲的结点名称!!
				isql.putFieldValue("parentmenuid", (String) mapMenuNameNewID.get(str_myparentMenuName)); ////
			} else { //如果我的父亲不在,即我本身就是第一层了!!
				if (str_addtoMenuId.equals("ROOT")) { //如果加入的是根结点!!则不设
				} else {
					isql.putFieldValue("parentmenuid", str_addtoMenuId); //
				}
			}
			isql.putFieldValue("seq", str_newid); //
			isql.putFieldValue("isautostart", "N"); //
			isql.putFieldValue("isalwaysopen", "N"); //不能永远开发
			if (itemNode.isLeaf()) { //如果这个结点是叶子结点,才设置以下参数!!!
				isql.putFieldValue("usecmdtype", "1"); ////
				if ("0A".equals(itemVO.getStringValue("commandtype")) || "ST".equals(itemVO.getStringValue("commandtype"))) {
					isql.putFieldValue("commandtype", itemVO.getStringValue("commandtype")); //自定义WorkpPanel类型
				} else {
					isql.putFieldValue("commandtype", "00"); //自定义WorkpPanel类型
				}
				isql.putFieldValue("command", itemVO.getStringValue("command")); //菜单路径配置。
				isql.putFieldValue("comments", str_itemMenuName + ";" + itemVO.getStringValue("xmlfile") + "\n" + itemVO.getStringValue("descr", ""));//说明中加入XML注册功能点的信息！加入菜单的说明信息[郝明2012-03-28]
				isql.putFieldValue("conf", itemVO.getStringValue("conf")); //插入菜单参数配置信息，以前为了把菜单导出再导入，重复去做，就把此项信息放到了说明中。[2012-07-11]郝明				
			}
			isql.putFieldValue("icon", itemVO.getStringValue("icon")); //图标
			al_sqls.add(isql); ////
			setCurrSchedule(null, 70, 90, i, allMenuNodes.length);
		}
		currExecuteInsertSql(null, al_sqls); //执行
	}

	protected void installOff_On(HashVO _install_updateConfig) throws Exception {

		String path = _install_updateConfig.getStringValue("packagepath");
		String modulecode = _install_updateConfig.getStringValue("code"); //模块名称
		String modulename = _install_updateConfig.getStringValue("name"); //模块名称
		List xmlOffList = new ArrayList();
		org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
		org.jdom.Document doc = builder.build(this.getClass().getResourceAsStream(path + "setting.xml")); // 加载XML
		List on_offNode = doc.getRootElement().getChildren("on-off");
		if (on_offNode != null) {
			for (int i = 0; i < on_offNode.size(); i++) {
				Element on_off = (Element) on_offNode.get(i);
				if (on_off != null) {
					List allconfig = on_off.getChildren("config"); //得到所有配置
					if (allconfig != null) {
						for (Object object : allconfig) {
							HashVO onVO = getXmlNodeAttributeByKeys((Element) object, "type", "key", "value"); //得到一条的配置
							xmlOffList.add(onVO);
						}
					}
				}
			}
		}
		if (xmlOffList.size() > 0) {
			HashVO history[] = getCommDMO().getHashVoArrayByDS(null, "select *from pub_module_on_off where modulecode = '" + modulecode + "'"); //看看有没有重复的。
			List sqlInsertList = new ArrayList();
			for (int i = 0; i < xmlOffList.size(); i++) { //循环xml中
				HashVO xmlOnVO = (HashVO) xmlOffList.get(i);
				String xml_key = xmlOnVO.getStringValue("key");
				String xml_type = xmlOnVO.getStringValue("type");
				String xml_value = xmlOnVO.getStringValue("value");
				if (xml_key == null || "".equals(xml_key) || xml_type == null || "".equals(xml_type))
					continue; //如果有空值，跳过
				xml_type = xml_type.toUpperCase();
				xml_key = xmlOnVO.getStringValue("key").toUpperCase();
				boolean exist = false;
				for (int j = 0; j < history.length; j++) { //遍历数据库历史
					HashVO hisVO = history[j];
					if (xml_type.equalsIgnoreCase(hisVO.getStringValue("modulecode")) && xml_key.equalsIgnoreCase(hisVO.getStringValue("mkey"))) { //如果编码和key都包含。那么直接跳过
						exist = true;
						break;
					}
				}
				if (!exist) { //新增进数据库
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
		setCurrSchedule(_prefix); //只写内容，不写进度
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
				getCommDMO().executeBatchByDSImmediately(null, per1000list); //执行成功后，放入已经安装缓存中
				/**以下是处理历史insertSQL**/
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
					if (j == per1000list.size() - 1) { //最后一个时,加入一把
						putTableInsertDataHistByIds(saveTable, idsList);
					}
				}
			}
		}
		setCurrSchedule(_prefix); //只写内容，不写进度
	}

	//获取树所有子节点
	private void visitAllNodes(Vector _vector, TreeNode node) {
		_vector.add(node); // 加入该结点
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) e.nextElement(); // 找到该儿子!!
				visitAllNodes(_vector, childNode); // 继续查找该儿子
			}
		}
	}

	private boolean isMyParentAlreadyIn(DefaultMutableTreeNode _thisNode, DefaultMutableTreeNode[] _allNodes) {
		String str_menuName = ((HashVO) _thisNode.getUserObject()).getStringValue("menuname"); //菜单名称
		if (str_menuName.indexOf(".") < 0) {
			return false; //如果自己就是第一层,则肯定不会有父亲结点了!!!
		}
		String str_myparentMenuName = str_menuName.substring(0, str_menuName.lastIndexOf(".")); //我父亲的结点名称!!
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
	 * 初始化数据大体分为两种
	 * 1、直接插入数据库
	 * 2、关联表新增插入。重新生成序列
	 */
	protected void installData(String _package_prefix) throws Exception {

		_package_prefix = tbutil.replaceAll(_package_prefix, ".", "/");
		if (!_package_prefix.startsWith("/")) {
			_package_prefix = "/" + _package_prefix;
		}
		if (!_package_prefix.endsWith("/")) {
			_package_prefix = _package_prefix + "/";
		}
		String newid_package_prefix = _package_prefix + "xtdata2/"; // 动态数据
		String oldid_package_prefix = _package_prefix + "xtdata/"; // 静态数据
		//		String table_xmlfileName = _package_prefix + "database/tables.xml"; // 所有表

		HashMap tabname_deffkmap = new HashMap(); // 定义了外键的表 表名-userid=pub_user.id;deptid=pub_corp_dept.id;
		String[][] modules = new BSUtil().getAllInstallPackages(null);
		if (modules == null || modules.length == 0) {
			return;
		}
		for (int i = 0; i < modules.length; i++) {
			String package_prefix = modules[i][0];
			String table_xmlfileName = package_prefix + "database/tables.xml"; // 所有表
			getTable_PKAndFK(table_xmlfileName, alltabname_key, tabname_deffkmap); // 获取所有表和定义外键的表  --2013-5-28郝明改为系统所有tables.xml的所有表
		}

		//先执行原数据插入数据库
		List initsqls_1 = new ArrayList();
		List seqsqls_1 = new ArrayList();
		HashMap needUpdateSeqTab = new HashMap(); // 安装了那些静态表
		importXmlToTableRecords(null, oldid_package_prefix, alltabname_key, initsqls_1, seqsqls_1, null, null, needUpdateSeqTab, false);
		currExecuteInsertSql("原数据初始化--数据插入", initsqls_1);
		//根据插入的数据重置序列缓存。更新pub_sequence表。
		getUpdateSeqSql(needUpdateSeqTab, seqsqls_1);
		currExecuteSql("原数据初始化--更新序列", seqsqls_1);
		System.gc(); //通知释放堆栈

		//再执行关联新序列插入
		List initsqls_2 = new ArrayList(); // 动态数据初始化sql
		List initsqls_addcolumn_2 = new ArrayList(); // 动态数据增加伪列sql
		List initsqls_update_2 = new ArrayList(); // 动态数据更新外键sql
		List initsqls_remcolumn_2 = new ArrayList(); // 动态数据删除伪列sql
		HashMap allChangeTab = new HashMap(); // 安装了那些动态表
		importXmlToTableRecords(null, newid_package_prefix, alltabname_key, initsqls_2, null, initsqls_addcolumn_2, initsqls_remcolumn_2, allChangeTab, true);
		if (initsqls_addcolumn_2 != null && initsqls_addcolumn_2.size() > 0) {
			if (initsqls_addcolumn_2.size() > 0) {
				setCurrSchedule("动态数据初始化增加伪列：共" + initsqls_addcolumn_2.size() + "张表");
			}
			for (int i = 0; i < initsqls_addcolumn_2.size(); i++) {
				currExecuteSql(null, (String) initsqls_addcolumn_2.get(i)); //一张一张处理，否则会出错。
			}
			currExecuteInsertSql("动态数据初始化", initsqls_2);
			getUpdateSqls(tabname_deffkmap, alltabname_key, initsqls_update_2, allChangeTab, needUpdateSeqTab);
			currExecuteSql("动态数据外键更新", initsqls_update_2);
			if (initsqls_remcolumn_2.size() > 0) {
				setCurrSchedule("动态数据初始化删除伪列:共" + initsqls_remcolumn_2.size() + "张表");
			}
			for (int i = 0; i < initsqls_remcolumn_2.size(); i++) {
				currExecuteSql(null, (String) initsqls_remcolumn_2.get(i));
			}
		}
	}

	/**
	 * 序列更新
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
					table_primarykey.put(str_tname.toLowerCase(), str_tkey.toLowerCase()); // 表名-主键名
				} else {
					System.out.println("表" + str_tname + "在table.xml里未定义主键!后面逻辑会看表中是否存在id字段。");
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
							if (!table_primarykey.containsKey(str_tname.toLowerCase()) && "id".equals(columnname.toLowerCase())) { // 如果xml里未定义主键且列里面有id字段则认为id为主键
								table_primarykey.put(str_tname.toLowerCase(), columnname.toLowerCase());
								System.out.println("表" + str_tname + "在table.xml里未定义主键!但存在id字段则默认id为主键。");
							}
							if (reffield != null && !"".equals(reffield.trim()) && reffield.indexOf(".") > 0) { // pub_user.id的形式
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
				for (int o = 0; o < allxmlname.size(); o++) { //每一个xml只能存放一张表的数据。。
					org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder();
					org.jdom.Document doc = builder.build(this.getClass().getResourceAsStream(packname + allxmlname.get(o)));
					java.util.List recordNodeList = doc.getRootElement().getChildren("record"); //遍历所有记录!
					if (recordNodeList != null && recordNodeList.size() > 0) {
						String str_tableName = "";
						TableDataStruct tabstrct = null;
						HashSet<String> hstCols = null; // 存放对比字段,如果有再插入数据库
						for (int i = 0; i < recordNodeList.size(); i++) { //遍历各条记录!!!
							org.jdom.Element recordNode = (org.jdom.Element) recordNodeList.get(i);
							String tableName = recordNode.getAttributeValue("tabname"); //表名
							if (!tableName.equalsIgnoreCase(str_tableName)) { //加入判断，不用每次都去取
								str_tableName = tableName;
								tabstrct = commDMO.getTableDataStructByDS(_dsName, "select * from " + str_tableName + " where 1=2", false); //
								String[] str_cols = tabstrct.getHeaderName(); //
								hstCols = new HashSet<String>();
								for (int k = 0; k < str_cols.length; k++) {
									hstCols.add(str_cols[k].toLowerCase()); //
								}
							}

							if (str_tableName != null && alltabname_key.containsKey(str_tableName.toLowerCase())) { // 即必须在tables.xml里定义
								MyInsertSQlBuilder isql = new MyInsertSQlBuilder(str_tableName); //
								java.util.List colNodeList = recordNode.getChildren("col"); //
								for (int j = 0; j < colNodeList.size(); j++) { //遍历所有Col子结点!!即各列!!
									org.jdom.Element colNode = (org.jdom.Element) colNodeList.get(j);
									String str_colName = colNode.getAttributeValue("name"); //列名!
									String str_colValue = colNode.getText(); //列值!
									if (!hstCols.contains(str_colName.toLowerCase())) { //如果没有此值，跳过。
										continue;
									}
									if ((alltabname_key.get(str_tableName.toLowerCase()).toString().toLowerCase()).equals(str_colName.toLowerCase()) && !isCreateId) {
										allchangetab.put(str_tableName.toLowerCase(), str_colName.toLowerCase()); //
									}
									if ((alltabname_key.get(str_tableName.toLowerCase()).toString().toLowerCase()).equals(str_colName.toLowerCase())) { // 如果此列为主键
										if (isCreateId) {
											if (!allchangetab.containsKey(str_tableName.toLowerCase())) {
												allchangetab.put(str_tableName.toLowerCase(), str_colName.toLowerCase());
												addColumnSqls.add(" alter table " + str_tableName + " add column " + str_colName.toLowerCase() + "_oldid " + (dbType.equalsIgnoreCase("oracle") ? "varchar2" : "varchar") + "(100);");
												remvColumnSqls.add(" alter table " + str_tableName + " drop column " + str_colName.toLowerCase() + "_oldid;");
											}
											String str_newValue = getCommDMO().getSequenceNextValByDS(_dsName, "S_" + str_tableName.toUpperCase()); //从序列取得新的主键值!!!
											isql.setPK_Item_Value(str_colName, str_newValue);
											isql.putFieldValue(str_colName.toLowerCase() + "_oldid", str_colValue); // 增加伪列存储旧的ID
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
						setCurrSchedule("正在解析文件[" + allxmlname.get(o).toString().toLowerCase() + "]中的数据", !isCreateId ? 40 : 50, !isCreateId ? 50 : 60, 0, allxmlname.size());
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
	 * 获取包下面的所有xml文件名
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
				if ("jar".equals(protocol)) { // 如果是在jar里
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
				} else { // 如果是在class里
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
	 * @param refmap 由于id重新生成涉及到需要更新的表 表名-refid=pub_user.id;
	 * @param table_primarykey 表的主键map
	 * @return
	 */
	private void getUpdateSqls(HashMap refmap, HashMap table_primarykey, List updatesql, HashMap allChangeTab, HashMap allNeedUpdateSeqTab) {
		if (refmap.size() > 0 && allChangeTab.size() > 0) { // 如果存在依赖关系
			String[] tabls = (String[]) refmap.keySet().toArray(new String[0]);
			String refdes = null;
			String tab = null;
			String key = null;
			for (int i = 0; i < tabls.length; i++) {
				if (allChangeTab.containsKey(tabls[i].toLowerCase()) || allNeedUpdateSeqTab.containsKey(tabls[i].toLowerCase())) { // 如果此表在安装范围内
					refdes = (String) refmap.get(tabls[i]);
					HashMap refdesmap = tbutil.convertStrToMapByExpress(refdes, ";", "=");
					if (refdesmap != null && refdesmap.size() > 0) {
						String[] refcolumn = (String[]) refdesmap.keySet().toArray(new String[0]);
						for (int j = 0; j < refcolumn.length; j++) {
							String ref = (String) refdesmap.get(refcolumn[j]);
							if (ref.indexOf(".") > 0) {
								tab = tbutil.split(ref, ".")[0].trim().toLowerCase();
								key = tbutil.split(ref, ".")[1].trim().toLowerCase();
								if (key.equals(table_primarykey.get(tab)) && allChangeTab.containsKey(tab.toLowerCase())) { // 如果关联的这个表的字段是主键同时又是动态安装
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
			System.out.println("没有找到自定义类");
		}
		for (int i = 0; i < 20; i++) {
			setCurrSchedule(null, 90, 100, i, 20);
		}
	}

	//根据xml标签中的属性key获取一个hashvo
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

	//根据一个路径获取模块的信息
	public String[] getModuleInfo(String _path) throws Exception {
		return null;
	}

	//获取dmo
	private CommDMO getCommDMO() {
		if (commDMO == null) {
			commDMO = new CommDMO();
		}
		return commDMO;
	}

	//获取dmo
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
		schedule.push(new Object[] { str, -1f }); //-1不影响进度
	}

	public synchronized static void setCurrSchedule(String str, int _begin, int _end, int index, int _totle) {
		//		System.out.println("schedule2："+str);
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

	private static final String MODULEONOFFTYPETABLE = "TABLE"; //表板字段显示隐藏
	private static final String MODULEONOFFTYPETEMPLET = "TEMPLET"; //模板字段显示隐藏
	private static final String MODULEONOFFTYPETBUTTON = "BUTTON"; //按钮显示隐藏

	private boolean checkModuleOn_OFF(String key) throws Exception {
		if (key == null || key.equals("")) {
			return true;
		}
		if (ServerEnvironment.getInstance().get("moduleonoff") == null) {
			try {
				HashVO[] onoffvo = getCommDMO().getHashVoArrayByDS(null, "select * from pub_module_on_off");
				HashMap map = new HashMap();
				for (int i = 0; i < onoffvo.length; i++) {
					String onoff_key = onoffvo[i].getStringValue("type").toUpperCase() + "_" + onoffvo[i].getStringValue("mkey").toUpperCase(); //开关的键
					String onoff_value = onoffvo[i].getStringValue("mvalue").toUpperCase();
					if ("N".equalsIgnoreCase(onoff_value)) { //默认就是Y不必要加入
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
					System.out.println("表【pub_module_on_off】不存在，系统自动创建");
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
		//开关
	}

	private boolean notExist(Exception ex) {
		if (ex != null && (ex.getMessage().contains("doesn't exist") || ex.getMessage().contains("不存在") || ex.getMessage().contains("204") || ex.getMessage().contains("208"))) {
			return true;
		}
		return false;
	}

	//根据ID值刷新服务器端开关缓存
	public void refreshModuleOn_OffByIds(List _onoffIds) throws Exception {
		if (_onoffIds != null && _onoffIds.size() > 0) {
			HashMap map = (HashMap) ServerEnvironment.getInstance().get("moduleonoff");
			if (map == null) {
				checkModuleOn_OFF("-test");
				map = (HashMap) ServerEnvironment.getInstance().get("moduleonoff");
			}
			HashVO[] onoffvo = getCommDMO().getHashVoArrayByDS(null, "select * from pub_module_on_off where id in (" + tbutil.getInCondition(_onoffIds) + ")");
			for (int i = 0; i < onoffvo.length; i++) {
				String onoff_key = onoffvo[i].getStringValue("type").toUpperCase() + "_" + onoffvo[i].getStringValue("mkey").toUpperCase(); //开关的键
				String onoff_value = onoffvo[i].getStringValue("mvalue").toUpperCase();
				if ("N".equalsIgnoreCase(onoff_value)) { //默认就是Y不必要加入
					map.put(onoff_key, onoff_value); //
				} else {
					if (map.containsKey(onoff_key)) {
						map.remove(onoff_key); //如果是Y，并且包含，直接移走。
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

	//服务器启动时，默认启动初始化内容，在bootservlet中反射调用。
	public void initBootServlet() {
		initServicePool();
	}

	private void initServicePool() {
		try {
			long ll_1 = System.currentTimeMillis();
			// debugMsg("开始初始化SOA服务池...");
			String[][] modules = new BSUtil().getAllInstallPackages(null);
			if (modules == null || modules.length == 0) {
				return;
			}
			List moduleList = new ArrayList();
			HashVO logs[] = null; //已经安装或者其他安装历史。
			SAXBuilder builder = new SAXBuilder();
			for (int i = 0; i < modules.length; i++) {
				InputStream input = this.getClass().getResourceAsStream(modules[i][0] + "setting.xml"); //获取文件
				if (input == null) {
					continue;
				}
				Document dom = builder.build(input);
				if (dom != null) {
					String moduleName = dom.getRootElement().getAttributeValue("name"); //模块名称
					Element weblight = dom.getRootElement().getChild("weblight"); //在settting中配置weblight中参数。
					if (weblight != null) {
						List moduleService = weblight.getChildren("service"); //找到模块的服务
						if (moduleService != null && moduleService.size() > 0) {
							for (int m = 0; m < moduleService.size(); m++) {
								Element service = (Element) moduleService.get(m); //服务
								String str_servicename = service.getAttributeValue("name"); // 得到属性
								String str_implclass = service.getChild("implclass").getText(); //
								String str_initsize = service.getChild("initsize").getText(); //
								String str_maxsize = service.getChild("poolsize").getText(); //
								if (ServicePoolFactory.getInstance().getPool(str_servicename) != null) {
									logger.info("模块[" + moduleName + "]初始化SOA服务池[" + str_servicename + "]已经存在。");
									continue;
								}
								str_implclass = str_implclass.trim(); //
								int li_initsize = Integer.parseInt(str_initsize); //
								int li_maxsize = Integer.parseInt(str_maxsize); //
								ServicePoolableObjectFactory factory = new ServicePoolableObjectFactory(str_servicename, str_implclass); //
								GenericObjectPool pool = new GenericObjectPool(factory); // 创建对象池,最多100个实例!!!!
								pool.setMaxActive(li_maxsize); // 最大活动数
								pool.setMaxIdle(li_maxsize); // 最大句柄数
								try {
									for (int j = 0; j < li_initsize; j++) { // 先创建3个实例!!
										pool.addObject(); // 先创建一个实例
									}
								} catch (Exception ex) {
									ex.printStackTrace();
								}
								ServicePoolFactory.getInstance().registPool(str_servicename, str_implclass, pool); // 注册一个池!!
								logger.info("初始化SOA服务池[" + str_servicename + "][" + str_implclass + "]成功,共创建[" + str_initsize + "]个实例,执行模块[" + moduleName + "]");
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
		rootNodeVO.setAttributeValue("name", "表关系");
		rootNodeVO.setAttributeValue("parentid", "-9987");
		rootNodeVO.setAttributeValue("control", "");
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootNodeVO);
		for (int i = 0; i < modules.length; i++) {
			String _package_prefix = modules[i][0];
			InputStream input = this.getClass().getResourceAsStream(modules[i][0] + "setting.xml"); //获取文件
			DefaultMutableTreeNode moduleNode = null;
			if (input != null) { //如果文件实际存在
				org.jdom.Document doc = new org.jdom.input.SAXBuilder().build(input); // 加载XML.
				Element root = doc.getRootElement();
				HashVO module = getXmlNodeAttributeByKeys(root, "name", "code", "sn", "icon");
				module.setToStringFieldName("name");
				module.setAttributeValue("id", (-1 - i) + "");
				module.setAttributeValue("parentid", "-999999");
				module.setAttributeValue("control", "");
				moduleNode = new DefaultMutableTreeNode(module);
			} else {
				moduleNode = new DefaultMutableTreeNode("未找到模块名称");
			}
			String table_xmlfileName = _package_prefix + "database/tables.xml"; // 所有表
			HashMap tabname_deffkmap = new HashMap(); // 定义了外键的表 表名-userid=pub_user.id;deptid=pub_corp_dept.id;
			HashMap alltabname_key = new HashMap(); // 所有表名-主键
			getTable_PKAndFK(table_xmlfileName, tabname_deffkmap, alltabname_key);

			DefaultMutableTreeNode nodes[] = new DefaultMutableTreeNode[alltabname_key.size()];
			Iterator it = alltabname_key.entrySet().iterator();
			int index = 0;
			HashMap allNodeMap = new HashMap();
			Vector moreChildCopy = new Vector();//有的表外键太多。需要复制一批 
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				HashVO vo = new HashVO();
				vo.setAttributeValue("id", (i * 1000 + index) + "");
				vo.setToStringFieldName("name");
				vo.setAttributeValue("name", key);
				if (tabname_deffkmap.containsKey(key)) {
					vo.setAttributeValue("code", tabname_deffkmap.get(key));//pk主键
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
					String[] foreigns = tbutil.split(value, ";"); //所有外键
					if (foreigns != null && foreigns.length > 0) { //如果有多个外键。那么就复制出一批
						String one_foreign = foreigns[0]; //不管怎么的都把第一个认为是最主要的。需要在map中注册。下面会有一些复制出来的此节点，只能作为孩子。
						String str[] = tbutil.split(one_foreign, "."); //2个长度数组
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
							String one_foreign_1 = foreigns[k]; // 得到一个外键。 
							String str_1[] = tbutil.split(one_foreign_1, "."); //2个长度数组
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

	//得到java代码引用其他包
	//以后可以检测服务器端类调用ui代码，ui代码调用服务器端代码。
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
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'z', 'x', 'c' }; // 我们自己的钥串
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
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'z', 'x', 'c' }; // 我们自己的钥串
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

	//客户端反射专用
	public HashMap getXMlFromTable500RecordsByCondition(HashMap _map) throws Exception {
		String dotype = (String) _map.get("dotype"); //如果小于500条，直接查全部就可以了。500条用平台机制。dotype有两个值:0用传过来的sql。1用平台机制新sql。
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

	//导出500条记录。郝明从ipushgrc合规中迁过来。
	public HashMap getXMlFromTable500RecordsByCondition(String _dsName, String table, int _beginNo, String joinSql, String _condition) throws Exception {
		String _tableName = table;
		int li_batchRecords = 500; // 一次取500条记录!!!
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
			sb_sql_new.append(" * from " + _tableName + " " + joinSql + " " + _condition); // 将原来的select后面开始的内容接上来!
			sb_sql_new.append(") ");
			sb_sql_new.append("select top " + (li_batchRecords) + " * from _t1 where _rownum >= " + _beginNo + ""); // 分页!!!
			sql_sb.append(sb_sql_new.toString()); //
		}
		return getXMLMapDataBySQL(_dsName, sql_sb.toString(), _tableName, (_beginNo + li_batchRecords)); //
	}

	// 取内容
	private HashMap<String, Object> getXMLMapDataBySQL(String _dsName, String _sql, String _tabName, int _lastrow) throws Exception { // 
		StringBuilder sb_xml = new StringBuilder(); //
		sb_xml.append("<?xml version=\"1.0\" encoding=\"GBK\"?>\r\n"); //
		sb_xml.append("<root tablename=\"" + _tabName + "\">\r\n"); //
		HashVOStruct hvst = getCommDMO().getHashVoStructByDS(_dsName, _sql); //
		String[] str_keys = hvst.getHeaderName(); // 列名
		HashVO[] hvs = hvst.getHashVOs(); //
		String str_itemValue = null; //
		int li_returnRecordCount = 0; //
		for (int i = 0; i < hvs.length; i++) { // 遍历各行!!
			sb_xml.append("<!--" + (i + 1) + "-->\r\n"); //
			sb_xml.append("<record tabname=\"" + _tabName + "\">\r\n"); //
			for (int j = 0; j < str_keys.length; j++) { // 遍历各列!!
				if (str_keys[j].equalsIgnoreCase("RN"))
					continue; // 由于用RN来进行条数的范围过滤，所以多出RN无用列。
				str_itemValue = hvs[i].getStringValue(str_keys[j], ""); // 取得值!!
				if (str_itemValue == null || str_itemValue.trim().equals(""))
					continue;
				if (str_itemValue.indexOf("<") >= 0 || str_itemValue.indexOf(">") >= 0 || str_itemValue.indexOf("&") >= 0) { // 如果本身的<>
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
		returnMap.put("记录数", new Integer(li_returnRecordCount)); //
		returnMap.put("结束点", new Integer(_lastrow)); //
		returnMap.put("内容", sb_xml.toString()); // x
		return returnMap; // 返回新的id值!
	}

	//本次安装的内容进行撤销。目前只对insert数据进行管理 
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
				setCurrSchedule("撤销安装[" + tableName + "]表数据");
			}
			getCommDMO().executeBatchByDSImmediately(null, deleteSqlList);
		}
		return "本次安装已经撤销";
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
