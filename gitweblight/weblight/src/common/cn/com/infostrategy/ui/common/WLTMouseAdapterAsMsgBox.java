package cn.com.infostrategy.ui.common;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * ��������Ҫ�Ҽ�����һЩ������ʾ! Ϊ�˲���������������,ͳһ�������!!
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
			parent.setToolTipText("<html>" + getShortText(str_text) + "<br>����Ҽ��鿴��ϸ����!!</html>");
		}
	}

	@Override
	public void mouseClicked(MouseEvent _event) {
		super.mouseClicked(_event);
		if (this.type == 1) {

		} else if (this.type == 2) { //����ǵڶ��ַ�ʽ
			if (_event.getButton() == MouseEvent.BUTTON3) { //�Ҽ�
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
