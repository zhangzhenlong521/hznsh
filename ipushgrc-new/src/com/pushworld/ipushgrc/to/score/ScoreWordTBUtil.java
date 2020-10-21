package com.pushworld.ipushgrc.to.score;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

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
public class ScoreWordTBUtil {

	private Dispatch doc = null;
	// word应用程序
	private ActiveXComponent word = null;
	// 光标位置 插入点
	private Dispatch select = null;
	// 创建文档集合
	private Dispatch documents = null;

	/**
	 * 替换word模板中的标记符
	 * */
	public void replaceScoreFile(String filename, HashMap _textmap) throws UnsatisfiedLinkError { // //
		try {
			ComThread.InitSTA();// 初始化线程,如果classpath没有jacob-1.15-M2-x86.dll和jacob-1.15-M2-x64.dll，这里会报错，需要抛出错误，在调用后捕获错误，进行其他操作【李春娟/2012-03-20】
		} catch (UnsatisfiedLinkError e) {
			throw e; //
		}
		word = new ActiveXComponent("Word.Application"); //
		word.setProperty("Visible", new Variant(false)); // //不显示,在后台运行!!!
		documents = word.getProperty("Documents").toDispatch(); //
		try {
			if (filename.contains("Documents and Settings")) {
				filename = filename.replaceAll("Documents and Settings", "DOCUME~1");//在客户端生成时，会找到代码缓存路径C:\Documents and Settings\myh\WEBLIGHT_CODECACHE，路径中有空格，会报错，故需要转换一下【李春娟/2012-11-01】
			}
			doc = Dispatch.call(documents, "Open", filename).toDispatch(); // 打开文件!!!
			select = Dispatch.get(word, "Selection").toDispatch(); // 取得filename的光标所有位置,默认是在开始
			onReplace(_textmap);//替换相关数据
			closeDocument();
			close();
		} catch (Exception e) {
			exit();
			e.printStackTrace();
		} finally {
			ComThread.Release(); // 退出线程
		}
	}

	/**
	 * 合并多个word文件
	 * **/
	public String mergeScoreWord(List list) {
		String filename = null;
		try {
			ComThread.InitSTA();// 初始化线程,如果classpath没有jacob-1.15-M2-x86.dll和jacob-1.15-M2-x64.dll，这里会报错，需要抛出错误，在调用后捕获错误，进行其他操作【李春娟/2012-03-20】
		} catch (UnsatisfiedLinkError e) {
			throw e; //
		}
		word = new ActiveXComponent("Word.Application"); //
		word.setProperty("Visible", new Variant(false)); // //不显示,在后台运行!!!
		documents = word.getProperty("Documents").toDispatch(); //
		try {
			if (list != null) {
				filename = list.get(0).toString();
				if (filename.contains("Documents and Settings")) {
					filename = filename.replaceAll("Documents and Settings", "DOCUME~1");//在客户端生成时，会找到代码缓存路径C:\Documents and Settings\myh\WEBLIGHT_CODECACHE，路径中有空格，会报错，故需要转换一下【李春娟/2012-11-01】
				}
				doc = Dispatch.call(documents, "Open", filename).toDispatch(); // 打开第一个文件!!!
				select = Dispatch.get(word, "Selection").toDispatch(); // 取得filename的光标所有位置,默认是在开始
				Dispatch.call(select, "EndKey", new Variant(6));//将光标移到最后
				for (int i = 1; i < list.size(); i++) {
					String _file1name = list.get(i).toString();
					if (_file1name != null && !"".equals(_file1name)) {// 如果设置了合并文件，则先将第一个文件打开并复制所有内容
						if (_file1name.contains("Documents and Settings")) {
							_file1name = _file1name.replaceAll("Documents and Settings", "DOCUME~1");//在客户端生成时，会找到代码缓存路径C:\Documents and Settings\myh\WEBLIGHT_CODECACHE，路径中有空格，会报错，故需要转换一下【李春娟/2012-11-01】
						}
						Dispatch doc1 = Dispatch.call(documents, "Open", _file1name).toDispatch(); // 打开文件!!!
						Dispatch shapes1 = Dispatch.get(doc1, "Content").toDispatch(); // 取得_file1name整个Word文件的内容!!!
						Dispatch.call(shapes1, "Copy"); // 将其中的所有数据拷贝下来,这将拷贝到系统的剪贴板中!!!
						Dispatch.call(select, "EndKey", new Variant(6));//将光标定位到文本最后
						Dispatch.call(select, "InsertBreak", new Variant(7));//插入空白页
						Dispatch.call(select, "Paste"); // 粘贴
					}
				}
				closeDocument();
				close();
				return filename;
			}
		} catch (Exception e) {
			exit();
			e.printStackTrace();
		} finally {
			ComThread.Release(); // 退出线程
			return filename;
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
