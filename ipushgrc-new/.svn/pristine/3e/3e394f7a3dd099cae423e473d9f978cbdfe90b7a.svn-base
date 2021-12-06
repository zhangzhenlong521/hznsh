package com.pushworld.ipushgrc.ui.indexpage;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.wfrisk.p010.WFAndRiskEditWKPanel;

/**
 * 首页 我的收藏夹 点击事件
 * @author hm
 *
 */
public class MyFavoriteAction extends AbstractAction implements ActionListener {
	JPanel panel;
	HashVO selectVO;
	BillDialog dialog;
	private WLTButton btn_cancel = new WLTButton("关闭");// 弹出对话框的关闭按钮

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_cancel) {
			if (dialog != null) {
				dialog.dispose();
			}
		} else {
			panel = (JPanel) this.getValue("DeskTopPanel");
			selectVO = (HashVO) this.getValue("DeskTopNewsDataVO");
			showMyFavorite(selectVO);
		}
	}

	public void showMyFavorite(HashVO vo) {
		BillListPanel listPanel = null;
		AbstractWorkPanel queryPanel = null;
		String classPath = vo.getStringValue("classpath");
		if (classPath == null || classPath.equals("")) {
			MessageBox.show(panel, "类路径为空！");
			return;
		}
		if (classPath.contains("WFAndRiskEditWKPanel")) { // 这是个流程维护面板。特殊处理
			WFAndRiskEditWKPanel wk = new WFAndRiskEditWKPanel();
			wk.setEditable(false);
			wk.initialize();
			listPanel = wk.getBillList_cmpfile();
			listPanel.QueryDataByCondition(" id = " + vo.getStringValue("itemid"));
			listPanel.setDataFilterCustCondition(" id = " + vo.getStringValue("itemid"));
			queryPanel = wk;
		} else {
			try {
				queryPanel = (AbstractWorkPanel) Class.forName(classPath).newInstance(); // 通过类路径加载面板
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			if (queryPanel == null) {
				MessageBox.show(panel, "出错！模板类反射加载失败！");
				return;
			}
			queryPanel.setLayout(new BorderLayout()); //
			queryPanel.initialize();
			Component com = queryPanel.getComponent(0); //得到BillListPanel
			listPanel = null;
			if (com instanceof BillListPanel) {
				listPanel = (BillListPanel) com;
				listPanel.QueryDataByCondition(" id = " + vo.getStringValue("itemid")); //设置显示数据唯一
				listPanel.setDataFilterCustCondition(" id = " + vo.getStringValue("itemid"));//设置显示数据唯一
			}
		}
		listPanel.getQuickQueryPanel().setVisible(false); //隐藏到快速查询面板！
		WLTButton joinFavority = listPanel.getBillListBtn("加入收藏"); //得到面板中的收藏按钮
		if (joinFavority != null) {
			joinFavority.setVisible(false); // 隐藏到收藏按钮
		}
		dialog = new BillDialog(panel, 800, 300);
		dialog.setTitle("我的收藏夹→" + vo.getStringValue("itemtype"));
		btn_cancel.addActionListener(this);
		WLTPanel btn_pane = new WLTPanel(WLTPanel.HORIZONTAL_LEFT_TO_RIGHT, new FlowLayout(), LookAndFeel.defaultShadeColor1, true);
		btn_pane.add(btn_cancel);
		dialog.add(queryPanel, BorderLayout.CENTER);
		dialog.add(btn_pane, BorderLayout.SOUTH);
		dialog.setVisible(true);
	}
}
