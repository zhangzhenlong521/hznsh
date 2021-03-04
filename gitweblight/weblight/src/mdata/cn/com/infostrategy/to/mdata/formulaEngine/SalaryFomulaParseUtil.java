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
 *可以给因子公式定义一个单据类型，可以在计算之前，把所有的用到的因子加入缓存中。
 * 
 * @author haoming create by 2013-6-24
 */
public class SalaryFomulaParseUtil {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8108164280696769371L;

	protected JEP parser = new JEP(); // 一条完整记录计算，只实例化一次jep。否则缓存和jep自带变量会有问题。

	private Object inputObj; // 暂时没有用到。

	private TBUtil tbutil = new TBUtil();

	private HashMap factorMap = new HashMap();

	private HashMap tableXmlMap = new HashMap();

	private HashMap factorCalcValue = new HashMap();//其他对象执行过后的缓存值。 

	public HashMap factorCalVar = new HashMap(); //记录计算过程中，因子名称对应的parse中的变量。

	private HashVO hisbaseDataHashVO = null; //onexcute方法调用是，如果传入的基础值变化了。 需要重置所有的缓存，默认读取Excel不处理。

	private int clientOrServer = TBUtil.getTBUtil().getJVSite() == TBUtil.JVMSITE_CLIENT ? WLTConstants.JEPTYPE_UI : WLTConstants.JEPTYPE_BS;

