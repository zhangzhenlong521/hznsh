package cn.com.infostrategy.ui.sysapp.dictmanager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JSplitPane;

import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.FrameWorkCommServiceIfc;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_TextField;

public class WLTTableDfManageMentPanel2 extends AbstractWorkPanel implements BillListSelectListener {
	private BillListPanel billListTable = null;
	private BillListPanel billListColumns = null;
	private QueryCPanel_TextField textField = null;
	@Override
	public void initialize() {
		

		
		billListTable = new BillListPanel(new DefaultTMO("����", new String[][] { { "����", "100" }, { "˵��", "150" } }));
		billListTable.addBillListSelectListener(this);
		billListTable.getQuickQueryPanel().setVisible(true);		
		
		billListTable.getQuickQueryPanel().addBillQuickActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				String tableNamePattern = null;
				if(!"".equals(textField.getText())){
					tableNamePattern = "%"+textField.getText().trim()+"%";		 //ע��Ҫд��%��			
				}
				onshow(tableNamePattern);	
			}}
		);
		
		onshow(null);
		textField = new QueryCPanel_TextField(billListTable.getTempletItemVO("����"));
		billListTable.getQuickQueryPanel().add(textField);
		
		billListColumns = new BillListPanel(new DefaultTMO("����", new String[][] { { "����", "100" }, { "˵��", "150" }, { "����", "150" }, { "����", "150" }, { "�Ƿ��ܿ�", "150" } }));
		WLTTabbedPane wlttp = new WLTTabbedPane();
		WLTSplitPane sp = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, billListTable, billListColumns);
		WLTSplitPane sp1 = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, billListTable, billListColumns);
		wlttp.addTab("��ѯ���ݿ��", sp1);
		wlttp.addTab("��ѯƽ̨��", sp);
		
		this.add(wlttp);
	}
	
	public void onshow(String tableNamePattern){
		 billListTable.removeAllRows();
		try {
			FrameWorkCommServiceIfc service= (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class);
			String[] s = service.getAllSysTables(null, tableNamePattern);		
			for (int i = 0; i < s.length; i++) {
				int li_newrow = billListTable.addEmptyRow(false);  //��������Ϊfalse
				billListTable.setValueAt(new StringItemVO(s[i]), li_newrow, "����");
				billListTable.setValueAt(new StringItemVO(s[i]), li_newrow, "˵��");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		// TODO Auto-generated method stub
		billListColumns.removeAllRows();
		String tableName = null;
		StringItemVO tableNameVO = (StringItemVO) _event.getCurrSelectedVO().getObject("����");
		if (tableNameVO != null) {
			tableName = tableNameVO.getStringValue();
			try {
				FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class);
				
				TableDataStruct tableDataStruct = service.getTableDataStructByDS(null, "select * from " + tableName + " where 1=1");
				
				if (tableDataStruct!= null) {
					for (int i = 0; i <tableDataStruct.getHeaderName().length; i++) {
						int li_newrow = billListColumns.addEmptyRow(false);
						billListColumns.setValueAt(new StringItemVO(tableDataStruct.getHeaderName()[i]), li_newrow, "����");
						billListColumns.setValueAt(new StringItemVO(tableDataStruct.getHeaderName()[i]), li_newrow, "˵��");
						billListColumns.setValueAt(new StringItemVO(tableDataStruct.getHeaderTypeName()[i]), li_newrow, "����");
						billListColumns.setValueAt(new StringItemVO(tableDataStruct.getHeaderLength()[i]+""), li_newrow, "����");
						billListColumns.setValueAt(new StringItemVO(tableDataStruct.getIsNullAble()[i]), li_newrow, "�Ƿ��ܿ�");
					}
				}
			} catch (WLTRemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
