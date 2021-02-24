/**************************************************************************
 * $RCSfile: StyleConfigPanel_0A.java,v $  $Revision: 1.8 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.config;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTMouseAdapterAsMsgBox;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedEvent;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.MultiStyleTextPanel;
import cn.com.infostrategy.ui.mdata.formatcomp.FormatEventBindFormulaParse;
import cn.com.infostrategy.ui.mdata.styletemplet.AbstractTempletRefPars;

/**
 * 格式化面板配置页面...
 * 
 * @author xch
 * 
 */
public class StyleConfigPanel_0A extends AbstractTempletRefPars implements ActionListener, BillListMouseDoubleClickedListener {

	private static final long serialVersionUID = -6147862007359706108L;

	private WLTButton btn_adfont, btn_delfont, btn_resetfont, btn_clear;

	private MultiStyleTextPanel textPanel = null;
	private JTextField textField = null;

	private static int[][] column_width = null;
	private Vector v_btns = new Vector(); //

	private BillListPanel listPanel_formaElementList = null; //控件列表
	private BillListPanel listPanel_templet = null; //所有模板..
	private BillListPanel listPanel_regformat = null; //所有注册样板..
	private BillListPanel listPanel_eventBind = null; //事件绑定公式..

	private String[] str_items = null;

	private WLTTabbedPane tabPanel = null; //
	private JScrollPane jsp_sys_exp = null;
	private MouseAdapter adapter = null;
	private JTabbedPane jtp_detail = null;
	private JPanel southPanel = null;

	private StyledDocument doc = null;

	private SimpleAttributeSet sab_doc;
	private final static Font textPaneFont = new Font("宋体", Font.PLAIN, 16);//

	/**
	 * 构造方法...
	 * @param _text
	 */
	public StyleConfigPanel_0A(String _text) {
		this.setLayout(new BorderLayout());
		str_items = getStrs(_text); //
		btn_adfont = new WLTButton("字体+"); //
		btn_delfont = new WLTButton("字体-"); //
		btn_resetfont = new WLTButton("重置字体"); //
		btn_clear = new WLTButton("清空"); //
		btn_adfont.addActionListener(this); //
		btn_delfont.addActionListener(this); //
		btn_resetfont.addActionListener(this); //
		btn_clear.addActionListener(this); //
		JPanel panel_north = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
		panel_north.add(btn_adfont); //
		panel_north.add(btn_delfont); //
		panel_north.add(btn_resetfont); //
		panel_north.add(btn_clear); //

		textPanel = new MultiStyleTextPanel(str_items[0]); // 
		textPanel.addKeyWordStyle(new String[] { "getCard", "getList", "getTree", "getCell", "getWorkFlow", "getChart", "getChartReal", "getSplit", "getVFlowLayout", "getTab", "getPanel", // 
				"getRegisterFormat", "getQuery", "getButtons", "getRadioTab", "getLevel", "getBorderLayout", "getBom", "getMultiReport", "getLabel" }, Color.BLUE, false); //

		tabPanel = new WLTTabbedPane(); //
		listPanel_formaElementList = new BillListPanel(new ServerTMODefine("cn.com.infostrategy.bs.sysapp.servertmo.TMO_FormatPanelGetItem"));
		String[][] str_data = getFormatElementDefine();
		for (int i = 0; i < str_data.length; i++) {
			int li_newRow = listPanel_formaElementList.addEmptyRow(false); //
			listPanel_formaElementList.setValueAt(new StringItemVO(str_data[i][0]), li_newRow, "名称");
			listPanel_formaElementList.setValueAt(new StringItemVO(str_data[i][1]), li_newRow, "例子");
			listPanel_formaElementList.setValueAt(new StringItemVO(str_data[i][2]), li_newRow, "说明");
		}

		listPanel_templet = new BillListPanel(new ServerTMODefine("cn.com.infostrategy.bs.mdata.servertmo.TMO_PubTemplet_1")); //所有模板
		listPanel_regformat = new BillListPanel(new ServerTMODefine("cn.com.infostrategy.bs.mdata.servertmo.TMO_RegFormatTemplet")); //所有format模板
		listPanel_eventBind = new BillListPanel(new DefaultTMO("各种事件", new String[][] { { "名称", "120" }, { "说明", "150" } }));
		String[][] str_eventBind = getEventBindDefine(); //得到所有事件定义
		for (int i = 0; i < str_eventBind.length; i++) {
			int li_newRow = listPanel_eventBind.addEmptyRow(false); //
			listPanel_eventBind.setValueAt(new StringItemVO(str_eventBind[i][0]), li_newRow, "名称");
			listPanel_eventBind.setValueAt(new StringItemVO(str_eventBind[i][1]), li_newRow, "说明");
		}

		listPanel_formaElementList.addBillListMouseDoubleClickedListener(this); //
		listPanel_templet.addBillListMouseDoubleClickedListener(this); //
		listPanel_regformat.addBillListMouseDoubleClickedListener(this); //
		listPanel_eventBind.addBillListMouseDoubleClickedListener(this); //

		tabPanel.addTab("控件列表", listPanel_formaElementList); //
		tabPanel.addTab("元原模板", listPanel_templet); //
		tabPanel.addTab("注册样板", listPanel_regformat); //

		WLTSplitPane splitPane = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(textPanel), tabPanel); //
		splitPane.setDividerLocation(550); //

