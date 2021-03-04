/**************************************************************************
 * $RCSfile: TextAreaDialog.java,v $  $Revision: 1.24 $  $Date: 2013/02/28 06:14:41 $
 **************************************************************************/
package cn.com.infostrategy.ui.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.mdata.VFlowLayoutPanel;

public class TextAreaDialog extends BillDialog implements ActionListener, KeyListener {

	private static final long serialVersionUID = 5540334144291572263L;

	private Object obj_content = null;
	private String str_content = null;
	private WLTTextArea jta_context = new WLTTextArea();
	private Color bgColor = null; //
	private String str_imgname = "info.gif"; //图片名称..
	private TBUtil tbUtil = null; //
	private boolean isHorizontalScrollable = false; //
	private int closeType = -1;

	private WLTButton btn_adfont, btn_delfont; //字体放大缩小 【杨科/2012-08-29】	

	private int sysOption = -1; //系统默认是0,即就是[确定]!
	private String[] str_custOptions = new String[] { "确定" }; //

	private int initFocusBtnValue = -1; //
	private JButton btn_focus = null; // 默认光标停留的按钮,也就是所谓的默认值!!!
	private int returnValue = -1; //以前如果点击窗口右上角关闭窗口和点击确定都返回0，故改为-1

	private int li_subfix_width = 25;
	private int li_subfix_height = 15; //
	private boolean isCanCopyText = true; //是否可以拷贝文本内容? 有的地方是不允许拷贝文字的,比如在线快速帮助，因为这是关键思想,怕被竞争对手快速复制！【xch/2012-02-28】
	private int liveSec = 0;
	private String defaultText = null;
	private List allbtn = new ArrayList();
	private Timer antotimer = null;

	public TextAreaDialog(Container _parent, String _title, Object _context) {
		this(_parent, _title, null, _context);
	}

	public TextAreaDialog(Container _parent, String _title, String _imgname, Object _context) {
		this(_parent, _title, _imgname, _context, 0, null);
	}

	public TextAreaDialog(Container _parent, String _title, String _imgname, Object _context, int _liveSec, String _defaultText) {
		super(_parent, _title); //
		if (_imgname != null) {
			str_imgname = _imgname;
		}
		this.liveSec = _liveSec;
		this.defaultText = _defaultText;
		this.obj_content = _context; //
		this.str_content = _context.toString();
		int li_thiswidth = getTextWidth() + li_subfix_width; //
		int li_thisheight = getTextHeight() + li_subfix_height; //
		this.setSize(li_thiswidth, li_thisheight); //设置大小
		this.setResizable(true); //
		locationToCenterPosition();
		initialize();
	}

	/**
	 * 系统设置!!
	 * @param _parent
	 * @param _title
	 * @param _imgname
	 * @param _context
	 * @param _sysOption
	 */
	public TextAreaDialog(Container _parent, String _title, String _imgname, Object _context, int _sysOption) {
		this(_parent, _title, _imgname, _context, _sysOption, 0, null);
	}

	public TextAreaDialog(Container _parent, String _title, String _imgname, Object _context, int _sysOption, int _liveSec, String _defaultText) {
		super(_parent, _title); //
		if (_imgname != null) {
			str_imgname = _imgname;
		}
		this.obj_content = _context; //
		this.str_content = _context.toString();
		this.sysOption = _sysOption; //
		int li_thiswidth = getTextWidth() + li_subfix_width; //
		int li_thisheight = getTextHeight() + li_subfix_height; //
		this.setSize(li_thiswidth, li_thisheight); //设置大小
		this.setResizable(true); //
		locationToCenterPosition();
		initialize();
	}

	public TextAreaDialog(Container _parent, String _title, String _imgname, Object _context, int _sysOption, int _width, int _height) {
		this(_parent, _title, _imgname, _context, _sysOption, _width, _height, 0, null);
	}

