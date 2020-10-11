package cn.com.infostrategy.ui.common;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicLabelUI;

import sun.swing.SwingUtilities2;

/**
 * �������е�JLabel��UI,�ǳ�NB..
 * @author xch
 *
 */
public class WLTLabelUI extends BasicLabelUI {
	private static Insets mypaintViewInsets = new Insets(0, 0, 0, 0); //

	private int directType = -1;
	private BackGroundDrawingUtil drawutil = new BackGroundDrawingUtil(); //

	public WLTLabelUI() {
	}

	public WLTLabelUI(int _directType) {
		directType = _directType; //
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		Graphics2D g2 = (Graphics2D) g;
		JLabel label = (JLabel) c;
		String text = label.getText();
		if (directType > -1) {
			//Rectangle rect = c.getBounds(); //һ��Ҫ����ʾ����!!Ч���ź�!!!
				drawutil.draw(directType, g, new Rectangle(0, 0, c.getWidth(), c.getHeight()), c.getBackground(), Color.WHITE); //
			//System.out.println("��ͷ������!" + rect.getX() + "," + rect.getY() + "," + rect.getWidth() + "," + rect.getHeight()); //
		}


		if (text == null || text.equals("") || text.toLowerCase().startsWith("<html>")) { //�����html��ͷ,��ʹ��ԭ���Ļ�ͼ��!!
			super.paint(g, c); //
			return;
		}

		Icon icon = (label.isEnabled()) ? label.getIcon() : label.getDisabledIcon();
		int iconWidth = 0; //ͼ����
		int iconHeight = 0; //ͼ��߶�
		if (icon != null) {
			iconWidth = icon.getIconWidth(); //
			iconHeight = icon.getIconHeight(); //
		}

		HashMap textColorMap = (HashMap) label.getClientProperty("TextItemColor"); //ָ���ķָ���������ɫ
		FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(label.getFont()); //sun.swing.SwingUtilities2.getFontMetrics(label, g);
		Insets insets = c.getInsets(mypaintViewInsets); //ȡ�ñ߿�
		int li_labelwidth = (int) c.getSize().getWidth(); //�ؼ����..

		int li_x = insets.left;
		int li_y = insets.top + fm.getAscent();

		int li_width = c.getWidth() - (insets.left + insets.right);
		int li_height = c.getHeight() - (insets.top + insets.bottom);
		g2.setFont(label.getFont()); //����!!!
		g2.setColor(label.getForeground()); //ǰ����ɫ!!!
		String[] str_aa = getSplitArray(text, fm, li_labelwidth); //���ַ�������,���س���ؼ���Ƚ����Զ�����!!!
		if (str_aa.length > 1) { //����л���
			int li_starty = getStartY(label.getVerticalAlignment(), li_y, li_height, str_aa.length * fm.getAscent(), fm.getAscent()); //
			for (int i = 0; i < str_aa.length; i++) { //������������
				int li_startx = getStartX(label.getHorizontalAlignment(), li_x, li_width, SwingUtilities.computeStringWidth(fm, str_aa[i])); // SwingUtilities.computeStringWidth(Toolkit.getDefaultToolkit().getFontMetrics(Font),"sdssdd");   SwingUtilities.computeStringWidth(fm, str_aa[i]);  ��������׼ȷ�����ֵĿ��,����ǰ���㷨������!!!
				if (i == 0) { //��һ��ʱҪ��ͼƬ
					if (icon != null) {
						icon.paintIcon(c, g, li_startx - iconWidth, li_starty - iconHeight); //��ͼƬ
					}
				}
				if (textColorMap == null) { //���û������ɫ!!
					g2.setColor(label.getForeground()); //ǰ����ɫ!!!
					drawString(label,g2,str_aa[i],li_startx,(li_starty + (i * fm.getAscent()) + (0 * 2)));//������0 * 2��i*2,�������ڱ�ͷ��Ҫ����,���ڱ�������Ҫ��ɢ,������ì��!��ʱ����,�Ժ���ú�ͳһ��!(xch)
				} else {
					drawStrBysSplitItem(g2, str_aa[i], li_startx, (li_starty + (i * fm.getAscent()) + (0 * 2)), textColorMap, label.getForeground(), fm,label); //һ�ζλ���!������0 * 2��i*2,�������ڱ�ͷ��Ҫ����,���ڱ�������Ҫ��ɢ,������ì��!��ʱ����!�Ժ���ú�ͳһ��(xch)
				}
			}
		} else { //
			if (textColorMap == null) {
				int li_startx = getStartX(label.getHorizontalAlignment(), li_x, li_width, SwingUtilities.computeStringWidth(fm, text) + iconWidth); //
				int li_starty = getStartY(label.getVerticalAlignment(), li_y, li_height, fm.getAscent(), fm.getAscent()); //
				if (icon != null) {
					icon.paintIcon(c, g, li_startx,(label.getHeight() - iconHeight)/2);
				}
				g2.setColor(label.getForeground()); //ǰ����ɫ!!!
				drawString(label,g2,text,li_startx + iconWidth,li_starty);//����!!!!���ָ������ɫItemMap��Ҫ�ֶλ�!!���鷳!!
			} else {
				int li_startx = getStartX(label.getHorizontalAlignment(), li_x, li_width, SwingUtilities.computeStringWidth(fm, text) + iconWidth); //
				int li_starty = getStartY(label.getVerticalAlignment(), li_y, li_height, fm.getAscent(), fm.getAscent()); //
				if (icon != null) {
					icon.paintIcon(c, g, li_startx, li_starty - iconHeight); //
				}
				drawStrBysSplitItem(g2, text, li_startx + iconWidth, li_starty, textColorMap, label.getForeground(), fm,label); //һ�ζλ���
			}
		}
	}

