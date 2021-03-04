package cn.com.pushworld.salary.ui.word;

import java.awt.Color;

import cn.com.infostrategy.ui.common.MessageBox;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComFailException;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * 操作word工具 调用jacob
 * 
 * @author hm
 */
public class WordUtil extends Dispatch {
	// 创建word文档
	private Dispatch doc = null;
	// word应用程序
	private ActiveXComponent word = null;
	// 光标位置 插入点
	private Dispatch select = null;
	// 创建文档集合
	private Dispatch documents = null;

	private Dispatch pageSetup = null;

	private Dispatch alignment = null;

	public static final String TYPE_CM = "厘米";
	public static final String TYPE_PIX = "像素";
	public static final int ALIGNMENT_LEFT_OR_TOP = 0;
	public static final int ALIGNMENT_CENTER = 1;
	public static final int ALIGNMENT_RIGHT_OR_BOTTOM = 2;
	public static final int PAGESET_ORIETATION_ENDWISE = 0; // 纵向
	public static final int PAGESET_ORIETATION_CROSSWISE = 1; // 横向

	public WordUtil() throws Exception {
		word = new ActiveXComponent("Word.Application");
		word.setProperty("Visible", new Variant(false)); // 设置后台执行
		documents = word.getProperty("Documents").toDispatch();
	}

	/**
	 * 创建新文档
	 */
	public void createNewWordDocument() {
		doc = Dispatch.call(documents, "Add").toDispatch();
		select = Dispatch.get(word, "Selection").toDispatch();
	}

	/**
	 * 打开文档
	 * 
	 * @param filePath
	 */
	public void openWordDocument(String filePath) {
		doc = Dispatch.call(documents, "Open", filePath).toDispatch();
		select = Dispatch.get(word, "Selection").toDispatch();
	}

	/**
	 * 保存文档
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
	 * 保存成 XML 文件
	 * 
	 * @param path
	 */
	public void saveXMLDocument(String path) {
		Dispatch.invoke(doc, "SaveAs", Dispatch.Method, new Object[] { path, new Variant(11) }, new int[1]);

	}

	/**
	 * 保存成 HTML 文件
	 * 
	 * @param path
	 */
	public void saveHTMLDocument(String path) {
		Dispatch.invoke(doc, "SaveAs", Dispatch.Method, new Object[] { path, new Variant(8) }, new int[1]);

	}

	/**
	 * 插入表格 row col 自定义
	 */
	public void createNewTable(int rownum, int colnum) {
		// 创建表集合
		Dispatch range = Dispatch.get(select, "Range").toDispatch();
		Dispatch.call(getTables(), "Add", range, new Variant(rownum), new Variant(colnum)).toDispatch();
		Dispatch.call(select, "MoveRight");
	}

	/**
	 * 在第一行前插入一行
	 */
	public void insertRowBeforeFirstRow(int tableIndex) {
		Dispatch rows = Dispatch.get(getTable(tableIndex), "Rows").toDispatch(); // 所有行
		Dispatch row = Dispatch.get(rows, "First").toDispatch(); // 第一行
		Dispatch.call(rows, "Add", new Variant(row));
	}

	/**
	 * 在最后一行前插入一行
	 */
	public void insertRowBeforeLastRow(int tableIndex) {
		// 得到所有表
		Dispatch rows = Dispatch.get(getTable(tableIndex), "Rows").toDispatch(); // 所有行
		Dispatch row = Dispatch.get(rows, "Last").toDispatch(); // 最后一行
		Dispatch.call(rows, "Add", new Variant(row));
	}

	/**
	 * 在光标位置 向下插入一行 这个方法可以避免合并单元格导致部门插入。
	 */
	public void insertRowBelowSelect(int num) {
		Dispatch.call(select, "InsertRowsBelow", new Variant(num));
	}

	/**
	 * 设置选定文字的对其方式
	 * 
	 * @param alignmentType
	 *            =0(左对齐),1(居中),2(右对齐)
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
	 * 相当于 ctrl+A
	 */
	public void getAllText() {
		Dispatch.call(select, "WholeStory");
	}

	/**
	 * 得到所有表格的 Dispatch
	 * 
	 * @param tableIndex
	 * @return
	 */
	public Dispatch getTables() {
		return Dispatch.get(doc, "Tables").toDispatch();
	}

	/**
	 * 得到指定表格
	 * 
	 * @param tableIndex
	 * @return
	 */
	public Dispatch getTable(int tableIndex) {
		return this.getItem(getTables(), tableIndex);
	}

	/**
	 * 得到指定表格有多少行
	 * 
	 * @return
	 */
	public int getTableRowsCount(int tableIndex) {
		return Dispatch.get(getRows(tableIndex), "Count").toInt();
	}

