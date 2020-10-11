package cn.com.infostrategy.bs.sysapp;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import cn.com.infostrategy.bs.common.BSUtil;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.FrameWorkCommServiceImpl;
import cn.com.infostrategy.bs.common.RemoteCallServlet;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.common.SessionFactory;
import cn.com.infostrategy.bs.common.SystemOptions;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.bs.mdata.FalsityThread;
import cn.com.infostrategy.bs.sysapp.install.database.DataBaseUtilDMO;
import cn.com.infostrategy.bs.sysapp.login.LoginServlet;
import cn.com.infostrategy.to.common.CurrSessionVO;
import cn.com.infostrategy.to.common.DESKeyTool;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.Queue;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.wlappletloader.bs.SynchronizerServlet;

/**
 * 查看服务器状态的Servlet,非常有用!!
 * 在有时不能正常登录时,需要使用它来查看服务器端状态! 曾经在民生银行项目中发生过大量用户登录时白屏,没有途径可以查看服务器端状态!!!就突然感觉必须要有这样一个工具!!!
 * 最好有什么办法可以探测到TomCat等Web服务器中的http容器中的线程状态就好了!!!因为有时会因为web容器的线程数超过上限而造成不能访问的!!!曾经就以前是这样的!!
 * 目前都是通过Tomcat的/manager的控制台来查看的!!是不是可以使用JMX来实现这一点??
 * @author xch
 *
 */
public class ServerStateServlet extends HttpServlet {

	private static final long serialVersionUID = 6745827582232019334L;

	@Override
	protected void service(HttpServletRequest _request, HttpServletResponse _response) throws ServletException, IOException {
		WLTInitContext initConText = null;
		try {
			_response.setCharacterEncoding("GBK");
			_response.setContentType("text/html"); //
			StringBuffer sb_html = new StringBuffer(); //
			String str_type = _request.getParameter("type"); //
			String xmlpath = _request.getParameter("xmlPath"); //
			if (xmlpath == null || "".equals(xmlpath)) {
				xmlpath = "/cn/com/infostrategy/bs/sysapp/install/database/tables.xml";
			}
			if (str_type == null) { //显示默认的状态..
				sb_html.append(getHtmlHead());
				sb_html.append(getDefaultStatu(_request)); //
				sb_html.append(getHtmlTail());
			} else {
				initConText = new WLTInitContext(); //
				if (str_type.equals("sysmemory")) { //显示系统内存
					sb_html.append(getHtmlHead());
					sb_html.append(getSysMemory());
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("sysprop")) { //显示系统属性
					sb_html.append(getHtmlHead());
					sb_html.append(getSysProp());
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("sysoption")) { //显示系统参数
					sb_html.append(getHtmlHead());
					sb_html.append(getSysOption()); //查看系统参数!!
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("reloadsysoption")) { //重新加载系统参数
					sb_html.append(getHtmlHead());
					sb_html.append(reloadSysOption()); //
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("reloadCorpCache")) { //重新加载机构缓存【李春娟/2019-01-23】
					sb_html.append(getHtmlHead());
					sb_html.append(reloadCorpCache());
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("reloadUserCache")) { //重新加载人员缓存【李春娟/2019-01-23】
					sb_html.append(getHtmlHead());
					sb_html.append(reloadUserCache());
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("dspoolstate")) { //显示数据库连接池状态
					sb_html.append(getHtmlHead());
					sb_html.append(getDataSourcePoolStatu());
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("servicepoolstate")) { //显示远程服务池状态
					sb_html.append(getHtmlHead());
					sb_html.append(getRemoteCallThreads());
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("destool")) { //DES加密工具!!!
					sb_html.append(getHtmlHead());
					sb_html.append(getDESKeyTool(_request)); //DES加密工具
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("threadmonitormem")) { //线程监控
					sb_html.append(getHtmlHead());
					sb_html.append(getThreadMonitor());
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("threadmonitor")) { //线程监控
					String str_date = _request.getParameter("date"); //
					sb_html.append(getHtmlHead());
					sb_html.append(getThreadMonitor(str_date));
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("threadmonitordetail")) { //线程监控明细
					String str_id = _request.getParameter("id"); //
					String str_isend = _request.getParameter("isend"); //
					sb_html.append(getHtmlHead());
					sb_html.append(getThreadMonitorDetail(str_id, str_isend));
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("remotecallrate")) { //交易成功率统计
					sb_html.append(getHtmlHead());
					sb_html.append(getRemoteCallRate());
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("remotecallrate_fivemin")) { //交易成功率统计
					sb_html.append(getHtmlHead());
					sb_html.append(getRemoteCallRate_Fivemin());
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("remotecallratedetail")) { //交易成功率统计
					sb_html.append(getHtmlHead());
					sb_html.append(getRemoteCallRate_Fivemin_inf(Long.parseLong(_request.getParameter("currtime")), _request.getParameter("monitortype")));
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("deletethreadmonitordetail")) { //线程监控明细
					RemoteCallServlet.revokeThreadHashtable.clear();
					sb_html.append("清除成功！");
				} else if (str_type.equals("serverconsole")) { //查看服务器控制台
					sb_html.append(getHtmlHead());
					if (_request.getParameter("isclear") != null) {
						sb_html.append(getServerConsole(true));
					} else {
						sb_html.append(getServerConsole(false));
					}
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("sessionsql")) { //显示某会话的SQL监控
					sb_html.append(getHtmlHead());
					String str_sid = _request.getParameter("sid"); //
					String str_isclear = _request.getParameter("isclear"); //
					if (str_isclear != null && str_isclear.equals("Y")) {
						sb_html.append(getSessionSQL(str_sid, true));
					} else {
						sb_html.append(getSessionSQL(str_sid, false));
					}
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("onlineuser")) { //显示在线用户
					sb_html.append(getHtmlHead());
					sb_html.append(getOnlineUser());
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("userclickmenuhist")) { //显示用户点击菜单历史
					sb_html.append(getHtmlHead());
					String str_date = null; //
					if (_request.getParameter("date") != null) {
						str_date = _request.getParameter("date"); //
					}
					sb_html.append(getUserClickMenuHist(str_date));
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("loginhist")) { //显示用户访问历史
					sb_html.append(getHtmlHead());
					sb_html.append(getLoginHist());
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("confxml")) { //显示配置文件
					_response.setContentType("text/plain"); //
					sb_html.append(getConfXml());
				} else if (str_type.equals("setexpprint")) { //显示配置文件
					sb_html.append(getHtmlHead());
					String str_setType = _request.getParameter("settype"); //
					if (str_setType != null) {
						if (str_setType.equalsIgnoreCase("Y")) {
							cn.com.infostrategy.bs.common.ServerEnvironment.isPrintExceptionStack = true;
							sb_html.append("设置服务器端TBUtil.printStackTrace()方法打印堆栈为[true]!!<br>");
						} else if (str_setType.equalsIgnoreCase("N")) {
							cn.com.infostrategy.bs.common.ServerEnvironment.isPrintExceptionStack = false;
							sb_html.append("设置服务器端TBUtil.printStackTrace()方法打印堆栈为[false]!!<br>");
						}
					}
					sb_html.append("当前状态是=【" + cn.com.infostrategy.bs.common.ServerEnvironment.isPrintExceptionStack + "】");
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("wltdict")) { //平台数据字典
					sb_html.append(new DataBaseUtilDMO().exportDictAsHtml("平台数据字典", xmlpath)); //
				} else if (str_type.equals("appdict")) { //产品或项目数据字典
					String appName = URLDecoder.decode(_request.getParameter("appName"));
					sb_html.append(new DataBaseUtilDMO().exportDictAsHtml(appName + "数据字典", xmlpath)); //
				} else if (str_type.equals("comparesfj")) { //比较平台表的html
					String datasourcename = _request.getParameter("datasourcename");//
					datasourcename = new String(datasourcename.getBytes("ISO-8859-1"), "GBK"); //转换成中文					
					String howtosort = _request.getParameter("howtosort");
					if ("1".equals(howtosort)) {
						sb_html.append(new DataBaseUtilDMO().exportCompareAsHtml(datasourcename, "平台表比较", xmlpath)); //
					} else {
						sb_html.append(new DataBaseUtilDMO().exportCompareSortByTypeAsHtml(datasourcename, "平台表比较", xmlpath)); //exportDBCompareSortByTypeAsHtml
					}
				} else if (str_type.equals("dbcomparesfj")) {
					String datasourcename = _request.getParameter("datasourcename1");
					String datasourcename2 = _request.getParameter("datasourcename2");
					datasourcename = new String(datasourcename.getBytes("ISO-8859-1"), "GBK"); //转换成中文	
					datasourcename2 = new String(datasourcename2.getBytes("ISO-8859-1"), "GBK"); //转换成中文		
					String howtosort = _request.getParameter("howtosort");
					if ("1".equals(howtosort)) {
						sb_html.append(new DataBaseUtilDMO().exportCompareDBAsHtml("数据源比较", xmlpath, datasourcename2, datasourcename, false)); //
					} else {
						sb_html.append(new DataBaseUtilDMO().exportDBCompareSortByTypeAsHtml("数据源比较", datasourcename2, datasourcename, xmlpath));
					}
				} else if (str_type.equals("dbcomparesfjrevose")) { //比较平台表的html
					String datasourcename = ServerEnvironment.getDefaultDataSourceName();
					String datasourcename2 = ServerEnvironment.getInstance().getDataSourceVOs()[1].getName();
					datasourcename = new String(datasourcename.getBytes("ISO-8859-1"), "GBK"); //转换成中文	
					datasourcename2 = new String(datasourcename2.getBytes("ISO-8859-1"), "GBK"); //转换成中文		
					sb_html.append(new DataBaseUtilDMO().exportCompareDBAsHtml("数据源比较", xmlpath, datasourcename2, datasourcename, true)); //
				} else if (str_type.equals("sortedcomparesfj")) { //比较平台表的html
					String datasourcename = ServerEnvironment.getDefaultDataSourceName();
					datasourcename = new String(datasourcename.getBytes("ISO-8859-1"), "GBK"); //转换成中文	
					sb_html.append(new DataBaseUtilDMO().exportCompareSortByTypeAsHtml(datasourcename, "平台表比较", xmlpath)); //exportDBCompareSortByTypeAsHtml
				} else if (str_type.equals("sortedDBcomparesfj")) { //数据源比较按类型分类html
					String datasourcename = ServerEnvironment.getDefaultDataSourceName();
					String datasourcename2 = ServerEnvironment.getInstance().getDataSourceVOs()[1].getName();
					datasourcename = new String(datasourcename.getBytes("ISO-8859-1"), "GBK"); //转换成中文	
					datasourcename2 = new String(datasourcename2.getBytes("ISO-8859-1"), "GBK"); //转换成中文	
					String ifrevose = _request.getParameter("ifrevose");
					if ("true".equals(ifrevose)) {
						sb_html.append(new DataBaseUtilDMO().exportDBCompareSortByTypeAsHtml("数据源比较", datasourcename2, datasourcename, xmlpath)); //exportDBCompareSortByTypeAsHtml
					} else {
						sb_html.append(new DataBaseUtilDMO().exportDBCompareSortByTypeAsHtml("数据源比较", datasourcename, datasourcename2, xmlpath)); //exportDBCompareSortByTypeAsHtml	
					}
				} else if (str_type.equals("comitsql")) { //比较平台表的html
					String sql = _request.getParameter("sql"); //
					sql = URLDecoder.decode(sql);
					String datasourcename = _request.getParameter("datesourcename"); //
					if (StringUtils.isEmpty(datasourcename)) {
						datasourcename = null;
					} else {
						datasourcename = new String(datasourcename.getBytes("ISO-8859-1"), "GBK"); //转换成中文
					}
					CommDMO commDMO = new CommDMO(); //
					if (StringUtils.isNotEmpty(sql)) {
						String[] allsqls = sql.split(";");
						if (allsqls != null && allsqls.length > 0) {
							for (int jj = 0; jj < allsqls.length; jj++) {
								if (allsqls[jj] != null && !"".equals(allsqls[jj].trim()))
									commDMO.executeBatchByDS(datasourcename, new String[] { allsqls[jj] });
							}
						}
					}
					//sb_html.append("<script>alert(\"执行成功！\");window.close();</script>");
					//"window.close();window.opener.location.reload();
					sb_html.append("执行成功！");
				} else if (str_type.equals("getallcreatesql")) { //比较平台表的html
					String datasourcename = _request.getParameter("datasourcename");
					datasourcename = new String(datasourcename.getBytes("ISO-8859-1"), "GBK"); //转换成中文		
					String type = _request.getParameter("dbtype");
					sb_html.append(new DataBaseUtilDMO().getAllCreateSql(datasourcename, type, xmlpath)); //
				}
				/*else if (str_type.equals("proddict")) { //平台数据字典
					sb_html.append(new DataBaseUtilDMO().exportDictAsHtml("PushGRC水平产品数据字典", "/com/pushworld/ipushgrc/bs/install/database/tables.xml")); ////
				}*/
				else if (str_type.equals("createtable")) { //创建表					
					String str_dbtype = _request.getParameter("dbtype"); //
					String str_tabname = _request.getParameter("tabname"); //
					sb_html.append(new DataBaseUtilDMO().exportCreateTableSqlAsHtml(str_dbtype, str_tabname, xmlpath));
				} else if (str_type.equals("compareonetable")) { //创建表					
					String str_tabname = _request.getParameter("tabname"); //
					String datasourcename = _request.getParameter("datasourcename");
					datasourcename = new String(datasourcename.getBytes("ISO-8859-1"), "GBK"); //转换成中文	
					sb_html.append(new DataBaseUtilDMO().exportCompareTableSqlAsHtml(datasourcename, str_tabname, xmlpath));
				} else if (str_type.equals("compareoneview")) { //比较一个视图(xml与数据库)				
					String viewname = _request.getParameter("viewname"); //
					String datasourcename = _request.getParameter("datasourcename");
					datasourcename = new String(datasourcename.getBytes("ISO-8859-1"), "GBK"); //转换成中文	
					String appTitleName = _request.getParameter("appTitleName");
					appTitleName = new String(appTitleName.getBytes("ISO-8859-1"), "GBK"); //转换成中文	
					String viewddl = URLDecoder.decode(_request.getParameter("viewddl"));
					String viewdescr = URLDecoder.decode(_request.getParameter("viewdescr"));
					sb_html.append(new DataBaseUtilDMO().getCompareOneViewHtml(datasourcename, viewname, viewddl, appTitleName, viewdescr));
					//以前执行killme后台服务会退出，故先屏蔽掉【李春娟/2016-06-01】
					//				} else if (str_type.equals("killme")) { //杀掉自己
					//					sb_html.append("<html>\r\n");
					//					sb_html.append("<head>\r\n");
					//					sb_html.append("<META http-equiv=Content-Type content=\"text/html; charset=GBK\">\r\n");
					//					sb_html.append("<script language=\"JavaScript\">\r\n");
					//					sb_html.append("function closeMe(){\r\n");
					//					sb_html.append("setTimeout(\"window.opener=null;window.open('','_self');window.close();\",1500);\r\n"); //两秒钟后关闭自己
					//					sb_html.append("}\r\n");
					//					sb_html.append("</script> \r\n");
					//					sb_html.append("</head>\r\n");
					//					sb_html.append("<body onLoad=\"closeMe();\">\r\n");
					//					sb_html.append("3秒钟后杀掉自己!!!\r\n");
					//					sb_html.append("</body>\r\n");
					//					sb_html.append("</html>\r\n");
					//					new KillMeThread().start(); //新开一个线程
				} else if (str_type.equals("reference")) { //显示系统属性
					sb_html.append(getHtmlHead());
					sb_html.append(getReference());
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("pagecountfromcache")) { //显示系统属性
					sb_html.append(getHtmlHead());
					sb_html.append(getPageCountFromCache());
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("pagepkfromcache")) { //
					sb_html.append(getHtmlHead());
					sb_html.append(getPagePkInSQLFromCache());
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("callCount")) { //
					sb_html.append(getHtmlHead());
					sb_html.append(getCallCount());
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("threadConfig")) { //
					sb_html.append(getHtmlHead());
					sb_html.append(getThreadConfig(_request));
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("clearPageCountCache")) { //
					sb_html.append(getHtmlHead());
					sb_html.append("清空之前的内存情况:<br>\r\n");
					sb_html.append(getJVMMemoery(_request)); //
					ServerEnvironment.countSQLCache.clear(); //清空
					sb_html.append("<br><font color=\"blue\">清空Count缓存成功!</font><br><br>\r\n");
					sb_html.append("清空之后的内存情况:<br>");
					sb_html.append(getJVMMemoery(_request)); //
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("clearPagePkValueCache")) { //
					sb_html.append(getHtmlHead());
					sb_html.append("清空之前的内存情况:<br>\r\n");
					sb_html.append(getJVMMemoery(_request)); //
					ServerEnvironment.pagePKValue.clear(); //清空
					sb_html.append("<br><font color=\"blue\">清空主键缓存成功!</font><br><br>\r\n");
					sb_html.append("清空之后的内存情况:<br>\r\n");
					sb_html.append(getJVMMemoery(_request)); //
				} else if (str_type.equals("wltview")) { //平台视图
					sb_html.append(new DataBaseUtilDMO().exportAllXmlViewAsHtml("平台视图", "/cn/com/infostrategy/bs/sysapp/install/database/views.xml")); //将视图导出成Html
				} else if (str_type.equals("appview")) { //产品及项目视图
					String appName = URLDecoder.decode(_request.getParameter("appName"));
					sb_html.append(new DataBaseUtilDMO().exportAllXmlViewAsHtml(appName + "视图", xmlpath));
				} else if (str_type.equals("compareallview")) {
					String datasourcename = URLDecoder.decode(_request.getParameter("datasourcename"));
					sb_html.append(new DataBaseUtilDMO().getCompareAllViewHtml(datasourcename, xmlpath));
				} else if (str_type.equals("createindex")) { //创建索引SQL Gwang 2013-08-27 
					sb_html.append(new DataBaseUtilDMO().exportCreateIndexSqlAsHtml(xmlpath));
				} else {
					sb_html.append(getHtmlHead());
					sb_html.append("没有对应的type【" + str_type + "】"); //显示默认的状态..
					sb_html.append(getHtmlTail());
				}
				initConText.commitAllTrans(); //提交所有连接..
			}
			_response.getWriter().println(sb_html.toString()); //输出状态..
		} catch (Exception ex) {
			if (initConText != null) {
				initConText.rollbackAllTrans(); //回滚所有连接.
			}
			ex.printStackTrace();
			_response.getWriter().println(new TBUtil().getExceptionStringBuffer(ex, true, true)); ///
		} finally { //最终释放..
			if (initConText != null) {
				initConText.closeAllConn(); //关闭所有连接!!至关重要!!!曾经为此造成死机的情况!!
			}
		}
	}

