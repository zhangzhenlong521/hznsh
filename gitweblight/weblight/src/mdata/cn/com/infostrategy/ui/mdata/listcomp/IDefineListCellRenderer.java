package cn.com.infostrategy.ui.mdata.listcomp;

import javax.swing.JComponent;
import javax.swing.JTable;

/**
 * �������б��ϼ����Զ���Ļ�����
 * @author gaofeng
 *
 */
public interface IDefineListCellRenderer {

	public void defineRenderer(JComponent component,JTable table,Object value, boolean isSelected, boolean hasFocus, int row, int column);
}
