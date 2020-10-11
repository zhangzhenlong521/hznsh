package cn.com.infostrategy.ui.report.cellcompent;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import cn.com.infostrategy.ui.common.ClientEnvironment;

/**
 * Excel工具类,可以将Excel数据导出，也可将数据输出成Excel
 * 
 * @author xch
 * 
 */
public class ExcelUtil {

	private static final Object Str_value = null;
	private static int value =2;

	public String[][] getExcelFileData(String _filename) {
		return getExcelFileData(_filename, 0); //
	}

	/**
	 * 将Excel中的数据导出成二维数组
	 * 
	 * @param _filename
	 * @return
	 */
	public String[][] getExcelFileData(String _filename, int _sheetIndex) {
		Vector v_rows = new Vector();
		String[][] value = null;
		FileInputStream in = null; //
		int li_cellType = 0; //
		Workbook wb = null;
		try {
			in = new FileInputStream(_filename); //
			if (_filename.endsWith(".xls")) {
				POIFSFileSystem fs = new POIFSFileSystem(in); // 找开一个文件
				wb = new HSSFWorkbook(fs); // 打开面板
			} else {
				wb = new XSSFWorkbook(in); // 打开面板
			}
			Sheet sheet = wb.getSheetAt(_sheetIndex); // 取得第一个页签
			int li_firstrow = sheet.getFirstRowNum();
			int li_lastrow = sheet.getLastRowNum();
			int max = 0;
			for (int i = li_firstrow; i <= li_lastrow; i++) {
				Vector v_cols = new Vector();
				Row row = sheet.getRow(i); // 取得第一行
				if (row != null) { // 如果某一行不为空
					int li_firstcol = row.getFirstCellNum(); // 第一列的序号
					int li_lastcol = row.getLastCellNum(); // 最后一列的序号
					if (li_lastcol >= max) {
						max = li_lastcol; //
					}
					for (int j = 0; j <= li_lastcol; j++) {
						Cell cell = row.getCell((short) j); // 取到一个格子!
						if (cell != null) { // 如果某个格子不为空!
							li_cellType = cell.getCellType(); //
							if (li_cellType == Cell.CELL_TYPE_STRING) { // 如果格子中的类型是字符型
								// cell.setEncoding((short) 1); //
								// 设置编码,只有设成这样,中文才不会乱码,否则会出现中文乱码的情况!!!
								String str_value = cell.getStringCellValue(); //
								v_cols.add(str_value);
							} else if (li_cellType == Cell.CELL_TYPE_NUMERIC) {
								String str_value = "";
								if (DateUtil.isCellDateFormatted(cell)) {
									Date d = cell.getDateCellValue();
									DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
									str_value = formater.format(d);
									if (str_value.substring(0, 4).equals("1900")) {
										str_value = String.valueOf((long) cell.getNumericCellValue());
									}
								} else {
									str_value = getNumber(cell.getNumericCellValue());
								}
								v_cols.add(str_value);
							} else if (li_cellType == Cell.CELL_TYPE_FORMULA) {
								String str_value = null; //
								try {
									str_value = "" + cell.getStringCellValue(); //
								} catch (Exception ex) {
									System.err.println("文件[" + _filename + "]第[" + i + "]行[" + j + "]列的格子的公式值转字符出错!"); //
									try {
										str_value = "" + cell.getNumericCellValue(); //
									} catch (Exception exx) {
										System.err.println("文件[" + _filename + "]第[" + i + "]行[" + j + "]列的格子的公式值转数字出错!"); //
									}
								}
								v_cols.add(str_value == null ? "" : str_value);
							} else if (li_cellType == Cell.CELL_TYPE_ERROR) {
								String str_value = "" + cell.getErrorCellValue(); //
								v_cols.add(str_value);
							} else {
								String str_value = cell.getStringCellValue(); //
								v_cols.add(str_value);
							}
						} else {

							v_cols.add("");
						}
					}
				}

				v_rows.add(v_cols); //
			}

			value = new String[v_rows.size()][max];
			for (int i = 0; i < v_rows.size(); i++) {
				Vector v_itemrows = (Vector) v_rows.get(i); //
				for (int j = 0; j < value[i].length; j++) { //
					if (v_itemrows.size() > j) { //
						value[i][j] = (String) v_itemrows.get(j); //
					}
				}
			}

		} catch (Exception e) {
			System.out.println("读取[" + _filename + "]中的了错误的格子数据类型=[" + li_cellType + "]"); //
			e.printStackTrace(); //
			return null;
		} finally {
			try {
				in.close(); // 先关闭
			} catch (Exception e) {
			}
		}

		return value; //
	}

