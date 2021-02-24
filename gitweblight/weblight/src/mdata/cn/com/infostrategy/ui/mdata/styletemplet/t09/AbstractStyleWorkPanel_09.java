/**************************************************************************
 * $RCSfile: AbstractStyleWorkPanel_09.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.t09;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.AggBillVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RowNumberItemVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.AbstractCustomerButtonBarPanel;
import cn.com.infostrategy.ui.mdata.BillCardEditEvent;
import cn.com.infostrategy.ui.mdata.BillCardEditListener;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListEditEvent;
import cn.com.infostrategy.ui.mdata.BillListEditListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.styletemplet.AbstractStyleWorkPanel;

/**
 * ���ģ��9,�����ӱ�,�Ƿǳ����õ�һ��ģ��
 * ��һ�������ϼ����ӱ�..��ÿ���ӱ�����������һ�Զ��ϵ,����ÿ���ӱ���ƽ�ȵ�.
 * @author xch
 *
 */
public abstract class AbstractStyleWorkPanel_09 extends AbstractStyleWorkPanel implements BillCardEditListener, BillListEditListener, BillListSelectListener {
	protected BillListPanel parent_BillListPanel = null; //�����б����
	protected BillCardPanel parent_BillCardPanel = null; //����Ƭ���

	private ArrayList child_BillListPanel = null; //�����ӱ�
	private BillListPanel currentchildpanel = null;// ��ǰѡ�е��ӱ�

	protected IUIIntercept_09 uiinterceptor = null; //������

	protected AbstractCustomerButtonBarPanel panel_customer = null; //
	private boolean oncreate = false;

	protected CardLayout cardlayout = null; //

	private JPanel topanel = null;
	private int UPDATE_ROW_NUM = -1; // �����н��б༭���к�
	private JTabbedPane tabs = null; //

	private int index = -1;
	private boolean initing = true;// ��ʾ�Ƿ��ǳ�ʼ��ʱ��Ϊ�ӱ�TAB�л��ṩ�¼�����

	protected String returntotable = "�л��б�"; //
	protected String returntocard = "�л���Ƭ"; //

	private JPanel[] childcustomerpanel = null;// �ӱ���ɾ�����.
	protected JPanel navigationpanel = new JPanel();

	public abstract String getParentTableTempletcode(); //����ģ�����

	public abstract ArrayList getChildTableTempletcode(); //�ӱ�ģ������б�

	public abstract String getParentTablePK(); //����������

	public abstract ArrayList getChildTablePK(); //�ӱ������б�

	public abstract ArrayList getChildTableFK(); //�ӱ���������������ֶ��� �б�

	public String getCustomerpanel() {
		return null;
	}

	public void initialize() {
		super.initialize(); //

		if (getUiinterceptor() != null && !getUiinterceptor().trim().equals("")) {
			try {
				uiinterceptor = (IUIIntercept_09) Class.forName(getUiinterceptor().trim()).newInstance(); //
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}

		this.setLayout(new BorderLayout()); //

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getParentPanelWithBtn(), getChildPanel());
		splitPane.setDividerSize(10);
		splitPane.setDividerLocation(250);
		splitPane.setOneTouchExpandable(true);
		setAllChildCustomerJPanelVisible(false);
		this.add(splitPane, BorderLayout.CENTER); //
		initing = false;
		// ע�������¼�����!!���ӱ��ڳ�ʼ��ʱ��ע��.
		parent_BillCardPanel.addBillCardEditListener(this);

	}

	/**
	 * �������!!!
	 * @return
	 */
	protected JPanel getParentPanelWithBtn() {
		JPanel rpanel = new JPanel();
		rpanel.setLayout(new BorderLayout());
		rpanel.add(getBtnPanel(), BorderLayout.NORTH); //��ť��
		rpanel.add(getParentPanel(), BorderLayout.CENTER); //�������
		initSysBtnPanel(); //��ʼ��ϵͳ��ť!!
		return rpanel;
	}

