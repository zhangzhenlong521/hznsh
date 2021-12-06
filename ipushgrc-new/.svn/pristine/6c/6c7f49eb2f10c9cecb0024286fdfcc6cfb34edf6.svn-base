package com.pushworld.ipushgrc.ui.wfrisk.p010;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import org.jgraph.graph.DefaultGraphCell;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.workflow.design.ActivityVO;
import cn.com.infostrategy.to.workflow.design.RiskVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.ImageIconFactory;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.workflow.design.WorkFlowCellDeleteEvent;
import cn.com.infostrategy.ui.workflow.design.WorkFlowCellDeleteListener;
import cn.com.infostrategy.ui.workflow.design.WorkFlowCellSelectedEvent;
import cn.com.infostrategy.ui.workflow.design.WorkFlowCellSelectedListener;
import cn.com.infostrategy.ui.workflow.design.WorkFlowDesignWPanel;

import com.pushworld.ipushgrc.ui.IPushGRCServiceIfc;
import com.pushworld.ipushgrc.ui.law.LawShowHtmlDialog;
import com.pushworld.ipushgrc.ui.rule.p010.RuleShowHtmlDialog;

/**
 * 流程文件的单个流程图的设计面板及流程的所有相关信息
 * 
 * @author lcj
 * 
 */
public class WFGraphEditItemPanel extends JPanel implements ActionListener, WorkFlowCellDeleteListener, WorkFlowCellSelectedListener, BillListHtmlHrefListener {
	public static String TYPE_WF = "流程";
	public static String TYPE_ACTIVITY = "环节";
	public int BTN_WIDTH = 100;
	public int BTN_HEIGHT = 25;
	private String cmpfileid;// 流程文件主键
	private String cmpfilename;// 流程文件名称
	private String processid;// 流程主键
	private String processcode;// 流程编码
	private String processname;// 流程名称
	private boolean editable;// 流程图是否可编辑
	private boolean showRefPanel = true;// 是否显示按钮面板
	private JPanel refPanel;
	private WorkFlowDesignWPanel workFlowPanel;// 流程图设计面板
	private JButton btn_savewf;// 流程图保存
	private JLabel refLabel;
	private JPanel ref1;
	private JPanel ref2;
	private boolean ref_twocol;
	private WLTButton wf_btn_0;// 流程相关的流程概述
	private WLTButton wf_btn_1;// 流程相关的相关法规
	private WLTButton wf_btn_2;// 流程相关的相关制度
	private WLTButton wf_btn_3;// 流程相关的相关检查要点
	private WLTButton wf_btn_4;// 流程相关的相关罚则
	//private WLTButton wf_btn_5;// 流程相关的相关案苑//未实现该功能，直接屏蔽掉
	private WLTButton wf_btn_6;// 流程相关的相关流程
	private WLTButton wf_btn_7;// 流程相关的风险点
	private WLTButton btn_0;// 环节相关的操作要求
	private WLTButton btn_1;// 环节相关的相关法规
	private WLTButton btn_2;// 环节相关的相关制度
	private WLTButton btn_3;// 环节相关的相关检查要点
	private WLTButton btn_4;// 环节相关的相关罚则
	private WLTButton btn_5;// 环节相关的风险点
	private IPushGRCServiceIfc service;// 产品服务类
	private ActivityVO curractivityvo;
	private ArrayList deleteactivityids = new ArrayList();// 临时删除的环节id
	private HashMap countmap = new HashMap();
	private TBUtil tbutil = new TBUtil();
	private boolean hasruleitem;// 制度是否分条目，如果分条目，应该可以关联到制度条目
	private String str_userDefinedCls;
	private int refpanelWidth = this.BTN_WIDTH + 30;//相关按钮面板宽度【李春娟/2012-05-24】
	private WFGraphEditPanel graphEditPanel = null;//所有流程图的设计面板【李春娟/2012-06-13】

	public WFGraphEditItemPanel(String _processid, boolean _editable, boolean _showRefPanel) {
		this(null, null, _processid, null, null, _editable, _showRefPanel);
	}

	public WFGraphEditItemPanel(String _cmpfileid, String _cmpfilename, String _processid, String _processcode, String _processname, boolean _editable) {
		this(_cmpfileid, _cmpfilename, _processid, _processcode, _processname, _editable, true);
	}

	public WFGraphEditItemPanel(String _cmpfileid, String _cmpfilename, String _processid, String _processcode, String _processname, boolean _editable, boolean _showRefPanel) {
		this.setLayout(new BorderLayout()); //
		this.cmpfileid = _cmpfileid;
		this.cmpfilename = _cmpfilename;
		if (_processid == null || "".equals(_processid.trim())) {//这里设置一下，保证流程id为空时不报错。【李春娟/2014-03-03】
			this.processid = "-999999";
		} else {
			this.processid = _processid;
		}
		this.processcode = _processcode;
		this.processname = _processname;
		this.editable = _editable;
		this.showRefPanel = _showRefPanel;
		String[] str_btnsize = tbutil.split(tbutil.getSysOptionStringValue("体系流程_流程相关和环节相关按钮的宽度和高度", null), ";");// 将相关按钮大小设置为可配置【李春娟/2012-03-31】
		if (str_btnsize != null && str_btnsize.length > 0) {
			this.BTN_WIDTH = Integer.parseInt(str_btnsize[0]);
			if (str_btnsize.length == 2) {
				this.BTN_HEIGHT = Integer.parseInt(str_btnsize[1]);
			}
		}
		this.add(getWorkFlowPanel(), BorderLayout.CENTER);// 添加流程图设计面板
		if (showRefPanel) {
			try {
				if (cmpfileid == null && _cmpfilename == null) {
					String[][] cmpfiles = UIUtil.getStringArrayByDS(null, "select cmpfile_id,cmpfilename from v_process_file where wfprocess_id=" + processid);
					if (cmpfiles != null && cmpfiles.length > 0) {
						this.cmpfileid = cmpfiles[0][0];
						this.cmpfilename = cmpfiles[0][1];
					}
					String[][] processes = UIUtil.getStringArrayByDS(null, "select code,name from pub_wf_process where id=" + processid);// 考虑到如果是示范流程，没有对应的流程文件，则需要分开取，否则取得流程的code和name都为空【李春娟/2012-04-24】
					if (processes != null && processes.length > 0) {
						this.processcode = processes[0][0];
						this.processname = processes[0][1];
					}
				}
				this.add(getRefPanel(), BorderLayout.EAST);// 添加流程相关和环节相关的面板
				service = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
				resetWfBtnText();
				resetCountMap();
				showTotalCounts(false);
			} catch (Exception e1) {
				MessageBox.showException(this, e1);
			}
		}
	}

	/**
	 * 如在拷贝流程图后，需要重新加载流程图【李春娟/2014-09-22】
	 */
	public void reloadWorkFlow() {
		getWorkFlowPanel().loadGraphByID(null, processid); // 根据数据源和流程id加载流程图
		loadAllRisk();
		if (showRefPanel) {
			resetWfBtnText();
			resetCountMap();
			showTotalCounts(false);
		}
	}

	public String getCmpfileid() {
		return cmpfileid;
	}

	public void setCmpfileid(String cmpfileid) {
		this.cmpfileid = cmpfileid;
	}

	public String getProcessid() {
		return processid;
	}

	public void setProcessid(String processid) {
		this.processid = processid;
	}

	public String getProcesscode() {
		return processcode;
	}

	public void setProcesscode(String _processcode) {
		this.processcode = _processcode;
		getWorkFlowPanel().setProcesscode(processcode);// 必须设置一下，否则流程保存后又变为修改前的编码了
	}

