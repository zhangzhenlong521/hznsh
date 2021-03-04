package cn.com.pushworld.salary.ui.tools;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.report.cellcompent.ExcelUtil;

/***
 * ����������Ա��Ϣ��[��Ӫ��/2014-05-20]
 * */
public class UpdateUserInfo extends AbstractWorkPanel implements ActionListener {
	private WLTButton button = null;
	private JLabel label = null;
	private JLabel label2 = null;
	private JPanel mainPanel = null;

	@Override
	public void initialize() {
		mainPanel = new JPanel(new FlowLayout());
		label = new JLabel("��ѡ���¼������Ϣ��excel���");
		button = new WLTButton("������Ա��Ϣ");
		label2 = new JLabel("Ŀǰֻ֧�ָ���[����]���޸�������Ϣ����������Ա�š����ϱ��ս�ҽ�Ʊ��ա�ס��������.");
		label2.setForeground(Color.RED);
		button.addActionListener(this);
		mainPanel.add(label);
		mainPanel.add(button);
		mainPanel.add(label2);
		this.add(mainPanel);
		this.setVisible(true);
		;

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == button) {
			chooseFile();
		}
	}

	private void chooseFile() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("��ѡ��һ��Excel�ļ�");
		chooser.setApproveButtonText("ѡ��");
		FileFilter filter = new FileNameExtensionFilter("Microsoft Office Excel ������", "xls", "xlsx");
		chooser.setFileFilter(filter);
		int flag = chooser.showOpenDialog(this);
		if (flag != JFileChooser.APPROVE_OPTION || chooser.getSelectedFile() == null) {
			return;
		}
		final String str_path = chooser.getSelectedFile().getAbsolutePath();

		if (!(str_path.toLowerCase().endsWith(".xls") || str_path.toLowerCase().endsWith(".xlsx"))) {
			MessageBox.show(this, "��ѡ��һ��Excel�ļ�!");
			return;
		}

		String[][] excelFileData = new ExcelUtil().getExcelFileData(str_path, 0);
		doUpdate(excelFileData);

	}

	private void doUpdate(String[][] date) {
		HashMap titlemap = new HashMap();
		List sqllist = new ArrayList();
		int codeindex = 0;
		boolean isadd = false;
		if (date == null) {
			return;
		}
		for (int i = 0; i < date.length; i++) {
			if (date[i] == null) {
				return;
			}
			StringBuilder sql = new StringBuilder();
			if (i != 0) {
				sql.append("update sal_personinfo set ");
				isadd = false;
			}
			for (int j = 0; j < date[i].length; j++) {
				if (i == 0) {
					if (date[i][j].equals("����")) {
						titlemap.put(date[i][j], "name");
					} else if (date[i][j].equals("����")) {
						titlemap.put(date[i][j], "code");
					} else if (date[i][j].equals("��Ա��")) {
						titlemap.put(date[i][j], "tellerno");
					} else if (date[i][j].equals("���ϱ���")) {
						titlemap.put(date[i][j], "pension");
					} else if (date[i][j].equals("ס��������")) {
						titlemap.put(date[i][j], "housingfund");
					} else if (date[i][j].equals("ҽ�Ʊ���")) {
						titlemap.put(date[i][j], "medicare");
					}else if(date[i][j].equals("�����ս�")){
						titlemap.put(date[i][j], "otherglod");
					}
				} else if (titlemap.get(date[0][j]) != null) {
					if (j == date[i].length - 1) {
						sql.append(titlemap.get(date[0][j]) + "='" + date[i][j] + "' ");
						sql.append(" where code ='" + date[i][codeindex] + "'");
						isadd = true;
					} else if (date[0][j].equals("����")) {
						codeindex = j;
					} else {
						sql.append(titlemap.get(date[0][j]) + "='" + date[i][j] + "', ");
					}
				}
			}
			if (isadd) {
				sqllist.add(sql.toString());
			}
		}
		if (sqllist.size() > 0) {
			try {
				UIUtil.executeBatchByDS(null, sqllist);
				MessageBox.show(mainPanel, "��Ա��Ϣ���³ɹ�.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
