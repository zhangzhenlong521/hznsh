/**************************************************************************
 * $RCSfile: AbstractStyleWorkPanel_10.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.t10;

/**
 * 主子孙表! 主表是列表/卡片! 下面是两个页签!! 第一个页签是子表的维护!! 第二个页签是孙表的维护!!!
 * 选择主表刷新子表,选择子表刷新孙表!! 以前在用友时在做上海烟草项目时经常使用这种风险的!!!
 */
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.AbstractCustomerButtonBarPanel;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.styletemplet.AbstractStyleWorkPanel;

public abstract class AbstractStyleWorkPanel_10 extends AbstractStyleWorkPanel {
	protected String srt_parenttemplete_code = null; // 主表模板编码

	protected String srt_childtemplete_code = null; // 子模板编码

	protected String str_parent_pkname = null; // 主表主键字段名

	protected String str_child_pkname = null; // 子表主键字段名

	protected String str_child_forpkname = null; // 子表外键字段名

	protected String str_grandchildtemplet_code = null;

	protected String str_grandchild_pkname = null;

	protected String str_grandchild_forpkname = null;

	protected String[] menu = null;

	CardLayout cardlayout = null;

	private JPanel topanel = null;

	private BillListPanel parent_BillListPanel = null;

	private BillCardPanel parent_BillCardPanel = null; //

	private BillListPanel child_BillListPanel = null;

	private BillListPanel grandchild_BillListPanel = null;

	private JButton btn_save = new JButton("保存");

	private JButton btn_return = new JButton("查看卡片");

	protected JButton btn_save_return = new JButton("保存并返回"); //

	protected JButton btn_cancel_return = new JButton("放弃并返回"); //

	JButton btn_insert = new JButton("新增"); //

	JButton btn_delete = new JButton("删除"); //

	JButton btn_update = new JButton("编辑"); //

	JButton btn_query = new JButton("查询"); //

	JButton btn_quicksearch = new JButton("显示数据"); //

	private boolean oncreate = false;

	private ArrayList deleteData = null;

	private static final int INSERT = 0;

	private static final int UPDATE = 1;

	private int status = -1;

	private String childlastselectkey = "-1";

	private JPanel childcustomerpanel = null;

	private JPanel grandchildcustomerpanel = null;

	HashMap granddata = new HashMap();

	HashMap insertrowmap = new HashMap();

	HashMap updaterowmap = new HashMap();

	protected String uiinterceptor = "";

	protected JPanel navigationpanel = new JPanel();

	protected String bsinterceptor = "";

	protected String customerpanel = null;

	private boolean showsystembutton = true;

	public abstract String getParentTableTempletcode(); //

	public abstract String getParentTablePK(); //

	public abstract String getChildTableTempletcode();

	public abstract String getChildTablePK();

	public abstract String getChildTableFK();

	public abstract String getGrandChildTableTempletcode();

	public abstract String getGrandChildTablePK();

	public abstract String getGrandChildTableFK();

	public boolean isShowsystembutton() {
		return showsystembutton;
	}

