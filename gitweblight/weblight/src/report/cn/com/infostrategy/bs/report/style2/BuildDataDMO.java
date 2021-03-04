package cn.com.infostrategy.bs.report.style2;

import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.util.HashMap;

import javax.swing.SwingUtilities;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.sysapp.login.CurrLoginUserVO;
import cn.com.infostrategy.ui.common.LookAndFeel;

/**
 * 风格报表2后台实际计算的逻辑
 * @author xch
 *
 */
public class BuildDataDMO extends AbstractDMO {

	public BillCellVO styleReport_2_BuildData(HashMap _condition, String _builderClassName, CurrLoginUserVO _loginUserVO) throws Exception {
		StyleReport_2_BuildDataIFC builder = (StyleReport_2_BuildDataIFC) Class.forName(_builderClassName).newInstance(); //
		HashVO[] hvs = builder.buildDataByCondition(_condition, _loginUserVO); //
		String str_title = builder.getTitle(); //标题
		String[][] str_sortColumns = builder.getSortColumns(); //
		String[] str_spanColumns = builder.getSpanColumns(); //

		BillCellVO cellVO = null;

		int li_rows = 10;
		int li_cols = 10; //
		if (hvs != null) {
			if (hvs.length == 0) {
				cellVO = new BillCellVO(); //
				li_rows = 5; //
				li_cols = 5; //
				cellVO.setRowlength(li_rows); //
				cellVO.setCollength(li_cols); //

				BillCellItemVO[][] cellItemVOs = new BillCellItemVO[li_rows][li_cols]; //
				cellItemVOs[0][0] = new BillCellItemVO(); //
				cellItemVOs[0][0].setCellvalue(str_title); //
				cellItemVOs[0][0].setHalign(2);
				cellItemVOs[0][0].setForeground("0,0,255"); //
				cellItemVOs[0][0].setFonttype("宋体");
				cellItemVOs[0][0].setFontsize("14"); //
				cellItemVOs[0][0].setFontstyle("1"); //
				cellItemVOs[0][0].setRowheight("35"); //
				cellItemVOs[0][0].setIseditable("false");
				cellItemVOs[0][0].setSpan("1,5"); //

				cellItemVOs[1][0] = new BillCellItemVO(); //
				cellItemVOs[1][0].setHalign(2);
				cellItemVOs[1][0].setCellvalue("返回结果数量为0"); //
				cellItemVOs[1][0].setSpan("1,5"); //
				cellItemVOs[1][0].setForeground("255,0,0"); //
				cellVO.setCellItemVOs(cellItemVOs); //
			} else {
				cellVO = getBillCellItemVOs(str_title, hvs, str_sortColumns, str_spanColumns); //
			}
		} else {
			cellVO = new BillCellVO(); //
			li_rows = 3; //
			li_cols = 3; //
			cellVO.setRowlength(li_rows); //
			cellVO.setCollength(li_cols); //

			BillCellItemVO[][] cellItemVOs = new BillCellItemVO[li_rows][li_cols]; //
			cellItemVOs[0][0] = new BillCellItemVO(); //
			cellItemVOs[0][0].setCellvalue(str_title); //
			cellItemVOs[0][0].setHalign(2);
			cellItemVOs[0][0].setForeground("0,0,255"); //
			cellItemVOs[0][0].setFonttype("宋体");
			cellItemVOs[0][0].setFontsize("14"); //
			cellItemVOs[0][0].setFontstyle("1"); //
			cellItemVOs[0][0].setRowheight("35"); //
			cellItemVOs[0][0].setIseditable("false");
			cellItemVOs[0][0].setSpan("1,3"); //
			cellItemVOs[0][0].setSpan("1,3"); //

			cellItemVOs[1][0] = new BillCellItemVO(); //
			cellItemVOs[1][0].setHalign(2);
			cellItemVOs[1][0].setCellvalue("返回结果集为空"); //
			cellItemVOs[1][0].setSpan("1,3"); //
			cellVO.setCellItemVOs(cellItemVOs); //
		}

		return cellVO;
	}

