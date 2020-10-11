package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;

/**
 * ����һ��ѡ���ȷ�ϵ�ͨ�ô���(�б�),����һ��������ť(ȷ��/ȡ��),Ȼ������һ���б��,Ȼ��ѡ��һ����¼����!!
 * @author xch
 *
 */
public abstract class AbstractConfirmListDialog extends BillDialog implements BillListSelectListener, MouseListener {

	protected BillListPanel billListPanel = null;

	private int closeType = 0;

	public abstract String getInitTitle(); //��ʼ������

	public abstract int getInitWidth(); //��ʼ�����,���������������д��getWidth(),��������ϵ�ҳ���С�仯ʱ,��Ļ������ס��!!�鳤�˳�ʱ����ҵ�ԭ������!!

	public abstract int getInitHeight(); //��ʼ���߶�

	public abstract void onBeforeConfirm() throws Exception; //���ȷ��ʱ����

	public abstract void onBeforeCancel() throws Exception; //���ȡ��ʱ����

	public abstract AbstractTMO getAbstractTempletVO(); //����VO

	private BillVO selectedBillVO = null; //

	public AbstractConfirmListDialog(Container _parent) {
		super(_parent);
		this.setTitle(getInitTitle());
		this.setSize(getInitWidth(), getInitHeight());
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //���Ǹ����Ĭ�Ϲرն���!!!
		locationToCenterPosition(); //���д���..
		this.getContentPane().setLayout(new BorderLayout()); //
		this.getContentPane().add(getBillListPanel(), BorderLayout.CENTER);
		this.getContentPane().add(getSourthPanel(), BorderLayout.SOUTH); //
		this.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
			}

			public void windowClosing(WindowEvent e) {
				try {
					closeType = 2;
					onBeforeCancel();
					AbstractConfirmListDialog.this.dispose(); //
				} catch (Exception e1) {
					MessageBox.showException(AbstractConfirmListDialog.this, e1);
				}
			}
		});

		getBillListPanel().addBillListSelectListener(this); //
		getBillListPanel().getTable().addMouseListener(this); //���˫���¼�!!
		initialize(); //����һ�λ����޸�����,����BillCard�Ƿ���ʾ�����!!
	}

	//���Խ���һЩ��������
	public void initialize() {
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			try {
				closeType = 1;
				putData(); //
				onBeforeConfirm();
				this.dispose(); //
			} catch (Exception e1) {
				MessageBox.showException(AbstractConfirmListDialog.this, e1);
			}
		}
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public final BillListPanel getBillListPanel() {
		if (billListPanel == null) {
			String str_templetCode = getBillListTempletCode(); //
			if (str_templetCode != null) {
				billListPanel = new BillListPanel(str_templetCode); //
			} else {
				billListPanel = new BillListPanel(getAbstractTempletVO()); //
			}
			billListPanel.setItemEditable(false);
		}
		return billListPanel;
	}

	public String getBillListTempletCode() {
		return null;
	}

	private JPanel getSourthPanel() {
		JPanel panel = new JPanel(); //
		panel.setLayout(new FlowLayout()); //
		WLTButton btn_confirm = new WLTButton("ȷ��");
		WLTButton btn_cancel = new WLTButton("ȡ��");
		btn_confirm.setPreferredSize(new Dimension(70, 20)); //
		btn_cancel.setPreferredSize(new Dimension(70, 20)); //
		btn_confirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					closeType = 1;
					putData(); //
					onBeforeConfirm(); //���ȷ��֮ǰ���Ĵ���,���Խ���һЩУ�����!!!!!
					AbstractConfirmListDialog.this.dispose();
				} catch (Exception e1) {
					MessageBox.showException(AbstractConfirmListDialog.this, e1);
				}
			}
		});
		btn_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					closeType = 2;
					onBeforeCancel(); //ȡ��֮ǰ�Ĵ���!!���Խ���ĳЩУ�鴦��!!!!!
					AbstractConfirmListDialog.this.dispose();
				} catch (Exception e1) {
					MessageBox.showException(AbstractConfirmListDialog.this, e1);
				}
			}
		});
		panel.add(btn_confirm);
		panel.add(btn_cancel);
		JButton[] custBtns = getCustBtns(); //
		if (custBtns != null) {
			for (int i = 0; i < custBtns.length; i++) {
				panel.add(custBtns[i]);
			}
		}
		return panel;
	}

	public JButton[] getCustBtns() {
		return null;
	}

	public int getCloseType() {
		return closeType;
	}

	public void putData() throws Exception {
		BillVO billVO = getBillListPanel().getSelectedBillVO(); //
		if (billVO == null) {
			throw new WLTAppException("��ѡ��һ����¼!");
		}
		setSelectedBillVO(billVO); //
	}

	public BillVO getSelectedBillVO() {
		return selectedBillVO;
	}

	public void setSelectedBillVO(BillVO selectedBillVO) {
		this.selectedBillVO = selectedBillVO;
	}

}
