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
	 * ����Դ��,�������ɹ�,ͬʱ��Դ����Class�붼�洢�����ݿ���!!!
	 * @param _actionName ����/������
	 * @param _codeText Դ��
	 * @param _isSave �Ƿ�ͬʱ����!!
	 * @return
	 * @throws Exception
	 */
	public String compileRunTimeActionCode(String _actionName, String _codeText, boolean _isSave) throws Exception {
		//�ȱ����ļ�
		String str_path = ServerEnvironment.getProperty("WLTUPLOADFILEDIR"); //·��!! C:/webPushTemp,�������ʱ�ļ�
		String str_fileName_java = str_path + "/" + _actionName + ".java"; //JavaԴ�ļ��������Class�ļ�����ͬһĿ���µ�!
		String str_fileName_class = str_path + "/" + _actionName + ".class"; //
		File file_java = new File(str_fileName_java); //
		FileOutputStream fout = new FileOutputStream(file_java, false); //
		fout.write(_codeText.getBytes("GBK")); //�϶������ĵ�!!!
		fout.close(); //

		long ll_1 = System.currentTimeMillis(); //
		String str_realpath = ServerEnvironment.getProperty("SERVERREALPATH"); //
		if (!str_realpath.endsWith("/")) { //�������/��β
			str_realpath = str_realpath + "/"; //��һ��·��,��Ϊ��websphere�еĲ���·����һ����....installedApps/PC-200910091202Node01Cell/cmb1_war.ear/cmb1.war��������!������/Ŀ¼��β��!!!
		}

		ByteArrayOutputStream byo = new ByteArrayOutputStream(); //�����������׼�Ŀ���̨,���ǽ����صĽ��������ͻ���!!
		int li_compileStatu = ToolProvider.getSystemJavaCompiler().run(null, byo, byo, new String[] { "-extdirs", str_realpath + "/WEB-INF/lib", "-cp", str_realpath + "/WEB-INF/classes", str_fileName_java }); //
		String str_return = null; //
		if (li_compileStatu == 0) { //����ɹ���!!!
			File file_class = new File(str_fileName_class); //
			if (_isSave) { //���ͬʱ����,��Ҫ��Java��Class�����뵽����!
				//�������JavaԴ���pub_rtaction_java���в������ݵ�SQL������
				String[] str_textRows = tbUtil.split(_codeText, "\n"); //
				System.out.println("�ܹ�[" + str_textRows.length + "]��"); //
				String[] str_sqls_java = getInsertSQLForCode(_actionName, str_textRows); //

				//�������Class���С�pub_rtaction_class����!!�����ɵ�Class�ļ���ȡ,ת���ɶ�����,Ȼ��ת��16����,Ȼ�󱣴��
				FileInputStream fin_class = new FileInputStream(file_class); //
				byte[] bytes_class = new byte[(int) file_class.length()]; //����!!
				fin_class.read(bytes_class); //��Class�ļ�����!!!
				fin_class.close(); //�ر���!!!
				String str_classCode = tbUtil.convertBytesToHexString(bytes_class); //ת����16������,��Ϊ����������!!!!!
				String[] str_sqls_class = getInsertSQLForClass(_actionName, str_classCode); ////ƴ��һ����SQL

				//ʵ�ʲ���!!
				getCommDMO().executeUpdateByDS(null, "delete from pub_rtaction_java where actionname='" + _actionName + "'"); //��ɾ��
				getCommDMO().executeBatchByDS(null, str_sqls_java); //��Դ����в���!!
				getCommDMO().executeUpdateByDS(null, "delete from pub_rtaction_class where actionname='" + _actionName + "'"); //��ɾ��
				getCommDMO().executeBatchByDS(null, str_sqls_class); //��Class���в���!!!!
			}

			//ɾ��java��Class�ļ�!!!!,��Ϊ���ǹؼ�,���ԳԵ��쳣!!
			try {
				file_java.delete(); //
				file_class.delete(); //
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}
			str_return = "����ɹ�!"; //
		} else { //���ʧ����!!!
			//Ҳɾ��java��class�ļ�,��ֹ���ɴ����������ļ�!!
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

			//������ԭ�򷵻ص��ͻ���
			str_return = byo.toString("GBK"); //����ԭ��!!
		}

		long ll_2 = System.currentTimeMillis(); //
		System.out.println("�����ʱ[" + (ll_2 - ll_1) + "]"); //
		return str_return; //
	}

	/**
	 * ƴ��һ����Դ�������SQL...
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
	 * ��Class���в�������!!!!!
	 * @param _actionName
	 * @param _classCode
	 * @return
	 * @throws Exception
	 */
	private String[] getInsertSQLForClass(String _actionName, String _classCode) throws Exception {
		int li_batchlength = 300; //һ�д漸��,���̫��,Ǩ��ʱ������!!
		int li_alllength = _classCode.length(); //
		int li_rows = li_alllength / li_batchlength; //����
		int li_mod = li_alllength % li_batchlength; //����!!!!
		ArrayList al_sqls = new ArrayList(); //�б�!!!
		InsertSQLBuilder isq_insert = null;
		for (int i = 0; i < li_rows; i++) {
			isq_insert = new InsertSQLBuilder("pub_rtaction_class"); //
			isq_insert.putFieldValue("id", getCommDMO().getSequenceNextValByDS(null, "S_PUB_RTACTION_CLASS")); //
			isq_insert.putFieldValue("actionname", _actionName); //
			isq_insert.putFieldValue("classcode", _classCode.substring(i * li_batchlength, i * li_batchlength + li_batchlength)); //��ȡĳһ��
			isq_insert.putFieldValue("seq", (i + 1)); //
			al_sqls.add(isq_insert.getSQL()); //
		}

		if (li_mod != 0) { //���������,��������һ��!!!
			isq_insert = new InsertSQLBuilder("pub_rtaction_class"); //
			isq_insert.putFieldValue("id", getCommDMO().getSequenceNextValByDS(null, "S_PUB_RTACTION_CLASS")); //
			isq_insert.putFieldValue("actionname", _actionName); //
			isq_insert.putFieldValue("classcode", _classCode.substring(li_rows * li_batchlength, li_alllength)); //��ȡĳһ��
			isq_insert.putFieldValue("seq", (li_rows + 1)); //
			al_sqls.add(isq_insert.getSQL()); //
		}

		return (String[]) al_sqls.toArray(new String[0]); //
	}

	//����Java����!!!
	public HashMap loadRuntimeActionCode(String _actionName) throws Exception {
		String[] str_codes = getCommDMO().getStringArrayFirstColByDS(null, "select javacode from pub_rtaction_java where actionname='" + _actionName + "' order by seq"); //
		if (str_codes == null || str_codes.length == 0) {
			return null;
		}

		int li_classRow = 0; //
		String str_rowText = null; //
		for (int i = 0; i < str_codes.length; i++) {
			str_rowText = (str_codes[i] == null ? "" : str_codes[i]); //
			if (str_rowText.startsWith("public")) { //����ǵ�һ�ε�public��ͷ,�������Class����
				li_classRow = i; //
				break; //
			}
		}
		System.out.println("��=[" + li_classRow + "]"); //
		HashMap map = new HashMap(); //
		map.put("javacode", str_codes); //
		map.put("beginrow", new Integer(li_classRow + 1)); //
		map.put("endrow", new Integer(str_codes.length - 2)); //
		return map; //
	}

	//������������һ��ʵ��!!!
	public Object getRunTimeActionObject(String _name) throws Exception {
		//long ll_1 = System.currentTimeMillis(); //
		Class cls = getRunTimeActionClass(_name); //
		Object obj = cls.newInstance(); //����һ��ʵ��,ֻ֧���޲�������!!!�Ժ���������й��������!!!
		//long ll_2 = System.currentTimeMillis(); //
		//System.out.println("�����ಢ����ʵ������ʱ[" + (ll_2 - ll_1) + "]"); //����ط���ʱһ�㼫С,����10������!!!���Բ���ƿ��!!����һ������10��,2000��Ҳ��2����!!��ĺ�!!!
		return obj; //
	}

	public Class getRunTimeActionClass(String _name) throws Exception {
		return new WLTRuntimeClassLoader(this.getClass().getClassLoader()).myloadClass(_name); //һ��Ҫ��this.getClass().getClassLoader()��Ϊ��������,����ò���!!
	}

	private CommDMO getCommDMO() {
		if (this.commDMO == null) {
			this.commDMO = new CommDMO(); //
		}

		return commDMO;
	}

	public static void main(String[] _args) {
		System.out.println("������..."); //
		//JavacTool.create().run(null, System.out, System.err, new String[] { "-cp", "D:/Tomcat6.0.18/webapps/cib3/WEB-INF/classes", "C:/WebPushTemp/BSFilter_171.java" }); //
		//JavacTool.create().run(null, System.out, System.err, new String[] { "-cp", "D:/Tomcat6.0.18/webapps/cib3/WEB-INF/classes", "E:/EclipseProjects/cib3/xch_src/cn/com/weblight/runtime/BSFilter_171.java" }); //
	}
}
