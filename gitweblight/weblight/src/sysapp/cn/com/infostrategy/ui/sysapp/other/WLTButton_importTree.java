package cn.com.infostrategy.ui.sysapp.other;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;
import cn.com.infostrategy.ui.report.cellcompent.ExcelUtil;

/**
 * 树形结构通用导入【李春娟/2018-08-16】
 * 根据按钮所在模板获得保存表、父节点字段、显示字段、排序字段。
 * 默认从第三行导入，前两行可设置表头。
 * 第一列为一级目录编码，第二列为一级目录名称，第三列为二级目录编码，第四列为二级目录名称，以此类推，支持最多五级目录导入。
 * 如无需导入编码，则编码列为空即可。
 * 相同父亲目录可合并单元格。
 * 支持多次导入，数据不进行覆盖。
 * @author lcj
 *
 */
public class WLTButton_importTree implements WLTActionListener {
	BillPanel billpanel = null;

	@Override
	public void actionPerformed(WLTActionEvent event) throws Exception {
		billpanel = event.getBillPanelFrom();

		onImport();
		if (billpanel instanceof BillTreePanel) {
			((BillTreePanel) billpanel).refreshTree();
		}
	}

	private void onImport() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				if (f.getAbsolutePath().toLowerCase().endsWith(".xls") || f.getAbsolutePath().toLowerCase().endsWith(".xlsx") || f.isDirectory()) {
					return true;
				}
				return false;
			}

			public String getDescription() {
				return "请导入.xls或.xlsx类型的文件";
			}
		});
		int flag = chooser.showOpenDialog(billpanel);
		if (flag == 1) {
			return;
		}
		File file = chooser.getSelectedFile();
		if (file == null) {
			return;
		}
		Pub_Templet_1VO vo = billpanel.getTempletVO();//
		final String id = vo.getTreepk();
		final String parentid = vo.getTreeparentpk();
		final String name = vo.getTreeviewfield();//显示的字段名称
		final String tablename = vo.getSavedtablename();
		final String seq = vo.getTreeseqfield();

		ExcelUtil util = new ExcelUtil();
		final String data[][] = util.getExcelFileData(file.getAbsolutePath());
		new SplashWindow(billpanel, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				try {
					//支持五级目录导入
					String parentid1 = "";//一级目录
					String parentid2 = "";//二级目录
					String parentid3 = "";//三级目录
					String parentid4 = "";//四级目录
					String linkcode1 = "";//一级目录linkcode
					String linkcode2 = "";//二级目录linkcode
					String linkcode3 = "";//三级目录linkcode
					String linkcode4 = "";//四级目录linkcode
					String linkcode5 = "";//五级目录linkcode
					int seq1 = 1;//一级目录seq
					int seq2 = 1;//二级目录seq
					int seq3 = 1;//三级目录seq
					int seq4 = 1;//四级目录seq
					int seq5 = 1;//五级目录seq
					String str_seq1 = UIUtil.getStringValueByDS(null, "select max(" + seq + ")+1 from " + tablename + " where  " + parentid + " is null  and " + seq + " is not null ");//一级目录最大seq
					if (str_seq1 != null && !str_seq1.trim().equals("")) {
						seq1 = Integer.parseInt(str_seq1);
					}
					InsertSQLBuilder insert = new InsertSQLBuilder(tablename);
					ArrayList sqlList = new ArrayList();
					for (int i = 2; i < data.length; i++) {//从第三行开始取数
						for (int j = 1; j < data[0].length; j = j + 2) {
							if (data[i][j] != null && !data[i][j].trim().equals("")) {//如果名称不为空
								String pk_id = UIUtil.getSequenceNextValByDS(null, "S_" + tablename + "");
								insert.putFieldValue(id, pk_id);
								insert.putFieldValue("code", data[i][j - 1].trim());//编号
								insert.putFieldValue(name, data[i][j].trim());//名称
								if (j == 1) {
									insert.putFieldValue(parentid, "");
									parentid1 = pk_id;//设置当前一级目录id
									insert.putFieldValue(seq, seq1);
									linkcode1 = ("" + (10000 + seq1)).substring(1, 5);
									insert.putFieldValue("linkcode", linkcode1);
									seq1++;
									seq2 = 1;
								} else if (j == 3) {
									insert.putFieldValue(parentid, parentid1);
									parentid2 = pk_id;//设置当前二级目录id
									insert.putFieldValue(seq, seq2);
									linkcode2 = linkcode1 + ("" + (10000 + seq2)).substring(1, 5);
									insert.putFieldValue("linkcode", linkcode2);
									seq2++;
									seq3 = 1;
								} else if (j == 5) {
									insert.putFieldValue(parentid, parentid2);
									parentid3 = pk_id;//设置当前三级目录id
									insert.putFieldValue(seq, seq3);
									linkcode3 = linkcode2 + ("" + (10000 + seq3)).substring(1, 5);
									insert.putFieldValue("linkcode", linkcode3);
									seq3++;
									seq4 = 1;
								} else if (j == 7) {
									insert.putFieldValue(parentid, parentid3);
									parentid4 = pk_id;//设置当前四级目录id
									insert.putFieldValue(seq, seq4);
									linkcode4 = linkcode2 + ("" + (10000 + seq4)).substring(1, 5);
									insert.putFieldValue("linkcode", linkcode4);
									seq4++;
									seq5 = 1;
								} else if (j == 9) {
									insert.putFieldValue(parentid, parentid4);
									insert.putFieldValue(seq, seq5);
									linkcode5 = linkcode4 + ("" + (10000 + seq5)).substring(1, 5);
									insert.putFieldValue("linkcode", linkcode5);
									seq5++;
								}
								sqlList.add(insert.getSQL());
							}
						}
					}
					if (sqlList.size() > 0) {
						UIUtil.executeBatchByDS(null, sqlList);
					}
				} catch (Exception a) {
					a.printStackTrace();
				}
			}
		}, 366, 366);
		//MessageBox.show(billpanel, "导入成功");
	}

}
