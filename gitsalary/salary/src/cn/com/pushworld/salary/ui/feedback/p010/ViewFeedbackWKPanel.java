package cn.com.pushworld.salary.ui.feedback.p010;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.sysapp.login.RoleVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BackGroundDrawingUtil;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.HFlowLayoutPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractWLTCompentPanel;
import cn.com.infostrategy.ui.mdata.hmui.ninepatch.IconFactory;
import cn.com.pushworld.salary.ui.HorizontalandVerticalLayout;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;

/**
 * 反馈申诉界面。
 * @author haoming
 * create by 2013-12-30
 */
public class ViewFeedbackWKPanel extends AbstractWorkPanel implements ActionListener, BillListHtmlHrefListener {
	private static final long serialVersionUID = 1L;
	private TBUtil tbutil = new TBUtil();
	private WLTButton btn_create = new WLTButton("我要反馈", UIUtil.getImage("zt_062.gif"));
	private WLTPanel mainpanel = new WLTPanel(new BorderLayout());

	private WLTButton btn_reply, btn_delete;
	private boolean isadmin = false; //是否是管理员
	private BillListPanel listpanel;
	private String curruserid = ClientEnvironment.getCurrLoginUserVO().getId();
	private JPanel personPanel;
	private SalaryServiceIfc service;
	private int rowcount; // 总条数
	private int currpage = 1; // 当前页
	private int onpagerownum = 10;
	private JLabel infolabel = new JLabel();
	private HashVO uservo;

	public void initialize() {
		if ("admin".equalsIgnoreCase(ClientEnvironment.getCurrLoginUserVO().getCode())) {
			isadmin = true;
		}
		try {
			rowcount = Integer.parseInt(UIUtil.getStringValueByDS(null, "select count(id) from sal_feedback where parentid is null and createuser = " + curruserid));
			HashVO uservos[] = UIUtil.getHashVoArrayByDS(null, "select *  from v_sal_personinfo where id = " + ClientEnvironment.getCurrLoginUserVO().getId());
			if (uservos.length > 0) {
				uservo = uservos[0];
			}
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!isadmin) {
			String adminrole = getMenuConfMapValueAsStr("管理角色");
			if (!TBUtil.isEmpty(adminrole)) {
				String adminroles[] = tbutil.split(adminrole, ";");
				RoleVO rvos[] = ClientEnvironment.getCurrLoginUserVO().getRoleVOs();
				if (rvos != null) {
					for (int i = 0; i < rvos.length; i++) {
						for (int j = 0; j < adminroles.length; j++) {
							if (adminroles[j].equals(rvos[i].getCode())) {
								isadmin = true;
								break;
							}
						}
					}
				}
			}
		}
		this.setLayout(new BorderLayout());
		personPanel = new JPanel(new BorderLayout());
		btn_create.addActionListener(this);
		if (rowcount == 0) {
			btn_create.setPreferredSize(new Dimension(150, 60));
			btn_create.setSize(150, 60);
			btn_create.setFont(new Font("新宋体", Font.PLAIN, 16));
			personPanel.setLayout(new HorizontalandVerticalLayout());
			personPanel.add(btn_create);
		} else {
			initHaveData();
		}
		if (isadmin) {
			WLTTabbedPane tabpane = new WLTTabbedPane();
			listpanel = new BillListPanel("SAL_FEEDBACK_CODE1");
			btn_reply = new WLTButton("回复", UIUtil.getImage("office_137.gif"));
			btn_delete = new WLTButton("删除", UIUtil.getImage("zt_031.gif"));
			btn_reply.addActionListener(this);
			btn_delete.addActionListener(this);
			listpanel.addBatchBillListButton(new WLTButton[] { btn_reply, btn_delete });
			listpanel.repaintBillListButton();
			listpanel.addBillListHtmlHrefListener(this);
			tabpane.addTab("反馈管理", listpanel);
			tabpane.addTab("个人中心", personPanel);
			this.add(tabpane);
		} else {
			this.add(personPanel);
		}
	}

