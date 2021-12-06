package com.pushworld.ipushgrc.ui.tools.impexcel;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

/**
 * 卡片中多选参照窗口
 * 
 * 
 * 
 */
public class CheckFixedInfoRef extends AbstractRefDialog {


	private static final long serialVersionUID = 2210719677766465354L;
	private Pub_Templet_1VO tmpletVO = null;
	private Pub_Templet_1VO tmpletVO1 = null;

	private BillListPanel billListPanel_1 = null;
	private BillListPanel billListPanel_2 = null; // 赋予的权限
	private BillCardPanel billCardPanel = null;
	private String idFildName = null; // 唯一标识的列名,约定好是第一列!!
	private String nameFildName = null; // 唯一标识的列名,约定好是第一列!!

	private JButton bnt_1, bnt_2, bnt_3, bnt_4 = null;

	private String refId = null;
	private String refName = null;

	private String[] allcolumns = null;
	private HashVO[] hvs = null;
	private String realvalue = null;
	private RefItemVO refinitvalue;
	private int openType = 0; // 找开方式
	private String dsname;
	private String sql;
	private BillQueryPanel billquery;
	private RefItemVO returnRefItemVO = null; //
	private HashMap hashmap = null;
	private String pk_pub_templet_1="";
	
	public CheckFixedInfoRef(Container _parent, String _title, RefItemVO refItemVO,
			BillPanel panel) {
		super(_parent, "key值", refItemVO, panel);
		billCardPanel = (BillCardPanel)panel;
	}


