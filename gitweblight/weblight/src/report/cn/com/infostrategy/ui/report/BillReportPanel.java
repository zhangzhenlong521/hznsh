package cn.com.infostrategy.ui.report;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.math.BigDecimal;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;
import cn.com.infostrategy.to.report.BillChartItemVO;
import cn.com.infostrategy.to.report.BillChartVO;
import cn.com.infostrategy.to.report.ReportUtil;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTPanelUI;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;
import cn.com.infostrategy.ui.report.chart.BillChartPanel;

/**
 * 目前是最强大的报表工具,但只限于对单个二维表格的各维度的钻取!! 并不能做报表合并!
 * 只是比较炫的地方是,可以自定义维度!!
 * @author xch
 */
public class BillReportPanel extends JPanel implements ActionListener, ItemListener {

	private static final long serialVersionUID = 1L;
	private String str_builderClassName = null; //

	private String[] allCanGroupFields; //
	private String[] allCanSumFields;
	private HashMap groupFieldOrder; //排序条件!!

	private BillQueryPanel billQueryPanel = null;

	private JLabel groupLabel = null; //
	private JComboBox comboBox_beforeHand = null; //预置的维度!!
	private WLTButton btn_chooseGroup = null; //自定义维度报表!!
	private BeforeHandGroupTypeVO custGroupVO = null; //自定义选择的维度
	private MultiLevelChooseGroupDialog chooseGroupDialog = null; //自定义选择维度窗口??

	private JPanel contentPanel = null; // 主内容
	private ReportCellPanel currReportCellPanel = null; //如果是表格报表时的面板!
	private BillChartPanel chartPanel = null; //图表报表时的面板!

	private JPanel WLPP = null; // 给外部一个接口 用于扩展 条件面板
	private BillRadarPanel spliderPanel = null;
	private String drillClassPath, drillTempletCode;
	private HashMap drillgroupbindMap = null; //钻取预置绑定的Map
	private HashMap zeroReportConfMap = null; //零汇报机制的Map
	private HashMap dateGroupDefineMap = null; //时间维度定义字段!
	//private HashMap secondHashVOComputeMap = null; //对HashVO二次计算的Map,这个只适用于对过滤后的数据进行再排序的情况!
	private TBUtil tbUtil = null; //工具类!
	private Icon icon = UIUtil.getImage("office_134.gif");

	//下面变量为袁江晓添加  主要解决报表部分数据权限问题
	private Pub_Templet_1VO pub_Templet_vo = null; //袁江晓20120913添加，主要为了处理数据权限  配合getDataPolicyCondition获得权限方法
	private FrameWorkMetaDataServiceIfc metaDataService = null; //元数据远程服务.
	private String str_dataPolicyInfo = null; //数据权限的说明
	private String str_dataPolicySQL = null; //数据权限的说明
	private String reportExpName = null;
	private String graphName = "图表";//报表维度类型为“图表”时，图上面显示文字【李春娟/2016-01-26】

