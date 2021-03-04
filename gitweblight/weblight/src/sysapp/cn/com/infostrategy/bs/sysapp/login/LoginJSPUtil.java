package cn.com.infostrategy.bs.sysapp.login;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.InitParamVO;
import cn.com.infostrategy.to.common.TBUtil;

public class LoginJSPUtil {

	int li_width = 1003; //
	int li_height = 603; //

	private TBUtil tbUtil = null; //

	/**
	 * ע����еĶ�������ǵ�ע����,������̶�������[webpush20100601],������ҵ��Ŀ����������������һ������!
	 * ͬʱװ�����ͻ���ʱ,���������ֻ����һ����!!����ͳ���ע���ֻ��ע��һ��, �����ע������,Ȼ����Ǹ���,Ȼ�������¿ͻ����е�ע����ļ�!�����������ܷ�����!
	 * ��֮,��������ɻ�ı�����,�ô����Ǵ���!!
	 * @return
	 */
	public String getRegeditCode() {
		String str_regcode = ServerEnvironment.getProperty("RegeditCode"); //
		if (str_regcode != null && !str_regcode.trim().equals("")) { //
			return str_regcode; //
		} else {
			return "webpush20100601"; //�������µİ汾��!!!���ǵ����ݾɵĴ󲿷ֶ���18,���ǿ��Ҫ�󽫾ɵ������µ�jre,��ָ������SERVERCLIENTVERSION!!
		}
	}

	//��ʵ��ǰ�������������Ϊ������AppletViewer.jar,����������AppletViewer���Ե�������(ֻ��70K),�����Ժ�����汾���Ժ���ĵĿ����Էǳ�С!!!!ֻ���ڴ�jre1.5����jre1.6������²Ż��õ�!!!!
	public String getClientJREVersion() {
		if (ServerEnvironment.getProperty("SERVERCLIENTVERSION") != null) { //��������˰汾��,�򷵻�֮,�����������п��ܲ������ؿͻ���!!!
			return ServerEnvironment.getProperty("SERVERCLIENTVERSION"); //
		} else {
			return "1.6.0_18"; //�������µİ汾��!!!���ǵ����ݾɵĴ󲿷ֶ���18,���ǿ��Ҫ�󽫾ɵ������µ�jre,��ָ������SERVERCLIENTVERSION!!
		}
	}

	/**
	 * IE����ʱ�ĵ�һ��Htmlҳ��,�����ֵ�¼�û���,����,��¼ͼƬ�ȡ�
	 * @param _request
	 * @param _response
	 * @return
	 */
	public String getLoginPageHtml(HttpServletRequest _request, HttpServletResponse _response) {
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<html>\r\n");
		sb_html.append("<head>\r\n");
		sb_html.append("<TITLE>" + ServerEnvironment.getProperty("PROJECT_NAME") + "</TITLE>\r\n"); //������
		sb_html.append("<META http-equiv=Content-Type content=\"text/html; charset=GBK\">\r\n"); //
		sb_html.append("<STYLE>\r\n");
		sb_html.append(".style_1 { BORDER-RIGHT: #999999 1px solid; BORDER-TOP: #999999 1px solid; BORDER-LEFT: #999999 1px solid; BORDER-BOTTOM: #999999 1px solid; FONT-SIZE: 12px; HEIGHT: 18px; WIDTH:130px; BACKGROUND-COLOR: #FFFFFF }\r\n");
		sb_html.append(".btn_1   { BORDER-RIGHT: #999999 1px solid; BORDER-TOP: #999999 1px solid; BORDER-LEFT: #999999 1px solid; BORDER-BOTTOM: #999999 1px solid; FONT-SIZE: 12px; HEIGHT: 22px; WIDTH:60px; BACKGROUND-COLOR: #EEEEEE }\r\n"); //
		sb_html.append("</STYLE>\r\n"); //

		boolean isRunJava2 = isRunCall2(_request); //һ��������

		sb_html.append("<script language=\"JavaScript\">  \r\n");
		sb_html.append(getJSFunctions(_request, 1)); //�����һ��JS����!!
		sb_html.append(getJS_getAjax());
		sb_html.append(getJS_getCode());
		sb_html.append("<!-- ȡ��ʵ�ʵ�������Url -->\r\n"); //
		sb_html.append("function getRealLoginUrl() { \r\n"); //
		sb_html.append("  var v_usercode = document.form1.usercode.value; \r\n");
		sb_html.append("  var v_userpwd =  document.form1.pwd.value; \r\n"); //
		sb_html.append("  if(v_usercode=='') { alert('�û�������Ϊ��!');   document.form1.usercode.focus(); return ''; } \r\n"); //
		sb_html.append("  if(v_userpwd=='')  { alert('�û����벻��Ϊ��!'); document.form1.pwd.focus(); return ''; } \r\n"); //
		sb_html.append("  var v_url = getClientRequestUrl() + 'logintype=IELogin&usercode=' + v_usercode + '&pwd='  + v_userpwd; \r\n"); //
		if ("Y".equalsIgnoreCase(_request.getParameter("admin"))) { //����ǹ�������
			sb_html.append("  var v_adminpwd =  document.form1.adminpwd.value; \r\n");
			sb_html.append("  if(v_adminpwd=='')  { alert('�������벻��Ϊ��!'); document.form1.adminpwd.focus(); return ''; } \r\n"); //sunfujun/20121210/hm��Ҫadmin=y����±������������
			sb_html.append("  v_url = v_url + '&adminpwd=' + v_adminpwd; \r\n"); //
		} else {
			if (tbUtil.getSysOptionBooleanValue("��¼�����Ƿ�����֤��", false)) {
				sb_html.append("  var v_checkcode =  document.form1.checkcode.value; \r\n"); //
				sb_html.append("  if(v_checkcode=='')  { alert('��֤�벻��Ϊ��!'); return ''; } \r\n"); //
				sb_html.append("  v_url = v_url + '&checkcode=' + v_checkcode; \r\n"); //
			}
		}
		sb_html.append("  return v_url;  \r\n"); //
		sb_html.append("} \r\n\r\n"); //

		sb_html.append("<!-- ���ñ���Java����,��¼���ڷ����޷�ʹ��Cmd���ڲ鿴�쳣��־ʱ,���Խ�C:\\WebPushJRE\\..\\java6.exe����Ϊjavaw6.exe -->\r\n"); //
		sb_html.append("function runJava(){ \r\n"); //���ȷ����ťʱִ�е�JavaScript,��JS�ű���ʵ����ͨ��ע���ע���Э������˱��ص�������!!
		String str_exitIECookie = ServerEnvironment.getProperty("PushMonitorIEExit"); //���IECookie�ı��!!
		if (str_exitIECookie != null && !str_exitIECookie.trim().equals("")) { //���������߼�,���ڼ���֮ǰ�������!�������ʹ�õ����¼��skip/skip2��¼��,Ȼ��ر�IE�ɹ�������JVMɱ��,����ʱCookieû������ɾ��!�����ʱ�����л���������IE��¼,��ͻ���������˳���!!����Ҫ�������!!!
			sb_html.append("  setCookie('" + str_exitIECookie + "',''); //��Ϊ�����˲���PushMonitorIEExit,������IE��ת��,����������¸�Cookie \r\n"); //���Cookie,��Ȼ���˳�ʱ�����˱��,��Ҫ�еط����һ��! ������ܻ������Զ����!
		}
		if (!isRunJava2) { //��һ�ַ�ʽ,������ע����Э��
			sb_html.append("  var v_url = getRealLoginUrl();  \r\n"); //���︺��У��������Ƿ�Ϊ��??
			sb_html.append("  if(v_url==''){ return; }\r\n"); //
			sb_html.append(getCheckCookie(true)); //���Cookie!
			sb_html.append("  self.location='" + getRegeditCode() + "://localjava/call?' + v_url + '&oldversionpar=url;user;pwd;adminpwd';  <!--��תִ�б���Java����--> \r\n"); //֮�����и�oldversionpar��Ϊ�˼���֧����ǰ�Էֺ��������λ���,�����ϵĻ���ʹ���������¼ʱ���ܲ�����!!
		} else { //�ڶ��ַ�ʽ,��ʹ��Activex��ʽ��¼��(�ں���ƹ�����Ŀ�з�����ȫ�������һ�ַ�ʽ�޷�����),���ַ����ĺô��ǲ�������ע����ļ���!!! ��ActivexҲ���������������ز���!�������ַ�ʽ��֧��,���ҿ����л�!!
			sb_html.append("  var v_url = getRealLoginUrl();  \r\n"); //���︺��У��������Ƿ�Ϊ��??
			sb_html.append("  if(v_url==''){ return; }\r\n"); //
			sb_html.append(getRunJava2(false)); //ʹ��Activex��Java
		}
		sb_html.append("  setTimeout(\"closeThisWindow();\",3500); <!--3.5���ر�ԭ������--> \r\n");
		sb_html.append("} \r\n");

		if (!isRunJava2) { //
			sb_html.append("\r\n"); //
			sb_html.append(getJS_downloadJRE()); //������������JRE
		}
		sb_html.append("</script> \r\n"); //

		sb_html.append("</head>\r\n");
		sb_html.append("\r\n");
		// �������ð� ��½���治Ҫ�����ź�/sunfujun
		sb_html.append("<body scroll=no style=\"overflow:hidden\" background=\"./applet/bg.gif\" bgcolor=\"#FFFFFF\" topmargin=0 leftmargin=0 rightmargin=0 bottommargin=0 marginwidth=0 marginheight=0>\r\n");
		sb_html.append("<div id=\"root\" style=\"position:absolute;left:0;top:0;width=" + li_width + ";height=" + li_height + ";\">\r\n");
		//Activex�ؼ�!!
		if (isRunJava2) { //ֻ�еڶ��ַ�ʽ�����ActiveX,��һ�ַ�ʽ�����,�����һ�δ�ҳ��ǳ���!!!
			sb_html.append(getActivexDiv()); //���������Ͻ�,��С(10px*5px),������������,���Բ�Ӱ��UIЧ��!!!
		}

		//ͼƬ
		sb_html.append("  <!-- �������ͼƬ -->\r\n"); //
		sb_html.append("  <div id=\"image\" style=\"position:absolute;left:0;top:0;width=" + li_width + ";height=" + li_height + ";BORDER-RIGHT: #999999 1px solid; BORDER-BOTTOM: #999999 1px solid;\">\r\n");
		sb_html.append("    <img src=\"./applet/" + getLoginBgImgName(_request) + "\"  usemap=\"#newhref\" border=0>\r\n"); //����������ȵ�.width=" + li_width + " height=" + li_height + "
		sb_html.append(getLoginHrefMap()); //���ͼ����������ȵ�!!!
		sb_html.append("  </div>\r\n"); //
		sb_html.append("\r\n"); //
		int li_comp_start_x = 700; //
		int li_comp_start_y = 279; //
		String str_comp_startxy = new TBUtil().getSysOptionStringValue("HTML��¼����ؼ�����ʼλ��", null); //�������������¼ʱ�Ĳ������ã���LoginPanel��Ҳ�и����Ƶ����á���¼����ؼ�����ʼλ�á����ǿͻ��˵�¼ʱ�Ĳ������ã����������/2012-07-03��
		if (str_comp_startxy != null && !str_comp_startxy.trim().equals("")) {
			li_comp_start_x = Integer.parseInt(str_comp_startxy.substring(0, str_comp_startxy.indexOf(","))); //
			li_comp_start_y = Integer.parseInt(str_comp_startxy.substring(str_comp_startxy.indexOf(",") + 1, str_comp_startxy.length())); //
		}
		sb_html.append("  <!-- �����,X,Y΢������[HTML��¼����ؼ�����ʼλ��]=[" + str_comp_startxy + "] -->\r\n"); //
		sb_html.append("  <div id=\"div_form\" style=\"position:absolute;left:" + (li_comp_start_x + 0) + ";top:" + (li_comp_start_y + 0) + ";width=275;height=125;\">\r\n"); //������������Ϊ��λ��Ū����Ҫ��10��40
		sb_html.append("    <form name=\"form1\" action=\"JavaScript:runJava();\" method=\"post\">\r\n"); //��Ȼ�ύ����ҳ��
		sb_html.append("    <table border=0 style=\"WIDTH:275px\">\r\n");
		sb_html.append("      <tr><td align=\"right\" width=\"40%\"><font size=2>�û���:</font></td><td width=\"60%\"><input name=\"usercode\" type=\"text\"     class=\"style_1\"></td></tr>\r\n");
		sb_html.append("      <tr><td align=\"right\" width=\"40%\"><font size=2>����:  </font></td><td width=\"60%\"><input name=\"pwd\"      type=\"password\" class=\"style_1\"></td></tr>\r\n");
		if ("Y".equalsIgnoreCase(_request.getParameter("admin"))) {
			sb_html.append("      <tr><td align=\"right\" width=\"40%\"><font size=2>��������:  </font></td><td width=\"60%\"><input name=\"adminpwd\" type=\"password\" class=\"style_1\" width=120\"></td></tr>\r\n"); //
		} else {
			if (tbUtil.getSysOptionBooleanValue("��¼�����Ƿ�����֤��", false)) {
				sb_html.append("      <tr><td align=\"right\" width=\"40%\"><font size=2>��֤��: </font></td><td width=\"60%\"><input name=\"checkcode\" type=\"text\" class=\"style_1\" width=60\"></tr>\r\n"); //
				sb_html.append("      <tr><td align=\"right\" width=\"40%\"></td><td width=\"60%\"><a href=\"#\"  id=\"info1\" style=\"dispaly:none;\"\"><a href=\"#\" title=\"��ȡ��֤��\" id=\"numLink\" onclick=\"getCode()\"><font size=2>��ȡ��֤��</font></a></td></tr>\r\n"); //
			}
		}
		if (ServerEnvironment.getInnerSys() != null && ServerEnvironment.getInnerSys().length > 0) { //�������ϵͳ,�������ϵͳ�˵�!���������аѹ��̽�����ҵ���뱾����һ��!!��������һ��ϵͳ,ֻ��Ҫ������!!!
			sb_html.append("      <tr><td align=\"right\" width=\"40%\"><font size=2>ϵͳ:  </font></td><td width=\"60%\">" + getAllInnerSys(_request) + "</td></tr>\r\n"); //
		}
		sb_html.append("      <tr><td align=\"right\" width=\"40%\">&nbsp;</td><td align=\"left\" width=\"60%\">\r\n");
		sb_html.append("        <input class=\"btn_1\" type=\"submit\" value=\"��¼\" name=\"Submit\">&nbsp;\r\n");
		sb_html.append("        <input class=\"btn_1\" type=\"Reset\"  value=\"���\" name=\"Reset\">&nbsp;\r\n"); //��Ҫ��հ�ť��
		sb_html.append("      </td></tr>\r\n");
		sb_html.append("      <tr><td colspan=2>&nbsp;</td></tr>\r\n");
		sb_html.append("      <tr><td colspan=2 align=\"right\">\r\n");
		sb_html.append("        <a href=\"./help/webpushjre.exe\"><font size=2 color=\"blue\" title=\"����Oracle��˾�ٷ���׼JRE,���Ѱ�װ�������ٰ�װ!\r\n���й�ͳ��,70%����ҵ�û����Ѱ�װJava����!\r\n������װ���̴�Լһ��������!\">Java���л���</font></a>&nbsp;\r\n"); //��ǰ����Servlet�Ļ���ʹ�����ظ���Щ,������������ũ���з���̫������ʱServlet����ס��,��ֱ�Ӵ�apache���ط�����!�������ﻹ�Ǹĳ�ֱ������!��Ϊ���Ǵ�apache�����ؾ�̬�ļ�,�Ƿǳ����!!��WebLogic���Լ��������!
		sb_html.append("        <a href=\"./help/loginhelp.html\" target=\"_blank\"><font size=2 color=\"blue\">�� ¼ ˵ ��</font></a>&nbsp;\r\n");
		sb_html.append("      </td></tr>\r\n");
		sb_html.append("    </table>\r\n");
		sb_html.append("   </form>\r\n"); //
		sb_html.append("  </div>\r\n");
		sb_html.append("</div>\r\n");

		if (!isRunJava2) { //ֻ�е�һ�ֲ������ʾ���ز���Ĳ�,�ڶ��ַ�ʽ����ֱ����Delphi��Activex�н��д����!!!
			sb_html.append(getPopDiv()); //�����Ĳ�!!!
		}
		sb_html.append("</body>\r\n");
		sb_html.append("</html>\r\n"); //
		return sb_html.toString();
	}

