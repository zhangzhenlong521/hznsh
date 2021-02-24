package cn.com.infostrategy.ui.report;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.HashVOStruct;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.report.BillChartItemVO;
import cn.com.infostrategy.to.report.BillChartVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.JepFormulaParseAtUI;
import cn.com.infostrategy.ui.mdata.NumberFormatdocument;
import cn.com.infostrategy.ui.report.chart.BillChartPanel;

/**
 * 万维报表的父亲容器,也就是将原来的大量逻辑都搬到这儿来了 因为需要钻取,所以实现上在Grid面板上存储的是一层层的该面板
 * 我在Grid面板中存储了一个堆栈,用该堆栈来存储钻取后的一层层面板,当后退时,从堆栈顶部移去旧的,显示新的堆栈顶部
 * 当钻取时,生成一个新的,并塞入堆栈顶部,然后显示之!!
 * @author xch
 * 
 */
public class ReportCellPanel extends JPanel implements BillCellHtmlHrefListener, ActionListener {

	private String str_builderClassName = null; //

	private WLTButton btn_xy, btn_exportHtml, btn_exportExcel; //上面的一排按钮!
	private BillCellPanel cellPanel = null; // 网络控件,最主要唯一的控件!
	private boolean isConvertXY = false; //

	private String[] allCanGroupFields = null; // 所有可参与分组的列
	private String[] allCanComputeFields = null; // 所有可参与计算的列

	private String[] chooseGroupRowFields = null; // 选中的行头分组,比如:new String[] {
	// "区域", "产品" }; //
	private String[] chooseGroupColFields = null; // 选中的列头分组,比如:new String[] {
	// "年度", "品种", "型号" }; //

	private String[][] chooseComputeFunAndField = null; // 参与计算的函数名与参数!!
	private boolean isRowGroupTiled = false; //行头是否平铺?

	private boolean isTotal = true; //是否有总计
	private boolean isRowGroupSubTotal = true; //行头是否有小计?
	private boolean isColGroupTiled = false; //列头是否平铺?
	private boolean isColGroupSubTotal = true; //列头是否有小计?
	private boolean isSortByCpValue = false; //是否按金额排序?

	private HashMap queryConditionMap = null; // 查询条件
	private HashMap[] drillConditionMap = null; // 过滤条件
	private HashVO[] hvs_data = null; // 钻取后的数据
	private HashVO[] hvs_data_beforefilter = null; // 过滤前的数据,第一次做时会将它构建.
	private HashMap filterConditionMap = null; // 过滤条件!!!
	private HashMap filterGroupValueMap = null; // 过滤条件!!!

	private HashMap groupfieldorder = null; // 分组头排序 key=字段名称
	// value=排序的顺序数组，例如key=文件状态
	// value=[编写中][申请发布][有效]..
	private DefaultMutableTreeNode rowHeaderRootNode = null;
	private DefaultMutableTreeNode colHeaderRootNode = null;
	private HashMap beforeBuilderDataMap = null; //

	private String[] headBgColors_row = new String[] { "255,215,255", "200,255,255", "222,222,188", "235,235,235" }; //
	private String[] headBgColors_col = new String[] { "200,255,230", "230,225,255", "255,200,200", "237,220,220" }; //
	private String[] totalBgColors = new String[] { "221,221,50", "255,255,100", "255,255,180", "255,255,220" }; //
	private String[] computeBgColors = new String[] { "255,128,64", "0,128,128", "0,0,255", "0,128,0" }; //计算列中的第几个位置
	private String[] warnBgColors = new String[] { "237,23,0", "255,128,0", "255,255,0", "128,255,128" }; //警界值的背景颜色,从【红色->橙色->黄色->绿色】
	private JepFormulaParseAtUI jepParse = null; //公式解析器!

	private JPopupMenu rightPopMenu = null;
	private JMenuItem menuItem_drillNextGroup, menuItem_drillDetail, menuItem_filter, menuItem_barChart, menuItem_lineChart, menuItem_pieChart, menuItem_warnValue, menuItem_viewInfo, menuItem_lock, menuItem_unlock; //

	private String str_drillActionClass = null; //客户端实现钻去真实数据的类路径。[郝明2012-06-08]
	private String str_drillTempletCode = null; //客户端钻取明细时打开哪一个单据模板??
	private HashMap drillgroupbindMap = null; //钻取的预置绑定的定义!
	private HashMap zeroReportConfMap = null; //零汇报机制的Map定义!
	private HashMap dateGroupDefineMap = null; //时间字段维度!
	private HashMap secondHashVOComputeMap = null; //对HashVO[]进行二次计算的Map
	private HashMap rowHeaderFormulaGroupMap = null; //行头上可以定义增加新的公式组!
	private boolean isCtrlDown = false; //

	private TBUtil tbutil = new TBUtil();
	private String[] str_notCanTotalTypes = new String[] { "max", "min" }; //

	private MultiLevelChooseDrillDialog drillNextGroupDialog = null;

	/**
	 * 默认构造方法不让用!
	 */
	private ReportCellPanel() {
	}

	/**
	 * 构造方法..
	 * 
	 * @param _builderClassName
	 */
	public ReportCellPanel(String _builderClassName, String[] _allCanGroupFields, String[] _allCanComputeFields, String[] _chooseGroupRowFields, String[] _chooseGroupColFields, String[][] _chooseComputeFunAndField, HashMap _queryConditionMap, HashMap[] _drillConditionMap) {
		this(_builderClassName, _allCanGroupFields, _allCanComputeFields, _chooseGroupRowFields, _chooseGroupColFields, _chooseComputeFunAndField, null, null, null, true, false, true, false, true, false, _queryConditionMap, _drillConditionMap, null, null, null, false);
	}

	/**
	 * 加入参数groupfieldorder 设置方式 key=字段名称 value=排序的顺序数组，例如key=文件状态
	 * value=[编写中][申请发布][有效]..
	 */
	public ReportCellPanel(String _builderClassName, String[] _allCanGroupFields, String[] _allCanComputeFields, String[] _chooseGroupRowFields, String[] _chooseGroupColFields, String[][] _chooseComputeFunAndField, HashMap _queryConditionMap, HashMap[] _drillConditionMap, HashMap _groupfieldorder) {
		this(_builderClassName, _allCanGroupFields, _allCanComputeFields, _chooseGroupRowFields, _chooseGroupColFields, _chooseComputeFunAndField, null, null, null, true, false, true, false, true, false, _queryConditionMap, _drillConditionMap, _groupfieldorder, null, null, false);
	}

	/**
	 * 加入参数groupfieldorder 设置方式 key=字段名称 value=排序的顺序数组，例如key=文件状态
	 * value=[编写中][申请发布][有效]..
	 */
	public ReportCellPanel(String _builderClassName, String[] _allCanGroupFields, String[] _allCanComputeFields, String[] _chooseGroupRowFields, String[] _chooseGroupColFields, String[][] _chooseComputeFunAndField, HashMap _filterGroupValueMap, HashMap _secondHashVOComputeMap, HashMap _rowHeaderFormulaGroupMap, boolean _isTotal, boolean _isRowGroupTiled, boolean _isRowGroupSubTotal,
			boolean _isColGroupTiled, boolean _isColGroupSubTotal, boolean _isSortByCpValue, HashMap _queryConditionMap, HashMap[] _drillConditionMap, HashMap _groupfieldorder, HashMap _zeroReportConfMap, HashMap _dateGroupDefineMap, boolean _isCtrlDown) {
		super(); //
		str_builderClassName = _builderClassName; //
		this.allCanGroupFields = _allCanGroupFields; //
		this.allCanComputeFields = _allCanComputeFields; //
		this.chooseGroupRowFields = _chooseGroupRowFields; //
		this.chooseGroupColFields = _chooseGroupColFields; //
		this.chooseComputeFunAndField = _chooseComputeFunAndField; //
		this.filterGroupValueMap = _filterGroupValueMap; //
		this.rowHeaderFormulaGroupMap = _rowHeaderFormulaGroupMap; //公式组!

		this.isTotal = _isTotal; //
		this.isRowGroupTiled = _isRowGroupTiled; //
		this.isRowGroupSubTotal = _isRowGroupSubTotal; //  
		this.isColGroupTiled = _isColGroupTiled; //
		this.isColGroupSubTotal = _isColGroupSubTotal; //
		this.isSortByCpValue = _isSortByCpValue; //是否按金额排序?

		this.queryConditionMap = _queryConditionMap; //
		this.drillConditionMap = _drillConditionMap; //
		this.groupfieldorder = _groupfieldorder;
		this.zeroReportConfMap = _zeroReportConfMap; //
		this.dateGroupDefineMap = _dateGroupDefineMap; //
		this.secondHashVOComputeMap = _secondHashVOComputeMap; //

		this.isCtrlDown = _isCtrlDown; //是否按了Ctrl键??

		initialize(); //
	}