	//将科学计算法转换为文本格式	
	private String getNumber(double num) {
		String value = "";
		if (("" + num).indexOf("E") > 0 || ("" + num).endsWith(".0")) {
			DecimalFormat df = new DecimalFormat("0");
			value = "" + df.format(num);
		} else {
			try {
				value = "" + Integer.parseInt("" + num);
			} catch (NumberFormatException e) {
				String v = num + "";
				if (v.indexOf(".") > 0 && v.substring(v.indexOf(".") + 1, v.length()).length() > 6) {
					value = new BigDecimal(num).setScale(this.value, BigDecimal.ROUND_HALF_UP).toString();
				} else {
					value = num + "";
				}
			}
		}
		return value;
	}

	/**
	 * 完成EXCEL数据向二维数组的转换，拓展了导出的数据，建议以后转换时使用此函数！
	 * 
	 * @param fileName
	 *            输入要导入的文件名：全路径。例如：c:/import.xls
	 * @return 返回结果是一个二维字符串数组。
	 */
	public String[][] getExcelFileDataToStringArray(String fileName) {
		FileInputStream input = null;
		POIFSFileSystem fileSystem = null;
		HSSFWorkbook workBook = null;
		HSSFSheet sheet = null;
		HSSFRow row = null;
		HSSFCell cell = null;
		List rowList = null;
		List columnList = null;
		int beginRow = 0;
		int endRow = 0;
		int beginColumn = 0;
		int endColumn = 0;
		int maxColumns = 0;
		String values[][] = null;
		try {
			input = new FileInputStream(fileName);

			fileSystem = new POIFSFileSystem(input);
			workBook = new HSSFWorkbook(fileSystem);
			int cout = workBook.getNumberOfSheets();
			rowList = new ArrayList();
			for (int n = 0; n < cout; n++) {
				sheet = null;
				sheet = workBook.getSheetAt(n);
				beginRow = sheet.getFirstRowNum();
				endRow = sheet.getLastRowNum();
				for (int i = beginRow + 1; i <= endRow; i++) {
					row = sheet.getRow(i);
					if (row != null) {
						beginColumn = row.getFirstCellNum();
						endColumn = row.getLastCellNum();
						if (endColumn > maxColumns) {
							maxColumns = endColumn;
						}
						columnList = new ArrayList();
						for (int j = beginColumn; j < endColumn; j++) {
							cell = row.getCell((short) j);
							if (cell != null) {
								if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
									columnList.add(cell.getStringCellValue());
								} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
									String number = "";
									if (HSSFDateUtil.isCellDateFormatted(cell)) {
										Date d = cell.getDateCellValue();
										DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
										number = formater.format(d);
										if (number.substring(0, 4).equals("1900")) {
											number = String.valueOf((long) cell.getNumericCellValue());
										}
									} else {
										DecimalFormat df = new DecimalFormat("0.00");
										number = df.format(cell.getNumericCellValue());
									}
									// if (cell.getNumericCellValue() - ((long)
									// cell.getNumericCellValue()) != 0) { //
									// 目前运算不准确，以后偏差在0.01左右，还可以接受
									// DecimalFormat df = new
									// DecimalFormat("0.00");
									// number =
									// df.format(cell.getNumericCellValue());
									// } else {
									// Date d = cell.getDateCellValue();
									// DateFormat formater = new
									// SimpleDateFormat("yyyy-MM-dd");
									// number = formater.format(d);
									// if (number.substring(0,
									// 4).equals("1900")) {
									// number = String.valueOf((long)
									// cell.getNumericCellValue());
									// }
									// }
									columnList.add(number);
								} else if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
									columnList.add(String.valueOf(cell.getBooleanCellValue()));
								} else {
									columnList.add("");
								}
							} else {
								columnList.add("");
							}
						}
					}
					if (columnList.size() != 0) {
						rowList.add(columnList);
					}
				}
				values = new String[rowList.size()][maxColumns + 1];
				for (int i = 0; i < rowList.size(); i++) {
					columnList = (List) rowList.get(i);
					int j = 0;
					for (; j < columnList.size(); j++) {
						if (columnList.get(j) != null) {
							values[i][j] = new String(columnList.get(j).toString());
						} else {
							values[i][j] = null;
						}
					}
					// if (j <= maxColumns) {
					// for (int k = j; k < maxColumns; k++) {
					// values[i][k] = null;
					// }
					// }
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return values;
	}