	public String getSingleLogin(HttpServletRequest _request, HttpServletResponse _response) throws UnsupportedEncodingException {
		return getSingleLogin(_request, _response, true, null); //
	}

	/**
	 * �����¼,����˵��ֱ�ӵ�¼�Ľ����߼�,��������һ��Htmlҳ��,ֱ�����<applet></applet>
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public String getSingleLogin(HttpServletRequest _request, HttpServletResponse _response, boolean _isClose, String _text) throws UnsupportedEncodingException {
		_request.setCharacterEncoding("GBK"); //
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<html>\r\n");
		sb_html.append("<head>\r\n");
		sb_html.append("<TITLE>" + ServerEnvironment.getProperty("PROJECT_NAME") + "</TITLE>\r\n"); //������
		sb_html.append("<META http-equiv=Content-Type content=\"text/html; charset=GBK\">\r\n"); //
		sb_html.append("<STYLE>\r\n");
		sb_html.append(".p {  FONT-SIZE: 12px; COLOR: #0000FF }\r\n");
		sb_html.append(".style_1 { BORDER-RIGHT: #999999 1px solid; BORDER-TOP: #999999 1px solid; BORDER-LEFT: #999999 1px solid; BORDER-BOTTOM: #999999 1px solid; FONT-SIZE: 12px; HEIGHT: 18px; WIDTH:150px; BACKGROUND-COLOR: #FFFFFF }\r\n");
		sb_html.append("</STYLE>\r\n");
		if ("Y".equals(ServerEnvironment.getProperty("SINGLEENCRYPT"))) {
			String sn = _request.getParameter("encrypt");
			boolean flag = true;
			if (TBUtil.isEmpty(sn)) {
				flag = false;
				sb_html.append("�����¼У��ʧ��(1)...</span>&nbsp;&nbsp;"); //����Ҫ��һ������,ָ�������¼ʱ�ĵ���ʾ��Ϣ!
			}else{
				sn = URLEncoder.encode(sn);
			}
			String usercode = _request.getParameter("usercode");
			if((TBUtil.isEmpty(usercode))){
				flag = false;
				sb_html.append("�����¼У��ʧ��(2)...</span>&nbsp;&nbsp;"); //����Ҫ��һ������,ָ�������¼ʱ�ĵ���ʾ��Ϣ!
			}
			if(!sn.equals(ServerEnvironment.getSingleEncrypt(usercode))){
				flag = false;
				sb_html.append("�����¼У��ʧ��(3)...</span>&nbsp;&nbsp;"); //����Ҫ��һ������,ָ�������¼ʱ�ĵ���ʾ��Ϣ!
			}
			ServerEnvironment.removeSingleEncrypt(usercode);
			if(!flag){
				sb_html.append("</body>\r\n"); //
				sb_html.append("</html>\r\n"); //
				return sb_html.toString(); //
			}
		}
		sb_html.append("<script language=\"JavaScript\">  \r\n"); //
		sb_html.append(" var v_intervalID=-1;  //�����洢����ѭ�����󷵻ص�id,��ֹͣʱ���봫�����ID��Ϊ���� \r\n"); //
		sb_html.append(" var v_temp1=0; //�ṩ�����������ַ���ʱ����,��Ϊѭ���߼��п϶����ܻ�����һ����ʱ���������洢��! \r\n"); //
		sb_html.append(" var v_temp2=0; \r\n"); //
		sb_html.append(" var v_temp3=''; \r\n"); //
		sb_html.append(" var v_temp4=''; \r\n"); //

		sb_html.append(getJSFunctions(_request, 2)); //���һ��JS����!!
		String str_exitIECookie = ServerEnvironment.getProperty("PushMonitorIEExit"); //���IECookie�ı��!!
		String str_refreshJS = getTBUtil().getSysOptionStringValue("�����¼��תҳ��JSˢ���̴߳���", null); //������Ŀ����������EACע��ʱҪͨ����̬��ѭ��ˢ��ȥ�ж�ע�����������,�����������ȫ��Ŀ���Ի���,���Ա��������!��Ϊ���ִ������,���Դ���ϵͳ����������!!
		boolean isRunJava2 = isRunCall2(_request); //
		int li_closetime = 2500; //Ĭ��2.5���ر�
		if (_request.getParameter("closetime") != null) {
			li_closetime = Integer.parseInt(_request.getParameter("closetime")); //
		}
		if (!isRunJava2) { //��һ�ַ�ʽ
			sb_html.append("\r\n<!-- ִ�б���Java���� -->\r\n"); //
			sb_html.append("function runJava(){ \r\n"); //0.5���Ӻ�ֱ�ӵ��ø�JS�ű�
			sb_html.append(getCheckCookie(false)); //���Cookie!
			if (str_exitIECookie != null && !str_exitIECookie.trim().equals("")) {
				sb_html.append("   setCookie('" + str_exitIECookie + "',''); //��Ϊ�����˲���PushMonitorIEExit,������IE��ת��,����������¸�Cookie \r\n"); //���Cookie,��Ȼ���˳�ʱ�����˱��,��Ҫ�еط����һ��! ������ܻ������Զ����!
			}
			if (str_refreshJS != null && !str_refreshJS.trim().equals("") && !_isClose) { //�������,�����ǲ��رյ�.�رյĻ���û��ѭ��������!
				sb_html.append("   v_intervalID=setInterval(\"refreshThread()\",500); //��Ϊ������ˢ���̴߳��� \r\n"); //ÿ0.5��ˢ��һ��
			}
			sb_html.append("   document.getElementById(\"ProcessInfo\").innerHTML='ϵͳ���ڴ�ҳ��...[' + getCurrTime('2') + ']'; \r\n"); //�ڵ��ñ���֮ǰ,�����ǰʱ��,Ȼ����Java������Ҳ��ʱ��,ͨ�����߲���,��֪������ϵͳͨ��ע�����𱾳���Ҫ�೤ʱ��!! ��Ϊ����Win7��������,�����ǲ�����һ����?
			sb_html.append("   self.location='" + getRegeditCode() + "://localjava/call?' + getClientRequestUrl() + 'oldversionpar=url;user;pwd;adminpwd';  //��תִ�б���Java���� \r\n"); //֮�����и�oldversionpar��Ϊ�˼���֧����ǰ�Էֺ��������λ���,�����ϵĻ���ʹ���������¼ʱ���ܲ�����!!
			if (_isClose) { //��������¼����Ҫ�ر��Լ�
				sb_html.append("   setTimeout(\"closeThisWindow();\"," + li_closetime + "); //3���ر�ԭ������ \r\n"); //���ǵ���һ�η�������Java�����е���,����ʱ��ų�һ��,���������ÿͻ�����ϵͳ"������"!!!!
			}
			sb_html.append("} \r\n\r\n");
		} else {
			sb_html.append("\r\n<!-- ʹ��Activex��Java,�������¼���ڷ����쳣�޷��鿴��־ʱ,���Խ�C:\\WebPushJRE\\..\\java6.exe����Ϊjavaw6.exe -->\r\n"); //
			sb_html.append("function runJava(){ \r\n");
			if (str_exitIECookie != null && !str_exitIECookie.trim().equals("")) {
				sb_html.append("   setCookie('" + str_exitIECookie + "',''); //��Ϊ�����˲���PushMonitorIEExit,������IE��ת��,����������¸�Cookie \r\n"); //���Cookie,��Ȼ���˳�ʱ�����˱��,��Ҫ�еط����һ��! ������ܻ������Զ����!
			}
			if (str_refreshJS != null && !str_refreshJS.trim().equals("") && !_isClose) { //�������,�����ǲ��رյ�.
				sb_html.append("   v_intervalID=setInterval(\"refreshThread()\",500); \r\n"); //ÿ0.5��ˢ��һ��
			}
			sb_html.append("   document.getElementById(\"ProcessInfo\").innerHTML='ϵͳ���ڴ�ҳ��..[' + getCurrTime('2') + ']';\r\n"); //
			sb_html.append(getRunJava2(true)); //ʹ��Activex��Java
			if (_isClose) { //��������¼����Ҫ�ر��Լ�
				sb_html.append(" else {\r\n");
				sb_html.append("   setTimeout(\"closeThisWindow();\"," + li_closetime + "); //3���ر�ԭ������ \r\n");
				sb_html.append("  }\r\n");
			}
			sb_html.append("} \r\n\r\n");
		}

		sb_html.append("<!-- �˳�����ʱ����ճ����д�������,��������Ŀ����EAC����ʱ��Ҫ��� -->\r\n"); //
		sb_html.append("function unLoadWebPush(){ \r\n");
		if (!_isClose && str_exitIECookie != null && !str_exitIECookie.trim().equals("")) {
			String str_closeWinJS = getTBUtil().getSysOptionStringValue("�����¼�رմ���JS����", null);
			if (str_closeWinJS != null && !str_closeWinJS.trim().equals("")) { //��������˲�����ʹ��֮
				sb_html.append(str_closeWinJS + "\r\n"); //
			} else { //
				sb_html.append("  if(window.event.clientX<0 || window.event.clientY<0 || event.altKey) { //��������˵���������رմ�����ˢ�´���ֻ��ͨ������ж�,ֻ���е��̬! \r\n"); //��Ϊ���������رպ�Ż����x,y��С��0
				sb_html.append("    setCookie('" + str_exitIECookie + "','exit'); //����Cookieֵ! \r\n"); //����cookie���Ϊexit!��ǰ����self.location,�򵯳����ڵ�,������֤��������!
				sb_html.append("  } \r\n"); //
			}
		} else {
			sb_html.append("<!-- û����weblight.xml�������PushMonitorIEExit,���Բ����߼�! -->\r\n");
		}
		sb_html.append("} \r\n");

		sb_html.append("\r\n<!-- ��������˲���[�����¼��תҳ��JSˢ���̴߳���],����������Jaav�󲻹رձ�����,����ѭ�����ñ�����! -->\r\n"); //
		sb_html.append("function refreshThread(){ \r\n");
		if (str_refreshJS != null && !str_refreshJS.trim().equals("") && !_isClose) {
			sb_html.append(str_refreshJS); //����
		}
		sb_html.append("\r\n} \r\n");

		sb_html.append("\r\n<!-- ֹͣˢ���߳�,�����洦���߼��п϶�������һ���ж϶�����ֹͣ��! -->\r\n");
		sb_html.append("function stopRefreshThread(){ \r\n");
		sb_html.append("   clearInterval(v_intervalID); \r\n");
		sb_html.append("} \r\n");

		if (!isRunJava2) { //
			sb_html.append("\r\n"); //
			sb_html.append(getJS_downloadJRE()); //������������JRE
		}
		sb_html.append("</script> \r\n"); //
		sb_html.append("</head>\r\n");
		sb_html.append("<body style=\"background-color:#E7EEF8;background-image:URL(./applet/main_bgimg.gif);background-repeat:no-repeat;\" onLoad=\"runJava();\" onbeforeUnload=\"unLoadWebPush();\">\r\n");
		sb_html.append("<span id=\"ProcessInfo\" style=\"FONT-SIZE:12px; COLOR:#000000\">"); //
		if (_text == null) {
			sb_html.append("�����¼������,���Ժ�...</span>&nbsp;&nbsp;"); //����Ҫ��һ������,ָ�������¼ʱ�ĵ���ʾ��Ϣ!
		} else {
			sb_html.append(_text + "</span>&nbsp;&nbsp;");
		}
		sb_html.append("<span id=\"hidespan\" style=\"FONT-SIZE:12px; COLOR:#000000\" onclick=\"alert(document.cookie);\">&nbsp;&nbsp;&nbsp;</span>"); //����͵͵��һ����������span,���ڵ����鿴cookie!���û�������,�����ǿ���
		sb_html.append("<br>\r\n");

		sb_html.append("<p style=\"FONT-SIZE:12px; COLOR:#000000\">\r\n");
		String str_req_info = _request.getParameter("FRAME_TITLE"); //
		if (str_req_info != null) {
			try {
				str_req_info = new String(str_req_info.getBytes("ISO-8859-1"), "GBK"); //�������Ҫתһ��!!!������������,�ں�����Ŀ�д���ǰ��ת����16����,���Ը��ȶ�,���������д���Ҳ��û�������!
				//System.out.println("�ͻ��˴���Ĳ���[" + str_parKeys[i] + "]=[" + str_value + "]"); //Debug�����Ƿ�������??
			} catch (Exception ex) {
				System.err.println("LoginJSPUtil.getSingleLogin()ת���ַ������쳣:" + ex.getClass() + ":" + ex.getMessage()); //
			}
			sb_html.append("ϵͳ���ص�ҳ����:��" + str_req_info + "��<br>\r\n");
		}
		sb_html.append("</p>"); //

		//������Զ���Ľ���!!!(Ӧ���ǲ���Html)
		String str_custhtml = (String) ServerEnvironment.getInstance().getCustMapValue("singlemain.htm"); //������
		if (str_custhtml == null) { //���û��,�򴴽������뻺��!
			InputStream ins_mainhtml = this.getClass().getResourceAsStream("/singlemain.html"); //���������ļ�,���������ļ�!!!
			if (ins_mainhtml != null) { //
				String str_html = getTBUtil().readFromInputStreamToStr(ins_mainhtml); //��һ��!
				str_custhtml = "\r\n\r\n<!-- ��%ClassPath%/singlemain.html�ļ��ж�ȡ������ -->\r\n" + str_html + "\r\n<!-- ��%ClassPath%/singlemain.html�ļ��ж�ȡ�����ݽ���!! -->\r\n\r\n"; //sb_html.toString(); //
				ServerEnvironment.getInstance().putCustMap("singlemain.htm", str_custhtml); //��������!!!
			} else {
				str_custhtml = "\r\n<!-- ClassPath��·��û���Զ����singlemain.htm,�ʺ���(�����Ҫ����������ͻ��Զ���İ���˵��,�ʹ���һ��singlemain.htm�ļ�,������WEB-INF/classesĿ¼��).. -->\r\n"; //
				ServerEnvironment.getInstance().putCustMap("singlemain.htm", str_custhtml); //
			}
		}
		sb_html.append(str_custhtml); //

		if ("Y".equalsIgnoreCase(ServerEnvironment.getProperty("ISRemindLoadType"))) { //�Ƿ��������ּ��ط�ʽ!
			sb_html.append("<br><br>\r\n"); //
			sb_html.append("<p class=\"p\">\r\n");
			sb_html.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��ǰ��¼��ʽ��[" + (isRunJava2 ? "�ڶ��ֵ�¼��ʽ" : "Ĭ�ϵ�¼��ʽ") + "],������ܽ���ϵͳ,��ѡ������һ�ֵ�¼��ʽ����!<br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\r\n");
			sb_html.append("<input type=\"button\" class=\"style_1\" value=\"Ĭ�ϵ�¼��ʽ\" onclick=\"JavaScript:setCookie('WebPushRunJavaType','1');alert('����Ĭ�ϵ�¼��ʽ�ɹ�,����Ҫ���µ�¼������Ч!')\">&nbsp;&nbsp;\r\n"); //
			sb_html.append("<input type=\"button\" class=\"style_1\" value=\"�ڶ��ֵ�¼��ʽ\" onclick=\"JavaScript:setCookie('WebPushRunJavaType','2');alert('���õڶ��ַ�ʽ�ɹ�,����Ҫ���µ�¼������Ч!')\"><br>\r\n"); //
			sb_html.append("</p>"); //
		}

		//sb_html.append("<table width=\"100%\"><tr><td align=\"right\"><img src=\"./applet/main_bgimg.jpg\"></td></tr></table>\r\n"); //���ͼƬ!!!
		if (!isRunJava2) { //��һ�ַ�ʽ
			sb_html.append(getPopDiv()); //
		} else {
			sb_html.append(getActivexDiv()); //
		}

		sb_html.append("\r\n\r\n<!--"); //
		sb_html.append("���в���:\r\n"); //
		Map parMap = _request.getParameterMap(); //
		String[] str_keys = (String[]) parMap.keySet().toArray(new String[0]); ////
		for (int i = 0; i < str_keys.length; i++) {
			String str_value = ((String[]) parMap.get(str_keys[i]))[0]; //
			try {
				str_value = new String(str_value.getBytes("ISO-8859-1"), "GBK"); //�������Ҫתһ��!!!������������,�ں�����Ŀ�д���ǰ��ת����16����,���Ը��ȶ�,���������д���Ҳ��û�������!
			} catch (Exception ex) {
				System.err.println("LoginJSPUtil.getSingleLogin()ת���ַ������쳣:" + ex.getClass() + ":" + ex.getMessage()); //
			}
			sb_html.append("[" + str_keys[i] + "]=[" + str_value + "]\r\n");
		}
		sb_html.append("-->\r\n\r\n"); //
		sb_html.append("</body>\r\n"); //
		sb_html.append("</html>\r\n"); //
		return sb_html.toString(); //
	}

	/**
	 * ��ǰͨ������IE�˳�ʱ����Java����д��ճ�����ر�JavaSwing,���������鷢��������,��Ϊ��ȫԭ�򴥷�����,���رմ��ں�Ȳ���ͨ��self.location=webpush20100601://localjava?������,Ҳ����ͨ����������������!
	 * ���Ժ����������˰취�����˳�ʱ��IE��дCookieֵ!Ȼ����Swing��ͨ��JDIC�ں�̨����һ��Html,���html�в���ˢ��cooike�е�ֵ,����IE�˳�ʱд��ֵ,������ͨ���޸Ĵ��ڱ�����֪ͨSwing,Swing�ӵ�����˳�Java!!
	 * ������Ҫ���һ������ˢ�µ�Html,���Ǳ�����ʵ�ֵ��߼�
	 * @return
	 */
	public String getMonitorIECookieHtml() {
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<html>\r\n");
		sb_html.append("<head>\r\n"); //
		sb_html.append("<META http-equiv=Content-Type content=\"text/html; charset=GBK\">\r\n"); //
		sb_html.append("<title>MyTitle</title>\r\n"); //
		String str_MonitorCookieKey = ServerEnvironment.getProperty("PushMonitorIEExit"); //������weblight.xml�ж���
		if (str_MonitorCookieKey != null && !str_MonitorCookieKey.trim().equals("")) {
			sb_html.append("<script language=\"JavaScript\">\r\n\r\n"); //
			sb_html.append("<!-- ����ҳ��ʱ���õķ��� -->\r\n"); //
			sb_html.append("function myOnLoad(){ \r\n");
			sb_html.append(" refreshCookie(); \r\n"); //�ȵ�һ��!������ͺ�һ��ѭ��ʱ��
			sb_html.append(" setInterval(\"refreshCookie()\",500); \r\n"); //ÿ0.5��ˢ��һ��
			sb_html.append("} \r\n\r\n"); // 

			sb_html.append("<!-- ˢ��ȡCookieֵ -->\r\n"); //
			sb_html.append("function refreshCookie(){ \r\n"); //
			sb_html.append("  var newDate = new Date(); \r\n"); //
			sb_html.append("  var v_currtime = newDate.getFullYear() + '-' + (newDate.getMonth()+1) + '-' + newDate.getDate() + ' ' + newDate.toLocaleTimeString() + ':' + newDate.getMilliseconds(); \r\n"); //
			sb_html.append("  var v_cookievalue = getCookie('" + str_MonitorCookieKey + "'); \r\n"); //
			sb_html.append("  document.form1.text1.value='[' + v_currtime + '],Cookieֵ[" + str_MonitorCookieKey + "]=[' + v_cookievalue + ']'; \r\n"); //ͨ�������֪��������û����ˢ��!
			sb_html.append("  if(v_cookievalue=='exit'){ \r\n"); //
			sb_html.append("     var v_oldTitle = document.title;\r\n"); //ԭ���ı���
			sb_html.append("     if(v_oldTitle != '" + str_MonitorCookieKey + "@exit') { \r\n"); //�����ԭ���ı���һ���򲻴���,�������ɴ������,���JDIC���߳�Ū��? ������һ����ഥ��3�ε��߼�!!
			sb_html.append("       document.title='" + str_MonitorCookieKey + "@exit'; \r\n"); //�������ñ���,ÿ��һ�����д���,�ͻᴥ����Swing��һ��,��������Ҫ��һ���ж�,�����û�仯�򲻴���!!
			sb_html.append("     }\r\n"); //
			sb_html.append("  } \r\n"); //
			sb_html.append("} \r\n\r\n"); // 

			sb_html.append(getJS_getCookie()); //ȡCookieֵ!!!

			sb_html.append("</script> \r\n");
			sb_html.append("</head>\r\n");
			sb_html.append("<body onload='javaScript:myOnLoad();'>\r\n"); //
			sb_html.append("<form name=\"form1\"><input type=\"text\" name=\"text1\" value=\"����ʱ��:[]\" size=60><input type=\"button\" value=\"�鿴����Cookie\" onClick=\"alert(document.cookie);\"></form>��ҳ����ˢ�»�ȡcookie�е�ֵ,��������ض�ֵ�ʹ���!\r\n"); //
		} else {
			sb_html.append("</head>\r\n");
			sb_html.append("<body>\r\n");
			sb_html.append("weblight.xml��û�ж������[PushMonitorIEExit],���Բ�����ˢ���߼�!\r\n");
		}
		sb_html.append("</body>\r\n"); //
		sb_html.append("</html>\r\n"); //
		return sb_html.toString(); //
	}

