package cn.com.infostrategy.ui.mdata;

import java.awt.Toolkit;
import java.math.BigDecimal;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import cn.com.infostrategy.to.mdata.CommUCDefineVO;

/**
 * 数字文本框的输入约束..
 * 本来想在这里做更为全面的验证后来发现不好，应该再加一个验证功能，这里只做简单的输入验证/sunfujun/20121106
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
			this.type = ucvo.getConfValue("类型");
			this.allowChar = ucvo.getConfValue("允许字符");
		}
	}
	public NumberFormatdocument() {

	}
	
	public void insertString(int offset, String s, AttributeSet attributeSet) throws BadLocationException {
		char[] charofs = s.toCharArray();// 查看字符串的每个字符是否是数字
		boolean isNum = true;
		for (int i = 0; i < charofs.length; i++) {
			if ("纯数字".equals(type)) {
				if ("0123456789".indexOf(charofs[i]) < 0) {
					isNum = false;
					break;
				}
			} else if ("电话".equals(type)) {
				if ("-0123456789".indexOf(charofs[i]) < 0) {
					isNum = false;
					break;
				}
			} else if ("整数".equals(type)) {
				if ("0123456789".indexOf(charofs[i]) < 0) {
					isNum = false;
					break;
				}
			} else if ("自定义校验".equals(type)) { //自定义，需要手工写allowChar
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
		if (isNum) { // 如果是数字就插入!!
			if (offset == 0 && (s.equals("."))) {
				Toolkit.getDefaultToolkit().beep(); // 放个声音!!
				throw new BadLocationException(s, offset);//sunfujun/20121105/应该抛异常不应该返回修改中文输入法的问题
			} else {
				String hole = this.getText(0, this.getLength());
				String whole = hole.substring(0, offset) + s + hole.substring(offset, hole.length());
				if (checkFormat(whole, this.type)) {
					super.insertString(offset, s, attributeSet);
					//					if (whole.endsWith(".")) {//先前孙富君加的代码，如果想输入1.5，这段代码会在输入点号后自动补俩零，就变成1.005了，这样肯定不行，故干脆去了【李春娟/2013-05-29】
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
	 * 删除事件执行此方法 这里不做验证了，否则不让删除肯定认为是bug了
	 */
	public void remove(int offs, int len) throws BadLocationException {
		super.remove(offs, len);
	}

	/**
	 * 发生替换事件时执行此其父类是先执行remove再insertString
	 */
	public void replace(int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
		super.replace(offset, length, text, attrs);
	}

	public boolean checkFormat(String s, String type) {
		if (s == null || "".equals(s)) {
			return true;
		} else {
			if ("纯数字".equals(type)) {
				char[] charofs = s.toCharArray();
				for (int i = 0; i < charofs.length; i++) {
					if ("0123456789".indexOf(charofs[i]) < 0) {
						return false;
					}
				}
			} else if ("电话".equals(type)) {
				char[] charofs = s.toCharArray();
				for (int i = 0; i < charofs.length; i++) {
					if ("-0123456789".indexOf(charofs[i]) < 0) {
						return false;
					}
				}
			} else if ("整数".equals(type)) {
				if (s.startsWith("0") || s.indexOf(".") >= 0) {
					return false;
				} else {
					try {
						Integer.parseInt(s);
					} catch (Exception e) {
						return false;
					}
				}
			} else if ("小数".equals(type)) {
				try {
					new BigDecimal(s); //孙富君原来用的是weblogic中的一个decimal类,现改成jdk中标准的BigDecimal类,
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				if (s.startsWith(".")) { // bug 
					return false;
				}
			} else if ("自定义校验".equals(type)) {
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