	private List errorList = new ArrayList(); //存放执行期间的错误日志。

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
		initStandardFunction(); // 初始化公式
		initCustomFunction(); // 初始化自定义公式
		inputObj = _obj;
	}

	// 标准化的函数,即只要最基本的公式计算!
	protected void initStandardFunction() {
		parser.addStandardFunctions(); // 增加所有标准函数!!
		parser.addStandardConstants(); // 增加所有变量!!!
	}

	private void initCustomFunction() {
		parser.addFunction("getHashVOByTable", new GetHashVOByTable(parser, inputObj)); // 加入getHashVOByTable()函数
		parser.addFunction("getDateDifference", new GetDateDifference()); // 得到两个时间差
		parser.addFunction("getCurrDBDate", new GetCurrentDBDate(clientOrServer)); // 加入getCurrDate()函数
		parser.addFunction("getCurrDBTime", new GetCurrentDBTime(clientOrServer)); // 加入getCurrDate()函数
		parser.addFunction("getCurrDate", new GetCurrentTime(GetCurrentTime.DATE)); // 加入getCurrDate()函数
		parser.addFunction("getCurrTime", new GetCurrentTime(GetCurrentTime.TIME)); // 加入getCurrTime()函数
		parser.addFunction("getYearByDateTime", new GetTimePart(GetTimePart.YEAY)); // 根据时间取得年度
		parser.addFunction("getSeasonByDateTime", new GetTimePart(GetTimePart.SEASON)); // 根据时间取得季度
		parser.addFunction("getMonthByDateTime", new GetTimePart(GetTimePart.MONTH)); // 根据时间取得月度
		parser.addFunction("getDateByDateTime", new GetTimePart(GetTimePart.DATE)); // 根据时间取得日期
		parser.addFunction("afterDate", new AfterDate()); // 加入getCurrDate()函数
		parser.addFunction("toString", new ToString()); // 加入toString函数
		parser.addFunction("toNumber", new ToNumber()); // 加入toNumber函数
		parser.addFunction("getMapStrItemValue", new GetMapStrItemValue()); // 从哈希表类型的字符串中取得对应key的value
		parser.addFunction("indexOf", new IndexOf()); // 检索给定字符串中给定的字符或字符串
		parser.addFunction("subString", new SubString());// 截取字符串
		parser.addFunction("fillBlank", new FillBlank());// 填充空格
		parser.addFunction("length", new Length());// 
		parser.addFunction("replaceall", new ReplaceAll());// 替换字符串中的内容
		parser.addFunction("replaceallandsubString", new ReplaceAllAndSubString());// 替换字符串中的内容并截取字符串
		parser.addFunction("getRandom", new GetRandom()); // 取得随机数 日期+随机十位数
		parser.addFunction("getParAsMap", new GetParAsMap()); // 取得随机数 日期+随机十位数
		parser.addFunction("匹配", new MatchByKey(parser));
		parser.addFunction("if", new IF(parser));
		parser.addFunction("excel", new Excel(parser, inputObj, this, null));
		parser.addFunction("avg", new Avg(parser));
		parser.addFunction("createMap", new createMap(parser, inputObj, this, null));
		parser.addFunction("getSqlInCondition", new GetSqlInCondition(parser, inputObj, this, null));
		//需要加一个group的方法。能够结合excel功能，在group一下，最后可以放到map中。可以大大提升性能。*****
	}

	/*
	 *把 DuZi(如果成立,那么返回值,如果成立,那么返回,.....,如果成立,那么返回,都不成立返回);
	 *解析成  index=0如果成立的公式,1=返回值的公式,2如果成立的公式,3返回值....
	 */
	public static List parseDuZi(String _express) {
		int yh = 0; //引号匹配情况
		int kh = 0; //括号
		List rtList = new ArrayList<String>();
		_express = _express.substring(_express.indexOf("("));
		char cs[] = _express.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < cs.length; i++) {
			if (cs[i] != '\"' && cs[i] == '"') { //为了判断逗号是否在字符串内部
				if (yh == 0) {
					yh++;
				} else {
					yh--;
				}
			} else if (cs[i] == ',') {
				if (yh == 0 && kh == 1) { //如果不再引号里面的逗号，说明是分割用
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
			if (kh == 0) { //收尾了。
				rtList.add(sb.toString());
				break;
			}
		}
		return rtList;
	}

	/**
	 * 直接调用公式计算
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
		if (_formulaStr != null && _formulaStr.trim().toLowerCase().indexOf("select ") == 0 && _formulaStr.toLowerCase().contains(" from ")) { //如果是sql,目前必须小写
			String items = _formulaStr.substring(6, _formulaStr.indexOf("from"));
			if ((items.contains(" *") || items.contains(",")) && !items.contains("count")) {//简单判断取多少字段
				return salaryTBUtil.getHashVoArrayByDS(null, _formulaStr);
			} else if (items.contains("count") && items.contains(",")) {
				salaryTBUtil.getHashVoArrayByDS(null, _formulaStr);
			} else {
				return salaryTBUtil.getStringValueByDS(null, _formulaStr);
			}
		}
		parser.parseExpression(_formulaStr);
		Node node = parser.getTopNode(); // 获取公式的函数名称
		if (node instanceof ASTFunNode) { // 方法函数
			ASTFunNode currFunNode = (ASTFunNode) node;
			PostfixMathCommandI commandI = parser.getFunctionTable().get(currFunNode.getName()); // 得到函数名
			if (commandI instanceof AbstractPostfixMathCommand) { // 如果实现的是自定义的方法。可以强行设定传入参数
				AbstractPostfixMathCommand customCommand = (AbstractPostfixMathCommand) commandI;
				customCommand.setInputData(_inputObject);
				customCommand.setConditionData(_conditionObject);
			}
			int child = currFunNode.jjtGetNumChildren();
			for (int i = 0; i < child; i++) {
				setAllNodeInputData(node, _inputObject);
			}
		} else if (node instanceof ASTVarNode) { // 由于自己不能解析公式是否正确，所以会出现parseExpression("工程师").结果返回值为null。
			ASTVarNode astVarnode = (ASTVarNode) node;
			if (astVarnode.getVar().getValue() == null) { // 如果是解析出错，那么var是空值，然后直接返回Name即可。
				if (astVarnode.getName().contains("_var")) {
					return null;
				}
				return astVarnode.getName();
			}
		} else if (node instanceof ASTConstant) { // 如果搞成解析成常量
			return _formulaStr;
		}
		Object obj = null;
		try {
			obj = parser.getEvaluatorVisitor().eval(parser.getTopNode());
		} catch (Exception ex) {
			WLTLogger.getLogger(SalaryFomulaParseUtil.class).error("公式:" + _formulaStr + "报错。", ex);
			throw ex;
		}
		return obj;
	}

	/**
	 * 给所有解析后的节点加入传入值。 
	 * @param _node
	 * @param _inputData
	 */
	private void setAllNodeInputData(Node _node, Object _inputData) {
		if (!(_node instanceof ASTFunNode)) {
			return;
		}
		ASTFunNode currFunNode = (ASTFunNode) _node;
		PostfixMathCommandI commandI = parser.getFunctionTable().get(currFunNode.getName()); // 得到函数名
		if (commandI instanceof AbstractPostfixMathCommand) { // 如果实现的是自定义的方法。可以强行设定传入参数
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
	 * _factorHashVO 传入的 因子定义 配置内容 _baseDataHashVO传入的where取值对象。 暂时废掉
	 */
	/*
	private Object onExecute(HashVO _factorHashVO, HashVO _baseDataHashVO) throws Exception {
		return onExecute(_factorHashVO, _baseDataHashVO, null);
	}
	*/
	/*
	 * 调用次方法前可能需要先调用 putDefaultFactorVO方法
	 * 把一个数据库中不存在的因子或者变量放到计算当中。
	 * 例如计算 部门定量指标时，根据各个指标的名称，把对应的score表的一条hashvo存入进来参与计算。
	 */
	public void putDefaultFactorValue(String _defFactorName, HashVO hvo) {
		this.putDefaultFactorValue(_defFactorName, new HashVO[] { hvo });

	}

	public void putDefaultFactorValue(String _defFactorName, HashVO[] hvo) {
		this.putDefaultFactorValue(_defFactorName, (Object) hvo);
	}

	public void putDefaultFactorValue(String _defFactorName, Object _value) {
		if (!factorMap.containsKey(_defFactorName)) { //如果没有默认创建为系统对象
			HashVO factorVO = new HashVO();
			factorVO.setAttributeValue("sourcetype", "系统对象"); // 得到数据来源类型。
			factorVO.setAttributeValue("value", "");// 设定的值，可以是公式。
			factorVO.setAttributeValue("conditions", ""); // 条件
			factorVO.setAttributeValue("extend", "");
			factorVO.setAttributeValue("name", _defFactorName);
			factorMap.put(_defFactorName, factorVO);
		}
		factorCalcValue.put(_defFactorName, _value);
	}

	/*
	 * 自定义公式。
	 * 自己用代码调用公式时，传入计算类型，公式内容，因子名称等.然后调用onExecute(该自定义因子)就可以取到计算结果。
	 */
	public void putDefaultFactorVO(String _factorSourceType, String _factorValue, String _factorName, String _factorConditions, String _factorextend) {
		this.putDefaultFactorVO(_factorSourceType, _factorValue, _factorName, _factorConditions, _factorextend, false);
	}

	/*
	 * @param _iscache  是否缓存，很重要。一般常见的系统性的，在一次大计算中不变的对象需要缓存，来提高性能。录入人员，机构，常用表单.
	 */

	public void putDefaultFactorVO(String _factorSourceType, String _factorValue, String _factorName, String _factorConditions, String _factorextend, boolean _iscache) {
		HashVO factorVO = new HashVO();
		factorVO.setAttributeValue("sourcetype", _factorSourceType); // 得到数据来源类型。
		factorVO.setAttributeValue("value", _factorValue);// 设定的值，可以是公式。
		factorVO.setAttributeValue("conditions", _factorConditions); // 条件
		factorVO.setAttributeValue("extend", _factorextend);
		factorVO.setAttributeValue("name", _factorName);
		factorVO.setAttributeValue("iscache", _iscache);
		factorMap.put(_factorName, factorVO);
	}

	/*
	 * 对外开放的调用方法。由于此方法会重置缓存区内容，所以公式计算的内部逻辑一定不要调用次函数
	 * _baseDataHashVO 此hashvo会传给所有引用过的因子中，通过[传入数据.key]，key就是hashvo的key来获取。
	 * 通过代码构造_baseDataHashVO，一般传入指标的所有信息，当前人或者机构的部分信息，还有重要的就是传入日期。
	 * 此工具即便写在to包下面，尽量要在bs端调用。
	 */
	public Object onExecute(HashVO _factorHashVO, HashVO _baseDataHashVO, StringBuffer rtStr) throws Exception {
		if (hisbaseDataHashVO != null && hisbaseDataHashVO != _baseDataHashVO) {
			resetAllHashMapHis(false); //默认不重置excel .	
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
	 * 每次执行onExecute方法时，会判断历史传入的基础Hashvo和当前是否一致，如果否，就把非必须缓存的因子结果清空。
	 * 现遇到问题，代码中，强行给因子设定值，然后再执行onExecute方法。结果把该设定的值给情况了，原因是传入的hashvo变了。
	 * 如果强行设置当前传入对象，那么可能在这动作之前需要手动调用resetAllHashMapHis方法。重置缓存。
	 * @param _hashvo
	 */
	public void forceSetCurrBaseVO(HashVO _hashvo) {
		hisbaseDataHashVO = _hashvo;
	}

	/*
	 * 重置缓存
	 */
	public void resetAllHashMapHis(boolean resetAll) {
		if (factorCalcValue.size() == 0) {
			return;
		}
		for (Iterator iterator = factorMap.keySet().iterator(); iterator.hasNext();) {
			String factorName = (String) iterator.next();
			try {
				HashVO factorVo = getFoctorHashVO(factorName);
				if (resetAll) { //如果是excel.并且重置excel.
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
	 * _called 被谁调用。用来判断数字类型是否精确返回。如果是jep公式中调用的用于计算，一般不建议调用次方法。
	 */
	protected Object onExecute(HashVO _factorHashVO, HashVO _baseDataHashVO, int _level, StringBuffer rtStr) throws Exception {
		String sourceType = _factorHashVO.getStringValue("sourcetype"); // 得到数据来源类型。
		String value = _factorHashVO.getStringValue("value");// 设定的值，可以是公式。
		String name = _factorHashVO.getStringValue("name");
		String def = value;// 定义
		if (TBUtil.getTBUtil().isEmpty(sourceType) || TBUtil.getTBUtil().isEmpty(value)) {
			return "";
		}
		Object obj = null;
		if (factorCalcValue.containsKey(name)) {
			if (_level == 0) { // 初始调用者
				rtStr.append(calc_process_msg.get(name));
			}
			return factorCalcValue.get(name);
		}
		AbstractFomulaParse fomulaParse = null;
		if ("系统对象".equals(sourceType)) {
			fomulaParse = new SystemObjectFomulaParse();
		} else if ("文本".equals(sourceType)) {
			fomulaParse = new TextFomulaParse();
		} else if ("数字".equals(sourceType)) {
			fomulaParse = new NumberFomulaParse();
		} else if ("代码计算".equals(sourceType)) { // bsh代码实现
			fomulaParse = new BshFomulaParse();
		} else if ("Excel".equals(sourceType)) { // 来自于Excel,一次取出放到缓存中
			fomulaParse = new ExcelFomulaParse();
		} else if ("map".equalsIgnoreCase(sourceType)) {
			fomulaParse = new MapFomulaParse();
		} else {
			fomulaParse = new TextFomulaParse(); //fortify软件测试fomulaParse必须实例化
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
					st = new BigDecimal(String.valueOf(obj)).toString(); //by haoming 2016-01-27 double会科学计数法
				} else {
					st = String.valueOf(obj);
				}
				if (_level == 0) { // 初始调用者
					if (!"代码计算".equals(sourceType)) {
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
		factorVO.setAttributeValue("sourcetype", _factorSourceType); // 得到数据来源类型。
		factorVO.setAttributeValue("value", _factorValue);// 设定的值，可以是公式。
		factorVO.setAttributeValue("conditions", _factorConditions); // 条件
		factorVO.setAttributeValue("extend", _factorextend);
		factorVO.setAttributeValue("name", _factorName);
		return onExecute(factorVO, _baseDataHashVO, _level, rtStr);
	}

	int varindex = 0;

	/*
	 * 把如果那么的公式替换成_varN,并且返回.给jepparse设置_varN的值.
	 */

	private Object replaceDuZiValue(String _factorStr, HashVO _baseDataHashVO, StringBuffer _rtsb, int level) throws Exception {
		List list = parseDuZi(_factorStr);
		int size = list.size();
		for (int i = 0; i < size; i++) {
			String condition = (String) list.get(i);
			if (i % 2 == 0 && i != size - 1) { //如果是偶数，说明是key，而且不是最后一个.从0,1,2,3,4,5
				Object obj = getReflectOtherFactor(condition, _baseDataHashVO, _rtsb, level + 1);
				Object b = execFormula(obj.toString(), _baseDataHashVO);
				if (b instanceof Double && (Double) b == 0) {
					i++; //两个宽度跳跃
					continue;
				}
			}
			String value = "";
			if (i == size - 1) {//如果是最后一个，奇数的情况
				value = (String) list.get(i);
			} else {
				value = (String) list.get(i + 1); ///得到值的公式，然后用jep执行
			}
			Object valueReflectStr = getReflectOtherFactor(value, _baseDataHashVO, _rtsb, level + 1); //用var1，var2替换后的值
			Object ooo = execFormula(String.valueOf(valueReflectStr), _baseDataHashVO); //执行得到键值，值的内容
			int varindexNum = ++varindex;
			parser.addVariable("_" + "var" + varindexNum, ooo); //把内容重新放到ooo,jep中
			return ("_" + "var" + varindexNum);
		}
		return null;
	}

	protected Object getReflectOtherFactor(String _factorStr, HashVO _baseDataHashVO, StringBuffer _rtsb, int level) throws Exception {
		return this.getReflectOtherFactor(_factorStr, _baseDataHashVO, _rtsb, level, false);
	}

	/*
	 * 此方法用来替换引用其他因子值。用变量方式_var1,_var设定参数值，再执行公式。 cls,String ,number
	 */
	protected Object getReflectOtherFactor(String _factorStr, HashVO _baseDataHashVO, StringBuffer _rtsb, int level, boolean _directReplace) throws Exception {
		if (_factorStr.indexOf("if(") == 0) {
			return replaceDuZiValue(_factorStr, _baseDataHashVO, _rtsb, level);
		}
		String[] str = tbutil.getMacroList(_factorStr, "[", "]");
		StringBuffer sb = new StringBuffer();
		boolean needNumCalc = false; //是否需要数字计算。

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
				String sourceConfig = str[i].substring(1, str[i].length() - 1); //找到其调用的对象因子
				String sourceConfigName = "";
				String sourceConfigItem = "";
				if (sourceConfig.indexOf(".") > 0) { //如果包含.，说明是取其某一列值
					String[] sourceConfig_s = tbutil.split(sourceConfig, ".");
					sourceConfigName = sourceConfig_s[0];
					sourceConfigItem = sourceConfig_s[1];
				} else {
					sourceConfigName = sourceConfig;
				}
				Object ooo = null;
				if ("传入数据".equals(sourceConfigName)) { //如果配置的是[传入数据.checkeddate].表示直接调用传入值的某一个字段。
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
								if (tbutil.isStrAllNunbers(String.valueOf(ooo))) { //如果是纯数字。
									if (_factorStr.contains("+") || _factorStr.contains("-") || _factorStr.contains("*") || _factorStr.contains("/") || _factorStr.contains("=") || _factorStr.contains("<") || _factorStr.contains(">") || _factorStr.contains("!=")) {
										try {
											ooo = Double.parseDouble(String.valueOf(ooo));
										} catch (Exception ex) {
											WLTLogger.getLogger(SalaryFomulaParseUtil.class).error("SalaryFomulaParseUtil的第549行数据[" + ooo + "]成数字出错。", ex);
										}
									}
								}
							}
						}
					} else {
						ooo = _baseDataHashVO;
					}
				} else {
					HashVO factorVO = getFoctorHashVO(sourceConfigName); //获取对象因子
					if (factorVO == null) {
						_rtsb.append("" + _factorStr + " 中定义的[" + sourceConfigName + "]系统中不存在。请检查");
						throw new Exception("" + _factorStr + " 中定义的[" + sourceConfigName + "]系统中不存在。请检查");
					}
					String factorName = factorVO.getStringValue("name"); //得到因子名称
					if (factorCalcValue.containsKey(factorName)) { //判断是否已经放入缓存
						//直接从缓存中取值
						ooo = factorCalcValue.get(factorName);
						for (int m = 0; m < level; m++) {
							_rtsb.append("   ");
						}
						if (ooo instanceof Number || ooo instanceof String) {
							String ps = null;
							if (ooo instanceof Number) {
								ps = new BigDecimal(String.valueOf(ooo)).toString(); //by haoming 2016-01-27 double会科学计数法
							} else {
								ps = String.valueOf(ooo);
							}
							_rtsb.append(factorName + " = " + String.valueOf(ps) + "\r\n");
						}
					} else {
						ooo = onExecute(factorVO, _baseDataHashVO, level + 1, _rtsb);
					}
					if (sourceConfigItem != null && !sourceConfigItem.equals("")) { // 用替换成变量的形式。
						if ("Excel".equalsIgnoreCase(factorVO.getStringValue("sourcetype"))) {
							// A15:A18  
							if (sourceConfigItem.contains(":")) { //如果包含:,表示需要求出一个区域的值. 
								int colonIndex = sourceConfigItem.indexOf(":"); //找到冒号的位置
								String beforeColon = ""; //冒号前的单元格
								String afterColon = ""; //冒号后的单元格
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
							// A15、AB34
							String row = "";
							String col = "";
							for (int j = 0; j < sourceConfigItem.length(); j++) {
								if ((sourceConfigItem.charAt(j) >= 'a' && sourceConfigItem.charAt(j) <= 'z') || (sourceConfigItem.charAt(j) <= 'Z' && sourceConfigItem.charAt(j) >= 'A')) {
									col = col + sourceConfigItem.charAt(j);
								} else {
									row = row + sourceConfigItem.charAt(j);
								}
							}
							int rowNum = Integer.parseInt(row); // 得到行号
							if (ooo instanceof HashVO[]) {
								HashVO vos[] = (HashVO[]) ooo;
								if (vos.length >= rowNum && rowNum > 0) {
									ooo = vos[rowNum - 1].getStringValue(col);
									if (tbutil.isStrAllNunbers(String.valueOf(ooo)) && String.valueOf(ooo).indexOf("-") <= 0) { // 如果有负号。那么只能在第一位。
										ooo = Double.parseDouble(String.valueOf(ooo));
									} else {
										ooo = 0;
									}
								} else {
									throw new Exception("Excel取值越界" + sourceConfigItem);
								}
							}
						} else {
							if (ooo instanceof HashVO[]) {
								HashVO vos[] = (HashVO[]) ooo;
								HashVO oneVO = vos[0];
								if (!oneVO.containsKey(sourceConfigItem)) { //如果是英文的,并且hashvo中有.
									String tableName = factorVO.getStringValue("value");
									if ("系统对象".equals(factorVO.getStringValue("sourcetype"))) {
										String extend = factorVO.getStringValue("extend");
										if (!TBUtil.getTBUtil().isEmpty(extend)) {
											tableName = extend;
										}
									}
									sourceConfigItem = convertTableItemFromCH_to_EN(tableName, sourceConfigItem); //表名,字段中文名
									if (tbutil.isEmpty(sourceConfigItem)) {
										throw new Exception("在执行因子[" + factorName + "]计算出的对象中没有找到\"" + tbutil.split(sourceConfig, ".")[1] + "对应的英文字段.请检查table.xml文件\"");
									}
								}
								ooo = ((HashVO) vos[0]).getStringValue(sourceConfigItem);
							} else if (ooo instanceof HashVO) {
								HashVO oneVO = (HashVO) ooo;
								if (!oneVO.containsKey(sourceConfigItem)) { //如果是英文的,并且hashvo中有.
									String tableName = factorVO.getStringValue("value");
									if ("系统对象".equals(factorVO.getStringValue("sourcetype"))) {
										String extend = factorVO.getStringValue("extend");
										if (!TBUtil.getTBUtil().isEmpty(extend)) {
											tableName = extend;
										}
									}
									sourceConfigItem = convertTableItemFromCH_to_EN(tableName, sourceConfigItem); //表名,字段中文名
								}
								ooo = oneVO.getStringValue(sourceConfigItem);
							}
						}
					}
				}
				if (ooo == null) {
					//获取到空值，是不是应该抛出异常。

					ooo = ""; //
				}
				int varindexNum = ++varindex;
				String varName = "_" + "var" + varindexNum;

				parser.addVariable(varName, ooo); //给jep添加全局变量
				if (_directReplace) { //直接把值替换掉，并没有放到jep缓存中。目前只有找系统对象中用到,常见是sql中直接拼接。
					sb.append(ooo); //拼接
				} else {
					sb.append(varName); //拼接
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
	 * 判断某个因子计算后的值
	 */
	public Object getFactorisCalc(String _factorName) {
		return factorCalcValue.get(_factorName);
	}

	/**
	 * 是所有因子公式的表数据定义。用于做缓存
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
					throw new Exception("系统中没有找到[" + sourceConfigName + "]因子");
				}
			} catch (Exception e) {
				WLTLogger.getLogger(SalaryFomulaParseUtil.class).error("", e);
			}
			return null;
		}
	}

	/*
	 * [机构.机构名称]把后面的中文名称转换为表对于的字段。
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
					tableXmlMap.put(_tableName + "." + descr[i][1], _itemEn); //把表名+中文缓存起来
				}
				if (tableXmlMap.containsKey(_tableName + "." + _itemName)) {
					_itemName = (String) tableXmlMap.get(_tableName + "." + _itemName);
				}
			}
		}
		return _itemName;
	}

	/*
	 * 一次性把需要用到的因子公式全部加在到缓存中。。
	 */
	public void initFactorHashVOCache(HashVO[] factorVos) {
		for (int i = 0; i < factorVos.length; i++) {
			factorMap.put(factorVos[i].getStringValue("name"), factorVos[i]);
		}
	}

	// 给当前执行的方法，传入变量值
	/*
	private void setCurrParseFunctionInputData(Object obj) {
		Node node = parser.getTopNode(); // 获取公式的函数名称
		if (node instanceof ASTFunNode) { // 方法函数
			ASTFunNode currFunNode = (ASTFunNode) node;
			PostfixMathCommandI commandI = parser.getFunctionTable().get(currFunNode.getName()); // 得到函数名
			if (commandI instanceof AbstractPostfixMathCommand) { // 如果实现的是自定义的方法。可以强行设定传入参数
				AbstractPostfixMathCommand customCommand = (AbstractPostfixMathCommand) commandI;
				customCommand.setInputData(obj);
			}
		}
	}
	*/
	//得到公式使用方式
	public static String[][] getFormulaExpression() {
		Vector<String[]> vc = new Vector<String[]>();
		String sql = "";
		if (WLTConstants.MYSQL.equals(TBUtil.getTBUtil().getDefaultDataSourceType())) {
			sql = "\"sql过滤条件\",\"concat(year,'-',month)='\"+[指标考核时间]+\"'\",";
		}else {//oracle和DB2中concat()函数只支持两个参数【李春娟/2017-05-17】
			sql = "\"sql过滤条件\",\"concat(concat(year,'-'),month)='\"+[指标考核时间]+\"'\",";
		}
			 
		String[][] excel = new String[][] { { "excel(\"excel文件名\",", "系统中存在的Excel文件名称" }, { sql, "查询excel所有内容是，根据这个条件进行过滤，放到缓存中方便下次使用.如把某月的该表数据全查出来" }, { "\"过滤条件\",\"A==\"+[条件],", "进行计算时，匹配[某列]的值是否是[条件]的值.如在该月的所有数据中找某个部门" }, { "\"计算\",\"\")", "求和或者平均" }

		};

		vc.add(new String[] { getFormulaExp(excel, false), getFormulaExp(excel, true), "此公式用与计算excel局部数据,意思：从某个Excel取数据，可以查询时就sql过滤，然后过滤条件计算某列的值，", "excel(\"中间业务交易量\",\r\n\"过滤条件\",\"A=\"+[指标考核机构.机构编码]),\r\n\"计算\",\"\")" });
		String[][] excel2 = new String[][] { { "excel([excel对象],", "从系统定义的对象因子中取Excel内容" }, { "\"过滤条件\",\"A==\"+[条件]," }, { "\"计算\",\"\")" } };
		vc.add(new String[] { getFormulaExp(excel2, false), getFormulaExp(excel2, true), "此公式用与计算excel局部数据", "excel([中间业务交易量],\r\n\"过滤条件\",\"A==\"+[指标考核机构.机构编码],\r\n\"计算\",\"\")" });

		String[][] createMap = new String[][] { { "createMap(\"源\",\"键\",\"值\")", "可以是sql，也可以是集合" } };
		vc.add(new String[] { getFormulaExp(createMap, false), getFormulaExp(createMap, true), "创建Map缓存数据，提高查找性能", "" });

		String[][] match = new String[][] { { "匹配(\"匹配入参\",[一个值]," }, { "\"匹配源\",[XX集合]," }, { "\"匹配字段\",\"name\"," }, { "\"返回字段\",\"age\"," }, { "\"返回值类型\",\"整数/1位小数/2位小数\")" } };
		vc.add(new String[] { getFormulaExp(match, false), getFormulaExp(match, true), "在map或者集合中比较值就行匹配判断，符合后返回值", "匹配(\"匹配入参\",[人员信息.主键],\r\n\"匹配源\",[亲情工资集合],\r\n\"匹配字段\",\"checkeduserid\",\r\n\"返回字段\",\"money\")" });
		String[][] iif = new String[][] { { "if(条件1,1返回值,", "如果条件1成立，那么【此公式值】=返回第二个参数的计算值" }, { "条件2,2返回值,", "如果条件2成立，那么【公式值】=后面参数的计算值" }, { "条件N,N返回值,", "以此类推" }, { "都不成立的返回默认值)", "如果上述条件均不成立,【公式值】=默认值.可以不写，但是后面的）右括号必须有。" }

		};
		vc.add(new String[] { getFormulaExp(iif, false), getFormulaExp(iif, true), "条件取值公式，如果条件1成立,则公式返回1返回值,如果1不成立，则判断条件2，以此类推.所有不成立，最后会返回默认值(可不写)", "if([营业部主任岗位工资条件]<[岗位工资基数]*[岗位系数],\r\n[岗位工资基数]*[岗位系数]/2*[身份系数],\"0\")"

		});

		vc.add(new String[] { "indexOf(\"源字符串\",\"查找的字符\")", "indexOf(\"源字符串\",\"查找的字符\")", "判断某字符在源字符串中的位置。不存在则返回-1", "index(\"abcdefg\",\"ab\")，返回值等于0" });
		vc.add(new String[] { "toString(par)", "toString(par)", "把传入的数字内容转化为字符串", "toString(80) = \"80\"" });
		vc.add(new String[] { "toNumber(\"par\")", "toNumber(\"par\")", "把传入的文本内容转化为Number", "toNumber(\"70\") = 70" });
		vc.add(new String[] { "subString(\"par\",0,0)", "substring(\"字符串\",起始位置,结束位置)", "把一个字符串按照起止位置就行截取。", "subString(\"abcdefg\",1,2)=b" });
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
