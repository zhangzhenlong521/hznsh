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
	private String str_imgname = "info.gif"; //ͼƬ����..
	private TBUtil tbUtil = null; //
	private boolean isHorizontalScrollable = false; //
	private int closeType = -1;

	private WLTButton btn_adfont, btn_delfont; //����Ŵ���С �����/2012-08-29��	

	private int sysOption = -1; //ϵͳĬ����0,������[ȷ��]!
	private String[] str_custOptions = new String[] { "ȷ��" }; //

	private int initFocusBtnValue = -1; //
	private JButton btn_focus = null; // Ĭ�Ϲ��ͣ���İ�ť,Ҳ������ν��Ĭ��ֵ!!!
	private int returnValue = -1; //��ǰ�������������Ͻǹرմ��ں͵��ȷ��������0���ʸ�Ϊ-1

	private int li_subfix_width = 25;
	private int li_subfix_height = 15; //
	private boolean isCanCopyText = true; //�Ƿ���Կ����ı�����? �еĵط��ǲ����������ֵ�,�������߿��ٰ�������Ϊ���ǹؼ�˼��,�±��������ֿ��ٸ��ƣ���xch/2012-02-28��
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
		this.setSize(li_thiswidth, li_thisheight); //���ô�С
		this.setResizable(true); //
		locationToCenterPosition();
		initialize();
	}

	/**
	 * ϵͳ����!!
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
		this.setSize(li_thiswidth, li_thisheight); //���ô�С
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
		this.setSize(_width, _height); //���ô�С
		this.setResizable(true); //
		locationToCenterPosition();
		initialize();
	}

	public TextAreaDialog(Container _parent, String _title, String _imgname, Object _context, String[] _custOptions, int _initValue, int _width, int _height) {
		this(_parent, _title, _imgname, _context, _custOptions, _initValue, _width, _height, true, true); //
	}

	/**
	 * �Զ��������!!!
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
		this.setSize(li_thiswidth, li_thisheight); //���ô�С
		this.isCanCopyText = _isCanCopyText; //
		this.setResizable(_isResizable); //�Ƿ�ɸı䴰�ڴ�С???
		locationToCenterPosition();
		initialize();
	}

	/**
	 * ��һ������,ָ����С
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
		this.setSize(_width, _height); //���ô�С
		this.setResizable(true); //
		this.bgColor = _bgcolor;
		this.isHorizontalScrollable = _isHorizontalScrollable; //ˮƽ����
		this.isCanCopyText = _icCanCopyText; //
		locationToCenterPosition();
		initialize(); //
	}

	/**
	 * ȡ�ÿ��
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
			//System.out.println("����["+li_words+"]");  //
			if (li_words > li_maxlength) {
				li_maxlength = li_words; //�ҳ����һ���ֵĸ���
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
	 * ȡ�ø߶�
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
		if (obj_content instanceof java.awt.Container) { //����Ǹ����,��ֱ�Ӽ���!!!��ǰֻ�ܴ����ַ���,���һ������һ�����ʱ,ֻ���Լ����BillDialog��xch/2012-06-08��
			this.getContentPane().add((java.awt.Container) obj_content, BorderLayout.CENTER); //
			this.getContentPane().add(getButtonPanel(true), BorderLayout.SOUTH); //
		} else { //�������������,��toString()���ַ������!!!
			jta_context.setFocusable(true); //
			jta_context.setOpaque(false); //͸����!!
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
			jsp_text.setOpaque(false); //͸��!
			jsp_text.getViewport().setOpaque(false); //͸��!

			if (isHorizontalScrollable) {
				jta_context.setLineWrap(false); //�Զ�����!!!
				jsp_text.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); //ˮƽ����!!
			} else {
				jta_context.setLineWrap(true); //�Զ�����!!!
				jsp_text.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); //ˮƽû��
			}

			if (this.bgColor != null) { //
				jta_context.setOpaque(true); //
				jta_context.setBackground(bgColor);
			} else { //���ûָ����ɫ,��ϣ����ƽ��һ��
				jta_context.setBackground(LookAndFeel.systembgcolor); //
				jsp_text.setBorder(BorderFactory.createEmptyBorder()); //
			}

			JPanel panel_content = new JPanel(new BorderLayout()); //
			panel_content.setBackground(LookAndFeel.defaultShadeColor1); //
			panel_content.setUI(new WLTPanelUI(WLTPanel.INCLINE_NW_TO_SE)); //
			panel_content.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10)); //
			panel_content.add(jsp_text, BorderLayout.CENTER); //�����ı���

			panel_content.add(getButtonPanel(false), BorderLayout.SOUTH); //���밴ť

			JPanel panel_img = null;
			if (this.bgColor != null) {
				panel_img = new JPanel(new FlowLayout(FlowLayout.LEFT));
				panel_img.setOpaque(false); //͸��!
				panel_img.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
				JLabel imgLabel = new JLabel(new ImageIcon(getTBUtil().getImageScale(UIUtil.getImage(str_imgname).getImage(), 36, 36)));
				imgLabel.setFocusable(false);
				imgLabel.setOpaque(false);

				btn_adfont = new WLTButton("����+");
				btn_delfont = new WLTButton("����-");

				btn_adfont.addActionListener(this);
				btn_delfont.addActionListener(this);

				panel_img.add(new VFlowLayoutPanel(new JComponent[] { imgLabel, btn_adfont, btn_delfont }));
			} else { //���ûָ����ɫ,��ϣ����ƽ��һ��
				panel_img = new JPanel(new BorderLayout());
				panel_img.setOpaque(false); //͸��!
				panel_img.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
				JLabel imgLabel = new JLabel(new ImageIcon(getTBUtil().getImageScale(UIUtil.getImage(str_imgname).getImage(), 36, 36)));
				imgLabel.setFocusable(false);
				imgLabel.setOpaque(false);
				panel_img.add(imgLabel, BorderLayout.NORTH);
			}

			panel_content.add(panel_img, BorderLayout.WEST); //����ͼƬ!
			this.getContentPane().add(panel_content, BorderLayout.CENTER); //
		}
	}

	//������·��İ�ť!!!
	private JPanel getButtonPanel(boolean _isOpaque) {
		JPanel jpn_btn = null; //
		if (_isOpaque) {
			jpn_btn = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.CENTER));
		} else {
			jpn_btn = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 10)); //
			jpn_btn.setOpaque(false); //͸��!
		}

		if (sysOption >= 0) { //�����ϵͳ�Ĳ���,����ݲ����������ɰ�ť!
			if (sysOption == JOptionPane.YES_NO_OPTION) { //�����0
				str_custOptions = new String[] { " ��(Y) ", " ��(N) " }; //
			} else if (sysOption == JOptionPane.YES_NO_CANCEL_OPTION) { //�����1
				str_custOptions = new String[] { " ��(Y) ", " ��(N) ", "ȡ��" }; //
			} else if (sysOption == JOptionPane.OK_CANCEL_OPTION) { //�����2
				str_custOptions = new String[] { "ȷ��", "ȡ��" }; //
			}
		}

		//ѭ�����������ť!
		for (int i = 0; i < str_custOptions.length; i++) {
			JButton btn_item = null; //
			String str_btntext = str_custOptions[i]; //��ť����
			if (str_btntext.startsWith("[")) { //�����ͼƬ
				String str_imgName = str_btntext.substring(1, str_btntext.indexOf("]")); //
				String str_realText = str_btntext.substring(str_btntext.indexOf("]") + 1, str_btntext.length()); //ʵ������
				String str_tooltip = null; //��ʾ!!
				if (str_realText.endsWith("}")) { //������ԡ�}��������,������{}�е�����ʾ
					str_tooltip = str_realText.substring(str_realText.lastIndexOf("{") + 1, str_realText.lastIndexOf("}")); //�����е�����!
					str_realText = str_realText.substring(0, str_realText.lastIndexOf("{")); //
				}
				btn_item = new WLTButton(str_realText, UIUtil.getImage(str_imgName)); //
				if (str_tooltip != null) {
					btn_item.setToolTipText(str_tooltip); //
				}
			} else {
				String str_realText = str_custOptions[i]; //
				String str_tooltip = null; //��ʾ!!
				if (str_realText.endsWith("}")) { //������ԡ�}��������,������{}�е�����ʾ
					str_tooltip = str_realText.substring(str_realText.lastIndexOf("{") + 1, str_realText.lastIndexOf("}")); //�����е�����!
					str_realText = str_realText.substring(0, str_realText.lastIndexOf("{")); //
				}
				btn_item = new WLTButton(str_realText); //�����ͼƬ����˵��!
				if (str_tooltip != null) {
					btn_item.setToolTipText(str_tooltip); //
				}
			}
			btn_item.setFocusable(true); //
			btn_item.putClientProperty("option_pos", new Integer(i)); //�����λ��!!
			btn_item.addActionListener(this); //
			btn_item.addKeyListener(this); //
			if (initFocusBtnValue > -1 && initFocusBtnValue == i) {//��ǰ����initFocusBtnValue>0�����һ����ť�޷���ù�꣬�ʸ�ΪinitFocusBtnValue>-1�����/2012-06-06��
				btn_focus = btn_item; //ָ�����ͣ���İ�ť!!!
			}
			if (str_custOptions.length == 1) {
				btn_focus = btn_item; //�����һ����ť��Ĭ�ϻ�ý��㣡[����2012-03-21]
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
			JButton btn = (JButton) e.getSource(); //ȡ�ð�ť!!
			onClickedButton(btn); //			
		}
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == e.VK_ENTER) { //����ǻس���!!!
			JButton btn = (JButton) e.getSource(); //ȡ�ð�ť!!
			onClickedButton(btn); //
		}
	}

	private void addFont() {
		int li_size = jta_context.getFont().getSize();
		int li_newSize = li_size + 3;
		if (li_newSize > 60) {
			li_newSize = 60;
		}
		jta_context.setFont(new Font("����", Font.PLAIN, li_newSize));
	}

	private void deFont() {
		int fontsize = jta_context.getFont().getSize();
		int li_newSize = fontsize - 3;
		if (li_newSize < 8) {
			li_newSize = 8;
		}
		jta_context.setFont(new Font("����", Font.PLAIN, li_newSize));
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	/**
	 * �����ť
	 */
	private void onClickedButton(JButton _btn) {
		if (sysOption >= 0) { //�����ϵͳ����!!!
			String str_text = _btn.getClientProperty("name").toString().trim(); //ȡ�ð�ť������!!
			if (str_text.equals("��(Y)")) {
				returnValue = JOptionPane.YES_OPTION; //ʵ����0
			} else if (str_text.equals("��(N)")) {
				returnValue = JOptionPane.NO_OPTION; //ʵ����1
			} else if (str_text.equals("ȡ��")) {
				returnValue = JOptionPane.CANCEL_OPTION; //ʵ����2
			} else if (str_text.equals("ȷ��")) {
				returnValue = JOptionPane.OK_OPTION; //ʵ����0
			}
		} else {
			int li_pos = (Integer) _btn.getClientProperty("option_pos"); //ȡ�ð�ť��λ��!!
			returnValue = li_pos; //Ĭ�ϵĻ������Զ���Ĳ���!!
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
		return true; //���ùر�!!
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