	//�ж��Ƿ��Եڶ��ַ�ʽ����,��ʹ��Activex����Java�ķ�ʽ!!!
	private boolean isRunCall2(HttpServletRequest _request) {
		String str_par = _request.getParameter("runjava"); //
		if ("2".equalsIgnoreCase(str_par)) { //���ǿ��ָ����,��ֱ�ӷ���True
			return true;
		}

		//���û��ָ��,�ٿ�cookie!
		Cookie[] cookies = _request.getCookies(); //ȡ������Cookie
		if (cookies != null && cookies.length > 0) {
			for (int i = 0; i < cookies.length; i++) {
				if ("WebPushRunJavaType".equals(cookies[i].getName()) && "2".equals(cookies[i].getValue())) { //���cookie�и�������WebPushRunJavaType,��ֵΪ2,����Ϊ�ǵڶ���!!
					return true;
				}
			}
		}
		return false; //������Ϊ�ǵ�һ��
	}

	//��¼���汳��ͼƬ����!!
	private String getLoginBgImgName(HttpServletRequest _request) {
		String str_logingifname = "login_default.gif"; //
		if (_request.getParameter("isys") != null) { //���ָ����isys,��ָ����ָϵͳ
			str_logingifname = "login_" + _request.getParameter("isys") + ".gif"; //
		} else {
			if (ServerEnvironment.getInnerSys() != null && ServerEnvironment.getInnerSys().length > 0) {
				str_logingifname = "login_" + ServerEnvironment.getInnerSys()[0][0] + ".gif"; //�����������ϵͳ,��Ĭ��ʹ�õ�һ����ϵͳ�ı���,Ҳ����ĸϵͳ�ı���!
			} else {
				if (ServerEnvironment.getProperty("PROJECT_SHORTNAME") != null && !ServerEnvironment.getProperty("PROJECT_SHORTNAME").trim().equals("")) {
					str_logingifname = "login_" + ServerEnvironment.getProperty("PROJECT_SHORTNAME") + ".gif"; //
				}
			}
		}
		return str_logingifname;
	}