	/**
	 * 得到指定表格有多少列
	 * 
	 * @return
	 */
	public int getTableColsCount(int tableIndex) {
		return Dispatch.get(getCols(tableIndex), "Count").toInt();
	}

	/**
	 * 在指定行前插入行
	 */
	public void insertRowBeforeRow(int tableIndex, int rowIndex) {
		Dispatch rows = getRows(tableIndex);
		Dispatch row = getRow(tableIndex, rowIndex);
		Dispatch.call(rows, "Add", row);
	}

	/**
	 * 在第一列前插入一列
	 */
	public void insertColBeforeFistCol(int tableIndex) {
		Dispatch cols = Dispatch.get(getTable(tableIndex), "Columns").toDispatch(); // 所有列
		Dispatch col = Dispatch.get(cols, "First").toDispatch(); // 第一列
		Dispatch.call(cols, "Add", col);
	}

	/**
	 * 在最后一列前插入一列
	 */
	public void insertColBeforeLastCol(int tableIndex) {
		Dispatch cols = Dispatch.get(getTable(tableIndex), "Columns").toDispatch(); // 所有列
		Dispatch col = Dispatch.get(cols, "Last").toDispatch(); // 最后一列
		Dispatch.call(cols, "Add", col).toDispatch();
	}

	/**
	 * 在指定列前插入一列
	 */
	public void insertColBeforeCol(int tableIndex, int colIndex) {
		Dispatch cols = Dispatch.get(getTable(tableIndex), "Columns").toDispatch(); // 所有列
		Dispatch col = getItem(cols, colIndex);
		Dispatch.call(cols, "Add", col);
	}

	/**
	 * 在指定表格插入内容
	 * 
	 * @param tableIndex
	 *            表格编号，从1开始
	 */
	public void insertTextIntoCell(int tableIndex, int rowIndex, int colIndex, String text) {
		Dispatch.call(getCell(tableIndex, rowIndex, colIndex), "Select");
		Dispatch.put(select, "Text", text);
	}

	/**
	 * 插入文字
	 */
	public void insertToDocument(String str) {
		if (str == null || str.equals("")) {
			return;
		}
		if (this.documents == null) {
			return;
		}
		/* 写出至word中 */
		Dispatch.put(this.select, "Text", str);
		this.moveRight(1, false);
	}

	/**
	 * 回车键
	 */
	public void enterDown(int count) {
		for (int i = 0; i < count; i++) {
			Dispatch.call(this.select, "TypeParagraph");
		}
	}

	/**
	 * 插入图片
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
	 * 加入水印 测试
	 */

	public void insertWaterMark(String text) {
		openPageBrow();
		Dispatch headerFooter = Dispatch.get(select, "HeaderFooter").toDispatch();
		Dispatch shapes = Dispatch.get(headerFooter, "Shapes").toDispatch();
		getSelect(Dispatch.call(shapes, "AddTextEffect", 1, text, "宋体", 1, false, false, 0, 0).toDispatch());
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
		// 设置水平 垂直居中
		Dispatch.put(shapeRange, "RelativeHorizontalPosition", 0);
		Dispatch.put(shapeRange, "Left", -999995);
		Dispatch.put(shapeRange, "RelativeVerticalPosition", 0);
		Dispatch.put(shapeRange, "Top", -999995);
		setPageBrowStyle(false);
		closePageBrow();
	}

	/**
	 * 打开页眉
	 */
	public void openPageBrow() {
		Dispatch activeWindow = Dispatch.get(word, "ActiveWindow").toDispatch();
		Dispatch activePane = Dispatch.get(activeWindow, "ActivePane").toDispatch();
		Dispatch view = Dispatch.get(activePane, "View").toDispatch();
		Dispatch.put(view, "SeekView", 1);
	}

	/**
	 * 关闭页眉
	 */
	public void closePageBrow() {
		Dispatch activeWindow = Dispatch.get(word, "ActiveWindow").toDispatch();
		Dispatch activePane = Dispatch.get(activeWindow, "ActivePane").toDispatch();
		Dispatch view = Dispatch.get(activePane, "View").toDispatch();
		Dispatch.put(view, "SeekView", 0);
	}

	/**
	 * 合并两个文件
	 * 
	 * @param otherFile
	 */
	public void mergeTwoFile(String otherFile) {
		Dispatch doc1 = Dispatch.call(documents, "Open", otherFile).toDispatch(); // 打开第一个文件!!!
		Dispatch content = Dispatch.get(doc1, "Content").toDispatch(); // 取得整个Word文件的内容!!!\
		Dispatch.call(content, "Copy");
		Dispatch.call(doc, "Select");
		this.moveDown(10, false);
		Dispatch.call(select, "Paste");
	}

