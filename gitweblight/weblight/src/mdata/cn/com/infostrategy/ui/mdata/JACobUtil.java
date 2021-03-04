package cn.com.infostrategy.ui.mdata;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * 处理Word的插件!!!!
 * @author xch
 *
 */
public class JACobUtil {

	/**
	 * 将文件1中的所有内容都拷贝出来粘贴到文件2对应的特定标识符的地方中去!!!
	 */
	public void mergeTwoFile(String[] _file1name, String _file2name, String[] _mark2) { ////
		long ll_1 = System.currentTimeMillis(); //
		ComThread.InitSTA();
		ActiveXComponent wordActivex = new ActiveXComponent("Word.Application"); //
		wordActivex.setProperty("Visible", new Variant(false)); ////不显示,在后台运行!!!
		Dispatch wordDocument = wordActivex.getProperty("Documents").toDispatch(); //
		Dispatch doc2 = Dispatch.call(wordDocument, "Open", _file2name).toDispatch(); //打开第二个文件!!!
		Dispatch selection2 = Dispatch.get(wordActivex, "Selection").toDispatch(); //取得光标所有位置,默认是在开始
		try {
			for (int i = 0; i < _file1name.length; i++) { //分别打开各个源文件..
				System.out.println("fileName[" + _file1name[i] + "]"); //
				Dispatch doc1 = Dispatch.call(wordDocument, "Open", _file1name[i]).toDispatch(); //打开第一个文件!!!
				Dispatch shapes1 = Dispatch.get(doc1, "Content").toDispatch(); //取得整个Word文件的内容!!!
				Dispatch.call(shapes1, "Copy"); //将其中的所有数据拷贝下来,这将拷贝到系统的剪贴板中!!!

				//从开始位置往后查找,直至找到标识的地方停下
				Dispatch find = ActiveXComponent.call(selection2, "Find").toDispatch(); //执行查询操作!!
				Dispatch.put(find, "Text", _mark2[i]); // 设置要查找的内容
				Dispatch.put(find, "Forward", "True"); // 向前查找
				Dispatch.put(find, "Format", "True"); // 设置格式
				Dispatch.put(find, "MatchCase", "True"); // 大小写匹配
				Dispatch.put(find, "MatchWholeWord", "True"); // 全字匹配
				boolean bo_iffind = Dispatch.call(find, "Execute").getBoolean(); //查找并定位该位置!!

				if (bo_iffind) { //如果找到,则将该位置的${{{xyz}}}$内容替换掉.
					Dispatch textRange2 = Dispatch.get(selection2, "Range").toDispatch(); //
					Dispatch.call(textRange2, "Paste"); //
					//Dispatch.call(doc2, "SaveAs", new Variant("C:/zzz.doc")); //将替换后的另存为一个新文件.
				}
			}

			Dispatch.call(doc2, "Save"); //直接保存目标文件!!!
			Dispatch.call(wordActivex, "Quit"); //退出
		} catch (Exception e) {
			Dispatch.call(wordActivex, "Quit"); //退出
			e.printStackTrace();
		} finally {
			ComThread.Release(); //退出线程
		}
		long ll_2 = System.currentTimeMillis(); //
		System.out.println("合并文件成功,共耗时[" + (ll_2 - ll_1) + "]!!"); //
	}

	/**
	 * 将一个Word文件中的某个纯文本内容替换成新的文本!!!
	 * @param _filename 文件名,比如:C:/abc.doc
	 * @param _oldtext 原来的内容,比如:code
	 * @param _newtext 替换的内容,比如:zhangshang
	 */
	public void replaceWordFileText(String _filename, String _oldtext, String _newtext) {
		ComThread.InitSTA();
		ActiveXComponent wordActivex = new ActiveXComponent("Word.Application"); //
		wordActivex.setProperty("Visible", new Variant(false)); ////不显示,在后台运行!!!
		Dispatch wordDocument = wordActivex.getProperty("Documents").toDispatch(); //
		Dispatch doc = Dispatch.call(wordDocument, "Open", _filename).toDispatch(); //打开第二个文件!!!
		Dispatch selection = Dispatch.get(wordActivex, "Selection").toDispatch(); //取得光标所有位置,默认是在开始

		boolean iffind = true;
		while (iffind) { //
			Dispatch find = wordActivex.call(selection, "Find").toDispatch();
			Dispatch.put(find, "Text", _oldtext); // 设置要查找的内容
			Dispatch.put(find, "Forward", "True"); // 向前查找
			Dispatch.put(find, "Format", "True"); // 设置格式
			Dispatch.put(find, "MatchCase", "True"); // 大小写匹配
			Dispatch.put(find, "MatchWholeWord", "True"); //全字品配
			iffind = Dispatch.call(find, "Execute").getBoolean();
			if (iffind) {
				Dispatch.put(selection, "Text", _newtext);
				Dispatch.call(selection, "MoveRight");
			}
		}

		Dispatch.call(doc, "Save"); //直接保存目标文件!!!
		Dispatch.call(wordActivex, "Quit"); //退出
		ComThread.Release(); //退出线程
	}

