package cn.com.infostrategy.to.common;

import java.util.LinkedList;

/**
 * ���ж���,��ƨ�ɺ������,��ͷ��ȡ�� ,�������Կ��������Ĵ�С!!
 *  push(obj);  ������� 
 *  pop();  ȡ����
 * @author user
 * 
 */
public class Queue extends LinkedList {

	private static final long serialVersionUID = 1L;
	private int queueMaxSize = -1; //������С

	public Queue() {
		super();
	}

	public Queue(int _maxsize) {
		super();
		queueMaxSize = _maxsize; //
	}

	//��β������һ������
	public void push(Object x) {
		super.addLast(x); //�ں�������!!
		try {
			if (queueMaxSize > 0) { //���ָ���˿ռ�����!!
				int li_size = size(); //Ŀǰ�Ĵ�С!
				if (li_size > queueMaxSize) { //�����С�ѳ����ռ�����,��ɾ��ǰ������,�������޴�С��5,������6��,��ɾ��0,1
					super.removeRange(0, (li_size - queueMaxSize)); //ɾ����Χ!!!
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	// ��ǰ��ȡ��һ��,ͬʱɾ��֮..
	public Object pop() {
		return super.poll(); //
	}

	// ȡ����һ������ɾ����
	public Object front() {
		return super.peek(); //
	}

	// �ж��Ƿ�Ϊ��
	public boolean empty() {
		return super.isEmpty();
	}

	// ����
	public int search(Object x) {
		return super.indexOf(x);
	}

	/**
	 * ȡ�ö��е��������.
	 * @return
	 */
	public int getQueueMaxSize() {
		return this.queueMaxSize;
	}

}
