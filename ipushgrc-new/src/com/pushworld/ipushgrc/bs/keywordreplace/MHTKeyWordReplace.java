package com.pushworld.ipushgrc.bs.keywordreplace;

import java.io.File;
import java.util.HashMap;

/**
 * MHTģ���ļ��ؼ����滻��
 * @author yyb
 * Jul 21, 2011 1:50:05 PM
 */
public class MHTKeyWordReplace extends AbstractKeyWordReplace {

	public MHTKeyWordReplace(HashMap<String, Object>[] maps, String template_name) {
		super(maps, template_name);
		// TODO Auto-generated constructor stub
	}

	/**
	 * �����滻����ļ�����
	 * @param maps  key :Ҫ�滻�Ĺؼ���;value:���ֻ�һ��HashMap(����ΪͼƬ��keyΪimg,valueΪͼƬFile��base64����)
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
		//System.out.println(strArr.length+"===��======================");
		for (int j = 0; j < maps.length; j++) {
			String finalStr;
			boolean is_contain_img = false;
			StringBuffer imgContentSb = new StringBuffer();//����滻ͼƬ��</html>������ͼƬ���ݲ���
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
					if (maps[j].containsKey(itemKey)) {//map���д˹ؼ���
						Object obj = maps[j].get(itemKey);
						String strToReplace = strArr[i].substring(0, strArr[i].indexOf("&#12303;")+ 8);
						String newStr = null;
						 if (obj instanceof HashMap) {//�滻ͼƬ
							is_contain_img = true;
							
							HashMap map = (HashMap) obj;
							Object imgObj = map.get("img");
							ImageMHT mht_img = null;
							if (imgObj instanceof File) {//������ͼƬ�ļ�
								File img_file = (File) imgObj;
								mht_img = new ImageMHT(img_file);
							} else if (imgObj instanceof String) {//������ͼƬ64����
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
						}else {//�滻����
							String value = (String) obj;
							newStr = strArr[i].replace(strToReplace, (value == null) ? "" : getStrMHTCode(value));
							//System.out.println("���滻===="+strToReplace);
							//System.out.println("�滻Ϊ===="+((value == null) ? "" : getStrMHTCode(value)));
						} 
						sb.append(newStr);
					} else {//map���޴˹ؼ���
						sb.append("&#12302;" + strArr[i]);
					}
				} else {
					sb.append(strArr[i]);
				}
			}
			finalStr = sb.toString();
			if (is_contain_img) {//</html>֮�����image  content
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
	 * ��һ���ַ���ת��������mht�ļ��еı���
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