	//��һ����¼����ϵ������ȵ�(��������ȳ�����!!
	private String getLoginHrefMap() {
		StringBuilder sb_html = new StringBuilder(""); //
		String[][] str_hrefs = ServerEnvironment.getLoginHref(); //
		if (str_hrefs != null && str_hrefs.length > 0) {
			sb_html.append("    <map name=\"newhref\">\r\n"); ////ѭ����������ȵ�..
			for (int i = 0; i < str_hrefs.length; i++) {
				String str_x = str_hrefs[i][0]; //
				String str_y = str_hrefs[i][1]; //
				String str_width = str_hrefs[i][2]; //
				String str_height = str_hrefs[i][3]; //
				String str_alt = str_hrefs[i][4]; //
				String str_url = str_hrefs[i][5]; //
				String str_x2 = "" + (Integer.parseInt(str_x) + Integer.parseInt(str_width));
				String str_y2 = "" + (Integer.parseInt(str_y) + Integer.parseInt(str_height));
				sb_html.append("      <area shape=\"rect\" coords=\"" + str_x + "," + str_y + "," + str_x2 + "," + str_y2 + "\" href=\"." + str_url + "\" target=\"_blank\" alt=\"" + str_alt + "\" border=1/>\r\n");
			}
			sb_html.append("    </map>\r\n"); //
		}
		return sb_html.toString(); //
	}

