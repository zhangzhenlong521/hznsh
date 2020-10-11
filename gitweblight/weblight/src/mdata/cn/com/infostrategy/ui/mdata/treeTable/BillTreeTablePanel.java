package cn.com.infostrategy.ui.mdata.treeTable;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.ImageIconFactory;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillButtonPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;
import cn.com.infostrategy.ui.mdata.NumberFormatdocument;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellEditor_Button;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellEditor_CheckBox;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellEditor_ComboBox;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellEditor_FileChoose;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellEditor_Label;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellEditor_Ref;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellEditor_TextArea;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellEditor_TextField;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellRender_Button;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellRender_CheckBox;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellRender_FileChoose;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellRender_JLabel;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellRender_JTextArea;

/**
 * ����ؼ�
 * @author haoming
 * create by 2015-7-2
 */
public class BillTreeTablePanel extends BillPanel {
	private static final long serialVersionUID = -1607093858101978403L;
	private Pub_Templet_1VO templetVO = null; // Ԫԭģ������
	private Pub_Templet_1_ItemVO[] templetItemVOs = null; //Ԫԭģ���ӱ�
	private BillTreeTable treetable;
	private DefaultTableColumnModel columnModel = null;
	private DefaultTreeTableModel treetablemodel;
	private TBUtil tBUtil;
	public int li_currsorttype = 0;
	private BillButtonPanel billListBtnPanel = null; // ��ť���
	private JScrollPane scrollPanel_main = new JScrollPane(); //
	private SortHeaderRenderer headerRenderer; //��ͷ

	/**
	 * 
	 * @param _templetcode ģ�����
	 */
	public BillTreeTablePanel(String _templetcode) {
		initialize(_templetcode); //
	}

