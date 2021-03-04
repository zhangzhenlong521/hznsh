package cn.com.infostrategy.ui.mdata.formatcomp;

import java.awt.BorderLayout;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTRadioPane;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillButtonPanel;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillFormatPanel;
import cn.com.infostrategy.ui.mdata.BillLevelPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.VFlowLayoutPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.infostrategy.ui.report.BillReportPanel;
import cn.com.infostrategy.ui.report.chart.BillChartPanel;
import cn.com.infostrategy.ui.sysapp.login.taskcenter.TaskAndMsgCenterPanel;
import cn.com.infostrategy.ui.workflow.design.WorkFlowDesignWPanel;
import cn.com.infostrategy.ui.workflow.pbom.BillBomPanel;

/**
 * 以前一个公式一个类,但每个类的代码非常少,所以搞成了一个类,将原来的类变成了这个类中的一个个方法!!!
 * 这些少了20个Java源文件!!!
 * @author xch
 *
 */
public class GetBillFormatPanel extends PostfixMathCommand {

	private BillFormatPanel formatpanel = null; //
	private String type = null; //

	public GetBillFormatPanel(BillFormatPanel _formatPanel, String _type) {
		numberOfParameters = -1; //不定参数!!!
		this.formatpanel = _formatPanel; //
		this.type = _type; //
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		if (type.equals("getLabel")) {
			getLabel(inStack); //
		} else if (type.equals("getCard")) {
			getCard(inStack); //
		} else if (type.equals("getList")) {
			getList(inStack); //
		} else if (type.equals("getTree")) {
			getTree(inStack); //
		} else if (type.equals("getQuery")) {
			getQuery(inStack); //
		} else if (type.equals("getButtons")) {
			getButtons(inStack); //
		} else if (type.equals("getCell")) {
			getCell(inStack); //
		} else if (type.equals("getWorkFlow")) {
			getWorkFlow(inStack); //
		} else if (type.equals("getSplit")) {
			getSplit(inStack); //
		} else if (type.equals("getTab")) {
			getTab(inStack); //
		} else if (type.equals("getRadioTab")) {
			getRadioTab(inStack); //
		} else if (type.equals("getLevel")) {
			getLevel(inStack); //
		} else if (type.equals("getVFlowLayout")) {
			getVFlowLayout(inStack); //
		} else if (type.equals("getBorderLayout")) {
			getBorderLayout(inStack); //
		} else if (type.equals("getChart")) {
			getChart(inStack); //
		} else if (type.equals("getChartReal")) {
			getChartReal(inStack); //
		} else if (type.equals("getPanel")) {
			getPanel(inStack); //
		}  else if (type.equals("getWorkPanel")) {
			getWorkPanel(inStack); //
		} else if (type.equals("getBom")) {
			getBom(inStack); //
		} else if (type.equals("getRegisterFormat")) {
			getRegisterFormat(inStack); //
		} else if (type.equals("getMultiReport")) {
			getMultiReport(inStack); //
		}

	}

	private void getLabel(Stack inStack) throws ParseException {
		checkStack(inStack);
		String[] str_pa = new String[curNumberOfParameters]; //取得所有参数!!
		for (int i = str_pa.length - 1; i >= 0; i--) { //倒叙获得函数参数
			str_pa[i] = (String) inStack.pop();
		}

		String str_code = null;
		String str_text = "";

		if (str_pa.length == 1) {
			str_text = str_pa[0];
		} else if (str_pa.length == 2) {
			str_code = str_pa[0];
			str_text = str_pa[1];
		}

		JPanel mainPanel = new JPanel(new BorderLayout()); //
		JLabel label = null;
		if (str_text.toUpperCase().indexOf(".JPG") > 0 || str_text.toUpperCase().indexOf(".GIF") > 0 || str_text.toUpperCase().indexOf(".PNG") > 0 || str_text.toUpperCase().indexOf(".BMP") > 0) {
			label = new JLabel(UIUtil.getImageFromServer("/applet/" + str_text));
		} else {
			label = new JLabel(str_text); //
		}

		JScrollPane scrollPanel = new JScrollPane(label); //因为有的时候图表比较大,所以要用一个滚动框包一下
		scrollPanel.setBorder(BorderFactory.createEmptyBorder()); //
		mainPanel.add(scrollPanel); //

		mainPanel.putClientProperty("BillFormatReg_labelPanelCode", str_code); //注册编码,以便可以根据编码找到该面板,然后重绘
		String _returnkey = "labelPanel_" + formatpanel.getCompentSeq(); //
		formatpanel.getPanelMap().put(_returnkey, mainPanel); //
		inStack.push(_returnkey); //
	}

