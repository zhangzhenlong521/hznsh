package cn.com.infostrategy.bs.mdata;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import bsh.Interpreter;
import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.BSUtil;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.bs.sysapp.DataPolicyDMO;
import cn.com.infostrategy.bs.sysapp.install.InstallDMO;
import cn.com.infostrategy.bs.sysapp.install.quickInstall.QuickInstallDMO;
import cn.com.infostrategy.bs.sysapp.install.templetdata.TempletBuilderDMO;
import cn.com.infostrategy.to.common.CurrSessionVO;
import cn.com.infostrategy.to.common.DESKeyTool;
import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.HashVOComparator;
import cn.com.infostrategy.to.common.HashVOStruct;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.BillVOBuilder;
import cn.com.infostrategy.to.mdata.ButtonDefineVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.Pub_Templet_1ParVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemParVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.RowNumberItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.to.mdata.jepfunctions.JepFormulaParse;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.ui.common.WLTButton;

/**
 *元数据处理的DMO,关键类!!
 * @author xch
 * 
 */
public class MetaDataDMO extends AbstractDMO {

	private Logger logger = WLTLogger.getLogger(MetaDataDMO.class);
	private TBUtil tbUtil = new TBUtil(); //
	private CommDMO commDMO = new CommDMO(); //
	private static String templet_1_col = null;
	private static String templet_1_item_col = null;
	int limitcount = tbUtil.getSysOptionIntegerValue("列表最大查询条数", 2000); //太平项目提出需要导出列表，但因分页就只能查询出2000记录，故需要设置一个参数，必须设置为全局变量，否则应用服务启动报错【李春娟/2017-09-26】

	public MetaDataDMO() {
	}

	private CommDMO getCommDMO() {
		if (commDMO != null) {
			return commDMO; //
		}
		commDMO = new CommDMO(); //
		return commDMO; //
	}

	private TBUtil getTBUtil() {
		if (tbUtil != null) {
			return tbUtil; //
		}
		tbUtil = new TBUtil(); //
		return tbUtil; //
	}

	/**
	 * 从一个物理或或视图导入一个元原模板..
	 * 即根据表结构或视图结构创建一个模板!!
	 * @param _tableName
	 * @param _templetCode
	 * @param _templetName
	 * @throws Exception
	 */
	public void importOneTempletVO(String _tableName, String _templetCode, String _templetName) throws Exception {
		ArrayList al_sql = new ArrayList();
		String str_parentPkId = getCommDMO().getSequenceNextValByDS(null, "S_PUB_TEMPLET_1"); //
		InsertSQLBuilder isql_1 = new InsertSQLBuilder("pub_templet_1"); //
		isql_1.putFieldValue("pk_pub_templet_1", str_parentPkId); //
		isql_1.putFieldValue("templetcode", _templetCode); //
		isql_1.putFieldValue("templetname", _templetName); //
		isql_1.putFieldValue("templetname_e", _templetName); //
		isql_1.putFieldValue("tablename", _tableName); //查询的表名
		isql_1.putFieldValue("savedtablename", _tableName); //保存到的表名
		isql_1.putFieldValue("pkname", "id"); //主键字段名
		isql_1.putFieldValue("pksequencename", "S_" + _tableName.toUpperCase()); //主键对应的序列名
		isql_1.putFieldValue("cardwidth", "777"); //卡片宽度
		isql_1.putFieldValue("cardborder", "BORDER"); //卡片边框
		isql_1.putFieldValue("cardlayout", "FLOWLAYOUT"); //卡片布局方式
		isql_1.putFieldValue("isshowcardborder", "N"); //卡片是否显示边框,用于调试
		isql_1.putFieldValue("isshowlistpagebar", "N"); //列表是否显示分页,默认不显示
		isql_1.putFieldValue("isshowlistopebar", "N"); //列表是否显示操作按钮栏?
		isql_1.putFieldValue("cardsaveifcheck", "N"); //卡片保存前是否校验?
		isql_1.putFieldValue("treepk", "id"); //树型主键!
		isql_1.putFieldValue("treeparentpk", "parentid");
		isql_1.putFieldValue("treeviewfield", "name"); //
		isql_1.putFieldValue("treeseqfield", "seq"); //树的排序字段!
		isql_1.putFieldValue("treeisshowroot", "Y"); //树是否显示根结点?
		al_sql.add(isql_1.getSQL());

		//先要取得XML中的缓存!!
		HashMap map_xmlRegCol = new HashMap(); //
		String[][] str_tabColDesc = new InstallDMO().getAllIntallTabColumnsDescr(_tableName); //
		if (str_tabColDesc != null && str_tabColDesc.length > 0) {
			for (int i = 0; i < str_tabColDesc.length; i++) {
				map_xmlRegCol.put(str_tabColDesc[i][0], str_tabColDesc[i][1]); //
			}
		}
		String str_sql = "select * from " + _tableName + " where 1=2"; //查一下表!!!
		HashVOStruct hvs = getCommDMO().getHashVoStructByDS(null, str_sql); //
		String[] str_colid = hvs.getHeaderName(); //
		String[] str_coltype = hvs.getHeaderTypeName(); //
		int[] li_colLength = hvs.getHeaderLength(); //
		for (int i = 0; i < str_colid.length; i++) { //遍历各列
			String str_item_type = WLTConstants.COMP_TEXTFIELD; // 默认是文本框
			if (str_coltype[i].toUpperCase().startsWith("DECIMAL") || str_coltype[i].toUpperCase().startsWith("NUMBER")) { //如果数字框!
				str_item_type = WLTConstants.COMP_NUMBERFIELD;
			} else if (str_coltype[i].toUpperCase().startsWith("CHAR") || str_coltype[i].toUpperCase().startsWith("VARCHAR")) { //如果是字符型
				if (li_colLength[i] == 1) {
					str_item_type = WLTConstants.COMP_CHECKBOX; // 勾选框
				} else if (li_colLength[i] == 10) {
					str_item_type = WLTConstants.COMP_DATE; //
				} else if (li_colLength[i] == 19) {
					str_item_type = WLTConstants.COMP_DATETIME; //
					//字符且大于200多行文本框 【杨科/2013-03-14】 
				} else if (li_colLength[i] > 200) {
					str_item_type = WLTConstants.COMP_TEXTAREA;
				}
			}
			String str_childPkId = getCommDMO().getSequenceNextValByDS(null, "S_PUB_TEMPLET_1_ITEM"); //
			InsertSQLBuilder isql_2 = new InsertSQLBuilder("pub_templet_1_item"); //
			isql_2.putFieldValue("pk_pub_templet_1_item", str_childPkId); //
			isql_2.putFieldValue("pk_pub_templet_1", str_parentPkId); //
			isql_2.putFieldValue("itemkey", str_colid[i]); //
			if (map_xmlRegCol.containsKey(_tableName.toUpperCase() + "." + str_colid[i].toUpperCase())) { //如果XML注册中的有!
				isql_2.putFieldValue("itemname", (String) map_xmlRegCol.get(_tableName.toUpperCase() + "." + str_colid[i].toUpperCase())); //
			} else {
				isql_2.putFieldValue("itemname", str_colid[i]); //
			}
			isql_2.putFieldValue("itemname_e", str_colid[i]); //
			isql_2.putFieldValue("itemtype", str_item_type); //控制类型
			isql_2.putFieldValue("issave", "Y"); //是否参与保存
			isql_2.putFieldValue("ismustinput", "N"); //是否必须项
			isql_2.putFieldValue("showorder", (i + 1)); //
			isql_2.putFieldValue("listwidth", "125"); //列表宽度
			//isql_2.putFieldValue("cardwidth", "138"); //卡片宽度

			//字符且大于200卡片宽度400 【杨科/2013-03-14】 
			if ((str_coltype[i].toUpperCase().startsWith("CHAR") || str_coltype[i].toUpperCase().startsWith("VARCHAR")) && li_colLength[i] > 200) { //如果是字符型
				isql_2.putFieldValue("cardwidth", "400"); //卡片宽度 
			} else {
				//修改卡片默认宽度为140 【杨科/2013-03-13】 
				isql_2.putFieldValue("cardwidth", "140"); //卡片宽度 
			}

			isql_2.putFieldValue("listisshowable", "id".equalsIgnoreCase(str_colid[i]) ? "N" : "Y"); //列表不显示
			isql_2.putFieldValue("cardisshowable", "id".equalsIgnoreCase(str_colid[i]) ? "N" : "Y"); //卡片不显示
			isql_2.putFieldValue("listiseditable", "4"); //列表默认都不可编辑
			isql_2.putFieldValue("listisexport", "Y");//列表默认参与导出 
			isql_2.putFieldValue("cardiseditable", "1"); //列表默认都可编辑
			isql_2.putFieldValue("propisshowable", "Y");
			isql_2.putFieldValue("propiseditable", "Y");

			//字符且大于200换行下一行也换行 【杨科/2013-03-14】 
			if (i > 1 && (str_coltype[i - 1].toUpperCase().startsWith("CHAR") || str_coltype[i - 1].toUpperCase().startsWith("VARCHAR")) && li_colLength[i - 1] > 200) {
				isql_2.putFieldValue("iswrap", "Y"); //换行
			} else {
				if ((str_coltype[i].toUpperCase().startsWith("CHAR") || str_coltype[i].toUpperCase().startsWith("VARCHAR")) && li_colLength[i] > 200) { //如果是字符型
					isql_2.putFieldValue("iswrap", "Y"); //换行
				} else {
					isql_2.putFieldValue("iswrap", (i != 0 && i % 3 == 0) ? "Y" : "N"); //每三个控件换一行!!
				}
			}

			al_sql.add(isql_2.getSQL()); //
		}
		getCommDMO().executeBatchByDS(null, al_sql); // 提交数据库!!
	}

	/**
	 * 从数据库中根据模板编码创建模板VO
	 * 
	 * @param _code
	 * @return
	 * @throws Exception
	 */
	public Pub_Templet_1VO getPub_Templet_1VO(String _code) throws Exception {
		String str_sql = "select * from pub_templet_1 where templetcode='" + _code + "'"; //等于某个编码,为了保证索引起效果,去掉以前做的lower处理
		HashVO[] vos = getCommDMO().getHashVoStructByDS(null, str_sql).getHashVOs(); //永远启用预编译!! 因为这条SQL太频繁使用到了!
		if (vos != null && vos.length > 0) { //如果数据库存中有,则直接用数据库中的!!!
			HashVO parentVO = vos[0]; //
			String str_sql_item = "select * from pub_templet_1_item where pk_pub_templet_1=" + parentVO.getStringValue("Pk_pub_templet_1") + " order by showorder asc";
			HashVO[] hashVOs_item = getCommDMO().getHashVoStructByDS(null, str_sql_item).getHashVOs(); //
			return getPub_Templet_1VO(parentVO, hashVOs_item, "DB", _code); //如果是从数据库存取的
		} else { ///如果数据表中没有,则从XML取,以前是直接抛出一个异常,新的机制是从XML中取一下!!!
			String[][] str_allinstallpackage = new BSUtil().getAllInstallPackages("/templetdata"); //返回的包顺序如：WebPush平台、合规产品、内控产品、项目
			String xmlfileName = null;
			ArrayList list_xmlfile = new ArrayList();
			for (int i = 0; i < str_allinstallpackage.length; i++) {
				String str_xmlfileName = str_allinstallpackage[i][0] + _code + ".xml"; //转向去找XML
				java.net.URL fileUrl = this.getClass().getResource(str_xmlfileName); //
				if (fileUrl != null) { //如果的确有这个资源!!!则去取!
					logger.debug("在数据库中没有找到编码为[" + _code + "]的单据模板,转向成功从xml文件[" + str_xmlfileName + "]中取得!"); //
					xmlfileName = str_xmlfileName;
					list_xmlfile.add(str_allinstallpackage[i][1]);
				}

			}
			if (xmlfileName != null) {// 如资源中有【WebPush平台、合规产品、内控产品、项目】，则返回的模板路径的优先级： 项目>内控产品>合规产品>WebPush平台【李春娟/2012-07-18】
				AbstractTMO tmo = new TempletBuilderDMO().buildDefaultTMOFromXMLFile(xmlfileName); //从xml中构建DMO!! 是否需要做缓存?? 从XML文件取应该比从DB中取更快吧!! 
				if (list_xmlfile.size() > 1) {
					xmlfileName = list_xmlfile.remove(list_xmlfile.size() - 1) + ":" + xmlfileName + "(" + list_xmlfile.toString().substring(1, list_xmlfile.toString().length() - 1) + "中也有该模板)";
				}
				Pub_Templet_1VO templetVO = getPub_Templet_1VO(tmo.getPub_templet_1Data(), tmo.getPub_templet_1_itemData(), "XML2", xmlfileName); //构建,但是第二种XML,即先找表找不到,转向用XML的!!!
				templetVO.setTempletcode(_code); //曾经遇到过XML里面的模板编码与文件名的大小写不一致,为了保证一致,重设一下!!
				return templetVO; //
			}
			throw new WLTAppException("编码为[" + _code + "]的模板在数据库中没有,在XML也没发现,请与系统开发商联系!"); //如果一直没找到资源,则抛异常!!!
		}
	}

	/**
	*根据模板名称从服务器端找到XML模板TMO
	*fromType = 0/从数据库中获取   1/从xml中获取 2/优先取数据库，如果没有再去xml中。 3/直接从xml路径取
	*/
	public DefaultTMO getDefaultTMOByCode(String _code, int fromType) throws Exception {
		return getDefaultTMOByCode(_code, fromType, false);
	}

	public DefaultTMO getDefaultTMOByCode(String _code, int fromType, boolean issim) throws Exception {
		if (0 == fromType || 2 == fromType) {
			String str_sql = "select * from pub_templet_1 where templetcode='" + _code + "'"; //等于某个编码,为了保证索引起效果,去掉以前做的lower处理
			HashVO[] vos = getCommDMO().getHashVoStructByDS(null, str_sql).getHashVOs(); //永远启用预编译!! 因为这条SQL太频繁使用到了!
			if (vos != null && vos.length > 0) { //如果数据库存中有,则直接用数据库中的!!!
				HashVO parentVO = vos[0]; //
				String str_sql_item = "select * from pub_templet_1_item where pk_pub_templet_1=" + parentVO.getStringValue("Pk_pub_templet_1") + " order by showorder asc";
				HashVO[] hashVOs_item = getCommDMO().getHashVoStructByDS(null, str_sql_item).getHashVOs(); //
				return new DefaultTMO(parentVO, hashVOs_item);
			} else if (0 == fromType) {
				throw new WLTAppException("编码为[" + _code + "]的模板在数据库中没有,请与系统开发商联系!"); //如果一直没找到资源,则抛异常!!!
			}
		}
		if (1 == fromType || 2 == fromType) { //可能数据库中实在没有！
			String[][] str_allinstallpackage = new BSUtil().getAllInstallPackages("/templetdata"); //
			for (int i = str_allinstallpackage.length - 1; i >= 0; i--) {
				String str_xmlfileName = str_allinstallpackage[i][0] + _code + ".xml"; //转向去找XML
				java.net.URL fileUrl = this.getClass().getResource(str_xmlfileName); //
				if (fileUrl != null) { //如果的确有这个资源!!!则去取!
					logger.debug("在数据库中没有找到编码为[" + _code + "]的单据模板,转向成功从xml文件[" + str_xmlfileName + "]中取得!"); //
					DefaultTMO tmo = new TempletBuilderDMO().buildDefaultTMOFromXMLFile(str_xmlfileName); //从xml中构建DMO!! 是否需要做缓存?? 从XML文件取应该比从DB中取更快吧!! 
					return tmo; //
				}
			}

		}

		//追加3-直接从xml路径取 【杨科/2013-03-25】
		if (3 == fromType) {
			String str_xmlfileName = _code;
			if (!str_xmlfileName.startsWith("/")) { //如果前面没/则加上
				str_xmlfileName = "/" + str_xmlfileName;
			}

			_code = str_xmlfileName.substring(str_xmlfileName.lastIndexOf("/") + 1);
			_code = _code.substring(0, _code.lastIndexOf("."));

			java.net.URL fileUrl = this.getClass().getResource(str_xmlfileName);
			if (fileUrl != null) { //如果的确有这个资源!!!则去取!
				logger.debug("在数据库中没有找到编码为[" + _code + "]的单据模板,转向成功从xml文件[" + str_xmlfileName + "]中取得!");
				DefaultTMO tmo = new TempletBuilderDMO().buildDefaultTMOFromXMLFile(str_xmlfileName); //从xml中构建DMO!! 是否需要做缓存?? 从XML文件取应该比从DB中取更快吧!! 
				return tmo;
			}
		}

		if (1 == fromType || 3 == fromType) {
			throw new WLTAppException("编码为[" + _code + "]的模板在XML也没发现,请与系统开发商联系!"); //如果一直没找到资源,则抛异常!!!	
		} else {
			throw new WLTAppException("编码为[" + _code + "]的模板在数据库中没有,在XML也没发现,请与系统开发商联系!"); //如果一直没找到资源,则抛异常!!!
		}
	}

