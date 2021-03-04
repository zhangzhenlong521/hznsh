package cn.com.infostrategy.ui.mdata;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;

import javax.swing.JLabel;

import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;

/**
 * �����ӵ�Label,��������Ե���һ���Ի���
 * ���ݴ�ǰ��˼��һ�����ģ��������Զ�����壬��Ƭ���б���Ҳ���Զ�����壬��ÿ���ؼ���ʵ����Ҳ�������Զ�������ݣ�Ϊ�˸�Ϊҳ���Ѻã�����ʹ�ó����ӵķ�ʽ����!!
 * ����ǳ�����Label��������Ŀ��!!
 * @author xch
 *
 */
public class WLTHyperLinkLabel extends JLabel {

	private static final long serialVersionUID = 2752965276881657192L;

	private String itemKey, itemName, text, hyperLinkDialogName = null;

	private BillPanel billPanel = null; //��������,������BillCardPanel,BillListPanel�ȵ�...

	private WLTHyperLinkLabel() {
	}

	public WLTHyperLinkLabel(String _text, String _hyperLinkDialogName) {
		super(); //
		this.text = _text;
		this.hyperLinkDialogName = _hyperLinkDialogName;
		resetSize();
	}

	public WLTHyperLinkLabel(String _itemKey, String _itemName, String _text, String _hyperLinkDialogName, BillPanel _billPanel) {
		super(); //
		this.itemKey = _itemKey;
		this.itemName = _itemName;
		this.text = _text;
		this.hyperLinkDialogName = _hyperLinkDialogName;
		this.billPanel = _billPanel; //
		resetSize();
	}

	private void resetSize() {
		this.setName(this.text); //
		this.setFocusable(false); //
		this.setText("<html><body>&nbsp;<u>" + this.text + "</u></body></html>");
		int li_length = getTexLengtht(text); //
		this.setForeground(Color.BLUE);
		this.setPreferredSize(new Dimension(li_length, 20));
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				onOpenDialog(); //
			}

			public void mouseEntered(MouseEvent e) {
				onMouseEnter();
			}

			public void mouseExited(MouseEvent e) {
				onMouseExit();
			}

		});
	}

	private int getTexLengtht(String _text) {
		int li_count = getUnicodeLength(_text);
		int li_length = 20 + (li_count * 8);
		if (li_length > 85) {
			li_length = 85;
		}
		return li_length; //
	}

	public int getUnicodeLength(String s) //���������ָ����һ���ַ���,������ֽڳ���,����ַ������и������ַ�,��ô���ĳ��Ⱦ���2
	{
		char c;
		int j = 0;
		boolean bo_1 = false;
		if (s == null || s.length() == 0) {
			return 0;
		}
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) >= 0x100) {
				bo_1 = true;
				j = j + 2;
			} else {
				j = j + 1;
			}
		}
		//		if (bo_1)
		//			j = j - 1;
		return j;
	}

	private void onOpenDialog() {
		if (this.hyperLinkDialogName == null || this.hyperLinkDialogName.trim().equals("")) {
			MessageBox.show(this, "û��ָ��������Ӧ��·��!"); //
			return;
		}

		if (hyperLinkDialogName.indexOf(".") > 0) { //�����"."����˵����һ����·����,���䴴������!!
			onOpenCustDialog();
		} else { //���û�е㣬��˵����һ��ģ����룬��ô��ֱ��ʹ�÷��ģ�売����
			onOpenCommDialog(); //
		}

	}

	private void onOpenCommDialog() {
		CommHyperLinkDialog dialog = new CommHyperLinkDialog(this, hyperLinkDialogName); //����һ��ģ�����
		dialog.setTitle("[" + this.text + "][" + (itemName == null ? "" : itemName.trim()) + "]");
		dialog.setBillPanel(this.billPanel);
		dialog.setItemKey(this.itemKey);
		dialog.setItemName(this.itemName);
		dialog.initialize(); //��ʼ��ҳ��
		dialog.setVisible(true);
	}

	private void onOpenCustDialog() {
		try {
			Class dialog_class = Class.forName(this.hyperLinkDialogName);
			Class cp[] = { java.awt.Container.class }; //
			Constructor constructor = dialog_class.getConstructor(cp);
			BillDialog dialog = (BillDialog) constructor.newInstance(new Object[]{this}); ////
			dialog.setTitle("[" + this.text + "][" + (itemName == null ? "" : itemName.trim()) + "]");
			if (dialog instanceof AbstractHyperLinkDialog) {
				((AbstractHyperLinkDialog) dialog).setBillPanel(this.billPanel); //����BillPanel
				((AbstractHyperLinkDialog) dialog).setItemKey(this.itemKey);
				((AbstractHyperLinkDialog) dialog).setItemName(this.itemName);
				((AbstractHyperLinkDialog) dialog).initialize(); //��ʼ��ҳ��
				dialog.setVisible(true);
			} else {
				if (!dialog.isVisible()) {
					dialog.setVisible(true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace(); //
			MessageBox.showException(this, e);
		}
	}

	private void onMouseEnter() {
		this.setForeground(Color.RED);
		this.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
	}

	private void onMouseExit() {
		this.setForeground(Color.BLUE);
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
	}

	public BillPanel getBillPanel() {
		return billPanel;
	}

	public String getItemKey() {
		return itemKey;
	}

}