	/**
	 * 将内存数据(二维数组)保存成一个Excel文件...
	 * 
	 * @param _data
	 *            二维数组
	 * @param _filename
	 *            输出的excel文件名
	 */
	public String setDataToExcelFile(String[][] _data, String _filename) {
		FileOutputStream fout = null;
		try {
			HSSFWorkbook wbook = new HSSFWorkbook(); // 建立新工作区..
			HSSFSheet wsheet = wbook.createSheet("ExportedData"); // 创建一个Sheet..
			wsheet.setGridsPrinted(true); // 有网格线..
			wsheet.setPrintGridlines(true); //
			wsheet.setFitToPage(true); //
			wsheet.setSelected(true); //
			wsheet.setAutobreaks(true); //
			wsheet.setDisplayGuts(true); //

			HSSFFont hf_font = wbook.createFont();
			hf_font.setFontName("宋体");
			hf_font.setColor(HSSFColor.BLACK.index);

			HSSFFont font_firstrow = wbook.createFont();
			font_firstrow.setFontName("宋体");
			font_firstrow.setColor(HSSFColor.BLACK.index);
			font_firstrow.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

			HSSFCellStyle cellstyle = wbook.createCellStyle(); // 风格
			cellstyle.setBorderRight((short) 1); //
			cellstyle.setBorderBottom((short) 1); //
			cellstyle.setRightBorderColor(HSSFColor.BLACK.index); //
			cellstyle.setBottomBorderColor(HSSFColor.BLACK.index); //
			cellstyle.setFillBackgroundColor(HSSFColor.ORANGE.index); // 不知为什么不起效果
			cellstyle.setFont(hf_font); //

			// liuxuanfei start
			HSSFCellStyle cellstyle_doublerow = wbook.createCellStyle();
			cellstyle_doublerow.cloneStyleFrom(cellstyle);
			cellstyle_doublerow.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND); // 纯色填充模式(默认为NO_FILL,
			// 即无填充),
			// 此模式下,
			// 前景色会覆盖背景色,
			// 所以也可以不设置背景色
			cellstyle_doublerow.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index); // 前景色并非字体颜色,
			// 根据不同的Pattern,
			// 呈现不同的风格.
			cellstyle_doublerow.setFillBackgroundColor(HSSFColor.ORANGE.index); // 当前景色与背景色同时存在时,
			// 前景色必须在背景色之前设置