	/**
	 * 根据类名在服务器端创建模板定义VO,即TMO是放在服务器端,不需要在客户端下载
	 * @param _serverTMO
	 * @return
	 * @throws Exception
	 */
	public Pub_Templet_1VO getPub_Templet_1VO(ServerTMODefine _serverTMO) throws Exception {
		try {
			if (_serverTMO.getTmoClassName().toLowerCase().endsWith(".xml")) { //如果是一个xml文件
				AbstractTMO tmo = new TempletBuilderDMO().buildDefaultTMOFromXMLFile(_serverTMO.getTmoClassName()); //从xml中构建DMO!! 是否需要做缓存?? 从XML文件取应该比从DB中取更快吧!! 
				return getPub_Templet_1VO(tmo.getPub_templet_1Data(), tmo.getPub_templet_1_itemData(), "XML", _serverTMO.getTmoClassName()); //
			} else {
				String str_clsName = _serverTMO.getTmoClassName(); //类名!!
				AbstractTMO tmo = null; //
				if (_serverTMO.getConstructorPars() == null) { //如果没有构造方法
					tmo = (AbstractTMO) Class.forName(str_clsName).newInstance(); //类名
				} else {
					Class recCls = Class.forName(str_clsName); //类名
					Constructor clsCons = recCls.getConstructor(new Class[] { String[].class }); //取得构造方法!!必须是String[]类型!!Class.forName("[Ljava.lang.String;")
					tmo = (AbstractTMO) clsCons.newInstance(new Object[] { _serverTMO.getConstructorPars() }); //使用构造方法创建!!!
				}
				return getPub_Templet_1VO(tmo.getPub_templet_1Data(), tmo.getPub_templet_1_itemData(), "CLASS", "服务器端类:" + _serverTMO.getTmoClassName()); //根据类取的
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new WLTAppException("根据ServerTMODefine创建模板VO时发生异常,没有在服务器定义类【" + e.getMessage() + "】");
		} catch (ClassCastException ex) {
			ex.printStackTrace();
			throw new WLTAppException("非法的TMO对象【" + _serverTMO.getTmoClassName() + "】,必须继承于AbstractTMO");
		}

	}

	/**
	 * 直接根据两个HashVO创建模板VO
	 * 创建生成的模板VO应该有个创建类型,有三种取值:DB,CLASS(客户端/服务器端),XML,然后在进行模板编辑配置时需要判断一下! 如果是Class的则不可编辑(可查看)! 如果是xml则建议复制到DB中进行修改!!
	 * buildtype,buildinfo  
	 * @param _parentVO
	 * @param _childVOs
	 * @return
	 * @throws Exception
	 */
	public Pub_Templet_1VO getPub_Templet_1VO(HashVO _parentVO, HashVO[] _childVOs, String _buildFromType, String _buildFromInfo) throws Exception {
		Pub_Templet_1VO templet1VO = new Pub_Templet_1VO(); //// 处理模板主表!!
		templet1VO.setBuildFromType(_buildFromType); //是从什么类型创建生成的,比如DB,CLASS,XML
		templet1VO.setBuildFromInfo(_buildFromInfo); //是编译的类型说明!!
		templet1VO.setPk_pub_templet_1(_parentVO.getStringValue("Pk_Pub_Templet_1")); // 主键
		templet1VO.setTempletcode(_parentVO.getStringValue("Templetcode")); // 模板编码
		templet1VO.setTempletname(_parentVO.getStringValue("Templetname", "")); // 模板名称
		templet1VO.setTempletname_e(_parentVO.getStringValue("Templetname_e", "")); // 模板显示英文名称
		templet1VO.setTablename(_parentVO.getStringValue("Tablename")); // 表名
		templet1VO.setDatasourcename(_parentVO.getStringValue("Datasourcename")); // 数据源名称!!!!!!后增加的
		templet1VO.setDataconstraint(_parentVO.getStringValue("Dataconstraint")); // 数据权限!!!!!!后增加的
		templet1VO.setDataSqlAndOrCondition(_parentVO.getStringValue("DataSqlAndOrCondition", "and"));//sunfujun/20121119/增加条件与数据权限sql的拼接方式
		templet1VO.setAutoloadconstraint(_parentVO.getStringValue("autoloadconstraint")); //自动加载数据时的约束条件!!!!
		templet1VO.setOrdercondition(_parentVO.getStringValue("ordercondition")); // 设置排序条件
		templet1VO.setAutoLoads(_parentVO.getIntegerValue("AutoLoads", 0)); //自动加载的数据
		templet1VO.setDatapolicy(_parentVO.getStringValue("Datapolicy")); //数据权限策略!!
		templet1VO.setDatapolicymap(_parentVO.getStringValue("DatapolicyMap")); //数据权限策略的映射!!
		templet1VO.setPkname(_parentVO.getStringValue("Pkname")); // 主键名
		templet1VO.setPksequencename(_parentVO.getStringValue("Pksequencename")); // 主键对应序列名
		templet1VO.setTostringkey(_parentVO.getStringValue("tostringkey")); //ToString的字段名,以后可以扩展成支持公式或宏代码的样子!!
		templet1VO.setSavedtablename(_parentVO.getStringValue("Savedtablename")); // 保存数据的表名!!
		templet1VO.setBsdatafilterclass(_parentVO.getStringValue("bsdatafilterclass")); // BS端过滤器类
		templet1VO.setBsdatafilterissql(_parentVO.getBooleanValue("bsdatafilterissql", false)); //是否是SQL,默认不是SQL,即默认是Java设置是否可显示!!!
		templet1VO.setCardlayout(_parentVO.getStringValue("cardlayout", "FLOWLAYOUT")); //卡片布局!以后要增加多页签式的布局!!!因为SAP等许多系统都是这么做的,而且的确有不少客户喜欢多页签的风格!!
		templet1VO.setCardwidth(_parentVO.getIntegerValue("cardwidth", 520)); //卡片宽度
		templet1VO.setCardBorder(_parentVO.getStringValue("CardBorder", "BORDER")); // 是底线..
		templet1VO.setCardinitformula(_parentVO.getStringValue("cardinitformula")); // 卡片初始化公式....
		templet1VO.setIsshowcardborder(_parentVO.getBooleanValue("isshowcardborder", false)); // 是否显示卡片边框,为了在调整复杂页面时方便使用!
		templet1VO.setIsshowcardcustbtn(_parentVO.getBooleanValue("isshowcardcustbtn", false)); // 是否显示卡片的自定义按钮栏!
		templet1VO.setIsshowlistpagebar(_parentVO.getBooleanValue("isshowlistpagebar", false)); // 是否显示列表的分页栏
		templet1VO.setIslistpagebarwrap(_parentVO.getBooleanValue("islistpagebarwrap", false)); //列表中的换页是否换行?
		templet1VO.setIsshowlistopebar(_parentVO.getBooleanValue("isshowlistopebar", false)); // 是否显示列表的操作栏
		templet1VO.setIsshowlistcustbtn(_parentVO.getBooleanValue("isshowlistcustbtn", false)); // 是否显示列表的自定义按钮栏!
		templet1VO.setIsshowlistquickquery(_parentVO.getBooleanValue("isshowlistquickquery", false)); // 是否显示列表快速查询!!

		templet1VO.setIsshowcommquerybtn(_parentVO.getBooleanValue("isshowcommquerybtn", false)); // 不显示通用查询!!
		templet1VO.setIscollapsequickquery(_parentVO.getBooleanValue("iscollapsequickquery", false)); //是否收起快速查询框!!!,默认是展开的
		templet1VO.setListheadheight(_parentVO.getIntegerValue("listheadheight", 27)); // 列表表头高度,默认是27个像素的高度!!
		templet1VO.setListrowheight(_parentVO.getStringValue("listrowheight")); // 列表中行高
		templet1VO.setListheaderisgroup(_parentVO.getBooleanValue("listheaderisgroup", false)); // 列表的表头是否分组
		templet1VO.setIslistautorowheight(_parentVO.getBooleanValue("islistautorowheight", false)); // 列表的表头是否分组
		templet1VO.setListweidudesc(_parentVO.getStringValue("listweidudesc")); // 维度定义
		templet1VO.setDefineRenderer(_parentVO.getStringValue("definerenderer")); //自定义绘制器
		templet1VO.setListinitformula(_parentVO.getStringValue("listinitformula")); // 列表初始化公式....
		templet1VO.setCardcustbtndesc(_parentVO.getStringValue("cardcustbtndesc")); // 卡片自定义按钮说明,以分号隔开说明
		templet1VO.setCardsaveifcheck(_parentVO.getBooleanValue("cardsaveifcheck", false)); // 卡片保存是否验证必填
		templet1VO.setCardsaveselfdesccheck(_parentVO.getStringValue("cardsaveselfdesccheck")); // 卡片自定义验证
		templet1VO.setListcustbtndesc(_parentVO.getStringValue("listcustbtndesc")); // 列表自定义按钮说明,以分号隔开说明
		templet1VO.setListbtnorderdesc(getListBtnsOrderDesc(_parentVO.getStringValue("listbtnorderdesc")));//列表按钮排序分号分隔
		templet1VO.setTreecustbtndesc(_parentVO.getStringValue("treecustbtndesc")); // 树型自定义按钮说明,以分号隔开说明
		templet1VO.setCardcustbtns(getRegButtons(_parentVO.getStringValue("cardcustbtndesc"))); //卡片按钮
		templet1VO.setListcustbtns(getRegButtons(_parentVO.getStringValue("listcustbtndesc"))); //列表按钮
		templet1VO.setTreecustbtns(getRegButtons(_parentVO.getStringValue("treecustbtndesc"))); //树型按钮
		templet1VO.setCardcustpanel(_parentVO.getStringValue("Cardcustpanel")); // 卡片自定义面板的按钮!!
		templet1VO.setListcustpanel(_parentVO.getStringValue("Listcustpanel")); // 列表的自定义面板!!!		
		templet1VO.setTreepk(_parentVO.getStringValue("TREEPK")); // 树型面板的主键!
		templet1VO.setTreeparentpk(_parentVO.getStringValue("TREEPARENTPK")); // 树型面板的对应于父记录的外键!!
		templet1VO.setTreeviewfield(_parentVO.getStringValue("Treeviewfield")); // 树型面板显示的字段
		templet1VO.setTreeseqfield(_parentVO.getStringValue("Treeseqfield")); // 树型面板排序的字段
		templet1VO.setTreeisshowroot(_parentVO.getBooleanValue("Treeisshowroot")); // 树型面板是否显示根结点
		templet1VO.setTreeIsChecked(_parentVO.getBooleanValue("treeIsChecked", false)); //
		templet1VO.setTreeisonlyone(_parentVO.getBooleanValue("treeisonlyone", false)); //
		templet1VO.setTreeisshowtoolbar(_parentVO.getBooleanValue("treeisshowtoolbar", false)); //树型控件是否显示工具条,为空则表示不显示
		templet1VO.setPropbeanclassname(_parentVO.getStringValue("PROPBEANCLASSNAME")); // 属性面板的对应的JavaBean的类名

		templet1VO.setWfcustexport(_parentVO.getStringValue("wfcustexport")); // 树型面板排序的字段

		String str_templeteDatasourceName = null;
		if (templet1VO.getDatasourcename() != null && !templet1VO.getDatasourcename().trim().equals("")) {
			str_templeteDatasourceName = tbUtil.convertDataSourceName(getCurrSession(), templet1VO.getDatasourcename()); // 还要转换一下!
		}

		//找出取数表中所有的列!
		TableDataStruct strct_viewtable = null; //
		if (templet1VO.getTablename() != null && !templet1VO.getTablename().trim().equals("")) {
			try {
				strct_viewtable = getCommDMO().getTableDataStructByDS(str_templeteDatasourceName, "select * from " + templet1VO.getTablename() + " where 1=2", false); //为了减少控制台输出! 这条SQL不打印! 以后这个应该搞缓存的!!
				if (strct_viewtable != null) {
					templet1VO.setRealViewColumns(strct_viewtable.getHeaderName()); // 查询数据的视图中的所有列
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		//找出保存表中所有的列,如果保存表名与查询表名一样,则直接使用查询表的信息!否则会多查一次数据库,从而性能不好!
		//以后这两个SQL一定要使用缓存!! 因为表结构修改的可能性极低! 根本没必要每次都查一把!!! 但因为这两条SQL执行速度极快,所以目前还没有优化的迫切性!!!
		HashMap map_savedtableColDataTypes = new HashMap(); // 保存到表中的
		HashMap map_itemLength = new HashMap();
		if (templet1VO.getSavedtablename() != null && !templet1VO.getSavedtablename().trim().equals("")) {
			try {
				TableDataStruct strct_savetable = null; //
				if (templet1VO.getSavedtablename().equalsIgnoreCase(templet1VO.getTablename()) && strct_viewtable != null) { //如果保存表与查询表名一样,则直接拿查询表的信息!这样会少查询一次数据库!从而提高性能!!
					strct_savetable = strct_viewtable; //
				} else {
					strct_savetable = getCommDMO().getTableDataStructByDS(str_templeteDatasourceName, "select * from " + templet1VO.getSavedtablename() + " where 1=2", false); //保存
				}
				if (strct_savetable != null) {
					templet1VO.setRealSavedTableHaveColumns(strct_savetable.getHeaderName()); // 保存的表中的所有列名
					for (int i = 0; i < strct_savetable.getHeaderName().length; i++) {
						map_savedtableColDataTypes.put(strct_savetable.getHeaderName()[i].toUpperCase(), strct_savetable.getHeaderTypeName()[i].toUpperCase());//
						//map_itemLength.put(strct_savetable.getHeaderName()[i].toUpperCase(), strct_savetable.getHeaderLength()[i] + "");//旧逻辑
						map_itemLength.put(strct_savetable.getHeaderName()[i].toUpperCase(), strct_savetable.getPrecision()[i] + "**" + strct_savetable.getScale()[i]);//袁江晓 20140127 更改主要添加精度方面在输入的时候进行验证
					}
				}
			} catch (Exception ex) {
				System.err.println("计算模板定义中的保存表[" + templet1VO.getTablename() + "]的列信息时发生异常:" + ex.getClass().getName() + ":" + ex.getMessage()); //
			}
		}

		//遍历并处理模板子表！！！
		JepFormulaParseAtBS jepParse = new JepFormulaParseAtBS(true); //公式解析器!!
		ArrayList al_saveCols = new ArrayList();
		Pub_Templet_1_ItemVO[] itemVOS = new Pub_Templet_1_ItemVO[_childVOs.length]; //
		/***郝明添加，模块开发模板开关配置***/

		for (int i = 0; i < itemVOS.length; i++) {
			itemVOS[i] = new Pub_Templet_1_ItemVO();
			itemVOS[i].setId(_childVOs[i].getStringValue("pk_pub_templet_1_item")); // 主键!!
			itemVOS[i].setItemkey(_childVOs[i].getStringValue("Itemkey")); // 设置key
			itemVOS[i].setItemname(_childVOs[i].getStringValue("Itemname", ""));
			itemVOS[i].setItemname_e(_childVOs[i].getStringValue("Itemname_e", "")); //
			itemVOS[i].setItemtype(_childVOs[i].getStringValue("Itemtype", "文本框")); //设置类型
			boolean itemVisible = QuickInstallDMO.checkTempletItemVisible(templet1VO.getTempletcode(), templet1VO.getSavedtablename(), itemVOS[i].getItemkey());//模块功能配置显示
			//			boolean itemVisible = true;
			//处理控件保存前的数据宽度校验!!
			if (map_itemLength != null && map_itemLength.size() > 0) {
				if (map_itemLength.get(itemVOS[i].getItemkey().toUpperCase()) != null) {
					//itemVOS[i].setSaveLimit(Integer.parseInt((String) map_itemLength.get(itemVOS[i].getItemkey().toUpperCase())));//旧逻辑
					String templen = (String) map_itemLength.get(itemVOS[i].getItemkey().toUpperCase());//袁江晓 20140127 更改主要添加精度方面在输入的时候进行验证
					String[] str = getTBUtil().split(templen, "**");//在Oracle和mysql 下验证没有问题
					itemVOS[i].setSaveLimit(Integer.parseInt(str[0]));//不论是什么类型的都会有两个值precision和scale
					itemVOS[i].setSaveScale(Integer.parseInt(str[1]));//存放scale便于存储时浮点验证
				}
			}
			itemVOS[i].setItemtiptext(_childVOs[i].getStringValue("Itemtiptext")); //标签说明!!!
			itemVOS[i].setIssave(_childVOs[i].getBooleanValue("Issave", false)); // 是否参与保存条件!
			itemVOS[i].setIsencrypt(_childVOs[i].getBooleanValue("isencrypt", false)); //保存时是否加密
			itemVOS[i].setComboxdesc(_childVOs[i].getStringValue("Comboxdesc")); // 设置下拉框定义!!
			itemVOS[i].setRefdesc(_childVOs[i].getStringValue("Refdesc")); // 设置参照定义!!!!!关键之关键!!!
			itemVOS[i].setQueryItemType(_childVOs[i].getStringValue("queryitemtype")); // 查询控件类型,可以为空
			itemVOS[i].setQueryItemDefine(_childVOs[i].getStringValue("queryitemdefine")); // 查询控件定义,可以为空,如果为空,则用编辑时的
			itemVOS[i].setHyperlinkdesc(_childVOs[i].getStringValue("Hyperlinkdesc")); // 超链接定义
			itemVOS[i].setIsuniquecheck(_childVOs[i].getBooleanValue("isuniquecheck", false)); // 是否校验唯一性!
			itemVOS[i].setIsRefCanEdit(_childVOs[i].getBooleanValue("isrefcanedit", false)); // 是否必输入项!
			itemVOS[i].setIsmustinput2(_childVOs[i].getStringValue("Ismustinput", "N")); // 是否必输入项!
			itemVOS[i].setIskeeptrace(_childVOs[i].getBooleanValue("Iskeeptrace", false)); // 是否必输入项!
			itemVOS[i].setShowbgcolor(_childVOs[i].getStringValue("Showbgcolor")); // 前景/背景颜色
			itemVOS[i].setLoadformula(_childVOs[i].getStringValue("Loadformula"));
			itemVOS[i].setEditformula(_childVOs[i].getStringValue("Editformula"));
			itemVOS[i].setShoworder(_childVOs[i].getIntegerValue("Showorder")); // 是否显示边框..
			itemVOS[i].setListwidth(_childVOs[i].getIntegerValue("Listwidth", 85)); //列表模式下的列度,默认是85个像素!!
			itemVOS[i].setLabelwidth(new Integer(120));
			itemVOS[i].setCardwidth(new Integer(145)); //
			itemVOS[i].setCardHeight(new Integer(20)); //
			int[] li_wh_1 = getComponentWidthHeight(_childVOs[i].getStringValue("Cardwidth"), itemVOS[i].getItemtype()); //卡片中的控件宽度与高度!!
			if (li_wh_1 != null) {
				itemVOS[i].setLabelwidth(li_wh_1[0]); //
				itemVOS[i].setCardwidth(li_wh_1[1]); //
				itemVOS[i].setCardHeight(li_wh_1[2]); //
			}
			itemVOS[i].setListisshowable(itemVisible ? _childVOs[i].getBooleanValue("Listisshowable") : false); //
			itemVOS[i].setListiseditable(_childVOs[i].getStringValue("Listiseditable", "1")); // 列表是否可编辑
			itemVOS[i].setListishtmlhref(_childVOs[i].getBooleanValue("listishtmlhref", false)); // 列表是否是Html超链接显示..
			itemVOS[i].setListiscombine(_childVOs[i].getBooleanValue("listiscombine", false)); // 列表是否合并..
			itemVOS[i].setCardisshowable(itemVisible ? _childVOs[i].getBooleanValue("Cardisshowable") : false); //
			itemVOS[i].setCardisexport(_childVOs[i].getStringValue("cardisexport")); //卡片是否导出
			itemVOS[i].setListisexport(_childVOs[i].getBooleanValue("listisexport", true)); //列表是否导出,默认导出
			itemVOS[i].setCardiseditable(_childVOs[i].getStringValue("Cardiseditable", "1")); // 卡片是否可编辑
			itemVOS[i].setPropisshowable(itemVisible ? _childVOs[i].getBooleanValue("Propisshowable") : false); // 属性框中是否显示!!
			itemVOS[i].setPropiseditable(_childVOs[i].getBooleanValue("Propiseditable")); // 属性框中是否可编辑!!
			itemVOS[i].setDefaultvalueformula(_childVOs[i].getStringValue("Defaultvalueformula")); //
			itemVOS[i].setIswrap(_childVOs[i].getBooleanValue("iswrap", false)); // 是否换行!!
			itemVOS[i].setGrouptitle(_childVOs[i].getStringValue("grouptitle")); // 分组显示的标题
			int[] li_wh_2 = getComponentWidthHeight(_childVOs[i].getStringValue("querywidth"), itemVOS[i].getQueryItemType()); //查询面板中的控件宽度与高度!!!
			if (li_wh_2 != null) {
				itemVOS[i].setQuerylabelwidth(li_wh_2[0]); //
				itemVOS[i].setQuerycompentwidth(li_wh_2[1]); //
				itemVOS[i].setQuerycompentheight(li_wh_2[2]); //
			}
			itemVOS[i].setQuerydefaultformula(_childVOs[i].getStringValue("querydefaultformula")); // 查询的默认值公式
			itemVOS[i].setQuerycreatetype(_childVOs[i].getStringValue("querycreatetype")); //查询器创建类型..
			itemVOS[i].setQuerycreatecustdef(_childVOs[i].getStringValue("querycreatecustdef")); //查询器创建自定义...
			itemVOS[i].setIsQueryMustInput(_childVOs[i].getStringValue("Isquerymustinput"));
			itemVOS[i].setIsQuickQueryWrap(_childVOs[i].getBooleanValue("isQuickQueryWrap", false)); // 快速查询是否换行!!
			itemVOS[i].setIsQuickQueryShowable(_childVOs[i].getBooleanValue("isQuickQueryShowable", false)); // 快速查询是否显示!!
			itemVOS[i].setIsQuickQueryEditable(_childVOs[i].getBooleanValue("isQuickQueryEditable", false)); // 快速查询是否可编辑!!!
			itemVOS[i].setIsCommQueryWrap(_childVOs[i].getBooleanValue("isCommQueryWrap", false)); // 通用查询是否换行!!
			itemVOS[i].setIsCommQueryShowable(_childVOs[i].getBooleanValue("isCommQueryShowable", false)); // 通用查询是否显示!!
			itemVOS[i].setIsCommQueryEditable(_childVOs[i].getBooleanValue("isCommQueryEditable", false)); // 通用查询是否可编辑!!
			itemVOS[i].setWorkflowiseditable(_childVOs[i].getBooleanValue("Workflowiseditable", false)); // 快速查询是否可编辑!!!
			itemVOS[i].setQueryeditformula(_childVOs[i].getStringValue("queryeditformula")); // 查询的编辑公式
			itemVOS[i].setSavedcolumndatatype((String) map_savedtableColDataTypes.get(itemVOS[i].getItemkey().toUpperCase())); //
			if (WLTConstants.COMP_COMBOBOX.equals(itemVOS[i].getItemtype())) { //如果是下拉框,则需要立即计算出控件的构造数据,因为下拉框与参照不一样,参照需要在客户端点击一样才
				itemVOS[i].setComBoxItemVos(getComBoxItemVOs(itemVOS[i].getComboxdesc(), jepParse, itemVOS[i], 1)); //
			}
			if (WLTConstants.COMP_COMBOBOX.equals(itemVOS[i].getQueryItemType())) { // 如果查询框的类型是下拉框,则也要构造出其下拉框数据!!
				itemVOS[i].setQueryComBoxItemVos(getComBoxItemVOs(itemVOS[i].getQueryItemDefine(), jepParse, itemVOS[i], 2)); //
			}

			if (itemVOS[i].getIssave().booleanValue()) { //如果参与保存,则记录下来!
				al_saveCols.add(itemVOS[i].getItemkey()); //
			}

			//处理CommUI的公式,生成CommUCDefineVO对象!!!
			//即后来将各种公式统一成一个标准的公式,然后在创建模板VO时就直接生成,像参照等控件本来是可以在UI端点击时计算定义对象的,但因为像上传文件,引用子表,上传图片等控件,其实是一开始就需要取得控件定义VO的,所以为了统一,则直接在服务器端就生成!
			//而有种需求,就是在UI端动态计算控件的定义属性,比如SQL语句的过滤条件,即一个控件的选择项发生变化,会影响另一个控件的SQL过滤条件,解决办法是在UI端搞一个公式,可以直接修改控件中的CommUCDefineVO中的配置项的值!! 这样就彻底统一的解决了这种复杂问题!而不需要在UI端计算定义VO了
			if (itemVOS[i].getRefdesc() != null && !itemVOS[i].getRefdesc().trim().equals("")) {
				CommUCDefineVO uCDfVO = getCommUCDefineVOByFormula(itemVOS[i].getRefdesc().trim(), itemVOS[i].getItemtype(), jepParse); //
				if (uCDfVO.getTypeName().equals("注册参照")) { //如果是注册参照,则再查询一下数据库,以前是在前台做的,但后来机制修改过后,感觉必须放在后台！【徐长华/2012-08-15】
					String str_regName = uCDfVO.getConfValue("注册名称"); //
					String[][] str_regdef = commDMO.getStringArrayByDS(null, "select reftype,refdefine from pub_refregister where name='" + str_regName + "'"); //
					if (str_regdef.length > 0) {
						uCDfVO = getCommUCDefineVOByFormula(str_regdef[0][1], str_regdef[0][0], jepParse); //重新解析定义!
						uCDfVO.setConfValue("★提醒说明★", "这本是个注册参照,是根据注册名<" + str_regName + ">再次查找到的"); //加个这个提醒,省得有人认为错了！
						itemVOS[i].setItemtype(str_regdef[0][0]); //重新定义类型!
						itemVOS[i].setRefdesc(str_regdef[0][1]); //重新定义注册参照中指定的参照定义!
					} else {
						uCDfVO = new CommUCDefineVO("注册参照"); //
						uCDfVO.setConfValue("解析控件公式发生异常", "根据注册参照名称<" + str_regName + ">在表pub_refregister中没有找到定义!"); //
					}
				}
				itemVOS[i].setUCDfVO(uCDfVO); //
				if ("下拉框".equals(uCDfVO.getTypeName()) && (itemVOS[i].getComboxdesc() == null || itemVOS[i].getComboxdesc().trim().equals(""))) { //如果类型是下拉框,则创建下拉框数据!之所以这么做是想以后去掉下拉框定义这个字段,统一成一个控件定义字段!!
					itemVOS[i].setComBoxItemVos(getComBoxItemVOsByCommUCDfVO(uCDfVO)); //
				}
			}

			if (itemVOS[i].getQueryItemDefine() != null && !itemVOS[i].getQueryItemDefine().trim().equals("")) { //如果查询框有定义,
				CommUCDefineVO uCDfVO = getCommUCDefineVOByFormula(itemVOS[i].getQueryItemDefine().trim(), itemVOS[i].getQueryItemType(), jepParse); //以前有个Bug就是参数用的是控件类型,而不是查询控件类型!
				if (uCDfVO.getTypeName().equals("注册参照")) { //如果是注册参照,则再查询一下数据库
					String str_regName = uCDfVO.getConfValue("注册名称"); //
					String[][] str_regdef = commDMO.getStringArrayByDS(null, "select reftype,refdefine from pub_refregister where name='" + str_regName + "'"); //
					if (str_regdef.length > 0) {
						uCDfVO = getCommUCDefineVOByFormula(str_regdef[0][1], str_regdef[0][0], jepParse); //重新解析定义!
						uCDfVO.setConfValue("★提醒说明★", "这本是个注册参照,是根据注册名<" + str_regName + ">再次查找到的"); //
						itemVOS[i].setQueryItemType(str_regdef[0][0]); //重新定义类型!
						itemVOS[i].setQueryItemDefine(str_regdef[0][1]); //重新定义注册参照中指定的参照定义!
					} else {
						uCDfVO = new CommUCDefineVO("注册参照"); //
						uCDfVO.setConfValue("解析控件公式发生异常", "根据注册参照名称<" + str_regName + ">在表pub_refregister中没有找到定义!"); //
					}
				}
				itemVOS[i].setQueryUCDfVO(uCDfVO); //设置控件定义VO!
				if ("下拉框".equals(itemVOS[i].getQueryItemType())) { //如果类型是下拉框,则创建下拉框数据!之所以这么做是想以后去掉下拉框定义这个字段,统一成一个控件定义字段!!
					ComBoxItemVO[] ppp = getComBoxItemVOsByCommUCDfVO(uCDfVO); //
					itemVOS[i].setQueryComBoxItemVos(ppp); //
				}
			}
			itemVOS[i].setPub_Templet_1VO(templet1VO); //为子表绑定主表!!
		}
		templet1VO.setItemVos(itemVOS); //为主表绑定子表!!
		templet1VO.setRealSavedTableColumns((String[]) al_saveCols.toArray(new String[0])); // 真正保存数据库存的列!!!做insert,update,拼SQL时就直接从这个数组转出来!!
		return templet1VO; //
	}

	/**
	 * 根据控件公式定义取得控件定久对象CommUCDefineVO
	 */
	private CommUCDefineVO getCommUCDefineVOByFormula(String _refdesc, String _itemtype, JepFormulaParseAtBS _jepParse) {
		try {
			if (_itemtype.equals(WLTConstants.COMP_FILECHOOSE) && !_refdesc.startsWith("getCommUC(")) { //如果是文件选择框并且不是新的语法,则采用旧的解析! 虽然这样的点变态,但为了兼容旧的语法,实在没办法!
				CommUCDefineVO uCDfVO = new CommUCDefineVO(WLTConstants.COMP_FILECHOOSE); //
				HashMap confMap = getTBUtil().convertStrToMapByExpress(_refdesc); //
				uCDfVO.setConfValueAll(confMap); //直接加入!!
				return uCDfVO;
			} else if (_itemtype.equals(WLTConstants.COMP_SELFDESC) && !_refdesc.startsWith("getCommUC(")) { //如果是自定义控件,且不是新的语法!
				String[] str_fs = getTBUtil().split(_refdesc, ";"); //
				CommUCDefineVO uCDfVO = new CommUCDefineVO(WLTConstants.COMP_SELFDESC); //自定义控件!!
				uCDfVO.setConfValue("卡片中的类", str_fs[0]); //
				uCDfVO.setConfValue("列表渲染器", str_fs[1]); //
				uCDfVO.setConfValue("列表编辑器", str_fs[2]); //这里可能有数组溢出异常!!
				return uCDfVO;
			} else if (_itemtype.equals(WLTConstants.COMP_COMBOBOX) && !_refdesc.startsWith("getCommUC(")) {
				CommUCDefineVO uCDfVO = new CommUCDefineVO(WLTConstants.COMP_COMBOBOX); //自定义控件!!
				uCDfVO.setConfValue("SQL语句", _refdesc); //
				return uCDfVO;
			} else if (_itemtype.equals(WLTConstants.COMP_BUTTON) && !_refdesc.startsWith("getCommUC(")) { // 为了支持原有逻辑直接配个类路径
				CommUCDefineVO uCDfVO = new CommUCDefineVO(WLTConstants.COMP_BUTTON); //自定义控件!!
				uCDfVO.setConfValue("点击事件", _refdesc); //
				return uCDfVO;
			} else {
				//_refdesc = tbUtil.replaceAll(_refdesc, "\r", ""); //不要替换,因为我发现JEP本身能自动处理后面带//备注的情况,如果一替换反而不行了!!!
				Object obj = _jepParse.execFormula(_refdesc); //执行公式
				if (obj instanceof CommUCDefineVO) {
					return (CommUCDefineVO) obj; //
				} else {
					CommUCDefineVO uCDfVO = new CommUCDefineVO(_itemtype); //
					uCDfVO.setConfValue("解析控件公式失败", "没有成功创建CommUCDefineVO对象,得到的对象类型是[" + (obj == null ? "null" : obj.getClass()) + "],对象值是[" + obj + "]"); //
					return uCDfVO; //
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace(); //
			CommUCDefineVO uCDfVO = new CommUCDefineVO(_itemtype); //
			uCDfVO.setConfValue("解析控件公式发生异常", "异常名:" + ex.getClass().getName() + ",异常说明:" + ex.getMessage()); //
			return uCDfVO; //
		}
	}

	//根据下拉框的定义构造下拉框内容
	private ComBoxItemVO[] getComBoxItemVOs(String _comboxdesc, JepFormulaParse _jep, Pub_Templet_1_ItemVO _pub_Templet_1_ItemVO, int _type) {
		if (_comboxdesc == null || _comboxdesc.trim().equals("")) {
			return null; //
		}
		try {
			_comboxdesc = _comboxdesc.trim(); //
			if (_comboxdesc.startsWith("=>")) { //如果不是SQL语句而是直接调用反射类,比如:=>cn.com.infostrategy.bs.sysapp.PubComboBoxDictDefine.getMenuCommandType(),则根据公式直接取值
				String str_clsName = _comboxdesc.substring(2, _comboxdesc.length()).trim(); //后面的类名
				if (str_clsName.endsWith(";")) {
					str_clsName = str_clsName.substring(0, str_clsName.length() - 1);
				}
				String[][] str_data = (String[][]) getTBUtil().reflectCallMethod(str_clsName); //反射调用!!!
				if (str_data != null) {
					ComBoxItemVO[] comBoxItemVOs = new ComBoxItemVO[str_data.length];
					for (int i = 0; i < str_data.length; i++) {
						comBoxItemVOs[i] = new ComBoxItemVO(str_data[i][0], str_data[i][1], str_data[i][2]);
					}
					return comBoxItemVOs; //
				}
			} else if (_comboxdesc.startsWith("getCommUC(")) { //如果是新的控件定义!
				_comboxdesc = tbUtil.replaceAll(_comboxdesc, "\r", ""); //将换行去掉!!
				_comboxdesc = tbUtil.replaceAll(_comboxdesc, "\n", ""); //
				//System.out.println("解析公式[" + _comboxdesc + "]");  //
				CommUCDefineVO uCDfVO = (CommUCDefineVO) _jep.execFormula(_comboxdesc); //执行公式
				if (uCDfVO != null) {
					if (_type == 1) {
						_pub_Templet_1_ItemVO.setUCDfVO(uCDfVO); //
					} else if (_type == 2) {
						_pub_Templet_1_ItemVO.setQueryUCDfVO(uCDfVO); //设置控件定义VO!
					}
					return getComBoxItemVOsByCommUCDfVO(uCDfVO); //
				}
			} else { //直接是字符串,即最老的直接写个SQL就搞定了!!
				String[] str_array = tbUtil.split(_comboxdesc, ";");
				String str_comboxitem_sql = str_array[0]; //
				String str_datasourcename = null;
				if (str_array.length == 1) {
					str_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName(); // 默认数据源!!
				} else if (str_array.length == 2) {
					str_datasourcename = tbUtil.convertDataSourceName(getCurrSession(), getDataSourceName(str_array[1])); //还可以指定数据源!!
				}
				String modify_str = tbUtil.convertComboBoxDescSQL(str_comboxitem_sql, getCurrSession()); //
				HashVO[] hvs = getCommDMO().getHashVoArrayByDS(str_datasourcename, modify_str); // 根据下拉框中的SQL取得下拉框中所有数据的HashVO,可能会有异常!!
				ComBoxItemVO[] comBoxItemVOs = new ComBoxItemVO[hvs.length];
				for (int i = 0; i < hvs.length; i++) {
					comBoxItemVOs[i] = new ComBoxItemVO(hvs[i]); //
				}
				return comBoxItemVOs; //
			}
			return null; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		}
	}

	//根据新的通用标准的控件定义VO创建下拉框数据!!
	private ComBoxItemVO[] getComBoxItemVOsByCommUCDfVO(CommUCDefineVO _uCDfVO) throws Exception {
		try {
			if (_uCDfVO.containsConfKey("直接值")) {
				String str_item = _uCDfVO.getConfValue("直接值"); //
				String[] str_items = tbUtil.split(str_item, ";"); //
				ComBoxItemVO[] comBoxItemVOs = new ComBoxItemVO[str_items.length];
				for (int i = 0; i < comBoxItemVOs.length; i++) {
					comBoxItemVOs[i] = new ComBoxItemVO(str_items[i], str_items[i], str_items[i]); //
				}
				return comBoxItemVOs; //
			} else if (_uCDfVO.containsConfKey("SQL语句")) { //如果是SQL语句
				HashVO[] hvs = getCommDMO().getHashVoArrayByDS(_uCDfVO.getConfValue("数据源名称"), _uCDfVO.getConfValue("SQL语句")); // 根据下拉框中的SQL取得下拉框中所有数据的HashVO,可能会有异常!!
				ComBoxItemVO[] comBoxItemVOs = new ComBoxItemVO[hvs.length]; //相撞
				for (int i = 0; i < hvs.length; i++) {
					comBoxItemVOs[i] = new ComBoxItemVO(hvs[i]); //直接创建!!
				}
				return comBoxItemVOs; //
			} else if (_uCDfVO.containsConfKey("反射类")) { //通过反射类
				String str_clsName = _uCDfVO.getConfValue("反射类"); //
				if (str_clsName.endsWith(";")) {
					str_clsName = str_clsName.substring(0, str_clsName.length() - 1);
				}
				String[][] str_data = (String[][]) getTBUtil().reflectCallMethod(str_clsName); //反射调用!!!
				if (str_data != null) {
					ComBoxItemVO[] comBoxItemVOs = new ComBoxItemVO[str_data.length];
					for (int i = 0; i < str_data.length; i++) {
						comBoxItemVOs[i] = new ComBoxItemVO(str_data[i][0], str_data[i][1], str_data[i][2]);
					}
					return comBoxItemVOs; //
				}
			}
			return null;
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null; //
		}
	}

	/**
	 * 取得注册按钮的定义
	 * @param _btndesc
	 * @return
	 * @throws Exception
	 */
	public ButtonDefineVO[] getRegButtons(String _btndesc) throws Exception {
		if (_btndesc == null || _btndesc.trim().equals("")) { //如果为空,则直接返回空!!
			return null;
		}
		String[] str_codes = _btndesc.split(";"); //
		ButtonDefineVO[] btns = new ButtonDefineVO[str_codes.length]; // 创建几个按钮.
		HashVO[] hvs = getCommDMO().getHashVoArrayByDS(null, "select * from pub_regbuttons where code in (" + tbUtil.getInCondition(str_codes) + ")"); // 去注册按钮表中找所有的按钮
		Hashtable ht_btns = new Hashtable();
		for (int i = 0; i < hvs.length; i++) {
			ht_btns.put(hvs[i].getStringValue("code"), hvs[i]); // 将找到的按钮放到哈希表中!因为数据迁移的问题,可能会造成在模板中定义,但在按钮注册表中却没有!!!
		}

		//遍历各个按钮!
		String[][] str_allregbtns = WLTButton.getSysRegButtonType(); //
		for (int i = 0; i < str_codes.length; i++) {
			btns[i] = new ButtonDefineVO(str_codes[i]); //
			if (str_codes[i].startsWith("$")) { //如果是$开头的,则不需要注册在系统表中!
				String str_btntype = str_codes[i].substring(1, str_codes[i].length()); //
				btns[i].setBtntype(str_btntype); //
				btns[i].setBtntext(str_btntype); //
				for (int j = 0; j < str_allregbtns.length; j++) {
					if (str_allregbtns[j][0].equals(str_btntype)) {
						btns[i].setBtntext(str_allregbtns[j][1]); //
						btns[i].setBtnimg(str_allregbtns[j][3]); //
						break; //
					}
				}
			} else {
				if (ht_btns.containsKey(str_codes[i])) { //如果注册了!
					HashVO hvBtn = (HashVO) ht_btns.get(str_codes[i]); //
					btns[i].setBtntype(hvBtn.getStringValue("btntype")); //
					btns[i].setBtntext(hvBtn.getStringValue("btntext")); //
					btns[i].setBtnimg(hvBtn.getStringValue("btnimg")); ////
					btns[i].setBtntooltiptext(hvBtn.getStringValue("btntooltiptext")); //
					btns[i].setBtnpars(hvBtn.getStringValue("btnpars")); //按钮参数
					btns[i].setClickingformula(hvBtn.getStringValue("clickingformula")); //
					btns[i].setClickedformula(hvBtn.getStringValue("clickedformula")); //
					btns[i].setAllowposts(hvBtn.getStringValue("allowposts")); //
					btns[i].setAllowroles(hvBtn.getStringValue("allowroles")); //
					btns[i].setAllowroletype(hvBtn.getStringValue("allowroletype")); //
					btns[i].setAllowusers(hvBtn.getStringValue("allowusers")); //
					btns[i].setAllowusertype(hvBtn.getStringValue("allowusertype")); //
					btns[i].setAllowifcbyinit(hvBtn.getStringValue("allowifcbyinit")); //BS端初始化时的拦截器!
					btns[i].setAllowifcbyclick(hvBtn.getStringValue("allowifcbyclick")); //UI端点击或选择变化时的拦截器!
					btns[i].setBtndescr(hvBtn.getStringValue("btndescr")); //
					btns[i].setRegisterBtn(true); //是注册型的按钮!!!
				} else { //
					btns[i].setBtntext("没注册"); //
					btns[i].setBtndescr("在系统中没有找到Code为[" + str_codes[i] + "]的注册按钮");
					btns[i].setRegisterBtn(true); //是注册型的按钮
				}
			}
		}
		MetaDataBSUtil bsUtil = new MetaDataBSUtil();
		HashMap sharePoolMap = new HashMap(); //

		CurrSessionVO sessionVO = new WLTInitContext().getCurrSession();
		String str_loginUserId = (sessionVO == null ? null : sessionVO.getLoginUserId()); //在工作流导出报表时,因为不是默认的远程调用,所以sessionVO为空,结果报空指针异常!! 故加此判断!【xch/2012-11-09】
		if (str_loginUserId != null) {
			for (int i = 0; i < btns.length; i++) {
				bsUtil.setWltBtnAllow(btns[i], str_loginUserId, sharePoolMap); //
			}
		}
		return btns; //
	}

	private String[] getListBtnsOrderDesc(String str_listorder) {
		if (str_listorder == null || str_listorder.trim().equals("")) {
			return null;
		}
		return getTBUtil().split(str_listorder, ";");
	}

	//取得控件宽度与高度
	private int[] getComponentWidthHeight(String str_widthstr, String _itemType) {
		try {
			if (str_widthstr == null || str_widthstr.trim().equals("")) {
				return null;
			}
			int[] li_return = new int[] { 120, 138, 20 }; //默认
			int li_pos_1 = str_widthstr.indexOf(","); //
			String str_widthstr2 = str_widthstr; //
			if (li_pos_1 >= 0) { // 如果指定了label的宽度
				li_return[0] = Integer.parseInt(str_widthstr.substring(0, li_pos_1)); //
				str_widthstr2 = str_widthstr.substring(li_pos_1 + 1, str_widthstr.length()); //
			}
			int li_pos_2 = str_widthstr2.indexOf("*"); //看有没有高度!
			if (li_pos_2 >= 0) {
				li_return[1] = Integer.parseInt(str_widthstr2.substring(0, li_pos_2)); //
				li_return[2] = Integer.parseInt(str_widthstr2.substring(li_pos_2 + 1, str_widthstr2.length())); //
			} else {
				li_return[1] = Integer.parseInt(str_widthstr2); //宽度
				if ("多行文本框".equals(_itemType)) { //如果是多行文本框,则高度是
					li_return[2] = 75; //
				}
			}
			return li_return; //
		} catch (Exception _ex) {
			System.err.println("计算控件高度【" + str_widthstr + "】时发生异常:" + _ex.getClass().getName() + ":" + _ex.getMessage()); //
			return null; //
		}
	}

	/**
	 * 取得BillListData
	 * 
	 * @param _sql
	 * @param _templetVO
	 * @param _env
	 * @return
	 * @throws Exception
	 */
	public Object[] getBillCardDataByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName();
		}
		Object[][] objs_formuls = getBillListDataByDS(_datasourcename, _sql, _templetVO); // 执行加载公式后的值
		if (objs_formuls != null && objs_formuls.length > 0) {
			return objs_formuls[0]; // 返回第一行
		} else {
			return null;
		}
	}

	/**
	 * 生成数据
	 * 
	 * @param _datasourcename
	 * @param _sql
	 * @param _templetCode
	 * @return
	 * @throws Exception
	 */
	public BillVOBuilder getBillVOBuilder(String _datasourcename, String _sql, String _templetCode) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName();
		}
		Pub_Templet_1VO templetVO = getPub_Templet_1VO(_templetCode); //
		return getBillVOBuilder(_datasourcename, _sql, templetVO); //
	}

	/**
	 * 创建billVO Builder
	 * 
	 * @param _datasourcename
	 * @param _sql
	 * @param _templetVO
	 * @return
	 * @throws Exception
	 */
	public BillVOBuilder getBillVOBuilder(String _datasourcename, String _sql, Pub_Templet_1VO _templetVO) throws Exception { //
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName();
		}
		TableDataStruct tbs = getCommDMO().getTableDataStructByDS(_datasourcename, _sql); //
		BillVOBuilder billVOBuilder = new BillVOBuilder(); //
		billVOBuilder.setTableDataStruct(tbs); //
		billVOBuilder.setTempletVO(_templetVO); //
		return billVOBuilder; //
	}

	public BillVO[] getBillVOsByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO) throws Exception {
		return getBillVOsByDS(_datasourcename, _sql, _templetVO, true); //
	}

