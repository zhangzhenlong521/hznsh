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
 * �������
 * @author xch
 *
 */
public class BillTreePanel extends BillPanel implements ActionListener {

	private static final long serialVersionUID = 8051676743971788439L;

	private Pub_Templet_1VO templetVO = null; // Ԫԭģ������
	private Pub_Templet_1_ItemVO[] templetItemVOs = null; //Ԫԭģ���ӱ�

	private TableDataStruct realStrData = null; //ʵ�ʴ洢������,Ϊ���������,ֻ�����ַ���,Ȼ���ڿͻ��˸�����Ҫ��ʱ������Ҫ��BillVO,
	//��ǰ���ȴ���BillVO[],Ȼ��ÿ�����洢��UserObject����BillVO,��API��װ�Ͽ�����ȷ��,���������Ͽ�ȴ����������һ���̶�ʱ���ܽ���,����ͬʱ������������ʱ,��ʹ�ڴ�ﵽ50��M,ֱ�ӵ���MemoeryOut!!���ǿͻ��˲���ͬʱ���ض��̫������!!

	private HashMap mapModelIndex = new HashMap(); //������¼ʵ��������ĳһ�е�ʵ��λ��,����ÿһ�ζ�ȥ��һ�α������

	private String[] iconNames = null;
	private String leaficonNames = null;
	private TBUtil tBUtil = null; //ת������!!

	private DefaultMutableTreeNode rootNode = null;
	private JTree jTree = null;

	private JPanel btnPanel = null; //��ť���
	private BillButtonPanel billTreeBtnPanel; //��ť���
	private JPanel quickLocatePanel = null; //��ť���

	private JButton btn_moveup, btn_movedown; //

	private JTextField textField_quickLocate; //���ٶ�λ
	private DefaultMutableTreeNode currQuickLocateNode = null; // 

	private JButton btn_quickLocate; //���ٲ�ѯ
	private JButton btn_MoveToNextQuickLocateNode, btn_MoveToPreviousQuickLocateNode; //���ٲ�ѯʱ���������Ƶİ�ť..

	private JScrollPane mainScrollPane = null;
	private JTextField text_dirpath; //
	private JLabel label_helpinfo = null; // 

	private JPopupMenu popmenu_header = null; //�Ҽ�������

	private String custCondition = null; //
	private String str_realsql = null;
	private String str_resAccessInfo = null; //����Ȩ�޹���˵��
	private String str_resAccessSQL = null; //����Ȩ�޹��˵�ʵ��SQL

	private Vector v_BillTreeSelectListeners = new Vector();
	private Vector v_BillTreeMovedListeners = new Vector(); //�ƶ����ʱ�������¼��洢��
	private ArrayList al_BillTreebeBorePopMenuListeners = new ArrayList();

	private JMenuItem item_quickqueryall, item_refresh, item_expand, item_collapse, item_modifytemplet, item_showDirTree, item_updatelinkcode, item_updatelinkname, item_updateParentid, item_checkOneField, item_quickLocate, item_showSQL, item_computechildnodecount, item_addNode, item_delNode;
	private ArrayList al_appMenuItems = new ArrayList(); //�Ҽ����ο���Ӧ�ò˵�

	private boolean dragable = false; //
	private BillFormatPanel loaderBillFormatPanel = null;

	private boolean isChecked = false;
	private BillTreeCheckCellRender cellRender = null;
	private BillTreeCheckCellEditor cellEditor = null; //

	private Vector v_billTreeCheckListener = new Vector(); //
	private JPanel toolKitBtnPanel = null;

	private boolean isDefaultLinkedCheck = true; //�Ƿ�Ĭ�Ϲ�����ѡ!!
	private boolean isTriggerSelectChangeEvent = true; //�Ƿ񴥷�ѡ���¼�?

	private HashSet virtualCorpIdsHst = new HashSet(); //����Ȩ�޹���ʱ,���ڴ洢����������Ķ���!

	private Logger logger = WLTLogger.getLogger(BillTreePanel.class); //

	private BillTreePanel() {
	}

