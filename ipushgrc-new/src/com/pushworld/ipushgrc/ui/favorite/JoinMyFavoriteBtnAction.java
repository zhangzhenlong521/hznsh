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
 * ��ģ������һ����ť������ֱ�ӵ��ô��߼����������������
 * 1�������ť���ڵ����ĶԻ����У���ô����ģ����롣ֱ�����ղ�����ʾ��ģ���б� type=2;
 * 2����ť�ڴ򿪵��б���߿�Ƭ�У����ڸ��б���ܻ����һЩ�߼������Ը��ݴ��б�򿪡�Ҳ����menuid��  type=1;
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
	private String type; //�ղط�ʽ��ԭ�л��ƣ�0����һ����·����1����menuid��2ģ�����
	private BillTreePanel treepanel;

	/*
	 * �˹��췽������������д�ղذ�ť����Ӧ�¼���
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
			itemtype = listPanel.getTempletVO().getTempletname(); //Ԥ���ղ�itemtypeΪģ������
			templetname = itemtype;
			if (workPanel == null) { //�����dialog�е������� �ͻ��Ҳ��� Abstractworkpanel.
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
			if (workPanel == null) { //�����dialog�е������� �ͻ��Ҳ��� Abstractworkpanel.
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

	// �����ղ�
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

	// �ж��Ƿ��Ѿ��ղع�
	private boolean checkIfHaveHistory(String value) throws Exception {
		String id = UIUtil.getStringValueByDS(null, "select id from my_favorites where creater='" + userid + "' and itemid ='" + value + "' and classpath='" + classPath + "'");
		if (id != null && !id.equals("")) {
			return true;
		}
		return false;
	}

	/*�ҵ��ð�ť����*/
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

	//��ԭʼ�ļ��뷽ʽ��
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
			MessageBox.show(container, "����ɹ�!");
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
		final BillDialog dialog = new BillDialog(_parent, "ѡ���ղص�λ��", 230, 350);
		dialog.getContentPane().setLayout(new BorderLayout());
		WLTButton btn_join = new WLTButton("�����ղ�");
		btn_join.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (treepanel.getSelectedVO() == null) {
					MessageBox.show(treepanel, "��ѡ��Ҫ�����λ�á�");
					return;
				}
				if (treepanel.getSelectedNode().isRoot()) {
					MessageBox.show(treepanel, "����ѡ����ڵ㡣");
					return;
				}
				dialog.setCloseType(1);
				dialog.dispose();
			}
		});
		WLTButton btn_exit = new WLTButton("����");
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
