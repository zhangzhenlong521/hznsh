package com.pushworld.ipushgrc.bs.keywordreplace;

import java.util.HashMap;

/**
 * 传入的HashMap格式为，key为模板中的关键字，vlaue为文字或图片（当内容为文字时，直接是String,为图片时，value应为一个
 * HashMap(key为img,value为图片File或图片的Base64码)，如果要设置图片高度和宽度，则KEY必须用width,height,value为int类型）
 * @author yyb
 * Jul 25, 2011 9:33:28 AM
 */
public class KeyWordReplaceContext {
	
	protected String template_name;//模板名称
	protected HashMap[] maps;
	protected AbstractKeyWordReplace  replace_obj;
	
	public KeyWordReplaceContext(HashMap[] maps,String template_name) {
		this.template_name = template_name;
		this.maps=maps;
		if(template_name.toLowerCase().endsWith(".xml")){
			replace_obj=new  XMLKeyWordReplace(maps, template_name);
		}else{
			replace_obj=new  MHTKeyWordReplace(maps, template_name);
		}
	}

	public String[] getContents() throws Exception{
		return replace_obj.getContents();
	}
	
	public String getTemplate_name() {
		return template_name;
	}

	public void setTemplate_name(String template_name) {
		this.template_name = template_name;
	}
}
