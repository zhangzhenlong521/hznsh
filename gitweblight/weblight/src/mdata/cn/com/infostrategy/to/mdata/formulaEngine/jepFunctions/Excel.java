package cn.com.infostrategy.to.mdata.formulaEngine.jepFunctions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;
import org.nfunk.jep.EvaluatorI;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.CallbackEvaluationI;
import org.nfunk.jep.type.Complex;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.formulaEngine.SalaryFomulaParseUtil;
import cn.com.infostrategy.to.mdata.jepfunctions.IndexOf;
import cn.com.infostrategy.to.mdata.jepfunctions.Length;
import cn.com.infostrategy.to.mdata.jepfunctions.SubString;
import cn.com.infostrategy.to.mdata.jepfunctions.ToNumber;
import cn.com.infostrategy.to.mdata.jepfunctions.ToString;

public class Excel extends AbstractPostfixMathCommand implements CallbackEvaluationI {
	private JEP exjep = new JEP();
	private TBUtil tbutil = new TBUtil();
	private static final Logger logger = WLTLogger.getLogger(Excel.class);
	private LinkedHashMap<String, String> excel_condition_num = new LinkedHashMap<String, String>(); //给excel读取时候的sql条件进行编码，sql可能不不同。
	private int same_num = 0;

	public Excel(JEP _jepParse, Object _wholeObjData, SalaryFomulaParseUtil _salaryParseUtil, StringBuffer _rtSb) {
		super(_jepParse, _wholeObjData, _salaryParseUtil, _rtSb);
	}

	public Object evaluate(Node node, EvaluatorI evaluatori) throws ParseException {
		exjep = new JEP();
		exjep.addStandardConstants();
		exjep.addStandardFunctions();
		exjep.addFunction("avg", new Avg(null));
		exjep.addFunction("indexOf", new IndexOf());
		exjep.addFunction("if", new IF(null));
		exjep.addFunction("匹配", new MatchByKey(null));
		exjep.addFunction("length", new Length());// 
		exjep.addFunction("toString", new ToString()); // 加入toString函数
		exjep.addFunction("toNumber", new ToNumber()); // 加入toNumber函数
		exjep.addFunction("subString", new SubString());// 截取字符串

		int index = node.jjtGetNumChildren();
		Object obj[] = new Object[index];
		for (int j = 0; j < index; j++) {
			Node cnode = node.jjtGetChild(j); //获取到节点
			obj[j] = evaluatori.eval(cnode);
		}
		HashMap<String, Object> allconditioninfo = new HashMap<String, Object>(); //所有的配置信息
		for (int i = 1; i < obj.length; i++) {
			String str_key = (String) obj[i]; //因为第一个是类型,所以要加1
			String str_value = (String) obj[i + 1]; //因为第一个是类型,并且是第二个,所以要加2
			allconditioninfo.put(str_key, str_value);
			i++;
		}
		String condition = (String) allconditioninfo.get("过滤条件"); //计算的过滤条件和sql条件用法很重要。sql条件查询完以后会缓存起来。而过滤条件只是在计算不同值时候就
		String calctype = (String) allconditioninfo.get("计算"); //计算方式
		String sqlCondition = (String) allconditioninfo.get("sql过滤条件");
		HashVO allcellVos[] = null; //
		if (obj[0] instanceof HashVO[]) {//如果传入的是第一个参数是变量，并且变量的值是hashvo数组。则是已经
			if (obj.length > 0) { //第一个参数是值
				allcellVos = (HashVO[]) obj[0];
			}
		} else if (obj[0] instanceof String) { //直接传入excel的文件名称。
			String excelName = (String) obj[0]; //
			String excelForFactorName = excelName + "_EXCEL对象"; //2014-4-3郝明把此处因子改名，以防自己写的因子，和excel解析生成的因子一致。
			String excel_condition_key = excelForFactorName + "" + sqlCondition;
			if (excel_condition_num.containsKey(excel_condition_key)) {
				excelForFactorName = excelForFactorName + "" + excel_condition_num.get(excel_condition_key);
			} else {
				same_num++;
				excelForFactorName = excelForFactorName + "" + same_num;
				excel_condition_num.put(excel_condition_key, same_num + "");
			}
			if ((allcellVos = (HashVO[]) salaryParseUtil.getFactorisCalc(excelForFactorName)) == null) { //如果没有加载过Excel
				StringBuffer sqlBuffer = new StringBuffer();
				try {
					String excel_tableName = salaryTBUtil.getStringValueByDS(null, "select  TABLENAME from EXCEL_TAB where EXCELNAME = '" + excelName + "' ");
					sqlBuffer.append("select * from " + excel_tableName + "");
					if (sqlCondition != null && sqlCondition.length() > 0) {
						sqlBuffer.append(" where " + sqlCondition);
					}
					sqlBuffer.append(" order by id");
					allcellVos = salaryTBUtil.getHashVoArrayByDS(null, sqlBuffer.toString());
				} catch (Exception e) {
					e.printStackTrace();
					throw new ParseException("◆◆" + sqlBuffer + "语句有误◆◆");
				}
				if (allcellVos == null || allcellVos.length == 0) {
					throw new ParseException("系统根据\"" + sqlCondition + "\"读取Excel【" + excelName + "】没有找到值，可能是没有上传,请检查!");
				}
				//如果没有报错，放入缓存中
				salaryParseUtil.putDefaultFactorVO("Excel", excelForFactorName, excelForFactorName, "", "", true);
				salaryParseUtil.putDefaultFactorValue(excelForFactorName, allcellVos);

			}
		}

		/*
		 * 解析计算calctype的内容。
		 * 目前支持类型
		 * 一：condition存在值过滤
		 * 		1、	calctype=sum(D)、avg(D)		在condition条件下，D列的和或者平均.
		 * 		2、	calctype=D					在condition条件下，返回D列的值.只要匹配到就返回.
		 * 		3、	calctype=D*E				在condition下 找到匹配的值后 就计算D*E然后返回.
		 * 		4、	calctype=D*C1				在condition下 找到匹配的值后 就计算D*C1然后返回.C1是固定单元格的值.
		 * 		5、   sum(D*E)、sum(D*E)			在condition下 找到匹配的值后 就计算D*E，然后继续匹配计算并且求和.
		 * 二:condition不存在时
		 * 		1、  calctype=sum(D)、avg(D)		求某Excel表中某列的和或者平均
		 * 		2、  calctype=D13					直接返回D13列的值
		 * 		3、  calctype=sum(D13:G13)、avg(D13:G13)、sum(D13,D14,E15)		计算D13到G13的和、D13到G13的平均
		 * 		4、  
		 */
		if (calctype == null) {
			throw new ParseException("Excel计算公式没有配置【计算】内容");
		}
		try {
			String lastParse = parseAndReplaceFormula(calctype, condition, allcellVos);
			exjep.parseExpression(lastParse);
			String s = null;
			if ((s = exjep.getErrorInfo()) != null) {
				logger.warn(s);
			}
			return exjep.getValueAsObject();
		} catch (Exception e) {
			logger.error("计算出错喽!", e);
		}

		return 0;
	}

