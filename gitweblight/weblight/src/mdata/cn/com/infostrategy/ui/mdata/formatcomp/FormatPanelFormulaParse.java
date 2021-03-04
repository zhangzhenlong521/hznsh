package cn.com.infostrategy.ui.mdata.formatcomp;

import org.nfunk.jep.JEP;

import cn.com.infostrategy.ui.mdata.BillFormatPanel;

/**
 * 格式面板
 * 使用公式下拉快速配置界面,以前是一个公式对应一个类,后来弄成就一个类了 ！！
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
		parser.addStandardFunctions(); //增加所有标准函数!!
		parser.addStandardConstants(); //增加所有变量!!!

		//注册所有函数
		parser.addFunction("getLabel", new GetBillFormatPanel(this.formatpanel, "getLabel")); //
		parser.addFunction("getCard", new GetBillFormatPanel(this.formatpanel, "getCard")); //
		parser.addFunction("getList", new GetBillFormatPanel(this.formatpanel, "getList")); //
		parser.addFunction("getTree", new GetBillFormatPanel(this.formatpanel, "getTree")); //
		parser.addFunction("getQuery", new GetBillFormatPanel(this.formatpanel, "getQuery")); //查询面板
		parser.addFunction("getButtons", new GetBillFormatPanel(this.formatpanel, "getButtons")); //按钮栏
		parser.addFunction("getCell", new GetBillFormatPanel(this.formatpanel, "getCell")); //
		parser.addFunction("getWorkFlow", new GetBillFormatPanel(this.formatpanel, "getWorkFlow")); //
		parser.addFunction("getWorkPanel", new GetBillFormatPanel(this.formatpanel, "getWorkPanel"));

		parser.addFunction("getSplit", new GetBillFormatPanel(this.formatpanel, "getSplit")); //
		parser.addFunction("getTab", new GetBillFormatPanel(this.formatpanel, "getTab")); //
		parser.addFunction("getRadioTab", new GetBillFormatPanel(this.formatpanel, "getRadioTab")); //按钮型多页签.
		parser.addFunction("getLevel", new GetBillFormatPanel(this.formatpanel, "getLevel")); //多层布局.
		parser.addFunction("getVFlowLayout", new GetBillFormatPanel(this.formatpanel, "getVFlowLayout")); //垂直布局.
		parser.addFunction("getBorderLayout", new GetBillFormatPanel(this.formatpanel, "getBorderLayout")); //BorderLayout的布局

		parser.addFunction("getChart", new GetBillFormatPanel(this.formatpanel, "getChart")); //
		parser.addFunction("getChartReal", new GetBillFormatPanel(this.formatpanel, "getChartReal")); //
		parser.addFunction("getPanel", new GetBillFormatPanel(this.formatpanel, "getPanel")); //
		parser.addFunction("getBom", new GetBillFormatPanel(this.formatpanel, "getBom")); //

		parser.addFunction("getRegisterFormat", new GetBillFormatPanel(this.formatpanel, "getRegisterFormat")); //公式面板,公式面板中还可以加入公式面板,即可以无限嵌套!
		parser.addFunction("getMultiReport", new GetBillFormatPanel(this.formatpanel, "getMultiReport")); //公式面板,公式面板中还可以加入公式面板,即可以无限嵌套!
	}

	/**
	 * 真正的执行公式!!
	 * @param _expr,真正的可执行的语法!!
	 * @return
	 */
	public Object execFormula(String _expr) {
		try {
			parser.parseExpression(_expr); //执行公式
			return parser.getValueAsObject(); //
		} catch (Throwable ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
