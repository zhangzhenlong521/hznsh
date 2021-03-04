/**************************************************************************
 * $RCSfile: BSUtil.java,v $  $Revision: 1.48 $  $Date: 2012/11/14 10:44:14 $
 **************************************************************************/

package cn.com.infostrategy.bs.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.DriverManager;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.zip.DataFormatException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import cn.com.infostrategy.to.common.ClassFileVO;
import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.InitParamVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;

/**
 * 服务器端工具类,最常用的方法都在这里!!! 以前的许多方法都移到CommDMO与MetaDataDMO中去了!!
 * 
 * @author user
 * 
 */
public class BSUtil {

	Logger logger = WLTLogger.getLogger(BSUtil.class); //

	public BSUtil() {
	}

	/**
	 * 根据指定的数据库连接来初始化数据库连接
	 * 
	 * @return
	 * @throws Exception
	 */
	public java.sql.Connection getJDBCConnByDS(String _datasourcename) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName();
		}
		DataSourceVO dsVO = ServerEnvironment.getInstance().getDataSourceVO(_datasourcename); //
		if (dsVO.getProvider_url() != null && dsVO.getInitial_context_factory() != null) {
			WLTDBConnection wltc = new WLTDBConnection(_datasourcename);
			return wltc.getConn();
		} else {
			return DriverManager.getConnection("jdbc:apache:commons:dbcp:" + _datasourcename); //
		}
	}

	/**
	 * 完整上传文件的方法,即以前上传有好几个方法,以后统一都转调到这里来!
	 * 上传文件可以存储到数据库中,也可以Http转存到另一台机器上,也可Ftp转存到另一台机器上...
	 * 上传文件就一个地方,也就是这里!!! 但下载有两个地方:一个是本类中的downloadfile方法,另一个是DownLoadFileServlet,即本类中的方法是Swing控件中的直接下载,而浏览器中还可能直接从一个Servlet下载
	 * @param _fileVO
	 * @param _isChangeFileName
	 * @return
	 * @throws Exception
	 */
	public String uploadFile(cn.com.infostrategy.to.common.ClassFileVO _fileVO, String _serverdir, String _serverFileNewName, boolean _isAbsoluteSeverDir, boolean _isConvertHex, boolean _isAddSerialNo) throws Exception {
		//先处理目录
		//System.out.println("服务器端目录[" + _serverdir + "],服务器端文件名[" + _serverFileNewName + "]"); //
		String str_beginDir = ServerEnvironment.getProperty("WLTUPLOADFILEDIR"); //
		if (_serverdir == null) { //
			str_beginDir = str_beginDir + "/" + WLTConstants.UPLOAD_DIRECTORY; //默认是上传到参数指定的目录!
		} else {
			if (_serverdir.startsWith("/")) {
				str_beginDir = str_beginDir + _serverdir; //
			} else {
				str_beginDir = str_beginDir + "/" + _serverdir; //
			}
		}

		//处理文件名!新的文件是转16进制的,且前面有"N序号_"作前辍!!!
		String str_oldfileName = _fileVO.getClassFileName(); //旧的文件名!!
		TBUtil tbUtil = new TBUtil(); //
		String[] str_excel = new String[] { "]", " ", "+", "[", "{", "}", "——", "<", ">" }; //如果有这些特殊符号,则进行转换!!
		for (int i = 0; i < str_excel.length; i++) {
			if (str_oldfileName.indexOf(str_excel[i]) >= 0) {
				str_oldfileName = new TBUtil().replaceAll(str_oldfileName, str_excel[i], "_"); //将文件名是所有怪符号统统转成下划线!!!!
			}
		}
		//System.out.println("旧的文件名[" + str_oldfileName + "]"); //
		String str_fileDate_init = ""; //
		String str_fileName_init = ""; //
		if (str_oldfileName.indexOf("/") >= 0) {
			str_fileDate_init = str_oldfileName.substring(0, str_oldfileName.lastIndexOf("/")); //文件的日期!!即前面的目录!!!
			str_fileName_init = str_oldfileName.substring(str_oldfileName.lastIndexOf("/") + 1, str_oldfileName.length()); //文件的名称!
		} else {
			str_fileName_init = str_oldfileName; //
		}
		CommDMO commDMO = new CommDMO();
		String str_convertfileName = ""; //转换文件名,即在前面加一个N,即为了防止在unix环境下乱码等,将文件名转换成了16进制码!!并在前面加一唯一性编号!
		String str_clientRealName = null; //反向解析的客户端真实名称!这是为了插入数据库时使用的!!
		if (str_fileName_init.lastIndexOf(".") > 0) {
			String str_clientRealName_1 = str_fileName_init.substring(0, str_fileName_init.lastIndexOf(".")); //
			String str_clientRealName_2 = str_fileName_init.substring(str_fileName_init.lastIndexOf(".") + 1, str_fileName_init.length()); //
			str_clientRealName = tbUtil.convertHexStringToStr(str_clientRealName_1) + "." + str_clientRealName_2; //
		} else {
			str_clientRealName = tbUtil.convertHexStringToStr(str_fileName_init); //
		}

		String str_fileid = ""; //
		if (_isAddSerialNo) { //如果需要增加序号!
			str_fileid = new CommDMO().getSequenceNextValByDS(null, "S_PUB_FILEUPLOAD") + "_"; //取文件的唯一性编号!
		}
		str_convertfileName = (_isConvertHex ? "N" : "") + str_fileid; //如果是转换过的,则使用新的名称
		if (_serverFileNewName == null) { //如果没有指定新的服务器名称,则使用ClassFileVO中指定的文件名
			str_convertfileName = str_convertfileName + str_fileName_init; //
		} else { //如果指定了服务器端新的文件名,则使用新的名称
			str_convertfileName = str_convertfileName + _serverFileNewName; //
		}

		String str_returnFileName = str_fileDate_init + "/" + str_convertfileName; //返回的文件名,即拿原来的日期目录加上转换后的文件名!
		//System.out.println("实际存储的文件名是[" + str_returnFileName + "]"); //
		if (tbUtil.getSysOptionBooleanValue("上传附件是否存数据库", false)) { //如果系统定义了参数,指定将附件写到数据库中,则存储进数据库! 因为招行后来提出必须将附件写到数据库中,另外如果不写到库中,在集群模式下,查文件需要寻找主服务器(一不方便,二来主服务器死了就不行了)! 但写到库中以后,就不能进行基于文件内容搜索了!! 即各有优缺点!!!
			byte[] fileBytes = _fileVO.getByteCodes(); //文件内容的字节码!!
			//要压缩一把! 但压缩必须有个保险机制!! 即如果是rar等文件,本身没有压缩空间了,则没必要进行压缩了! 则直接输出!! 否则会造成上传rar文件其慢无比! 以前在项目中遇到一个很小的rar文件死活上传或下载不了的情况,就是压缩的计算循环无穷大!!
			CompressBytesVO zipedVO = compressBytes(fileBytes); //压缩对象!!
			byte[] zipedBytes = zipedVO.getBytes(); //压缩对象的字节!!

			ArrayList al_sqls = new ArrayList(); //存储SQL的列表对象!
			String str_currtime = tbUtil.getCurrTime(); //取得当前时间,精确到秒!
			String str_64Code = tbUtil.convertBytesTo64Code(zipedBytes); //将字节转成64位码!!没有用16进制码,是为了更节约空间,很关键! 因为像Word/pdf/excel文件根本看不懂内容,所以统一成64位码!! 而且64位码全是英文,最好的是没有怪字符! 尤其是没有单引号,否则会造成SQL报错!! 这说明设计64码的老大早考虑到塞数据库的问题的! 将单引号排除在外! he 
			ArrayList al_split = splitStrBySpecialWidth(str_64Code, 10, 4000); //将64位码打断成,每一段是4000位长的数组,然后每25段为一行,即用来插入数据库中! 即在数据库表pub_upfiles_b中,是25列,每列宽度4000,一行能存10万字节,一个1M的文件只要15行左右就够了! 从而保证了行数较少!
			//使用更多列,保证行数少的思想非常关键,因为关系数据库是基于行存储的,索引查询也是基于行的,行越多,查询越慢!! 如果只有一列,则用不了多久,该表就有上百万甚至上千万行,查询会非常慢!!! 横向存储时就一下子性能提高很多!!! 取数时再将各列的数据接起来!
			//但也不是列越多越好,因为大部分文件是在300K左右(压缩后),如果列太多,则没有用到,则是浪费,所以要找到一个平衡点!!

			//先处理主表!!!
			InsertSQLBuilder isql_insert = new InsertSQLBuilder("pub_upfiles"); // 
			String str_parentPK = commDMO.getSequenceNextValByDS(null, "S_PUB_UPFILES"); //主表主键!
			isql_insert.putFieldValue("id", str_parentPK); //主表主键!
			isql_insert.putFieldValue("filename", str_returnFileName); //应该是唯一性的!!!
			isql_insert.putFieldValue("fileinitname", str_clientRealName); //原来的文件名,即不转换的!
			isql_insert.putFieldValue("isziped", zipedVO.isZip() ? "Y" : "N"); //是否真的压缩的!
			isql_insert.putFieldValue("unzipedbytesize", fileBytes.length); //压缩前的字节大小
			isql_insert.putFieldValue("zipedbytesize", zipedBytes.length); //压缩后的字节大小
			isql_insert.putFieldValue("zipedscale", ((zipedBytes.length * 100) / fileBytes.length)); //压缩比,即: 压缩后/压缩前%,我直接乘上100,即不用%号了!
			isql_insert.putFieldValue("filecodesize", str_64Code.length()); //文件转成64码后的大小!
			isql_insert.putFieldValue("createtime", str_currtime); //创建时间!!即上传时间!!
			al_sqls.add(isql_insert.getSQL()); //

			//再处理子表!!
			for (int i = 0; i < al_split.size(); i++) { //总共有几行!
				String str_pk_b = commDMO.getSequenceNextValByDS(null, "S_PUB_UPFILES_B"); //子表的这行记录的主键!!
				String[] str_rowData = (String[]) al_split.get(i); //这一行记录!!!
				for (int j = 0; j < str_rowData.length; j++) { //遍历各列! 为了防止SQL过长而提交失败!(有的数据库会接收不了太长的SQL),第一列是insert操作,后面的列是update操作!!!
					if (j == 0) { //如果是第一列,则做insert操作!!!
						InsertSQLBuilder isql_insert_b = new InsertSQLBuilder("pub_upfiles_b"); //子表主键!!
						isql_insert_b.putFieldValue("id", str_pk_b); //主键!!
						isql_insert_b.putFieldValue("fileid", str_parentPK); //外键,关联主表的!
						isql_insert_b.putFieldValue("seq", (i + 1)); //序号,排序用!千万不能少!查询是必须使用 order by seq,否则行错乱了,肯定生成的文件不对! 到时也找不开!
						isql_insert_b.putFieldValue("createtime", str_currtime); //创建时间,因为需要根据该时间字段进行数据迁移! 所以干脆也在子表搞个
						isql_insert_b.putFieldValue("c1", str_rowData[j]); //第一列!!
						al_sqls.add(isql_insert_b.getSQL(false)); //加入SQL
					} else { //后面的列统统做update操作,这个设计思想非常关键!!! 即将一行数据分成25条SQL执行! 第一列是insert,后面的都是update,从而防止一个10万个字符串的SQL会报错! 因为Oracle就不支持超长SQL执行! Mysql倒是可以的,也真是奇怪!! 看来Mysql也是很方便的地方!
						UpdateSQLBuilder isql_update_b = new UpdateSQLBuilder("pub_upfiles_b", "id='" + str_pk_b + "'"); //做修改操作!!!
						isql_update_b.putFieldValue("c" + (j + 1), str_rowData[j]); //
						al_sqls.add(isql_update_b.getSQL(false)); //加入SQL
					}
				}
			}
			commDMO.executeBatchByDS(null, al_sqls, false); ////最后插入数据库!!!! 因为SQL太多,不输出控制台!! 所以第二个参数是false,一定要注意这一点,千万不要以前控制台不输出sql就以前没执行! 要到表中实际查下,这是容易犯错的地方!!
			WLTLogger.getLogger(this).debug("上传文件[" + str_returnFileName + "][" + str_clientRealName + "]至数据库表pub_upfiles中成功!"); //
		} else if (ServerEnvironment.getProperty("FullTextSearchURL") != null) { //如果指定了http重定向存储url,则转调!
			String str_newWebUrl = ServerEnvironment.getProperty("FullTextSearchURL");
			System.out.println("BSUtil向远程全文检索的地址[" + str_newWebUrl + "]上传文件[" + str_beginDir + "/" + str_returnFileName + "]...");
			HashMap requestMap = new HashMap();
			requestMap.put("Action", "upload");
			requestMap.put("FileName", str_beginDir + "/" + str_returnFileName); //目录加文件名....
			requestMap.put("ByteCodes", _fileVO.getByteCodes());
			HashMap responseMap = new BSUtil().callWebUrl(str_newWebUrl + "/UploadFileServlet", requestMap); //
			Object obj_return = responseMap.get("ReturnResult"); //
			if (obj_return instanceof Exception) {
				throw (Exception) obj_return;
			}
		} else if (ServerEnvironment.getProperty("FtpFileServerURL") != null && !ServerEnvironment.getProperty("FtpFileServerURL").equals("")) { //如果定义了Ftp参数
			String str_fileName = "/" + str_fileDate_init + "/" + str_convertfileName; //FTP只能存储相对路径名!!!
			new FtpDealUtil().upFile(ServerEnvironment.getProperty("FtpFileServerURL"), _fileVO.getByteCodes(), str_fileName); //
			WLTLogger.getLogger(this).debug("上传文件[" + _fileVO.getClassFileName() + "]至FTP服务器[" + ServerEnvironment.getProperty("FtpFileServerURL") + "]成功,返回文件名[" + str_returnFileName + "]"); //
		} else { //如果没定义参数,则还是使用以前的机制,直接往Web服务器的目录下写文件,则输出至文件即可! 如果写到目录中,则可以使用lucense等搜索工具进行基于文件的搜索!
			//以前是所有文件都放在一个目录下,不分二级子目录,结果在民生银行中,后来遇到一年后,文件实在太多,结果导致操作系统打开这个文件都需要很长时间,所以后来做成了每天一个目录!! 然后每天的文件就存在每天的目录下! 即有个二级子目录! 这样迁移与备份也方便!
			String str_dir = str_beginDir + "/" + str_fileDate_init; //..
			File filedir = new File(str_dir); //上传文件的目录..
			if (!filedir.exists()) { //如果目录不存在,则创建目录! 每天的文件在第一个文件时才会调用此!
				filedir.mkdirs(); // 创建目录
			}
			//以前没有考虑有文件夹的情况，现已解决【李春娟/2016-09-08】
			if (str_convertfileName.contains(File.separator)) {
				//zipEntry.getName() 路径如 icheck\20160907\N3142_BBFDB7D6D6B8B1EABFE2.xls
				String currpath = str_dir + File.separator + str_convertfileName.substring(0, str_convertfileName.lastIndexOf(File.separator));
				File df = new File(currpath);
				if (!df.exists()) {
					df.mkdirs();
				}
			}
			String str_newFileNameByDir = str_dir + "/" + str_convertfileName; //完整的文件绝对路径!
			File file = new File(str_newFileNameByDir); //
			file.createNewFile(); //创建新文件
			FileOutputStream out = new FileOutputStream(file, false); //写文件流,是覆盖模式!! 
			out.write(_fileVO.getByteCodes()); //写文件!!
			out.close(); //关闭流
			WLTLogger.getLogger(this).debug("上传文件[" + _fileVO.getClassFileName() + "]至目录[" + str_newFileNameByDir + "]成功,返回文件名[" + str_returnFileName + "]"); //
		}
		return str_returnFileName;//
	}

	/**
	 * 从服务器端下载文件
	 * 上传文件就一个地方,也就是前面一个方法!!! 
	 * 但下载有两个地方:一个是本类中的downloadfile方法,另一个是DownLoadFileServlet,即本类中的方法是Swing控件中的直接下载,而浏览器中还可能直接从一个Servlet下载
	 * @param _serverdir
	 * @param _serverFileName
	 * @param _isAbsoluteSeverDir
	 * @return
	 * @throws Exception
	 */
	public cn.com.infostrategy.to.common.ClassFileVO downLoadFile(String _serverdir, String fileName, boolean absoluteSeverDir) throws Exception {
		TBUtil tbutil = new TBUtil(); //  
		String str_filepathname = null; //
		if (absoluteSeverDir) {// 如果是绝对路径
			str_filepathname = _serverdir + "/" + fileName; //
		} else {
			if (_serverdir == null || _serverdir.trim().equals("")) { // 如果是相对路径,且没有定义相对路径,则直接放在系统上传目录的根目录下!
				str_filepathname = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/" + fileName; //
			} else {
				str_filepathname = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + _serverdir + "/" + fileName; //
			}
		}
		str_filepathname = tbutil.replaceAll(str_filepathname, "\\", "/"); //文件全路径
		String str_newWebUrl = ServerEnvironment.getProperty("FullTextSearchURL"); //刘旋飞加的逻辑,即可以转向存储到另一台机器上
		if (str_newWebUrl != null) { //如果定义了这个参数!
			try {
				System.out.println("BSUtil.downLoadFile()从远程全文检索的地址[" + str_newWebUrl + "/UploadFileServlet]下载文件[" + fileName + "]....");
				HashMap requestMap = new HashMap();
				requestMap.put("Action", "download");
				requestMap.put("FileName", str_filepathname); //文件全称!
				HashMap rtMap = callWebUrl(str_newWebUrl + "/UploadFileServlet", requestMap); //
				if ("Y".equals(rtMap.get("isException"))) { //如果发生异常
					Exception ex = (Exception) rtMap.get("Exception"); //
					throw new Exception("远程请求文件发生异常:" + ex.getMessage(), ex); //
				}
				ClassFileVO cFileVO = new ClassFileVO();
				cFileVO.setByteCodes((byte[]) rtMap.get("ByteCodes")); //返回的字节码!!
				cFileVO.setClassFileName((String) rtMap.get("OldFileName"));
				return cFileVO;
			} catch (Exception e) {
				throw new WLTAppException(e.getMessage());
			}
		} else if (ServerEnvironment.getProperty("FtpFileServerURL") != null && !ServerEnvironment.getProperty("FtpFileServerURL").equals("")) { //如果定义了FTP,则从Ftp取!!
			//以后补充
			//new FtpDealUtil().downFile(_ip, _port, _user, _pwd, _fileName);  //
			return null; //
		} else { //直接从本地取!
			byte[] filecontent = null;
			try {
				filecontent = tbutil.readFromInputStreamToBytes(new FileInputStream(str_filepathname)); //文件内容!!!
			} catch (FileNotFoundException ex) { //如果文件没找到!则去数据库中试找下!
				filecontent = getFileContentFromDB(fileName, "没有发现文件[" + tbutil.replaceHtmlEncode(str_filepathname) + "],转向数据库中查找..."); //从数据库读文件!!!!!
			}
			cn.com.infostrategy.to.common.ClassFileVO filevo = new cn.com.infostrategy.to.common.ClassFileVO();
			filevo.setClassFileName(fileName);
			filevo.setByteCodes(filecontent);
			return filevo;
		}
	}

	/**
	 * 从数据库中取数据!
	 * @param _fileName
	 * @return
	 * @throws Exception
	 */
	public byte[] getFileContentFromDB(String _fileName, String _preInfo) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		TBUtil tbutil = new TBUtil();
		//sql注入漏洞,需要用预编译处理 by haoming 20160519
		HashVO[] hvs_1 = commDMO.getHashVoStructByDS(null, "select * from pub_upfiles where filename='" + _fileName + "'", true, false, null, false, false, true).getHashVOs();
		//		HashVO[] hvs_1 = commDMO.getHashVoArrayByDS(null, "select * from pub_upfiles where filename='" + _fileName + "'"); //先根据文件名查表
		if (hvs_1 == null || hvs_1.length == 0) {
			throw new WLTAppException(_preInfo + "\r\n数据库表pub_upfiles中没有filename='" + tbutil.replaceHtmlEncode(_fileName) + "'的文件!!!"); //
		}
		if (hvs_1.length > 1) {
			throw new WLTAppException(_preInfo + "\r\n数据库表pub_upfiles中竟然找到两个filename='" + tbutil.replaceHtmlEncode(_fileName) + "'的文件,这可能造成打开的不是你要找的文件!!!"); //
		}
		String str_id = hvs_1[0].getStringValue("id"); //文件的id
		boolean isZiped = hvs_1[0].getBooleanValue("isziped"); //是否压缩!!
		HashVO[] hvs_2 = commDMO.getHashVoArrayByDS(null, "select * from pub_upfiles_b where fileid='" + str_id + "' order by seq"); //从文件子表寻找!!
		StringBuilder sb_64code = new StringBuilder(); //拼接的字符串!
		String str_text = null; //
		for (int i = 0; i < hvs_2.length; i++) { //遍历每一行
			for (int j = 0; j < 10; j++) { //遍历25列
				str_text = hvs_2[i].getStringValue("c" + (j + 1)); //取得某一列的数据!!!
				if (str_text == null || str_text.trim().equals("")) {
					break; //如果没数据了,则中断!!
				}
				sb_64code.append(str_text); //不断的拼接!!!
			}
		}
		byte[] bytes = new TBUtil().convert64CodeToBytes(sb_64code.toString()); ///将64位码转成byte[]
		if (isZiped) { //如果是经过压缩的,则进行解压! 因为有些本身就是rar文件,根本没有再压缩的空间了,所以系统不做压缩处理!!!
			bytes = decompressBytes(bytes); //解压一把!!!
		}
		return bytes; //返回!

	}

	/**
	 * 将一个字符串进行打断分隔,按指定的宽度!这个方法非常有用!! 在需要将一个大字符串插入到一个表中的时候非常需要!
	 * 即先一行行的插入,插到行尾时换行! 如果最后一行与最后一行需要特殊处理!!!即零头的处理!!
	 * @param _str
	 * @param _oneColWidth 一列的字符串个数
	 * @param _oneRowCols 一行有几列
	 * @return
	 */
	public ArrayList splitStrBySpecialWidth(String _str, int _oneRowCols, int _oneColWidth) {
		return new TBUtil().split(_str, _oneRowCols, _oneColWidth); //
	}

	/**
	 * 服务器word文件是否含有关键字
	 * @param _fileName 服务器绝对路径
	 * @param _keywords为关键字
	 * @param _isAllContain 判断文件是否同时包含所有关键字
	 * @return
	 * @throws Exception
	 */
	public boolean checkWordFileContainKey(String _serverdir, String _fileName, String[] _keywords, boolean _isAllContain) {
		if (_keywords == null || _keywords.length <= 0) {
			return false; //
		}
		FileInputStream input = null;
		try {
			/** start liuxuanfei **/
			File file = new File(_serverdir + _fileName); // 先判断文件是否存在
			if (!file.exists()) {
				return false;
			}
			input = new FileInputStream(file);
			/** end **/
			WordExtractor we = new WordExtractor(input); //这里可能报错!
			String content = we.getText();
			//input.close();
			if (content == null || content.trim().equals("")) {
				return false;
			}
			content = content.toLowerCase();
			boolean isfinded = true; //默认为真
			for (int jj = 0; jj < _keywords.length; jj++) { //遍历所有关键字
				if (content.indexOf(_keywords[jj].toLowerCase()) < 0) { //如果有一个对不上,就退出
					isfinded = false; //
					break; //
				}
			}
			return isfinded; //
		} catch (Exception e) { //
			System.err.println("解析Word文件[" + _fileName + "]发生异常:" + e.getMessage()); //
			//e.printStackTrace();
			return false; //
		} finally {
			try {
				input.close(); //
			} catch (Exception ex) {
			}
		}
	}

	/**
	 * 服务器excel文件是否含有关键字
	 * @param _fileName 服务器绝对路径
	 * @param _keywords 为关键字
	 * @param _isAllContain 判断文件是否同时包含所有关键字
	 * @return
	 * @throws Exception
	 */

	public boolean checkExcelFileContainKey(String _serverdir, String _fileName, String[] _keywords, boolean _isAllContain) {
		if (_keywords == null || _keywords.length <= 0) {
			return false; //
		}
		FileInputStream input = null;
		POIFSFileSystem fileSystem = null;
		HSSFWorkbook workBook = null;
		HSSFSheet sheet = null;
		HSSFRow row = null;
		HSSFCell cell = null;
		List allList = null;
		int beginRow = 0;
		int endRow = 0;
		int beginColumn = 0;
		int endColumn = 0;
		int maxColumns = 0;
		try {
			/** start liuxuanfei **/
			File file = new File(_serverdir + _fileName); // 先判断文件是否存在
			if (!file.exists()) {
				return false;
			}
			input = new FileInputStream(file);
			/** end **/
			fileSystem = new POIFSFileSystem(input);
			workBook = new HSSFWorkbook(fileSystem);
			sheet = workBook.getSheetAt(0);
			beginRow = sheet.getFirstRowNum();
			endRow = sheet.getLastRowNum();
			allList = new ArrayList();
			for (int i = beginRow; i <= endRow; i++) {
				row = sheet.getRow(i);
				if (row != null) {
					beginColumn = row.getFirstCellNum();
					endColumn = row.getLastCellNum();
					if (endColumn > maxColumns) {
						maxColumns = endColumn;
					}
					for (int j = beginColumn; j < endColumn; j++) {
						cell = row.getCell((short) j);
						if (cell != null) {
							if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
								allList.add(cell.getStringCellValue().toLowerCase());
							} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
								String number = "";
								if (cell.getNumericCellValue() - ((long) cell.getNumericCellValue()) != 0) { // 目前运算不准确，以后偏差在0.01左右，还可以接受
									DecimalFormat df = new DecimalFormat("0.00");
									number = df.format(cell.getNumericCellValue());
								} else {
									number = String.valueOf((long) cell.getNumericCellValue());
								}
								allList.add(number);
							} else if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
								allList.add(String.valueOf(cell.getBooleanCellValue()).toLowerCase());
							}
						}
					}
				}
			}

			StringBuilder sb_text = new StringBuilder(); //
			for (int i = 0; i < allList.size(); i++) {
				String str_cellvalue = (String) allList.get(i); //
				if (str_cellvalue != null) {
					sb_text.append("#" + str_cellvalue + "#"); //
				}
			}
			String str_alltext = sb_text.toString(); //
			if (str_alltext.equals("")) {
				return false;
			}
			boolean isFinded = true; //
			for (int i = 0; i < _keywords.length; i++) { //与的关系,即必须都对上,只要一个以坏上就立即退出!
				if (str_alltext.indexOf(_keywords[i].toLowerCase()) < 0) { //如果有一个对不上,就退出
					isFinded = false; //
					break; //
				}
			}
			return isFinded; //
		} catch (Exception e) {
			System.err.println("解析Excel文件[" + _fileName + "]发生异常:" + e.getMessage()); //
			//e.printStackTrace();
			return false;
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 服务器word或excel文件是否含有关键字
	 * @param fileNames 服务器绝对路径
	 * @param _keywords 为关键字
	 * @param isAllContain 判断文件是否同时包含所有关键字
	 * @return
	 * @throws Exception
	 */

	public List checkWordOrExcelContainKeys(String _serverdir, List _filenames, String[] _keywords, boolean _isAllContain) {
		List returnList = new ArrayList();
		for (int i = 0; i < _filenames.size(); i++) {
			String filename = (String) _filenames.get(i); //
			if (filename != null) {
				if ((filename.toLowerCase().endsWith(".doc") || filename.toLowerCase().endsWith(".wps")) && checkWordFileContainKey(_serverdir, filename, _keywords, _isAllContain)) {
					returnList.add(filename);
				} else if (filename.toLowerCase().endsWith(".xls") && checkExcelFileContainKey(_serverdir, filename, _keywords, _isAllContain)) {
					returnList.add(filename);
				}
			}
		}
		return returnList;
	}

	/** start liuxuanfei **/
	public List<String> checkWordOrExcelContainKeys(String _serverdir, String[][] _filesInfo, String[] _keywords, boolean _isAllContain) {
		if (ServerEnvironment.getProperty("FullTextSearchURL") != null) { //如果有全文检索的url这个参数!则执行这个参数!!即在兴业项目中遇到四个中间的文件存储目录都是X:/这时检索就非常慢!!!需要在文件目标服务器上做!!
			String str_fullsearchurl = ServerEnvironment.getProperty("FullTextSearchURL"); //
			logger.info("定义了全文检索的地址[" + str_fullsearchurl + "],系统正在再次该地址进行全文检索..."); //
			try {
				long ll_1 = System.currentTimeMillis(); //
				TBUtil tbUtil = new TBUtil(); ////
				HashMap requestMap = new HashMap(); //
				requestMap.put("subdir", "/officecompfile/"); //子目录!!默认是!
				requestMap.put("FileInfo", _filesInfo); //
				requestMap.put("KeyWords", _keywords); //
				requestMap.put("isAllContain", new Boolean(_isAllContain)); //
				byte[] requestBytes = tbUtil.serialize(requestMap); //先序列化!!!
				URL url = new URL(str_fullsearchurl + "/FullTextSearchServlet"); //定义远程的全文检索的url!!! http://192.168.0.25:80/hg 
				URLConnection conn = url.openConnection(); //创建连接
				if (str_fullsearchurl.startsWith("https")) {
					addHttpsParam(conn);
				}
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setUseCaches(false); //
				tbUtil.writeBytesToOutputStream(conn.getOutputStream(), requestBytes); //输出到服务器端!!
				byte[] responseBytes = tbUtil.readFromInputStreamToBytes(conn.getInputStream()); //从服务器端得到反馈!!
				HashMap responseMap = (HashMap) tbUtil.deserialize(responseBytes); //反序列化!!
				Object obj_return = responseMap.get("ReturnFileIds"); //
				if (obj_return instanceof Exception) {
					throw (Exception) obj_return; //
				} else {
					ArrayList al_return = (ArrayList) obj_return; //
					long ll_2 = System.currentTimeMillis(); //
					logger.info("从全文检索的地址[" + str_fullsearchurl + "]取数成功,输入的文件数[" + _filesInfo.length + "],返回文件数[" + al_return.size() + "],共耗时[" + (ll_2 - ll_1) + "]"); //
					return al_return; //返回!!!
				}
			} catch (Exception ex) {
				throw new WLTAppException(ex.getMessage()); //
			}
		} else { //如果没有定义!!!
			int li_thread_df = _filesInfo.length / 100; //先假设一个线程处理100个文件!!倒算线程数!
			if (li_thread_df <= 0) { //至少有一个线程!!
				li_thread_df = 1;
			}
			if (li_thread_df >= 20) { //最多30个线程!!!
				li_thread_df = 20;
			}
			int li_onethread_filecount = _filesInfo.length / li_thread_df; //30个线程同时跑,一个线程处理多少个文件!!  1000/30=33(10)
			int li_model = _filesInfo.length % li_thread_df; //余数
			HashMap mainMap = new HashMap(); //先给主线程的Map塞入值!!!
			if (li_onethread_filecount > 0) {
				for (int i = 0; i < li_thread_df; i++) { //遍历!!
					mainMap.put("" + i, null); //
				}
			}
			if (li_model != 0) { //如果有余数!!
				mainMap.put("" + li_thread_df, null); //
			}
			logger.debug("一共启动[" + li_thread_df + "]个线程,每个线程处理[" + li_onethread_filecount + "]个文件,最后一个余数线程处理[" + li_model + "]个文件"); //
			if (li_onethread_filecount > 0) { //启动线程!
				for (int i = 0; i < li_thread_df; i++) { //遍历!!
					String[][] str_fileinfo_item = new String[li_onethread_filecount][2]; //
					System.arraycopy(_filesInfo, li_onethread_filecount * i, str_fileinfo_item, 0, li_onethread_filecount); //拷贝33个文件
					new CheckFileThread("" + i, mainMap, str_fileinfo_item, _serverdir, _keywords).start(); //启动线程!!
				}
			}
			if (li_model != 0) { //如果有余数!!
				String[][] str_fileinfo_item = new String[li_model][2]; //
				System.arraycopy(_filesInfo, li_onethread_filecount * li_thread_df, str_fileinfo_item, 0, li_model); //拷贝33个文件
				new CheckFileThread("" + li_thread_df, mainMap, str_fileinfo_item, _serverdir, _keywords).start(); //启动线程!!
			}

			String[] str_keys = (String[]) mainMap.keySet().toArray(new String[0]); //
			int li_tmp = 1;
			int li_while_count = 1; //
			while (li_tmp == 1) { //做死循环!!
				li_while_count++; //
				boolean isAllEnd = true; //是否全部结束!!
				ArrayList al_temp = new ArrayList(); //
				int li_notendthread = 0; //
				for (int i = 0; i < str_keys.length; i++) {
					ArrayList al_item = (ArrayList) mainMap.get(str_keys[i]); //
					if (al_item == null) {
						li_notendthread++; //
						isAllEnd = false; //
					} else {
						al_temp.addAll(al_item); //加入!!!
					}
				}
				if (isAllEnd) {
					logger.debug("所有[" + str_keys.length + "]个线程都处理结束了,共返回[" + al_temp.size() + "]个文件!"); //
					return al_temp; //
				} else {
					logger.debug("发现仍然有[" + li_notendthread + "]线程没结束(共[" + str_keys.length + "]个线程),一秒后继续扫描!!"); //
					try {
						Thread.currentThread().sleep(500); //每休息一秒
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					if (li_while_count > 1800) { //如果15分钟还没反应,则立即退出!!
						return new ArrayList(); //
					}
				}
			}
			return null; //实际上永远走不到这!
		}
	}

	/**
	 * 根据Key取得某个初始化参数的值
	 * 
	 * @param _key
	 * @return
	 */
	public String getInitParamValue(String _key) {
		InitParamVO[] vos = ServerEnvironment.getInstance().getInitParamVOs(); //
		for (int i = 0; i < vos.length; i++) {
			if (vos[i].getKey().equalsIgnoreCase(_key)) {
				return vos[i].getValue();
			}
		}
		return null;
	}

	/**
	 * 取得项目的名称
	 * 
	 * @return
	 */
	public String getProjectName() {
		return getInitParamValue("PROJECT_NAME"); //
	}

	public String getDBType(String _dsName) {
		DataSourceVO[] allDsVOs = ServerEnvironment.getInstance().getDataSourceVOs(); //
		for (int i = 0; i < allDsVOs.length; i++) {
			if (allDsVOs[i].getName().equals(_dsName)) {
				return allDsVOs[i].getDbtype(); // 返回数据库类型
			}
		}
		return null;
	}

	/**
	 * 必须连续跑过15秒了,而且这15秒内,每秒都有20个线程,才算是真正的忙!!!
	 * 以前算总数的方法有问题!!!
	 * 但这个方法在并发时会不会有性能的锁问题?? 全部依靠Vector的功能了??还是有没可能与HeardThread冲突了!
	 * @return
	 */
	public boolean isRealBusiCall() {
		try {
			List vc_calltimes = RemoteCallServlet.callThreadTimeList; //调用的时间
			int li_area = 10; //15秒
			long ll_currtime = System.currentTimeMillis(); //到秒!!
			long ll_beginTime = ll_currtime - (li_area * 1000); //最近15秒的累加!
			long ll_endTime = ll_currtime - 4000; //最近3秒的累加!

			//找出大于数据的累加
			LinkedHashMap map_second = new LinkedHashMap(); //排序的!
			Long[] ll_timeList = (Long[]) vc_calltimes.toArray(new Long[0]); //必须用这个方法!! 而且并发时慢就慢在这里!!!
			for (int i = 0; i < ll_timeList.length; i++) {
				Long ll_item = ll_timeList[i]; //这里可能有问题!!!即在被删除的情况下,坐标发生错位!甚至报错!
				if (ll_item >= ll_beginTime && ll_item <= ll_endTime) { //必须是15秒内的!但当前秒不算,因为当前秒很可能很小!!!
					Long ll_second = ll_item / 1000; //折算成秒!!
					if (map_second.containsKey(ll_second)) {
						int li_count = (Integer) map_second.get(ll_second); //
						map_second.put(ll_second, li_count + 1); //加上
					} else {
						map_second.put(ll_second, 1); //置入!!
					}
				}
			}
			int li_mapSize = map_second.size(); //
			if (li_mapSize < 3) { //如果还没有15秒,则不算忙,在测试时
				return false; //
			}

			Long[] ll_keys = (Long[]) map_second.keySet().toArray(new Long[0]); //
			for (int i = 1; i < ll_keys.length - 1; i++) {
				int li_count = (Integer) map_second.get(ll_keys[i]); //
				if (li_count < ServerEnvironment.realBusiCallCountOneSecond) { //只要有一个小于20则算不忙,即必须这15秒中每一秒都有20个访问,才算是真正的忙!! 
					return false; //不忙!!
				}
			}
			return true; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return false; //
		}
	}

	public String isRealBusiCallHtml() {
		List vc_calltimes = RemoteCallServlet.callThreadTimeList; //调用的时间
		int li_area = 10; //15秒
		long ll_currtime = System.currentTimeMillis(); //到秒!!
		long ll_beginTime = ll_currtime - (li_area * 1000); //最近15秒的累加!
		long ll_endTime = ll_currtime - 4000; //最近3秒的累加!

		//找出大于数据的累加
		LinkedHashMap map_second = new LinkedHashMap(); //排序的!
		Long[] ll_timeList = (Long[]) vc_calltimes.toArray(new Long[0]); //必须用这个方法!! 而且并发时慢就慢在这里!!!
		for (int i = 0; i < ll_timeList.length; i++) {
			Long ll_item = ll_timeList[i]; //这里可能有问题!!!即在被删除的情况下,坐标发生错位!甚至报错!
			if (ll_item >= ll_beginTime && ll_item <= ll_endTime) { //必须是15秒内的!但当前秒不算,因为当前秒很可能很小!!!
				Long ll_second = ll_item / 1000; //折算成秒!!
				if (map_second.containsKey(ll_second)) {
					int li_count = (Integer) map_second.get(ll_second); //
					map_second.put(ll_second, li_count + 1); //加上
				} else {
					map_second.put(ll_second, 1); //置入!!
				}
			}
		}

		boolean isRealBusi = true; //
		int li_mapSize = map_second.size(); //
		if (li_mapSize < 3) { //如果还没有15秒,则不算忙,在测试时
			isRealBusi = false; //
		}
		Long[] ll_keys = (Long[]) map_second.keySet().toArray(new Long[0]); //
		StringBuilder sb_html = new StringBuilder(); //
		SimpleDateFormat sdf_curr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE); //
		if (isRealBusi) {
			for (int i = 1; i < ll_keys.length - 1; i++) {
				int li_count = (Integer) map_second.get(ll_keys[i]); //
				if (li_count < ServerEnvironment.realBusiCallCountOneSecond) { //只要有一个小于20则算不忙,即必须这15秒中每一秒都有20个访问,才算是真正的忙!! 
					isRealBusi = false; //不忙!!
					break;
				}
			}
		}
		sb_html.append("是否真的很忙=[" + isRealBusi + "],很忙的实际计算结果(必须每个都大于" + ServerEnvironment.realBusiCallCountOneSecond + "才算真的忙):<br>\r\n"); //
		for (int i = 1; i < ll_keys.length - 1; i++) {
			String str_time = sdf_curr.format(new Date(ll_keys[i] * 1000)); //补上差异!!
			int li_count = (Integer) map_second.get(ll_keys[i]); //
			sb_html.append("[" + str_time + "]=[" + li_count + "]<br>\r\n"); //
		}
		return sb_html.toString(); //
	}

	/**
	 * 取得所有安装的包! 第一个是平台的,后面依次是各个产品或项目的!!!
	 * 返回的结果的第一列是包名,第二列是包的说明!!!
	 * @return
	 */
	public String[][] getAllInstallPackages(String _subdir) throws Exception {
		ArrayList al_list = new ArrayList(); //
		String str_platFormRootDir = "/cn/com/infostrategy/bs/sysapp/install/"; //平台的根路径!!!是固定的!!
		if (_subdir != null && !_subdir.trim().equals("")) { //如果子目录不为空
			if (_subdir.startsWith("/")) { //如果子目录不小心写成斜杠开头的,则要裁掉!!
				str_platFormRootDir = str_platFormRootDir + _subdir.substring(1, _subdir.length()); //
			} else {
				str_platFormRootDir = str_platFormRootDir + _subdir; //
			}
			if (!str_platFormRootDir.endsWith("/")) { //如果后面没有/则加上!
				str_platFormRootDir = str_platFormRootDir + "/"; //
			}
		}
		al_list.add(new String[] { str_platFormRootDir, "WebPush平台", null }); //先加入平台!!
		String str_installs = ServerEnvironment.getProperty("INSTALLAPPS"); ////
		if (str_installs != null && !str_installs.trim().equals("")) { //如果有!!
			TBUtil tbUtil = new TBUtil(); //
			String[] str_items = tbUtil.split(str_installs, ";"); //
			for (int i = 0; i < str_items.length; i++) {
				int li_pos = str_items[i].indexOf("-"); //
				String str_package = null; //
				String str_packdescr = null; //
				if (li_pos > 0) { //如果有中杠说明
					str_package = str_items[i].substring(0, li_pos); //中杠前面的
					str_packdescr = str_items[i].substring(li_pos + 1, str_items[i].length()); //中杠后面的!
				} else {
					str_package = str_items[i]; //就直接是包名!!
					str_packdescr = "未命名"; //
				}
				str_package = tbUtil.replaceAll(str_package, ".", "/"); //将逗号转成反斜杠!!!
				if (!str_package.startsWith("/")) { //如果前面没/则加上
					str_package = "/" + str_package; //
				}
				if (!str_package.endsWith("/")) { //如果后面没有/则加上!
					str_package = str_package + "/"; //
				}

				if (_subdir != null && !_subdir.trim().equals("")) { //如果子目录不为空
					if (_subdir.startsWith("/")) { //如果子目录不小心写成斜杠开头的,则要裁掉!!
						_subdir = _subdir.substring(1, _subdir.length()); //
					}
					if (!_subdir.endsWith("/")) { //如果子目录不是以斜杠结尾,则补上!
						_subdir = _subdir + "/"; //
					}
					str_package = str_package + _subdir; //包名
				}

				//寻找这个安装包下面的RegisterMenu.xml中有没有<app>子项!!这是后来新加的功能,即考虑到所谓4个产品,其实本质上是一个产品的四种表现,为了兼容以前的逻辑,且最大限度的简化安装!在RegisterMenu.xml中增加了<app>项!
				StringBuilder sb_apptext = new StringBuilder(); //
				InputStream ins = this.getClass().getResourceAsStream(str_package + "RegisterMenu.xml"); //
				if (ins != null) {
					org.jdom.Document doc = new org.jdom.input.SAXBuilder().build(ins); // 加载XML
					java.util.List list_app = doc.getRootElement().getChildren("app"); //寻找app子结点!
					if (list_app != null && list_app.size() > 0) { //如果找到!!
						for (int k = 0; k < list_app.size(); k++) { //遍历所有app,即可能有多个!
							org.jdom.Element el_app = (org.jdom.Element) list_app.get(k); //
							java.util.List list_attrs = el_app.getAttributes(); //得到所有属性!!!
							for (int r = 0; r < list_attrs.size(); r++) { //遍历所有属性!
								org.jdom.Attribute attrItem = (org.jdom.Attribute) list_attrs.get(r); //取得某个属性!!
								String str_attr_name = attrItem.getName(); //属性名称!!
								String str_attr_value = attrItem.getValue(); //属性值!!
								sb_apptext.append(str_attr_name + "=" + str_attr_value); //key=value
								if (r < list_attrs.size() - 1) { //4,0123
									sb_apptext.append(";"); //如果不是最后一个,则加分号!
								}
							}
							if (k < list_app.size() - 1) { //4,0123
								sb_apptext.append("@"); //如果不是最后一个,则加分号!
							}
						}
					}
				}
				al_list.add(new String[] { str_package, str_packdescr, sb_apptext.toString() }); //包的描述!
			}
		}
		String[][] str_return = new String[al_list.size()][3]; ////
		for (int i = 0; i < str_return.length; i++) { //遍历!!!
			str_return[i] = (String[]) al_list.get(i); //
		}
		return str_return; //
	}

	//取得一个结点是的某些列的所有值
	public String[] getOneTreeNodeParentPathItemValue(DefaultMutableTreeNode _node, String[] _itemKeys) {
		Stack[] tempStacks = new Stack[_itemKeys.length]; //使用堆栈,末结点在下面,父亲结点在上面,取的时候真好是从上至下!!
		for (int i = 0; i < tempStacks.length; i++) {
			tempStacks[i] = new Stack(); //
		}
		getOneTreeNodeParentPathItemValue(_node, _itemKeys, tempStacks); //递归调用!!
		StringBuilder[] sb_returns = new StringBuilder[_itemKeys.length]; //
		for (int i = 0; i < sb_returns.length; i++) {
			sb_returns[i] = new StringBuilder(); //
			if (!tempStacks[i].isEmpty()) {
				sb_returns[i].append(";"); //如果有的话,行要有第一个加上!!!
			}
			while (!tempStacks[i].isEmpty()) {
				String str_itemValue = (String) tempStacks[i].pop(); //
				sb_returns[i].append(str_itemValue + ";"); //
			}
		}

		String[] str_returns = new String[sb_returns.length]; //
		for (int i = 0; i < str_returns.length; i++) {
			str_returns[i] = sb_returns[i].toString();
		}
		return str_returns; //返回数组!!
	}

	//递归调用
	private void getOneTreeNodeParentPathItemValue(DefaultMutableTreeNode _node, String[] _itemKeys, Stack[] _tempStacks) {
		if (_node.isRoot()) { //如果到了根结点,就返回了!!
			return;
		}
		HashVO hvo = (HashVO) _node.getUserObject(); //
		for (int i = 0; i < _tempStacks.length; i++) {
			_tempStacks[i].push(hvo.getStringValue(_itemKeys[i], "")); //先塞进去!!!
		}
		getOneTreeNodeParentPathItemValue((DefaultMutableTreeNode) _node.getParent(), _itemKeys, _tempStacks); //递归调用!!
	}

	public String getTreePathItemValueFromHashVOs(HashVO[] _hvsAll, String str_linkedIDFieldName, String _returnfieldName, String _whereFieldName, String _whereCondition) {
		//long ll_1 = System.currentTimeMillis(); //
		TBUtil tbUtil = new TBUtil(); //
		HashMap[] maps = tbUtil.getHashMapsFromHashVOs(_hvsAll, new String[][] { { str_linkedIDFieldName, _returnfieldName }, { _whereFieldName, str_linkedIDFieldName }, { str_linkedIDFieldName, "$parentpathids" } }); //
		HashMap id_nams_map = maps[0];
		HashMap where_pks_map = maps[1];
		HashMap id_parentids_map = maps[2];
		String str_whereMatch_id = (String) where_pks_map.get(_whereCondition); //看看有没有！！
		if (str_whereMatch_id != null) { //如果找到了
			String str_parentids = (String) id_parentids_map.get(str_whereMatch_id); //
			if (str_parentids != null) { //如果有父亲记录
				String[] str_ids = tbUtil.split(str_parentids, ";"); //分隔一下!!
				StringBuilder sb_name = new StringBuilder(); //
				for (int i = 0; i < str_ids.length; i++) {
					if (i != str_ids.length - 1) {
						sb_name.append(id_nams_map.get(str_ids[i]) + "->"); //拼起来!!
					} else {
						sb_name.append(id_nams_map.get(str_ids[i])); //拼起来!!
					}
				}
				//long ll_2 = System.currentTimeMillis(); //
				//System.out.println("计算一把耗时[" + (ll_2 - ll_1) + "]"); //像机构树执行一次要15毫秒左右(在我笔记本上)!!3000多条的记录!!
				return sb_name.toString();
			}
		}
		return "";
	}

	public HashVO[] getTreePathVOsByOneRecord(HashVO _leafVO, String _tableName, String _idFieldName, String _parentIdFieldName) throws Exception {
		Stack stack = new Stack(); //
		getTreePathVOsByOneRecord(_leafVO, _tableName, _idFieldName, _parentIdFieldName, stack); ////
		HashVO[] hvsReturn = new HashVO[stack.size()]; //
		int i = 0;
		while (!stack.isEmpty()) {
			hvsReturn[i] = (HashVO) stack.pop(); //
			i++;
		}
		return hvsReturn; //
	}

	//递归算法
	private void getTreePathVOsByOneRecord(HashVO _leafVO, String _tableName, String _idFieldName, String _parentIdFieldName, Stack _stack) throws Exception {
		_stack.add(_leafVO); //先塞入
		String str_parentIdValue = _leafVO.getStringValue(_parentIdFieldName); //找到父亲记录Id
		if (str_parentIdValue == null || str_parentIdValue.trim().equals("")) { //如果父亲记录Id为空,则直接退出
			return;
		}

		HashVO[] hvsParent = new CommDMO().getHashVoArrayByDS(null, "select * from " + _tableName + " where " + _idFieldName + "='" + str_parentIdValue + "'"); //找到父亲记录的
		if (hvsParent == null || hvsParent.length <= 0) { //如果父亲记录没找到,即有可能某种原因造成外键关联失败了,比如导数据等造成的,则直接退出!
			return;
		}
		getTreePathVOsByOneRecord(hvsParent[0], _tableName, _idFieldName, _parentIdFieldName, _stack); //递归调用!!
	}

	/**
	 * 压缩数据,为了节省内存开销,服务器端与客户端不一样,而使用了一个CompressBytesVO,这是因为如果像客户端一样复制另一个数组,则会开销内存过多!!
	 * 一般来说,rar文件会压缩失败,一个5M的rar文件,压缩耗时1秒,而且是压缩失败的! 一个2M的rar文件耗时0.5秒
	 * 一个3M的非rar文件,则会压缩成功,耗时0.4秒!
	 * 新的方法实现了以下几点:
	 * 1.在第一位加标记位,动态决定是否真正压缩!
	 * 2.太小的数据不进行压缩,取得高的性价比
	 * 3.动态计算临时空间,保证较大的文件能快速处理,较小的文件能节省内存
	 * 4.压缩比小的文件不进行压缩,比如rar文件,从而提高性能
	 * 5.客户端与服务器的参数不一样,加标记位的方法也不一样!
	 * @param _initbytes
	 * @return
	 */
	public CompressBytesVO compressBytes(byte[] _initbytes) {
		//long ll_1 = System.currentTimeMillis(); //
		if (_initbytes.length <= 15360) { //如果小于15K,则不压缩则是直接返回!!!这样性能会更高些!因为这时压缩的意义已不大了,因为数量小并不会产生网络传输瓶颈!!!反之,压缩本身是消耗CPU与内存的,而且根本没有什么可压缩空间的,即10K的文件再压也压不到哪去!所以这时不压缩反而更好!!
			//System.out.println("因为数据量太小而不压缩!"); //
			return new CompressBytesVO(false, _initbytes); //
		}
		//计算合理的临时压缩空间,即太大与太小都不好,应合理计算!!!这个对性能影响非常关键,即必须在时间与空间两者之间找到一个最科学平衡点!!!否则会造成要么非常耗时,要么狂耗内存!! 都不好!!
		int li_size = _initbytes.length / 20; //假设最坏的情况下,即啥都压不了,认为压缩10次,那么空间放多大!!!
		if (li_size < 5120) { //如果小于2K,则最小是2K,即下限!!!
			li_size = 5120;
		}
		if (li_size > 51200) { //如果大于50K,则最大为50K,即上限!
			li_size = 51200;
		}
		java.util.zip.Deflater compressor = new java.util.zip.Deflater(); //构建压缩器!!
		compressor.setLevel(java.util.zip.Deflater.BEST_COMPRESSION); //设置压缩模式,即是最好的压缩比模式!而不是最快的!!这与rar的模式一致!!!
		compressor.setInput(_initbytes); //置入内容!
		compressor.finish(); //真正进行压缩...
		ByteArrayOutputStream bos = new ByteArrayOutputStream(_initbytes.length); //创建输出流,即接收压缩输出内容的流!
		byte[] buf = new byte[li_size]; //根据上面计算出来的最科学的临时变量空间,创建该空间!
		long li_cyclecount = 0; //计数器,看压缩了多少回!
		boolean isBreakZip = false; //是否中断了压缩过程!!!
		long ll_maxcycleCount = (long) ((long) _initbytes.length * 0.8 / li_size) + 2; //即如果压缩比小于1/5,则不压缩了,即压缩后的大小必须小于原来大小的60%才有压缩意义!!
		//System.out.println("临时压缩空间=[" + li_size + "],最大压缩次数=[" + ll_maxcycleCount + "]!"); //
		while (!compressor.finished()) { //如果参数传的不对,则有可能会造 成死循环!!!形成阻塞的症状!!!所有必须有一个超过最坏情况的次数,则退出循环,不进行压缩处理,而是返回原来值,则系统的健壮性大为增强!!!!!因为压缩耗时与数据量大小之间有个性能矛盾!即不压缩传输会慢,而压缩太耗时,且又压缩不了多少,则又没有意义!!所以需要一个平衡处理,这就是该处理的初衷!!
			li_cyclecount++; //累加!
			if (li_cyclecount > ll_maxcycleCount) { //如果超过30次,则强行退出,这个处理极其关键! 即有些文件本身就是压缩后的文件(比如rar文件),即没有再被压缩的空间了,则直接返回原始内容!!否则会造成极大循环,结果就像死了一样!! 这里不是根据文件后辍名(比如.rar,.zip)来判断是否需要真正压缩!而是根据实际可压缩次数来判断,是绝对准确的!
				//实际情况中在民生银行与招行等项目中我发现一个rar文件,因为无可压缩空间,结果造成服务器端压缩耗时8秒,客户端解压又要8秒,结果是花蛇添足,得不偿失!!!
				isBreakZip = true; //直接退出压缩过程!!!
				break; //
			}
			int count = compressor.deflate(buf); //将数据压缩到buf中,一般来说,除了最后一次外都会返回1024,即用满了!!!!! 
			bos.write(buf, 0, count); //向输出流中输出!!!
		}
		try {
			bos.close(); //关闭流,并单独异常捕获,从而彻底保证流能绝对关闭,否则会出现内存溢出或锁住文件的状况!!
		} catch (Exception e) {
			e.printStackTrace(); //
		}
		if (isBreakZip) { //如果是因为强行中断压缩而退出的,则直接返回压缩前的内容!
			//System.out.println("因为压缩太麻烦(压了" + li_cyclecount + "回),不压缩"); //
			return new CompressBytesVO(false, _initbytes); //直接返回压缩前的内容!!
		} else {
			//System.out.println("成功压缩了(压了" + li_cyclecount + "回)"); //
			byte[] compressedData = bos.toByteArray(); //从输出流中得到返回大小!
			return new CompressBytesVO(true, compressedData); //返回!!!
		}
		//long ll_2 = System.currentTimeMillis(); //
		//System.out.println("服务器端返回时压缩处理,是否中断压缩=[" + isBreakZip + "],原内容大小[" + _initbytes.length + "],压缩了[" + li_cyclecount + "]回循环,压缩后内容大小=[" + returnBytes.length + "],处理耗时[" + (ll_2 - ll_1) + "]"); //
	}

	/**
	 * 解压某个字节数组,当参数本身就是一个压缩比非常高的文件时就会造成无穷次的压缩,甚至是死循环!!比如一个rar文件,所以会发生有时一些rar文件无法上传或下载的情况!!
	 * @param _initbyte
	 * @return
	 */
	public byte[] decompressBytes(byte[] _initbyte) {
		//long ll_1 = System.currentTimeMillis();
		//计算解压临时空间
		int li_size = _initbyte.length / 5; //假设50%的压缩比,然后最多就膨胀20次,这样就是合理的空间!
		if (li_size < 5120) { //如果小于2K,则最小是2K,即下限
			li_size = 5120;
		}
		if (li_size > 51200) { //如果大于50K,则最大为50K，即上限!
			li_size = 51200;
		}
		java.util.zip.Inflater decompressor = new java.util.zip.Inflater(); //构建解压器!!!
		decompressor.setInput(_initbyte); //置入内容
		ByteArrayOutputStream bos = new ByteArrayOutputStream(_initbyte.length);
		byte[] buf = new byte[li_size]; //要大点才快,但反之又消耗内存!! 以上面科学计算的结果为准!服务器以20K为合理!!
		long li_cyclecount = 0; //
		try {
			while (!decompressor.finished()) {
				li_cyclecount++;
				int count = decompressor.inflate(buf); //解压
				bos.write(buf, 0, count); //输出流
			}
		} catch (DataFormatException e) {
			e.printStackTrace(); //
		}
		try {
			bos.close(); //关闭流,并单独异常捕获,从而彻底保证流能绝对关闭,否则会出现内存溢出或锁住文件的状况!!
		} catch (IOException e) {
		}
		//System.out.println("临时解压空间=[" + li_size + "],实际解压次数=[" + li_cyclecount + "]!"); //
		byte[] decompressedData = bos.toByteArray();
		//long ll_2 = System.currentTimeMillis(); //
		//System.out.println("服务器端解压了提交内容,共耗时[" + (ll_2 - ll_1) + "]"); //
		return decompressedData; //
	}

	/**
	 * 根据文件类型返回Mime类型,返回值的第一列用于_response.setContentType(),第二列值用于_response.setHeader("Content-Disposition","");  //
	 * 在DownLoadFileServlet与WebCallServlet中都用到这个,所以统一在BSUtil中定义!!!
	 * @param _type
	 * @param _filename
	 * @return
	 */
	public String[] getMimeTypeByDocType(String _type, String _filename) {
		if (_type.equalsIgnoreCase("txt") || _type.equalsIgnoreCase("ini")) {
			return new String[] { "text/plain", "text/plain; filename=" + _filename }; //
		} else if (_type.equalsIgnoreCase("htm") || _type.equalsIgnoreCase("html")) {
			return new String[] { "text/html", "text/html; filename=" + _filename }; //
		} else if (_type.equalsIgnoreCase("mht") || _type.equalsIgnoreCase("mhtml")) {
			return new String[] { "message/rfc822", "message/rfc822; filename=" + _filename }; //
		} else if (_type.equalsIgnoreCase("jpg") || _type.equalsIgnoreCase("jpeg")) {
			return new String[] { "image/jpeg", "image/jpeg; filename=" + _filename }; //
		} else if (_type.equalsIgnoreCase("bmp")) {
			return new String[] { "image/bmp", "image/bmp; filename=" + _filename }; //
		} else if (_type.equalsIgnoreCase("gif")) {
			return new String[] { "image/gif", "image/gif; filename=" + _filename }; //
		} else if (_type.equalsIgnoreCase("tif")) {
			return new String[] { "image/tiff", "image/tiff; filename=" + _filename }; //
		} else if (_type.equalsIgnoreCase("xml")) {
			return new String[] { "application/xml", "application/xml; filename=" + _filename }; //
		} else if (_type.equalsIgnoreCase("doc")) {
			return new String[] { "application/msword", "application/msword; filename=" + _filename }; //
		} else if (_type.equalsIgnoreCase("docx")) {//解决docx的文档用ie打开默认打开zip压缩包的问题
			return new String[] { "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/vnd.openxmlformats-officedocument.wordprocessingml.document; filename=" + _filename }; //
		} else if (_type.equalsIgnoreCase("xls")) {
			return new String[] { "application/vnd.ms-excel", "application/vnd.ms-excel; filename=" + _filename }; //
		} else if (_type.equalsIgnoreCase("xlsx")) {//解决xlsx的文档用ie打开默认打开zip压缩包的问题
			return new String[] { "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet; filename=" + _filename }; //
		} else if (_type.equalsIgnoreCase("pdf")) {
			return new String[] { "application/pdf", "application/pdf; filename=" + _filename }; //
		} else if (_type.equalsIgnoreCase("zip")) {
			return new String[] { "application/zip", "application/zip; filename=" + _filename }; //
		} else {
			return new String[] { "application/octet-stream", "application/octet-stream; filename=" + _filename }; //
		}
	}

	/**
	 * 根据文件名判断附件上传路径下文件是否存在
	 * @param filename文件名
	 * @return
	 * @throws Exception
	 */
	public boolean fileExist(String filename) throws Exception {

		File file = (new File(ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/upload/" + new String(filename.getBytes("ISO-8859-1"), "GBk")));
		return file.exists();
	}

	/**
	 * 根据文件名数组判断附件上传路径下是否含全部文件
	 * @param filenames文件名 数组
	 * @return
	 * @throws Exception
	 */
	public boolean filesExist(String[] filenames) throws Exception {
		// 功能改善下 文件不存在的时候可以吧不存在的文件名传回去
		boolean exist = false;
		int j = 0;
		for (int i = 0; i < filenames.length; i++) {
			File file = (new File(ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/upload/" + new String(filenames[i].getBytes("ISO-8859-1"), "GBk")));
			if (file.exists()) {
				j++;
			} else {
				j--;
			}
		}
		if (j == filenames.length) {
			exist = true;
		} else {
			exist = false;
		}
		return exist;
	}

	/**
	 * 根据文件名删除附件上传的文件
	 * @param filename 文件名 文件名可能为相对路径
	 * @return
	 * @throws Exception
	 */
	public boolean deleteZipFileName(String filename) throws Exception {
		String str_fileName = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/upload/" + filename; //
		String str_newWebUrl = ServerEnvironment.getProperty("FullTextSearchURL"); //邮储中集群时不能使用nfs与ftp,所以只能http从另一台机器上存取...
		if (str_newWebUrl != null) { //如果定义了是从远程中取
			System.out.println("因为定义了FullTextSearchURL=[" + str_newWebUrl + "],所以要转向远程删除[" + str_fileName + "]..."); //
			HashMap requestMap = new HashMap();
			requestMap.put("Action", "delete"); //
			requestMap.put("FileName", str_fileName); //必须是路径全称
			URLConnection conn = new URL(str_newWebUrl + "/UploadFileServlet").openConnection(); //
			if (str_newWebUrl.startsWith("https")) {
				addHttpsParam(conn);
			}
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			//先输出请求数据..
			ObjectOutputStream objOut = new ObjectOutputStream(conn.getOutputStream()); //
			objOut.writeObject(requestMap); //
			objOut.flush(); //
			objOut.close(); //
			//再得到返回数据..
			ObjectInputStream objIns = new ObjectInputStream(conn.getInputStream()); //
			HashMap rtMap = (HashMap) objIns.readObject(); //
			objIns.close(); //

			if ("Y".equals(rtMap.get("isException"))) { //如果发生异常
				Exception ex = (Exception) rtMap.get("Exception"); //
				throw new Exception("远程请求删除文件发生异常", ex); //
			}

			System.out.println("从远程服务器[" + str_newWebUrl + "]删除文件[" + str_fileName + "],结果是:" + rtMap.get("ReturnResult"));
			return true;
		} else {
			File file = new File(str_fileName); //
			if (file.exists()) {
				boolean del_result = file.delete(); //删除文件
				if (del_result) {
					System.out.println("从服务器删除文件[" + str_fileName + "]成功！");
				} else {
					System.err.println("从服务器删除文件[" + str_fileName + "]失败,可能不存该文件,或该文件被锁定！");
				}
				return del_result; //
			} else {
				System.out.println("从服务器删除文件[" + str_fileName + "]时发现不存在该文件！");
				return false;
			}
		}
	}

	/**
	* 批量下载
	*/
	public String getZipFileName(String[] filenames) throws Exception {
		String str_zipfileName = filenames[0].substring(0, filenames[0].indexOf(".")) + "_z.jar";
		String[] files = new String[filenames.length];
		String encoding = System.getProperty("file.encoding");
		System.out.println("系统环境字符集:" + encoding);
		for (int i = 0; i < filenames.length; i++) {
			files[i] = filenames[i]; // new
			// String(filenames[i].getBytes("ISO-8859-1"),
			// "GBk"); //
			System.out.println("原文件名:" + filenames[i] + ",转码后:" + new String(filenames[i].getBytes("GBK"), "UTF-8")); //
		}
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/upload/" + str_zipfileName));
		byte[] buf = new byte[1024];
		for (int i = 0; i < files.length; i++) {
			FileInputStream in = new FileInputStream(ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/upload/" + files[i]); //
			out.putNextEntry(new ZipEntry(files[i])); // 创建一个ZipEntry.
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.closeEntry();
			in.close();
		}
		out.close();
		return str_zipfileName;
	}

	/**
	 * 
	 * @param _parMap
	 * @return
	 * @throws Exception
	 */
	public HashMap callWebUrl(String _webUrl, HashMap _parMap) throws Exception {
		URLConnection conn = new URL(_webUrl).openConnection(); //
		if (_webUrl.startsWith("https")) {
			addHttpsParam(conn);
		}
		conn.setDoInput(true); //
		conn.setDoOutput(true); //
		conn.setUseCaches(false); //
		conn.setRequestProperty("Content-type", "application/x-java-serialized-object"); //

		//先输出请求数据..
		ObjectOutputStream objOut = new ObjectOutputStream(conn.getOutputStream()); //
		objOut.writeObject(_parMap); //
		objOut.flush(); //
		objOut.close(); //

		//再得到返回数据..
		ObjectInputStream objIns = new ObjectInputStream(conn.getInputStream()); //
		HashMap rtMap = (HashMap) objIns.readObject(); //
		objIns.close(); //

		return rtMap; //
	}

	/**
	 * https参数设置
	 * @param conn
	 * @throws Exception
	 */
	public void addHttpsParam(URLConnection conn) throws Exception {
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, new TrustManager[] { new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

			}

			public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

			}

			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[] {};
			}
		} }, new java.security.SecureRandom());
		((HttpsURLConnection) conn).setSSLSocketFactory(sc.getSocketFactory());
		((HttpsURLConnection) conn).setHostnameVerifier(new HostnameVerifier() {
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
		});
	}

	class CheckFileThread extends Thread {
		String str_serverdir = null; ///
		String[] str_keywords = null; //
		boolean _isAllContain = false; //
		String str_threadNo = null; //
		HashMap mainThreadMap = null; //
		String[][] str_filesInfo = null; //

		public CheckFileThread(String _threadNo, HashMap _mainThreadMap, String[][] _fileInfo, String _serverdir, String[] _keywords) {
			str_threadNo = _threadNo; //
			mainThreadMap = _mainThreadMap; //
			str_filesInfo = _fileInfo; //
			str_serverdir = _serverdir; //
			str_keywords = _keywords; //
		}

		@Override
		public void run() {
			long ll_1 = System.currentTimeMillis(); //
			ArrayList list_ids = new ArrayList(); //
			for (int i = 0; i < str_filesInfo.length; i++) { //所有文件!!! 要搞成多线程!!!
				if (str_filesInfo[i][0] != null && str_filesInfo[i][1] != null) { //
					if (str_filesInfo[i][1].indexOf(";") != -1) { //
						String[] split_filenames = str_filesInfo[i][1].split(";");
						for (int j = 0; j < split_filenames.length; j++) {
							if (split_filenames[j].toLowerCase().endsWith(".doc") || split_filenames[j].toLowerCase().endsWith(".wps")) {
								if (checkWordFileContainKey(str_serverdir, split_filenames[j], str_keywords, _isAllContain)) {
									list_ids.add(str_filesInfo[i][0]);
									break; // 只要有一个文件包含_keywords, 即可
								}
							} else if (split_filenames[j].toLowerCase().endsWith(".xls")) {
								if (checkExcelFileContainKey(str_serverdir, split_filenames[j], str_keywords, _isAllContain)) {
									list_ids.add(str_filesInfo[i][0]);
									break; // 只要有一个文件包含_keywords, 即可
								}
							}
						}
					} else {
						if (str_filesInfo[i][1].toLowerCase().endsWith(".doc") || str_filesInfo[i][1].toLowerCase().endsWith(".wps")) {
							if (checkWordFileContainKey(str_serverdir, str_filesInfo[i][1], str_keywords, _isAllContain)) {
								list_ids.add(str_filesInfo[i][0]);
							}
						} else if (str_filesInfo[i][1].toLowerCase().endsWith(".xls")) {
							if (checkExcelFileContainKey(str_serverdir, str_filesInfo[i][1], str_keywords, _isAllContain)) {
								list_ids.add(str_filesInfo[i][0]);
							}
						}
					}
				}
			}
			long ll_2 = System.currentTimeMillis(); //
			logger.debug("第[" + str_threadNo + "]号线程处理结束(共" + mainThreadMap.size() + "个线程),共检索了[" + str_filesInfo.length + "]个文件,返回了[" + list_ids.size() + "]个结果,耗时[" + (ll_2 - ll_1) + "]");
			mainThreadMap.put(str_threadNo, list_ids); //查询结束后往主线程的map中置入!!
		}
	}

}

/*******************************************************************************
 * $RCSfile: BSUtil.java,v $ $Revision: 1.48 $ $Date: 2012/11/14 10:44:14 $
 * 
 ******************************************************************************/