	/**
	 * 获得BillVO数组....
	 * 
	 * @return
	 * @throws Exception
	 */
	public BillVO[] getBillVOsByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO, boolean _isExecLoadFormula) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName(); //
		}
		Pub_Templet_1_ItemParVO[] templetItemVOs = _templetVO.getItemVos(); // 各子项.
		int li_length = templetItemVOs.length;

		// 先取得所有数据
		Object[][] objs = getBillListDataByDS(_datasourcename, _sql, _templetVO, _isExecLoadFormula); // 取得所有数据!!

		BillVO[] billVOs = new BillVO[objs.length]; //
		for (int r = 0; r < billVOs.length; r++) {
			billVOs[r] = new BillVO();
			billVOs[r].setQueryTableName(_templetVO.getTablename()); // 设置查询表
			billVOs[r].setSaveTableName(_templetVO.getSavedtablename()); // 设置保存表
			billVOs[r].setPkName(_templetVO.getPkname()); // 设置主键字段名
			billVOs[r].setSequenceName(_templetVO.getPksequencename()); // 序列名

			// 所有ItemKey
			String[] all_Keys = new String[li_length + 1]; //
			all_Keys[0] = "_RECORD_ROW_NUMBER"; // 行号
			for (int i = 1; i < all_Keys.length; i++) {
				all_Keys[i] = _templetVO.getItemKeys()[i - 1];
			}

			// 所有的名称
			String[] all_Names = new String[li_length + 1]; //
			all_Names[0] = "行号"; // 行号
			for (int i = 1; i < all_Names.length; i++) {
				all_Names[i] = _templetVO.getItemNames()[i - 1];
			}

			String[] all_Types = new String[li_length + 1]; //
			all_Types[0] = "行号"; // 行号
			for (int i = 1; i < all_Types.length; i++) {
				all_Types[i] = _templetVO.getItemTypes()[i - 1];
			}

			String[] all_ColumnTypes = new String[li_length + 1]; //
			all_ColumnTypes[0] = "NUMBER"; // 行号
			for (int i = 1; i < all_ColumnTypes.length; i++) {
				all_ColumnTypes[i] = templetItemVOs[i - 1].getSavedcolumndatatype(); //
			}

			boolean[] bo_isNeedSaves = new boolean[li_length + 1];
			bo_isNeedSaves[0] = false; // 行号
			for (int i = 1; i < bo_isNeedSaves.length; i++) {
				bo_isNeedSaves[i] = templetItemVOs[i - 1].isNeedSave();
			}

			billVOs[r].setKeys(all_Keys); // 设置所有的key
			billVOs[r].setNames(all_Names); // 设置所有的Name
			billVOs[r].setItemType(all_Types); // 控件类型!!设置所有的类型!!
			billVOs[r].setColumnType(all_ColumnTypes); // 数据库类型!!
			billVOs[r].setNeedSaves(bo_isNeedSaves); // 是否需要保存!!
			billVOs[r].setToStringFieldName(_templetVO.getTostringkey()); //
			billVOs[r].setDatas(objs[r]); // 设置真正的数据!!
		}

		// 执行数据过滤器!!!
		if (_templetVO.getBsdatafilterclass() != null && !_templetVO.getBsdatafilterclass().trim().equals("")) { // 如果有数据拦截器
			ArrayList v_filters = new ArrayList();
			IBsDataFilter_IFC filter = (IBsDataFilter_IFC) Class.forName(_templetVO.getBsdatafilterclass()).newInstance(); //
			for (int i = 0; i < billVOs.length; i++) {
				if (filter.filterBillVO(billVOs[i])) { // 如果通过过滤器这一关!!!则加入
					v_filters.add(billVOs[i]); //
				}
			}
			return (BillVO[]) v_filters.toArray(new BillVO[0]); //
		}

		return billVOs; //
	}

	/**
	 * 取得树型结构的数据,树型与表型结构不一样,树型要一下子全部取出来,所以返回给客户端的数据要小,必须在客户端构造数据!!!
	 * @return
	 */
	public TableDataStruct getBillTreeData(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName(); ////
		}
		if (_templetVO.getBsdatafilterclass() != null) { //
			if (_templetVO.getBsdatafilterissql()) { //如果BS端过滤器是返回的SQL
				String str_sqlcons = getFilterByBSFilterSQLCons(_templetVO.getBsdatafilterclass()); //
				if (str_sqlcons != null && !str_sqlcons.trim().equals("")) {
					int li_orderpos = _sql.toLowerCase().indexOf("order by"); //
					if (li_orderpos > 0) { //如果原来有order by,则要在中间插入!!!
						_sql = _sql.substring(0, li_orderpos) + " and (" + str_sqlcons + ") " + _sql.substring(li_orderpos, _sql.length()); //
					} else {
						_sql = _sql + " and (" + str_sqlcons + ") ";
					}
				}
			}
		}

		HashVOStruct hvsStruct = getCommDMO().getHashVoStructByDS(_datasourcename, _sql); //取得真正的数据,是HashVO!!!
		HashVO[] hashVOs = hvsStruct.getHashVOs(); ////先取得所有数据,如果是分页的话,只会返回某一页的数据!!!

		//第一次过滤!!
		if (_templetVO.getBsdatafilterclass() != null && !_templetVO.getBsdatafilterissql()) { //如果定义了过滤器,则进行过滤!的方法是通过设置HashVO的setVisible()来实现!
			hashVOs = filterByBSFilter(hashVOs, _templetVO.getBsdatafilterclass()); //
		}

		TableDataStruct tds = new TableDataStruct(); //
		tds.setTableName(hvsStruct.getTableName()); //
		tds.setHeaderName(hvsStruct.getHeaderName()); //
		tds.setHeaderType(hvsStruct.getHeaderType()); //
		tds.setHeaderTypeName(hvsStruct.getHeaderTypeName()); //
		tds.setHeaderLength(hvsStruct.getHeaderLength()); //

		String[][] tds_bodyData = new String[hashVOs.length][hvsStruct.getHeaderName().length]; //
		for (int i = 0; i < tds_bodyData.length; i++) {
			for (int j = 0; j < tds_bodyData[i].length; j++) {
				tds_bodyData[i][j] = hashVOs[i].getStringValue(hvsStruct.getHeaderName()[j]); //
			}
		}
		tds.setBodyData(tds_bodyData); //设置表体数据
		return tds; //
	}

	/**
	 * 取得BS端过滤器中返回的SQL!
	 * @param _bsFilterDefine
	 * @return
	 */
	private String getFilterByBSFilterSQLCons(String _bsFilterDefine) {
		try {
			if (_bsFilterDefine.startsWith("@")) { //直接某个类名与方法名
				String str_realClassName = _bsFilterDefine.substring(1, _bsFilterDefine.length()); //
				str_realClassName = getTBUtil().replaceAll(str_realClassName, "\r", ""); //
				str_realClassName = getTBUtil().replaceAll(str_realClassName, "\n", ""); //
				String str_sqlcons = (String) getTBUtil().reflectCallMethod(str_realClassName); //反射调用!!
				return str_sqlcons; //
			} else { //使用BSH公式执行!!!!必须是Bsh2.4才能执行!!!
				Interpreter inter = new Interpreter(); //创建BSH拦截器
				String str_sqlcons = (String) inter.eval(_bsFilterDefine); //执行公式!!!
				return str_sqlcons; //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null; //
		}

	}

	/**
	 * 使用BS端过滤器过滤!!!
	 * @param _hashVOs
	 * @param _bsFilterDefine
	 */
	private HashVO[] filterByBSFilter(HashVO[] _hashVOs, String _bsFilterDefine) {
		try {
			if (_bsFilterDefine.startsWith("@")) {
				String str_realClassName = _bsFilterDefine.substring(1, _bsFilterDefine.length()); //
				str_realClassName = this.tbUtil.replaceAll(str_realClassName, "\r", ""); //
				str_realClassName = this.tbUtil.replaceAll(str_realClassName, "\n", ""); //
				BillDataBSFilterIFC bsfilter = (BillDataBSFilterIFC) Class.forName(str_realClassName).newInstance(); //
				bsfilter.filterBillData(_hashVOs); //实际过滤
			} else { //使用BSH公式执行!!!!必须是Bsh2.4才能执行!!!
				Interpreter inter = new Interpreter(); //创建BSH拦截器
				inter.set("_hvs", _hashVOs); //送入参数!!!
				inter.eval(_bsFilterDefine); //执行公式!!! 
			}

			//只输出显示的!!!!
			ArrayList al_tempHvs = new ArrayList(); //
			for (int i = 0; i < _hashVOs.length; i++) {
				if (_hashVOs[i].isVisible()) { //只有设置了可以显示,才可以加入!
					al_tempHvs.add(_hashVOs[i]); //
				}
			}
			return (HashVO[]) al_tempHvs.toArray(new HashVO[0]); //重新赋值!!!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return _hashVOs; //
		}
	}

	public Object[][] getBillListDataByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO) throws Exception {
		return getBillListDataByDS(_datasourcename, _sql, _templetVO, true, null); //
	}

	public Object[][] getBillListDataByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO, boolean _isExecLoadFormula) throws Exception {
		return getBillListDataByDS(_datasourcename, _sql, _templetVO, true, null); //
	}

	public Object[][] getBillListDataByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO, boolean _isExecLoadFormula, int[] _rowArea) throws Exception {
		return getBillListDataByDS(_datasourcename, _sql, _templetVO, _isExecLoadFormula, _rowArea, false); //
	}

	/**
	 * 取数据!!!
	 * @param _datasourcename
	 * @param _sql
	 * @param _templetVO
	 * @param _isExecLoadFormula
	 * @param _rowArea
	 * @param _isRegHVOinRowNumberVO
	 * @param isps
	 * @return
	 * @throws Exception
	 */
	public Object[][] getBillListDataByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO, boolean _isExecLoadFormula, int[] _rowArea, boolean _isRegHVOinRowNumberVO) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName(); ////
		}
		//实际查询数据库! 压力测试时就是在这产生问题!
		WLTInitContext initContext = new WLTInitContext(); //
		CurrSessionVO sessionVO = initContext.getCurrSession(); //
		boolean isLRCall = false; //
		if (sessionVO != null && sessionVO.isLRCall()) {
			isLRCall = true; //判断出是LR在调用!! 如果是LV在调用,而且又是处于忙碌状态,则新开线程去访问!!然后直接返回一个假数据!!!
			//反之,如果是直接访问,则永远直接查数据库! 或者说虽然是LR访问,但只要不是太忙,则也是直接查询数据库!
			//现在与以前的区别是,如果使用LR单测,即使是是否真忙的算法有问题,因为是单开线程查询数据库,即实际是也是会查询数据库的,即根本就看不出问题!!
			//为了保证效果唯妙唯肖,应该Sleep一把!!
		}

		boolean isRealBusy = new BSUtil().isRealBusiCall(); //判断是否真下的忙!!很关键!如果误算成忙是不要紧的! 就怕该忙的没有算出来!!

		//以前默认最大查询2000条
		boolean isUpperLimit = false;
		if (limitcount > 0) {
			isUpperLimit = true;
		}

		if (_templetVO.getBsdatafilterclass() != null) { //如果有数据权限过滤器,则强行不使用SQL分页!!!而是查出所有数据!! 以后应该优化,将过滤器送入,然后遍历时满足20条就直接返回! 而不是先查出所有,然后再过滤!
			if (_templetVO.getBsdatafilterissql()) { //如果BS端过滤器是返回的SQL
				String str_sqlcons = getFilterByBSFilterSQLCons(_templetVO.getBsdatafilterclass()); //
				if (str_sqlcons != null && !str_sqlcons.trim().equals("")) {
					int li_orderpos = _sql.toLowerCase().indexOf("order by"); //
					if (li_orderpos > 0) { //如果原来有order by,则要在中间插入!!!
						_sql = _sql.substring(0, li_orderpos) + " and (" + str_sqlcons + ") " + _sql.substring(li_orderpos, _sql.length()); //
					} else {
						_sql = _sql + " and (" + str_sqlcons + ") ";
					}
				}
				initContext.addCurrSessionForClientTrackMsg("发现BS过滤器返回的是SQL,且取得的条件是[" + str_sqlcons + "]\r\n"); //
			} else {
				_rowArea = null; //
				isUpperLimit = false; //是否限制上限!!
			}
		}

		HashVOStruct hvsStruct = null; //如果能有种两全其美的办法,解决BS端过滤与SQL分页过滤就好了!!!即功能与性能的完美组合! 有待突破!!!
		String str_fromtabName = null; //
		int li_from_pos = _sql.toLowerCase().indexOf("from"); //
		if (li_from_pos > 0) {
			String str_tmp = _sql.substring(li_from_pos + 4, _sql.length()).trim(); //
			int li_blank_pos = str_tmp.indexOf(" "); //第一个空格,因为是 from pub_user where 1=1
			if (li_blank_pos > 0) {
				str_fromtabName = str_tmp.substring(0, li_blank_pos).toLowerCase(); //表名!
			}
		}

		//如果是LR调用,且指定
		if (ServerEnvironment.isPageFalsity && isLRCall && isRealBusy && _rowArea != null && str_fromtabName != null && ServerEnvironment.pageFalsityMapData.containsKey(str_fromtabName)) { //如果是LR在调用,而且的确很忙!而且缓存中有
			new FalsityThread().getHashVoStructByDS(_datasourcename, _sql, true, true, _rowArea); //先开一个线程去查!保证数据库是有负载的!!
			ServerEnvironment.newThreadCallCount++; //
			hvsStruct = (HashVOStruct) ServerEnvironment.pageFalsityMapData.get(str_fromtabName); //从缓存取直接返回!
			Thread.currentThread().sleep(ServerEnvironment.falsitySleep); //休息600毫秒!!形成效果!!!即也不能太快!!
			//System.out.println("新开线程,休息了[" + ServerEnvironment.falsitySleep + "]"); //
		} else { //否则是单线程直接取!!
			hvsStruct = getCommDMO().getHashVoStructByDS(_datasourcename, _sql, true, isUpperLimit, _rowArea, true, false); //取得真正的数据,是HashVO!!!列表查询是做上限控制的! 即当数据量太多时逼着分页! new int[] {1,20},{21-40}
			if (ServerEnvironment.isPageFalsity && !ServerEnvironment.pageFalsityMapData.containsKey(str_fromtabName)) { //如果指定搞虚拟的,而且虚拟缓存中没有该表! 则置入!!
				ServerEnvironment.pageFalsityMapData.put(str_fromtabName, hvsStruct); //
			}
		}
		HashVO[] hashVOs = hvsStruct.getHashVOs(); //得到所有列表数据!!

		//第一次过滤!!
		if (_templetVO.getBsdatafilterclass() != null && !_templetVO.getBsdatafilterissql()) { //如果定义了过滤器,则进行过滤!的方法是通过设置HashVO的setVisible()来实现!
			int li_oldcount = hashVOs.length; //
			hashVOs = filterByBSFilter(hashVOs, _templetVO.getBsdatafilterclass()); //使用Java设置是否可显示来进行过滤!!!
			initContext.addCurrSessionForClientTrackMsg("BS过滤器类型是setVisiable,过滤前记录数是[" + li_oldcount + "]条,过滤后是[" + hashVOs.length + "]条\r\n"); //
		}

		//包装成一个方法!!因为后来遇到一种情况,即不是通过SQL创建HashVO[],而是直接创建HashVO[],比如将XML中的数据来加载数据!!!
		Object[][] valueData = getBillListDataByHashVOs(_templetVO, hashVOs, hvsStruct.getTotalRecordCount(), _isRegHVOinRowNumberVO);

		Object[][] returnObjects = null; //实际返回值!!
		if (_isExecLoadFormula) { // 如果需要执行加载公式
			//long ll_a = System.currentTimeMillis();  //
			returnObjects = execLoadformula(hashVOs, valueData, _templetVO, getCurrSession()); //执行加载公式!!这是最容易产生瓶颈的地方,如果机构与人员不做缓存则性能更严重,一般来说,一个定义了5个公式,实际执行100次的处理，不做缓存需要800多毫秒,做缓存后则只需要15毫秒!!!
			//long ll_b = System.currentTimeMillis();  //
			//System.out.println("***************执行加载公式耗时[" + (ll_b - ll_a) + "]"); //
		} else {
			returnObjects = valueData;
		}
		//以前在这里使用Java排序的,后来在招行项目中发现性能测试根本过不去!!!所以放弃了!
		return returnObjects; //
	}//

	/**
	 * 直接根据模板VO与HashVO[]得到实际数据!在直接打开XML的地方用到!!
	 */
	public Object[][] getBillListDataByHashVOs(Pub_Templet_1ParVO _templetVO, HashVO[] _hashVOs) {
		return getBillListDataByHashVOs(_templetVO, _hashVOs, _hashVOs.length, false); //转调!!
	}

	/**
	 * 这个方法是真正将一个HashVO[]数据根据模板VO中定义的控件类型转换真正的模板格式的数据!!!
	 * @param _templetVO
	 * @param _hashVOs
	 * @param totalRecordCount
	 * @param _isRegHVOinRowNumberVO
	 * @return
	 */
	private Object[][] getBillListDataByHashVOs(Pub_Templet_1ParVO _templetVO, HashVO[] _hashVOs, int totalRecordCount, boolean _isRegHVOinRowNumberVO) {
		Pub_Templet_1_ItemParVO[] itemVos = _templetVO.getItemVos(); // 各子项.
		String[] itemKeys = _templetVO.getItemKeys(); // 各项的key
		String[] itemTypes = _templetVO.getItemTypes(); // 各项的类型
		boolean[] isEncrypts = _templetVO.getItemIsEncrypt(); //各项是否需要加密存储!!!
		int li_length = _templetVO.getItemKeys().length; // 总共有多少列,加上行号,还应多一列!!

		//将HashVO的数据组装成BillVO,最耗时的地方,在没有加载公式的情况下,占整个耗时的80%左右!!!
		//但由于是纯内存计算,所以性能是还是相当高的,像一个20行30列共有600次处理的结构,有参照等各种类型,一般耗时是16毫秒左右!即一行一列的处理是0.02毫秒,一行记录的处理是0.8毫秒!!!
		Object[][] valueData = new Object[_hashVOs.length][li_length + 1]; // 创建数据对象!!!,比模板多一列是行号,永远在第一列!!
		String str_key = null;
		String str_type = null;
		String str_value = null;
		String str_datetimevalue = null;
		DESKeyTool desTool = new DESKeyTool(); //DES加密与解密工具!!!
		for (int i = 0; i < _hashVOs.length; i++) { // 遍历各行
			Object[] rowobjs = new Object[li_length + 1]; // 一行的数据
			RowNumberItemVO rowNumberVO = new RowNumberItemVO(); // 行号VO
			rowNumberVO.setTotalRecordCount(totalRecordCount); //设置这批记录的实际的总共记录数!
			rowNumberVO.setState(WLTConstants.BILLDATAEDITSTATE_INIT); //
			if (_isRegHVOinRowNumberVO) {
				rowNumberVO.setRecordHVO(_hashVOs[i]); //如果需要在行号中注册HashVO,则处理一下!!
			}
			//rowNumberVO.setRecordIndex(i); // 记录号
			rowobjs[0] = rowNumberVO; // 第一列永远是行号VO

			// 然后再处理每一列的值
			for (int j = 0; j < li_length; j++) { // 遍历各列
				str_key = itemKeys[j]; //
				str_type = itemTypes[j]; //类型
				str_value = _hashVOs[i].getStringValue(str_key); // 先取得出实际值!!!
				if (str_value != null && !str_value.trim().equals("")) { //
					//如果这个字段是加密的,则还要做解密处理!!!
					if (isEncrypts[j]) { //如果是加密存储
						str_value = desTool.decrypt(str_value); //解密一下!!!
					}
					if (str_type.equals(WLTConstants.COMP_TEXTFIELD) || //文本框
							str_type.equals(WLTConstants.COMP_LABEL) || //label
							str_type.equals(WLTConstants.COMP_NUMBERFIELD) || //数字框
							str_type.equals(WLTConstants.COMP_PASSWORDFIELD) || //密码框
							str_type.equals(WLTConstants.COMP_TEXTAREA) || //多行文本框
							str_type.equals(WLTConstants.COMP_BUTTON) || //按钮
							str_type.equals(WLTConstants.COMP_CHECKBOX)) { //勾选框 
						rowobjs[j + 1] = new StringItemVO(str_value); //
					} else if (str_type.equals(WLTConstants.COMP_COMBOBOX)) { // 如果是下拉框
						ComBoxItemVO matchVO = findComBoxItemVO(itemVos[j].getComBoxItemVos(), str_value); //处理下拉框是里面最易耗时的地方
						if (matchVO != null) {
							rowobjs[j + 1] = matchVO;
						} else {
							rowobjs[j + 1] = new ComBoxItemVO(str_value, null, str_value); // 下拉框VO
						}
					} else if (str_type.equals(WLTConstants.COMP_REFPANEL) || //表型参照
							str_type.equals(WLTConstants.COMP_REFPANEL_TREE) || //树型参照
							str_type.equals(WLTConstants.COMP_REFPANEL_MULTI) || //多选参照
							str_type.equals(WLTConstants.COMP_REFPANEL_CUST) || //自定义参照
							str_type.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET) || //列表模板参照
							str_type.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || //树型模板参照
							str_type.equals(WLTConstants.COMP_REFPANEL_REGFORMAT) || //注册样板参照
							str_type.equals(WLTConstants.COMP_REFPANEL_REGEDIT) || //注册参照!!
							str_type.equals(WLTConstants.COMP_BIGAREA) || //大文本框
							str_type.equals(WLTConstants.COMP_COLOR) || //颜色
							str_type.equals(WLTConstants.COMP_CALCULATE) || //计算器
							str_type.equals(WLTConstants.COMP_PICTURE) || //图片
							str_type.equals(WLTConstants.COMP_LINKCHILD) || //引用子表
							str_type.equals(WLTConstants.COMP_IMPORTCHILD) //导入子表 
					) {
						if (str_value != null && str_value.startsWith(";")) { //由于后来有大量的多选参照,前面会有一个分号,干脆在这里直接干掉算了!
							String str_viewvalue = str_value.substring(1, str_value.length()); //
							rowobjs[j + 1] = new RefItemVO(str_value, null, str_viewvalue); // 
						} else {
							rowobjs[j + 1] = new RefItemVO(str_value, null, str_value); // 先用取得的值直接赋给某个参照
						}
					} else if (str_type.equals(WLTConstants.COMP_FILECHOOSE)) { //文件选择框
						String str_refId = str_value; //
						String[] str_items = tbUtil.split(str_refId, ";"); //
						StringBuilder sb_names = new StringBuilder(); //
						for (int ff = 0; ff < str_items.length; ff++) {
							sb_names.append(getViewFileName(str_items[ff])); //
							if (ff != str_items.length - 1) {
								sb_names.append(";");
							}
						}
						rowobjs[j + 1] = new RefItemVO(str_refId, null, sb_names.toString()); ////
					} else if (str_type.equals(WLTConstants.COMP_DATE)) { //日历
						str_datetimevalue = _hashVOs[i].getStringValueForDay(str_key);
						rowobjs[j + 1] = new RefItemVO(str_datetimevalue, null, str_datetimevalue); //itemTypes[j].equals(WLTConstants.COMP_FILECHOOSE) || //文件选择框
					} else if (str_type.equals(WLTConstants.COMP_DATETIME)) {
						str_datetimevalue = _hashVOs[i].getStringValueForSecond(str_key);
						rowobjs[j + 1] = new RefItemVO(str_datetimevalue, null, str_datetimevalue); //
					} else if (str_type.equals(WLTConstants.COMP_EXCEL)) {
						rowobjs[j + 1] = new RefItemVO(str_value, null, (str_value.indexOf("#") > 0 ? str_value.substring(str_value.indexOf("#") + 1, str_value.length()) : "点击查看Excel数据")); //
					} else if (str_type.equals(WLTConstants.COMP_OFFICE)) { //
						String str_viewName = "点击查看"; //默认叫随机生成的文件名!以前是叫点击查看!! 但后来有了上传功能后!会生成16进制的文件名!
						int li_markcount = getTBUtil().findCount(str_value, "_"); //
						if (li_markcount == 0) { //王永龙导数据时,搞了一堆没有前辍的!!
							String str_masterName = null, str_extentName = null; //主名与扩展名
							int li_dot_pos = str_value.lastIndexOf("."); //看有没有点,如果有,则说明是有扩展名的!
							if (li_dot_pos > 0) { //如果有扩展名!
								str_masterName = str_value.substring(0, li_dot_pos); //文件主名!!
								str_extentName = str_value.substring(li_dot_pos, str_value.length()); //扩展名!!比如[.doc][.xls][.pdf]
							} else {
								str_masterName = str_value; //文件主名!!即全部了
								str_extentName = ""; //扩展名为空串
							}
							boolean isHexNo = getTBUtil().isHexStr(str_masterName); //
							if (isHexNo) {
								str_viewName = tbUtil.convertHexStringToStr(str_masterName) + str_extentName; //
							}
						} else if (li_markcount == 1) { //如果只有一个下划线,即极可能不是随机生成的!因为随机生成的是好几个下划线
							boolean isStartWith_N = str_value.startsWith("N"); //
							String str_serialNo = str_value.substring((isStartWith_N ? 1 : 0), str_value.indexOf("_")); //下划线前面,N符号后面的序号!!
							String str_masterName = null, str_extentName = null; //主名与扩展名
							int li_dot_pos = str_value.lastIndexOf("."); //看有没有点,如果有,则说明是有扩展名的!
							if (li_dot_pos > 0) { //如果有扩展名!
								str_masterName = str_value.substring(str_value.indexOf("_") + 1, li_dot_pos); //文件主名!!
								str_extentName = str_value.substring(li_dot_pos, str_value.length()); //扩展名!!比如[.doc][.xls][.pdf]
							} else {
								str_masterName = str_value.substring(str_value.indexOf("_") + 1, str_value.length()); //文件主名!!即全部了
								str_extentName = ""; //扩展名为空串
							}
							boolean isRealNo = getTBUtil().isStrAllNunbers(str_serialNo); //是否是数字
							boolean isHexNo = getTBUtil().isHexStr(str_masterName); //
							if (isRealNo && isHexNo) { //如果前辍是数字,且主名是16进制,则肯定是要转换的了!!严格判断!!
								str_viewName = tbUtil.convertHexStringToStr(str_masterName) + str_extentName; //
							}
						}
						rowobjs[j + 1] = new RefItemVO(str_value, null, str_viewName); //
					} else {
						rowobjs[j + 1] = new StringItemVO(str_value); //
					}
				} else { // 如果没取到数,则为空!
					rowobjs[j + 1] = null; //
				}
			} // 一行数据中的各列全部处理处理结束!!!

			valueData[i] = rowobjs; //
		} // end for 所有行数据处理结束!! 以前曾以前上面这段逻辑有性能问题! 但后来证实根本没有太多问题,再次证明纯内存逻辑计算是非常快的!

		return valueData;
	}

	//取得显示的文件名!即去掉索引号
	private String getViewFileName(String _realFileName) {
		if (_realFileName != null && _realFileName.indexOf("/") != -1) {
			String param = _realFileName.substring(_realFileName.lastIndexOf("/") + 1, _realFileName.length());
			if (param != null && param.startsWith("N")) {
				int li_extentNamePos = param.lastIndexOf("."); //文件的扩展名的位置!即必须有个点!但在兴业项目中有许多文件是从后台灌入的!!也遇到到没后辍的!!所以报错!
				if (li_extentNamePos > 0) {
					return getTBUtil().convertHexStringToStr(param.substring(param.indexOf("_") + 1, li_extentNamePos)) + param.substring(li_extentNamePos, param.length()); //
				} else {
					return getTBUtil().convertHexStringToStr(param.substring(param.indexOf("_") + 1, param.length())); ////
				}
			} else {
				return param; //以前的版本也有存路径的？
			}
		} else {
			if (_realFileName == null || _realFileName.indexOf("_") < 0) {
				return _realFileName; //
			}
			return _realFileName.substring(_realFileName.indexOf("_") + 1, _realFileName.length()); //
		}
	}

	/**
	 * 取得排序列
	 * @param _orderCondition 排序条件
	 * @param _itemKeys 列名!!
	 * @param _itemTypes 列类型!!
	 * @return
	 */
	private String[][] getOrderCol(String _orderCondition, String[] _itemKeys, String[] _itemTypes) {
		String str_ordercondition = _orderCondition; //
		if (str_ordercondition.startsWith("\"")) {
			str_ordercondition = str_ordercondition.substring(1, str_ordercondition.length()); //如果以双引号开头,则去掉
		}
		if (str_ordercondition.endsWith("\"")) {
			str_ordercondition = str_ordercondition.substring(0, str_ordercondition.length() - 1); //如果以双引号结尾，则去掉，就是兼容双引号写法..
		}
		String[] str_item = this.tbUtil.split(str_ordercondition, ","); //分隔,共有几个排序条件
		String[][] str_returns = new String[str_item.length][3]; ////有3列,[itemkey][是否数字][是否倒序]
		for (int i = 0; i < str_item.length; i++) {
			String[] str_item_asc = tbUtil.split(str_item[i], " "); //以空格划分
			str_returns[i][0] = str_item_asc[0]; //
			if (str_item_asc.length >= 2 && str_item_asc[1].trim().equalsIgnoreCase("desc")) { //如果后面显式定义为desc,则设置
				str_returns[i][1] = "Y"; //是倒序
			} else {
				str_returns[i][1] = "N"; //不是倒序,即是顺序
			}
			str_returns[i][2] = testIsNumber(_itemKeys, _itemTypes, str_item_asc[0]); //判断该列是否是数字框!!
		}
		return str_returns;
	}

	/**
	 * 取得一个字符串在数组中的位置...
	 * @param _itemKeys
	 * @param _itemKey
	 * @return
	 */
	private String testIsNumber(String[] _itemKeys, String[] _itemTypes, String _itemKey) {
		int li_index = -1;
		for (int i = 0; i < _itemKeys.length; i++) {
			if (_itemKeys[i].equalsIgnoreCase(_itemKey)) { ////
				li_index = i;
				break;
			}
		}
		if (li_index < 0) {
			return "N"; //不是数字,即是字符串
		}

		if (_itemTypes[li_index].equals(WLTConstants.COMP_NUMBERFIELD)) { //如果是数字框...
			return "Y"; //是数字
		} else {
			return "N"; //不是数字,即是字符!!
		}
	}

	/**
	 * 执行加载公式!!!至关重要 后来增加了setRefCode(),setRefName()等公式!!!非常有用!!
	 * 加载公式的设计对性能挑战极高!!现在已发现在频繁使用jep计算公式时,性能有严重问题！！ 
	 * @param _hashVOs
	 * @param _data
	 * @param _templetVO
	 * @param _env
	 * @return
	 */
	private Object[][] execLoadformula(HashVO[] _hashVOs, Object[][] _data, Pub_Templet_1ParVO _templetVO, CurrSessionVO _currSessionVO) {
		if (_data == null) {
			return null;
		}
		Pub_Templet_1_ItemParVO[] itemVos = _templetVO.getItemVos(); // 各子项.
		int li_length = itemVos.length; //
		VectorMap vm_loadFormulas = new VectorMap(); //LinkedHashSet
		for (int j = 0; j < li_length; j++) { // 遍历每一列!!!!!
			String str_itemkey = itemVos[j].getItemkey(); //itemKey...
			String str_loadformula_define = itemVos[j].getLoadformula(); // 取得加载公式定义!!
			if (str_loadformula_define != null && !str_loadformula_define.trim().equals("")) { // 如果有加载公式定义
				String[] str_loadformulas = tbUtil.split1(str_loadformula_define.trim(), ";"); // 拆分出所有公式项!!!
				for (int k = 0; k < str_loadformulas.length; k++) { // 遍历公式!!可能有分号隔开!!!分别执行每个公式项!!!!
					vm_loadFormulas.put(str_loadformulas[k], str_itemkey); //
				}
			}
		}
		if (vm_loadFormulas.size() == 0) { //如果没有找到一个加载公式,则直接返回原来的数据!!!
			return _data;
		}

		String[] itemKeys = _templetVO.getItemKeys(); // 各项的key
		String[] itemTypes = _templetVO.getItemTypes(); // 各项的类型
		String[] str_allLoadFormulaItems = vm_loadFormulas.getKeysAsString(); //真正的所有公式!!!
		JepFormulaParseAtBS jepFormulaAtBS = new JepFormulaParseAtBS(_hashVOs); // 公式解析器实例!!!!!!
		HashMap colDataTypeMap = new HashMap();
		for (int i = 0; i < itemKeys.length; i++) {
			colDataTypeMap.put(itemKeys[i], itemTypes[i]); ///
		}
		jepFormulaAtBS.setColDataTypeMap(colDataTypeMap); //

		HashMap mapRowCache = null; // 存储一行数据的缓存..
		String[] str_viewdatakeys = null;
		long ll_realexeccount = 0; //
		long ll_maxdeal = 0; //
		int li_biger10 = 0; //
		//遍历每一行数据,分别执行加载公式!!!
		long ll_exec_1 = 0, ll_exec_2 = 0;
		HashMap map_itemkey_dealcount = new HashMap(); //哈希表!!!
		HashMap map_itemkey_recordcount = new HashMap(); //哈希表!!!
		long ll_1 = System.currentTimeMillis(); //
		for (int i = 0; i < _data.length; i++) {
			mapRowCache = new HashMap(); //创建行记录缓存,即用来存储一行的数据,因为可能某一列的值需要从另一列计算出!!
			str_viewdatakeys = _hashVOs[i].getKeys(); //
			for (int k = 0; k < str_viewdatakeys.length; k++) {
				mapRowCache.put(str_viewdatakeys[k], _hashVOs[i].getStringValue(str_viewdatakeys[k])); // 先将从数据库存中取出的所有列的值都赋给这个HashMap,可能用到!!
			}
			for (int j = 0; j < li_length; j++) {
				mapRowCache.put(itemKeys[j], _data[i][j + 1]); //再一下子在缓存中加入这一行的数据,有的是参照,有的是下拉框,然后每执行一步则继续往该缓存中加入数据!!
			}

			jepFormulaAtBS.setRowDataMap(mapRowCache); // 设置某一行数据
			for (int j = 0; j < str_allLoadFormulaItems.length; j++) { // 遍历每一列!!!!!需要将执行的某一个ItemKey的耗时累加到对应的ItemKey上!!!!!
				ll_exec_1 = System.currentTimeMillis(); //
				jepFormulaAtBS.execFormula(str_allLoadFormulaItems[j]); //执行公式!!!!这一行在Debug模式下时存在极其严重的性能问题!!但在运行模式下却没有性能问题,不知为什么相差那么大?
				ll_realexeccount++; //实际累加
				ll_exec_2 = System.currentTimeMillis(); //
				long ll_deal = ll_exec_2 - ll_exec_1; //
				if (ll_deal > ll_maxdeal) {
					ll_maxdeal = ll_deal; //
				}
				if (ll_deal > 10) {
					li_biger10 = li_biger10 + 1;
				}
				String str_itemKey = (String) vm_loadFormulas.get(str_allLoadFormulaItems[j]); //
				if (!map_itemkey_dealcount.containsKey(str_itemKey)) { //如果不包含对应的key
					map_itemkey_dealcount.put(str_itemKey, new Long(ll_exec_2 - ll_exec_1)); ////
					map_itemkey_recordcount.put(str_itemKey, new Integer(1)); //
				} else {
					long ll_olddeal = (Long) map_itemkey_dealcount.get(str_itemKey); //
					int li_oldrecords = (Integer) map_itemkey_recordcount.get(str_itemKey); //
					map_itemkey_dealcount.put(str_itemKey, new Long(ll_olddeal + (ll_exec_2 - ll_exec_1))); ////
					map_itemkey_recordcount.put(str_itemKey, new Integer(li_oldrecords + 1)); //
				}
			}
			for (int j = 0; j < li_length; j++) {
				_data[i][j + 1] = mapRowCache.get(itemKeys[j]); //
			}
		}
		long ll_2 = System.currentTimeMillis(); //
		long ll_dealcount = ll_2 - ll_1; //总计耗时!!!

		String[] str_itemkey_keys = (String[]) map_itemkey_dealcount.keySet().toArray(new String[0]); //
		ArrayList al_msgs = new ArrayList(); //
		for (int i = 0; i < str_itemkey_keys.length; i++) {
			long ll_dealcount2 = (Long) map_itemkey_dealcount.get(str_itemkey_keys[i]); //
			al_msgs.add((1000000000 + ll_dealcount2) + "[" + str_itemkey_keys[i] + "(" + map_itemkey_recordcount.get(str_itemkey_keys[i]) + "次)=" + ll_dealcount2 + "毫秒]"); //
		}
		String[] str_msgs = (String[]) al_msgs.toArray(new String[0]); //转换成数组,因为需要进行排序,即将最耗时的放在前面!!!
		getTBUtil().sortStrs(str_msgs); //排序一把,把最耗时的放在前面!!!
		StringBuilder sb_msg = new StringBuilder(); //拼接明细的字符串!!
		for (int i = str_msgs.length - 1; i >= 0; i--) {
			sb_msg.append(str_msgs[i].substring(10, str_msgs[i].length())); ////
		}
		if (_data.length > 0 && ll_dealcount > 0) { //显示具体耗时(以后考虑专门送到一个系统性能严重瓶颈表中去)!一般来说,一个定义了5个公式,实际执行100次的处理，不做缓存需要800多毫秒,做缓存后则只需要15毫秒!!!
			logger.info("处理[" + str_allLoadFormulaItems.length + "]条加载公式,实际执行[" + ll_realexeccount + "]次,共耗时[" + ll_dealcount + "]毫秒(强烈提醒:如果是Debug模式,有些加载公式奇怪的慢),明细" + sb_msg.toString() + ",最大耗时[" + ll_maxdeal + "]毫秒,超过10毫钞的有[" + li_biger10 + "]条"); //
		}
		return _data; //
	}

	/**
	 * 要到匹配的ComBoxItemVO
	 * 
	 * @param _vos
	 * @param _id
	 * @return
	 */
	private ComBoxItemVO findComBoxItemVO(ComBoxItemVO[] _vos, String _id) {
		if (_vos == null || _vos.length == 0) {
			return null;
		}
		for (int k = 0; k < _vos.length; k++) {
			if (_vos[k].getId() != null && _vos[k].getId().equals(_id)) { // 如果下拉框的主键不为空!!
				return (ComBoxItemVO) _vos[k].deepClone(); //必须克隆一下!
			}
		}
		return null;
	}

	public Object[][] getQueryDataByDS(String _datasourcename, String _sql, Pub_Templet_1VO _templetVO) throws Exception {
		int li_length = _templetVO.getItemKeys().length; // 总共有多少列,加上行号,还应多一列!!
		Pub_Templet_1_ItemVO[] itemVos = _templetVO.getItemVos(); // 各子项.
		String[] itemKeys = _templetVO.getItemKeys(); // 各项的key
		String[] itemTypes = _templetVO.getItemTypes(); // 各项的类型
		HashVO[] hashVOs = getCommDMO().getHashVoArrayByDS(_datasourcename, _sql); // 取得真正的数据

		Object[][] valueData = new Object[hashVOs.length][li_length]; // 创建数据对象!!!,比模板多一列是行号,永远在第一列!!

		// HashMap hm_RefItemVos = new HashMap(); // 一下子为各个参照灌入所有的参照数据!!!
		for (int i = 0; i < hashVOs.length; i++) { // 遍历各行
			Object[] rowobjs = new Object[li_length]; // 一行的数据
			HashMap map_cache = new HashMap(); // 一行数据中的缓存!!!其Value可能是String,ComBoxItemVO,RefItemVO

			// 首先一下子将所有值灌入....
			String[] str_realvalues = new String[li_length]; //
			for (int j = 0; j < li_length; j++) { // 遍历各列
				String str_key = itemKeys[j];
				String str_type = itemTypes[j]; //
				if (str_type.equals("日历")) {
					str_realvalues[j] = hashVOs[i].getStringValueForDay(str_key); //
				} else if (str_type.equals("时间")) {
					str_realvalues[j] = hashVOs[i].getStringValueForSecond(str_key); //
				} else {
					str_realvalues[j] = hashVOs[i].getStringValue(str_key); //
				}

				if (str_realvalues[j] != null) {
					if (itemTypes[j].equals("文本框")) { // 如果是文本框
						map_cache.put(str_key, str_realvalues[j]);
					} else if (itemTypes[j].equals("下拉框")) {
						map_cache.put(str_key, new ComBoxItemVO(str_realvalues[j], str_realvalues[j], str_realvalues[j]));
					} else if (itemTypes[j].equals("参照")) {
						map_cache.put(str_key, new RefItemVO(str_realvalues[j], str_realvalues[j], str_realvalues[j]));
					} else {
						map_cache.put(str_key, str_realvalues[j]);//
					}
				}
			}
			// 然后再处理每一列的值
			for (int j = 0; j < li_length; j++) // 遍历各列
			{
				String str_key = itemKeys[j];
				String str_type = itemTypes[j];
				String str_value = null;
				if (str_type.equals("日历")) {
					str_value = hashVOs[i].getStringValueForDay(str_key); //
				} else if (str_type.equals("时间")) {
					str_value = hashVOs[i].getStringValueForSecond(str_key); //
				} else {
					str_value = hashVOs[i].getStringValue(str_key); //
				}
				if (str_value != null) {
					if (itemTypes[j].equals("文本框")) { // 如果是文本框
						rowobjs[j] = str_value;
					} else if (itemTypes[j].equals("下拉框")) { // 如果是下拉框..
						ComBoxItemVO[] comBoxItemVos = itemVos[j].getComBoxItemVos();
						ComBoxItemVO matchVO = findComBoxItemVO(comBoxItemVos, str_value); // //..
						if (matchVO != null) {
							rowobjs[j] = matchVO;
						} else {
							rowobjs[j] = new ComBoxItemVO(str_value, str_value, str_value); // 下拉框VO
						}
					} else if (itemTypes[j].equals("参照")) {
						if (itemVos[j].getItemtype().equals(WLTConstants.COMP_REFPANEL) || itemVos[j].getItemtype().equals(WLTConstants.COMP_REFPANEL_TREE) || itemVos[j].getItemtype().equals(WLTConstants.COMP_REFPANEL_MULTI)) { // 如果是表或者树
							String str_refsql = itemVos[j].getRefdesc(); //
							if (str_refsql != null && !str_refsql.trim().equals("")) {
								String modify_str = null; //
								try {
									modify_str = tbUtil.replaceStrWithSessionOrHashData(str_refsql, getCurrSession(), map_cache); // !!!!得到转换后的SQL,即直接可以执行的SQL,即将其中的{aaa}进行转换,!!!这是关键
								} catch (Exception ex) {
									System.out.println("转换参照[" + _templetVO.getTempletcode() + "][" + itemVos[j].getItemkey() + "][" + itemVos[j].getItemname() + "]参照定义SQL[" + str_refsql + "]失败!!!");
									ex.printStackTrace(); //
								}

								if (modify_str != null) { // 如果成功转换SQL!!
									// modify_str =
									// tbUtil.replaceAll(modify_str, "1=1",
									// itemVos[j].getRefdesc_firstColName() +
									// "='" + str_value + "'"); //
									// 将SQL语句中的1=1替换成..
									System.out.println("开始执行参照定义转换后的SQL:" + modify_str); //
									HashVO[] ht_allRefItemVOS = null;
									try {
										ht_allRefItemVOS = getCommDMO().getHashVoArrayByDS(_datasourcename, modify_str); // 执行SQL!!!可能会抛异常!!
									} catch (Exception ex) {
										System.out.println("执行参照[" + _templetVO.getTempletcode() + "][" + itemVos[j].getItemkey() + "][" + itemVos[j].getItemname() + "]转换后SQL[" + modify_str + "]失败,原因:" + ex.getMessage()); //
										ex.printStackTrace();
									}

									if (ht_allRefItemVOS != null) { // 如果取得到数据!!
										boolean bo_iffindref = false;
										for (int pp = 0; pp < ht_allRefItemVOS.length; pp++) { // 遍历去找!!!
											if (str_value.equals(ht_allRefItemVOS[pp].getStringValue(0))) {
												rowobjs[j] = new RefItemVO(ht_allRefItemVOS[pp]); //
												bo_iffindref = true; // 如果非常幸运的找到品配的了!!!!!!!!!
												break;

											}
										}
										if (!bo_iffindref) { // 如果没找到品配的,则直接创建参照VO
											rowobjs[j] = new RefItemVO(str_value, str_value, str_value); // 参照VO
										}
									} else
									// 如果执行SQL取数失败!!
									{
										rowobjs[j] = new RefItemVO(str_value, str_value, str_value); // 参照VO
									}
								} else { // 如果转换SQL失败
									rowobjs[j] = new RefItemVO(str_value, str_value, str_value); // 参照VO
								}
							} else {
								rowobjs[j] = new RefItemVO(str_value, str_value, str_value); // 参照VO
							}
						} else { // 如果不是表型1或树型1参照!!,比如自定义,TABLE2,TREE2等
							rowobjs[j] = new RefItemVO(str_value, str_value, str_value); // 参照VO
						}
					} else { // 如果是其他控件
						rowobjs[j] = str_value;
					}
				} else { // 如果没取到数,则为空!
					rowobjs[j] = null;
				}

				map_cache.put(str_key, rowobjs[j]); // 往缓存中送入
			} // 一行数据中的各列全部处理处理结束!!!

			valueData[i] = rowobjs; //
		} // 所有行数据处理结束!!

		return valueData; //
	}//

	// 提交BillVO进行数据库操作!!
	public void commitBillVO(String _dsName, BillVO[] _deleteVOs, BillVO[] _insertVOs, BillVO[] _updateVOs) throws Exception {
		String[] str_sql_deletes = null;
		if (_deleteVOs != null && _deleteVOs.length > 0) {
			str_sql_deletes = new String[_deleteVOs.length]; //
			for (int i = 0; i < str_sql_deletes.length; i++) {
				str_sql_deletes[i] = _deleteVOs[i].getDeleteSQL(); //  
			}
		}

		String[] str_sql_inserts = null;
		if (_insertVOs != null && _insertVOs.length > 0) {
			str_sql_inserts = new String[_insertVOs.length]; //
			for (int i = 0; i < str_sql_inserts.length; i++) {
				str_sql_inserts[i] = _insertVOs[i].getInsertSQL(); //
			}
		}

		String[] str_sql_updates = null;
		if (_updateVOs != null && _updateVOs.length > 0) {
			str_sql_updates = new String[_updateVOs.length]; //
			for (int i = 0; i < str_sql_updates.length; i++) {
				str_sql_updates[i] = _updateVOs[i].getUpdateSQL(); //
			}
		}

		if (str_sql_deletes != null) {
			getCommDMO().executeBatchByDS(_dsName, str_sql_deletes); // 先删除!!
		}

		if (str_sql_inserts != null) {
			getCommDMO().executeBatchByDS(_dsName, str_sql_inserts); // 后插入!!
		}

		if (str_sql_updates != null) {
			getCommDMO().executeBatchByDS(_dsName, str_sql_updates); // 最后修改!!
		}

	}

	/**
	 * 权限权限策略过滤,返回的是SQL条件,比如【blcorpid in ('12','13')】
	 * @param _loginUserid
	 * @param _datapolicy
	 * @param _datapolicyMap
	 * @return
	 * @throws Exception
	 */
	public String[] getDataPolicyCondition(String _loginUserid, String _datapolicy, String _datapolicyMap) throws Exception {
		return new DataPolicyDMO().getDataPolicyCondition(_loginUserid, _datapolicy, _datapolicyMap); //
	}

	/**
	 * 替换字符
	 * 
	 * @param str_par
	 * @param old_item
	 * @param new_item
	 * @return
	 */
	private String replaceAll(String str_par, String old_item, String new_item) {
		String str_return = "";
		String str_remain = str_par;
		boolean bo_1 = true;
		while (bo_1) {
			int li_pos = str_remain.indexOf(old_item);
			if (li_pos < 0) {
				break;
			} // 如果找不到,则返回
			String str_prefix = str_remain.substring(0, li_pos);
			str_return = str_return + str_prefix + new_item; // 将结果字符串加上原来前辍
			str_remain = str_remain.substring(li_pos + old_item.length(), str_remain.length());
		}
		str_return = str_return + str_remain; // 将剩余的加上
		return str_return;
	}

	private String getDataSourceName(String _des) {
		String str_new = _des;
		int li_pos = str_new.indexOf("="); //
		str_new = str_new.substring(li_pos + 1, str_new.length()).trim();

		if (str_new.startsWith("\"") || str_new.startsWith("'")) {
			str_new = str_new.substring(1, str_new.length());
		}

		if (str_new.endsWith("\"") || str_new.endsWith("'")) {
			str_new = str_new.substring(0, str_new.length() - 1);
		}

		return str_new; // 取得数据源名称
	}

	/**
	 * 取得
	 * 
	 * @param _tabname
	 * @param _dbtype
	 * @param _sqlviewtype
	 * @return
	 */
	public String getCreateSQL(String _tabname, String _dbtype, String _sqlviewtype) throws Exception {
		HashVOStruct hvst = getCommDMO().getHashVoStructByDS(null, "select * from " + _tabname + " where 1=2"); //
		String[] str_cols = hvst.getHeaderName();
		String[] str_coltypes = hvst.getHeaderTypeName(); //
		int[] li_collength = hvst.getHeaderLength(); //

		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("drop table " + _tabname + ";\r\n"); //
		sb_sql.append("create table " + _tabname + "\r\n(\r\n");
		for (int i = 0; i < str_coltypes.length; i++) {
			String str_coltype = convertColType(str_coltypes[i], _dbtype); //
			if (str_cols[i].equalsIgnoreCase("ID")) {
				sb_sql.append(str_cols[i] + " " + str_coltype); //
			} else {
				sb_sql.append(str_cols[i] + " " + str_coltype); //	
			}

			if (str_coltype.equalsIgnoreCase("text") || str_coltype.equalsIgnoreCase("clob") || str_coltype.equalsIgnoreCase("integer") || str_coltype.equalsIgnoreCase("DECIMAL")) {
			} else {
				sb_sql.append("(" + li_collength[i] + ")");
			}
			sb_sql.append(",\r\n"); //
		}
		sb_sql.append("constraint pk_" + _tabname + " primary key (id)\r\n");
		sb_sql.append(");\r\n\r\n");

		sb_sql.append("delete from pub_sequence where sename='S_" + _tabname.toUpperCase() + "';\r\n"); //
		sb_sql.append("insert into pub_sequence (sename,currvalue) values('S_" + _tabname.toUpperCase() + "',0);\r\n"); //
		sb_sql.append("commit;\r\n"); //
		return sb_sql.toString();
	}

	private String convertColType(String _oldtype, String _destdbtype) {
		String str_oldtype = _oldtype.toUpperCase(); // //
		_destdbtype = _destdbtype.toUpperCase(); // //
		if (str_oldtype.startsWith("NUMBER") || str_oldtype.startsWith("DECIMAL") || str_oldtype.startsWith("INTEGER")) { //
			if (_destdbtype.equals(WLTConstants.ORACLE)) {
				return "number"; //
			} else if (_destdbtype.equals(WLTConstants.SQLSERVER)) {
				return "decimal"; //
			} else if (_destdbtype.equals(WLTConstants.MYSQL)) {
				return "decimal"; //
			} else if (_destdbtype.equals(WLTConstants.DB2)) {
				return "decimal"; //
			} else {
				return "decimal"; //
			}
		} else if (str_oldtype.startsWith("VARCHAR")) {
			if (_destdbtype.equals(WLTConstants.ORACLE)) {
				return "varchar2"; //
			} else if (_destdbtype.equals(WLTConstants.SQLSERVER)) {
				return "varchar"; //
			} else if (_destdbtype.equals(WLTConstants.MYSQL)) {
				return "varchar"; //
			} else if (_destdbtype.equals(WLTConstants.DB2)) {
				return "varchar"; //
			} else {
				return "varchar"; //
			}
		} else if (str_oldtype.startsWith("CHAR") || str_oldtype.startsWith("CHARACTER")) {
			if (_destdbtype.equals(WLTConstants.ORACLE)) { //
				return "char"; //
			} else if (_destdbtype.equals(WLTConstants.SQLSERVER)) { //
				return "char"; //
			} else if (_destdbtype.equals(WLTConstants.MYSQL)) { //
				return "char"; //
			} else if (_destdbtype.equals(WLTConstants.DB2)) { //
				return "character"; //
			} else {
				return "char"; //
			}
		} else if (str_oldtype.startsWith("TEXT") || str_oldtype.startsWith("CLOB")) {
			if (_destdbtype.equals(WLTConstants.ORACLE)) { //
				return "clob"; //
			} else if (_destdbtype.equals(WLTConstants.SQLSERVER)) { //
				return "text"; //
			} else if (_destdbtype.equals(WLTConstants.MYSQL)) { //
				return "text"; //
			} else if (_destdbtype.equals(WLTConstants.DB2)) { //
				return "clob"; //
			} else {
				return "text"; //
			}
		} else {
			return "varchar"; //
		}

	}

	/**
	 * 取得某个表的数据SQL
	 * 
	 * @param _tabname
	 * @param _dbtype
	 * @param _sqlviewtype
	 * @return
	 * @throws Exception
	 */
	public String getInserTableSQL(String _tabname, String _dbtype, String _sqlviewtype) throws Exception {
		boolean _iswrap = (_sqlviewtype.equals("横向") ? false : true); //
		StringBuffer sb_sql = new StringBuffer();
		sb_sql.append("\r\ntruncate table " + _tabname + ";\r\n\r\n"); //

		HashVOStruct hvst = getCommDMO().getHashVoStructByDS(null, "select * from " + _tabname + " where 1=1"); //
		String[] str_keys = hvst.getHeaderName(); // 取得所有字段名.
		String sequencecount = getCommDMO().getStringValueByDS(null, "select currvalue from pub_sequence where sename='S_" + _tabname.toUpperCase() + "'");
		int sequencecounts = Integer.parseInt(sequencecount);
		int[] str_type = new int[str_keys.length];
		str_type = hvst.getHeaderType();
		for (int r = 0; r < hvst.getHashVOs().length; r++) {
			String str_columnnames = ""; // 所有字段名
			String str_columvalues = ""; // 所有字段值
			for (int i = 0; i < str_keys.length; i++) {
				str_columnnames = str_columnnames + str_keys[i];
				if (str_keys[i].equalsIgnoreCase("ID") || str_type[i] == 3) { // 如果是主键

					str_columvalues = str_columvalues + ++sequencecounts;
				} else {
					str_columvalues = str_columvalues + getInsertValue(hvst.getHashVOs()[r].getStringValue(str_keys[i]), _dbtype); //
				}

				if (i != str_keys.length - 1) {
					str_columnnames = str_columnnames + ",";
					str_columvalues = str_columvalues + ",";
				}

				if (_iswrap) { // 如果换行
					// str_columnnames = str_columnnames + "\r\n";
					str_columvalues = str_columvalues + " --" + str_keys[i] + "\r\n";
				}
			}

			if (_iswrap) { // 如果换行
				sb_sql.append("insert into " + _tabname + " (" + str_columnnames + ")\r\nvalues\r\n(\r\n" + str_columvalues + ");\r\n\r\n");
			} else {
				sb_sql.append("insert into " + _tabname + " (" + str_columnnames + ") values (" + str_columvalues + ");\r\n\r\n");
			}
		}
		sb_sql.append("update pub_sequence set currvalue='" + sequencecounts + "' where sename='S_" + _tabname.toUpperCase() + "';\r\n");

		return sb_sql.toString();
	}

	/**
	 * 取得某个表的数据SQL全部
	 * 
	 * @param _tabname
	 * @param _dbtype
	 * @param _sqlviewtype
	 * @return
	 * @throws Exception
	 */
	public String getInserTableSQLAll(String _tabname, String _dbtype, String _sqlviewtype) throws Exception {
		boolean _iswrap = (_sqlviewtype.equals("横向") ? false : true); //
		StringBuffer sb_sql = new StringBuffer();
		sb_sql.append("\r\ntruncate table " + _tabname + ";\r\n\r\n"); //

		HashVOStruct hvst = getCommDMO().getHashVoStructByDS(null, "select * from " + _tabname + " where 1=1"); //
		String[] str_keys = hvst.getHeaderName(); // 取得所有字段名.
		int[] str_type = new int[str_keys.length];
		str_type = hvst.getHeaderType();
		for (int r = 0; r < hvst.getHashVOs().length; r++) {
			String str_columnnames = ""; // 所有字段名
			String str_columvalues = ""; // 所有字段值
			for (int i = 0; i < str_keys.length; i++) {
				str_columnnames = str_columnnames + str_keys[i];

				str_columvalues = str_columvalues + getInsertValue(hvst.getHashVOs()[r].getStringValue(str_keys[i]), _dbtype, str_type[i]); //

				if (i != str_keys.length - 1) {
					str_columnnames = str_columnnames + ",";
					str_columvalues = str_columvalues + ",";
				}

				if (_iswrap) { // 如果换行
					// str_columnnames = str_columnnames + "\r\n";
					str_columvalues = str_columvalues + " --" + str_keys[i] + "\r\n";
				}
			}

			if (_iswrap) { // 如果换行
				sb_sql.append("insert into " + _tabname + " (" + str_columnnames + ")\r\nvalues\r\n(\r\n" + str_columvalues + ");\r\n\r\n");
			} else {
				sb_sql.append("insert into " + _tabname + " (" + str_columnnames + ") values (" + str_columvalues + ");\r\n\r\n");
			}
		}
		return sb_sql.toString();
	}

	private String getInsertValue(String _value, String _dbtype) {
		String str_value = null;
		if (_value == null || _value.equals("")) {
			str_value = "null";
		} else {
			str_value = "'" + convert(_value, _dbtype) + "'";
		}
		return str_value;
	}

	/**
	 * 根据_value来确定要插入的值
	 * 
	 * @param _value
	 * @return
	 */
	private String getInsertValue(String _value, String _dbtype, int type) {
		String str_value = null;
		if (_value == null || _value.equals("")) {
			str_value = "null";
		} else if (type == 3) {
			str_value = "" + convert(_value, _dbtype) + "";
		} else {
			str_value = "'" + convert(_value, _dbtype) + "'";
		}
		return str_value;
	}

	private String convert(String _str, String _dbtype) {
		if (_str == null) {
			return "";
		}
		if (_dbtype.equalsIgnoreCase(WLTConstants.MYSQL)) {
			_str = tbUtil.replaceAll(_str, "\\", "\\\\"); //
			_str = tbUtil.replaceAll(_str, "'", "''"); //

		} else {
			_str = tbUtil.replaceAll(_str, "\\", "\\\\"); //
			_str = tbUtil.replaceAll(_str, "'", "''"); //
		}
		return _str; //
	}

	/**
	 * 取得一个BillCellVO,用来加载一个BillCellPanel
	 * 
	 * @param _templetCode
	 * @param _billNo
	 * @return
	 * @throws Exception
	 */
	public BillCellVO getBillCellVO(String _templetCode, String _billNo, String _descr) throws Exception {
		String str_sql_1 = null; //
		if (_billNo == null) {
			str_sql_1 = "select * from pub_billcelltemplet_h where templetcode='" + _templetCode + "' and billno is null"; //
		} else {
			str_sql_1 = "select * from pub_billcelltemplet_h where templetcode='" + _templetCode + "' and billno ='" + _billNo + "' and descr='" + _descr + "'"; //
		}
		HashVO[] hvs_1 = getCommDMO().getHashVoArrayByDS(null, str_sql_1); //
		if (hvs_1.length < 0) {
			throw new WLTAppException("Cant' found BillCellTemplet[" + _templetCode + "," + _billNo + "]."); // 
		}

		BillCellVO cellVO = new BillCellVO(); // //...
		cellVO.setId(hvs_1[0].getStringValue("id")); //
		cellVO.setBillNo(hvs_1[0].getStringValue("billno")); //
		cellVO.setTempletcode(hvs_1[0].getStringValue("templetcode")); //
		cellVO.setTempletname(hvs_1[0].getStringValue("templetname")); //
		cellVO.setRowlength(hvs_1[0].getIntegerValue("rowlength").intValue()); //
		cellVO.setCollength(hvs_1[0].getIntegerValue("collength").intValue()); //
		cellVO.setDescr(hvs_1[0].getStringValue("descr")); // 备注说明....
		cellVO.setSeq(hvs_1[0].getStringValue("seq"));

		String str_sql_2 = "select * from pub_billcelltemplet_d where templet_h_id=" + cellVO.getId() + " order by cellrow,cellcol";
		HashVO[] hvs_2 = getCommDMO().getHashVoArrayByDS(null, str_sql_2); //
		BillCellItemVO[][] cellItemVOs = new BillCellItemVO[cellVO.getRowlength()][cellVO.getCollength()]; //
		for (int i = 0; i < hvs_2.length; i++) {
			int li_row = hvs_2[i].getIntegerValue("cellrow").intValue(); //
			int li_col = hvs_2[i].getIntegerValue("cellcol").intValue(); //

			cellItemVOs[li_row][li_col] = new BillCellItemVO(); // //
			cellItemVOs[li_row][li_col].setCellrow(li_row); // //
			cellItemVOs[li_row][li_col].setCellcol(li_col); // //
			cellItemVOs[li_row][li_col].setCellkey(hvs_2[i].getStringValue("cellkey")); // 唯一标识
			cellItemVOs[li_row][li_col].setCellvalue(hvs_2[i].getStringValue("cellvalue")); // 值
			cellItemVOs[li_row][li_col].setCellhelp(hvs_2[i].getStringValue("cellhelp")); // 帮助说明
			cellItemVOs[li_row][li_col].setCelltype(hvs_2[i].getStringValue("celltype")); // 类型
			cellItemVOs[li_row][li_col].setCelldesc(hvs_2[i].getStringValue("celldesc")); // 格子定义..

			cellItemVOs[li_row][li_col].setIseditable(hvs_2[i].getStringValue("iseditable")); // 是否可编辑
			cellItemVOs[li_row][li_col].setIshtmlhref(hvs_2[i].getStringValue("ishtmlhref")); //是否html链接!

			cellItemVOs[li_row][li_col].setRowheight(hvs_2[i].getStringValue("rowheight")); // 行高
			cellItemVOs[li_row][li_col].setColwidth(hvs_2[i].getStringValue("colwidth")); // 列宽

			cellItemVOs[li_row][li_col].setForeground(hvs_2[i].getStringValue("foreground")); // 前景颜色
			cellItemVOs[li_row][li_col].setBackground(hvs_2[i].getStringValue("background")); // 后景颜色

			cellItemVOs[li_row][li_col].setLoadformula(hvs_2[i].getStringValue("loadformula")); // 加载公式
			cellItemVOs[li_row][li_col].setEditformula(hvs_2[i].getStringValue("editformula")); // 编辑公式
			cellItemVOs[li_row][li_col].setValidateformula(hvs_2[i].getStringValue("validateformula")); // 校验公式..
			cellItemVOs[li_row][li_col].setAvgformula(hvs_2[i].getStringValue("avgformula"));

			cellItemVOs[li_row][li_col].setFonttype(hvs_2[i].getStringValue("fonttype")); // 字体类型
			cellItemVOs[li_row][li_col].setFontstyle(hvs_2[i].getStringValue("fontstyle")); // 字体风格
			cellItemVOs[li_row][li_col].setFontsize(hvs_2[i].getStringValue("fontsize")); // 字体大小

			Integer li_halign = hvs_2[i].getIntegerValue("halign"); // 左右排序
			Integer li_valign = hvs_2[i].getIntegerValue("valign"); // 上下排序

			cellItemVOs[li_row][li_col].setHalign(li_halign == null ? 1 : li_halign.intValue()); //
			cellItemVOs[li_row][li_col].setValign(li_valign == null ? 1 : li_valign.intValue()); //

			cellItemVOs[li_row][li_col].setSpan(hvs_2[i].getStringValue("span")); //
		}

		cellVO.setCellItemVOs(cellItemVOs); //
		return cellVO; //
	}

	/**
	 * 保存BillCellVO
	 * 
	 * @param _datasourcename
	 * @param _cellvo
	 */
	public void saveBillCellVO(String _datasourcename, BillCellVO _cellvo) throws Exception {
		Vector v_sqls = new Vector(); //
		String str_parentid = _cellvo.getId(); //
		v_sqls.add("delete from pub_billcelltemplet_d where templet_h_id=" + str_parentid);
		v_sqls.add("delete from pub_billcelltemplet_h where id=" + str_parentid); //

		// 再先增主表数据
		InsertSQLBuilder isql = new InsertSQLBuilder("pub_billcelltemplet_h"); //
		isql.putFieldValue("id", str_parentid); //
		isql.putFieldValue("templetcode", _cellvo.getTempletcode()); //
		isql.putFieldValue("templetname", _cellvo.getTempletname()); //
		isql.putFieldValue("rowlength", _cellvo.getRowlength()); //
		isql.putFieldValue("collength", _cellvo.getCollength()); //
		isql.putFieldValue("billno", _cellvo.getBillNo()); //
		isql.putFieldValue("seq", _cellvo.getSeq()); //
		isql.putFieldValue("descr", _cellvo.getDescr()); //

		v_sqls.add(isql.getSQL()); //加入这个SQL!
		BillCellItemVO[][] cellItemVOs = _cellvo.getCellItemVOs(); //处理子表数据!!

		//long ll_1 = System.currentTimeMillis(); //
		int li_batchNo = (_cellvo.getRowlength() * _cellvo.getCollength()) / 2; //生成批号的次数!!因为记录太多时,耗时是耗在了生成批号上,这是提升性能的关键,因为一个1000个格子的表格,生成主键就需要操作数据库50次,结果就生成主键就要耗时近2秒!!!
		if (li_batchNo < 20) {
			li_batchNo = 20;
		}
		for (int i = 0; i < _cellvo.getRowlength(); i++) {
			for (int j = 0; j < _cellvo.getCollength(); j++) {
				v_sqls.add(getBillCellTemplet_d_SQL(li_batchNo, str_parentid, i, j, cellItemVOs[i][j])); //
			}
		}
		//long ll_2 = System.currentTimeMillis(); //
		//System.out.println("拼成子表SQL耗时[" + (ll_2 - ll_1) + "]"); //经过实践检测,这段逻辑是最耗时的地方,像一个150*6的网格,需要2秒多,而且耗时是耗时是耗在生成主键上,我曾经将主键预先生成,结果内存计算生成SQL只用了20毫秒左右,也就是99%的时间用在了生成主键,1%的时间用在了拼SQL,再次证明操作数据库是耗时的操作,而纯内存是极快的!

		//long ll_3 = System.currentTimeMillis(); //
		getCommDMO().executeBatchByDS(_datasourcename, v_sqls); // 提交数据库
		//long ll_4 = System.currentTimeMillis(); //
		//System.out.println("处理[" + v_sqls.size() + "]条SQL至数据库耗时[" + (ll_4 - ll_3) + "]"); //像一个150*6的网格,一共921条SQL提交数据库存,需要1秒左右的时间,一般在900毫秒左右!

	}

	/**
	 * 
	 * @param _row
	 * @param _col
	 * @param _value
	 * @return
	 */
	private String getBillCellTemplet_d_SQL(long _batchNo, String _parentID, int _row, int _col, BillCellItemVO _cellItemVO) throws Exception {
		String str_newid = getCommDMO().getSequenceNextValByDS(null, "s_pub_billcelltemplet_d", _batchNo); // 曾经写错了,老是出主键重复的问题
		InsertSQLBuilder isql = new InsertSQLBuilder("pub_billcelltemplet_d"); //强烈建议要使用InsertSQLBuilder对象构建SQL,而不是自己使用StringBuffer死"拼"了,因为至少有三个问题容易出错,一个是空值的处理,一个是默认值的处理,一个是逗号的处理,一个是单引号的处理!
		isql.putFieldValue("id", str_newid); //
		isql.putFieldValue("templet_h_id", _parentID); //
		isql.putFieldValue("cellrow", _row); //
		isql.putFieldValue("cellcol", _col); //
		isql.putFieldValue("cellkey", _cellItemVO.getCellkey()); //
		isql.putFieldValue("cellvalue", _cellItemVO.getCellvalue()); //
		isql.putFieldValue("cellhelp", _cellItemVO.getCellhelp()); //
		isql.putFieldValue("celltype", _cellItemVO.getCelltype(), "TEXT"); //
		isql.putFieldValue("celldesc", _cellItemVO.getCelldesc()); //
		isql.putFieldValue("iseditable", _cellItemVO.getIseditable(), "Y"); //是否可编辑,为null时,默认值是Y
		isql.putFieldValue("ishtmlhref", _cellItemVO.getIshtmlhref(), "N"); //是否是超链,为null时的默认值是N
		isql.putFieldValue("rowheight", _cellItemVO.getRowheight()); //
		isql.putFieldValue("colwidth", _cellItemVO.getColwidth()); //
		isql.putFieldValue("foreground", _cellItemVO.getForeground()); //
		isql.putFieldValue("background", _cellItemVO.getBackground()); //
		isql.putFieldValue("loadformula", _cellItemVO.getLoadformula()); //
		isql.putFieldValue("editformula", _cellItemVO.getEditformula()); //
		isql.putFieldValue("validateformula", _cellItemVO.getValidateformula()); //
		isql.putFieldValue("avgformula", _cellItemVO.getAvgformula()); //平均数公式,高峰后来加的,应该以后考虑几个(比如3个)自定义项,这样就不要扩展各种非通用的属性了!!
		isql.putFieldValue("fonttype", _cellItemVO.getFonttype()); //
		isql.putFieldValue("fontstyle", _cellItemVO.getFontstyle()); //
		isql.putFieldValue("fontsize", _cellItemVO.getFontsize()); //
		isql.putFieldValue("halign", _cellItemVO.getHalign()); //横向排列居中/左/右?
		isql.putFieldValue("valign", _cellItemVO.getValign()); //
		isql.putFieldValue("span", _cellItemVO.getSpan()); //合并的定义,最重要的属性!!!!!
		return isql.getSQL(); //
	}

	/**
	 * insert 10 demo records
	 * 
	 * @param _datasourcename
	 * @param _tablename
	 * @throws Exception
	 */
	public void createTenDemoRecords(String _datasourcename, String _tablename) throws Exception {
		String str_sql = "select * from v_pub_syscolumns where tabname='" + _tablename + "'";
		HashVO[] hvs = getCommDMO().getHashVoArrayByDS(_datasourcename, str_sql);
		String[] str_sqls = new String[10];
		for (int i = 0; i < 10; i++) {
			String str_seq_Name = "s_" + _tablename;
			String seq_number = getCommDMO().getSequenceNextValByDS(_datasourcename, str_seq_Name);// 获得对应表的seq

			StringBuffer str_item = new StringBuffer();
			str_item.append("insert into ");
			str_item.append(_tablename);
			str_item.append("(");
			for (int j = 0; j < hvs.length; j++) {
				String type_str = hvs[j].getStringValue("datatype");
				str_item.append(hvs[j].getStringValue("colcode"));
				if (j != hvs.length - 1) {
					str_item.append(",");
				}
			}
			str_item.append(") values (");
			String str_values = null;

			for (int j = 0; j < hvs.length; j++) {
				String str_colcode = hvs[j].getStringValue("colcode"); //
				String type_str = hvs[j].getStringValue("datatype");

				if (str_colcode.equalsIgnoreCase("parentid")) { // 如果是ParentId则不加入
					str_item.append("null");
				} else {
					if (type_str.startsWith("decimal")) { // 数字
						str_item.append(seq_number); //
					} else if (type_str.startsWith("char(1)")) { // 布尔
						str_item.append("'Y'"); //
					} else if (type_str.startsWith("datetime")) { // 时间
						str_item.append("getdate()");
					} else if (type_str.startsWith("varchar")) { // 字符
						String str_value = hvs[j].getStringValue("colcode") + "_" + seq_number;
						int li_collength = Integer.parseInt(type_str.substring(type_str.indexOf("(") + 1, type_str.length() - 1)); //
						if (str_value.length() >= li_collength) {
							str_value = str_value.substring(0, li_collength - 1); //
						}
						str_item.append("'" + str_value + "'"); //
					} else if (type_str.startsWith("text")) { // 字符
						String str_value = hvs[j].getStringValue("colcode") + "_" + seq_number;
						str_item.append("'" + str_value + "'"); //
					}
				}
				if (j != hvs.length - 1) {
					str_item.append(",");
				}
			}

			str_item.append(")");
			str_sqls[i] = str_item.toString();
			System.out.println(str_sqls[i]);
		}

		getCommDMO().executeBatchByDS(_datasourcename, str_sqls); //
	}

	/**
	 * 复制一个BillCell数据
	 * 
	 * @param _templetcode
	 * @param _billNO
	 */
	public void copyBillCellData(String _templetcode, String _billNO, String _descr) throws Exception {
		String str_sql_1 = "select * from pub_billcelltemplet_h where templetcode='" + _templetcode + "' and billno is null"; // 先找到模板
		HashVO[] hvs_1 = getCommDMO().getHashVoArrayByDS(null, str_sql_1);
		if (hvs_1.length == 0) {
			throw new WLTAppException("Not Find BillCellTemplet[" + _templetcode + "]"); //
		}

		String str_oldparentid = hvs_1[0].getStringValue("id"); //

		Vector v_sqls = new Vector(); //
		String str_newid = getCommDMO().getSequenceNextValByDS(null, "s_pub_billcelltemplet_h"); //
		v_sqls.add("insert into pub_billcelltemplet_h (id,templetcode,templetname,billno,rowlength,collength,descr) select '" + str_newid + "',templetcode,templetname,'" + _billNO + "',rowlength,collength,'" + _descr + "' from pub_billcelltemplet_h where id=" + str_oldparentid);

		String str_sql_2 = "select * from pub_billcelltemplet_d where templet_h_id='" + str_oldparentid + "' order by cellrow,cellcol";
		HashVO[] hvs_2 = getCommDMO().getHashVoArrayByDS(null, str_sql_2);
		for (int i = 0; i < hvs_2.length; i++) {
			String str_oldchildid = hvs_2[i].getStringValue("id"); //
			String str_newchildid = getCommDMO().getSequenceNextValByDS(null, "s_pub_billcelltemplet_d"); //

			StringBuffer sb_sal_child = new StringBuffer(); //
			sb_sal_child.append("insert into pub_billcelltemplet_d"); //
			sb_sal_child.append("(");
			sb_sal_child.append("id,");
			sb_sal_child.append("templet_h_id,");
			sb_sal_child.append("cellrow,");
			sb_sal_child.append("cellcol,");
			sb_sal_child.append("cellkey,");
			sb_sal_child.append("cellvalue,");
			sb_sal_child.append("cellhelp,"); // 帮助说明
			sb_sal_child.append("celltype,"); // Cell类型
			sb_sal_child.append("celldesc,"); // Cell定义
			sb_sal_child.append("iseditable,"); // 是否可编辑
			sb_sal_child.append("rowheight,");
			sb_sal_child.append("colwidth,");
			sb_sal_child.append("foreground,");
			sb_sal_child.append("background,");
			sb_sal_child.append("fonttype,");
			sb_sal_child.append("fontstyle,");
			sb_sal_child.append("fontsize,");
			sb_sal_child.append("span");
			sb_sal_child.append(")");
			sb_sal_child.append(" select ");
			sb_sal_child.append("'" + str_newchildid + "',");
			sb_sal_child.append("'" + str_newid + "',");
			sb_sal_child.append("cellrow,");
			sb_sal_child.append("cellcol,");
			sb_sal_child.append("cellkey,"); // cellkey
			sb_sal_child.append("cellvalue,");
			sb_sal_child.append("cellhelp,"); // 帮助说明
			sb_sal_child.append("celltype,"); // Cell类型
			sb_sal_child.append("celldesc,"); // Cell的描述
			sb_sal_child.append("iseditable,"); // 是否可编辑
			sb_sal_child.append("rowheight,");
			sb_sal_child.append("colwidth,");
			sb_sal_child.append("foreground,");
			sb_sal_child.append("background,");
			sb_sal_child.append("fonttype,");
			sb_sal_child.append("fontstyle,");
			sb_sal_child.append("fontsize,");
			sb_sal_child.append("span");
			sb_sal_child.append(" from pub_billcelltemplet_d where id='" + str_oldchildid + "'");
			v_sqls.add(sb_sal_child.toString()); //
		}

		// 提交数据库
		getCommDMO().executeBatchByDS(null, v_sqls); //
	}

	/**
	 * 替换SQL中的单引号,因为单引号会导致保存失败!!
	 * 
	 * @param _value
	 * @return
	 */
	private String convertSQLValue(String _value) {
		if (_value == null) {
			return null;
		} else {
			return replaceAll(_value, "'", "''");
		}
	}

	/***************************************************************************
	 * 通过模板修改表
	 * 
	 * @param _datasourcename
	 * @param _tablename
	 * @param _template
	 */
	public void compareTemplateAndTable(String _datasourcename, String[] _tablename, String[] _template) {
		HashVO[] hashvo1 = null;
		HashVO[] hashvo2 = null;
		HashVO[] hashvo3 = null;

		boolean bool = false;
		try {
			for (int j = 0; j < _template.length; j++) {
				hashvo2 = new HashVO[_template.length];
				hashvo2 = getCommDMO().getHashVoArrayByDS(null, "select pub.tablename,item.itemkey,item.itemtype,item.issave from pub_templet_1_item item,pub_templet_1 pub " + "where item.pk_pub_templet_1=pub.pk_pub_templet_1 and pub.templetcode='" + _template[j] + "'");
				hashvo1 = new HashVO[_tablename.length];
				hashvo1 = getCommDMO().getHashVoArrayByDS(null, "select * from v_pub_syscolumns where tabname='" + _tablename[j] + "'");

				for (int i = 0; i < hashvo2.length; i++) {
					for (int k = 0; k < hashvo1.length; k++) {
						if (!hashvo2[i].getStringValue("itemkey").equals(hashvo1[k].getStringValue("colcode"))) {

							if (hashvo2[i].getStringValue("issave").equals("Y")) {
								// UIUtil.executeUpdateByDS(null, );
								bool = false;
							} else {
								bool = true;
							}
						} else {
							bool = true;
							break;
						}

					}
					if (bool != true) {
						if (hashvo2[i].getStringValue("itemtype").equals("文本框")) {
							getCommDMO().executeUpdateByDS(null, "alter table " + hashvo1[0].getStringValue("tabname") + " add " + hashvo2[i].getStringValue("itemkey") + " varchar(100)");
						} else if (hashvo2[i].getStringValue("itemtype").equals("数字框")) {
							getCommDMO().executeUpdateByDS(null, "alter table " + hashvo1[0].getStringValue("tabname") + " add " + hashvo2[i].getStringValue("itemkey") + " decimal");
						} else if (hashvo2[i].getStringValue("itemtype").equals("勾选框")) {
							getCommDMO().executeUpdateByDS(null, "alter table " + hashvo1[0].getStringValue("tabname") + " add " + hashvo2[i].getStringValue("itemkey") + " char(1)");
						} else if (hashvo2[i].getStringValue("itemtype").equals("时间")) {
							getCommDMO().executeUpdateByDS(null, "alter table " + hashvo1[0].getStringValue("tabname") + " add " + hashvo2[i].getStringValue("itemkey") + " datetime");
						} else if (hashvo2[i].getStringValue("itemtype").equals("日历")) {
							getCommDMO().executeUpdateByDS(null, "alter table " + hashvo1[0].getStringValue("tabname") + " add " + hashvo2[i].getStringValue("itemkey") + " datetime");
						} else if (hashvo2[i].getStringValue("itemtype").equals("多行文本框")) {
							getCommDMO().executeUpdateByDS(null, "alter table " + hashvo1[0].getStringValue("tabname") + " add " + hashvo2[i].getStringValue("itemkey") + " varchar(1000)");
						} else {
							getCommDMO().executeUpdateByDS(null, "alter table " + hashvo1[0].getStringValue("tabname") + " add " + hashvo2[i].getStringValue("itemkey") + " varchar(100)");
						}

					}
					if (bool != true) {
						System.out.println("alter table " + hashvo1[0].getStringValue("tabname") + " add " + hashvo2[i].getStringValue("itemkey") + " " + hashvo2[i].getStringValue("itemtype") + "");
					}
				}

			}

		} catch (WLTRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/***************************************************************************
	 * 往BillCellPanel里读取数据的方法
	 * 
	 * @param templethid
	 * @param _billno
	 * 
	 */
	public HashVO[] getCellLoadDate(String templethid, String _billno) {
		HashVO[] hashvo = null;
		try {
			hashvo = getCommDMO().getHashVoArrayByDS(null, "select * from pub_billcelltemplet_data where billno='" + _billno + "' and templet_h_id='" + templethid + "'");
		} catch (WLTRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();

			return null;
		}
		return hashvo;

	}

	/***************************************************************************
	 * 往BillCellPanel里存储数据的方法
	 * 
	 * @param templethid
	 * @param _billno
	 * 
	 */
	public void getCellSaveDate(String templethid, String _billno, HashMap hashmap) {
		try {
			getCommDMO().executeUpdateByDS(null, "delete from pub_billcelltemplet_data where billno='" + _billno + "' and templet_h_id='" + templethid + "'");
			String[] sql = new String[hashmap.size()];
			String[] str_keys = (String[]) hashmap.keySet().toArray(new String[0]); //
			for (int i = 0; i < str_keys.length; i++) {
				String str_newid = getCommDMO().getSequenceNextValByDS(null, "s_pub_billcelltemplet_data"); // 曾经写错了,老是出主键重复的问题
				StringBuffer sb_sql = new StringBuffer(); //
				String value = (String) hashmap.get(str_keys[i]);
				value = (value == null ? "" : value);
				sb_sql.append("insert into pub_billcelltemplet_data");
				sb_sql.append("(");
				sb_sql.append("id,"); // id (null)
				sb_sql.append("templet_h_id,"); // templet_h_id (null)
				sb_sql.append("billno,"); //
				sb_sql.append("cellkey,"); // cellkey (null)
				sb_sql.append("cellvalue) values("); // cellvalue (null)
				sb_sql.append("" + str_newid + ",'" + templethid + "',"); //
				sb_sql.append("'" + _billno + "','" + str_keys[i] + "','" + convertSQLValue(value) + "')"); //
				sql[i] = sb_sql.toString();
				System.out.println(sql[i]);
			}
			getCommDMO().executeBatchByDS(null, sql); //
		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	/**
	 * 
	 * @param string
	 * @param _dbtype
	 * @param _sqlviewtype
	 */
	public String getOneRegFormatPanelSQL(String _code, String _dbtype, String _sqlviewtype) throws Exception {
		boolean _iswrap = (_sqlviewtype.equals("横向") ? false : true); //
		StringBuffer sb_return = new StringBuffer(); //
		HashVOStruct hvst = getCommDMO().getHashVoStructByDS(null, "select * from pub_regformatpanel where code='" + _code + "'"); //
		String[] str_keys = hvst.getHeaderName(); // 取得所有字段名.

		for (int r = 0; r < hvst.getHashVOs().length; r++) {
			String str_columnnames = ""; // 所有字段名
			String str_columvalues = ""; // 所有字段值
			for (int i = 0; i < str_keys.length; i++) {
				str_columnnames = str_columnnames + str_keys[i];
				if (str_keys[i].equalsIgnoreCase("ID")) { // 如果是主键
					str_columvalues = str_columvalues + (_dbtype.equals(WLTConstants.SQLSERVER) ? "dbo." : "") + "fn_getsequence('S_PUB_REGFORMATPANEL')"; //
				} else {
					str_columvalues = str_columvalues + getInsertValue(hvst.getHashVOs()[r].getStringValue(str_keys[i]), _dbtype); //
				}

				if (i != str_keys.length - 1) {
					str_columnnames = str_columnnames + ",";
					str_columvalues = str_columvalues + ",";
				}

				if (_iswrap) { // 如果换行
					// str_columnnames = str_columnnames + "\r\n";
					str_columvalues = str_columvalues + " --" + str_keys[i] + "\r\n"; //
				}
			}

			sb_return.append("delete from pub_regformatpanel where code='" + _code + "';\r\n"); // 先删除之
			sb_return.append("update pub_sequence set currvalue=currvalue+1 where sename='S_PUB_REGFORMATPANEL';\r\n");
			if (_iswrap) { // 如果换行
				sb_return.append("insert into pub_regformatpanel (" + str_columnnames + ")\r\nvalues\r\n(\r\n" + str_columvalues + ");\r\n\r\n");
			} else {
				sb_return.append("insert into pub_regformatpanel (" + str_columnnames + ") values (" + str_columvalues + ");\r\n\r\n");
			}
		}
		return sb_return.toString();
	}

	/**
	 * 导出列表模板SQL
	 * 
	 * @param _code
	 * @param _dbtype
	 * @param _iswrap
	 * @return
	 */
	public String getExportBillTempletSQL(String _code, String _dbtype, boolean _iswrap) throws Exception {
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("delete pub_templet_1_item where pk_pub_templet_1 in " + "(select pk_pub_templet_1 from pub_templet_1 where templetcode = '" + _code + "');\r\n");
		sb_sql.append("delete pub_templet_1 where templetcode ='" + _code + "';\r\n");
		sb_sql.append("\r\n");

		HashVO[] hv_data = getCommDMO().getHashVoArrayByDS(null, "Select * From PUB_TEMPLET_1 Where templetcode ='" + _code + "'"); //
		if (hv_data.length <= 0) {
			return "";
		}

		String str_PK_PUB_TEMPLET_1 = hv_data[0].getStringValue("PK_PUB_TEMPLET_1"); //

		String[] str_keys = hv_data[0].getKeys(); // 取得所有字段名.
		String str_columnnames = ""; // 所有字段名
		String str_columvalues = ""; // 所有字段值
		for (int i = 0; i < str_keys.length; i++) {
			str_columnnames = str_columnnames + str_keys[i];
			if (str_keys[i].equalsIgnoreCase("PK_PUB_TEMPLET_1")) { // 如果是主键
				str_columvalues = str_columvalues + (_dbtype.equals(WLTConstants.SQLSERVER) ? "dbo." : "") + "fn_getsequence('S_PUB_TEMPLET_1')"; //
			} else {
				str_columvalues = str_columvalues + getInsertValue(hv_data[0].getStringValue(str_keys[i]), _dbtype); //
			}

			if (i != str_keys.length - 1) {
				str_columnnames = str_columnnames + ",";
				str_columvalues = str_columvalues + ",";
			}

			if (_iswrap) { // 如果换行
				// str_columnnames = str_columnnames + "\r\n";
				str_columvalues = str_columvalues + " --" + str_keys[i] + "\r\n";
			}
		}

		sb_sql.append("update pub_sequence set currvalue=currvalue+1 where sename='S_PUB_TEMPLET_1';\r\n");
		if (_iswrap) { // 如果换行
			sb_sql.append("insert into pub_templet_1 (" + str_columnnames + ")\r\nvalues\r\n(\r\n" + str_columvalues + ");\r\n\r\n");
		} else {
			sb_sql.append("insert into pub_templet_1 (" + str_columnnames + ") values (" + str_columvalues + ");\r\n\r\n");
		}

		// 把每个Item的列和数据写入文件
		String _sql_item_context = "Select * From PUB_TEMPLET_1_ITEM Where PK_PUB_TEMPLET_1='" + str_PK_PUB_TEMPLET_1 + "'";
		HashVO[] hv_item = getCommDMO().getHashVoArrayByDS(null, _sql_item_context); //
		for (int i = 0; i < hv_item.length; i++) {
			String[] str_itemkeys = hv_item[i].getKeys(); // 得到所有列名
			String str_itemcolumnnames = ""; // 所有字段名
			String str_iemcolumvalues = ""; // 所有字段值
			for (int j = 0; j < str_itemkeys.length; j++) {
				str_itemcolumnnames = str_itemcolumnnames + str_itemkeys[j];
				if (str_itemkeys[j].equalsIgnoreCase("PK_PUB_TEMPLET_1_ITEM")) { // 如果是主键
					str_iemcolumvalues = str_iemcolumvalues + (_dbtype.equals(WLTConstants.SQLSERVER) ? "dbo." : "") + "fn_getsequence('S_PUB_TEMPLET_1_ITEM')"; // 采用序列取数..
				} else if (str_itemkeys[j].equalsIgnoreCase("PK_PUB_TEMPLET_1")) { // 如果是父记录主键
					str_iemcolumvalues = str_iemcolumvalues + (_dbtype.equals(WLTConstants.SQLSERVER) ? "dbo." : "") + "fn_getsequence('S_PUB_TEMPLET_1')"; // 采用序列取数..
				} else {
					str_iemcolumvalues = str_iemcolumvalues + getInsertValue(hv_item[i].getStringValue(str_itemkeys[j]), _dbtype); //
				}

				if (j != str_itemkeys.length - 1) {
					str_itemcolumnnames = str_itemcolumnnames + ",";
					str_iemcolumvalues = str_iemcolumvalues + ",";
				}

				if (_iswrap) { // 如果换行
					// str_itemcolumnnames = str_itemcolumnnames + "\r\n";
					str_iemcolumvalues = str_iemcolumvalues + " --" + str_itemkeys[j] + "\r\n";
				}
			}

			sb_sql.append("update pub_sequence set currvalue=currvalue+1 where sename='S_PUB_TEMPLET_1_ITEM';\r\n");
			if (_iswrap) { // 如果换行
				sb_sql.append("insert into pub_templet_1_item (" + str_itemcolumnnames + ")\r\nvalues\r\n(\r\n" + str_iemcolumvalues + ");\r\n\r\n");
			} else {
				sb_sql.append("insert into pub_templet_1_item (" + str_itemcolumnnames + ") values (" + str_iemcolumvalues + ");\r\n\r\n");
			}
		}

		return sb_sql.toString(); //
	}

	/**
	 * 转移数据
	 * 
	 * @param _sourcedsname
	 * @param _destdsname
	 * @param _tables
	 * @param _iscreate
	 * @param _isinsert
	 * @return
	 */
	public int transferDB(String _sourcedsname, String _destdsname, String _table, boolean _iscreate, boolean _isinsert) throws Exception {
		String pk_fieldname = null; //
		HashVOStruct hvst = getCommDMO().getHashVoStructByDS(_sourcedsname, "select * from " + _table + " where 1=2"); //
		String[] str_cols = hvst.getHeaderName();
		String[] str_coltypes = hvst.getHeaderTypeName(); //
		int[] li_collength = hvst.getHeaderLength(); //

		// //
		if (str_cols[0].toUpperCase().startsWith("PK_")) { // 如果第一列是以PK开头，则作为主键．
			pk_fieldname = str_cols[0]; //
		} else { //
			for (int i = 0; i < str_cols.length; i++) { //
				if (str_cols[i].equalsIgnoreCase("id")) { //
					pk_fieldname = "id"; // 如果有id字段则用id字段
					break; //
				}
			}
		}

		//
		String str_destdbtype = getDBType(_destdsname); // 目标库的类型..
		if (_iscreate) {
			StringBuffer sb_sql = new StringBuffer(); //
			sb_sql.append("create table " + _table + " (");
			for (int i = 0; i < str_coltypes.length; i++) {
				String str_coltype = convertColType(str_coltypes[i], str_destdbtype); //
				if (li_collength[i] > 4000 && "varchar2".equals(str_coltype)) {
					sb_sql.append(str_cols[i] + " " + "clob"); //
				} else {
					sb_sql.append(str_cols[i] + " " + str_coltype); //
					if (str_coltype.equalsIgnoreCase("text") || str_coltype.equalsIgnoreCase("clob")) {
					} else {

						sb_sql.append("(" + li_collength[i] + ")");

					}
				}
				if (i == str_coltypes.length - 1) { // 如果是最后一列
					if (pk_fieldname != null) {
						sb_sql.append(","); // 如果有主键，则加逗号..
					}
				} else {
					sb_sql.append(","); //
				}
			}

			if (pk_fieldname != null) {
				if (_table.length() < 25) {
					sb_sql.append("constraint pk_" + _table + " primary key (" + pk_fieldname + ")"); //
				} else {
					sb_sql.append("primary key (" + pk_fieldname + ")"); //
				}
			}
			sb_sql.append(")");

			getCommDMO().executeUpdateByDSImmediately(_destdsname, sb_sql.toString()); // 在目标库中立即执行
		}

		int li_insertrecords = 0; //
		if (_isinsert) { // 如果需要插入数据
			int li_batchrecords = 1; //
			HashVO[] hvs_count = getCommDMO().getHashVoArrayByDS(_sourcedsname, "select count(*) c1 from " + _table); // //
			int li_recordcount = hvs_count[0].getIntegerValue("c1").intValue(); //
			if (li_recordcount > 0) {
				if (pk_fieldname == null) { // 如果主键为空
					li_insertrecords = insertAllData(_sourcedsname, _destdsname, str_destdbtype, _table); // 如果没有主键,直接插入所有数据
				} else {
					HashVO[] hvs_min = getCommDMO().getHashVoArrayByDS(_sourcedsname, "select min(" + pk_fieldname + ") minc1 from " + _table); //
					Integer li_MinID = hvs_min[0].getIntegerValue("minc1"); //
					if (li_MinID == null) { // 如果主键值为空
						li_insertrecords = insertAllData(_sourcedsname, _destdsname, str_destdbtype, _table); // 如果没有主键,直接插入所有数据
					} else {
						int li_minid = li_MinID.intValue(); //
						HashVO[] hvs_max = getCommDMO().getHashVoArrayByDS(_sourcedsname, "select max(" + pk_fieldname + ") maxc1 from " + _table); //
						int li_maxid = hvs_max[0].getIntegerValue("maxc1").intValue(); //

						int li_begin = li_minid;
						int li_end = li_begin + li_batchrecords; //
						ArrayList v_sqls = null; //
						while (li_begin <= li_maxid) {
							HashVO[] hvs_record = getCommDMO().getHashVoArrayByDS(_sourcedsname, "select * from " + _table + " where " + pk_fieldname + " between '" + li_begin + "' and '" + li_end + "'"); //
							li_insertrecords = li_insertrecords + hvs_record.length; // //
							v_sqls = new ArrayList(); //
							for (int r = 0; r < hvs_record.length; r++) {
								String str_columnnames = ""; // 所有字段名
								String str_columvalues = ""; // 所有字段值
								for (int i = 0; i < str_cols.length; i++) {
									str_columnnames = str_columnnames + str_cols[i]; //
									str_columvalues = str_columvalues + getInsertValue(hvs_record[r].getStringValue(str_cols[i]), str_destdbtype); //
									if (i != str_cols.length - 1) { //
										str_columnnames = str_columnnames + ",";
										str_columvalues = str_columvalues + ",";
									}
								}
								v_sqls.add("insert into " + _table + " (" + str_columnnames + ") values (" + str_columvalues + ")"); //
							}
							getCommDMO().executeBatchByDSImmediately(_destdsname, v_sqls); // 立即插入..
							li_begin = li_end + 1;
							li_end = li_begin + li_batchrecords; //
						}
					}
				}
			}
		}
		return li_insertrecords;
	}

	public String getXMlFromTableRecords(String _sourcedsname, String[] _sqls) throws Exception {
		return getXMlFromTableRecords(_sourcedsname, _sqls, null, null, null); //
	}

	/**
	 * 根据SQL将数据导出成XML,因为表之间有关系,所以以前的方法不通用!! 这个方法是最简洁最通用的!! 也将是以后导出的最主要的方法!!
	 * @param _sourcedsname
	 * @param _sqls
	 * @param _pkConstraint 主键约束定义, N行3列,第1列是表名,第2列是主键字段名,第3列是序列名
	 * @param _foreignPKConstraint 外键约束定义,N行2列,第一列是表名.字段名,第2列是对应哪张表的哪个字段!!
	 * @param _xmlFile如何指定了xml文件路径则直接返回此xml内容
	 * @return
	 * @throws Exception
	 */
	public String getXMlFromTableRecords(String _sourcedsname, String[] _sqls, String[][] _pkConstraint, String[][] _foreignPKConstraint, String _xmlFile) throws Exception {
		if (_xmlFile != null) {
			InputStream fileInStream = this.getClass().getResourceAsStream(_xmlFile); //
			if (fileInStream != null) { //如果存在此文件!
				return new String(getTBUtil().readFromInputStreamToBytes(fileInStream), "UTF-8");
			}
		}

		StringBuffer sb_xml = new StringBuffer(); //
		sb_xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"); //
		sb_xml.append("<root>\r\n"); //
		for (int i = 0; i < _sqls.length; i++) {
			HashVOStruct hvst = new CommDMO().getHashVoStructByDS(_sourcedsname, _sqls[i]); //取得结构!!
			String str_tabname = hvst.getTableName(); //表名
			String[] str_keys = hvst.getHeaderName(); //字段名
			HashVO[] hvs = hvst.getHashVOs(); //所有记录
			String str_itemValue = null; //
			for (int j = 0; j < hvs.length; j++) {
				sb_xml.append("<record tabname=\"" + str_tabname + "\" pkname=\"" + getPKOrSEName(_pkConstraint, str_tabname, 1) + "\" sename=\"" + getPKOrSEName(_pkConstraint, str_tabname, 2) + "\">\r\n"); //同时指定表名,主键字段名,序列名
				for (int k = 0; k < str_keys.length; k++) { //
					str_itemValue = hvs[j].getStringValue(str_keys[k], ""); //
					if (str_itemValue.indexOf("<") >= 0 || str_itemValue.indexOf(">") >= 0 || str_itemValue.indexOf("&") >= 0) { //如果本身的<>或&
						str_itemValue = "<![CDATA[" + str_itemValue + "]]>"; //
					}
					sb_xml.append("  <col name=\"" + str_keys[k] + "\"" + getForeignPKRef(_foreignPKConstraint, str_tabname, str_keys[k]) + ">" + str_itemValue + "</col>\r\n"); //取得列的值! 如果定义了外键,则还要指定外键!!!
				}
				sb_xml.append("</record>\r\n"); //
				sb_xml.append("\r\n"); //
			}
		}
		sb_xml.append("</root>\r\n"); //
		return sb_xml.toString();
	}

	/**
	 * 获取所有模板
	 * @param templetcode
	 * @param tablename
	 * @return
	 * @throws Exception
	 */
	public List<Object> getAllTemplate(String templetcode, String tablename, String type) throws Exception {
		List<Object> rtn = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer(" select pk_pub_templet_1,templetcode,templetname,tablename,savedtablename,datapolicy,'数据库' savetype from pub_templet_1 where 1=1 ");
		if (templetcode != null && !"".equals(templetcode.trim())) {
			sb.append(" and (lower(templetcode) like '%" + templetcode.trim().toLowerCase() + "%' " + " or lower(templetname) like '%" + templetcode.trim().toLowerCase() + "%' ) ");
		}
		if (tablename != null && !"".equals(tablename.trim())) {
			sb.append(" and (lower(savedtablename) like '%" + tablename.trim().toLowerCase() + "%' " + " or lower(tablename) like '%" + tablename.trim().toLowerCase() + "%' ) ");
		}
		sb.append(" order by templetcode asc  ");
		HashVO[] t_db = null;
		HashVO[] all = null;
		List<Object> t_xml = null;
		if ("DB".equals(type)) {
			t_db = getCommDMO().getHashVoArrayByDS(null, sb.toString());
			rtn.add(t_db);
			rtn.add("");
		} else if ("XML".equals(type)) {
			t_xml = getAllXmlTemplet_(templetcode, tablename);
			rtn.add((HashVO[]) t_xml.get(0));
			rtn.add(t_xml.get(1));
		} else {
			t_db = getCommDMO().getHashVoArrayByDS(null, sb.toString());
			t_xml = getAllXmlTemplet_(templetcode, tablename);
			all = (HashVO[]) ArrayUtils.addAll(t_db, (HashVO[]) t_xml.get(0));
			Arrays.sort(all, new HashVOComparator(new String[][] { { "templetcode", "N", "N" } }));
			rtn.add(all);
			rtn.add(t_xml.get(1));
		}
		return rtn;
	}

	public List<Object> getAllXmlTemplet_(String templetcode, String tablename) throws Exception {
		List<Object> rtn = new ArrayList<Object>();
		List all = new ArrayList();
		DefaultTMO tmp_o = null;
		HashVO tmp = null;
		String platdir = "cn/com/infostrategy/bs/sysapp/install/templetdata/"; //
		List<String[]> other_dir = new ArrayList<String[]>();
		other_dir.add(new String[] { platdir, "平台" });
		String str_installs = ServerEnvironment.getProperty("INSTALLAPPS");
		if (str_installs != null && !str_installs.trim().equals("")) {
			TBUtil tbUtil = new TBUtil(); //
			String[] str_items = tbUtil.split(str_installs, ";");
			for (int i = 0; i < str_items.length; i++) {
				int li_pos = str_items[i].indexOf("-"); //
				String str_package = null; //
				String str_packdescr = null; //
				if (li_pos > 0) {
					str_package = str_items[i].substring(0, li_pos);
					str_packdescr = str_items[i].substring(li_pos + 1, str_items[i].length());
				} else {
					str_package = str_items[i];
					str_packdescr = "未命名";
				}
				str_package = tbUtil.replaceAll(str_package, ".", "/");
				str_package = str_package + "/templetdata/";
				other_dir.add(new String[] { str_package, str_packdescr });
			}
		}
		//找到存放模板文件的包后就开始找了
		URL url = null;
		String protocol = null;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < other_dir.size(); i++) {
			ClassLoader l = Thread.currentThread().getContextClassLoader();
			url = l.getResource(other_dir.get(i)[0]);//这个地方困扰很久，是我们打jar包时出了问题
			//			if(url == null) {//开始这么写是因为老是得不到我们打的jar包的包，以为是classloader的问题，其实不是，是我们打jar包的问题
			//				while(true) {
			//					l = l.getParent();
			//					if(l == null) {
			//						break;
			//					}
			//					url = l.getResource(other_dir.get(i)[0]);
			//					if(url != null) {
			//						break;
			//					}
			//				}
			//			}
			if (url == null) {
				if (sb.length() <= 0) {
					sb.append(other_dir.get(i)[1]);
				} else {
					sb.append("、" + other_dir.get(i)[1]);
				}
				continue;
			}
			protocol = url.getProtocol();
			try {
				if ("jar".equals(protocol)) {
					JarURLConnection con = (JarURLConnection) url.openConnection();
					JarFile file = con.getJarFile();
					Enumeration enu = file.entries();
					String className = "";
					String entryName = "";
					while (enu.hasMoreElements()) {
						JarEntry element = (JarEntry) enu.nextElement();
						entryName = element.getName();
						className = entryName.substring(entryName.lastIndexOf("/") + 1);
						if (!className.equals("") && entryName.equals(other_dir.get(i)[0] + className)) {
							if (className.endsWith(".xml") || className.endsWith(".XML")) {
								if (templetcode != null && !"".equals(templetcode.trim()) && className.toLowerCase().indexOf(templetcode.toLowerCase()) < 0) {
									continue;
								}
								//直接从xml路径取 【杨科/2013-03-25】
								tmp_o = getDefaultTMOByCode(entryName, 3, true);
								if (tablename != null && !"".equals(tablename.trim())) {
									if (tmp_o.getPub_templet_1Data().getAttributeValue("savedtablename") != null && tmp_o.getPub_templet_1Data().getAttributeValue("savedtablename").toString().toLowerCase().indexOf(tablename.toLowerCase()) >= 0) {
									} else if (tmp_o.getPub_templet_1Data().getAttributeValue("tablename") != null && tmp_o.getPub_templet_1Data().getAttributeValue("tablename").toString().toLowerCase().indexOf(tablename.toLowerCase()) >= 0) {
									} else {
										continue;
									}
								}
								tmp = new HashVO();
								tmp.setAttributeValue("templetcode", className.substring(0, className.lastIndexOf(".")));
								tmp.setAttributeValue("templetname", tmp_o.getPub_templet_1Data().getAttributeValue("templetname"));
								tmp.setAttributeValue("tablename", tmp_o.getPub_templet_1Data().getAttributeValue("tablename"));
								tmp.setAttributeValue("savedtablename", tmp_o.getPub_templet_1Data().getAttributeValue("savedtablename"));
								tmp.setAttributeValue("datapolicy", tmp_o.getPub_templet_1Data().getAttributeValue("datapolicy"));//xml中查询需要显示策略【李春娟/2016-06-15】
								tmp.setAttributeValue("pk_pub_templet_1", null);
								tmp.setAttributeValue("savetype", other_dir.get(i)[1] + "XML_" + other_dir.get(i)[0] + className);
								all.add(tmp);
							}
						}
					}
				} else if ("zip".equals(protocol)) { //weblogic读出来的是zip 杨科/2013-12-30
					String str_url = url.getPath();
					if (str_url.lastIndexOf("!") > -1) {
						str_url = str_url.substring(0, str_url.lastIndexOf("!"));
					}
					ZipFile file = new ZipFile(str_url);
					Enumeration enu = file.entries();
					String className = "";
					String entryName = "";
					while (enu.hasMoreElements()) {
						ZipEntry element = (ZipEntry) enu.nextElement();
						entryName = element.getName();
						className = entryName.substring(entryName.lastIndexOf("/") + 1);
						if (!className.equals("") && entryName.equals(other_dir.get(i)[0] + className)) {
							if (className.endsWith(".xml") || className.endsWith(".XML")) {
								if (templetcode != null && !"".equals(templetcode.trim()) && className.toLowerCase().indexOf(templetcode.toLowerCase()) < 0) {
									continue;
								}
								//直接从xml路径取 【杨科/2013-03-25】
								tmp_o = getDefaultTMOByCode(entryName, 3, true);
								if (tablename != null && !"".equals(tablename.trim())) {
									if (tmp_o.getPub_templet_1Data().getAttributeValue("savedtablename") != null && tmp_o.getPub_templet_1Data().getAttributeValue("savedtablename").toString().toLowerCase().indexOf(tablename.toLowerCase()) >= 0) {
									} else if (tmp_o.getPub_templet_1Data().getAttributeValue("tablename") != null && tmp_o.getPub_templet_1Data().getAttributeValue("tablename").toString().toLowerCase().indexOf(tablename.toLowerCase()) >= 0) {
									} else {
										continue;
									}
								}
								tmp = new HashVO();
								tmp.setAttributeValue("templetcode", className.substring(0, className.lastIndexOf(".")));
								tmp.setAttributeValue("templetname", tmp_o.getPub_templet_1Data().getAttributeValue("templetname"));
								tmp.setAttributeValue("tablename", tmp_o.getPub_templet_1Data().getAttributeValue("tablename"));
								tmp.setAttributeValue("savedtablename", tmp_o.getPub_templet_1Data().getAttributeValue("savedtablename"));
								tmp.setAttributeValue("datapolicy", tmp_o.getPub_templet_1Data().getAttributeValue("datapolicy"));//xml中查询需要显示策略【李春娟/2016-06-15】
								tmp.setAttributeValue("pk_pub_templet_1", null);
								tmp.setAttributeValue("savetype", other_dir.get(i)[1] + "XML_" + other_dir.get(i)[0] + className);
								all.add(tmp);
							}
						}
					}
				} else {
					File file = new File(new URI(url.toExternalForm()));
					File[] files = file.listFiles();
					String name = null;
					for (int j = 0; j < files.length; j++) {
						if (!files[j].isDirectory()) {
							name = files[j].getName();
							if (name.endsWith(".xml") || name.endsWith(".XML")) {
								if (templetcode != null && !"".equals(templetcode.trim()) && name.toLowerCase().indexOf(templetcode.toLowerCase()) < 0) {
									continue;
								}
								//直接从xml路径取 【杨科/2013-03-25】
								tmp_o = getDefaultTMOByCode(other_dir.get(i)[0] + name, 3, true);
								if (tablename != null && !"".equals(tablename.trim())) {
									if (tmp_o.getPub_templet_1Data().getAttributeValue("savedtablename") != null && tmp_o.getPub_templet_1Data().getAttributeValue("savedtablename").toString().toLowerCase().indexOf(tablename.toLowerCase()) >= 0) {
									} else if (tmp_o.getPub_templet_1Data().getAttributeValue("tablename") != null && tmp_o.getPub_templet_1Data().getAttributeValue("tablename").toString().toLowerCase().indexOf(tablename.toLowerCase()) >= 0) {
									} else {
										continue;
									}
								}
								tmp = new HashVO();
								tmp.setAttributeValue("templetcode", name.substring(0, name.lastIndexOf(".")));
								tmp.setAttributeValue("templetname", tmp_o.getPub_templet_1Data().getAttributeValue("templetname"));
								tmp.setAttributeValue("tablename", tmp_o.getPub_templet_1Data().getAttributeValue("tablename"));
								tmp.setAttributeValue("savedtablename", tmp_o.getPub_templet_1Data().getAttributeValue("savedtablename"));
								tmp.setAttributeValue("datapolicy", tmp_o.getPub_templet_1Data().getAttributeValue("datapolicy"));//xml中查询需要显示策略【李春娟/2016-06-15】
								tmp.setAttributeValue("pk_pub_templet_1", null);
								tmp.setAttributeValue("savetype", other_dir.get(i)[1] + "XML_" + other_dir.get(i)[0] + name);
								all.add(tmp);
							}
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		rtn.add((HashVO[]) all.toArray(new HashVO[0]));
		if (sb.length() > 0) {
			rtn.add("未取到" + sb + "XML模板,是因为在打jar包时未勾选【add directory entries】!");
		} else {
			rtn.add(sb);
		}

		return rtn;
	}

	/**
	 * 将XML中的数据导入到数据库中!! 该方法与上面的方法是一对! 也将是以后进行数据导出,复制,迁移的最主要的方法!! 即一切都是基于这两个方法! XML数据格式也是最简洁,平铺的!
	 * @return
	 * @throws Exception
	 */
	public String importRecordsXMLToTable(String[] _xmlFileNams, String _dsName, boolean _isReCreateId) throws Exception {
		return this.importRecordsXMLToTable(_xmlFileNams, _dsName, _isReCreateId, null);
	}

	/**
	 * param可以指定一些字段的特殊值,格式为:表名_字段名,值
	 * @param _xmlFileNams
	 * @param _dsName
	 * @param _isReCreateId
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String importRecordsXMLToTable(String[] _xmlFileNams, String _dsName, boolean _isReCreateId, HashMap param) throws Exception {
		ArrayList al_sqls = new ArrayList(); //
		CommDMO commDMO = new CommDMO();
		HashMap targetTaColsMap = new HashMap(); //为了保证两个库在发生字段不一致的情况也能插入!! 即兼容性要相当健壮! 必须要知道目标库中的某张表的实际列名!!
		HashMap pkValueMap = new HashMap(); //记录某个表的某个旧的id对应的新的值!!! key是【表名.列名.旧值】,value是新值
		for (int i = 0; i < _xmlFileNams.length; i++) { //遍历各个文件!!
			InputStream fileInStream = this.getClass().getResourceAsStream(_xmlFileNams[i]); //文件流
			if (fileInStream == null) {
				throw new WLTAppException("文件[" + _xmlFileNams[i] + "]不存在!"); //
			}
			org.jdom.Document doc = new org.jdom.input.SAXBuilder().build(fileInStream); // 加载XML!!
			java.util.List recordList = doc.getRootElement().getChildren("record"); //取得所有的record字段!!
			for (int j = 0; j < recordList.size(); j++) { //遍历各条记录!!
				org.jdom.Element recordNode = (org.jdom.Element) recordList.get(j); //
				String str_tabName = recordNode.getAttributeValue("tabname"); //表名!
				String str_pkName = recordNode.getAttributeValue("pkname"); //主键字段名!
				String str_seName = recordNode.getAttributeValue("sename"); //主键字段对应的序列名!可能为空

				List targetTabColList = null; //
				if (!targetTaColsMap.containsKey(str_tabName.toUpperCase())) { //如果没有,则从DB取
					String[] str_taregtTableCols = commDMO.getTableDataStructByDS(_dsName, "select * from " + str_tabName + " where 1=2").getHeaderName(); //列名!
					for (int p = 0; p < str_taregtTableCols.length; p++) {
						str_taregtTableCols[p] = str_taregtTableCols[p].toUpperCase(); //为了精准,都转换成大写!!!
					}
					targetTabColList = Arrays.asList(str_taregtTableCols); //
					targetTaColsMap.put(str_tabName.toUpperCase(), targetTabColList); //
				} else { //如果有,则取出来
					targetTabColList = (List) targetTaColsMap.get(str_tabName.toUpperCase()); ////
				}

				InsertSQLBuilder isql = new InsertSQLBuilder(str_tabName); //创建SQL构造器!!
				java.util.List colList = recordNode.getChildren(); //取得各个列!
				for (int k = 0; k < colList.size(); k++) { //遍历各个列!!!
					org.jdom.Element colNode = (org.jdom.Element) colList.get(k); //某一个列!!
					String str_colName = colNode.getAttributeValue("name"); //列名!!!
					String str_refTabid = colNode.getAttributeValue("reftabid"); //对应的外键
					String str_colValue = colNode.getValue(); //value值!!

					if (targetTabColList.contains(str_colName.toUpperCase())) { //必须目标表中具有该字段我才加入拼接SQL!
						if (param != null) {//此处可以指定某些字段为特殊值，比如复制模板可以用到
							if (param.containsKey((str_tabName + "_" + str_colName).toUpperCase())) {
								isql.putFieldValue(str_colName, (String) param.get((str_tabName + "_" + str_colName).toUpperCase()));
								continue;
							} else if (param.containsKey((str_tabName + "_" + str_colName).toLowerCase())) {
								isql.putFieldValue(str_colName, (String) param.get((str_tabName + "_" + str_colName).toLowerCase()));
								continue;
							}
						}
						if (_isReCreateId) { //如果是重新生成主键!!!
							if (str_colName.equalsIgnoreCase(str_pkName)) { //如果是主键!
								String str_newValue = commDMO.getSequenceNextValByDS(_dsName, str_seName.toUpperCase()); //从序列取得新的主键值!!!
								isql.putFieldValue(str_colName, str_newValue); //置入新值!!
								String str_pkMapKey = (str_tabName + "." + str_colName + "." + str_colValue).toUpperCase(); //
								pkValueMap.put(str_pkMapKey, str_newValue); //
							} else { //如果不是主键
								if (str_refTabid != null) { //如果是外键!!! 比如麻烦!!! 必须去取一下!!
									String str_pkMapKey = (str_refTabid + "." + str_colValue).toUpperCase(); //key
									String str_convertValue = (String) pkValueMap.get(str_pkMapKey); //在缓存中找到对应的值
									isql.putFieldValue(str_colName, str_convertValue); //置入值!!
								} else { //即不是主键,也不是外键!!
									isql.putFieldValue(str_colName, str_colValue); //置入值!!
								}
							}
						} else {
							isql.putFieldValue(str_colName, str_colValue); //置入值!!
						}
					}
				}

				al_sqls.add(isql.getSQL()); //插入列表
			}
		}
		commDMO.executeBatchByDS(_dsName, al_sqls); //实际插入数据库!!
		return "成功"; //
	}

	private String getPKOrSEName(String[][] _pkConstraint, String _tableName, int _type) {
		if (_pkConstraint == null || _pkConstraint.length == 0) {
			return "";
		}
		for (int i = 0; i < _pkConstraint.length; i++) {
			if (_pkConstraint[i][0].equalsIgnoreCase(_tableName)) { //如果匹配上!!
				return _pkConstraint[i][_type]; //
			}
		}
		return "";
	}

	private String getForeignPKRef(String[][] _foreignPKConstraint, String _tabName, String _colName) {
		if (_foreignPKConstraint == null || _foreignPKConstraint.length == 0) { //如果没定义,则直接返回!!!
			return "";
		}
		String str_key = _tabName + "." + _colName; //将本表的表名与字段名先拼起来!!作为索引!!
		for (int i = 0; i < _foreignPKConstraint.length; i++) { //遍历所有外键约束!!!
			if (_foreignPKConstraint[i][0].equalsIgnoreCase(str_key)) {
				return " " + "reftabid=\"" + _foreignPKConstraint[i][1] + "\""; //
			}
		}
		return "";
	}

	/**
	 * 从一张表中取得1000条记录! 以后要保证任意一张表能导出XML,为了保证内存不溢出,必须一千条一千条的导!
	 * 即客户端与服务器端是每一千次查询一把!
	 * @param _dsName
	 * @param _tableName
	 * @param _pkName
	 * @return
	 * @throws Exception
	 */
	public HashMap getXMlFromTable1000Records(String _dsName, String _tableName, String _pkName, int _beginNo) throws Exception {
		return getXMLMapDataBySQL(_dsName, _tableName, _pkName, _beginNo); //
	}

	//取内容
	private HashMap getXMLMapDataBySQL(String _dsName, String _tabName, String _pkName, int _beginNo) throws Exception {
		StringBuilder sb_xml = new StringBuilder(); //
		sb_xml.append("<?xml version=\"1.0\" encoding=\"GBK\"?>\r\n"); //
		sb_xml.append("<root>\r\n"); //
		HashVOStruct hvst = null;
		if (_pkName == null) { //如果主键为空
			hvst = new CommDMO().getHashVoStructByDS(_dsName, "select * from " + _tabName); //取出所有记录!!!
		} else {
			hvst = new CommDMO().getHashVoStructByDS(_dsName, "select * from " + _tabName + " where " + _pkName + ">" + _beginNo + " order by " + _pkName, 500); //取出前500条记录!!
		}
		String[] str_keys = hvst.getHeaderName(); //列名
		HashVO[] hvs = hvst.getHashVOs(); //
		if (hvs == null || hvs.length <= 0) { //如果没找到数据,则返回空!!!
			return null; //
		}
		String str_itemValue = null; //
		int li_realEndPKValue = -1; //结束记录的主键值!!
		int li_returnRecordCount = 0; //
		for (int i = 0; i < hvs.length; i++) { //遍历各行!!
			sb_xml.append("<!--" + (i + 1) + "-->\r\n"); //
			sb_xml.append("<record tabname=\"" + _tabName + "\">\r\n"); //
			for (int j = 0; j < str_keys.length; j++) { //遍历各列!!
				str_itemValue = hvs[i].getStringValue(str_keys[j], ""); //取得值!!
				if (str_itemValue.indexOf("<") >= 0 || str_itemValue.indexOf(">") >= 0 || str_itemValue.indexOf("&") >= 0) { //如果本身的<>
					str_itemValue = "<![CDATA[" + str_itemValue + "]]>"; //
				}
				sb_xml.append("  <col name=\"" + str_keys[j] + "\">" + str_itemValue + "</col>\r\n"); ////
			}
			sb_xml.append("</record>\r\n"); //
			sb_xml.append("\r\n"); //
			li_returnRecordCount++; //

			if (_pkName != null && i == hvs.length - 1) { //如果是最后一个!!
				li_realEndPKValue = hvs[i].getIntegerValue(_pkName); //
			}
		}
		sb_xml.append("</root>\r\n"); //
		HashMap returnMap = new HashMap(); //
		returnMap.put("记录数", new Integer(li_returnRecordCount)); //
		returnMap.put("结束点", new Integer(li_realEndPKValue)); //
		returnMap.put("内容", sb_xml.toString()); //
		return returnMap; //返回新的id值!!
	}

	//将一张表的数据全部导出然后可以重置某个主键的值,即从1开始,这个方法用到迁移或导出数据时用到!!!
	public String getXMLDataBySQLAsResetId(String _dsName, String _sql, String _tabName, String _resetIdField) throws Exception {
		HashVOStruct hvst = new CommDMO().getHashVoStructByDS(_dsName, _sql); //取出所有记录!!!
		String[] str_keys = hvst.getHeaderName(); //列名
		HashVO[] hvs = hvst.getHashVOs(); //
		if (hvs == null || hvs.length <= 0) { //如果没找到数据,则返回空!!!
			return null; //
		}
		String str_itemValue = null; //
		StringBuilder sb_xml = new StringBuilder(); //
		sb_xml.append("<?xml version=\"1.0\" encoding=\"GBK\"?>\r\n"); //
		sb_xml.append("<root>\r\n"); //
		for (int i = 0; i < hvs.length; i++) { //遍历各行!!
			sb_xml.append("<!--" + (i + 1) + "-->\r\n"); //
			sb_xml.append("<record tabname=\"" + _tabName + "\">\r\n"); //
			for (int j = 0; j < str_keys.length; j++) { //遍历各列!!
				str_itemValue = hvs[i].getStringValue(str_keys[j], ""); //取得值!!
				if (str_itemValue.indexOf("<") >= 0 || str_itemValue.indexOf(">") >= 0 || str_itemValue.indexOf("&") >= 0) { //如果本身的<>
					str_itemValue = "<![CDATA[" + str_itemValue + "]]>"; //
				}
				if (_resetIdField != null && str_keys[j].equalsIgnoreCase(_resetIdField)) { //正好是需要重新设置的字段名
					sb_xml.append("  <col name=\"" + str_keys[j] + "\">" + (i + 1) + "</col>\r\n"); ////
				} else {
					sb_xml.append("  <col name=\"" + str_keys[j] + "\">" + str_itemValue + "</col>\r\n"); ////
				}
			}
			sb_xml.append("</record>\r\n"); //
			sb_xml.append("\r\n"); //
		}
		sb_xml.append("</root>\r\n"); //
		return sb_xml.toString();
	}

	//导入1000条记录!
	public void importXmlToTable1000Records(String _dsName, String _fileName, String _xml) throws Exception {
		_xml = _xml.replaceAll("[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]", "");
		java.io.ByteArrayInputStream bins = new java.io.ByteArrayInputStream(_xml.getBytes("GBK")); //
		org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
		org.jdom.Document rootNode = null; //
		try {
			rootNode = builder.build(bins); //
		} catch (Exception ex) {
			System.err.println("读取xml文件[" + _fileName + "]发生异常:"); //
			ex.printStackTrace(); //
			throw ex; //
		}
		bins.close(); //关闭!!
		java.util.List recordNodeList = rootNode.getRootElement().getChildren("record"); //遍历所有记录!

		HashSet hstCols = new HashSet(); //
		ArrayList al_sqls = new ArrayList(); //

		for (int i = 0; i < recordNodeList.size(); i++) { //遍历各条记录!!!
			org.jdom.Element recordNode = (org.jdom.Element) recordNodeList.get(i);
			String str_tableName = recordNode.getAttributeValue("tabname"); //表名
			if (i == 0) { //记录下目标表的结构,下面用来计算只插入目标表中存在的列!
				TableDataStruct tabstrct = getCommDMO().getTableDataStructByDS(_dsName, "select * from " + str_tableName + " where 1=2"); //
				String[] str_cols = tabstrct.getHeaderName(); //
				for (int k = 0; k < str_cols.length; k++) {
					hstCols.add(str_cols[k].toLowerCase()); //
				}
			}
			//System.out.println("表名[" + str_tableName + "]"); ////
			InsertSQLBuilder isql = new InsertSQLBuilder(str_tableName); //
			java.util.List colNodeList = recordNode.getChildren("col"); //
			for (int j = 0; j < colNodeList.size(); j++) { //遍历所有Col子结点!!即各列!!
				org.jdom.Element colNode = (org.jdom.Element) colNodeList.get(j);
				String str_colName = colNode.getAttributeValue("name"); //列名!
				String str_colValue = colNode.getText(); //列值!
				if (hstCols.contains(str_colName.toLowerCase())) { //如果目标库中实际包含该列,则才进行插入!! 从而保证结构发生差异时都能导入!!
					isql.putFieldValue(str_colName, str_colValue); //插入值!!有单引号会处理!
				}
			}
			al_sqls.add(isql.getSQL()); //插入这批记录
		}
		getCommDMO().executeBatchByDS(_dsName, al_sqls, false); //往目标表中批量插入这500条记录!!!
	}

	public ArrayList getExportSql(String _sourcedsname, String _destdsname, String _table, boolean _iscreate, boolean _isinsert) throws Exception {
		//StringBuffer returnStr = new StringBuffer();
		ArrayList v_sqls = new ArrayList(); //
		String pk_fieldname = null; //
		HashVOStruct hvst = getCommDMO().getHashVoStructByDS(_sourcedsname, "select * from " + _table + " where 1=2"); //
		String[] str_cols = hvst.getHeaderName();
		String[] str_coltypes = hvst.getHeaderTypeName(); //
		int[] li_collength = hvst.getHeaderLength(); //

		// //
		if (str_cols[0].toUpperCase().startsWith("PK_")) { // 如果第一列是以PK开头，则作为主键．
			pk_fieldname = str_cols[0]; //
		} else { //
			for (int i = 0; i < str_cols.length; i++) { //
				if (str_cols[i].equalsIgnoreCase("id")) { //
					pk_fieldname = "id"; // 如果有id字段则用id字段
					break; //
				}
			}
		}

		//
		String str_destdbtype = getDBType(_destdsname); // 目标库的类型..
		if (_iscreate) {
			StringBuffer sb_sql = new StringBuffer(); //
			sb_sql.append("create table " + _table + " (");
			for (int i = 0; i < str_coltypes.length; i++) {
				String str_coltype = convertColType(str_coltypes[i], str_destdbtype); //
				if (li_collength[i] > 4000 && "varchar2".equals(str_coltype)) {
					sb_sql.append(str_cols[i] + " " + "clob"); //
				} else {
					sb_sql.append(str_cols[i] + " " + str_coltype); //
					if (str_coltype.equalsIgnoreCase("text") || str_coltype.equalsIgnoreCase("clob")) {
					} else {

						sb_sql.append("(" + li_collength[i] + ")");

					}
				}
				if (i == str_coltypes.length - 1) { // 如果是最后一列
					if (pk_fieldname != null) {
						sb_sql.append(","); // 如果有主键，则加逗号..
					}
				} else {
					sb_sql.append(","); //
				}
			}

			if (pk_fieldname != null) {
				if (_table.length() < 25) {
					sb_sql.append("constraint pk_" + _table + " primary key (" + pk_fieldname + ")"); //
				} else {
					sb_sql.append("primary key (" + pk_fieldname + ")"); //
				}
			}
			sb_sql.append(");\r\n");
			//returnStr.append(sb_sql.toString());
			v_sqls.add(sb_sql.toString());
		}

		int li_insertrecords = 0; //
		if (_isinsert) { // 如果需要插入数据
			int li_batchrecords = 1; //
			HashVO[] hvs_count = getCommDMO().getHashVoArrayByDS(_sourcedsname, "select count(*) c1 from " + _table); // //
			int li_recordcount = hvs_count[0].getIntegerValue("c1").intValue(); //
			if (li_recordcount > 0) {
				if (pk_fieldname == null) { // 如果主键为空
					li_insertrecords = insertAllData(_sourcedsname, _destdsname, str_destdbtype, _table); // 如果没有主键,直接插入所有数据
				} else {
					HashVO[] hvs_min = getCommDMO().getHashVoArrayByDS(_sourcedsname, "select min(" + pk_fieldname + ") minc1 from " + _table); //
					Integer li_MinID = hvs_min[0].getIntegerValue("minc1"); //
					if (li_MinID == null) { // 如果主键值为空
						li_insertrecords = insertAllData(_sourcedsname, _destdsname, str_destdbtype, _table); // 如果没有主键,直接插入所有数据
					} else {
						int li_minid = li_MinID.intValue(); //
						HashVO[] hvs_max = getCommDMO().getHashVoArrayByDS(_sourcedsname, "select max(" + pk_fieldname + ") maxc1 from " + _table); //
						int li_maxid = hvs_max[0].getIntegerValue("maxc1").intValue(); //

						int li_begin = li_minid;
						int li_end = li_begin + li_batchrecords; //

						while (li_begin <= li_maxid) {
							HashVO[] hvs_record = getCommDMO().getHashVoArrayByDS(_sourcedsname, "select * from " + _table + " where " + pk_fieldname + " between '" + li_begin + "' and '" + li_end + "'"); //
							li_insertrecords = li_insertrecords + hvs_record.length; // //

							for (int r = 0; r < hvs_record.length; r++) {
								String str_columnnames = ""; // 所有字段名
								String str_columvalues = ""; // 所有字段值
								for (int i = 0; i < str_cols.length; i++) {
									str_columnnames = str_columnnames + str_cols[i]; //
									str_columvalues = str_columvalues + getInsertValue(hvs_record[r].getStringValue(str_cols[i]), str_destdbtype); //
									if (i != str_cols.length - 1) { //
										str_columnnames = str_columnnames + ",";
										str_columvalues = str_columvalues + ",";
									}
								}
								//returnStr.append("insert into " + _table + " (" + str_columnnames + ") values (" + str_columvalues + ");\r\n");
								v_sqls.add("insert into " + _table + " (" + str_columnnames + ") values (" + str_columvalues + ");"); //
							}
							li_begin = li_end + 1;
							li_end = li_begin + li_batchrecords; //
						}
					}
				}
			}
		}
		return v_sqls;
	}

	/**
	 * 从源库往目标库插入所有数据...
	 * 
	 * @param str_cols
	 * @param _sourcedsname
	 * @param _destdsname
	 * @param str_destdbtype
	 * @param _table
	 * @throws Exception
	 */
	private int insertAllData(String _sourcedsname, String _destdsname, String str_destdbtype, String _table) throws Exception {
		HashVOStruct hvstruct = getCommDMO().getHashVoStructByDS(_sourcedsname, "select * from " + _table); //
		String[] str_cols = hvstruct.getHeaderName(); //
		HashVO[] hvs_record = hvstruct.getHashVOs(); //

		ArrayList v_sqls = new ArrayList(); //
		for (int r = 0; r < hvs_record.length; r++) {
			String str_columnnames = ""; // 所有字段名
			String str_columvalues = ""; // 所有字段值
			for (int i = 0; i < str_cols.length; i++) {
				str_columnnames = str_columnnames + str_cols[i]; //
				str_columvalues = str_columvalues + getInsertValue(hvs_record[r].getStringValue(str_cols[i]), str_destdbtype); //
				if (i != str_cols.length - 1) { //
					str_columnnames = str_columnnames + ",";
					str_columvalues = str_columvalues + ",";
				}
			}
			v_sqls.add("insert into " + _table + " (" + str_columnnames + ") values (" + str_columvalues + ")"); //
		}
		getCommDMO().executeBatchByDSImmediately(_destdsname, v_sqls); // 立即插入..
		return hvs_record.length; //
	}

	private String getDBType(String _dsname) {
		DataSourceVO dsVO = ServerEnvironment.getInstance().getDataSourceVO(_dsname); //
		return dsVO.getDbtype(); //
	}

	/**
	 * 导视图定义
	 * 
	 * @param _sourcedsname
	 * @param _destdsname
	 * @param _viewname
	 * @return
	 */
	public Object transferDBView(String _sourcedsname, String _destdsname, String _viewname) throws Exception {
		String str_sourcedbtype = getDBType(_sourcedsname); // 源库类型..
		if (str_sourcedbtype.equalsIgnoreCase(WLTConstants.SQLSERVER)) { // SQLServer
			HashVO[] hvs = getCommDMO().getHashVoArrayByDS(_sourcedsname, "select view_definition from INFORMATION_SCHEMA.VIEWS  where table_name='" + _viewname + "'"); //
			if (hvs.length > 0) {
				String str_viewdefine = hvs[0].getStringValue("view_definition"); //
				if (str_viewdefine != null) { //
					createView(_destdsname, str_viewdefine); //
				} else {
					throw new WLTAppException("视图定义为空"); //
				}
			} else {
				throw new WLTAppException("没有找到视图定义"); //
			}
		} else if (str_sourcedbtype.equalsIgnoreCase(WLTConstants.ORACLE)) { // Oracle
			String define = commDMO.getStringValueByDS(_sourcedsname, "select text from user_views where view_name = '" + _viewname.toUpperCase() + "'");
			if (define == null || define.equals("")) {
				throw new WLTAppException("没有找到视图定义"); //
			} else {
				createViewFromOracle(_destdsname, define, _viewname);
			}
		} else if (str_sourcedbtype.equalsIgnoreCase(WLTConstants.MYSQL)) { // Mysql
			HashVO[] hvs = getCommDMO().getHashVoArrayByDS(_sourcedsname, "select view_definition,table_schema from INFORMATION_SCHEMA.VIEWS  where table_name='" + _viewname + "'"); //
			if (hvs.length > 0) {
				String str_viewdefine = hvs[0].getStringValue("view_definition"); //
				String table_schema = hvs[0].getStringValue("table_schema"); //
				if (str_viewdefine != null && !"".equals(str_viewdefine)) { //
					createViewMySQL(_destdsname, str_viewdefine, _viewname, table_schema); //
				} else {
					throw new WLTAppException("视图定义为空"); //
				}
			} else {
				//在这个地方可以添加一个通用方法，和产品安装原理一样，从views.xml中读取视图ddl
				throw new WLTAppException("没有找到视图定义"); //
			}
		}
		return null;
	}

	//从oracle读取出视图结构
	private void createViewFromOracle(String _destdsname, String _define, String _viewName) throws Exception {
		StringBuffer viewsb = new StringBuffer();
		viewsb.append(" create or replace view " + _viewName + " as \r\n");
		viewsb.append(_define);
		getCommDMO().executeBatchByDSImmediately(_destdsname, new String[] { viewsb.toString() });
	}

	/**
	 * 创建视图..
	 */
	private void createView(String _destdsname, String _define) throws Exception {
		String str_destdbtype = getDBType(_destdsname); // 目标库类型
		_define = tbUtil.replaceAll(_define, "dbo.", ""); //
		String[] str_defineitems = tbUtil.split(_define, "\r"); //
		StringBuffer sb_sql = new StringBuffer(); //
		for (int i = 0; i < str_defineitems.length; i++) {
			if (str_destdbtype.equals(WLTConstants.MYSQL)) { // 如果是MYSQL,则还需要截掉--后面的数据!!!
				int li_pos = str_defineitems[i].indexOf("--"); //
				if (li_pos > 0) {
					sb_sql.append(str_defineitems[i].subSequence(0, li_pos)); //
				} else {
					sb_sql.append(str_defineitems[i]); //
				}
			} else {
				sb_sql.append(str_defineitems[i]); //
			}
		}

		getCommDMO().executeUpdateByDS(_destdsname, sb_sql.toString()); //
	}

	/**
	 * 创建视图源为MYSQL..
	 */
	private void createViewMySQL(String _destdsname, String _define, String table_name, String table_schema) throws Exception {
		String str_destdbtype = getDBType(_destdsname); // 目标库类型
		int endlength = _define.indexOf("*/");
		if (endlength > 0) { //有注释需要截取。
			_define = _define.substring(endlength + 2, _define.length());
		}
		_define = tbUtil.replaceAll(_define, "`", ""); //
		_define = tbUtil.replaceAll(_define, table_schema + ".", ""); //

		String[] str_defineitems = tbUtil.split(_define, "\r"); //
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("create view " + table_name + " as ");
		for (int i = 0; i < str_defineitems.length; i++) {
			if (str_destdbtype.equals(WLTConstants.MYSQL)) { // 如果是MYSQL,则还需要截掉--后面的数据!!!
				int li_pos = str_defineitems[i].indexOf("--"); //
				if (li_pos > 0) {
					sb_sql.append(str_defineitems[i].subSequence(0, li_pos)); //
				} else {
					sb_sql.append(str_defineitems[i]); //
				}
			} else {
				sb_sql.append(str_defineitems[i]); //
			}
		}

		getCommDMO().executeUpdateByDS(_destdsname, sb_sql.toString()); //
	}

	/**
	 * 富文本框保存数据的方法!!
	 * @param _batchid
	 * @param _doc64code
	 * @param _text
	 * @param _sql
	 * @return
	 * @throws Exception
	 */
	public String saveStylePadDocument(String _batchid, String _doc64code, String _text, String[] _sqls) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		String str_batchid = _batchid; //
		if (str_batchid == null) {
			str_batchid = commDMO.getSequenceNextValByDS(null, "S_PUB_STYLEPADDOC_BATCHID"); //取新的!!!
		} else {
			commDMO.executeUpdateByDS(null, "delete from pub_stylepaddoc where batchid='" + str_batchid + "'"); //先删除掉原来的数据
		}
		if (_doc64code != null) { //如果有内容才进行真正的删除!!
			ArrayList al_data = tbUtil.split(_doc64code, 10, 2000); //分割
			ArrayList al_sql = new ArrayList(); //
			for (int i = 0; i < al_data.size(); i++) { //遍历各行!
				String[] str_rowData = (String[]) al_data.get(i); //该行的数据
				String str_id = commDMO.getSequenceNextValByDS(null, "S_PUB_STYLEPADDOC"); //主键
				for (int j = 0; j < str_rowData.length; j++) {
					if (j == 0) { //如果是第一列
						InsertSQLBuilder isql_insert = new InsertSQLBuilder("pub_stylepaddoc"); //
						isql_insert.putFieldValue("id", str_id); //
						isql_insert.putFieldValue("batchid", str_batchid); //批号
						isql_insert.putFieldValue("seq", "" + (i + 1)); //序号
						isql_insert.putFieldValue("doc0", str_rowData[0]); //第一列的值
						al_sql.add(isql_insert.getSQL()); //
					} else {
						UpdateSQLBuilder isql_update = new UpdateSQLBuilder("pub_stylepaddoc", "id='" + str_id + "'"); //
						isql_update.putFieldValue("doc" + j, str_rowData[j]); //第一列的值
						al_sql.add(isql_update.getSQL()); //
					}
				}
			}
			commDMO.executeBatchByDS(null, al_sql); //实际插入数据!!!
		}

		if (_sqls != null) {
			try {
				UpdateSQLBuilder isql_update = new UpdateSQLBuilder(_sqls[0], _sqls[2] + "='" + _sqls[3] + "'"); //
				if (_text == null || _text.trim().equals("")) {
					isql_update.putFieldValue(_sqls[1], ""); //
				} else {
					isql_update.putFieldValue(_sqls[1], _text + "#@$" + str_batchid + "$@#"); //
				}
				commDMO.executeUpdateByDS(null, isql_update); //
			} catch (Exception ex) {
				ex.printStackTrace(); //吃掉异常,即即使这个保存失败仍然是可以继续的,因为主表可能继续保存!
			}
		}
		return str_batchid; //
	}

	/**
	 * 富文本框保存数据的方法!!
	 * @param _batchid
	 * @param _doc64code
	 * @param _text
	 * @param _sql
	 * @return
	 * @throws Exception
	 */
	public String saveImageUploadDocument(String _batchid, String _doc64code, String _tabName, String _pkName, String _pkValue) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		String str_batchid = _batchid; //
		if (str_batchid == null) {
			str_batchid = commDMO.getSequenceNextValByDS(null, "S_PUB_IMGUPLOAD_BATCHID"); //取新的!!!
		} else {
			commDMO.executeUpdateByDS(null, "delete from pub_imgupload where batchid='" + str_batchid + "'"); //先删除掉原来的数据
		}
		if (_doc64code != null) { //如果有内容才进行真正的删除!!
			ArrayList al_data = tbUtil.split(_doc64code, 10, 2000); //分割
			ArrayList al_sql = new ArrayList(); //
			for (int i = 0; i < al_data.size(); i++) { //遍历各行!
				String[] str_rowData = (String[]) al_data.get(i); //该行的数据
				String str_id = commDMO.getSequenceNextValByDS(null, "S_PUB_IMGUPLOAD"); //主键
				for (int j = 0; j < str_rowData.length; j++) {
					if (j == 0) { //如果是第一列
						InsertSQLBuilder isql_insert = new InsertSQLBuilder("pub_imgupload"); //
						isql_insert.putFieldValue("id", str_id); //
						isql_insert.putFieldValue("batchid", str_batchid); //批号
						isql_insert.putFieldValue("billtable", _tabName); //
						isql_insert.putFieldValue("billpkname", _pkName); //
						isql_insert.putFieldValue("billpkvalue", _pkValue); //
						isql_insert.putFieldValue("seq", "" + (i + 1)); //序号
						isql_insert.putFieldValue("img0", str_rowData[0]); //第一列的值
						al_sql.add(isql_insert.getSQL()); //
					} else {
						UpdateSQLBuilder isql_update = new UpdateSQLBuilder("pub_imgupload", "id='" + str_id + "'"); //
						isql_update.putFieldValue("img" + j, str_rowData[j]); //第一列的值
						al_sql.add(isql_update.getSQL()); //
					}
				}
			}
			commDMO.executeBatchByDS(null, al_sql); //实际插入数据!!!
		}
		return str_batchid; //
	}

	/*
	 * 得到创建表的标准语句！
	 */
	public void getCreateTableSQL(String dsName, String _destdsname, String table) throws Exception {
		String pk_fieldname = null; //
		HashVOStruct hvst = getCommDMO().getHashVoStructByDS(dsName, "select * from " + table + " where 1=2"); //
		String[] str_cols = hvst.getHeaderName();
		String[] str_coltypes = hvst.getHeaderTypeName(); //
		int[] li_collength = hvst.getHeaderLength(); //
		int[] li_precision = hvst.getPrecision(); //精确度
		int[] li_scale = hvst.getScale(); //小数有效位数
		if (str_cols[0].toUpperCase().startsWith("PK_")) { // 如果第一列是以PK开头，则作为主键．
			pk_fieldname = str_cols[0]; //
		} else { //
			for (int i = 0; i < str_cols.length; i++) { //
				if (str_cols[i].equalsIgnoreCase("id")) { //
					pk_fieldname = "id"; // 如果有id字段则用id字段
					break; //
				}
			}
		}
		//
		String str_destdbtype = getDBType(_destdsname); // 目标库的类型..
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("create table " + table + " (");
		for (int i = 0; i < str_coltypes.length; i++) {
			String str_coltype = convertColType(str_coltypes[i], str_destdbtype); //
			if (li_collength[i] > 4000 && "varchar2".equals(str_coltype)) {
				sb_sql.append(str_cols[i] + " " + "clob"); //
			} else {
				sb_sql.append(str_cols[i] + " " + str_coltype); //
				if (str_coltype.equalsIgnoreCase("text") || str_coltype.equalsIgnoreCase("clob")) {
				} else {
					if ((str_coltype.equalsIgnoreCase("decimal") || str_coltype.equalsIgnoreCase("number")) && li_scale[i] > 0) {
						sb_sql.append("(" + li_precision[i] + "," + li_scale[i] + ")"); //decimal(3,1);
					} else {
						sb_sql.append("(" + li_collength[i] + ")");
					}
				}
				if (str_cols[i].equalsIgnoreCase(pk_fieldname)) {
					sb_sql.append(" not null");
				}
			}
			if (i == str_coltypes.length - 1) { // 如果是最后一列
				if (pk_fieldname != null) {
					sb_sql.append(","); // 如果有主键，则加逗号..
				}
			} else {
				sb_sql.append(","); //
			}
		}
		if (pk_fieldname != null) {
			if (table.length() < 25) {
				sb_sql.append("constraint pk_" + table + " primary key (" + pk_fieldname + ")"); //
			} else {
				sb_sql.append("primary key (" + pk_fieldname + ")"); //
			}
		}
		sb_sql.append(")");
		getCommDMO().executeUpdateByDSImmediately(_destdsname, sb_sql.toString()); // 在目标库中立即执行
	}

	public String safeMoveData(String _sourcedsname, String _destdsname, String tablename, HashMap tableMap, String type, HashMap conditon) throws Exception { // 一张表一个事物，500条记录/次
		if (type.equalsIgnoreCase("VIEW")) {//视图
			Object obj = transferDBView(_sourcedsname, _destdsname, tablename);
			if (obj == null) {
				return "视图[" + tablename + "]创建成功!\r\n";
			}
			return "";
		}
		//第二个数据库是否存在此表。不存在就创建。
		StringBuffer record = new StringBuffer();
		if (!tableMap.containsKey(tablename.toLowerCase())) { // 不包含此表需要创建
			try {
				getCreateTableSQL(_sourcedsname, _destdsname, tablename);
			} catch (Exception ex) {
				ex.printStackTrace();
				record.append("[" + tablename + "]表创建失败!详细请查看服务器端控制台!\r\n");
			}
			record.append("[" + tablename + "]表创建成功!\r\n");
		}
		boolean check_data = true;
		if (conditon != null) {
			check_data = (Boolean) conditon.get("check_data");
			if (!check_data) { //如果不迁移数据，只创建表就可以了!
				return record.length() == 0 ? "[" + tablename + "]已存在!\r\n" : record.toString();
			}
		}
		StringBuffer retsb = new StringBuffer();
		int li_beginNo = 0; // 起始号!
		String con_sql = "select count(*) from " + tablename + " where 1=1";
		int li_countall = Integer.parseInt(commDMO.getStringValueByDS(_sourcedsname, con_sql)); //
		if (li_countall == 0) {
			return "[" + tablename + "]表数据为空！\r\n";
		}
		while (1 == 1) { // 死循环
			long ll_1 = System.currentTimeMillis(); //
			if (li_beginNo >= li_countall) {
				break;
			}
			HashMap returnMap = safeMoveDataby500(_sourcedsname, _destdsname, tablename, li_beginNo, conditon);
			retsb.append(returnMap.get("日志"));
			if (returnMap == null) { // 如果为空了则直接返回
				break; //
			}
			li_beginNo = (Integer) returnMap.get("结束点"); // 实际内容的结束号!
		}
		hvst_new = null; // 一次访问后就清空！ 
		return "[" + tablename + "]表成功迁移" + li_countall + "条数据\r\n" + retsb.toString();
	}

	public HashMap safeMoveDataby500(String _sourcedsname, String _destdsname, String table, int _beginNo, HashMap conditon) throws Exception {
		String _tableName = table;
		int li_batchRecords = 500; // 一次取500条记录!!!
		int li_endNo = _beginNo + li_batchRecords; // [>=1 and <=1000][>=1001
		// and <=2000][>=2001 and
		// <=3000]
		String dbType = ServerEnvironment.getDefaultDataSourceType();
		StringBuffer sql_sb = new StringBuffer();
		if (_sourcedsname != null && !_sourcedsname.equals("null")) {
			DataSourceVO vo = ServerEnvironment.getInstance().getDataSourceVO(_sourcedsname);
			if (vo != null) {
				dbType = vo.getDbtype();
			}
		}
		if (dbType.equalsIgnoreCase("MYSQL")) {
			sql_sb.append("select *  from " + _tableName + " order by 1 limit " + _beginNo + "," + li_batchRecords);
		} else if (dbType.equalsIgnoreCase("ORACLE")) {
			sql_sb.append("select " + _tableName + ".* from (select " + _tableName + ".*,Rownum RN from (select " + _tableName + ".* from " + _tableName + " order by 1 )" + _tableName);
			sql_sb.append("  where Rownum <=" + (_beginNo + li_batchRecords) + ") " + _tableName);
			sql_sb.append(" where RN > " + _beginNo);
		} else if (dbType.equals("SQLSERVER")) {
			StringBuilder sb_sql_new = new StringBuilder(); //
			sb_sql_new.append("with _t1 as "); //
			sb_sql_new.append("("); //
			sb_sql_new.append("select row_number() over (order by 1 asc) _rownum,");
			sb_sql_new.append(" * from " + _tableName); // 将原来的select后面开始的内容接上来!
			sb_sql_new.append(") ");
			sb_sql_new.append("select top " + (li_batchRecords) + " * from _t1 where _rownum >= " + _beginNo + ""); // 分页!!!
			sql_sb.append(sb_sql_new.toString()); //
		} else if (dbType.equalsIgnoreCase("db2")) { //2013-6-21郝明添加支持从db2迁移数据到其他库
			StringBuilder sb_sql_new = new StringBuilder(); //
			sb_sql_new.append("with t1_ as "); //
			sb_sql_new.append("("); //
			sb_sql_new.append("select row_number() over (order by 1 asc) RN,"); ////
			sb_sql_new.append(table + ".* from " + table); // //如果取的字段就是*,则DB2有个变态的地方，就是必须在*前面加上表名，否则会报错,SQLServer与Oracle都没有这个问题!!!
			sb_sql_new.append(") ");
			sb_sql_new.append("select * from t1_ where RN > " + _beginNo + "  and RN<=" + (_beginNo + li_batchRecords)); //分页!!!
			sql_sb.append(sb_sql_new.toString());
		}
		return safeMoveDataCurrDo(_sourcedsname, _destdsname, _tableName, sql_sb.toString(), _beginNo + li_batchRecords, conditon); //
	}

	HashVOStruct hvst_new = null;

	public HashMap safeMoveDataCurrDo(String _sourcedsname, String _destdsname, String table, String sql, int lastNum, HashMap conditon) throws Exception {
		boolean check_quick = true;
		if (conditon != null) {
			check_quick = (Boolean) conditon.get("check_quick");
		}
		StringBuffer record = new StringBuffer();
		HashMap returnMap = new HashMap();
		HashVOStruct hvst = commDMO.getHashVoStructByDS(_sourcedsname, sql); // 取出500条数据。
		String[] str_keys = hvst.getHeaderName(); // 列名
		List l = new ArrayList();
		boolean haveRN = false;
		for (int i = 0; i < str_keys.length; i++) {
			if (!str_keys[i].equalsIgnoreCase("RN")) {
				l.add(str_keys[i]);
			} else {
				haveRN = true;
			}
		}
		if (haveRN) {
			str_keys = (String[]) l.toArray(new String[0]); //oracle 
		}
		HashVO[] str_vos = hvst.getHashVOs();
		List insertSql = new ArrayList();
		DataSourceVO vo = ServerEnvironment.getInstance().getDataSourceVO(_destdsname);
		String _desttype = ""; // 得到新数据库类型
		if (vo != null) {
			_desttype = vo.getDbtype();
		}

		if (hvst_new == null) {
			hvst_new = commDMO.getHashVoStructByDS(_destdsname, "select * from " + table + " where 1=2"); // 新的表结构		
		}
		//有严重问题！！！！！！！！！！  新表和老表的结构不一样。顺序不同！
		String[] head = hvst_new.getHeaderName();
		String[] headtype = hvst_new.getHeaderTypeName();
		int[] str_length_new = hvst_new.getHeaderLength();
		int[] l_type = hvst_new.getHeaderType();
		HashMap newTableInfo = new HashMap(); //这个hashmap用来存放新表的信息[类型，长度，headerType]
		for (int i = 0; i < head.length; i++) {
			newTableInfo.put(head[i].toLowerCase(), new Object[] { headtype[i], str_length_new[i], l_type[i] });
		}
		for (int i = 0; i < str_vos.length; i++) { //
			StringBuffer items = new StringBuffer();
			StringBuffer values = new StringBuffer();
			for (int j = 0; j < str_keys.length; j++) { // 遍历各列!!
				if (str_keys[j].equalsIgnoreCase("RN"))
					continue; // 由于用RN来进行条数的范围过滤，所以多出RN无用列。
				String str_itemValue = str_vos[i].getStringValue(str_keys[j], ""); // 取得值!!
				if (str_itemValue == null || str_itemValue.trim().equals(""))
					continue;
				Object[] obj = (Object[]) newTableInfo.get(str_keys[j].toLowerCase());
				if (obj == null) {
					continue;
				}
				String itemtype = (String) obj[0];
				int itemlength = (Integer) obj[1];
				int l_type_n = (Integer) obj[2];
				int newLength = compareHeadlength(_desttype, str_itemValue, itemlength);
				if (newLength > 0 && newLength <= 4000 && itemlength != 4000) {
					String alterSQL = null;
					if (itemtype.equalsIgnoreCase("decimal") || itemtype.equalsIgnoreCase("number")) {
						if (_desttype.equalsIgnoreCase("DB2")) {
							alterSQL = " alter table " + table + " alter column " + str_keys[j] + " set data type " + itemtype + "(" + newLength + ")";
						} else {
							alterSQL = " alter table " + table + " modify " + str_keys[j] + " " + itemtype + "(" + newLength + ")";
						}
					} else {
						if (_desttype.equalsIgnoreCase("DB2")) {
							alterSQL = " alter table " + table + " alter column " + str_keys[j] + " set data type " + itemtype + "(" + newLength + ")";
						} else {
							alterSQL = " alter table " + table + " modify " + str_keys[j] + " " + itemtype + "(" + newLength + ")";
						}
					}
					if (check_quick) {
						commDMO.executeBatchByDSImmediately(_destdsname, new String[] { alterSQL });
					} else {
						commDMO.executeBatchByDS(_destdsname, new String[] { alterSQL });
					}
					hvst_new = commDMO.getHashVoStructByDS(_destdsname, "select * from " + table + " where 1=2"); // 新的表结构
					record.append("表[" + table + "]的字段" + str_keys[j] + "长度为:" + str_length_new[j] + "太小强制转换为:" + newLength + "\r\n");
					str_length_new = hvst_new.getHeaderLength(); //更新缓存
					newTableInfo.put(str_keys[j].toLowerCase(), new Object[] { itemtype, newLength, l_type_n });
				} else if (newLength > 4000) { //如果大于4000就截取
					if ("MYSQL".equalsIgnoreCase(_desttype)) {
						str_itemValue = str_itemValue.substring(0, 3999);
						record.append("MYSQL:表[" + table + "]的字段" + str_keys[j] + "长度为:" + str_length_new[j] + "，把数据强制截取前4000个字符" + "\r\n");
					} else { //oracle按字节走
						String s = new String(str_itemValue.getBytes(), "GBK");
						byte[] b = s.getBytes("utf-8"); //所有字节
						str_itemValue = new String(b, 0, 3996, "utf-8");
						record.append("ORACLE[DB2]:表[" + table + "]的字段" + str_keys[j] + "长度为:" + str_length_new[j] + "，把数据强制截取前4000个字节" + "\r\n");
					}
				}
				if (items.length() == 0) {
					items.append(str_keys[j]);
					values.append(getInsertValue(str_itemValue, _desttype, l_type_n));
				} else {
					items.append("," + str_keys[j]);
					values.append("," + getInsertValue(str_itemValue, _desttype, l_type_n));
				}
			}
			String insertSQL = "insert into " + table + " (" + items.toString() + ") values (" + values.toString() + ") ";
			insertSql.add(insertSQL);
		}
		if (check_quick) {
			commDMO.executeBatchByDSImmediately(_destdsname, insertSql, false);
		} else {
			commDMO.executeBatchByDS(_destdsname, insertSql, false, false);
		}
		returnMap.put("结束点", lastNum);
		returnMap.put("日志", record);
		return returnMap;
	}

	public int compareHeadlength(String dbType, String value, int oldLength) {
		if ("MYSQL".equalsIgnoreCase(dbType)) {
			int length = value.length();
			if (length > oldLength) {
				if (length < 100) { //小于100的直接搞成100
					length = 100;
				} else if (length >= 100 && length < 1000) {
					length = (length % 100 + 1) * 100; //多100。取整
				} else if (length >= 1000 && length <= 4000) { //大于1000, 搞成4000得了
					length = 4000;
				} else {
					length = 4001; //越界自动截取字符串.
				}
				return length;
			}
		} else {
			int length = 0;
			try {
				length = value.getBytes("UTF-8").length;
			} catch (UnsupportedEncodingException e) {
				length = value.getBytes().length;
				e.printStackTrace();
			}
			if (length > oldLength) {
				if (length < 100) { //小于100的直接搞成200
					length = 200;
				} else if (length >= 100 && length < 1000) {
					length = (length / 100 + 1) * 100; //多100。取整
				} else if (length >= 1000 && length < 4000) { //大于1000, 搞成4000得了
					length = 4000;
				} else {
					length = 4001; //越界自动截取字符串.
				}
				return length;
			}
		}
		return -1; //不需要修改
	}
}