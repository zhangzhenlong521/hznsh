package cn.com.infostrategy.ui.mdata;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * ����Word�Ĳ��!!!!
 * @author xch
 *
 */
public class JACobUtil {

	/**
	 * ���ļ�1�е��������ݶ���������ճ�����ļ�2��Ӧ���ض���ʶ���ĵط���ȥ!!!
	 */
	public void mergeTwoFile(String[] _file1name, String _file2name, String[] _mark2) { ////
		long ll_1 = System.currentTimeMillis(); //
		ComThread.InitSTA();
		ActiveXComponent wordActivex = new ActiveXComponent("Word.Application"); //
		wordActivex.setProperty("Visible", new Variant(false)); ////����ʾ,�ں�̨����!!!
		Dispatch wordDocument = wordActivex.getProperty("Documents").toDispatch(); //
		Dispatch doc2 = Dispatch.call(wordDocument, "Open", _file2name).toDispatch(); //�򿪵ڶ����ļ�!!!
		Dispatch selection2 = Dispatch.get(wordActivex, "Selection").toDispatch(); //ȡ�ù������λ��,Ĭ�����ڿ�ʼ
		try {
			for (int i = 0; i < _file1name.length; i++) { //�ֱ�򿪸���Դ�ļ�..
				System.out.println("fileName[" + _file1name[i] + "]"); //
				Dispatch doc1 = Dispatch.call(wordDocument, "Open", _file1name[i]).toDispatch(); //�򿪵�һ���ļ�!!!
				Dispatch shapes1 = Dispatch.get(doc1, "Content").toDispatch(); //ȡ������Word�ļ�������!!!
				Dispatch.call(shapes1, "Copy"); //�����е��������ݿ�������,�⽫������ϵͳ�ļ�������!!!

				//�ӿ�ʼλ���������,ֱ���ҵ���ʶ�ĵط�ͣ��
				Dispatch find = ActiveXComponent.call(selection2, "Find").toDispatch(); //ִ�в�ѯ����!!
				Dispatch.put(find, "Text", _mark2[i]); // ����Ҫ���ҵ�����
				Dispatch.put(find, "Forward", "True"); // ��ǰ����
				Dispatch.put(find, "Format", "True"); // ���ø�ʽ
				Dispatch.put(find, "MatchCase", "True"); // ��Сдƥ��
				Dispatch.put(find, "MatchWholeWord", "True"); // ȫ��ƥ��
				boolean bo_iffind = Dispatch.call(find, "Execute").getBoolean(); //���Ҳ���λ��λ��!!

				if (bo_iffind) { //����ҵ�,�򽫸�λ�õ�${{{xyz}}}$�����滻��.
					Dispatch textRange2 = Dispatch.get(selection2, "Range").toDispatch(); //
					Dispatch.call(textRange2, "Paste"); //
					//Dispatch.call(doc2, "SaveAs", new Variant("C:/zzz.doc")); //���滻������Ϊһ�����ļ�.
				}
			}

			Dispatch.call(doc2, "Save"); //ֱ�ӱ���Ŀ���ļ�!!!
			Dispatch.call(wordActivex, "Quit"); //�˳�
		} catch (Exception e) {
			Dispatch.call(wordActivex, "Quit"); //�˳�
			e.printStackTrace();
		} finally {
			ComThread.Release(); //�˳��߳�
		}
		long ll_2 = System.currentTimeMillis(); //
		System.out.println("�ϲ��ļ��ɹ�,����ʱ[" + (ll_2 - ll_1) + "]!!"); //
	}

