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
 * �������˹�����,��õķ�����������!!! ��ǰ����෽�����Ƶ�CommDMO��MetaDataDMO��ȥ��!!
 * 
 * @author user
 * 
 */
public class BSUtil {

	Logger logger = WLTLogger.getLogger(BSUtil.class); //

	public BSUtil() {
	}

	/**
	 * ����ָ�������ݿ���������ʼ�����ݿ�����
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
	 * �����ϴ��ļ��ķ���,����ǰ�ϴ��кü�������,�Ժ�ͳһ��ת����������!
	 * �ϴ��ļ����Դ洢�����ݿ���,Ҳ����Httpת�浽��һ̨������,Ҳ��Ftpת�浽��һ̨������...
	 * �ϴ��ļ���һ���ط�,Ҳ��������!!! �������������ط�:һ���Ǳ����е�downloadfile����,��һ����DownLoadFileServlet,�������еķ�����Swing�ؼ��е�ֱ������,��������л�����ֱ�Ӵ�һ��Servlet����
	 * @param _fileVO
	 * @param _isChangeFileName
	 * @return
	 * @throws Exception
	 */
	public String uploadFile(cn.com.infostrategy.to.common.ClassFileVO _fileVO, String _serverdir, String _serverFileNewName, boolean _isAbsoluteSeverDir, boolean _isConvertHex, boolean _isAddSerialNo) throws Exception {
		//�ȴ���Ŀ¼
		//System.out.println("��������Ŀ¼[" + _serverdir + "],���������ļ���[" + _serverFileNewName + "]"); //
		String str_beginDir = ServerEnvironment.getProperty("WLTUPLOADFILEDIR"); //
		if (_serverdir == null) { //
			str_beginDir = str_beginDir + "/" + WLTConstants.UPLOAD_DIRECTORY; //Ĭ�����ϴ�������ָ����Ŀ¼!
		} else {
			if (_serverdir.startsWith("/")) {
				str_beginDir = str_beginDir + _serverdir; //
			} else {
				str_beginDir = str_beginDir + "/" + _serverdir; //
			}
		}

		//�����ļ���!�µ��ļ���ת16���Ƶ�,��ǰ����"N���_"��ǰ�!!!
		String str_oldfileName = _fileVO.getClassFileName(); //�ɵ��ļ���!!
		TBUtil tbUtil = new TBUtil(); //
		String[] str_excel = new String[] { "]", " ", "+", "[", "{", "}", "����", "<", ">" }; //�������Щ�������,�����ת��!!
		for (int i = 0; i < str_excel.length; i++) {
			if (str_oldfileName.indexOf(str_excel[i]) >= 0) {
				str_oldfileName = new TBUtil().replaceAll(str_oldfileName, str_excel[i], "_"); //���ļ��������йַ���ͳͳת���»���!!!!
			}
		}
		//System.out.println("�ɵ��ļ���[" + str_oldfileName + "]"); //
		String str_fileDate_init = ""; //
		String str_fileName_init = ""; //
		if (str_oldfileName.indexOf("/") >= 0) {
			str_fileDate_init = str_oldfileName.substring(0, str_oldfileName.lastIndexOf("/")); //�ļ�������!!��ǰ���Ŀ¼!!!
			str_fileName_init = str_oldfileName.substring(str_oldfileName.lastIndexOf("/") + 1, str_oldfileName.length()); //�ļ�������!
		} else {
			str_fileName_init = str_oldfileName; //
		}
		CommDMO commDMO = new CommDMO();
		String str_convertfileName = ""; //ת���ļ���,����ǰ���һ��N,��Ϊ�˷�ֹ��unix�����������,���ļ���ת������16������!!����ǰ���һΨһ�Ա��!
		String str_clientRealName = null; //��������Ŀͻ�����ʵ����!����Ϊ�˲������ݿ�ʱʹ�õ�!!
		if (str_fileName_init.lastIndexOf(".") > 0) {
			String str_clientRealName_1 = str_fileName_init.substring(0, str_fileName_init.lastIndexOf(".")); //
			String str_clientRealName_2 = str_fileName_init.substring(str_fileName_init.lastIndexOf(".") + 1, str_fileName_init.length()); //
			str_clientRealName = tbUtil.convertHexStringToStr(str_clientRealName_1) + "." + str_clientRealName_2; //
		} else {
			str_clientRealName = tbUtil.convertHexStringToStr(str_fileName_init); //
		}

		String str_fileid = ""; //
		if (_isAddSerialNo) { //�����Ҫ�������!
			str_fileid = new CommDMO().getSequenceNextValByDS(null, "S_PUB_FILEUPLOAD") + "_"; //ȡ�ļ���Ψһ�Ա��!
		}
		str_convertfileName = (_isConvertHex ? "N" : "") + str_fileid; //�����ת������,��ʹ���µ�����
		if (_serverFileNewName == null) { //���û��ָ���µķ���������,��ʹ��ClassFileVO��ָ�����ļ���
			str_convertfileName = str_convertfileName + str_fileName_init; //
		} else { //���ָ���˷��������µ��ļ���,��ʹ���µ�����
			str_convertfileName = str_convertfileName + _serverFileNewName; //
		}

		String str_returnFileName = str_fileDate_init + "/" + str_convertfileName; //���ص��ļ���,����ԭ��������Ŀ¼����ת������ļ���!
		//System.out.println("ʵ�ʴ洢���ļ�����[" + str_returnFileName + "]"); //
		if (tbUtil.getSysOptionBooleanValue("�ϴ������Ƿ�����ݿ�", false)) { //���ϵͳ�����˲���,ָ��������д�����ݿ���,��洢�����ݿ�! ��Ϊ���к���������뽫����д�����ݿ���,���������д������,�ڼ�Ⱥģʽ��,���ļ���ҪѰ����������(һ������,���������������˾Ͳ�����)! ��д�������Ժ�,�Ͳ��ܽ��л����ļ�����������!! ��������ȱ��!!!
			byte[] fileBytes = _fileVO.getByteCodes(); //�ļ����ݵ��ֽ���!!
			//Ҫѹ��һ��! ��ѹ�������и����ջ���!! �������rar���ļ�,����û��ѹ���ռ���,��û��Ҫ����ѹ����! ��ֱ�����!! ���������ϴ�rar�ļ������ޱ�! ��ǰ����Ŀ������һ����С��rar�ļ������ϴ������ز��˵����,����ѹ���ļ���ѭ�������!!
			CompressBytesVO zipedVO = compressBytes(fileBytes); //ѹ������!!
			byte[] zipedBytes = zipedVO.getBytes(); //ѹ��������ֽ�!!

			ArrayList al_sqls = new ArrayList(); //�洢SQL���б����!
			String str_currtime = tbUtil.getCurrTime(); //ȡ�õ�ǰʱ��,��ȷ����!
			String str_64Code = tbUtil.convertBytesTo64Code(zipedBytes); //���ֽ�ת��64λ��!!û����16������,��Ϊ�˸���Լ�ռ�,�ܹؼ�! ��Ϊ��Word/pdf/excel�ļ���������������,����ͳһ��64λ��!! ����64λ��ȫ��Ӣ��,��õ���û�й��ַ�! ������û�е�����,��������SQL����!! ��˵�����64����ϴ��翼�ǵ������ݿ�������! ���������ų�����! he 
			ArrayList al_split = splitStrBySpecialWidth(str_64Code, 10, 4000); //��64λ���ϳ�,ÿһ����4000λ��������,Ȼ��ÿ25��Ϊһ��,�������������ݿ���! �������ݿ��pub_upfiles_b��,��25��,ÿ�п��4000,һ���ܴ�10���ֽ�,һ��1M���ļ�ֻҪ15�����Ҿ͹���! �Ӷ���֤����������!
			//ʹ�ø�����,��֤�����ٵ�˼��ǳ��ؼ�,��Ϊ��ϵ���ݿ��ǻ����д洢��,������ѯҲ�ǻ����е�,��Խ��,��ѯԽ��!! ���ֻ��һ��,���ò��˶��,�ñ�����ϰ���������ǧ����,��ѯ��ǳ���!!! ����洢ʱ��һ����������ߺܶ�!!! ȡ��ʱ�ٽ����е����ݽ�����!
			//��Ҳ������Խ��Խ��,��Ϊ�󲿷��ļ�����300K����(ѹ����),�����̫��,��û���õ�,�����˷�,����Ҫ�ҵ�һ��ƽ���!!

			//�ȴ�������!!!
			InsertSQLBuilder isql_insert = new InsertSQLBuilder("pub_upfiles"); // 
			String str_parentPK = commDMO.getSequenceNextValByDS(null, "S_PUB_UPFILES"); //��������!
			isql_insert.putFieldValue("id", str_parentPK); //��������!
			isql_insert.putFieldValue("filename", str_returnFileName); //Ӧ����Ψһ�Ե�!!!
			isql_insert.putFieldValue("fileinitname", str_clientRealName); //ԭ�����ļ���,����ת����!
			isql_insert.putFieldValue("isziped", zipedVO.isZip() ? "Y" : "N"); //�Ƿ����ѹ����!
			isql_insert.putFieldValue("unzipedbytesize", fileBytes.length); //ѹ��ǰ���ֽڴ�С
			isql_insert.putFieldValue("zipedbytesize", zipedBytes.length); //ѹ������ֽڴ�С
			isql_insert.putFieldValue("zipedscale", ((zipedBytes.length * 100) / fileBytes.length)); //ѹ����,��: ѹ����/ѹ��ǰ%,��ֱ�ӳ���100,������%����!
			isql_insert.putFieldValue("filecodesize", str_64Code.length()); //�ļ�ת��64���Ĵ�С!
			isql_insert.putFieldValue("createtime", str_currtime); //����ʱ��!!���ϴ�ʱ��!!
			al_sqls.add(isql_insert.getSQL()); //

			//�ٴ����ӱ�!!
			for (int i = 0; i < al_split.size(); i++) { //�ܹ��м���!
				String str_pk_b = commDMO.getSequenceNextValByDS(null, "S_PUB_UPFILES_B"); //�ӱ�����м�¼������!!
				String[] str_rowData = (String[]) al_split.get(i); //��һ�м�¼!!!
				for (int j = 0; j < str_rowData.length; j++) { //��������! Ϊ�˷�ֹSQL�������ύʧ��!(�е����ݿ����ղ���̫����SQL),��һ����insert����,���������update����!!!
					if (j == 0) { //����ǵ�һ��,����insert����!!!
						InsertSQLBuilder isql_insert_b = new InsertSQLBuilder("pub_upfiles_b"); //�ӱ�����!!
						isql_insert_b.putFieldValue("id", str_pk_b); //����!!
						isql_insert_b.putFieldValue("fileid", str_parentPK); //���,���������!
						isql_insert_b.putFieldValue("seq", (i + 1)); //���,������!ǧ������!��ѯ�Ǳ���ʹ�� order by seq,�����д�����,�϶����ɵ��ļ�����! ��ʱҲ�Ҳ���!
						isql_insert_b.putFieldValue("createtime", str_currtime); //����ʱ��,��Ϊ��Ҫ���ݸ�ʱ���ֶν�������Ǩ��! ���Ըɴ�Ҳ���ӱ���
						isql_insert_b.putFieldValue("c1", str_rowData[j]); //��һ��!!
						al_sqls.add(isql_insert_b.getSQL(false)); //����SQL
					} else { //�������ͳͳ��update����,������˼��ǳ��ؼ�!!! ����һ�����ݷֳ�25��SQLִ��! ��һ����insert,����Ķ���update,�Ӷ���ֹһ��10����ַ�����SQL�ᱨ��! ��ΪOracle�Ͳ�֧�ֳ���SQLִ��! Mysql���ǿ��Ե�,Ҳ�������!! ����MysqlҲ�Ǻܷ���ĵط�!
						UpdateSQLBuilder isql_update_b = new UpdateSQLBuilder("pub_upfiles_b", "id='" + str_pk_b + "'"); //���޸Ĳ���!!!
						isql_update_b.putFieldValue("c" + (j + 1), str_rowData[j]); //
						al_sqls.add(isql_update_b.getSQL(false)); //����SQL
					}
				}
			}
			commDMO.executeBatchByDS(null, al_sqls, false); ////���������ݿ�!!!! ��ΪSQL̫��,���������̨!! ���Եڶ���������false,һ��Ҫע����һ��,ǧ��Ҫ��ǰ����̨�����sql����ǰûִ��! Ҫ������ʵ�ʲ���,�������׷���ĵط�!!
			WLTLogger.getLogger(this).debug("�ϴ��ļ�[" + str_returnFileName + "][" + str_clientRealName + "]�����ݿ��pub_upfiles�гɹ�!"); //
		} else if (ServerEnvironment.getProperty("FullTextSearchURL") != null) { //���ָ����http�ض���洢url,��ת��!
			String str_newWebUrl = ServerEnvironment.getProperty("FullTextSearchURL");
			System.out.println("BSUtil��Զ��ȫ�ļ����ĵ�ַ[" + str_newWebUrl + "]�ϴ��ļ�[" + str_beginDir + "/" + str_returnFileName + "]...");
			HashMap requestMap = new HashMap();
			requestMap.put("Action", "upload");
			requestMap.put("FileName", str_beginDir + "/" + str_returnFileName); //Ŀ¼���ļ���....
			requestMap.put("ByteCodes", _fileVO.getByteCodes());
			HashMap responseMap = new BSUtil().callWebUrl(str_newWebUrl + "/UploadFileServlet", requestMap); //
			Object obj_return = responseMap.get("ReturnResult"); //
			if (obj_return instanceof Exception) {
				throw (Exception) obj_return;
			}
		} else if (ServerEnvironment.getProperty("FtpFileServerURL") != null && !ServerEnvironment.getProperty("FtpFileServerURL").equals("")) { //���������Ftp����
			String str_fileName = "/" + str_fileDate_init + "/" + str_convertfileName; //FTPֻ�ܴ洢���·����!!!
			new FtpDealUtil().upFile(ServerEnvironment.getProperty("FtpFileServerURL"), _fileVO.getByteCodes(), str_fileName); //
			WLTLogger.getLogger(this).debug("�ϴ��ļ�[" + _fileVO.getClassFileName() + "]��FTP������[" + ServerEnvironment.getProperty("FtpFileServerURL") + "]�ɹ�,�����ļ���[" + str_returnFileName + "]"); //
		} else { //���û�������,����ʹ����ǰ�Ļ���,ֱ����Web��������Ŀ¼��д�ļ�,��������ļ�����! ���д��Ŀ¼��,�����ʹ��lucense���������߽��л����ļ�������!
			//��ǰ�������ļ�������һ��Ŀ¼��,���ֶ�����Ŀ¼,���������������,��������һ���,�ļ�ʵ��̫��,������²���ϵͳ������ļ�����Ҫ�ܳ�ʱ��,���Ժ���������ÿ��һ��Ŀ¼!! Ȼ��ÿ����ļ��ʹ���ÿ���Ŀ¼��! ���и�������Ŀ¼! ����Ǩ���뱸��Ҳ����!
			String str_dir = str_beginDir + "/" + str_fileDate_init; //..
			File filedir = new File(str_dir); //�ϴ��ļ���Ŀ¼..
			if (!filedir.exists()) { //���Ŀ¼������,�򴴽�Ŀ¼! ÿ����ļ��ڵ�һ���ļ�ʱ�Ż���ô�!
				filedir.mkdirs(); // ����Ŀ¼
			}
			//��ǰû�п������ļ��е���������ѽ�������/2016-09-08��
			if (str_convertfileName.contains(File.separator)) {
				//zipEntry.getName() ·���� icheck\20160907\N3142_BBFDB7D6D6B8B1EABFE2.xls
				String currpath = str_dir + File.separator + str_convertfileName.substring(0, str_convertfileName.lastIndexOf(File.separator));
				File df = new File(currpath);
				if (!df.exists()) {
					df.mkdirs();
				}
			}
			String str_newFileNameByDir = str_dir + "/" + str_convertfileName; //�������ļ�����·��!
			File file = new File(str_newFileNameByDir); //
			file.createNewFile(); //�������ļ�
			FileOutputStream out = new FileOutputStream(file, false); //д�ļ���,�Ǹ���ģʽ!! 
			out.write(_fileVO.getByteCodes()); //д�ļ�!!
			out.close(); //�ر���
			WLTLogger.getLogger(this).debug("�ϴ��ļ�[" + _fileVO.getClassFileName() + "]��Ŀ¼[" + str_newFileNameByDir + "]�ɹ�,�����ļ���[" + str_returnFileName + "]"); //
		}
		return str_returnFileName;//
	}

