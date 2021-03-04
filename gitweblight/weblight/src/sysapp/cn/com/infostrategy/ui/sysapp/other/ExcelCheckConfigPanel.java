package cn.com.infostrategy.ui.sysapp.other;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JSplitPane;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryListener;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryEvent;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.ChildTableCommUIIntercept;

/**
 * ���ݵ���У���������.
 * @author push
 *
 */
public class ExcelCheckConfigPanel extends AbstractWorkPanel implements ChildTableCommUIIntercept, BillListSelectListener, ActionListener, BillListAfterQueryListener, BillListHtmlHrefListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private WLTButton btn_delete_his, btn_exit_his, btn_exit_look;
	protected BillListPanel parentBillListPanel = null; // ����,��ڱ�,���
	protected BillListPanel childBilllListPanel = null; // �ӱ�,������,�����,�ұ�
	private WLTButton btn_insert, btn_update, btn_delete, btn_list; // ��,ɾ,��
	private BillListPanel parentList = null;
	private BillListPanel billListPanel_his = null;
	private BillListPanel billList = null;
	private BillDialog dialog = null;
	private BillDialog dialog_look = null;

	public String getChildTempletCode() {
		return "PUB_IMPORT_EXCEL_POLICY_CODE1";
	}

	public String getParentTempletCode() {
		return "EXCEL_TAB_CODE1";
	}

	private String getParentAssocField() {
		return "id";
	}

	public String getChildAssocField() {
		return "excelid";
	}

	public boolean isShowsystembutton() {
		return true;
	}

	public String getCustomerpanel() {
		return null;
	}

	public BillListPanel getChildBillListPanel() {
		return childBilllListPanel;
	}

	private int getOrientation() {
		return JSplitPane.VERTICAL_SPLIT;
	}

	public BillListPanel getParentBillListPanel() {
		return parentBillListPanel;
	}

	public void onBillListAfterQuery(BillListAfterQueryEvent _event) {
		childBilllListPanel.clearTable(); //
	}

	public void initialize() {
		try {
			parentBillListPanel = new BillListPanel(getParentTempletCode()); // ����
			parentBillListPanel.setItemEditable(false); //

			parentBillListPanel.addBillListSelectListener(this); //
			parentBillListPanel.addBillListAfterQueryListener(this);

			childBilllListPanel = new BillListPanel(getChildTempletCode()); //
			btn_insert = new WLTButton("����"); //
			btn_insert.addActionListener(this); //
			btn_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); // �޸�
			btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE); // ɾ��
			btn_list = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD); // ���
			childBilllListPanel.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_list });
			childBilllListPanel.repaintBillListButton(); // ˢ�°�ť

			JSplitPane splitPanel = new WLTSplitPane(getOrientation(), parentBillListPanel, childBilllListPanel);
			splitPanel.setDividerLocation(250);
			splitPanel.setOneTouchExpandable(true);

			this.setLayout(new BorderLayout()); //
			this.add(splitPanel, BorderLayout.CENTER); //

			// ����Ӱ�ť!
			parentList = getParentBillListPanel(); //
			parentList.addBillListHtmlHrefListener(this);

			// �ӱ�Ӱ�ť!!
			billList = getChildBillListPanel(); //
			WLTButton btn_moveup = WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEUP); //
			WLTButton btn_movedown = WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEDOWN); //
			WLTButton btn_save = WLTButton.createButtonByType(WLTButton.LIST_SAVE, "����˳��"); //
			billList.addBatchBillListButton(new WLTButton[] { btn_moveup, btn_movedown, btn_save }); //
			billList.repaintBillListButton(); //

		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		// TODO Auto-generated method stub
		if (_event.getItemkey().equals("UPDATETIME")) {
			billListPanel_his = new BillListPanel(new DefaultTMO("", new String[][] { { "�ϴ�ʱ��", "150" }, { "��������", "70" }, { "�鿴", "70" }, { "ɾ��", "70" } }));
			billListPanel_his.putValue(getHvos());
			billListPanel_his.getTempletItemVO("�鿴").setListishtmlhref(true);
			billListPanel_his.getTempletItemVO("ɾ��").setListishtmlhref(true);

			btn_delete_his = new WLTButton("ɾ��");
			btn_delete_his.addActionListener(this);

			btn_exit_his = new WLTButton("�ر�");
			btn_exit_his.addActionListener(this);

			billListPanel_his.addBillListHtmlHrefListener((BillListHtmlHrefListener) this);

			WLTPanel btnPanel = new WLTPanel(1, new FlowLayout(1));
			btnPanel.add(btn_exit_his);

			dialog = new BillDialog(this, "�����ϴ���ʷ", 500, 400);
			dialog.getContentPane().setLayout(new BorderLayout());
			dialog.getContentPane().add(billListPanel_his, "Center");
			dialog.getContentPane().add(btnPanel, "South");
			dialog.setVisible(true);
		} else if (_event.getItemkey().equals("�鿴")) {
			onLook_his();
		} else if (_event.getItemkey().equals("ɾ��")) {
			onDelete_his();
		}

	}

	private HashVO[] getHvos() {
		BillVO billVO_ = parentList.getSelectedBillVO();
		String table_name = billVO_.getStringValue("tablename");

		HashVO[] hvs = null;
		try {
			String sql = "select distinct year, month, creattime from " + table_name + " order by creattime";
			hvs = UIUtil.getHashVoArrayByDS(null, sql);
			for (int i = 0; i < hvs.length; i++) {
				hvs[i].setAttributeValue("�ϴ�ʱ��", hvs[i].getStringValue("creattime", ""));
				String year = hvs[i].getStringValue("year", "");
				String month = hvs[i].getStringValue("month", "");
				if (month.equals("")) {
					hvs[i].setAttributeValue("��������", year + "��");
				} else {
					hvs[i].setAttributeValue("��������", year + "��" + month + "��");
				}
				hvs[i].setAttributeValue("�鿴", "�鿴");
				hvs[i].setAttributeValue("ɾ��", "ɾ��");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return hvs;
	}

	private void onLook_his() {
		int li_selRow = billListPanel_his.getSelectedRow();
		if (li_selRow < 0) {
			MessageBox.showSelectOne(billListPanel_his);
			return;
		}

		BillVO billVO_ = parentList.getSelectedBillVO();
		String table_name = billVO_.getStringValue("tablename");

		BillVO billVO = billListPanel_his.getSelectedBillVO();
		String upload_time = billVO.getStringValue("�ϴ�ʱ��");
		String data_time = billVO.getStringValue("��������");
		String year = data_time.substring(0, data_time.indexOf("��"));
		String month = "";
		if (data_time.indexOf("��") > 0) {
			month = data_time.substring(data_time.indexOf("��") + 1, data_time.indexOf("��"));
		}

		HashVO[] hvs = null;
		String sql = "select * from " + table_name + " where creattime='" + upload_time + "' and " + getWhere(year, month);

		try {
			hvs = UIUtil.getHashVoArrayByDS(null, sql);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (hvs != null && hvs.length > 0) {
			String[][] strs_header = new String[hvs[0].getKeys().length][2];
			strs_header[0] = new String[] { "id", "85" };
			strs_header[1] = new String[] { "year", "85" };
			strs_header[2] = new String[] { "month", "85" };
			strs_header[3] = new String[] { "creattime", "85" };
			for (int i = 4; i < hvs[0].getKeys().length; i++) {
				strs_header[i] = new String[] { getColumnName(i + 1 - 4), "85" };
			}

			BillListPanel billListPanel = new BillListPanel(new DefaultTMO("", strs_header));
			billListPanel.putValue(hvs);

			billListPanel.getTempletItemVO("year").setItemname("���");
			billListPanel.getTempletItemVO("month").setItemname("�·�");
			billListPanel.getTempletItemVO("creattime").setItemname("�ϴ�ʱ��");

			billListPanel.getTable().getColumn("year").setHeaderValue("���");
			billListPanel.getTable().getColumn("month").setHeaderValue("�·�");
			billListPanel.getTable().getColumn("creattime").setHeaderValue("�ϴ�ʱ��");

			billListPanel.getTempletItemVO("id").setCardisshowable(false);
			if (!(data_time.indexOf("��") > 0)) {
				billListPanel.getTempletItemVO("month").setCardisshowable(false);
			}

			billListPanel.setItemVisible("id", false);
			if (!(data_time.indexOf("��") > 0)) {
				billListPanel.setItemVisible("month", false);
			}

			btn_exit_look = new WLTButton("�ر�");
			btn_exit_look.addActionListener(this);

			WLTPanel btnPanel = new WLTPanel(1, new FlowLayout(1));
			btnPanel.add(btn_exit_look);

			dialog_look = new BillDialog(this, "���ݲ鿴", 800, 600);
			dialog_look.getContentPane().setLayout(new BorderLayout());
			dialog_look.getContentPane().add(billListPanel, "Center");
			dialog_look.getContentPane().add(btnPanel, "South");
			dialog_look.setVisible(true);
		} else {
			MessageBox.show(this, "�����ѱ�ɾ��!");
		}
	}

	private void onDelete_his() {
		int li_selRow = billListPanel_his.getSelectedRow();
		if (li_selRow < 0) {
			MessageBox.showSelectOne(billListPanel_his);
			return;
		}

		BillVO billVO_ = parentList.getSelectedBillVO();
		String table_name = billVO_.getStringValue("tablename");

		BillVO billVO = billListPanel_his.getSelectedBillVO();
		String upload_time = billVO.getStringValue("�ϴ�ʱ��");
		String data_time = billVO.getStringValue("��������");
		String year = data_time.substring(0, data_time.indexOf("��"));
		String month = "";
		if (data_time.indexOf("��") > 0) {
			month = data_time.substring(data_time.indexOf("��") + 1, data_time.indexOf("��"));
		}

		String del_sql = "delete from " + table_name + " where creattime='" + upload_time + "' and " + getWhere(year, month);

		try {
			if (MessageBox.confirm(this, "��ȷ��Ҫɾ�������ϴ�������?")) {
				UIUtil.executeUpdateByDS(null, del_sql);
				billListPanel_his.removeSelectedRows(); // ɾ��һ������
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ��ȡ�����մ��ж�����
	private String getWhere(String year, String month) {
		String sql_where = "";
		if (year != null && !year.equals("")) {
			sql_where += " and year='" + year + "' ";
		}
		if (month != null && !month.equals("")) {
			sql_where += " and month='" + month + "' ";
		}

		if (!sql_where.equals("")) {
			sql_where = sql_where.substring(4, sql_where.length());
		}

		return sql_where;
	}

	private String getColumnName(int columnNum) {
		String result = "";

		int first;
		int last;
		if (columnNum > 256)
			columnNum = 256;
		first = columnNum / 27;
		last = columnNum - (first * 26);

		if (first > 0)
			result = String.valueOf((char) (first + 64));

		if (last > 0)
			result = result + String.valueOf((char) (last + 64));
		return result.toUpperCase();
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		BillVO selVO = _event.getCurrSelectedVO(); // ѡ�õ�����!!

		if (selVO == null) {
			return;
		}
		String str_parentidValue = selVO.getStringValue(getParentAssocField()); //�õ������id
		childBilllListPanel.QueryDataByCondition(getChildAssocField() + "='" + str_parentidValue + "'");
		//���ӱ��excelid���������id
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_insert) {
			onInsert(); //
		} else if (e.getSource() == btn_exit_his) {
			dialog.dispose();
		} else if (e.getSource() == btn_exit_look) {
			dialog_look.dispose();
		} else if (e.getSource() == b_up) {
			onBillListRowUp();
			childlistpanel.saveData();
		} else if (e.getSource() == b_down) {
			onBillListRowDown();
			childlistpanel.saveData();
		}
	}

	protected void onInsert() {
		BillVO billVO = parentBillListPanel.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, "��ѡ��[" + parentBillListPanel.getTempletVO().getTempletname() + "]��һ����¼���д˲���!"); //
			return; //
		}
		HashMap<Object, Object> defaultValueMap = new HashMap<Object, Object>(); //
		defaultValueMap.put(getChildAssocField(), billVO.getStringValue(getParentAssocField())); // //
		childBilllListPanel.doInsert(defaultValueMap); // ���������!!
	}

	private WLTButton b_up, b_down;
	BillListPanel childlistpanel;

	public void afterInitialize(BillPanel panel) throws Exception {
		childlistpanel = (BillListPanel) panel;
		b_up = new WLTButton(UIUtil.getImage("office_081.gif"));//office_081.gif
		b_up.addActionListener(this);
		b_up.setBillPanelFrom(childlistpanel);
		b_down = new WLTButton(UIUtil.getImage("office_059.gif"));
		b_down.addActionListener(this);
		b_down.setBillPanelFrom(childlistpanel);
		childlistpanel.addCustButton(b_up);
		childlistpanel.addCustButton(b_down);
	}

	/**
	 * �б�������
	 * @throws Exception
	 */
	private void onBillListRowDown() {
		childlistpanel.moveDownRow(); //����
		int li_rowcount = childlistpanel.getRowCount();
		String seqfild = childlistpanel.getTempletVO().getTreeseqfield();//�������������������У����ø��ֶ����򣬷���Ĭ��Ϊseq
		if (seqfild == null || seqfild.trim().equals("")) {
			seqfild = "seq";
		}
		for (int i = 0; i < li_rowcount; i++) {
			//��ǰ��������һ������������ƻ����Ʋ���������󣬷������ݲ�û�б䣬��Ϊ��ǰ����û�д������seqΪ�յ����
			if (childlistpanel.getValueAt(i, seqfild) == null || !((i + 1) + "").equals(childlistpanel.getRealValueAtModel(i, seqfild))) {
				childlistpanel.setValueAt(new StringItemVO("" + (i + 1)), i, seqfild); //
				childlistpanel.setRowStatusAs(i, WLTConstants.BILLDATAEDITSTATE_UPDATE); //
			}
		}
	}

	/**
	 * �б�������
	 * @throws Exception
	 */
	private void onBillListRowUp() {
		childlistpanel.moveUpRow(); //����
		int li_rowcount = childlistpanel.getRowCount();
		String seqfild = childlistpanel.getTempletVO().getTreeseqfield();//�������������������У����ø��ֶ����򣬷���Ĭ��Ϊseq
		if (seqfild == null || seqfild.trim().equals("")) {
			seqfild = "seq";
		}
		for (int i = 0; i < li_rowcount; i++) {
			//��ǰ��������һ������������ƻ����Ʋ���������󣬷������ݲ�û�б䣬��Ϊ��ǰ����û�д������seqΪ�յ���������Ҵ�����seqfildֵ�����ֵ������
			//ע��ڶ����ж���billList.getRealValueAtModel()�õ������ַ�������billList.getValueAt()�õ�����StringItemVO����
			if (childlistpanel.getValueAt(i, seqfild) == null || !((i + 1) + "").equals(childlistpanel.getRealValueAtModel(i, seqfild))) {
				childlistpanel.setValueAt(new StringItemVO("" + (i + 1)), i, seqfild); //
				childlistpanel.setRowStatusAs(i, WLTConstants.BILLDATAEDITSTATE_UPDATE); //
			}
		}
	}

}