	public void getCard(Stack inStack) throws ParseException {
		Object param_1 = inStack.pop();
		String templetCode = (String) param_1; //
		BillCardPanel cardPanel = new BillCardPanel(templetCode); //
		cardPanel.setEditable(true); //
		String _returnkey = "billcard_" + formatpanel.getCompentSeq(); //
		formatpanel.getPanelMap().put(_returnkey, cardPanel); ////
		cardPanel.setLoaderBillFormatPanel(formatpanel); //
		inStack.push(_returnkey); //
		//System.out.println(_returnkey + "=getList(\"" + templetCode + "\")"); //
	}

	public void getList(Stack inStack) throws ParseException {
		Object param_1 = inStack.pop();
		String templetCode = (String) param_1; //
		BillListPanel listPanel = new BillListPanel(templetCode); //
		String _returnkey = "billlist_" + formatpanel.getCompentSeq(); //
		formatpanel.getPanelMap().put(_returnkey, listPanel); //
		listPanel.setLoaderBillFormatPanel(formatpanel); //
		inStack.push(_returnkey); //
	}

	public void getTree(Stack inStack) throws ParseException {
		Object param_1 = inStack.pop();
		String templetCode = (String) param_1; //
		BillTreePanel treePanel = new BillTreePanel(templetCode); //
		String _returnkey = "billtree_" + formatpanel.getCompentSeq(); //
		formatpanel.getPanelMap().put(_returnkey, treePanel); ////
		treePanel.setLoaderBillFormatPanel(formatpanel); //
		inStack.push(_returnkey); //
	}

	public void getQuery(Stack inStack) throws ParseException {
		Object param_1 = inStack.pop();
		String templetCode = (String) param_1; //
		BillQueryPanel billQueryPanel = new BillQueryPanel(templetCode); //
		String _returnkey = "billquery_" + formatpanel.getCompentSeq(); //
		formatpanel.getPanelMap().put(_returnkey, billQueryPanel); ////
		//treePanel.setLoaderBillFormatPanel(formatpanel); //
		inStack.push(_returnkey); //
	}

	public void getButtons(Stack inStack) throws ParseException {
		checkStack(inStack);
		String[] compentNames = new String[curNumberOfParameters]; //取得所有参数!!
		for (int i = compentNames.length - 1; i >= 0; i--) { //倒叙获得函数参数
			compentNames[i] = (String) inStack.pop();
		}

		BillButtonPanel btnPanel = new BillButtonPanel(); //
		WLTButton[] btns = new WLTButton[compentNames.length];
		for (int i = 0; i < compentNames.length; i++) {
			if (compentNames[i].startsWith("[")) {
				String str_imageName = compentNames[i].substring(1, compentNames[i].indexOf("]")); //
				String str_textName = compentNames[i].substring(compentNames[i].indexOf("]") + 1, compentNames[i].length()); //
				btns[i] = new WLTButton(str_textName, UIUtil.getImage(str_imageName)); //
			} else {
				btns[i] = new WLTButton(compentNames[i]); //
			}
		}
		btnPanel.addBatchButton(btns);
		btnPanel.paintButton(); //
		String _returnkey = "billbuttons_" + formatpanel.getCompentSeq(); //
		formatpanel.getPanelMap().put(_returnkey, btnPanel); //
		inStack.push(_returnkey); //
	}

