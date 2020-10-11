package cn.com.infostrategy.to.mdata.formulaEngine;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.nfunk.jep.ASTConstant;
import org.nfunk.jep.ASTFunNode;
import org.nfunk.jep.ASTVarNode;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.function.PostfixMathCommandI;

import cn.com.infostrategy.bs.sysapp.install.InstallDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.formulaEngine.jepFunctions.AbstractPostfixMathCommand;
import cn.com.infostrategy.to.mdata.formulaEngine.jepFunctions.Avg;
import cn.com.infostrategy.to.mdata.formulaEngine.jepFunctions.Excel;
import cn.com.infostrategy.to.mdata.formulaEngine.jepFunctions.GetHashVOByTable;
import cn.com.infostrategy.to.mdata.formulaEngine.jepFunctions.GetSqlInCondition;
import cn.com.infostrategy.to.mdata.formulaEngine.jepFunctions.IF;
import cn.com.infostrategy.to.mdata.formulaEngine.jepFunctions.MatchByKey;
import cn.com.infostrategy.to.mdata.formulaEngine.jepFunctions.createMap;
import cn.com.infostrategy.to.mdata.jepfunctions.AfterDate;
import cn.com.infostrategy.to.mdata.jepfunctions.FillBlank;
import cn.com.infostrategy.to.mdata.jepfunctions.GetCurrentDBDate;
import cn.com.infostrategy.to.mdata.jepfunctions.GetCurrentDBTime;
import cn.com.infostrategy.to.mdata.jepfunctions.GetCurrentTime;
import cn.com.infostrategy.to.mdata.jepfunctions.GetDateDifference;
import cn.com.infostrategy.to.mdata.jepfunctions.GetMapStrItemValue;
import cn.com.infostrategy.to.mdata.jepfunctions.GetParAsMap;
import cn.com.infostrategy.to.mdata.jepfunctions.GetRandom;
import cn.com.infostrategy.to.mdata.jepfunctions.GetTimePart;
import cn.com.infostrategy.to.mdata.jepfunctions.IndexOf;
import cn.com.infostrategy.to.mdata.jepfunctions.Length;
import cn.com.infostrategy.to.mdata.jepfunctions.ReplaceAll;
import cn.com.infostrategy.to.mdata.jepfunctions.ReplaceAllAndSubString;
import cn.com.infostrategy.to.mdata.jepfunctions.SubString;
import cn.com.infostrategy.to.mdata.jepfunctions.ToNumber;
import cn.com.infostrategy.to.mdata.jepfunctions.ToString;
import cn.com.infostrategy.ui.common.UIUtil;

/**
 *���Ը����ӹ�ʽ����һ���������ͣ������ڼ���֮ǰ�������е��õ������Ӽ��뻺���С�
 * 
 * @author haoming create by 2013-6-24
 */
public class SalaryFomulaParseUtil {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8108164280696769371L;

	protected JEP parser = new JEP(); // һ��������¼���㣬ֻʵ����һ��jep�����򻺴��jep�Դ������������⡣

	private Object inputObj; // ��ʱû���õ���

	private TBUtil tbutil = new TBUtil();

	private HashMap factorMap = new HashMap();

	private HashMap tableXmlMap = new HashMap();

	private HashMap factorCalcValue = new HashMap();//��������ִ�й���Ļ���ֵ�� 

	public HashMap factorCalVar = new HashMap(); //��¼��������У��������ƶ�Ӧ��parse�еı�����

	private HashVO hisbaseDataHashVO = null; //onexcute���������ǣ��������Ļ���ֵ�仯�ˡ� ��Ҫ�������еĻ��棬Ĭ�϶�ȡExcel������

	private int clientOrServer = TBUtil.getTBUtil().getJVSite() == TBUtil.JVMSITE_CLIENT ? WLTConstants.JEPTYPE_UI : WLTConstants.JEPTYPE_BS;

	private List errorList = new ArrayList(); //���ִ���ڼ�Ĵ�����־��

	private SalaryTBUtil salaryTBUtil = new SalaryTBUtil();

	private HashMap<String, String> calc_process_msg = new HashMap<String, String>();

	public SalaryFomulaParseUtil() {
		this(null);
	}

	public SalaryFomulaParseUtil(Object _obj) {
		parser.setAllowAssignment(true);
		parser.setAllowUndeclared(true);
		parser.addComplex();
		parser.setImplicitMul(true);
		initStandardFunction(); // ��ʼ����ʽ
		initCustomFunction(); // ��ʼ���Զ��幫ʽ
		inputObj = _obj;
	}

	// ��׼���ĺ���,��ֻҪ������Ĺ�ʽ����!
	protected void initStandardFunction() {
		parser.addStandardFunctions(); // �������б�׼����!!
		parser.addStandardConstants(); // �������б���!!!
	}