	/**
	 * 删除最后一行
	 */
	public void deleteLastRow(int tableIndex) {
		Dispatch row = Dispatch.get(getRows(tableIndex), "Last").toDispatch();
		Dispatch.call(row, "Delete");
	}

	/**
	 * 删除指定行
	 */
	public void deleteRow(int tableIndex, int rowIndex) {
		Dispatch row = Dispatch.call(getRows(tableIndex), "Item", new Variant(rowIndex)).toDispatch();
		Dispatch.call(row, "Delete");
	}

	/**
	 * 删除最后一列
	 */
	public void deleteLastCol(int tableIndex) {
		Dispatch tables = Dispatch.get(doc, "Tables").toDispatch();
		Dispatch table = Dispatch.call(tables, "Item", new Variant(tableIndex)).toDispatch();
		Dispatch cols = Dispatch.get(table, "Columns").toDispatch();
		Dispatch col = Dispatch.get(cols, "Last").toDispatch();
		Dispatch.call(col, "Delete");
	}

	/**
	 * 删除指定列
	 */
	public void deleteCol(int tableIndex, int colIndex) {
		Dispatch tables = Dispatch.get(doc, "Tables").toDispatch();
		Dispatch table = Dispatch.call(tables, "Item", new Variant(tableIndex)).toDispatch();
		Dispatch cols = Dispatch.get(table, "Columns").toDispatch();
		Dispatch col = Dispatch.call(cols, "Item", new Variant(colIndex)).toDispatch();
		Dispatch.call(col, "Delete");
	}

	/**
	 * 合并单元格
	 */
	public void mergeCell(int tableIndex, int firstCellRowIndex, int firstCellColIndex, int secondCellRowIndex, int secondCellColIndex) {
		Dispatch tables = Dispatch.get(doc, "Tables").toDispatch();
		Dispatch table = Dispatch.call(tables, "Item", new Variant(tableIndex)).toDispatch();
		Dispatch firstCell = Dispatch.call(table, "Cell", new Variant(firstCellRowIndex), new Variant(firstCellColIndex)).toDispatch();
		Dispatch secondCell = Dispatch.call(table, "Cell", new Variant(secondCellRowIndex), new Variant(secondCellColIndex)).toDispatch();
		Dispatch.call(firstCell, "Merge", secondCell);
	}

	/**
	 * 拆分单元格。
	 */
	/**
	 * 表格自动调整
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
	 * 移动到文本首 5 回到首页
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
	 * 移动到文本行最后 end
	 * 
	 * @param ifSelect是否选择
	 *            相当于按shift+end
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
	 * 向上移动N行
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
	 * 向下移动N行
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
	 * 向左移动N列
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
	 * 向右移动N列
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
	 * 得到 指定表格内的内容。
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
	 * 替换所有符合要求的文本
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
	 * 查找符合文字
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
	 * 设置字体
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
	 * 设置一个单元格的背景颜色
	 * 
	 * @param tableIndex
	 * @param rowIndex
	 * @param colIndex
	 * @param color
	 *            color = 16为系统常用灰色。
	 */
	public void setCellColor(int tableIndex, int rowIndex, int colIndex, int color) {
		Dispatch tables = Dispatch.get(doc, "Tables").toDispatch();
		Dispatch table = Dispatch.call(tables, "Item", new Variant(tableIndex)).toDispatch();
		Dispatch cell = Dispatch.call(table, "Cell", new Variant(rowIndex), new Variant(colIndex)).toDispatch();
		Dispatch shading = Dispatch.get(cell, "shading").toDispatch();
		Dispatch.put(shading, "BackgroundPatternColorIndex", new Variant(color));
	}

	/**
	 * 设置一个cell表格垂直对其方式
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
	 * 设置选中表格集合的垂直对其方式 1为垂直居中
	 * 
	 * @param alignmentType
	 */
	public void setSelectCellsVarticalAlignment(int alignmentType) {
		Dispatch cells = Dispatch.get(select, "Cells").toDispatch();
		Dispatch.put(cells, "VerticalAlignment", new Variant(alignmentType));
	}

	/**
	 * 设置页面属性 orientation 1 为 横向 0为纵向
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
	 * 设置光标所在页面的页面属性 如果需要在一个word文档中出现两种页面设置，需要调用 insertBreak()插入一个下一页符号,
	 * 
	 * @param orientation
	 *            =0(横向),1(纵向)
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
	 * 设置表格边线粗细
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
	 * 设置表格在各页顶端 是否以表头出现
	 * 
	 * @param tab_index
	 *            如果为-1 表示所有表格
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
	 * 设置表格的第N行重复标题。 实用与表格有合并的。
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
	 * 选中一行 ，但是不能有纵向合并的单元格。
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
	 * 设置选中单元格 的垂直对其方式
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
	 * 在当前位置 插入一个 符号
	 * 
	 * @param insertBreak
	 *            =1(分页符号) 2(下一页，可以把两页面分开搞) 3连续 4是偶数页 5奇数页 6换行副 .
	 * 
	 * 
	 */
	public void insertBreak(int breakType) {
		Dispatch.call(select, "InsertBreak", new Variant(breakType));
	}