	public void getCell(Stack inStack) throws ParseException {
		Object param_1 = inStack.pop();
		String templetCode = (String) param_1; //
		BillCellPanel cellPanel = new BillCellPanel(templetCode); //
		cellPanel.setToolBarVisiable(false); //
		String _returnkey = "billcell_" + formatpanel.getCompentSeq(); //
		formatpanel.getPanelMap().put(_returnkey, cellPanel); ////
		inStack.push(_returnkey); //
		//System.out.println(_returnkey + "=getList(\"" + templetCode + "\")"); //
	}

	public void getWorkFlow(Stack inStack) throws ParseException {
		Object param_1 = inStack.pop();
		String templetCode = (String) param_1; //
		WorkFlowDesignWPanel wfpanel = null;
		if (templetCode.equals("")) {
			wfpanel = new WorkFlowDesignWPanel(false);
		} else {
			wfpanel = new WorkFlowDesignWPanel(templetCode, false);
		}

		String _returnkey = "workflow_" + formatpanel.getCompentSeq(); //
		formatpanel.getPanelMap().put(_returnkey, wfpanel); ////
		inStack.push(_returnkey); //
		//System.out.println(_returnkey + "=getList(\"" + templetCode + "\")"); //
	}

	public void getSplit(Stack inStack) throws ParseException {
		try {
			Object param_1 = inStack.pop();
			Object param_2 = inStack.pop();
			Object param_3 = inStack.pop();
			Object param_4 = inStack.pop();

			int li_location = 255; //
			if (param_1 instanceof Double) {
				li_location = ((Double) param_1).intValue();
			} else if (param_1 instanceof String) {
				li_location = new Double((String) param_1).intValue(); //
			}

			String splittype = (String) param_2; //右边的框
			int li_type = JSplitPane.HORIZONTAL_SPLIT;
			if (splittype.equals("左右")) {
				li_type = JSplitPane.HORIZONTAL_SPLIT;
			} else if (splittype.equals("上下")) {
				li_type = JSplitPane.VERTICAL_SPLIT;
			}

			String rightPanelKey = (String) param_3; //右边的框
			String leftPanelKey = (String) param_4; //左边的框
			//System.out.println("[" + rightPanelKey + "][" + rightPanelKey + "]"); //

			java.awt.Component compent_1 = (java.awt.Component) formatpanel.getPanelMap().get(leftPanelKey); //
			java.awt.Component compent_2 = (java.awt.Component) formatpanel.getPanelMap().get(rightPanelKey); //

			WLTSplitPane splitPane = new WLTSplitPane(li_type, compent_1, compent_2); //创建控件,并将两者加入
			splitPane.setDividerLocation(li_location); //

			String _returnkey = "split_" + formatpanel.getCompentSeq(); //
			formatpanel.getPanelMap().put(_returnkey, splitPane); //
			inStack.push(_returnkey); //
			//System.out.println(_returnkey + "=getSplit(\"" + leftPanelKey + "\",\"" + rightPanelKey + "\",\"" + splittype + "\",\"" + li_location + "\")"); //
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	public void getTab(Stack inStack) throws ParseException {
		try {
			checkStack(inStack);
			String[] str_pa = new String[curNumberOfParameters];
			for (int i = 0; i < str_pa.length; i++) {// 倒叙获得函数参数
				str_pa[i] = (String) inStack.pop();
				// System.out.println(str_pa[i]); //
			}

			String[] str_pa_convert = new String[str_pa.length]; //
			for (int i = 0; i < str_pa_convert.length; i++) {
				str_pa_convert[i] = str_pa[str_pa.length - i - 1]; //
				// System.out.println(str_pa_convert[i]); //
			}

			WLTTabbedPane tabbedPanel = new WLTTabbedPane();
			int li_dui = 0;
			li_dui = str_pa_convert.length / 2; // 看有几对..
			for (int i = 0; i < li_dui; i++) {
				String str_title = str_pa_convert[i * 2]; //
				String str_compentname = str_pa_convert[i * 2 + 1]; //
				JComponent compent = (JComponent) formatpanel.getPanelMap().get(str_compentname); //
				String[] str_imagetitles = getImageTile(str_title); //
				if (str_imagetitles[0] == null) {
					tabbedPanel.addTab(str_imagetitles[1], compent); //
				} else {
					tabbedPanel.addTab(str_imagetitles[1], UIUtil.getImage(str_imagetitles[0]), compent); //
				}
			}
			String _returnkey = "tab_" + formatpanel.getCompentSeq(); //
			formatpanel.getPanelMap().put(_returnkey, tabbedPanel); //
			inStack.push(_returnkey); //
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	private String[] getImageTile(String _title) {
		if (_title.indexOf("[") < 0) {
			return new String[] { null, _title }; //
		} else {
			int li_pos_1 = _title.indexOf("["); //
			int li_pos_2 = _title.indexOf("]"); //
			String str_images = _title.substring(li_pos_1 + 1, li_pos_2);
			String str_title = _title.substring(li_pos_2 + 1, _title.length());
			return new String[] { str_images, str_title };
		}
	}

	public void getRadioTab(Stack inStack) throws ParseException {
		try {
			checkStack(inStack);
			String[] str_pa = new String[curNumberOfParameters];
			for (int i = 0; i < str_pa.length; i++) {// 倒叙获得函数参数
				str_pa[i] = (String) inStack.pop();
			}

			String[] str_pa_convert = new String[str_pa.length]; //
			for (int i = 0; i < str_pa_convert.length; i++) {
				str_pa_convert[i] = str_pa[str_pa.length - i - 1]; //
			}

			WLTRadioPane tabbedPanel = new WLTRadioPane();
			int li_dui = 0;
			li_dui = str_pa_convert.length / 2; // 看有几对..
			for (int i = 0; i < li_dui; i++) {
				String str_title = str_pa_convert[i * 2]; //
				String str_compentname = str_pa_convert[i * 2 + 1]; //
				JComponent compent = (JComponent) formatpanel.getPanelMap().get(str_compentname); //
				String[] str_imagetitles = getImageTile(str_title); //
				if (str_imagetitles[0] == null) {
					tabbedPanel.addTab(str_imagetitles[1], compent); //
				} else {
					tabbedPanel.addTab(str_imagetitles[1], str_imagetitles[0], compent); //
				}
			}
			String _returnkey = "radiotab_" + formatpanel.getCompentSeq(); //
			formatpanel.getPanelMap().put(_returnkey, tabbedPanel); //
			inStack.push(_returnkey); //
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	public void getLevel(Stack inStack) throws ParseException {
		try {
			checkStack(inStack);
			String[] str_pa = new String[curNumberOfParameters]; //取得所有参数!!
			for (int i = 0; i < str_pa.length; i++) {//倒叙获得函数参数
				str_pa[i] = (String) inStack.pop();
			}

			String[] str_pa_convert = new String[str_pa.length]; //
			for (int i = 0; i < str_pa_convert.length; i++) {
				str_pa_convert[i] = str_pa[str_pa.length - i - 1]; //
			}

			BillLevelPanel levelPanel = new BillLevelPanel(); //
			int li_dui = str_pa_convert.length / 2; //看有几对..
			for (int i = 0; i < li_dui; i++) {
				String str_title = str_pa_convert[i * 2]; //
				String str_compentname = str_pa_convert[i * 2 + 1]; //
				java.awt.Component compent = (java.awt.Component) formatpanel.getPanelMap().get(str_compentname); //
				levelPanel.addLevelPanel(str_title, compent); //
			}

			String _returnkey = "billlevel_" + formatpanel.getCompentSeq(); //
			formatpanel.getPanelMap().put(_returnkey, levelPanel); //
			inStack.push(_returnkey); //
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	public void getVFlowLayout(Stack inStack) throws ParseException {
		try {
			checkStack(inStack);
			String[] compentNames = new String[curNumberOfParameters]; //取得所有参数!!
			for (int i = compentNames.length - 1; i >= 0; i--) { //倒叙获得函数参数
				compentNames[i] = (String) inStack.pop();
			}

			JComponent[] compents = new JComponent[compentNames.length]; //
			for (int i = 0; i < compents.length; i++) {
				compents[i] = (javax.swing.JComponent) formatpanel.getPanelMap().get(compentNames[i]); //
			}

			VFlowLayoutPanel flowPanel = new VFlowLayoutPanel(compents); //
			String _returnkey = "billvflow_" + formatpanel.getCompentSeq(); //
			formatpanel.getPanelMap().put(_returnkey, flowPanel); //
			inStack.push(_returnkey); //
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	public void getBorderLayout(Stack inStack) throws ParseException {
		try {
			checkStack(inStack);
			String[] compentNames = new String[4]; //取得所有参数!!
			for (int i = compentNames.length - 1; i >= 0; i--) { //倒叙获得函数参数
				compentNames[i] = (String) inStack.pop();
			}

			String str_type = "上中下";
			String str_lay_1 = BorderLayout.NORTH;
			String str_lay_3 = BorderLayout.SOUTH;

			if (compentNames[3].equals("左中右")) {
				str_type = compentNames[3];
				str_lay_1 = BorderLayout.WEST;
				str_lay_3 = BorderLayout.EAST;
			}

			JComponent compent_1 = null;
			JComponent compent_2 = null;
			JComponent compent_3 = null;

			JPanel contentPanel = new JPanel(new BorderLayout());
			if (!compentNames[0].trim().equals("")) {
				compent_1 = (javax.swing.JComponent) formatpanel.getPanelMap().get(compentNames[0]); //
				//compent_1.setPreferredSize(new Dimension(200, 100)); //
				contentPanel.add(compent_1, str_lay_1); //
			}
			if (!compentNames[1].trim().equals("")) {
				compent_2 = (javax.swing.JComponent) formatpanel.getPanelMap().get(compentNames[1]); //
				contentPanel.add(compent_2, BorderLayout.CENTER); //
			}
			if (!compentNames[2].trim().equals("")) {
				compent_3 = (javax.swing.JComponent) formatpanel.getPanelMap().get(compentNames[2]); //
				contentPanel.add(compent_3, str_lay_3); //
			}

			String _returnkey = "billborderlayout_" + formatpanel.getCompentSeq(); //
			formatpanel.getPanelMap().put(_returnkey, contentPanel); //
			inStack.push(_returnkey); //
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	public void getChart(Stack inStack) throws ParseException {
		Object param_1 = inStack.pop(); //types[]
		Object param_2 = inStack.pop(); //months[]
		Object param_3 = inStack.pop(); //vaues
		Object param_4 = inStack.pop(); //months
		Object param_5 = inStack.pop(); //title

		String str_title = (String) param_5;
		String str_xlabel = (String) param_4;
		String str_ylabel = (String) param_3;

		String[] str_xs = ((String) param_1).split(",");
		String[] str_ys = ((String) param_2).split(",");

		BillChartPanel chartPanel = new BillChartPanel(str_title, str_xlabel, str_ylabel, str_xs, str_ys); //

		String _returnkey = "billchart_" + formatpanel.getCompentSeq(); //
		formatpanel.getPanelMap().put(_returnkey, chartPanel); ////
		inStack.push(_returnkey); //
		//System.out.println(_returnkey + "=getList(\"" + templetCode + "\")"); //
	}

	public void getChartReal(Stack inStack) throws ParseException {
		Object param_1 = inStack.pop();
		Object param_2 = inStack.pop();
		Object param_3 = inStack.pop();
		Object param_4 = inStack.pop();
		Object param_5 = inStack.pop();

		String str_title = (String) param_5;
		String str_xlabel = (String) param_4;
		String str_ylabel = (String) param_3;
		String str_sql = (String) param_2;
		String bool = (String) param_1;
		if (bool.equals("false")) {
			BillChartPanel chartPanel = BillChartPanel.getChartPanelBySQL(str_title, str_xlabel, str_ylabel, str_sql, false); //

			String _returnkey = "billchart_" + formatpanel.getCompentSeq(); //
			formatpanel.getPanelMap().put(_returnkey, chartPanel); // //
			inStack.push(_returnkey); //

		} else {
			BillChartPanel chartPanel = BillChartPanel.getChartPanelBySQL(str_title, str_xlabel, str_ylabel, str_sql, true); //

			String _returnkey = "billchart_" + formatpanel.getCompentSeq(); //
			formatpanel.getPanelMap().put(_returnkey, chartPanel); // //
			inStack.push(_returnkey); //

		}
	}

	public void getPanel(Stack inStack) throws ParseException {
		Object param_1 = inStack.pop(); //types[]
		String str_classname = (String) param_1;

		try {
			JPanel panael = null;
			if (str_classname.indexOf(".") > 0) { //如果是类名..
				panael = (JPanel) Class.forName(str_classname).newInstance(); //
				if (panael instanceof AbstractWorkPanel) {
					AbstractWorkPanel workPanel = (AbstractWorkPanel) panael; //
					workPanel.initialize(); //
				}
			} else {
				panael = new JPanel(new BorderLayout()); //
				panael.add(new JLabel(str_classname)); //
			}
			String _returnkey = "panel_" + str_classname + "_" + formatpanel.getCompentSeq(); //
			formatpanel.getPanelMap().put(_returnkey, panael); ////
			inStack.push(_returnkey); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
		//System.out.println(_returnkey + "=getList(\"" + templetCode + "\")"); //
	}
	
	public void getWorkPanel(Stack inStack) throws ParseException {
		Object param_1 = inStack.pop();
		String templetcode = (String) param_1;
		try {
			JPanel panel = TaskAndMsgCenterPanel.getChildWorkPanel(templetcode, false);
			String _returnkey = "panel_" + templetcode + "_" + formatpanel.getCompentSeq();
			formatpanel.getPanelMap().put(_returnkey, panel);
			inStack.push(_returnkey);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void getBom(Stack inStack) throws ParseException {
		Object param_1 = inStack.pop(); //types[]
		String str_bomcode = (String) param_1;

		try {
			BillBomPanel bompanel = new BillBomPanel(str_bomcode); //
			String _returnkey = "billbom_" + formatpanel.getCompentSeq(); //
			formatpanel.getPanelMap().put(_returnkey, bompanel); ////
			inStack.push(_returnkey); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
		//System.out.println(_returnkey + "=getList(\"" + templetCode + "\")"); //
	}

	public void getRegisterFormat(Stack inStack) throws ParseException {
		Object param_1 = inStack.pop();
		String templetCode = (String) param_1; //模板编码
		BillFormatPanel billFormatPanel = BillFormatPanel.getRegisterFormatPanel(templetCode); //
		String _returnkey = "billformat_" + formatpanel.getCompentSeq(); //
		formatpanel.getPanelMap().put(_returnkey, billFormatPanel); //
		formatpanel.getPanelMap().putAll(billFormatPanel.getPanelMap()); //将子FormatPanel的所有数据对象装进父亲Format
		inStack.push(_returnkey); //
	}

	public void getMultiReport(Stack inStack) throws ParseException {
		Object param_1 = inStack.pop();
		String classname = "";
		Object param_2 = inStack.pop();
		String templetCode = "";
		if (param_1 != null) {
			classname = (String) param_1; //
		}
		if (param_2 != null) {
			templetCode = (String) param_2; //
		}
		BillReportPanel reportPanel = new BillReportPanel(templetCode, classname); //
		String _returnkey = "billreport_" + formatpanel.getCompentSeq(); //
		formatpanel.getPanelMap().put(_returnkey, reportPanel); ////
		inStack.push(_returnkey); //
	}

}
