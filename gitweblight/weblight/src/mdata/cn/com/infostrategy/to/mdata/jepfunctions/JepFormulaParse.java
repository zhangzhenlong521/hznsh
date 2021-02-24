/**************************************************************************
 * $RCSfile: JepFormulaParse.java,v $  $Revision: 1.21 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/
package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Vector;

import org.nfunk.jep.JEP;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.mdata.SystemOutPrintln;
import cn.com.infostrategy.ui.mdata.ThrowException;
import cn.com.infostrategy.ui.mdata.formatcomp.GetUserRole;

/**
 * ��ʽ�������ĸ���!!Ҳ�ǳ�����!!!�����ﶨ���ҹ��õ�ע���һЩ����!!
 * 
 * @author xch
 * 
 */
public abstract class JepFormulaParse {

	protected JEP parser = new JEP();

	protected abstract byte getJepType(); // Jep������!!!

	//��׼���ĺ���,��ֻҪ������Ĺ�ʽ����!����ά����ľ���ֵ��������Ҫ�õ�!
	protected void initStandardFunction() {
		parser.addStandardFunctions(); // �������б�׼����!!
		parser.addStandardConstants(); // �������б���!!!
	}

	/**
	 * ��׼�ĳ��õļ��㺯��!!
	 */
	protected void initNormalFunction() {
		parser.addStandardFunctions(); // �������б�׼����!!
		parser.addStandardConstants(); // �������б���!!!

		// ע�����к���
		parser.addFunction("getColValue", new GetColValue(getJepType())); // ����getColValue()����
		parser.addFunction("getSQLValue", new GetSQLValue(getJepType())); // ����getColValue()����

		parser.addFunction("getTreePathColValue", new GetTreePathTableName(getJepType())); // ����getColValue()����

		parser.addFunction("getColValueByDS", new GetColValueByDS(getJepType())); // ����getColValue()����,��������Դȡ��!!!!
		parser.addFunction("getFnValue", new GetFnValue(getJepType())); // ��ô洢����ֵ,�����õ�!!!
		parser.addFunction("getClassValue", new GetClassValue(getJepType())); // ͨ����������������
		parser.addFunction("DecideMessageBox", new DecideMessageBox()); // ����getLoginCode()����

		parser.addFunction("getLoginUserId", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_ID)); // ����getLoginCode()����
		parser.addFunction("getLoginUserCode", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_CODE)); // ����getLoginCode()����
		parser.addFunction("getLoginUserName", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_NAME)); // ����getLoginName()����
		parser.addFunction("getLoginUserDeptQSql", new GetLoginUserSql(getJepType(), "corp"));
		parser.addFunction("getLoginUserRoleQSql", new GetLoginUserSql(getJepType(), "role"));
		parser.addFunction("getLoginUserDeptId", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPTID)); // ��¼��Ա������������
		parser.addFunction("getLoginUserDeptName", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPTNAME)); // ��¼��Ա������������
		parser.addFunction("getLoginUserDeptType", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPT_CORPTYPE)); // ��¼��Ա������������

		parser.addFunction("getCorpIdByType", new GetCorpIdByType(getJepType())); //���ݻ�������ȡ�û���ID,��������Ϊ����
		parser.addFunction("getLoginUserParentCorpItemValueByType", new GetLoginUserParentCorpItemValueByType(getJepType())); //ȡ�õ�¼��Ա�����������������ϼ������л�������Ϊָ�����͵Ļ���!!	

		parser.addFunction("getBusitypeCode", new GetBusitypeCode()); // �õ���Ź���
		//ĳЩ�ط��õ����·�������ע���ˣ����ܲ�ͨ�ˣ��Ȳ�ע���ˣ��Ժ��кõİ취�ٸģ�
		parser.addFunction("getLoginUser_DeptId", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPTID)); // ��¼��Ա������������
		parser.addFunction("getLoginUser_DeptCode", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPTCODE)); // ��¼��Ա�������ű���
		parser.addFunction("getLoginUser_DeptName", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPTNAME)); // ��¼��Ա������������
		parser.addFunction("getLoginUser_DeptType", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPT_CORPTYPE)); // ��¼��Ա������������

		parser.addFunction("getLoginUser_Dept_BLZHBM", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPT_BLZHBM)); // ��¼��Ա��������֮�������в���
		parser.addFunction("getLoginUser_Dept_BLZHBM_NAME", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPT_BLZHBM_NAME)); // ��¼��Ա��������֮�������в���

		parser.addFunction("getLoginUser_Dept_FENGH", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPT_BLFENGH)); // ��¼��Ա��������֮��������
		parser.addFunction("getLoginUser_Dept_FENGH_NAME", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPT_BLFENGH_NAME)); // ��¼��Ա��������֮��������

		parser.addFunction("getLoginUser_Dept_FENGHBM", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPT_BLFENGHBM)); // ��¼��Ա��������֮�������в���
		parser.addFunction("getLoginUser_Dept_FENGHBM_NAME", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPT_BLFENGHBM_NAME)); // ��¼��Ա��������֮�������в���

		parser.addFunction("getLoginUser_Dept_ZHIH", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPT_BLZHIH)); // ��¼��Ա��������֮����֧��
		parser.addFunction("getLoginUser_Dept_ZHIH_NAME", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPT_BLZHIH_NAME)); // ��¼��Ա��������֮����֧��

		parser.addFunction("getLoginUser_Dept_SHIYB", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPT_BLSHIYB)); // ��¼��Ա��������֮������ҵ��
		parser.addFunction("getLoginUser_Dept_SHIYB_NAME", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPT_BLSHIYB_NAME)); // ��¼��Ա��������֮������ҵ��

		parser.addFunction("getLoginUser_Dept_SHIYBFB", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPT_BLSHIYBFB)); // ��¼��Ա��������֮������ҵ���ֲ�
		parser.addFunction("getLoginUser_Dept_SHIYBFB_NAME", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPT_BLSHIYBFB_NAME)); // ��¼��Ա��������֮������ҵ���ֲ�

		parser.addFunction("getLoginUser_PostId", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_POSTID)); // ��¼��Ա������λID
		parser.addFunction("getLoginUser_PostCode", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_POSTCODE)); // ��¼��Ա������λCode
		parser.addFunction("getLoginUser_PostName", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_POSTNAME)); // ��¼��Ա������λName

		parser.addFunction("getLoginUserCorpId", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_CORPID)); //��������֮�������������/2012-06-06��
		parser.addFunction("getLoginUserCorpName", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_CORPNAME)); //��������֮������������ Gwang 2016-08-30
		
		parser.addFunction("getLoginUserBelongDept", new GetLoginUserBelongDept()); // ��¼��Ա��������Ϊ���л��Ƿ���
		parser.addFunction("getLoginUser_HeadFhDept", new GetLoginUser_HeadFhDept()); // �õ���������
		parser.addFunction("getLoginUser_HeadZhDept", new GetLoginUser_HeadZhDept()); // �õ�֧�����ƻ���в�������
		parser.addFunction("getLoginUser_BranchDept", new GetLoginUser_BranchDept()); // �õ�֧������ǰ��λ ���з��ؿ��ַ�

		parser.addFunction("getLoginUser_Dept", new GetLoginUser_Dept()); // 
		///////////////////////////////

		parser.addFunction("getLoginUserDeptLinkCode", new GetLoginUserDeptLinkCode()); // ��¼��Ա�������Ų㼶����!!!������Ҫ!!
		parser.addFunction("getLoginUserPostCode", new GetLoginUserPostCode()); // ��¼��Ա������λ����
		//parser.addFunction("getLoginUserInfo", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_NAME)); // ����getLoginUserInfo()��

		parser.addFunction("getSequence", new GetSequence()); // �õ����к���
		parser.addFunction("getFillZeroNum", new GetFillZeroNum()); // �õ�������ֵ
		parser.addFunction("getUserRole", new GetUserRole()); // �õ�������ֵ
		parser.addFunction("getDateDifference", new GetDateDifference()); //�õ�����ʱ���

		parser.addFunction("getCurrDBDate", new GetCurrentDBDate(getJepType())); // ����getCurrDate()����
		parser.addFunction("getCurrDBTime", new GetCurrentDBTime(getJepType())); // ����getCurrDate()����
		parser.addFunction("getCurrDate", new GetCurrentTime(GetCurrentTime.DATE)); // ����getCurrDate()����
		parser.addFunction("getCurrTime", new GetCurrentTime(GetCurrentTime.TIME)); // ����getCurrTime()����
		parser.addFunction("getYearByDateTime", new GetTimePart(GetTimePart.YEAY)); //����ʱ��ȡ�����
		parser.addFunction("getSeasonByDateTime", new GetTimePart(GetTimePart.SEASON)); //����ʱ��ȡ�ü���
		parser.addFunction("getMonthByDateTime", new GetTimePart(GetTimePart.MONTH)); //����ʱ��ȡ���¶�
		parser.addFunction("getDateByDateTime", new GetTimePart(GetTimePart.DATE)); //����ʱ��ȡ������
		parser.addFunction("afterDate", new AfterDate()); // ����getCurrDate()����
		parser.addFunction("toString", new ToString()); // ����toString����
		parser.addFunction("toNumber", new ToNumber()); // ����toNumber����
		parser.addFunction("getMapStrItemValue", new GetMapStrItemValue()); //�ӹ�ϣ�����͵��ַ�����ȡ�ö�Ӧkey��value
		parser.addFunction("indexOf", new IndexOf()); // ���������ַ����и������ַ����ַ���
		parser.addFunction("subString", new SubString());// ��ȡ�ַ���
		parser.addFunction("fillBlank", new FillBlank());//���ո�
		parser.addFunction("getCaseValue", new GetCaseValue());//Ϊ��ʡȥif/else���鷳,һ���㶨!!
		parser.addFunction("length", new Length());// ��ȡ�ַ���
		parser.addFunction("replaceall", new ReplaceAll());// �滻�ַ����е�����
		parser.addFunction("replaceallandsubString", new ReplaceAllAndSubString());// �滻�ַ����е����ݲ���ȡ�ַ���
		parser.addFunction("getRandom", new GetRandom()); //ȡ������� ����+���ʮλ��
		parser.addFunction("getParAsMap", new GetParAsMap()); //ȡ������� ����+���ʮλ��

		parser.addFunction("throwException", new ThrowException()); // �¼��󶨺���
		parser.addFunction("systemOutPrintln", new SystemOutPrintln()); //��ӡһ�λ�  
		parser.addFunction("getDecimalFormatNumber", new GetDecimalFormatNumber()); //����С���ĸ�ʽ
	}

	/**
	 * ������ִ�й�ʽ!!
	 * 
	 * @param _expr,�����Ŀ�ִ�е��﷨!!
	 * @return
	 */
	public final Object execFormula(String _expr) {
		try {
			parser.parseExpression(_expr); // ִ�й�ʽ
			return parser.getValueAsObject(); //
		} catch (Throwable ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * ���պ�����������˵���������ķ�ʽ����Vector
	 * 
	 * @return
	 */
	public static Vector getFunctionDetail() {
		Vector vec_functions = new Vector(); //
		vec_functions.add(new String[] { "getColValue(\"tablename\",\"fieldname\",\"con_field\",\"con_value\")", "����tablename,con_field,con_value����ѯ��Ӧ�����ݣ�����fielname��value", "getColValue(\"pub_menu\",\"LOCALNAME\",\"ID\",\"22\") = \"�ͻ���ѯ\"" });
		vec_functions.add(new String[] { "getColValue2(\"tablename\",\"fieldname\",\"con_field\",\"itemKey\")",
				"���ݲ���ƴ��select {fieldname} from {tablename} where {con_field}=getItemValue({itemKey}),����getColValue�������������Ĳ�����itemkey�����ƶ�����ֵ,\"getColValue2(\"pub_user\",\"name\",\"id\",\"createuser\") = \"����\"" });
		vec_functions.add(new String[] { "getSQLValue(\"����\",\"select * from pub_user\",\"null\")", "����SQL��䣬��ѯ��Ӧ�����ݣ������ַ�������ƴ���ַ�������һ����������������򷵻�ƴ���ַ�����������ַ����򷵻�һ���ַ���,���һ�����������null��ִ�еڶ���������SQL��䣬������ǣ�����Ҫ�ѵڶ�������ƴ��in����������������������in������������ݣ�ע�ⲻҪ��()",
				"getSQLValue(\"�ַ���\",\"select * from pub_user\",\"null\") = \"�ͻ���ѯ\"" });

		vec_functions
				.add(new String[] { "GetColValueByDS(\"datasourcename\",\"tablename\",\"fieldname\",\"con_field\",\"con_value\")", "��ָ������Դ��,����tablename,con_field,con_value����ѯ��Ӧ�����ݣ�����fielname��value", "GetColValueByDS(\"datasource_default\",\"pub_menu\",\"LOCALNAME\",\"ID\",\"22\") = \"�ͻ���ѯ\"" });

		vec_functions.add(new String[] { "getFnValue(\"fnname\",\"pa_1\",����)", "��ô洢������ִ�н���������Ĳ���������ȷ�������ؽ��ΪString", "" });

		vec_functions.add(new String[] { "if(1==1,\"aaa\",\"bbb\")", "������������", "if(1==1,\"aaa\",\"bbb\"),�򷵻�\"aaa\"" });
		vec_functions.add(new String[] { "DecideMessageBox(\"�б�\",\"templetcode\",\"��ѡ��һ����Ա\")", "����ģ�����ж��Ƿ���Ҫ���������,һ������������", "DecideMessageBox(\"�б�\",\"pub_user\",\"��ѡ��һ����Ա\")" });
		vec_functions.add(new String[] { "showMsg(\"��ʾ��\")", "����һ�仰", "showMsg(\"aaa\")" });
		vec_functions.add(new String[] { "isSelected(getList(\"pub_corp_dept\"))", "�жϵ�ǰ����Ƿ�ѡ�У�����Ture��False", "" });
		vec_functions.add(new String[] { "throwException(\"��ѡ��һ����Ա\")", "�׳��쳣", "" });
		vec_functions.add(new String[] { "systemOutPrintln(\"��ӡһ�λ�\")", "��ӡһ�λ�", "" });

		vec_functions.add(new String[] { "getClassValue(\"cn.com.infostrategy.to.mdata.jepfunctions.IClassJepFormulaParseIFC\",\"aa\",\"bb\")", "ͨ������������", "cn.com.infostrategy.to.mdata.jepfunctions.IClassJepFormulaParseIFC\",\"aa\",\"bb\")" });
		vec_functions.add(new String[] { "getClientEnvironmentPut(\"string\")", "�õ��ͻ���PUT��ֵ,����Ϊ�ַ�����ʽ", "\")" });

		vec_functions.add(new String[] { "getLoginUserId()", "���ص�ǰ��¼�û�ID", "getLoginUserId()= \"1\"" });
		vec_functions.add(new String[] { "getLoginUserCode()", "���ص�ǰ��¼�û�CODE", "getLoginUserCode()= \"ADMIN\"" });
		vec_functions.add(new String[] { "getLoginUserName()", "���ص�ǰ��¼�û�", "getLoginUserName()= \"ADMIN\"" });

		vec_functions.add(new String[] { "getLoginUserDeptId()", "��¼�û���������ID", "getLoginUser_DeptId()= \"126\"" }); //
		vec_functions.add(new String[] { "getLoginUserDeptName()", "��¼�û�������������", "getLoginUser_DeptName()= \"�Ͼ�����\"" }); //
		vec_functions.add(new String[] { "getLoginUserDeptType()", "��¼�û�������������", "getLoginUser_DeptType()= \"����\"" }); //

		vec_functions.add(new String[] { "getLoginUserCorpId()", "��¼�û���������֮��������ID", "getLoginUserCorpId()= \"126\"" }); //
		vec_functions.add(new String[] { "getLoginUserCorpName()", "��¼�û���������֮��������Name", "getLoginUserCorpName()= \"���Ź�˾\"" }); //
		vec_functions.add(new String[] { "getLoginUserDeptQSql()", "��¼�û���������·��ƴ��likeor��sql", "getLoginUserDeptQSql(\"receivercorps\",\"likeor\")" }); //
		vec_functions.add(new String[] { "getLoginUserRoleQSql()", "��¼�û�������ɫƴ��likeor��sql", "getLoginUserRoleQSql(\"receiverroles\",\"likeor\")" }); //

		vec_functions.add(new String[] { "getCorpIdByType(\"����\")", "���ݻ�������ȡ�û���id", "getCorpIdByType(\"����\")= \"12356\"" }); //
		vec_functions.add(new String[] { "getLoginUserParentCorpItemValueByType(\"һ������/���в���\",\"����\",\"name\")", "ȡ�õ�¼��Ա�����ϼ�����������Ϊָ�����͵Ļ���,��/���ֱ�ʾ���ҵ�˭����˭,�ڶ���������ʾ��ȫ����,�������Ƿ�����", "getLoginUserParentCorpIdByType(\"һ������/���в���\",\"����\",\"name\")= \"�Ϻ�����\"" }); //

		vec_functions.add(new String[] { "getBusitypeCode(\"getLoginUser_HeadFhDept()\",\"getYearByDateTime(getCurrDate())\",\"����\")", "ͨ��3�������õ�����", "getBusitypeCode(\"getLoginUser_HeadFhDept()\",\"getYearByDateTime(getCurrDate())\",\"����\")" });

		//		vec_functions.add(new String[] { "getLoginUser_Dept_BLZHBM()", "��¼�û���������֮�������в���ID", "getLoginUser_Dept_BLZHBM()= \"126\"" }); //
		//		vec_functions.add(new String[] { "getLoginUser_Dept_BLZHBM_NAME()", "��¼�û���������֮�������в�������", "getLoginUser_Dept_BLZHBM_NAME()= \"���з��ɺϹ沿\"" }); //
		//
		//		vec_functions.add(new String[] { "getLoginUser_Dept_FENGH()", "��¼�û���������֮��������ID", "getLoginUser_Dept_FENGH()= \"3463\"" }); //
		//		vec_functions.add(new String[] { "getLoginUser_Dept_FENGH_NAME()", "��¼�û���������֮������������", "getLoginUser_Dept_FENGH_NAME()= \"�Ϻ�����\"" }); //
		//
		//		vec_functions.add(new String[] { "getLoginUser_Dept_FENGHBM()", "��¼�û���������֮�������в���ID", "getLoginUser_Dept_FENGHBM()= \"45622\"" }); //
		//		vec_functions.add(new String[] { "getLoginUser_Dept_FENGHBM_NAME()", "��¼�û���������֮�������в�������", "getLoginUserDeptId()= \"�Ϻ����мƻ�����\"" }); //
		//
		//		vec_functions.add(new String[] { "getLoginUser_Dept_ZHIH()", "��¼�û���������֮����֧��ID", "getLoginUser_Dept_ZHIH()= \"32786\"" }); //
		//		vec_functions.add(new String[] { "getLoginUser_Dept_ZHIH_NAME()", "��¼�û���������֮����֧������", "getLoginUser_Dept_ZHIH_NAME()= \"�Ϻ����������֧��\"" }); //
		//
		//		vec_functions.add(new String[] { "getLoginUser_Dept_SHIYB()", "��¼�û���������֮������ҵ��ID", "getLoginUser_Dept_SHIYB()= \"345\"" }); //
		//		vec_functions.add(new String[] { "getLoginUser_Dept_SHIYB_NAME()", "��¼�û���������֮������ҵ������", "getLoginUser_Dept_SHIYB_NAME()= \"���̽�����ҵ��\"" }); //
		//
		//		vec_functions.add(new String[] { "getLoginUser_Dept_SHIYBFB()", "��¼�û���������֮������ҵ���ֲ�ID", "getLoginUser_Dept_SHIYBFB()= \"126\"" }); //
		//		vec_functions.add(new String[] { "getLoginUser_Dept_SHIYBFB_NAME()", "��¼�û���������֮������ҵ���ֲ�����", "getLoginUser_Dept_SHIYBFB_NAME()= \"���̽�����ҵ���Ϻ��ֲ�\"" }); //
		//
		//		vec_functions.add(new String[] { "getLoginUser_PostId()", "���ص�ǰ��¼�û�������������", "getLoginUserDeptId()= \"126\"" }); //
		//		vec_functions.add(new String[] { "getLoginUser_PostCode()", "���ص�ǰ��¼�û�������������", "getLoginUserDeptId()= \"126\"" }); //
		//		vec_functions.add(new String[] { "getLoginUser_PostName()", "���ص�ǰ��¼�û�������������", "getLoginUserDeptId()= \"126\"" }); //
		//
		//		vec_functions.add(new String[] { "getLoginUserDeptId()", "���ص�ǰ��¼�û�������������", "getLoginUserDeptId()= \"126\"" }); //
		//		vec_functions.add(new String[] { "getLoginUser_Dept()", "���ص�ǰ��¼�û�����������", "getLoginUser_Dept()= \"�ɶ�����\"" }); //
		//
		//		vec_functions.add(new String[] { "getLoginUserBelongDept()", "���ص�ǰ��¼�û���������Ϊ���л��Ƿ���", "getLoginUserBelongDept()= \"����\"" }); //
		//		vec_functions.add(new String[] { "getLoginUser_HeadFhDept()", "���ص�ǰ��¼�û�������ͷ���л����е�����", "getLoginUser_HeadFhDept()= \"����\"" }); //
		//		vec_functions.add(new String[] { "getLoginUser_HeadZhDept()", "���ص�ǰ��¼�û�������֧�е�����", "getLoginUser_HeadZhDept()= \"XXXX֧��\"" }); //
		//		vec_functions.add(new String[] { "getLoginUser_BranchDept()", "���ص�ǰ��¼�û�����������ǰ��λ", "getLoginUser_BranchDept()= \"�ɶ�\"" }); //
		//		vec_functions.add(new String[] { "getLoginUserDeptLinkCode()", "���ص�ǰ��¼�û��������ű���", "getLoginUserDeptLinkCode()= \"00010003\"" });
		//		vec_functions.add(new String[] { "getLoginUserPostCode()", "���ص�ǰ��¼�û���λ����", "getLoginUserPostCode() = \"BPS/B/LG/M010\"" });
		//		vec_functions.add(new String[] { "getUserRole()", "���ݵ�½��ȡ���൱�Ľ�ɫ", "getLoginUserInfo(\"userdef01\")= \"aa\"" });

//		vec_functions.add(new String[] { "getLoginUserInfo(\"userdef01\")", "���ص�ǰ��¼�û�ĳһ�е�ֵ", "getLoginUserInfo(\"userdef01\")= \"aa\"" });
		vec_functions.add(new String[] { "getTreePathColValue(\"����\",\"�����ֶ���\",\"���νṹ������id�ֶ�\",\"���νṹ������parentid�ֶ�\",\"�����ֶ�(����id)\",\"��������(����12345)\",\"����·������=Y/�Ƿ�ص���һ��=N\");", "��һ�����ͽṹ�и���id��ֵ�����丸��·�������н���nameֵƴ��һ��!",
				"getTreePathColValue(\"pub_corp_dept\",\"name\",\"id\",\"parentid\",\"id\",\"12345\",\"����·������=Y/�Ƿ�ص���һ��=N\");" });
		vec_functions.add(new String[] { "getTreePathColValue(\"����\",\"�����ֶ���\",\"���νṹ������id�ֶ�\",\"���νṹ������parentid�ֶ�\",\"�����ֶ�(����id)\",\"��������(����12345)\",\"����·������=Y/�Ƿ�ص���һ��=N\",\"�ص����ֶ�����=corptype/�ص����ֶ�ֵ=����1,����2\");", "��һ�����ͽṹ�и���id��ֵ�����丸��·�������н���nameֵƴ��һ��!",
		"getTreePathColValue(\"pub_corp_dept\",\"name\",\"id\",\"parentid\",\"id\",getItemValue(\"deptid\"),\"����·������=Y/�Ƿ�ص���һ��=Y\",\"�ص����ֶ�����=corptype/�ص����ֶ�ֵ=���в��Ŵ���,һ�����в��Ŵ���\");" });
		vec_functions.add(new String[] { "getDateDifference(\"firsttime\",\"secondtime\")", "��������ʱ��Ĳ�,��������", "getDateDifference(\"2007-01-05\",\"2007-01-06\")= 1" });

		vec_functions.add(new String[] { "getCurrDate()", "����ϵͳ��ǰ����", "getCurrDate()= \"2007-01-05\"" });
		vec_functions.add(new String[] { "getCurrTime()", "����ϵͳ��ǰʱ��", "getCurrTime()= \"2007-01-05 10:05:33\"" });

		vec_functions.add(new String[] { "getYearByDateTime()", "����ĳ��ʱ������", "getYearByDateTime(\"2008-10-05\")= \"2008\"" });
		vec_functions.add(new String[] { "getSeasonByDateTime()", "����ĳ��ʱ��ļ���", "getSeasonByDateTime(\"2008-10-05\")= \"2008��4����\"" });
		vec_functions.add(new String[] { "getMonthByDateTime()", "����ĳ��ʱ����·�", "getMonthByDateTime(\"2008-10-05\")= \"2008��12��\"" });
		vec_functions.add(new String[] { "getDateByDateTime()", "����ĳ��ʱ�������", "getDateByDateTime(\"2008-10-05\")= \"2008-10-05\"" });

		vec_functions.add(new String[] { "getCurrDBDate()", "���ط������˵�ǰ����", "getCurrDBDate()= \"2007-01-05\"" });
		vec_functions.add(new String[] { "getCurrDBTime()", "���ط������˵�ǰʱ��", "getCurrDBTime()= \"2007-01-05 10:05:33\"" });
		vec_functions.add(new String[] { "getSequence(\"sequencename\",\"digit\")", "���ص�ǰ���е�ֵ", "getSequence(\"sequencename\",\"3\")= \"001\"" });
		vec_functions.add(new String[] { "getFillZeroNum(\"number\",\"digit\")", "���ز�����ֵ", "getFillZeroNum(\"number\",\"3\")= \"001\"" });

		vec_functions.add(new String[] { "afterDate(\"oldDate\",5)", "����ָ��ʱ�����", "afterDate(\"2007-05-01\",5)= \"2007-05-05\"" });
		vec_functions.add(new String[] { "toString(Object)", "�Ѵ���Ĳ���objectת��ΪString", "toString(123) = \"123\"" });
		vec_functions.add(new String[] { "getMapStrItemValue(\"par1=10;par2=21;par3=aa;\",\"par1\")", "��һ����ϣ�����͵��ַ�����ȡ�ö�Ӧkey��ֵ", "getMapStrItemValue(getItemValue(\"mylastsubmittime\"),getLoginUserId()) = \"123\"" });

		vec_functions.add(new String[] { "getCaseValue(\"RUN\",\"RUN,������,END,�����ѽ���,null,����δ����\")", "ʡȥ��ǰ��if/else���õ��鷳,һ���㶨", "getCaseValue(\"RUN\",\"RUN,������,END,�����ѽ���,null,����δ����\")" });
		vec_functions.add(new String[] { "toNumber(\"par\")", "�Ѵ���Ĳ���objectת��ΪNumber", "toNumber(\"70\") = 123" });
		vec_functions.add(new String[] { "indexOf(\"string\",\"indexvalue\")", "����string��indexvalue��λ��", "indexOf(\"abcdf\",\"c\") = 2" });
		vec_functions.add(new String[] { "length(\"abcde\")", "ͳ��һ���ַ����Ŀ��", "length(\"abcde\")=5" });

		vec_functions.add(new String[] { "subString(\"string\",beginindex,endindex)", "��ȡstring��beginindex-endindex֮����ַ���", "subString(\"abcdefg\",1,5) = \"bcde\"" });
		vec_functions.add(new String[] { "fillBlank(\"��ϴǮ��\")", "��һ���ַ����ָ�����м����һ���ո�", "fillBlank(\"��ϴǮ��\")=�� ϴ Ǯ �� " });
		vec_functions.add(new String[] { "replaceall(\"string\",\"regexstring\",\"replacestring\")", "��string�����е�regexstring�滻Ϊreplacestring", "replaceall(\"abcd abdg\",\"ab\",\"11\") = \"11cd 11dg\"" });
		vec_functions.add(new String[] { "replaceallandsubString(\"string\",\";\",\",\")", "��string�����е�regexstring�滻Ϊreplacestring��������ȥ���ַ��������һλ", "replaceall(\"aba,bcd,\",\";\",\",\") = \"aba;bcd\"" });

		vec_functions.add(new String[] { "getRandom()", "ȡ������� ����+���ʮλ��", "getRandom()= \"2008-1234567890\"" });
		vec_functions.add(new String[] { "getParAsMap(\"�Ƿ���ʾ�����\",\"N\",\"�Ƿ�ֻ����ѡ��Ҷ�ӽ��\",\"Y\")", "����������żλΪkey��value,����һ����ϣ��", "getParAsMap(\"�Ƿ���ʾ�����\",\"Y\",\"�Ƿ�ֻ����ѡ��Ҷ�ӽ��\",\"N\")=һ��HashMap����!" });

		// ���пؼ���������!!!�ǳ����õĺ�!!
		vec_functions.add(new String[] { "getItemValue(\"itemKey\")", "ȡ��ҳ����ĳһ���ֵ", "getItemValue(\"code\") = \"A001\"" });
		vec_functions.add(new String[] { "setItemValue(\"itemKey\",\"newvalue\")", "����ҳ����ĳһ���ֵ", "setItemValue(\"code\",\"A001\")" });
		vec_functions.add(new String[] { "setItemLabel(\"itemKey\",\"newvalue\")", "����ҳ����ĳһ�������", "setItemLabel(\"code\",\"A001\")" });

		// ���ò���

		vec_functions.add(new String[] { "getRefName(\"itemKey\")", "�õ�ĳ�����յ�Nameֵ", "getRefName(\"userid\")" });
		vec_functions.add(new String[] { "getRefCode(\"itemKey\")", "�õ�ĳ�����յ�Codeֵ", "getRefCode(\"userid\")" });
		vec_functions.add(new String[] { "getTreeItemLevelValue(\"itemkey\",\"level\")", "�õ��Զ�������β��յ�N���ֵ", "getTreeItemLevelValue(\"itemkey\",\"level\")=AAA" });

		vec_functions.add(new String[] { "getMultiRefName(\"pub_user\",\"name\",\"code\",getItemValue(\"usercode\"))", "�õ���ѡ���յ�Nameֵ", "getMultiRefName(\"pub_user\",\"name\",\"code\",getItemValue(\"usercode\"))" });

		vec_functions.add(new String[] { "setRefItemCode(\"itemKey\",\"refcode\")", "����ĳ�����յ�Codeֵ", "setItemValue(\"userid\",\"A001\")" });
		vec_functions.add(new String[] { "setRefItemName(\"itemKey\",\"refname\")", "����ĳ�����յ�Nameֵ", "setItemValue(\"userid\",\"����\")" });
		vec_functions.add(new String[] { "setRefDefine(\"itemKey\",\"refdefine\")", "����ĳ�����յĶ���", "setItemValue(\"userid\",\"getRegFormatRef(\"����\",\"\")" });

		vec_functions.add(new String[] { "getStringItemVO(\"����\")", "����һ��StringItemVO����", "getStringItemVO(\"����\")" });
		vec_functions.add(new String[] { "getComBoxItemVO(\"3\",\"003\",\"����\")", "����һ��ComBoxItemVO����", "getComBoxItemVO(\"3\",\"003\",\"����\")" });
		vec_functions.add(new String[] { "getRefItemVO(\"3\",\"003\",\"����\")", "����һ��RefItemVO����", "getRefItemVO(\"3\",\"003\",\"����\")" });
		//ͳ�ƻ��ѯ���������Ĭ�ϲ�ѯ��ǰ��ȣ�����Ҫ�ڲ�ѯĬ�����������ø�RefItemVO����ͨ��RefItemVOû��HashVO������������ѯ��䣬��������ʽ�����/2016-03-20��
		vec_functions.add(new String[] { "getYearDateRefItemVO(\"2016\")", "����һ������RefItemVO����", "getRefItemVO(\"2016��;\",\"��;\",\"2016��;\")" });

		vec_functions.add(new String[] { "resetAllItemValue()", "�������ֵ", "resetAllItemValue()" });
		vec_functions.add(new String[] { "resetItemValue(\"itemKey\")", "���ҳ����ĳһ���ֵ", "resetItemValue(\"code\")" });
		vec_functions.add(new String[] { "resetCardGroupValue(\"grouptitle1,grouptitle2\")", "���ҳ����һ�������ֵ", "resetCardGroupValue(\"����,������Ϣ\")" });
		vec_functions.add(new String[] { "setItemEditable(\"itemKey\",\"true\")", "����ҳ����ĳһ���Ƿ�ɱ༭", "setItemEditable(\"code\",\"true\")" });
		vec_functions.add(new String[] { "setItemForeGround(\"itemKey\",\"0000FF\")", "����ĳһ���ǰ����ɫ", "setItemForeGround(\"name\",\"FFFF00\")" });
		vec_functions.add(new String[] { "setItemBackGround(\"itemKey\",\"0000FF\")", "����ĳһ��ı�����ɫ,ֻ֧���б�����", "setItemBackGround(\"name\",\"FFFF00\")" });

		vec_functions.add(new String[] { "SetItemIsmustinput(\"itemKey\",\"true\")", "����ҳ����ĳһ���Ƿ�ɱ�����", "setItemEditable(\"code\",\"true\")" });
		vec_functions.add(new String[] { "setItemVisiable(\"itemKey\",\"true\")", "����ҳ����ĳһ���Ƿ����ʾ", "setItemVisiable(\"code\",\"true\")" });
		vec_functions.add(new String[] { "setItemVisiable(\"itemKey\",\"true\")", "����ҳ����ĳһ���Ƿ����ʾ", "setItemVisiable(\"code\",\"true\")" });
		vec_functions.add(new String[] { "setCardRowVisiable(\"itemKey\",\"true\")", "���ÿ�Ƭҳ����ĳһ�е����пؼ��Ƿ����ʾ", "setCardRowVisiable(\"code\",\"true\")" });
		vec_functions.add(new String[] { "setCardGroupVisiable(\"grouptitle\",\"true\")", "���ÿ�Ƭҳ����ĳһ��������пؼ��Ƿ����ʾ", "setCardGroupVisiable(\"������Ϣ\",\"true\")" });
		vec_functions.add(new String[] { "setCardGroupExpand(\"grouptitle\",\"true\")", "���ÿ�Ƭҳ����ĳһ�����Ƿ�չ��", "setCardGroupExpand(\"������Ϣ\",\"true\")" });
		vec_functions.add(new String[] { "getEditState()", "��ÿ�Ƭ�ı༭״̬", "getEditState()" });//�����/2013-05-09��
		
		vec_functions.add(new String[] { "getFormatChildListItemValue(\"Pub_user_code1\",\"id\")", "ȡ��ҳ����ĳ���б��е�ĳһ���ֵ,ֻ�и�ҳ������Billformat���ɲ���!", "getFormatChildListItemValue(\"Pub_user_code1\") = \"id\"" });
		vec_functions.add(new String[] { "getFormatChildTreeItemValue(\"Pub_user_code1\",\"id\")", "ȡ��ҳ����ĳ�����е�ĳһ���ֵ,ֻ�и�ҳ������Billformat���ɲ���!", "getFormatChildTreeItemValue(\"Pub_user_code1\") = \"id\"" });
		vec_functions.add(new String[] { "execWLTAction(\"cn.com.pushworld.TestWLTAction\")", "ִ��ĳһ����ʽ", "execWLTAction(\"cn.com.pushworld.TestWLTAction\")" });
		vec_functions.add(new String[] { "openBillListDialog(\"PUB_USER_CODE_1\",\"id in (select userid from pub_user_role where roleid ='${id}')\",\"500\",\"300\")", "ֱ�Ӵ�һ���б��ѯ��,��������֧��${}������滻,��������������ѡ���е�ĳһ��ֵ�滻",
				"openBillListDialog(\"PUB_USER_CODE_1\",\"id in (select userid from pub_user_role where roleid='${ID}')\",\"500\",\"300\")" });

		// �������ӵ��¼���ʽ!!!
		vec_functions.add(new String[] { "setBtnBarVisiable(getList(\"PUB_POST_CODE1\"),\"false\");", "����ĳ��ģ���ͨ�ò�����ť�Ƿ���ʾ", "" });
		vec_functions.add(new String[] { "setBillPanelBtnVisiable(getList(\"PUB_POST_CODE1\"),\"����\",\"false\");", "���ð�ť�Ƿ���ʾ", "" });
		vec_functions.add(new String[] { "getSelectedBillVOItemValue(getList(\"PUB_POST_CODE1\"),\"name\")", "ȡ��ĳ�������ĳ������", "" });
		vec_functions.add(new String[] { "addBillSelectListener(getList(\"PUB_POST_CODE1\"),\"\"queryAllData(getList(\"PUB_POST_CODE1\")\")\");", "�����¼�,�ٴε��ù�ʽ,��������ù�ʽ!", "" });
		vec_functions.add(new String[] { "getUserRole();", "ͨ����½��ID�õ����Ľ�ɫ", "" }); //
		vec_functions.add(new String[] { "queryAllData(getList(\"PUB_POST_CODE1\"));", "Ĭ�ϲ�ѯ��������", "" }); //
		vec_functions.add(new String[] { "queryDataByCondition(getList(\"PUB_POST_CODE1\"),\"code='001'\");", "����������ѯ����", "" });
		vec_functions.add(new String[] { "addSelectEventBindRefresh(getTree(\"pub_corp_dept_CODE1\"),getList(\"PUB_POST_CODE1\"),\"id\",\"code\");", "ѡ��ˢ���¼�,ͨ��һ����ģ��ˢ����һ��ģ��,IDΪ������codeΪ��ʾ����", "addSelectEventBindRefresh(getTree(\"pub_corp_dept_CODE1\"),getList(\"PUB_POST_CODE1\"),\"id\",\"code\"" });
		vec_functions.add(new String[] { "addSelectQuickQueryRefresh(getTree(\"pub_corp_dept_CODE1\"),getList(\"PUB_POST_CODE1\"),\"id\",\"name\",\"listid\");", "ˢ�¿��ٲ�ѯ�¼���ͨ��һ��ģ��ˢ����һ��ģ��Ŀ��ٲ�ѯ���",
				"addSelectQuickQueryRefresh(getTree(\"pub_corp_dept_CODE1\"),getList(\"PUB_POST_CODE1\"),\"id\",\"name\",\"listid\")" });
		vec_functions.add(new String[] { "setEveryBtnBarIsVisiable(getTree(\"PUB_POST_CODE1\"),getList(\"PUB_POST_CODE1\"),\"����\",\"false\",\"�༭\",\"false\",\"ɾ��\",\"false\",\"�鿴\",\"false\");", "һ��ģ��ˢ��ʱ��������һ��ģ���Ĭ�ϲ�����ť�Ƿ��й���", "" });
		vec_functions.add(new String[] { "setSelfBtnBarIsVisiable(getTree(\"pub_corp_dept_CODE1\"),getList(\"PUB_POST_CODE1\"),\"�Զ��尴ť\",\"false\");", "һ��ģ��ˢ��ʱ��������һ��ģ����Զ��������ť�Ƿ��й���", "" });
		vec_functions.add(new String[] { "clearTable(getTree(\"PUB_POST_CODE1\"),getList(\"PUB_POST_CODE1\"));", "��ĳ����һ��ģ�巢���仯ʱ�����һ��ģ���ֵ", "" });
		vec_functions.add(new String[] { "setListQQVisiable(getList(\"pub_corp_dept_CODE1\"),\"false\");", "�����б�Ŀ��ٲ�ѯ����Ƿ���ʾ", "" });
		vec_functions.add(new String[] { "setSelfListQQIsVisiable(getList(\"pub_corp_dept_CODE1\"),\"�Զ��尴ť\",\"false\");", "�����Զ��尴ť�ڿ��ٲ�ѯ���Ƿ��й���", "" });
		vec_functions.add(new String[] { "setListQQIsVisiable(getList(\"pub_corp_dept_CODE1\"),\"����\",\"false\",\"�༭\",\"false\",\"ɾ��\",\"false\",\"�鿴\",\"false\");", "����ͨ�ð�ť�ڿ��ٲ�ѯ���Ƿ��й���", "" });
		vec_functions.add(new String[] { "setListQQAfterIsVisiable(getList(\"pub_corp_dept_CODE1\"),getList(\"pub_corp_dept_CODE1\"),\"����\",\"false\",\"�༭\",\"false\",\"ɾ��\",\"false\",\"�鿴\",\"false\");", "��һ��ģ����ٲ�ѯ��������һ��ģ���ͨ�ð�ť�Ƿ��й���", "" });
		vec_functions.add(new String[] { "setSelfListQQAfterIsVisiable(getList(\"pub_corp_dept_CODE1\"),getList(\"pub_corp_dept_CODE1\"),\"�Զ��尴ť\",\"false\");", "��һ��ģ����ٲ�ѯ��������һ��ģ����Զ��尴ť�Ƿ��й���", "" });
		vec_functions.add(new String[] { "setQQAfterClearTable(getList(\"pub_corp_dept_CODE1\"),getList(\"pub_corp_dept_CODE1\"));", "��һ��ģ����ٲ�ѯ�������һ��ģ���ֵ", "" });
		vec_functions.add(new String[] { "setDefaultRefItemVOReturnFrom(getList(\"pub_corp_dept_CODE1\"),\"id\",\"name\");", "����Ĭ�ϲ��շ���", "" });

		//Excel��幫ʽ
		vec_functions.add(new String[] { "setCellItemValue(\"2-D\",\"Y\");", "����ĳ��Cell��ֵ", "" }); //
		vec_functions.add(new String[] { "getCellItemValue(\"2-D\");", "�õ�ĳһ��Cell��ֵ", "" }); //
		vec_functions.add(new String[] { "getCellItemNumberValue(\"2-D\");", "�õ�ĳһ��Cell��ֵ,��ת��Ϊ����", "" }); //
		vec_functions.add(new String[] { "getCellSumValueByRowCol(\"2\",\"3\",\"4\",\"5\");", "ȡ��ĳһ�ε�ֵ", "" }); //

		vec_functions.add(new String[] { "getCellEditingItemNumberValue();", "ȡ�õ�ǰ�༭�ĸ��ӵ�����ֵ", "" }); //
		vec_functions.add(new String[] { "getCellCurrRowItemValue(\"2\");", "ȡ�õ�ǰ�е�2�е�ֵ", "" }); //
		vec_functions.add(new String[] { "getCellCurrRowItemNumberValue(\"2\");", "ȡ�õ�ǰ�е�2�е�ֵ,��ת��������", "" }); //
		vec_functions.add(new String[] { "getCellCurrColItemValue(\"2\");", "ȡ�õ�ǰ�е�2�е�ֵ", "" }); //
		vec_functions.add(new String[] { "getCellCurrColItemNumberValue(\"2\");", "ȡ�õ�ǰ�е�2�е�ֵ,��ת��������", "" }); //

		//���������湫ʽ!!
		vec_functions.add(new String[] { "getWFInfo(\"��ǰ���̱���;��ǰ���ڱ���;��ǰ�����˱���;����ύ�˱���;��ʷ�����˱���\");", "ȡ�ù����������Ϣ", "" }); //
		vec_functions.add(new String[] { "setWFRouterMarkValue(\"xchapprove\",\"Y\");", "�ڹ���������������·�ɱ��", "" }); //
		vec_functions.add(new String[] { "addWFRouterMarkValue(\"xchapprove\",\"Y\");", "�ڹ���������������·�ɱ��", "" }); //
		vec_functions.add(new String[] { "getWFRouterMarkValue(\"xchapprove\");", "ȡ�ù���������������·�ɱ��", "" }); //
		vec_functions.add(new String[] { "getWFRouterMarkCount(\"xchapprove\");", "ȡ��ĳ��·�ɱ�Ǹ���", "" }); //
		vec_functions.add(new String[] { "getWFBillItemValue(\"casetype\");", "ȡ�ù����������ж�Ӧ�ĵ����е�ֵ", "" }); //

		//������ʽ
		vec_functions.add(new String[] { "DirectLinkRef(\"field2\");", "��table������������¼��field2��ֵͨ������ѡ��", "" }); //
		vec_functions.add(new String[] { "ShowNewCardDialog(\"templetcode\");", "����ģ�������ʾ�����Ƭ", "" }); //
		vec_functions.add(new String[] { "getDecimalFormatNumber(\"\",\"\");", "������ת����һ���ĸ�ʽ����һ������Ϊ��ʽ���ڶ���������", "getDecimalFormatNumber(\"#,###.00\",\"12345.6\")=\"12,345.60\"" }); //

		return vec_functions;
	}

	public static JepFormulaParse createUIJepParse() {
		return createJepParseByType(WLTConstants.JEPTYPE_UI); //
	}

	public static JepFormulaParse createBSJepParse() {
		return createJepParseByType(WLTConstants.JEPTYPE_BS); //
	}

	//ֱ�Ӵ���һ��������Ľ�����! ���Ȳ���JepFormulaParseAtUIҲ����JepFormulaParseAtBS,����UI/BS����
	public static JepFormulaParse createJepParseByType(final byte _jepType) {
		JepFormulaParse jep = new JepFormulaParse() {
			@Override
			protected byte getJepType() {
				return _jepType;
			}
		};
		jep.initNormalFunction(); //����һ�㹫ʽ!!
		return jep; //
	}

	public JEP getParser() {
		return parser;
	}
}