	/*
	 * 得到冒号前后的值
	 */
	private String[][] getColonValue(String _formula) {
		int index = 0;
		List<String[]> list = new ArrayList<String[]>();
		int lenght = _formula.length();
		String col = "";
		String row = "";
		while (true) {
			char s = _formula.charAt(index);
			if ((s >= 65 && s <= 90) || (s >= 97 && s <= 122)) {
				col += s;
			} else if ((s >= 48 && s <= 57)) {
				row += s;
			} else {
				if (!col.equals("")) {
					list.add(new String[] { col, row });
				}
				col = "";
				row = "";
			}
			index++;
			if (index >= lenght) {
				if (!col.equals("")) {
					list.add(new String[] { col, row });
				}
				break;
			}
		}
		return (String[][]) list.toArray(new String[0][0]);
	}

	/*
	 * 得到sum或者avg公式内部的详细数据
	 */
	private String getSumOrAvgInnerValueExp(DefaultMutableTreeNode _node, String _condition, HashVO[] _allcellVos, String _func) throws Exception { //返回冒号的所有值
		String[] formulas = (String[]) _node.getUserObject();
		String formula = formulas[0];//得到公式或者单元格内容的内容
		String formulaType = formulas[1]; //得到每一个的类型
		if (formula != null && formula.indexOf("(") < 0 && formula.indexOf(":") > 0) { //如果传入的内容不包含括号,而且包含:，传入的是类似  D13:E18的值
			String[][] colunNearCellColAndRowIndex = getColonValue(formula); //冒号前后的值
			if (colunNearCellColAndRowIndex == null || colunNearCellColAndRowIndex.length != 2) {
				throw new Exception("Excel公式解析失败.错误原因:左右必须有两个值.");
			}
			String beforeCol = colunNearCellColAndRowIndex[0][0];
			String beforeRow = colunNearCellColAndRowIndex[0][1];
			String afterCol = colunNearCellColAndRowIndex[1][0];
			String afterRow = colunNearCellColAndRowIndex[1][1];
			if (beforeCol == null || afterCol == null) {
				throw new Exception("Excel公式解析失败.错误原因:冒号前后的列不能为空.");
			}
			if (_condition == null || _condition.equals("")) { //如果没有过滤条件,计算 D14:E15,理论上sum内部用到,就不能有过滤条件。是定位找数据
				beforeCol = beforeCol.toUpperCase(); //大写
				afterCol = afterCol.toUpperCase(); //
				int startCol = convertEnColInt(beforeCol);
				int endCol = convertEnColInt(afterCol);

				int newstartCol = Math.min(startCol, endCol);
				int newEndCol = Math.max(startCol, endCol);
				int row1 = 0;
				int row2 = 0;
				if (_allcellVos != null) {
					row2 = _allcellVos.length - 1;
				}

				try {
					row1 = Integer.parseInt(beforeRow) - 1;
					row2 = Integer.parseInt(afterRow) - 1;
				} catch (Exception ex) {
					logger.error("", ex);
				}
				StringBuffer sb = new StringBuffer();
				for (int i = row1; i <= row2; i++) { //行数是正真的行哦。并不是从零开始。
					for (int j = newstartCol; j <= newEndCol; j++) {
						String value = _allcellVos[i].getStringValue(convertIntColToEn(j), "0"); //取得 某一行某列的值
						//						String value = "123"; //取得 某一行某列的值
						double currvalue = 0;
						try {
							currvalue = Double.parseDouble(value);
						} catch (Exception ex) {
							//报错就是零
							logger.error("", ex);
						}
						sb.append(currvalue + ","); //加入计算
					}
				}
				if (sb.length() > 0) {
					return sb.substring(0, sb.length() - 1);
				}
			} else { //如果没有：号。说明传入的是固定的值
				throw new Exception("公式中配置" + _func + "(" + formula + "),不应该有过滤条件");
			}
		} else {
			//			condition存在时候
			//			calctype=sum(D)、avg(D)		在condition条件下，D列的和或者平均.
			//			sum(D*E)、sum(D*E)			在condition下 找到匹配的值后 就计算D*E，然后继续匹配计算并且求和.
			//			condition不存在时
			//			1、  calctype=sum(D)、avg(D)		求某Excel表中某列的和或者平均
			String newformula = String.valueOf(getValueByCellAndCondition(_node, _condition, _allcellVos, false));
			if (newformula != null && !newformula.trim().equals("") && newformula.lastIndexOf(",") == newformula.length() - 1) {
				newformula = newformula.substring(0, newformula.length() - 1);
			}
			return newformula;
		}
		return formula;
	}