	/**
	 * �ӷ������������ļ�
	 * �ϴ��ļ���һ���ط�,Ҳ����ǰ��һ������!!! 
	 * �������������ط�:һ���Ǳ����е�downloadfile����,��һ����DownLoadFileServlet,�������еķ�����Swing�ؼ��е�ֱ������,��������л�����ֱ�Ӵ�һ��Servlet����
	 * @param _serverdir
	 * @param _serverFileName
	 * @param _isAbsoluteSeverDir
	 * @return
	 * @throws Exception
	 */
	public cn.com.infostrategy.to.common.ClassFileVO downLoadFile(String _serverdir, String fileName, boolean absoluteSeverDir) throws Exception {
		TBUtil tbutil = new TBUtil(); //  
		String str_filepathname = null; //
		if (absoluteSeverDir) {// ����Ǿ���·��
			str_filepathname = _serverdir + "/" + fileName; //
		} else {
			if (_serverdir == null || _serverdir.trim().equals("")) { // ��������·��,��û�ж������·��,��ֱ�ӷ���ϵͳ�ϴ�Ŀ¼�ĸ�Ŀ¼��!
				str_filepathname = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/" + fileName; //
			} else {
				str_filepathname = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + _serverdir + "/" + fileName; //
			}
		}
		str_filepathname = tbutil.replaceAll(str_filepathname, "\\", "/"); //�ļ�ȫ·��
		String str_newWebUrl = ServerEnvironment.getProperty("FullTextSearchURL"); //�����ɼӵ��߼�,������ת��洢����һ̨������
		if (str_newWebUrl != null) { //����������������!
			try {
				System.out.println("BSUtil.downLoadFile()��Զ��ȫ�ļ����ĵ�ַ[" + str_newWebUrl + "/UploadFileServlet]�����ļ�[" + fileName + "]....");
				HashMap requestMap = new HashMap();
				requestMap.put("Action", "download");
				requestMap.put("FileName", str_filepathname); //�ļ�ȫ��!
				HashMap rtMap = callWebUrl(str_newWebUrl + "/UploadFileServlet", requestMap); //
				if ("Y".equals(rtMap.get("isException"))) { //��������쳣
					Exception ex = (Exception) rtMap.get("Exception"); //
					throw new Exception("Զ�������ļ������쳣:" + ex.getMessage(), ex); //
				}
				ClassFileVO cFileVO = new ClassFileVO();
				cFileVO.setByteCodes((byte[]) rtMap.get("ByteCodes")); //���ص��ֽ���!!
				cFileVO.setClassFileName((String) rtMap.get("OldFileName"));
				return cFileVO;
			} catch (Exception e) {
				throw new WLTAppException(e.getMessage());
			}
		} else if (ServerEnvironment.getProperty("FtpFileServerURL") != null && !ServerEnvironment.getProperty("FtpFileServerURL").equals("")) { //���������FTP,���Ftpȡ!!
			//�Ժ󲹳�
			//new FtpDealUtil().downFile(_ip, _port, _user, _pwd, _fileName);  //
			return null; //
		} else { //ֱ�Ӵӱ���ȡ!
			byte[] filecontent = null;
			try {
				filecontent = tbutil.readFromInputStreamToBytes(new FileInputStream(str_filepathname)); //�ļ�����!!!
			} catch (FileNotFoundException ex) { //����ļ�û�ҵ�!��ȥ���ݿ���������!
				filecontent = getFileContentFromDB(fileName, "û�з����ļ�[" + tbutil.replaceHtmlEncode(str_filepathname) + "],ת�����ݿ��в���..."); //�����ݿ���ļ�!!!!!
			}
			cn.com.infostrategy.to.common.ClassFileVO filevo = new cn.com.infostrategy.to.common.ClassFileVO();
			filevo.setClassFileName(fileName);
			filevo.setByteCodes(filecontent);
			return filevo;
		}
	}

