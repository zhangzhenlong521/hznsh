package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.RowNumberItemVO;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 * 孙富君搞的导入子表,点击导入按钮时弹出的窗口!!
 * @author xch
 *
 */
public class RefDialog_ChildTableImport extends AbstractRefDialog {

	private static final long serialVersionUID = 8753649417760275054L;
	private BillListPanel billListPanel_1 = null;
	private BillListPanel billListPanel_2 = null;
	private String templetCode = null;
	private String condition = null;
	private int closeType = -1;
	private String returnRealValue = null;
	private String returnNameValue = null;
	private JButton bnt_1, bnt_2, bnt_3, bnt_4 = null;
	private String primarykey = null; //
	private String primaryname = null; //
	private String intercept = null;//导入子表的拦截器。
	private RefItemVO currRefItemVO = null; //

	public RefDialog_ChildTableImport(Container _parent, String _title, RefItemVO value, BillPanel panel, CommUCDefineVO _dfvo) {
		super(_parent, _title, value, panel);
		this.templetCode = _dfvo.getConfValue("模板编码");
		this.primarykey = _dfvo.getConfValue("主键字段名");
		this.primaryname = _dfvo.getConfValue("主键字段显示名");
		this.intercept = _dfvo.getConfValue("拦截器");
		this.condition = getCondition(value);
		initialize();
	}

	private String getCondition(RefItemVO value) {
		String condition = "";
		if (value != null && value.getId() != null && value.getId().length() > 0) {
			condition = primarykey + " in (" + TBUtil.getTBUtil().getInCondition(value.getId()) + ")"; //原来的如果ID不是纯数字就报错了 by hm 2016-03-02
		}
		return condition;
	}

	public void initialize() {
		this.getContentPane().setLayout(new BorderLayout()); //
		this.getContentPane().add(getCenterPanel(), BorderLayout.CENTER); //
		this.getContentPane().add(getSourthPanel(), BorderLayout.SOUTH); //
	}

	private JPanel getCenterPanel() {
		bnt_1 = new WLTButton(" >> ");//修改按钮名称，增加按钮长度，以前的长度太短了不好看【李春娟/2013-05-08】
		bnt_2 = new WLTButton("  > ");
		bnt_3 = new WLTButton("  < ");
		bnt_4 = new WLTButton(" << ");

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
		bnt_4.setPreferredSize(new Dimension(100, 20));

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
		BillVO[] billvos = billListPanel_2.getAllBillVOs();
		StringBuffer sb = new StringBuffer();
		StringBuffer sbname = new StringBuffer();
		if (billvos != null && billvos.length > 0) {
			for (int i = 0; i < billvos.length; i++) {
				sb.append(billvos[i].getRealValue(primarykey) + ";");
				sbname.append(billvos[i].getRealValue(primaryname) + ";");
			}
			returnRealValue = sb.toString();
			returnNameValue = sbname.toString();
			currRefItemVO = new RefItemVO(returnRealValue, null, returnNameValue);
		}
		this.closeType = 1;
		this.dispose();
	}

	private void onCancel() {
		this.closeType = 2;
		this.dispose();
	}

	public BillListPanel getBillListPanel_1() {
		if (billListPanel_1 == null) {
			billListPanel_1 = new BillListPanel(this.templetCode); //
			billListPanel_1.getBillListBtnPanel().setVisible(false);
			billListPanel_1.setItemEditable(false);
			afterInitIntercept(billListPanel_1);
		}
		return billListPanel_1;
	}

