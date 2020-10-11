package cn.com.infostrategy.ui.mdata.querycomp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;

public class QueryDateModelPanel extends JPanel implements ItemListener {
	private static final long serialVersionUID = 1L;

	private RefItemVO initRefItemVO = null;
	private static final int startX = 10;

	private static final int startY = 90;

	private static final int baseYear = new TBUtil().getSysOptionIntegerValue("日历选择的开始年份", 1950);
	
	private static final int years = new TBUtil().getSysOptionIntegerValue("日历选择的年份范围", 100);

	private static final Font smallFont = new Font("宋体", Font.PLAIN, 12);

	private static final Font largeFont = new Font("宋体", Font.PLAIN, 14);

	private static final Insets insets = new Insets(2, 2, 2, 2);

	private static final Color white = new Color(255, 255, 255);

	private Component selectedDay = null;

	private GregorianCalendar selectedDate = null;

	private boolean hideOnSelect = true;

	private final JButton backButton = new JButton();

	private final JLabel monthAndYear = new JLabel();

	private final JButton forwardButton = new JButton();

	private final JComboBox jboxYear = new JComboBox();

	private final JComboBox jboxMonth = new JComboBox();

	private final JLabel[] dayHeadings = new JLabel[] { new JLabel("日"), new JLabel("一"), new JLabel("二"), new JLabel("三"), new JLabel("四"), new JLabel("五"), new JLabel("六") };

	private final JLabel[][] daysInMonth = new JLabel[][] { { new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel() }, { new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel() },
			{ new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel() }, { new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel() },
			{ new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel() }, { new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel() } };

	private JButton todayButton = null;

	private final JLabel yearLabel = new JLabel(" "); //

	private final JLabel monthLabel = new JLabel(" "); //

	public QueryDateModelPanel() {
		initialize();
	}

	public void initialize() {
		this.setLayout(new BorderLayout());
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

		JPanel con = WLTPanel.createDefaultPanel(null,WLTPanel.HORIZONTAL_FROM_MIDDLE);  //
		//con.setLayout(null);
		//con.setBackground(new Color(240, 240, 240));

		jboxYear.setPreferredSize(new Dimension(40, 18));
		jboxYear.setFont(new Font("宋体", Font.PLAIN, 12)); //						
		for (int i = 0; i < years; i++) {
			jboxYear.addItem("" + (baseYear + i));
		}
		for (int j = 1; j < 13; j++) {
			jboxMonth.addItem("" + j);
		}

		jboxYear.setBounds(10, 10, 80, 18);
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
							//doubleClick();
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

		todayButton.setBounds(80, 220, 60, 20);

		con.add(todayButton);
		calculateCalendar();
		this.setFocusable(true);
		this.add(con, BorderLayout.CENTER);

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
		return 268;
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

	//	private void doubleClick() {
	//		setVisible(!hideOnSelect);
	//		if (isVisible()) {
	//			monthAndYear.setText(formatDateText(selectedDate.getTime()));
	//			calculateCalendar();
	//		}
	//		setCloseType(1); //
	//		this.dispose();
	//	}
	//
	//	private void onConfirm(final ActionEvent evt) {
	//		setVisible(!hideOnSelect);
	//		if (isVisible()) {
	//			monthAndYear.setText(formatDateText(selectedDate.getTime()));
	//			calculateCalendar();
	//		}
	//		setCloseType(1); //
	//		this.dispose();
	//	}
	//
	//	private void onCancel() {
	//		setCloseType(2); //
	//		selectedDate = null;
	//		this.dispose();
	//	}

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

		if (tempYear >= baseYear + years) {
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
		if (!"".equals(fld.getText())) {
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

	public void calculateCalendar() {
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
		selectedDate = c; //设置当前天数
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
		//		final DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
		//
		//		final StringBuffer mm = new StringBuffer();
		//		final FieldPosition mmfp = new FieldPosition(DateFormat.MONTH_FIELD);
		//		df.format(dt, mm, mmfp);  //
		//		

		return dt.getMonth() + 1;
		//return Integer.parseInt(mm.toString().substring(mmfp.getBeginIndex(), mmfp.getEndIndex()).toString());  //
	}

	private static int getTodayYear(final Date dt) {
		final DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);

		final StringBuffer yy = new StringBuffer();
		final FieldPosition yyfp = new FieldPosition(DateFormat.YEAR_FIELD);
		df.format(dt, yy, yyfp);
		return Integer.parseInt(yy.toString().substring(yyfp.getBeginIndex(), yyfp.getEndIndex()).toString());
	}

	public static String formatDateText(final Date dt) {
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

	public String getDataStringValue() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(selectedDate.getTime());
	}

	public RefItemVO getInitRefItemVO() {
		return initRefItemVO;
	}

	public GregorianCalendar getSelectedDate() {
		return selectedDate;
	}
}
