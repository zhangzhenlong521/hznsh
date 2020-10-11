package cn.com.pushworld.salary.ui.targetcheck;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.I_MyJScrollPane;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.VFlowLayoutPanel;

public class SalaryBomPanel extends JPanel implements ActionListener {
	private CardLayout cardLayout;
	private LinkedHashMap hisHashMap = new LinkedHashMap();
	private SalaryBomClickListener listener;
	private JPanel contentPanel = new JPanel();
	private WLTButton btn_back = new WLTButton(UIUtil.getImage("zt_072.gif"));
	JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

	public SalaryBomPanel() {
		this.setOpaque(false);
		cardLayout = new CardLayout();
		contentPanel.setOpaque(false);
		contentPanel.setLayout(cardLayout);

		this.setLayout(new BorderLayout());
		btnPanel.setBorder(BorderFactory.createEmptyBorder());
		btn_back.addActionListener(this);
		btn_back.setMargin(new Insets(0, 0, 0, 0));
		btn_back.setPreferredSize(new Dimension(20, 14));
		btnPanel.add(btn_back);
//		this.add(btnPanel, BorderLayout.NORTH);
		this.add(contentPanel, BorderLayout.CENTER);
	}

	// 系统会调用起toString方法。
	// 用flowlayout布局
	public void addBomPanel(List _hashvoList) {
		SalaryBomItemPanel itemPanel = new SalaryBomItemPanel(_hashvoList);
		itemPanel.addActionListener(new SalaryBomClickListener() {
			public void onBomClickListener(SalaryBomClickEvent event) {
				if (listener != null) {
					listener.onBomClickListener(event);
				}
			}
		});
		addBomPanel(itemPanel);
	}

	public void addBomPanel(String[] str) {
		SalaryBomItemPanel itemPanel = new SalaryBomItemPanel(str);
		itemPanel.addActionListener(new SalaryBomClickListener() {
			public void onBomClickListener(SalaryBomClickEvent event) {
				if (listener != null) {
					listener.onBomClickListener(event);
				}
			}
		});
		addBomPanel(itemPanel);
	}

	public void addBomPanel(String[][] str) {
		I_MyJScrollPane scroll = new I_MyJScrollPane(); //
		scroll.setOpaque(false); // 透明!
		scroll.getViewport().setOpaque(false); // 透明!
		scroll.setBorder(BorderFactory.createEmptyBorder());
		SalaryBomItemPanel itemPanel = new SalaryBomItemPanel(str);
		itemPanel.addActionListener(new SalaryBomClickListener() {
			public void onBomClickListener(SalaryBomClickEvent event) {
				if (listener != null) {
					listener.onBomClickListener(event);
				}
			}
		});
		scroll.getViewport().add(itemPanel); // 在滚动框中加入容器面板
		addBomPanel(scroll);
	}

	public void addBomPanel(JComponent _component) {
		String index = hisHashMap.size() + "";
		if (_component instanceof BillListPanel) {
			BillListPanel listPanel = (BillListPanel) _component;
			listPanel.getTitlePanel().add(btn_back, BorderLayout.WEST);
			btnPanel.setVisible(false);
		}
		contentPanel.add(index, _component);
		cardLayout.show(contentPanel, index);
		hisHashMap.put(index, _component);
		backBtnAutoVisible();
	}

	public void addBomClickListener(SalaryBomClickListener _l) {
		listener = _l;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_back) {
			onBack();
		}
	}

	protected void onBack() {
		if (hisHashMap.size() > 1) {
			JComponent jcomponent = (JComponent) hisHashMap.get(String.valueOf(hisHashMap.size() - 1));
			cardLayout.removeLayoutComponent(jcomponent);
			hisHashMap.remove(String.valueOf(hisHashMap.size() - 1)); // 移除
			cardLayout.show(contentPanel, hisHashMap.size() + "");
			backBtnAutoVisible();
		}
	}

	private void backBtnAutoVisible() {
		btn_back.setVisible(hisHashMap.size() > 1 ? true : false);
	}
}

class SalaryBomItemPanel extends JPanel implements ActionListener {
	private SalaryBomClickListener listener;

	public SalaryBomItemPanel(List _hashvoList) {
		this.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 15));
		for (int i = 0; i < _hashvoList.size(); i++) {
			HashVO vo = (HashVO) _hashvoList.get(i);
			WLTButton btn = new WLTButton(vo.toString());
			btn.putClientProperty("obj", vo);
			// btn.putClientProperty("index", i);
			btn.addActionListener(this);
			btn.setBackground(TBUtil.getTBUtil().getColor("f6f5ec"));
			btn.setPreferredSize(new Dimension(80, 60));
			this.add(btn);
		}
	}

	public SalaryBomItemPanel(String[] _str) {
		this.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 15));
		for (int i = 0; i < _str.length; i++) {
			WLTButton btn = new WLTButton(_str[i]);
			btn.putClientProperty("obj", _str[i]);
			btn.putClientProperty("index", i);
			btn.addActionListener(this);
			btn.setBackground(TBUtil.getTBUtil().getColor("f6f5ec"));
			btn.setPreferredSize(new Dimension(80, 60));
			this.add(btn);
		}
	}

	public SalaryBomItemPanel(String[][] _str) {
		this.setOpaque(false);
		List panelList = new ArrayList();
		for (int i = 0; i < _str.length; i++) {
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 15));
			for (int j = 0; j < _str[i].length; j++) {
				WLTButton btn = new WLTButton(_str[i][j]);
				btn.putClientProperty("obj", _str[i][j]);
				// btn.putClientProperty("index", i * 10 + j);
				btn.addActionListener(this);
				btn.setBackground(TBUtil.getTBUtil().getColor("f6f5ec"));
				btn.setPreferredSize(new Dimension(80, 60));
				panel.add(btn);
			}
			panelList.add(panel);
		}
		VFlowLayoutPanel vv = new VFlowLayoutPanel(panelList);
		this.add(vv);
	}

	public void addActionListener(SalaryBomClickListener _listener) {
		listener = _listener;
	}

	public void actionPerformed(ActionEvent e) {
		if (listener != null) {
			WLTButton btn = (WLTButton) e.getSource();
			Object obj = btn.getClientProperty("obj");
			if (obj instanceof HashVO) {
				listener.onBomClickListener(new SalaryBomClickEvent(e.getSource(), (HashVO) obj));
			} else {
				listener.onBomClickListener(new SalaryBomClickEvent(e.getSource(), obj));
			}
		}
	}
}
