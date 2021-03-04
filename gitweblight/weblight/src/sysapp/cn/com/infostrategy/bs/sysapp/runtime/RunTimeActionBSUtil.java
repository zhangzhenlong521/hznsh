package cn.com.infostrategy.bs.sysapp.runtime;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.tools.ToolProvider;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;

public class RunTimeActionBSUtil {

	private TBUtil tbUtil = new TBUtil(); //
	private CommDMO commDMO = null; //

	/**
	 * 编译源码,如果编译成功,同时将源码与Class码都存储到数据库中!!!
	 * @param _actionName 类名/动作名
	 * @param _codeText 源码
	 * @param _isSave 是否同时保存!!
	 * @return
	 * @throws Exception
	 */
	public String compileRunTimeActionCode(String _actionName, String _codeText, boolean _isSave) throws Exception {
		//先保存文件
		String str_path = ServerEnvironment.getProperty("WLTUPLOADFILEDIR"); //路径!! C:/webPushTemp,最好是临时文件
		String str_fileName_java = str_path + "/" + _actionName + ".java"; //Java源文件与编译后的Class文件是在同一目标下的!
		String str_fileName_class = str_path + "/" + _actionName + ".class"; //
		File file_java = new File(str_fileName_java); //
		FileOutputStream fout = new FileOutputStream(file_java, false); //
		fout.write(_codeText.getBytes("GBK")); //肯定是中文的!!!
		fout.close(); //

		long ll_1 = System.currentTimeMillis(); //
		String str_realpath = ServerEnvironment.getProperty("SERVERREALPATH"); //
		if (!str_realpath.endsWith("/")) { //如果不是/结尾
			str_realpath = str_realpath + "/"; //加一个路径,因为在websphere中的部署路径是一个【....installedApps/PC-200910091202Node01Cell/cmb1_war.ear/cmb1.war】的样子!即不是/目录结尾的!!!
		}

		ByteArrayOutputStream byo = new ByteArrayOutputStream(); //不是输出到标准的控制台,而是将返回的结果输出到客户端!!
		int li_compileStatu = ToolProvider.getSystemJavaCompiler().run(null, byo, byo, new String[] { "-extdirs", str_realpath + "/WEB-INF/lib", "-cp", str_realpath + "/WEB-INF/classes", str_fileName_java }); //
		String str_return = null; //
		if (li_compileStatu == 0) { //如果成功了!!!
			File file_class = new File(str_fileName_class); //
			if (_isSave) { //如果同时保存,则还要将Java与Class都插入到表中!
				//计算出往Java源码表【pub_rtaction_java】中插入数据的SQL！！！
				String[] str_textRows = tbUtil.split(_codeText, "\n"); //
				System.out.println("总共[" + str_textRows.length + "]行"); //
				String[] str_sqls_java = getInsertSQLForCode(_actionName, str_textRows); //

				//计算出往Class表中【pub_rtaction_class】中!!将生成的Class文件读取,转换成二进制,然后转成16进制,然后保存进
				FileInputStream fin_class = new FileInputStream(file_class); //
				byte[] bytes_class = new byte[(int) file_class.length()]; //长度!!
				fin_class.read(bytes_class); //将Class文件读入!!!
				fin_class.close(); //关闭流!!!
				String str_classCode = tbUtil.convertBytesToHexString(bytes_class); //转换成16进制码,因为反正看不懂!!!!!
				String[] str_sqls_class = getInsertSQLForClass(_actionName, str_classCode); ////拼成一条条SQL

				//实际插入!!
				getCommDMO().executeUpdateByDS(null, "delete from pub_rtaction_java where actionname='" + _actionName + "'"); //先删除
				getCommDMO().executeBatchByDS(null, str_sqls_java); //往源码表中插入!!
				getCommDMO().executeUpdateByDS(null, "delete from pub_rtaction_class where actionname='" + _actionName + "'"); //先删除
				getCommDMO().executeBatchByDS(null, str_sqls_class); //往Class表中插入!!!!
			}

			//删除java与Class文件!!!!,因为不是关键,所以吃掉异常!!
			try {
				file_java.delete(); //
				file_class.delete(); //
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}
			str_return = "编译成功!"; //
		} else { //如果失败了!!!
			//也删除java与class文件,防止生成大量的垃圾文件!!
			try {
				file_java.delete(); //
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}

			File file_class = new File(str_fileName_class); //
			try {
				if (file_class != null && file_class.exists()) {
					file_java.delete(); //
				}
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}

			//将错误原因返回到客户端
			str_return = byo.toString("GBK"); //错误原因!!
		}

		long ll_2 = System.currentTimeMillis(); //
		System.out.println("编译耗时[" + (ll_2 - ll_1) + "]"); //
		return str_return; //
	}