	/**
	 * 默认的显示
	 * @return
	 * @throws Exception
	 */
	private String getDefaultStatu(HttpServletRequest _request) throws Exception {
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append(getJVMMemoery(_request)); //

		sb_html.append("<br><a href=\"./state?type=serverconsole\" target=\"_blank\">查看服务器控制台</a>&nbsp;<a href=\"./state?type=serverconsole&isclear=Y\" target=\"_blank\">查看并清空服务器控制台</a></br>\r\n");
		sb_html
				.append("<br><a href=\"./state?type=sysmemory\" target=\"_blank\">★查看并发线程★</a>&nbsp;&nbsp;<a href=\"./state?type=sysprop\" target=\"_blank\">查看系统属性</a>&nbsp;&nbsp;<a href=\"./state?type=sysoption\" target=\"_blank\">查看系统参数(pub_option)</a>&nbsp;&nbsp;<a href=\"./state?type=reloadsysoption\" target=\"_blank\">重新加载系统参数(pub_option)</a>&nbsp;&nbsp;<a href=\"./state?type=reloadCorpCache\" target=\"_blank\">重新加载机构缓存(pub_corp_dept)</a>&nbsp;&nbsp;<a href=\"./state?type=reloadUserCache\" target=\"_blank\">重新加载人员缓存(pub_user)</a></br>\r\n");
		sb_html.append("<br><a href=\"./state?type=threadmonitor&date=" + getCurrTimeByHour() + "\" target=\"_blank\">系统全面监控历史日志</a>&nbsp;<a href=\"./state?type=threadmonitormem\" target=\"_blank\">查看阻塞/超时线程内存记录</a>\r\n<a href=\"./state?type=deletethreadmonitordetail\" target=\"_blank\">清除监控缓存记录</a>&nbsp;&nbsp;\r\n<br>");
		sb_html.append("<br><a href=\"./state?type=remotecallrate\" target=\"_blank\">当天交易成功率</a>&nbsp;<a href=\"./state?type=remotecallrate_fivemin\" target=\"_blank\">5分钟内交易成功率</a>&nbsp;</br>\r\n");
		sb_html.append("<br><a href=\"./state?type=dspoolstate\" target=\"_blank\">查看数据库连接池状态</a>&nbsp;<a href=\"./state?type=servicepoolstate\" target=\"_blank\">查看远程服务连接池状态</a>&nbsp;<a href=\"./state?type=destool\" target=\"_blank\">DES/16进制转换工具</a></br>\r\n"); //
		sb_html.append("<br><a href=\"./state?type=setexpprint\"  target=\"_blank\">查看服务器打印异常堆栈</a>&nbsp;<a href=\"./state?type=setexpprint&settype=Y\"  target=\"_blank\">启用服务器打印异常堆栈</a>&nbsp;<a href=\"./state?type=setexpprint&settype=N\"  target=\"_blank\">禁用服务器打印异常堆栈</a></br>\r\n"); //

		//在线用户与访问历史!
		sb_html.append("<br>");
		sb_html.append("<a href=\"./state?type=onlineuser\" target=\"_blank\">查看在线用户</a>&nbsp;&nbsp;\r\n");
		sb_html.append("<a href=\"./state?type=userclickmenuhist\" target=\"_blank\">查看用户点击菜单历史</a>&nbsp;&nbsp;\r\n");
		sb_html.append("<a href=\"./state?type=loginhist\"  target=\"_blank\">查看曾经访问过的用户</a>&nbsp;&nbsp;\r\n"); //
		sb_html.append("<br>");

		sb_html.append("<br><a href=\"./state?type=confxml\"  target=\"_blank\">查看weblight.xml配置文件</a></br>\r\n"); //

		//数据库表!!! 这里应该是动态输出,即平台永远是第一个固定输出,产品与项目应该根据weblight.xml中的参数[INSTALLAPPS]来动态输出
		sb_html.append("<br>");
		sb_html.append("<a href=\"./state?type=wltdict\"  target=\"_blank\">查看平台数据字典</a>&nbsp;&nbsp;&nbsp;&nbsp;");

		List<HashMap<String, String>> appList = getInstalledAppList();
		for (int i = 0; (appList != null) && (i < appList.size()); i++) {
			HashMap<String, String> app = appList.get(i);
			sb_html.append("<a href=\"./state?type=appdict&&appName=" + URLEncoder.encode(URLEncoder.encode(app.get("appName"))) + "&&xmlPath=" + app.get("tableXmlLocation") + "\"  target=\"_blank\">查看[" + app.get("appName") + "]数据字典</a>&nbsp;&nbsp;");
		}
		sb_html.append("<br>");

		//数据库视图!!! 这里应该是动态输出,即平台永远是第一个固定输出,产品与项目应该根据weblight.xml中的参数[INSTALLAPPS]来动态输出
		sb_html.append("<br>");
		sb_html.append("<a href=\"./state?type=wltview\"  target=\"_blank\">查看平台视图</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		for (int i = 0; (appList != null) && (i < appList.size()); i++) {
			HashMap<String, String> app = appList.get(i);
			sb_html.append("<a href=\"./state?type=appview&&appName=" + URLEncoder.encode(URLEncoder.encode(app.get("appName"))) + "&&xmlPath=" + app.get("viewXmlLocation") + "\"  target=\"_blank\">查看[" + app.get("appName") + "]视图</a>&nbsp;&nbsp;");
		}

		sb_html.append("<br>");

		//其他监控工具!!
		sb_html.append("<br>");
		sb_html.append("<a href=\"./state?type=pagecountfromcache\"  target=\"_blank\">分页之Count结果总数缓存</a>&nbsp;");
		sb_html.append("<a href=\"./state?type=pagepkfromcache\"  target=\"_blank\">分页之首页记录主键缓存</a>&nbsp;");
		sb_html.append("<a href=\"./state?type=callCount\"  target=\"_blank\">线程查看</a>&nbsp;");
		sb_html.append("<a href=\"./state?type=threadConfig\"  target=\"_blank\">线程配置</a>");
		sb_html.append("<br>\r\n"); //

		sb_html.append("<br>");
		sb_html.append("<a href=\"./state?type=reference\"  target=\"_blank\">关键指导</a>&nbsp;&nbsp;");
		sb_html.append("<br>");

		sb_html.append("<br><a href=\"./WebCallServlet?StrParCallClassName=cn.com.infostrategy.bs.sysapp.help.HelpWebCallBean\"  target=\"_blank\">帮助中心</a></br>\r\n"); //

		return sb_html.toString(); //
	}

