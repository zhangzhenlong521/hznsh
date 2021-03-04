package cn.com.pushworld.salary.ui.posteval.p040;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.DivComponentLayoutUtil;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

/**
 * 岗位价值评估方案维护【李春娟/2013-10-21】
 * 方案状态：
 * 
 */
public class PostEvalWKPanel extends AbstractWorkPanel implements ActionListener {
	private WLTButton btn_submit;
	private String currPlanID;
	private String currPlanName;
	private String input_value_range = TBUtil.getTBUtil().getSysOptionStringValue("薪酬模块人员打分区间", "0-10");
	private String postids;
	private String loginuserid;
	private BillCellPanel currCellpanel;
	private boolean issubmit = false;
	private int targetlevel;
	private SalaryServiceIfc service = null;
	private boolean showWeight = TBUtil.getTBUtil().getSysOptionBooleanValue("岗位价值评估界面是否显示权重", true);
	private boolean showHistory = TBUtil.getTBUtil().getSysOptionBooleanValue("岗位价值评估界面是否可查看历史记录", true);
	private BillQueryPanel queryPanel;
	private QueryCPanel_UIRefPanel dateRef;
	private StringBuffer showWarnInfo = new StringBuffer();
	private JPanel jp = null;
	private String status = "";
	private boolean nullpost = false;
	private WLTPanel btnPanel;

