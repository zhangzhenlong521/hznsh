package com.pushworld.ipushgrc.bs.keywordreplace;

import java.io.File;
import java.util.HashMap;

/**
 * MHT模板文件关键词替换类
 * @author yyb
 * Jul 21, 2011 1:50:05 PM
 */
public class MHTKeyWordReplace extends AbstractKeyWordReplace {

	public MHTKeyWordReplace(HashMap<String, Object>[] maps, String template_name) {
		super(maps, template_name);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 返回替换后的文件内容
	 * @param maps  key :要替换的关键字;value:文字或一个HashMap(内容为图片，key为img,value为图片File或base64编码)
	 * @param mht_name 
	 * @return
	 */
	public String[] getContents() throws Exception{
		int fileNum = super.maps.length;
		if (fileNum == 0) {
			return null;
		}
		String[] returnStrArr = new String[fileNum];
		String mht_content = readFromUpLoadFile(super.template_name);
		if (mht_content.contains("=\r\n")) {
			mht_content = mht_content.replaceAll("=\r\n", "");
		}
		mht_content = mht_content.replaceAll("\r\n  ", "");
		final String[] strArr = mht_content.split("&#12302;");
		//System.out.println(strArr.length+"===段======================");
		for (int j = 0; j < maps.length; j++) {
			String finalStr;
			boolean is_contain_img = false;
			StringBuffer imgContentSb = new StringBuffer();//如果替换图片，</html>后增加图片内容部分
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < strArr.length; i++) {
				if (strArr[i].contains("&#12303;")) {
					int index1=strArr[i].indexOf(">", 0);
					int index2=strArr[i].indexOf("<", index1+1);
					String itemKey = strArr[i].substring(index1+1, index2).trim();
					if (itemKey.contains("=\r\n"))
						itemKey = itemKey.replace("=\r\n", "");
					if (itemKey.contains("\r\n"))
						itemKey = itemKey.replace("\r\n", "");
					//System.out.println("itemkey"+itemKey);
					if (maps[j].containsKey(itemKey)) {//map中有此关键字
						Object obj = maps[j].get(itemKey);
						String strToReplace = strArr[i].substring(0, strArr[i].indexOf("&#12303;")+ 8);
						String newStr = null;
						 if (obj instanceof HashMap) {//替换图片
							is_contain_img = true;
							
							HashMap map = (HashMap) obj;
							Object imgObj = map.get("img");
							ImageMHT mht_img = null;
							if (imgObj instanceof File) {//传的是图片文件
								File img_file = (File) imgObj;
								mht_img = new ImageMHT(img_file);
							} else if (imgObj instanceof String) {//传的是图片64编码
								mht_img = new ImageMHT((String)imgObj);
							}
							MHTFileUtil mhtfile = new MHTFileUtil(mht_content);
							String img_content = mht_img.getMHTImageContentCode(mhtfile.getBoundaryStr(), mhtfile.getContentLocationStr());
							imgContentSb.append(img_content);
							String img_span;
							if(map.containsKey("width")&&map.containsKey("height")){
								img_span = mht_img.getMhtImgSpanStr((Integer)map.get("width"),(Integer)map.get("height"));
							}else{
								img_span = mht_img.getMhtImgSpanStr();
							}
							//System.out.println(img_span);
							newStr = strArr[i].replace(strToReplace, img_span);
						}else {//替换文字
							String value = (String) obj;
							newStr = strArr[i].replace(strToReplace, (value == null) ? "" : getStrMHTCode(value));
							//System.out.println("被替换===="+strToReplace);
							//System.out.println("替换为===="+((value == null) ? "" : getStrMHTCode(value)));
						} 
						sb.append(newStr);
					} else {//map中无此关键字
						sb.append("&#12302;" + strArr[i]);
					}
				} else {
					sb.append(strArr[i]);
				}
			}
			finalStr = sb.toString();
			if (is_contain_img) {//</html>之后加入image  content
				int html_index = finalStr.indexOf("</html>");
				StringBuffer sbtemp = new StringBuffer();
				sbtemp.append(finalStr.substring(0, html_index));
				sbtemp.append("</html>");
				sbtemp.append(imgContentSb);
				sbtemp.append(finalStr.substring(html_index + 7));
				finalStr = sbtemp.toString();
			}
			returnStrArr[j] = finalStr;
		}
		return returnStrArr;
	}

	/**
	 * 将一段字符串转换成其在mht文件中的编码
	 * @param str
	 * @return
	 */
	private String getStrMHTCode(String str) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			int c = str.charAt(i);
			if (c < 127)
				sb.append((char) c);
			else
				sb.append("&#" + c + ";");
		}
		return sb.toString();
	}

}
