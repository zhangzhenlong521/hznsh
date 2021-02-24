package com.pushworld.ipushgrc.bs.keywordreplace;

import java.io.File;
import java.util.HashMap;

/**
 * XML模板文件关键词替换类
 * @author yyb
 * Jul 21, 2011 1:59:25 PM
 */
public class XMLKeyWordReplace extends AbstractKeyWordReplace {

	public XMLKeyWordReplace(HashMap<String, Object>[] maps, String template_name) {
		super(maps, template_name);
	}

	/**
	 * 返回替换后的文件内容
	 * @param maps  key :要替换的关键字;value:文字或一个HashMap(内容为图片，key为img,value为图片File或base64编码)
	 * @param xml_name 
	 * @return
	 */
	public String[] getContents() throws Exception{
		int num = super.maps.length;
		String xmlContent = super.readFromUpLoadFile(super.template_name);
		if (num == 0) {
			return null;
		}
		String[] strs = new String[num];
		String[] strArr = xmlContent.split("『");

		for (int j = 0; j < num; j++) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < strArr.length; i++) {
				if (strArr[i].contains("』")) {
					int index = strArr[i].indexOf("』");
					String itemKey = strArr[i].substring(0, index);
					if (maps[j].containsKey(itemKey)) {
						Object obj = maps[j].get(itemKey);
						String value = null;
						if (obj instanceof String) {
							value = (String) obj;
						} else if (obj instanceof HashMap) {
							HashMap img_map = (HashMap) obj;
							Object imgObj =img_map.get("img");
							ImageXML img_xml = null;
							if (imgObj instanceof File) {//传的是图片文件
								File img_file = (File) imgObj;
								img_xml = new ImageXML (img_file);
							} else if (imgObj instanceof String) {//传的是图片64编码
								img_xml = new ImageXML((String)imgObj);
							}
							if(img_map.containsKey("width")&&img_map.containsKey("height")){
								value = img_xml.getImageXmlOnlyPict((Integer)img_map.get("width"),(Integer)img_map.get("height"));
							}else{
								value = img_xml.getImageXmlOnlyPict();
							}
						
						}
						String strToReplace = strArr[i].substring(0, index + 1);
						if(value==null){
							value="";
						}else{
//							value=value.replaceAll("<", "&lt;");
//							value=value.replaceAll(">", "&gt;");
						}
						String newStr = strArr[i].replace(strToReplace,value);
						sb.append(newStr);
					} else {
						sb.append("『" + strArr[i]);
					}
				} else {
					sb.append(strArr[i]);
				}
			}
			strs[j] = sb.toString();
		}
		return strs;
	}
}