	/**
	 * 得到单元格值
	 * @param _formulaNode
	 * @param _condition
	 * @param _allcellVos
	 * @param onlyReturn
	 * @return
	 */
	private Object getValueByCellAndCondition(DefaultMutableTreeNode _formulaNode, String _condition, HashVO _allcellVos[], boolean onlyReturn) {
		if (_condition != null && !_condition.equals("")) {//有条件的
			StringBuffer newsb = new StringBuffer();
			//如果找到了匹配行
			if (_formulaNode.getChildCount() == 0) {
				String formulas[] = (String[]) _formulaNode.getUserObject(); //得到节点中的配置信息
				String _cellOrFomula = formulas[0];//得到每一个的内容
				String[][] colandrow = getColonValue(_cellOrFomula);
				String beforeCol = colandrow[0][0]; //得到行列
				String beforeRow = colandrow[0][1];
				if (beforeRow == null || beforeRow.equals("")) { //如果写的是A,B这样的值需要去找
					String value = "";
					for (int i = 0; i < _allcellVos.length; i++) {
						HashVO rowVO = _allcellVos[i]; //一行的数据
						boolean flag = parseCondition(rowVO, _condition); //条件是否成立
						if (flag) { //如果找到值
							value = rowVO.getStringValue(beforeCol); //郝明2014-4-3修改。以前默认返回0，可能会出问题。在indexOf中可能会有问题。
							if (value != null) {
								value = value.trim();
							}
							if (onlyReturn) {//如果找到就返回值
								if (value!=null && tbutil.isStrAllNunbers(value)) {
									return Double.parseDouble(value);
								}
								return value;
							} else { //
								newsb.append(value + ",");
							}
						}
					}
					if (newsb.length() > 0 && !onlyReturn) {
						return newsb.substring(0, newsb.length() - 1);
					}
				} else { //如果写的是D13.那就不用找了。
					HashVO OnlyOneCellVO = _allcellVos[Integer.parseInt(beforeRow) - 1];
					String value = OnlyOneCellVO.getStringValue(beforeCol, "0");
					if (value != null) {
						value = value.trim();
					}
					newsb.append(value);//把值拼接起来			
				}
			} else { //如果有子节点说明是 D+B+C这种结构。
				for (int i = 0; i < _allcellVos.length; i++) {
					HashVO rowVO = _allcellVos[i]; //一行的数据
					boolean flag = parseCondition(rowVO, _condition); //条件是否成立
					if (flag) { //如果true了。
						StringBuffer innersb = new StringBuffer();
						replaceSumOrAvgNodeValueByOneRowVO(rowVO, _allcellVos, _formulaNode, innersb);
						if (innersb.length() > 0) {
							newsb.append(innersb.toString() + ",");
						} else {
							newsb.append("0,");
						}
					}
				}
			}
			return newsb.toString();
		} else { //如果没有条件 D13 或者是sum(D) 全部求和 sum(D13,D14,D15),回来发现直接进行计算。(D3+C4)/2
			if (_formulaNode.getChildCount() == 0) {
				String formulas[] = (String[]) _formulaNode.getUserObject(); //得到节点中的配置信息
				String _cellOrFomula = formulas[0];//得到每一个的内容
				StringBuffer sb = new StringBuffer();
				String[][] colandrow = getColonValue(_cellOrFomula);
				String beforeCol = colandrow[0][0]; //得到行列
				String beforeRow = colandrow[0][1];
				if (beforeRow == null || beforeRow.equals("")) { //如果写的是A,B这样的值需要去找
					for (int i = 0; i < _allcellVos.length; i++) {
						HashVO rowVO = _allcellVos[i]; //一行的数据
						String value = rowVO.getStringValue(beforeCol);
						if (tbutil.isEmpty(value)) { //2014-4-3郝明添加，原因是在excel公式中的过滤条件中，经常会用到indexOf公式。原有默认会返回0，这样就可能会出现柜员号中永远包含0，导致excel值跑第一行就停止了。
							continue;
						}
						if (value != null) {
							value = value.trim();
						}
						sb.append(value + ",");
					}
					if (sb.length() == 0) {
						return "";
					}
					sb.replace(sb.length() - 1, sb.length(), "");//把最后一个，去掉
				} else { //如果写的是D13.那就不用找了。
					HashVO OnlyOneCellVO = _allcellVos[Integer.parseInt(beforeRow) - 1];
					String value = OnlyOneCellVO.getStringValue(beforeCol, "");
					if (value != null) {
						value = value.trim();
					}
					sb.append(value);//把值拼接起来			
				}
				return sb.toString();
			} else {//有孩子节点,说明是公式.
				StringBuffer newsb = new StringBuffer();
				for (int i = 0; i < _allcellVos.length; i++) {
					HashVO rowVO = _allcellVos[i]; //一行的数据
					StringBuffer innersb = new StringBuffer();
					replaceSumOrAvgNodeValueByOneRowVO(rowVO, _allcellVos, _formulaNode, innersb);
					if (innersb.length() > 0) {
						newsb.append(innersb.toString() + ",");
					} else {
						newsb.append("0,");
					}
				}
				return newsb.toString();
			}

		}
	}

