package cn.com.pushworld.salary.ui.word;

import java.awt.Color;

import cn.com.infostrategy.ui.common.MessageBox;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComFailException;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * ����word���� ����jacob
 * 
 * @author hm
 */
public class WordUtil extends Dispatch {
	// ����word�ĵ�
	private Dispatch doc = null;
	// wordӦ�ó���
	private ActiveXComponent word = null;
	// ���λ�� �����
	private Dispatch select = null;
	// �����ĵ�����
	private Dispatch documents = null;

	private Dispatch pageSetup = null;

	private Dispatch alignment = null;

	public static final String TYPE_CM = "����";
	public static final String TYPE_PIX = "����";
	public static final int ALIGNMENT_LEFT_OR_TOP = 0;
	public static final int ALIGNMENT_CENTER = 1;
	public static final int ALIGNMENT_RIGHT_OR_BOTTOM = 2;
	public static final int PAGESET_ORIETATION_ENDWISE = 0; // ����
	public static final int PAGESET_ORIETATION_CROSSWISE = 1; // ����

	public WordUtil() throws Exception {
		word = new ActiveXComponent("Word.Application");
		word.setProperty("Visible", new Variant(false)); // ���ú�ִ̨��
		documents = word.getProperty("Documents").toDispatch();
	}

	/**
	 * �������ĵ�
	 */
	public void createNewWordDocument() {
		doc = Dispatch.call(documents, "Add").toDispatch();
		select = Dispatch.get(word, "Selection").toDispatch();
	}

	/**
	 * ���ĵ�
	 * 
	 * @param filePath
	 */
	public void openWordDocument(String filePath) {
		doc = Dispatch.call(documents, "Open", filePath).toDispatch();
		select = Dispatch.get(word, "Selection").toDispatch();
	}

	/**
	 * �����ĵ�
	 */
	public void saveDocument(String savePath) {
		try {
			if (savePath == null) {
				Dispatch.call(doc, "Save");
				return;
			}
			Dispatch.call((Dispatch) Dispatch.call(word, "WordBasic").getDispatch(), "FileSaveAs", savePath);
		} catch (ComFailException com) {
			com.printStackTrace();
		} catch (Exception ex) {
			MessageBox.showException(null, ex);
		}
	}

	/**
	 * ����� XML �ļ�
	 * 
	 * @param path
	 */
	public void saveXMLDocument(String path) {
		Dispatch.invoke(doc, "SaveAs", Dispatch.Method, new Object[] { path, new Variant(11) }, new int[1]);

	}

	/**
	 * ����� HTML �ļ�
	 * 
	 * @param path
	 */
	public void saveHTMLDocument(String path) {
		Dispatch.invoke(doc, "SaveAs", Dispatch.Method, new Object[] { path, new Variant(8) }, new int[1]);

	}

	/**
	 * ������ row col �Զ���
	 */
	public void createNewTable(int rownum, int colnum) {
		// ��������
		Dispatch range = Dispatch.get(select, "Range").toDispatch();
		Dispatch.call(getTables(), "Add", range, new Variant(rownum), new Variant(colnum)).toDispatch();
		Dispatch.call(select, "MoveRight");
	}

	/**
	 * �ڵ�һ��ǰ����һ��
	 */
	public void insertRowBeforeFirstRow(int tableIndex) {
		Dispatch rows = Dispatch.get(getTable(tableIndex), "Rows").toDispatch(); // ������
		Dispatch row = Dispatch.get(rows, "First").toDispatch(); // ��һ��
		Dispatch.call(rows, "Add", new Variant(row));
	}

	/**
	 * �����һ��ǰ����һ��
	 */
	public void insertRowBeforeLastRow(int tableIndex) {
		// �õ����б�
		Dispatch rows = Dispatch.get(getTable(tableIndex), "Rows").toDispatch(); // ������
		Dispatch row = Dispatch.get(rows, "Last").toDispatch(); // ���һ��
		Dispatch.call(rows, "Add", new Variant(row));
	}

	/**
	 * �ڹ��λ�� ���²���һ�� ����������Ա���ϲ���Ԫ���²��Ų��롣
	 */
	public void insertRowBelowSelect(int num) {
		Dispatch.call(select, "InsertRowsBelow", new Variant(num));
	}

