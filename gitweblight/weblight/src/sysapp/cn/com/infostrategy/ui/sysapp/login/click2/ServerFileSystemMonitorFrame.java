package cn.com.infostrategy.ui.sysapp.login.click2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonModel;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.plaf.metal.MetalSplitPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;

import com.sun.java.swing.plaf.windows.WindowsTreeUI;

/**
 * 监控服务器端文件的Frame! 即我们要有一个工具可以直接查看服务器端文件,然后可以【上传】【下载】【删除】【执行】
 * 之所以要这个功能是为了在实施与维护阶段，可以直接跳过客户的权限进行部署！甚至是导出数据库！！！
 * 整个界面模仿windows的资源管理器,左边是个树,右边是个列表! 列表中有：文件名，大小，创建时间，按文件名排序!都直接使用JTree,JTable对象,不要平台对象!!因为以后可以导出来作为他用!
 * ★左边的目录树必须是懒装入!!即双击后才加载子结点！左边菜单树上面要有个刷新按钮！！
 * ★【上传】【下载】时如果文件太大,要有一个分步下载的功能!!! 即把一个文件拆成每500K一份，分多次远程调用进行!
 * ★执行服务器端文件时,会返回java.lang.Process对象,然后通过这个对象可以输出执行结果!! 比如将一个cmd命令的结果输出!!
 * 
 * 本文件所在包之所以叫....click2，是为了将登录后，在主界面再次进行第二次点击时才需要的类都统统放在这里!!!比如修改密码,客户端控制台，等...从而保证登录过程下载的class文件足够少!只要必须的!!当再次点击鼠标时才下载本包下的内容!!
 * 以后要将SQLQueryFrame,SQLQueryPanel,ShowServerFrame,ShowClientEnvDialog...等类都移到这个包下面!!然后将原来调用的地方都统统搞成反射!! 只有反射调用才不会在原来调用的地方需要引用本包中的内容!!!
 * 使用TBUtil().refectCallClassStaticMethod()方法!!直接调静态方法!!!
 * @author xch
 *
 */
public class ServerFileSystemMonitorFrame extends JFrame implements ActionListener {
	private JTree jtree = null;
	private DefaultTreeModel treeModel = null;
	private JButton btn_upload, btn_download, btn_run, btn_refesh, btn_delete = null;
	private DefaultTableModel dm = null;
	private JTable jtable = null;
	private JTextField address = null;
	private JPanel btnPanel = null;
	private int uploadlen = 102400, downloadlen = 102400, sorttype = 0;
	private Timer runlogt = null;
	private boolean stop = false;
	private boolean mousePressed = false;
	private boolean mouseReleased = false;
	private String sortKey = null;
	private Color backgroundcolor = new Color(212, 208, 200);
	private Color selectcolor = new Color(10, 36, 106);

	//private MySplashPanel aa = null;

	public ServerFileSystemMonitorFrame() {
		super("查看服务器端文件系统"); //
		this.setSize(800, 500); //
		initialize();
		this.setLocationRelativeTo(null);
	}

	private void initialize() {
		this.setLayout(new BorderLayout());
		this.setIconImage(((ImageIcon) getIconByFileName("", true)).getImage());
		JSplitPane jsp = new JSplitPane(1, getFileSysTree(), getTablePanel());
		jsp.setDividerLocation(150);
		jsp.setDividerSize(5);
		jsp.setUI(new MySplitPaneUI());
		jsp.setBorder(BorderFactory.createLineBorder(backgroundcolor));
		this.add(getAddressPanel(), BorderLayout.NORTH);
		this.add(jsp, BorderLayout.CENTER);
		this.add(getStatusPanel(), BorderLayout.SOUTH);

	}

