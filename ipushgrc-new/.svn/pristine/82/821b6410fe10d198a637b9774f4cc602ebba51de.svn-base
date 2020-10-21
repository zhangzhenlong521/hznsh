package com.pushworld.ipushgrc.bs.keywordreplace;

/**
 * 处理mht文件，从现有的mht文件中提取boundary，Content-Location值
 * @author yyb
 * Jul 21, 2011 1:41:53 PM
 */
public class MHTFileUtil {
	private String mht_content;

	public MHTFileUtil(String mht_content) {
		this.mht_content = mht_content;
	}

	/**
	 * 如文件头中的boundry部分，如Content-Type: multipart/related; boundary="----=_NextPart_01CC4208.7BF558F0"，则返回
	 * ----=_NextPart_01CC4208.7BF558F0，在文件追加内容时将用到，如增加图片
	 * @return
	 */
	public String getBoundaryStr() {
		int boundary_str_index = mht_content.indexOf("boundary");
		int first_colon_index = mht_content.indexOf("\"", boundary_str_index);
		int second_colon_index = mht_content.indexOf("\"", first_colon_index + 1);
		String boundary_str = mht_content.substring(first_colon_index + 1, second_colon_index);
		return "--" + boundary_str;
	}

	/**
	 * 得到Content-Location路径，追加内容时用到
	 * @return  Content-Location路径
	 */
	public String getContentLocationStr() {
		int Content_Location_str_index = mht_content.indexOf("Content-Location");
		int file_str_index = mht_content.indexOf("file", Content_Location_str_index);
		int first_slant_index = mht_content.indexOf("/", file_str_index + 8);
		int second_slant_index = mht_content.indexOf("/", first_slant_index + 1);
		String file_path_str = mht_content.substring(first_slant_index - 2, second_slant_index + 1);
		return file_path_str;
	}
}