	/**
	 * ����ѡ�����ֵĶ��䷽ʽ
	 * 
	 * @param alignmentType
	 *            =0(�����),1(����),2(�Ҷ���)
	 */
	public void setAlignment(Dispatch dispatch, int alignmentType) {
		if (alignment == null) {
			getAlignment();
		}
		Dispatch.put(alignment, "Alignment", alignmentType);
	}

	public void getAlignment() {
		alignment = Dispatch.get(select, "ParagraphFormat").toDispatch();
	}

	/**
	 * �൱�� ctrl+A
	 */
	public void getAllText() {
		Dispatch.call(select, "WholeStory");
	}

	/**
	 * �õ����б��� Dispatch
	 * 
	 * @param tableIndex
	 * @return
	 */
	public Dispatch getTables() {
		return Dispatch.get(doc, "Tables").toDispatch();
	}

	/**
	 * �õ�ָ�����
	 * 
	 * @param tableIndex
	 * @return
	 */
	public Dispatch getTable(int tableIndex) {
		return this.getItem(getTables(), tableIndex);
	}

	/**
	 * �õ�ָ������ж�����
	 * 
	 * @return
	 */
	public int getTableRowsCount(int tableIndex) {
		return Dispatch.get(getRows(tableIndex), "Count").toInt();
	}

	/**
	 * �õ�ָ������ж�����
	 * 
	 * @return
	 */
	public int getTableColsCount(int tableIndex) {
		return Dispatch.get(getCols(tableIndex), "Count").toInt();
	}

	/**
	 * ��ָ����ǰ������
	 */
	public void insertRowBeforeRow(int tableIndex, int rowIndex) {
		Dispatch rows = getRows(tableIndex);
		Dispatch row = getRow(tableIndex, rowIndex);
		Dispatch.call(rows, "Add", row);
	}

	/**
	 * �ڵ�һ��ǰ����һ��
	 */
	public void insertColBeforeFistCol(int tableIndex) {
		Dispatch cols = Dispatch.get(getTable(tableIndex), "Columns").toDispatch(); // ������
		Dispatch col = Dispatch.get(cols, "First").toDispatch(); // ��һ��
		Dispatch.call(cols, "Add", col);
	}

	/**
	 * �����һ��ǰ����һ��
	 */
	public void insertColBeforeLastCol(int tableIndex) {
		Dispatch cols = Dispatch.get(getTable(tableIndex), "Columns").toDispatch(); // ������
		Dispatch col = Dispatch.get(cols, "Last").toDispatch(); // ���һ��
		Dispatch.call(cols, "Add", col).toDispatch();
	}

	/**
	 * ��ָ����ǰ����һ��
	 */
	public void insertColBeforeCol(int tableIndex, int colIndex) {
		Dispatch cols = Dispatch.get(getTable(tableIndex), "Columns").toDispatch(); // ������
		Dispatch col = getItem(cols, colIndex);
		Dispatch.call(cols, "Add", col);
	}

	/**
	 * ��ָ������������
	 * 
	 * @param tableIndex
	 *            ����ţ���1��ʼ
	 */
	public void insertTextIntoCell(int tableIndex, int rowIndex, int colIndex, String text) {
		Dispatch.call(getCell(tableIndex, rowIndex, colIndex), "Select");
		Dispatch.put(select, "Text", text);
	}

	/**
	 * ��������
	 */
	public void insertToDocument(String str) {
		if (str == null || str.equals("")) {
			return;
		}
		if (this.documents == null) {
			return;
		}
		/* д����word�� */
		Dispatch.put(this.select, "Text", str);
		this.moveRight(1, false);
	}

	/**
	 * �س���
	 */
	public void enterDown(int count) {
		for (int i = 0; i < count; i++) {
			Dispatch.call(this.select, "TypeParagraph");
		}
	}

	/**
	 * ����ͼƬ
	 * 
	 * @param path
	 */
	public void insertImage(String path) {
		if (path == null) {
			return;
		}
		Dispatch shapes = Dispatch.get(select, "InlineShapes").toDispatch();
		Dispatch.call(shapes, "AddPicture", new Variant(path));
	}

	/**
	 * ����ˮӡ ����
	 */