	/**
	 * 生成表格数据..
	 * @param _title
	 * @param _hashVOs
	 * @return
	 */
	private BillCellVO getBillCellItemVOs(String _title, HashVO[] _hashVOs, String[][] _sortColumns, String[] _spanColumns) {
		String[] str_keys = _hashVOs[0].getKeys(); //
		int li_rows = _hashVOs.length + 2; //
		int li_cols = str_keys.length; //

		BillCellVO cellVO = new BillCellVO(); //
		cellVO.setRowlength(li_rows); //
		cellVO.setCollength(li_cols); //

		int li_defaultRowHeight = 23; //
		BillCellItemVO[][] cellItemVOs = new BillCellItemVO[li_rows][li_cols]; //
		for (int i = 0; i < cellItemVOs.length; i++) {
			for (int j = 0; j < cellItemVOs[i].length; j++) {
				cellItemVOs[i][j] = new BillCellItemVO(); //
				cellItemVOs[i][j].setCellkey(i + "," + j);
				cellItemVOs[i][j].setCelltype("TEXTAREA"); //
				cellItemVOs[i][j].setCellrow(i); //
				cellItemVOs[i][j].setCellcol(j); //
				cellItemVOs[i][j].setCellvalue(""); //
				cellItemVOs[i][j].setSpan("1,1"); //
				cellItemVOs[i][j].setRowheight("" + li_defaultRowHeight); //
				cellItemVOs[i][j].setColwidth("75"); //
			}
		}

		//标题
		cellItemVOs[0][0].setCellvalue(_title); //
		cellItemVOs[0][0].setHalign(2); //
		cellItemVOs[0][0].setValign(2); //
		cellItemVOs[0][0].setForeground("0,0,255"); //
		cellItemVOs[0][0].setFonttype("新宋体");
		cellItemVOs[0][0].setFontsize("14"); //
		cellItemVOs[0][0].setFontstyle("1"); //
		cellItemVOs[0][0].setRowheight("35"); //
		cellItemVOs[0][0].setIseditable("false");
		cellItemVOs[0][0].setSpan("1," + str_keys.length); //

		//表头
		for (int i = 0; i < str_keys.length; i++) {
			cellItemVOs[1][i].setCellvalue(str_keys[i]); //
			cellItemVOs[1][i].setHalign(2);
			cellItemVOs[1][i].setRowheight("35"); //
			cellItemVOs[1][i].setBackground("232,255,255"); //
			//cellItemVOs[1][i].setForeground("0,0,255"); //
		}

		TBUtil tbUtil = new TBUtil();

		//处理排序,直接处理HashVO[]
		if (_sortColumns != null) {
			tbUtil.sortHashVOs(_hashVOs, _sortColumns); //对HashVO[]数组进行排序!!!
		}

		//表体,即所有记录!!!!!!!!!!!!!!!核心内容...
		for (int i = 2; i < cellItemVOs.length; i++) {
			for (int j = 0; j < str_keys.length; j++) {
				//cellItemVOs[i][j].setHalign(2); //
				cellItemVOs[i][j].setValign(2); //
				cellItemVOs[i][j].setCellvalue(_hashVOs[i - 2].getStringValue(str_keys[j], "")); //
			}
		}

		//处理合并
		if (_spanColumns != null) {
			for (int i = 0; i < _spanColumns.length; i++) {
				int li_pos = -1; //
				for (int j = 0; j < str_keys.length; j++) {
					if (str_keys[j].equals(_spanColumns[i])) {
						li_pos = j; //
					}
				}

				if (li_pos >= 0) { //发现定义的合并的列在数据中是存在的!
					int li_spancount = 1; //
					int li_spanbeginpos = 1; //
					for (int k = 1; k < cellItemVOs.length; k++) {
						String str_value = cellItemVOs[k][li_pos].getCellvalue(); //
						String str_value_front = cellItemVOs[k - 1][li_pos].getCellvalue(); //
						if (tbUtil.compareTwoString(str_value_front, str_value)) { //如果与前面的相等,则继续累加
							li_spancount++;
						} else { //如果两者不相等
							cellItemVOs[li_spanbeginpos][li_pos].setSpan(li_spancount + ",1"); //
							li_spancount = 1; //初始化合并行数据
							li_spanbeginpos = k; //重新记录准备合并的行
						}
					}

					cellItemVOs[li_spanbeginpos][li_pos].setSpan(li_spancount + ",1"); //
				}
			}
		}

		//动态计算行高与列宽,使得表格出来是一个尽可能匀称的表格,即比较漂亮!!..待续...
		FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(LookAndFeel.font); //
		int li_allowMaxColWidth = 175; //允许的最大列度,即再大也不能大于这个宽度.
		//计算出各列最大的宽度
		for (int j = 0; j < str_keys.length; j++) { //遍历各列
			int li_maxwidth = 70; //
			String str_cellValue = null; //
			for (int i = 1; i < cellItemVOs.length; i++) { //遍列该列的各行
				str_cellValue = cellItemVOs[i][j].getCellvalue(); //
				if (str_cellValue != null && !str_cellValue.trim().equals("")) {
					int li_width = SwingUtilities.computeStringWidth(fm, str_cellValue); //
					if (li_width > li_maxwidth) {
						li_maxwidth = li_width; //最大
					}
				}
			}

			li_maxwidth = li_maxwidth + 10; //为了好看向右多5个像素,否则靠的太挤不好看!
			if (li_maxwidth > li_allowMaxColWidth) {
				li_maxwidth = li_allowMaxColWidth; //
			}

			for (int i = 1; i < cellItemVOs.length; i++) { //遍列该列的各行
				str_cellValue = cellItemVOs[i][j].getCellvalue(); //
				if (str_cellValue != null && !str_cellValue.trim().equals("")) {
					int li_width = SwingUtilities.computeStringWidth(fm, str_cellValue); //
					if (li_width > 0) {
						int li_length = (li_width / li_maxwidth) + 1; //有几行
						int li_itemRowHeight = li_length * 17 + 5; //
						if (i == 1) {
							if (li_itemRowHeight > 35) {
								cellItemVOs[i][j].setRowheight("" + li_itemRowHeight); //
							} else {
								cellItemVOs[i][j].setRowheight("35"); //
							}
						} else {
							cellItemVOs[i][j].setRowheight("" + li_itemRowHeight); //
						}
						cellItemVOs[i][j].setColwidth("" + li_maxwidth); //
					}
				}
			}
		}
		cellVO.setCellItemVOs(cellItemVOs); //
		return cellVO;
	}
	
	
}
