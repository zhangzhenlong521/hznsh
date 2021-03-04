/**************************************************************************
 * $RCSfile: RefDialog_DateTime.java,v $  $Revision: 1.8 $  $Date: 2012/10/08 02:22:48 $
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
import java.util.TimeZone;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;

public final class RefDialog_DateTime extends AbstractRefDialog implements ItemListener {

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

	private final JButton backButton = new JButton();

	private final JLabel monthAndYear = new JLabel();

	private final JButton forwardButton = new JButton();

	private final JComboBox jboxYear = new JComboBox();

	private final JComboBox jboxMonth = new JComboBox();

	private final JLabel[] dayHeadings = new JLabel[] { new JLabel("日"), new JLabel("一"), new JLabel("二"), new JLabel("三"), new JLabel("四"), new JLabel("五"), new JLabel("六") };

	private final JLabel[][] daysInMonth = new JLabel[][] { { new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel() }, { new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel() },
			{ new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel() }, { new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel() },
			{ new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel() }, { new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel() } };

	private final JButton todayButton = new JButton();

	private final JButton assureButton = new JButton();

	private final JButton cancelButton = new JButton();

	private final JLabel yearLabel = new JLabel(" ");

	private final JLabel monthLabel = new JLabel(" ");

	private JLabel timeLabel = new JLabel("");

	protected DateFormat myFormat;

	private SpinnerDateModel model = null;

	private JSpinner spinner = null;

	private JSpinner.DateEditor editor = null;

	private String time_return = "";

	TimeZone tz = TimeZone.getDefault();

	public RefDialog_DateTime(Container _parent, String _title, RefItemVO value, BillPanel panel) {
		super(_parent, _title, value, panel);
	}

	public void initialize() {
		this.setResizable(false); //
		if (getInitRefItemVO() == null) {
			selectedDate = getToday();
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			selectedDate = new GregorianCalendar();
			try {
				selectedDate.setTime(sdf.parse(getInitRefItemVO().getId())); //
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}
		}

		JPanel con = WLTPanel.createDefaultPanel(null, WLTPanel.HORIZONTAL_FROM_MIDDLE); // 
		for (int i = 0; i < years; i++) {
			jboxYear.addItem("" + (baseYear + i));
		}
		for (int j = 1; j < 13; j++) {
			jboxMonth.addItem("" + j);
		}
		jboxYear.setBounds(10, 10, 80, 20);
		jboxYear.setFont(smallFont);
		jboxYear.addItemListener(this);

		yearLabel.setBounds(90, 10, 30, 20);
		yearLabel.setFont(largeFont);
		yearLabel.setHorizontalAlignment(JLabel.CENTER);

		jboxMonth.setBounds(120, 10, 70, 20);
		jboxMonth.setFont(smallFont);
		jboxMonth.addItemListener(this);

		monthLabel.setBounds(190, 10, 30, 20);
		monthLabel.setFont(largeFont);
		monthLabel.setHorizontalAlignment(JLabel.CENTER);

		Date dt = selectedDate.getTime();
		jboxYear.setSelectedItem("" + getTodayYear(dt));
		jboxMonth.setSelectedItem("" + getTodayMonth(dt));

		con.add(jboxMonth);
		con.add(monthLabel);
		con.add(jboxYear);
		con.add(yearLabel);

		backButton.setFont(smallFont);
		backButton.setText("<");
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
		monthAndYear.setText(formatDateText(selectedDate.getTime()));
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

		timeLabel.setBounds(30, 220, 70, 20);
		timeLabel.setFont(smallFont);
		timeLabel.setText(UIUtil.getLanguage("当前时间") + ":");
		con.add(timeLabel);

		model = new SpinnerDateModel(); //
		model.setCalendarField(Calendar.WEEK_OF_MONTH);
		spinner = new JSpinner(model); //微调器....model.setValue(selectedDate.getTime()); //设成带进来的值!! 以前是个bug!!!
		editor = new JSpinner.DateEditor(spinner, "HH:mm:ss");
		spinner.setEditor(editor);
		spinner.setBounds(90, 220, 100, 20);
		spinner.setFont(smallFont);
		spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				try {
					dealSpinner(e);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		spinner.getModel().setValue(dt); //
		con.add(spinner);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss"); //
		this.time_return = sdf.format(model.getDate());

		final Dimension buttonSize = new Dimension(68, 24);
		todayButton.setText(UIUtil.getLanguage("当前"));
		todayButton.setFont(smallFont);
		todayButton.setMargin(insets);
		todayButton.setMaximumSize(buttonSize);
		todayButton.setMinimumSize(buttonSize);
		todayButton.setPreferredSize(buttonSize);
		todayButton.setDefaultCapable(true);
		todayButton.setSelected(true);
		todayButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent evt) {
				onToday(evt);
			}
		});

		assureButton.setText(UIUtil.getLanguage("确定"));
		assureButton.setFont(smallFont);
		assureButton.setMargin(insets);
		assureButton.setMaximumSize(buttonSize);
		assureButton.setMinimumSize(buttonSize);
		assureButton.setPreferredSize(buttonSize);
		assureButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent evt) {
				onConfirm();
			}
		});

		cancelButton.setText(UIUtil.getLanguage("取消"));
		cancelButton.setFont(smallFont);
		cancelButton.setMargin(insets);
		cancelButton.setMaximumSize(buttonSize);
		cancelButton.setMinimumSize(buttonSize);
		cancelButton.setPreferredSize(buttonSize);
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent evt) {
				onCancel();
			}
		});

		todayButton.setBounds(15, 250, 60, 20);
		assureButton.setBounds(80, 250, 60, 20);
		cancelButton.setBounds(145, 250, 60, 20);
		con.add(todayButton);
		con.add(assureButton);
		con.add(cancelButton);

		calculateCalendar();

		this.setFocusable(true);
		this.setLayout(new BorderLayout());  //
		this.add(con);  //
	}

	public int getInitWidth() {
		return 235;
	}

	public int getInitHeight() {
		return 310;
	}

	public Date getDate() {
		if (null != selectedDate)
			return selectedDate.getTime();
		return null;
	}

	private void dealSpinner(ChangeEvent e) throws Exception {
		SpinnerDateModel source = (SpinnerDateModel) ((JSpinner) e.getSource()).getModel();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		this.time_return = sdf.format(source.getDate());
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

		GregorianCalendar cal = new GregorianCalendar(tz);
		Date dt = cal.getTime(); //使用的是

		spinner.getModel().setValue(dt);
	}

	private void doubleClick() {
		onConfirm();
	}

	private void onConfirm() {
		monthAndYear.setText(formatDateText(selectedDate.getTime()));
		calculateCalendar();
		this.setCloseType(1);
		this.dispose();
	}

	private void onCancel() {
		selectedDate = null;
		this.setCloseType(2);
		this.dispose();
	}

	public void itemStateChanged(ItemEvent e) {
		if (e.getSource().equals(jboxYear) || e.getSource().equals(jboxMonth)) {
			Date dt = selectedDate.getTime();
			int todayMonth = getTodayMonth(dt);
			int todayYear = getTodayYear(dt);

			//	testLabel.setText("the month is:" + todayMonth);
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
		if (!"".equals(fld.getText())) {
			renewBackground();
			fld.setBackground(Color.green);//highlight
			selectedDay = fld;
			selectedDate.set(Calendar.DATE, Integer.parseInt(fld.getText()));
			//	setVisible(!hideOnSelect);
		}
	}

	private static GregorianCalendar getToday() {
		final GregorianCalendar gc = new GregorianCalendar();
		//	gc.set(Calendar.HOUR_OF_DAY, 0);
		//	gc.set(Calendar.MINUTE, 0);
		//	gc.set(Calendar.SECOND, 0);
		//	gc.set(Calendar.MILLISECOND, 0);
		return gc;
	}

	private void calculateCalendar() {
		Locale.setDefault(Locale.CHINA);//设置地区，太平项目有子公司在英国，日历打开报错，故需要设置一下【李春娟/2017-09-18】
		if (null != selectedDay) {
			selectedDay.setBackground(white);
			selectedDay = null;
		}

		int li_day_month = selectedDate.get(Calendar.DAY_OF_MONTH);

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
				fld.setBackground(Color.green);//highlight
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

		if (spinner != null) {
			SpinnerDateModel spinnerModel = (SpinnerDateModel) (spinner.getModel());
			//c.set(Calendar.DATE, spinnerModel.getDate().getHours());
			c.set(Calendar.HOUR_OF_DAY, spinnerModel.getDate().getHours());
			c.set(Calendar.MINUTE, spinnerModel.getDate().getMinutes());
			c.set(Calendar.SECOND, spinnerModel.getDate().getSeconds());
		}

		selectedDate = c;
		selectedDate.set(Calendar.DAY_OF_MONTH, li_day_month); //
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
		//		df.format(dt, mm, mmfp);
		//		return Integer.parseInt(mm.toString().substring(mmfp.getBeginIndex(), mmfp.getEndIndex()).toString());

		return dt.getMonth() + 1;
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
		String str_timevalue = getDataStringValue();
		return new RefItemVO(str_timevalue, null, str_timevalue);
	}

	private String getDataStringValue() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(selectedDate.getTime());
	}

}