	public void insertWaterMark(String text) {
		openPageBrow();
		Dispatch headerFooter = Dispatch.get(select, "HeaderFooter").toDispatch();
		Dispatch shapes = Dispatch.get(headerFooter, "Shapes").toDispatch();
		getSelect(Dispatch.call(shapes, "AddTextEffect", 1, text, "����", 1, false, false, 0, 0).toDispatch());
		Dispatch shapeRange = Dispatch.get(select, "ShapeRange").toDispatch();
		Dispatch fill = Dispatch.get(shapeRange, "Fill").toDispatch();
		Color color = new Color(192, 192, 192);
		Dispatch.put(getDispatch(fill, "ForeColor"), "RGB", Math.abs(color.getRGB()));
		Dispatch.put(fill, "Transparency", new Variant(0.5));
		Dispatch.call(fill, "Solid");
		Dispatch.put(Dispatch.get(shapeRange, "Line").toDispatch(), "Visible", false);
		Dispatch.put(shapeRange, "Rotation", new Variant(315));
		Dispatch.put(shapeRange, "Width", 16.52 * 28.364);
		Dispatch.put(shapeRange, "Height", 6.13 * 28.364);
		// ����ˮƽ ��ֱ����
		Dispatch.put(shapeRange, "RelativeHorizontalPosition", 0);
		Dispatch.put(shapeRange, "Left", -999995);
		Dispatch.put(shapeRange, "RelativeVerticalPosition", 0);
		Dispatch.put(shapeRange, "Top", -999995);
		setPageBrowStyle(false);
		closePageBrow();
	}

	/**
	 * ��ҳü
	 */
	public void openPageBrow() {
		Dispatch activeWindow = Dispatch.get(word, "ActiveWindow").toDispatch();
		Dispatch activePane = Dispatch.get(activeWindow, "ActivePane").toDispatch();
		Dispatch view = Dispatch.get(activePane, "View").toDispatch();
		Dispatch.put(view, "SeekView", 1);
	}

	/**
	 * �ر�ҳü
	 */
	public void closePageBrow() {
		Dispatch activeWindow = Dispatch.get(word, "ActiveWindow").toDispatch();
		Dispatch activePane = Dispatch.get(activeWindow, "ActivePane").toDispatch();
		Dispatch view = Dispatch.get(activePane, "View").toDispatch();
		Dispatch.put(view, "SeekView", 0);
	}

	/**
	 * �ϲ������ļ�
	 * 
	 * @param otherFile
	 */
	public void mergeTwoFile(String otherFile) {
		Dispatch doc1 = Dispatch.call(documents, "Open", otherFile).toDispatch(); // �򿪵�һ���ļ�!!!
		Dispatch content = Dispatch.get(doc1, "Content").toDispatch(); // ȡ������Word�ļ�������!!!\
		Dispatch.call(content, "Copy");
		Dispatch.call(doc, "Select");
		this.moveDown(10, false);
		Dispatch.call(select, "Paste");
	}

	/**
	 * ɾ�����һ��
	 */
	public void deleteLastRow(int tableIndex) {
		Dispatch row = Dispatch.get(getRows(tableIndex), "Last").toDispatch();
		Dispatch.call(row, "Delete");
	}

	/**
	 * ɾ��ָ����
	 */
	public void deleteRow(int tableIndex, int rowIndex) {
		Dispatch row = Dispatch.call(getRows(tableIndex), "Item", new Variant(rowIndex)).toDispatch();
		Dispatch.call(row, "Delete");
	}

	/**
	 * ɾ�����һ��
	 */
	public void deleteLastCol(int tableIndex) {
		Dispatch tables = Dispatch.get(doc, "Tables").toDispatch();
		Dispatch table = Dispatch.call(tables, "Item", new Variant(tableIndex)).toDispatch();
		Dispatch cols = Dispatch.get(table, "Columns").toDispatch();
		Dispatch col = Dispatch.get(cols, "Last").toDispatch();
		Dispatch.call(col, "Delete");
	}

	/**
	 * ɾ��ָ����
	 */
	public void deleteCol(int tableIndex, int colIndex) {
		Dispatch tables = Dispatch.get(doc, "Tables").toDispatch();
		Dispatch table = Dispatch.call(tables, "Item", new Variant(tableIndex)).toDispatch();
		Dispatch cols = Dispatch.get(table, "Columns").toDispatch();
		Dispatch col = Dispatch.call(cols, "Item", new Variant(colIndex)).toDispatch();
		Dispatch.call(col, "Delete");
	}