	private void initialize(String _templetcode) {
		setLayout(new BorderLayout());
		try {
			templetVO = UIUtil.getPub_Templet_1VO(_templetcode);
			templetItemVOs = templetVO.getItemVos();
		} catch (Exception e) {
			e.printStackTrace();
		}
		treetablemodel = new DefaultTreeTableModel(new BillTreeTableDefaultMutableTreeNode(""), templetVO);
		treetable = new BillTreeTable(treetablemodel, null);
		try {
			queryDataByDS(null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		treetable.setSelectionBackground(LookAndFeel.tablerowselectbgcolor); //���ñ�����ɫ������Ҫ
		treetable.getColumnModel().getColumn(0).setPreferredWidth(200);
		scrollPanel_main.setOpaque(false); //͸��!!
		scrollPanel_main.getViewport().setOpaque(false); //͸��!!!
		scrollPanel_main.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);//�︻��
		scrollPanel_main.getViewport().add(treetable); // ʵ�����ݵı��!!
		this.add(getBillListBtnPanel(false), BorderLayout.NORTH);
		this.add(scrollPanel_main, BorderLayout.CENTER);
		this.setPreferredSize(new Dimension(975, 125)); //
		headerRenderer = new SortHeaderRenderer(templetVO, true);
		treetable.getTableHeader().setDefaultRenderer(headerRenderer); //
		treetable.getTableHeader().setPreferredSize(new Dimension(20000, templetVO.getListheadheight())); //
		treetable.getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) { //
					sortByColumn(evt.getPoint());
				}
			}
		});
		initColumnRender();
		//		treetable.getModel().addTableModelListener(this);
		CheckTreeTableManager manager = new CheckTreeTableManager(treetable); //��ѡ�����ڵ�
		treetable.getJTree().getSelectionModel();
	}

	private void initColumnRender() {
		int columnCount = treetablemodel.getColumnCount();
		TableColumn column[] = getTableColumns();
		for (int i = 0; i < columnCount; i++) {
			String columnKey = treetablemodel.getColumnKey(i);
			if (columnKey.equals("$treetableshowitem")) {
				treetable.getColumnModel().getColumn(i).setCellEditor(new BillTreeTableCellEditor(treetable.getJTree(), treetable, true));
				treetable.getColumnModel().getColumn(i).setCellRenderer((BillTreeTableCellRenderer) treetable.getJTree());
				continue;
			}
			for (int j = 0; j < column.length; j++) {
				if (columnKey.equals(column[j].getIdentifier())) {
					treetable.getColumnModel().getColumn(i).setCellEditor(column[j].getCellEditor());
					treetable.getColumnModel().getColumn(i).setCellRenderer(column[j].getCellRenderer());
					treetable.getColumnModel().getColumn(i).setPreferredWidth(column[j].getWidth());
					break;
				}
			}
		}
	}

	/**
	 * �������е���
	 * 
	 * @return
	 */
	private TableColumn[] allTableColumns;

	//����ƽ̨ԭ�б����Ⱦ���ƺͱ༭�ؼ�
	private TableColumn[] getTableColumns() {
		if (this.allTableColumns != null) {
			return allTableColumns;
		}
		allTableColumns = new TableColumn[templetItemVOs.length]; // �е���������ģ���е�����!
		for (int i = 0; i < allTableColumns.length; i++) {
			String str_key = templetItemVOs[i].getItemkey(); // key
			String str_type = templetItemVOs[i].getItemtype(); // ����
			int li_width = templetItemVOs[i].getListwidth().intValue(); // ���
			if (!TBUtil.isEmpty(templetItemVOs[i].getItemname())) {
				li_width += LookAndFeel.getFONT_REVISE_SIZE() * (templetItemVOs[i].getItemname().length() + (templetItemVOs[i].getIsmustinput() ? 4 : 0));
			}

			TableCellEditor cellEditor = null;
			TableCellRenderer cellRender = null;
			if (str_type.equals(WLTConstants.COMP_LABEL)) {
				if (templetVO.getIslistautorowheight()) { //������Զ��Ÿ�,��ǿ��ʹ��TextArea��ʾ!��Ϊ�����Ÿߺ�Ż���Ч��,������Ȼ�Ÿ���,������һ��,������˸�[...],û�ﵽЧ��!��xch/2012-05-04��
					cellRender = new ListCellRender_JTextArea(templetItemVOs[i]); // �����ı���ʾ��
				} else {
					cellRender = new ListCellRender_JLabel(templetItemVOs[i]); //
				}
				cellEditor = new ListCellEditor_Label(templetItemVOs[i]); // ��ǩ
			} else if (str_type.equals(WLTConstants.COMP_TEXTFIELD) || // �ı���
					str_type.equals(WLTConstants.COMP_NUMBERFIELD) || // ���ֿ�
					str_type.equals(WLTConstants.COMP_PASSWORDFIELD) // �����
			) {
				if (templetVO.getIslistautorowheight()) {
					cellRender = new ListCellRender_JTextArea(templetItemVOs[i]); // �����ı���ʾ��
				} else {
					cellRender = new ListCellRender_JLabel(templetItemVOs[i]); //
				}
				JTextField textField = null;
				if (str_type.equals(WLTConstants.COMP_TEXTFIELD)) {
					textField = new JTextField();
				} else if (str_type.equals(WLTConstants.COMP_NUMBERFIELD)) // ���ֿ�
				{
					textField = new JFormattedTextField();
					CommUCDefineVO ucvo = templetItemVOs[i].getUCDfVO();
					textField.setDocument(new NumberFormatdocument(ucvo)); //�������ֿ�ֻ����������,������ĸ���ü���!�����/2013-06-05��
				} else if (str_type.equals(WLTConstants.COMP_PASSWORDFIELD)) { // �����
					textField = new JPasswordField();
				}
				textField.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, cn.com.infostrategy.ui.common.LookAndFeel.compBorderLineColor)); //
				cellEditor = new ListCellEditor_TextField(textField, templetItemVOs[i]);
			} else if (str_type.equals(WLTConstants.COMP_COMBOBOX)) { // ������
				if (templetVO.getIslistautorowheight()) { //������Զ��Ÿ�,��xch/2012-05-04��
					cellRender = new ListCellRender_JTextArea(templetItemVOs[i]); // �����ı���ʾ��
				} else {
					cellRender = new ListCellRender_JLabel(templetItemVOs[i]); //
				}
				JComboBox comBox = new JComboBox(); //
				if (templetItemVOs[i].getComBoxItemVos() != null) {
					comBox.addItem(new ComBoxItemVO("", null, "")); //
					for (int kk = 0; kk < templetItemVOs[i].getComBoxItemVos().length; kk++) {
						if (templetItemVOs[i].getComBoxItemVos()[kk] != null) {
							comBox.addItem(templetItemVOs[i].getComBoxItemVos()[kk]); //�ȳ�ʼ���ؼ�!!
						}
					}
				}
				cellEditor = new ListCellEditor_ComboBox(comBox, templetItemVOs[i]);
			} else if (str_type.equals(WLTConstants.COMP_TEXTAREA)) { // �����ı���
				cellRender = new ListCellRender_JTextArea(templetItemVOs[i]); // �����ı���ʾ��
				cellEditor = new ListCellEditor_TextArea(templetItemVOs[i]); // �����ı���༭��
			} else if (str_type.equals(WLTConstants.COMP_REFPANEL) || // ���Ͳ���1
					str_type.equals(WLTConstants.COMP_REFPANEL_TREE) || // ���Ͳ���1
					str_type.equals(WLTConstants.COMP_REFPANEL_MULTI) || // ��ѡ����1
					str_type.equals(WLTConstants.COMP_REFPANEL_CUST) || // �Զ������
					str_type.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET) || //
					str_type.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || //
					str_type.equals(WLTConstants.COMP_REFPANEL_REGFORMAT) || // 
					str_type.equals(WLTConstants.COMP_REFPANEL_REGEDIT) || // ע�����
					str_type.equals(WLTConstants.COMP_DATE) || // ����
					str_type.equals(WLTConstants.COMP_DATETIME) || // ʱ��
					str_type.equals(WLTConstants.COMP_COLOR) || // ��ɫѡ���
					str_type.equals(WLTConstants.COMP_BIGAREA) || // ���ı���
					str_type.equals(WLTConstants.COMP_PICTURE) || // ͼ��ѡ���
					str_type.equals(WLTConstants.COMP_LINKCHILD) || // ͼ��ѡ���
					str_type.equals(WLTConstants.COMP_IMPORTCHILD) || // �����ӱ�
					str_type.equals(WLTConstants.COMP_OFFICE) // Office�ؼ�
			) { // ����Ǹ��ֲ���,���ڰ���7�ֲ���
				if (templetVO.getIslistautorowheight()) { //������Զ��Ÿ�,��xch/2012-05-04��!!
					cellRender = new ListCellRender_JTextArea(templetItemVOs[i]); //�����ı���ʾ��!!
				} else {
					cellRender = new ListCellRender_JLabel(templetItemVOs[i]); //
				}
				cellEditor = new ListCellEditor_Ref(templetItemVOs[i]);
			} else if (str_type.equals(WLTConstants.COMP_CHECKBOX)) { // ��ѡ��
				cellRender = new ListCellRender_CheckBox();
				cellEditor = new ListCellEditor_CheckBox(new JCheckBox(), templetItemVOs[i]);
			} else if (str_type.equals(WLTConstants.COMP_BUTTON)) { // ����ǰ�ť
				cellRender = new ListCellRender_Button(templetItemVOs[i]); // ��ť��ʾ��
				cellEditor = new ListCellEditor_Button(templetItemVOs[i]); // �����ı���༭��
			} else if (str_type.equals(WLTConstants.COMP_FILECHOOSE)) { // ������ļ�ѡ���
				cellRender = new ListCellRender_FileChoose(templetItemVOs[i]); // ��ť��ʾ��
				cellEditor = new ListCellEditor_FileChoose(templetItemVOs[i]); // �ļ�ѡ���
			} else if (str_type.equals(WLTConstants.COMP_STYLEAREA)) { //���ı���
				cellRender = new ListCellRender_JTextArea(templetItemVOs[i], true); //ʹ�ö����ı�����ʾ!!����ʾ��β�������
				cellEditor = new ListCellEditor_TextArea(templetItemVOs[i]); //
			} else if (str_type.equals(WLTConstants.COMP_SELFDESC)) {
				if (templetItemVOs[i].getUCDfVO() != null) {
					String str_render_cls = templetItemVOs[i].getUCDfVO().getConfValue("�б���Ⱦ��"); //
					if (str_render_cls != null && !str_render_cls.trim().equals("")) { //�����Ϊ��,���䴴��!!
						try {
							cellRender = (TableCellRenderer) Class.forName(str_render_cls).getConstructor(new Class[] { Pub_Templet_1_ItemVO.class }).newInstance(new Object[] { templetItemVOs[i] });
						} catch (Exception e) {
							e.printStackTrace();
							cellRender = new ListCellRender_JLabel(templetItemVOs[i]); //����ʧ�ܵĻ�,��ʹ��Ĭ��,��֤ҳ���ܳ���!
						}
					} else {
						cellRender = new ListCellRender_JLabel(templetItemVOs[i]); //
					}
					String str_editor_cls = templetItemVOs[i].getUCDfVO().getConfValue("�б�༭��"); //
					if (str_editor_cls != null && !str_editor_cls.trim().equals("")) {
						try {
							cellEditor = (TableCellEditor) Class.forName(str_editor_cls).getConstructor(new Class[] { Pub_Templet_1_ItemVO.class }).newInstance(new Object[] { templetItemVOs[i] });
						} catch (Exception e) {
							e.printStackTrace();
							cellEditor = new ListCellEditor_TextField(new JTextField(), templetItemVOs[i]);
						}
					} else {
						cellEditor = new ListCellEditor_TextField(new JTextField(), templetItemVOs[i]);
					}
				} else {
					cellRender = new ListCellRender_JLabel(templetItemVOs[i]); //
					cellEditor = new ListCellEditor_TextField(new JTextField(), templetItemVOs[i]);
				}
			} else {
				cellRender = null; //
				cellEditor = null;
			}

			allTableColumns[i] = new TableColumn(i + 1, li_width, cellRender, cellEditor); // ������
			allTableColumns[i].setIdentifier(templetItemVOs[i].getItemkey()); // Ψһ�Ա�ʶ
			if (ClientEnvironment.getInstance().isEngligh()) {
				allTableColumns[i].setHeaderValue(templetItemVOs[i].getItemname_e()); // �еı���
			} else {
				allTableColumns[i].setHeaderValue(templetItemVOs[i].getItemname()); // �еı���
			}

		}
		return allTableColumns;
	}

	/**
	 * �б�����Զ���һЩ���ٰ�ť
	 * 
	 * @return
	 */
	private BillButtonPanel getBillListBtnPanel(boolean _scrollable) {
		if (billListBtnPanel != null) {
			return billListBtnPanel;
		}

		billListBtnPanel = new BillButtonPanel(this.templetVO.getListcustbtns(), this, _scrollable); //
		billListBtnPanel.setOpaque(false);
		billListBtnPanel.paintButton(); //���ư�ť
		return billListBtnPanel;
	}

	public void repaintBillListButton() {
		billListBtnPanel.paintButton(); //
	}

	public HashVO getSelectHashVO() {
		int selectRow = treetable.getSelectedRow();
		Object obj[] = getValueAtRow(selectRow);
		HashVO hvo = new HashVO();
		for (int i = 0; i < obj.length; i++) {
			hvo.setAttributeValue(templetItemVOs[i + 1].getItemkey(), String.valueOf(obj[i]));
		}
		return hvo;
	}

	public BillVO getSelectedBillVO() {
		int selectRow = treetable.getSelectedRow();
		return getTableModel().getBillVOAt(selectRow);
	}

	public Object[] getValueAtRow(int _row) {
		return getTableModel().getValueAtRow(_row);
	}

	private DefaultTreeTableModelAdapter getTableModel() {
		return (DefaultTreeTableModelAdapter) treetable.getModel();
	}

	public void addBatchBillListButton(WLTButton[] _btns) {
		for (int i = 0; i < _btns.length; i++) {
			_btns[i].setBillPanelFrom(this); //�Ȱ󶨺��Լ�
		}
		billListBtnPanel.addBatchButton(_btns);
	}

	public void setRowHeight(int _row, int _height) {
		treetable.setRowHeight(_row, _height);
	}

	public String str_currsortcolumnkey = null;

	/**
	 * ˫��ĳһ��ʱ��ĳһ�н�������!!!!
	 * @param _point
	 */
	private void sortByColumn(Point _point) {
		int li_pos = treetable.getTableHeader().columnAtPoint(_point);
		TableColumn column = treetable.getColumnModel().getColumn(li_pos);
		Pub_Templet_1_ItemVO itemvo = templetItemVOs[li_pos];
		String str_key = (String) column.getIdentifier(); //
		if (itemvo != null) {
			str_key = itemvo.getItemkey();
		}
		if (str_currsortcolumnkey != null && !str_key.equals(str_currsortcolumnkey)) {
			li_currsorttype = 0;
		}
		if (li_currsorttype == 0 || li_currsorttype == -1) {
			//			sortObjs(this.all_realValueData, str_key, false, true); // 
			li_currsorttype = 1;
		} else {
			//			sortObjs(this.all_realValueData, str_key, false, false); // ����
			li_currsorttype = -1;
		}
		str_currsortcolumnkey = str_key; //
		try {
			treetablemodel.setRoot(getRootNodeByData());
		} catch (Exception e) {
			e.printStackTrace();
		}
		treetable.setRowHeight(30);
		treetable.getTableHeader().resizeAndRepaint(); //
		treetable.getTableHeader().invalidate(); //
		treetable.invalidate();

	}

	//ʵ������
	public void sortObjs(HashVO hvo[], String _item, boolean _isNumber, boolean _isdesc) {
		getTBUtil().sortHashVOs(hvo, new String[][] { { _item, (_isdesc ? "N" : "Y"), (_isNumber ? "Y" : "N") } });
	}

	public void QueryDataByCondition(String _wherecondition) {
		queryDataByDS(getDataSourceName(), _wherecondition);
	}

	private BillVO[] all_realValueData = null; //��ǰ����!!!

	private void queryDataByDS(String dataSourceName, String string) {
		try {
			all_realValueData = UIUtil.getBillVOsByDS(null, "select * from  " + templetVO.getSavedtablename(), templetVO);
			treetablemodel.setRoot(getRootNodeByData());
			scrollPanel_main.invalidate(); //��һ������һ��һ��Ҫ�����򣬿�����ʱ��Ļ�Ứ!
			scrollPanel_main.repaint(); //
			treetable.invalidate(); //
			treetable.repaint(); //
			this.revalidate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private DefaultMutableTreeNode getRootNodeByData() throws Exception {
		if (all_realValueData == null) {
			return new DefaultMutableTreeNode("������");
		}
		BillTreeTableDefaultMutableTreeNode root = new BillTreeTableDefaultMutableTreeNode("��");
		HashMap his = new HashMap();
		BillTreeTableDefaultMutableTreeNode allnodes[] = new BillTreeTableDefaultMutableTreeNode[all_realValueData.length];
		for (int i = 0; i < all_realValueData.length; i++) {
			all_realValueData[i].setToStringFieldName("name");
			BillTreeTableDefaultMutableTreeNode node = new BillTreeTableDefaultMutableTreeNode(all_realValueData[i]);
			his.put(all_realValueData[i].getStringValue("id"), node);
			allnodes[i] = node;
		}
		for (int i = 0; i < all_realValueData.length; i++) {
			String parentid = all_realValueData[i].getStringValue("parentid");
			if (his.containsKey(parentid)) {
				DefaultMutableTreeNode pnode = (DefaultMutableTreeNode) his.get(parentid);
				pnode.add(allnodes[i]);
			} else {
				root.add(allnodes[i]);
			}
		}
		return root;
	}

	public String getDataSourceName() {
		return null;
	}

	private FrameWorkMetaDataServiceIfc metaDataService = null; //Ԫ����Զ�̷���.

	private FrameWorkMetaDataServiceIfc getMetaDataService() {
		if (metaDataService == null) {
			try {
				metaDataService = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); ////
			} catch (Exception e) {
				e.printStackTrace();
				return null; //
			}
		}
		return metaDataService; //
	}

	public void expandAll(boolean expand) {
		treetable.expandAll(expand);
	}

	/**
	 * ��ͷ˫������ʱ��ʾ��ͬͼ��!!
	 */
	class SortHeaderRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 2924685007982535240L;

		Pub_Templet_1VO parentVO = null;

		Pub_Templet_1_ItemVO[] childVOs = null;

		boolean isadmin = true;

		public SortHeaderRenderer(Pub_Templet_1VO _templetVO, boolean isadmin) {
			this.parentVO = _templetVO;
			this.childVOs = _templetVO.getItemVos();
			this.isadmin = isadmin;
		}

		public Component getTableCellRendererComponent(JTable jtable, Object obj, boolean isSelected, boolean hasFocus, int _row, int _column) {
			JLabel label2 = (JLabel) super.getTableCellRendererComponent(jtable, obj, isSelected, hasFocus, _row, _column); //
			WLTLabel label = new WLTLabel(label2.getText(), WLTPanel.VERTICAL_TOP_TO_BOTTOM2); //
			label.setOpaque(true); //��͸��
			label.setHorizontalAlignment(SwingConstants.CENTER); //����
			label.setForeground(LookAndFeel.table_headFontColor);
			label.setBackground(LookAndFeel.tableheaderbgcolor);

			TableColumn column = jtable.getColumnModel().getColumn(_column);
			String str_key = (String) column.getIdentifier(); //
			if (TBUtil.isEmpty(str_key)) {
				return super.getTableCellRendererComponent(jtable, obj, isSelected, hasFocus, _row, _column);
			}
			String str_text = (obj != null ? obj.toString() : ""); //
			Pub_Templet_1_ItemVO itemVO = childVOs[_column];
			if (itemVO == null) {
				return super.getTableCellRendererComponent(jtable, obj, isSelected, hasFocus, _row, _column);
			}
			String str_tooltiptext = itemVO.getItemtiptext(); //
			if (str_tooltiptext == null || str_tooltiptext.trim().equals("")) { ////
				str_tooltiptext = str_text;
			}
			boolean _ismustinput = false;
			this.setHorizontalAlignment(SwingConstants.CENTER); //
			if (itemVO == null) {
				label.setForeground(java.awt.Color.RED); //
				label.setToolTipText("û���ҵ���Ӧ��ItemKey"); //
			} else {
				if ("Y".equals(itemVO.getIsmustinput2())) {
					_ismustinput = true;
				}
				if (itemVO.isNeedSave() && !itemVO.isCanSave()) { //
					label.setForeground(java.awt.Color.RED); //
				} else {
					label.setForeground(LookAndFeel.table_headFontColor); //��ͷ������ɫ
				}
			}

			if (jtable != null) {
				JTableHeader jtableheader = jtable.getTableHeader();
				if (jtableheader != null) {
					String fg_bgcolor = itemVO.getShowbgcolor();
					if (fg_bgcolor == null || fg_bgcolor.trim().equals("")) {
						label.setBackground(LookAndFeel.tableheaderbgcolor);
					} else {
						String[] fg_bgcolors = fg_bgcolor.split(",");
						if (fg_bgcolors.length == 1) { //ֻ����ǰ��ɫ������ɫĬ��
							label.setForeground(getTBUtil().getColor(fg_bgcolors[0])); //����ǰ��ɫ����������ɫ
							label.setBackground(LookAndFeel.tableheaderbgcolor); //����Ĭ�ϱ���ɫ
						} else if (fg_bgcolors.length > 1) {
							label.setForeground(getTBUtil().getColor(fg_bgcolors[0])); //����ǰ��ɫ����������ɫ
							label.setBackground(getTBUtil().getColor(fg_bgcolors[1])); //���ñ���ɫ
						}
					}
					label.setFont(jtableheader.getFont());
				}
			}
			//sunfujun/20120720/�����ͷ��������
			String str_realText = getTBUtil().getLableRealText(str_text, ClientEnvironment.getInstance().isAdmin(), _ismustinput && isHeadShowAsterisk(), itemVO.isViewColumn(), itemVO.isNeedSave(), isHeadShowAsterisk()); //
			label.setText(str_realText); ////
			label.setToolTipText(str_tooltiptext); //
			if (_ismustinput && isHeadShowAsterisk()) {//�ʴ���Ŀ�о�������ֶζ��Ǳ������ͷ�����к��ǣ�������������ͷ��Ҫ��ʾ���ǣ������Ӵ˹��ˡ����/2012-06-18��
				label.addStrItemColor("*", Color.RED); //
			}
			if (itemVO.getItemkey().equalsIgnoreCase(str_currsortcolumnkey)) {
				if (li_currsorttype == 1) {
					label.setIcon(ImageIconFactory.getUpArrowIcon());
				} else if (li_currsorttype == -1) {
					label.setIcon(ImageIconFactory.getDownArrowIcon());
				} else {
					label.setIcon(null);
				}
			} else {
				label.setIcon(null);
			}
			label.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, LookAndFeel.tableHeadLineClolr)); //
			return label;
		}

		private boolean isHeadShowAsterisk() {
			return true;
		}
	}

	private TBUtil getTBUtil() {
		if (tBUtil == null) {
			tBUtil = new TBUtil();
		}
		return tBUtil; //
	}
}

class TestFrame3 extends JFrame {

	public TestFrame3() {
		super("jxtreetable_with_checkbox, hacks");
		getContentPane().setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
	}
}