	public BillTreePanel(String _templetcode) {
		if (_templetcode.indexOf(".") > 0) { //����Ǹ�����,���������и�".",��������Ϊ�Ǹ�����!!��ֱ�ӷ������
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
	 * ֱ����Pub_Templet_1VO����������壬������Pub_Templet_1VO���ڿͻ��˻�õ��������������һ��Զ�̵��á����/2012-02-24��
	 * @param _templetvo
	 */
	public BillTreePanel(Pub_Templet_1VO _templetvo) {
		try {
			templetVO = _templetvo; // ȡ��Ԫԭģ������
			templetItemVOs = templetVO.getItemVos(); //����..
			initialize(); // ��ʼ��!!
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BillTreePanel(AbstractTMO _templetVO) {
		init_2(_templetVO);
	}

	/**
	 * ֱ��ͨ��ServerTMO����..
	 * @param _serverTMO
	 */
	public BillTreePanel(ServerTMODefine _serverTMO) {
		try {
			templetVO = UIUtil.getPub_Templet_1VO(_serverTMO); //
			templetItemVOs = templetVO.getItemVos(); //����..
			initialize(); // ��ʼ��!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	public void init_1(String _templetcode) {
		try {
			templetVO = UIUtil.getPub_Templet_1VO(_templetcode); // ȡ��Ԫԭģ������
			templetItemVOs = templetVO.getItemVos(); // ����.....
			initialize(); // ��ʼ��!!
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init_2(AbstractTMO _templetVO) {
		try {
			templetVO = UIUtil.getPub_Templet_1VO(_templetVO); // ȡ��Ԫԭģ������
			templetItemVOs = templetVO.getItemVos(); //����..
			initialize(); // ��ʼ��!!
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//����ҳ��!!!
	private final void initialize() {
		setDragable(false); //Ĭ�ϲ����϶�,����ǳ�Σ��!!!
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
		text_dirpath = new JTextField("ֱ��·��:[]"); //
		text_dirpath.setForeground(new Color(0, 128, 192)); //
		text_dirpath.setHorizontalAlignment(JTextField.LEFT); //
		text_dirpath.setBorder(BorderFactory.createEmptyBorder()); //
		text_dirpath.setEditable(false); //
		text_dirpath.setOpaque(false); //
		text_dirpath.setVisible(false); //Ĭ�ϲ���ʾ!
		panel.add(text_dirpath, BorderLayout.NORTH); //

		String str_helpinfo = "��ʾ:ѡ��ĳһ�ڵ�,����Ҽ���:����в���,ȫ��չ��/����..�ȹ���,��֪Ϥ!";
		label_helpinfo = new JLabel(str_helpinfo); //WLTLabel(str_helpinfo, BackGroundDrawingUtil.HORIZONTAL_FROM_MIDDLE); //
		label_helpinfo.setToolTipText(str_helpinfo); //
		label_helpinfo.setBorder(BorderFactory.createEmptyBorder()); //
		label_helpinfo.setForeground(new Color(0, 128, 192)); //
		label_helpinfo.setVisible(false); //Ĭ�ϲ���ʾ!
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
	 * ���ٲ�ѯ���
	 * @return
	 */
	public JPanel getQuickLocatePanel() {
		if (quickLocatePanel != null) {
			return quickLocatePanel; //
		}

		toolKitBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 2)); //
		toolKitBtnPanel.setOpaque(false); //
		if (this.templetVO.getTreeisshowtoolbar()) { //�����ʾ������ sunfujun/���ؼ��������ŵ���������
			JLabel label_locate = new JLabel("�ؼ���", SwingConstants.RIGHT); //
			label_locate.setPreferredSize(new Dimension(57 + LookAndFeel.getFONT_REVISE_SIZE() * 3, 20)); //

			textField_quickLocate = new JTextField(); //
			textField_quickLocate.setPreferredSize(new Dimension(45, 20)); //
			//		textField_quickLocate.setBorder(BorderFactory.createLineBorder(new Color(175, 175, 175), 1)); //

			btn_quickLocate = new WLTButton("����"); //
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

			btn_MoveToPreviousQuickLocateNode.setToolTipText("����������һ��"); ////
			btn_MoveToNextQuickLocateNode.setToolTipText("����������һ��"); ////

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

			//����/����������ť

			btn_moveup = new WLTButton(UIUtil.getImage("up1.gif")); //
			btn_movedown = new WLTButton(UIUtil.getImage("down1.gif")); //
			btn_moveup.setPreferredSize(new Dimension(20, 20)); //
			btn_movedown.setPreferredSize(new Dimension(20, 20)); //
			btn_moveup.setToolTipText("����˳��"); //
			btn_movedown.setToolTipText("����˳��"); //
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
	 * ��ť��,������Ҫ,�����Դ���Զ�����ģ����д���İ�ť,Ҳ���Դ洢ģ���ж���İ�ť!!
	 * �Ժ����е�ҵ���߼�����������İ�ť��ʵ��!! 
	 * @return
	 */
	public BillButtonPanel getBillTreeBtnPanel() {
		if (billTreeBtnPanel != null) {
			return billTreeBtnPanel;
		}

		billTreeBtnPanel = new BillButtonPanel(this.templetVO.getTreecustbtns(), this);
		billTreeBtnPanel.setOpaque(false); //
		billTreeBtnPanel.paintButton(); //����,����Ҫ����һ��,����ť������!!
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
	 * ����һ����ť.
	 * @param _btn
	 */
	public void addBillTreeButton(WLTButton _btn) {
		_btn.setBillPanelFrom(this); //
		getBillTreeBtnPanel().addButton(_btn); //
	}

	/**
	 * ����һ����ť
	 * @param _btns
	 */
	public void addBatchBillTreeButton(WLTButton[] _btns) {
		for (int i = 0; i < _btns.length; i++) {
			_btns[i].setBillPanelFrom(this); //
		}
		getBillTreeBtnPanel().addBatchButton(_btns);
	}

	/**
	 * ����һ����ť.
	 * @param _btn
	 */
	public void insertBillTreeButton(WLTButton _btn) {
		_btn.setBillPanelFrom(this); //
		getBillTreeBtnPanel().insertButton(_btn); //
	}

	/**
	 * ����һ����ť.
	 * @param _btn
	 */
	public void insertBatchBillTreeButton(WLTButton[] _btns) {
		for (int i = 0; i < _btns.length; i++) {
			_btns[i].setBillPanelFrom(this); //
		}
		getBillTreeBtnPanel().insertBatchButton(_btns); //
	}

	/**
	 * ���»��ư�ť
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
			moveUpNode(); //���ƽ��
		} else if (e.getSource() == btn_movedown) {
			moveDownNode(); //���ƽ��
		} else if (e.getSource() == btn_quickLocate) { //���ٶ�λ
			onQuickLocateByBtnClicked(); //��������
		} else if (e.getSource() == btn_MoveToNextQuickLocateNode) { //����..
			onMoveNextQuickLocateNode();
		} else if (e.getSource() == btn_MoveToPreviousQuickLocateNode) { //����..
			onMovePreviouQuickLocateNode(); //
		} else if (e.getSource() == item_quickLocate) {
			onQuickLocateByMouseRightClicked(); //����/��λ
		} else if (e.getSource() == item_quickqueryall) { //���ٴ�͸��ѯ,������������Ҳ���һ��,ҲҪ�п��ٴ�͸�ŷ��� ��xch/2012-02-23��
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
			modifyTemplet2(); //ģ��༭
		} else if (e.getSource() == item_showDirTree) {
			if (text_dirpath.isVisible()) {
				text_dirpath.setVisible(false); //
			} else {
				text_dirpath.setVisible(true); //
			}
			this.revalidate(); //
			this.repaint(); //
		} else if (e.getSource() == item_updatelinkcode) {
			updateAllLinkCode(); //��������LinkCode
		} else if (e.getSource() == item_updatelinkname) {
			updateAllLinkName(); //��������LinkName��xch/2012-04-24��
		} else if (e.getSource() == item_checkOneField) { //��ĳ�ֶδ���!
			onCheckOneFieldRule(); //
		} else if (e.getSource() == item_updateParentid) {
			updateAllParentIdByInit(); //��������Parentid
		} else if (e.getSource() == item_showSQL) {
			showSQL();
		} else if (e.getSource() == item_addNode) {
			addNode(null);
		} else if (e.getSource() == item_delNode) {
			delCurrNode();
		}
	}

	/**
	 * ����һ�����
	 */
	private void moveUpNode() {
		try {
			isTriggerSelectChangeEvent = false; //�Ƿ񴥷�ѡ���¼�
			DefaultMutableTreeNode myself = this.getSelectedNode(); //
			//�����ڳ�ʼ�������û��Ĭ��ѡ�нڵ�ʱ�Ŀ�ָ���쳣
			if (myself == null) {
				return;
			}
			DefaultMutableTreeNode father = (DefaultMutableTreeNode) myself.getParent(); //ȡ�ø���
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
			nodeMoveChanged(); //���������ƶ�������
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			isTriggerSelectChangeEvent = true;
		}

	}

	/**
	 * ��������LinkCode,��νlinkCode������ÿ4λ��ʾһ��,�������ɲ�ι�ϵ
	 */
	public void updateAllLinkCode() {
		try {
			if (JOptionPane.showConfirmDialog(this, "��ȷ��Ҫ���������������linkcode��?�⽫�޸����н���linkcodeֵ!", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}
			long ll_1 = System.currentTimeMillis(); //
			if (this.getTempletVO().containsItemKey("linkcode")) { //�����linkCode
				Vector v_sqls = new Vector(); //
				DefaultMutableTreeNode[] allNodes = this.getAllNodes(); //
				for (int i = 1; i < 20; i++) { //������20��
					boolean bo_iffind = false; //
					for (int j = 0; j < allNodes.length; j++) { //�������н��..
						if (allNodes[j].getLevel() == i) { //����ǵڼ���
							bo_iffind = true; //���ϱ��,��ʾ�Ѿ�������!
							DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) allNodes[j].getParent(); //ȡ�ø���!!!
							int li_index = parentNode.getIndex(allNodes[j]); //�ж����ڸ�����е�λ��
							String str_currlinkcode = ("" + (10000 + li_index + 1)).substring(1, 5); //�������,��λ����
							String str_parlinkcode = ""; //
							if (!parentNode.isRoot()) {
								str_parlinkcode = getBillVOFromNode(parentNode).getStringValue("linkcode");
							}

							String str_linkcode = str_parlinkcode + str_currlinkcode; //
							BillVO billVO = getBillVOFromNode(allNodes[j]); //
							String str_billvolinkcode = billVO.getStringValue("linkcode"); //
							if (str_billvolinkcode == null || !str_billvolinkcode.equals(str_linkcode)) { //�������ԭ��������Ϊ�ջ��߲������µ�ֵ
								v_sqls.add("update " + getTempletVO().getSavedtablename() + " set linkcode='" + str_linkcode + "' where id='" + billVO.getPkValue() + "'"); //
								billVO.setObject("linkcode", new StringItemVO(str_linkcode)); //
								setBillVO(allNodes[j], billVO); //
							}
						}
					}

					if (!bo_iffind) { //���ĳһ��һ��Ҳû����,���˳�ѭ��..
						break;
					}
				}
				UIUtil.executeBatchByDS(null, v_sqls); //
				long ll_2 = System.currentTimeMillis(); //
				MessageBox.show(this, "����LinkCode�ɹ�,һ���޸���[" + v_sqls.size() + "]����¼��LinkCodeֵ,����ʱ[" + (ll_2 - ll_1) + "]����!!"); //
			} else {
				MessageBox.show(this, "ģ����û�ж���linkcode�ֶ�,�޷��������ö���!���뱣֤�������ģ���ж�����Ϊlinkcode���ֶ�!"); //
			}
		} catch (Exception e) {
			MessageBox.showException(this, e); //
		}
	}

	/**
	 * ��������LinkName
	 */
	public void updateAllLinkName() {
		try {
			if (JOptionPane.showConfirmDialog(this, "��ȷ��Ҫ���������������LinkName��?�⽫�޸����н���LinkNameֵ!", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}
			long ll_1 = System.currentTimeMillis(); //
			boolean isFind = false; //�Ƿ���?
			Pub_Templet_1_ItemVO[] itemVOs = getTempletItemVOs();
			for (int i = 0; i < itemVOs.length; i++) {
				if (itemVOs[i].getItemkey().equalsIgnoreCase("linkname")) {
					isFind = true; //
					break; //
				}
			}
			if (!isFind) {
				MessageBox.show(this, "ģ�������û��linkname�ֶ�,���ܽ��д˲���!!"); //
				return; //
			}

			String str_tableName = getTempletVO().getSavedtablename(); //����!!
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
			MessageBox.show(this, str_msg + "\r\n�ܹ���ʱ[" + (ll_2 - ll_1) + "]"); //
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		}
	}

	//���ٽ�����id,parentid��ɴ�1��ʼ�ĳ�ʼ״̬,�ڵ�������ʱ�õ�Q
	private void updateAllParentIdByInit() {
		try {
			if (!MessageBox.confirm(this, "��ȷ��Ҫ��������ParentidΪ��ʼ״̬(��1��ʼ..)��?�⽫���ܵ�����������������!")) {
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
				String str_oldId = billVOs[i].getStringValue(str_idField); //ԭ����id
				map.put(str_oldId, "-" + (i + 1)); //����!!
			}
			ArrayList al_sqls = new ArrayList(); //
			for (int i = 0; i < billVOs.length; i++) {
				String str_oldId = billVOs[i].getStringValue(str_idField); //ԭ����id
				String str_newid = (String) map.get(str_oldId); //
				String str_sql = "update " + templetVO.getSavedtablename() + " set " + str_idField + "='" + str_newid + "'"; //
				String str_oldParentId = billVOs[i].getStringValue(str_parentidField); //ԭ����id
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
			MessageBox.show(this, "�������м�¼�ɹ�,�����´򿪸�ҳ��!"); //
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		}
	}

	/**
	 * ������ĳ���ֶι���!!
	 */
	private void onCheckOneFieldRule() {
		String str_demoField = "corptype"; //
		StringBuilder sb_demoRule = new StringBuilder(); //
		sb_demoRule.append("ȫ��-����-���в���-���в���-���в���\r\n"); //
		sb_demoRule.append("ȫ��-һ������-һ�����в���-һ�����в���-һ�����в���-һ�����в���\r\n"); //
		sb_demoRule.append("ȫ��-һ������-һ��֧��-һ��֧�в���-һ��֧�в���-һ��֧�в���-һ��֧�в���\r\n"); //
		sb_demoRule.append("ȫ��-һ������-��������-�������в���-�������в���-�������в���-�������в���\r\n"); //
		sb_demoRule.append("ȫ��-һ������-��������-����֧��-����֧�в���-����֧�в���-����֧�в���-����֧�в���\r\n"); //

		JLabel label_1 = new JLabel("�����ֶ���:", SwingConstants.RIGHT); //
		JTextField textField = new JTextField(str_demoField); //
		JLabel label_2 = new JLabel("������:", SwingConstants.RIGHT);
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

		if (JOptionPane.showConfirmDialog(this, panel, "����������", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.YES_OPTION) {
			return; //
		}

		String str_fieldName = textField.getText(); //
		String str_rule = textArea.getText(); //
		str_rule = str_rule.trim(); //
		str_rule = getTBUtil().replaceAll(str_rule, "\r", ""); //ȥ��\r
		String[] str_rules = getTBUtil().split(str_rule, "\n"); //�ָ�!!
		for (int i = 0; i < str_rules.length; i++) {
			str_rules[i] = str_rules[i].trim(); //
		}

		//��һ���ı�����һ�������ı���!!
		checkOneFieldRule(str_fieldName, str_rules); //
	}

	/**
	 * ���һ���ֶεĹ����Ƿ�����ĳ�ֹ���,���������corptype�ֶ�!
	 * ����ʱ�����������õĲ���
	 * ȫ��-����-���в���
	 * ȫ��-һ������-һ�����в���
	 * ȫ��-һ������-һ��֧��-һ��֧�в���
	 * ȫ��-һ������-����֧��-����֧�в���
	 */
	public void checkOneFieldRule(String _field, String[] _rules) {
		try {
			String str_tableName = getTempletVO().getSavedtablename(); //����!!
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
			text1.setText("�޸Ŀ�ֵ��SQL(�ֶ�������дֵ����ִ�и���):\r\n" + str_return[0]); //

			JTextArea text2 = new JTextArea(); //
			text2.setFont(LookAndFeel.font); //
			text2.setEditable(false);
			text2.setBackground(LookAndFeel.systembgcolor); //
			text2.setText("��Ϊ�и����ֵΪ��,��ɵĸ���·����ֵ����,�������ݿ��ܱ�Ϊ�յ����ݶ�,��Ϊ������һ��Ŀ¼����ֵΪ��!��֮,����޸Ŀ�ֵ,��һ��������:\r\n" + str_return[1]); //

			JTextArea text3 = new JTextArea(); //
			text3.setFont(LookAndFeel.font); //
			text3.setEditable(false);
			text3.setBackground(LookAndFeel.systembgcolor); //
			text3.setText("�޸�·�������Ե�SQL,ǿ������:��һ������ĩ��㲻��,��������ĳ��Ŀ¼��㲻��!��ʱ��SQL�Ͳ���ִ��,��Ҫ�ֹ�ȥ��!:\r\n" + str_return[2]); //

			JTextArea text4 = new JTextArea(); //
			text4.setFont(LookAndFeel.font); //
			text4.setEditable(false);
			text4.setBackground(LookAndFeel.systembgcolor); //
			text4.setText("��Ի��������ܲ���,ǿ������,��һ��׼,Ҫȷ�Ϻ����:\r\n" + str_return[3]); //

			JTabbedPane tabbed = new JTabbedPane(); //
			tabbed.addTab("�޸Ŀ�ֵ��SQL", new JScrollPane(text1)); //
			tabbed.addTab("·��������(��Ϊ��ֵ��ɵ�)", new JScrollPane(text2)); //
			tabbed.addTab("�޸�·�������Ե�SQL", new JScrollPane(text3)); //

			if (str_tableName.equalsIgnoreCase("pub_corp_dept") && _field.equalsIgnoreCase("corptype")) { //ֻ���ǻ����ż���
				tabbed.addTab("��Ի����Ĳ²�", new JScrollPane(text4)); //
			}

			BillDialog dialog = new BillDialog(this, "�����,����[" + str_tableName + "],�����ֶ�[" + _field + "]", 950, 700); //
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
			isTriggerSelectChangeEvent = false; //�Ƿ񴥷�ѡ���¼�
			DefaultMutableTreeNode myself = this.getSelectedNode(); //
			//�����ڳ�ʼ������ʱ��δ��Ĭ��ѡ�нڵ�ʱ�Ŀ�ָ���쳣
			if (myself == null) {
				return;
			}
			DefaultMutableTreeNode father = (DefaultMutableTreeNode) myself.getParent(); //
			if (father == null) {
				return;
			}
			int li_index = father.getIndex(myself); //
			if (li_index == father.getChildCount() - 1) {
				return; //��������һ����,��������
			}
			getJTreeModel().removeNodeFromParent(myself); //
			getJTreeModel().insertNodeInto(myself, father, li_index + 1); //
			setSelected(myself); //
			resetChildSeq(father); //
			nodeMoveChanged(); //���������ƶ�������
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

		rootNode = new BillTreeDefaultMutableTreeNode(new BillTreeNodeVO(-1, this.templetVO.getTempletname())); // ���������!!!

		if (templetVO.getTreeisshowtoolbar()) {
			jTree = new MyDragTree(new DefaultTreeModel(rootNode)); // ������!!
		} else {
			jTree = new JTree(new DefaultTreeModel(rootNode)); // ������!!
		}

		jTree.setBackground(LookAndFeel.treebgcolor); //defaultShadeColor1
		((BasicTreeUI) jTree.getUI()).setCollapsedIcon(ImageIconFactory.getCollapsedIcon()); //
		((BasicTreeUI) jTree.getUI()).setExpandedIcon(ImageIconFactory.getExpandedIcon()); //
		//jTree.setUI(new WLTTreeUI());
		jTree.setOpaque(false); //һ��Ҫ���͸��,������ٹ���ʱ����ְ���,������Ϊ����ĥ�úܾ�!!!!!!
		jTree.setShowsRootHandles(true); //
		jTree.setRowHeight(17); //
		jTree.setEnabled(true); //
		cellRender = new BillTreeCheckCellRender(isChecked); //
		jTree.setCellRenderer(cellRender); //���ù�ѡ��

		if (isChecked) {
			jTree.setEditable(true); //
			cellEditor = new BillTreeCheckCellEditor(jTree, cellRender, this); //
			jTree.setCellEditor(cellEditor); //
			jTree.setToolTipText("ע��:��סShift��������ѡ���ӽ��!"); //
		} else {
			jTree.setEditable(false); //
		}

		jTree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				if (evt.getButton() == MouseEvent.BUTTON3) {
					showPopMenu(evt.getComponent(), evt.getX(), evt.getY()); // �����˵�
				} else {
					super.mousePressed(evt); //
				}
			}
		});

		jTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent evt) {
				TreePath[] paths = evt.getPaths(); //ȡ������ѡ�е�
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[0].getLastPathComponent();
				onChangeSelectTree(node); // This
			}
		});

		//�ж��Ƿ�ֻ��ͬʱ��һ��!!��ʱΪ�˱������ݹ���Ϊ�˲����û�һ����,��Ҫ�����ֿ���!!!!���ֲ���!!!
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

		jTree.setRootVisible(templetVO.getTreeisshowroot().booleanValue()); // �����Ƿ���ʾ�����!!
		mainScrollPane = new JScrollPane(jTree);
		mainScrollPane.setOpaque(false); //
		mainScrollPane.getViewport().setOpaque(false); //
		//mainScrollPane.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210), 1)); //
		mainScrollPane.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, new Color(210, 210, 210))); //
		return mainScrollPane;
	}

	/**
	 * �������Ƿ�ɶ�ѡ�༭
	 * @param _checked
	 */
	public void reSetTreeChecked(boolean _checked) {
		this.isChecked = _checked; //
		cellRender = new BillTreeCheckCellRender(isChecked); //
		jTree.setCellRenderer(cellRender); //���ù�ѡ��
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

	//���ݱ��ģ�����õ��ֶν�������ѯ��
	public String getItems() {
		if (!"pub_corp_dept".equalsIgnoreCase(templetVO.getSavedtablename())) { //Ŀǰֻ�ж�pub_corp_dept
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

		String str_cutcondition = getCustCondition(); //�ͻ��Զ�������.
		if (str_cutcondition != null && !str_cutcondition.trim().equals("")) {
			str_sql = str_sql + " and (" + str_cutcondition + ") "; //�ͻ��Զ�������
		}
		//str_sql = str_sql + " order by " + templetVO.getTreeseqfield();

		String str_resAccessSQLCons = getResAccesSQLCondition(); //Ȩ�޹��˵�SQL����!!
		if (str_resAccessSQLCons != null && !str_resAccessSQLCons.trim().equals("")) {
			str_sql = str_sql + " and (" + str_resAccessSQLCons + ") "; //�ͻ��Զ�������
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
	 * �õ�����Ȩ�޹�������
	 * 
	 * @return
	 */
	public String getDataconstraint() {
		if (templetVO.getDataconstraint() == null || templetVO.getDataconstraint().trim().equals("null") || templetVO.getDataconstraint().trim().equals("")) {
			return null; // Ĭ������Դ
		} else {
			return "" + new JepFormulaParseAtUI(null).execFormula(templetVO.getDataconstraint()); //
		}
	}

	public String getResAccesSQLCondition() {
		//�����ǿ�м�������Ȩ�޶����SQL
		if (this.templetVO.getDatapolicy() != null && !this.templetVO.getDatapolicy().trim().equals("")) { //�����������Ȩ�޶���!!���������Ȩ�޹���!!
			try {
				String[] str_accessCondition = getMetaDataService().getDataPolicyCondition(ClientEnvironment.getCurrLoginUserVO().getId(), this.templetVO.getDatapolicy(), this.templetVO.getDatapolicymap()); //
				str_resAccessInfo = str_accessCondition[0];
				String str_virtualCorpIds = null;
				if (str_accessCondition.length >= 3) {
					str_virtualCorpIds = str_accessCondition[2]; //
				}
				if (str_virtualCorpIds == null) { //�ж������Ҫ���������,��ƴ�ӡ�str_accessCondition[1] or str_accessCondition[2]��,Ȼ����������м�¼��������� 
					str_resAccessSQL = str_accessCondition[1]; //
					virtualCorpIdsHst.clear(); //��ջ��������������е�����!!!
				} else {
					str_resAccessSQL = str_accessCondition[1] + " or " + str_virtualCorpIds; //�����Ĳ�ѯ����!!��blcorp in ();
					virtualCorpIdsHst.clear(); //��ջ��������������е�����!!!
					String str_ids = str_virtualCorpIds.substring(str_virtualCorpIds.indexOf("(") + 1, str_virtualCorpIds.indexOf(")")); //�϶��Ǹ�in (...)�Ľṹ!!
					String[] str_idItems = getTBUtil().split(str_ids, ","); //
					for (int i = 0; i < str_idItems.length; i++) {
						virtualCorpIdsHst.add(str_idItems[i]); //����!!!
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
	 * ��������Ȩ�޲���!������Ȩ�޲��ն���ȵط�������������Ҫ�ֹ���������Ȩ�޲���!
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
	 * �ͻ��Զ�������...
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
	 * ��ʱ�����������в�ѯ������,Ȼ��ֱ�Ӹ���һ��HashVO����������!
	 * ���ַ�����ǰ�������HashVO������Ҫ��ģ���ж������͹�����id��parentid���ֶ�!!���������ǹ��첻������!
	 * @param _hvs
	 * @param _leafLevel
	 */
	public void queryDataByDS(HashVO[] _hvs, int _leafLevel) {
		this.str_realsql = null; //
		this.realStrData = null;
		mapModelIndex.clear(); //���
		rootNode.removeAllChildren(); // �����������

		realStrData = new TableDataStruct(); //
		if (_hvs != null && _hvs.length > 0) {
			String[] str_keys = _hvs[0].getKeys(); //
			realStrData.setHeaderName(str_keys); //��ͷ
			String[][] str_data = new String[_hvs.length][str_keys.length]; //
			for (int i = 0; i < _hvs.length; i++) {
				for (int j = 0; j < str_keys.length; j++) {
					str_data[i][j] = _hvs[i].getStringValue(str_keys[j]); ////
				}
			}
			realStrData.setBodyData(str_data); //
		}

		long ll_1 = System.currentTimeMillis(); //
		buidTree(_leafLevel); //����������
		long ll_2 = System.currentTimeMillis();

		logger.debug("����[" + (_hvs == null ? "0" : _hvs.length) + "]������,���������ʱ[" + (ll_2 - ll_1) + "]����!!"); //
	}

	/**
	 * ��ѯ����
	 * @param _datasourcename
	 * @param _sql
	 */
	public void queryDataByDS(final String _datasourcename, final String _sql, int _leafLevel) {
		this.str_realsql = _sql; //
		this.realStrData = null;
		mapModelIndex.clear(); //���
		rootNode.removeAllChildren(); // �����������

		long ll_0 = System.currentTimeMillis();
		try {
			this.realStrData = UIUtil.getMetaDataService().getBillTreeData(_datasourcename, _sql, this.templetVO.getParPub_Templet_1VO()); //
		} catch (Exception ex) {
			logger.error("��ѯ���Ϳؼ�����ʱ�����쳣:", ex); //
			return;
		}

		long ll_1 = System.currentTimeMillis(); //
		buidTree(_leafLevel); //����������
		long ll_2 = System.currentTimeMillis();

		logger.debug("ǰ̨�õ�[" + (realStrData.getBodyData() == null ? "0" : realStrData.getBodyData().length) + "]������,�����ݺ�ʱ[" + (ll_1 - ll_0) + "]����,�����ݹ��������ʱ[" + (ll_2 - ll_1) + "]����,����ʱ[" + (ll_2 - ll_0) + "]����!!"); //
	}

	/**
	 * ������������
	 * @param _leafLevel
	 */
	private void buidTree(int _leafLevel) {
		try {
			getTBUtil().sortTableDataStruct(this.realStrData, new String[][] { { templetVO.getTreeseqfield(), "N", "Y" } }); //����seq�ֶν�������,��������ͨ��LinkCode����������!
		} catch (NumberFormatException e) {
			getTBUtil().sortTableDataStruct(this.realStrData, new String[][] { { templetVO.getTreeseqfield(), "N", "N" } });
		}

		DefaultMutableTreeNode[] node_level_1 = new DefaultMutableTreeNode[realStrData.getBodyData().length]; //�������
		BillTreeNodeVO treeNodeVO = null;

		boolean isCorpDeptObject = "pub_corp_dept".equalsIgnoreCase(this.templetVO.getTablename()); //�ж��Ƿ��ǻ�������??
		boolean isVirtualNode = false; //
		HashMap map_parent = new HashMap();//�������нڵ�
		for (int i = 0; i < realStrData.getBodyData().length; i++) {
			String[] str_nodeTextAndIcon = getTreeNodeViewText(i); //
			if (isCorpDeptObject) { //�����ǻ�����������������ж�!!��Ϊ����Ȩ��Ŀǰ��ֻ�����֡���������/��Ա���ˡ�
				String str_nodePkValue = getTreeNodePkValue(i); //�ҵ��������������ֵ!!
				isVirtualNode = this.virtualCorpIdsHst.contains(str_nodePkValue); //��ִ������Ȩ��ʱ,����ǻ�������,����ܲ���������!����ʱ�ж�һ���Ƿ���������?
			}
			treeNodeVO = new BillTreeNodeVO(i, str_nodeTextAndIcon[0], str_nodeTextAndIcon[1], isVirtualNode); //����������ݶ���
			node_level_1[i] = new BillTreeDefaultMutableTreeNode(treeNodeVO); //һ���������нڵ�ŵ����ڵ㣬Ȼ���ٶϿ���ϵ����������[����2012-07-18]
			map_parent.put(getModelItemVaueFromNode(node_level_1[i], templetVO.getTreepk()), node_level_1[i]); //���ڹ�ϣ����ע��һ��,�´��������ſ�!!
		}

		BillTreeNodeVO nodeVO = null; //
		String str_pk_parentPK = null; //
		BillTreeNodeVO nodeVO_2 = null; //
		String str_pk_2 = null; //

		for (int i = 0; i < node_level_1.length; i++) {
			nodeVO = (BillTreeNodeVO) node_level_1[i].getUserObject();
			//String str_thispk = getTreeModelItemVaue(nodeVO, templetVO.getTreepk()); //
			str_pk_parentPK = getTreeModelItemVaue(nodeVO, templetVO.getTreeparentpk()); //nodeVO.getRealValue(templetVO.getTreeparentpk()); // ��������
			if (str_pk_parentPK == null || str_pk_parentPK.trim().equals("")) {
				rootNode.add(node_level_1[i]); //������ط����Ҳ������׵Ľڵ������ڵ��¡�����4-6��[����2012-07-18]
				continue;
			}

			DefaultMutableTreeNode parentnode = (DefaultMutableTreeNode) map_parent.get(str_pk_parentPK); //
			if (parentnode != null) { //����ҵ��ְ���..
				try {
					parentnode.add(node_level_1[i]); //�ڰְ����������..
				} catch (Exception ex) {
					logger.error("��[" + parentnode + "]�ϴ����ӽ��[" + node_level_1[i] + "]ʧ��!!");
					ex.printStackTrace(); //
				}
			} else { //���û�ҵ��ְ�,��ǿ�м����ӽ��!
				rootNode.add(node_level_1[i]); //
			}
		}

		//�Ƿ�ֻ����ǰ����
		if (_leafLevel > 0) {
			for (int i = 0; i < node_level_1.length; i++) {
				if (node_level_1[i].getLevel() == _leafLevel) { //�����ǰ���ǵ�����,����ȥ��ǰ���µ���������,���������������µ�����֦Ҷ
					node_level_1[i].removeAllChildren();
				}
			}
		}

		map_parent.clear(); //��չ�ϣ��..

		jTree.expandPath(new TreePath(rootNode)); //��չ����һ��

		if (rootNode.getChildCount() == 1) { //��������ֻ��һ���ӽ��,��ͬʱչ������ӽ��!
			TreePath path = new TreePath(getJTreeModel().getPathToRoot(rootNode.getChildAt(0)));
			jTree.expandPath(path); //
		}

		//jTree.repaint(); //
		updateUI(); //
	}

	private String getTreeNodePkValue(int _row) {
		String str_itemKey = this.getTempletVO().getPkname(); //�����ֶ���!!
		if (str_itemKey == null || str_itemKey.trim().equals("")) {
			return null; //
		}
		int li_index_key = findIndexInModel(str_itemKey); //����itemkeyȥ��������
		if (li_index_key < 0) {
			return null; //
		}
		return realStrData.getBodyData()[_row][li_index_key]; //��ʾ����
	}

	/**
	 * ȡ�����Ϳؼ�������
	 * @param _row
	 * @param _itemKey
	 * @return
	 */
	private String[] getTreeNodeViewText(int _row) {
		String str_itemKey = this.getTempletVO().getTreeviewfield();
		if (str_itemKey == null || str_itemKey.trim().equals("")) {
			return new String[] { "û��ָ�����Ϳؼ���ʾ������", null };
		}

		int li_index_key = findIndexInModel(str_itemKey); //����itemkeyȥ��������
		if (li_index_key >= 0) {
			String str_nodeText = realStrData.getBodyData()[_row][li_index_key]; //��ʾ����
			String str_nodeIconName = null; //ͼ������
			int li_index_icon = findIndexInModel("iconname"); //����û����Ϊicon���ֶ�
			if (li_index_icon >= 0) { //�����ͼ���ֶ�
				str_nodeIconName = realStrData.getBodyData()[_row][li_index_icon]; //
			}
			return new String[] { str_nodeText, str_nodeIconName }; //
		} else {
			return new String[] { "û���ڲ�ѯ������ҵ�����[" + str_itemKey + "]", null }; //
		}
	}

	private ComBoxItemVO findComBoxItemVO(ComBoxItemVO[] _vos, String _id) {
		if (_vos == null || _vos.length == 0) {
			return null;
		}
		for (int k = 0; k < _vos.length; k++) {
			if (_vos[k].getId() != null && _vos[k].getId().equals(_id)) { //����������������Ϊ��!!
				return _vos[k];
			}
		}
		return null;
	}

	/**
	 * ȡ��ĳһ������ϵ�ĳһ�е�ֵ
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
	 * ���ݽ��VOȥʵ�����ݶ�Ӧ��λ�����ҵ�ĳһ�е�ֵ
	 * @param _vo
	 * @param _itemKey
	 * @return
	 */
	private String getTreeModelItemVaue(BillTreeNodeVO _vo, String _itemKey) {
		int li_rowNo = _vo.getRowNo();
		if (li_rowNo < 0) {
			return null;
		}
		return getStringValueInModel(li_rowNo, _itemKey); //һ���Ӹ��ݾ�������λ��ȥ��!������!!
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
	 * ����ĳһ������,�ҵ���ʵ�������е�λ��,Ϊ���������,һ���ҳ�����������¼����!
	 * @param _itemKey
	 * @return
	 */
	private int findIndexInModel(String _itemKey) {
		if (_itemKey == null || _itemKey.trim().equals("")) {
			return -1;
		}
		Integer li_index = (Integer) mapModelIndex.get(_itemKey.toUpperCase());
		if (li_index != null) { //����Ѵ洢��ֱ�ӷ���
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
	 * �õ�����Դ����
	 * 
	 * @return
	 */
	public String getDataSourceName() {
		if (templetVO.getDatasourcename() == null || templetVO.getDatasourcename().trim().equals("null") || templetVO.getDatasourcename().trim().equals("")) {
			return ClientEnvironment.getInstance().getDefaultDataSourceName(); // Ĭ������Դ
		} else {
			return getTBUtil().convertDataSourceName(ClientEnvironment.getInstance().getCurrSessionVO(), templetVO.getDatasourcename()); // �������Դ!!
		}
	}

	/**
	 * ���ǹ�ѡ�������ʱ,�����ѡ�򴥷����¼�!!!
	 * @param _listener
	 */
	public void addBillTreeCheckEditListener(BillTreeCheckEditListener _listener) {
		v_billTreeCheckListener.add(_listener); //
	}

	/**
	 * ���ѡ���¼�!
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
	 * ��������ѡ��ڵ�ı�����¼�.....
	 * @param _node
	 */
	private void onChangeSelectTree(DefaultMutableTreeNode _node) {
		if (!isTriggerSelectChangeEvent) {
			return;
		}
		for (int i = 0; i < v_BillTreeSelectListeners.size(); i++) {
			BillTreeSelectListener listener = (BillTreeSelectListener) v_BillTreeSelectListeners.get(i);
			BillVO billVO = getBillVOFromNode(_node); //
			BillTreeSelectionEvent event = new BillTreeSelectionEvent(this, _node, billVO); // ��ǰѡ�еĽ��
			listener.onBillTreeSelectChanged(event); //
		}

		DefaultTreeModel model = (DefaultTreeModel) this.jTree.getModel();
		TreeNode[] nodes = model.getPathToRoot(_node);
		TreePath path = new TreePath(nodes);
		cellRender.setSelectPath(path); //

		StringBuilder sb_path = new StringBuilder("ֱ��·��:��"); //
		for (int i = 0; i < nodes.length; i++) {
			sb_path.append("" + ((DefaultMutableTreeNode) nodes[i]).getUserObject()); //
			if (i != nodes.length - 1) { //
				sb_path.append("->"); //
			}
		}
		sb_path.append("��"); //
		text_dirpath.setText(sb_path.toString()); //
		text_dirpath.setToolTipText(sb_path.toString()); //
		text_dirpath.setSelectionStart(0); //
		text_dirpath.setSelectionEnd(0); //
		jTree.repaint(); ////
	}

	/**
	 * ѡ��ĳһ�����....
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
	 * ����ѡ��
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
	 * ȡ�õ�ǰѡ�еĽ��
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
	 * ȡ��ѡ�н���ʵ�ʽ�����
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
	 * ȡ�õ�ǰѡ�еĽ�㵽���������н��..
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
	 * ȡ��ѡ�н��·���ϵ��������ݶ���!!,���Ӹ���㵽ѡ�н����������ݶ���,����������
	 * ���û��ѡ��,�򷵻�null,���ֻѡ�и����,�򷵻�BillVO[0]
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
			billVO = getBillVOFromNode(nodes[i]); //Ϊ�ý�㴴��BillVO
			if (billVO != null) {
				al_BillVOs.add(billVO); //
			}
		}
		return (BillVO[]) al_BillVOs.toArray(new BillVO[0]);
	}

	/**
	 * ȡ�����й��еĽ��
	 * @return
	 */
	public DefaultMutableTreeNode[] getCheckedNodes() {
		DefaultMutableTreeNode[] allNodes = getAllNodes(); //���ҳ����н��!!!
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
	 * �õ����й��еĽ��!!
	 * @return
	 */
	public BillVO[] getCheckedVOs() {
		DefaultMutableTreeNode[] allNodes = getAllNodes(); //���ҳ����н��!!!
		ArrayList al_temp = new ArrayList(); //
		for (int i = 0; i < allNodes.length; i++) {
			BillTreeDefaultMutableTreeNode checkedNode = (BillTreeDefaultMutableTreeNode) allNodes[i]; //
			if (checkedNode.isChecked()) { //����Ǳ����ϵ�!
				al_temp.add(checkedNode); //
			}
		}
		DefaultMutableTreeNode[] allCheckNodes = (DefaultMutableTreeNode[]) al_temp.toArray(new DefaultMutableTreeNode[0]); //ת��!!
		BillVO[] billVOs = new BillVO[allCheckNodes.length]; //
		for (int i = 0; i < allCheckNodes.length; i++) {
			billVOs[i] = getBillVOFromNode(allCheckNodes[i]);
		}
		return billVOs; //
	}

	/**
	 * ȡ�����н��
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
	 * ȡ������������
	 * @return
	 */
	public int getMaxLevel() {
		DefaultMutableTreeNode[] allNodes = getAllNodes(); //���ҳ����н��!!!
		int maxlevel = 0;
		for (int i = 0; i < allNodes.length; i++) {
			if (allNodes[i].getLevel() > maxlevel) {
				maxlevel = allNodes[i].getLevel();
			}
		}

		return maxlevel;
	}

	/**
	 * �õ��������ӽ��.
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
	 * �õ���������.
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
	 * ȡ�õ�ǰ�������ж��ӽ��!!
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
	 * ȡ��ݯ�����������ӽ��
	 * @param _node
	 * @return
	 */
	public DefaultMutableTreeNode[] getOneNodeChildPathNodes(DefaultMutableTreeNode _node) {
		return getOneNodeChildPathNodes(_node, false);
	}

	/**
	 * 
	 * @param _node ȡ��ĳһ������������ӽ�,����ָ���Ƿ�����Լ�
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
	 *            ȡ��ĳһ������������ӽ�,����ָ���Ƿ�����Լ���������������ӽڵ������
	 * @param _containSelf
	 * @return
	 */
	public String[] getAllSubNodesFromOneNode(DefaultMutableTreeNode _node) {
		String[] allNodesIds = null;
		DefaultMutableTreeNode[] pathNodes = this.getOneNodeChildPathNodes(_node, true); //
		allNodesIds = new String[pathNodes.length];
		for (int i = 0; i < pathNodes.length; i++) {
			allNodesIds[i] = getModelItemVaueFromNode(pathNodes[i], this.templetVO.getTreepk()); // ȡ�����ͽṹ������
		}
		return allNodesIds;
	}

	/**
	 * ȡ��ѡ�н���������������VO
	 * 
	 * @return
	 */
	public BillVO[] getSelectedChildPathBillVOs() {
		DefaultMutableTreeNode currNode = this.getSelectedNode(); //
		if (currNode.isRoot()) { // ���ѡ�е��Ǹ����,�򷵻���������VO
			return getAllBillVOs();
		}
		DefaultMutableTreeNode[] nodes = getSelectedChildPathNodes(); // ȡ����������..
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

		DefaultMutableTreeNode[] parentNodes = getSelectedParentPathNodes(); // ���и���
		DefaultMutableTreeNode[] childNodes = getSelectedChildPathNodes(); // ��������

		DefaultMutableTreeNode[] allNodes = new DefaultMutableTreeNode[parentNodes.length + childNodes.length - 1];
		for (int i = 0; i < parentNodes.length; i++) {
			allNodes[i] = parentNodes[i];
		}

		for (int j = 1; j < childNodes.length; j++) { // ��һ������!!
			allNodes[parentNodes.length + j - 1] = childNodes[j]; //
		}
		return allNodes;
	}

	/**
	 * ȡ��ѡ�н��ĸ�����������������ݶ���...
	 * 
	 * @return
	 */
	public BillVO[] getSelectedParentAndChildPathBillVOs() {
		TreePath path = jTree.getSelectionPath();
		if (path == null) {
			return null;
		}
		DefaultMutableTreeNode[] allNodes = getSelectedParentAndChildPathNodes(); // ȡ�����и�������ӽ��
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
	 * �ݹ����ĳ���������ӽ��!
	 * 
	 * @param _vector
	 *            ����
	 * @param node
	 *            ,ĳ��ʼ���!
	 */
	private void visitAllNodes(Vector _vector, TreeNode node) {
		if (!_vector.contains(node)) { //�����û�м���,�����!
			_vector.add(node); // ����ý��
		}
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) e.nextElement(); // �ҵ��ö���!!
				visitAllNodes(_vector, childNode); // �������Ҹö���
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
	 * ˢ�����н������
	 */
	public void refreshAllNodeVOFromModel() {
		DefaultMutableTreeNode[] allNodes = this.getAllNodes(); //
		for (int i = 0; i < allNodes.length; i++) {
			refreshNodeVOFromModel(allNodes[i]); //ˢ��ĳһ����������
		}
	}

	/**
	 * ˢ��ĳһ����������
	 * @param _node
	 */
	public void refreshNodeVOFromModel(DefaultMutableTreeNode _node) {
		BillTreeNodeVO nodeVO = (BillTreeNodeVO) _node.getUserObject(); //
		refreshNodeVOFromModel(nodeVO); //
	}

	/**
	 * ˢ�½�����ݴ�ģ����
	 * @param _nodeVO
	 */
	public void refreshNodeVOFromModel(BillTreeNodeVO _nodeVO) {
		int li_row = _nodeVO.getRowNo(); //
		if (li_row < 0) {
			return;
		}
		int li_index = findIndexInModel(this.templetVO.getTreeviewfield()); //��һ��
		if (li_index < 0) {
			return;
		}

		_nodeVO.setText(this.realStrData.getBodyData()[li_row][li_index]); //���¸�ֵ

		int li_index_icon = findIndexInModel("iconname"); //ͼ�괦��
		if (li_index_icon >= 0) {
			_nodeVO.setIconName(this.realStrData.getBodyData()[li_row][li_index_icon]); //���¸�ֵ
		}

	}

	/**
	 * ȡ��ĳһ������ϵ�BillVO
	 * @param _treeNode
	 * @return
	 */
	public BillVO getBillVOFromNode(DefaultMutableTreeNode _treeNode) {
		return getBillVOFromNode(_treeNode, this.getTempletVO());
	}

	/**
	 * ��ĳ������л�д����,��дĳһ�е�ֵ
	 * ��Ϊ��ʱֱ����ǰ̨ͨ��ĳ�ֲ���,����ֱ���ύSQL�޸��˿��е�����,ͬʱ�޸���UI����
	 * ��ʱ��Ҫ��дModel������ͻ�������ݲ�һ�µ����,�����´���ȡBillVOʱ�ͻ��Ǿɵ�!!
	 * ԭ����Ǹ��ݽ�������ҵ��кţ�Ȼ�����ջ���ȥ����realStrData.getBody()���Ǹ���ά����
	 * @param _billVO
	 */
	public void setBillVO(DefaultMutableTreeNode _node, String _itemkey, String _value) {
		if (_node == null) {
			return;
		}
		BillTreeNodeVO nodeVO = (BillTreeNodeVO) _node.getUserObject(); //ȡ�ý���ϰ󶨵�����...
		int li_row = nodeVO.getRowNo();
		if (li_row < 0) {
			return;
		}
		int li_index = findIndexInModel(_itemkey); //
		if (li_index < 0) {
			return;
		}
		this.realStrData.getBodyData()[li_row][li_index] = _value; //
		refreshNodeVOFromModel(nodeVO); //ˢ�½�������
	}

	/**
	 * ��BillVO�е����ݻ�д��Model��
	 * ��Ϊ��ʱֱ����ǰ̨ͨ��ĳ�ֲ���,����ֱ���ύSQL�޸��˿��е�����,ͬʱ�޸���UI����
	 * ��ʱ��Ҫ��дModel������ͻ�������ݲ�һ�µ����,�����´���ȡBillVOʱ�ͻ��Ǿɵ�!!
	 * @param _billVO
	 */
	public void setBillVO(DefaultMutableTreeNode _node, BillVO _billVO) {
		if (_node == null) {
			return;
		}
		BillTreeNodeVO nodeVO = (BillTreeNodeVO) _node.getUserObject(); //ȡ�ý���ϰ󶨵�����...
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
		refreshNodeVOFromModel(nodeVO); //ˢ�½������
	}

	/**
	 * Ϊ��ǰѡ�н�������µ�BillVOֵ
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
	 * ֱ����BillVO��д,ͨ��BillVO�е�ModelIndex����
	 * ʹ�����ְ취��ǰ����BillVO��Ԥ������ModelIndexֵ��,�����BillVO�Ǵ�����ȡ�õ�,������ͨ������;����õ�,����SQL��ѯ����,�����Ǵ�ĳ���б�,��Ƭ�ϵõ���!
	 * @param _billVO
	 */
	public void setBillVO(BillVO _billVO) {
		if (_billVO == null) {
			return;
		}
		int li_row = _billVO.getModelRowNoInTree(); //��ģ���еĵڼ���
		if (li_row < 0) {
			logger.error("�����Ϳؼ��л�дBillVOʧ��,��ΪBillVO�е�ModelRowNoInTree��ֵ."); //
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
	 * ��дһ��BillVO,ÿ��BillVO��������modelRowNoInTreeֵ
	 * @param _billVOs
	 */
	public void setBillVOs(BillVO[] _billVOs) {
		String[] keys = this.realStrData.getHeaderName(); //
		DefaultMutableTreeNode[] allNodes = getAllNodes(); //ȡ�����н��,��Ϊ�п��ܻ��޸Ľ���ֵ,��BillTreeNodeVO
		for (int i = 0; i < _billVOs.length; i++) {
			int li_row = _billVOs[i].getModelRowNoInTree(); //��ģ���еĵڼ���
			if (li_row < 0) {
				logger.error("�����Ϳؼ��л�дBillVOʧ��,��ΪBillVO�е�ModelRowNoInTree��ֵ."); //
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
	 * ��ĳһ������ϵ�ֵ��HashVO����ʽ����,����BillVO�y������������,�������ܸ���!
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
	 * ���ݽ��VO��ģ��VO����һ��BillVO����
	 * @param pub_Templet_1VO 
	 * @param nodeVO 
	 * @return
	 */
	public BillVO getBillVOFromNodeVO(BillTreeNodeVO _nodeVO, Pub_Templet_1VO _Templet_1VO) {
		if (_nodeVO.getRowNo() < 0) { //����к�С��0,�򷵻�null.
			return null;
		}

		BillVO billVO = new BillVO(); //�ȴ���,�������ֿ�ָ���쳣!!
		billVO.setModelRowNoInTree(_nodeVO.getRowNo()); //���ø����ݶ�ӦModel�����еĵڼ���!�ڻ�дʱ�ǳ�����!
		billVO.setTempletCode(templetVO.getTempletcode()); //ģ�����
		billVO.setTempletName(templetVO.getTempletname()); //ģ������
		billVO.setQueryTableName(templetVO.getTablename()); //��ѯ����
		billVO.setSaveTableName(templetVO.getSavedtablename()); //�������
		billVO.setPkName(templetVO.getPkname()); // �����ֶ���
		billVO.setSequenceName(templetVO.getPksequencename()); // ������

		int li_length = templetVO.getItemVos().length;
		// ����ItemKey
		String[] all_Keys = new String[li_length + 1]; //
		all_Keys[0] = "" + _nodeVO.getRowNo(); // �к�
		for (int i = 1; i < all_Keys.length; i++) {
			all_Keys[i] = this.templetVO.getItemKeys()[i - 1];
		}

		//itemName
		String[] all_Names = new String[li_length + 1]; //
		all_Names[0] = " "; // �к�
		for (int i = 1; i < all_Names.length; i++) {
			all_Names[i] = this.templetVO.getItemNames()[i - 1];
		}

		//����ItemType
		String[] all_Types = new String[li_length + 1]; //
		all_Types[0] = " "; // �к�
		for (int i = 1; i < all_Types.length; i++) {
			all_Types[i] = this.templetVO.getItemTypes()[i - 1];
		}

		//����������
		String[] all_ColumnTypes = new String[li_length + 1]; //
		all_ColumnTypes[0] = "NUMBER"; // �к�
		for (int i = 1; i < all_ColumnTypes.length; i++) {
			all_ColumnTypes[i] = templetVO.getItemVos()[i - 1].getSavedcolumndatatype(); //
		}

		//�Ƿ���Ҫ����
		boolean[] bo_isNeedSaves = new boolean[li_length + 1];
		bo_isNeedSaves[0] = false; // �к�
		for (int i = 1; i < bo_isNeedSaves.length; i++) {
			bo_isNeedSaves[i] = templetVO.getItemVos()[i - 1].isNeedSave();
		}

		billVO.setKeys(all_Keys); //
		billVO.setNames(all_Names); //
		billVO.setItemType(all_Types); // �ؼ�����!!
		billVO.setColumnType(all_ColumnTypes); // ���ݿ�����!!
		billVO.setNeedSaves(bo_isNeedSaves); // �Ƿ���Ҫ����!!

		//����ʵ������..
		Object[] obj_datas = new Object[li_length + 1]; // 
		obj_datas[0] = new RowNumberItemVO(RowNumberItemVO.INIT, _nodeVO.getRowNo()); //
		for (int i = 1; i < obj_datas.length; i++) {
			String str_item = getStringValueInModel(_nodeVO.getRowNo(), all_Keys[i]); //�ؼ�!!!��ȥ�������ҵ�ʵ��ֵ!!
			if (str_item == null) { //���Ϊ��,�򷵻ؿ�
				obj_datas[i] = null; //
			} else {
				if (all_Types[i].equals(WLTConstants.COMP_LABEL) || //
						all_Types[i].equals(WLTConstants.COMP_TEXTFIELD) || //
						all_Types[i].equals(WLTConstants.COMP_NUMBERFIELD) || //
						all_Types[i].equals(WLTConstants.COMP_PASSWORDFIELD) || //
						all_Types[i].equals(WLTConstants.COMP_TEXTAREA) || //
						all_Types[i].equals(WLTConstants.COMP_BUTTON)) {
					obj_datas[i] = new StringItemVO(str_item); //
				} else if (all_Types[i].equals(WLTConstants.COMP_COMBOBOX)) { //������
					ComBoxItemVO matchVO = findComBoxItemVO(templetVO.getItemVo(all_Keys[i]).getComBoxItemVos(), str_item); //
					if (matchVO != null) {
						obj_datas[i] = matchVO;
					} else {
						obj_datas[i] = new ComBoxItemVO(str_item, null, str_item); // ������VO
					}
				} else if (all_Types[i].equals(WLTConstants.COMP_REFPANEL) || //����
						all_Types[i].equals(WLTConstants.COMP_REFPANEL_TREE) || //
						all_Types[i].equals(WLTConstants.COMP_REFPANEL_MULTI) || //
						all_Types[i].equals(WLTConstants.COMP_REFPANEL_CUST) || //
						all_Types[i].equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET) || //
						all_Types[i].equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || //
						all_Types[i].equals(WLTConstants.COMP_REFPANEL_REGFORMAT) || //
						all_Types[i].equals(WLTConstants.COMP_REFPANEL_REGEDIT)) {
					obj_datas[i] = new RefItemVO(str_item, null, str_item); //
				} else if (all_Types[i].equals(WLTConstants.COMP_DATE) || all_Types[i].equals(WLTConstants.COMP_DATETIME)) { //������ʱ��
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
					obj_datas[i] = new RefItemVO(str_item, null, (str_item.indexOf("#") > 0 ? str_item.substring(str_item.indexOf("#") + 1, str_item.length()) : "����鿴Excel����")); //
				} else if (all_Types[i].equals(WLTConstants.COMP_IMPORTCHILD)) { //�����ӱ�!!
					obj_datas[i] = new RefItemVO(str_item, null, str_item); //
				} else if (all_Types[i].equals(WLTConstants.COMP_OFFICE)) { //Excel
					if (str_item.contains("_") || !str_item.contains(".")) {
						obj_datas[i] = new RefItemVO(str_item, null, "����鿴"); //
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
		billVO.setDatas(obj_datas); //��������������!!!!
		billVO.setToStringFieldName(templetVO.getTreeviewfield()); //
		billVO.setVirtualNode(_nodeVO.isVirtualNode()); //�Ƿ�������VO,���Ϳؼ��к���������������ĸ���,����BillVOҲ�иø���!Ȼ���ڲ��ղ�ѯ��������ʱ,�����������,����������!!!
		return billVO;
	}

	/**
	 * ȡ�õ�ǰѡ�е����н��
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
	 * ȡ��ѡ�еĽ���BillVO
	 * @return
	 */
	public BillVO getSelectedVO() {
		TreePath path = jTree.getSelectionPath();
		if (path == null) {
			return null;
		}
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		BillTreeNodeVO nodeVO = (BillTreeNodeVO) node.getUserObject(); //
		if (nodeVO.isVirtualNode()) { //�����������?
		}
		BillVO billVO = getBillVOFromNode(node); //���ݽ��������ģ�嶨�������,����һ��BillVO����
		return billVO; //
	}

	// ȡ��ѡ���е�BillVOs,����ѡʱ
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
			billVO = getBillVOFromNode(nodes[i]); //Ϊ�ý�㴴��BillVO
			if (billVO != null) {
				appendUserObjectByVOFromNode(billVO, nodes[i]); //�����û���Ϣ!!!
				al_BillVOs.add(billVO); //
			}
		}
		return (BillVO[]) al_BillVOs.toArray(new BillVO[0]); //
	}

	//ΪBillVO����һЩ�û���Ϣ
	private void appendUserObjectByVOFromNode(BillVO _billVO, DefaultMutableTreeNode _node) {
		_billVO.setUserObject("mylevel", _node.getLevel()); //ΪʲôҪ���ϲ��
		_billVO.setUserObject("myisleaf", new Boolean(_node.isLeaf()));
		String[] str_path = getOneNodeRootPathToStrings(_node, false); //
		_billVO.setUserObject("$ParentPathName", str_path[0]); //��-���ӵ�
		_billVO.setUserObject("$ParentPathNames", str_path[1]); //�÷ֺŷָ���
		_billVO.setUserObject("$parentpathids", str_path[2]); //�÷ֺŷָ���
	}

	/**
	 * ѡ�е���!
	 * @return
	 */
	public TreePath getSelectionPath() {
		return jTree.getSelectionPath(); //
	}

	/**
	 * ��������Ѱ��ĳһ�����.
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
	 * ��������Ѱ��ĳһ�����.
	 * @param _id
	 * @return
	 */
	public DefaultMutableTreeNode[] findNodeByViewName(String _name, boolean _isPrefix) {
		ArrayList al_nodes = new ArrayList(); //
		DefaultMutableTreeNode[] nodes = this.getAllNodes(); //ȡ�����н��
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
	 * ��ת��ĳһ�����.
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
	 * Ѱ�Ҳ���ת��ĳһ�����
	 * @param _id
	 */
	public void findNodeByKeyAndScrollTo(String _id) {
		DefaultMutableTreeNode node = findNodeByKey(_id); //
		scrollToOneNode(node); //
	}

	/**
	 * ��������Ѱ��ĳһ����� �����/2012-08-30��
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
	 * ��ת��ĳһ����� �����/2012-08-30��
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
			if (al_paths.size() <= 20) { //���ֻ�������Ľ��,��ѡ������
				jTree.setSelectionPaths(allSelPaths);
			} else { //��������,��ֻѡ���һ��!
				jTree.setSelectionPaths(new TreePath[] { allSelPaths[0] });
			}
		}
		jTree.repaint();
	}

	/**
	 * Ѱ�Ҳ���ת��ĳһ����� �����/2012-08-30��
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
		if (popmenu_header != null) { //�����
			if (text_dirpath.isVisible()) {
				item_showDirTree.setText(UIUtil.getLanguage("����ֱϵ·��"));
			} else {
				item_showDirTree.setText(UIUtil.getLanguage("��ʾֱϵ·��"));
			}
		} else {
			popmenu_header = new JPopupMenu(); //
			popmenu_header.setLightWeightPopupEnabled(false); //��ֹ��HtmlPanel��ס.
			item_quickLocate = new JMenuItem(UIUtil.getLanguage("����в���"), UIUtil.getImage("office_115.gif")); // ��ʾSQL
			item_quickqueryall = new JMenuItem(UIUtil.getLanguage("���ٴ�͸��ѯ"), UIUtil.getImage("office_163.gif")); //���ٴ�͸��ѯ
			item_refresh = new JMenuItem(UIUtil.getLanguage("ˢ��"), UIUtil.getImage("office_191.gif")); // ˢ��

			JMenu menu_expand_collapse = new JMenu("չ��/����"); //
			menu_expand_collapse.setIcon(UIUtil.getImage("office_008.gif")); //
			item_expand = new JMenuItem(UIUtil.getLanguage("ȫ��չ��")); // չ�����н��
			item_collapse = new JMenuItem(UIUtil.getLanguage("ȫ������")); // �������н��

			if (this.templetVO.getTreeisonlyone()) { // �����ֻ����ͬ����һ��,��ȫ��չ�����ܽ��õ�
				item_expand.setEnabled(false);
			} else {
				item_expand.setEnabled(true);
			}

			menu_expand_collapse.add(item_expand); //
			menu_expand_collapse.add(item_collapse); //

			JMenu menu_others = new JMenu("��������"); //
			item_updatelinkcode = new JMenuItem(UIUtil.getLanguage("����LinkCode")); //
			item_updatelinkname = new JMenuItem(UIUtil.getLanguage("����LinkName")); //����Ϊ�˸�������ʾ·��,��չ��linkName����
			item_updateParentid = new JMenuItem(UIUtil.getLanguage("����Parentid")); //
			item_checkOneField = new JMenuItem("��ĳ�ֶδ���"); //�����������!!

			item_updateParentid.setToolTipText("�����ͽṹ��id����Ū�ɴ�1..��ʼ,ֻ��������װ����ʱ�õ�"); //
			item_checkOneField.setToolTipText("����Ի������ͼ���Ƿ�Ϊ��,·���ϵĸ��������Ƿ�����ĳ��˳�����!���罫һ��һ����������֧������!"); //
			menu_others.add(item_updatelinkcode); //
			menu_others.add(item_updatelinkname); //
			menu_others.add(item_updateParentid); //
			menu_others.add(item_checkOneField); //

			item_showDirTree = new JMenuItem(UIUtil.getLanguage("��ʾֱϵ·��"), UIUtil.getImage("office_164.gif")); // ��ʾֱ��·��
			item_computechildnodecount = new JMenuItem(UIUtil.getLanguage("ͳ���ӽ������"), UIUtil.getImage("office_053.gif")); // ��ʾSQL
			item_modifytemplet = new JMenuItem(UIUtil.getLanguage("ģ��༭"), UIUtil.getImage("office_127.gif")); //ģ��༭
			item_showSQL = new JMenuItem(UIUtil.getLanguage("�鿴�����Ϣ"), UIUtil.getImage("office_162.gif")); // ��ʾSQL

			item_addNode = new JMenuItem(UIUtil.getLanguage("���ӽ��")); // ��ʾSQL
			item_delNode = new JMenuItem(UIUtil.getLanguage("ɾ�����")); // ��ʾSQL

			item_quickLocate.setPreferredSize(new Dimension(125, 19));
			item_quickqueryall.setPreferredSize(new Dimension(125, 19));
			item_refresh.setPreferredSize(new Dimension(125, 19));

			item_expand.setPreferredSize(new Dimension(70, 19));
			item_collapse.setPreferredSize(new Dimension(70, 19));
			item_modifytemplet.setPreferredSize(new Dimension(125, 19)); //ģ��༭
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

			//�Զ���˵�
			JMenuItem[] appMenuItems = getAppMenuItems();
			if (appMenuItems != null && appMenuItems.length != 0) {
				for (int i = 0; i < appMenuItems.length; i++) {
					if (appMenuItems[i].getText() == null || appMenuItems[i].getText().trim().equals("")) {
						popmenu_header.addSeparator(); //�ָ���
					} else {
						popmenu_header.add(appMenuItems[i]); //�����Զ���˵�
					}
				}

				popmenu_header.addSeparator(); //�ָ���
			}

			//��׼�˵�
			popmenu_header.add(item_quickLocate); //����в���
			popmenu_header.add(menu_expand_collapse); //
			popmenu_header.add(item_computechildnodecount); //�����ӽ������
			popmenu_header.add(item_showDirTree); //��ʾֱϵ·����
			if (ClientEnvironment.isAdmin()) { //
				popmenu_header.add(item_quickqueryall); //
				popmenu_header.add(item_refresh); //
				if (this.templetVO.getTreeisshowtoolbar()) {//���û�й�������Ҳ����������linkcode
					popmenu_header.add(menu_others); //
				}
				//popmenu_header.add(item_resetchildseq); // ��ʾSQL..
				popmenu_header.add(item_modifytemplet); //ģ��༭...
				popmenu_header.add(item_showSQL); // ��ʾSQL..
			}

			// popmenu_header.add(item_addNode); //���ӽ��
			// popmenu_header.add(item_delNode); //ɾ�����		

		}
		//�ڵ���֮ǰ�л��ᴦ����Щ�˵���Ч����Ч.
		for (int i = 0; i < al_BillTreebeBorePopMenuListeners.size(); i++) {
			ActionListener actionListener = (ActionListener) al_BillTreebeBorePopMenuListeners.get(i); //
			actionListener.actionPerformed(new ActionEvent(this, 0, "popmenu")); //
		}
		popmenu_header.show(_compent, _x, _y); //
	}

	/**
	 * ���ٴ�͸��ѯ!!
	 * ������������Ҳ���һ��,ҲҪ�п��ٴ�͸�ŷ��� xch/2012-02-23
	 */
	private void onQuickQueryAll() {
		if (templetVO.getTablename() == null || templetVO.getTablename().trim().equals("")) {
			MessageBox.show(this, "ģ�嶨����Ĳ�ѯ����Ϊ��,���ܽ��д˲���!"); //
			return;
		}
		queryData("select * from " + templetVO.getTablename()); //ֱ�Ӳ�ѯ,��ģ���ж���Ĺ������������������!!
	}

	public void refreshTree() {
		if (this.str_realsql == null) {
			MessageBox.show(this, "��ǰSQLΪ��,���ܻ�û�в�ѯ��һ��,���ܽ��д˲���!"); //
			return;
		}
		this.getJTree().clearSelection();
		this.clearSelection();//ˢ��ʱ��Ҫ��ѡ����ա����/2013-11-22��
		queryData(str_realsql); // ˢ������!!
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
	 * չ��ĳһ�����!
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
	 * ����ĳһ�����,�Ӹ���㵽�ý���·��,��->ƴ����.
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
			String str_id = getModelItemVaueFromNode((DefaultMutableTreeNode) pathNodes[i], this.templetVO.getTreepk()); //ȡ�����ͽṹ������
			sb_text1.append(str_nodeName);
			sb_text2.append(str_nodeName);
			sb_text3.append(str_id);
			if (i != pathNodes.length - 1) {
				sb_text1.append("-"); //��ǰ��->,�����ĳ�-��
			}
			sb_text2.append(";");
			sb_text3.append(";");
		}
		return new String[] { sb_text1.toString(), sb_text2.toString(), sb_text3.toString() }; //
	}

	/**
	 * ����������,����Ŀ¼�����,Ҷ�ӽ����,����Ľ����.
	 */
	private void onComputeChildNodeCount() {
		DefaultMutableTreeNode[] selNodes = this.getSelectedNodes(); //
		if (selNodes == null) {
			MessageBox.show(this, "��ѡ��һ�����"); //
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
		sb_msg.append("ѡ�еĽ�㹲��[" + (li_leafcount + li_dircount) + "]���ӽ��(�����Լ�),����:\r\n"); //
		sb_msg.append("Ŀ¼�����[" + li_dircount + "]��\r\n"); //
		sb_msg.append("Ҷ�ӽ����[" + li_leafcount + "]��\r\n\r\n"); //
		if (jTree.isRootVisible()) {
			for (int i = 0; i <= li_maxlevel; i++) {
				sb_msg.append("��[" + (i + 1) + "]������[" + li_levelCount[i] + "]��\r\n"); //
			}
		} else {
			for (int i = 1; i <= li_maxlevel; i++) {
				sb_msg.append("��[" + (i) + "]������[" + li_levelCount[i] + "]��\r\n"); //
			}
		}
		MessageBox.showTextArea(this, "ѡ�н�㡾" + str_selNodeText.toString() + "���������ӽ��ͳ�����", sb_msg.toString(), 400, 250); //
	}

	/**
	 * ���ӵ����Ҽ�ǰ�Ķ�������.
	 */
	public void addBillTreeBeforePopMenuActionListener(ActionListener _actionListener) {
		al_BillTreebeBorePopMenuListeners.add(_actionListener); //
	}

	// ���Ӽ�����
	public void addBillTreeSelectListener(BillTreeSelectListener _listener) {
		v_BillTreeSelectListeners.add(_listener);
	}

	// ��ȥ����ѡ�������!
	public void removeAllBillTreeSelectListener() {
		v_BillTreeSelectListeners.removeAllElements(); //
	}

	//�����ƶ�������
	public void addBillTreeMovedListener(BillTreeMoveListener _listener) {
		v_BillTreeMovedListeners.add(_listener); //
	}

	/**
	 * ���ٶ�λ,����Ҽ�����
	 */
	private void onQuickLocateByMouseRightClicked() {
		DefaultMutableTreeNode[] allNodes = this.getAllNodes(); //
		boolean showToString = getTBUtil().getSysOptionBooleanValue("����������в����Ƿ���ʾToString����", false);//�����/2015-02-19��
		String toStringKey = templetVO.getTostringkey();//toString����
		String treeViewField = templetVO.getTreeviewfield();//���������ʾ��
		//ģ����������ToString���У���������ʾ�в�ͬ������ʾToString���в��ɲ�ѯ�����/2016-02-19��
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
					sb_path.append(" �� "); //
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
				str_data[i][5] = allNodes[i].isLeaf() ? "��" : ""; //
			} else {
				str_data[i][2] = sb_path.toString(); //
				str_data[i][3] = "" + (allNodes[i].getLevel() + 1); //
				str_data[i][4] = allNodes[i].isLeaf() ? "��" : ""; //
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
	 * ����ť����Ŀ��ٲ�ѯ
	 * @param _isShiftDown �Ƿ���Shift��
	 */
	private void onQuickLocateByBtnClicked() {
		String str_text = textField_quickLocate.getText(); //
		if (str_text == null || str_text.trim().equals("")) {
			MessageBox.show(this, "������ؼ��ֲ�ѯ����!"); //
			return;
		}

		boolean bo_isPrefix = false; ////�Ƿ������֮Ϊ��ͷ..
		if (str_text.startsWith("~")) {
			bo_isPrefix = true; //
			str_text = str_text.substring(1, str_text.length()); //
		}

		DefaultMutableTreeNode[] quickLocateNodes = findNodeByViewName(str_text, bo_isPrefix); //
		if (quickLocateNodes == null || quickLocateNodes.length == 0) {
			MessageBox.show(this, "û���ҵ�ƥ�������!"); //
			return;
		}

		currQuickLocateNode = quickLocateNodes[0]; //��ǰ����ǵ�һ��
		scrollToOneNode(quickLocateNodes[0]); //��������Ӧ�ļ�¼!!
		if (quickLocateNodes.length == 1) {
			MessageBox.show(this, "���ҵ�[1]�����������ļ�¼!"); //
		} else {
			MessageBox.show(this, "���ҵ�[" + quickLocateNodes.length + "]�����������ļ�¼!\r\n����ұߵ����°�ť���Լ�������..."); //
		}
	}

	/**
	 * �Ƶ���һ�����..
	 */
	private void onMoveNextQuickLocateNode() {
		String str_text = textField_quickLocate.getText(); //
		if (str_text == null || str_text.trim().equals("")) {
			MessageBox.show(this, "������ؼ��ֲ�ѯ����!"); //
			return;
		}

		boolean bo_isPrefix = false; ////�Ƿ������֮Ϊ��ͷ..
		if (str_text.startsWith("~")) {
			bo_isPrefix = true; //
			str_text = str_text.substring(1, str_text.length()); //
		}

		DefaultMutableTreeNode[] quickLocateNodes = findNodeByViewName(str_text, bo_isPrefix); //�ҳ��������������Ľ��
		if (quickLocateNodes == null || quickLocateNodes.length == 0) {
			MessageBox.show(this, "û���ҵ�ƥ�������!"); //
			return;
		}

		for (int i = 0; i < quickLocateNodes.length; i++) {
			if (quickLocateNodes[i] == currQuickLocateNode) {
				if (i == quickLocateNodes.length - 1) {
					MessageBox.show(this, "�������һ�����������Ľ����!"); //
				} else {
					currQuickLocateNode = quickLocateNodes[i + 1]; //��ǰ����ǵ�һ��
					scrollToOneNode(quickLocateNodes[i + 1]); //��������Ӧ�ļ�¼!!
				}
				return;
			}
		}
	}

	/**
	 * �Ƶ�ǰһ�����....
	 */
	private void onMovePreviouQuickLocateNode() {
		String str_text = textField_quickLocate.getText(); //
		if (str_text == null || str_text.trim().equals("")) {
			MessageBox.show(this, "������ؼ��ֲ�ѯ����!"); //
			return;
		}

		boolean bo_isPrefix = false; ////�Ƿ������֮Ϊ��ͷ..
		if (str_text.startsWith("~")) {
			bo_isPrefix = true; //
			str_text = str_text.substring(1, str_text.length()); //
		}

		DefaultMutableTreeNode[] quickLocateNodes = findNodeByViewName(str_text, bo_isPrefix); //�ҳ��������������Ľ��
		if (quickLocateNodes == null || quickLocateNodes.length == 0) {
			MessageBox.show(this, "û���ҵ�ƥ�������!"); //
			return;
		}

		for (int i = 0; i < quickLocateNodes.length; i++) {
			if (quickLocateNodes[i] == currQuickLocateNode) {
				if (i == 0) {
					MessageBox.show(this, "���ǵ�һ�����������Ľ����!"); //
				} else {
					currQuickLocateNode = quickLocateNodes[i - 1]; //��ǰ����ǵ�һ��
					scrollToOneNode(quickLocateNodes[i - 1]); //��������Ӧ�ļ�¼!!
				}
				return;
			}
		}
	}

	/**
	 * ���������ӽ���˳��,��12345����
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
			for (int i = 0; i < li_childcount; i++) { //���ж���
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) selNode.getChildAt(i);
				billVO = getBillVOFromNode(childNode); //
				if (billVO != null) {
					if (!("" + (i + 1)).equals(billVO.getStringValue(templetVO.getTreeseqfield()))) { //�������,������
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
				this.realStrData.getBodyData()[(Integer) li_keys[i]][findIndexInModel(templetVO.getTreeseqfield())] = (String) vm_setNode.get(li_keys[i]); //�޸�ʵ������
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * ��ʾ��ǰ�����Ϣ��SQL
	 */
	private void showSQL() {
		StringBuffer sb_text = new StringBuffer(); //
		sb_text.append("ģ�����=��" + this.getTempletVO().getTempletcode() + "��\r\n");
		sb_text.append("ģ������=��" + this.getTempletVO().getTempletname() + "��\r\n");
		sb_text.append("ģ���ѯ����=��" + this.getTempletVO().getTablename() + "��\r\n");
		sb_text.append("����ı���=��" + this.getTempletVO().getSavedtablename() + "��\r\n");
		if (ClientEnvironment.getInstance().isAdmin()) {
			sb_text.append("��ǰ��ѯ��SQL:��" + this.str_realsql + "��\r\n\r\n"); //
		}

		sb_text.append("����Ȩ�޹���˵��:��" + this.str_resAccessInfo + "��\r\n"); //
		sb_text.append("����Ȩ�޹���SQL:��" + this.str_resAccessSQL + "��\r\n"); //

		sb_text.append("\r\n");
		if (this.getSelectedNode() != null) { //�����ѡ�еĽ��
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

			sb_text.append("��ǰ���·��:��" + sb_path.toString() + "��\r\n"); //
			BillTreeNodeVO treeNodeVO = this.getSelectedTreeNodeVO();
			sb_text.append("ѡ�н���BillTreeNodeVO����:��" + treeNodeVO.getRowNo() + "," + treeNodeVO.getText() + "��\r\n");
			sb_text.append("ѡ�н���BillVO����:\r\n");
			BillVO billVO = this.getSelectedVO();
			if (billVO != null) {
				String[] str_keys = billVO.getKeys(); //
				String[] str_names = billVO.getNames();
				for (int i = 1; i < str_keys.length; i++) {
					sb_text.append(str_keys[i] + "(" + str_names[i] + ")=[" + billVO.getStringValue(str_keys[i]) + "]\r\n"); //
				}

				HashMap billVOUserMap = billVO.getUserObjectMap(); //���Զ������!!!
				sb_text.append("\r\n��BillVO��userObjectֵ<��Ҫͨ��billVO.getUserObject(\"key\")���ܵõ�>:\r\n");
				if (billVOUserMap.size() > 0) {
					Iterator its = billVOUserMap.keySet().iterator(); //
					while (its.hasNext()) {
						String str_key = (String) its.next(); //
						String str_value = "" + billVOUserMap.get(str_key); //
						sb_text.append("[" + str_key + "]=[" + str_value + "]\r\n"); //
					}
				} else {
					sb_text.append("��BillVO��userObjectֵΪ��!\r\n");
				}
			} else {
				sb_text.append("��null��\r\n");
			}

		}
		MessageBox.showTextArea(this, "ѡ�н�����Ϣ", sb_text.toString(), 500, 300); //
	}

	private FrameWorkMetaDataServiceIfc getFWMetaService() throws WLTRemoteException, Exception {
		FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) RemoteServiceFactory.getInstance().lookUpService(FrameWorkMetaDataServiceIfc.class);
		return service;
	}

	//
	public void updateUI() {
		super.updateUI(); //
		if (jTree != null) {
			jTree.updateUI(); //��ǰ�õ���WLTTreeUI,����������û�б�Ҫ,��ΪֻҪ�������͸��,������BillTreePanelŪ�ɽ��������! ������һ����ˢ��ʱ��������������,���������,���½�ɫ����Ȩ��ʱ��bug��!!
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
	 * ��ѡ�еĽ��������һ���½��
	 * @param _billVO
	 * @param autoSelected ���һ���½ڵ��Ƿ��Զ�ѡ�񡣺���2012-07-11
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
	 * ����ĳ�����,�������ͱ�����BillVO
	 */
	public void addNode(DefaultMutableTreeNode _parentNode, BillVO _billVO, boolean autoSelected) {
		String str_viewText = _billVO.getStringValue(this.getTempletVO().getTreeviewfield()); //
		String str_iconName = null; //
		_billVO.setToStringFieldName(this.templetVO.getTreeviewfield()); // ������ʾ����!!
		if (_billVO.containsKey("iconname")) { //ͼ������
			str_iconName = _billVO.getStringValue("iconname"); //
		}

		String[][] str_data = this.realStrData.getBodyData(); //
		int li_col_count = realStrData.getHeaderName().length; //
		int li_row_count = str_data.length; //

		String[] str_newRowData = new String[li_col_count]; //�µ�һ������
		for (int j = 0; j < li_col_count; j++) {
			str_newRowData[j] = _billVO.getStringValue(realStrData.getHeaderName()[j]); //Ϊ�µ�һ�����ݸ�ֵ
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

		this.realStrData.setBodyData(str_newDatas); //ΪModel����������

		BillTreeNodeVO nodeVO = new BillTreeNodeVO(li_row_count, str_viewText, str_iconName); //����һ��������,�кž��Ǽ�1��
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(nodeVO);
		if (this.getJTree().getCellEditor() instanceof BillTreeCheckCellEditor) {//��������й�ѡ���򴴽�BillTreeDefaultMutableTreeNode ���󣬷���ѡʱ�������/2012-03-05��
			newNode = new BillTreeDefaultMutableTreeNode(nodeVO);
		}

		getJTreeModel().insertNodeInto(newNode, _parentNode, _parentNode.getChildCount()); //
		if (autoSelected) {
			expandOneNode(newNode);
		}
		//getJTree().expandPath(getSelectedPath()); // չ���ý��
	}

	public void addNodes(BillVO[] _billVOs) {
		this.addNodes(_billVOs, true);
	}

	public void addNodes(BillVO[] _billVOs, boolean autoSelected) {
		for (int i = 0; i < _billVOs.length; i++) { //�������н��
			String str_pk_value = _billVOs[i].getPkValue(); //
			if (findNodeByKey(str_pk_value) != null) { //������иý��,������.
				continue; //..
			}
			String str_parentid = _billVOs[i].getStringValue(this.templetVO.getTreeparentpk()); //�ҵ����׽��
			if (str_parentid != null) {
				DefaultMutableTreeNode parentNode = findNodeByKey(str_parentid); //�ҵ������
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
	 * ɾ����ǰ���,ֻ�Ǵ�ҳ����ɾ��!!
	 * ɾ�������Բ��ܴ���Model�е�ֵ,��Ϊ���µĽ����к��Ի��Ӧ��Model�����е�ʵ����
	 */
	public void delCurrNode() {
		DefaultMutableTreeNode node = getSelectedNode();
		if (node == null) {
			return;
		}
		jTree.cancelEditing();//sunfujun/20120607/���Ӵ˾����й�ѡ�򼴿ɱ༭��ʱ��ᱨ��
		getJTreeModel().removeNodeFromParent(node); //
	}

	/**
	 * �����...
	 */
	public void clearTree() {
		int li_childcount = rootNode.getChildCount();
		for (int i = 0; i < li_childcount; i++) {
			MutableTreeNode childNode = (MutableTreeNode) rootNode.getChildAt(i); //
			getJTreeModel().removeNodeFromParent(childNode); //
		}
		this.realStrData = null; //��Model�е�ֵҲ���
	}

	/**
	 * ���ѡ��,����ѡ�е����ͽ��ȡ��ѡ��!
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
	 * ���������û��˵�,���Ҽ�ʱ�ᵯ��..
	 * @param _appMenuItems
	 */
	public void setAppMenuItems(JMenuItem[] _appMenuItems) {
		al_appMenuItems.clear(); //
		for (int i = 0; i < _appMenuItems.length; i++) {
			al_appMenuItems.add(_appMenuItems[i]); //
		}
	}

	/**
	 * �����Ҽ��˵�...
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

	//����Ȩ�޹��˺�,�������������,����չʾʱ����һ�������������!�ñ������Ǵ洢���������������!!
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
			//System.out.println("�·�" + this.getSelectionPath());

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
					//System.out.println("�����Ƶ��Լ�����!!!"); //
					return;
				}

				if (JOptionPane.showConfirmDialog(BillTreePanel.this, "���Ƿ�����뽫��� " + selPath + " ������� " + parentPath + " ��?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
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
						return; //���������ͬ,�϶����ܴ���!!!
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
				nodeMoveChanged(); //���������ƶ�������
				jTree.expandPath(parentPath);
				this.updateUI(); //
				jTree.updateUI();
				((BasicTreeUI) jTree.getUI()).setCollapsedIcon(ImageIconFactory.getCollapsedIcon());//�ƶ�������ͼ�꡾���/2014-03-10��
				((BasicTreeUI) jTree.getUI()).setExpandedIcon(ImageIconFactory.getCollapsedIcon());
			}
		}
	}

	/**
	 * ��ɾ������������ɾ�������нڵ�id�����/2014-03-05��
	 */
	public ArrayList doDelete(boolean _isQuiet) {
		ArrayList deleteids = new ArrayList();
		if (this.getSelectedNode() == null) {
			MessageBox.show(this, "��ѡ��һ��������ɾ������!"); //
			return deleteids;
		}
		BillTreeNodeVO treeNodeVO = this.getSelectedTreeNodeVO();//��ǰ��ֱ��ɾ��ѡ�нڵ㣬���ӽڵ㲻ɾ���������ӽڵ��ٴμ���ʱ�Ҳ������ڵ�������ʾ�����ĵ�һ���Ĵ����ָ�Ϊһ��ɾ��������ڵ㣡�����/2012-02-29��
		BillVO[] childVOs = this.getSelectedChildPathBillVOs(); //ȡ������ѡ�е�
		if (!_isQuiet && MessageBox.showConfirmDialog(this, "��ȷ��Ҫɾ����¼��" + treeNodeVO.toString() + "����?\r\n�⽫һ��ɾ�����¹���" + childVOs.length + "���������¼,����ؽ�������!", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return deleteids;
		}
		Vector v_sqls = new Vector(); //�˵�ɾ��
		for (int i = 0; i < childVOs.length; i++) {
			v_sqls.add("delete from " + childVOs[i].getSaveTableName() + " where " + childVOs[i].getPkName() + "='" + childVOs[i].getPkValue() + "'"); //
			deleteids.add(childVOs[i].getPkValue());
		}
		try {
			UIUtil.executeBatchByDS(null, v_sqls);
		} catch (Exception e) {
			e.printStackTrace();
		} //ִ�����ݿ����!!
		this.delCurrNode(); //
		this.updateUI();
		return deleteids;
	}
}
