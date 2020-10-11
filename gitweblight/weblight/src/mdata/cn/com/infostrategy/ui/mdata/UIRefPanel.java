/**************************************************************************
 * $RCSfile: UIRefPanel.java,v $  $Revision: 1.38 $  $Date: 2013/02/28 06:14:42 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.ScrollablePopupFactory;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractWLTCompentPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_Ref;
import cn.com.infostrategy.ui.mdata.cardcomp.PopQuickSearchDialog;
import cn.com.infostrategy.ui.mdata.cardcomp.RefDialog_BigArea;
import cn.com.infostrategy.ui.mdata.cardcomp.RefDialog_Color;
import cn.com.infostrategy.ui.mdata.cardcomp.RefDialog_Date;
import cn.com.infostrategy.ui.mdata.cardcomp.RefDialog_DateTime;
import cn.com.infostrategy.ui.mdata.cardcomp.RefDialog_Excel;
import cn.com.infostrategy.ui.mdata.cardcomp.RefDialog_File;
import cn.com.infostrategy.ui.mdata.cardcomp.RefDialog_ListTemplet;
import cn.com.infostrategy.ui.mdata.cardcomp.RefDialog_Multi;
import cn.com.infostrategy.ui.mdata.cardcomp.RefDialog_Office;
import cn.com.infostrategy.ui.mdata.cardcomp.RefDialog_Picture;
import cn.com.infostrategy.ui.mdata.cardcomp.RefDialog_RegFormat;
import cn.com.infostrategy.ui.mdata.cardcomp.RefDialog_Table;
import cn.com.infostrategy.ui.mdata.cardcomp.RefDialog_Tree;
import cn.com.infostrategy.ui.mdata.cardcomp.RefDialog_TreeTemplet;

/**
 * ���ǲ������Ļ���,�Ǹ�������!!�ؼ��ĺ� WebLightƽ̨�в��տ���˵��һ������Ҫ�Ŀؼ�,���ж��������˼��!!���г������������Ҫ! ���˳��õı��Ͳ���,���Ͳ���,��ѡ����,�Լ����������,����ı���,����,ʱ��,�ļ�ѡ���ȶ��ǲ���!!
 * 
 * @author xch
 * 
 */
public class UIRefPanel extends AbstractWLTCompentPanel {
	private static final long serialVersionUID = 1L;

	protected Pub_Templet_1_ItemVO templetItemVO = null; //
	protected String type = null;
	protected String refDesc = null; // ����ԭʼ����...
	protected String hyperlinkdesc = null; // �����Ӷ���
	protected WLTHyperLinkLabel[] hyperlinks = null;
	protected BillPanel billPanel = null; //
	protected String key = null;
	protected String name = null;
	protected JLabel label = null;
	protected int li_textfield_width = 150 - 19;
	protected JTextComponent textField = null;
	protected JButton btn_ref = null; // ���հ�ť
	protected WLTButton importB = null;
	protected boolean itemEditable = true; //
	protected TBUtil tBUtil = null; // ת������!!
	protected RefItemVO currRefItemVO = null; // �ò��ն�Ӧ������VO

	private static Logger logger = WLTLogger.getLogger(UIRefPanel.class); //

	private AbstractRefDialog refDialog = null;
	protected int li_opentype = -1; //

	private boolean stopEdit = false; // �ı���ֹͣ�༭�������ı���༭�󣬻ᵯ��JPopMenu�������ı���ʧȥ���㡣��ô��Ҫ��ϴ˲������п��ơ�
	private String quear[][]; // ������ն�Ӧ�����������
	private PopQuickSearchDialog dialog; // ������popmenu�Ի���
	private Timer timer = new Timer();
	private String querytextValue; // �ֹ�¼�������
	public boolean textComponentWrap = false; // �ı��Ƿ��У���������JTextFiled��������JTextArea��
	int filesize = TBUtil.getTBUtil().getSysOptionIntegerValue("�ϴ�������С", 25);//��Ŀ�о����и����˾����ϴ������Ĵ�С��������һ�����������/2016-12-26��

	/**
	 * ֱ�Ӵ���!!!
	 * 
	 * @param _key
	 * @param _name
	 * @param _refdesc
	 * @param _type
	 */
	public UIRefPanel(String _key, String _name, String _refdesc, String _type, RefItemVO _initRefItemVO, BillPanel _billPanel) {
		super();
		this.key = _key; //
		this.name = _name; //
		this.currRefItemVO = _initRefItemVO; // ��ʼֵ
		this.refDesc = _refdesc; //
		this.billPanel = _billPanel; //
		this.type = _type; // ��������!!
		li_opentype = 1;
		initCompent();
	}

	/**
	 * ��Ԫԭģ�����ݹ���!!
	 * 
	 * @param _templetVO
	 */
	public UIRefPanel(Pub_Templet_1_ItemVO _templetVO, RefItemVO _initRefItemVO, BillPanel _billPanel) {
		this(_templetVO, _initRefItemVO, _billPanel, true); //
	}

	public UIRefPanel(Pub_Templet_1_ItemVO _templetVO, RefItemVO _initRefItemVO, BillPanel _billPanel, boolean _isInit) {
		super();
		this.templetItemVO = _templetVO; //
		this.key = templetItemVO.getItemkey(); // ItemKey
		this.type = templetItemVO.getItemtype(); // ItemType
		if (ClientEnvironment.getInstance().isEngligh()) {
			this.name = templetItemVO.getItemname_e(); // Ӣ������
		} else {
			this.name = templetItemVO.getItemname(); // ��������
		}
		this.currRefItemVO = _initRefItemVO; // ��ʼֵ
		this.refDesc = templetItemVO.getRefdesc(); // ԭʼ����
		this.hyperlinkdesc = templetItemVO.getHyperlinkdesc(); // �����Ӷ���
		this.billPanel = _billPanel; // ���
		this.li_textfield_width = templetItemVO.getCardwidth().intValue() - 19; // Ĭ�Ͽ��,�ı�����
		li_opentype = 2;
		if (_isInit) {
			initCompent(); // �ȳ�ʼ���ÿؼ�!!!,Ȼ��������ȥʵ�ֲ�����λ��!
		}
	}

	public UIRefPanel(String _key, String _name) {
		super();
		this.key = templetItemVO.getItemkey(); // ItemKey
		this.type = templetItemVO.getItemtype(); // ItemType
	}