	/**
	 * �����ݿ���ȡ����!
	 * @param _fileName
	 * @return
	 * @throws Exception
	 */
	public byte[] getFileContentFromDB(String _fileName, String _preInfo) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		TBUtil tbutil = new TBUtil();
		//sqlע��©��,��Ҫ��Ԥ���봦�� by haoming 20160519
		HashVO[] hvs_1 = commDMO.getHashVoStructByDS(null, "select * from pub_upfiles where filename='" + _fileName + "'", true, false, null, false, false, true).getHashVOs();
		//		HashVO[] hvs_1 = commDMO.getHashVoArrayByDS(null, "select * from pub_upfiles where filename='" + _fileName + "'"); //�ȸ����ļ������
		if (hvs_1 == null || hvs_1.length == 0) {
			throw new WLTAppException(_preInfo + "\r\n���ݿ��pub_upfiles��û��filename='" + tbutil.replaceHtmlEncode(_fileName) + "'���ļ�!!!"); //
		}
		if (hvs_1.length > 1) {
			throw new WLTAppException(_preInfo + "\r\n���ݿ��pub_upfiles�о�Ȼ�ҵ�����filename='" + tbutil.replaceHtmlEncode(_fileName) + "'���ļ�,�������ɴ򿪵Ĳ�����Ҫ�ҵ��ļ�!!!"); //
		}
		String str_id = hvs_1[0].getStringValue("id"); //�ļ���id
		boolean isZiped = hvs_1[0].getBooleanValue("isziped"); //�Ƿ�ѹ��!!
		HashVO[] hvs_2 = commDMO.getHashVoArrayByDS(null, "select * from pub_upfiles_b where fileid='" + str_id + "' order by seq"); //���ļ��ӱ�Ѱ��!!
		StringBuilder sb_64code = new StringBuilder(); //ƴ�ӵ��ַ���!
		String str_text = null; //
		for (int i = 0; i < hvs_2.length; i++) { //����ÿһ��
			for (int j = 0; j < 10; j++) { //����25��
				str_text = hvs_2[i].getStringValue("c" + (j + 1)); //ȡ��ĳһ�е�����!!!
				if (str_text == null || str_text.trim().equals("")) {
					break; //���û������,���ж�!!
				}
				sb_64code.append(str_text); //���ϵ�ƴ��!!!
			}
		}
		byte[] bytes = new TBUtil().convert64CodeToBytes(sb_64code.toString()); ///��64λ��ת��byte[]
		if (isZiped) { //����Ǿ���ѹ����,����н�ѹ! ��Ϊ��Щ�������rar�ļ�,����û����ѹ���Ŀռ���,����ϵͳ����ѹ������!!!
			bytes = decompressBytes(bytes); //��ѹһ��!!!
		}
		return bytes; //����!

	}

	/**
	 * ��һ���ַ������д�Ϸָ�,��ָ���Ŀ��!��������ǳ�����!! ����Ҫ��һ�����ַ������뵽һ�����е�ʱ��ǳ���Ҫ!
	 * ����һ���еĲ���,�嵽��βʱ����! ������һ�������һ����Ҫ���⴦��!!!����ͷ�Ĵ���!!
	 * @param _str
	 * @param _oneColWidth һ�е��ַ�������
	 * @param _oneRowCols һ���м���
	 * @return
	 */
	public ArrayList splitStrBySpecialWidth(String _str, int _oneRowCols, int _oneColWidth) {
		return new TBUtil().split(_str, _oneRowCols, _oneColWidth); //
	}

	/**
	 * ������word�ļ��Ƿ��йؼ���
	 * @param _fileName ����������·��
	 * @param _keywordsΪ�ؼ���
	 * @param _isAllContain �ж��ļ��Ƿ�ͬʱ�������йؼ���
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
			File file = new File(_serverdir + _fileName); // ���ж��ļ��Ƿ����
			if (!file.exists()) {
				return false;
			}
			input = new FileInputStream(file);
			/** end **/
			WordExtractor we = new WordExtractor(input); //������ܱ���!
			String content = we.getText();
			//input.close();
			if (content == null || content.trim().equals("")) {
				return false;
			}
			content = content.toLowerCase();
			boolean isfinded = true; //Ĭ��Ϊ��
			for (int jj = 0; jj < _keywords.length; jj++) { //�������йؼ���
				if (content.indexOf(_keywords[jj].toLowerCase()) < 0) { //�����һ���Բ���,���˳�
					isfinded = false; //
					break; //
				}
			}
			return isfinded; //
		} catch (Exception e) { //
			System.err.println("����Word�ļ�[" + _fileName + "]�����쳣:" + e.getMessage()); //
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
	 * ������excel�ļ��Ƿ��йؼ���
	 * @param _fileName ����������·��
	 * @param _keywords Ϊ�ؼ���
	 * @param _isAllContain �ж��ļ��Ƿ�ͬʱ�������йؼ���
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
			File file = new File(_serverdir + _fileName); // ���ж��ļ��Ƿ����
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
								if (cell.getNumericCellValue() - ((long) cell.getNumericCellValue()) != 0) { // Ŀǰ���㲻׼ȷ���Ժ�ƫ����0.01���ң������Խ���
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
			for (int i = 0; i < _keywords.length; i++) { //��Ĺ�ϵ,�����붼����,ֻҪһ���Ի��Ͼ������˳�!
				if (str_alltext.indexOf(_keywords[i].toLowerCase()) < 0) { //�����һ���Բ���,���˳�
					isFinded = false; //
					break; //
				}
			}
			return isFinded; //
		} catch (Exception e) {
			System.err.println("����Excel�ļ�[" + _fileName + "]�����쳣:" + e.getMessage()); //
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
	 * ������word��excel�ļ��Ƿ��йؼ���
	 * @param fileNames ����������·��
	 * @param _keywords Ϊ�ؼ���
	 * @param isAllContain �ж��ļ��Ƿ�ͬʱ�������йؼ���
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
		if (ServerEnvironment.getProperty("FullTextSearchURL") != null) { //�����ȫ�ļ�����url�������!��ִ���������!!������ҵ��Ŀ�������ĸ��м���ļ��洢Ŀ¼����X:/��ʱ�����ͷǳ���!!!��Ҫ���ļ�Ŀ�����������!!
			String str_fullsearchurl = ServerEnvironment.getProperty("FullTextSearchURL"); //
			logger.info("������ȫ�ļ����ĵ�ַ[" + str_fullsearchurl + "],ϵͳ�����ٴθõ�ַ����ȫ�ļ���..."); //
			try {
				long ll_1 = System.currentTimeMillis(); //
				TBUtil tbUtil = new TBUtil(); ////
				HashMap requestMap = new HashMap(); //
				requestMap.put("subdir", "/officecompfile/"); //��Ŀ¼!!Ĭ����!
				requestMap.put("FileInfo", _filesInfo); //
				requestMap.put("KeyWords", _keywords); //
				requestMap.put("isAllContain", new Boolean(_isAllContain)); //
				byte[] requestBytes = tbUtil.serialize(requestMap); //�����л�!!!
				URL url = new URL(str_fullsearchurl + "/FullTextSearchServlet"); //����Զ�̵�ȫ�ļ�����url!!! http://192.168.0.25:80/hg 
				URLConnection conn = url.openConnection(); //��������
				if (str_fullsearchurl.startsWith("https")) {
					addHttpsParam(conn);
				}
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setUseCaches(false); //
				tbUtil.writeBytesToOutputStream(conn.getOutputStream(), requestBytes); //�������������!!
				byte[] responseBytes = tbUtil.readFromInputStreamToBytes(conn.getInputStream()); //�ӷ������˵õ�����!!
				HashMap responseMap = (HashMap) tbUtil.deserialize(responseBytes); //�����л�!!
				Object obj_return = responseMap.get("ReturnFileIds"); //
				if (obj_return instanceof Exception) {
					throw (Exception) obj_return; //
				} else {
					ArrayList al_return = (ArrayList) obj_return; //
					long ll_2 = System.currentTimeMillis(); //
					logger.info("��ȫ�ļ����ĵ�ַ[" + str_fullsearchurl + "]ȡ���ɹ�,������ļ���[" + _filesInfo.length + "],�����ļ���[" + al_return.size() + "],����ʱ[" + (ll_2 - ll_1) + "]"); //
					return al_return; //����!!!
				}
			} catch (Exception ex) {
				throw new WLTAppException(ex.getMessage()); //
			}
		} else { //���û�ж���!!!
			int li_thread_df = _filesInfo.length / 100; //�ȼ���һ���̴߳���100���ļ�!!�����߳���!
			if (li_thread_df <= 0) { //������һ���߳�!!
				li_thread_df = 1;
			}
			if (li_thread_df >= 20) { //���30���߳�!!!
				li_thread_df = 20;
			}
			int li_onethread_filecount = _filesInfo.length / li_thread_df; //30���߳�ͬʱ��,һ���̴߳�����ٸ��ļ�!!  1000/30=33(10)
			int li_model = _filesInfo.length % li_thread_df; //����
			HashMap mainMap = new HashMap(); //�ȸ����̵߳�Map����ֵ!!!
			if (li_onethread_filecount > 0) {
				for (int i = 0; i < li_thread_df; i++) { //����!!
					mainMap.put("" + i, null); //
				}
			}
			if (li_model != 0) { //���������!!
				mainMap.put("" + li_thread_df, null); //
			}
			logger.debug("һ������[" + li_thread_df + "]���߳�,ÿ���̴߳���[" + li_onethread_filecount + "]���ļ�,���һ�������̴߳���[" + li_model + "]���ļ�"); //
			if (li_onethread_filecount > 0) { //�����߳�!
				for (int i = 0; i < li_thread_df; i++) { //����!!
					String[][] str_fileinfo_item = new String[li_onethread_filecount][2]; //
					System.arraycopy(_filesInfo, li_onethread_filecount * i, str_fileinfo_item, 0, li_onethread_filecount); //����33���ļ�
					new CheckFileThread("" + i, mainMap, str_fileinfo_item, _serverdir, _keywords).start(); //�����߳�!!
				}
			}
			if (li_model != 0) { //���������!!
				String[][] str_fileinfo_item = new String[li_model][2]; //
				System.arraycopy(_filesInfo, li_onethread_filecount * li_thread_df, str_fileinfo_item, 0, li_model); //����33���ļ�
				new CheckFileThread("" + li_thread_df, mainMap, str_fileinfo_item, _serverdir, _keywords).start(); //�����߳�!!
			}

			String[] str_keys = (String[]) mainMap.keySet().toArray(new String[0]); //
			int li_tmp = 1;
			int li_while_count = 1; //
			while (li_tmp == 1) { //����ѭ��!!
				li_while_count++; //
				boolean isAllEnd = true; //�Ƿ�ȫ������!!
				ArrayList al_temp = new ArrayList(); //
				int li_notendthread = 0; //
				for (int i = 0; i < str_keys.length; i++) {
					ArrayList al_item = (ArrayList) mainMap.get(str_keys[i]); //
					if (al_item == null) {
						li_notendthread++; //
						isAllEnd = false; //
					} else {
						al_temp.addAll(al_item); //����!!!
					}
				}
				if (isAllEnd) {
					logger.debug("����[" + str_keys.length + "]���̶߳����������,������[" + al_temp.size() + "]���ļ�!"); //
					return al_temp; //
				} else {
					logger.debug("������Ȼ��[" + li_notendthread + "]�߳�û����(��[" + str_keys.length + "]���߳�),һ������ɨ��!!"); //
					try {
						Thread.currentThread().sleep(500); //ÿ��Ϣһ��
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					if (li_while_count > 1800) { //���15���ӻ�û��Ӧ,�������˳�!!
						return new ArrayList(); //
					}
				}
			}
			return null; //ʵ������Զ�߲�����!
		}
	}

	/**
	 * ����Keyȡ��ĳ����ʼ��������ֵ
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
	 * ȡ����Ŀ������
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
				return allDsVOs[i].getDbtype(); // �������ݿ�����
			}
		}
		return null;
	}

	/**
	 * ���������ܹ�15����,������15����,ÿ�붼��20���߳�,������������æ!!!
	 * ��ǰ�������ķ���������!!!
	 * ����������ڲ���ʱ�᲻�������ܵ�������?? ȫ������Vector�Ĺ�����??������û������HeardThread��ͻ��!
	 * @return
	 */
	public boolean isRealBusiCall() {
		try {
			List vc_calltimes = RemoteCallServlet.callThreadTimeList; //���õ�ʱ��
			int li_area = 10; //15��
			long ll_currtime = System.currentTimeMillis(); //����!!
			long ll_beginTime = ll_currtime - (li_area * 1000); //���15����ۼ�!
			long ll_endTime = ll_currtime - 4000; //���3����ۼ�!

			//�ҳ��������ݵ��ۼ�
			LinkedHashMap map_second = new LinkedHashMap(); //�����!
			Long[] ll_timeList = (Long[]) vc_calltimes.toArray(new Long[0]); //�������������!! ���Ҳ���ʱ������������!!!
			for (int i = 0; i < ll_timeList.length; i++) {
				Long ll_item = ll_timeList[i]; //�������������!!!���ڱ�ɾ���������,���귢����λ!��������!
				if (ll_item >= ll_beginTime && ll_item <= ll_endTime) { //������15���ڵ�!����ǰ�벻��,��Ϊ��ǰ��ܿ��ܺ�С!!!
					Long ll_second = ll_item / 1000; //�������!!
					if (map_second.containsKey(ll_second)) {
						int li_count = (Integer) map_second.get(ll_second); //
						map_second.put(ll_second, li_count + 1); //����
					} else {
						map_second.put(ll_second, 1); //����!!
					}
				}
			}
			int li_mapSize = map_second.size(); //
			if (li_mapSize < 3) { //�����û��15��,����æ,�ڲ���ʱ
				return false; //
			}

			Long[] ll_keys = (Long[]) map_second.keySet().toArray(new Long[0]); //
			for (int i = 1; i < ll_keys.length - 1; i++) {
				int li_count = (Integer) map_second.get(ll_keys[i]); //
				if (li_count < ServerEnvironment.realBusiCallCountOneSecond) { //ֻҪ��һ��С��20���㲻æ,��������15����ÿһ�붼��20������,������������æ!! 
					return false; //��æ!!
				}
			}
			return true; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return false; //
		}
	}

	public String isRealBusiCallHtml() {
		List vc_calltimes = RemoteCallServlet.callThreadTimeList; //���õ�ʱ��
		int li_area = 10; //15��
		long ll_currtime = System.currentTimeMillis(); //����!!
		long ll_beginTime = ll_currtime - (li_area * 1000); //���15����ۼ�!
		long ll_endTime = ll_currtime - 4000; //���3����ۼ�!

		//�ҳ��������ݵ��ۼ�
		LinkedHashMap map_second = new LinkedHashMap(); //�����!
		Long[] ll_timeList = (Long[]) vc_calltimes.toArray(new Long[0]); //�������������!! ���Ҳ���ʱ������������!!!
		for (int i = 0; i < ll_timeList.length; i++) {
			Long ll_item = ll_timeList[i]; //�������������!!!���ڱ�ɾ���������,���귢����λ!��������!
			if (ll_item >= ll_beginTime && ll_item <= ll_endTime) { //������15���ڵ�!����ǰ�벻��,��Ϊ��ǰ��ܿ��ܺ�С!!!
				Long ll_second = ll_item / 1000; //�������!!
				if (map_second.containsKey(ll_second)) {
					int li_count = (Integer) map_second.get(ll_second); //
					map_second.put(ll_second, li_count + 1); //����
				} else {
					map_second.put(ll_second, 1); //����!!
				}
			}
		}

		boolean isRealBusi = true; //
		int li_mapSize = map_second.size(); //
		if (li_mapSize < 3) { //�����û��15��,����æ,�ڲ���ʱ
			isRealBusi = false; //
		}
		Long[] ll_keys = (Long[]) map_second.keySet().toArray(new Long[0]); //
		StringBuilder sb_html = new StringBuilder(); //
		SimpleDateFormat sdf_curr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE); //
		if (isRealBusi) {
			for (int i = 1; i < ll_keys.length - 1; i++) {
				int li_count = (Integer) map_second.get(ll_keys[i]); //
				if (li_count < ServerEnvironment.realBusiCallCountOneSecond) { //ֻҪ��һ��С��20���㲻æ,��������15����ÿһ�붼��20������,������������æ!! 
					isRealBusi = false; //��æ!!
					break;
				}
			}
		}
		sb_html.append("�Ƿ���ĺ�æ=[" + isRealBusi + "],��æ��ʵ�ʼ�����(����ÿ��������" + ServerEnvironment.realBusiCallCountOneSecond + "�������æ):<br>\r\n"); //
		for (int i = 1; i < ll_keys.length - 1; i++) {
			String str_time = sdf_curr.format(new Date(ll_keys[i] * 1000)); //���ϲ���!!
			int li_count = (Integer) map_second.get(ll_keys[i]); //
			sb_html.append("[" + str_time + "]=[" + li_count + "]<br>\r\n"); //
		}
		return sb_html.toString(); //
	}

	/**
	 * ȡ�����а�װ�İ�! ��һ����ƽ̨��,���������Ǹ�����Ʒ����Ŀ��!!!
	 * ���صĽ���ĵ�һ���ǰ���,�ڶ����ǰ���˵��!!!
	 * @return
	 */
	public String[][] getAllInstallPackages(String _subdir) throws Exception {
		ArrayList al_list = new ArrayList(); //
		String str_platFormRootDir = "/cn/com/infostrategy/bs/sysapp/install/"; //ƽ̨�ĸ�·��!!!�ǹ̶���!!
		if (_subdir != null && !_subdir.trim().equals("")) { //�����Ŀ¼��Ϊ��
			if (_subdir.startsWith("/")) { //�����Ŀ¼��С��д��б�ܿ�ͷ��,��Ҫ�õ�!!
				str_platFormRootDir = str_platFormRootDir + _subdir.substring(1, _subdir.length()); //
			} else {
				str_platFormRootDir = str_platFormRootDir + _subdir; //
			}
			if (!str_platFormRootDir.endsWith("/")) { //�������û��/�����!
				str_platFormRootDir = str_platFormRootDir + "/"; //
			}
		}
		al_list.add(new String[] { str_platFormRootDir, "WebPushƽ̨", null }); //�ȼ���ƽ̨!!
		String str_installs = ServerEnvironment.getProperty("INSTALLAPPS"); ////
		if (str_installs != null && !str_installs.trim().equals("")) { //�����!!
			TBUtil tbUtil = new TBUtil(); //
			String[] str_items = tbUtil.split(str_installs, ";"); //
			for (int i = 0; i < str_items.length; i++) {
				int li_pos = str_items[i].indexOf("-"); //
				String str_package = null; //
				String str_packdescr = null; //
				if (li_pos > 0) { //������и�˵��
					str_package = str_items[i].substring(0, li_pos); //�и�ǰ���
					str_packdescr = str_items[i].substring(li_pos + 1, str_items[i].length()); //�иܺ����!
				} else {
					str_package = str_items[i]; //��ֱ���ǰ���!!
					str_packdescr = "δ����"; //
				}
				str_package = tbUtil.replaceAll(str_package, ".", "/"); //������ת�ɷ�б��!!!
				if (!str_package.startsWith("/")) { //���ǰ��û/�����
					str_package = "/" + str_package; //
				}
				if (!str_package.endsWith("/")) { //�������û��/�����!
					str_package = str_package + "/"; //
				}

				if (_subdir != null && !_subdir.trim().equals("")) { //�����Ŀ¼��Ϊ��
					if (_subdir.startsWith("/")) { //�����Ŀ¼��С��д��б�ܿ�ͷ��,��Ҫ�õ�!!
						_subdir = _subdir.substring(1, _subdir.length()); //
					}
					if (!_subdir.endsWith("/")) { //�����Ŀ¼������б�ܽ�β,����!
						_subdir = _subdir + "/"; //
					}
					str_package = str_package + _subdir; //����
				}

				//Ѱ�������װ�������RegisterMenu.xml����û��<app>����!!���Ǻ����¼ӵĹ���,�����ǵ���ν4����Ʒ,��ʵ��������һ����Ʒ�����ֱ���,Ϊ�˼�����ǰ���߼�,������޶ȵļ򻯰�װ!��RegisterMenu.xml��������<app>��!
				StringBuilder sb_apptext = new StringBuilder(); //
				InputStream ins = this.getClass().getResourceAsStream(str_package + "RegisterMenu.xml"); //
				if (ins != null) {
					org.jdom.Document doc = new org.jdom.input.SAXBuilder().build(ins); // ����XML
					java.util.List list_app = doc.getRootElement().getChildren("app"); //Ѱ��app�ӽ��!
					if (list_app != null && list_app.size() > 0) { //����ҵ�!!
						for (int k = 0; k < list_app.size(); k++) { //��������app,�������ж��!
							org.jdom.Element el_app = (org.jdom.Element) list_app.get(k); //
							java.util.List list_attrs = el_app.getAttributes(); //�õ���������!!!
							for (int r = 0; r < list_attrs.size(); r++) { //������������!
								org.jdom.Attribute attrItem = (org.jdom.Attribute) list_attrs.get(r); //ȡ��ĳ������!!
								String str_attr_name = attrItem.getName(); //��������!!
								String str_attr_value = attrItem.getValue(); //����ֵ!!
								sb_apptext.append(str_attr_name + "=" + str_attr_value); //key=value
								if (r < list_attrs.size() - 1) { //4,0123
									sb_apptext.append(";"); //����������һ��,��ӷֺ�!
								}
							}
							if (k < list_app.size() - 1) { //4,0123
								sb_apptext.append("@"); //����������һ��,��ӷֺ�!
							}
						}
					}
				}
				al_list.add(new String[] { str_package, str_packdescr, sb_apptext.toString() }); //��������!
			}
		}
		String[][] str_return = new String[al_list.size()][3]; ////
		for (int i = 0; i < str_return.length; i++) { //����!!!
			str_return[i] = (String[]) al_list.get(i); //
		}
		return str_return; //
	}

	//ȡ��һ������ǵ�ĳЩ�е�����ֵ
	public String[] getOneTreeNodeParentPathItemValue(DefaultMutableTreeNode _node, String[] _itemKeys) {
		Stack[] tempStacks = new Stack[_itemKeys.length]; //ʹ�ö�ջ,ĩ���������,���׽��������,ȡ��ʱ������Ǵ�������!!
		for (int i = 0; i < tempStacks.length; i++) {
			tempStacks[i] = new Stack(); //
		}
		getOneTreeNodeParentPathItemValue(_node, _itemKeys, tempStacks); //�ݹ����!!
		StringBuilder[] sb_returns = new StringBuilder[_itemKeys.length]; //
		for (int i = 0; i < sb_returns.length; i++) {
			sb_returns[i] = new StringBuilder(); //
			if (!tempStacks[i].isEmpty()) {
				sb_returns[i].append(";"); //����еĻ�,��Ҫ�е�һ������!!!
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
		return str_returns; //��������!!
	}

	//�ݹ����
	private void getOneTreeNodeParentPathItemValue(DefaultMutableTreeNode _node, String[] _itemKeys, Stack[] _tempStacks) {
		if (_node.isRoot()) { //������˸����,�ͷ�����!!
			return;
		}
		HashVO hvo = (HashVO) _node.getUserObject(); //
		for (int i = 0; i < _tempStacks.length; i++) {
			_tempStacks[i].push(hvo.getStringValue(_itemKeys[i], "")); //������ȥ!!!
		}
		getOneTreeNodeParentPathItemValue((DefaultMutableTreeNode) _node.getParent(), _itemKeys, _tempStacks); //�ݹ����!!
	}

	public String getTreePathItemValueFromHashVOs(HashVO[] _hvsAll, String str_linkedIDFieldName, String _returnfieldName, String _whereFieldName, String _whereCondition) {
		//long ll_1 = System.currentTimeMillis(); //
		TBUtil tbUtil = new TBUtil(); //
		HashMap[] maps = tbUtil.getHashMapsFromHashVOs(_hvsAll, new String[][] { { str_linkedIDFieldName, _returnfieldName }, { _whereFieldName, str_linkedIDFieldName }, { str_linkedIDFieldName, "$parentpathids" } }); //
		HashMap id_nams_map = maps[0];
		HashMap where_pks_map = maps[1];
		HashMap id_parentids_map = maps[2];
		String str_whereMatch_id = (String) where_pks_map.get(_whereCondition); //������û�У���
		if (str_whereMatch_id != null) { //����ҵ���
			String str_parentids = (String) id_parentids_map.get(str_whereMatch_id); //
			if (str_parentids != null) { //����и��׼�¼
				String[] str_ids = tbUtil.split(str_parentids, ";"); //�ָ�һ��!!
				StringBuilder sb_name = new StringBuilder(); //
				for (int i = 0; i < str_ids.length; i++) {
					if (i != str_ids.length - 1) {
						sb_name.append(id_nams_map.get(str_ids[i]) + "->"); //ƴ����!!
					} else {
						sb_name.append(id_nams_map.get(str_ids[i])); //ƴ����!!
					}
				}
				//long ll_2 = System.currentTimeMillis(); //
				//System.out.println("����һ�Ѻ�ʱ[" + (ll_2 - ll_1) + "]"); //�������ִ��һ��Ҫ15��������(���ұʼǱ���)!!3000�����ļ�¼!!
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

	//�ݹ��㷨
	private void getTreePathVOsByOneRecord(HashVO _leafVO, String _tableName, String _idFieldName, String _parentIdFieldName, Stack _stack) throws Exception {
		_stack.add(_leafVO); //������
		String str_parentIdValue = _leafVO.getStringValue(_parentIdFieldName); //�ҵ����׼�¼Id
		if (str_parentIdValue == null || str_parentIdValue.trim().equals("")) { //������׼�¼IdΪ��,��ֱ���˳�
			return;
		}

		HashVO[] hvsParent = new CommDMO().getHashVoArrayByDS(null, "select * from " + _tableName + " where " + _idFieldName + "='" + str_parentIdValue + "'"); //�ҵ����׼�¼��
		if (hvsParent == null || hvsParent.length <= 0) { //������׼�¼û�ҵ�,���п���ĳ��ԭ������������ʧ����,���絼���ݵ���ɵ�,��ֱ���˳�!
			return;
		}
		getTreePathVOsByOneRecord(hvsParent[0], _tableName, _idFieldName, _parentIdFieldName, _stack); //�ݹ����!!
	}

	/**
	 * ѹ������,Ϊ�˽�ʡ�ڴ濪��,����������ͻ��˲�һ��,��ʹ����һ��CompressBytesVO,������Ϊ�����ͻ���һ��������һ������,��Ὺ���ڴ����!!
	 * һ����˵,rar�ļ���ѹ��ʧ��,һ��5M��rar�ļ�,ѹ����ʱ1��,������ѹ��ʧ�ܵ�! һ��2M��rar�ļ���ʱ0.5��
	 * һ��3M�ķ�rar�ļ�,���ѹ���ɹ�,��ʱ0.4��!
	 * �µķ���ʵ�������¼���:
	 * 1.�ڵ�һλ�ӱ��λ,��̬�����Ƿ�����ѹ��!
	 * 2.̫С�����ݲ�����ѹ��,ȡ�øߵ��Լ۱�
	 * 3.��̬������ʱ�ռ�,��֤�ϴ���ļ��ܿ��ٴ���,��С���ļ��ܽ�ʡ�ڴ�
	 * 4.ѹ����С���ļ�������ѹ��,����rar�ļ�,�Ӷ��������
	 * 5.�ͻ�����������Ĳ�����һ��,�ӱ��λ�ķ���Ҳ��һ��!
	 * @param _initbytes
	 * @return
	 */
	public CompressBytesVO compressBytes(byte[] _initbytes) {
		//long ll_1 = System.currentTimeMillis(); //
		if (_initbytes.length <= 15360) { //���С��15K,��ѹ������ֱ�ӷ���!!!�������ܻ����Щ!��Ϊ��ʱѹ���������Ѳ�����,��Ϊ����С������������紫��ƿ��!!!��֮,ѹ������������CPU���ڴ��,���Ҹ���û��ʲô��ѹ���ռ��,��10K���ļ���ѹҲѹ������ȥ!������ʱ��ѹ����������!!
			//System.out.println("��Ϊ������̫С����ѹ��!"); //
			return new CompressBytesVO(false, _initbytes); //
		}
		//����������ʱѹ���ռ�,��̫����̫С������,Ӧ�������!!!���������Ӱ��ǳ��ؼ�,��������ʱ����ռ�����֮���ҵ�һ�����ѧƽ���!!!��������Ҫô�ǳ���ʱ,Ҫô����ڴ�!! ������!!
		int li_size = _initbytes.length / 20; //������������,��ɶ��ѹ����,��Ϊѹ��10��,��ô�ռ�Ŷ��!!!
		if (li_size < 5120) { //���С��2K,����С��2K,������!!!
			li_size = 5120;
		}
		if (li_size > 51200) { //�������50K,�����Ϊ50K,������!
			li_size = 51200;
		}
		java.util.zip.Deflater compressor = new java.util.zip.Deflater(); //����ѹ����!!
		compressor.setLevel(java.util.zip.Deflater.BEST_COMPRESSION); //����ѹ��ģʽ,������õ�ѹ����ģʽ!����������!!����rar��ģʽһ��!!!
		compressor.setInput(_initbytes); //��������!
		compressor.finish(); //��������ѹ��...
		ByteArrayOutputStream bos = new ByteArrayOutputStream(_initbytes.length); //���������,������ѹ��������ݵ���!
		byte[] buf = new byte[li_size]; //�������������������ѧ����ʱ�����ռ�,�����ÿռ�!
		long li_cyclecount = 0; //������,��ѹ���˶��ٻ�!
		boolean isBreakZip = false; //�Ƿ��ж���ѹ������!!!
		long ll_maxcycleCount = (long) ((long) _initbytes.length * 0.8 / li_size) + 2; //�����ѹ����С��1/5,��ѹ����,��ѹ����Ĵ�С����С��ԭ����С��60%����ѹ������!!
		//System.out.println("��ʱѹ���ռ�=[" + li_size + "],���ѹ������=[" + ll_maxcycleCount + "]!"); //
		while (!compressor.finished()) { //����������Ĳ���,���п��ܻ��� ����ѭ��!!!�γ�������֢״!!!���б�����һ�����������Ĵ���,���˳�ѭ��,������ѹ������,���Ƿ���ԭ��ֵ,��ϵͳ�Ľ�׳�Դ�Ϊ��ǿ!!!!!��Ϊѹ����ʱ����������С֮���и�����ì��!����ѹ���������,��ѹ��̫��ʱ,����ѹ�����˶���,����û������!!������Ҫһ��ƽ�⴦��,����Ǹô���ĳ���!!
			li_cyclecount++; //�ۼ�!
			if (li_cyclecount > ll_maxcycleCount) { //�������30��,��ǿ���˳�,���������ؼ�! ����Щ�ļ��������ѹ������ļ�(����rar�ļ�),��û���ٱ�ѹ���Ŀռ���,��ֱ�ӷ���ԭʼ����!!�������ɼ���ѭ��,�����������һ��!! ���ﲻ�Ǹ����ļ������(����.rar,.zip)���ж��Ƿ���Ҫ����ѹ��!���Ǹ���ʵ�ʿ�ѹ���������ж�,�Ǿ���׼ȷ��!
				//ʵ����������������������е���Ŀ���ҷ���һ��rar�ļ�,��Ϊ�޿�ѹ���ռ�,�����ɷ�������ѹ����ʱ8��,�ͻ��˽�ѹ��Ҫ8��,����ǻ�������,�ò���ʧ!!!
				isBreakZip = true; //ֱ���˳�ѹ������!!!
				break; //
			}
			int count = compressor.deflate(buf); //������ѹ����buf��,һ����˵,�������һ���ⶼ�᷵��1024,��������!!!!! 
			bos.write(buf, 0, count); //������������!!!
		}
		try {
			bos.close(); //�ر���,�������쳣����,�Ӷ����ױ�֤���ܾ��Թر�,���������ڴ��������ס�ļ���״��!!
		} catch (Exception e) {
			e.printStackTrace(); //
		}
		if (isBreakZip) { //�������Ϊǿ���ж�ѹ�����˳���,��ֱ�ӷ���ѹ��ǰ������!
			//System.out.println("��Ϊѹ��̫�鷳(ѹ��" + li_cyclecount + "��),��ѹ��"); //
			return new CompressBytesVO(false, _initbytes); //ֱ�ӷ���ѹ��ǰ������!!
		} else {
			//System.out.println("�ɹ�ѹ����(ѹ��" + li_cyclecount + "��)"); //
			byte[] compressedData = bos.toByteArray(); //��������еõ����ش�С!
			return new CompressBytesVO(true, compressedData); //����!!!
		}
		//long ll_2 = System.currentTimeMillis(); //
		//System.out.println("�������˷���ʱѹ������,�Ƿ��ж�ѹ��=[" + isBreakZip + "],ԭ���ݴ�С[" + _initbytes.length + "],ѹ����[" + li_cyclecount + "]��ѭ��,ѹ�������ݴ�С=[" + returnBytes.length + "],�����ʱ[" + (ll_2 - ll_1) + "]"); //
	}

	/**
	 * ��ѹĳ���ֽ�����,�������������һ��ѹ���ȷǳ��ߵ��ļ�ʱ�ͻ��������ε�ѹ��,��������ѭ��!!����һ��rar�ļ�,���Իᷢ����ʱһЩrar�ļ��޷��ϴ������ص����!!
	 * @param _initbyte
	 * @return
	 */
	public byte[] decompressBytes(byte[] _initbyte) {
		//long ll_1 = System.currentTimeMillis();
		//�����ѹ��ʱ�ռ�
		int li_size = _initbyte.length / 5; //����50%��ѹ����,Ȼ����������20��,�������Ǻ���Ŀռ�!
		if (li_size < 5120) { //���С��2K,����С��2K,������
			li_size = 5120;
		}
		if (li_size > 51200) { //�������50K,�����Ϊ50K��������!
			li_size = 51200;
		}
		java.util.zip.Inflater decompressor = new java.util.zip.Inflater(); //������ѹ��!!!
		decompressor.setInput(_initbyte); //��������
		ByteArrayOutputStream bos = new ByteArrayOutputStream(_initbyte.length);
		byte[] buf = new byte[li_size]; //Ҫ���ſ�,����֮�������ڴ�!! �������ѧ����Ľ��Ϊ׼!��������20KΪ����!!
		long li_cyclecount = 0; //
		try {
			while (!decompressor.finished()) {
				li_cyclecount++;
				int count = decompressor.inflate(buf); //��ѹ
				bos.write(buf, 0, count); //�����
			}
		} catch (DataFormatException e) {
			e.printStackTrace(); //
		}
		try {
			bos.close(); //�ر���,�������쳣����,�Ӷ����ױ�֤���ܾ��Թر�,���������ڴ��������ס�ļ���״��!!
		} catch (IOException e) {
		}
		//System.out.println("��ʱ��ѹ�ռ�=[" + li_size + "],ʵ�ʽ�ѹ����=[" + li_cyclecount + "]!"); //
		byte[] decompressedData = bos.toByteArray();
		//long ll_2 = System.currentTimeMillis(); //
		//System.out.println("�������˽�ѹ���ύ����,����ʱ[" + (ll_2 - ll_1) + "]"); //
		return decompressedData; //
	}

	/**
	 * �����ļ����ͷ���Mime����,����ֵ�ĵ�һ������_response.setContentType(),�ڶ���ֵ����_response.setHeader("Content-Disposition","");  //
	 * ��DownLoadFileServlet��WebCallServlet�ж��õ����,����ͳһ��BSUtil�ж���!!!
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
		} else if (_type.equalsIgnoreCase("docx")) {//���docx���ĵ���ie��Ĭ�ϴ�zipѹ����������
			return new String[] { "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/vnd.openxmlformats-officedocument.wordprocessingml.document; filename=" + _filename }; //
		} else if (_type.equalsIgnoreCase("xls")) {
			return new String[] { "application/vnd.ms-excel", "application/vnd.ms-excel; filename=" + _filename }; //
		} else if (_type.equalsIgnoreCase("xlsx")) {//���xlsx���ĵ���ie��Ĭ�ϴ�zipѹ����������
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
	 * �����ļ����жϸ����ϴ�·�����ļ��Ƿ����
	 * @param filename�ļ���
	 * @return
	 * @throws Exception
	 */
	public boolean fileExist(String filename) throws Exception {

		File file = (new File(ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/upload/" + new String(filename.getBytes("ISO-8859-1"), "GBk")));
		return file.exists();
	}

	/**
	 * �����ļ��������жϸ����ϴ�·�����Ƿ�ȫ���ļ�
	 * @param filenames�ļ��� ����
	 * @return
	 * @throws Exception
	 */
	public boolean filesExist(String[] filenames) throws Exception {
		// ���ܸ����� �ļ������ڵ�ʱ����԰ɲ����ڵ��ļ�������ȥ
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
	 * �����ļ���ɾ�������ϴ����ļ�
	 * @param filename �ļ��� �ļ�������Ϊ���·��
	 * @return
	 * @throws Exception
	 */
	public boolean deleteZipFileName(String filename) throws Exception {
		String str_fileName = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/upload/" + filename; //
		String str_newWebUrl = ServerEnvironment.getProperty("FullTextSearchURL"); //�ʴ��м�Ⱥʱ����ʹ��nfs��ftp,����ֻ��http����һ̨�����ϴ�ȡ...
		if (str_newWebUrl != null) { //����������Ǵ�Զ����ȡ
			System.out.println("��Ϊ������FullTextSearchURL=[" + str_newWebUrl + "],����Ҫת��Զ��ɾ��[" + str_fileName + "]..."); //
			HashMap requestMap = new HashMap();
			requestMap.put("Action", "delete"); //
			requestMap.put("FileName", str_fileName); //������·��ȫ��
			URLConnection conn = new URL(str_newWebUrl + "/UploadFileServlet").openConnection(); //
			if (str_newWebUrl.startsWith("https")) {
				addHttpsParam(conn);
			}
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			//�������������..
			ObjectOutputStream objOut = new ObjectOutputStream(conn.getOutputStream()); //
			objOut.writeObject(requestMap); //
			objOut.flush(); //
			objOut.close(); //
			//�ٵõ���������..
			ObjectInputStream objIns = new ObjectInputStream(conn.getInputStream()); //
			HashMap rtMap = (HashMap) objIns.readObject(); //
			objIns.close(); //

			if ("Y".equals(rtMap.get("isException"))) { //��������쳣
				Exception ex = (Exception) rtMap.get("Exception"); //
				throw new Exception("Զ������ɾ���ļ������쳣", ex); //
			}

			System.out.println("��Զ�̷�����[" + str_newWebUrl + "]ɾ���ļ�[" + str_fileName + "],�����:" + rtMap.get("ReturnResult"));
			return true;
		} else {
			File file = new File(str_fileName); //
			if (file.exists()) {
				boolean del_result = file.delete(); //ɾ���ļ�
				if (del_result) {
					System.out.println("�ӷ�����ɾ���ļ�[" + str_fileName + "]�ɹ���");
				} else {
					System.err.println("�ӷ�����ɾ���ļ�[" + str_fileName + "]ʧ��,���ܲ�����ļ�,����ļ���������");
				}
				return del_result; //
			} else {
				System.out.println("�ӷ�����ɾ���ļ�[" + str_fileName + "]ʱ���ֲ����ڸ��ļ���");
				return false;
			}
		}
	}

	/**
	* ��������
	*/
	public String getZipFileName(String[] filenames) throws Exception {
		String str_zipfileName = filenames[0].substring(0, filenames[0].indexOf(".")) + "_z.jar";
		String[] files = new String[filenames.length];
		String encoding = System.getProperty("file.encoding");
		System.out.println("ϵͳ�����ַ���:" + encoding);
		for (int i = 0; i < filenames.length; i++) {
			files[i] = filenames[i]; // new
			// String(filenames[i].getBytes("ISO-8859-1"),
			// "GBk"); //
			System.out.println("ԭ�ļ���:" + filenames[i] + ",ת���:" + new String(filenames[i].getBytes("GBK"), "UTF-8")); //
		}
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/upload/" + str_zipfileName));
		byte[] buf = new byte[1024];
		for (int i = 0; i < files.length; i++) {
			FileInputStream in = new FileInputStream(ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/upload/" + files[i]); //
			out.putNextEntry(new ZipEntry(files[i])); // ����һ��ZipEntry.
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

		//�������������..
		ObjectOutputStream objOut = new ObjectOutputStream(conn.getOutputStream()); //
		objOut.writeObject(_parMap); //
		objOut.flush(); //
		objOut.close(); //

		//�ٵõ���������..
		ObjectInputStream objIns = new ObjectInputStream(conn.getInputStream()); //
		HashMap rtMap = (HashMap) objIns.readObject(); //
		objIns.close(); //

		return rtMap; //
	}

	/**
	 * https��������
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
			for (int i = 0; i < str_filesInfo.length; i++) { //�����ļ�!!! Ҫ��ɶ��߳�!!!
				if (str_filesInfo[i][0] != null && str_filesInfo[i][1] != null) { //
					if (str_filesInfo[i][1].indexOf(";") != -1) { //
						String[] split_filenames = str_filesInfo[i][1].split(";");
						for (int j = 0; j < split_filenames.length; j++) {
							if (split_filenames[j].toLowerCase().endsWith(".doc") || split_filenames[j].toLowerCase().endsWith(".wps")) {
								if (checkWordFileContainKey(str_serverdir, split_filenames[j], str_keywords, _isAllContain)) {
									list_ids.add(str_filesInfo[i][0]);
									break; // ֻҪ��һ���ļ�����_keywords, ����
								}
							} else if (split_filenames[j].toLowerCase().endsWith(".xls")) {
								if (checkExcelFileContainKey(str_serverdir, split_filenames[j], str_keywords, _isAllContain)) {
									list_ids.add(str_filesInfo[i][0]);
									break; // ֻҪ��һ���ļ�����_keywords, ����
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
			logger.debug("��[" + str_threadNo + "]���̴߳������(��" + mainThreadMap.size() + "���߳�),��������[" + str_filesInfo.length + "]���ļ�,������[" + list_ids.size() + "]�����,��ʱ[" + (ll_2 - ll_1) + "]");
			mainThreadMap.put(str_threadNo, list_ids); //��ѯ�����������̵߳�map������!!
		}
	}

}

/*******************************************************************************
 * $RCSfile: BSUtil.java,v $ $Revision: 1.48 $ $Date: 2012/11/14 10:44:14 $
 * 
 ******************************************************************************/
