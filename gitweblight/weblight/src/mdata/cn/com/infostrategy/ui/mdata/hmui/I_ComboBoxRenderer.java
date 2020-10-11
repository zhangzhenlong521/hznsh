package cn.com.infostrategy.ui.mdata.hmui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/** 
 * Copyright Pushine
 * @ClassName: cn.com.infostrategy.ui.mdata.hmui.I_ComboBoxRenderer 
 * @Description: ����
 * @author haoming
 * @date Mar 19, 2013 4:44:18 PM
 *  
*/
public class I_ComboBoxRenderer extends BasicComboBoxRenderer {
	private I_ComboBoxUI ui;
	private boolean selected = false;

	public I_ComboBoxRenderer(I_ComboBoxUI _ui) {
		ui = _ui;
		setOpaque(false); //��������͸��
		setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
	}

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		JLabel compent = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus); //
		if (I_LookAndFeel.windows_word_antialias) {
			compent.setFont(I_LookAndFeel.font_word_yh); //�����ź�
		}
		compent.setBorder(BorderFactory.createEmptyBorder(1, 3, 3, 3));
		selected = isSelected;
		if (index >= 0) {
			if (value == null || value.toString() == null || value.toString().trim().equals("")) {//�������ǰ��½��Ա������λ�ı��ˣ����л����ť���ʱ���ﱨ�����/2014-04-18��
				list.setToolTipText(null); //
			} else {
				list.setToolTipText(value.toString());
			}
		}
		return compent;
	}

	public void paintComponent(Graphics g) {
		if (ui.isPopupVisible(null) && selected) {
			LAFUtil.fillTextureRoundRec((Graphics2D) g, getBackground(), 0, 0, getWidth(), getHeight(), 5, 20);
		}//�����������
		super.paintComponent(g); //���ø������
	}

	public static class UIResource extends I_ComboBoxRenderer implements javax.swing.plaf.UIResource {

		public UIResource(I_ComboBoxUI ui) {
			super(ui);
		}
	}

}