package cn.com.infostrategy.bs.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.infostrategy.to.common.TBUtil;

public class DownLoadFileServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private int threadLimit = -1; //线程上限,即限制最多可以有多少个用户同时下载，如果超过这个数则提示必须等待。
	private int li_OneUserSpeed = -1; //1500; //限制每个用户下载的最大速度,即KB/秒
	public static int threadCount = 0; //线程累加计数器变量,因为是所有线程都要使用这个,所以必须是全局静态变量
	private static Hashtable hm_FileCache = new Hashtable(); //(HashMap) Collections.synchronizedMap(new HashMap()); //缓存文件的哈希表,就是有些文件只取第一次,然后就放在缓存中了,而不是每次都取,比如客户端插件,系统上线时并发量非常大!!

	@Override
	protected void service(HttpServletRequest _request, HttpServletResponse _response) throws ServletException, IOException {
		//_request.setCharacterEncoding("GBK"); //
		FileInputStream fileIn = null; //
		ServletOutputStream resOutStream = null; //
		try {
			String str_pathtype = _request.getParameter("pathtype"); //upload
			String str_iscachefile = _request.getParameter("iscachefile"); ////upload
			String str_fileName = _request.getParameter("filename"); //
			if (str_fileName == null || str_fileName.trim().equals("")) {
				StringBuilder sb_help = new StringBuilder(); //
				sb_help.append("必须指定filename参数,格式是【/abc.doc】,或【/dir1/dir2/abc.doc】的样子,即文件名本身是可以带子目录的,它将拼接在参数pathtyp后面形成一个完整的文件全路径!<br>\r\n"); //
				sb_help.append("参数pathtype是可选的,如果为空,则默认根目录是【%WebRoot%】目录,比如【D:\\TomCat5.5.23\\webapps\\ipushgrc】<br>\r\n");
				sb_help.append("如果pathtyp=upload,则根目录是【%WLTUPLOADFILEDIR%\\upload】,其中变量%WLTUPLOADFILEDIR%是weblight.xml配置的参数,具体值比如是【C:\\WebPushTemp】!<br>\r\n"); //
				sb_help.append("如果pathtyp=officecompfile,则根目录是【%WLTUPLOADFILEDIR%\\officecompfile】<br>\r\n"); //
				sb_help.append("如果pathtyp=serverdir,则根目录直接是【%WLTUPLOADFILEDIR%\\】,即直接从参数指定的根目录中下载!<br>\r\n"); //
				sb_help.append("参数iscachefile=Y,表示一旦下载后就自动缓存起来,比如webpushjre.exe就有这种需求,默认为否!<br><br>\r\n"); //
				sb_help.append("具体的例子如下:<br>\r\n"); //
				sb_help.append("/DownLoadFileServlet?filename=/abc.doc<br>\r\n"); //
				sb_help.append("/DownLoadFileServlet?filename=/abc.doc&pathtype=upload<br>\r\n"); //
				sb_help.append("/DownLoadFileServlet?filename=/abc.doc&pathtype=office&iscachefile=Y<br>\r\n"); //
				sb_help.append("/DownLoadFileServlet?filename=/dir1/abc.doc&pathtype=serverdir&iscachefile=Y<br>\r\n"); //
				writeErrorMsg(_response, sb_help.toString()); //
				return; //直接退出
			}
		
			
			str_fileName = URLDecoder.decode(str_fileName, "GBK");//最好加上这句，否则容易乱码,就算是new String(str_fileName.getBytes("ISO-8859-1"), "GBK")这种方式还是会乱码【吴鹏/2012-6-19】
			str_fileName = new String(str_fileName.getBytes("ISO-8859-1"), "GBK"); //转换成中文!
			//by haoming 20160519 需要把路径转一下再判断一次。
			if(str_fileName!=null && str_fileName.contains("..")){//太平黑盒扫描发现此漏洞，DownLoadFileServlet?filename=/../../../../../../../../../../../../etc/passwd%00.html&iscachefile=Y  
				writeErrorMsg(_response,"传入路径有非法参数"); //
				return;
			}
			if (!str_fileName.startsWith("/")) { //如果不是/开头,则自动加上/
				str_fileName = "/" + str_fileName; //
			}
			//System.out.println("DownLoadFileServlet:需要下载的文件为：" + str_fileName);
			String str_realpath = "";
			if (str_pathtype == null) { //路径类型
				str_realpath = ServerEnvironment.getProperty("SERVERREALPATH"); //
			} else if (str_pathtype.equalsIgnoreCase("upload")) {
				str_realpath = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/upload//"; //
			} else if (str_pathtype.equalsIgnoreCase("office")) {
				str_realpath = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/officecompfile//"; //
			} else if (str_pathtype.equalsIgnoreCase("serverdir")) {
				str_realpath = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "//"; //如果是上传附件根目录下的某个直接目录!!
			} else if (str_pathtype.equalsIgnoreCase("realdir")) { //如果是服务器的绝对路径 Gwang 2012-09-17修改
				str_realpath = "";
			}

			//路径处理,好麻烦,以前遇到过WebSphere路径少个东西
			if (!str_realpath.endsWith("/")) { //如果不是/结尾
				str_realpath = str_realpath + "/"; //加一个路径,因为在websphere中的部署路径是一个【....installedApps/PC-200910091202Node01Cell/cmb1_war.ear/cmb1.war】的样子!即不是/目录结尾的!!!
			}

			String str_realfilename = str_fileName.substring(str_fileName.lastIndexOf("/") + 1, str_fileName.length()); //实际文件名,即去掉日期目录后的文件名!
			String str_downloadFileName = str_realpath.substring(0, str_realpath.length() - 1) + str_fileName; ////
			//System.out.println("DownLoadFileServlet：需要下载的文件的全路径：" + str_downloadFileName);
			if (threadLimit > 0) { //如果指定了上限
				DownLoadFileServlet.threadCount++; //在全局变量计数器中加1
				if (DownLoadFileServlet.threadCount > threadLimit) { //先判断是否超过上限
					writeErrorMsg(_response, "已有[" + threadLimit + "]人同时在下载,请稍候再下载...."); //
					return; //直接退出
				}
			}

			//先读取文件,放入字节数组byte[]变量中,实际运行时附件内容是做缓存处理的.
			byte[] bytes = null;
			if (str_iscachefile != null && str_iscachefile.equalsIgnoreCase("Y") && hm_FileCache.containsKey(str_fileName)) { //如果指定了缓存处理,且已做过缓存
				bytes = (byte[]) hm_FileCache.get(str_fileName); //
				System.out.println("DownLoadFileServlet下载文件【" + str_fileName + "】(从缓存取)"); //
			} else { //否则是从硬盘读文件
				String str_newWebUrl = ServerEnvironment.getProperty("FullTextSearchURL"); //邮储中集群时不能使用nfs与ftp,所以只能http从另一台机器上存取...
				if (str_newWebUrl != null) { //如果定义了是从远程中取
					//System.out.println("因为定义了FullTextSearchURL=[" + str_newWebUrl + "],所以要转向远程取文件[" + str_fileName + "]..."); //
					HashMap requestMap = new HashMap();
					requestMap.put("Action", "download"); //
					requestMap.put("FileName", str_downloadFileName); //必须是路径全称

					HashMap rtMap = new BSUtil().callWebUrl(str_newWebUrl + "/UploadFileServlet", requestMap); //

					if ("Y".equals(rtMap.get("isException"))) { //如果发生异常
						Exception ex = (Exception) rtMap.get("Exception"); //
						throw new Exception("远程请求文件发生异常", ex); //
					}
					bytes = (byte[]) rtMap.get("ByteCodes"); //返回的字节码!!
					if (str_iscachefile != null && str_iscachefile.equalsIgnoreCase("Y")) { //如果是指定了缓存,则立即送入缓存
						hm_FileCache.put(str_fileName, bytes); //
					}
				} else {
					File file = new File(str_downloadFileName); //创建文件对象
					if (file.exists()) { //如果文件在目录下!
						fileIn = new FileInputStream(file); //创建文件读取流对象
						ByteArrayOutputStream bout = new ByteArrayOutputStream(); //
						byte[] tmpBys = new byte[5012]; //原来是2048,现在搞大点!! 经过测试,更快些,但会增大内存开销!
						int li_pos = -1; //
						while ((li_pos = fileIn.read(tmpBys)) != -1) { //循环读完文件,一次读2个字节,即流式读取!!!更流畅些!!以前是一下子读到一个大数组中,可能性能不稳定,但没有证据!! 只是JDK的标准方法都建议这种读法!!!
							bout.write(tmpBys, 0, li_pos); //读入,关键!!!必须指定位置!!
						}
						bytes = bout.toByteArray(); //得到字节!!!
						try {
							bout.close(); //关闭输出流!
						} catch (Exception e) {
							e.printStackTrace(); //
						}
						try {
							fileIn.close(); //立即关闭与释放
							fileIn = null; //
						} catch (Exception e) {
							e.printStackTrace(); //
						}
						System.out.println("DownLoadFileServlet下载文件【" + str_downloadFileName + "】(从硬盘读)"); //
						if (str_iscachefile != null && str_iscachefile.equalsIgnoreCase("Y")) { //如果是指定了缓存,则立即送入缓存
							hm_FileCache.put(str_fileName, bytes); //
						}
					} else { //则继续从数据库中取!!!这样会兼容新的功能!!
						System.out.println("在硬盘中没发现文件[" + str_downloadFileName + "],转向从数据库读,是否置入缓存=[" + str_iscachefile + "]"); //
						bytes = new BSUtil().getFileContentFromDB(str_fileName, "在硬盘中没发现文件[" + new TBUtil().replaceHtmlEncode(str_downloadFileName) + "],转向从数据库读..."); //by haoming 20160519输出前台有js漏洞
						if (str_iscachefile != null && str_iscachefile.equalsIgnoreCase("Y")) { //如果是指定了缓存,则立即送入缓存
							hm_FileCache.put(str_fileName, bytes); //塞入缓存!!!
						}
					}
				}
			}

			if (bytes == null) {
				writeErrorMsg(_response, "文件【" + str_downloadFileName + "】取出的内容为空!!"); ////
				return;
			}

			//向浏览器客户端以流的方式输出文件流
			_response.setBufferSize(51200); //缓冲区设为50K,以前是chunked模式,肯定不好!
			_response.setContentLength(bytes.length); //一定要设下,否则可能变成[Transfer-Encoding]=[chunked],然后性能下降,特别是有的Win7环境下...
			setMimeType(_response, str_realfilename, bytes.length); //设置Mime类型,最关键!!!!!!!!!!
			resOutStream = _response.getOutputStream(); //得到输出流,即向浏览器端输出数据和流对象
			int li_start = 0; //定义坐标临时局部变量
			int li_onecyclesize = 5012; //每次循环输出多大!!一般是2048!! 经过反复测试,与通过浏览器直接下载比较,如果设成2048是没有浏览器快的,而设成5012,则比浏览器快! 这说明两个问题：一个是设大点还是有效果的；二是我们的代码是与IE的性能不相上下的!即是没有硬伤的!
			while (1 == 1) { //做死循环,向客户端输出数据,直接数据结束，其中通过每输出1K数据就休息一段时间的方式来控制流量
				int li_end = li_start + li_onecyclesize; //计算结尾坐标
				if (li_end >= bytes.length - 1) { //结果结尾坐标位置已超过文件长度
					resOutStream.write(bytes, li_start, bytes.length - li_start); //
					//System.out.println("输出最后剩余的" + (bytes.length - li_start) + "字节的数据..");
					break; //中断循环
				} else {
					resOutStream.write(bytes, li_start, li_onecyclesize); //每次循环只输出1024字节的数量,即1K的数据
					li_start = li_start + li_onecyclesize; //位置坐标加1024
					//System.out.println("第[" + (li_start / 1024) + "]次输出1024字节的数据..");
				}
			}
		} catch (Exception _ex) {
			_ex.printStackTrace(); //
			_response.setContentType("text/html"); //
			_response.setCharacterEncoding("GBK"); //
			_response.getWriter().println(new TBUtil().getExceptionStringBuffer(_ex, true, true)); //
		} finally {
			if (threadLimit > 0) {
				DownLoadFileServlet.threadCount--; //下载完后在全局变量中减1,好让其他人下以下载...
			}
			try {
				if (fileIn != null) {
					fileIn.close(); //
				}
			} catch (Exception e) {
			}
			try {
				if (resOutStream != null) {
					resOutStream.flush(); //
					resOutStream.close(); //
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 设置Mime类型.
	 * @param _response
	 * @param _realfilename
	 * @param _length
	 */
	private void setMimeType(HttpServletResponse _response, String _realfilename, int _length) throws Exception {
		if (_realfilename.lastIndexOf(".") < 0) { //如果没有后辍
			_response.setContentType("application/octet-stream"); //二进制数据流
			_response.setHeader("Content-Disposition", "attachment; filename=" + convertFileName(_realfilename));
		} else {
			String str_filetype = _realfilename.substring(_realfilename.lastIndexOf(".") + 1, _realfilename.length()); //
			String[] str_headerType = new BSUtil().getMimeTypeByDocType(str_filetype, convertFileName(_realfilename)); //
			_response.setContentType(str_headerType[0]); //设置输出类型
			_response.setHeader("Content-Disposition", str_headerType[1]); //设置输出描述!!!
		}
		_response.setContentLength(_length); //设置输出长度
	}

	//如果文件名是16进制的所谓乱码,则需要转换成看得懂的中文!!
	private String convertFileName(String _realfilename) throws UnsupportedEncodingException {
		TBUtil tbUtil = new TBUtil(); //
		int li_pos = _realfilename.indexOf("_"); //看是否有下划线
		if (li_pos < 0) {
			li_pos = -1;
		}
		int li_pos2 = _realfilename.lastIndexOf("."); //
		String str_masterName = _realfilename.substring(li_pos + 1, li_pos2 >= 0 ? li_pos2 : _realfilename.length()); //主名
		if (tbUtil.isHexStr(str_masterName)) { //如果主名是16进制码主名
			str_masterName = tbUtil.convertHexStringToStr(str_masterName); //转换!
			if (li_pos2 >= 0) {
				_realfilename = str_masterName + "." + _realfilename.substring(li_pos2 + 1, _realfilename.length()); //全称!!
			} else {
				_realfilename = str_masterName;
			}
		} else { //如果不是16进制,但仍然有特殊码,则系统定义一个类
			String str_className = tbUtil.getSysOptionStringValue("项目自定义附件文件乱码转换器", ""); //"cn.com.infostrategy.bs.common.ConvertRefFileNameUtil"; //如果定义了转换器类!
			if (str_className != null && !str_className.trim().equals("")) { //如果有类!!
				String str_newName = (String) new TBUtil().refectCallClassStaticMethod(str_className, "convertName", new String[] { _realfilename }); //
				if (str_newName != null) {
					_realfilename = str_newName; //转换一下!
				}
			} else {
				if (li_pos2 >= 0) {
					_realfilename = str_masterName + "." + _realfilename.substring(li_pos2 + 1, _realfilename.length()); //全称!!
				} else {
					_realfilename = str_masterName;
				}
			}
		}
		//System.out.println("向客户端输出的真实文件名[" + _realfilename + "]"); //
		_realfilename = new String(_realfilename.getBytes(), "ISO-8859-1"); //必须这样转一下,否则不能显示中文!而且在IE9中还会出现下载不了的错误!!
		return _realfilename; //
	}

	//输出错误信息..
	private void writeErrorMsg(HttpServletResponse _response, String _message) throws Exception {
		_response.setContentType("text/html"); //
		_response.setCharacterEncoding("GBK"); //
		_response.getWriter().println("<html><body><font size=2 color=\"red\">" + new TBUtil().replaceHtmlEncode(_message) + "</font></body></html>"); //如果超过上限人数，则提示稍候下载.
	}
}
