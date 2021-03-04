package cn.com.infostrategy.to.sysapp.login;

import java.io.Serializable;
import java.util.Vector;

/**
 * ������ģ�����,��չ�µķ��ģ��ʱֻ��Ҫ�޸�����������,Ȼ��������һ���༴��,��÷ǳ����
 * @author xch
 *
 */
public class StyleTempletDefineBuilder implements Serializable {
	private static final long serialVersionUID = 1L;

	private Vector vc_vos = new Vector(); //�洢���ж�����ļ���

	/**
	 * ���췽��,����ʼ������ģ�嶨��...
	 * ����һ��ģ��ʱֻҪ��������:1-�ڸ÷�����ע��һ����ģ�嶨��;2-��ע�����������һ����,�����п���ͨ��getStyleFormulaValue("$TempletCode")ȡ�ò���ֵ.
	 * ����ƽ̨�Զ��������˼�����:
	 * 1.����˵�ʱ�Զ��������ʵ��
	 * 2.�Զ��������͵����ʵ����ȥ
	 * 3.�������ͨ����ʽ�����Զ�ȡ�ò���ֵ,�����ַ������ַ����顣
	 */
	public StyleTempletDefineBuilder() {
		StyleTempletDefineVO stdo = null; //

		stdo = new StyleTempletDefineVO("���ģ��1", "���б�", "cn.com.infostrategy.ui.mdata.styletemplet.t01.DefaultStyleWorkPanel_01"); //
		stdo.putFormula("$TempletCode", "PUB_USER_CODE1"); //
		vc_vos.add(stdo); //

		stdo = new StyleTempletDefineVO("���ģ��2", "���б�/��", "cn.com.infostrategy.ui.mdata.styletemplet.t02.DefaultStyleWorkPanel_02"); //
		stdo.putFormula("$TempletCode", "PUB_USER_CODE1"); //
		stdo.putFormula("UIIntercept", "cn.com.pushworld.Intercept"); //
		vc_vos.add(stdo); //

		stdo = new StyleTempletDefineVO("���ģ��3", "������/��", "cn.com.infostrategy.ui.mdata.styletemplet.t03.DefaultStyleWorkPanel_03"); //
		stdo.putFormula("$TempletCode", "pub_corp_dept_CODE1"); //
		vc_vos.add(stdo); //

		stdo = new StyleTempletDefineVO("���ģ��4", "˫������", "cn.com.infostrategy.ui.mdata.styletemplet.t04.DefaultStyleWorkPanel_04"); //
		stdo.putFormula("$TreeTempletCode", "pub_corp_dept_CODE1"); //
		stdo.putFormula("$TreeJoinField", "id"); //
		stdo.putFormula("$TableTempletCode", "pub_user_post_CODE1"); //
		stdo.putFormula("$TableJoinField", "deptid"); //
		stdo.putFormulaBlankLine(); //
		stdo.putFormula("$CustAfterInitClass", "cn.com.pushworld.ui.AfterInitialize"); //
		vc_vos.add(stdo); //

		stdo = new StyleTempletDefineVO("���ģ��5", "˫������/��", "cn.com.infostrategy.ui.mdata.styletemplet.t05.DefaultStyleWorkPanel_05"); //
		stdo.putFormula("$TreeTempletCode", "pub_corp_dept_CODE1"); //
		stdo.putFormula("$TreeJoinField", "id"); //
		stdo.putFormula("$TableTempletCode", "pub_user_post_CODE1"); //
		stdo.putFormula("$TableJoinField", "deptid"); //
		stdo.putFormulaBlankLine(); //
		stdo.putFormula("$CustPanel", "cn.com.pushworld.ui.TestCustPanel"); //
		stdo.putFormula("$UIIntercept", "cn.com.pushworld.ui.TestUIIntercept"); //
		stdo.putFormula("$BSIntercept", "cn.com.pushworld.bs.TestBSIntercept"); //
		stdo.putFormula("$IsShowSysButton", "Y"); //
		vc_vos.add(stdo); //

		stdo = new StyleTempletDefineVO("���ģ��6", "˫������", "cn.com.infostrategy.ui.mdata.styletemplet.t06.DefaultStyleWorkPanel_06"); //
		stdo.putFormula("$ParentTempletCode", "pub_corp_dept_CODE1"); //
		stdo.putFormula("$ParentJoinField", "id"); //
		stdo.putFormula("$ChildTempletCode", "pub_user_post_CODE1"); //
		stdo.putFormula("$ChildJoinField", "deptid"); //
		stdo.putFormulaBlankLine(); //
		stdo.putFormula("$CustPanel", "cn.com.pushworld.ui.TestCustPanel"); //
		stdo.putFormula("$UIIntercept", "cn.com.pushworld.ui.TestUIIntercept"); //
		stdo.putFormula("$BSIntercept", "cn.com.pushworld.bs.TestBSIntercept"); //
		stdo.putFormula("$IsShowSysButton", "Y"); //
		stdo.putFormula("$Layout", "����"); //
		vc_vos.add(stdo); //

		stdo = new StyleTempletDefineVO("���ģ��7", "˫������/��", "cn.com.infostrategy.ui.mdata.styletemplet.t07.DefaultStyleWorkPanel_07"); //
		stdo.putFormula("$ParentTempletCode", "pub_corp_dept_CODE1"); //
		stdo.putFormula("$ParentJoinField", "id"); //
		stdo.putFormula("$ChildTempletCode", "pub_user_post_CODE1"); //
		stdo.putFormula("$ChildJoinField", "deptid"); //
		stdo.putFormulaBlankLine(); //
		stdo.putFormula("$CustPanel", "cn.com.pushworld.ui.TestCustPanel"); //
		stdo.putFormula("$UIIntercept", "cn.com.pushworld.ui.TestUIIntercept"); //
		stdo.putFormula("$BSIntercept", "cn.com.pushworld.bs.TestBSIntercept"); //
		stdo.putFormula("$IsShowSysButton", "Y"); //
		stdo.putFormula("$Layout", "����"); //
		vc_vos.add(stdo); //

		stdo = new StyleTempletDefineVO("���ģ��8", "���ӱ�", "cn.com.infostrategy.ui.mdata.styletemplet.t08.DefaultStyleWorkPanel_08"); //
		stdo.putFormula("$ParentTempletCode", "pub_corp_dept_CODE1"); //
		stdo.putFormula("$ParentJoinField", "id"); //
		stdo.putFormula("$ChildTempletCode", "pub_user_post_CODE1"); //
		stdo.putFormula("$ChildJoinField", "deptid"); //
		stdo.putFormulaBlankLine(); //
		stdo.putFormula("$CustPanel", "cn.com.pushworld.ui.TestCustPanel"); //
		stdo.putFormula("$UIIntercept", "cn.com.pushworld.ui.TestUIIntercept"); //
		stdo.putFormula("$BSIntercept", "cn.com.pushworld.bs.TestBSIntercept"); //
		stdo.putFormula("$IsShowSysButton", "Y"); //
		vc_vos.add(stdo); //

		stdo = new StyleTempletDefineVO("���ģ��9", "���ӱ�", "cn.com.infostrategy.ui.mdata.styletemplet.t09.DefaultStyleWorkPanel_09"); //
		stdo.putFormula("$ParentTempletCode", "PUB_USER_CODE1"); //
		stdo.putFormula("$ParentJoinField", "id"); //
		stdo.putFormula("$ChildTempletCode", new String[] { "pub_user_post_CODE1", "pub_user_role_CODE1", "pub_user_menu_CODE1" }); //
		stdo.putFormula("$ChildJoinField", new String[] { "deptid", "roleid", "menuid" }); //
		stdo.putFormulaBlankLine(); //
		stdo.putFormula("$CustPanel", "cn.com.pushworld.ui.TestCustPanel"); //
		stdo.putFormula("$UIIntercept", "cn.com.pushworld.ui.TestUIIntercept"); //
		stdo.putFormula("$BSIntercept", "cn.com.pushworld.bs.TestBSIntercept"); //
		stdo.putFormula("$IsShowSysButton", "Y"); //
		vc_vos.add(stdo); //		

		stdo = new StyleTempletDefineVO("���ģ��10", "�������", "cn.com.infostrategy.ui.mdata.styletemplet.t10.DefaultStyleWorkPanel_10"); //
		stdo.putFormula("$ParentTempletCode", "pub_corp_dept_CODE1"); //
		stdo.putFormula("$ParentJoinField", "id"); //
		stdo.putFormula("$ChildTempletCode", "pub_user_post_CODE1"); //
		stdo.putFormula("$ChildJoinField", "deptid"); //
		stdo.putFormulaBlankLine(); //
		stdo.putFormula("$CustPanel", "cn.com.pushworld.ui.TestCustPanel"); //
		stdo.putFormula("$UIIntercept", "cn.com.pushworld.ui.TestUIIntercept"); //
		stdo.putFormula("$BSIntercept", "cn.com.pushworld.bs.TestBSIntercept"); //
		stdo.putFormula("$IsShowSysButton", "Y"); //
		vc_vos.add(stdo); //	

		//���ݲ����Ķ������,������ĸ��ֱ���!!!
		stdo = new StyleTempletDefineVO("���ģ��1A", "����ҳǩ,��һ���ǿ�Ƭ�ڶ������б�", "cn.com.infostrategy.ui.mdata.styletemplet.t1a.DefaultStyleWorkPanel_1A"); //
		stdo.putFormula("$TempletCode", "pub_corp_dept_CODE1"); //
		vc_vos.add(stdo); //	

		stdo = new StyleTempletDefineVO("���ģ��2A", "ͨ��һ��������г���Ƭ����ȥά����һ������", "cn.com.infostrategy.ui.mdata.styletemplet.t2a.DefaultStyleWorkPanel_2A"); //
		stdo.putFormula("$TempletCode1", "pub_corp_dept_CODE1"); //
		stdo.putFormula("$TempletCode2", "pub_corp_dept_CODE1"); //
		vc_vos.add(stdo); //	

		stdo = new StyleTempletDefineVO("���ģ��2B", "ͨ��һ�����󵯳��б�ȥά����һ������", "cn.com.infostrategy.ui.mdata.styletemplet.t2b.DefaultStyleWorkPanel_2B"); //
		stdo.putFormula("$TempletCode1", "pub_corp_dept_CODE1"); //
		stdo.putFormula("$TempletCode2", "pub_corp_dept_CODE1"); //
		vc_vos.add(stdo); //	

		stdo = new StyleTempletDefineVO("���ģ��3A", "�����б�,ѡ��Aˢ��B,ѡ��Bˢ��C", "cn.com.infostrategy.ui.mdata.styletemplet.t3a.DefaultStyleWorkPanel_3A"); //
		stdo.putFormula("$TempletCode1", "pub_corp_dept_CODE1"); //
		stdo.putFormula("$TempletCode2", "pub_corp_dept_CODE1"); //
		stdo.putFormula("$TempletCode3", "pub_corp_dept_CODE1"); //
		vc_vos.add(stdo); //	

		stdo = new StyleTempletDefineVO("���������1", "��ҳǩ��ʽ", "cn.com.infostrategy.ui.mdata.styletemplet.wf1.DfWFStyle1WKPanel"); //
		stdo.putFormula("$TempletCode", "pub_corp_dept_CODE1"); //
		vc_vos.add(stdo); //	
		
		stdo = new StyleTempletDefineVO("���������2", "���䰴ť���б���", "cn.com.infostrategy.ui.mdata.styletemplet.wf2.DfWFStyle2WKPanel"); //
		stdo.putFormula("$TempletCode", "pub_corp_dept_CODE1"); //
		vc_vos.add(stdo); //	
		
	}

	public StyleTempletDefineVO[] getAllStyleTempletDefineVOs() {
		return (StyleTempletDefineVO[]) vc_vos.toArray(new StyleTempletDefineVO[0]);
	}

	/**
	 * ȡ�����з��ģ�������..
	 * @return
	 */
	public String[] gettAllStyleTempletNames() {
		StyleTempletDefineVO[] stdos = getAllStyleTempletDefineVOs(); //
		String[] str_returns = new String[stdos.length]; //
		for (int i = 0; i < str_returns.length; i++) {
			str_returns[i] = stdos[i].getName(); //
		}
		return str_returns;
	}

	/**
	 * ��������ȡ��ĳһ�ַ��ģ���ʵ������.
	 * @param _name
	 * @return
	 */
	public String getDefaultClassName(String _name) {
		for (int i = 0; i < vc_vos.size(); i++) {
			StyleTempletDefineVO stvo = (StyleTempletDefineVO) vc_vos.get(i); //
			if (stvo.getName().equals(_name)) {
				return stvo.getDefaultClassName(); //
			}
		}
		return null;
	}
}
