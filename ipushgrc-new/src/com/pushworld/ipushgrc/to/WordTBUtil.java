package com.pushworld.ipushgrc.to;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.ui.common.UIUtil;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * 利用jacob合并两个word文件或替换word的内容
 * 
 * @author lcj
 * 
 */
public class WordTBUtil {

	private Dispatch doc = null;
	// word应用程序
	private ActiveXComponent word = null;
	// 光标位置 插入点
	private Dispatch select = null;
	// 创建文档集合
	private Dispatch documents = null;

	/**
	 * 
	 * @param _file1name
	 *            某部分内容
	 * @param _file2name
	 *            模板文件，该文件中要有特殊标识，来标识要替换处
	 * @param _filemark
	 *            将文件_file1name所有内容替换到_file2name中的某个标识处
	 * @param _textmap
	 *            文本替换的map,如key="$发布日期$",value="2011-05-24"
	 * @return
	 * @throws Exception 
	 */
	public void mergeOrReplaceFile(String _file1name, String _file2name, String _filemark, HashMap _textmap, String cmpfile_id) throws UnsatisfiedLinkError { // //
		try {
			ComThread.InitSTA();// 初始化线程,如果classpath没有jacob-1.15-M2-x86.dll和jacob-1.15-M2-x64.dll，这里会报错，需要抛出错误，在调用后捕获错误，进行其他操作【李春娟/2012-03-20】
		} catch (UnsatisfiedLinkError e) {
			throw e; //
		}
		word = new ActiveXComponent("Word.Application"); //
		word.setProperty("Visible", new Variant(false)); // //不显示,在后台运行!!!
		documents = word.getProperty("Documents").toDispatch(); //
		Dispatch doc1 = null;
		try {
			if (_file1name != null && !"".equals(_file1name) && _filemark != null && !"".equals(_filemark)) {// 如果设置了合并文件，则先将第一个文件打开并复制所有内容
				if (_file1name.contains("Documents and Settings")) {
					_file1name = _file1name.replaceAll("Documents and Settings", "DOCUME~1");//在客户端生成时，会找到代码缓存路径C:\Documents and Settings\myh\WEBLIGHT_CODECACHE，路径中有空格，会报错，故需要转换一下【李春娟/2012-11-01】
				}
				doc1 = Dispatch.call(documents, "Open", _file1name).toDispatch(); // 打开第一个文件!!!
				Dispatch shapes1 = Dispatch.get(doc1, "Content").toDispatch(); // 取得_file1name整个Word文件的内容!!!
				Dispatch.call(shapes1, "Copy"); // 将其中的所有数据拷贝下来,这将拷贝到系统的剪贴板中!!!
			}
			if (_file2name.contains("Documents and Settings")) {
				_file2name = _file2name.replaceAll("Documents and Settings", "DOCUME~1");//在客户端生成时，会找到代码缓存路径C:\Documents and Settings\myh\WEBLIGHT_CODECACHE，路径中有空格，会报错，故需要转换一下【李春娟/2012-11-01】
			}
			doc = Dispatch.call(documents, "Open", _file2name).toDispatch(); // 打开第二个文件!!!
			select = Dispatch.get(word, "Selection").toDispatch(); // 取得_file2name的光标所有位置,默认是在开始
			if (_file1name != null && !"".equals(_file1name) && _filemark != null && !"".equals(_filemark)) {// 如果设置了合并文件，则进行合并
				// 从_file2name的开始位置往后查找,直至找到标识的地方停下
				Dispatch find = ActiveXComponent.call(select, "Find").toDispatch(); // 执行查询操作!!
				Dispatch.put(find, "Text", _filemark); // 设置要查找的内容
				Dispatch.put(find, "Forward", "True"); // 向前查找
				Dispatch.put(find, "Format", "True"); // 设置格式
				Dispatch.put(find, "MatchCase", "True"); // 大小写匹配
				Dispatch.put(find, "MatchWholeWord", "True"); // 全字匹配
				boolean bo_iffind = Dispatch.call(find, "Execute").getBoolean(); // 查找并定位该位置!!
				if (bo_iffind) { // 如果找到,则将该位置的$xyz$内容替换掉.
					Dispatch textRange2 = Dispatch.get(select, "Range").toDispatch(); //
					Dispatch.call(textRange2, "Paste"); // 粘贴
				}
			}

			addOtherReplace(_textmap, cmpfile_id);//【李春娟/2014-09-22】

			// 从开始位置往后查找,直至找到标识的地方停下
			// 替换文本，如流程文件名称，编码，发布日期，生效日期，编制单位等
			openPageBrow();//如果页眉中也要替换，则需要先打开页眉，替换完后在关闭，因在打开页眉时不能替换word文本中的内容，故关闭后还需要再替换文本内容【李春娟/2012-05-09】
			onReplace(_textmap);
			closePageBrow();
			onReplace(_textmap);
			/* 插入版本号 */
			if (new TBUtil().getSysOptionBooleanValue("流程文件生成正文是否替换修改记录", true)) {
				try {
					int colCount = getTableColsCount(1); //得到表格的列的数目
					boolean ifhaveNOtable = false;
					if (colCount == 4) {//如果表格列数为四列，如序号、修改日期、修改原因和修改内容提示、版本号 才查找是否有版本号列，如果连列数都不同说明客户来维护修改记录，就没有必要再自动生成了【李春娟/2012-04-18】
						for (int i = 1; i <= colCount; i++) {
							String str = getCellString(1, 1, i);
							if (str != null && str.contains("版本号")) {
								ifhaveNOtable = true;
								break;
							}
						}
					}
					if (ifhaveNOtable) { //如果没有版本号这一表格，说明没有文件更新版本表格。禅城提出去掉此表格！
						HashVO[] versionVOs = null;
						//风险识别评估结束编辑的同时流程文件又是有效状态，则需要生成一个小版本号，小版本号不需要加入历史记录中，只有流程文件发布才加入！李春娟修改
						String sql = "select cmpfile_publishdate ,cmpfile_versionno  from cmp_cmpfile_hist  where cmpfile_id=" + cmpfile_id + "  and cmpfile_versionno not like '%._1'  order by cmpfile_publishdate,cmpfile_versionno";
						if (System.getProperty("JVMSITE") != null && System.getProperty("JVMSITE").equals("CLIENT")) {//如果是客户端执行
							versionVOs = new UIUtil().getHashVoArrayByDS(null, sql);
						} else {
							CommDMO dmo = new CommDMO();
							versionVOs = dmo.getHashVoArrayByDS(null, sql);
						}
						int rowCount = getTableRowsCount(1);
						if (rowCount < versionVOs.length + 1) {
							for (int i = 0; i < versionVOs.length + 1 - rowCount; i++) {
								insertRowBeforeLastRow(1);
							}
						}
						if (versionVOs.length > 0) {
							for (int i = 0; i < versionVOs.length; i++) {
								insertTextIntoCell(1, i + 2, 1, i + 1 + "");
								insertTextIntoCell(1, i + 2, 2, versionVOs[i].getStringValue("cmpfile_publishdate", ""));
								String versionno = versionVOs[i].getStringValue("cmpfile_versionno", "");
								if (versionno.contains(".")) {
									if (versionno.substring(versionno.indexOf(".")).length() > 2) {//如果包含小数点后两位，将百分位隐藏【李春娟/2012-03-14】
										versionno = versionno.substring(0, versionno.length() - 1);
									}
								} else {
									versionno += ".0";//如果查出来是整数，如2或3则手动添加小数点和十分位，oracle中存入"1.0"查出来是"1"【李春娟/2012-03-23】
								}
								insertTextIntoCell(1, i + 2, 4, versionno);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			//打开的复制的word关闭[YangQing/2013-09-09]
			Dispatch.call(doc1, "Close", new Variant(false));
			closeDocument();
			close();
		} catch (Exception e) {
			exit();
			e.printStackTrace();
		} finally {
			ComThread.Release(); // 退出线程
		}
	}

	//新增了五项替换：相关文件、相关表单、相关法规、相关制度、相关流程，这里应该随即统计一下查数据库快，还是先判断word里有没有“$相关表单$”标识再查询数据库快？
	//【李春娟/2014-09-22】
	private void addOtherReplace(HashMap _textmap, String cmpfile_id) throws WLTRemoteException, Exception {
		//1.替换相关文件（流程文件的属性）为显示内容
		Object object = _textmap.get("$相关文件$");
		if (object == null || "".equals(object.toString())) {
			_textmap.put("$相关文件$", "");
		} else {//格式为：/20140922/N9901_6F7261636C65B5BCB3F6CAFDBEDDBFE2.txt;/20140922/N9902_BACFB9E6CFB5CDB3B2E2CAD4CECACCE2.xlsx  
			String[] filenames = TBUtil.getTBUtil().split((String) object, ";");
			if (filenames == null || filenames.length == 0) {
				_textmap.put("$相关文件$", "");
			} else {
				String reffiles = "";
				for (int i = 0; i < filenames.length; i++) {
					reffiles += "《" + getViewFileName(filenames[i]) + "》\r\n";
				}
				_textmap.put("$相关文件$", reffiles.substring(0, reffiles.length() - 2));
			}

		}

		//2.替换相关表单（流程文件的属性）为显示内容【李春娟/2014-09-22】
		object = _textmap.get("$相关表单$");
		if (object == null || "".equals(object.toString())) {
			_textmap.put("$相关表单$", "");
		} else {//格式为：;233;2492;
			String[] forms = null;
			String sql = "select distinct(name) from bsd_form where id in(" + TBUtil.getTBUtil().getInCondition((String) object) + ")";
			if (System.getProperty("JVMSITE") != null && System.getProperty("JVMSITE").equals("CLIENT")) {//如果是客户端执行
				forms = new UIUtil().getStringArrayFirstColByDS(null, sql);
			} else {
				CommDMO dmo = new CommDMO();
				forms = dmo.getStringArrayFirstColByDS(null, sql);
			}

			if (forms == null || forms.length == 0) {
				_textmap.put("$相关表单$", "");
			} else {
				String formnames = "";
				for (int i = 0; i < forms.length; i++) {
					formnames += "《" + forms[i] + "》\r\n";
				}
				_textmap.put("$相关表单$", formnames.substring(0, formnames.length() - 2));
			}

		}

		//3.替换相关法规为显示内容
		String[] laws = null;
		String sql = "select distinct(law_name) from cmp_cmpfile_law where cmpfile_id =" + cmpfile_id;//以前只查询了流程相关的，觉得不全，故修改之【李春娟/2014-12-23】
		if (System.getProperty("JVMSITE") != null && System.getProperty("JVMSITE").equals("CLIENT")) {//如果是客户端执行
			laws = new UIUtil().getStringArrayFirstColByDS(null, sql);
		} else {
			CommDMO dmo = new CommDMO();
			laws = dmo.getStringArrayFirstColByDS(null, sql);
		}

		if (laws == null || laws.length == 0) {
			_textmap.put("$相关法规$", "");
		} else {
			String lawnames = "";
			for (int i = 0; i < laws.length; i++) {
				if (laws[i] != null && laws[i].contains("《")) {
					laws[i] = TBUtil.getTBUtil().replaceAll(laws[i], "《", "<");
					laws[i] = TBUtil.getTBUtil().replaceAll(laws[i], "》", ">");
				}
				lawnames += "《" + laws[i] + "》\r\n";
			}
			_textmap.put("$相关法规$", lawnames.substring(0, lawnames.length() - 2));
		}

		//4.替换相关制度为显示内容
		String[] rules = null;
		sql = "select distinct(rule_name) from cmp_cmpfile_rule where cmpfile_id =" + cmpfile_id;//以前只查询了流程相关的，觉得不全，故修改之【李春娟/2014-12-23】
		if (System.getProperty("JVMSITE") != null && System.getProperty("JVMSITE").equals("CLIENT")) {//如果是客户端执行
			rules = new UIUtil().getStringArrayFirstColByDS(null, sql);
		} else {
			CommDMO dmo = new CommDMO();
			rules = dmo.getStringArrayFirstColByDS(null, sql);
		}

		if (rules == null || rules.length == 0) {
			_textmap.put("$相关制度$", "");
		} else {
			String rulenames = "";
			for (int i = 0; i < rules.length; i++) {
				if (rules[i] != null && rules[i].contains("《")) {
					rules[i] = TBUtil.getTBUtil().replaceAll(rules[i], "《", "<");
					rules[i] = TBUtil.getTBUtil().replaceAll(rules[i], "》", ">");
				}
				rulenames += "《" + rules[i] + "》\r\n";
			}
			_textmap.put("$相关制度$", rulenames.substring(0, rulenames.length() - 2));
		}

		//5.替换相关流程（流程相关）为显示内容
		String[] process = null;
		sql = "select distinct(ref_wfprocess_name) from cmp_cmpfile_refwf where cmpfile_id =" + cmpfile_id;
		if (System.getProperty("JVMSITE") != null && System.getProperty("JVMSITE").equals("CLIENT")) {//如果是客户端执行
			process = new UIUtil().getStringArrayFirstColByDS(null, sql);
		} else {
			CommDMO dmo = new CommDMO();
			process = dmo.getStringArrayFirstColByDS(null, sql);
		}

		if (process == null || process.length == 0) {
			_textmap.put("$相关流程$", "");
		} else {
			String processnames = "";
			for (int i = 0; i < process.length; i++) {
				processnames += "《" + process[i] + "》\r\n";
			}
			_textmap.put("$相关流程$", processnames.substring(0, processnames.length() - 2));
		}

	}

	//取得显示的文件名!即去掉索引号【李春娟/2014-09-22】
	private String getViewFileName(String _realFileName) {
		if (_realFileName != null && _realFileName.indexOf("/") != -1) {
			String param = _realFileName.substring(_realFileName.lastIndexOf("/") + 1, _realFileName.length());
			if (param != null && param.startsWith("N")) {
				TBUtil tbUtil = new TBUtil(); //
				int li_extentNamePos = param.lastIndexOf("."); //文件的扩展名的位置!即必须有个点!但在兴业项目中有许多文件是从后台灌入的!!也遇到到没后辍的!!所以报错!
				if (li_extentNamePos > 0) {
					return tbUtil.convertHexStringToStr(param.substring(param.indexOf("_") + 1, li_extentNamePos)) + param.substring(li_extentNamePos, param.length()); //
				} else {
					return tbUtil.convertHexStringToStr(param.substring(param.indexOf("_") + 1, param.length())); ////
				}
			} else {
				return param; //以前的版本也有存路径的？
			}
		} else {
			if (_realFileName == null || _realFileName.indexOf("_") < 0) {
				return _realFileName; //
			}
			return _realFileName.substring(_realFileName.indexOf("_") + 1, _realFileName.length()); //
		}
	}

	/**
	 * 真正替换的逻辑
	 * @param _textmap 需要替换的内容
	 */

	private void onReplace(HashMap _textmap) {
		Iterator iter = _textmap.entrySet().iterator();
		while (iter.hasNext()) {
			select = Dispatch.get(word, "Selection").toDispatch(); // 取得光标所有位置,默认是在开始
			Dispatch.call(select, "HomeKey", new Variant(6));
			boolean iffind = true;
			Entry en = (Entry) iter.next();
			String key = (String) en.getKey();
			String value = (String) en.getValue();
			while (iffind) { //
				Dispatch find2 = ActiveXComponent.call(select, "Find").toDispatch();
				Dispatch.put(find2, "Text", key); // 设置要查找的内容
				Dispatch.put(find2, "Forward", "True"); // 向前查找
				Dispatch.put(find2, "Format", "True"); // 设置格式
				Dispatch.put(find2, "MatchCase", "True"); // 大小写匹配
				Dispatch.put(find2, "MatchWholeWord", "True"); // 全字品配
				iffind = Dispatch.call(find2, "Execute").getBoolean();
				if (iffind) {
					Dispatch.put(select, "Text", value);
					Dispatch.call(select, "MoveRight");
				}
			}
		}

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
	 * 以下是从科工导过来的。 在最后一行前插入一行
	 */
	public void insertRowBeforeLastRow(int tableIndex) {
		// 得到所有表
		Dispatch rows = Dispatch.get(getTable(tableIndex), "Rows").toDispatch(); // 所有行
		Dispatch row = Dispatch.get(rows, "Last").toDispatch(); // 最后一行
		Dispatch.call(rows, "Add", new Variant(row));
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

	public void insertColBeforeCol(int tableIndex, int colIndex) {
		Dispatch cols = Dispatch.get(getTable(tableIndex), "Columns").toDispatch(); // 所有列
		Dispatch col = getItem(cols, colIndex);
		Dispatch.call(cols, "Add", col);
	}

	/**
	 * 得到 指定表格内的内容。
	 * @param tableIndex
	 * @param cellRowIndex
	 * @param cellColIndex
	 * @return
	 */
	public String getCellString(int tableIndex, int cellRowIndex, int cellColIndex) {
		Dispatch cell = getCell(tableIndex, cellRowIndex, cellColIndex);
		Dispatch.call(cell, "Select");
		return Dispatch.get(select, "Text").toString();
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
	 * 得到一个表格的所有行
	 */
	public Dispatch getRows(int tableindex) {
		Dispatch rows = Dispatch.get(getTable(tableindex), "Rows").toDispatch();
		return rows;
	}

	/**
	 * 得到一个表格
	 */
	public Dispatch getCell(int tab_index, int row_index, int col_index) {
		return Dispatch.call(this.getTable(tab_index), "Cell", new Variant(row_index), new Variant(col_index)).toDispatch();
	}

	/**
	 * 得到一表格所有列
	 */
	public Dispatch getCols(int tableindex) {
		return Dispatch.get(getTable(tableindex), "Columns").toDispatch();
	}

	/**
	 * 得到指定表格有多少列
	 * @return
	 */
	public int getTableColsCount(int tableIndex) {
		return Dispatch.get(getCols(tableIndex), "Count").toInt();
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
		if (doc != null) {
			Dispatch.call(doc, "Close", new Variant(false));
		}
		Dispatch.call(word, "Quit");
	}
}
