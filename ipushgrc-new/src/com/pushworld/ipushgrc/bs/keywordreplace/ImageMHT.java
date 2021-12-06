package com.pushworld.ipushgrc.bs.keywordreplace;

import java.io.File;

/**
 * ���ڴ���һ��ͼƬ�ļ����뵽mht�ļ�
 * @author yyb
 * Jul 21, 2011 1:43:58 PM
 */
public class ImageMHT extends ImageSuper {

	/**
	 * ����ͼƬbase64���룬��Ϊ���캯������
	 * @param _base64code
	 */
	public ImageMHT(String base64code) {
		super(base64code);
	}

	/**
	 * ����ͼƬ�ļ�����Ϊ���캯������
	 * @param img_file
	 */
	public ImageMHT(File img_file) {
		super(img_file);
	}

	/**
	 * ��ͼƬ��װ��MHT����������!!! MHT���ǽ�ͼƬ���������һ���������,ֻ����ͨ���ļ�������������ݹ���������!!!
	 * @param boundary_str  �ֽ��ʾ����MHT�ļ�ͷע�������� boundary="----=_NextPart_01CC46FF.CD26AB10"
	 * @param location_str  Content-Location: file:///C:/B1334A73/123.htm�е�C:/B1334A73/����
	 * @param _fileName
	 * @param _64Code
	 * @return
	 */
	public String getMHTImageContentCode(String boundary_str, String location_str) {

		StringBuffer sb_html = new StringBuffer();
		sb_html.append("\r\n");
		sb_html.append(boundary_str + "\r\n");
		sb_html.append("Content-Location: file:///" + location_str + img_name + ".jpg\r\n");
		sb_html.append("Content-Transfer-Encoding: base64\r\n");
		sb_html.append("Content-Type: image/jpeg\r\n");
		sb_html.append("\r\n");
		sb_html.append(this._base64code + "\r\n"); // ʵ�ʵ�64λ��!!
		sb_html.append("\r\n");
		return sb_html.toString();
	}

	/**
	 * ��MHT�ļ��е�ͼƬspan,ָ����Ⱥ͸߶�
	 * @param _fileName
	 * @param _width
	 * @param _height
	 * @return
	 */
	public String getMhtImgSpanStr(int _width, int _height) {
		StringBuffer sb_imgspan = new StringBuffer();
		sb_imgspan.append("<span lang=3DEN-US>\r\n");
		sb_imgspan.append("<v:shapetype id=3D\"_x0000_t75\" coordsize=3D\"21600,21600\" o:spt=3D\"75\" o:preferrelative=3D\"t\" path=3D\"m@4@5l@4@11@9@11@9@5xe\" filled=3D\"f\" stroked=3D\"f\">\r\n");
		sb_imgspan.append("<v:stroke joinstyle=3D\"miter\"/>\r\n");
		sb_imgspan.append("<v:formulas>\r\n");
		sb_imgspan.append("<v:f eqn=3D\"if lineDrawn pixelLineWidth 0\"/>\r\n");
		sb_imgspan.append("<v:f eqn=3D\"sum @0 1 0\"/>\r\n");
		sb_imgspan.append("<v:f eqn=3D\"sum 0 0 @1\"/>\r\n");
		sb_imgspan.append("<v:f eqn=3D\"prod @2 1 2\"/>\r\n");
		sb_imgspan.append("<v:f eqn=3D\"prod @3 21600 pixelWidth\"/>\r\n");
		sb_imgspan.append("<v:f eqn=3D\"prod @3 21600 pixelHeight\"/>\r\n");
		sb_imgspan.append("<v:f eqn=3D\"sum @0 0 1\"/>\r\n");
		sb_imgspan.append("<v:f eqn=3D\"prod @6 1 2\"/>\r\n");
		sb_imgspan.append("<v:f eqn=3D\"prod @7 21600 pixelWidth\"/>\r\n");
		sb_imgspan.append("<v:f eqn=3D\"sum @8 21600 0\"/>\r\n");
		sb_imgspan.append("<v:f eqn=3D\"prod @7 21600 pixelHeight\"/>\r\n");
		sb_imgspan.append("<v:f eqn=3D\"sum @10 21600 0\"/>\r\n");
		sb_imgspan.append("</v:formulas>\r\n");
		sb_imgspan.append("<v:path o:extrusionok=3D\"f\" gradientshapeok=3D\"t\" o:connecttype=3D\"rect\"/>\r\n");
		sb_imgspan.append("<o:lock v:ext=3D\"edit\" aspectratio=3D\"t\"/>\r\n");
		sb_imgspan.append("</v:shapetype>\r\n");
		sb_imgspan.append("<v:shape id=3D\"_x0000_i1025\" type=3D\"#_x0000_t75\" style=3D'width:" + _width * 3 / 4 + "pt; height:" + _height * 3 / 4 + "pt'>\r\n");
		sb_imgspan.append("<v:imagedata src=3D\"" + img_name + ".jpg\" o:title=3D\"logo\"/>\r\n");
		sb_imgspan.append("</v:shape>\r\n");
		sb_imgspan.append("<![if !vml]>\r\n");
		sb_imgspan.append("<img width=3D" + _width + " height=3D" + _height + " src=3D\"" + img_name + ".jpg \"  v:shapes=3D\"_x0000_i1025\">\r\n");
		sb_imgspan.append("</span>\r\n");
		return sb_imgspan.toString();
	}

	/**
	 * ��MHT�ļ��е�ͼƬspan,��ָ����Ⱥ͸߶ȣ�����ͼƬԭ��Ⱥ͸߶�
	 * @param _fileName
	 * @param _width
	 * @param _height
	 * @return
	 */
	public String getMhtImgSpanStr() {
		return getMhtImgSpanStr(getImgWidth(), getImgHeight());
	}

}