	/**
	 * 拼成一条条源码表插入的SQL...
	 * @param _actionName
	 * @param _rowTexts
	 * @return
	 * @throws Exception
	 */
	private String[] getInsertSQLForCode(String _actionName, String[] _rowTexts) throws Exception {
		String[] str_sqls = new String[_rowTexts.length]; //
		String str_rowText = null; //
		for (int i = 0; i < str_sqls.length; i++) {
			str_rowText = (_rowTexts[i] == null ? "" : _rowTexts[i]); //
			if (str_rowText.endsWith("\r")) {
				str_rowText = str_rowText.substring(0, str_rowText.length() - 1); //
			}
			InsertSQLBuilder isq_insert = new InsertSQLBuilder("pub_rtaction_java"); //
			isq_insert.putFieldValue("id", getCommDMO().getSequenceNextValByDS(null, "S_PUB_RTACTION_JAVA")); //
			isq_insert.putFieldValue("actionname", _actionName); //
			isq_insert.putFieldValue("javacode", str_rowText); //
			isq_insert.putFieldValue("seq", (i + 1)); //
			str_sqls[i] = isq_insert.getSQL(); //
		}
		return str_sqls;
	}

	/**
	 * 往Class表中插入数据!!!!!
	 * @param _actionName
	 * @param _classCode
	 * @return
	 * @throws Exception
	 */
	private String[] getInsertSQLForClass(String _actionName, String _classCode) throws Exception {
		int li_batchlength = 300; //一行存几个,存得太多,迁移时不方便!!
		int li_alllength = _classCode.length(); //
		int li_rows = li_alllength / li_batchlength; //行数
		int li_mod = li_alllength % li_batchlength; //余数!!!!
		ArrayList al_sqls = new ArrayList(); //列表!!!
		InsertSQLBuilder isq_insert = null;
		for (int i = 0; i < li_rows; i++) {
			isq_insert = new InsertSQLBuilder("pub_rtaction_class"); //
			isq_insert.putFieldValue("id", getCommDMO().getSequenceNextValByDS(null, "S_PUB_RTACTION_CLASS")); //
			isq_insert.putFieldValue("actionname", _actionName); //
			isq_insert.putFieldValue("classcode", _classCode.substring(i * li_batchlength, i * li_batchlength + li_batchlength)); //截取某一段
			isq_insert.putFieldValue("seq", (i + 1)); //
			al_sqls.add(isq_insert.getSQL()); //
		}

		if (li_mod != 0) { //如果有余数,则加上最后一行!!!
			isq_insert = new InsertSQLBuilder("pub_rtaction_class"); //
			isq_insert.putFieldValue("id", getCommDMO().getSequenceNextValByDS(null, "S_PUB_RTACTION_CLASS")); //
			isq_insert.putFieldValue("actionname", _actionName); //
			isq_insert.putFieldValue("classcode", _classCode.substring(li_rows * li_batchlength, li_alllength)); //截取某一段
			isq_insert.putFieldValue("seq", (li_rows + 1)); //
			al_sqls.add(isq_insert.getSQL()); //
		}

		return (String[]) al_sqls.toArray(new String[0]); //
	}

	//加载Java代码!!!
	public HashMap loadRuntimeActionCode(String _actionName) throws Exception {
		String[] str_codes = getCommDMO().getStringArrayFirstColByDS(null, "select javacode from pub_rtaction_java where actionname='" + _actionName + "' order by seq"); //
		if (str_codes == null || str_codes.length == 0) {
			return null;
		}

		int li_classRow = 0; //
		String str_rowText = null; //
		for (int i = 0; i < str_codes.length; i++) {
			str_rowText = (str_codes[i] == null ? "" : str_codes[i]); //
			if (str_rowText.startsWith("public")) { //如果是第一次的public开头,则必须是Class类名
				li_classRow = i; //
				break; //
			}
		}
		System.out.println("行=[" + li_classRow + "]"); //
		HashMap map = new HashMap(); //
		map.put("javacode", str_codes); //
		map.put("beginrow", new Integer(li_classRow + 1)); //
		map.put("endrow", new Integer(str_codes.length - 2)); //
		return map; //
	}

	//根据类名创建一个实例!!!
	public Object getRunTimeActionObject(String _name) throws Exception {
		//long ll_1 = System.currentTimeMillis(); //
		Class cls = getRunTimeActionClass(_name); //
		Object obj = cls.newInstance(); //创建一个实例,只支持无参数构造!!!以后可能遇到有构造参数的!!!
		//long ll_2 = System.currentTimeMillis(); //
		//System.out.println("加载类并创建实例共耗时[" + (ll_2 - ll_1) + "]"); //这个地方耗时一般极小,都是10毫秒内!!!所以不是瓶颈!!假设一个类有10行,2000类也就2万行!!快的很!!!
		return obj; //
	}

	public Class getRunTimeActionClass(String _name) throws Exception {
		return new WLTRuntimeClassLoader(this.getClass().getClassLoader()).myloadClass(_name); //一定要将this.getClass().getClassLoader()作为参数传入,否则得不到!!
	}

	private CommDMO getCommDMO() {
		if (this.commDMO == null) {
			this.commDMO = new CommDMO(); //
		}

		return commDMO;
	}

	public static void main(String[] _args) {
		System.out.println("编译了..."); //
		//JavacTool.create().run(null, System.out, System.err, new String[] { "-cp", "D:/Tomcat6.0.18/webapps/cib3/WEB-INF/classes", "C:/WebPushTemp/BSFilter_171.java" }); //
		//JavacTool.create().run(null, System.out, System.err, new String[] { "-cp", "D:/Tomcat6.0.18/webapps/cib3/WEB-INF/classes", "E:/EclipseProjects/cib3/xch_src/cn/com/weblight/runtime/BSFilter_171.java" }); //
	}
}