	public TextAreaDialog(Container _parent, String _title, String _imgname, Object _context, int _sysOption, int _width, int _height, int _liveSec, String _defaultText) {
		super(_parent, _title); //
		if (_imgname != null) {
			str_imgname = _imgname;
		}
		this.liveSec = _liveSec;
		this.defaultText = _defaultText;
		this.obj_content = _context; //
		this.str_content = _context.toString();
		this.sysOption = _sysOption; //
		this.setSize(_width, _height); //设置大小
		this.setResizable(true); //
		locationToCenterPosition();
		initialize();
	}

	public TextAreaDialog(Container _parent, String _title, String _imgname, Object _context, String[] _custOptions, int _initValue, int _width, int _height) {
		this(_parent, _title, _imgname, _context, _custOptions, _initValue, _width, _height, true, true); //
	}

	/**
	 * 自定义的设置!!!
	 * @param _parent
	 * @param _title
	 * @param _imgname
	 * @param _context
	 * @param _custOptions
	 */
	public TextAreaDialog(Container _parent, String _title, String _imgname, Object _context, String[] _custOptions, int _initValue, int _width, int _height, boolean _isCanCopyText, boolean _isResizable) {
		this(_parent, _title, _imgname, _context, _custOptions, _initValue, _width, _height, _isCanCopyText, _isResizable, 0, null);
	}

	public TextAreaDialog(Container _parent, String _title, String _imgname, Object _context, String[] _custOptions, int _initValue, int _width, int _height, boolean _isCanCopyText, boolean _isResizable, int _liveSec, String _defaultText) {
		super(_parent, _title); //
		if (_imgname != null) {
			str_imgname = _imgname;
		}
		this.liveSec = _liveSec;
		this.defaultText = _defaultText;
		this.obj_content = _context; //
		this.str_content = _context.toString(); //
		this.str_custOptions = _custOptions; //
		this.initFocusBtnValue = _initValue; //
		int li_thiswidth = getTextWidth() + li_subfix_width; //
		int li_thisheight = getTextHeight() + li_subfix_height; //
		if (_width > 0) {
			li_thiswidth = _width;
		}
		if (_height > 0) {
			li_thisheight = _height;
		}
		this.setSize(li_thiswidth, li_thisheight); //设置大小
		this.isCanCopyText = _isCanCopyText; //
		this.setResizable(_isResizable); //是否可改变窗口大小???
		locationToCenterPosition();
		initialize();
	}

	/**
	 * 打开一个窗口,指定大小
	 * @param _parent
	 * @param _title
	 * @param _context
	 * @param _width
	 * @param _height
	 */
	public TextAreaDialog(Container _parent, String _title, Object _context, int _width, int _height) {
		this(_parent, _title, _context, _width, _height, null);
	}

	public TextAreaDialog(Container _parent, String _title, Object _context, int _width, int _height, Color _bgcolor) {
		this(_parent, _title, _context, _width, _height, null, false, true); //
	}

	public TextAreaDialog(Container _parent, String _title, Object _context, int _width, int _height, Color _bgcolor, boolean _isHorizontalScrollable) {
		this(_parent, _title, _context, _width, _height, _bgcolor, _isHorizontalScrollable, true); //
	}

	public TextAreaDialog(Container _parent, String _title, Object _context, int _width, int _height, Color _bgcolor, boolean _isHorizontalScrollable, boolean _icCanCopyText) {
		super(_parent, _title); //
		this.obj_content = _context; //
		this.str_content = _context.toString(); //
		this.setSize(_width, _height); //设置大小
		this.setResizable(true); //
		this.bgColor = _bgcolor;
		this.isHorizontalScrollable = _isHorizontalScrollable; //水平滚动
		this.isCanCopyText = _icCanCopyText; //
		locationToCenterPosition();
		initialize(); //
	}

	/**
	 * 取得宽度
	 * @return
	 */
	private int getTextWidth() {
		if (str_content == null) {
			return 275;
		}
		String[] str_rowtexts = this.str_content.split("\n"); //
		int li_maxlength = 25; //
		for (int i = 0; i < str_rowtexts.length; i++) {
			int li_words = getTBUtil().getStrWidth(LookAndFeel.font, str_rowtexts[i]) + 70; //
			//System.out.println("字数["+li_words+"]");  //
			if (li_words > li_maxlength) {
				li_maxlength = li_words; //找出最多一行字的个数
			}
		}

		int li_width = li_maxlength * 1; //

		if (li_width < 275) {
			li_width = 275;
		}

		if (li_width > 900) {
			li_width = 900;
		}
		return li_width;
	}

