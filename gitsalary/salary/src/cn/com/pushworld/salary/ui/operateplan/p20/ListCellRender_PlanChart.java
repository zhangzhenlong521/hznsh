/**************************************************************************
 * $RCSfile: ListCellRender_CheckBox.java,v $  $Revision: 1.5 $  $Date: 2012/10/08 02:22:49 $
 **************************************************************************/
package cn.com.pushworld.salary.ui.operateplan.p20;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.report.chart.BillChartPanel;
/**
 * 年度经营计划统计列表界面的柱状图展现【李春娟/2014-01-16】
 * @author lcj
 *
 */
public class ListCellRender_PlanChart extends JPanel implements TableCellRenderer {
	private static final long serialVersionUID = -8423383065033861538L;
	private Pub_Templet_1_ItemVO itemvo;

	public ListCellRender_PlanChart(Pub_Templet_1_ItemVO _itemVO) {
		this.itemvo = _itemVO; //
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {
		RefItemVO refitemvo = (RefItemVO) value;
		if (refitemvo == null) {
			return new JPanel();
		}
		org.jfree.chart.JFreeChart chart = BillChartPanel.getInstance().createBarChart2("柱形图", "", "", new String[] { "" }, new String[] { "计划值", "实际值" },false, new double[][] { { Double.parseDouble(refitemvo.getId()), Double.parseDouble(refitemvo.getName())} }, false,new Color[]{Color.RED,Color.BLUE,Color.GREEN}); //
		org.jfree.chart.ChartPanel chartPanel = new org.jfree.chart.ChartPanel(chart); //
		return chartPanel;
	}

	 

}