	//取得JVM内存情况!!!!
	private String getJVMMemoery(HttpServletRequest _request) {
		String str_contextpath = _request.getContextPath(); //
		if (!str_contextpath.startsWith("/")) {
			str_contextpath = "/" + str_contextpath; //
		}
		StringBuffer sb_html = new StringBuffer(); //
		long ll_free = Runtime.getRuntime().freeMemory() / 1024; //
		long ll_total = Runtime.getRuntime().totalMemory() / 1024; //
		long ll_max = Runtime.getRuntime().maxMemory() / 1024; //
		long ll_busy = ll_total - ll_free; //
		sb_html.append("正忙的内存空间:" + ll_busy + "K/" + (ll_busy / 1024) + "M<br>\r\n");
		sb_html.append("空闲的内存空间:" + ll_free + "K/" + (ll_free / 1024) + "M<br>\r\n");
		sb_html.append("JVM已用内存空间:" + ll_total + "K/" + (ll_total / 1024) + "M(=正忙+空间)<br>\r\n");
		sb_html.append("JVM内存可用上限:" + ll_max + "K/" + (ll_max / 1024) + "M(已用数大于该数就会内存溢出)<br>\r\n");
		sb_html.append("RemoteCallServlet  当前并发数【" + RemoteCallServlet.THREADCOUNT + "】,曾经最大并发数【" + RemoteCallServlet.MAXTHREADCOUNT + "】,总共访问数【" + RemoteCallServlet.TOTALTHREADCOUNT + "】<br>\r\n"); //
		sb_html.append("SynchronizerServlet当前并发数【" + SynchronizerServlet.THREADCOUNT + "】,曾经最大并发数【" + SynchronizerServlet.MAXTHREADCOUNT + "】,总共访问数【" + SynchronizerServlet.TOTALTHREADCOUNT + "】<br>\r\n");

		sb_html.append("集群服务情况:\r\n<br>"); //
		HashMap clusterMap = LoginServlet.clusterMap;
		String[] str_keys = (String[]) clusterMap.keySet().toArray(new String[0]); //
		for (int i = 0; i < str_keys.length; i++) {
			String str_itemurl = "http://" + str_keys[i] + str_contextpath + "/state";
			sb_html.append("【" + str_keys[i] + "】=【" + clusterMap.get(str_keys[i]) + "】&nbsp;&nbsp;<a href=\"" + str_itemurl + "\" target=\"_blank\">查看其状态[" + str_itemurl + "]<br>\r\n"); //
		}
		sb_html.append("<br>\r\n"); //
		return sb_html.toString(); //
	}

	private String getSysMemory() {
		System.gc(); //先释放内存

		StringBuffer sb_html = new StringBuffer(); //
		long ll_free = Runtime.getRuntime().freeMemory() / 1024; //
		long ll_total = Runtime.getRuntime().totalMemory() / 1024; //
		long ll_max = Runtime.getRuntime().maxMemory() / 1024; //
		long ll_busy = ll_total - ll_free; //

		sb_html.append("调用System.gc()释放垃圾内存....<br><br>");
		sb_html.append("正忙的内存空间:" + ll_busy + "K/" + (ll_busy / 1024) + "M<br>\r\n"); //
		sb_html.append("空闲的内存空间:" + ll_free + "K/" + (ll_free / 1024) + "M<br>\r\n"); //
		sb_html.append("JVM已用内存空间:" + ll_total + "K/" + (ll_total / 1024) + "M(=正忙+空间)<br>\r\n"); //
		sb_html.append("JVM内存可用上限:" + ll_max + "K/" + (ll_max / 1024) + "M(已用数大于该数就会内存溢出)<br>\r\n"); //
		sb_html.append("RemoteCallServlet  当前并发数【" + RemoteCallServlet.THREADCOUNT + "】,曾经最大并发数【" + RemoteCallServlet.MAXTHREADCOUNT + "】,总共访问数【" + RemoteCallServlet.TOTALTHREADCOUNT + "】<br>\r\n"); //
		sb_html.append("SynchronizerServlet当前并发数【" + SynchronizerServlet.THREADCOUNT + "】,曾经最大并发数【" + SynchronizerServlet.MAXTHREADCOUNT + "】,总共访问数【" + SynchronizerServlet.TOTALTHREADCOUNT + "】<br>\r\n"); //

		ThreadGroup threadGroup = Thread.currentThread().getThreadGroup(); //
		int li_activeGount = threadGroup.activeCount(); //活动数
		int li_groupCount = threadGroup.activeGroupCount(); //
		Thread[] copyThreads = new Thread[li_activeGount + 10]; //
		threadGroup.enumerate(copyThreads); //拷贝活动的线程!
		sb_html.append("当前线程组中活动线程总数【" + li_activeGount + "】,当前线程组中活动组总数【" + li_groupCount + "】"); //
		sb_html.append("所有活动线程:<br>"); //
		for (int i = 0; i < copyThreads.length; i++) {
			if (copyThreads[i] != null) { //
				sb_html.append(copyThreads[i] + ",状态:" + copyThreads[i].getState() + "<br>\r\n"); //
				//				StackTraceElement[] tracks = copyThreads[i].getStackTrace(); //
				//				for (int j = 0; j < tracks.length; j++) { //
				//					sb_html.append("&nbsp;&nbsp;" + tracks[j].getClassName() + "." + tracks[j].getMethodName() + "(" + tracks[j].getLineNumber() + ")<br>\r\n"); //
				//				}
				//				sb_html.append("<br>"); //  
			}
		}
		sb_html.append(getRemoteCallThreads()); //具体的线程清单..
		return sb_html.toString(); //
	}

	//查看系统属性
	private String getSysProp() {
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<br>所有的System.getProperties():<br>"); //
		Properties props = System.getProperties(); //
		String[] str_keys = props.keySet().toArray(new String[0]);
		Arrays.sort(str_keys); //
		for (int i = 0; i < str_keys.length; i++) {
			sb_html.append("[" + str_keys[i] + "]=[" + props.getProperty(str_keys[i]) + "]<br>\r\n"); //
		}

		sb_html.append("<br><br>所有的ServerEnvironment.get(_key):<br>"); //
		String[] str_envkeys = ServerEnvironment.getInstance().getKeys(); //
		Arrays.sort(str_envkeys); //
		for (int i = 0; i < str_envkeys.length; i++) {//以前是str_keys.length，这里写错了
			sb_html.append("[" + str_envkeys[i] + "]=[" + ServerEnvironment.getInstance().get(str_envkeys[i]) + "]<br>\r\n"); //
		}
		return sb_html.toString(); //
	}

	/**
	 * 查看系统参数! 就是从option表中取!!
	 * @return
	 */
	private String getSysOption() {
		StringBuilder sb_html = new StringBuilder(); //
		if (SystemOptions.sysOptions == null) {
			sb_html.append("还没构建系统参数对象!"); //
			return sb_html.toString(); //
		}
		HashMap mapData = SystemOptions.sysOptions.getDataMap(); //
		sb_html.append("在SystemOptions内存对象中的所有参数值是:<br>\r\n"); //
		sb_html.append(getMapDataAsHtmlTable(mapData)); //
		return sb_html.toString(); //
	}

	private String reloadSysOption() {
		StringBuilder sb_html = new StringBuilder(); //
		SystemOptions.getInstance().reLoadDataFromDB(true); //重新加载,如果原来还没构建,则可能会两次取数据库!
		sb_html.append("<font color=\"blue\">成功的重新从表pub_option中加载数据!!</font><br>\r\n");
		sb_html.append("<font color=\"red\">请注意：由于有的参数已到客户端了,所以有的客户端的效果需要重新登录才能看到!但总之是不需要重启服务器了(以前都要重启服务器,烦的很)!</font><br>\r\n");
		sb_html.append("现在最新的SystemOptions内存对象中的所有参数值是:<br>\r\n"); //
		HashMap mapData = SystemOptions.getInstance().getDataMap(); // 
		sb_html.append(getMapDataAsHtmlTable(mapData)); //
		return sb_html.toString(); //
	}

	/**
	 * 加载机构缓存【李春娟/2019-01-23】
	 * @return
	 */
	private String reloadCorpCache() {
		StringBuilder sb_html = new StringBuilder(); //
		ServerCacheDataFactory.getInstance().registeCorpCacheData(); // 重新加载一下缓存!!
		sb_html.append("<font color=\"blue\">重新加载机构缓存成功!!</font><br>\r\n");
		return sb_html.toString(); //
	}

	/**
	 * 加载人员缓存【李春娟/2019-01-23】
	 * @return
	 */
	private String reloadUserCache() {
		StringBuilder sb_html = new StringBuilder(); //
		ServerCacheDataFactory.getInstance().registeUserCacheData(); // 重新加载一下缓存!!
		sb_html.append("<font color=\"blue\">重新加载人员缓存成功!!</font><br>\r\n");
		return sb_html.toString(); //
	}

	private String getMapDataAsHtmlTable(HashMap _map) {
		StringBuilder sb_html = new StringBuilder(); //
		String[] str_keys = (String[]) _map.keySet().toArray(new String[0]); //
		new TBUtil().sortStrs(str_keys); //排序一把!
		sb_html.append("<table>\r\n"); //
		sb_html.append("<tr><td align=\"center\" bgcolor=\"#DDFFFF\">参数名</td><td align=\"center\" bgcolor=\"#DDFFFF\">参数值</td></tr>\r\n"); //
		for (int i = 0; i < str_keys.length; i++) { //
			String str_value = (String) _map.get(str_keys[i]); //
			str_value = (str_value == null ? "" : str_value);
			sb_html.append("<tr><td>" + str_keys[i] + "</td><td>" + str_value + "</td></tr>\r\n"); ////
		}
		sb_html.append("</table>\r\n"); //
		return sb_html.toString(); //
	}

	/**
	 * 取得服务器端控制台信息..
	 * @return
	 */
	private String getServerConsole(boolean _isclean) throws Exception {
		Queue queue = ServerEnvironment.getServerConsoleQueue(); //
		StringBuffer sb_text = new StringBuffer();
		TBUtil tbutil = new TBUtil(); //
		if (_isclean) {
			while (!queue.isEmpty()) {
				String str_text = "" + queue.pop(); //
				if (str_text.startsWith("\r")) {
					str_text = str_text.substring(1, str_text.length()); //
				}
				if (str_text.startsWith("\n")) {
					str_text = str_text.substring(1, str_text.length()); //
				}
				if (str_text.endsWith("\r")) {
					str_text = str_text.substring(0, str_text.length() - 1); //
				}
				if (str_text.endsWith("\n")) {
					str_text = str_text.substring(0, str_text.length() - 1); //
				}
				str_text = tbutil.replaceAll(str_text, "\n", "<br>"); //
				sb_text.append(str_text + "<br>\r\n");
			}
		} else {
			for (int i = 0; i < queue.size(); i++) {
				String str_text = "" + queue.get(i); //
				if (str_text.startsWith("\r")) {
					str_text = str_text.substring(1, str_text.length()); //
				}
				if (str_text.startsWith("\n")) {
					str_text = str_text.substring(1, str_text.length()); //
				}
				if (str_text.endsWith("\r")) {
					str_text = str_text.substring(0, str_text.length() - 1); //
				}
				if (str_text.endsWith("\n")) {
					str_text = str_text.substring(0, str_text.length() - 1); //
				}
				str_text = tbutil.replaceAll(str_text, "\n", "<br>"); //
				sb_text.append(str_text + "<br>\r\n");
			}
		}
		sb_text.append("<br><br>");
		return sb_text.toString();
	}

