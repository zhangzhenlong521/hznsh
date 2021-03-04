package cn.com.infostrategy.ui.mdata;

import java.awt.Toolkit;
import java.math.BigDecimal;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import cn.com.infostrategy.to.mdata.CommUCDefineVO;

/**
 * �����ı��������Լ��..
 * ����������������Ϊȫ�����֤�������ֲ��ã�Ӧ���ټ�һ����֤���ܣ�����ֻ���򵥵�������֤/sunfujun/20121106
 * @author xch
 *
 */
public class NumberFormatdocument extends PlainDocument {

	private static final long serialVersionUID = -6296303236990368928L;
	private CommUCDefineVO ucvo = null;
	private String type = "";
	private String allowChar = null;

	public NumberFormatdocument(CommUCDefineVO ucvo_) {
		this.ucvo = ucvo_;
		if (ucvo != null) {
			this.type = ucvo.getConfValue("����");
			this.allowChar = ucvo.getConfValue("�����ַ�");
		}
	}
	public NumberFormatdocument() {

	}
	
	public void insertString(int offset, String s, AttributeSet attributeSet) throws BadLocationException {
		char[] charofs = s.toCharArray();// �鿴�ַ�����ÿ���ַ��Ƿ�������
		boolean isNum = true;
		for (int i = 0; i < charofs.length; i++) {
			if ("������".equals(type)) {
				if ("0123456789".indexOf(charofs[i]) < 0) {
					isNum = false;
					break;
				}
			} else if ("�绰".equals(type)) {
				if ("-0123456789".indexOf(charofs[i]) < 0) {
					isNum = false;
					break;
				}
			} else if ("����".equals(type)) {
				if ("0123456789".indexOf(charofs[i]) < 0) {
					isNum = false;
					break;
				}
			} else if ("�Զ���У��".equals(type)) { //�Զ��壬��Ҫ�ֹ�дallowChar
				if (allowChar.indexOf(charofs[i]) < 0) {
					isNum = false;
					break;
				}
			} else {
				if ("-0123456789.".indexOf(charofs[i]) < 0) {
					isNum = false;
					break;
				}
			}
		}
		if (isNum) { // ��������־Ͳ���!!
			if (offset == 0 && (s.equals("."))) {
				Toolkit.getDefaultToolkit().beep(); // �Ÿ�����!!
				throw new BadLocationException(s, offset);//sunfujun/20121105/Ӧ�����쳣��Ӧ�÷����޸��������뷨������
			} else {
				String hole = this.getText(0, this.getLength());
				String whole = hole.substring(0, offset) + s + hole.substring(offset, hole.length());
				if (checkFormat(whole, this.type)) {
					super.insertString(offset, s, attributeSet);
					//					if (whole.endsWith(".")) {//��ǰ�︻���ӵĴ��룬���������1.5����δ�����������ź��Զ������㣬�ͱ��1.005�ˣ������϶����У��ʸɴ�ȥ�ˡ����/2013-05-29��
					//						super.insertString(this.getLength(), "00", attributeSet);
					//					}
				} else {
					Toolkit.getDefaultToolkit().beep();
					throw new BadLocationException(s, offset);
				}
			}
		} else {
			Toolkit.getDefaultToolkit().beep();
			throw new BadLocationException(s, offset);
		}

	}

	/**
	 * ɾ���¼�ִ�д˷��� ���ﲻ����֤�ˣ�������ɾ���϶���Ϊ��bug��
	 */
	public void remove(int offs, int len) throws BadLocationException {
		super.remove(offs, len);
	}

	/**
	 * �����滻�¼�ʱִ�д��丸������ִ��remove��insertString
	 */
	public void replace(int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
		super.replace(offset, length, text, attrs);
	}

	public boolean checkFormat(String s, String type) {
		if (s == null || "".equals(s)) {
			return true;
		} else {
			if ("������".equals(type)) {
				char[] charofs = s.toCharArray();
				for (int i = 0; i < charofs.length; i++) {
					if ("0123456789".indexOf(charofs[i]) < 0) {
						return false;
					}
				}
			} else if ("�绰".equals(type)) {
				char[] charofs = s.toCharArray();
				for (int i = 0; i < charofs.length; i++) {
					if ("-0123456789".indexOf(charofs[i]) < 0) {
						return false;
					}
				}
			} else if ("����".equals(type)) {
				if (s.startsWith("0") || s.indexOf(".") >= 0) {
					return false;
				} else {
					try {
						Integer.parseInt(s);
					} catch (Exception e) {
						return false;
					}
				}
			} else if ("С��".equals(type)) {
				try {
					new BigDecimal(s); //�︻��ԭ���õ���weblogic�е�һ��decimal��,�ָĳ�jdk�б�׼��BigDecimal��,
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				if (s.startsWith(".")) { // bug 
					return false;
				}
			} else if ("�Զ���У��".equals(type)) {
				return true;
			} else {
				char[] charofs = s.toCharArray();
				for (int i = 0; i < charofs.length; i++) {
					if ("-0123456789.".indexOf(charofs[i]) < 0) {
						return false;
					}
				}
			}
		}
		return true;
	}

}