	/**
	 * ��һ��Word�ļ��е�ĳ�����ı������滻���µ��ı�!!!
	 * @param _filename �ļ���,����:C:/abc.doc
	 * @param _oldtext ԭ��������,����:code
	 * @param _newtext �滻������,����:zhangshang
	 */
	public void replaceWordFileText(String _filename, String _oldtext, String _newtext) {
		ComThread.InitSTA();
		ActiveXComponent wordActivex = new ActiveXComponent("Word.Application"); //
		wordActivex.setProperty("Visible", new Variant(false)); ////����ʾ,�ں�̨����!!!
		Dispatch wordDocument = wordActivex.getProperty("Documents").toDispatch(); //
		Dispatch doc = Dispatch.call(wordDocument, "Open", _filename).toDispatch(); //�򿪵ڶ����ļ�!!!
		Dispatch selection = Dispatch.get(wordActivex, "Selection").toDispatch(); //ȡ�ù������λ��,Ĭ�����ڿ�ʼ

		boolean iffind = true;
		while (iffind) { //
			Dispatch find = wordActivex.call(selection, "Find").toDispatch();
			Dispatch.put(find, "Text", _oldtext); // ����Ҫ���ҵ�����
			Dispatch.put(find, "Forward", "True"); // ��ǰ����
			Dispatch.put(find, "Format", "True"); // ���ø�ʽ
			Dispatch.put(find, "MatchCase", "True"); // ��Сдƥ��
			Dispatch.put(find, "MatchWholeWord", "True"); //ȫ��Ʒ��
			iffind = Dispatch.call(find, "Execute").getBoolean();
			if (iffind) {
				Dispatch.put(selection, "Text", _newtext);
				Dispatch.call(selection, "MoveRight");
			}
		}

		Dispatch.call(doc, "Save"); //ֱ�ӱ���Ŀ���ļ�!!!
		Dispatch.call(wordActivex, "Quit"); //�˳�
		ComThread.Release(); //�˳��߳�
	}

	/**
	 * ��һ��Word�ļ��е�ĳЩ���ı������滻���µ��ı�!!!
	 * @param _filename �ļ���,����:C:/abc.doc
	 * @param _oldtext ԭ��������,����:{code}code
	 * @param _newtext �滻������,����:{zhangshang}zhangshang
	 */
	public void replaceWordFileText(String _filename, String[] _oldtext, String[] _newtext) {
		ComThread.InitSTA();
		ActiveXComponent wordActivex = new ActiveXComponent("Word.Application"); //
		wordActivex.setProperty("Visible", new Variant(false)); ////����ʾ,�ں�̨����!!!
		Dispatch wordDocument = wordActivex.getProperty("Documents").toDispatch(); //
		Dispatch doc = Dispatch.call(wordDocument, "Open", _filename).toDispatch(); //�򿪵ڶ����ļ�!!!
		Dispatch selection = Dispatch.get(wordActivex, "Selection").toDispatch(); //ȡ�ù������λ��,Ĭ�����ڿ�ʼ
		for (int i = 0; i < _oldtext.length; i++) {
			boolean iffind = true;
			while (iffind) { //
				Dispatch find = wordActivex.call(selection, "Find").toDispatch();
				Dispatch.put(find, "Text", _oldtext[i]); // ����Ҫ���ҵ�����
				Dispatch.put(find, "Forward", "True"); // ��ǰ����
				Dispatch.put(find, "Format", "True"); // ���ø�ʽ
				Dispatch.put(find, "MatchCase", "True"); // ��Сдƥ��
				Dispatch.put(find, "MatchWholeWord", "True"); //ȫ��Ʒ��
				iffind = Dispatch.call(find, "Execute").getBoolean();
				if (iffind) {
					Dispatch.put(selection, "Text", _newtext[i]);
					Dispatch.call(selection, "MoveRight");
				}
			}
		}
		Dispatch.call(doc, "Save"); //ֱ�ӱ���Ŀ���ļ�!!!
		Dispatch.call(wordActivex, "Quit"); //�˳�
		ComThread.Release(); //�˳��߳�
	}

	/** sunfujun/20130514/WORD���Ϊ������ʽ��api
	 * @param FolderPath Ŀ¼
	 * @param oldfilname ��Ҫ���Ϊ�������ļ���
	 * @param newfilename ���Ϊ���ļ���
	 * @param variant ����
	 * ����:0-doc;1-dot;2-5-txt;6-rtf;7-txt;8\10-htm;11-xml;12\16-docx;13-docm;14-dotx;15-dotm;17-pdf
	 * @return
	 */
	public boolean change(String FolderPath, String oldfilname, String newfilename, int variant) {
		String FileFormat = "";
		FileFormat = oldfilname.substring(oldfilname.lastIndexOf("."), oldfilname.length());
		if (FileFormat.equalsIgnoreCase(".doc") || FileFormat.equalsIgnoreCase(".docx")) {
			String DocFile = FolderPath + "\\" + oldfilname;
			System.out.println("word�ļ�·����" + DocFile);
			//word�ļ�������·��
			String HtmlFile = FolderPath + "\\" + newfilename;
			System.out.println("���Ϊ���ļ�·����" + HtmlFile);
			ActiveXComponent app = new ActiveXComponent("Word.Application");
			//����word
			try {
				app.setProperty("Visible", new Variant(false));
				//����word����ǿ��ӻ�����
				Dispatch docs = app.getProperty("Documents").toDispatch();
				Dispatch doc = Dispatch.invoke(docs, "Open", Dispatch.Method, new Object[] { DocFile, new Variant(false), new Variant(true) }, new int[1]).toDispatch();
				//��word�ļ�
				Dispatch.invoke(doc, "SaveAs", Dispatch.Method, new Object[] { HtmlFile, new Variant(variant) }, new int[1]);
				//��Ϊhtm��ʽ�����ļ�
				Dispatch.call(doc, "Close", new Variant(false));
				//�ر��ļ�
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				app.invoke("Quit", new Variant[] {});
				//�˳�word����
			}
			//ת�����
			return true;
		}
		return false;
	}