	//������ϵͳ,��һ�����˵�!!!
	private String getAllInnerSys(HttpServletRequest _request) {
		StringBuilder sb_html = new StringBuilder(""); //
		sb_html.append("      <select size=\"1\" name=\"isys\" style=\"width=150\">\r\n"); //
		for (int i = 0; i < ServerEnvironment.getInnerSys().length; i++) { //selected
			if (_request.getParameter("isys") != null && _request.getParameter("isys").equals(ServerEnvironment.getInnerSys()[i][0])) {
				sb_html.append("      <option selected value=\"" + ServerEnvironment.getInnerSys()[i][0] + "\">" + ServerEnvironment.getInnerSys()[i][1] + "</option>\r\n");
			} else {
				sb_html.append("      <option  value=\"" + ServerEnvironment.getInnerSys()[i][0] + "\">" + ServerEnvironment.getInnerSys()[i][1] + "</option>\r\n");
			}
		}
		sb_html.append("      </select>\r\n");
		return sb_html.toString(); //
	}

	//	

	//У���Ƿ���Cookie,Ȼ������Ƿ񵯳����ؿ�!!
	private String getCheckCookie(boolean _isLogin) {
		//���Weblight.xml���������������, �Ͳ���Cookie��
		//����������ж������Cookie������
		if ("N".equalsIgnoreCase(ServerEnvironment.getProperty("CHECKCOOKIE"))) {
			return "";
		}

		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("  var v_cookie = getCookie('WebPushJREInstalled'); \r\n"); //���Ƿ���Cookieֵ???
		sb_html.append("  if(v_cookie=='') { \r\n"); //���CookieΪ��,����ʾ��װ!!!
		sb_html.append("    var v_div = document.getElementById('div_pop');\r\n"); //
		sb_html.append("    v_div.style.left=" + (_isLogin ? "500" : "50") + ";\r\n"); //  
		sb_html.append("    v_div.style.top=" + (_isLogin ? "300" : "50") + ";\r\n"); // 
		sb_html.append("    v_div.style.display = '';\r\n"); //��ʾ��
		sb_html.append("    return; \r\n"); //
		sb_html.append("  }\r\n"); //
		return sb_html.toString(); //
	}

	//����JavaScript����!!
	private String getJSFunctions(HttpServletRequest _request, int _type) {
		StringBuilder sb_html = new StringBuilder(); //
		String str_logintype = _request.getParameter("logintype"); //
		if (_type == 1 || _type == 2) { //
			sb_html.append("<!--ȡ�����������ĵ�ַ-->\r\n"); //
			sb_html.append("function getClientRequestUrl() { \r\n"); //
			sb_html.append("  var v_url = String(window.location); \r\n"); //
			sb_html.append("  if(v_url.indexOf('#')>0) {v_url = v_url.substring(0, v_url.indexOf('#'));}\r\n");
			sb_html.append("  if(v_url.indexOf('?')>0) {\r\n"); //������ʺ�,˵�����в���,���ٽӲ����ͱ�����&
			sb_html.append("    v_url = v_url + '&';\r\n"); //
			sb_html.append("  } else { \r\n"); //
			sb_html.append("    v_url = v_url + '?';\r\n"); //
			sb_html.append("  } \r\n"); //

			//�������м���ʱ��Ҫ��������λ�����С,����Ƕ���������λ��,�γ�һ�ּټ��ɵ�Ч��!!ֻҪ������url�д������FRAME_X,FRAME_Y,FRAME_WIDTH,FRAME_HEIGHT�Ȳ����Ϳ���,����ʵ�Ͽ��Բ�����ֱ��������ȡ��!!�Ӷ�ʡȥ���ο�����Ա�Ĺ�����!!!
			if (_type == 2 && "Y".equalsIgnoreCase(ServerEnvironment.getProperty("ISBedFrameCenter")) && ("skip".equals(str_logintype) || "skip2".equals(str_logintype))) { //����ָ���ò���!!!�����о�Ӧ������ IELogin/skip/single����ָ��,����skipʱ��Ҫ��������,��IE��¼ʱȴ��һ����Ҫ!!
				sb_html.append("  var v_x = window.screenLeft;  \r\n"); //
				sb_html.append("  var v_y = window.screenTop;\r\n"); //
				sb_html.append("  var v_width = document.body.clientWidth;\r\n"); //
				sb_html.append("  var v_height = document.body.clientHeight;\r\n"); //
				if (_request.getParameter("FRAME_X") == null) { //���������
					String str_adjust = ServerEnvironment.getProperty("FRAME_X_ADJUST"); //�е��Ż�ϵͳ������ʹ��FrameSet����,����ȡ�õ�λ��������������е����ݵ�λ��!��ʵ����Ҫȥ���������������߲˵����Ŀռ�,���Դ���һ����������,��������һ��λ�����С,�Ӷ���֤����Ƕ������!!
					if (str_adjust != null && !str_adjust.trim().equals("")) { //����������ߴ�!!!
						sb_html.append("  v_x = v_x" + (str_adjust.startsWith("-") ? str_adjust : "+" + str_adjust) + "; //�е�������\r\n"); //Ҫ����һ�¸���
					}
					sb_html.append("  v_url = v_url + 'FRAME_X=' + v_x + '&';\r\n"); //
				}
				if (_request.getParameter("FRAME_Y") == null) { //���������
					String str_adjust = ServerEnvironment.getProperty("FRAME_Y_ADJUST"); //
					if (str_adjust != null && !str_adjust.trim().equals("")) { //����������ߴ�!!!
						sb_html.append("  v_y = v_y" + (str_adjust.startsWith("-") ? str_adjust : "+" + str_adjust) + "; //�е�������\r\n"); //Ҫ����һ�¸���
					}
					sb_html.append("  v_url = v_url + 'FRAME_Y=' + v_y + '&';\r\n"); //
				}
				if (_request.getParameter("FRAME_WIDTH") == null) { //���������
					String str_adjust = ServerEnvironment.getProperty("FRAME_WIDTH_ADJUST"); //
					if (str_adjust != null && !str_adjust.trim().equals("")) { //����������ߴ�!!!
						sb_html.append("  v_width = v_width" + (str_adjust.startsWith("-") ? str_adjust : "+" + str_adjust) + "; //�е�������\r\n"); //Ҫ����һ�¸���
					}
					sb_html.append("  v_url = v_url + 'FRAME_WIDTH=' + v_width + '&';\r\n"); //
				}
				if (_request.getParameter("FRAME_HEIGHT") == null) { //���������
					String str_adjust = ServerEnvironment.getProperty("FRAME_HEIGHT_ADJUST"); //
					if (str_adjust != null && !str_adjust.trim().equals("")) { //����������ߴ�!!!
						sb_html.append("  v_height = v_height" + (str_adjust.startsWith("-") ? str_adjust : "+" + str_adjust) + "; //�е�������\r\n"); //Ҫ����һ�¸���
					}
					sb_html.append("  v_url = v_url + 'FRAME_HEIGHT=' + v_height + '&';\r\n"); //
				}
			}
			sb_html.append("  return v_url;  //��ǰ���ڷ������˵�IP��˿ڵ�,�����������еĿͻ�������Httpת��,���Ա����ÿͻ��˵ĵ�ַ \r\n");
			sb_html.append("} \r\n\r\n"); //
		}

		sb_html.append(getJS_setCookie()); //����Cookieֵ!

		if (_type == 1 || _type == 2) { //
			sb_html.append(getJS_getCookie()); //ȡCookieֵ!!

			sb_html.append("<!--���ؿͻ����ļ�-->\r\n"); //
			sb_html.append("function downClientFile() { \r\n"); //
			//sb_html.append("if(window.confirm(\"��װ�����п��ܻ���ְ�ȫ����,��ѡ������!\")==true){\r\n"); //������ΪWin7�����ڰ�װ������ʱ��ͼдע���ʱ�о�������!!���ѡ���,��ᵼ�²��ܷ���,ֻ���ֹ�ֱ���ٴ�����%JRE_HOME%\bin\wpreg.reg
			sb_html.append("     window.open(\"./DownLoadFileServlet?filename=/help/webpushjre.exe&iscachefile=Y\",\"download\",\"scrollbars=no,status=no,toolbar=no,location=no,menubar=no,top=100,left=10,width=800,height=150\"); \r\n"); //���ص��ļ���Զ��webpushjre.exe!��!!!
			//sb_html.append(" }\r\n"); //
			sb_html.append("}\r\n\r\n");
		}

		sb_html.append(getJS_closeThisWindow()); //�رմ��ڵ�JS
		sb_html.append(getJS_getCurrTime()); //ȡ��ǰʱ���JS
		return sb_html.toString(); //
	}

	/**
	 * ����Cookie��JS��������
	 * @return
	 */
	private String getJS_setCookie() {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<!--����Cookieֵ-->\r\n"); //
		sb_html.append("function setCookie(_name,_value) {\r\n"); //
		sb_html.append("  var exp  = new Date(\"December 31, 3000\"); //ʧЧ������3000-12-31\r\n"); //
		sb_html.append("  document.cookie = _name + '=' + escape (_value) + ';expires=' + exp.toGMTString(); \r\n"); //
		sb_html.append("}\r\n\r\n"); //
		return sb_html.toString(); //
	}

	private String getJS_getCode() {
		Integer time1 = getTBUtil().getSysOptionIntegerValue("������֤���ٴλ�ȡʱ��", 30);
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("function getCode() {\r\n"); //
		sb_html.append(" var i = " + time1 + "; var info1  = document.getElementById('info1');\r\n");
		sb_html.append(" var numLink  = document.getElementById('numLink');\r\n");
		sb_html.append("  var v_usercode = document.form1.usercode.value; \r\n");
		sb_html.append("  var v_userpwd = document.form1.pwd.value; \r\n");
		sb_html.append("  if(v_usercode=='' || v_userpwd=='') { alert('�û��������벻��Ϊ��!');   return ; } \r\n"); //
		sb_html.append("var xmlhttp = getAjax();xmlhttp.onreadystatechange = function() {\r\n");
		sb_html.append("if (4 == xmlhttp.readyState) {if (200 == xmlhttp.status) { var res = xmlhttp.responseText;");
		sb_html.append("if(res == 1){alert('�����û��Ѵ�����!'); }");
		sb_html.append("else if(res ==2){alert('�û�������!'); }");
		sb_html.append("else if(res == 3){alert('ϵͳ�����û��ظ�!'); }");
		sb_html.append("else if(res == 4){alert('�û�Ȩ����δ��ͨ!'); }");
		sb_html.append("else if(res == 5){alert('�û��ѷ�ͣ!'); }");
		sb_html.append("else if(res == 6) {alert('���ͳɹ�!');numLink.style.display='none';var timer1 = setInterval(function(){info1.innerHTML='<font size=2>' + i + '�����ٴλ�ȡ!</font>';i--;if(i==0){info1.innerHTML='';clearInterval(timer1); info1.style.display='none';numLink.style.display='block'; }}, 1000);}");
		sb_html.append("else if(res == 7){alert('����ʧ��!'); }");
		sb_html.append("else if(res == 9){alert('�ֻ���Ϊ��!'); }");
		sb_html.append("else if(res == 10){alert('��֤���ȡʱ����Ϊ" + time1 + "��!'); }");
		sb_html.append("else if(res == 11){alert('�û��������!'); }");
		sb_html.append("else {alert('�����������Ժ�����!'); }}}\r\n");
		sb_html.append(" }\r\n");
		sb_html.append("xmlhttp.open(\"get\", \"./WebCallServlet?StrParCallClassName=" + getTBUtil().getSysOptionStringValue("������֤�����߼�", "cn.com.psbc.bs.message.CheckCodeService") + "&usercode=\"+v_usercode+\"&pwd=\"+v_userpwd, true);\r\n");
		sb_html.append("if(xmlhttp.readyState==1){xmlhttp.send(\"\");}else{alert('�����������Ժ�����!');}\r\n");
		sb_html.append("}\r\n\r\n"); //
		return sb_html.toString(); //
	}

