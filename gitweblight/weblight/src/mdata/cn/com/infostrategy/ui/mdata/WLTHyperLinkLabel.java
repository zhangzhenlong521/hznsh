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
 * 超链接的Label,从这里可以弹出一个对话框
 * 根据从前的思想一个风格模板可以有自定义面板，卡片与列表本身也有自定义面板，而每个控件其实本身也可以有自定义的内容，为了更为页面友好，我们使用超链接的方式进行!!
 * 这就是超链接Label的由来与目的!!
 * @author xch
 *
 */
public class WLTHyperLinkLabel extends JLabel {

	private static final long serialVersionUID = 2752965276881657192L;

	private String itemKey, itemName, text, hyperLinkDialogName = null;

	private BillPanel billPanel = null; //传入的面板,可能是BillCardPanel,BillListPanel等等...

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

	public int getUnicodeLength(String s) //这个方法是指送入一个字符串,算出其字节长度,如果字符串中有个中文字符,那么他的长度就算2
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
			MessageBox.show(this, "没有指定超链对应的路径!"); //
			return;
		}

		if (hyperLinkDialogName.indexOf(".") > 0) { //如果有"."，则说明是一个类路径名,则反射创建该类!!
			onOpenCustDialog();
		} else { //如果没有点，则说明是一个模板编码，那么就直接使用风格模板２打开它
			onOpenCommDialog(); //
		}

	}

	private void onOpenCommDialog() {
		CommHyperLinkDialog dialog = new CommHyperLinkDialog(this, hyperLinkDialogName); //就是一个模板编码
		dialog.setTitle("[" + this.text + "][" + (itemName == null ? "" : itemName.trim()) + "]");
		dialog.setBillPanel(this.billPanel);
		dialog.setItemKey(this.itemKey);
		dialog.setItemName(this.itemName);
		dialog.initialize(); //初始化页面
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
				((AbstractHyperLinkDialog) dialog).setBillPanel(this.billPanel); //设置BillPanel
				((AbstractHyperLinkDialog) dialog).setItemKey(this.itemKey);
				((AbstractHyperLinkDialog) dialog).setItemName(this.itemName);
				((AbstractHyperLinkDialog) dialog).initialize(); //初始化页面
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
