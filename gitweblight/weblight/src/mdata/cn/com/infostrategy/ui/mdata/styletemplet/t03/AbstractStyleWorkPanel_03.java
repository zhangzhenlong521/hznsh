/**************************************************************************
 * $RCSfile: AbstractStyleWorkPanel_03.java,v $  $Revision: 1.18 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/

package cn.com.infostrategy.ui.mdata.styletemplet.t03;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.tree.DefaultMutableTreeNode;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.BillTreeNodeVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillTreeMoveEvent;
import cn.com.infostrategy.ui.mdata.BillTreeMoveListener;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

/**
 * 模板3 左边树，右边卡。。。右边卡中的纪录为树结点的信息。对树的结点进行操作。 模板3-2
 * 左边树，右边卡。右边卡中的纪录为根据树找到的另外一张表的一条纪录，此表与树相关联。
 */

/**
 * 风格模板3抽象类实现!!
 * 主要逻辑基本上都在这里实现!!
 * @author xch
 *
 */
public abstract class AbstractStyleWorkPanel_03 extends AbstractWorkPanel implements BillTreeSelectListener, BillTreeMoveListener {

	private BillTreePanel billTreePanel = null; //树型面板
	private BillCardPanel billCardPanel = null; //卡片面板

	private WLTButton btn_tree_insert = null;
	private WLTButton btn_tree_edit = null;
	private WLTButton btn_tree_delete = null;
	private WLTButton btn_tree_refresh = null;

	private boolean bo_ifneedrefresh = true; //
	private ActionListener buttonActionListener = null;
	private JMenuItem item_insert, item_delete, item_edit; //
	private ActionListener appPopMenuAction = null;

	private IUIIntercept_03 uiIntercept = null;

	public abstract String getTempletcode(); //模板编码

