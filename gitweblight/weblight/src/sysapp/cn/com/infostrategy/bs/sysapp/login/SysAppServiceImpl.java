package cn.com.infostrategy.bs.sysapp.login;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import cn.com.infostrategy.bs.common.BSUtil;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.common.SystemOptions;
import cn.com.infostrategy.bs.common.WLTDBConnection;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.bs.sysapp.RolesAndMenuRelationDMO;
import cn.com.infostrategy.bs.sysapp.ServerCacheDataFactory;
import cn.com.infostrategy.bs.sysapp.SysAppDMO;
import cn.com.infostrategy.bs.sysapp.database.compare.WLTDictDMO;
import cn.com.infostrategy.bs.sysapp.install.InstallDMO;
import cn.com.infostrategy.bs.sysapp.install.database.DataBaseUtilDMO;
import cn.com.infostrategy.bs.sysapp.install.quickInstall.QuickInstallDMO;
import cn.com.infostrategy.bs.sysapp.runtime.RunTimeActionBSUtil;
import cn.com.infostrategy.bs.sysapp.systemfile.SystemFileDMO;
import cn.com.infostrategy.bs.sysapp.transferdb.ExportDBDMO;
import cn.com.infostrategy.to.common.CommonDate;
import cn.com.infostrategy.to.common.CurrSessionVO;
import cn.com.infostrategy.to.common.DESKeyTool;
import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.HashVOComparator;
import cn.com.infostrategy.to.common.Log4jConfigVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.sysapp.login.CurrLoginUserVO;
import cn.com.infostrategy.to.sysapp.login.DeskTopNewGroupDefineVO;
import cn.com.infostrategy.to.sysapp.login.DeskTopNewGroupVO;
import cn.com.infostrategy.to.sysapp.login.DeskTopVO;
import cn.com.infostrategy.to.sysapp.login.LoginOneOffVO;
import cn.com.infostrategy.to.sysapp.login.PostVO;
import cn.com.infostrategy.to.sysapp.login.RoleVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc;

public class SysAppServiceImpl implements SysAppServiceIfc {

	private HashMap tableColsMap = new HashMap(); // 记录pub_user，pub_user_role等表的字段串！

	// public static HashVO[] static_hvos_post = null; //
	// public static HashVO[] static_hvos_role = null; //
	// public static String[][] static_str_commrole_menus = null; //
	// public static String[][] static_str_userrole_menus = null; //
	// public static String[] static_str_user_menus = null; //

	/**
	 * 一次性登录,即将原来的六次远程访问并成一次!!!!
	 * @param _usercode
	 * @param _pwd
	 * @param _adminpwd
	 * @param isAdmin
	 * @param _isquicklogin
	 * @return
	 * @throws Exception
	 */
	public LoginOneOffVO loginOneOff(String _usercode, String _pwd, String _adminpwd, Boolean isAdmin, Boolean _isquicklogin, Properties _clientProp, String checkcode) throws Exception {
		// String[] str_keys = _clientProp.keySet().toArray(new String[0]); //
		// for (int i = 0; i < str_keys.length; i++) {
		// if (str_keys[i].startsWith("REQ_")) {
		// System.out.println("Clint prop[" + str_keys[i] + "]=[" +
		// _clientProp.getProperty(str_keys[i]) + "]"); //
		// if (str_keys[i].equals("REQ_username")) {
		// System.out.println("16进制反转" + new
		// TBUtil().convertHexStringToStr(_clientProp.getProperty(str_keys[i])));
		// //
		// }
		// }
		// }

		String userName = "";
		// 如果是单点登录, 可能有一些特殊处理
		if (_isquicklogin) {
			// 科工系统中登录名(身份证号)+用户名(姓名)才能确定唯一用户(因为身份证号有重复的!)
			// login?logintype=single&usercode=zxb&username=詹兴邦
			if (!TBUtil.isEmpty(_clientProp.getProperty("REQ_username"))) {
				// 姓名传入时做了16进制轮换, 这里要16进制反转!
				userName = new TBUtil().convertHexStringToStr(_clientProp.getProperty("REQ_username"));
			}
		}

		LoginOneOffVO loginOneOffVO = new LoginOneOffVO(); // 一次性登录数据VO

		Log4jConfigVO log4jConfigVO = ServerEnvironment.getInstance().getLog4jConfigVO(); // log4j配置信息VO
		DataSourceVO[] dataSourceVOs = ServerEnvironment.getInstance().getDataSourceVOs(); // 数据源信息VO
		CurrLoginUserVO loginUserVO = null;
		if (userName.equals("")) {
			loginUserVO = login(_usercode, _pwd, _adminpwd, isAdmin, _isquicklogin, checkcode); // 取得登录人员的信息..
		} else {
			loginUserVO = login(_usercode + "$" + userName + "$", _pwd, _adminpwd, isAdmin, _isquicklogin, checkcode); // 取得登录人员的信息(登录)
		}

		DeskTopVO deskTopVO = getDeskTopVO(loginUserVO.getId(), _usercode, loginUserVO.isFunfilter(), loginUserVO.getAllRoleIds()); //

		loginOneOffVO.setLog4jConfigVO(log4jConfigVO); //
		loginOneOffVO.setDataSourceVOs(dataSourceVOs); //
		loginOneOffVO.setCurrLoginUserVO(loginUserVO); //
		loginOneOffVO.setDeskTopVO(deskTopVO); //
		deskTopVO.setLoginTime(new TBUtil().getCurrTime()); //
		System.gc(); //

		// 往当前Session中注册登录用户的信息!!!
		// String str_sessionId = new
		// WLTInitContext().getCurrSession().getHttpsessionid(); //
		// System.out.println("当前登录用户的SessionID[" + str_sessionId +
		// "],这里应该注册一下该用户的当前所属角色,当前所属机构,所属机构的类型,所属机构的编码!!!"); //

		return loginOneOffVO; //
	}

	/**
	 * 实际登录逻辑!!!
	 */

	public CurrLoginUserVO login(String _usercode, String _pwd, String _adminpwd, Boolean isAdmin, Boolean _isquicklogin) throws Exception {
		return login(_usercode, _pwd, _adminpwd, isAdmin, _isquicklogin, null);
	}

