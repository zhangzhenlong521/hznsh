package cn.com.infostrategy.ui.mdata.listcomp;

import javax.swing.JComponent;
import javax.swing.JTable;

/**
 * 用于在列表上加入自定义的绘制器
 * @author gaofeng
 *
 */
public interface IDefineListCellRenderer {

	public void defineRenderer(JComponent component,JTable table,Object value, boolean isSelected, boolean hasFocus, int row, int column);
}
