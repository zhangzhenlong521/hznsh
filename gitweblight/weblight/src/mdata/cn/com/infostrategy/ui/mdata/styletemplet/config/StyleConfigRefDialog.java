/**************************************************************************
 * $RCSfile: StyleConfigRefDialog.java,v $  $Revision: 1.12 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.config;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;
import cn.com.infostrategy.ui.mdata.styletemplet.AbstractTempletRefPars;

/**
 * ���ַ��ģ�����ý���
 * @author xch
 *
 */
public class StyleConfigRefDialog extends AbstractRefDialog {

	private static final long serialVersionUID = -7521165672198422057L;
	private Container parentContainer = null;
	private String title = null; //
	private String initstring = null; //
	private String str_commandtype = null;
	private JButton btn_ok = null; //ȷ����ť
	private JButton btn_cancel = null; //ȡ����ť
	private JButton btn_close = null; //�رհ�ť

	AbstractTempletRefPars parap = null;

	String result = ""; //

	public StyleConfigRefDialog(Container _parent, String _title, RefItemVO value, BillPanel panel) {
		super(_parent, _title, value, panel);
		this.parentContainer = _parent; //
		this.title = _title; //
	}

	/**
	 * ��ʼ��ҳ��,ʵ�ֳ��󷽷�!!
	 */
	public void initialize() {
		if (title.equals("·��1")) {
			str_commandtype = ((BillCardPanel) getBillPanel()).getRealValueAt("COMMANDTYPE"); //�˵�����
		} else if (title.equals("·��2")) {
			str_commandtype = ((BillCardPanel) getBillPanel()).getRealValueAt("COMMANDTYPE2"); //�˵�����
		} else if (title.equals("·��3")) {
			str_commandtype = ((BillCardPanel) getBillPanel()).getRealValueAt("COMMANDTYPE3"); //�˵�����
		}

		if (getInitRefItemVO() != null) {
			initstring = ((RefItemVO) getInitRefItemVO()).getName(); //
			if (initstring != null) {
				this.setTitle(this.getTitle() + "[" + initstring + "]"); //
			}
		}

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(getMainPanel(), BorderLayout.CENTER); //�����!!!,���б仯��������!
		this.getContentPane().add(getSthPanel(), BorderLayout.SOUTH);

		if ("00".equals(str_commandtype) || "99".equals(str_commandtype)) {
			this.setSize(650, 135); //������Զ���WorkPanel���򽫴�����С!!!
			resetLocation();
		}
	}

	private JPanel getMainPanel() {
		if (initstring == null) {
			initstring = "";
		}

		if (str_commandtype == null) {
			JPanel panel = new JPanel(); //
			panel.add(new JLabel("��ѡ��һ����������!")); //
			return panel; //
		}

		if (str_commandtype.equals("00")) { //�Զ���WorkpPanel
			parap = new StyleConfigPanel_00(initstring);
		} else if (str_commandtype.equals("0A")) { //Format��ʽ���
			parap = new StyleConfigPanel_0A(initstring);
		} else if (str_commandtype.equals("11")) { //��ע�Ṧ�ܵ�!!!!
			parap = new StyleConfigPanel_11(initstring);
		} else if (str_commandtype.equals("99")) { //�Լ�����Frame
			parap = new StyleConfigPanel_99(initstring);
		} else { //���ַ��ģ��(������,���ٵ�)
			parap = new StyleConfigPanel_Styles(initstring); //
		}
		return parap;
	}

	private void resetLocation() {
		Frame frame = JOptionPane.getFrameForComponent(parentContainer);
		double ld_screenWidth = frame.getSize().getWidth();
		double ld_screenHeight = frame.getSize().getHeight();
		double ld_x = frame.getLocation().getX();
		double ld_y = frame.getLocation().getY();

		double ld_thisX = ld_x + ld_screenWidth / 2 - this.getSize().getWidth() / 2; //
		double ld_thisY = ld_y + ld_screenHeight / 2 - this.getSize().getHeight() / 2; //
		if (ld_thisX < 0) {
			ld_thisX = 0;
		}
		if (ld_thisY < 0) {
			ld_thisY = 0;
		}
		this.setLocation(new Double(ld_thisX).intValue(), new Double(ld_thisY).intValue()); //
	}

	public RefItemVO getReturnRefItemVO() {
		return new RefItemVO(result, result, result);
	}

	private JPanel getSthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.CENTER));
		btn_ok = new JButton("ȷ��"); //
		btn_cancel = new JButton("ȡ��"); //
		btn_close = new JButton("�ر�"); //

		btn_ok.setPreferredSize(new Dimension(60, 20));
		btn_cancel.setPreferredSize(new Dimension(60, 20));
		btn_close.setPreferredSize(new Dimension(60, 20));

		btn_ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onConfirm();
			}
		});

		btn_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});

		btn_close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//setResult(initstring);
				onCancel();
			}
		});

		panel.add(btn_ok);
		//panel.add(btn_cancel);
		panel.add(btn_close);
		return panel;
	}

	private boolean setResult() {
		setCloseType(1); //
		result = "";
		if (parap == null) {
			return true;
		}
		try {
			parap.stopEdit();
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
			return false; //
		}
		VectorMap pars = parap.getParameters(); //ȡ�����в���
		String[][] data = pars.getAllDataAsString(); //ת�����ַ�������
		if (str_commandtype.equals("0A")) { //����Ǹ�ʽ�����
			for (int i = 0; i < data.length; i++) { //
				if (data[i][1].equals("")) { //
					MessageBox.show(this, "������ȫ������!"); //
					return false; //
				}
				result = result + data[i][0] + "=" + data[i][1] + ";"; //
			}
			result = result.substring(0, result.length() - 1); //
		} else { //����������
			for (int i = 0; i < data.length; i++) {
				result = result + data[i][1];
			}
		}
		return true;
	}

	private void onConfirm() {
		setCloseType(1);
		if (setResult()) {
			this.dispose();
		}
	}

	private void onCancel() {
		setCloseType(2);
		this.dispose();
	}

	public int getInitWidth() {
		return 950;
	}

	public int getInitHeight() {
		return 525;
	}

}
