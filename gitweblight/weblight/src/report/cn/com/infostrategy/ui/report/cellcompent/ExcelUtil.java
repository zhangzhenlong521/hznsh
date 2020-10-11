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
 * Excel������,���Խ�Excel���ݵ�����Ҳ�ɽ����������Excel
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
	 * ��Excel�е����ݵ����ɶ�ά����
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
				POIFSFileSystem fs = new POIFSFileSystem(in); // �ҿ�һ���ļ�
				wb = new HSSFWorkbook(fs); // �����
			} else {
				wb = new XSSFWorkbook(in); // �����
			}
			Sheet sheet = wb.getSheetAt(_sheetIndex); // ȡ�õ�һ��ҳǩ
			int li_firstrow = sheet.getFirstRowNum();
			int li_lastrow = sheet.getLastRowNum();
			int max = 0;
			for (int i = li_firstrow; i <= li_lastrow; i++) {
				Vector v_cols = new Vector();
				Row row = sheet.getRow(i); // ȡ�õ�һ��
				if (row != null) { // ���ĳһ�в�Ϊ��
					int li_firstcol = row.getFirstCellNum(); // ��һ�е����
					int li_lastcol = row.getLastCellNum(); // ���һ�е����
					if (li_lastcol >= max) {
						max = li_lastcol; //
					}
					for (int j = 0; j <= li_lastcol; j++) {
						Cell cell = row.getCell((short) j); // ȡ��һ������!
						if (cell != null) { // ���ĳ�����Ӳ�Ϊ��!
							li_cellType = cell.getCellType(); //
							if (li_cellType == Cell.CELL_TYPE_STRING) { // ��������е��������ַ���
								// cell.setEncoding((short) 1); //
								// ���ñ���,ֻ���������,���ĲŲ�������,��������������������!!!
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
									System.err.println("�ļ�[" + _filename + "]��[" + i + "]��[" + j + "]�еĸ��ӵĹ�ʽֵת�ַ�����!"); //
									try {
										str_value = "" + cell.getNumericCellValue(); //
									} catch (Exception exx) {
										System.err.println("�ļ�[" + _filename + "]��[" + i + "]��[" + j + "]�еĸ��ӵĹ�ʽֵת���ֳ���!"); //
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
			System.out.println("��ȡ[" + _filename + "]�е��˴���ĸ�����������=[" + li_cellType + "]"); //
			e.printStackTrace(); //
			return null;
		} finally {
			try {
				in.close(); // �ȹر�
			} catch (Exception e) {
			}
		}

		return value; //
	}

	//����ѧ���㷨ת��Ϊ�ı���ʽ	
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
	 * ���EXCEL�������ά�����ת������չ�˵��������ݣ������Ժ�ת��ʱʹ�ô˺�����
	 * 
	 * @param fileName
	 *            ����Ҫ������ļ�����ȫ·�������磺c:/import.xls
	 * @return ���ؽ����һ����ά�ַ������顣
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
									// Ŀǰ���㲻׼ȷ���Ժ�ƫ����0.01���ң������Խ���
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
	 * ���ڴ�����(��ά����)�����һ��Excel�ļ�...
	 * 
	 * @param _data
	 *            ��ά����
	 * @param _filename
	 *            �����excel�ļ���
	 */
	public String setDataToExcelFile(String[][] _data, String _filename) {
		FileOutputStream fout = null;
		try {
			HSSFWorkbook wbook = new HSSFWorkbook(); // �����¹�����..
			HSSFSheet wsheet = wbook.createSheet("ExportedData"); // ����һ��Sheet..
			wsheet.setGridsPrinted(true); // ��������..
			wsheet.setPrintGridlines(true); //
			wsheet.setFitToPage(true); //
			wsheet.setSelected(true); //
			wsheet.setAutobreaks(true); //
			wsheet.setDisplayGuts(true); //

			HSSFFont hf_font = wbook.createFont();
			hf_font.setFontName("����");
			hf_font.setColor(HSSFColor.BLACK.index);

			HSSFFont font_firstrow = wbook.createFont();
			font_firstrow.setFontName("����");
			font_firstrow.setColor(HSSFColor.BLACK.index);
			font_firstrow.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

			HSSFCellStyle cellstyle = wbook.createCellStyle(); // ���
			cellstyle.setBorderRight((short) 1); //
			cellstyle.setBorderBottom((short) 1); //
			cellstyle.setRightBorderColor(HSSFColor.BLACK.index); //
			cellstyle.setBottomBorderColor(HSSFColor.BLACK.index); //
			cellstyle.setFillBackgroundColor(HSSFColor.ORANGE.index); // ��֪Ϊʲô����Ч��
			cellstyle.setFont(hf_font); //

			// liuxuanfei start
			HSSFCellStyle cellstyle_doublerow = wbook.createCellStyle();
			cellstyle_doublerow.cloneStyleFrom(cellstyle);
			cellstyle_doublerow.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND); // ��ɫ���ģʽ(Ĭ��ΪNO_FILL,
			// �������),
			// ��ģʽ��,
			// ǰ��ɫ�Ḳ�Ǳ���ɫ,
			// ����Ҳ���Բ����ñ���ɫ
			cellstyle_doublerow.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index); // ǰ��ɫ����������ɫ,
			// ���ݲ�ͬ��Pattern,
			// ���ֲ�ͬ�ķ��.
			cellstyle_doublerow.setFillBackgroundColor(HSSFColor.ORANGE.index); // ��ǰ��ɫ�뱳��ɫͬʱ����ʱ,
			// ǰ��ɫ�����ڱ���ɫ֮ǰ����

			HSSFCellStyle cellstyle_firstrow = wbook.createCellStyle();
			cellstyle_firstrow.cloneStyleFrom(cellstyle);
			cellstyle_firstrow.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND); // ��ɫ���ģʽ(Ĭ��ΪNO_FILL,
			// �������),
			// ��ģʽ��,
			// ǰ��ɫ�Ḳ�Ǳ���ɫ,
			// ����Ҳ���Բ����ñ���ɫ
			cellstyle_firstrow.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index); // ǰ��ɫ����������ɫ,
			// ���ݲ�ͬ��Pattern,
			// ���ֲ�ͬ�ķ��.
			cellstyle_firstrow.setFillBackgroundColor(HSSFColor.ORANGE.index); // ��ǰ��ɫ�뱳��ɫͬʱ����ʱ,
			// ǰ��ɫ�����ڱ���ɫ֮ǰ����
			cellstyle_firstrow.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			cellstyle_firstrow.setFont(font_firstrow);
			// liuxuanfei end

			if (_data != null && _data.length > 0) { // ���������
				for (int i = 0; i < _data.length; i++) {
					HSSFRow row = wsheet.createRow((short) i); // ��������
					for (int j = 0; j < _data[i].length; j++) {
						HSSFCell dCell = row.createCell(j); // ��������
						dCell.setCellType(HSSFCell.CELL_TYPE_STRING); //
						// dCell.setEncoding(HSSFCell.ENCODING_UTF_16); //
						dCell.setCellValue(_data[i][j] == null ? "" : _data[i][j]); // ��������..
						if (i == 0) {
							wsheet.setColumnWidth(j, (int) 36.36 * 120); // 120����
							dCell.setCellStyle(cellstyle_firstrow);
						} else if (i % 2 == 0) {
							dCell.setCellStyle(cellstyle_doublerow);
						} else {
							dCell.setCellStyle(cellstyle); //
						}
					}
				}
			}

			fout = new FileOutputStream(_filename, false); // ����ļ���..
			wbook.write(fout); // ��������������ļ���..
			ClientEnvironment.str_downLoadFileDir = _filename.substring(0, _filename.lastIndexOf("\\") + 1); //
			return "�����������ļ���" + _filename + "���ɹ�!!"; //
		} catch (Exception e) {
			e.printStackTrace(); //
			return "��������ʧ��,ԭ����:" + e.getMessage(); //
		} finally {
			try {
				fout.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * ���ڴ�����(��ά����)�����һ��Excel�ļ�...
	 * 
	 * @param _data
	 *            ��ά����
	 * @param _filename
	 *            �����excel�ļ���
	 */
	public String setDataToExcelFile_xlsx(String[][] _data, String _filename) {
		FileOutputStream fout = null;
		try {
			SXSSFWorkbook wbook = new SXSSFWorkbook(); // �����¹�����..
			SXSSFSheet wsheet = (SXSSFSheet) wbook.createSheet("ExportedData"); // ����һ��Sheet..
			//			wsheet.setDisplayGridlines(true);
			////			wsheet.setGridsPrinted(true); // ��������..
			//			wsheet.setPrintGridlines(true); //
			//			wsheet.setFitToPage(true); //
			//			wsheet.setSelected(true); //
			//			wsheet.setAutobreaks(true); //
			//			wsheet.setDisplayGuts(true); //

			Font hf_font = wbook.createFont();
			hf_font.setFontName("����");
			hf_font.setColor(HSSFColor.BLACK.index);

			Font font_firstrow = wbook.createFont();
			font_firstrow.setFontName("����");
			font_firstrow.setColor(HSSFColor.BLACK.index);
			font_firstrow.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

			CellStyle cellstyle = wbook.createCellStyle(); // ���
			cellstyle.setBorderRight((short) 1); //
			cellstyle.setBorderBottom((short) 1); //
			cellstyle.setRightBorderColor(HSSFColor.BLACK.index); //
			cellstyle.setBottomBorderColor(HSSFColor.BLACK.index); //
			cellstyle.setFillBackgroundColor(HSSFColor.ORANGE.index); // ��֪Ϊʲô����Ч��
			cellstyle.setFont(hf_font); //

			// liuxuanfei start
			CellStyle cellstyle_doublerow = wbook.createCellStyle();
			cellstyle_doublerow.cloneStyleFrom(cellstyle);
			cellstyle_doublerow.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND); // ��ɫ���ģʽ(Ĭ��ΪNO_FILL,
			// �������),
			// ��ģʽ��,
			// ǰ��ɫ�Ḳ�Ǳ���ɫ,
			// ����Ҳ���Բ����ñ���ɫ
			cellstyle_doublerow.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index); // ǰ��ɫ����������ɫ,
			// ���ݲ�ͬ��Pattern,
			// ���ֲ�ͬ�ķ��.
			cellstyle_doublerow.setFillBackgroundColor(HSSFColor.ORANGE.index); // ��ǰ��ɫ�뱳��ɫͬʱ����ʱ,
			// ǰ��ɫ�����ڱ���ɫ֮ǰ����

			CellStyle cellstyle_firstrow = wbook.createCellStyle();
			cellstyle_firstrow.cloneStyleFrom(cellstyle);
			cellstyle_firstrow.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND); // ��ɫ���ģʽ(Ĭ��ΪNO_FILL,
			// �������),
			// ��ģʽ��,
			// ǰ��ɫ�Ḳ�Ǳ���ɫ,
			// ����Ҳ���Բ����ñ���ɫ
			cellstyle_firstrow.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index); // ǰ��ɫ����������ɫ,
			// ���ݲ�ͬ��Pattern,
			// ���ֲ�ͬ�ķ��.
			cellstyle_firstrow.setFillBackgroundColor(HSSFColor.ORANGE.index); // ��ǰ��ɫ�뱳��ɫͬʱ����ʱ,
			// ǰ��ɫ�����ڱ���ɫ֮ǰ����
			cellstyle_firstrow.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			cellstyle_firstrow.setFont(font_firstrow);
			// liuxuanfei end
			if (_data != null && _data.length > 0) { // ���������
				for (int i = 0; i < _data.length; i++) {
					Row row = wsheet.createRow(i); // ��������
					for (int j = 0; j < _data[i].length; j++) {
						Cell dCell = row.createCell(j);
						dCell.setCellType(HSSFCell.CELL_TYPE_STRING); //
						dCell.setCellValue(_data[i][j] == null ? "" : _data[i][j]); // ��������..
						if (i == 0) {
							wsheet.setColumnWidth(j, (int) 36.36 * 120); // 120����
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

			fout = new FileOutputStream(_filename, false); // ����ļ���..
			wbook.write(fout); // ��������������ļ���..
			ClientEnvironment.str_downLoadFileDir = _filename.substring(0, _filename.lastIndexOf("\\") + 1); //
			return "�����������ļ���" + _filename + "���ɹ�!!"; //
		} catch (Exception e) {
			e.printStackTrace(); //
			return "��������ʧ��,ԭ����:" + e.getMessage(); //
		} finally {
			try {
				fout.close();
			} catch (Exception e) {
			}
		}
	}
	/**
	 * zzl[2018-11-15]����excel����С������λ��
	 * @param a
	 */
	public void setDecimals(int a){
		this.value=a;
	}

	public static void main(String[] _args) {
		// String[][] str_data = new String[][] { { "��һ�е�һ��", "aa2", "aa3" }, {
		// "bb1", "bb2", "bb3" }, { "cc1", "cc2", "cc3" } };
		// new ExcelUtil().setDataToExcelFile(str_data, "C:/ppp.xls"); //
		//		new ExcelUtil().getExcelFileData("C:/ddd.xls", 0);
	}
}
