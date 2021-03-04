package cn.com.pushworld.salary.ui.person.p020;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.DivComponentLayoutUtil;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.pushworld.salary.to.SalaryTBUtil;
import cn.com.pushworld.salary.ui.SalaryUIUtil;
import cn.com.pushworld.salary.ui.targetcheck.SalaryBomClickEvent;
import cn.com.pushworld.salary.ui.targetcheck.SalaryBomClickListener;
import cn.com.pushworld.salary.ui.targetcheck.SalaryBomPanel;

/**
 * 员工互评界面。 此页面可以进行 普通员工互评，中层。
 * 
 * 
 * @author haoming create by 2013-7-15
 */
public class PersonMutualWKPanel extends AbstractWorkPanel implements ChangeListener, ActionListener {

	private static final long serialVersionUID = 3880878924865803724L;
	private HashVO currLog = null; // 当前计划日志ID。
	private String userid = ClientEnvironment.getCurrSessionVO().getLoginUserId(); // 当前登录人ID
	private String inputChecktype = null;
	private HashVO currCheckVOs[]; // 当前人员评分的所有内容

	private WLTTabbedPane tabpane = new WLTTabbedPane();// 如果本人可以评价多类指标，（员工互评，中层等）那么就是页签。否则直接搞一个BillListPanel.

	private List canchecktype; // 登录人员可以评的指标类型。一般员工互评，中层评分。

	private int value_col_start_index = 2; // 正真的数据从的第几列开始。
	private int value_row_start_index = 2; // 正真的数据从的第几行开始。

	private HashMap tabOpenHis = new HashMap(); // 页签对应的面板(BillCellPanel,BillListPanel)。

	private SalaryBomPanel planBomPanel = new SalaryBomPanel();
	/*
	 * 设置当前页面状态
	 */
	private boolean otherPanelInit = false; // 是否是其他页面调用

	private int beginTabIndex = 0;

	private String input_value_range = TBUtil.getTBUtil().getSysOptionStringValue("薪酬模块人员打分区间", "0-10");

	private boolean autoCheckCommit = true;

	private TBUtil tbutil = new TBUtil();

	public static final String ggccTabName = SalaryTBUtil.ggCheckTargetType;

	public static final boolean hideTotle = TBUtil.getTBUtil().getSysOptionBooleanValue("打分界面是否隐藏总计", false); // 默认不隐藏

	private boolean editable = true;

	private static final Logger logger = WLTLogger.getLogger(PersonMutualWKPanel.class);

	private SalaryTBUtil salaryTBUtil = new SalaryTBUtil();

	public void initialize() {
		this.add(init());
	}

	private JPanel init() {
		HashVO vos[];
		try {
			// 求出登陆人可评的所有计划
			vos = UIUtil.getHashVoArrayByDS(null, "select distinct(t1.id),t1.checkdate,t1.status,t1.name from SAL_TARGET_CHECK_LOG t1,sal_person_check_score t2 where t1.id = t2.logid and t1.status!='考核结束' and t2.scoreuser='" + userid + "'");
			if (vos == null || vos.length == 0) {
				JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.CENTER));
				WLTLabel label = new WLTLabel("目前全行没有考评中的计划...");
				label.setFont(new Font("宋体", Font.BOLD, 16));
				panel.add(label);
				return panel;
			}
			if (vos.length > 1) { // 如果有多个用bom显示。
				return getPlanBomPanel(vos);
			} else {// 如果现有就一个执行中
				currLog = vos[0];
				return loadPanelByLogPlanVO();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JPanel();
	}

	/*
	 * 从其他类中调用初始化方法。需要传入页签，当前的计划VO，开始的页签数
	 */
	public void customInit(WLTTabbedPane _tabPanel, HashVO _logVO, String _userid, String _inputchecktype, boolean _editable) {
		tabpane = _tabPanel;
		currLog = _logVO;
		otherPanelInit = true;
		beginTabIndex = tabpane.getTabCount(); // 开始位置，如果有其他
		if (beginTabIndex > 0) {
			for (int i = 0; i < beginTabIndex; i++) {
				tabOpenHis.put(i + "", null);
			}

		}
		userid = _userid;
		editable = _editable;
		inputChecktype = _inputchecktype;
		loadPanelByLogPlanVO();
	}

