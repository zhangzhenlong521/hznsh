package com.pushworld.ipushgrc.ui.law.p010;
import com.pushworld.ipushgrc.ui.rule.p010.RuleContentVO;

public class ContentTree {
	private String[] titleid;

	public ContentTree(String[] allid) {
		titleid = allid;
	}

	public LawContentVO parseLaw(String content) {
		LawContentVO list = new LawContentVO(titleid, content);
		return list;
	}

	public RuleContentVO parseRule(String content) {
		RuleContentVO list = new RuleContentVO(titleid, content);
		return list;
	}
	
}
