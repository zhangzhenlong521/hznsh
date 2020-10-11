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
 * 这是一个选择或确认的通用窗口(列表),就是一个两个按钮(确认/取消),然后上面一下列表框,然后选择一个记录即可!!
 * @author xch
 *
 */
public abstract class AbstractConfirmListDialog extends BillDialog implements BillListSelectListener, MouseListener {

	protected BillListPanel billListPanel = null;

	private int closeType = 0;

	public abstract String getInitTitle(); //初始化标题

	public abstract int getInitWidth(); //初始化宽度,这个方法曾经被我写成getWidth(),结果导致拖到页面大小变化时,屏幕灰屏抽住了!!查长了长时间才找到原因所在!!

	public abstract int getInitHeight(); //初始化高度

	public abstract void onBeforeConfirm() throws Exception; //点击确定时做的

	public abstract void onBeforeCancel() throws Exception; //点击取消时做的

	public abstract AbstractTMO getAbstractTempletVO(); //数据VO

	private BillVO selectedBillVO = null; //

	public AbstractConfirmListDialog(Container _parent) {
		super(_parent);
		this.setTitle(getInitTitle());
		this.setSize(getInitWidth(), getInitHeight());
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //覆盖父类的默认关闭动作!!!
		locationToCenterPosition(); //居中处理..
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
		getBillListPanel().getTable().addMouseListener(this); //鼠标双击事件!!
		initialize(); //还有一次机会修改属性,比如BillCard是否显示标题等!!
	}

	//可以进行一些后续操作
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
		WLTButton btn_confirm = new WLTButton("确定");
		WLTButton btn_cancel = new WLTButton("取消");
		btn_confirm.setPreferredSize(new Dimension(70, 20)); //
		btn_cancel.setPreferredSize(new Dimension(70, 20)); //
		btn_confirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					closeType = 1;
					putData(); //
					onBeforeConfirm(); //点击确定之前做的处理,可以进行一些校验操作!!!!!
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
					onBeforeCancel(); //取消之前的处理!!可以进行某些校验处理!!!!!
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
			throw new WLTAppException("请选择一条记录!");
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
