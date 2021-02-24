package com.pushworld.ipushgrc.bs.keywordreplace;

import java.io.File;

/**
 * 用于处理将一个图片文件插入到xml文件
 * @author yyb
 * Jul 21, 2011 4:37:44 PM
 */
public class ImageXML extends ImageSuper {

	/**
	 * 传入图片base64编码，作为构造函数参数
	 * @param _base64code
	 */
	public ImageXML(String base64code) {
		super(base64code);
	}

	/**
	 * 传入图片文件，作为构造函数参数
	 * @param img_file
	 */
	public ImageXML(File img_file) {
		super(img_file);
	}

	/**
	 * 根据图片的64位码生成图片的word xml,仅获取图片定义部分，不设置段落，居中等，在关键字替换时用到
	 * @return
	 */
	public String getImageXmlOnlyPict(int _width, int _height) {
		StringBuilder sb_xml = new StringBuilder();
		sb_xml.append("<w:pict>\r\n");

		// 图片绑定的数据,最关键
		long ll_jpgid = System.currentTimeMillis(); //
		sb_xml.append("<w:binData w:name=\"wordml://" + ll_jpgid + ".jpg\">\r\n");
		sb_xml.append(this.get_base64code()); // 图片的64位码xml
		sb_xml.append("</w:binData>\r\n");

		sb_xml.append("<v:shape id=\"_x0000_i1025\" type=\"#_x0000_t75\" style=\"width:" + _width + "pt;height:" + _height + "pt\">\r\n");
		sb_xml.append("<v:imagedata src=\"wordml://" + ll_jpgid + ".jpg\" o:title=\"Sunset\" />\r\n");
		sb_xml.append("</v:shape>\r\n");

		sb_xml.append("</w:pict>\r\n");
		return sb_xml.toString();
	}

	/**
	 * 使用默认高度宽度 根据图片的64位码生成图片的word xml,仅获取图片定义部分
	 * @return
	 */
	public String getImageXmlOnlyPict() {
		return this.getImageXmlOnlyPict(this.getImgWidth(), this.getImgHeight());
	}

	/**
	 * 根据图片的64位码生成图片的word xml,返回的是一个段落，段落中包含图片
	 * @return
	 */
	public String getImageParaXml(int _width, int _height) {
		StringBuffer sb_xml = new StringBuffer(); //

		// 图片必须包含在一个段落里
		sb_xml.append("\r\n");
		sb_xml.append("<w:p>\r\n");
		sb_xml.append("<w:pPr>\r\n"); //
		sb_xml.append("<w:jc w:val='center'/>\r\n"); // 图片要居中!!
		sb_xml.append("<w:rPr>\r\n");
		sb_xml.append("<w:rFonts w:hint='fareast'/>\r\n"); //
		sb_xml.append("</w:rPr>\r\n");
		sb_xml.append("</w:pPr>\r\n");
		sb_xml.append("<w:r>\r\n");
		sb_xml.append("<w:pict>\r\n");

		// 图片绑定的数据,最关键
		long ll_jpgid = System.currentTimeMillis(); //
		sb_xml.append("<w:binData w:name=\"wordml://" + ll_jpgid + ".jpg\">\r\n");
		sb_xml.append(this.get_base64code()); // 图片的64位码xml
		sb_xml.append("</w:binData>\r\n");

		sb_xml.append("<v:shape id=\"_x0000_i1025\" type=\"#_x0000_t75\" style=\"width:" + _width + "pt;height:" + _height + "pt\">\r\n");
		sb_xml.append("<v:imagedata src=\"wordml://" + ll_jpgid + ".jpg\" o:title=\"Sunset\" />\r\n");
		sb_xml.append("</v:shape>\r\n");

		sb_xml.append("</w:pict>\r\n");
		sb_xml.append("</w:r>\r\n");
		sb_xml.append("</w:p>\r\n"); //
		return sb_xml.toString(); //
	}

	/**
	 * 使用默认高度和宽度，根据图片的64位码生成图片的word xml,返回的是一个段落，段落中包含图片
	 * @return
	 */
	public String getImageParaXml() {
		return this.getImageParaXml(this.getImgWidth(), this.getImgHeight());
	}
}
