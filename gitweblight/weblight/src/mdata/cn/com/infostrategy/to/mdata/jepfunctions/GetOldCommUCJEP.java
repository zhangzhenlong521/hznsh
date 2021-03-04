package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.HashMap;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;

/**
 * ��ǰ�ĸ��ֿؼ�����һ�����Ĺ�ʽ��VO,����ͳһ����µ�getCommUC��,��Ϊ��֧������ݾɵĹ�ʽ,������������һ���ɵĹ�ʽ,Ȼ�����оɵĹ�ʽ��ͳһִ����������!
 * Ȼ��ͨ������UCType��������ʲô����!! Ȼ�󷵻ص����ݶ�����Զ����CommUCDefineVO,Ȼ���ڿؼ���ʵ�ִ����н�ԭ���ĸ����жϸĳ�һ�����ķ���,Ȼ�󶼴�CommUCDefineVO��ȡ!
 * @author xch
 *
 */
public class GetOldCommUCJEP extends PostfixMathCommand {

	private String UCType = null; //�ؼ�����

	/**
	 *���췽��
	 */
	public GetOldCommUCJEP(String _UCType) {
		numberOfParameters = -1; //��ȷ���Ĳ���!!
		this.UCType = _UCType; //�ؼ�����!!
	}

	/**
	 * ����ʱִ�еķ���!!!
	 */
	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		if (this.UCType.equals("����")) { //һ��12����Ҫ�ؼ�,�Ժ�Ҫ�����ļ�ѡ����,���������κ�һ�ֿؼ�����Ҫ����!! ���Ҳ�����չ�ǳ�����!!!
			getTableRef(inStack);
		} else if (this.UCType.equals("���Ͳ���")) {
			getTreeRef(inStack);
		} else if (this.UCType.equals("��ѡ����")) {
			getMultiRef(inStack);
		} else if (this.UCType.equals("�Զ������")) {
			getCustRef(inStack);
		} else if (this.UCType.equals("�б�ģ�����")) {
			getListTempletRef(inStack);
		} else if (this.UCType.equals("����ģ�����")) {
			getTreeTempletRef(inStack);
		} else if (this.UCType.equals("ע���������")) {
			getRegFormatRef(inStack);
		} else if (this.UCType.equals("ע�����")) {
			getRegisterRef(inStack);
		} else if (this.UCType.equals("Excel�ؼ�")) {
			getExcelRef(inStack);
		} else if (this.UCType.equals("Office�ؼ�")) {
			getOfficeRef(inStack);
		} else if (this.UCType.equals("�����ӱ�")) {
			getChildTable(inStack);
		} else if (this.UCType.equals("�����ӱ�")) {
			getChildTableImport(inStack);
		}
	}

	/**
	 * ���Ͳ���!!
	 * @param inStack
	 */
	private void getTableRef(Stack inStack) {
		String[] str_sqls = new String[curNumberOfParameters];
		for (int i = 0; i < str_sqls.length; i++) {
			str_sqls[str_sqls.length - 1 - i] = (String) inStack.pop(); //
		}
		CommUCDefineVO ucDfVO = new CommUCDefineVO("����"); //��ǰд���˱��Ͳ���,�ǲ��Ե�!!!
		for (int i = 0; i < str_sqls.length; i++) {
			ucDfVO.setConfValue("SQL���" + (i + 1), str_sqls[i]); //
		}
		inStack.push(ucDfVO); //�����ջ!!
	}

	/**
	 * ���Ͳ���
	 * @param inStack
	 */
	private void getTreeRef(Stack inStack) {
		Object param_1 = inStack.pop(); //ParentField
		Object param_2 = inStack.pop(); //ID
		Object param_3 = inStack.pop(); //SQL

		String str_sql = (String) param_3; //
		String str_pkField = (String) param_2; //
		String str_parentPKfield = (String) param_1; //

		CommUCDefineVO ucDfVO = new CommUCDefineVO("���Ͳ���"); //
		ucDfVO.setConfValue("SQL���", str_sql); //
		ucDfVO.setConfValue("PKField", str_pkField); //
		ucDfVO.setConfValue("ParentPKField", str_parentPKfield); //

		inStack.push(ucDfVO); //�����ջ!!
	}

	/**
	 * ��ѡ����
	 */
	private void getMultiRef(Stack inStack) {
		Object param_1 = inStack.pop();
		String str_sql = (String) param_1; //
		CommUCDefineVO ucDfVO = new CommUCDefineVO("��ѡ����"); //
		ucDfVO.setConfValue("SQL���", str_sql); //
		inStack.push(ucDfVO); //�����ջ!!
	}

	/**
	 * �Զ������
	 * @param inStack
	 */
	private void getCustRef(Stack inStack) {
		String[] str_pa = new String[curNumberOfParameters - 1];
		for (int i = str_pa.length - 1; i >= 0; i--) { //�����ú�������
			str_pa[i] = (String) inStack.pop();
		}
		HashMap sysRefMap = new HashMap(); //
		sysRefMap.put("����ѡ��", "cn.com.infostrategy.ui.sysapp.refdialog.CommonCorpDeptRefDialog"); //
		sysRefMap.put("��¼��Աֱ������", "cn.com.infostrategy.ui.sysapp.corpdept.LoginUserDirtDeptRefDialog"); //
		sysRefMap.put("�������ڷ�Χѡ��", "cn.com.infostrategy.ui.sysapp.refdialog.CommonDateTimeRefDialog"); //
		sysRefMap.put("��̬�붯̬�ű�����", "cn.com.infostrategy.ui.sysapp.runtime.RunTimeActionEditRefDialog"); //

		String str_classname = (String) inStack.pop();//��һ��������,
		if (str_classname.indexOf(".") < 0) { //���û�ж���,����������,���������ض��Ĳ���
			str_classname = (String) sysRefMap.get(str_classname); //ת��һ��
		}
		CommUCDefineVO ucDfVO = new CommUCDefineVO("�Զ������"); //
		ucDfVO.setConfValue("�Զ�������", str_classname); //
		for (int i = 0; i < str_pa.length; i++) { //
			ucDfVO.setConfValue("����" + (i + 1), str_pa[i]); //
		}
		inStack.push(ucDfVO); //�����ջ!!
	}

	/**
	 * �б�ģ�����!!!
	 * @param inStack
	 */
	private void getListTempletRef(Stack inStack) {
		String[] str_pa = new String[curNumberOfParameters]; //�������в����б�
		for (int i = str_pa.length - 1; i >= 0; i--) { //�����ú�������
			str_pa[i] = (String) inStack.pop();
		}

		CommUCDefineVO ucDfVO = new CommUCDefineVO("�б�ģ�����"); //
		if (str_pa.length == 1) { //���ֻ��һ������
			String str_templetcode = str_pa[0]; //
			ucDfVO.setConfValue("ģ�����", str_templetcode); //
			inStack.push(ucDfVO); //�����ջ!!
		} else if (str_pa.length == 5) { //�����5������!!
			String str_templetCode = str_pa[0]; //ģ�����
			String str_idField = str_pa[1]; //����ID
			String str_nameField = str_pa[2]; //����Name
			String str_refCustCondition = str_pa[3]; //�����Զ�������
			String str_parameters = str_pa[4]; //���ò�����
			ucDfVO.setConfValue("ģ�����", str_templetCode); //
			if (str_idField != null && !str_idField.trim().equals("")) {
				ucDfVO.setConfValue("ID�ֶ�", str_idField); //
			}
			if (str_nameField != null && !str_nameField.trim().equals("")) {
				ucDfVO.setConfValue("NAME�ֶ�", str_nameField); //
			}
			if (str_refCustCondition != null && !str_refCustCondition.trim().equals("")) {
				ucDfVO.setConfValue("����SQL����", str_refCustCondition); //	
			}
			if (str_parameters != null && !str_parameters.trim().equals("")) { //��󻹿����в���!!
				String[] parameters = new TBUtil().split(str_parameters, ";"); //�Էֺŷָ�!!
				for (int i = 0; i < parameters.length; i++) {
					try {
						int li_pos = parameters[i].indexOf("="); //
						String str_key = parameters[i].substring(0, li_pos); //
						String str_value = parameters[i].substring(li_pos + 1, parameters[i].length()); //
						ucDfVO.setConfValue(str_key, str_value); //����!!������:���Զ�ѡ=Y,�Զ���ѯ=Y
					} catch (Exception ex) {
						System.err.println("�����б�ģ����չ�ʽ���������쳣:" + ex.getClass().getName() + ":" + ex.getMessage()); //
					}
				}
			}
			inStack.push(ucDfVO); //�����ջ!!
		} else {
			System.err.println("ִ�б��Ͳ��ն��幫ʽʱ�������󣬲����ĸ������ԣ�ֻ�ܽ���1����5��,������" + str_pa.length + "������"); //
		}
	}

	/**
	 * ����ģ�����,����������,Ҳ������Ҫ�Ż��Ĳ���!!
	 * @param inStack
	 */
	private void getTreeTempletRef(Stack inStack) {
		String[] str_pa = new String[curNumberOfParameters]; //�������в����б�
		for (int i = str_pa.length - 1; i >= 0; i--) { //�����ú�������
			str_pa[i] = (String) inStack.pop();
		}

		CommUCDefineVO ucDfVO = new CommUCDefineVO("����ģ�����"); //
		if (str_pa.length == 1) { //���ֻ��һ������!!
			ucDfVO.setConfValue("ģ�����", (String) str_pa[0]); //
			inStack.push(ucDfVO); //�����ջ!!
		} else if (str_pa.length == 5) { //�����5������!
			String str_templetcode = (String) str_pa[0]; //ģ�����
			String str_idField = (String) str_pa[1]; //����id���ֶ���
			String str_nameField = (String) str_pa[2]; //�������Ƶ��ֶ���
			String str_queryCondition = (String) str_pa[3]; //
			String str_propdefine = (String) str_pa[4]; //�������ԣ����磺"���Զ�ѡ=N;ֻ��ѡҶ��=N;ֻ��ǰ����=0;"

			ucDfVO.setConfValue("ģ�����", str_templetcode); //
			if (str_idField != null && !str_idField.trim().equals("")) {
				ucDfVO.setConfValue("ID�ֶ�", str_idField); //
			}
			if (str_nameField != null && !str_nameField.trim().equals("")) {
				ucDfVO.setConfValue("NAME�ֶ�", str_nameField); //
			}
			if (str_queryCondition != null && !str_queryCondition.trim().equals("")) {
				ucDfVO.setConfValue("����SQL����", str_queryCondition); //
			}

			//���������Զ���
			if (str_propdefine != null && !str_propdefine.trim().equals("")) {
				String[] str_items = new TBUtil().split(str_propdefine, ";"); //�Ȱ��ֺŲ�
				for (int i = 0; i < str_items.length; i++) {
					int li_pos = str_items[i].indexOf("="); //
					if (li_pos > 0) { //����еȺŷָ�,
						String str_key = str_items[i].substring(0, li_pos).trim(); //
						String str_value = str_items[i].substring(li_pos + 1, str_items[i].length()).trim(); //
						ucDfVO.setConfValue(str_key, str_value); //���ø�������,����:���Զ�ѡ=N;ֻ��ѡҶ��=N;ֻ��ǰ����=0;
					}
				}
			}
			inStack.push(ucDfVO); //�����ջ!!
		} else {
			System.err.println("ִ�����Ͳ��ն��幫ʽʱ�������󣬲����ĸ������ԣ�ֻ�ܽ���1����5��,������" + str_pa.length + "������"); //
		}

	}

	/**
	 * ע���������!!
	 * @param inStack
	 */
	private void getRegFormatRef(Stack inStack) {
		CommUCDefineVO ucDfVO = new CommUCDefineVO("ע���������"); // 
		if (curNumberOfParameters == 1) {
			ucDfVO.setConfValue("ע�����", (String) inStack.pop()); //
			ucDfVO.setConfValue("���", "0"); //
		} else if (curNumberOfParameters == 2) {
			Object param_2 = inStack.pop();
			Object param_1 = inStack.pop();
			ucDfVO.setConfValue("ע�����", (String) param_1); //
			ucDfVO.setConfValue("����������", (String) param_2); //
			ucDfVO.setConfValue("���", "0"); //
		} else if (curNumberOfParameters == 3) {
			Object param_3 = inStack.pop();
			Object param_2 = inStack.pop();
			Object param_1 = inStack.pop(); //
			ucDfVO.setConfValue("ע�����", (String) param_1); //
			ucDfVO.setConfValue("����������", (String) param_2); //
			ucDfVO.setConfValue("���", (String) param_3); //һ�������ַ��,������/����/��ҳǩ!!
		}
		inStack.push(ucDfVO); //�����ջ!!
	}

	/**
	 * ע�����!!
	 * @param inStack
	 */
	private void getRegisterRef(Stack inStack) {
		Object param_1 = inStack.pop();
		String str_regrefname = (String) param_1; //
		CommUCDefineVO ucDfVO = new CommUCDefineVO("ע�����"); // 
		ucDfVO.setConfValue("ע������", str_regrefname); //
		inStack.push(ucDfVO); //�����ջ!!
	}

	/**
	 * Excel����!!
	 * @param inStack
	 */
	private void getExcelRef(Stack inStack) {
		CommUCDefineVO ucDfVO = new CommUCDefineVO("Excel�ؼ�"); //
		if (curNumberOfParameters == 1) {
			Object str_excelcode = inStack.pop(); //
			if (str_excelcode instanceof HashMap) {
				ucDfVO.setConfValueAll((HashMap) str_excelcode); //
			} else {
				ucDfVO.setConfValue("ģ�����", (String) str_excelcode); //
			}
		} else if (curNumberOfParameters == 2) {
			String str_ifc = (String) inStack.pop(); //
			String str_excelcode = (String) inStack.pop(); //
			ucDfVO.setConfValue("ģ�����", str_excelcode); //
			ucDfVO.setConfValue("��ʼ������", str_ifc); //
		} else if (curNumberOfParameters == 3) {
			String str_custbtnPaenl = (String) inStack.pop(); //
			String str_ifc = (String) inStack.pop(); //
			String str_excelcode = (String) inStack.pop(); //
			ucDfVO.setConfValue("ģ�����", str_excelcode); //
			ucDfVO.setConfValue("��ʼ������", str_ifc); //
			ucDfVO.setConfValue("��ť�Զ������", str_custbtnPaenl); //
		} else if (curNumberOfParameters == 4) {
			String str_returnCellKey = (String) inStack.pop(); //
			String str_custbtnPaenl = (String) inStack.pop(); //
			String str_ifc = (String) inStack.pop(); //
			String str_excelcode = (String) inStack.pop(); //
			ucDfVO.setConfValue("ģ�����", str_excelcode); //
			ucDfVO.setConfValue("��ʼ������", str_ifc); //
			ucDfVO.setConfValue("��ť�Զ������", str_custbtnPaenl); //
			ucDfVO.setConfValue("������ʾֵ", str_returnCellKey); //
		} else {
			System.err.println("ִ�����Ͳ��ն��幫ʽʱ�������󣬲����ĸ������ԣ�ֻ�ܽ���1-4��,������" + curNumberOfParameters + "������"); //
		}
		inStack.push(ucDfVO); //�����ջ!!
	}

	/**
	 * Office�ؼ�!!!
	 * @param inStack
	 */
	private void getOfficeRef(Stack inStack) {
		CommUCDefineVO ucDfVO = new CommUCDefineVO("Office�ؼ�"); //
		if (this.curNumberOfParameters == 1) { //�����һ����������ָ����doc����xls��
			String str_filetype = (String) inStack.pop(); //
			ucDfVO.setConfValue("�ļ�����", str_filetype); //
		} else if (this.curNumberOfParameters == 2) { //�����������������һ����doc/xls,�ڶ�����ģ���ļ���
			String str_filetempletname = (String) inStack.pop(); //
			String str_filetype = (String) inStack.pop(); //
			ucDfVO.setConfValue("�ļ�����", str_filetype); //
			ucDfVO.setConfValue("ģ���ļ���", str_filetempletname); //���û���ҵ��ļ�,���һ�μ���,���Զ���ģ��,Ĭ����blank.doc
		} else if (this.curNumberOfParameters == 3) { //�����������������һ����doc/xls,�ڶ�����ģ���ļ�,��������ǩ��������������
			String str_bookmarcreater = (String) inStack.pop(); //�õ�ĳ���˵�������,��ҪΪ�˷��������OFFICE�ؼ����ɱ�����
			String str_filetempletname = (String) inStack.pop(); //
			String str_filetype = (String) inStack.pop(); //			
			ucDfVO.setConfValue("�ļ�����", str_filetype); //
			ucDfVO.setConfValue("ģ���ļ���", str_filetempletname); //���û���ҵ��ļ�,���һ�μ���,���Զ���ģ��,Ĭ����blank.doc
			ucDfVO.setConfValue("��ǩ������", str_bookmarcreater); //
		} else if (this.curNumberOfParameters == 4) { //�����4����������һ����doc/xls,�ڶ�����ģ���ļ�,��������ǩ������������,���ĸ��ؼ�����,������/ǧ����
			String str_officeActivex = (String) inStack.pop(); //office�ؼ�
			String str_bookmarcreater = (String) inStack.pop(); //��������bookMark�ĵĹ�����!!
			String str_filetempletname = (String) inStack.pop(); //ģ���ļ�����
			String str_filetype = (String) inStack.pop(); //�ļ�����
			ucDfVO.setConfValue("�ļ�����", str_filetype); //
			ucDfVO.setConfValue("ģ���ļ���", str_filetempletname); //���û���ҵ��ļ�,���һ�μ���,���Զ���ģ��,Ĭ����blank.doc
			ucDfVO.setConfValue("��ǩ������", str_bookmarcreater); //
			ucDfVO.setConfValue("�ؼ�����", str_officeActivex); //�н��/ǧ��,�Ժ���ܻ�������!
		} else if (this.curNumberOfParameters == 5) { //�����5����������һ����doc/xls,�ڶ�����ģ���ļ�,��������ǩ������������,���ĸ��ؼ�����,������/ǧ��,��5����ָ���Ƿ���ʾ���밴ť��
			Object obj_importBVisible = inStack.pop(); //�Ƿ���ʾ���밴ť
			String str_officeActivex = (String) inStack.pop(); //office�ؼ�
			String str_bookmarcreater = (String) inStack.pop(); //��������bookMark�ĵĹ�����!!
			String str_filetempletname = (String) inStack.pop(); //ģ���ļ�����
			String str_filetype = (String) inStack.pop(); //�ļ�����
			ucDfVO.setConfValue("�ļ�����", str_filetype); //
			ucDfVO.setConfValue("ģ���ļ���", str_filetempletname); //���û���ҵ��ļ�,���һ�μ���,���Զ���ģ��,Ĭ����blank.doc
			ucDfVO.setConfValue("��ǩ������", str_bookmarcreater); //
			ucDfVO.setConfValue("�ؼ�����", str_officeActivex);
			if (obj_importBVisible instanceof String) { //������ַ���!
				String str_importBVisible = (String) obj_importBVisible; //
				ucDfVO.setConfValue("�Ƿ���ʾ���밴ť", str_importBVisible); //��true/false
			} else if (obj_importBVisible instanceof java.util.HashMap) { //����Ϊ����չ����,�ɴ�ֱ�Ӹ��˸�getParAsMap()��������ֱ�ӷ��ع�ϣ��!!��û������,���ұ�Ť!
				HashMap parMap = (HashMap) obj_importBVisible; //
				ucDfVO.setConfValueAll(parMap); //ȫ������!!!����:���洢Ŀ¼,�Ƿ���ʾ���밴ť���Ȳ���!!
			}
		}
		inStack.push(ucDfVO); //�����ջ!!
	}

	/**
	 * �����ӱ�
	 * @param inStack
	 */
	private void getChildTable(Stack inStack) {
		String param_foreignkey = (String) inStack.pop(); //���
		String param_primarykey = (String) inStack.pop(); //����
		String param_billtempletcode = (String) inStack.pop(); //ģ�����
		CommUCDefineVO ucDfVO = new CommUCDefineVO("�����ӱ�"); //
		ucDfVO.setConfValue("ģ�����", param_billtempletcode); //
		ucDfVO.setConfValue("�����ֶ���", param_primarykey); //
		ucDfVO.setConfValue("��������ֶ���", param_foreignkey); //
		inStack.push(ucDfVO); //�����ջ!!
	}

	/**
	 * �����ӱ�,�︻�����,���˸о���ȫ�����������ӱ����һ��! ֻ������������һ�¾͹���,����û��Ҫ��Ū��һ���µĿؼ�!!
	 * @param inStack
	 */
	private void getChildTableImport(Stack inStack) {
		String param_foreignkey = (String) inStack.pop(); //A�����;�ָ�
		String param_primarykey = (String) inStack.pop(); //B������
		String param_primaryname = (String) inStack.pop(); //B������
		String param_billtempletcode = (String) inStack.pop(); //Bģ�����

		CommUCDefineVO ucDfVO = new CommUCDefineVO("�����ӱ�"); //
		ucDfVO.setConfValue("ģ�����", param_billtempletcode); //
		ucDfVO.setConfValue("�����ֶ���", param_primarykey); //
		ucDfVO.setConfValue("�����ֶ���ʾ��", param_primaryname); //
		ucDfVO.setConfValue("��������ֶ���", param_foreignkey); //
		inStack.push(ucDfVO); //�����ջ!!
	}

}
