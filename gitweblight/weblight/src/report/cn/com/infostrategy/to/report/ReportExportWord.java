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
 * ������WORD�࣬���ݱ�����ԭ��ʽ���е����������BillCellItemVO�����cellKey����titleֵ��,��ᱻ��Ϊ�Ǳ��⣬�ᱻ�ó��������Ϊ����
 * @author YangQing/2013-10-25
 *
 */
public class ReportExportWord {
	private JTable reportTable = null;
	private BillCellVO[] cellvos;

	public ReportExportWord() {

	}

	/**
	 * ����Word�ļ�
	 */
	public void exportWordFile(JTable _table, String _fileName) {
		reportTable = _table;
		try {
			if (reportTable == null) {
				MessageBox.show(_table, "û�����ݣ����ɵ���.");
				return;
			}
			byte[] bytes = wordContent(); //WORD����
			UIUtil.saveFile(_table, "doc", _fileName, bytes); //����WORD����
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * ��Ҫ�����Ի�����ʾ���档
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
				MessageBox.show(_parent, "û�����ݣ����ɵ���.");
				return;
			}
			byte[] bytes = wordContent(); //WORD����
			UIUtil.saveFile(_parent, "doc", _filename, bytes); //����WORD����
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * ֱ�Ӵ��뱣���·����
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
	 * WORD����
	 * @return
	 * @throws DocumentException
	 */
	private byte[] wordContent() throws DocumentException {
		Document document = new Document(PageSize.A4, 40, 40, 40, 40);//���ĵ���С
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		RtfWriter2.getInstance(document, byteOutStream);// ����һ����д��(Writer)��document���������ͨ����д��(Writer)���Խ��ĵ�д�뵽������ 
		document.open();//�ĵ���
		Paragraph titleStyle = null;//�����ʽ
		Table table_control = null;
		int rowcount = 0;
		int colcount = 0;

		for (int m = 0; m < cellvos.length; m++) {
			BillCellItemVO[][] itemvos = null;
			BillCellVO cellvo = cellvos[m];
			if (cellvos == null || cellvos.length == 0) {
				table_control = new Table(reportTable.getColumnCount());//����table,����3��ʾ��3�У���add(cell)ʱ���������л��Զ�����
				table_control.setWidth(99);//�����
				rowcount = reportTable.getRowCount();
				colcount = reportTable.getColumnCount();
			} else {
				itemvos = cellvo.getCellItemVOs();
				if (itemvos.length > 0) {
					table_control = new Table(cellvo.getCollength());//����table,����3��ʾ��3�У���add(cell)ʱ���������л��Զ�����

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
			table_control.setPadding(5);//�ڱ߾�Ϊ5
			//table_control.setWidths(new int[] { 20, 40, 40 });//����ÿ����ռ���� 
			table_control.setAlignment(Element.ALIGN_LEFT);
			Map<Object, String> cel_span = new HashMap<Object, String>();//���ÿ����Ԫ�����,�������ϲ�
			Map<String, String> location_span = new HashMap<String, String>();//��¼���ϲ����ӵ�λ��

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
					String cellValue = itemVO.getCellvalue();//ֵ
					String spanValue = itemVO.getSpan();//���ֵ
					String font_size = itemVO.getFontsize();//�����С
					String font_style = itemVO.getFontstyle();//������
					String font_family = itemVO.getFonttype();//������ʽ
					int halign = itemVO.getHalign();//ˮƽ���뷽ʽ
					int valign = itemVO.getValign();//��ֱ���뷽ʽ
					if (TBUtil.isEmpty(font_size)) {
						font_size = "12";
					}
					//******���⴦��******
					String key = itemVO.getCellkey();//�����ж��Ƿ��Ǳ�����ֶ�
					if (!TBUtil.isEmpty(key) && (key.contains("title") || key.contains("bottom"))) {//ͨ������֤Ϊ�Ǳ���
						Font titleFont = new Font();// С���������� ,�������	
						if (font_style != null && Integer.parseInt(font_style) == 1) {//Ϊ1��Ӵ�
							titleFont = new RtfFont(font_family, Integer.parseInt(font_size) - 1, Font.BOLD);//���������ʽ
						} else {
							titleFont = new RtfFont(font_family, Integer.parseInt(font_size) - 1, Font.NORMAL);//���������ʽ
						}
						titleStyle = new Paragraph(cellValue, titleFont);
						if (halign == 1) {//��
							titleStyle.setAlignment(Element.ALIGN_LEFT);// ���ñ����ʽ���뷽ʽ 
						} else if (halign == 2) {//��
							titleStyle.setAlignment(Element.ALIGN_CENTER);
						} else if (halign == 3) {//��
							titleStyle.setAlignment(Element.ALIGN_RIGHT);
						}
						if (cellvo == null) {
							y = reportTable.getColumnCount();//��ѭ���ӵ���󣬽�����һ�еı���(�˴���Ϊ����������У�ռһ����)
						} else {
							y = cellvo.getCollength();
						}
						if (titleStyle != null) {//�����ʽ��Ϊ�գ��������	
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
					//******�������岿�ִ���******
					Font fontstyle = new Font();
					if (!TBUtil.isEmpty(font_style) && !TBUtil.isEmpty(font_size)) {
						if (Integer.parseInt(font_style) == 1) {//���������Ϊ1ʱ��Ϊ������
							fontstyle = new RtfFont(font_family, Integer.parseInt(font_size), Font.BOLD);
						} else {
							fontstyle = new RtfFont(font_family, Integer.parseInt(font_size), Font.NORMAL);
						}
					} else {
						fontstyle = new RtfFont("��_��", 12, Font.NORMAL);
					}
					//******������ɫ******
					String fontColor = itemVO.getForeground();
					if ((!TBUtil.isEmpty(fontColor)) && fontColor.contains(",")) {
						String[] split_fontcolor = fontColor.split(",");
						if (split_fontcolor.length == 3) {
							int col1 = Integer.parseInt(split_fontcolor[0]);
							int col2 = Integer.parseInt(split_fontcolor[1]);
							int col3 = Integer.parseInt(split_fontcolor[2]);
							fontstyle.setColor(new Color(col1, col2, col3));//Ϊ���帳���Լ�����ɫ
						}
					}
					cel.addElement(new Phrase(cellValue, fontstyle));//���Ӽ����ʽ�������
					//******�������ݶ��뷽ʽ����******
					if (halign == 1) {//��
						cel.setHorizontalAlignment(cel.ALIGN_LEFT);
					} else if (halign == 2) {//��
						cel.setHorizontalAlignment(cel.ALIGN_CENTER);
					} else if (halign == 3) {//��
						cel.setHorizontalAlignment(cel.ALIGN_RIGHT);
					}
					if (valign == 1) {//��ֱ��
						cel.setVerticalAlignment(cel.ALIGN_TOP);
					} else if (valign == 2) {//�м�
						cel.setVerticalAlignment(cel.ALIGN_MIDDLE);
					} else if (valign == 3) {//��
						cel.setVerticalAlignment(cel.ALIGN_BOTTOM);
					}

					//******���ӱ���ɫ����******
					String itemColor = itemVO.getBackground();
					int[] colorNum = new int[] { 255, 255, 255 };//Ĭ�ϰ�ɫ
					if (!TBUtil.isEmpty(itemColor) && itemColor.contains(",")) {//����ɫ��Ϊ��,�Ҹ�ʽ��
						String[] split_color = itemColor.trim().split(",");
						if (split_color.length == 3) {
							colorNum[0] = Integer.parseInt(split_color[0]);
							colorNum[1] = Integer.parseInt(split_color[1]);
							colorNum[2] = Integer.parseInt(split_color[2]);
						}
					}
					cel.setBackgroundColor(new Color(colorNum[0], colorNum[1], colorNum[2]));//��������ɫ
					//******�ϲ��뱻�ϲ�����******
					if (!TBUtil.isEmpty(spanValue) && spanValue.contains(",")) {//��Ϊ���ҷ��ϸ�ʽ
						String[] split_span = spanValue.trim().split(",");
						if (split_span.length == 2) {
							int rowspan = Integer.parseInt(split_span[0]);
							int colspan = Integer.parseInt(split_span[1]);
							if ((rowspan > 1 || colspan > 1) && !location_span.containsKey(x + "," + y)) {//˵����Ҫ�ϲ�,�Ҳ����ڱ��ϲ���Χ
								cel_span.put(cel, spanValue);//����Ҫ�ϲ��ĵ�Ԫ�����
								for (int a = 0; a < rowspan; a++) {
									for (int b = 0; b < colspan; b++) {
										int location_x = x + a;
										int location_y = y + b;
										if (!(a == 0 && b == 0)) {//�ϲ��ĵ�Ԫ�����,ֻ��¼�����ϲ����ĵ�Ԫ��
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
			//�ϲ���Ԫ��
			for (Map.Entry<Object, String> entry : cel_span.entrySet()) {
				String spanValue = entry.getValue();//ֵ
				if (!TBUtil.isEmpty(spanValue) && spanValue.contains(",")) {
					String[] split_span = spanValue.trim().split(",");
					if (split_span.length == 2) {
						int rowspan = Integer.parseInt(split_span[0]);
						int colspan = Integer.parseInt(split_span[1]);
						Cell obj_cel = (Cell) entry.getKey();
						if (rowspan > 1) {//�������1��˵��Ҫ�ϲ���
							obj_cel.setRowspan(rowspan);
						}
						if (colspan > 1) {//�������1��˵��Ҫ�ϲ���
							obj_cel.setColspan(colspan);
						}
					}
				}
			}
			for (int i = 0; i < vc_title.size(); i++) {
				document.add((Paragraph) vc_title.get(i));
			}
			document.add(table_control);//����table
			for (int i = 0; i < vc_bottom.size(); i++) {
				if (vc_bottom.get(i) instanceof Paragraph) {
					document.add((Paragraph) vc_bottom.get(i));
				}
			}
			if (m >= 0 && m != cellvos.length - 1) {
				document.newPage();
			}
		}
		document.close();//�ر�
		return byteOutStream.toByteArray();
	}
}