	public void initialize() {
		srt_parenttemplete_code = getParentTableTempletcode();
		srt_childtemplete_code = getChildTableTempletcode();
		str_parent_pkname = getParentTablePK();
		str_child_pkname = getChildTablePK();
		str_child_forpkname = getChildTableFK();
		str_grandchildtemplet_code = getGrandChildTableTempletcode();
		str_grandchild_pkname = getGrandChildTablePK();
		str_grandchild_forpkname = getGrandChildTableFK();
		customerpanel = getCustomerpanel();
		uiinterceptor = getUiinterceptor(); //
		bsinterceptor = getBsinterceptor();
		showsystembutton = isShowsystembutton();
		menu = getSys_Selection_Path();

		this.setLayout(new BorderLayout()); //

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getParentPanelWithBtn(), getChildPanel());
		splitPane.setDividerSize(10);
		splitPane.setDividerLocation(200);
		splitPane.setOneTouchExpandable(true);
		setAllChildCustomerJPanelVisible(false);
		this.add(splitPane, BorderLayout.CENTER); //
		this.add(getStatusPanel(), BorderLayout.SOUTH);
	}

	public String[] getSys_Selection_Path() {
		return menu;
	}

	public String getCustomerpanel() {
		return customerpanel;
	}

	protected String getTempletTitle() {
		if (menu == null)
			return srt_parenttemplete_code + "-" + srt_childtemplete_code + "-" + str_grandchildtemplet_code;
		return "[" + menu[menu.length - 1] + "]";
	}

	protected Dimension getTempletSize() {
		return new Dimension(1000, 750);
	}

	protected String getChildTabTitle() {
		return this.srt_childtemplete_code;
	}

	protected String getGrandChildTabTitle() {
		return this.str_grandchildtemplet_code;
	}

	protected JPanel getParentPanelWithBtn() {
		JPanel rpanel = new JPanel();
		rpanel.setLayout(new BorderLayout());
		rpanel.add(getBtnPanel(), BorderLayout.NORTH);
		rpanel.add(getParentPanel(), BorderLayout.CENTER);
		return rpanel;
	}

	protected JPanel getStatusPanel() {
		JPanel rpanel = new JPanel();
		rpanel.setBackground(new java.awt.Color(240, 240, 240));
		rpanel.setLayout(new GridLayout(1, 2));
		Label user = new Label();
		user.setAlignment(Label.LEFT);
		user.setText(WLTConstants.STRING_CURRENT_USER + (String) ClientEnvironment.getInstance().get("SYS_LOGINUSER_NAME"));
		Label time = new Label();
		time.setAlignment(Label.RIGHT);
		time.setText(WLTConstants.STRING_LOGIN_TIME + (String) ClientEnvironment.getInstance().get("SYS_LOGIN_TIME"));
		rpanel.add(user);
		rpanel.add(time);
		return rpanel;
	}

	protected JPanel getBtnPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		if (showsystembutton)
			panel.add(getSysBtnPanel());
		if (customerpanel != null)
			panel.add(getCustomerBtnPanel());
		return panel;
	}

	protected JPanel getCustomerBtnPanel() {
		AbstractCustomerButtonBarPanel panel = null;
		try {
			panel = (AbstractCustomerButtonBarPanel) Class.forName(customerpanel).newInstance();
			panel.setParentWorkPanel(this); //
			panel.initialize(); //
			return panel;
		} catch (Exception e) {
			MessageBox.show(this, "初始化[" + customerpanel + "]失败，请检查", WLTConstants.MESSAGE_ERROR);
			e.printStackTrace();
		}
		return new JPanel();
	}

	/**
	 * 获取定义的UI拦截器类名
	 * 
	 * @return String
	 */
	public String getUiinterceptor() {
		return uiinterceptor;
	}

	public void setUiinterceptor(String uiinterceptor) {
		this.uiinterceptor = uiinterceptor;
	}

	public String getBsinterceptor() {
		return bsinterceptor;
	}

	public void setNavigationVisible(boolean isshow) {
		this.navigationpanel.setVisible(isshow);
	}

	public void setBsinterceptor(String bsinterceptor) {
		this.bsinterceptor = bsinterceptor;
	}

	public Object[] getNagivationPath() {
		return menu;
	}

	protected JPanel getParentPanel() {
		JPanel rpanel = new JPanel();
		rpanel.setLayout(new BorderLayout());
		cardlayout = new CardLayout();
		topanel = new JPanel(cardlayout);

		topanel.add("list", getParent_BillListPanel());
		topanel.add("card", getParent_BillCardPanel());
		rpanel.add(topanel, BorderLayout.CENTER); //
		return rpanel;
	}

	public BillListPanel getParent_BillListPanel() {
		if (parent_BillListPanel != null) {
			return parent_BillListPanel;
		}
		parent_BillListPanel = new BillListPanel(srt_parenttemplete_code); //
		parent_BillListPanel.setAllItemValue("listiseditable", WLTConstants.BILLCOMPENTEDITABLE_NONE);
		parent_BillListPanel.getTable().addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getButton() == MouseEvent.BUTTON1) {
					refreshChildTable();
				}
			}
		});

		return parent_BillListPanel;
	}

	public BillCardPanel getParent_BillCardPanel() {
		if (parent_BillCardPanel != null) {
			return parent_BillCardPanel;
		}
		parent_BillCardPanel = new BillCardPanel(srt_parenttemplete_code);
		return parent_BillCardPanel;
	}

	private void refreshChildTable() {
		child_BillListPanel.QueryDataByCondition(str_child_forpkname + "='" + parent_BillListPanel.getValueAt(parent_BillListPanel.getSelectedRow(), str_parent_pkname) + "'");
		grandchild_BillListPanel.clearTable();
	}

	protected JPanel getChildPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout()); //
		JTabbedPane tabp = new JTabbedPane();
		tabp.addTab(getChildTabTitle(), getChild_BillListPanel());
		tabp.addTab(getGrandChildTabTitle(), getGrandChildPanel());
		panel.add(tabp, BorderLayout.CENTER);
		return panel;
	}

	protected JPanel getGrandChildPanel() {
		JPanel rpanel = new JPanel();
		rpanel.setLayout(new BorderLayout());
		rpanel.add(getGrandChild_BillListPanel(), BorderLayout.CENTER);
		return rpanel;
	}

	public BillListPanel getChild_BillListPanel() {
		if (child_BillListPanel != null) {
			return child_BillListPanel;
		}
		child_BillListPanel = new BillListPanel(srt_childtemplete_code);
		child_BillListPanel.getTable().addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getButton() == MouseEvent.BUTTON1) {
					grandchild_BillListPanel.stopEditing();
					String currentselectkey = child_BillListPanel.getRealValueAtModel(child_BillListPanel.getSelectedRow(), str_child_pkname);
					if (!childlastselectkey.equals("-1")) {
						// 对原选中子表纪录的备份操作
						if (checkIsGrandChildChanged()) {// 如果孙表被修改过.保存孙表，然后再刷新.
							// System.out.println("孙表"+childlastselectkey+"变化...");
							saveGrandTableData(childlastselectkey, grandchild_BillListPanel.getValueAtAll());
							// System.out.println("保存孙表"+childlastselectkey+"完成...");
						}
						// 对新选中子表记录的操作
						// 如果先前保存过孙表的数据则恢复
						if (checkExistGrandTableData(currentselectkey)) {
							recoverGrandChildTable(currentselectkey);// 恢复
						}
						// 如果先前未保存过，刚直接刷新
						else
							refreshGrandChildTable();
					} else
						refreshGrandChildTable();
					childlastselectkey = currentselectkey;

				}

			}

		});
		child_BillListPanel.setCustomerNavigationJPanel(getChildCustomerJPanel());

		return child_BillListPanel;
	}

	public BillListPanel getGrandChild_BillListPanel() {
		if (grandchild_BillListPanel != null) {
			return grandchild_BillListPanel;
		}
		grandchild_BillListPanel = new BillListPanel(str_grandchildtemplet_code);
		grandchild_BillListPanel.setCustomerNavigationJPanel(getGrandChildCustomerJPanel());
		return grandchild_BillListPanel;
	}

	public void onSaveAndReturn() {
		onSave();
		setAllChildCustomerJPanelVisible(false);
		parent_BillCardPanel.initCurrRow();
		onReturn();
	}

	public void onCancelAndReturn() {
		parent_BillCardPanel.reset();
		setAllChildCustomerJPanelVisible(false);
		parent_BillCardPanel.initCurrRow();
		this.setStatus(-1);
		onReturn();
	}

	private void setAllChildCustomerJPanelVisible(boolean show) {
		grandchildcustomerpanel.setVisible(show);
		childcustomerpanel.setVisible(show);
	}

	private JPanel getChildCustomerJPanel() {
		childcustomerpanel = new JPanel();
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
		layout.setHgap(10);
		childcustomerpanel.setLayout(layout);

		JButton btn_insert = new JButton(UIUtil.getImage("insert.gif")); //
		JButton btn_delete = new JButton(UIUtil.getImage("delete.gif")); //
		JButton btn_refresh = new JButton(UIUtil.getImage("refresh.gif")); //

		btn_insert.setToolTipText("新增记录");
		btn_delete.setToolTipText("删除记录");
		btn_refresh.setToolTipText("刷新");

		btn_insert.setPreferredSize(new Dimension(18, 18));
		btn_delete.setPreferredSize(new Dimension(18, 18));
		btn_refresh.setPreferredSize(new Dimension(18, 18));

		btn_insert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onChildInsert();
			}
		});

		btn_delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onChildDelete();
			}
		});

		btn_refresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onChildRefresh();
			}
		});

		childcustomerpanel.add(btn_insert);
		childcustomerpanel.add(btn_delete);
		childcustomerpanel.add(btn_refresh);

		return childcustomerpanel;
	}

	private JPanel getGrandChildCustomerJPanel() {
		grandchildcustomerpanel = new JPanel();
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
		layout.setHgap(10);
		grandchildcustomerpanel.setLayout(layout);

		JButton btn_insert = new JButton(UIUtil.getImage("insert.gif")); //
		JButton btn_delete = new JButton(UIUtil.getImage("delete.gif")); //
		JButton btn_refresh = new JButton(UIUtil.getImage("refresh.gif")); //

		btn_insert.setToolTipText("新增记录");
		btn_delete.setToolTipText("删除记录");
		btn_refresh.setToolTipText("刷新");

		btn_insert.setPreferredSize(new Dimension(18, 18));
		btn_delete.setPreferredSize(new Dimension(18, 18));
		btn_refresh.setPreferredSize(new Dimension(18, 18));

		btn_insert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onGrandChildInsert();
			}
		});

		btn_delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onGrandChildDel();
			}
		});

		btn_refresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onGrandChildRefresh();
			}
		});

		grandchildcustomerpanel.add(btn_insert);
		grandchildcustomerpanel.add(btn_delete);
		grandchildcustomerpanel.add(btn_refresh);

		return grandchildcustomerpanel;
	}

	public void onGrandChildRefresh() {
		if (child_BillListPanel.getTable().getSelectedRowCount() == 0) {
			grandchild_BillListPanel.refreshCurrData();
		} else
			refreshGrandChildTable();
	}

	private void refreshGrandChildTable() {
		grandchild_BillListPanel.QueryDataByCondition(str_grandchild_forpkname + "='" + child_BillListPanel.getValueAt(child_BillListPanel.getSelectedRow(), str_child_pkname) + "'");
	}

	public void onChildRefresh() {
		if (parent_BillListPanel.getTable().getSelectedRowCount() == 0) {
			child_BillListPanel.refreshCurrData();
			grandchild_BillListPanel.clearTable();
		} else
			refreshChildTable();
	}

	public void onReturn() {
		oncreate = !oncreate;
		btn_return.setVisible(oncreate);
		btn_insert.setVisible(!oncreate);
		btn_update.setVisible(!oncreate);
		btn_delete.setVisible(!oncreate);
		btn_quicksearch.setVisible(!oncreate);
		btn_query.setVisible(!oncreate);

		if (oncreate) {
			btn_return.setText("返回列表");
		} else {
			if (getStatus() == AbstractStyleWorkPanel_10.INSERT || getStatus() == AbstractStyleWorkPanel_10.UPDATE) {
				btn_return.setVisible(true);
				btn_return.setText("返回卡片");
			}
		}
		btn_save.setVisible(oncreate);
		btn_save_return.setVisible(oncreate);
		btn_cancel_return.setVisible(oncreate);
		cardlayout.next(topanel);
	}

	private void onParentInsert() {
		try {
			setAllChildCustomerJPanelVisible(true);
			child_BillListPanel.clearTable();
			grandchild_BillListPanel.clearTable();
			onReturn();
			setStatus(AbstractStyleWorkPanel_10.INSERT);
			parent_BillCardPanel.insertRow();
		} catch (Exception e) {
			MessageBox.showException(this, e); //
		}
	}

	private void onChildInsert() {

		if (!oncreate && parent_BillListPanel.getTable().getSelectedRowCount() != 1) {
			MessageBox.show("请选择一条父记录", WLTConstants.MESSAGE_WARN);
			return;
		}

		int li_row = child_BillListPanel.newRow();
		if (!btn_save.isVisible())
			btn_save.setVisible(true);
		if (getStatus() != -1) {
			child_BillListPanel.setValueAt(parent_BillCardPanel.getValueAt(str_parent_pkname), li_row, str_child_forpkname);
		} else
			child_BillListPanel.setValueAt(parent_BillListPanel.getRealValueAtModel(parent_BillListPanel.getSelectedRow(), str_parent_pkname), li_row, str_child_forpkname);

	}

	private void onGrandChildInsert() {

		if (!oncreate && child_BillListPanel.getTable().getSelectedRowCount() != 1) {
			MessageBox.show("请选择一条父记录", WLTConstants.MESSAGE_WARN);
			return;
		}

		int li_row = grandchild_BillListPanel.newRow();
		if (!btn_save.isVisible())
			btn_save.setVisible(true);
		grandchild_BillListPanel.setValueAt(child_BillListPanel.getRealValueAtModel(child_BillListPanel.getSelectedRow(), str_child_pkname), li_row, str_grandchild_forpkname);

	}

	private void onParentRefresh() {
		parent_BillListPanel.QueryData(parent_BillListPanel.getSQL("1=1"));
		child_BillListPanel.clearTable();
	}

	public void onSave() {
		child_BillListPanel.stopEditing();
		try {
			HashMap map = parent_BillCardPanel.getAllObjectValuesWithHashMap();
			ArrayList allsqls = new ArrayList();
			if (getStatus() == AbstractStyleWorkPanel_10.INSERT) {// 新增时子表只会有新增的记录.不会有修改的记录.

				String parentsql = parent_BillCardPanel.getInsertSQL();
				allsqls.add(parentsql);
				parent_BillListPanel.insertRowWithInitStatus(parent_BillListPanel.getSelectedRow(), map);
			} else if (getStatus() == AbstractStyleWorkPanel_10.UPDATE) {
				String parentsql = parent_BillCardPanel.getUpdateSQL();
				allsqls.add(parentsql);
			}
			String[] childsqls = child_BillListPanel.getOperatorSQLs();
			String[] grandchildsqls = grandchild_BillListPanel.getOperatorSQLs();
			for (int i = 0; i < childsqls.length; i++)
				allsqls.add(childsqls[i]);
			for (int i = 0; i < grandchildsqls.length; i++)
				allsqls.add(grandchildsqls[i]);
			if (allsqls.size() <= 0)
				return;
			UIUtil.executeBatchByDS(null, allsqls);
			childlastselectkey = "-1";
			granddata.clear();
			insertrowmap.clear();
			updaterowmap.clear();
			grandchild_BillListPanel.setAllRowStatusAs("INIT");
			child_BillListPanel.setAllRowStatusAs("INIT");
			parent_BillListPanel.setAllRowStatusAs("INIT");
			MessageBox.show(WLTConstants.STRING_OPERATION_SUCCESS);
			setAllChildCustomerJPanelVisible(false);
		} catch (Exception e) {
			MessageBox.show(WLTConstants.STRING_OPERATION_FAILED + ":" + e.getMessage(), WLTConstants.MESSAGE_ERROR);
		}
		setStatus(-1);
	}

	private void onChildDelete() {
		if (MessageBox.confirm(this, "确定删除选中记录，这将删除子表中的关联记录?")) {
			ArrayList sql_delete = new ArrayList();
			deleteData = new ArrayList();

			int[] li_selectRow = child_BillListPanel.getTable().getSelectedRows();
			for (int i = 0; i < li_selectRow.length; i++) {
				String id = child_BillListPanel.getRealValueAtModel(li_selectRow[i], this.str_child_pkname);
				String delsql = "delete from " + grandchild_BillListPanel.getTempletVO().getTablename() + " where " + this.str_grandchild_forpkname + "='" + id + "'";
				sql_delete.add(delsql);
				deleteData.add(child_BillListPanel.getRealValueAtModel(li_selectRow[i], str_child_pkname));
			}
			child_BillListPanel.removeRows(li_selectRow);
			String[] str_dels = child_BillListPanel.getDeleteSQLs();
			if (str_dels != null && str_dels.length > 0) {
				for (int i = 0; i < str_dels.length; i++) {
					sql_delete.add(str_dels[i]);
				}
			}
			try {
				if (sql_delete.size() > 0) {
					UIUtil.executeBatchByDS(null, sql_delete);
					sql_delete.clear();
					grandchild_BillListPanel.clearTable();
				}
			} catch (Exception e) {
				MessageBox.show(WLTConstants.STRING_OPERATION_FAILED + ":" + e.getMessage(), WLTConstants.MESSAGE_ERROR);
			}
		}
	}

	private void onGrandChildDel() {
		grandchild_BillListPanel.removeRow();
		String[] delsql = grandchild_BillListPanel.getDeleteSQLs();
		try {
			UIUtil.executeBatchByDS(null, delsql);
			MessageBox.show(WLTConstants.STRING_OPERATION_SUCCESS);
		} catch (Exception e) {
			MessageBox.show(WLTConstants.STRING_OPERATION_FAILED + ":" + e.getMessage(), WLTConstants.MESSAGE_ERROR);
		}
	}

	private void onParentDel() {
		// 删除时级联.....
		if (MessageBox.confirm(this, "确定删除选中记录，这将删除子表中的关联记录?")) {
			ArrayList sql_delete = new ArrayList();
			deleteData = new ArrayList();

			// 删除孙表

			for (int p = 0; p < child_BillListPanel.getRowCount(); p++) {
				String id = child_BillListPanel.getRealValueAtModel(p, this.str_child_pkname);
				String delsql = "delete from " + grandchild_BillListPanel.getTempletVO().getTablename() + " where " + this.str_grandchild_forpkname + "='" + id + "'";
				sql_delete.add(delsql);
			}
			grandchild_BillListPanel.clearTable();
			// 删除子表
			String id = parent_BillListPanel.getRealValueAtModel(parent_BillListPanel.getSelectedRow(), this.str_parent_pkname);
			String delsql = "delete from " + child_BillListPanel.getTempletVO().getTablename() + " where " + this.str_child_forpkname + "='" + id + "'";
			sql_delete.add(delsql);
			child_BillListPanel.clearTable();
			// 删除主表
			int[] li_selectRow = parent_BillListPanel.getTable().getSelectedRows();
			for (int i = 0; i < li_selectRow.length; i++) {
				deleteData.add(parent_BillListPanel.getRealValueAtModel(li_selectRow[i], str_parent_pkname));
			}
			parent_BillListPanel.removeRows(li_selectRow);
			String[] str_delsqls = parent_BillListPanel.getDeleteSQLs();
			if (str_delsqls != null && str_delsqls.length > 0) {
				for (int i = 0; i < str_delsqls.length; i++) {
					sql_delete.add(str_delsqls[i]);
				}
			}
			try {
				if (sql_delete.size() > 0) {
					UIUtil.executeBatchByDS(null, sql_delete);
					sql_delete.clear();
					child_BillListPanel.clearTable();
				}
			} catch (Exception e) {
				MessageBox.show(WLTConstants.STRING_OPERATION_FAILED + ":" + e.getMessage(), WLTConstants.MESSAGE_ERROR);
			}
		}
	}

	public boolean isCanWorkFlowDeal() {
		return false;
	}

	public boolean isCanWorkFlowMonitor() {
		return false;
	}

	private void recoverGrandChildTable(String _key) {
		Object[][] data = getGrandTableData(_key);
		grandchild_BillListPanel.clearTable();
		grandchild_BillListPanel.getTableModel().setValueAtAll(data);
		grandchild_BillListPanel.setAllRowStatusAs("INIT");
		if (updaterowmap.containsKey(_key)) {// 恢复更改行标志
			ArrayList updaterowlist = (ArrayList) updaterowmap.get(_key);
			for (int i = 0; i < updaterowlist.size(); i++)
				grandchild_BillListPanel.setRowStatusAs(((Integer) updaterowlist.get(i)).intValue(), "UPDATE");
			updaterowmap.remove(_key);
		}
		if (insertrowmap.containsKey(_key)) {// 恢复插入行标志
			ArrayList insertrowlist = (ArrayList) insertrowmap.get(_key);
			for (int i = 0; i < insertrowlist.size(); i++)
				grandchild_BillListPanel.setRowStatusAs(((Integer) insertrowlist.get(i)).intValue(), "INSERT");
			insertrowmap.remove(_key);
		}
		// System.out.println("恢愎"+_key+"孙表完成...");
		removeGrandChildFromBack(_key);
		// System.out.println("删除 "+_key+" 备份完成...");

	}

	private void removeGrandChildFromBack(String _key) {
		this.granddata.remove(_key);
	}

	private Object[][] getGrandTableData(String key) {
		return (Object[][]) this.granddata.get(key);
	}

	private void saveGrandTableData(String key, Object[][] _data) {
		this.granddata.put(key, _data);
		insertrowmap.put(key, grandchild_BillListPanel.getRowNumsWithStatus("INSERT"));
		updaterowmap.put(key, grandchild_BillListPanel.getRowNumsWithStatus("UPDATE"));
	}

	private int getStatus() {
		return status;
	}

	private void setStatus(int i) {
		this.status = i;
	}

	private boolean checkExistGrandTableData(String key) {
		return this.granddata.containsKey(key);
	}

	private boolean checkIsGrandChildChanged() {
		for (int i = 0; i < grandchild_BillListPanel.getRowCount(); i++) {
			String temp = grandchild_BillListPanel.getValueAt(i, "_RECORD_ROW_NUMBER").toString();
			if (!temp.equals("INIT"))
				return true;
		}
		return false;
	}
}
/**************************************************************************
 * $RCSfile: AbstractStyleWorkPanel_10.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:22:57 $
 *
 * $Log: AbstractStyleWorkPanel_10.java,v $
 * Revision 1.5  2012/09/14 09:22:57  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:41:02  Administrator
 * *** empty log message ***
 *
 * Revision 1.4  2011/10/10 06:31:47  wanggang
 * restore
 *
 * Revision 1.2  2011/04/02 11:43:58  xch123
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/17 10:23:17  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:32:06  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:19  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:02:04  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:13:06  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:11:01  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:34  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:29  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2008/06/23 08:36:51  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/04/14 09:12:13  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:25  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:36  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/09/23 12:18:52  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:48  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:35  xch
 * *** empty log message ***
 *
 * Revision 1.6  2007/03/13 08:55:13  shxch
 * *** empty log message ***
 *
 * Revision 1.5  2007/03/02 05:16:43  shxch
 * *** empty log message ***
 *
 * Revision 1.4  2007/03/01 09:05:34  shxch
 * *** empty log message ***
 *
 * Revision 1.3  2007/02/10 08:59:37  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 04:48:32  lujian
 * *** empty log message ***
 *
 *
 **************************************************************************/
