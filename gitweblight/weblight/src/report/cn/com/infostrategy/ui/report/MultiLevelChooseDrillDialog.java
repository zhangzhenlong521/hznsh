package cn.com.infostrategy.ui.report;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * ��ȡʱѡ��ĶԻ���
 * @author xch
 *
 */
public class MultiLevelChooseDrillDialog extends BillDialog {

	private static final long serialVersionUID = 6210230660775593956L;

	private BillListPanel billlistPanel_all, billlistPanel_row, billlistPanel_col; //

	private String[] all_groupname = null; //

	private WLTButton btn_confirm, btn_cancel;
	private int closeType = -1;

	private String[] rowGroupnames = null;
	private String[] colGroupnames = null; //

	public MultiLevelChooseDrillDialog(Container _parent, String[] _allgroupname) {
		super(_parent, "ѡ����ȡ����һά��", 600, 600); //
		this.all_groupname = _allgroupname; //
		initialize();
	}

	/**
	 * ��ʼҳ��..
	 */
	private void initialize() {
		this.getContentPane().setLayout(new BorderLayout()); //
		try {
			billlistPanel_all = new BillListPanel(new DefaultTMO("��ѡά��", new String[][] { { "name", "ά������", "138" } })); //
			billlistPanel_row = new BillListPanel(new DefaultTMO("�ϱ�ͷά��", new String[][] { { "name", "ά������", "138" } })); //
			billlistPanel_col = new BillListPanel(new DefaultTMO("���ͷά��", new String[][] { { "name", "ά������", "138" } })); //

			billlistPanel_all.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //
			billlistPanel_row.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //
			billlistPanel_col.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //
			
			billlistPanel_all.setItemEditable(false);
			billlistPanel_row.setItemEditable(false);
			billlistPanel_col.setItemEditable(false);

			billlistPanel_all.setCanShowCardInfo(false); //
			billlistPanel_row.setCanShowCardInfo(false); //
			billlistPanel_col.setCanShowCardInfo(false); //

			billlistPanel_all.setTableToolTipText("ѡ��һ����¼ֱ���϶������������Ŀ���"); //

			billlistPanel_all.getBillListBtn("������").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onAddRow();
				}
			});

			billlistPanel_all.getBillListBtn("������").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onAddCol();
				}
			});

			billlistPanel_row.getBillListBtn("��ȥ").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onRemoveRow();
				}
			});

			billlistPanel_col.getBillListBtn("��ȥ").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onRemoveCol();
				}
			});

			JLabel label_help = new JLabel("<html>˵��:<br>1.����ѡ����ȡ���϶����л�����.<br>2.���������,������ͷ�Ͻ�����ȡ.<br>3.���������,������ͷ������ȡ</html>");
			label_help.setBackground(LookAndFeel.systembgcolor);
			label_help.setOpaque(true); //
			WLTSplitPane splitPanel_1 = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, billlistPanel_all, billlistPanel_row); //
			WLTSplitPane splitPanel_2 = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, billlistPanel_col, label_help); //

			splitPanel_1.setDividerLocation(290); //
			splitPanel_2.setDividerLocation(290); //

			WLTSplitPane splitPanel_3 = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, splitPanel_1, splitPanel_2); //
			splitPanel_3.setDividerLocation(300); //

			for (int i = 0; i < all_groupname.length; i++) {
				int li_newrow = billlistPanel_all.addEmptyRow(false); //
				billlistPanel_all.setValueAt(new StringItemVO(all_groupname[i]), li_newrow, "name"); //
				billlistPanel_all.clearSelection(); //
				billlistPanel_all.moveToTop(); //
				billlistPanel_all.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT); //
			}

			//�����϶��¼�..
			billlistPanel_all.getTable().putClientProperty("dragtype", "ALL"); //
			billlistPanel_row.getTable().putClientProperty("dragtype", "ROW"); //
			billlistPanel_col.getTable().putClientProperty("dragtype", "COL"); //

			new MyDraggListener(billlistPanel_all.getTable()); //�����϶�����..
			new MyDropListener(billlistPanel_all.getTable()); //

			new MyDraggListener(billlistPanel_row.getTable()); //�����϶�����..			
			new MyDropListener(billlistPanel_row.getTable()); //

			new MyDraggListener(billlistPanel_col.getTable()); //�����϶�����..			
			new MyDropListener(billlistPanel_col.getTable()); //

			new MyDropListener(billlistPanel_all.getMainScrollPane()); //
			new MyDropListener(billlistPanel_row.getMainScrollPane()); //
			new MyDropListener(billlistPanel_col.getMainScrollPane()); //

			this.getContentPane().add(splitPanel_3, BorderLayout.CENTER); //
			this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		btn_confirm = new WLTButton("ȷ��");
		btn_cancel = new WLTButton("ȡ��");

		btn_confirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onConfirm(); //
			}
		}); //

		btn_cancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				onCancel(); //
			}
		}); //

		panel.add(btn_confirm); //
		panel.add(btn_cancel); //

		return panel;
	}

	protected void onRemoveRow() {
		moveDataBetweenBillListPanel(billlistPanel_row, billlistPanel_all); //
	}

	private void onRemoveCol() {
		moveDataBetweenBillListPanel(billlistPanel_col, billlistPanel_all); //
	}

	private void onAddRow() {
		moveDataBetweenBillListPanel(billlistPanel_all, billlistPanel_row); //
	}

	private void onAddCol() {
		moveDataBetweenBillListPanel(billlistPanel_all, billlistPanel_col); //
	}

	public void onConfirm() {
		int li_count_row = billlistPanel_row.getRowCount();
		int li_count_col = billlistPanel_col.getRowCount();
		if (li_count_row == 0 && li_count_col == 0) {
			MessageBox.show(this, "����ѡ��һ������ͳ�Ƶ���!"); //
			return; //
		}

		rowGroupnames = new String[li_count_row];
		for (int i = 0; i < rowGroupnames.length; i++) {
			rowGroupnames[i] = "" + billlistPanel_row.getValueAt(i, "name");
		}

		colGroupnames = new String[li_count_col];
		for (int i = 0; i < colGroupnames.length; i++) {
			colGroupnames[i] = "" + billlistPanel_col.getValueAt(i, "name");
		}

		closeType = 1; //
		this.dispose();
	}

	public void onCancel() {
		closeType = 2;
		this.dispose();
	}

	public String[] getRowGroupnames() {
		return rowGroupnames;
	}

	public String[] getColGroupnames() {
		return colGroupnames;
	}

	public int getCloseType() {
		return closeType;
	}

	/**
	 * �϶��б�..
	 * @param _source
	 * @param _target
	 */
	private void moveDataBetweenBillListPanel(BillListPanel _source, BillListPanel _target) {
		if (_source == _target) {
			return; //�����һ������,�򷵻�!
		}

		//System.out.println("��������!!");
		BillVO[] billVOs = _source.getSelectedBillVOs(); //
		for (int i = 0; i < billVOs.length; i++) {
			int li_row = _target.addEmptyRow(false); //
			_target.setBillVOAt(li_row, billVOs[i].deepClone()); //��Ŀ���м���!!!
		}
		_source.removeSelectedRows();
		_source.clearSelection(); //
	}

	class MyDropListener implements DropTargetListener {
		public MyDropListener(JComponent _component) {
			new DropTarget(_component, this); //
		}

		public void dragEnter(DropTargetDragEvent evt) {
			// Called when the user is dragging and enters this drop target.
		}

		public void dragOver(DropTargetDragEvent evt) {
			// Called when the user is dragging and moves over this drop target.
		}

		public void dragExit(DropTargetEvent evt) {
			// Called when the user is dragging and leaves this drop target.
		}

		public void dropActionChanged(DropTargetDragEvent evt) {
			// Called when the user changes the drag action between copy or move.
		}

		public void drop(DropTargetDropEvent evt) {
			// Called when the user finishes or cancels the drag operation.
			//((StringSelection)evt.getTransferable()).
			//System.out.println(evt.getCurrentDataFlavors()[0].getHumanPresentableName());
			String str_dragsource = null;
			try {
				Object obj = evt.getTransferable().getTransferData(evt.getCurrentDataFlavors()[0]);
				str_dragsource = (String) obj;
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}

			if (str_dragsource != null) {
				BillListPanel billList_source = null;
				if (str_dragsource.equals("ALL")) {
					billList_source = billlistPanel_all;
				} else if (str_dragsource.equals("ROW")) {
					billList_source = billlistPanel_row;
				} else if (str_dragsource.equals("COL")) {
					billList_source = billlistPanel_col;
				}

				BillListPanel billList_target = null;
				if (evt.getDropTargetContext().getComponent() == billlistPanel_all.getTable()) {
					billList_target = billlistPanel_all;
				} else if (evt.getDropTargetContext().getComponent() == billlistPanel_row.getTable()) {
					billList_target = billlistPanel_row;
				} else if (evt.getDropTargetContext().getComponent() == billlistPanel_col.getTable()) {
					billList_target = billlistPanel_col;
				} else if (evt.getDropTargetContext().getComponent() == billlistPanel_all.getMainScrollPane()) {
					billList_target = billlistPanel_all;
				} else if (evt.getDropTargetContext().getComponent() == billlistPanel_row.getMainScrollPane()) {
					billList_target = billlistPanel_row;
				} else if (evt.getDropTargetContext().getComponent() == billlistPanel_col.getMainScrollPane()) {
					billList_target = billlistPanel_col;
				}

				if (billList_source != null && billList_target != null) {
					moveDataBetweenBillListPanel(billList_source, billList_target); //
				}
			}
		}
	}

	class MyDraggListener implements DragGestureListener, DragSourceListener {
		DragSource dragSource;
		JTable dragtable = null;

		public MyDraggListener(JTable table) {
			dragtable = table; //
			dragSource = new DragSource();
			dragSource.createDefaultDragGestureRecognizer(table, DnDConstants.ACTION_COPY_OR_MOVE, this);
		}

		public void dragGestureRecognized(DragGestureEvent evt) {
			String str_tabtype = (String) dragtable.getClientProperty("dragtype");
			Transferable t = new StringSelection(str_tabtype);
			dragSource.startDrag(evt, DragSource.DefaultCopyDrop, t, this);
		}

		public void dragEnter(DragSourceDragEvent evt) {
			// Called when the user is dragging this drag source and enters
			// the drop target.
		}

		public void dragOver(DragSourceDragEvent evt) {
			// Called when the user is dragging this drag source and moves
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

			// Called when the user finishes or cancels the drag operation.
			//System.out.println("ע���¼�!!"); //
		}
	}

	class TMO_SumField extends AbstractTMO {
		private static final long serialVersionUID = 8057184541083294474L;

		public HashVO getPub_templet_1Data() {
			HashVO vo = new HashVO(); //
			vo.setAttributeValue("templetcode", "���������"); //ģ�����,��������޸�
			vo.setAttributeValue("templetname", "���������"); //ģ������
			vo.setAttributeValue("templetname_e", "���������"); //ģ������
			return vo;
		}

		public HashVO[] getPub_templet_1_itemData() {
			Vector vector = new Vector();
			HashVO itemVO = null;

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ischecked"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "ѡ��"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "ischecked"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "60"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "N"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "4"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "fieldname"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�ɲ���ͳ�Ƶ���"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "fieldname"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "170"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "N"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "4"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "computefunctionname"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "ͳ������"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "computetype"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "������"); //�ؼ�����
			itemVO
					.setAttributeValue("comboxdesc",
							"select 'sum' id,'' code,'���' name from wltdual union all select 'count' id,'' code,'��¼��' name from wltdual union all select 'max' id,'' code,'���ֵ' name from wltdual union all select 'min' id,'' code,'��Сֵ' name from wltdual union all select 'avg' id,'' code,'ƽ����' name from wltdual"); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "85"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "N"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "4"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);

			return (HashVO[]) vector.toArray(new HashVO[0]);
		}
	}

}
