package com.pushworld.ipushgrc.bs.keywordreplace;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.common.TBUtil;

/**
 * �ؼ����滻�������滻����ļ�����
 * @author yyb
 * Jul 21, 2011 1:55:11 PM
 */
public abstract class AbstractKeyWordReplace {
	public HashMap<String,Object>[] maps;
	public String template_name;
	public AbstractKeyWordReplace(HashMap<String, Object>[] maps, String template_name) {
		super();
		this.maps = maps;
		this.template_name = template_name;
	}

	/**
	 * �����滻����ļ�����
	 * @param maps  key :Ҫ�滻�Ĺؼ���;value:���ֻ�һ��HashMap(����ΪͼƬ��keyΪimg,valueΪͼƬFile��base64����)
	 * @param template_name 
	 * @return
	 */
	public abstract String[] getContents() throws Exception;

	/**
	 * �ӷ�����UPLOAD�ļ����ж��ļ�
	 * @param template_name
	 * @return
	 */
	public String readFromUpLoadFile(String template_name) throws Exception{
		String path = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/upload/" + template_name;
		File f = new File(path);
		String str = null;
		InputStream is = new FileInputStream(f);
		byte[] bytes = new TBUtil().readFromInputStreamToBytes(is);
		str = new String(bytes, "UTF-8");
		return str;
	}
}
