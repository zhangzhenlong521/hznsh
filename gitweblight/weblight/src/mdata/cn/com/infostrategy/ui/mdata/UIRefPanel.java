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
 * 这是参照面板的基类,是个抽象类!!关键的很 WebLight平台中参照可以说是一个最重要的控件,所有都基于这个思想!!所有抽象好这个类很重要! 除了常用的表型参照,树型参照,多选参照,自己定义参照外,像大文本框,日历,时间,文件选择框等都是参照!!
 * 
 * @author xch
 * 
 */
public class UIRefPanel extends AbstractWLTCompentPanel {
	private static final long serialVersionUID = 1L;

	protected Pub_Templet_1_ItemVO templetItemVO = null; //
	protected String type = null;
	protected String refDesc = null; // 参照原始定义...
	protected String hyperlinkdesc = null; // 超链接定义
	protected WLTHyperLinkLabel[] hyperlinks = null;
	protected BillPanel billPanel = null; //
	protected String key = null;
	protected String name = null;
	protected JLabel label = null;
	protected int li_textfield_width = 150 - 19;
	protected JTextComponent textField = null;
	protected JButton btn_ref = null; // 参照按钮
	protected WLTButton importB = null;
	protected boolean itemEditable = true; //
	protected TBUtil tBUtil = null; // 转换工具!!
	protected RefItemVO currRefItemVO = null; // 该参照对应的数据VO

	private static Logger logger = WLTLogger.getLogger(UIRefPanel.class); //

	private AbstractRefDialog refDialog = null;
	protected int li_opentype = -1; //

	private boolean stopEdit = false; // 文本框停止编辑。由于文本框编辑后，会弹出JPopMenu，导致文本框失去焦点。那么需要配合此参数进行控制。
	private String quear[][]; // 缓存参照对应表的所有数据
	private PopQuickSearchDialog dialog; // 弹出的popmenu对话框
	private Timer timer = new Timer();
	private String querytextValue; // 手工录入的条件
	public boolean textComponentWrap = false; // 文本是否换行，不换航是JTextFiled，换行是JTextArea；
	int filesize = TBUtil.getTBUtil().getSysOptionIntegerValue("上传附件大小", 25);//项目中经常有个别人纠结上传附件的大小，故设置一个参数【李春娟/2016-12-26】

