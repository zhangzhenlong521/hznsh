package cn.com.infostrategy.ui.sysapp.transferdb;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.FrameWorkCommServiceIfc;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class MWPanel_TransferDB extends AbstractWorkPanel implements ActionListener {

	private JLabel label_source = null; //
	private JComboBox comboxsourceDS = null; // 源数据源..
	private JTextField textfield_1 = null; //

	private JButton btn_search = null; //

	private JButton btn_move = null; //xml转移

	private JButton btn_transfer = null; // 转移数据

	private JButton btn_validate = null; // 验证表是否含有关键字

	private JLabel label_dest = null; //
	private JComboBox comboxdestDS = null; // 目标数据源..

	private JCheckBox check_struct = new JCheckBox("表结构", true); //
	private JCheckBox check_data = new JCheckBox("表数据", true); //
	private JCheckBox check_quick = new JCheckBox("立即提交", false); //
	private BillListPanel billList_source = null; //

	private JScrollPane scrollPane = null; //
	private JTextArea textArea = null; //

	@Override
	public void initialize() {
		this.setLayout(null); //

		label_source = new JLabel("来源库", JLabel.RIGHT); //
		DataSourceVO[] dsVOs = ClientEnvironment.getInstance().getDataSourceVOs(); //
		comboxsourceDS = new JComboBox(); //
		for (int i = 0; i < dsVOs.length; i++) {
			comboxsourceDS.addItem(dsVOs[i]); //
		}
		textfield_1 = new JTextField(); //
		btn_search = new WLTButton("查询"); //
		btn_search.addActionListener(this); //

		btn_transfer = new WLTButton("转移"); //
		btn_transfer.addActionListener(this); //

		btn_validate = new WLTButton("验证");
		btn_validate.addActionListener(this);

		btn_move = new WLTButton("安全转移");
		btn_move.addActionListener(this);
		label_dest = new JLabel("目标库", JLabel.RIGHT); //
		comboxdestDS = new JComboBox(); //
		for (int i = 0; i < dsVOs.length; i++) {
			comboxdestDS.addItem(dsVOs[i]); //
		}

		billList_source = new BillListPanel(new DefaultTMO("所有物理表", new String[][] { { "表名", "150" }, { "类型", "70" }, { "说明", "160" } })); //

		scrollPane = new JScrollPane(); //
		textArea = new JTextArea(); //
		scrollPane.getViewport().add(textArea); //

		label_source.setBounds(10, 10, 60, 20); //
		comboxsourceDS.setBounds(70, 10, 125, 20); //
		textfield_1.setBounds(195, 10, 120, 20); //
		btn_search.setBounds(315, 10, 50, 20); //

		btn_transfer.setBounds(370, 10, 50, 20); //
		btn_validate.setBounds(425, 10, 50, 20);

		label_dest.setBounds(520, 10, 60, 20); //
		comboxdestDS.setBounds(585, 10, 110, 20); //

		check_struct.setBounds(695, 10, 80, 20); //
		check_data.setBounds(775, 10, 80, 20); //
		check_quick.setBounds(860, 10, 80, 20);

		billList_source.setBounds(10, 35, 450, 450); //
		scrollPane.setBounds(465, 35, 500, 450); //

		btn_move.setBounds(480, 10, 60, 20);

		check_quick.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBox check = (JCheckBox) e.getSource();
				if (check.isSelected()) {
					MessageBox.show(MWPanel_TransferDB.this, "此操作会每500条数据会独立提交一次，使得同一张表的数据不一定能在同一事务中\r\n不建议使用！");
				}
			}
		});

		this.add(label_source); //
		this.add(comboxsourceDS); //
		this.add(textfield_1); //
		this.add(btn_search); //
		this.add(btn_transfer); //
		this.add(btn_validate); //
		this.add(btn_move);
		this.add(label_dest); //
		this.add(comboxdestDS); //
		this.add(check_struct); //
		this.add(check_data); //
		this.add(check_quick);
		this.add(billList_source); //
		this.add(scrollPane); //

	}

	/**
	 * 点击按钮执行
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_search) {
			onQueryTable(); //
		} else if (e.getSource() == btn_transfer) {
			onTransfer(); //
		} else if (e.getSource() == btn_validate) {
			onValidate(); //
		} else if (e.getSource() == btn_move) {
			onMove();
		}
	}

	private void onQueryTable() {
		DataSourceVO dsVO = (DataSourceVO) comboxsourceDS.getSelectedItem(); //
		if (dsVO == null) {
			MessageBox.show(this, "请选择一个数据源"); //
		}
		try {
			billList_source.clearTable(); //
			FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class); //
			String[][] str_tabdesc = service.getAllSysTableAndDescr(dsVO.getName(), null, true, true); //
			String str_tname = textfield_1.getText(); //
			for (int i = 0; i < str_tabdesc.length; i++) { //遍历各个表!!
				if (!str_tname.equals("") && str_tabdesc[i][0].indexOf(str_tname) < 0) { //如果不为空,且又匹配不上!!!
					continue;
				}
				int li_newRow = billList_source.addEmptyRow(false); //
				billList_source.setValueAt(new StringItemVO(str_tabdesc[i][0]), li_newRow, "表名"); //
				billList_source.setValueAt(new StringItemVO(str_tabdesc[i][1]), li_newRow, "类型"); //
				billList_source.setValueAt(new StringItemVO(str_tabdesc[i][2]), li_newRow, "说明"); //
			}

		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		}
	}

	/**
	 * 转移数据
	 */
	private void onTransfer() {
		DataSourceVO sourceDSVO = (DataSourceVO) comboxsourceDS.getSelectedItem(); //
		if (sourceDSVO == null) {
			MessageBox.show(this, "请选择来源库!"); //
			return;
		}

		DataSourceVO destDSVO = (DataSourceVO) comboxdestDS.getSelectedItem(); //
		if (destDSVO == null) {
			MessageBox.show(this, "请选择目标库!"); //
			return;
		}

		final String str_dsname1 = sourceDSVO.getName(); //
		final String str_dsname2 = destDSVO.getName(); //
		if (str_dsname1.equals(str_dsname2)) { //
			MessageBox.show(this, "来源库与目标库不能相同!"); //
			return;
		}

		BillVO[] billVOs = billList_source.getSelectedBillVOs(); //
		if (billVOs.length == 0) {
			MessageBox.show(this, "请选择要导入的表!"); //
			return;
		}

		final String[] str_tablename = new String[billVOs.length]; //
		for (int i = 0; i < str_tablename.length; i++) {
			str_tablename[i] = billVOs[i].getStringValue("表名"); //
		}

		if (JOptionPane.showConfirmDialog(this, "您确定要导入这[" + str_tablename.length + "]个表吗?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) { // 提示是否真的导入
			return;
		}

		Thread appThread = new Thread() {
			public void run() {
				btn_transfer.setEnabled(false);
				long ll_1 = System.currentTimeMillis(); //
				for (int i = 0; i < str_tablename.length; i++) {
					try {
						int li_insertcount = UIUtil.getMetaDataService().transferDB(str_dsname1, str_dsname2, str_tablename[i], check_struct.isSelected(), check_data.isSelected()); //
						String str_msg = ""; //
						if (check_struct.isSelected()) {
							str_msg = str_msg + "创建表[" + str_tablename[i] + "]成功!"; //
						}
						if (check_data.isSelected()) {
							str_msg = str_msg + "插入表[" + str_tablename[i] + "]共[" + li_insertcount + "]条记录!"; //
						}

						msg(str_msg + "\r\n"); //
					} catch (Exception ex) {
						ex.printStackTrace(); //
						msg("处理表[" + str_tablename[i] + "]失败,详细原因见控制台!\r\n"); //
					}
				}
				long ll_2 = System.currentTimeMillis(); //
				msg("处理所有表结束,共耗时[" + (ll_2 - ll_1) + "]!\r\n"); //
				btn_transfer.setEnabled(true);
			}
		};
		appThread.start(); //
	}

	/**
	 * 验证数据
	 */
	private void onValidate() {
		DataSourceVO sourceDSVO = (DataSourceVO) comboxsourceDS.getSelectedItem(); //
		if (sourceDSVO == null) {
			MessageBox.show(this, "请选择来源库!"); //
			return;
		}

		DataSourceVO destDSVO = (DataSourceVO) comboxdestDS.getSelectedItem(); //
		if (destDSVO == null) {
			MessageBox.show(this, "请选择目标库!"); //
			return;
		}

		final String str_dsname1 = sourceDSVO.getName(); //
		final String str_dsname2 = destDSVO.getName(); //
		if (str_dsname1.equals(str_dsname2)) { //
			MessageBox.show(this, "来源库与目标库不能相同!"); //
			return;
		}

		BillVO[] billVOs = billList_source.getSelectedBillVOs(); //
		if (billVOs.length == 0) {
			MessageBox.show(this, "请选择要验证的表!"); //
			return;
		}

		final String[] str_tablename = new String[billVOs.length]; //
		for (int i = 0; i < str_tablename.length; i++) {
			str_tablename[i] = billVOs[i].getStringValue("表名"); //
		}
		try {
			String primarykey = UIUtil.getMetaDataService().getCollidePKByTableName(str_tablename, destDSVO.getDbtype()); //
			msg(primarykey + "\r\n"); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}

	}

	private void onMove() {
		DataSourceVO sourceDSVO = (DataSourceVO) comboxsourceDS.getSelectedItem(); //
		if (sourceDSVO == null) {
			MessageBox.show(this, "请选择来源库!"); //
			return;
		}

		DataSourceVO destDSVO = (DataSourceVO) comboxdestDS.getSelectedItem(); //
		if (destDSVO == null) {
			MessageBox.show(this, "请选择目标库!"); //
			return;
		}

		final String str_dsname1 = sourceDSVO.getName(); //
		final String str_dsname2 = destDSVO.getName(); //
		if (str_dsname1.equals(str_dsname2)) { //
			MessageBox.show(this, "来源库与目标库不能相同!"); //
			return;
		}
		BillVO[] billVOs = billList_source.getSelectedBillVOs(); //
		if (billVOs.length == 0) {
			MessageBox.show(this, "请选择要导入的表!"); //
			return;
		}
		List tableList = new ArrayList();
		List viewList = new ArrayList();
		for (int i = 0; i < billVOs.length; i++) {
			if (billVOs[i].getStringValue("类型").equals("TABLE")) {
				tableList.add(billVOs[i].getStringValue("表名")); //	
			} else { //试图
				viewList.add(billVOs[i].getStringValue("表名"));
			}
		}
		final String[] str_tablename = (String[]) tableList.toArray(new String[0]);
		final String[] str_viewname = (String[]) viewList.toArray(new String[0]);
		if (JOptionPane.showConfirmDialog(this, "您确定要导入这[" + str_tablename.length + "]个表吗?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) { // 提示是否真的导入
			return;
		}
		Thread appThread = new Thread() {
			public void run() {
				FrameWorkCommServiceIfc service = null;
				HashMap tablemap = new HashMap();
				try {
					service = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class);
					String[] db2_tables = service.getAllSysTables(str_dsname2, null); //
					for (int i = 0; i < db2_tables.length; i++) {
						tablemap.put(db2_tables[i].toLowerCase(), null);
					}
				} catch (WLTRemoteException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} //
				btn_move.setEnabled(false);
				long ll_1 = System.currentTimeMillis(); //
				HashMap conditon = new HashMap(); //条件参数
				conditon.put("check_data", check_data.isSelected()); //是否迁移数据
				conditon.put("check_quick", check_quick.isSelected());
				for (int i = 0; i < str_tablename.length; i++) { //创建表
					try {
						String str_msg = UIUtil.getMetaDataService().safeMoveData(str_dsname1, str_dsname2, str_tablename[i], tablemap, "TABLE", conditon); //
						msg(str_msg); //
					} catch (Exception ex) {
						ex.printStackTrace(); //
						msg("处理表[" + str_tablename[i] + "]失败,详细原因见控制台!\r\n"); //
					}
				}
				long ll_2 = System.currentTimeMillis(); //
				for (int i = 0; i < str_viewname.length; i++) { //创建视图
					try {
						String str_msg = UIUtil.getMetaDataService().safeMoveData(str_dsname1, str_dsname2, str_viewname[i], null, "VIEW", null);
						msg(str_msg);
					} catch (Exception e) {
						msg("处理视图[" + str_viewname[i] + "]失败,详细原因见控制台!\r\n"); //
						e.printStackTrace();

					} //
				}
				msg("处理所有表、视图结束,共耗时[" + (ll_2 - ll_1) + "]!\r\n"); //
				btn_move.setEnabled(true);
			}
		};
		appThread.start(); //
	}

	private void msg(final String _text) {
		textArea.append(_text); //
		textArea.select(textArea.getText().length(), textArea.getText().length()); //
	}

}