	public String getProcessname() {
		return processname;
	}

	public void setProcessname(String _processname) {
		this.processname = _processname;
		getWorkFlowPanel().setProcessname(processname);// 必须设置一下，否则流程保存后又变为修改前的名称了
	}

	public WFGraphEditPanel getGraphEditPanel() {
		return graphEditPanel;
	}

	public void setGraphEditPanel(WFGraphEditPanel graphEditPanel) {
		this.graphEditPanel = graphEditPanel;
	}

	/**
	 * 环节相关风险点按钮
	 * 
	 * @return
	 */
	public WLTButton getBtn_5() {
		return btn_5;
	}

	public int getBTN_WIDTH() {
		return BTN_WIDTH;
	}

	public void setBTN_WIDTH(int btn_width) {
		BTN_WIDTH = btn_width;
	}

	public int getBTN_HEIGHT() {
		return BTN_HEIGHT;
	}

	public void setBTN_HEIGHT(int btn_height) {
		BTN_HEIGHT = btn_height;
	}

	/**
	 * 获得流程图设计面板
	 * 
	 * @return
	 */
	public WorkFlowDesignWPanel getWorkFlowPanel() {
		if (workFlowPanel == null) {
			workFlowPanel = new WorkFlowDesignWPanel(false);// 新建流程设计面板，默认工具箱不显示
			workFlowPanel.loadGraphByID(null, processid); // 根据数据源和流程id加载流程图
			btn_savewf = workFlowPanel.getBtn_save();
			ActionListener[] listeners = btn_savewf.getActionListeners();
			if (listeners != null && listeners.length > 0) {// 删除流程保存按钮的所有事件
				for (int i = 0; i < listeners.length; i++) {
					btn_savewf.removeActionListener(listeners[i]); // 这句代码会使保存按钮可用！
				}
			}
			if (this.editable) {// 判断流程图是否可编辑
				workFlowPanel.setAllBtnEnable();// 如果可编辑则设置所有按钮都可用
				workFlowPanel.addWorkWolkFlowCellDeleteListener(this);// 如果流程可编辑，流程编辑面板添加删除监听事件
				workFlowPanel.showStaff(true);//显示A4标尺【李春娟/2012-11-16】
			} else {
				workFlowPanel.setToolBarVisiable(false);// 不可编辑默认不显示工具条
				workFlowPanel.lockGroupAndOnlyDoSelect();// 不可编辑默认锁定
			}
			if (this.showRefPanel) {
				workFlowPanel.addWorkFlowCellSelectedListener(this);
			}
			btn_savewf.setIcon(UIUtil.getImage("workflow/save.gif"));// 设置保存按钮的图标
			btn_savewf.addActionListener(this);// 保存按钮添加监听事件
			loadAllRisk();
		}
		return workFlowPanel;
	}

	/**
	 * 流程图选中监听事件,重设环节相关按钮文字
	 */
	public void onWorkFlowCellSelected(WorkFlowCellSelectedEvent _event) {
		Object selectCell = workFlowPanel.getGraph().getSelectionCell();
		if (selectCell instanceof DefaultGraphCell) {
			Object userobj = ((DefaultGraphCell) selectCell).getUserObject();
			if (userobj instanceof ActivityVO) {// 判断选中的是否是环节
				ActivityVO activityvo = (ActivityVO) userobj;
				String[] counts = null;
				if (activityvo != null && activityvo != this.curractivityvo) {
					String activityid = activityvo.getId().toString();
					if (countmap.containsKey(activityid)) {
						counts = (String[]) countmap.get(activityid);
					} else {
						counts = new String[] { "0", "0", "0", "0", "0", "0" };
					}
				} else {
					return;
				}
				resetActivityBtnText(counts);
				this.curractivityvo = activityvo;
			} else {
				showTotalCounts(true);
			}
		} else {
			showTotalCounts(true);
		}
	}

	public void showTotalCounts(boolean flag) {
		if (this.curractivityvo == null && flag) {
			return;
		}
		String[] keys = (String[]) countmap.keySet().toArray(new String[0]);
		int num0 = 0;
		int num1 = 0;
		int num2 = 0;
		int num3 = 0;
		int num4 = 0;
		int num5 = 0;
		for (int i = 0; i < keys.length; i++) {
			String[] nums = (String[]) countmap.get(keys[i]);
			num0 += Integer.parseInt(nums[0]);
			num1 += Integer.parseInt(nums[1]);
			num2 += Integer.parseInt(nums[2]);
			num3 += Integer.parseInt(nums[3]);
			num4 += Integer.parseInt(nums[4]);
			num5 += Integer.parseInt(nums[5]);
		}
		String[] counts = new String[] { num0 + "", num1 + "", num2 + "", num3 + "", num4 + "", num5 + "" };
		resetActivityBtnText(counts);
		this.curractivityvo = null;
	}

