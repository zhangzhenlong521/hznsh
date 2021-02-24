package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.pushworld.wn.bs.WnSalaryServiceImpl;

/**
 * �ͻ�����ȼ�����
 * 
 * @author ZPY
 */
public class ManagerScoreDXWKPanel extends AbstractWorkPanel implements
		ActionListener {

	private BillListPanel listPanel = null;
	private WLTButton rateButton;
	private String message;

	@Override
	public void initialize() {
		listPanel = new BillListPanel("WN_MANAGERDXSCORE_ZPY_Q01");
		rateButton = new WLTButton("�ȼ�����");
		rateButton.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] { rateButton });
		listPanel.repaintBillListButton();
		this.add(listPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == rateButton) {// �ͻ�����ȼ�����
			try {
				// ����ͻ�ȥѡ���Ƕ��ϰ�����п��˻��Ƕ��°�����п��� 0 ��ʾ�ϰ��꣬1��ʾ�°��� -1��ʾȡ��
				final int dateNum = MessageBox.showOptionDialog(this,
						"��ѡ�񿼺˵�ʱ���", "��ʾ", new String[] { "�ϰ���", "�°���" }, 0);
				MessageBox.show(this,"��ǰѡ�е�ֵΪ:"+dateNum);
				final WnSalaryServiceIfc service = new WnSalaryServiceImpl();
				if (dateNum == -1) {
					return;
				}
				new SplashWindow(this, new AbstractAction() {
					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {// �Կͻ���������������˹��ܿ���
						message = service.managerLevelCompute(dateNum);
					}
				});
				MessageBox.show(this, message);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
}