	private String getJS_getAjax() {
		StringBuilder sb_html = new StringBuilder();
		sb_html.append("function getAjax()\r\n"); //
		sb_html.append("{  var ajax=false;\r\n");
		sb_html.append("  try{\r\n");
		sb_html.append("      ajax = new ActiveXObject(\"Msxml2.XMLHTTP\");\r\n");
		sb_html.append("     }\r\n");
		sb_html.append("     catch (e){\r\n");
		sb_html.append("        try\r\n");
		sb_html.append("           {\r\n");
		sb_html.append("            ajax = new ActiveXObject(\"Microsoft.XMLHTTP\");\r\n");
		sb_html.append("            }\r\n");
		sb_html.append("            catch (E){\r\n");
		sb_html.append("            ajax = false;}\r\n");
		sb_html.append("            }\r\n");
		sb_html.append("if (!ajax && typeof XMLHttpRequest!='undefined')\r\n");
		sb_html.append("{\r\n");
		sb_html.append("ajax = new XMLHttpRequest();\r\n");
		sb_html.append("}\r\n");
		sb_html.append("return ajax;\r\n");
		sb_html.append("}\r\n");

		return sb_html.toString(); //
	}

	/**
	 * ��ȡCookieֵ��JS��������
	 * @return
	 */
	private String getJS_getCookie() {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<!--ȡCookieֵ-->\r\n"); //
		sb_html.append("function getCookie(_name) { \r\n"); //
		sb_html.append("  var search = _name + '=';\r\n"); //
		sb_html.append("  if(document.cookie.length > 0) { \r\n");
		sb_html.append("    var offset = document.cookie.indexOf(search);\r\n");
		sb_html.append("    if(offset!=-1) {\r\n");
		sb_html.append("      offset += search.length;\r\n");
		sb_html.append("      var end = document.cookie.indexOf(';', offset);\r\n");
		sb_html.append("      if(end == -1) { end = document.cookie.length; }\r\n");
		sb_html.append("      return unescape(document.cookie.substring(offset, end));\r\n");
		sb_html.append("    }\r\n");
		sb_html.append("  }\r\n");
		sb_html.append("  return '';\r\n");
		sb_html.append("}\r\n\r\n");
		return sb_html.toString(); //
	}

	/**
	 * �رմ��ڵ�JS
	 * @return
	 */
	private String getJS_closeThisWindow() {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<!--�رմ���,�����������FireFox�в���Ч��,������window.close()û����-->\r\n"); //
		sb_html.append("function closeThisWindow() { \r\n"); //
		sb_html.append("  window.opener=null;\r\n"); //IE6��֪Ϊʲô����Ҫ����д!
		sb_html.append("  window.open('','_self');\r\n"); //
		sb_html.append("  window.close();\r\n"); //
		sb_html.append("}\r\n\r\n");
		return sb_html.toString(); //
	}

	/**
	 * ȡ�õ�ǰʱ��!!!
	 * @return
	 */
	private String getJS_getCurrTime() {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<!--ȡ�õ�ǰʱ��,���ط��õ�-->\r\n"); //
		sb_html.append("function getCurrTime(_type) {\r\n"); //
		sb_html.append("  var newDate=new Date();\r\n"); //
		sb_html.append("  var v_currtime = newDate.getFullYear() + '-' + (newDate.getMonth()+1) + '-' + newDate.getDate() + ' ' + newDate.toLocaleTimeString();\r\n"); //
		sb_html.append("  if('2'==_type) { \r\n");
		sb_html.append("    v_currtime = v_currtime + ':' + newDate.getMilliseconds(); \r\n"); //���Ϻ���
		sb_html.append("  }\r\n"); //
		sb_html.append("  return v_currtime;\r\n"); //
		sb_html.append("}\r\n\r\n"); //
		return sb_html.toString(); //
	}

	/**
	 * ����JRE!!
	 * @return
	 */
	private String getJS_downloadJRE() {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<!--������������JRE-->\r\n"); //
		sb_html.append("function downloadJRE(){ \r\n");
		sb_html.append("  document.getElementById('div_pop').style.display='none'; \r\n"); //�ȹر���ʾ��!
		boolean isdirectlyload = !("N".equalsIgnoreCase(ServerEnvironment.getProperty("ISJREDIRECTLYDOWNLOAD"))); //Ĭ����ֱ�����أ��������Ϊ��N������ʹ�����ֵ�Activex���أ���겹�䣡�����/2012-07-09��
		String str_download_1 = "    window.open('./help/webpushjre.exe','_blank','top=300,left=300,width=500,height=200,toolbar=no,location=no,menubar=no'); \r\n"; //
		String str_download_2 = "    window.open('./login?logintype=activexsharejre','share','top=300,left=300,width=650,height=200,toolbar=no,location=no,menubar=no,resizable=yes'); \r\n"; //���´���,����д��Activex�ؼ�!!,ʹ��Activex�ж�,�󶨻�����JRE,����Ѱ�װJRE����а�,������װJRE�������߸��û�!!����ΪJRE̫�������!!
		sb_html.append("  if(window.event.ctrlKey){ \r\n");
		sb_html.append(isdirectlyload ? str_download_2 : str_download_1); //
		sb_html.append("  } else { \r\n");
		sb_html.append(isdirectlyload ? str_download_1 : str_download_2); //
		sb_html.append("  } \r\n");
		sb_html.append("} \r\n\r\n");
		return sb_html.toString(); //
	}

	/**
	 * ���û��װ���,�򵯳�һ������ʾ˵Ҫ���ز��!!     
	 * @return
	 */
	private String getPopDiv() {
		StringBuilder sb_html = new StringBuilder(); //
		String str_msg = "�״η�����Ҫ��װ���,��ѡ��!"; //
		sb_html.append("\r\n");
		sb_html.append("<!--������ʾ���ؿͻ��˵Ĳ�-->\r\n"); //
		sb_html.append("<div id=\"div_pop\" style=\"width:450px;height:115px;background:#FFFFFF;position:absolute;BORDER:#4F4F4F 1px solid;display:none\">\r\n"); //
		sb_html.append("  <div style=\"RIGHT: 4px; POSITION: absolute; TOP: 5px\"><a onclick=\"JavaScript:document.getElementById('div_pop').style.display='none';\" href=\"javascript:;\" style=\"FONT-SIZE:12px;COLOR:#0000FF;\">�ر�</a></div>\r\n"); //
		sb_html.append("  <div id=div_pop_body style=\"PADDING-RIGHT: 20px; PADDING-LEFT: 50px; PADDING-BOTTOM: 20px; PADDING-TOP: 15px\">\r\n"); //
		sb_html.append("    <p style=\"FONT-SIZE:12px;COLOR:#FF0000;\" title=\"������Ѱ�װ����,ֻ����������Cookie���ֳ����˸�����,����Ҫ���°�װ!\">" + str_msg + "<br/></p>\r\n");
		boolean isdirectlyload = !("N".equalsIgnoreCase(ServerEnvironment.getProperty("ISJREDIRECTLYDOWNLOAD"))); //�Ƿ�ֱ�����ػ���ʹ�����ֵ�Activex����??,Ĭ��ʱ��Y
		String str_tooltip = ""; //
		if (isdirectlyload) {//ֻ������Ϊ��N��ʱ ��� ����װ����� ��ֱ�ӵ���Activex�󶨻�����JRE
			str_tooltip = "���ز���װ���,ȫ���̴�Լ1��������!\r\n��סCtrl������ɰ󶨻�����!"; //
		} else {//ֱ������
			str_tooltip = "���ز���װ���,ȫ���̴�Լ1��������!\r\n��סCtrl�������ֱ������!"; //
		}
		sb_html.append("    <input type=\"button\" value=\"����װ���\"  title=\"" + str_tooltip + "\" onclick=\"JavaScript:downloadJRE();\" class=\"style_1\">\r\n"); //
		sb_html.append("    <input type=\"button\" value=\"���Ѱ�װ,��Ҫ������\" title=\"�����ȷ�Ѱ�װ�����,��Ϊ��������Cookie�򻺴����������,��ֱ�ӵ������ť!\" onclick=\"JavaScript:setCookie('WebPushJREInstalled','Y');document.getElementById('div_pop').style.display='none';runJava();\" class=\"style_1\">&nbsp;&nbsp;\r\n"); //����Cookieֵ,���ر�!!
		sb_html.append("  </div>\r\n"); //
		sb_html.append("</div>\r\n"); //
		return sb_html.toString(); //
	}

	private String getActivexDiv() {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("  <!-- ���Activex�ؼ� -->\r\n"); //
		sb_html.append("  <div id=\"ActivexDiv\" style=\"position:absolute;left:0px;top:0px;width:10px;height:5px;background:#FFFFFF;BORDER:#4F4F4F 0px solid;FILTER:alpha(opacity=95)\">\r\n"); //
		sb_html.append("    <OBJECT name=\"WebPushOCX\" classid=\"clsid:87477552-7850-4E33-8C9F-4AA6DAC7F154\" codebase=\"./applet/WebPushCallJava.cab#version=1,0,1,0\" width=\"100%\" height=\"100%\" border=\"0\"></OBJECT>\r\n"); //
		sb_html.append("  </div>\r\n"); //
		sb_html.append("\r\n");
		return sb_html.toString(); //
	}