	private String getSessionSQL(String _sid, boolean _isclear) throws Exception {
		StringBuffer sb_sql = new StringBuffer();
		sb_sql.append("<font color=\"blue\">跟踪监听会话SessionId=[" + _sid + "]的SQL执行记录,取出后是否立即从服务器删除状态是[" + _isclear + "]</font><br><br>\r\n"); //

		Queue queue = (Queue) ServerEnvironment.getSessionSqlListenerMap().get(_sid); //
		if (queue == null) {
			sb_sql.append("<font color=\"red\">[" + new TBUtil().getCurrTime() + "][" + _sid + "]还没有注册SessionSQL监听呢,请先注册监听!!</font>\r\n"); //
			return sb_sql.toString();
		}

		if (queue.isEmpty()) {
			sb_sql.append("<font color=\"#FF80C0\">[" + new TBUtil().getCurrTime() + "][" + _sid + "]没有发现一条数据!!</font>\r\n"); //
			return sb_sql.toString();
		}

		sb_sql.append("<font color=\"green\">[" + new TBUtil().getCurrTime() + "][" + _sid + "]取得如下[" + queue.size() + "]条数据:</font><br><br>\r\n"); //
		if (_isclear) {
			int li_count = 0; //
			while (!queue.empty()) {
				sb_sql.append("[" + (++li_count) + "]" + queue.pop() + ";<br>\r\n"); //
			}
		} else {
			for (int i = 0; i < queue.size(); i++) {
				sb_sql.append("[" + (i + 1) + "]" + queue.get(i) + ";<br>\r\n"); //
			}
		}

		return sb_sql.toString(); //
	}

	/**
	 * 取得数据库连接池状态
	 * @return
	 */
	private String getDataSourcePoolStatu() throws Exception {
		StringBuffer sb_html = new StringBuffer(); //
		FrameWorkCommServiceImpl commService = new FrameWorkCommServiceImpl(); //
		String[][] str_dsnumbers = commService.getDatasourcePoolActiveNumbers(); //
		sb_html.append("<br>数据库连接池状态:<br>\r\n"); //
		sb_html.append("<table align=\"left\">\r\n"); //
		sb_html.append("<tr><td>数据源名称</td><td>数据源说明</td><td>当前活动数</td><td>当前实例数</td><td>最大数</td><td>状态</td></tr>\r\n"); //
		for (int i = 0; i < str_dsnumbers.length; i++) {
			sb_html.append("<tr><td>" + str_dsnumbers[i][0] + "</td><td>" + str_dsnumbers[i][1] + "</td><td>" + str_dsnumbers[i][2] + "</td><td>" + str_dsnumbers[i][3] + "</td><td>" + str_dsnumbers[i][4] + "</td><td>" + str_dsnumbers[i][5] + "</td></tr>\r\n"); //
		}
		sb_html.append("</table>\r\n"); //
		return sb_html.toString(); //
	}

	/**
	 * 返回调用RemoteCall的线程.
	 * @return
	 */
	private String getRemoteCallThreads() {
		StringBuffer sb_html = new StringBuffer(); //
		FrameWorkCommServiceImpl commService = new FrameWorkCommServiceImpl(); //
		Thread[] threads = commService.getAllRemoteCallServletThreads(); //

		sb_html.append("<br><br><br>当前调用RemoteCallServlet的线程数[" + threads.length + "]:<br>\r\n"); //
		sb_html.append("<table align=\"left\">\r\n"); //
		sb_html.append("<tr>"); //
		sb_html.append("<td>线程名</td>"); //
		sb_html.append("<td>线程类名</td>"); //
		sb_html.append("<td>线程组中的总数</td>"); //
		sb_html.append("<td>具体调用参数</td>"); //
		sb_html.append("</tr>"); //
		for (int i = 0; i < threads.length; i++) {
			CurrSessionVO sessionVO = SessionFactory.getInstance().getClientEnv(threads[i]); //取得session信息!
			sb_html.append("<tr>");
			sb_html.append("<td>" + threads[i].getName() + "</td>"); //
			sb_html.append("<td>" + threads[i].getClass().getName() + "</td>"); //
			sb_html.append("<td>" + threads[i].getThreadGroup().activeCount() + "</td>"); //
			sb_html.append("<td>" + (sessionVO == null ? "" : sessionVO.getSessionCallInfo()) + "</td>"); //
			sb_html.append("</tr>");
		}
		sb_html.append("</table><br>\r\n");
		return sb_html.toString(); //
	}

	private String getDESKeyTool(HttpServletRequest _request) {
		StringBuilder sb_html = new StringBuilder(); //
		String str_init = _request.getParameter("initstr"); //
		if (str_init != null) { //如果没有,即第一次打开!!
			String str_encryedPwd = new DESKeyTool().encrypt(str_init); //加密
			sb_html.append("对字符串【<font color=\"blue\">" + str_init + "</font>】进行DES加密的结果是【<font color=\"red\">" + str_encryedPwd + "</font>】<br>\r\n"); //
			String str_hexstr = new TBUtil().convertStrToHexString(str_init); //转换成16进制
			sb_html.append("对字符串【<font color=\"blue\">" + str_init + "</font>】进行16进制转换的结果是【<font color=\"red\">" + str_hexstr + "</font>】<br>\r\n"); //
		}
		sb_html.append("<form method=\"post\" action=\"./state?type=destool\">请输入需要加密的字符串:<input name=\"initstr\" type=\"text\" value=\"" + (str_init == null ? "" : str_init) + "\" size=\"50\">&nbsp;<input type=\"submit\" name=\"Submit\" value=\"提交\"></form>\r\n"); //
		return sb_html.toString(); //
	}

	private String getThreadConfig(HttpServletRequest _request) {
		StringBuilder sb_html = new StringBuilder(); //
		String str_conf = _request.getParameter("confstr"); //如果有
		if (str_conf != null) { //如果没有,即第一次打开!!
			sb_html.append("提交成功!<br>\r\n");
			;
			if (str_conf.toUpperCase().startsWith("Y")) {
				ServerEnvironment.isPageFalsity = true; //
				if (str_conf.length() > 1) {
					try {
						int li_sleep = Integer.parseInt(str_conf.substring(1, str_conf.length())); //可能会失败
						ServerEnvironment.falsitySleep = li_sleep; //循环次数,越大越快也越假!!
					} catch (Exception ex) {
						ex.printStackTrace(); //
					}
				}
			} else {
				ServerEnvironment.isPageFalsity = false; //
			}
		} else {
			sb_html.append("取数或刷新成功!<br>\r\n");
			;
		}
		sb_html.append("是否新开线程=[" + ServerEnvironment.isPageFalsity + "],Sleep的毫秒数=[" + ServerEnvironment.falsitySleep + "]<br>\r\n"); //
		sb_html.append("<form method=\"post\" action=\"./state?type=threadConfig\">重新设置配置(默认值Y300,如果想禁用就设成N):<input name=\"confstr\" type=\"text\" value=\"Y300\" size=\"10\">&nbsp;<input type=\"submit\" name=\"Submit\" value=\"提交\"> <a href=\"./state?type=threadConfig\">刷新</a></form>\r\n"); //
		return sb_html.toString(); //
	}

	/**
	 * 线程监控
	 * @return
	 */
	private String getThreadMonitor() {
		Hashtable ht = RemoteCallServlet.revokeThreadHashtable; //
		String[] str_allKeys = (String[]) ht.keySet().toArray(new String[0]); //
		Arrays.sort(str_allKeys); //
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("这里显示的是内存中(RemoteCallServlet.revokeThreadHashtable)记录的在线线程!!<br>如果线程阻塞则会在这里出现,同时如果访问已结束但超过5秒的也会在这里出现,这些记录会马上被心跳线程删除掉,所以说在没有人访问的情况下这里应该没有记录才是正常的!<br>"); //
		sb_html.append("<table align=\"left\">\r\n"); //
		sb_html.append("<tr>"); //
		sb_html.append("<td>序号</td>"); //
		sb_html.append("<td>线程No.</td>"); //0
		sb_html.append("<td>开始时间</td>"); //1
		sb_html.append("<td>是否反馈</td>"); //2
		sb_html.append("<td>访问人</td>"); //3
		sb_html.append("<td>服务名</td>"); //4
		sb_html.append("<td>方法名</td>"); //5
		sb_html.append("<td>参数值</td>"); //6
		sb_html.append("<td>时长</td>"); //7时长
		sb_html.append("<td>重量</td>"); //8,重量
		sb_html.append("<td>SQLs</td>"); //9SQL
		sb_html.append("<td>起调的功能点</td>"); //10
		sb_html.append("<td>客户端堆栈</td>"); //11

		TBUtil tbUtil = new TBUtil(); //
		sb_html.append("</tr>"); //
		for (int i = 0; i < str_allKeys.length; i++) {
			String[] str_values = (String[]) ht.get(str_allKeys[i]); ////
			sb_html.append("<tr>"); //
			sb_html.append("<td nowrap>" + (i + 1) + "</td>"); //
			sb_html.append("<td nowrap>" + str_values[0] + "</td>"); //
			sb_html.append("<td nowrap>" + str_values[1] + "</td>"); //
			sb_html.append("<td nowrap>" + str_values[2] + "</td>"); //
			sb_html.append("<td nowrap>" + str_values[3] + "</td>"); //
			sb_html.append("<td nowrap>" + str_values[4] + "</td>"); //
			sb_html.append("<td nowrap>" + str_values[5] + "</td>"); //
			sb_html.append("<td nowrap>" + (str_values[6] == null ? "" : tbUtil.replaceAll(str_values[6], "§", "<br>")) + "</td>"); //参数值
			sb_html.append("<td nowrap>" + (str_values[7] == null ? "" : (str_values[7] + "毫秒," + (Integer.parseInt(str_values[7]) / 1000) + "秒")) + "</td>"); //时长
			sb_html.append("<td nowrap>" + (str_values[8] == null ? "" : (str_values[8] + "字节," + (Integer.parseInt(str_values[8]) / 1024) + "K")) + "</td>"); //重量
			sb_html.append("<td nowrap>" + (str_values[9] == null ? "" : tbUtil.replaceAll(str_values[9], "§", "<br>")) + "</td>"); //SQLs
			sb_html.append("<td nowrap>" + str_values[10] + "</td>"); //
			sb_html.append("<td nowrap>" + str_values[11] + "</td>"); //
			sb_html.append("</tr>"); //
		}
		sb_html.append("</table>"); //
		return sb_html.toString(); //
	}