	//��һ���ַ�������ָ���ķָ���ɫ!!
	private void drawStrBysSplitItem(Graphics2D g2, String _text, int _startX, int _startY, HashMap textColorMap, Color _defaultForeGround, FontMetrics fm,JLabel label) {
		String[] str_items = (String[]) textColorMap.keySet().toArray(new String[0]); //�ȿ��ж��ٶ���
		String[] str_splits = splitTextByItems(_text, str_items); //���׷ָ�!
		for (int i = 0; i < str_splits.length; i++) { //���λ�
			if (textColorMap.containsKey(str_splits[i].toLowerCase())) { //�����������ɫ
				g2.setColor((Color) textColorMap.get(str_splits[i].toLowerCase())); //
			} else {
				g2.setColor(_defaultForeGround); //��������Ĭ�ϵ�ǰ����ɫ!!!
			}
			int li_prefixwidth = 0; //
			if (i != 0) {
				StringBuilder sb_prefix = new StringBuilder(); //
				for (int j = 0; j < i; j++) {
					sb_prefix.append(str_splits[j]); //
				}
				li_prefixwidth = SwingUtilities.computeStringWidth(fm, sb_prefix.toString()); //
			}
			drawString(label,g2,str_splits[i],_startX + li_prefixwidth,_startY);
		}
	}
	
	//��ǰϵͳ��win7���������õ����岻�����ã��ʽ����޸ġ����/2012-04-09��
	private void drawString(JLabel label,Graphics2D g2,String _text, int _startX, int _startY) {
		SwingUtilities2.drawString(label, g2, _text,_startX, _startY);
	}

	//�ָ�һ���ַ���
	private String[] splitTextByItems(String _text, String[] _splitItems) {
		String[] str_initText = new String[] { _text }; //
		for (int i = 0; i < _splitItems.length; i++) { //��һ��������,����,�ڶ����ǹ�,����!!
			ArrayList al_temp = new ArrayList(); //
			for (int j = 0; j < str_initText.length; j++) {
				int li_pos = str_initText[j].toLowerCase().indexOf(_splitItems[i].toLowerCase()); //
				if (li_pos < 0) { //���û��,��ֱ������,�������
					al_temp.add(str_initText[j]); //
				} else {
					String str_remain = str_initText[j]; //
					while (li_pos >= 0) {
						if (li_pos == 0) {
							al_temp.add(str_remain.substring(0, _splitItems[i].length())); //����ǵ�һ��,����ֱ�Ӽ���
						} else {
							al_temp.add(str_remain.substring(0, li_pos)); ////�ȼ���ǰ���
							al_temp.add(str_remain.substring(li_pos, li_pos + _splitItems[i].length()));
						}
						str_remain = str_remain.substring(li_pos + _splitItems[i].length(), str_remain.length()); //����
						li_pos = str_remain.toLowerCase().indexOf(_splitItems[i].toLowerCase()); //���¸�ֵ
					}
					if (!str_remain.equals("")) {
						al_temp.add(str_remain); //
					}
				}
			}
			str_initText = (String[]) al_temp.toArray(new String[0]); //���¸�ֵ!!
		}
		return str_initText; //
	}

