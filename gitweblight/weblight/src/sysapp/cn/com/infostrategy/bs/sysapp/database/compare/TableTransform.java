package cn.com.infostrategy.bs.sysapp.database.compare;

import java.io.FileOutputStream;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;

/**
 * 
 * @author Administrator
 *
 */
public class TableTransform {

	/**
	 * 本类的内部属性
	 */
	private BillCellVO[] billCellVO = null;
	private BillCellItemVO[][] billCellItemVos = null;
	private HSSFSheet sheet = null;
	private HSSFCellStyle style = null;
	private HSSFFont font = null;
	private HSSFRow row = null; //定义行	
	private HSSFCell cell = null; //定义列
	private String unReport = "未上报";
	private String report = "已上报";
	private String noProject = "无任务";
	private String no = "否";
	private String print = "打印";
	private TBUtil tbutil = new TBUtil();
	/**
	 * 构造方法
	 * @param billCellVO
	 */
	public TableTransform(BillCellVO[] billCellVO) {
		this.billCellVO = billCellVO;
	}

	public TableTransform() {
		this.billCellVO = null;
	}

	/**
	 * 
	 * @param billCellVO
	 * @return String 表格代码
	 */
	private String getTableFile(BillCellVO billCellVO) {
		StringBuffer returnHtml = null;
		StringBuffer bgcolor = null;
		int[] tableSpan = null;
		billCellItemVos = billCellVO.getCellItemVOs();
		int row = 0;
		int column = 0;

		row = billCellVO.getRowlength();
		column = billCellVO.getCollength();
		bgcolor = new StringBuffer();
		returnHtml = new StringBuffer();
		returnHtml.append("<table width=\"100%\" align=\"center\" style=\"margin-left: 0px; margin-right: 0px; margin-top: 3px; margin-bottom: 8px\" cellSpacing=\"0\" cellPadding=\"5\" bgColor=\"#999999\" border=\"1\" bordercolor=\"#CCCCCC\" style=\"word-break:break-all\">\r\n");
		for (int i = 0; i < row; i++) {
			returnHtml.append("<tr height=\"" + billCellItemVos[i][0].getRowheight() + "\">\r\n");
			for (int j = 0; j < column; j++) {
				tableSpan = toSpan(billCellItemVos[i][j].getSpan());

				if (tableSpan == null || tableSpan[0] < 1 || tableSpan[1] < 1) {
					continue;
				}
				if (tableSpan[0] == 1 && tableSpan[1] == 1) {
					bgcolor.delete(0, bgcolor.length());
					if (billCellItemVos[i][j].getBackground() == null) {
						bgcolor.append("#FFFFFF");
					} else {
						bgcolor.append("#");
						bgcolor.append(getColor(billCellItemVos[i][j].getBackground().split(",")[0]));
						bgcolor.append(getColor(billCellItemVos[i][j].getBackground().split(",")[1]));
						bgcolor.append(getColor(billCellItemVos[i][j].getBackground().split(",")[2]));
					}
					returnHtml.append("<td bgColor=\"" + bgcolor.toString() + "\" class=\"style_1\" width=\"" + billCellItemVos[i][j].getColwidth() + "\" align=\"center\" valign=\"middle\">\r\n");
					if (billCellItemVos[i][j] == null || billCellItemVos[i][j].getCellvalue() == null || billCellItemVos[i][j].getCellvalue().toString().equals("")) {
						returnHtml.append("&nbsp;");
					} else {
						returnHtml.append(billCellItemVos[i][j].getCellvalue().toString());
					}
					returnHtml.append("</td>\r\n");
				}
				if (tableSpan[0] > 1 || tableSpan[1] > 1) {
					bgcolor.delete(0, bgcolor.length());
					if (billCellItemVos[i][j].getBackground() == null) {
						bgcolor.append("#FFFFFF");
					} else {
						bgcolor.append("#");
						bgcolor.append(getColor(billCellItemVos[i][j].getBackground().split(",")[0]));
						bgcolor.append(getColor(billCellItemVos[i][j].getBackground().split(",")[1]));
						bgcolor.append(getColor(billCellItemVos[i][j].getBackground().split(",")[2]));
					}
					returnHtml.append("<td bgColor=\"" + bgcolor.toString() + "\" class=\"style_1\" valign=\"middle\" align=\"center\" rowspan=\"" + tableSpan[0] + "\" colspan=\"" + tableSpan[1] + "\">\r\n");
					if (billCellItemVos[i][j] == null || billCellItemVos[i][j].getCellvalue() == null || billCellItemVos[i][j].getCellvalue().toString().equals("")) {
						returnHtml.append("&nbsp;");
					} else {
						returnHtml.append(billCellItemVos[i][j].getCellvalue().toString());
					}
					returnHtml.append("</td>\r\n");
				}
			}
			returnHtml.append("</tr>\r\n");
		}
		returnHtml.append("</table>\r\n");

		return returnHtml.toString();
	}

