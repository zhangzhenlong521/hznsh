package cn.com.infostrategy.to.report;

import java.awt.Color;
import java.awt.Container;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JTable;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;

import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.rtf.RtfWriter2;
import com.lowagie.text.rtf.style.RtfFont;

/**
 * 报表导出WORD类，根据报表中原格式进行导出，如果对BillCellItemVO对象的cellKey赋“title值”,则会被认为是标题，会被拿出表格外作为标题
 * @author YangQing/2013-10-25
 *
 */
public class ReportExportWord {
	private JTable reportTable = null;
	private BillCellVO[] cellvos;

	public ReportExportWord() {

	}

	/**
	 * 导出Word文件
	 */
	public void exportWordFile(JTable _table, String _fileName) {
		reportTable = _table;
		try {
			if (reportTable == null) {
				MessageBox.show(_table, "没有数据，不可导出.");
				return;
			}
			byte[] bytes = wordContent(); //WORD内容
			UIUtil.saveFile(_table, "doc", _fileName, bytes); //保存WORD内容
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 需要弹出对话框提示保存。
	 * @param _cellvo
	 * @param _filename
	 */
	public void exportWordFile(Container _parent, BillCellVO _cellvo, String _filename) {
		exportWordFile(_parent, new BillCellVO[] { _cellvo }, _filename);
	}

	public void exportWordFile(Container _parent, BillCellVO[] _cellvos, String _filename) {
		cellvos = _cellvos;
		try {
			if (cellvos == null || cellvos.length == 0) {
				MessageBox.show(_parent, "没有数据，不可导出.");
				return;
			}
			byte[] bytes = wordContent(); //WORD内容
			UIUtil.saveFile(_parent, "doc", _filename, bytes); //保存WORD内容
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 直接传入保存的路径。
	 * @param _cellvo
	 * @param _fileName
	 * @param _path
	 */
	public void exportWordFile(BillCellVO _cellvo, String _path, String _fileName) {
		exportWordFile(new BillCellVO[] { _cellvo }, _path, _fileName);
	}

	public void exportWordFile(BillCellVO[] _cellvos, String _path, String _fileName) {
		cellvos = _cellvos;
		try {
			byte[] bytes = wordContent();
			FileOutputStream outputStream = new FileOutputStream(_path + File.separator + _fileName + ".doc");
			TBUtil.getTBUtil().writeBytesToOutputStream(outputStream, bytes);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * WORD内容
	 * @return
	 * @throws DocumentException
	 */
	private byte[] wordContent() throws DocumentException {
		Document document = new Document(PageSize.A4, 40, 40, 40, 40);//设文档大小
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		RtfWriter2.getInstance(document, byteOutStream);// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中 
		document.open();//文档打开
		Paragraph titleStyle = null;//标题格式
		Table table_control = null;
		int rowcount = 0;
		int colcount = 0;

		for (int m = 0; m < cellvos.length; m++) {
			BillCellItemVO[][] itemvos = null;
			BillCellVO cellvo = cellvos[m];
			if (cellvos == null || cellvos.length == 0) {
				table_control = new Table(reportTable.getColumnCount());//创建table,参数3表示有3列，当add(cell)时，超了三列会自动换行
				table_control.setWidth(99);//表格宽度
				rowcount = reportTable.getRowCount();
				colcount = reportTable.getColumnCount();
			} else {
				itemvos = cellvo.getCellItemVOs();
				if (itemvos.length > 0) {
					table_control = new Table(cellvo.getCollength());//创建table,参数3表示有3列，当add(cell)时，超了三列会自动换行

					float width = 0;
					for (int i = 0; i < itemvos[0].length; i++) {
						width += Float.parseFloat(itemvos[0][i].getColwidth());
					}
					int bili[] = new int[cellvo.getCollength()];
					float cellcolwidths[] = new float[cellvo.getCollength()];
					for (int i = 0; i < cellvo.getCollength(); i++) {
						cellcolwidths[i] = Float.parseFloat(itemvos[0][i].getColwidth());
						bili[i] = (int) ((Float.parseFloat(itemvos[0][i].getColwidth()) / width) * 100);
					}
					//				table_control.setWidths(cellcolwidths);
					table_control.setWidths(bili);
					table_control.setWidth(100);
					table_control.setLocked(true);
				}
				rowcount = cellvo.getRowlength();
				colcount = cellvo.getCollength();
			}
			table_control.setPadding(5);//内边距为5
			//table_control.setWidths(new int[] { 20, 40, 40 });//设置每列所占比例 
			table_control.setAlignment(Element.ALIGN_LEFT);
			Map<Object, String> cel_span = new HashMap<Object, String>();//存放每个单元格对象,用于最后合并
			Map<String, String> location_span = new HashMap<String, String>();//记录被合并格子的位置

			Vector vc_title = new Vector();
			Vector vc_bottom = new Vector();
			for (int x = 0; x < rowcount; x++) {
				for (int y = 0; y < colcount; y++) {
					BillCellItemVO itemVO = null;
					if (cellvo == null) {
						itemVO = (BillCellItemVO) reportTable.getValueAt(x, y);
					} else {
						itemVO = itemvos[x][y];
					}
					if (itemVO == null) {
						continue;
					}
					String cellValue = itemVO.getCellvalue();//值
					String spanValue = itemVO.getSpan();//跨度值
					String font_size = itemVO.getFontsize();//字体大小
					String font_style = itemVO.getFontstyle();//字体风格
					String font_family = itemVO.getFonttype();//字体样式
					int halign = itemVO.getHalign();//水平对齐方式
					int valign = itemVO.getValign();//垂直对齐方式
					if (TBUtil.isEmpty(font_size)) {
						font_size = "12";
					}
					//******标题处理******
					String key = itemVO.getCellkey();//用以判断是否是标题的字段
					if (!TBUtil.isEmpty(key) && (key.contains("title") || key.contains("bottom"))) {//通过此认证为是标题
						Font titleFont = new Font();// 小标题字体风格 ,五号字体	
						if (font_style != null && Integer.parseInt(font_style) == 1) {//为1则加粗
							titleFont = new RtfFont(font_family, Integer.parseInt(font_size) - 1, Font.BOLD);//标题字体格式
						} else {
							titleFont = new RtfFont(font_family, Integer.parseInt(font_size) - 1, Font.NORMAL);//标题字体格式
						}
						titleStyle = new Paragraph(cellValue, titleFont);
						if (halign == 1) {//左
							titleStyle.setAlignment(Element.ALIGN_LEFT);// 设置标题格式对齐方式 
						} else if (halign == 2) {//中
							titleStyle.setAlignment(Element.ALIGN_CENTER);
						} else if (halign == 3) {//右
							titleStyle.setAlignment(Element.ALIGN_RIGHT);
						}
						if (cellvo == null) {
							y = reportTable.getColumnCount();//列循环加到最大，进入下一行的遍历(此处认为标题跨所有列，占一整行)
						} else {
							y = cellvo.getCollength();
						}
						if (titleStyle != null) {//标题格式不为空，加入标题	
							if (key.contains("title")) {
								vc_title.add(titleStyle);
							} else if (key.contains("bottom")) {
								vc_bottom.add(titleStyle);
							}
						}
						continue;
					}

					Cell cel = new Cell();
					cel.setLeading(100f);
					//******内容字体部分处理******
					Font fontstyle = new Font();
					if (!TBUtil.isEmpty(font_style) && !TBUtil.isEmpty(font_size)) {
						if (Integer.parseInt(font_style) == 1) {//如果字体风格为1时，为粗字体
							fontstyle = new RtfFont(font_family, Integer.parseInt(font_size), Font.BOLD);
						} else {
							fontstyle = new RtfFont(font_family, Integer.parseInt(font_size), Font.NORMAL);
						}
					} else {
						fontstyle = new RtfFont("宋_体", 12, Font.NORMAL);
					}
					//******字体颜色******
					String fontColor = itemVO.getForeground();
					if ((!TBUtil.isEmpty(fontColor)) && fontColor.contains(",")) {
						String[] split_fontcolor = fontColor.split(",");
						if (split_fontcolor.length == 3) {
							int col1 = Integer.parseInt(split_fontcolor[0]);
							int col2 = Integer.parseInt(split_fontcolor[1]);
							int col3 = Integer.parseInt(split_fontcolor[2]);
							fontstyle.setColor(new Color(col1, col2, col3));//为字体赋上自己的颜色
						}
					}
					cel.addElement(new Phrase(cellValue, fontstyle));//格子加入格式后的内容
					//******格子内容对齐方式处理******
					if (halign == 1) {//左
						cel.setHorizontalAlignment(cel.ALIGN_LEFT);
					} else if (halign == 2) {//中
						cel.setHorizontalAlignment(cel.ALIGN_CENTER);
					} else if (halign == 3) {//右
						cel.setHorizontalAlignment(cel.ALIGN_RIGHT);
					}
					if (valign == 1) {//垂直上
						cel.setVerticalAlignment(cel.ALIGN_TOP);
					} else if (valign == 2) {//中间
						cel.setVerticalAlignment(cel.ALIGN_MIDDLE);
					} else if (valign == 3) {//下
						cel.setVerticalAlignment(cel.ALIGN_BOTTOM);
					}

					//******格子背景色处理******
					String itemColor = itemVO.getBackground();
					int[] colorNum = new int[] { 255, 255, 255 };//默认白色
					if (!TBUtil.isEmpty(itemColor) && itemColor.contains(",")) {//背景色不为空,且格式对
						String[] split_color = itemColor.trim().split(",");
						if (split_color.length == 3) {
							colorNum[0] = Integer.parseInt(split_color[0]);
							colorNum[1] = Integer.parseInt(split_color[1]);
							colorNum[2] = Integer.parseInt(split_color[2]);
						}
					}
					cel.setBackgroundColor(new Color(colorNum[0], colorNum[1], colorNum[2]));//赋背景颜色
					//******合并与被合并处理******
					if (!TBUtil.isEmpty(spanValue) && spanValue.contains(",")) {//不为空且符合格式
						String[] split_span = spanValue.trim().split(",");
						if (split_span.length == 2) {
							int rowspan = Integer.parseInt(split_span[0]);
							int colspan = Integer.parseInt(split_span[1]);
							if ((rowspan > 1 || colspan > 1) && !location_span.containsKey(x + "," + y)) {//说明需要合并,且不属于被合并范围
								cel_span.put(cel, spanValue);//加入要合并的单元格对象
								for (int a = 0; a < rowspan; a++) {
									for (int b = 0; b < colspan; b++) {
										int location_x = x + a;
										int location_y = y + b;
										if (!(a == 0 && b == 0)) {//合并的单元格除外,只记录【被合并】的单元格
											location_span.put(location_x + "," + location_y, "");
										}
									}
								}
							}
						}
					}
					cel.rotate();
					table_control.addCell(cel);
				}
			}
			//合并单元格
			for (Map.Entry<Object, String> entry : cel_span.entrySet()) {
				String spanValue = entry.getValue();//值
				if (!TBUtil.isEmpty(spanValue) && spanValue.contains(",")) {
					String[] split_span = spanValue.trim().split(",");
					if (split_span.length == 2) {
						int rowspan = Integer.parseInt(split_span[0]);
						int colspan = Integer.parseInt(split_span[1]);
						Cell obj_cel = (Cell) entry.getKey();
						if (rowspan > 1) {//如果大于1，说明要合并行
							obj_cel.setRowspan(rowspan);
						}
						if (colspan > 1) {//如果大于1，说明要合并列
							obj_cel.setColspan(colspan);
						}
					}
				}
			}
			for (int i = 0; i < vc_title.size(); i++) {
				document.add((Paragraph) vc_title.get(i));
			}
			document.add(table_control);//加入table
			for (int i = 0; i < vc_bottom.size(); i++) {
				if (vc_bottom.get(i) instanceof Paragraph) {
					document.add((Paragraph) vc_bottom.get(i));
				}
			}
			if (m >= 0 && m != cellvos.length - 1) {
				document.newPage();
			}
		}
		document.close();//关闭
		return byteOutStream.toByteArray();
	}
}
