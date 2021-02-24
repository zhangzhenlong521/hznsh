package cn.com.infostrategy.ui.workflow.design;

import java.util.EventObject;

public class WorkFlowCellDeleteEvent extends EventObject {

	private Object[] cells = null;

	public WorkFlowCellDeleteEvent(Object[] source) {
		super(source);
		this.cells = source;
	}

	/**
	 * �õ�ɾ���Ķ���
	 */
	public Object[] getSource() {
		return this.cells;
	}

}