	public void initialize() {
		this.setLayout(new BorderLayout());
		this.setSize(1000, 600);
		billListPanel_1 = new BillListPanel("INPUT_INFO_CODE2");//hn_homologous_CODE1
		billquery = billListPanel_1.getQuickQueryPanel();
		pk_pub_templet_1 = billCardPanel.getCompentRealValue("template_id");
		billListPanel_1.QueryDataByCondition("pk_pub_templet_1='"+pk_pub_templet_1+"'");
		billquery.addBillQuickActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent actionevent) {
				hashmap = billquery.getQuickQueryConditionAsMap();
				String cellkey = (String) hashmap.get("cellkey");
				StringBuffer sb = new StringBuffer();
				sb.append("select * from pub_templet_1_item_CODE1 where 1=1  ");
				sb.append("and pk_pub_templet_1='"+pk_pub_templet_1+"'");
				if(!cellkey.equals("")){
					sb.append(" and cellkey like '%"+cellkey+"%'");
//					sb.append("and cellkey = '"+cellkey+"'");
				}
//					new UIUtil().getStringValueByDS(null, sb.toString());
					billListPanel_1.queryDataByDS(null, sb.toString());
			}
			
		});
		billListPanel_2 = new BillListPanel("INPUT_INFO_CODE3");
		tmpletVO = billListPanel_1.getTempletVO();
		tmpletVO1 = billListPanel_2.getTempletVO();
		idFildName = "itemkey"; // 主键字段名
		nameFildName = "itemname"; // 名称字段名
		this.getContentPane().setLayout(new BorderLayout()); //
		this.getContentPane().add(getCenterPanel(), BorderLayout.CENTER); //
		this.getContentPane().add(getSourthPanel(), BorderLayout.SOUTH); //
		if (refinitvalue != null) {
			String hvs_name = refinitvalue.getName();
			String[] str_keys = hvs_name.split(";"); //
			Vector v_data = new Vector();
			String str_compareFieldName = null;
			if (openType == 1) {
				str_compareFieldName = this.nameFildName;
			} else if (openType == 2) {
				str_compareFieldName = this.idFildName;
			}
			for (int i = 0; i < str_keys.length; i++) {
				for (int j = 0; j < hvs.length; j++) {
					if (hvs[j].getStringValue(idFildName).equals(str_keys[i]) || str_keys[i].equals(hvs[j].getStringValue(str_compareFieldName))) {
						v_data.add(hvs[j].deepClone());
					}
				}
			}
			HashVO[] realVOs = (HashVO[]) v_data.toArray(new HashVO[0]);
			getBillListPanel_2().putValue(realVOs);//

		}

		getBillListPanel_1().putValue(hvs); //
		if (realvalue != null) {
			String[] str_keys = realvalue.split(";"); //
			Vector v_data = new Vector();
			BillVO[] billVOs = getBillListPanel_1().getAllBillVOs();

			String str_compareFieldName = null;
			if (openType == 1) {
				str_compareFieldName = this.nameFildName;
			} else if (openType == 2) {
				str_compareFieldName = this.idFildName;
			}
			for (int i = 0; i < str_keys.length; i++) {
				for (int j = 0; j < billVOs.length; j++) {
					if (billVOs[j].getStringValue(str_compareFieldName).equals(str_keys[i])) {
						v_data.add(hvs[j].deepClone());
						billVOs[j].getRowNumberItemVO().setState(WLTConstants.BILLDATAEDITSTATE_UPDATE); //
						break;
					}
				}
			}
			HashVO[] realVOs = (HashVO[]) v_data.toArray(new HashVO[0]);
			getBillListPanel_2().putValue(realVOs);//
		}

	}

	private JPanel getCenterPanel() {
		bnt_1 = new JButton(" >>");
		bnt_2 = new JButton(" > ");
		bnt_3 = new JButton(" < ");
		bnt_4 = new JButton(" <<");

		bnt_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClicked_1(); //
			}
		});//

		bnt_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClicked_2(); //
			}
		});//

		bnt_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClicked_3(); //
			}
		});//

		bnt_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClicked_4(); //
			}
		});//

		bnt_1.setPreferredSize(new Dimension(100, 20));
		bnt_2.setPreferredSize(new Dimension(100, 20));
		bnt_3.setPreferredSize(new Dimension(100, 20));
		bnt_4.setPreferredSize(new Dimension(85, 20));

		JPanel panel_temp = new JPanel(); //
		panel_temp.setLayout(new BorderLayout());
		Box rightAndLeftBox = Box.createVerticalBox();
		rightAndLeftBox.add(Box.createGlue());
		rightAndLeftBox.add(bnt_1); //
		rightAndLeftBox.add(Box.createVerticalStrut(10));
		rightAndLeftBox.add(bnt_2); //
		rightAndLeftBox.add(Box.createVerticalStrut(10));
		rightAndLeftBox.add(bnt_3); //
		rightAndLeftBox.add(Box.createVerticalStrut(10));
		rightAndLeftBox.add(bnt_4); //
		rightAndLeftBox.add(Box.createGlue());

		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalStrut(20));

		JLabel label_left = new JLabel("所有数据:", SwingConstants.LEFT);
		label_left.setForeground(java.awt.Color.GRAY);
		JPanel left_panel = new JPanel();
		left_panel.setLayout(new BorderLayout()); //
		left_panel.add(label_left, BorderLayout.NORTH); //
		left_panel.add(getBillListPanel_1(), BorderLayout.CENTER); //

		box.add(left_panel);

		box.add(Box.createHorizontalStrut(20)); //
		box.add(rightAndLeftBox); //

		JLabel label_right = new JLabel("选择的数据:", SwingConstants.LEFT);
		label_right.setForeground(java.awt.Color.GRAY);
		JPanel right_panel = new JPanel();
		right_panel.setLayout(new BorderLayout()); //
		right_panel.add(label_right, BorderLayout.NORTH); //
		right_panel.add(getBillListPanel_2(), BorderLayout.CENTER); //

		box.add(right_panel);
		box.add(Box.createHorizontalStrut(20)); //

		panel_temp.add(box, BorderLayout.CENTER);
		panel_temp.updateUI(); //
		return panel_temp;
	}

	private JPanel getSourthPanel() {
		JPanel panel = new JPanel(); //
		panel.setLayout(new FlowLayout()); //
		JButton btn_confirm = new JButton("确定");
		JButton btn_cancel = new JButton("取消");
		btn_confirm.setPreferredSize(new Dimension(70, 20)); //
		btn_cancel.setPreferredSize(new Dimension(70, 20)); //
		btn_confirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onConfirm();
			}
		});
		btn_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		panel.add(btn_confirm);
		panel.add(btn_cancel);
		return panel;
	}

	private void onConfirm() {
		BillVO[] vos = getBillListPanel_2().getAllBillVOs(); //
		String str_allkeys = "";
		String str_allnames = "";
		String value = "";
		for (int i = 0; i < vos.length; i++) {
			value = vos[i].getStringValue("value");
			if(value==null||"".equals(value)){
				MessageBox.show("请将信息填写完整");
				return;
			}
			str_allkeys = str_allkeys + vos[i].getStringValue("itemkey") +"="+value+";;;";
		}
//		for (int i = 0; i < vos.length; i++) {
//			str_allkeys = str_allkeys + vos[i].getStringValue("itemkey") + ";";
//		}
//		for (int i = 0; i < vos.length; i++) {
//			str_allnames = str_allnames + vos[i].getStringValue("itemkey") + ";";
//		}
//		if (!str_allkeys.equals("")) { // 如果不为空,则前面再加分号!!!
//			str_allkeys = ";" + str_allkeys;
//		}
		refId = str_allkeys;
	    refName = str_allkeys;
		this.setCloseType(1);
		this.dispose(); //
	}

	private void onCancel() {
		this.setCloseType(2);
		this.dispose(); //
	}

	private BillListPanel getBillListPanel_1() {
		if (billListPanel_1 == null) {
			billListPanel_1 = new BillListPanel(this.tmpletVO); //
			billListPanel_1.setItemEditable(false);
			billListPanel_1.getQuickQueryPanel().setVisible(false);
			billListPanel_1.setToolbarVisiable(true);
			// billListPanel_1.getTable().setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			billListPanel_1.getTable().addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						onDoubleClicked();
					}
				}
			}); //
		}
		return billListPanel_1;
	}

	private BillListPanel getBillListPanel_2() {
		if (billListPanel_2 == null) {
			billListPanel_2 = new BillListPanel(this.tmpletVO1); //
			billListPanel_2.setItemEditable(false);
			billListPanel_2.getQuickQueryPanel().setVisible(true);
			billListPanel_2.setToolbarVisiable(false);
			// billListPanel_2.getTable().setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		}
		return billListPanel_2;
	}

	/**
	 * 分配所有的角色
	 * 
	 */
	private void onClicked_1() {
		BillVO[] vos = billListPanel_1.getAllBillVOs(); //
		BillVO[] vos_right = billListPanel_2.getAllBillVOs();

		for (int i = 0; i < vos.length; i++) {
			boolean iffind = false;
			String str_id = vos[i].getStringValue(idFildName); //
			for (int j = 0; j < vos_right.length; j++) {
				if (vos_right[j].getStringValue(idFildName).equals(str_id)) {
					iffind = true;
					break;
				}
			}
			if (!iffind) {
				billListPanel_2.addRow(vos[i].deepClone()); //
			}
		}
	}

	/**
	 * 分配选中的角色
	 * 
	 */
	private void onClicked_2() {
		BillVO[] vos = billListPanel_1.getSelectedBillVOs(); //
		BillVO[] vos_right = billListPanel_2.getAllBillVOs();

		for (int i = 0; i < vos.length; i++) {
			boolean iffind = false;
			String str_id = vos[i].getStringValue(idFildName); //
			for (int j = 0; j < vos_right.length; j++) {
				if (vos_right[j].getStringValue(idFildName).equals(str_id)) {
					iffind = true;
					break;
				}
			}

			if (!iffind) {
				billListPanel_2.addRow(vos[i].deepClone()); //
			}
		}

	}

	private void onDoubleClicked() {
		int li_row = getBillListPanel_1().getSelectedRow();
		if (li_row >= 0) {
			BillVO vo = getBillListPanel_1().getBillVO(li_row);
			BillVO[] vos_right = billListPanel_2.getAllBillVOs();
			boolean iffind = false;
			for (int j = 0; j < vos_right.length; j++) {
				if (vos_right[j].getStringValue(idFildName).equals(vo.getStringValue(idFildName))) {
					iffind = true;
					break;
				}
			}

			if (!iffind) {
				getBillListPanel_2().addRow(vo.deepClone()); //
			}
		}
	}

	private void onClicked_3() {
		billListPanel_2.removeSelectedRows(); //
	}

	private void onClicked_4() {
		billListPanel_2.clearTable(); //
	}

	public RefItemVO getReturnRefItemVO() {
		return new RefItemVO(refId, refId, refName); //	
	}

	public int getInitWidth() {
		return 800;
	}

	public int getInitHeight() {
		return 500;
	}

}

