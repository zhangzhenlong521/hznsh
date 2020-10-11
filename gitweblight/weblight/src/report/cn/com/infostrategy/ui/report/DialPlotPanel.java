package cn.com.infostrategy.ui.report;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.jfreechart.chart.JFreeChart;

/**
 * "“«±Ì≈Ã"√Ê∞Â¿‡
 * 
 * @author Liyj
 * 
 */
public class DialPlotPanel extends JPanel {

	private List charts;

	public DialPlotPanel(java.awt.LayoutManager layoutmanager) {
		super(layoutmanager);
		charts = new ArrayList();
	}

	public void addChart(JFreeChart jfreechart) {
		charts.add(jfreechart);
	}

	public JFreeChart[] getCharts() {
		int i = charts.size();
		JFreeChart ajfreechart[] = new JFreeChart[i];
		for (int j = 0; j < i; j++)
			ajfreechart[j] = (JFreeChart) charts.get(j);

		return ajfreechart;
	}

}
