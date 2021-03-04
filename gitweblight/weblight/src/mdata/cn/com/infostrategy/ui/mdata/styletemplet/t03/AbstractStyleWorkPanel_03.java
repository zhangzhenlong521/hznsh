/**************************************************************************
 * $RCSfile: AbstractStyleWorkPanel_03.java,v $  $Revision: 1.18 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/

package cn.com.infostrategy.ui.mdata.styletemplet.t03;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.tree.DefaultMutableTreeNode;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.BillTreeNodeVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillTreeMoveEvent;
import cn.com.infostrategy.ui.mdata.BillTreeMoveListener;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

/**
 * ģ��3 ��������ұ߿��������ұ߿��еļ�¼Ϊ��������Ϣ�������Ľ����в����� ģ��3-2
 * ��������ұ߿����ұ߿��еļ�¼Ϊ�������ҵ�������һ�ű��һ����¼���˱������������
 */

/**
 * ���ģ��3������ʵ��!!
 * ��Ҫ�߼������϶�������ʵ��!!
 * @author xch
 *
 */
public abstract class AbstractStyleWorkPanel_03 extends AbstractWorkPanel implements BillTreeSelectListener, BillTreeMoveListener {

	private BillTreePanel billTreePanel = null; //�������
	private BillCardPanel billCardPanel = null; //��Ƭ���

	private WLTButton btn_tree_insert = null;
	private WLTButton btn_tree_edit = null;
	private WLTButton btn_tree_delete = null;
	private WLTButton btn_tree_refresh = null;

	private boolean bo_ifneedrefresh = true; //
	private ActionListener buttonActionListener = null;
	private JMenuItem item_insert, item_delete, item_edit; //
	private ActionListener appPopMenuAction = null;

	private IUIIntercept_03 uiIntercept = null;

	public abstract String getTempletcode(); //ģ�����

