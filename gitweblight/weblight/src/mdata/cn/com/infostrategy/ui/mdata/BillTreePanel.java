/**************************************************************************
 * $RCSfile: BillTreePanel.java,v $  $Revision: 1.72 $  $Date: 2013/02/28 06:14:42 $
 **************************************************************************/

package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillTreeDefaultMutableTreeNode;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.RowNumberItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.to.mdata.templetvo.BillTreeNodeVO;
import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.ImageIconFactory;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.RemoteServiceFactory;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTScrollPanel;
import cn.com.infostrategy.ui.common.WLTTextArea;
import cn.com.infostrategy.ui.mdata.treecomp.BillTreeCheckCellEditor;
import cn.com.infostrategy.ui.mdata.treecomp.BillTreeCheckCellRender;
import cn.com.infostrategy.ui.mdata.treecomp.QuickFilterDialog;

/**
 * 树型面板
 * @author xch
 *
 */
public class BillTreePanel extends BillPanel implements ActionListener {

	private static final long serialVersionUID = 8051676743971788439L;

	private Pub_Templet_1VO templetVO = null; // 元原模板主表
	private Pub_Templet_1_ItemVO[] templetItemVOs = null; //元原模板子表

	private TableDataStruct realStrData = null; //实际存储的数据,为了提高性能,只返回字符串,然后在客户端根据需要随时创建需要的BillVO,
	//以前是先创建BillVO[],然后每个结点存储的UserObject都是BillVO,从API封装上看是正确的,但从性能上看却在数据量大到一定程度时不能接收,比如同时打开两个机构树时,就使内存达到50多M,直接导致MemoeryOut!!就是客户端不能同时加载多个太多数据!!

	private HashMap mapModelIndex = new HashMap(); //用来记录实际数据中某一列的实际位置,否则每一次都去找一次必须很慢

	private String[] iconNames = null;
	private String leaficonNames = null;
	private TBUtil tBUtil = null; //转换工具!!

	private DefaultMutableTreeNode rootNode = null;
	private JTree jTree = null;

	private JPanel btnPanel = null; //按钮面板
	private BillButtonPanel billTreeBtnPanel; //按钮面板
	private JPanel quickLocatePanel = null; //按钮面板

	private JButton btn_moveup, btn_movedown; //

	private JTextField textField_quickLocate; //快速定位
	private DefaultMutableTreeNode currQuickLocateNode = null; // 

	private JButton btn_quickLocate; //快速查询
	private JButton btn_MoveToNextQuickLocateNode, btn_MoveToPreviousQuickLocateNode; //快速查询时上移与下移的按钮..

	private JScrollPane mainScrollPane = null;
	private JTextField text_dirpath; //
	private JLabel label_helpinfo = null; // 

	private JPopupMenu popmenu_header = null; //右键弹出框

	private String custCondition = null; //
	private String str_realsql = null;
	private String str_resAccessInfo = null; //数据权限过滤说明
	private String str_resAccessSQL = null; //数据权限过滤的实际SQL

	private Vector v_BillTreeSelectListeners = new Vector();
	private Vector v_BillTreeMovedListeners = new Vector(); //移动结点时触发的事件存储器
	private ArrayList al_BillTreebeBorePopMenuListeners = new ArrayList();

	private JMenuItem item_quickqueryall, item_refresh, item_expand, item_collapse, item_modifytemplet, item_showDirTree, item_updatelinkcode, item_updatelinkname, item_updateParentid, item_checkOneField, item_quickLocate, item_showSQL, item_computechildnodecount, item_addNode, item_delNode;
	private ArrayList al_appMenuItems = new ArrayList(); //右键二次开发应用菜单

	private boolean dragable = false; //
	private BillFormatPanel loaderBillFormatPanel = null;

	private boolean isChecked = false;
	private BillTreeCheckCellRender cellRender = null;
	private BillTreeCheckCellEditor cellEditor = null; //

	private Vector v_billTreeCheckListener = new Vector(); //
	private JPanel toolKitBtnPanel = null;

	private boolean isDefaultLinkedCheck = true; //是否默认关联勾选!!
	private boolean isTriggerSelectChangeEvent = true; //是否触发选择事件?

	private HashSet virtualCorpIdsHst = new HashSet(); //数据权限过滤时,用于存储虚拟机构结点的对象!

	private Logger logger = WLTLogger.getLogger(BillTreePanel.class); //

	private BillTreePanel() {
	}

	public BillTreePanel(String _templetcode) {
		if (_templetcode.indexOf(".") > 0) { //如果是个类名,即编码中有个".",我们则认为是个类名!!则直接反射调用
			try {
				init_2((AbstractTMO) Class.forName(_templetcode).newInstance());
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}
		} else {
			init_1(_templetcode); //
		}
	}