	private void initCustomFunction() {
		parser.addFunction("getHashVOByTable", new GetHashVOByTable(parser, inputObj)); // ����getHashVOByTable()����
		parser.addFunction("getDateDifference", new GetDateDifference()); // �õ�����ʱ���
		parser.addFunction("getCurrDBDate", new GetCurrentDBDate(clientOrServer)); // ����getCurrDate()����
		parser.addFunction("getCurrDBTime", new GetCurrentDBTime(clientOrServer)); // ����getCurrDate()����
		parser.addFunction("getCurrDate", new GetCurrentTime(GetCurrentTime.DATE)); // ����getCurrDate()����
		parser.addFunction("getCurrTime", new GetCurrentTime(GetCurrentTime.TIME)); // ����getCurrTime()����
		parser.addFunction("getYearByDateTime", new GetTimePart(GetTimePart.YEAY)); // ����ʱ��ȡ�����
		parser.addFunction("getSeasonByDateTime", new GetTimePart(GetTimePart.SEASON)); // ����ʱ��ȡ�ü���
		parser.addFunction("getMonthByDateTime", new GetTimePart(GetTimePart.MONTH)); // ����ʱ��ȡ���¶�
		parser.addFunction("getDateByDateTime", new GetTimePart(GetTimePart.DATE)); // ����ʱ��ȡ������
		parser.addFunction("afterDate", new AfterDate()); // ����getCurrDate()����
		parser.addFunction("toString", new ToString()); // ����toString����
		parser.addFunction("toNumber", new ToNumber()); // ����toNumber����
		parser.addFunction("getMapStrItemValue", new GetMapStrItemValue()); // �ӹ�ϣ�����͵��ַ�����ȡ�ö�Ӧkey��value
		parser.addFunction("indexOf", new IndexOf()); // ���������ַ����и������ַ����ַ���
		parser.addFunction("subString", new SubString());// ��ȡ�ַ���
		parser.addFunction("fillBlank", new FillBlank());// ���ո�
		parser.addFunction("length", new Length());// 
		parser.addFunction("replaceall", new ReplaceAll());// �滻�ַ����е�����
		parser.addFunction("replaceallandsubString", new ReplaceAllAndSubString());// �滻�ַ����е����ݲ���ȡ�ַ���
		parser.addFunction("getRandom", new GetRandom()); // ȡ������� ����+���ʮλ��
		parser.addFunction("getParAsMap", new GetParAsMap()); // ȡ������� ����+���ʮλ��
		parser.addFunction("ƥ��", new MatchByKey(parser));
		parser.addFunction("if", new IF(parser));
		parser.addFunction("excel", new Excel(parser, inputObj, this, null));
		parser.addFunction("avg", new Avg(parser));
		parser.addFunction("createMap", new createMap(parser, inputObj, this, null));
		parser.addFunction("getSqlInCondition", new GetSqlInCondition(parser, inputObj, this, null));
		//��Ҫ��һ��group�ķ������ܹ����excel���ܣ���groupһ�£������Էŵ�map�С����Դ���������ܡ�*****
	}

