package com.pushworld.ipushgrc.ui.favorite;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.tree.DefaultMutableTreeNode;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;

/**
 * 在模板中配一个按钮，可以直接调用此逻辑。会有两种情况。
 * 1、如果按钮是在弹出的对话框中，那么根据模板编码。直接在收藏中显示此模板列表。 type=2;
 * 2、按钮在打开的列表或者卡片中，由于该列表可能会存在一些逻辑，可以根据此列表打开。也就是menuid。  type=1;
 * @author hm
 *
 */
public class JoinMyFavoriteBtnAction implements WLTActionListener, ActionListener {
	public JoinMyFavoriteBtnAction() {
	};

	private String itemtype;
	private String classPath;
	private String colName;
	private String templetname;
	private String userid = ClientEnvironment.getInstance().getLoginUserID();
	private String type; //收藏方式，原有机制，0传入一个类路径，1根据menuid，2模板编码
	private BillTreePanel treepanel;

	/*
	 * 此构造方法是用作代码写收藏按钮的响应事件。
	 */
	public JoinMyFavoriteBtnAction(String _itemtype, String _classPath, String _colName) {
		itemtype = _itemtype;
		classPath = _classPath;
		colName = _colName;
		type = "0";
	}

	/*
	 * 
	 */
	public void actionPerformed(WLTActionEvent _event) throws Exception {
		if (_event.getBillPanelFrom() instanceof BillListPanel) {
			BillListPanel listPanel = (BillListPanel) _event.getBillPanelFrom();
			BillVO vo = listPanel.getSelectedBillVO();
			if (vo == null) {
				MessageBox.showSelectOne(listPanel);
				return;
			}
			AbstractWorkPanel workPanel = getWorkPanel(listPanel);
			itemtype = listPanel.getTempletVO().getTempletname(); //预置收藏itemtype为模板名称
			templetname = itemtype;
			if (workPanel == null) { //如果是dialog中弹出来的 就会找不到 Abstractworkpanel.
				classPath = listPanel.getTempletVO().getTempletcode();
				type = "2";
			} else {
				classPath = workPanel.getMenuId();
				type = "1";
			}
			BillVO folderVO = getSelectFolder(_event.getBillPanelFrom());
			if (folderVO == null) {
				return;
			}
			onJoinBtn(listPanel, vo, folderVO);
		} else if (_event.getBillPanelFrom() instanceof BillCardPanel) {
			BillCardPanel cardPanel = (BillCardPanel) _event.getBillPanelFrom();
			BillVO vo = cardPanel.getBillVO();
			if (vo == null) {
				MessageBox.showSelectOne(cardPanel);
				return;
			}
			AbstractWorkPanel workPanel = getWorkPanel(cardPanel);
			itemtype = cardPanel.getTempletVO().getTempletname();
			templetname = itemtype;
			if (workPanel == null) { //如果是dialog中弹出来的 就会找不到 Abstractworkpanel.
				classPath = cardPanel.getTempletVO().getTempletcode();
				type = "2";
			} else {
				classPath = workPanel.getMenuId();
				type = "1";
			}
			BillVO folderVO = getSelectFolder(_event.getBillPanelFrom());
			if (folderVO == null) {
				return;
			}
			onJoinBtn(cardPanel, vo, folderVO);
		}
	}