		this.add(panel_north, BorderLayout.NORTH);
		this.add(splitPane, BorderLayout.CENTER); //
		this.add(getSourthPanel(), BorderLayout.SOUTH); //
	}

	/**
	 * 加上事件定义的页签,在注册样板维护页面上,有一项叫事件定义的用到这个!
	 */
	public void addEventBindTab() {
		tabPanel.addTab("所有事件", listPanel_eventBind); //
	}

	/**
	 * 获得系统公式,即事件定义
	 * @return
	 */
	private String[][] getEventBindDefine() {
		Vector vec_function = FormatEventBindFormulaParse.getFunctionDetail();
		String[][] str_values = new String[vec_function.size()][];
		for (int i = 0; i < str_values.length; i++) {
			str_values[i] = (String[]) vec_function.get(i);
		}
		return str_values;
	}

	/**
	 * 拦截器的文本框
	 * @return
	 */
	public JPanel getSourthPanel() {
		if (southPanel != null) {
			return southPanel;
		}
		southPanel = WLTPanel.createDefaultPanel(new BorderLayout()); //
		JLabel label = new JLabel(" 拦截器: ", JLabel.RIGHT); //
		southPanel.add(label, BorderLayout.WEST); //
		textField = new JTextField(str_items[1]); //
		textField.addMouseListener(new WLTMouseAdapterAsMsgBox(textField, 2, "必须实现接口[cn.com.infostrategy.ui.mdata.styletemplet.format.IUIIntercept_0A]")); //
		textField.setPreferredSize(new Dimension(300, 20)); //
		textField.setForeground(Color.BLUE);
		southPanel.add(textField, BorderLayout.CENTER); //
		return southPanel; //
	}

	public VectorMap getParameters() {
		VectorMap map = new VectorMap(); //		
		map.put("FORMATFORMULA", textPanel.getText().trim()); //
		if (textField.getText() != null && !textField.getText().trim().equals("")) {
			map.put("INTERCEPTNAME", textField.getText().trim()); //
		}
		return map;
	}

	public void stopEdit() {

	}

	protected String bsInformation() {
		return null;
	}

	protected String uiInformation() {
		return null;
	}

	private String[] getStrs(String _text) {
		if (_text == null || _text.equals("")) {
			return new String[] { "", "" };
		}
		String[] str_return = new String[] { "", "" }; //
		int li_pos = _text.indexOf("INTERCEPTNAME");
		if (li_pos > 0) {
			str_return[0] = _text.substring(14, li_pos - 1); //
			str_return[1] = _text.substring(li_pos + 14, _text.length()); //
		} else {
			str_return[0] = _text.substring(14, _text.length());
		}
		return str_return; //
	}

	/**
	 * 点击按钮
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_adfont) {
			addFont(); //
		} else if (e.getSource() == btn_delfont) {
			deFont(); //
		} else if (e.getSource() == btn_resetfont) {
			textPanel.setFont(new Font("宋体", Font.PLAIN, 12)); //
			//textPanel.updateUI();
		} else if (e.getSource() == btn_clear) {
			textPanel.setText(""); //
		}
	}

	private void addFont() {
		int li_size = textPanel.getFont().getSize();
		int li_newSize = li_size + 3; //
		if (li_newSize > 60) {
			li_newSize = 60;
		}
		textPanel.setFont(new Font("宋体", Font.PLAIN, li_newSize)); //
		//textPanel.updateUI();
	}

	private void deFont() {
		int fontsize = textPanel.getFont().getSize();
		int li_newSize = fontsize - 3; //
		if (li_newSize < 8) {
			li_newSize = 8;
		}
		textPanel.setFont(new Font("宋体", Font.PLAIN, li_newSize)); //
		//textPanel.updateUI();
	}

	public WLTTabbedPane getTabPanel() {
		return tabPanel;
	}

	private void putStrIntextArea(String _text) {
		textPanel.inputText(_text); //
	}

	public JTextPane getTextArea() {
		return textPanel;
	}

	/**
	 * 双击
	 */
	public void onMouseDoubleClicked(BillListMouseDoubleClickedEvent _event) {
		if (_event.getBillListPanel() == listPanel_formaElementList) {
			int li_row = listPanel_formaElementList.getSelectedRow();
			String str_text = listPanel_formaElementList.getValueAt(li_row, "例子").toString();
			putStrIntextArea(str_text); //
		} else if (_event.getBillListPanel() == listPanel_templet) {

		} else if (_event.getBillListPanel() == listPanel_regformat) {

		}
	}

	/**
	 * 公式定义
	 * @return
	 */
	private String[][] getFormatElementDefine() {
		return new String[][] { //
		{ "标签", "getLabel(\"abcd\")", getHelpDescr_getLabel() }, //
				{ "卡片", "getCard(\"test001\")", "组件类容器，就是直接创建一个BillCardPanel对象，相当于直接调用new BillCardPanel(\"test001\")" }, //
				{ "列表", "getList(\"test001\")", "组件类容器，就是直接创建一个BillListPanel对象，相当于直接调用new BillListPanel(\"test001\")" }, //
				{ "树", "getTree(\"test001\")", "组件类容器，直接创建一个BillTreePanel对象，相当于直接调用new BillTreePanel(\"test001\")" }, //
				{ "查询框", "getQuery(\"test001\")", "组件类容器，创建一个快速查询面板，快速查询面板与卡片很相似，但会多一个查询按钮，对应的类是BillQueryPanel,且支持addBillQuickActionListener()方法，这里相当于调用了 new BillQueryPanel(\"test001\")" }, //查询框.待开发
				{ "按钮栏", "getButtons(\"新增\",\"修改\",\"删除\")", "组件类容器，创建一个按钮栏，所谓按钮栏就是一个横条栏面板上可以放一排按钮，对应的类是BillButtonPanel\r\n该公式支持任意多个参数，一个参数就是一个按钮,在按钮上点击右键还可以查看按钮详细信息" }, //按钮栏.待开发
				{ "Excel", "getCell(\"test001\")", "组件类容器，创建一个类Excel的控件，对应的类是BillCellPanel，相当于直接调用new BillCellPanel(\"test001\")" }, //
				{ "工作流", "getWorkFlow(\"test001\")", "组件类容器，创建一个工作流控件，对应的类是WorkFlowDesignWPanel" }, //
				{ "工作流", "getWorkPanel(\"模板编码\")", "组件类容器，创建一个工作处理界面，只显示对应模板的任务" }, //
				{ "演示图表", "getChart(\"风险统计表<风险数量>\",\"年度\",\"风险等级\",\"2005年,2006年,2007年,2008年,2009年,2010年,2011年\",\"极大风险,高风险,中等风险,低风险,极小风险\")", "组件类容器，创建一个演示的图表控件,对应的类是BillChartPanel" }, //
				{ "SQL图表", "getChartReal(\"销售情况统计表\",\"年度\",\"产量\",\"select a,b,count(*) from table group by a,b\",\"false\")", "组件类容器，根据一个SQL直接创建一个图表控件，对应的类是BillChartPanel" }, //

				{ "多维报表", "getMultiReport(\"pub_corp_dept_CODE1\",\"继承于抽象类:cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter\")", "组件类容器,万维报表" }, //
				{ "演示多维报表", "getMultiReport(\"WLTDUAL\",\"cn.com.infostrategy.bs.report.DemoReportBuilder\")", "组件类容器,为了快速演示用的万维报表" }, //每次去售前演示前都要做一个万维报表演示数据,很烦!所以必须像演示图表一样有个快速演示的万维报表!【xch/2012-06-11】

				{ "分割器", "getSplit(getLabel(\"左边内容\"),getLabel(\"右边内容\"),\"左右\",200)", getHelpDescr_getSplit() }, //
				{ "多页签", "getTab(\"第一页签\",getLabel(\"内容1\"),\"第二页签\",getLabel(\"内容2\"))", getHelpDescr_getTab() }, //
				{ "按钮型多页签", "getRadioTab(\"第一页签\",getLabel(\"内容1\"),\"第二页签\",getLabel(\"内容2\"))", getHelpDescr_getRadioTab() }, //按钮型多页签
				{ "多层", "getLevel(\"level_1\",getLabel(\"第一层\"),\"level_2\",getLabel(\"第二层\"))", "" }, //多层
				{ "垂直布局", "getVFlowLayout(getCard(\"test001\"),getList(\"test001\"),getCard(\"test001\"))", "" }, //
				{ "Border布局", "getBorderLayout(getQuery(\"test001\"),getList(\"test001\"),\"\",\"上中下\")", "" }, //还可以是左中右,就是使用BorderLayout布局,它与垂直布局的区别是中会自动撑满,中间的面板会自动变大

				{ "BOM图", "getBom(\"bom001\")", "" }, //
				{ "反射面板", "getPanel(\"cn.com.cmbc.TestPanel\")", "" }, //
				{ "注册样板", "getRegisterFormat(\"机构_岗位\")", "" }, //

		};
	}

	private String getHelpDescr_getLabel() {
		StringBuilder sb_help = new StringBuilder();
		sb_help.append("组件类容器，直接输出一个字符串,实际返回的是JPanel对象,里面包了个Jabel\r\n");
		sb_help.append("共有两种配置方式，一种是就一个参数比如getLabel(\"欢迎光临\")，一种是二个参数，比如:getLabel(\"content\",\"输出查询条件查询数据\"),第一个是标识用的编码，第二个参数才是实际显示的字符串，然后可以通过getLabelPanelByCode(\"content\")得到这个对象,进行重绘\r\n");
		sb_help.append("最常见一种配置就是上面上一个查询框，下面先是一个LabelPanel对象，然后点击查询时重绘对象！例码如下：\r\n");
		sb_help.append("public class Intercept_XCH implements IUIIntercept_0A, ActionListener {\r\n");
		sb_help.append("  \r\n");
		sb_help.append("  private BillQueryPanel queryPanel = null;  //定义查询框对象\r\n");
		sb_help.append("  private JPanel contentPanel = null;  //定义显示查询内容的面板对象\r\n");
		sb_help.append("  //实现接口方法，实现业务逻辑\r\n");
		sb_help.append("  public void afterSysInitialize(AbstractStyleWorkPanel_0A _panel) throws Exception {\r\n");
		sb_help.append("    queryPanel = _panel.getBillQueryPanel();  //先取得查询框句柄\r\n");
		sb_help.append("    contentPanel = _panel.getLabelPanelByCode(\"content\"); //取得显示内容的面板句柄，注意，就里就用到了根据编码去取的方法！\r\n");
		sb_help.append("    queryPanel.addBillQuickActionListener(this); //对查询框增加点击查询的监听对象\r\n");
		sb_help.append("  }\r\n");
		sb_help.append("\r\n");
		sb_help.append("  //点击查询后要做的逻辑,就是重绘面板\r\n");
		sb_help.append("  public void actionPerformed(ActionEvent e) {\r\n");
		sb_help.append("      contentPanel.removeAll(); //先将面板上所有内容删除掉\r\n");
		sb_help.append("      contentPanel.setLayout(new BorderLayout()); //重新设置布局，一定要做这一步动作，否则增加的控件会显示不了，这是经常易犯的错误！\r\n");
		sb_help.append("      contentPanel.add(new BillCellPanel(\"合规标准_09\")); //在面板上添加一个Excel面板，实际应该根据上面的查询条件进行拼成SQL语句条件等，进行相关业务逻辑，生成列表，卡片，树等各种组件，加入该面板中，也就是直正的业务逻辑所在！！\r\n");
		sb_help.append("      contentPanel.updateUI();  //刷新一下UI，有时不一定需要调用\r\n");
		sb_help.append("  }\r\n");
		sb_help.append("}\r\n");
		return sb_help.toString();
	}

	private String getHelpDescr_getSplit() {
		StringBuilder sb_help = new StringBuilder();
		sb_help.append("布局类容器，创建一个左右或上下分割的两个区域的布局，每个区域内可以是各种组件类容器\r\n");
		sb_help.append("一共四个参数，其中：\r\n");
		sb_help.append("第一个与第二个参数可以是任意一个组件类容器，比如getCard(\"test001\")或者getList(\"test002\")等..\r\n");
		sb_help.append("第三个参数有两种取值：\"左右\"与\"上下\",用来表示在什么方向上分割\r\n");
		sb_help.append("第四个参数表示分割条距上边或左边的距离");
		return sb_help.toString();
	}

	private String getHelpDescr_getTab() {
		StringBuilder sb_help = new StringBuilder();
		sb_help.append("布局类容器，创建一个多页签的布局，每个页签内可以是各种组件类容器\r\n");
		sb_help.append("可以有任意个参数，但必须是成双成对的，即参数数量必须是偶数，比如第1个与第2个是一对，第3个与第4个是一对\r\n");
		sb_help.append("在一对中，第一个参数会用来显示在页签栏上的字，这个字还支持[]内设置图片名字，比如[office_042.gif]浏览，即会在前面自己创建一个小图标。。\r\n");
		sb_help.append("第二个参数可以是各种组件类容器，比如卡片，列表，树等\r\n");
		sb_help.append("例子语法如：getTab(\"[office_050.gif]第一页签\",getList(\"test001\"),\"第二页签\",getCard(\"test002\"))");
		return sb_help.toString();
	}

	private String getHelpDescr_getRadioTab() {
		StringBuilder sb_help = new StringBuilder();
		sb_help.append("布局类容器，创建一个按钮型多页签的布局，上面是一排Radio(开关)型按钮，点击每一个会显示一个页面，它与getTab()本质上一样，就是上面展示有点不同，而这种Radio型按钮有的用户更喜欢些..\r\n");
		sb_help.append("可以有任意个参数，但必须是成双成对的，即参数数量必须是偶数，比如第1个与第2个是一对，第3个与第4个是一对\r\n");
		sb_help.append("在一对中，第一个参数会用来显示在页签栏上的字，这个字还支持[]内设置图片名字，比如[office_042.gif]浏览，即会在前面自己创建一个小图标。。\r\n");
		sb_help.append("第二个参数可以是各种组件类容器，比如卡片，列表，树等\r\n");
		sb_help.append("例子语法如：getTab(\"[office_050.gif]第一页签\",getList(\"test001\"),\"第二页签\",getCard(\"test002\"))");
		return sb_help.toString();
	}
}