	/**
	 * ֧��word��������wps��֧�� sunfujun/20130326
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
			System.out.println("word�ļ�·����" + DocFile); //word�ļ�������·��
			HtmlFile = DocFile.substring(0, DocFile.lastIndexOf(".")) + ".html"; //html�ļ�������·��
		} else if (FileFormat.equalsIgnoreCase(".wps")) {
			System.out.println("wps�ļ�·����" + DocFile); //word�ļ�������·��
			HtmlFile = DocFile.substring(0, DocFile.lastIndexOf(".")) + ".html"; //html�ļ�������·��
			application = "Wps.Application";
		} else {
			return false;
		}
		System.out.println("htm�ļ�·����" + HtmlFile);
		ActiveXComponent app = new ActiveXComponent(application);
		//��������
		try {
			app.setProperty("Visible", new Variant(false));
			//����word����ǿ��ӻ�����
			Dispatch docs = app.getProperty("Documents").toDispatch();
			Dispatch doc = Dispatch.invoke(docs, "Open", Dispatch.Method, new Object[] { DocFile, new Variant(false), new Variant(true) }, new int[1]).toDispatch();
			//��word�ļ�
			Dispatch.invoke(doc, "SaveAs", Dispatch.Method, new Object[] { HtmlFile, new Variant(8) }, new int[1]);
			//��Ϊhtm��ʽ�����ļ�
			Dispatch.call(doc, "Close", new Variant(false));
			//�ر��ļ�
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			app.invoke("Quit", new Variant[] {});
			//�˳�word����
		}
		//ת�����
		return true;
	}

	/**
	 * WORD�ĵ��ȽϱȽ����� sunfujun
	 * @param filename1 �ļ�1·��
	 * @param filename2 �ļ�2·��
	 * @param cmppareresult �ȽϽ������ļ���·��
	 * ����word�ļ�
	 */
	public void compare(String filename1, String filename2, String cmppareresult) {
		ComThread.InitSTA();
		ActiveXComponent wordActivex = null;
		try {
			wordActivex = new ActiveXComponent("Word.Application"); //
			wordActivex.setProperty("Visible", new Variant(false)); //����ʾ,�ں�̨����!!!
			Dispatch wordDocument = wordActivex.getProperty("Documents").toDispatch(); //
			Dispatch doc = Dispatch.call(wordDocument, "Open", filename1).toDispatch();
			Dispatch doc2 = Dispatch.call(wordDocument, "Open", filename2).toDispatch();
			Dispatch compare = Dispatch.call(wordActivex, "CompareDocuments", doc, doc2).toDispatch(); //ȡ�ù������λ��,Ĭ�����ڿ�ʼ
			Dispatch.invoke(compare, "SaveAs", Dispatch.Method, new Object[] { cmppareresult, new Variant(0) }, new int[1]);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (wordActivex != null) {
				Dispatch.call(wordActivex, "Quit"); //�˳�
			}
			ComThread.Release(); //�˳��߳�
		}
	}

	public static void main(String[] args) {
		JACobUtil util = new JACobUtil(); //
		//util.mergeTwoFile(new String[] { "C:\\aaa.doc", "C:\\bbb.doc" }, "C:\\ccc.doc", new String[] { "{aaa}", "{bbb}" }); //
		//        util.replaceWordFileText("C:/aaa.doc", "ʾ��", "ABCD123123123BBB");
		util.mergeTwoFile(new String[] { "C:\\abc.rtf" }, "http://127.0.0.1:9002/ipush/DownLoadFileServlet?pathtype=office&filename=N1281_616161.doc", new String[] { "{AAA}", "{bbb}" }); //
	}
}
