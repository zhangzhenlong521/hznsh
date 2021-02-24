/**************************************************************************
 * $RCSfile: ListCellEditor_TextArea.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.listcomp;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellEditor;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.mdata.NumberFormatdocument;

public class ListCellEditor_TextArea extends AbstractCellEditor implements TableCellEditor, IBillCellEditor, FocusListener {

	private static final long serialVersionUID = 1L;

	private JTextArea textArea = null; //

	private Pub_Templet_1_ItemVO itemVO = null; //

	private StringItemVO oldStrItemVO = null;

	private NumberFormatdocument numfd = null;

	public ListCellEditor_TextArea(Pub_Templet_1_ItemVO _itemvo) {
		itemVO = _itemvo;
		if (itemVO.getUCDfVO() != null) { //�б�����ʱ�ж�
			numfd = new NumberFormatdocument(itemVO.getUCDfVO());
		}
	}

	public Component getTableCellEditorComponent(JTable table, Object _value, boolean isSelected, int row, int column) {
		oldStrItemVO = (StringItemVO) _value;
		textArea = new JTextArea(); //new ListCPanel_Ref(itemVO, (RefItemVO) value, model.getBillListPanel()); //�����ؼ�!!����ʼֵ��BillListPanel�������!!
		textArea.setText(oldStrItemVO == null ? "" : oldStrItemVO.getStringValue()); //
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true); //

		CommUCDefineVO ucvo = itemVO.getUCDfVO();
		if (ucvo != null) { //�б�����ʱ�ж�
			final String type = ucvo.getTypeName();
			if (type != null) {
				if (type.equals(WLTConstants.COMP_TEXTAREA)) { //����Ǵ��ı���
					if (ucvo != null && "�Զ���У��".equals(ucvo.getConfValue("����")) && numfd != null) {  //by haoming 2016-05-09
						textArea.setDocument(numfd); //�������ֿ�ֻ����������,������ĸ���ü���!!!!
						textArea.setText(oldStrItemVO == null ? "" : oldStrItemVO.getStringValue());
					}
				}
			}
		}

		if (itemVO.getListiseditable() == null || !itemVO.getListiseditable().equals("1") || !itemVO.getListiseditable().equals("2") || !itemVO.getListiseditable().equals("3")) {
			textArea.setEditable(true); //
		} else {
			textArea.setEditable(false); //
		}
		textArea.addFocusListener(this);//�������ӱ�����������б�ɱ༭���༭�������Ƭ�������ؼ�¼������ʱ�������ӱ��еı༭��û��ֹͣ�༭״̬������Ҫ�ֶ�����һ�¡����/2012-08-02��
		return new JScrollPane(textArea); //
	}

	public Object getCellEditorValue() {
		String str_text = textArea.getText(); //
		StringItemVO newStrItemVO = new StringItemVO(str_text); //
		if (oldStrItemVO == null) {
			if (textArea.getText().equals("")) {
				newStrItemVO.setValueChanged(false); //
			} else {
				newStrItemVO.setValueChanged(true); //
			}
		} else {
			if (textArea.getText().equals(oldStrItemVO.getStringValue())) {
				newStrItemVO.setValueChanged(false); //
			} else {
				newStrItemVO.setValueChanged(true); //
			}
		}

		return newStrItemVO;
	}

	public boolean isCellEditable(EventObject evt) {
		if (itemVO.getListiseditable() != null && itemVO.getListiseditable().equals("1")) {
			if (evt instanceof MouseEvent) {
				return ((MouseEvent) evt).getClickCount() >= 2;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public javax.swing.JComponent getCompent() {
		return textArea;
	}

	public void focusGained(FocusEvent e) {

	}

	public void focusLost(FocusEvent e) {
		this.stopCellEditing();
	}

}
