package cn.com.infostrategy.ui.mdata.querycomp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;

import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.HFlowLayoutPanel;
import cn.com.infostrategy.ui.mdata.VFlowLayoutPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractWLTCompentPanel;

/**
 * ͨ�ò�ѯ���
 * @author xch
 *
 */
public class CommQueryDialog extends BillDialog implements ActionListener {
	private VFlowLayoutPanel queryPanel = null; //��ѯ���..
	private WLTButton btn_confirm, btn_reset, btn_cancel; //ȷ��,���,ȡ��
	int closeType = -1; //

	public CommQueryDialog(Container _parent, String _title, int _width, int li_height, VFlowLayoutPanel _queryPanel) {
		super(_parent, _title, _width, li_height);
		this.queryPanel = _queryPanel;
		initialize(); //
	}

	/**
	 * ��ʼ��ҳ��
	 */
	private void initialize() {
		this.getContentPane().add(queryPanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	/**
	 * ��ť�����..
	 * @return
	 */
	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		panel.setBackground(LookAndFeel.billlistquickquerypanelbgcolor);
		btn_confirm = new WLTButton("ȷ��"); //
		btn_reset = new WLTButton("���"); //
		btn_cancel = new WLTButton("ȡ��"); //
		btn_cancel.addActionListener(this); //
		btn_reset.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		panel.add(btn_confirm); //
		panel.add(btn_reset); //
		panel.add(btn_cancel); //
		return panel;
	}

	/**
	 * 
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) { //
			this.closeType = BillDialog.CONFIRM; //
			this.dispose(); //
		} else if (e.getSource() == btn_reset) { //
			resetAllCompent(); //������пؼ�
		} else if (e.getSource() == btn_cancel) { //
			this.closeType = BillDialog.CANCEL; //
			this.dispose(); //
		}
	}

	/**
	 * ������пؼ�..
	 */
	public void resetAllCompent() {
		AbstractWLTCompentPanel[] allCompents = getAllQueryCompents(); //
		for (int i = 0; i < allCompents.length; i++) {
			allCompents[i].reset(); //���ĳ���ؼ�!
		}
	}

	/**
	 * 
	 * @return
	 */
	public AbstractWLTCompentPanel[] getAllQueryCompents() {
		ArrayList al_allcompents = new ArrayList(); //
		JComponent[] rows = queryPanel.getAllCompents(); //
		for (int i = 0; i < rows.length; i++) {
			HFlowLayoutPanel rowPanel = (HFlowLayoutPanel) rows[i]; //
			JComponent[] rowcompents = rowPanel.getAllCompents(); //
			for (int j = 0; j < rowcompents.length; j++) {
				al_allcompents.add(rowcompents[j]); //
			}
		}
		return (AbstractWLTCompentPanel[]) al_allcompents.toArray(new AbstractWLTCompentPanel[0]); //
	}

	public int getCloseType() {
		return closeType;
	}

}