	/**
	 * 取得高度
	 * @return
	 */
	private int getTextHeight() {
		if (str_content == null) {
			return 120;
		}

		int li_width = getTextWidth(); //
		String[] str_rowtexts = this.str_content.split("\n"); //
		int li_rows = 0;
		for (int i = 0; i < str_rowtexts.length; i++) {
			int li_words = getTBUtil().getStrWidth(LookAndFeel.font, str_rowtexts[i]); //
			li_rows = li_rows + ((li_words / li_width) + 1); //
		}
		int li_height = li_rows * 22 + 70; //

		if (li_height < 120) {
			li_height = 120;
		}

		if (li_height > 600) {
			li_height = 600;
		}
		return li_height; //
	}

	private void initialize() {
		this.getContentPane().setLayout(new BorderLayout());
		if (obj_content instanceof java.awt.Container) { //如果是个面板,则直接加入!!!以前只能处理字符串,结果一遇到是一个面板时,只能自己搞个BillDialog【xch/2012-06-08】
			this.getContentPane().add((java.awt.Container) obj_content, BorderLayout.CENTER); //
			this.getContentPane().add(getButtonPanel(true), BorderLayout.SOUTH); //
		} else { //如果是其他内容,则将toString()的字符串输出!!!
			jta_context.setFocusable(true); //
			jta_context.setOpaque(false); //透明的!!
			jta_context.setFont(LookAndFeel.font);
			//jta_context.setContentType("text/plain;");
			jta_context.setText(str_content); //
			jta_context.select(0, 0); //
			jta_context.setEditable(false);
			jta_context.setCanCopyText(isCanCopyText); //
			//jta_context.setLineWrap(true);
			jta_context.setBorder(BorderFactory.createEmptyBorder()); //

			JScrollPane jsp_text = new JScrollPane(jta_context); //
			jsp_text.setFocusable(false); //
			jsp_text.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); //
			jsp_text.setOpaque(false); //透明!
			jsp_text.getViewport().setOpaque(false); //透明!

			if (isHorizontalScrollable) {
				jta_context.setLineWrap(false); //自动换行!!!
				jsp_text.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); //水平滚动!!
			} else {
				jta_context.setLineWrap(true); //自动换行!!!
				jsp_text.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); //水平没有
			}

			if (this.bgColor != null) { //
				jta_context.setOpaque(true); //
				jta_context.setBackground(bgColor);
			} else { //如果没指定颜色,则希望像平板一样
				jta_context.setBackground(LookAndFeel.systembgcolor); //
				jsp_text.setBorder(BorderFactory.createEmptyBorder()); //
			}

			JPanel panel_content = new JPanel(new BorderLayout()); //
			panel_content.setBackground(LookAndFeel.defaultShadeColor1); //
			panel_content.setUI(new WLTPanelUI(WLTPanel.INCLINE_NW_TO_SE)); //
			panel_content.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10)); //
			panel_content.add(jsp_text, BorderLayout.CENTER); //加入文本框

			panel_content.add(getButtonPanel(false), BorderLayout.SOUTH); //加入按钮

			JPanel panel_img = null;
			if (this.bgColor != null) {
				panel_img = new JPanel(new FlowLayout(FlowLayout.LEFT));
				panel_img.setOpaque(false); //透明!
				panel_img.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
				JLabel imgLabel = new JLabel(new ImageIcon(getTBUtil().getImageScale(UIUtil.getImage(str_imgname).getImage(), 36, 36)));
				imgLabel.setFocusable(false);
				imgLabel.setOpaque(false);

				btn_adfont = new WLTButton("字体+");
				btn_delfont = new WLTButton("字体-");

				btn_adfont.addActionListener(this);
				btn_delfont.addActionListener(this);

				panel_img.add(new VFlowLayoutPanel(new JComponent[] { imgLabel, btn_adfont, btn_delfont }));
			} else { //如果没指定颜色,则希望像平板一样
				panel_img = new JPanel(new BorderLayout());
				panel_img.setOpaque(false); //透明!
				panel_img.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
				JLabel imgLabel = new JLabel(new ImageIcon(getTBUtil().getImageScale(UIUtil.getImage(str_imgname).getImage(), 36, 36)));
				imgLabel.setFocusable(false);
				imgLabel.setOpaque(false);
				panel_img.add(imgLabel, BorderLayout.NORTH);
			}

			panel_content.add(panel_img, BorderLayout.WEST); //加入图片!
			this.getContentPane().add(panel_content, BorderLayout.CENTER); //
		}
	}

	//计算出下方的按钮!!!
	private JPanel getButtonPanel(boolean _isOpaque) {
		JPanel jpn_btn = null; //
		if (_isOpaque) {
			jpn_btn = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.CENTER));
		} else {
			jpn_btn = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 10)); //
			jpn_btn.setOpaque(false); //透明!
		}

		if (sysOption >= 0) { //如果是系统的参数,则根据参数重新生成按钮!
			if (sysOption == JOptionPane.YES_NO_OPTION) { //如果是0
				str_custOptions = new String[] { " 是(Y) ", " 否(N) " }; //
			} else if (sysOption == JOptionPane.YES_NO_CANCEL_OPTION) { //如果是1
				str_custOptions = new String[] { " 是(Y) ", " 否(N) ", "取消" }; //
			} else if (sysOption == JOptionPane.OK_CANCEL_OPTION) { //如果是2
				str_custOptions = new String[] { "确定", "取消" }; //
			}
		}

		//循环加入各个按钮!
		for (int i = 0; i < str_custOptions.length; i++) {
			JButton btn_item = null; //
			String str_btntext = str_custOptions[i]; //按钮名称
			if (str_btntext.startsWith("[")) { //如果有图片
				String str_imgName = str_btntext.substring(1, str_btntext.indexOf("]")); //
				String str_realText = str_btntext.substring(str_btntext.indexOf("]") + 1, str_btntext.length()); //实际名称
				String str_tooltip = null; //提示!!
				if (str_realText.endsWith("}")) { //如果是以“}”结束的,则最后的{}中的是提示
					str_tooltip = str_realText.substring(str_realText.lastIndexOf("{") + 1, str_realText.lastIndexOf("}")); //括号中的内容!
					str_realText = str_realText.substring(0, str_realText.lastIndexOf("{")); //
				}
				btn_item = new WLTButton(str_realText, UIUtil.getImage(str_imgName)); //
				if (str_tooltip != null) {
					btn_item.setToolTipText(str_tooltip); //
				}
			} else {
				String str_realText = str_custOptions[i]; //
				String str_tooltip = null; //提示!!
				if (str_realText.endsWith("}")) { //如果是以“}”结束的,则最后的{}中的是提示
					str_tooltip = str_realText.substring(str_realText.lastIndexOf("{") + 1, str_realText.lastIndexOf("}")); //括号中的内容!
					str_realText = str_realText.substring(0, str_realText.lastIndexOf("{")); //
				}
				btn_item = new WLTButton(str_realText); //如果有图片文字说明!
				if (str_tooltip != null) {
					btn_item.setToolTipText(str_tooltip); //
				}
			}
			btn_item.setFocusable(true); //
			btn_item.putClientProperty("option_pos", new Integer(i)); //加入的位置!!
			btn_item.addActionListener(this); //
			btn_item.addKeyListener(this); //
			if (initFocusBtnValue > -1 && initFocusBtnValue == i) {//以前这里initFocusBtnValue>0，则第一个按钮无法获得光标，故改为initFocusBtnValue>-1【李春娟/2012-06-06】
				btn_focus = btn_item; //指定光标停留的按钮!!!
			}
			if (str_custOptions.length == 1) {
				btn_focus = btn_item; //如果就一个按钮，默认获得焦点！[郝明2012-03-21]
			}
			btn_item.putClientProperty("LARGE_BUTTON", "Y");
			int textWidth = getTBUtil().getStrWidth(btn_item.getText());
			int icon_width = 0;
			if (btn_item.getIcon() != null) {
				icon_width = btn_item.getIcon().getIconWidth();
			}
			btn_item.setPreferredSize(new Dimension(textWidth < 80 ? 80 + icon_width : textWidth + 30 + icon_width, 28));
			btn_item.putClientProperty("name", btn_item.getText());
			allbtn.add(btn_item);
			jpn_btn.add(btn_item);
		}
		return jpn_btn;
	}

	@Override
	public void initFocusAfterWindowOpened() {
		if (btn_focus != null) {
			btn_focus.requestFocus(); //
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_adfont) {
			addFont();
		} else if (e.getSource() == btn_delfont) {
			deFont();
		} else {
			JButton btn = (JButton) e.getSource(); //取得按钮!!
			onClickedButton(btn); //			
		}
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == e.VK_ENTER) { //如果是回车键!!!
			JButton btn = (JButton) e.getSource(); //取得按钮!!
			onClickedButton(btn); //
		}
	}

	private void addFont() {
		int li_size = jta_context.getFont().getSize();
		int li_newSize = li_size + 3;
		if (li_newSize > 60) {
			li_newSize = 60;
		}
		jta_context.setFont(new Font("宋体", Font.PLAIN, li_newSize));
	}

	private void deFont() {
		int fontsize = jta_context.getFont().getSize();
		int li_newSize = fontsize - 3;
		if (li_newSize < 8) {
			li_newSize = 8;
		}
		jta_context.setFont(new Font("宋体", Font.PLAIN, li_newSize));
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	/**
	 * 点击按钮
	 */
	private void onClickedButton(JButton _btn) {
		if (sysOption >= 0) { //如果是系统配置!!!
			String str_text = _btn.getClientProperty("name").toString().trim(); //取得按钮的文字!!
			if (str_text.equals("是(Y)")) {
				returnValue = JOptionPane.YES_OPTION; //实际是0
			} else if (str_text.equals("否(N)")) {
				returnValue = JOptionPane.NO_OPTION; //实际是1
			} else if (str_text.equals("取消")) {
				returnValue = JOptionPane.CANCEL_OPTION; //实际是2
			} else if (str_text.equals("确定")) {
				returnValue = JOptionPane.OK_OPTION; //实际是0
			}
		} else {
			int li_pos = (Integer) _btn.getClientProperty("option_pos"); //取得按钮的位置!!
			returnValue = li_pos; //默认的或者是自定义的参数!!
		}
		setCloseType(1);

		if (antotimer != null) {
			antotimer.cancel();
			antotimer = null;
		}
		this.dispose(); //
	}

	private void runAutoCloseTimer() {
		antotimer = new Timer();
		antotimer.schedule(new TimerTask() {
			public void run() {
				refreshLabel();
			}
		}, 50, 1000);
	}

	private void refreshLabel() {
		getDefaultBtn().setText(getDefaultBtn().getClientProperty("name") + "(" + liveSec + ")");
		liveSec--;
		if (liveSec < 0 && antotimer != null) {
			antotimer.cancel();
			antotimer = null;
			getDefaultBtn().doClick();
		}
	}

	private JButton getDefaultBtn() {
		if (defaultText != null) {
			for (int i = 0; i < allbtn.size(); i++) {
				if (defaultText.equals(((JButton) allbtn.get(i)).getClientProperty("name"))) {
					return (JButton) allbtn.get(i);
				}
			}
		}
		return (JButton) allbtn.get(0);
	}

	@Override
	public boolean beforeWindowClosed() {
		setCloseType(2); //
		return true; //不让关闭!!
	}

	public int getReturnValue() {
		return returnValue; //
	}

	private TBUtil getTBUtil() {
		if (tbUtil == null) {
			tbUtil = new TBUtil();
		}
		return tbUtil; //
	}

	public JTextArea getJta_context() {
		return jta_context;
	}

	public int getCloseType() {
		return closeType;
	}

	public void setCloseType(int closeType) {
		this.closeType = closeType;
	}

	public void setVisible(boolean flag) {
		if (flag && liveSec > 0) {
			runAutoCloseTimer();
		}
		super.setVisible(flag);
	}

}