	/**
	 * 初始化页面
	 */
	private void initialize() {
		try {
			ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class); //
			HashVO[] hvs_initdata = service.queryMultiLevelReportData(queryConditionMap, this.str_builderClassName, getAllChooseGroupFields(), chooseComputeFunAndField, isCtrlDown); //真正调用远程服务,进行统计!这里返回的已经是汇总的数据,即数据量是少的!!
			if (hvs_initdata.length == 0) {
				this.add(new JLabel("没有查询到一条数据!!!")); //
				return; //
			}

			//先处理所有为空的数据为空字符串!!这一步看来还是很必要的,因为有许多数据为空!
			String[] itemKey = hvs_initdata[0].getKeys(); //
			for (int i = 0; i < hvs_initdata.length; i++) {
				for (int j = 0; j < itemKey.length; j++) {
					if (hvs_initdata[i].getStringValue(itemKey[j]) == null) { //
						hvs_initdata[i].setAttributeValue(itemKey[j], ""); //
					}
				}
			}

			if (drillConditionMap == null || drillConditionMap.length == 0) { //如果直接查询的,不是钻取的,则直接等于!
				this.hvs_data = hvs_initdata; //如果没有进行数据钻取
			} else { //如果进行数据钻取,弹出来的窗口,则要根据钻取的维度进行再次过滤!!!即钻取的逻辑是在前台进行的! 为什么不在后台进行呢？因为钻取过滤,反正还是要遍历所有明细HashVO[],这样一来,在前台还是后台过滤性能都差不多!!
				ArrayList al_filteredhvs = new ArrayList();
				String str_a1, str_a2;//
				for (int i = 0; i < hvs_initdata.length; i++) { // 遍历数据
					boolean bo_isonematch = false; //
					for (int j = 0; j < drillConditionMap.length; j++) {
						boolean bo_ismatch = true;
						String[] str_secondFilterGroupFields = (String[]) drillConditionMap[j].keySet().toArray(new String[0]); //
						for (int k = 0; k < str_secondFilterGroupFields.length; k++) { // 每一行条件,如果品配不上就立即退出!
							str_a1 = hvs_initdata[i].getStringValue(str_secondFilterGroupFields[k]); //
							str_a2 = (String) drillConditionMap[j].get(str_secondFilterGroupFields[k]); //
							if (str_a1 == null || str_a1.trim().equals("")) { //必须考虑空值的匹配!
								str_a1 = "【空值】";
							}
							if (!str_a1.equals(str_a2)) { //只要有一个不等,就立即退出!
								bo_ismatch = false;
								break;
							}
						}
						if (bo_ismatch) {
							bo_isonematch = true; // 如果幸运的遇到,一组条件就满足了,则其他条件就不要试了!!!立即跳出!
							break;
						}
					}
					if (bo_isonematch) {
						al_filteredhvs.add(hvs_initdata[i]); //
					}
				}
				this.hvs_data = (HashVO[]) al_filteredhvs.toArray(new HashVO[0]); // 设置过滤后的值
			}
			hvs_data_beforefilter = null; // 每次钻取或者重新查询时,总是将其置空!
			filterConditionMap = null; //

			buildUI2(); // 画页面了!
		} catch (Exception ex) {
			ex.printStackTrace(); //
			JOptionPane.showMessageDialog(this, ex.getMessage()); //
		}
	}

	/**
	 * 构建树！！用于测试用！！！关键就是靠这两颗树来搞定一切的！！！
	 */
	private void buildTree() {
		boolean isOnlyRowGroup = (chooseGroupColFields.length == 0 ? true : false); //是否只有行维度?即如果列维度数量为0,则表示只有行维度！！！
		boolean isOnlyColGroup = (chooseGroupRowFields.length == 0 ? true : false); //是否只有列维度?即如果行维度数量为0,则表示只有列维度！！！
		boolean isCanTotal = isCaneTotlal(chooseComputeFunAndField); //是否可以合计?即只要的一个字段是可以合计的,则就认为是可以合计的!
		rowHeaderRootNode = getGroupHeaderNode(this.chooseGroupRowFields, isRowGroupTiled, isRowGroupSubTotal, isCanTotal, true, isOnlyRowGroup, chooseComputeFunAndField, groupfieldorder); // 生成行头
		colHeaderRootNode = getGroupHeaderNode(this.chooseGroupColFields, isColGroupTiled, isColGroupSubTotal, isCanTotal, false, isOnlyColGroup, chooseComputeFunAndField, groupfieldorder); // 生成列头

		JTree tree1 = new JTree(rowHeaderRootNode); //
		JTree tree2 = new JTree(colHeaderRootNode); //

		JTabbedPane tabbPane = new JTabbedPane(); //
		tabbPane.addTab("行头树", new JScrollPane(tree1)); //
		tabbPane.addTab("列头树", new JScrollPane(tree2)); //

		this.removeAll();
		this.setLayout(new BorderLayout(0, 0)); //
		this.add(tabbPane); //

		this.updateUI(); //
	}

	/**
	 * 真正的构造数据！！新的逻辑！！
	 */
	private void buildUI2() {
		boolean isOnlyRowGroup = (chooseGroupColFields.length == 0 ? true : false); //是否只有行维度?即如果列维度数量为0,则表示只有行维度！！！
		boolean isOnlyColGroup = (chooseGroupRowFields.length == 0 ? true : false); //是否只有列维度?即如果行维度数量为0,则表示只有列维度！！！
		boolean isCanTotal = isCaneTotlal(chooseComputeFunAndField); //是否可以合计?即只要的一个字段是可以合计的,则就认为是可以合计的!
		boolean isINIT = false;// 加以判断是否为init模式，如果是的话则需要根据这个来设置格子的宽度，因为在Init模式下，格子需要再宽一些    20130308   袁江晓

		//处理所有格子之前,先要构造哈希表数据!这是为了提高匹配维度值时的性能!
		//以前是放在后面计算的,后来遇到排行榜需求!!即根据统计值进行排序!所以需要先计算出值,所以放到前面来了!
		beforeBuilderDataMap = new LinkedHashMap(); //哈希表！！！
		HashSet hst_cpfield = new HashSet(); //存储唯一性的计算列的名称,因为一个列完全有可能同时有sum,avg,count,所以必须做唯一性过滤,否则会重复计算,造成数据量翻倍的bug
		for (int k = 0; k < chooseComputeFunAndField.length; k++) { //遍历计算
			String str_fieldName = chooseComputeFunAndField[k][2]; //
			if (str_fieldName.indexOf("-") > 0) {
				str_fieldName = str_fieldName.substring(0, str_fieldName.indexOf("-")); //截取前面的!!!
			}
			hst_cpfield.add(str_fieldName); //
		}
		String[] str_dist_fields = (String[]) hst_cpfield.toArray(new String[0]); //转换成数组
		boolean havecount = false;
		for (int i = 0; i < chooseComputeFunAndField.length; i++) {
			if ("count".equalsIgnoreCase(chooseComputeFunAndField[i][0])) {
				havecount = true;
				break;
			}
		}
		for (int r = 0; r < hvs_data.length; r++) { //遍历所有数据!!
			//if()
			//直接单独处理行维度！！！
			for (int i = 0; i < chooseGroupRowFields.length; i++) { //遍历所有行维度，如果没有行维度,为了保证列数据能出来，也是有一次循环的!
				//先从0到当前位计算出全称,分别是A,AB,ABC,ABCD
				StringBuilder sb_key_1 = new StringBuilder(); //
				if (isRowGroupTiled) { //行头平铺
					String str_levelValue = hvs_data[r].getStringValue(chooseGroupRowFields[i], "【空值】", true); //维度值！
					sb_key_1.append(chooseGroupRowFields[i] + "=" + str_levelValue + "◆"); //实际值！
				} else {
					for (int i2 = 0; i2 <= i; i2++) {
						String str_levelValue = hvs_data[r].getStringValue(chooseGroupRowFields[i2], "【空值】", true); //维度值！
						sb_key_1.append(chooseGroupRowFields[i2] + "=" + str_levelValue + "◆"); //实际值！
					}
				}

				for (int k = 0; k < str_dist_fields.length; k++) { //遍历所有计算列!
					String str_cpField = str_dist_fields[k]; //字段名!!
					if (str_cpField.endsWith("数量") || havecount) { //袁江晓添加 统计显示初始值  20130312   如果为数量统计
						BigDecimal ld_value_count = hvs_data[r].getBigDecimalValue(str_cpField + "◆count"); //以前为double类型，超过千万级数据会报错，故修改之【李春娟/2019-03-21】
						BigDecimal ld_value_sum = hvs_data[r].getBigDecimalValue(str_cpField + "◆sum"); //
						appendMapData(beforeBuilderDataMap, sb_key_1.toString() + "@" + str_cpField + "(count)", ld_value_count); //判断如果有就累加,如果没有就加入!!
						appendMapData(beforeBuilderDataMap, sb_key_1.toString() + "@" + str_cpField + "(sum)", ld_value_sum); //

					} else {//如果为init   袁江晓添加 统计显示初始值  20130312
						isINIT = true;// 如果有一个为init则此次都为init，后面不用做判断
						BigDecimal ld_value_sum = hvs_data[r].getBigDecimalValue(str_cpField + "◆sum"); //以前为double类型，超过千万级数据会报错，故修改之【李春娟/2019-03-21】
						appendMapData(beforeBuilderDataMap, sb_key_1.toString() + "@" + str_cpField + "(sum)", ld_value_sum); //
						/*String ld_value_init= hvs_data[r].getStringValue(str_cpField+"◆init", ";");   //袁江晓添加init
						appendMapData(beforeBuilderDataMap, sb_key_1.toString() + "@" + str_cpField + "(init)", ld_value_init);  //袁江晓添加init
						*/}
				}

				String str_value = hvs_data[r].getStringValue("#value"); //看有没有?
				String str_realKey_1_values = sb_key_1.toString() + "#value"; //
				appendMapData2(beforeBuilderDataMap, str_realKey_1_values, str_value); //
			}

			//单独处理列维度！！
			for (int j = 0; j < chooseGroupColFields.length; j++) { //遍历所有列维度
				StringBuilder sb_key_2 = new StringBuilder(); //
				if (isColGroupTiled) { //列头平铺
					String str_levelValue = hvs_data[r].getStringValue(chooseGroupColFields[j], "【空值】", true); //维度值！
					sb_key_2.append(chooseGroupColFields[j] + "=" + str_levelValue + "▲"); //实际值！
				} else {
					for (int j2 = 0; j2 <= j; j2++) { //
						String str_levelValue = hvs_data[r].getStringValue(chooseGroupColFields[j2], "【空值】", true); //维度值！
						sb_key_2.append(chooseGroupColFields[j2] + "=" + str_levelValue + "▲"); //实际值！
					}
				}

				for (int k = 0; k < str_dist_fields.length; k++) { //遍历所有计算列!
					String str_cpField = str_dist_fields[k]; //字段名!!
					if (str_cpField.endsWith("数量") || havecount) { //如果为数量统计
						BigDecimal ld_value_count = hvs_data[r].getBigDecimalValue(str_cpField + "◆count"); //以前为double类型，超过千万级数据会报错，故修改之【李春娟/2019-03-21】
						BigDecimal ld_value_sum = hvs_data[r].getBigDecimalValue(str_cpField + "◆sum"); //
						appendMapData(beforeBuilderDataMap, sb_key_2.toString() + "@" + str_cpField + "(count)", ld_value_count); //判断如果有就累加,如果没有就加入!!
						appendMapData(beforeBuilderDataMap, sb_key_2.toString() + "@" + str_cpField + "(sum)", ld_value_sum); //
					} else {//不为数量统计则为init
						//后来在做日志项目工时统计时需要添加合计功能，于是初始化也加上合计功能  20130319   袁江晓
						BigDecimal ld_value_sum = hvs_data[r].getBigDecimalValue(str_cpField + "◆sum"); //以前为double类型，超过千万级数据会报错，故修改之【李春娟/2019-03-21】
						appendMapData(beforeBuilderDataMap, sb_key_2.toString() + "@" + str_cpField + "(sum)", ld_value_sum); //
						/*String ld_value_init= hvs_data[r].getStringValue(str_cpField+"◆init", ";");   //袁江晓添加init
						appendMapData(beforeBuilderDataMap, sb_key_2.toString() + "@" + str_cpField + "(init)", ld_value_init);  //袁江晓添加init
						*/}
				}

				String str_value = hvs_data[r].getStringValue("#value"); //看有没有?
				String str_realKey_2_values = sb_key_2.toString() + "#value"; //
				appendMapData2(beforeBuilderDataMap, str_realKey_2_values, str_value); //
			}

			//行与列交叉！！！如果只有行或列，则都不会运行，但正好上面两步已处理了！！！
			for (int i = 0; i < chooseGroupRowFields.length; i++) { //遍历所有行维度，如果没有行维度,为了保证列数据能出来，也是有一次循环的!
				//先从0到当前位计算出全称,分别是A,AB,ABC,ABCD
				StringBuilder sb_key_1 = new StringBuilder(); //
				if (isRowGroupTiled) { //行头平铺
					String str_levelValue = hvs_data[r].getStringValue(chooseGroupRowFields[i], "【空值】", true); //维度值！
					sb_key_1.append(chooseGroupRowFields[i] + "=" + str_levelValue + "◆"); //实际值！
				} else {
					for (int i2 = 0; i2 <= i; i2++) {
						String str_levelValue = hvs_data[r].getStringValue(chooseGroupRowFields[i2], "【空值】", true); //维度值！
						sb_key_1.append(chooseGroupRowFields[i2] + "=" + str_levelValue + "◆"); //实际值！
					}
				}

				//遍历所有列维度!!
				for (int j = 0; j < chooseGroupColFields.length; j++) { //遍历所有列维度
					StringBuilder sb_key_2 = new StringBuilder(); //
					if (isColGroupTiled) { //列头平铺
						String str_levelValue = hvs_data[r].getStringValue(chooseGroupColFields[j], "【空值】", true); //维度值！
						sb_key_2.append(chooseGroupColFields[j] + "=" + str_levelValue + "▲"); //实际值！
					} else {
						for (int j2 = 0; j2 <= j; j2++) { //
							String str_levelValue = hvs_data[r].getStringValue(chooseGroupColFields[j2], "【空值】", true); //维度值！
							sb_key_2.append(chooseGroupColFields[j2] + "=" + str_levelValue + "▲"); //实际值！
						}
					}

					for (int k = 0; k < str_dist_fields.length; k++) { //遍历所有计算列!
						String str_cpField = str_dist_fields[k]; //字段名!!
						if (str_cpField.endsWith("数量") || havecount) { //如果为数量统计
							BigDecimal ld_value_count = hvs_data[r].getBigDecimalValue(str_cpField + "◆count"); //以前为double类型，超过千万级数据会报错，故修改之【李春娟/2019-03-21】
							BigDecimal ld_value_sum = hvs_data[r].getBigDecimalValue(str_cpField + "◆sum"); //
							appendMapData(beforeBuilderDataMap, sb_key_1.toString() + sb_key_2.toString() + "@" + str_cpField + "(count)", ld_value_count); //判断如果有就累加,如果没有就加入!!
							appendMapData(beforeBuilderDataMap, sb_key_1.toString() + sb_key_2.toString() + "@" + str_cpField + "(sum)", ld_value_sum); //
						} else {//不为数量统计则为init
							String ld_value_init = hvs_data[r].getStringValue(str_cpField + "◆init", ";"); //袁江晓添加init
							appendMapData(beforeBuilderDataMap, sb_key_1.toString() + sb_key_2.toString() + "@" + str_cpField + "(init)", ld_value_init); //袁江晓添加init
							//后来在做日志项目工时统计时需要添加合计功能，于是初始化也加上合计功能  20130319   袁江晓
							BigDecimal ld_value_sum = hvs_data[r].getBigDecimalValue(str_cpField + "◆sum"); //以前为double类型，超过千万级数据会报错，故修改之【李春娟/2019-03-21】
							appendMapData(beforeBuilderDataMap, sb_key_1.toString() + sb_key_2.toString() + "@" + str_cpField + "(sum)", ld_value_sum); //
						}
					}

					String str_value = hvs_data[r].getStringValue("#value"); //看有没有?
					String str_realKey_3_values = sb_key_1.toString() + sb_key_2.toString() + "#value"; //
					//System.out.println("【" + str_realKey_3_values + "】=[" + str_value + "]"); //
					appendMapData2(beforeBuilderDataMap, str_realKey_3_values, str_value); //
				}
			} //

			//最右下角有个总计
			for (int k = 0; k < str_dist_fields.length; k++) { //遍历所有计算列!
				String str_cpField = str_dist_fields[k]; //字段名!!
				if (str_cpField.endsWith("数量") || havecount) { //如果为数量统计
					BigDecimal ld_value_count = hvs_data[r].getBigDecimalValue(str_cpField + "◆count"); //以前为double类型，超过千万级数据会报错，故修改之【李春娟/2019-03-21】
					BigDecimal ld_value_sum = hvs_data[r].getBigDecimalValue(str_cpField + "◆sum");
					appendMapData(beforeBuilderDataMap, "@" + str_cpField + "(count)", ld_value_count); //判断如果有就累加,如果没有就加入!!
					appendMapData(beforeBuilderDataMap, "@" + str_cpField + "(sum)", ld_value_sum); //
				} else {//不为数量统计则为init   不进行总计
					//后来在做日志项目工时统计时需要添加合计功能，于是初始化也加上合计功能  20130319   袁江晓
					BigDecimal ld_value_sum = hvs_data[r].getBigDecimalValue(str_cpField + "◆sum"); //以前为double类型，超过千万级数据会报错，故修改之【李春娟/2019-03-21】
					appendMapData(beforeBuilderDataMap, "@" + str_cpField + "(sum)", ld_value_sum); //
					/*	String ld_value_init= hvs_data[r].getStringValue(str_cpField+"◆init", ";");   //袁江晓添加init
						appendMapData(beforeBuilderDataMap, str_cpField.toString() + "@" + str_cpField + "(init)", ld_value_init);  //袁江晓添加init
					*/}
			}

		}
		/*		for(Iterator iterator =beforeBuilderDataMap.keySet().iterator();iterator.hasNext();){
					Object name= iterator.next();
					System.out.println("遍历beforeBuilderDataMap===="+name+"=====================>"+beforeBuilderDataMap.get(name));
				}*/

		//处理平均数,占比,同比,环比,连续增长等...
		//构造数据Map结束！！！
		rowHeaderRootNode = getGroupHeaderNode(this.chooseGroupRowFields, isRowGroupTiled, isRowGroupSubTotal, isCanTotal, true, isOnlyRowGroup, chooseComputeFunAndField, groupfieldorder); // 生成行头
		colHeaderRootNode = getGroupHeaderNode(this.chooseGroupColFields, isColGroupTiled, isColGroupSubTotal, isCanTotal, false, isOnlyColGroup, chooseComputeFunAndField, groupfieldorder); // 生成列头

		int[] li_rowLeafCountAndLevel = getLeafNodeCountAndLevel(rowHeaderRootNode); //行头上叶子结点的数量！！！
		int[] li_colLeafCountAndLevel = getLeafNodeCountAndLevel(colHeaderRootNode); //列头上叶子结点的数量！！！

		int li_row_leafCount = li_rowLeafCountAndLevel[0]; //行上面叶子结点的数量！！！
		int li_row_level = li_rowLeafCountAndLevel[1]; //层次

		int li_col_leafCount = li_colLeafCountAndLevel[0]; //列上面叶子结点的数量！！！
		int li_col_level = li_colLeafCountAndLevel[1]; //层次

		int li_all_rows = li_row_level + li_col_leafCount; //总行数！！
		int li_all_cols = li_col_level + li_row_leafCount; //总列数！！

		//System.out.println("行维度的层次[" + li_row_prefix + "],列维度的层次[" + li_col_prefix + "]"); //

		//创建数据对象!!
		BillCellVO billCellVO = new BillCellVO(); //
		billCellVO.setTempletcode("");
		billCellVO.setTempletname("");
		billCellVO.setBillNo(""); //
		billCellVO.setRowlength(li_all_rows); //几行
		billCellVO.setCollength(li_all_cols); //几列

		BillCellItemVO[][] cellItemVOs = new BillCellItemVO[li_all_rows][li_all_cols]; //
		for (int i = 0; i < cellItemVOs.length; i++) {
			for (int j = 0; j < cellItemVOs[i].length; j++) {
				cellItemVOs[i][j] = new BillCellItemVO(); //
				cellItemVOs[i][j].setCelltype(BillCellPanel.ITEMTYPE_TEXTAREA); //
				cellItemVOs[i][j].setCellrow(i);
				cellItemVOs[i][j].setCellcol(j);
				if (i == 0 && j == 0 && li_row_level <= 1) {
					cellItemVOs[i][j].setRowheight("40");//行高!!
				} else {
					cellItemVOs[i][j].setRowheight("25");//行高!!
				}
				if (j == 0 && li_col_level == 1) {
					cellItemVOs[i][j].setColwidth("125"); //列宽!!
				} else {
					if (!isINIT) {
						if (j < li_col_level) {
							cellItemVOs[i][j].setColwidth("80"); //列宽!!
						} else {
							cellItemVOs[i][j].setColwidth("65"); //列宽!!
						}
					} else { //init模式  格子需要宽一些  好看   袁江晓  20130308
						if (j < li_col_level) {
							cellItemVOs[i][j].setColwidth("100"); //列宽!!
						} else {
							cellItemVOs[i][j].setColwidth("140"); //列宽!!
						}
					}
				}
				cellItemVOs[i][j].setSpan(null); //合并!!
				cellItemVOs[i][j].setCellvalue(""); //
			}
		}

		//处理左上角的标题!!!
		cellItemVOs[0][0].setSpan(li_row_level + "," + li_col_level); //向下跨的行数肯定是行头树的层次!向右正好是列头树的层次！
		cellItemVOs[0][0].setCustProperty("itemtype", "TopLeftTitle"); //类型!!
		cellItemVOs[0][0].setCellvalue(getTopLeftTitle()); //左上角标题内容
		cellItemVOs[0][0].setBackground("255,255,160"); //
		cellItemVOs[0][0].setHalign(3); //
		cellItemVOs[0][0].setValign(3); //

		//处理列头,或者叫左表头!!!
		DefaultMutableTreeNode[] allLeafNodes_col = getAllLeafNodes(colHeaderRootNode); //所有结点!
		HashSet hstParent_col = new HashSet(); //
		for (int i = 0; i < allLeafNodes_col.length; i++) { //遍历所有叶子结点!
			int li_itemLevel = allLeafNodes_col[i].getLevel() - 1; //层级
			//System.out.println("第[" + i + "]行[" + li_itemLevel + "]列结点的内容[" + allNodes[i].getUserObject() + "]"); //
			HashVO hvo_node = (HashVO) allLeafNodes_col[i].getUserObject(); //
			String str_itemType = hvo_node.getStringValue("ItemType"); //
			cellItemVOs[li_row_level + i][li_itemLevel].setCellvalue(hvo_node.toString()); //
			cellItemVOs[li_row_level + i][li_itemLevel].setCustProperty(hvo_node.getM_hData()); //将结点
			if (str_itemType.equals("SubTotal") || str_itemType.equals("Total")) { //如果是小计/合计,则背景颜色是黄色！
				cellItemVOs[li_row_level + i][li_itemLevel].setBackground(getTotalColor(allLeafNodes_col[i].getLevel() - 1)); //
				cellItemVOs[li_row_level + i][li_itemLevel].setSpan("1," + (li_col_level - li_itemLevel)); //可能要合并
				cellItemVOs[li_row_level + i][li_itemLevel].setHalign(3); //
				cellItemVOs[li_row_level + i][li_itemLevel].setValign(2); //
			} else {
				if (str_itemType.equals("SplitCP")) { //如果是计算列,则使用计算列的颜色!即如果行头组为空,这时计算列会自动转移到列头上！
					int li_myIndexOfParent = allLeafNodes_col[i].getParent().getIndex(allLeafNodes_col[i]); //我是父亲结点中的第几个!!
					cellItemVOs[li_row_level + i][li_itemLevel].setBackground("245,245,245"); //
					cellItemVOs[li_row_level + i][li_itemLevel].setForeground(getComputeHeaderColor(li_myIndexOfParent)); //
				} else { //如果是Level
					if (isColGroupTiled) { //如果列是平铺的情况,则背景颜色权是一体的!
						DefaultMutableTreeNode thisGroupParentNode = (DefaultMutableTreeNode) allLeafNodes_col[i].getParent(); // 
						int li_myIndexOfParent = thisGroupParentNode.getParent().getIndex(thisGroupParentNode); //我是父亲结点中的第几个!!
						cellItemVOs[li_row_level + i][li_itemLevel].setBackground(getColHeaderColor(li_myIndexOfParent)); //
					} else {
						cellItemVOs[li_row_level + i][li_itemLevel].setBackground(getColHeaderColor(li_itemLevel)); //
					}
				}
				cellItemVOs[li_row_level + i][li_itemLevel].setHalign(2);
				cellItemVOs[li_row_level + i][li_itemLevel].setValign(2);

				//如果是叶子结点,且又定义了预约钻取维度,则有超接连!
				cellItemVOs[li_row_level + i][li_itemLevel].setIshtmlhref("Y"); //
			}

			TreeNode[] parentNodes = allLeafNodes_col[i].getPath(); //取得他的父亲!
			for (int j = 1; j < parentNodes.length - 1; j++) { //遍历所有父亲
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentNodes[j]; //
				HashVO hvo_parent = (HashVO) parentNode.getUserObject();
				cellItemVOs[li_row_level + i][j - 1].setCellvalue(hvo_parent.toString()); //设置值！！
				cellItemVOs[li_row_level + i][j - 1].setCustProperty(hvo_parent.getM_hData()); //
				if (!hstParent_col.contains(parentNode)) { //如果还没有这个结点,即说明是第一次,则进行合并处理!!!
					int li_thisAllLeafCount = getLeafNodeCount(parentNode); //
					cellItemVOs[li_row_level + i][j - 1].setSpan(li_thisAllLeafCount + ",1"); //
					cellItemVOs[li_row_level + i][j - 1].setHalign(2); //
					cellItemVOs[li_row_level + i][j - 1].setValign(2); //
					if (isColGroupTiled) { //如果行是平铺的情况
						int li_myIndexOfParent = parentNode.getParent().getIndex(parentNode); //我是父亲结点中的第几个!!
						cellItemVOs[li_row_level + i][j - 1].setBackground(getColHeaderColor(li_myIndexOfParent)); //
					} else {
						cellItemVOs[li_row_level + i][j - 1].setBackground(getColHeaderColor(j - 1)); //
					}
					hstParent_col.add(parentNode); //
				}
			}
		}

		//处理行头，应该是与行头反过来！！！
		DefaultMutableTreeNode[] allLeafNodes_row = getAllLeafNodes(rowHeaderRootNode); //所有结点!
		HashSet hstParent_row = new HashSet(); //
		for (int i = 0; i < allLeafNodes_row.length; i++) { //遍历所有叶子结点!
			int li_itemLevel = allLeafNodes_row[i].getLevel() - 1; //层级
			HashVO hvo_node = (HashVO) allLeafNodes_row[i].getUserObject(); //
			String str_itemType = hvo_node.getStringValue("ItemType"); //
			cellItemVOs[li_itemLevel][li_col_level + i].setCellvalue(hvo_node.toString()); //
			cellItemVOs[li_itemLevel][li_col_level + i].setCustProperty(hvo_node.getM_hData()); //
			if (str_itemType.equals("SubTotal") || str_itemType.equals("Total")) { //
				cellItemVOs[li_itemLevel][li_col_level + i].setBackground(getTotalColor(allLeafNodes_row[i].getLevel() - 1)); //
				cellItemVOs[li_itemLevel][li_col_level + i].setSpan((li_row_level - li_itemLevel) + ",1"); //
				cellItemVOs[li_itemLevel][li_col_level + i].setHalign(2);
				cellItemVOs[li_itemLevel][li_col_level + i].setValign(3);
			} else if (str_itemType.equals("FormulaLevel")) { //
				cellItemVOs[li_itemLevel][li_col_level + i].setSpan((li_row_level - li_itemLevel) + ",1"); //
				cellItemVOs[li_itemLevel][li_col_level + i].setHalign(2);
				cellItemVOs[li_itemLevel][li_col_level + i].setValign(2);
				if (isRowGroupTiled) { //如果行是平铺的情况
					DefaultMutableTreeNode thisGroupParentNode = (DefaultMutableTreeNode) allLeafNodes_row[i].getParent(); // 
					int li_myIndexOfParent = thisGroupParentNode.getParent().getIndex(thisGroupParentNode); //我是父亲结点中的第几个!!
					cellItemVOs[li_itemLevel][li_col_level + i].setBackground(getRowHeaderColor(li_myIndexOfParent)); //
				} else { //如果是层级!
					cellItemVOs[li_itemLevel][li_col_level + i].setBackground(getRowHeaderColor(li_itemLevel)); //
				}
				cellItemVOs[li_itemLevel][li_col_level + i].setForeground("0,0,255"); //
			} else { //如果是Level类型!
				if (str_itemType.equals("SplitCP")) { //如果是计算列,则使用计算列的颜色!
					int li_myIndexOfParent = allLeafNodes_row[i].getParent().getIndex(allLeafNodes_row[i]); //我是父亲结点中的第几个!!
					cellItemVOs[li_itemLevel][li_col_level + i].setBackground("245,245,245"); //
					cellItemVOs[li_itemLevel][li_col_level + i].setForeground(getComputeHeaderColor(li_myIndexOfParent)); //
				} else { //如果不是计算列?即标准维度!
					if (isRowGroupTiled) { //如果行是平铺的情况
						DefaultMutableTreeNode thisGroupParentNode = (DefaultMutableTreeNode) allLeafNodes_row[i].getParent(); // 
						int li_myIndexOfParent = thisGroupParentNode.getParent().getIndex(thisGroupParentNode); //我是父亲结点中的第几个!!
						cellItemVOs[li_itemLevel][li_col_level + i].setBackground(getRowHeaderColor(li_myIndexOfParent)); //
					} else { //如果是层级!
						cellItemVOs[li_itemLevel][li_col_level + i].setBackground(getRowHeaderColor(li_itemLevel)); //
					}
				}
				cellItemVOs[li_itemLevel][li_col_level + i].setHalign(2);
				cellItemVOs[li_itemLevel][li_col_level + i].setValign(2);
				//如果是叶子结点,且又定义了预置钻取维度,则有超链接
				cellItemVOs[li_itemLevel][li_col_level + i].setIshtmlhref("Y"); //
			}

			TreeNode[] parentNodes = allLeafNodes_row[i].getPath(); //取得他的父亲
			for (int j = 1; j < parentNodes.length - 1; j++) { //遍历所有父亲
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentNodes[j]; //
				HashVO hvo_parent = (HashVO) parentNode.getUserObject();
				cellItemVOs[j - 1][li_col_level + i].setCellvalue(hvo_parent.toString()); //设置值！！
				cellItemVOs[j - 1][li_col_level + i].setCustProperty(hvo_parent.getM_hData()); //
				if (!hstParent_row.contains(parentNode)) { //如果还没有这个结点,即说明是第一次,则进行合并处理!!!
					int li_thisAllLeafCount = getLeafNodeCount(parentNode); //
					cellItemVOs[j - 1][li_col_level + i].setSpan("1," + li_thisAllLeafCount); //
					cellItemVOs[j - 1][li_col_level + i].setHalign(2); //
					cellItemVOs[j - 1][li_col_level + i].setValign(2); //

					if (isRowGroupTiled) { //如果行是平铺的情况
						int li_myIndexOfParent = 0; //
						if (parentNode.getLevel() == 2) {
							DefaultMutableTreeNode parentNode1 = (DefaultMutableTreeNode) parentNode.getParent(); //
							li_myIndexOfParent = parentNode1.getParent().getIndex(parentNode1); //我是父亲结点中的第几个!!
						} else {
							li_myIndexOfParent = parentNode.getParent().getIndex(parentNode); //我是父亲结点中的第几个!!
						}
						cellItemVOs[j - 1][li_col_level + i].setBackground(getRowHeaderColor(li_myIndexOfParent)); //
					} else {
						cellItemVOs[j - 1][li_col_level + i].setBackground(getRowHeaderColor(j - 1)); //
					}
					hstParent_row.add(parentNode); //
				}
			}
		}

		HashMap formulaCpMap = new HashMap(); //用来存储T1,T2,T3等计算列的map,因为后面公式计算需要！！
		//实际给每个数据格子赋值！！！实际上就是将行头与列头的所有叶子结点做【交叉迪卡尔】乘积！！！
		ArrayList al_hvos = new ArrayList(); //存储所有实际数据的HashVO
		for (int i = 0; i < allLeafNodes_col.length; i++) { //遍历所有列头的叶子结点！！！！
			HashVO hvo_colItem = (HashVO) allLeafNodes_col[i].getUserObject(); //
			String str_realLevelNameValue_col = hvo_colItem.getStringValue("RealLevelNameValue"); //实际维度值
			String str_itemType_col = hvo_colItem.getStringValue("ItemType"); //类型,至关重要！！

			HashVO hvo_realRowData = null; //某一实际行的数据
			if (!(str_itemType_col.equals("SubTotal") || str_itemType_col.equals("Total"))) { //如果不是合计
				hvo_realRowData = new HashVO(); //
				HashMap colGroupValueMap = tbutil.convertStrToMapByExpress(str_realLevelNameValue_col, "▲", "="); //
				for (int w = 0; w < chooseGroupColFields.length; w++) { //先将所有列维加入
					String str_levelValue = (String) colGroupValueMap.get(chooseGroupColFields[w]); //
					hvo_realRowData.setAttributeValue(chooseGroupColFields[w], str_levelValue == null ? "" : str_levelValue); //
					hvo_realRowData.setUserObject("bgcolor@" + chooseGroupColFields[w], getColHeaderColor(w)); //设置背景颜色!
				}
			}
			for (int j = 0; j < allLeafNodes_row.length; j++) { //遍历所有行头的叶子结点！！！！
				HashVO hvo_rowItem = (HashVO) allLeafNodes_row[j].getUserObject(); //
				String str_realLevelNameValue_row = hvo_rowItem.getStringValue("RealLevelNameValue"); //实际维度值
				String str_itemType_row = hvo_rowItem.getStringValue("ItemType"); //类型,至关重要！！
				String str_realComputeField = null, str_realComputeType = null, str_realComputeConfig = null; //
				if (isOnlyRowGroup) { //如果只有行维度,则说明这时计算列自动移到了列上,这时就不能从行的叶子结点上取计算列了,而应该是从列头上取了!
					str_realComputeField = hvo_colItem.getStringValue("RealCPField"); //
					str_realComputeType = hvo_colItem.getStringValue("RealCPType"); //必须使用行头的计算,因为行头可能发生分裂计算的情况!
					str_realComputeConfig = hvo_colItem.getStringValue("RealCPConfig"); //配置信息 
				} else { //否则永远强行从行头上取计算类型与字段名！
					str_realComputeField = hvo_rowItem.getStringValue("RealCPField"); //
					str_realComputeType = hvo_rowItem.getStringValue("RealCPType"); //必须使用行头的计算,因为行头可能发生分裂计算的情况!
					str_realComputeConfig = hvo_rowItem.getStringValue("RealCPConfig"); //配置信息
				}
				HashMap confMap = tbutil.convertStrToMapByExpress(str_realComputeConfig, ";", "="); //其他配置信息,比如预警规则,是否带百分号等...

				String str_mapDataKey = str_realLevelNameValue_row + str_realLevelNameValue_col + "@" + str_realComputeField + "(" + str_realComputeType + ")"; //
				String str_mapDataKey2 = str_realLevelNameValue_row + str_realLevelNameValue_col + "#value"; //存储id数组的。。。
				String real_value = "";
				BigDecimal ld_value = new BigDecimal("0");//以前为double类型，超过千万级数据会报错，故修改之【李春娟/2019-03-21】
				if (str_realComputeType != null && str_realComputeType.equals("init")) { // 袁江晓添加 统计显示初始值  20130312
					real_value = (String) beforeBuilderDataMap.get(str_mapDataKey); //
				} else {
					ld_value = (BigDecimal) beforeBuilderDataMap.get(str_mapDataKey); //
				}
				String str_values2 = (String) beforeBuilderDataMap.get(str_mapDataKey2); //取得所有绑定的Values

				BillCellItemVO cellItemVO = new BillCellItemVO(); //创建数据VO
				cellItemVO.setCellvalue(""); //先搞个空串
				boolean isTotalCell = (str_itemType_col.equals("SubTotal") || str_itemType_col.equals("Total") || str_itemType_row.equals("SubTotal") || str_itemType_row.equals("Total")) ? true : false; //判断是否是合计的格子!
				if ((str_itemType_col.equals("SubTotal") || str_itemType_col.equals("Total")) && !isCaneTotlal(str_realComputeType)) { //如果左表头上是合计列,但实际上这个计算字段又是不能合计的玩意(比如avg)
					cellItemVO.setCellvalue(""); //为空  
					//像平均数,
				} else {
					if (str_itemType_row.equals("FormulaLevel")) { //如果是公式组!则进行复杂的公式计算!他计算的是同一行！
						String[] str_containGroups = (String[]) hvo_rowItem.getObjectValue("containgroups"); //
						String str_LevelFormula = hvo_rowItem.getStringValue("LevelFormula"); //
						String str_LevelConf = hvo_rowItem.getStringValue("LevelConfig"); //维度的参数
						cellItemVO.setCustProperty("ContainGroups", str_containGroups); //包含哪些组！！
						cellItemVO.setCustProperty("LevelFormula", str_LevelFormula); //包含哪些组！！

						//先分解行头组,然后将最一个组的维度替换成一个个的包含组,然后一个个取值(是真正的Map中的值),放在一个HashMap中!(Key是组的值,值是其实际值!)
						//取出计算公式!然后将公式中的{}中包含的数据替换成上一步Map中得到的数据!!取不到的当零处理!
						String[] str_rowLevels = tbutil.split(str_realLevelNameValue_row, "◆"); //
						String str_lastLevel = str_rowLevels[str_rowLevels.length - 1]; //
						HashMap allOtherGroupValueMap = new HashMap(); //其他所有组的值!
						for (int k = 0; k < str_containGroups.length; k++) { //遍历所有其他维度!
							String str_oneGroupKey = tbutil.replaceAll(str_mapDataKey, str_lastLevel, hvo_rowItem.getStringValue("levelname") + "=" + str_containGroups[k]); //替换!!!将mapkey中的【风险等级=比率1】替换成【风险等级=高风险】
							String str_oneGroupValue = trimDoubleToStr((Double) beforeBuilderDataMap.get(str_oneGroupKey)); //取得某个组的值!
							if (str_oneGroupValue.equals("")) {
								str_oneGroupValue = "0"; //
							}
							allOtherGroupValueMap.put(str_containGroups[k], str_oneGroupValue); //将其他组的值放进来!其实是这行的值!【高风险】=【30】
						}

						//将公式替换!
						try {
							String str_LevelFormula_convert = str_LevelFormula; //
							String[] str_formulaKeys = tbutil.getFormulaMacPars(str_LevelFormula_convert, "{", "}"); //
							for (int k = 0; k < str_formulaKeys.length; k++) {
								String str_mapItemValue = (String) allOtherGroupValueMap.get(str_formulaKeys[k]); //
								if (str_mapItemValue == null || str_mapItemValue.trim().equals("")) { //如果为空或空串,则当零处理!
									str_mapItemValue = "0";
								}
								str_LevelFormula_convert = tbutil.replaceAll(str_LevelFormula_convert, "{" + str_formulaKeys[k] + "}", str_mapItemValue); //替换!!
							}

							//然后使用公式引擎进行计算!! 将计算出来的值放在这个格子中！
							Double jepValue = (Double) getJepParse().execFormula(str_LevelFormula_convert); //【(0*100)/(0+0+0+0)】【(0*100)/(0+1+0+0)】18+22
							if (!"NaN".equalsIgnoreCase("" + jepValue)) { //发生除数为零的则不赋值!
								cellItemVO.setCellvalue(trimDoubleToStr(jepValue, null, tbutil.convertStrToMapByExpress(str_LevelConf, ";", "="))); //所有组！！
							}
							cellItemVO.setCustProperty("LevelFormulaParse", "【" + str_LevelFormula + "】=>【" + str_LevelFormula_convert + "】=>【" + jepValue + "】"); //公式解析后的样子!可以与当前行的数据验证!
						} catch (Exception ex) {
							String str_exmsg = "计算公式组[" + str_LevelFormula + "]发生异常:<" + ex.getClass().getName() + ">" + ex.getMessage();
							System.err.println(str_exmsg); //
							cellItemVO.setCustProperty("LevelFormulaParse", str_exmsg); //
						}
					} else { //如果标准的组!则进行取数!
						//以前像：平均数,记录数占比,求和占比,两者差额,两者比例,同比,环比,都是在后台计算的,后来发现在后来处理更复杂的逻辑是不行的(比如小计的平均数,百分比是横向之间的计算!),
						if (str_realComputeType != null & str_realComputeType.equals("init")) { // 袁江晓添加 统计显示初始值  20130312
							cellItemVO.setCellvalue(real_value == null ? "" : real_value.toString()); //★★★★★★格子中真正赋值的地方★★★★★★   //20120909  袁江晓修改，根据要求，为空的地方统计的结果就显示0
						} else if (str_realComputeType.equals("count") || str_realComputeType.equals("sum")) {
							//以前为double类型，超过千万级数据会报错，故修改之【李春娟/2019-03-21】
							cellItemVO.setCellvalue(ld_value == null ? "0" : trimBigDecimalToStr(ld_value, str_realComputeType, confMap)); //★★★★★★格子中真正赋值的地方★★★★★★   //20120909  袁江晓修改，根据要求，为空的地方统计的结果就显示0
						} else if (str_realComputeType.equals("avg")) { //平均数!   // 平均数时有问题 以后会修改该功能  20130312
							String str_trim_cpfield = str_realComputeField;
							if (str_realComputeField.indexOf("-") > 0) {
								str_trim_cpfield = str_realComputeField.substring(0, str_realComputeField.indexOf("-")); //实际计算的字段名,因为当前的计算名是【记录数-占比】,实际上是要取前面的【记录数】三个字
							}
							String str_key_thiscount = str_realLevelNameValue_row + str_realLevelNameValue_col + "@" + str_trim_cpfield + "(count)"; //
							String str_key_thissum = str_realLevelNameValue_row + str_realLevelNameValue_col + "@" + str_trim_cpfield + "(sum)"; //

							//以前为double类型，超过千万级数据会报错，故修改之【李春娟/2019-03-21】
							BigDecimal ld_value_thiscount = (BigDecimal) beforeBuilderDataMap.get(str_key_thiscount); //
							BigDecimal ld_value_thissum = (BigDecimal) beforeBuilderDataMap.get(str_key_thissum); //
							if (ld_value_thiscount == null) {
								ld_value_thiscount = new BigDecimal("0"); //
							}
							if (ld_value_thissum == null) {
								ld_value_thissum = new BigDecimal("0"); //
							}
							cellItemVO.setCustProperty("平均数计算公式", "" + ld_value_thissum + "/" + ld_value_thiscount); //
							if (ld_value_thiscount.compareTo(new BigDecimal("0")) != 0) { //可能发生除数为零的情况!!
								ld_value = ld_value_thissum.divide(ld_value_thiscount, 2, BigDecimal.ROUND_HALF_UP); //
								cellItemVO.setCellvalue(trimBigDecimalToStr(ld_value, str_realComputeType, confMap)); //拿当前值与纵向最右边的合并维度计算!
							}
						} else if (str_realComputeType.equals("PercentCount")) { //记录数占比!
							String str_trim_cpfield = str_realComputeField;
							if (str_realComputeField.indexOf("-") > 0) {
								str_trim_cpfield = str_realComputeField.substring(0, str_realComputeField.indexOf("-")); //实际计算的字段名,因为当前的计算名是【记录数-占比】,实际上是要取前面的【记录数】三个字
							}
							String str_key_thiscount = str_realLevelNameValue_row + str_realLevelNameValue_col + "@" + str_trim_cpfield + "(count)"; //
							String str_key_countsum = str_realLevelNameValue_col + "@" + str_trim_cpfield + "(count)"; //
							if ((str_realLevelNameValue_row.equals("") && !isTotalCell) || chooseGroupRowFields.length == 0) { //如果没有行头维度,即表示直接坚着的,这时应强行自动取总数!但合计格子不算!
								str_key_countsum = "@" + str_trim_cpfield + "(count)"; //
							}
							//以前为double类型，超过千万级数据会报错，故修改之【李春娟/2019-03-21】
							BigDecimal ld_value_thiscount = (BigDecimal) beforeBuilderDataMap.get(str_key_thiscount); //
							BigDecimal ld_value_countsum = (BigDecimal) beforeBuilderDataMap.get(str_key_countsum); //
							if (ld_value_thiscount == null) {
								ld_value_thiscount = new BigDecimal("0"); //
							}
							if (ld_value_countsum == null) {
								ld_value_countsum = new BigDecimal("0"); //
							}
							cellItemVO.setCustProperty("记录数占比的实际计算情况", "" + ld_value_thiscount + "/" + ld_value_countsum); //
							if (ld_value_countsum.compareTo(new BigDecimal("0")) != 0) { //可能发生除数为零的情况!!
								BigDecimal ld_percent = (ld_value_thiscount.multiply(new BigDecimal("100")).divide(ld_value_countsum, 2, BigDecimal.ROUND_HALF_UP)); //
								cellItemVO.setCellvalue(trimBigDecimalToStr(ld_percent, str_realComputeType, confMap)); //拿当前值与纵向最右边的合并维度计算!
							}
						} else if (str_realComputeType.equals("PercentSum")) { //求和占比!!
							String str_trim_cpfield = str_realComputeField;
							if (str_realComputeField.indexOf("-") > 0) {
								str_trim_cpfield = str_realComputeField.substring(0, str_realComputeField.indexOf("-")); //实际计算的字段名,因为当前的计算名是【记录数-占比】,实际上是要取前面的【记录数】三个字
							}
							String str_key_thiscount = str_realLevelNameValue_row + str_realLevelNameValue_col + "@" + str_trim_cpfield + "(sum)"; //
							String str_key_countsum = str_realLevelNameValue_col + "@" + str_trim_cpfield + "(sum)"; //
							if ((str_realLevelNameValue_row.equals("") && !isTotalCell) || chooseGroupRowFields.length == 0) { //如果没有行头维度,即表示直接坚着的,这时应强行自动取总数!但合计格子不算!
								str_key_countsum = "@" + str_trim_cpfield + "(sum)"; //
							}
							//以前为double类型，超过千万级数据会报错，故修改之【李春娟/2019-03-21】
							BigDecimal ld_value_thiscount = (BigDecimal) beforeBuilderDataMap.get(str_key_thiscount); //
							BigDecimal ld_value_countsum = (BigDecimal) beforeBuilderDataMap.get(str_key_countsum); //
							if (ld_value_thiscount == null) {
								ld_value_thiscount = new BigDecimal("0"); //
							}
							if (ld_value_countsum == null) {
								ld_value_countsum = new BigDecimal("0"); //
							}
							cellItemVO.setCustProperty("求和占比的实际计算情况", "" + ld_value_thiscount + "/" + ld_value_countsum); //
							if (ld_value_countsum.compareTo(new BigDecimal("0")) != 0) { //可能发生除数为零的情况!!
								BigDecimal ld_percent = (ld_value_thiscount.multiply(new BigDecimal("100")).divide(ld_value_countsum, 2, BigDecimal.ROUND_HALF_UP)); //
								cellItemVO.setCellvalue(trimBigDecimalToStr(ld_percent, str_realComputeType, confMap)); //拿当前值与纵向最右边的合并维度计算!
							}
						} else if (str_realComputeType.equals("FormulaCompute")) { //公式计算！！！即{T1}+{T2}
							String str_formula_cp = (String) confMap.get("公式"); //计算列的公式!
							if (str_formula_cp != null && !str_formula_cp.trim().equals("")) { //如果定义了计算列公式!
								cellItemVO.setCustProperty("FormulaCompute", str_formula_cp); //公式解析后的样子!可以与当前行的数据验证!
								try {
									String str_formula_cp_convert = str_formula_cp; //
									String str_level_prefix = str_realLevelNameValue_row + str_realLevelNameValue_col; //为了公式计算,必须寻找维度与我相同的格子,即前面的一样!
									String[] str_formulaKeys = tbutil.getFormulaMacPars(str_formula_cp_convert, "{", "}"); //挖掘大括号包含的段落！！！
									for (int k = 0; k < str_formulaKeys.length; k++) { //遍历所有段落
										String str_mapItemValue = (String) formulaCpMap.get(str_level_prefix + "@" + str_formulaKeys[k]); //即寻找【业务类型=个人业务@T1】
										if (str_mapItemValue == null || str_mapItemValue.trim().equals("")) { //如果为空或空串,则当零处理!
											str_mapItemValue = "0";
										}
										if (str_mapItemValue.endsWith("%")) {
											str_mapItemValue = str_mapItemValue.substring(0, str_mapItemValue.length() - 1); //如果是百分比的数值,则裁掉百分号,否则公式计算肯定报错!!
										}
										str_formula_cp_convert = tbutil.replaceAll(str_formula_cp_convert, "{" + str_formulaKeys[k] + "}", str_mapItemValue); //替换!!即将【{T1}+{T2}】替换成【25+86】
									}
									//然后使用公式引擎进行计算!! 将计算出来的值放在这个格子中！
									Double jepValue = (Double) getJepParse().execFormula(str_formula_cp_convert); //【(0*100)/(0+0+0+0)】【(0*100)/(0+1+0+0)】18+22
									if (!"NaN".equalsIgnoreCase("" + jepValue)) { //发生除数为零的则不赋值!
										cellItemVO.setCellvalue(trimDoubleToStr(jepValue, null, confMap)); //所有组！！
									}
									cellItemVO.setCustProperty("FormulaComputeParse", "【" + str_formula_cp + "】=>【" + str_formula_cp_convert + "】=>【" + jepValue + "】"); //公式解析后的样子!可以与当前行的数据验证!
								} catch (Exception ex) {
									String str_exmsg = "计算公式发生异常:" + ex.getClass().getName() + "," + ex.getMessage(); //
									System.err.println(str_exmsg); //
									cellItemVO.setCustProperty("FormulaComputeParse", str_exmsg); //
								}
							} else {
								cellItemVO.setCustProperty("FormulaComputeParse", "扩展参数中没有定义\"公式\",所以不进行任何计算!"); //
							}
						} else if (str_realComputeType.endsWith("ChainIncrease") || str_realComputeType.endsWith("PeriodIncrease")) { //所有【同比/环比】增长率！！！
							ld_value = setCellItemVOByIncrese(cellItemVO, str_realComputeField, str_realLevelNameValue_row, str_realLevelNameValue_col, str_realComputeType, confMap); //
						} else if (str_realComputeType.endsWith("ChainSeries") || str_realComputeType.endsWith("PeriodSeries")) { //所有【同比/环比】增长次数！！！
							ld_value = setCellItemVOByPeriodSeries(cellItemVO, str_realComputeField, str_realLevelNameValue_row, str_realLevelNameValue_col, str_realComputeType, confMap); //
						} else {
							cellItemVO.setCustProperty("FormulaComputeParse", "未知的计算类型:" + str_realComputeType); //
						}
					}
				}

				//为了支持公式计算,必须再搞一个哈希表,然后专门使用T1,T1,T3作为key来存储!!!一定要在计算后执行!即后面的可以用前面的，而前面的不能用后面的！比如T4中的公式可以使用T1,T2,T3,但T2不能使用T4
				if (str_itemType_row.equals("SplitCP")) { //如果是分裂的!
					String str_cpindexno = hvo_rowItem.getStringValue("CpIndexNo"); //计算列的索引号！！！
					String str_formulaKey = str_realLevelNameValue_row + str_realLevelNameValue_col + "@" + "T" + str_cpindexno; //即【业务类型=个人业务@T1】
					formulaCpMap.put(str_formulaKey, cellItemVO.getCellvalue()); //直接调值,可能是百分比,比如35%,则到时公式计算时，需要先将百分号截掉！！！
					cellItemVO.setCustProperty("FormulaMapKey", str_formulaKey); //为了调试方便,在这个格子属性上也设置下这个Key
				}
				cellItemVO.setHalign(3); //
				cellItemVO.setCustProperty("MapKey", str_mapDataKey); //
				cellItemVO.setCustProperty("#value", str_values2); //
				if (isTotalCell) { //如果是小计或合计列??
					int li_colPathDepth = 0; //
					int li_rowPathDepth = 0; //
					if (str_itemType_col.equals("SubTotal") || str_itemType_col.equals("Total")) {
						li_colPathDepth = allLeafNodes_col[i].getLevel() - 1; //	
					}
					if (str_itemType_row.equals("SubTotal") || str_itemType_row.equals("Total")) {
						li_rowPathDepth = allLeafNodes_row[j].getLevel() - 1; //
					}
					int li_pathDepth = (li_colPathDepth < li_rowPathDepth ? li_rowPathDepth : li_colPathDepth); //取小的层级!!!
					cellItemVO.setBackground(getTotalColor(li_pathDepth)); //如果合计的格子,则黄色!
					cellItemVO.setCustProperty("itemtype", "ComputeTotal"); //计算格子的合计!
				} else {
					cellItemVO.setCustProperty("itemtype", "ComputeCell"); //计算格子的合计！！
					if (!str_itemType_row.equals("FormulaLevel")) { //如果是公式组计算的,则无法进行超接连
						cellItemVO.setIshtmlhref("Y"); //有超链！！	
					}

					//报表无法钻取明细不加超链 【杨科/2013-06-20】
					/*if (str_values2==null||str_values2.equals("")) { 
						cellItemVO.setIshtmlhref("N"); 
					}*/

					//处理警界值,即经常遇到项目有预警需求,即直接查询时就显示出警界值!
					if (ld_value != null && confMap.containsKey("警界规则")) { //如果有警界规则这个属性！！
						String str_warnRule = (String) confMap.get("警界规则"); //这里要设计一个非常健壮易懂的的预警规则语法,然后解析一下！！
						if (str_warnRule != null) { //如果定义了警界值!
							String[] str_warnValues = tbutil.split(str_warnRule, ","); //使用分号相隔!
							for (int w = 0; w < str_warnValues.length; w++) {
								String str_warnItem = str_warnValues[w]; //这里支持公式与宏代码,比如是【{平均数}*1.5】
								double ld_warnValue = getJepFormulaValue(str_warnItem); //转成double
								//以前为double类型，超过千万级数据会报错，故修改之【李春娟/2019-03-21】
								if (ld_value.compareTo(new BigDecimal(ld_warnValue)) > 0) { //如果当前值大于警界值,则警告色显示!以后这里可能搞个参数支持小于计算方法！现在默认是是大于计算！
									cellItemVO.setBackground(getWarnColor(w)); //红色显示!!!
									break; //优先匹配前面的警界值,即满足最高警界面值后,后面的不再计算了！
								}
							}
						}
					}
				}

				cellItemVO.setCustProperty("RowHeaderLevelNameValue", str_realLevelNameValue_row); //行头维度与值!
				cellItemVO.setCustProperty("ColHeaderLevelNameValue", str_realLevelNameValue_col); //列头维度与值!
				cellItemVO.setCustProperty("RealCPField", str_realComputeField); //计算字段！！
				cellItemVO.setCustProperty("RealCPType", str_realComputeType); //计算类型！！
				cellItemVO.setCustProperty("RealCPConfig", str_realComputeConfig); //计算类型！！

				if (hvo_realRowData != null && !(str_itemType_row.equals("SubTotal") || str_itemType_row.equals("Total"))) { //如果不是合计!
					String str_hvoitemKey = getPathName(allLeafNodes_row[j]); //
					hvo_realRowData.setAttributeValue(str_hvoitemKey, cellItemVO.getCellvalue()); //设置值!
					hvo_realRowData.setUserObject("bgcolor@" + str_hvoitemKey, cellItemVO.getBackground()); //设置背景颜色!
				}

				cellItemVOs[li_row_level + i][li_col_level + j] = cellItemVO; ////
			}

			if (hvo_realRowData != null) {
				al_hvos.add(hvo_realRowData); //
			}
		}
		billCellVO.setCellItemVOs(cellItemVOs); //
		HashVO[] hvs_realData = (HashVO[]) al_hvos.toArray(new HashVO[0]); //

		if (secondHashVOComputeMap != null) { //必须是定了了的
			String str_orderindex = (String) secondHashVOComputeMap.get("按第几位计算列排序"); //
			if (str_orderindex != null && !str_orderindex.equals("")) { //是否指定了重新排序??
				int li_cpIndex = Integer.parseInt(str_orderindex); //二次数据计算,生成HashVO后排序的字段!
				if (hvs_realData.length > 0) { //排序
					String[] str_hvkeys = hvs_realData[0].getKeys(); //
					if (str_hvkeys.length >= chooseGroupColFields.length + li_cpIndex) {
						String str_sortKey = str_hvkeys[chooseGroupColFields.length + li_cpIndex - 1]; //
						tbutil.sortHashVOs(hvs_realData, new String[][] { { str_sortKey, "Y", "Y" } }); //排一下!
					}
				}
			}
			String[] str_hiddcols = (String[]) secondHashVOComputeMap.get("输出时隐藏的列"); ////
			if (str_hiddcols != null && str_hiddcols.length > 0) {
				tbutil.removeHashVOItems(hvs_realData, str_hiddcols); //移去一些列!比如邮储银行中就强烈要求月份不要显示!
			}
		}

		//根据构造好的数据对象创建面板!
		cellPanel = new BillCellPanel(billCellVO); //
		cellPanel.setEditable(false); //不可编辑
		cellPanel.addBillCellHtmlHrefListener(this); //
		cellPanel.setAllowShowPopMenu(false); //先禁用掉原来的右键功能!
		cellPanel.getTable().addMouseListener(new MouseAdapter() { //增加自己的右键功能!
					@Override
					public void mouseClicked(MouseEvent e) {
						if (e.getButton() == MouseEvent.BUTTON3) {
							popShowRightMenu((JComponent) e.getSource(), e.getPoint()); //右键弹出显示菜单!
						}
					}
				}); //
		cellPanel.getTable().setToolTipText("提示:右键有【钻取】【过滤】等更多功能"); //
		cellPanel.setCrystal(true); //透明!!!

		this.removeAll();
		this.setOpaque(false); // 透明!!
		this.setLayout(new BorderLayout(0, 0)); //

		if (secondHashVOComputeMap == null || secondHashVOComputeMap.size() <= 0) { //如果没有定义显示HashVO的Map,则原来的处理! 这是绝大多数情况!
			this.add(cellPanel, BorderLayout.CENTER); //在中间内容加入!!
		} else { //如果定义了,则可能要使用双页签!
			BillCellPanel hvoCell = new BillCellPanel(hvs_realData); //
			hvoCell.setEditable(false); //不可编辑
			hvoCell.setAllowShowPopMenu(false); //先禁用掉原来的右键功能!
			hvoCell.setCrystal(true); //透明!
			if (ClientEnvironment.isAdmin()) { //如果是管理员身份,则使用两个页签,这是为了Debug时方便
				JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM); //
				JPanel tmpPanel_1 = WLTPanel.createDefaultPanel(new BorderLayout()); //
				tmpPanel_1.add(hvoCell); //
				JPanel tmpPanel_2 = WLTPanel.createDefaultPanel(new BorderLayout()); //
				tmpPanel_2.add(cellPanel); //
				tabbedPane.addTab("HashVO数据", tmpPanel_1); //
				tabbedPane.addTab("Cell数据", tmpPanel_2); //
				this.add(tabbedPane, BorderLayout.CENTER); //在中间内容加入!!
			} else { //如果不是管理员,则直接是输出HashVO
				this.add(hvoCell, BorderLayout.CENTER); //在中间内容加入!!
			}
		}
		this.add(getNorthBtnPanel(), BorderLayout.NORTH); //在上方加入按钮面板!!

		this.updateUI(); //
	}

	/**
	 * 以前为double类型，超过千万级数据会报错，故修改之【李春娟/2019-03-21】
	 * 处理【计录数,求和,平均数】的【环比,同比】计算
	 * @param cellItemVO
	 * @param str_realComputeField
	 * @param str_realLevelNameValue_row
	 * @param str_realLevelNameValue_col
	 * @param _computeType
	 */
	private BigDecimal setCellItemVOByIncrese(BillCellItemVO cellItemVO, String str_realComputeField, String str_realLevelNameValue_row, String str_realLevelNameValue_col, String _computeType, HashMap _confMap) {
		String[] str_thidDatePeriodValue = getThisDatePeriodValue(chooseGroupRowFields, chooseGroupColFields, str_realLevelNameValue_row, str_realLevelNameValue_col); //首先取得时间维度!看是否存在时间维度!如果存在,则才可以进行
		if (str_thidDatePeriodValue[0] == null) {
			cellItemVO.setCustProperty("计算当前维度值时失败", "" + str_thidDatePeriodValue[1]);
			return null; //直接返回!
		}

		String str_dateGroupName = str_thidDatePeriodValue[0]; //
		String str_thisDatePeriodValue = str_thidDatePeriodValue[1];
		String str_thisDateType = str_thidDatePeriodValue[2]; //季度/月度
		String str_fromRowCol = str_thidDatePeriodValue[3]; //row/col   

		String str_frontPeriod = getPeriodDateValue(str_thisDatePeriodValue, str_thisDateType, _computeType); //取得上一个时间周期
		String str_newMapKey = null; //
		if (str_fromRowCol.equals("row")) { //如果是在行头上发现的!
			str_newMapKey = tbutil.replaceAll(str_realLevelNameValue_row, str_dateGroupName + "=" + str_thisDatePeriodValue, str_dateGroupName + "=" + str_frontPeriod) + str_realLevelNameValue_col; //
		} else if (str_fromRowCol.equals("col")) { //
			str_newMapKey = str_realLevelNameValue_row + tbutil.replaceAll(str_realLevelNameValue_col, str_dateGroupName + "=" + str_thisDatePeriodValue, str_dateGroupName + "=" + str_frontPeriod); //
		}

		String[] str_cpValue = getOneDatePeriodValue(str_newMapKey, str_realComputeField, str_realLevelNameValue_row + str_realLevelNameValue_col, _computeType); //
		cellItemVO.setCustProperty("公式计算后的实际结果", "" + str_cpValue[0]);
		cellItemVO.setCustProperty("上一时间周期{" + str_frontPeriod + "}的计算公式", str_cpValue[1]); //实际公式
		cellItemVO.setCustProperty("上一时间周期{" + str_frontPeriod + "}的取数MapKey", str_cpValue[2]); //
		if (str_cpValue[0] != null && !"NaN".equalsIgnoreCase(str_cpValue[0]) && !"Infinity".equalsIgnoreCase(str_cpValue[0])) { //如果不是除数为零件的情况
			cellItemVO.setCellvalue(trimBigDecimalToStr(new BigDecimal(str_cpValue[0]), _computeType, _confMap)); //赋值！！
			return new BigDecimal(str_cpValue[0]); //
		} else {
			return null;
		}
	}

	/**
	 * //以前为double类型，超过千万级数据会报错，故修改之【李春娟/2019-03-21】
	 * 连续增长次数计算!! 它麻烦的是,它需要连续往前计算,至于找不到数据,才停止!
	 * @param cellItemVO
	 * @param str_realComputeField
	 * @param str_realLevelNameValue_row
	 * @param str_realLevelNameValue_col
	 * @param _computeType
	 * @param _confMap
	 */
	private BigDecimal setCellItemVOByPeriodSeries(BillCellItemVO cellItemVO, String str_realComputeField, String str_realLevelNameValue_row, String str_realLevelNameValue_col, String _computeType, HashMap _confMap) {
		String[] str_thidDatePeriodValue = getThisDatePeriodValue(chooseGroupRowFields, chooseGroupColFields, str_realLevelNameValue_row, str_realLevelNameValue_col); //首先取得时间维度!看是否存在时间维度!如果存在,则才可以进行
		if (str_thidDatePeriodValue[0] == null) {
			cellItemVO.setCustProperty("计算当前维度值时失败", "" + str_thidDatePeriodValue[1]);
			return null; //直接返回!
		}

		String str_dateGroupName = str_thidDatePeriodValue[0]; //
		String str_thisDatePeriodValue = str_thidDatePeriodValue[1];
		String str_thisDateType = str_thidDatePeriodValue[2]; //季度/月度
		String str_fromRowCol = str_thidDatePeriodValue[3]; //row/col   
		String str_cycleDatePeriodValue = str_thisDatePeriodValue; //循环使用的日期变量,即在死循环中不断更新这个值！！！

		int li_count = 0;
		//死循环!,即一个个的周期往前推,直到找到一个不存在的时间维度,或者维度值小于0,才退出！！！！！否则不断累加!!
		//以后为了提高性能,应该搞个map,记录下前面计算过的!即当计算3季度的增长次数时,其实前面的2季度已经计算过了! 而且更多的情况是往往同时计算增长率与增长次数的! 这时缓存提高性能的效果更明显！！因为时间关系,此功能以后再弄！！！
		StringBuilder sb_allPriods = new StringBuilder(); //记录所有计算过的维度! 
		while (1 == 1) {
			//先找出当前的!
			String str_newMapKey1 = null; //
			if (str_fromRowCol.equals("row")) { //如果是在行头上发现的!
				str_newMapKey1 = tbutil.replaceAll(str_realLevelNameValue_row, str_dateGroupName + "=" + str_thisDatePeriodValue, str_dateGroupName + "=" + str_cycleDatePeriodValue) + str_realLevelNameValue_col; //
			} else if (str_fromRowCol.equals("col")) { //
				str_newMapKey1 = str_realLevelNameValue_row + tbutil.replaceAll(str_realLevelNameValue_col, str_dateGroupName + "=" + str_thisDatePeriodValue, str_dateGroupName + "=" + str_cycleDatePeriodValue); //
			}

			//再找出前一个期间的!
			String str_frontPeriod = getPeriodDateValue(str_cycleDatePeriodValue, str_thisDateType, _computeType); //取得该时间的上一个时间周期!这永远是能取到的!
			String str_newMapKey2 = null; //
			if (str_fromRowCol.equals("row")) { //如果是在行头上发现的!
				str_newMapKey2 = tbutil.replaceAll(str_realLevelNameValue_row, str_dateGroupName + "=" + str_thisDatePeriodValue, str_dateGroupName + "=" + str_frontPeriod) + str_realLevelNameValue_col; //
			} else if (str_fromRowCol.equals("col")) { //
				str_newMapKey2 = str_realLevelNameValue_row + tbutil.replaceAll(str_realLevelNameValue_col, str_dateGroupName + "=" + str_thisDatePeriodValue, str_dateGroupName + "=" + str_frontPeriod); //
			}

			//计算!!!
			String[] str_cpValue = getOneDatePeriodValue(str_newMapKey2, str_realComputeField, str_newMapKey1, _computeType); //取得与上一周期的实际比例,比如记录数环比!
			sb_allPriods.append("【" + str_cycleDatePeriodValue + "】=【" + trimDoubleToStr(str_cpValue[0]) + "】"); ////

			if (str_cpValue[0] == null || str_cpValue[0].trim().equals("") || "NaN".equalsIgnoreCase(str_cpValue[0]) || "Infinity".equalsIgnoreCase(str_cpValue[0])) { //如果为空,则退出循环!
				break; //
			}

			if (Double.parseDouble(str_cpValue[0]) <= 0) { //如果金额小于0则退出
				break; //
			}

			li_count = li_count + 1; //加1
			str_cycleDatePeriodValue = str_frontPeriod; //重新赋值,造成递归再次小溯的效果,否则会造成死循环！！！
		}

		cellItemVO.setCellvalue("" + li_count); //
		cellItemVO.setCustProperty("所有计算过的维度", sb_allPriods.toString()); //
		return new BigDecimal(li_count); //
	}

	//取得时间维度,只找到第一个即可!
	private String[] getThisDatePeriodValue(String[] _rowGroups, String[] _colGroups, String _realLevelNameValue_row, String _realLevelNameValue_col) {
		if (this.dateGroupDefineMap != null) { //如果定义了
			if (_rowGroups != null && _rowGroups.length > 0) {
				for (int i = _rowGroups.length - 1; i >= 0; i--) { //倒过来找!
					if (dateGroupDefineMap.containsKey(_rowGroups[i])) {
						HashMap map = tbutil.convertStrToMapByExpress(_realLevelNameValue_row, "◆", "="); //分隔!
						String str_value = (String) map.get(_rowGroups[i]); //维度值!比如:2012年1季度!!!
						if (str_value == null || str_value.trim().equals("") || str_value.equals("【空值】")) { //特别要注意时间维度值可能存在【空值】的情况(比如根本就没有录入“发生时间”),否则后面肯定报错!
							return new String[] { null, "取得的本时间维度值为空,所以无法继续计算" }; //
						} else {
							return new String[] { _rowGroups[i], str_value, (String) dateGroupDefineMap.get(_rowGroups[i]), "row" }; //是在行头上找到的!
						}
					}
				}
			}

			if (_colGroups != null && _colGroups.length > 0) {
				for (int i = _colGroups.length - 1; i >= 0; i--) { //倒过来找!
					if (dateGroupDefineMap.containsKey(_colGroups[i])) {
						HashMap map = tbutil.convertStrToMapByExpress(_realLevelNameValue_col, "▲", "="); //分隔!
						String str_value = (String) map.get(_colGroups[i]); //维度值!比如:2012年1季度
						if (str_value == null || str_value.trim().equals("") || str_value.equals("【空值】")) { //特别要注意时间维度值可能存在【空值】的情况(比如根本就没有录入“发生时间”),否则后面肯定报错!
							return new String[] { null, "取得的本时间维度值为空,所以无法继续计算" }; //
						} else {
							return new String[] { _colGroups[i], str_value, (String) dateGroupDefineMap.get(_colGroups[i]), "col" }; //是在列头上找到的
						}
					}
				}
			}
			return new String[] { null, "当前维度中没有一个是时间维度,要么是在构造器的方法getDateGroupDefineMap()中没定义,要么是在选择/定义参与维度中没有" };
		} else {
			return new String[] { null, "构造器中没有定义哪几个维度是时间维度,请定义getDateGroupDefineMap()" };
		}
	}

	/**
	 * 取得某一个时间维度的值,在进行同比,环比,连续增长计算时都需要用到!
	 * @return
	 */
	private String[] getOneDatePeriodValue(String _datePeriodMapKey, String str_realComputeField, String _thisMapKey, String _computeType) {
		String str_trim_cpfield = str_realComputeField;
		if (str_realComputeField.indexOf("-") > 0) {
			str_trim_cpfield = str_realComputeField.substring(0, str_realComputeField.indexOf("-")); //实际计算的字段名,因为当前的计算名是【记录数-占比】,实际上是要取前面的【记录数】三个字
		}

		//本时间周期内的count与sum
		String str_key_this_count = _thisMapKey + "@" + str_trim_cpfield + "(count)"; //
		String str_key_this_sum = _thisMapKey + "@" + str_trim_cpfield + "(sum)"; //

		//上一时间周期内的count与sum
		String str_key_frontPeriod_count = _datePeriodMapKey + "@" + str_trim_cpfield + "(count)"; //上一周期的count计算！！
		String str_key_frontPeriod_sum = _datePeriodMapKey + "@" + str_trim_cpfield + "(sum)"; //上一周期的sum计算！！
		String str_key_frontPeriod_avg = _datePeriodMapKey + "@" + str_trim_cpfield + "(avg)"; //上一周期的sum计算！！

		if (!beforeBuilderDataMap.containsKey(str_key_frontPeriod_count)) { //如果根本不包含这个key,则说明这个时间维度下根本没有值!
			return new String[] { null, "因没有取到值故无公式", "根本没有这个MapKey【" + str_key_frontPeriod_count + "】值" }; //
		}

		//以前为double类型，超过千万级数据会报错，故修改之【李春娟/2019-03-21】
		//取值!
		BigDecimal ld_value_this_count = (BigDecimal) beforeBuilderDataMap.get(str_key_this_count); //
		BigDecimal ld_value_this_sum = (BigDecimal) beforeBuilderDataMap.get(str_key_this_sum); //
		BigDecimal ld_value_frontPeriod_count = (BigDecimal) beforeBuilderDataMap.get(str_key_frontPeriod_count); //
		BigDecimal ld_value_frontPeriod_sum = (BigDecimal) beforeBuilderDataMap.get(str_key_frontPeriod_sum); //
		ld_value_this_count = (ld_value_this_count == null ? new BigDecimal("0") : ld_value_this_count);
		ld_value_this_sum = (ld_value_this_sum == null ? new BigDecimal("0") : ld_value_this_sum);
		ld_value_frontPeriod_count = (ld_value_frontPeriod_count == null ? new BigDecimal("0") : ld_value_frontPeriod_count);
		ld_value_frontPeriod_sum = (ld_value_frontPeriod_sum == null ? new BigDecimal("0") : ld_value_frontPeriod_sum);

		String str_formula = null; //
		String str_mapkeyInfo = null; //

		BigDecimal value = new BigDecimal("0");
		if (_computeType.startsWith("Count")) { //如果是Count类型的环比或同比!
			str_formula = "((" + ld_value_this_count + "-" + ld_value_frontPeriod_count + ")*100)/" + ld_value_frontPeriod_count; //
			value = ld_value_this_count.subtract(ld_value_frontPeriod_count).multiply(new BigDecimal("100")).divide(ld_value_frontPeriod_count, 2, BigDecimal.ROUND_HALF_UP);
			str_mapkeyInfo = str_key_frontPeriod_count; //
		} else if (_computeType.startsWith("Sum")) { //如果是Sum类型的环比或同比
			str_formula = "((" + ld_value_this_sum + "-" + ld_value_frontPeriod_sum + ")*100)/" + ld_value_frontPeriod_sum; //
			value = ld_value_this_sum.subtract(ld_value_frontPeriod_sum).multiply(new BigDecimal("100")).divide(ld_value_frontPeriod_sum, 2, BigDecimal.ROUND_HALF_UP);
			str_mapkeyInfo = str_key_frontPeriod_sum; //
		} else if (_computeType.startsWith("Avg")) { //如果是平均数类型的环比或同比!
			str_formula = "((" + ld_value_this_sum + "/" + ld_value_this_count + "-" + ld_value_frontPeriod_sum + "/" + ld_value_frontPeriod_count + ")*100)/(" + ld_value_frontPeriod_sum + "/" + ld_value_frontPeriod_count + ")"; //
			value = ld_value_this_sum.divide(ld_value_this_count, 2, BigDecimal.ROUND_HALF_UP).subtract((ld_value_frontPeriod_sum.divide(ld_value_frontPeriod_count, 2, BigDecimal.ROUND_HALF_UP))).multiply(new BigDecimal("100")).divide((ld_value_frontPeriod_sum.divide(ld_value_frontPeriod_count, 2, BigDecimal.ROUND_HALF_UP)), 2, BigDecimal.ROUND_HALF_UP);
			str_mapkeyInfo = str_key_frontPeriod_avg; //
		}
		//Double jepValue = (Double) getJepParse().execFormula(str_formula); //【(0*100)/(0+0+0+0)】【(0*100)/(0+1+0+0)】18+22
		if (value.compareTo(new BigDecimal(0)) == 0) {
			value = new BigDecimal("0");
		} else {
			value = value.setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		return new String[] { "" + value, str_formula, str_mapkeyInfo }; //
	}

	//取得上一个期间,比如当前期间是2012年06月,上一期就是2012年05月
	private String getPeriodDateValue(String _thisPeriod, String _type, String _tbhb) {
		if (_thisPeriod == null || _thisPeriod.trim().equals("")) {
			return _thisPeriod;
		}
		int li_year = Integer.parseInt(_thisPeriod.substring(0, 4)); //
		if (_tbhb.endsWith("ChainIncrease") || _tbhb.endsWith("ChainSeries")) { //如果是环比,麻烦一点
			if (_type.equals("季度") || _type.equals("季")) { //如果是季度
				int li_season = Integer.parseInt(_thisPeriod.substring(5, 6)); //
				li_season = li_season - 1; //
				if (li_season == 0) {
					li_season = 4; //
					li_year = li_year - 1; //
				}
				return "" + li_year + "年" + li_season + "季度"; //
			} else if (_type.equals("月度") || _type.equals("月")) { //如果是月度
				int li_month = Integer.parseInt(_thisPeriod.substring(5, 7)); //
				li_month = li_month - 1; //
				if (li_month == 0) {
					li_month = 12; //
					li_year = li_year - 1; //
				}
				return "" + li_year + "年" + ("" + (100 + li_month)).substring(1, 3) + "月"; //
			} else if (_type.equals("年度") || _type.equals("年")) {
				return "" + (li_year - 1) + _thisPeriod.substring(4, _thisPeriod.length()); //将年度减1,然后加上原来的！！！
			} else {
				return _thisPeriod; //
			}
		} else if (_tbhb.endsWith("PeriodIncrease") || _tbhb.endsWith("PeriodSeries")) { //同比很简单,就是年份减一
			return "" + (li_year - 1) + _thisPeriod.substring(4, _thisPeriod.length()); //将年度减1,然后加上原来的！！！
		} else {
			return _thisPeriod; //
		}
	}

	//对警告公式进行计算!!
	private double getJepFormulaValue(String _formula) {
		if (_formula.indexOf("+") > 0 || _formula.indexOf("-") > 0 || _formula.indexOf("*") > 0 || _formula.indexOf("/") > 0 || _formula.indexOf("}") > 0) { //如果有加减乘除,或{}，则认为是公式,才做公式处理,否则直接当是数字处理,这是为了提高性能!
			String str_newFormula = _formula; //
			if (_formula.indexOf("{总平均数}") >= 0) { //如果有特殊字符器,则进行替换处理！！
				str_newFormula = tbutil.replaceAll(_formula, "{总平均数}", "10"); //将特殊字符串替换
			}

			Double jepValue = (Double) getJepParse().execFormula(str_newFormula); //
			//System.out.println("公式[" + _formula + "]的计算值是[" + jepValue + "]"); //
			return jepValue; //
		} else {
			return Double.parseDouble(_formula); //
		}
	}

	private JepFormulaParseAtUI getJepParse() {
		if (jepParse == null) {
			jepParse = new JepFormulaParseAtUI(true);
		}
		return jepParse; //
	}

	/**
	 * 像平均数,最大值,增长率什么的是没有合计意义的!所以需要判断是否真的可以合计??
	 * @param chooseComputeFunAndField2
	 * @return
	 */
	private boolean isCaneTotlal(String[][] _chooseComputeFunAndField) {
		boolean canTotal = false; //
		for (int i = 0; i < _chooseComputeFunAndField.length; i++) {
			canTotal = isCaneTotlal(_chooseComputeFunAndField[i][0]); //
			if (canTotal) { //只要有一个能合计就算是可以合计！！
				break;
			}
		}
		return canTotal;
	}

	/**
	 * 判断某一列是否可以合计??
	 * @param _cmType
	 * @return
	 */
	private boolean isCaneTotlal(String _cmType) {
		if (_cmType.endsWith("Increase") || _cmType.endsWith("Series") || tbutil.isExistInArray(_cmType, str_notCanTotalTypes)) {
			return false; //如果是增长率或者是指定的计算方式Series
		} else {
			return true; //
		}
	}

	/**
	 * 计算上表头,左表头的内容值！！
	 * @return
	 */
	private String getTopLeftTitle() {
		StringBuilder sb_text = new StringBuilder(); //
		String str_row = ""; //
		if (chooseGroupRowFields.length > 0) {
			for (int i = 0; i < chooseGroupRowFields.length; i++) { // 还是从层开始吧,遍历各层
				str_row = str_row + chooseGroupRowFields[i];
				if (i != chooseGroupRowFields.length - 1) {
					str_row = str_row + "，"; //
				}
			}
			str_row = "【" + str_row + "】" + (isConvertXY ? "↓" : "→") + "\r\n"; //
		}

		String str_col = ""; //
		if (chooseGroupColFields.length > 0) {
			for (int i = 0; i < chooseGroupColFields.length; i++) { // 还是从层开始吧,遍历各层
				str_col = str_col + chooseGroupColFields[i];
				if (i != chooseGroupColFields.length - 1) {
					str_col = str_col + "，"; //
				}
			}
			str_col = "【" + str_col + "】" + (isConvertXY ? "→" : "↓") + "\r\n"; //
		}

		String str_cp = ""; //
		//计算列
		if (chooseComputeFunAndField.length == 1) { //如果只有一个计算列
			str_cp = "（" + chooseComputeFunAndField[0][2] + "）↘\r\n"; //
		}

		if (!isConvertXY) { //如果没有转轴
			sb_text.append(str_row); //
			sb_text.append(str_cp); //
			sb_text.append(str_col); //
		} else {
			sb_text.append(str_col); //
			sb_text.append(str_cp); //
			sb_text.append(str_row); //
		}
		String str_rt = sb_text.toString(); //
		if (str_rt.endsWith("\r\n")) {
			str_rt = str_rt.substring(0, str_rt.length() - 2); //
		}
		return str_rt; //
	}

	//以前为double类型，超过千万级数据会报错，故修改之【李春娟/2019-03-21】
	private void appendMapData(HashMap _map, String _key, BigDecimal _newValue) {
		if (_map.containsKey(_key)) { //如果有值,则累加！
			BigDecimal ld_oldValue = (BigDecimal) _map.get(_key); //
			_newValue = ld_oldValue.add(_newValue);
			_map.put(_key, _newValue); //
		} else { //如果没值,则直接加入！
			_map.put(_key, _newValue); //
		}
	}

	private void appendMapData(HashMap _map, String _key, Double _newValue) {
		if (_map.containsKey(_key)) { //如果有值,则累加！
			double ld_oldValue = (Double) _map.get(_key); //
			_map.put(_key, ld_oldValue + _newValue); //
		} else { //如果没值,则直接加入！
			_map.put(_key, _newValue); //
		}
	}

	private void appendMapData(HashMap _map, String _key, String _newValue) { //袁江晓添加  显示初始值  20130312
		/*之前的写法，主要将初始值像字符串一样拼接起来
		 * if (_map.containsKey(_key)) { //如果有值,则累加！
			String ld_oldValue = (String) _map.get(_key); //
			_map.put(_key, ld_oldValue + _newValue); //
		} else { //如果没值,则直接加入！
			_map.put(_key, _newValue); //
		}*/
		//下面为后来杨庆提出来的，即如果全为数字，则将数字的结果相加
		if (_map.containsKey(_key)) { //如果有值,则累加！
			String ld_oldValue = (String) _map.get(_key); //
			Pattern pattern = Pattern.compile("[0-9]");//判断是否为“数字”，是，转为double，进行相加，得出小计
			if (pattern.matcher(ld_oldValue).find() && pattern.matcher(_newValue).find()) {
				Double oldvalue = Double.parseDouble(ld_oldValue);
				Double newvalue = Double.parseDouble(_newValue);
				_map.put(_key, (oldvalue + newvalue) + ""); //
			} else {
				_map.put(_key, ld_oldValue + _newValue); //
			}

		} else { //如果没值,则直接加入！
			_map.put(_key, _newValue); //
		}
	}

	private void appendMapData2(HashMap _map, String _key, String _values) {
		if (_values == null || _values.equals("")) {
			return;
		}
		if (_map.containsKey(_key)) { //如果有值,则累加！
			String str_oldValue = (String) _map.get(_key); //
			if (str_oldValue.equals(";")) {
				_map.put(_key, str_oldValue + _values); //
			} else {
				_map.put(_key, str_oldValue + ";" + _values); //
			}
		} else { //如果没值,则直接加入！
			_map.put(_key, _values); //
		}
	}

	private JPanel getNorthBtnPanel() {
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
		btnPanel.setOpaque(false); //透明

		btn_xy = new WLTButton("X/Y轴转换", UIUtil.getImage("office_169.gif"));
		btn_exportHtml = new WLTButton("导出Html", UIUtil.getImage("office_192.gif")); //
		btn_exportExcel = new WLTButton("导出Excel", UIUtil.getImage("office_170.gif")); //
		btn_xy.addActionListener(this); //
		btn_exportHtml.addActionListener(this); //
		btn_exportExcel.addActionListener(this); //

		btnPanel.add(btn_xy); //
		btnPanel.add(btn_exportHtml); //
		btnPanel.add(btn_exportExcel); //
		return btnPanel; //
	}

	//右键弹出!!
	protected void popShowRightMenu(JComponent _component, Point _point) {
		if (rightPopMenu == null) { //如果还没创建,则第一次先创建!
			rightPopMenu = new JPopupMenu(); //

			menuItem_drillNextGroup = new JMenuItem("钻取新维度", UIUtil.getImage("office_059.gif")); //
			menuItem_drillDetail = new JMenuItem("钻取明细", UIUtil.getImage("office_070.gif")); //
			menuItem_filter = new JMenuItem("过  滤", UIUtil.getImage("office_115.gif")); //
			menuItem_barChart = new JMenuItem("转换成柱形图", UIUtil.getImage("office_153.gif")); //
			menuItem_lineChart = new JMenuItem("转换成曲线图", UIUtil.getImage("office_152.gif")); //
			menuItem_pieChart = new JMenuItem("转换成饼图", UIUtil.getImage("office_005.gif")); //
			menuItem_warnValue = new JMenuItem("警界值设置", UIUtil.getImage("office_053.gif")); //
			menuItem_viewInfo = new JMenuItem("数据详细信息", UIUtil.getImage("savedata.gif")); //
			menuItem_lock = new JMenuItem("冻结窗格", UIUtil.getImage("savedata.gif")); //
			menuItem_unlock = new JMenuItem("取消冻结窗格", UIUtil.getImage("savedata.gif")); //
			menuItem_drillDetail.setToolTipText("返回的HashVO[]中必须有id字段,然后定义弹出明细的列表模板编码,或反射类名称!"); //

			menuItem_drillNextGroup.setPreferredSize(new Dimension(105, 20)); //
			menuItem_drillDetail.setPreferredSize(new Dimension(105, 20)); //
			menuItem_filter.setPreferredSize(new Dimension(105, 20)); //
			menuItem_barChart.setPreferredSize(new Dimension(105, 20)); //
			menuItem_lineChart.setPreferredSize(new Dimension(105, 20)); //
			menuItem_pieChart.setPreferredSize(new Dimension(105, 20)); //
			menuItem_warnValue.setPreferredSize(new Dimension(105, 20)); //
			menuItem_viewInfo.setPreferredSize(new Dimension(105, 20)); //
			menuItem_lock.setPreferredSize(new Dimension(105, 20)); //
			menuItem_unlock.setPreferredSize(new Dimension(105, 20)); //

			menuItem_drillNextGroup.addActionListener(this); //
			menuItem_drillDetail.addActionListener(this); //
			menuItem_filter.addActionListener(this); //
			menuItem_barChart.addActionListener(this); //
			menuItem_lineChart.addActionListener(this); //
			menuItem_pieChart.addActionListener(this); //
			menuItem_warnValue.addActionListener(this); //
			menuItem_viewInfo.addActionListener(this); //
			menuItem_lock.addActionListener(this); //
			menuItem_unlock.addActionListener(this); //

			rightPopMenu.add(menuItem_drillNextGroup); //
			rightPopMenu.add(menuItem_drillDetail); //
			rightPopMenu.add(menuItem_filter); //
			rightPopMenu.add(menuItem_barChart); //
			rightPopMenu.add(menuItem_lineChart); //
			rightPopMenu.add(menuItem_pieChart); //
			rightPopMenu.add(menuItem_warnValue); //
			if (ClientEnvironment.isAdmin()) {
				rightPopMenu.add(menuItem_viewInfo); //只有是管理员登录方式才能看到这个!
			}
			rightPopMenu.add(menuItem_lock); //
			rightPopMenu.add(menuItem_unlock); //
		}
		if (cellPanel != null) {
			menuItem_lock.setVisible(!cellPanel.isLocked());
			menuItem_unlock.setVisible(cellPanel.isLocked());
		}
		menuItem_drillNextGroup.setVisible(false); //
		menuItem_drillDetail.setVisible(false); //
		menuItem_filter.setVisible(false); //
		menuItem_barChart.setVisible(false); //
		menuItem_lineChart.setVisible(false); //
		menuItem_pieChart.setVisible(false); //

		int li_selRow = cellPanel.getTable().getSelectedRow(); // 选中的行
		int li_selCol = cellPanel.getTable().getSelectedColumn(); // 选中的列
		if (li_selRow < 0 || li_selCol < 0) {
			return; //
		}
		boolean isEnbleMenuItem = true; //
		boolean isShowMenu = true; //
		BillCellItemVO cellItemVO = cellPanel.getBillCellItemVOAt(li_selRow, li_selCol); //
		if (cellItemVO.getCellvalue() == null || cellItemVO.getCellvalue().trim().equals("")) {
			isEnbleMenuItem = false; //
		}
		menuItem_drillNextGroup.setEnabled(isEnbleMenuItem); //
		menuItem_drillDetail.setEnabled(isEnbleMenuItem); //
		menuItem_barChart.setEnabled(isEnbleMenuItem); //
		menuItem_lineChart.setEnabled(isEnbleMenuItem); //
		menuItem_pieChart.setEnabled(isEnbleMenuItem); //

		String str_cellType = (String) cellItemVO.getCustProperty("itemtype"); //
		if ("Level".equals(str_cellType)) { //只有在行头组与列头组上才显示【过滤】菜单
			menuItem_drillNextGroup.setVisible(true); //
			menuItem_filter.setVisible(true); //
		} else if ("ComputeCell".equals(str_cellType) || "ComputeTotal".equals(str_cellType)) { //只有在计算格中才显示的菜单,即过滤不显示了!
			menuItem_drillNextGroup.setVisible(true); //
			menuItem_drillDetail.setVisible(true); //
			menuItem_barChart.setVisible(true); //
			menuItem_lineChart.setVisible(true); //
			menuItem_pieChart.setVisible(true); //
		} else {
			isShowMenu = false;
		}

		if (isShowMenu || ClientEnvironment.isAdmin()) {
			rightPopMenu.show(_component, _point.x, _point.y); //弹出菜单!!!
		}
	}

	//超链接点击!
	public void onBillCellHtmlHrefClicked(BillCellHtmlHrefEvent _event) {
		if (!_event.isCtrlDown() && !_event.isShiftDown() && !_event.isAltDown()) { //如果按住Ctrl键
			onDrillDetail(); //钻取明细!
		}
	}

	public void actionPerformed(ActionEvent _event) {
		if (_event.getSource() == btn_xy) { //X/Y转换
			onConvertXY(); //
		} else if (_event.getSource() == btn_exportHtml) { //导出Html
			cellPanel.exportHtml((this.getClientProperty("templetname") == null || "".equals(this.getClientProperty("templetname").toString().trim())) ? "报表数据导出" : (this.getClientProperty("templetname") + "")); //
		} else if (_event.getSource() == btn_exportExcel) { //导出Excel
			cellPanel.exportExcel((this.getClientProperty("templetname") == null || "".equals(this.getClientProperty("templetname").toString().trim())) ? "报表数据导出" : (this.getClientProperty("templetname") + ""), "BillReportPanel"); //20130911  袁江晓更改主要用于将风格报表的导出excel分开
		} else if (_event.getSource() == menuItem_drillNextGroup) { //如果是钻取下一维度
			onDrillNextGroup(null); //
		} else if (_event.getSource() == menuItem_drillDetail) { //如果是钻取明細
			onDrillDetail(); //
		} else if (_event.getSource() == menuItem_filter) { //过滤
			onFilterData(); //
		} else if (_event.getSource() == menuItem_barChart) { //转换成柱形图
			onConvertBarChart(); //
		} else if (_event.getSource() == menuItem_lineChart) { //转换成曲线图
			onConvertLineChart(); //
		} else if (_event.getSource() == menuItem_pieChart) { //饼图显示!
			onConvertPieChart(); //
		} else if (_event.getSource() == menuItem_warnValue) { //显示所有值
			onSetWarnValue(); //设置警界值!
		} else if (_event.getSource() == menuItem_viewInfo) { //显示所有值
			onViewInfo(); //
		} else if (_event.getSource() == menuItem_lock) { //显示所有值
			onLock(); //
		} else if (_event.getSource() == menuItem_unlock) { //显示所有值
			onUnLock(); //
		}
	}

	/**
	 * X/Y转轴!!
	 */
	private void onConvertXY() {
		isConvertXY = !isConvertXY; //设置一下!
		cellPanel.convertxy(false); //一转换后表格重新构建了,所以需要重新监听事件
		BillCellItemVO itemVO = cellPanel.getBillCellItemVOAt(0, 0);
		itemVO.setCellvalue(getTopLeftTitle()); //
		itemVO.setRowheight("40"); //
		cellPanel.getTable().addMouseListener(new MouseAdapter() { //增加自己的右键功能!
					@Override
					public void mouseClicked(MouseEvent e) {
						if (e.getButton() == MouseEvent.BUTTON3) {
							popShowRightMenu((JComponent) e.getSource(), e.getPoint()); //右键弹出显示菜单!
						}
					}
				}); //
		cellPanel.getTable().setToolTipText("提示:右键有【钻取】【过滤】等更多功能"); //
		cellPanel.setEditable(false);
		cellPanel.setCrystal(true); //透明!!!
	}

	//钻取明细
	public void onDrillDetail() {
		try {
			int li_selRow = cellPanel.getTable().getSelectedRow(); // 选中的行
			int li_selCol = cellPanel.getTable().getSelectedColumn(); // 选中的列
			if (li_selRow < 0 || li_selCol < 0) {
				MessageBox.show(this, "请选中数据才能进行钻取操作!"); //
				return; //
			}
			BillCellItemVO cellItemVO = cellPanel.getBillCellItemVOAt(li_selRow, li_selCol); //
			if (cellItemVO.getCellvalue() == null || cellItemVO.getCellvalue().trim().equals("")) { //
				return;
			}
			String str_cellType = (String) cellItemVO.getCustProperty("itemtype"); //
			if ("ComputeCell".equals(str_cellType)) { //如果是计算格,则直接进行钻取!
				String str_ids = (String) cellItemVO.getCustProperty("#value"); // 得到选择单元格真实ID数据
				new ReportUIUtil().onDrillDetail(this.cellPanel, str_ids, cellItemVO, this.str_builderClassName, this.queryConditionMap, this.allCanGroupFields, this.str_drillActionClass, this.str_drillTempletCode); //
			} else if ("Level".equals(str_cellType)) { //如果行头组还是列头组,则进行下一维度钻取!!
				if (this.drillgroupbindMap == null) {
					onDrillNextGroup(null); //
				} else {
					String str_grouptype = (String) cellItemVO.getCustProperty("levelname"); //
					if (drillgroupbindMap.containsKey(str_grouptype)) { //如果这个维度预先绑定了下一个维度,则在点击时直接钻取到下一维度
						onDrillNextGroup((String) drillgroupbindMap.get(str_grouptype)); //
					} else {
						onDrillNextGroup(null); //
					}
				}
			}
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		}
	}

	//取得钻取数据
	private void onDrillNextGroup(String _nextGroupName) {
		DrillDefineVO drillVO = getDrillDefine(_nextGroupName); //弹出钻取下一维度的窗口,选择继续个别取的维度!!
		if (drillVO == null) {
			return;
		}

		String[] str_newRowGroupFields = chooseGroupRowFields; //因为有可能钻取时在“列上”进行了“新的钻取”,这时“行维度”是不变的,所以先赋值为当前的行维度！！
		if (drillVO.getDrillRowGroupName() != null && drillVO.getDrillRowGroupName().length > 0) { //如果进行了新的行维度上的钻取!!
			str_newRowGroupFields = new String[chooseGroupRowFields.length + drillVO.getDrillRowGroupName().length]; //
			System.arraycopy(chooseGroupRowFields, 0, str_newRowGroupFields, 0, chooseGroupRowFields.length); //将把原来的拷贝进来
			System.arraycopy(drillVO.getDrillRowGroupName(), 0, str_newRowGroupFields, chooseGroupRowFields.length, drillVO.getDrillRowGroupName().length); //将把原来的拷贝进来
		}

		String[] str_newColGroupFields = chooseGroupColFields; //因为有可能钻取时在“行上”进行了“新的钻取”,这时“列维度”是不变的,所以先赋值为当前的列维度！！
		if (drillVO.getDrillColGroupName() != null && drillVO.getDrillColGroupName().length > 0) { //如果进行了新的列维度上的钻取!!
			str_newColGroupFields = new String[chooseGroupColFields.length + drillVO.getDrillColGroupName().length]; //
			System.arraycopy(chooseGroupColFields, 0, str_newColGroupFields, 0, chooseGroupColFields.length); //
			System.arraycopy(drillVO.getDrillColGroupName(), 0, str_newColGroupFields, chooseGroupColFields.length, drillVO.getDrillColGroupName().length); //将把原来的拷贝进来
		}

		ReportCellPanel temp_ReportCellPanel = null;
		if (drillVO.getDrillType() == DrillDefineVO.ROWGROUP || drillVO.getDrillType() == DrillDefineVO.COLGROUP) { //如果是在行头上分组
			String[][] str_newComputeFunandFields = getChooseComputeFunAndField();
			temp_ReportCellPanel = new ReportCellPanel(this.str_builderClassName, this.allCanGroupFields, this.allCanComputeFields, str_newRowGroupFields, str_newColGroupFields, str_newComputeFunandFields, null, null, null, isTotal, false, isRowGroupSubTotal, false, isColGroupSubTotal, isSortByCpValue, getQueryConditionMap(), drillVO.getDrillConditionMap(), groupfieldorder, zeroReportConfMap,
					dateGroupDefineMap, this.isCtrlDown); //
		} else if (drillVO.getDrillType() == DrillDefineVO.CELL) {
			String[][] str_newComputeFunandFields = drillVO.getComputeFunAndFields();
			temp_ReportCellPanel = new ReportCellPanel(this.str_builderClassName, this.allCanGroupFields, this.allCanComputeFields, str_newRowGroupFields, str_newColGroupFields, str_newComputeFunandFields, null, null, null, isTotal, false, isRowGroupSubTotal, false, isColGroupSubTotal, isSortByCpValue, getQueryConditionMap(), drillVO.getDrillConditionMap(), groupfieldorder, zeroReportConfMap,
					dateGroupDefineMap, this.isCtrlDown); //
		}
		temp_ReportCellPanel.setStr_drillActionClass(this.str_drillActionClass); //
		temp_ReportCellPanel.setStr_drillTempletCode(this.str_drillTempletCode); //
		temp_ReportCellPanel.setDrillgroupbindMap(this.drillgroupbindMap); //设置钻取绑定的维度

		Window frame = BillDialog.getWindowForComponent(this); //
		BillDialog dialog = new BillDialog(this, "钻取新维度", 650, 550, (int) frame.getLocation().getX() + 150, (int) frame.getLocation().getY() + 120); //故意错移位!!

		JPanel conttentPanel = WLTPanel.createDefaultPanel(new BorderLayout()); //
		conttentPanel.add(temp_ReportCellPanel); //
		dialog.getContentPane().add(conttentPanel); //
		dialog.addConfirmButtonPanel(2); //
		dialog.setVisible(true); //
	}

	/**
	 * 取得下一个想要继续钻取的维度!!!
	 * 选中计算格也可以钻取,选中行头/列头也是可以钻取的！！
	 * 选中行头与列头等价于选中对应的行与列上的所有格子的值！！
	 */
	public DrillDefineVO getDrillDefine(String _groupName) {
		if (cellPanel == null) {
			MessageBox.show(this, "没有数据,不能进行此操作!"); //
			return null;
		}

		int li_selRow = cellPanel.getTable().getSelectedRow(); // 选中的行
		int li_selCol = cellPanel.getTable().getSelectedColumn(); // 选中的列
		if (li_selRow < 0 || li_selCol < 0) {
			MessageBox.show(this, "请选中数据才能进行钻取操作!"); //
			return null; //
		}

		int[] li_selrows = cellPanel.getTable().getSelectedRows(); //
		int[] li_selcols = cellPanel.getTable().getSelectedColumns(); //

		BillCellItemVO cellItemVO = cellPanel.getBillCellItemVOAt(li_selRow, li_selCol); //
		String str_cellrealtype = (String) cellItemVO.getCustProperty("itemtype"); // 判断是行还是列
		String str_rowColType = (String) cellItemVO.getCustProperty("rowcoltype"); //行列类型！
		if (!(str_cellrealtype != null && (str_cellrealtype.equals("Level") || str_cellrealtype.equals("ComputeCell")))) {
			MessageBox.show(this, "选项中的内容类型不对,只能选中[上表头],[左表头],[计算格]三种类型才能进行钻取操作!"); //
			return null; //
		}

		if (str_cellrealtype.equals("ComputeCell")) { // 如果选中的是计算格
			ArrayList al_drillNextItemVOs = new ArrayList(); //
			String str_firstcellLevel = null; //
			for (int i = 0; i < li_selrows.length; i++) { // 遍历行
				for (int j = 0; j < li_selcols.length; j++) { // 遍历列
					BillCellItemVO cellItemVO_item = cellPanel.getBillCellItemVOAt(li_selrows[i], li_selcols[j]); //
					String str_cellrealtype_item = (String) cellItemVO_item.getCustProperty("itemtype"); // 判断是行还是列
					if ("ComputeCell".equals(str_cellrealtype_item)) {
						if (str_firstcellLevel == null) {
							str_firstcellLevel = getCellLevelName(cellItemVO_item); //
							al_drillNextItemVOs.add(cellItemVO_item); //
						} else { //
							String str_thiscellLevel = getCellLevelName(cellItemVO_item); //
							if (str_thiscellLevel.equals(str_firstcellLevel)) { //因为后来存在平铺的情况,所以如果选择多个平铺范围中的数据时，是无法钻取的！所以必须是同一个维度类型的才处理！
								al_drillNextItemVOs.add(cellItemVO_item); //
							}
						}
					}
				}
			}

			Object[] drillConsObjs = getDrillNextGroupCons((BillCellItemVO[]) al_drillNextItemVOs.toArray(new BillCellItemVO[0])); //
			HashMap[] prefixGroupTypeValues = (HashMap[]) drillConsObjs[0]; //
			String[][] str_newComputeFunandFields = (String[][]) drillConsObjs[1]; //

			//弹出对话框,选择一个维度!!
			if (drillNextGroupDialog == null) {
				drillNextGroupDialog = new MultiLevelChooseDrillDialog(this, getRemainGroupFields()); //弹出钻取对话框!!
			}
			drillNextGroupDialog.setVisible(true); //
			if (drillNextGroupDialog.getCloseType() == 1) {
				DrillDefineVO drillVO = new DrillDefineVO();
				drillVO.setDrillType(DrillDefineVO.CELL); //
				drillVO.setDrillRowGroupName(drillNextGroupDialog.getRowGroupnames()); // 新的要钻取的组
				drillVO.setDrillColGroupName(drillNextGroupDialog.getColGroupnames()); // 新的要钻取的组
				drillVO.setDrillConditionMap(prefixGroupTypeValues); //
				drillVO.setComputeFunAndFields(str_newComputeFunandFields); //
				return drillVO;
			} else {
				return null;
			}
		} else { //如果选中的是行头或列头组!!!
			if (li_selrows.length > 1 && li_selcols.length > 1) {
				MessageBox.show(this, "只能对同一行或同一列的数据进行选择钻取!!"); //
				return null; //
			}

			ArrayList al_drillNextItemVOs = new ArrayList(); //
			if (str_rowColType.equals("Row")) { //如果选中的是行头组
				for (int i = 0; i < li_selcols.length; i++) { //遍历列
					for (int j = li_selRow + 1; j < cellPanel.getRowCount(); j++) { //遍历所有行,相当于把选中列上的所有行的“计算格”都拉进来计算!
						BillCellItemVO preFixCellItemVO = cellPanel.getBillCellItemVOAt(j, li_selcols[i]); // 找到前面的
						if ("ComputeCell".equals(preFixCellItemVO.getCustProperty("itemtype"))) { //必须是计算格才参与计算!!
							al_drillNextItemVOs.add(preFixCellItemVO); //
						}
					}
				}
			} else if (str_rowColType.equals("Col")) { // 如果选中的是行头组
				for (int i = 0; i < li_selrows.length; i++) { // 遍历各行
					for (int j = li_selCol + 1; j < cellPanel.getColumnCount(); j++) { //遍历所有列,相当于把选中行上的所有列的“计算格”都拉进来计算!
						BillCellItemVO preFixCellItemVO = cellPanel.getBillCellItemVOAt(li_selrows[i], j); //找到前面的各维度
						if ("ComputeCell".equals(preFixCellItemVO.getCustProperty("itemtype"))) { //必须是计算格才参与计算!!
							al_drillNextItemVOs.add(preFixCellItemVO); //
						}
					}
				}
			}

			Object[] drillConsObjs = getDrillNextGroupCons((BillCellItemVO[]) al_drillNextItemVOs.toArray(new BillCellItemVO[0])); //
			HashMap[] prefixGroupTypeValues = (HashMap[]) drillConsObjs[0]; //
			String[][] str_newComputeFunandFields = (String[][]) drillConsObjs[1]; //

			//弹出对话框,选择一个维度!!
			if (_groupName == null) { //
				String[] str_remainGroups = getRemainGroupFields(); //
				if (null == str_remainGroups) { //袁江晓20121109添加，主要针对老的报表出现的问题
					return null;
				}
				MultiLevelChooseDrillDialog dialog = new MultiLevelChooseDrillDialog(this, str_remainGroups);
				dialog.setVisible(true); //
				if (dialog.getCloseType() == 1) {
					DrillDefineVO drillVO = new DrillDefineVO();
					drillVO.setDrillRowGroupName(dialog.getRowGroupnames()); //新的要钻取的组
					drillVO.setDrillColGroupName(dialog.getColGroupnames()); //新的要钻取的组
					drillVO.setDrillConditionMap(prefixGroupTypeValues); //
					drillVO.setComputeFunAndFields(str_newComputeFunandFields); //
					if ("Row".equals(str_rowColType)) {
						drillVO.setDrillType(DrillDefineVO.ROWGROUP); //
					} else if ("Col".equals(str_rowColType)) {
						drillVO.setDrillType(DrillDefineVO.COLGROUP); //
					}
					return drillVO;
				} else {
					return null;
				}
			} else { //如果直接定义了想钻取的组名!
				DrillDefineVO drillVO = new DrillDefineVO();
				if ("Row".equals(str_rowColType)) {
					drillVO.setDrillRowGroupName(new String[] { _groupName }); //新的要钻取的组
					drillVO.setDrillColGroupName(null); //新的要钻取的组
					drillVO.setDrillConditionMap(prefixGroupTypeValues); //
					drillVO.setComputeFunAndFields(str_newComputeFunandFields); //
					drillVO.setDrillType(DrillDefineVO.ROWGROUP); //
				} else if ("Col".equals(str_rowColType)) {
					drillVO.setDrillRowGroupName(null); //新的要钻取的组
					drillVO.setDrillColGroupName(new String[] { _groupName }); //新的要钻取的组
					drillVO.setDrillConditionMap(prefixGroupTypeValues); //
					drillVO.setComputeFunAndFields(str_newComputeFunandFields); //
					drillVO.setDrillType(DrillDefineVO.COLGROUP); //
				}
				return drillVO;
			}
		}
	}

	private String getCellLevelName(BillCellItemVO cellItemVO_item) {
		StringBuilder sb_text = new StringBuilder(); //
		String str_rowLevel = (String) cellItemVO_item.getCustProperty("RowHeaderLevelNameValue"); //
		if (!str_rowLevel.equals("")) {
			String[] str_keys = (String[]) TBUtil.getTBUtil().convertStrToMapByExpress(str_rowLevel, "◆", "=").keySet().toArray(new String[0]); //
			for (int i = 0; i < str_keys.length; i++) {
				sb_text.append(str_keys[i] + "◆"); //
			}
		}

		String str_colLevel = (String) cellItemVO_item.getCustProperty("ColHeaderLevelNameValue"); //
		if (!str_colLevel.equals("")) {
			String[] str_keys = (String[]) TBUtil.getTBUtil().convertStrToMapByExpress(str_colLevel, "▲", "=").keySet().toArray(new String[0]); //
			for (int i = 0; i < str_keys.length; i++) {
				sb_text.append(str_keys[i] + "▲"); //
			}
		}
		return sb_text.toString();
	}

	//将一组格子中的数据的各维度的值进行计算!!用于钻取用!
	private Object[] getDrillNextGroupCons(BillCellItemVO[] _itemVOs) {
		ArrayList al_prefixGroupTypeValues = new ArrayList(); //
		ArrayList al_computeFunAndFields = new ArrayList(); //
		for (int i = 0; i < _itemVOs.length; i++) {
			String str_cpField = (String) _itemVOs[i].getCustProperty("RealCPField"); //
			String str_cpType = (String) _itemVOs[i].getCustProperty("RealCPType"); //
			String[] str_computeFunAndField = new String[] { str_cpType, "", str_cpField }; //
			if (!containComputeFunandField(al_computeFunAndFields, str_computeFunAndField)) { // 如果不包含,则加入
				al_computeFunAndFields.add(str_computeFunAndField); //
			}

			HashMap map_prefixGroupTypeValues = new HashMap(); //
			String str_RowHeaderLevelNameValue = (String) _itemVOs[i].getCustProperty("RowHeaderLevelNameValue"); //行头！
			if (!str_RowHeaderLevelNameValue.equals("")) {
				HashMap map = TBUtil.getTBUtil().convertStrToMapByExpress(str_RowHeaderLevelNameValue, "◆", "="); //
				String[] str_keys = (String[]) map.keySet().toArray(new String[0]); //
				for (int k = 0; k < str_keys.length; k++) {
					map_prefixGroupTypeValues.put(str_keys[k], map.get(str_keys[k])); //记录下这个格子中的值在各个维度上的实际维度值!!
				}
			}

			String str_ColHeaderLevelNameValue = (String) _itemVOs[i].getCustProperty("ColHeaderLevelNameValue"); //列头！
			if (!str_ColHeaderLevelNameValue.equals("")) {
				HashMap map = TBUtil.getTBUtil().convertStrToMapByExpress(str_ColHeaderLevelNameValue, "▲", "="); //
				String[] str_keys = (String[]) map.keySet().toArray(new String[0]); //
				for (int k = 0; k < str_keys.length; k++) {
					map_prefixGroupTypeValues.put(str_keys[k], map.get(str_keys[k])); //记录下这个格子中的值在各个维度上的实际维度值!!
				}
			}

			al_prefixGroupTypeValues.add(map_prefixGroupTypeValues); //
		}

		HashMap[] prefixGroupTypeValues = (HashMap[]) al_prefixGroupTypeValues.toArray(new HashMap[0]); //
		String[][] str_newComputeFunandFields = getComputeFunAndFields(al_computeFunAndFields); //

		return new Object[] { prefixGroupTypeValues, str_newComputeFunandFields }; //
	}

	/**
	 * 过滤当前的数据!!! 即有时数据太多,只想看其中一部分!!! 可以选择任意一个组,进行过滤,只能同时选择一个组
	 * 过滤时,是不进行后台取数的!直接在前台做的!!!
	 */
	public void onFilterData() {
		if (cellPanel == null) {
			MessageBox.show(this, "没有数据,不能进行此操作!"); //
			return;
		}

		int li_selRow = cellPanel.getTable().getSelectedRow(); // 选中的行
		int li_selCol = cellPanel.getTable().getSelectedColumn(); // 选中的列
		if (li_selRow < 0 || li_selCol < 0) {
			MessageBox.show(this, "请选中数据才能进行过滤操作!"); // 提示错误【李春娟/2012-03-15】
			return; //
		}

		BillCellItemVO cellItemVO = cellPanel.getBillCellItemVOAt(li_selRow, li_selCol); //
		String str_celltype = (String) cellItemVO.getCustProperty("itemtype"); //
		if (!"Level".equals(str_celltype)) { //必须是组格式!
			MessageBox.show(this, "选中的数据格式不对,请选择行分组或列分组!"); //
			return; //
		}

		String str_grouptype = (String) cellItemVO.getCustProperty("levelname"); // 什么组
		if (hvs_data_beforefilter == null) {
			hvs_data_beforefilter = new HashVO[hvs_data.length]; //
			for (int i = 0; i < hvs_data.length; i++) {
				hvs_data_beforefilter[i] = hvs_data[i].deepClone(); // 先克隆一份
			}
		}

		if (filterConditionMap == null) {
			filterConditionMap = new HashMap(); //
		}

		String[][] str_filterData = null; //就是计算出某一个维度的实际的唯一值！！
		if (filterConditionMap.containsKey(str_grouptype)) {
			str_filterData = (String[][]) filterConditionMap.get(str_grouptype); //
		} else {
			LinkedHashSet hst_distinct = new LinkedHashSet(); //
			for (int i = 0; i < hvs_data_beforefilter.length; i++) { //现在这里是从实际数据中过滤的!
				hst_distinct.add(hvs_data_beforefilter[i].getStringValue(str_grouptype, "【空值】", true)); //
			}
			String[] str_filterValues = (String[]) hst_distinct.toArray(new String[0]); //
			TBUtil.getTBUtil().sortStrs(str_filterValues); //
			str_filterData = new String[str_filterValues.length][2]; //
			for (int i = 0; i < str_filterValues.length; i++) { //
				str_filterData[i][0] = str_filterValues[i];
				str_filterData[i][1] = "0"; //
			}
		}

		MultiLevelChooseFilterDialog dialog = new MultiLevelChooseFilterDialog(this, "选择一个或多个 [" + str_grouptype + "] 进行过滤", str_filterData); //
		dialog.setVisible(true); // 显示
		if (dialog.getCloseType() != 1) { //如果不是确定返回的,则直接退出！！
			return;
		}

		filterConditionMap.put(str_grouptype, str_filterData); //先加入！！！
		String[][] str_choosefilter = dialog.getReturnFilterField(); // 返回
		filterConditionMap.put(str_grouptype, str_choosefilter); // 立即送入!!!

		//过滤所有条件,即将其他的组的过滤也要算上!!!
		String[] str_allFilterGroupTypes = (String[]) filterConditionMap.keySet().toArray(new String[0]); //
		HashMap[] allFilterGroupMap = new HashMap[str_allFilterGroupTypes.length];
		for (int i = 0; i < str_allFilterGroupTypes.length; i++) {
			allFilterGroupMap[i] = new HashMap(); //
			String[][] str_chooseFilterDetail = (String[][]) filterConditionMap.get(str_allFilterGroupTypes[i]); //
			for (int j = 0; j < str_chooseFilterDetail.length; j++) {
				if (str_chooseFilterDetail[j][1].equals("1")) {
					allFilterGroupMap[i].put(str_chooseFilterDetail[j][0], null); //
				}
			}
		}

		ArrayList al_filtered = new ArrayList(); //
		String str_a1; //
		for (int i = 0; i < hvs_data_beforefilter.length; i++) {
			boolean ismatch = true;
			for (int j = 0; j < allFilterGroupMap.length; j++) { //
				str_a1 = hvs_data_beforefilter[i].getStringValue(str_allFilterGroupTypes[j], "【空值】", true); //这里要处理空值！！！
				if (!allFilterGroupMap[j].containsKey(str_a1)) { // 如果我这一关就过不了,就立即退出
					ismatch = false;
					break;
				}
			}

			if (ismatch) {
				al_filtered.add(hvs_data_beforefilter[i]); //
			}
		}

		this.hvs_data = (HashVO[]) al_filtered.toArray(new HashVO[0]); //
		buildUI2(); // 重新生成页面
	}

	//将选中的格子转换成柱形图显示!
	private void onConvertBarChart() {
		BillChartVO chartVO = getChartVO(); //
		if (chartVO == null) {
			return; //
		}

		org.jfree.chart.JFreeChart chart = BillChartPanel.getInstance().createBarChart("柱形图", chartVO.getXHeadName(), chartVO.getYHeadName(), chartVO.getXSerial(), chartVO.getYSerial(), getChartData(chartVO.getDataVO()), true, null); //
		org.jfree.chart.ChartPanel chartPanel = new org.jfree.chart.ChartPanel(chart); //
		BillDialog dialog = new BillDialog(this, "构建柱形图", 900, 600); //
		dialog.getContentPane().add(chartPanel); //
		dialog.addConfirmButtonPanel(2); //
		dialog.setVisible(true); //
	}

	//将选中的格子转换成曲线图显示!
	private void onConvertLineChart() {
		BillChartVO chartVO = getChartVO(); //
		if (chartVO == null) {
			return; //
		}
		org.jfree.chart.JFreeChart chart = BillChartPanel.getInstance().createLineChart("曲线图", chartVO.getXHeadName(), chartVO.getYHeadName(), chartVO.getXSerial(), chartVO.getYSerial(), getChartData(chartVO.getDataVO()), false, null); //jfreechart-1.0.14升级追加曲线图3维效果 【杨科/2013-06-13】
		org.jfree.chart.ChartPanel chartPanel = new org.jfree.chart.ChartPanel(chart); //
		BillDialog dialog = new BillDialog(this, "构建曲线图", 900, 600); //
		dialog.getContentPane().add(chartPanel); //
		dialog.addConfirmButtonPanel(2); //
		dialog.setVisible(true); //
	}

	//将选中的格子转换成饼图显示!
	private void onConvertPieChart() {
		BillChartVO chartVO = getChartVO(); //
		if (chartVO == null) {
			return; //
		}
		BillChartItemVO[][] chartItemVOs = chartVO.getDataVO(); //
		String[] str_serial = new String[chartVO.getXSerial().length * chartVO.getYSerial().length]; //
		double[] ld_values = new double[chartVO.getXSerial().length * chartVO.getYSerial().length]; //
		for (int i = 0; i < chartVO.getXSerial().length; i++) {
			for (int j = 0; j < chartVO.getYSerial().length; j++) {
				int li_pos = i * chartVO.getYSerial().length + j; //
				String str_groupName = chartVO.getXSerial()[i] + "-" + chartVO.getYSerial()[j]; //
				str_serial[li_pos] = str_groupName; //
				ld_values[li_pos] = (chartItemVOs[i][j] == null ? 0 : chartItemVOs[i][j].getValue()); //
			}
		}
		org.jfree.chart.JFreeChart chart = BillChartPanel.getInstance().createPieChart("饼形图", str_serial, ld_values, 2, null); //
		org.jfree.chart.ChartPanel chartPanel = new org.jfree.chart.ChartPanel(chart); //
		BillDialog dialog = new BillDialog(this, "构建饼形图", 900, 600); //
		dialog.getContentPane().add(chartPanel); //
		dialog.addConfirmButtonPanel(2); //
		dialog.setVisible(true); //
	}

	private double[][] getChartData(BillChartItemVO[][] _vos) {
		double[][] ld_value = new double[_vos.length][_vos[0].length]; //
		for (int i = 0; i < ld_value.length; i++) {
			for (int j = 0; j < ld_value[0].length; j++) {
				Double ld_itemvalue = (_vos[i][j] == null ? 0 : _vos[i][j].getValue()); //
				if (ld_itemvalue == null) {
					ld_itemvalue = 0d; //
				}
				ld_value[i][j] = ld_itemvalue; //
			}
		}
		return ld_value; //
	}

	//计算出选中格子的数据!
	private BillChartVO getChartVO() {
		int[] li_rows = cellPanel.getTable().getSelectedRows(); //选中的所有行?
		int[] li_cols = cellPanel.getTable().getSelectedColumns(); //选中的所有列?

		String[] str_xSerial = new String[li_rows.length]; //
		String[] str_ySerial = new String[li_cols.length]; //
		BillChartItemVO[][] chartItemVOs = new BillChartItemVO[li_rows.length][li_cols.length]; //

		String str_cellValue = null; //
		for (int i = 0; i < li_rows.length; i++) { //遍历所有选中的行!
			for (int j = 0; j < li_cols.length; j++) { //遍历所有选中的列!
				BillCellItemVO cellItemVO = cellPanel.getBillCellItemVOAt(li_rows[i], li_cols[j]); //
				String str_realType = (String) cellItemVO.getCustProperty("itemtype"); //
				if (!("ComputeCell".equals(str_realType) || "ComputeTotal".equals(str_realType))) { //
					MessageBox.show(this, "只能选中计算格进行转换!\r\n请注意你是否多余选中了合计列?"); //
					return null; //
				}

				if (i == 0) {
					String str_rowLevel = (String) cellItemVO.getCustProperty(isConvertXY ? "ColHeaderLevelNameValue" : "RowHeaderLevelNameValue"); //
					str_ySerial[j] = getSplitValue(str_rowLevel, isConvertXY ? "▲" : "◆"); //
					if (chooseGroupRowFields.length == 0 || (chooseComputeFunAndField.length > 1 && chooseGroupColFields.length > 0)) { //如果行维度为空,或者有多个计算列时,存在
						str_ySerial[j] = str_ySerial[j] + "<" + (String) cellItemVO.getCustProperty("RealCPField") + ">"; //
					}
				}

				if (j == 0) { //如果是第一列!
					String str_colLevel = (String) cellItemVO.getCustProperty(isConvertXY ? "RowHeaderLevelNameValue" : "ColHeaderLevelNameValue"); //
					str_xSerial[i] = getSplitValue(str_colLevel, isConvertXY ? "◆" : "▲"); //
					if (chooseGroupColFields.length == 0) {
						str_xSerial[i] = str_xSerial[i] + "<" + (String) cellItemVO.getCustProperty("RealCPField") + ">"; //
					}
				}
				str_cellValue = cellItemVO.getCellvalue(); //
				if (str_cellValue != null && !str_cellValue.trim().equals("")) {
					if (str_cellValue.endsWith("%")) {
						str_cellValue = str_cellValue.substring(0, str_cellValue.length() - 1); //
					}
					chartItemVOs[i][j] = new BillChartItemVO(Double.parseDouble(str_cellValue)); //构造对象VO
				}
			}
		}

		BillChartVO chartVO = new BillChartVO(); //
		chartVO.setTitle("图表"); //
		chartVO.setXHeadName(chooseGroupRowFields.length > 0 ? chooseGroupRowFields[0] : chooseGroupColFields[0]); //this.chooseGroupRowFields[0]
		chartVO.setYHeadName(this.chooseComputeFunAndField[0][2]); //
		chartVO.setXSerial(str_xSerial); //
		chartVO.setYSerial(str_ySerial); //
		chartVO.setDataVO(chartItemVOs); //设置数据!!
		return chartVO; //
	}

	private String getSplitValue(String _text, String _split) {
		if (_text.equals("")) {
			return "";
		}
		HashMap map = TBUtil.getTBUtil().convertStrToMapByExpress(_text, _split, "="); //
		Iterator its = map.values().iterator(); //
		StringBuilder sb_text = new StringBuilder(); //
		while (its.hasNext()) {
			sb_text.append("" + its.next() + "_"); //
		}
		String str_return = sb_text.toString(); //
		str_return = str_return.substring(0, str_return.length() - 1); //
		return str_return; //
	}

	//设置警界值!!!
	private void onSetWarnValue() {
		JPanel panel = WLTPanel.createDefaultPanel(null); //
		JLabel label = new JLabel("输入一个警界数,返回后就会将超过这个警界值的格子红色显示!");
		JComboBox comBox = new JComboBox(); //
		comBox.addItem("大于"); //
		comBox.addItem("小于"); //
		comBox.setFocusable(false); //

		JFormattedTextField textField = new JFormattedTextField("100"); //格式化的数字框
		textField.setHorizontalAlignment(SwingConstants.RIGHT); //
		textField.setDocument(new NumberFormatdocument()); //定义数字框只能输入数字,输入字母不让键入!!!!

		label.setBounds(25, 15, 350, 20); //
		comBox.setBounds(100, 40, 100, 20); //
		textField.setBounds(205, 40, 100, 20); //

		panel.add(label); //
		panel.add(comBox); //
		panel.add(textField); //

		if (!MessageBox.confirm(this, panel, 400, 150)) { //
			return;
		}

		String str_text = textField.getText(); //
		if (str_text.equals("")) {
			MessageBox.show(this, "请输入一个数值!"); //
			return; //
		}
		double ld_warnValue = Double.parseDouble(str_text); ////
		int li_rows = cellPanel.getTable().getRowCount(); ////
		int li_cols = cellPanel.getTable().getColumnCount(); ////
		for (int i = 0; i < li_rows; i++) {
			for (int j = 0; j < li_cols; j++) {
				BillCellItemVO itemVO = cellPanel.getBillCellItemVOAt(i, j); //
				if (itemVO != null && itemVO.getCellvalue() != null) {
					String str_realType = (String) itemVO.getCustProperty("itemtype"); //
					if ("ComputeCell".equals(str_realType)) { //
						double ld_value = Double.parseDouble(itemVO.getCellvalue()); //
						boolean isWarn = false; //
						if (comBox.getSelectedIndex() == 0) {
							isWarn = (ld_value > ld_warnValue); //
						} else {
							isWarn = (ld_value < ld_warnValue); //
						}
						if (isWarn) {
							itemVO.setBackground("255,0,0"); //
							itemVO.setForeground("255,255,255"); //
						} else {
							itemVO.setBackground("255,255,255"); //
							itemVO.setForeground("0,0,0"); //
						}
					}
				}
			}
		}
		cellPanel.getTable().clearSelection(); //
	}

	/**
	 * 显示数据内容!!
	 */
	private void onViewInfo() {
		int li_result = MessageBox.showOptionDialog(this, "请选择想要查看的信息类型？！", "提示", new String[] { "显示本格信息", "显示两颗维度树信息", "取消" }, 450, 150); //
		if (li_result == 0) {
			cellPanel.showCellAllProp(); //直接显示所有值
		} else if (li_result == 1) { //显示维度树内容!!
			onViewInfo2(); //
		}
	}

	private void onLock() {
		cellPanel.onLock();
	}

	private void onUnLock() {
		cellPanel.onUnLock();
	}

	private void onViewInfo2() {
		JTree tree_rowHeader = new JTree(rowHeaderRootNode); //
		JTree tree_colHeader = new JTree(colHeaderRootNode); //

		JTabbedPane tabbed = new JTabbedPane(); //
		tabbed.addTab("行头维度", new JScrollPane(tree_rowHeader)); //
		tabbed.addTab("列头维度", new JScrollPane(tree_colHeader)); //

		//数据！！！
		HashVOStruct hvst = new HashVOStruct(); //
		hvst.setHeaderName(this.hvs_data[0].getKeys()); //
		hvst.setHashVOs(this.hvs_data); //
		BillListPanel billList = new BillListPanel(hvst); //
		tabbed.addTab("返回前台的汇总数据", billList); //

		//哈希表！！！
		String[] str_keys = (String[]) beforeBuilderDataMap.keySet().toArray(new String[0]); //
		HashVO[] hvs_mapData = new HashVO[str_keys.length]; //
		for (int i = 0; i < str_keys.length; i++) { //
			hvs_mapData[i] = new HashVO(); //
			hvs_mapData[i].setAttributeValue("Key", str_keys[i]); //
			hvs_mapData[i].setAttributeValue("Value", beforeBuilderDataMap.get(str_keys[i])); //
		}
		HashVOStruct hvst_map = new HashVOStruct(); //
		hvst_map.setHeaderName(new String[] { "Key", "Value" }); //
		hvst_map.setHashVOs(hvs_mapData); //
		BillListPanel billList_map = new BillListPanel(hvst_map); //
		tabbed.addTab("汇总数据转换成Map", billList_map); //

		BillDialog dialog = new BillDialog(this, "显示所有数据", 500, 500);
		dialog.getContentPane().add(tabbed); //
		dialog.addConfirmButtonPanel(); //
		dialog.setVisible(true);
	}

	/**
	 * 指定某个格子向后,向下合拼几列几行! 除了在开始合并格中设置外,还需在向下,向右的合并格中设置负数的样子!
	 * 
	 * @param itemVOS
	 * @param _beginX
	 * @param _beginY
	 * @param _spanDown
	 *            向下并几格
	 * @param _spanRight
	 *            向右并几格
	 */
	private void span(BillCellItemVO[][] itemVOS, int _beginX, int _beginY, int _spanDown, int _spanRight, String[][] _custsype) {
		itemVOS[_beginX][_beginY].setSpan("" + _spanDown + "," + _spanRight + ""); // cellrealtype
		for (int k = 0; k < _custsype.length; k++) {
			itemVOS[_beginX][_beginY].setCustProperty(_custsype[k][0], _custsype[k][1]);
		}

		for (int i = 0; i < _spanDown; i++) { // 向下几个,如果向下是1,则只做一次循环
			for (int j = 0; j < _spanRight; j++) { // 向右几个
				if (i == 0 && j == 0) {
				} else {
					itemVOS[_beginX + i][_beginY + j].setSpan("" + (0 - j) + "," + (0 - i)); // 指定该行该列比开始合并的行与列少几行几列!!!
					for (int k = 0; k < _custsype.length; k++) {
						itemVOS[_beginX + i][_beginY + j].setCustProperty(_custsype[k][0], _custsype[k][1]);
					}
				}
			}
		}
	}

	private String getRowHeaderColor(int _row) {
		if (_row >= headBgColors_row.length) {
			return "231,231,232";
		} else {
			return headBgColors_row[_row]; //
		}

	}

	private String getColHeaderColor(int _col) {
		if (_col >= headBgColors_col.length) {
			return "231,231,232";
		} else {
			return headBgColors_col[_col]; //
		}
	}

	//合计列的颜色！！！
	private String getTotalColor(int _depth) {
		if (_depth >= totalBgColors.length) {
			return "255,255,142";
		} else {
			return totalBgColors[_depth]; //
		}
	}

	//获得警界值的背景颜色！！
	private String getWarnColor(int _len) {
		if (_len >= warnBgColors.length) {
			return warnBgColors[warnBgColors.length - 1];
		} else {
			return warnBgColors[_len]; //
		}
	}

	private String getComputeHeaderColor(int _index) {
		int index = 0; //
		if (_index < computeBgColors.length) {
			index = _index; //
		}
		return computeBgColors[index]; // 
	}

	/**
	 * 维度上的值是一个树型结构!
	 * 第二种算总行总列的方法!
	 * _orderConsMap 排序字段定义
	 */
	private DefaultMutableTreeNode getGroupHeaderNode(String[] _chooseGroupFields, boolean _isTiled, boolean _isSubTotal, boolean _isCanTotal, boolean _isRowHeader, boolean _isOnlyThisGroup, String[][] _computeFields, HashMap _orderConsMap) {
		boolean isDoZeroAppend = true;//this.tbutil.getSysOptionBooleanValue("报表是否进行零汇报处理", true); //默认是做的,但有时有急时，需要强行去掉补零机制!  20120911袁江晓修改    目前有的字段需要补零，所以加上了零汇报机制
		HashVO hvo_root = new HashVO(); //
		hvo_root.setAttributeValue("ItemType", "ROOT"); //
		hvo_root.setAttributeValue("ItemValue", "ROOT"); //
		hvo_root.setToStringFieldName("ItemValue"); //
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(hvo_root); //根结点
		HashMap[] extConfMaps = new HashMap[_computeFields.length]; //
		for (int i = 0; i < _computeFields.length; i++) { //遍历各个计算列
			if (_computeFields[i][1] != null && !_computeFields[i][1].trim().equals("")) { //如果不为空!!
				extConfMaps[i] = tbutil.convertStrToMapByExpress(_computeFields[i][1], ";", "="); //
			}
		}
		if (_chooseGroupFields.length == 0) { //如果维度为空,则自动将计算列长期过来！
			for (int r = 0; r < _computeFields.length; r++) { //最后一层根据计算列进行分裂！！！
				HashVO hvo_splitCp = new HashVO(); //计算列分裂
				hvo_splitCp.setAttributeValue("ItemType", "SplitCP"); //
				hvo_splitCp.setAttributeValue("RowColType", _isRowHeader ? "Row" : "Col"); //
				hvo_splitCp.setAttributeValue("CpType", _computeFields[r][0]); //计算的类型,比如count,sum
				hvo_splitCp.setAttributeValue("CpConfig", _computeFields[r][1]); //其他配置信息
				hvo_splitCp.setAttributeValue("CpField", _computeFields[r][2]); //计算的字段名称
				hvo_splitCp.setAttributeValue("CpIndexNo", "" + (r + 1)); //计算列的顺序编号
				if (extConfMaps[r] != null && extConfMaps[r].containsKey("显示名称")) { //如果强行指定了
					hvo_splitCp.setAttributeValue("ViewText", extConfMaps[r].get("显示名称")); // + "(" + _computeFields[r][0] + ")"
				} else {
					hvo_splitCp.setAttributeValue("ViewText", _computeFields[r][2]); // + "(" + _computeFields[r][0] + ")"
				}
				hvo_splitCp.setToStringFieldName("ViewText"); ////
				DefaultMutableTreeNode computeNode = new DefaultMutableTreeNode(hvo_splitCp); //
				rootNode.add(computeNode); //加入计算列结点！！
			}
		} else {
			if (_isTiled) { //如果是平铺的,则逻辑根本不一样,即平铺时永远只有两层！！第一层是维度的名称,第二层是维度的值!
				for (int i = 0; i < _chooseGroupFields.length; i++) { //遍历维度值！！
					HashVO hvo_tiled = new HashVO(); //平铺的VO
					hvo_tiled.setAttributeValue("ItemType", "Tiled"); //平铺的类型
					hvo_tiled.setAttributeValue("RowColType", _isRowHeader ? "Row" : "Col"); //
					hvo_tiled.setAttributeValue("LevelName", _chooseGroupFields[i]); //维护名称
					hvo_tiled.setToStringFieldName("LevelName"); //显示的值就是维底名称
					DefaultMutableTreeNode itemNode1 = new DefaultMutableTreeNode(hvo_tiled); //创建子结点!
					rootNode.add(itemNode1); //加入子结点！！

					String[] thisLevelValue = findOneLevelDistinctValue(_chooseGroupFields[i]); //计算出这一个维度下的实际值,有可能出现【空值】的情况！！

					//补零机制,即零汇报机制！！即有些维度如果数据库中没有,则还是要显示维度本身,值当零处理!!
					//需要特别注意的是,日期维度的补零机制比较特别,即需要强行补全的机制!从最早的时间维度到当前最大时间维度之间要连续!但如果查询条件中有1季度,但数据库中最小值是2季度,1季度也补出来么? 如果永远强行从1季度补全,则如果查询条件只有3季度,会不会认为是错的?,
					if (isDoZeroAppend && !isContainDrillGroup(_chooseGroupFields[i])) { //如果不是被钻取的.
						if (this.zeroReportConfMap != null && zeroReportConfMap.containsKey(_chooseGroupFields[i])) { //如果这个维度定义了必须补零的数组!
							String[] str_zerodef = (String[]) zeroReportConfMap.get(_chooseGroupFields[i]); //value必须是一个一维数组！
							thisLevelValue = this.tbutil.getSpanFromTwoArray(thisLevelValue, str_zerodef); //将两个数组合并!
						}
					}
					//处理排序!!如果同时存在补零件与排序定义,必须先补零再排序!实际上大都情况(比如某个下拉框)的排序与补零的定义是一样的！ 但为什么不搞成一个呢?因为有时排时可能很简单，比如机构,我只要定义总行在前面就可以了!其他的省略。。
					if (!_isRowHeader && isSortByCpValue) { //只对列上的维度进行排行榜处理!
						//遍历所有维度,然后从Map中取得各个维度的实际值!然后构建一个HashVO[],第一列是名称,第二列是金额,然后进行按金额大小排序! 然后将排序后的数组再取出来!
						HashVO[] sortVOs = new HashVO[thisLevelValue.length]; //
						for (int s = 0; s < thisLevelValue.length; s++) { //变量叫s,表示sort之意!
							String str_mapKey = _chooseGroupFields[i] + "=" + thisLevelValue[s] + "▲" + "@" + chooseComputeFunAndField[0][2] + "(" + chooseComputeFunAndField[0][0] + ")";
							Double ld_sortValue = (Double) beforeBuilderDataMap.get(str_mapKey); //
							if (ld_sortValue == null) {
								ld_sortValue = 0D;
							}
							sortVOs[s] = new HashVO(); //
							sortVOs[s].setAttributeValue("name", thisLevelValue[s]); //
							sortVOs[s].setAttributeValue("value", ld_sortValue); //
							//System.out.println("【" + str_mapKey + "】=【" + ld_sortValue + "】"); //
						}
						String[] str_sortedNames = new String[sortVOs.length]; //
						tbutil.sortHashVOs(sortVOs, new String[][] { { "value", "Y", "Y" } }); //进行排序,根据金额排序!!而且是倒序!
						for (int s = 0; s < str_sortedNames.length; s++) { //变量叫s,表示sort之意!
							str_sortedNames[s] = sortVOs[s].getStringValue("name"); //
						}
						thisLevelValue = str_sortedNames; //
					} else {
						if (_orderConsMap != null && _orderConsMap.containsKey(_chooseGroupFields[i])) { // 如果没有设置分组排序，按照数据库查处的顺序显示。设置了就执行下面的逻辑！
							TBUtil.getTBUtil().sortStrsByOrders(thisLevelValue, (String[]) _orderConsMap.get(_chooseGroupFields[i])); //指定排序!
						} else {
							TBUtil.getTBUtil().sortStrs(thisLevelValue); //按ACSAII码排序
						}
					}
					//袁江晓 20130723 修改  对数组进行排序  【空值】显示到最后
					if (null != thisLevelValue && TBUtil.getTBUtil().isExistInArray("【空值】", thisLevelValue)) {
						MoveToEnd("【空值】", thisLevelValue);
					}
					//排序列后再处理过滤!!
					if (filterGroupValueMap != null && filterGroupValueMap.containsKey(_chooseGroupFields[i])) { //如果定义了过滤条件
						String[] str_filterDatas = (String[]) filterGroupValueMap.get(_chooseGroupFields[i]); //如果本维度被定义了!
						if (str_filterDatas != null && str_filterDatas.length > 0) {
							thisLevelValue = tbutil.getInterSectionFromTwoArray(thisLevelValue, str_filterDatas); //取交集
						}
					}

					DefaultMutableTreeNode[] itemNodes2 = new DefaultMutableTreeNode[thisLevelValue.length]; //
					for (int j = 0; j < itemNodes2.length; j++) { //遍历所有结点
						HashVO hvo_level = new HashVO(); //真正的维度值类型的数据!最后实际上是根据这种类型的数据来匹配计算的！！
						hvo_level.setAttributeValue("ItemType", "Level"); //真正的层级
						hvo_level.setAttributeValue("RowColType", _isRowHeader ? "Row" : "Col"); //
						hvo_level.setAttributeValue("LevelName", _chooseGroupFields[i]); //这个维度的名称！！
						hvo_level.setAttributeValue("LevelValue", thisLevelValue[j]); //这个维度的实际值！！
						hvo_level.setToStringFieldName("LevelValue"); //显示的是维度值
						itemNodes2[j] = new DefaultMutableTreeNode(hvo_level); //创建
						itemNode1.add(itemNodes2[j]); //作为子结点加入！！

						//判断是否选择了多个计算列，是否要再次进行分裂？？只有在行头进行分裂！！！
						if (_isRowHeader && !_isOnlyThisGroup && _computeFields.length > 1) { //如果有多个计算列！！则要再次进行分裂,只在行头上进行分裂，而且如果是列维度为空时(即只有行维度)，也是不分裂的,因为这时计算列自动移到列上了
							for (int k = 0; k < _computeFields.length; k++) { //最后一层根据计算列进行分裂！！！
								HashVO hvo_splitCp = new HashVO(); //计算列分裂
								hvo_splitCp.setAttributeValue("ItemType", "SplitCP"); //
								hvo_splitCp.setAttributeValue("RowColType", _isRowHeader ? "Row" : "Col"); //
								hvo_splitCp.setAttributeValue("CpType", _computeFields[k][0]); //计算的类型,比如count,sum
								hvo_splitCp.setAttributeValue("CpConfig", _computeFields[k][1]); //其他配置信息
								hvo_splitCp.setAttributeValue("CpField", _computeFields[k][2]); //计算的字段名称
								hvo_splitCp.setAttributeValue("CpIndexNo", "" + (k + 1)); //计算列的顺序编号
								if (extConfMaps[k] != null && extConfMaps[k].containsKey("显示名称")) { //如果强行指定了
									hvo_splitCp.setAttributeValue("ViewText", extConfMaps[k].get("显示名称")); // + "(" + _computeFields[r][0] + ")"
								} else {
									hvo_splitCp.setAttributeValue("ViewText", _computeFields[k][2]); //+ "(" + _computeFields[k][0] + ")"
								}
								hvo_splitCp.setToStringFieldName("ViewText"); //
								DefaultMutableTreeNode computeNode = new DefaultMutableTreeNode(hvo_splitCp); ////
								itemNodes2[j].add(computeNode); //加入计算列结点！！
							}
						}
					}

					//处理公式组！！！即有一些组是公式的,比如【({高风险}+{极大风险})/({低风险}+{极小风险})】....
					if (_isRowHeader && rowHeaderFormulaGroupMap != null && rowHeaderFormulaGroupMap.containsKey(_chooseGroupFields[i])) { //
						String[][] str_appendFormulaGroups = (String[][]) rowHeaderFormulaGroupMap.get(_chooseGroupFields[i]); //new String[][] { { "所有高风险", "{高风险}+{极大风险}", "是否带百分号=N" }, { "高风险/低风险", "({高风险}*100)/{极小风险}", "是否带百分号=Y" } }; //...
						for (int k = 0; k < str_appendFormulaGroups.length; k++) {
							HashVO hvo_level = new HashVO(); //真正的维度值类型的数据!最后实际上是根据这种类型的数据来匹配计算的！！
							hvo_level.setAttributeValue("ItemType", "FormulaLevel"); //公式组
							hvo_level.setAttributeValue("RowColType", _isRowHeader ? "Row" : "Col"); //
							hvo_level.setAttributeValue("LevelName", _chooseGroupFields[i]); //这个维度的名称！！
							hvo_level.setAttributeValue("LevelValue", str_appendFormulaGroups[k][0]); //这个维度的实际值！！
							hvo_level.setAttributeValue("ContainGroups", thisLevelValue); //包含哪些组!
							hvo_level.setAttributeValue("LevelFormula", str_appendFormulaGroups[k][1]); //这个组的公式
							hvo_level.setAttributeValue("LevelConfig", str_appendFormulaGroups[k][2]); //其他配置信息
							hvo_level.setToStringFieldName("LevelValue"); //显示的是维度值...

							DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(hvo_level); //
							itemNode1.add(childNode); //加上这个儿子
						}
					}

					//判断是否需要增加小计？？？平铺是没有合计的概念的！！因为即然是平铺,则说明总共就一层,所谓小计其实是等价于合计！！
					if (isTotal && _isCanTotal && _isSubTotal) { //如果有小计,并且的确是能够合计计算的(比如avg是不可以合计的)!!
						if (_computeFields.length > 1 && _isRowHeader && !_isOnlyThisGroup) { //如果有多个计算列！！则要再次进行分裂!
							for (int k = 0; k < _computeFields.length; k++) { //最后一层根据计算列进行分裂！！即在后面要加
								if (isCaneTotlal(_computeFields[k][0])) { //如果这一个计算列的确是“数量”或“金额”这种可以合计的，才真正加入计算列！也就是说如果有两个计算列，一个是数量,一个是百分比，则只会真正有一个合计列！
									HashVO hvo_subtotal = new HashVO(); //小计列
									hvo_subtotal.setAttributeValue("ItemType", "SubTotal"); //小计！！
									hvo_subtotal.setAttributeValue("RowColType", _isRowHeader ? "Row" : "Col"); //
									hvo_subtotal.setAttributeValue("SubTotalType", _computeFields[k][0]); //
									hvo_subtotal.setAttributeValue("SubTotalConfig", _computeFields[k][1]); //其他配置信息
									hvo_subtotal.setAttributeValue("SubTotalField", _computeFields[k][2]); //
									if (extConfMaps[k] != null && extConfMaps[k].containsKey("显示名称")) { //如果强行指定了
										hvo_subtotal.setAttributeValue("ViewText", extConfMaps[k].get("显示名称") + "&小计"); // + "(" + _computeFields[r][0] + ")"
									} else {
										hvo_subtotal.setAttributeValue("ViewText", _computeFields[k][2] + "&小计"); //
									}
									hvo_subtotal.setToStringFieldName("ViewText"); //
									DefaultMutableTreeNode subTotalNode = new DefaultMutableTreeNode(hvo_subtotal); //
									itemNode1.add(subTotalNode); //
								}
							}
						} else { //如果是列头,则肯定只有一个合计!且只有一个计算列则肯定只有一个小计！
							HashVO hvo_subtotal = new HashVO(); //小计列
							hvo_subtotal.setAttributeValue("ItemType", "SubTotal"); //小计！！
							hvo_subtotal.setAttributeValue("RowColType", _isRowHeader ? "Row" : "Col"); //
							hvo_subtotal.setAttributeValue("SubTotalType", _computeFields[0][0]); //
							hvo_subtotal.setAttributeValue("SubTotalConfig", _computeFields[0][1]); //其他配置信息！！
							hvo_subtotal.setAttributeValue("SubTotalField", _computeFields[0][2]); //
							hvo_subtotal.setAttributeValue("ViewText", "小计"); //
							hvo_subtotal.setToStringFieldName("ViewText"); //
							DefaultMutableTreeNode subTotalNode = new DefaultMutableTreeNode(hvo_subtotal); //
							itemNode1.add(subTotalNode); //
						}
					}
				}
			} else { //如果是层级
				for (int i = 0; i < _chooseGroupFields.length; i++) { //循环各层!!
					DefaultMutableTreeNode[] thisLevelNodes = getSomeLevelAllNodes(rootNode, i); //取得某一层的所有子结点!第一次只会返回根结点!
					for (int j = 0; j < thisLevelNodes.length; j++) { //遍历这一层的所有结点!
						HashVO hvo_thisNodeVO = (HashVO) thisLevelNodes[j].getUserObject(); ////
						String str_thisNodeType = hvo_thisNodeVO.getStringValue("ItemType"); //

						String[] str_pathLevelName = new String[i];
						String[] str_pathLevelValue = new String[i]; //
						String str_pathMapkey = ""; //
						TreeNode[] pathNodes = thisLevelNodes[j].getPath(); // 出取该结点到该层的所有结点
						for (int k = 0; k < str_pathLevelName.length; k++) {
							str_pathLevelName[k] = _chooseGroupFields[k]; //维度名称!比如【业务类型】【发现渠道】
							HashVO hvo_item = (HashVO) ((DefaultMutableTreeNode) pathNodes[k + 1]).getUserObject(); //
							str_pathLevelValue[k] = hvo_item.toString(); //维度的值,比如【个人业务】
							str_pathMapkey = str_pathMapkey + str_pathLevelName[k] + "=" + str_pathLevelValue[k] + "▲"; //
						}

						//关键计算逻辑！！！找到所有属于我这条血脉的儿子,即这一干支下可能有许多儿子。因为不同层级下面有不同的值
						String[] str_newchildNames = findNewChildName(str_pathLevelName, str_pathLevelValue, _chooseGroupFields[i]); //可能会出现【空值】

						//补零机制,即零汇报机制！！即有些维度如果数据库中没有,则还是要显示维度本身,值当零处理!
						//时间维度需要特别注意！！！应该是自动拼接!
						if (isDoZeroAppend && !isContainDrillGroup(_chooseGroupFields[i])) { //如果不是被钻取的.
							if (this.zeroReportConfMap != null && zeroReportConfMap.containsKey(_chooseGroupFields[i])) { //如果这个维度定义了必须补零的数组!
								String[] str_zerodef = (String[]) zeroReportConfMap.get(_chooseGroupFields[i]); //value必须是一个一维数组！
								str_newchildNames = this.tbutil.getSpanFromTwoArray(str_newchildNames, str_zerodef); //将两个数组合并!
							}
						}
						//在邮储项目中遇到,风险预警时,只显示本月记录,然后再倒序排序!!也就是说过滤后再排放序
						if (!_isRowHeader && isSortByCpValue) { //如果是列维度(只对列维度进行排行处理),且指定了要进行排序!!
							//遍历所有维度,然后从Map中取得各个维度的实际值!然后构建一个HashVO[],第一列是名称,第二列是金额,然后进行按金额大小排序! 然后将排序后的数组再取出来!
							HashVO[] sortVOs = new HashVO[str_newchildNames.length]; //
							for (int s = 0; s < str_newchildNames.length; s++) { //变量叫s,表示sort之意!
								String str_mapKey = str_pathMapkey + _chooseGroupFields[i] + "=" + str_newchildNames[s] + "▲" + "@" + chooseComputeFunAndField[0][2] + "(" + chooseComputeFunAndField[0][0] + ")"; //取得Map中的Key,这里应该可以根据第三个列中的值取数！
								//以前为double类型，超过千万级数据会报错，故修改之【李春娟/2019-03-21】
								BigDecimal ld_sortValue = (BigDecimal) beforeBuilderDataMap.get(str_mapKey); //取数
								if (ld_sortValue == null) { //
									ld_sortValue = new BigDecimal(0);
								}
								sortVOs[s] = new HashVO(); //
								sortVOs[s].setAttributeValue("name", str_newchildNames[s]); //
								sortVOs[s].setAttributeValue("value", ld_sortValue); //
								//System.out.println("【" + str_mapKey + "】=【" + ld_sortValue + "】"); //
							}
							String[] str_sortedNames = new String[sortVOs.length]; //
							tbutil.sortHashVOs(sortVOs, new String[][] { { "value", "Y", "Y" } }); //进行排序,根据金额排序!!而且是倒序!
							for (int s = 0; s < str_sortedNames.length; s++) { //变量叫s,表示sort之意!
								str_sortedNames[s] = sortVOs[s].getStringValue("name"); //
							}
							str_newchildNames = str_sortedNames; //
						} else {
							if (_orderConsMap != null && _orderConsMap.containsKey(_chooseGroupFields[i])) {// 如果没有设置分组排序，按照数据库查处的顺序显示。设置了就执行下面的逻辑！
								TBUtil.getTBUtil().sortStrsByOrders(str_newchildNames, (String[]) _orderConsMap.get(_chooseGroupFields[i])); //因为中文排序有编码问题,所以必须使用TBUtil中的排序方法!!
							} else {
								//	TBUtil.getTBUtil().sortStrs(str_newchildNames); //按ACSAII码排序  20130206  袁江晓更改  默认的应该为数据库的输出顺序，不按照ACSAII码排序
							}
						}
						//袁江晓 20130723 修改  对数组进行排序  【空值】显示到最后
						if (null != str_newchildNames && TBUtil.getTBUtil().isExistInArray("【空值】", str_newchildNames)) {
							MoveToEnd("【空值】", str_newchildNames);
						}
						//处理过滤情况!!!即只想显示哪些东东!过滤必须在最后处理!
						if (filterGroupValueMap != null && filterGroupValueMap.containsKey(_chooseGroupFields[i])) { //如果定义了过滤条件
							String[] str_filterDatas = (String[]) filterGroupValueMap.get(_chooseGroupFields[i]); //
							if (str_filterDatas != null && str_filterDatas.length > 0) {
								str_newchildNames = tbutil.getInterSectionFromTwoArray(str_newchildNames, str_filterDatas); //取交集
							}
						}

						//循环加入所有子结点!!!
						if (!str_thisNodeType.endsWith("Total")) { //如果是小计或合计列,则永远不加子结点!
							for (int k = 0; k < str_newchildNames.length; k++) {
								HashVO hvo_level = new HashVO(); //真正的维度值类型的数据!最后实际上是根据这种类型的数据来匹配计算的！！
								hvo_level.setAttributeValue("ItemType", "Level"); //真正的层级
								hvo_level.setAttributeValue("RowColType", _isRowHeader ? "Row" : "Col"); //
								hvo_level.setAttributeValue("LevelName", _chooseGroupFields[i]); //这个维度的名称！！
								hvo_level.setAttributeValue("LevelValue", str_newchildNames[k]); //这个维度的实际值！！
								hvo_level.setToStringFieldName("LevelValue"); //显示的是维度值...
								DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(hvo_level); //
								thisLevelNodes[j].add(childNode); //加上这个儿子

								//再见加上计算列!
								if (i == _chooseGroupFields.length - 1 && _isRowHeader && !_isOnlyThisGroup && _computeFields.length > 1) { //如果是末结点,且有多个计算列！！则要再次进行分裂!! 只在行头上进行分裂，而且如果是列维度为空时(即只有行维度)，也是不分裂的,因为这时计算列自动移到列上了
									for (int r = 0; r < _computeFields.length; r++) { //最后一层根据计算列进行分裂！！！
										HashVO hvo_splitCp = new HashVO(); //计算列分裂
										hvo_splitCp.setAttributeValue("ItemType", "SplitCP"); //
										hvo_splitCp.setAttributeValue("RowColType", _isRowHeader ? "Row" : "Col"); //
										hvo_splitCp.setAttributeValue("CpType", _computeFields[r][0]); //计算的类型,比如count,sum
										hvo_splitCp.setAttributeValue("CpConfig", _computeFields[r][1]); //其他配置信息
										hvo_splitCp.setAttributeValue("CpField", _computeFields[r][2]); //计算的字段名称
										hvo_splitCp.setAttributeValue("CpIndexNo", "" + (r + 1)); //计算列的顺序编号
										if (extConfMaps[r] != null && extConfMaps[r].containsKey("显示名称")) { //如果强行指定了
											hvo_splitCp.setAttributeValue("ViewText", extConfMaps[r].get("显示名称")); // + "(" + _computeFields[r][0] + ")"
										} else {
											hvo_splitCp.setAttributeValue("ViewText", _computeFields[r][2]); // + "(" + _computeFields[r][0] + ")"
										}
										hvo_splitCp.setToStringFieldName("ViewText"); ////
										DefaultMutableTreeNode computeNode = new DefaultMutableTreeNode(hvo_splitCp); //
										childNode.add(computeNode); //加入计算列结点！！
									}
								}
							}
						}

						//处理公式组！！！即有一些组是公式的,比如【({高风险}+{极大风险})/({低风险}+{极小风险})】....
						if (!str_thisNodeType.endsWith("Total") && _isRowHeader && (i == _chooseGroupFields.length - 1) && rowHeaderFormulaGroupMap != null && rowHeaderFormulaGroupMap.containsKey(_chooseGroupFields[i])) { //
							String[][] str_appendFormulaGroups = (String[][]) rowHeaderFormulaGroupMap.get(_chooseGroupFields[i]); //new String[][] { { "所有高风险", "{高风险}+{极大风险}", "是否带百分号=N" }, { "高风险/低风险", "({高风险}*100)/{极小风险}", "是否带百分号=Y" } }; //...
							for (int k = 0; k < str_appendFormulaGroups.length; k++) {
								HashVO hvo_level = new HashVO(); //真正的维度值类型的数据!最后实际上是根据这种类型的数据来匹配计算的！！
								hvo_level.setAttributeValue("ItemType", "FormulaLevel"); //公式组
								hvo_level.setAttributeValue("RowColType", _isRowHeader ? "Row" : "Col"); //
								hvo_level.setAttributeValue("LevelName", _chooseGroupFields[i]); //这个维度的名称！！
								hvo_level.setAttributeValue("LevelValue", str_appendFormulaGroups[k][0]); //这个维度的实际值！！
								hvo_level.setAttributeValue("ContainGroups", str_newchildNames); //包含哪些组!
								hvo_level.setAttributeValue("LevelFormula", str_appendFormulaGroups[k][1]); //这个组的公式
								hvo_level.setAttributeValue("LevelConfig", str_appendFormulaGroups[k][2]); //其他配置信息
								hvo_level.setToStringFieldName("LevelValue"); //显示的是维度值...

								DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(hvo_level); //
								thisLevelNodes[j].add(childNode); //加上这个儿子
							}
						}

						//判断是否需要增加小计？？？也就是说每一层都有小计！！！
						if (isTotal && _isCanTotal && (_isSubTotal || i == 0)) { //如果有小计,且可以合计计算(比如avg就不能合计计算)！！如果没有指定小计,则默认是有合计的(即i==0时就是合计)
							if (!str_thisNodeType.equals("SubTotal") && !str_thisNodeType.equals("Total")) { //如果本人就是小计,则不再添加了!即小计结点永远是叶子结点！！
								if (_isRowHeader && !_isOnlyThisGroup && _computeFields.length > 1) { //必须是行头才搞多个小计,且如果列头是计算列时不处理！
									for (int k = 0; k < _computeFields.length; k++) { //最后一层根据计算列进行分裂！！！
										if (isCaneTotlal(_computeFields[k][0])) { //必须是能够合计的玩意!!!比如avg就实际上不能合计!
											HashVO hvo_subtotal = new HashVO(); //小计列
											hvo_subtotal.setAttributeValue("ItemType", i == 0 ? "" : "SubTotal"); //小计！！
											hvo_subtotal.setAttributeValue("RowColType", _isRowHeader ? "Row" : "Col"); //
											hvo_subtotal.setAttributeValue("SubTotalType", _computeFields[k][0]); //如果有多个计算列,这里应该也是多个小计,比如【风险事件-小计】【损失金额-小计】!!
											hvo_subtotal.setAttributeValue("SubTotalConfig", _computeFields[k][1]); //
											hvo_subtotal.setAttributeValue("SubTotalField", _computeFields[k][2]); //
											if (extConfMaps[k] != null && extConfMaps[k].containsKey("显示名称")) { //如果强行指定了
												hvo_subtotal.setAttributeValue("ViewText", extConfMaps[k].get("显示名称") + (i == 0 ? "" : "&小计")); // + "(" + _computeFields[r][0] + ")"
											} else {
												hvo_subtotal.setAttributeValue("ViewText", i == 0 ? (_computeFields[k][2] + "") : (_computeFields[k][2] + "&小计")); //
											}
											hvo_subtotal.setToStringFieldName("ViewText"); //
											DefaultMutableTreeNode subTotalNode = null;
											if (hvo_subtotal.getAttributeValue("ItemType") == null || hvo_subtotal.getAttributeValue("ItemType").equals("")) {
											} else {
												subTotalNode = new DefaultMutableTreeNode(hvo_subtotal); //
												thisLevelNodes[j].add(subTotalNode); //
											}

										}
									}
								} else { //如果是行上的小计/合计,则永远只有一个！或者说总共就一个计算列！
									HashVO hvo_subtotal = new HashVO(); //小计列
									hvo_subtotal.setAttributeValue("ItemType", i == 0 ? "" : "SubTotal"); //小计！！
									hvo_subtotal.setAttributeValue("RowColType", _isRowHeader ? "Row" : "Col"); //
									hvo_subtotal.setAttributeValue("SubTotalType", _computeFields[0][0]); //如果有多个计算列,这里应该也是多个小计,比如【风险事件-小计】【损失金额-小计】!!
									hvo_subtotal.setAttributeValue("SubTotalConfig", _computeFields[0][1]); //
									hvo_subtotal.setAttributeValue("SubTotalField", _computeFields[0][2]); //
									hvo_subtotal.setAttributeValue("ViewText", i == 0 ? "" : "小计"); //
									hvo_subtotal.setToStringFieldName("ViewText"); //
									DefaultMutableTreeNode subTotalNode = null;
									if (hvo_subtotal.getAttributeValue("ItemType") == null || hvo_subtotal.getAttributeValue("ItemType").equals("")) {
									} else {
										subTotalNode = new DefaultMutableTreeNode(hvo_subtotal); //
										thisLevelNodes[j].add(subTotalNode); //
									}

								}
							}
						}
					}

				}
			} //如果是层级结束!!!
		} //如果有维度

		//遍历所有叶子结点,一下子为所有叶子结点加上所有：维度名称，维度值，计算列值！！！这样做完全是为了提高性能!
		DefaultMutableTreeNode[] allLeafNodes = getAllLeafNodes(rootNode); //取得所有叶子结点！！！
		for (int i = 0; i < allLeafNodes.length; i++) { //遍历所有吓子结点!
			HashVO hvo_leaf = (HashVO) allLeafNodes[i].getUserObject(); ////

			StringBuilder sb_levelNameValue = new StringBuilder(); //
			TreeNode[] parentNodes = allLeafNodes[i].getPath(); //取得这个结点的所有父亲结点！
			for (int j = 1; j < parentNodes.length; j++) { //遍历所有父亲结点！！
				HashVO hvo_nodeItem = (HashVO) ((DefaultMutableTreeNode) parentNodes[j]).getUserObject(); //某个父亲结点!!
				if (hvo_nodeItem.getStringValue("ItemType").equals("Level") || hvo_nodeItem.getStringValue("ItemType").equals("FormulaLevel")) { //必须是Level！
					sb_levelNameValue.append(hvo_nodeItem.getStringValue("LevelName") + "=" + hvo_nodeItem.getStringValue("LevelValue") + (_isRowHeader ? "◆" : "▲")); ////
				}
			}
			hvo_leaf.setAttributeValue("RealLevelNameValue", sb_levelNameValue.toString()); //
			String str_itemType = hvo_leaf.getStringValue("ItemType"); //
			if (str_itemType.equals("SplitCP")) { //如果本身就是分裂的
				hvo_leaf.setAttributeValue("RealCPType", hvo_leaf.getStringValue("CpType")); //
				hvo_leaf.setAttributeValue("RealCPField", hvo_leaf.getStringValue("CpField")); //
				hvo_leaf.setAttributeValue("RealCPConfig", hvo_leaf.getStringValue("CpConfig")); //
			} else if (str_itemType.equals("SubTotal") || str_itemType.equals("Total")) {
				hvo_leaf.setAttributeValue("RealCPType", hvo_leaf.getStringValue("SubTotalType")); //
				hvo_leaf.setAttributeValue("RealCPField", hvo_leaf.getStringValue("SubTotalField")); //
				hvo_leaf.setAttributeValue("RealCPConfig", hvo_leaf.getStringValue("SubTotalConfig")); //
			} else { //如果叶子结点既不是分裂的,也不是合计的,则必须是只有一个计算列,所以直接使用第一位计算列！
				hvo_leaf.setAttributeValue("RealCPType", _computeFields[0][0]); //
				hvo_leaf.setAttributeValue("RealCPConfig", _computeFields[0][1]); //
				hvo_leaf.setAttributeValue("RealCPField", _computeFields[0][2]); //
			}
		}

		return rootNode; //
	}

	//袁江晓 20130723 添加 将一数组中的一个元素移动到末尾 首先该数组应该只含有一个str
	public void MoveToEnd(String str, String[] thisLevelValue) {
		int iStr = 0;
		for (int i = 0; i <= thisLevelValue.length - 1 - iStr; i++) {
			if (!thisLevelValue[i].equals(str)) {
				continue;
			} else {
				for (int j = i + 1; j <= thisLevelValue.length - 1; j++) {
					thisLevelValue[j - 1] = thisLevelValue[j];
				}
				thisLevelValue[thisLevelValue.length - 1] = str;
				i--;
				iStr++;
			}
		}
	}

	//取得某一个结点的整个路径的全称！！
	private String getPathName(DefaultMutableTreeNode _node) {
		TreeNode[] parentNodes = _node.getPath(); //
		StringBuilder sb_text = new StringBuilder(); //
		for (int i = 1; i < parentNodes.length; i++) {
			sb_text.append(((DefaultMutableTreeNode) parentNodes[i]).getUserObject() + "-"); //
		}
		String str_return = sb_text.toString(); //
		if (str_return.endsWith("-")) {
			str_return = str_return.substring(0, str_return.length() - 1); //
		}
		return str_return; //
	}

	/**
	 * 计算出某一维度的实际值,要做唯一性过滤!
	 * @return
	 */
	private String[] findOneLevelDistinctValue(String _groupLevelName) {
		HashSet hst = new HashSet(); //
		for (int i = 0; i < hvs_data.length; i++) { //从数据中取得某个维度的唯一性的数据!!!
			String str_value = hvs_data[i].getStringValue(_groupLevelName, "【空值】", true); //
			hst.add(str_value); //
		}

		return (String[]) hst.toArray(new String[0]); //
	}

	/**
	 * 找出某一支下面新一层的所有儿子
	 * 
	 * @param str_pathlevelname
	 *            血系各层的名称,比如 年度,区域,产品. 你也可以理解成 高祖,祖父,父亲,
	 * @param str_pathvalue
	 *            血系各层的值 比如 2008,华东,电视 你也可以理解成 康熙,雍正,乾隆
	 * @param string
	 *            找出新一层的所有值,比如 产品类型, 你也可以理解成 儿子, 返回值应该是 乾隆所有的儿子, 可能有4个
	 * @return
	 */
	private String[] findNewChildName(String[] str_pathlevelname, String[] str_pathvalue, String _newlevelname) {
		ArrayList al = new ArrayList();
		//HashSet lhs = new HashSet(); //   旧的代码  袁江晓 2013-02-26更改  按照查询顺序显示
		for (int i = 0; i < hvs_data.length; i++) { // 从数据中取得某个维度的唯一性的数据!!!
			boolean bo_ismatch = true;
			for (int j = 0; j < str_pathlevelname.length; j++) { //遍历各个已有维度上的值,必须全部匹配,才认为是对上了!
				if (!hvs_data[i].getStringValue(str_pathlevelname[j]).equals(str_pathvalue[j])) { //如果有一个对不上则认为上对不上
					bo_ismatch = false; //
					break;
				}
			}
			if (bo_ismatch) { // 如果条件都满足,即顺利过关了,则加入
				//  lhs.add(hvs_data[i].getStringValue(_newlevelname, "【空值】", true)); // 找出唯一性的值! //   旧的代码  袁江晓 2013-02-26更改  按照查询顺序显示
				String temp = hvs_data[i].getStringValue(_newlevelname, "【空值】", true);
				if (al.indexOf(temp) == -1) { //先判断有没有重复
					al.add(temp);//春娟说航天科工系统会出现重复，于是加上一个判断   旧的代码  袁江晓 2013-02-23更改  按照查询顺序显示
				}
			}
		}
		return (String[]) al.toArray(new String[0]); //
	}

	private boolean isContainDrillGroup(String _groupName) {
		if (drillConditionMap != null && drillConditionMap.length > 0) {
			for (int i = 0; i < drillConditionMap.length; i++) {
				if (drillConditionMap[i].containsKey(_groupName)) { //如果包含这个组名,则说明是被钻取的!则就忽略补零机构
					return true; //
				}
			}
		}

		return false; //
	}

	/**
	 * 取得某一层的所有结点!
	 * 
	 * @return
	 */
	private DefaultMutableTreeNode[] getSomeLevelAllNodes(DefaultMutableTreeNode _node, int _level) {
		DefaultMutableTreeNode[] allNodes = getAllNodes(_node); //
		Vector v_return = new Vector(); //
		for (int i = 0; i < allNodes.length; i++) {
			if (allNodes[i].getLevel() == _level) {
				v_return.add(allNodes[i]); //
			}
		}
		return (DefaultMutableTreeNode[]) v_return.toArray(new DefaultMutableTreeNode[0]); //
	}

	/**
	 * 取得一组结点中层次等于_level的所有结点
	 * 
	 * @param _nodes
	 * @param _level
	 * @return
	 */
	private DefaultMutableTreeNode[] getLevelNodes(DefaultMutableTreeNode[] _nodes, int _level) {
		ArrayList al_nodes = new ArrayList();
		for (int i = 0; i < _nodes.length; i++) {
			if (_nodes[i].getLevel() == _level) {
				al_nodes.add(_nodes[i]); //
			}
		}
		return (DefaultMutableTreeNode[]) al_nodes.toArray(new DefaultMutableTreeNode[0]);
	}

	private DefaultMutableTreeNode[] getAllNodes(DefaultMutableTreeNode _node) {
		Vector vector = new Vector(); //
		visitAllNodes(vector, _node); //
		DefaultMutableTreeNode[] allNodes = (DefaultMutableTreeNode[]) vector.toArray(new DefaultMutableTreeNode[0]); //
		return allNodes; //
	}

	/**
	 * 取得某个结点的所有末结点的数量
	 * @param _node
	 * @return
	 */
	private int getLeafNodeCount(DefaultMutableTreeNode _node) {
		return getLeafNodeCountAndLevel(_node)[0]; //
	}

	private int[] getLeafNodeCountAndLevel(DefaultMutableTreeNode _node) {
		int li_count = 0;
		int li_level = 0; //
		DefaultMutableTreeNode[] allNodes = getAllNodes(_node); //
		for (int i = 0; i < allNodes.length; i++) {
			if (allNodes[i].isLeaf()) {
				li_count++;
				if (allNodes[i].getLevel() > li_level) { //如果大于最大层!
					li_level = allNodes[i].getLevel(); //
				}
			}
		}
		return new int[] { li_count, li_level };
	}

	//计算出一个结点下的所有叶子结点！！！
	private DefaultMutableTreeNode[] getAllLeafNodes(DefaultMutableTreeNode _node) {
		ArrayList al_nodes = new ArrayList(); //
		DefaultMutableTreeNode[] allNodes = getAllNodes(_node); //
		for (int i = 0; i < allNodes.length; i++) {
			if (allNodes[i].isLeaf()) {
				al_nodes.add(allNodes[i]); //
			}
		}

		return (DefaultMutableTreeNode[]) al_nodes.toArray(new DefaultMutableTreeNode[0]);
	}

	private void visitAllNodes(Vector _vector, TreeNode node) {
		_vector.add(node); // 加入该结点
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) e.nextElement(); // 找到该儿子!!
				visitAllNodes(_vector, childNode); // 继续查找该儿子
			}
		}
	}

	private String[] getAllChooseGroupFields() {
		String[] str_allGroupFields = new String[chooseGroupRowFields.length + chooseGroupColFields.length];
		System.arraycopy(chooseGroupRowFields, 0, str_allGroupFields, 0, chooseGroupRowFields.length); // 分组字段
		System.arraycopy(chooseGroupColFields, 0, str_allGroupFields, chooseGroupRowFields.length, chooseGroupColFields.length);
		return str_allGroupFields; //
	}

	private String[][] getComputeFunAndFields(ArrayList _al) {
		String[][] str_returns = new String[_al.size()][3];
		for (int i = 0; i < _al.size(); i++) {
			str_returns[i] = (String[]) _al.get(i);
		}
		return str_returns;
	}

	private boolean containComputeFunandField(ArrayList _al, String[] _oneComputeDefine) {
		for (int i = 0; i < _al.size(); i++) {
			String[] str_items = (String[]) _al.get(i); //
			if (str_items[0].equals(_oneComputeDefine[0]) && str_items[2].equals(_oneComputeDefine[2])) {
				return true;
			}
		}
		return false; //
	}

	//以前为double类型，超过千万级数据会报错，故修改之【李春娟/2019-03-21】
	private String trimBigDecimalToStr(BigDecimal _bigDecimal, String _cpType, HashMap _configMap) {
		String str_text = trimBigDecimalToStr(_bigDecimal); //
		if (!str_text.equals("")) {
			if (_cpType != null && (_cpType.indexOf("Percent") >= 0 || _cpType.indexOf("Increase") >= 0)) {
				str_text = str_text + "%"; //
			} else {
				if ("Y".equalsIgnoreCase((String) _configMap.get("是否带百分号"))) {
					str_text = str_text + "%"; //
				}
			}
		}
		return str_text; //
	}

	//以前为double类型，超过千万级数据会报错，故修改之【李春娟/2019-03-21】
	private String trimBigDecimalToStr(BigDecimal _value) {
		if (_value == null) {
			return "";
		}
		if (_value.compareTo(new BigDecimal("0")) == 0) {
			return "0";
		}
		_value = _value.setScale(2, BigDecimal.ROUND_HALF_UP);
		String str_value = _value.toString();
		if (str_value.endsWith(".00")) {
			str_value = str_value.substring(0, str_value.indexOf(".00"));
		}
		return str_value;
	}

	private String trimDoubleToStr(Double _double, String _cpType, HashMap _configMap) {
		String str_text = trimDoubleToStr(_double); //
		if (!str_text.equals("")) {
			if (_cpType != null && (_cpType.indexOf("Percent") >= 0 || _cpType.indexOf("Increase") >= 0)) {
				str_text = str_text + "%"; //
			} else {
				if ("Y".equalsIgnoreCase((String) _configMap.get("是否带百分号"))) {
					str_text = str_text + "%"; //
				}
			}
		}
		return str_text; //
	}

	private String trimDoubleToStr(Double _double) {
		if (_double == null) {
			return "";
		}
		if (_double == 0) {
			return "0";
		}
		String str_value = "" + _double; //
		int li_pos = str_value.indexOf("."); //
		if (li_pos > 0) {
			String str_prefix = str_value.substring(0, li_pos); //
			String str_subfix = str_value.substring(li_pos + 1, str_value.length()); //后辍
			if (str_subfix.length() > 2) { //如果有两位小数
				str_subfix = str_subfix.substring(0, 2); //
			}
			if (str_subfix.equals("0") || str_subfix.equals("00")) {
				return str_prefix; //
			} else {
				return str_prefix + "." + str_subfix; //
			}
		} else {
			return str_value;
		}
	}

	private String trimDoubleToStr(String _str) {
		if (_str == null || _str.equals("")) {
			return "";
		}
		int li_pos = _str.indexOf("."); //
		if (li_pos > 0) {
			String str_prefix = _str.substring(0, li_pos); //
			String str_subfix = _str.substring(li_pos + 1, _str.length()); //后辍
			if (str_subfix.length() > 2) { //如果有两位小数
				str_subfix = str_subfix.substring(0, 2); //
			}
			return str_prefix + "." + str_subfix; //
		} else {
			return _str;
		}

	}

	/**
	 * 取得剩余的可进行钻取的维度
	 * 
	 * @return
	 */
	private String[] getRemainGroupFields() {
		ArrayList al_remains = new ArrayList(); //
		if (null == allCanGroupFields) {
			return null;
		}
		for (int i = 0; i < allCanGroupFields.length; i++) {
			if (!isInArray(allCanGroupFields[i], chooseGroupRowFields) && !isInArray(allCanGroupFields[i], chooseGroupColFields)) {
				al_remains.add(allCanGroupFields[i]); //
			}
		}
		return (String[]) al_remains.toArray(new String[0]); //
	}

	private boolean isInArray(String _item, String[] _arrays) {
		for (int i = 0; i < _arrays.length; i++) {
			if (_arrays[i].equals(_item)) {
				return true;
			}
		}
		return false;
	}

	public String[] getAllCanGroupFields() {
		return allCanGroupFields;
	}

	public void setAllCanGroupFields(String[] allCanGroupFields) {
		this.allCanGroupFields = allCanGroupFields;
	}

	public String[] getAllCanComputeFields() {
		return allCanComputeFields;
	}

	public void setAllCanComputeFields(String[] allCanComputeFields) {
		this.allCanComputeFields = allCanComputeFields;
	}

	public BillCellPanel getCellPanel() {
		return cellPanel;
	}

	public String[] getChooseGroupRowFields() {
		return chooseGroupRowFields;
	}

	public String[] getChooseGroupColFields() {
		return chooseGroupColFields;
	}

	public String[][] getChooseComputeFunAndField() {
		return chooseComputeFunAndField;
	}

	public HashMap getQueryConditionMap() {
		return queryConditionMap;
	}

	public String getStr_drillActionClass() {
		return str_drillActionClass;
	}

	public void setStr_drillActionClass(String str_drillActionClass) {
		this.str_drillActionClass = str_drillActionClass;
	}

	public String getStr_drillTempletCode() {
		return str_drillTempletCode;
	}

	public void setStr_drillTempletCode(String str_drillTempletCode) {
		this.str_drillTempletCode = str_drillTempletCode;
	}

	public void setDrillgroupbindMap(HashMap drillgroupbindMap) {
		this.drillgroupbindMap = drillgroupbindMap;
	}

}
