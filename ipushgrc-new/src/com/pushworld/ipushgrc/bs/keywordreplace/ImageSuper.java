package com.pushworld.ipushgrc.bs.keywordreplace;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import cn.com.infostrategy.to.common.TBUtil;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;

/**
 * 模板中替换关键字的图片
 * @author yyb
 * Jul 21, 2011 4:30:10 PM
 */
public class ImageSuper {
	protected String _base64code;
	protected String img_name;
	protected BufferedImage buffer_img;//base64转为图片时用于得到图片原始宽高

	/**
	 * 传入图片base64编码，作为构造函数参数
	 * @param _base64code
	 */
	public ImageSuper(String base64code) {
		img_name = String.valueOf(System.currentTimeMillis());
		_base64code = base64code;
	}

	/**
	 * 传入图片文件，作为构造函数参数
	 * @param img_file
	 */
	public ImageSuper(File img_file) {
		img_name = String.valueOf(System.currentTimeMillis());
		try {
			byte[] bytes = new TBUtil().readFromInputStreamToBytes(new FileInputStream(img_file));
			_base64code = new BASE64Encoder().encode(bytes);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 返回图片宽度
	 * @return
	 */
	public int getImgWidth() {
		if (buffer_img == null) {
			setBuffer_img();
		}
		return buffer_img.getWidth();
	}

	/**
	 * 返回图片高度
	 * @return
	 */
	public int getImgHeight() {
		if (buffer_img == null) {
			setBuffer_img();
		}
		return buffer_img.getHeight();
	}

	public String get_base64code() {
		return _base64code;
	}

	public void set_base64code(String _base64code) {
		this._base64code = _base64code;
	}

	public BufferedImage getBuffer_img() {
		return buffer_img;
	}

	public void setBuffer_img() {
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] bytes = decoder.decodeBuffer(_base64code);
			for (int i = 0; i < bytes.length; i++) {
				if (bytes[i] < 0) {
					bytes[i] += 256;
				}
			}
			InputStream is = new ByteArrayInputStream(bytes);
			JPEGImageDecoder jpeg_decoder = JPEGCodec.createJPEGDecoder(is);
			buffer_img = jpeg_decoder.decodeAsBufferedImage();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getImg_name() {
		return img_name;
	}

	public void setImg_name(String img_name) {
		this.img_name = img_name;
	}

}