	public void initHaveData() {
		JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
		btn_create.setPreferredSize(new Dimension(80, 35));
		btn_create.setFont(LookAndFeel.font);
		northPanel.setOpaque(false);
		northPanel.add(btn_create);
		personPanel.setLayout(new BorderLayout());
		personPanel.add(northPanel, BorderLayout.NORTH);
		personPanel.add(mainpanel, BorderLayout.CENTER);
		loadNextData();
		infolabel.setHorizontalAlignment(JLabel.CENTER);
		infolabel.setVisible(false);
		personPanel.add(infolabel, BorderLayout.SOUTH);
	}

	private List<HashVO[]> alldata = new ArrayList<HashVO[]>();

	/**
	 * 加载下一页数据
	 */
	private synchronized void loadNextData() {
		try {
			if ((currpage - 1) * onpagerownum >= rowcount) {
				return;
			}
			int oldIndex = alldata.size();//旧位置
			loading = true;
			List ask_answer = getService().getFeedbackDataHashvo("select id from sal_feedback where parentid is null and createuser = " + curruserid + " order by createtime desc", new int[] { (currpage - 1) * onpagerownum + 1, currpage * onpagerownum });
			alldata.addAll(ask_answer);
			loadDate(ask_answer, oldIndex, 1);
			currpage++;
			infolabel.setVisible(false);
			loading = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	JComponent old = null;
	VFlowLayoutPanel flowlayout;
	private HashMap<String, OneRowPanel> history = new HashMap<String, OneRowPanel>();
	private boolean loading = false;
	Vector<OneRowPanel> vc = new Vector<OneRowPanel>();

	private void refreshNum() {
		OneRowPanel[] com = vc.toArray(new OneRowPanel[0]);
		for (int i = 0; i < com.length; i++) {
			com[i].setBackground((i + 1) % 2 == 0 ? LookAndFeel.tablebgcolor : new Color(241, 248, 252, 200));
			com[i].setRowNum(i + 1);
			com[i].islast = false;
		}
		if (com.length > 0) {
			com[com.length - 1].islast = true;
		}
		updateUI();
	}

	/**
	 * 加载新数据方法
	 * @param ask_answer  传入的记录
	 * @param _stopIndex  停止位置编号
	 * @param before_after 最前最后
	 */
	private void loadDate(List ask_answer, int _stopIndex, int before_after) {
		old = null;
		TBUtil tbutil = TBUtil.getTBUtil();
		try {
			List myquestion = new ArrayList<List>();
			List<JComponent> list = new ArrayList<JComponent>();
			List rowlist = new ArrayList<VFlowLayoutPanel>();
			if (ask_answer.size() > 0) {
				for (int i = 0; i < ask_answer.size(); i++) {
					HashVO[] hvo = (HashVO[]) ask_answer.get(i);
					final OneRowPanel rowpanel = new OneRowPanel(hvo, i + 1);
					rowpanel.addMouseMoveListener(new MouseMoveListener() {
						public void mouseExited(MouseEvent event) {
							rowpanel.getBtnpanel().setVisible(false);
						}

						public void mouseEnter(MouseEvent event) {
							rowpanel.getBtnpanel().setVisible(true);
						}
					});
					WLTButton btn_update = new WLTButton(UIUtil.getImage("note_edit.png"));
					btn_update.putClientProperty("panel", rowpanel);
					btn_update.addActionListener(this);
					btn_update.setToolTipText("编辑");
					WLTButton btn_delete = new WLTButton(UIUtil.getImage("del.gif"));
					btn_delete.putClientProperty("panel", rowpanel);
					btn_delete.setToolTipText("删除");
					btn_delete.addActionListener(this);
					rowpanel.getBtnpanel().setVisible(false);
					rowpanel.getBtnpanel().add(btn_update);
					rowpanel.getBtnpanel().add(btn_delete);
					if (hvo.length > 1) {
						btn_update.setVisible(false);
						btn_delete.setVisible(false);
					}
					rowpanel.putClientProperty("obj", hvo[0]);
					if (i == _stopIndex) {
						old = rowpanel;
					}
					if (before_after == 0) {
						vc.add(0, rowpanel);
					} else {
						vc.add(rowpanel);
					}
					history.put(hvo[0].getStringValue("id"), rowpanel);
					rowlist.add(rowpanel);
				}
			}
			if (flowlayout == null) {
				flowlayout = new VFlowLayoutPanel(rowlist, 2, true);
				flowlayout.setOpaque(false);
				mainpanel.add(flowlayout, BorderLayout.CENTER);
			} else {
				flowlayout.addPanel((JComponent[]) rowlist.toArray(new JComponent[0]), before_after);
			}
			final JScrollPane scroll = flowlayout.getScollPanel();
			if (scroll != null) {
				scroll.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
					public void adjustmentValueChanged(AdjustmentEvent adjustmentevent) {
						int maxheight = adjustmentevent.getAdjustable().getMaximum();
						if (loading) {
							return;
						}
						if (maxheight <= (adjustmentevent.getValue() + scroll.getVerticalScrollBar().getVisibleAmount())) {
							if ((currpage - 1) * onpagerownum >= rowcount) {
								infolabel.setText("数据已经全部加载.");
								infolabel.setIcon(null);
								infolabel.setVisible(true);
							} else {
								loading = true;
								infolabel.setText("正在努力加载数据...");
								infolabel.setIcon(UIUtil.getImage("loading_16x16.gif"));
								infolabel.setVisible(true);
								personPanel.updateUI();
								new Timer().schedule(new TimerTask() {
									public void run() {
										loadNextData();
									}
								}, 0);
							}
						} else {
							infolabel.setVisible(false);
						}
					}
				});
			}
			refreshNum();
			mainpanel.updateUI();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//新增
	private void onCreate() {
		BillCardDialog carddialog = new BillCardDialog(this, "SAL_FEEDBACK_CODE1", WLTConstants.BILLDATAEDITSTATE_INSERT);
		BillCardPanel cardpanel = carddialog.getCardPanel();
		cardpanel.setValueAt("groupid", cardpanel.getValueAt("id"));
		carddialog.setVisible(true);
		if (carddialog.getCloseType() == 1) {
			if (rowcount == 0) {
				initHaveData();
			}
			List<HashVO[]> list = new ArrayList<HashVO[]>();
			HashVO hvo = cardpanel.getBillVO().convertViewToHashVO();
			if (uservo != null) {
				hvo.setAttributeValue("sex", uservo.getAttributeValue("sex"));
			}
			list.add(0, new HashVO[] { hvo });
			alldata.add(0, new HashVO[] { hvo });
			loadDate(list, 0, 0);
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_create) {
			onCreate();
		} else if (e.getSource() == btn_reply) {
			BillCardDialog carddialog = new BillCardDialog(this, "SAL_FEEDBACK_CODE1", WLTConstants.BILLDATAEDITSTATE_INSERT);
			carddialog.setTitle("回复");
			carddialog.getBillcardPanel().setVisiable("title", false);
			BillVO hvo = listpanel.getSelectedBillVO();
			if (hvo == null) {
				MessageBox.showSelectOne(listpanel);
				return;
			}
			carddialog.getBillcardPanel().setValueAt("parentid", new StringItemVO(hvo.getStringValue("id")));
			carddialog.getBillcardPanel().setValueAt("groupid", new StringItemVO(hvo.getStringValue("groupid")));
			carddialog.setVisible(true);
			if (carddialog.getCloseType() == 1) {
				try {
					UIUtil.executeUpdateByDS(null, "update SAL_FEEDBACK set status='已回复' where id = " + hvo.getStringValue("id"));
					listpanel.refreshCurrSelectedRow();
				} catch (WLTRemoteException e1) {
					e1.printStackTrace();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		} else if (e.getSource() == btn_delete) {
			BillVO hvo = listpanel.getSelectedBillVO();
			if (hvo == null) {
				MessageBox.showSelectOne(listpanel);
				return;
			}
			if (hvo != null) {
				String id = hvo.getStringValue("id");
				if (MessageBox.confirm(mainpanel, "确定要删除此记录吗?")) {
					try {
						UIUtil.executeUpdateByDS(null, "delete from SAL_FEEDBACK where id = '" + id + "' or parentid='" + id + "'");
						listpanel.removeSelectedRow();
					} catch (WLTRemoteException e1) {
						e1.printStackTrace();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		} else if (e.getSource() instanceof JMenuItem) {
			JMenuItem menu = (JMenuItem) e.getSource();
			if ("删除".equals(menu.getText())) {
				HashVO hvo = (HashVO) menu.getClientProperty("obj");
				if (hvo != null) {
					String id = hvo.getStringValue("id");
					if (MessageBox.confirm(mainpanel, "确定要删除此记录吗?")) {
						try {
							UIUtil.executeUpdateByDS(null, "delete from SAL_FEEDBACK where id = '" + id + "' or parentid='" + id + "'");
							int index = 0;
							for (int i = 0; i < alldata.size(); i++) {
								HashVO hvos[] = alldata.get(i);
								if (hvos.length > 0) {
									if (id.equals(hvos[0].getStringValue("id"))) {
										alldata.remove(i);
										index = i;
										JComponent com = history.get(hvos[0].getStringValue("id"));
										com.setVisible(false);
										vc.remove(com);
										history.remove(hvos[0].getStringValue("id"));
										refreshNum();
										break;
									}
								}
							}
						} catch (WLTRemoteException e1) {
							e1.printStackTrace();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}

			}
		} else {
			if (e.getSource() instanceof WLTButton) {
				WLTButton btn = (WLTButton) e.getSource();
				OneRowPanel rowpanel = (OneRowPanel) btn.getClientProperty("panel");
				if (rowpanel != null) {
					boolean refresh = true;
					if ("删除".equals(btn.getToolTipText())) {
						HashVO hvo = (HashVO) rowpanel.getClientProperty("obj");
						if (hvo != null) {
							String id = hvo.getStringValue("id");
							if (MessageBox.confirm(mainpanel, "确定要删除此记录吗?")) {
								try {
									UIUtil.executeUpdateByDS(null, "delete from SAL_FEEDBACK where id = '" + id + "' or parentid='" + id + "'");
									for (int i = 0; i < alldata.size(); i++) {
										HashVO hvos[] = alldata.get(i);
										if (hvos.length > 0) {
											if (id.equals(hvos[0].getStringValue("id"))) {
												alldata.remove(i);
												JComponent com = history.get(hvos[0].getStringValue("id"));
												com.setVisible(false);
												vc.remove(com);
												history.remove(hvos[0].getStringValue("id"));
												refreshNum();
												refresh = false;
												break;
											}
										}
									}
								} catch (WLTRemoteException e1) {
									e1.printStackTrace();
								} catch (Exception e1) {
									e1.printStackTrace();
								}
							}
						}
					} else {
						HashVO hvo = (HashVO) rowpanel.getClientProperty("obj");
						BillCardDialog dialog = new BillCardDialog(mainpanel, "SAL_FEEDBACK_CODE1", WLTConstants.BILLDATAEDITSTATE_UPDATE);
						dialog.getBillcardPanel().queryDataByCondition(" id= " + hvo.getStringValue("id"));
						dialog.getBillcardPanel().setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
						dialog.setVisible(true);
						if (dialog.getCloseType() == 1) {
							BillVO bvo = dialog.getBillcardPanel().getBillVO();
							rowpanel.setHashVO(bvo.convertToHashVO(), "content");
						}
					}
					if (refresh) {
						Point mousepoint = MouseInfo.getPointerInfo().getLocation();
						double m_x = mousepoint.getX();
						double m_y = mousepoint.getY();
						double p_x = rowpanel.getLocationOnScreen().getX();
						double p_y = rowpanel.getLocationOnScreen().getY();
						if (!(m_x >= p_x && m_x <= (p_x + rowpanel.getWidth()) && m_y >= p_y && m_y <= (p_y + rowpanel.getY()))) {
							rowpanel.getBtnpanel().setVisible(false);
						}
					}
				}
			}
		}

	}

	private SalaryServiceIfc getService() {
		if (service == null) {
			try {
				service = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return service;
	}

	private Icon man = UIUtil.getImage("office_084.gif");
	private Icon woman = UIUtil.getImage("office_015.gif");

	class OneRowPanel extends JComponent implements MouseListener {
		JLabel numlabel = new JLabel();
		private boolean islast = false;
		private int rownum;
		private WLTButton btn_update, btn_delete;
		private JPanel btnpanel = new WLTPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
		private JTextArea firstTextArea = null;
		private ChatBubblePanel firstPanel = null;

		public JPanel getBtnpanel() {
			return btnpanel;
		}

		public OneRowPanel(HashVO[] hvo, int _rowNum) {
			this(hvo, _rowNum, false);
		}

		public OneRowPanel(HashVO[] hvo, int _rowNum, boolean showName) {
			ArrayList<JComponent> onask_panel = new ArrayList<JComponent>();
			if (hvo.length == 0) {
				return;
			}
			HashVO groupvo = hvo[0];
			for (int j = 0; j < hvo.length; j++) {
				ChatBubblePanel panel = null;
				JPanel row_panel = null;
				if (groupvo.getStringValue("createuser", "").equals(hvo[j].getStringValue("createuser", ""))) {
					panel = new ChatBubblePanel(ChatBubblePanel.DIRECTION_TYPE_LEFT);
					panel.setBackground(new Color(0, 238, 0, 80));
					row_panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				} else {
					panel = new ChatBubblePanel(ChatBubblePanel.DIRECTION_TYPE_RIGHT);
					row_panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
					panel.setBackground(new Color(238, 238, 209, 80));
				}
				JTextArea textpane = new JTextArea();
				if (j == 0) {
					firstPanel = panel;
					firstTextArea = textpane;
				}
				textpane.putClientProperty("obj", hvo[j]);
				textpane.setEditable(false);
				textpane.setOpaque(false);
				String msg = hvo[j].getStringValue("content");
				String str[] = msg.split("\n");
				int length = 0;
				for (int k = 0; k < str.length; k++) {
					length = Math.max(str[k].length(), length);
				}
				if (length > 50) {
					textpane.setColumns(50);
					textpane.setLineWrap(true);
				}
				textpane.setText(msg);
				panel.setLayout(new BorderLayout());
				panel.add(textpane);
				String labeltext = "";
				if (j == 0) {
					numlabel.setText((_rowNum + " "));
					labeltext = hvo[j].getStringValue("createtime").substring(0, 10);
				} else {
					labeltext = hvo[j].getStringValue("createtime").substring(0, 10);
				}
				JLabel createdate = new JLabel(labeltext);
				Font f = new Font("宋体", Font.PLAIN, 11);
				createdate.setFont(f);
				JPanel row_main_panel = new JPanel(new BorderLayout()); //每一行的总面板

				JPanel row_date_panel = null; //行上面日期
				if (groupvo.getStringValue("createuser", "").equals(hvo[j].getStringValue("createuser", ""))) {
					createdate.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
					row_date_panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				} else {
					createdate.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
					row_date_panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
				}
				if (j == 0) {
					row_date_panel.add(numlabel);

				}
				row_date_panel.add(createdate);
				row_date_panel.setOpaque(false);
				row_main_panel.add(row_date_panel, BorderLayout.NORTH);
				row_main_panel.setOpaque(false);
				JLabel image_iconlabel = null;
				if ("女".equals(hvo[j].getStringValue("sex"))) {
					image_iconlabel = new JLabel(woman);
				} else {
					image_iconlabel = new JLabel(man);
				}
				if (groupvo.getStringValue("createuser", "").equals(hvo[j].getStringValue("createuser", ""))) {
					row_panel.add(image_iconlabel);
					if (showName) {
						row_panel.add(new JLabel(hvo[j].getStringValue("username", "")));
					}
					row_panel.add(panel);
				} else {
					row_panel.add(panel);
					row_panel.add(new JLabel(hvo[j].getStringValue("username", "")));
					row_panel.add(image_iconlabel);
				}
				if (j == 0) {
					row_panel.add(btnpanel);
				}
				row_panel.setOpaque(false);
				row_panel.setBorder(BorderFactory.createEmptyBorder());
				row_main_panel.add(row_panel, BorderLayout.CENTER);
				onask_panel.add(row_main_panel);
			}
			VFlowLayoutPanel flowpane = new VFlowLayoutPanel(onask_panel, 0, false);
			this.setLayout(new BorderLayout());
			this.add(flowpane);
			this.addMouseListener(this);
			this.setBorder(BorderFactory.createEmptyBorder(6, 8, 3, 6));
		}

		public void setRowNum(int _rowNum) {
			rownum = _rowNum;
			numlabel.setText(rownum + " ");
		}

		@Override
		protected void paintBorder(Graphics g) {
			IconFactory.getInstance().getPanelQueryItem_BG().draw((Graphics2D) g, 0, 0, this.getWidth(), this.getHeight());
		}

		public void mouseClicked(MouseEvent mouseevent) {

		}

		public void mouseEntered(MouseEvent mouseevent) {
			if (listener != null && mouseevent.getY() >= 0 && mouseevent.getY() <= getHeight()) {
				listener.mouseEnter(mouseevent);
			}
		}

		public void mouseExited(MouseEvent mouseevent) {
			if (listener != null && mouseevent.getY() <= 0 || mouseevent.getY() >= getHeight() || mouseevent.getX() <= 0 || mouseevent.getX() >= getWidth()) {
				listener.mouseExited(mouseevent);
			}
		}

		public void mousePressed(MouseEvent mouseevent) {

		}

		public void mouseReleased(MouseEvent mouseevent) {

		}

		private MouseMoveListener listener;

		public void addMouseMoveListener(MouseMoveListener _listener) {
			listener = _listener;
		}

		public void setHashVO(HashVO _hvo, String _toStringItem) {
			firstTextArea.putClientProperty("obj", _hvo);
			this.putClientProperty("obj", _hvo);
			firstTextArea.setText(_hvo.getStringValue(_toStringItem));
			firstPanel.updataUI();
		}
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent event) {
		BillListPanel listpanel = event.getBillListPanel();
		BillVO selectvo = listpanel.getSelectedBillVO();
		try {
			int count = Integer.parseInt(UIUtil.getStringValueByDS(null, "select count(id) from sal_feedback where groupid='" + selectvo.getPkValue() + "'"));
			List ask_answer = getService().getFeedbackDataHashvo("select id from sal_feedback where groupid = '" + selectvo.getPkValue() + "'order by createtime desc", new int[] { 1, count });
			WLTPanel mainpanel = new WLTPanel(new BorderLayout());
			List myquestion = new ArrayList<List>();
			List<JComponent> list = new ArrayList<JComponent>();
			List rowlist = new ArrayList<VFlowLayoutPanel>();
			int before_after = 1;
			int _stopIndex = 1;
			VFlowLayoutPanel flowlayout = null;
			if (ask_answer.size() > 0) {
				for (int i = 0; i < ask_answer.size(); i++) {
					HashVO[] hvo = (HashVO[]) ask_answer.get(i);
					OneRowPanel rowpanel = new OneRowPanel(hvo, i + 1, true);
					rowlist.add(rowpanel);
				}
			}
			if (flowlayout == null) {
				flowlayout = new VFlowLayoutPanel(rowlist, 2, true);
				flowlayout.setOpaque(false);
				mainpanel.add(flowlayout, BorderLayout.CENTER);
			} else {
				flowlayout.addPanel((JComponent[]) rowlist.toArray(new JComponent[0]), before_after);
			}
			final BillDialog dialog = new BillDialog(this, 800, 500);
			dialog.getContentPane().add(mainpanel, BorderLayout.CENTER);
			WLTPanel btnpanel = new WLTPanel(new FlowLayout());
			WLTButton btn_close = new WLTButton("关闭");
			btn_close.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent actionevent) {
					dialog.dispose();
				}
			});
			btnpanel.add(btn_close);
			dialog.getContentPane().add(btnpanel, BorderLayout.SOUTH);
			dialog.setVisible(true);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

interface MouseMoveListener extends EventListener {
	public void mouseEnter(MouseEvent _event);

	public void mouseExited(MouseEvent _event);

}

class VFlowLayoutPanel extends WLTPanel {

	private static final long serialVersionUID = 1L;
	private JComponent[] compents = null; //
	private JScrollPane scollPanel = null; //滚动框

	private JPanel[] panels = null;
	HashMap hm_compents = new HashMap(); //

	private int li_vgap = 7; //各个控件之间的间距

	private boolean needscroll = false;

	public VFlowLayoutPanel(java.util.List _list, boolean _needscroll) {
		super();
		compents = (JComponent[]) _list.toArray(new JComponent[0]); //
		needscroll = _needscroll;
		initialize(); //
	}

	public VFlowLayoutPanel(java.util.List _list, int _vgap, boolean _needscroll) {
		super();
		compents = (JComponent[]) _list.toArray(new JComponent[0]); //
		this.li_vgap = _vgap; //
		needscroll = _needscroll;
		initialize(); //
	}

	public VFlowLayoutPanel(java.util.List _list, Color _bgcolor) {
		super(BackGroundDrawingUtil.SINGLE_COLOR_UNCHANGE, _bgcolor);
		compents = (JComponent[]) _list.toArray(new JComponent[0]); //
		initialize(); //
	}

	public VFlowLayoutPanel(java.util.List _list, int _shadeType, Color _bgcolor) {
		super(_shadeType, _bgcolor);
		compents = (JComponent[]) _list.toArray(new JComponent[0]); //
		initialize(); //
	}

	public void setRowItemVisiable(int _row, boolean _visiable) {
		JComponent compent = compents[_row]; //
		if (compent instanceof HFlowLayoutPanel) {
			HFlowLayoutPanel flowPanel = (HFlowLayoutPanel) compent; //
			JComponent[] rowCompents = flowPanel.getAllCompents(); //
			for (int j = 0; j < rowCompents.length; j++) { //
				AbstractWLTCompentPanel realCompent = (AbstractWLTCompentPanel) rowCompents[j]; //
				realCompent.setVisible(_visiable);
			}
		}
	}

	private JPanel lastPane = null;
	private JPanel firstPane = null;

	private void initialize() {
		BorderLayout layout = new BorderLayout(); //
		this.setLayout(layout); //
		this.setOpaque(false);
		panels = new JPanel[compents.length]; //
		for (int i = 0; i < panels.length; i++) {
			panels[i] = new WLTPanel(new BorderLayout(0, li_vgap)); //!!
		}
		for (int i = compents.length - 1; i >= 0; i--) { //从最后一个往前加!!
			if (i == compents.length - 1) { //如果是最后一个,则在CENTER中加入一个空面板!!!
				panels[i].add(compents[i], BorderLayout.NORTH); //
				JPanel freePanel = new WLTPanel();
				freePanel.setPreferredSize(new Dimension(0, 0)); //
				panels[i].add(freePanel, BorderLayout.CENTER); //
				lastPane = panels[i];
			} else {
				panels[i].add(compents[i], BorderLayout.NORTH); //
				panels[i].add(panels[i + 1], BorderLayout.CENTER); //
			}
		}
		if (panels.length > 0) { //如果宽度大于0
			firstPane = new WLTPanel(new BorderLayout());
			firstPane.add(panels[0], BorderLayout.CENTER);
			if (needscroll) {
				panels[0].setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0)); //
				scollPanel = new JScrollPane(firstPane); //滚动框..
				scollPanel.setBorder(BorderFactory.createEmptyBorder()); //
				scollPanel.setOpaque(false);
				scollPanel.getViewport().setOpaque(false);
				//设置垂直方向滚动速度
				scollPanel.getVerticalScrollBar().setUnitIncrement(100);
				this.add(scollPanel); //
			} else {
				this.add(firstPane);
			}
		}
	}

	// 0 前,1后
	public void addPanel(JComponent[] compents, int befor_or_after) {
		JPanel[] panels = new JPanel[compents.length]; //
		for (int i = 0; i < panels.length; i++) {
			panels[i] = new WLTPanel(new BorderLayout(0, li_vgap)); //!!
			panels[i].setOpaque(false);
		}
		for (int i = compents.length - 1; i >= 0; i--) { //从最后一个往前加!!
			if (i == compents.length - 1) { //如果是最后一个,则在CENTER中加入一个空面板!!!
				panels[i].add(compents[i], BorderLayout.NORTH); //
				JPanel freePanel = new WLTPanel();
				freePanel.setPreferredSize(new Dimension(0, 0)); //
				panels[i].add(freePanel, BorderLayout.CENTER); //
			} else {
				panels[i].add(compents[i], BorderLayout.NORTH); //
				panels[i].add(panels[i + 1], BorderLayout.CENTER); //
			}
		}
		if (befor_or_after == 0) {
			JPanel fpanel = new WLTPanel(new BorderLayout());
			fpanel.add(panels[0]);
			firstPane.add(fpanel, BorderLayout.NORTH);
			firstPane = fpanel;
		} else {
			lastPane.add(panels[0], BorderLayout.CENTER);
			lastPane = panels[panels.length - 1];
			lastPane.updateUI();
		}
	}

	public void setRowVisiable(int _row, boolean _visiable) {
		compents[_row].setVisible(_visiable); //
	}

	public JComponent getContentPanel() {
		return panels[0]; //
	}

	public int getContentCount() {
		return panels.length; //
	}

	public JScrollPane getScollPanel() {
		return scollPanel;
	}

	public JComponent[] getAllCompents() {
		return compents;
	}
}