	/**
	 * �ϲ���Ԫ��
	 */
	public void mergeCell(int tableIndex, int firstCellRowIndex, int firstCellColIndex, int secondCellRowIndex, int secondCellColIndex) {
		Dispatch tables = Dispatch.get(doc, "Tables").toDispatch();
		Dispatch table = Dispatch.call(tables, "Item", new Variant(tableIndex)).toDispatch();
		Dispatch firstCell = Dispatch.call(table, "Cell", new Variant(firstCellRowIndex), new Variant(firstCellColIndex)).toDispatch();
		Dispatch secondCell = Dispatch.call(table, "Cell", new Variant(secondCellRowIndex), new Variant(secondCellColIndex)).toDispatch();
		Dispatch.call(firstCell, "Merge", secondCell);
	}

	/**
	 * ��ֵ�Ԫ��
	 */
	/**
	 * ����Զ�����
	 */
	public void autoFitTable() {
		Dispatch tables = Dispatch.get(doc, "Tables").toDispatch();
		int count = Dispatch.get(tables, "Count").toInt();
		for (int i = 0; i < count; i++) {
			Dispatch table = Dispatch.call(tables, "Item", new Variant(i + 1)).toDispatch();
			Dispatch cols = Dispatch.get(table, "Columns").toDispatch();
			Dispatch.call(cols, "AutoFit");
		}
	}

	/**
	 * �ƶ����ı��� 5 �ص���ҳ
	 */
	public void moveHome(boolean ifSelect) {
		if (select == null) {
			select = Dispatch.get(word, "Selection").toDispatch();
		}
		if (ifSelect) {
			Dispatch.call(select, "HomeKey", new Variant(6), new Variant(1));
			return;
		}
		Dispatch.call(select, "HomeKey", new Variant(6));
	}

	/**
	 * �ƶ����ı������ end
	 * 
	 * @param ifSelect�Ƿ�ѡ��
	 *            �൱�ڰ�shift+end
	 */
	public void moveEnd(boolean ifSelect) {
		if (select == null) {
			select = Dispatch.get(word, "Selection").toDispatch();
		}
		if (ifSelect) {
			Dispatch.call(select, "EndKey", new Variant(6), new Variant(1));
		} else {
			Dispatch.call(select, "EndKey", new Variant(6));
		}
	}

	/**
	 * �����ƶ�N��
	 * 
	 * @param count
	 */
	public void moveUp(int count, boolean ifSelect) {
		if (ifSelect) {
			Dispatch.call(select, "MoveUp", new Variant(4), new Variant(count), new Variant(1));
			return;
		}
		for (int i = 0; i < count; i++) {
			Dispatch.call(this.select, "MoveUp");
		}

	}

	/**
	 * �����ƶ�N��
	 * 
	 * @param count
	 */
	public void moveDown(int count, boolean ifSelect) {
		if (ifSelect) {
			Dispatch.call(this.select, "MoveDown", new Variant(4), new Variant(count), new Variant(1));
			return;
		}
		for (int i = 0; i < count; i++) {
			Dispatch.call(this.select, "MoveDown");
		}

	}

	/**
	 * �����ƶ�N��
	 * 
	 * @param count
	 */
	public void moveLeft(int count, boolean ifSelect) {
		if (ifSelect) {
			Dispatch.call(this.select, "MoveLeft", new Variant(1), new Variant(count), new Variant(1));
		} else {
			for (int i = 0; i < count; i++) {
				Dispatch.call(this.select, "MoveLeft");
			}
		}
	}

	/**
	 * �����ƶ�N��
	 * 
	 * @param count
	 */
	public void moveRight(int count, boolean ifSelect) {
		if (ifSelect) {
			Dispatch.call(this.select, "MoveRight", new Variant(1), new Variant(count), new Variant(1));
		} else {
			for (int i = 0; i < count; i++) {
				Dispatch.call(this.select, "MoveRight");
			}
		}

	}

	/**
	 * �õ� ָ������ڵ����ݡ�
	 * 
	 * @param tableIndex
	 * @param cellRowIndex
	 * @param cellColIndex
	 * @return
	 */
	public String getCellString(int tableIndex, int cellRowIndex, int cellColIndex) {
		Dispatch tables = Dispatch.get(doc, "Tables").toDispatch();
		Dispatch table = Dispatch.call(tables, "Item", new Variant(tableIndex)).toDispatch();
		Dispatch cell = Dispatch.call(table, "Cell", new Variant(cellRowIndex), new Variant(cellColIndex)).toDispatch();
		Dispatch.call(cell, "Select");
		return Dispatch.get(select, "Text").toString();
	}