	/**
	 * 将一个Word文件中的某些纯文本内容替换成新的文本!!!
	 * @param _filename 文件名,比如:C:/abc.doc
	 * @param _oldtext 原来的内容,比如:{code}code
	 * @param _newtext 替换的内容,比如:{zhangshang}zhangshang
	 */
	public void replaceWordFileText(String _filename, String[] _oldtext, String[] _newtext) {
		ComThread.InitSTA();
		ActiveXComponent wordActivex = new ActiveXComponent("Word.Application"); //
		wordActivex.setProperty("Visible", new Variant(false)); ////不显示,在后台运行!!!
		Dispatch wordDocument = wordActivex.getProperty("Documents").toDispatch(); //
		Dispatch doc = Dispatch.call(wordDocument, "Open", _filename).toDispatch(); //打开第二个文件!!!
		Dispatch selection = Dispatch.get(wordActivex, "Selection").toDispatch(); //取得光标所有位置,默认是在开始
		for (int i = 0; i < _oldtext.length; i++) {
			boolean iffind = true;
			while (iffind) { //
				Dispatch find = wordActivex.call(selection, "Find").toDispatch();
				Dispatch.put(find, "Text", _oldtext[i]); // 设置要查找的内容
				Dispatch.put(find, "Forward", "True"); // 向前查找
				Dispatch.put(find, "Format", "True"); // 设置格式
				Dispatch.put(find, "MatchCase", "True"); // 大小写匹配
				Dispatch.put(find, "MatchWholeWord", "True"); //全字品配
				iffind = Dispatch.call(find, "Execute").getBoolean();
				if (iffind) {
					Dispatch.put(selection, "Text", _newtext[i]);
					Dispatch.call(selection, "MoveRight");
				}
			}
		}
		Dispatch.call(doc, "Save"); //直接保存目标文件!!!
		Dispatch.call(wordActivex, "Quit"); //退出
		ComThread.Release(); //退出线程
	}

	/** sunfujun/20130514/WORD另存为其他格式的api
	 * @param FolderPath 目录
	 * @param oldfilname 需要另存为操作的文件名
	 * @param newfilename 另存为的文件名
	 * @param variant 类型
	 * 大致:0-doc;1-dot;2-5-txt;6-rtf;7-txt;8\10-htm;11-xml;12\16-docx;13-docm;14-dotx;15-dotm;17-pdf
	 * @return
	 */
	public boolean change(String FolderPath, String oldfilname, String newfilename, int variant) {
		String FileFormat = "";
		FileFormat = oldfilname.substring(oldfilname.lastIndexOf("."), oldfilname.length());
		if (FileFormat.equalsIgnoreCase(".doc") || FileFormat.equalsIgnoreCase(".docx")) {
			String DocFile = FolderPath + "\\" + oldfilname;
			System.out.println("word文件路径：" + DocFile);
			//word文件的完整路径
			String HtmlFile = FolderPath + "\\" + newfilename;
			System.out.println("另存为的文件路径：" + HtmlFile);
			ActiveXComponent app = new ActiveXComponent("Word.Application");
			//启动word
			try {
				app.setProperty("Visible", new Variant(false));
				//设置word程序非可视化运行
				Dispatch docs = app.getProperty("Documents").toDispatch();
				Dispatch doc = Dispatch.invoke(docs, "Open", Dispatch.Method, new Object[] { DocFile, new Variant(false), new Variant(true) }, new int[1]).toDispatch();
				//打开word文件
				Dispatch.invoke(doc, "SaveAs", Dispatch.Method, new Object[] { HtmlFile, new Variant(variant) }, new int[1]);
				//作为htm格式保存文件
				Dispatch.call(doc, "Close", new Variant(false));
				//关闭文件
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				app.invoke("Quit", new Variant[] {});
				//退出word程序
			}
			//转化完毕
			return true;
		}
		return false;
	}

