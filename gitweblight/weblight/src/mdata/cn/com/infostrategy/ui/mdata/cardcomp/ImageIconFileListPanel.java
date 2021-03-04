/**************************************************************************
 * $RCSfile: ImageIconFileListPanel.java,v $  $Revision: 1.6 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.ImageIconFactory;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTLabel;

/**
 * 预览所有图标的面板...
 * @author xch
 *
 */
public class ImageIconFileListPanel extends JPanel implements ActionListener, KeyListener {

	private static final long serialVersionUID = -4494833836273195514L;

	private JLabel jlb_image = null;
	private JPanel jpn_left = null;
	private JLabel jlb_title = null;
	private JLabel search_l = null;
	private WLTLabel help = null;
	private JTextField search_f = null;
	private String str_init_image = null;
	private JScrollPane jsp_left = null;
	private JLabel[] labels = null;
	private String str_selectedicon = null;
	private JButton jb_n, jb_p = null;
	private int currenindex = 0;

	public ImageIconFileListPanel(String _init_image) {
		str_init_image = _init_image;
		str_selectedicon = _init_image;
		init();
	}

	/**
	 * 初始化页面....
	 */
	private void init() {
		jlb_title = new JLabel("图标预览:");
		jlb_title.setPreferredSize(new Dimension(80, 20));
		jlb_title.setHorizontalAlignment(JLabel.RIGHT);
		jlb_image = new JLabel("");
		jlb_image.setPreferredSize(new Dimension(300, 40));
		jlb_image.setHorizontalAlignment(JLabel.LEFT);
		jlb_image.setVerticalAlignment(JLabel.CENTER);
		JPanel search_P = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
		search_l = new JLabel("图标名称:");
		search_f = new JTextField(str_init_image);
		search_f.setPreferredSize(new Dimension(100, 20));
		search_f.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, LookAndFeel.compBorderLineColor));
		search_f.addKeyListener(this);
		JPanel panel_up_down = new JPanel(new BorderLayout(0, 0));
		panel_up_down.setOpaque(false); //
		help = new WLTLabel("alt+↑/↓可快速查询");
		help.setPreferredSize(new Dimension(160, 20));
		help.addStrItemColor("alt+↑/↓可快速查询", Color.BLUE);
		jb_p = new JButton();
		jb_n = new JButton();
		jb_p.addActionListener(this);
		jb_n.addActionListener(this);
		jb_p.setMnemonic(KeyEvent.VK_UP);
		jb_n.setMnemonic(KeyEvent.VK_DOWN);
		jb_p.setToolTipText("继续搜索上一个");
		jb_n.setToolTipText("继续搜索下一个");
		jb_p.setIcon(ImageIconFactory.getUpEntityArrowIcon(new Color(120, 120, 120)));
		jb_n.setIcon(ImageIconFactory.getDownEntityArrowIcon(new Color(120, 120, 120)));
		jb_p.setPreferredSize(new Dimension(16, 10));
		jb_n.setPreferredSize(new Dimension(16, 10));
		jb_n.setBackground(LookAndFeel.btnbgcolor);
		jb_p.setBackground(LookAndFeel.btnbgcolor);
		jb_n.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY));
		jb_p.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.GRAY));
		panel_up_down.add(jb_p, BorderLayout.NORTH);
		panel_up_down.add(jb_n, BorderLayout.SOUTH);
		panel_up_down.setPreferredSize(new Dimension(15, 20));
		search_P.add(search_l);
		search_P.add(search_f);
		search_P.add(panel_up_down);
		search_P.add(help);
		JPanel jpn_north = new JPanel();
		jpn_north.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 15));
		jpn_north.add(search_P);
		jpn_north.add(jlb_title);
		jpn_north.add(jlb_image);
		jpn_left = new JPanel();
		jpn_left.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
		jsp_left = new JScrollPane(jpn_left);
		jsp_left.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jsp_left.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		this.setLayout(new BorderLayout()); //
		this.add(jsp_left, BorderLayout.CENTER); //
		this.add(jpn_north, BorderLayout.NORTH); //
		addElement();
	}

	/**
	 * 加入每一个小图片
	 */
	private void addElement() {
		String[] str_images = null;
		JLabel jlb_temp = null;
		try {
			str_images = ClientEnvironment.getInstance().getImagesNames(); //
			if (str_images == null) {
				str_images = UIUtil.getImageFileNames();//
				ClientEnvironment.getInstance().setImagesNames(str_images); //
			}
			ImageIcon[] icons = new ImageIcon[str_images.length]; //
			labels = new JLabel[str_images.length]; //
			int li_count = icons.length;
			int h = (li_count / 10 + 1) * 40 > 400 ? (li_count / 10) * 40 : 400;
			jpn_left.setPreferredSize(new Dimension(380, h));
			for (int i = 0; i < str_images.length; i++) {
				icons[i] = UIUtil.getImage(str_images[i]); //
				addImageItem(i, icons[i], str_images[i]); //
				if (str_images[i].equals(str_init_image)) {
					jlb_temp = labels[i];
					jlb_image.setIcon(jlb_temp.getIcon());
					currenindex = i;
				}
			}
			jpn_left.updateUI(); //
			onSelected(jlb_temp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addImageItem(final int _index, ImageIcon _image, String _file) {
		if (_image == null) {
			labels[_index] = new JLabel("×");
			labels[_index].setForeground(Color.RED);
		} else {
			labels[_index] = new JLabel(_image);
		}
		labels[_index].setToolTipText(_file);
		labels[_index].putClientProperty("bindfilename", _file); //
		labels[_index].setPreferredSize(new Dimension(20, 20));

		labels[_index].addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				JLabel label_sel = (JLabel) e.getSource(); //
				str_selectedicon = label_sel.getToolTipText();
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1) {
					currenindex = _index;
					onSelected(label_sel); //
				} else if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() >= 2) {
					onEnsure(e); //
				}
			}
		});
		jpn_left.add(labels[_index]); //
	}

	/**
	 * 实现选中某个JLabel
	 * 
	 * @param _label
	 */
	private void onSelected(JLabel _label) {
		for (int i = 0; i < labels.length; i++) {
			labels[i].setBorder(BorderFactory.createEmptyBorder());
		}
		if (_label != null) {
			jlb_image.setIcon(_label.getIcon()); //
			jlb_image.setText("" + _label.getClientProperty("bindfilename")); //
			_label.setBorder(BorderFactory.createLineBorder(Color.red, 3)); //
		}
	}

	/**
	 * 实现双击，选中图标，并关闭Dialog
	 * 
	 * @param e
	 */
	private void onEnsure(MouseEvent e) {
		Object obj = this.getParent().getParent().getParent().getParent();
		if (obj instanceof RefDialog_Picture) {
			((RefDialog_Picture) obj).onConfirm();
		}
	}

	public String getSelectedIcon() {
		return str_selectedicon;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == jb_n) {
			next();
		} else if (e.getSource() == jb_p) {
			priview();
		}
	}

	public void next() {
		if (search_f.getText() == null || search_f.getText().trim().equals("")) {
			return;
		}
		if (labels != null && labels.length > 0) {
			int j = currenindex;
			for (int i = currenindex + 1; i < labels.length; i++) {
				if (("" + labels[i].getClientProperty("bindfilename")).indexOf(search_f.getText()) >= 0) {
					currenindex = i;
					str_selectedicon = labels[i].getToolTipText();
					onSelected(labels[i]);
					jsp_left.getVerticalScrollBar().setValue((int) labels[i].getBounds().getY());
					return;
				}
			}
			currenindex = j;
			str_selectedicon = labels[j].getToolTipText();
			onSelected(labels[j]);
			jsp_left.getVerticalScrollBar().setValue((int) labels[j].getBounds().getY());
		}
	}

	public void priview() {
		if (search_f.getText() == null || search_f.getText().trim().equals("")) {
			return;
		}
		if (labels != null && labels.length > 0) {
			int j = currenindex;
			for (int i = currenindex - 1; i >= 0; i--) {
				if (("" + labels[i].getClientProperty("bindfilename")).indexOf(search_f.getText()) >= 0) {
					currenindex = i;
					str_selectedicon = labels[i].getToolTipText();
					onSelected(labels[i]);
					jsp_left.getVerticalScrollBar().setValue((int) labels[i].getBounds().getY());
					return;
				}
			}
			currenindex = j;
			str_selectedicon = labels[j].getToolTipText();
			onSelected(labels[j]);
			jsp_left.getVerticalScrollBar().setValue((int) labels[j].getBounds().getY());
		}
	}

	public void quicksearch() {
		if (search_f.getText() == null || search_f.getText().trim().equals("")) {
			return;
		}
		if (labels != null && labels.length > 0) {
			for (int i = 0; i < labels.length; i++) {
				if (("" + labels[i].getClientProperty("bindfilename")).indexOf(search_f.getText()) >= 0) {
					currenindex = i;
					str_selectedicon = labels[i].getToolTipText();
					onSelected(labels[i]);
					jsp_left.getVerticalScrollBar().setValue((int) labels[i].getBounds().getY());
					return;
				}
			}
		}
	}

	public void keyPressed(KeyEvent keyevent) {

	}

	public void keyReleased(KeyEvent keyevent) {
		quicksearch();
	}

	public void keyTyped(KeyEvent keyevent) {

	}
}