	private BillListPanel getBillListPanel_2() {

		if (billListPanel_2 == null) {
			try {
				Pub_Templet_1VO templetVO = UIUtil.getPub_Templet_1VO(this.templetCode);
				templetVO.setAutoLoads(0); // 阻止自动加载
				billListPanel_2 = new BillListPanel(templetVO);
				billListPanel_2.getBillListBtnPanel().setVisible(false);
				billListPanel_2.getTitlePanel().setVisible(false);
				billListPanel_2.getQuickQueryPanel().setVisible(false);
				billListPanel_2.getPagePanel().setVisible(false);
				billListPanel_2.getPageDescLabel().setVisible(false);
				if (condition != null && !"".equals(condition)) {
					billListPanel_2.QueryDataByCondition(condition);
				}
				afterInitIntercept(billListPanel_2);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return billListPanel_2;
	}

	private void afterInitIntercept(BillListPanel _listPanel) {
		if (intercept != null && !intercept.equals("")) {
			try {
				ChildTableCommUIIntercept listener = (ChildTableCommUIIntercept) Class.forName(intercept).newInstance(); //				
				listener.afterInitialize(_listPanel);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void onClicked_1() {
		BillVO[] vos = billListPanel_1.getAllBillVOs(); //
		BillVO[] vos_right = billListPanel_2.getAllBillVOs();

		for (int i = 0; i < vos.length; i++) {
			boolean iffind = false;
			String str_id = vos[i].getPkValue(); //
			for (int j = 0; j < vos_right.length; j++) {
				if (vos_right[j].getPkValue().equals(str_id)) {
					iffind = true;
					break;
				}
			}
			if (!iffind) {
				BillVO b = vos[i].deepClone();
				String str_keys[] = b.getKeys();
				int li_newRow = billListPanel_2.addEmptyRow();
				for (int i1 = 0; i1 < str_keys.length; i1++) {
					billListPanel_2.setValueAt(b.getObject(str_keys[i1]), li_newRow, str_keys[i1]);
				}
				billListPanel_2.setValueAt(new RowNumberItemVO("INSERT", li_newRow), li_newRow, "_RECORD_ROW_NUMBER");
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
			String str_id = vos[i].getPkValue(); //
			for (int j = 0; j < vos_right.length; j++) {
				if (vos_right[j].getPkValue().equals(str_id)) {
					iffind = true;
					break;
				}
			}

			if (!iffind) {
				BillVO b = vos[i].deepClone();
				String str_keys[] = b.getKeys();
				int li_newRow = billListPanel_2.addEmptyRow();
				for (int i1 = 0; i1 < str_keys.length; i1++) {
					billListPanel_2.setValueAt(b.getObject(str_keys[i1]), li_newRow, str_keys[i1]);
				}
				billListPanel_2.setValueAt(new RowNumberItemVO("INSERT", li_newRow), li_newRow, "_RECORD_ROW_NUMBER");
			}
		}

	}

	private void onClicked_3() {
		BillVO[] billvos = billListPanel_2.getSelectedBillVOs();
		int[] rows = billListPanel_2.getSelectedRows();
		if (billvos != null && billvos.length > 0) {
			for (int i = billvos.length - 1; i >= 0; i--) {
				if ("INSERT".equals(billvos[i].getRowNumberItemVO().getState())) {
					billListPanel_2.removeRow(rows[i]);
				}
			}
		}
	}

	private void onClicked_4() {
		BillVO[] billvos = billListPanel_2.getAllBillVOs();
		if (billvos != null && billvos.length > 0) {
			for (int i = billvos.length - 1; i >= 0; i--) {
				if ("INSERT".equals(billvos[i].getRowNumberItemVO().getState())) {
					billListPanel_2.removeRow(i);
				}
			}
		}
	}

	public int getCloseType() {
		return closeType;
	}

	public void setCloseType(int closeType) {
		this.closeType = closeType;
	}

	public String getReturnRealValue() {
		return returnRealValue;
	}

	public void setReturnRealValue(String returnRealValue) {
		this.returnRealValue = returnRealValue;
	}

	public String getReturnNameValue() {
		return returnNameValue;
	}

	public void setReturnNameValue(String returnNameValue) {
		this.returnNameValue = returnNameValue;
	}

	@Override
	public RefItemVO getReturnRefItemVO() {
		return currRefItemVO;
	}

	/**
	 * 初始宽度
	 * @return
	 */
	public int getInitWidth() {
		return 950;//导入子表页面要显示两个列表，宽度应该设宽些，否则只能显示一列，并且查询框也很憋屈【李春娟/2012-03-27】
	}

	public int getInitHeight() {
		return 700;
	}

}
