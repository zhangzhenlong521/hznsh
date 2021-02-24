package cn.com.infostrategy.ui.workflow.engine;

/**
 * 工作流
 * 新建流程
 */
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class WorkFlowUpdateDilaog extends BillDialog {
	private WLTButton wLTButton_save = new WLTButton("保存");
	private WLTButton wLTButton_saveback = new WLTButton("保存返回");
	private WLTButton wLTButton_savecommit = new WLTButton("保存并提交");
	private WLTButton wLTButton_back = new WLTButton("返回");
	private BillCardPanel bcp1;
	private BillListPanel listPanel;

	public WorkFlowUpdateDilaog(Container _parent, String _title, String _cardPanelCode, BillListPanel _listPanel, String _cardid) {
		super(_parent, _title, 1000, 600);
		listPanel = _listPanel;
		bcp1 = new BillCardPanel(_cardPanelCode);
		bcp1.queryDataByCondition("id=" + _cardid);
		bcp1.setEditableByEditInit();
		bcp1.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
		//WLTConstants.BILLDATAEDITSTATE_UPDATE是个常量；

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
		jPanel_north.add(wLTButton_savecommit); //保存并提交
		jPanel_north.add(wLTButton_saveback);
		jPanel_north.add(wLTButton_back);

		jPanel.setLayout(new BorderLayout());

		jPanel.add(jPanel_north, BorderLayout.NORTH);

		JSplitPane splitPane_2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, jPanel, bcp1); // 左右的浮动条
		splitPane_2.setDividerSize(0); //
		splitPane_2.setDividerLocation(30); //
		splitPane_2.setOneTouchExpandable(true);

		this.getContentPane().add(splitPane_2, BorderLayout.CENTER);

	}

	protected void onSaveCommit() {
		try {
			if (!bcp1.newCheckValidate("submit")) { //如果校验失败,则返回
				return;
			}
			bcp1.updateData();
			bcp1.updateUI();

			BillVO billVO = bcp1.getBillVO(); //
			if (billVO == null) {
				MessageBox.show(this, "No Record will be deal!", WLTConstants.MESSAGE_WARN); //
				return; //
			}

			WorkFlowProcessDialog dialog = new WorkFlowProcessDialog(this, "流程处理", bcp1, listPanel, null, null, null, false, null); //
			dialog.setVisible(true); //

			this.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void Onsave() {
		try {
			if (!bcp1.newCheckValidate("save")) { //如果校验失败,则返回
				return;
			}
			bcp1.updateData();
			bcp1.updateUI();
			MessageBox.show("保存数据成功!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void Onsaveback() {
		try {
			if (!bcp1.newCheckValidate("save")) { //如果校验失败,则返回
				return;
			}
			bcp1.updateData();
			bcp1.updateUI();
			listPanel.refreshData();
			MessageBox.show("保存数据成功!");
			this.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void Onback() {
		try {
			this.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BillCardPanel getBillCardPanel() {
		return bcp1;
	}
}
