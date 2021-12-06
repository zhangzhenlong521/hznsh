package com.pushworld.ipushgrc.ui.law.p010;

import java.awt.Color;
import java.awt.Container;
import java.awt.SystemColor;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JFileChooser;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.rtf.RtfWriter2;
import com.lowagie.text.rtf.style.RtfFont;

public class LawWordBuilder {
	private ByteArrayOutputStream byteOutStream = null;
	private String id = null;
	private String lawname = null;
	Font filetitleFont;
	Font titleFont;
	private Font contextFont = null;
	private Document document = null;
	private HashVO[] childHashVOs = null;
	private Paragraph paragraph;
	private String itemtitle = "";
	private String itemcontent = "";
	private Paragraph paragraph1;
	private Container container = null;//设置提示窗口所属容器【李春娟/2015-01-27】

	public LawWordBuilder(String titilename, String id) {
		this(null, titilename, id);
	}

	public LawWordBuilder(Container _container, String titilename, String id) {
		this.id = id;
		this.lawname = titilename;
		this.container = _container;
		showTempLawbook();
	}

	/**
	 * 使用itext计算Word文件的内容,返回的是整个word的byte[]
	 * @param 
	 * @return
	 * @throws Exception
	 */
	public byte[] getDocContextBytes() throws Exception {
		filetitleFont = new RtfFont("仿_宋", 16, Font.BOLD);// 文件名字体风格 
		titleFont = new RtfFont("仿_宋", 14, Font.BOLD);// 章节标题字体风格 
		contextFont = new RtfFont("仿_宋", 12, Font.NORMAL);// 正文字体风格,五号字体
		contextFont.setColor(0, 0, 0);//必须设置一下，否则因风险等级的字体设置了颜色，导致跟风险等级的颜色一致了
		document = new Document(PageSize.A4);
		byteOutStream = new ByteArrayOutputStream();
		RtfWriter2.getInstance(document, byteOutStream);
		document.open();
		HeaderFooter footer = new HeaderFooter(null, true);
		footer.setBackgroundColor(new Color(SystemColor.window.getRed(), SystemColor.window.getGreen(), SystemColor.window.getBlue()));
		footer.setAlignment(Element.ALIGN_RIGHT);
		document.setFooter(footer);
		paragraph = new Paragraph(lawname);
		paragraph.setSpacingBefore(40);
		paragraph.setSpacingAfter(20);
		paragraph.setAlignment(Element.ALIGN_CENTER);
		paragraph.setFont(filetitleFont);
		document.add(paragraph);
		addContentPage();
		document.close();
		return byteOutStream.toByteArray(); //返回
	}

	/**
	 * 将法规条目写入到word文档中
	 */
	private void addContentPage() {
		try {
			for (int i = 0; i < childHashVOs.length; i++) {
				paragraph = new Paragraph();
				paragraph1 = new Paragraph();
				Chunk chunk = new Chunk();
				Chunk chunk1 = new Chunk();
				itemtitle = childHashVOs[i].getStringValue("itemtitle");
				itemcontent = childHashVOs[i].getStringValue("itemcontent");
				String[] ss = new String[2];
				if (itemtitle != null && !"".equals(itemtitle)) {
					if (itemtitle.contains(" ")) {
						ss[0] = itemtitle.substring(0, itemtitle.indexOf(" ")).trim();
						ss[1] = itemtitle.substring(itemtitle.indexOf(" "));
					} else {
						ss[0] = itemtitle;
						ss[1] = "";
					}
					paragraph.setFirstLineIndent(24);
					paragraph.setLeading(20);
					paragraph.setSpacingBefore(5);
					paragraph.setSpacingAfter(5);
					if (itemtitle != null && ((itemtitle.contains("章") || itemtitle.contains("节")) && ss[0].startsWith("第") && (ss[0].endsWith("章") || ss[0].endsWith("节")))) {
						paragraph.add(itemtitle);
						paragraph.setFont(titleFont);
						paragraph.setAlignment(Element.ALIGN_CENTER);
						document.add(paragraph);
						if (itemcontent != null && !"".equals(itemcontent)) {
							paragraph1.add(itemcontent);
							paragraph1.setFont(new RtfFont("仿_宋", 10, Font.NORMAL));
							paragraph1.setFirstLineIndent(24);
							paragraph1.setLeading(20);
							paragraph1.setSpacingBefore(5);
							paragraph1.setSpacingAfter(5);
							paragraph1.setAlignment(Element.ALIGN_LEFT);
							document.add(paragraph1);
						}
					} else {
						paragraph.setAlignment(Element.ALIGN_LEFT);
						chunk.append(itemtitle);
						chunk.setFont(new RtfFont("仿_宋", 12, Font.BOLD));
						if (itemcontent != null && !"".equals(itemcontent)) {
							chunk1.append(itemcontent);
						}
						chunk1.setFont(contextFont);
						paragraph.add(chunk);
						paragraph.add("  " + chunk1);
						paragraph.setAlignment(Element.ALIGN_LEFT);
						document.add(paragraph);
					}
				} else {
					paragraph1 = new Paragraph("");
					if (itemcontent != null && !"".equals(itemcontent)) {
						paragraph1.add(itemcontent);
						paragraph1.setFont(new RtfFont("仿_宋", 10, Font.NORMAL));
						paragraph1.setFirstLineIndent(24);
						paragraph1.setLeading(20);
						paragraph1.setSpacingBefore(5);
						paragraph1.setSpacingAfter(5);
						paragraph1.setAlignment(Element.ALIGN_LEFT);
						document.add(paragraph1);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showTempLawbook() {
		try {
			String sql = "select * from LAW_LAW_ITEM where lawid =  '" + id + "'  order by linkcode,abs(id)";
			try {
				childHashVOs = UIUtil.getHashVoArrayAsTreeStructByDS(null, sql, "id", "parentid", "seq", null);
				if (childHashVOs.length > 0) {
					JFileChooser chooser = new JFileChooser();
					File f = new File(new File(ClientEnvironment.str_downLoadFileDir + File.separator + lawname.trim() + ".doc").getCanonicalPath());
					chooser.setSelectedFile(f);
					String str_path = "";
					int li_rewult = chooser.showSaveDialog(null);
					if (li_rewult == JFileChooser.APPROVE_OPTION) {
						File curFile = chooser.getSelectedFile(); //
						if (curFile != null) {
							str_path = curFile.getAbsolutePath();
						}
					}
					if (str_path == null || str_path.equals("")) {
						return;
					}
					byte[] bytes = this.getDocContextBytes();
					FileOutputStream output = new FileOutputStream(str_path);
					output.write(bytes);
					output.close();
					Runtime.getRuntime().exec("rundll32 url.dll FileProtocolHandler  \"" + str_path + "\"");
				} else {
					MessageBox.show(this.container, "该法规没有内容.");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception ex) {
			MessageBox.show(this.container, "另一个程序正在使用此文件");
			ex.printStackTrace(); //
		}
	}
}