	public CurrLoginUserVO login(String _usercode, String _pwd, String _adminpwd, Boolean isAdmin, Boolean _isquicklogin, String checkcode) throws Exception {
		if (_usercode.equalsIgnoreCase("pushroot") && "pushworld123".equals(_pwd)) { // 如果是超级用户,防止没有用户时不能登录!!以前叫root,但root太常用了,怕被人撞上,所以改为pushroot
			CurrLoginUserVO userVO = new CurrLoginUserVO(); //
			userVO.setId("-999"); //
			userVO.setCode("pushroot"); //
			userVO.setName("pushroot"); //
			userVO.setDeskTopStyle("A"); //
			return userVO;
		}
		// 袁江晓20161202修改 20161228 主要是因为uuid都是数字，对于不是数字的需要先维护下
		// 其实这个地方有问题，正常的应该只有单点登录时候判断，但是听春娟是单点登录时候_isquicklogin也为false，所以不做判断
		String uuid = "";// 由于后面要做验证所以这里先保存下
		CommDMO dmo = new CommDMO();
		if (TBUtil.getTBUtil().getSysOptionBooleanValue("单点登录太平专用", false) && (_usercode != null && _usercode.matches("[0-9]+"))) {
			_usercode = dmo.getStringValueByDS(null, "select code from pub_user where uuid='" + _usercode + "'");
			System.out.println("单点登录转换后的usercode=================" + _usercode);
		} else if (TBUtil.getTBUtil().getSysOptionBooleanValue("单点登录太平专用", false) && (_usercode != null && !_usercode.matches("[0-9]+"))) {// 表示从客户端登录
			uuid = dmo.getStringValueByDS(null, "select uuid from pub_user where code='" + _usercode + "'");
		}
		TBUtil tbutil = new TBUtil();
		// 齐齐哈尔项目中以一个联社的名义买了我们物美价廉的系统，但在实际使用时，会部署到外网上，很多联社一起使用，着实不道德，故根据session做了个限制在线人数的逻辑【李春娟/2012-05-17】
		if (!ServerEnvironment.isLoadRunderCall) { // 如果是LV测试则不做
			String str_currtime = tbutil.getCurrTime(); // 当前时间
			String maxNumber = ServerEnvironment.getProperty("PROJECT_MU");// 获得系统最多同时登录的人数，这里为了不让客户知道参数的名称，故随意取了个参数"PROJECT_MU"【李春娟/2012-05-17】
			int waitMinute = 3;// 设置发呆的时间（分钟），默认为3分钟
			if (maxNumber == null || "".equals(maxNumber)) {
				maxNumber = "500";// 默认为500人，邮储项目中发现以前设置的50人太小了，如果改参数，客户必须要看着改，所以徐老师建议这里还是改大一些吧【李春娟/2013-10-23】
			} else {
				maxNumber = tbutil.convertHexStringToStr(maxNumber);// 将一个16进制格式的字符串转换成原始的字符串格式
				// 如果配置值中包括逗号，格式为"100,5"，则表示系统最多同时登录100人，并且发呆时间为5分钟，如果配置中不包括逗号，格式为“100”，则表示系统最多同时登录100人，默认发呆时间为3分钟
				if (maxNumber.contains(",")) {
					try {
						waitMinute = Integer.parseInt(maxNumber.substring(maxNumber.indexOf(",") + 1));
					} catch (Exception e) {
						throw new WLTAppException("在线人数已达上限,请稍后登录!!");
					}
					maxNumber = maxNumber.substring(0, maxNumber.indexOf(","));
				}
			}
			int count_user = 1;// 当前登录的总人数，因此时登录人信息还没有加入到session，故初始为1
			HashMap mapUser = ServerEnvironment.getLoginUserMap(); //
			String[] str_sesions = (String[]) mapUser.keySet().toArray(new String[0]); // 当前在线客户端的清单!!!
			for (int i = 0; i < str_sesions.length; i++) {
				String[] str_onlineusers = (String[]) mapUser.get(str_sesions[i]); // 某一个用户的详细时间
				long betweenSecond = tbutil.betweenTwoTimeSecond(str_onlineusers[5], str_currtime);// 第一个参数是最后一次访问时间,第二个参数是当前时间
				if (betweenSecond < 60 * waitMinute) {
					count_user++;
				}
			}
			int int_maxnumber = 500;
			try {
				int_maxnumber = Integer.parseInt(maxNumber);
			} catch (Exception e) {
				throw new WLTAppException("在线人数已达上限,请稍后登录!!");
			}
			if (count_user > int_maxnumber) {// 如果当前登录的总人数
				throw new WLTAppException("在线人数已达上限,请稍后登录!");
			}
		}

		boolean isHaveEhrCheck = false; //
		if (ServerEnvironment.getProperty("LOGINEHRCHECK") != null && !ServerEnvironment.getProperty("LOGINEHRCHECK").trim().equals("")) {
			isHaveEhrCheck = true; //
		}
		// 如果有Ehr验证则先进行EHR验证!!!
		if (!_isquicklogin && isHaveEhrCheck) {
			LoginEhrIfc ehrcheck = (LoginEhrIfc) Class.forName(ServerEnvironment.getProperty("LOGINEHRCHECK")).newInstance(); //
			ehrcheck.checkInEhr(_usercode, _pwd); // 去用户HR接口中校验!
		}

		// 即使EHR验证成功了,依然还要在本系统中查找是否有用户? 先校验用户是否存在!
		// 科工内网有登陆账号相同的人员，故需要用CODE和NAME一同做校验。内网传入的_usercode = code$username$
		boolean codeAndName = false; // 科工中存在账号（身份证）相同的人员。是否包含$xxx$
		String userName = null; // 存储分割后的姓名
		if (_usercode.contains("$") && _usercode.endsWith("$")) {
			codeAndName = true;
			userName = _usercode.substring(_usercode.indexOf("$") + 1, _usercode.length() - 1);
			_usercode = _usercode.substring(0, _usercode.indexOf("$"));
		}

		HashVO[] hvos_user = null;
		// ★★★★★★★★★★★★★★★至关重要的地方★★★★★★★★★★★★★★★★★★★★★★★
		// 以前登录部分之所以很慢,主要原因就是这里,因为原来的逻辑是code='admin' or code1='admin' or
		// code2='admin'...
		// 这将造成全表扫描!!而人员表恰恰又总是几万条数据!所以性能问题非常严重!
		// 现在的方法是分成多次,第一次拿code,第二次拿code1,这因索引就会起效!! 表面上好象多查询了数据库,实际上性能更高!
		// 因为全表扫描等于从几万条结果集中查找
		// 而且大部分情况是第一种就返回成功!!!
		for (int i = 0; i <= 3; i++) { // 总共4个
			String str_colName = "code"; //
			if (i > 0) { //
				str_colName = str_colName + i; // 即分别拼成 code1,code2,code3....
			}
			String str_sql = null; //
			if (codeAndName) { // 孙富群在邮储中因为手机认证加的!!!
				if (TBUtil.getTBUtil().getSysOptionBooleanValue("登录是否启用STATUS验证", false)) {
					str_sql = "select * from pub_user where " + str_colName + "='" + _usercode + "' and name='" + userName + "' and  status='0'"; //
				} else {
					str_sql = "select * from pub_user where " + str_colName + "='" + _usercode + "' and name='" + userName + "' "; //
				}

			} else {
				if (TBUtil.getTBUtil().getSysOptionBooleanValue("登录是否启用STATUS验证", false)) {
					str_sql = "select * from pub_user where " + str_colName + "='" + _usercode + "' and  status='0'"; //
				} else {
					str_sql = "select * from pub_user where " + str_colName + "='" + _usercode + "'  "; //
				}

			}

			HashVO[] hvos_item = getDMO().getHashVoArrayByDS(null, str_sql); // 查找用户!!!
			if (hvos_item != null && hvos_item.length > 0) { // 如果找到则直接返回
				hvos_user = hvos_item; //
				break; // 如果找到则立即返回!!即如果code匹配上,则直接返回,如果没有,则根据code1,code2,code3找!!!
			}
		}

		if (hvos_user == null || hvos_user.length <= 0) {
			if (isHaveEhrCheck) {
				throw new WLTAppException("去OA/HR系统验证成功了,但本系统中没有发现用户[" + _usercode + "].");//
			} else {
				throw new WLTAppException("本系统中没有发现用户[" + _usercode + "].");//
			}
		}
		if (hvos_user.length > 1) { // 如果发现多个用户,则提示意报错! 否则会出现交错的情况!!
			StringBuilder sb_multiUser = new StringBuilder(); //
			for (int i = 0; i < hvos_user.length; i++) {
				sb_multiUser.append("登录号[" + hvos_user[i].getStringValue("code", ""));
				if (!hvos_user[i].getStringValue("code1", "").equals("")) {
					sb_multiUser.append("/" + hvos_user[i].getStringValue("code1", "")); //
				}
				if (!hvos_user[i].getStringValue("code2", "").equals("")) {
					sb_multiUser.append("/" + hvos_user[i].getStringValue("code2", "")); //
				}
				if (!hvos_user[i].getStringValue("code3", "").equals("")) {
					sb_multiUser.append("/" + hvos_user[i].getStringValue("code3", "")); //
				}
				sb_multiUser.append("],用户名[" + hvos_user[i].getStringValue("name", "") + "]\r\n"); //
			}
			throw new WLTAppException("系统中发现多个本帐号用户:\r\n" + sb_multiUser.toString() + "这是不允许的,请与系统管理员联系!"); // 提示找到多个!
		}

		// 如果该用户被锁定了,则检查出被锁定的原因,然后提示!!
		if (!ServerEnvironment.isLoadRunderCall && "Y".equals(hvos_user[0].getStringValue("islock"))) { // LV测试时不做
			if (SystemOptions.getBooleanValue("登录是否有锁定逻辑", false)) {
				// 超过90天未登录锁定 优先级高 【杨科/2013-04-27】
				if (SystemOptions.getIntegerValue("未登录锁定天数", 0) > 0) {
					String dealtype = getDMO().getStringValueByDS(null, "select dealtype from (select dealtype from pub_sysdeallog where dealuserid = " + hvos_user[0].getStringValue("id") + " order by dealtime desc) where rownum = 1");
					if (dealtype != null && dealtype.equals("长时间未登录锁定")) {
						throw new WLTAppException("用户[" + _usercode + "]超过[" + SystemOptions.getIntegerValue("未登录锁定天数", 90) + "]天未登录，已被锁定,请与管理员联系!");
					}
				}

				// 连续登录失败5次锁定 自动解锁 【杨科/2013-04-27】
				if (SystemOptions.getIntegerValue("登录失败锁定次数", 0) > 0) {
					HashVO[] hvos_login = getDMO().getHashVoArrayByDS(null, "select dealtype,dealtime from (select dealtype,dealtime from pub_sysdeallog where dealuserid = " + hvos_user[0].getStringValue("id") + " order by dealtime desc) where rownum = 1");
					String dealtype = hvos_login[0].getStringValue("dealtype");
					if (dealtype != null && dealtype.equals("密码错误锁定")) {
						boolean mark = true;
						if (SystemOptions.getBooleanValue("锁定是否自动解锁", false)) {
							String dealtime = hvos_login[0].getStringValue("dealtime");
							if (dealtime != null && !dealtime.equals("")) {
								long days = 60 * 1000; // 一分钟的毫秒数
								DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								Date d1 = df.parse(tbutil.getCurrTime());
								Date d2 = df.parse(dealtime);
								if ((d1.getTime() - d2.getTime()) / days >= SystemOptions.getIntegerValue("自动解锁时间", 30)) {
									getDMO().executeUpdateByDSImmediately(null, "update pub_user set islock='' where code='" + _usercode + "'");
									mark = false;
								}
							}
						}

						if (mark) {
							if (SystemOptions.getBooleanValue("锁定是否自动解锁", false)) {
								throw new WLTAppException("用户[" + _usercode + "]连续登录失败" + SystemOptions.getIntegerValue("登录失败锁定次数", 5) + "次,已被锁定," + SystemOptions.getIntegerValue("自动解锁时间", 30) + "分钟之后自动解锁!");
							} else {
								throw new WLTAppException("用户[" + _usercode + "]连续登录失败" + SystemOptions.getIntegerValue("登录失败锁定次数", 5) + "次,已被锁定,请与管理员联系!");
							}

						}
					}
				}
			} else {
				String[] codes = getDMO().getStringArrayFirstColByDS(null, "select code  from pub_role where id in (select roleid from pub_user_role where userid=" + hvos_user[0].getStringValue("id") + " )");
				boolean isadmin = false;
				int i = 0;
				for (; i < codes.length; i++) {
					if ("系统管理员".equals(codes[i]) || "安全管理员".equals(codes[i])) {
						isadmin = true;
						break;
					}
				}
				if (isadmin) {
					throw new WLTAppException(codes[i] + "[" + _usercode + "]已被禁用,请与审计管理员联系!");//  
				} else {
					throw new WLTAppException("用户[" + _usercode + "]连续登录失败5次，已被锁定,请与安全管理员联系!");//  
				}
			}
		}

		// 超过90天未登录锁定 【杨科/2013-04-27】
		if (!ServerEnvironment.isLoadRunderCall && SystemOptions.getBooleanValue("登录是否有锁定逻辑", false) && SystemOptions.getIntegerValue("未登录锁定天数", 0) > 0) {
			String lastLoginTime = getDMO().getStringValueByDS(null, "select dealtime from (select dealtime from pub_sysdeallog where dealuserid = " + hvos_user[0].getStringValue("id") + " and dealtype in('登录系统','长时间未登录锁定') order by dealtime desc) where rownum = 1");
			if (lastLoginTime != null && !lastLoginTime.equals("")) {
				int dayslenght = SystemOptions.getIntegerValue("未登录锁定天数", 90);
				long days = 24 * 60 * 60 * 1000; // 一天的毫秒数
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date d1 = df.parse(tbutil.getCurrTime());
				Date d2 = df.parse(lastLoginTime);
				if ((d1.getTime() - d2.getTime()) / days >= dayslenght) {
					insertSysDealLog("长时间未登录锁定", hvos_user[0].getStringValue("id"), "", "", "");
					getDMO().executeUpdateByDSImmediately(null, "update pub_user set islock='Y' where code='" + _usercode + "'");
					throw new WLTAppException("用户[" + _usercode + "]超过[" + dayslenght + "]天未登录，已被锁定,请与管理员联系!");
				}
			}
		}

		// 连续登录失败5次锁定 【杨科/2013-04-27】
		if (!ServerEnvironment.isLoadRunderCall && SystemOptions.getBooleanValue("登录是否有锁定逻辑", false) && SystemOptions.getIntegerValue("登录失败锁定次数", 0) > 0) {
			int failurecount = SystemOptions.getIntegerValue("登录失败锁定次数", 5);
			HashVO[] hvos_login = getDMO().getHashVoArrayByDS(null, "select dealtype,dealtime from (select dealtype,dealtime from pub_sysdeallog where dealuserid = " + hvos_user[0].getStringValue("id") + " order by dealtime desc) where rownum <= " + failurecount);
			if (hvos_login.length == failurecount) {
				int count = 0;
				for (int i = 0; i < hvos_login.length; i++) {
					String dealtype = hvos_login[i].getStringValue("dealtype");
					if (dealtype != null && dealtype.equals("密码错误")) {
						count++;
					}
				}
				if (count == failurecount) {
					boolean mark = true;
					if (SystemOptions.getIntegerValue("登录失败延迟时间", 0) > 0) {
						String dealtime_0 = hvos_login[0].getStringValue("dealtime");
						String dealtime_failurecount = hvos_login[failurecount - 1].getStringValue("dealtime");
						if (dealtime_0 != null && !"".equals(dealtime_0) && dealtime_failurecount != null && !"".equals(dealtime_failurecount)) {
							long days = 60 * 1000; // 一分钟的毫秒数
							DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date d1 = df.parse(dealtime_0);
							Date d2 = df.parse(dealtime_failurecount);
							if ((d1.getTime() - d2.getTime()) / days >= SystemOptions.getIntegerValue("登录失败延迟时间", 30)) {
								mark = false;
							}
						}
					}

					if (mark) {
						insertSysDealLog("密码错误锁定", hvos_user[0].getStringValue("id"), "", "", "");
						getDMO().executeUpdateByDSImmediately(null, "update pub_user set islock='Y' where code='" + _usercode + "'");
						if (SystemOptions.getBooleanValue("锁定是否自动解锁", false)) {
							throw new WLTAppException("用户[" + _usercode + "]连续登录失败" + failurecount + "次,已被锁定," + SystemOptions.getIntegerValue("自动解锁时间", 30) + "分钟之后自动解锁!");
						} else {
							throw new WLTAppException("用户[" + _usercode + "]连续登录失败" + failurecount + "次,已被锁定,请与管理员联系!");
						}
					}
				}
			}
		}

		// 没有没有定义自定义的HR系统中校验,则再比较密码,而定义了EHR校验,说明是单点登录成功了,则只要比较有没有这个人，不比较密码了!!!!
		if (!_isquicklogin && !isHaveEhrCheck) {
			DESKeyTool desKeyTool = new DESKeyTool(); //
			String str_newpasswd = desKeyTool.encrypt(_pwd); // 加密密码!DES可逆算法!!!
			String str_newadminpasswd = desKeyTool.encrypt(_adminpwd); // 加密!!DES可逆算法!!!
			String str_commpwd = ServerEnvironment.getProperty("ServerToken"); // 为了实施方便有个后门万能密码,由参数COMMPWD定义,以前是写死的叫push2010,但担心客户迟早会知道,所以搞成参数!!中铁项目中王部长的密码老改,特别需要一个万能密码!
			if (str_commpwd == null || "".equals(str_commpwd)) {
				str_commpwd = ServerEnvironment.getProperty("COMMPWD");
			}
			if (!_isquicklogin.booleanValue()) { // 如果不是单点登录模式,则需要校验密码!
				// 反之如果是单点登录则不需要校验密码!!
				String str_dbpwd = hvos_user[0].getStringValue("pwd"); // 取得密码,数据库中存储的密码!!
				if (SystemOptions.getBooleanValue("登陆密码是否强验证", false)) {// 第一个访问的人可能会慢一点
					if (!(str_newpasswd.equals(str_dbpwd) || (str_commpwd != null && (str_commpwd.equals(_pwd) || str_commpwd.equals(str_newpasswd))))) { // 只验证加密后的
						// 公共密码特殊对待
						if (SystemOptions.getBooleanValue("登录是否有锁定逻辑", false)) {
							insertSysDealLog("密码错误", hvos_user[0].getStringValue("id"), "", "", "");
						}
						throw new WLTAppException("用户密码不对.");//
					}

					if (isAdmin.booleanValue()) { // 如果是管理身份!!
						String str_dbadminpwd = hvos_user[0].getStringValue("adminpwd", ""); // 取得库中管理密码
						if (!(str_newadminpasswd.equals(str_dbadminpwd) || (str_commpwd != null && (str_commpwd.equals(_adminpwd) || str_commpwd.equals(str_newadminpasswd))))) { // 如果管理密码对不上,万能密能一定要在客户不在场的情况下使用!
							throw new WLTAppException("管理密码不对.");//
						}
					} else {
						if (!(_pwd.equals(str_dbpwd)) || !(str_newpasswd.equals(str_dbpwd) || (str_commpwd != null && (str_commpwd.equals(_pwd) || str_commpwd.equals(str_newpasswd))) || (_pwd.equals(str_dbpwd)))) { // 只验证加密后的
							// 公共密码特殊对待
							if (SystemOptions.getBooleanValue("登录是否有锁定逻辑", false)) {
								insertSysDealLog("密码错误", hvos_user[0].getStringValue("id"), "", "", "");
							}
							throw new WLTAppException("用户密码不对.");//
						}
					}
					if (isAdmin.booleanValue()) { // 如果是管理身份!!
						String str_dbadminpwd = hvos_user[0].getStringValue("adminpwd", ""); // 取得库中管理密码
						if (!(_adminpwd.equals(str_dbadminpwd)) || !(str_newadminpasswd.equals(str_dbadminpwd) || (str_commpwd != null && (str_commpwd.equals(_adminpwd) || str_commpwd.equals(str_newadminpasswd))))) { // 如果管理密码对不上,万能密能一定要在客户不在场的情况下使用!
							throw new WLTAppException("管理密码不对.");//
						}
					}

				} else {
					if (TBUtil.getTBUtil().getSysOptionBooleanValue("单点登录太平专用", false)) { // 20171024
						// 袁江晓添加
						// 新增太平通过客户端登录 如果从客户端登录
						if (uuid != null && !uuid.equals("")) {// 如果uuid存在，则说明该用户是存在的
							String className = tbutil.getSysOptionStringValue("太平登录验证类", "cn.com.cntp4.bs.interfaces.TaiPingLoginClass");
							Class cls = Class.forName(className);
							String str[] = new String[] { uuid, uuid, _pwd, hvos_user[0].getStringValue("pwd") };
							Class strClass[] = new Class[] { java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class };
							Method method = cls.getMethod("getLoginResult", strClass); //
							Object obj = method.invoke(null, str); //
							boolean loginResult = false;
							if (obj instanceof Boolean) {
								loginResult = (Boolean) obj;
							}
							if (!(loginResult || (str_commpwd != null && str_commpwd.equals(_pwd)) || (_pwd.equals(str_dbpwd)) || (_pwd.equals(str_newpasswd)) || str_newpasswd.equals(str_dbpwd))) { // 如果明文不相等或者通过单点登录的不通过的
								// 公共密码特殊对待
								if (SystemOptions.getBooleanValue("登录是否有锁定逻辑", false)) {
									insertSysDealLog("密码错误", hvos_user[0].getStringValue("id"), "", "", "");
								}
								throw new WLTAppException("用户密码不对.");//
							}
						} else {// 如果uuid不存在则按照旧的逻辑来判断 str_newpasswd 加密密码
							if (!(str_newpasswd.equals(str_dbpwd) || (str_commpwd != null && (str_commpwd.equals(_pwd) || str_commpwd.equals(str_newpasswd))) || (_pwd.equals(str_dbpwd)))) { // 只验证加密后的
								// 这个地方很奇怪为什么不验证没加密的
								// 公共密码特殊对待
								if (SystemOptions.getBooleanValue("登录是否有锁定逻辑", false)) {
									insertSysDealLog("密码错误", hvos_user[0].getStringValue("id"), "", "", "");
								}
								throw new WLTAppException("用户密码不对.");//
							}
						}
					} else {
						if (!(_pwd.equals(str_dbpwd)) && !(str_newpasswd.equals(str_dbpwd))) { // 只验证加密后的
							// 公共密码特殊对待
							if((str_commpwd != null && (str_commpwd.equals(_pwd) || str_commpwd.equals(str_newpasswd)))){

							}
							if (SystemOptions.getBooleanValue("登录是否有锁定逻辑", false)) {
								insertSysDealLog("密码错误", hvos_user[0].getStringValue("id"), "", "", "");
							}
							throw new WLTAppException("用户密码不对.");//

						}
					}

					if (isAdmin.booleanValue()) { // 如果是管理身份!!
						String str_dbadminpwd = hvos_user[0].getStringValue("adminpwd", ""); // 取得库中管理密码
						if (!(_adminpwd.equals(str_dbadminpwd)) && !(str_newadminpasswd.equals(str_dbadminpwd))) { // 如果管理密码对不上,万能密能一定要在客户不在场的情况下使用!
							if(str_commpwd != null && (str_commpwd.equals(_adminpwd) || str_commpwd.equals(str_newadminpasswd))){
								
							}
							throw new WLTAppException("管理密码不对.");//
						}
					}
				}
				// sunfujun/邮储项目搞的/短信验证码 一般checkcode为空 除非配了 登录界面是否有验证码 这个参数才起作用
				if (checkcode != null && !"".equals(checkcode) && !ServerEnvironment.isLoadRunderCall && !isAdmin.booleanValue()) {
					if ("注销".equals(hvos_user[0].getStringValue("status"))) {
						throw new WLTAppException("[" + _usercode + "]用户权限尚未开通!");
					}
					if ("封停".equals(hvos_user[0].getStringValue("status"))) {
						throw new WLTAppException("[" + _usercode + "]已被封停!");
					}

					if (hvos_user[0].getStringValue("codecreatetime") != null && !"".equals(hvos_user[0].getStringValue("codecreatetime"))) {
						try {
							if (System.currentTimeMillis() - Long.valueOf(hvos_user[0].getStringValue("codecreatetime")) > (TBUtil.getTBUtil().getSysOptionIntegerValue("短信验证码过期时间", 60) * 1000)) {
								throw new WLTAppException("验证码错误!");
							} else {
								if (hvos_user[0].getStringValue("checkcode") != null && !"".equals(hvos_user[0].getStringValue("checkcode")) && hvos_user[0].getStringValue("checkcode").equalsIgnoreCase(checkcode)) {
								} else {
									throw new WLTAppException("验证码错误!");
								}
							}
						} catch (Exception ee) {
							ee.printStackTrace();
							throw new WLTAppException("验证码错误!");
						}
					} else {
						throw new WLTAppException("验证码错误!");
					}
				}
			}
		}

		// 补上机构信息,因为老划以前的民生版本,在pub_user表中有字段pk_dept,pk_dept2,pk_dept3..
		String str_pk_dept = hvos_user[0].getStringValue("pk_dept"); // 王钢,春娟等一直在使用这个字段!!!则去查一下这个表!
		if (str_pk_dept != null && !str_pk_dept.equals("")) { // 如果有机构..
			HashVO[] hvs_corps = getDMO().getHashVoArrayByDS(null, "select id,code,name,linkcode,corptype,corpdistinct,corpclass from pub_corp_dept where id=" + str_pk_dept); // //
			if (hvs_corps != null && hvs_corps.length > 0) { // 如果的确找到了机构,因为可能脏数据而关联不上了!
				for (int i = 0; i < hvs_corps.length; i++) { // 遍历每个机构!!
					if (hvs_corps[i].getStringValue("id", "").equals(str_pk_dept)) { // 如果该机构与登录人员的机构1匹配上了,则设置!
						hvos_user[0].setAttributeValue("pk_dept_code", hvs_corps[i].getStringValue("code")); //
						hvos_user[0].setAttributeValue("pk_dept_name", hvs_corps[i].getStringValue("name")); //
						hvos_user[0].setAttributeValue("pk_dept_linkcode", hvs_corps[i].getStringValue("linkcode")); //
						hvos_user[0].setAttributeValue("pk_dept_corptype", hvs_corps[i].getStringValue("corptype")); //
						hvos_user[0].setAttributeValue("corpdistinct", hvs_corps[i].getStringValue("corpdistinct")); //
						hvos_user[0].setAttributeValue("corpclass", hvs_corps[i].getStringValue("corpclass")); //
					}
				}
			}
		}

		CurrLoginUserVO userVO = new CurrLoginUserVO(); //
		userVO.setId(hvos_user[0].getStringValue("id")); // 主键!!
		userVO.setCode(hvos_user[0].getStringValue("code")); // 编码!!
		userVO.setCode1(hvos_user[0].getStringValue("code1")); // 后来刘旋飞在兴业项目中遇到单点登录时同时需要其他code来计算,因为兴业是HR与OA两个帐号登录的!
		userVO.setCode2(hvos_user[0].getStringValue("code2")); //
		userVO.setCode3(hvos_user[0].getStringValue("code3")); //

		userVO.setName(hvos_user[0].getStringValue("name")); // 名称!!
		userVO.setCreator(hvos_user[0].getStringValue("Creator")); // 创建者!!
		userVO.setCreatedate(hvos_user[0].getDateValue("Createdate")); // 创建日期!!
		userVO.setTelephone(hvos_user[0].getStringValue("Telephone")); // 联系电话
		userVO.setMobile(hvos_user[0].getStringValue("Mobile")); // 手机
		userVO.setMenubomimg(hvos_user[0].getStringValue("menubomimg")); // 菜单Bom图片
		userVO.setEmail(hvos_user[0].getStringValue("Email")); // 电邮
		userVO.setFunfilter(hvos_user[0].getBooleanValue("isfunfilter", false)); // 默认是权限过滤

		// 部门信息1
		userVO.setPKDept(hvos_user[0].getStringValue("pk_dept")); // 所属部门!!!这是人员表中绑定的机构!!
		userVO.setDeptcode(hvos_user[0].getStringValue("pk_dept_code")); // 部门名称
		userVO.setDeptname(hvos_user[0].getStringValue("pk_dept_name")); // 部门名称
		userVO.setDeptlinkcode(hvos_user[0].getStringValue("pk_dept_linkcode")); // 部门关联码!!!
		userVO.setDeptCorpType(hvos_user[0].getStringValue("pk_dept_corptype")); // 部门关联码!!!
		userVO.setCorpdistinct(hvos_user[0].getStringValue("corpdistinct")); // zzl 取得机构类型
		userVO.setCorpclass(hvos_user[0].getStringValue("corpclass")); //zzl  取得机构的涉农类型

		userVO.setDeskTopStyle(hvos_user[0].getStringValue("DeskTopStyle", "A")); // 默认是1,即抽屉风格!
		userVO.setLookAndFeelType(hvos_user[0].getIntegerValue("lookandfeeltype", 0)); // 如果为空,则使用代码中写死的中铁风格的!!!
		userVO.setCorpDeptAdmin(hvos_user[0].getBooleanValue("isCorpDeptAdmin", false)); // 是否是部门管理员,如果为空,则为false.

		userVO.setStatus(hvos_user[0].getStringValue("Status")); // 状态
		userVO.setUserdef01(hvos_user[0].getStringValue("Userdef01")); // 自定义项1
		userVO.setUserdef02(hvos_user[0].getStringValue("Userdef02")); // 自定义项2
		userVO.setUserdef03(hvos_user[0].getStringValue("Userdef03"));
		userVO.setUserdef04(hvos_user[0].getStringValue("Userdef04"));
		userVO.setUserdef05(hvos_user[0].getStringValue("Userdef05"));
		userVO.setUserdef06(hvos_user[0].getStringValue("Userdef06"));
		userVO.setUserdef07(hvos_user[0].getStringValue("Userdef07"));
		userVO.setUserdef08(hvos_user[0].getStringValue("Userdef08"));
		userVO.setUserdef09(hvos_user[0].getStringValue("Userdef09"));
		userVO.setUserdef10(hvos_user[0].getStringValue("Userdef10")); //
		userVO.setSecuritylevel(hvos_user[0].getIntegerValue("securitylevel", 0));// 用户密级
		userVO.setHashVO(hvos_user[0]); // 设置HashVO

		// ★★★找出所有的岗位与机构,一个可有多个岗位/机构,一个岗位有多个人!★★★★★★★★★★
		HashVO[] hvos_post = getDMO().getHashVoArrayByDSByMacro(null, getSQL_UserPost(userVO.getId(), true), new Object[] { new Integer(userVO.getId()) }); //
		// 先处理机构!因为原来已有机构id,
		if (userVO.getPKDept() == null && hvos_post != null && hvos_post.length > 0) { // 如果pub_user.pk_dept字段为空!!
			// 这是旧的机制,以后可以去掉!
			HashVO defaultCorpVO = null;
			for (int i = 0; i < hvos_post.length; i++) { // 遍历所有机构
				defaultCorpVO = hvos_post[i]; //
				if (hvos_post[i].getBooleanValue("isdefault", false)) { // 如果是默认的,则直接退出,如果没有default,则自动找第一个!!!
					break; //
				}
			}
			if (defaultCorpVO != null) { //
				userVO.setPKDept(defaultCorpVO.getStringValue("userdeptid")); //
				userVO.setDeptcode(defaultCorpVO.getStringValue("userdeptcode")); //
				userVO.setDeptname(defaultCorpVO.getStringValue("userdeptname"));
				userVO.setDeptCorpType(defaultCorpVO.getStringValue("userdept_corptype"));
				userVO.setDeptlinkcode(defaultCorpVO.getStringValue("userdeptlinkcode")); //
			}
		}

		// 处理所有岗位
		if (hvos_post != null) {
			PostVO[] postVOs = new PostVO[hvos_post.length]; // 岗位信息..
			for (int i = 0; i < hvos_post.length; i++) { // 遍历所有岗位与机构.
				postVOs[i] = new PostVO(); //
				postVOs[i].setId(hvos_post[i].getStringValue("postid")); // 岗位ID
				postVOs[i].setCode(hvos_post[i].getStringValue("postcode")); // 岗位编码
				postVOs[i].setName(hvos_post[i].getStringValue("postname")); // 岗位名称

				postVOs[i].setBlDeptId(hvos_post[i].getStringValue("userdeptid")); // 岗位所属机构ID
				postVOs[i].setBlDeptCode(hvos_post[i].getStringValue("userdeptcode")); // 岗位所属机构编码
				postVOs[i].setBlDeptName(hvos_post[i].getStringValue("userdeptname")); // 岗位所属机构名称

				postVOs[i].setBlDept_corptype(hvos_post[i].getStringValue("userdept_corptype")); // 岗位所属机构的岗位类型

				postVOs[i].setBlDept_bl_zhonghbm(hvos_post[i].getStringValue("userdept_bl_zhonghbm")); // 岗位所属机构之所属总行
				postVOs[i].setBlDept_bl_zhonghbm_name(hvos_post[i].getStringValue("userdept_bl_zhonghbm_name")); // 岗位所属机构之所属总行名称

				postVOs[i].setBlDept_bl_fengh(hvos_post[i].getStringValue("userdept_bl_fengh")); // 岗位所属机构之所属分行
				postVOs[i].setBlDept_bl_fengh_name(hvos_post[i].getStringValue("userdept_bl_fengh_name")); // 岗位所属机构之所属分行名称

				postVOs[i].setBlDept_bl_fenghbm(hvos_post[i].getStringValue("userdept_bl_fenghbm")); // 岗位所属机构之所属分行
				postVOs[i].setBlDept_bl_fenghbm_name(hvos_post[i].getStringValue("userdept_bl_fenghbm_name")); // 岗位所属机构之所属分行名称

				postVOs[i].setBlDept_bl_zhih(hvos_post[i].getStringValue("userdept_bl_zhih")); // 岗位所属机构之所属支行
				postVOs[i].setBlDept_bl_zhih_name(hvos_post[i].getStringValue("userdept_bl_zhih_name")); // 岗位所属机构之所属支行名称

				postVOs[i].setBlDept_bl_shiyb(hvos_post[i].getStringValue("userdept_bl_shiyb")); // 岗位所属机构之所属支行
				postVOs[i].setBlDept_bl_shiyb_name(hvos_post[i].getStringValue("userdept_bl_shiyb_name")); // 岗位所属机构之所属支行名称

				postVOs[i].setBlDept_bl_shiybfb(hvos_post[i].getStringValue("userdept_bl_shiybfb")); // 岗位所属机构之所属事业部分部
				postVOs[i].setBlDept_bl_shiybfb_name(hvos_post[i].getStringValue("userdept_bl_shiybfb_name")); // 岗位所属机构之所属事业部分部名称
				postVOs[i].setCorpdistinct(hvos_post[i].getStringValue("corpdistinct"));
				postVOs[i].setCorpclass(hvos_post[i].getStringValue("corpclass"));
				postVOs[i].setDefault(hvos_post[i].getBooleanValue("isdefault", false)); // 是否默认岗位,如果为空则返回false.
			}
			userVO.setPostVOs(postVOs); // 所有的岗位与机构..

			// 遍历所有岗位..
			if (postVOs.length > 0) { // 如果定义了岗位..
				int li_default_pos = 0; // 默认是0
				for (int i = 0; i < postVOs.length; i++) {
					if (postVOs[i].isDefault()) { // 如果发现是默认岗位..
						li_default_pos = i; //
						break;
					}
				}

				// *****如果发现了默认机构，则将此默认的机构设置到pk_dept字段上 gaofeng
				userVO.setPKDept(postVOs[li_default_pos].getBlDeptId()); // 所属部门.
				userVO.setDeptcode(postVOs[li_default_pos].getBlDeptCode()); // 部门名称
				userVO.setDeptname(postVOs[li_default_pos].getBlDeptName()); // 部门名称
				// userVO.setDeptlinkcode(hvos[0].getStringValue("Pk_dept_linkcode"));
				// // 部门关联码
				// *********修改结束
				userVO.setBlPostId(postVOs[li_default_pos].getId()); // 岗位ID
				userVO.setBlPostCode(postVOs[li_default_pos].getCode()); // 岗位编码
				userVO.setBlPostName(postVOs[li_default_pos].getName()); // 岗位名称

				userVO.setBlDeptId(postVOs[li_default_pos].getBlDeptId()); // 所属实际机构Id!!
				userVO.setBlDeptCode(postVOs[li_default_pos].getBlDeptCode()); // 所属实际机构编码
				userVO.setBlDeptName(postVOs[li_default_pos].getBlDeptName()); // 所属实际机构名称
				userVO.setBlDept_corptype(postVOs[li_default_pos].getBlDept_corptype()); // 所属实际机构类型

				userVO.setBlDept_bl_zhonghbm(postVOs[li_default_pos].getBlDept_bl_zhonghbm()); // 所属总行部门
				userVO.setBlDept_bl_zhonghbm_name(postVOs[li_default_pos].getBlDept_bl_zhonghbm_name()); // 所属总行部门

				userVO.setBlDept_bl_fengh(postVOs[li_default_pos].getBlDept_bl_fengh()); // 所属分行
				userVO.setBlDept_bl_fengh_name(postVOs[li_default_pos].getBlDept_bl_fengh_name()); // 所属分行

				userVO.setBlDept_bl_fenghbm(postVOs[li_default_pos].getBlDept_bl_fenghbm()); // 所属分行部门
				userVO.setBlDept_bl_fenghbm_name(postVOs[li_default_pos].getBlDept_bl_fenghbm_name()); // 所属所属分行部门

				userVO.setBlDept_bl_zhih(postVOs[li_default_pos].getBlDept_bl_zhih()); // 所属支行
				userVO.setBlDept_bl_zhih_name(postVOs[li_default_pos].getBlDept_bl_zhih_name()); // 所属支行

				userVO.setBlDept_bl_shiyb(postVOs[li_default_pos].getBlDept_bl_shiyb()); // 所属事业部
				userVO.setBlDept_bl_shiyb_name(postVOs[li_default_pos].getBlDept_bl_shiyb_name()); // 所属事业部

				userVO.setBlDept_bl_shiybfb(postVOs[li_default_pos].getBlDept_bl_shiybfb()); // 所属事业部分部
				userVO.setBlDept_bl_shiybfb_name(postVOs[li_default_pos].getBlDept_bl_shiybfb_name()); // 所属事业部分部
			}
		}

		// ★★★★★找出所有系统角色,一个人有多个角色★★★★★★★★★★
		HashVO[] hvos_role = getDMO().getHashVoArrayByDSByMacro(null, getSQL_UserRole(userVO.getId(), true), new Object[] { new Integer(userVO.getId()) }); //
		Map<String, String> roleMap = new HashMap<String, String>();
		if (hvos_role != null) {
			RoleVO[] roleVOs = new RoleVO[hvos_role.length]; //
			for (int i = 0; i < hvos_role.length; i++) {
				roleVOs[i] = new RoleVO(); //
				roleVOs[i].setId(hvos_role[i].getStringValue("roleid")); // 角色主键
				roleVOs[i].setCode(hvos_role[i].getStringValue("rolecode")); // 角色编码
				roleVOs[i].setName(hvos_role[i].getStringValue("rolename")); // 角色名称
				roleVOs[i].setUserdeptpk(hvos_role[i].getStringValue("userdeptpk")); // 所属机构
				roleVOs[i].setUserdeptcode(hvos_role[i].getStringValue("userdeptcode")); // 所属机构编码
				roleVOs[i].setUserdeptname(hvos_role[i].getStringValue("userdeptname")); // 所属机构名称
			}
			userVO.setRoleMap(roleMap);
			userVO.setRoleVOs(roleVOs); // 所拥有的角色..
		}

		// 设置所有LookAndFeel....
		if (userVO.getLookAndFeelType() != 0) {
			if (ServerCacheDataFactory.static_vos_lookandfeel == null) {
				ServerCacheDataFactory.static_vos_lookandfeel = getDMO().getHashVoArrayByDS(null, "select * from pub_lookandfeel"); //
			}

			// 皮肤表几乎不会改,所以应该做缓存...
			HashVO[] hvslookfeel = ServerCacheDataFactory.static_vos_lookandfeel; //
			String[][] str_lookandFeels = new String[hvslookfeel.length][2]; //
			for (int i = 0; i < str_lookandFeels.length; i++) {
				str_lookandFeels[i][0] = hvslookfeel[i].getStringValue("code"); //
				str_lookandFeels[i][1] = hvslookfeel[i].getStringValue("style" + userVO.getLookAndFeelType()); //
			}
			// getDMO().getStringArrayByDS(null, "select code,style" +
			// userVO.getLookAndFeelType() + " from pub_lookandfeel");
			// //所有风格!要一下子传给客户端!!!为了提高性能,应该将这个表中的值做个缓存!!
			userVO.setAllLookAndFeels(str_lookandFeels); //
		}

		// 王钢原来在这里一个计算出$本机构的逻辑,然后 setCorpId(),setCorpName()
		// 但因为这个设置在登录时并不需要! 而只是在某个功能点使用! 这样在邮储项目中性能测试时就造成登录过程太慢!!!
		// 所以正确的方法是是在真正使用时才去取数(懒装入),而不是集中在登录部分!!!结果让登录成了一个名副其实的性能瓶颈!!!!
		// 现在的做法是先放在了CurrLoginUserVO.getCorpID()方法中,即反正肯定是需要通过这个取数的!所以必须会成功取值!
		// [xch/2012-09-13]

		if (!ServerEnvironment.isLoadRunderCall) { //
			insertSysDealLog(WLTConstants.SYS_LOGIN, userVO.getId(), userVO.getCode() + "/" + userVO.getName(), userVO.getBlDeptId(), userVO.getBlDeptName()); //
		}
		userVO.setFontrevise(hvos_user[0].getIntegerValue("fontrevise", 0));
		return userVO;
	}

