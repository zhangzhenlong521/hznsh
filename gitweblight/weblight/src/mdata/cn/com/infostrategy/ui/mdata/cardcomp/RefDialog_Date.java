/**************************************************************************
 * $RCSfile: RefDialog_Date.java,v $  $Revision: 1.10 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 * 日期控件2012-04-18更新，增加日期对比功能。用到<= >= < > = 输入方式如下：
 *  1、一个条件： {}<=2012-04-18表示选择的日期需小于2012-04-18 简写为 <2012-04-18 
 *  2、多个条件   {}<=2012-04-18 and {}>=2012-01-01 表示选择的日期徐在2012-01-01到2012-04-18 之间.  {}可有可无.
 *  3、和一个日期还有天数比较 。  {}+30>=2012-04-18 表示在2012-04-18的30天后。顺序必须这么写。
 *  
 *  
 *  控件配置中可以调用平台公式例如 getItemValue("otherdate")。但是如果这个日期没有填写，提示信息中不能显示出必填的字段信息。最好用{otherdate}来表示。例子如下:
 *  {}>getCurrDate() and {}<getItemValue("date1") and <{data2}  
 */
public final class RefDialog_Date extends AbstractRefDialog implements ItemListener {

	private static final long serialVersionUID = 1L;

	private static final int startX = 10;

	private static final int startY = 90;

	private static final int baseYear = new TBUtil().getSysOptionIntegerValue("日历选择的开始年份", 1950);

	private static final int years = new TBUtil().getSysOptionIntegerValue("日历选择的年份范围", 100);

	private static final Font smallFont = new Font("宋体", Font.PLAIN, 12);

	private static final Font largeFont = new Font("宋体", Font.PLAIN, 14);

	private static final Insets insets = new Insets(2, 2, 2, 2);

	private static final Color white = new Color(255, 255, 255);

	private Component selectedDay = null;

	private static GregorianCalendar selectedDate = null;

	private boolean hideOnSelect = true;

	private final JButton backButton = new JButton();

	private final JLabel monthAndYear = new JLabel();

	private final JButton forwardButton = new JButton();

	private final JComboBox jboxYear = new JComboBox();

	private final JComboBox jboxMonth = new JComboBox();

	private final JLabel[] dayHeadings = new JLabel[] { new JLabel("日"), new JLabel("一"), new JLabel("二"), new JLabel("三"), new JLabel("四"), new JLabel("五"), new JLabel("六") };

	private final JLabel[][] daysInMonth = new JLabel[][] { { new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel() }, { new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel() }, { new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel() },
			{ new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel() }, { new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel() }, { new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel() } };

	private JButton todayButton = null;

	private JButton assureButton = null;

	private JButton cancelButton = null;

	private final JLabel yearLabel = new JLabel(" "); //

	private final JLabel monthLabel = new JLabel(" "); //

	private String condition = null;//

	private CommUCDefineVO commUCDefineVO = null; //参照定义 时间只选未来或过去用 【杨科/2012-08-30】

	private String em[] = new String[] { "<=", ">=", "<", ">", "=" };// 定义符号
	private TBUtil tbutil = new TBUtil();
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public RefDialog_Date(Container _parent, String _title, RefItemVO value, BillPanel panel) throws Exception {
		this(_parent, _title, value, panel, null);
	}

	public RefDialog_Date(Container _parent, String _title, RefItemVO value, BillPanel panel, CommUCDefineVO _dfvo) throws Exception {
		super(_parent, _title, value, panel);
		if (_dfvo != null) {
			condition = _dfvo.getConfValue("条件");
		}
		if (condition != null && condition.length() > 0) {
			initCondition();
		}
		commUCDefineVO = _dfvo;
	}