	/**
	 *  
	 * @param _rowHashVO  当前行的数据
	 * @param _allHashVOs	全部数据
	 * @param _node	
	 * @param _sb
	 */
	private void replaceSumOrAvgNodeValueByOneRowVO(HashVO _rowHashVO, HashVO[] _allHashVOs, DefaultMutableTreeNode _node, StringBuffer _formulasb) {
		String[] formulas = (String[]) _node.getUserObject();
		String formula = formulas[0];//得到公式或者单元格内容的内容
		String formulaType = formulas[1]; //得到每一个的类型
		if (formulaType.equals("表格")) {
			String[][] colandrow = getColonValue(formula);
			String beforeCol = colandrow[0][0]; //得到行列
			String beforeRow = colandrow[0][1];
			String value = "";
			if (beforeRow == null || beforeRow.equals("")) { //如果写的是A,B这样的值需要去找
				value = _rowHashVO.getStringValue(beforeCol, "0");
				if (value != null) {
					value = value.trim();
				}
			} else { //如果写的是D13.那就不用找了。
				HashVO OnlyOneCellVO = _allHashVOs[Integer.parseInt(beforeRow) - 1];
				value = OnlyOneCellVO.getStringValue(beforeCol, "0");
				if (value != null) {
					value = value.trim();
				}
			}
			if (tbutil.isStrAllNunbers(value)) {
				_formulasb.append(value);
			} else {
				_formulasb.append("0");
			}
		} else if (_node.getChildCount() > 0) {
			int childcount = _node.getChildCount();
			for (int i = 0; i < childcount; i++) {
				replaceSumOrAvgNodeValueByOneRowVO(_rowHashVO, _allHashVOs, (DefaultMutableTreeNode) _node.getChildAt(i), _formulasb);
			}
		} else {
			_formulasb.append(formula);
		}

	}