	private void initSysBtnPanel() {
		hiddenAllSysButtons(); //
		if (isCanInsert()) {
			getSysButton(BTN_INSERT).setVisible(true); //����
		}

		if (isCanEdit()) {
			getSysButton(BTN_EDIT).setVisible(true); //�༭
		}

		if (isCanDelete()) {
			getSysButton(BTN_DELETE).setVisible(true); //ɾ��
		}

		getSysButton(BTN_SEARCH).setVisible(true); //��ѯ
		getSysButton(BTN_LIST).setVisible(true); //�鿴
	}

	/**
	 * ϵͳ��ť
	 * 
	 * @return JPanel
	 */
	protected JPanel getBtnPanel() {
		JPanel panel = new JPanel();
		panel.setBackground(LookAndFeel.billlistquickquerypanelbgcolor);
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		if (isShowsystembutton()) {
			panel.add(getSysBtnPanel());
		}

		if (getCustomerpanel() != null) {
			JPanel panel_tmp = getCustomerBtnPanel();
			if (panel_tmp != null) {
				panel.add(panel_tmp); //
			}
		}
		return panel;
	}

	public boolean isShowsystembutton() {
		return true;
	}

	/*
	 * �û��Զ��尴ť��
	 */
	protected JPanel getCustomerBtnPanel() {
		try {
			panel_customer = (AbstractCustomerButtonBarPanel) Class.forName(getCustomerpanel()).newInstance();
			panel_customer.setParentWorkPanel(this); //
			panel_customer.initialize(); //
			return panel_customer;
		} catch (Exception e) {
			MessageBox.showException(this, e);
			return null;
		}
	}

	/**
	 * ��ȡ�����UI����������
	 * 
	 * @return String
	 */
	public String getUiinterceptor() {
		return null;
	}

	/**
	 * ��ȡBS����������.��Ҫʵ���า��
	 * 
	 * @return String
	 */
	public String getBsinterceptor() {
		return null;
	}

	/**
	 * ��������Panel
	 * 
	 * @return JPanel
	 */
	protected JPanel getParentPanel() {
		JPanel rpanel = new JPanel();
		rpanel.setLayout(new BorderLayout());
		cardlayout = new CardLayout(); //������β���
		topanel = new JPanel(cardlayout); //�������

		topanel.add("list", getParent_BillListPanel()); //
		topanel.add("card", getParent_BillCardPanel()); //

		rpanel.add(topanel, BorderLayout.CENTER);
		return rpanel;
	}

	/**
	 * �ڿ�Ƭ���б����л�
	 */
	protected void onSwitch() {
		oncreate = !oncreate;
		btn_insert.setVisible(!oncreate);
		btn_edit.setVisible(!oncreate);
		btn_delete.setVisible(!oncreate);
		if (panel_customer != null)
			panel_customer.setVisible(!oncreate);
		btn_save.setVisible(oncreate);
		btn_save_return.setVisible(oncreate);
		btn_cancel_return.setVisible(oncreate);
		cardlayout.next(topanel);
	}

