package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.UIUtil;

import com.l2fprod.common.swing.JTaskPane;
import com.l2fprod.common.swing.JTaskPaneGroup;

public class BillOptionPanel extends JPanel {

	private static final long serialVersionUID = 2886708816880735319L;

	private Pub_Templet_1VO pub_Templet_1VO = null; //
	private Pub_Templet_1_ItemVO[] pub_Templet_1_ItemVOs = null; //

	public BillOptionPanel(Pub_Templet_1VO _pub_Templet_1VO) {
		this.pub_Templet_1VO = _pub_Templet_1VO;
		//this.pub_Templet_1_ItemVOs = _pub_Templet_1VO.getItemVos();
		initialize();
	}

	private void initialize() {
		this.setLayout(new BorderLayout());

		JTaskPane taskPane = new JTaskPane();
		taskPane.setBorder(BorderFactory.createEmptyBorder());  //
		//taskPane.setBackground(Color.LIGHT_GRAY); //
		
		taskPane.add(getGroupPanel("ื้1"));
		taskPane.add(getGroupPanel("ื้2"));
		taskPane.add(getGroupPanel("ื้3"));
		this.add(new JScrollPane(taskPane));
	}

	private JTaskPaneGroup getGroupPanel(String _title) {
		JTaskPaneGroup groupPanel = new JTaskPaneGroup();
		//groupPanel.setBorder(BorderFactory.createMatteBorder(300,2,300,2,Color.GRAY));  //
		//waitdealtaskgroup.setPreferredSize(new Dimension(300,19));  
		
		groupPanel.setFont(LookAndFeel.font);
		groupPanel.setCollapsable(true);
		groupPanel.setExpanded(true);
		groupPanel.setScrollOnExpand(false);
		
		groupPanel.setAnimated(false);  //
		groupPanel.setTitle(UIUtil.getLanguage(_title)); //
		groupPanel.setIcon(UIUtil.getImage("insert.gif")); //
		groupPanel.setBackground(Color.LIGHT_GRAY); //
		groupPanel.setForeground(Color.LIGHT_GRAY);  //
		//groupPanel.setSpecial(true);

		JPanel panel = new JPanel();
		
		panel.setLayout(new BorderLayout());  //
		panel.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 0, Color.LIGHT_GRAY));  //
		JTable table = new JTable(new String[][] { // 
				{ "a01", "a02", "a03" }, //
						{ "b01", "b02", "b03" }, //
						{ "c01", "c02", "c03" }, //
				}, new String[] { "id", "code", "name" });
		table.setGridColor(Color.LIGHT_GRAY);  //
		panel.add(table);
		groupPanel.getContentPane().add(panel); //
		return groupPanel;
	}
}