	/**
	 * �滻���з���Ҫ����ı�
	 * 
	 * @param findText
	 * @param newText
	 */
	public void replaceAllText(String findText, String newText) {
		while (findText(findText)) {
			Dispatch.put(select, "Text", newText);
			Dispatch.call(select, "MoveRight");
		}

	}

	/**
	 * ���ҷ�������
	 * 
	 * @param text
	 * @return
	 */
	public boolean findText(String text) {
		if (text == null || text.equals("")) {
			return false;
		}
		Dispatch find = word.call(select, "Find").toDispatch();
		Dispatch.put(find, "Text", text);
		Dispatch.put(find, "Forward", "True");
		Dispatch.put(find, "Format", "True");
		Dispatch.put(find, "MatchCase", "True");
		Dispatch.put(find, "MatchWholeWord", "True");
		return Dispatch.call(find, "Execute").getBoolean();
	}

	/**
	 * ��������
	 * 
	 * @param fontName
	 * @param isBold
	 * @param isItalic
	 * @param isUnderline
	 * @param rgbColor
	 * @param Scale
	 * @param fontSize
	 */
	public void setFontScale(String fontName, boolean isBold, boolean isItalic, boolean isUnderline, String rgbColor, int Scale, float fontSize) {
		Dispatch font = Dispatch.get(this.select, "Font").toDispatch();
		Dispatch.put(font, "Name", fontName);
		Dispatch.put(font, "Bold", isBold);
		Dispatch.put(font, "Italic", isItalic);
		Dispatch.put(font, "Underline", isUnderline);
		// Dispatch.put(font, "Color", rgbColor);
		// Dispatch.put(font, "Scaling", Scale);
		Dispatch.put(font, "Size", fontSize);
	}

	public void setFontBgColor(int color) {
		Dispatch font = Dispatch.get(this.select, "Font").toDispatch();
		Dispatch shading = Dispatch.get(font, "shading").toDispatch();
		Dispatch.put(shading, "BackgroundPatternColorIndex", color);
	}

	/**
	 * ����һ����Ԫ��ı�����ɫ
	 * 
	 * @param tableIndex
	 * @param rowIndex
	 * @param colIndex
	 * @param color
	 *            color = 16Ϊϵͳ���û�ɫ��
	 */
	public void setCellColor(int tableIndex, int rowIndex, int colIndex, int color) {
		Dispatch tables = Dispatch.get(doc, "Tables").toDispatch();
		Dispatch table = Dispatch.call(tables, "Item", new Variant(tableIndex)).toDispatch();
		Dispatch cell = Dispatch.call(table, "Cell", new Variant(rowIndex), new Variant(colIndex)).toDispatch();
		Dispatch shading = Dispatch.get(cell, "shading").toDispatch();
		Dispatch.put(shading, "BackgroundPatternColorIndex", new Variant(color));
	}

	/**
	 * ����һ��cell���ֱ���䷽ʽ
	 * 
	 * @param tableIndex
	 * @param rowIndex
	 * @param colIndex
	 * @param alignmentType
	 */
	public void setCellVerticalAlignment(int tableIndex, int rowIndex, int colIndex, int alignmentType) {
		Dispatch tables = Dispatch.get(doc, "Tables").toDispatch();
		Dispatch table = Dispatch.call(tables, "Item", new Variant(tableIndex)).toDispatch();
		Dispatch cell = Dispatch.call(table, "Cell", new Variant(rowIndex), new Variant(colIndex)).toDispatch();
		Dispatch.put(cell, "VerticalAlignment", new Variant(alignmentType));
	}

	/**
	 * ����ѡ�б�񼯺ϵĴ�ֱ���䷽ʽ 1Ϊ��ֱ����
	 * 
	 * @param alignmentType
	 */
	public void setSelectCellsVarticalAlignment(int alignmentType) {
		Dispatch cells = Dispatch.get(select, "Cells").toDispatch();
		Dispatch.put(cells, "VerticalAlignment", new Variant(alignmentType));
	}