	//Ĭ�ϵ�¼�뵥���¼�ĵڶ��ַ��ʷ�ʽ�����õ���δ���,���԰�װ��һ������!!
	private String getRunJava2(boolean _isSingle) {
		StringBuilder sb_html = new StringBuilder(); //
		String str_jre = getClientJREVersion(); //Ĭ����1.6.0_18
		//�Զ���ͻ������������ڴ� �����/2013-04-23��
		sb_html.append("    var v_1 = 'C:/WebPushJRE/jre" + str_jre + "/bin/java6.exe -client -Xms32m -Xmx" + getTBUtil().getSysOptionIntegerValue("�ͻ������������ڴ�", 1024) + "m ';  \r\n"); //
		sb_html.append("    var v_2 = '-Djava.library.path=C:/WebPushJRE/jre" + str_jre + "/bin;C:/WebPushJRE/jre" + str_jre + "/bin/bin3 -Dfile.encoding=GBK '; \r\n"); //
		sb_html.append("    var v_3 = 'cn.com.weblight.applet.WLTAppletMainFrame " + getRegeditCode() + "://localjava/call?' + " + (_isSingle ? "getClientRequestUrl()" : "v_url + '&'") + " + 'oldversionpar=url;user;pwd;adminpwd'; \r\n"); //
		sb_html.append("    var v_callurl = v_1 + v_2 + v_3;\r\n"); //��������,û�о������ļ�!!
		sb_html.append("    var v_return = WebPushOCX.CallJava(v_callurl);\r\n"); //��������,û�о������ļ�!!
		sb_html.append("    if(v_return=='0'){  \r\n"); //���û���ҵ��ļ�,�����ز��!!
		sb_html.append("       downClientFile();  \r\n"); //���ؿͻ����ļ�!!!
		sb_html.append("       return; \r\n");
		sb_html.append("    } \r\n");
		return sb_html.toString(); //
	}

