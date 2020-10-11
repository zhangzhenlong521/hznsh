package cn.com.infostrategy.ui.mdata.formatcomp;

import java.util.Vector;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.jepfunctions.JepFormulaParse;
import cn.com.infostrategy.ui.mdata.BillFormatPanel;
import cn.com.infostrategy.ui.mdata.styletemplet.config.SetSelfListQQIsVisiable;

/**
 * ��ʽ���
 * 
 * @author xch
 *
 */
public class FormatEventBindFormulaParse extends JepFormulaParse {
	private BillFormatPanel formatpanel = null;
	private String str_regformula1, str_regformula2, str_regformula3 = null;

	//JEP parser = new JEP();

	public FormatEventBindFormulaParse(BillFormatPanel _panel) {
		this(_panel, null, null, null); //
	}

	public FormatEventBindFormulaParse(BillFormatPanel _panel, String _regformula1, String _regformula2, String _regformula3) {
		this.formatpanel = _panel; //
		this.str_regformula1 = _regformula1; //
		this.str_regformula2 = _regformula2; //
		this.str_regformula3 = _regformula3; //

		initNormalFunction(); //��ע�Ḹ�������ͨ�ú���!!

		//ע�����к���
		parser.addFunction("getCard", new GetFormatItemPanel(this.formatpanel, GetFormatItemPanel.ITEMTYPE_CARD)); //
		parser.addFunction("getList", new GetFormatItemPanel(this.formatpanel, GetFormatItemPanel.ITEMTYPE_LIST)); //
		parser.addFunction("getTree", new GetFormatItemPanel(this.formatpanel, GetFormatItemPanel.ITEMTYPE_TREE)); //
		parser.addFunction("getCell", new GetFormatItemPanel(this.formatpanel, GetFormatItemPanel.ITEMTYPE_CELL)); //

		parser.addFunction("getSelectedBillVOItemValue", new GetSelectedBillVOItemValueParse(this.formatpanel)); //�¼��󶨺���
		parser.addFunction("addSelectEventBindRefresh", new AddSelectEventBindRefresh(this.formatpanel)); //�¼��󶨺���
		parser.addFunction("addSelectQuickQueryRefresh", new AddSelectQuickQueryRefresh(this.formatpanel)); //�¼��󶨺���

		parser.addFunction("queryAllData", new QueryAllDataParse(this.formatpanel)); //�¼��󶨺���
		parser.addFunction("getUserRole", new GetUserRole()); //�¼��󶨺���

		parser.addFunction("queryDataByCondition", new QueryDataByConditionParse(this.formatpanel)); //�¼��󶨺���
		parser.addFunction("setEveryBtnBarIsVisiable", new SetEveryBtnBarIsVisiable(this.formatpanel)); //�¼��󶨺���
		parser.addFunction("setSelfBtnBarIsVisiable", new SetSelfBtnBarIsVisiable(this.formatpanel)); //�¼��󶨺���
		parser.addFunction("setBtnBarVisiable", new SetBtnBarVisiableParse(this.formatpanel)); //���ð�ť�Ƿ�ȫ����ʾ/����
		parser.addFunction("setBillPanelBtnVisiable", new SetBillPanelBtnVisiableParse(this.formatpanel)); //����ĳ����ť�Ƿ���ʾ

		parser.addFunction("setListQQVisiable", new SetListQQVisiableParse(this.formatpanel)); //�¼��󶨺���
		parser.addFunction("clearTable", new ClearTable(this.formatpanel)); //�¼��󶨺���
		parser.addFunction("setSelfListQQIsVisiable", new SetSelfListQQIsVisiable(this.formatpanel)); //�¼��󶨺���
		parser.addFunction("setListQQIsVisiable", new SetListQQIsVisiableParse(this.formatpanel)); //�¼��󶨺���
		parser.addFunction("setDefaultRefItemVOReturnFrom", new SetDefaultRefItemVOReturnFromParse(this.formatpanel)); //�¼��󶨺���
		parser.addFunction("setListQQAfterIsVisiable", new SetListQQAfterIsVisiable(this.formatpanel)); //�¼��󶨺���
		parser.addFunction("setSelfListQQAfterIsVisiable", new SetSelfListQQAfterIsVisiable(this.formatpanel)); //�¼��󶨺���
		parser.addFunction("setQQAfterClearTable", new SetQQAfterClearTable(this.formatpanel)); //�¼��󶨺���

		//ע���¼��󶨺���
		parser.addFunction("addBillSelectListener", new AddBillSelectListenerParse(this.formatpanel)); //�б����,ѡ��仯ʱ�������¼�,�����õĹ�ʽ!!!

		parser.addFunction("getRegFormula", new GetRegFormulaParse(this.formatpanel, str_regformula1, str_regformula2, str_regformula3)); //�б����,ѡ��仯ʱ�������¼�,�����õĹ�ʽ!!!
	}

	/**
	 * ���պ�����������˵���������ķ�ʽ����Vector
	 * 
	 * @return
	 */

	public static Vector getFunctionDetail() {
		Vector vec_functions = new Vector(); //
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
		vec_functions.add(new String[] { "clearTable(getList(\"PUB_POST_CODE1\"));", "���ĳһ������е�����,�����б�,��,��Ƭ", "" });
		vec_functions.add(new String[] { "setListQQVisiable(getList(\"pub_corp_dept_CODE1\"),\"false\");", "�����б�Ŀ��ٲ�ѯ����Ƿ���ʾ", "" });
		vec_functions.add(new String[] { "setSelfListQQIsVisiable(getList(\"pub_corp_dept_CODE1\"),\"�Զ��尴ť\",\"false\");", "�����Զ��尴ť�ڿ��ٲ�ѯ���Ƿ��й���", "" });
		vec_functions.add(new String[] { "setListQQIsVisiable(getList(\"pub_corp_dept_CODE1\"),\"����\",\"false\",\"�༭\",\"false\",\"ɾ��\",\"false\",\"�鿴\",\"false\");", "����ͨ�ð�ť�ڿ��ٲ�ѯ���Ƿ��й���", "" });
		vec_functions.add(new String[] { "setListQQAfterIsVisiable(getList(\"pub_corp_dept_CODE1\"),getList(\"pub_corp_dept_CODE1\"),\"����\",\"false\",\"�༭\",\"false\",\"ɾ��\",\"false\",\"�鿴\",\"false\");", "��һ��ģ����ٲ�ѯ��������һ��ģ���ͨ�ð�ť�Ƿ��й���", "" });
		vec_functions.add(new String[] { "setSelfListQQAfterIsVisiable(getList(\"pub_corp_dept_CODE1\"),getList(\"pub_corp_dept_CODE1\"),\"�Զ��尴ť\",\"false\");", "��һ��ģ����ٲ�ѯ��������һ��ģ����Զ��尴ť�Ƿ��й���", "" });
		vec_functions.add(new String[] { "setQQAfterClearTable(getList(\"pub_corp_dept_CODE1\"),getList(\"pub_corp_dept_CODE1\"));", "��һ��ģ����ٲ�ѯ�������һ��ģ���ֵ", "" });
		vec_functions.add(new String[] { "setDefaultRefItemVOReturnFrom(getList(\"pub_corp_dept_CODE1\"),\"id\",\"name\");", "����Ĭ�ϲ��շ���", "" });

		vec_functions.add(new String[] { "getRegFormula(\"1\")", "ȡ��ע�ṫʽ�ڼ���", "" });

		return vec_functions;
	}

	@Override
	protected byte getJepType() {
		return WLTConstants.JEPTYPE_UI; //
	}

}