	public void initialize() {
		try {
			showWarnInfo.append("等级要求(按其重要性分为1-10十个等级),任职分数(只填中层以上干部,不考虑等级要求,按其在该项的胜任程度分别打1-10分),不能漏填.");
			loginuserid = ClientEnvironment.getCurrLoginUserVO().getId();
			String[][] plans = UIUtil.getStringArrayByDS(null, "select id,planname,postids from sal_post_eval_plan where status ='评估中'");
			currCellpanel = new BillCellPanel();
			btn_submit = new WLTButton("提交", UIUtil.getImage("zt_050.gif"));
			btn_submit.setToolTipText("提交结果, 提交后不能修改");
			btn_submit.addActionListener(this);
			JLabel info_label = new JLabel("<html><font color='blue'>" + showWarnInfo.toString() + "</font></html>");
			info_label.setPreferredSize(new Dimension(800, 18));
			info_label.setVerticalTextPosition(JLabel.CENTER);
			btnPanel = new WLTPanel(new FlowLayout(FlowLayout.LEFT));
			btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			btnPanel.add(btn_submit);
			btnPanel.add(info_label);
			currCellpanel.add(btnPanel, BorderLayout.NORTH);
			currCellpanel.setEditable(false);
			currCellpanel.setToolBarVisiable(false); //
			currCellpanel.setAllowShowPopMenu(false); //
			if (showHistory) {
				queryPanel = new BillQueryPanel("SAL_POST_EVAL_PLAN_LCJ_Q01"); //
				if (plans != null && plans.length > 0) {//默认选中当前评价中中计划，并且统计出来
					dateRef = (QueryCPanel_UIRefPanel) queryPanel.getCompentByKey("planid");
					RefItemVO itemvo = new RefItemVO(plans[0][0], "", plans[0][1]);
					dateRef.setObject(itemvo);
					new Timer().schedule(new TimerTask() {
						public void run() {
							onQuery();
						}
					}, 0);
				} else {//如果没有评价中的计划，则找最近结束的计划
					HashVO[] planvos = UIUtil.getHashVoArrayByDS(null, "select * from SAL_POST_EVAL_PLAN where status='评估结束' order by enddate desc");
					if (planvos != null && planvos.length > 0) {//默认选中最后结束的计划，并且统计出来
						dateRef = (QueryCPanel_UIRefPanel) queryPanel.getCompentByKey("planid");
						RefItemVO itemvo = new RefItemVO(planvos[0].getStringValue("id"), "", planvos[0].getStringValue("planname"));
						dateRef.setObject(itemvo);
						new Timer().schedule(new TimerTask() {
							public void run() {
								onQuery();
							}
						}, 0);
					}
				}
				queryPanel.addBillQuickActionListener(this);
				this.add(queryPanel, new BorderLayout().NORTH);
				this.add(currCellpanel, new BorderLayout().CENTER);
			} else if (plans == null || plans.length == 0 || plans[0] == null) {
				currCellpanel.loadBillCellData(getMSGCellVo("目前全行没有评估中的计划"));
				currCellpanel.remove(btnPanel);
				this.add(currCellpanel);
			} else {
				currPlanID = plans[0][0];
				currPlanName = plans[0][1];
				String posts = plans[0][2];
				if (posts == null || "".equals(posts)) {
					currCellpanel.loadBillCellData(getMSGCellVo("没有找到可评估的岗位"));
					currCellpanel.remove(btnPanel);
					this.add(currCellpanel);
				} else {
					HashVO[] postvos = UIUtil.getHashVoArrayByDS(null, "select * from sal_post_eval_plan_post where planid=" + currPlanID + " order by seq");//按顺序（优先级由高到低）查询评分人和被评岗位的信息
					for (int i = 0; i < postvos.length; i++) {
						if (postvos[i].getStringValue("userids", "").contains(";" + loginuserid + ";")) {
							postids = postvos[i].getStringValue("postlist", "");
							break;
						}
					}
					if (postids == null || "".equals(postids)) {
						currCellpanel.loadBillCellData(getMSGCellVo("没有找到可评估的岗位"));
						currCellpanel.remove(btnPanel);
						this.add(currCellpanel);
					} else {
						this.add(getCheckScoreMainBillCellPanel());
						String str_targetlevel = UIUtil.getStringValueByDS(null, "select max(length(linkcode)) from sal_post_eval_target_copy where planid=" + currPlanID);
						targetlevel = Integer.parseInt(str_targetlevel) / 4;
					}
				}
			}
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void onQuery() {
		currCellpanel.putClientProperty("this", this);
		postids = "";
		if (queryPanel.checkValidate()) {
			try {
				RefItemVO itemvo = (RefItemVO) dateRef.getObject();
				currPlanID = itemvo.getId();
				currPlanName = itemvo.getName();
				HashVO[] postvos = UIUtil.getHashVoArrayByDS(null, "select * from sal_post_eval_plan_post where planid=" + currPlanID + " order by seq");//按顺序（优先级由高到低）查询评分人和被评岗位的信息
				status = UIUtil.getStringValueByDS(null, "select status from sal_post_eval_score where planid=" + currPlanID + " and scoreuser=" + loginuserid + " group by status");
				for (int i = 0; i < postvos.length; i++) {
					if (postvos[i].getStringValue("userids", "").contains(";" + loginuserid + ";")) {
						postids = postvos[i].getStringValue("postlist", "");
						break;
					}
				}
				if (postids == null || "".equals(postids)) {
					currCellpanel.loadBillCellData(getMSGCellVo("没有找到可评估的岗位"));
					if (!nullpost) {
						nullpost = true;
						currCellpanel.remove(btnPanel);
					}
					setCurrCellPanelStatus(currCellpanel, "已 提 交", false);
				} else {
					if(nullpost){
						nullpost = false;
						currCellpanel.add(btnPanel, BorderLayout.NORTH);
					}
					currCellpanel.loadBillCellData(getService().getPostEvalVO(currPlanID, currPlanName, loginuserid, postids));
					if ("已提交".equals(status)) {
						setCurrCellPanelStatus(currCellpanel, "已 提 交", true);
						currCellpanel.setEditable(false);
					} else {
						setCurrCellPanelStatus(currCellpanel, "已 提 交", false);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			String str_targetlevel = "";
			try {
				str_targetlevel = UIUtil.getStringValueByDS(null, "select max(length(linkcode)) from sal_post_eval_target_copy where planid=" + currPlanID);
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!nullpost) {
				int targetlevel = Integer.parseInt(str_targetlevel) / 4;
				if (showWeight) {
					currCellpanel.setLockedCell(targetlevel + 3, 2);
				} else {
					currCellpanel.setLockedCell(targetlevel + 2, 2);
				}
				currCellpanel.getTable().getColumnModel().getColumn(1).setPreferredWidth(120);
				new SalaryUIUtil().formatSpan(currCellpanel, new int[] { 0 });
			}

		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_submit) {//提交按钮的逻辑
			onSubmit();
		} else {//查询按钮的逻辑
			onQuery();
			issubmit = false;
		}
	}

	private void onSubmit() {
		if (issubmit || "已提交".equals(status)) {
			MessageBox.show(this, "已提交,请勿重复操作!");
			return;
		}
		currCellpanel.stopEditing();
		boolean iffind = false;
		int i = targetlevel + 2;
		if (showWeight) {
			i++;
		}
		for (; i < currCellpanel.getRowCount(); i++) {//行
			for (int j = 2; j < currCellpanel.getColumnCount(); j++) {//列
				BillCellItemVO itemVO = currCellpanel.getBillCellItemVOAt(i, j);
				if (itemVO != null && "Y".equals(itemVO.getIseditable())) {
					String value = itemVO.getCellvalue();
					if (value == null || "".equals(value) || "请填写".equals(value) || value.contains("范围")) {
						if (!iffind) {
							iffind = true;
						}
						itemVO.setCellvalue("请填写");
						itemVO.setBackground("246,53,89");
					}
				}
			}
		}
		if (iffind) {
			MessageBox.show(this, "部分数据没有填写!");
			currCellpanel.repaint();
		} else if (MessageBox.confirm(this, "提交后将不可修改,是否继续提交?")) {
			try {
				getService().submitPostEvalTable(currPlanID, loginuserid);
				issubmit = true;
				currCellpanel.setEditable(false);
				setCurrCellPanelStatus(currCellpanel, "已 提 交", true);
				MessageBox.show(this, "提交成功!");
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * 根据页签编号，获取BillCellPanel评分页面。
	 */
	private JPanel getCheckScoreMainBillCellPanel() throws Exception {
		currCellpanel = new BillCellPanel(getService().getPostEvalVO(currPlanID, currPlanName, loginuserid, postids));
		currCellpanel.setAllowShowPopMenu(false); //
		currCellpanel.putClientProperty("this", this);
		JPanel mainPanel = new WLTPanel(new BorderLayout());
		mainPanel.setOpaque(false);
		mainPanel.add(currCellpanel, BorderLayout.CENTER);
		JLabel info_label = new JLabel("<html><font color='blue'>" + showWarnInfo.toString() + "</font></html>");
		info_label.setPreferredSize(new Dimension(800, 18));
		info_label.setVerticalTextPosition(JLabel.CENTER);
		String status = UIUtil.getStringValueByDS(null, "select status from sal_post_eval_score where planid=" + currPlanID + " and scoreuser=" + loginuserid + " group by status");
		if ("已提交".equals(status)) {
			issubmit = true;
			currCellpanel.setEditable(false);
			WLTPanel btnPanel = new WLTPanel(new FlowLayout(FlowLayout.LEFT));
			btnPanel.add(info_label);
			mainPanel.add(btnPanel, BorderLayout.NORTH);
			setCurrCellPanelStatus(currCellpanel, "已 提 交", true);
		} else { //
			issubmit = false;
			btn_submit = new WLTButton("提交", UIUtil.getImage("zt_050.gif"));
			btn_submit.setToolTipText("提交结果, 提交后不能修改");
			btn_submit.addActionListener(this);
			WLTPanel btnPanel = new WLTPanel(new FlowLayout(FlowLayout.LEFT));
			btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			btnPanel.add(btn_submit);
			btnPanel.add(info_label);
			mainPanel.add(btnPanel, BorderLayout.NORTH);
		}
		String str_targetlevel = UIUtil.getStringValueByDS(null, "select max(length(linkcode)) from sal_post_eval_target_copy where planid=" + currPlanID);
		int targetlevel = Integer.parseInt(str_targetlevel) / 4;
		if (showWeight) {
			currCellpanel.setLockedCell(targetlevel + 3, 2);
		} else {
			currCellpanel.setLockedCell(targetlevel + 2, 2);
		}
		currCellpanel.getTable().getColumnModel().getColumn(1).setPreferredWidth(120);
		new SalaryUIUtil().formatSpan(currCellpanel, new int[] { 0 });
		return mainPanel;
	}

	private void setCurrCellPanelStatus(final JComponent parent, final String _status, final boolean hassub) {
		new Timer().schedule(new TimerTask() {
			public void run() {
				if (jp == null) {
					jp = new JPanel() {
						protected void paintComponent(Graphics g) {
							// 盖章
							if (parent != null && parent.isShowing()) { // 搞到bufferedimage中特别不清楚。
								super.paintComponent(g);
								Graphics2D g2d = (Graphics2D) g.create();
								g2d.translate(15, 8);
								g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
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
				}
				Point p = parent.getLocationOnScreen();
				SwingUtilities.convertPointFromScreen(p, parent.getRootPane().getLayeredPane());
				BillCellPanel cellPanel = (BillCellPanel) parent;
				int tablewidth = (int) cellPanel.getTable().getVisibleRect().getWidth();
				int tableheight = (int) cellPanel.getTable().getVisibleRect().getHeight();
				Rectangle rect = new Rectangle((tablewidth / 2 - 40), tableheight / 2, 180, 130);
				DivComponentLayoutUtil.getInstanse().removeComponentOnDiv(cellPanel, jp);
				if (hassub) {
					DivComponentLayoutUtil.getInstanse().addComponentOnDiv(cellPanel, jp, rect, null); // 添加浮动层
				}
			}
		}, 50);
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
		}
		return new int[] { begin, end };
	}

	private SalaryServiceIfc getService() throws Exception {
		if (service == null) {
			service = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
		}
		return service;
	}

	private BillCellVO getMSGCellVo(String msg) {
		BillCellVO vo = new BillCellVO();
		vo.setRowlength(1);
		vo.setCollength(1);
		BillCellItemVO[][] items = new BillCellItemVO[1][1];
		items[0][0] = getBillTitleCellItemVO(msg);
		items[0][0].setColwidth("300");
		vo.setCellItemVOs(items);
		return vo;
	}

	private BillCellItemVO getBillTitleCellItemVO(String value) {
		BillCellItemVO item = new BillCellItemVO();
		item.setIseditable("N");
		item.setCellvalue(value);
		item.setBackground("184,255,185");
		item.setFonttype("新宋体");
		item.setFontsize("12");
		item.setFontstyle("1");
		item.setSpan("1,1");
		return item;
	}
}
