/**************************************************************************
 * $RCSfile: BillDialog.java,v $  $Revision: 1.29 $  $Date: 2012/09/14 09:17:30 $
 **************************************************************************/
package cn.com.infostrategy.ui.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import cn.com.infostrategy.to.common.WLTLogger;

public class BillDialog extends JDialog {

	public static int CONFIRM = 1;
	public static int CANCEL = 2;

	private static final long serialVersionUID = 1L;
	private java.awt.Container parentContainer = null; //
	private boolean isAddDefaultWindowListener = true; //是否有默认窗口监听事件!!
	protected int closeType = -1; //
	private JButton maxWindowButton = null; //最大化窗口的按钮
	private Point beforeMaxLocation = null; //最大化之前的窗口位置!!
	private Dimension beforeMaxDimension = null; //最大化之前的窗口大小!!

	private WLTButton[] optionBtnPanelBtns = null; //多个配置按钮时的按钮数组!
	private ActionListener confirmAct = null; //
	private int count=0;//zzl 记录是否点击了确定按钮
	
	private transient Logger logger = WLTLogger.getLogger(cn.com.infostrategy.ui.common.BillDialog.class); //

	public BillDialog() throws HeadlessException {
		super();
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //只对该frame有效..
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //默认是关闭,子类应该经常经覆盖之!!!
		this.addWindowListener(new MyWindowAdapter()); //
		locationToCenterPosition(); //
	}

	public BillDialog(Dialog owner, boolean modal) throws HeadlessException {
		super(owner);
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //只对该frame有效..
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		this.addWindowListener(new MyWindowAdapter()); //
		locationToCenterPosition(); //
	}