	public void initialize() {
		try {
			this.setLayout(new BorderLayout()); //
			billTreePanel = new BillTreePanel(getTempletcode()); // formatContentPanel.getBillTreePanel();
			billCardPanel = new BillCardPanel(getTempletcode()); //formatContentPanel.getBillCardPanel(); //

			billTreePanel.reSetTreeChecked(false); //不带勾选框
			item_insert = new JMenuItem("新增", UIUtil.getImage("insert.gif")); //新增
			item_delete = new JMenuItem("删除", UIUtil.getImage("del.gif")); //删除
			item_edit = new JMenuItem("编辑", UIUtil.getImage("modify.gif")); //编辑

			appPopMenuAction = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						if (e.getSource() == item_insert) {
							onInsert(); //
						} else if (e.getSource() == item_delete) {
							onDelete(); //
						} else if (e.getSource() == item_edit) {
							onEdit(); //
						}
					} catch (Exception ex) {
						MessageBox.showException(AbstractStyleWorkPanel_03.this, ex);
					}
				}
			};

			item_insert.addActionListener(appPopMenuAction);
			item_delete.addActionListener(appPopMenuAction);
			item_edit.addActionListener(appPopMenuAction);

			billTreePanel.setAppMenuItems(new JMenuItem[] { item_insert, item_edit, item_delete }); //
			buttonActionListener = new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					try {
						if (e.getSource() == btn_tree_insert) {
							onInsert();
						} else if (e.getSource() == btn_tree_edit) {
							onEdit();
						} else if (e.getSource() == btn_tree_delete) {
							onDelete();
						} else if (e.getSource() == btn_tree_refresh) {
							onRefresh();
						}
					} catch (Exception ex) {
						MessageBox.showException(AbstractStyleWorkPanel_03.this, ex);
					}
				}
			};

			btn_tree_insert = new WLTButton(WLTConstants.BUTTON_TEXT_INSERT);
			btn_tree_edit = new WLTButton(WLTConstants.BUTTON_TEXT_EDIT);
			btn_tree_delete = new WLTButton(WLTConstants.BUTTON_TEXT_DELETE);
			btn_tree_refresh = new WLTButton(WLTConstants.BUTTON_TEXT_REFRESH);
			btn_tree_insert.addActionListener(buttonActionListener);
			btn_tree_edit.addActionListener(buttonActionListener);
			btn_tree_delete.addActionListener(buttonActionListener);
			btn_tree_refresh.addActionListener(buttonActionListener);
			billTreePanel.insertBatchBillTreeButton(new WLTButton[] { btn_tree_insert, btn_tree_edit, btn_tree_delete, btn_tree_refresh }); //
			billTreePanel.repaintBillTreeButton(); //
			billTreePanel.setDragable(true);
			billTreePanel.queryDataByCondition("1=1 "); //打开时立即查出所有数据..

			billTreePanel.addBillTreeSelectListener(this); //
			billTreePanel.addBillTreeMovedListener(this); //

			billCardPanel.setEditable(false); //

			WLTSplitPane splitPane = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, billTreePanel, billCardPanel); //
			splitPane.setDividerLocation(getSplitLocation()); //
			this.add(splitPane, BorderLayout.CENTER); //
			initUIIntercept(); //初始化客户端拦截器类!!!
			afterInitialize(); //初始化结束后所要做的.
		} catch (Exception ex) {
			ex.printStackTrace(); //
			MessageBox.showException(this, ex); //
		}
	}

	private void initUIIntercept() {
		try {
			String str_UIIntercept = getMenuConfMapValueAsStr("$UIIntercept"); //
			if (str_UIIntercept != null && !str_UIIntercept.trim().equals("")) { //如果不为空!
				uiIntercept = (IUIIntercept_03) Class.forName(str_UIIntercept).newInstance(); //
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 初始化结束后要做的事,可以被子类覆盖..
	 */
	public void afterInitialize() throws Exception {
		if (this.uiIntercept != null) {
			uiIntercept.afterInitialize(this); //
		}
	}

	/**
	 * 取得树型面板..
	 * @return
	 */
	public BillTreePanel getBillTreePanel() {
		return billTreePanel;
	}

	/**
	 * 取得卡片面板..
	 * @return
	 */
	public BillCardPanel getBillCardPanel() {
		return billCardPanel;
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		if (!bo_ifneedrefresh) {
			return;
		}

		if (!_event.getCurrSelectedNode().isRoot()) {
			BillVO billVO = _event.getCurrSelectedVO();
			String str_id = billVO.getPkValue();
			getBillCardPanel().queryDataByCondition(billTreePanel.getTempletVO().getTreepk() + " = '" + str_id + "'"); //改为从模板中取主键
		} else {
			getBillCardPanel().refreshData("1=2");//根结点没有值，应该清空cardpanel！
		}
	}

	public void onInsert() {
		try {
			if (getBillTreePanel().getSelectedPath() == null) {
				MessageBox.show(this, "请选择一个父亲结点进行新增操作!"); //
				return; //如果没有选择一个结点则直接返回
			}

			BillVO billVO = getBillTreePanel().getSelectedVO();
			BillCardPanel insertCardPanel = new BillCardPanel(billTreePanel.getTempletVO()); //

			if (billVO != null) { //如果选中的不是根结点
				insertCardPanel.insertRow(); //
				insertCardPanel.setEditableByInsertInit(); //
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) getBillTreePanel().getSelectedNode();
				if (!parentNode.isRoot()) { //如果不是根结点,则要设置parentid
					BillVO parentVO = getBillTreePanel().getBillVOFromNode(parentNode); //
					String parent_id = ((StringItemVO) parentVO.getObject(billTreePanel.getTempletVO().getTreepk())).getStringValue(); //
					insertCardPanel.setCompentObjectValue(billTreePanel.getTempletVO().getTreeparentpk(), new StringItemVO(parent_id)); //设置父亲字段
					BillVO[] parentVOs = getBillTreePanel().getSelectedParentPathVOs(); //
					StringBuilder sb_ids = new StringBuilder();
					for (int i = 0; i < parentVOs.length; i++) {
						sb_ids.append(parentVOs[i].getStringValue(billTreePanel.getTempletVO().getTreepk()) + ";"); //
					}
					//临时写死是blparentcorpids,以后要改成可配置的参数!!
					insertCardPanel.setCompentObjectValue("blparentcorpids", new StringItemVO(";" + sb_ids.toString() + insertCardPanel.getValueAt(billTreePanel.getTempletVO().getTreepk()) + ";")); //设置父亲字段
				} else {
					insertCardPanel.setCompentObjectValue("blparentcorpids", new StringItemVO(";" + insertCardPanel.getValueAt(billTreePanel.getTempletVO().getTreepk()) + ";")); //设置父亲字段
				}
			} else { //如果选中的是根结点
				insertCardPanel.insertRow(); //
				insertCardPanel.setEditableByInsertInit(); //
				insertCardPanel.setCompentObjectValue("blparentcorpids", new StringItemVO(insertCardPanel.getValueAt(billTreePanel.getTempletVO().getTreepk()) + ";")); //设置父亲字段
			}

			BillCardDialog dialog = new BillCardDialog(this, "新增", insertCardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); //
			if (uiIntercept != null)
				uiIntercept.actionAfterInsert(insertCardPanel);
			dialog.setVisible(true); //
			if (dialog.getCloseType() == BillDialog.CONFIRM) {
				BillVO newVO = insertCardPanel.getBillVO(); //
				newVO.setToStringFieldName(billTreePanel.getTempletVO().getTreeviewfield()); //
				bo_ifneedrefresh = false;
				billTreePanel.addNode(newVO); //
				getBillCardPanel().setBillVO(newVO); //
				bo_ifneedrefresh = true;

				if (this.uiIntercept != null) {
					uiIntercept.dealCommitAfterInsert(this, newVO); // 新增提交后处理
				}
			}
		} catch (Exception e) {
			MessageBox.showException(this, e); //
		}
	}

	protected void onEdit() throws Exception {
		try {
			if (getBillTreePanel().getSelectedPath() == null) {
				MessageBox.show(this, "请选择一个结点进行修改操作!"); //
				return; //如果没有选择一个结点则直接返回
			}

			BillVO billVO = getBillTreePanel().getSelectedVO();
			BillCardPanel editCardPanel = new BillCardPanel(billTreePanel.getTempletVO()); //

			if (billVO != null) { //如果选中的不是根结点
				editCardPanel.queryDataByCondition("id='" + billVO.getPkValue() + "'"); //
				editCardPanel.setEditableByEditInit(); //
			} else { //如果选中的是根结点
				MessageBox.show(this, "根结点不可以编辑!"); //
				return; //如果没有选择一个结点则直接返回
			}

			BillCardDialog dialog = new BillCardDialog(this, "编辑", editCardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE); //
			if (uiIntercept != null)
				uiIntercept.actionAfterUpdate(editCardPanel, "");
			dialog.setVisible(true); //
			if (dialog.getCloseType() == BillDialog.CONFIRM) {
				BillVO newVO = editCardPanel.getBillVO(); //
				newVO.setToStringFieldName(billTreePanel.getTempletVO().getTreeviewfield()); //
				bo_ifneedrefresh = false; //
				getBillTreePanel().setBillVOForCurrSelNode(newVO); //向树中回写数据
				getBillCardPanel().setBillVO(newVO); //
				bo_ifneedrefresh = true; //
				getBillTreePanel().updateUI(); //
			}

		} catch (Exception e) {
			MessageBox.showException(this, e); //
		}
	}

	public void onDelete() {
		BillTreeNodeVO treeNodeVO = getBillTreePanel().getSelectedTreeNodeVO();
		BillVO billVO = getBillTreePanel().getSelectedVO();
		if (billVO == null) {
			MessageBox.show(this, "请选择一个结点进行删除操作!"); //
			return;
		}

		if (billVO != null) {
			BillVO[] childVOs = getBillTreePanel().getSelectedChildPathBillVOs(); //取得所有选中的
			if (JOptionPane.showConfirmDialog(this, "您确定要删除记录【" + treeNodeVO.toString() + "】吗?\r\n这将一并删除其下共【" + childVOs.length + "】条子孙记录,请务必谨慎操作!", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}
			try {
				if (uiIntercept != null) {
					uiIntercept.actionBeforeDelete(getBillCardPanel());
				}
				Vector v_sqls = new Vector(); //菜单删除
				for (int i = 0; i < childVOs.length; i++) {
					v_sqls.add("delete from " + childVOs[i].getSaveTableName() + " where " + childVOs[i].getPkName() + "='" + childVOs[i].getPkValue() + "'"); //
				}
				UIUtil.executeBatchByDS(null, v_sqls); //执行数据库操作!!
				getBillTreePanel().delCurrNode(); //
				getBillTreePanel().getJTree().repaint(); //
				getBillCardPanel().clear();
				if (billTreePanel.getSelectedVO() != null) {
					billCardPanel.setValueAt("linkcode", billTreePanel.getSelectedVO().getObject("linkcode")); //
				}
			} catch (Exception ex) {
				MessageBox.showException(AbstractStyleWorkPanel_03.this, ex); //
			}
		}
	}

	public void onRefresh() {
		long ll_1 = System.currentTimeMillis();
		getBillTreePanel().queryDataByCondition("1=1");//刷新数据，这里不能直接用refreshTree(),因为上次查询的sql中很可能不包括新增数据的id，故需要全查。【李春娟/2013-07-30】
		long ll_2 = System.currentTimeMillis();
		MessageBox.show(this, "刷新数据成功!共耗时[" + (ll_2 - ll_1) + "]毫秒"); //
	}

	//	/**
	//	 * 保存数据!!
	//	 *
	//	 */
	//	public void onSave() {
	//		try {
	//			if (getBillCardPanel().getEditState() == WLTConstants.BILLDATAEDITSTATE_INSERT) {
	//				String str_pkvalue = getBillCardPanel().getCompentRealValue(billTreePanel.getTempletVO().getTreepk()); //
	//				getBillCardPanel().updateData(); //保存数据!!
	//				FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) RemoteServiceFactory.getInstance().lookUpService(FrameWorkMetaDataServiceIfc.class);
	//				BillVO[] vos = service.getBillVOsByDS(null, "select * from " + getBillTreePanel().getTempletVO().getTablename() + " where " + billTreePanel.getTempletVO().getTreepk() + "='" + str_pkvalue + "'", billTreePanel.getTempletVO()); // 
	//
	//				bo_ifneedrefresh = false;
	//				billTreePanel.addNode(vos[0]); //
	//				bo_ifneedrefresh = true;
	//
	//				setInformation("保存菜单数据成功!!");
	//			} else if (getBillCardPanel().getEditState() == WLTConstants.BILLDATAEDITSTATE_UPDATE) {
	//				getBillCardPanel().updateData();
	//				billTreePanel.getSelectedVO().setObject(billTreePanel.getTempletVO().getTreeviewfield(), getBillCardPanel().getBillVO().getObject(billTreePanel.getTempletVO().getTreeviewfield())); //
	//				setInformation("保存菜单数据成功!!");
	//			}
	//
	//			billTreePanel.getJTree().updateUI(); //
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		} //
	//
	//		getBillTreePanel().resetAllLinkCode(); //
	//		if (billTreePanel.getSelectedVO() != null) {
	//			//billCardPanel.setValueAt("linkcode", billTreePanel.getSelectedVO().getObject("linkcode")); //
	//		}
	//	}

	/**
	 * 
	 */
	public void onBillTreeNodeMoved(BillTreeMoveEvent _event) {
		if (billTreePanel.getSelectedNode() != null && !billTreePanel.getSelectedNode().isRoot()) {
			BillVO billVO = billTreePanel.getSelectedVO();
			String str_id = billVO.getPkValue();
			getBillCardPanel().queryDataByCondition(billTreePanel.getTempletVO().getTreepk() + " = '" + str_id + "'"); //改为从模板中取主键
		} else {
			getBillCardPanel().clear(); //
		}
	}

	/**
	 * 分隔条的位置,为了可以
	 * @return
	 */
	public int getSplitLocation() {
		try {
			String str_location = this.getMenuConfMapValueAsStr("SplitLocation"); //看是否公式定义了
			if (str_location != null && !str_location.trim().equals("")) {
				return Integer.parseInt(str_location);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 235;
	}

}