	/**
	 * 送入一个模板编码,一个反射类名,就可以构造一个报表模板了!
	 * @param _templetCode
	 * @param _classname,必须继承于抽象类【cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter】或实现于接口【cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderIFC】
	 */
	public BillReportPanel(String _templetCode, String _classname) {
		super();
		try {
			Pub_Templet_1VO templetVO = UIUtil.getPub_Templet_1VO(_templetCode);
			initialize(templetVO, _classname); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	public BillReportPanel(String _graphName, String _templetCode, String _classname) {
		super();
		try {
			this.graphName = _graphName;
			Pub_Templet_1VO templetVO = UIUtil.getPub_Templet_1VO(_templetCode);
			initialize(templetVO, _classname); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	public BillReportPanel(AbstractTMO _abstractTempletVO, String _classname) {
		super();
		try {
			Pub_Templet_1VO templetVO = UIUtil.getPub_Templet_1VO(_abstractTempletVO);
			initialize(templetVO, _classname); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	//构造页面!!
	private void initialize(Pub_Templet_1VO _templetVO, String _classname) {
		this.str_builderClassName = _classname; //

		JPanel temp_panel = new JPanel(new BorderLayout(0, 10)); //
		temp_panel.setOpaque(false); //透明!!!

		this.pub_Templet_vo = _templetVO;
		reportExpName = this.pub_Templet_vo.getTempletname();
		//查询面板!
		billQueryPanel = new BillQueryPanel(_templetVO, true, getDataPolicyCondition()); // 不带查询按钮  //袁江晓20120913修改，加入最后一个参数表示报表的数据权限
		billQueryPanel.addBillQuickActionListener(this); //
		temp_panel.add(billQueryPanel, BorderLayout.NORTH); //加入查询面板

		//主表格面板!
		contentPanel = new JPanel(new BorderLayout()); //WLTPanel.createDefaultPanel(new BorderLayout()); //
		contentPanel.setOpaque(false); //透明!!
		BillCellPanel cellPanel = new BillCellPanel(null, true, false, true); //手工创建一个默认的表格!
		cellPanel.setEditable(false);
		cellPanel.setAllowShowPopMenu(false);
		cellPanel.span(0, 2, 2, 4); //
		cellPanel.setValueAt("请输入查询条件后点击查询按钮!", 0, 2); //
		cellPanel.setHalign(new int[] { 0 }, new int[] { 2 }, 2); //
		cellPanel.setCrystal(true); //水晶,透明!!!
		contentPanel.add(cellPanel); //
		temp_panel.add(contentPanel, BorderLayout.CENTER); //加入默认表格面板!

		//主布局
		this.setLayout(new BorderLayout(0, 0)); //
		this.setBackground(LookAndFeel.defaultShadeColor1); //
		this.setUI(new WLTPanelUI(WLTPanel.INCLINE_NW_TO_SE, Color.WHITE, true)); //最底层的是渐变!!
		this.add(getReportTypePanel(), BorderLayout.NORTH);
		this.add(temp_panel, BorderLayout.CENTER); //
	}

	/**
	 * 报表类型
	 * @return
	 */
	private JPanel getReportTypePanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5)); //
		panel.setOpaque(false); //
		groupLabel = new JLabel("报表维度类型", SwingConstants.RIGHT); //
		groupLabel.setPreferredSize(new Dimension(110, 25)); //选择自定义维度后，JLabel前面会设置一个图标，在字体放大的情况下导致文字显示不全，故这里增加宽度【李春娟/2019-03-25】
		comboBox_beforeHand = new JComboBox(); //
		comboBox_beforeHand.setEditable(true); //
		comboBox_beforeHand.setMaximumRowCount(25); //最大一下子显示25行,即让领导一眼清知道所有维度!!省得不知道下面还有个图表型的报表!!
		comboBox_beforeHand.setFocusable(false); //
		comboBox_beforeHand.addItemListener(this); //
		try {
			ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class); //
			HashMap hm_return = service.getMultiLevelReportGroup(this.str_builderClassName); //根据类名去服务器端取得
			allCanGroupFields = (String[]) hm_return.get("AllGroupFields"); //所有的组
			allCanSumFields = (String[]) hm_return.get("AllComputeFields"); //所有的计算列
			groupFieldOrder = (HashMap) hm_return.get("groupfieldorder"); //得到排序字段，以前是默认string排序，很多地方都有问题。所以必须指定排序方式！
			drillClassPath = (String) hm_return.get("drillclass"); //从服务器端得到客户端实现的类的路径
			drillTempletCode = (String) hm_return.get("drilltempletcode"); //钻取明细时弹出的列表模板编码!!
			drillgroupbindMap = (HashMap) hm_return.get("drillgroupbind"); //组绑定的定义
			zeroReportConfMap = (HashMap) hm_return.get("zeroReportConfMap"); //零汇报机制定义的Map！！
			dateGroupDefineMap = (HashMap) hm_return.get("dateGroupDefineMap"); //时间字段维度！！

			BeforeHandGroupTypeVO[] beforeHandTypeVOs_grid = (BeforeHandGroupTypeVO[]) hm_return.get("BeforeHandGroupType_Grid"); //
			BeforeHandGroupTypeVO[] beforeHandTypeVOs_chart = (BeforeHandGroupTypeVO[]) hm_return.get("BeforeHandGroupType_Chart"); //
			BeforeHandGroupTypeVO[] beforeHandTypeVOs_splider = (BeforeHandGroupTypeVO[]) hm_return.get("BeforeHandGroupType_Splider"); //雷达图!
			int li_maxlength = 55; //
			if (beforeHandTypeVOs_grid != null) {
				for (int i = 0; i < beforeHandTypeVOs_grid.length; i++) {
					beforeHandTypeVOs_grid[i].setType("grid"); //
					if (getTBUtil().getSysOptionBooleanValue("报表种类是否去星标记", false)) {
						beforeHandTypeVOs_grid[i].setName("表格-" + beforeHandTypeVOs_grid[i].getName()); //邮储样式 【杨科/2012-09-07】
					} else {
						beforeHandTypeVOs_grid[i].setName("☆表格--" + beforeHandTypeVOs_grid[i].getName());
					}
					comboBox_beforeHand.addItem(beforeHandTypeVOs_grid[i]); //
					int li_length = SwingUtilities.computeStringWidth(comboBox_beforeHand.getFontMetrics(comboBox_beforeHand.getFont()), beforeHandTypeVOs_grid[i].getName()); //
					if (li_length > li_maxlength) {
						li_maxlength = li_length;
					}
				}
			}

			if (beforeHandTypeVOs_chart != null) {
				for (int i = 0; i < beforeHandTypeVOs_chart.length; i++) {
					beforeHandTypeVOs_chart[i].setType("chart"); //
					if (getTBUtil().getSysOptionBooleanValue("报表种类是否去星标记", false)) {
						beforeHandTypeVOs_chart[i].setName("图表-" + beforeHandTypeVOs_chart[i].getName()); //邮储样式 【杨科/2012-09-07】
					} else {
						beforeHandTypeVOs_chart[i].setName("★图表--" + beforeHandTypeVOs_chart[i].getName());
					}

					comboBox_beforeHand.addItem(beforeHandTypeVOs_chart[i]); //
					int li_length = SwingUtilities.computeStringWidth(comboBox_beforeHand.getFontMetrics(comboBox_beforeHand.getFont()), beforeHandTypeVOs_chart[i].getName()); //
					if (li_length > li_maxlength) {
						li_maxlength = li_length;
					}
				}
			}

			if (beforeHandTypeVOs_splider != null) {
				for (int i = 0; i < beforeHandTypeVOs_splider.length; i++) {
					beforeHandTypeVOs_splider[i].setType("splider"); //
					beforeHandTypeVOs_splider[i].setName("▲雷达图--" + beforeHandTypeVOs_splider[i].getName());
					comboBox_beforeHand.addItem(beforeHandTypeVOs_splider[i]); //
					int li_length = SwingUtilities.computeStringWidth(comboBox_beforeHand.getFontMetrics(comboBox_beforeHand.getFont()), beforeHandTypeVOs_splider[i].getName()); //
					if (li_length > li_maxlength) {
						li_maxlength = li_length;
					}
				}
			}
			comboBox_beforeHand.setPreferredSize(new Dimension(li_maxlength + 25, 22)); //

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		panel.add(groupLabel); //
		panel.add(comboBox_beforeHand); //

		btn_chooseGroup = new WLTButton("自定义维度", "office_135.gif"); //
		btn_chooseGroup.setToolTipText("自由选择维度后,点击查询按钮!"); //
		btn_chooseGroup.addActionListener(this); //
		btn_chooseGroup.addCustPopMenuItem("查看Demo报表", "office_181.gif", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onRunDemoReport(); //运行例子报表
			}
		}); //
		panel.add(btn_chooseGroup); //

		WLPP = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2)); //
		WLPP.setOpaque(false); //透明
		panel.add(WLPP); //

