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
	private boolean isAddDefaultWindowListener = true; //�Ƿ���Ĭ�ϴ��ڼ����¼�!!
	protected int closeType = -1; //
	private JButton maxWindowButton = null; //��󻯴��ڵİ�ť
	private Point beforeMaxLocation = null; //���֮ǰ�Ĵ���λ��!!
	private Dimension beforeMaxDimension = null; //���֮ǰ�Ĵ��ڴ�С!!

	private WLTButton[] optionBtnPanelBtns = null; //������ð�ťʱ�İ�ť����!
	private ActionListener confirmAct = null; //
	private int count=0;//zzl ��¼�Ƿ�����ȷ����ť
	
	private transient Logger logger = WLTLogger.getLogger(cn.com.infostrategy.ui.common.BillDialog.class); //

	public BillDialog() throws HeadlessException {
		super();
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //ֻ�Ը�frame��Ч..
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //Ĭ���ǹر�,����Ӧ�þ���������֮!!!
		this.addWindowListener(new MyWindowAdapter()); //
		locationToCenterPosition(); //
	}

	public BillDialog(Dialog owner, boolean modal) throws HeadlessException {
		super(owner);
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //ֻ�Ը�frame��Ч..
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		this.addWindowListener(new MyWindowAdapter()); //
		locationToCenterPosition(); //
	}

	public BillDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) throws HeadlessException {
		super(owner, title, modal, gc);
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //ֻ�Ը�frame��Ч..
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		this.addWindowListener(new MyWindowAdapter()); //
		locationToCenterPosition(); //
	}

	public BillDialog(Dialog owner, String title, boolean modal) throws HeadlessException {
		super(owner, title, modal);
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //ֻ�Ը�frame��Ч..
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		this.addWindowListener(new MyWindowAdapter()); //
		locationToCenterPosition(); //
	}

	public BillDialog(Dialog owner, String title) throws HeadlessException {
		super(owner, title);
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //ֻ�Ը�frame��Ч..
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		this.addWindowListener(new MyWindowAdapter()); //
		locationToCenterPosition(); //
	}

	public BillDialog(Dialog owner) throws HeadlessException {
		super(owner);
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //ֻ�Ը�frame��Ч..
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		this.addWindowListener(new MyWindowAdapter()); //
		locationToCenterPosition(); //
	}

	public BillDialog(Frame owner, boolean modal) throws HeadlessException {
		super(owner, modal);
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //ֻ�Ը�frame��Ч..
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		this.addWindowListener(new MyWindowAdapter()); //
		locationToCenterPosition(); //
	}

	public BillDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //ֻ�Ը�frame��Ч..
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		this.addWindowListener(new MyWindowAdapter()); //
		locationToCenterPosition(); //
	}

	public BillDialog(Frame owner, String title, boolean modal) throws HeadlessException {
		super(owner, title, modal);
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //ֻ�Ը�frame��Ч..
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		this.addWindowListener(new MyWindowAdapter()); //
		locationToCenterPosition(); //
	}

	public BillDialog(Frame owner, String title) throws HeadlessException {
		super(owner, title);
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //ֻ�Ը�frame��Ч..
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		this.addWindowListener(new MyWindowAdapter()); //
		locationToCenterPosition(); //
	}

	public BillDialog(Frame owner) throws HeadlessException {
		super(owner);
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //ֻ�Ը�frame��Ч..
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		this.addWindowListener(new MyWindowAdapter()); //
		locationToCenterPosition(); //
	}

	public BillDialog(Container _parent) {
		super(getWindowForComponent(_parent));
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //ֻ�Ը�frame��Ч..
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
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //ֻ�Ը�frame��Ч..
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
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //ֻ�Ը�frame��Ч..
		this.parentContainer = _parent; //
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		this.addWindowListener(new MyWindowAdapter()); //
		this.setSize(550, 350); //Ĭ����600*400�Ĵ���
		locationToCenterPosition(); //
	}

	public BillDialog(Container _parent, String _title, int _width, int li_height) {
		super(getWindowForComponent(_parent), _title);
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //ֻ�Ը�frame��Ч..
		this.parentContainer = _parent; //
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		this.addWindowListener(new MyWindowAdapter()); //
		this.setSize(_width, li_height); //
		locationToCenterPosition(); //
	}

	/**
	 * 
	 * @param _parent ��������
	 * @param _title  ����
	 * @param _width  ���
	 * @param li_height  �߶�
	 * @param _x  xλ��
	 * @param _y  yλ��
	 */
	public BillDialog(Container _parent, String _title, int _width, int li_height, int _x, int _y) {
		super(getWindowForComponent(_parent), _title);
		this.setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL); //ֻ�Ը�frame��Ч..
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
	 * ��������пͻ����Dialog�������,��Dialog�ǲ�֧�ִ�����󻯵�! ����ֻ������һ�и����ť!!!
	 * �������JmenuBar��ʵ��!! ����Ӱ����ǰ���е���!
	 */
	public void setMaxWindowMenuBar() {
		if (maxWindowButton != null) {
			return;
		}
		maxWindowButton = new JButton("�������", UIUtil.getImage("office_003.gif")); //
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
	 * ��󻯴���,�ٵ�����Իָ�!
	 */
	private void maxWindow() {
		String str_text = maxWindowButton.getText(); //
		if (str_text.equals("�������")) {
			beforeMaxLocation = this.getLocation(); //��¼��ԭ����λ��
			beforeMaxDimension = this.getSize(); //��¼ԭ���Ĵ�С
			maxToScreenSize(); //�������!!!
			maxWindowButton.setText("�ָ����ڴ�С"); //
		} else if (str_text.equals("�ָ����ڴ�С")) { //
			this.setLocation(beforeMaxLocation); //
			this.setSize(beforeMaxDimension); //
			maxWindowButton.setText("�������"); //
		}
	}

	public void addConfirmButtonPanel() {
		addConfirmButtonPanel(0); //
	}

	/**
	 * ���Ǿ�������һ������,����Ҫ����һ������,�����С�ȷ������ȡ����������ť,�����������һЩ�Զ����Swing�ؼ�(����һ���ı���),Ȼ��ȷ����,�ʹӿؼ���ȡ�÷���ֵ!
	 * һ���߼�������дһ���̳���BillDailog�Ĵ�����,����ʵ���ǱȽ��鷳��! ����һ�ְ취��ʹ��JOptionPane.showMessageDialog(),Ȼ���������һ�����,��Ч��Ҳ����!�ؼ��ǲ��ÿ��ƴ��ڴ�С!
	 * �����������������ͻ᷽��Ķ�,����ֱ�ӵ��ñ�������ӡ�ȷ��/ȡ������ť,Ȼ����BorderLayout.CENTER����������Զ����Swing�ؼ�,Ȼ��ֱ�ӷ���!
	 * ��xch/2012-06-08��
	 */
	public void addConfirmButtonPanel(int _type) {
		confirmAct = new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				onClickThisConfirmBtn(_event);
			}
		}; //

		optionBtnPanelBtns = new WLTButton[2]; //
		optionBtnPanelBtns[0] = new WLTButton("ȷ��"); //
		optionBtnPanelBtns[1] = new WLTButton("ȡ��"); //
		optionBtnPanelBtns[0].addActionListener(confirmAct); //���Ӽ���!!
		optionBtnPanelBtns[1].addActionListener(confirmAct); //���Ӽ���!!

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
	 * ��ʱ��Ҫ�����ť,�����Ǽ򵥵ġ�ȷ������ȡ����
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

	//���ȷ����ť
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

	//���Option��ť
	private void onClickThisOptionBtn(ActionEvent _event) {
		for (int i = 0; i < optionBtnPanelBtns.length; i++) {
			if (optionBtnPanelBtns[i] == _event.getSource()) {
				this.setCloseType(i); //
				this.dispose(); //
			}
		}
	}
	//zzl  ��¼�Ƿ�����ȷ����ť
	public int intlickThisConfirmBtn(){
		int a=0;
		a=count;
		return a;
	}

	//�ҳ�������!!!
	public static Window getWindowForComponent(Component parentComponent) throws HeadlessException {
		if (parentComponent == null) {
			return JOptionPane.getRootFrame(); //
		}
		if (parentComponent instanceof Frame || parentComponent instanceof Dialog) {
			return (Window) parentComponent;
		}
		return getWindowForComponent(parentComponent.getParent()); //�ݹ�!!!
	}

	public void maxToScreenSize() {
		Dimension dimens = UIUtil.getScreenMaxDimension();
		int li_width = (int) dimens.getWidth(); //
		int li_height = (int) dimens.getHeight(); //
		this.setSize(li_width, li_height); //
		this.setLocation(0, 0); //
	}

	public void maxToScreenSizeBy1280AndLocationCenter() {
		Dimension dimens = UIUtil.getScreenMaxDimension(); //��Ļ��С,�����1024����1024.�������1280��,����Ϊ��1280
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
	 * �����м�λ��
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

			int li_thisWidth = this.getWidth(); //������
			int li_thisHeight = this.getHeight(); //����߶�
			boolean isCross = false;
			if (li_thisWidth > li_maxscreen_width) { //��������������Ļ����ֱ���޸�,ǿ�б�֤��Զ����Խ��!!
				li_thisWidth = li_maxscreen_width;
				isCross = true;
			}
			if (li_thisHeight > li_maxscreen_height) {
				li_thisHeight = li_maxscreen_height;
				isCross = true;
			}

			if (isCross) {
				this.setSize(li_thisWidth, li_thisHeight); //ǿ�б�֤��С����Խ��Ļ������
			}

			double ld_thisX = ld_x + ld_width / 2 - li_thisWidth / 2;
			double ld_thisY = ld_y + ld_height / 2 - li_thisHeight / 2;
			if (ld_thisX + li_thisWidth >= li_maxscreen_width) { //����ұ߳�����
				ld_thisX = (li_maxscreen_width - li_thisWidth) / 2; //�����Ļ�м�
			}

			if (ld_thisY + li_thisHeight >= li_maxscreen_height) { //����±߳�����
				ld_thisY = (li_maxscreen_height - li_thisHeight) / 2; //�����Ļ�м�
			}

			if (ld_thisX <= 0) { //��ֹ�������
				ld_thisX = 0;
			}

			if (ld_thisY <= 0) {
				ld_thisY = 0;
			}
			this.setLocation(new Double(ld_thisX).intValue(), new Double(ld_thisY).intValue());
		} else { //�������û�ж��常�״���,�����
			int li_maxscreen_width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
			int li_maxscreen_height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 60;
			int li_thisWidth = this.getWidth(); //������
			int li_thisHeight = this.getHeight(); //����߶�
			double ld_thisX = li_maxscreen_width / 2 - li_thisWidth / 2;
			double ld_thisY = li_maxscreen_height / 2 - li_thisHeight / 2;
			if (ld_thisX > 0 && ld_thisY > 0) { //��һ���ֱ�̬���ڱ���Ļ����
				this.setLocation((int) ld_thisX, (int) ld_thisY); //
			} else {
				this.setLocation(300, 200);
			}

		}
	}

	/**
	 * �в��ٵط��������ں�,���ô���λ��ʱ,������Ļ����!!
	 * Ϊ�˳��׽���������,���ع�һ�´˷���!!,�ж�һ��,���С��0,����ڱ߽�,��ǿ�����¸�ֵ!!
	 * �����������˲��Ϸ�ӳ��ͼ�Ῠס,��ʵ�ܿ��ܾ������ԭ��,�����˺ܳ�ʱ��!!!Ҳ�������ҵ��������ԭ���ˣ�����
	 * ��xch/2012-03-09��
	 */
	@Override
	public void setLocation(int _x, int _y) {
		if (_x < 0 || _x > 1000) {
			System.out.println("BillDialog.setLocation()ʱ����X[" + _x + "]Խ��(0-1000),ǿ�Ƹĳ���100."); //
			_x = 100;

		}
		if (_y < 0 || _y > 700) {
			System.out.println("BillDialog.setLocation()ʱ����Y[" + _y + "]Խ��(0-700),ǿ�Ƹĳ���100��"); //
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
	 * �ر�֮ǰ����һ��,�������า��!!!,�������true,����Թر�,�����ܹر�!!
	 * @return
	 */
	public boolean beforeWindowClosed() {
		return true; //
	}

	/**
	 * �ڴ��ڹر�ǰ��!!!
	 */
	public void closeMe() {
		if (isAddDefaultWindowListener) { //�������������,����!!!,�����������,���൱�ڲ���Ч����!!!
			if (beforeWindowClosed()) { //
				this.dispose(); //�ر�!!
			}
		}
	}
	//��ʼ�����
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
		logger.debug("JVM GC�������ɹ�������BillDialog��" + this.getClass().getName() + "��(" + this.hashCode() + ")����Դ...");
	}

	class MyWindowAdapter extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			closeMe(); //
		}

		@Override
		public void windowOpened(WindowEvent e) {
			super.windowOpened(e); //
			initFocusAfterWindowOpened(); //��ʼ�����,��Ϊ�ڴ���û�д�ʱ����compent.requestFocus()��û��Ч����!!!
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
