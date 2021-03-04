package cn.com.infostrategy.ui.common;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * 许多地主需要右键弹出一些帮助提示! 为了不到处创建匿名类,统一用这个类!!
 * @author xch
 *
 */
public class WLTMouseAdapterAsMsgBox extends MouseAdapter {
	private javax.swing.JComponent parent = null; //
	private int type = -1; //
	private String str_text = null; //

	public WLTMouseAdapterAsMsgBox(javax.swing.JComponent _parent, int _type, String _text) {
		this.parent = _parent; //
		this.type = _type; //
		this.str_text = _text; //
		if (parent != null) {
			parent.setToolTipText("<html>" + getShortText(str_text) + "<br>点击右键查看详细帮助!!</html>");
		}
	}

	@Override
	public void mouseClicked(MouseEvent _event) {
		super.mouseClicked(_event);
		if (this.type == 1) {

		} else if (this.type == 2) { //如果是第二种方式
			if (_event.getButton() == MouseEvent.BUTTON3) { //右键
				MessageBox.show(parent, str_text); //
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		super.mouseEntered(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		super.mouseExited(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		super.mouseWheelMoved(e);
	}

	private String getShortText(String _text) {
		if (_text == null) {
			return null;
		}
		if (_text.length() > 10) {
			return _text.substring(0, 10) + "...";
		} else {
			return _text; //
		}
	}
}