	/**
	 * ����ҳ������ orientation 1 Ϊ ���� 0Ϊ����
	 */
	public void setPageSetup(int orientation, float leftMargin, float rightMargin, float topMargin, float buttomMargin) {
		if (pageSetup == null) {
			this.getPageSetup();
		}
		Dispatch.put(pageSetup, "Orientation", orientation); //
		if (leftMargin > 0)
			Dispatch.put(pageSetup, "LeftMargin", leftMargin);
		if (rightMargin > 0)
			Dispatch.put(pageSetup, "RightMargin", rightMargin);
		if (topMargin > 0)
			Dispatch.put(pageSetup, "TopMargin", topMargin);
		if (buttomMargin > 0)
			Dispatch.put(pageSetup, "BottomMargin", buttomMargin);
	}

	/**
	 * ���ù������ҳ���ҳ������ �����Ҫ��һ��word�ĵ��г�������ҳ�����ã���Ҫ���� insertBreak()����һ����һҳ����,
	 * 
	 * @param orientation
	 *            =0(����),1(����)
	 */
	public void setSelectPageSetup(int orientation) {
		this.getSelectPageSetup();
		Dispatch.put(pageSetup, "Orientation", orientation);
	}

	public void setSelectPageSetup(int orientation, float leftMargin, float rightMargin, float topMargin, float buttomMargin) {
		this.getSelectPageSetup();
		setPageSetup(orientation, leftMargin, rightMargin, topMargin, buttomMargin);
	}

	/**
	 * ���ñ����ߴ�ϸ
	 * 
	 * @param tableIndex
	 * @param width
	 *            =0.1
	 */
	public void setTableLineType(int tableIndex, float width) {
		Dispatch tables = Dispatch.get(doc, "Tables").toDispatch();
		Dispatch table = Dispatch.call(tables, "Item", new Variant(tableIndex)).toDispatch();
		Dispatch borders = Dispatch.get(table, "Borders").toDispatch();
		Dispatch border = null;
		for (int i = 1; i < 7; i++) {
			border = Dispatch.call(borders, "Item", new Variant(i)).toDispatch();
			if (width != 0) {
				Dispatch.put(border, "LineWidth", new Variant(width));
				Dispatch.put(border, "Visible", new Variant(true));
			} else if (width == 0) {
				Dispatch.put(border, "Visible", new Variant(false));
			}
		}
	}

	/**
	 * ���ñ���ڸ�ҳ���� �Ƿ��Ա�ͷ����
	 * 
	 * @param tab_index
	 *            ���Ϊ-1 ��ʾ���б��
	 */
	// public void setTableHeadingFormat(int tab_index,boolean flag,int rowNum){
	// if(tab_index!=-1){
	// Dispatch.put(getRow(tab_index, rowNum),"HeadingFormat",new
	// Variant(flag));
	// return;
	// }
	// Dispatch tables = getTables();
	// int count = Dispatch.get(tables, "Count").toInt();
	// for (int i = 0; i < count; i++) {
	// Dispatch table = Dispatch.call(tables, "Item", new Variant(i + 1))
	// .toDispatch();
	// Dispatch rows = Dispatch.get(table, "Rows").toDispatch();
	// Dispatch row = Dispatch.call(rows, "Item",new Variant(1)).toDispatch();
	// Dispatch.put(row, "HeadingFormat",new Variant(flag));
	// }
	// }
	/**
	 * ���ñ��ĵ�N���ظ����⡣ ʵ�������кϲ��ġ�
	 */
	public void setTableHeadingFormat(int tab_index, boolean flag, int rowNum) {
		if (tab_index != -1) {
			this.setTableHeadingFormat(tab_index, flag, rowNum, 1);
			return;
		}
		Dispatch tables = getTables();
		int count = Dispatch.get(tables, "Count").toInt();
		for (int i = 0; i < count; i++) {
			setTableHeadingFormat(i + 1, flag, rowNum, 1);
		}
	}

	public void setTableHeadingFormat(int tab_index, boolean flag, int rowNum, int colNum) {
		if (tab_index <= 0) {
			return;
		}
		getSelect(getCell(tab_index, rowNum, colNum));
		Dispatch rows = Dispatch.get(select, "Rows").toDispatch();
		Dispatch.put(rows, "HeadingFormat", new Variant(flag));
	}

	/**
	 * ѡ��һ�� �����ǲ���������ϲ��ĵ�Ԫ��
	 * 
	 * @param tab_index
	 * @param rownum
	 */
	public void setRowSelected(int tableIndex, int rownum) {
		Dispatch rows = Dispatch.get(getTable(tableIndex), "Rows").toDispatch();
		Dispatch row = Dispatch.call(rows, "Item", rownum).toDispatch();
		Dispatch.call(row, "Select");
	}

