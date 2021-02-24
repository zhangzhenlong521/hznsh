package cn.com.infostrategy.ui.common;

/**
 * 维度展示
 * 
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.HFlowLayoutPanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;

public class WeiduSearchButton implements WLTActionListener, ActionListener {
	private JPanel scrollPane = null;
	private WLTSplitPane splitp = null;
	private WLTSplitPane splitpleft = null;
	private BillListPanel blp2 = null;
	private BillVO[] billvos = null;
	private WLTTabbedPane tabPanel_choosePeriod = null;
	private String weiduparam = null;
	private String weidunameparam = null;
	private BillListPanel weiduList = null;
	private MyTreeCellRender cellRender = new MyTreeCellRender();;
	private WLTButton btnup = new WLTButton("上移");
	private WLTButton btndown = new WLTButton("下移");
	private WLTButton btnload = new WLTButton("确定");
	private WLTButton btnweiduset = new WLTButton("维度设置");
	private WLTLabel lbweiduset = new WLTLabel("");
	private JTree jtree = null;
	private BillDialog bd = null;
	BillDialog billdconfig = null;

	public void actionPerformed(WLTActionEvent _event) throws Exception {
		// TODO Auto-generated method stub
		BillListPanel blp = null;
		try {
			blp = (BillListPanel) _event.getBillPanelFrom();
		} catch (Exception e) {
			MessageBox.show(blp, "请在列表添加此按钮！");
			return;
		}

		if (blp.getAllBillVOs() == null || blp.getAllBillVOs().length <= 0) {
			MessageBox.show(blp, "没有数据！");
			return;
		}
		weiduparam = blp.getTempletVO().getListweidudesc();
		if (weiduparam == null || weiduparam == "" || weiduparam.length() < 0) {
			MessageBox.show(blp, "没有配置维度！");
			return;
		}
		billvos = (BillVO[]) new TBUtil().deepClone(blp.getAllBillVOs());
		Pub_Templet_1VO vo = (Pub_Templet_1VO) new TBUtil().deepClone(blp.getTempletVO());
		blp2 = new BillListPanel(vo);
		blp2.getBillListBtnPanel().setVisible(false);
		blp2.getQuickQueryPanel().setVisible(false);
		blp2.getToolbarPanel().setVisible(false);
		blp2.setQuickQueryPanelVisiable(false);
		blp2.setItemEditable(false);
		String[] weidus = new TBUtil().split(weiduparam, ";");
		jtree = getJTree(billvos, weiduparam);
		scrollPane = new JPanel();
		scrollPane.setLayout(new BorderLayout());
		lbweiduset.setText("|" + getparamname(blp, weidus));
		scrollPane.add(new HFlowLayoutPanel(new JComponent[] { btnweiduset, lbweiduset }), BorderLayout.NORTH);
		btnweiduset.addActionListener(this);
		scrollPane.add(new JScrollPane(jtree), BorderLayout.CENTER); //
		scrollPane.setOpaque(false); //
		scrollPane.setBorder(BorderFactory.createEmptyBorder()); //
		//expandAll(jtree, true);//是否展开所有
		weiduList = getWeiduList("维度", new String[][] { { "维度名", "100" }, { "是否显示", "80" }, { "维度key", "80" } });
		btnup.addActionListener(this);
		btndown.addActionListener(this);
		//btnload.addActionListener(this);
		weiduList.getBillListBtnPanel().addButton(btnup);
		weiduList.getBillListBtnPanel().addButton(btndown);
		//weiduList.getBillListBtnPanel().addButton(btnload);
		weiduList.repaintBillListButton();
		if (weidus != null && weidus.length > 0) {
			for (int i = 0; i < weidus.length; i++) {
				int li_newrow = weiduList.addEmptyRow(false);
				weiduList.setValueAt(new StringItemVO(getparamname(blp, weidus[i])), li_newrow, "维度名");
				weiduList.setValueAt(new StringItemVO("Y"), li_newrow, "是否显示");
				weiduList.setValueAt(new StringItemVO(weidus[i]), li_newrow, "维度key");
			}
		}
		weiduList.setAllRowStatusAs("INIT");
		splitpleft = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, weiduList, scrollPane);
		splitpleft.setDividerLocation(100);
		splitp = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, blp2);
		splitp.setDividerLocation(300);
		splitp.setDividerSize(10);
		splitp.setOneTouchExpandable(true);
		bd = new BillDialog(blp, "多维展示", 1300, 500);
		bd.add(splitp);
		bd.setVisible(true);
	}

	public BillVO[] getData(BillVO[] billvos, String key, String param) {
		List newbillvos = new ArrayList();
		for (int i = 0; i < billvos.length; i++) {
			if (param.lastIndexOf("=>") < 0) {//->以前是这个标识为但是有时候机构会存这个所以改成这个
				if ("空值".equals(param)) {
					if (billvos[i].getStringViewValue(key) == null || "".equals(billvos[i].getStringViewValue(key))) {
						newbillvos.add(billvos[i]);
					}
				} else {
					if ("日历".equals(getItemType(billvos[i], key)) || "时间".equals(getItemType(billvos[i], key))) {
						if (billvos[i].getStringViewValue(key) != null) {
							if (param.equals(billvos[i].getStringViewValue(key).substring(0, 7))) {
								newbillvos.add(billvos[i]);
							}
						}
					} else {
						if (param.equals(billvos[i].getStringViewValue(key))) {
							newbillvos.add(billvos[i]);
						}
					}
				}

			} else {
				String[] keys = key.split("=>");
				String[] params = param.split("=>");
				String state = "1";
				for (int j = 0; j < keys.length; j++) {
					try {
						if ("空值".equals(params[j])) {
							if (!(billvos[i].getStringViewValue(keys[j]) == null) && !"".equals(billvos[i].getStringViewValue(keys[j]).trim())) {
								state = "2";
								break;
							}
						} else {
							if ("日历".equals(getItemType(billvos[i], keys[j])) || "时间".equals(getItemType(billvos[i], keys[j]))) {
								if (billvos[i].getStringViewValue(keys[j]) != null) {
									if (!params[j].equals(billvos[i].getStringViewValue(keys[j]).substring(0, 7))) {
										state = "2";
										break;
									}
								} else {
									state = "2";
									break;
								}
							} else {
								if (!params[j].equals(billvos[i].getStringViewValue(keys[j]))) {
									state = "2";
									break;
								}
							}
						}
					} catch (Exception e) {
						state = "2";
						e.printStackTrace();
					}
				}
				if ("1".equals(state)) {
					newbillvos.add(billvos[i]);
				}
			}
		}
		if (newbillvos != null) {
			return (BillVO[]) newbillvos.toArray(new BillVO[0]);
		}
		return null;

	}

	public String getAmount(BillVO[] billvos, String key, String param) {
		//BillVO[] newbillvos = new BillVO[billvos.length];
		List newbillvos = new ArrayList();
		for (int i = 0; i < billvos.length; i++) {
			if (param.lastIndexOf("=>") < 0) {
				if ("空值".equals(param)) {
					if (billvos[i].getStringViewValue(key) == null || "".equals(billvos[i].getStringViewValue(key))) {
						newbillvos.add(billvos[i]);
					}
				} else {

					if ("日历".equals(getItemType(billvos[i], key)) || "时间".equals(getItemType(billvos[i], key))) {
						if (billvos[i].getStringViewValue(key) != null) {
							if (param.equals(billvos[i].getStringViewValue(key).substring(0, 7))) {
								newbillvos.add(billvos[i]);
							}
						}
					} else {

						if (param.equals(billvos[i].getStringViewValue(key))) {
							newbillvos.add(billvos[i]);
						}
					}
				}
			} else {
				String[] keys = key.split("=>");
				String[] params = param.split("=>");
				String state = "1";
				for (int j = 0; j < keys.length; j++) {
					try {
						if ("空值".equals(params[j])) {
							if (!(billvos[i].getStringViewValue(keys[j]) == null) && !"".equals(billvos[i].getStringViewValue(keys[j]).trim())) {
								state = "2";
								break;
							}
						} else {
							if ("日历".equals(getItemType(billvos[i], keys[j])) || "时间".equals(getItemType(billvos[i], keys[j]))) {
								if (billvos[i].getStringViewValue(keys[j]) != null) {
									if (!params[j].equals(billvos[i].getStringViewValue(keys[j]).substring(0, 7))) {
										state = "2";
										break;
									}
								} else {
									state = "2";
									break;
								}
							} else {
								if (!params[j].equals(billvos[i].getStringViewValue(keys[j]))) {
									state = "2";
									break;
								}
							}
						}
					} catch (Exception e) {
						state = "2";
						e.printStackTrace();
					}
				}
				if ("1".equals(state)) {
					newbillvos.add(billvos[i]);
				}
			}
		}
		if (newbillvos != null) {
			return String.valueOf(newbillvos.size());
		}
		return "0";

	}

	public JTree loadWeiduTree(BillVO[] billvos, String weidu) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("所有");
		String[] weidus = new TBUtil().split(weidu, ";");
		VectorMap weiduMaps = new VectorMap();
		if (weidus != null && !"".equals(weidus) && weidus.length > 0) {
			try {
				for (int i = 0; i < billvos.length; i++) {
					for (int j = 0; j < weidus.length; j++) {
						StringBuffer weidukey = new StringBuffer();
						StringBuffer valuekey = new StringBuffer();
						for (int k = 0; k <= j; k++) {
							if (billvos[i].getStringViewValue(weidus[k]) != null && !"".equals(billvos[i].getStringViewValue(weidus[k]).trim())) {
								if (k == 0) {

									if ("日历".equals(getItemType(billvos[i], weidus[k])) || "时间".equals(getItemType(billvos[i], weidus[k]))) {
										weidukey.append(billvos[i].getStringViewValue(weidus[k]).substring(0, 7));
									} else {
										if (billvos[i].getStringViewValue(weidus[k]) == null || "".equals(billvos[i].getStringViewValue(weidus[k]).trim()) || "null".equals(billvos[i].getStringViewValue(weidus[k]).trim())) {
											weidukey.append("空值");
										} else {
											weidukey.append(billvos[i].getStringViewValue(weidus[k]));
										}
									}
									valuekey.append(weidus[k]);
								} else {
									if ("日历".equals(getItemType(billvos[i], weidus[k])) || "时间".equals(getItemType(billvos[i], weidus[k]))) {
										weidukey.append("=>" + billvos[i].getStringViewValue(weidus[k]).substring(0, 7));
									} else {
										if (billvos[i].getStringViewValue(weidus[k]) == null || "".equals(billvos[i].getStringViewValue(weidus[k]).trim()) || "null".equals(billvos[i].getStringViewValue(weidus[k]).trim())) {
											weidukey.append("=>空值");
										} else {
											weidukey.append("=>" + billvos[i].getStringViewValue(weidus[k]));
										}
									}
									valuekey.append("=>" + weidus[k]);
								}
							} else {
								if (k == 0) {
									weidukey.append("空值");
									valuekey.append(weidus[k]);
								} else {
									weidukey.append("=>空值");
									valuekey.append("=>" + weidus[k]);
								}
							}
						}
						weiduMaps.put(weidukey.toString(), valuekey.toString());

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (weiduMaps != null && weiduMaps.size() > 0) {
			String[] keys = (String[]) weiduMaps.keySet().toArray(new String[0]);
			new TBUtil().sortStrs(keys);
			DefaultMutableTreeNode[] notes = new DefaultMutableTreeNode[weiduMaps.size()];
			for (int i = 0; i < keys.length; i++) {
				notes[i] = new DefaultMutableTreeNode(new myvo(keys[i], (String) weiduMaps.get(keys[i]), getAmount(billvos, (String) weiduMaps.get(keys[i]), keys[i])));
				root.add(notes[i]);
			}
			HashMap map_parent = new HashMap();
			for (int i = 0; i < notes.length; i++) { //先扫一遍,存储下以主键为key的所有结点,这样找爸爸时才快
				map_parent.put(((myvo) notes[i].getUserObject()).getKey(), notes[i]); //先在哈希表中注册一下,下次找起来才快!!
			}
			for (int i = 0; i < notes.length; i++) {
				myvo nodeVO = (myvo) notes[i].getUserObject();
				String str_pk_parentPK = getPK(nodeVO.getKey()); //父亲主键
				if (str_pk_parentPK == null || str_pk_parentPK.trim().equals("")) {
					continue;
				}
				DefaultMutableTreeNode parentnode = (DefaultMutableTreeNode) map_parent.get(str_pk_parentPK); //
				if (parentnode != null) { //如果找到爸爸了..
					try {
						parentnode.add(notes[i]); //在爸爸下面加入我..
					} catch (Exception ex) {
						root.add(notes[i]);
					}
				}
			}
		}
		JTree jtree = new JTree(new DefaultTreeModel(root));
		return jtree;
	}

	public void expandAll(JTree tree, boolean expand) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		expandAll(tree, new TreePath(root), expand);
	}

	private String getPK(String note) {
		String pk = new String();
		if (note.indexOf("=>") < 0) {
			return note;
		} else {
			return note.substring(0, note.lastIndexOf("=>"));
		}
	}

	private String getVL(String note) {
		String pk = new String();
		if (note.indexOf("=>") < 0) {
			return note;
		} else {
			return note.substring(note.lastIndexOf("=>") + 2, note.length());
		}
	}

	private BillListPanel getWeiduList(String _templetName, String[][] _itemAndWidth) {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", _templetName); //模版编码,请勿随便修改
		vo.setAttributeValue("templetname", _templetName); //模板名称
		vo.setAttributeValue("templetname_e", _templetName); //模板名称
		vo.setAttributeValue("tablename", null); //查询数据的表(视图)名
		vo.setAttributeValue("savedtablename", null); //保存数据的表名
		vo.setAttributeValue("Isshowlistpagebar", "N"); //列表是否显示分页栏
		vo.setAttributeValue("Isshowlistopebar", "N"); //列表是否显示操作按钮栏
		vo.setAttributeValue("listcustpanel", null); //列表自定义面板
		vo.setAttributeValue("cardcustpanel", null); //卡片自定义面板

		HashVO[] itemVOs = new HashVO[_itemAndWidth.length];
		for (int i = 0; i < itemVOs.length; i++) {
			itemVOs[i] = new HashVO(); //
			itemVOs[i].setAttributeValue("itemkey", _itemAndWidth[i][0]); //唯一标识,用于取数与保存
			itemVOs[i].setAttributeValue("itemname", _itemAndWidth[i][0]); //唯一标识,用于取数与保存
			itemVOs[i].setAttributeValue("itemname_e", _itemAndWidth[i][0]); //唯一标识,用于取数与保存
			if ("是否显示".equals(_itemAndWidth[i][0])) {
				itemVOs[i].setAttributeValue("itemtype", "勾选框"); //唯一标识,用于取数与保存
				itemVOs[i].setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			} else {
				itemVOs[i].setAttributeValue("itemtype", "文本框"); //唯一标识,用于取数与保存
				itemVOs[i].setAttributeValue("listiseditable", "4"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			}
			itemVOs[i].setAttributeValue("issave", "N"); //是否参与保存(Y,N)
			itemVOs[i].setAttributeValue("listwidth", getWidth(_itemAndWidth[i][1])); //列表是宽度
			itemVOs[i].setAttributeValue("cardwidth", getWidth(_itemAndWidth[i][1])); //卡片时宽度
			if ("维度key".equals(_itemAndWidth[i][0])) {
				itemVOs[i].setAttributeValue("listisshowable", "N"); //列表时是否显示(Y,N)
			} else {
				itemVOs[i].setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			}
			itemVOs[i].setAttributeValue("cardisshowable", "Y"); //卡片时是否显示(Y,N)
			itemVOs[i].setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVOs[i].setAttributeValue("iswrap", "Y"); //卡片是否换行
		}
		DefaultTMO defTMO = new DefaultTMO(vo, itemVOs);
		BillListPanel billlistpanel = new BillListPanel(defTMO);
		return billlistpanel;
	}

	private String getparamname(BillListPanel billlistpanel, String param) {
		Pub_Templet_1_ItemVO[] ps = billlistpanel.getTempletItemVOs();
		StringBuffer sb = new StringBuffer();
		if (ps != null && ps.length > 0) {
			for (int i = 0; i < ps.length; i++) {
				if (param.equals(ps[i].getItemkey())) {
					return ps[i].getItemname();
				}
			}
		}
		return "";
	}

	private String getparamname(BillListPanel billlistpanel, String[] param) {
		Pub_Templet_1_ItemVO[] ps = billlistpanel.getTempletItemVOs();
		StringBuffer sb = new StringBuffer();
		if (ps != null && ps.length > 0) {
			for (int j = 0; j < param.length; j++) {
				for (int i = 0; i < ps.length; i++) {
					if (param[j].equals(ps[i].getItemkey())) {
						sb.append(ps[i].getItemname() + ";");
						break;
					}
				}
			}
		}
		return sb.toString();
	}

	private String getItemType(BillVO bl, String key) {
		int li_index = findIndex(bl, key);
		if (li_index >= 0) {
			return bl.getItemType()[li_index];
		}
		return null;
	}

	private int findIndex(BillVO bl, String key) {
		for (int i = 0; i < bl.getKeys().length; i++) {
			if (bl.getKeys()[i] != null && bl.getKeys()[i].trim().equalsIgnoreCase(key)) {
				return i;
			}
		}
		return -1;
	}

	private int getWidth(String _s) {
		try {
			return Integer.parseInt(_s);
		} catch (Exception e) {
			e.printStackTrace();
			return 100;
		}
	}

	private JTree getJTree(final BillVO[] billvos, String weiduparam) {
		JTree jtree = loadWeiduTree(billvos, weiduparam);
		jtree.setUI(new WLTTreeUI()); //
		jtree.setOpaque(false); //
		jtree.setRootVisible(true); //
		jtree.setShowsRootHandles(true); //
		jtree.setRowHeight(18); //
		jtree.setCellRenderer(cellRender); //
		jtree.setEnabled(true); //
		jtree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent evt) {
				TreePath[] paths = evt.getPaths(); //取得所有选中的
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[0].getLastPathComponent();
				/*JTree tree = (JTree) evt.getSource(); //
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();*/
				if (node != null && !node.isRoot()) {
					blp2.clearTable();
					if (node.getUserObject() != null) {
						myvo param = (myvo) node.getUserObject();
						BillVO[] newb = getData(billvos, param.getValue(), param.getKey());
						if (newb != null && newb.length > 0)
							for (int i = 0; i < newb.length; i++) {
								blp2.addRow(newb[i]);
							}
					}
				} else {
					blp2.clearTable();
					for (int i = 0; i < billvos.length; i++) {
						blp2.addRow(billvos[i]);
					}
				}
				blp2.setAllRowStatusAs("INIT");
			}
		});
		return jtree;
	}

	private void updateTree(String param) {
		JTree jtreenew = getJTree(billvos, param.toString());
		jtreenew.setUI(new WLTTreeUI()); //
		jtreenew.setOpaque(false); //
		jtreenew.setRootVisible(true); //
		jtreenew.setShowsRootHandles(true); //
		jtreenew.setRowHeight(18); //
		jtreenew.setCellRenderer(cellRender); //
		jtreenew.setEnabled(true); //
		scrollPane.removeAll();
		lbweiduset.setText("|" + getSelectName());
		scrollPane.add(new HFlowLayoutPanel(new JComponent[] { btnweiduset, lbweiduset }), BorderLayout.NORTH);
		btnweiduset.addActionListener(this);
		scrollPane.add(new JScrollPane(jtreenew), BorderLayout.CENTER);
		scrollPane.updateUI();
	}

	private AbstractTMO getTMO(HashVO[] hvos) {
		HashVO parentVO = new HashVO(); //
		parentVO.setAttributeValue("templetcode", "Test"); // 模版编码,请勿随便修改
		AbstractTMO tmo = new DefaultTMO(parentVO, hvos); // 创建元原模板数据
		return tmo;
	}

	private void expandAll(JTree tree, TreePath parent, boolean expand) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(tree, path, expand);
			}
		}
		if (expand) {
			tree.expandPath(parent);
		} else {
			if (!node.isRoot()) {
				tree.collapsePath(parent);
			}
		}
	}

	class myvo {
		private String key = null;//实际值
		private String value = null;//维度字段
		private String amount = null;//数量

		public myvo(String key, String value, String amount) {
			this.key = key;
			this.value = value;
			this.amount = amount;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String toString() {
			return getVL(this.key) + "[" + this.amount + "]";
		}
	}

	class MyTreeCellRender extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = 1150121537427225362L;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			JLabel oldlabel = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus); //
			JLabel label = null;
			label = new JLabel(oldlabel.getText()); //
			label.setFont(oldlabel.getFont()); //
			label.setIcon(oldlabel.getIcon()); //           
			DefaultMutableTreeNode thisNode = (DefaultMutableTreeNode) value; //
			if (!thisNode.isRoot()) {
				if (sel) {
					label.setOpaque(true); //
					label.setBackground(Color.YELLOW); //
					label.setForeground(Color.RED); //
				} else {
					label.setOpaque(false); //
				}
			}

			return label; //
		}
	}

	public String getSelectName() {
		BillVO[] params = weiduList.getAllBillVOs();
		StringBuffer sbs = new StringBuffer();
		if (params != null && params.length > 0) {
			for (int i = 0; i < params.length; i++) {
				if ("Y".equals(params[i].getStringViewValue("是否显示")))
					sbs.append(params[i].getStringViewValue("维度名") + ";");
			}
		}
		return sbs.toString();
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object obj = e.getSource();

		if (obj == (btnup)) {
			weiduList.moveUpRow();
			/*BillVO[] params = weiduList.getAllBillVOs();
			StringBuffer sbs = new StringBuffer();
			if (params != null && params.length > 0) {
				for (int i = 0; i < params.length; i++) {
					if ("Y".equals(params[i].getStringViewValue("是否显示")))
						sbs.append(params[i].getStringViewValue("维度key") + ";");
				}
				//updateTree(sbs.toString());
			}*/
		}
		if (obj == (btndown)) {
			weiduList.moveDownRow();
			/*BillVO[] params = weiduList.getAllBillVOs();
			StringBuffer sbs = new StringBuffer();
			if (params != null && params.length > 0) {
				for (int i = 0; i < params.length; i++) {
					if ("Y".equals(params[i].getStringViewValue("是否显示")))
						sbs.append(params[i].getStringViewValue("维度key") + ";");
				}*/
			//updateTree(sbs.toString());
			//}
		}
		if (obj == (btnload)) {
			BillVO[] params = weiduList.getAllBillVOs();
			StringBuffer sbs = new StringBuffer();
			if (params != null && params.length > 0) {
				for (int i = 0; i < params.length; i++) {
					if ("Y".equals(params[i].getStringViewValue("是否显示")))
						sbs.append(params[i].getStringViewValue("维度key") + ";");
				}
				updateTree(sbs.toString());
				if (billdconfig != null) {
					billdconfig.dispose();
				}
			}
		}
		if (obj == (btnweiduset)) {
			billdconfig = new BillDialog(bd, "维度设置", 300, 300);
			billdconfig.add(weiduList, BorderLayout.CENTER);
			billdconfig.add(getSouthPanel(), BorderLayout.SOUTH);
			billdconfig.setVisible(true);
		}

	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		btnload.addActionListener(this); //
		panel.add(btnload); //

		return panel;
	}
}