	/**
	 * Webҳ���¼�ύ��������ļ�
	 * @return
	 */
	public String getAppletObjectParams(HttpServletRequest _request, HttpServletResponse _response, String _loadClass) {
		StringBuffer sb_html = new StringBuffer(); //
		//��һ����©���������weblight.xml�ж����init-param
		String transpro = _request.getParameter("transpro");
		if (transpro == null || "".equals(transpro.trim())) {
			transpro = "http";
		}
		System.setProperty("transpro", transpro);
		InitParamVO[] initParmVOs = ServerEnvironment.getInstance().getInitParamVOs(); //
		sb_html.append("\r\n  <!-- ��ʼ�������weblight.xml�е�init-param -->\r\n"); //
		for (int i = 0; i < initParmVOs.length; i++) { //
			String str_itemValue = initParmVOs[i].getValue(); //
			if (str_itemValue.indexOf(">") >= 0 || str_itemValue.indexOf("<") >= 0) { //�����������Ż��Ҽ�����,����Ҫ�滻,��������������Ŀ�������������,����Ҫ��ת��һ��url,url���м����ţ���xch/2012-08-12��
				str_itemValue = getTBUtil().replaceAll(str_itemValue, "<", "&lt;"); //�����滻
				str_itemValue = getTBUtil().replaceAll(str_itemValue, ">", "&gt;"); //
			}
			if (str_itemValue.indexOf("\"") >= 0) { //������ݱ�����˫����,�����ʹ�õ����Ű�����
				str_itemValue = "'" + str_itemValue + "'";
			} else { //������ݱ���û��˫����,��ʹ��˫����!
				str_itemValue = "\"" + str_itemValue + "\"";
			}
			sb_html.append("  <PARAM NAME=\"" + initParmVOs[i].getKey() + "\"  " + getSpace(initParmVOs[i].getKey()) + "VALUE=" + str_itemValue + " />\r\n"); //��һ����©���������weblight.xml�����init-param
		}
		sb_html.append("  <!-- �������weblight.xml�е�init-param���� -->\r\n\r\n"); //

		sb_html.append("  <PARAM NAME=\"SERVERCLIENTVERSION\"   VALUE=\"" + getClientJREVersion() + "\" />\r\n"); //����JRE�İ汾,����ͻ��������еĲ����Ƚ�,�����һ��,�����������JRE,����19M����!!!!
		sb_html.append("  <PARAM NAME=\"APPLETVIEWER_VERSION\"  VALUE=\"" + cn.com.weblight.applet.WLTAppletUtil.APPLETVIEWER_VERSION + "\" />\r\n"); //�ͻ�������İ汾��,�������������µĻ���,������Ҫ��������19M��JRE��,��ֻ����Ҫ����70��K�����漴��!!�����ĸ��»�ʹ�ͻ��ܽ���!!!!

		//�������ǽӳ��IP��ַ����
		String str_clienRequestIPPort = _request.getParameter("clrequestip"); //�¿ͻ��˰汾�Ĳ���!!
		String str_ip = convertIP(str_clienRequestIPPort, _request.getServerName()); //Ҫת��!!����Ϻ�ũ���е��������绷��������!
		String str_port = convertPort(str_clienRequestIPPort, _request.getServerPort()); //Ҫת��!!
		System.setProperty("CALLURL", transpro + "://" + str_ip + ":" + str_port + _request.getContextPath()); // �������ʱ��Ҳ��Ҫ�õ������ַ
		sb_html.append("  <PARAM NAME=\"SERVER_HOST_NAME\"      VALUE=\"" + str_ip + "\" />\r\n");
		sb_html.append("  <PARAM NAME=\"SERVER_PORT\"           VALUE=\"" + str_port + "\" />\r\n");
		sb_html.append("  <PARAM NAME=\"APP_CONTEXT\"           VALUE=\"" + _request.getContextPath() + "\" />\r\n");
		sb_html.append("  <PARAM NAME=\"URL\"                   VALUE=\"" + transpro + "://" + str_ip + ":" + str_port + _request.getContextPath() + "\" />\r\n");

		//tool.getRealCallPort(li_port, _request.getContextPath()); //ȡ��ֱ�ӵļ�Ⱥģʽ�µ�ʵ�ʶ˿�!!!����������߼����ڿͻ���ʵ����!!!�����Եø���׳һЩ!!!��Ҳ�Բ�׼�Ƿ����!!!
		sb_html.append("  <PARAM NAME=\"CALLSERVER_HOST_NAME\"  VALUE=\"" + str_ip + "\" />\r\n");
		sb_html.append("  <PARAM NAME=\"CALLSERVER_PORT\"       VALUE=\"" + str_port + "\" />\r\n");
		sb_html.append("  <PARAM NAME=\"CALLURL\"               VALUE=\"" + transpro + "://" + str_ip + ":" + str_port + _request.getContextPath() + "\" /> <!--������Ҫ�Ĳ���,�����������������ַ-->\r\n");

		if (_loadClass == null) {
			sb_html.append("  <PARAM NAME=\"LoaderAppletClass\"     VALUE=\"cn.com.infostrategy.ui.sysapp.login.LoginAppletLoader\" />\r\n"); //����Ҫ�Ĳ���!!!! �ͻ��������ʹ���Զ���classLoader������������!!!!!
		} else {
			sb_html.append("  <PARAM NAME=\"LoaderAppletClass\"     VALUE=\"" + _loadClass + "\" />\r\n"); //����Ҫ�Ĳ���!!!! �ͻ��������ʹ���Զ���classLoader������������!!!!!
		}

		sb_html.append("  <PARAM NAME=\"APP_DEPLOYPATH\"        VALUE=\"" + ServerEnvironment.getProperty("WebAppRealPath") + "\" />\r\n");
		sb_html.append("  <PARAM NAME=\"DEPLOYMODEL\"           VALUE=\"CLUSTER\" />\r\n"); //��Ⱥ����
		if (_request.getParameter("admin") != null && _request.getParameter("admin").equalsIgnoreCase("Y")) {
			sb_html.append("  <PARAM NAME=\"LOGINMODEL\"            VALUE=\"ADMIN\" />\r\n"); //��¼ģʽ,����Ա��¼
		} else {
			sb_html.append("  <PARAM NAME=\"LOGINMODEL\"            VALUE=\"NORMAL\" />\r\n"); //��¼ģʽ,��ͨ��ݵ�¼
		}

		//ʵ��ѡ�е��ڲ�ϵͳ...
		if (_request.getParameter("isys") != null) { //ȡʵ���ύ��!!
			sb_html.append("  <PARAM NAME=\"ISYS\"                  VALUE=\"" + _request.getParameter("isys") + "\" />\r\n"); //ѡ�е���ϵͳ
		} else {
			if (ServerEnvironment.getInnerSys() != null && ServerEnvironment.getInnerSys().length > 0) { //
				sb_html.append("  <PARAM NAME=\"ISYS\"                  VALUE=\"" + ServerEnvironment.getInnerSys()[0][0] + "\" />\r\n"); //ѡ�е���ϵͳ
			} else {
				sb_html.append("  <PARAM NAME=\"ISYS\"                  VALUE=\"\" />\r\n"); //ѡ�е���ϵͳ
			}
		}
		if (_request.getParameter("desktopLogin") != null && _request.getParameter("desktopLogin").equalsIgnoreCase("Y")) {
			sb_html.append("  <PARAM NAME=\"STARTMODEL\"            VALUE=\"DESKTOP\" />\r\n"); //��������

		} else {
			sb_html.append("  <PARAM NAME=\"STARTMODEL\"            VALUE=\"BROWSE\" />\r\n"); //���������
		}

		if (_request.getParameter("usercode") != null) { //������û���������!�����ݵ�¼,����������վֱ����ת������
			String userCode = _request.getParameter("usercode");
			if (userCode.indexOf("%") > -1) {//��ʾ���Ϊgoogle�򴫵ݹ�����Ϊ16���Ʊ����������⴦��   һ�������code���ǲ�����%��     Ԭ����20130422���
				try {
					userCode = java.net.URLDecoder.decode(userCode, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			} else {
				try {
					userCode = new String(userCode.getBytes("iso8859-1"), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			sb_html.append("  <PARAM NAME=\"LOGINUSERCODE\"         VALUE=\"" + userCode + "\" />\r\n"); //�û���    //Ԭ���� 20130422�޸� ��Ҫ�����IE��½codeΪ���Ķ����µ�½�������������
			sb_html.append("  <PARAM NAME=\"LOGINUSERPWD\"          VALUE=\"" + _request.getParameter("pwd") + "\" />\r\n"); //�û�����,�Ժ���Ҫ���ܴ���Ȼ����swing�н��ܴ�����У��
			sb_html.append("  <PARAM NAME=\"LOGINUSERADMINPWD\"     VALUE=\"" + _request.getParameter("adminpwd") + "\" />\r\n"); //�û�����,�Ժ���Ҫ���ܴ���Ȼ����swing�н��ܴ�����У��
		} else if (_request.getAttribute("singlelogin_usercode") != null) {
			try {
				cn.com.infostrategy.bs.common.CommDMO dmo = new cn.com.infostrategy.bs.common.CommDMO();
				HashVO[] temp = dmo.getHashVoArrayByDS(null, "select * from pub_user where code='" + _request.getAttribute("singlelogin_usercode").toString() + "'");
				if (temp != null && temp.length == 1) {
					sb_html.append("  <PARAM NAME=\"LOGINUSERCODE\"         VALUE=\"" + _request.getAttribute("singlelogin_usercode") + "\" />\r\n"); //�û���
					sb_html.append("  <PARAM NAME=\"LOGINUSERPWD\"          VALUE=\"" + temp[0].getStringValue("pwd") + "\" />\r\n"); //�û�����,�Ժ���Ҫ���ܴ���Ȼ����swing�н��ܴ�����У��
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (_request.getParameter("LANGUAGE") != null) { //������..
			sb_html.append("  <PARAM NAME=\"LANGUAGE\"              VALUE=\"" + _request.getParameter("LANGUAGE") + "\" />\r\n"); //��ݵ�¼
		}
		sb_html.append("  <PARAM NAME=\"FILTERPACKAGE\"         VALUE=\"org.apache.poi.;cn.com.weblight.images;com.sun.;org.jdesktop.;bin.windows.x86;org.jfreechart;\" />\r\n"); //���˵İ�,�����ӷ�������ȡ,���ӱ���ȡ
		return sb_html.toString(); //
	}

	/**
	 * ����д����Activex����һ���Ƿ�������JRE,�����,��������19M��PushJRE,��ֱ��ʹ�ñ��˵�!! �����ĺô���ʡȥ�еĿͻ���Թ���ǲ��̫�������!! ������������Ŀ�о�һֱ�����ڲ��̫�������!!
	 * ���û���ҵ�����JRE,��û�а취,��ֱ����ActiveX������,����Ҳ�н�������ʾ!! �������ֱ���������ǵ�JRE��װ����!!
	 * @return
	 */
	public String getActiveXShareJREHtml(HttpServletRequest _request) {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<HTML >\r\n");
		sb_html.append("<HEAD>\r\n");
		sb_html.append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=GBK\">\r\n"); //

		//���һЩJS
		sb_html.append("<script language=\"JavaScript\">  \r\n");
		sb_html.append(getJSFunctions(_request, 3)); //�����һ��JS����!!

		sb_html.append("<!-- Ѱ��JRE,�����ֱ�ӹ���֮,���û��,����������JRE  --> \r\n"); //Ѱ��JRE
		sb_html.append("function findShareJRE() {  //Ѱ��JRE \r\n"); //Ѱ��JRE
		sb_html.append("  var ret = WebPushActiveX.HasPushJRE();  //���Ƿ���PushJRE,����ע������Ƿ���WebPush20100601 \r\n"); //
		sb_html.append("  if (ret==\"1\") { //�����PushJRE \r\n"); //�����PushJRE
		sb_html.append("    setCookie('WebPushJREInstalled','Y');  //ֱ��дCookie\r\n");
		sb_html.append("    alert('ϵͳ�����Ѱ�װ�˲��,�����½�����Cookie�Ǽ�,�����·��ʼ���!');  \r\n");
		sb_html.append("    closeThisWindow();  //�رմ���  \r\n");
		sb_html.append("  } else { //���û��WebPushJRE \r\n"); //
		sb_html.append("    var v_javahome = WebPushActiveX.GetJavaHome(); \r\n"); //
		sb_html.append("    if (v_javahome==\"\") { \r\n");
		sb_html.append("       MSGLabel.value='����û�а�װJRE,��������...';  \r\n");
		sb_html.append("    } else {  \r\n"); //ʹ���������������!!!
		sb_html.append("       MSGLabel.value='�����Ѱ�װJRE��' + v_javahome + '��,���ڽ��а�...'; //����ҵ���JRE,��ֻ����appletViewer.jar \r\n");
		sb_html.append("    } \r\n"); //
		sb_html.append("    var v_url = String(window.location); //�����ÿͻ��˵�url,����ӷ�����������Ի���Ϊ�Ϻ�ũ�����������绷������ȡ�Ĳ���! \r\n"); //
		sb_html.append("    var v_pos = v_url.indexOf('/login?'); \r\n"); //
		sb_html.append("    var v_url_prefix = v_url.substring(0,v_pos);  //��ȡǰ��Ĳ���! \r\n"); //
		sb_html.append("    var urlPushJRE = v_url_prefix + '/help/webpushjre.exe'; //���������! \r\n"); //
		sb_html.append("    var urlPushJAR = v_url_prefix + '/DownLoadFileServlet?filename=/WEB-INF/lib/wltappletviewer.jar'; //���Ҫ���ص�JRE��extĿ¼�� \r\n"); //
		sb_html.append("    WebPushActiveX.InstallPushJRE(urlPushJRE, urlPushJAR); //����JRE,��Activex�����������������JRE���Ǿ�AppletViewer.jar  \r\n"); //
		sb_html.append("    if (v_javahome==\"\") {  //���û�ҵ�����JRE,����������JRE,���ؽ������Զ������Խ�ѹ����,�����ֱ�ӹرմ��� \r\n"); //
		//sb_html.append("      setCookie('WebPushJREInstalled','Y');  //дCookie \r\n");   //��ʱ��дCookie,��Ϊ�ڵ����Խ�ѹ���ں�,�������˻�㡾�ܾ�����ť,����ʱ��Ϊ���첽��,ȡ�������״̬,������ʱдCookie,����ܾͲ�׼!! ���Բ�д!!
		sb_html.append("      closeThisWindow();  // \r\n"); //
		sb_html.append("    } else { //���������JRE,��󶨽�����������ʾ,Ȼ��ֱ�ӹرմ��� \r\n"); //
		sb_html.append("      setCookie('WebPushJREInstalled','Y');  //ֱ��дCookie,ʡ���ٴη���ʱ�ٵ��²�Ҫ�����ѵİ�ť! \r\n");
		sb_html.append("      alert('�����Ѱ�װJRE��' + v_javahome + '��,�ѳɹ���֮,�����·���!');  \r\n");
		sb_html.append("      closeThisWindow(); \r\n"); //
		sb_html.append("    }\r\n");
		sb_html.append("  }\r\n");
		sb_html.append("}\r\n");

		sb_html.append("</script>  \r\n"); //
		sb_html.append("<TITLE>WebPush IE</TITLE>\r\n");
		sb_html.append("</HEAD>\r\n\r\n");
		sb_html.append("<BODY onLoad=\"findShareJRE();\">\r\n"); //Ѱ��JRE
		sb_html.append("<input type=\"text\" name=\"MSGLabel\" value=\"ϵͳ���ڴ���......\" size=\"100\" style=\"border:none;color:#0000FF;font-size: 12px;\"><br>\r\n"); //������ʾ˵����!!!
		sb_html.append("<OBJECT name=\"WebPushActiveX\" classid=\"clsid:53F7F8C2-2853-404B-B241-68F0E3D0E533\" codebase=\"./applet/CallWebPush.cab#version=4,0,0,0\" width=\"600\" height=\"26\"></OBJECT>\r\n"); //
		sb_html.append("</BODY>\r\n");
		sb_html.append("</HTML>\r\n");
		return sb_html.toString(); //
	}

	//ת��IP
	private String convertIP(String _clientRequestIPPort, String _serverRequestIP) {
		String str_forceIPPort = ServerEnvironment.getProperty("ForceIPPort_ForClientCall"); //
		if (str_forceIPPort != null) {
			return str_forceIPPort.substring(0, str_forceIPPort.indexOf(":")); //���ǿ�ж�����,��ֱ�ӷ���!
		}
		if (_clientRequestIPPort != null) { //�����,��ֱ�ӷ�����! ��Ϊ�����Ϻ�ũ���е����绷���лᷢ���ڷ������˵õ��ĵ�ַ��˿���ͻ���ʵ������ĸ�����һ��! ������CallURL����,�Ӷ��޷�����!!
			return _clientRequestIPPort.substring(0, _clientRequestIPPort.indexOf(":")); //���ǿ�ж�����,��ֱ�ӷ���!
		}
		return _serverRequestIP; //���ط������˵�,��ԭ����Ĭ�Ϸ���!!
	}

	//ת���˿�
	private String convertPort(String _clientRequestIPPort, int _serverRequestPort) {
		String str_forceIPPort = ServerEnvironment.getProperty("ForceIPPort_ForClientCall"); //
		if (str_forceIPPort != null) {
			return str_forceIPPort.substring(str_forceIPPort.indexOf(":") + 1, str_forceIPPort.length()); //���ǿ�ж�����,��ֱ�ӷ���!
		}
		if (_clientRequestIPPort != null) { //�����,��ֱ�ӷ�����! ��Ϊ�����Ϻ�ũ���е����绷���лᷢ���ڷ������˵õ��ĵ�ַ��˿���ͻ���ʵ������ĸ�����һ��! ������CallURL����,�Ӷ��޷�����!!
			return _clientRequestIPPort.substring(_clientRequestIPPort.indexOf(":") + 1, _clientRequestIPPort.length()); //
		}
		return "" + _serverRequestPort; //
	}

	public String getSpace(String _key) {
		int li_length = _key.length(); //
		String str_space = "";
		for (int i = 0; i < (30 - li_length); i++) {
			str_space = str_space + " ";
		}
		return str_space;
	}

	public String getErrorMsg(String _text) {
		StringBuffer sb_exception = new StringBuffer(); //
		sb_exception.append("<html>\r\n");
		sb_exception.append("<head>\r\n");
		sb_exception.append("<META http-equiv=Content-Type content=\"text/html; charset=GBK\">\r\n"); //
		sb_exception.append("</head>\r\n");
		sb_exception.append("<body>\r\n");
		sb_exception.append(_text);
		sb_exception.append("</body>\r\n");
		sb_exception.append("</html>\r\n");
		return sb_exception.toString(); //
	}

	private TBUtil getTBUtil() {
		if (tbUtil != null) {
			return tbUtil; //
		}
		tbUtil = new TBUtil(); //
		return tbUtil; //
	}
}