	//解析传入的过滤条件
	private boolean parseCondition(HashVO _cellVO, String _conition) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(new String("root"));
		parseConditions(_conition, node);
		StringBuffer sb = new StringBuffer();
		try {
			int child = node.getChildCount();
			for (int i = 0; i < child; i++) {
				visit((DefaultMutableTreeNode) node.getChildAt(i), sb, new HashVO[] { _cellVO }, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		exjep.parseExpression(sb.toString());
		Object falg = exjep.getValueAsObject();
		if (falg instanceof Boolean && ((Boolean) falg).booleanValue()) {
			return true;
		}
		double d = 0;
		if (falg instanceof Complex)
			d = ((Complex) falg).re();
		else if (falg instanceof Number)
			d = ((Number) falg).doubleValue();
		else {

		}
		if (d > 0.0D) {
			return true;
		}
		return false;
	}

	//根据ABC得到数字编号
	private int convertEnColInt(String _col) {
		char[] c = _col.toCharArray();
		int i = 0;
		for (int j = c.length - 1; j >= 0; j--) {
			i += (c[j] - 65 + 1) * Math.pow(26, c.length - 1 - j);
		}
		return i;
	}

	//把数字转换成英文编码
	private String convertIntColToEn(int _index) {
		StringBuffer sb = new StringBuffer();
		int in = _index / 26;
		if (in == 0) { //如果是最后一层
			char ccc = (char) (65 - 1 + _index);
			sb.append(ccc);
		} else {
			char ccc = (char) (65 - 1 + _index % 26);
			sb.append(ccc);
			sb.insert(0, convertIntColToEn(in));
		}
		return sb.toString();
	}

	/*
	 * 传入公式，条件，所有表格数据
	 * 对公式就行解析
	 */
	private String parseAndReplaceFormula(String formula, String _condition, HashVO[] _allcellVos) throws Exception {
		StringBuffer n_formula = new StringBuffer(); //新公式
		DefaultMutableTreeNode rootnode = new DefaultMutableTreeNode("root");//公式根几点，后面会拼接从 excel中取值替换后的新公式。
		parseConditions(formula, rootnode); //把公式就行解析，每一个节点是  一个列，符号，或者公式等
		int num = rootnode.getChildCount();
		for (int i = 0; i < num; i++) { //遍历每一个节点，就行替换拼接,转换为新公式。
			visit((DefaultMutableTreeNode) rootnode.getChildAt(i), n_formula, _allcellVos, _condition);
		}
		return n_formula.toString();
	}

	/*
	public static void main(String[] args) {
		Excel ex = new Excel(null, null, null, null);
		JEP jep = new JEP();
		jep.addStandardConstants();
		jep.addStandardFunctions();
		jep.addFunction("avg", new Avg(null));
		jep.addFunction("indexOf", new IndexOf());
		jep.addFunction("if", new IF(null));
		String formula = "(_var1+_var2)<_var3";
		Integer in = new Integer(10);
		jep.addVariable("_var1", 1d);
		jep.addVariable("_var2", 1d);
		jep.addVariable("_var3", 1d);
		jep.parseExpression(formula);
		System.out.println(jep.getErrorInfo());
		System.out.println(jep.getValueAsObject());
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new String[] { "root", "root" });
		ex.parseConditions(formula, rootNode);
		//		Vector vc = new Vector();
		//		StringBuffer sb = new StringBuffer();
		//		ex.visittest(rootNode, 0, sb);
		//		System.out.println("公式为：" + sb.toString());
		//		System.out.println(ex.convertEnColInt("ABC"));
		//		System.out.println(ex.convertIntColToEn(731));
	}
	*/
	int jepindex = 0;

	private void visit(DefaultMutableTreeNode _node, StringBuffer n_formula, HashVO[] _allcellVos, String _condition) throws Exception {
		if (_node == null) {
			return;
		}
		int num = _node.getChildCount();
		String[] formulas = (String[]) _node.getUserObject();
		String formulaContent = formulas[0];//得到每一个的内容
		String formulaType = formulas[1]; //得到每一个的类型
		if (formulaType.equals("符号") || formulaType.equals("数字")) {
			if (formulaContent.contains("_var")) { //说明是在公式初步解析时传过来的,由于本函数内部调用新的jep.需要重新设置
				exjep.addVariable(formulaContent, super.jep.getVarValue(formulaContent));
			}
			n_formula.append(formulaContent);
		} else if (formulaType.equals("表格")) {
			//判断是否是求和
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) _node.getParent();
			String[] parentConfig = null;
			boolean needNum = false;
			if (parentNode != null && !parentNode.isRoot()) {
				parentConfig = (String[]) parentNode.getUserObject();
				if ("sum".equals(parentConfig[0]) || "avg".equals(parentConfig[0])) {
					String str = getSumOrAvgInnerValueExp(_node, _condition, _allcellVos, formulaContent);
					n_formula.append(str);
					return;
				}
				//如果是求和需要判断
			}
			String[] currNodeConfig = (String[]) _node.getUserObject();
			if (currNodeConfig != null && currNodeConfig.length >= 3) { //第三个字段记录了是否是数字。在解析中已经设置好了。
				if ("NUM".equals(currNodeConfig[2])) {
					needNum = true;
				}
			}
			Object cellValue = getValueByCellAndCondition(_node, _condition, _allcellVos, true);
			if (needNum) {
				try {
					if (cellValue == null || "".equals(cellValue)) {
						cellValue = 0f;
					} else {
						cellValue = Double.parseDouble(cellValue + "");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			String varName = getJepVarIndex(cellValue); //把值放到jep变量中
			n_formula.append(varName); //只要条件成立就立马返回值
		} else {//sum avg公式了。
			if ("if".equals(formulaContent)) { //如果是if公式，用自己的if解析算法。
				StringBuffer ifsb = new StringBuffer();
				visitOnlyParseByOneNode(_node, ifsb);
				n_formula.append(replaceDuZiValue(ifsb.toString(), _allcellVos, _condition));
				return;
			} else if ("sum".equals(formulaContent) || "avg".equals(formulaContent)) {
				n_formula.append(formulaContent);
				StringBuffer formula_input = new StringBuffer(); //传入的公式
				visitOnlyParseByOneNode(_node, formula_input);
				if (formula_input.indexOf(":") > 0) {
					for (int i = 0; i < num; i++) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) _node.getChildAt(i);
						visit(node, n_formula, _allcellVos, _condition);
					}
				} else {
					n_formula.append("(");
					n_formula.append(getSumOrAvgInnerValueExp(_node, _condition, _allcellVos, formulaContent));
					n_formula.append(")");
				}
			} else {
				n_formula.append(formulaContent);
				for (int i = 0; i < num; i++) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) _node.getChildAt(i);
					visit(node, n_formula, _allcellVos, _condition);
				}
			}
		}
	}

