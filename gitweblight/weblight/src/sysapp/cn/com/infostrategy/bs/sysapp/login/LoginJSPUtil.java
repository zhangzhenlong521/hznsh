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
	 * 注册表中的定义的我们的注册码,曾经想固定死就是[webpush20100601],但在兴业项目上升级过程中遇到一个问题!
	 * 同时装两个客户端时,浏览器访问只能有一个行!!问题就出在注册表只能注册一个, 如果能注册两个,然后各是各的,然后重做下客户端中的注册表文件!这样两个都能访问了!
	 * 总之,如果这个搞成活的本配置,好处还是大大的!!
	 * @return
	 */
	public String getRegeditCode() {
		String str_regcode = ServerEnvironment.getProperty("RegeditCode"); //
		if (str_regcode != null && !str_regcode.trim().equals("")) { //
			return str_regcode; //
		} else {
			return "webpush20100601"; //返回最新的版本号!!!考虑到兼容旧的大部分都是18,如果强行要求将旧的升到新的jre,则指定参数SERVERCLIENTVERSION!!
		}
	}

	//其实以前对这个升级就是为了升级AppletViewer.jar,但后来将对AppletViewer可以单独升级(只有70K),所以以后这个版本号以后更改的可能性非常小!!!!只有在从jre1.5升到jre1.6的情况下才会用到!!!!
	public String getClientJREVersion() {
		if (ServerEnvironment.getProperty("SERVERCLIENTVERSION") != null) { //如果定义了版本号,则返回之,比如民生银行可能不想下载客户端!!!
			return ServerEnvironment.getProperty("SERVERCLIENTVERSION"); //
		} else {
			return "1.6.0_18"; //返回最新的版本号!!!考虑到兼容旧的大部分都是18,如果强行要求将旧的升到新的jre,则指定参数SERVERCLIENTVERSION!!
		}
	}

	/**
	 * IE访问时的第一个Html页面,即出现登录用户名,密码,登录图片等。
	 * @param _request
	 * @param _response
	 * @return
	 */
	public String getLoginPageHtml(HttpServletRequest _request, HttpServletResponse _response) {
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<html>\r\n");
		sb_html.append("<head>\r\n");
		sb_html.append("<TITLE>" + ServerEnvironment.getProperty("PROJECT_NAME") + "</TITLE>\r\n"); //标题名
		sb_html.append("<META http-equiv=Content-Type content=\"text/html; charset=GBK\">\r\n"); //
		sb_html.append("<STYLE>\r\n");
		sb_html.append(".style_1 { BORDER-RIGHT: #999999 1px solid; BORDER-TOP: #999999 1px solid; BORDER-LEFT: #999999 1px solid; BORDER-BOTTOM: #999999 1px solid; FONT-SIZE: 12px; HEIGHT: 18px; WIDTH:130px; BACKGROUND-COLOR: #FFFFFF }\r\n");
		sb_html.append(".btn_1   { BORDER-RIGHT: #999999 1px solid; BORDER-TOP: #999999 1px solid; BORDER-LEFT: #999999 1px solid; BORDER-BOTTOM: #999999 1px solid; FONT-SIZE: 12px; HEIGHT: 22px; WIDTH:60px; BACKGROUND-COLOR: #EEEEEE }\r\n"); //
		sb_html.append("</STYLE>\r\n"); //

		boolean isRunJava2 = isRunCall2(_request); //一共有两种

		sb_html.append("<script language=\"JavaScript\">  \r\n");
		sb_html.append(getJSFunctions(_request, 1)); //先输出一堆JS函数!!
		sb_html.append(getJS_getAjax());
		sb_html.append(getJS_getCode());
		sb_html.append("<!-- 取得实际的完整的Url -->\r\n"); //
		sb_html.append("function getRealLoginUrl() { \r\n"); //
		sb_html.append("  var v_usercode = document.form1.usercode.value; \r\n");
		sb_html.append("  var v_userpwd =  document.form1.pwd.value; \r\n"); //
		sb_html.append("  if(v_usercode=='') { alert('用户名不能为空!');   document.form1.usercode.focus(); return ''; } \r\n"); //
		sb_html.append("  if(v_userpwd=='')  { alert('用户密码不能为空!'); document.form1.pwd.focus(); return ''; } \r\n"); //
		sb_html.append("  var v_url = getClientRequestUrl() + 'logintype=IELogin&usercode=' + v_usercode + '&pwd='  + v_userpwd; \r\n"); //
		if ("Y".equalsIgnoreCase(_request.getParameter("admin"))) { //如果是管理密码
			sb_html.append("  var v_adminpwd =  document.form1.adminpwd.value; \r\n");
			sb_html.append("  if(v_adminpwd=='')  { alert('管理密码不能为空!'); document.form1.adminpwd.focus(); return ''; } \r\n"); //sunfujun/20121210/hm需要admin=y情况下必须输管理密码
			sb_html.append("  v_url = v_url + '&adminpwd=' + v_adminpwd; \r\n"); //
		} else {
			if (tbUtil.getSysOptionBooleanValue("登录界面是否有验证码", false)) {
				sb_html.append("  var v_checkcode =  document.form1.checkcode.value; \r\n"); //
				sb_html.append("  if(v_checkcode=='')  { alert('验证码不能为空!'); return ''; } \r\n"); //
				sb_html.append("  v_url = v_url + '&checkcode=' + v_checkcode; \r\n"); //
			}
		}
		sb_html.append("  return v_url;  \r\n"); //
		sb_html.append("} \r\n\r\n"); //

		sb_html.append("<!-- 调用本地Java程序,登录初期发生无法使用Cmd窗口查看异常日志时,可以将C:\\WebPushJRE\\..\\java6.exe改名为javaw6.exe -->\r\n"); //
		sb_html.append("function runJava(){ \r\n"); //点击确定按钮时执行的JavaScript,该JS脚本其实就是通过注册表注册的协议调用了本地的批命令!!
		String str_exitIECookie = ServerEnvironment.getProperty("PushMonitorIEExit"); //监控IECookie的标记!!
		if (str_exitIECookie != null && !str_exitIECookie.trim().equals("")) { //如果有这个逻辑,则在加载之前先清空下!否则如果使用单点登录或skip/skip2登录后,然后关闭IE成功将所有JVM杀掉,但这时Cookie没有人来删除!如果这时又人切换成正常的IE登录,则就会造成起来了程序!!所以要先清空下!!!
			sb_html.append("  setCookie('" + str_exitIECookie + "',''); //因为定认了参数PushMonitorIEExit,而且是IE跳转的,所以先清空下该Cookie \r\n"); //清空Cookie,既然在退出时设置了标记,就要有地方清空一下! 否则可能会造成永远起不来!
		}
		if (!isRunJava2) { //第一种方式,即访问注册表的协议
			sb_html.append("  var v_url = getRealLoginUrl();  \r\n"); //这里负责校验输入框是否为空??
			sb_html.append("  if(v_url==''){ return; }\r\n"); //
			sb_html.append(getCheckCookie(true)); //检查Cookie!
			sb_html.append("  self.location='" + getRegeditCode() + "://localjava/call?' + v_url + '&oldversionpar=url;user;pwd;adminpwd';  <!--跳转执行本地Java程序--> \r\n"); //之所以有个oldversionpar是为了兼容支持以前以分号相隔的入参机制,否则老的机制使用浏览器登录时会跑不起来!!
		} else { //第二种方式,即使用Activex方式登录的(在航天科工等项目中发现因安全问题而第一种方式无法运行),这种方法的好处是不再依赖注册表文件了!!! 但Activex也经常遇到有人下载不了!所以两种方式都支持,而且可以切换!!
			sb_html.append("  var v_url = getRealLoginUrl();  \r\n"); //这里负责校验输入框是否为空??
			sb_html.append("  if(v_url==''){ return; }\r\n"); //
			sb_html.append(getRunJava2(false)); //使用Activex调Java
		}
		sb_html.append("  setTimeout(\"closeThisWindow();\",3500); <!--3.5秒后关闭原来窗口--> \r\n");
		sb_html.append("} \r\n");

		if (!isRunJava2) { //
			sb_html.append("\r\n"); //
			sb_html.append(getJS_downloadJRE()); //弹出窗口下载JRE
		}
		sb_html.append("</script> \r\n"); //

		sb_html.append("</head>\r\n");
		sb_html.append("\r\n");
		// 滚动不好吧 登陆界面不要滚动才好/sunfujun
		sb_html.append("<body scroll=no style=\"overflow:hidden\" background=\"./applet/bg.gif\" bgcolor=\"#FFFFFF\" topmargin=0 leftmargin=0 rightmargin=0 bottommargin=0 marginwidth=0 marginheight=0>\r\n");
		sb_html.append("<div id=\"root\" style=\"position:absolute;left:0;top:0;width=" + li_width + ";height=" + li_height + ";\">\r\n");
		//Activex控件!!
		if (isRunJava2) { //只有第二种方式才输出ActiveX,第一种方式不输出,否则第一次打开页面非常慢!!!
			sb_html.append(getActivexDiv()); //摆在最左上角,很小(10px*5px),几乎看不出来,所以不影响UI效果!!!
		}

		//图片
		sb_html.append("  <!-- 输出背景图片 -->\r\n"); //
		sb_html.append("  <div id=\"image\" style=\"position:absolute;left:0;top:0;width=" + li_width + ";height=" + li_height + ";BORDER-RIGHT: #999999 1px solid; BORDER-BOTTOM: #999999 1px solid;\">\r\n");
		sb_html.append("    <img src=\"./applet/" + getLoginBgImgName(_request) + "\"  usemap=\"#newhref\" border=0>\r\n"); //上面可以有热点.width=" + li_width + " height=" + li_height + "
		sb_html.append(getLoginHrefMap()); //输出图上面的所有热点!!!
		sb_html.append("  </div>\r\n"); //
		sb_html.append("\r\n"); //
		int li_comp_start_x = 700; //
		int li_comp_start_y = 279; //
		String str_comp_startxy = new TBUtil().getSysOptionStringValue("HTML登录界面控件的起始位置", null); //这里是浏览器登录时的参数配置，在LoginPanel中也有个类似的配置“登录界面控件的起始位置”，是客户端登录时的参数配置！！！【李春娟/2012-07-03】
		if (str_comp_startxy != null && !str_comp_startxy.trim().equals("")) {
			li_comp_start_x = Integer.parseInt(str_comp_startxy.substring(0, str_comp_startxy.indexOf(","))); //
			li_comp_start_y = Integer.parseInt(str_comp_startxy.substring(str_comp_startxy.indexOf(",") + 1, str_comp_startxy.length())); //
		}
		sb_html.append("  <!-- 输出表单,X,Y微调参数[HTML登录界面控件的起始位置]=[" + str_comp_startxy + "] -->\r\n"); //
		sb_html.append("  <div id=\"div_form\" style=\"position:absolute;left:" + (li_comp_start_x + 0) + ";top:" + (li_comp_start_y + 0) + ";width=275;height=125;\">\r\n"); //中铁建中曾经为了位置弄成了要加10与40
		sb_html.append("    <form name=\"form1\" action=\"JavaScript:runJava();\" method=\"post\">\r\n"); //仍然提交给本页面
		sb_html.append("    <table border=0 style=\"WIDTH:275px\">\r\n");
		sb_html.append("      <tr><td align=\"right\" width=\"40%\"><font size=2>用户名:</font></td><td width=\"60%\"><input name=\"usercode\" type=\"text\"     class=\"style_1\"></td></tr>\r\n");
		sb_html.append("      <tr><td align=\"right\" width=\"40%\"><font size=2>密码:  </font></td><td width=\"60%\"><input name=\"pwd\"      type=\"password\" class=\"style_1\"></td></tr>\r\n");
		if ("Y".equalsIgnoreCase(_request.getParameter("admin"))) {
			sb_html.append("      <tr><td align=\"right\" width=\"40%\"><font size=2>管理密码:  </font></td><td width=\"60%\"><input name=\"adminpwd\" type=\"password\" class=\"style_1\" width=120\"></td></tr>\r\n"); //
		} else {
			if (tbUtil.getSysOptionBooleanValue("登录界面是否有验证码", false)) {
				sb_html.append("      <tr><td align=\"right\" width=\"40%\"><font size=2>验证码: </font></td><td width=\"60%\"><input name=\"checkcode\" type=\"text\" class=\"style_1\" width=60\"></tr>\r\n"); //
				sb_html.append("      <tr><td align=\"right\" width=\"40%\"></td><td width=\"60%\"><a href=\"#\"  id=\"info1\" style=\"dispaly:none;\"\"><a href=\"#\" title=\"获取验证码\" id=\"numLink\" onclick=\"getCode()\"><font size=2>获取验证码</font></a></td></tr>\r\n"); //
			}
		}
		if (ServerEnvironment.getInnerSys() != null && ServerEnvironment.getInnerSys().length > 0) { //如果有子系统,则输出子系统菜单!民生银行中把工商金融事业部与本放在一起!!本质上是一个系统,只需要换个脸!!!
			sb_html.append("      <tr><td align=\"right\" width=\"40%\"><font size=2>系统:  </font></td><td width=\"60%\">" + getAllInnerSys(_request) + "</td></tr>\r\n"); //
		}
		sb_html.append("      <tr><td align=\"right\" width=\"40%\">&nbsp;</td><td align=\"left\" width=\"60%\">\r\n");
		sb_html.append("        <input class=\"btn_1\" type=\"submit\" value=\"登录\" name=\"Submit\">&nbsp;\r\n");
		sb_html.append("        <input class=\"btn_1\" type=\"Reset\"  value=\"清空\" name=\"Reset\">&nbsp;\r\n"); //不要清空按钮了
		sb_html.append("      </td></tr>\r\n");
		sb_html.append("      <tr><td colspan=2>&nbsp;</td></tr>\r\n");
		sb_html.append("      <tr><td colspan=2 align=\"right\">\r\n");
		sb_html.append("        <a href=\"./help/webpushjre.exe\"><font size=2 color=\"blue\" title=\"这是Oracle公司官方标准JRE,若已安装则无需再安装!\r\n据有关统计,70%的企业用户都已安装Java环境!\r\n整个安装过程大约一分钟左右!\">Java运行环境</font></a>&nbsp;\r\n"); //以前想用Servlet的缓存使得下载更快些,但王雷在深圳农商中发现太多下载时Servlet好象卡住了,但直接从apache下载反而快!所以这里还是改成直接下载!因为这是从apache中下载静态文件,是非常快的!!而WebLogic是自己做缓存的!
		sb_html.append("        <a href=\"./help/loginhelp.html\" target=\"_blank\"><font size=2 color=\"blue\">登 录 说 明</font></a>&nbsp;\r\n");
		sb_html.append("      </td></tr>\r\n");
		sb_html.append("    </table>\r\n");
		sb_html.append("   </form>\r\n"); //
		sb_html.append("  </div>\r\n");
		sb_html.append("</div>\r\n");

		if (!isRunJava2) { //只有第一种才输出提示下载插件的层,第二种方式则是直接在Delphi的Activex中进行处理的!!!
			sb_html.append(getPopDiv()); //弹出的层!!!
		}
		sb_html.append("</body>\r\n");
		sb_html.append("</html>\r\n"); //
		return sb_html.toString();
	}

	public String getSingleLogin(HttpServletRequest _request, HttpServletResponse _response) throws UnsupportedEncodingException {
		return getSingleLogin(_request, _response, true, null); //
	}

	/**
	 * 单点登录,或者说是直接登录的界面逻辑,即跳过第一个Html页面,直接输出<applet></applet>
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public String getSingleLogin(HttpServletRequest _request, HttpServletResponse _response, boolean _isClose, String _text) throws UnsupportedEncodingException {
		_request.setCharacterEncoding("GBK"); //
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<html>\r\n");
		sb_html.append("<head>\r\n");
		sb_html.append("<TITLE>" + ServerEnvironment.getProperty("PROJECT_NAME") + "</TITLE>\r\n"); //标题名
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
				sb_html.append("单点登录校验失败(1)...</span>&nbsp;&nbsp;"); //这里要加一个参数,指定单点登录时的的提示信息!
			}else{
				sn = URLEncoder.encode(sn);
			}
			String usercode = _request.getParameter("usercode");
			if((TBUtil.isEmpty(usercode))){
				flag = false;
				sb_html.append("单点登录校验失败(2)...</span>&nbsp;&nbsp;"); //这里要加一个参数,指定单点登录时的的提示信息!
			}
			if(!sn.equals(ServerEnvironment.getSingleEncrypt(usercode))){
				flag = false;
				sb_html.append("单点登录校验失败(3)...</span>&nbsp;&nbsp;"); //这里要加一个参数,指定单点登录时的的提示信息!
			}
			ServerEnvironment.removeSingleEncrypt(usercode);
			if(!flag){
				sb_html.append("</body>\r\n"); //
				sb_html.append("</html>\r\n"); //
				return sb_html.toString(); //
			}
		}
		sb_html.append("<script language=\"JavaScript\">  \r\n"); //
		sb_html.append(" var v_intervalID=-1;  //用来存储创建循环器后返回的id,在停止时必须传入这个ID作为参数 \r\n"); //
		sb_html.append(" var v_temp1=0; //提供两个数字与字符临时变量,因为循环逻辑中肯定可能会遇到一个临时变量用来存储的! \r\n"); //
		sb_html.append(" var v_temp2=0; \r\n"); //
		sb_html.append(" var v_temp3=''; \r\n"); //
		sb_html.append(" var v_temp4=''; \r\n"); //

		sb_html.append(getJSFunctions(_request, 2)); //输出一堆JS函数!!
		String str_exitIECookie = ServerEnvironment.getProperty("PushMonitorIEExit"); //监控IECookie的标记!!
		String str_refreshJS = getTBUtil().getSysOptionStringValue("单点登录跳转页面JS刷新线程代码", null); //中铁项目中遇到监听EAC注销时要通过变态的循环刷新去判断注销层的来处理,这个代码是完全项目个性化的,所以必须可配置!因为这种代码很少,所以存在系统参数中算了!!
		boolean isRunJava2 = isRunCall2(_request); //
		int li_closetime = 2500; //默认2.5秒后关闭
		if (_request.getParameter("closetime") != null) {
			li_closetime = Integer.parseInt(_request.getParameter("closetime")); //
		}
		if (!isRunJava2) { //第一种方式
			sb_html.append("\r\n<!-- 执行本地Java程序 -->\r\n"); //
			sb_html.append("function runJava(){ \r\n"); //0.5秒钟后直接调用该JS脚本
			sb_html.append(getCheckCookie(false)); //检查Cookie!
			if (str_exitIECookie != null && !str_exitIECookie.trim().equals("")) {
				sb_html.append("   setCookie('" + str_exitIECookie + "',''); //因为定认了参数PushMonitorIEExit,而且是IE跳转的,所以先清空下该Cookie \r\n"); //清空Cookie,既然在退出时设置了标记,就要有地方清空一下! 否则可能会造成永远起不来!
			}
			if (str_refreshJS != null && !str_refreshJS.trim().equals("") && !_isClose) { //如果定义,而且是不关闭的.关闭的话则没有循环意义了!
				sb_html.append("   v_intervalID=setInterval(\"refreshThread()\",500); //因为定义了刷新线程代码 \r\n"); //每0.5秒刷新一次
			}
			sb_html.append("   document.getElementById(\"ProcessInfo\").innerHTML='系统正在打开页面...[' + getCurrTime('2') + ']'; \r\n"); //在调用本地之前,输出当前时间,然后在Java程序中也有时间,通过两者差异,好知道操作系统通过注册表调起本程序要多长时间!! 因为有人Win7机器较慢,怀疑是不是这一步慢?
			sb_html.append("   self.location='" + getRegeditCode() + "://localjava/call?' + getClientRequestUrl() + 'oldversionpar=url;user;pwd;adminpwd';  //跳转执行本地Java程序 \r\n"); //之所以有个oldversionpar是为了兼容支持以前以分号相隔的入参机制,否则老的机制使用浏览器登录时会跑不起来!!
			if (_isClose) { //如果单点登录合需要关闭自己
				sb_html.append("   setTimeout(\"closeThisWindow();\"," + li_closetime + "); //3秒后关闭原来窗口 \r\n"); //考虑到第一次访问启动Java进程有点慢,所以时间放长一点,这样不会让客户觉得系统"不见了"!!!!
			}
			sb_html.append("} \r\n\r\n");
		} else {
			sb_html.append("\r\n<!-- 使用Activex调Java,浏览器登录初期发生异常无法查看日志时,可以将C:\\WebPushJRE\\..\\java6.exe改名为javaw6.exe -->\r\n"); //
			sb_html.append("function runJava(){ \r\n");
			if (str_exitIECookie != null && !str_exitIECookie.trim().equals("")) {
				sb_html.append("   setCookie('" + str_exitIECookie + "',''); //因为定认了参数PushMonitorIEExit,而且是IE跳转的,所以先清空下该Cookie \r\n"); //清空Cookie,既然在退出时设置了标记,就要有地方清空一下! 否则可能会造成永远起不来!
			}
			if (str_refreshJS != null && !str_refreshJS.trim().equals("") && !_isClose) { //如果定义,而且是不关闭的.
				sb_html.append("   v_intervalID=setInterval(\"refreshThread()\",500); \r\n"); //每0.5秒刷新一次
			}
			sb_html.append("   document.getElementById(\"ProcessInfo\").innerHTML='系统正在打开页面..[' + getCurrTime('2') + ']';\r\n"); //
			sb_html.append(getRunJava2(true)); //使用Activex调Java
			if (_isClose) { //如果单点登录合需要关闭自己
				sb_html.append(" else {\r\n");
				sb_html.append("   setTimeout(\"closeThisWindow();\"," + li_closetime + "); //3秒后关闭原来窗口 \r\n");
				sb_html.append("  }\r\n");
			}
			sb_html.append("} \r\n\r\n");
		}

		sb_html.append("<!-- 退出窗口时往剪粘板中写标记数据,中铁建项目中与EAC集成时需要这个 -->\r\n"); //
		sb_html.append("function unLoadWebPush(){ \r\n");
		if (!_isClose && str_exitIECookie != null && !str_exitIECookie.trim().equals("")) {
			String str_closeWinJS = getTBUtil().getSysOptionStringValue("单点登录关闭窗口JS代码", null);
			if (str_closeWinJS != null && !str_closeWinJS.trim().equals("")) { //如果定义了参数则使用之
				sb_html.append(str_closeWinJS + "\r\n"); //
			} else { //
				sb_html.append("  if(window.event.clientX<0 || window.event.clientY<0 || event.altKey) { //网上资料说区别真正关闭窗口与刷新窗口只能通过这个判断,只是有点变态! \r\n"); //因为窗口真正关闭后才会造成x,y都小于0
				sb_html.append("    setCookie('" + str_exitIECookie + "','exit'); //设置Cookie值! \r\n"); //设置cookie标记为exit!以前是想self.location,或弹出窗口的,但后来证明都不行!
				sb_html.append("  } \r\n"); //
			}
		} else {
			sb_html.append("<!-- 没有在weblight.xml中义参数PushMonitorIEExit,所以不做逻辑! -->\r\n");
		}
		sb_html.append("} \r\n");

		sb_html.append("\r\n<!-- 如果定义了参数[单点登录跳转页面JS刷新线程代码],而且是运行Jaav后不关闭本窗口,则有循环调用本方法! -->\r\n"); //
		sb_html.append("function refreshThread(){ \r\n");
		if (str_refreshJS != null && !str_refreshJS.trim().equals("") && !_isClose) {
			sb_html.append(str_refreshJS); //加入
		}
		sb_html.append("\r\n} \r\n");

		sb_html.append("\r\n<!-- 停止刷新线程,在上面处理逻辑中肯定会遇到一个判断而决定停止的! -->\r\n");
		sb_html.append("function stopRefreshThread(){ \r\n");
		sb_html.append("   clearInterval(v_intervalID); \r\n");
		sb_html.append("} \r\n");

		if (!isRunJava2) { //
			sb_html.append("\r\n"); //
			sb_html.append(getJS_downloadJRE()); //弹出窗口下载JRE
		}
		sb_html.append("</script> \r\n"); //
		sb_html.append("</head>\r\n");
		sb_html.append("<body style=\"background-color:#E7EEF8;background-image:URL(./applet/main_bgimg.gif);background-repeat:no-repeat;\" onLoad=\"runJava();\" onbeforeUnload=\"unLoadWebPush();\">\r\n");
		sb_html.append("<span id=\"ProcessInfo\" style=\"FONT-SIZE:12px; COLOR:#000000\">"); //
		if (_text == null) {
			sb_html.append("单点登录处理中,请稍候...</span>&nbsp;&nbsp;"); //这里要加一个参数,指定单点登录时的的提示信息!
		} else {
			sb_html.append(_text + "</span>&nbsp;&nbsp;");
		}
		sb_html.append("<span id=\"hidespan\" style=\"FONT-SIZE:12px; COLOR:#000000\" onclick=\"alert(document.cookie);\">&nbsp;&nbsp;&nbsp;</span>"); //这里偷偷放一个看不出来span,用于点击后查看cookie!即用户看不到,但我们看到
		sb_html.append("<br>\r\n");

		sb_html.append("<p style=\"FONT-SIZE:12px; COLOR:#000000\">\r\n");
		String str_req_info = _request.getParameter("FRAME_TITLE"); //
		if (str_req_info != null) {
			try {
				str_req_info = new String(str_req_info.getBytes("ISO-8859-1"), "GBK"); //好象必须要转一下!!!否则中文乱码,在航天项目中传入前就转成了16进制,所以更稳定,但经过这行处理也是没有问题的!
				//System.out.println("客户端传入的参数[" + str_parKeys[i] + "]=[" + str_value + "]"); //Debug中文是否乱码了??
			} catch (Exception ex) {
				System.err.println("LoginJSPUtil.getSingleLogin()转换字符发生异常:" + ex.getClass() + ":" + ex.getMessage()); //
			}
			sb_html.append("系统加载的页面是:【" + str_req_info + "】<br>\r\n");
		}
		sb_html.append("</p>"); //

		//如果有自定义的界面!!!(应该是部分Html)
		String str_custhtml = (String) ServerEnvironment.getInstance().getCustMapValue("singlemain.htm"); //做缓存
		if (str_custhtml == null) { //如果没有,则创建并塞入缓存!
			InputStream ins_mainhtml = this.getClass().getResourceAsStream("/singlemain.html"); //如果有这个文件,则加载这个文件!!!
			if (ins_mainhtml != null) { //
				String str_html = getTBUtil().readFromInputStreamToStr(ins_mainhtml); //读一下!
				str_custhtml = "\r\n\r\n<!-- 从%ClassPath%/singlemain.html文件中读取的内容 -->\r\n" + str_html + "\r\n<!-- 从%ClassPath%/singlemain.html文件中读取的内容结束!! -->\r\n\r\n"; //sb_html.toString(); //
				ServerEnvironment.getInstance().putCustMap("singlemain.htm", str_custhtml); //塞入数据!!!
			} else {
				str_custhtml = "\r\n<!-- ClassPath根路径没有自定义的singlemain.htm,故忽略(如果想要在这里输出客户自定义的帮助说明,就创建一个singlemain.htm文件,并放在WEB-INF/classes目录下).. -->\r\n"; //
				ServerEnvironment.getInstance().putCustMap("singlemain.htm", str_custhtml); //
			}
		}
		sb_html.append(str_custhtml); //

		if ("Y".equalsIgnoreCase(ServerEnvironment.getProperty("ISRemindLoadType"))) { //是否提醒两种加载方式!
			sb_html.append("<br><br>\r\n"); //
			sb_html.append("<p class=\"p\">\r\n");
			sb_html.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;当前登录方式是[" + (isRunJava2 ? "第二种登录方式" : "默认登录方式") + "],如果不能进入系统,可选择另外一种登录方式试试!<br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\r\n");
			sb_html.append("<input type=\"button\" class=\"style_1\" value=\"默认登录方式\" onclick=\"JavaScript:setCookie('WebPushRunJavaType','1');alert('设置默认登录方式成功,但需要重新登录才能起效!')\">&nbsp;&nbsp;\r\n"); //
			sb_html.append("<input type=\"button\" class=\"style_1\" value=\"第二种登录方式\" onclick=\"JavaScript:setCookie('WebPushRunJavaType','2');alert('设置第二种方式成功,但需要重新登录才能起效!')\"><br>\r\n"); //
			sb_html.append("</p>"); //
		}

		//sb_html.append("<table width=\"100%\"><tr><td align=\"right\"><img src=\"./applet/main_bgimg.jpg\"></td></tr></table>\r\n"); //搞个图片!!!
		if (!isRunJava2) { //第一种方式
			sb_html.append(getPopDiv()); //
		} else {
			sb_html.append(getActivexDiv()); //
		}

		sb_html.append("\r\n\r\n<!--"); //
		sb_html.append("所有参数:\r\n"); //
		Map parMap = _request.getParameterMap(); //
		String[] str_keys = (String[]) parMap.keySet().toArray(new String[0]); ////
		for (int i = 0; i < str_keys.length; i++) {
			String str_value = ((String[]) parMap.get(str_keys[i]))[0]; //
			try {
				str_value = new String(str_value.getBytes("ISO-8859-1"), "GBK"); //好象必须要转一下!!!否则中文乱码,在航天项目中传入前就转成了16进制,所以更稳定,但经过这行处理也是没有问题的!
			} catch (Exception ex) {
				System.err.println("LoginJSPUtil.getSingleLogin()转换字符发生异常:" + ex.getClass() + ":" + ex.getMessage()); //
			}
			sb_html.append("[" + str_keys[i] + "]=[" + str_value + "]\r\n");
		}
		sb_html.append("-->\r\n\r\n"); //
		sb_html.append("</body>\r\n"); //
		sb_html.append("</html>\r\n"); //
		return sb_html.toString(); //
	}

	/**
	 * 以前通过监听IE退出时调用Java程序写剪粘板来关闭JavaSwing,但反复试验发现有问题,因为安全原因触发不了,即关闭窗口后既不能通过self.location=webpush20100601://localjava?来调用,也不能通过弹出窗口来调用!
	 * 所以后来重新想了办法就是退出时往IE中写Cookie值!然后在Swing中通过JDIC在后台加载一个Html,这个html中不断刷新cooike中的值,发现IE退出时写的值,则立即通过修改窗口标题来通知Swing,Swing接到后就退出Java!!
	 * 所以需要这个一个不断刷新的Html,就是本方法实现的逻辑
	 * @return
	 */
	public String getMonitorIECookieHtml() {
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<html>\r\n");
		sb_html.append("<head>\r\n"); //
		sb_html.append("<META http-equiv=Content-Type content=\"text/html; charset=GBK\">\r\n"); //
		sb_html.append("<title>MyTitle</title>\r\n"); //
		String str_MonitorCookieKey = ServerEnvironment.getProperty("PushMonitorIEExit"); //必须在weblight.xml中定义
		if (str_MonitorCookieKey != null && !str_MonitorCookieKey.trim().equals("")) {
			sb_html.append("<script language=\"JavaScript\">\r\n\r\n"); //
			sb_html.append("<!-- 加载页面时调用的方法 -->\r\n"); //
			sb_html.append("function myOnLoad(){ \r\n");
			sb_html.append(" refreshCookie(); \r\n"); //先调一下!否则会滞后一个循环时间
			sb_html.append(" setInterval(\"refreshCookie()\",500); \r\n"); //每0.5秒刷新一次
			sb_html.append("} \r\n\r\n"); // 

			sb_html.append("<!-- 刷新取Cookie值 -->\r\n"); //
			sb_html.append("function refreshCookie(){ \r\n"); //
			sb_html.append("  var newDate = new Date(); \r\n"); //
			sb_html.append("  var v_currtime = newDate.getFullYear() + '-' + (newDate.getMonth()+1) + '-' + newDate.getDate() + ' ' + newDate.toLocaleTimeString() + ':' + newDate.getMilliseconds(); \r\n"); //
			sb_html.append("  var v_cookievalue = getCookie('" + str_MonitorCookieKey + "'); \r\n"); //
			sb_html.append("  document.form1.text1.value='[' + v_currtime + '],Cookie值[" + str_MonitorCookieKey + "]=[' + v_cookievalue + ']'; \r\n"); //通过这个来知道到底有没有在刷新!
			sb_html.append("  if(v_cookievalue=='exit'){ \r\n"); //
			sb_html.append("     var v_oldTitle = document.title;\r\n"); //原来的标题
			sb_html.append("     if(v_oldTitle != '" + str_MonitorCookieKey + "@exit') { \r\n"); //如果与原来的标题一样则不触发,否则会造成触发多次,会把JDIC的线程弄死? 或者做一个最多触发3次的逻辑!!
			sb_html.append("       document.title='" + str_MonitorCookieKey + "@exit'; \r\n"); //重新设置标题,每调一下这行代码,就会触发到Swing中一次,所以上面要做一个判断,即如果没变化则不触发!!
			sb_html.append("     }\r\n"); //
			sb_html.append("  } \r\n"); //
			sb_html.append("} \r\n\r\n"); // 

			sb_html.append(getJS_getCookie()); //取Cookie值!!!

			sb_html.append("</script> \r\n");
			sb_html.append("</head>\r\n");
			sb_html.append("<body onload='javaScript:myOnLoad();'>\r\n"); //
			sb_html.append("<form name=\"form1\"><input type=\"text\" name=\"text1\" value=\"最新时间:[]\" size=60><input type=\"button\" value=\"查看所有Cookie\" onClick=\"alert(document.cookie);\"></form>该页不断刷新获取cookie中的值,如果发现特定值就触发!\r\n"); //
		} else {
			sb_html.append("</head>\r\n");
			sb_html.append("<body>\r\n");
			sb_html.append("weblight.xml中没有定义参数[PushMonitorIEExit],所以不进行刷新逻辑!\r\n");
		}
		sb_html.append("</body>\r\n"); //
		sb_html.append("</html>\r\n"); //
		return sb_html.toString(); //
	}

	//判断是否以第二种方式运行,即使用Activex调用Java的方式!!!
	private boolean isRunCall2(HttpServletRequest _request) {
		String str_par = _request.getParameter("runjava"); //
		if ("2".equalsIgnoreCase(str_par)) { //如果强行指定了,则直接返回True
			return true;
		}

		//如果没有指定,再看cookie!
		Cookie[] cookies = _request.getCookies(); //取得所有Cookie
		if (cookies != null && cookies.length > 0) {
			for (int i = 0; i < cookies.length; i++) {
				if ("WebPushRunJavaType".equals(cookies[i].getName()) && "2".equals(cookies[i].getValue())) { //如果cookie有个变量叫WebPushRunJavaType,且值为2,则认为是第二种!!
					return true;
				}
			}
		}
		return false; //否则认为是第一种
	}

	//登录界面背景图片名称!!
	private String getLoginBgImgName(HttpServletRequest _request) {
		String str_logingifname = "login_default.gif"; //
		if (_request.getParameter("isys") != null) { //如果指定了isys,即指定了指系统
			str_logingifname = "login_" + _request.getParameter("isys") + ".gif"; //
		} else {
			if (ServerEnvironment.getInnerSys() != null && ServerEnvironment.getInnerSys().length > 0) {
				str_logingifname = "login_" + ServerEnvironment.getInnerSys()[0][0] + ".gif"; //如果定义子子系统,则默认使用第一个子系统的背景,也就是母系统的背景!
			} else {
				if (ServerEnvironment.getProperty("PROJECT_SHORTNAME") != null && !ServerEnvironment.getProperty("PROJECT_SHORTNAME").trim().equals("")) {
					str_logingifname = "login_" + ServerEnvironment.getProperty("PROJECT_SHORTNAME") + ".gif"; //
				}
			}
		}
		return str_logingifname;
	}

	//第一个登录输出上的所有热点(比如帮助等超链接!!
	private String getLoginHrefMap() {
		StringBuilder sb_html = new StringBuilder(""); //
		String[][] str_hrefs = ServerEnvironment.getLoginHref(); //
		if (str_hrefs != null && str_hrefs.length > 0) {
			sb_html.append("    <map name=\"newhref\">\r\n"); ////循环输出各个热点..
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

	//所有子系统,即一个拉菜单!!!
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

	//校验是否有Cookie,然后决定是否弹出下载框!!
	private String getCheckCookie(boolean _isLogin) {
		//如果Weblight.xml中配置了这个参数, 就不查Cookie了
		//解决部门银行定期清除Cookie的问题
		if ("N".equalsIgnoreCase(ServerEnvironment.getProperty("CHECKCOOKIE"))) {
			return "";
		}

		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("  var v_cookie = getCookie('WebPushJREInstalled'); \r\n"); //看是否有Cookie值???
		sb_html.append("  if(v_cookie=='') { \r\n"); //如果Cookie为空,则提示安装!!!
		sb_html.append("    var v_div = document.getElementById('div_pop');\r\n"); //
		sb_html.append("    v_div.style.left=" + (_isLogin ? "500" : "50") + ";\r\n"); //  
		sb_html.append("    v_div.style.top=" + (_isLogin ? "300" : "50") + ";\r\n"); // 
		sb_html.append("    v_div.style.display = '';\r\n"); //显示层
		sb_html.append("    return; \r\n"); //
		sb_html.append("  }\r\n"); //
		return sb_html.toString(); //
	}

	//所有JavaScript函数!!
	private String getJSFunctions(HttpServletRequest _request, int _type) {
		StringBuilder sb_html = new StringBuilder(); //
		String str_logintype = _request.getParameter("logintype"); //
		if (_type == 1 || _type == 2) { //
			sb_html.append("<!--取得浏览器请求的地址-->\r\n"); //
			sb_html.append("function getClientRequestUrl() { \r\n"); //
			sb_html.append("  var v_url = String(window.location); \r\n"); //
			sb_html.append("  if(v_url.indexOf('#')>0) {v_url = v_url.substring(0, v_url.indexOf('#'));}\r\n");
			sb_html.append("  if(v_url.indexOf('?')>0) {\r\n"); //如果有问号,说明已有参数,则再接参数就必须是&
			sb_html.append("    v_url = v_url + '&';\r\n"); //
			sb_html.append("  } else { \r\n"); //
			sb_html.append("    v_url = v_url + '?';\r\n"); //
			sb_html.append("  } \r\n"); //

			//中铁建中集成时需要调整窗口位置与大小,正好嵌在主界面的位置,形成一种假集成的效果!!只要从请求url中传入参数FRAME_X,FRAME_Y,FRAME_WIDTH,FRAME_HEIGHT等参数就可以,但事实上可以不传而直接在这里取得!!从而省去二次开发人员的工作量!!!
			if (_type == 2 && "Y".equalsIgnoreCase(ServerEnvironment.getProperty("ISBedFrameCenter")) && ("skip".equals(str_logintype) || "skip2".equals(str_logintype))) { //必须指定该参数!!!后来感觉应该区分 IELogin/skip/single三种指定,如能skip时需要这样处理,但IE登录时却不一定需要!!
				sb_html.append("  var v_x = window.screenLeft;  \r\n"); //
				sb_html.append("  var v_y = window.screenTop;\r\n"); //
				sb_html.append("  var v_width = document.body.clientWidth;\r\n"); //
				sb_html.append("  var v_height = document.body.clientHeight;\r\n"); //
				if (_request.getParameter("FRAME_X") == null) { //如果传入了
					String str_adjust = ServerEnvironment.getProperty("FRAME_X_ADJUST"); //有的门户系统并不是使用FrameSet技术,这样取得的位置是整个浏览器中的内容的位置!但实际上要去掉上面标题栏与左边菜单树的空间,所以存在一个修正参数,进行修正一下位置与大小,从而保证完美嵌在其中!!
					if (str_adjust != null && !str_adjust.trim().equals("")) { //如果有修正尺寸!!!
						sb_html.append("  v_x = v_x" + (str_adjust.startsWith("-") ? str_adjust : "+" + str_adjust) + "; //有调整参数\r\n"); //要考虑一下负数
					}
					sb_html.append("  v_url = v_url + 'FRAME_X=' + v_x + '&';\r\n"); //
				}
				if (_request.getParameter("FRAME_Y") == null) { //如果传入了
					String str_adjust = ServerEnvironment.getProperty("FRAME_Y_ADJUST"); //
					if (str_adjust != null && !str_adjust.trim().equals("")) { //如果有修正尺寸!!!
						sb_html.append("  v_y = v_y" + (str_adjust.startsWith("-") ? str_adjust : "+" + str_adjust) + "; //有调整参数\r\n"); //要考虑一下负数
					}
					sb_html.append("  v_url = v_url + 'FRAME_Y=' + v_y + '&';\r\n"); //
				}
				if (_request.getParameter("FRAME_WIDTH") == null) { //如果传入了
					String str_adjust = ServerEnvironment.getProperty("FRAME_WIDTH_ADJUST"); //
					if (str_adjust != null && !str_adjust.trim().equals("")) { //如果有修正尺寸!!!
						sb_html.append("  v_width = v_width" + (str_adjust.startsWith("-") ? str_adjust : "+" + str_adjust) + "; //有调整参数\r\n"); //要考虑一下负数
					}
					sb_html.append("  v_url = v_url + 'FRAME_WIDTH=' + v_width + '&';\r\n"); //
				}
				if (_request.getParameter("FRAME_HEIGHT") == null) { //如果传入了
					String str_adjust = ServerEnvironment.getProperty("FRAME_HEIGHT_ADJUST"); //
					if (str_adjust != null && !str_adjust.trim().equals("")) { //如果有修正尺寸!!!
						sb_html.append("  v_height = v_height" + (str_adjust.startsWith("-") ? str_adjust : "+" + str_adjust) + "; //有调整参数\r\n"); //要考虑一下负数
					}
					sb_html.append("  v_url = v_url + 'FRAME_HEIGHT=' + v_height + '&';\r\n"); //
				}
			}
			sb_html.append("  return v_url;  //以前是在服务器端得IP与端口的,但后来遇到有的客户进行了Http转换,所以必须拿客户端的地址 \r\n");
			sb_html.append("} \r\n\r\n"); //
		}

		sb_html.append(getJS_setCookie()); //设置Cookie值!

		if (_type == 1 || _type == 2) { //
			sb_html.append(getJS_getCookie()); //取Cookie值!!

			sb_html.append("<!--下载客户端文件-->\r\n"); //
			sb_html.append("function downClientFile() { \r\n"); //
			//sb_html.append("if(window.confirm(\"安装过程中可能会出现安全警告,请选择允许!\")==true){\r\n"); //这是因为Win7下面在安装最后结束时试图写注册表时有警告提醒!!如果选择否,则会导致不能访问,只能手工直接再次运行%JRE_HOME%\bin\wpreg.reg
			sb_html.append("     window.open(\"./DownLoadFileServlet?filename=/help/webpushjre.exe&iscachefile=Y\",\"download\",\"scrollbars=no,status=no,toolbar=no,location=no,menubar=no,top=100,left=10,width=800,height=150\"); \r\n"); //下载的文件永远叫webpushjre.exe!简化!!!
			//sb_html.append(" }\r\n"); //
			sb_html.append("}\r\n\r\n");
		}

		sb_html.append(getJS_closeThisWindow()); //关闭窗口的JS
		sb_html.append(getJS_getCurrTime()); //取当前时间的JS
		return sb_html.toString(); //
	}

	/**
	 * 设置Cookie的JS函数代码
	 * @return
	 */
	private String getJS_setCookie() {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<!--设置Cookie值-->\r\n"); //
		sb_html.append("function setCookie(_name,_value) {\r\n"); //
		sb_html.append("  var exp  = new Date(\"December 31, 3000\"); //失效日期是3000-12-31\r\n"); //
		sb_html.append("  document.cookie = _name + '=' + escape (_value) + ';expires=' + exp.toGMTString(); \r\n"); //
		sb_html.append("}\r\n\r\n"); //
		return sb_html.toString(); //
	}

	private String getJS_getCode() {
		Integer time1 = getTBUtil().getSysOptionIntegerValue("短信验证码再次获取时间", 30);
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("function getCode() {\r\n"); //
		sb_html.append(" var i = " + time1 + "; var info1  = document.getElementById('info1');\r\n");
		sb_html.append(" var numLink  = document.getElementById('numLink');\r\n");
		sb_html.append("  var v_usercode = document.form1.usercode.value; \r\n");
		sb_html.append("  var v_userpwd = document.form1.pwd.value; \r\n");
		sb_html.append("  if(v_usercode=='' || v_userpwd=='') { alert('用户名和密码不能为空!');   return ; } \r\n"); //
		sb_html.append("var xmlhttp = getAjax();xmlhttp.onreadystatechange = function() {\r\n");
		sb_html.append("if (4 == xmlhttp.readyState) {if (200 == xmlhttp.status) { var res = xmlhttp.responseText;");
		sb_html.append("if(res == 1){alert('在线用户已达上限!'); }");
		sb_html.append("else if(res ==2){alert('用户不存在!'); }");
		sb_html.append("else if(res == 3){alert('系统发现用户重复!'); }");
		sb_html.append("else if(res == 4){alert('用户权限尚未开通!'); }");
		sb_html.append("else if(res == 5){alert('用户已封停!'); }");
		sb_html.append("else if(res == 6) {alert('发送成功!');numLink.style.display='none';var timer1 = setInterval(function(){info1.innerHTML='<font size=2>' + i + '秒后可再次获取!</font>';i--;if(i==0){info1.innerHTML='';clearInterval(timer1); info1.style.display='none';numLink.style.display='block'; }}, 1000);}");
		sb_html.append("else if(res == 7){alert('发送失败!'); }");
		sb_html.append("else if(res == 9){alert('手机号为空!'); }");
		sb_html.append("else if(res == 10){alert('验证码获取时间间隔为" + time1 + "秒!'); }");
		sb_html.append("else if(res == 11){alert('用户密码错误!'); }");
		sb_html.append("else {alert('发生错误请稍后再试!'); }}}\r\n");
		sb_html.append(" }\r\n");
		sb_html.append("xmlhttp.open(\"get\", \"./WebCallServlet?StrParCallClassName=" + getTBUtil().getSysOptionStringValue("短信验证服务逻辑", "cn.com.psbc.bs.message.CheckCodeService") + "&usercode=\"+v_usercode+\"&pwd=\"+v_userpwd, true);\r\n");
		sb_html.append("if(xmlhttp.readyState==1){xmlhttp.send(\"\");}else{alert('发生错误请稍后再试!');}\r\n");
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
	 * 获取Cookie值的JS函数代码
	 * @return
	 */
	private String getJS_getCookie() {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<!--取Cookie值-->\r\n"); //
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
	 * 关闭窗口的JS
	 * @return
	 */
	private String getJS_closeThisWindow() {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<!--关闭窗口,但这个函数在FireFox中不起效果,好象是window.close()没作用-->\r\n"); //
		sb_html.append("function closeThisWindow() { \r\n"); //
		sb_html.append("  window.opener=null;\r\n"); //IE6不知为什么必须要这样写!
		sb_html.append("  window.open('','_self');\r\n"); //
		sb_html.append("  window.close();\r\n"); //
		sb_html.append("}\r\n\r\n");
		return sb_html.toString(); //
	}

	/**
	 * 取得当前时间!!!
	 * @return
	 */
	private String getJS_getCurrTime() {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<!--取得当前时间,许多地方用到-->\r\n"); //
		sb_html.append("function getCurrTime(_type) {\r\n"); //
		sb_html.append("  var newDate=new Date();\r\n"); //
		sb_html.append("  var v_currtime = newDate.getFullYear() + '-' + (newDate.getMonth()+1) + '-' + newDate.getDate() + ' ' + newDate.toLocaleTimeString();\r\n"); //
		sb_html.append("  if('2'==_type) { \r\n");
		sb_html.append("    v_currtime = v_currtime + ':' + newDate.getMilliseconds(); \r\n"); //加上毫秒
		sb_html.append("  }\r\n"); //
		sb_html.append("  return v_currtime;\r\n"); //
		sb_html.append("}\r\n\r\n"); //
		return sb_html.toString(); //
	}

	/**
	 * 下载JRE!!
	 * @return
	 */
	private String getJS_downloadJRE() {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<!--弹出窗口下载JRE-->\r\n"); //
		sb_html.append("function downloadJRE(){ \r\n");
		sb_html.append("  document.getElementById('div_pop').style.display='none'; \r\n"); //先关闭提示框!
		boolean isdirectlyload = !("N".equalsIgnoreCase(ServerEnvironment.getProperty("ISJREDIRECTLYDOWNLOAD"))); //默认是直接下载，如果配置为“N”才是使用王钢的Activex下载，李春娟补充！【李春娟/2012-07-09】
		String str_download_1 = "    window.open('./help/webpushjre.exe','_blank','top=300,left=300,width=500,height=200,toolbar=no,location=no,menubar=no'); \r\n"; //
		String str_download_2 = "    window.open('./login?logintype=activexsharejre','share','top=300,left=300,width=650,height=200,toolbar=no,location=no,menubar=no,resizable=yes'); \r\n"; //打开新窗口,王钢写的Activex控件!!,使用Activex判断,绑定或下载JRE,如果已安装JRE则进行绑定,即将安装JRE的问题踢给用户!!不再为JRE太大而纠结!!
		sb_html.append("  if(window.event.ctrlKey){ \r\n");
		sb_html.append(isdirectlyload ? str_download_2 : str_download_1); //
		sb_html.append("  } else { \r\n");
		sb_html.append(isdirectlyload ? str_download_1 : str_download_2); //
		sb_html.append("  } \r\n");
		sb_html.append("} \r\n\r\n");
		return sb_html.toString(); //
	}

	/**
	 * 如果没有装插件,则弹出一个层提示说要下载插件!!     
	 * @return
	 */
	private String getPopDiv() {
		StringBuilder sb_html = new StringBuilder(); //
		String str_msg = "首次访问需要安装插件,请选择!"; //
		sb_html.append("\r\n");
		sb_html.append("<!--弹出提示下载客户端的层-->\r\n"); //
		sb_html.append("<div id=\"div_pop\" style=\"width:450px;height:115px;background:#FFFFFF;position:absolute;BORDER:#4F4F4F 1px solid;display:none\">\r\n"); //
		sb_html.append("  <div style=\"RIGHT: 4px; POSITION: absolute; TOP: 5px\"><a onclick=\"JavaScript:document.getElementById('div_pop').style.display='none';\" href=\"javascript:;\" style=\"FONT-SIZE:12px;COLOR:#0000FF;\">关闭</a></div>\r\n"); //
		sb_html.append("  <div id=div_pop_body style=\"PADDING-RIGHT: 20px; PADDING-LEFT: 50px; PADDING-BOTTOM: 20px; PADDING-TOP: 15px\">\r\n"); //
		sb_html.append("    <p style=\"FONT-SIZE:12px;COLOR:#FF0000;\" title=\"如果你已安装过了,只是清空浏览器Cookie后又出现了该提醒,则不需要重新安装!\">" + str_msg + "<br/></p>\r\n");
		boolean isdirectlyload = !("N".equalsIgnoreCase(ServerEnvironment.getProperty("ISJREDIRECTLYDOWNLOAD"))); //是否直接下载还是使用王钢的Activex下载??,默认时是Y
		String str_tooltip = ""; //
		if (isdirectlyload) {//只有配置为“N”时 点击 “安装插件” 才直接调用Activex绑定或下载JRE
			str_tooltip = "下载并安装插件,全过程大约1分钟左右!\r\n按住Ctrl键点击可绑定或下载!"; //
		} else {//直接下载
			str_tooltip = "下载并安装插件,全过程大约1分钟左右!\r\n按住Ctrl键点击可直接下载!"; //
		}
		sb_html.append("    <input type=\"button\" value=\"·安装插件\"  title=\"" + str_tooltip + "\" onclick=\"JavaScript:downloadJRE();\" class=\"style_1\">\r\n"); //
		sb_html.append("    <input type=\"button\" value=\"·已安装,不要再提醒\" title=\"如果的确已安装过插件,因为清空浏览器Cookie或缓存又造成提醒,则直接点击本按钮!\" onclick=\"JavaScript:setCookie('WebPushJREInstalled','Y');document.getElementById('div_pop').style.display='none';runJava();\" class=\"style_1\">&nbsp;&nbsp;\r\n"); //设置Cookie值,并关闭!!
		sb_html.append("  </div>\r\n"); //
		sb_html.append("</div>\r\n"); //
		return sb_html.toString(); //
	}

	private String getActivexDiv() {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("  <!-- 输出Activex控件 -->\r\n"); //
		sb_html.append("  <div id=\"ActivexDiv\" style=\"position:absolute;left:0px;top:0px;width:10px;height:5px;background:#FFFFFF;BORDER:#4F4F4F 0px solid;FILTER:alpha(opacity=95)\">\r\n"); //
		sb_html.append("    <OBJECT name=\"WebPushOCX\" classid=\"clsid:87477552-7850-4E33-8C9F-4AA6DAC7F154\" codebase=\"./applet/WebPushCallJava.cab#version=1,0,1,0\" width=\"100%\" height=\"100%\" border=\"0\"></OBJECT>\r\n"); //
		sb_html.append("  </div>\r\n"); //
		sb_html.append("\r\n");
		return sb_html.toString(); //
	}

	//默认登录与单点登录的第二种访问方式都会用到这段代码,所以包装成一个方法!!
	private String getRunJava2(boolean _isSingle) {
		StringBuilder sb_html = new StringBuilder(); //
		String str_jre = getClientJREVersion(); //默认是1.6.0_18
		//自定义客户端虚拟机最大内存 【杨科/2013-04-23】
		sb_html.append("    var v_1 = 'C:/WebPushJRE/jre" + str_jre + "/bin/java6.exe -client -Xms32m -Xmx" + getTBUtil().getSysOptionIntegerValue("客户端虚拟机最大内存", 1024) + "m ';  \r\n"); //
		sb_html.append("    var v_2 = '-Djava.library.path=C:/WebPushJRE/jre" + str_jre + "/bin;C:/WebPushJRE/jre" + str_jre + "/bin/bin3 -Dfile.encoding=GBK '; \r\n"); //
		sb_html.append("    var v_3 = 'cn.com.weblight.applet.WLTAppletMainFrame " + getRegeditCode() + "://localjava/call?' + " + (_isSingle ? "getClientRequestUrl()" : "v_url + '&'") + " + 'oldversionpar=url;user;pwd;adminpwd'; \r\n"); //
		sb_html.append("    var v_callurl = v_1 + v_2 + v_3;\r\n"); //运行命令,没有就下载文件!!
		sb_html.append("    var v_return = WebPushOCX.CallJava(v_callurl);\r\n"); //运行命令,没有就下载文件!!
		sb_html.append("    if(v_return=='0'){  \r\n"); //如果没有找到文件,则下载插件!!
		sb_html.append("       downClientFile();  \r\n"); //下载客户端文件!!!
		sb_html.append("       return; \r\n");
		sb_html.append("    } \r\n");
		return sb_html.toString(); //
	}

	/**
	 * Web页面登录提交后的生成文件
	 * @return
	 */
	public String getAppletObjectParams(HttpServletRequest _request, HttpServletResponse _response, String _loadClass) {
		StringBuffer sb_html = new StringBuffer(); //
		//先一个不漏的输出所有weblight.xml中定义的init-param
		String transpro = _request.getParameter("transpro");
		if (transpro == null || "".equals(transpro.trim())) {
			transpro = "http";
		}
		System.setProperty("transpro", transpro);
		InitParamVO[] initParmVOs = ServerEnvironment.getInstance().getInitParamVOs(); //
		sb_html.append("\r\n  <!-- 开始输出所有weblight.xml中的init-param -->\r\n"); //
		for (int i = 0; i < initParmVOs.length; i++) { //
			String str_itemValue = initParmVOs[i].getValue(); //
			if (str_itemValue.indexOf(">") >= 0 || str_itemValue.indexOf("<") >= 0) { //如果有左尖括号或右尖括号,则需要替换,后来在中铁建项目中遇到这种情况,即需要跳转到一个url,url中有尖括号！【xch/2012-08-12】
				str_itemValue = getTBUtil().replaceAll(str_itemValue, "<", "&lt;"); //必须替换
				str_itemValue = getTBUtil().replaceAll(str_itemValue, ">", "&gt;"); //
			}
			if (str_itemValue.indexOf("\"") >= 0) { //如果内容本身有双引号,则必须使用单引号包括！
				str_itemValue = "'" + str_itemValue + "'";
			} else { //如果内容本身没有双引号,则使用双引号!
				str_itemValue = "\"" + str_itemValue + "\"";
			}
			sb_html.append("  <PARAM NAME=\"" + initParmVOs[i].getKey() + "\"  " + getSpace(initParmVOs[i].getKey()) + "VALUE=" + str_itemValue + " />\r\n"); //先一个不漏的输出所有weblight.xml定义的init-param
		}
		sb_html.append("  <!-- 输出所有weblight.xml中的init-param结束 -->\r\n\r\n"); //

		sb_html.append("  <PARAM NAME=\"SERVERCLIENTVERSION\"   VALUE=\"" + getClientJREVersion() + "\" />\r\n"); //整个JRE的版本,安与客户端命令中的参数比较,如果不一致,则会下载整个JRE,即有19M左右!!!!
		sb_html.append("  <PARAM NAME=\"APPLETVIEWER_VERSION\"  VALUE=\"" + cn.com.weblight.applet.WLTAppletUtil.APPLETVIEWER_VERSION + "\" />\r\n"); //客户端引擎的版本号,即后来增加了新的机制,不再需要下载整个19M的JRE了,而只是需要下载70多K的引擎即可!!这样的更新会使客户能接受!!!!

		//处理防火墙映射IP地址问题
		String str_clienRequestIPPort = _request.getParameter("clrequestip"); //新客户端版本的参数!!
		String str_ip = convertIP(str_clienRequestIPPort, _request.getServerName()); //要转换!!解决上海农商行的那种网络环境的问题!
		String str_port = convertPort(str_clienRequestIPPort, _request.getServerPort()); //要转换!!
		System.setProperty("CALLURL", transpro + "://" + str_ip + ":" + str_port + _request.getContextPath()); // 服务端有时候也需要用到这个地址
		sb_html.append("  <PARAM NAME=\"SERVER_HOST_NAME\"      VALUE=\"" + str_ip + "\" />\r\n");
		sb_html.append("  <PARAM NAME=\"SERVER_PORT\"           VALUE=\"" + str_port + "\" />\r\n");
		sb_html.append("  <PARAM NAME=\"APP_CONTEXT\"           VALUE=\"" + _request.getContextPath() + "\" />\r\n");
		sb_html.append("  <PARAM NAME=\"URL\"                   VALUE=\"" + transpro + "://" + str_ip + ":" + str_port + _request.getContextPath() + "\" />\r\n");

		//tool.getRealCallPort(li_port, _request.getContextPath()); //取得直接的集群模式下的实际端口!!!后来将这段逻辑改在客户端实现了!!!这样显得更健壮一些!!!但也吃不准是否合理!!!
		sb_html.append("  <PARAM NAME=\"CALLSERVER_HOST_NAME\"  VALUE=\"" + str_ip + "\" />\r\n");
		sb_html.append("  <PARAM NAME=\"CALLSERVER_PORT\"       VALUE=\"" + str_port + "\" />\r\n");
		sb_html.append("  <PARAM NAME=\"CALLURL\"               VALUE=\"" + transpro + "://" + str_ip + ":" + str_port + _request.getContextPath() + "\" /> <!--至关重要的参数,真正的请求都是这个地址-->\r\n");

		if (_loadClass == null) {
			sb_html.append("  <PARAM NAME=\"LoaderAppletClass\"     VALUE=\"cn.com.infostrategy.ui.sysapp.login.LoginAppletLoader\" />\r\n"); //最重要的参数!!!! 客户端引擎会使用自定义classLoader反射加载这个类!!!!!
		} else {
			sb_html.append("  <PARAM NAME=\"LoaderAppletClass\"     VALUE=\"" + _loadClass + "\" />\r\n"); //最重要的参数!!!! 客户端引擎会使用自定义classLoader反射加载这个类!!!!!
		}

		sb_html.append("  <PARAM NAME=\"APP_DEPLOYPATH\"        VALUE=\"" + ServerEnvironment.getProperty("WebAppRealPath") + "\" />\r\n");
		sb_html.append("  <PARAM NAME=\"DEPLOYMODEL\"           VALUE=\"CLUSTER\" />\r\n"); //集群部署
		if (_request.getParameter("admin") != null && _request.getParameter("admin").equalsIgnoreCase("Y")) {
			sb_html.append("  <PARAM NAME=\"LOGINMODEL\"            VALUE=\"ADMIN\" />\r\n"); //登录模式,管理员登录
		} else {
			sb_html.append("  <PARAM NAME=\"LOGINMODEL\"            VALUE=\"NORMAL\" />\r\n"); //登录模式,普通身份登录
		}

		//实际选中的内部系统...
		if (_request.getParameter("isys") != null) { //取实际提交的!!
			sb_html.append("  <PARAM NAME=\"ISYS\"                  VALUE=\"" + _request.getParameter("isys") + "\" />\r\n"); //选中的子系统
		} else {
			if (ServerEnvironment.getInnerSys() != null && ServerEnvironment.getInnerSys().length > 0) { //
				sb_html.append("  <PARAM NAME=\"ISYS\"                  VALUE=\"" + ServerEnvironment.getInnerSys()[0][0] + "\" />\r\n"); //选中的子系统
			} else {
				sb_html.append("  <PARAM NAME=\"ISYS\"                  VALUE=\"\" />\r\n"); //选中的子系统
			}
		}
		if (_request.getParameter("desktopLogin") != null && _request.getParameter("desktopLogin").equalsIgnoreCase("Y")) {
			sb_html.append("  <PARAM NAME=\"STARTMODEL\"            VALUE=\"DESKTOP\" />\r\n"); //桌面启动

		} else {
			sb_html.append("  <PARAM NAME=\"STARTMODEL\"            VALUE=\"BROWSE\" />\r\n"); //浏览器启动
		}

		if (_request.getParameter("usercode") != null) { //如果有用户名或密码!比如快捷登录,或由其他网站直接跳转过来的
			String userCode = _request.getParameter("usercode");
			if (userCode.indexOf("%") > -1) {//表示如果为google则传递过来的为16进制编码所以特殊处理   一般情况下code中是不会有%的     袁江晓20130422添加
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
			sb_html.append("  <PARAM NAME=\"LOGINUSERCODE\"         VALUE=\"" + userCode + "\" />\r\n"); //用户名    //袁江晓 20130422修改 主要解决从IE登陆code为中文而导致登陆报错乱码的问题
			sb_html.append("  <PARAM NAME=\"LOGINUSERPWD\"          VALUE=\"" + _request.getParameter("pwd") + "\" />\r\n"); //用户密码,以后需要加密处理，然后在swing中解密处理再校验
			sb_html.append("  <PARAM NAME=\"LOGINUSERADMINPWD\"     VALUE=\"" + _request.getParameter("adminpwd") + "\" />\r\n"); //用户密码,以后需要加密处理，然后在swing中解密处理再校验
		} else if (_request.getAttribute("singlelogin_usercode") != null) {
			try {
				cn.com.infostrategy.bs.common.CommDMO dmo = new cn.com.infostrategy.bs.common.CommDMO();
				HashVO[] temp = dmo.getHashVoArrayByDS(null, "select * from pub_user where code='" + _request.getAttribute("singlelogin_usercode").toString() + "'");
				if (temp != null && temp.length == 1) {
					sb_html.append("  <PARAM NAME=\"LOGINUSERCODE\"         VALUE=\"" + _request.getAttribute("singlelogin_usercode") + "\" />\r\n"); //用户名
					sb_html.append("  <PARAM NAME=\"LOGINUSERPWD\"          VALUE=\"" + temp[0].getStringValue("pwd") + "\" />\r\n"); //用户密码,以后需要加密处理，然后在swing中解密处理再校验
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (_request.getParameter("LANGUAGE") != null) { //多语言..
			sb_html.append("  <PARAM NAME=\"LANGUAGE\"              VALUE=\"" + _request.getParameter("LANGUAGE") + "\" />\r\n"); //快捷登录
		}
		sb_html.append("  <PARAM NAME=\"FILTERPACKAGE\"         VALUE=\"org.apache.poi.;cn.com.weblight.images;com.sun.;org.jdesktop.;bin.windows.x86;org.jfreechart;\" />\r\n"); //过滤的包,即不从服务器端取,而从本地取
		return sb_html.toString(); //
	}

	/**
	 * 王钢写的在Activex中找一下是否有其他JRE,如果有,则不再下载19M的PushJRE,而直接使用别人的!! 这样的好处是省去有的客户抱怨我们插件太大的问题!! 比如中铁建项目中就一直纠结于插件太大的问题!!
	 * 如果没有找到其他JRE,则没有办法,则直接在ActiveX中下载,而且也有进度条显示!! 下载完后直接运行我们的JRE安装程序!!
	 * @return
	 */
	public String getActiveXShareJREHtml(HttpServletRequest _request) {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<HTML >\r\n");
		sb_html.append("<HEAD>\r\n");
		sb_html.append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=GBK\">\r\n"); //

		//输出一些JS
		sb_html.append("<script language=\"JavaScript\">  \r\n");
		sb_html.append(getJSFunctions(_request, 3)); //先输出一堆JS函数!!

		sb_html.append("<!-- 寻找JRE,如果有直接共用之,如果没有,则下载普信JRE  --> \r\n"); //寻找JRE
		sb_html.append("function findShareJRE() {  //寻找JRE \r\n"); //寻找JRE
		sb_html.append("  var ret = WebPushActiveX.HasPushJRE();  //看是否有PushJRE,即看注册表中是否有WebPush20100601 \r\n"); //
		sb_html.append("  if (ret==\"1\") { //如果有PushJRE \r\n"); //如果有PushJRE
		sb_html.append("    setCookie('WebPushJREInstalled','Y');  //直接写Cookie\r\n");
		sb_html.append("    alert('系统发现已安装了插件,已重新进行了Cookie登记,请重新访问即可!');  \r\n");
		sb_html.append("    closeThisWindow();  //关闭窗口  \r\n");
		sb_html.append("  } else { //如果没有WebPushJRE \r\n"); //
		sb_html.append("    var v_javahome = WebPushActiveX.GetJavaHome(); \r\n"); //
		sb_html.append("    if (v_javahome==\"\") { \r\n");
		sb_html.append("       MSGLabel.value='发现没有安装JRE,正在下载...';  \r\n");
		sb_html.append("    } else {  \r\n"); //使用王钢里面的下载!!!
		sb_html.append("       MSGLabel.value='发现已安装JRE【' + v_javahome + '】,正在进行绑定...'; //如果找到了JRE,则只下载appletViewer.jar \r\n");
		sb_html.append("    } \r\n"); //
		sb_html.append("    var v_url = String(window.location); //必须用客户端的url,如果从服务器端则可以会因为上海农商行那种网络环境导致取的不对! \r\n"); //
		sb_html.append("    var v_pos = v_url.indexOf('/login?'); \r\n"); //
		sb_html.append("    var v_url_prefix = v_url.substring(0,v_pos);  //截取前面的部分! \r\n"); //
		sb_html.append("    var urlPushJRE = v_url_prefix + '/help/webpushjre.exe'; //插件的名称! \r\n"); //
		sb_html.append("    var urlPushJAR = v_url_prefix + '/DownLoadFileServlet?filename=/WEB-INF/lib/wltappletviewer.jar'; //这个要下载到JRE的ext目录下 \r\n"); //
		sb_html.append("    WebPushActiveX.InstallPushJRE(urlPushJRE, urlPushJAR); //下载JRE,在Activex里面决定是下载整个JRE还是就AppletViewer.jar  \r\n"); //
		sb_html.append("    if (v_javahome==\"\") {  //如果没找到其他JRE,则下载普信JRE,下载结束后自动弹出自解压窗口,这里可直接关闭窗口 \r\n"); //
		//sb_html.append("      setCookie('WebPushJREInstalled','Y');  //写Cookie \r\n");   //暂时不写Cookie,因为在弹出自解压窗口后,可能有人会点【拒绝】按钮,而这时因为是异步了,取不到这个状态,所以这时写Cookie,则可能就不准!! 所以不写!!
		sb_html.append("      closeThisWindow();  // \r\n"); //
		sb_html.append("    } else { //如果有其他JRE,则绑定结束后来个提示,然后直接关闭窗口 \r\n"); //
		sb_html.append("      setCookie('WebPushJREInstalled','Y');  //直接写Cookie,省得再次访问时再点下不要再提醒的按钮! \r\n");
		sb_html.append("      alert('发现已安装JRE【' + v_javahome + '】,已成功绑定之,请重新访问!');  \r\n");
		sb_html.append("      closeThisWindow(); \r\n"); //
		sb_html.append("    }\r\n");
		sb_html.append("  }\r\n");
		sb_html.append("}\r\n");

		sb_html.append("</script>  \r\n"); //
		sb_html.append("<TITLE>WebPush IE</TITLE>\r\n");
		sb_html.append("</HEAD>\r\n\r\n");
		sb_html.append("<BODY onLoad=\"findShareJRE();\">\r\n"); //寻找JRE
		sb_html.append("<input type=\"text\" name=\"MSGLabel\" value=\"系统正在处理......\" size=\"100\" style=\"border:none;color:#0000FF;font-size: 12px;\"><br>\r\n"); //用来显示说明的!!!
		sb_html.append("<OBJECT name=\"WebPushActiveX\" classid=\"clsid:53F7F8C2-2853-404B-B241-68F0E3D0E533\" codebase=\"./applet/CallWebPush.cab#version=4,0,0,0\" width=\"600\" height=\"26\"></OBJECT>\r\n"); //
		sb_html.append("</BODY>\r\n");
		sb_html.append("</HTML>\r\n");
		return sb_html.toString(); //
	}

	//转换IP
	private String convertIP(String _clientRequestIPPort, String _serverRequestIP) {
		String str_forceIPPort = ServerEnvironment.getProperty("ForceIPPort_ForClientCall"); //
		if (str_forceIPPort != null) {
			return str_forceIPPort.substring(0, str_forceIPPort.indexOf(":")); //如果强行定义了,则直接返回!
		}
		if (_clientRequestIPPort != null) { //如果有,则直接返回它! 因为像在上海农商行的网络环境中会发生在服务器端得到的地址与端口与客户端实际请求的根本不一样! 结果造成CallURL不对,从而无法访问!!
			return _clientRequestIPPort.substring(0, _clientRequestIPPort.indexOf(":")); //如果强行定义了,则直接返回!
		}
		return _serverRequestIP; //返回服务器端的,即原来的默认方法!!
	}

	//转换端口
	private String convertPort(String _clientRequestIPPort, int _serverRequestPort) {
		String str_forceIPPort = ServerEnvironment.getProperty("ForceIPPort_ForClientCall"); //
		if (str_forceIPPort != null) {
			return str_forceIPPort.substring(str_forceIPPort.indexOf(":") + 1, str_forceIPPort.length()); //如果强行定义了,则直接返回!
		}
		if (_clientRequestIPPort != null) { //如果有,则直接返回它! 因为像在上海农商行的网络环境中会发生在服务器端得到的地址与端口与客户端实际请求的根本不一样! 结果造成CallURL不对,从而无法访问!!
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
