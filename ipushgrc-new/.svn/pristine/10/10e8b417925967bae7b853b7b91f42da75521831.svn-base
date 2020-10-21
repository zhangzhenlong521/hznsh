package com.pushworld.ipushgrc.ui.wfrisk.p010;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JPanel;

import org.jgraph.graph.DefaultGraphCell;

import cn.com.infostrategy.to.workflow.design.ActivityVO;
import cn.com.infostrategy.to.workflow.design.ProcessVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.workflow.design.WorkFlowCellSelectedEvent;
import cn.com.infostrategy.ui.workflow.design.WorkFlowCellSelectedListener;

/**
 * 自定义一图两表按钮面板类的实现类【李春娟/2012-03-07】
 * 以后项目中可参照此类对一图两表的流程相关面板和环节相关面板进行个性化添加
 * @author lichunjuan
 *
 */
public class WFGraphUserDefinedImpl implements ActionListener, WorkFlowCellSelectedListener {
	private JPanel ref_panel1 = null;//自定义的流程相关面板
	private JPanel ref_panel2 = null;//自定义的环节相关面板
	private WFGraphEditItemPanel itempanel = null;//流程文件的单个流程图的设计面板及流程的所有相关信息的面板
	private WLTButton btn_target;//流程相关新增的相关指标
	private WLTButton btn_target2;//环节相关新增的相关指标，在选择不同环节时，按钮上的记录条数不同

	/**
	 * 可以从参数_hashmap.get("itempanel")获得WFGraphEditItemPanel句柄，从而得到WorkFlowDesignWPanel即流程设计面板，可添加面板选择事件等。
	 * 返回的HashMap中可配置key为"流程相关面板"或"环节相关面板"分别对应流程相关自定义的面板和环节相关自定义的面板
	 * 
	 * @param _hashmap
	 * @return
	 */
	public HashMap getRefPanels(HashMap _hashmap) {
		itempanel = (WFGraphEditItemPanel) _hashmap.get("itempanel");//获得句柄，很重要
		itempanel.getWorkFlowPanel().addWorkFlowCellSelectedListener(this);//这里来设置自定义按钮的记录条数

		ref_panel1 = new JPanel();
		ref_panel1.setOpaque(false);//设置面板透明
		ref_panel1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		ref_panel1.setPreferredSize(new Dimension(itempanel.getBTN_WIDTH(), 30));//面板宽度为itempanel.BTN_WIDTH，与系统默认的相关按钮一致，高度可根据添加的按钮个数进行调整

		btn_target = new WLTButton("相关指标");
		btn_target.setPreferredSize(new Dimension(itempanel.getBTN_WIDTH(), itempanel.getBTN_HEIGHT()));//需要设置按钮的大小与系统默认的相关按钮一致
		btn_target.addActionListener(this);//添加按钮事件

		ref_panel1.add(btn_target);

		ref_panel2 = new JPanel();
		ref_panel2.setOpaque(false);//设置面板透明
		ref_panel2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		ref_panel2.setPreferredSize(new Dimension(itempanel.getBTN_WIDTH(), 30));//面板宽度为itempanel.BTN_WIDTH，与系统默认的相关按钮一致，高度可根据添加的按钮个数进行调整

		btn_target2 = new WLTButton("相关指标");
		btn_target2.setPreferredSize(new Dimension(itempanel.getBTN_WIDTH(),  itempanel.getBTN_HEIGHT()));//需要设置按钮的大小与系统默认的相关按钮一致
		btn_target2.addActionListener(this);//添加按钮事件

		ref_panel2.add(btn_target2);

		if (ClientEnvironment.isAdmin()) {// 如果是管理员身份，设置相关按钮的提示信息
			setRefBtnToolTipText(new WLTButton[] { btn_target }, WFGraphEditItemPanel.TYPE_WF);
			setRefBtnToolTipText(new WLTButton[] { btn_target2 }, WFGraphEditItemPanel.TYPE_ACTIVITY);
		}
		_hashmap.put("流程相关面板", ref_panel1);//很重要，需要添加到_hashmap中，才能有效果
		_hashmap.put("环节相关面板", ref_panel2);
		return _hashmap;
	}

	/**
	 * 按钮的点击事件
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_target) {//这里应该弹出相关记录
			ProcessVO pvo = itempanel.getWorkFlowPanel().getCurrentProcessVO();
			MessageBox.show(itempanel, "流程[" + pvo.getName() + "]的相关指标");//这里应该有段逻辑弹出该流程的相关指标
		} else if (e.getSource() == btn_target2) {//这里应该弹出相关记录
			ProcessVO pvo = itempanel.getWorkFlowPanel().getCurrentProcessVO();
			ActivityVO actvo = itempanel.getWorkFlowPanel().getSelectedActivityVO();
			if (actvo == null) {
				MessageBox.show(itempanel, "请选择一个环节进行此操作!");
				return;
			} else {
				MessageBox.show(itempanel, "流程[" + pvo.getName() + "]中环节[" + actvo.getWfname() + "]的相关指标");//这里应该有段逻辑弹出被选中环节的相关指标
			}
		}
	}

	/**
	 * 流程图选中监听事件,重设环节相关按钮文字
	 */
	public void onWorkFlowCellSelected(WorkFlowCellSelectedEvent _event) {
		Object selectCell = itempanel.getWorkFlowPanel().getGraph().getSelectionCell();
		if (selectCell instanceof DefaultGraphCell) {
			Object userobj = ((DefaultGraphCell) selectCell).getUserObject();
			if (userobj instanceof ActivityVO) {//判断选中的是否是环节
				ActivityVO activityvo = (ActivityVO) userobj;
				if (activityvo != null) {
					String activityid = activityvo.getId().toString();
					btn_target2.setText("相关指标(" + activityid + ")");//这里应该有段逻辑计算出选中环节的相关指标条数！
				} else {
					btn_target2.setText("相关指标");
					return;
				}
			} else {
				btn_target2.setText("相关指标");
			}
		} else {
			btn_target2.setText("相关指标");
		}
	}

	/**
	 * 如果是管理员身份，设置相关按钮的提示信息
	 * @param _btns
	 * @param _type
	 */
	private void setRefBtnToolTipText(WLTButton[] _btns, String _type) {
		for (WLTButton btn : _btns) {
			btn.setToolTipText("通过系统参数[自定义一图两表按钮面板类]设置的" + _type + "相关按钮");
		}
	}
}
