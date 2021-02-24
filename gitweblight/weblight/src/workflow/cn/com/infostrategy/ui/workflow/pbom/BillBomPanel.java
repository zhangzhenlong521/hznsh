package cn.com.infostrategy.ui.workflow.pbom;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Stack;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.workflow.design.RiskVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.I_MyJScrollPane;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.sysapp.login.DeskTopPanel;
import cn.com.infostrategy.ui.sysapp.login.WorkTabbedPanel;

/**
 * 一个自动带层的图形Bom导航面板
 * 
 * @author xch
 * 
 */
public class BillBomPanel extends JPanel implements BomItemClickListener, ActionListener {
	private static final long serialVersionUID = 1L;

	private JPanel buttonBarPanel = null; //上面的按钮栏面板!!
	private JPanel toftPanel = new JPanel(); // 一个层面板,可以放N个Bom图层!!!
	private CardLayout cardLayout = new CardLayout(); //
	private LinkedHashMap hm_Boms = new LinkedHashMap();//存储BillItemPanel

	private Vector v_billbomlicklistener = new Vector(); //
	private WLTButton btn_skipfirst, btn_back; //

	private Stack stack = new Stack(); // //
	private String str_firstitemname = null; //

	private Hashtable ht_riskVO = null; //
	private boolean editable = true;//兼容以前的，默认为可编辑

	private TBUtil tbutil = new TBUtil(); //
	private boolean isBomimgAlignLeft = false; //以前图片是居左的,但后来弄成默认居中!怕万一还是有客户喜欢居左,所以搞个参数!
	private String scrolltype = null;
	private boolean showNorthPanel = true; //是否显示Bom图上的导航按钮, 适用于Bom图中配置前后的链接(如首次登录系统的帮助图片) Gwang 2012-11-30

	/**
	 * @return the showNorthPanel
	 */
	public boolean isShowNorthPanel() {
		return showNorthPanel;
	}

	/**
	 * @param showNorthPanel the showNorthPanel to set
	 */
	public void setShowNorthPanel(boolean showNorthPanel) {
		this.showNorthPanel = showNorthPanel;
	}

	/**
	 * 默认构造方法不允许访问
	 */
	private void BillBomPanel() {
	}

	/**
	 * 
	 * @param _bomcode
	 */
	public BillBomPanel(String _bomcode) {
		toftPanel.setOpaque(false); //透明!!!
		toftPanel.setLayout(cardLayout); //
		stack.push(_bomcode); //压入堆栈!!
		BomItemPanel bomitemPanel = new BomItemPanel(this, _bomcode); // 先将第一个Bom图搞上
		bomitemPanel.addBomItemClickListener(this); //
		hm_Boms.put(bomitemPanel.getBomcode(), bomitemPanel); //绑定起来!!!
		isBomimgAlignLeft = tbutil.getSysOptionBooleanValue("BOM模板图片是否居左", false); //由系统参数决定,不设则默认是居中!
		scrolltype = tbutil.getSysOptionStringValue("BOM模板滚动条风格", "1");//默认全部可滚动，兼容以前系统风格
		//容器面板
		JPanel containerPanel = new JPanel(new FlowLayout((isBomimgAlignLeft ? FlowLayout.LEFT : FlowLayout.CENTER), 0, 0)); //为了居中,弄一个容器
		containerPanel.setOpaque(false); //透明!
		containerPanel.add(bomitemPanel);
		int li_item_width = (int) bomitemPanel.getPreferredSize().getWidth(); //
		int li_item_height = (int) bomitemPanel.getPreferredSize().getHeight(); //
		containerPanel.setPreferredSize(new Dimension(li_item_width, li_item_height)); //要设大小!! 原有的在li_item_height后加30 

		//		JScrollPane scroll = new JScrollPane(); //
		//		scroll.setOpaque(false); //透明!
		//		scroll.getViewport().setOpaque(false); //透明!
		//		scroll.getViewport().add(containerPanel); //
		//
		//		if ("1".equals(scrolltype)) {//bom图滚动条设置：1-全部可滚动；2-仅横向可滚动；3-仅纵向可滚动；4-全部不可滚动
		//			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		//			scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		//		} else if ("2".equals(scrolltype)) {
		//			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		//			scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		//		} else if ("3".equals(scrolltype)) {
		//			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		//			scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		//		} else if ("4".equals(scrolltype)) {
		//			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		//			scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		//		}

		//		scroll.setBorder(BorderFactory.createEmptyBorder()); //设置滚动条的边框为空

		I_MyJScrollPane scroll = new I_MyJScrollPane(); //
		scroll.setOpaque(false); //透明!
		scroll.getViewport().setOpaque(false); //透明!
		scroll.getViewport().add(containerPanel); //在滚动框中加入容器面板
		scroll.setBorder(BorderFactory.createEmptyBorder());

		toftPanel.add(bomitemPanel.getBomcode(), scroll); //加入滚动框!
		cardLayout.show(toftPanel, bomitemPanel.getBomcode()); //显示
		str_firstitemname = bomitemPanel.getBomcode(); //

		JPanel contentPanel = WLTPanel.createDefaultPanel(new BorderLayout()); //必须再用一个包一下!因为上面有导航按钮!
		contentPanel.add(toftPanel, BorderLayout.CENTER); //搞个渐变效果!!!
		contentPanel.add(getNorthPanel(), BorderLayout.NORTH); // //上面有个导航按钮
		if (!ClientEnvironment.isAdmin()) { // 如果不是管理员,则默认不可编辑
			setEditable(false);
		}
		this.setLayout(new BorderLayout()); //
		this.add(contentPanel, BorderLayout.CENTER); //
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean _editable) {
		this.editable = _editable;
		BomItemPanel[] itemPanels = getBomItemPanels();
		for (int i = 0; i < itemPanels.length; i++) {
			itemPanels[i].setEditable(editable);
		}
	}

