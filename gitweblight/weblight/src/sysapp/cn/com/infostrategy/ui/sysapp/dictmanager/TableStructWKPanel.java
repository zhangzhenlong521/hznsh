package cn.com.infostrategy.ui.sysapp.dictmanager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.FrameWorkCommServiceIfc;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc;

/**
 * ��ṹ����!!��ϵͳ��ʵ����������!!
 * @author xch
 *
 */
public class TableStructWKPanel extends AbstractWorkPanel implements BillListSelectListener, ActionListener {

	private BillListPanel billList_table = null; //��
	private BillListPanel billList_column = null; //��

	private WLTButton btn_createTable, btn_droptable, btn_exportSQL, btn_viewWarnSQL; //
	private WLTButton btn_createCol, btn_modifyCol, btn_dropColumn; //�����ֶ�,�޸��ֶ�,ɾ���ֶ�

	@Override
	public void initialize() {
		try {
			billList_table = new BillListPanel(new DefaultTMO("���������", new String[][] { { "����", "200" }, { "����", "80" }, { "˵��", "200" } })); //
			billList_column = new BillListPanel(new DefaultTMO("�ֶ�", new String[][] { { "�ֶ���", "150" }, { "�ֶ�˵��", "150" }, { "�ֶ�����", "100" }, { "�ֶο��", "100" } })); //
			WLTSplitPane split = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, billList_table, billList_column); //
			split.setDividerLocation(300); //
			this.add(split); //

			//��������!!
			FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class); //
			String[][] str_tabdesc = service.getAllSysTableAndDescr(null, null, true, true); //
			for (int i = 0; i < str_tabdesc.length; i++) {
				int li_newRow = billList_table.addEmptyRow(false); //
				billList_table.setValueAt(new StringItemVO(str_tabdesc[i][0]), li_newRow, "����"); //
				billList_table.setValueAt(new StringItemVO(str_tabdesc[i][1]), li_newRow, "����"); //
				billList_table.setValueAt(new StringItemVO(str_tabdesc[i][2]), li_newRow, "˵��"); //
			}
			billList_table.addBillListSelectListener(this); //

			//�����ز�����ť!!
			btn_createTable = new WLTButton("������"); //
			btn_droptable = new WLTButton("ɾ����"); //
			btn_exportSQL = new WLTButton("����SQL"); //
			btn_viewWarnSQL = new WLTButton("�鿴������"); //
			btn_createTable.addActionListener(this);
			btn_droptable.addActionListener(this);
			btn_exportSQL.addActionListener(this);
			btn_viewWarnSQL.addActionListener(this);
			billList_table.addBatchBillListButton(new WLTButton[] { btn_createTable, btn_droptable, btn_exportSQL, btn_viewWarnSQL }); //
			billList_table.repaintBillListButton(); //

			//�ֶε���ز�����ť!!
			btn_createCol = new WLTButton("�����ֶ�"); //
			btn_modifyCol = new WLTButton("�޸��ֶ�"); //
			btn_dropColumn = new WLTButton("ɾ���ֶ�"); //
			btn_createCol.addActionListener(this); //
			btn_modifyCol.addActionListener(this); //
			btn_dropColumn.addActionListener(this); //
			billList_column.addBatchBillListButton(new WLTButton[] { btn_createCol, btn_modifyCol, btn_dropColumn }); //
			billList_column.repaintBillListButton(); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	//ѡ��仯�¼�!!
	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		try {
			billList_column.clearTable(); //���������!!!
			BillVO billVO = _event.getCurrSelectedVO(); //
			String str_tableName = billVO.getStringValue("����"); //����!!
			TableDataStruct tdst = UIUtil.getTableDataStructByDS(null, "select * from " + str_tableName + " where 1=2"); //��ṹ!!
			String[] str_names = tdst.getHeaderName(); //
			String[] str_type = tdst.getHeaderTypeName(); //
			int[] li_length = tdst.getHeaderLength(); //
			for (int i = 0; i < str_names.length; i++) {
				int li_newrow = billList_column.addEmptyRow(false); //
				billList_column.setValueAt(new StringItemVO(str_names[i]), li_newrow, "�ֶ���"); //
				billList_column.setValueAt(new StringItemVO(str_type[i]), li_newrow, "�ֶ�����"); //
				billList_column.setValueAt(new StringItemVO("" + li_length[i]), li_newrow, "�ֶο��"); //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_createTable) {
			onCreateTable(); //
		} else if (e.getSource() == btn_droptable) {
			onDropTable(); //
		} else if (e.getSource() == btn_exportSQL) {
			onExportSQL(); //
		} else if (e.getSource() == btn_viewWarnSQL) {
			onViewWarnSQL(); //
		} else if (e.getSource() == btn_createCol) {
			onCreateColumn(); //
		} else if (e.getSource() == btn_modifyCol) {
			onModifyColumn(); //
		} else if (e.getSource() == btn_dropColumn) {
			onDropColumn(); //
		}
	}

	private void onCreateTable() {
		// TODO ����һ���Ի���,Ȼ����һ�����,����������,������Զ�ƴ��create table�ű�,Ȼ��ִ��֮,���������!!!
	}

	private void onDropTable() {
		//ѡ��һ����,�����ִ��[drop table **],����!
	}

	private void onExportSQL() {
		// ѡ��һ����,ƴ��������create�ű�,�����Ի�����ʾ!!!
	}

	//�鿴�����SQL!!!
	private void onViewWarnSQL() {
		try {
			SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); ////!!!
			String[] str_sqls = service.getCascadeWarnSQL(true); //
			StringBuilder sb_text = new StringBuilder(); //
			for (int i = 0; i < str_sqls.length; i++) { //
				sb_text.append(str_sqls[i] + ";\r\n"); //
			}
			JTextArea textArea = new JTextArea(sb_text.toString()); ////
			JScrollPane scrollPanel = new JScrollPane(textArea); //
			BillDialog dialog = new BillDialog(this, "�鿴�����ݵ�SQL", 800, 500); //
			dialog.getContentPane().add(scrollPanel); //
			dialog.setVisible(true); //Ԥ��!!!
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		}
	}

	//������
	private void onCreateColumn() {
		// TODO Auto-generated method stub

	}

	//�޸���,������������!!
	private void onModifyColumn() {

	}

	//ɾ����!
	private void onDropColumn() {
		// TODO Auto-generated method stub
	}

}