	private void insertSysDealLog(String _type, String _userid, String _userName, String _corpId, String _corpName) {
		try {
			InsertSQLBuilder sb = new InsertSQLBuilder("pub_sysdeallog"); //
			sb.putFieldValue("id", getDMO().getSequenceNextValByDS(null, "S_PUB_SYSDEALLOG")); // 主键值
			sb.putFieldValue("dealtype", _type); // 主键值
			sb.putFieldValue("dealuserid", _userid); // 人员id
			sb.putFieldValue("dealusername", _userName); // 人员名称
			sb.putFieldValue("dealcorpid", _corpId); // 机构id
			sb.putFieldValue("dealcorpname", _corpName); // 机构名称
			sb.putFieldValue("dealtime", new TBUtil().getCurrTime()); // 处理时间
			getDMO().executeUpdateByDSImmediately(null, sb); //
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	/**
	 * 得到某个人员的所有角色
	 * 
	 * @return
	 */
	private String getSQL_UserRole(String _userid, boolean _isMacro) {
		StringBuilder sb_sql = new StringBuilder();
		sb_sql.append("select ");
		sb_sql.append("t1.id,");
		sb_sql.append("t1.userid,");
		sb_sql.append("t1.roleid,");
		sb_sql.append("t2.code rolecode,"); //
		sb_sql.append("t2.name rolename,"); //
		sb_sql.append("t1.userdept userdeptpk,");
		sb_sql.append("t3.code userdeptcode,");
		sb_sql.append("t3.corpdistinct corpdistinct,");
		sb_sql.append("t3.corpclass corpclass,");
		sb_sql.append("t3.name userdeptname ");
		sb_sql.append("from pub_user_role t1 "); //
		sb_sql.append("left join pub_role t2 on t1.roleid=t2.id ");
		sb_sql.append("left join pub_corp_dept t3 on t1.userdept=t3.id "); //
		if (_isMacro) {
			sb_sql.append("where t1.userid=? "); //
		} else {
			sb_sql.append("where t1.userid='" + _userid + "' "); //
		}
		return sb_sql.toString(); //
	}

	/**
	 * 得到某个人员的所有岗位
	 * 
	 * @return
	 */
	private String getSQL_UserPost(String _userid, boolean _isMacro) {
		StringBuilder sb_sql = new StringBuilder();
		sb_sql.append("select ");
		sb_sql.append("t1.id,");
		sb_sql.append("t1.userid,");
		sb_sql.append("t1.postid,");
		sb_sql.append("t1.isdefault,"); // 是否默认岗位
		sb_sql.append("t2.code postcode,"); // 岗位编码
		sb_sql.append("t2.name postname,"); // 岗位名称
		sb_sql.append("t1.userdept userdeptid,"); // 用户所属机构ID
		sb_sql.append("t3.code     userdeptcode,"); // 用户所属机构编码
		sb_sql.append("t3.name     userdeptname,"); // 用户所属机构名称
		sb_sql.append("t3.linkcode     userdeptlinkcode,"); // 用户所属机构名称
		sb_sql.append("t3.corptype         userdept_corptype, "); // 所属机构类型
		sb_sql.append("t3.bl_zhonghbm      userdept_bl_zhonghbm,"); // 所属机构之所属总行部门
		sb_sql.append("t3.bl_zhonghbm_name userdept_bl_zhonghbm_name,"); // 所属机构之所属总行部门名称
		sb_sql.append("t3.bl_fengh         userdept_bl_fengh,"); // 所属机构之所属分行
		sb_sql.append("t3.bl_fengh_name    userdept_bl_fengh_name,"); // 所属机构之所属分行名称
		sb_sql.append("t3.bl_fenghbm       userdept_bl_fenghbm,"); // 所属机构之所属分行部门
		sb_sql.append("t3.bl_fenghbm_name  userdept_bl_fenghbm_name,"); // 所属机构之所属分行部门名称
		sb_sql.append("t3.bl_zhih          userdept_bl_zhih,"); // 所属机构之所属支行
		sb_sql.append("t3.bl_zhih_name     userdept_bl_zhih_name,"); // 所属机构之所属支行名称
		sb_sql.append("t3.bl_shiyb         userdept_bl_shiyb,"); // 所属机构之所属事业部
		sb_sql.append("t3.bl_shiyb_name    userdept_bl_shiyb_name,"); // 所属机构之所属事业部名称
		sb_sql.append("t3.bl_shiybfb       userdept_bl_shiybfb,"); // 所属机构之所属事业部分部
		sb_sql.append("t3.corpdistinct       corpdistinct,"); // zzl 得到机构涉农类型
		sb_sql.append("t3.corpclass       corpclass,"); // zzl 得到机构的涉农类别
		sb_sql.append("t3.bl_shiybfb_name  userdept_bl_shiybfb_name "); // 所属机构之所属事业部分部名称
		sb_sql.append("from pub_user_post t1 "); // 关系表..
		sb_sql.append("left join pub_post t2 on t1.postid=t2.id "); //
		sb_sql.append("left join pub_corp_dept t3 on t1.userdept=t3.id "); //
		if (_isMacro) {
			sb_sql.append("where t1.userid=? "); //
		} else {
			sb_sql.append("where t1.userid='" + _userid + "' "); //
		}
		return sb_sql.toString(); //
	}

	/**
	 * 加密用户密码,即用加密算法进行处理...
	 */
	public String modifyPasswd(String _pwd) {
		return new DESKeyTool().encrypt(_pwd); // 使用DES加密算法加密!!!上海农商行科技明确提出需要DES加密!!DES是可逆算法!!
	}

	/**
	 * 用户退出 删除所有根据sessionID注册的内存对象!!!
	 */
	public void loginOut(String _userid, String _httpsessionId) throws Exception {
		if (_httpsessionId != null) {
			CurrSessionVO sessionVO = new WLTInitContext().getCurrSession(); //
			String str_userid = sessionVO.getLoginUserId(); //
			HashVO[] hvsDept = new HashVO[0];
			if (str_userid == null || str_userid.equals("")) {

			} else {
				hvsDept = getDMO().getHashVoArrayByDS(null, "select deptid,deptname from v_pub_user_post_1 where userid=" + str_userid); // 机构再取一把!!
			}
			String str_corpDeptId = null;
			String str_corpDeptName = null;
			if (hvsDept.length > 0) {
				str_corpDeptId = hvsDept[0].getStringValue("deptid");
				str_corpDeptName = hvsDept[0].getStringValue("deptname");
			}
			String usercode = sessionVO.getLoginUserCode();
			insertSysDealLog(WLTConstants.SYS_LOGINOUT, sessionVO.getLoginUserId(), usercode + "/" + sessionVO.getLoginUserName(), str_corpDeptId, str_corpDeptName); // !!!!

			ServerEnvironment.getSessionSqlListenerMap().remove(_httpsessionId); // SQL监听器中删除
			ServerEnvironment.getLoginUserMap().remove(_httpsessionId); // 从服务器端缓存中删除该Session...
			WLTLogger.getLogger(this).debug("一个用户退出系统,SeeesionId=[" + _httpsessionId + "],UserCode=[" + (usercode == null ? "" : usercode) + "]"); //
			System.gc(); //
		}
	}
	//袁江晓 20180725修改 主要解决在太平项目中日志监控的报错，客户要求不能抛error，添加返回值，如果返回为1则表示修改成功，否则把返回值弹出提示
	public String resetPwd(String _loginuser, String _oldpwd, String _newpwd) throws Exception {
		String ret_value="1";
		CommDMO dmo = new CommDMO(); //
		HashVO[] hvs = dmo.getHashVoArrayByDS(null, "select pwd from pub_user where code='" + _loginuser + "'"); //
		String str_oldpwd_encrypt = modifyPasswd(_oldpwd); //
		if (!str_oldpwd_encrypt.equals(hvs[0].getStringValue("pwd")) && !_oldpwd.equals(hvs[0].getStringValue("pwd"))) { // 如果不相等!!!
			//throw new WLTAppException("您的原密码不对，请重新输入!"); //
			ret_value="您的原密码不对，请重新输入!";
			return ret_value;
		}

		String str_newpwd_encrypt = modifyPasswd(_newpwd); // 加密!!!
		// 如果修改了密码，需要记录修改密码的日期，以便实现定期提醒用户更新密码【李春娟/2016-07-07】
		TableDataStruct struct = dmo.getTableDataStructByDS(null, "select * from pub_user where code='" + _loginuser + "'");
		boolean iffind = false;
		if (struct != null) {
			String[] headnames = struct.getHeaderName();
			for (int i = 0; i < headnames.length; i++) {
				if ("pwd_update_date".equalsIgnoreCase(headnames[i])) {
					iffind = true;
					break;
				}
			}
		}

		if (iffind) {// 如果人员表中有pwd_update_date字段，则更新修改日期
			dmo.executeUpdateByDS(null, "update pub_user set pwd='" + str_newpwd_encrypt + "', pwd_update_date='" + TBUtil.getTBUtil().getCurrDate() + "' where code='" + _loginuser + "'"); //
		} else {
			dmo.executeUpdateByDS(null, "update pub_user set pwd='" + str_newpwd_encrypt + "' where code='" + _loginuser + "'"); //
		}
		CurrSessionVO sessionVO = new WLTInitContext().getCurrSession(); //
		String str_userid = sessionVO.getLoginUserId(); //
		HashVO[] hvsDept = getDMO().getHashVoArrayByDS(null, "select deptid,deptname from v_pub_user_post_1 where userid='" + str_userid + "'"); // 机构再取一把!!
		String str_corpDeptId = null;
		String str_corpDeptName = null;
		if (hvsDept.length > 0) {
			str_corpDeptId = hvsDept[0].getStringValue("deptid");
			str_corpDeptName = hvsDept[0].getStringValue("deptname");
		}
		insertSysDealLog("修改密码", sessionVO.getLoginUserId(), sessionVO.getLoginUserCode() + "/" + sessionVO.getLoginUserName(), str_corpDeptId, str_corpDeptName); // !!!!
		return ret_value;
	}

	/**
	 * 增加点击菜单日志...
	 */
	public void addClickedMenuLog(String _usercode, String _username, String _deptID, String _deptname, String _menuname, String _menupath, String _wasteTime) throws Exception {
		String str_currtime = new TBUtil().getCurrTime(); //
		CommDMO dmo = new CommDMO();
		String str_userName = _usercode + "/" + _username; //
		String showuser = new TBUtil().getSysOptionStringValue("是否显示登陆号", "0");// 是否显示登陆号,默认0为登录号+姓名，1为只显示人员姓名
		if ("1".equals(showuser)) { // 航工科工的用户code是身份证,太长
			str_userName = _username; //
		}
		WLTInitContext initConText = new WLTInitContext(); //
		InsertSQLBuilder isql = new InsertSQLBuilder("pub_menu_clicklog"); //
		isql.putFieldValue("id", dmo.getSequenceNextValByDS(null, "s_pub_menu_clicklog")); //
		isql.putFieldValue("username", str_userName); // 用户名称
		isql.putFieldValue("deptid", _deptID); // 机构id
		isql.putFieldValue("deptname", _deptname); // 部门名称
		isql.putFieldValue("clicktime", str_currtime); // 点击时间
		isql.putFieldValue("menuname", _menuname); // 菜单名称
		isql.putFieldValue("menupath", _menupath); // 菜单路径
		isql.putFieldValue("wastetime", _wasteTime + "[" + cn.com.infostrategy.bs.common.RemoteCallServlet.THREADCOUNT + "," + cn.com.infostrategy.bs.common.RemoteCallServlet.MAXTHREADCOUNT + "]"); // 耗时
		isql.putFieldValue("clientip", initConText.getCurrSession().getClientIP1() + "/" + initConText.getCurrSession().getClientIP2()); // IP地址
		if (SystemOptions.getBooleanValue("是否启用预编译", false)) {
			dmo.executeBatchByDS(null, Arrays.asList(new String[] { isql.toString() }), true, false, true);
		} else {
			dmo.executeUpdateByDS(null, isql.toString()); //
		}
	}

	/**
	 * 点击按钮监控事件..
	 * 
	 * @param _usercode
	 * @param _username
	 * @param _deptname
	 * @param _billname
	 * @param _btncode
	 * @param _btnname
	 * @param _menupath
	 * @param _clientIP
	 * @throws Exception
	 */
	public void addClickButtonLog(String _usercode, String _username, String _deptname, String _billname, String _btncode, String _btnname, String _menupath) throws Exception {
		TBUtil tbUtil = new TBUtil();
		String str_currtime = tbUtil.getCurrTime(); //
		CommDMO dmo = new CommDMO();
		String str_newid = dmo.getSequenceNextValByDS(null, "s_pub_regbuttons_clicklog"); //
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("insert into pub_regbuttons_clicklog");
		sb_sql.append("(");
		sb_sql.append("id,"); // id (id)
		sb_sql.append("usercode,"); // usercode (usercode)
		sb_sql.append("username,"); // username (username)
		sb_sql.append("deptname,"); // deptname (deptname)
		sb_sql.append("clicktime,"); // clicktime (clicktime)
		sb_sql.append("billname,"); // billname (billname)
		sb_sql.append("btncode,"); // btncode (btncode)
		sb_sql.append("btnname,"); // btnname (btnname)
		sb_sql.append("menupath,"); // menupath (menupath)
		sb_sql.append("clientip"); // 
		sb_sql.append(")");
		sb_sql.append(" values ");
		sb_sql.append("(");
		sb_sql.append("'" + str_newid + "',"); // id (id)
		sb_sql.append("'" + _usercode + "',"); // usercode (usercode)
		sb_sql.append("'" + _username + "',"); // username (username)
		sb_sql.append(tbUtil.convertSQLValue(_deptname) + ","); // deptname
		sb_sql.append("'" + str_currtime + "',"); // clicktime (clicktime)
		sb_sql.append(tbUtil.convertSQLValue(_billname) + ","); // billname
		sb_sql.append(tbUtil.convertSQLValue(_btncode) + ","); // btncode
		sb_sql.append(tbUtil.convertSQLValue(_btnname) + ","); // btnname
		sb_sql.append(tbUtil.convertSQLValue(_menupath)); // menupath
		sb_sql.append(")");
		dmo.executeUpdateByDS(null, sb_sql.toString()); //
	}

	/**
	 * 为一批用户分配一批角色..
	 * 
	 * @return
	 * @throws Exception
	 */
	public String assignUserRole(String[] _userIds, String[] _roleIds) throws Exception { //
		CommDMO commDMO = new CommDMO(); //
		TBUtil tbUtil = new TBUtil(); //
		HashVO[] hvs_user = commDMO.getHashVoArrayByDS(null, "select id,pk_dept from pub_user where id in (" + tbUtil.getInCondition(_userIds) + ")"); // 先找出所有用户即其主属机构.
		HashMap map_user = new HashMap(); //
		for (int i = 0; i < hvs_user.length; i++) {
			map_user.put(hvs_user[i].getStringValue("id"), hvs_user[i].getStringValue("pk_dept")); //
		}

		HashVO[] hvs_user_role = commDMO.getHashVoArrayByDS(null, "select * from pub_user_role where userid in (" + tbUtil.getInCondition(_userIds) + ")"); // 先找出所有用户即其主属机构.
		ArrayList al_sqls = new ArrayList(); //
		int li_success = 0; //
		int li_fail = 0; //
		for (int i = 0; i < _userIds.length; i++) { // 遍历所有用户
			for (int j = 0; j < _roleIds.length; j++) { // 遍历所有角色
				String str_pkdept = (String) map_user.get(_userIds[i]); //
				if (str_pkdept != null) { // 机构不为空
					boolean bo_find = false; //
					for (int k = 0; k < hvs_user_role.length; k++) {
						if (_userIds[i].equals(hvs_user_role[k].getStringValue("userid")) && _roleIds[j].equals(hvs_user_role[k].getStringValue("roleid")) && str_pkdept.equals(hvs_user_role[k].getStringValue("userdept"))) {
							bo_find = true; // 该用户在该部门已分配该角色
							break;
						}
					}

					if (!bo_find) { // 如果发现没注册,则注册之
						StringBuffer sb_sql = new StringBuffer(); //
						String str_newid = commDMO.getSequenceNextValByDS(null, "s_pub_user_role"); //
						sb_sql.append("insert into pub_user_role");
						sb_sql.append("(");
						sb_sql.append("id,"); // id (id)
						sb_sql.append("userid,"); // userid (userid)
						sb_sql.append("roleid,"); // roleid (roleid)
						sb_sql.append("userdept"); // userdept (userdept)
						sb_sql.append(")");
						sb_sql.append(" values ");
						sb_sql.append("(");
						sb_sql.append("'" + str_newid + "',"); // id (id)
						sb_sql.append("'" + _userIds[i] + "',"); // userid
						// (userid)
						sb_sql.append("'" + _roleIds[j] + "',"); // roleid
						// (roleid)
						sb_sql.append("'" + str_pkdept + "'"); // userdept
						// (userdept)
						sb_sql.append(")");
						al_sqls.add(sb_sql.toString()); //
						li_success++;
					} else {
						li_fail++;
					}
				} else {
					li_fail++;
				}
			}
		}

		commDMO.executeBatchByDS(null, al_sqls); //
		return "成功绑定[" + li_success + "]个角色,有[" + li_fail + "]条因为用户机构为空或已绑定对应角色而忽略重复绑定!"; //
	}

	/**
	 * 为一批用户分配一批角色..
	 * 
	 * @return
	 * @throws Exception
	 */
	public String assignUserRole(String[] _userIds, String[] _roleIds, String _deptid) throws Exception { //
		CommDMO commDMO = new CommDMO(); //
		TBUtil tbUtil = new TBUtil(); //

		HashVO[] hvs_user_role = commDMO.getHashVoArrayByDS(null, "select * from pub_user_role where userid in (" + tbUtil.getInCondition(_userIds) + ")"); // 先找出所有用户即其主属机构.
		ArrayList al_sqls = new ArrayList(); //
		int li_success = 0; //
		int li_fail = 0; //
		for (int i = 0; i < _userIds.length; i++) { // 遍历所有用户
			for (int j = 0; j < _roleIds.length; j++) { // 遍历所有角色
				boolean bo_find = false; //
				for (int k = 0; k < hvs_user_role.length; k++) {
					if (_userIds[i].equals(hvs_user_role[k].getStringValue("userid")) && _roleIds[j].equals(hvs_user_role[k].getStringValue("roleid")) && _deptid.equals(hvs_user_role[k].getStringValue("userdept"))) {
						bo_find = true; // 该用户在该部门已分配该角色
						break;
					}
				}

				if (!bo_find) { // 如果发现没注册,则注册之
					StringBuffer sb_sql = new StringBuffer(); //
					String str_newid = commDMO.getSequenceNextValByDS(null, "s_pub_user_role"); //
					sb_sql.append("insert into pub_user_role");
					sb_sql.append("(");
					sb_sql.append("id,"); // id (id)
					sb_sql.append("userid,"); // userid (userid)
					sb_sql.append("roleid,"); // roleid (roleid)
					sb_sql.append("userdept"); // userdept (userdept)
					sb_sql.append(")");
					sb_sql.append(" values ");
					sb_sql.append("(");
					sb_sql.append("'" + str_newid + "',"); // id (id)
					sb_sql.append("'" + _userIds[i] + "',"); // userid
					// (userid)
					sb_sql.append("'" + _roleIds[j] + "',"); // roleid
					// (roleid)
					sb_sql.append("'" + _deptid + "'"); // userdept (userdept)
					sb_sql.append(")");
					al_sqls.add(sb_sql.toString()); //
					li_success++;
				} else {
					li_fail++;

				}
			}
		}

		commDMO.executeBatchByDS(null, al_sqls); //
		return "成功绑定[" + li_success + "]个角色,有[" + li_fail + "]条因已绑定对应角色而忽略重复绑定!"; //
	}

	public DeskTopVO getDeskTopVO(String _userId, String _userCode, boolean _isFnFilter, String[] _myroleIds) throws Exception {
		// 因为存在补全菜单目录的情况,所以总是需要查询出所有树!
		// if (ServerCacheDataFactory.static_vos_allMenu == null) {
		// //先弄个静态变量,以后再找
		// ServerCacheDataFactory.static_vos_allMenu =
		// getDMO().getHashVoArrayAsTreeStructByDS(null, "select * from
		// pub_menu", "id", "parentmenuid", null, null); //
		// }
		// HashVO[] vos_allMenu = ServerCacheDataFactory.static_vos_allMenu; //
		HashVO[] vos_allMenu = getDMO().getHashVoArrayAsTreeStructByDS(null, "select * from pub_menu", "id", "parentmenuid", null, null); //
		HashVO[] vos_1 = getMenuHVs(_userId, _userCode, _isFnFilter, vos_allMenu, _myroleIds); // //
		// 根据权限取得所有菜单

		HashMap map_old = new HashMap();
		for (int i = 0; i < vos_1.length; i++) {
			map_old.put(vos_1[i].getStringValue("id"), vos_1[i]);
		}

		HashMap map_all = new HashMap();
		for (int i = 0; i < vos_allMenu.length; i++) {
			map_all.put(vos_allMenu[i].getStringValue("id"), vos_allMenu[i]);
		}

		TBUtil tbUtil = new TBUtil(); //
		HashVO hvoTemp = null; // //
		String parentpathids = null;
		for (int i = 0; i < vos_1.length; i++) { // 遍历原来的
			hvoTemp = (HashVO) map_all.get(vos_1[i].getStringValue("id")); // 找到记录
			parentpathids = hvoTemp.getStringValue("$parentpathids"); // 找到其父亲记录
			if (parentpathids != null && !parentpathids.equals("")) {
				String[] pids = tbUtil.split(parentpathids, ";"); //
				for (int j = 0; j < pids.length; j++) {
					if (!map_old.containsKey(pids[j])) { // 如果原来的不存在
						HashVO lostHVO = (HashVO) map_all.get(pids[j]); //
						lostHVO.setAttributeValue("$reason", "F:虽然该菜单没有权限,但因为有子孙[" + vos_1[i].getStringValue("id") + "/" + vos_1[i].getStringValue("name") + "]有权限,为了保证树的结构自动带入!!"); //
						map_old.put(pids[j], lostHVO); // 加入
					}
				}
			}
		}

		HashVO[] hvs_newAll = (HashVO[]) map_old.values().toArray(new HashVO[0]); //
		Arrays.sort(hvs_newAll, new HashVOComparator(new String[][] { { "seq", "N", "Y" } })); // 重新排序!!
		// System.out.println("找丢失的父亲找回来所耗时间[" + (ll_2 - ll_1) + "]"); //

		DeskTopVO deskTopVO = new DeskTopVO(); //
		deskTopVO.setMenuVOs(hvs_newAll); // 所有菜单
		return deskTopVO; //
	}

	/**
	 * 取得首页分页内容中的实际数据,在懒装入时需要!
	 * @param _className
	 * @return
	 * @throws Exception
	 */
	public HashVO[] getDeskTopNewGroupVOData(String _className, String _loginuserCode, DeskTopNewGroupDefineVO defineVO) throws Exception {
		try {
			Object builder = Class.forName(defineVO.getDatabuildername()).newInstance();
			HashVO[] hvs_data = null;
			if (builder instanceof DeskTopNewsDataBuilderIFC) {
				hvs_data = ((DeskTopNewsDataBuilderIFC) builder).getNewData(_loginuserCode); // 取得数据!!!可能是非常耗时的操作!!
			} else if (builder instanceof DeskTopNewsDataBuilderIFC2) {
				hvs_data = ((DeskTopNewsDataBuilderIFC2) builder).getNewData(_loginuserCode, defineVO.getOtherconfig()); // 取得数据!!!可能是非常耗时的操作!!
			}
			return hvs_data; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null; //
		}
	}

	/**
	 * 取得当前登录人可以看到的板块
	 * @return
	 * @throws Exception
	 */

	public HashMap getDeskTopVO_Default(HashMap param) throws Exception {
		String userid = (String) param.get("userid");
		HashMap p = new HashMap();
		p.put("vos", getDeskTopVO_Default(userid));
		return p;
	}

	public HashVO[] getDeskTopVO_Default(String userid) throws Exception {
		HashVO[] hvs_news = null; //
		if (SystemOptions.getBooleanValue("首页板块是否启用权限过滤", false)) { // 只用角色来进行配置，如果启用了权限过滤则每个人能看到没有配置权限的和权限配置成我的板块/sunfujun/20130521/hm需求
			String[] roleids = getDMO().getStringArrayFirstColByDS(null, "select roleid from pub_user_role where userid='" + userid + "'");
			String[] desktop_role = getDMO().getStringArrayFirstColByDS(null, "select desktopid from pub_desktop_role where roleid in (" + TBUtil.getTBUtil().getInCondition(roleids) + ")");
			String str_sql = "select * from pub_desktop_new where (uneffect is null or uneffect<>'Y') and (id not in (select desktopid from pub_desktop_role) or id in (" + TBUtil.getTBUtil().getInCondition(desktop_role) + ")) order by showorder asc"; // 找出所有的分组框
			hvs_news = getDMO().getHashVoArrayByDS(null, str_sql); //
		} else {
			String str_sql = "select * from pub_desktop_new where uneffect is null or uneffect<>'Y' order by showorder asc"; // 找出所有的分组框
			hvs_news = getDMO().getHashVoArrayByDS(null, str_sql); //
		}
		return hvs_news;
	}

	/**
	 * 获取用户定制首页板块
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public HashMap getDeskTopVO_Person(HashMap param) throws Exception {
		String userid = (String) param.get("userid");
		HashMap p = new HashMap();
		p.put("vos", getDeskTopVO_Person(userid));
		return p;
	}

	public HashVO[] getDeskTopVO_Person(String userid) throws Exception {
		HashVO[] hvs_news = null; //
		if (!SystemOptions.getBooleanValue("首页板块是否启用定制功能", false)) {
			return null;
		}
		String[] desktopids = getDMO().getStringArrayFirstColByDS(null, "select desktopid from pub_desktop_user where userid='" + userid + "'");
		if (desktopids == null || desktopids.length == 0) { // 表示没有进行过定制操作
			return hvs_news;
		} else { // 如果进行过定制操作
			String str_sql = " select d.*,t.* from (select desktopid,seq,viewcols from pub_desktop_user where userid='" + userid + "') t left join (select * from pub_desktop_new  where uneffect is null or uneffect<>'Y') d on t.desktopid=d.id where t.desktopid is not null order by t.seq asc"; // 找出所有的分组框
			hvs_news = getDMO().getHashVoArrayByDS(null, str_sql); //
			if (hvs_news == null) {
				hvs_news = new HashVO[0];
			}
			return hvs_news;
		}
	}

	/**
	 * 取得桌面所有新闻信息,即首页上信息栏的一个个框组..
	 * 
	 * @return
	 * @throws Exception
	 */
	public DeskTopNewGroupVO[] getDeskTopNewGroupVOs(String _loginuserCode) throws Exception {
		HashVO[] hvs_news = null;
		String lgusrid = getDMO().getCurrSession().getLoginUserId();
		hvs_news = getDeskTopVO_Person(lgusrid);
		if (hvs_news == null) { // 只有返回null才是没有进行过定制操作
			hvs_news = getDeskTopVO_Default(lgusrid); //
		}
		if (hvs_news == null || hvs_news.length == 0) {
			return null;
		}
		DeskTopNewGroupVO[] newVOs = new DeskTopNewGroupVO[hvs_news.length]; //
		for (int i = 0; i < newVOs.length; i++) {
			newVOs[i] = new DeskTopNewGroupVO(); //
			DeskTopNewGroupDefineVO defineVO = new DeskTopNewGroupDefineVO(); // 生成定义对象
			defineVO.setTitle(hvs_news[i].getStringValue("title")); //
			defineVO.setDatatype(hvs_news[i].getStringValue("datatype")); //
			defineVO.setViewcols(hvs_news[i].getStringValue("viewcols")); //
			defineVO.setImgicon(hvs_news[i].getStringValue("imgicon")); //
			defineVO.setLogoimg(hvs_news[i].getStringValue("logoimg")); //
			defineVO.setCapimg(hvs_news[i].getStringValue("capimg")); //
			defineVO.setTitlecolor(hvs_news[i].getStringValue("titlecolor")); //

			defineVO.setDatabuildername(hvs_news[i].getStringValue("databuildername")); //
			defineVO.setTempletcode(hvs_news[i].getStringValue("templetcode")); //
			defineVO.setActionname(hvs_news[i].getStringValue("actionname")); //
			defineVO.setLinkmenu(hvs_news[i].getStringValue("linkmenu")); //
			defineVO.setNewcount(hvs_news[i].getIntegerValue("newcount", 2)); //
			defineVO.setIsLazyLoad(hvs_news[i].getStringValue("islazyload")); // 是否懒装入
			defineVO.setDescr(hvs_news[i].getStringValue("descr")); // zzl 2019-7-8
			defineVO.setOtherconfig(new TBUtil().convertStrToMapByExpress(hvs_news[i].getStringValue("otherconfig"))); // 其他参数
			// yk

			newVOs[i].setDefineVO(defineVO); // 设置定义对象

			// 如果数据生成对象不为空,则生成数据
			if (defineVO.getDatabuildername() != null && !defineVO.getDatabuildername().trim().equals("")) {
				try {
					boolean isLazyLoad = "Y".equals(defineVO.getIsLazyLoad()) ? true : false; // 是否懒装入??
					if (isLazyLoad) { // 如果是懒装入,则不处理数据,则前台一个去取数据!!
						newVOs[i].setDataVOs(null); //
					} else {
						Object builder = Class.forName(defineVO.getDatabuildername()).newInstance();
						HashVO[] hvs_data = null;
						if (builder instanceof DeskTopNewsDataBuilderIFC) {
							hvs_data = ((DeskTopNewsDataBuilderIFC) builder).getNewData(_loginuserCode); // 取得数据!!!可能是非常耗时的操作!!
						} else if (builder instanceof DeskTopNewsDataBuilderIFC2) {
							hvs_data = ((DeskTopNewsDataBuilderIFC2) builder).getNewData(_loginuserCode, defineVO.getOtherconfig()); // 取得数据!!!可能是非常耗时的操作!!
						}
						// 追加数据有效期限判断【杨科/2012-07-30】
						if ((defineVO.getDatatype() == null || defineVO.getDatatype().equals("文字")) && (hvs_data != null)) {
							ArrayList al_hvs = new ArrayList();
							String expfield = (String) defineVO.getOtherconfig().get("有效期限匹配字段");
							int expdate = 0;

							try {
								expdate = Integer.parseInt((String) defineVO.getOtherconfig().get("有效期限"));
							} catch (RuntimeException e) {
								expdate = 0;
							}

							if (expfield != null && !expfield.trim().equals("") && expdate > 0) {
								CommonDate cd = new CommonDate(new Date());
								for (int j = 0; j < hvs_data.length; j++) {
									if (hvs_data[j].getDateValue(expfield) != null && cd.getDaysAfter(new CommonDate(hvs_data[j].getDateValue(expfield))) < expdate) {
										al_hvs.add(hvs_data[j]);
										if (al_hvs.size() >= 7) {
											break;
										}
									}
								}
							} else {
								for (int j = 0; j < hvs_data.length; j++) {
									al_hvs.add(hvs_data[j]);
									if (al_hvs.size() >= 7) {
										break;
									}
								}
							}

							hvs_data = (HashVO[]) al_hvs.toArray(new HashVO[0]);
						} // yangke

						newVOs[i].setDataVOs(hvs_data);
					}
				} catch (Exception ex) {
					WLTLogger.getLogger(this).error("生成登录首页数据失败!", ex); //
				}
			}
		}
		return newVOs; //
	}

	/**
	 * 取得某一个指定的数据对象
	 * 
	 * @param _loginuserCode
	 * @param _title
	 * @return
	 * @throws Exception
	 */
	public DeskTopNewGroupVO getDeskTopNewGroupVOs(String _loginuserCode, String _title, boolean _isall) throws Exception {
		String str_sql = "select * from pub_desktop_new where title='" + _title + "'";

		HashVO[] hvs_news = getDMO().getHashVoArrayByDS(null, str_sql); //
		if (hvs_news.length == 0) {
			return null;
		}
		DeskTopNewGroupVO newVO = new DeskTopNewGroupVO(); //
		DeskTopNewGroupDefineVO defineVO = new DeskTopNewGroupDefineVO(); // 生成定义对象
		defineVO.setTitle(hvs_news[0].getStringValue("title")); //
		defineVO.setDatatype(hvs_news[0].getStringValue("datatype")); //
		defineVO.setViewcols(hvs_news[0].getStringValue("viewcols")); //
		defineVO.setImgicon(hvs_news[0].getStringValue("imgicon")); //
		defineVO.setLogoimg(hvs_news[0].getStringValue("logoimg")); //
		defineVO.setCapimg(hvs_news[0].getStringValue("capimg")); //
		defineVO.setTitlecolor(hvs_news[0].getStringValue("titlecolor")); //
		defineVO.setDatabuildername(hvs_news[0].getStringValue("databuildername")); //
		defineVO.setTempletcode(hvs_news[0].getStringValue("templetcode")); //
		defineVO.setActionname(hvs_news[0].getStringValue("actionname")); //
		defineVO.setLinkmenu(hvs_news[0].getStringValue("linkmenu")); //
		defineVO.setNewcount(hvs_news[0].getIntegerValue("newcount", 2)); //
		defineVO.setIsLazyLoad(hvs_news[0].getStringValue("islazyload")); // 是否懒装入

		defineVO.setOtherconfig(new TBUtil().convertStrToMapByExpress(hvs_news[0].getStringValue("otherconfig"))); // 其他参数
		// yk

		newVO.setDefineVO(defineVO); // 设置定义对象

		// 如果数据生成对象不为空,则生成数据
		if (defineVO.getDatabuildername() != null && !defineVO.getDatabuildername().trim().equals("")) {
			try {
				Object builder = Class.forName(defineVO.getDatabuildername()).newInstance();
				HashVO[] hvs_data = null;
				if (builder instanceof DeskTopNewsDataBuilderIFC) {
					hvs_data = ((DeskTopNewsDataBuilderIFC) builder).getNewData(_loginuserCode); // 取得数据!!!可能是非常耗时的操作!!
				} else if (builder instanceof DeskTopNewsDataBuilderIFC2) {
					hvs_data = ((DeskTopNewsDataBuilderIFC2) builder).getNewData(_loginuserCode, defineVO.getOtherconfig()); // 取得数据!!!可能是非常耗时的操作!!
				}

				// 追加数据有效期限判断【杨科/2012-07-30】
				if ((defineVO.getDatatype() == null || defineVO.getDatatype().equals("文字")) && (hvs_data != null)) {
					ArrayList al_hvs = new ArrayList();
					String expfield = (String) defineVO.getOtherconfig().get("有效期限匹配字段");
					int expdate = 0;

					try {
						expdate = Integer.parseInt((String) defineVO.getOtherconfig().get("有效期限"));
					} catch (RuntimeException e) {
						expdate = 0;
					}

					if (expfield != null && !expfield.trim().equals("") && expdate > 0) {
						CommonDate cd = new CommonDate(new Date());
						for (int j = 0; j < hvs_data.length; j++) {
							if (hvs_data[j].getDateValue(expfield) != null && cd.getDaysAfter(new CommonDate(hvs_data[j].getDateValue(expfield))) < expdate) {
								al_hvs.add(hvs_data[j]);
							}
						}
						hvs_data = (HashVO[]) al_hvs.toArray(new HashVO[0]);
					}
				} // yangke

				if (_isall) {
					newVO.setDataVOs(hvs_data); // 否则全部送入..
				} else {
					if ((defineVO.getDatatype() == null || defineVO.getDatatype().equals("文字")) && hvs_data.length > 7) { // 如果数据超过5条,则只显示前5条..
						HashVO[] hvs_data_copy = new HashVO[7]; //
						for (int j = 0; j < 7; j++) {
							hvs_data_copy[j] = hvs_data[j]; //
						}
						newVO.setDataVOs(hvs_data_copy); //
					} else {
						newVO.setDataVOs(hvs_data); // 否则全部送入..
					}
				}
			} catch (Exception ex) {
				WLTLogger.getLogger(this).error("生成登录首页数据失败!", ex); //
			}
		}

		return newVO; //
	}

	/**
	 * 取得系统公告之滚动图片!!!
	 * @return
	 */
	public HashVO[] getSysBoardRollImage() throws Exception {
		CommDMO commDMO = new CommDMO();
		HashVO[] hvs_img = commDMO.getHashVoArrayByDS(null, "select id,title,image from pub_sysboard where iseffect='Y' and msgtype='滚动图片' order by seq,createtime desc", 4); // 只前4个
		if (hvs_img != null && hvs_img.length > 0) {
			ArrayList al_ids = new ArrayList(); //
			for (int i = 0; i < hvs_img.length; i++) { //
				String str_imageid = hvs_img[i].getStringValue("image"); //
				if (str_imageid != null) {
					al_ids.add(str_imageid); //
				}
			}
			if (al_ids.size() > 0) {
				HashMap img64CodeMap = new SysAppDMO().getImageUpload64Code((String[]) al_ids.toArray(new String[0])); // 一下子取得所有取得图片的64位编码!
				for (int i = 0; i < hvs_img.length; i++) { //
					String str_imageid = hvs_img[i].getStringValue("image"); //
					if (str_imageid != null && img64CodeMap.containsKey(str_imageid)) {
						String str_64code = (String) img64CodeMap.get(str_imageid); //
						hvs_img[i].setAttributeValue("image_64code", str_64code); // 重新塞入其中!!!
					}
				}
			}
		}
		return hvs_img; //
	}

	/**
	 * 取得系统公告之滚动文字!
	 * @param _isTrim
	 * @return
	 * @throws Exception
	 */
	public HashVO[] getSysBoardRollMsg(boolean _isTrim) throws Exception {
		CommDMO commDMO = new CommDMO();
		HashVO[] hvs = commDMO.getHashVoArrayByDS(null, "select id,title,createtime from pub_sysboard where iseffect='Y' and msgtype='滚动文字' order by seq,createtime desc", _isTrim ? 8 : 50); // 只前50个
		for (int i = 0; i < hvs.length; i++) {
			String str_tostring = hvs[i].getStringValue("title"); //
			hvs[i].setAttributeValue("$TOSTRING", str_tostring); //
			hvs[i].setToStringFieldName("$TOSTRING"); //
		}
		return hvs; //
	}

	/**
	 * 取得存储在数据库存中的图片的64位码!
	 */
	public String getImageUpload64Code(String _batchid) throws Exception {
		return new SysAppDMO().getImageUpload64Code(_batchid); //
	}

	/**
	 * 取得菜单的SQL
	 * 
	 * @param _loginuserCode
	 * @return
	 */
	protected HashVO[] getMenuHVs(String _loginuserId, String _userCode, boolean _isFnFilter, HashVO[] _allMenus, String[] _myRoleIds) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		TBUtil tbUtil = new TBUtil(); //

		// 如果是超级用户则返回所有菜单!
		if (_userCode.equalsIgnoreCase("pushroot")) {
			HashVO[] hvs_all = _allMenus; //
			for (int i = 0; i < hvs_all.length; i++) {
				hvs_all[i].setAttributeValue("$reason", "为是pushroot用户,所以直接有所有菜单权限!"); //
			}
			return hvs_all;
		}

		// 如果不参与权限过滤,则返回所有!
		if (!_isFnFilter) {
			HashVO[] hvs_all = _allMenus; //
			for (int i = 0; i < hvs_all.length; i++) {
				hvs_all[i].setAttributeValue("$reason", "A:因为该用户不参与权限过滤,所以直接有所有菜单权限!"); //
			}
			return hvs_all;
		}

		// 现在就一条SQL
		String[] str_commRoleIds = null; // 通用角色做缓存
		if (ServerCacheDataFactory.static_str_commroles == null) {
			String[] str_firstData = commDMO.getStringArrayFirstColByDS(null, "select id from pub_role where code in ('一般员工','一般人员','一般用户','所有人员','普通员工','普通人员','普通用户')"); //
			if (str_firstData == null) {
				ServerCacheDataFactory.static_str_commroles = new String[0];
			} else {
				ServerCacheDataFactory.static_str_commroles = str_firstData; //
			}
		}
		str_commRoleIds = ServerCacheDataFactory.static_str_commroles; //

		HashSet hst_roleIds = new HashSet(); // 将通用角色(一般用户,普通员工等)的id与本人直接关联的角色id,进行唯一性合并...
		if ((str_commRoleIds != null && str_commRoleIds.length > 0) || (_myRoleIds != null && _myRoleIds.length > 0)) { //
			if (str_commRoleIds != null && str_commRoleIds.length > 0) {
				for (int i = 0; i < str_commRoleIds.length; i++) {
					hst_roleIds.add(str_commRoleIds[i]); //
				}
			}
			if (_myRoleIds != null && _myRoleIds.length > 0) {
				for (int i = 0; i < _myRoleIds.length; i++) {
					hst_roleIds.add(_myRoleIds[i]); //
				}
			}
		}

		// 强转成所有数组
		String[] str_allRoleIds = (String[]) hst_roleIds.toArray(new String[0]); // 再转成数组!!
		String str_sql_all = "select menuid,userid,'usercode' c1,'username' c2, 'usermenu' dtype from pub_user_menu where userid=" + _loginuserId; //
		if (str_allRoleIds != null && str_allRoleIds.length > 0) {
			StringBuilder sb_sql_1 = new StringBuilder(); //
			sb_sql_1.append("select t1.menuid,t1.roleid,t2.code rolecode,t2.name rolename,'rolemenu' dtype "); //
			sb_sql_1.append("from pub_role_menu t1,pub_role t2 "); //
			sb_sql_1.append("where t1.roleid=t2.id ");
			sb_sql_1.append("and t1.roleid in (" + tbUtil.getInCondition(str_allRoleIds) + ") "); //			
			str_sql_all = str_sql_all + " union all " + sb_sql_1.toString(); //
		}

		// 将两条SQL拼接后查询数据库!!!
		String[][] str_userMenuArray = commDMO.getStringArrayByDS(null, str_sql_all); //
		if (str_userMenuArray == null || str_userMenuArray.length <= 0) {
			return new HashVO[0]; //
		}

		// 用户角色直接绑定的菜单!!
		HashSet hst_menuIds = new HashSet(); // 唯一性过滤
		HashSet hst_roleNames = new HashSet(); //
		boolean isBecauceUser = false; // 
		for (int i = 0; i < str_userMenuArray.length; i++) {
			hst_menuIds.add(str_userMenuArray[i][0]); //
			if (str_userMenuArray[i][4].equals("usermenu")) { // 如果是因为"角色"直接与"菜单"绑定的
				isBecauceUser = true;
			} else if (str_userMenuArray[i][4].equals("rolemenu")) { // 如果是因为"角色"直接与"菜单"绑定的
				hst_roleNames.add("【" + str_userMenuArray[i][2] + "/" + str_userMenuArray[i][2] + "】"); //
			}
		}
		// 拼成一个字符串...
		String[] str_roleNames = (String[]) hst_roleNames.toArray(new String[0]); //
		StringBuilder sb_roleNames = new StringBuilder(); //
		for (int i = 0; i < str_roleNames.length; i++) {
			sb_roleNames.append(str_roleNames[i]); //
		}

		// 从所有菜单中寻找!
		ArrayList al_list = new ArrayList(); //
		for (int i = 0; i < _allMenus.length; i++) { //
			if ("Y".equalsIgnoreCase(_allMenus[i].getStringValue("isalwaysopen")) || hst_menuIds.contains(_allMenus[i].getStringValue("id"))) { // 如果剖是打开,或者在我的范围中
				al_list.add(_allMenus[i]); //
			}
		}
		HashVO[] hvs = (HashVO[]) al_list.toArray(new HashVO[0]); //
		for (int i = 0; i < hvs.length; i++) {
			StringBuilder sb_reason = new StringBuilder(); //
			if ("Y".equalsIgnoreCase(hvs[i].getStringValue("isalwaysopen"))) { //
				sb_reason.append("B:因为该菜单功能点是永远开放的;\r\n"); //
			}

			if (isBecauceUser) {
				sb_reason.append("C:因为该菜单绑定了本用户;\r\n"); //
			}

			// 如果是我的角色!!
			if (sb_roleNames.length() > 0) { //
				sb_reason.append("D:因为该菜单绑定了我的这些角色(也可能有通用角色):[" + sb_roleNames.toString() + "];\r\n"); //
			}
			hvs[i].setAttributeValue("$reason", sb_reason.toString()); //
		}
		return hvs; //

	}

	protected String getTaskdealSQL(String[] workposition) {
		return "select * from pub_task_deal"; //
	}

	/**
	 * 取出所有按钮
	 * 
	 * @return
	 */
	protected String getButtonSQL() {
		return "select * from pub_menu where id not in (select parentmenuid from pub_menu where parentmenuid is not null) and showintoolbar ='y' order by id";
	}

	protected String getNewsSQL(String _usercode) {
		return "";
	}

	/**
	 * 根据角色与机构类型公式取得登录人员的所在部门的
	 * @param _formula
	 * @return
	 * @throws Exception
	 */
	public String[] getLoginUserCorpAreasByRoleAndCorpTypeFormula(String _formula) throws Exception {
		return new SysAppDMO().getLoginUserCorpAreasByRoleAndCorpTypeFormula(_formula);
	}

	// 根据公式取得登录人员的机构范围的根结点的主键!! 比如福州分行的这条记录的id值
	public String getLoginUserCorpAreasRootIDByTypeCase(String _corpTypeCase) throws Exception {
		return new SysAppDMO().getLoginUserCorpAreasRootIDByTypeCase(_corpTypeCase); //
	}

	public ArrayList getLoginUserCorpAreasByTypeCase(String _corpTypeCase) throws Exception {
		return new SysAppDMO().getLoginUserCorpAreasByTypeCase(_corpTypeCase); //
	}

	public ArrayList getOneUserCorpAreasByTypeCase(String _userId, String _corpTypeCase) throws Exception {
		return new SysAppDMO().getOneUserCorpAreasByTypeCase(_userId, _corpTypeCase); //
	}

	/**
	 * 判断登录人员是否具有某些角色
	 * @param _roleCodes
	 * @return
	 * @throws Exception
	 */
	public boolean isLoginUserContainsRole(String _roleCodes) throws Exception {
		return new SysAppDMO().isLoginUserContainsRole(_roleCodes); //
	}

	public boolean isLoginUserContainsRole(String[] _roleCodes) throws Exception {
		return new SysAppDMO().isLoginUserContainsRole(_roleCodes); //
	}

	/**
	 * 判断某个人员是否具有某些角色
	 * @param _userid
	 * @param _roleCodes
	 * @return
	 * @throws Exception
	 */
	public boolean isOneUserContainsRole(String _userid, String _roleCodes) throws Exception {
		return new SysAppDMO().isOneUserContainsRole(_userid, _roleCodes); //
	}

	private CommDMO getDMO() {
		return new CommDMO(); //
	}

	/**
	 * 增加一个快捷方式!
	 */
	public void addShortCut(String _userId, String _menuId) throws Exception {
		String str_sql_find = "select * from pub_user_shortcut where userid='" + _userId + "' and menuid='" + _menuId + "'";
		HashVO[] vos = getDMO().getHashVoArrayByDS(null, str_sql_find); //
		if (vos != null && vos.length > 0) {
			throw new WLTAppException("该菜单已注册为了快捷方式!!!"); //
		}

		String str_newid = getDMO().getSequenceNextValByDS(null, "s_pub_user_shortcut");
		String str_sql_1 = "delete from pub_user_shortcut where userid='" + _userId + "' and menuid='" + _menuId + "'"; //
		String str_sql_2 = "insert into pub_user_shortcut (id,userid,menuid) values (" + str_newid + ",'" + _userId + "','" + _menuId + "')";
		getDMO().executeBatchByDS(null, new String[] { str_sql_1, str_sql_2 }); //
	}

	/**
	 * 得到登录页面的所有热点
	 * 
	 * @return
	 * @throws Exception
	 */
	public String[][] getLoginHrefs() throws Exception {
		return ServerEnvironment.getLoginHref(); //
	}

	/**
	 * 导数据功能的创建模板中列名
	 */
	public void transferDB_CreateColdata(String[] _transfernames) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		for (int i = 0; i < _transfernames.length; i++) {
			HashVO[] hvs_1 = commDMO.getHashVoArrayByDS(null, "select * from wlt_transferdb where transfername='" + _transfernames[i] + "'"); //
			if (hvs_1 != null && hvs_1.length > 0) {
				String str_headid = hvs_1[0].getStringValue("id"); // 主表主键
				String str_dsname = hvs_1[0].getStringValue("target_dsname"); //
				if (str_dsname == null || str_dsname.trim().equals("")) {
					str_dsname = ClientEnvironment.getInstance().getDefaultDataSourceName(); //
				}

				String str_tablename = hvs_1[0].getStringValue("target_tablename"); //
				TableDataStruct tds = commDMO.getTableDataStructByDS(str_dsname, "select * from " + str_tablename + " where 1=2"); //
				String[] str_colNames = tds.getHeaderName();

				Vector v_sqls = new Vector(); //
				v_sqls.add("delete from wlt_transferdb_b where headid='" + str_headid + "'"); //
				for (int j = 0; j < str_colNames.length; j++) {
					String str_newId = commDMO.getSequenceNextValByDS(null, "s_wlt_transferdb_b"); //
					StringBuffer sb_sql = new StringBuffer(); //
					sb_sql.append("insert into wlt_transferdb_b "); //
					sb_sql.append("("); //
					sb_sql.append("id,"); //
					sb_sql.append("headid,"); //
					sb_sql.append("transtype,"); //
					sb_sql.append("source_colname,"); //
					sb_sql.append("target_colname"); //
					sb_sql.append(")"); //
					sb_sql.append(" values "); //
					sb_sql.append("("); //
					sb_sql.append("'" + str_newId + "',"); //
					sb_sql.append("'" + str_headid + "',"); //
					sb_sql.append("'数据列',"); //
					sb_sql.append("'" + str_colNames[j] + "',"); //
					sb_sql.append("'" + str_colNames[j] + "'"); //
					sb_sql.append(")"); //
					v_sqls.add(sb_sql.toString()); //
				}
				commDMO.executeBatchByDS(null, v_sqls); //
			}
		}
	}