	/*
	 *�� DuZi(�������,��ô����ֵ,�������,��ô����,.....,�������,��ô����,������������);
	 *������  index=0��������Ĺ�ʽ,1=����ֵ�Ĺ�ʽ,2��������Ĺ�ʽ,3����ֵ....
	 */
	public static List parseDuZi(String _express) {
		int yh = 0; //����ƥ�����
		int kh = 0; //����
		List rtList = new ArrayList<String>();
		_express = _express.substring(_express.indexOf("("));
		char cs[] = _express.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < cs.length; i++) {
			if (cs[i] != '\"' && cs[i] == '"') { //Ϊ���ж϶����Ƿ����ַ����ڲ�
				if (yh == 0) {
					yh++;
				} else {
					yh--;
				}
			} else if (cs[i] == ',') {
				if (yh == 0 && kh == 1) { //���������������Ķ��ţ�˵���Ƿָ���
					rtList.add(sb.toString());
					sb.delete(0, sb.length());
				} else {
					sb.append(cs[i]);
				}
			} else if (cs[i] == '(' && yh == 0) {
				kh++;
				if (kh > 1) {
					sb.append(cs[i]);
				}
			} else if (cs[i] == ')' && yh == 0) {
				kh--;
				if (kh >= 1) {
					sb.append(cs[i]);
				}
			} else {
				sb.append(cs[i]);
			}
			if (kh == 0) { //��β�ˡ�
				rtList.add(sb.toString());
				break;
			}
		}
		return rtList;
	}

	/**
	 * ֱ�ӵ��ù�ʽ����
	 * @param _formulaStr
	 * @return
	 * @throws Exception
	 */

	public Object execFormula(String _formulaStr) throws Exception {
		return execFormula(_formulaStr, null);
	}

	public Object execFormula(String _formulaStr, Object _inputObject) throws Exception {
		return this.execFormula(_formulaStr, _inputObject, null);
	}

	protected Object execFormula(String _formulaStr, Object _inputObject, Object _conditionObject) throws Exception {
		if (_formulaStr != null && _formulaStr.trim().toLowerCase().indexOf("select ") == 0 && _formulaStr.toLowerCase().contains(" from ")) { //�����sql,Ŀǰ����Сд
			String items = _formulaStr.substring(6, _formulaStr.indexOf("from"));
			if ((items.contains(" *") || items.contains(",")) && !items.contains("count")) {//���ж�ȡ�����ֶ�
				return salaryTBUtil.getHashVoArrayByDS(null, _formulaStr);
			} else if (items.contains("count") && items.contains(",")) {
				salaryTBUtil.getHashVoArrayByDS(null, _formulaStr);
			} else {
				return salaryTBUtil.getStringValueByDS(null, _formulaStr);
			}
		}
		parser.parseExpression(_formulaStr);
		Node node = parser.getTopNode(); // ��ȡ��ʽ�ĺ�������
		if (node instanceof ASTFunNode) { // ��������
			ASTFunNode currFunNode = (ASTFunNode) node;
			PostfixMathCommandI commandI = parser.getFunctionTable().get(currFunNode.getName()); // �õ�������
			if (commandI instanceof AbstractPostfixMathCommand) { // ���ʵ�ֵ����Զ���ķ���������ǿ���趨�������
				AbstractPostfixMathCommand customCommand = (AbstractPostfixMathCommand) commandI;
				customCommand.setInputData(_inputObject);
				customCommand.setConditionData(_conditionObject);
			}
			int child = currFunNode.jjtGetNumChildren();
			for (int i = 0; i < child; i++) {
				setAllNodeInputData(node, _inputObject);
			}
		} else if (node instanceof ASTVarNode) { // �����Լ����ܽ�����ʽ�Ƿ���ȷ�����Ի����parseExpression("����ʦ").�������ֵΪnull��
			ASTVarNode astVarnode = (ASTVarNode) node;
			if (astVarnode.getVar().getValue() == null) { // ����ǽ���������ôvar�ǿ�ֵ��Ȼ��ֱ�ӷ���Name���ɡ�
				if (astVarnode.getName().contains("_var")) {
					return null;
				}
				return astVarnode.getName();
			}
		} else if (node instanceof ASTConstant) { // �����ɽ����ɳ���
			return _formulaStr;
		}
		Object obj = null;
		try {
			obj = parser.getEvaluatorVisitor().eval(parser.getTopNode());
		} catch (Exception ex) {
			WLTLogger.getLogger(SalaryFomulaParseUtil.class).error("��ʽ:" + _formulaStr + "����", ex);
			throw ex;
		}
		return obj;
	}

	/**
	 * �����н�����Ľڵ���봫��ֵ�� 
	 * @param _node
	 * @param _inputData
	 */
	private void setAllNodeInputData(Node _node, Object _inputData) {
		if (!(_node instanceof ASTFunNode)) {
			return;
		}
		ASTFunNode currFunNode = (ASTFunNode) _node;
		PostfixMathCommandI commandI = parser.getFunctionTable().get(currFunNode.getName()); // �õ�������
		if (commandI instanceof AbstractPostfixMathCommand) { // ���ʵ�ֵ����Զ���ķ���������ǿ���趨�������
			AbstractPostfixMathCommand customCommand = (AbstractPostfixMathCommand) commandI;
			customCommand.setInputData(_inputData);
		}
		int num = _node.jjtGetNumChildren();
		for (int i = 0; i < num; i++) {
			Node childnode = _node.jjtGetChild(i);
			setAllNodeInputData(childnode, _inputData);
		}

	}

	/*
	 * _factorHashVO ����� ���Ӷ��� �������� _baseDataHashVO�����whereȡֵ���� ��ʱ�ϵ�
	 */
	/*
	private Object onExecute(HashVO _factorHashVO, HashVO _baseDataHashVO) throws Exception {
		return onExecute(_factorHashVO, _baseDataHashVO, null);
	}
	*/
	/*
	 * ���ôη���ǰ������Ҫ�ȵ��� putDefaultFactorVO����
	 * ��һ�����ݿ��в����ڵ����ӻ��߱����ŵ����㵱�С�
	 * ������� ���Ŷ���ָ��ʱ�����ݸ���ָ������ƣ��Ѷ�Ӧ��score���һ��hashvo�������������㡣
	 */
	public void putDefaultFactorValue(String _defFactorName, HashVO hvo) {
		this.putDefaultFactorValue(_defFactorName, new HashVO[] { hvo });

	}

	public void putDefaultFactorValue(String _defFactorName, HashVO[] hvo) {
		this.putDefaultFactorValue(_defFactorName, (Object) hvo);
	}

	public void putDefaultFactorValue(String _defFactorName, Object _value) {
		if (!factorMap.containsKey(_defFactorName)) { //���û��Ĭ�ϴ���Ϊϵͳ����
			HashVO factorVO = new HashVO();
			factorVO.setAttributeValue("sourcetype", "ϵͳ����"); // �õ�������Դ���͡�
			factorVO.setAttributeValue("value", "");// �趨��ֵ�������ǹ�ʽ��
			factorVO.setAttributeValue("conditions", ""); // ����
			factorVO.setAttributeValue("extend", "");
			factorVO.setAttributeValue("name", _defFactorName);
			factorMap.put(_defFactorName, factorVO);
		}
		factorCalcValue.put(_defFactorName, _value);
	}

	/*
	 * �Զ��幫ʽ��
	 * �Լ��ô�����ù�ʽʱ������������ͣ���ʽ���ݣ��������Ƶ�.Ȼ�����onExecute(���Զ�������)�Ϳ���ȡ����������
	 */
	public void putDefaultFactorVO(String _factorSourceType, String _factorValue, String _factorName, String _factorConditions, String _factorextend) {
		this.putDefaultFactorVO(_factorSourceType, _factorValue, _factorName, _factorConditions, _factorextend, false);
	}

	/*
	 * @param _iscache  �Ƿ񻺴棬����Ҫ��һ�㳣����ϵͳ�Եģ���һ�δ�����в���Ķ�����Ҫ���棬��������ܡ�¼����Ա�����������ñ�.
	 */

	public void putDefaultFactorVO(String _factorSourceType, String _factorValue, String _factorName, String _factorConditions, String _factorextend, boolean _iscache) {
		HashVO factorVO = new HashVO();
		factorVO.setAttributeValue("sourcetype", _factorSourceType); // �õ�������Դ���͡�
		factorVO.setAttributeValue("value", _factorValue);// �趨��ֵ�������ǹ�ʽ��
		factorVO.setAttributeValue("conditions", _factorConditions); // ����
		factorVO.setAttributeValue("extend", _factorextend);
		factorVO.setAttributeValue("name", _factorName);
		factorVO.setAttributeValue("iscache", _iscache);
		factorMap.put(_factorName, factorVO);
	}

	/*
	 * ���⿪�ŵĵ��÷��������ڴ˷��������û��������ݣ����Թ�ʽ������ڲ��߼�һ����Ҫ���ôκ���
	 * _baseDataHashVO ��hashvo�ᴫ���������ù��������У�ͨ��[��������.key]��key����hashvo��key����ȡ��
	 * ͨ�����빹��_baseDataHashVO��һ�㴫��ָ���������Ϣ����ǰ�˻��߻����Ĳ�����Ϣ��������Ҫ�ľ��Ǵ������ڡ�
	 * �˹��߼���д��to�����棬����Ҫ��bs�˵��á�
	 */
	public Object onExecute(HashVO _factorHashVO, HashVO _baseDataHashVO, StringBuffer rtStr) throws Exception {
		if (hisbaseDataHashVO != null && hisbaseDataHashVO != _baseDataHashVO) {
			resetAllHashMapHis(false); //Ĭ�ϲ�����excel .	
		}
		hisbaseDataHashVO = _baseDataHashVO;
		try {
			Object obj = onExecute(_factorHashVO, _baseDataHashVO, 0, rtStr);
			return obj;
		} catch (Exception ex) {
			throw new Exception(ex.getMessage() + "\r\b" + rtStr);
		}
	}

	/**
	 * ÿ��ִ��onExecute����ʱ�����ж���ʷ����Ļ���Hashvo�͵�ǰ�Ƿ�һ�£�����񣬾ͰѷǱ��뻺������ӽ����ա�
	 * ���������⣬�����У�ǿ�и������趨ֵ��Ȼ����ִ��onExecute����������Ѹ��趨��ֵ������ˣ�ԭ���Ǵ����hashvo���ˡ�
	 * ���ǿ�����õ�ǰ���������ô�������⶯��֮ǰ��Ҫ�ֶ�����resetAllHashMapHis���������û��档
	 * @param _hashvo
	 */
	public void forceSetCurrBaseVO(HashVO _hashvo) {
		hisbaseDataHashVO = _hashvo;
	}

	/*
	 * ���û���
	 */
	public void resetAllHashMapHis(boolean resetAll) {
		if (factorCalcValue.size() == 0) {
			return;
		}
		for (Iterator iterator = factorMap.keySet().iterator(); iterator.hasNext();) {
			String factorName = (String) iterator.next();
			try {
				HashVO factorVo = getFoctorHashVO(factorName);
				if (resetAll) { //�����excel.��������excel.
					factorCalcValue.remove(factorName);
					calc_process_msg.remove(factorName);
				} else if (!factorVo.getBooleanValue("iscache", false)) {
					factorCalcValue.remove(factorName);
					calc_process_msg.remove(calc_process_msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * _called ��˭���á������ж����������Ƿ�ȷ���ء������jep��ʽ�е��õ����ڼ��㣬һ�㲻������ôη�����
	 */
	protected Object onExecute(HashVO _factorHashVO, HashVO _baseDataHashVO, int _level, StringBuffer rtStr) throws Exception {
		String sourceType = _factorHashVO.getStringValue("sourcetype"); // �õ�������Դ���͡�
		String value = _factorHashVO.getStringValue("value");// �趨��ֵ�������ǹ�ʽ��
		String name = _factorHashVO.getStringValue("name");
		String def = value;// ����
		if (TBUtil.getTBUtil().isEmpty(sourceType) || TBUtil.getTBUtil().isEmpty(value)) {
			return "";
		}
		Object obj = null;
		if (factorCalcValue.containsKey(name)) {
			if (_level == 0) { // ��ʼ������
				rtStr.append(calc_process_msg.get(name));
			}
			return factorCalcValue.get(name);
		}
		AbstractFomulaParse fomulaParse = null;
		if ("ϵͳ����".equals(sourceType)) {
			fomulaParse = new SystemObjectFomulaParse();
		} else if ("�ı�".equals(sourceType)) {
			fomulaParse = new TextFomulaParse();
		} else if ("����".equals(sourceType)) {
			fomulaParse = new NumberFomulaParse();
		} else if ("�������".equals(sourceType)) { // bsh����ʵ��
			fomulaParse = new BshFomulaParse();
		} else if ("Excel".equals(sourceType)) { // ������Excel,һ��ȡ���ŵ�������
			fomulaParse = new ExcelFomulaParse();
		} else if ("map".equalsIgnoreCase(sourceType)) {
			fomulaParse = new MapFomulaParse();
		} else {
			fomulaParse = new TextFomulaParse(); //fortify�������fomulaParse����ʵ����
		}
		StringBuffer cursb = new StringBuffer();
		obj = fomulaParse.parse(this, _factorHashVO, _baseDataHashVO, _level, cursb);
		if (!factorCalcValue.containsKey(name)) {
			factorCalcValue.put(name, obj);
		}
		if (!factorMap.containsKey(name)) {
			factorMap.put(name, _factorHashVO);
		}
		if (rtStr != null) {
			String st = "";
			if (obj instanceof String || obj instanceof Number) {
				if (obj instanceof Number) {
					st = new BigDecimal(String.valueOf(obj)).toString(); //by haoming 2016-01-27 double���ѧ������
				} else {
					st = String.valueOf(obj);
				}
				if (_level == 0) { // ��ʼ������
					if (!"�������".equals(sourceType)) {
						cursb.append(name + " = " + def + " = " + st + "\r\n");
					} else {
						cursb.append(name + " = " + st + "\r\n");
					}

				} else {
					for (int i = 0; i < _level; i++) {
						cursb.append("   ");
					}
					cursb.append(name + " = " + st + "\r\n");
				}
			}
			rtStr.append(cursb.toString());
			calc_process_msg.put(name, cursb.toString());
		}
		return obj;
	}

	public Object onExecute(String _factorSourceType, String _factorValue, String _factorName, String _factorConditions, String _factorextend, HashVO _baseDataHashVO, int _level, StringBuffer rtStr) throws Exception {
		HashVO factorVO = new HashVO();
		factorVO.setAttributeValue("sourcetype", _factorSourceType); // �õ�������Դ���͡�
		factorVO.setAttributeValue("value", _factorValue);// �趨��ֵ�������ǹ�ʽ��
		factorVO.setAttributeValue("conditions", _factorConditions); // ����
		factorVO.setAttributeValue("extend", _factorextend);
		factorVO.setAttributeValue("name", _factorName);
		return onExecute(factorVO, _baseDataHashVO, _level, rtStr);
	}

	int varindex = 0;

	/*
	 * �������ô�Ĺ�ʽ�滻��_varN,���ҷ���.��jepparse����_varN��ֵ.
	 */

	private Object replaceDuZiValue(String _factorStr, HashVO _baseDataHashVO, StringBuffer _rtsb, int level) throws Exception {
		List list = parseDuZi(_factorStr);
		int size = list.size();
		for (int i = 0; i < size; i++) {
			String condition = (String) list.get(i);
			if (i % 2 == 0 && i != size - 1) { //�����ż����˵����key�����Ҳ������һ��.��0,1,2,3,4,5
				Object obj = getReflectOtherFactor(condition, _baseDataHashVO, _rtsb, level + 1);
				Object b = execFormula(obj.toString(), _baseDataHashVO);
				if (b instanceof Double && (Double) b == 0) {
					i++; //���������Ծ
					continue;
				}
			}
			String value = "";
			if (i == size - 1) {//��������һ�������������
				value = (String) list.get(i);
			} else {
				value = (String) list.get(i + 1); ///�õ�ֵ�Ĺ�ʽ��Ȼ����jepִ��
			}
			Object valueReflectStr = getReflectOtherFactor(value, _baseDataHashVO, _rtsb, level + 1); //��var1��var2�滻���ֵ
			Object ooo = execFormula(String.valueOf(valueReflectStr), _baseDataHashVO); //ִ�еõ���ֵ��ֵ������
			int varindexNum = ++varindex;
			parser.addVariable("_" + "var" + varindexNum, ooo); //���������·ŵ�ooo,jep��
			return ("_" + "var" + varindexNum);
		}
		return null;
	}

	protected Object getReflectOtherFactor(String _factorStr, HashVO _baseDataHashVO, StringBuffer _rtsb, int level) throws Exception {
		return this.getReflectOtherFactor(_factorStr, _baseDataHashVO, _rtsb, level, false);
	}

	/*
	 * �˷��������滻������������ֵ���ñ�����ʽ_var1,_var�趨����ֵ����ִ�й�ʽ�� cls,String ,number
	 */
	protected Object getReflectOtherFactor(String _factorStr, HashVO _baseDataHashVO, StringBuffer _rtsb, int level, boolean _directReplace) throws Exception {
		if (_factorStr.indexOf("if(") == 0) {
			return replaceDuZiValue(_factorStr, _baseDataHashVO, _rtsb, level);
		}
		String[] str = tbutil.getMacroList(_factorStr, "[", "]");
		StringBuffer sb = new StringBuffer();
		boolean needNumCalc = false; //�Ƿ���Ҫ���ּ��㡣

		if (str == null) {
			return _factorStr;
		}
		for (int i = 0; i < str.length; i++) {
			String compare = str[i].trim();
			if (">".equals(compare) || "<".equals(compare) || "<=".equals(compare) || ">=".equals(compare) || "+".equals(compare) || "-".equals(compare) || "*".equals(compare) || "/".equals(compare)) {
				needNumCalc = true;
				break;
			}
		}
		for (int i = 0; i < str.length; i++) {
			if (str[i].contains("[")) {
				String sourceConfig = str[i].substring(1, str[i].length() - 1); //�ҵ�����õĶ�������
				String sourceConfigName = "";
				String sourceConfigItem = "";
				if (sourceConfig.indexOf(".") > 0) { //�������.��˵����ȡ��ĳһ��ֵ
					String[] sourceConfig_s = tbutil.split(sourceConfig, ".");
					sourceConfigName = sourceConfig_s[0];
					sourceConfigItem = sourceConfig_s[1];
				} else {
					sourceConfigName = sourceConfig;
				}
				Object ooo = null;
				if ("��������".equals(sourceConfigName)) { //������õ���[��������.checkeddate].��ʾֱ�ӵ��ô���ֵ��ĳһ���ֶΡ�
					if (sourceConfigItem != null && !sourceConfigItem.equals("")) {
						ooo = _baseDataHashVO.getStringValue(sourceConfigItem);
						if (needNumCalc) {
							if (ooo == null) {
								ooo = 0;
							} else if (!tbutil.isStrAllNunbers(String.valueOf(ooo))) {
								ooo = String.valueOf(ooo);
							} else {
								try {
									ooo = Double.parseDouble(String.valueOf(ooo));
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}
						} else {
							if (ooo == null) {
								ooo = 0;
							} else {
								if (tbutil.isStrAllNunbers(String.valueOf(ooo))) { //����Ǵ����֡�
									if (_factorStr.contains("+") || _factorStr.contains("-") || _factorStr.contains("*") || _factorStr.contains("/") || _factorStr.contains("=") || _factorStr.contains("<") || _factorStr.contains(">") || _factorStr.contains("!=")) {
										try {
											ooo = Double.parseDouble(String.valueOf(ooo));
										} catch (Exception ex) {
											WLTLogger.getLogger(SalaryFomulaParseUtil.class).error("SalaryFomulaParseUtil�ĵ�549������[" + ooo + "]�����ֳ���", ex);
										}
									}
								}
							}
						}
					} else {
						ooo = _baseDataHashVO;
					}
				} else {
					HashVO factorVO = getFoctorHashVO(sourceConfigName); //��ȡ��������
					if (factorVO == null) {
						_rtsb.append("" + _factorStr + " �ж����[" + sourceConfigName + "]ϵͳ�в����ڡ�����");
						throw new Exception("" + _factorStr + " �ж����[" + sourceConfigName + "]ϵͳ�в����ڡ�����");
					}
					String factorName = factorVO.getStringValue("name"); //�õ���������
					if (factorCalcValue.containsKey(factorName)) { //�ж��Ƿ��Ѿ����뻺��
						//ֱ�Ӵӻ�����ȡֵ
						ooo = factorCalcValue.get(factorName);
						for (int m = 0; m < level; m++) {
							_rtsb.append("   ");
						}
						if (ooo instanceof Number || ooo instanceof String) {
							String ps = null;
							if (ooo instanceof Number) {
								ps = new BigDecimal(String.valueOf(ooo)).toString(); //by haoming 2016-01-27 double���ѧ������
							} else {
								ps = String.valueOf(ooo);
							}
							_rtsb.append(factorName + " = " + String.valueOf(ps) + "\r\n");
						}
					} else {
						ooo = onExecute(factorVO, _baseDataHashVO, level + 1, _rtsb);
					}
					if (sourceConfigItem != null && !sourceConfigItem.equals("")) { // ���滻�ɱ�������ʽ��
						if ("Excel".equalsIgnoreCase(factorVO.getStringValue("sourcetype"))) {
							// A15:A18  
							if (sourceConfigItem.contains(":")) { //�������:,��ʾ��Ҫ���һ�������ֵ. 
								int colonIndex = sourceConfigItem.indexOf(":"); //�ҵ�ð�ŵ�λ��
								String beforeColon = ""; //ð��ǰ�ĵ�Ԫ��
								String afterColon = ""; //ð�ź�ĵ�Ԫ��
								int index = 0;
								while (true) {
									char s = sourceConfigItem.charAt(colonIndex - index);
									if ((s >= 65 && s <= 90) || (s >= 48 && s <= 57) || (s >= 97 && s <= 122)) {
										beforeColon = s + beforeColon;
									} else {
										break;
									}
									index++;
								}
								index = 0;
								while (true) {
									char s = sourceConfigItem.charAt(colonIndex + index);
									if ((s >= 65 && s <= 90) || (s >= 48 && s <= 57) || (s >= 97 && s <= 122)) {
										afterColon = s + afterColon;
									} else {
										break;
									}
									index++;
								}

								for (int j = 0; j < str.length; j++) {

								}
							} else {
							}
							// A15��AB34
							String row = "";
							String col = "";
							for (int j = 0; j < sourceConfigItem.length(); j++) {
								if ((sourceConfigItem.charAt(j) >= 'a' && sourceConfigItem.charAt(j) <= 'z') || (sourceConfigItem.charAt(j) <= 'Z' && sourceConfigItem.charAt(j) >= 'A')) {
									col = col + sourceConfigItem.charAt(j);
								} else {
									row = row + sourceConfigItem.charAt(j);
								}
							}
							int rowNum = Integer.parseInt(row); // �õ��к�
							if (ooo instanceof HashVO[]) {
								HashVO vos[] = (HashVO[]) ooo;
								if (vos.length >= rowNum && rowNum > 0) {
									ooo = vos[rowNum - 1].getStringValue(col);
									if (tbutil.isStrAllNunbers(String.valueOf(ooo)) && String.valueOf(ooo).indexOf("-") <= 0) { // ����и��š���ôֻ���ڵ�һλ��
										ooo = Double.parseDouble(String.valueOf(ooo));
									} else {
										ooo = 0;
									}
								} else {
									throw new Exception("ExcelȡֵԽ��" + sourceConfigItem);
								}
							}
						} else {
							if (ooo instanceof HashVO[]) {
								HashVO vos[] = (HashVO[]) ooo;
								HashVO oneVO = vos[0];
								if (!oneVO.containsKey(sourceConfigItem)) { //�����Ӣ�ĵ�,����hashvo����.
									String tableName = factorVO.getStringValue("value");
									if ("ϵͳ����".equals(factorVO.getStringValue("sourcetype"))) {
										String extend = factorVO.getStringValue("extend");
										if (!TBUtil.getTBUtil().isEmpty(extend)) {
											tableName = extend;
										}
									}
									sourceConfigItem = convertTableItemFromCH_to_EN(tableName, sourceConfigItem); //����,�ֶ�������
									if (tbutil.isEmpty(sourceConfigItem)) {
										throw new Exception("��ִ������[" + factorName + "]������Ķ�����û���ҵ�\"" + tbutil.split(sourceConfig, ".")[1] + "��Ӧ��Ӣ���ֶ�.����table.xml�ļ�\"");
									}
								}
								ooo = ((HashVO) vos[0]).getStringValue(sourceConfigItem);
							} else if (ooo instanceof HashVO) {
								HashVO oneVO = (HashVO) ooo;
								if (!oneVO.containsKey(sourceConfigItem)) { //�����Ӣ�ĵ�,����hashvo����.
									String tableName = factorVO.getStringValue("value");
									if ("ϵͳ����".equals(factorVO.getStringValue("sourcetype"))) {
										String extend = factorVO.getStringValue("extend");
										if (!TBUtil.getTBUtil().isEmpty(extend)) {
											tableName = extend;
										}
									}
									sourceConfigItem = convertTableItemFromCH_to_EN(tableName, sourceConfigItem); //����,�ֶ�������
								}
								ooo = oneVO.getStringValue(sourceConfigItem);
							}
						}
					}
				}
				if (ooo == null) {
					//��ȡ����ֵ���ǲ���Ӧ���׳��쳣��

					ooo = ""; //
				}
				int varindexNum = ++varindex;
				String varName = "_" + "var" + varindexNum;

				parser.addVariable(varName, ooo); //��jep���ȫ�ֱ���
				if (_directReplace) { //ֱ�Ӱ�ֵ�滻������û�зŵ�jep�����С�Ŀǰֻ����ϵͳ�������õ�,������sql��ֱ��ƴ�ӡ�
					sb.append(ooo); //ƴ��
				} else {
					sb.append(varName); //ƴ��
				}
			} else {
				sb.append(str[i]);
			}
		}
		if (str != null && sb.length() > 0) {
			return sb.toString();
		}
		return _factorStr;
	}


	/*
	 * �ж�ĳ�����Ӽ�����ֵ
	 */
	public Object getFactorisCalc(String _factorName) {
		return factorCalcValue.get(_factorName);
	}

	/**
	 * ���������ӹ�ʽ�ı����ݶ��塣����������
	 * 
	 * @param sourceConfigName
	 * @return
	 */
	public HashVO getFoctorHashVO(String sourceConfigName) throws Exception {
		if (factorMap.containsKey(sourceConfigName)) {
			return (HashVO) factorMap.get(sourceConfigName);
		} else {
			try {
				HashVO[] factors = null;
				String sql = "select *from sal_factor_def where name = '" + sourceConfigName + "'";
				factors = salaryTBUtil.getHashVoArrayByDS(null, sql);
				if (factors != null && factors.length > 0) {
					factorMap.put(sourceConfigName, factors[0]);
					return factors[0];
				} else {
					throw new Exception("ϵͳ��û���ҵ�[" + sourceConfigName + "]����");
				}
			} catch (Exception e) {
				WLTLogger.getLogger(SalaryFomulaParseUtil.class).error("", e);
			}
			return null;
		}
	}

	/*
	 * [����.��������]�Ѻ������������ת��Ϊ����ڵ��ֶΡ�
	 */
	private String convertTableItemFromCH_to_EN(String _tableName_, String _itemName) {
		String _tableName = _tableName_.toLowerCase();
		if (tableXmlMap.containsKey(_tableName + "." + _itemName)) {
			return (String) tableXmlMap.get(_tableName + "." + _itemName);
		} else {
			String[][] descr = null;
			try {
				if (clientOrServer == WLTConstants.JEPTYPE_UI) {
					//					descr = getService().getTableItemAndDescr(_tableName);
				} else {
					descr = new InstallDMO().getAllIntallTabColumnsDescr(_tableName);
				}
			} catch (Exception _ex) {
				_ex.printStackTrace();
			}
			if (descr != null) {
				for (int i = 0; i < descr.length; i++) {
					String _itemEn = descr[i][0].substring(descr[i][0].indexOf(".") + 1);
					tableXmlMap.put(_tableName + "." + descr[i][1], _itemEn); //�ѱ���+���Ļ�������
				}
				if (tableXmlMap.containsKey(_tableName + "." + _itemName)) {
					_itemName = (String) tableXmlMap.get(_tableName + "." + _itemName);
				}
			}
		}
		return _itemName;
	}

	/*
	 * һ���԰���Ҫ�õ������ӹ�ʽȫ�����ڵ������С���
	 */
	public void initFactorHashVOCache(HashVO[] factorVos) {
		for (int i = 0; i < factorVos.length; i++) {
			factorMap.put(factorVos[i].getStringValue("name"), factorVos[i]);
		}
	}

	// ����ǰִ�еķ������������ֵ
	/*
	private void setCurrParseFunctionInputData(Object obj) {
		Node node = parser.getTopNode(); // ��ȡ��ʽ�ĺ�������
		if (node instanceof ASTFunNode) { // ��������
			ASTFunNode currFunNode = (ASTFunNode) node;
			PostfixMathCommandI commandI = parser.getFunctionTable().get(currFunNode.getName()); // �õ�������
			if (commandI instanceof AbstractPostfixMathCommand) { // ���ʵ�ֵ����Զ���ķ���������ǿ���趨�������
				AbstractPostfixMathCommand customCommand = (AbstractPostfixMathCommand) commandI;
				customCommand.setInputData(obj);
			}
		}
	}
	*/
	//�õ���ʽʹ�÷�ʽ
	public static String[][] getFormulaExpression() {
		Vector<String[]> vc = new Vector<String[]>();
		String sql = "";
		if (WLTConstants.MYSQL.equals(TBUtil.getTBUtil().getDefaultDataSourceType())) {
			sql = "\"sql��������\",\"concat(year,'-',month)='\"+[ָ�꿼��ʱ��]+\"'\",";
		}else {//oracle��DB2��concat()����ֻ֧���������������/2017-05-17��
			sql = "\"sql��������\",\"concat(concat(year,'-'),month)='\"+[ָ�꿼��ʱ��]+\"'\",";
		}
			 
		String[][] excel = new String[][] { { "excel(\"excel�ļ���\",", "ϵͳ�д��ڵ�Excel�ļ�����" }, { sql, "��ѯexcel���������ǣ���������������й��ˣ��ŵ������з����´�ʹ��.���ĳ�µĸñ�����ȫ�����" }, { "\"��������\",\"A==\"+[����],", "���м���ʱ��ƥ��[ĳ��]��ֵ�Ƿ���[����]��ֵ.���ڸ��µ�������������ĳ������" }, { "\"����\",\"\")", "��ͻ���ƽ��" }

		};

		vc.add(new String[] { getFormulaExp(excel, false), getFormulaExp(excel, true), "�˹�ʽ�������excel�ֲ�����,��˼����ĳ��Excelȡ���ݣ����Բ�ѯʱ��sql���ˣ�Ȼ�������������ĳ�е�ֵ��", "excel(\"�м�ҵ������\",\r\n\"��������\",\"A=\"+[ָ�꿼�˻���.��������]),\r\n\"����\",\"\")" });
		String[][] excel2 = new String[][] { { "excel([excel����],", "��ϵͳ����Ķ���������ȡExcel����" }, { "\"��������\",\"A==\"+[����]," }, { "\"����\",\"\")" } };
		vc.add(new String[] { getFormulaExp(excel2, false), getFormulaExp(excel2, true), "�˹�ʽ�������excel�ֲ�����", "excel([�м�ҵ������],\r\n\"��������\",\"A==\"+[ָ�꿼�˻���.��������],\r\n\"����\",\"\")" });

		String[][] createMap = new String[][] { { "createMap(\"Դ\",\"��\",\"ֵ\")", "������sql��Ҳ�����Ǽ���" } };
		vc.add(new String[] { getFormulaExp(createMap, false), getFormulaExp(createMap, true), "����Map�������ݣ���߲�������", "" });

		String[][] match = new String[][] { { "ƥ��(\"ƥ�����\",[һ��ֵ]," }, { "\"ƥ��Դ\",[XX����]," }, { "\"ƥ���ֶ�\",\"name\"," }, { "\"�����ֶ�\",\"age\"," }, { "\"����ֵ����\",\"����/1λС��/2λС��\")" } };
		vc.add(new String[] { getFormulaExp(match, false), getFormulaExp(match, true), "��map���߼����бȽ�ֵ����ƥ���жϣ����Ϻ󷵻�ֵ", "ƥ��(\"ƥ�����\",[��Ա��Ϣ.����],\r\n\"ƥ��Դ\",[���鹤�ʼ���],\r\n\"ƥ���ֶ�\",\"checkeduserid\",\r\n\"�����ֶ�\",\"money\")" });
		String[][] iif = new String[][] { { "if(����1,1����ֵ,", "�������1��������ô���˹�ʽֵ��=���صڶ��������ļ���ֵ" }, { "����2,2����ֵ,", "�������2��������ô����ʽֵ��=��������ļ���ֵ" }, { "����N,N����ֵ,", "�Դ�����" }, { "���������ķ���Ĭ��ֵ)", "�������������������,����ʽֵ��=Ĭ��ֵ.���Բ�д�����Ǻ���ģ������ű����С�" }

		};
		vc.add(new String[] { getFormulaExp(iif, false), getFormulaExp(iif, true), "����ȡֵ��ʽ���������1����,��ʽ����1����ֵ,���1�����������ж�����2���Դ�����.���в����������᷵��Ĭ��ֵ(�ɲ�д)", "if([Ӫҵ�����θ�λ��������]<[��λ���ʻ���]*[��λϵ��],\r\n[��λ���ʻ���]*[��λϵ��]/2*[���ϵ��],\"0\")"

		});

		vc.add(new String[] { "indexOf(\"Դ�ַ���\",\"���ҵ��ַ�\")", "indexOf(\"Դ�ַ���\",\"���ҵ��ַ�\")", "�ж�ĳ�ַ���Դ�ַ����е�λ�á��������򷵻�-1", "index(\"abcdefg\",\"ab\")������ֵ����0" });
		vc.add(new String[] { "toString(par)", "toString(par)", "�Ѵ������������ת��Ϊ�ַ���", "toString(80) = \"80\"" });
		vc.add(new String[] { "toNumber(\"par\")", "toNumber(\"par\")", "�Ѵ�����ı�����ת��ΪNumber", "toNumber(\"70\") = 70" });
		vc.add(new String[] { "subString(\"par\",0,0)", "substring(\"�ַ���\",��ʼλ��,����λ��)", "��һ���ַ���������ֹλ�þ��н�ȡ��", "subString(\"abcdefg\",1,2)=b" });
		return vc.toArray(new String[0][0]);
	}

	private static String getFormulaExp(String[][] _lines, boolean _haveDescr) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < _lines.length; i++) {
			if (_lines[i] != null && !_lines[i].equals("")) {
				sb.append(_lines[i][0]);
				if (_haveDescr && _lines[i].length > 1) {
					sb.append("\t//" + _lines[i][1]);
				}
				sb.append("\r\n");
			}
		}
		return sb.toString();
	}
}
