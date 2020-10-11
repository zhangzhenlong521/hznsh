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
 * 可以折行的JLabel的UI,非常NB..
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
			//Rectangle rect = c.getBounds(); //一定要拿显示区域!!效果才好!!!
				drawutil.draw(directType, g, new Rectangle(0, 0, c.getWidth(), c.getHeight()), c.getBackground(), Color.WHITE); //
			//System.out.println("表头渐变了!" + rect.getX() + "," + rect.getY() + "," + rect.getWidth() + "," + rect.getHeight()); //
		}


		if (text == null || text.equals("") || text.toLowerCase().startsWith("<html>")) { //如果是html开头,则使用原来的绘图法!!
			super.paint(g, c); //
			return;
		}

		Icon icon = (label.isEnabled()) ? label.getIcon() : label.getDisabledIcon();
		int iconWidth = 0; //图标宽度
		int iconHeight = 0; //图标高度
		if (icon != null) {
			iconWidth = icon.getIconWidth(); //
			iconHeight = icon.getIconHeight(); //
		}

		HashMap textColorMap = (HashMap) label.getClientProperty("TextItemColor"); //指定的分隔的字体颜色
		FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(label.getFont()); //sun.swing.SwingUtilities2.getFontMetrics(label, g);
		Insets insets = c.getInsets(mypaintViewInsets); //取得边框
		int li_labelwidth = (int) c.getSize().getWidth(); //控件宽度..

		int li_x = insets.left;
		int li_y = insets.top + fm.getAscent();

		int li_width = c.getWidth() - (insets.left + insets.right);
		int li_height = c.getHeight() - (insets.top + insets.bottom);
		g2.setFont(label.getFont()); //字体!!!
		g2.setColor(label.getForeground()); //前景颜色!!!
		String[] str_aa = getSplitArray(text, fm, li_labelwidth); //将字符串折行,按回车或控件宽度进行自动折行!!!
		if (str_aa.length > 1) { //如果有换行
			int li_starty = getStartY(label.getVerticalAlignment(), li_y, li_height, str_aa.length * fm.getAscent(), fm.getAscent()); //
			for (int i = 0; i < str_aa.length; i++) { //遍历各行数据
				int li_startx = getStartX(label.getHorizontalAlignment(), li_x, li_width, SwingUtilities.computeStringWidth(fm, str_aa[i])); // SwingUtilities.computeStringWidth(Toolkit.getDefaultToolkit().getFontMetrics(Font),"sdssdd");   SwingUtilities.computeStringWidth(fm, str_aa[i]);  可以用来准确计算字的宽度,我以前的算法有问题!!!
				if (i == 0) { //第一行时要画图片
					if (icon != null) {
						icon.paintIcon(c, g, li_startx - iconWidth, li_starty - iconHeight); //画图片
					}
				}
				if (textColorMap == null) { //如果没定义颜色!!
					g2.setColor(label.getForeground()); //前景颜色!!!
					drawString(label,g2,str_aa[i],li_startx,(li_starty + (i * fm.getAscent()) + (0 * 2)));//按道理0 * 2是i*2,但由于在表头中要紧凑,但在表格参数中要松散,所以有矛盾!暂时不改,以后想好后统一改!(xch)
				} else {
					drawStrBysSplitItem(g2, str_aa[i], li_startx, (li_starty + (i * fm.getAscent()) + (0 * 2)), textColorMap, label.getForeground(), fm,label); //一段段画字!按道理0 * 2是i*2,但由于在表头中要紧凑,但在表格参数中要松散,所以有矛盾!暂时不改!以后想好后统一改(xch)
				}
			}
		} else { //
			if (textColorMap == null) {
				int li_startx = getStartX(label.getHorizontalAlignment(), li_x, li_width, SwingUtilities.computeStringWidth(fm, text) + iconWidth); //
				int li_starty = getStartY(label.getVerticalAlignment(), li_y, li_height, fm.getAscent(), fm.getAscent()); //
				if (icon != null) {
					icon.paintIcon(c, g, li_startx,(label.getHeight() - iconHeight)/2);
				}
				g2.setColor(label.getForeground()); //前景颜色!!!
				drawString(label,g2,text,li_startx + iconWidth,li_starty);//画字!!!!如果指定了颜色ItemMap则还要分段画!!很麻烦!!
			} else {
				int li_startx = getStartX(label.getHorizontalAlignment(), li_x, li_width, SwingUtilities.computeStringWidth(fm, text) + iconWidth); //
				int li_starty = getStartY(label.getVerticalAlignment(), li_y, li_height, fm.getAscent(), fm.getAscent()); //
				if (icon != null) {
					icon.paintIcon(c, g, li_startx, li_starty - iconHeight); //
				}
				drawStrBysSplitItem(g2, text, li_startx + iconWidth, li_starty, textColorMap, label.getForeground(), fm,label); //一段段画字
			}
		}
	}

	//画一个字符串根据指定的分隔颜色!!
	private void drawStrBysSplitItem(Graphics2D g2, String _text, int _startX, int _startY, HashMap textColorMap, Color _defaultForeGround, FontMetrics fm,JLabel label) {
		String[] str_items = (String[]) textColorMap.keySet().toArray(new String[0]); //先看有多少定义
		String[] str_splits = splitTextByItems(_text, str_items); //彻底分隔!
		for (int i = 0; i < str_splits.length; i++) { //画段画
			if (textColorMap.containsKey(str_splits[i].toLowerCase())) { //如果定义了颜色
				g2.setColor((Color) textColorMap.get(str_splits[i].toLowerCase())); //
			} else {
				g2.setColor(_defaultForeGround); //否则仍用默认的前景颜色!!!
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
	
	//以前系统在win7环境下设置的字体不起作用，故进行修改【李春娟/2012-04-09】
	private void drawString(JLabel label,Graphics2D g2,String _text, int _startX, int _startY) {
		SwingUtilities2.drawString(label, g2, _text,_startX, _startY);
	}

	//分隔一个字符串
	private String[] splitTextByItems(String _text, String[] _splitItems) {
		String[] str_initText = new String[] { _text }; //
		for (int i = 0; i < _splitItems.length; i++) { //第一次是人民,拆下,第二次是国,拆下!!
			ArrayList al_temp = new ArrayList(); //
			for (int j = 0; j < str_initText.length; j++) {
				int li_pos = str_initText[j].toLowerCase().indexOf(_splitItems[i].toLowerCase()); //
				if (li_pos < 0) { //如果没有,则直接送入,提高性能
					al_temp.add(str_initText[j]); //
				} else {
					String str_remain = str_initText[j]; //
					while (li_pos >= 0) {
						if (li_pos == 0) {
							al_temp.add(str_remain.substring(0, _splitItems[i].length())); //如果是第一个,则先直接加入
						} else {
							al_temp.add(str_remain.substring(0, li_pos)); ////先加入前面的
							al_temp.add(str_remain.substring(li_pos, li_pos + _splitItems[i].length()));
						}
						str_remain = str_remain.substring(li_pos + _splitItems[i].length(), str_remain.length()); //截下
						li_pos = str_remain.toLowerCase().indexOf(_splitItems[i].toLowerCase()); //重新赋值
					}
					if (!str_remain.equals("")) {
						al_temp.add(str_remain); //
					}
				}
			}
			str_initText = (String[]) al_temp.toArray(new String[0]); //重新赋值!!
		}
		return str_initText; //
	}

	/**
	 * 取得每行的X的坐标
	 * @param _aligntype
	 * @param li_x
	 * @param li_width
	 * @param _textlength
	 * @return
	 */
	private int getStartX(int _aligntype, int li_x, int li_width, int _textlength) {
		if (_aligntype == SwingConstants.LEFT) { //居左
			return li_x;
		} else if (_aligntype == SwingConstants.CENTER) { //如果居中
			int _tmpx = (li_width - _textlength) / 2;
			if (_tmpx < li_x) {
				return li_x;
			} else {
				return _tmpx;
			}
		} else if (_aligntype == SwingConstants.RIGHT) { //居右
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
	 * 取得所有数据的所有Y位置
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
	 * 对一个字符串进行分割...
	 * @return
	 */
	private String[] getSplitArray(String _str, FontMetrics _fm, int _width) {
		ArrayList al_return = new ArrayList(); // 
		ArrayList al_strs_1 = splitStr(_str, "\r"); //
		for (int i = 0; i < al_strs_1.size(); i++) {
			String str_item_1 = (String) al_strs_1.get(i); //
			ArrayList al_strs_2 = splitStr(str_item_1, "\n"); //
			for (int j = 0; j < al_strs_2.size(); j++) { //
				String str_item_2 = (String) al_strs_2.get(j); //得到各项...
				ArrayList al_strs_3 = splitByLength(str_item_2, _fm, _width); //对某个字符串根据长度进行分割
				for (int k = 0; k < al_strs_3.size(); k++) {
					al_return.add(al_strs_3.get(k)); //
				}
			}
		}

		return (String[]) al_return.toArray(new String[0]); //
	}

	/**
	 * 对某个字符串根据长度进行分割..
	 * @param str_item_2
	 * @param _fm
	 * @param _width
	 * @return
	 */
	private ArrayList splitByLength(String _par_str, FontMetrics _fm, int _width) {
		ArrayList al_return = new ArrayList(); //
		int li_start_pos = 0; //
		int li_length = _par_str.length(); //看有多少个字.
		for (int i = 0; i <= li_length; i++) { //遍历所有的字....
			String str_item = _par_str.substring(li_start_pos, i); //
			int li_wordlength = SwingUtilities.computeStringWidth(_fm, str_item); //字符串的宽度...
			if (li_wordlength >= (_width - _fm.getAscent())) { //如果超过宽度,则加入列表..
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
	 * 分割字符串
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