	// 加入收藏
	private void joinFavority(String value, String itemName, String parentid, String type) throws Exception {
		InsertSQLBuilder insertSQL = new InsertSQLBuilder("my_favorites");
		insertSQL.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "s_my_favorites"));
		insertSQL.putFieldValue("itemtype", itemtype);
		insertSQL.putFieldValue("templetname",templetname);
		insertSQL.putFieldValue("itemname", itemName);
		insertSQL.putFieldValue("itemid", value);
		insertSQL.putFieldValue("type", type);
		insertSQL.putFieldValue("classpath", classPath);
		insertSQL.putFieldValue("createdate", UIUtil.getServerCurrDate());
		insertSQL.putFieldValue("creater", userid);
		insertSQL.putFieldValue("parentid", parentid);
		insertSQL.putFieldValue("deptid", ClientEnvironment.getCurrLoginUserVO().getBlDeptId());
		UIUtil.executeBatchByDS(null, new String[] { insertSQL.getSQL() });
	}

	// 判断是否已经收藏过
	private boolean checkIfHaveHistory(String value) throws Exception {
		String id = UIUtil.getStringValueByDS(null, "select id from my_favorites where creater='" + userid + "' and itemid ='" + value + "' and classpath='" + classPath + "'");
		if (id != null && !id.equals("")) {
			return true;
		}
		return false;
	}

	/*找到该按钮所在*/
	private AbstractWorkPanel getWorkPanel(Container _panel) {
		if (_panel.getParent() instanceof AbstractWorkPanel) {
			return (AbstractWorkPanel) _panel.getParent();
		} else {
			if (_panel.getParent() == null) {
				return null;
			} else {
				return getWorkPanel(_panel.getParent());
			}
		}
	}

	//最原始的加入方式。
	public void actionPerformed(ActionEvent e) {
		WLTButton btn_favority = (WLTButton) e.getSource();
		BillListPanel billList = (BillListPanel) btn_favority.getBillPanelFrom(); //
		templetname = billList.getTempletVO().getTempletname();
		BillVO vo = billList.getSelectedBillVO();
		if (vo == null) {
			MessageBox.showSelectOne(billList);
			return;
		}
		BillVO folderVO = getSelectFolder(billList);
		if (folderVO == null) {
			return;
		}
		onJoinBtn(billList, vo, folderVO);
	}

	private void onJoinBtn(Container container, BillVO vo, BillVO folderVO) {
		String parentid = "";
		if ("Y".equals(folderVO.getStringValue("isfolder"))) {
			parentid = folderVO.getStringValue("id");
		} else {
			itemtype = folderVO.getStringValue("itemtype");
		}
		String keyValue = vo.getStringValue(vo.getPkName());
		if (colName == null || "".equals(colName)) {
			colName = vo.getToStringFieldName();
		}
		String itemname = vo.getStringValue(colName, "");
		try {
			if (!checkIfHaveHistory(keyValue)) {
				joinFavority(keyValue, itemname, parentid, type);
			}
			MessageBox.show(container, "加入成功!");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public BillVO getSelectFolder(Container _parent) {
		MyFavoriteQueryWKPanel panel = new MyFavoriteQueryWKPanel();
		treepanel = panel.getMyFavoriteTree();
		DefaultMutableTreeNode nodes[] = treepanel.findNodeByViewName(itemtype,false);
		if(nodes.length == 0){
			BillVO billvo = new BillVO();
			Pub_Templet_1VO templet = null;
			try {
				templet = UIUtil.getPub_Templet_1VO("MY_FAVORITES_CODE1");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			billvo.setKeys(templet.getItemKeys());
			billvo.setDatas(new Object[templet.getItemKeys().length]);
			billvo.setObject("itemtype",new StringItemVO(itemtype));
			billvo.setObject("parentid",new StringItemVO(""));
			billvo.setObject("seq",new StringItemVO("999"));
			treepanel.addNode(billvo);
		}else{
			treepanel.setSelected(nodes[0]);
		}
		final BillDialog dialog = new BillDialog(_parent, "选择收藏的位置", 230, 350);
		dialog.getContentPane().setLayout(new BorderLayout());
		WLTButton btn_join = new WLTButton("加入收藏");
		btn_join.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (treepanel.getSelectedVO() == null) {
					MessageBox.show(treepanel, "请选择要加入的位置。");
					return;
				}
				if (treepanel.getSelectedNode().isRoot()) {
					MessageBox.show(treepanel, "不能选择根节点。");
					return;
				}
				dialog.setCloseType(1);
				dialog.dispose();
			}
		});
		WLTButton btn_exit = new WLTButton("返回");
		btn_exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.setCloseType(0);
				dialog.dispose();
			}
		});
		WLTPanel btnPane = new WLTPanel(WLTPanel.HORIZONTAL_LEFT_TO_RIGHT, new FlowLayout(FlowLayout.CENTER));
		btnPane.add(btn_join);
		btnPane.add(btn_exit);
		dialog.getContentPane().add(treepanel, BorderLayout.CENTER);
		dialog.getContentPane().add(btnPane, BorderLayout.SOUTH);
		dialog.setVisible(true);
		if (dialog.getCloseType() == 1) {
			return treepanel.getSelectedVO();
		}
		return null;
	}
}