	/**
	 * 设置风险点
	 * 
	 * @param _htriskVO,是个哈希表,value必须是RiskVO
	 */
	public void setRiskVO(Hashtable _htriskVO) {
		ht_riskVO = _htriskVO; //
		BomItemPanel[] itemPanels = getBomItemPanels(); //
		for (int i = 0; i < itemPanels.length; i++) {
			itemPanels[i].setRiskVO(ht_riskVO); //
		}
	}

	public void setRiskVO(Hashtable _htriskVO, String type) {
		ht_riskVO = _htriskVO; //
		BomItemPanel[] itemPanels = getBomItemPanels(); //
		for (int i = 0; i < itemPanels.length; i++) {
			itemPanels[i].setRiskVO(ht_riskVO, type); //
		}
	}

	/**
	 * 设置风险点!
	 */
	public void setRiskVO(String _itemKey, RiskVO _riskVO) {
		if (ht_riskVO == null) {
			ht_riskVO = new Hashtable();
		}
		ht_riskVO.put(_itemKey, _riskVO); //加入
		BomItemPanel[] itemPanels = getBomItemPanels(); //
		for (int i = 0; i < itemPanels.length; i++) {
			itemPanels[i].setRiskVO(ht_riskVO); //
		}
	}
	public void setCanClicked(boolean _canClick){
		BomItemPanel[] itemPanels = getBomItemPanels();
		for (int i = 0; i < itemPanels.length; i++) {
			itemPanels[i].setCanClicked(_canClick);
		}
	}

