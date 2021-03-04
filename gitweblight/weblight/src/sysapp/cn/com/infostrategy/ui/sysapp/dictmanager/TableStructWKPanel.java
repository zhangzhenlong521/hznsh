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
 * 表结构管理!!即系统中实际物理表管理!!
 * @author xch
 *
 */
public class TableStructWKPanel extends AbstractWorkPanel implements BillListSelectListener, ActionListener {

	private BillListPanel billList_table = null; //表
	private BillListPanel billList_column = null; //列

	private WLTButton btn_createTable, btn_droptable, btn_exportSQL, btn_viewWarnSQL; //
	private WLTButton btn_createCol, btn_modifyCol, btn_dropColumn; //创建字段,修改字段,删除字段

	@Override
	public void initialize() {
		try {
			billList_table = new BillListPanel(new DefaultTMO("所有物理表", new String[][] { { "表名", "200" }, { "类型", "80" }, { "说明", "200" } })); //
			billList_column = new BillListPanel(new DefaultTMO("字段", new String[][] { { "字段名", "150" }, { "字段说明", "150" }, { "字段类型", "100" }, { "字段宽度", "100" } })); //
			WLTSplitPane split = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, billList_table, billList_column); //
			split.setDividerLocation(300); //
			this.add(split); //

			//构造数据!!
			FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class); //
			String[][] str_tabdesc = service.getAllSysTableAndDescr(null, null, true, true); //
			for (int i = 0; i < str_tabdesc.length; i++) {
				int li_newRow = billList_table.addEmptyRow(false); //
				billList_table.setValueAt(new StringItemVO(str_tabdesc[i][0]), li_newRow, "表名"); //
				billList_table.setValueAt(new StringItemVO(str_tabdesc[i][1]), li_newRow, "类型"); //
				billList_table.setValueAt(new StringItemVO(str_tabdesc[i][2]), li_newRow, "说明"); //
			}
			billList_table.addBillListSelectListener(this); //

			//表的相关操作按钮!!
			btn_createTable = new WLTButton("创建表"); //
			btn_droptable = new WLTButton("删除表"); //
			btn_exportSQL = new WLTButton("导出SQL"); //
			btn_viewWarnSQL = new WLTButton("查看脏数据"); //
			btn_createTable.addActionListener(this);
			btn_droptable.addActionListener(this);
			btn_exportSQL.addActionListener(this);
			btn_viewWarnSQL.addActionListener(this);
			billList_table.addBatchBillListButton(new WLTButton[] { btn_createTable, btn_droptable, btn_exportSQL, btn_viewWarnSQL }); //
			billList_table.repaintBillListButton(); //

			//字段的相关操作按钮!!
			btn_createCol = new WLTButton("新增字段"); //
			btn_modifyCol = new WLTButton("修改字段"); //
			btn_dropColumn = new WLTButton("删除字段"); //
			btn_createCol.addActionListener(this); //
			btn_modifyCol.addActionListener(this); //
			btn_dropColumn.addActionListener(this); //
			billList_column.addBatchBillListButton(new WLTButton[] { btn_createCol, btn_modifyCol, btn_dropColumn }); //
			billList_column.repaintBillListButton(); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	//选择变化事件!!
	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		try {
			billList_column.clearTable(); //先清空数据!!!
			BillVO billVO = _event.getCurrSelectedVO(); //
			String str_tableName = billVO.getStringValue("表名"); //表名!!
			TableDataStruct tdst = UIUtil.getTableDataStructByDS(null, "select * from " + str_tableName + " where 1=2"); //表结构!!
			String[] str_names = tdst.getHeaderName(); //
			String[] str_type = tdst.getHeaderTypeName(); //
			int[] li_length = tdst.getHeaderLength(); //
			for (int i = 0; i < str_names.length; i++) {
				int li_newrow = billList_column.addEmptyRow(false); //
				billList_column.setValueAt(new StringItemVO(str_names[i]), li_newrow, "字段名"); //
				billList_column.setValueAt(new StringItemVO(str_type[i]), li_newrow, "字段类型"); //
				billList_column.setValueAt(new StringItemVO("" + li_length[i]), li_newrow, "字段宽度"); //
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
		// TODO 弹出一个对话框,然后有一个表格,可以新增行,点击后自动拼成create table脚本,然后执行之,创建物理表!!!
	}

	private void onDropTable() {
		//选中一个表,点击后执行[drop table **],即可!
	}

	private void onExportSQL() {
		// 选中一个表,拼成这个表的create脚本,弹出对话框显示!!!
	}

	//查看警告的SQL!!!
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
			BillDialog dialog = new BillDialog(this, "查看脏数据的SQL", 800, 500); //
			dialog.getContentPane().add(scrollPanel); //
			dialog.setVisible(true); //预览!!!
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		}
	}

	//新增列
	private void onCreateColumn() {
		// TODO Auto-generated method stub

	}

	//修改列,包括类型与宽度!!
	private void onModifyColumn() {

	}

	//删除列!
	private void onDropColumn() {
		// TODO Auto-generated method stub
	}

}
