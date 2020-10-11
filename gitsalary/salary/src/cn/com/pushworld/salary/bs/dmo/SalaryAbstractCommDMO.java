package cn.com.pushworld.salary.bs.dmo;

import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.SwingUtilities;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.pushworld.salary.to.SalaryTBUtil;

public abstract class SalaryAbstractCommDMO extends AbstractDMO {
	private CommDMO dmo = null;
	private TBUtil tb = null;
	private SalaryTBUtil stbutil = null;

	public TBUtil getTb() {
		if (tb == null) {
			tb = new TBUtil();
		}
		return tb;
	}

	public CommDMO getDmo() {
		if (dmo == null) {
			dmo = new CommDMO();
		}
		return dmo;
	}

	public BillCellItemVO getBillTitleCellItemVO(String value) {
		BillCellItemVO item = new BillCellItemVO();
		item.setIseditable("N");
		item.setCellvalue(value);
		item.setBackground("184,255,185");
		item.setFonttype("新宋体");
		item.setFontsize("12");
		item.setFontstyle("1");
		item.setSpan("1,1");
		return item;
	}

	// 背景搞成隔行出现 Gwang 2013-08-29
	public BillCellItemVO getBillNormalCellItemVO(int row, String value) {
		BillCellItemVO item = new BillCellItemVO();
		item.setIseditable("N");
		item.setCellvalue(value);
		if (row % 2 == 0) {
			item.setBackground("234,240,248");
		} else {
			item.setBackground("255,255,255");
		}
		item.setFonttype("新宋体");
		item.setFontsize("12");
		item.setFontstyle("0");
		item.setSpan("1,1");
		return item;
	}
	
	/**
	 * 前几个月的str
	 * 
	 * @param date
	 * @param frontmonth
	 * @return
	 * @throws Exception
	 */
	public String getBackMonth(String date, int frontmonth) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.SIMPLIFIED_CHINESE);
		Calendar c = new GregorianCalendar();
		c.setTime(sdf.parse(date));
		c.add(c.MONTH, -frontmonth);
		return sdf.format(c.getTime());
	}
	/**
	 * 设置单元格的长度，根据字的长度算一把
	 * 
	 * @param items
	 */
	public void formatClen(BillCellItemVO[][] items) {
		int li_allowMaxColWidth = 375;
		FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(LookAndFeel.font_b);
		for (int j = 0; j < items[0].length; j++) {
			int li_maxwidth = 0;
			String str_cellValue = null;
			for (int i = 0; i < items.length; i++) {
				str_cellValue = items[i][j].getCellvalue();
				if (str_cellValue != null && !str_cellValue.trim().equals("") && "1,1".equals(items[i][j].getSpan())) {
					int li_width = SwingUtilities.computeStringWidth(fm, str_cellValue) + 10;
					if (li_width > li_maxwidth) {
						li_maxwidth = li_width;
					}
				}
			}
			li_maxwidth = li_maxwidth + 13;
			if (li_maxwidth > li_allowMaxColWidth) {
				li_maxwidth = li_allowMaxColWidth;
			}
			for (int i = 1; i < items.length; i++) {
				str_cellValue = items[i][j].getCellvalue();
				items[i][j].setColwidth("" + li_maxwidth);
			}
		}
	}

	/**
	 * 将单元格相同内容的合并
	 * 
	 * @param cellItemVOs
	 * @param _spanColumns
	 *            那几列需要处理
	 */
	public void formatSpan(BillCellItemVO[][] cellItemVOs, int[] _spanColumns) {
		if (_spanColumns != null) {
			HashMap temp = new HashMap();
			for (int i = 0; i < _spanColumns.length; i++) {
				int li_pos = _spanColumns[i];
				if (li_pos >= 0) {
					int li_spancount = 1;
					int li_spanbeginpos = 1;
					for (int k = 2; k < cellItemVOs.length; k++) {
						String str_value = cellItemVOs[k][li_pos].getCellvalue();
						// 合并的列颜色一样
						cellItemVOs[k][li_pos].setBackground("234,240,248");
						String str_value_front = cellItemVOs[k - 1][li_pos].getCellvalue();
						if (getTb().compareTwoString(str_value_front, str_value)) {
							if (i >= 1) {
								String str_value0 = cellItemVOs[k][_spanColumns[i - 1]].getCellvalue();
								String str_value_front0 = cellItemVOs[k - 1][_spanColumns[i - 1]].getCellvalue();
								if (getTb().compareTwoString(str_value0, str_value_front0)) {
									li_spancount++;
								} else {
									cellItemVOs[li_spanbeginpos][li_pos].setSpan(li_spancount + ",1");
									li_spancount = 1;
									li_spanbeginpos = k;
								}
							} else {
								li_spancount++;
							}

						} else {
							cellItemVOs[li_spanbeginpos][li_pos].setSpan(li_spancount + ",1");
							li_spancount = 1;
							li_spanbeginpos = k;
						}
					}
					cellItemVOs[li_spanbeginpos][li_pos].setSpan(li_spancount + ",1");
				}
			}
		}
	}

	public SalaryTBUtil getSalaryTBUtil() {
		if (stbutil == null) {
			stbutil = new SalaryTBUtil();
		}
		return stbutil;
	}
	public String getColorByState(String statestr) {
		if (statestr == null || "".equals(statestr)) {
			return "191,213,255";
		} else if (statestr.equals("未评分")) {
			return "255,31,32";
		} else if (statestr.equals("已完成") || statestr.equals("已提交") || statestr.equals("评分完成") || statestr.equals("-")) {
			return "61,137,211";
		} else if (statestr.equals("评分中") || statestr.equals("待提交")) {
			return "1,164,97";
		} else if (statestr.equals("申请修改")) {
			return "191,0,255";
		}
		return "191,213,255";
	}
}