	public BillDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) throws HeadlessException {
		super(owner, title, modal, gc);
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //只对该frame有效..
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		this.addWindowListener(new MyWindowAdapter()); //
		locationToCenterPosition(); //
	}

	public BillDialog(Dialog owner, String title, boolean modal) throws HeadlessException {
		super(owner, title, modal);
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //只对该frame有效..
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		this.addWindowListener(new MyWindowAdapter()); //
		locationToCenterPosition(); //
	}

	public BillDialog(Dialog owner, String title) throws HeadlessException {
		super(owner, title);
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //只对该frame有效..
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		this.addWindowListener(new MyWindowAdapter()); //
		locationToCenterPosition(); //
	}

	public BillDialog(Dialog owner) throws HeadlessException {
		super(owner);
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //只对该frame有效..
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		this.addWindowListener(new MyWindowAdapter()); //
		locationToCenterPosition(); //
	}

	public BillDialog(Frame owner, boolean modal) throws HeadlessException {
		super(owner, modal);
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //只对该frame有效..
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		this.addWindowListener(new MyWindowAdapter()); //
		locationToCenterPosition(); //
	}

	public BillDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //只对该frame有效..
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		this.addWindowListener(new MyWindowAdapter()); //
		locationToCenterPosition(); //
	}

	public BillDialog(Frame owner, String title, boolean modal) throws HeadlessException {
		super(owner, title, modal);
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //只对该frame有效..
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		this.addWindowListener(new MyWindowAdapter()); //
		locationToCenterPosition(); //
	}

	public BillDialog(Frame owner, String title) throws HeadlessException {
		super(owner, title);
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //只对该frame有效..
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		this.addWindowListener(new MyWindowAdapter()); //
		locationToCenterPosition(); //
	}

	public BillDialog(Frame owner) throws HeadlessException {
		super(owner);
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //只对该frame有效..
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		this.addWindowListener(new MyWindowAdapter()); //
		locationToCenterPosition(); //
	}

	public BillDialog(Container _parent) {
		super(getWindowForComponent(_parent));
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //只对该frame有效..
		this.parentContainer = _parent; //
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		this.addWindowListener(new MyWindowAdapter()); //
		locationToCenterPosition(); //
	}

	public BillDialog(Container _parent, int _width, int li_height) {
		this(_parent, "", _width, li_height);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		this.addWindowListener(new MyWindowAdapter()); //
	}

	public BillDialog(Container _parent, int _width, int li_height, int _x, int _y) {
		super(getWindowForComponent(_parent));
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //只对该frame有效..
		this.parentContainer = _parent; //
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		this.addWindowListener(new MyWindowAdapter()); //
		if (_width <= 0) {
			maxToScreenSize(); //
		} else {
			this.setSize(_width, li_height); //
			this.setLocation(new Double(_x).intValue(), new Double(_y).intValue());
		}
	}

	public BillDialog(Container _parent, String _title) {
		super(getWindowForComponent(_parent), _title);
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //只对该frame有效..
		this.parentContainer = _parent; //
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		this.addWindowListener(new MyWindowAdapter()); //
		this.setSize(550, 350); //默认是600*400的窗口
		locationToCenterPosition(); //
	}

	public BillDialog(Container _parent, String _title, int _width, int li_height) {
		super(getWindowForComponent(_parent), _title);
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //只对该frame有效..
		this.parentContainer = _parent; //
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		this.addWindowListener(new MyWindowAdapter()); //
		this.setSize(_width, li_height); //
		locationToCenterPosition(); //
	}

	/**
	 * 
	 * @param _parent 父亲容器
	 * @param _title  标题
	 * @param _width  宽度
	 * @param li_height  高度
	 * @param _x  x位置
	 * @param _y  y位置
	 */
	public BillDialog(Container _parent, String _title, int _width, int li_height, int _x, int _y) {
		super(getWindowForComponent(_parent), _title);
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //只对该frame有效..
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); //
		this.addWindowListener(new MyWindowAdapter()); //
		if (_width <= 0) {
			maxToScreenSize(); //
		} else {
			this.setSize(_width, li_height); //
			this.setLocation(new Double(_x).intValue(), new Double(_y).intValue());
		}
	}

	/**
	 * 曾有许多有客户提出Dialog不能最大化,而Dialog是不支持窗口最大化的! 所以只能另起一行搞个按钮!!!
	 * 真好利用JmenuBar来实现!! 而不影响以前所有的类!
	 */
	public void setMaxWindowMenuBar() {
		if (maxWindowButton != null) {
			return;
		}
		maxWindowButton = new JButton("窗口最大化", UIUtil.getImage("office_003.gif")); //
		maxWindowButton.setFont(cn.com.infostrategy.ui.common.LookAndFeel.font); //
		maxWindowButton.setMargin(new Insets(0, 0, 0, 0)); //
		maxWindowButton.setPreferredSize(new Dimension(105, 22)); //
		maxWindowButton.setFocusable(false); //
		maxWindowButton.setBackground(new Color(230, 230, 230)); //
		maxWindowButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				maxWindow();

			}
		}); //
		JMenuBar menuBar = new JMenuBar(); //
		menuBar.setBorder(BorderFactory.createEmptyBorder()); //
		menuBar.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0)); //
		menuBar.add(maxWindowButton); //
		this.setJMenuBar(menuBar); //
	}

	/**
	 * 最大化窗口,再点击可以恢复!
	 */
	private void maxWindow() {
		String str_text = maxWindowButton.getText(); //
		if (str_text.equals("窗口最大化")) {
			beforeMaxLocation = this.getLocation(); //记录下原来的位置
			beforeMaxDimension = this.getSize(); //记录原来的大小
			maxToScreenSize(); //窗口最大化!!!
			maxWindowButton.setText("恢复窗口大小"); //
		} else if (str_text.equals("恢复窗口大小")) { //
			this.setLocation(beforeMaxLocation); //
			this.setSize(beforeMaxDimension); //
			maxWindowButton.setText("窗口最大化"); //
		}
	}

	public void addConfirmButtonPanel() {
		addConfirmButtonPanel(0); //
	}

	/**
	 * 我们经常遇到一种需求,就是要弹出一个窗口,下面有【确定】【取消】两个按钮,上面的内容是一些自定义的Swing控件(比如一个文本框),然后确定后,就从控件中取得返回值!
	 * 一般逻辑就是另写一个继承于BillDailog的窗口类,这其实还是比较麻烦的! 还有一种办法是使用JOptionPane.showMessageDialog(),然后在里面放一个面板,但效果也不好!关键是不好控制窗口大小!
	 * 现在增加这个方法后就会方便的多,可以直接调用本方法添加【确定/取消】按钮,然后在BorderLayout.CENTER面板中增加自定义的Swing控件,然后直接返回!
	 * 【xch/2012-06-08】
	 */
	public void addConfirmButtonPanel(int _type) {
		confirmAct = new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				onClickThisConfirmBtn(_event);
			}
		}; //

		optionBtnPanelBtns = new WLTButton[2]; //
		optionBtnPanelBtns[0] = new WLTButton("确定"); //
		optionBtnPanelBtns[1] = new WLTButton("取消"); //
		optionBtnPanelBtns[0].addActionListener(confirmAct); //增加监听!!
		optionBtnPanelBtns[1].addActionListener(confirmAct); //增加监听!!

		JPanel btnPanel = WLTPanel.createDefaultPanel(new FlowLayout()); ////
		if (_type == 0 || _type == 1) {
			btnPanel.add(optionBtnPanelBtns[0]); //
		}
		if (_type == 0 || _type == 2) {
			btnPanel.add(optionBtnPanelBtns[1]); //
		}
		this.getContentPane().add(btnPanel, BorderLayout.SOUTH); //
	}

	/**
	 * 有时需要多个按钮,而不是简单的【确定】【取消】
	 * @param _btnTexts
	 */
	public void addOptionButtonPanel(String[] _btnTexts) {
		confirmAct = new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				onClickThisOptionBtn(_event);
			}
		}; //

		JPanel btnPanel = WLTPanel.createDefaultPanel(new FlowLayout()); ////
		optionBtnPanelBtns = new WLTButton[_btnTexts.length]; //
		for (int i = 0; i < optionBtnPanelBtns.length; i++) {
			optionBtnPanelBtns[i] = new WLTButton(_btnTexts[i]); //
			optionBtnPanelBtns[i].addActionListener(confirmAct); //
			btnPanel.add(optionBtnPanelBtns[i]); //
		}
		this.getContentPane().add(btnPanel, BorderLayout.SOUTH); //
	}

	//点击确定按钮
	private void onClickThisConfirmBtn(ActionEvent _event) {
		if (_event.getSource() == optionBtnPanelBtns[0]) {
			count=1;
			this.setCloseType(BillDialog.CONFIRM);
			this.dispose();			
		} else if (_event.getSource() == optionBtnPanelBtns[1]) {
			this.setCloseType(BillDialog.CANCEL);
			this.dispose();			
		}
	}

	//点击Option按钮
	private void onClickThisOptionBtn(ActionEvent _event) {
		for (int i = 0; i < optionBtnPanelBtns.length; i++) {
			if (optionBtnPanelBtns[i] == _event.getSource()) {
				this.setCloseType(i); //
				this.dispose(); //
			}
		}
	}
	//zzl  记录是否点击了确定按钮
	public int intlickThisConfirmBtn(){
		int a=0;
		a=count;
		return a;
	}

	//找出根窗口!!!
	public static Window getWindowForComponent(Component parentComponent) throws HeadlessException {
		if (parentComponent == null) {
			return JOptionPane.getRootFrame(); //
		}
		if (parentComponent instanceof Frame || parentComponent instanceof Dialog) {
			return (Window) parentComponent;
		}
		return getWindowForComponent(parentComponent.getParent()); //递归!!!
	}

	public void maxToScreenSize() {
		Dimension dimens = UIUtil.getScreenMaxDimension();
		int li_width = (int) dimens.getWidth(); //
		int li_height = (int) dimens.getHeight(); //
		this.setSize(li_width, li_height); //
		this.setLocation(0, 0); //
	}

	public void maxToScreenSizeBy1280AndLocationCenter() {
		Dimension dimens = UIUtil.getScreenMaxDimension(); //屏幕大小,如果是1024则是1024.如果超过1280了,则认为是1280
		int li_width = (int) dimens.getWidth(); //
		int li_height = (int) dimens.getHeight(); //
		if (li_width > 1280) {
			li_width = 1280;
		}
		if (li_height > 800) {
			li_height = 800;
		}
		this.setSize(li_width, li_height); //
		locationToCenterPosition(); //
	}

	/**
	 * 移至中间位置
	 */
	public void locationToCenterPosition() {
		if (parentContainer != null) {
			//Frame frame = JOptionPane.getFrameForComponent(parentContainer);
			Window frame = getWindowForComponent(parentContainer); //
			double ld_width = frame.getSize().getWidth();
			double ld_height = frame.getSize().getHeight();
			double ld_x = frame.getLocation().getX();
			double ld_y = frame.getLocation().getY();

			int li_maxscreen_width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
			int li_maxscreen_height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 60;

			int li_thisWidth = this.getWidth(); //本身宽度
			int li_thisHeight = this.getHeight(); //本身高度
			boolean isCross = false;
			if (li_thisWidth > li_maxscreen_width) { //如果本身宽超过了屏幕宽，则直接修改,强行保证永远不会越界!!
				li_thisWidth = li_maxscreen_width;
				isCross = true;
			}
			if (li_thisHeight > li_maxscreen_height) {
				li_thisHeight = li_maxscreen_height;
				isCross = true;
			}

			if (isCross) {
				this.setSize(li_thisWidth, li_thisHeight); //强行保证大小不可越屏幕！！！
			}

			double ld_thisX = ld_x + ld_width / 2 - li_thisWidth / 2;
			double ld_thisY = ld_y + ld_height / 2 - li_thisHeight / 2;
			if (ld_thisX + li_thisWidth >= li_maxscreen_width) { //如果右边出界了
				ld_thisX = (li_maxscreen_width - li_thisWidth) / 2; //则居屏幕中间
			}

			if (ld_thisY + li_thisHeight >= li_maxscreen_height) { //如果下边出界了
				ld_thisY = (li_maxscreen_height - li_thisHeight) / 2; //则居屏幕中间
			}

			if (ld_thisX <= 0) { //防止上面出界
				ld_thisX = 0;
			}

			if (ld_thisY <= 0) {
				ld_thisY = 0;
			}
			this.setLocation(new Double(ld_thisX).intValue(), new Double(ld_thisY).intValue());
		} else { //如果有人没有定义父亲窗口,则居中
			int li_maxscreen_width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
			int li_maxscreen_height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 60;
			int li_thisWidth = this.getWidth(); //本身宽度
			int li_thisHeight = this.getHeight(); //本身高度
			double ld_thisX = li_maxscreen_width / 2 - li_thisWidth / 2;
			double ld_thisY = li_maxscreen_height / 2 - li_thisHeight / 2;
			if (ld_thisX > 0 && ld_thisY > 0) { //万一出现变态窗口比屏幕还大
				this.setLocation((int) ld_thisX, (int) ld_thisY); //
			} else {
				this.setLocation(300, 200);
			}

		}
	}

	/**
	 * 有不少地方构建窗口后,设置窗口位置时,会在屏幕外面!!
	 * 为了彻底解决这个问题,就重构一下此方法!!,判断一下,如果小于0,或大于边界,则强行重新赋值!!
	 * 曾经总是有人不断反映画图会卡住,其实很可能就是这个原因,纠结了很长时间!!!也许终于找到这个问题原因了？？？
	 * 【xch/2012-03-09】
	 */
	@Override
	public void setLocation(int _x, int _y) {
		if (_x < 0 || _x > 1000) {
			System.out.println("BillDialog.setLocation()时发现X[" + _x + "]越界(0-1000),强制改成了100."); //
			_x = 100;

		}
		if (_y < 0 || _y > 700) {
			System.out.println("BillDialog.setLocation()时发现Y[" + _y + "]越界(0-700),强制改成了100了"); //
			_y = 100;
		}
		super.setLocation(_x, _y);
	}

	public void setCloseType(int _type) {
		closeType = _type; //
	}

	public int getCloseType() {
		return closeType;
	}

	/**
	 * 关闭之前拦截一把,用来子类覆盖!!!,如果返回true,则可以关闭,否则不能关闭!!
	 * @return
	 */
	public boolean beforeWindowClosed() {
		return true; //
	}

	/**
	 * 在窗口关闭前做!!!
	 */
	public void closeMe() {
		if (isAddDefaultWindowListener) { //如果允许做处理,才行!!!,如果被禁用了,则相当于不起效果了!!!
			if (beforeWindowClosed()) { //
				this.dispose(); //关闭!!
			}
		}
	}
	//初始化光标
	public void initFocusAfterWindowOpened() {
	}

	public boolean isAddDefaultWindowListener() {
		return isAddDefaultWindowListener;
	}

	public void setAddDefaultWindowListener(boolean isAddDefaultWindowListener) {
		this.isAddDefaultWindowListener = isAddDefaultWindowListener;
	}

	public java.awt.Container getParentContainer() {
		return parentContainer;
	}

	protected void finalize() throws Throwable {
		super.finalize(); //
		logger.debug("JVM GC回收器成功回收了BillDialog【" + this.getClass().getName() + "】(" + this.hashCode() + ")的资源...");
	}

	class MyWindowAdapter extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			closeMe(); //
		}

		@Override
		public void windowOpened(WindowEvent e) {
			super.windowOpened(e); //
			initFocusAfterWindowOpened(); //初始化光标,因为在窗口没有打开时调用compent.requestFocus()是没有效果的!!!
		}

	}

}
/**************************************************************************
 * $RCSfile: BillDialog.java,v $  $Revision: 1.29 $  $Date: 2012/09/14 09:17:30 $
 *
 * $Log: BillDialog.java,v $
 * Revision 1.29  2012/09/14 09:17:30  xch123
 * *** empty log message ***
 *
 * Revision 1.1  2012/08/28 09:40:49  Administrator
 * *** empty log message ***
 *
 * Revision 1.28  2012/06/29 09:25:39  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.27  2012/06/15 09:50:40  xch123
 * *** empty log message ***
 *
 * Revision 1.26  2012/06/08 10:15:23  xch123
 * *** empty log message ***
 *
 * Revision 1.25  2012/06/08 02:09:23  xch123
 * *** empty log message ***
 *
 * Revision 1.24  2012/06/08 02:05:59  xch123
 * *** empty log message ***
 *
 * Revision 1.23  2012/06/07 10:58:35  xch123
 * *** empty log message ***
 *
 * Revision 1.22  2012/03/09 11:09:46  xch123
 * *** empty log message ***
 *
 * Revision 1.21  2012/03/09 10:24:45  xch123
 * *** empty log message ***
 *
 * Revision 1.20  2012/03/09 10:19:52  xch123
 * *** empty log message ***
 *
 * Revision 1.19  2012/03/09 10:17:46  xch123
 * *** empty log message ***
 *
 * Revision 1.18  2012/03/09 10:14:12  xch123
 * *** empty log message ***
 *
 * Revision 1.17  2012/03/09 10:06:47  xch123
 * *** empty log message ***
 *
 * Revision 1.16  2011/10/21 06:25:01  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.15  2011/10/20 10:38:28  xch123
 * *** empty log message ***
 *
 * Revision 1.14  2011/10/10 06:31:35  wanggang
 * restore
 *
 * Revision 1.12  2011/08/12 06:19:45  xch123
 * *** empty log message ***
 *
 * Revision 1.11  2011/08/05 11:17:29  xch123
 * *** empty log message ***
 *
 * Revision 1.10  2011/03/25 12:05:29  xch123
 * *** empty log message ***
 *
 * Revision 1.9  2011/03/24 13:45:34  haoming
 * *** empty log message ***
 *
 * Revision 1.8  2011/02/18 10:22:49  xch123
 * *** empty log message ***
 *
 * Revision 1.7  2010/10/29 05:18:34  xch123
 * *** empty log message ***
 *
 * Revision 1.6  2010/09/30 06:12:51  xch123
 * *** empty log message ***
 * 
 * Revision 1.18  2009/08/05 11:14:43  xuchanghua
 * *** empty log message ***
 *
 *
**************************************************************************/
