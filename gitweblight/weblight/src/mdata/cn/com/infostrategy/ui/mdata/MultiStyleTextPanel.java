package cn.com.infostrategy.ui.mdata;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextPane;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.ui.common.LookAndFeel;

/**
 * ��ʵ����,���������ùؼ��ֲ�ɫ��.
 * @author xch
 *
 */
public class MultiStyleTextPanel extends JTextPane implements KeyListener {

	private static final long serialVersionUID = -298997609886389303L;
	protected DefaultStyledDocument m_doc;
	private VectorMap map_keyWordColor = new VectorMap(); //

	private MutableAttributeSet normalAttr;

	public MultiStyleTextPanel(String _text) {
		super(); //
		m_doc = new DefaultStyledDocument(new StyleContext());
		this.setDocument(m_doc);
		this.setFont(LookAndFeel.font); //
		normalAttr = new SimpleAttributeSet();
		StyleConstants.setBold(normalAttr, false);
		StyleConstants.setForeground(normalAttr, Color.black);

		this.addKeyListener(this);
		if (_text != null) {
			setText(_text); //
		}
	}

	public MultiStyleTextPanel() {
		this(null); //
	}

	/**
	 * ���ӹؼ��ֵķ��
	 * @param _keyWords
	 * @param _foreGround
	 */
	public void addKeyWordStyle(String[] _keyWords, Color _foreGround, boolean _isBold) {
		SimpleAttributeSet keyAttr = new SimpleAttributeSet();
		StyleConstants.setForeground(keyAttr, _foreGround); //��ɫ�ı�ǩ
		StyleConstants.setBold(keyAttr, _isBold); //�Ǵ���
		for (int i = 0; i < _keyWords.length; i++) {
			map_keyWordColor.put(_keyWords[i], keyAttr); //
		}
		resetStyleDocument(); //
	}

	@Override
	public void setText(String t) {
		super.setText(t);
		resetStyleDocument(); //
	}

	/**
	 * ��������
	 * @param _text
	 */
	public void inputText(String _text) {
		try {
			int pos = this.getSelectionStart();
			if (isTextSelected()) {
				this.replaceSelection("" + _text);
			} else {
				this.getStyledDocument().insertString(pos, _text, null);
			}
			this.requestFocus();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			resetStyleDocument(); //
		}
	}

	/**
	 * �ж��ı������Ƿ���ѡ�е��ı�
	 * 
	 * @return
	 */
	private boolean isTextSelected() {
		return (this.getSelectionEnd() - this.getSelectionStart()) != 0;
	}

	/**
	 * ���÷����ɫ
	 */
	public void resetStyleDocument() {
		try {
			//String str_text = this.getText(); ////
			String str_text = this.getDocument().getText(0, this.getDocument().getLength()); //��ǰ��this.getText()��ɰܻ���ʱ,���λ��ɫ!!
			//TBUtil tbUtil = new TBUtil(); //
			//str_text = tbUtil.replaceAll(str_text, "\n", " "); //
			//str_text = tbUtil.replaceAll(str_text, "\r", " "); //
			m_doc.setCharacterAttributes(0, str_text.length(), new SimpleAttributeSet(), true); //
			String[] str_keyWorlds = map_keyWordColor.getKeysAsString(); //�õ����йؼ���
			for (int i = 0; i < str_keyWorlds.length; i++) {
				setColor(str_text, str_keyWorlds[i]); //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * Ϊĳ���ؼ���������ɫ.
	 * @param _allText
	 * @param _keyword
	 */
	private void setColor(String _allText, String _keyword) {
		String str_text = _allText; //
		int li_pos = str_text.indexOf(_keyword); //
		int li_start = li_pos; //
		while (li_pos >= 0) {
			//String str_itemtext = str_text.substring(li_start, li_start + _keyword.length()); //
			//System.out.println("Ҫ��ɫ������[" + str_itemtext + "]"); //
			m_doc.setCharacterAttributes(li_start, _keyword.length(), (SimpleAttributeSet) map_keyWordColor.get(_keyword), true); //
			str_text = str_text.substring(li_pos + _keyword.length(), str_text.length()); //
			li_pos = str_text.indexOf(_keyword); //��������û�иùؼ�����
			li_start = li_start + li_pos + _keyword.length(); //
		}
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		resetStyleDocument(); //
	}

	public void keyTyped(KeyEvent e) {

	}

}