	/**
	 * 交易成功率
	 * @return
	 */
	private String getRemoteCallRate() {
		if (ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE") != null && ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE").equals("Y")) {
		} else {
			return "weblight.xml中未设置ISMONITORREMOTECALLRATE=Y";
		}
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<table align=\"left\">\r\n"); //
		sb_html.append("<tr>"); //
		sb_html.append("<td bgcolor=\"FFCACA\" rowspan=2>序号</td>");
		sb_html.append("<td bgcolor=\"FFCACA\" rowspan=2>时间</td>");
		sb_html.append("<td bgcolor=\"FFCACA\" rowspan=2>类型</td>");
		for (int i = 0; i < 24; i++) {
			sb_html.append("<td colspan=3 align=center bgcolor=\"FFCACA\">" + i + "点</td>");
		}
		sb_html.append("</tr>");
		sb_html.append("<tr>");
		for (int i = 0; i < 24; i++) {
			sb_html.append("<td align=center bgcolor=\"FFCACA\">成<br>功<br>次<br>数</td>");
			sb_html.append("<td align=center bgcolor=\"FFCACA\">失<br>败<br>次<br>数</td>");
			sb_html.append("<td align=center bgcolor=\"FFCACA\">成<br>功<br>率</td>");
		}
		sb_html.append("</tr>");
		try {
			CommDMO dmo = new CommDMO();
			HashVO[] hvs = dmo.getHashVoArrayByDS(null, "select * from pub_remotecallsucrate order by daystr desc ");
			HashMap day_count = dmo.getHashMapBySQLByDS(null, "select daystr,count(*) from pub_remotecallsucrate group by daystr ");
			HashMap havespan = new HashMap();
			TBUtil.getTBUtil().sortHashVOs(hvs, new String[][] { { "daystr", "Y", "N" }, { "monitortype", "Y", "N" } });
			if (hvs == null || hvs.length <= 0) {
				sb_html.append("</table>");
				return sb_html.toString();
			}
			TBUtil tbUtil = new TBUtil();
			String coo = null;
			for (int i = 0; i < hvs.length; i++) {
				sb_html.append("<tr>"); //
				sb_html.append("<td bgcolor=\"FFCACA\" nowrap >" + (i + 1) + "</td>");
				if (day_count.containsKey(hvs[i].getStringValue("daystr"))) {
					if (!havespan.containsKey(hvs[i].getStringValue("daystr"))) {
						sb_html.append("<td bgcolor=\"FFCACA\" nowrap rowspan=" + day_count.get(hvs[i].getStringValue("daystr")) + ">" + hvs[i].getStringValue("daystr") + "</td>");
						havespan.put(hvs[i].getStringValue("daystr"), i);
					} else {
					}
				} else {
					sb_html.append("<td bgcolor=\"FFCACA\" nowrap >" + hvs[i].getStringValue("daystr") + "</td>");
				}
				sb_html.append("<td bgcolor=\"FFCACA\" nowrap>" + hvs[i].getStringValue("monitortype", "") + "</td>");
				for (int o = 0; o < 24; o++) {
					coo = (o < 10 ? "0" + o : o + "");
					sb_html.append("<td nowrap><font color=\"blue\">" + hvs[i].getStringValue("sc" + coo, "0") + "</font></td>"); //
					sb_html.append("<td nowrap><font color=\"red\">" + hvs[i].getStringValue("fc" + coo, "0") + "</font></td>"); //
					int rate = 0;
					if (hvs[i].getIntegerValue("sc" + coo, 0) == 0) {
						if (hvs[i].getIntegerValue("sc" + coo, 0) + hvs[i].getIntegerValue("fc" + coo, 0) == 0) {
							rate = 100;
						}
					} else {
						rate = hvs[i].getIntegerValue("sc" + coo, 0) * 100 / (hvs[i].getIntegerValue("sc" + coo, 0) + hvs[i].getIntegerValue("fc" + coo, 0));
					}
					sb_html.append("<td nowrap><font color=\"" + (rate < 90 ? "red" : "blue") + "\">" + rate + "%</font></td>"); //
				}
				sb_html.append("</tr>");
			}
		} catch (Exception e) {
			sb_html.append("</table>");
			e.printStackTrace();
			return sb_html.toString();
		}
		sb_html.append("</table>");
		return sb_html.toString();
	}

	/**
	 * 五分钟内的交易成功率
	 * @return
	 */
	public String getRemoteCallRate_Fivemin() {
		if (ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE") != null && ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE").equals("Y")) {
		} else {
			return "weblight.xml中未设置ISMONITORREMOTECALLRATE=Y";
		}
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<table align=\"left\">\r\n"); //
		sb_html.append("<tr>"); //
		sb_html.append("<td align=center bgcolor=\"FFCACA\">序号</td>");
		sb_html.append("<td align=center bgcolor=\"FFCACA\">采集时间</td>");
		sb_html.append("<td align=center bgcolor=\"FFCACA\">类型</td>");
		sb_html.append("<td align=center bgcolor=\"FFCACA\">成功次数</td>");
		sb_html.append("<td align=center bgcolor=\"FFCACA\">失败次数</td>");
		sb_html.append("<td align=center bgcolor=\"FFCACA\">交易成功率</td>");
		sb_html.append("<td align=center bgcolor=\"FFCACA\">查看详情</td>");
		sb_html.append("</tr>"); //
		try {
			long ll_currtime = System.currentTimeMillis();
			long li_fiveminbefor = ll_currtime - 300 * 1000;
			TBUtil tbUtil = new TBUtil();
			HashVO[] vos = new CommDMO().getHashVoArrayByDS(null, "select * from pub_threadmonitor_b where iscallend='Y' and begintime <= '" + tbUtil.getTimeByLongValue(ll_currtime) + "' and begintime >= '" + tbUtil.getTimeByLongValue(li_fiveminbefor) + "' ");
			if (vos == null || vos.length <= 0) {
				sb_html.append("</table>");
				return sb_html.toString();
			}
			HashMap<String, Integer[]> map = new HashMap<String, Integer[]>();
			String key = null;
			for (int i = 0; i < vos.length; i++) {
				key = vos[i].getStringValue("monitortype", "首页");
				if (map.containsKey(key)) {
					if ("Y".equals(vos[i].getStringValue("issucess"))) {
						map.put(key, new Integer[] { map.get(key)[0] + 1, map.get(key)[1] });
					} else {
						map.put(key, new Integer[] { map.get(key)[0], map.get(key)[1] + 1 });
					}
				} else {
					if ("Y".equals(vos[i].getStringValue("issucess"))) {
						map.put(key, new Integer[] { 1, 0 });
					} else {
						map.put(key, new Integer[] { 0, 1 });
					}
				}
			}
			String[] keys = (String[]) map.keySet().toArray(new String[0]);
			int fc = 0;
			int sc = 0;
			for (int j = 0; j < map.size(); j++) {
				fc = map.get(keys[j])[1];
				sc = map.get(keys[j])[0];
				sb_html.append("<tr>");
				sb_html.append("<td nowrap><font color=\"blue\">" + (j + 1) + "</font></td>");
				if (j == 0) {
					sb_html.append("<td nowrap rowspan=" + map.size() + "><font color=\"blue\">从" + tbUtil.getTimeByLongValue(li_fiveminbefor) + "到" + tbUtil.getTimeByLongValue(ll_currtime) + "</font></td>");
				}
				sb_html.append("<td nowrap><font color=\"blue\">" + keys[j] + "</font></td>");
				sb_html.append("<td nowrap><font color=\"blue\">" + sc + "</font></td>");
				sb_html.append("<td nowrap><font color=\"red\">" + fc + "</font></td>");
				int rate = 0;
				if (sc == 0) {
					if (sc + fc == 0) {
						rate = 100;
					}
				} else {
					rate = sc * 100 / (sc + fc);
				}
				sb_html.append("<td nowrap><font color=\"" + (rate < 90 ? "red" : "blue") + "\">" + rate + "%</font></td>");
				sb_html.append("<td nowrap><a href =\"./state?type=remotecallratedetail&currtime=" + ll_currtime + "&monitortype=" + tbUtil.convertStrToHexString(keys[j]) + "\" target=\"_blank\">查看详情</a></td>");
				sb_html.append("</tr>");
			}

		} catch (Exception e) {
			sb_html.append("</table>");
			e.printStackTrace();
			return sb_html.toString();
		}
		sb_html.append("</table>");
		return sb_html.toString();
	}

	private String getRemoteCallRate_Fivemin_inf(long ll_currtime, String monitortype) {
		if (ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE") != null && ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE").equals("Y")) {
		} else {
			return "weblight.xml中未设置ISMONITORREMOTECALLRATE=Y";
		}
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<table align=\"left\">\r\n"); //
		sb_html.append("<tr>"); //
		sb_html.append("<td align=center bgcolor=\"FFCACA\">序号</td>");
		sb_html.append("<td align=center bgcolor=\"FFCACA\">操作人</td>");
		sb_html.append("<td align=center bgcolor=\"FFCACA\">操作时间</td>");
		sb_html.append("<td align=center bgcolor=\"FFCACA\">访问功能</td>");
		sb_html.append("<td align=center bgcolor=\"FFCACA\">调用服务</td>");
		sb_html.append("<td align=center bgcolor=\"FFCACA\">调用方法</td>");
		sb_html.append("<td align=center bgcolor=\"FFCACA\">是否成功</td>");
		sb_html.append("<td align=center bgcolor=\"FFCACA\">异常信息</td>");
		sb_html.append("</tr>"); //
		try {
			long li_fiveminbefor = ll_currtime - 300 * 1000;
			TBUtil tbUtil = new TBUtil();
			StringBuffer sb = new StringBuffer("select * from pub_threadmonitor_b where iscallend='Y' and begintime <= '" + tbUtil.getTimeByLongValue(ll_currtime) + "' and begintime >= '" + tbUtil.getTimeByLongValue(li_fiveminbefor) + "' ");
			if (monitortype != null) {
				sb.append(" and monitortype='" + tbUtil.convertHexStringToStr(monitortype) + "' ");
			}
			sb.append(" order by begintime desc ");
			HashVO[] vos = new CommDMO().getHashVoArrayByDS(null, sb.toString());
			if (vos == null || vos.length <= 0) {
				sb_html.append("</table>");
				return sb_html.toString();
			}
			for (int i = 0; i < vos.length; i++) {
				sb_html.append("<tr>"); //
				sb_html.append("<td nowrap><font color=\"blue\">" + (i + 1) + "</td>");
				sb_html.append("<td nowrap><font color=\"blue\">" + vos[i].getStringValue("calluser", "空") + "</font></td>");
				sb_html.append("<td nowrap><font color=\"blue\">" + vos[i].getStringValue("begintime", "空") + "</font></td>");
				sb_html.append("<td nowrap><font color=\"blue\">" + vos[i].getStringValue("monitortype", "空") + "</font></td>");
				sb_html.append("<td nowrap><font color=\"blue\">" + vos[i].getStringValue("servicename", "空") + "</font></td>");
				sb_html.append("<td nowrap><font color=\"blue\">" + vos[i].getStringValue("methodname", "空") + "</font></td>");
				sb_html.append("<td nowrap><font color=\"blue\">" + vos[i].getStringValue("issucess", "空") + "</font></td>");
				sb_html.append("<td nowrap><font color=\"red\">" + vos[i].getStringValue("errormsg", "空").replaceAll("\n", "<br>") + "</font></td>");
				sb_html.append("</tr>");
			}
		} catch (Exception e) {
			sb_html.append("</table>");
			e.printStackTrace();
			return sb_html.toString();
		}
		sb_html.append("</table>");
		return sb_html.toString();
	}

	/**
	 * 根据某个时间去数据库存中取某天数据!!
	 * @param _date
	 * @return
	 */
	private String getThreadMonitor(String _date) throws Exception {
		if (_date == null) {
			return "必须定义date参数!";
		}
		if (_date.length() == 10) { //如果是天,则默认用8点
			_date = _date + " 08"; //
		}

		String str_day = _date.substring(0, 10); //天
		TBUtil tbUtil = new TBUtil(); ////
		String str_cuurrdate = tbUtil.getCurrDate(); //今天
		long ll_currdate = tbUtil.parseDateToLongValue(str_cuurrdate); //今天..
		String str_beforeDate_1 = tbUtil.formatDateToStr(ll_currdate - 1 * 24 * 60 * 60 * 1000); //
		String str_beforeDate_2 = tbUtil.formatDateToStr(ll_currdate - 2 * 24 * 60 * 60 * 1000); //

		StringBuffer sb_html = new StringBuffer(); //
		if (str_day.equals(str_beforeDate_2)) {
			sb_html.append("前天[<font color=\"green\">" + str_beforeDate_2 + "</font>]&nbsp;&nbsp;"); //
		} else {
			sb_html.append("前天[<a href=\"./state?type=threadmonitor&date=" + str_beforeDate_2 + "\"><font color=\"blue\">" + str_beforeDate_2 + "</font></a>]&nbsp;&nbsp;"); //
		}
		if (str_day.equals(str_beforeDate_1)) {
			sb_html.append("昨天[<font color=\"green\">" + str_beforeDate_1 + "</font>]&nbsp;&nbsp;"); //
		} else {
			sb_html.append("昨天[<a href=\"./state?type=threadmonitor&date=" + str_beforeDate_1 + "\"><font color=\"blue\">" + str_beforeDate_1 + "</font></a>]&nbsp;&nbsp;"); //
		}
		if (str_day.equals(str_cuurrdate)) {
			sb_html.append("今天[<font color=\"green\">" + str_cuurrdate + "</font>]&nbsp;&nbsp;"); //
		} else {
			sb_html.append("今天[<a href=\"./state?type=threadmonitor&date=" + str_cuurrdate + "\"><font color=\"blue\">" + str_cuurrdate + "</font></a>]&nbsp;&nbsp;"); //
		}
		sb_html.append("<br><br>\r\n");
		sb_html.append("<table width=\"60%\" cellpadding=\"3\" cellspacing=\"0\">\r\n"); //
		sb_html.append("<tr>\r\n");
		for (int i = 8; i <= 14; i++) {
			String str_hour = ("" + (100 + i)).substring(1, 3); //
			String str_day_hour = str_day + " " + str_hour; //
			if (str_day_hour.equals(_date)) {
				sb_html.append("<td align=\"center\"><font color=\"green\">" + str_day + "&nbsp;" + str_hour + "</font></td>"); //
			} else {
				sb_html.append("<td align=\"center\"><a href=\"./state?type=threadmonitor&date=" + str_day_hour + "\"><font color=\"blue\">" + str_day + "&nbsp;" + str_hour + "</font></a></td>"); //
			}
		}
		sb_html.append("</tr>");

		sb_html.append("<tr>");
		for (int i = 15; i <= 21; i++) {
			String str_hour = ("" + (100 + i)).substring(1, 3); //
			String str_day_hour = str_day + " " + str_hour; //
			if (str_day_hour.equals(_date)) {
				sb_html.append("<td align=\"center\"><font color=\"green\">" + str_day + "&nbsp;" + str_hour + "</font></td>"); //
			} else {
				sb_html.append("<td align=\"center\"><a href=\"./state?type=threadmonitor&date=" + str_day_hour + "\"><font color=\"blue\">" + str_day + "&nbsp;" + str_hour + "</font></a></td>"); //
			}
		}
		sb_html.append("</tr>");
		sb_html.append("</table>\r\n"); //

		sb_html.append("<br>时间段【" + _date + "】中的日志(如果发生大量超时或阻塞的线程,则说明代码有问题,如果并无太多超时线程但并发量依然很大,这说明需要做集群或调整服务器参数或尽可能减少远程访问次数):<br>\r\n");
		String str_sql = null;
		if (ServerEnvironment.getProperty("MYSELFPORT") != null) { //如果有端口号,则只过滤本机的!以后可以考虑搞一个超连接,然后可以同时看其他机器的!!!
			str_sql = "select * from pub_threadmonitor where clusterport='" + ServerEnvironment.getProperty("MYSELFPORT") + "' and monitortime like '" + _date + "%' order by serverstarttime desc,monitortime desc";
		} else {
			str_sql = "select * from pub_threadmonitor where monitortime like '" + _date + "%' order by serverstarttime desc,monitortime desc";
		}
		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, str_sql); //
		sb_html.append("<table align=\"left\">\r\n"); //
		sb_html.append("<tr>"); //
		sb_html.append("<td bgcolor=\"E1FFE1\" nowrap>序号</td>"); //
		sb_html.append("<td bgcolor=\"E1FFE1\" align=\"center\" width=40 nowrap>端口号</td>"); //
		sb_html.append("<td bgcolor=\"E1FFE1\" align=\"center\" width=125 nowrap>监控时间</td>"); //
		sb_html.append("<td bgcolor=\"E1FFE1\" align=\"center\" width=110 nowrap>服务器启动时间</td>"); //
		sb_html.append("<td bgcolor=\"B3E7FF\" align=\"center\" nowrap>正忙<br>JVM(M)</td>"); //
		sb_html.append("<td bgcolor=\"B3E7FF\" align=\"center\" nowrap>空闲<br>虚拟机(M)</td>"); //
		sb_html.append("<td bgcolor=\"B3E7FF\" align=\"center\" nowrap>已开销<br>虚拟机(M)</td>"); //
		sb_html.append("<td bgcolor=\"B3E7FF\" align=\"center\" nowrap>曾经开销的<br>最大虚拟机(M)</td>"); //
		sb_html.append("<td bgcolor=\"00CCCC\" align=\"center\" nowrap>在线<br>活动用户</td>"); //
		sb_html.append("<td bgcolor=\"00CCCC\" align=\"center\" nowrap>在线<br>全部用户</td>");
		sb_html.append("<td bgcolor=\"00CCCC\" align=\"center\" nowrap>曾经最多的<br>在线用户</td>");
		sb_html.append("<td bgcolor=\"FFCACA\" align=\"center\" nowrap>同步线程<br>总计</td>");
		sb_html.append("<td bgcolor=\"FFCACA\" align=\"center\" nowrap>同步线程<br>当前并发</td>"); //
		sb_html.append("<td bgcolor=\"FFCACA\" align=\"center\" nowrap>同步线程<br>最大并发</td>"); //
		sb_html.append("<td bgcolor=\"FFCACA\" align=\"center\" nowrap>访问线程<br>总计</td>"); //
		sb_html.append("<td bgcolor=\"FFCACA\" align=\"center\" nowrap>访问线程<br>当前并发</td>"); //
		sb_html.append("<td bgcolor=\"FFCACA\" align=\"center\" nowrap>访问线程<br>最大并发</td>"); //
		sb_html.append("<td bgcolor=\"FFCACA\" align=\"center\" nowrap>阻塞线程数<br>超过" + (WLTConstants.THREAD_OVERTIME_VALUE / 1000) + "秒<br>还未反应</td>"); //
		sb_html.append("<td bgcolor=\"FFCACA\" align=\"center\" nowrap>超时线程数<br>反应时长<br>超过" + (WLTConstants.THREAD_OVERTIME_VALUE / 1000) + "秒</td>"); //
		sb_html.append("<td bgcolor=\"FFCACA\" align=\"center\" nowrap>超重线程数<br>(大于" + (WLTConstants.THREAD_OVERWEIGHT_VALUE / 1024) + "K)</td>"); //
		sb_html.append("<td bgcolor=\"FFCACA\" align=\"center\" nowrap>JVM增长过大<br>线程数<br>(大于" + WLTConstants.THREAD_OVERJVM_VALUE + "K)</td>"); //
		sb_html.append("<td bgcolor=\"E8D0D0\" align=\"center\" nowrap>数据库连接池<br>正忙实例数</td>"); //
		sb_html.append("<td bgcolor=\"E8D0D0\" align=\"center\" nowrap>数据库连接池<br>共有实例数</td>"); //
		sb_html.append("<td bgcolor=\"E8D0D0\" align=\"center\" nowrap>数据库连接池<br>曾经最大忙碌数</td>"); //
		sb_html.append("<td bgcolor=\"E8D0D0\" align=\"center\" nowrap>数据库连接池<br>曾经最大实例数</td>"); //
		sb_html.append("</tr>"); //

		for (int i = 0; i < hvs.length; i++) {
			sb_html.append("<tr>\r\n"); //
			sb_html.append("<td>" + (i + 1) + "</td>"); //
			sb_html.append("<td align=\"center\" nowrap>" + hvs[i].getStringValue("clusterport", "") + "</td>"); //
			sb_html.append("<td align=\"center\" nowrap>" + hvs[i].getStringValue("monitortime") + "</td>"); //
			sb_html.append("<td align=\"center\" nowrap>" + hvs[i].getStringValue("serverstarttime") + "</td>"); //服务器启动时间!!!
			sb_html.append("<td align=\"center\" nowrap bgcolor='" + getColorByCount(hvs[i].getLognValue("jvmbusy", 0), 200) + "'>" + hvs[i].getStringValue("jvmbusy", "") + "</td>"); //忙的虚拟机,超过200M就告警..
			sb_html.append("<td align=\"center\" nowrap>" + hvs[i].getStringValue("jvmfree", "") + "</td>"); //空闲的虚拟机
			sb_html.append("<td align=\"center\" nowrap bgcolor='" + getColorByCount(hvs[i].getLognValue("jvmtotal", 0), 500) + "'>" + hvs[i].getStringValue("jvmtotal", "") + "</td>"); //已开销的虚拟机,超过500M就告警
			sb_html.append("<td align=\"center\" nowrap bgcolor='" + getColorByCount(hvs[i].getLognValue("evermaxjvmtotal", 0), 800) + "'>" + hvs[i].getStringValue("evermaxjvmtotal", "") + "</td>"); //曾经开销的最大内存,超过800M就警告!!!

			sb_html.append("<td align=\"center\" nowrap bgcolor='" + getColorByCount(hvs[i].getLognValue("onlineuserbusys", 0), 100) + "'>" + hvs[i].getStringValue("onlineuserbusys", "") + "</td>"); //在线用户,活动/全部
			sb_html.append("<td align=\"center\" nowrap bgcolor='" + getColorByCount(hvs[i].getLognValue("onlineusertotals", 0), 100) + "'>" + hvs[i].getStringValue("onlineusertotals", "") + "</td>"); //在线用户,活动/全部
			sb_html.append("<td align=\"center\" nowrap bgcolor='" + getColorByCount(hvs[i].getLognValue("evermaxonlineusers", 0), 100) + "'>" + hvs[i].getStringValue("evermaxonlineusers", "") + "</td>"); //曾经最多的在线用户数!!!

			sb_html.append("<td align=\"center\" nowrap>" + hvs[i].getStringValue("syncthreadtotals", "") + "</td>"); //同步线程线程总计!!!
			sb_html.append("<td align=\"center\" nowrap bgcolor='" + getColorByCount(hvs[i].getLognValue("syncthreadcurrs"), 30) + "'>" + hvs[i].getLognValue("syncthreadcurrs") + "</td>"); //
			sb_html.append("<td align=\"center\" nowrap bgcolor='" + getColorByCount(hvs[i].getLognValue("syncthreadmaxs"), 30) + "'>" + hvs[i].getLognValue("syncthreadmaxs") + "</td>"); //

			sb_html.append("<td align=\"center\" nowrap>" + hvs[i].getStringValue("callthreadtotals", "") + "</td>"); //访问线程总计!!!
			sb_html.append("<td align=\"center\" nowrap bgcolor='" + getColorByCount(hvs[i].getLognValue("callthreadcurrs"), 30) + "'>" + hvs[i].getLognValue("callthreadcurrs") + "</td>"); //
			sb_html.append("<td align=\"center\" nowrap bgcolor='" + getColorByCount(hvs[i].getLognValue("callthreadmaxs"), 30) + "'>" + hvs[i].getLognValue("callthreadmaxs") + "</td>"); //

			sb_html.append("<td align=\"center\" bgcolor='" + getColorByCount(hvs[i].getLognValue("blockthreads"), 1) + "'><a href=\"./state?type=threadmonitordetail&id=" + hvs[i].getStringValue("id") + "&isend=N\" target=\"_blank\"><font color=\"blue\">" + hvs[i].getLognValue("blockthreads") + "</font></a></td>"); //

			sb_html.append("<td align=\"center\" bgcolor='" + getColorByCount(hvs[i].getLognValue("deferthreads"), 1) + "'><a href=\"./state?type=threadmonitordetail&id=" + hvs[i].getStringValue("id") + "&isend=Y1\" target=\"_blank\"><font color=\"blue\">" + hvs[i].getLognValue("deferthreads") + "</font></a></td>"); //

			sb_html.append("<td align=\"center\" bgcolor='" + getColorByCount(hvs[i].getLognValue("weighterthreads"), 1) + "'><a href=\"./state?type=threadmonitordetail&id=" + hvs[i].getStringValue("id") + "&isend=Y2\" target=\"_blank\"><font color=\"blue\">" + hvs[i].getLognValue("weighterthreads") + "</font></a></td>"); //

			sb_html.append("<td align=\"center\" bgcolor='" + getColorByCount(hvs[i].getLognValue("jvmoverthreads", 0), 1) + "'><a href=\"./state?type=threadmonitordetail&id=" + hvs[i].getStringValue("id") + "&isend=Y3\" target=\"_blank\"><font color=\"blue\">" + hvs[i].getStringValue("jvmoverthreads", "") + "</font></a></td>"); //

			sb_html.append("<td align=\"center\" nowrap bgcolor='" + getColorByCount(hvs[i].getLognValue("dspoolbusy", 0), 5) + "'>" + hvs[i].getStringValue("dspoolbusy", "") + "</td>"); //正忙数
			sb_html.append("<td align=\"center\" nowrap bgcolor='" + getColorByCount(hvs[i].getLognValue("dspooltotal", 0), 8) + "'>" + hvs[i].getStringValue("dspooltotal", "") + "</td>"); //所有数
			sb_html.append("<td align=\"center\" nowrap bgcolor='" + getColorByCount(hvs[i].getLognValue("dspoolmaxbusy", 0), 5) + "'>" + hvs[i].getStringValue("dspoolmaxbusy", "") + "</td>"); //曾经最大忙碌数
			sb_html.append("<td align=\"center\" nowrap bgcolor='" + getColorByCount(hvs[i].getLognValue("dspoolmaxtotal", 0), 8) + "'>" + hvs[i].getStringValue("dspoolmaxtotal", "") + "</td>"); //曾经最大实例数
			sb_html.append("</tr>\r\n"); //
		}
		sb_html.append("</table>"); //
		return sb_html.toString(); //
	}

	private String getColorByCount(long _count, int _max) {
		if (_count >= _max) { //如果超过上限,就大红显示!!否则就白色显示!!!
			return "#FF9DFF"; //大红
		} else {
			return "#FFFFFF"; //
		}
	}

	/**
	 * 根据某个时间去数据库存中取某天数据!!
	 * @param _date
	 * @return
	 */
	private String getThreadMonitorDetail(String _id, String _isend) throws Exception {
		if (_id == null) {
			return "必须定义id参数!";
		}
		TBUtil tbUtil = new TBUtil(); //
		String str_sql = null;
		if (_isend == null) {
			str_sql = "select * from pub_threadmonitor_b where threadmonitor_id='" + _id + "' order by threadno";
		} else {
			if (_isend.equals("N")) {
				str_sql = "select * from pub_threadmonitor_b where threadmonitor_id='" + _id + "' and iscallend='N' order by threadno";
			} else if (_isend.equals("Y1")) {
				str_sql = "select * from pub_threadmonitor_b where threadmonitor_id='" + _id + "' and iscallend='Y' and overtype like '%超时%' order by threadno";
			} else if (_isend.equals("Y2")) {
				str_sql = "select * from pub_threadmonitor_b where threadmonitor_id='" + _id + "' and iscallend='Y' and overtype like '%超重%' order by threadno";
			} else if (_isend.equals("Y3")) {
				str_sql = "select * from pub_threadmonitor_b where threadmonitor_id='" + _id + "' and iscallend='Y' and jvmused>" + WLTConstants.THREAD_OVERJVM_VALUE + " order by threadno";
			}
		}
		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, str_sql);
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("阻塞/超时的可能存在性能隐患(超过10秒未响应)的线程:<br>\r\n");
		sb_html.append("<table align=\"left\" cellpadding=\"3\" cellspacing=\"0\">\r\n"); //
		sb_html.append("<tr>"); //
		sb_html.append("<td bgcolor=\"E3E3E3\" nowrap>序号</td>"); //
		sb_html.append("<td bgcolor=\"E3E3E3\" nowrap>监控时间</td>"); //
		sb_html.append("<td bgcolor=\"E3E3E3\" nowrap>线程序号</td>"); //
		sb_html.append("<td bgcolor=\"E3E3E3\" nowrap>是否<br>响应</td>"); //
		sb_html.append("<td bgcolor=\"E3E3E3\" nowrap>调用时间</td>"); //
		sb_html.append("<td bgcolor=\"E3E3E3\" nowrap>调用者</td>"); //
		sb_html.append("<td bgcolor=\"E3E3E3\" nowrap>调用的功能点</td>"); //
		sb_html.append("<td bgcolor=\"E3E3E3\" nowrap>客户端调用堆栈</td>"); //
		sb_html.append("<td bgcolor=\"E3E3E3\">服务名/方法名/参数</td>"); ///
		sb_html.append("<td bgcolor=\"E3E3E3\" nowrap>超时/超重</td>"); //
		sb_html.append("<td bgcolor=\"E3E3E3\" nowrap>耗时</td>"); //
		sb_html.append("<td bgcolor=\"E3E3E3\" nowrap>重量</td>"); //
		sb_html.append("<td bgcolor=\"E3E3E3\" nowrap>调用前<br>JVM</td>"); //
		sb_html.append("<td bgcolor=\"E3E3E3\" nowrap>调用后<br>JVM</td>"); //
		sb_html.append("<td bgcolor=\"E3E3E3\" nowrap>JVM<br>增长</td>"); //
		sb_html.append("<td bgcolor=\"E3E3E3\" nowrap>执行的查询SQL</td>"); //
		sb_html.append("</tr>"); //
		for (int i = 0; i < hvs.length; i++) {
			sb_html.append("<tr>"); //
			sb_html.append("<td>" + (i + 1) + "</td>"); //
			sb_html.append("<td nowrap>" + hvs[i].getStringValue("monitortime") + "</td>"); //
			sb_html.append("<td nowrap>" + hvs[i].getStringValue("threadno") + "</td>"); //
			sb_html.append("<td nowrap>" + hvs[i].getStringValue("iscallend") + "</td>"); //
			sb_html.append("<td nowrap>" + hvs[i].getStringValue("begintime") + "</td>"); ////
			sb_html.append("<td nowrap>" + hvs[i].getStringValue("calluser") + "</td>"); //
			sb_html.append("<td nowrap>" + tbUtil.replaceAll(hvs[i].getStringValue("callmenuname"), "→", "-><br>") + "</td>"); ////调用的菜单名称!!!

			sb_html.append("<td nowrap>" + tbUtil.replaceAll(hvs[i].getStringValue("callstack"), "←", "<br>") + "</td>"); //调用的堆栈!!!
			sb_html.append("<td>服务名：<font color=\"blue\">" + hvs[i].getStringValue("servicename") + "</font><br>方法名：<font color=\"blue\">" + hvs[i].getStringValue("methodname") + "()</font>,参数值：<br>" + tbUtil.replaceAll(hvs[i].getStringValue("parameters"), "§", "<br>") + "</td>"); //
			sb_html.append("<td nowrap>" + hvs[i].getStringValue("overtype") + "</td>"); //
			sb_html.append("<td nowrap bgcolor=\"" + getColorByCount(hvs[i].getLognValue("usedsecond", 0), WLTConstants.THREAD_OVERTIME_VALUE) + "\">" + hvs[i].getLognValue("usedsecond") + "毫秒<br>" + (hvs[i].getLognValue("usedsecond") / 1000) + "秒</td>"); //

			if (hvs[i].getStringValue("weightersize") == null) {
				sb_html.append("<td nowrap></td>"); ///重量!
			} else {
				sb_html.append("<td nowrap bgcolor=\"" + getColorByCount(hvs[i].getLognValue("weightersize", 0), WLTConstants.THREAD_OVERWEIGHT_VALUE) + "\">" + hvs[i].getLognValue("weightersize") + "字节<br>" + (hvs[i].getLognValue("weightersize") / 1000) + "K</td>"); ///重量!
			}
			sb_html.append("<td nowrap>" + hvs[i].getStringValue("jvm1") + "K<br>" + (hvs[i].getLognValue("jvm1", 0) / 1024) + "M</td>"); //

			if (hvs[i].getStringValue("jvm2") == null) {
				sb_html.append("<td nowrap></td>"); //
			} else {
				sb_html.append("<td nowrap>" + hvs[i].getStringValue("jvm2") + "K<br>" + (hvs[i].getLognValue("jvm2", 0) / 1024) + "M</td>"); //
			}

			if (hvs[i].getStringValue("jvmused") == null) {
				sb_html.append("<td nowrap></td>"); //
			} else {
				sb_html.append("<td nowrap bgcolor=\"" + getColorByCount(hvs[i].getLognValue("jvmused", 0), WLTConstants.THREAD_OVERJVM_VALUE) + "\">" + hvs[i].getStringValue("jvmused") + "K<br>" + (hvs[i].getLognValue("jvmused", 0) / 1024) + "M</td>"); //
			}
			sb_html.append("<td nowrap>" + tbUtil.replaceAll(hvs[i].getStringValue("weightersqls"), "§", "<br>") + "</td>"); //超重的SQL						
			sb_html.append("</tr>"); //
		}
		sb_html.append("</table>"); //
		return sb_html.toString(); //
	}

	/**
	 * 显示在线用户!!
	 * @return
	 * @throws Exception
	 */
	private String getOnlineUser() throws Exception {
		TBUtil tbutil = new TBUtil(); //
		StringBuffer sb_html = new StringBuffer(); //
		FrameWorkCommServiceImpl commService = new FrameWorkCommServiceImpl(); //
		String[][] str_onlineusers = commService.getServerOnlineUser(); //
		sb_html.append("<br>当前在线用户[" + str_onlineusers.length + "]:<br>\r\n"); //
		sb_html.append("<table align=\"left\">\r\n"); //
		sb_html.append("<tr>\r\n"); //
		sb_html.append("<td>SessionId</td>\r\n"); //
		sb_html.append("<td>IP1</td>\r\n"); //
		sb_html.append("<td>IP2</td>\r\n"); //
		sb_html.append("<td>用户编码</td>\r\n"); //
		sb_html.append("<td>用户名称</td>\r\n"); //
		sb_html.append("<td>所属机构</td>\r\n"); //
		sb_html.append("<td>登录时间</td>\r\n"); //
		sb_html.append("<td>最后一次访问时间</td>\r\n"); //
		sb_html.append("<td>使用时长</td>\r\n"); //
		sb_html.append("<td>当前时间</td>\r\n"); //
		sb_html.append("<td>发呆时长</td>\r\n"); //
		sb_html.append("</tr>"); //
		for (int i = 0; i < str_onlineusers.length; i++) {
			sb_html.append("<tr>\r\n");
			for (int j = 0; j < 11; j++) {
				String str_text = str_onlineusers[i][j]; //
				str_text = tbutil.replaceAll(str_text, "<html>", ""); //
				str_text = tbutil.replaceAll(str_text, "</html>", ""); //
				sb_html.append("<td>" + str_text + "</td>\r\n"); //
			}
			sb_html.append("</tr>\r\n");
		}
		sb_html.append("</table>\r\n");
		return sb_html.toString(); //

	}

	/**
	 * 显示在线用户!
	 * @return
	 * @throws Exception
	 */
	private String getUserClickMenuHist(String _date) throws Exception {
		String str_curdate = null; //
		if (_date == null) {
			str_curdate = new TBUtil().getCurrDate(); //
		} else {
			str_curdate = _date; //
		}

		StringBuffer sb_html = new StringBuffer(); //

		sb_html.append("日期:<input name=\"date1\" type=\"text\" size=\"15\" onfocus=\"calendar()\" value=\"" + str_curdate + "\" style=\"color: #D78D9C; border: 1 solid #D78D9C; width: 130px; height: 19px;\">\r\n"); //
		sb_html.append("&nbsp;&nbsp;<input type=\"button\" class=\"style_button\" value=\"查询\" onClick=\"JavaScript:self.location='./state?type=userclickmenuhist&date=' + date1.value + ''\">");
		sb_html.append("<br><br>[" + str_curdate + "]用户点击菜单历史:<br>\r\n"); //

		HashVO[] hvs_clickmenu = new CommDMO().getHashVoArrayByDS(null, "select * from pub_menu_clicklog  where clicktime like '" + str_curdate + "%' order by clicktime asc"); //
		sb_html.append("<table align=\"left\" width=\"1060\">\r\n"); //
		sb_html.append("<tr>\r\n"); //
		sb_html.append("<td nowrap>序号</td>\r\n"); //
		sb_html.append("<td nowrap>用户名称</td>\r\n"); //
		sb_html.append("<td nowrap>所属机构</td>\r\n"); //
		sb_html.append("<td nowrap>点击时间</td>\r\n"); //
		sb_html.append("<td nowrap>菜单名称</td>\r\n"); //
		sb_html.append("<td nowrap>菜单路径</td>\r\n"); //
		sb_html.append("<td nowrap>消耗时长</td>\r\n"); //
		sb_html.append("</tr>"); //
		for (int i = 0; i < hvs_clickmenu.length; i++) {
			sb_html.append("<tr>\r\n");
			sb_html.append("<td nowrap width=\"30\">" + (i + 1) + "</td>\r\n"); //
			sb_html.append("<td nowrap width=\"50\">" + hvs_clickmenu[i].getStringValue("username") + "</td>\r\n"); //
			sb_html.append("<td nowrap width=\"190\">" + hvs_clickmenu[i].getStringValue("deptname") + "</td>\r\n"); //
			sb_html.append("<td nowrap width=\"125\">" + hvs_clickmenu[i].getStringValue("clicktime") + "</td>\r\n"); //
			sb_html.append("<td nowrap width=\"150\">" + hvs_clickmenu[i].getStringValue("menuname") + "</td>\r\n"); //
			sb_html.append("<td nowrap width=\"400\">" + hvs_clickmenu[i].getStringValue("menupath") + "</td>\r\n"); //
			sb_html.append("<td nowrap>" + hvs_clickmenu[i].getStringValue("wastetime") + "</td>\r\n"); //
			sb_html.append("</tr>\r\n");
		}
		sb_html.append("</table>\r\n");
		return sb_html.toString(); //
	}

	/**
	 * 系统指导
	 * @return
	 * @throws Exception
	 */
	private String getReference() throws Exception {
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("◆系统一级模块简称<br>\r\n"); //
		sb_html.append("内外规(law),合规管理(cmp),法律事务管理(lbs),风险管理(risk),如果想新增一级模块,必须全体讨论,任何人不得随意创建新的一级模块名<br>\r\n"); //
		sb_html.append("<br>\r\n"); //

		sb_html.append("◆平品与产品的关键表<br>\r\n"); //
		sb_html.append("&nbsp;&nbsp;平台的关键表有:pub_menu(功能菜单),pub_user(人员),pub_corp_dept(机构),pub_role(角色),pub_templet_1(元原模板主表),pub_templet_1_item(元原模板子表),pub_wf_process(流程定义),pub_wf_prinstance(流程实例),pub_wf_dealpool(流程处理)<br>\r\n"); //
		sb_html.append("&nbsp;&nbsp;产品的关键表有:law_innerrule(内部制度),cmp_cmpfile(体系文件),risk_point(风险点库),cmp_event(风险事件),cmp_report(合规报告)<br>\r\n"); //
		sb_html.append("<br>\r\n"); //

		sb_html.append("◆包名定义规则<br>\r\n"); //
		sb_html.append("&nbsp;&nbsp;如果是产品,则以com.pushworld.grc开始,然后下面是bs/ui/to,然后是一级模块名(lbs/cmp),然后是二级模块名(p010/p020/p030),比如com.pushworld.grc.bs.lbs.p050<br>"); //
		sb_html.append("&nbsp;&nbsp;如果是项目,则以客户的网站域名开始,比如com.cmbc(民生银行),cn.com.cib(兴业银行),然后后面是bs/ui/to,然后是一级模块名(law/cmp/law),然后是二级模块名(p010/p020),比如cn.com.cib.ui.p030<br>"); //
		sb_html.append("<br>\r\n"); //

		sb_html.append("◆系统权限部分相关说明<br>\r\n"); //
		sb_html.append("&nbsp;系统权限部分涉及到的关键表有pub_user(人员),pub_corp_dept(机构),pub_role(角色),pub_post(岗位),pub_user_role(人员与角色关联),pub_user_post(人员/机构/岗位三者关联)<br>\r\n"); //
		sb_html.append("&nbsp;机构的关键属性有:corptype(机构类型),该属性不能为空,且千万不能设错,因为几乎工作流中的所有计算都依赖于该属性<br>\r\n"); //
		sb_html.append("<br>\r\n"); //

		sb_html.append("◆工作流部分说明<br>\r\n"); //
		sb_html.append("&nbsp;工作流中涉及到的主要表有pub_wf_process(流程定义表),pub_wf_prinstance(流程实例表),pub_wf_dealpool(流程处理信息表)<br>\r\n"); ////
		sb_html.append("&nbsp;工作流的原理是先画一个流程,然后找一张单据表,在表中定义billtype,busitype,prinstanceid等字段,然后定义流程分配(指定对应的billtype与busitype使用对应的流程),然后在表单中定义billtype与busitype的默认值,最后选择风格模板19,就自动建立了工作流应用了!"); //
		sb_html.append("<br>\r\n"); //

		return sb_html.toString(); //
	}

	private String getPageCountFromCache() throws Exception {
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("从缓存取的有[" + ServerEnvironment.ll_fromCache + "]次,不从缓存取的有[" + ServerEnvironment.ll_notfromCache + "]次,缓存目前大小[" + ServerEnvironment.countSQLCache.size() + "]，缓存内容: <a href=\"./state?type=clearPageCountCache\" target=\"_blank\">清除Count缓存</a><br>"); // 
		String[] str_allKeys = (String[]) ServerEnvironment.countSQLCache.keySet().toArray(new String[0]); //
		for (int i = 0; i < str_allKeys.length; i++) { //
			java.util.Hashtable sqlMap = (java.util.Hashtable) ServerEnvironment.countSQLCache.get(str_allKeys[i]); //
			String[] str_sqls = (String[]) sqlMap.keySet().toArray(new String[0]); //
			sb_html.append(str_allKeys[i] + "(" + str_sqls.length + "条)<br>\r\n"); //
			sb_html.append("<table>\r\n"); //
			sb_html.append("<tr><td>序号</td><td>SQL语句</td><td>记录数</td></r>\r\n"); //
			int li_rows = 0; //
			for (int j = 0; j < str_sqls.length; j++) {
				li_rows++; //
				String str_sql = str_sqls[j]; //
				int li_count = (Integer) sqlMap.get(str_sql); //
				sb_html.append("<tr><td>" + li_rows + "</td><td>" + str_sql + "</td><td>" + li_count + "</td></r>\r\n"); //
			}
			sb_html.append("</table>\r\n"); //
			sb_html.append("<br>\r\n"); //
		}

		return sb_html.toString(); //
	}

	private String getCallCount() throws Exception {
		StringBuffer sb_html = new StringBuffer(); //
		LinkedHashMap map_second = new LinkedHashMap(); //排序的!
		long ll_1 = FalsityThread.ll_falthread_1; //
		long ll_2 = FalsityThread.ll_falthread_2; //
		sb_html.append("新开线程的次数[" + ServerEnvironment.newThreadCallCount + "],实际新开线程的增量[" + ll_1 + "],减量[" + ll_2 + "],差额[" + (ll_1 - ll_2) + "]<br>\r\n"); //

		String str_isbusy = new BSUtil().isRealBusiCallHtml(); //
		sb_html.append(str_isbusy); //
		Long[] ll_timeList = (Long[]) RemoteCallServlet.callThreadTimeList.toArray(new Long[0]); //必须用这个方法!!
		sb_html.append("计数器中的总共大小[" + ll_timeList.length + "],所有明细:<br>\r\n"); //
		for (int i = 0; i < ll_timeList.length; i++) {
			long ll_item = ll_timeList[i]; //时间!!!!
			Long ll_second = ll_item / 1000; //折算成秒!!
			if (map_second.containsKey(ll_second)) {
				int li_count = (Integer) map_second.get(ll_second); //
				map_second.put(ll_second, li_count + 1); //加上
			} else {
				map_second.put(ll_second, 1); //置入!!
			}
		}
		SimpleDateFormat sdf_curr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE); //
		Long[] ll_keys = (Long[]) map_second.keySet().toArray(new Long[0]); //
		for (int i = 0; i < ll_keys.length; i++) {
			String str_time = sdf_curr.format(new Date(ll_keys[i] * 1000)); //补上差异!!
			int li_count = (Integer) map_second.get(ll_keys[i]); //
			sb_html.append("[" + str_time + "]=[" + li_count + "]<br>\r\n"); //
		}
		return sb_html.toString(); //
	}

	private String getPagePkInSQLFromCache() throws Exception {
		StringBuffer sb_html = new StringBuffer(); //
		return sb_html.toString(); //
	}

	/**
	 * 显示访问历史
	 * @return
	 * @throws Exception
	 */
	private String getLoginHist() throws Exception {
		return "显示访问历史"; //
	}

	/**
	 * 显示访问历史
	 * @return
	 * @throws Exception
	 */
	private String getConfXml() throws Exception {
		return new FrameWorkCommServiceImpl().getServerConfigXML(); //
	}

	private String getHtmlHead() {
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<html>\r\n");
		sb_html.append("<head>\r\n");
		sb_html.append("<META http-equiv=Content-Type content=\"text/html; charset=GBK\">\r\n"); //
		sb_html.append("<TITLE>服务器端状态</TITLE>\r\n"); //标题名
		sb_html.append("<style type=\"text/css\">\r\n");
		sb_html.append(".p_text {\r\n");
		sb_html.append(" font-size: 12px;\r\n");
		sb_html.append("}\r\n");
		sb_html.append(".style_button {\r\n");
		sb_html.append(" BORDER-RIGHT: #999999 1px solid; BORDER-TOP: #999999 1px solid; BORDER-LEFT: #999999 1px solid; BORDER-BOTTOM: #999999 1px solid; FONT-SIZE: 12px; HEIGHT: 18px; WIDTH=55px; BACKGROUND-COLOR: #EEEEEE;\r\n");
		sb_html.append("}\r\n");
		sb_html.append("table   {  border-collapse:   collapse; font-size: 12px;};\r\n");
		sb_html.append("td   {  border:   solid   1px   #888888; font-size: 12px; };\r\n");
		sb_html.append("</style>\r\n");
		sb_html.append("</head>\r\n");
		sb_html.append("<script language=\"JavaScript\" src=\"./applet/calendar.js\"></script>\r\n"); //
		sb_html.append("<body bgcolor=\"#FFFFFF\" topmargin=10 leftmargin=10 rightmargin=0 bottommargin=0 marginwidth=10 marginheight=10>\r\n");
		sb_html.append("<p class=\"p_text\">\r\n"); //
		return sb_html.toString(); //
	}

	private String getHtmlTail() {
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("</p>\r\n");
		sb_html.append("</body>\r\n"); //
		sb_html.append("</html>\r\n"); //
		return sb_html.toString(); //
	}

	private String getCurrTimeByHour() {
		String str_cuurrTime = new TBUtil().getCurrTime(); //
		return str_cuurrTime.substring(0, 13); //2006-12-15 15
	}

	class KillMeThread extends Thread {

		@Override
		public void run() {
			try {
				Thread.currentThread().sleep(3000);
				//System.exit(0); ////以前执行System.exit(0)后台服务会退出，故屏蔽掉【李春娟/2016-06-01】
			} catch (InterruptedException e) {
				e.printStackTrace();
			} //
		}

	}

	/*private   List<InstalledAppInfo> getInstalledAppList(){
		String installedAppStr=ServerEnvironment.getInstance().getProperty("INSTALLAPPS");
		//String  installedAppStr="com.pushworld.ipushgrc.bs.install-标准产品;cn.com.crcc.bs.install-民生银行;";
		if(installedAppStr!=null&&(installedAppStr.trim().length()>0)){
			ArrayList<InstalledAppInfo> appList=new ArrayList<InstalledAppInfo>();
			String[] strArr=installedAppStr.trim().split(";");
			for (int i = 0; i < strArr.length; i++) {
				String[] appStrArr=strArr[i].split("-");
				InstalledAppInfo app=new InstalledAppInfo(appStrArr[0],appStrArr[1]);
				appList.add(app);
			}
			return appList;
		}
		return null;
	}*/
	private List<HashMap<String, String>> getInstalledAppList() {
		String installedAppStr = ServerEnvironment.getInstance().getProperty("INSTALLAPPS");
		if (installedAppStr != null && (installedAppStr.trim().length() > 0)) {
			ArrayList<HashMap<String, String>> appList = new ArrayList<HashMap<String, String>>();
			String[] strArr = installedAppStr.trim().split(";");
			for (int i = 0; i < strArr.length; i++) {
				String[] appStrArr = strArr[i].split("-");
				HashMap<String, String> map = new HashMap<String, String>();
				String packagePath = "/" + appStrArr[0].replaceAll("\\.", "/") + "/database";
				String tableXmlLocation = packagePath + "/tables.xml";
				String viewXmlLocation = packagePath + "/views.xml";
				String appName = appStrArr[1];
				map.put("appName", appName);
				map.put("viewXmlLocation", viewXmlLocation);
				map.put("tableXmlLocation", tableXmlLocation);
				appList.add(map);
			}
			return appList;
		}
		return null;
	}
}