	/**
	 * 直接由Pub_Templet_1VO创建树型面板，适用于Pub_Templet_1VO已在客户端获得的情况，这样就少一次远程调用【李春娟/2012-02-24】
	 * @param _templetvo
	 */
	public BillTreePanel(Pub_Templet_1VO _templetvo) {
		try {
			templetVO = _templetvo; // 取得元原模板主表
			templetItemVOs = templetVO.getItemVos(); //各项..
			initialize(); // 初始化!!
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BillTreePanel(AbstractTMO _templetVO) {
		init_2(_templetVO);
	}

	/**
	 * 直接通过ServerTMO创建..
	 * @param _serverTMO
	 */
	public BillTreePanel(ServerTMODefine _serverTMO) {
		try {
			templetVO = UIUtil.getPub_Templet_1VO(_serverTMO); //
			templetItemVOs = templetVO.getItemVos(); //各项..
			initialize(); // 初始化!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	public void init_1(String _templetcode) {
		try {
			templetVO = UIUtil.getPub_Templet_1VO(_templetcode); // 取得元原模板主表
			templetItemVOs = templetVO.getItemVos(); // 各项.....
			initialize(); // 初始化!!
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init_2(AbstractTMO _templetVO) {
		try {
			templetVO = UIUtil.getPub_Templet_1VO(_templetVO); // 取得元原模板主表
			templetItemVOs = templetVO.getItemVos(); //各项..
			initialize(); // 初始化!!
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//构造页面!!!
	private final void initialize() {
		setDragable(false); //默认不能拖动,否则非常危险!!!
		isChecked = this.templetVO.getTreeIsChecked().booleanValue(); //
		this.removeAll(); //
		this.setLayout(new BorderLayout());
		JPanel contentPanel = WLTPanel.createDefaultPanel(new BorderLayout()); //
		contentPanel.add(getBtnPanel(), BorderLayout.NORTH); //
		contentPanel.add(getMainScrollPane(), BorderLayout.CENTER); //
		contentPanel.add(getSouthPanel(), BorderLayout.SOUTH); //
		this.add(contentPanel); //
		this.repaint(); //
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new BorderLayout(2, 1)); //WLTPanel.createDefaultPanel(new BorderLayout(2,1), WLTPanel.HORIZONTAL_FROM_MIDDLE); //
		panel.setOpaque(false); //
		text_dirpath = new JTextField("直接路径:[]"); //
		text_dirpath.setForeground(new Color(0, 128, 192)); //
		text_dirpath.setHorizontalAlignment(JTextField.LEFT); //
		text_dirpath.setBorder(BorderFactory.createEmptyBorder()); //
		text_dirpath.setEditable(false); //
		text_dirpath.setOpaque(false); //
		text_dirpath.setVisible(false); //默认不显示!
		panel.add(text_dirpath, BorderLayout.NORTH); //

		String str_helpinfo = "提示:选择某一节点,点击右键有:结果中查找,全部展开/收缩..等功能,请知悉!";
		label_helpinfo = new JLabel(str_helpinfo); //WLTLabel(str_helpinfo, BackGroundDrawingUtil.HORIZONTAL_FROM_MIDDLE); //
		label_helpinfo.setToolTipText(str_helpinfo); //
		label_helpinfo.setBorder(BorderFactory.createEmptyBorder()); //
		label_helpinfo.setForeground(new Color(0, 128, 192)); //
		label_helpinfo.setVisible(false); //默认不显示!
		panel.add(label_helpinfo, BorderLayout.SOUTH); //

		return panel; //
	}

	public void setHelpInfoVisiable(boolean _isVisiable) {
		label_helpinfo.setVisible(_isVisiable); //
	}

	/**
	 * 
	 * @return
	 */
	public JPanel getBtnPanel() {
		JPanel panel_north = WLTPanel.createDefaultPanel(new BorderLayout()); //
		panel_north.add(getBillTreeBtnPanel(), BorderLayout.NORTH); //
		panel_north.add(getQuickLocatePanel(), BorderLayout.SOUTH); //
		return panel_north; //
	}

	/**
	 * 快速查询面板
	 * @return
	 */
	public JPanel getQuickLocatePanel() {
		if (quickLocatePanel != null) {
			return quickLocatePanel; //
		}

		toolKitBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 2)); //
		toolKitBtnPanel.setOpaque(false); //
		if (this.templetVO.getTreeisshowtoolbar()) { //如果显示工具条 sunfujun/将关键字搜索放到工具条中
			JLabel label_locate = new JLabel("关键字", SwingConstants.RIGHT); //
			label_locate.setPreferredSize(new Dimension(57 + LookAndFeel.getFONT_REVISE_SIZE() * 3, 20)); //

			textField_quickLocate = new JTextField(); //
			textField_quickLocate.setPreferredSize(new Dimension(45, 20)); //
			//		textField_quickLocate.setBorder(BorderFactory.createLineBorder(new Color(175, 175, 175), 1)); //

			btn_quickLocate = new WLTButton("搜索"); //
			btn_quickLocate.setPreferredSize(new Dimension(30 + LookAndFeel.getFONT_REVISE_SIZE() * 2, 20)); //
			btn_quickLocate.setFocusable(false); //
			btn_quickLocate.addActionListener(this); //

			JPanel panel_up_down = new JPanel(new BorderLayout(0, 0)); //
			panel_up_down.setOpaque(false); //
			btn_MoveToPreviousQuickLocateNode = new JButton(); //
			btn_MoveToNextQuickLocateNode = new JButton(); //
			btn_MoveToPreviousQuickLocateNode.setIcon(ImageIconFactory.getUpEntityArrowIcon(new Color(120, 120, 120)));
			btn_MoveToNextQuickLocateNode.setIcon(ImageIconFactory.getDownEntityArrowIcon(new Color(120, 120, 120))); //

			//		btn_MoveToPreviousQuickLocateNode.setMargin(new Insets(0, 0, 0, 0)); //
			//		btn_MoveToNextQuickLocateNode.setMargin(new Insets(0, 0, 0, 0)); //

			btn_MoveToPreviousQuickLocateNode.setToolTipText("继续搜索上一个"); ////
			btn_MoveToNextQuickLocateNode.setToolTipText("继续搜索下一个"); ////

			btn_MoveToPreviousQuickLocateNode.addActionListener(this); //
			btn_MoveToNextQuickLocateNode.addActionListener(this); //

			btn_MoveToPreviousQuickLocateNode.setPreferredSize(new Dimension(16, 10));
			btn_MoveToNextQuickLocateNode.setPreferredSize(new Dimension(16, 10));

			btn_MoveToPreviousQuickLocateNode.setBackground(LookAndFeel.btnbgcolor); //
			btn_MoveToNextQuickLocateNode.setBackground(LookAndFeel.btnbgcolor); //

			//		btn_MoveToPreviousQuickLocateNode.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY)); // 
			//		btn_MoveToNextQuickLocateNode.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.GRAY)); // 

			panel_up_down.add(btn_MoveToPreviousQuickLocateNode, BorderLayout.NORTH); //
			panel_up_down.add(btn_MoveToNextQuickLocateNode, BorderLayout.SOUTH); //

			panel_up_down.setPreferredSize(new Dimension(16, 20)); //

			toolKitBtnPanel.add(label_locate); //
			toolKitBtnPanel.add(textField_quickLocate); //
			toolKitBtnPanel.add(btn_quickLocate); //
			toolKitBtnPanel.add(panel_up_down); //

			//上移/下移两个按钮

			btn_moveup = new WLTButton(UIUtil.getImage("up1.gif")); //
			btn_movedown = new WLTButton(UIUtil.getImage("down1.gif")); //
			btn_moveup.setPreferredSize(new Dimension(20, 20)); //
			btn_movedown.setPreferredSize(new Dimension(20, 20)); //
			btn_moveup.setToolTipText("上移顺序"); //
			btn_movedown.setToolTipText("下移顺序"); //
			btn_moveup.addActionListener(this); //
			btn_movedown.addActionListener(this); //

			toolKitBtnPanel.add(btn_moveup); //
			toolKitBtnPanel.add(btn_movedown); //
		}

		quickLocatePanel = new WLTScrollPanel(toolKitBtnPanel); //
		quickLocatePanel.setOpaque(false); //
		return quickLocatePanel;
	}

	/**
	 * 按钮栏,至关重要,即可以存放自定义风格模板中写死的按钮,也可以存储模板中定义的按钮!!
	 * 以后所有的业务逻辑都靠这里面的按钮来实现!! 
	 * @return
	 */
	public BillButtonPanel getBillTreeBtnPanel() {
		if (billTreeBtnPanel != null) {
			return billTreeBtnPanel;
		}

		billTreeBtnPanel = new BillButtonPanel(this.templetVO.getTreecustbtns(), this);
		billTreeBtnPanel.setOpaque(false); //
		billTreeBtnPanel.paintButton(); //绘制,必须要调用一下,否则按钮出不来!!
		return billTreeBtnPanel;
	}

	public void setMoveUpDownBtnVisiable(boolean _visiable) {
		if (btn_moveup != null) {
			btn_moveup.setVisible(_visiable); //
		}
		if (btn_movedown != null) {
			btn_movedown.setVisible(_visiable); //
		}
	}

	/**
	 * 增加一个按钮.
	 * @param _btn
	 */
	public void addBillTreeButton(WLTButton _btn) {
		_btn.setBillPanelFrom(this); //
		getBillTreeBtnPanel().addButton(_btn); //
	}

	/**
	 * 增加一批按钮
	 * @param _btns
	 */
	public void addBatchBillTreeButton(WLTButton[] _btns) {
		for (int i = 0; i < _btns.length; i++) {
			_btns[i].setBillPanelFrom(this); //
		}
		getBillTreeBtnPanel().addBatchButton(_btns);
	}

	/**
	 * 增加一个按钮.
	 * @param _btn
	 */
	public void insertBillTreeButton(WLTButton _btn) {
		_btn.setBillPanelFrom(this); //
		getBillTreeBtnPanel().insertButton(_btn); //
	}

	/**
	 * 插入一批按钮.
	 * @param _btn
	 */
	public void insertBatchBillTreeButton(WLTButton[] _btns) {
		for (int i = 0; i < _btns.length; i++) {
			_btns[i].setBillPanelFrom(this); //
		}
		getBillTreeBtnPanel().insertBatchButton(_btns); //
	}

	/**
	 * 重新绘制按钮
	 */
	public void repaintBillTreeButton() {
		getBillTreeBtnPanel().paintButton(); //
	}

	public void setBillTreeBtnVisiable(String _text, boolean _visiable) {
		getBillTreeBtnPanel().setBillListBtnVisiable(_text, _visiable); //
	}

	public void setAllBillTreeBtnVisiable(boolean _visiable) {
		getBillTreeBtnPanel().setAllBillListBtnVisiable(_visiable); //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_moveup) {
			moveUpNode(); //上移结点
		} else if (e.getSource() == btn_movedown) {
			moveDownNode(); //下移结点
		} else if (e.getSource() == btn_quickLocate) { //快速定位
			onQuickLocateByBtnClicked(); //快速搜索
		} else if (e.getSource() == btn_MoveToNextQuickLocateNode) { //下移..
			onMoveNextQuickLocateNode();
		} else if (e.getSource() == btn_MoveToPreviousQuickLocateNode) { //上移..
			onMovePreviouQuickLocateNode(); //
		} else if (e.getSource() == item_quickLocate) {
			onQuickLocateByMouseRightClicked(); //过滤/定位
		} else if (e.getSource() == item_quickqueryall) { //快速穿透查询,后来觉得树型也像表一样,也要有快速穿透才方便 【xch/2012-02-23】
			onQuickQueryAll(); //
		} else if (e.getSource() == item_refresh) {
			refreshTree(); //
		} else if (e.getSource() == item_expand) {
			myExpandAll(); //
		} else if (e.getSource() == item_collapse) {
			myCollapseAll(); //
		} else if (e.getSource() == item_computechildnodecount) {
			onComputeChildNodeCount(); //
		} else if (e.getSource() == item_modifytemplet) {
			modifyTemplet2(); //模板编辑
		} else if (e.getSource() == item_showDirTree) {
			if (text_dirpath.isVisible()) {
				text_dirpath.setVisible(false); //
			} else {
				text_dirpath.setVisible(true); //
			}
			this.revalidate(); //
			this.repaint(); //
		} else if (e.getSource() == item_updatelinkcode) {
			updateAllLinkCode(); //重置所有LinkCode
		} else if (e.getSource() == item_updatelinkname) {
			updateAllLinkName(); //重置所有LinkName【xch/2012-04-24】
		} else if (e.getSource() == item_checkOneField) { //对某字段大检查!
			onCheckOneFieldRule(); //
		} else if (e.getSource() == item_updateParentid) {
			updateAllParentIdByInit(); //重置所有Parentid
		} else if (e.getSource() == item_showSQL) {
			showSQL();
		} else if (e.getSource() == item_addNode) {
			addNode(null);
		} else if (e.getSource() == item_delNode) {
			delCurrNode();
		}
	}

	/**
	 * 上移一个结点
	 */
	private void moveUpNode() {
		try {
			isTriggerSelectChangeEvent = false; //是否触发选择事件
			DefaultMutableTreeNode myself = this.getSelectedNode(); //
			//避免在初始化界面后没有默认选中节点时的空指针异常
			if (myself == null) {
				return;
			}
			DefaultMutableTreeNode father = (DefaultMutableTreeNode) myself.getParent(); //取得父亲
			if (father == null) {
				return;
			}
			int li_index = father.getIndex(myself); //
			if (li_index == 0) {
				return;
			}

			getJTreeModel().removeNodeFromParent(myself); //
			getJTreeModel().insertNodeInto(myself, father, li_index - 1); //
			setSelected(myself); //
			resetChildSeq(father); //
			nodeMoveChanged(); //调用所有移动监听者
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			isTriggerSelectChangeEvent = true;
		}

	}

	/**
	 * 重置所有LinkCode,所谓linkCode就是以每4位表示一层,用来生成层次关系
	 */
	public void updateAllLinkCode() {
		try {
			if (JOptionPane.showConfirmDialog(this, "您确定要对这颗树重置所有linkcode吗?这将修改所有结点的linkcode值!", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}
			long ll_1 = System.currentTimeMillis(); //
			if (this.getTempletVO().containsItemKey("linkcode")) { //如果有linkCode
				Vector v_sqls = new Vector(); //
				DefaultMutableTreeNode[] allNodes = this.getAllNodes(); //
				for (int i = 1; i < 20; i++) { //假设有20层
					boolean bo_iffind = false; //
					for (int j = 0; j < allNodes.length; j++) { //遍历所有结点..
						if (allNodes[j].getLevel() == i) { //如果是第几层
							bo_iffind = true; //打上标记,表示已经发现了!
							DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) allNodes[j].getParent(); //取得父亲!!!
							int li_index = parentNode.getIndex(allNodes[j]); //判断我在父结点中的位置
							String str_currlinkcode = ("" + (10000 + li_index + 1)).substring(1, 5); //生成序号,四位数据
							String str_parlinkcode = ""; //
							if (!parentNode.isRoot()) {
								str_parlinkcode = getBillVOFromNode(parentNode).getStringValue("linkcode");
							}

							String str_linkcode = str_parlinkcode + str_currlinkcode; //
							BillVO billVO = getBillVOFromNode(allNodes[j]); //
							String str_billvolinkcode = billVO.getStringValue("linkcode"); //
							if (str_billvolinkcode == null || !str_billvolinkcode.equals(str_linkcode)) { //如果树中原来的数据为空或者不等于新的值
								v_sqls.add("update " + getTempletVO().getSavedtablename() + " set linkcode='" + str_linkcode + "' where id='" + billVO.getPkValue() + "'"); //
								billVO.setObject("linkcode", new StringItemVO(str_linkcode)); //
								setBillVO(allNodes[j], billVO); //
							}
						}
					}

					if (!bo_iffind) { //如果某一层一个也没发现,则退出循环..
						break;
					}
				}
				UIUtil.executeBatchByDS(null, v_sqls); //
				long ll_2 = System.currentTimeMillis(); //
				MessageBox.show(this, "重置LinkCode成功,一共修改了[" + v_sqls.size() + "]条记录的LinkCode值,共耗时[" + (ll_2 - ll_1) + "]毫秒!!"); //
			} else {
				MessageBox.show(this, "模板中没有定义linkcode字段,无法进行重置动作!必须保证物理表与模板中都有名为linkcode的字段!"); //
			}
		} catch (Exception e) {
			MessageBox.showException(this, e); //
		}
	}

	/**
	 * 重置所有LinkName
	 */
	public void updateAllLinkName() {
		try {
			if (JOptionPane.showConfirmDialog(this, "您确定要对这颗树重置所有LinkName吗?这将修改所有结点的LinkName值!", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}
			long ll_1 = System.currentTimeMillis(); //
			boolean isFind = false; //是否发现?
			Pub_Templet_1_ItemVO[] itemVOs = getTempletItemVOs();
			for (int i = 0; i < itemVOs.length; i++) {
				if (itemVOs[i].getItemkey().equalsIgnoreCase("linkname")) {
					isFind = true; //
					break; //
				}
			}
			if (!isFind) {
				MessageBox.show(this, "模板与表中没有linkname字段,不能进行此操作!!"); //
				return; //
			}

			String str_tableName = getTempletVO().getSavedtablename(); //表名!!
			String str_pkField = getTempletVO().getTreepk(); //
			if (str_pkField == null || str_pkField.trim().equals("")) {
				str_pkField = getTempletVO().getPkname(); //
			}
			if (str_pkField == null || str_pkField.trim().equals("")) {
				str_pkField = "id"; //
			}

			String str_nameField = getTempletVO().getTreeviewfield(); //
			if (str_nameField == null || str_nameField.trim().equals("")) {
				str_nameField = getTempletVO().getTostringkey(); //
			}
			if (str_nameField == null || str_nameField.trim().equals("")) {
				str_nameField = "name"; //
			}

			String str_parenPkField = getTempletVO().getTreeparentpk(); //
			if (str_parenPkField == null || str_parenPkField.trim().equals("")) {
				str_parenPkField = "parentid"; //
			}

			String str_msg = UIUtil.getMetaDataService().resetTreeLinkName(null, str_tableName, str_pkField, str_nameField, str_parenPkField, "linkname"); //
			long ll_2 = System.currentTimeMillis(); //
			MessageBox.show(this, str_msg + "\r\n总共耗时[" + (ll_2 - ll_1) + "]"); //
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		}
	}

	//快速将所有id,parentid搞成从1开始的初始状态,在导出数据时用到Q
	private void updateAllParentIdByInit() {
		try {
			if (!MessageBox.confirm(this, "您确定要重置所有Parentid为初始状态(从1开始..)吗?这将可能导致其他关联有问题!")) {
				return;
			}
			String str_idField = "id"; //
			String str_parentidField = "parentid"; //
			if (templetVO.getTreepk() != null) { //
				str_idField = templetVO.getTreepk();
			}
			if (templetVO.getTreeparentpk() != null) { //
				str_parentidField = templetVO.getTreeparentpk();
			}
			BillVO[] billVOs = getAllBillVOs(); //
			LinkedHashMap map = new LinkedHashMap(); //
			for (int i = 0; i < billVOs.length; i++) {
				String str_oldId = billVOs[i].getStringValue(str_idField); //原来的id
				map.put(str_oldId, "-" + (i + 1)); //设置!!
			}
			ArrayList al_sqls = new ArrayList(); //
			for (int i = 0; i < billVOs.length; i++) {
				String str_oldId = billVOs[i].getStringValue(str_idField); //原来的id
				String str_newid = (String) map.get(str_oldId); //
				String str_sql = "update " + templetVO.getSavedtablename() + " set " + str_idField + "='" + str_newid + "'"; //
				String str_oldParentId = billVOs[i].getStringValue(str_parentidField); //原来的id
				if (str_oldParentId != null && !str_oldParentId.trim().equals("")) {
					String str_newParentId = (String) map.get(str_oldParentId); //
					str_sql = str_sql + "," + str_parentidField + "='" + str_newParentId + "'"; //
				}
				str_sql = str_sql + " where " + str_idField + "='" + str_oldId + "'"; //
				System.out.println(str_sql); //
				al_sqls.add(str_sql); //
			}

			UIUtil.executeBatchByDS(null, al_sqls); //
			UIUtil.executeBatchByDS(null, new String[] { "update " + templetVO.getSavedtablename() + " set " + str_idField + "=0-" + str_idField, "update " + templetVO.getSavedtablename() + " set " + str_parentidField + "=0-" + str_parentidField + " where " + str_parentidField + " is not null" }); //
			MessageBox.show(this, "更新所有记录成功,请重新打开该页面!"); //
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		}
	}

	/**
	 * 点击检查某个字段规则!!
	 */
	private void onCheckOneFieldRule() {
		String str_demoField = "corptype"; //
		StringBuilder sb_demoRule = new StringBuilder(); //
		sb_demoRule.append("全行-总行-总行部门-总行部门-总行部门\r\n"); //
		sb_demoRule.append("全行-一级分行-一级分行部门-一级分行部门-一级分行部门-一级分行部门\r\n"); //
		sb_demoRule.append("全行-一级分行-一级支行-一级支行部门-一级支行部门-一级支行部门-一级支行部门\r\n"); //
		sb_demoRule.append("全行-一级分行-二级分行-二级分行部门-二级分行部门-二级分行部门-二级分行部门\r\n"); //
		sb_demoRule.append("全行-一级分行-二级分行-二级支行-二级支行部门-二级支行部门-二级支行部门-二级支行部门\r\n"); //

		JLabel label_1 = new JLabel("检查的字段名:", SwingConstants.RIGHT); //
		JTextField textField = new JTextField(str_demoField); //
		JLabel label_2 = new JLabel("检查规则:", SwingConstants.RIGHT);
		JTextArea textArea = new WLTTextArea(sb_demoRule.toString()); //
		textArea.setLineWrap(false); //

		label_1.setBounds(5, 5, 85, 20); //
		textField.setBounds(95, 5, 150, 20); //
		label_2.setBounds(5, 30, 85, 20); //

		JScrollPane scroll = new JScrollPane(textArea); //
		scroll.setBounds(95, 30, 500, 150); //

		JPanel panel = new JPanel(null); //
		panel.setPreferredSize(new Dimension(610, 180)); //
		panel.add(label_1); //
		panel.add(textField); //
		panel.add(label_2); //
		panel.add(scroll); //

		if (JOptionPane.showConfirmDialog(this, panel, "请输入条件", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.YES_OPTION) {
			return; //
		}

		String str_fieldName = textField.getText(); //
		String str_rule = textArea.getText(); //
		str_rule = str_rule.trim(); //
		str_rule = getTBUtil().replaceAll(str_rule, "\r", ""); //去掉\r
		String[] str_rules = getTBUtil().split(str_rule, "\n"); //分割!!
		for (int i = 0; i < str_rules.length; i++) {
			str_rules[i] = str_rules[i].trim(); //
		}

		//搞一个文本框与一个多行文本框!!
		checkOneFieldRule(str_fieldName, str_rules); //
	}

	/**
	 * 检查一个字段的规则是否满足某种规则,比如机构的corptype字段!
	 * 即有时机构类型设置的不对
	 * 全行-总行-总行部门
	 * 全行-一级分行-一级分行部门
	 * 全行-一级分行-一级支行-一级支行部门
	 * 全行-一级分行-二级支行-二级支行部门
	 */
	public void checkOneFieldRule(String _field, String[] _rules) {
		try {
			String str_tableName = getTempletVO().getSavedtablename(); //表名!!
			String str_pkField = getTempletVO().getTreepk(); //
			if (str_pkField == null || str_pkField.trim().equals("")) {
				str_pkField = getTempletVO().getPkname(); //
			}
			if (str_pkField == null || str_pkField.trim().equals("")) {
				str_pkField = "id"; //
			}

			String str_nameField = getTempletVO().getTreeviewfield(); //
			if (str_nameField == null || str_nameField.trim().equals("")) {
				str_nameField = getTempletVO().getTostringkey(); //
			}
			if (str_nameField == null || str_nameField.trim().equals("")) {
				str_nameField = "name"; //
			}

			String str_parenPkField = getTempletVO().getTreeparentpk(); //
			if (str_parenPkField == null || str_parenPkField.trim().equals("")) {
				str_parenPkField = "parentid"; //
			}

			String str_seqField = getTempletVO().getTreeseqfield(); //

			String[] str_return = UIUtil.getMetaDataService().checkTreeOneFieldRule(null, str_tableName, str_pkField, str_nameField, str_parenPkField, str_seqField, _field, _rules); //
			JTextArea text1 = new JTextArea(); //
			text1.setFont(LookAndFeel.font); //
			text1.setEditable(false);
			text1.setBackground(LookAndFeel.systembgcolor); //
			text1.setText("修改空值的SQL(手动快速填写值批量执行更快):\r\n" + str_return[0]); //

			JTextArea text2 = new JTextArea(); //
			text2.setFont(LookAndFeel.font); //
			text2.setEditable(false);
			text2.setBackground(LookAndFeel.systembgcolor); //
			text2.setText("因为有个结点值为空,造成的父亲路径链值不对,它的数据可能比为空的数据多,因为可能是一个目录结点的值为空!反之,如果修改空值,则一批都好了:\r\n" + str_return[1]); //

			JTextArea text3 = new JTextArea(); //
			text3.setFont(LookAndFeel.font); //
			text3.setEditable(false);
			text3.setBackground(LookAndFeel.systembgcolor); //
			text3.setText("修改路径链不对的SQL,强烈提醒:并一定就是末结点不对,可能是其某个目录结点不对!这时该SQL就不能执行,需要手工去调!:\r\n" + str_return[2]); //

			JTextArea text4 = new JTextArea(); //
			text4.setFont(LookAndFeel.font); //
			text4.setEditable(false);
			text4.setBackground(LookAndFeel.systembgcolor); //
			text4.setText("针对机构的智能测试,强烈提醒,不一定准,要确认后才行:\r\n" + str_return[3]); //

			JTabbedPane tabbed = new JTabbedPane(); //
			tabbed.addTab("修改空值的SQL", new JScrollPane(text1)); //
			tabbed.addTab("路径链不对(因为空值造成的)", new JScrollPane(text2)); //
			tabbed.addTab("修改路径链不对的SQL", new JScrollPane(text3)); //

			if (str_tableName.equalsIgnoreCase("pub_corp_dept") && _field.equalsIgnoreCase("corptype")) { //只有是机构才加上
				tabbed.addTab("针对机构的猜测", new JScrollPane(text4)); //
			}

			BillDialog dialog = new BillDialog(this, "检查结果,表名[" + str_tableName + "],检查的字段[" + _field + "]", 950, 700); //
			dialog.getContentPane().add(tabbed); //
			dialog.setVisible(true); //
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		}
	}

	private void nodeMoveChanged() {
		DefaultMutableTreeNode selnode = this.getSelectedNode(); //
		BillVO billVO = this.getSelectedVO(); //
		for (int i = 0; i < v_BillTreeMovedListeners.size(); i++) {
			BillTreeMoveListener movelistener = (BillTreeMoveListener) v_BillTreeMovedListeners.get(i); ////
			movelistener.onBillTreeNodeMoved(new BillTreeMoveEvent(this, selnode, billVO)); //
		}
	}

	private void moveDownNode() {
		try {
			isTriggerSelectChangeEvent = false; //是否触发选择事件
			DefaultMutableTreeNode myself = this.getSelectedNode(); //
			//避免在初始化界面时，未有默认选中节点时的空指针异常
			if (myself == null) {
				return;
			}
			DefaultMutableTreeNode father = (DefaultMutableTreeNode) myself.getParent(); //
			if (father == null) {
				return;
			}
			int li_index = father.getIndex(myself); //
			if (li_index == father.getChildCount() - 1) {
				return; //如果是最后一个了,则不做处理
			}
			getJTreeModel().removeNodeFromParent(myself); //
			getJTreeModel().insertNodeInto(myself, father, li_index + 1); //
			setSelected(myself); //
			resetChildSeq(father); //
			nodeMoveChanged(); //调用所有移动监听者
		} catch (Exception ex) {
			ex.printStackTrace(); //
		} finally {
			isTriggerSelectChangeEvent = true; //
		}
	}

	private JScrollPane getMainScrollPane() {
		if (mainScrollPane != null) {
			return mainScrollPane;
		}

		rootNode = new BillTreeDefaultMutableTreeNode(new BillTreeNodeVO(-1, this.templetVO.getTempletname())); // 创建根结点!!!

		if (templetVO.getTreeisshowtoolbar()) {
			jTree = new MyDragTree(new DefaultTreeModel(rootNode)); // 创建树!!
		} else {
			jTree = new JTree(new DefaultTreeModel(rootNode)); // 创建树!!
		}

		jTree.setBackground(LookAndFeel.treebgcolor); //defaultShadeColor1
		((BasicTreeUI) jTree.getUI()).setCollapsedIcon(ImageIconFactory.getCollapsedIcon()); //
		((BasicTreeUI) jTree.getUI()).setExpandedIcon(ImageIconFactory.getExpandedIcon()); //
		//jTree.setUI(new WLTTreeUI());
		jTree.setOpaque(false); //一定要设成透明,否则快速滚动时会出现白条,我曾经为此折磨得很久!!!!!!
		jTree.setShowsRootHandles(true); //
		jTree.setRowHeight(17); //
		jTree.setEnabled(true); //
		cellRender = new BillTreeCheckCellRender(isChecked); //
		jTree.setCellRenderer(cellRender); //设置勾选框

		if (isChecked) {
			jTree.setEditable(true); //
			cellEditor = new BillTreeCheckCellEditor(jTree, cellRender, this); //
			jTree.setCellEditor(cellEditor); //
			jTree.setToolTipText("注意:按住Shift键不联动选择子结点!"); //
		} else {
			jTree.setEditable(false); //
		}

		jTree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				if (evt.getButton() == MouseEvent.BUTTON3) {
					showPopMenu(evt.getComponent(), evt.getX(), evt.getY()); // 弹出菜单
				} else {
					super.mousePressed(evt); //
				}
			}
		});

		jTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent evt) {
				TreePath[] paths = evt.getPaths(); //取得所有选中的
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[0].getLastPathComponent();
				onChangeSelectTree(node); // This
			}
		});