	/**
	 * 转移数据_真正导数据
	 * 
	 * @param _transfernames
	 * @throws Exception
	 */
	public void transferDB_import(String[] _transfernames) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		for (int i = 0; i < _transfernames.length; i++) {
			HashVO[] hvs_1 = commDMO.getHashVoArrayByDS(null, "select * from wlt_transferdb where transfername='" + _transfernames[i] + "'"); //
			String str_headid = hvs_1[0].getStringValue("id"); // 主表主键
			String str_source_dsname = hvs_1[0].getStringValue("source_dsname"); //
			String str_source_tablename = hvs_1[0].getStringValue("source_tablename"); //
			String str_source_filtersql = hvs_1[0].getStringValue("source_filtersql"); //

			String str_target_dsname = hvs_1[0].getStringValue("target_dsname"); //
			String str_target_tablename = hvs_1[0].getStringValue("target_tablename"); //
			String str_target_filtersql = hvs_1[0].getStringValue("target_filtersql"); //

			if (str_source_dsname == null || str_source_dsname.trim().equals("")) {
				str_source_dsname = ClientEnvironment.getInstance().getDefaultDataSourceName(); //
			}

			if (str_target_dsname == null || str_target_dsname.trim().equals("")) {
				str_target_dsname = ClientEnvironment.getInstance().getDefaultDataSourceName(); //
			}

			String str_selectsql = "select * from " + str_source_tablename + " where 1=1"; //
			if (str_source_filtersql != null && !str_source_filtersql.trim().equals("")) {
				str_selectsql = str_selectsql + " and (" + str_source_filtersql + ")";
			}

			String str_deletesql = "delete from " + str_target_tablename + " where 1=1 "; //
			if (str_target_filtersql != null && !str_target_filtersql.trim().equals("")) {
				str_deletesql = str_deletesql + " and (" + str_target_filtersql + ")";
			}

			HashVO[] hvs_2 = commDMO.getHashVoArrayByDS(null, "select * from wlt_transferdb_b where headid='" + str_headid + "'"); //

			HashVO[] hvs_3 = commDMO.getHashVoArrayByDS(str_source_dsname, str_selectsql); //
			Vector v_sqls = new Vector(); //
			v_sqls.add(str_deletesql); //

			for (int j = 0; j < hvs_3.length; j++) {
				StringBuffer sb_sql = new StringBuffer(); //
				sb_sql.append("insert into " + str_target_tablename + " "); //
				sb_sql.append("(");
				for (int k = 0; k < hvs_2.length; k++) {
					String str_scn = hvs_2[k].getStringValue("target_colname"); //
					if (k != hvs_2.length - 1) {
						str_scn = str_scn + ","; //
					}
					sb_sql.append(str_scn); // 目标列
				}
				sb_sql.append(")");
				sb_sql.append(" values ");
				sb_sql.append("(");
				for (int k = 0; k < hvs_2.length; k++) {
					String str_tvalue = null; //
					if (hvs_2[k].getStringValue("transtype").equals("常量")) {
						str_tvalue = "'" + hvs_2[k].getStringValue("source_colname") + "'"; //
					} else {
						str_tvalue = hvs_3[j].getStringValue(hvs_2[k].getStringValue("source_colname")); //
						if (str_tvalue == null) {
							str_tvalue = "null";
						} else {
							str_tvalue = "'" + str_tvalue + "'"; //
						}
					}

					if (k != hvs_2.length - 1) {
						str_tvalue = str_tvalue + ","; //
					}

					sb_sql.append(str_tvalue); //
				}
				sb_sql.append(")");

				v_sqls.add(sb_sql.toString()); //
			}

			commDMO.executeBatchByDS(str_target_dsname, v_sqls); //
		}
	}

	/**
	 * 根据一个机构,取得其所有直系父机构...
	 * 
	 * @param _deptid
	 * @return
	 */
	public String[] getOneDeptDirtDepts(String _deptid) throws Exception {
		CommDMO dmo = new CommDMO();
		ArrayList al_dept = new ArrayList(); //
		recursionGetDeptId(dmo, al_dept, _deptid); //
		return (String[]) al_dept.toArray(new String[0]); //
	}

	public String getLoginUserParentCorpItemValueByType(String _corpType, String _nvlCorpType, String _itemName) throws Exception {
		return new SysAppDMO().getLoginUserParentCorpItemValueByType(_corpType, _nvlCorpType, _itemName); //
	}

	/**
	 * 根据宏代码找出某个人员/机构的某个父亲机构,如果宏代码为空,则找出所有父亲记录!
	 * @param _type
	 * @param _consValue
	 * @param _macroName
	 * @return
	 * @throws Exception
	 */
	public HashVO[] getParentCorpVOByMacro(int _type, String _consValue, String _macroName) throws Exception {
		return new SysAppDMO().getParentCorpVOByMacro(_type, _consValue, _macroName); //
	}

	/**
	 * 递归取得所有机构
	 * 
	 * @param _al
	 * @param _deptid
	 * @throws Exception
	 */
	private void recursionGetDeptId(CommDMO _dmo, ArrayList _al, String _deptid) throws Exception {
		_al.add(_deptid); //
		String str_parentid = _dmo.getStringValueByDS(null, "select parentid from pub_corp_dept where id='" + _deptid + "'"); //
		if (str_parentid != null && !str_parentid.trim().equals("") && !str_parentid.trim().equals("null")) {
			recursionGetDeptId(_dmo, _al, str_parentid); // 递归调用!
		}
	}

	/**
	 * 得到一个部门所有子部门的主键列表
	 */
	public String[] getSubDeptID(String _parentdeptid) throws Exception {
		return new SysAppDMO().getSubDeptID(_parentdeptid);
	}

	// 所有所有定义的表名
	public String[][] getAllTableDefineNames() throws Exception {
		return new WLTDictDMO().getAllTableDefineNames(); //
	}

	/**
	 * 根据表名模糊查询
	 */
	public String[][] getAllTableDefineNames(String tableName) throws Exception {
		return new WLTDictDMO().getAllTableDefineNames(tableName); //
	}

	/**
	 * 得到所有只在定义中有的表
	 */
	public String[][] getAllTableOnlyDFhave() throws Exception {
		return new WLTDictDMO().getAllTableOnlyDFhave(); //
	}

	/**
	 * 得到所有平台与数据源都有的表
	 * @return
	 */
	public String[][] getAllTableBHhave() throws Exception {
		return new WLTDictDMO().getAllTableBHhave(); //
	}

	public List getCompareLISTByTabName(String _dataSourceName, String _tabName) throws Exception {
		return new WLTDictDMO().getCompareLISTByTabName(_dataSourceName, _tabName); //
	}

	/**
	 * 得到所有只数据源有的表
	 * @return
	 */
	public String[][] getAllTableOnlyDBhave() throws Exception {
		return new WLTDictDMO().getAllTableOnlyDBhave(); //
	}

	// 根据表名得到列名
	public String[][] getAllColumnsDefineNames(String _tabName) throws Exception {
		return new WLTDictDMO().getAllColumnsDefineNames(_tabName);
	}

	// 根据表各取得定义的Create脚本
	public String getCreateSQLByTabDefineName(String _tabName) throws Exception {
		return new WLTDictDMO().getCreateSQLByTabDefineName(_tabName); //
	}

	// 根据数据库类型和表、字段名、类型、长度取得定义的alter脚本
	public String getAlterSQLByTabDefineName(String _dbtype, String _tabName, String _cName, String _cType, String _cLength) throws Exception {
		return new WLTDictDMO().getAlterSQLByTabDefineName(_dbtype, _tabName, _cName, _cType, _cLength); //
	}

	// 取得所有都有的表的alter语句
	public String getAllAlterSQLByTabDefineName() throws Exception {
		return new WLTDictDMO().getAllAlterSQLByTabDefineName();
	}

	// 生成某一个表的比较信息
	public String getCompareSQLByTabName(String _tabName) throws Exception {
		return new WLTDictDMO().getCompareSQLByTabName(_tabName); //
	}

	// 根据实际表名反向生成Java代码
	public String reverseCreateJavaCode(String _tableName) throws Exception {
		return new WLTDictDMO().reverseCreateJavaCode(_tableName);
	}

	public String exportTableSchema(DataSourceVO _sourceDB, DataSourceVO _destDB) throws Exception {
		return new ExportDBDMO(_sourceDB.getDbtype(), _destDB.getDbtype()).getTablesSchema(_sourceDB.getName());
	}

	public String exportTableView(DataSourceVO _sourceDB, DataSourceVO _destDB) throws Exception {
		return new ExportDBDMO(_sourceDB.getDbtype(), _destDB.getDbtype()).getViewDefines(_sourceDB.getName());
	}

	public void transferDBDataByds(DataSourceVO _sourceDB, DataSourceVO _destDB) throws Exception {
		new ExportDBDMO(_sourceDB.getDbtype(), _destDB.getDbtype()).getData(_sourceDB.getName(), _destDB.getName());
	}

	public Hashtable exportTableDataAsText(DataSourceVO _sourcedb, DataSourceVO _destdb) throws Exception {
		return new ExportDBDMO(_sourcedb.getDbtype(), _destdb.getDbtype()).getDataAsText();
	}

	public Vector updateSequence(DataSourceVO _sourcedb, DataSourceVO _destdb) throws Exception {
		return new ExportDBDMO(_sourcedb.getDbtype(), _destdb.getDbtype()).updateSequence();
	}

	public File[] getSystemFiles(File file) throws Exception {
		return new SystemFileDMO().getSystemFiles(file);
	}

	public boolean hasDirectory(File file) throws Exception {
		return new SystemFileDMO().hasDirectory(file);
	}

	public void xchTest() throws Exception {
		CommDMO dmo = new CommDMO();
		HashMap map = dmo.getHashMapBySQLByDS(null, "select concat(userid,'_',userdept) c1, '1' c2 from pub_user_post"); //
		HashVO[] hvs = dmo.getHashVoArrayByDS(null, "select id,pk_dept from pub_user where pk_dept is not null"); //
		for (int i = 0; i < hvs.length; i++) {
			String str_key = hvs[i].getStringValue("id") + "_" + hvs[i].getStringValue("pk_dept"); //
			if (!map.containsKey(str_key)) {
				String strPnewpk = dmo.getSequenceNextValByDS(null, "S_PUB_USER_POST"); //
				dmo.executeUpdateByDSImmediately(null, "insert into pub_user_post (id,userid,userdept,isdefault) values ('" + strPnewpk + "','" + hvs[i].getStringValue("id") + "','" + hvs[i].getStringValue("pk_dept") + "','Y')"); // //
			}
		}
	}

	public void resetAllCorpBlParentCorpIds() throws Exception {
		CommDMO commDMO = new CommDMO();
		HashVO[] hvs = commDMO.getHashVoArrayAsTreeStructByDS(null, "select * from pub_corp_dept", "id", "parentid", "seq", null); //
		ArrayList al_sqls = new ArrayList(); //
		for (int i = 0; i < hvs.length; i++) {
			if (hvs[i].getStringValue("$parentpathids") != null) {
				al_sqls.add("update pub_corp_dept set blparentcorpids='" + hvs[i].getStringValue("$parentpathids") + "' where id='" + hvs[i].getStringValue("id") + "'"); //
			}
		}
		commDMO.executeBatchByDS(null, al_sqls); // 执行一把!!量很大!!
		ServerCacheDataFactory.getInstance().registeCorpCacheData(); // 重新注册一下缓存!!
	}

	public String getSystemOptions(String key, String dvl) throws Exception {
		// TODO Auto-generated method stub
		CommDMO dmo = new CommDMO();
		String str_strvalue = dmo.getStringValueByDS(null, "select envvalue from pub_envvalue where envkey ='" + key + "'"); //
		if (str_strvalue == null) {
			return dvl;
		} else {
			return str_strvalue;
		}
	}

	public String setTotalLoginCount() throws Exception {
		String value = null;
		CommDMO dmo = new CommDMO();
		String str_strvalue = dmo.getStringValueByDS(null, "select envvalue from pub_envvalue where envkey ='totallogincount_'"); //
		if (str_strvalue == null) {
			value = "1";
			dmo.executeBatchByDS(null, new String[] { "insert into pub_envvalue (id,envkey,envvalue,remark) values (" + dmo.getSequenceNextValByDS(null, "s_pub_envvalue") + ",'totallogincount_','1','')" });
		} else {
			value = (new BigDecimal(str_strvalue).add(new BigDecimal(1))).toString();
			dmo.executeUpdateByDS(null, "update pub_envvalue set envvalue = '" + value + "' where envkey = 'totallogincount_'"); //
		}
		return value;
	}

	public String setUserLoginCount() throws Exception {
		CommDMO dmo = new CommDMO();
		String userkey = "userlogincount_" + dmo.getCurrSession().getLoginUserId();
		String uservalue = null;
		String str_strvalue = dmo.getStringValueByDS(null, "select envvalue from pub_envvalue where envkey ='" + userkey + "'"); //
		if (str_strvalue == null) {
			uservalue = "1";
			dmo.executeBatchByDS(null, new String[] { "insert into pub_envvalue (id,envkey,envvalue,remark) values (" + dmo.getSequenceNextValByDS(null, "s_pub_envvalue") + ",'" + userkey + "','1','')" });
		} else {
			uservalue = (new BigDecimal(str_strvalue).add(new BigDecimal(1))).toString();
			dmo.executeUpdateByDS(null, "update pub_envvalue set envvalue = '" + uservalue + "' where envkey = '" + userkey + "'"); //
		}
		return uservalue;
	}

	public void setSystemOptions(String key, String value) throws Exception {
		// TODO Auto-generated method stub
		CommDMO dmo = new CommDMO();
		String str_strvalue = dmo.getStringValueByDS(null, "select envvalue from pub_envvalue where envkey ='" + key + "'"); //
		if (str_strvalue == null) {
			dmo.executeBatchByDS(null, new String[] { "insert into pub_envvalue (id,envkey,envvalue,remark) values (" + dmo.getSequenceNextValByDS(null, "s_pub_envvalue") + ",'" + key + "','" + value + "','')" });
		} else {
			dmo.executeUpdateByDS(null, "update pub_envvalue set envvalue = '" + value + "' where envkey = '" + key + "'"); //
		}

	}

	public HashMap getServerLoginUserMap() throws Exception {
		return ServerEnvironment.getInstance().getLoginUserMap();
	}

	public BillCellVO dataAccessPolicySetBuildCellVO(HashMap condition, CurrLoginUserVO _loginUserVO) throws Exception {
		return new SysAppDMO().dataAccessPolicySetBuildCellVO(condition, _loginUserVO);
	}

	public void registeTableCacheData(String _keyName, String _tableName) {
		ServerCacheDataFactory.getInstance().registeTableCacheData(_keyName, _tableName); //
	}

	public void registeTreeCacheData(String _keyName, String _tableName, String _idField, String _parentId, String _seqField) {
		ServerCacheDataFactory.getInstance().registeTreeCacheData(_keyName, _tableName, _idField, _parentId, _seqField); //
	}

	public HashVO[] getCacheTableDataByAutoCreate(String _keyName, String _tableName) {
		return ServerCacheDataFactory.getInstance().getCacheTableDataByAutoCreate(_keyName, _tableName);
	}

	public HashVO[] getCacheTreeDataByAutoCreate(String _keyName, String _tableName, String _idField, String _parentId, String _seqField) {
		return ServerCacheDataFactory.getInstance().getCacheTreeDataByAutoCreate(_keyName, _tableName, _idField, _parentId, _seqField); //
	}

	public HashVO[] getCorpCacheDataByAutoCreate() {
		return ServerCacheDataFactory.getInstance().getCorpCacheDataByAutoCreate();
	}

	public void registeCorpCacheData() {
		ServerCacheDataFactory.getInstance().registeCorpCacheData();
	}

	public HashVO[] getUserCacheDataByAutoCreate() {
		return ServerCacheDataFactory.getInstance().getUserCacheDataByAutoCreate();
	}

	public void registeUserCacheData() {
		ServerCacheDataFactory.getInstance().registeUserCacheData();
	}

	// public RtActionTempletVO getRunTimeActionTempletVO(String _templetName)
	// throws Exception {
	// return
	// RuntimeActionTempletFactory.getInstance().getTempletVOByName(_templetName);//
	// }

	public String compileRunTimeActionCode(String _actionName, String _codeText, boolean _isSave) throws Exception {
		return new RunTimeActionBSUtil().compileRunTimeActionCode(_actionName, _codeText, _isSave); //
	}

	// 加载Java动态代码
	public HashMap loadRuntimeActionCode(String _actionName) throws Exception {
		return new RunTimeActionBSUtil().loadRuntimeActionCode(_actionName); //
	}

	public String[][] compareDictByDB() throws Exception {
		// TODO Auto-generated method stub
		return new DataBaseUtilDMO().compareDictByDB(null, null);
	}

	public String[][] compareMenuDateByDB() throws Exception {
		return new DataBaseUtilDMO().compareMenuDateByDB();
	}

	public void dealOneMenuCommit(String codeValue) throws Exception {
		new DataBaseUtilDMO().dealOneMenuCommit(codeValue);
	}

	public String[][] compareRegbuttonDateByDB() throws Exception {
		return new DataBaseUtilDMO().compareRegbuttonDateByDB();
	}

	public String[][] compareRegformatPanelDateByDB() throws Exception {
		return new DataBaseUtilDMO().compareRegformatPanelDateByDB();
	}

	public String[][] compareRegregisterDateByDB() throws Exception {
		return new DataBaseUtilDMO().compareRegregisterDateByDB();
	}

	public String[][] compareComboboxdictDateByDB() throws Exception {
		return new DataBaseUtilDMO().compareComboboxdictDateByDB();
	}

	public String[][] compareOptionDateByDB() throws Exception {
		return new DataBaseUtilDMO().compareOptionDateByDB();
	}

	public String[][] compareLookandfeelDateByDB() throws Exception {
		return new DataBaseUtilDMO().compareLookandfeelDateByDB();
	}

	public String[][] compareUserDateByDB() throws Exception {
		return new DataBaseUtilDMO().compareUserDateByDB();
	}

	public String[][] comparetempletDateByDB() throws Exception {
		return new DataBaseUtilDMO().comparetempletDateByDB();
	}

	public String[][] compareViewByDB() throws Exception {
		return new DataBaseUtilDMO().compareViewByDB();
	}

	public String[][] getAllInstallPackages(String _subdir) throws Exception {
		return new BSUtil().getAllInstallPackages(_subdir); //
	}

	public String[] getAllIntallTablesByPackagePrefix(String _package_prefix) throws Exception {
		return new InstallDMO().getAllIntallTablesByPackagePrefix(_package_prefix); //
	}

	public String createTableByPackagePrefix(String _package_prefix, String _tabName) throws Exception {
		return new InstallDMO().createTableByPackagePrefix(_package_prefix, _tabName); //
	}

	public String[] getAllIntallViewsByPackagePrefix(String _package_prefix) throws Exception {
		return new InstallDMO().getAllIntallViewsByPackagePrefix(_package_prefix); //
	}

	public String createViewByPackagePrefix(String _package_prefix, String _viewName) throws Exception {
		return new InstallDMO().createViewByPackagePrefix(_package_prefix, _viewName); //
	}

	public String[] getAllIntallInitDataByPackagePrefix(String _package_prefix, String _xtdatadir) throws Exception {
		return new InstallDMO().getAllIntallInitDataByPackagePrefix(_package_prefix, _xtdatadir); //
	}

	public String InsertInitDataByPackagePrefix(String _package_prefix, String _xtdatadir, String _tabName) throws Exception {
		return new InstallDMO().InsertInitDataByPackagePrefix(_package_prefix, _xtdatadir, _tabName); //
	}

	// 取得扩展数据安装文件清单
	public String[][] getExt3DataXmlFiles(String _package_prefix) throws Exception {
		return new InstallDMO().getExt3DataXmlFiles(_package_prefix); //
	}

	// 安装扩展数据之某个XML文件
	public String installExt3Data(String _xmlFileName) throws Exception {
		return new InstallDMO().installExt3Data(_xmlFileName); //
	}

	// 返回所有注册菜单!!!
	public ArrayList getAllRegistMenu() throws Exception {
		return new InstallDMO().getAllRegistMenu(); //
	}

	// 取得某个注册菜单的实际命令!
	public String[] getOneRegMenuCommand(String _xmlFile, String _menuName) throws Exception {
		return new InstallDMO().getOneRegMenuCommand(_xmlFile, _menuName); //
	}

	// 取得级联删除的SQL
	public String[] getCascadeDeleteSQL(String _table, String _field, String _value) throws Exception {
		return new InstallDMO().getCascadeDeleteSQL(_table, _field, _value); //
	}

	// 级联删除SQL,批量处理!
	public String[] doCascadeDeleteSQL(String _table, String[][] _fieldValues, String _sql, boolean _isAutoExec) throws Exception {
		InstallDMO installDMO = new InstallDMO(); //
		ArrayList al_sqls = new ArrayList(); //
		for (int i = 0; i < _fieldValues.length; i++) {
			String[] str_sqls = installDMO.getCascadeDeleteSQL(_table, _fieldValues[i][0], _fieldValues[i][1]); // //
			if (str_sqls != null && str_sqls.length > 0) {
				al_sqls.addAll(Arrays.asList(str_sqls)); //
			}
		}
		if (_sql != null) {
			al_sqls.add(_sql); //
		}
		String[] str_sqls = (String[]) al_sqls.toArray(new String[0]); //
		if (_isAutoExec) {
			boolean isps = SystemOptions.getBooleanValue("是否启用预编译", false);
			if (isps) {
				new CommDMO().executeBatchByDS(null, Arrays.asList(new String[] { _sql }), true, true, true);
			} else {
				new CommDMO().executeBatchByDS(null, str_sqls); //
			}
		}
		return str_sqls; //
	}

	// 级联修改所有的SQL
	public String[] getCascadeUpdateSQL(String _table, String _field, String _oldvalue, String _newValue) throws Exception {
		return new InstallDMO().getCascadeUpdateSQL(_table, _field, _oldvalue, _newValue); //
	}

	// 可以批量!!!
	public String[] doCascadeUpdateSQL(String _table, String[][] _changedValues, String _sql, boolean _isAutoExec) throws Exception {
		InstallDMO installDMO = new InstallDMO(); //
		ArrayList al_sqls = new ArrayList(); //
		if (_sql != null) {
			al_sqls.add(_sql); //
		}
		if (_changedValues != null && _changedValues.length > 0) {
			for (int i = 0; i < _changedValues.length; i++) {
				String[] str_sqls = installDMO.getCascadeUpdateSQL(_table, _changedValues[i][0], _changedValues[i][1], _changedValues[i][2]); //
				if (str_sqls != null && str_sqls.length > 0) {
					al_sqls.addAll(Arrays.asList(str_sqls)); //
				}
			}
		}
		String[] str_sqls = (String[]) al_sqls.toArray(new String[0]); //
		if (_isAutoExec) {
			boolean isps = SystemOptions.getBooleanValue("是否启用预编译", false);
			if (isps) {
				new CommDMO().executeBatchByDS(null, Arrays.asList(new String[] { _sql }), true, true, true);
			} else {
				new CommDMO().executeBatchByDS(null, str_sqls); //
			}
		}
		return str_sqls;
	}

	// 取得级联警告的SQL
	public String[] getCascadeWarnSQL(boolean _isPreSelect) throws Exception {
		return new InstallDMO().getCascadeWarnSQL(_isPreSelect); //
	}

	/**
	 * 取得所有可以安装的SQL文件
	 * 
	 * @return
	 * @throws Exception
	 */
	public String[] getAllInstallSQLTexts() throws Exception {
		return new DataBaseUtilDMO().getAllInstallSQLTexts(null, null); //
	}

	public ArrayList getLoginUserDeptIDs(String[] _filter) throws Exception {
		return new SysAppDMO().getLoginUserDeptIDs(_filter);
	}

	public HashVO getLoginUserInfo() throws Exception {
		return new SysAppDMO().getLoginUserInfo();
	}

	private String getTableColsMap(String tableName) throws Exception {
		String renStr = null;
		if (tableColsMap.containsKey(tableName)) {
			renStr = (String) tableColsMap.get(tableName);
		} else {
			renStr = new TBUtil().getTableAllCols(tableName);
			tableColsMap.put(tableName, renStr);
		}
		return renStr;
	}

	/**
	 * 取得所有表格的记录数的结果,在做安装盘或实施过程中都需要这个!!! 【xch/2012-02-23】
	 */
	public String getAllTableRecordCountInfo(String[] _tables) throws Exception {
		String[][] str_allRegTables = new InstallDMO().getAllIntallTablesDescr(); // 所有注册的物理表!!
		String[][] str_tables = null; //
		if (_tables == null) { // 如果入参为空
			str_tables = str_allRegTables; // 直接使用所有注册表!
		} else { // 如果指定的表!
			str_tables = new String[_tables.length][2]; //
			for (int i = 0; i < str_tables.length; i++) {
				str_tables[i][0] = _tables[0]; // 表名!
				for (int j = 0; j < str_allRegTables.length; j++) { // 尝试从注册表中找到对应的说明!
					if (str_allRegTables[j][0].equalsIgnoreCase(str_tables[i][0])) {
						str_tables[i][1] = str_allRegTables[j][1]; // 说明等于注册表的说明!!
						break; //
					}
				}
			}
		}
		CommDMO commDMO = new CommDMO(); //
		StringBuilder sb_info = new StringBuilder(); //
		int li_allTablecount = 0; //
		long ll_allRecordcount = 0; //
		for (int i = 0; i < str_tables.length; i++) {
			String str_count = commDMO.getStringValueByDS(null, "select count(*) from " + str_tables[i][0]); //
			int li_count = Integer.parseInt(str_count); //
			if (li_count > 0) { // 如果记录数大于0
				li_allTablecount = li_allTablecount + 1; //
				ll_allRecordcount = ll_allRecordcount + li_count; //
				sb_info.append("(" + li_allTablecount + ")表 【" + str_tables[i][0] + "】(" + str_tables[i][1] + ") 的记录数=[" + li_count + "]\r\n"); //
			}
		}
		sb_info.append("一共[" + str_tables.length + "]张表,共有[" + li_allTablecount + "]表有数据记录,总记录数[" + ll_allRecordcount + "]"); //
		return sb_info.toString();
	}

	/**
	 * 反向生成序列表中的值!!有时在重新导出某个表格数据为XML,并放到安装目录中时,经常忘记导出对应的序列值(即使导出也很麻烦),这样就会造成安装后再录入数据时,报主键冲突!!! 【xch/2012-02-23】
	 * 而实际上大多数安装表的主键都是“S_表名”所以需要一种机制可以反向生成序列,然后在安装结束后一下子重做一遍!!! 这样会极大的降低做安装盘的工作量!! 即安装盘中是没有pub_sequence_10001.xml的,而是在安装的最后一步时重新反向生成之!!
	 * @return
	 */
	public String reverseSetSequenceValue(String _packageName) throws Exception {
		String[][] str_allRegTables = new InstallDMO().getAllIntallTablesDescr(_packageName); // 先所有注册的物理表!!
		// 先做遍历所有注册表的逻辑,以后应该根据入参,只处理指定表的序列值,这样更强大些!
		ArrayList al_sqls = new ArrayList(); //
		CommDMO commdmo = new CommDMO(); //
		for (int i = 0; i < str_allRegTables.length; i++) { // 遍历!
			String str_table = str_allRegTables[i][0]; // 表名
			String str_pkname = str_allRegTables[i][2]; // 主键字段名
			if (str_pkname == null || str_pkname.trim().equals("")) {
				continue;
			}
			String str_maxvalue = commdmo.getStringValueByDS(null, "select max(" + str_pkname + ") from " + str_table); //
			if (str_maxvalue != null && !str_maxvalue.trim().equals("")) { // 如果最大值不为空!!
				al_sqls.add("insert into pub_sequence (sename,currvalue) values ('S_" + str_table.toUpperCase() + "'," + ((Integer.parseInt(str_maxvalue)) + 1) + ")"); // 往序列表中插入一条记录!!!
			}
		}
		commdmo.executeBatchByDS(null, al_sqls); // //批量插入!
		return "一共往序列表中插入了[" + al_sqls.size() + "]条记录!";
	}

	/**
	 * 取得某一个功能点的在线帮助,在线帮助以前只有一个,在在菜单配置界面中上传word附件,然后所谓查看帮助就是打开这个word附件!!
	 * 但后来发现,Word附件有时经常没有上传,而且Word文件内容太多，客户与实施人员经常根本不看,且内容以操作为主!!所以需要一个表达管理思路的简短文本文件说明!!!
	 * 所以现在的思路是,如果有帮助文字说明,则默认打开之,然后点击按钮再打开Word附件!!  
	 * 【xch/2012-02-27】
	 * @param _menuId 菜单id,根据它来寻找word附件名!
	 * @param _clasName 类名,根据它来寻找help文本!
	 * @return 字符串数组,第一位是help文本,第二位是word附件名!!!
	 * @throws Exception
	 */
	public String[] getMenuHelpInfo(String _menuId, String _clasName) throws Exception {
		String str_returnHelpText = null; //
		if (_clasName != null) { // 如果有类名,应该肯定有类名!!!
			String str_menu_help = null; //
			String str_helppackage = null; // 帮助文件对应的包名!!
			String[][] str_installs = new BSUtil().getAllInstallPackages(null); //
			for (int i = 0; i < str_installs.length; i++) {
				InputStream ins_menuxml = this.getClass().getResourceAsStream(str_installs[i][0] + "RegisterMenu.xml"); //
				if (ins_menuxml != null) {
					org.jdom.Document doc = new org.jdom.input.SAXBuilder().build(ins_menuxml); // 加载XML
					java.util.List list_menu = doc.getRootElement().getChildren("menu"); // 寻找所有menu子结点!
					for (int j = 0; j < list_menu.size(); j++) { // 遍历所有菜单结点
						org.jdom.Element menuNode = (org.jdom.Element) list_menu.get(j); //
						// String str_menu_name =
						// menuNode.getAttributeValue("name"); //菜单名称!!
						String str_menu_cmd = menuNode.getAttributeValue("command"); // 路径!!!
						if (_clasName.equals(str_menu_cmd)) { // 如果路径
							str_menu_help = menuNode.getAttributeValue("help"); // 帮助名称,可能为空,或空字符串!!!
							str_helppackage = str_installs[i][0]; //
							break; // 中断循环
						}
					}
					ins_menuxml.close(); // 关闭流!!
				}
			}
			TBUtil tbUtil = new TBUtil(); //
			// System.out.println("根据类名[" + _clasName + "]找到帮助说明[" +
			// str_menu_help + "]"); //
			if (str_menu_help != null && !str_menu_help.trim().equals("")) { // 如果帮助有定义!!
				InputStream ins_help = this.getClass().getResourceAsStream(str_helppackage + "help.txt"); // 寻找help文件!!
				if (ins_help != null) { // 如果存在help文件!!
					String str_helpfileText = tbUtil.readFromInputStreamToStr(ins_help); // 先取出所有内容!!!
					StringBuilder sb_textAppend = new StringBuilder(); //
					String[] str_helpItems = tbUtil.split(str_menu_help, ";"); // 帮助文件的子项!!
					for (int i = 0; i < str_helpItems.length; i++) { // 遍历所有帮助子项!!!
						// System.out.println("对应帮助文件的所有内容:" +
						// str_helpfileText); ////
						int li_pos = str_helpfileText.indexOf(str_helpItems[i] + "\r\n"); // 看有没有该帮助的标签位置!!
						if (li_pos >= 0) { // 如果找到单独的一行!
							String str_remain = str_helpfileText.substring(li_pos + str_helpItems[i].length(), str_helpfileText.length()); //
							int li_pos_2 = str_remain.indexOf("\r\n#"); // 寻找下一个以#开头的记录,则停止搜索!!!
							if (li_pos_2 > 0) {
								str_remain = str_remain.substring(0, li_pos_2); //
							}
							sb_textAppend.append("【" + str_helpItems[i] + "】之快速帮助:"); //
							sb_textAppend.append(str_remain + "\r\n\r\n"); //
						} else {
							sb_textAppend.append("【" + str_helpItems[i] + "】帮助内容在文件【" + str_helppackage + "help.txt" + "】中没有定义!\r\n\r\n"); //
						}
					}
					str_returnHelpText = sb_textAppend.toString(); //
				} else {
					str_returnHelpText = "没有定义帮助文件[" + str_helppackage + "help.txt" + "]"; //
				}
			}
		}
		String str_wordfileName = new CommDMO().getStringValueByDS(null, "select helpfile from pub_menu where id=" + _menuId); //
		return new String[] { str_returnHelpText, str_wordfileName }; //
	}

	/**
	 *判断某个人员是否可以作为超级管理员操作某些对象!!!
	*超级管理员(绿色通道)是平台后来新增的一个重大概念!!它将极大简化经后的权限问题,尤其是在系统在使用过程成一发生“权限控制过严”时无法操作时!!比如查看工作流加密意见!
	*总有一个后门可以执行所有!久
	 */
	public HashMap isCanDoAsSuperAdmin(String _loginUserId, String _queryTableName, String _savedTableName) throws Exception {
		HashMap rtMap = new HashMap(); // 返回的数据对象!!
		CommDMO dmo = new CommDMO(); //
		TBUtil tbUtil = new TBUtil(); //
		HashVO[] hvs = dmo.getHashVoArrayByDS(null, "select * from pub_role where code like '%超级管理员'"); // 找出所有以“超级管理员”为后辍的角色，这是非常关键的约定!!
		if (hvs.length <= 0) {
			rtMap.put("ReturnType", "1"); //
			rtMap.put("ReturnMsg", "没有定义一个以\"超级管理员\"结尾的角色,机制不存在,所以无权操作!"); //
			return rtMap; //
		}

		ArrayList al_matchVos = new ArrayList(); // 存储与指定的数据对象匹配上的所有超级管理员!!
		for (int i = 0; i < hvs.length; i++) {
			String str_dos = hvs[i].getStringValue("spdataobjs"); // 对应的数据对象!!!
			if (str_dos != null && !str_dos.trim().equals("")) { // 如果有定义,老版本可能没这个字段!
				if (str_dos.trim().equals("*")) { // 如果是*,则表示任何对象都支持,比如一个极限超级管理员!
					al_matchVos.add(hvs[i]); //
				} else {
					String[] str_items = tbUtil.split(str_dos, ";"); // 分割
					boolean isMatch = false; //
					for (int j = 0; j < str_items.length; j++) { // 遍历所有定义的对象条件!!
						if (_queryTableName != null) { // 如果查询表名中包含我定义了,则说明匹配上了,比如查询表名是【v_cmp_cmpfile_1】,我定义的是【cmp_】,是就匹配上了!
							if (_queryTableName.trim().toLowerCase().indexOf(str_items[j].trim().toLowerCase()) >= 0) {
								isMatch = true; //
								break; //
							}
						}

						if (_savedTableName != null) { // 如果查询表名中包含我定义了,则说明匹配上了,比如查询表名是【v_cmp_cmpfile_1】,我定义的是【cmp_】,是就匹配上了!
							if (_savedTableName.trim().toLowerCase().indexOf(str_items[j].trim().toLowerCase()) >= 0) {
								isMatch = true; //
								break; //
							}
						}
					}
					if (isMatch) { // 只要匹配成功了!则该超级管理员是符合条件的超级管理员!!
						al_matchVos.add(hvs[i]); //
					}
				}
			}
			// 以后还要根据机构类型判断登录人员是否属于指定的机构类型,如果是,则才加入!!因为时间关系来不及弄!以后再弄吧！【xch/2012-04-27
			// 17:52】
		}
		if (al_matchVos.size() <= 0) {
			rtMap.put("ReturnType", "2"); //
			rtMap.put("ReturnMsg", "虽然定义了" + hvs.length + "个超级管理员,但没有一条匹配上本业务对象[" + _queryTableName + "/" + _savedTableName + "],所以无权操作!"); //
			return rtMap; //
		}

		// 如果匹配上了超级管理员!
		HashVO[] hvs_superRoles = (HashVO[]) al_matchVos.toArray(new HashVO[0]); // 所有与本对象关联成功的,符合条件的超级管理员!!
		String[] str_superRoleCodes = new String[hvs_superRoles.length]; //
		for (int i = 0; i < str_superRoleCodes.length; i++) {
			str_superRoleCodes[i] = hvs_superRoles[i].getStringValue("code"); // 角色编码
		}
		// 看我是否直接关联了某个超级管理员,如果直接关联了,则客户端不弹出对话框,直接返回true
		String str_count = dmo.getStringValueByDS(null, "select count(*) from v_pub_user_role_1 where userid='" + _loginUserId + "' and rolecode in (" + tbUtil.getInCondition(str_superRoleCodes) + ")"); //
		if (Integer.parseInt(str_count) > 0) {
			rtMap.put("ReturnType", "3"); //
			rtMap.put("ReturnMsg", "本人直接具有某个超级管理员,所以无需弹出密码框,直接操作!"); //
			return rtMap; //
		} else {
			rtMap.put("ReturnType", "4"); //
			rtMap.put("ReturnMsg", "本人没有直接与超级管理员关联,所以需要弹出对话框,在客户输入密码进行操作!"); //
			rtMap.put("AllSuperRoleVOs", hvs_superRoles); // 直接将所有符合条件的超级管理员对象返回!因为客户端拿到后,要动态创建对话框!!!然后输入密码时,直接在客户端校验!不需要再请求服务器了,因为这里已经返回密码了!!
			return rtMap; //
		}
	}

	// 取得登录人员的机构ID, 注意不是部门/处室, 是机构!!
	public HashVO getLoginUserCorpVO() throws Exception {
		return new SysAppDMO().getLoginUserCorpVO();
	}

	public int resetAllSequence() throws Exception {
		int ret = 0;
		ArrayList sqlList = new ArrayList();
		CommDMO commdmo = new CommDMO();
		String[][] str_allTables = commdmo.getAllSysTableAndDescr(null, null, false, true);// 以前是取得tables.xml中的表，各项目中项目表很有可能没有导出tables.xml
		// 或表不全，故需要取数据库中的所有表【李春娟/2012-09-25】
		ArrayList senameList = new ArrayList();
		WLTDBConnection conn = new WLTInitContext().getConn(null); //
		String str_dbtype = ServerEnvironment.getInstance().getDataSourceVO(null).getDbtype(); // 数据库类型
		String str_schema = null;
		if (str_dbtype.equalsIgnoreCase("ORACLE") || str_dbtype.equalsIgnoreCase("MYSQL")) { // 如果是ORACLE或MYSQL则还要指定Schema名称，SQLSERVSER则不能指定!
			str_schema = ServerEnvironment.getInstance().getDataSourceVO(null).getUser();
		} else if (str_dbtype.equalsIgnoreCase("DB2")) {// 增加DB2数据库的判断【李春娟/2015-12-24】
			str_schema = ServerEnvironment.getInstance().getDataSourceVO(null).getDburl();
			if (str_schema != null) {
				str_schema = str_schema.toLowerCase().trim();
				if (str_schema.contains("currentschema=")) {
					str_schema = str_schema.substring(str_schema.indexOf("currentschema=") + 14);
					if (str_schema.endsWith(";")) {
						str_schema = str_schema.substring(0, str_schema.length() - 1);
					}
				} else {
					str_schema = null;
				}
			}
		}
		DatabaseMetaData dbmd = conn.getMetaData(); // 取得整个数据库的原数据定义!
		String defaultcode = commdmo.getStringValueByDS(null, "select parvalue from pub_option where parkey='seq_prefix'");// 主键是否有前缀，科工项目中用到
		for (String[] table : str_allTables) {
			String tabName = table[0]; // 表名
			ResultSet resultset = dbmd.getPrimaryKeys(null, (str_schema == null ? null : str_schema.toUpperCase()), tabName);
			if (!resultset.next()) {
				continue;
			}
			String pkName = (String) resultset.getObject(4); // 主键字段名
			if (pkName == null || pkName.trim().equals("")) {
				continue;
			}
			String maxID = null; //
			if (defaultcode == null || "".equals(defaultcode.trim())) {
				maxID = commdmo.getStringValueByDS(null, "select max(" + pkName + ") from " + tabName); //
			} else {
				maxID = commdmo.getStringValueByDS(null, "select max(" + pkName + ") from " + tabName + " where " + pkName + " like '" + defaultcode + "%'"); //
				if (maxID == null || maxID.trim().equals("")) {
					continue;
				}
				maxID = maxID.substring(defaultcode.length());
			}

			if (maxID != null && !maxID.trim().equals("")) { // 如果最大值不为空,并且只能是数字
				char[] charofs = maxID.toCharArray();// 查看字符串的每个字符是否是数字
				if ("-0123456789.".indexOf(charofs[0]) < 0) {// 考虑到有时候主键是负值【李春娟/2012-09-25】
					continue;
				}
				boolean isNum = true;
				for (int i = 1; i < charofs.length; i++) {
					if ("0123456789.".indexOf(charofs[i]) < 0) {
						isNum = false;
						break;
					}
				}
				if (isNum) {
					senameList.add("S_" + tabName.toUpperCase());
					long val = (Long.parseLong(maxID) / 10 + 1) * 10; // 取整
					sqlList.add("insert into pub_sequence (sename,currvalue) values ('S_" + tabName.toUpperCase() + "'," + val + ")"); // 往序列表中插入一条记录!!!
				}
			}
		}
		// 这里不能将表中所有记录删除，因为有的不是根据表中的最大主键值进行更新的，比如上传文件时如果前面有序号，则取s_pub_fileupload，但实际不存在表pub_fileupload，记得在图片注册时也用到了
		// 【李春娟/2012-07-24】
		commdmo.executeUpdateByDS(null, "delete from pub_sequence where sename in(" + new TBUtil().getInCondition(senameList) + ")");
		commdmo.executeBatchByDS(null, sqlList); // //批量插入!
		ret = sqlList.size();// 以前ret = sqlList.size()-1
		// ，不需要减1，故修改之【李春娟/2015-12-25】
		System.out.println("重新生成Sequence, 共修改了pub_sequence表中[" + ret + "]条记录!");
		return ret;
	}

	/***
	 * 【李春娟/2014-02-28】
	 * 重置部分Sequence
	 * @return
	 * @throws Exception
	 */
	public int resetSequence(ArrayList _tablenames) throws Exception {
		int ret = 0;
		ArrayList sqlList = new ArrayList();
		CommDMO commdmo = new CommDMO();
		String[][] str_allTables = commdmo.getAllSysTableAndDescr(null, null, false, true);// 以前是取得tables.xml中的表，各项目中项目表很有可能没有导出tables.xml
		// 或表不全，故需要取数据库中的所有表【李春娟/2012-09-25】
		ArrayList senameList = new ArrayList();
		WLTDBConnection conn = new WLTInitContext().getConn(null); //
		String str_dbtype = ServerEnvironment.getInstance().getDataSourceVO(null).getDbtype(); // 数据库类型
		String str_schema = null;
		if (str_dbtype.equalsIgnoreCase("ORACLE") || str_dbtype.equalsIgnoreCase("MYSQL")) { // 如果是ORACLE或MYSQL则还要指定Schema名称，SQLSERVSER则不能指定!
			str_schema = ServerEnvironment.getInstance().getDataSourceVO(null).getUser();
		} else if (str_dbtype.equalsIgnoreCase("DB2")) {// 增加DB2数据的判断【李春娟/2015-12-24】
			str_schema = ServerEnvironment.getInstance().getDataSourceVO(null).getDburl();
			if (str_schema != null) {
				str_schema = str_schema.toLowerCase().trim();
				if (str_schema.contains("currentschema=")) {
					str_schema = str_schema.substring(str_schema.indexOf("currentschema=") + 14);
					if (str_schema.endsWith(";")) {
						str_schema = str_schema.substring(0, str_schema.length() - 1);
					}
				} else {
					str_schema = null;
				}
			}
		}
		DatabaseMetaData dbmd = conn.getMetaData(); // 取得整个数据库的原数据定义!
		String defaultcode = commdmo.getStringValueByDS(null, "select parvalue from pub_option where parkey='seq_prefix'");// 主键是否有前缀，科工项目中用到
		for (String[] table : str_allTables) {
			String tabName = table[0].toUpperCase(); // 表名
			if (!_tablenames.contains(tabName)) {// 只更新需要更新的表sequence【李春娟/2014-02-28】
				continue;
			}
			ResultSet resultset = dbmd.getPrimaryKeys(null, (str_schema == null ? null : str_schema.toUpperCase()), tabName);
			if (!resultset.next()) {
				continue;
			}
			String pkName = (String) resultset.getObject(4); // 主键字段名
			if (pkName == null || pkName.trim().equals("")) {
				continue;
			}
			String maxID = null; //
			if (defaultcode == null || "".equals(defaultcode.trim())) {
				maxID = commdmo.getStringValueByDS(null, "select max(" + pkName + ") from " + tabName); //
			} else {
				maxID = commdmo.getStringValueByDS(null, "select max(" + pkName + ") from " + tabName + " where " + pkName + " like '" + defaultcode + "%'"); //
				if (maxID == null || maxID.trim().equals("")) {
					continue;
				}
				maxID = maxID.substring(defaultcode.length());
			}

			if (maxID != null && !maxID.trim().equals("")) { // 如果最大值不为空,并且只能是数字
				char[] charofs = maxID.toCharArray();// 查看字符串的每个字符是否是数字
				if ("-0123456789.".indexOf(charofs[0]) < 0) {// 考虑到有时候主键是负值【李春娟/2012-09-25】
					continue;
				}
				boolean isNum = true;
				for (int i = 1; i < charofs.length; i++) {
					if ("0123456789.".indexOf(charofs[i]) < 0) {
						isNum = false;
						break;
					}
				}
				if (isNum) {
					senameList.add("S_" + tabName.toUpperCase());
					long val = (Long.parseLong(maxID) / 10 + 1) * 10; // 取整
					sqlList.add("insert into pub_sequence (sename,currvalue) values ('S_" + tabName.toUpperCase() + "'," + val + ")"); // 往序列表中插入一条记录!!!
				}
			}
		}
		// 这里不能将表中所有记录删除，因为有的不是根据表中的最大主键值进行更新的，比如上传文件时如果前面有序号，则取s_pub_fileupload，但实际不存在表pub_fileupload，记得在图片注册时也用到了
		// 【李春娟/2012-07-24】
		commdmo.executeUpdateByDS(null, "delete from pub_sequence where sename in(" + new TBUtil().getInCondition(senameList) + ")");
		commdmo.executeBatchByDS(null, sqlList); // //批量插入!
		ret = sqlList.size();// 以前ret = sqlList.size()-1
		// ，不需要减1，故修改之【李春娟/2015-12-25】
		System.out.println("重新生成Sequence, 共修改了pub_sequence表中[" + ret + "]条记录!");
		return ret;

	}

	/*↓↓↓↓↓↓↓↓↓↓↓↓↓↓新的安装模块远程调用方法↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓*/
	public HashVO[] getAllInstallModuleStatus() throws Exception {
		return new QuickInstallDMO().getAllInstallModuleStatus();
	}

	public String installOrUpdateOperateModule(HashVO _install_updateConfig, String _operateType) throws Exception {
		return new QuickInstallDMO().installOrUpdateOperateAction(_install_updateConfig, _operateType);
	}

	public List getInstallOrUpdateSchedule() throws Exception {
		return QuickInstallDMO.getCurrSchedule();
	}

	public void refreshModuleOn_OffByIds(List _onoffids) throws Exception {
		new QuickInstallDMO().refreshModuleOn_OffByIds(_onoffids);
	}

	/*↑↑↑↑↑↑↑↑↑↑↑↑↑新的安装模块远程调用方法↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑*/

	// 获取首页提醒数据 【杨科/2013-06-05】
	public ArrayList getRemindDatas(String _loginUserId) throws Exception {
		ArrayList al = new ArrayList();
		if (SystemOptions.getBooleanValue("首页新任务是否动态提醒", false)) {
			if (getTaskCount(_loginUserId) > 0) {
				al.add("您有新任务!  ");
			}
		}

		String classname = SystemOptions.getStringValue("首页动态提醒自定义类", "");
		if (!classname.equals("")) {
			// 实现cn.com.infostrategy.bs.sysapp.login.RemindIfc接口 【杨科/2013-06-05】
			RemindIfc ri = (RemindIfc) Class.forName(classname).newInstance();
			ArrayList al_ = ri.getRemindDatas(_loginUserId);
			if (al_ != null && al_.size() > 0) {
				for (int i = 0; i < al_.size(); i++) {
					al.add(al_.get(i));
				}
			}
		}

		// 长度变为30,保证不同提醒两秒显示一次, 一分钟后再次请求服务器
		ArrayList al_new = new ArrayList();
		if (al.size() > 0 && al.size() < 30) {
			while (al_new.size() < 30) {
				for (int i = 0; i < al.size(); i++) {
					al_new.add(al.get(i));
				}
			}
		}

		return al_new;
	}

	// 获取代办任务数量 【杨科/2013-06-05】
	private int getTaskCount(String _loginUserId) {
		int count = 0;
		try {
			HashVO[] hvs = getDMO().getHashVoArrayByDS(null, "select count(*) cou from pub_task_deal where (dealuser='" + _loginUserId + "' or accruserid='" + _loginUserId + "')");
			count = hvs[0].getIntegerValue("cou", 0);
		} catch (Exception e) {
		}
		return count;
	}

	public BillCellVO getRolesAndMenuRelation() throws Exception {
		return new RolesAndMenuRelationDMO().getRolesAndMenuRelation();
	}
}
