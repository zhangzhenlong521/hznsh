/**************************************************************************
 * $RCSfile: RefDialog_Date.java,v $  $Revision: 1.10 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/
package cn.com.infostrategy.ui.sysapp.other;

import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.GregorianCalendar;

public final class RefDialog_Month extends AbstractRefDialog {
	private static final long serialVersionUID = 1L;
	private int baseYear = 2013;
	private int years = 50;
	private final JComboBox jboxYear = new JComboBox();
	private final JComboBox jboxMonth = new JComboBox();
	private JButton assureButton = null;
	private JButton cancelButton = null;
	private String curryear = null;// 固定年份，有的选择需要限定选择某一年份，故增加该属性【李春娟/2014-01-08】

	public RefDialog_Month(Container _parent, String _title, RefItemVO value, BillPanel panel) throws Exception {
		super(_parent, _title, value, panel);
	}

	public RefDialog_Month(Container _parent, String _title, RefItemVO value, BillPanel panel, String _curryear) throws Exception {
		super(_parent, _title, value, panel);
		if (_curryear != null && !_curryear.equals("-99999") && _curryear.length() >= 4) {
			curryear = _curryear.substring(0, 4);
			jboxYear.setEnabled(false);
		}
	}

	public RefDialog_Month(Container _parent, String _title, RefItemVO value, BillPanel panel, CommUCDefineVO _dfvo) throws Exception {
		super(_parent, _title, value, panel);
	}

	public void initialize() {
		this.setResizable(false);
		RefItemVO initVO = getInitRefItemVO();
		if (initVO == null || initVO.getId() == null || initVO.getId().equals("")) {
			getToday();
		} else {
			jboxYear.setSelectedItem(initVO.getId().split("-")[0]);
			jboxMonth.setSelectedItem(initVO.getId().split("-")[1]);
		}
		JPanel con = WLTPanel.createDefaultPanel(null, WLTPanel.HORIZONTAL_FROM_MIDDLE);
		jboxYear.setPreferredSize(new Dimension(40, 18));
		jboxYear.setFont(new Font("宋体", Font.PLAIN, 12)); //
		for (int i = 0; i < years; i++) {
			jboxYear.addItem("" + (baseYear + i));
		}
		for (int j = 1; j < 13; j++) {
			if (j < 10) {
				jboxMonth.addItem("0" + j);
			} else {
				jboxMonth.addItem("" + j);
			}
		}
		jboxYear.setBounds(30, 10, 80, 18);
		jboxMonth.setBounds(130, 10, 70, 18);
		con.add(jboxMonth);
		con.add(jboxYear);
		assureButton = new WLTButton(UIUtil.getLanguage("确定"));
		assureButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent evt) {
				onConfirm(evt);
			}
		});
		cancelButton = new WLTButton(UIUtil.getLanguage("取消"));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent evt) {
				onCancel();
			}
		});
		assureButton.setBounds(45, 60, 60, 20);
		cancelButton.setBounds(130, 60, 60, 20);
		con.add(assureButton);
		con.add(cancelButton);
		this.setFocusable(true);
		this.setLayout(new BorderLayout());
		this.add(con, BorderLayout.CENTER);
	}

	public int getInitWidth() {
		return 238;
	}

	public int getInitHeight() {
		return 120;
	}

	private void onConfirm(final ActionEvent evt) {
		setCloseType(1);
		this.dispose();
	}

	private void onCancel() {
		setCloseType(2);
		this.dispose();
	}

	private void getToday() {
		String checkDate = new SalaryUIUtil().getCheckDate();
		if (checkDate == null || checkDate.equals("") || curryear != null) {
			GregorianCalendar gc = new GregorianCalendar();
			if (curryear != null) {
				jboxYear.setSelectedItem(curryear + "");
			} else {
				jboxYear.setSelectedItem(gc.get(Calendar.YEAR) + "");
			}
			int month = gc.get(Calendar.MONTH) + 1;
			String month_str = month < 10 ? ("0" + month) : ("" + month);
			jboxMonth.setSelectedItem(month_str);
		} else {
			String checkyear = checkDate.substring(0, 4);
			jboxYear.setSelectedItem(checkyear + "");
			String checkmonth = checkDate.substring(5, 7);
			jboxMonth.setSelectedItem(checkmonth);

		}

	}

	public RefItemVO getReturnRefItemVO() {
		String str_date = jboxYear.getSelectedItem() + "-" + jboxMonth.getSelectedItem();
		return new RefItemVO(str_date, null, str_date);
	}
}