	/**
	 * 直接创建!!!
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
		this.currRefItemVO = _initRefItemVO; // 初始值
		this.refDesc = _refdesc; //
		this.billPanel = _billPanel; //
		this.type = _type; // 参照类型!!
		li_opentype = 1;
		initCompent();
	}

	/**
	 * 从元原模板数据构造!!
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
			this.name = templetItemVO.getItemname_e(); // 英文名字
		} else {
			this.name = templetItemVO.getItemname(); // 中文名称
		}
		this.currRefItemVO = _initRefItemVO; // 初始值
		this.refDesc = templetItemVO.getRefdesc(); // 原始定义
		this.hyperlinkdesc = templetItemVO.getHyperlinkdesc(); // 超链接定义
		this.billPanel = _billPanel; // 面板
		this.li_textfield_width = templetItemVO.getCardwidth().intValue() - 19; // 默认宽度,文本框宽度
		li_opentype = 2;
		if (_isInit) {
			initCompent(); // 先初始化好控件!!!,然后由子类去实现布局与位置!
		}
	}

	public UIRefPanel(String _key, String _name) {
		super();
		this.key = templetItemVO.getItemkey(); // ItemKey
		this.type = templetItemVO.getItemtype(); // ItemType
	}

	/**
	 * 初始化页面控件,包括实例化与事件,但不设位置,位置在子类中实现
	 */
	protected final void initCompent() {
		if (templetItemVO != null && templetItemVO.getUCDfVO() != null && "Y".equals(templetItemVO.getUCDfVO().getConfValue("文本是否换行"))) {
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
		textField.setToolTipText("按住Ctrl键双击可扩大显示!"); //
		// textField.setEditable(false); //
		btn_ref = new JButton(UIUtil.getImage(getIconName(this.type))); // 不同的控件使用不同的标标!!!
		btn_ref.setToolTipText("点击右键清空数据"); //
		btn_ref.setRequestFocusEnabled(false); //
		btn_ref.setPreferredSize(new Dimension(20, 20)); // 按扭的宽度与高度
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
					if (e.isShiftDown()) { // 如果按了Shift键!!
						showRefMsg(); // 显示参照信息
					} else { // 如果是有用的
						if (((JButton) e.getSource()).isEnabled()) {
							//惠州项目客户中个别人左撇子，总会不小心右击清空了文件，文件不容易找回，客户很情绪化，故此处增加提示语
							boolean msg = TBUtil.getTBUtil().getSysOptionBooleanValue("Office控件右击清空是否提示", false);
							if (msg) {
								if (!MessageBox.confirm(UIRefPanel.this, "右击将清空该文件，是否继续？")) {
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

		if (currRefItemVO == null) { // 如果有初始值,则在页面上显示出来!
			textField.setText("");
		} else {
			textField.setText(currRefItemVO.getName()); //
		}
		if (this.type.equals(WLTConstants.COMP_FILECHOOSE) || this.type.equals(WLTConstants.COMP_BIGAREA) || this.type.equals(WLTConstants.COMP_EXCEL) || this.type.equals(WLTConstants.COMP_DATE) || this.type.equals(WLTConstants.COMP_DATETIME)) { //
			textField.setEditable(false);
		} else if (this.type.equals(WLTConstants.COMP_OFFICE)) {
			textField.setEditable(false); //
			if (this.templetItemVO.getUCDfVO() == null || this.templetItemVO.getUCDfVO().getConfBooleanValue("是否显示导入按钮", true)) { // 如果指定有导入按钮!默认是有导入按钮的,这是孙富君后来加的,可以直接上传文件!! 有的地方需求是不允许上传!!
				importB = new WLTButton(UIUtil.getImage("office_132.gif"));
				importB.setPreferredSize(new Dimension(19, 19));
				importB.setToolTipText("导入文件");
				importB.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						importB(); // 增加文件!
					}
				});
			}
		}
		this.hyperlinks = createHyperLinkLabel(this.key, this.name, this.hyperlinkdesc, this.billPanel); //
		btn_ref.putClientProperty("JButton.RefTextField", textField);
		textField.putClientProperty("JTextField.OverWidth", (int) btn_ref.getPreferredSize().getWidth()); // 没必要搞按钮这么宽，只要超出一点儿就可以。
		textField.putClientProperty("refpanel", this);
		initQuickSearchPopAction();
	}

	protected void initQuickSearchPopAction() {
		if (templetItemVO == null)
			return; // 工作流处理界面此对象为空
		final CommUCDefineVO commUCDfVO = templetItemVO.getUCDfVO(); // 取得这个模板定义的控件VO
		/*
		 * 单选参照添加文本输入选择功能。by郝明 2013-4-26
		 */
		if (commUCDfVO != null) {
			if ("Y".equals(commUCDfVO.getConfValue("文本框是否可编辑", "N")) && "N".equals(templetItemVO.getUCDfVO().getConfValue("可以多选", "N"))) {
				textField.addKeyListener(new KeyAdapter() {
					public void keyReleased(KeyEvent e) { // 键盘事件
						if (e.getKeyCode() != KeyEvent.VK_ENTER && e.getKeyCode() != KeyEvent.VK_DOWN) { // 非回车才执行
							if (querytextValue == null) {// 获取文本条件
								querytextValue = textField.getText();
							} else if (querytextValue.equals(textField.getText())) {
								return;
							}
							querytextValue = textField.getText(); // 赋新值
							if (querytextValue == null || querytextValue.trim().equals("")) {
								return;
							}
							timer.cancel(); // 停止
							timer.purge();// 释放
							timer = new Timer();
							timer.schedule(new TimerTask() { // 启动任务
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
								timer.schedule(new TimerTask() { // 启动任务
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
						} else if (e.getKeyCode() == KeyEvent.VK_ENTER && dialog.isShowing()) { // 回车键。赋值
							dialog.setObject();
							dialog.setVisible(false);
						}
					}
				});
				textField.addFocusListener(new FocusAdapter() {
					@Override
					public void focusLost(FocusEvent e) {
						// 如果是手动输入的值，并没有选择。需要复原
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
	 * 点击,如果是Office控件,则可以直接点击打开!!!比如正文什么的! 如果是一般的,则在双击时弹出显示所有内容!
	 * 
	 * @param _event
	 */
	private void onClickTextField(MouseEvent _event) {
		if ((this instanceof CardCPanel_Ref) && type.equals(WLTConstants.COMP_OFFICE)) { // 如果是卡片的情况才做..
			try {
				CommUCDefineVO uCDfVO = this.templetItemVO.getUCDfVO(); //
				String str_serverDir = "/officecompfile"; //
				if (uCDfVO != null) {
					str_serverDir = uCDfVO.getConfValue("存储目录", "/officecompfile"); // 存储目录!!
				}
				textField.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				if (!textField.getText().trim().equals("")) {
					String parOpenType = null;
					if (uCDfVO != null) {// 需要判断一下是否为空【李春娟/2012-03-29】
						parOpenType = uCDfVO.getConfValue("Office参照的打开方式", null);
					}
					if (parOpenType == null) {// 如果该字段没有设置office参数的打开方式，则用全局的平台参数
						parOpenType = new TBUtil().getSysOptionStringValue("Office参照的打开方式", "IE");
					}
					if (parOpenType.equalsIgnoreCase("IE")) { // 如果使用IE打开
						if (str_serverDir.equals("/officecompfile")) { // 如果是默认的office路径!
							UIUtil.openRemoteServerFile("office", this.getRefID()); // 直接打开服务器端的文件!! 直接打开tif文件!!不知为什么,在客户端这里只调一次,但服务器端的Servlet却会被调用2-3次!!
						} else { // 如果另行定义了上传路径!比如 /help
							/***
							 * Gwang 2012-09-17修改 Office控件中加入"是否绝对路径"参数, 默认为Flase 因为系统中的一些文档必须要随着安装盘一起安装到指定目录, 比如帮助文档. 否则还要手动复制过去增加系统部署时的复杂度, 特别是在产品安装盘时 *
							 */
							boolean isAbsoluteSeverDir = uCDfVO.getConfBooleanValue("是否绝对路径", false);
							String pathType = "serverdir";
							if (isAbsoluteSeverDir) {
								pathType = "realdir";
							}
							UIUtil.openRemoteServerFile(pathType, str_serverDir + "/" + this.getRefID()); // 直接打开服务器端的文件!! 直接打开tif文件!!
						}
					} else if (parOpenType.equalsIgnoreCase("Office")) { // 如果使用Office控制打开
						onButtonClicked(); //
					}
					try {
						Thread.currentThread().sleep(3000); // 一般要等3秒才能打开,而打开过程是另开线程的,所以这里采用等待3秒的方式!!
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
				if (str_text != null && !str_text.equals("") && !clickedTextField.isEditable()) { // 如果内容为空，就不弹出。郝明
					str_text = getTBUtil().replaceAll(str_text, ";", ";\n"); // 以前是一行,显得很乱!兴业客户石瑜总是抱怨!所以要坚起来!!
					MessageBox.showTextArea(clickedTextField, str_text); //
				}
			}
		}
	}

	// 右边多了个+号的直接上传附件的按钮!!!
	private void importB() {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File(ClientEnvironment.str_downLoadFileDir));
		String str_tempdir = "/officecompfile"; //
		if (templetItemVO.getUCDfVO() != null) { // 如果定义了!!
			str_tempdir = templetItemVO.getUCDfVO().getConfValue("存储目录", "/officecompfile"); //
		}
		final String str_uploaddir = str_tempdir;
		chooser.setDialogTitle("打开(文件大小不得超过" + filesize + "M)"); //
		int result = chooser.showOpenDialog(this); // 防止在最右下角弹出!
		if (result == 0 && chooser.getSelectedFile() != null) {
			ClientEnvironment.str_downLoadFileDir = chooser.getSelectedFile().getParent();
		} else {
			return;
		}
		final File allChooseFiles = chooser.getSelectedFile(); // 选中的文件!! 以前限制只能上传doc,xls等! 后来一想,其实也可以上传任何文件!!
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
		String str_serverRealFileName = onUploadBtnClicked(_allChooseFiles, _serverDir); // 返回服务器端的文件!!
		if (str_serverRealFileName == null || str_serverRealFileName.trim().equals("")) {
			return;
		}
		String str_masterName = str_serverRealFileName.substring(0, str_serverRealFileName.lastIndexOf(".")); // 主名
		String str_extName = str_serverRealFileName.substring(str_serverRealFileName.lastIndexOf(".") + 1, str_serverRealFileName.length()); // 扩展名
		if (str_masterName.indexOf("_") > 0) { // 如果有序号!
			str_masterName = str_masterName.substring(str_masterName.indexOf("_") + 1, str_masterName.length()); //
		}
		str_masterName = new TBUtil().convertHexStringToStr(str_masterName); // 将16进制转成看得懂的中文
		setObject(new RefItemVO(str_serverRealFileName, null, str_masterName + "." + str_extName));
	}

	// 上传处理
	private String onUploadBtnClicked(File uploadfile, String _serverDir) throws Exception {
		FileInputStream fins = new FileInputStream(uploadfile);
		try {
			if (uploadfile.length() > (25 * 1024 * 1024)) {
				MessageBox.show(this, "您上传的文件过大,最大为" + filesize + "MB!");
				return null;
			}
			if (uploadfile.getName().lastIndexOf(".") < 0 || uploadfile.getName().lastIndexOf(".") == uploadfile.getName().length()) {
				MessageBox.show(this, "请上传有后缀名的文件!");
				return null;
			}
			if (uploadfile.getName().lastIndexOf(".") > 0) {
				String houzhui = uploadfile.getName().substring(uploadfile.getName().lastIndexOf(".") + 1, uploadfile.getName().length());
				for (int i = 0; i < houzhui.length(); i++) {
					if (!((houzhui.charAt(i) >= 'a' && houzhui.charAt(i) <= 'z') || (houzhui.charAt(i) >= 'A' && houzhui.charAt(i) <= 'Z'))) {
						MessageBox.show(this, "请上传有合法后缀名的文件!");
						return null;
					}
				}
			}
			int filelength = new Long(uploadfile.length()).intValue();
			byte[] filecontent = new byte[filelength];
			fins.read(filecontent);
			String name = uploadfile.getName().substring(0, uploadfile.getName().lastIndexOf(".")); // 主名!
			String type = uploadfile.getName().substring(uploadfile.getName().lastIndexOf(".")); // 扩展名
			String str_newFileName = new TBUtil().convertStrToHexString(name) + type; // 转换成16进制,以N开头!!!在前台就进行了转16进制处理!

			if (str_newFileName.length() > 235) {// 为了文件名唯一性，在后台还需要加N1516，1516为序列号，因序列号位数不定，默认文件名最大长度为255-20=235.
				MessageBox.show(this, "文件名过长,请截掉约" + (str_newFileName.length() - 235) / 4 + "个汉字!");
				return null;
			}

			/***
			 * Gwang 2012-09-17修改 Office控件中加入"是否绝对路径"参数, 默认为Flase 因为系统中的一些文档必须要随着安装盘一起安装到指定目录, 比如帮助文档. 否则还要手动复制过去增加系统部署时的复杂度, 特别是在产品安装盘时
			 */
			boolean isAbsoluteSeverDir = false; //
			if (templetItemVO.getUCDfVO() != null) {
				isAbsoluteSeverDir = this.templetItemVO.getUCDfVO().getConfBooleanValue("是否绝对路径", false); //
			}
			String str_serverRealFileName = UIUtil.upLoadFile(_serverDir, str_newFileName, isAbsoluteSeverDir, uploadfile.getAbsolutePath().substring(0, uploadfile.getAbsolutePath().lastIndexOf("\\")), uploadfile.getName(), true, true, true); // //
			System.out.println("返回文件名[" + str_serverRealFileName + "]"); //
			return str_serverRealFileName.substring(str_serverRealFileName.lastIndexOf("/") + 1, str_serverRealFileName.length()); // 最后的文件名!
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
	 *鼠标移入,如果是Office控件,则要做效果
	 */
	private void onMouseEnter() {
		if (this instanceof CardCPanel_Ref) { // 如果是卡片的情况才做..
			if (this.type.equals(WLTConstants.COMP_OFFICE)) { // 如果是Office控件
				if (!textField.getText().trim().equals("")) {
					textField.setCursor(new Cursor(Cursor.HAND_CURSOR));
					textField.setForeground(Color.RED); //
				}
			}
		}
	}

	/**
	 *鼠标移出,如果是Office控件,则要做效果
	 */
	private void onMouseExit() {
		if (this instanceof CardCPanel_Ref) { // 如果是卡片的情况才做..
			if (this.type.equals(WLTConstants.COMP_OFFICE)) { // 如果是Office控件
				if (!textField.getText().trim().equals("")) {
					textField.setCursor(Cursor.getDefaultCursor()); //
					textField.setForeground(Color.BLUE); //
				}
			}
		}
	}

	/**
	 * 直接人工构造,总是执行SQL
	 */
	public void onButtonClicked() {
		try {
			if (this.templetItemVO == null) { // 如果模板VO为空,则表示是直接使用itemkey,itemname创建的!!!则要立即创建模板VO!否则后面要到处判断这两种情况,太费事!!
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
			refDialog.setPubTempletItemVO(this.templetItemVO); // 设置模板子项定义VO
			refDialog.initialize(); //
			if (refDialog.isShowAfterInitialize()) { // 如果在定义了
				refDialog.setVisible(true); //
			}

			// 弹出窗口关闭返回!!
			boolean bo_ifdataChanged = false;
			if (refDialog.getCloseType() == 1) { // 如果按确认返回
				RefItemVO returnVO = refDialog.getReturnRefItemVO(); //
				bo_ifdataChanged = ifChanged(returnVO, this.currRefItemVO); // 比如返回值与当前值是否一样!
				setObject(returnVO); // 设置当前值,并修改控件文本框中的值!!

				// 卡片结束编辑状态,触发编辑公式.....
				// if (bo_ifdataChanged) { Office控件因为值一旦有了,则永远不变!这样第二次就不会触发编辑公式了!! 所以干脆永远触发编辑公式算了! 反正在客户端,性能不是问题! 所以注释掉这里!
				onBillCardValueChanged(new BillCardEditEvent(this.key, this.getObject(), this)); // 如果是卡片,则触发事件!!
				// }
			} else {
				bo_ifdataChanged = false; //
			}

			if (currRefItemVO != null) {
				currRefItemVO.setValueChanged(bo_ifdataChanged); // 设置没有变化
			}

			if (this.billPanel instanceof BillListPanel) {
				((BillListPanel) billPanel).stopEditing(); // 列表结束编辑状态，触发编辑公式!!
			} else if (this.billPanel instanceof BillPropPanel) {
				((BillPropPanel) billPanel).stopEditing(); // 列表结束编辑状态，触发编辑公式!!
			}
		} catch (java.lang.ClassCastException _ex) {
			_ex.printStackTrace(); //
			MessageBox.showTextArea(this, "参照类型与参照定义可能不符合!!\r\n你将参照类型定义成[" + this.type + "],但参照定义却不一定对,参照定义如下:\r\n" + this.refDesc); //
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		} finally {
			refDialog = null;
		}
	}

	/**
	 * 创建参照窗口!!
	 * 
	 * @return
	 * @throws Exception
	 */
	public AbstractRefDialog getRefDialog() throws Exception {
		CommUCDefineVO commUCDfVO = this.templetItemVO.getUCDfVO(); // 取得这个模板定义的控件VO
		if (this.type.equals(WLTConstants.COMP_REFPANEL) || this.type.equals(WLTConstants.COMP_REFPANEL_TREE) || this.type.equals(WLTConstants.COMP_REFPANEL_MULTI) || //
				this.type.equals(WLTConstants.COMP_REFPANEL_CUST) || this.type.equals(WLTConstants.COMP_REFPANEL_REGEDIT) || this.type.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || this.type.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET)) { //
			if (this.refDesc == null || this.refDesc.trim().equalsIgnoreCase("null") || this.refDesc.trim().equals("")) {
				MessageBox.show(this, "没有定义参照说明,请检查!"); //
				return null;
			}
			if (commUCDfVO == null) { // 这类控件必须有定义!!!
				MessageBox.show(this, "解析定义公式【\r\n" + this.refDesc + "\r\n】时失败,没有成功创建CommUCDefineVO对象,请检查定义语法!!\r\n友情提醒:请注意是否多了或少了逗号,双引号之类的(尤其是最后一个参数更容易多个逗号)..."); //
				return null;
			}
		}
		if (commUCDfVO == null) {
			commUCDfVO = new CommUCDefineVO(this.type); //
		}
		if (!this.type.equals(commUCDfVO.getTypeName())) { //
			MessageBox.show(this, "控件类型[" + type + "]与控件定义公式中的类型[" + commUCDfVO.getTypeName() + "]不一致,请检查定义配置的第一个参数!"); //
			return null; //
		}

		// 将commUCDfVO克隆一个,然后替换一些控件定义的SQL语句中的条件!!因为后来改成了在BS端解析公式,所以无法取得getItemValue()这种公式的值,则只能在服务器端进行宏代码转换!!!
		commUCDfVO = new MetaDataUIUtil().cloneCommUCDefineVO(commUCDfVO, this.billPanel); // 之所要克隆,是因为一旦替换后,第二次就不会再替换了,从而达不到想要实现的逻辑了!!//以后在UI端可能有公式来动态修改定义中的值,比如SQL,如果不克隆,直接修改原来的,则会造成第二次执行时逻辑不对!!!
		if (commUCDfVO == null) {
			return null; //
		}

		// 一个个的判断是什么类型!!!
		AbstractRefDialog tmpRefDialog = null; //
		if (this.type.equals(WLTConstants.COMP_REFPANEL)) { // 如果是表型参照
			tmpRefDialog = new RefDialog_Table(this, this.name, this.currRefItemVO, this.billPanel, commUCDfVO); //
		} else if (this.type.equals(WLTConstants.COMP_REFPANEL_TREE)) { // 树型参照!!
			tmpRefDialog = new RefDialog_Tree(this, this.name, this.currRefItemVO, this.billPanel, commUCDfVO); //
		} else if (this.type.equals(WLTConstants.COMP_REFPANEL_MULTI)) { // 多选参照
			tmpRefDialog = new RefDialog_Multi(this, this.name, this.currRefItemVO, this.billPanel, commUCDfVO); //
		} else if (this.type.equals(WLTConstants.COMP_REFPANEL_CUST)) { // 自定义参照,反射构建
			tmpRefDialog = getCustRefDialog(commUCDfVO); //
		} else if (this.type.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET)) { // 列表模板参照
			tmpRefDialog = new RefDialog_ListTemplet(this, this.name, this.currRefItemVO, this.billPanel, commUCDfVO); //
		} else if (this.type.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET)) { // 树型模板参照
			tmpRefDialog = new RefDialog_TreeTemplet(this, this.name, this.currRefItemVO, this.billPanel, commUCDfVO); //
		} else if (this.type.equals(WLTConstants.COMP_REFPANEL_REGFORMAT)) { // 注册样板参照
			tmpRefDialog = new RefDialog_RegFormat(this, this.name, this.currRefItemVO, this.billPanel, commUCDfVO); //
		} else if (this.type.equals(WLTConstants.COMP_DATE)) { // 日历
			tmpRefDialog = new RefDialog_Date(this, this.name, this.currRefItemVO, this.billPanel, commUCDfVO); //
		} else if (this.type.equals(WLTConstants.COMP_DATETIME)) { // 时间
			tmpRefDialog = new RefDialog_DateTime(this, this.name, this.currRefItemVO, this.billPanel); //
		} else if (this.type.equals(WLTConstants.COMP_BIGAREA)) { // 大文本框
			tmpRefDialog = new RefDialog_BigArea(this, this.name, this.currRefItemVO, this.billPanel, commUCDfVO); //
		} else if (this.type.equals(WLTConstants.COMP_FILECHOOSE)) { // 文件选择框
			tmpRefDialog = new RefDialog_File(this, this.name + "(单个附件上传最大为" + filesize + "MB!!)", this.currRefItemVO, this.billPanel, commUCDfVO); //
		} else if (this.type.equals(WLTConstants.COMP_COLOR)) { // 颜色选择框
			tmpRefDialog = new RefDialog_Color(this, this.name, this.currRefItemVO, this.billPanel); //
		} else if (this.type.equals(WLTConstants.COMP_CALCULATE)) { // 计算器
			// tmpRefDialog = new RefDialog_(this, this.name, this.currRefItemVO, this.billPanel); //
		} else if (this.type.equals(WLTConstants.COMP_PICTURE)) { // 图片选择框
			tmpRefDialog = new RefDialog_Picture(this, this.name, this.currRefItemVO, this.billPanel); //
		} else if (this.type.equals(WLTConstants.COMP_EXCEL)) { // Excel控件!!
			tmpRefDialog = new RefDialog_Excel(this, this.name, this.currRefItemVO, this.billPanel, commUCDfVO); // //
		} else if (this.type.equals(WLTConstants.COMP_OFFICE)) { // 如果是Office控件!!! 较麻烦的一种参照!!!
			String str_serverDir = "/officecompfile"; //
			if (commUCDfVO == null) {
				commUCDfVO = new CommUCDefineVO("Office控件"); //
				commUCDfVO.setConfValue("文件类型", "doc"); //
			} else { //
				str_serverDir = commUCDfVO.getConfValue("存储目录", "/officecompfile"); //
			}
			if (this.currRefItemVO != null && this.currRefItemVO.getId() != null && !getTBUtil().isEndWithInArray(this.currRefItemVO.getId(), new String[] { ".doc", "docx", ".xls", ".wps" }, true)) { // 在兴业银行中认为tif也是正文,干脆除了office文件外,统统是直接下载!!
				if (str_serverDir.equals("/officecompfile")) { // 如果是默认的office路径!
					UIUtil.openRemoteServerFile("office", this.currRefItemVO.getId()); // 直接打开服务器端的文件!! 直接打开tif文件!!
				} else { // 如果另行定义了上传路径!比如 /help
					UIUtil.openRemoteServerFile("serverdir", str_serverDir + "/" + this.currRefItemVO.getId()); // 直接打开服务器端的文件!! 直接打开tif文件!!
				}
				return null; //
			} else {
				tmpRefDialog = new RefDialog_Office(this, this.name, this.currRefItemVO, this.billPanel, commUCDfVO); // 弹出窗口!!
			}
		} else {
			MessageBox.show(this, "未知的参照类型[" + this.type + "]!"); //
		}
		return tmpRefDialog; //
	}

	// 根据自定义参照VO创建参照!!!
	private AbstractRefDialog getCustRefDialog(CommUCDefineVO _dfvo) throws Exception {
		String str_clsName = _dfvo.getConfValue("自定义类名"); //
		String[] str_paras = _dfvo.getAllConfKeys("参数", true); // 取所有参数!!
		if (str_paras.length == 0) { // 如果没有参数!!
			Class dialog_class = Class.forName(str_clsName);
			Class cp[] = { java.awt.Container.class, String.class, RefItemVO.class, BillPanel.class }; //
			Constructor constructor = dialog_class.getConstructor(cp);
			return (AbstractRefDialog) constructor.newInstance(new Object[] { this, this.name, this.currRefItemVO, this.billPanel }); //
		} else { // 如果有参数!!
			Class cp[] = new Class[4 + str_paras.length]; // 前4个参数是固定的,后面的递增!!
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
				ob[4 + j] = _dfvo.getConfValue(str_paras[j]); // 原代码为 ob[4 + j] = str_paras[j]，在自定义参照中只能得到参数的key值。而不能真正得到配置的参数值。【郝明2012-02-27】
			}
			Class dialog_class = Class.forName(str_clsName);
			Constructor constructor = dialog_class.getConstructor(cp);
			return (AbstractRefDialog) constructor.newInstance(ob); //
		}
	}

	public void showRefMsg() {
		StringBuffer sb_info = new StringBuffer(); //
		if (currRefItemVO == null) {
			sb_info.append("当前值为null"); //
		} else {
			sb_info.append("RefID=[" + currRefItemVO.getId() + "]\r\n");
			sb_info.append("RefCode=[" + currRefItemVO.getCode() + "]\r\n");
			sb_info.append("RefName=[" + currRefItemVO.getName() + "]\r\n");

			sb_info.append("\r\n----------- HashVO数据 -------------\r\n");

			if (currRefItemVO.getHashVO() == null) {
				sb_info.append("HaVO为空\r\n");
			} else {
				String[] keys = currRefItemVO.getHashVO().getKeys(); //
				for (int i = 0; i < keys.length; i++) {
					sb_info.append(keys[i] + "=[" + currRefItemVO.getHashVO().getStringValue(keys[i]) + "]\r\n");
				}
			}
		}
		// ////.....

		MessageBox.showTextArea(this, "参照实际绑定的数据", sb_info.toString()); //
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
		if (_type.equals(WLTConstants.COMP_REFPANEL) || // 表型参照1
				_type.equals(WLTConstants.COMP_REFPANEL_TREE) || // 树型参照
				_type.equals(WLTConstants.COMP_REFPANEL_MULTI) || // 多选参照
				_type.equals(WLTConstants.COMP_REFPANEL_CUST) || // 自定义参照
				_type.equals(WLTConstants.COMP_REFPANEL_REGEDIT) // 注册参照
		) {
			return getTBUtil().getSysOptionStringValue("默认参照图标名称", "refsearch.gif"); // zt_012.gif,中铁建项目中因为所谓的UI标准,必须是用他们的图标,所以只能搞成活的!!!
		} else if (_type.equals(WLTConstants.COMP_DATE)) { // 日期
			return getTBUtil().getSysOptionStringValue("日期参照图标名称", "date.gif"); // zt_030.gif/zt_075.gif,中铁建项目中因为所谓的UI标准,必须是用他们的图标,所以只能搞成活的!!!
		} else if (_type.equals(WLTConstants.COMP_DATETIME)) { // 时间
			return "office_129.gif";
		} else if (_type.equals(WLTConstants.COMP_BIGAREA)) { // 大文本框
			return "bigtextarea.gif";
		} else if (_type.equals(WLTConstants.COMP_FILECHOOSE)) { // 文件选择框
			return "filepath.gif";
		} else if (_type.equals(WLTConstants.COMP_COLOR)) { // 颜色选择框
			return "colorchoose.gif";
		} else if (_type.equals(WLTConstants.COMP_CALCULATE)) { // 计算器
			return "office_004.GIF";
		} else if (_type.equals(WLTConstants.COMP_PICTURE)) { // 图片选择框
			return "pic2.gif";
		} else if (_type.equals(WLTConstants.COMP_EXCEL)) { // Excel控件
			return "office_138.gif"; // Excel控件
		} else if (_type.equals(WLTConstants.COMP_OFFICE)) { // office控件
			return "office_193.gif"; // office控件
		} else {
			return getTBUtil().getSysOptionStringValue("默认参照图标名称", "refsearch.gif"); // zt_012.gif,中铁建项目中因为所谓的UI标准,必须是用他们的图标,所以只能搞成活的!!!
		}
	}

	private boolean ifChanged(RefItemVO returnVO, RefItemVO _currVO) {
		if (returnVO == null) {
			if (_currVO == null) {
				return false; // 设置没有变化
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
	 * 被子类覆盖
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
	 * 这里也应该是复杂的变化!!
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
		// 卡片结束编辑状态,触发编辑公式.....
	}

	/**
	 * 点击清除按钮,先清空数据,然后触发修改事件,从而触发编辑公式!! 因为如果该参照上定义了编辑公式,如果不触发changed事件,就会发生问题!!
	 */
	private void clearData() {
		reset(); //
		onBillCardValueChanged(new BillCardEditEvent(this.key, this.getObject(), this)); // 如果是卡片,则触发事件!!
		if (this.billPanel instanceof BillCardPanel) {
			// ((BillCardPanel) billPanel).execEditFormula(this.key); //列表结束编辑状态，触发编辑公式!!
		} else if (this.billPanel instanceof BillListPanel) {
			((BillListPanel) billPanel).stopEditing(); // 列表结束编辑状态，触发编辑公式!!
		}
	}

	public void setItemEditable(boolean _bo) {
		itemEditable = _bo; //
		if (this.type.equals(WLTConstants.COMP_FILECHOOSE) || this.type.equals(WLTConstants.COMP_OFFICE) || this.type.equals(WLTConstants.COMP_EXCEL)) { //
			// textField.setEditable(false);//模板的参照是否能编辑不能用了 修改
			btn_ref.setEnabled(true); //
		} else {
			if (billPanel instanceof BillCardPanel) {
				BillCardPanel cardPanel = (BillCardPanel) billPanel;
				// 如果卡片是可编辑的。
				if (WLTConstants.BILLDATAEDITSTATE_INSERT.equals(cardPanel.getEditState()) || WLTConstants.BILLDATAEDITSTATE_UPDATE.equals(cardPanel.getEditState())) {
					if (templetItemVO.getUCDfVO() != null && "Y".equals(templetItemVO.getUCDfVO().getConfValue("文本框是否可编辑", "N")) && "N".equals(templetItemVO.getUCDfVO().getConfValue("可以多选", "N"))) {
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
		return currRefItemVO; // 应该是要取得整个HashVO!!!
	}

	public void setObject(Object _obj) {
		try {
			currRefItemVO = null;
			if (_obj == null) {
				textField.setText("");
			} else {
				if (_obj instanceof StringItemVO) { // 有时直接在UI端往列表中塞入HashVO时,但日历控件在卡片浏览顺会报错,把以要处理一下这种特殊情况!!
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

			if (this.type.equals(WLTConstants.COMP_COLOR)) { // 如果是颜色参照,需要特殊处理,即设置文本框的背景颜色!!
				String str_color = "255,255,255";
				if (currRefItemVO != null) { // 修复空指针报错！
					str_color = currRefItemVO.getId(); //
				}
				String[] str_items = str_color.split(","); //
				textField.setBackground(new Color(Integer.parseInt(str_items[0]), Integer.parseInt(str_items[1]), Integer.parseInt(str_items[2]))); //
			}
		} catch (java.lang.ClassCastException ex) {
			System.out.println("控件[" + key + "][" + name + "]的数据类型不对,需要RefItemVO,实际是[" + _obj.getClass().getName() + "]!"); //
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

	// 设置参照定义，编辑公式需要修改它!!
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

	// 弹出快速查询对话框
	private void showQuickSearchPopupMenu() {
		// 如果是树形模板参照
		CommUCDefineVO commUCDfVO = templetItemVO.getUCDfVO(); // 取得这个模板定义的控件VO
		if (quear == null) {
			String templetCode = commUCDfVO.getConfValue("模板编码");
			boolean isTrimFirstLevel = commUCDfVO.getConfBooleanValue("是否截掉第一层", true); // 是否截掉第一层??默认是截的!
			try {
				Pub_Templet_1VO tpVO = UIUtil.getPub_Templet_1VO(templetCode);
				BillTreePanel treePanel = new BillTreePanel(tpVO);
				String queryStr = commUCDfVO.getConfValue("附加SQL条件");
				String str_dataPolicy = commUCDfVO.getConfValue("数据权限策略"); // 数据过滤权限！！
				if (str_dataPolicy != null) { // 如果有这样一个定义,则强行手工修改模板中的数据权限策略!之所以这么做是因为，如果不这样，则必须为每个策略先配置一个模板！然后依赖更多的模板！如果有这样一个参数,则只需要一个模板!而且可以重用以前的某个模板（哪怕其已经定义了策略）!因为反正我会冲掉之！
					treePanel.setDataPolicy(str_dataPolicy, commUCDfVO.getConfValue("数据权限策略映射")); //
				}
				treePanel.queryDataByCondition(queryStr, getViewOnlyLevel(commUCDfVO)); // 查询数据!!!
				BillVO vos[] = treePanel.getAllBillVOs();// 取得所有对象
				String id = commUCDfVO.getConfValue("ID字段");
				String name = commUCDfVO.getConfValue("NAME字段");
				String[][] str_data = new String[vos.length][3]; // 目前搞一个三列的即可
				String str_pathName = "";
				for (int i = 0; i < vos.length; i++) {
					str_data[i][0] = vos[i].getPkValue();
					str_data[i][1] = vos[i].getStringValue(name, ""); //
					str_pathName = (String) vos[i].getUserObject("$ParentPathName");
					if (isTrimFirstLevel && str_pathName != null && str_pathName.indexOf("-") > 0) { // 如果要裁掉第一层!
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
						stopEdit = true; // 停止编辑,这样就不会由于焦点问题，
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
				// 下移动
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
		// 上移动
	}

	private int getViewOnlyLevel(CommUCDefineVO dfvo) {
		int li_level = 0; //
		if (dfvo.getConfValue("只留前几层") != null) { //
			try {
				li_level = Integer.parseInt(dfvo.getConfValue("只留前几层")); //
			} catch (Exception e) {
				e.printStackTrace(); // 可能数字不合法,则吃掉异常，即当0处理,保证界面能出来.
			}
		}
		return li_level; //
	}
}
