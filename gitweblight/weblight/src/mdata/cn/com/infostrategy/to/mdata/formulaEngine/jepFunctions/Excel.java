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
	private LinkedHashMap<String, String> excel_condition_num = new LinkedHashMap<String, String>(); //��excel��ȡʱ���sql�������б��룬sql���ܲ���ͬ��
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
		exjep.addFunction("ƥ��", new MatchByKey(null));
		exjep.addFunction("length", new Length());// 
		exjep.addFunction("toString", new ToString()); // ����toString����
		exjep.addFunction("toNumber", new ToNumber()); // ����toNumber����
		exjep.addFunction("subString", new SubString());// ��ȡ�ַ���

		int index = node.jjtGetNumChildren();
		Object obj[] = new Object[index];
		for (int j = 0; j < index; j++) {
			Node cnode = node.jjtGetChild(j); //��ȡ���ڵ�
			obj[j] = evaluatori.eval(cnode);
		}
		HashMap<String, Object> allconditioninfo = new HashMap<String, Object>(); //���е�������Ϣ
		for (int i = 1; i < obj.length; i++) {
			String str_key = (String) obj[i]; //��Ϊ��һ��������,����Ҫ��1
			String str_value = (String) obj[i + 1]; //��Ϊ��һ��������,�����ǵڶ���,����Ҫ��2
			allconditioninfo.put(str_key, str_value);
			i++;
		}
		String condition = (String) allconditioninfo.get("��������"); //����Ĺ���������sql�����÷�����Ҫ��sql������ѯ���Ժ�Ỻ������������������ֻ���ڼ��㲻ֵͬʱ���
		String calctype = (String) allconditioninfo.get("����"); //���㷽ʽ
		String sqlCondition = (String) allconditioninfo.get("sql��������");
		HashVO allcellVos[] = null; //
		if (obj[0] instanceof HashVO[]) {//���������ǵ�һ�������Ǳ��������ұ�����ֵ��hashvo���顣�����Ѿ�
			if (obj.length > 0) { //��һ��������ֵ
				allcellVos = (HashVO[]) obj[0];
			}
		} else if (obj[0] instanceof String) { //ֱ�Ӵ���excel���ļ����ơ�
			String excelName = (String) obj[0]; //
			String excelForFactorName = excelName + "_EXCEL����"; //2014-4-3�����Ѵ˴����Ӹ������Է��Լ�д�����ӣ���excel�������ɵ�����һ�¡�
			String excel_condition_key = excelForFactorName + "" + sqlCondition;
			if (excel_condition_num.containsKey(excel_condition_key)) {
				excelForFactorName = excelForFactorName + "" + excel_condition_num.get(excel_condition_key);
			} else {
				same_num++;
				excelForFactorName = excelForFactorName + "" + same_num;
				excel_condition_num.put(excel_condition_key, same_num + "");
			}
			if ((allcellVos = (HashVO[]) salaryParseUtil.getFactorisCalc(excelForFactorName)) == null) { //���û�м��ع�Excel
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
					throw new ParseException("����" + sqlBuffer + "����������");
				}
				if (allcellVos == null || allcellVos.length == 0) {
					throw new ParseException("ϵͳ����\"" + sqlCondition + "\"��ȡExcel��" + excelName + "��û���ҵ�ֵ��������û���ϴ�,����!");
				}
				//���û�б������뻺����
				salaryParseUtil.putDefaultFactorVO("Excel", excelForFactorName, excelForFactorName, "", "", true);
				salaryParseUtil.putDefaultFactorValue(excelForFactorName, allcellVos);

			}
		}

		/*
		 * ��������calctype�����ݡ�
		 * Ŀǰ֧������
		 * һ��condition����ֵ����
		 * 		1��	calctype=sum(D)��avg(D)		��condition�����£�D�еĺͻ���ƽ��.
		 * 		2��	calctype=D					��condition�����£�����D�е�ֵ.ֻҪƥ�䵽�ͷ���.
		 * 		3��	calctype=D*E				��condition�� �ҵ�ƥ���ֵ�� �ͼ���D*EȻ�󷵻�.
		 * 		4��	calctype=D*C1				��condition�� �ҵ�ƥ���ֵ�� �ͼ���D*C1Ȼ�󷵻�.C1�ǹ̶���Ԫ���ֵ.
		 * 		5��   sum(D*E)��sum(D*E)			��condition�� �ҵ�ƥ���ֵ�� �ͼ���D*E��Ȼ�����ƥ����㲢�����.
		 * ��:condition������ʱ
		 * 		1��  calctype=sum(D)��avg(D)		��ĳExcel����ĳ�еĺͻ���ƽ��
		 * 		2��  calctype=D13					ֱ�ӷ���D13�е�ֵ
		 * 		3��  calctype=sum(D13:G13)��avg(D13:G13)��sum(D13,D14,E15)		����D13��G13�ĺ͡�D13��G13��ƽ��
		 * 		4��  
		 */
		if (calctype == null) {
			throw new ParseException("Excel���㹫ʽû�����á����㡿����");
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
			logger.error("��������!", e);
		}

		return 0;
	}

	/*
	 * �õ�ð��ǰ���ֵ
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
	 * �õ�sum����avg��ʽ�ڲ�����ϸ����
	 */
	private String getSumOrAvgInnerValueExp(DefaultMutableTreeNode _node, String _condition, HashVO[] _allcellVos, String _func) throws Exception { //����ð�ŵ�����ֵ
		String[] formulas = (String[]) _node.getUserObject();
		String formula = formulas[0];//�õ���ʽ���ߵ�Ԫ�����ݵ�����
		String formulaType = formulas[1]; //�õ�ÿһ��������
		if (formula != null && formula.indexOf("(") < 0 && formula.indexOf(":") > 0) { //�����������ݲ���������,���Ұ���:�������������  D13:E18��ֵ
			String[][] colunNearCellColAndRowIndex = getColonValue(formula); //ð��ǰ���ֵ
			if (colunNearCellColAndRowIndex == null || colunNearCellColAndRowIndex.length != 2) {
				throw new Exception("Excel��ʽ����ʧ��.����ԭ��:���ұ���������ֵ.");
			}
			String beforeCol = colunNearCellColAndRowIndex[0][0];
			String beforeRow = colunNearCellColAndRowIndex[0][1];
			String afterCol = colunNearCellColAndRowIndex[1][0];
			String afterRow = colunNearCellColAndRowIndex[1][1];
			if (beforeCol == null || afterCol == null) {
				throw new Exception("Excel��ʽ����ʧ��.����ԭ��:ð��ǰ����в���Ϊ��.");
			}
			if (_condition == null || _condition.equals("")) { //���û�й�������,���� D14:E15,������sum�ڲ��õ�,�Ͳ����й����������Ƕ�λ������
				beforeCol = beforeCol.toUpperCase(); //��д
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
				for (int i = row1; i <= row2; i++) { //�������������Ŷ�������Ǵ��㿪ʼ��
					for (int j = newstartCol; j <= newEndCol; j++) {
						String value = _allcellVos[i].getStringValue(convertIntColToEn(j), "0"); //ȡ�� ĳһ��ĳ�е�ֵ
						//						String value = "123"; //ȡ�� ĳһ��ĳ�е�ֵ
						double currvalue = 0;
						try {
							currvalue = Double.parseDouble(value);
						} catch (Exception ex) {
							//���������
							logger.error("", ex);
						}
						sb.append(currvalue + ","); //�������
					}
				}
				if (sb.length() > 0) {
					return sb.substring(0, sb.length() - 1);
				}
			} else { //���û�У��š�˵��������ǹ̶���ֵ
				throw new Exception("��ʽ������" + _func + "(" + formula + "),��Ӧ���й�������");
			}
		} else {
			//			condition����ʱ��
			//			calctype=sum(D)��avg(D)		��condition�����£�D�еĺͻ���ƽ��.
			//			sum(D*E)��sum(D*E)			��condition�� �ҵ�ƥ���ֵ�� �ͼ���D*E��Ȼ�����ƥ����㲢�����.
			//			condition������ʱ
			//			1��  calctype=sum(D)��avg(D)		��ĳExcel����ĳ�еĺͻ���ƽ��
			String newformula = String.valueOf(getValueByCellAndCondition(_node, _condition, _allcellVos, false));
			if (newformula != null && !newformula.trim().equals("") && newformula.lastIndexOf(",") == newformula.length() - 1) {
				newformula = newformula.substring(0, newformula.length() - 1);
			}
			return newformula;
		}
		return formula;
	}

	/**
	 * �õ���Ԫ��ֵ
	 * @param _formulaNode
	 * @param _condition
	 * @param _allcellVos
	 * @param onlyReturn
	 * @return
	 */
	private Object getValueByCellAndCondition(DefaultMutableTreeNode _formulaNode, String _condition, HashVO _allcellVos[], boolean onlyReturn) {
		if (_condition != null && !_condition.equals("")) {//��������
			StringBuffer newsb = new StringBuffer();
			//����ҵ���ƥ����
			if (_formulaNode.getChildCount() == 0) {
				String formulas[] = (String[]) _formulaNode.getUserObject(); //�õ��ڵ��е�������Ϣ
				String _cellOrFomula = formulas[0];//�õ�ÿһ��������
				String[][] colandrow = getColonValue(_cellOrFomula);
				String beforeCol = colandrow[0][0]; //�õ�����
				String beforeRow = colandrow[0][1];
				if (beforeRow == null || beforeRow.equals("")) { //���д����A,B������ֵ��Ҫȥ��
					String value = "";
					for (int i = 0; i < _allcellVos.length; i++) {
						HashVO rowVO = _allcellVos[i]; //һ�е�����
						boolean flag = parseCondition(rowVO, _condition); //�����Ƿ����
						if (flag) { //����ҵ�ֵ
							value = rowVO.getStringValue(beforeCol); //����2014-4-3�޸ġ���ǰĬ�Ϸ���0�����ܻ�����⡣��indexOf�п��ܻ������⡣
							if (value != null) {
								value = value.trim();
							}
							if (onlyReturn) {//����ҵ��ͷ���ֵ
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
				} else { //���д����D13.�ǾͲ������ˡ�
					HashVO OnlyOneCellVO = _allcellVos[Integer.parseInt(beforeRow) - 1];
					String value = OnlyOneCellVO.getStringValue(beforeCol, "0");
					if (value != null) {
						value = value.trim();
					}
					newsb.append(value);//��ֵƴ������			
				}
			} else { //������ӽڵ�˵���� D+B+C���ֽṹ��
				for (int i = 0; i < _allcellVos.length; i++) {
					HashVO rowVO = _allcellVos[i]; //һ�е�����
					boolean flag = parseCondition(rowVO, _condition); //�����Ƿ����
					if (flag) { //���true�ˡ�
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
		} else { //���û������ D13 ������sum(D) ȫ����� sum(D13,D14,D15),��������ֱ�ӽ��м��㡣(D3+C4)/2
			if (_formulaNode.getChildCount() == 0) {
				String formulas[] = (String[]) _formulaNode.getUserObject(); //�õ��ڵ��е�������Ϣ
				String _cellOrFomula = formulas[0];//�õ�ÿһ��������
				StringBuffer sb = new StringBuffer();
				String[][] colandrow = getColonValue(_cellOrFomula);
				String beforeCol = colandrow[0][0]; //�õ�����
				String beforeRow = colandrow[0][1];
				if (beforeRow == null || beforeRow.equals("")) { //���д����A,B������ֵ��Ҫȥ��
					for (int i = 0; i < _allcellVos.length; i++) {
						HashVO rowVO = _allcellVos[i]; //һ�е�����
						String value = rowVO.getStringValue(beforeCol);
						if (tbutil.isEmpty(value)) { //2014-4-3������ӣ�ԭ������excel��ʽ�еĹ��������У��������õ�indexOf��ʽ��ԭ��Ĭ�ϻ᷵��0�������Ϳ��ܻ���ֹ�Ա������Զ����0������excelֵ�ܵ�һ�о�ֹͣ�ˡ�
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
					sb.replace(sb.length() - 1, sb.length(), "");//�����һ����ȥ��
				} else { //���д����D13.�ǾͲ������ˡ�
					HashVO OnlyOneCellVO = _allcellVos[Integer.parseInt(beforeRow) - 1];
					String value = OnlyOneCellVO.getStringValue(beforeCol, "");
					if (value != null) {
						value = value.trim();
					}
					sb.append(value);//��ֵƴ������			
				}
				return sb.toString();
			} else {//�к��ӽڵ�,˵���ǹ�ʽ.
				StringBuffer newsb = new StringBuffer();
				for (int i = 0; i < _allcellVos.length; i++) {
					HashVO rowVO = _allcellVos[i]; //һ�е�����
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
	 * @param _rowHashVO  ��ǰ�е�����
	 * @param _allHashVOs	ȫ������
	 * @param _node	
	 * @param _sb
	 */
	private void replaceSumOrAvgNodeValueByOneRowVO(HashVO _rowHashVO, HashVO[] _allHashVOs, DefaultMutableTreeNode _node, StringBuffer _formulasb) {
		String[] formulas = (String[]) _node.getUserObject();
		String formula = formulas[0];//�õ���ʽ���ߵ�Ԫ�����ݵ�����
		String formulaType = formulas[1]; //�õ�ÿһ��������
		if (formulaType.equals("���")) {
			String[][] colandrow = getColonValue(formula);
			String beforeCol = colandrow[0][0]; //�õ�����
			String beforeRow = colandrow[0][1];
			String value = "";
			if (beforeRow == null || beforeRow.equals("")) { //���д����A,B������ֵ��Ҫȥ��
				value = _rowHashVO.getStringValue(beforeCol, "0");
				if (value != null) {
					value = value.trim();
				}
			} else { //���д����D13.�ǾͲ������ˡ�
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

	//��������Ĺ�������
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

	//����ABC�õ����ֱ��
	private int convertEnColInt(String _col) {
		char[] c = _col.toCharArray();
		int i = 0;
		for (int j = c.length - 1; j >= 0; j--) {
			i += (c[j] - 65 + 1) * Math.pow(26, c.length - 1 - j);
		}
		return i;
	}

	//������ת����Ӣ�ı���
	private String convertIntColToEn(int _index) {
		StringBuffer sb = new StringBuffer();
		int in = _index / 26;
		if (in == 0) { //��������һ��
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
	 * ���빫ʽ�����������б������
	 * �Թ�ʽ���н���
	 */
	private String parseAndReplaceFormula(String formula, String _condition, HashVO[] _allcellVos) throws Exception {
		StringBuffer n_formula = new StringBuffer(); //�¹�ʽ
		DefaultMutableTreeNode rootnode = new DefaultMutableTreeNode("root");//��ʽ�����㣬�����ƴ�Ӵ� excel��ȡֵ�滻����¹�ʽ��
		parseConditions(formula, rootnode); //�ѹ�ʽ���н�����ÿһ���ڵ���  һ���У����ţ����߹�ʽ��
		int num = rootnode.getChildCount();
		for (int i = 0; i < num; i++) { //����ÿһ���ڵ㣬�����滻ƴ��,ת��Ϊ�¹�ʽ��
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
		//		System.out.println("��ʽΪ��" + sb.toString());
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
		String formulaContent = formulas[0];//�õ�ÿһ��������
		String formulaType = formulas[1]; //�õ�ÿһ��������
		if (formulaType.equals("����") || formulaType.equals("����")) {
			if (formulaContent.contains("_var")) { //˵�����ڹ�ʽ��������ʱ��������,���ڱ������ڲ������µ�jep.��Ҫ��������
				exjep.addVariable(formulaContent, super.jep.getVarValue(formulaContent));
			}
			n_formula.append(formulaContent);
		} else if (formulaType.equals("���")) {
			//�ж��Ƿ������
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
				//����������Ҫ�ж�
			}
			String[] currNodeConfig = (String[]) _node.getUserObject();
			if (currNodeConfig != null && currNodeConfig.length >= 3) { //�������ֶμ�¼���Ƿ������֡��ڽ������Ѿ����ú��ˡ�
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
			String varName = getJepVarIndex(cellValue); //��ֵ�ŵ�jep������
			n_formula.append(varName); //ֻҪ����������������ֵ
		} else {//sum avg��ʽ�ˡ�
			if ("if".equals(formulaContent)) { //�����if��ʽ�����Լ���if�����㷨��
				StringBuffer ifsb = new StringBuffer();
				visitOnlyParseByOneNode(_node, ifsb);
				n_formula.append(replaceDuZiValue(ifsb.toString(), _allcellVos, _condition));
				return;
			} else if ("sum".equals(formulaContent) || "avg".equals(formulaContent)) {
				n_formula.append(formulaContent);
				StringBuffer formula_input = new StringBuffer(); //����Ĺ�ʽ
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
	 * �������ô�Ĺ�ʽ�滻��_varN,���ҷ���.��jepparse����_varN��ֵ.
	 */

	private Object replaceDuZiValue(String _factorStr, HashVO[] _allcellVos, String _condition) throws Exception {
		List list = salaryParseUtil.parseDuZi(_factorStr);
		int size = list.size();
		for (int i = 0; i < size; i++) {
			String condition = (String) list.get(i);
			if (i % 2 == 0 && i != size - 1) { //�����ż����˵����key�����Ҳ������һ��.��0,1,2,3,4,5
				DefaultMutableTreeNode valueNode = new DefaultMutableTreeNode(); //��Ҫ����ֵ��node���½���
				parseConditions(condition, valueNode);
				int childCount = valueNode.getChildCount();
				StringBuffer newvalueformula = new StringBuffer();
				for (int j = 0; j < childCount; j++) {
					visit((DefaultMutableTreeNode) valueNode.getChildAt(j), newvalueformula, _allcellVos, _condition);
				}
				exjep.parseExpression(newvalueformula.toString());
				Object b = exjep.getValueAsObject();
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
			DefaultMutableTreeNode valueNode = new DefaultMutableTreeNode(); //��Ҫ����ֵ��node���½���
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

	//ͨ��һ���ڵ㣬�ҵ����ӽڵ�Ĺ�ʽ��䡣��������ʵ��ֵ�滻��Ŀǰ��Ҫ����if�жϡ�
	private void visitOnlyParseByOneNode(DefaultMutableTreeNode _node, StringBuffer n_formula) {
		if (_node == null) {
			return;
		}
		int num = _node.getChildCount();
		String[] formulas = (String[]) _node.getUserObject();
		String formulaContent = formulas[0];//�õ�ÿһ��������
		n_formula.append(formulaContent);
		for (int i = 0; i < num; i++) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) _node.getChildAt(i);
			visitOnlyParseByOneNode(node, n_formula);
		}
	}

	private String getJepVarIndex(Object obj) {
		String var = "_ivar" + jepindex++;
		exjep.addVariable(var, obj);
		return var; //�ڲ�ivar
	}

	private void parseConditions(String _condition, DefaultMutableTreeNode _treeNode) {
		char chars[] = _condition.toCharArray();
		StringBuffer onkey = new StringBuffer();
		StringBuffer inner = new StringBuffer();
		int kh = 0;
		int yh = 0; //����
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
						} else if (ccc == '=') { //���л��һ���Ⱥţ���ô����
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
					DefaultMutableTreeNode node = new DefaultMutableTreeNode(new String[] { st, (ccc == '<' || ccc == '>') ? "�ȽϷ�" : "����" });
					_treeNode.add(node);
				} else if (ccc == ')') {
					parseConditions(inner.toString(), onNode);
					String[] kuohao = new String[] { ")", "����" };
					DefaultMutableTreeNode node = new DefaultMutableTreeNode(kuohao);
					if (onNode == null) {
						onNode = new DefaultMutableTreeNode(); //�������Ӧ�ò���ִ��
						logger.error("Excel��parseConditions����ִ�г���");
					}
					onNode.add(node);

					inner = new StringBuffer();
				} else if ("+-*/".contains(ccc + "")) { //�����
					if (onkey.length() > 0) {
						DefaultMutableTreeNode node = new DefaultMutableTreeNode(new String[] { onkey.toString(), findParseKeyType(onkey.toString()), "NUM" });
						onkey = new StringBuffer();
						_treeNode.add(node);
					}
					DefaultMutableTreeNode node = new DefaultMutableTreeNode(new String[] { ccc + "", "�����" });
					_treeNode.add(node);
				} else if (ccc == '&') { //��Ҫ�ж���һ
					if (i < chars.length - 1) {
						String st = ccc + "";
						char nextchar = chars[i + 1];
						if (nextchar == '&') {
							i++;
							st += "&";
						}
						DefaultMutableTreeNode node = new DefaultMutableTreeNode(new String[] { st, "����" }); //&&����
						_treeNode.add(node);
					}
				} else if ((ccc >= 65 && ccc <= 90) || ccc >= 48 && ccc <= 57 || (ccc >= 97 && ccc <= 122)) { //��ĸ��������
					onkey.append(ccc);
				} else if (ccc > 127) { //�����ַ���
					onkey.append(ccc);
				} else {
					if (yh == 0 && ccc == ',') {
						if (onkey.length() > 0) {
							DefaultMutableTreeNode node = new DefaultMutableTreeNode(new String[] { onkey.toString(), findParseKeyType(onkey.toString()) }); //&&����
							if (_treeNode.getChildCount() > 0) {
								DefaultMutableTreeNode beforeNode = (DefaultMutableTreeNode) _treeNode.getLastChild(); //�õ�ǰ���һ��Node .�����ǲ��������
								if (beforeNode != null) {
									String[] beforeConfig = (String[]) beforeNode.getUserObject();
									if (beforeConfig != null && ("�����".equals(beforeConfig[1]) || "�ȽϷ�".equals(beforeConfig[1]))) {
										node = new DefaultMutableTreeNode(new String[] { onkey.toString(), findParseKeyType(onkey.toString()), "NUM" });
									}
								}
							}
							_treeNode.add(node);
						}
						onkey = new StringBuffer();
						DefaultMutableTreeNode dh = new DefaultMutableTreeNode(new String[] { ccc + "", "����" }); //&&����
						_treeNode.add(dh);
					} else if (yh == 0 && ' ' == ccc) {
						if (onkey.length() > 0) {
							DefaultMutableTreeNode node = new DefaultMutableTreeNode(new String[] { onkey.toString(), findParseKeyType(onkey.toString()) }); //&&����
							_treeNode.add(node);
						}
						onkey = new StringBuffer();
						DefaultMutableTreeNode dh = new DefaultMutableTreeNode(new String[] { ccc + "", "�ո�" }); //&&����
						_treeNode.add(dh);
					} else {
						onkey.append(ccc);
					}
				}
				if (i == chars.length - 1 && onkey.length() > 0) {
					DefaultMutableTreeNode node = new DefaultMutableTreeNode(new String[] { onkey.toString(), findParseKeyType(onkey.toString()) });
					if (_treeNode.getChildCount() > 0) {
						DefaultMutableTreeNode beforeNode = (DefaultMutableTreeNode) _treeNode.getLastChild(); //�õ�ǰ���һ��Node .�����ǲ��������
						if (beforeNode != null) {
							String[] beforeConfig = (String[]) beforeNode.getUserObject();
							if (beforeConfig != null && ("�����".equals(beforeConfig[1]) || "�ȽϷ�".equals(beforeConfig[1]))) {
								node = new DefaultMutableTreeNode(new String[] { onkey.toString(), findParseKeyType(onkey.toString()), "NUM" });
							}
						}
					}
					onkey = new StringBuffer();
					_treeNode.add(node);
				}
			} else if (kh == 1 && ccc == '(') {//����ǵ�һ��������
				String[] node = new String[] { onkey.toString(), "��ʽ" };
				onNode = new DefaultMutableTreeNode(node);
				onkey = new StringBuffer();
				_treeNode.add(onNode);

				String[] kuohao = new String[] { "(", "����" };
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

	//�ҹؼ��ֵ�����
	private String findParseKeyType(String _parse) {
		char ccc = _parse.charAt(0);
		if (ccc > 127) {
			return "�ַ���";
		} else if (ccc >= 48 && ccc <= 57) {
			return "����";
		} else if (ccc >= 65 && ccc <= 90) {
			return "���";
		} else {
			return "����";
		}
	}
}