	/**
	 * 上方的向导按钮
	 * 
	 * @return
	 */
	public JPanel getNorthPanel() {
		if (buttonBarPanel != null) {
			return buttonBarPanel; //
		}
		buttonBarPanel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.LEFT, 0, 2)); //
		buttonBarPanel.setOpaque(false); //透明!!!

		btn_skipfirst = new WLTButton(UIUtil.getImage("office_038.gif")); //
		btn_skipfirst.setToolTipText("直接返回第一个页面"); //
		btn_skipfirst.addActionListener(this); //

		btn_back = new WLTButton(UIUtil.getImage("office_165.gif")); //
		btn_back.setToolTipText("返回上一页面"); //

		btn_back.setEnabled(false);
		btn_back.addActionListener(this); //

		btn_back.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					showStackMessage(); //
				}
			}
		});

		buttonBarPanel.add(btn_skipfirst); // 第一个
		buttonBarPanel.add(btn_back); // 后退
		buttonBarPanel.setVisible(false); //默认是隐藏的!
		return buttonBarPanel; //
	}

	/**
	 * 点击事件
	 */
	public void onBomItemClicked(final BomItemClickEvent _event) {
		String str_bomCode = _event.getBomcode(); //
		String str_itemKey = _event.getItemkey(); //
		String str_hashKey = str_bomCode + "#" + str_itemKey; //
		//直接返回
		if (str_hashKey.contains("back") || str_hashKey.contains("BACK") || str_hashKey.contains("后退") || str_hashKey.contains("返回")) {
			onBack();
			return;
		}
		if ("2".equals(_event.getLoadtype())) { //如果是弹出窗口方式，则不要等待框,否则会卡在后面!!
			onClickedBomItem(_event);
		} else {
			new SplashWindow(this, new AbstractAction() {
				private static final long serialVersionUID = -287905438900197436L;

				public void actionPerformed(ActionEvent e) {
					onClickedBomItem(_event);
				}
			}, 366, 366);
		}
	}

	/**
	 * 点击了每个BomItem中的热点
	 * 
	 * @param _event
	 */
	private void onClickedBomItem(BomItemClickEvent _event) {
		try {
			String str_bindType = _event.getBindtype(); //
			String str_bomCode = _event.getBomcode(); //
			String str_itemKey = _event.getItemkey(); //
			String str_hashKey = str_bomCode + "#" + str_itemKey; //
			if (str_bindType == null || str_bindType.equals("Bom图")) { // 如果是图
				if (_event.getBindbomcode() != null && !_event.getBindbomcode().trim().equals("")) {
					if ("2".equals(_event.getLoadtype())) { //如果是弹出方式,则使用Dialog打开,因为后来王宁在财政部项目中遇到想弹出来,而不下一层,也算是个变态需求吧!!!【xch/2012-03-09】
						BillBomPanel newBomPanel = new BillBomPanel(_event.getBindbomcode()); //必须是一个新的BillBom,而不是billBomItemPanel
						if (ht_riskVO != null) {
							newBomPanel.setRiskVO(ht_riskVO); //带过去!!
						}
						Point point = new Point(0, 0); //
						Point invokerOrigin = ((Component) _event.getSource()).getLocationOnScreen();
						int mouseX = (int) MouseInfo.getPointerInfo().getLocation().getX() + 5; //获取鼠标的屏幕坐标
						int mouseY = (int) MouseInfo.getPointerInfo().getLocation().getY() + 5;
						//						int mouseX = (int) invokerOrigin.getX()?;
						//						int mouseY = (int) invokerOrigin.getY();
						int width = (int) newBomPanel.getPreferredSize().getWidth(); //获得打开Bom面板的大小。
						int height = (int) newBomPanel.getPreferredSize().getHeight();
						final JWindow win = new JWindow(SwingUtilities.getWindowAncestor(this));
						win.setFocusable(true);
						win.addFocusListener(new FocusAdapter() {
							public void focusLost(FocusEvent e) {
								win.dispose();
							}
						});
						win.setBounds(mouseX, mouseY, width + 10, height + 30);
						newBomPanel.setFocusable(false);
						win.getContentPane().add(newBomPanel); //
						win.setVisible(true);
						//						BillDialog dialog = new BillDialog(this, _event.getBindbomcode(), width + 10, height + 30, mouseX, mouseY); //这个地方要根据鼠标位置自动设置窗口位置,根据图片大小自动设置窗口大小!
						//						dialog.getContentPane().add(newBomPanel); //
						//						dialog.setVisible(true); //
					} else {
						if ("1".equals(_event.getLoadtype())) {
							stack.push(str_hashKey); // 在堆栈中加入
						} else if ("3".equals(_event.getLoadtype())) { //直接替换
							stack.clear(); // 清空上一层的堆栈
							stack.push(str_hashKey); // 在堆栈中加入
							if (hm_Boms.containsKey(str_hashKey)) { //直接显示
								cardLayout.show(toftPanel, str_hashKey);
								return;
							} else {//其他情况(比如未选择类型),则直接加入【李春娟/2014-12-10】
								stack.push(str_hashKey); // 在堆栈中加入
							}

						}
						BomItemPanel itemPanel = new BomItemPanel(this, _event.getBindbomcode()); // 先将第一个Bom图搞上
						itemPanel.addBomItemClickListener(this); //
						if (ht_riskVO != null) {
							itemPanel.setRiskVO(ht_riskVO);
						}
						itemPanel.setEditable(this.editable);

						//容器面板
						JPanel containerPanel = new JPanel(new FlowLayout((isBomimgAlignLeft ? FlowLayout.LEFT : FlowLayout.CENTER), 0, 0)); //为了居中,弄一个容器
						containerPanel.setOpaque(false); //透明!
						containerPanel.add(itemPanel); //
						int li_item_width = (int) itemPanel.getPreferredSize().getWidth(); //
						int li_item_height = (int) itemPanel.getPreferredSize().getHeight(); //
						containerPanel.setPreferredSize(new Dimension(li_item_width, li_item_height)); //要设大小,否则没内容!!如果下一层仍然是bom图，不用增加容器高度。会出现滚动条。[郝明2012-12-04]
						//滚动框
						I_MyJScrollPane scroll = new I_MyJScrollPane(); //
						scroll.setOpaque(false); //透明!
						scroll.getViewport().setOpaque(false); //透明!
						scroll.getViewport().add(containerPanel); //在滚动框中加入容器面板
						scroll.setBorder(BorderFactory.createEmptyBorder());
						if ("1".equals(scrolltype)) {//bom图滚动条设置：1-全部可滚动；2-仅横向可滚动；3-仅纵向可滚动；4-全部不可滚动
							scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
							scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
						} else if ("2".equals(scrolltype)) {
							scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
							scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
						} else if ("3".equals(scrolltype)) {
							scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
							scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
						} else if ("4".equals(scrolltype)) {
							scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
							scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
						}

						hm_Boms.put(str_hashKey, itemPanel); //
						itemPanel.putClientProperty("parentname", _event.getItemname());
						toftPanel.add(str_hashKey, scroll); //
						cardLayout.show(toftPanel, str_hashKey); // 如果已经有了这个图
						btn_back.setEnabled(true);
						getNorthPanel().setVisible(showNorthPanel); //
					}
				} else {
					MessageBox.show(this, "对应的链接图为空!!"); //
				}
			} else if (_event.getBindtype().equals("Class类")) { // 如果是类
				JPanel panel = null;
				if (_event.getBindclass() != null && !_event.getBindclass().trim().equals("")) {
					stack.push(str_hashKey); // 在堆栈中加入
					String str_className = _event.getBindclass(); //
					if (str_className.indexOf("(") > 0) {
						String str_csname = str_className.substring(0, str_className.indexOf("(")); //
						String str_par = str_className.substring(str_className.indexOf("(") + 1, str_className.lastIndexOf(")")); //参数里有可能有小括号，所以这里取最后一个右括号【李春娟/2012-03-16】
						str_par = tbutil.replaceAll(str_par, "\"", ""); //
						String[] str_pars = tbutil.split(str_par, ","); //
						//第一种构造参数
						Class cp_1[] = new Class[str_pars.length];
						for (int i = 0; i < cp_1.length; i++) {
							cp_1[i] = java.lang.String.class;
						}
						//第2种构造参数
						Class cp_2[] = new Class[str_pars.length + 1];
						for (int i = 0; i < cp_2.length; i++) {
							cp_2[i] = java.lang.String.class;
						}
						cp_2[cp_2.length - 1] = BillBomPanel.class; //
						Class panelclass = Class.forName(str_csname); //

						try { //优先使用第二种构造方法,即直接在构造方法中传入BillBomPanel
							Constructor constructor = panelclass.getConstructor(cp_2); //
							Object[] obj_par2 = new Object[str_pars.length + 1];
							for (int i = 0; i < str_pars.length; i++) {
								obj_par2[i] = str_pars[i];
							}
							obj_par2[obj_par2.length - 1] = this; //
							Object oo = constructor.newInstance(obj_par2);
							if (!(oo instanceof JPanel)) {//郝明希望自己实现一段逻辑/sunfujun/20121203
								panelclass.getMethod("invokeMe", this.getClass(), BomItemClickEvent.class).invoke(oo, this, _event);
								return;
							}

							panel = (JPanel) oo; //
						} catch (NoSuchMethodException exx) { //如果没发现这个构造方法,则使用
							Constructor constructor = panelclass.getConstructor(cp_1); //
							Object oo = constructor.newInstance(str_pars);
							if (!(oo instanceof JPanel)) {//郝明希望自己实现一段逻辑/sunfujun/20121203
								panelclass.getMethod("invokeMe", this.getClass(), BomItemClickEvent.class).invoke(oo, this, _event);
								return;
							}
							panel = (JPanel) oo; //
							if (panel instanceof DefaultBillBomHrefPanel) {
								DefaultBillBomHrefPanel bomHref = (DefaultBillBomHrefPanel) panel; //
								bomHref.setBillBomPanel(this); //
							}
						}
					} else {
						Object clas = Class.forName(str_className).newInstance();//直接传类[郝明2012-12-04]
						if (clas instanceof JPanel) {
							panel = (JPanel) clas;
						} else {
							Class.forName(str_className).getMethod("invokeMe", this.getClass(), BomItemClickEvent.class).invoke(clas, this, _event);
							return;
						}
					}
					if ("2".equals(_event.getLoadtype())) { //如果是弹出方式,则使用Dialog打开,因为后来王宁在财政部项目中遇到想弹出来,而不下一层,也算是个变态需求吧!!!【xch/2012-03-09】
						int width = (int) panel.getPreferredSize().getWidth(); //获得打开面板的大小
						int height = (int) panel.getPreferredSize().getHeight();
						BillDialog dialog = new BillDialog(this, str_hashKey, width + 10, height + 30); //
						dialog.getContentPane().add(panel); //
						dialog.setVisible(true); //
					} else {
						//王雷曾测出在主bom图点击进入子bom图后再进入定义的菜单是没有问题的，但返回主bom图再进入子bom图就报ArrayStoreException异常了，
						//原因是后面用到getBomItemPanels()方法中hm_Boms.values().toArray(new BomItemPanel[0]),但hm_Boms中存储的并不都是BomItemPanel对象，下面这句加入了Jpanel，故注释掉这句【李春娟/2012-03-15】
						hm_Boms.put(str_hashKey, panel);
						toftPanel.add(str_hashKey, panel); // //
						cardLayout.show(toftPanel, str_hashKey); // ///
						btn_back.setEnabled(true);
						getNorthPanel().setVisible(showNorthPanel); //
					}
				} else {
					MessageBox.show(this, "对应的Class为空!!"); //
					return;
				}
			} else if (_event.getBindtype().equals("菜单")) { // 如果是菜单,菜单因为在另一个页签打开，所以不用加入堆栈
				String str_menuid = _event.getBindmenu(); //绑定的菜单id  
				int li_menutype = cn.com.infostrategy.ui.sysapp.login.DeskTopPanel.deskTopPanel.getOneMenuType(str_menuid); //
				if (li_menutype < 0) {
					MessageBox.show(this, "您没有权限访问对应的功能点!"); //
					return; //
				}
				if (li_menutype == 1) { //叶子结点,在新页签中打开，不需要加入堆栈!!
					if ("2".equals(_event.getLoadtype())) { //如果是弹出方式，则同双击菜单打开
						DeskTopPanel.getDeskTopPanel().openAppMainFrameWindowById(str_menuid); //【李春娟/2015-04-10】
					} else {
						stack.push(str_hashKey); // 在堆栈中加入
						DeskTopPanel.getDeskTopPanel().expandTopBtnPane(false);
						HashMap map = new HashMap();
						map.put("label_name", _event.getItemname());
						map.put("label_key", _event.getItemkey());
						map.put("bindclassname", _event.getBindclass());
						final WorkTabbedPanel panel = cn.com.infostrategy.ui.sysapp.login.DeskTopPanel.deskTopPanel.getOpenAppMainPanel(str_menuid, map);
						//					WLTButton btn = panel.getBtn_closeMe();
						//					if (btn != null) {
						//						btn.addCustActionListener(new ActionListener() {
						//							public void actionPerformed(ActionEvent e) {
						//								onBack();
						//								DeskTopPanel.getDeskTopPanel().getNewDeskTopTabPane().expandPane();
						//								System.gc();
						//							}
						//						});
						//					}
						JComponent closeBtn = panel.getBtn_closeMe();
						closeBtn.addMouseListener(new MouseAdapter() {
							public void mouseReleased(MouseEvent e) {
								JComponent btn = (JComponent) e.getSource();
								if (!(e.getX() > 0 && e.getX() < btn.getWidth() && e.getY() > 0 && e.getY() < btn.getHeight())) {
									return;
								}
								panel.getWorkPanel().beforeDispose();
								onBack();
								DeskTopPanel.getDeskTopPanel().expandTopBtnPane(true);
								System.gc();
							}
						});
						panel.putClientProperty("parentname", _event.getItemname()); //这个不能加入到hm_Boms缓存中
						toftPanel.add(str_hashKey, panel); //
						cardLayout.show(toftPanel, str_hashKey); //
						hm_Boms.put(str_hashKey, panel);
						StringBuffer title_sb = new StringBuffer("当前位置:");
						for (int i = 0; i < stack.size(); i++) {
							JComponent bompanel = (JComponent) hm_Boms.get(stack.get(i));
							if (bompanel != null) {
								String titlename = (String) bompanel.getClientProperty("parentname");
								if (titlename == null || titlename.trim().equals("")) {//判断一下，如果为空，则取编码【李春娟/2013-02-26】
									if (bompanel instanceof BomItemPanel) {
										titlename = ((BomItemPanel) bompanel).getBomcode();
									}
								}
								title_sb.append(titlename + " > ");
							}
						}
						if (stack.size() > 0) {
							title_sb = new StringBuffer(title_sb.substring(0, title_sb.length() - 3));
							panel.setTitle(title_sb.toString());
						}
					}
				} else { //目录结点!!
					stack.push(str_hashKey); // 在堆栈中加入
					//					HashVO[] hvs = cn.com.infostrategy.ui.sysapp.login.DeskTopPanel.deskTopPanel.findOneNodeAllChildNOdeVOs(str_menuid); //找出该模块下的所有功能结点!!!
					//					cn.com.infostrategy.ui.sysapp.login.IndexShortCutPanel shortCutPanel = new cn.com.infostrategy.ui.sysapp.login.IndexShortCutPanel(hvs, false); //
					JPanel jp = cn.com.infostrategy.ui.sysapp.login.DeskTopPanel.deskTopPanel.getIBarMenuAndWorkPanel(str_menuid, this);
					//王雷曾测出在主bom图点击进入子bom图后再进入定义的菜单是没有问题的，但返回主bom图再进入子bom图就报ArrayStoreException异常了，
					//原因是后面用到getBomItemPanels()方法中hm_Boms.values().toArray(new BomItemPanel[0]),但hm_Boms中存储的并不都是BomItemPanel对象，下面这句加入了IndexShortCutPanel，故注释掉这句【李春娟/2012-03-15】
					hm_Boms.put(str_hashKey, jp); // //
					toftPanel.add(str_hashKey, jp); //
					cardLayout.show(toftPanel, str_hashKey); //
					btn_back.setEnabled(true); //
					getNorthPanel().setVisible(showNorthPanel); //
					DeskTopPanel.getDeskTopPanel().expandTopBtnPane(false);
				}
			}

			/**
			 * 再调用所有监听者的方法..
			 */
			for (int i = 0; i < v_billbomlicklistener.size(); i++) {
				BillBomClickedListener listener = (BillBomClickedListener) v_billbomlicklistener.get(i); //
				BillBomClickEvent event = new BillBomClickEvent(_event.getBomcode(), _event.getItemkey(), _event.getItemname(), _event.getBindbomcode(), this); //
				listener.onBillBomClicked(event); //
			}
		} catch (Exception _ex) {
			if (_ex instanceof java.lang.reflect.InvocationTargetException) {
				InvocationTargetException targetEx = (InvocationTargetException) _ex;
				MessageBox.showException(this, targetEx.getTargetException()); //
			} else {
				MessageBox.showException(this, _ex); //
			}
		}
	}

	public void addBillBomClickListener(BillBomClickedListener _listener) {
		v_billbomlicklistener.add(_listener); //
	}

	
	/**
	 * 将指定面板加到层布局里，可以后退
	 * @param key
	 * @param jp
	 */
	public void addToCardLayout(String key, JPanel jp) {
		if (hm_Boms.containsKey(key)) { //保证只打开一次
			cardLayout.show(toftPanel, key);
		} else {
			hm_Boms.put(key, jp);
			stack.push(key);
			toftPanel.add(key, jp);
			cardLayout.show(toftPanel, key);
			btn_back.setEnabled(true);
			getNorthPanel().setVisible(false); // 2013-3-12被郝明修改
		}
	}

	/**
	 * 取得某一个Bom面板
	 * 
	 * @param _code
	 */
	public BomItemPanel getBomItemPanel(String _code) {
		return (BomItemPanel) hm_Boms.get(_code);
	}

	public BomItemPanel[] getBomItemPanels() {
		JComponent[] component = (JComponent[]) hm_Boms.values().toArray(new JComponent[0]);
		ArrayList l = new ArrayList();
		for (int i = 0; i < component.length; i++) {
			if (component[i] instanceof BomItemPanel) {
				l.add((BomItemPanel) component[i]);
			}
		}
		return (BomItemPanel[]) l.toArray(new BomItemPanel[0]); //
	}

	public JComponent[] getCardPanelAllComponent() {
		JComponent[] component = (JComponent[]) hm_Boms.values().toArray(new JComponent[0]);
		return component;
	}

	/*
	 * 得到当前的面板
	 */

	/**
	 * 取得堆栈中的各层一路点过来的itemkey!!其实就是从堆栈中取得值【xch/2012-03-07】
	 * @return
	 */
	public String[] getClickPath() {
		ArrayList al_return = new ArrayList(); //
		for (int i = 1; i < stack.size(); i++) { //遍历!!!第一个去掉,因为第一个不是热点名!!而是第一张图的bomcode!即只有进入第二层后才会产生点击路径的概念【xch/2012-03-07】
			al_return.add(stack.get(i)); //
		}
		return (String[]) al_return.toArray(new String[0]); //
	}

	private void showStackMessage() {
		String str_msg = ""; //
		for (int i = 0; i < stack.size(); i++) {
			str_msg = str_msg + stack.get(i) + "\n"; //
		}
		MessageBox.showTextArea(this, str_msg); //
	}

	public void setCurrItemPanelEdit() {
		if (stack.isEmpty()) {
			return; //
		}
		String str_currrCode = (String) stack.peek(); //
		BomItemPanel bomItemPanel = getBomItemPanel(str_currrCode); //
		bomItemPanel.onSetEditState(); //编辑
	}

	/**
	 * 后退
	 */
	public void onBack() {
		if (!stack.isEmpty()) {
			String str_itemlevel = null;
			if (stack.size() == 1) { //如果只剩一个则只取不删
				str_itemlevel = (String) stack.peek(); //
			} else {
				hm_Boms.remove((String) stack.peek());
				stack.pop(); // 先取一下
				str_itemlevel = (String) stack.peek(); //
			}
			cardLayout.show(toftPanel, str_itemlevel); // 如果已经有了这个图
		}

		if (stack.size() == 1) { // 如果只剩一个则只取不删
			btn_back.setEnabled(false); //
			getNorthPanel().setVisible(false); //如果只剩一个,则第一层不显示导航条面板!【xch/2012-04-10】
		} else {
			getNorthPanel().setVisible(showNorthPanel); //
		}

	}

	/**
	 * 重置...
	 */
	public void reSet() {
		onSkipToFirst(); // 重置就等于跳至首页
	}

	/**
	 * 跳至第一步
	 */
	private void onSkipToFirst() {
		cardLayout.show(toftPanel, str_firstitemname); // 显示第一个
		stack.clear(); // 清空
		stack.push(str_firstitemname); //
		btn_back.setEnabled(false); //
	}

	/**
	 * 
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_skipfirst) {
			onSkipToFirst(); //
		} else if (e.getSource() == btn_back) {
			onBack(); //
		}
	}

}