	/**
	 * ����ѡ�е�Ԫ�� �Ĵ�ֱ���䷽ʽ
	 * 
	 * @param tab_index
	 * @param startRow
	 * @param endRow
	 */
	public void setSelectCellVerticalAlignment(int tab_index, int startRow, int endRow) {
		Dispatch cells = Dispatch.get(select, "Cells").toDispatch();
		Dispatch.put(cells, "VerticalAlignment", 1);
	}

	/**
	 * �ڵ�ǰλ�� ����һ�� ����
	 * 
	 * @param insertBreak
	 *            =1(��ҳ����) 2(��һҳ�����԰���ҳ��ֿ���) 3���� 4��ż��ҳ 5����ҳ 6���и� .
	 * 
	 * 
	 */
	public void insertBreak(int breakType) {
		Dispatch.call(select, "InsertBreak", new Variant(breakType));
	}

	/**
	 * ��ʽ��ͼƬ �ü�
	 * 
	 * @param type
	 */
	public void formatImage(int img_index, float top, float bottom, float left, float right, String type) {
		Dispatch dispatch = getImage(img_index);
		Dispatch format = Dispatch.get(dispatch, "PictureFormat").toDispatch();
		if (type != null && type.equals(this.TYPE_CM)) {
			if (left > 0) {
				format.put(format, "CropLeft", left * 28.364f);
			}
			if (right > 0) {
				format.put(format, "CropRight", right * 28.364f);
			}
			if (top > 0) {
				format.put(format, "CropTop", top * 28.364f);
			}
			if (bottom > 0) {
				format.put(format, "CropBottom", bottom * 28.364f);
			}
		} else {
			if (left > 0) {
				format.put(format, "CropLeft", left);
			}
			if (right > 0) {
				format.put(format, "CropRight", right);
			}
			if (top > 0) {
				format.put(format, "CropTop", top);
			}
			if (bottom > 0) {
				format.put(format, "CropBottom", bottom);
			}
		}
	}

	/**
	 * ��ȡҳ������
	 */
	public void getPageSetup() {
		if (pageSetup == null) {
			this.pageSetup = Dispatch.get(doc, "PageSetup").toDispatch();
		}
	}

	/**
	 * ��ȡ�������λ��ҳ������
	 */
	public void getSelectPageSetup() {
		this.pageSetup = Dispatch.get(select, "PageSetup").toDispatch();
	}

	/**
	 * ���Item
	 * 
	 * @param dispatch
	 * @param item
	 * @return
	 */
	public Dispatch getItem(Dispatch dispatch, Object item) {
		return Dispatch.call(dispatch, "Item", new Variant(item)).toDispatch();
	}

	/**
	 * ���õ�Ԫ�� ��С��
	 * 
	 * @param tab_index
	 * @param row_index
	 * @param col_index
	 * @param width
	 *            -1 ΪĬ�ϴ�С
	 * @param height
	 */
	public void setCellWidthAndHight(int tab_index, int row_index, int col_index, int width, int height, int type) {
		if (width != -1) {
			if (type == 1)
				Dispatch.put(getCol(tab_index, col_index), "PreferredWidthType", 1); // 2
			// �ǰٷֱ�
			// 1����
			Dispatch.put(getCol(tab_index, col_index), "PreferredWidth", width);
		}
		if (height != -1) {
			Dispatch.put(getRow(tab_index, row_index), "Height", height);
		}
	}

	/**
	 * ����ҳü �Ƿ�߿� ������ɵ��ĵ�ҳü��һ�����ߣ��������������
	 */
	public void setPageBrowStyle(boolean flag) {
		Dispatch styles = Dispatch.call(getActiveDocument(), "Styles", "ҳü").toDispatch();
		Dispatch paragraphFormat = Dispatch.get(styles, "ParagraphFormat").toDispatch();
		Dispatch Borders = Dispatch.get(paragraphFormat, "Borders").toDispatch();
		for (int i = 1; i <= 4; i++) {
			Dispatch border = Dispatch.call(Borders, "Item", i).toDispatch();
			Dispatch.put(border, "Visible", flag);
		}
	}

