package com.pushworld.ipushgrc.ui.wfrisk;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

import cn.com.infostrategy.to.workflow.design.ActivityVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.workflow.design.WorkFlowDesignWPanel;

import com.pushworld.ipushgrc.ui.wfrisk.p010.WFGraphEditItemPanel;

/**
 * 流程文件的单个流程图的设计面板及流程的所有相关信息的窗口
 * 注意修改此类，要考虑是否同时修改WFGraphEditItemFrame类！！！
 * @author lcj
 *
 */

public class WFGraphEditItemDialog extends BillDialog implements ActionListener {
	private String processid;// 当前流程id
	private boolean editable;
	private boolean showRefPanel;
	private WFGraphEditItemPanel itempanel;
	private String[] activityIds = null;
	private WLTButton btn_confirm, btn_cancel;

	public WFGraphEditItemPanel getItempanel() {
		return itempanel;
	}

	public WFGraphEditItemDialog(Container _parentContainer, String _title, String _processid, boolean _editable, boolean _showRefPanel) {
		this(_parentContainer, _title, _processid, _editable, _showRefPanel, 1000, 700);
	}

	public WFGraphEditItemDialog(Container _parentContainer, String _title, String _processid, boolean _editable, boolean _showRefPanel, int _width, int _height) {
		this(_parentContainer, _title, _processid, null, _editable, _showRefPanel, _width, _height);
	}

	// 可以传入高亮环节框数组
	public WFGraphEditItemDialog(Container _parentContainer, String _title, String _processid, String[] _activityid, boolean _editable, boolean _showRefPanel) {
		this(_parentContainer, _title, _processid, _activityid, _editable, _showRefPanel, 1000, 700);
	}

	/**
	 * 
	 * @param _parentContainer
	 *            父面板
	 * @param _title
	 *            对话框名称
	 * @param _processid
	 *            流程ID
	 * @param _activityid
	 *            需要高亮的环节
	 * @param _editable
	 *            是否可编辑
	 * @param _showRefPanel
	 *            是否显示按钮面板
	 * @param _width
	 *            宽
	 * @param _height
	 *            高
	 */
	public WFGraphEditItemDialog(Container _parentContainer, String _title, String _processid, String[] _activityid, boolean _editable, boolean _showRefPanel, int _width, int _height) {
		super(_parentContainer, _title, _width, _height);
		this.processid = _processid;
		this.editable = _editable;
		this.showRefPanel = _showRefPanel;
		this.activityIds = _activityid;
		initialize();
	}

	public void initialize() {
		itempanel = new WFGraphEditItemPanel(this.processid, this.editable, this.showRefPanel);
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(itempanel, BorderLayout.CENTER);
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
		if (activityIds != null && activityIds.length > 0) {
			WorkFlowDesignWPanel wf_wkPanel = itempanel.getWorkFlowPanel();
			DefaultGraphCell[] activityCell = wf_wkPanel.getActivityCells();
			List list = new ArrayList();
			for (int i = 0; i < activityIds.length; i++) {
				String activityID = activityIds[i];
				if (activityID == null || activityID.equals(""))
					continue;
				for (int j = 0; j < activityCell.length; j++) {
					ActivityVO acvo = (ActivityVO) activityCell[j].getUserObject();
					if (acvo != null && activityID.equals(acvo.getId() + "")) {
						DefaultGraphCell cell = activityCell[j];
						list.add(cell);
						GraphConstants.setBorder(cell.getAttributes(), BorderFactory.createLineBorder(Color.BLUE, 2)); //
						break;
					}
				}
			}
			if (list.size() > 0) { //默认变色选中环节。[2012-06-14]
				wf_wkPanel.getGraph().setSelectionCells(list.toArray());
			}
		}
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout()); //
		if (editable) {
			btn_confirm = new WLTButton("确定");
			btn_cancel = new WLTButton("取消");
			btn_cancel.addActionListener(this); //
			btn_confirm.addActionListener(this); //
			panel.add(btn_confirm); //
			panel.add(btn_cancel); //
		} else {
			btn_cancel = new WLTButton("关闭");
			btn_cancel.addActionListener(this); //
			panel.add(btn_cancel); //
		}
		return panel;
	}

	/**
	 * 
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		}
	}

	public void onConfirm() {
		setCloseType(BillDialog.CONFIRM);
		itempanel.isWfEditChanged();
		this.dispose(); //
	}

	public void onCancel() {
		itempanel.deleteAllAddActivity();//取消默认不保存，需要将新增的环节及相关信息删除，否则会产生脏数据【李春娟/2012-08-20】
		setCloseType(BillDialog.CANCEL);
		this.dispose();
	}

	/**
	 * 点击窗口右上角的X，在窗口关闭前做!
	 */
	public void closeMe() {
		onConfirm();
	}
}
