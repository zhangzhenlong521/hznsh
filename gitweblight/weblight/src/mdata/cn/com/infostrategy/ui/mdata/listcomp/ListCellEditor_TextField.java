/**************************************************************************
 * $RCSfile: ListCellEditor_TextField.java,v $  $Revision: 1.6 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.listcomp;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.mdata.NumberFormatdocument;

public class ListCellEditor_TextField extends DefaultCellEditor implements IBillCellEditor, FocusListener {

	private static final long serialVersionUID = 1L;

	Pub_Templet_1_ItemVO itemVO = null;

	JTextField textField = null;

	private StringItemVO oldStrItemVO = null;
	private NumberFormatdocument numfd = null;

	public ListCellEditor_TextField(JTextField _textField, Pub_Templet_1_ItemVO _itemvo) {
		super(_textField);
		this.itemVO = _itemvo;
		if (itemVO.getUCDfVO() != null) { //�б�����ʱ�ж�
			numfd = new NumberFormatdocument(itemVO.getUCDfVO());
		}
	}

	public Component getTableCellEditorComponent(JTable table, Object _value, boolean isSelected, int row, int column) {
		oldStrItemVO = (StringItemVO) _value;
		CommUCDefineVO ucvo = itemVO.getUCDfVO();
		textField = new JTextField();
		if (ucvo != null) { //�б�����ʱ�ж�
			String type = ucvo.getTypeName();
			if (type != null) {
				if (type.equals(WLTConstants.COMP_TEXTFIELD)) { //����Ǵ��ı���
					if (ucvo != null && "�Զ���У��".equals(ucvo.getConfValue("����")) && numfd != null) {
						textField.setDocument(numfd); //�������ֿ�ֻ����������,������ĸ���ü���!!!!
					}
				} else if (type.equals(WLTConstants.COMP_NUMBERFIELD)) { //��������ֿ�
					textField = new JFormattedTextField(); //��ʽ�������ֿ�
					textField.setText(_value != null ? String.valueOf(_value) : "");
					textField.setHorizontalAlignment(SwingConstants.RIGHT); //
					textField.setDocument(numfd); //�������ֿ�ֻ����������,������ĸ���ü���!!!!
				} else if (type.equals(WLTConstants.COMP_PASSWORDFIELD)) { //����������
					textField = new JPasswordField(); //
					textField.setText(_value != null ? String.valueOf(_value) : "");
					textField.setHorizontalAlignment(SwingConstants.LEFT); //
				}
			}
		}
		if (oldStrItemVO != null) {
			textField.setText(oldStrItemVO.getStringValue());
		} else {
			textField.setText("");
		}
		textField.setFont(LookAndFeel.font); //

		if (itemVO.getItemtype().equals(WLTConstants.COMP_NUMBERFIELD)) {
			textField.setHorizontalAlignment(JTextField.RIGHT); //��������ֿ�,����!!
		} else if (itemVO.getItemtype().equals(WLTConstants.COMP_PASSWORDFIELD)) {
			textField.setHorizontalAlignment(JTextField.RIGHT); //��������ֿ�,����!!
		}

		if (itemVO.getListiseditable() == null || itemVO.getListiseditable().equals("1") || itemVO.getListiseditable().equals("2") || itemVO.getListiseditable().equals("3")) {
			textField.setEditable(true); //
		} else {
			textField.setEditable(false); //
		}
		textField.addFocusListener(this);//�������ӱ�����������б�ɱ༭���༭�������Ƭ�������ؼ�¼������ʱ�������ӱ��еı༭��û��ֹͣ�༭״̬������Ҫ�ֶ�����һ�¡����/2012-08-02��
		return textField;
	}

	public Object getCellEditorValue() {
		String str_text = null;
		if (itemVO.getItemtype().equals(WLTConstants.COMP_PASSWORDFIELD)) {
			str_text = new String(((JPasswordField) textField).getPassword());
		} else {
			str_text = textField.getText(); //
		}

		StringItemVO newStrItemVO = new StringItemVO(str_text); //
		if (oldStrItemVO == null) {
			if (textField.getText().equals("")) {
				newStrItemVO.setValueChanged(false); //
			} else {
				newStrItemVO.setValueChanged(true); //
			}
		} else {
			if (textField.getText().equals(oldStrItemVO.getStringValue())) {
				newStrItemVO.setValueChanged(false); //
			} else {
				newStrItemVO.setValueChanged(true); //
			}
		}

		return newStrItemVO;
	}

	public boolean isCellEditable(EventObject anEvent) {
		if (itemVO.getListiseditable() != null && !itemVO.getListiseditable().equals("1")) {
			return false;
		} else {
			if (anEvent instanceof MouseEvent) {
				return ((MouseEvent) anEvent).getClickCount() >= 2;
			} else {
				return true;
			}
		}
	}

	public javax.swing.JComponent getCompent() {
		return textField;
	}

	public void focusGained(FocusEvent e) {

	}

	public void focusLost(FocusEvent e) {
		this.stopCellEditing();
	}

}