		//判断是否只能同时打开一层!!有时为了保护数据故意为了不让用户一眼清,需要做这种控制!!!!见怪不怪!!!
		if (templetVO.getTreeisonlyone()) {
			jTree.addTreeExpansionListener(new TreeExpansionListener() {
				public void treeCollapsed(TreeExpansionEvent event) {

				}

				public void treeExpanded(TreeExpansionEvent event) {
					JTree expandtree = (JTree) event.getSource(); //
					TreePath path = event.getPath(); //
					DefaultMutableTreeNode node_pp = (DefaultMutableTreeNode) path.getLastPathComponent(); //
					if (!node_pp.isRoot()) {
						TreePath parentpath = path.getParentPath(); //
						DefaultMutableTreeNode node_parent = (DefaultMutableTreeNode) node_pp.getParent(); //
						int li_childcount = node_parent.getChildCount(); //
						for (int i = 0; i < li_childcount; i++) {
							DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node_parent.getChildAt(i); //
							if (childNode != node_pp) {
								expandtree.collapsePath(new TreePath(childNode.getPath())); //
							}
						}
					}
				}
			}); //
		}

		jTree.setRootVisible(templetVO.getTreeisshowroot().booleanValue()); // 设置是否显示根结点!!
		mainScrollPane = new JScrollPane(jTree);
		mainScrollPane.setOpaque(false); //
		mainScrollPane.getViewport().setOpaque(false); //
		//mainScrollPane.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210), 1)); //
		mainScrollPane.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, new Color(210, 210, 210))); //
		return mainScrollPane;
	}

	/**
	 * 重置树是否可多选编辑
	 * @param _checked
	 */
	public void reSetTreeChecked(boolean _checked) {
		this.isChecked = _checked; //
		cellRender = new BillTreeCheckCellRender(isChecked); //
		jTree.setCellRenderer(cellRender); //设置勾选框
		if (isChecked) {
			jTree.setEditable(true); //
			cellEditor = new BillTreeCheckCellEditor(jTree, cellRender, this); //
			jTree.setCellEditor(cellEditor); //
		} else {
			jTree.setEditable(false); //
		}
		jTree.repaint(); //
	}

	public String item = null;

	//根据表和模板配置的字段交集，查询表
	public String getItems() {
		if (!"pub_corp_dept".equalsIgnoreCase(templetVO.getSavedtablename())) { //目前只判断pub_corp_dept
			return "*";
		}
		if (getTBUtil().isEmpty(item)) {
			try {
				HashSet tableStructNames = (HashSet) ClientEnvironment.getInstance().get("tablestructnames$" + templetVO.getSavedtablename().toLowerCase());
				if (tableStructNames == null) {
					tableStructNames = new HashSet();
					String[] itemNames = UIUtil.getTableDataStructByDS(null, "select * from " + templetVO.getTablename() + " where 1=2").getHeaderName();
					for (int i = 0; i < itemNames.length; i++) {
						tableStructNames.add(itemNames[i].toLowerCase());
					}
					ClientEnvironment.getInstance().put("tablestructnames$" + templetVO.getSavedtablename().toLowerCase(), tableStructNames);
				}
				StringBuffer sb = new StringBuffer();
				for (int j = 0; j < templetItemVOs.length; j++) {
					if (tableStructNames.contains(templetItemVOs[j].getItemkey().toLowerCase())) {
						sb.append(templetItemVOs[j].getItemkey() + ",");
					}
				}
				if (sb.length() > 0) {
					item = sb.substring(0, sb.length() - 1);
				} else {
					item = "*";
				}
			} catch (Exception e) {
				item = "*";
				e.printStackTrace();
			}
		}
		return item;
	}

	public String getSQL(String _condition) {
		//		String str_sql = "select * from " + templetVO.getTablename() + " where 1=1 "; //
		String str_sql = "select " + getItems() + " from " + templetVO.getTablename() + " where 1=1 "; //
		if (_condition != null && !_condition.trim().equals("")) {
			str_sql = str_sql + " and (" + _condition + ") "; //
		}

		String str_constraintFilterCondition = getDataconstraint();
		if (str_constraintFilterCondition != null && !str_constraintFilterCondition.trim().equals("")) {
			str_sql = str_sql + " and (" + str_constraintFilterCondition + ") ";
		}

		String str_cutcondition = getCustCondition(); //客户自定义条件.
		if (str_cutcondition != null && !str_cutcondition.trim().equals("")) {
			str_sql = str_sql + " and (" + str_cutcondition + ") "; //客户自定义条件
		}
		//str_sql = str_sql + " order by " + templetVO.getTreeseqfield();

		String str_resAccessSQLCons = getResAccesSQLCondition(); //权限过滤的SQL条件!!
		if (str_resAccessSQLCons != null && !str_resAccessSQLCons.trim().equals("")) {
			str_sql = str_sql + " and (" + str_resAccessSQLCons + ") "; //客户自定义条件
		}
		return str_sql; //
	}

	public String getTableAllItem() {
		StringBuffer sb_item = new StringBuffer();
		int itemlength = templetItemVOs.length;
		for (int i = 0; i < itemlength; i++) {
			if (templetItemVOs[i].isNeedSave()) {
				if (sb_item.length() == 0) {
					sb_item.append(" " + templetItemVOs[i].getItemkey());
				} else {
					sb_item.append("," + templetItemVOs[i].getItemkey());
				}
			}
		}
		sb_item.append(" ");
		if (sb_item.toString().trim().length() == 0) {
			return "*";
		}
		return sb_item.toString();
	}

	/**
	 * 得到数据权限过滤条件
	 * 
	 * @return
	 */
	public String getDataconstraint() {
		if (templetVO.getDataconstraint() == null || templetVO.getDataconstraint().trim().equals("null") || templetVO.getDataconstraint().trim().equals("")) {
			return null; // 默认数据源
		} else {
			return "" + new JepFormulaParseAtUI(null).execFormula(templetVO.getDataconstraint()); //
		}
	}

	public String getResAccesSQLCondition() {
		//最后再强行加上数据权限定义的SQL
		if (this.templetVO.getDatapolicy() != null && !this.templetVO.getDatapolicy().trim().equals("")) { //如果定义数据权限定义!!则加上数据权限过滤!!
			try {
				String[] str_accessCondition = getMetaDataService().getDataPolicyCondition(ClientEnvironment.getCurrLoginUserVO().getId(), this.templetVO.getDatapolicy(), this.templetVO.getDatapolicymap()); //
				str_resAccessInfo = str_accessCondition[0];
				String str_virtualCorpIds = null;
				if (str_accessCondition.length >= 3) {
					str_virtualCorpIds = str_accessCondition[2]; //
				}
				if (str_virtualCorpIds == null) { //判断如果需要输出虚拟结点,则拼接【str_accessCondition[1] or str_accessCondition[2]】,然后在类变量中记录下虚拟结点的 
					str_resAccessSQL = str_accessCondition[1]; //
					virtualCorpIdsHst.clear(); //清空机构虚拟结点容器中的数据!!!
				} else {
					str_resAccessSQL = str_accessCondition[1] + " or " + str_virtualCorpIds; //真正的查询条件!!即blcorp in ();
					virtualCorpIdsHst.clear(); //清空机构虚拟结点容器中的数据!!!
					String str_ids = str_virtualCorpIds.substring(str_virtualCorpIds.indexOf("(") + 1, str_virtualCorpIds.indexOf(")")); //肯定是个in (...)的结构!!
					String[] str_idItems = getTBUtil().split(str_ids, ","); //
					for (int i = 0; i < str_idItems.length; i++) {
						virtualCorpIdsHst.add(str_idItems[i]); //加入!!!
					}
				}
				return str_resAccessSQL;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 设置数据权限策略!在数据权限参照定义等地方，经常遇到需要手工设置数据权限策略!
	 * @param _datapolicyName
	 * @param _policyMap
	 */
	public void setDataPolicy(String _datapolicyName, String _policyMap) {
		if (templetVO != null) {
			templetVO.setDatapolicy(_datapolicyName); //
			templetVO.setDatapolicymap(_policyMap); //
		}
	}

	private FrameWorkMetaDataServiceIfc getMetaDataService() throws Exception {
		FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); ////
		return service;
	}

	/**
	 * 客户自定义条件...
	 * @return
	 */
	public String getCustCondition() {
		return custCondition; //
	}

	public void setCustCondition(String _condition) {
		custCondition = _condition; //
	}

	public void setRootTitle(String _title) {
		this.rootNode.setUserObject(_title); //
		jTree.repaint(); //
	}

	//
	public void queryDataByCondition(String _condition) {
		queryData(getSQL(_condition));
	}

	public void queryDataByCondition(String _condition, int _leafLevel) {
		queryData(getSQL(_condition), _leafLevel);
	}

	public void queryData(String _sql) {
		queryDataByDS(getDataSourceName(), _sql); //
	}

	public void queryData(String _sql, int _leafLevel) {
		queryDataByDS(getDataSourceName(), _sql, _leafLevel); //
	}

	public void queryDataByDS(final String _datasourcename, final String _sql) {
		queryDataByDS(_datasourcename, _sql, 0); //
	}

	/**
	 * 有时经常遇到事行查询出数据,然后直接根据一个HashVO来构造数据!
	 * 这种方法的前提是这个HashVO数组中要有模板中定的树型关联的id与parentid的字段!!否则树型是构造不出来的!
	 * @param _hvs
	 * @param _leafLevel
	 */
	public void queryDataByDS(HashVO[] _hvs, int _leafLevel) {
		this.str_realsql = null; //
		this.realStrData = null;
		mapModelIndex.clear(); //清空
		rootNode.removeAllChildren(); // 清空所有子孙

		realStrData = new TableDataStruct(); //
		if (_hvs != null && _hvs.length > 0) {
			String[] str_keys = _hvs[0].getKeys(); //
			realStrData.setHeaderName(str_keys); //表头
			String[][] str_data = new String[_hvs.length][str_keys.length]; //
			for (int i = 0; i < _hvs.length; i++) {
				for (int j = 0; j < str_keys.length; j++) {
					str_data[i][j] = _hvs[i].getStringValue(str_keys[j]); ////
				}
			}
			realStrData.setBodyData(str_data); //
		}

		long ll_1 = System.currentTimeMillis(); //
		buidTree(_leafLevel); //构造树！！
		long ll_2 = System.currentTimeMillis();

		logger.debug("根据[" + (_hvs == null ? "0" : _hvs.length) + "]条数据,构造成树耗时[" + (ll_2 - ll_1) + "]毫秒!!"); //
	}

	/**
	 * 查询数据
	 * @param _datasourcename
	 * @param _sql
	 */
	public void queryDataByDS(final String _datasourcename, final String _sql, int _leafLevel) {
		this.str_realsql = _sql; //
		this.realStrData = null;
		mapModelIndex.clear(); //清空
		rootNode.removeAllChildren(); // 清空所有子孙

		long ll_0 = System.currentTimeMillis();
		try {
			this.realStrData = UIUtil.getMetaDataService().getBillTreeData(_datasourcename, _sql, this.templetVO.getParPub_Templet_1VO()); //
		} catch (Exception ex) {
			logger.error("查询树型控件数据时发生异常:", ex); //
			return;
		}

		long ll_1 = System.currentTimeMillis(); //
		buidTree(_leafLevel); //构造树！！
		long ll_2 = System.currentTimeMillis();

		logger.debug("前台拿到[" + (realStrData.getBodyData() == null ? "0" : realStrData.getBodyData().length) + "]条数据,拿数据耗时[" + (ll_1 - ll_0) + "]毫秒,将数据构造成树耗时[" + (ll_2 - ll_1) + "]毫秒,共耗时[" + (ll_2 - ll_0) + "]毫秒!!"); //
	}

	/**
	 * 构造树！！！
	 * @param _leafLevel
	 */
	private void buidTree(int _leafLevel) {
		try {
			getTBUtil().sortTableDataStruct(this.realStrData, new String[][] { { templetVO.getTreeseqfield(), "N", "Y" } }); //根据seq字段进行排序,不再是在通过LinkCode进行排序了!
		} catch (NumberFormatException e) {
			getTBUtil().sortTableDataStruct(this.realStrData, new String[][] { { templetVO.getTreeseqfield(), "N", "N" } });
		}

		DefaultMutableTreeNode[] node_level_1 = new DefaultMutableTreeNode[realStrData.getBodyData().length]; //创建结点
		BillTreeNodeVO treeNodeVO = null;

		boolean isCorpDeptObject = "pub_corp_dept".equalsIgnoreCase(this.templetVO.getTablename()); //判断是否是机构对象??
		boolean isVirtualNode = false; //
		HashMap map_parent = new HashMap();//缓存所有节点
		for (int i = 0; i < realStrData.getBodyData().length; i++) {
			String[] str_nodeTextAndIcon = getTreeNodeViewText(i); //
			if (isCorpDeptObject) { //必须是机构对象才做虚拟结点判断!!因为数据权限目前就只是两种【机构过滤/人员过滤】
				String str_nodePkValue = getTreeNodePkValue(i); //找到这条对象的主键值!!
				isVirtualNode = this.virtualCorpIdsHst.contains(str_nodePkValue); //在执行数据权限时,如果是机构过滤,则可能产生虚拟结点!则这时判断一下是否是虚拟结点?
			}
			treeNodeVO = new BillTreeNodeVO(i, str_nodeTextAndIcon[0], str_nodeTextAndIcon[1], isVirtualNode); //创建结点数据对象
			node_level_1[i] = new BillTreeDefaultMutableTreeNode(treeNodeVO); //一上来把所有节点放到根节点，然后再断开关系，性能慢。[郝明2012-07-18]
			map_parent.put(getModelItemVaueFromNode(node_level_1[i], templetVO.getTreepk()), node_level_1[i]); //先在哈希表中注册一下,下次找起来才快!!
		}

		BillTreeNodeVO nodeVO = null; //
		String str_pk_parentPK = null; //
		BillTreeNodeVO nodeVO_2 = null; //
		String str_pk_2 = null; //

		for (int i = 0; i < node_level_1.length; i++) {
			nodeVO = (BillTreeNodeVO) node_level_1[i].getUserObject();
			//String str_thispk = getTreeModelItemVaue(nodeVO, templetVO.getTreepk()); //
			str_pk_parentPK = getTreeModelItemVaue(nodeVO, templetVO.getTreeparentpk()); //nodeVO.getRealValue(templetVO.getTreeparentpk()); // 父亲主键
			if (str_pk_parentPK == null || str_pk_parentPK.trim().equals("")) {
				rootNode.add(node_level_1[i]); //在这个地方把找不到父亲的节点加入根节点下。提升4-6倍[郝明2012-07-18]
				continue;
			}

			DefaultMutableTreeNode parentnode = (DefaultMutableTreeNode) map_parent.get(str_pk_parentPK); //
			if (parentnode != null) { //如果找到爸爸了..
				try {
					parentnode.add(node_level_1[i]); //在爸爸下面加入我..
				} catch (Exception ex) {
					logger.error("在[" + parentnode + "]上创建子结点[" + node_level_1[i] + "]失败!!");
					ex.printStackTrace(); //
				}
			} else { //如果没找到爸爸,则强行加入子结点!
				rootNode.add(node_level_1[i]); //
			}
		}

		//是否只显留前几层
		if (_leafLevel > 0) {
			for (int i = 0; i < node_level_1.length; i++) {
				if (node_level_1[i].getLevel() == _leafLevel) { //如果当前层是第三层,则移去当前层下的所有子孙,即砍掉第三层以下的所有枝叶
					node_level_1[i].removeAllChildren();
				}
			}
		}

		map_parent.clear(); //清空哈希表..

		jTree.expandPath(new TreePath(rootNode)); //先展开第一层

		if (rootNode.getChildCount() == 1) { //如果根结点只有一个子结点,则同时展开这个子结点!
			TreePath path = new TreePath(getJTreeModel().getPathToRoot(rootNode.getChildAt(0)));
			jTree.expandPath(path); //
		}

		//jTree.repaint(); //
		updateUI(); //
	}

	private String getTreeNodePkValue(int _row) {
		String str_itemKey = this.getTempletVO().getPkname(); //主键字段名!!
		if (str_itemKey == null || str_itemKey.trim().equals("")) {
			return null; //
		}
		int li_index_key = findIndexInModel(str_itemKey); //根据itemkey去数据中找
		if (li_index_key < 0) {
			return null; //
		}
		return realStrData.getBodyData()[_row][li_index_key]; //显示名称
	}

	/**
	 * 取得树型控件的名称
	 * @param _row
	 * @param _itemKey
	 * @return
	 */
	private String[] getTreeNodeViewText(int _row) {
		String str_itemKey = this.getTempletVO().getTreeviewfield();
		if (str_itemKey == null || str_itemKey.trim().equals("")) {
			return new String[] { "没有指定树型控件显示的列名", null };
		}

		int li_index_key = findIndexInModel(str_itemKey); //根据itemkey去数据中找
		if (li_index_key >= 0) {
			String str_nodeText = realStrData.getBodyData()[_row][li_index_key]; //显示名称
			String str_nodeIconName = null; //图标名称
			int li_index_icon = findIndexInModel("iconname"); //看有没有名为icon的字段
			if (li_index_icon >= 0) { //如果有图标字段
				str_nodeIconName = realStrData.getBodyData()[_row][li_index_icon]; //
			}
			return new String[] { str_nodeText, str_nodeIconName }; //
		} else {
			return new String[] { "没有在查询结果中找到列名[" + str_itemKey + "]", null }; //
		}
	}

	private ComBoxItemVO findComBoxItemVO(ComBoxItemVO[] _vos, String _id) {
		if (_vos == null || _vos.length == 0) {
			return null;
		}
		for (int k = 0; k < _vos.length; k++) {
			if (_vos[k].getId() != null && _vos[k].getId().equals(_id)) { //如果下拉框的主键不为空!!
				return _vos[k];
			}
		}
		return null;
	}

	/**
	 * 取得某一个结点上的某一列的值
	 * @param _node
	 * @param _itemKey
	 * @return
	 */
	public String getModelItemVaueFromNode(DefaultMutableTreeNode _node, String _itemKey) {
		BillTreeNodeVO nodeVo = (BillTreeNodeVO) _node.getUserObject(); //
		if (nodeVo == null) {
			return null;
		}
		return getTreeModelItemVaue(nodeVo, _itemKey); //
	}

	/**
	 * 根据结点VO去实际数据对应的位置中找到某一列的值
	 * @param _vo
	 * @param _itemKey
	 * @return
	 */
	private String getTreeModelItemVaue(BillTreeNodeVO _vo, String _itemKey) {
		int li_rowNo = _vo.getRowNo();
		if (li_rowNo < 0) {
			return null;
		}
		return getStringValueInModel(li_rowNo, _itemKey); //一下子根据绝对坐标位置去找!是最快的!!
	}

	/**
	 * 
	 * @param _row
	 * @param _itemkey
	 * @return
	 */
	private String getStringValueInModel(int _row, String _itemkey) {
		int li_index = findIndexInModel(_itemkey); // 
		if (li_index < 0 || realStrData.getBodyData().length <= _row) {
			return null;
		}
		String[] str_data = realStrData.getBodyData()[_row];
		return str_data[li_index];
	}

	/**
	 * 根据某一个列名,找到在实际数据中的位置,为了提高性能,一旦找出，则立即记录下来!
	 * @param _itemKey
	 * @return
	 */
	private int findIndexInModel(String _itemKey) {
		if (_itemKey == null || _itemKey.trim().equals("")) {
			return -1;
		}
		Integer li_index = (Integer) mapModelIndex.get(_itemKey.toUpperCase());
		if (li_index != null) { //如果已存储则直接返回
			return li_index.intValue();
		} else {
			int li_pos = -1;
			String[] keys = this.realStrData.getHeaderName();
			for (int i = 0; i < keys.length; i++) {
				if (keys[i] != null && keys[i].equalsIgnoreCase(_itemKey)) {
					li_pos = i; //
					break;
				}
			}
			mapModelIndex.put(_itemKey.toUpperCase(), new Integer(li_pos));
			return li_pos;
		}
	}

	/**
	 * 得到数据源名称
	 * 
	 * @return
	 */
	public String getDataSourceName() {
		if (templetVO.getDatasourcename() == null || templetVO.getDatasourcename().trim().equals("null") || templetVO.getDatasourcename().trim().equals("")) {
			return ClientEnvironment.getInstance().getDefaultDataSourceName(); // 默认数据源
		} else {
			return getTBUtil().convertDataSourceName(ClientEnvironment.getInstance().getCurrSessionVO(), templetVO.getDatasourcename()); // 算出数据源!!
		}
	}

	/**
	 * 当是勾选框的树型时,点击勾选框触发的事件!!!
	 * @param _listener
	 */
	public void addBillTreeCheckEditListener(BillTreeCheckEditListener _listener) {
		v_billTreeCheckListener.add(_listener); //
	}

	/**
	 * 点击选中事件!
	 * @param _node
	 * @param _checked
	 */
	public void onCheckEditChange(BillTreeDefaultMutableTreeNode _node, boolean _checked) {
		BillTreeCheckEditEvent event = new BillTreeCheckEditEvent(_node, _checked, this);
		for (int i = 0; i < v_billTreeCheckListener.size(); i++) {
			((BillTreeCheckEditListener) v_billTreeCheckListener.get(i)).onBillTreeCheckEditChanged(event);
		}
	}

	/**
	 * 处理树的选择节点的变更的事件.....
	 * @param _node
	 */
	private void onChangeSelectTree(DefaultMutableTreeNode _node) {
		if (!isTriggerSelectChangeEvent) {
			return;
		}
		for (int i = 0; i < v_BillTreeSelectListeners.size(); i++) {
			BillTreeSelectListener listener = (BillTreeSelectListener) v_BillTreeSelectListeners.get(i);
			BillVO billVO = getBillVOFromNode(_node); //
			BillTreeSelectionEvent event = new BillTreeSelectionEvent(this, _node, billVO); // 当前选中的结点
			listener.onBillTreeSelectChanged(event); //
		}

		DefaultTreeModel model = (DefaultTreeModel) this.jTree.getModel();
		TreeNode[] nodes = model.getPathToRoot(_node);
		TreePath path = new TreePath(nodes);
		cellRender.setSelectPath(path); //

		StringBuilder sb_path = new StringBuilder("直接路径:【"); //
		for (int i = 0; i < nodes.length; i++) {
			sb_path.append("" + ((DefaultMutableTreeNode) nodes[i]).getUserObject()); //
			if (i != nodes.length - 1) { //
				sb_path.append("->"); //
			}
		}
		sb_path.append("】"); //
		text_dirpath.setText(sb_path.toString()); //
		text_dirpath.setToolTipText(sb_path.toString()); //
		text_dirpath.setSelectionStart(0); //
		text_dirpath.setSelectionEnd(0); //
		jTree.repaint(); ////
	}

	/**
	 * 选中某一个结点....
	 * @param _node
	 */
	public void setSelected(TreeNode _node) {
		if (_node == null) {
			return; ////
		}
		DefaultTreeModel model = (DefaultTreeModel) jTree.getModel();
		TreeNode[] nodes = model.getPathToRoot(_node);
		TreePath path = new TreePath(nodes); //
		jTree.setSelectionPath(path); //
	}

	/**
	 * 增加选择
	 * @param _node
	 */
	public void addSelected(TreeNode _node) {
		if (_node == null) {
			return; //
		}
		DefaultTreeModel model = (DefaultTreeModel) jTree.getModel();
		TreeNode[] nodes = model.getPathToRoot(_node);
		TreePath path = new TreePath(nodes); //
		jTree.addSelectionPath(path); //
	}

	public TreePath getSelectedPath() {
		return jTree.getSelectionPath();
	}

	/**
	 * 取得当前选中的结点
	 * 
	 * @return
	 */
	public DefaultMutableTreeNode getSelectedNode() {
		TreePath path = jTree.getSelectionPath();
		if (path == null) {
			return null;
		}
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		return node;
	}

	/**
	 * 取得选中结点的实际结点对象
	 * @return
	 */
	public BillTreeNodeVO getSelectedTreeNodeVO() {
		DefaultMutableTreeNode node = getSelectedNode(); //
		if (node == null) {
			return null;
		}
		return (BillTreeNodeVO) node.getUserObject(); //
	}

	/**
	 * 取得当前选中的结点到根结点的所有结点..
	 * 
	 * @return
	 */
	public DefaultMutableTreeNode[] getSelectedParentPathNodes() {
		TreePath path = jTree.getSelectionPath();
		if (path == null) {
			return null;
		}

		Object[] objs = path.getPath(); //
		DefaultMutableTreeNode[] nodes = new DefaultMutableTreeNode[objs.length];
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = (DefaultMutableTreeNode) objs[i];
		}
		return nodes; //
	}

	public DefaultMutableTreeNode[] getOneNodeParentNodes(DefaultMutableTreeNode _node) {
		TreeNode[] nodes_init = _node.getPath(); //
		DefaultMutableTreeNode[] nodes = new DefaultMutableTreeNode[nodes_init.length];
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = (DefaultMutableTreeNode) nodes_init[i];
		}
		return nodes;
	}

	/**
	 * 取得选中结点路径上的所有数据对象!!,即从根结点到选中结点的所有数据对象,但根结点除外
	 * 如果没有选中,则返回null,如果只选中根结点,则返回BillVO[0]
	 * 
	 * @return
	 */
	public BillVO[] getSelectedParentPathVOs() {
		TreePath path = jTree.getSelectionPath();
		if (path == null) {
			return null;
		}
		DefaultMutableTreeNode[] nodes = getSelectedParentPathNodes();
		ArrayList al_BillVOs = new ArrayList(); //
		BillVO billVO = null;
		for (int i = 0; i < nodes.length; i++) {
			billVO = getBillVOFromNode(nodes[i]); //为该结点创建BillVO
			if (billVO != null) {
				al_BillVOs.add(billVO); //
			}
		}
		return (BillVO[]) al_BillVOs.toArray(new BillVO[0]);
	}

	/**
	 * 取得所有勾中的结点
	 * @return
	 */
	public DefaultMutableTreeNode[] getCheckedNodes() {
		DefaultMutableTreeNode[] allNodes = getAllNodes(); //先找出所有结点!!!
		ArrayList al_vos = new ArrayList(); //
		BillVO billVO = null;
		for (int i = 0; i < allNodes.length; i++) {
			BillTreeDefaultMutableTreeNode checkedNode = (BillTreeDefaultMutableTreeNode) allNodes[i]; //
			if (checkedNode.isChecked()) {
				al_vos.add(checkedNode); //
			}
		}
		return (DefaultMutableTreeNode[]) al_vos.toArray(new DefaultMutableTreeNode[0]); //
	}

	/**
	 * 得到所有勾中的结点!!
	 * @return
	 */
	public BillVO[] getCheckedVOs() {
		DefaultMutableTreeNode[] allNodes = getAllNodes(); //先找出所有结点!!!
		ArrayList al_temp = new ArrayList(); //
		for (int i = 0; i < allNodes.length; i++) {
			BillTreeDefaultMutableTreeNode checkedNode = (BillTreeDefaultMutableTreeNode) allNodes[i]; //
			if (checkedNode.isChecked()) { //如果是被勾上的!
				al_temp.add(checkedNode); //
			}
		}
		DefaultMutableTreeNode[] allCheckNodes = (DefaultMutableTreeNode[]) al_temp.toArray(new DefaultMutableTreeNode[0]); //转换!!
		BillVO[] billVOs = new BillVO[allCheckNodes.length]; //
		for (int i = 0; i < allCheckNodes.length; i++) {
			billVOs[i] = getBillVOFromNode(allCheckNodes[i]);
		}
		return billVOs; //
	}

	/**
	 * 取得所有结点
	 * 
	 * @return
	 */
	public DefaultMutableTreeNode[] getAllNodes() {
		Vector vector = new Vector();
		visitAllNodes(vector, this.rootNode); //
		return (DefaultMutableTreeNode[]) vector.toArray(new DefaultMutableTreeNode[0]); //
	}

	public int getAllNodesCount() {
		DefaultMutableTreeNode[] allNodes = getAllNodes(); //
		if (allNodes != null) {
			return allNodes.length;
		} else {
			return -1;
		}
	}

	/**
	 * 取得树的最大层数
	 * @return
	 */
	public int getMaxLevel() {
		DefaultMutableTreeNode[] allNodes = getAllNodes(); //先找出所有结点!!!
		int maxlevel = 0;
		for (int i = 0; i < allNodes.length; i++) {
			if (allNodes[i].getLevel() > maxlevel) {
				maxlevel = allNodes[i].getLevel();
			}
		}

		return maxlevel;
	}

	/**
	 * 得到所有中子结点.
	 * @return
	 */
	public DefaultMutableTreeNode[] getAllLeafNodes() {
		Vector v_nodes = new Vector(); //
		DefaultMutableTreeNode[] allNodes = getAllNodes(); //
		for (int i = 0; i < allNodes.length; i++) {
			if (allNodes[i].isLeaf()) {
				v_nodes.add(allNodes[i]); //
			}
		}

		return (DefaultMutableTreeNode[]) v_nodes.toArray(new DefaultMutableTreeNode[0]); //
	}

	/**
	 * 得到所有数据.
	 * @return
	 */
	public BillVO[] getAllBillVOs() {
		DefaultMutableTreeNode[] nodes = getAllNodes(); //
		ArrayList al_vos = new ArrayList(); //
		for (int i = 0; i < nodes.length; i++) {
			BillVO billVO = getBillVOFromNode(nodes[i]); //
			if (billVO != null) {
				al_vos.add(billVO); //
			}
		}
		return (BillVO[]) al_vos.toArray(new BillVO[0]);
	}

	/**
	 * 取得当前结点的所有儿子结点!!
	 * 
	 * @return
	 */
	public DefaultMutableTreeNode[] getSelectedChildPathNodes() {
		DefaultMutableTreeNode currNode = this.getSelectedNode(); //
		if (currNode == null) {
			return null; //
		}
		Vector vector = new Vector();
		visitAllNodes(vector, currNode); //
		return (DefaultMutableTreeNode[]) vector.toArray(new DefaultMutableTreeNode[0]); //
	}

	/**
	 * 取得莜个结点的所有子结点
	 * @param _node
	 * @return
	 */
	public DefaultMutableTreeNode[] getOneNodeChildPathNodes(DefaultMutableTreeNode _node) {
		return getOneNodeChildPathNodes(_node, false);
	}

	/**
	 * 
	 * @param _node 取得某一个结点下所有子结,可以指定是否包含自己
	 * @param _containSelf
	 * @return
	 */
	public DefaultMutableTreeNode[] getOneNodeChildPathNodes(DefaultMutableTreeNode _node, boolean _containSelf) {
		Vector vector = new Vector();
		if (_containSelf) {
			vector.add(_node); //
		}
		visitAllNodes(vector, _node); //
		return (DefaultMutableTreeNode[]) vector.toArray(new DefaultMutableTreeNode[0]); //
	}

	/**
	 * 
	 * @param _node
	 *            取得某一个结点下所有子结,可以指定是否包含自己，结果返回所有子节点的主键
	 * @param _containSelf
	 * @return
	 */
	public String[] getAllSubNodesFromOneNode(DefaultMutableTreeNode _node) {
		String[] allNodesIds = null;
		DefaultMutableTreeNode[] pathNodes = this.getOneNodeChildPathNodes(_node, true); //
		allNodesIds = new String[pathNodes.length];
		for (int i = 0; i < pathNodes.length; i++) {
			allNodesIds[i] = getModelItemVaueFromNode(pathNodes[i], this.templetVO.getTreepk()); // 取得树型结构主键的
		}
		return allNodesIds;
	}

	/**
	 * 取得选中结点的所有子孙数据VO
	 * 
	 * @return
	 */
	public BillVO[] getSelectedChildPathBillVOs() {
		DefaultMutableTreeNode currNode = this.getSelectedNode(); //
		if (currNode.isRoot()) { // 如果选中的是根结点,则返回所有数据VO
			return getAllBillVOs();
		}
		DefaultMutableTreeNode[] nodes = getSelectedChildPathNodes(); // 取得所子孙结点..
		ArrayList al_vos = new ArrayList(); //
		BillVO billVO = null;
		for (int i = 0; i < nodes.length; i++) {
			billVO = getBillVOFromNode(nodes[i]); //
			if (billVO != null) {
				al_vos.add(billVO); //
			}
		}
		return (BillVO[]) al_vos.toArray(new BillVO[0]); //
	}

	public DefaultMutableTreeNode[] getSelectedParentAndChildPathNodes() {
		TreePath path = jTree.getSelectionPath();
		if (path == null) {
			return null;
		}

		DefaultMutableTreeNode[] parentNodes = getSelectedParentPathNodes(); // 所有父亲
		DefaultMutableTreeNode[] childNodes = getSelectedChildPathNodes(); // 所有子孙

		DefaultMutableTreeNode[] allNodes = new DefaultMutableTreeNode[parentNodes.length + childNodes.length - 1];
		for (int i = 0; i < parentNodes.length; i++) {
			allNodes[i] = parentNodes[i];
		}

		for (int j = 1; j < childNodes.length; j++) { // 第一个跳过!!
			allNodes[parentNodes.length + j - 1] = childNodes[j]; //
		}
		return allNodes;
	}

	/**
	 * 取得选中结点的父亲与子孙的所有数据对象...
	 * 
	 * @return
	 */
	public BillVO[] getSelectedParentAndChildPathBillVOs() {
		TreePath path = jTree.getSelectionPath();
		if (path == null) {
			return null;
		}
		DefaultMutableTreeNode[] allNodes = getSelectedParentAndChildPathNodes(); // 取得所有父亲与儿子结点
		ArrayList al_vos = new ArrayList(); ////
		BillVO billVO = null;
		for (int i = 0; i < allNodes.length; i++) {
			billVO = getBillVOFromNode(allNodes[i]); //
			if (billVO != null) {
				al_vos.add(billVO); //
			}
		}
		return (BillVO[]) al_vos.toArray(new BillVO[0]); //
	}

	/**
	 * 递归遍历某结点的所有子结点!
	 * 
	 * @param _vector
	 *            向量
	 * @param node
	 *            ,某开始结点!
	 */
	private void visitAllNodes(Vector _vector, TreeNode node) {
		if (!_vector.contains(node)) { //如果还没有加入,则加入!
			_vector.add(node); // 加入该结点
		}
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) e.nextElement(); // 找到该儿子!!
				visitAllNodes(_vector, childNode); // 继续查找该儿子
			}
		}
	}

	public boolean isDragable() {
		return this.dragable; //
	}

	public void setDragable(boolean _dragable) {
		this.dragable = _dragable; //
	}

	/**
	 * 刷新所有结点数据
	 */
	public void refreshAllNodeVOFromModel() {
		DefaultMutableTreeNode[] allNodes = this.getAllNodes(); //
		for (int i = 0; i < allNodes.length; i++) {
			refreshNodeVOFromModel(allNodes[i]); //刷新某一个结点的数据
		}
	}

	/**
	 * 刷新某一个结点的数据
	 * @param _node
	 */
	public void refreshNodeVOFromModel(DefaultMutableTreeNode _node) {
		BillTreeNodeVO nodeVO = (BillTreeNodeVO) _node.getUserObject(); //
		refreshNodeVOFromModel(nodeVO); //
	}

	/**
	 * 刷新结点数据从模型中
	 * @param _nodeVO
	 */
	public void refreshNodeVOFromModel(BillTreeNodeVO _nodeVO) {
		int li_row = _nodeVO.getRowNo(); //
		if (li_row < 0) {
			return;
		}
		int li_index = findIndexInModel(this.templetVO.getTreeviewfield()); //哪一列
		if (li_index < 0) {
			return;
		}

		_nodeVO.setText(this.realStrData.getBodyData()[li_row][li_index]); //重新赋值

		int li_index_icon = findIndexInModel("iconname"); //图标处理
		if (li_index_icon >= 0) {
			_nodeVO.setIconName(this.realStrData.getBodyData()[li_row][li_index_icon]); //重新赋值
		}

	}

	/**
	 * 取得某一个结点上的BillVO
	 * @param _treeNode
	 * @return
	 */
	public BillVO getBillVOFromNode(DefaultMutableTreeNode _treeNode) {
		return getBillVOFromNode(_treeNode, this.getTempletVO());
	}

	/**
	 * 向某个结点中回写数据,回写某一列的值
	 * 因为有时直接在前台通过某种操作,比如直接提交SQL修改了库中的数据,同时修改了UI数据
	 * 这时需要回写Model，否则就会出现数据不一致的情况,比如下次再取BillVO时就还是旧的!!
	 * 原理就是根据结点数据找到行号，然后最终还是去处理realStrData.getBody()的那个二维数组
	 * @param _billVO
	 */
	public void setBillVO(DefaultMutableTreeNode _node, String _itemkey, String _value) {
		if (_node == null) {
			return;
		}
		BillTreeNodeVO nodeVO = (BillTreeNodeVO) _node.getUserObject(); //取得结点上绑定的数据...
		int li_row = nodeVO.getRowNo();
		if (li_row < 0) {
			return;
		}
		int li_index = findIndexInModel(_itemkey); //
		if (li_index < 0) {
			return;
		}
		this.realStrData.getBodyData()[li_row][li_index] = _value; //
		refreshNodeVOFromModel(nodeVO); //刷新结点的数据
	}

	/**
	 * 将BillVO中的数据回写到Model中
	 * 因为有时直接在前台通过某种操作,比如直接提交SQL修改了库中的数据,同时修改了UI数据
	 * 这时需要回写Model，否则就会出现数据不一致的情况,比如下次再取BillVO时就还是旧的!!
	 * @param _billVO
	 */
	public void setBillVO(DefaultMutableTreeNode _node, BillVO _billVO) {
		if (_node == null) {
			return;
		}
		BillTreeNodeVO nodeVO = (BillTreeNodeVO) _node.getUserObject(); //取得结点上绑定的数据...
		int li_row = nodeVO.getRowNo();
		if (li_row < 0) {
			return;
		}
		String[] keys = this.realStrData.getHeaderName(); //
		for (int i = 0; i < keys.length; i++) {
			if (_billVO.containsKey(keys[i])) {
				this.realStrData.getBodyData()[li_row][i] = _billVO.getStringValue(keys[i]); //
			}
		}
		refreshNodeVOFromModel(nodeVO); //刷新结点数据
	}

	/**
	 * 为当前选中结点设置新的BillVO值
	 * @param _billVO
	 */
	public void setBillVOForCurrSelNode(BillVO _billVO) {
		DefaultMutableTreeNode currnode = this.getSelectedNode(); //
		if (currnode == null) {
			return;
		}
		setBillVO(currnode, _billVO); //
	}

	/**
	 * 直接拿BillVO回写,通过BillVO中的ModelIndex来绑定
	 * 使用这种办法的前提是BillVO中预先已有ModelIndex值了,即这个BillVO是从树中取得的,而不是通过其他途径获得的,比如SQL查询到的,或者是从某个列表,卡片上得到的!
	 * @param _billVO
	 */
	public void setBillVO(BillVO _billVO) {
		if (_billVO == null) {
			return;
		}
		int li_row = _billVO.getModelRowNoInTree(); //在模型中的第几行
		if (li_row < 0) {
			logger.error("向树型控件中回写BillVO失败,因为BillVO中的ModelRowNoInTree无值."); //
			return;
		}
		String[] keys = this.realStrData.getHeaderName(); //
		for (int i = 0; i < keys.length; i++) {
			if (_billVO.containsKey(keys[i])) {
				this.realStrData.getBodyData()[li_row][i] = _billVO.getStringValue(keys[i]); //
			}
		}

		DefaultMutableTreeNode[] allNodes = getAllNodes(); //
		for (int i = 0; i < allNodes.length; i++) {
			if (((BillTreeNodeVO) allNodes[i].getUserObject()).getRowNo() == li_row) {
				refreshNodeVOFromModel(allNodes[i]); //
			}
		}

	}

	/**
	 * 回写一批BillVO,每个BillVO都必须有modelRowNoInTree值
	 * @param _billVOs
	 */
	public void setBillVOs(BillVO[] _billVOs) {
		String[] keys = this.realStrData.getHeaderName(); //
		DefaultMutableTreeNode[] allNodes = getAllNodes(); //取得所有结点,因为有可能会修改结点的值,即BillTreeNodeVO
		for (int i = 0; i < _billVOs.length; i++) {
			int li_row = _billVOs[i].getModelRowNoInTree(); //在模型中的第几行
			if (li_row < 0) {
				logger.error("向树型控件中回写BillVO失败,因为BillVO中的ModelRowNoInTree无值."); //
				return;
			}

			for (int j = 0; j < keys.length; j++) {
				if (_billVOs[i].containsKey(keys[j])) {
					this.realStrData.getBodyData()[li_row][j] = _billVOs[i].getStringValue(keys[j]); //
				}
			}

			for (int k = 0; k < allNodes.length; k++) {
				if (((BillTreeNodeVO) allNodes[k].getUserObject()).getRowNo() == li_row) {
					refreshNodeVOFromModel(allNodes[k]); //
				}
			}
		}

	}

	/**
	 * 将某一个结点上的值以HashVO的形式返回,它与BillVO杫比是轻量级的,所以性能更高!
	 * @param _treeNode
	 * @return
	 */
	public HashVO getHashVOFromNode(DefaultMutableTreeNode _treeNode) {
		BillTreeNodeVO nodeVO = (BillTreeNodeVO) _treeNode.getUserObject();
		if (nodeVO == null) {
			return null;
		}
		int li_row = nodeVO.getRowNo();
		if (li_row < 0) {
			return null;
		}

		String[] str_keys = this.realStrData.getHeaderName();
		String[] str_values = this.realStrData.getBodyData()[li_row];
		HashVO hvo = new HashVO(); //
		for (int i = 0; i < str_keys.length; i++) {
			hvo.setAttributeValue(str_keys[i], str_values[i]);
		}
		return hvo;
	}

	private BillVO getBillVOFromNode(DefaultMutableTreeNode _treeNode, Pub_Templet_1VO _Templet_1VO) {
		BillTreeNodeVO nodeVO = (BillTreeNodeVO) _treeNode.getUserObject(); //
		BillVO billVO = getBillVOFromNodeVO(nodeVO, _Templet_1VO);
		if (billVO != null) {
			appendUserObjectByVOFromNode(billVO, _treeNode); //
		}
		return billVO; //
	}

	/**
	 * 根据结点VO与模板VO创建一个BillVO返回
	 * @param pub_Templet_1VO 
	 * @param nodeVO 
	 * @return
	 */
	public BillVO getBillVOFromNodeVO(BillTreeNodeVO _nodeVO, Pub_Templet_1VO _Templet_1VO) {
		if (_nodeVO.getRowNo() < 0) { //如果行号小于0,则返回null.
			return null;
		}

		BillVO billVO = new BillVO(); //先创建,否则会出现空指针异常!!
		billVO.setModelRowNoInTree(_nodeVO.getRowNo()); //设置该数据对应Model数据中的第几行!在回写时非常有用!
		billVO.setTempletCode(templetVO.getTempletcode()); //模板编码
		billVO.setTempletName(templetVO.getTempletname()); //模板名称
		billVO.setQueryTableName(templetVO.getTablename()); //查询表名
		billVO.setSaveTableName(templetVO.getSavedtablename()); //保存表名
		billVO.setPkName(templetVO.getPkname()); // 主键字段名
		billVO.setSequenceName(templetVO.getPksequencename()); // 序列名

		int li_length = templetVO.getItemVos().length;
		// 所有ItemKey
		String[] all_Keys = new String[li_length + 1]; //
		all_Keys[0] = "" + _nodeVO.getRowNo(); // 行号
		for (int i = 1; i < all_Keys.length; i++) {
			all_Keys[i] = this.templetVO.getItemKeys()[i - 1];
		}

		//itemName
		String[] all_Names = new String[li_length + 1]; //
		all_Names[0] = " "; // 行号
		for (int i = 1; i < all_Names.length; i++) {
			all_Names[i] = this.templetVO.getItemNames()[i - 1];
		}

		//所有ItemType
		String[] all_Types = new String[li_length + 1]; //
		all_Types[0] = " "; // 行号
		for (int i = 1; i < all_Types.length; i++) {
			all_Types[i] = this.templetVO.getItemTypes()[i - 1];
		}

		//所有列类型
		String[] all_ColumnTypes = new String[li_length + 1]; //
		all_ColumnTypes[0] = "NUMBER"; // 行号
		for (int i = 1; i < all_ColumnTypes.length; i++) {
			all_ColumnTypes[i] = templetVO.getItemVos()[i - 1].getSavedcolumndatatype(); //
		}

		//是否需要保存
		boolean[] bo_isNeedSaves = new boolean[li_length + 1];
		bo_isNeedSaves[0] = false; // 行号
		for (int i = 1; i < bo_isNeedSaves.length; i++) {
			bo_isNeedSaves[i] = templetVO.getItemVos()[i - 1].isNeedSave();
		}

		billVO.setKeys(all_Keys); //
		billVO.setNames(all_Names); //
		billVO.setItemType(all_Types); // 控件类型!!
		billVO.setColumnType(all_ColumnTypes); // 数据库类型!!
		billVO.setNeedSaves(bo_isNeedSaves); // 是否需要保存!!

		//所有实际数据..
		Object[] obj_datas = new Object[li_length + 1]; // 
		obj_datas[0] = new RowNumberItemVO(RowNumberItemVO.INIT, _nodeVO.getRowNo()); //
		for (int i = 1; i < obj_datas.length; i++) {
			String str_item = getStringValueInModel(_nodeVO.getRowNo(), all_Keys[i]); //关键!!!即去数据组找到实际值!!
			if (str_item == null) { //如果为空,则返回空
				obj_datas[i] = null; //
			} else {
				if (all_Types[i].equals(WLTConstants.COMP_LABEL) || //
						all_Types[i].equals(WLTConstants.COMP_TEXTFIELD) || //
						all_Types[i].equals(WLTConstants.COMP_NUMBERFIELD) || //
						all_Types[i].equals(WLTConstants.COMP_PASSWORDFIELD) || //
						all_Types[i].equals(WLTConstants.COMP_TEXTAREA) || //
						all_Types[i].equals(WLTConstants.COMP_BUTTON)) {
					obj_datas[i] = new StringItemVO(str_item); //
				} else if (all_Types[i].equals(WLTConstants.COMP_COMBOBOX)) { //下拉框
					ComBoxItemVO matchVO = findComBoxItemVO(templetVO.getItemVo(all_Keys[i]).getComBoxItemVos(), str_item); //
					if (matchVO != null) {
						obj_datas[i] = matchVO;
					} else {
						obj_datas[i] = new ComBoxItemVO(str_item, null, str_item); // 下拉框VO
					}
				} else if (all_Types[i].equals(WLTConstants.COMP_REFPANEL) || //参照
						all_Types[i].equals(WLTConstants.COMP_REFPANEL_TREE) || //
						all_Types[i].equals(WLTConstants.COMP_REFPANEL_MULTI) || //
						all_Types[i].equals(WLTConstants.COMP_REFPANEL_CUST) || //
						all_Types[i].equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET) || //
						all_Types[i].equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || //
						all_Types[i].equals(WLTConstants.COMP_REFPANEL_REGFORMAT) || //
						all_Types[i].equals(WLTConstants.COMP_REFPANEL_REGEDIT)) {
					obj_datas[i] = new RefItemVO(str_item, null, str_item); //
				} else if (all_Types[i].equals(WLTConstants.COMP_DATE) || all_Types[i].equals(WLTConstants.COMP_DATETIME)) { //日历与时间
					obj_datas[i] = new RefItemVO(str_item, null, str_item); //
				} else if (all_Types[i].equals(WLTConstants.COMP_BIGAREA) || //
						all_Types[i].equals(WLTConstants.COMP_FILECHOOSE) || //
						all_Types[i].equals(WLTConstants.COMP_COLOR) || //
						all_Types[i].equals(WLTConstants.COMP_CALCULATE) || //
						all_Types[i].equals(WLTConstants.COMP_PICTURE) || //
						all_Types[i].equals(WLTConstants.COMP_LINKCHILD) //
				) {
					obj_datas[i] = new RefItemVO(str_item, null, str_item); //
				} else if (all_Types[i].equals(WLTConstants.COMP_EXCEL)) { //Excel
					obj_datas[i] = new RefItemVO(str_item, null, (str_item.indexOf("#") > 0 ? str_item.substring(str_item.indexOf("#") + 1, str_item.length()) : "点击查看Excel数据")); //
				} else if (all_Types[i].equals(WLTConstants.COMP_IMPORTCHILD)) { //导入子表!!
					obj_datas[i] = new RefItemVO(str_item, null, str_item); //
				} else if (all_Types[i].equals(WLTConstants.COMP_OFFICE)) { //Excel
					if (str_item.contains("_") || !str_item.contains(".")) {
						obj_datas[i] = new RefItemVO(str_item, null, "点击查看"); //
					} else {
						String name = str_item.substring(0, str_item.lastIndexOf("."));
						String type = str_item.substring(str_item.lastIndexOf("."));
						obj_datas[i] = new RefItemVO(str_item, null, getTBUtil().convertHexStringToStr(name) + type); //
					}
				} else {
					obj_datas[i] = new StringItemVO(str_item); //
				}
			}
		}
		billVO.setDatas(obj_datas); //设置真正的数据!!!!
		billVO.setToStringFieldName(templetVO.getTreeviewfield()); //
		billVO.setVirtualNode(_nodeVO.isVirtualNode()); //是否是虚拟VO,树型控件中后来增加了虚拟结点的概念,所以BillVO也有该概念!然后在参照查询条件返回时,如果是虚拟结点,则跳过计算!!!
		return billVO;
	}

	/**
	 * 取得当前选中的所有结点
	 * 
	 * @return
	 */
	public DefaultMutableTreeNode[] getSelectedNodes() {
		TreePath[] paths = jTree.getSelectionPaths();
		if (paths == null) {
			return null;
		}

		DefaultMutableTreeNode[] nodes = new DefaultMutableTreeNode[paths.length];
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
		}
		return nodes;
	}

	/**
	 * 取得选中的结点的BillVO
	 * @return
	 */
	public BillVO getSelectedVO() {
		TreePath path = jTree.getSelectionPath();
		if (path == null) {
			return null;
		}
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		BillTreeNodeVO nodeVO = (BillTreeNodeVO) node.getUserObject(); //
		if (nodeVO.isVirtualNode()) { //如果是虚拟结点?
		}
		BillVO billVO = getBillVOFromNode(node); //根据结点数据与模板定义的数据,创建一个BillVO返回
		return billVO; //
	}

	// 取得选中行的BillVOs,即多选时
	public BillVO[] getSelectedVOs() {
		TreePath[] path = jTree.getSelectionPaths();
		if (path == null) {
			return null;
		}

		ArrayList al_BillVOs = new ArrayList(); //
		DefaultMutableTreeNode[] nodes = new DefaultMutableTreeNode[path.length];
		BillVO billVO = null;
		for (int i = 0; i < path.length; i++) {
			nodes[i] = (DefaultMutableTreeNode) path[i].getLastPathComponent();
			billVO = getBillVOFromNode(nodes[i]); //为该结点创建BillVO
			if (billVO != null) {
				appendUserObjectByVOFromNode(billVO, nodes[i]); //补上用户信息!!!
				al_BillVOs.add(billVO); //
			}
		}
		return (BillVO[]) al_BillVOs.toArray(new BillVO[0]); //
	}

	//为BillVO加上一些用户信息
	private void appendUserObjectByVOFromNode(BillVO _billVO, DefaultMutableTreeNode _node) {
		_billVO.setUserObject("mylevel", _node.getLevel()); //为什么要加上层次
		_billVO.setUserObject("myisleaf", new Boolean(_node.isLeaf()));
		String[] str_path = getOneNodeRootPathToStrings(_node, false); //
		_billVO.setUserObject("$ParentPathName", str_path[0]); //用-连接的
		_billVO.setUserObject("$ParentPathNames", str_path[1]); //用分号分隔的
		_billVO.setUserObject("$parentpathids", str_path[2]); //用分号分隔的
	}

	/**
	 * 选中的树!
	 * @return
	 */
	public TreePath getSelectionPath() {
		return jTree.getSelectionPath(); //
	}

	/**
	 * 根据主键寻找某一个结点.
	 * @param _id
	 * @return
	 */
	public DefaultMutableTreeNode findNodeByKey(String _id) {
		DefaultMutableTreeNode[] nodes = this.getAllNodes();
		BillVO billlVO = null; //
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].isRoot()) {
				continue;
			}
			String str_itemValue = getModelItemVaueFromNode(nodes[i], this.templetVO.getTreepk()); //
			if (str_itemValue != null && str_itemValue.equals(_id)) {
				return nodes[i];
			}
		}
		return null;
	}

	/**
	 * 根据主键寻找某一个结点.
	 * @param _id
	 * @return
	 */
	public DefaultMutableTreeNode[] findNodeByViewName(String _name, boolean _isPrefix) {
		ArrayList al_nodes = new ArrayList(); //
		DefaultMutableTreeNode[] nodes = this.getAllNodes(); //取得所有结点
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].isRoot()) {
				continue;
			}

			Object obj = nodes[i].getUserObject(); //
			if (obj != null && obj.toString() != null) {
				if (_isPrefix) {
					if (obj.toString().indexOf(_name) == 0) {
						al_nodes.add(nodes[i]);
					}
				} else {
					if (obj.toString().indexOf(_name) >= 0) {
						al_nodes.add(nodes[i]);
					}
				}
			}
		}
		return (DefaultMutableTreeNode[]) al_nodes.toArray(new DefaultMutableTreeNode[0]); //
	}

	/**
	 * 跳转到某一个结点.
	 * @param _node
	 */
	public void scrollToOneNode(DefaultMutableTreeNode _node) {
		if (_node != null) {
			TreeNode[] nodes = getJTreeModel().getPathToRoot(_node);
			TreePath path = new TreePath(nodes);
			jTree.makeVisible(path);
			jTree.scrollPathToVisible(path);
			jTree.setSelectionPath(path); //
			jTree.repaint(); //
		}
	}

	/**
	 * 寻找并跳转到某一个结点
	 * @param _id
	 */
	public void findNodeByKeyAndScrollTo(String _id) {
		DefaultMutableTreeNode node = findNodeByKey(_id); //
		scrollToOneNode(node); //
	}

	/**
	 * 根据主键寻找某一批结点 【杨科/2012-08-30】
	 */
	public DefaultMutableTreeNode[] findNodesByKeys(String[] _ids) {
		ArrayList al_nodes = new ArrayList();
		DefaultMutableTreeNode[] nodes = this.getAllNodes();
		BillVO billlVO = null;
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].isRoot()) {
				continue;
			}
			String str_itemValue = getModelItemVaueFromNode(nodes[i], this.templetVO.getTreepk());
			for (int j = 0; j < _ids.length; j++) {
				if (str_itemValue != null && str_itemValue.equals(_ids[j])) {
					al_nodes.add(nodes[i]);
				}
			}
		}
		return (DefaultMutableTreeNode[]) al_nodes.toArray(new DefaultMutableTreeNode[0]);
	}

	/**
	 * 跳转到某一批结点 【杨科/2012-08-30】
	 */
	public void scrollToNodes(DefaultMutableTreeNode[] _nodes) {
		ArrayList al_paths = new ArrayList();
		for (int i = 0; i < _nodes.length; i++) {
			if (_nodes[i] != null) {
				TreeNode[] nodes = getJTreeModel().getPathToRoot(_nodes[i]);
				TreePath path = new TreePath(nodes);
				//jTree.makeVisible(path);
				//jTree.scrollPathToVisible(path);
				if (isChecked) {
					((BillTreeDefaultMutableTreeNode) _nodes[i]).setChecked(true);
				}
				al_paths.add(path);
			}
		}

		if (al_paths.size() > 0) { //
			TreePath[] allSelPaths = (TreePath[]) al_paths.toArray(new TreePath[0]); //
			if (al_paths.size() <= 20) { //如果只有少量的结点,则选择所有
				jTree.setSelectionPaths(allSelPaths);
			} else { //如果有许多,则只选择第一个!
				jTree.setSelectionPaths(new TreePath[] { allSelPaths[0] });
			}
		}
		jTree.repaint();
	}

	/**
	 * 寻找并跳转到某一批结点 【杨科/2012-08-30】
	 */
	public void findNodesByKeysAndScrollTo(String[] _ids) {
		DefaultMutableTreeNode[] nodes = findNodesByKeys(_ids);
		scrollToNodes(nodes); //
	}

	private void showPopMenu(Component _compent, int _x, int _y) {
		int li_count = ((JTree) _compent).getSelectionCount(); //
		if (li_count <= 1) {
			TreePath parentPath = ((JTree) _compent).getClosestPathForLocation(_x, _y);
			((JTree) _compent).setSelectionPath(parentPath); //
		}
		if (popmenu_header != null) { //如果已
			if (text_dirpath.isVisible()) {
				item_showDirTree.setText(UIUtil.getLanguage("隐藏直系路径"));
			} else {
				item_showDirTree.setText(UIUtil.getLanguage("显示直系路径"));
			}
		} else {
			popmenu_header = new JPopupMenu(); //
			popmenu_header.setLightWeightPopupEnabled(false); //防止被HtmlPanel挡住.
			item_quickLocate = new JMenuItem(UIUtil.getLanguage("结果中查找"), UIUtil.getImage("office_115.gif")); // 显示SQL
			item_quickqueryall = new JMenuItem(UIUtil.getLanguage("快速穿透查询"), UIUtil.getImage("office_163.gif")); //快速穿透查询
			item_refresh = new JMenuItem(UIUtil.getLanguage("刷新"), UIUtil.getImage("office_191.gif")); // 刷新

			JMenu menu_expand_collapse = new JMenu("展开/收缩"); //
			menu_expand_collapse.setIcon(UIUtil.getImage("office_008.gif")); //
			item_expand = new JMenuItem(UIUtil.getLanguage("全部展开")); // 展开所有结点
			item_collapse = new JMenuItem(UIUtil.getLanguage("全部收缩")); // 收缩所有结点

			if (this.templetVO.getTreeisonlyone()) { // 如果是只允许同果看一层,则全部展开功能禁用掉
				item_expand.setEnabled(false);
			} else {
				item_expand.setEnabled(true);
			}

			menu_expand_collapse.add(item_expand); //
			menu_expand_collapse.add(item_collapse); //

			JMenu menu_others = new JMenu("其他操作"); //
			item_updatelinkcode = new JMenuItem(UIUtil.getLanguage("重置LinkCode")); //
			item_updatelinkname = new JMenuItem(UIUtil.getLanguage("重置LinkName")); //后来为了更方便显示路径,扩展人linkName机制
			item_updateParentid = new JMenuItem(UIUtil.getLanguage("重置Parentid")); //
			item_checkOneField = new JMenuItem("对某字段大检查"); //比如机构类型!!

			item_updateParentid.setToolTipText("将树型结构的id重新弄成从1..开始,只有在做安装数据时用到"); //
			item_checkOneField.setToolTipText("比如对机构类型检查是否为空,路径上的各项配置是否满足某种顺序规则!比如将一个一级分行配在支行下面!"); //
			menu_others.add(item_updatelinkcode); //
			menu_others.add(item_updatelinkname); //
			menu_others.add(item_updateParentid); //
			menu_others.add(item_checkOneField); //

			item_showDirTree = new JMenuItem(UIUtil.getLanguage("显示直系路径"), UIUtil.getImage("office_164.gif")); // 显示直接路径
			item_computechildnodecount = new JMenuItem(UIUtil.getLanguage("统计子结点数量"), UIUtil.getImage("office_053.gif")); // 显示SQL
			item_modifytemplet = new JMenuItem(UIUtil.getLanguage("模板编辑"), UIUtil.getImage("office_127.gif")); //模板编辑
			item_showSQL = new JMenuItem(UIUtil.getLanguage("查看结点信息"), UIUtil.getImage("office_162.gif")); // 显示SQL

			item_addNode = new JMenuItem(UIUtil.getLanguage("增加结点")); // 显示SQL
			item_delNode = new JMenuItem(UIUtil.getLanguage("删除结点")); // 显示SQL

			item_quickLocate.setPreferredSize(new Dimension(125, 19));
			item_quickqueryall.setPreferredSize(new Dimension(125, 19));
			item_refresh.setPreferredSize(new Dimension(125, 19));

			item_expand.setPreferredSize(new Dimension(70, 19));
			item_collapse.setPreferredSize(new Dimension(70, 19));
			item_modifytemplet.setPreferredSize(new Dimension(125, 19)); //模板编辑
			item_showDirTree.setPreferredSize(new Dimension(125, 19));
			item_updatelinkcode.setPreferredSize(new Dimension(125, 19)); //

			item_computechildnodecount.setPreferredSize(new Dimension(125, 19));
			item_showSQL.setPreferredSize(new Dimension(125, 19));
			item_addNode.setPreferredSize(new Dimension(125, 19));
			item_delNode.setPreferredSize(new Dimension(125, 19));

			item_quickLocate.addActionListener(this); //
			item_quickqueryall.addActionListener(this); //
			item_refresh.addActionListener(this);
			item_expand.addActionListener(this); //
			item_collapse.addActionListener(this); //
			item_computechildnodecount.addActionListener(this); //
			item_modifytemplet.addActionListener(this); //
			item_showDirTree.addActionListener(this); //
			item_updatelinkcode.addActionListener(this); //
			item_updatelinkname.addActionListener(this); //
			item_updateParentid.addActionListener(this); //
			item_checkOneField.addActionListener(this); //
			item_showSQL.addActionListener(this); //
			item_addNode.addActionListener(this); //
			item_delNode.addActionListener(this); //

			//自定义菜单
			JMenuItem[] appMenuItems = getAppMenuItems();
			if (appMenuItems != null && appMenuItems.length != 0) {
				for (int i = 0; i < appMenuItems.length; i++) {
					if (appMenuItems[i].getText() == null || appMenuItems[i].getText().trim().equals("")) {
						popmenu_header.addSeparator(); //分隔线
					} else {
						popmenu_header.add(appMenuItems[i]); //加入自定义菜单
					}
				}

				popmenu_header.addSeparator(); //分隔线
			}

			//标准菜单
			popmenu_header.add(item_quickLocate); //结果中查找
			popmenu_header.add(menu_expand_collapse); //
			popmenu_header.add(item_computechildnodecount); //计算子结点数量
			popmenu_header.add(item_showDirTree); //显示直系路径框
			if (ClientEnvironment.isAdmin()) { //
				popmenu_header.add(item_quickqueryall); //
				popmenu_header.add(item_refresh); //
				if (this.templetVO.getTreeisshowtoolbar()) {//如果没有工具条，也不可以重置linkcode
					popmenu_header.add(menu_others); //
				}
				//popmenu_header.add(item_resetchildseq); // 显示SQL..
				popmenu_header.add(item_modifytemplet); //模板编辑...
				popmenu_header.add(item_showSQL); // 显示SQL..
			}

			// popmenu_header.add(item_addNode); //增加结点
			// popmenu_header.add(item_delNode); //删除结点		

		}
		//在弹出之前有机会处理哪些菜单有效或无效.
		for (int i = 0; i < al_BillTreebeBorePopMenuListeners.size(); i++) {
			ActionListener actionListener = (ActionListener) al_BillTreebeBorePopMenuListeners.get(i); //
			actionListener.actionPerformed(new ActionEvent(this, 0, "popmenu")); //
		}
		popmenu_header.show(_compent, _x, _y); //
	}

	/**
	 * 快速穿透查询!!
	 * 后来觉得树型也像表一样,也要有快速穿透才方便 xch/2012-02-23
	 */
	private void onQuickQueryAll() {
		if (templetVO.getTablename() == null || templetVO.getTablename().trim().equals("")) {
			MessageBox.show(this, "模板定定义的查询表名为空,不能进行此操作!"); //
			return;
		}
		queryData("select * from " + templetVO.getTablename()); //直接查询,连模板中定义的过滤条件都不参与计算!!
	}

	public void refreshTree() {
		if (this.str_realsql == null) {
			MessageBox.show(this, "当前SQL为空,可能还没有查询过一次,不能进行此操作!"); //
			return;
		}
		this.getJTree().clearSelection();
		this.clearSelection();//刷新时需要将选择清空【李春娟/2013-11-22】
		queryData(str_realsql); // 刷新数据!!
	}

	public void myExpandAll() {
		expandAll(jTree, true);
	}

	public void myCollapseAll() {
		expandAll(jTree, false);
	}

	public void modifyTemplet2() {
		try {
			new MetaDataUIUtil().modifyTemplet(this, this.getTempletVO().getBuildFromType(), this.getTempletVO().getBuildFromInfo(), this.getTempletVO().getTempletcode(), this.getTempletVO().getTempletname(), false, null);
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
			return; //
		}
	}

	/**
	 * 展开某一个结点!
	 * 
	 * @param _node
	 */
	public void expandOneNode(TreeNode _node) {
		TreePath path = new TreePath(getJTreeModel().getPathToRoot(_node));
		expandAll(jTree, path, true);
		jTree.makeVisible(path);
		jTree.setSelectionPath(path);
		jTree.scrollPathToVisible(path);
	}

	public void expandOneNodeOnly(TreeNode _node) {
		TreePath path = new TreePath(getJTreeModel().getPathToRoot(_node));
		jTree.expandPath(path); //
	}

	public void expandOneNodeByKey(String _id) {
		TreeNode node = findNodeByKey(_id); //
		if (node != null) {
			expandOneNode(node); //
		}
	}

	public void expandAll(JTree tree, boolean expand) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		expandAll(tree, new TreePath(root), expand);
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

	/**
	 * 返回某一个结点,从根结点到该结点的路径,用->拼起来.
	 * @param _node
	 * @return
	 */
	public String getOneNodeRootPathToString(DefaultMutableTreeNode _node, boolean _isHasRoot) {
		String[] str_name = getOneNodeRootPathToStrings(_node, _isHasRoot); //
		return str_name[0]; //
	}

	public String[] getOneNodeRootPathToStrings(DefaultMutableTreeNode _node, boolean _isHasRoot) {
		TreeNode[] pathNodes = _node.getPath(); //
		StringBuffer sb_text1 = new StringBuffer(); //
		StringBuffer sb_text2 = new StringBuffer(); //
		StringBuffer sb_text3 = new StringBuffer(); //
		sb_text3.append(";"); //
		for (int i = 0; i < pathNodes.length; i++) {
			if (!_isHasRoot) {
				if (((DefaultMutableTreeNode) pathNodes[i]).isRoot()) {
					continue;
				}
			}
			String str_nodeName = ((DefaultMutableTreeNode) pathNodes[i]).getUserObject() + ""; //
			String str_id = getModelItemVaueFromNode((DefaultMutableTreeNode) pathNodes[i], this.templetVO.getTreepk()); //取得树型结构主键的
			sb_text1.append(str_nodeName);
			sb_text2.append(str_nodeName);
			sb_text3.append(str_id);
			if (i != pathNodes.length - 1) {
				sb_text1.append("-"); //以前是->,后来改成-了
			}
			sb_text2.append(";");
			sb_text3.append(";");
		}
		return new String[] { sb_text1.toString(), sb_text2.toString(), sb_text3.toString() }; //
	}

	/**
	 * 计算结点数量,包括目录结点数,叶子结点数,各层的结点数.
	 */
	private void onComputeChildNodeCount() {
		DefaultMutableTreeNode[] selNodes = this.getSelectedNodes(); //
		if (selNodes == null) {
			MessageBox.show(this, "请选中一个结点"); //
			return;
		}
		StringBuilder str_selNodeText = new StringBuilder(); //
		for (int i = 0; i < selNodes.length; i++) {
			str_selNodeText.append("" + selNodes[i].getUserObject());
			if (i != selNodes.length - 1) {
				str_selNodeText.append(";"); //
			}
		}
		int li_leafcount = 0;
		int li_dircount = 0;
		int[] li_levelCount = new int[100]; //
		int li_maxlevel = 0; //
		for (int k = 0; k < selNodes.length; k++) {
			DefaultMutableTreeNode[] childNodes = this.getOneNodeChildPathNodes(selNodes[k], true); //
			for (int i = 1; i < childNodes.length; i++) {
				int li_level = childNodes[i].getLevel();
				if (li_level > li_maxlevel) {
					li_maxlevel = li_level;
				}
				li_levelCount[li_level] = li_levelCount[li_level] + 1;
				if (childNodes[i].isLeaf()) {
					li_leafcount++;
				} else {
					li_dircount++;
				}
			}
		}

		StringBuilder sb_msg = new StringBuilder(); //
		sb_msg.append("选中的结点共有[" + (li_leafcount + li_dircount) + "]个子结点(包括自己),其中:\r\n"); //
		sb_msg.append("目录结点有[" + li_dircount + "]个\r\n"); //
		sb_msg.append("叶子结点有[" + li_leafcount + "]个\r\n\r\n"); //
		if (jTree.isRootVisible()) {
			for (int i = 0; i <= li_maxlevel; i++) {
				sb_msg.append("第[" + (i + 1) + "]层结点有[" + li_levelCount[i] + "]个\r\n"); //
			}
		} else {
			for (int i = 1; i <= li_maxlevel; i++) {
				sb_msg.append("第[" + (i) + "]层结点有[" + li_levelCount[i] + "]个\r\n"); //
			}
		}
		MessageBox.showTextArea(this, "选中结点【" + str_selNodeText.toString() + "】的所有子结点统计情况", sb_msg.toString(), 400, 250); //
	}

	/**
	 * 增加弹出右键前的动作处理.
	 */
	public void addBillTreeBeforePopMenuActionListener(ActionListener _actionListener) {
		al_BillTreebeBorePopMenuListeners.add(_actionListener); //
	}

	// 增加监听器
	public void addBillTreeSelectListener(BillTreeSelectListener _listener) {
		v_BillTreeSelectListeners.add(_listener);
	}

	// 移去所有选择监听器!
	public void removeAllBillTreeSelectListener() {
		v_BillTreeSelectListeners.removeAllElements(); //
	}

	//增加移动监听器
	public void addBillTreeMovedListener(BillTreeMoveListener _listener) {
		v_BillTreeMovedListeners.add(_listener); //
	}

	/**
	 * 快速定位,点击右键进行
	 */
	private void onQuickLocateByMouseRightClicked() {
		DefaultMutableTreeNode[] allNodes = this.getAllNodes(); //
		boolean showToString = getTBUtil().getSysOptionBooleanValue("树型面板结果中查找是否显示ToString的列", false);//【李春娟/2015-02-19】
		String toStringKey = templetVO.getTostringkey();//toString的列
		String treeViewField = templetVO.getTreeviewfield();//树型面板显示列
		//模板中设置了ToString的列，并且与显示列不同，则显示ToString的列并可查询【李春娟/2016-02-19】
		if (showToString && toStringKey != null && !"".equals(toStringKey.trim()) && !toStringKey.equals(treeViewField)) {//
			toStringKey = toStringKey.trim();
			showToString = true;
		} else {
			showToString = false;
		}
		String[][] str_data = null;
		if (showToString) {
			str_data = new String[allNodes.length][6];
		} else {
			str_data = new String[allNodes.length][5];
		}
		int li_maxlevel = 0;
		for (int i = 0; i < allNodes.length; i++) {
			StringBuilder sb_path = new StringBuilder(); //
			TreeNode[] pathNodes = allNodes[i].getPath(); //
			for (int j = 0; j < pathNodes.length; j++) {
				sb_path.append("" + pathNodes[j]); //
				if (j != pathNodes.length - 1) {
					sb_path.append(" → "); //
				}
			}

			str_data[i][0] = "" + (i + 1); //
			str_data[i][1] = "" + (allNodes[i].getUserObject() == null ? "" : allNodes[i].getUserObject()); //
			if (showToString) {
				BillVO billvo = getBillVOFromNode(allNodes[i]);
				if (billvo != null) {
					String toStringName = billvo.getStringValue(toStringKey);
					if (toStringName != null && !"".equals(toStringName)) {
						str_data[i][2] = toStringName;
					} else {
						str_data[i][2] = "";
					}
				} else {
					str_data[i][2] = "";
				}
				str_data[i][3] = sb_path.toString(); //
				str_data[i][4] = "" + (allNodes[i].getLevel() + 1); //
				str_data[i][5] = allNodes[i].isLeaf() ? "是" : ""; //
			} else {
				str_data[i][2] = sb_path.toString(); //
				str_data[i][3] = "" + (allNodes[i].getLevel() + 1); //
				str_data[i][4] = allNodes[i].isLeaf() ? "是" : ""; //
			}

			if (allNodes[i].getLevel() > li_maxlevel) {
				li_maxlevel = allNodes[i].getLevel();
			}
		}
		QuickFilterDialog dialog = new QuickFilterDialog(this, str_data, (li_maxlevel + 1), showToString); //
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {
			this.clearSelection(); //
			this.myCollapseAll(); //
			int[] li_rows = dialog.getSelectedRows(); //
			for (int i = 0; i < li_rows.length; i++) {
				addSelected(allNodes[li_rows[i]]); //
			}
			TreeNode[] nodes = getJTreeModel().getPathToRoot(allNodes[li_rows[0]]);
			TreePath path = new TreePath(nodes);
			jTree.makeVisible(path);
			jTree.scrollPathToVisible(path);
			jTree.repaint(); //
		}
	}

	/**
	 * 过按钮点击的快速查询
	 * @param _isShiftDown 是否按了Shift键
	 */
	private void onQuickLocateByBtnClicked() {
		String str_text = textField_quickLocate.getText(); //
		if (str_text == null || str_text.trim().equals("")) {
			MessageBox.show(this, "请输入关键字查询条件!"); //
			return;
		}

		boolean bo_isPrefix = false; ////是否必须以之为开头..
		if (str_text.startsWith("~")) {
			bo_isPrefix = true; //
			str_text = str_text.substring(1, str_text.length()); //
		}

		DefaultMutableTreeNode[] quickLocateNodes = findNodeByViewName(str_text, bo_isPrefix); //
		if (quickLocateNodes == null || quickLocateNodes.length == 0) {
			MessageBox.show(this, "没有找到匹配的数据!"); //
			return;
		}

		currQuickLocateNode = quickLocateNodes[0]; //当前结点是第一个
		scrollToOneNode(quickLocateNodes[0]); //滚动到对应的记录!!
		if (quickLocateNodes.length == 1) {
			MessageBox.show(this, "共找到[1]个满足条件的记录!"); //
		} else {
			MessageBox.show(this, "共找到[" + quickLocateNodes.length + "]个满足条件的记录!\r\n点击右边的上下按钮可以继续查找..."); //
		}
	}

	/**
	 * 移到下一个结点..
	 */
	private void onMoveNextQuickLocateNode() {
		String str_text = textField_quickLocate.getText(); //
		if (str_text == null || str_text.trim().equals("")) {
			MessageBox.show(this, "请输入关键字查询条件!"); //
			return;
		}

		boolean bo_isPrefix = false; ////是否必须以之为开头..
		if (str_text.startsWith("~")) {
			bo_isPrefix = true; //
			str_text = str_text.substring(1, str_text.length()); //
		}

		DefaultMutableTreeNode[] quickLocateNodes = findNodeByViewName(str_text, bo_isPrefix); //找出所有满足条件的结点
		if (quickLocateNodes == null || quickLocateNodes.length == 0) {
			MessageBox.show(this, "没有找到匹配的数据!"); //
			return;
		}

		for (int i = 0; i < quickLocateNodes.length; i++) {
			if (quickLocateNodes[i] == currQuickLocateNode) {
				if (i == quickLocateNodes.length - 1) {
					MessageBox.show(this, "已是最后一个满足条件的结点了!"); //
				} else {
					currQuickLocateNode = quickLocateNodes[i + 1]; //当前结点是第一个
					scrollToOneNode(quickLocateNodes[i + 1]); //滚动到对应的记录!!
				}
				return;
			}
		}
	}

	/**
	 * 移到前一个结点....
	 */
	private void onMovePreviouQuickLocateNode() {
		String str_text = textField_quickLocate.getText(); //
		if (str_text == null || str_text.trim().equals("")) {
			MessageBox.show(this, "请输入关键字查询条件!"); //
			return;
		}

		boolean bo_isPrefix = false; ////是否必须以之为开头..
		if (str_text.startsWith("~")) {
			bo_isPrefix = true; //
			str_text = str_text.substring(1, str_text.length()); //
		}

		DefaultMutableTreeNode[] quickLocateNodes = findNodeByViewName(str_text, bo_isPrefix); //找出所有满足条件的结点
		if (quickLocateNodes == null || quickLocateNodes.length == 0) {
			MessageBox.show(this, "没有找到匹配的数据!"); //
			return;
		}

		for (int i = 0; i < quickLocateNodes.length; i++) {
			if (quickLocateNodes[i] == currQuickLocateNode) {
				if (i == 0) {
					MessageBox.show(this, "已是第一个满足条件的结点了!"); //
				} else {
					currQuickLocateNode = quickLocateNodes[i - 1]; //当前结点是第一个
					scrollToOneNode(quickLocateNodes[i - 1]); //滚动到对应的记录!!
				}
				return;
			}
		}
	}

	/**
	 * 重置所有子结点的顺序,以12345排序
	 */
	public void resetChildSeq() {
		TreeNode selNode = this.getSelectedNode();
		resetChildSeq(selNode); //
	}

	private void resetChildSeq(TreeNode selNode) {
		try {
			if (selNode == null) {
				return;
			}
			int li_childcount = selNode.getChildCount(); //
			ArrayList al_sqls = new ArrayList(); //
			VectorMap vm_setNode = new VectorMap(); //
			BillVO billVO = null;
			for (int i = 0; i < li_childcount; i++) { //所有儿子
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) selNode.getChildAt(i);
				billVO = getBillVOFromNode(childNode); //
				if (billVO != null) {
					if (!("" + (i + 1)).equals(billVO.getStringValue(templetVO.getTreeseqfield()))) { //如果不等,则重置
						String str_pk_value = billVO.getStringValue(this.templetVO.getPkname());
						al_sqls.add("update " + templetVO.getSavedtablename() + " set " + templetVO.getTreeseqfield() + "=" + (i + 1) + " where " + templetVO.getPkname() + "='" + str_pk_value + "'"); //
						BillTreeNodeVO nodeVO = (BillTreeNodeVO) childNode.getUserObject(); //
						vm_setNode.put(Integer.valueOf(nodeVO.getRowNo()), "" + (i + 1)); //
					}
				}
			}
			UIUtil.executeBatchByDS(this.getDataSourceName(), al_sqls); //
			Object[] li_keys = (Object[]) vm_setNode.getKeys(); //
			for (int i = 0; i < li_keys.length; i++) {
				this.realStrData.getBodyData()[(Integer) li_keys[i]][findIndexInModel(templetVO.getTreeseqfield())] = (String) vm_setNode.get(li_keys[i]); //修改实际数据
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * 显示当前结点信息与SQL
	 */
	private void showSQL() {
		StringBuffer sb_text = new StringBuffer(); //
		sb_text.append("模板编码=【" + this.getTempletVO().getTempletcode() + "】\r\n");
		sb_text.append("模板名称=【" + this.getTempletVO().getTempletname() + "】\r\n");
		sb_text.append("模板查询表名=【" + this.getTempletVO().getTablename() + "】\r\n");
		sb_text.append("保存的表名=【" + this.getTempletVO().getSavedtablename() + "】\r\n");
		if (ClientEnvironment.getInstance().isAdmin()) {
			sb_text.append("当前查询的SQL:【" + this.str_realsql + "】\r\n\r\n"); //
		}

		sb_text.append("数据权限过滤说明:【" + this.str_resAccessInfo + "】\r\n"); //
		sb_text.append("数据权限过滤SQL:【" + this.str_resAccessSQL + "】\r\n"); //

		sb_text.append("\r\n");
		if (this.getSelectedNode() != null) { //如果有选中的结点
			DefaultMutableTreeNode[] nodes = getSelectedParentPathNodes(); //
			StringBuilder sb_path = new StringBuilder(); //
			int li_beginLevel = 0;
			if (jTree.isRootVisible()) {
				li_beginLevel = 0;
			} else {
				li_beginLevel = 1;
			}
			for (int i = li_beginLevel; i < nodes.length; i++) { //
				sb_path.append("" + nodes[i]); //
				if (i != nodes.length - 1) {
					sb_path.append("->"); //
				}
			}

			sb_text.append("当前结点路径:【" + sb_path.toString() + "】\r\n"); //
			BillTreeNodeVO treeNodeVO = this.getSelectedTreeNodeVO();
			sb_text.append("选中结点的BillTreeNodeVO数据:【" + treeNodeVO.getRowNo() + "," + treeNodeVO.getText() + "】\r\n");
			sb_text.append("选中结点的BillVO数据:\r\n");
			BillVO billVO = this.getSelectedVO();
			if (billVO != null) {
				String[] str_keys = billVO.getKeys(); //
				String[] str_names = billVO.getNames();
				for (int i = 1; i < str_keys.length; i++) {
					sb_text.append(str_keys[i] + "(" + str_names[i] + ")=[" + billVO.getStringValue(str_keys[i]) + "]\r\n"); //
				}

				HashMap billVOUserMap = billVO.getUserObjectMap(); //有自定义对象!!!
				sb_text.append("\r\n该BillVO的userObject值<需要通过billVO.getUserObject(\"key\")才能得到>:\r\n");
				if (billVOUserMap.size() > 0) {
					Iterator its = billVOUserMap.keySet().iterator(); //
					while (its.hasNext()) {
						String str_key = (String) its.next(); //
						String str_value = "" + billVOUserMap.get(str_key); //
						sb_text.append("[" + str_key + "]=[" + str_value + "]\r\n"); //
					}
				} else {
					sb_text.append("该BillVO的userObject值为空!\r\n");
				}
			} else {
				sb_text.append("【null】\r\n");
			}

		}
		MessageBox.showTextArea(this, "选中结点的信息", sb_text.toString(), 500, 300); //
	}

	private FrameWorkMetaDataServiceIfc getFWMetaService() throws WLTRemoteException, Exception {
		FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) RemoteServiceFactory.getInstance().lookUpService(FrameWorkMetaDataServiceIfc.class);
		return service;
	}

	//
	public void updateUI() {
		super.updateUI(); //
		if (jTree != null) {
			jTree.updateUI(); //以前用的是WLTTreeUI,但后来发现没有必要,因为只要把树搞成透明,将整个BillTreePanel弄成渐变就行了! 但这样一来，刷新时必须调用这个方法,结果忘记了,导致角色分配权限时有bug了!!
			((BasicTreeUI) jTree.getUI()).setCollapsedIcon(ImageIconFactory.getCollapsedIcon()); //
			((BasicTreeUI) jTree.getUI()).setExpandedIcon(ImageIconFactory.getExpandedIcon()); //
		}
	}

	
	public JTree getJTree() {
		return jTree;
	}

	public DefaultTreeModel getJTreeModel() {
		return (DefaultTreeModel) jTree.getModel();
	}

	public Pub_Templet_1_ItemVO[] getTempletItemVOs() {
		return templetItemVOs;
	}

	public Pub_Templet_1VO getTempletVO() {
		return templetVO;
	}

	public String[] getIconNames() {
		return iconNames;
	}

	public void setIconNames(String[] iconNames) {
		this.iconNames = iconNames;
	}

	public String getIconName(int _level) {
		if (this.iconNames == null) {
			return null;
		}

		if (this.iconNames.length >= _level) {
			return iconNames[_level - 1];
		}
		if (iconNames.length == 1)
			return iconNames[0];
		return null;
	}

	public String getLeafNodeIcon() {
		if (leaficonNames == null) {
			if (iconNames != null && iconNames.length > 0)
				return iconNames[iconNames.length - 1];
		}
		return leaficonNames;
	}

	public void setLeafIcon(String icon) {
		leaficonNames = icon;
	}

	public void addNode(BillVO _billVO) {
		this.addNode(_billVO, true);
	}

	/**
	 * 在选中的结点上增加一个新结点
	 * @param _billVO
	 * @param autoSelected 添加一个新节点是否自动选择。郝明2012-07-11
	 */
	public void addNode(BillVO _billVO, boolean autoSelected) {
		DefaultMutableTreeNode parentNode = getSelectedNode();
		if (parentNode == null) {
			return;
		}
		addNode(parentNode, _billVO, autoSelected); //
	}

	public void addNode(DefaultMutableTreeNode _parentNode, BillVO _billVO) {
		this.addNode(_parentNode, _billVO, true);
	}

	/**
	 * 增加某个结点,数据类型必须是BillVO
	 */
	public void addNode(DefaultMutableTreeNode _parentNode, BillVO _billVO, boolean autoSelected) {
		String str_viewText = _billVO.getStringValue(this.getTempletVO().getTreeviewfield()); //
		String str_iconName = null; //
		_billVO.setToStringFieldName(this.templetVO.getTreeviewfield()); // 设置显示名称!!
		if (_billVO.containsKey("iconname")) { //图标名称
			str_iconName = _billVO.getStringValue("iconname"); //
		}

		String[][] str_data = this.realStrData.getBodyData(); //
		int li_col_count = realStrData.getHeaderName().length; //
		int li_row_count = str_data.length; //

		String[] str_newRowData = new String[li_col_count]; //新的一行数据
		for (int j = 0; j < li_col_count; j++) {
			str_newRowData[j] = _billVO.getStringValue(realStrData.getHeaderName()[j]); //为新的一行数据赋值
		}

		String[][] str_newDatas = new String[li_row_count + 1][li_col_count]; //
		for (int i = 0; i < li_row_count; i++) {
			for (int j = 0; j < li_col_count; j++) {
				str_newDatas[i][j] = str_data[i][j];
			}
		}

		for (int j = 0; j < li_col_count; j++) {
			str_newDatas[li_row_count][j] = str_newRowData[j];
		}

		this.realStrData.setBodyData(str_newDatas); //为Model设置新数据

		BillTreeNodeVO nodeVO = new BillTreeNodeVO(li_row_count, str_viewText, str_iconName); //创建一个结点对象,行号就是加1的
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(nodeVO);
		if (this.getJTree().getCellEditor() instanceof BillTreeCheckCellEditor) {//如果该树有勾选框，则创建BillTreeDefaultMutableTreeNode 对象，否则勾选时报错【李春娟/2012-03-05】
			newNode = new BillTreeDefaultMutableTreeNode(nodeVO);
		}

		getJTreeModel().insertNodeInto(newNode, _parentNode, _parentNode.getChildCount()); //
		if (autoSelected) {
			expandOneNode(newNode);
		}
		//getJTree().expandPath(getSelectedPath()); // 展开该结点
	}

	public void addNodes(BillVO[] _billVOs) {
		this.addNodes(_billVOs, true);
	}

	public void addNodes(BillVO[] _billVOs, boolean autoSelected) {
		for (int i = 0; i < _billVOs.length; i++) { //遍历所有结点
			String str_pk_value = _billVOs[i].getPkValue(); //
			if (findNodeByKey(str_pk_value) != null) { //如果已有该结点,则跳过.
				continue; //..
			}
			String str_parentid = _billVOs[i].getStringValue(this.templetVO.getTreeparentpk()); //找到父亲结点
			if (str_parentid != null) {
				DefaultMutableTreeNode parentNode = findNodeByKey(str_parentid); //找到父结点
				if (parentNode != null) {
					addNode(parentNode, _billVOs[i], autoSelected); //
				} else {
					addNode(rootNode, _billVOs[i], autoSelected); //
				}
			} else { //
				addNode(rootNode, _billVOs[i], autoSelected); //
			}
		}

		jTree.repaint(); //
	}

	/**
	 * 删除当前结点,只是从页面上删除!!
	 * 删除结点可以不能处理Model中的值,因为余下的结点的行号仍会对应上Model数组中的实际行
	 */
	public void delCurrNode() {
		DefaultMutableTreeNode node = getSelectedNode();
		if (node == null) {
			return;
		}
		jTree.cancelEditing();//sunfujun/20120607/不加此句在有勾选框即可编辑的时候会报错
		getJTreeModel().removeNodeFromParent(node); //
	}

	/**
	 * 清空树...
	 */
	public void clearTree() {
		int li_childcount = rootNode.getChildCount();
		for (int i = 0; i < li_childcount; i++) {
			MutableTreeNode childNode = (MutableTreeNode) rootNode.getChildAt(i); //
			getJTreeModel().removeNodeFromParent(childNode); //
		}
		this.realStrData = null; //把Model中的值也清空
	}

	/**
	 * 清空选择,即将选中的树型结点取消选择!
	 */
	public void clearSelection() {
		jTree.clearSelection(); //
	}

	private TBUtil getTBUtil() {
		if (tBUtil == null) {
			tBUtil = new TBUtil();
		}
		return tBUtil; //
	}

	public JMenuItem[] getAppMenuItems() {
		JMenuItem[] menuItems = (JMenuItem[]) al_appMenuItems.toArray(new JMenuItem[0]);
		return menuItems;
	}

	/**
	 * 设置所有用户菜单,在右键时会弹出..
	 * @param _appMenuItems
	 */
	public void setAppMenuItems(JMenuItem[] _appMenuItems) {
		al_appMenuItems.clear(); //
		for (int i = 0; i < _appMenuItems.length; i++) {
			al_appMenuItems.add(_appMenuItems[i]); //
		}
	}

	/**
	 * 增加右键菜单...
	 * @param _appMenuItems
	 */
	public void addAppMenuItems(JMenuItem[] _appMenuItems) {
		for (int i = 0; i < _appMenuItems.length; i++) {
			al_appMenuItems.add(_appMenuItems[i]); //
		}
	}

	public BillFormatPanel getLoaderBillFormatPanel() {
		return loaderBillFormatPanel;
	}

	public void setLoaderBillFormatPanel(BillFormatPanel loaderBillFormatPanel) {
		this.loaderBillFormatPanel = loaderBillFormatPanel;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public DefaultMutableTreeNode getRootNode() {
		return rootNode;
	}

	public JPanel getToolKitBtnPanel() {
		return toolKitBtnPanel;
	}

	public TableDataStruct getRealStrData() {
		return realStrData;
	}

	public void setRealStrData(TableDataStruct realStrData) {
		this.realStrData = realStrData;
	}

	public JMenuItem getItem_updatelinkcode() {
		return item_updatelinkcode;
	}

	public boolean isDefaultLinkedCheck() {
		return isDefaultLinkedCheck;
	}

	public void setDefaultLinkedCheck(boolean isDefaultLinkedCheck) {
		this.isDefaultLinkedCheck = isDefaultLinkedCheck;
	}

	//数据权限过滤后,机构过滤情况下,树型展示时存在一个虚拟结点的问题!该变量就是存储机构虚拟结点的容器!!
	public HashSet getVirtualCorpIdsHst() {
		return virtualCorpIdsHst;
	}

	class MyDragTree extends JTree implements DragGestureListener, DragSourceListener {
		DragSource dragSource;

		public MyDragTree(DefaultTreeModel _model) {
			super(_model); //

			dragSource = new DragSource();
			dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);

		}

		public void dragGestureRecognized(DragGestureEvent evt) {
			Transferable t = new StringSelection("aString");
			dragSource.startDrag(evt, DragSource.DefaultCopyDrop, t, this);
		}

		public void dragEnter(DragSourceDragEvent evt) {
			// Called when the user is dragging this drag source and enters
			// the drop target.
		}

		public void dragOver(DragSourceDragEvent evt) {
			System.out.println("over");
			// over the drop target.
		}

		public void dragExit(DragSourceEvent evt) {
			// Called when the user is dragging this drag source and leaves
			// the drop target.
		}

		public void dropActionChanged(DragSourceDragEvent evt) {
			// Called when the user changes the drag action between copy or move.
		}

		public void dragDropEnd(DragSourceDropEvent evt) {
			if (!isDragable()) {
				return; //
			}

			// Called when the user finishes or cancels the drag operation.
			//System.out.println("下放" + this.getSelectionPath());

			Point pt = evt.getLocation();
			SwingUtilities.convertPointFromScreen(pt, this);
			TreePath parentPath = this.getClosestPathForLocation(pt.x, pt.y);
			if (parentPath != null) {
				//System.out.println(parentPath); //
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent(); //

				TreePath selPath = this.getSelectionPath(); //
				if (selPath == null) {
					return;
				}
				DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();
				if (parentNode == selNode) {
					//System.out.println("不能移到自己身上!!!"); //
					return;
				}

				if (JOptionPane.showConfirmDialog(BillTreePanel.this, "您是否真的想将结点 " + selPath + " 移至结点 " + parentPath + " 下?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
					return;
				}

				if (parentNode.isRoot()) {
					BillVO selBillVO = getBillVOFromNode(selNode); //
					String str_selPKValue = selBillVO.getStringValue(templetVO.getPkname()); //
					try {
						String str_sql = "update " + templetVO.getSavedtablename() + " set " + templetVO.getTreeparentpk() + "=null where " + templetVO.getPkname() + "='" + str_selPKValue + "'";
						UIUtil.executeUpdateByDS(getDataSourceName(), str_sql); //
					} catch (Exception e) {
						e.printStackTrace(); //
					}
				} else {
					BillVO parentBillVO = getBillVOFromNode(parentNode); //
					BillVO selBillVO = getBillVOFromNode(selNode); //
					String str_parentTreePK = parentBillVO.getStringValue(templetVO.getTreepk()); //
					String str_selPKValue = selBillVO.getStringValue(templetVO.getPkname()); //

					if (str_parentTreePK.equals(str_selPKValue)) {
						return; //如果两者相同,肯定不能处理!!!
					}

					try {
						String str_sql = "update " + templetVO.getSavedtablename() + " set " + templetVO.getTreeparentpk() + "='" + str_parentTreePK + "' where " + templetVO.getPkname() + "='" + str_selPKValue + "'";
						UIUtil.executeUpdateByDS(getDataSourceName(), str_sql); //
					} catch (Exception e) {
						e.printStackTrace(); //
					}
				}

				selNode.removeFromParent(); //
				parentNode.insert(selNode, 0); //

				resetChildSeq(parentNode); //
				nodeMoveChanged(); //调用所有移动监听者
				jTree.expandPath(parentPath);
				this.updateUI(); //
				jTree.updateUI();
				((BasicTreeUI) jTree.getUI()).setCollapsedIcon(ImageIconFactory.getCollapsedIcon());//移动后重置图标【李春娟/2014-03-10】
				((BasicTreeUI) jTree.getUI()).setExpandedIcon(ImageIconFactory.getCollapsedIcon());
			}
		}
	}

	/**
	 * 做删除操作，返回删除的所有节点id【李春娟/2014-03-05】
	 */
	public ArrayList doDelete(boolean _isQuiet) {
		ArrayList deleteids = new ArrayList();
		if (this.getSelectedNode() == null) {
			MessageBox.show(this, "请选择一个结点进行删除操作!"); //
			return deleteids;
		}
		BillTreeNodeVO treeNodeVO = this.getSelectedTreeNodeVO();//以前是直接删除选中节点，但子节点不删除，导致子节点再次加载时找不到父节点于是显示在树的第一级的错误，现改为一并删除其子孙节点！【李春娟/2012-02-29】
		BillVO[] childVOs = this.getSelectedChildPathBillVOs(); //取得所有选中的
		if (!_isQuiet && MessageBox.showConfirmDialog(this, "您确定要删除记录【" + treeNodeVO.toString() + "】吗?\r\n这将一并删除其下共【" + childVOs.length + "】条子孙记录,请务必谨慎操作!", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return deleteids;
		}
		Vector v_sqls = new Vector(); //菜单删除
		for (int i = 0; i < childVOs.length; i++) {
			v_sqls.add("delete from " + childVOs[i].getSaveTableName() + " where " + childVOs[i].getPkName() + "='" + childVOs[i].getPkValue() + "'"); //
			deleteids.add(childVOs[i].getPkValue());
		}
		try {
			UIUtil.executeBatchByDS(null, v_sqls);
		} catch (Exception e) {
			e.printStackTrace();
		} //执行数据库操作!!
		this.delCurrNode(); //
		this.updateUI();
		return deleteids;
	}
}