		return panel; //
	}

	/**
	 * 获取【自定义维度】按钮，可在项目中设置不显示【李春娟/2018-12-06】
	 * @return
	 */
	public WLTButton getBtn_chooseGroup() {
		return btn_chooseGroup;
	}

	/**
	 * 查询数据..
	 */
	private void onQueryData(boolean _isCtrlDown) {
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
		try {
			BeforeHandGroupTypeVO groupVO = null; //
			if (custGroupVO != null) {
				groupVO = custGroupVO;
			} else {
				groupVO = (BeforeHandGroupTypeVO) comboBox_beforeHand.getSelectedItem(); //
			}
			if (groupVO == null) {
				MessageBox.show(this, "请选择一个报表类型(预设的或自定义的)!"); //
				return; //
			}

			HashMap queryConsMap = billQueryPanel.getQuickQueryConditionAsMap(true, _isCtrlDown ? false : true); //先算出查询条件,如果有必须项的查询条件而没输入,则直接返回!
			if (queryConsMap == null) {
				return; //
			}

			String[][] computeFunAndFields = new String[groupVO.getComputeGroupFields().length][3]; //实际上是3列!
			for (int i = 0; i < groupVO.getComputeGroupFields().length; i++) { //
				if (groupVO.getComputeGroupFields()[i].length == 3) { //如果有3位
					computeFunAndFields[i] = new String[] { groupVO.getComputeGroupFields()[i][1], groupVO.getComputeGroupFields()[i][2], groupVO.getComputeGroupFields()[i][0] }; //cout,count,风险事件!
				} else { //如果只有2位
					computeFunAndFields[i] = new String[] { groupVO.getComputeGroupFields()[i][1], "", groupVO.getComputeGroupFields()[i][0] }; //cout,count,风险事件!
				}
			}
			queryConsMap.put("obj_rowheader", groupVO.getRowHeaderGroupFields()); //条件中传入现在选择的维度。后台可以根据选择的维度对返回的HashVo做逻辑处理。
			queryConsMap.put("obj_colheader", groupVO.getColHeaderGroupFields()); //后台得到的是维度数组。
			queryConsMap.put("obj_$typename", groupVO.getName()); //把选择统计类型名称传到后台

			if (groupVO.getType().equalsIgnoreCase("GRID")) { //如果是万维网络型的！！！
				currReportCellPanel = new ReportCellPanel(this.str_builderClassName, this.allCanGroupFields, this.allCanSumFields, groupVO.getRowHeaderGroupFields(), groupVO.getColHeaderGroupFields(), computeFunAndFields, groupVO.getFilterGroupValueMap(), groupVO.getSecondHashVOComputeMap(), groupVO.getRowHeaderFormulaGroupMap(), groupVO.isTotal(), groupVO.isRowGroupTiled(), groupVO
						.isRowGroupSubTotal(), groupVO.isColGroupTiled(), groupVO.isColGroupSubTotal(), groupVO.isSortByCpValue(), queryConsMap, null, groupFieldOrder, zeroReportConfMap, dateGroupDefineMap, _isCtrlDown); //
				currReportCellPanel.setStr_drillActionClass(drillClassPath); //设置反射类!
				currReportCellPanel.setStr_drillTempletCode(drillTempletCode); //设置模板编码!
				currReportCellPanel.setDrillgroupbindMap(drillgroupbindMap); //设置绑定的Map
				currReportCellPanel.putClientProperty("templetname", reportExpName);
				this.contentPanel.removeAll();
				this.contentPanel.setLayout(new BorderLayout()); //
				this.contentPanel.add(currReportCellPanel); //重新加入!!
				this.contentPanel.updateUI(); //
			} else if (groupVO.getType().equalsIgnoreCase("CHART")) { //如果是图表数据!
				String[] str_groupFields = new String[groupVO.getRowHeaderGroupFields().length + groupVO.getColHeaderGroupFields().length];
				System.arraycopy(groupVO.getRowHeaderGroupFields(), 0, str_groupFields, 0, groupVO.getRowHeaderGroupFields().length);
				System.arraycopy(groupVO.getColHeaderGroupFields(), 0, str_groupFields, groupVO.getRowHeaderGroupFields().length, groupVO.getColHeaderGroupFields().length);
				ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class); //
				HashVO[] hvs_data = service.queryMultiLevelReportData(queryConsMap, this.str_builderClassName, str_groupFields, computeFunAndFields, _isCtrlDown); // 作为表格来说这个结果集只会出现三列或二列,否则就会有问题!
				BillChartVO chartVO = new ReportUtil().convertHashVOToChartVO(hvs_data, groupFieldOrder, computeFunAndFields); //将合并后的数据转换成图表的BillChartVO对象 袁江晓20121108添加第三个参数，表示图表按照何种方式来计算，之前的只支持按照count来计算
				if (chartVO == null || chartVO.getDataVO() == null) {
					MessageBox.show(this, "没有符合条件的数据！");
					return;
				}

				//如果行与列为空...
				if (groupVO.getRowHeaderGroupFields().length == 0 || groupVO.getColHeaderGroupFields().length == 0) {
					if (groupVO.getRowHeaderGroupFields().length == 0) {
						chartPanel = new BillChartPanel(this.graphName, "", groupVO.getColHeaderGroupFields()[0], convertHashVOToChartVO(hvs_data)); //
					} else {
						chartPanel = new BillChartPanel(this.graphName, groupVO.getRowHeaderGroupFields()[0], "", convertHashVOToChartVO(hvs_data)); //
					}
				} else {
					chartPanel = new BillChartPanel(this.graphName, groupVO.getColHeaderGroupFields()[0], groupVO.getRowHeaderGroupFields()[0], chartVO); //郝明修改，行列标题
				}
				chartPanel.setStr_builderClassName(str_builderClassName); //
				chartPanel.setAllCanGroupFields(allCanGroupFields); //
				chartPanel.setQueryConditionMap(queryConsMap); //
				chartPanel.setStr_drillActionClass(drillClassPath); //
				chartPanel.setStr_drillTempletCode(drillTempletCode);

				contentPanel.removeAll(); //
				contentPanel.setLayout(new BorderLayout()); //
				contentPanel.add(chartPanel, BorderLayout.CENTER); //
				contentPanel.updateUI(); //
			} else if (groupVO.getType().equalsIgnoreCase("splider")) { //如果是雷达图
				String[] str_groupFields = new String[groupVO.getRowHeaderGroupFields().length + groupVO.getColHeaderGroupFields().length];
				System.arraycopy(groupVO.getRowHeaderGroupFields(), 0, str_groupFields, 0, groupVO.getRowHeaderGroupFields().length);
				System.arraycopy(groupVO.getColHeaderGroupFields(), 0, str_groupFields, groupVO.getRowHeaderGroupFields().length, groupVO.getColHeaderGroupFields().length);
				ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class); //
				HashVO[] hvs_data = service.queryMultiLevelReportData(queryConsMap, this.str_builderClassName, str_groupFields, computeFunAndFields, _isCtrlDown);
				spliderPanel = new BillRadarPanel("", hvs_data, groupVO.getRowHeaderGroupFields(), groupVO.getColHeaderGroupFields(), groupVO.getComputeGroupFields()[0][0], true);
				contentPanel.removeAll(); //
				contentPanel.setLayout(new BorderLayout()); //
				contentPanel.add(spliderPanel, BorderLayout.CENTER); //
				contentPanel.updateUI(); //
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		} finally {
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == billQueryPanel) {
			onQueryData(Boolean.parseBoolean(e.getActionCommand())); //
		} else if (e.getSource() == btn_chooseGroup) {
			onChooseGroupField(); //
		}
	}

	//预定义维度下拉框选择变化的事件
	public void itemStateChanged(ItemEvent evt) {
		if (evt.getStateChange() == ItemEvent.SELECTED) {
			this.groupLabel.setIcon(null); //
			this.groupLabel.setForeground(Color.BLACK); //
			custGroupVO = null; //当发生变化时,立即清空自下义维度!
		}
	}

	/**
	 * 运行例子报表!!即运行系统的DemoReportBuilder
	 */
	private void onRunDemoReport() {
		//MessageBox.show(this, "运行系统Demo报表!");  //
		JFrame frame = new JFrame();
		frame.setTitle("Demo Report"); //
		frame.setSize(1027, 768); //
		frame.setLocation(30, 20);
		BillReportPanel reportDemoPane = new BillReportPanel("WLTDUAL", "cn.com.infostrategy.bs.report.DemoReportBuilder");//demo报表模板
		frame.setLayout(new BorderLayout());
		frame.getContentPane().add(reportDemoPane);
		frame.setVisible(true);
	}

	/**
	 * 选择组
	 */
	private void onChooseGroupField() {
		try {
			if (chooseGroupDialog == null) {
				chooseGroupDialog = new MultiLevelChooseGroupDialog(this, allCanGroupFields, allCanSumFields); //弹出选择自定义维度的窗口!!!
			}
			chooseGroupDialog.setVisible(true); //显示!!
			if (chooseGroupDialog.getCloseType() == 1) { //如果是确定关闭
				String[] chooseGroupRowFields = chooseGroupDialog.getRowGroupnames(); //行头.
				String[] chooseGroupColFields = chooseGroupDialog.getColGroupnames(); //列头.
				String[][] chooseComputeFunAndField = chooseGroupDialog.getComputeFunAndFields(); //返回的计算列!
				String str_reportType = chooseGroupDialog.getReportType(); //报表类型,有"GRID"与"CHART"两种!

				StringBuffer sb_text = new StringBuffer(); //
				if (str_reportType.equalsIgnoreCase("GRID")) {
					if (getTBUtil().getSysOptionBooleanValue("报表种类是否去星标记", false)) {
						sb_text.append("表格-"); //邮储样式 【杨科/2012-09-07】
					} else {
						sb_text.append("☆表格--");
					}
				} else if (str_reportType.equalsIgnoreCase("CHART")) {
					if (getTBUtil().getSysOptionBooleanValue("报表种类是否去星标记", false)) {
						sb_text.append("图表-"); //邮储样式 【杨科/2012-09-07】
					} else {
						sb_text.append("★图表--");
					}
				}

				for (int i = 0; i < chooseGroupRowFields.length; i++) {
					sb_text.append(chooseGroupRowFields[i] + "-"); //
				}
				for (int i = 0; i < chooseGroupColFields.length; i++) {
					sb_text.append(chooseGroupColFields[i] + "-"); //
				}
				String str_text = sb_text.toString(); //
				if (str_text.endsWith("-")) {
					str_text = str_text.substring(0, str_text.length() - 1); ////???
				}
				comboBox_beforeHand.setSelectedItem(null); //先清空选择!
				JTextField cmb_textField = ((JTextField) ((JComponent) comboBox_beforeHand.getEditor().getEditorComponent())); //
				cmb_textField.setText(str_text); //
				groupLabel.setIcon(icon); //
				groupLabel.setForeground(Color.BLUE); //

				//设置当前选中的维度的值！！！
				custGroupVO = new BeforeHandGroupTypeVO("自定选择的维度"); ////
				custGroupVO.setType(str_reportType); //表格型!
				custGroupVO.setRowHeaderGroupFields(chooseGroupRowFields); //行头
				custGroupVO.setColHeaderGroupFields(chooseGroupColFields); //列头
				custGroupVO.setRowHeaderFormulaGroupMap(chooseGroupDialog.getRowHeaderFormulaMap()); //行头上自定义的维度定义!
				custGroupVO.setComputeGroupFields(chooseComputeFunAndField); //计算列!
				custGroupVO.setRowGroupTiled(chooseGroupDialog.isRowGroupTiled()); //行头是否是平铺
				custGroupVO.setRowGroupSubTotal(chooseGroupDialog.isRowGroupSubTotal()); //行头是否有小计
				custGroupVO.setColGroupTiled(chooseGroupDialog.isColGroupTiled()); //列头是否是平铺
				custGroupVO.setColGroupSubTotal(chooseGroupDialog.isColGroupSubTotal()); //列头是否有小计?
				custGroupVO.setSortByCpValue(chooseGroupDialog.isSortByCpValue()); //是否按计算值排序? 【杨科/2012-08-21】
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	//袁江晓添20120913加权限策略，获取权限机制
	public String getDataPolicyCondition() {
		if (this.pub_Templet_vo.getDatapolicy() != null && !this.pub_Templet_vo.getDatapolicy().trim().equals("")) { //如果定义数据权限定义!!则加上数据权限过滤!!
			try {
				String[] str_datapolicyCondition = getMetaDataService().getDataPolicyCondition(ClientEnvironment.getCurrLoginUserVO().getId(), this.pub_Templet_vo.getDatapolicy(), this.pub_Templet_vo.getDatapolicymap()); //
				str_dataPolicyInfo = str_datapolicyCondition[0]; //
				str_dataPolicySQL = str_datapolicyCondition[1]; //				
				return str_dataPolicySQL;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private FrameWorkMetaDataServiceIfc getMetaDataService() {
		if (metaDataService == null) {
			try {
				metaDataService = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); ////
			} catch (Exception e) {
				e.printStackTrace();
				return null; //
			}
		}
		return metaDataService; //
	}

	public JComboBox getComboBox_beforeHand() {
		return comboBox_beforeHand;
	}

	public void setComboBox_beforeHand(JComboBox comboBox_beforeHand) {
		this.comboBox_beforeHand = comboBox_beforeHand;
	}

	public JPanel getWLPP() {
		return WLPP;
	}

	public void setWLPP(JPanel wlpp) {
		WLPP = wlpp;
	}

	public ReportCellPanel getCurrReportCellPanel() {
		return currReportCellPanel;
	}

	public BillQueryPanel getBillQueryPanel() {
		return billQueryPanel;
	}

	private TBUtil getTBUtil() {
		if (this.tbUtil != null) {
			return tbUtil;
		}
		tbUtil = new TBUtil();
		return tbUtil;
	}

	/**
	 * 设置报表导出的默认文件名
	 * 默认是模板名称
	 * 如果模板名称为空则默认为"报表数据导出"
	 * @param reportExpName
	 */
	public void setReportExpName(String reportExpName) {
		this.reportExpName = reportExpName;
	}

	public BillChartVO convertHashVOToChartVO(HashVO[] _hvs) {
		String[] str_XSerial = null; //
		HashMap map_x = new HashMap();
		for (int i = 0; i < _hvs.length; i++) {
			map_x.put(_hvs[i].getStringValue(0), null); //
		}
		str_XSerial = (String[]) map_x.keySet().toArray(new String[0]);
		new TBUtil().sortStrs(str_XSerial); // 默认按字母顺序..
		BillChartItemVO[][] ld_data = new BillChartItemVO[str_XSerial.length][1]; //
		for (int i = 0; i < str_XSerial.length; i++) {
			for (int k = 0; k < _hvs.length; k++) { // 去数据中找位置
				if (str_XSerial[i].equals(_hvs[k].getStringValue(0))) { //
					Object cellValue = _hvs[k].getObjectValue(1); //
					if (cellValue instanceof Double) { // 如果是Double类型
						ld_data[i][0] = new BillChartItemVO((Double) cellValue); //
					} else if (cellValue instanceof String) {
						ld_data[i][0] = new BillChartItemVO(Double.parseDouble((String) cellValue)); //
					} else if (cellValue instanceof Integer) {
						ld_data[i][0] = new BillChartItemVO(Double.valueOf("" + cellValue)); //
					} else if (cellValue instanceof BillChartItemVO) {
						ld_data[i][0] = (BillChartItemVO) cellValue; //
					} else if (cellValue instanceof BigDecimal) {
						ld_data[i][0] = new BillChartItemVO(((BigDecimal) cellValue).doubleValue());
					} else {
						ld_data[i][0] = new BillChartItemVO(-77777);
					}
					ld_data[i][0].setCustProperty("#value", _hvs[k].getStringValue("#value")); //
					break;
				}
			}
		}

		BillChartVO chartVO = new BillChartVO(); //
		chartVO.setXSerial(str_XSerial); //
		chartVO.setYSerial(new String[] { "数量" }); //
		chartVO.setDataVO(ld_data);
		return chartVO.convertXY(); //
	}

}
