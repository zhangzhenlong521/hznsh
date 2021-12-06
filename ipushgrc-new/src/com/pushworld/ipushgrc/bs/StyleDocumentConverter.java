package com.pushworld.ipushgrc.bs;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;

import com.lowagie.text.Chapter;
import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;

/***
 * 富文本转换类, 应该支持html, word格式输出
 * @author Gwang
 *
 */
public class StyleDocumentConverter {
	private Document doc;

	public StyleDocumentConverter() {
	}

	public StyleDocumentConverter(Document doc) {
		this.doc = doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

	private static final StringBuffer htmlHeader = new StringBuffer();
	private static final StringBuffer htmlCSS = new StringBuffer();
	private static final StringBuffer htmlFooter = new StringBuffer();

	static {
		htmlCSS.append("<style>\r\n");
		htmlCSS.append("  body{background-color:#FFFFFF; margin:0; font-size:12px; font-family:'宋体', arial;\r\n");
		htmlCSS.append("  .bold{font-weight: bold;}\r\n");
		htmlCSS.append("  .italic{font-style: italic;}\r\n");
		htmlCSS.append("  .underline{text-decoration:underline;}\r\n");
		for (int i = 8, n = 40; i <= n; i += 2) {
			htmlCSS.append("  .font" + i + "{FONT-SIZE: " + i + "px;}\r\n");
		}
		htmlCSS.append("</style>\r\n");

		htmlHeader.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\r\n");
		htmlHeader.append("<HTML>\r\n");
		htmlHeader.append("<HEAD>\r\n");
		htmlHeader.append("<TITLE>Style Document</TITLE>\r\n");
		htmlHeader.append("<META NAME=\"Generator\" CONTENT=\"StyleDocumentConverter\">\r\n");
		htmlHeader.append("<META NAME=\"Author\" CONTENT=\"Gwang\">\r\n");
		htmlHeader.append(htmlCSS);
		htmlHeader.append("</HEAD>\r\n");
		htmlHeader.append("<BODY>\r\n");

		htmlFooter.append("</BODY>\r\n");
		htmlFooter.append("</HTML>\r\n");
	}

	public static String getHeader() {
		return htmlHeader.toString();
	}

	public static String getFooter() {
		return htmlFooter.toString();
	}

	public static String getCSS() {
		return htmlCSS.toString();
	}

	public Chapter toWord(Chapter returnChapter) {
		Element[] root = doc.getRootElements();
		Element section = root[0];
		Element paragraph = null;
		Element content = null;
		Paragraph tempParagraph = null;
		for (int i = 0, n = section.getElementCount(); i < n; i++) {
			paragraph = section.getElement(i);
			tempParagraph = (Paragraph) this.getWordObject(paragraph);
			for (int j = 0, nn = paragraph.getElementCount(); j < nn; j++) {
				content = paragraph.getElement(j);
				tempParagraph.add(this.getWordObject(content));
			}
			returnChapter.add(tempParagraph);
		}
		return returnChapter;
	}

	/**
	 * 格式解析
	 * @param doc
	 */
	public String toHtml() {
		Element[] root = doc.getRootElements();
		Element section = root[0];
		Element paragraph = null;
		Element content = null;

		StringBuffer html = new StringBuffer();
		for (int i = 0, n = section.getElementCount(); i < n; i++) {
			paragraph = section.getElement(i);
			html.append(this.getHtmlString(paragraph));
			for (int j = 0, nn = paragraph.getElementCount(); j < nn; j++) {
				content = paragraph.getElement(j);
				html.append(this.getHtmlString(content));
			}
			html.append("</p>\r\n");
		}
		return html.toString();
	}

	public String toHtmlFully() {
		StringBuffer html = new StringBuffer();
		html.append(htmlHeader);
		html.append(this.toHtml());
		html.append(htmlFooter);
		return html.toString();
	}

	/**
	 * 转换成html代码
	 * @param e
	 * @return
	 */
	private String getHtmlString(Element e) {
		StringBuffer html = new StringBuffer("");
		ArrayList<String> classList = new ArrayList();

		String eName = e.getName();
		if (eName.equals("paragraph")) {
			html.append("<p");
		} else if (eName.equals("content")) {
			// 没有font属性时不输出font标签
		} else {
			// 不支持的属性, 比如图片...
			return "unknow type: " + eName;
		}

		// 取字体的风格属性
		String att = "";
		AttributeSet attset = e.getAttributes().copyAttributes();
		if (attset != null) {
			Enumeration names = attset.getAttributeNames();

			while (names.hasMoreElements()) {
				Object nextName = names.nextElement();
				if (nextName != StyleConstants.ResolveAttribute) {
					//javaStyle.append(" ");
					//javaStyle.append(nextName);
					//javaStyle.append("=");
					//javaStyle.append(attset.getAttribute(nextName));
					att = this.getHtmlTag(nextName.toString(), attset.getAttribute(nextName).toString());
					if (att.equals("")) {
						//
					} else if (att.startsWith("<font")) {//.fontX{FONT-SIZE: Xpx;}
						classList.add(att.substring(1, att.length() - 1));
					} else if (att.startsWith("<b>")) {
						classList.add("bold"); //.bold{font-weight: bold;}
					} else if (att.startsWith("<i>")) {
						classList.add("italic"); //.italic{font-style: italic;}
					} else if (att.startsWith("<u>")) {
						classList.add("underline"); //.underline{text-decoration:underline;}
					} else {
						//有font属性时才输出font标签
						if (html.length() == 0) {
							html.append("<font");
						}
						html.append(" " + att);
					}
				}
			}

			// 用css处理粗体, 斜体, 下划线
			String htmlClass = "";
			if (classList.size() > 0) {
				htmlClass = " class='";
				for (String s : classList) {
					htmlClass += s + " ";
				}
				htmlClass = htmlClass.substring(0, htmlClass.length() - 1);
				htmlClass += "'";
			}

			if (html.length() > 0) {
				html.append("" + htmlClass + ">");
			}
		}

		// 取文字内容
		String text = "";
		if (e.isLeaf()) {
			try {
				int a = e.getStartOffset();
				int b = e.getEndOffset();
				text = e.getDocument().getText(a, b - a).trim();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		// 没有font属性时不输出font标签
		if (eName.equals("content") && html.length() > 0) {
			html.append(text + "</font>");
		} else {
			html.append(text);
		}
		return html.toString();

	}

	/**
	 * 转换成word对象
	 * @param e
	 * @return
	 */
	private Object getWordObject(Element e) {
		Paragraph paragraph = null;//段落
		Chunk chunk = null;//文本块
		int i = 0;//文字风格属性
		int alignment = 0;//字体对齐方式
		AttributeSet attset = e.getAttributes().copyAttributes();
		Font font = new Font();
		Enumeration names = attset.getAttributeNames();
		String key = null;
		String val = null;
		while (names.hasMoreElements()) {
			Object nextName = names.nextElement();
			if (nextName != StyleConstants.ResolveAttribute) {
				key = nextName.toString();
				val = attset.getAttribute(nextName).toString();
				if (key.equals("size")) {
					font.setSize(Float.parseFloat(val));
				} else if (key.equals("foreground")) {//[r=255,g=255,b=255]
					val = val.substring(val.indexOf('['), val.indexOf(']'));
					String[] tmp = val.split(",");
					font.setColor(new Color(Integer.parseInt(tmp[0].substring(tmp[0].indexOf("=") + 1)), Integer.parseInt(tmp[1].substring(tmp[1].indexOf("=") + 1)), Integer.parseInt(tmp[2].substring(tmp[2].indexOf("=") + 1))));
				} else if (key.equals("bold") && val.equals("true")) {
					i |= 1;
				} else if (key.equals("italic") && val.equals("true")) {
					i |= 2;
				} else if (key.equals("underline") && val.equals("true")) {
					i |= 4;
				} else if (key.equals("Alignment")) {
					alignment = Integer.parseInt(val);
				} else if (key.equals("family")) {
					font.setFamily(val);
				}
				//其他属性
			}
		}
		font.setStyle(i);//设置字体风格，如加粗，斜体，加下划线
		String text = "";// 取文字内容
		if (e.isLeaf()) {
			try {
				int a = e.getStartOffset();
				int b = e.getEndOffset();
				text = e.getDocument().getText(a, b - a).trim();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		String eName = e.getName();
		if (eName.equals("content")) {
			chunk = new Chunk(text);
			chunk.setFont(font);
			return chunk;
		} else if (eName.equals("paragraph")) {
			paragraph = new Paragraph(text);
			paragraph.setAlignment(alignment);
			paragraph.setFont(font);
			return paragraph;
		}
		return null;
	}

	/***
	 * 转成html标签
	 * @param key
	 * @param val
	 * @return
	 */
	private String getHtmlTag(String key, String val) {
		String html = "";
		if (key.equals("size")) {
			html = "<font" + val + ">";
		} else if (key.equals("foreground")) {
			html = "color='" + this.getColorHex(val) + "'";
		} else if (key.equals("bold") && val.equals("true")) {
			html = "<b></b>";
		} else if (key.equals("italic") && val.equals("true")) {
			html = "<i></i>";
		} else if (key.equals("underline") && val.equals("true")) {
			html = "<u></u>";
		} else if (key.equals("Alignment")) {
			if (val.equals("1")) {
				html = "align='center'";
			} else if (val.equals("0")) {
				html = "align='left'";
			} else if (val.equals("2")) {
				html = "align='right'";
			}
		}

		return html;

	}

	/**
	 * RGB 转成 16进制颜色码
	 * @param s
	 * @return
	 */
	private String getColorHex(String s) {
		s = s.substring(s.indexOf('['), s.indexOf(']'));
		String[] tmp = s.split(",");
		String r = Integer.toHexString(Integer.parseInt(tmp[0].split("=")[1]));
		if (r.length() == 1) {
			r = "0" + r;
		}
		String g = Integer.toHexString(Integer.parseInt(tmp[1].split("=")[1]));
		if (g.length() == 1) {
			g = "0" + g;
		}
		String b = Integer.toHexString(Integer.parseInt(tmp[2].split("=")[1]));
		if (b.length() == 1) {
			b = "0" + b;
		}
		return "#" + r + g + b;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(StyleDocumentConverter.getHeader());
	}

}