	public void initialize() {
		this.setResizable(false); //
		if (getInitRefItemVO() == null) {
			selectedDate = getToday();
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			selectedDate = new GregorianCalendar();
			try {
				String str_tmp_initvalue = ((RefItemVO) getInitRefItemVO()).getId(); //
				if (str_tmp_initvalue != null && !str_tmp_initvalue.equals("")) {
					selectedDate.setTime(sdf.parse(str_tmp_initvalue));
				}
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}
		}

		JPanel con = WLTPanel.createDefaultPanel(null, WLTPanel.HORIZONTAL_FROM_MIDDLE); // 

		jboxYear.setPreferredSize(new Dimension(40, 18));
		jboxYear.setFont(new Font("宋体", Font.PLAIN, 12)); //
		for (int i = 0; i < years; i++) {
			jboxYear.addItem("" + (baseYear + i));
		}
		for (int j = 1; j < 13; j++) {
			jboxMonth.addItem("" + j);
		}

		jboxYear.setBounds(10, 10, 80, 18);
		// jboxYear.setFont(largeFont);
		jboxYear.addItemListener(this);

		yearLabel.setBounds(90, 10, 30, 18);
		yearLabel.setFont(largeFont);
		yearLabel.setHorizontalAlignment(JLabel.CENTER);

		jboxMonth.setBounds(120, 10, 70, 18);
		jboxMonth.setFont(smallFont);
		jboxMonth.addItemListener(this);

		monthLabel.setBounds(190, 10, 30, 18);
		monthLabel.setFont(largeFont);
		monthLabel.setHorizontalAlignment(JLabel.CENTER);

		Date dt = selectedDate.getTime();
		int temp = getTodayYear(dt);
		if (temp > baseYear + years) {
			jboxYear.setSelectedItem("" + (baseYear + years));
			jboxMonth.setSelectedItem("" + 12);
		} else if (temp < baseYear) {
			jboxYear.setSelectedItem("" + baseYear);
			jboxMonth.setSelectedItem("" + 1);
		} else {
			jboxYear.setSelectedItem("" + getTodayYear(dt));
			jboxMonth.setSelectedItem("" + getTodayMonth(dt));
		}

		con.add(jboxMonth);
		con.add(monthLabel);
		con.add(jboxYear);
		con.add(yearLabel);

		backButton.setFont(smallFont);
		backButton.setText("<"); //
		backButton.setMargin(insets);
		backButton.setDefaultCapable(false);
		backButton.setBounds(10, 40, 30, 20);
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent evt) {
				onBackClicked(evt);
			}
		});
		con.add(backButton);

		monthAndYear.setFont(largeFont);
		monthAndYear.setHorizontalAlignment(JTextField.CENTER);
		monthAndYear.setText(formatDateText(dt));
		monthAndYear.setBounds(40, 40, 150, 20);
		con.add(monthAndYear);

		forwardButton.setFont(smallFont);
		forwardButton.setText(">");
		forwardButton.setMargin(insets);
		forwardButton.setDefaultCapable(false);
		forwardButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent evt) {
				onForwardClicked(evt);
			}
		});
		forwardButton.setBounds(190, 40, 30, 20);
		con.add(forwardButton);

		int x = startX;
		for (int ii = 0; ii < dayHeadings.length; ii++) {
			dayHeadings[ii].setOpaque(true);
			dayHeadings[ii].setBackground(Color.LIGHT_GRAY);
			dayHeadings[ii].setForeground(Color.WHITE);
			dayHeadings[ii].setHorizontalAlignment(JLabel.CENTER);
			dayHeadings[ii].setBounds(x, 70, 31, 21);
			con.add(dayHeadings[ii]);
			x += 30;
		}

		x = startX;
		int y = startY;
		for (int ii = 0; ii < daysInMonth.length; ii++) {
			for (int jj = 0; jj < daysInMonth[ii].length; jj++) {
				daysInMonth[ii][jj].setOpaque(true);
				daysInMonth[ii][jj].setBackground(white);
				daysInMonth[ii][jj].setFont(smallFont);
				daysInMonth[ii][jj].setHorizontalAlignment(JLabel.CENTER);
				daysInMonth[ii][jj].setText("");
				daysInMonth[ii][jj].addMouseListener(new MouseAdapter() {
					public void mouseClicked(final MouseEvent evt) {
						onDayClicked(evt);
						if (evt.getClickCount() == 2) {
							doubleClick();
						}
					}
				});
				daysInMonth[ii][jj].setBounds(x, y, 31, 21);
				con.add(daysInMonth[ii][jj]);
				x += 30;
			}
			x = startX;
			y += 20;
		}

		final Dimension buttonSize = new Dimension(68, 24);
		todayButton = new WLTButton(UIUtil.getLanguage("今天"));
		todayButton.setDefaultCapable(true);
		todayButton.setSelected(true);
		todayButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent evt) {
				onToday(evt);
			}
		});

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

		todayButton.setBounds(15, 220, 60, 20);
		assureButton.setBounds(80, 220, 60, 20);
		cancelButton.setBounds(145, 220, 60, 20);

		con.add(todayButton);
		con.add(assureButton);
		con.add(cancelButton);

		calculateCalendar();
		this.setFocusable(true);
		this.setLayout(new BorderLayout()); //
		this.add(con, BorderLayout.CENTER); //
	}

	public boolean isHideOnSelect() {
		return hideOnSelect;
	}

	public void setHideOnSelect(final boolean hideOnSelect) {
		if (this.hideOnSelect != hideOnSelect) {
			this.hideOnSelect = hideOnSelect;
		}
	}

	public int getInitWidth() {
		return 238;
	}

	public int getInitHeight() {
		return 280;
	}

	private void onToday(final java.awt.event.ActionEvent evt) {
		selectedDate = getToday();
		if (isVisible()) {
			monthAndYear.setText(formatDateText(selectedDate.getTime()));
			final Date dt = selectedDate.getTime();
			this.jboxYear.setSelectedItem("" + getTodayYear(dt));
			this.jboxMonth.setSelectedItem("" + getTodayMonth(dt));
			calculateCalendar();
		}
	}

	private void doubleClick() {
		if (!check(getDataStringValue())) {
			MessageBox.show(this, "[" + this.getDataStringValue() + "]不在正确的时间范围内!");
			return;
		}
		setVisible(!hideOnSelect);
		if (isVisible()) {
			monthAndYear.setText(formatDateText(selectedDate.getTime()));
			calculateCalendar();
		}

		setCloseType(1);

		try {
			dealCommUCDefineVO(); //时间只选未来或过去用 【杨科/2012-08-30】
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.dispose();
	}

	private void onConfirm(final ActionEvent evt) {
		if (!check(getDataStringValue())) {
			MessageBox.show(this, "[" + this.getDataStringValue() + "]不在正确的时间范围内!");
			return;
		}

		setVisible(!hideOnSelect);
		if (isVisible()) {
			monthAndYear.setText(formatDateText(selectedDate.getTime()));
			calculateCalendar();
		}

		setCloseType(1);

		try {
			dealCommUCDefineVO(); //时间只选未来或过去用 【杨科/2012-08-30】
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.dispose();
	}

	private void onCancel() {
		setCloseType(2); //
		selectedDate = null;
		this.dispose();
	}

	/*
	 * 初始化日期控件的条件。{item}的会自动重面板找值，如果没有找到会做出提示。
	 */
	private void initCondition() throws Exception {
		String items[] = tbutil.getFormulaMacPars(condition);
		for (int i = 0; i < items.length; i++) {
			String item = items[i]; // 取得对比的字段
			if (item == null || item.trim().equals("")) { // 是{}标记，表示本字段
				continue;
			}
			BillPanel panel = getBillPanel();
			if (panel instanceof BillCardPanel) {
				BillCardPanel cardPanel = (BillCardPanel) panel;
				String itemValue = cardPanel.getBillVO().getStringValue(item);
				if (itemValue == null) {
					throw new WLTAppException("请先选择[" + cardPanel.getTempletVO().getItemVo(item).getItemname() + "]");
				} else {
					condition = tbutil.replaceAll(condition, "{" + item + "}", itemValue);
				}
			} else if (panel instanceof BillListPanel) {
				BillListPanel listPanel = (BillListPanel) panel;
				String itemValue = listPanel.getSelectedBillVO().getStringValue(item);
				if (itemValue == null) {
					throw new WLTAppException("请先选择[" + listPanel.getTempletVO().getItemVo(item).getItemname() + "]");
				} else {
					condition = condition.replace("{" + item + "}", itemValue);
				}
			}
		}
	}

	// 检验日期，传入一个比较的日期即可。会自动和条件进行匹配
	private boolean check(String checkDate) {
		return this.check(checkDate, condition);
	}

	private boolean check(String checkDate, String _condition) {
		boolean flag = true;
		if (_condition == null || _condition.equals("")) {
			return flag;
		} else {
			// {}<=2012-12-5
			_condition = _condition.toLowerCase().trim();
			if (_condition.contains("and")) {
				String[] conditions = tbutil.split(_condition, "and");
				for (int i = 0; i < conditions.length; i++) {
					String con = conditions[i]; // 单一条件
					if (con.contains("{}")) {
						con = tbutil.replaceAll(con, "{}", checkDate);
					} else {
						con = checkDate + con;
					}
					flag = compare(con);
					if (!flag) { // 如果false了，不符合条件，直接跳出
						return flag;
					}
				}
			} else {
				if (_condition.contains("{}")) {
					_condition = tbutil.replaceAll(_condition, "{}", checkDate);
				} else {
					_condition = checkDate + _condition;
				}
				flag = compare(_condition);
			}
		}
		return flag;
	}

	/*
	 * 公式比较配置方式 1、选择的日期大于或小于某个日期 {}>=2012-02-01 可以简写为 >=2012-02-01
	 * 2、选择的日期大于或者小于某个(日期的前后多少天) {}-30>=2012-02-01 可以简写为 -30>=2012-02-01
	 */
	private boolean compare(String _condition) {
		boolean flag = true;
		for (int i = 0; i < em.length; i++) {
			if (_condition.contains(em[i])) {
				String str[] = tbutil.split(_condition, em[i]); // 进行分割
				if (str != null && str.length > 2) {
					MessageBox.show(this, "该控件定义有问题,请检查!");
					flag = false;
				} else if (str != null && str.length == 2) { // 例如：
					// {}>=2012-02-01
					try {
						if (str[1].contains("-99999")) {
							throw new Exception("此日期的参照没有选择");
						}
						int difference = -90909090;
						if (str[0] != null && str[0].length() >= 10) {
							str[0] = str[0].trim();
							String data_1 = str[0].substring(0, 10);
							difference = compareTwoDate(str[1], data_1);// 求出这两个时间的差
						}
						if (str[0].trim().length() == 10) {
							flag = compareTwoNum(0 + em[i] + difference, em[i]);
						} else {
							flag = compareTwoNum(str[0].substring(10, str[0].length()) + em[i] + difference, em[i]);
						}
					} catch (Exception e) {
						flag = false;
						e.printStackTrace();
					}
				}
				return flag;
			}
		}
		return flag;
	}

	/*
	 * 比较两个数字
	 */
	private boolean compareTwoNum(String str, String en) {
		if (en == null) {
			return false;
		}
		boolean flag = false;
		en = en.trim();
		String[] s = str.split(en);
		int a = Integer.parseInt(s[0].trim().replace("+", ""));
		int b = Integer.parseInt(s[1].trim().replace("+", ""));
		if ("<=".equals(en)) {
			if (a <= b)
				flag = true;
		} else if (">=".equals(en)) {
			if (a >= b)
				flag = true;
		} else if ("<".equals(en)) {
			if (a < b)
				flag = true;
		} else if (">".equals(en)) {
			if (a > b)
				flag = true;
		} else if ("=".equals(en)) {
			if (a == b)
				flag = true;
		}
		return flag;
	}

	/*
	 * 两个时间的天数差。 first-second
	 */
	public int compareTwoDate(String firstDate, String secondDate) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = sdf.parse(firstDate);
		Date date2 = sdf.parse(secondDate);
		long mills = date1.getTime() - date2.getTime();
		return (int) (mills / (long) (1000 * 60 * 60 * 24));
	}

	public void itemStateChanged(ItemEvent e) {
		Date dt = selectedDate.getTime();
		int todayMonth = getTodayMonth(dt);
		int todayYear = getTodayYear(dt);

		final int year = Integer.parseInt((String) jboxYear.getSelectedItem());
		final int month = Integer.parseInt((String) jboxMonth.getSelectedItem());
		final int day = selectedDate.get(Calendar.DATE);

		selectedDate.set(Calendar.DATE, 1);
		selectedDate.add(Calendar.MONTH, month - todayMonth);
		selectedDate.add(Calendar.YEAR, year - todayYear);
		selectedDate.set(Calendar.DATE, Math.min(day, calculateDaysInMonth(selectedDate)));
		monthAndYear.setText(formatDateText(selectedDate.getTime()));

		calculateCalendar();
	}

	private void onForwardClicked(final java.awt.event.ActionEvent evt) {

		final int day = selectedDate.get(Calendar.DATE);
		selectedDate.set(Calendar.DATE, 1);
		selectedDate.add(Calendar.MONTH, 1);
		selectedDate.set(Calendar.DATE, Math.min(day, calculateDaysInMonth(selectedDate)));
		monthAndYear.setText(formatDateText(selectedDate.getTime()));

		final Date dt = selectedDate.getTime();
		final int tempYear = getTodayYear(dt);
		final int tempMonth = getTodayMonth(dt);

		if (tempYear >= baseYear + 200) {
		} else {
			this.jboxYear.setSelectedItem("" + tempYear);
			this.jboxMonth.setSelectedItem("" + tempMonth);
		}
		calculateCalendar();
	}

	private void onBackClicked(final java.awt.event.ActionEvent evt) {
		final int day = selectedDate.get(Calendar.DATE);
		selectedDate.set(Calendar.DATE, 1);
		selectedDate.add(Calendar.MONTH, -1);
		selectedDate.set(Calendar.DATE, Math.min(day, calculateDaysInMonth(selectedDate)));
		monthAndYear.setText(formatDateText(selectedDate.getTime()));

		final int tempYear = getTodayYear(selectedDate.getTime());
		final int tempMonth = getTodayMonth(selectedDate.getTime());

		if (tempYear < baseYear) {
		} else {
			this.jboxYear.setSelectedItem("" + tempYear);
			this.jboxMonth.setSelectedItem("" + tempMonth);
		}

		calculateCalendar();
	}

	private void renewBackground() {
		for (int ii = 0; ii < daysInMonth.length; ii++) {
			for (int jj = 0; jj < daysInMonth[ii].length; jj++) {
				JLabel tempLabel = (JLabel) daysInMonth[ii][jj];
				tempLabel.setBackground(white);
			}
		}
	}

	private void onDayClicked(final java.awt.event.MouseEvent evt) {
		final javax.swing.JLabel fld = (javax.swing.JLabel) evt.getSource();
		if (fld.isEnabled() && !"".equals(fld.getText())) {
			renewBackground();
			fld.setBackground(Color.green);// highlight
			selectedDay = fld;
			selectedDate.set(Calendar.DATE, Integer.parseInt(fld.getText()));
			// setVisible(!hideOnSelect);
		}
	}

	private static GregorianCalendar getToday() {
		final GregorianCalendar gc = new GregorianCalendar();
		gc.set(Calendar.HOUR_OF_DAY, 0);
		gc.set(Calendar.MINUTE, 0);
		gc.set(Calendar.SECOND, 0);
		gc.set(Calendar.MILLISECOND, 0);
		return gc;
	}

	private void calculateCalendar() {
		Locale.setDefault(Locale.CHINA);//设置地区，太平项目有子公司在英国，日历打开报错，故需要设置一下【李春娟/2017-09-18】

		if (null != selectedDay) {
			selectedDay.setBackground(white);
			selectedDay = null;
		}

		final GregorianCalendar c = new GregorianCalendar(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), 1);

		final int maxDay = calculateDaysInMonth(c);

		final int selectedDay = Math.min(maxDay, selectedDate.get(Calendar.DATE));

		int dow = c.get(Calendar.DAY_OF_WEEK);
		for (int dd = 0; dd < dow; dd++) {
			daysInMonth[0][dd].setText("");
		}
		int week;
		do {
			week = c.get(Calendar.WEEK_OF_MONTH);
			dow = c.get(Calendar.DAY_OF_WEEK);
			final JLabel fld = this.daysInMonth[week - 1][dow - 1];
			boolean canEnable = check(sdf.format(c.getTime()));
			fld.setEnabled(canEnable);
			fld.setText(Integer.toString(c.get(Calendar.DATE)));
			if (selectedDay == c.get(Calendar.DATE)) {
				fld.setBackground(Color.green);// highlight
				this.selectedDay = fld;
			}
			if (c.get(Calendar.DATE) >= maxDay)
				break;
			c.add(Calendar.DATE, 1);
		} while (c.get(Calendar.DATE) <= maxDay);

		week--;
		for (int ww = week; ww < daysInMonth.length; ww++) {
			for (int dd = dow; dd < daysInMonth[ww].length; dd++) {
				daysInMonth[ww][dd].setText("");
			}
			dow = 0;
		}

		c.set(Calendar.DATE, selectedDay);
		selectedDate = c; // 设置当前天数
	}

	private static int calculateDaysInMonth(final Calendar c) {
		int daysInMonth = 0;
		switch (c.get(Calendar.MONTH)) {
		case 0:
		case 2:
		case 4:
		case 6:
		case 7:
		case 9:
		case 11:
			daysInMonth = 31;
			break;
		case 3:
		case 5:
		case 8:
		case 10:
			daysInMonth = 30;
			break;
		case 1:
			final int year = c.get(Calendar.YEAR);
			daysInMonth = (0 == year % 1000) ? 29 : (0 == year % 100) ? 28 : (0 == year % 4) ? 29 : 28;
			break;
		}
		return daysInMonth;
	}

	private static int getTodayMonth(final Date dt) {
		// final DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
		//
		// final StringBuffer mm = new StringBuffer();
		// final FieldPosition mmfp = new FieldPosition(DateFormat.MONTH_FIELD);
		// df.format(dt, mm, mmfp); //
		//		

		return dt.getMonth() + 1;
		// return Integer.parseInt(mm.toString().substring(mmfp.getBeginIndex(),
		// mmfp.getEndIndex()).toString()); //
	}

	private static int getTodayYear(final Date dt) {
		final DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);

		final StringBuffer yy = new StringBuffer();
		final FieldPosition yyfp = new FieldPosition(DateFormat.YEAR_FIELD);
		df.format(dt, yy, yyfp);
		return Integer.parseInt(yy.toString().substring(yyfp.getBeginIndex(), yyfp.getEndIndex()).toString());
	}

	private static String formatDateText(final Date dt) {
		final DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);

		final StringBuffer mm = new StringBuffer();
		final StringBuffer yy = new StringBuffer();
		final FieldPosition mmfp = new FieldPosition(DateFormat.MONTH_FIELD);
		final FieldPosition yyfp = new FieldPosition(DateFormat.YEAR_FIELD);
		df.format(dt, mm, mmfp);
		df.format(dt, yy, yyfp);
		return (yy.toString().substring(yyfp.getBeginIndex(), yyfp.getEndIndex()) + " -- " + mm.toString().substring(mmfp.getBeginIndex(), mmfp.getEndIndex()) + "");
	}

	public RefItemVO getReturnRefItemVO() {
		String str_date = getDataStringValue();
		return new RefItemVO(str_date, null, str_date);
	}

	private String getDataStringValue() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(selectedDate.getTime());
	}

	private void dealCommUCDefineVO() throws Exception { //时间只选未来或过去用 【杨科/2012-08-30】 //其实本来有这个逻辑 条件 sfj
		if (commUCDefineVO != null && commUCDefineVO.getConfValue("只能选未来") != null && commUCDefineVO.getConfValue("只能选未来").equals("true")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			int l = compareTwoDate(getDataStringValue(), sdf.format(getToday().getTime()));
			if (l <= 0) {
				MessageBox.show(this, "日期只能选未来!");
				if (!(commUCDefineVO.getConfValue("只能选未来是否有返回值") != null && commUCDefineVO.getConfValue("只能选未来是否有返回值").equals("true"))) {
					setCloseType(2);
					selectedDate = null;
				}
			}
		}
		if (commUCDefineVO != null && commUCDefineVO.getConfValue("只能选过去") != null && commUCDefineVO.getConfValue("只能选过去").equals("true")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			int l = compareTwoDate(getDataStringValue(), sdf.format(getToday().getTime()));
			if (l >= 0) {
				MessageBox.show(this, "日期只能选过去!");
				if (!(commUCDefineVO.getConfValue("只能选过去是否有返回值") != null && commUCDefineVO.getConfValue("只能选过去是否有返回值").equals("true"))) {
					setCloseType(2);
					selectedDate = null;
				}
			}
		}
		if (commUCDefineVO != null && commUCDefineVO.getConfValue("只能选未来包含今天") != null && commUCDefineVO.getConfValue("只能选未来包含今天").equals("true")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			int l = compareTwoDate(getDataStringValue(), sdf.format(getToday().getTime()));
			if (l < 0) {
				MessageBox.show(this, "日期只能选未来!");
				if (!(commUCDefineVO.getConfValue("只能选未来是否有返回值") != null && commUCDefineVO.getConfValue("只能选未来是否有返回值").equals("true"))) {
					setCloseType(2);
					selectedDate = null;
				}
			}
		}
		if (commUCDefineVO != null && commUCDefineVO.getConfValue("只能选过去包含今天") != null && commUCDefineVO.getConfValue("只能选过去包含今天").equals("true")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			int l = compareTwoDate(getDataStringValue(), sdf.format(getToday().getTime()));
			if (l > 0) {
				MessageBox.show(this, "日期只能选过去!");
				if (!(commUCDefineVO.getConfValue("只能选过去是否有返回值") != null && commUCDefineVO.getConfValue("只能选过去是否有返回值").equals("true"))) {
					setCloseType(2);
					selectedDate = null;
				}
			}
		}
	}
}