	/*
	 * 把如果那么的公式替换成_varN,并且返回.给jepparse设置_varN的值.
	 */

	private Object replaceDuZiValue(String _factorStr, HashVO[] _allcellVos, String _condition) throws Exception {
		List list = salaryParseUtil.parseDuZi(_factorStr);
		int size = list.size();
		for (int i = 0; i < size; i++) {
			String condition = (String) list.get(i);
			if (i % 2 == 0 && i != size - 1) { //如果是偶数，说明是key，而且不是最后一个.从0,1,2,3,4,5
				DefaultMutableTreeNode valueNode = new DefaultMutableTreeNode(); //需要计算值的node重新解析
				parseConditions(condition, valueNode);
				int childCount = valueNode.getChildCount();
				StringBuffer newvalueformula = new StringBuffer();
				for (int j = 0; j < childCount; j++) {
					visit((DefaultMutableTreeNode) valueNode.getChildAt(j), newvalueformula, _allcellVos, _condition);
				}
				exjep.parseExpression(newvalueformula.toString());
				Object b = exjep.getValueAsObject();
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
			DefaultMutableTreeNode valueNode = new DefaultMutableTreeNode(); //需要计算值的node重新解析
			parseConditions(value, valueNode);
			int childCount = valueNode.getChildCount();
			StringBuffer newvalueformula = new StringBuffer();
			for (int j = 0; j < childCount; j++) {
				visit((DefaultMutableTreeNode) valueNode.getChildAt(j), newvalueformula, _allcellVos, _condition);
			}
			exjep.parseExpression(newvalueformula.toString());
			Object obj = exjep.getValueAsObject();
			if (obj == null) {
				return "";
			} else {
				return obj;
			}
		}
		return null;
	}

	//通过一个节点，找到它子节点的公式语句。并不进行实际值替换。目前主要用于if判断。
	private void visitOnlyParseByOneNode(DefaultMutableTreeNode _node, StringBuffer n_formula) {
		if (_node == null) {
			return;
		}
		int num = _node.getChildCount();
		String[] formulas = (String[]) _node.getUserObject();
		String formulaContent = formulas[0];//得到每一个的内容
		n_formula.append(formulaContent);
		for (int i = 0; i < num; i++) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) _node.getChildAt(i);
			visitOnlyParseByOneNode(node, n_formula);
		}
	}

	private String getJepVarIndex(Object obj) {
		String var = "_ivar" + jepindex++;
		exjep.addVariable(var, obj);
		return var; //内部ivar
	}

	private void parseConditions(String _condition, DefaultMutableTreeNode _treeNode) {
		char chars[] = _condition.toCharArray();
		StringBuffer onkey = new StringBuffer();
		StringBuffer inner = new StringBuffer();
		int kh = 0;
		int yh = 0; //引号
		DefaultMutableTreeNode onNode = null;
		for (int i = 0; i < chars.length; i++) {
			char ccc = chars[i];
			if (ccc == '\"') {
				if (yh == 0) {
					yh++;
				} else {
					yh--;
				}
			}
			if (ccc == '(') {
				kh++;
			} else if (ccc == ')') {
				kh--;
			}
			if (kh == 0) {
				if (ccc == '!' || ccc == '=' || ccc == '<' || ccc == '>') {
					String st = ccc + "";
					if (i < chars.length - 1) {
						char nextchar = chars[i + 1];
						if (nextchar == '=') {
							i++;
							st += "=";
						} else if (ccc == '=') { //如果谢了一个等号，那么补齐
							st += "=";
						}
					}
					if (onkey.length() > 0) {
						DefaultMutableTreeNode node = null;
						if (ccc == '<' || ccc == '>') {
							node = new DefaultMutableTreeNode(new String[] { onkey.toString(), findParseKeyType(onkey.toString()), "NUM" });
						} else {
							node = new DefaultMutableTreeNode(new String[] { onkey.toString(), findParseKeyType(onkey.toString()) });
						}
						onkey = new StringBuffer();
						_treeNode.add(node);
					}
					DefaultMutableTreeNode node = new DefaultMutableTreeNode(new String[] { st, (ccc == '<' || ccc == '>') ? "比较符" : "符号" });
					_treeNode.add(node);
				} else if (ccc == ')') {
					parseConditions(inner.toString(), onNode);
					String[] kuohao = new String[] { ")", "符号" };
					DefaultMutableTreeNode node = new DefaultMutableTreeNode(kuohao);
					if (onNode == null) {
						onNode = new DefaultMutableTreeNode(); //这个代码应该不会执行
						logger.error("Excel类parseConditions方法执行出错。");
					}
					onNode.add(node);

					inner = new StringBuffer();
				} else if ("+-*/".contains(ccc + "")) { //如果是
					if (onkey.length() > 0) {
						DefaultMutableTreeNode node = new DefaultMutableTreeNode(new String[] { onkey.toString(), findParseKeyType(onkey.toString()), "NUM" });
						onkey = new StringBuffer();
						_treeNode.add(node);
					}
					DefaultMutableTreeNode node = new DefaultMutableTreeNode(new String[] { ccc + "", "运算符" });
					_treeNode.add(node);
				} else if (ccc == '&') { //需要判断下一
					if (i < chars.length - 1) {
						String st = ccc + "";
						char nextchar = chars[i + 1];
						if (nextchar == '&') {
							i++;
							st += "&";
						}
						DefaultMutableTreeNode node = new DefaultMutableTreeNode(new String[] { st, "符号" }); //&&符号
						_treeNode.add(node);
					}
				} else if ((ccc >= 65 && ccc <= 90) || ccc >= 48 && ccc <= 57 || (ccc >= 97 && ccc <= 122)) { //字母或者数字
					onkey.append(ccc);
				} else if (ccc > 127) { //中文字符串
					onkey.append(ccc);
				} else {
					if (yh == 0 && ccc == ',') {
						if (onkey.length() > 0) {
							DefaultMutableTreeNode node = new DefaultMutableTreeNode(new String[] { onkey.toString(), findParseKeyType(onkey.toString()) }); //&&符号
							if (_treeNode.getChildCount() > 0) {
								DefaultMutableTreeNode beforeNode = (DefaultMutableTreeNode) _treeNode.getLastChild(); //得到前面的一个Node .看看是不是运算符
								if (beforeNode != null) {
									String[] beforeConfig = (String[]) beforeNode.getUserObject();
									if (beforeConfig != null && ("运算符".equals(beforeConfig[1]) || "比较符".equals(beforeConfig[1]))) {
										node = new DefaultMutableTreeNode(new String[] { onkey.toString(), findParseKeyType(onkey.toString()), "NUM" });
									}
								}
							}
							_treeNode.add(node);
						}
						onkey = new StringBuffer();
						DefaultMutableTreeNode dh = new DefaultMutableTreeNode(new String[] { ccc + "", "逗号" }); //&&符号
						_treeNode.add(dh);
					} else if (yh == 0 && ' ' == ccc) {
						if (onkey.length() > 0) {
							DefaultMutableTreeNode node = new DefaultMutableTreeNode(new String[] { onkey.toString(), findParseKeyType(onkey.toString()) }); //&&符号
							_treeNode.add(node);
						}
						onkey = new StringBuffer();
						DefaultMutableTreeNode dh = new DefaultMutableTreeNode(new String[] { ccc + "", "空格" }); //&&符号
						_treeNode.add(dh);
					} else {
						onkey.append(ccc);
					}
				}
				if (i == chars.length - 1 && onkey.length() > 0) {
					DefaultMutableTreeNode node = new DefaultMutableTreeNode(new String[] { onkey.toString(), findParseKeyType(onkey.toString()) });
					if (_treeNode.getChildCount() > 0) {
						DefaultMutableTreeNode beforeNode = (DefaultMutableTreeNode) _treeNode.getLastChild(); //得到前面的一个Node .看看是不是运算符
						if (beforeNode != null) {
							String[] beforeConfig = (String[]) beforeNode.getUserObject();
							if (beforeConfig != null && ("运算符".equals(beforeConfig[1]) || "比较符".equals(beforeConfig[1]))) {
								node = new DefaultMutableTreeNode(new String[] { onkey.toString(), findParseKeyType(onkey.toString()), "NUM" });
							}
						}
					}
					onkey = new StringBuffer();
					_treeNode.add(node);
				}
			} else if (kh == 1 && ccc == '(') {//如果是第一个右括号
				String[] node = new String[] { onkey.toString(), "公式" };
				onNode = new DefaultMutableTreeNode(node);
				onkey = new StringBuffer();
				_treeNode.add(onNode);

				String[] kuohao = new String[] { "(", "符号" };
				DefaultMutableTreeNode khnode = new DefaultMutableTreeNode(kuohao);
				onNode.add(khnode);

			}
			if (kh > 0) {
				if (!(kh == 1 && ccc == '(')) {
					inner.append(ccc);
				}
			}

		}
	}

	//找关键字的类型
	private String findParseKeyType(String _parse) {
		char ccc = _parse.charAt(0);
		if (ccc > 127) {
			return "字符串";
		} else if (ccc >= 48 && ccc <= 57) {
			return "数字";
		} else if (ccc >= 65 && ccc <= 90) {
			return "表格";
		} else {
			return "符号";
		}
	}
}
