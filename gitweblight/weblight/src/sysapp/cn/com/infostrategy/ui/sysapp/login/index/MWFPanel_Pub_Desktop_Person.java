package cn.com.infostrategy.ui.sysapp.login.index;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTPanelUI;
import cn.com.infostrategy.ui.common.WLTSplitPane;

/**
 * 首页个人定制
 */
public class MWFPanel_Pub_Desktop_Person extends AbstractWorkPanel implements MouseListener {

	private static final long serialVersionUID = 1L;
	private JPanel person_Desk, personPanel, default_Desk, defaultpanel = null;
	private WLTSplitPane sp = null;
	private Border border1 = BorderFactory.createLineBorder(Color.GRAY, 1); //
	private Cursor cursor = new Cursor(Cursor.HAND_CURSOR); //
	private Color textColor = new Color(60, 60, 60);
	private WLTButton add, del, save = null;
	private HashMap selfdeskMap = new HashMap();
	private int xspit = 5;
	private int yspit = 5;
	private int width = 80;
	private int height = 80;
	private int hspit = 10;
	private int vspit = 10;

	public void initialize() {
		this.setLayout(new BorderLayout());
		JPanel defaultP = getDefault_Desk();
		sp = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, getPerson_Desk(), defaultP);
		sp.setDividerLocation(300);
		this.add(sp, BorderLayout.CENTER);
	}

	public JPanel getPerson_Desk() {
		if (personPanel != null) {
			return personPanel;
		}
		person_Desk = new JPanel();
		person_Desk.setLayout(null);
		person_Desk.setOpaque(false);
		person_Desk.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		new MyDraggListener(person_Desk);
		new MyDropListener(person_Desk);
		try {
			HashMap _parMap = new HashMap();
			_parMap.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
			HashMap defaultmap = UIUtil.getCommonService().commMethod("cn.com.infostrategy.bs.sysapp.login.SysAppServiceImpl", "getDeskTopVO_Person", _parMap);
			Object defaultvos = defaultmap.get("vos");
			int li_x = 0, li_y = 0;
			if (defaultvos != null) {
				HashVO[] _hvs = (HashVO[]) defaultvos;
				boolean isFirstHalft = false;
				int li_width = 0; //宽度
				String str_id = null;
				String str_title = null;
				String str_imgName = null;
				String str_viewCols = null;
				for (int i = 0; i < _hvs.length; i++) {
					str_id = _hvs[i].getStringValue("id");
					str_title = _hvs[i].getStringValue("title");
					str_imgName = _hvs[i].getStringValue("imgicon", "office_066.gif"); //
					str_viewCols = _hvs[i].getStringValue("viewcols");
					if ("全列".equals(str_viewCols)) { //如果是全列!
						li_x = xspit; //
						if (i == 0) {
							li_y = yspit;
						} else {
							li_y = li_y + height + vspit;
						}
						li_width = width * 2 + hspit;
					} else {
						if (isFirstHalft) {
							li_x = li_x + hspit + width;
						} else {
							li_x = xspit;
							if (i == 0) {
								li_y = yspit;
							} else {
								li_y = li_y + height + vspit;
							}
						}
						li_width = width;
					}
					JPanel itemPanel = getItemPanel(str_id, str_imgName, str_title, "SELF", str_viewCols, li_x + "," + li_y);
					itemPanel.setBounds(li_x, li_y, li_width, height);
					selfdeskMap.put(li_x + "," + li_y, itemPanel);
					person_Desk.add(itemPanel);
					if (li_x == xspit && "半列".equals(str_viewCols)) {
						isFirstHalft = true;
					} else {
						isFirstHalft = false;
					}
				}
			}
			int defaultheight = default_Desk.getPreferredSize().height;
			int currheight = li_y + height + yspit;
			person_Desk.setPreferredSize(new Dimension(xspit * 2 + width * 2 + hspit, currheight < defaultheight ? defaultheight : currheight));
		} catch (Exception e) {
			e.printStackTrace();
		}
		JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0));
		contentPanel.setOpaque(false);
		contentPanel.setFocusable(true);
		contentPanel.add(person_Desk);
		JScrollPane scrollPanel = new JScrollPane(contentPanel);
		scrollPanel.setFocusable(true);
		scrollPanel.getVerticalScrollBar().setUnitIncrement(20);
		scrollPanel.setOpaque(false);
		scrollPanel.getViewport().setOpaque(false);
		personPanel = new JPanel();
		personPanel.setBackground(LookAndFeel.desktop_Background);
		personPanel.setLayout(new BorderLayout());
		personPanel.setUI(new WLTPanelUI(WLTPanel.HORIZONTAL_FROM_MIDDLE, false));
		personPanel.add(scrollPanel, BorderLayout.CENTER);
		JPanel tool = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tool.setOpaque(false);
		JLabel desc = new JLabel("定制首页");
		desc.setForeground(Color.BLUE);
		add = new WLTButton("加高", "office_081.gif");
		add.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				addLength();
			}
		});
		del = new WLTButton("减高", "office_059.gif");
		del.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				delLength();
			}
		});
		save = new WLTButton("保存", "zt_036.gif");
		save.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				save();
			}
		});

		tool.add(desc);
		tool.add(add);
		tool.add(del);
		tool.add(save);
		personPanel.add(tool, BorderLayout.NORTH);
		new MyDraggListener(personPanel);
		new MyDropListener(personPanel);
		return personPanel;

	}

	public JPanel getDefault_Desk() {
		if (defaultpanel != null) {
			return defaultpanel;
		}
		default_Desk = new WLTPanel(WLTPanel.HORIZONTAL_FROM_MIDDLE, new BorderLayout(), LookAndFeel.cardbgcolor, false);
		default_Desk.setLayout(null);
		default_Desk.setOpaque(false);
		try {
			HashMap _parMap = new HashMap();
			_parMap.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
			HashMap defaultmap = UIUtil.getCommonService().commMethod("cn.com.infostrategy.bs.sysapp.login.SysAppServiceImpl", "getDeskTopVO_Default", _parMap);
			Object defaultvos = defaultmap.get("vos");
			if (defaultvos != null && ((HashVO[]) defaultvos).length > 0) {
				HashVO[] _hvs = (HashVO[]) defaultvos;
				int li_x = 0, li_y = 0;
				boolean isFirstHalft = false;
				int li_width = 0; //宽度
				for (int i = 0; i < _hvs.length; i++) {
					String str_id = _hvs[i].getStringValue("id");
					String str_title = _hvs[i].getStringValue("title");
					String str_imgName = _hvs[i].getStringValue("imgicon", "office_066.gif"); //
					String str_viewCols = _hvs[i].getStringValue("viewcols");
					if ("全列".equals(str_viewCols)) { //如果是全列!
						li_x = xspit; //
						if (i == 0) {
							li_y = yspit;
						} else {
							li_y = li_y + height + vspit;
						}
						li_width = width * 2 + hspit;
					} else {
						if (isFirstHalft) {
							li_x = li_x + hspit + width;
						} else {
							li_x = xspit;
							if (i == 0) {
								li_y = yspit;
							} else {
								li_y = li_y + height + vspit;
							}
						}
						li_width = width;
					}
					JPanel itemPanel = getItemPanel(str_id, str_imgName, str_title, "ALL", str_viewCols, li_x + "," + li_y);
					itemPanel.setBounds(li_x, li_y, li_width, height);
					default_Desk.add(itemPanel);
					if (li_x == xspit && "半列".equals(str_viewCols)) {
						isFirstHalft = true;
					} else {
						isFirstHalft = false;
					}
				}
				default_Desk.setPreferredSize(new Dimension(xspit * 2 + width * 2 + hspit, li_y + height + yspit));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0));
		contentPanel.setOpaque(false);
		contentPanel.setFocusable(true);
		contentPanel.add(default_Desk);
		JScrollPane scrollPanel = new JScrollPane(contentPanel);
		scrollPanel.setFocusable(true);
		scrollPanel.getVerticalScrollBar().setUnitIncrement(20);
		scrollPanel.setOpaque(false);
		scrollPanel.getViewport().setOpaque(false);
		defaultpanel = new JPanel();
		defaultpanel.setBackground(LookAndFeel.desktop_Background);
		defaultpanel.setLayout(new BorderLayout());
		defaultpanel.setUI(new WLTPanelUI(WLTPanel.HORIZONTAL_FROM_MIDDLE, false));
		defaultpanel.add(scrollPanel, BorderLayout.CENTER);
		JPanel tool = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tool.setOpaque(false);
		JLabel desc = new JLabel("所有板块(请拖动进行添加)");
		desc.setForeground(Color.BLUE);
		tool.add(desc);
		defaultpanel.add(tool, BorderLayout.NORTH);
		return defaultpanel;
	}

	private JPanel getItemPanel(final String _id, final String _imgName, final String _text, final String dragtype, final String str_viewCols, final String address) {
		JPanel panel = null;
		if ("astring".equals(_id)) {
			//	panel = new WLTPanel(WLTPanel.VERTICAL_OUTSIDE_TO_MIDDLE, new BorderLayout(), LookAndFeel.cardbgcolor, false);
			panel = new ItemPanel(true);
			panel.setUI(new WLTPanelUI(WLTPanel.VERTICAL_OUTSIDE_TO_MIDDLE, LookAndFeel.cardbgcolor, false, true));
		} else {
			if ("SELF".equals(dragtype)) {
				panel = new ItemPanel(true);
				panel.setUI(new WLTPanelUI(WLTPanel.VERTICAL_OUTSIDE_TO_MIDDLE, LookAndFeel.cardbgcolor, false, true));
			} else {
				panel = new ItemPanel(false);
				panel.setUI(new WLTPanelUI(WLTPanel.VERTICAL_LIGHT2, LookAndFeel.cardbgcolor, false, true));
			}
			//	panel = new WLTPanel(WLTPanel.VERTICAL_LIGHT2, new BorderLayout(), LookAndFeel.cardbgcolor, false);
		}
		panel.setBorder(border1);
		panel.setCursor(cursor);
		panel.setToolTipText(_text);
		panel.addMouseListener(this);
		ImageIcon imgIcon = UIUtil.getImage(_imgName); //
		imgIcon = new ImageIcon(TBUtil.getTBUtil().getImageScale(imgIcon.getImage(), 40, 40));
		JLabel imgLabel = new MyAlphaLabel(imgIcon);
		panel.add(imgLabel, BorderLayout.CENTER);
		JLabel textLabel = new JLabel(_text, JLabel.CENTER);
		textLabel.setForeground(textColor);
		panel.add(textLabel, BorderLayout.SOUTH);
		panel.putClientProperty("id", _id);
		panel.putClientProperty("dragtype", dragtype);
		panel.putClientProperty("imgName", _imgName);
		panel.putClientProperty("text", _text);
		panel.putClientProperty("str_viewCols", str_viewCols);
		panel.putClientProperty("address", address);
		new MyDraggListener(panel);
		return panel; //
	}

	public static void openMe(java.awt.Container _parent, String _tile, String _type) {
		MWFPanel_Pub_Desktop_Person configPanel = new MWFPanel_Pub_Desktop_Person();
		configPanel.initialize();
		BillDialog frame = new BillDialog(_parent);
		frame.setTitle("首页板块个人定制");
		frame.setSize(600, 710);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().add(configPanel);
		frame.setVisible(true);
		frame.toFront();
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	private void moveDeskItem(JPanel _source, JPanel _target, int x, int y, String str_dragsource) {
		if (_source == person_Desk && _target == person_Desk) { // 如果源和目标都是自定义则应该是交换
			changeDeskItem(x, y, str_dragsource);
		} else if (_source == default_Desk && _target == person_Desk) { // 添加
			addDeskItem(x, y, str_dragsource);
		}
	}

	private void addLength() {
		person_Desk.setPreferredSize(new Dimension(person_Desk.getPreferredSize().width, person_Desk.getPreferredSize().height + vspit + height));
		person_Desk.updateUI();
	}

	private void save() {
		String[] keys = (String[]) selfdeskMap.keySet().toArray(new String[0]);
		List sql = new ArrayList();
		try {
			sql.add("delete from pub_desktop_user where userid = '" + ClientEnvironment.getCurrLoginUserVO().getId() + "'");
			if (keys != null && keys.length > 0) {
				for (int i = 0; i < keys.length; i++) {
					if (!"astring".equals((String) ((JPanel) selfdeskMap.get(keys[i])).getClientProperty("id"))) {
						InsertSQLBuilder isb = new InsertSQLBuilder();
						isb.setTableName("pub_desktop_user");
						isb.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_PUB_DESKTOP_USER"));
						isb.putFieldValue("desktopid", (String) ((JPanel) selfdeskMap.get(keys[i])).getClientProperty("id"));
						isb.putFieldValue("userid", ClientEnvironment.getCurrLoginUserVO().getId());
						isb.putFieldValue("viewcols", (String) ((JPanel) selfdeskMap.get(keys[i])).getClientProperty("str_viewCols"));
						String y = ((String) ((JPanel) selfdeskMap.get(keys[i])).getClientProperty("address")).split(",")[1];
						if (selfdeskMap.containsKey(xspit + "," + y) && selfdeskMap.containsKey((xspit + width + hspit) + "," + y) && !"astring".equals(((JPanel) selfdeskMap.get(xspit + "," + y)).getClientProperty("id")) && !"astring".equals(((JPanel) selfdeskMap.get((xspit + width + hspit) + "," + y)).getClientProperty("id"))) {
						} else {
							isb.putFieldValue("viewcols", "全列");
						}
						isb.putFieldValue("seq", keys[i].split(",")[1] + (keys[i].split(",")[0].equals(xspit + "") ? "0" : "1"));
						sql.add(isb.getSQL());
					}

				}
			}
			if (sql.size() == 1) {
				InsertSQLBuilder isb = new InsertSQLBuilder();
				isb.setTableName("pub_desktop_user");
				isb.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_PUB_DESKTOP_USER"));
				isb.putFieldValue("desktopid", "");
				isb.putFieldValue("userid", ClientEnvironment.getCurrLoginUserVO().getId());
				sql.add(isb.getSQL());
			}
			UIUtil.executeBatchByDS(null, sql);
			MessageBox.show(this, "保存成功!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void delLength() {
		int currlength = person_Desk.getPreferredSize().height - vspit - height;
		if (currlength >= yspit) {
			person_Desk.setPreferredSize(new Dimension(person_Desk.getPreferredSize().width, currlength));
			String[] keys = (String[]) selfdeskMap.keySet().toArray(new String[0]);
			if (keys != null && keys.length > 0) {
				for (int i = 0; i < keys.length; i++) {
					if (Integer.parseInt(keys[i].split(",")[1]) > currlength) {
						person_Desk.remove((JPanel) selfdeskMap.get(keys[i]));
						selfdeskMap.remove(keys[i]);
					}
				}
			}
			person_Desk.updateUI();
		}
	}

	private void removeDeskItem(String str_dragsource) {
		String[] str_dragsources = str_dragsource.split(";");
		Object target = selfdeskMap.get(str_dragsources[5]);
		if (target != null) {
			if ("全列".equals(str_dragsources[4])) {
				person_Desk.remove(((JPanel) selfdeskMap.get(str_dragsources[5])));
				selfdeskMap.remove(str_dragsources[5]);
				//				JPanel jp = getItemPanel("astring", "zt_045.gif", "可添加", "SELF", "半列", xspit + "," + Integer.parseInt(str_dragsources[5].split(",")[1]));
				//				jp.setBounds(xspit, Integer.parseInt(str_dragsources[5].split(",")[1]), width, height);
				//				selfdeskMap.put(xspit + "," + Integer.parseInt(str_dragsources[5].split(",")[1]), jp);
				//				person_Desk.add(jp);
				//				JPanel jp2 = getItemPanel("astring", "zt_045.gif", "可添加", "SELF", "半列", (xspit + width + hspit) + "," + Integer.parseInt(str_dragsources[5].split(",")[1]));
				//				jp2.setBounds((xspit + width + hspit), Integer.parseInt(str_dragsources[5].split(",")[1]), width, height);
				//				selfdeskMap.put((xspit + width + hspit) + "," + Integer.parseInt(str_dragsources[5].split(",")[1]), jp2);
				//				person_Desk.add(jp2);
			} else {
				//				JPanel jp = getItemPanel("astring", "zt_045.gif", "可添加", "SELF", str_dragsources[4], str_dragsources[5]);
				//				jp.setBounds(Integer.parseInt(str_dragsources[5].split(",")[0]), Integer.parseInt(str_dragsources[5].split(",")[1]), width, height);
				person_Desk.remove(((JPanel) selfdeskMap.get(str_dragsources[5])));
				selfdeskMap.remove(str_dragsources[5]);
				//	selfdeskMap.put(str_dragsources[5], jp);
				//	person_Desk.add(jp);
			}
			person_Desk.updateUI();
		}
	}

	private void chageItemViewCols(String str_dragsource) {
		String[] str_dragsources = str_dragsource.split(";");
		if ("全列".equals(str_dragsources[4])) { // 全列变半列
			JPanel jp = getItemPanel(str_dragsources[1], str_dragsources[2], str_dragsources[3], "SELF", "半列", str_dragsources[5]);
			jp.setBounds(xspit, Integer.parseInt(str_dragsources[5].split(",")[1]), width, height);
			//			JPanel jp2 = getItemPanel("astring", "zt_045.gif", "可添加", "SELF", "半列", (xspit + width + hspit) + "," + str_dragsources[5].split(",")[1]);
			//			jp2.setBounds((xspit + width + hspit), Integer.parseInt(str_dragsources[5].split(",")[1]), width, height);
			person_Desk.remove((JPanel) selfdeskMap.get(str_dragsources[5]));
			selfdeskMap.remove(str_dragsources[5]);
			//			selfdeskMap.put((xspit + width + hspit) + "," + str_dragsources[5].split(",")[1], jp2);
			selfdeskMap.put(str_dragsources[5], jp);
			person_Desk.add(jp);
			//			person_Desk.add(jp2);
		} else { // 半列变全列
			if (selfdeskMap.containsKey(xspit + "," + str_dragsources[5].split(",")[1])) {
				person_Desk.remove((JPanel) selfdeskMap.get(xspit + "," + str_dragsources[5].split(",")[1]));
				selfdeskMap.remove(xspit + "," + str_dragsources[5].split(",")[1]);
			}
			if (selfdeskMap.containsKey((xspit + width + hspit) + "," + str_dragsources[5].split(",")[1])) {
				person_Desk.remove((JPanel) selfdeskMap.get((xspit + width + hspit) + "," + str_dragsources[5].split(",")[1]));
				selfdeskMap.remove((xspit + width + hspit) + "," + str_dragsources[5].split(",")[1]);
			}
			JPanel jp = getItemPanel(str_dragsources[1], str_dragsources[2], str_dragsources[3], "SELF", "全列", xspit + "," + str_dragsources[5].split(",")[1]);
			jp.setBounds(xspit, Integer.parseInt(str_dragsources[5].split(",")[1]), width * 2 + hspit, height);
			selfdeskMap.put(xspit + "," + str_dragsources[5].split(",")[1], jp);
			person_Desk.add(jp);
		}
		person_Desk.updateUI();
	}

	private void changeDeskItem(int x, int y, String str_dragsource) {
		String[] str_dragsources = str_dragsource.split(";");
		int a = (y - 2 * yspit) / (height + vspit);
		int b = (y - 2 * yspit) % (height + vspit);
		if (b <= (height + vspit / 2)) {
			y = a * (height + vspit) + yspit;
		} else {
			y = (a + 1) * (height + vspit) + yspit;
		}
		Object target = null;
		if (x <= (height + vspit / 2)) {
			if ((xspit + "," + y).equals(str_dragsources[5])) {
				return;
			}
			x = xspit;
			target = selfdeskMap.get(x + "," + y);
		} else {
			if (selfdeskMap.get(xspit + "," + y) != null && "全列".equals(((JPanel) selfdeskMap.get(xspit + "," + y)).getClientProperty("str_viewCols"))) {
				if ((xspit + "," + y).equals(str_dragsources[5])) {
					return;
				}
				x = xspit;
				target = selfdeskMap.get(x + "," + y);
			} else {
				if (((xspit + width + hspit) + "," + y).equals(str_dragsources[5])) {
					return;
				}
				x = xspit + width + hspit;
				target = selfdeskMap.get(x + "," + y);
			}
		}

		if (target == null) {
			JPanel jp = getItemPanel(str_dragsources[1], str_dragsources[2], str_dragsources[3], "SELF", "半列", x + "," + y);
			jp.setBounds(x, y, width, height);
			selfdeskMap.put(x + "," + y, jp);
			person_Desk.add(jp);
			//			JPanel jp2 = getItemPanel("astring", "zt_045.gif", "可添加", "SELF", str_dragsources[4], str_dragsources[5]);
			//			jp2.setBounds(Integer.parseInt(str_dragsources[5].split(",")[0]), Integer.parseInt(str_dragsources[5].split(",")[1]), ((JPanel) selfdeskMap.get(str_dragsources[5])).getBounds().width, height);
			person_Desk.remove(((JPanel) selfdeskMap.get(str_dragsources[5])));
			selfdeskMap.remove(str_dragsources[5]);
			//			selfdeskMap.put(str_dragsources[5], jp2);
			//			person_Desk.add(jp2);
		} else {
			JPanel jp = getItemPanel(str_dragsources[1], str_dragsources[2], str_dragsources[3], "SELF", (String) ((JPanel) target).getClientProperty("str_viewCols"), x + "," + y);
			jp.setBounds(x, y, ((JPanel) target).getBounds().width, height);
			selfdeskMap.put(x + "," + y, jp);
			person_Desk.add(jp);
			person_Desk.remove(((JPanel) target));
			String targetid = (String) ((JPanel) target).getClientProperty("id");
			String targetimg = (String) ((JPanel) target).getClientProperty("imgName");
			String targettext = (String) ((JPanel) target).getClientProperty("text");
			String targettype = (String) ((JPanel) target).getClientProperty("dragtype");
			String targetview = (String) ((JPanel) target).getClientProperty("str_viewCols");
			JPanel jp2 = getItemPanel(targetid, targetimg, targettext, "SELF", str_dragsources[4], str_dragsources[5]);
			jp2.setBounds(Integer.parseInt(str_dragsources[5].split(",")[0]), Integer.parseInt(str_dragsources[5].split(",")[1]), ((JPanel) selfdeskMap.get(str_dragsources[5])).getBounds().width, height);
			person_Desk.remove(((JPanel) selfdeskMap.get(str_dragsources[5])));
			selfdeskMap.put(str_dragsources[5], jp2);
			person_Desk.add(jp2);
		}
		person_Desk.updateUI();
	}

	private void addDeskItem(int x, int y, String str_dragsource) {
		String[] str_dragsources = str_dragsource.split(";");
		int a = (y - 2 * yspit) / (height + vspit);
		int b = (y - 2 * yspit) % (height + vspit);
		if (b <= (height + vspit / 2)) {
			y = a * (height + vspit) + yspit;
		} else {
			y = (a + 1) * (height + vspit) + yspit;
		}
		int currwidth = width;
		if ("半列".equals(str_dragsources[4])) {
			if (x <= (height + vspit / 2)) {
				x = xspit;
			} else {
				x = xspit + width + hspit;
			}
			// 判断此行是否有全列的
			if (selfdeskMap.containsKey(xspit + "," + y) && "全列".equals(((JPanel) selfdeskMap.get(xspit + "," + y)).getClientProperty("str_viewCols"))) {
				person_Desk.remove((JPanel) selfdeskMap.get(xspit + "," + y));
				selfdeskMap.remove(xspit + "," + y);
				if (x == xspit) {
					//					JPanel jp = getItemPanel("astring", "zt_045.gif", "可添加", "SELF", "半列", (xspit + width + hspit) + "," + y);
					//					jp.setBounds((xspit + width + hspit), y, width, height);
					//					selfdeskMap.put((xspit + width + hspit) + "," + y, jp);
					//					person_Desk.add(jp);
				} else {
					//					JPanel jp = getItemPanel("astring", "zt_045.gif", "可添加", "SELF", "半列", xspit + "," + y);
					//					jp.setBounds(xspit, y, width, height);
					//					selfdeskMap.put(xspit + "," + y, jp);
					//					person_Desk.add(jp);
				}
			}
		} else {
			x = xspit;
			currwidth = width * 2 + hspit;
			if (selfdeskMap.containsKey((xspit + width + hspit) + "," + y)) {
				person_Desk.remove((JPanel) selfdeskMap.get((xspit + width + hspit) + "," + y));
				selfdeskMap.remove((xspit + width + hspit) + "," + y);
			}
		}
		if (selfdeskMap.containsKey(x + "," + y)) {
			person_Desk.remove((JPanel) selfdeskMap.get(x + "," + y));
		}
		JPanel jp = getItemPanel(str_dragsources[1], str_dragsources[2], str_dragsources[3], "SELF", str_dragsources[4], x + "," + y);
		jp.setBounds(x, y, currwidth, height);
		selfdeskMap.put(x + "," + y, jp);
		person_Desk.add(jp);
		person_Desk.setOpaque(false);
		person_Desk.updateUI();
	}

	class ItemPanel extends JPanel implements MouseListener, MouseMotionListener {
		private boolean iscanclose = false;
		private ImageIcon closeImg = new ImageIcon(TBUtil.getTBUtil().getImageScale(UIUtil.getImage("zt_031.gif").getImage(), 13, 13));
		private ImageIcon closeImg_clicked = new ImageIcon(TBUtil.getTBUtil().getImageScale(UIUtil.getImage("del.gif").getImage(), 13, 13));
		private boolean isenter, isenterclose = false;

		public ItemPanel(boolean _iscanclose) {
			this.iscanclose = _iscanclose;
			this.addMouseMotionListener(this);
			this.addMouseListener(this);
		}

		public void paint(Graphics g) {
			super.paint(g);
			if (iscanclose) {
				if (isenterclose) {
					g.drawImage(closeImg_clicked.getImage(), getWidth() - 13, 0, null);
				} else {
					g.drawImage(closeImg.getImage(), getWidth() - 13, 0, null);
				}
			}
		}

		public void mouseClicked(MouseEvent e) {
			String str_tabtype = (String) this.getClientProperty("dragtype");
			String id = (String) this.getClientProperty("id");
			String _imgName = (String) this.getClientProperty("imgName");
			String _text = (String) this.getClientProperty("text");
			String str_viewCols = (String) this.getClientProperty("str_viewCols");
			String address = (String) this.getClientProperty("address");
			String source = str_tabtype + ";" + id + ";" + _imgName + ";" + _text + ";" + str_viewCols + ";" + address;
			if (isenterclose) {
				removeDeskItem(source);
			} else {
				if (e.getButton() == e.BUTTON1 && e.getClickCount() == 2) { // 双击则要改变半列为全列或全列为半列
					chageItemViewCols(source);
				}
			}
		}

		public void mouseEntered(MouseEvent e) {

		}

		public void mouseExited(MouseEvent e) {
			isenter = false;
			isenterclose = false;
			this.repaint();
		}

		public void mousePressed(MouseEvent e) {

		}

		public void mouseReleased(MouseEvent e) {

		}

		public void mouseDragged(MouseEvent e) {

		}

		public void mouseMoved(MouseEvent e) {
			isenter = true;
			if (e.getX() >= this.getWidth() - 28 && e.getX() <= this.getWidth() && e.getY() >= 0 && e.getY() <= 28) {
				isenterclose = true;
			} else {
				isenterclose = false;
			}
			this.repaint();
		}
	}

	class MyAlphaLabel extends JLabel {
		private static final long serialVersionUID = -3607779567980034624L;

		public MyAlphaLabel(Icon image) {
			super(image);
		}

		public void paint(Graphics g) {
			String str_isEnter = (String) this.getClientProperty("isEnter"); //
			if ("Y".equals(str_isEnter)) {
				Graphics2D g2d = (Graphics2D) g;
				Composite alphaComp = AlphaComposite.SrcOver.derive(0.65f);
				g2d.setComposite(alphaComp);
				super.paint(g2d);
			} else {
				Graphics2D g2d = (Graphics2D) g;
				Composite alphaComp = AlphaComposite.SrcOver.derive(1f);
				g2d.setComposite(alphaComp);
				super.paint(g2d);
			}
		}
	}

	class MyDropListener implements DropTargetListener {
		public MyDropListener(JComponent _component) {
			new DropTarget(_component, this); //
		}

		public void drop(DropTargetDropEvent evt) {
			String str_dragsource = null;
			try {
				Object obj = evt.getTransferable().getTransferData(evt.getCurrentDataFlavors()[0]);
				str_dragsource = (String) obj;
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}

			if (str_dragsource != null) {
				JPanel source = null;
				if (str_dragsource.startsWith("ALL")) {
					source = default_Desk;
				} else if (str_dragsource.startsWith("SELF")) {
					source = person_Desk;
				}

				JPanel target = null;
				if (evt.getDropTargetContext().getComponent() == defaultpanel || evt.getDropTargetContext().getComponent() == default_Desk) {
					target = default_Desk;
				} else if (evt.getDropTargetContext().getComponent() == personPanel || evt.getDropTargetContext().getComponent() == person_Desk) {
					target = person_Desk;
				}
				if (source != null && target != null) {
					moveDeskItem(source, target, evt.getLocation().x, evt.getLocation().y, str_dragsource);
				}
			}
		}

		public void dragEnter(DropTargetDragEvent dtde) {
		}

		public void dragExit(DropTargetEvent dte) {
		}

		public void dragOver(DropTargetDragEvent dtde) {
		}

		public void dropActionChanged(DropTargetDragEvent dtde) {
		}
	}

	class MyDraggListener implements DragGestureListener, DragSourceListener {
		DragSource dragSource;
		JPanel dragpanel = null;

		public MyDraggListener(JPanel _dragpanel) {
			dragpanel = _dragpanel; //
			dragSource = new DragSource();
			dragSource.createDefaultDragGestureRecognizer(dragpanel, DnDConstants.ACTION_COPY_OR_MOVE, this);
		}

		public void dragGestureRecognized(DragGestureEvent evt) {
			String str_tabtype = (String) dragpanel.getClientProperty("dragtype");
			String id = (String) dragpanel.getClientProperty("id");
			String _imgName = (String) dragpanel.getClientProperty("imgName");
			String _text = (String) dragpanel.getClientProperty("text");
			String str_viewCols = (String) dragpanel.getClientProperty("str_viewCols");
			String address = (String) dragpanel.getClientProperty("address");
			Transferable t = new StringSelection(str_tabtype + ";" + id + ";" + _imgName + ";" + _text + ";" + str_viewCols + ";" + address);
			dragSource.startDrag(evt, DragSource.DefaultCopyDrop, t, this);
		}

		public void dragEnter(DragSourceDragEvent evt) {
		}

		public void dragOver(DragSourceDragEvent evt) {
		}

		public void dragExit(DragSourceEvent evt) {
		}

		public void dropActionChanged(DragSourceDragEvent evt) {
		}

		public void dragDropEnd(DragSourceDropEvent evt) {
		}
	}

}