	/**
	 * ��ʼ��ҳ��ؼ�,����ʵ�������¼�,������λ��,λ����������ʵ��
	 */
	protected final void initCompent() {
		if (templetItemVO != null && templetItemVO.getUCDfVO() != null && "Y".equals(templetItemVO.getUCDfVO().getConfValue("�ı��Ƿ���"))) {
			textComponentWrap = true;
			textField = new JTextArea(1, 10);
			((JTextArea) textField).setLineWrap(true);
		} else {
			textField = new JTextField();
		}
		if (this.type.equals(WLTConstants.COMP_OFFICE)) {
			textField.setForeground(Color.BLUE); //
		} else {
			textField.setForeground(LookAndFeel.systemLabelFontcolor); //
		}
		textField.setToolTipText("��סCtrl��˫����������ʾ!"); //
		// textField.setEditable(false); //
		btn_ref = new JButton(UIUtil.getImage(getIconName(this.type))); // ��ͬ�Ŀؼ�ʹ�ò�ͬ�ı��!!!
		btn_ref.setToolTipText("����Ҽ��������"); //
		btn_ref.setRequestFocusEnabled(false); //
		btn_ref.setPreferredSize(new Dimension(20, 20)); // ��Ť�Ŀ����߶�
		btn_ref.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getID() != ActionEvent.SHIFT_MASK) {
					btn_ref.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
					textField.putClientProperty("JTextField.DrawFocusBorder", "Y");
					textField.requestFocus();
					onButtonClicked();
					textField.putClientProperty("JTextField.DrawFocusBorder", null);
					btn_ref.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
				}
			}
		});

		btn_ref.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					if (e.isShiftDown()) { // �������Shift��!!
						showRefMsg(); // ��ʾ������Ϣ
					} else { // ��������õ�
						if (((JButton) e.getSource()).isEnabled()) {
							//������Ŀ�ͻ��и�������Ʋ�ӣ��ܻ᲻С���һ�������ļ����ļ��������һأ��ͻ������������ʴ˴�������ʾ��
							boolean msg = TBUtil.getTBUtil().getSysOptionBooleanValue("Office�ؼ��һ�����Ƿ���ʾ", false);
							if (msg) {
								if (!MessageBox.confirm(UIRefPanel.this, "�һ�����ո��ļ����Ƿ������")) {
									return;
								}
							}
							clearData(); //
						}
					}
				}
			}

		}); //

		textField.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent _event) {
				onClickTextField(_event); //

			}

			public void mouseEntered(MouseEvent e) {
				onMouseEnter();
			}

			public void mouseExited(MouseEvent e) {
				onMouseExit();
			}

		});

		if (currRefItemVO == null) { // ����г�ʼֵ,����ҳ������ʾ����!
			textField.setText("");
		} else {
			textField.setText(currRefItemVO.getName()); //
		}
		if (this.type.equals(WLTConstants.COMP_FILECHOOSE) || this.type.equals(WLTConstants.COMP_BIGAREA) || this.type.equals(WLTConstants.COMP_EXCEL) || this.type.equals(WLTConstants.COMP_DATE) || this.type.equals(WLTConstants.COMP_DATETIME)) { //
			textField.setEditable(false);
		} else if (this.type.equals(WLTConstants.COMP_OFFICE)) {
			textField.setEditable(false); //
			if (this.templetItemVO.getUCDfVO() == null || this.templetItemVO.getUCDfVO().getConfBooleanValue("�Ƿ���ʾ���밴ť", true)) { // ���ָ���е��밴ť!Ĭ�����е��밴ť��,�����︻�������ӵ�,����ֱ���ϴ��ļ�!! �еĵط������ǲ������ϴ�!!
				importB = new WLTButton(UIUtil.getImage("office_132.gif"));
				importB.setPreferredSize(new Dimension(19, 19));
				importB.setToolTipText("�����ļ�");
				importB.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						importB(); // �����ļ�!
					}
				});
			}
		}
		this.hyperlinks = createHyperLinkLabel(this.key, this.name, this.hyperlinkdesc, this.billPanel); //
		btn_ref.putClientProperty("JButton.RefTextField", textField);
		textField.putClientProperty("JTextField.OverWidth", (int) btn_ref.getPreferredSize().getWidth()); // û��Ҫ�㰴ť��ô��ֻҪ����һ����Ϳ��ԡ�
		textField.putClientProperty("refpanel", this);
		initQuickSearchPopAction();
	}

	protected void initQuickSearchPopAction() {
		if (templetItemVO == null)
			return; // �������������˶���Ϊ��
		final CommUCDefineVO commUCDfVO = templetItemVO.getUCDfVO(); // ȡ�����ģ�嶨��Ŀؼ�VO
		/*
		 * ��ѡ��������ı�����ѡ���ܡ�by���� 2013-4-26
		 */
		if (commUCDfVO != null) {
			if ("Y".equals(commUCDfVO.getConfValue("�ı����Ƿ�ɱ༭", "N")) && "N".equals(templetItemVO.getUCDfVO().getConfValue("���Զ�ѡ", "N"))) {
				textField.addKeyListener(new KeyAdapter() {
					public void keyReleased(KeyEvent e) { // �����¼�
						if (e.getKeyCode() != KeyEvent.VK_ENTER && e.getKeyCode() != KeyEvent.VK_DOWN) { // �ǻس���ִ��
							if (querytextValue == null) {// ��ȡ�ı�����
								querytextValue = textField.getText();
							} else if (querytextValue.equals(textField.getText())) {
								return;
							}
							querytextValue = textField.getText(); // ����ֵ
							if (querytextValue == null || querytextValue.trim().equals("")) {
								return;
							}
							timer.cancel(); // ֹͣ
							timer.purge();// �ͷ�
							timer = new Timer();
							timer.schedule(new TimerTask() { // ��������
										public void run() {
											showQuickSearchPopupMenu();
										}
									}, 500); //
						}
					}

					public void keyPressed(KeyEvent e) {
						if (e.getKeyCode() == KeyEvent.VK_DOWN && commUCDfVO.getTypeName().equals(WLTConstants.COMP_REFPANEL_TREETEMPLET)) {
							if (dialog == null || !dialog.isVisible()) {
								timer.cancel();
								timer.purge();
								timer = new Timer();
								timer.schedule(new TimerTask() { // ��������
											public void run() {
												showQuickSearchPopupMenu();
											}
										}, 10); //
							} else {
								dialog.moveDown();
							}
						} else if (e.getKeyCode() == KeyEvent.VK_UP && commUCDfVO.getTypeName().equals(WLTConstants.COMP_REFPANEL_TREETEMPLET)) {
							String textValue = textField.getText();
							if (textValue != null && dialog != null && dialog.isShowing() && textValue.equals(dialog.getCondition())) {
								dialog.moveUp();
							}
						} else if (e.getKeyCode() == KeyEvent.VK_ENTER && dialog.isShowing()) { // �س�������ֵ
							dialog.setObject();
							dialog.setVisible(false);
						}
					}
				});
				textField.addFocusListener(new FocusAdapter() {
					@Override
					public void focusLost(FocusEvent e) {
						// ������ֶ������ֵ����û��ѡ����Ҫ��ԭ
						if (!stopEdit && currRefItemVO != null && currRefItemVO.getName() != null && !currRefItemVO.getName().equals(textField.getText())) {
							setObject(currRefItemVO);
							timer.cancel();
							timer.purge();
						}
					}
				});
			}
		}
	}

	/**
	 * ���,�����Office�ؼ�,�����ֱ�ӵ����!!!��������ʲô��! �����һ���,����˫��ʱ������ʾ��������!
	 * 
	 * @param _event
	 */
	private void onClickTextField(MouseEvent _event) {
		if ((this instanceof CardCPanel_Ref) && type.equals(WLTConstants.COMP_OFFICE)) { // ����ǿ�Ƭ���������..
			try {
				CommUCDefineVO uCDfVO = this.templetItemVO.getUCDfVO(); //
				String str_serverDir = "/officecompfile"; //
				if (uCDfVO != null) {
					str_serverDir = uCDfVO.getConfValue("�洢Ŀ¼", "/officecompfile"); // �洢Ŀ¼!!
				}
				textField.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				if (!textField.getText().trim().equals("")) {
					String parOpenType = null;
					if (uCDfVO != null) {// ��Ҫ�ж�һ���Ƿ�Ϊ�ա����/2012-03-29��
						parOpenType = uCDfVO.getConfValue("Office���յĴ򿪷�ʽ", null);
					}
					if (parOpenType == null) {// ������ֶ�û������office�����Ĵ򿪷�ʽ������ȫ�ֵ�ƽ̨����
						parOpenType = new TBUtil().getSysOptionStringValue("Office���յĴ򿪷�ʽ", "IE");
					}
					if (parOpenType.equalsIgnoreCase("IE")) { // ���ʹ��IE��
						if (str_serverDir.equals("/officecompfile")) { // �����Ĭ�ϵ�office·��!
							UIUtil.openRemoteServerFile("office", this.getRefID()); // ֱ�Ӵ򿪷������˵��ļ�!! ֱ�Ӵ�tif�ļ�!!��֪Ϊʲô,�ڿͻ�������ֻ��һ��,���������˵�Servletȴ�ᱻ����2-3��!!
						} else { // ������ж������ϴ�·��!���� /help
							/***
							 * Gwang 2012-09-17�޸� Office�ؼ��м���"�Ƿ����·��"����, Ĭ��ΪFlase ��Ϊϵͳ�е�һЩ�ĵ�����Ҫ���Ű�װ��һ��װ��ָ��Ŀ¼, ��������ĵ�. ����Ҫ�ֶ����ƹ�ȥ����ϵͳ����ʱ�ĸ��Ӷ�, �ر����ڲ�Ʒ��װ��ʱ *
							 */
							boolean isAbsoluteSeverDir = uCDfVO.getConfBooleanValue("�Ƿ����·��", false);
							String pathType = "serverdir";
							if (isAbsoluteSeverDir) {
								pathType = "realdir";
							}
							UIUtil.openRemoteServerFile(pathType, str_serverDir + "/" + this.getRefID()); // ֱ�Ӵ򿪷������˵��ļ�!! ֱ�Ӵ�tif�ļ�!!
						}
					} else if (parOpenType.equalsIgnoreCase("Office")) { // ���ʹ��Office���ƴ�
						onButtonClicked(); //
					}
					try {
						Thread.currentThread().sleep(3000); // һ��Ҫ��3����ܴ�,���򿪹��������̵߳�,����������õȴ�3��ķ�ʽ!!
					} catch (Exception exx) {
					}
				} else {
					textField.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			} catch (Exception ex) {
				ex.printStackTrace(); //
			} finally {
				textField.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		} else {
			if (_event.getClickCount() == 2) {
				JTextComponent clickedTextField = ((JTextComponent) _event.getSource()); //
				String str_text = clickedTextField.getText(); //
				if (str_text != null && !str_text.equals("") && !clickedTextField.isEditable()) { // �������Ϊ�գ��Ͳ�����������
					str_text = getTBUtil().replaceAll(str_text, ";", ";\n"); // ��ǰ��һ��,�Եú���!��ҵ�ͻ�ʯ����Ǳ�Թ!����Ҫ������!!
					MessageBox.showTextArea(clickedTextField, str_text); //
				}
			}
		}
	}

	// �ұ߶��˸�+�ŵ�ֱ���ϴ������İ�ť!!!
	private void importB() {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File(ClientEnvironment.str_downLoadFileDir));
		String str_tempdir = "/officecompfile"; //
		if (templetItemVO.getUCDfVO() != null) { // ���������!!
			str_tempdir = templetItemVO.getUCDfVO().getConfValue("�洢Ŀ¼", "/officecompfile"); //
		}
		final String str_uploaddir = str_tempdir;
		chooser.setDialogTitle("��(�ļ���С���ó���" + filesize + "M)"); //
		int result = chooser.showOpenDialog(this); // ��ֹ�������½ǵ���!
		if (result == 0 && chooser.getSelectedFile() != null) {
			ClientEnvironment.str_downLoadFileDir = chooser.getSelectedFile().getParent();
		} else {
			return;
		}
		final File allChooseFiles = chooser.getSelectedFile(); // ѡ�е��ļ�!! ��ǰ����ֻ���ϴ�doc,xls��! ����һ��,��ʵҲ�����ϴ��κ��ļ�!!
		new SplashWindow(this, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				try {
					dealUpload(allChooseFiles, str_uploaddir);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}, 366, 366);
		btn_ref.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
	}

	private void dealUpload(File _allChooseFiles, String _serverDir) throws Exception {
		String str_serverRealFileName = onUploadBtnClicked(_allChooseFiles, _serverDir); // ���ط������˵��ļ�!!
		if (str_serverRealFileName == null || str_serverRealFileName.trim().equals("")) {
			return;
		}
		String str_masterName = str_serverRealFileName.substring(0, str_serverRealFileName.lastIndexOf(".")); // ����
		String str_extName = str_serverRealFileName.substring(str_serverRealFileName.lastIndexOf(".") + 1, str_serverRealFileName.length()); // ��չ��
		if (str_masterName.indexOf("_") > 0) { // ��������!
			str_masterName = str_masterName.substring(str_masterName.indexOf("_") + 1, str_masterName.length()); //
		}
		str_masterName = new TBUtil().convertHexStringToStr(str_masterName); // ��16����ת�ɿ��ö�������
		setObject(new RefItemVO(str_serverRealFileName, null, str_masterName + "." + str_extName));
	}

	// �ϴ�����
	private String onUploadBtnClicked(File uploadfile, String _serverDir) throws Exception {
		FileInputStream fins = new FileInputStream(uploadfile);
		try {
			if (uploadfile.length() > (25 * 1024 * 1024)) {
				MessageBox.show(this, "���ϴ����ļ�����,���Ϊ" + filesize + "MB!");
				return null;
			}
			if (uploadfile.getName().lastIndexOf(".") < 0 || uploadfile.getName().lastIndexOf(".") == uploadfile.getName().length()) {
				MessageBox.show(this, "���ϴ��к�׺�����ļ�!");
				return null;
			}
			if (uploadfile.getName().lastIndexOf(".") > 0) {
				String houzhui = uploadfile.getName().substring(uploadfile.getName().lastIndexOf(".") + 1, uploadfile.getName().length());
				for (int i = 0; i < houzhui.length(); i++) {
					if (!((houzhui.charAt(i) >= 'a' && houzhui.charAt(i) <= 'z') || (houzhui.charAt(i) >= 'A' && houzhui.charAt(i) <= 'Z'))) {
						MessageBox.show(this, "���ϴ��кϷ���׺�����ļ�!");
						return null;
					}
				}
			}
			int filelength = new Long(uploadfile.length()).intValue();
			byte[] filecontent = new byte[filelength];
			fins.read(filecontent);
			String name = uploadfile.getName().substring(0, uploadfile.getName().lastIndexOf(".")); // ����!
			String type = uploadfile.getName().substring(uploadfile.getName().lastIndexOf(".")); // ��չ��
			String str_newFileName = new TBUtil().convertStrToHexString(name) + type; // ת����16����,��N��ͷ!!!��ǰ̨�ͽ�����ת16���ƴ���!

			if (str_newFileName.length() > 235) {// Ϊ���ļ���Ψһ�ԣ��ں�̨����Ҫ��N1516��1516Ϊ���кţ������к�λ��������Ĭ���ļ�����󳤶�Ϊ255-20=235.
				MessageBox.show(this, "�ļ�������,��ص�Լ" + (str_newFileName.length() - 235) / 4 + "������!");
				return null;
			}

			/***
			 * Gwang 2012-09-17�޸� Office�ؼ��м���"�Ƿ����·��"����, Ĭ��ΪFlase ��Ϊϵͳ�е�һЩ�ĵ�����Ҫ���Ű�װ��һ��װ��ָ��Ŀ¼, ��������ĵ�. ����Ҫ�ֶ����ƹ�ȥ����ϵͳ����ʱ�ĸ��Ӷ�, �ر����ڲ�Ʒ��װ��ʱ
			 */
			boolean isAbsoluteSeverDir = false; //
			if (templetItemVO.getUCDfVO() != null) {
				isAbsoluteSeverDir = this.templetItemVO.getUCDfVO().getConfBooleanValue("�Ƿ����·��", false); //
			}
			String str_serverRealFileName = UIUtil.upLoadFile(_serverDir, str_newFileName, isAbsoluteSeverDir, uploadfile.getAbsolutePath().substring(0, uploadfile.getAbsolutePath().lastIndexOf("\\")), uploadfile.getName(), true, true, true); // //
			System.out.println("�����ļ���[" + str_serverRealFileName + "]"); //
			return str_serverRealFileName.substring(str_serverRealFileName.lastIndexOf("/") + 1, str_serverRealFileName.length()); // �����ļ���!
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
			return null;
		} finally {
			if (fins != null) {
				fins.close();
			}
		}
	}

	/**
	 *�������,�����Office�ؼ�,��Ҫ��Ч��
	 */
	private void onMouseEnter() {
		if (this instanceof CardCPanel_Ref) { // ����ǿ�Ƭ���������..
			if (this.type.equals(WLTConstants.COMP_OFFICE)) { // �����Office�ؼ�
				if (!textField.getText().trim().equals("")) {
					textField.setCursor(new Cursor(Cursor.HAND_CURSOR));
					textField.setForeground(Color.RED); //
				}
			}
		}
	}

	/**
	 *����Ƴ�,�����Office�ؼ�,��Ҫ��Ч��
	 */
	private void onMouseExit() {
		if (this instanceof CardCPanel_Ref) { // ����ǿ�Ƭ���������..
			if (this.type.equals(WLTConstants.COMP_OFFICE)) { // �����Office�ؼ�
				if (!textField.getText().trim().equals("")) {
					textField.setCursor(Cursor.getDefaultCursor()); //
					textField.setForeground(Color.BLUE); //
				}
			}
		}
	}

	/**
	 * ֱ���˹�����,����ִ��SQL
	 */
	public void onButtonClicked() {
		try {
			if (this.templetItemVO == null) { // ���ģ��VOΪ��,���ʾ��ֱ��ʹ��itemkey,itemname������!!!��Ҫ��������ģ��VO!�������Ҫ�����ж����������,̫����!!
				DefaultTMO tmo = new DefaultTMO("", new String[] { "itemkey", "itemname", "itemtype", "Refdesc" }, new String[][] { // 
						{ this.key, this.name, this.type, this.refDesc } // 
						});
				this.templetItemVO = UIUtil.getPub_Templet_1VO(tmo).getItemVos()[0]; //
			}
			refDialog = null; //
			refDialog = getRefDialog(); //
			if (refDialog == null) {
				return;
			}
			refDialog.setPubTempletItemVO(this.templetItemVO); // ����ģ�������VO
			refDialog.initialize(); //
			if (refDialog.isShowAfterInitialize()) { // ����ڶ�����
				refDialog.setVisible(true); //
			}

			// �������ڹرշ���!!
			boolean bo_ifdataChanged = false;
			if (refDialog.getCloseType() == 1) { // �����ȷ�Ϸ���
				RefItemVO returnVO = refDialog.getReturnRefItemVO(); //
				bo_ifdataChanged = ifChanged(returnVO, this.currRefItemVO); // ���緵��ֵ�뵱ǰֵ�Ƿ�һ��!
				setObject(returnVO); // ���õ�ǰֵ,���޸Ŀؼ��ı����е�ֵ!!

				// ��Ƭ�����༭״̬,�����༭��ʽ.....
				// if (bo_ifdataChanged) { Office�ؼ���Ϊֵһ������,����Զ����!�����ڶ��ξͲ��ᴥ���༭��ʽ��!! ���Ըɴ���Զ�����༭��ʽ����! �����ڿͻ���,���ܲ�������! ����ע�͵�����!
				onBillCardValueChanged(new BillCardEditEvent(this.key, this.getObject(), this)); // ����ǿ�Ƭ,�򴥷��¼�!!
				// }
			} else {
				bo_ifdataChanged = false; //
			}

			if (currRefItemVO != null) {
				currRefItemVO.setValueChanged(bo_ifdataChanged); // ����û�б仯
			}

			if (this.billPanel instanceof BillListPanel) {
				((BillListPanel) billPanel).stopEditing(); // �б�����༭״̬�������༭��ʽ!!
			} else if (this.billPanel instanceof BillPropPanel) {
				((BillPropPanel) billPanel).stopEditing(); // �б�����༭״̬�������༭��ʽ!!
			}
		} catch (java.lang.ClassCastException _ex) {
			_ex.printStackTrace(); //
			MessageBox.showTextArea(this, "������������ն�����ܲ�����!!\r\n�㽫�������Ͷ����[" + this.type + "],�����ն���ȴ��һ����,���ն�������:\r\n" + this.refDesc); //
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		} finally {
			refDialog = null;
		}
	}

	/**
	 * �������մ���!!
	 * 
	 * @return
	 * @throws Exception
	 */
	public AbstractRefDialog getRefDialog() throws Exception {
		CommUCDefineVO commUCDfVO = this.templetItemVO.getUCDfVO(); // ȡ�����ģ�嶨��Ŀؼ�VO
		if (this.type.equals(WLTConstants.COMP_REFPANEL) || this.type.equals(WLTConstants.COMP_REFPANEL_TREE) || this.type.equals(WLTConstants.COMP_REFPANEL_MULTI) || //
				this.type.equals(WLTConstants.COMP_REFPANEL_CUST) || this.type.equals(WLTConstants.COMP_REFPANEL_REGEDIT) || this.type.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || this.type.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET)) { //
			if (this.refDesc == null || this.refDesc.trim().equalsIgnoreCase("null") || this.refDesc.trim().equals("")) {
				MessageBox.show(this, "û�ж������˵��,����!"); //
				return null;
			}
			if (commUCDfVO == null) { // ����ؼ������ж���!!!
				MessageBox.show(this, "�������幫ʽ��\r\n" + this.refDesc + "\r\n��ʱʧ��,û�гɹ�����CommUCDefineVO����,���鶨���﷨!!\r\n��������:��ע���Ƿ���˻����˶���,˫����֮���(���������һ�����������׶������)..."); //
				return null;
			}
		}
		if (commUCDfVO == null) {
			commUCDfVO = new CommUCDefineVO(this.type); //
		}
		if (!this.type.equals(commUCDfVO.getTypeName())) { //
			MessageBox.show(this, "�ؼ�����[" + type + "]��ؼ����幫ʽ�е�����[" + commUCDfVO.getTypeName() + "]��һ��,���鶨�����õĵ�һ������!"); //
			return null; //
		}

		// ��commUCDfVO��¡һ��,Ȼ���滻һЩ�ؼ������SQL����е�����!!��Ϊ�����ĳ�����BS�˽�����ʽ,�����޷�ȡ��getItemValue()���ֹ�ʽ��ֵ,��ֻ���ڷ������˽��к����ת��!!!
		commUCDfVO = new MetaDataUIUtil().cloneCommUCDefineVO(commUCDfVO, this.billPanel); // ֮��Ҫ��¡,����Ϊһ���滻��,�ڶ��ξͲ������滻��,�Ӷ��ﲻ����Ҫʵ�ֵ��߼���!!//�Ժ���UI�˿����й�ʽ����̬�޸Ķ����е�ֵ,����SQL,�������¡,ֱ���޸�ԭ����,�����ɵڶ���ִ��ʱ�߼�����!!!
		if (commUCDfVO == null) {
			return null; //
		}

		// һ�������ж���ʲô����!!!
		AbstractRefDialog tmpRefDialog = null; //
		if (this.type.equals(WLTConstants.COMP_REFPANEL)) { // ����Ǳ��Ͳ���
			tmpRefDialog = new RefDialog_Table(this, this.name, this.currRefItemVO, this.billPanel, commUCDfVO); //
		} else if (this.type.equals(WLTConstants.COMP_REFPANEL_TREE)) { // ���Ͳ���!!
			tmpRefDialog = new RefDialog_Tree(this, this.name, this.currRefItemVO, this.billPanel, commUCDfVO); //
		} else if (this.type.equals(WLTConstants.COMP_REFPANEL_MULTI)) { // ��ѡ����
			tmpRefDialog = new RefDialog_Multi(this, this.name, this.currRefItemVO, this.billPanel, commUCDfVO); //
		} else if (this.type.equals(WLTConstants.COMP_REFPANEL_CUST)) { // �Զ������,���乹��
			tmpRefDialog = getCustRefDialog(commUCDfVO); //
		} else if (this.type.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET)) { // �б�ģ�����
			tmpRefDialog = new RefDialog_ListTemplet(this, this.name, this.currRefItemVO, this.billPanel, commUCDfVO); //
		} else if (this.type.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET)) { // ����ģ�����
			tmpRefDialog = new RefDialog_TreeTemplet(this, this.name, this.currRefItemVO, this.billPanel, commUCDfVO); //
		} else if (this.type.equals(WLTConstants.COMP_REFPANEL_REGFORMAT)) { // ע���������
			tmpRefDialog = new RefDialog_RegFormat(this, this.name, this.currRefItemVO, this.billPanel, commUCDfVO); //
		} else if (this.type.equals(WLTConstants.COMP_DATE)) { // ����
			tmpRefDialog = new RefDialog_Date(this, this.name, this.currRefItemVO, this.billPanel, commUCDfVO); //
		} else if (this.type.equals(WLTConstants.COMP_DATETIME)) { // ʱ��
			tmpRefDialog = new RefDialog_DateTime(this, this.name, this.currRefItemVO, this.billPanel); //
		} else if (this.type.equals(WLTConstants.COMP_BIGAREA)) { // ���ı���
			tmpRefDialog = new RefDialog_BigArea(this, this.name, this.currRefItemVO, this.billPanel, commUCDfVO); //
		} else if (this.type.equals(WLTConstants.COMP_FILECHOOSE)) { // �ļ�ѡ���
			tmpRefDialog = new RefDialog_File(this, this.name + "(���������ϴ����Ϊ" + filesize + "MB!!)", this.currRefItemVO, this.billPanel, commUCDfVO); //
		} else if (this.type.equals(WLTConstants.COMP_COLOR)) { // ��ɫѡ���
			tmpRefDialog = new RefDialog_Color(this, this.name, this.currRefItemVO, this.billPanel); //
		} else if (this.type.equals(WLTConstants.COMP_CALCULATE)) { // ������
			// tmpRefDialog = new RefDialog_(this, this.name, this.currRefItemVO, this.billPanel); //
		} else if (this.type.equals(WLTConstants.COMP_PICTURE)) { // ͼƬѡ���
			tmpRefDialog = new RefDialog_Picture(this, this.name, this.currRefItemVO, this.billPanel); //
		} else if (this.type.equals(WLTConstants.COMP_EXCEL)) { // Excel�ؼ�!!
			tmpRefDialog = new RefDialog_Excel(this, this.name, this.currRefItemVO, this.billPanel, commUCDfVO); // //
		} else if (this.type.equals(WLTConstants.COMP_OFFICE)) { // �����Office�ؼ�!!! ���鷳��һ�ֲ���!!!
			String str_serverDir = "/officecompfile"; //
			if (commUCDfVO == null) {
				commUCDfVO = new CommUCDefineVO("Office�ؼ�"); //
				commUCDfVO.setConfValue("�ļ�����", "doc"); //
			} else { //
				str_serverDir = commUCDfVO.getConfValue("�洢Ŀ¼", "/officecompfile"); //
			}
			if (this.currRefItemVO != null && this.currRefItemVO.getId() != null && !getTBUtil().isEndWithInArray(this.currRefItemVO.getId(), new String[] { ".doc", "docx", ".xls", ".wps" }, true)) { // ����ҵ��������ΪtifҲ������,�ɴ����office�ļ���,ͳͳ��ֱ������!!
				if (str_serverDir.equals("/officecompfile")) { // �����Ĭ�ϵ�office·��!
					UIUtil.openRemoteServerFile("office", this.currRefItemVO.getId()); // ֱ�Ӵ򿪷������˵��ļ�!! ֱ�Ӵ�tif�ļ�!!
				} else { // ������ж������ϴ�·��!���� /help
					UIUtil.openRemoteServerFile("serverdir", str_serverDir + "/" + this.currRefItemVO.getId()); // ֱ�Ӵ򿪷������˵��ļ�!! ֱ�Ӵ�tif�ļ�!!
				}
				return null; //
			} else {
				tmpRefDialog = new RefDialog_Office(this, this.name, this.currRefItemVO, this.billPanel, commUCDfVO); // ��������!!
			}
		} else {
			MessageBox.show(this, "δ֪�Ĳ�������[" + this.type + "]!"); //
		}
		return tmpRefDialog; //
	}

	// �����Զ������VO��������!!!
	private AbstractRefDialog getCustRefDialog(CommUCDefineVO _dfvo) throws Exception {
		String str_clsName = _dfvo.getConfValue("�Զ�������"); //
		String[] str_paras = _dfvo.getAllConfKeys("����", true); // ȡ���в���!!
		if (str_paras.length == 0) { // ���û�в���!!
			Class dialog_class = Class.forName(str_clsName);
			Class cp[] = { java.awt.Container.class, String.class, RefItemVO.class, BillPanel.class }; //
			Constructor constructor = dialog_class.getConstructor(cp);
			return (AbstractRefDialog) constructor.newInstance(new Object[] { this, this.name, this.currRefItemVO, this.billPanel }); //
		} else { // ����в���!!
			Class cp[] = new Class[4 + str_paras.length]; // ǰ4�������ǹ̶���,����ĵ���!!
			cp[0] = java.awt.Container.class;
			cp[1] = String.class;
			cp[2] = RefItemVO.class;
			cp[3] = BillPanel.class;
			for (int i = 4; i < cp.length; i++) {
				cp[i] = java.lang.String.class;
			}
			Object[] ob = new Object[4 + str_paras.length];
			ob[0] = this;
			ob[1] = this.name;
			ob[2] = this.currRefItemVO;
			ob[3] = this.billPanel;
			for (int j = 0; j < str_paras.length; j++) {
				ob[4 + j] = _dfvo.getConfValue(str_paras[j]); // ԭ����Ϊ ob[4 + j] = str_paras[j]�����Զ��������ֻ�ܵõ�������keyֵ�������������õ����õĲ���ֵ��������2012-02-27��
			}
			Class dialog_class = Class.forName(str_clsName);
			Constructor constructor = dialog_class.getConstructor(cp);
			return (AbstractRefDialog) constructor.newInstance(ob); //
		}
	}

	public void showRefMsg() {
		StringBuffer sb_info = new StringBuffer(); //
		if (currRefItemVO == null) {
			sb_info.append("��ǰֵΪnull"); //
		} else {
			sb_info.append("RefID=[" + currRefItemVO.getId() + "]\r\n");
			sb_info.append("RefCode=[" + currRefItemVO.getCode() + "]\r\n");
			sb_info.append("RefName=[" + currRefItemVO.getName() + "]\r\n");

			sb_info.append("\r\n----------- HashVO���� -------------\r\n");

			if (currRefItemVO.getHashVO() == null) {
				sb_info.append("HaVOΪ��\r\n");
			} else {
				String[] keys = currRefItemVO.getHashVO().getKeys(); //
				for (int i = 0; i < keys.length; i++) {
					sb_info.append(keys[i] + "=[" + currRefItemVO.getHashVO().getStringValue(keys[i]) + "]\r\n");
				}
			}
		}
		// ////.....

		MessageBox.showTextArea(this, "����ʵ�ʰ󶨵�����", sb_info.toString()); //
	}

	public String getItemKey() {
		return key;
	}

	public JLabel getLabel() {
		return label;
	}

	public String getItemName() {
		return name;
	}

	public JTextComponent getTextField() {
		return textField;
	}

	private String getIconName(String _type) {
		if (_type.equals(WLTConstants.COMP_REFPANEL) || // ���Ͳ���1
				_type.equals(WLTConstants.COMP_REFPANEL_TREE) || // ���Ͳ���
				_type.equals(WLTConstants.COMP_REFPANEL_MULTI) || // ��ѡ����
				_type.equals(WLTConstants.COMP_REFPANEL_CUST) || // �Զ������
				_type.equals(WLTConstants.COMP_REFPANEL_REGEDIT) // ע�����
		) {
			return getTBUtil().getSysOptionStringValue("Ĭ�ϲ���ͼ������", "refsearch.gif"); // zt_012.gif,��������Ŀ����Ϊ��ν��UI��׼,�����������ǵ�ͼ��,����ֻ�ܸ�ɻ��!!!
		} else if (_type.equals(WLTConstants.COMP_DATE)) { // ����
			return getTBUtil().getSysOptionStringValue("���ڲ���ͼ������", "date.gif"); // zt_030.gif/zt_075.gif,��������Ŀ����Ϊ��ν��UI��׼,�����������ǵ�ͼ��,����ֻ�ܸ�ɻ��!!!
		} else if (_type.equals(WLTConstants.COMP_DATETIME)) { // ʱ��
			return "office_129.gif";
		} else if (_type.equals(WLTConstants.COMP_BIGAREA)) { // ���ı���
			return "bigtextarea.gif";
		} else if (_type.equals(WLTConstants.COMP_FILECHOOSE)) { // �ļ�ѡ���
			return "filepath.gif";
		} else if (_type.equals(WLTConstants.COMP_COLOR)) { // ��ɫѡ���
			return "colorchoose.gif";
		} else if (_type.equals(WLTConstants.COMP_CALCULATE)) { // ������
			return "office_004.GIF";
		} else if (_type.equals(WLTConstants.COMP_PICTURE)) { // ͼƬѡ���
			return "pic2.gif";
		} else if (_type.equals(WLTConstants.COMP_EXCEL)) { // Excel�ؼ�
			return "office_138.gif"; // Excel�ؼ�
		} else if (_type.equals(WLTConstants.COMP_OFFICE)) { // office�ؼ�
			return "office_193.gif"; // office�ؼ�
		} else {
			return getTBUtil().getSysOptionStringValue("Ĭ�ϲ���ͼ������", "refsearch.gif"); // zt_012.gif,��������Ŀ����Ϊ��ν��UI��׼,�����������ǵ�ͼ��,����ֻ�ܸ�ɻ��!!!
		}
	}

	private boolean ifChanged(RefItemVO returnVO, RefItemVO _currVO) {
		if (returnVO == null) {
			if (_currVO == null) {
				return false; // ����û�б仯
			} else {
				return true;
			}
		} else {
			if (_currVO == null) {
				return true;
			} else {
				if (returnVO.getId() != null && returnVO.getId().equals(_currVO.getId())) {
					return false;
				} else {
					return true;
				}
			}
		}
	}

	/**
	 * �����า��
	 * 
	 * @param _evt
	 */
	public void onBillCardValueChanged(BillCardEditEvent _evt) {

	}

	public JButton getBtn_ref() {
		return btn_ref;
	}

	public BillPanel getBillPanelFrom() {
		return billPanel; //
	}

	public String getRefID() {
		if (getObject() != null) {
			return ((RefItemVO) getObject()).getId();
		} else {
			return null;
		}
	}

	public void setRefID(String _id) {
		if (getObject() != null) {
			RefItemVO itemVO = (RefItemVO) getObject();
			itemVO.setId(_id); //
		}
	}

	public String getRefCode() {
		if (getObject() != null) {
			return ((RefItemVO) getObject()).getCode();
		} else {
			return null;
		}
	}

	public void setRefCode(String _code) {
		if (getObject() != null) {
			RefItemVO itemVO = (RefItemVO) getObject();
			itemVO.setCode(_code); //
		}
	}

	public String getRefName() {
		if (getObject() != null) {
			return ((RefItemVO) getObject()).getName();
		} else {
			return null;
		}
	}

	public void setRefName(String _name) {
		if (getObject() != null) {
			RefItemVO itemVO = (RefItemVO) getObject();
			itemVO.setName(_name); //
		}
		this.getTextField().setText(_name); //
	}

	public String getValue() {
		return getRefID();
	}

	/**
	 * ����ҲӦ���Ǹ��ӵı仯!!
	 */
	public void setValue(String _value) {
		this.currRefItemVO = new RefItemVO(_value, _value, _value);
		if (_value == null) {
			textField.setText(""); //
		} else {
			textField.setText(_value); //
		}
		textField.select(0, 0); //
	}

	public void reset() {
		setObject(null); //
		// ��Ƭ�����༭״̬,�����༭��ʽ.....
	}

	/**
	 * ��������ť,���������,Ȼ�󴥷��޸��¼�,�Ӷ������༭��ʽ!! ��Ϊ����ò����϶����˱༭��ʽ,���������changed�¼�,�ͻᷢ������!!
	 */
	private void clearData() {
		reset(); //
		onBillCardValueChanged(new BillCardEditEvent(this.key, this.getObject(), this)); // ����ǿ�Ƭ,�򴥷��¼�!!
		if (this.billPanel instanceof BillCardPanel) {
			// ((BillCardPanel) billPanel).execEditFormula(this.key); //�б�����༭״̬�������༭��ʽ!!
		} else if (this.billPanel instanceof BillListPanel) {
			((BillListPanel) billPanel).stopEditing(); // �б�����༭״̬�������༭��ʽ!!
		}
	}

	public void setItemEditable(boolean _bo) {
		itemEditable = _bo; //
		if (this.type.equals(WLTConstants.COMP_FILECHOOSE) || this.type.equals(WLTConstants.COMP_OFFICE) || this.type.equals(WLTConstants.COMP_EXCEL)) { //
			// textField.setEditable(false);//ģ��Ĳ����Ƿ��ܱ༭�������� �޸�
			btn_ref.setEnabled(true); //
		} else {
			if (billPanel instanceof BillCardPanel) {
				BillCardPanel cardPanel = (BillCardPanel) billPanel;
				// �����Ƭ�ǿɱ༭�ġ�
				if (WLTConstants.BILLDATAEDITSTATE_INSERT.equals(cardPanel.getEditState()) || WLTConstants.BILLDATAEDITSTATE_UPDATE.equals(cardPanel.getEditState())) {
					if (templetItemVO.getUCDfVO() != null && "Y".equals(templetItemVO.getUCDfVO().getConfValue("�ı����Ƿ�ɱ༭", "N")) && "N".equals(templetItemVO.getUCDfVO().getConfValue("���Զ�ѡ", "N"))) {
						textField.setEditable(_bo);
					}
				}
			}
			btn_ref.setEnabled(_bo);
		}
		if (importB != null && this.type.equals(WLTConstants.COMP_OFFICE)) {
			importB.setEnabled(_bo);
		}
	}

	@Override
	public boolean isItemEditable() {
		return btn_ref.isEnabled();
	}

	public boolean getItemEditable() {
		return itemEditable; //
	}

	public void setItemVisiable(boolean _bo) {
		this.setVisible(_bo); //
	}

	public Object getObject() {
		return currRefItemVO; // Ӧ����Ҫȡ������HashVO!!!
	}

	public void setObject(Object _obj) {
		try {
			currRefItemVO = null;
			if (_obj == null) {
				textField.setText("");
			} else {
				if (_obj instanceof StringItemVO) { // ��ʱֱ����UI�����б�������HashVOʱ,�������ؼ��ڿ�Ƭ���˳�ᱨ��,����Ҫ����һ�������������!!
					String str_realValue = ((StringItemVO) _obj).getStringValue(); //
					currRefItemVO = new RefItemVO(str_realValue, null, str_realValue); //
				} else if (_obj instanceof RefItemVO) {
					currRefItemVO = (RefItemVO) _obj; //
				} else {
					currRefItemVO = new RefItemVO(_obj.toString(), null, _obj.toString()); //
				}
				textField.setText(currRefItemVO.getName()); //
			}
			textField.select(0, 0); //

			if (this.type.equals(WLTConstants.COMP_COLOR)) { // �������ɫ����,��Ҫ���⴦��,�������ı���ı�����ɫ!!
				String str_color = "255,255,255";
				if (currRefItemVO != null) { // �޸���ָ�뱨��
					str_color = currRefItemVO.getId(); //
				}
				String[] str_items = str_color.split(","); //
				textField.setBackground(new Color(Integer.parseInt(str_items[0]), Integer.parseInt(str_items[1]), Integer.parseInt(str_items[2]))); //
			}
		} catch (java.lang.ClassCastException ex) {
			System.out.println("�ؼ�[" + key + "][" + name + "]���������Ͳ���,��ҪRefItemVO,ʵ����[" + _obj.getClass().getName() + "]!"); //
			throw ex;
		}
	}

	public void focus() {
		textField.requestFocus();
		textField.requestFocusInWindow();
	}

	private TBUtil getTBUtil() {
		if (tBUtil == null) {
			tBUtil = new TBUtil();
		}
		return tBUtil; //
	}

	public String getType() {
		return type;
	}

	public String getRefDesc() {
		return refDesc;
	}

	// ���ò��ն��壬�༭��ʽ��Ҫ�޸���!!
	public void setRefDesc(String refDesc) {
		this.refDesc = refDesc;
	}

	public WLTButton getImportB() {
		return importB;
	}

	public void setImportBVisible(boolean _visible) {
		importB.setVisible(_visible);
	}

	@Override
	public int getAllWidth() {
		return 0;
	}

	// �������ٲ�ѯ�Ի���
	private void showQuickSearchPopupMenu() {
		// ���������ģ�����
		CommUCDefineVO commUCDfVO = templetItemVO.getUCDfVO(); // ȡ�����ģ�嶨��Ŀؼ�VO
		if (quear == null) {
			String templetCode = commUCDfVO.getConfValue("ģ�����");
			boolean isTrimFirstLevel = commUCDfVO.getConfBooleanValue("�Ƿ�ص���һ��", true); // �Ƿ�ص���һ��??Ĭ���ǽص�!
			try {
				Pub_Templet_1VO tpVO = UIUtil.getPub_Templet_1VO(templetCode);
				BillTreePanel treePanel = new BillTreePanel(tpVO);
				String queryStr = commUCDfVO.getConfValue("����SQL����");
				String str_dataPolicy = commUCDfVO.getConfValue("����Ȩ�޲���"); // ���ݹ���Ȩ�ޣ���
				if (str_dataPolicy != null) { // ���������һ������,��ǿ���ֹ��޸�ģ���е�����Ȩ�޲���!֮������ô������Ϊ������������������Ϊÿ������������һ��ģ�壡Ȼ�����������ģ�壡���������һ������,��ֻ��Ҫһ��ģ��!���ҿ���������ǰ��ĳ��ģ�壨�������Ѿ������˲��ԣ�!��Ϊ�����һ���֮��
					treePanel.setDataPolicy(str_dataPolicy, commUCDfVO.getConfValue("����Ȩ�޲���ӳ��")); //
				}
				treePanel.queryDataByCondition(queryStr, getViewOnlyLevel(commUCDfVO)); // ��ѯ����!!!
				BillVO vos[] = treePanel.getAllBillVOs();// ȡ�����ж���
				String id = commUCDfVO.getConfValue("ID�ֶ�");
				String name = commUCDfVO.getConfValue("NAME�ֶ�");
				String[][] str_data = new String[vos.length][3]; // Ŀǰ��һ�����еļ���
				String str_pathName = "";
				for (int i = 0; i < vos.length; i++) {
					str_data[i][0] = vos[i].getPkValue();
					str_data[i][1] = vos[i].getStringValue(name, ""); //
					str_pathName = (String) vos[i].getUserObject("$ParentPathName");
					if (isTrimFirstLevel && str_pathName != null && str_pathName.indexOf("-") > 0) { // ���Ҫ�õ���һ��!
						str_pathName = str_pathName.substring(str_pathName.indexOf("-") + 1, str_pathName.length()); //
					}
					str_data[i][2] = str_pathName;//
				}
				quear = str_data;
				String textValue = textField.getText();
				if (textValue != null && textValue.trim().length() > 0) {
					textValue = textValue.trim();
					List l = new ArrayList();
					for (int i = 0; i < quear.length; i++) {
						if (quear[i][1].contains(textValue)) {
							l.add(new RefItemVO(quear[i][0], quear[i][1], quear[i][2]));
						}
					}
					if (l.size() > 0) {
						dialog = new PopQuickSearchDialog(textField, this, false);
						textField.putClientProperty("POPMENUTYPE", ScrollablePopupFactory.POP_TYPE_VERTICAL);
						dialog.setCondition(textField.getText());
						dialog.setList((RefItemVO[]) l.toArray(new RefItemVO[0]));
						stopEdit = true; // ֹͣ�༭,�����Ͳ������ڽ������⣬
						dialog.show(textField, 0, textField.getHeight() + 1);
						stopEdit = false;
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else {
			String textValue = textField.getText();
			if (textValue != null && textValue.length() > 0) {
				List l = new ArrayList();
				// ���ƶ�
				if (dialog != null && dialog.isShowing() && textValue.equals(dialog.getCondition())) {
					dialog.moveDown();
				} else {
					for (int i = 0; i < quear.length; i++) {
						if (quear[i][1].contains(textValue)) {
							l.add(new RefItemVO(quear[i][0], quear[i][1], quear[i][2]));
						}
					}
					if (l.size() > 0) {
						dialog = new PopQuickSearchDialog(textField, this, false);
						textField.putClientProperty("POPMENUTYPE", ScrollablePopupFactory.POP_TYPE_VERTICAL);
						dialog.setCondition(textField.getText());
						dialog.setList((RefItemVO[]) l.toArray(new RefItemVO[0]));
						stopEdit = true;
						dialog.show(textField, 0, textField.getHeight() + 1);
						stopEdit = false;
					}
				}
			}
		}
		// ���ƶ�
	}

	private int getViewOnlyLevel(CommUCDefineVO dfvo) {
		int li_level = 0; //
		if (dfvo.getConfValue("ֻ��ǰ����") != null) { //
			try {
				li_level = Integer.parseInt(dfvo.getConfValue("ֻ��ǰ����")); //
			} catch (Exception e) {
				e.printStackTrace(); // �������ֲ��Ϸ�,��Ե��쳣������0����,��֤�����ܳ���.
			}
		}
		return li_level; //
	}
}