	public void getExcelFile(HashMap<String, String> map) {
		FileOutputStream output = null;
		HSSFWorkbook wb = null;

		//处理EXCEL文件的数据
		try {
			output = new FileOutputStream(map.get("fileName").toString());
			wb = new HSSFWorkbook(); //打开EXCEL面板
			for (int i = 0, j = 0; i < billCellVO.length; i++, j++) {
				if (map.get("sheet" + i) != null) {
					getSheet(billCellVO[i], wb, map.get("sheet" + i).toString(), j);
				}
			}
			wb.write(output);
			output.flush();
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getSheet(BillCellVO billCellVO, HSSFWorkbook wb, String sheetName, int sheetIndex) {
		int[] cellSpan = null;
		billCellItemVos = billCellVO.getCellItemVOs();
		HashMap<String, String> map = new HashMap<String, String>();
		sheet = wb.createSheet(sheetName); //创建一个面板
		//wb.setSheetName(sheetIndex, sheetName, (short) 1);
		wb.setSheetName(sheetIndex, sheetName);
		map.put("fontName", "宋体");
		map.put("boldweight", "1");
		map.put("color", "8");
		map.put("fontSize", "10");
		HSSFCellStyle styleTitle = getStyle(wb, map);
		map.remove("boldweight");
		HSSFCellStyle styleUnBold = getStyle(wb, map);
		map.remove("color");
		map.put("color", "10");
		HSSFCellStyle styleRed = getStyle(wb, map);
		map.remove("color");
		map.put("color", "12");
		HSSFCellStyle styleBlue = getStyle(wb, map);
		map.remove("color");
		map.put("color", "8");
		map.put("boldweight", "1");
		map.put("alignment", "1");
		HSSFCellStyle styleLeft = getStyle(wb, map);

		//遍历整个表格，取出数据
		for (int i = 0; i < billCellVO.getRowlength(); i++) {

			row = sheet.createRow(i);
			//设置行高
			row.setHeight((short) (Short.valueOf(billCellItemVos[i][0].getRowheight()) * 20));
			for (int j = 0; j < billCellVO.getCollength(); j++) {
				cell = row.createCell((short) j); //创建单元格
				//cell.setEncoding((short) 1); //解决中文编码问题

				if (i == 0) {
					//设置列宽
					sheet.setColumnWidth((short) j, (short) (Short.valueOf(billCellItemVos[0][j].getColwidth()) * 50));
				}
				if (billCellItemVos[i][j].getCellvalue() != null) {
					cell.setCellValue(billCellItemVos[i][j].getCellvalue().toString()); //表格进行赋值
				} else {
					cell.setCellValue("");
				}

				//返回单元格的合并数据
				cellSpan = toSpan(billCellItemVos[i][j].getSpan());
				//返回合并单元格错误
				if (cellSpan == null || cellSpan.length == 0 || cellSpan.length != 2) {
					return;
				}
				if (cellSpan[0] > 1 || cellSpan[1] > 1) {
					initExcelSpan(sheet, i, j, i + cellSpan[0] - 1, j + cellSpan[1] - 1);
				}
				/**
				 * 以下是EXCEL文件的显示风格的初始化：
				 * 背景颜色
				 * 字体样式
				 * 字体颜色
				 * 边框颜色
				 * 等等....
				 * ----------------------------------------------------------------------------------------------------------
				 */
				if (billCellItemVos[i][j] != null) {

					//style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
					//显示风格为大点显示样式
					//style.setFillPattern(HSSFCellStyle.BIG_SPOTS);
					//设置前景颜色
					//style.setFillForegroundColor(HSSFColor.RED.index);
					//style.setFillPattern((short)3);
					//style.setTopBorderColor(HSSFColor.YELLOW.index);  
					if (i == 0 && j == 0) {
						cell.setCellStyle(styleLeft);
					} else if (i == 1 || j == 0) {
						cell.setCellStyle(styleTitle);
					} else if (cell.getStringCellValue().equals(unReport)) {
						cell.setCellStyle(styleRed);
					} else if (cell.getStringCellValue().equals(report)) {
						cell.setCellStyle(styleBlue);
					} else if (cell.getStringCellValue().equals(noProject)) {
						cell.setCellStyle(styleUnBold);
					} else if (cell.getStringCellValue().equals(no)) {
						cell.setCellStyle(styleRed);
					} else {
						cell.setCellStyle(styleUnBold);
					}
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				}
				/**
				 * 以上初始化完毕。--------------------------------------------------------------------
				 */
			}
		}
	}

	private HSSFCellStyle getStyle(HSSFWorkbook workBook, HashMap<String, String> map) {
		font = workBook.createFont();
		style = workBook.createCellStyle();
		if (map.get("fontName") != null) {
			font.setFontName(map.get("fontName").toString());
		}
		if (map.get("boldweight") != null) {
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		}
		if (map.get("color") != null) {

			switch (Short.valueOf(map.get("color").toString())) {
			case HSSFColor.BLACK.index:
				font.setColor(HSSFColor.BLACK.index);
				break;
			case HSSFColor.RED.index:
				font.setColor(HSSFColor.RED.index);
				break;
			case HSSFColor.BLUE.index:
				font.setColor(HSSFColor.BLUE.index);
				break;
			default:
				font.setColor(HSSFColor.BLACK.index);
			}
		}
		if (map.get("fontSize") != null) {
			font.setFontHeightInPoints(Short.valueOf(map.get("fontSize").toString()));
		}
		style.setBorderTop((short) 1);
		style.setBorderRight((short) 1);
		style.setBorderBottom((short) 1);
		style.setBorderLeft((short) 1);
		if (map.get("alignment") == null) {
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		} else {
			style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		}
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		style.setFont(font);
		return style;
	}

	/**
	 * 生成网页源代码
	 * @return 网页源文件
	 */
	public String getHtmlFile(String title) {
		StringBuffer returnHtml = null;
		returnHtml = new StringBuffer();
		returnHtml.append("<html><head><title>" + title + "</title>\r\n");
		returnHtml.append("<style type=\"text/css\">\r\n");
		returnHtml.append("table{\r\n");
		returnHtml.append("border-collapse:collapse;\r\n");
		returnHtml.append("}\r\n");
		returnHtml.append(".style_1 {\r\n");
		returnHtml.append(" font-size: 12px; color: #333333; line-height: 18px; font-family: 宋体\r\n");
		returnHtml.append("}\r\n");
		returnHtml.append("</style>\r\n");
		returnHtml.append("</head>\r\n");
		returnHtml.append("<script type=\"text/javascript\" language=\"javascript1.4\">\r\n");
		returnHtml.append("function doprint(){\r\n");
		returnHtml.append("window.print();\r\n");
		returnHtml.append("}\r\n");
		returnHtml.append("</script>\r\n");
		returnHtml.append("<body>\r\n");
		returnHtml.append("<input type=\"button\" onClick=\"doprint();\" value=\"" + print + "\"/>\r\n");
		returnHtml.append("<OBJECT classid=\"CLSID:8856F961-340A-11D0-A96B-00C04FD705A2\" height=0 id=pushworld name=pushworld width=3></OBJECT>");
		returnHtml.append("<br><center><strong><font color=\"blue\">");
		returnHtml.append(title);
		returnHtml.append("</font></strong><center><br>\r\n");
		returnHtml.append("<table align=\"center\" style=\"margin-left: 0px; margin-right: 0px; margin-top: 0px; margin-bottom: 0px\" cellSpacing=\"0\" cellPadding=\"0\" bgColor=\"#999999\" border=\"1\" bordercolor=\"#CCCCCC\" style=\"word-break:break-all\">\r\n");

		for (int i = 0; i < billCellVO.length; i++) {
			returnHtml.append("<tr><td class=\"style_1\" align=\"center\" valign=\"middle\">\r\n");
			returnHtml.append(getTableFile(billCellVO[i]) + "\r\n");
			returnHtml.append("</td></tr>\r\n");
		}

		returnHtml.append("</table>\r\n");
		returnHtml.append("</body></html>\r\n");
		return returnHtml.toString();
	}

	/**
	 * 合并单元格字符串转换为一维数组
	 * @param span String
	 * @return 整形[2]
	 */
	private int[] toSpan(String span) {
		String[] splitString = null;
		int[] returnResult = new int[] { 1, 1 };
		if (span != null) {
			splitString = span.split(",");
			returnResult[0] = Integer.parseInt(splitString[0].toString());
			returnResult[1] = Integer.parseInt(splitString[1].toString());
		}
		return returnResult;
	}

	/**
	 * 武坤萌
	 * 合并单元格函数
	 * @param sheet
	 * @param row
	 * @param column
	 * @param row_sum
	 * @param column_sum
	 */
	private void initExcelSpan(HSSFSheet sheet, int row, int column, int row_sum, int column_sum) {
//		由于版本升级太快，为了能适应新POI，做如下修改
//		Region region = new Region(row, (short) column, row_sum, (short) column_sum);
		CellRangeAddress region = new CellRangeAddress(row, column, row_sum, column_sum);
		sheet.addMergedRegion(region);
	}

	/**
	 * 武坤萌
	 * 只得到html表格
	 * @return table
	 * @throws Exception
	 */
	public String getHtmlTableForBillCellVO(BillCellVO myBillCell) throws Exception {
		StringBuffer returnHtml = null;
		StringBuffer bgcolor = null;
		StringBuffer forecolor = null;
		String halign = null;
		String valign = null;
		int[] tableSpan = null;
		int row = 0;
		int column = 0;

		row = myBillCell.getRowlength();
		column = myBillCell.getCollength();
		billCellItemVos = myBillCell.getCellItemVOs();
		bgcolor = new StringBuffer();
		forecolor = new StringBuffer();
		returnHtml = new StringBuffer();

		returnHtml.append("<table width=\"100%\" align=\"center\" style=\" margin-left: 0px; margin-right: 0px; margin-top: 3px; margin-bottom: 8px\" cellSpacing=\"1\" cellPadding=\"5\" bgColor=\"#999999\" border=\"0\" bordercolor=\"#CCCCCC\" style=\"word-break:break-all\">\r\n");
		for (int i = 0; i < row; i++) {
			returnHtml.append("<tr height=\"" + billCellItemVos[i][0].getRowheight() + "\">\r\n");
			for (int j = 0; j < column; j++) {
				tableSpan = toSpan(billCellItemVos[i][j].getSpan());
				if (tableSpan == null || tableSpan[0] < 1 || tableSpan[1] < 1) {
					continue;
				}
				if (tableSpan[0] == 1 && tableSpan[1] == 1) {
					bgcolor.delete(0, bgcolor.length());
					if (billCellItemVos[i][j].getBackground() == null) {
						bgcolor.append("#FFFFFF");
					} else {
						bgcolor.append("#");
						bgcolor.append(getColor(billCellItemVos[i][j].getBackground().split(",")[0]));
						bgcolor.append(getColor(billCellItemVos[i][j].getBackground().split(",")[1]));
						bgcolor.append(getColor(billCellItemVos[i][j].getBackground().split(",")[2]));
					}
					forecolor.delete(0, forecolor.length());
					if (billCellItemVos[i][j].getForeground() == null) {
						forecolor.append("#000000");
					} else {
						forecolor.append("#");
						forecolor.append(getColor(billCellItemVos[i][j].getForeground().split(",")[0]));
						forecolor.append(getColor(billCellItemVos[i][j].getForeground().split(",")[1]));
						forecolor.append(getColor(billCellItemVos[i][j].getForeground().split(",")[2]));
					}
					if (billCellItemVos[i][j].getValign() == 1) {
						valign = "top";
					} else if (billCellItemVos[i][j].getValign() == 2) {
						valign = "middle";
					} else if (billCellItemVos[i][j].getValign() == 3) {
						valign = "bottom";
					}
					if (billCellItemVos[i][j].getHalign() == 1) {
						halign = "left";
					} else if (billCellItemVos[i][j].getHalign() == 2) {
						halign = "center";
					} else if (billCellItemVos[i][j].getHalign() == 3) {
						halign = "right";
					}
					returnHtml.append("<td bgColor=\"" + bgcolor.toString() + "\" class=\"style_1\" width=\"" + billCellItemVos[i][j].getColwidth() + "\" align=\"" + halign + "\" valign=\"" + valign + "\">");
					if (billCellItemVos[i][j] == null || billCellItemVos[i][j].getCellvalue() == null || billCellItemVos[i][j].getCellvalue().toString().equals("")) {
						returnHtml.append("&nbsp;");
					} else {
						returnHtml.append("<font color=\"" + forecolor.toString() + "\">" + billCellItemVos[i][j].getCellvalue().toString() + "</font>");
					}
					returnHtml.append("</td>\r\n");
				}
				if (tableSpan[0] > 1 || tableSpan[1] > 1) {
					bgcolor.delete(0, bgcolor.length());
					if (billCellItemVos[i][j].getBackground() == null) {
						bgcolor.append("#FFFFFF");
					} else {
						bgcolor.append("#");
						bgcolor.append(getColor(billCellItemVos[i][j].getBackground().split(",")[0]));
						bgcolor.append(getColor(billCellItemVos[i][j].getBackground().split(",")[1]));
						bgcolor.append(getColor(billCellItemVos[i][j].getBackground().split(",")[2]));
					}
					forecolor.delete(0, forecolor.length());
					if (billCellItemVos[i][j].getForeground() == null) {
						forecolor.append("#000000");
					} else {
						forecolor.append("#");
						forecolor.append(getColor(billCellItemVos[i][j].getForeground().split(",")[0]));
						forecolor.append(getColor(billCellItemVos[i][j].getForeground().split(",")[1]));
						forecolor.append(getColor(billCellItemVos[i][j].getForeground().split(",")[2]));
					}
					if (billCellItemVos[i][j].getValign() == 1) {
						valign = "top";
					} else if (billCellItemVos[i][j].getValign() == 2) {
						valign = "middle";
					} else if (billCellItemVos[i][j].getValign() == 3) {
						valign = "bottom";
					}
					if (billCellItemVos[i][j].getHalign() == 1) {
						halign = "left";
					} else if (billCellItemVos[i][j].getHalign() == 2) {
						halign = "center";
					} else if (billCellItemVos[i][j].getHalign() == 3) {
						halign = "right";
					}
					returnHtml.append("<td bgColor=\"" + bgcolor.toString() + "\" class=\"style_1\" valign=\"" + valign + "\" align=\"" + halign + "\" rowspan=\"" + tableSpan[0] + "\" colspan=\"" + tableSpan[1] + "\">\r\n");
					if (billCellItemVos[i][j] == null || billCellItemVos[i][j].getCellvalue() == null || billCellItemVos[i][j].getCellvalue().toString().equals("")) {
						returnHtml.append("&nbsp;");
					} else {
						returnHtml.append("<font color=\"" + forecolor.toString() + "\">" + billCellItemVos[i][j].getCellvalue().toString() + "</font>");
					}
					returnHtml.append("</td>\r\n");
				}
			}
			returnHtml.append("</tr>\r\n");
		}
		returnHtml.append("</table>\r\n");
		return returnHtml.toString();
	}

	public String getHtmlTableForBillListVO(String[] names, String[][] data) throws Exception {
		StringBuffer html = new StringBuffer();

		html.append("<table width=\"100%\" align=\"center\" style=\" margin-left: 0px; margin-right: 0px; margin-top: 3px; margin-bottom: 8px\" cellSpacing=\"1\" cellPadding=\"5\" bgColor=\"#999999\" border=\"0\" bordercolor=\"#CCCCCC\" style=\"word-break:break-all\">\r\n");
		html.append("<tr height=\"20\">");
		for (int i = 0; i < names.length; i++) {
			html.append("<td align=\"center\" bgColor=\"#EEEEEE\" class=\"style_1\">");
			html.append(names[i]);
			html.append("</td>");
		}
		html.append("</tr>");
		for (int i = 0; i < data.length; i++) {
			html.append("<tr height=\"20\">");
			for (int j = 0; j < names.length; j++) {
				html.append("<td align=\"center\" bgColor=\"#FFFFFF\" class=\"style_1\">");
				html.append(data[i][j]);
				html.append("</td>");
			}
			html.append("</tr>");
		}
		html.append("</table>");
		return html.toString();
	}
	private String getColor(String _in){
		if(tbutil.isStrAllNunbers(_in)){
			return Integer.toHexString(Integer.parseInt((_in)));
		}
		return _in;
	}
}