	/**
	 * 支持word后来增加wps的支持 sunfujun/20130326
	 * @param FolderPath
	 * @param FileName
	 * @return
	 */
	public static boolean change(String FolderPath, String FileName) {
		String FileFormat = "";
		System.out.println(FolderPath);
		FileFormat = FileName.substring(FileName.lastIndexOf("."), FileName.length());
		System.out.println(FileFormat);
		String DocFile = FolderPath + "\\" + FileName;
		String HtmlFile = null;
		String application = "Word.Application";
		if (FileFormat.equalsIgnoreCase(".doc") || FileFormat.equalsIgnoreCase(".docx")) {
			System.out.println("word文件路径：" + DocFile); //word文件的完整路径
			HtmlFile = DocFile.substring(0, DocFile.lastIndexOf(".")) + ".html"; //html文件的完整路径
		} else if (FileFormat.equalsIgnoreCase(".wps")) {
			System.out.println("wps文件路径：" + DocFile); //word文件的完整路径
			HtmlFile = DocFile.substring(0, DocFile.lastIndexOf(".")) + ".html"; //html文件的完整路径
			application = "Wps.Application";
		} else {
			return false;
		}
		System.out.println("htm文件路径：" + HtmlFile);
		ActiveXComponent app = new ActiveXComponent(application);
		//启动程序
		try {
			app.setProperty("Visible", new Variant(false));
			//设置word程序非可视化运行
			Dispatch docs = app.getProperty("Documents").toDispatch();
			Dispatch doc = Dispatch.invoke(docs, "Open", Dispatch.Method, new Object[] { DocFile, new Variant(false), new Variant(true) }, new int[1]).toDispatch();
			//打开word文件
			Dispatch.invoke(doc, "SaveAs", Dispatch.Method, new Object[] { HtmlFile, new Variant(8) }, new int[1]);
			//作为htm格式保存文件
			Dispatch.call(doc, "Close", new Variant(false));
			//关闭文件
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			app.invoke("Quit", new Variant[] {});
			//退出word程序
		}
		//转化完毕
		return true;
	}

	/**
	 * WORD文档比较比较厉害 sunfujun
	 * @param filename1 文件1路径
	 * @param filename2 文件2路径
	 * @param cmppareresult 比较结果存放文件的路径
	 * 都是word文件
	 */
	public void compare(String filename1, String filename2, String cmppareresult) {
		ComThread.InitSTA();
		ActiveXComponent wordActivex = null;
		try {
			wordActivex = new ActiveXComponent("Word.Application"); //
			wordActivex.setProperty("Visible", new Variant(false)); //不显示,在后台运行!!!
			Dispatch wordDocument = wordActivex.getProperty("Documents").toDispatch(); //
			Dispatch doc = Dispatch.call(wordDocument, "Open", filename1).toDispatch();
			Dispatch doc2 = Dispatch.call(wordDocument, "Open", filename2).toDispatch();
			Dispatch compare = Dispatch.call(wordActivex, "CompareDocuments", doc, doc2).toDispatch(); //取得光标所有位置,默认是在开始
			Dispatch.invoke(compare, "SaveAs", Dispatch.Method, new Object[] { cmppareresult, new Variant(0) }, new int[1]);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (wordActivex != null) {
				Dispatch.call(wordActivex, "Quit"); //退出
			}
			ComThread.Release(); //退出线程
		}
	}

	public static void main(String[] args) {
		JACobUtil util = new JACobUtil(); //
		//util.mergeTwoFile(new String[] { "C:\\aaa.doc", "C:\\bbb.doc" }, "C:\\ccc.doc", new String[] { "{aaa}", "{bbb}" }); //
		//        util.replaceWordFileText("C:/aaa.doc", "示范", "ABCD123123123BBB");
		util.mergeTwoFile(new String[] { "C:\\abc.rtf" }, "http://127.0.0.1:9002/ipush/DownLoadFileServlet?pathtype=office&filename=N1281_616161.doc", new String[] { "{AAA}", "{bbb}" }); //
	}
}
