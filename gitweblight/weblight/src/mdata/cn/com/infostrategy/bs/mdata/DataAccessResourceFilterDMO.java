package cn.com.infostrategy.bs.mdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;
import org.nfunk.jep.JEP;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;

/**
 * ����Ȩ��֮��Դ����
 * @author xch
 *
 */
public class DataAccessResourceFilterDMO extends AbstractDMO {

	private CommDMO commDMO = new CommDMO(); //
	private TBUtil tbUtil = new TBUtil(); //
	private Hashtable ht_funInstance = new Hashtable(); //��ϣ��..
	private JEP jepParser = new JEP();

	private Logger logger = WLTLogger.getLogger(DataAccessResourceFilterDMO.class); //

	public DataAccessResourceFilterDMO() {
		jepParser.addStandardFunctions(); // �������б�׼����!!
		jepParser.addStandardConstants(); // �������б���!!!
	}

	public HashMap fn_1(HashMap _pars) {
		filterByPolicyTree(null, "41", null);
		return null; //
	}

	/**
	 * ��������
	 * @param _resType
	 * @param _policyTreeType ����������
	 * @param _filterType ���˵�����(������;ֻ���н�ɫ����;ֻ���в���������;ͬʱ����ʽ����;ͬʱ����ʽ����)
	 * @param _objs
	 * @return
	 */
	public Object[][] filterData(String _resType, String _policyTreeType, String _filterType, Object[][] _objs) {

		return null;
	}

	/**
	 * ������Դ������л��ڽ�ɫ�Ĺ���
	 * @param _resType ��Դ����
	 * @param _objs ʵ������,�����ݲ�ѯ�������ص�����
	 * @return
	 */
	public Object[][] filterByRole(String _resType, Object[][] _objs) {
		//���ҳ���¼��Ա�����н�ɫ

		//���ҳ��ý�ɫ���Է�����Щ��Դ!

		//��һ����������Щ��Դ�Ķ��幫ʽ,��ʵ��������������һ����ִ��,����һ��booleanֵ,�����true,��ͨ���������ɱ����

		//����Ǻ������Զ����������������ѭ���е�һ����¼�е�ĳһ���ֶε�ֵ�����ú����ܿ��ܻ���ݸò��������ݿ��в��ң����ÿ����¼���ǲ�ѯ���ݿ���������ܺܵͣ���
		//Ӧ���Ȱ�����һ����ȡ������Ȼ�����ڴ���ʹ��HashMap����,���Ա��������������!!�������ܴ�������!!

		return null; ////
	}