	/**
	 * ȡ��ÿ�е�X������
	 * @param _aligntype
	 * @param li_x
	 * @param li_width
	 * @param _textlength
	 * @return
	 */
	private int getStartX(int _aligntype, int li_x, int li_width, int _textlength) {
		if (_aligntype == SwingConstants.LEFT) { //����
			return li_x;
		} else if (_aligntype == SwingConstants.CENTER) { //�������
			int _tmpx = (li_width - _textlength) / 2;
			if (_tmpx < li_x) {
				return li_x;
			} else {
				return _tmpx;
			}
		} else if (_aligntype == SwingConstants.RIGHT) { //����
			int _tmpx = li_width - _textlength;
			if (_tmpx < li_x) {
				return li_x;
			} else {
				return _tmpx;
			}
		} else {
			return li_x;
		}
	}

	/**
	 * ȡ���������ݵ�����Yλ��
	 * @param _vligntype
	 * @param li_y
	 * @param li_height
	 * @param _textheight
	 * @return
	 */
	private int getStartY(int _vligntype, int li_y, int li_height, int _textheight, int _fontsize) {
		if (_vligntype == SwingConstants.TOP) {
			return li_y;
		} else if (_vligntype == SwingConstants.CENTER) {
			int _tmpy = (li_height - _textheight) / 2 + _fontsize;
			if (_tmpy < li_y) {
				return li_y;
			} else {
				return _tmpy;
			}
		} else if (_vligntype == SwingConstants.BOTTOM) {
			int _tmpy = li_height - _textheight + _fontsize;
			if (_tmpy < li_y) {
				return li_y;
			} else {
				return _tmpy;
			}

		} else {
			return li_y;
		}
	}

	/**
	 * ��һ���ַ������зָ�...
	 * @return
	 */
	private String[] getSplitArray(String _str, FontMetrics _fm, int _width) {
		ArrayList al_return = new ArrayList(); // 
		ArrayList al_strs_1 = splitStr(_str, "\r"); //
		for (int i = 0; i < al_strs_1.size(); i++) {
			String str_item_1 = (String) al_strs_1.get(i); //
			ArrayList al_strs_2 = splitStr(str_item_1, "\n"); //
			for (int j = 0; j < al_strs_2.size(); j++) { //
				String str_item_2 = (String) al_strs_2.get(j); //�õ�����...
				ArrayList al_strs_3 = splitByLength(str_item_2, _fm, _width); //��ĳ���ַ������ݳ��Ƚ��зָ�
				for (int k = 0; k < al_strs_3.size(); k++) {
					al_return.add(al_strs_3.get(k)); //
				}
			}
		}

		return (String[]) al_return.toArray(new String[0]); //
	}

	/**
	 * ��ĳ���ַ������ݳ��Ƚ��зָ�..
	 * @param str_item_2
	 * @param _fm
	 * @param _width
	 * @return
	 */
	private ArrayList splitByLength(String _par_str, FontMetrics _fm, int _width) {
		ArrayList al_return = new ArrayList(); //
		int li_start_pos = 0; //
		int li_length = _par_str.length(); //���ж��ٸ���.
		for (int i = 0; i <= li_length; i++) { //�������е���....
			String str_item = _par_str.substring(li_start_pos, i); //
			int li_wordlength = SwingUtilities.computeStringWidth(_fm, str_item); //�ַ����Ŀ��...
			if (li_wordlength >= (_width - _fm.getAscent())) { //����������,������б�..
				al_return.add(str_item); //
				li_start_pos = i; //
			}
		}

		if (li_start_pos < li_length) {
			al_return.add(_par_str.substring(li_start_pos, li_length)); //
		}
		return al_return;
	}

	/**
	 * �ָ��ַ���
	 * @param _str
	 * @param _separator
	 * @return
	 */
	private ArrayList splitStr(String _str, String _separator) {
		ArrayList al_return = new ArrayList();
		StringTokenizer st = new StringTokenizer(_str, _separator);
		while (st.hasMoreTokens()) {
			al_return.add(st.nextToken());
		}
		return al_return;
	}

}
