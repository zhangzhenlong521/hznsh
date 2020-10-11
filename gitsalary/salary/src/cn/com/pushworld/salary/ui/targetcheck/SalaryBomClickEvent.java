package cn.com.pushworld.salary.ui.targetcheck;

import java.util.EventObject;

import cn.com.infostrategy.to.common.HashVO;

public class SalaryBomClickEvent extends EventObject {
	private static final long serialVersionUID = 3151638227355132577L;
	private HashVO hashvo; // 一般使用hashvo
	private Object other;// 可以用obj

	public Object getOther() {
		return other;
	}

	public void setOther(Object other) {
		this.other = other;
	}

	public SalaryBomClickEvent(Object _source, HashVO _hashvo) {
		super(_source);
		hashvo = _hashvo;
	}

	public SalaryBomClickEvent(Object _source, Object _other) {
		super(_source);
		other = _other;
	}

	public HashVO getHashvo() {
		return hashvo;
	}

	public void setHashvo(HashVO hashvo) {
		this.hashvo = hashvo;
	}
}
