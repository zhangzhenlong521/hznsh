package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.WLTButton;

/**
 * ����һ��ѡ���ȷ�ϵ�ͨ�ô���(��Ƭ),����һ��������ť(ȷ��/ȡ��),Ȼ������һ���б��,Ȼ��ѡ��һ����¼����!!
 * @author xch
 *
 */
public abstract class AbstractConfirmCardDialog extends BillDialog implements BillCardEditListener {

	private static final long serialVersionUID = 4113997787904420154L;

	protected BillCardPanel billCardPanel = null;

	protected int closeType = 0;

	public AbstractConfirmCardDialog(Container _parent) {
		super(_parent);
		this.setTitle(getTitle());
		this.setSize(getInitWidth(), getInitHeight());
		Frame frame = JOptionPane.getFrameForComponent(_parent);
		double ld_width = frame.getSize().getWidth();
		double ld_height = frame.getSize().getHeight();
		double ld_x = frame.getLocation().getX();
		double ld_y = frame.getLocation().getY();

		double ld_thisX = ld_x + ld_width / 2 - (getInitWidth() / 2);
		double ld_thisY = ld_y + ld_height / 2 - (getInitHeight() / 2);
		if (ld_thisX < 0) {
			ld_thisX = 0;
		}

		if (ld_thisY < 0) {
			ld_thisY = 0;
		}
		this.setLocation(new Double(ld_thisX).intValue(), new Double(ld_thisY).intValue());
		this.setLayout(new BorderLayout()); //
		this.add(getBilCardPanel(), BorderLayout.CENTER); //���뿨Ƭ
		this.add(getSourthPanel(), BorderLayout.SOUTH);
		this.addWindowListener(new WindowAdapter() {

			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
			}

			public void windowClosing(WindowEvent e) {
				closeType = 2;
				onCancel();
			}

		});
		getBilCardPanel().addBillCardEditListener(this); //
		initialize(); //����һ�λ����޸�����,����BillCard�Ƿ���ʾ�����!!
	}

	public abstract String getTitle();

	public abstract int getInitWidth();//��ǰ��getWidth()��getHeight()��д�˸��෽����������ڱ��󣬻�ÿ�͸߶����䣬��ʹ���������ֿհס����/2012-03-15��

	public abstract int getInitHeight();

	public abstract void onConfirm();

	public abstract void onCancel();

	public void initialize() {
	}

	public void onBillCardValueChanged(BillCardEditEvent _evt) {

	}

	public abstract String getTempletCode(); //����VO

	public final BillCardPanel getBilCardPanel() {
		if (billCardPanel == null) {
			billCardPanel = new BillCardPanel(getTempletCode());
			billCardPanel.setEditable(true);
			billCardPanel.setTitleable(false); //
			billCardPanel.setScrollable(false); //ȥ��������
		}
		return billCardPanel;
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
				getBilCardPanel().stopEditing(); //
				if (!getBilCardPanel().checkValidate()) {
					return;
				}
				closeType = 1;
				onConfirm();
				AbstractConfirmCardDialog.this.dispose();
			}
		});
		btn_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeType = 2;
				onCancel();
				AbstractConfirmCardDialog.this.dispose();
			}
		});
		panel.add(btn_confirm);
		panel.add(btn_cancel);
		return panel;
	}

	public int getCloseType() {
		return closeType;
	}

}
