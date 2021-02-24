package com.pushworld.ipushgrc.bs.keywordreplace;

import java.io.File;

/**
 * ���ڴ���һ��ͼƬ�ļ����뵽xml�ļ�
 * @author yyb
 * Jul 21, 2011 4:37:44 PM
 */
public class ImageXML extends ImageSuper {

	/**
	 * ����ͼƬbase64���룬��Ϊ���캯������
	 * @param _base64code
	 */
	public ImageXML(String base64code) {
		super(base64code);
	}

	/**
	 * ����ͼƬ�ļ�����Ϊ���캯������
	 * @param img_file
	 */
	public ImageXML(File img_file) {
		super(img_file);
	}

	/**
	 * ����ͼƬ��64λ������ͼƬ��word xml,����ȡͼƬ���岿�֣������ö��䣬���еȣ��ڹؼ����滻ʱ�õ�
	 * @return
	 */
	public String getImageXmlOnlyPict(int _width, int _height) {
		StringBuilder sb_xml = new StringBuilder();
		sb_xml.append("<w:pict>\r\n");

		// ͼƬ�󶨵�����,��ؼ�
		long ll_jpgid = System.currentTimeMillis(); //
		sb_xml.append("<w:binData w:name=\"wordml://" + ll_jpgid + ".jpg\">\r\n");
		sb_xml.append(this.get_base64code()); // ͼƬ��64λ��xml
		sb_xml.append("</w:binData>\r\n");

		sb_xml.append("<v:shape id=\"_x0000_i1025\" type=\"#_x0000_t75\" style=\"width:" + _width + "pt;height:" + _height + "pt\">\r\n");
		sb_xml.append("<v:imagedata src=\"wordml://" + ll_jpgid + ".jpg\" o:title=\"Sunset\" />\r\n");
		sb_xml.append("</v:shape>\r\n");

		sb_xml.append("</w:pict>\r\n");
		return sb_xml.toString();
	}

	/**
	 * ʹ��Ĭ�ϸ߶ȿ�� ����ͼƬ��64λ������ͼƬ��word xml,����ȡͼƬ���岿��
	 * @return
	 */
	public String getImageXmlOnlyPict() {
		return this.getImageXmlOnlyPict(this.getImgWidth(), this.getImgHeight());
	}

	/**
	 * ����ͼƬ��64λ������ͼƬ��word xml,���ص���һ�����䣬�����а���ͼƬ
	 * @return
	 */
	public String getImageParaXml(int _width, int _height) {
		StringBuffer sb_xml = new StringBuffer(); //

		// ͼƬ���������һ��������
		sb_xml.append("\r\n");
		sb_xml.append("<w:p>\r\n");
		sb_xml.append("<w:pPr>\r\n"); //
		sb_xml.append("<w:jc w:val='center'/>\r\n"); // ͼƬҪ����!!
		sb_xml.append("<w:rPr>\r\n");
		sb_xml.append("<w:rFonts w:hint='fareast'/>\r\n"); //
		sb_xml.append("</w:rPr>\r\n");
		sb_xml.append("</w:pPr>\r\n");
		sb_xml.append("<w:r>\r\n");
		sb_xml.append("<w:pict>\r\n");

		// ͼƬ�󶨵�����,��ؼ�
		long ll_jpgid = System.currentTimeMillis(); //
		sb_xml.append("<w:binData w:name=\"wordml://" + ll_jpgid + ".jpg\">\r\n");
		sb_xml.append(this.get_base64code()); // ͼƬ��64λ��xml
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
	 * ʹ��Ĭ�ϸ߶ȺͿ�ȣ�����ͼƬ��64λ������ͼƬ��word xml,���ص���һ�����䣬�����а���ͼƬ
	 * @return
	 */
	public String getImageParaXml() {
		return this.getImageParaXml(this.getImgWidth(), this.getImgHeight());
	}
}