	/**
	 * 格式化图片 裁剪
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
	 * 获取页面属性
	 */
	public void getPageSetup() {
		if (pageSetup == null) {
			this.pageSetup = Dispatch.get(doc, "PageSetup").toDispatch();
		}
	}

	/**
	 * 获取光标所在位置页面属性
	 */
	public void getSelectPageSetup() {
		this.pageSetup = Dispatch.get(select, "PageSetup").toDispatch();
	}

	/**
	 * 获得Item
	 * 
	 * @param dispatch
	 * @param item
	 * @return
	 */
	public Dispatch getItem(Dispatch dispatch, Object item) {
		return Dispatch.call(dispatch, "Item", new Variant(item)).toDispatch();
	}

	/**
	 * 设置单元格 大小。
	 * 
	 * @param tab_index
	 * @param row_index
	 * @param col_index
	 * @param width
	 *            -1 为默认大小
	 * @param height
	 */
	public void setCellWidthAndHight(int tab_index, int row_index, int col_index, int width, int height, int type) {
		if (width != -1) {
			if (type == 1)
				Dispatch.put(getCol(tab_index, col_index), "PreferredWidthType", 1); // 2
			// 是百分比
			// 1像素
			Dispatch.put(getCol(tab_index, col_index), "PreferredWidth", width);
		}
		if (height != -1) {
			Dispatch.put(getRow(tab_index, row_index), "Height", height);
		}
	}

	/**
	 * 设置页眉 是否边框 如果生成的文档页眉有一条横线，设置这个方法。
	 */
	public void setPageBrowStyle(boolean flag) {
		Dispatch styles = Dispatch.call(getActiveDocument(), "Styles", "页眉").toDispatch();
		Dispatch paragraphFormat = Dispatch.get(styles, "ParagraphFormat").toDispatch();
		Dispatch Borders = Dispatch.get(paragraphFormat, "Borders").toDispatch();
		for (int i = 1; i <= 4; i++) {
			Dispatch border = Dispatch.call(Borders, "Item", i).toDispatch();
			Dispatch.put(border, "Visible", flag);
		}
	}

	/**
	 * 设置指定图片大小
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
	 * 设置是显示页码
	 */
	public void setPageNumbers(boolean flag) {
		Dispatch sections = Dispatch.get(select, "Sections").toDispatch();
		Dispatch section = this.getItem(sections, 1);
		Dispatch foot = getItem(Dispatch.get(section, "Footers").toDispatch(), 1);
		Dispatch pageNumbers = Dispatch.get(foot, "PageNumbers").toDispatch();
		Dispatch.call(pageNumbers, "add", ALIGNMENT_CENTER, flag);
	}

	/**
	 * 得到一个表格的所有行
	 */
	public Dispatch getRows(int tableindex) {
		Dispatch rows = Dispatch.get(getTable(tableindex), "Rows").toDispatch();
		return rows;
	}

	/**
	 * 得到一行
	 */
	public Dispatch getRow(int tableindex, int rowNum) {
		return getItem(getRows(tableindex), rowNum);
	}

	/**
	 * 得到一表格所有列
	 */
	public Dispatch getCols(int tableindex) {
		return Dispatch.get(getTable(tableindex), "Columns").toDispatch();
	}

	/**
	 * 得到一列
	 */
	public Dispatch getCol(int tableindex, int colNum) {
		return getItem(getCols(tableindex), colNum);
	}

	/**
	 * 得到一个表格
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
	 * 使得一个对象获取select 很重要。
	 */
	public void getSelect(Dispatch dispatch) {
		Dispatch.call(dispatch, "Select");
	}

	/**
	 * 得到指定图片
	 */
	public Dispatch getImage(int img_index) {
		Dispatch dispatch = Dispatch.get(doc, "InlineShapes").toDispatch();
		return getItem(dispatch, img_index);
	}

	/**
	 * 得到指定图片的 宽和高
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
	 * 关闭当前文档
	 */
	public void closeDocument() {
		if (doc != null) {
			Dispatch.call(doc, "Save");
			Dispatch.call(doc, "Close", new Variant(true));
			doc = null;
		}
	}

	/**
	 * 关闭word应用程序
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
	 * 如果报错，需要强制关掉word应用 在catch捕捉异常中用
	 */
	public void exit() {
		Dispatch.call(doc, "Close", new Variant(false));
		Dispatch.call(word, "Quit");
	}
}