	public JPanel getAddressPanel() {//这里是仿JFileChooser 以后再弄
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.LINE_AXIS));
		JPanel labelPanel = new JPanel();
		labelPanel.setOpaque(false);
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.PAGE_AXIS));
		labelPanel.add(Box.createRigidArea(new Dimension(1, 4)));
		JLabel jl = new JLabel(" 地址:");
		JButton jb = getButton("设置");
		jb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSet();
			}
		});
		jl.setAlignmentY(0);
		labelPanel.add(jl);
		JPanel fileAndFilterPanel = new JPanel();
		fileAndFilterPanel.setOpaque(false);
		fileAndFilterPanel.add(Box.createRigidArea(new Dimension(1, 8)));
		fileAndFilterPanel.setLayout(new BoxLayout(fileAndFilterPanel, BoxLayout.Y_AXIS));
		address = new JTextField(35) {
			public Dimension getMaximumSize() {
				return new Dimension(Short.MAX_VALUE, super.getPreferredSize().height);
			}
		};
		address.setEditable(false);
		address.setBackground(Color.WHITE);
		fileAndFilterPanel.add(address);
		fileAndFilterPanel.add(Box.createRigidArea(new Dimension(1, 8)));
		jPanel.add(labelPanel);
		jPanel.add(Box.createRigidArea(new Dimension(2, 0)));
		jPanel.add(fileAndFilterPanel);
		jPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		jPanel.add(jb);
		jPanel.setBackground(backgroundcolor);
		return jPanel;
	}

	public void onSet() {
		JPanel jp1 = new JPanel();
		jp1.setLayout(new BoxLayout(jp1, BoxLayout.LINE_AXIS));
		JPanel jp2 = new JPanel();
		jp2.setLayout(new BoxLayout(jp2, BoxLayout.LINE_AXIS));
		JPanel jp = new JPanel();
		jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));
		JLabel jl1 = new JLabel("每次上传字节数(断点续传)");
		JTextField jt1 = new JTextField(uploadlen + "");
		JLabel jl2 = new JLabel("每次下载字节数(断点续传)");
		JTextField jt2 = new JTextField(downloadlen + "");
		jp1.add(jl1);
		jp1.add(jt1);
		jp2.add(jl2);
		jp2.add(jt2);
		jp.add(jp1);
		jp.add(jp2);
		try {
			if (JOptionPane.showConfirmDialog(btnPanel, jp, "提示", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
				uploadlen = Integer.parseInt(jt1.getText());
				downloadlen = Integer.parseInt(jt2.getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(btnPanel, e.getMessage());
			return;
		}
	}

	private void setAddress(String path) {
		address.setText(path);
	}

	public JPanel getStatusPanel() {
		JPanel jp = new JPanel();
		jp.setLayout(new FlowLayout());
		JLabel jl = new JLabel("传输队列");
		jp.add(jl);
		jp.setBackground(backgroundcolor);
		return jp;
	}

	public JPanel getFileSysTree() {
		JPanel jp = new JPanel();
		jp.setLayout(new BorderLayout());
		JPanel jp_btn = new JPanel(new FlowLayout(FlowLayout.LEFT));
		btn_refesh = getButton("刷新");
		btn_refesh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onRefesh();
			}
		});
		jp_btn.add(btn_refesh);
		jp_btn.setBackground(backgroundcolor);
		jp.add(jp_btn, BorderLayout.NORTH);
		try {
			HashMap resultmap = UIUtil.getCommonService().commMethod("cn.com.infostrategy.bs.sysapp.ServerFileSystemBSO", "getFileListByDirectory", null);
			String resultstr = (String) resultmap.get("state");
			jtree = new JTree();
			jtree.setBackground(Color.WHITE);
			if ("100".equals(resultstr)) {
				List data = (List) resultmap.get("data");
				jtree.setModel(getDefaultTreeModel(data));
				((BasicTreeUI) jtree.getUI()).setCollapsedIcon(WindowsTreeUI.CollapsedIcon.createCollapsedIcon()); //
				((BasicTreeUI) jtree.getUI()).setExpandedIcon(WindowsTreeUI.CollapsedIcon.createExpandedIcon()); //
				jtree.setCellRenderer(new MyTreeRender());
				jtree.setRootVisible(false);
				jtree.setShowsRootHandles(true);
				jtree.addTreeWillExpandListener(new TreeWillExpandListener() {
					public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
						onCollapse(event.getPath());
					}

					public void treeWillExpand(final TreeExpansionEvent event) throws ExpandVetoException {
						jtree.setCursor(new Cursor(Cursor.WAIT_CURSOR));
						onExpand(event.getPath());
						jtree.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					}
				});
				jtree.addTreeSelectionListener(new TreeSelectionListener() {
					public void valueChanged(TreeSelectionEvent e) {
						onSelect(e.getNewLeadSelectionPath());
					}
				});
				jtree.expandRow(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(jtree, e.getMessage());
		}
		JScrollPane sp = new JScrollPane(jtree);
		sp.setOpaque(false);
		jp.add(sp, BorderLayout.CENTER);
		jp.setBorder(BorderFactory.createLineBorder(backgroundcolor));
		return jp;
	}

	public void onRefesh() {
		try {
			jtree.removeAll();
			HashMap resultmap = UIUtil.getCommonService().commMethod("cn.com.infostrategy.bs.sysapp.ServerFileSystemBSO", "getFileListByDirectory", null);
			String resultstr = (String) resultmap.get("state");
			if ("100".equals(resultstr)) {
				List data = (List) resultmap.get("data");
				jtree.setModel(getDefaultTreeModel(data));
				jtree.expandRow(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(jtree, e.getMessage());
		}
	}

	private void onSelect(TreePath path) {
		if (path != null && path.getLastPathComponent() != null) {
			final DefaultMutableTreeNode selectvo = (DefaultMutableTreeNode) path.getLastPathComponent();
			new SplashWindow(jtree, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					onQueryList(((MyTreeNodeVO) selectvo.getUserObject()).getId());
				}
			});
		}
	}

	private void onQueryList(String path) {
		try {
			setAddress(path);
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("directory", path);
			param.put("level", "1");
			param.put("rootable", "false");
			param.put("filetype", "singlefile");
			jtable.clearSelection();
			if (sortKey != null) {
				sorttype = 0;
				jtable.getTableHeader().repaint(jtable.getTableHeader().getHeaderRect(jtable.getColumnModel().getColumnIndex(sortKey)));
				sortKey = null;
			}
			if (jtable.getRowCount() >= 0 && jtable.getCellEditor() != null) {
				jtable.getCellEditor().stopCellEditing(); //
			}
			dm.getDataVector().removeAllElements();
			dm.fireTableRowsDeleted(0, dm.getRowCount()); //
			jtable.invalidate();
			jtable.repaint();
			HashMap<String, Object> resultmap = UIUtil.getCommonService().commMethod("cn.com.infostrategy.bs.sysapp.ServerFileSystemBSO", "getFileListByDirectory", param);
			if ("100".equals((String) resultmap.get("state"))) {
				List<HashMap<String, Object>> data = (List<HashMap<String, Object>>) resultmap.get("data");
				Vector allrow = new Vector();
				Vector onerow = null;
				if (data != null && data.size() > 0) {
					for (int i = 0; i < data.size(); i++) {
						onerow = new Vector();
						onerow.add(data.get(i).get("name"));
						onerow.add(data.get(i).get("filetype"));
						onerow.add(data.get(i).get("length"));
						onerow.add(data.get(i).get("lastmodify"));
						if ((data.get(i).get("icon")) == null) {
							onerow.add(getIconByFileName(data.get(i).get("name").toString(), false));
						} else {
							onerow.add(new ImageIcon((byte[]) data.get(i).get("icon")));
						}
						//this.setIcon(new ImageIcon((byte[])p.get("icon")));
						onerow.add(data.get(i).get("id"));
						onerow.add(data.get(i).get("reallength"));
						allrow.add(onerow);
					}
				}
				dm.getDataVector().addAll(allrow);
				dm.fireTableRowsInserted(0, allrow.size() - 1);
				jtable.invalidate();
				jtable.repaint();
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(jtable, e.getMessage());
		}

	}

	/**
	 */
	private void onCollapse(TreePath path) {

	}

	/**
	 */
	private void onExpand(TreePath path) {
		DefaultMutableTreeNode selectnode = (DefaultMutableTreeNode) path.getLastPathComponent();
		if (selectnode != null && !selectnode.isRoot()) {
			try {
				MyTreeNodeVO vo = (MyTreeNodeVO) selectnode.getUserObject();
				if (!vo.ifhasExpand) {
					HashMap<String, Object> param = new HashMap<String, Object>();
					param.put("directory", vo.getId());
					param.put("level", "1");
					param.put("rootable", "false");
					HashMap<String, Object> resultmap = UIUtil.getCommonService().commMethod("cn.com.infostrategy.bs.sysapp.ServerFileSystemBSO", "getFileListByDirectory", param);
					treeModel.removeNodeFromParent(selectnode.getFirstLeaf());
					if ("100".equals((String) resultmap.get("state"))) {
						List<HashMap<String, String>> data = (List<HashMap<String, String>>) resultmap.get("data");
						if (data != null && data.size() > 0) {
							for (int i = 0; i < data.size(); i++) {
								DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new MyTreeNodeVO(data.get(i)));
								newNode.add(new DefaultMutableTreeNode());
								treeModel.insertNodeInto(newNode, selectnode, selectnode.getChildCount());
							}
						}
					}
					vo.setIfhasExpand(true);
				}
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(jtree, e.getMessage());
			}
		}
	}

	public DefaultMutableTreeNode getSelectedNode() {
		if (jtree == null) {
			return null;
		}
		TreePath path = jtree.getSelectionPath();
		if (path == null) {
			return null;
		}
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		return node;
	}

	public JPanel getTablePanel() {
		JPanel tablepanel = new JPanel();
		tablepanel.setLayout(new BorderLayout());
		JPanel tablebtnpanel = getListBtnPanel();
		tablepanel.add(tablebtnpanel, BorderLayout.NORTH);
		dm = new DefaultTableModel(new String[] { "名称", "类型", "大小", "最后修改时间" }, 0);
		DefaultTableColumnModel cm = new DefaultTableColumnModel();
		TableColumn a = new TableColumn(0);
		a.setHeaderValue("名称");
		a.setIdentifier("名称");
		a.setPreferredWidth(300);
		a.setCellRenderer(new MyTableCellRender());
		DefaultCellEditor aa = (DefaultCellEditor) new MyTableCellEditer(new JTextField());
		aa.setClickCountToStart(2);
		a.setCellEditor((TableCellEditor) new MyTableCellEditer(new JTextField()));
		TableColumn b = new TableColumn(1);
		b.setHeaderValue("类型");
		b.setIdentifier("类型");
		b.setPreferredWidth(110);
		b.setCellRenderer(new MyTableCellRender());
		b.setCellEditor((TableCellEditor) new MyTableCellEditer(new JTextField()));
		TableColumn c = new TableColumn(2);
		c.setHeaderValue("大小");
		c.setIdentifier("大小");
		c.setPreferredWidth(110);
		c.setCellRenderer(new MyTableCellRender());
		c.setCellEditor((TableCellEditor) new MyTableCellEditer(new JTextField()));
		TableColumn d = new TableColumn(3);
		d.setHeaderValue("最后修改时间");
		d.setIdentifier("最后修改时间");
		d.setPreferredWidth(150);
		d.setCellRenderer(new MyTableCellRender());
		d.setCellEditor((TableCellEditor) new MyTableCellEditer(new JTextField()));
		cm.addColumn(a);
		cm.addColumn(b);
		cm.addColumn(c);
		cm.addColumn(d);
		jtable = new JTable(dm, cm);
		jtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jtable.getTableHeader().setPreferredSize(new Dimension(1000, 20));
		jtable.getTableHeader().setDefaultRenderer(new MySortRender());
		jtable.getTableHeader().setBackground(Color.WHITE);
		jtable.getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				sort(e);
			}

			public void mousePressed(MouseEvent e) {
				mousePressed = true;
				int a = jtable.getTableHeader().columnAtPoint(new Point(e.getX(), e.getY()));
				if (a < jtable.getColumnModel().getColumnCount() && a > -1) {
					if (jtable.getColumnModel().getColumn(a).getIdentifier().toString().equals(sortKey)) {
						jtable.getTableHeader().repaint(jtable.getTableHeader().getHeaderRect(a));
					} else {
						if (sortKey != null) {
							jtable.getTableHeader().repaint(jtable.getTableHeader().getHeaderRect(jtable.getColumnModel().getColumnIndex(sortKey)));
						}
						sortKey = jtable.getColumnModel().getColumn(a).getIdentifier().toString();
						sorttype = 0;
						jtable.getTableHeader().repaint(jtable.getTableHeader().getHeaderRect(a));
					}
				}
			}

			public void mouseReleased(MouseEvent e) {
				mousePressed = false;
				int a = jtable.getTableHeader().columnAtPoint(new Point(e.getX(), e.getY()));
				if (a < jtable.getColumnModel().getColumnCount() && a > -1) {
					if (jtable.getColumnModel().getColumn(a).getIdentifier().toString().equals(sortKey)) {
						jtable.getTableHeader().repaint(jtable.getTableHeader().getHeaderRect(a));
					} else {
						if (sortKey != null) {
							jtable.getTableHeader().repaint(jtable.getTableHeader().getHeaderRect(jtable.getColumnModel().getColumnIndex(sortKey)));
						}
						sortKey = jtable.getColumnModel().getColumn(a).getIdentifier().toString();
						sorttype = 0;
						jtable.getTableHeader().repaint(jtable.getTableHeader().getHeaderRect(a));
					}
				}
			}
		});
		jtable.setShowGrid(false);
		jtable.setRowHeight(30);
		jtable.setOpaque(false);
		JScrollPane js = new JScrollPane();
		js.getViewport().add(jtable);
		js.getViewport().setBackground(Color.WHITE);
		tablepanel.add(js, BorderLayout.CENTER);
		tablepanel.setBackground(Color.WHITE);
		return tablepanel;
	}

	public void sort(MouseEvent e) {
		int a = jtable.getTableHeader().columnAtPoint(new Point(e.getX(), e.getY()));
		if (a < jtable.getColumnModel().getColumnCount() && a > -1) {
			if (jtable.getColumnModel().getColumn(a).getIdentifier().toString().equals(sortKey)) {
				if (sorttype == 0) {
					sorttype = 1;
				} else if (sorttype == 1) {
					sorttype = -1;
				} else if (sorttype == -1) {
					sorttype = 1;
				}
			} else {
				sorttype = 1;
			}
			realSortJtable(a, sorttype);
		}
	}

	public void realSortJtable(int index, int sorttype) {
		Vector<Vector<String>> date = dm.getDataVector();
		if (date == null || date.size() < 1) {
			return;
		}
		Vector<Vector<String>> date2 = new Vector<Vector<String>>();
		for (int i = 0; i < date.size() - 1; i++) {
			for (int j = date.size() - 1; j > i; j--) {
				if (sorttype == 1) {
					if ("大小".equals(sortKey)) {
						if (Long.valueOf(date.get(i).get(6)) > Long.valueOf(date.get(j).get(6))) {
							Vector<String> param = date.get(i);
							date.set(i, date.get(j));
							date.set(j, param);
						}
					} else {
						if (date.get(i).get(index).compareTo(date.get(j).get(index)) > 0) {
							Vector<String> param = date.get(i);
							date.set(i, date.get(j));
							date.set(j, param);
						}
					}

				} else {
					if ("大小".equals(sortKey)) {
						if (Long.valueOf(date.get(i).get(6)) < Long.valueOf(date.get(j).get(6))) {
							Vector<String> param = date.get(i);
							date.set(i, date.get(j));
							date.set(j, param);
						}
					} else {
						if (date.get(i).get(index).compareTo(date.get(j).get(index)) < 0) {
							Vector<String> param = date.get(i);
							date.set(i, date.get(j));
							date.set(j, param);
						}
					}
				}
			}
			date2.add(date.get(i));
		}
		date2.add(date.get(date.size() - 1));
		jtable.clearSelection();
		if (jtable.getRowCount() >= 0 && jtable.getCellEditor() != null) {
			jtable.getCellEditor().stopCellEditing(); //
		}
		dm.getDataVector().removeAllElements();
		dm.fireTableRowsDeleted(0, dm.getRowCount());
		dm.getDataVector().addAll(date2);
		dm.fireTableRowsInserted(0, date2.size() - 1);
		//dm.fireTableRowsUpdated(0, date.size() - 1);
		jtable.invalidate();
		jtable.repaint();
	}

	public Icon getIconByFileName(String filename, boolean isdir) {

		String type = filename;
		if (filename.indexOf(".") >= 0) {
			type = filename.substring(filename.lastIndexOf("."));
		}
		try {
			File a = File.createTempFile("icon", type);
			Icon rt = null;
			if (isdir) {
				rt = FileSystemView.getFileSystemView().getSystemIcon(a.getParentFile());
			} else {
				rt = FileSystemView.getFileSystemView().getSystemIcon(a);
			}
			a.delete();
			return rt;
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (isdir) {
			return UIManager.getIcon("FileView.directoryIcon");
		}
		return UIManager.getIcon("FileView.fileIcon");
	}

	public JPanel getListBtnPanel() {
		btnPanel = new JPanel();
		btnPanel.setLayout(new FlowLayout());
		btn_upload = getButton("上传");
		btn_download = getButton("下载");
		btn_delete = getButton("删除");
		btn_run = getButton("执行");
		btn_upload.addActionListener(this);
		btn_download.addActionListener(this);
		btn_delete.addActionListener(this);
		btn_run.addActionListener(this);
		btnPanel.add(btn_upload);
		btnPanel.add(btn_download);
		btnPanel.add(btn_delete);
		btnPanel.add(btn_run);
		btnPanel.setBackground(backgroundcolor);
		return btnPanel;
	}

	private DefaultTreeModel getDefaultTreeModel(List data) {
		if (data != null && data.size() > 0) {
			HashMap rootM = (HashMap) data.get(0);
			DefaultMutableTreeNode root = new DefaultMutableTreeNode(new MyTreeNodeVO("服务器", "服务器", true));
			HashMap a = null;
			HashMap treeNodeMap = new HashMap();
			DefaultMutableTreeNode[] node_level_1 = new DefaultMutableTreeNode[data.size()];
			for (int i = 0; i < data.size(); i++) {
				a = (HashMap) data.get(i);
				node_level_1[i] = new DefaultMutableTreeNode(new MyTreeNodeVO(a));
				node_level_1[i].setAllowsChildren(true);
				treeNodeMap.put(a.get("id"), node_level_1[i]);
			}
			String parentid = null;
			for (int i = 0; i < node_level_1.length; i++) {
				parentid = ((MyTreeNodeVO) node_level_1[i].getUserObject()).getParentid();
				if (parentid != null && treeNodeMap.containsKey(parentid)) {
					((DefaultMutableTreeNode) treeNodeMap.get(parentid)).add(node_level_1[i]);
				} else {
					root.add(node_level_1[i]);
				}
			}

			for (int i = 0; i < node_level_1.length; i++) {//在每个叶子下加一个空的叶子，在打开时懒装入
				if (node_level_1[i].isLeaf()) {
					node_level_1[i].add(new DefaultMutableTreeNode());
				}
			}
			treeModel = new DefaultTreeModel(root);
		}
		return treeModel;
	}

	public void onDealDownLoad() {
		int row = jtable.getSelectedRow();
		if (row == -1) {
			JOptionPane.showMessageDialog(btnPanel, "请选择一个文件!");
			return;
		}
		final String path = (String) dm.getValueAt(row, 5);
		final String name = (String) dm.getValueAt(row, 0);
		try {
			JFileChooser jchooser = new JFileChooser();
			File f = null;
			if (ClientEnvironment.str_downLoadFileDir.endsWith("/") || ClientEnvironment.str_downLoadFileDir.endsWith("\\")) {
				f = new File(new File(ClientEnvironment.str_downLoadFileDir + name).getCanonicalPath());
			} else {
				f = new File(new File(ClientEnvironment.str_downLoadFileDir + File.separator + name).getCanonicalPath());
			}
			jchooser.setSelectedFile(f); //
			int li_rewult = jchooser.showSaveDialog(btnPanel);
			if (li_rewult == JFileChooser.APPROVE_OPTION) {
				final File chooseFile = jchooser.getSelectedFile();
				ClientEnvironment.str_downLoadFileDir = chooseFile.getParent();
				if (chooseFile != null) {
					stop = false;
					new SplashWindow(btnPanel, new AbstractAction() {
						public void actionPerformed(ActionEvent e) {
							realDownLoadFile(chooseFile, path);
						}
					});
					stop = true;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(btnPanel, ex.getMessage());
		}
	}

	private void onDealDelete() {
		try {
			int row = jtable.getSelectedRow();
			if (row == -1) {
				JOptionPane.showMessageDialog(btnPanel, "请选择一个文件!");
				return;
			}
			HashMap param = new HashMap();
			String path = (String) dm.getValueAt(row, 5);
			param.put("path", path);
			UIUtil.getCommonService().commMethod("cn.com.infostrategy.bs.sysapp.ServerFileSystemBSO", "deleteFile", param);
			onSelect(jtree.getSelectionPath());
			JOptionPane.showMessageDialog(btnPanel, "删除文件成功!");
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(btnPanel, e.getMessage());
		}
	}

	private static Window getWindowForComponent(Component parentComponent) throws HeadlessException {
		if (parentComponent == null) {
			return JOptionPane.getRootFrame(); //
		}
		if (parentComponent instanceof Frame || parentComponent instanceof Dialog) {
			return (Window) parentComponent;
		}
		return getWindowForComponent(parentComponent.getParent());
	}

	public void realDownLoadFile(File f, String path) {
		FileOutputStream out = null;
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			out = new FileOutputStream(f);
			HashMap param = new HashMap();
			int len = downloadlen;
			int off = 0;
			int rlen = 0;
			byte[] buf = null;
			param.put("len", len);
			param.put("path", path);
			while (true) {
				if (stop) {
					if (SplashWindow.window != null) {
						SplashWindow.window.closeWindow();
					}
					JOptionPane.showMessageDialog(btnPanel, "下载文件失败!!!");
					break;
				}
				param.put("off", off);
				HashMap map = UIUtil.getCommonService().commMethod("cn.com.infostrategy.bs.sysapp.ServerFileSystemBSO", "downLoadFile", param);
				if (map != null) {
					rlen = Integer.parseInt(map.get("rlen").toString());
					if (rlen != -1) {
						off = off + rlen;
						buf = (byte[]) map.get("byte");
						out.write(buf);
					} else {
						break;
					}
				} else {
					break;
				}
			}
			if (SplashWindow.window != null) {
				SplashWindow.window.closeWindow(); //
				JOptionPane.showMessageDialog(btnPanel, "下载文件成功!!!");
			}
			return;
		} catch (Exception e) {
			e.printStackTrace();
			if (SplashWindow.window != null) {
				SplashWindow.window.closeWindow(); //
			}
			JOptionPane.showMessageDialog(btnPanel, "下载文件失败，原因【" + e.getMessage() + "】!!!");
			return;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
					if (SplashWindow.window != null) {
						SplashWindow.window.closeWindow(); //
					}
					JOptionPane.showMessageDialog(btnPanel, e.getMessage());
				}
			}
		}
	}

	public void onDealUpLoad() {
		DefaultMutableTreeNode node = getSelectedNode();
		if (node == null) {
			JOptionPane.showMessageDialog(btnPanel, "请选择一个文件夹!");
			return;
		}
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		chooser.setCurrentDirectory(new File("C:\\"));
		int result = chooser.showOpenDialog(btnPanel);
		final File allChooseFile = chooser.getSelectedFile(); //
		if (result == 0 && chooser.getSelectedFile() != null) {

		} else {
			return;
		}
		final String path = ((MyTreeNodeVO) node.getUserObject()).getId();
		stop = false;
		new SplashWindow(btnPanel, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				realUploadFile(allChooseFile, path, allChooseFile.getName());
			}
		});
		stop = true;
	}

	public void realUploadFile(File file, String serverpath, String name) {
		FileInputStream fins = null;
		try {
			int filelength = new Long(file.length()).intValue();
			ByteArrayOutputStream baos = null;
			byte[] filecontent = new byte[uploadlen];
			if (filelength < uploadlen) {
				filecontent = new byte[uploadlen];
			}
			int len = -1;
			int rel = 0;
			fins = new FileInputStream(file);
			HashMap param = new HashMap();
			while ((len = fins.read(filecontent)) != -1) {
				try {
					if (stop) {
						if (SplashWindow.window != null) {
							SplashWindow.window.closeWindow();
						}
						JOptionPane.showMessageDialog(btnPanel, "上传文件失败!!!");
						break;
					}
					baos = new ByteArrayOutputStream();
					baos.write(filecontent, 0, len);
					byte[] byteCodes = baos.toByteArray();
					param.put("path", serverpath);
					param.put("name", name);
					param.put("date", byteCodes);
					param.put("off", rel);
					rel = rel + byteCodes.length;
					UIUtil.getCommonService().commMethod("cn.com.infostrategy.bs.sysapp.ServerFileSystemBSO", "upLoadFile", param);
				} catch (Exception e) {
					if (SplashWindow.window != null) {
						SplashWindow.window.closeWindow(); //
					}
					JOptionPane.showMessageDialog(btnPanel, e.getMessage());
					return;
				} finally {
					if (baos != null) {
						try {
							baos.close();
						} catch (IOException e) {
							e.printStackTrace();
							JOptionPane.showMessageDialog(btnPanel, e.getMessage());
						}
					}
				}
			}
			if (SplashWindow.window != null) {
				SplashWindow.window.closeWindow(); //
				onSelect(jtree.getSelectionPath());
				JOptionPane.showMessageDialog(btnPanel, "上传成功!");

			}
		} catch (Exception ex) {
			ex.printStackTrace();
			if (SplashWindow.window != null) {
				SplashWindow.window.closeWindow(); //
			}
			JOptionPane.showMessageDialog(btnPanel, ex.getMessage());
		} finally {
			if (fins != null) {
				try {
					fins.close();
				} catch (IOException e) {
					e.printStackTrace();
					if (SplashWindow.window != null) {
						SplashWindow.window.closeWindow(); //
					}
					JOptionPane.showMessageDialog(btnPanel, e.getMessage());
				}
			}
		}
	}

	public void onDealRun() {
		int row = jtable.getSelectedRow();
		if (row == -1) {
			JOptionPane.showMessageDialog(btnPanel, "请选择一个文件!");
			return;
		}
		final String path = (String) dm.getValueAt(row, 5);
		try {
			HashMap param = new HashMap();
			param.put("path", path);
			HashMap resultmap = UIUtil.getCommonService().commMethod("cn.com.infostrategy.bs.sysapp.ServerFileSystemBSO", "runFile", param);
			String logpath = resultmap.get("logpath").toString();
			realRun(logpath);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(btnPanel, e.getMessage());
		}
	}

	public void realRun(final String path) {
		stop = false;
		final JFrame jf = new JFrame("查看进程日志");
		jf.setLayout(new BorderLayout());
		JPanel jp = new JPanel();
		jp.setLayout(new FlowLayout());
		//		JButton jb = getButton("刷新");
		//		jp.add(jb);
		final JTextArea textArea = new JTextArea();
		textArea.setEditable(false); //
		textArea.setLineWrap(true);
		textArea.setFont(new Font("宋体", Font.PLAIN, 12));
		jf.add(jp, BorderLayout.NORTH);
		JScrollPane js = new JScrollPane(textArea);
		jf.add(js, BorderLayout.CENTER);
		jf.setSize(800, 500);
		jf.setLocationRelativeTo(null);
		jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jf.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				if (runlogt != null) {
					System.out.println("timer cancel");
					stop = true;
				}
			}
		});
		jf.setVisible(true);
		try {
			runlogt = new Timer();
			runlogt.schedule(new TimerTask() {
				public void run() {
					HashMap param = new HashMap();
					int len = 10;//一次取多少行
					int off = 0;//取到了哪行
					int rlen = 0;
					String[] buf = null;
					param.put("len", len);
					param.put("path", path);
					int j = 0;
					while (true) {
						if (jf == null || !jf.isShowing()) {
							this.cancel();
							runlogt.cancel();
							break;
						}
						if (j > 100) {
							this.cancel();
							runlogt.cancel();
							break;
							//100秒没有数据更新就退出
						}
						param.put("off", off);
						try {
							HashMap map = UIUtil.getCommonService().commMethod("cn.com.infostrategy.bs.sysapp.ServerFileSystemBSO", "readLineFromFile", param);
							if (map != null) {
								rlen = Integer.parseInt(map.get("rlen").toString());
								if (rlen > 0) {
									j = 0;
									buf = (String[]) map.get("string");
									if (buf != null && buf.length > 0) {
										for (int i = 0; i < buf.length; i++) {
											textArea.append(buf[i] + "\r\n");
											textArea.setCaretPosition(textArea.getText().length());
										}
										if ("-------------结束-----------".equals(buf[buf.length - 1])) {
											System.out.println("结束--------------------------------------");
											break;
										}
									}
									off = Integer.parseInt(map.get("off").toString());
								}
							}
							Thread.sleep(1000);
							j++;
						} catch (Exception e) {
							e.printStackTrace();
							JOptionPane.showMessageDialog(btnPanel, e.getMessage());
							break;
						}
					}
				}
			}, 0);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(btnPanel, e.getMessage());
		}
	}

	public int getSelectRow() {
		int selectrow = jtable.getSelectedRow();
		return selectrow;
	}

	//TBUtil().refectCallClassStaticMethod("ServerFileSystemMonitorFrame","openMe",new Object[]{this,"监控窗口","3"});
	public static void openMe(java.awt.Container _parent, String _tile, String _type) {
		ServerFileSystemMonitorFrame frame = new ServerFileSystemMonitorFrame(); //
		frame.initialize(); //
		frame.setVisible(true); //
	}

	class MyTreeNodeVO {

		private String name = null;
		private String length = null;
		private String id = null;
		private String lastmodify = null;
		private String isdirectory = null;
		private String parentid = null;
		private String level = null;
		private boolean ifhasExpand = false;
		private String filetype = null;
		private Icon icon = null;

		public MyTreeNodeVO(HashMap p) {
			this.setId((String) p.get("id"));
			this.setName((String) p.get("name"));
			this.setParentid((String) p.get("parentid"));
			this.setLastmodify((String) p.get("lastmodify"));
			this.setIsdirectory((String) p.get("isdirectory"));
			this.setIfhasExpand(Boolean.parseBoolean(p.get("ifhasExpand").toString()));
			this.setLevel((String) p.get("level"));
			this.setFiletype((String) p.get("filetype"));
			if (p.get("icon") == null) {
				this.setIcon(getIconByFileName(this.getName(), true));
			} else {
				this.setIcon(new ImageIcon((byte[]) p.get("icon")));
			}
		}

		public MyTreeNodeVO(String id, String name, boolean ifhasExpand) {
			this.id = id;
			this.name = name;
			this.ifhasExpand = ifhasExpand;
		}

		public String getFiletype() {
			return filetype;
		}

		public void setFiletype(String filetype) {
			this.filetype = filetype;
		}

		@Override
		public String toString() {
			return getName();
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getLength() {
			return length;
		}

		public void setLength(String length) {
			this.length = length;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getLastmodify() {
			return lastmodify;
		}

		public void setLastmodify(String lastmodify) {
			this.lastmodify = lastmodify;
		}

		public String getIsdirectory() {
			return isdirectory;
		}

		public void setIsdirectory(String isdirectory) {
			this.isdirectory = isdirectory;
		}

		public String getParentid() {
			return parentid;
		}

		public void setParentid(String parentid) {
			this.parentid = parentid;
		}

		public String getLevel() {
			return level;
		}

		public void setLevel(String level) {
			this.level = level;
		}

		public boolean isIfhasExpand() {
			return ifhasExpand;
		}

		public void setIfhasExpand(boolean ifhasExpand) {
			this.ifhasExpand = ifhasExpand;
		}

		public Icon getIcon() {
			return icon;
		}

		public void setIcon(Icon icon) {
			this.icon = icon;
		}
	}

	class MyTreeRender extends DefaultTreeCellRenderer {
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			JLabel oldLabel = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
			MyTreeNodeVO vo = (MyTreeNodeVO) ((DefaultMutableTreeNode) value).getUserObject();
			JLabel superlabel = new JLabel(oldLabel.getText());
			//superlabel.setIcon(MetalIconFactory.getFileChooserNewFolderIcon());
			try {
				//				File tmp = new File("c:\\");
				if (vo != null && vo.getIcon() != null) {
					superlabel.setIcon(vo.getIcon());
				}
				if (selected) {
					superlabel.setOpaque(true);
					superlabel.setBackground(selectcolor);
					superlabel.setForeground(Color.WHITE); //
				} else {
					superlabel.setOpaque(false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return superlabel;
		}
	}

	class MyTableCellRender extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel oldLabel = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			JLabel jl = new JLabel();
			jl.setText(oldLabel.getText());
			if (column == 0) {
				try {
					jl.setIcon((Icon) table.getModel().getValueAt(row, 4));
				} catch (Exception ee) {
					ee.printStackTrace();
				}
			}
			if (isSelected) {
				jl.setOpaque(true);
				jl.setBackground(selectcolor);
				jl.setForeground(Color.WHITE);
			} else {
				jl.setOpaque(false);
			}
			return jl;
		}
	}

	class MyTableCellEditer extends DefaultCellEditor implements TableCellEditor {
		private String value_ = null;
		private JLabel label = null;

		public MyTableCellEditer(JTextField textField) {
			super(textField);
		}

		public Component getTableCellEditorComponent(JTable _table, Object value, boolean isSelected, final int row, final int column) {
			label = new JLabel();
			label.setText(value == null ? "" : String.valueOf(value));
			if (column == 0) {
				label.setIcon((Icon) _table.getModel().getValueAt(row, 4));
			}
			if (isSelected) {
				label.setOpaque(true);
				label.setBackground(selectcolor);
				label.setForeground(Color.WHITE);
			} else {
				label.setOpaque(false);
			}
			return label;
		}

		public Object getCellEditorValue() {
			return label.getText();
		}
	}

	class MySortRender extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel jl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			jl.setBackground(backgroundcolor);
			jl.setHorizontalTextPosition(SwingConstants.LEFT);
			jl.setBorder(BorderFactory.createRaisedBevelBorder());
			if (table.getColumnModel().getColumn(column).getIdentifier().equals(sortKey)) {
				if (mousePressed) {
					jl.setBorder(BorderFactory.createLoweredBevelBorder());
				}
				if (sorttype == 0) {
					jl.setIcon(null);
				} else if (sorttype == 1) {
					jl.setIcon(new MyIcon(sorttype));
				} else if (sorttype == -1) {
					jl.setIcon(new MyIcon(sorttype));
				}
			} else {
				jl.setIcon(null);
			}
			return jl;
		}
	}

	class MyIcon implements Icon {
		private int d = 1;

		public MyIcon(int d) {
			this.d = d;
		}

		public int getIconHeight() {
			return 10;
		}

		public int getIconWidth() {
			return 10;
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(Color.BLACK);
			g.drawLine(x + 5, y, x + 5, y + 10);
			if (d == 1) {
				g.drawLine(x + 5, y, x, y + 5);
				g.drawLine(x + 5, y, x + 10, y + 5);
			} else {
				g.drawLine(x, y + 5, x + 5, y + 10);
				g.drawLine(x + 10, y + 5, x + 5, y + 10);
			}
		}

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_upload) {
			onDealUpLoad();
		} else if (e.getSource() == btn_download) {
			onDealDownLoad();
		} else if (e.getSource() == btn_delete) {
			onDealDelete();
		} else if (e.getSource() == btn_run) {
			onDealRun();
		}
	}

	class MySplitPaneUI extends MetalSplitPaneUI {
		public BasicSplitPaneDivider createDefaultDivider() {
			return new MySplitPaneDivider(this);
		}
	}

	class MySplitPaneDivider extends BasicSplitPaneDivider {
		public MySplitPaneDivider(BasicSplitPaneUI ui) {
			super(ui);
		}

		public void paint(Graphics g) {
			//			  super.paint(g);
			g.setColor(backgroundcolor);
			Dimension size = getSize();
			Rectangle clip = g.getClipBounds();
			g.fillRect(clip.x, clip.y, clip.width, clip.height);
		}
	}

	class MySplashPanel extends JPanel {
		private Timer actionT = new Timer();
		private JDialog parent = null;
		private AbstractAction action = null;
		private JProgressBar jpb = null;
		private JButton jb = null;

		public MySplashPanel() {
			super();
		}

		public MySplashPanel(JDialog jd, AbstractAction action) {
			super();
			this.parent = jd;
			this.action = action;
		}

		public void init() {
			this.setLayout(new BorderLayout());
			jpb = new JProgressBar();
			jpb.setStringPainted(true);
			jb = getButton("取消");
			jb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JLabel confirmlabel = new JLabel("你是否真的想取消当前操作?"); //
					confirmlabel.setFont(new Font("宋体", Font.PLAIN, 12)); //
					if (JOptionPane.showConfirmDialog(jb, confirmlabel, "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
						return;
					}
					actionT.cancel();
					if (parent != null) {
						parent.dispose();
					}
				}
			});
			this.add(jpb, BorderLayout.CENTER);
			this.add(jb, BorderLayout.EAST);
			actionT.schedule(new TimerTask() {
				public void run() {
					doAction();
				}
			}, 0);
			if (parent != null) {
				this.parent.setSize(new Dimension(350, 130));
				this.parent.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
				parent.add(this);
				parent.setVisible(true);
			}
		}

		private void doAction() {
			try {
				action.actionPerformed(new ActionEvent(this, 0, ""));
				actionT.cancel();
				if (parent != null) {
					parent.dispose();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				actionT.cancel();
				if (parent != null) {
					parent.dispose();
				}
			}
		}

		public Timer getActionT() {
			return actionT;
		}

		public void setActionT(Timer actionT) {
			this.actionT = actionT;
		}

		public JDialog getParent() {
			return parent;
		}

		public void setParent(JDialog parent) {
			this.parent = parent;
		}

		public AbstractAction getAction() {
			return action;
		}

		public void setAction(AbstractAction action) {
			this.action = action;
		}

		public JProgressBar getJpb() {
			return jpb;
		}

		public void setJpb(JProgressBar jpb) {
			this.jpb = jpb;
		}
	}

	public JButton getButton(String text) {
		JButton rt = new JButton(text);
		rt.setBorderPainted(false);
		rt.setBackground(backgroundcolor);
		rt.setUI(new MyWindowsButtonUI());
		return rt;
	}

	class MyWindowsButtonUI extends BasicButtonUI {
		public void paint(Graphics g, JComponent c) {
			AbstractButton button = (AbstractButton) c;
			ButtonModel model = button.getModel();
			Rectangle rect = c.getBounds();
			if (model.isArmed() || model.isPressed()) { //按下去的状态!!
				paintLoweredBevel(button, g, 0, 0, rect.width, rect.height);
			} else {
				paintRaisedBevel(button, g, 0, 0, rect.width, rect.height);
			}
			super.paint(g, c);
		}

		private void paintRaisedBevel(Component c, Graphics g, int x, int y, int width, int height) {
			Color oldColor = g.getColor();
			int h = height;
			int w = width;
			g.translate(x, y);
			g.setColor(c.getBackground().brighter().brighter());
			g.drawLine(0, 0, 0, h - 2);
			g.drawLine(1, 0, w - 2, 0);
			g.setColor(c.getBackground().brighter());
			g.drawLine(1, 1, 1, h - 3);
			g.drawLine(2, 1, w - 3, 1);
			g.setColor(c.getBackground().darker().darker());
			g.drawLine(0, h - 1, w - 1, h - 1);
			g.drawLine(w - 1, 0, w - 1, h - 2);
			g.setColor(c.getBackground().darker());
			g.drawLine(1, h - 2, w - 2, h - 2);
			g.drawLine(w - 2, 1, w - 2, h - 3);
			g.translate(-x, -y);
			g.setColor(oldColor);
		}

		private void paintLoweredBevel(Component c, Graphics g, int x, int y, int width, int height) {
			Color oldColor = g.getColor();
			int h = height;
			int w = width;
			g.translate(x, y);
			g.setColor(c.getBackground().darker());
			g.drawLine(0, 0, 0, h - 1);
			g.drawLine(1, 0, w - 1, 0);
			g.setColor(c.getBackground().darker().darker());
			g.drawLine(1, 1, 1, h - 2);
			g.drawLine(2, 1, w - 2, 1);
			g.setColor(c.getBackground().brighter());
			g.drawLine(1, h - 1, w - 1, h - 1);
			g.drawLine(w - 1, 1, w - 1, h - 2);
			g.setColor(c.getBackground().brighter().brighter());
			g.drawLine(2, h - 2, w - 2, h - 2);
			g.drawLine(w - 2, 2, w - 2, h - 3);
			g.translate(-x, -y);
			g.setColor(oldColor);
		}

	}

	public static void main(String[] args) {
		JDialog jd = new JDialog();
		jd.setSize(500, 500);
		MySplashPanel a = new ServerFileSystemMonitorFrame().new MySplashPanel(jd, null);
	}
}
