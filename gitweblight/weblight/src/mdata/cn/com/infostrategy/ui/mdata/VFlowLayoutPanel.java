package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import cn.com.infostrategy.ui.common.BackGroundDrawingUtil;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractWLTCompentPanel;

/**
 * 一个垂直布局的控件,即将各个控件垂直布局,而且会自动增高
 * 其思想就是利用BorderLayout递归叠加,构成页面!!!
 * @author xch
 * 
 * From 2009-06-22
 * 1. 不再继承class JPanel，改为继承class WLTPanel
 * 2. 该类中包含的所有容器，其背景皆设为透明的
 *
 */
public class VFlowLayoutPanel extends WLTPanel {

	private static final long serialVersionUID = 1L;
	private JComponent[] compents = null; //
	private JScrollPane scollPanel = null; //滚动框

	private JPanel[] panels = null;
	HashMap hm_compents = new HashMap(); //

	private int li_vgap = 7; //各个控件之间的间距

	public VFlowLayoutPanel(java.util.List _list) {
		super();
		JComponent[] oldcompents = (JComponent[]) _list.toArray(new JComponent[0]); //
		compents = getSplitCompents(oldcompents); //
		initialize(); //
	}

	public VFlowLayoutPanel(java.util.List _list, int _vgap) {
		super();
		JComponent[] oldcompents = (JComponent[]) _list.toArray(new JComponent[0]); //
		compents = getSplitCompents(oldcompents); //
		this.li_vgap = _vgap; //
		initialize(); //
	}

	public VFlowLayoutPanel(java.util.List _list, Color _bgcolor) {
		super(BackGroundDrawingUtil.SINGLE_COLOR_UNCHANGE, _bgcolor);
		JComponent[] oldcompents = (JComponent[]) _list.toArray(new JComponent[0]); //
		compents = getSplitCompents(oldcompents); //
		initialize(); //
	}

	public VFlowLayoutPanel(java.util.List _list, int _shadeType, Color _bgcolor) {
		super(_shadeType, _bgcolor);
		JComponent[] oldcompents = (JComponent[]) _list.toArray(new JComponent[0]); //
		compents = getSplitCompents(oldcompents); //
		initialize(); //
	}

	/**
	 * 
	 */
	public VFlowLayoutPanel(JComponent[] _compents) {
		super();
		compents = getSplitCompents(_compents); //
		initialize(); //
	}

	public VFlowLayoutPanel(JComponent[] _compents, Color _bgcolor) {
		super(BackGroundDrawingUtil.SINGLE_COLOR_UNCHANGE, _bgcolor);
		compents = getSplitCompents(_compents); //
		initialize(); //
	}

	public VFlowLayoutPanel(JComponent[] _compents, int _shadeType, Color _bgcolor) {
		super(_shadeType, _bgcolor);
		compents = getSplitCompents(_compents); //
		initialize(); //
	}

	/**
	 * 拆分控件!!!即如果是卡片,则将卡片
	 * @param _compents
	 * @return
	 */
	private JComponent[] getSplitCompents(JComponent[] _compents) {
		ArrayList al_newCompents = new ArrayList(); //
		for (int i = 0; i < _compents.length; i++) {
			if (_compents[i] instanceof BillCardPanel) { //如果是卡片,则将卡片拆开加入!!!
				BillCardPanel cardPanel = (BillCardPanel) _compents[i]; //
				JComponent[] cardCompents = cardPanel.getHflowPanels(); //
				for (int j = 0; j < cardCompents.length; j++) { //
					al_newCompents.add(cardCompents[j]); ////
				}
			} else if (_compents[i] instanceof BillListPanel) { //如果是列表!!!
				BillListPanel listPanel = (BillListPanel) _compents[i]; //
				//al_newCompents.add(new HFlowLayoutPanel(new JComponent[] { listPanel }, bgcolor)); //用水平布局再包装一下!!!
				al_newCompents.add(new HFlowLayoutPanel(new JComponent[] { listPanel })); //用水平布局再包装一下!!!
			} else {
				al_newCompents.add(_compents[i]); ////
			}
		}
		return (JComponent[]) al_newCompents.toArray(new JComponent[0]); //
	}

	public void setRowItemVisiable(int _row, boolean _visiable) {
		JComponent compent = compents[_row]; //
		if (compent instanceof HFlowLayoutPanel) {
			HFlowLayoutPanel flowPanel = (HFlowLayoutPanel) compent; //
			JComponent[] rowCompents = flowPanel.getAllCompents(); //
			for (int j = 0; j < rowCompents.length; j++) { //
				AbstractWLTCompentPanel realCompent = (AbstractWLTCompentPanel) rowCompents[j]; //
				realCompent.setVisible(_visiable);
			}
		}
	}

	private void initialize() {
		BorderLayout layout = new BorderLayout(); //
		this.setLayout(layout); //
		this.setOpaque(false);
		panels = new JPanel[compents.length]; //
		for (int i = 0; i < panels.length; i++) {
			panels[i] = new WLTPanel(new BorderLayout(0, li_vgap)); //!!
		}

		for (int i = compents.length - 1; i >= 0; i--) { //从最后一个往前加!!
			if (i == compents.length - 1) { //如果是最后一个,则在CENTER中加入一个空面板!!!
				panels[i].add(compents[i], BorderLayout.NORTH); //
				JPanel freePanel = new WLTPanel();
				freePanel.setPreferredSize(new Dimension(0, 0)); //
				panels[i].add(freePanel, BorderLayout.CENTER); //
			} else {
				panels[i].add(compents[i], BorderLayout.NORTH); //
				panels[i].add(panels[i + 1], BorderLayout.CENTER); //
			}
		}

		if (panels.length > 0) { //如果宽度大于0
			panels[0].setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0)); //
			scollPanel = new JScrollPane(panels[0]); //滚动框..
			scollPanel.setBorder(BorderFactory.createEmptyBorder()); //
			scollPanel.setOpaque(false);
			scollPanel.getViewport().setOpaque(false);

			//设置垂直方向滚动速度
			scollPanel.getVerticalScrollBar().setUnitIncrement(100);
			this.add(scollPanel); //
		}
	}

	
	
	public void setRowVisiable(int _row, boolean _visiable) {
		compents[_row].setVisible(_visiable); //
	}

	public JComponent getContentPanel() {
		return panels[0]; //
	}

	public int getContentCount() {
		return panels.length; //
	}

	public JScrollPane getScollPanel() {
		return scollPanel;
	}

	public JComponent[] getAllCompents() {
		return compents;
	}

}