	/**
	 * ����ָ��ͼƬ��С
	 * 
	 * @param img_index
	 * @param width
	 * @param height
	 * @param type
	 */
	public void setImageWidthAndHeight(int img_index, float width, float height, String type) {
		Dispatch dispatch = getImage(img_index);
		if (width > 0 && type.equals(this.TYPE_CM)) {
			Dispatch.put(dispatch, "Width", width * 28.364f);
		} else if (width > 0) {
			Dispatch.put(dispatch, "Width", width);
		}
		if (height > 0 && type.equals(this.TYPE_CM)) {
			Dispatch.put(dispatch, "Height", height * 28.364f);
		} else if (height > 0) {
			Dispatch.put(dispatch, "Height", height);
		}
	}

	/**
	 * ��������ʾҳ��
	 */
	public void setPageNumbers(boolean flag) {
		Dispatch sections = Dispatch.get(select, "Sections").toDispatch();
		Dispatch section = this.getItem(sections, 1);
		Dispatch foot = getItem(Dispatch.get(section, "Footers").toDispatch(), 1);
		Dispatch pageNumbers = Dispatch.get(foot, "PageNumbers").toDispatch();
		Dispatch.call(pageNumbers, "add", ALIGNMENT_CENTER, flag);
	}

	/**
	 * �õ�һ������������
	 */
	public Dispatch getRows(int tableindex) {
		Dispatch rows = Dispatch.get(getTable(tableindex), "Rows").toDispatch();
		return rows;
	}

	/**
	 * �õ�һ��
	 */
	public Dispatch getRow(int tableindex, int rowNum) {
		return getItem(getRows(tableindex), rowNum);
	}

	/**
	 * �õ�һ���������
	 */
	public Dispatch getCols(int tableindex) {
		return Dispatch.get(getTable(tableindex), "Columns").toDispatch();
	}

	/**
	 * �õ�һ��
	 */
	public Dispatch getCol(int tableindex, int colNum) {
		return getItem(getCols(tableindex), colNum);
	}

	/**
	 * �õ�һ�����
	 */
	public Dispatch getCell(int tab_index, int row_index, int col_index) {
		return Dispatch.call(this.getTable(tab_index), "Cell", new Variant(row_index), new Variant(col_index)).toDispatch();
	}

	/**
	 * ActiveDocument
	 */
	public Dispatch getActiveDocument() {
		return Dispatch.get(word, "ActiveDocument").toDispatch();
	}

	/**
	 * ʹ��һ�������ȡselect ����Ҫ��
	 */
	public void getSelect(Dispatch dispatch) {
		Dispatch.call(dispatch, "Select");
	}

	/**
	 * �õ�ָ��ͼƬ
	 */
	public Dispatch getImage(int img_index) {
		Dispatch dispatch = Dispatch.get(doc, "InlineShapes").toDispatch();
		return getItem(dispatch, img_index);
	}

	/**
	 * �õ�ָ��ͼƬ�� ��͸�
	 * 
	 * @param img_index
	 * @param RETURNTYPE
	 *            =RETURN_TYPE_CM,RETURN_TYPE_PIX
	 * @return
	 */
	public float[] getImageWidthAndHeight(int img_index, String RETURNTYPE) {
		Dispatch dispatch = getImage(img_index);
		int width = Dispatch.get(dispatch, "Width").toInt();
		int height = Dispatch.get(dispatch, "Height").toInt();
		if (RETURNTYPE != null && RETURNTYPE.equals(this.TYPE_CM)) {
			return new float[] { width / 28.34f, height / 28.34f };
		} else {
			return new float[] { width, height };
		}

	}

	public Dispatch getDispatch(Dispatch dispatch, String getDisType) {
		return Dispatch.get(dispatch, getDisType).toDispatch();
	}

	/**
	 * �رյ�ǰ�ĵ�
	 */
	public void closeDocument() {
		if (doc != null) {
			Dispatch.call(doc, "Save");
			Dispatch.call(doc, "Close", new Variant(true));
			doc = null;
		}
	}

	/**
	 * �ر�wordӦ�ó���
	 */
	public void close() {
		if (word != null) {
			Dispatch.call(word, "Quit");
			word = null;
		}
		select = null;
		documents = null;
	}

	/**
	 * ���������Ҫǿ�ƹص�wordӦ�� ��catch��׽�쳣����
	 */
	public void exit() {
		Dispatch.call(doc, "Close", new Variant(false));
		Dispatch.call(word, "Quit");
	}
}