	public void initialize() {
		try {
			this.setLayout(new BorderLayout()); //
			billTreePanel = new BillTreePanel(getTempletcode()); // formatContentPanel.getBillTreePanel();
			billCardPanel = new BillCardPanel(getTempletcode()); //formatContentPanel.getBillCardPanel(); //

			billTreePanel.reSetTreeChecked(false); //������ѡ��
			item_insert = new JMenuItem("����", UIUtil.getImage("insert.gif")); //����
			item_delete = new JMenuItem("ɾ��", UIUtil.getImage("del.gif")); //ɾ��
			item_edit = new JMenuItem("�༭", UIUtil.getImage("modify.gif")); //�༭

			appPopMenuAction = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						if (e.getSource() == item_insert) {
							onInsert(); //
						} else if (e.getSource() == item_delete) {
							onDelete(); //
						} else if (e.getSource() == item_edit) {
							onEdit(); //
						}
					} catch (Exception ex) {
						MessageBox.showException(AbstractStyleWorkPanel_03.this, ex);
					}
				}
			};

			item_insert.addActionListener(appPopMenuAction);
			item_delete.addActionListener(appPopMenuAction);
			item_edit.addActionListener(appPopMenuAction);

			billTreePanel.setAppMenuItems(new JMenuItem[] { item_insert, item_edit, item_delete }); //
			buttonActionListener = new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					try {
						if (e.getSource() == btn_tree_insert) {
							onInsert();
						} else if (e.getSource() == btn_tree_edit) {
							onEdit();
						} else if (e.getSource() == btn_tree_delete) {
							onDelete();
						} else if (e.getSource() == btn_tree_refresh) {
							onRefresh();
						}
					} catch (Exception ex) {
						MessageBox.showException(AbstractStyleWorkPanel_03.this, ex);
					}
				}
			};

			btn_tree_insert = new WLTButton(WLTConstants.BUTTON_TEXT_INSERT);
			btn_tree_edit = new WLTButton(WLTConstants.BUTTON_TEXT_EDIT);
			btn_tree_delete = new WLTButton(WLTConstants.BUTTON_TEXT_DELETE);
			btn_tree_refresh = new WLTButton(WLTConstants.BUTTON_TEXT_REFRESH);
			btn_tree_insert.addActionListener(buttonActionListener);
			btn_tree_edit.addActionListener(buttonActionListener);
			btn_tree_delete.addActionListener(buttonActionListener);
			btn_tree_refresh.addActionListener(buttonActionListener);
			billTreePanel.insertBatchBillTreeButton(new WLTButton[] { btn_tree_insert, btn_tree_edit, btn_tree_delete, btn_tree_refresh }); //
			billTreePanel.repaintBillTreeButton(); //
			billTreePanel.setDragable(true);
			billTreePanel.queryDataByCondition("1=1 "); //��ʱ���������������..

			billTreePanel.addBillTreeSelectListener(this); //
			billTreePanel.addBillTreeMovedListener(this); //

			billCardPanel.setEditable(false); //

			WLTSplitPane splitPane = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, billTreePanel, billCardPanel); //
			splitPane.setDividerLocation(getSplitLocation()); //
			this.add(splitPane, BorderLayout.CENTER); //
			initUIIntercept(); //��ʼ���ͻ�����������!!!
			afterInitialize(); //��ʼ����������Ҫ����.
		} catch (Exception ex) {
			ex.printStackTrace(); //
			MessageBox.showException(this, ex); //
		}
	}

	private void initUIIntercept() {
		try {
			String str_UIIntercept = getMenuConfMapValueAsStr("$UIIntercept"); //
			if (str_UIIntercept != null && !str_UIIntercept.trim().equals("")) { //�����Ϊ��!
				uiIntercept = (IUIIntercept_03) Class.forName(str_UIIntercept).newInstance(); //
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * ��ʼ��������Ҫ������,���Ա����า��..
	 */
	public void afterInitialize() throws Exception {
		if (this.uiIntercept != null) {
			uiIntercept.afterInitialize(this); //
		}
	}

	/**
	 * ȡ���������..
	 * @return
	 */
	public BillTreePanel getBillTreePanel() {
		return billTreePanel;
	}

	/**
	 * ȡ�ÿ�Ƭ���..
	 * @return
	 */
	public BillCardPanel getBillCardPanel() {
		return billCardPanel;
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		if (!bo_ifneedrefresh) {
			return;
		}

		if (!_event.getCurrSelectedNode().isRoot()) {
			BillVO billVO = _event.getCurrSelectedVO();
			String str_id = billVO.getPkValue();
			getBillCardPanel().queryDataByCondition(billTreePanel.getTempletVO().getTreepk() + " = '" + str_id + "'"); //��Ϊ��ģ����ȡ����
		} else {
			getBillCardPanel().refreshData("1=2");//�����û��ֵ��Ӧ�����cardpanel��
		}
	}

	public void onInsert() {
		try {
			if (getBillTreePanel().getSelectedPath() == null) {
				MessageBox.show(this, "��ѡ��һ�����׽�������������!"); //
				return; //���û��ѡ��һ�������ֱ�ӷ���
			}

			BillVO billVO = getBillTreePanel().getSelectedVO();
			BillCardPanel insertCardPanel = new BillCardPanel(billTreePanel.getTempletVO()); //

			if (billVO != null) { //���ѡ�еĲ��Ǹ����
				insertCardPanel.insertRow(); //
				insertCardPanel.setEditableByInsertInit(); //
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) getBillTreePanel().getSelectedNode();
				if (!parentNode.isRoot()) { //������Ǹ����,��Ҫ����parentid
					BillVO parentVO = getBillTreePanel().getBillVOFromNode(parentNode); //
					String parent_id = ((StringItemVO) parentVO.getObject(billTreePanel.getTempletVO().getTreepk())).getStringValue(); //
					insertCardPanel.setCompentObjectValue(billTreePanel.getTempletVO().getTreeparentpk(), new StringItemVO(parent_id)); //���ø����ֶ�
					BillVO[] parentVOs = getBillTreePanel().getSelectedParentPathVOs(); //
					StringBuilder sb_ids = new StringBuilder();
					for (int i = 0; i < parentVOs.length; i++) {
						sb_ids.append(parentVOs[i].getStringValue(billTreePanel.getTempletVO().getTreepk()) + ";"); //
					}
					//��ʱд����blparentcorpids,�Ժ�Ҫ�ĳɿ����õĲ���!!
					insertCardPanel.setCompentObjectValue("blparentcorpids", new StringItemVO(";" + sb_ids.toString() + insertCardPanel.getValueAt(billTreePanel.getTempletVO().getTreepk()) + ";")); //���ø����ֶ�
				} else {
					insertCardPanel.setCompentObjectValue("blparentcorpids", new StringItemVO(";" + insertCardPanel.getValueAt(billTreePanel.getTempletVO().getTreepk()) + ";")); //���ø����ֶ�
				}
			} else { //���ѡ�е��Ǹ����
				insertCardPanel.insertRow(); //
				insertCardPanel.setEditableByInsertInit(); //
				insertCardPanel.setCompentObjectValue("blparentcorpids", new StringItemVO(insertCardPanel.getValueAt(billTreePanel.getTempletVO().getTreepk()) + ";")); //���ø����ֶ�
			}

			BillCardDialog dialog = new BillCardDialog(this, "����", insertCardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); //
			if (uiIntercept != null)
				uiIntercept.actionAfterInsert(insertCardPanel);
			dialog.setVisible(true); //
			if (dialog.getCloseType() == BillDialog.CONFIRM) {
				BillVO newVO = insertCardPanel.getBillVO(); //
				newVO.setToStringFieldName(billTreePanel.getTempletVO().getTreeviewfield()); //
				bo_ifneedrefresh = false;
				billTreePanel.addNode(newVO); //
				getBillCardPanel().setBillVO(newVO); //
				bo_ifneedrefresh = true;

				if (this.uiIntercept != null) {
					uiIntercept.dealCommitAfterInsert(this, newVO); // �����ύ����
				}
			}
		} catch (Exception e) {
			MessageBox.showException(this, e); //
		}
	}

	protected void onEdit() throws Exception {
		try {
			if (getBillTreePanel().getSelectedPath() == null) {
				MessageBox.show(this, "��ѡ��һ���������޸Ĳ���!"); //
				return; //���û��ѡ��һ�������ֱ�ӷ���
			}

			BillVO billVO = getBillTreePanel().getSelectedVO();
			BillCardPanel editCardPanel = new BillCardPanel(billTreePanel.getTempletVO()); //

			if (billVO != null) { //���ѡ�еĲ��Ǹ����
				editCardPanel.queryDataByCondition("id='" + billVO.getPkValue() + "'"); //
				editCardPanel.setEditableByEditInit(); //
			} else { //���ѡ�е��Ǹ����
				MessageBox.show(this, "����㲻���Ա༭!"); //
				return; //���û��ѡ��һ�������ֱ�ӷ���
			}

			BillCardDialog dialog = new BillCardDialog(this, "�༭", editCardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE); //
			if (uiIntercept != null)
				uiIntercept.actionAfterUpdate(editCardPanel, "");
			dialog.setVisible(true); //
			if (dialog.getCloseType() == BillDialog.CONFIRM) {
				BillVO newVO = editCardPanel.getBillVO(); //
				newVO.setToStringFieldName(billTreePanel.getTempletVO().getTreeviewfield()); //
				bo_ifneedrefresh = false; //
				getBillTreePanel().setBillVOForCurrSelNode(newVO); //�����л�д����
				getBillCardPanel().setBillVO(newVO); //
				bo_ifneedrefresh = true; //
				getBillTreePanel().updateUI(); //
			}

		} catch (Exception e) {
			MessageBox.showException(this, e); //
		}
	}

	public void onDelete() {
		BillTreeNodeVO treeNodeVO = getBillTreePanel().getSelectedTreeNodeVO();
		BillVO billVO = getBillTreePanel().getSelectedVO();
		if (billVO == null) {
			MessageBox.show(this, "��ѡ��һ��������ɾ������!"); //
			return;
		}

		if (billVO != null) {
			BillVO[] childVOs = getBillTreePanel().getSelectedChildPathBillVOs(); //ȡ������ѡ�е�
			if (JOptionPane.showConfirmDialog(this, "��ȷ��Ҫɾ����¼��" + treeNodeVO.toString() + "����?\r\n�⽫һ��ɾ�����¹���" + childVOs.length + "���������¼,����ؽ�������!", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}
			try {
				if (uiIntercept != null) {
					uiIntercept.actionBeforeDelete(getBillCardPanel());
				}
				Vector v_sqls = new Vector(); //�˵�ɾ��
				for (int i = 0; i < childVOs.length; i++) {
					v_sqls.add("delete from " + childVOs[i].getSaveTableName() + " where " + childVOs[i].getPkName() + "='" + childVOs[i].getPkValue() + "'"); //
				}
				UIUtil.executeBatchByDS(null, v_sqls); //ִ�����ݿ����!!
				getBillTreePanel().delCurrNode(); //
				getBillTreePanel().getJTree().repaint(); //
				getBillCardPanel().clear();
				if (billTreePanel.getSelectedVO() != null) {
					billCardPanel.setValueAt("linkcode", billTreePanel.getSelectedVO().getObject("linkcode")); //
				}
			} catch (Exception ex) {
				MessageBox.showException(AbstractStyleWorkPanel_03.this, ex); //
			}
		}
	}

	public void onRefresh() {
		long ll_1 = System.currentTimeMillis();
		getBillTreePanel().queryDataByCondition("1=1");//ˢ�����ݣ����ﲻ��ֱ����refreshTree(),��Ϊ�ϴβ�ѯ��sql�кܿ��ܲ������������ݵ�id������Ҫȫ�顣�����/2013-07-30��
		long ll_2 = System.currentTimeMillis();
		MessageBox.show(this, "ˢ�����ݳɹ�!����ʱ[" + (ll_2 - ll_1) + "]����"); //
	}

	//	/**
	//	 * ��������!!
	//	 *
	//	 */
	//	public void onSave() {
	//		try {
	//			if (getBillCardPanel().getEditState() == WLTConstants.BILLDATAEDITSTATE_INSERT) {
	//				String str_pkvalue = getBillCardPanel().getCompentRealValue(billTreePanel.getTempletVO().getTreepk()); //
	//				getBillCardPanel().updateData(); //��������!!
	//				FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) RemoteServiceFactory.getInstance().lookUpService(FrameWorkMetaDataServiceIfc.class);
	//				BillVO[] vos = service.getBillVOsByDS(null, "select * from " + getBillTreePanel().getTempletVO().getTablename() + " where " + billTreePanel.getTempletVO().getTreepk() + "='" + str_pkvalue + "'", billTreePanel.getTempletVO()); // 
	//
	//				bo_ifneedrefresh = false;
	//				billTreePanel.addNode(vos[0]); //
	//				bo_ifneedrefresh = true;
	//
	//				setInformation("����˵����ݳɹ�!!");
	//			} else if (getBillCardPanel().getEditState() == WLTConstants.BILLDATAEDITSTATE_UPDATE) {
	//				getBillCardPanel().updateData();
	//				billTreePanel.getSelectedVO().setObject(billTreePanel.getTempletVO().getTreeviewfield(), getBillCardPanel().getBillVO().getObject(billTreePanel.getTempletVO().getTreeviewfield())); //
	//				setInformation("����˵����ݳɹ�!!");
	//			}
	//
	//			billTreePanel.getJTree().updateUI(); //
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		} //
	//
	//		getBillTreePanel().resetAllLinkCode(); //
	//		if (billTreePanel.getSelectedVO() != null) {
	//			//billCardPanel.setValueAt("linkcode", billTreePanel.getSelectedVO().getObject("linkcode")); //
	//		}
	//	}

	/**
	 * 
	 */
	public void onBillTreeNodeMoved(BillTreeMoveEvent _event) {
		if (billTreePanel.getSelectedNode() != null && !billTreePanel.getSelectedNode().isRoot()) {
			BillVO billVO = billTreePanel.getSelectedVO();
			String str_id = billVO.getPkValue();
			getBillCardPanel().queryDataByCondition(billTreePanel.getTempletVO().getTreepk() + " = '" + str_id + "'"); //��Ϊ��ģ����ȡ����
		} else {
			getBillCardPanel().clear(); //
		}
	}

	/**
	 * �ָ�����λ��,Ϊ�˿���
	 * @return
	 */
	public int getSplitLocation() {
		try {
			String str_location = this.getMenuConfMapValueAsStr("SplitLocation"); //���Ƿ�ʽ������
			if (str_location != null && !str_location.trim().equals("")) {
				return Integer.parseInt(str_location);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 235;
	}

}