	/*
	 * 根据选择的计划，获取评分面板。
	 */
	private JPanel loadPanelByLogPlanVO() {
		try {
			StringBuffer sqlsb = new StringBuffer();
			sqlsb.append("select t1.*,t2.catalog from  sal_person_check_score t1 left join sal_person_check_list t2 on t1.targetid=t2.id left join v_pub_user_post_2 u  on " + "u.userid = t1.checkeduser and u.isdefault='Y' left join pub_corp_Dept u2 on u2.id = u.deptid left join sal_person_check_type t4 on t4.name=t1.checktype where t1.scoreuser='" + userid + "' and t1.logid='"
					+ currLog.getStringValue("id") + "' and t1.targettype!='员工定量指标' ");
			if (!tbutil.isEmpty(inputChecktype)) {
				sqlsb.append(" and t1.checktype='" + inputChecktype + "' ");
			}
			sqlsb.append(" order by t4.code,t1.targettype,t2.seq,u2.seq,u.seq");
			currCheckVOs = UIUtil.getHashVoArrayByDS(null, sqlsb.toString()); // 根据部门人员排序。
			String currcheckType = "-1";// 当前遍历的评分类型。
			canchecktype = new ArrayList();
			// 遍历所有可评分信息。首先对被考核对象进行分类（中层，一般员工），再对同一指标对应被评人员进行归类（一指标对应多个被评人员）,
			HashMap<String, String> checktypeAndtargetType = new HashMap<String, String>(); // 用来记录检查类型和指标类型。
			for (int i = 0; i < currCheckVOs.length; i++) {
				String targeteID = currCheckVOs[i].getStringValue("targetid");
				String targettype = currCheckVOs[i].getStringValue("targettype"); // 指标的类型
				String catalog = currCheckVOs[i].getStringValue("catalog", "暂无分类");
				String targetname = currCheckVOs[i].getStringValue("targetname");
				String userName = currCheckVOs[i].getStringValue("checkedusername");
				String checkuserid = currCheckVOs[i].getStringValue("checkeduser");
				String score = currCheckVOs[i].getStringValue("checkscore");
				String checktype = currCheckVOs[i].getStringValue("checktype", "类型缺失"); // 指标的考评类型
				String status = currCheckVOs[i].getStringValue("status");
				String weights = currCheckVOs[i].getStringValue("weights"); // 权重
				// if ("高管".equals(checktype) && targettype.equals("高管定性指标")) {
				// currCheckVOs[i].setAttributeValue("checktype", ggccTabName);
				// checktype = ggccTabName; // 指标的考评类型
				// }
				if ("".equals(checktype)) {
					checktype = "类型缺失";
				}
				LinkedHashMap userset = null; // 用来记录登陆人考核一个类型指标的所有[被考核人员],key:USERID,value：{userid,username}
				LinkedHashMap scorevomap = null;// 用来记录每一个指标对应的员工考核分数TargetHashVO。key:targetid,value:HashVO{id,targetName,userscore1,userscore2..}
				// HashMap sumMap = null; // 记录总分key:userid,value:sumcount
				LinkedHashMap cellCompareHashVOMap; // 用来记录表格和数据库的关系。目前表格里面只有一个数值，其实应该对应数据库的一条数据。
				if (!checktype.equals(currcheckType)) { // 如果是新的类型。重新定义一次人员map和指标map
					userset = new LinkedHashMap(); // 唯一过滤被评的人,就是表头。
					scorevomap = new LinkedHashMap(); //
					cellCompareHashVOMap = new LinkedHashMap();
					// sumMap = new HashMap(); // 人员总分
					Boolean submit = Boolean.FALSE; // 存放是否已经提交过。
					if (status != null && !status.equals("") && !status.equals("待提交")) {// 有一条不为空，该记录就提交了。必须一次性全部提交。
						submit = Boolean.TRUE;
					}
					Object[] obj = new Object[] { userset, scorevomap, null, checktype, cellCompareHashVOMap, submit };
					if (ggccTabName.equals(checktype)) { // 高管需要特殊处理
						List targetAndScoreList = new ArrayList();
						targetAndScoreList.add(currCheckVOs[i]);
						obj = new Object[] { targetAndScoreList, null, null, checktype, null, submit };
						canchecktype.add(obj);
						currcheckType = checktype; // 设定标识值。
						continue;
					} else {
						canchecktype.add(obj);
						currcheckType = checktype; // 设定标识值。
					}

				} else {// 类别包含设定
					Object[] obj = (Object[]) canchecktype.get(canchecktype.size() - 1);
					if (ggccTabName.equals(checktype)) { // 高管需要特殊处理
						List targetAndScoreList = (List) obj[0];
						targetAndScoreList.add(currCheckVOs[i]);
						continue;
					}
					if ((Boolean) obj[5]) { // 如果是已经标识了状态是已提交，需要判断下当前数据是不是未提交。
						if (status == null || status.equals("") || status.equals("待提交")) {
							obj[5] = Boolean.FALSE;
						}
					}
					userset = (LinkedHashMap) obj[0];
					scorevomap = (LinkedHashMap) obj[1];
					cellCompareHashVOMap = (LinkedHashMap) obj[4];
				}
				userset.put(checkuserid, new String[] { checkuserid, userName });//
				HashVO rowTargetVO = null; // 一行指标的HashVO。
				if (scorevomap.containsKey(targeteID)) {
					rowTargetVO = (HashVO) scorevomap.get(targeteID); // 得到一样
				} else {
					rowTargetVO = new HashVO();
					rowTargetVO.setAttributeValue("id", targeteID); // 设定id
				}
				if (!rowTargetVO.containsKey(checkuserid)) {
					rowTargetVO.setAttributeValue(checkuserid, score);
				}
				rowTargetVO.setAttributeValue("targetName", targetname);// 设定名称
				rowTargetVO.setAttributeValue("catalog", catalog);// 设定名称
				rowTargetVO.setAttributeValue("weights", weights);// 权重
				scorevomap.put(targeteID, rowTargetVO);
				cellCompareHashVOMap.put((scorevomap.size() - 1) + "_" + checkuserid, i); // key
				// =
				// 行数+userid
			}
			for (int i = 0; i < canchecktype.size(); i++) { //
				Object[] obj = (Object[]) canchecktype.get(i);
				String tabName = (String) obj[3];
				String imageName = "office_021.gif";
				if ("高管".equals(tabName)) {
					imageName = "office_015.gif";
				} else if ("中层".equals(tabName)) {
					imageName = "office_108.gif";
				}
				tabpane.addTab(tabName, UIUtil.getImage(imageName), new JPanel());
			}
			tabpane.addChangeListener(this);
			if (beginTabIndex == 0) {
				tabPanelLoad(beginTabIndex); // 加载第一个面板
			}
			return tabpane;
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JPanel();
	}

	/**
	 * 将单元格相同内容的合并
	 * 
	 * @param cellItemVOs
	 * @param _spanColumns
	 *            那几列需要处理
	 */
	public void formatSpan(BillCellItemVO[][] cellItemVOs, int[] _spanColumns) {
		if (_spanColumns != null) {
			HashMap temp = new HashMap();
			for (int i = 0; i < _spanColumns.length; i++) {
				int li_pos = _spanColumns[i];
				if (li_pos >= 0) {
					int li_spancount = 1;
					int li_spanbeginpos = 1;
					for (int k = 1; k < cellItemVOs.length; k++) {
						String str_value = cellItemVOs[k][li_pos].getCellvalue();
						String str_value_front = cellItemVOs[k - 1][li_pos].getCellvalue();
						if (TBUtil.getTBUtil().compareTwoString(str_value_front, str_value)) {
							if (i >= 1) {
								String str_value0 = cellItemVOs[k][_spanColumns[i - 1]].getCellvalue();
								String str_value_front0 = cellItemVOs[k - 1][_spanColumns[i - 1]].getCellvalue();
								if (TBUtil.getTBUtil().compareTwoString(str_value0, str_value_front0)) {
									li_spancount++;
								} else {
									cellItemVOs[li_spanbeginpos][li_pos].setSpan(li_spancount + ",1");
									li_spancount = 1;
									li_spanbeginpos = k;
								}
							} else {
								li_spancount++;
							}

						} else {
							cellItemVOs[li_spanbeginpos][li_pos].setSpan(li_spancount + ",1");
							li_spancount = 1;
							li_spanbeginpos = k;
						}
					}
					cellItemVOs[li_spanbeginpos][li_pos].setSpan(li_spancount + ",1");
				}
			}
		}
	}

	/*
	 * 根据页签编号，获取BillCellPanel评分页面。
	 */
	private JPanel getCheckScoreMainBillCellPanel(int _index) {
		BillCellPanel currCellpanel = new BillCellPanel(getBillCellVo());
		currCellpanel.setAllowShowPopMenu(false); //
		int colCount = currCellpanel.getColumnCount();
		currCellpanel.putClientProperty("this", this);
		Object[] obj = (Object[]) canchecktype.get(_index);
		if (!ggccTabName.equals(obj[3])) {
			for (int i = 2; i < colCount; i++) { // 求和
				resetColSum(currCellpanel, i);
			}
		}
		boolean issubmit = (Boolean) obj[5]; // 评分类型，是给中层，还是一般员工。
		JPanel mainPanel = new WLTPanel(new BorderLayout());
		mainPanel.setOpaque(false);
		mainPanel.add(currCellpanel, BorderLayout.CENTER);
		StringBuffer showWarnInfo = new StringBuffer();
		showWarnInfo.append("&nbsp;&nbsp;&nbsp;&nbsp;满意度评分标准: 非常满意 9-10 分; 满意 7-8 分; 合格 5-6 分; 基本合格 3-4 分; 不合格 0-2分.");
		if ("一般人员".equals(obj[3]) && !hideTotle) {
			showWarnInfo.append("(多个员工总分相同不能提交)");
		}
		JLabel info_label = new JLabel("<html><font color='blue'>" + showWarnInfo.toString() + "</font></html>");
		// info_label.setPreferredSize(new Dimension(550 +
		// LookAndFeel.getFONT_REVISE_SIZE() * showWarnInfo.length(),
		// hideTotle?18:33));
		info_label.setVerticalTextPosition(JLabel.CENTER);
		info_label.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
		if (!issubmit) { //
			WLTButton btn_submit = new WLTButton("提交", UIUtil.getImage("zt_050.gif"));
			btn_submit.setToolTipText("提交结果, 提交后不能修改");
			btn_submit.addActionListener(this);
			WLTPanel btnPanel = new WLTPanel(new BorderLayout());
			btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			btnPanel.add(btn_submit, BorderLayout.WEST);
			btnPanel.add(info_label, BorderLayout.CENTER);
			mainPanel.add(btnPanel, BorderLayout.NORTH);
		} else { // 如果已经提交.
			currCellpanel.setEditable(false);
			WLTButton btn_submit = new WLTButton("修改", UIUtil.getImage("zt_071.gif"));
			btn_submit.addActionListener(this);
			WLTPanel btnPanel = new WLTPanel(new FlowLayout(FlowLayout.LEFT));
			// btnPanel.add(btn_submit);
			btnPanel.add(info_label);
			mainPanel.add(btnPanel, BorderLayout.NORTH);
			setCurrCellPanelStatus(currCellpanel, "已 提 交");
		}
		tabOpenHis.put((_index + beginTabIndex) + "", currCellpanel); // 把正在的页签编号加入
		if (!editable) {
			currCellpanel.setEditable(editable);
		}
		currCellpanel.setLockedCell(2, 1);
		return mainPanel;
	}

	private void setCurrCellPanelStatus(final JComponent parent, final String _status) {
		new Timer().schedule(new TimerTask() {
			public void run() {
				JPanel jp = new JPanel() {
					protected void paintComponent(Graphics g) {
						// 盖章
						if (parent != null && parent.isShowing()) { // 搞到bufferedimage中特别不清楚。
							super.paintComponent(g);
							Graphics2D g2d = (Graphics2D) g.create();
							g2d.translate(15, 8);
							g2d.setComposite(AlphaComposite.SrcOver.derive(0.6f));
							g2d.setColor(Color.red.brighter());
							g2d.rotate(0.4);
							g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
							g2d.setStroke(new BasicStroke(3.5f));
							g2d.drawRect(1, 1, 150, 30);
							g2d.setFont(new Font("黑体", Font.BOLD, 25));
							g2d.drawString(_status, 15, 25);
						}
					}
				};
				jp.setOpaque(false);
				int i = 0;
				while (!parent.isShowing()) {
					i++;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (i > 300) { // 30秒再出不来，直接返回。应该不会这么慢。
						return;
					}
				}
				Point p = parent.getLocationOnScreen();
				SwingUtilities.convertPointFromScreen(p, parent.getRootPane().getLayeredPane());
				BillCellPanel cellPanel = (BillCellPanel) parent;
				int tablewidth = (int) cellPanel.getTable().getVisibleRect().getWidth();
				int tableheight = (int) cellPanel.getTable().getVisibleRect().getHeight();
				Rectangle rect = new Rectangle((tablewidth / 2 - 40), tableheight / 2, 180, 130);
				DivComponentLayoutUtil.getInstanse().addComponentOnDiv(cellPanel, jp, rect, null); // 添加浮动层
			}
		}, 100);
	}

	/*
	 * 加载页签。
	 */
	public void tabPanelLoad(int index) {
		if (!tabOpenHis.containsKey((index) + "")) {
			JPanel jp = (JPanel) tabpane.getComponentAt(index);
			jp.removeAll();
			jp.setLayout(new BorderLayout());
			if (index - beginTabIndex < 0) {
				tabOpenHis.put(index + "", null);
				return;
			}
			JPanel currListPanel = getCheckScoreMainBillCellPanel(index - beginTabIndex);
			jp.add(currListPanel, BorderLayout.CENTER);
		} else {
			// listPanel = tabOpenHis.get(index + "");
		}

	}

	private SalaryBomPanel getPlanBomPanel(HashVO[] _planLogs) {
		for (int i = 0; i < _planLogs.length; i++) {
			_planLogs[i].setToStringFieldName("checkdate");
		}

		planBomPanel.addBomPanel(Arrays.asList(_planLogs));
		planBomPanel.addBomClickListener(new SalaryBomClickListener() {
			public void onBomClickListener(SalaryBomClickEvent event) {
				currLog = event.getHashvo();
				planBomPanel.addBomPanel(loadPanelByLogPlanVO());
			}
		});
		return planBomPanel;
	}

	/*
	 * 得到当前计划对象
	 */
	public HashVO getCurrLog() {
		return currLog;
	}

	public void stateChanged(ChangeEvent changeevent) {
		int selectIndex = tabpane.getSelectedIndex();
		tabPanelLoad(selectIndex);
	}

	public HashVO getHashVO(int _row, String itemKey) {
		Object[] curTabVO = getCurrCheckPageObject();
		Map cellCompareHashVOMap = (Map) curTabVO[4];
		if (!cellCompareHashVOMap.containsKey(_row + "_" + itemKey)) {
			HashVO vo = new HashVO();
			return vo;
		}
		int index = (Integer) cellCompareHashVOMap.get(_row + "_" + itemKey); // 得到
		return currCheckVOs[index];
	}

	/*
	 * 点击提交!
	 */
	private void onSubmit() {
		int currPanelIndex = 0; // 得到当前页面的位置
		if (!otherPanelInit && canchecktype.size() > 1) {
			currPanelIndex = tabpane.getSelectedIndex();
		} else if (otherPanelInit) {
			currPanelIndex = tabpane.getSelectedIndex();
		}
		BillPanel billpanel = (BillPanel) tabOpenHis.get(currPanelIndex + "");
		if (billpanel instanceof BillCellPanel) {
			onCellPanelSubmit((BillCellPanel) billpanel);
			return;
		}
	}

	/*
	 * Excel表单填写提交.
	 */
	private void onCellPanelSubmit(BillCellPanel cellPanel) {
		cellPanel.stopEditing();
		Object[] curTabVO = getCurrCheckPageObject();
		String chekctype = (String) curTabVO[3];
		int lastRowNum = cellPanel.getRowCount();
		if (!ggccTabName.equals(chekctype) && !hideTotle) {
			lastRowNum--; // 如果不是高管垂直，那么最后一行是合计。需要去掉.
		}
		int lastColNum = cellPanel.getColumnCount();
		int alldatanum = 0;
		try {
			int range[] = getInputNumRange();
			for (int i = value_row_start_index; i < lastRowNum; i++) {
				for (int j = value_col_start_index; j < lastColNum; j++) {
					String cellValue = cellPanel.getValueAt(i, j);
					if (cellValue == null || "".equals(cellValue)) {
						MessageBox.show(cellPanel, "部分数据没有填写!");
						cellPanel.getTable().scrollRectToVisible(cellPanel.getTable().getCellRect(i, j, true));
						cellPanel.getTable().setColumnSelectionInterval(j, j);
						cellPanel.getTable().setRowSelectionInterval(i, i);
						return;
					} else {
						try {
							Float value = Float.parseFloat(cellValue); //
							if (value < range[0] || value > range[1]) {// 应该不会存在
								MessageBox.show(cellPanel, "评分值范围" + input_value_range + "之间");
								cellPanel.getTable().scrollRectToVisible(cellPanel.getTable().getCellRect(i, j, true));
								cellPanel.getTable().setColumnSelectionInterval(j, j);
								cellPanel.getTable().setRowSelectionInterval(i, i);
								return;
							}
							alldatanum++;
						} catch (Exception _ex) {
							_ex.printStackTrace();
							MessageBox.show(cellPanel, "填写值范围为" + input_value_range + ",错误数据已用红色标识,请修改后再提交.");
							cellPanel.getTable().scrollRectToVisible(cellPanel.getTable().getCellRect(i, j, true));
							cellPanel.getTable().setColumnSelectionInterval(j, j);
							cellPanel.getTable().setRowSelectionInterval(i, i);
							return;
						}

					}
				}
			}
			if ((Boolean) curTabVO[5]) {
				MessageBox.show(cellPanel, "已经提交!");
				return;
			}
			StringBuffer msgsb = new StringBuffer();
			boolean havesameTotleScore = checkPersonTotleSame(cellPanel, msgsb); // 检查是否有相同的值
			if (havesameTotleScore) {
				MessageBox.show(cellPanel, "部门内部员工互评总分不能相同\r\n" + msgsb);
				return;
			}
			boolean maxSameCount = checkPersonEveryFactorMaxScoreSameCount(cellPanel);
			if (!maxSameCount) {
				return;
			}
			// 需要检测服务器端和客户端是否一直.测试时发现有些数据录入了,但是没有更新到数据库中.
			HashMap undo = UIUtil.getHashMapBySQLByDS(null, "select t1.id,checkscore from  sal_person_check_score t1  where t1.scoreuser='" + userid + "' and t1.logid='" + currLog.getStringValue("id") + "' and t1.targettype!='员工定量指标' and (t1.checkscore is null or t1.checkscore='') and t1.checktype='" + chekctype + "'");
			if (undo.size() > 0) { // 数据库中有空值.页面有数据.需要重新同步页面数据到服务器.
				List updateSqls = new ArrayList();
				for (int i = value_row_start_index; i < lastRowNum; i++) {
					for (int j = value_col_start_index; j < lastColNum; j++) {
						BillCellItemVO itemVO = cellPanel.getBillCellItemVOAt(i, j);
						HashVO hashvo = (HashVO) itemVO.getCustProperty("hashvo");
						if (!undo.containsKey(hashvo.getStringValue("id"))) {
							continue;
						}
						String score = cellPanel.getValueAt(i, j);
						UpdateSQLBuilder sqlBuilder = new UpdateSQLBuilder("sal_person_check_score"); // 重新update一次
						if (hashvo != null) {
							sqlBuilder.setWhereCondition(" id = '" + hashvo.getStringValue("id") + "'");
						} else {
							MessageBox.showWarn(this, "表格[" + convertIntColToEn(j + 1) + "" + (i + 1) + "]数据处理发生严重错误,请尽快联系管理员");
							InsertSQLBuilder insert = new SalaryUIUtil().getErrLogSql("员工打分表格中没取到[hashvo]值", "数据错误", "错误发生在PersonMutualWKPanel类,表格[" + convertIntColToEn(j + 1) + "" + (i + 1) + "]执行getCustProperty方法取不到hashvo值。", "严重");
							if (insert != null) {
								UIUtil.executeUpdateByDS(null, insert);
							}
							return;
						}
						sqlBuilder.putFieldValue("lasteditdate", UIUtil.getServerCurrDate());
						sqlBuilder.putFieldValue("checkscore", score);
						sqlBuilder.putFieldValue("status", "待提交");
						updateSqls.add(sqlBuilder);
					}
				}
				UIUtil.executeBatchByDS(null, updateSqls, true, false);
				// 再重新查一次 数据库
				String strtotle = UIUtil.getStringValueByDS(null, "select count(t1.id) from  sal_person_check_score t1  where t1.scoreuser='" + userid + "' and t1.logid='" + currLog.getStringValue("id") + "' and t1.targettype!='员工定量指标' and (t1.checkscore is null or t1.checkscore='') and t1.checktype='" + chekctype + "'");
				if (!"0".equals(strtotle)) { // 上面把数据库中的空值补上后，再查一遍，在不一样就不管了。直接刷新页面数据。用户自己搞。
					currCheckVOs = UIUtil.getHashVoArrayByDS(null, "select t1.*,t2.catalog from  sal_person_check_score t1 left join sal_person_check_list t2 on t1.targetid=t2.id where t1.scoreuser='" + userid + "' and t1.logid='" + currLog.getStringValue("id") + "' and t1.targettype!='员工定量指标' order by t1.checktype,t2.catalog,t1.targetid,t1.id ");
					cellPanel.loadBillCellData(getBillCellVo());// 重新加载服务器端数据
					MessageBox.show(cellPanel, "页面数据未能及时同步到服务器,数据已经刷新,请重新核实遗漏项.");
					return;
				}
			}
			if (!MessageBox.confirm(cellPanel, "提交后不能修改，确定提交吗？")) {
				return;
			}
			UpdateSQLBuilder updateSql = new UpdateSQLBuilder("sal_person_check_score");
			updateSql.putFieldValue("status", "已提交");
			updateSql.setWhereCondition(" logid='" + currLog.getStringValue("id") + "' and checktype='" + chekctype + "' and scoreuser='" + userid + "'");
			int num = UIUtil.executeUpdateByDS(null, updateSql);
			if (num == alldatanum) {
				curTabVO[5] = Boolean.TRUE;// 设置执行标志
				cellPanel.setEditable(false);
				setCurrCellPanelStatus(cellPanel, "已 提 交");
				String[][] result = UIUtil.getStringArrayByDS(null, "select count(id),checktype from sal_person_check_score where scoreuser = " + userid + " and (status <> '已提交' or status is null) and logid=" + currLog.getStringValue("id") + " group by checktype" + " union all select count(id),targettype checktype from sal_dept_check_score where  scoreuser = " + userid
						+ " and targettype='部门定性指标' and (status <> '已提交' or status is null) and logid=" + currLog.getStringValue("id") + " group by targettype");
				StringBuffer msg = new StringBuffer("您还没有对 ");
				boolean have = false;
				for (int i = 0; i < result.length; i++) {
					if (!"0".equals(result[i][0])) {
						msg.append(result[i][1] + "、");
						have = true;
					}
				}
				if (have) {
					msg = new StringBuffer(msg.substring(0, msg.length() - 1));
				}
				if (have) {
					msg.append(" 进行评分或者提交，请点击【左上方页签】完成操作.");
					MessageBox.show(cellPanel, msg.toString());
				} else {
					MessageBox.show(cellPanel, "您已经完成了本次所有打分内容,谢谢！");
				}
			}
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void actionPerformed(ActionEvent actionevent) {
		WLTButton clickButton = (WLTButton) actionevent.getSource();
		if (clickButton.getText().equals("修改")) {
			JLabel l = new JLabel("修改暂时不支持");
			l.setBackground(Color.BLUE);
			Rectangle rect = new Rectangle(100, -25, 100, 30);
			int currPanelIndex = 0; // 得到当前页面的位置
			if (canchecktype.size() > 1) { // 就一个页签
				currPanelIndex = tabpane.getSelectedIndex();
			}
			BillPanel billpanel = (BillPanel) tabOpenHis.get(currPanelIndex + "");
			DivComponentLayoutUtil.getInstanse().addComponentOnDiv(billpanel, l, rect, null);
		} else {
			onSubmit();
		}
	}

	// 把数字转换成英文编码
	private String convertIntColToEn(int _index) {
		StringBuffer sb = new StringBuffer();
		int in = _index / 26;
		if (in == 0) { // 如果是最后一层
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
	 * 得到当前评分页面的对象数组。{被评分用户Map，一行指标对应的打分Map，总分map，该页签名称，每一个表格对应的HashVO序列号}
	 */
	public Object[] getCurrCheckPageObject() {
		Object[] curTabVO = null;
		if (canchecktype.size() > 1) {
			curTabVO = (Object[]) canchecktype.get(tabpane.getSelectedIndex() - beginTabIndex); // 当前页签对应的内容
		} else {
			curTabVO = (Object[]) canchecktype.get(0);
		}
		return curTabVO;
	}

	/*
	 * 高管打分界面。
	 */
	public BillCellVO getGGCellVO() {
		BillCellVO cellVO = new BillCellVO();
		Object obj[] = getCurrCheckPageObject();
		List<HashVO> targetVOList = (ArrayList<HashVO>) obj[0];
		HashVO vos[] = targetVOList.toArray(new HashVO[0]);

		TBUtil.getTBUtil().sortHashVOs(vos, new String[][] { { "checkeduser", "N", "N" } }); // 先按照人员排序。
		int colLength = 3;
		// List userList = new ArrayList(); // 记录当前人员评的人员ID和名称
		List<BillCellItemVO[]> cellVOs = new ArrayList<BillCellItemVO[]>();
		List<BillCellItemVO> titleRow = new ArrayList<BillCellItemVO>();
		BillCellItemVO cell_title_1 = getBillCellVO_Blue(currLog.getStringValue("name")); // 计划名称
		cell_title_1.setHalign(2); //
		cell_title_1.setFontsize("15");
		cell_title_1.setFontstyle("1");
		cell_title_1.setSpan("1," + colLength);
		titleRow.add(cell_title_1);
		titleRow.add(getBillCellVO_Normal(""));
		// 第一行
		List<BillCellItemVO> firstRow = new ArrayList<BillCellItemVO>();
		BillCellItemVO cell_1_1 = getBillCellVO_Blue("被考核人");
		cell_1_1.setHalign(2); //
		cell_1_1.setSpan("1,1");
		firstRow.add(cell_1_1);

		BillCellItemVO cell_1_2 = getBillCellVO_Blue("考核内容"); //
		cell_1_2.setColwidth("400");
		firstRow.add(cell_1_2);		
		// BillCellItemVO cell_1_3 = getBillCellVO_Blue("权重(%)"); // 表头
		// firstRow.add(cell_1_3);

		BillCellItemVO cell_1_4 = getBillCellVO_Blue("打分"); // 表头
		firstRow.add(cell_1_4);
		// 最后一行
		cellVOs.add(titleRow.toArray(new BillCellItemVO[0]));
		cellVOs.add(firstRow.toArray(new BillCellItemVO[0]));
		// 第二行
		int rowIndex = 2;
		HashMap<String, Float> xiaoji = new HashMap<String, Float>(); // 小计
		String flag = ""; // 标记
		if (vos.length > 0) {
			flag = vos[0].getStringValue("checkeduser");
		}
		int beginRowIndex = rowIndex; // 开始位置。
		for (int i = 0; i < vos.length; i++) {
			HashVO targetVO = vos[i]; // 得到没一个vo
			String checkUserID = targetVO.getStringValue("checkeduser");
			BillCellItemVO cell_N_1 = getBillCellVO_Blue(targetVO.getStringValue("checkedusername"));
			cell_N_1.setValign(2);
			cell_N_1.setHalign(2);
			String targetName = targetVO.getStringValue("targetName");
			BillCellItemVO cell_N_2 = getBillCellVO_Blue(targetName);
			cell_N_2.setValign(2);
			cell_N_2.setCelltype("TEXTAREA");
			cell_N_2.setRowheight(getRowMaxHeight(targetName, 400) + "");
			String weights = targetVO.getStringValue("weights");
			// BillCellItemVO cell_N_3 = getBillCellVO_Blue(weights); // 权重
			List<BillCellItemVO> row_N_VO = new ArrayList();
			row_N_VO.add(cell_N_1);
			row_N_VO.add(cell_N_2);
			// row_N_VO.add(cell_N_3);

			BillCellItemVO cell_N = getBillCellVO_Normal(targetVO.getStringValue("checkscore"));// 显示分数
			cell_N.setCustProperty("hashvo", targetVO);
			cell_N.setEditformula("AddItemValueChangedListener(\"cn.com.pushworld.salary.ui.person.p020.PersonMutualCheckValueChangeLinstener\",\"Cell执行\")");
			cell_N.setCelltype("NUMBERTEXT");
			row_N_VO.add(cell_N);
			if (weights != null && !weights.equals("")) {
				if (xiaoji.containsKey(checkUserID)) { // 用户ID
					Float oldSum = xiaoji.get(checkUserID);
					oldSum += Float.parseFloat(weights);
					xiaoji.put(checkUserID, oldSum);
				} else {
					Float oldSum = Float.parseFloat(weights);
					xiaoji.put(checkUserID, oldSum);
				}
			}
			if (!checkUserID.equals(flag)) {
				beginRowIndex = rowIndex;
			}
			flag = checkUserID;
			cellVOs.add(row_N_VO.toArray(new BillCellItemVO[0]));
			// rowIndex++;
			if (i != vos.length - 1) { // 如果不是最后一个
				String nextUserID = vos[i + 1].getStringValue("checkeduser");
				if (nextUserID.equals(flag)) { // 如果下一个是同一个人。
					continue;
				}
			}
			BillCellItemVO xiaoji_1 = getBillCellVO_Blue("小计");
			xiaoji_1.setSpan("1,2");
			// BillCellItemVO xiaoji_2 = getBillCellVO_Blue(""); // 空白
			// BillCellItemVO xiaoji_3 =
			// getBillCellVO_Blue(String.valueOf(xiaoji.get(flag))); // 空白
			// List<BillCellItemVO> cell_xiaoji = new
			// ArrayList<BillCellItemVO>();
			// cell_xiaoji.add(xiaoji_1);
			// cell_xiaoji.add(xiaoji_2);
			// cell_xiaoji.add(xiaoji_3);
			// cell_xiaoji.add(getBillCellVO_Blue(""));
			// cellVOs.add(cell_xiaoji.toArray(new BillCellItemVO[0])); //

			if (obj[1] == null) {
				HashMap userXiaojiRowIndex = new HashMap();
				userXiaojiRowIndex.put(checkUserID, new int[] { beginRowIndex, rowIndex }); // 开始和结束
				obj[1] = userXiaojiRowIndex;
			} else {
				HashMap userXiaojiRowIndex = (HashMap) obj[1];
				userXiaojiRowIndex.put(checkUserID, new int[] { beginRowIndex, rowIndex });
			}
			rowIndex++;
		}
		// 总计
		BillCellItemVO[][] cellitemvos = cellVOs.toArray(new BillCellItemVO[0][0]);
		// formatSpan(cellitemvos, new int[] { 0, 1 });
		// if (obj[1] != null) {
		// HashMap userXiaojiRowIndex = (HashMap) obj[1];
		// for (Iterator iterator = userXiaojiRowIndex.entrySet().iterator();
		// iterator.hasNext();) {
		// Entry userAndXiaojiRowIndex = (Entry) iterator.next();
		// String userid = (String) userAndXiaojiRowIndex.getKey();
		// int[] index = (int[]) userAndXiaojiRowIndex.getValue();
		// cellitemvos[index[1]][0].setSpan("1,2");
		// cellitemvos[index[1]][0].setHalign(2);
		// }
		// }
		cellVO.setCellItemVOs(cellitemvos);
		cellVO.setRowlength(rowIndex); // 加上表头。
		cellVO.setCollength(3);
		return cellVO;
	}

	/*
	 * 根据当前的评分类型(中层，一般员工)获取CellVO
	 */
	public BillCellVO getBillCellVo() {
		BillCellVO cellVO = new BillCellVO();
		Object obj[] = getCurrCheckPageObject();
		String tabName = (String) obj[3];
		// 调用 高管
		if (ggccTabName.equals(tabName)) {
			return getGGCellVO();
		}
		LinkedHashMap userset = (LinkedHashMap) obj[0];
		LinkedHashMap scorevomap = (LinkedHashMap) obj[1];
		int colLength = userset.size() + 2;// 总列数
		int rowLength = scorevomap.size() + (hideTotle ? 2 : 3); // 总行数
		// List userList = new ArrayList(); // 记录当前人员评的人员ID和名称
		List<BillCellItemVO[]> cellVOs = new ArrayList<BillCellItemVO[]>();

		List<BillCellItemVO> titleRow = new ArrayList<BillCellItemVO>();
		BillCellItemVO cell_title_1 = getBillCellVO_Blue(currLog.getStringValue("name")); // 计划名称
		cell_title_1.setHalign(2); //
		cell_title_1.setFontsize("15");
		cell_title_1.setFontstyle("1");
		cell_title_1.setSpan("1," + colLength);
		titleRow.add(cell_title_1);
		titleRow.add(getBillCellVO_Normal(""));
		// 第一行
		List<BillCellItemVO> firstRow = new ArrayList<BillCellItemVO>();
		BillCellItemVO cell_1_1 = getBillCellVO_Blue("考核内容");
		cell_1_1.setHalign(2);
		cell_1_1.setSpan("1,2");
		cell_1_1.setFontstyle("1");
		firstRow.add(cell_1_1);

		BillCellItemVO cell_1_2 = getBillCellVO_Blue(""); // 空表头
		firstRow.add(cell_1_2);
		// BillCellItemVO cell_1_3 = getBillCellVO_Blue("权重(%)"); // 表头
		// firstRow.add(cell_1_3);
		// 最后一行
		List<BillCellItemVO> lastRowVO = new ArrayList<BillCellItemVO>();
		BillCellItemVO last_1_1 = getBillCellVO_Blue("总计");
		last_1_1.setIshtmlhref("Y");
		last_1_1.setForeground("255,69,0");
		last_1_1.setFonttype("1");
		last_1_1.setCellhelp("总分数算法：加权平均(分数A*权重A+分数B*权重B+...+分数Z*权重Z)/(权重A+权重B+...+权重Z)");
		last_1_1.setSpan("1,2");
		lastRowVO.add(last_1_1);
		BillCellItemVO last_1_2 = getBillCellVO_Blue(""); // 空格
		lastRowVO.add(last_1_2);

		// BillCellItemVO last_1_3 = getBillCellVO_Blue("");// 空格
		// lastRowVO.add(last_1_3);

		for (Iterator iterator = userset.entrySet().iterator(); iterator.hasNext();) {
			titleRow.add(getBillCellVO_Normal(""));
			Entry object = (Entry) iterator.next();
			String[] user = (String[]) object.getValue();
			BillCellItemVO cell_1 = getBillTitleCellItemVO(user[1]); // 显示用户名称
			cell_1.setIseditable("N");
			cell_1.setHalign(2);
			firstRow.add(cell_1);
			BillCellItemVO cell_last = getBillTitleCellItemVO(""); // 最后一行的数据。
			cell_last.setIseditable("N");
			lastRowVO.add(cell_last);
		}
		cellVOs.add(titleRow.toArray(new BillCellItemVO[0]));
		cellVOs.add(firstRow.toArray(new BillCellItemVO[0]));
		// 第二行
		int rowIndex = 0;
		float weightcount = 0;
		String maxLengthCatalog = ""; // 内容分类文字长度
		String maxLengthTargetName = ""; // 考核指标内容文字长度
		for (Iterator iterator = scorevomap.entrySet().iterator(); iterator.hasNext();) {
			Entry object = (Entry) iterator.next();
			HashVO targetVO = (HashVO) object.getValue();
			String catalog = targetVO.getStringValue("catalog");
			if (catalog.length() > maxLengthCatalog.length()) {
				maxLengthCatalog = catalog;
			}
			BillCellItemVO cell_N_1 = getBillCellVO_Blue(catalog);
			cell_N_1.setHalign(2);
			String targetName = targetVO.getStringValue("targetName");
			BillCellItemVO cell_N_2 = getBillCellVO_Blue(targetVO.getStringValue("targetName"));
			cell_N_2.setCelltype("TEXTAREA");
			cell_N_2.setValign(2);
			int length = tbutil.getStrWidth(cell_N_2.getCellvalue());
			if (length > 600) {
				cell_N_2.setRowheight("" + (length / 600 + 1) * 23);
			}
			String weights = targetVO.getStringValue("weights");
			if (targetName.length() > maxLengthTargetName.length()) {
				maxLengthTargetName = targetName;
			}
			if (weights != null && !"".equals(weights)) {
				weightcount += Float.parseFloat(weights);
			}
			// BillCellItemVO cell_N_3 = getBillCellVO_Blue(weights); // 权重
			List<BillCellItemVO> row_N_VO = new ArrayList();
			row_N_VO.add(cell_N_1);
			row_N_VO.add(cell_N_2);
			// row_N_VO.add(cell_N_3);
			for (Iterator iterator2 = userset.keySet().iterator(); iterator2.hasNext();) {
				String userid = (String) iterator2.next();
				BillCellItemVO cell_N = getBillCellVO_Normal(targetVO.getStringValue(userid));// 显示分数
				cell_N.setHalign(2);
				cell_N.setCustProperty("hashvo", getHashVO(rowIndex, userid));
				cell_N.setCelldesc("纯数字");
				cell_N.setEditformula("AddItemValueChangedListener(\"cn.com.pushworld.salary.ui.person.p020.PersonMutualCheckValueChangeLinstener\",\"Cell执行\")");
				cell_N.setCelltype("NUMBERTEXT");
				row_N_VO.add(cell_N);
			}
			cellVOs.add(row_N_VO.toArray(new BillCellItemVO[0]));
			rowIndex++;
		}
		// 设置最宽长度
		cell_1_1.setColwidth(TBUtil.getTBUtil().getStrWidth(maxLengthCatalog) + 30 + "");
		int length = TBUtil.getTBUtil().getStrWidth(maxLengthTargetName);
		if (length > 600) {
			cell_1_2.setColwidth((600) + "");
		} else {
			cell_1_2.setColwidth(TBUtil.getTBUtil().getStrWidth(maxLengthTargetName) + 30 + "");
		}
		// last_1_3.setCellvalue(weightcount + "");
		// 总计
		if (!hideTotle) {
			cellVOs.add(lastRowVO.toArray(new BillCellItemVO[0]));
		}
		BillCellItemVO[][] cellitemvos = cellVOs.toArray(new BillCellItemVO[0][0]);
		formatSpan(cellitemvos, new int[] { 0 });

		cellitemvos[1][0].setSpan("1,2"); // 重新合并表头(考核内容)
		if (!hideTotle) {
			cellitemvos[cellitemvos.length - 1][0].setSpan("1,2"); // 合并最后一行的（总计）
			cellitemvos[cellitemvos.length - 1][0].setHalign(2);
		}
		cellVO.setCellItemVOs(cellitemvos);
		cellVO.setRowlength(rowLength);
		cellVO.setCollength(colLength);
		return cellVO;
	}

	/*
	 * 得到换行后的文本框高度。
	 */
	private int getRowMaxHeight(String str_value, int _columnWidth) {
		if (str_value != null) {
			int li_worllength = TBUtil.getTBUtil().getStrWidth(str_value); // 所有字符的长度(像素)，如果输入了换行则获得的行数就不准了！！！
			int height = Toolkit.getDefaultToolkit().getFontMetrics(LookAndFeel.font).getHeight();
			int li_rows = li_worllength / _columnWidth + 1; //
			if (li_rows == 1) {
				return 22;
			}
			return li_rows * height;
		}
		return 22;// 多行文本框换行后，一行的高度要比22像素小些，如果在加上html链接，行高就更小了？李春娟修改！
	}

	/*
	 * 蓝色背景的表格
	 */
	private BillCellItemVO getBillCellVO_Blue(String value) {
		BillCellItemVO item = new BillCellItemVO();
		item.setCellvalue(value);
		item.setBackground("191,213,255");
		item.setFonttype("新宋体");
		item.setFontsize("12");
		item.setFontstyle("0");
		item.setSpan("1,1");
		item.setIseditable("N");
		item.setCellhelp(null);
		return item;
	}

	/*
	 * 默认白色
	 */
	private BillCellItemVO getBillCellVO_Normal(String value) {
		BillCellItemVO item = new BillCellItemVO();
		item.setCellvalue(value);
		item.setFonttype("新宋体");
		item.setFontsize("12");
		item.setFontstyle("0");
		item.setSpan("1,1");
		item.setCellhelp(null);
		return item;
	}

	/*
	 * 标题
	 */
	private BillCellItemVO getBillTitleCellItemVO(String value) {
		BillCellItemVO item = new BillCellItemVO();
		item.setCellvalue(value);
		item.setBackground("191,213,0");
		item.setFonttype("新宋体");
		item.setFontsize("12");
		item.setFontstyle("1");
		item.setSpan("1,1");
		item.setIseditable("N");
		item.setCellhelp(null);
		return item;
	}

	/*
	 * 重置某一列的总计
	 */
	public void resetColSum(BillCellPanel cellPanel, int _colIndex) {
		Object obj[] = getCurrCheckPageObject();
		if (ggccTabName.equals(obj[3]) || hideTotle) { // 如果是董事长对高管就行评分,不需要小计.
			return;
		}
		int lastRow = cellPanel.getRowCount() - 1;
		float sum = 0;
		StringBuffer help = new StringBuffer();
		help.append("(");
		float weightsSum = 0f;// Float.parseFloat(cellPanel.getBillCellItemVOAt(lastRow,
		// 2).getCellvalue()); // 权重和
		for (int i = value_row_start_index; i < lastRow; i++) {
			HashVO hvo = (HashVO) cellPanel.getBillCellItemVOAt(i, 2).getCustProperty("hashvo");
			String weights = hvo.getStringValue("weights");// cellPanel.getBillCellItemVOAt(i,
			// 2).getCellvalue();
			String cellValue = cellPanel.getBillCellItemVOAt(i, _colIndex).getCellvalue();
			try {
				if (cellValue != null && !cellValue.equals("")) {
					sum += Float.parseFloat(cellValue) * Float.parseFloat(weights);
					help.append(Float.parseFloat(cellValue) + "*" + Float.parseFloat(weights) + "+");
				}
			} catch (Exception ex) {
				MessageBox.show("请重新填写" + convertIntColToEn(_colIndex + 1) + "" + (i + 1));
				cellPanel.getTable().scrollRectToVisible(cellPanel.getTable().getCellRect(i, _colIndex, true));
				cellPanel.getTable().setColumnSelectionInterval(_colIndex, _colIndex);
				cellPanel.getTable().setRowSelectionInterval(i, i);
				return;
			}
			weightsSum += Float.parseFloat(weights);
		}
		if (help.length() > 2) {
			help = new StringBuffer(help.substring(0, help.length() - 1));
		}
		help.append(")");
		help.append("/" + weightsSum);
		float lastsum = (sum / weightsSum);
		BillCellItemVO sumitemvo = cellPanel.getBillCellItemVOAt(cellPanel.getRowCount() - 1, _colIndex);
		if (lastsum == 0) {
			sumitemvo.setCellhelp(null);
			sumitemvo.setCellvalue("");
		} else {
			sumitemvo.setCellvalue(String.format("%." + 2 + "f", lastsum));
		}
		// 检查是否有重复的分值
		checkPersonTotleSame(cellPanel, new StringBuffer());
	}

	/*
	 * 打分人对每一个指标评分，同一指标不同人员满分(最高分雷同)数量进行控制。如[1-4]人，某指标只能有1人为10分。
	 */
	private boolean checkPersonEveryFactorMaxScoreSameCount(BillCellPanel cellPanel) {
		// 此方法目前一定要与【打分界面是否隐藏总计】参数不同时执行。
		if (!hideTotle) {// 用于总分不能雷同。
			return true;
		}
		int range[] = getInputNumRange();
		int lastColNum = cellPanel.getColumnCount();
		int lastRowNum = cellPanel.getRowCount();
		int percount = lastColNum - value_col_start_index;
		String maxscorecount = tbutil.getSysOptionStringValue("同一指标不同人员满分(最高分雷同)数量进行控制", null);// 取出参数，解析各个人数段对应的最高分数量。
		if (tbutil.isEmpty(maxscorecount)) { //不细心，这个参数系统默认是不设置的。
			return true;
		}
		String defcount = salaryTBUtil.getScoreConfigMinVal(maxscorecount, percount);
		int maxcount = 0;
		if (defcount.contains("%")) {
			Double maxde = 0.4 * percount;
			maxcount = Integer.parseInt(format(0, maxde));
		} else {
			maxcount = Integer.parseInt(defcount);
		}
		String isMAx = tbutil.getSysOptionStringValue("同一指标不同人员满分分雷同数量控制", "");
		if (isMAx.equals("")) {
			return true;
		}
		int maxscore = 0;
		try {
			for (int i = value_row_start_index; i < lastRowNum; i++) {// 遍历每一行，进行判断。给出友情提示
				int count = 0;
				if (isMAx.equalsIgnoreCase("N")) {// 判断是取满分还是最大值
					maxscore = range[1];
				} else {
					maxscore = 0;
				}
				for (int j = value_col_start_index; j < lastColNum; j++) {
					String colValue = cellPanel.getValueAt(i, j);
					int score = Integer.parseInt(colValue);
					if (score > maxscore) {
						maxscore = score;
						count = 1;
					} else if (score == maxscore) {
						count++;
					}
				}
				if (count > maxcount) {
					MessageBox.show(cellPanel, "您赋予指标【" + cellPanel.getValueAt(i, value_row_start_index - 1) + "】的最高分为:" + maxscore + "分,\n得到该分者不得超过" + maxcount + "人(" + percount + "人的" + defcount + ");请调整后提交");
					return false;
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 四舍五入
	 */
	public String format(int size, double number) {
		StringBuffer formatString = new StringBuffer("0");
		if (size > 0) {
			formatString.append(".");
		}
		for (int i = 0; i < size; i++) {
			formatString.append("#");
		}
		DecimalFormat df = new DecimalFormat(formatString.toString());
		return df.format(number);
	}

	/*
	 * 检查是否有相同的分数总和
	 */
	private boolean checkPersonTotleSame(BillCellPanel cellPanel, StringBuffer samemsg) {
		if (samemsg == null) {
			samemsg = new StringBuffer();
		}
		if (hideTotle) {
			return false;
		}
		int lastColNum = cellPanel.getColumnCount();
		int lastRowNum = cellPanel.getRowCount() - 1;
		Object obj[] = getCurrCheckPageObject();
		HashMap sameMap = new HashMap();// 记录相同
		try {
			if ("一般人员".equals(obj[3])) { // 一般人员需要校验是否有同样分值的。
				for (int i = value_col_start_index; i < lastColNum; i++) {
					String cellValue = cellPanel.getValueAt(lastRowNum, i);
					if (tbutil.isEmpty(cellValue)) {
						continue;
					}
					if (sameMap.containsKey(cellValue)) {
						List cells = (List) sameMap.get(cellValue);
						cells.add(i);
					} else {
						List cells = new ArrayList();
						cells.add(i);// 把列加进来
						sameMap.put(cellValue, cells);
					}
					BillCellItemVO itemvo = cellPanel.getBillCellItemVOAt(lastRowNum, i); // 恢复本质颜色
					itemvo.setCellhelp(null);
					itemvo.setBackground("255,255,255");

				}
			} else {
				return false;
			}
			boolean have = false;
			int index = 0; // 有多少对重复的

			if (sameMap.size() > 0) {
				for (Iterator iterator = sameMap.entrySet().iterator(); iterator.hasNext();) {
					Entry<String, List> type = (Entry<String, List>) iterator.next();
					String cellvalue = type.getKey();
					List cells = type.getValue();
					if (cells.size() < 2) {
						continue;
					}
					have = true;
					StringBuffer cellHelp = new StringBuffer();
					for (int i = 0; i < cells.size(); i++) {
						samemsg.append(convertIntColToEn((Integer) cells.get(i) + 1));
						cellHelp.append(convertIntColToEn((Integer) cells.get(i) + 1));
						if (i < cells.size() - 1) {
							samemsg.append("、");
							cellHelp.append("、");
						}
						int colindex = (Integer) cells.get(i);
						BillCellItemVO itemvo = cellPanel.getBillCellItemVOAt(lastRowNum, colindex);
						itemvo.setBackground(getColor(index));
					}
					samemsg.append("总计值不能重复,目前均为:" + cellvalue + "\r\n");
					cellHelp.append("总计值不能重复,目前均为:" + cellvalue + "\r\n");
					for (int i = 0; i < cells.size(); i++) {
						int colindex = (Integer) cells.get(i);
						BillCellItemVO itemvo = cellPanel.getBillCellItemVOAt(lastRowNum, colindex);
						itemvo.setCellhelp(cellHelp.toString());
					}
					index++;
				}
				return have;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	private String getColor(int index) {
		String[] beautifulColors = new String[] { "255,80,0", "74,63,228",//
				"254,0,254", "169,143,80", "128,0,254", "0,128,0", "254,254,128", "128,0,128", "64,0,128", "128,128,0", "128,128,192", "254,128,192", "0,128,254", "128,64,0",//
				"225,213,218", "255,30,0", "200,234,210", "224,242,26", "231,192,229", "98,69,77", "192,188,226", "73,129,90", "241,244,210",//
				"225,39,214", "195,229,226", "0,254,254", "225,48,94", "100,99,108", "42,231,99", "153,159,87", "96,69,94", "74,143,138", "244,178,18" }; // 一系列好看的颜色,一共25个,依次交错排序
		return beautifulColors[index % 28];
	}

	public void autoCheckIFCanCommit() {
		if (!autoCheckCommit) {// 是否需要自动检测
			return;
		}
		Object[] curTabVO = getCurrCheckPageObject();
		String chekctype = (String) curTabVO[3];
		int currPanelIndex = 0; // 得到当前页面的位置
		if (!otherPanelInit && canchecktype.size() > 1) {
			currPanelIndex = tabpane.getSelectedIndex();
		} else if (otherPanelInit) {
			currPanelIndex = tabpane.getSelectedIndex();
		}
		final BillCellPanel cellPanel = (BillCellPanel) tabOpenHis.get(currPanelIndex + "");
		int lastColNum = cellPanel.getColumnCount();
		int lastRowNum = cellPanel.getRowCount();
		if (!ggccTabName.equals(chekctype) && !hideTotle) {
			lastRowNum--; // 如果不是高管垂直，那么最后一行是合计。需要去掉.
		}
		int alldatanum = 0;
		int range[] = getInputNumRange();
		try {
			for (int i = value_row_start_index; i < lastRowNum; i++) {
				for (int j = value_col_start_index; j < lastColNum; j++) {
					String cellValue = cellPanel.getValueAt(i, j);
					if (cellValue == null || "".equals(cellValue)) {
						return;
					} else {
						try {
							Float value = Float.parseFloat(cellValue); //
							if (value < range[0] || value > range[1]) {// 应该不会存在
								return;
							}
							alldatanum++;
						} catch (Exception _ex) {
							return;
						}

					}
				}
			}
			// 全部填写完了。
			StringBuffer msgsb = new StringBuffer();
			boolean havesameTotleScore = checkPersonTotleSame(cellPanel, msgsb); // 检查是否有相同的值
			if (havesameTotleScore) {
				return;
			}
			new Timer().schedule(new TimerTask() {
				public void run() {
					String msg = "当前页面已评分完毕,是否提交结果?";
					int index = MessageBox.showOptionDialog(cellPanel, msg, "系统提示", new String[] { "立即提交", "稍后提交" });
					if (index != 0) {
						autoCheckCommit = false;// 如果不喜欢自动提示。设置为false。
						return;
					}
					onCellPanelSubmit(cellPanel);
				}
			}, 300); // 延迟一会儿再弹出来.感受好一些。
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// 得到可以录入值的范围,两个长度的数组.
	public int[] getInputNumRange() {
		String[] values = TBUtil.getTBUtil().split(input_value_range, "-");
		int begin = 0;
		int end = 10;
		try {
			if (values.length == 2) {
				begin = Integer.parseInt(values[0]);
				end = Integer.parseInt(values[1]);
				if (begin >= end) { // 如果值前后顺序有问题。
					begin = 0;
					end = 10;
				}
			}
		} catch (Exception _ex) {
			logger.error("", _ex);
		}
		return new int[] { begin, end };
	}

	public void markTargetScoresByName(String checktype, String[] targetname, String _color) {
		if (tbutil.isEmpty(_color)) {
			_color = "246,53,89";
		}
		if (tabpane != null) {
			int tabindex = -1;
			if (canchecktype != null) {
				for (int i = 0; i < canchecktype.size(); i++) {
					Object[] obj = (Object[]) canchecktype.get(i);
					if (obj != null) {
						String ctype = (String) obj[3];
						if (ctype != null && ctype.equals(checktype)) {
							tabindex = i;
							break;
						}
					}
				}
			}
			if (tabindex >= 0) {
				BillCellPanel cellp = (BillCellPanel) tabOpenHis.get((tabindex + beginTabIndex) + "");
				if (cellp != null) {
					int rowcount = cellp.getRowCount();
					int colcount = cellp.getColumnCount();
					HashMap<String, Integer> ind = new HashMap<String, Integer>();

					for (int i = 2; i < rowcount; i++) {
						try {
							String value = cellp.getValueAt(i, 1);
							ind.put(value, (Integer) i);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					for (int i = 0; i < targetname.length; i++) {
						String onetagetname = targetname[i];
						Integer index = ind.get(onetagetname);
						if (index != null && index > 0) {
							for (int j = 1; j < colcount; j++) {
								BillCellItemVO cellvo = cellp.getBillCellItemVOAt(index, j);
								cellvo.setBackground(_color);
							}
						}
					}

				}
			}
		}
	}
}

/*
 * 自定义人员评分TMO,表头列根据传入'人数'决定。适用于一般员工和高管
 */
class PersonMutualTMO extends AbstractTMO {
	String[][] personVos;

	private PersonMutualTMO() {
	}

	public PersonMutualTMO(String[][] _persons) { // id,name
		personVos = _persons;
	}

	@Override
	public HashVO getPub_templet_1Data() {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", "SAL_PERSON_CHECK_SCORE_CODE6"); // 模版编码,请勿随便修改
		vo.setAttributeValue("templetname", "员工互相考评"); // 模板名称
		vo.setAttributeValue("templetname_e", ""); // 模板名称
		vo.setAttributeValue("tablename", "WLTDUAL"); // 查询数据的表(视图)名
		vo.setAttributeValue("pkname", "ID"); // 主键名
		vo.setAttributeValue("pksequencename", "S_SAL_PERSON_CHECK_SCORE"); // 序列名
		vo.setAttributeValue("savedtablename", "SAL_PERSON_CHECK_SCORE"); // 保存数据的表名
		vo.setAttributeValue("CardWidth", "577"); // 卡片宽度
		vo.setAttributeValue("Isshowlistpagebar", "N"); // 列表是否显示分页栏
		vo.setAttributeValue("Isshowlistopebar", "N"); // 列表是否显示操作按钮栏
		vo.setAttributeValue("listcustpanel", null); // 列表自定义面板
		vo.setAttributeValue("cardcustpanel", null); // 卡片自定义面板

		vo.setAttributeValue("TREEPK", "id"); // 列表是否显示操作按钮栏
		vo.setAttributeValue("TREEPARENTPK", ""); // 列表是否显示操作按钮栏
		vo.setAttributeValue("Treeviewfield", ""); // 列表是否显示操作按钮栏
		vo.setAttributeValue("Treeisshowtoolbar", "Y"); // 树型显示工具栏
		vo.setAttributeValue("Treeisonlyone", "N"); // 树型显示工具栏
		vo.setAttributeValue("Treeseqfield", "seq"); // 列表是否显示操作按钮栏
		vo.setAttributeValue("Treeisshowroot", "Y"); // 列表是否显示操作按钮栏
		return vo;
	}

	@Override
	public HashVO[] getPub_templet_1_itemData() {
		List itemvoList = new ArrayList();

		HashVO itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "ID"); // 唯一标识,用于取数与保存 //指标ID
		itemVO.setAttributeValue("itemname", "主键"); // 显示名称
		itemVO.setAttributeValue("itemname_e", "Id"); // 显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型
		itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
		itemVO.setAttributeValue("refdesc", null); // 参照定义
		itemVO.setAttributeValue("issave", "Y"); // 是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); // 加载公式
		itemVO.setAttributeValue("editformula", null); // 编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
		itemVO.setAttributeValue("listwidth", "145"); // 列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); // 卡片时宽度
		itemVO.setAttributeValue("listisshowable", "N"); // 列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "1"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); // 卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "N");
		itemvoList.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "catalog"); // 唯一标识,用于取数与保存 //指标ID
		itemVO.setAttributeValue("itemname", "分类"); // 显示名称
		itemVO.setAttributeValue("itemname_e", "Id"); // 显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型
		itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
		itemVO.setAttributeValue("refdesc", null); // 参照定义
		itemVO.setAttributeValue("issave", "N"); // 是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); // 加载公式
		itemVO.setAttributeValue("editformula", null); // 编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
		itemVO.setAttributeValue("listwidth", "80"); // 列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); // 卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); // 卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "N");
		itemvoList.add(itemVO);

		itemVO = new HashVO();
		itemVO.setAttributeValue("itemkey", "targetName"); // 唯一标识,用于取数与保存
		// //指标ID
		itemVO.setAttributeValue("itemname", "考核内容"); // 显示名称
		itemVO.setAttributeValue("itemname_e", "Id"); // 显示名称
		itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型
		itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
		itemVO.setAttributeValue("refdesc", null); // 参照定义
		itemVO.setAttributeValue("issave", "N"); // 是否参与保存(Y,N)
		itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
		itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
		itemVO.setAttributeValue("loadformula", null); // 加载公式
		itemVO.setAttributeValue("editformula", null); // 编辑公式
		itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
		itemVO.setAttributeValue("listwidth", "80"); // 列表是宽度
		itemVO.setAttributeValue("cardwidth", "150"); // 卡片时宽度
		itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
		itemVO.setAttributeValue("listiseditable", "4"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("cardisshowable", "N"); // 卡片时是否显示(Y,N)
		itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
		itemVO.setAttributeValue("iswrap", "N");
		itemvoList.add(itemVO);

		for (int i = 0; i < personVos.length; i++) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", personVos[i][0]); // 唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", personVos[i][1]); // 显示名称
			itemVO.setAttributeValue("itemname_e", ""); // 显示名称
			// itemVO.setAttributeValue("itemtype", "自定义控件"); // 控件类型
			itemVO.setAttributeValue("itemtype", "数字框"); // 控件类型
			itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
			// itemVO.setAttributeValue("refdesc",
			// "getCommUC(\"自定义控件\",\"卡片中的类\",\"\",\"列表渲染器\",\"cn.com.pushworld.salary.ui.target.p040.ScoreCellRenderer\",\"列表编辑器\",\"cn.com.pushworld.salary.ui.target.p040.ScoreCellEditor\");");
			// //
			// 参照定义
			itemVO.setAttributeValue("issave", "N"); // 是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); // 加载公式
			itemVO.setAttributeValue("editformula", "AddItemValueChangedListener(\"cn.com.pushworld.salary.ui.person.p020.PersonMutualCheckValueChangeLinstener\",\"列表执行\");"); // 编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
			itemVO.setAttributeValue("listwidth", "80"); // 列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); // 卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "N"); // 卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemvoList.add(itemVO);
		}
		return (HashVO[]) itemvoList.toArray(new HashVO[0]);
	}
}