	public void resetCountMap() {
		try {
			HashMap map = service.getRelationCountMap(this.processid);
			if (map != null) {
				this.countmap = map;
			}
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	/**
	 * 流程图编辑面板的删除监听事件，如果要删除某个环节，要将环节相关的所有信息都删除！
	 */
	public void onWorkFlowCellDelete(WorkFlowCellDeleteEvent _event) {
		Object[] objs = _event.getSource();
		for (int i = 0, n = objs.length; i < n; i++) {
			Object obj = ((DefaultGraphCell) objs[i]).getUserObject();
			if (obj instanceof ActivityVO) {
				String activityid = ((ActivityVO) obj).getId() + "";
				if (!deleteactivityids.contains(activityid)) {
					deleteactivityids.add(activityid);
				}
			}
		}
	}

	/**
	 * 获得流程相关和环节相关的面板,高老师需要获得该面板，故改为public方法【李春娟/2012-04-10】
	 * 
	 * @return
	 */
	public JPanel getRefPanel() {
		if (refPanel != null) {
			return refPanel;
		}
		hasruleitem = tbutil.getSysOptionBooleanValue("制度是否分条目", true);// 制度是否分条目，如果分条目，应该可以关联到制度条目
		ref_twocol = tbutil.getSysOptionBooleanValue("体系流程_流程相关和环节相关按钮是否分两列显示", false);// 一汽项目中王雷提出项目中流程图一般都是瘦高的，客户要求按钮要放大，一列显示不全，故可配置为两列显示

		refPanel = new WLTPanel(WLTPanel.VERTICAL_TOP_TO_BOTTOM, new BorderLayout(), LookAndFeel.defaultShadeColor1, false);
		refpanelWidth = this.BTN_WIDTH * (ref_twocol ? 2 : 1) + 30;//相关按钮面板宽度【李春娟/2012-05-24】
		refPanel.setPreferredSize(new Dimension(refpanelWidth, 1000)); //

		JPanel btnShowPanel = new WLTPanel(WLTPanel.VERTICAL_TOP_TO_BOTTOM, new BorderLayout(), LookAndFeel.defaultShadeColor1, false);
		btnShowPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		btnShowPanel.setPreferredSize(new Dimension(12, 1000)); //
		btnShowPanel.setOpaque(false);
		final Icon collicon = ImageIconFactory.getCollapsedIcon();
		refLabel = new JLabel(collicon, JLabel.CENTER);
		refLabel.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
		refLabel.setPreferredSize(new Dimension(12, 500));
		refLabel.putClientProperty("isExpand", true);// 按钮面板默认展开
		refLabel.setToolTipText("收起按钮面板");
		refLabel.addMouseListener(new MouseAdapter() { //
					public void mousePressed(MouseEvent evt) {
						WFGraphEditItemPanel.this.setExpandRefPanel(!(Boolean) (refLabel.getClientProperty("isExpand")));
					}
				});
		btnShowPanel.add(refLabel, BorderLayout.NORTH);
		// 后台获得相关信息的条数
		refPanel.add(btnShowPanel, BorderLayout.WEST);
		/* 流程相关 */
		final JLabel label_wf = new JLabel("流程相关", UIUtil.getImage("office_164.gif"), JLabel.CENTER);
		label_wf.setForeground(Color.RED); //
		label_wf.setPreferredSize(new Dimension(this.BTN_WIDTH + 10, this.BTN_HEIGHT + 5)); //

		wf_btn_0 = new WLTButton("流程概述", "office_026.gif");
		wf_btn_1 = new WLTButton("相关法规", "office_119.gif");
		wf_btn_2 = new WLTButton("相关制度", "office_121.gif");
		wf_btn_3 = new WLTButton("检查要点", "office_089.gif");
		wf_btn_4 = new WLTButton("相关罚则", "office_143.gif");
		//wf_btn_5 = new WLTButton("相关案苑", "office_174.gif");
		wf_btn_6 = new WLTButton("相关流程", "zt_046.gif");
		wf_btn_7 = new WLTButton("风险点", "office_016.gif");
		// 设置流程相关的按钮的大小
		wf_btn_0.setPreferredSize(new Dimension(this.BTN_WIDTH, this.BTN_HEIGHT));
		wf_btn_1.setPreferredSize(new Dimension(this.BTN_WIDTH, this.BTN_HEIGHT));
		wf_btn_2.setPreferredSize(new Dimension(this.BTN_WIDTH, this.BTN_HEIGHT));
		wf_btn_3.setPreferredSize(new Dimension(this.BTN_WIDTH, this.BTN_HEIGHT));
		wf_btn_4.setPreferredSize(new Dimension(this.BTN_WIDTH, this.BTN_HEIGHT));
		//wf_btn_5.setPreferredSize(new Dimension(this.BTN_WIDTH, this.BTN_HEIGHT));
		wf_btn_6.setPreferredSize(new Dimension(this.BTN_WIDTH, this.BTN_HEIGHT));
		wf_btn_7.setPreferredSize(new Dimension(this.BTN_WIDTH, this.BTN_HEIGHT));
		// 按钮添加事件
		wf_btn_0.addActionListener(this);
		wf_btn_1.addActionListener(this);
		wf_btn_2.addActionListener(this);
		wf_btn_3.addActionListener(this);
		wf_btn_4.addActionListener(this);
		//wf_btn_5.addActionListener(this);
		wf_btn_6.addActionListener(this);
		wf_btn_7.addActionListener(this);

		/* 环节相关 */
		final JLabel label_activity = new JLabel("环节相关", UIUtil.getImage("zt_023.gif"), JLabel.CENTER);
		label_activity.setForeground(Color.RED); //
		label_activity.setPreferredSize(new Dimension(this.BTN_WIDTH + 10, this.BTN_HEIGHT + 5)); //
		btn_0 = new WLTButton("操作要求", "office_138.gif");
		btn_1 = new WLTButton("相关法规", "office_119.gif");
		btn_2 = new WLTButton("相关制度", "office_121.gif");
		btn_3 = new WLTButton("检查要点", "office_089.gif");
		btn_4 = new WLTButton("相关罚则", "office_143.gif");
		btn_5 = new WLTButton(" 风险点", "office_016.gif");
		// 设置环节相关的按钮的大小
		btn_0.setPreferredSize(new Dimension(this.BTN_WIDTH, this.BTN_HEIGHT));
		btn_1.setPreferredSize(new Dimension(this.BTN_WIDTH, this.BTN_HEIGHT));
		btn_2.setPreferredSize(new Dimension(this.BTN_WIDTH, this.BTN_HEIGHT));
		btn_3.setPreferredSize(new Dimension(this.BTN_WIDTH, this.BTN_HEIGHT));
		btn_4.setPreferredSize(new Dimension(this.BTN_WIDTH, this.BTN_HEIGHT));
		btn_5.setPreferredSize(new Dimension(this.BTN_WIDTH, this.BTN_HEIGHT));

		// 按钮添加事件
		btn_0.addActionListener(this);
		btn_1.addActionListener(this);
		btn_2.addActionListener(this);
		btn_3.addActionListener(this);
		btn_4.addActionListener(this);
		btn_5.addActionListener(this);

		JPanel ref_panel1 = null;// 自定义流程相关面板
		JPanel ref_panel2 = null;// 自定义环节相关面板
		str_userDefinedCls = tbutil.getSysOptionStringValue("自定义一图两表按钮面板类", null);
		boolean isAlldefined = false;//右边的相关按钮面板是否完全自定义，完全自定义时可以将所有系统默认的按钮都设置为不显示并且包括流程相关和环节相关标头
		if (str_userDefinedCls != null && !str_userDefinedCls.trim().equals("")) {// 如果配置了自定义一图两表按钮面板类
			HashMap hashmap = new HashMap();
			hashmap.put("itempanel", this);// 将本类句柄传入
			hashmap = tbutil.reflectCallCommMethod(str_userDefinedCls + ".getRefPanels()", hashmap);
			if (hashmap != null) {
				if (hashmap.get("是否自定义整个面板") != null) {
					isAlldefined = (Boolean) hashmap.get("是否自定义整个面板");
					if (isAlldefined) {
						JPanel jpanel = (JPanel) hashmap.get("整个面板");
						refpanelWidth = (int) (12 + jpanel.getPreferredSize().getWidth());//相关按钮面板宽度【李春娟/2012-05-24】
						refPanel.setPreferredSize(new Dimension(refpanelWidth, 1000)); //
						refPanel.add(jpanel, BorderLayout.CENTER);
						return refPanel;
					}
				}
				ref_panel1 = (JPanel) hashmap.get("流程相关面板");
				ref_panel2 = (JPanel) hashmap.get("环节相关面板");
			}
		}

		if (ClientEnvironment.isAdmin()) {// 如果是管理员身份，设置相关按钮的提示信息【李春娟/2012-03-07】
			label_wf.setToolTipText("可通过系统参数[自定义一图两表按钮面板类]自定义面板,\r\n点击时收起/展开流程相关按钮");
			label_activity.setToolTipText("可通过系统参数[自定义一图两表按钮面板类]自定义面板,\r\n点击时收起/展开环节相关按钮");
			setRefBtnToolTipText(new WLTButton[] { wf_btn_0, wf_btn_1, wf_btn_2, wf_btn_3, wf_btn_4, wf_btn_6, wf_btn_7 }, this.TYPE_WF);
			setRefBtnToolTipText(new WLTButton[] { btn_0, btn_1, btn_2, btn_3, btn_4, btn_5 }, this.TYPE_ACTIVITY);
		} else {
			label_wf.setToolTipText("收起流程相关");// 【李春娟/2012-04-20】
			label_activity.setToolTipText("收起环节相关");
		}
		// 下面要判断该系统是否要作为演示系统来显示，只有三个值：强制启动，强制禁用，弃权。由于汪总演示总要提，为了可以演示给客户最全面的功能，并且到时候部署时是最简洁、健壮的系统，所以增加这个参数
		// 参数值可以为[强制启动]:强制设置本系统为演示系统，功能最全面，如一图两表界面的【相关罚则】、【相关案苑】和【检查要点】按钮肯定会显示出来;
		// 参数值为[强制禁用]:强制系统中某些功能不显示，如上面说到的三个按钮;
		// 参数值为[弃权]:表示该参数不干预本系统功能的显示;
		// 默认为[弃权]！
		String isDemoSystem = tbutil.getSysOptionStringValue("是否为演示系统", "弃权");
		String show_wfref = tbutil.getSysOptionStringValue("体系流程_流程相关", null);
		String show_activityref = tbutil.getSysOptionStringValue("体系流程_环节相关", null);

		ref1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));// 流程相关面板
		ref2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));// 环节相关面板
		ref1.setOpaque(false);
		ref2.setOpaque(false);
		// ref1.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		// ref2.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		int btnwf_count = 1;// 流程相关按钮及label的个数
		int btnactivity_count = 1;// 环节相关按钮及label的个数
		if ("强制启动".equals(isDemoSystem)) {
			// 增加流程相关按钮
			btnwf_count = 9;// 8个按钮加上一个流程相关label
			btnactivity_count = 7;// 6个按钮加上一个环节相关label
			ref1.add(label_wf);
			ref1.add(wf_btn_0);
			ref1.add(wf_btn_1);
			ref1.add(wf_btn_2);
			ref1.add(wf_btn_3);
			ref1.add(wf_btn_4);
			//ref1.add(wf_btn_5);
			ref1.add(wf_btn_6);
			ref1.add(wf_btn_7);
			if (ref_panel1 != null) {
				ref1.add(ref_panel1);
			}
			// 增加环节相关按钮
			ref2.add(label_activity);
			ref2.add(btn_0);
			ref2.add(btn_1);
			ref2.add(btn_2);
			ref2.add(btn_3);
			ref2.add(btn_4);
			ref2.add(btn_5);
			if (ref_panel2 != null) {
				ref2.add(ref_panel2);
			}
		} else if ("强制禁用".equals(isDemoSystem)) {
			// 强制禁用，不管其他参数配置，肯定是不能显示的,比如流程相关的【检查要点】、【相关罚则】、【相关案苑】三个按钮不显示
			btnwf_count = 6;
			btnactivity_count = 5;
			ref1.add(label_wf);
			ref1.add(wf_btn_0);// 流程概述
			ref1.add(wf_btn_1);// 相关法规
			ref1.add(wf_btn_2);// 相关制度
			ref1.add(wf_btn_6);// 相关流程
			ref1.add(wf_btn_7);// 相关风险点
			if (ref_panel1 != null) {
				ref1.add(ref_panel1);
			}
			// 强制禁用，不管其他参数配置，肯定是不能显示的,比如环节相关的【检查要点】、【相关罚则】两个按钮不显示
			ref2.add(label_activity);
			ref2.add(btn_0);// 操作要求
			ref2.add(btn_1);// 相关法规
			ref2.add(btn_2);// 相关制度
			ref2.add(btn_5);// 风险点
			if (ref_panel2 != null) {
				ref2.add(ref_panel2);
			}
		} else {
			if (show_wfref != null && !"".equals(show_wfref.trim())) {// 设置了流程相关的配置
				String[] show_wfrefs = tbutil.split(show_wfref, ";");
				ref1.add(label_wf);
				if ("Y".equals(show_wfrefs[0])) {
					ref1.add(wf_btn_0);
					btnwf_count++;
				}
				if ("Y".equals(show_wfrefs[1])) {
					ref1.add(wf_btn_1);
					btnwf_count++;
				}
				if ("Y".equals(show_wfrefs[2])) {
					ref1.add(wf_btn_2);
					btnwf_count++;
				}
				if ("Y".equals(show_wfrefs[3])) {
					ref1.add(wf_btn_3);
					btnwf_count++;
				}
				if ("Y".equals(show_wfrefs[4])) {
					ref1.add(wf_btn_4);
					btnwf_count++;
				}
				//				if ("Y".equals(show_wfrefs[5])) {
				//					ref1.add(wf_btn_5);
				//					btnwf_count++;
				//				}
				if (show_wfrefs.length == 8) {//去掉了相关案苑，需要兼容旧逻辑【李春娟/2014-09-23】
					if ("Y".equals(show_wfrefs[6])) {
						ref1.add(wf_btn_6);
						btnwf_count++;
					}

					if ("Y".equals(show_wfrefs[7])) {
						ref1.add(wf_btn_7);
						btnwf_count++;
					}
				} else if (show_wfrefs.length == 7) {//需要兼容旧逻辑
					if ("Y".equals(show_wfrefs[5])) {
						ref1.add(wf_btn_6);
						btnwf_count++;
					}
					if ("Y".equals(show_wfrefs[6])) {
						ref1.add(wf_btn_7);
						btnwf_count++;
					}
				}
				if (ref_panel1 != null) {
					ref1.add(ref_panel1);
				}
			} else {
				btnwf_count = 9;
				ref1.add(label_wf);
				ref1.add(wf_btn_0);
				ref1.add(wf_btn_1);
				ref1.add(wf_btn_2);
				ref1.add(wf_btn_3);
				ref1.add(wf_btn_4);
				//ref1.add(wf_btn_5);
				ref1.add(wf_btn_6);
				ref1.add(wf_btn_7);
				if (ref_panel1 != null) {
					ref1.add(ref_panel1);
				}
			}
			if (show_activityref != null && !"".equals(show_activityref.trim())) {// 设置了环节相关的配置
				String[] show_activityrefs = tbutil.split(show_activityref, ";");
				ref2.add(label_activity);
				if ("Y".equals(show_activityrefs[0])) {
					ref2.add(btn_0);
					btnactivity_count++;
				}
				if ("Y".equals(show_activityrefs[1])) {
					ref2.add(btn_1);
					btnactivity_count++;
				}
				if ("Y".equals(show_activityrefs[2])) {
					ref2.add(btn_2);
					btnactivity_count++;
				}
				if ("Y".equals(show_activityrefs[3])) {
					ref2.add(btn_3);
					btnactivity_count++;
				}
				if ("Y".equals(show_activityrefs[4])) {
					ref2.add(btn_4);
					btnactivity_count++;
				}
				if ("Y".equals(show_activityrefs[5])) {
					ref2.add(btn_5);
					btnactivity_count++;
				}
				if (ref_panel2 != null) {
					ref2.add(ref_panel2);
				}
			} else {
				btnactivity_count = 7;
				ref2.add(label_activity);
				ref2.add(btn_0);
				ref2.add(btn_1);
				ref2.add(btn_2);
				ref2.add(btn_3);
				ref2.add(btn_4);
				ref2.add(btn_5);
				if (ref_panel2 != null) {
					ref2.add(ref_panel2);
				}
			}
		}
		if (ref_panel1 != null) {
			ref1.setPreferredSize(new Dimension(this.BTN_WIDTH + 10, ref_twocol ? 1000 : (this.BTN_HEIGHT + 5) * btnwf_count + (int) ref_panel1.getPreferredSize().getHeight() + 20)); //
		} else {
			ref1.setPreferredSize(new Dimension(this.BTN_WIDTH + 10, ref_twocol ? 1000 : (this.BTN_HEIGHT + 5) * btnwf_count + 15)); //
		}
		if (ref_panel2 != null) {
			ref2.setPreferredSize(new Dimension(this.BTN_WIDTH + 10, ref_twocol ? 1000 : (this.BTN_HEIGHT + 5) * btnactivity_count + (int) ref_panel2.getPreferredSize().getHeight() + 20)); //
		} else {
			ref2.setPreferredSize(new Dimension(this.BTN_WIDTH + 10, ref_twocol ? 1000 : (this.BTN_HEIGHT + 5) * btnactivity_count + 15)); //
		}
		if (!ref_twocol) {// 如果流程相关和环节相关按钮不分为两列显示的话，需要增加label的点击事件
			label_wf.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
			label_activity.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
			final int ref1_height = (int) ref1.getPreferredSize().getHeight();
			label_wf.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
			label_wf.putClientProperty("isExpand", true);
			label_wf.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent evt) {
					if ((Boolean) (label_wf.getClientProperty("isExpand"))) {
						label_wf.putClientProperty("isExpand", false);
						label_wf.setToolTipText("展开流程相关");
						ref1.setPreferredSize(new Dimension((int) ref1.getPreferredSize().getWidth(), (int) label_wf.getPreferredSize().getHeight() + 5));// 宽度不变，高度为流程相关页签高度再加5个像素
						ref1.revalidate();
						refPanel.repaint();
					} else {
						label_wf.putClientProperty("isExpand", true);
						label_wf.setToolTipText("收起流程相关");
						ref1.setPreferredSize(new Dimension((int) ref1.getPreferredSize().getWidth(), ref1_height));
						ref1.revalidate();
						refPanel.repaint();
					}
				}
			});

			final int ref2_height = (int) ref2.getPreferredSize().getHeight();
			label_activity.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
			label_activity.putClientProperty("isExpand", true);
			label_activity.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent evt) {
					if ((Boolean) (label_activity.getClientProperty("isExpand"))) {
						label_activity.putClientProperty("isExpand", false);
						label_activity.setToolTipText("展开环节相关");
						ref2.setPreferredSize(new Dimension((int) ref2.getPreferredSize().getWidth(), (int) label_activity.getPreferredSize().getHeight() + 5));// 宽度不变，高度为环节相关页签高度再加5个像素
						ref2.revalidate();
						refPanel.repaint();
					} else {
						label_activity.putClientProperty("isExpand", true);
						label_activity.setToolTipText("收起环节相关");
						ref2.setPreferredSize(new Dimension((int) ref2.getPreferredSize().getWidth(), ref2_height));
						ref2.revalidate();
						refPanel.repaint();
					}
				}
			});
		} else {
			ref1.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
			ref2.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		}

		if (!ref_twocol) {
			JPanel jpanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
			jpanel.setPreferredSize(new Dimension(ref1.getWidth(), ref1.getHeight() + ref2.getHeight()));
			jpanel.setOpaque(false);
			jpanel.add(ref1);
			jpanel.add(ref2);
			refPanel.add(jpanel, BorderLayout.CENTER);
		} else {
			refPanel.add(ref1, BorderLayout.CENTER);
			refPanel.add(ref2, BorderLayout.EAST);
		}
		return refPanel;
	}

	/**
	 * 按钮的监听事件
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == wf_btn_0) {// 流程相关的流程概述
			onLookWfdesc(this.TYPE_WF);
		} else if (e.getSource() == wf_btn_1) {// 流程相关的相关法规
			onLookLaw(this.TYPE_WF);
		} else if (e.getSource() == wf_btn_2) {// 流程相关的相关制度
			onLookRule(this.TYPE_WF);
		} else if (e.getSource() == wf_btn_3) {// 流程相关的相关检查要点
			onLookCheckPoint(this.TYPE_WF);
		} else if (e.getSource() == wf_btn_4) {// 流程相关的相关罚则
			onLookPunish(this.TYPE_WF);
			//		} else if (e.getSource() == wf_btn_5) {// 该逻辑以后实现
			//			MessageBox.show(this, "该功能正在开发中...");
		} else if (e.getSource() == wf_btn_6) {// 流程相关的相关流程
			onLookProcessRefwf();
		} else if (e.getSource() == wf_btn_7) {// 流程相关的风险点
			onLookRisk(this.TYPE_WF);
		} else if (e.getSource() == btn_0) {// 环节相关的操作要求
			onLookActivityOpereq();
		} else if (e.getSource() == btn_1) {// 环节相关的相关法规
			onLookLaw(this.TYPE_ACTIVITY);
		} else if (e.getSource() == btn_2) {// 环节相关的相关制度
			onLookRule(this.TYPE_ACTIVITY);
		} else if (e.getSource() == btn_3) {// 环节相关的相关检查要点
			onLookCheckPoint(this.TYPE_ACTIVITY);
		} else if (e.getSource() == btn_4) {// 环节相关的相关罚则
			onLookPunish(this.TYPE_ACTIVITY);
		} else if (e.getSource() == btn_5) {// 环节相关的风险点
			onLookRisk(this.TYPE_ACTIVITY);
		} else if (e.getSource() == btn_savewf) {// 流程保存按钮的点击事件
			onSaveWf(true);
		}
	}

	/**
	 * 如果是管理员身份，设置相关按钮的提示信息【李春娟/2012-03-07】
	 * 
	 * @param _btns
	 * @param _type
	 */
	private void setRefBtnToolTipText(WLTButton[] _btns, String _type) {
		for (WLTButton btn : _btns) {
			btn.setToolTipText("通过系统参数[体系流程_" + _type + "相关]设置是否显示");
		}
	}

	/**
	 * 查看或编辑流程相关的流程概述
	 * 
	 * @param _type
	 *            1-流程相关；2-环节相关
	 */
	protected void onLookWfdesc(String _type) {
		LookWfdescDialog dialog_editopereq = new LookWfdescDialog(this, "流程概述", this.cmpfileid, this.cmpfilename, this.processid, this.processcode, this.processname, this.editable);
		dialog_editopereq.setVisible(true);
		resetWfBtnText();
	}

	/**
	 * 查看或编辑相关法规
	 * 
	 * @param _type
	 *            1-流程相关；2-环节相关
	 */
	protected void onLookLaw(String _type) {
		if (_type.equals(this.TYPE_ACTIVITY)) {
			ActivityVO activityvo = workFlowPanel.getSelectedActivityVO();
			if (activityvo == null) {
				if (MessageBox.confirm(this, "未选中环节,是否查看本流程所有环节相关法规?")) {
					getLookDialog("环节相关法规", "CMP_CMPFILE_LAW_CODE1", "relationtype").setVisible(true);
				}
				return;
			}
			ImportLawDialog dialog_importlaw = new ImportLawDialog(this, "环节相关法规", this.cmpfileid, this.cmpfilename, this.processid, this.processcode, this.processname, activityvo.getId() + "", activityvo.getCode(), activityvo.getWfname(), this.TYPE_ACTIVITY, this.editable);
			if (dialog_importlaw.isShowDialog()) {
				dialog_importlaw.setVisible(true);
				resetActivityBtnText(activityvo.getId() + "");
			} else {
				dialog_importlaw.dispose();
			}
		} else {
			ImportLawDialog dialog_importlaw = new ImportLawDialog(this, "流程相关法规", this.cmpfileid, this.cmpfilename, this.processid, this.processcode, this.processname, this.editable);
			if (dialog_importlaw.isShowDialog()) {
				dialog_importlaw.setVisible(true);
				resetWfBtnText();
			} else {
				dialog_importlaw.dispose();
			}
		}
	}

	/**
	 * 查看或编辑相关制度
	 * 
	 * @param _type
	 *            1-流程相关；2-环节相关
	 */
	protected void onLookRule(String _type) {
		if (_type.equals(this.TYPE_ACTIVITY)) {
			ActivityVO activityvo = workFlowPanel.getSelectedActivityVO();
			if (activityvo == null) {
				if (MessageBox.confirm(this, "未选中环节,是否查看本流程所有环节相关制度?")) {
					String templetcode = "CMP_CMPFILE_RULE_CODE1";
					if (this.hasruleitem) {// 制度是否分条目
						templetcode = "CMP_CMPFILE_RULE_CODE2";
					}
					getLookDialog("环节相关制度", templetcode, "relationtype").setVisible(true);
				}
				return;
			}
			if (this.hasruleitem) {// 制度是否分条目
				ImportRuleItemDialog dialog_importruleitem = new ImportRuleItemDialog(this, "环节相关制度", this.cmpfileid, this.cmpfilename, this.processid, this.processcode, this.processname, activityvo.getId() + "", activityvo.getCode(), activityvo.getWfname(), this.TYPE_ACTIVITY, this.editable);
				if (dialog_importruleitem.isShowDialog()) {
					dialog_importruleitem.setVisible(true);
				} else {
					dialog_importruleitem.dispose();
					return;
				}
			} else {
				ImportRuleDialog dialog_importrule = new ImportRuleDialog(this, "环节相关制度", this.cmpfileid, this.cmpfilename, this.processid, this.processcode, this.processname, activityvo.getId() + "", activityvo.getCode(), activityvo.getWfname(), this.TYPE_ACTIVITY, this.editable);
				if (dialog_importrule.isShowDialog()) {
					dialog_importrule.setVisible(true);
				} else {
					dialog_importrule.dispose();
					return;
				}
			}
			resetActivityBtnText(activityvo.getId() + "");
		} else {
			if (this.hasruleitem) {// 制度是否分条目
				ImportRuleItemDialog dialog_importruleitem = new ImportRuleItemDialog(this, "流程相关制度", this.cmpfileid, this.cmpfilename, this.processid, this.processcode, this.processname, this.editable);
				if (dialog_importruleitem.isShowDialog()) {
					dialog_importruleitem.setVisible(true);
				} else {
					dialog_importruleitem.dispose();
					return;
				}
			} else {
				ImportRuleDialog dialog_importrule = new ImportRuleDialog(this, "流程相关制度", this.cmpfileid, this.cmpfilename, this.processid, this.processcode, this.processname, this.editable);
				if (dialog_importrule.isShowDialog()) {
					dialog_importrule.setVisible(true);
				} else {
					dialog_importrule.dispose();
					return;
				}
			}
			resetWfBtnText();
		}
	}

	/**
	 * 查看或编辑相关检查要点
	 * 
	 * @param _type
	 *            1-流程相关；2-环节相关
	 */
	protected void onLookCheckPoint(String _type) {
		if (_type.equals(this.TYPE_ACTIVITY)) {
			ActivityVO activityvo = workFlowPanel.getSelectedActivityVO();
			if (activityvo == null) {
				if (MessageBox.confirm(this, "未选中环节,是否查看本流程所有环节相关检查要点?")) {
					getLookDialog("环节相关检查要点", "CMP_CMPFILE_CHECKPOINT_CODE1", "relationtype").setVisible(true);
				}
				return;
			}
			ImportCheckPointDialog dialog_importchekpoint = new ImportCheckPointDialog(this, "环节相关检查要点", this.cmpfileid, this.cmpfilename, this.processid, this.processcode, this.processname, activityvo.getId() + "", activityvo.getCode(), activityvo.getWfname(), this.TYPE_ACTIVITY, this.editable);
			if (dialog_importchekpoint.isShowDialog()) {
				dialog_importchekpoint.setVisible(true);
				resetActivityBtnText(activityvo.getId() + "");
			} else {
				dialog_importchekpoint.dispose();
			}
		} else {
			ImportCheckPointDialog dialog_importchekpoint = new ImportCheckPointDialog(this, "流程相关检查要点", this.cmpfileid, this.cmpfilename, this.processid, this.processcode, this.processname, this.editable);
			if (dialog_importchekpoint.isShowDialog()) {
				dialog_importchekpoint.setVisible(true);
				resetWfBtnText();
			} else {
				dialog_importchekpoint.dispose();
			}
		}
	}

	/**
	 * 查看或编辑相关罚则
	 * 
	 * @param _type
	 *            1-流程相关；2-环节相关
	 */
	protected void onLookPunish(String _type) {
		if (_type.equals(this.TYPE_ACTIVITY)) {
			ActivityVO activityvo = workFlowPanel.getSelectedActivityVO();
			if (activityvo == null) {
				if (MessageBox.confirm(this, "未选中环节,是否查看本流程所有环节相关罚则?")) {
					getLookDialog("环节相关罚则", "CMP_CMPFILE_PUNISH_CODE1", "relationtype").setVisible(true);
				}
				return;
			}
			ImportPunishDialog dialog_importpunish = new ImportPunishDialog(this, "环节相关罚则", this.cmpfileid, this.cmpfilename, this.processid, this.processcode, this.processname, activityvo.getId() + "", activityvo.getCode(), activityvo.getWfname(), this.TYPE_ACTIVITY, this.editable);
			if (dialog_importpunish.isShowDialog()) {
				dialog_importpunish.setVisible(true);
				resetActivityBtnText(activityvo.getId() + "");
			} else {
				dialog_importpunish.dispose();
			}
		} else {
			ImportPunishDialog dialog_importpunish = new ImportPunishDialog(this, "流程相关罚则", this.cmpfileid, this.cmpfilename, this.processid, this.processcode, this.processname, this.editable);
			if (dialog_importpunish.isShowDialog()) {
				dialog_importpunish.setVisible(true);
				resetWfBtnText();
			} else {
				dialog_importpunish.dispose();
			}
		}
	}

	/**
	 * 查看或编辑流程相关的相关流程
	 */
	protected void onLookProcessRefwf() {
		ImportRefwfDialog dialog_importrefwf = new ImportRefwfDialog(this, "相关流程", this.cmpfileid, this.cmpfilename, this.processid, this.processcode, this.processname, this.editable);
		if (dialog_importrefwf.isShowDialog()) {
			dialog_importrefwf.setVisible(true);
			resetWfBtnText();
		} else {
			dialog_importrefwf.dispose();
		}
	}

	/**
	 * 查看或编辑环节相关的操作要求
	 */
	protected void onLookActivityOpereq() {
		ActivityVO activityvo = workFlowPanel.getSelectedActivityVO();
		if (activityvo == null) {
			if (MessageBox.confirm(this, "未选中环节,是否查看本流程所有操作要求?")) {
				BillListDialog dialog_list = getLookDialog("操作要求", "CMP_CMPFILE_WFOPEREQ_CODE1", null);
				BillListPanel listpanel = dialog_list.getBilllistPanel();
				listpanel.setItemsVisible(new String[] { "cmpfile_name", "wfprocess_name" }, false);
				listpanel.setItemsVisible(new String[] { "wfactivity_name", "operatepost", "operatedept" }, true);
				dialog_list.setVisible(true);
			}
			return;
		}
		LookOpereqDialog dialog_editopereq = new LookOpereqDialog(this, "操作要求", this.cmpfileid, this.cmpfilename, this.processid, this.processcode, this.processname, activityvo.getId() + "", activityvo.getCode(), activityvo.getWfname(), this.editable);
		dialog_editopereq.setVisible(true);
		resetActivityBtnText(activityvo.getId() + "");
	}

	/**
	 * 查看或编辑环节相关的风险点
	 * 
	 * @param _type
	 *            1-流程相关；2-环节相关
	 */
	protected void onLookRisk(String _type) {
		if (_type.equals(this.TYPE_ACTIVITY)) {
			ActivityVO activityvo = workFlowPanel.getSelectedActivityVO();
			if (activityvo == null) {
				if (MessageBox.confirm(this, "未选中环节,是否查看本流程所有环节相关风险点?")) {
					this.getLookDialog("环节相关风险点", "CMP_RISK_CODE1", "riskreftype").setVisible(true);
				}
				return;
			}
			LookRiskDialog dialog_evaluaterisk = new LookRiskDialog(this, "环节相关风险点", this.cmpfileid, this.cmpfilename, this.processid, this.processcode, this.processname, activityvo.getId() + "", activityvo.getCode(), activityvo.getWfname(), this.TYPE_ACTIVITY, this.editable);
			if (dialog_evaluaterisk.isShowDialog()) {
				dialog_evaluaterisk.setVisible(true);
				String activityid = activityvo.getId() + "";
				resetRisk(activityid);// 更新风险点
				resetActivityBtnText(activityid);
			} else {
				dialog_evaluaterisk.dispose();
			}
		} else {
			LookRiskDialog dialog_evaluaterisk = new LookRiskDialog(this, "流程相关风险点", this.cmpfileid, this.cmpfilename, this.processid, this.processcode, this.processname, this.editable);
			if (dialog_evaluaterisk.isShowDialog()) {
				dialog_evaluaterisk.setVisible(true);
				resetWfBtnText();
			} else {
				dialog_evaluaterisk.dispose();
			}
		}
	}

	/**
	 * 刷新流程相关的按钮文字
	 */
	protected void resetWfBtnText() {
		String[] counts = null;
		try {
			counts = service.getRelationCountByWfId(this.processid);
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
		if (Integer.parseInt(counts[0]) > 0) {
			wf_btn_0.setText("流程概述(" + counts[0] + ")");
		} else {
			wf_btn_0.setText("流程概述");
		}
		if (Integer.parseInt(counts[1]) > 0) {
			wf_btn_1.setText("相关法规(" + counts[1] + ")");
		} else {
			wf_btn_1.setText("相关法规");
		}
		if (Integer.parseInt(counts[2]) > 0) {
			wf_btn_2.setText("相关制度(" + counts[2] + ")");
		} else {
			wf_btn_2.setText("相关制度");
		}
		if (Integer.parseInt(counts[3]) > 0) {
			wf_btn_3.setText("检查要点(" + counts[3] + ")");
		} else {
			wf_btn_3.setText("检查要点");
		}
		if (Integer.parseInt(counts[4]) > 0) {
			wf_btn_4.setText("相关罚则(" + counts[4] + ")");
		} else {
			wf_btn_4.setText("相关罚则");
		}
		//		if (Integer.parseInt(counts[5]) > 0) {
		//			wf_btn_5.setText("相关案苑(" + counts[5] + ")");
		//		} else {
		//			wf_btn_5.setText("相关案苑");
		//		}
		if (Integer.parseInt(counts[5]) > 0) {
			wf_btn_6.setText("相关流程(" + counts[5] + ")");
		} else {
			wf_btn_6.setText("相关流程");
		}
		if (Integer.parseInt(counts[6]) > 0) {
			wf_btn_7.setText("风险点(" + counts[6] + ")");
		} else {
			wf_btn_7.setText("风险点");
		}
	}

	/**
	 * 刷新环节相关的按钮文字
	 */
	protected void resetActivityBtnText(String _activityid) {
		String[] counts = null;
		try {
			counts = service.getRelationCountByActivityId(_activityid);
			countmap.put(_activityid, counts);
			resetActivityBtnText(counts);
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	private void resetActivityBtnText(String[] counts) {
		if (Integer.parseInt(counts[0]) > 0) {
			btn_0.setText("操作要求(" + counts[0] + ")");
		} else {
			btn_0.setText("操作要求");
		}
		if (Integer.parseInt(counts[1]) > 0) {
			btn_1.setText("相关法规(" + counts[1] + ")");
		} else {
			btn_1.setText("相关法规");
		}
		if (Integer.parseInt(counts[2]) > 0) {
			btn_2.setText("相关制度(" + counts[2] + ")");
		} else {
			btn_2.setText("相关制度");
		}
		if (Integer.parseInt(counts[3]) > 0) {
			btn_3.setText("检查要点(" + counts[3] + ")");
		} else {
			btn_3.setText("检查要点");
		}
		if (Integer.parseInt(counts[4]) > 0) {
			btn_4.setText("相关罚则(" + counts[4] + ")");
		} else {
			btn_4.setText("相关罚则");
		}
		if (Integer.parseInt(counts[5]) > 0) {
			btn_5.setText(" 风险点(" + counts[5] + ")");
		} else {
			btn_5.setText(" 风险点");
		}
	}

	/**
	 * 加载全部风险点
	 */
	private void loadAllRisk() {
		try {
			String str_sql = "select wfactivity_id,rank from cmp_risk where wfprocess_id=" + this.processid;
			HashVO hvs[] = UIUtil.getHashVoArrayByDS(null, str_sql);
			Hashtable ht_risk = new Hashtable();
			for (int i = 0; i < hvs.length; i++) {
				String str_activityid = hvs[i].getStringValue("wfactivity_id");
				String str_rank = hvs[i].getStringValue("rank");
				if (str_rank != null && str_activityid != null)
					if (ht_risk.containsKey(str_activityid)) {
						RiskVO riskVO = (RiskVO) ht_risk.get(str_activityid);
						if (str_rank.equals("极大风险") || str_rank.equals("高风险")) {
							riskVO.setLevel1RiskCount(riskVO.getLevel1RiskCount() + 1);
						} else if (str_rank.equals("中等风险")) {
							riskVO.setLevel2RiskCount(riskVO.getLevel2RiskCount() + 1);
						} else if (str_rank.equals("低风险") || str_rank.equals("极小风险")) {
							riskVO.setLevel3RiskCount(riskVO.getLevel3RiskCount() + 1);
						}
					} else {
						RiskVO riskVO = new RiskVO();
						if (str_rank.equals("极大风险") || str_rank.equals("高风险")) {
							riskVO.setLevel1RiskCount(1);
						} else if (str_rank.equals("中等风险")) {
							riskVO.setLevel2RiskCount(1);
						} else if (str_rank.equals("低风险") || str_rank.equals("极小风险")) {
							riskVO.setLevel3RiskCount(1);
						}
						ht_risk.put(str_activityid, riskVO);
					}
			}
			String str_allkeys[] = (String[]) ht_risk.keySet().toArray(new String[0]);
			for (int i = 0; i < str_allkeys.length; i++) {
				workFlowPanel.setCellAddRisk(str_allkeys[i], (RiskVO) ht_risk.get(str_allkeys[i]));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新当前选中环节的风险点
	 */
	public void resetRisk(String _activityid) {
		try {
			String[] risks = UIUtil.getStringArrayFirstColByDS(null, "select rank from cmp_risk where wfactivity_id =" + _activityid);
			RiskVO riskVO = new RiskVO();
			for (int i = 0; i < risks.length; i++) {
				String str_rank = risks[i];
				if (str_rank != null) {
					if (str_rank.equals("极大风险") || str_rank.equals("高风险"))
						riskVO.setLevel1RiskCount(riskVO.getLevel1RiskCount() + 1);
					else if (str_rank.equals("中等风险"))
						riskVO.setLevel2RiskCount(riskVO.getLevel2RiskCount() + 1);
					else if (str_rank.equals("低风险") || str_rank.equals("极小风险"))
						riskVO.setLevel3RiskCount(riskVO.getLevel3RiskCount() + 1);
				}
			}
			String activityid = workFlowPanel.getSelectedActivityVO().getId() + "";
			workFlowPanel.setCellAddRisk(activityid, riskVO);
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	/**
	 * 流程图保存按钮的监听事件,保存时判断流程是否已编辑，是否要执行删除环节级联删除环节相关信息的sql
	 * 
	 * @param _isShowMessage
	 *            是否显示提示信息
	 */

	public void onSaveWf(boolean _isShowMessage) {
		boolean ifedit = workFlowPanel.isEditChanged();// 此时流程是否已编辑
		workFlowPanel.onSaveWfProcess(_isShowMessage); // 保存流程
		boolean ifedit2 = workFlowPanel.isEditChanged();// 如果保存流程报错，流程编辑状态是不会变化的，不应该删除所删除的环节的相关信息，故设置此变量
		if (ifedit && !ifedit2 && deleteactivityids.size() > 0) {
			/* 这里以后要增加修改流程记录日志的逻辑 */
			try {
				service.deleteActivityRelationByActivityIds(tbutil.getInCondition(deleteactivityids));
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}
	}

	/**
	 * 判断是否有流程编辑了但未保存
	 * 
	 * @return
	 */
	public boolean isEditChanged() {
		if (!editable) {// 如果流程不可编辑，则直接返回false
			return false;
		}
		return getWorkFlowPanel().isEditChanged();
	}

	/**
	 * 获得流程图是否可编辑
	 * 
	 * @return
	 */

	public boolean isEditable() {
		return editable;
	}

	/**
	 * 设置流程图是否可编辑
	 * 
	 * @param _editable
	 */

	public void setEditable(boolean _editable) {
		this.editable = _editable;
		if (editable) {// 判断流程图是否可编辑
			workFlowPanel.setAllBtnEnable();
			workFlowPanel.addWorkWolkFlowCellDeleteListener(this);// 如果流程可编辑，流程编辑面板添加删除监听事件
		} else {// 如果不可编辑干脆不要显示按钮栏了
			workFlowPanel.setToolBarVisiable(false);
		}
	}

	/**
	 * 获得按钮面板是否显示
	 * 
	 * @return
	 */

	public boolean isShowRefPanel() {
		return showRefPanel;
	}

	/**
	 * 设置按钮面板是否显示
	 * 
	 * @param _showRefPanel
	 */
	public void setShowRefPanel(boolean _showRefPanel) {
		if (this.showRefPanel == _showRefPanel) {
			return;
		}
		this.showRefPanel = _showRefPanel;

		if (showRefPanel) {
			try {
				String[][] processes = UIUtil.getStringArrayByDS(null, "select code,name from pub_wf_process where id=" + processid);// 考虑到如果是示范流程，没有对应的流程文件，则需要分开取，否则取得流程的code和name都为空【李春娟/2012-04-24】
				if (processes != null && processes.length > 0) {
					this.processcode = processes[0][0];
					this.processname = processes[0][1];
				}
				String[][] cmpfiles = UIUtil.getStringArrayByDS(null, "select cmpfile_id,cmpfilename from v_process_file where wfprocess_id=" + processid);
				if (cmpfiles != null && cmpfiles.length > 0) {
					this.cmpfileid = cmpfiles[0][0];
					this.cmpfilename = cmpfiles[0][1];
				}
				this.add(getRefPanel(), BorderLayout.EAST);// 添加流程相关和环节相关的面板
				if (service == null) {
					service = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
				}
				resetWfBtnText();
				resetCountMap();// 单个流程面板的就直接设置countmap了
				showTotalCounts(false);

				workFlowPanel.addWorkFlowCellSelectedListener(this);
			} catch (Exception e1) {
				MessageBox.showException(this, e1);
			}
		} else {
			getRefPanel().setVisible(false);
		}
	}

	/**
	 * 设置按钮面板是否收起【李春娟/2012-04-01】
	 * 
	 * @param _showRefPanel
	 */
	public void setExpandRefPanel(boolean _expand) {
		if (!showRefPanel && this.refPanel == null) {// 如果本身不显示按钮面板或者按钮面板为空则直接返回
			return;
		}
		if (_expand) {// 展开相关按钮面板
			if ((Boolean) (refLabel.getClientProperty("isExpand"))) {// 如果本来就是展开的
				return;
			} else {
				refLabel.putClientProperty("isExpand", true);
				refLabel.setToolTipText("收起按钮面板");
				refLabel.setIcon(ImageIconFactory.getCollapsedIcon());
				refPanel.setPreferredSize(new Dimension(refpanelWidth, 1000)); //
				WFGraphEditItemPanel.this.revalidate();
			}
		} else {// 收起相关按钮面板
			if (!(Boolean) (refLabel.getClientProperty("isExpand"))) {// 如果本来没有展开的则直接返回
				return;
			} else {
				refLabel.putClientProperty("isExpand", false);
				refLabel.setToolTipText("展开按钮面板");
				refLabel.setIcon(ImageIconFactory.getLeftCollapsedIcon());
				refPanel.setPreferredSize(new Dimension(10, 1000)); //
				WFGraphEditItemPanel.this.revalidate();
			}
		}
	}

	public BillListDialog getLookDialog(String _title, String _templetcode, String _relationkey) {
		BillListDialog dialog_list = new BillListDialog(this, _title, _templetcode, 900, 650);
		BillListPanel listpanel = dialog_list.getBilllistPanel();
		listpanel.setItemVisible("wfactivity_name", true);
		if (_relationkey == null) {
			listpanel.queryDataByCondition("wfprocess_id=" + this.processid, "wfactivity_name");
		} else {
			listpanel.queryDataByCondition(_relationkey + "='" + WFGraphEditItemPanel.TYPE_ACTIVITY + "' and wfprocess_id=" + this.processid, "wfactivity_name");
		}
		listpanel.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));
		listpanel.repaintBillListButton();
		listpanel.addBillListHtmlHrefListener(this);
		dialog_list.getBtn_confirm().setVisible(false);
		dialog_list.getBtn_cancel().setText("关闭");
		dialog_list.getBtn_cancel().setToolTipText("关闭");
		return dialog_list;
	}

	/**
	 * 删除所有流程中是增加状态的环节的相关信息，只判断看过的流程图
	 */
	public void deleteAllAddActivity() {
		StringBuffer sb_addActivityIds = new StringBuffer();
		if (this.isEditChanged()) {
			ArrayList addActivityIds = workFlowPanel.getAddActivityIds();
			if (addActivityIds.size() > 0) {
				sb_addActivityIds.append(tbutil.getInCondition(addActivityIds) + ",");
			}
		}
		if (sb_addActivityIds.length() > 0) {
			try {
				service.deleteActivityRelationByActivityIds(sb_addActivityIds.substring(0, sb_addActivityIds.length() - 1));
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}
	}

	/**
	 * 判断是否有流程编辑了但未保存
	 * 
	 * @return
	 */
	public boolean isWfEditChanged() {
		if (!editable) {// 如果流程不可编辑，则直接返回false
			return false;
		}
		if (!this.isEditChanged()) {
			return false;
		}
		if (MessageBox.confirm(this, "本流程已发生变化,是否需要保存?")) {
			this.onSaveWf(true);
		} else {
			deleteAllAddActivity();//删除新增环节的环节相关信息
		}
		return false;
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		// TODO Auto-generated method stub
		showHtmlDialog(_event.getBillListPanel(), _event.getItemkey());
	}

	/**
	 * 打开 html
	 * 
	 * @param _listPanel
	 */
	public void showHtmlDialog(BillListPanel _listpanel, String type) {
		BillVO billvo = _listpanel.getSelectedBillVO();
		String str_ruleid = null;
		if (type.equalsIgnoreCase("law_name")) {
			str_ruleid = billvo.getStringValue("LAW_ID", "");
			new LawShowHtmlDialog(_listpanel, str_ruleid, null);
		} else if (type.equalsIgnoreCase("rule_name")) {
			str_ruleid = billvo.getStringValue("RULE_ID", "");
			new RuleShowHtmlDialog(_listpanel, str_ruleid, null);
		}

	}
}
