package cn.com.infostrategy.to.common;

import java.util.LinkedList;

/**
 * 队列对象,从屁股后面插入,从头部取出 ,但他可以控制整个的大小!!
 *  push(obj);  插入对象 
 *  pop();  取对象
 * @author user
 * 
 */
public class Queue extends LinkedList {

	private static final long serialVersionUID = 1L;
	private int queueMaxSize = -1; //容器大小

	public Queue() {
		super();
	}

	public Queue(int _maxsize) {
		super();
		queueMaxSize = _maxsize; //
	}

	//在尾部加入一个对象
	public void push(Object x) {
		super.addLast(x); //在后面增加!!
		try {
			if (queueMaxSize > 0) { //如果指定了空间上限!!
				int li_size = size(); //目前的大小!
				if (li_size > queueMaxSize) { //如果大小已超过空间上限,则删除前面多余的,比如上限大小是5,现在是6了,则删除0,1
					super.removeRange(0, (li_size - queueMaxSize)); //删除范围!!!
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	// 从前面取出一个,同时删除之..
	public Object pop() {
		return super.poll(); //
	}

	// 取出第一个但不删除它
	public Object front() {
		return super.peek(); //
	}

	// 判断是否为空
	public boolean empty() {
		return super.isEmpty();
	}

	// 查找
	public int search(Object x) {
		return super.indexOf(x);
	}

	/**
	 * 取得队列的最大上限.
	 * @return
	 */
	public int getQueueMaxSize() {
		return this.queueMaxSize;
	}

}
