package cn.com.infostrategy.ui.sysapp.dictmanager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;

public class ComboxDictManagerPanel extends JPanel implements BillListSelectListener, ActionListener {

	private static final long serialVersionUID = 1L; ////

	private BillListPanel billListPanel_1 = null; //
	private BillListPanel billListPanel_2 = null; //
	private JButton btn_new, btn_del, btn_save, btn_refresh, btn_moveup, btn_movedown;
	private JTextArea textArea = new JTextArea(); //

	public ComboxDictManagerPanel() {
		JPanel panel_1 = new WLTPanel(WLTPanel.HORIZONTAL_FROM_MIDDLE, new FlowLayout(FlowLayout.LEFT), LookAndFeel.defaultShadeColor1, false); //
		JButton btn_1 = new WLTButton("新增类型"); //
		JButton btn_2 = new WLTButton("刷新类型"); //
		JButton btn_deltype = new WLTButton("删除类型"); //
		JButton btn_3 = new WLTButton("导出XML格式下拉框"); //
		JButton btn_4 = new WLTButton("导入XML格式下拉框"); //
		btn_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onNewType(); //
			}
		}); //
		btn_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onExportComboboxXML(); //
			}
		}); //
		btn_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onImportComboboxXML(); //
			}
		}); //

		btn_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshType(); //
			}
		}); //

		btn_deltype.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onDelType(); //
			}
		}); //

		panel_1.add(btn_1); //
		panel_1.add(btn_deltype); //
		panel_1.add(btn_3); //
		panel_1.add(btn_4); //
		//panel_1.add(btn_2); //

		JPanel panel_left = new JPanel(new BorderLayout()); //
		panel_left.add(panel_1, BorderLayout.NORTH); //
		billListPanel_1 = new BillListPanel(new TMO_ComboBoxDict()); //
		billListPanel_1.addBillListSelectListener(this); //
		panel_left.add(billListPanel_1, BorderLayout.CENTER); //

		JPanel panel_2 = new WLTPanel(WLTPanel.HORIZONTAL_FROM_MIDDLE, new FlowLayout(FlowLayout.LEFT), LookAndFeel.defaultShadeColor1, false); //
		btn_new = new WLTButton("新增"); //
		btn_del = new WLTButton("删除"); //
		btn_save = new WLTButton("保存"); //
		btn_refresh = new WLTButton("刷新"); //
		btn_moveup = new WLTButton("上移");
		btn_movedown = new WLTButton("下移");

		btn_new.addActionListener(this); //
		btn_del.addActionListener(this); //
		btn_save.addActionListener(this); //
		btn_refresh.addActionListener(this); //
		btn_moveup.addActionListener(this); //
		btn_movedown.addActionListener(this); //

		panel_2.add(btn_new); //
		panel_2.add(btn_del); //
		panel_2.add(btn_save); //
		panel_2.add(btn_refresh); //
		panel_2.add(btn_moveup); //
		panel_2.add(btn_movedown); //

		JPanel panel_right = new JPanel(new BorderLayout()); //
		panel_right.add(panel_2, BorderLayout.NORTH); //

		billListPanel_2 = new BillListPanel("pub_comboboxdict_CODE1"); //
		panel_right.add(billListPanel_2, BorderLayout.CENTER); //

		JSplitPane splitPanel = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel_left, panel_right); //
		splitPanel.setDividerLocation(350); //

		this.setLayout(new BorderLayout()); //
		this.add(splitPanel); //
	}

	protected void onImportComboboxXML() {//导入XML格式下拉框

		final BillDialog dialog = new BillDialog(this, "导入XML", 1000, 700);
		dialog.setLayout(new BorderLayout());
		textArea.setBackground(Color.WHITE); //
		textArea.setForeground(Color.BLUE); //
		textArea.setFont(new Font("宋体", Font.PLAIN, 12));
		textArea.select(0, 0); //

		JPanel jp = new JPanel();
		jp.setLayout(new FlowLayout(FlowLayout.CENTER));
		WLTButton confirm = new WLTButton("确定");
		WLTButton cancel = new WLTButton("取消");
		confirm.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					if (textArea == null || textArea.getText().trim().equals("") || textArea.getText() == null) {
						MessageBox.show("请输入需要导入的下拉框!");
						return;
					}
					FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //
					service.importXMLCombobox(textArea.getText());

					MessageBox.show(billListPanel_1, "导入XML格式下拉框成功!");

				} catch (Exception ex) {
					ex.printStackTrace(); //
				}
				textArea.setText("");
				dialog.dispose();
			}
		});
		cancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				dialog.dispose();

			}
		});
		jp.add(confirm);
		jp.add(cancel);

		dialog.getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER); //
		dialog.getContentPane().add(jp, BorderLayout.SOUTH);
		dialog.setVisible(true); //

	}

	protected void onExportComboboxXML() {//导出XML格式下拉框
		try {

			int li_count = billListPanel_1.getTable().getSelectedRowCount();
			if (li_count <= 0) {
				MessageBox.showSelectOne(this);
				return;
			}
			FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //

			int[] selected_rows = billListPanel_1.getTable().getSelectedRows();
			String[] tempcode = new String[selected_rows.length];
			for (int i = 0; i < selected_rows.length; i++) {
				tempcode[i] = billListPanel_1.getValueAt(selected_rows[i], "TYPE").toString(); //
			}
			String sb_xml = service.exportXMLCombobox(tempcode, selected_rows);

			BillDialog dialog = new BillDialog(this, "导出XML", 1000, 700);
			JTextArea textArea = new JTextArea(sb_xml); //
			textArea.setBackground(new Color(240, 240, 240)); //
			textArea.setForeground(Color.BLACK); //
			textArea.setFont(new Font("宋体", Font.PLAIN, 12));
			textArea.select(0, 0); //
			dialog.getContentPane().add(new JScrollPane(textArea)); //
			dialog.setVisible(true); //

		} catch (Exception e) {
			e.printStackTrace(); //
		}

	}

	private void refreshType() {
		billListPanel_1.clearTable(); //
		billListPanel_2.clearTable(); //
		billListPanel_1.queryDataByCondition(null, "type"); //
	}

	/**
	 * 删除类型..
	 */
	private void onDelType() {
		try {
			BillVO billVO = billListPanel_1.getSelectedBillVO();
			if (billVO != null) {
				if (JOptionPane.showConfirmDialog(this, "您确定要删除该类型吗?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
					return;
				}

				String str_type = billVO.getStringValue("type"); //
				String str_sql = "delete from pub_comboboxdict where type='" + str_type + "'"; //
				UIUtil.executeUpdateByDS(null, str_sql); //
				billListPanel_2.clearTable(); //
				billListPanel_1.removeRow(); //
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	//改动按钮事件
	private void onNewType() {
		try {
			JPanel panel = new JPanel(); //
			panel.setLayout(null); //

			JLabel label = new JLabel("新增字典类型 ", JLabel.RIGHT);
			JTextField textField = new JTextField(); //
			label.setBounds(10, 10, 80, 20);
			textField.setBounds(90, 10, 150, 20);

			panel.add(label);
			panel.add(textField); //
			panel.setPreferredSize(new Dimension(240, 30)); //

			if (JOptionPane.showConfirmDialog(this, panel, "您确定要新增类型吗?", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return; //
			}

			if (textField.getText() == null || textField.getText().trim().equals("")) {
				MessageBox.show(this, "类型值不能为空!"); //
				return;
			}

			StringBuffer sb_sql = new StringBuffer();
			sb_sql.append("insert into pub_comboboxdict");
			sb_sql.append("(");
			sb_sql.append("pk_pub_comboboxdict,"); //pk_pub_comboboxdict  (null)
			sb_sql.append("type,"); //type  (null)
			sb_sql.append("id,"); //id  (null)
			sb_sql.append("code,"); //code  (null)
			sb_sql.append("name,"); //name  (null)
			sb_sql.append("seq"); //seq  (null)
			sb_sql.append(")");
			sb_sql.append(" values ");
			sb_sql.append("(");
			sb_sql.append("'" + UIUtil.getSequenceNextValByDS(null, "s_pub_comboboxdict") + "',"); //pk_pub_comboboxdict  (null)
			sb_sql.append("'" + textField.getText() + "',"); //type  (null)
			sb_sql.append("1,"); //id  (null)
			sb_sql.append("'001',"); //code  (null)
			sb_sql.append("'name1',"); //name  (null)
			sb_sql.append("1"); //seq  (null)
			sb_sql.append(")");

			UIUtil.executeUpdateByDS(null, sb_sql.toString());

			int li_row = billListPanel_1.newRow();
			billListPanel_1.setValueAt(new StringItemVO(textField.getText()), li_row, "type"); //
			billListPanel_1.setQuickQueryPanelItemRealValue("TYPE", textField.getText()); //
			billListPanel_2.queryDataByCondition("type='" + textField.getText() + "'", "seq asc"); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		String str_type = _event.getCurrSelectedVO().getStringValue("type");
		billListPanel_2.queryDataByCondition("type='" + str_type + "'", "seq asc"); //
	}

	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == btn_new) {
			onNew(); //
		} else if (evt.getSource() == btn_del) {
			onDelete(); //
		} else if (evt.getSource() == btn_save) {
			onSave(); //
		} else if (evt.getSource() == btn_refresh) {
			onRefresh(); //
		} else if (evt.getSource() == btn_moveup) {
			onMoveUp(); //
		} else if (evt.getSource() == btn_movedown) {
			onMoveDown(); //
		}
	}

	private void onNew() {
		try {
			BillVO billVO = billListPanel_1.getSelectedBillVO(); //
			if (billVO == null) {
				MessageBox.show(this, "请先选择一个类型!"); //
				return;
			}

			String str_type = billVO.getStringValue("type"); //
			int li_row = billListPanel_2.newRow();
			String str_id = UIUtil.getSequenceNextValByDS(null, "s_pub_comboboxdict"); //
			billListPanel_2.setValueAt(new StringItemVO(str_id), li_row, "pk_pub_comboboxdict"); //
			billListPanel_2.setValueAt(new StringItemVO(str_type), li_row, "type"); //
			billListPanel_2.setValueAt(new StringItemVO("" + (li_row + 1)), li_row, "seq"); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	private void onSave() {
		billListPanel_2.saveData(); //
		//billListPanel_2.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT); //
		MessageBox.show(this, "保存数据成功!"); //
	}

	private void onDelete() {
		billListPanel_2.removeRow(); //
	}

	private void onRefresh() {
		BillVO billVO = billListPanel_1.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, "请先选择一个类型!"); //
			return;
		}
		String str_type = billVO.getStringValue("type"); //
		billListPanel_2.queryDataByCondition("type='" + str_type + "'", "seq asc"); //
	}

	/**
	 * 
	 */
	private void onMoveUp() {
		if (billListPanel_2.moveUpRow()) {
			resetShowOrder();
		}
	}

	private void resetShowOrder() {
		int li_rowcount = billListPanel_2.getRowCount();
		for (int i = 0; i < li_rowcount; i++) {
			if (billListPanel_2.getValueAt(i, "SEQ") == null || Integer.parseInt("" + billListPanel_2.getValueAt(i, "SEQ")) != (i + 1)) {
				billListPanel_2.setValueAt("" + (i + 1), i, "SEQ"); //
			}
		}
	}

	private void onMoveDown() {
		if (billListPanel_2.moveDownRow()) {
			resetShowOrder(); //	
		}
	}

	class TMO_ComboBoxDict extends AbstractTMO {
		private static final long serialVersionUID = 8057184541083294474L;

		public HashVO getPub_templet_1Data() {
			HashVO vo = new HashVO(); //
			vo.setAttributeValue("templetcode", "pub_comboboxdict"); //模版编码,请勿随便修改
			vo.setAttributeValue("templetname", "字典类型"); //模板名称
			vo.setAttributeValue("templetname_e", "ComBoboxdict"); //模板名称
			vo.setAttributeValue("tablename", "v_pub_comboboxdict_1"); //查询数据的表(视图)名
			vo.setAttributeValue("pkname", "ID"); //主键名
			vo.setAttributeValue("pksequencename", null); //序列名
			vo.setAttributeValue("savedtablename", null); //保存数据的表名
			vo.setAttributeValue("CardWidth", "577"); //卡片宽度
			vo.setAttributeValue("Isshowlistpagebar", "N"); //列表是否显示分页栏
			vo.setAttributeValue("Isshowlistopebar", "N"); //列表是否显示操作按钮栏
			vo.setAttributeValue("ISSHOWLISTQUICKQUERY", "Y"); //列表是否显示快速查询框
			vo.setAttributeValue("listcustpanel", null); //列表自定义面板
			vo.setAttributeValue("cardcustpanel", null); //卡片自定义面板
			return vo;
		}

		public HashVO[] getPub_templet_1_itemData() {
			Vector vector = new Vector();
			HashVO itemVO = null;

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "TYPE"); //唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "类型"); //显示名称
			itemVO.setAttributeValue("itemname_e", "type"); //显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); //控件类型
			itemVO.setAttributeValue("comboxdesc", null); //下拉框定义
			itemVO.setAttributeValue("refdesc", null); //参照定义
			itemVO.setAttributeValue("issave", "N"); //是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "1"); //1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); //是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); //加载公式
			itemVO.setAttributeValue("editformula", null); //编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); //默认值公式

			itemVO.setAttributeValue("listwidth", "200"); //列表是宽度
			itemVO.setAttributeValue("cardwidth", "50,120"); //卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); //列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "N"); //卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("isQuickQueryShowable", "Y"); //
			itemVO.setAttributeValue("isQuickQueryEditable", "Y"); //
			itemVO.setAttributeValue("querywidth", "60,100"); //
			itemVO.setAttributeValue("iswrap", "N");
			vector.add(itemVO);

			return (HashVO[]) vector.toArray(new HashVO[0]);
		}
	}
}