	/*
	 * �ӱ����ڵ�Panel
	 */
	protected JPanel getChildPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout()); //
		tabs = new JTabbedPane();
		tabs.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if (!initing)
					setSelectStatus();
			}

		});
		child_BillListPanel = new ArrayList();
		childcustomerpanel = new JPanel[getChildTableTempletcode().size()];
		for (int i = 0; i < this.getChildTableTempletcode().size(); i++)
			tabs.addTab(getChild_BillListPanel(i).getTempletVO().getTempletname(), getChild_BillListPanel(i));
		panel.add(tabs, BorderLayout.CENTER);
		currentchildpanel = (BillListPanel) tabs.getSelectedComponent();
		index = tabs.indexOfComponent(currentchildpanel);
		return panel;
	}

	public BillListPanel getParent_BillListPanel() {
		if (parent_BillListPanel != null) {
			return parent_BillListPanel;
		}

		parent_BillListPanel = new BillListPanel(getParentTableTempletcode()); //
		parent_BillListPanel.setLoadedWorkPanel(this); //
		parent_BillListPanel.setItemEditable(false); //ȫ�����ɱ༭
		parent_BillListPanel.addBillListSelectListener(this); //
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
		parent_BillCardPanel = new BillCardPanel(parent_BillListPanel.getTempletVO());
		parent_BillCardPanel.setLoadedWorkPanel(this);
		return parent_BillCardPanel;
	}

	public BillListPanel getChild_BillListPanel(int i) {
		if (child_BillListPanel != null && i < child_BillListPanel.size()) {
			return (BillListPanel) child_BillListPanel.get(i);
		}

		BillListPanel child = new BillListPanel((String) getChildTableTempletcode().get(i));
		child.setLoadedWorkPanel(this);
		child.setAllItemValue("listiseditable", WLTConstants.BILLCOMPENTEDITABLE_NONE);
		child.setCustomerNavigationJPanel(getChildCustomerJPanel(i));
		child.addBillListEditListener(this);// ע�������
		child_BillListPanel.add(child);
		return child;
	}

	/**
	 * �õ������ӱ�ҳǩ!!
	 * 
	 * @return
	 */
	public BillListPanel[] getChild_BillListPanels() {
		return (BillListPanel[]) child_BillListPanel.toArray(new BillListPanel[0]);
	}

	public void setAllChildTableEditable(boolean show) {
		for (int i = 0; i < childcustomerpanel.length; i++) {
			((BillListPanel) (child_BillListPanel.get(i))).stopEditing();
			((BillListPanel) (child_BillListPanel.get(i))).setAllItemValue("listiseditable", show ? WLTConstants.BILLCOMPENTEDITABLE_ALL : WLTConstants.BILLCOMPENTEDITABLE_NONE);
		}
		tabs.updateUI();
	}

	public void setAllChildTableStatusAS(String _status) {
		for (int i = 0; i < childcustomerpanel.length; i++) {
			((BillListPanel) (child_BillListPanel.get(i))).stopEditing();
			((BillListPanel) (child_BillListPanel.get(i))).setAllRowStatusAs(_status);
		}
	}

	public void clearAllChildTable() {
		for (int i = 0; i < childcustomerpanel.length; i++) {
			((BillListPanel) (child_BillListPanel.get(i))).stopEditing();
			((BillListPanel) (child_BillListPanel.get(i))).clearTable();
		}
		tabs.updateUI();
	}

	public void setAllChildCustomerJPanelVisible(boolean show) {
		for (int i = 0; i < childcustomerpanel.length; i++) {
			childcustomerpanel[i].setVisible(show);
		}
		tabs.updateUI();
	}

	protected JPanel getChildCustomerJPanel(int i) {
		childcustomerpanel[i] = new JPanel();
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
		layout.setHgap(10);
		childcustomerpanel[i].setLayout(layout);
		JButton btn_insert = new JButton(UIUtil.getImage("insert.gif")); //
		JButton btn_delete = new JButton(UIUtil.getImage("delete.gif")); //
		JButton btn_refresh = new JButton(UIUtil.getImage("refresh.gif")); //

		btn_insert.setToolTipText("������¼");
		btn_delete.setToolTipText("ɾ����¼");
		btn_refresh.setToolTipText("ˢ��");

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

		childcustomerpanel[i].add(btn_insert);
		childcustomerpanel[i].add(btn_delete);
		childcustomerpanel[i].add(btn_refresh);

		return childcustomerpanel[i];
	}

	private void setSelectStatus() {
		this.currentchildpanel = getCurrentChild();
		this.index = getCurrentIndex();
	}

	/**
	 * �������� �¼�
	 * 
	 * @return void
	 */
	protected void onInsert() {
		try {
			setAllChildCustomerJPanelVisible(true);
			for (int i = 0; i < child_BillListPanel.size(); i++) {
				((BillListPanel) (child_BillListPanel.get(i))).clearTable();
			}
			onSwitch();
			setAllChildTableEditable(true);
			parent_BillCardPanel.insertRow();
			// ִ���������� ������;
			if (uiinterceptor != null) {
				try {
					uiinterceptor.actionAfterInsert_parent(parent_BillCardPanel);
				} catch (Exception e1) {
					MessageBox.showException(this, e1);
					setInformation("���������������쳣");
					return;
				}
			}
		} catch (Exception e) {
			MessageBox.showException(this, e); //
		}
	}

	/**
	 * ����༭�¼�
	 */
	protected void onEdit() {
		try {
			if (parent_BillListPanel.getTable().getSelectedRowCount() != 1)
				MessageBox.showSelectOne(this);
			else {
				setAllChildCustomerJPanelVisible(true);
				setAllChildTableEditable(true);
				UPDATE_ROW_NUM = parent_BillListPanel.getSelectedRow();
				onSwitch();
				parent_BillCardPanel.setValue(parent_BillListPanel.getValueAtRowWithHashMap(parent_BillListPanel.getSelectedRow()));
				parent_BillCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	protected BillListPanel getCurrentChild() {
		return (BillListPanel) child_BillListPanel.get(tabs.getSelectedIndex());
	}

	protected int getCurrentIndex() {
		return tabs.getSelectedIndex();
	}

	protected void onChildInsert() {
		if (!oncreate && parent_BillListPanel.getTable().getSelectedRowCount() != 1) {
			MessageBox.showSelectOne(this);
			return;
		}
		int li_row = currentchildpanel.newRow();
		currentchildpanel.setValueAt(parent_BillCardPanel.getValueAt(getParentTablePK()), li_row, (String) getChildTableFK().get(index));
		currentchildpanel.setValueAt(new Integer(1).toString(), currentchildpanel.getSelectedRow(), "VERSION");
		// ִ���ӱ����� ������;
		if (uiinterceptor != null) {
			try {
				uiinterceptor.actionAfterInsert_child(tabs.getSelectedIndex(), currentchildpanel, li_row);
			} catch (Exception e1) {
				MessageBox.showException(this, e1);
			}
		}
	}

	/**
	 * ���� ���������BILLCARD��ȷ��.
	 * 
	 */
	protected boolean dealInsert(AggBillVO _insertVO) throws Exception {
		// ִ�������ύǰ��������
		setInformation("ִ������������ǰ����");
		if (this.uiinterceptor != null) {
			try {
				uiinterceptor.dealCommitBeforeInsert(this, _insertVO);
			} catch (Exception e) {
				MessageBox.showException(this, e);
				return false;
			}
		}

		AggBillVO returnVO = getMetaService().style09_dealInsert(parent_BillListPanel.getDataSourceName(), getBsinterceptor(), _insertVO); // ֱ���ύ���ݿ�,����������쳣!!
		setInformation("ִ����������������");
		// ִ�������ύǰ��������
		if (this.uiinterceptor != null)
			uiinterceptor.dealCommitAfterInsert(this, returnVO);
		return true;
	}

	/**
	 * �޸��ύ
	 * 
	 */
	protected boolean dealUpdate(AggBillVO _updateVO) throws Exception {
		setInformation("ִ���������޸�ǰ����");
		if (this.uiinterceptor != null) {
			try {
				uiinterceptor.dealCommitBeforeUpdate(this, _updateVO); // �޸��ύǰ������
			} catch (Exception ex) {
				MessageBox.showException(this, ex);
				return false;
			}
		}

		AggBillVO returnvo = getMetaService().style09_dealUpdate(parent_BillListPanel.getDataSourceName(), getBsinterceptor(), _updateVO); //
		setInformation("ִ���������޸ĺ���");
		if (this.uiinterceptor != null)
			uiinterceptor.dealCommitAfterUpdate(this, returnvo); //
		return true;
	}

	/**
	 * �鿴������Ϣ
	 * 
	 */
	protected void onParentView() {
		try {

			if (parent_BillListPanel.getTable().getSelectedColumnCount() != 1) {
				MessageBox.show(AbstractStyleWorkPanel_09.this, "��ѡ��һ����¼", WLTConstants.MESSAGE_ERROR);
			} else {
				setInformation("�鿴��¼");
				onSwitch();
				btn_save.setVisible(false);
				btn_save_return.setVisible(false);
				btn_cancel_return.setVisible(false);
				final JButton back = new JButton("����");
				back.setPreferredSize(new Dimension(75, 20));
				this.getSysBtnPanel().add(back);
				back.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onCancelAndReturn();
						back.setVisible(false);
					}
				});
				parent_BillCardPanel.setValue(parent_BillListPanel.getValueAtRowWithHashMap(parent_BillListPanel.getSelectedRow()));
				parent_BillCardPanel.setRowNumberItemVO((RowNumberItemVO) parent_BillListPanel.getValueAt(parent_BillListPanel.getSelectedRow(), 0)); // �����к�
				parent_BillCardPanel.setEditable(false);
				parent_BillCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT);
			}
		} catch (Exception ex) {
			MessageBox.show(AbstractStyleWorkPanel_09.this, WLTConstants.STRING_OPERATION_FAILED + ":" + ex.getMessage(), WLTConstants.MESSAGE_ERROR);
		}
	}

	protected void onSave() {
		for (int i = 0; i < child_BillListPanel.size(); i++) {
			((BillListPanel) (child_BillListPanel.get(i))).stopEditing();
		}

		if (!parent_BillCardPanel.checkValidate()) { //У������
			return;
		}

		for (int i = 0; i < child_BillListPanel.size(); i++) {
			if (!((BillListPanel) (child_BillListPanel.get(i))).checkValidate()) { //У���ӱ�
				return;
			}
		}

		try {

			HashMap map = parent_BillCardPanel.getAllObjectValuesWithHashMap();
			AggBillVO aggvo = new AggBillVO();
			aggvo.setParentVO(parent_BillCardPanel.getBillVO());
			// if (aggvo.getParentVO().getVersion() != null) {
			// map.put("VERSION", new
			// Integer(aggvo.getParentVO().getVersion().intValue() +
			// 1).toString());
			// } else {
			// map.put("VERSION", new Integer(1).toString());
			// }
			VectorMap child_billvo = new VectorMap();
			for (int i = 0; i < child_BillListPanel.size(); i++) {
				BillListPanel temppanel = (BillListPanel) (child_BillListPanel.get(i));
				child_billvo.put("" + (i + 1), temppanel.getBillVOs());
			}
			aggvo.setChildVOMaps(child_billvo);
			try {
				if (parent_BillCardPanel.getEditState().equals(WLTConstants.BILLDATAEDITSTATE_INSERT)) { // ����������ύ
					if (!this.dealInsert(aggvo))
						return;
					parent_BillListPanel.insertRowWithInitStatus(parent_BillListPanel.getSelectedRow(), map);
					parent_BillListPanel.setAllRowStatusAs("INIT");
				} else if (parent_BillCardPanel.getEditState().equals(WLTConstants.BILLDATAEDITSTATE_UPDATE)) { // ������޸��ύ

					if (!this.dealUpdate(aggvo))
						return;
					Set keys = map.keySet();
					Iterator it = keys.iterator();
					while (it.hasNext()) {
						String key = (String) it.next();
						Object value = map.get(key);
						parent_BillListPanel.setValueAt(value, UPDATE_ROW_NUM, key);
						parent_BillListPanel.setAllRowStatusAs("INIT");
					}
					UPDATE_ROW_NUM = -1;
				}

				btn_save.setVisible(false);
				btn_save_return.setVisible(false);
				btn_cancel_return.setVisible(false);
				parent_BillCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT);
				this.setAllChildTableStatusAS(WLTConstants.BILLDATAEDITSTATE_INIT);
				setAllChildCustomerJPanelVisible(false);
				setAllChildTableEditable(false);
				setInformation("����ɹ�");
				MessageBox.show(this, "�������ݳɹ�!");
			} catch (Exception e) {
				MessageBox.showException(this, e);
				return;
			}

		} catch (Exception e) {
			MessageBox.show(WLTConstants.STRING_OPERATION_FAILED + ":" + e.getMessage(), WLTConstants.MESSAGE_ERROR);
			return;
		}
		return;
	}

	protected void onChildDelete() {
		// ִ���ӱ�ɾ��ǰ����;
		if (currentchildpanel.getTable().getSelectedRowCount() != 1) {
			MessageBox.show(WLTConstants.STRING_DEL_SELECTION_NEED, WLTConstants.MESSAGE_ERROR);
			return;
		}
		if (uiinterceptor != null) {
			try {
				setInformation("ɾ���ӱ�" + tabs.getName() + "��¼");
				uiinterceptor.actionBeforeDelete_child(tabs.getSelectedIndex(), currentchildpanel, currentchildpanel.getSelectedRow());
			} catch (Exception e1) {
				MessageBox.show(this, e1.getMessage(), WLTConstants.MESSAGE_WARN);
				e1.printStackTrace();
				setInformation("ɾ���ӱ�" + tabs.getName() + "�����쳣");
				return;
			}
		}
		currentchildpanel.removeRow();
	}

	protected void onParentDelete() {
		// ɾ��ʱ����.....
		int li_selectedRow = parent_BillListPanel.getTable().getSelectedRow();
		if (li_selectedRow < 0) {
			MessageBox.show(WLTConstants.STRING_DEL_SELECTION_NEED, WLTConstants.MESSAGE_ERROR);
			return;
		}

		if (MessageBox.confirm(this, "ȷ��ɾ��ѡ�м�¼���⽫ɾ���ӱ��еĹ�����¼?")) {
			// ִ��������ɾ��ǰ����!!
			if (uiinterceptor != null) {
				try {
					setInformation("ִ��������ɾ��ǰ����");
					uiinterceptor.actionBeforeDelete_parent(parent_BillListPanel, parent_BillListPanel.getTable().getSelectedRowCount()); // ִ��ɾ��ǰ�Ķ���!!
				} catch (Exception e) {
					if (!e.getMessage().trim().equals("")) {
						JOptionPane.showMessageDialog(this, e.getMessage()); //
					}
					return;
				}
			}

			// ����ɾ��������!!
			AggBillVO aggvo = new AggBillVO();
			aggvo.setParentVO(parent_BillListPanel.getBillVO(li_selectedRow)); // ֻ��ɾ��һ����¼������billvoҲֻ��һ��.

			VectorMap child_billvo = new VectorMap();
			// ɾ�������ӱ�
			for (int i = 0; i < child_BillListPanel.size(); i++) {
				BillListPanel tmp_billListPanel = (BillListPanel) (child_BillListPanel.get(i));
				BillVO[] tmp_childVOs = new BillVO[tmp_billListPanel.getTable().getRowCount()];
				for (int j = 0; j < tmp_childVOs.length; j++) {
					tmp_childVOs[j] = tmp_billListPanel.getBillVO(j);
					tmp_childVOs[j].setEditType(WLTConstants.BILLDATAEDITSTATE_DELETE); //
				}
				child_billvo.put("" + (i + 1), tmp_childVOs); //
			}
			aggvo.setChildVOMaps(child_billvo);

			try {
				// ִ��ɾ��;
				dealDelete(aggvo); // ֱ��ȥɾ��!!

				// ��ҳ������ȥ��¼!!
				for (int i = 0; i < child_BillListPanel.size(); i++) {
					BillListPanel temppanel = (BillListPanel) (child_BillListPanel.get(i));
					temppanel.removeAllRows();
					temppanel.clearDeleteBillVOs();
				}

				// ɾ������
				parent_BillListPanel.removeRow();
				parent_BillListPanel.clearDeleteBillVOs();// ���ɾ���ļ�¼
				setInformation("ɾ����¼�ɹ�!!");
			} catch (Exception e) {
				e.printStackTrace();
				setInformation("ɾ����¼ʧ��,ԭ��:" + e.getMessage());
				MessageBox.show(this, "ɾ����¼ʧ��,ԭ��:" + e.getMessage(), WLTConstants.MESSAGE_WARN); //
			}
		}
	}

	/**
	 * ɾ���ύ ����ɾ��ʱ����
	 * 
	 */
	protected void dealDelete(AggBillVO _deleteVO) throws Exception {
		if (this.uiinterceptor != null) {
			try {
				uiinterceptor.dealCommitBeforeDelete(this, _deleteVO);
			} catch (Exception ex) {
				throw ex;
			}
		}

		getMetaService().style09_dealDelete(parent_BillListPanel.getDataSourceName(), getBsinterceptor(), _deleteVO); //

		if (this.uiinterceptor != null) {
			uiinterceptor.dealCommitAfterDelete(this, _deleteVO);
		}

	}

	public void onSaveAndReturn() {
		onSave();
		parent_BillCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT);
		onChildRefresh();
		onSwitch();
	}

	public void onCancelAndReturn() {
		parent_BillCardPanel.reset();
		setAllChildCustomerJPanelVisible(false);
		setAllChildTableEditable(false);
		clearAllChildTable();
		parent_BillCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT);
		parent_BillListPanel.getTable().clearSelection();
		onSwitch();
	}

	/**
	 * ��Ƭ���������
	 */
	public void onBillCardValueChanged(BillCardEditEvent _evt) {
		if (uiinterceptor != null) {
			BillCardPanel card_tmp = (BillCardPanel) _evt.getSource(); //
			String tmp_itemkey = _evt.getItemKey(); //
			try {
				uiinterceptor.actionAfterUpdate_parent(card_tmp, tmp_itemkey);
			} catch (Exception e) {
				if (!e.getMessage().trim().equals("")) {
					JOptionPane.showMessageDialog(this, e.getMessage()); //
				}
			}
		}
	}

	public void onBillListValueChanged(BillListEditEvent _evt) {
		if (uiinterceptor != null) {
			BillListPanel list_tmp = (BillListPanel) _evt.getSource(); //
			String tmp_itemkey = _evt.getItemKey(); //
			try {
				uiinterceptor.actionAfterUpdate_child(tabs.getSelectedIndex(), list_tmp, tmp_itemkey, list_tmp.getSelectedRow());
			} catch (Exception e) {
				if (!e.getMessage().trim().equals("")) {
					JOptionPane.showMessageDialog(this, e.getMessage()); //
				}
			}
		}
	}

	public JTable getTable() {
		return parent_BillListPanel.getTable();
	}

	public void onChildRefresh() {
		if (parent_BillListPanel.getTable().getSelectedRowCount() == 0) {
			for (int i = 0; i < child_BillListPanel.size(); i++) {
				BillListPanel temppanel = (BillListPanel) (child_BillListPanel.get(i));
				temppanel.refreshCurrData();
			}
		} else
			refreshChildTable();
	}

	/**
	 * �����¼ѡ��仯ʱ�������¼�
	 */
	public void onBillListSelectChanged(BillListSelectionEvent _event) {

	}

	protected void refreshChildTable() {
		BillVO billVO = parent_BillListPanel.getSelectedBillVO(); //����ѡ�е���
		if (billVO == null) {
			return; //�������û��ѡ�����򷵻�
		}

		String str_parent_pkvalue = billVO.getStringValue(getParentTablePK()); //��������ֵ
		for (int i = 0; i < child_BillListPanel.size(); i++) {
			BillListPanel itemPanel = (BillListPanel) (child_BillListPanel.get(i));
			itemPanel.QueryDataByCondition(getChildTableFK().get(i) + "='" + str_parent_pkvalue + "'"); //ˢ�������
		}
	}

	protected BillListPanel getChildAt(int index) {
		return (BillListPanel) child_BillListPanel.get(index);
	}

	public JButton getInsertButton() {
		return this.btn_insert;
	}

	public JButton getDeleteButton() {
		return this.btn_delete;
	}

	public JButton getSearchButton() {
		return null;// this.btn_Search;
	}

	public JButton getRefreshButton() {
		return null; // this.btn_Search;
	}

	public JButton getEditButton() {
		return this.btn_edit;
	}

	public JButton getSaveButton() {
		return this.btn_save;
	}

	public AbstractCustomerButtonBarPanel getPanel_customer() {
		return panel_customer;
	}

	public boolean isCanWorkFlowDeal() {
		return false;
	}

	public boolean isCanWorkFlowMonitor() {
		return false;
	}

}
