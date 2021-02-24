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
 * �鿴������״̬��Servlet,�ǳ�����!!
 * ����ʱ����������¼ʱ,��Ҫʹ�������鿴��������״̬! ����������������Ŀ�з����������û���¼ʱ����,û��;�����Բ鿴��������״̬!!!��ͻȻ�о�����Ҫ������һ������!!!
 * �����ʲô�취����̽�⵽TomCat��Web�������е�http�����е��߳�״̬�ͺ���!!!��Ϊ��ʱ����Ϊweb�������߳����������޶���ɲ��ܷ��ʵ�!!!��������ǰ��������!!
 * Ŀǰ����ͨ��Tomcat��/manager�Ŀ���̨���鿴��!!�ǲ��ǿ���ʹ��JMX��ʵ����һ��??
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
			if (str_type == null) { //��ʾĬ�ϵ�״̬..
				sb_html.append(getHtmlHead());
				sb_html.append(getDefaultStatu(_request)); //
				sb_html.append(getHtmlTail());
			} else {
				initConText = new WLTInitContext(); //
				if (str_type.equals("sysmemory")) { //��ʾϵͳ�ڴ�
					sb_html.append(getHtmlHead());
					sb_html.append(getSysMemory());
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("sysprop")) { //��ʾϵͳ����
					sb_html.append(getHtmlHead());
					sb_html.append(getSysProp());
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("sysoption")) { //��ʾϵͳ����
					sb_html.append(getHtmlHead());
					sb_html.append(getSysOption()); //�鿴ϵͳ����!!
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("reloadsysoption")) { //���¼���ϵͳ����
					sb_html.append(getHtmlHead());
					sb_html.append(reloadSysOption()); //
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("reloadCorpCache")) { //���¼��ػ������桾���/2019-01-23��
					sb_html.append(getHtmlHead());
					sb_html.append(reloadCorpCache());
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("reloadUserCache")) { //���¼�����Ա���桾���/2019-01-23��
					sb_html.append(getHtmlHead());
					sb_html.append(reloadUserCache());
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("dspoolstate")) { //��ʾ���ݿ����ӳ�״̬
					sb_html.append(getHtmlHead());
					sb_html.append(getDataSourcePoolStatu());
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("servicepoolstate")) { //��ʾԶ�̷����״̬
					sb_html.append(getHtmlHead());
					sb_html.append(getRemoteCallThreads());
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("destool")) { //DES���ܹ���!!!
					sb_html.append(getHtmlHead());
					sb_html.append(getDESKeyTool(_request)); //DES���ܹ���
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("threadmonitormem")) { //�̼߳��
					sb_html.append(getHtmlHead());
					sb_html.append(getThreadMonitor());
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("threadmonitor")) { //�̼߳��
					String str_date = _request.getParameter("date"); //
					sb_html.append(getHtmlHead());
					sb_html.append(getThreadMonitor(str_date));
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("threadmonitordetail")) { //�̼߳����ϸ
					String str_id = _request.getParameter("id"); //
					String str_isend = _request.getParameter("isend"); //
					sb_html.append(getHtmlHead());
					sb_html.append(getThreadMonitorDetail(str_id, str_isend));
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("remotecallrate")) { //���׳ɹ���ͳ��
					sb_html.append(getHtmlHead());
					sb_html.append(getRemoteCallRate());
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("remotecallrate_fivemin")) { //���׳ɹ���ͳ��
					sb_html.append(getHtmlHead());
					sb_html.append(getRemoteCallRate_Fivemin());
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("remotecallratedetail")) { //���׳ɹ���ͳ��
					sb_html.append(getHtmlHead());
					sb_html.append(getRemoteCallRate_Fivemin_inf(Long.parseLong(_request.getParameter("currtime")), _request.getParameter("monitortype")));
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("deletethreadmonitordetail")) { //�̼߳����ϸ
					RemoteCallServlet.revokeThreadHashtable.clear();
					sb_html.append("����ɹ���");
				} else if (str_type.equals("serverconsole")) { //�鿴����������̨
					sb_html.append(getHtmlHead());
					if (_request.getParameter("isclear") != null) {
						sb_html.append(getServerConsole(true));
					} else {
						sb_html.append(getServerConsole(false));
					}
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("sessionsql")) { //��ʾĳ�Ự��SQL���
					sb_html.append(getHtmlHead());
					String str_sid = _request.getParameter("sid"); //
					String str_isclear = _request.getParameter("isclear"); //
					if (str_isclear != null && str_isclear.equals("Y")) {
						sb_html.append(getSessionSQL(str_sid, true));
					} else {
						sb_html.append(getSessionSQL(str_sid, false));
					}
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("onlineuser")) { //��ʾ�����û�
					sb_html.append(getHtmlHead());
					sb_html.append(getOnlineUser());
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("userclickmenuhist")) { //��ʾ�û�����˵���ʷ
					sb_html.append(getHtmlHead());
					String str_date = null; //
					if (_request.getParameter("date") != null) {
						str_date = _request.getParameter("date"); //
					}
					sb_html.append(getUserClickMenuHist(str_date));
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("loginhist")) { //��ʾ�û�������ʷ
					sb_html.append(getHtmlHead());
					sb_html.append(getLoginHist());
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("confxml")) { //��ʾ�����ļ�
					_response.setContentType("text/plain"); //
					sb_html.append(getConfXml());
				} else if (str_type.equals("setexpprint")) { //��ʾ�����ļ�
					sb_html.append(getHtmlHead());
					String str_setType = _request.getParameter("settype"); //
					if (str_setType != null) {
						if (str_setType.equalsIgnoreCase("Y")) {
							cn.com.infostrategy.bs.common.ServerEnvironment.isPrintExceptionStack = true;
							sb_html.append("���÷�������TBUtil.printStackTrace()������ӡ��ջΪ[true]!!<br>");
						} else if (str_setType.equalsIgnoreCase("N")) {
							cn.com.infostrategy.bs.common.ServerEnvironment.isPrintExceptionStack = false;
							sb_html.append("���÷�������TBUtil.printStackTrace()������ӡ��ջΪ[false]!!<br>");
						}
					}
					sb_html.append("��ǰ״̬��=��" + cn.com.infostrategy.bs.common.ServerEnvironment.isPrintExceptionStack + "��");
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("wltdict")) { //ƽ̨�����ֵ�
					sb_html.append(new DataBaseUtilDMO().exportDictAsHtml("ƽ̨�����ֵ�", xmlpath)); //
				} else if (str_type.equals("appdict")) { //��Ʒ����Ŀ�����ֵ�
					String appName = URLDecoder.decode(_request.getParameter("appName"));
					sb_html.append(new DataBaseUtilDMO().exportDictAsHtml(appName + "�����ֵ�", xmlpath)); //
				} else if (str_type.equals("comparesfj")) { //�Ƚ�ƽ̨���html
					String datasourcename = _request.getParameter("datasourcename");//
					datasourcename = new String(datasourcename.getBytes("ISO-8859-1"), "GBK"); //ת��������					
					String howtosort = _request.getParameter("howtosort");
					if ("1".equals(howtosort)) {
						sb_html.append(new DataBaseUtilDMO().exportCompareAsHtml(datasourcename, "ƽ̨��Ƚ�", xmlpath)); //
					} else {
						sb_html.append(new DataBaseUtilDMO().exportCompareSortByTypeAsHtml(datasourcename, "ƽ̨��Ƚ�", xmlpath)); //exportDBCompareSortByTypeAsHtml
					}
				} else if (str_type.equals("dbcomparesfj")) {
					String datasourcename = _request.getParameter("datasourcename1");
					String datasourcename2 = _request.getParameter("datasourcename2");
					datasourcename = new String(datasourcename.getBytes("ISO-8859-1"), "GBK"); //ת��������	
					datasourcename2 = new String(datasourcename2.getBytes("ISO-8859-1"), "GBK"); //ת��������		
					String howtosort = _request.getParameter("howtosort");
					if ("1".equals(howtosort)) {
						sb_html.append(new DataBaseUtilDMO().exportCompareDBAsHtml("����Դ�Ƚ�", xmlpath, datasourcename2, datasourcename, false)); //
					} else {
						sb_html.append(new DataBaseUtilDMO().exportDBCompareSortByTypeAsHtml("����Դ�Ƚ�", datasourcename2, datasourcename, xmlpath));
					}
				} else if (str_type.equals("dbcomparesfjrevose")) { //�Ƚ�ƽ̨���html
					String datasourcename = ServerEnvironment.getDefaultDataSourceName();
					String datasourcename2 = ServerEnvironment.getInstance().getDataSourceVOs()[1].getName();
					datasourcename = new String(datasourcename.getBytes("ISO-8859-1"), "GBK"); //ת��������	
					datasourcename2 = new String(datasourcename2.getBytes("ISO-8859-1"), "GBK"); //ת��������		
					sb_html.append(new DataBaseUtilDMO().exportCompareDBAsHtml("����Դ�Ƚ�", xmlpath, datasourcename2, datasourcename, true)); //
				} else if (str_type.equals("sortedcomparesfj")) { //�Ƚ�ƽ̨���html
					String datasourcename = ServerEnvironment.getDefaultDataSourceName();
					datasourcename = new String(datasourcename.getBytes("ISO-8859-1"), "GBK"); //ת��������	
					sb_html.append(new DataBaseUtilDMO().exportCompareSortByTypeAsHtml(datasourcename, "ƽ̨��Ƚ�", xmlpath)); //exportDBCompareSortByTypeAsHtml
				} else if (str_type.equals("sortedDBcomparesfj")) { //����Դ�Ƚϰ����ͷ���html
					String datasourcename = ServerEnvironment.getDefaultDataSourceName();
					String datasourcename2 = ServerEnvironment.getInstance().getDataSourceVOs()[1].getName();
					datasourcename = new String(datasourcename.getBytes("ISO-8859-1"), "GBK"); //ת��������	
					datasourcename2 = new String(datasourcename2.getBytes("ISO-8859-1"), "GBK"); //ת��������	
					String ifrevose = _request.getParameter("ifrevose");
					if ("true".equals(ifrevose)) {
						sb_html.append(new DataBaseUtilDMO().exportDBCompareSortByTypeAsHtml("����Դ�Ƚ�", datasourcename2, datasourcename, xmlpath)); //exportDBCompareSortByTypeAsHtml
					} else {
						sb_html.append(new DataBaseUtilDMO().exportDBCompareSortByTypeAsHtml("����Դ�Ƚ�", datasourcename, datasourcename2, xmlpath)); //exportDBCompareSortByTypeAsHtml	
					}
				} else if (str_type.equals("comitsql")) { //�Ƚ�ƽ̨���html
					String sql = _request.getParameter("sql"); //
					sql = URLDecoder.decode(sql);
					String datasourcename = _request.getParameter("datesourcename"); //
					if (StringUtils.isEmpty(datasourcename)) {
						datasourcename = null;
					} else {
						datasourcename = new String(datasourcename.getBytes("ISO-8859-1"), "GBK"); //ת��������
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
					//sb_html.append("<script>alert(\"ִ�гɹ���\");window.close();</script>");
					//"window.close();window.opener.location.reload();
					sb_html.append("ִ�гɹ���");
				} else if (str_type.equals("getallcreatesql")) { //�Ƚ�ƽ̨���html
					String datasourcename = _request.getParameter("datasourcename");
					datasourcename = new String(datasourcename.getBytes("ISO-8859-1"), "GBK"); //ת��������		
					String type = _request.getParameter("dbtype");
					sb_html.append(new DataBaseUtilDMO().getAllCreateSql(datasourcename, type, xmlpath)); //
				}
				/*else if (str_type.equals("proddict")) { //ƽ̨�����ֵ�
					sb_html.append(new DataBaseUtilDMO().exportDictAsHtml("PushGRCˮƽ��Ʒ�����ֵ�", "/com/pushworld/ipushgrc/bs/install/database/tables.xml")); ////
				}*/
				else if (str_type.equals("createtable")) { //������					
					String str_dbtype = _request.getParameter("dbtype"); //
					String str_tabname = _request.getParameter("tabname"); //
					sb_html.append(new DataBaseUtilDMO().exportCreateTableSqlAsHtml(str_dbtype, str_tabname, xmlpath));
				} else if (str_type.equals("compareonetable")) { //������					
					String str_tabname = _request.getParameter("tabname"); //
					String datasourcename = _request.getParameter("datasourcename");
					datasourcename = new String(datasourcename.getBytes("ISO-8859-1"), "GBK"); //ת��������	
					sb_html.append(new DataBaseUtilDMO().exportCompareTableSqlAsHtml(datasourcename, str_tabname, xmlpath));
				} else if (str_type.equals("compareoneview")) { //�Ƚ�һ����ͼ(xml�����ݿ�)				
					String viewname = _request.getParameter("viewname"); //
					String datasourcename = _request.getParameter("datasourcename");
					datasourcename = new String(datasourcename.getBytes("ISO-8859-1"), "GBK"); //ת��������	
					String appTitleName = _request.getParameter("appTitleName");
					appTitleName = new String(appTitleName.getBytes("ISO-8859-1"), "GBK"); //ת��������	
					String viewddl = URLDecoder.decode(_request.getParameter("viewddl"));
					String viewdescr = URLDecoder.decode(_request.getParameter("viewdescr"));
					sb_html.append(new DataBaseUtilDMO().getCompareOneViewHtml(datasourcename, viewname, viewddl, appTitleName, viewdescr));
					//��ǰִ��killme��̨������˳����������ε������/2016-06-01��
					//				} else if (str_type.equals("killme")) { //ɱ���Լ�
					//					sb_html.append("<html>\r\n");
					//					sb_html.append("<head>\r\n");
					//					sb_html.append("<META http-equiv=Content-Type content=\"text/html; charset=GBK\">\r\n");
					//					sb_html.append("<script language=\"JavaScript\">\r\n");
					//					sb_html.append("function closeMe(){\r\n");
					//					sb_html.append("setTimeout(\"window.opener=null;window.open('','_self');window.close();\",1500);\r\n"); //�����Ӻ�ر��Լ�
					//					sb_html.append("}\r\n");
					//					sb_html.append("</script> \r\n");
					//					sb_html.append("</head>\r\n");
					//					sb_html.append("<body onLoad=\"closeMe();\">\r\n");
					//					sb_html.append("3���Ӻ�ɱ���Լ�!!!\r\n");
					//					sb_html.append("</body>\r\n");
					//					sb_html.append("</html>\r\n");
					//					new KillMeThread().start(); //�¿�һ���߳�
				} else if (str_type.equals("reference")) { //��ʾϵͳ����
					sb_html.append(getHtmlHead());
					sb_html.append(getReference());
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("pagecountfromcache")) { //��ʾϵͳ����
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
					sb_html.append("���֮ǰ���ڴ����:<br>\r\n");
					sb_html.append(getJVMMemoery(_request)); //
					ServerEnvironment.countSQLCache.clear(); //���
					sb_html.append("<br><font color=\"blue\">���Count����ɹ�!</font><br><br>\r\n");
					sb_html.append("���֮����ڴ����:<br>");
					sb_html.append(getJVMMemoery(_request)); //
					sb_html.append(getHtmlTail());
				} else if (str_type.equals("clearPagePkValueCache")) { //
					sb_html.append(getHtmlHead());
					sb_html.append("���֮ǰ���ڴ����:<br>\r\n");
					sb_html.append(getJVMMemoery(_request)); //
					ServerEnvironment.pagePKValue.clear(); //���
					sb_html.append("<br><font color=\"blue\">�����������ɹ�!</font><br><br>\r\n");
					sb_html.append("���֮����ڴ����:<br>\r\n");
					sb_html.append(getJVMMemoery(_request)); //
				} else if (str_type.equals("wltview")) { //ƽ̨��ͼ
					sb_html.append(new DataBaseUtilDMO().exportAllXmlViewAsHtml("ƽ̨��ͼ", "/cn/com/infostrategy/bs/sysapp/install/database/views.xml")); //����ͼ������Html
				} else if (str_type.equals("appview")) { //��Ʒ����Ŀ��ͼ
					String appName = URLDecoder.decode(_request.getParameter("appName"));
					sb_html.append(new DataBaseUtilDMO().exportAllXmlViewAsHtml(appName + "��ͼ", xmlpath));
				} else if (str_type.equals("compareallview")) {
					String datasourcename = URLDecoder.decode(_request.getParameter("datasourcename"));
					sb_html.append(new DataBaseUtilDMO().getCompareAllViewHtml(datasourcename, xmlpath));
				} else if (str_type.equals("createindex")) { //��������SQL Gwang 2013-08-27 
					sb_html.append(new DataBaseUtilDMO().exportCreateIndexSqlAsHtml(xmlpath));
				} else {
					sb_html.append(getHtmlHead());
					sb_html.append("û�ж�Ӧ��type��" + str_type + "��"); //��ʾĬ�ϵ�״̬..
					sb_html.append(getHtmlTail());
				}
				initConText.commitAllTrans(); //�ύ��������..
			}
			_response.getWriter().println(sb_html.toString()); //���״̬..
		} catch (Exception ex) {
			if (initConText != null) {
				initConText.rollbackAllTrans(); //�ع���������.
			}
			ex.printStackTrace();
			_response.getWriter().println(new TBUtil().getExceptionStringBuffer(ex, true, true)); ///
		} finally { //�����ͷ�..
			if (initConText != null) {
				initConText.closeAllConn(); //�ر���������!!������Ҫ!!!����Ϊ��������������!!
			}
		}
	}

	/**
	 * Ĭ�ϵ���ʾ
	 * @return
	 * @throws Exception
	 */
	private String getDefaultStatu(HttpServletRequest _request) throws Exception {
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append(getJVMMemoery(_request)); //

		sb_html.append("<br><a href=\"./state?type=serverconsole\" target=\"_blank\">�鿴����������̨</a>&nbsp;<a href=\"./state?type=serverconsole&isclear=Y\" target=\"_blank\">�鿴����շ���������̨</a></br>\r\n");
		sb_html
				.append("<br><a href=\"./state?type=sysmemory\" target=\"_blank\">��鿴�����̡߳�</a>&nbsp;&nbsp;<a href=\"./state?type=sysprop\" target=\"_blank\">�鿴ϵͳ����</a>&nbsp;&nbsp;<a href=\"./state?type=sysoption\" target=\"_blank\">�鿴ϵͳ����(pub_option)</a>&nbsp;&nbsp;<a href=\"./state?type=reloadsysoption\" target=\"_blank\">���¼���ϵͳ����(pub_option)</a>&nbsp;&nbsp;<a href=\"./state?type=reloadCorpCache\" target=\"_blank\">���¼��ػ�������(pub_corp_dept)</a>&nbsp;&nbsp;<a href=\"./state?type=reloadUserCache\" target=\"_blank\">���¼�����Ա����(pub_user)</a></br>\r\n");
		sb_html.append("<br><a href=\"./state?type=threadmonitor&date=" + getCurrTimeByHour() + "\" target=\"_blank\">ϵͳȫ������ʷ��־</a>&nbsp;<a href=\"./state?type=threadmonitormem\" target=\"_blank\">�鿴����/��ʱ�߳��ڴ��¼</a>\r\n<a href=\"./state?type=deletethreadmonitordetail\" target=\"_blank\">�����ػ����¼</a>&nbsp;&nbsp;\r\n<br>");
		sb_html.append("<br><a href=\"./state?type=remotecallrate\" target=\"_blank\">���콻�׳ɹ���</a>&nbsp;<a href=\"./state?type=remotecallrate_fivemin\" target=\"_blank\">5�����ڽ��׳ɹ���</a>&nbsp;</br>\r\n");
		sb_html.append("<br><a href=\"./state?type=dspoolstate\" target=\"_blank\">�鿴���ݿ����ӳ�״̬</a>&nbsp;<a href=\"./state?type=servicepoolstate\" target=\"_blank\">�鿴Զ�̷������ӳ�״̬</a>&nbsp;<a href=\"./state?type=destool\" target=\"_blank\">DES/16����ת������</a></br>\r\n"); //
		sb_html.append("<br><a href=\"./state?type=setexpprint\"  target=\"_blank\">�鿴��������ӡ�쳣��ջ</a>&nbsp;<a href=\"./state?type=setexpprint&settype=Y\"  target=\"_blank\">���÷�������ӡ�쳣��ջ</a>&nbsp;<a href=\"./state?type=setexpprint&settype=N\"  target=\"_blank\">���÷�������ӡ�쳣��ջ</a></br>\r\n"); //

		//�����û��������ʷ!
		sb_html.append("<br>");
		sb_html.append("<a href=\"./state?type=onlineuser\" target=\"_blank\">�鿴�����û�</a>&nbsp;&nbsp;\r\n");
		sb_html.append("<a href=\"./state?type=userclickmenuhist\" target=\"_blank\">�鿴�û�����˵���ʷ</a>&nbsp;&nbsp;\r\n");
		sb_html.append("<a href=\"./state?type=loginhist\"  target=\"_blank\">�鿴�������ʹ����û�</a>&nbsp;&nbsp;\r\n"); //
		sb_html.append("<br>");

		sb_html.append("<br><a href=\"./state?type=confxml\"  target=\"_blank\">�鿴weblight.xml�����ļ�</a></br>\r\n"); //

		//���ݿ��!!! ����Ӧ���Ƕ�̬���,��ƽ̨��Զ�ǵ�һ���̶����,��Ʒ����ĿӦ�ø���weblight.xml�еĲ���[INSTALLAPPS]����̬���
		sb_html.append("<br>");
		sb_html.append("<a href=\"./state?type=wltdict\"  target=\"_blank\">�鿴ƽ̨�����ֵ�</a>&nbsp;&nbsp;&nbsp;&nbsp;");

		List<HashMap<String, String>> appList = getInstalledAppList();
		for (int i = 0; (appList != null) && (i < appList.size()); i++) {
			HashMap<String, String> app = appList.get(i);
			sb_html.append("<a href=\"./state?type=appdict&&appName=" + URLEncoder.encode(URLEncoder.encode(app.get("appName"))) + "&&xmlPath=" + app.get("tableXmlLocation") + "\"  target=\"_blank\">�鿴[" + app.get("appName") + "]�����ֵ�</a>&nbsp;&nbsp;");
		}
		sb_html.append("<br>");

		//���ݿ���ͼ!!! ����Ӧ���Ƕ�̬���,��ƽ̨��Զ�ǵ�һ���̶����,��Ʒ����ĿӦ�ø���weblight.xml�еĲ���[INSTALLAPPS]����̬���
		sb_html.append("<br>");
		sb_html.append("<a href=\"./state?type=wltview\"  target=\"_blank\">�鿴ƽ̨��ͼ</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		for (int i = 0; (appList != null) && (i < appList.size()); i++) {
			HashMap<String, String> app = appList.get(i);
			sb_html.append("<a href=\"./state?type=appview&&appName=" + URLEncoder.encode(URLEncoder.encode(app.get("appName"))) + "&&xmlPath=" + app.get("viewXmlLocation") + "\"  target=\"_blank\">�鿴[" + app.get("appName") + "]��ͼ</a>&nbsp;&nbsp;");
		}

		sb_html.append("<br>");

		//������ع���!!
		sb_html.append("<br>");
		sb_html.append("<a href=\"./state?type=pagecountfromcache\"  target=\"_blank\">��ҳ֮Count�����������</a>&nbsp;");
		sb_html.append("<a href=\"./state?type=pagepkfromcache\"  target=\"_blank\">��ҳ֮��ҳ��¼��������</a>&nbsp;");
		sb_html.append("<a href=\"./state?type=callCount\"  target=\"_blank\">�̲߳鿴</a>&nbsp;");
		sb_html.append("<a href=\"./state?type=threadConfig\"  target=\"_blank\">�߳�����</a>");
		sb_html.append("<br>\r\n"); //

		sb_html.append("<br>");
		sb_html.append("<a href=\"./state?type=reference\"  target=\"_blank\">�ؼ�ָ��</a>&nbsp;&nbsp;");
		sb_html.append("<br>");

		sb_html.append("<br><a href=\"./WebCallServlet?StrParCallClassName=cn.com.infostrategy.bs.sysapp.help.HelpWebCallBean\"  target=\"_blank\">��������</a></br>\r\n"); //

		return sb_html.toString(); //
	}

	//ȡ��JVM�ڴ����!!!!
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
		sb_html.append("��æ���ڴ�ռ�:" + ll_busy + "K/" + (ll_busy / 1024) + "M<br>\r\n");
		sb_html.append("���е��ڴ�ռ�:" + ll_free + "K/" + (ll_free / 1024) + "M<br>\r\n");
		sb_html.append("JVM�����ڴ�ռ�:" + ll_total + "K/" + (ll_total / 1024) + "M(=��æ+�ռ�)<br>\r\n");
		sb_html.append("JVM�ڴ��������:" + ll_max + "K/" + (ll_max / 1024) + "M(���������ڸ����ͻ��ڴ����)<br>\r\n");
		sb_html.append("RemoteCallServlet  ��ǰ��������" + RemoteCallServlet.THREADCOUNT + "��,������󲢷�����" + RemoteCallServlet.MAXTHREADCOUNT + "��,�ܹ���������" + RemoteCallServlet.TOTALTHREADCOUNT + "��<br>\r\n"); //
		sb_html.append("SynchronizerServlet��ǰ��������" + SynchronizerServlet.THREADCOUNT + "��,������󲢷�����" + SynchronizerServlet.MAXTHREADCOUNT + "��,�ܹ���������" + SynchronizerServlet.TOTALTHREADCOUNT + "��<br>\r\n");

		sb_html.append("��Ⱥ�������:\r\n<br>"); //
		HashMap clusterMap = LoginServlet.clusterMap;
		String[] str_keys = (String[]) clusterMap.keySet().toArray(new String[0]); //
		for (int i = 0; i < str_keys.length; i++) {
			String str_itemurl = "http://" + str_keys[i] + str_contextpath + "/state";
			sb_html.append("��" + str_keys[i] + "��=��" + clusterMap.get(str_keys[i]) + "��&nbsp;&nbsp;<a href=\"" + str_itemurl + "\" target=\"_blank\">�鿴��״̬[" + str_itemurl + "]<br>\r\n"); //
		}
		sb_html.append("<br>\r\n"); //
		return sb_html.toString(); //
	}

	private String getSysMemory() {
		System.gc(); //���ͷ��ڴ�

		StringBuffer sb_html = new StringBuffer(); //
		long ll_free = Runtime.getRuntime().freeMemory() / 1024; //
		long ll_total = Runtime.getRuntime().totalMemory() / 1024; //
		long ll_max = Runtime.getRuntime().maxMemory() / 1024; //
		long ll_busy = ll_total - ll_free; //

		sb_html.append("����System.gc()�ͷ������ڴ�....<br><br>");
		sb_html.append("��æ���ڴ�ռ�:" + ll_busy + "K/" + (ll_busy / 1024) + "M<br>\r\n"); //
		sb_html.append("���е��ڴ�ռ�:" + ll_free + "K/" + (ll_free / 1024) + "M<br>\r\n"); //
		sb_html.append("JVM�����ڴ�ռ�:" + ll_total + "K/" + (ll_total / 1024) + "M(=��æ+�ռ�)<br>\r\n"); //
		sb_html.append("JVM�ڴ��������:" + ll_max + "K/" + (ll_max / 1024) + "M(���������ڸ����ͻ��ڴ����)<br>\r\n"); //
		sb_html.append("RemoteCallServlet  ��ǰ��������" + RemoteCallServlet.THREADCOUNT + "��,������󲢷�����" + RemoteCallServlet.MAXTHREADCOUNT + "��,�ܹ���������" + RemoteCallServlet.TOTALTHREADCOUNT + "��<br>\r\n"); //
		sb_html.append("SynchronizerServlet��ǰ��������" + SynchronizerServlet.THREADCOUNT + "��,������󲢷�����" + SynchronizerServlet.MAXTHREADCOUNT + "��,�ܹ���������" + SynchronizerServlet.TOTALTHREADCOUNT + "��<br>\r\n"); //

		ThreadGroup threadGroup = Thread.currentThread().getThreadGroup(); //
		int li_activeGount = threadGroup.activeCount(); //���
		int li_groupCount = threadGroup.activeGroupCount(); //
		Thread[] copyThreads = new Thread[li_activeGount + 10]; //
		threadGroup.enumerate(copyThreads); //��������߳�!
		sb_html.append("��ǰ�߳����л�߳�������" + li_activeGount + "��,��ǰ�߳����л��������" + li_groupCount + "��"); //
		sb_html.append("���л�߳�:<br>"); //
		for (int i = 0; i < copyThreads.length; i++) {
			if (copyThreads[i] != null) { //
				sb_html.append(copyThreads[i] + ",״̬:" + copyThreads[i].getState() + "<br>\r\n"); //
				//				StackTraceElement[] tracks = copyThreads[i].getStackTrace(); //
				//				for (int j = 0; j < tracks.length; j++) { //
				//					sb_html.append("&nbsp;&nbsp;" + tracks[j].getClassName() + "." + tracks[j].getMethodName() + "(" + tracks[j].getLineNumber() + ")<br>\r\n"); //
				//				}
				//				sb_html.append("<br>"); //  
			}
		}
		sb_html.append(getRemoteCallThreads()); //������߳��嵥..
		return sb_html.toString(); //
	}

	//�鿴ϵͳ����
	private String getSysProp() {
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<br>���е�System.getProperties():<br>"); //
		Properties props = System.getProperties(); //
		String[] str_keys = props.keySet().toArray(new String[0]);
		Arrays.sort(str_keys); //
		for (int i = 0; i < str_keys.length; i++) {
			sb_html.append("[" + str_keys[i] + "]=[" + props.getProperty(str_keys[i]) + "]<br>\r\n"); //
		}

		sb_html.append("<br><br>���е�ServerEnvironment.get(_key):<br>"); //
		String[] str_envkeys = ServerEnvironment.getInstance().getKeys(); //
		Arrays.sort(str_envkeys); //
		for (int i = 0; i < str_envkeys.length; i++) {//��ǰ��str_keys.length������д����
			sb_html.append("[" + str_envkeys[i] + "]=[" + ServerEnvironment.getInstance().get(str_envkeys[i]) + "]<br>\r\n"); //
		}
		return sb_html.toString(); //
	}

	/**
	 * �鿴ϵͳ����! ���Ǵ�option����ȡ!!
	 * @return
	 */
	private String getSysOption() {
		StringBuilder sb_html = new StringBuilder(); //
		if (SystemOptions.sysOptions == null) {
			sb_html.append("��û����ϵͳ��������!"); //
			return sb_html.toString(); //
		}
		HashMap mapData = SystemOptions.sysOptions.getDataMap(); //
		sb_html.append("��SystemOptions�ڴ�����е����в���ֵ��:<br>\r\n"); //
		sb_html.append(getMapDataAsHtmlTable(mapData)); //
		return sb_html.toString(); //
	}

	private String reloadSysOption() {
		StringBuilder sb_html = new StringBuilder(); //
		SystemOptions.getInstance().reLoadDataFromDB(true); //���¼���,���ԭ����û����,����ܻ�����ȡ���ݿ�!
		sb_html.append("<font color=\"blue\">�ɹ������´ӱ�pub_option�м�������!!</font><br>\r\n");
		sb_html.append("<font color=\"red\">��ע�⣺�����еĲ����ѵ��ͻ�����,�����еĿͻ��˵�Ч����Ҫ���µ�¼���ܿ���!����֮�ǲ���Ҫ������������(��ǰ��Ҫ����������,���ĺ�)!</font><br>\r\n");
		sb_html.append("�������µ�SystemOptions�ڴ�����е����в���ֵ��:<br>\r\n"); //
		HashMap mapData = SystemOptions.getInstance().getDataMap(); // 
		sb_html.append(getMapDataAsHtmlTable(mapData)); //
		return sb_html.toString(); //
	}

	/**
	 * ���ػ������桾���/2019-01-23��
	 * @return
	 */
	private String reloadCorpCache() {
		StringBuilder sb_html = new StringBuilder(); //
		ServerCacheDataFactory.getInstance().registeCorpCacheData(); // ���¼���һ�»���!!
		sb_html.append("<font color=\"blue\">���¼��ػ�������ɹ�!!</font><br>\r\n");
		return sb_html.toString(); //
	}

	/**
	 * ������Ա���桾���/2019-01-23��
	 * @return
	 */
	private String reloadUserCache() {
		StringBuilder sb_html = new StringBuilder(); //
		ServerCacheDataFactory.getInstance().registeUserCacheData(); // ���¼���һ�»���!!
		sb_html.append("<font color=\"blue\">���¼�����Ա����ɹ�!!</font><br>\r\n");
		return sb_html.toString(); //
	}

	private String getMapDataAsHtmlTable(HashMap _map) {
		StringBuilder sb_html = new StringBuilder(); //
		String[] str_keys = (String[]) _map.keySet().toArray(new String[0]); //
		new TBUtil().sortStrs(str_keys); //����һ��!
		sb_html.append("<table>\r\n"); //
		sb_html.append("<tr><td align=\"center\" bgcolor=\"#DDFFFF\">������</td><td align=\"center\" bgcolor=\"#DDFFFF\">����ֵ</td></tr>\r\n"); //
		for (int i = 0; i < str_keys.length; i++) { //
			String str_value = (String) _map.get(str_keys[i]); //
			str_value = (str_value == null ? "" : str_value);
			sb_html.append("<tr><td>" + str_keys[i] + "</td><td>" + str_value + "</td></tr>\r\n"); ////
		}
		sb_html.append("</table>\r\n"); //
		return sb_html.toString(); //
	}

	/**
	 * ȡ�÷������˿���̨��Ϣ..
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
		sb_sql.append("<font color=\"blue\">���ټ����ỰSessionId=[" + _sid + "]��SQLִ�м�¼,ȡ�����Ƿ������ӷ�����ɾ��״̬��[" + _isclear + "]</font><br><br>\r\n"); //

		Queue queue = (Queue) ServerEnvironment.getSessionSqlListenerMap().get(_sid); //
		if (queue == null) {
			sb_sql.append("<font color=\"red\">[" + new TBUtil().getCurrTime() + "][" + _sid + "]��û��ע��SessionSQL������,����ע�����!!</font>\r\n"); //
			return sb_sql.toString();
		}

		if (queue.isEmpty()) {
			sb_sql.append("<font color=\"#FF80C0\">[" + new TBUtil().getCurrTime() + "][" + _sid + "]û�з���һ������!!</font>\r\n"); //
			return sb_sql.toString();
		}

		sb_sql.append("<font color=\"green\">[" + new TBUtil().getCurrTime() + "][" + _sid + "]ȡ������[" + queue.size() + "]������:</font><br><br>\r\n"); //
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
	 * ȡ�����ݿ����ӳ�״̬
	 * @return
	 */
	private String getDataSourcePoolStatu() throws Exception {
		StringBuffer sb_html = new StringBuffer(); //
		FrameWorkCommServiceImpl commService = new FrameWorkCommServiceImpl(); //
		String[][] str_dsnumbers = commService.getDatasourcePoolActiveNumbers(); //
		sb_html.append("<br>���ݿ����ӳ�״̬:<br>\r\n"); //
		sb_html.append("<table align=\"left\">\r\n"); //
		sb_html.append("<tr><td>����Դ����</td><td>����Դ˵��</td><td>��ǰ���</td><td>��ǰʵ����</td><td>�����</td><td>״̬</td></tr>\r\n"); //
		for (int i = 0; i < str_dsnumbers.length; i++) {
			sb_html.append("<tr><td>" + str_dsnumbers[i][0] + "</td><td>" + str_dsnumbers[i][1] + "</td><td>" + str_dsnumbers[i][2] + "</td><td>" + str_dsnumbers[i][3] + "</td><td>" + str_dsnumbers[i][4] + "</td><td>" + str_dsnumbers[i][5] + "</td></tr>\r\n"); //
		}
		sb_html.append("</table>\r\n"); //
		return sb_html.toString(); //
	}

	/**
	 * ���ص���RemoteCall���߳�.
	 * @return
	 */
	private String getRemoteCallThreads() {
		StringBuffer sb_html = new StringBuffer(); //
		FrameWorkCommServiceImpl commService = new FrameWorkCommServiceImpl(); //
		Thread[] threads = commService.getAllRemoteCallServletThreads(); //

		sb_html.append("<br><br><br>��ǰ����RemoteCallServlet���߳���[" + threads.length + "]:<br>\r\n"); //
		sb_html.append("<table align=\"left\">\r\n"); //
		sb_html.append("<tr>"); //
		sb_html.append("<td>�߳���</td>"); //
		sb_html.append("<td>�߳�����</td>"); //
		sb_html.append("<td>�߳����е�����</td>"); //
		sb_html.append("<td>������ò���</td>"); //
		sb_html.append("</tr>"); //
		for (int i = 0; i < threads.length; i++) {
			CurrSessionVO sessionVO = SessionFactory.getInstance().getClientEnv(threads[i]); //ȡ��session��Ϣ!
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
		if (str_init != null) { //���û��,����һ�δ�!!
			String str_encryedPwd = new DESKeyTool().encrypt(str_init); //����
			sb_html.append("���ַ�����<font color=\"blue\">" + str_init + "</font>������DES���ܵĽ���ǡ�<font color=\"red\">" + str_encryedPwd + "</font>��<br>\r\n"); //
			String str_hexstr = new TBUtil().convertStrToHexString(str_init); //ת����16����
			sb_html.append("���ַ�����<font color=\"blue\">" + str_init + "</font>������16����ת���Ľ���ǡ�<font color=\"red\">" + str_hexstr + "</font>��<br>\r\n"); //
		}
		sb_html.append("<form method=\"post\" action=\"./state?type=destool\">��������Ҫ���ܵ��ַ���:<input name=\"initstr\" type=\"text\" value=\"" + (str_init == null ? "" : str_init) + "\" size=\"50\">&nbsp;<input type=\"submit\" name=\"Submit\" value=\"�ύ\"></form>\r\n"); //
		return sb_html.toString(); //
	}

	private String getThreadConfig(HttpServletRequest _request) {
		StringBuilder sb_html = new StringBuilder(); //
		String str_conf = _request.getParameter("confstr"); //�����
		if (str_conf != null) { //���û��,����һ�δ�!!
			sb_html.append("�ύ�ɹ�!<br>\r\n");
			;
			if (str_conf.toUpperCase().startsWith("Y")) {
				ServerEnvironment.isPageFalsity = true; //
				if (str_conf.length() > 1) {
					try {
						int li_sleep = Integer.parseInt(str_conf.substring(1, str_conf.length())); //���ܻ�ʧ��
						ServerEnvironment.falsitySleep = li_sleep; //ѭ������,Խ��Խ��ҲԽ��!!
					} catch (Exception ex) {
						ex.printStackTrace(); //
					}
				}
			} else {
				ServerEnvironment.isPageFalsity = false; //
			}
		} else {
			sb_html.append("ȡ����ˢ�³ɹ�!<br>\r\n");
			;
		}
		sb_html.append("�Ƿ��¿��߳�=[" + ServerEnvironment.isPageFalsity + "],Sleep�ĺ�����=[" + ServerEnvironment.falsitySleep + "]<br>\r\n"); //
		sb_html.append("<form method=\"post\" action=\"./state?type=threadConfig\">������������(Ĭ��ֵY300,�������þ����N):<input name=\"confstr\" type=\"text\" value=\"Y300\" size=\"10\">&nbsp;<input type=\"submit\" name=\"Submit\" value=\"�ύ\"> <a href=\"./state?type=threadConfig\">ˢ��</a></form>\r\n"); //
		return sb_html.toString(); //
	}

	/**
	 * �̼߳��
	 * @return
	 */
	private String getThreadMonitor() {
		Hashtable ht = RemoteCallServlet.revokeThreadHashtable; //
		String[] str_allKeys = (String[]) ht.keySet().toArray(new String[0]); //
		Arrays.sort(str_allKeys); //
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("������ʾ�����ڴ���(RemoteCallServlet.revokeThreadHashtable)��¼�������߳�!!<br>����߳�����������������,ͬʱ��������ѽ���������5���Ҳ�����������,��Щ��¼�����ϱ������߳�ɾ����,����˵��û���˷��ʵ����������Ӧ��û�м�¼����������!<br>"); //
		sb_html.append("<table align=\"left\">\r\n"); //
		sb_html.append("<tr>"); //
		sb_html.append("<td>���</td>"); //
		sb_html.append("<td>�߳�No.</td>"); //0
		sb_html.append("<td>��ʼʱ��</td>"); //1
		sb_html.append("<td>�Ƿ���</td>"); //2
		sb_html.append("<td>������</td>"); //3
		sb_html.append("<td>������</td>"); //4
		sb_html.append("<td>������</td>"); //5
		sb_html.append("<td>����ֵ</td>"); //6
		sb_html.append("<td>ʱ��</td>"); //7ʱ��
		sb_html.append("<td>����</td>"); //8,����
		sb_html.append("<td>SQLs</td>"); //9SQL
		sb_html.append("<td>����Ĺ��ܵ�</td>"); //10
		sb_html.append("<td>�ͻ��˶�ջ</td>"); //11

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
			sb_html.append("<td nowrap>" + (str_values[6] == null ? "" : tbUtil.replaceAll(str_values[6], "��", "<br>")) + "</td>"); //����ֵ
			sb_html.append("<td nowrap>" + (str_values[7] == null ? "" : (str_values[7] + "����," + (Integer.parseInt(str_values[7]) / 1000) + "��")) + "</td>"); //ʱ��
			sb_html.append("<td nowrap>" + (str_values[8] == null ? "" : (str_values[8] + "�ֽ�," + (Integer.parseInt(str_values[8]) / 1024) + "K")) + "</td>"); //����
			sb_html.append("<td nowrap>" + (str_values[9] == null ? "" : tbUtil.replaceAll(str_values[9], "��", "<br>")) + "</td>"); //SQLs
			sb_html.append("<td nowrap>" + str_values[10] + "</td>"); //
			sb_html.append("<td nowrap>" + str_values[11] + "</td>"); //
			sb_html.append("</tr>"); //
		}
		sb_html.append("</table>"); //
		return sb_html.toString(); //
	}

	/**
	 * ���׳ɹ���
	 * @return
	 */
	private String getRemoteCallRate() {
		if (ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE") != null && ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE").equals("Y")) {
		} else {
			return "weblight.xml��δ����ISMONITORREMOTECALLRATE=Y";
		}
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<table align=\"left\">\r\n"); //
		sb_html.append("<tr>"); //
		sb_html.append("<td bgcolor=\"FFCACA\" rowspan=2>���</td>");
		sb_html.append("<td bgcolor=\"FFCACA\" rowspan=2>ʱ��</td>");
		sb_html.append("<td bgcolor=\"FFCACA\" rowspan=2>����</td>");
		for (int i = 0; i < 24; i++) {
			sb_html.append("<td colspan=3 align=center bgcolor=\"FFCACA\">" + i + "��</td>");
		}
		sb_html.append("</tr>");
		sb_html.append("<tr>");
		for (int i = 0; i < 24; i++) {
			sb_html.append("<td align=center bgcolor=\"FFCACA\">��<br>��<br>��<br>��</td>");
			sb_html.append("<td align=center bgcolor=\"FFCACA\">ʧ<br>��<br>��<br>��</td>");
			sb_html.append("<td align=center bgcolor=\"FFCACA\">��<br>��<br>��</td>");
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
	 * ������ڵĽ��׳ɹ���
	 * @return
	 */
	public String getRemoteCallRate_Fivemin() {
		if (ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE") != null && ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE").equals("Y")) {
		} else {
			return "weblight.xml��δ����ISMONITORREMOTECALLRATE=Y";
		}
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<table align=\"left\">\r\n"); //
		sb_html.append("<tr>"); //
		sb_html.append("<td align=center bgcolor=\"FFCACA\">���</td>");
		sb_html.append("<td align=center bgcolor=\"FFCACA\">�ɼ�ʱ��</td>");
		sb_html.append("<td align=center bgcolor=\"FFCACA\">����</td>");
		sb_html.append("<td align=center bgcolor=\"FFCACA\">�ɹ�����</td>");
		sb_html.append("<td align=center bgcolor=\"FFCACA\">ʧ�ܴ���</td>");
		sb_html.append("<td align=center bgcolor=\"FFCACA\">���׳ɹ���</td>");
		sb_html.append("<td align=center bgcolor=\"FFCACA\">�鿴����</td>");
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
				key = vos[i].getStringValue("monitortype", "��ҳ");
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
					sb_html.append("<td nowrap rowspan=" + map.size() + "><font color=\"blue\">��" + tbUtil.getTimeByLongValue(li_fiveminbefor) + "��" + tbUtil.getTimeByLongValue(ll_currtime) + "</font></td>");
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
				sb_html.append("<td nowrap><a href =\"./state?type=remotecallratedetail&currtime=" + ll_currtime + "&monitortype=" + tbUtil.convertStrToHexString(keys[j]) + "\" target=\"_blank\">�鿴����</a></td>");
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
			return "weblight.xml��δ����ISMONITORREMOTECALLRATE=Y";
		}
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<table align=\"left\">\r\n"); //
		sb_html.append("<tr>"); //
		sb_html.append("<td align=center bgcolor=\"FFCACA\">���</td>");
		sb_html.append("<td align=center bgcolor=\"FFCACA\">������</td>");
		sb_html.append("<td align=center bgcolor=\"FFCACA\">����ʱ��</td>");
		sb_html.append("<td align=center bgcolor=\"FFCACA\">���ʹ���</td>");
		sb_html.append("<td align=center bgcolor=\"FFCACA\">���÷���</td>");
		sb_html.append("<td align=center bgcolor=\"FFCACA\">���÷���</td>");
		sb_html.append("<td align=center bgcolor=\"FFCACA\">�Ƿ�ɹ�</td>");
		sb_html.append("<td align=center bgcolor=\"FFCACA\">�쳣��Ϣ</td>");
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
				sb_html.append("<td nowrap><font color=\"blue\">" + vos[i].getStringValue("calluser", "��") + "</font></td>");
				sb_html.append("<td nowrap><font color=\"blue\">" + vos[i].getStringValue("begintime", "��") + "</font></td>");
				sb_html.append("<td nowrap><font color=\"blue\">" + vos[i].getStringValue("monitortype", "��") + "</font></td>");
				sb_html.append("<td nowrap><font color=\"blue\">" + vos[i].getStringValue("servicename", "��") + "</font></td>");
				sb_html.append("<td nowrap><font color=\"blue\">" + vos[i].getStringValue("methodname", "��") + "</font></td>");
				sb_html.append("<td nowrap><font color=\"blue\">" + vos[i].getStringValue("issucess", "��") + "</font></td>");
				sb_html.append("<td nowrap><font color=\"red\">" + vos[i].getStringValue("errormsg", "��").replaceAll("\n", "<br>") + "</font></td>");
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
	 * ����ĳ��ʱ��ȥ���ݿ����ȡĳ������!!
	 * @param _date
	 * @return
	 */
	private String getThreadMonitor(String _date) throws Exception {
		if (_date == null) {
			return "���붨��date����!";
		}
		if (_date.length() == 10) { //�������,��Ĭ����8��
			_date = _date + " 08"; //
		}

		String str_day = _date.substring(0, 10); //��
		TBUtil tbUtil = new TBUtil(); ////
		String str_cuurrdate = tbUtil.getCurrDate(); //����
		long ll_currdate = tbUtil.parseDateToLongValue(str_cuurrdate); //����..
		String str_beforeDate_1 = tbUtil.formatDateToStr(ll_currdate - 1 * 24 * 60 * 60 * 1000); //
		String str_beforeDate_2 = tbUtil.formatDateToStr(ll_currdate - 2 * 24 * 60 * 60 * 1000); //

		StringBuffer sb_html = new StringBuffer(); //
		if (str_day.equals(str_beforeDate_2)) {
			sb_html.append("ǰ��[<font color=\"green\">" + str_beforeDate_2 + "</font>]&nbsp;&nbsp;"); //
		} else {
			sb_html.append("ǰ��[<a href=\"./state?type=threadmonitor&date=" + str_beforeDate_2 + "\"><font color=\"blue\">" + str_beforeDate_2 + "</font></a>]&nbsp;&nbsp;"); //
		}
		if (str_day.equals(str_beforeDate_1)) {
			sb_html.append("����[<font color=\"green\">" + str_beforeDate_1 + "</font>]&nbsp;&nbsp;"); //
		} else {
			sb_html.append("����[<a href=\"./state?type=threadmonitor&date=" + str_beforeDate_1 + "\"><font color=\"blue\">" + str_beforeDate_1 + "</font></a>]&nbsp;&nbsp;"); //
		}
		if (str_day.equals(str_cuurrdate)) {
			sb_html.append("����[<font color=\"green\">" + str_cuurrdate + "</font>]&nbsp;&nbsp;"); //
		} else {
			sb_html.append("����[<a href=\"./state?type=threadmonitor&date=" + str_cuurrdate + "\"><font color=\"blue\">" + str_cuurrdate + "</font></a>]&nbsp;&nbsp;"); //
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

		sb_html.append("<br>ʱ��Ρ�" + _date + "���е���־(�������������ʱ���������߳�,��˵������������,�������̫�೬ʱ�̵߳���������Ȼ�ܴ�,��˵����Ҫ����Ⱥ����������������򾡿��ܼ���Զ�̷��ʴ���):<br>\r\n");
		String str_sql = null;
		if (ServerEnvironment.getProperty("MYSELFPORT") != null) { //����ж˿ں�,��ֻ���˱�����!�Ժ���Կ��Ǹ�һ��������,Ȼ�����ͬʱ������������!!!
			str_sql = "select * from pub_threadmonitor where clusterport='" + ServerEnvironment.getProperty("MYSELFPORT") + "' and monitortime like '" + _date + "%' order by serverstarttime desc,monitortime desc";
		} else {
			str_sql = "select * from pub_threadmonitor where monitortime like '" + _date + "%' order by serverstarttime desc,monitortime desc";
		}
		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, str_sql); //
		sb_html.append("<table align=\"left\">\r\n"); //
		sb_html.append("<tr>"); //
		sb_html.append("<td bgcolor=\"E1FFE1\" nowrap>���</td>"); //
		sb_html.append("<td bgcolor=\"E1FFE1\" align=\"center\" width=40 nowrap>�˿ں�</td>"); //
		sb_html.append("<td bgcolor=\"E1FFE1\" align=\"center\" width=125 nowrap>���ʱ��</td>"); //
		sb_html.append("<td bgcolor=\"E1FFE1\" align=\"center\" width=110 nowrap>����������ʱ��</td>"); //
		sb_html.append("<td bgcolor=\"B3E7FF\" align=\"center\" nowrap>��æ<br>JVM(M)</td>"); //
		sb_html.append("<td bgcolor=\"B3E7FF\" align=\"center\" nowrap>����<br>�����(M)</td>"); //
		sb_html.append("<td bgcolor=\"B3E7FF\" align=\"center\" nowrap>�ѿ���<br>�����(M)</td>"); //
		sb_html.append("<td bgcolor=\"B3E7FF\" align=\"center\" nowrap>����������<br>��������(M)</td>"); //
		sb_html.append("<td bgcolor=\"00CCCC\" align=\"center\" nowrap>����<br>��û�</td>"); //
		sb_html.append("<td bgcolor=\"00CCCC\" align=\"center\" nowrap>����<br>ȫ���û�</td>");
		sb_html.append("<td bgcolor=\"00CCCC\" align=\"center\" nowrap>��������<br>�����û�</td>");
		sb_html.append("<td bgcolor=\"FFCACA\" align=\"center\" nowrap>ͬ���߳�<br>�ܼ�</td>");
		sb_html.append("<td bgcolor=\"FFCACA\" align=\"center\" nowrap>ͬ���߳�<br>��ǰ����</td>"); //
		sb_html.append("<td bgcolor=\"FFCACA\" align=\"center\" nowrap>ͬ���߳�<br>��󲢷�</td>"); //
		sb_html.append("<td bgcolor=\"FFCACA\" align=\"center\" nowrap>�����߳�<br>�ܼ�</td>"); //
		sb_html.append("<td bgcolor=\"FFCACA\" align=\"center\" nowrap>�����߳�<br>��ǰ����</td>"); //
		sb_html.append("<td bgcolor=\"FFCACA\" align=\"center\" nowrap>�����߳�<br>��󲢷�</td>"); //
		sb_html.append("<td bgcolor=\"FFCACA\" align=\"center\" nowrap>�����߳���<br>����" + (WLTConstants.THREAD_OVERTIME_VALUE / 1000) + "��<br>��δ��Ӧ</td>"); //
		sb_html.append("<td bgcolor=\"FFCACA\" align=\"center\" nowrap>��ʱ�߳���<br>��Ӧʱ��<br>����" + (WLTConstants.THREAD_OVERTIME_VALUE / 1000) + "��</td>"); //
		sb_html.append("<td bgcolor=\"FFCACA\" align=\"center\" nowrap>�����߳���<br>(����" + (WLTConstants.THREAD_OVERWEIGHT_VALUE / 1024) + "K)</td>"); //
		sb_html.append("<td bgcolor=\"FFCACA\" align=\"center\" nowrap>JVM��������<br>�߳���<br>(����" + WLTConstants.THREAD_OVERJVM_VALUE + "K)</td>"); //
		sb_html.append("<td bgcolor=\"E8D0D0\" align=\"center\" nowrap>���ݿ����ӳ�<br>��æʵ����</td>"); //
		sb_html.append("<td bgcolor=\"E8D0D0\" align=\"center\" nowrap>���ݿ����ӳ�<br>����ʵ����</td>"); //
		sb_html.append("<td bgcolor=\"E8D0D0\" align=\"center\" nowrap>���ݿ����ӳ�<br>�������æµ��</td>"); //
		sb_html.append("<td bgcolor=\"E8D0D0\" align=\"center\" nowrap>���ݿ����ӳ�<br>�������ʵ����</td>"); //
		sb_html.append("</tr>"); //

		for (int i = 0; i < hvs.length; i++) {
			sb_html.append("<tr>\r\n"); //
			sb_html.append("<td>" + (i + 1) + "</td>"); //
			sb_html.append("<td align=\"center\" nowrap>" + hvs[i].getStringValue("clusterport", "") + "</td>"); //
			sb_html.append("<td align=\"center\" nowrap>" + hvs[i].getStringValue("monitortime") + "</td>"); //
			sb_html.append("<td align=\"center\" nowrap>" + hvs[i].getStringValue("serverstarttime") + "</td>"); //����������ʱ��!!!
			sb_html.append("<td align=\"center\" nowrap bgcolor='" + getColorByCount(hvs[i].getLognValue("jvmbusy", 0), 200) + "'>" + hvs[i].getStringValue("jvmbusy", "") + "</td>"); //æ�������,����200M�͸澯..
			sb_html.append("<td align=\"center\" nowrap>" + hvs[i].getStringValue("jvmfree", "") + "</td>"); //���е������
			sb_html.append("<td align=\"center\" nowrap bgcolor='" + getColorByCount(hvs[i].getLognValue("jvmtotal", 0), 500) + "'>" + hvs[i].getStringValue("jvmtotal", "") + "</td>"); //�ѿ����������,����500M�͸澯
			sb_html.append("<td align=\"center\" nowrap bgcolor='" + getColorByCount(hvs[i].getLognValue("evermaxjvmtotal", 0), 800) + "'>" + hvs[i].getStringValue("evermaxjvmtotal", "") + "</td>"); //��������������ڴ�,����800M�;���!!!

			sb_html.append("<td align=\"center\" nowrap bgcolor='" + getColorByCount(hvs[i].getLognValue("onlineuserbusys", 0), 100) + "'>" + hvs[i].getStringValue("onlineuserbusys", "") + "</td>"); //�����û�,�/ȫ��
			sb_html.append("<td align=\"center\" nowrap bgcolor='" + getColorByCount(hvs[i].getLognValue("onlineusertotals", 0), 100) + "'>" + hvs[i].getStringValue("onlineusertotals", "") + "</td>"); //�����û�,�/ȫ��
			sb_html.append("<td align=\"center\" nowrap bgcolor='" + getColorByCount(hvs[i].getLognValue("evermaxonlineusers", 0), 100) + "'>" + hvs[i].getStringValue("evermaxonlineusers", "") + "</td>"); //�������������û���!!!

			sb_html.append("<td align=\"center\" nowrap>" + hvs[i].getStringValue("syncthreadtotals", "") + "</td>"); //ͬ���߳��߳��ܼ�!!!
			sb_html.append("<td align=\"center\" nowrap bgcolor='" + getColorByCount(hvs[i].getLognValue("syncthreadcurrs"), 30) + "'>" + hvs[i].getLognValue("syncthreadcurrs") + "</td>"); //
			sb_html.append("<td align=\"center\" nowrap bgcolor='" + getColorByCount(hvs[i].getLognValue("syncthreadmaxs"), 30) + "'>" + hvs[i].getLognValue("syncthreadmaxs") + "</td>"); //

			sb_html.append("<td align=\"center\" nowrap>" + hvs[i].getStringValue("callthreadtotals", "") + "</td>"); //�����߳��ܼ�!!!
			sb_html.append("<td align=\"center\" nowrap bgcolor='" + getColorByCount(hvs[i].getLognValue("callthreadcurrs"), 30) + "'>" + hvs[i].getLognValue("callthreadcurrs") + "</td>"); //
			sb_html.append("<td align=\"center\" nowrap bgcolor='" + getColorByCount(hvs[i].getLognValue("callthreadmaxs"), 30) + "'>" + hvs[i].getLognValue("callthreadmaxs") + "</td>"); //

			sb_html.append("<td align=\"center\" bgcolor='" + getColorByCount(hvs[i].getLognValue("blockthreads"), 1) + "'><a href=\"./state?type=threadmonitordetail&id=" + hvs[i].getStringValue("id") + "&isend=N\" target=\"_blank\"><font color=\"blue\">" + hvs[i].getLognValue("blockthreads") + "</font></a></td>"); //

			sb_html.append("<td align=\"center\" bgcolor='" + getColorByCount(hvs[i].getLognValue("deferthreads"), 1) + "'><a href=\"./state?type=threadmonitordetail&id=" + hvs[i].getStringValue("id") + "&isend=Y1\" target=\"_blank\"><font color=\"blue\">" + hvs[i].getLognValue("deferthreads") + "</font></a></td>"); //

			sb_html.append("<td align=\"center\" bgcolor='" + getColorByCount(hvs[i].getLognValue("weighterthreads"), 1) + "'><a href=\"./state?type=threadmonitordetail&id=" + hvs[i].getStringValue("id") + "&isend=Y2\" target=\"_blank\"><font color=\"blue\">" + hvs[i].getLognValue("weighterthreads") + "</font></a></td>"); //

			sb_html.append("<td align=\"center\" bgcolor='" + getColorByCount(hvs[i].getLognValue("jvmoverthreads", 0), 1) + "'><a href=\"./state?type=threadmonitordetail&id=" + hvs[i].getStringValue("id") + "&isend=Y3\" target=\"_blank\"><font color=\"blue\">" + hvs[i].getStringValue("jvmoverthreads", "") + "</font></a></td>"); //

			sb_html.append("<td align=\"center\" nowrap bgcolor='" + getColorByCount(hvs[i].getLognValue("dspoolbusy", 0), 5) + "'>" + hvs[i].getStringValue("dspoolbusy", "") + "</td>"); //��æ��
			sb_html.append("<td align=\"center\" nowrap bgcolor='" + getColorByCount(hvs[i].getLognValue("dspooltotal", 0), 8) + "'>" + hvs[i].getStringValue("dspooltotal", "") + "</td>"); //������
			sb_html.append("<td align=\"center\" nowrap bgcolor='" + getColorByCount(hvs[i].getLognValue("dspoolmaxbusy", 0), 5) + "'>" + hvs[i].getStringValue("dspoolmaxbusy", "") + "</td>"); //�������æµ��
			sb_html.append("<td align=\"center\" nowrap bgcolor='" + getColorByCount(hvs[i].getLognValue("dspoolmaxtotal", 0), 8) + "'>" + hvs[i].getStringValue("dspoolmaxtotal", "") + "</td>"); //�������ʵ����
			sb_html.append("</tr>\r\n"); //
		}
		sb_html.append("</table>"); //
		return sb_html.toString(); //
	}

	private String getColorByCount(long _count, int _max) {
		if (_count >= _max) { //�����������,�ʹ����ʾ!!����Ͱ�ɫ��ʾ!!!
			return "#FF9DFF"; //���
		} else {
			return "#FFFFFF"; //
		}
	}

	/**
	 * ����ĳ��ʱ��ȥ���ݿ����ȡĳ������!!
	 * @param _date
	 * @return
	 */
	private String getThreadMonitorDetail(String _id, String _isend) throws Exception {
		if (_id == null) {
			return "���붨��id����!";
		}
		TBUtil tbUtil = new TBUtil(); //
		String str_sql = null;
		if (_isend == null) {
			str_sql = "select * from pub_threadmonitor_b where threadmonitor_id='" + _id + "' order by threadno";
		} else {
			if (_isend.equals("N")) {
				str_sql = "select * from pub_threadmonitor_b where threadmonitor_id='" + _id + "' and iscallend='N' order by threadno";
			} else if (_isend.equals("Y1")) {
				str_sql = "select * from pub_threadmonitor_b where threadmonitor_id='" + _id + "' and iscallend='Y' and overtype like '%��ʱ%' order by threadno";
			} else if (_isend.equals("Y2")) {
				str_sql = "select * from pub_threadmonitor_b where threadmonitor_id='" + _id + "' and iscallend='Y' and overtype like '%����%' order by threadno";
			} else if (_isend.equals("Y3")) {
				str_sql = "select * from pub_threadmonitor_b where threadmonitor_id='" + _id + "' and iscallend='Y' and jvmused>" + WLTConstants.THREAD_OVERJVM_VALUE + " order by threadno";
			}
		}
		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, str_sql);
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("����/��ʱ�Ŀ��ܴ�����������(����10��δ��Ӧ)���߳�:<br>\r\n");
		sb_html.append("<table align=\"left\" cellpadding=\"3\" cellspacing=\"0\">\r\n"); //
		sb_html.append("<tr>"); //
		sb_html.append("<td bgcolor=\"E3E3E3\" nowrap>���</td>"); //
		sb_html.append("<td bgcolor=\"E3E3E3\" nowrap>���ʱ��</td>"); //
		sb_html.append("<td bgcolor=\"E3E3E3\" nowrap>�߳����</td>"); //
		sb_html.append("<td bgcolor=\"E3E3E3\" nowrap>�Ƿ�<br>��Ӧ</td>"); //
		sb_html.append("<td bgcolor=\"E3E3E3\" nowrap>����ʱ��</td>"); //
		sb_html.append("<td bgcolor=\"E3E3E3\" nowrap>������</td>"); //
		sb_html.append("<td bgcolor=\"E3E3E3\" nowrap>���õĹ��ܵ�</td>"); //
		sb_html.append("<td bgcolor=\"E3E3E3\" nowrap>�ͻ��˵��ö�ջ</td>"); //
		sb_html.append("<td bgcolor=\"E3E3E3\">������/������/����</td>"); ///
		sb_html.append("<td bgcolor=\"E3E3E3\" nowrap>��ʱ/����</td>"); //
		sb_html.append("<td bgcolor=\"E3E3E3\" nowrap>��ʱ</td>"); //
		sb_html.append("<td bgcolor=\"E3E3E3\" nowrap>����</td>"); //
		sb_html.append("<td bgcolor=\"E3E3E3\" nowrap>����ǰ<br>JVM</td>"); //
		sb_html.append("<td bgcolor=\"E3E3E3\" nowrap>���ú�<br>JVM</td>"); //
		sb_html.append("<td bgcolor=\"E3E3E3\" nowrap>JVM<br>����</td>"); //
		sb_html.append("<td bgcolor=\"E3E3E3\" nowrap>ִ�еĲ�ѯSQL</td>"); //
		sb_html.append("</tr>"); //
		for (int i = 0; i < hvs.length; i++) {
			sb_html.append("<tr>"); //
			sb_html.append("<td>" + (i + 1) + "</td>"); //
			sb_html.append("<td nowrap>" + hvs[i].getStringValue("monitortime") + "</td>"); //
			sb_html.append("<td nowrap>" + hvs[i].getStringValue("threadno") + "</td>"); //
			sb_html.append("<td nowrap>" + hvs[i].getStringValue("iscallend") + "</td>"); //
			sb_html.append("<td nowrap>" + hvs[i].getStringValue("begintime") + "</td>"); ////
			sb_html.append("<td nowrap>" + hvs[i].getStringValue("calluser") + "</td>"); //
			sb_html.append("<td nowrap>" + tbUtil.replaceAll(hvs[i].getStringValue("callmenuname"), "��", "-><br>") + "</td>"); ////���õĲ˵�����!!!

			sb_html.append("<td nowrap>" + tbUtil.replaceAll(hvs[i].getStringValue("callstack"), "��", "<br>") + "</td>"); //���õĶ�ջ!!!
			sb_html.append("<td>��������<font color=\"blue\">" + hvs[i].getStringValue("servicename") + "</font><br>��������<font color=\"blue\">" + hvs[i].getStringValue("methodname") + "()</font>,����ֵ��<br>" + tbUtil.replaceAll(hvs[i].getStringValue("parameters"), "��", "<br>") + "</td>"); //
			sb_html.append("<td nowrap>" + hvs[i].getStringValue("overtype") + "</td>"); //
			sb_html.append("<td nowrap bgcolor=\"" + getColorByCount(hvs[i].getLognValue("usedsecond", 0), WLTConstants.THREAD_OVERTIME_VALUE) + "\">" + hvs[i].getLognValue("usedsecond") + "����<br>" + (hvs[i].getLognValue("usedsecond") / 1000) + "��</td>"); //

			if (hvs[i].getStringValue("weightersize") == null) {
				sb_html.append("<td nowrap></td>"); ///����!
			} else {
				sb_html.append("<td nowrap bgcolor=\"" + getColorByCount(hvs[i].getLognValue("weightersize", 0), WLTConstants.THREAD_OVERWEIGHT_VALUE) + "\">" + hvs[i].getLognValue("weightersize") + "�ֽ�<br>" + (hvs[i].getLognValue("weightersize") / 1000) + "K</td>"); ///����!
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
			sb_html.append("<td nowrap>" + tbUtil.replaceAll(hvs[i].getStringValue("weightersqls"), "��", "<br>") + "</td>"); //���ص�SQL						
			sb_html.append("</tr>"); //
		}
		sb_html.append("</table>"); //
		return sb_html.toString(); //
	}

	/**
	 * ��ʾ�����û�!!
	 * @return
	 * @throws Exception
	 */
	private String getOnlineUser() throws Exception {
		TBUtil tbutil = new TBUtil(); //
		StringBuffer sb_html = new StringBuffer(); //
		FrameWorkCommServiceImpl commService = new FrameWorkCommServiceImpl(); //
		String[][] str_onlineusers = commService.getServerOnlineUser(); //
		sb_html.append("<br>��ǰ�����û�[" + str_onlineusers.length + "]:<br>\r\n"); //
		sb_html.append("<table align=\"left\">\r\n"); //
		sb_html.append("<tr>\r\n"); //
		sb_html.append("<td>SessionId</td>\r\n"); //
		sb_html.append("<td>IP1</td>\r\n"); //
		sb_html.append("<td>IP2</td>\r\n"); //
		sb_html.append("<td>�û�����</td>\r\n"); //
		sb_html.append("<td>�û�����</td>\r\n"); //
		sb_html.append("<td>��������</td>\r\n"); //
		sb_html.append("<td>��¼ʱ��</td>\r\n"); //
		sb_html.append("<td>���һ�η���ʱ��</td>\r\n"); //
		sb_html.append("<td>ʹ��ʱ��</td>\r\n"); //
		sb_html.append("<td>��ǰʱ��</td>\r\n"); //
		sb_html.append("<td>����ʱ��</td>\r\n"); //
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
	 * ��ʾ�����û�!
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

		sb_html.append("����:<input name=\"date1\" type=\"text\" size=\"15\" onfocus=\"calendar()\" value=\"" + str_curdate + "\" style=\"color: #D78D9C; border: 1 solid #D78D9C; width: 130px; height: 19px;\">\r\n"); //
		sb_html.append("&nbsp;&nbsp;<input type=\"button\" class=\"style_button\" value=\"��ѯ\" onClick=\"JavaScript:self.location='./state?type=userclickmenuhist&date=' + date1.value + ''\">");
		sb_html.append("<br><br>[" + str_curdate + "]�û�����˵���ʷ:<br>\r\n"); //

		HashVO[] hvs_clickmenu = new CommDMO().getHashVoArrayByDS(null, "select * from pub_menu_clicklog  where clicktime like '" + str_curdate + "%' order by clicktime asc"); //
		sb_html.append("<table align=\"left\" width=\"1060\">\r\n"); //
		sb_html.append("<tr>\r\n"); //
		sb_html.append("<td nowrap>���</td>\r\n"); //
		sb_html.append("<td nowrap>�û�����</td>\r\n"); //
		sb_html.append("<td nowrap>��������</td>\r\n"); //
		sb_html.append("<td nowrap>���ʱ��</td>\r\n"); //
		sb_html.append("<td nowrap>�˵�����</td>\r\n"); //
		sb_html.append("<td nowrap>�˵�·��</td>\r\n"); //
		sb_html.append("<td nowrap>����ʱ��</td>\r\n"); //
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
	 * ϵͳָ��
	 * @return
	 * @throws Exception
	 */
	private String getReference() throws Exception {
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("��ϵͳһ��ģ����<br>\r\n"); //
		sb_html.append("�����(law),�Ϲ����(cmp),�����������(lbs),���չ���(risk),���������һ��ģ��,����ȫ������,�κ��˲������ⴴ���µ�һ��ģ����<br>\r\n"); //
		sb_html.append("<br>\r\n"); //

		sb_html.append("��ƽƷ���Ʒ�Ĺؼ���<br>\r\n"); //
		sb_html.append("&nbsp;&nbsp;ƽ̨�Ĺؼ�����:pub_menu(���ܲ˵�),pub_user(��Ա),pub_corp_dept(����),pub_role(��ɫ),pub_templet_1(Ԫԭģ������),pub_templet_1_item(Ԫԭģ���ӱ�),pub_wf_process(���̶���),pub_wf_prinstance(����ʵ��),pub_wf_dealpool(���̴���)<br>\r\n"); //
		sb_html.append("&nbsp;&nbsp;��Ʒ�Ĺؼ�����:law_innerrule(�ڲ��ƶ�),cmp_cmpfile(��ϵ�ļ�),risk_point(���յ��),cmp_event(�����¼�),cmp_report(�Ϲ汨��)<br>\r\n"); //
		sb_html.append("<br>\r\n"); //

		sb_html.append("�������������<br>\r\n"); //
		sb_html.append("&nbsp;&nbsp;����ǲ�Ʒ,����com.pushworld.grc��ʼ,Ȼ��������bs/ui/to,Ȼ����һ��ģ����(lbs/cmp),Ȼ���Ƕ���ģ����(p010/p020/p030),����com.pushworld.grc.bs.lbs.p050<br>"); //
		sb_html.append("&nbsp;&nbsp;�������Ŀ,���Կͻ�����վ������ʼ,����com.cmbc(��������),cn.com.cib(��ҵ����),Ȼ�������bs/ui/to,Ȼ����һ��ģ����(law/cmp/law),Ȼ���Ƕ���ģ����(p010/p020),����cn.com.cib.ui.p030<br>"); //
		sb_html.append("<br>\r\n"); //

		sb_html.append("��ϵͳȨ�޲������˵��<br>\r\n"); //
		sb_html.append("&nbsp;ϵͳȨ�޲����漰���Ĺؼ�����pub_user(��Ա),pub_corp_dept(����),pub_role(��ɫ),pub_post(��λ),pub_user_role(��Ա���ɫ����),pub_user_post(��Ա/����/��λ���߹���)<br>\r\n"); //
		sb_html.append("&nbsp;�����Ĺؼ�������:corptype(��������),�����Բ���Ϊ��,��ǧ�������,��Ϊ�����������е����м��㶼�����ڸ�����<br>\r\n"); //
		sb_html.append("<br>\r\n"); //

		sb_html.append("������������˵��<br>\r\n"); //
		sb_html.append("&nbsp;���������漰������Ҫ����pub_wf_process(���̶����),pub_wf_prinstance(����ʵ����),pub_wf_dealpool(���̴�����Ϣ��)<br>\r\n"); ////
		sb_html.append("&nbsp;��������ԭ�����Ȼ�һ������,Ȼ����һ�ŵ��ݱ�,�ڱ��ж���billtype,busitype,prinstanceid���ֶ�,Ȼ�������̷���(ָ����Ӧ��billtype��busitypeʹ�ö�Ӧ������),Ȼ���ڱ��ж���billtype��busitype��Ĭ��ֵ,���ѡ����ģ��19,���Զ������˹�����Ӧ����!"); //
		sb_html.append("<br>\r\n"); //

		return sb_html.toString(); //
	}

	private String getPageCountFromCache() throws Exception {
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("�ӻ���ȡ����[" + ServerEnvironment.ll_fromCache + "]��,���ӻ���ȡ����[" + ServerEnvironment.ll_notfromCache + "]��,����Ŀǰ��С[" + ServerEnvironment.countSQLCache.size() + "]����������: <a href=\"./state?type=clearPageCountCache\" target=\"_blank\">���Count����</a><br>"); // 
		String[] str_allKeys = (String[]) ServerEnvironment.countSQLCache.keySet().toArray(new String[0]); //
		for (int i = 0; i < str_allKeys.length; i++) { //
			java.util.Hashtable sqlMap = (java.util.Hashtable) ServerEnvironment.countSQLCache.get(str_allKeys[i]); //
			String[] str_sqls = (String[]) sqlMap.keySet().toArray(new String[0]); //
			sb_html.append(str_allKeys[i] + "(" + str_sqls.length + "��)<br>\r\n"); //
			sb_html.append("<table>\r\n"); //
			sb_html.append("<tr><td>���</td><td>SQL���</td><td>��¼��</td></r>\r\n"); //
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
		LinkedHashMap map_second = new LinkedHashMap(); //�����!
		long ll_1 = FalsityThread.ll_falthread_1; //
		long ll_2 = FalsityThread.ll_falthread_2; //
		sb_html.append("�¿��̵߳Ĵ���[" + ServerEnvironment.newThreadCallCount + "],ʵ���¿��̵߳�����[" + ll_1 + "],����[" + ll_2 + "],���[" + (ll_1 - ll_2) + "]<br>\r\n"); //

		String str_isbusy = new BSUtil().isRealBusiCallHtml(); //
		sb_html.append(str_isbusy); //
		Long[] ll_timeList = (Long[]) RemoteCallServlet.callThreadTimeList.toArray(new Long[0]); //�������������!!
		sb_html.append("�������е��ܹ���С[" + ll_timeList.length + "],������ϸ:<br>\r\n"); //
		for (int i = 0; i < ll_timeList.length; i++) {
			long ll_item = ll_timeList[i]; //ʱ��!!!!
			Long ll_second = ll_item / 1000; //�������!!
			if (map_second.containsKey(ll_second)) {
				int li_count = (Integer) map_second.get(ll_second); //
				map_second.put(ll_second, li_count + 1); //����
			} else {
				map_second.put(ll_second, 1); //����!!
			}
		}
		SimpleDateFormat sdf_curr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE); //
		Long[] ll_keys = (Long[]) map_second.keySet().toArray(new Long[0]); //
		for (int i = 0; i < ll_keys.length; i++) {
			String str_time = sdf_curr.format(new Date(ll_keys[i] * 1000)); //���ϲ���!!
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
	 * ��ʾ������ʷ
	 * @return
	 * @throws Exception
	 */
	private String getLoginHist() throws Exception {
		return "��ʾ������ʷ"; //
	}

	/**
	 * ��ʾ������ʷ
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
		sb_html.append("<TITLE>��������״̬</TITLE>\r\n"); //������
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
				//System.exit(0); ////��ǰִ��System.exit(0)��̨������˳��������ε������/2016-06-01��
			} catch (InterruptedException e) {
				e.printStackTrace();
			} //
		}

	}

	/*private   List<InstalledAppInfo> getInstalledAppList(){
		String installedAppStr=ServerEnvironment.getInstance().getProperty("INSTALLAPPS");
		//String  installedAppStr="com.pushworld.ipushgrc.bs.install-��׼��Ʒ;cn.com.crcc.bs.install-��������;";
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
