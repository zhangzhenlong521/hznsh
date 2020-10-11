package cn.com.infostrategy.ui.sysapp.database.transfer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;
import cn.com.infostrategy.ui.mdata.cardcomp.TableRefPanel;

/**
 * 自定义模板参照
 * @author demo
 *
 */
public class Ref_QueryTBColumnDialog extends AbstractRefDialog implements ActionListener {

	private static final long serialVersionUID = 2362312986058279809L;
	BillListPanel list = null;
	WLTButton btn_confirm = null;
	WLTButton btn_cancel = null;
	RefItemVO initvo = null;
	TableRefPanel refpanel = null;
	String returnId = null;
	String returnName = null;
	String returnCode = null;
	RefItemVO returnRefvo = null;

	public Ref_QueryTBColumnDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
		super(_parent, _title, refItemVO, panel);
		this.list = (BillListPanel) panel;
		this.initvo = refItemVO;
	}

	public Ref_QueryTBColumnDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel, String name) {
		super(_parent, _title, refItemVO, panel);
		this.list = (BillListPanel) panel;
		this.initvo = refItemVO;
	}

	@Override
	public RefItemVO getReturnRefItemVO() {
		return returnRefvo;
	}

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout());
		TableDataStruct struct1 = new TableDataStruct();
		struct1.setHeaderName(new String[] { "列名", "类型", "长度" });
		try {
			TableDataStruct struct = UIUtil.getTableDataStructByDS(this.getDestDsName(), "select * from  " + this.getDestTableName() + "  where 1=2"); //

			String[] names = struct.getHeaderName();
			String[] types = struct.getHeaderTypeName();
			int[] lengths = struct.getHeaderLength();

			String[][] str_bodyData = new String[names.length][3];
			for (int i = 0; i < str_bodyData.length; i++) {
				str_bodyData[i][0] = names[i];
				str_bodyData[i][1] = types[i];
				str_bodyData[i][2] = lengths[i] + "";
			}

			struct1.setBodyData(str_bodyData);
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
		JPanel contentPanel = WLTPanel.createDefaultPanel(new BorderLayout());
		refpanel = new TableRefPanel(this.getDestDsName(), struct1, this.initvo);
		contentPanel.add(refpanel, BorderLayout.CENTER);
		int maxWidth = refpanel.getAllWidth();
		contentPanel.add(this.getSouthPanel(), BorderLayout.SOUTH);
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(contentPanel, BorderLayout.CENTER);
		maxWidth += 50;
		if (maxWidth < 450)
			maxWidth = 450;
		if (maxWidth > 1000)
			maxWidth = 1000;

		this.setSize(maxWidth, 400);
		setLocationCenter(this);
	}

	private WLTPanel getSouthPanel() {
		WLTPanel panel = new WLTPanel();
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");
		btn_confirm.addActionListener(this);
		btn_cancel.addActionListener(this);
		panel.add(btn_confirm);
		panel.add(btn_cancel);
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == btn_cancel) {
			this.setCloseType(BillDialog.CANCEL);
			this.dispose();
		} else if (source == btn_confirm) {
			onConfirm();
		}

	}

	private void onConfirm() {
		RefItemVO refvos = refpanel.getSelectVO();
		returnRefvo = new RefItemVO();
		if (refvos != null) {//如果没有选择，点击确定，则认为是清空
			returnRefvo.setId(refvos.getId());
			returnRefvo.setCode(refvos.getId());
			returnRefvo.setName(refvos.getId());
		}
		list.setValueAt(returnRefvo, list.getSelectedRow(), "dest_colname");
		setCloseType(BillDialog.CONFIRM);
		this.dispose();
	}

	private String getDestDsName() {
		String name = list.getSelectedBillVO().getStringValue("dest_dsname");
		if ("".equals(name))
			name = null;
		return name;
	}

	private String getDestTableName() {
		String name = list.getSelectedBillVO().getStringValue("dest_table");
		return name;
	}

	private void setLocationCenter(Component comp) {
		Toolkit tool = Toolkit.getDefaultToolkit();
		double screenWidth = tool.getScreenSize().getWidth();
		double screenHeight = tool.getScreenSize().getHeight();
		int compWidth = comp.getWidth();
		int compHeight = comp.getHeight();

		int startX = (int) screenWidth / 2 - compWidth / 2;
		int startY = (int) screenHeight / 2 - compHeight / 2;

		if (startX + compWidth > screenWidth) {
			startX = (int) screenWidth - compWidth;
		}
		if (startY + compHeight > screenHeight) {
			startY = (int) screenHeight - compHeight;
		}
		comp.setLocation(startX, startY);
	}

}