			HSSFCellStyle cellstyle_firstrow = wbook.createCellStyle();
			cellstyle_firstrow.cloneStyleFrom(cellstyle);
			cellstyle_firstrow.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND); // 纯色填充模式(默认为NO_FILL,
			// 即无填充),
			// 此模式下,
			// 前景色会覆盖背景色,
			// 所以也可以不设置背景色
			cellstyle_firstrow.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index); // 前景色并非字体颜色,
			// 根据不同的Pattern,
			// 呈现不同的风格.
			cellstyle_firstrow.setFillBackgroundColor(HSSFColor.ORANGE.index); // 当前景色与背景色同时存在时,
			// 前景色必须在背景色之前设置
			cellstyle_firstrow.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			cellstyle_firstrow.setFont(font_firstrow);
			// liuxuanfei end

			if (_data != null && _data.length > 0) { // 如果有数据
				for (int i = 0; i < _data.length; i++) {
					HSSFRow row = wsheet.createRow((short) i); // 创建新行
					for (int j = 0; j < _data[i].length; j++) {
						HSSFCell dCell = row.createCell(j); // 创建新列
						dCell.setCellType(HSSFCell.CELL_TYPE_STRING); //
						// dCell.setEncoding(HSSFCell.ENCODING_UTF_16); //
						dCell.setCellValue(_data[i][j] == null ? "" : _data[i][j]); // 设置数据..
						if (i == 0) {
							wsheet.setColumnWidth(j, (int) 36.36 * 120); // 120像素
							dCell.setCellStyle(cellstyle_firstrow);
						} else if (i % 2 == 0) {
							dCell.setCellStyle(cellstyle_doublerow);
						} else {
							dCell.setCellStyle(cellstyle); //
						}
					}
				}
			}

			fout = new FileOutputStream(_filename, false); // 输出文件流..
			wbook.write(fout); // 将工作薄输出到文件中..
			ClientEnvironment.str_downLoadFileDir = _filename.substring(0, _filename.lastIndexOf("\\") + 1); //
			return "导出数据至文件【" + _filename + "】成功!!"; //
		} catch (Exception e) {
			e.printStackTrace(); //
			return "导出数据失败,原因是:" + e.getMessage(); //
		} finally {
			try {
				fout.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 将内存数据(二维数组)保存成一个Excel文件...
	 * 
	 * @param _data
	 *            二维数组
	 * @param _filename
	 *            输出的excel文件名
	 */
	public String setDataToExcelFile_xlsx(String[][] _data, String _filename) {
		FileOutputStream fout = null;
		try {
			SXSSFWorkbook wbook = new SXSSFWorkbook(); // 建立新工作区..
			SXSSFSheet wsheet = (SXSSFSheet) wbook.createSheet("ExportedData"); // 创建一个Sheet..
			//			wsheet.setDisplayGridlines(true);
			////			wsheet.setGridsPrinted(true); // 有网格线..
			//			wsheet.setPrintGridlines(true); //
			//			wsheet.setFitToPage(true); //
			//			wsheet.setSelected(true); //
			//			wsheet.setAutobreaks(true); //
			//			wsheet.setDisplayGuts(true); //

			Font hf_font = wbook.createFont();
			hf_font.setFontName("宋体");
			hf_font.setColor(HSSFColor.BLACK.index);

			Font font_firstrow = wbook.createFont();
			font_firstrow.setFontName("宋体");
			font_firstrow.setColor(HSSFColor.BLACK.index);
			font_firstrow.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

			CellStyle cellstyle = wbook.createCellStyle(); // 风格
			cellstyle.setBorderRight((short) 1); //
			cellstyle.setBorderBottom((short) 1); //
			cellstyle.setRightBorderColor(HSSFColor.BLACK.index); //
			cellstyle.setBottomBorderColor(HSSFColor.BLACK.index); //
			cellstyle.setFillBackgroundColor(HSSFColor.ORANGE.index); // 不知为什么不起效果
			cellstyle.setFont(hf_font); //

			// liuxuanfei start
			CellStyle cellstyle_doublerow = wbook.createCellStyle();
			cellstyle_doublerow.cloneStyleFrom(cellstyle);
			cellstyle_doublerow.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND); // 纯色填充模式(默认为NO_FILL,
			// 即无填充),
			// 此模式下,
			// 前景色会覆盖背景色,
			// 所以也可以不设置背景色
			cellstyle_doublerow.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index); // 前景色并非字体颜色,
			// 根据不同的Pattern,
			// 呈现不同的风格.
			cellstyle_doublerow.setFillBackgroundColor(HSSFColor.ORANGE.index); // 当前景色与背景色同时存在时,
			// 前景色必须在背景色之前设置

			CellStyle cellstyle_firstrow = wbook.createCellStyle();
			cellstyle_firstrow.cloneStyleFrom(cellstyle);
			cellstyle_firstrow.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND); // 纯色填充模式(默认为NO_FILL,
			// 即无填充),
			// 此模式下,
			// 前景色会覆盖背景色,
			// 所以也可以不设置背景色
			cellstyle_firstrow.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index); // 前景色并非字体颜色,
			// 根据不同的Pattern,
			// 呈现不同的风格.
			cellstyle_firstrow.setFillBackgroundColor(HSSFColor.ORANGE.index); // 当前景色与背景色同时存在时,
			// 前景色必须在背景色之前设置
			cellstyle_firstrow.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			cellstyle_firstrow.setFont(font_firstrow);
			// liuxuanfei end
			if (_data != null && _data.length > 0) { // 如果有数据
				for (int i = 0; i < _data.length; i++) {
					Row row = wsheet.createRow(i); // 创建新行
					for (int j = 0; j < _data[i].length; j++) {
						Cell dCell = row.createCell(j);
						dCell.setCellType(HSSFCell.CELL_TYPE_STRING); //
						dCell.setCellValue(_data[i][j] == null ? "" : _data[i][j]); // 设置数据..
						if (i == 0) {
							wsheet.setColumnWidth(j, (int) 36.36 * 120); // 120像素
							dCell.setCellStyle(cellstyle_firstrow);
						} else if (i % 2 == 0) {
							dCell.setCellStyle(cellstyle_doublerow);
						} else {
							dCell.setCellStyle(cellstyle); //
						}
					}
					if (i % 5000 == 0) {
						System.gc();
					}
				}
			}

			fout = new FileOutputStream(_filename, false); // 输出文件流..
			wbook.write(fout); // 将工作薄输出到文件中..
			ClientEnvironment.str_downLoadFileDir = _filename.substring(0, _filename.lastIndexOf("\\") + 1); //
			return "导出数据至文件【" + _filename + "】成功!!"; //
		} catch (Exception e) {
			e.printStackTrace(); //
			return "导出数据失败,原因是:" + e.getMessage(); //
		} finally {
			try {
				fout.close();
			} catch (Exception e) {
			}
		}
	}
	/**
	 * zzl[2018-11-15]设置excel导入小数保留位数
	 * @param a
	 */
	public void setDecimals(int a){
		this.value=a;
	}

	public static void main(String[] _args) {
		// String[][] str_data = new String[][] { { "第一行第一列", "aa2", "aa3" }, {
		// "bb1", "bb2", "bb3" }, { "cc1", "cc2", "cc3" } };
		// new ExcelUtil().setDataToExcelFile(str_data, "C:/ppp.xls"); //
		//		new ExcelUtil().getExcelFileData("C:/ddd.xls", 0);
	}
}
