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
 * ���νṹͨ�õ��롾���/2018-08-16��
 * ���ݰ�ť����ģ���ñ�������ڵ��ֶΡ���ʾ�ֶΡ������ֶΡ�
 * Ĭ�ϴӵ����е��룬ǰ���п����ñ�ͷ��
 * ��һ��Ϊһ��Ŀ¼���룬�ڶ���Ϊһ��Ŀ¼���ƣ�������Ϊ����Ŀ¼���룬������Ϊ����Ŀ¼���ƣ��Դ����ƣ�֧������弶Ŀ¼���롣
 * �����赼����룬�������Ϊ�ռ��ɡ�
 * ��ͬ����Ŀ¼�ɺϲ���Ԫ��
 * ֧�ֶ�ε��룬���ݲ����и��ǡ�
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
				return "�뵼��.xls��.xlsx���͵��ļ�";
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
		final String name = vo.getTreeviewfield();//��ʾ���ֶ�����
		final String tablename = vo.getSavedtablename();
		final String seq = vo.getTreeseqfield();

		ExcelUtil util = new ExcelUtil();
		final String data[][] = util.getExcelFileData(file.getAbsolutePath());
		new SplashWindow(billpanel, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				try {
					//֧���弶Ŀ¼����
					String parentid1 = "";//һ��Ŀ¼
					String parentid2 = "";//����Ŀ¼
					String parentid3 = "";//����Ŀ¼
					String parentid4 = "";//�ļ�Ŀ¼
					String linkcode1 = "";//һ��Ŀ¼linkcode
					String linkcode2 = "";//����Ŀ¼linkcode
					String linkcode3 = "";//����Ŀ¼linkcode
					String linkcode4 = "";//�ļ�Ŀ¼linkcode
					String linkcode5 = "";//�弶Ŀ¼linkcode
					int seq1 = 1;//һ��Ŀ¼seq
					int seq2 = 1;//����Ŀ¼seq
					int seq3 = 1;//����Ŀ¼seq
					int seq4 = 1;//�ļ�Ŀ¼seq
					int seq5 = 1;//�弶Ŀ¼seq
					String str_seq1 = UIUtil.getStringValueByDS(null, "select max(" + seq + ")+1 from " + tablename + " where  " + parentid + " is null  and " + seq + " is not null ");//һ��Ŀ¼���seq
					if (str_seq1 != null && !str_seq1.trim().equals("")) {
						seq1 = Integer.parseInt(str_seq1);
					}
					InsertSQLBuilder insert = new InsertSQLBuilder(tablename);
					ArrayList sqlList = new ArrayList();
					for (int i = 2; i < data.length; i++) {//�ӵ����п�ʼȡ��
						for (int j = 1; j < data[0].length; j = j + 2) {
							if (data[i][j] != null && !data[i][j].trim().equals("")) {//������Ʋ�Ϊ��
								String pk_id = UIUtil.getSequenceNextValByDS(null, "S_" + tablename + "");
								insert.putFieldValue(id, pk_id);
								insert.putFieldValue("code", data[i][j - 1].trim());//���
								insert.putFieldValue(name, data[i][j].trim());//����
								if (j == 1) {
									insert.putFieldValue(parentid, "");
									parentid1 = pk_id;//���õ�ǰһ��Ŀ¼id
									insert.putFieldValue(seq, seq1);
									linkcode1 = ("" + (10000 + seq1)).substring(1, 5);
									insert.putFieldValue("linkcode", linkcode1);
									seq1++;
									seq2 = 1;
								} else if (j == 3) {
									insert.putFieldValue(parentid, parentid1);
									parentid2 = pk_id;//���õ�ǰ����Ŀ¼id
									insert.putFieldValue(seq, seq2);
									linkcode2 = linkcode1 + ("" + (10000 + seq2)).substring(1, 5);
									insert.putFieldValue("linkcode", linkcode2);
									seq2++;
									seq3 = 1;
								} else if (j == 5) {
									insert.putFieldValue(parentid, parentid2);
									parentid3 = pk_id;//���õ�ǰ����Ŀ¼id
									insert.putFieldValue(seq, seq3);
									linkcode3 = linkcode2 + ("" + (10000 + seq3)).substring(1, 5);
									insert.putFieldValue("linkcode", linkcode3);
									seq3++;
									seq4 = 1;
								} else if (j == 7) {
									insert.putFieldValue(parentid, parentid3);
									parentid4 = pk_id;//���õ�ǰ�ļ�Ŀ¼id
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
		//MessageBox.show(billpanel, "����ɹ�");
	}

}
