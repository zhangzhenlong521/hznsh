package cn.com.infostrategy.ui.mdata.formatcomp;

import org.nfunk.jep.JEP;

import cn.com.infostrategy.ui.mdata.BillFormatPanel;

/**
 * ��ʽ���
 * ʹ�ù�ʽ�����������ý���,��ǰ��һ����ʽ��Ӧһ����,����Ū�ɾ�һ������ ����
 * @author xch
 *
 */
public class FormatPanelFormulaParse {
	BillFormatPanel formatpanel = null;
	JEP parser = new JEP();

	public FormatPanelFormulaParse(BillFormatPanel _panel) {
		this.formatpanel = _panel; //
		initNormalFunction(); //
	}

	protected void initNormalFunction() {
		parser.addStandardFunctions(); //�������б�׼����!!
		parser.addStandardConstants(); //�������б���!!!

		//ע�����к���
		parser.addFunction("getLabel", new GetBillFormatPanel(this.formatpanel, "getLabel")); //
		parser.addFunction("getCard", new GetBillFormatPanel(this.formatpanel, "getCard")); //
		parser.addFunction("getList", new GetBillFormatPanel(this.formatpanel, "getList")); //
		parser.addFunction("getTree", new GetBillFormatPanel(this.formatpanel, "getTree")); //
		parser.addFunction("getQuery", new GetBillFormatPanel(this.formatpanel, "getQuery")); //��ѯ���
		parser.addFunction("getButtons", new GetBillFormatPanel(this.formatpanel, "getButtons")); //��ť��
		parser.addFunction("getCell", new GetBillFormatPanel(this.formatpanel, "getCell")); //
		parser.addFunction("getWorkFlow", new GetBillFormatPanel(this.formatpanel, "getWorkFlow")); //
		parser.addFunction("getWorkPanel", new GetBillFormatPanel(this.formatpanel, "getWorkPanel"));

		parser.addFunction("getSplit", new GetBillFormatPanel(this.formatpanel, "getSplit")); //
		parser.addFunction("getTab", new GetBillFormatPanel(this.formatpanel, "getTab")); //
		parser.addFunction("getRadioTab", new GetBillFormatPanel(this.formatpanel, "getRadioTab")); //��ť�Ͷ�ҳǩ.
		parser.addFunction("getLevel", new GetBillFormatPanel(this.formatpanel, "getLevel")); //��㲼��.
		parser.addFunction("getVFlowLayout", new GetBillFormatPanel(this.formatpanel, "getVFlowLayout")); //��ֱ����.
		parser.addFunction("getBorderLayout", new GetBillFormatPanel(this.formatpanel, "getBorderLayout")); //BorderLayout�Ĳ���

		parser.addFunction("getChart", new GetBillFormatPanel(this.formatpanel, "getChart")); //
		parser.addFunction("getChartReal", new GetBillFormatPanel(this.formatpanel, "getChartReal")); //
		parser.addFunction("getPanel", new GetBillFormatPanel(this.formatpanel, "getPanel")); //
		parser.addFunction("getBom", new GetBillFormatPanel(this.formatpanel, "getBom")); //

		parser.addFunction("getRegisterFormat", new GetBillFormatPanel(this.formatpanel, "getRegisterFormat")); //��ʽ���,��ʽ����л����Լ��빫ʽ���,����������Ƕ��!
		parser.addFunction("getMultiReport", new GetBillFormatPanel(this.formatpanel, "getMultiReport")); //��ʽ���,��ʽ����л����Լ��빫ʽ���,����������Ƕ��!
	}

	/**
	 * ������ִ�й�ʽ!!
	 * @param _expr,�����Ŀ�ִ�е��﷨!!
	 * @return
	 */
	public Object execFormula(String _expr) {
		try {
			parser.parseExpression(_expr); //ִ�й�ʽ
			return parser.getValueAsObject(); //
		} catch (Throwable ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