	/**
	 * ���ݲ���������..
	 * ��������һ�����ͽṹ,���������ǳ���������,Ӧ����һ���ļ���,����������������ȥ����,Ȼ�󷵻���һѭ����������(�����ǹ��˲�)!!
	 * @param _resType
	 * @param _policyTreeType
	 * @return
	 */
	public Object[][] filterByPolicyTree(String _resType, String _policyTreeType, Object[][] _objs) {
		String str_sql = "select * from pub_dataaccess_policytree where typeid='" + _policyTreeType + "'"; //��ѯ��SQL..
		try {
			HashVO[] hvs = commDMO.getHashVoArrayAsTreeStructByDS(null, str_sql, "id", "parentid", "seq", ""); //
			for (int i = 0; i < hvs.length; i++) {
				System.out.println(hvs[i].getStringValue("nodename") + ",����:" + hvs[i].getStringValue("$level"));
			}

			ArrayList firstLevelConditionList = new ArrayList(); //
			DefaultMutableTreeNode rootNode = null; //commDMO.getTreeNode(); //
			for (int i = 0; i < rootNode.getChildCount(); i++) {
				DefaultMutableTreeNode firstLevelNode = (DefaultMutableTreeNode) rootNode.getChildAt(i); //
				HashVO nodeVO = (HashVO) firstLevelNode.getUserObject(); //ȡ������
				if (nodeVO.getStringValue("nodetype", "").equals("����")) {
					firstLevelConditionList.add(firstLevelNode); //����
				} else {
					logger.error("��һ����[" + nodeVO.getStringValue("nodename") + "]�����Ͳ���[����],������Լ��,����֮."); //
				}
			}
			ArrayList filterList = new ArrayList(); //������Ҫ��!!
			getNextLevelCondition(firstLevelConditionList, filterList); //

			//�����������й�����,ȥ���������ҵ�����Ĺ��˹�ʽ������,��ִ��֮
			if (filterList.size() > 0) {
				String[] treeIds = (String[]) filterList.toArray(new String[0]); //
				String resSqlStr = "select resid from pub_dataaccess_policy_resmap where policytree_id in (" + tbUtil.getInCondition(treeIds) + ") and restypeid='" + _resType + "'"; //ȡ�����п��Է��ʵ���Դ�嵥
				HashVO[] hvs_reslist = commDMO.getHashVoArrayByDS(null, resSqlStr); //
				HashSet hs_distinct = new HashSet(); //
				for (int i = 0; i < hvs_reslist.length; i++) { //��Ψһ�Թ���,��Ϊ���ܲ�ͬ�Ľ��ͬʱ�������һ����Դ!
					hs_distinct.add(hvs_reslist[i].getAttributeValue("resid")); //����
				}
				String[] str_resList = (String[]) hs_distinct.toArray(new String[0]); //
				String str_resformulaSQL = "select * from pub_dataaccess_resformula where resid in (" + tbUtil.getInCondition(str_resList) + ") order by resid,seq"; //��һ����ȡ�����й�ʽ,����Ϊ���������!
				HashVO[] hvs_resFormula = commDMO.getHashVoArrayByDS(null, str_resformulaSQL); //��������Դ����!��ִ�й�ʽ,�����������ִ��Ӧ����һ���߼�!!
				for (int i = 0; i < str_resList.length; i++) {
					ArrayList resFormulaList = new ArrayList(); //
					for (int j = 0; i < hvs_resFormula.length; j++) {
						if (hvs_resFormula[j].getStringValue("resid").equals(str_resList[i])) {
							resFormulaList.add(hvs_resFormula[j]); //
						}
					}
					HashVO[] hvs_resFormula_one = (HashVO[]) resFormulaList.toArray(new HashVO[0]); //ĳһ����Դ�ľ��幫ʽ����
					long[] ll_indexs = getPassedIndex(_objs, hvs_resFormula_one); //������Դ��ʽ���巵��
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
		return null;
	}

	/**
	 * ����ĳһ�������,ȡ����һ�������(���߹��˲�)
	 * @param _listCondition
	 * @param _filterList
	 * @return
	 */
	private void getNextLevelCondition(ArrayList _listCondition, ArrayList _filterList) {
		if (_listCondition.size() == 0) { //�ߵ��Ҳ��������ͷ���
			return;
		}

		ArrayList returnList = new ArrayList(); //
		for (int i = 0; i < _listCondition.size(); i++) { //����ÿһ������
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) _listCondition.get(i); //ȡ�ý��
			HashVO nodeVO = (HashVO) node.getUserObject(); //��������..
			String str_result = computeCondition(nodeVO); //����������ֵ.

			//���������µ������ӽ��,����������ֵ����˽��.
			DefaultMutableTreeNode[] childNodes = getOneNodeChildNodes(node); //
			boolean isMatch = false; //�Ƿ����һ��ֵ,����others�Ĵ���
			for (int j = 0; j < childNodes.length; j++) {
				HashVO childNodeVO = (HashVO) childNodes[j].getUserObject(); //�ӽ�������..
				if (childNodeVO.getStringValue("nodetype").equals("��������ֵ")) { //������Ͳ���"��������ֵ"..
					logger.error("���[" + childNodeVO.getStringValue("nodename") + "]�����Ͳ���[��������ֵ],������Լ��,����֮."); //
					continue; //����
				}
				String str_caseValue = childNodeVO.getStringValue("casevalue"); //ʵ��ֵ.
				if (compareResultAndCaseValue(str_result, str_caseValue)) { //���������!!!!!!!!!!!!!!!!!!!!������Ҫ,Ӧ�����߶��ǿ��Դ��ֺŵ�һά����!�������ķ�ʽ!
					if (!str_caseValue.equals("@ȫ��")) { //������������ַ���@ȫ��,����������������!!!
						isMatch = true; //���ϱ�Ǳ�ʾ�ҵ���һ��!
					}
					DefaultMutableTreeNode[] subChildNodes = getOneNodeChildNodes(childNodes[j]); //�ҳ��������ӽ��,����һ��������������߹�����
					for (int k = 0; k < subChildNodes.length; k++) {
						HashVO hvo_subchild = (HashVO) subChildNodes[k].getUserObject(); //
						String str_notetype = hvo_subchild.getStringValue("nodetype"); //ȡ�øý�������
						if (str_notetype.equals("������")) {
							_filterList.add(hvo_subchild.getStringValue("id")); //����ǹ�����,��ֱ�Ӽ���������б�!
						} else if (str_notetype.equals("����")) { //���������,���������б��м���!
							returnList.add(subChildNodes[k]); //
						} else {
							logger.error("���[" + hvo_subchild.getStringValue("nodename") + "]�����ͼȲ���[����]Ҳ����[������],������Լ��,����֮."); //
						}
					}
					String str_isbreak = childNodeVO.getStringValue("isbreak"); //�Ƿ�break,�����break�򲻼�����������
					if (str_isbreak.equals("Y")) {
						break; //
					}
				}

				if (j == childNodes.length - 1 && !isMatch && (str_caseValue.equals("@����") || str_caseValue.equals("@����"))) { //��������һ�����,��ǰ����ֶ�û����,�Ҹý���ֵ�ֽ�"@����"

				}
			}
		}
		getNextLevelCondition(returnList, _filterList); //�ٴε����Լ�,�γɵݹ�,ֱ����û���ҵ��µ�����Ȼ���˳�!!
	}

	/**
	 * �Ƚ���������ֵ��case��ֵ!!!!
	 * @param _conResult
	 * @param _caseValue
	 * @return
	 */
	private boolean compareResultAndCaseValue(String _conResult, String _caseValue) {
		if (_caseValue.equals("@ȫ��")) { //����������ַ�@ȫ��,��ֱ�ӷ���true.
			return true; ////
		}
		String[] str_items_1 = tbUtil.split(_conResult, ";"); //
		String[] str_items_2 = tbUtil.split(_caseValue, ";"); //
		boolean isContain = tbUtil.containTwoArrayCompare(str_items_1, str_items_2); //�Ƿ����..
		return isContain; //
	}

	/**
	 * ȡ��ĳһ�����������ӽ��
	 * @param _node
	 * @return
	 */
	private DefaultMutableTreeNode[] getOneNodeChildNodes(DefaultMutableTreeNode _node) {
		int li_count = _node.getChildCount(); //
		DefaultMutableTreeNode[] childNodes = new DefaultMutableTreeNode[li_count]; //
		for (int i = 0; i < childNodes.length; i++) {
			childNodes[i] = (DefaultMutableTreeNode) _node.getChildAt(i); //
		}
		return childNodes;
	}

	/**
	 * ���������ķ��ؽ��,�������ļ����߼�!!
	 * @return
	 */
	private String computeCondition(HashVO _conditionVO) {
		try {
			String str_conType = _conditionVO.getStringValue("isconditiontype"); //
			if (str_conType.equals("N")) {
				String str_switchfunname = _conditionVO.getStringValue("switchfunname"); //
				String str_fnValue = getFunctionReturnValue(str_switchfunname); //���������,һ���Ǻ������Զ���,��û�к����!!����ֱ�Ӽ���!!!
			} else {
				HashVO[] hvs_cons = commDMO.getHashVoArrayByDS(null, "select * from pub_dataaccess_policytree_con where policytree_id='" + _conditionVO.getStringValue("id") + "' order by seq"); //
				for (int i = 0; i < hvs_cons.length; i++) { //����!!!

				}
			}
			return "��"; //
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * ������Դ��ʽ����,һ�����жϸ��������Ƿ�ͨ��
	 * @param _objs
	 * @param _resFormula
	 * @return
	 */
	private long[] getPassedIndex(Object[][] _objs, HashVO[] _resFormula) {
		Hashtable ht_index = null; //�洢��ά����itemKey������λ�õĹ�ϣ��!!
		for (int i = 0; i < _objs.length; i++) { //����һ������¼
			boolean[] bo_passed = new boolean[_resFormula.length]; //
			for (int j = 0; j < _resFormula.length; j++) {
				String str_funName = _resFormula[i].getStringValue("custfunname"); //������
				String str_comparetype = _resFormula[i].getStringValue("comparetype"); //�ȽϷ�
				String str_comparevalue = _resFormula[i].getStringValue("comparevalue"); //�Ƚ�ֵ

				if (str_funName.indexOf("{") > 0) { //����������к����,��Ҫ�����滻����
					str_funName = convertFunByReplaceMac(str_funName, ht_index, _objs[i]); //
				}
				if (str_comparevalue.indexOf("{") > 0) { //����������к����,��Ҫ�����滻����
					str_comparevalue = convertFunByReplaceMac(str_comparevalue, ht_index, _objs[i]); //
				}

				bo_passed[j] = dealPassCompare(str_funName, str_comparetype, str_comparevalue); //����������Ƚ�ֵ���бȽ�!!
			}

			//������ϼ���
			StringBuffer sb_span = new StringBuffer(); //
			for (int j = 0; j < _resFormula.length; j++) {
				if (_resFormula[j].getStringValue("andor") != null) {
					sb_span.append(_resFormula[j].getStringValue("andor") + " "); //
				}

				if (_resFormula[j].getStringValue("leftbrack") != null) { //������
					sb_span.append(_resFormula[j].getStringValue("leftbrack") + " "); //
				}

				sb_span.append(bo_passed[j] + " "); //���벼��ֵ

				if (_resFormula[j].getStringValue("rightbrack") != null) { //������
					sb_span.append(_resFormula[j].getStringValue("rightbrack") + " "); //
				}
			}

			jepParser.parseExpression(sb_span.toString()); // ִ�й�ʽ
			Object obj = jepParser.getValueAsObject(); //
		}

		return null; //

	}

	/**
	 * ת��������,
	 * @param _oldFunName
	 * @param _mapIndex
	 * @param _obj
	 * @return
	 */
	private String convertFunByReplaceMac(String _oldFunName, Hashtable _mapIndex, Object[] _obj) {
		String str_funName = _oldFunName; //
		try {
			String[] str_maclist = tbUtil.getFormulaMacPars(str_funName, "{", "}"); //ȡ�����к����..
			for (int k = 0; k < str_maclist.length; k++) { //�������к����
				int li_realIndex = (Integer) _mapIndex.get(str_maclist[k]); //ȡ��ʵ��λ��
				String str_realValue = "" + _obj[li_realIndex]; //ʵ��ֵ
				str_funName = tbUtil.replaceAll(str_funName, "{" + str_maclist[k] + "}", str_realValue); //
			}
			return str_funName; //
		} catch (Exception e) {
			logger.error(e.getMessage()); //
			return null;
		}
	}

	/**
	 * �����Ľ���
	 * @param str_funName
	 * @param str_comparetype
	 * @param str_comparevalue
	 * @return
	 */
	private boolean dealPassCompare(String str_funName, String str_comparetype, String str_comparevalue) {
		String str_1 = getFunctionReturnValue(str_funName); //�����ķ���ֵ
		String str_2 = getFunctionReturnValue(str_comparevalue); //�Ƚ�ֵ�ķ���ֵ
		if (str_1 != null && str_2 != null) { //���߲�Ϊ��
			if (str_comparetype.equals("=")) {
				return str_1.equals(str_2); //�Ƚ�����!!
			} else if (str_comparetype.equals("!=")) {
				return !str_1.equals(str_2); //�Ƚ�����!!
			} else if (str_comparetype.equals(">=")) {
				return str_1.equals(str_2); //�Ƚ�����!!
			} else if (str_comparetype.equals("<=")) {
				return str_1.equals(str_2); //�Ƚ�����!!
			} else if (str_comparetype.equals("like")) {
				if (str_1.indexOf(str_2) >= 0) {
					return true;
				}
			} else if (str_comparetype.equals("in")) {
				String[] str_items_2 = tbUtil.split(str_2, ";"); //
				for (int i = 0; i < str_items_2.length; i++) {
					if (str_items_2[i].equals(str_1)) {
						return true; //
					}
				}
			} else if (str_comparetype.equals("not in")) { //��������
				String[] str_items_2 = tbUtil.split(str_2, ";"); //
				for (int i = 0; i < str_items_2.length; i++) {
					if (str_items_2[i].equals(str_1)) {
						return false; //
					}
				}
				return true; //���һ��û����,�򷵻�true!!
			} else if (str_comparetype.equals("contain")) { //���߶�����������
				String[] str_items_1 = tbUtil.split(str_1, ";"); //
				String[] str_items_2 = tbUtil.split(str_2, ";"); //
				return tbUtil.containTwoArrayCompare(str_items_1, str_items_2);
			}
		}

		return false;
	}

	/**
	 * ȡ��һ�������ķ���ֵ.....
	 * @return
	 */
	private String getFunctionReturnValue(String _fnName) {
		String str_funName = _fnName; //
		if (str_funName.indexOf("(") > 0 && str_funName.trim().endsWith(")")) { //�����������˵���Ǻ�����ֱ������!!
			String str_fn = str_funName.substring(0, str_funName.indexOf("(")); //�����ĺ�����..
			String str_pars = str_funName.substring(str_funName.indexOf("(") + 1, str_funName.indexOf(")")).trim(); //�ҳ�()�ڵ�����,�����в���!!!
			String[] str_parItems = tbUtil.split(str_pars, ","); //�Զ��ŷָ�,�ҳ����в���!!
			for (int i = 0; i < str_parItems.length; i++) {
				str_parItems[i] = str_parItems[i].trim(); //
				if (str_parItems[i].startsWith("\"")) { //�����˫���ſ�ͷ,��ɾ��֮!
					str_parItems[i] = str_parItems[i].substring(1, str_parItems[i].length()); //
				}
				if (str_parItems[i].endsWith("\"")) { //�����˫���Ž�β,��ɾ��֮!
					str_parItems[i] = str_parItems[i].substring(0, str_parItems[i].length() - 1); //
				}
			}

			String str_className = null; //
			if (str_fn.indexOf(".") > 0) { //����ж���,��˵����ֱ�Ӷ��������
				str_className = str_fn; //
			} else { //��֮�Ǻ�����,��ע����ĺ���!!
				str_className = getClassNameByFnName(str_fn); //������Class��
			}

			DataAccessFunctionIFC funObject = null; //��������
			try {
				if (ht_funInstance.containsKey(str_className)) { //���������������,
					funObject = (DataAccessFunctionIFC) ht_funInstance.get(str_className); //
				} else {
					funObject = (DataAccessFunctionIFC) Class.forName(str_className).newInstance(); //
					ht_funInstance.put(str_className, funObject); //���뻺��!!!
				}
			} catch (Exception e) {
				logger.error("�������ݹ��˺�����[" + str_className + "]ʧ��,ԭ��:" + e.getMessage()); //////
			}

			if (funObject != null) { //����ɹ��Ĵ����˶���!!!
				String str_fnReturnValue = funObject.getFunValue(str_parItems); ////
				return str_fnReturnValue;
			} else {
				return null;
			}
		} else {
			return _fnName;
		}
	}

	/**
	 * ��ע�����ҵ�ĳ��������ʵ�ֵ�����!!
	 * @param _fnName
	 * @return
	 */
	private String getClassNameByFnName(String _fnName) {
		for (int i = 0; i < DataAccessFunctionIFC.regFunctions.length; i++) {
			if (DataAccessFunctionIFC.regFunctions[i][0].equals(_fnName)) {
				return DataAccessFunctionIFC.regFunctions[i][1]; //��������������
			}
		}
		return null; //
	}

	private void execFormula() {
		jepParser.parseExpression("if(((1==1) && (1==2)),\"true\",\"false\")"); // ִ�й�ʽ
		Object obj = jepParser.getValueAsObject(); //
		System.out.println(obj);  //
	}

	public static void main(String[] _args) {
		new DataAccessResourceFilterDMO().execFormula();
	}

}
