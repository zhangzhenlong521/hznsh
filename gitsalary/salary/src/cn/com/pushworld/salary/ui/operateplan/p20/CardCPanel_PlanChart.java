/**************************************************************************
 * $RCSfile: CardCPanel_ChildTable.java,v $  $Revision: 1.19 $  $Date: 2012/10/08 02:22:48 $
 **************************************************************************/
package cn.com.pushworld.salary.ui.operateplan.p20;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractWLTCompentPanel;
import cn.com.infostrategy.ui.report.chart.BillChartPanel;

/**
 * 年度经营计划统计卡片界面的柱状图展现【李春娟/2014-01-16】
 * @author lcj
 *
 */
public class CardCPanel_PlanChart extends AbstractWLTCompentPanel implements ActionListener {

	private Pub_Templet_1_ItemVO templetItemVO = null;

	private String key = null;
	private String name = null;

	private JLabel label = null;
	private int li_width_all;

	public CardCPanel_PlanChart(Pub_Templet_1_ItemVO _templetVO, BillPanel _billPanel) {
		super();
		this.templetItemVO = _templetVO; //
		this.key = templetItemVO.getItemkey(); //
		this.name = templetItemVO.getItemname(); //
		this.billPanel = _billPanel; //
		initialize();
	}

	private void initialize() {
		try {
			this.setLayout(new BorderLayout()); //
			this.setBackground(LookAndFeel.cardbgcolor); //
			int li_tablewidth = 300; //
			if (templetItemVO.getCardwidth() != null) {
				li_tablewidth = templetItemVO.getCardwidth().intValue(); //
			}

			int li_tableheight = 80; //
			if (templetItemVO.getCardHeight() != null) {
				li_tableheight = templetItemVO.getCardHeight().intValue(); //
			}

			if (li_tableheight < 80) {
				li_tableheight = 80;
			}

			label = createLabel(templetItemVO); //采用父亲提供的方法创建Label
			li_width_all = (int) (label.getPreferredSize().getWidth() + li_tablewidth); ////
			this.setPreferredSize(new Dimension(li_width_all, li_tableheight)); //
			this.add(label, BorderLayout.WEST); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			this.removeAll(); //
			this.setLayout(new BorderLayout()); //
			this.setPreferredSize(new Dimension(100, 20)); //
			this.add(new JLabel("加载页面发生异常:" + ex.getClass().getName() + ":" + ex.getMessage())); //
		}
	}

	public JLabel getLabel() {
		return label;
	}

	public String getItemKey() {
		return key;
	}

	public String getItemName() {
		return name;
	}

	public void setItemVisiable(boolean _bo) {
		this.setVisible(_bo); //
	}

	@Override
	public void focus() {

	}

	@Override
	public int getAllWidth() {
		return 0;
	}

	@Override
	public Object getObject() {
		return null;
	}

	@Override
	public String getValue() {
		return null;
	}

	@Override
	public boolean isItemEditable() {
		return false;
	}

	@Override
	public void reset() {

	}

	@Override
	public void setItemEditable(boolean bo) {

	}

	@Override
	public void setObject(Object obj) {
		RefItemVO refitemvo = (RefItemVO) obj;
		if (refitemvo == null) {
			this.add(new JPanel(), BorderLayout.CENTER); //
		} else {
			org.jfree.chart.JFreeChart chart = BillChartPanel.getInstance().createBarChart("柱形图", "", "", new String[] { "" }, new String[] { "计划值", "实际值" }, new double[][] { { Double.parseDouble(refitemvo.getId()), Double.parseDouble(refitemvo.getName()) } }, true, null); //
			org.jfree.chart.ChartPanel chartPanel = new org.jfree.chart.ChartPanel(chart); //
			this.add(chartPanel, BorderLayout.CENTER); //
		}
	}

	@Override
	public void setValue(String value) {

	}

	public void actionPerformed(ActionEvent e) {

	}

}
