package cn.com.infostrategy.ui.workflow.engine;

/**
 * ������
 * �½�����
 */
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class WorkFlowInsertDilaog extends BillDialog {
	private WLTButton wLTButton_save = new WLTButton("����");
	private WLTButton wLTButton_saveback = new WLTButton("���淵��");
	private WLTButton wLTButton_savecommit = new WLTButton("���沢�ύ");
	private WLTButton wLTButton_back = new WLTButton("����");
	private BillCardPanel bcp1;
	private BillListPanel listPanel;

	public WorkFlowInsertDilaog(Container _parent, String _title, String _cardPanelCode, BillListPanel _listPanel) {
		super(_parent, _title, 1000, 600);
		listPanel = _listPanel;
		bcp1 = new BillCardPanel(_cardPanelCode);
		try {
			bcp1.insertRow();
		} catch (WLTAppException e1) {
			e1.printStackTrace();
		}
		bcp1.setEditableByInsertInit();
		bcp1.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
		//WLTConstants.BILLDATAEDITSTATE_UPDATE�Ǹ�������

		JPanel jPanel = new JPanel();
		JPanel jPanel_north = new JPanel();
		jPanel_north.setLayout(new FlowLayout(FlowLayout.LEFT));
		wLTButton_back.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				Onback();
			}

		});
		wLTButton_saveback.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				Onsaveback();
			}

		});
		wLTButton_save.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				Onsave();
			}

		});
		wLTButton_savecommit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				onSaveCommit();
			}

		});
		jPanel_north.add(wLTButton_save);
		jPanel_north.add(wLTButton_savecommit); //���沢�ύ
		jPanel_north.add(wLTButton_saveback);
		jPanel_north.add(wLTButton_back);

		jPanel.setLayout(new BorderLayout());

		jPanel.add(jPanel_north, BorderLayout.NORTH);

		JSplitPane splitPane_2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, jPanel, bcp1); // ���ҵĸ�����
		splitPane_2.setDividerSize(0); //
		splitPane_2.setDividerLocation(30); //
		splitPane_2.setOneTouchExpandable(true);

		this.getContentPane().add(splitPane_2, BorderLayout.CENTER);

	}

	protected void onSaveCommit() {
		try {
			if (!bcp1.newCheckValidate("submit")) { //���У��ʧ��,�򷵻�
				return;
			}
			bcp1.updateData();
			bcp1.updateUI();
			BillVO billVO = bcp1.getBillVO(); //
			if (billVO == null) {
				MessageBox.show(this, "No Record will be deal!", WLTConstants.MESSAGE_WARN); //
				return; //
			}
			WorkFlowProcessDialog dialog = new WorkFlowProcessDialog(this, "���̴���", bcp1, listPanel, null, null, null, false, null); //
			dialog.setVisible(true); //
			this.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void Onsave() {
		try {
			if (!bcp1.newCheckValidate("save")) { //���У��ʧ��,�򷵻�
				return;
			}
			bcp1.updateData();
			bcp1.updateUI();
			MessageBox.show("�������ݳɹ�!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void Onsaveback() {
		try {
			if (!bcp1.newCheckValidate("save")) { //���У��ʧ��,�򷵻�
				return;
			}
			bcp1.updateData();
			bcp1.updateUI();
			listPanel.refreshData();
			MessageBox.show("�������ݳɹ�!");
			this.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BillCardPanel getInsertCardPanel() {
		return bcp1;
	}

	public void Onback() {
		try {
			this.dispose();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
