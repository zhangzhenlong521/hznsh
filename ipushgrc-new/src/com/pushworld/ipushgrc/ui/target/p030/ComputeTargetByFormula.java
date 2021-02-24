package com.pushworld.ipushgrc.ui.target.p030;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Stack;

import org.nfunk.jep.JEP;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class ComputeTargetByFormula {

	public static String TYPE_NUM = "NUM"; // ����  ��ʽΪ  "NUM","({0}*{2}-{1})/{3}"
	public static String TYPE_WORD = "WORD";// ����		 "WORD","({0}+{1}+{2})/3","�ǳ�����=100;����=80;һ��=60;��=40;�ǳ���=0"
	JEP jep = new JEP();
	public ComputeTargetByFormula(BillListPanel _billPanel) {
		jep.addFunction("computePostfix",new ComputePostfix(_billPanel));
	};
	public  String getValue(String _formula) throws Exception {
		String form ="computePostfix("+_formula+")";
		jep.parseExpression(form);
		Object obj = jep.getValueAsObject(); 
		return (String) (obj != null?obj:"");
	}
	public static void main(String[] args) {
		ComputeTargetByFormula test = new ComputeTargetByFormula(null);
		String form = "computePostfix(\"NUM\",\"1*3\")computePostfix(\"NUM\",\"1*3\")";
		try {
			Object p = test.getValue(form);
			System.out.println(p );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}



class ComputePostfix extends PostfixMathCommand{
	BillListPanel billPanel=null;
	public ComputePostfix(BillListPanel _billPanel){
		numberOfParameters = -1;
		this.billPanel = _billPanel;
	}
	public void run(Stack inStack) throws ParseException{
			checkStack(inStack);
			String _type = "";  //����
			String _formula=""; //��ʽ
			LinkedHashMap dingyi = new LinkedHashMap();  //����Ĵ�С
			Object lastValue = null;  //���ķ���ֵ
			if(curNumberOfParameters == 2){
				Object obj1 = inStack.pop(); 	//��ʽ
				Object obj2 = inStack.pop();   //��ʽ
				_type = (String) obj2;
				_formula = (String) obj1;
			}else{
				Object obj1 = inStack.pop();  //�����С
				Object obj2 = inStack.pop();	//��ʽ
				Object obj3 = inStack.pop();	//��ʽ
				_type = (String) obj3;
				_formula = (String) obj2;
				String [] dingyis = obj1.toString().split(";");
				for (int i = 0; i < dingyis.length; i++) {
					String[] diny =  dingyis[i].split("=");
					String key =diny[0];
					String val = diny[1];
					dingyi.put(key, val);
				}
			}
			TBUtil tbutil = new TBUtil();
			String[] item = null;
			if(_type == null || _type.equals("")){
				throw new ParseException("��ʽ����Ϊ�գ�");
			}
			if(_type.equals(ComputeTargetByFormula.TYPE_NUM)){
				//  ��ʽΪ  ({0}*{2}-{1})/{3}
				try {
					item = tbutil.getFormulaMacPars(_formula);
				} catch (Exception e) {
					e.printStackTrace();
				}
				String endFormula = "";
				if(item.length == 0){
					endFormula = _formula;
				}
				for (int i = 0; i < item.length; i++) {
					if (i == 0) {
						endFormula += _formula.substring(0, _formula.indexOf("{"));
					}
					String str = item[i];
					BillVO vo = billPanel.getBillVO(Integer.parseInt(str));
					String value = vo.getStringValue("parvalue","0");
					_formula = _formula.substring(_formula.indexOf("}") + 1);
					String s2 = "";
					if (_formula.length() > 0) {
						if (_formula.contains("{")) {
							s2 = _formula.substring(0, _formula.indexOf("{"));
						} else {
							s2 = _formula;
						}
					}
					endFormula += value + s2;
				}
				JEP jep = new JEP();
				jep.parseExpression(endFormula);
				lastValue = jep.getValueAsObject();
				if(lastValue == null){
					ParseException ex = new ParseException("��ʽ���󡣲��ܽ���");
					throw ex;
				}
				DecimalFormat df = new DecimalFormat("0.00");
				lastValue = df.format(lastValue);
			}else if(_type.equals(ComputeTargetByFormula.TYPE_WORD)){
				//  ��ʽΪ:  ({0}+{1}+{2})/3,�ǳ�����=100;����=80;һ��=60;��=40;�ǳ���=0
					try {
						item =new TBUtil().getFormulaMacPars(_formula);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String endFormula = "";
					for (int i = 0; i < item.length; i++) {
						if (i == 0) {
							endFormula += _formula.substring(0, _formula.indexOf("{"));
						}
						String str = item[i];
						BillVO vo = billPanel.getBillVO(Integer.parseInt(str));
						String value = vo.getStringValue("parvalue","0");
						if(!dingyi.containsKey(value)){
							throw new ParseException("��ָ�깫ʽ�в��������Ӳ���ֵ["+value+"]��\n��ָ��Ϊ"+dingyi.keySet().toString());
						}
						value = (String) dingyi.get(value);
					
						_formula = _formula.substring(_formula.indexOf("}") + 1);
						String s2 = "";
						if (_formula.length() > 0) {
							if (_formula.contains("{")) {
								s2 = _formula.substring(0, _formula.indexOf("{"));
							} else {
								s2 = _formula;
							}
						}
						endFormula += value + s2;
					}
					JEP jep = new JEP();
					jep.parseExpression(endFormula);
					lastValue = jep.getValueAsObject();
					if(lastValue == null){
						ParseException ex = new ParseException("��ʽ���󡣲��ܽ���");
						throw ex;
					}
					DecimalFormat df = new DecimalFormat("0.00");
					lastValue = df.format(lastValue);
				}else{
					throw new ParseException("��ʽ��ʽ����ȷ��({0}+{1}+{2})/3,�ǳ�����=100;����=80;һ��=60;��=40;�ǳ���=0");
				}
			inStack.push(lastValue); // �����ջ!!
			}
			
}