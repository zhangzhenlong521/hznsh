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
 * һ����ֱ���ֵĿؼ�,���������ؼ���ֱ����,���һ��Զ�����
 * ��˼���������BorderLayout�ݹ����,����ҳ��!!!
 * @author xch
 * 
 * From 2009-06-22
 * 1. ���ټ̳�class JPanel����Ϊ�̳�class WLTPanel
 * 2. �����а����������������䱳������Ϊ͸����
 *
 */
public class VFlowLayoutPanel extends WLTPanel {

	private static final long serialVersionUID = 1L;
	private JComponent[] compents = null; //
	private JScrollPane scollPanel = null; //������

	private JPanel[] panels = null;
	HashMap hm_compents = new HashMap(); //

	private int li_vgap = 7; //�����ؼ�֮��ļ��

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
	 * ��ֿؼ�!!!������ǿ�Ƭ,�򽫿�Ƭ
	 * @param _compents
	 * @return
	 */
	private JComponent[] getSplitCompents(JComponent[] _compents) {
		ArrayList al_newCompents = new ArrayList(); //
		for (int i = 0; i < _compents.length; i++) {
			if (_compents[i] instanceof BillCardPanel) { //����ǿ�Ƭ,�򽫿�Ƭ�𿪼���!!!
				BillCardPanel cardPanel = (BillCardPanel) _compents[i]; //
				JComponent[] cardCompents = cardPanel.getHflowPanels(); //
				for (int j = 0; j < cardCompents.length; j++) { //
					al_newCompents.add(cardCompents[j]); ////
				}
			} else if (_compents[i] instanceof BillListPanel) { //������б�!!!
				BillListPanel listPanel = (BillListPanel) _compents[i]; //
				//al_newCompents.add(new HFlowLayoutPanel(new JComponent[] { listPanel }, bgcolor)); //��ˮƽ�����ٰ�װһ��!!!
				al_newCompents.add(new HFlowLayoutPanel(new JComponent[] { listPanel })); //��ˮƽ�����ٰ�װһ��!!!
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

		for (int i = compents.length - 1; i >= 0; i--) { //�����һ����ǰ��!!
			if (i == compents.length - 1) { //��������һ��,����CENTER�м���һ�������!!!
				panels[i].add(compents[i], BorderLayout.NORTH); //
				JPanel freePanel = new WLTPanel();
				freePanel.setPreferredSize(new Dimension(0, 0)); //
				panels[i].add(freePanel, BorderLayout.CENTER); //
			} else {
				panels[i].add(compents[i], BorderLayout.NORTH); //
				panels[i].add(panels[i + 1], BorderLayout.CENTER); //
			}
		}

		if (panels.length > 0) { //�����ȴ���0
			panels[0].setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0)); //
			scollPanel = new JScrollPane(panels[0]); //������..
			scollPanel.setBorder(BorderFactory.createEmptyBorder()); //
			scollPanel.setOpaque(false);
			scollPanel.getViewport().setOpaque(false);

			//���ô�ֱ��������ٶ�
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
