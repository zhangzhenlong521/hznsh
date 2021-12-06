package com.pushworld.ipushgrc.ui.icheck.ref;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

/**
 * 左边目录，右边问题词条【李春娟/2016-09-29】
 * 检查实施时，实际问题关联问题词条
 * @author lcj
 *
 */
public class RefDialog_Problem_dict extends AbstractRefDialog implements ActionListener, BillTreeSelectListener {

	private BillTreePanel billTreePanel = null; //
	private BillListPanel listPro = null; //
	private WLTButton btn_confirm, btn_cancel;
	private RefItemVO returnRefItemVO = null; //
	private String initid;

	public RefDialog_Problem_dict(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
		super(_parent, _title, refItemVO, panel);
		initid = refItemVO.getId();
	}

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout()); //
		billTreePanel = new BillTreePanel("CK_PROJECT_LIST_SCY_E01"); //
		billTreePanel.setMoveUpDownBtnVisiable(false);
		billTreePanel.queryDataByCondition("1=1");
		billTreePanel.addBillTreeSelectListener(this); //

		listPro = new BillListPanel("CK_PROBLEM_DICT_SCY_E01"); //
		listPro.setQuickQueryPanelVisiable(false); //
		listPro.setAllBillListBtnVisiable(false); //

		if (initid != null && !"".equals(initid)) {
			String parentid;
			try {
				parentid = UIUtil.getStringValueByDS(null, "select parentid from CK_PROBLEM_DICT where id =" + initid);
				if (parentid != null && !"".equals(parentid)) {
					billTreePanel.expandOneNodeByKey(parentid);//前面树形面板加了选择事件，这里问题词条会查出结果
					for (int i = 0; i < listPro.getRowCount(); i++) {
						String id = listPro.getRealValueAtModel(i, "id");
						if (initid.equals(id)) {
							listPro.setSelectedRow(i);
							break;
						}
					}
				}
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		WLTSplitPane pane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTreePanel, listPro);
		pane.setDividerLocation(200);
		this.getContentPane().add(pane, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");

		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //

		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel;
	}

	@Override
	public RefItemVO getReturnRefItemVO() {
		return returnRefItemVO;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			BillVO billvo = listPro.getSelectedBillVO();
			if (billvo == null) {
				MessageBox.show(this, "请选择问题词条!"); //
				return;
			}
			returnRefItemVO = new RefItemVO(); //
			returnRefItemVO.setId(billvo.getStringValue("id")); //
			returnRefItemVO.setCode(null);
			returnRefItemVO.setName(billvo.getStringValue("dictname")); //
			this.setCloseType(BillDialog.CONFIRM); //
			this.dispose(); //
		} else if (e.getSource() == btn_cancel) {
			returnRefItemVO = null; //
			this.setCloseType(BillDialog.CANCEL); //
			this.dispose(); //
		}
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		BillVO billVO = _event.getCurrSelectedVO();
		if (null == billVO) {
			listPro.clearTable();//如果选中根节点，或未选中，需要清空列表【李春娟/2016-09-29】
			return;
		}
		String id = billVO.getStringValue("id");
		String level = billVO.getStringValue("leveldesc");
		if ("一级目录".equals(level)) {
			listPro.QueryDataByCondition("firstid = '" + id + "' ");
		} else if ("二级目录".equals(level)) {
			listPro.QueryDataByCondition("secondid = '" + id + "' ");
		} else if ("三级目录".equals(level)) {
			listPro.QueryDataByCondition("parentid = '" + id + "' ");
		}
	}

	/**
	 * 初始宽度
	 * @return
	 */
	public int getInitWidth() {
		return 900;
	}

	public int getInitHeight() {
		return 600;
	}
}
