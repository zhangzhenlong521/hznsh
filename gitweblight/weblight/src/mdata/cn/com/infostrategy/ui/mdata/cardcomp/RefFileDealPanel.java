package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jdesktop.jdic.browser.WebBrowser;
import org.jdesktop.jdic.desktop.Desktop;

import cn.com.infostrategy.to.common.ClassFileVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.OfficeCompentControlVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;
import cn.com.infostrategy.ui.mdata.OneSQLBillListConfirmDialog;
import cn.com.infostrategy.ui.report.ReportServiceIfc;

/**
 * 上传/下载文件参照的实际面板类
 * 即以前的文件选择框
 * 因为后来修改成在卡片中不是直接以参照点击按钮的样式弹出RefDialog_File，而是直接在主面板上直接布局,所以这时有必须单独将该面板拿出来，然后在RefDialog_File与卡片中再调用它!
 * 在卡片中与列表中有一个不同的效果在于需要动态修改高度，即上传文件越多，高度越高，自动撑高，即永远保证一眼清的看到所有文件！
 * 所以既要实现在加载时修改高度,又要实现在新增/删除时修改高度！！这是一个挑战，关键是要稳定,以前的引用子表的问题在于不够稳定！！
 * 
 * 为了保证Linux下的文件存储的中文文件不是乱码,决定将上传文件在服务器目录中是16进制的文件名,而存储在数据库中的还是原始文件名(因为可能需要根据关键字查询)，这本身也是一种加密安全处理。
 * 这样就要求该控件在处理文件名时要不停传换，很复杂，很容易出错！一定要注意！还要兼容旧的文件名！
 * @author xch
 *
 */
public class RefFileDealPanel extends JPanel implements ActionListener, BillListHtmlHrefListener {

	private static final long serialVersionUID = 1L;

	private CardCPanel_FileDeal cardCompent = null; //卡片中的控件..
	private BillPanel parentBillPanel = null; //父亲窗口窗口
	private BillListPanel billListPanel = null;
	private WLTButton btn_upload = null; //上传文件的按钮!!
	private WLTButton btn_batchdownload = null; //批量下载的按钮!!
	private WLTButton btn_dealHist = null; //查看处理历史
	//private String str_fileName = null; //

	//作为一个文件上传/下载控件,抽象出来一般有4个属性...
	private boolean isOnlineLookAbled = true; //是否可以在线查看,即文件名上的那个超链接,在线查看即直接在服务器端打开,在线查看是不允许编辑,不允许拷贝，不允许另存为，除非可以在线保存!!!
	private boolean isOnlineSaveAbled = true; //是否可以在线保存,即使用Office控件的功能直接保存到服务器上,在线保存时，同时是可以编辑与拷贝的!!!
	private boolean isAddDelAbled = true; //是否可以上传新文件与删除旧文件
	private boolean isDownLoadAbled = true; //是否可以下载

	private TBUtil tbUtil = new TBUtil(); //
	private AbstractRefFileIntercept intercept = null;
	private CommUCDefineVO uCDfVO; //

	private boolean isMultiSel = true; //附件是否可以上传多个, 通过控件参数"文件数量"设置  Gwang 2016-07-22 从深农商移迁
	int filesize = TBUtil.getTBUtil().getSysOptionIntegerValue("上传附件大小", 25);//项目中经常有个别人纠结上传附件的大小，故设置一个参数【李春娟/2016-12-26】

	/**
	 * 默认构造方法禁用掉
	 */
	private RefFileDealPanel() {
	}

	/**
	 * 构造方法
	 * @param initFileNames 初始化的文件名,以分隔符分隔成多个文件
	 */
	public RefFileDealPanel(BillPanel _billPanel, CommUCDefineVO _uCDfVO) {
		this.parentBillPanel = _billPanel; //
		this.uCDfVO = _uCDfVO; //
		initialize(); //
	}

	/**
	 * 构造方法
	 * @param initFileNames 初始化的文件名,以分隔符分隔成多个文件
	 */
	public RefFileDealPanel(CardCPanel_FileDeal _cardCompent, BillPanel _billPanel, CommUCDefineVO _uCDfVO) {
		this.cardCompent = _cardCompent; //
		this.parentBillPanel = _billPanel; //
		this.uCDfVO = _uCDfVO; //
		initialize(); //
	}

	/**
	 * 初始化页面..
	 */
	private void initialize() {
		if (this.uCDfVO == null) {
			uCDfVO = new CommUCDefineVO("文件选择框"); //
		}
		this.setLayout(new BorderLayout()); //设置布局类
		this.setBackground(LookAndFeel.cardbgcolor); //
		this.setOpaque(false); //透明
		billListPanel = new BillListPanel(new ServerTMODefine("cn.com.infostrategy.bs.mdata.servertmo.TMO_RefFile")); //创建列表
		billListPanel.setBillListOpaque(false); //透明
		btn_upload = new WLTButton("上传文件");
		btn_batchdownload = new WLTButton("批量下载");
		btn_dealHist = new WLTButton("处理历史"); //
		btn_upload.addActionListener(this); //
		btn_batchdownload.addActionListener(this); //
		btn_dealHist.addActionListener(this); //监听
		//没有什么用处， 去了。 Gwang 2016-09-29
		//		//兴业银行客户死活要显示上传附件的历史记录(什么时候什么人上传了,修改了,删除了附件)！
		//		if (TBUtil.getTBUtil().getSysOptionBooleanValue("上传附件是否显示处理历史按钮", true)) { //上传附件是否有【处理历史】按钮
		//			billListPanel.insertBatchBillListButton(new WLTButton[] { btn_upload, btn_batchdownload, btn_dealHist }); //加上两个按钮!!!
		//		} else {
		//			billListPanel.insertBatchBillListButton(new WLTButton[] { btn_upload, btn_batchdownload });
		//		}
		billListPanel.insertBatchBillListButton(new WLTButton[] { btn_upload, btn_batchdownload });
		billListPanel.repaintBillListButton(); //刷新按钮
		billListPanel.addBillListHtmlHrefListener(this); //
		this.add(billListPanel, BorderLayout.CENTER); //
		String interceptname = uCDfVO.getConfValue("拦截器");
		if (interceptname != null && !"".equals(interceptname.trim())) {
			try {
				intercept = (AbstractRefFileIntercept) Class.forName(interceptname.trim()).newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		//读取参数"文件数量" Gwang 2016-07-22 从深农商移迁
		if (getRefFileParam("文件数量") != null && !getRefFileParam("文件数量").equals("")) {
			if (Integer.parseInt(getRefFileParam("文件数量").toString()) == 1) {
				isMultiSel = false;
			}
		}
		//读取参数"文件数量" 从深农商移迁控件定义是老格式，这里处理一下，兼容新格式 【李春娟/2017-08-18】
		if (isMultiSel) {
			String filecount = uCDfVO.getConfValue("文件数量");
			if (filecount != null && "1".equals(filecount.trim())) {
				isMultiSel = false;
			}
		}
	}

	/**
	 * 是否可以在线可看
	 * @return
	 */
	public boolean isOnlineLookAbled() {
		return isOnlineLookAbled;
	}

	/**
	 * 是否可以在线保存
	 * @return
	 */
	public boolean isOnlineSaveAbled() {
		return isOnlineSaveAbled;
	}

	/**
	 * 是否可以新增,删除，即可以继续上传与删除
	 * @return
	 */
	public boolean isAddDelAbled() {
		return isAddDelAbled;
	}

	/**
	 * 是否可以下载
	 * @return
	 */
	public boolean isDownLoadAbled() {
		return isDownLoadAbled;
	}

	/**
	 * 设置是否可以在线查看
	 * @param isOnlineLookAbled
	 */
	public void setOnlineLookAbled(boolean isOnlineLookAbled) {
		this.isOnlineLookAbled = isOnlineLookAbled;
	}

	/**
	 * 设置是否可以在线保存
	 * @param isOnlineSaveAbled
	 */
	public void setOnlineSaveAbled(boolean isOnlineSaveAbled) {
		this.isOnlineSaveAbled = isOnlineSaveAbled;
	}

	/**
	 * 设置是否可以新增与删除
	 * @param isAddDelAbled
	 */
	public void setAddDelAbled(boolean _isAddDelAbled) {
		this.isAddDelAbled = _isAddDelAbled;
		if (isAddDelAbled) {
			this.billListPanel.setBillListBtnVisiable("上传文件", true); //
			this.billListPanel.setItemVisible("delete", true); //
		} else {
			this.billListPanel.setBillListBtnVisiable("上传文件", false); //
			this.billListPanel.setItemVisible("delete", false); //
		}
	}

	/**
	 * 设置是否可以下载
	 * @param isDownLoadAbled
	 */
	public void setDownLoadAbled(boolean _isDownLoadAbled) {
		this.isDownLoadAbled = _isDownLoadAbled;
		if (isDownLoadAbled) {
			this.billListPanel.setBillListBtnVisiable("批量下载", true); //
			this.billListPanel.setItemVisible("download", true); //
		} else {
			this.billListPanel.setBillListBtnVisiable("批量下载", false); //
			this.billListPanel.setItemVisible("download", false); //
		}
	}

	/**
	 * 设置是否可编辑,其实会分别调用4个属性..
	 * @param _editabled
	 */
	public void setEditabled(boolean _editabled) {
		if (_editabled) {
			setOnlineLookAbled(true); //
			setOnlineSaveAbled(true); //
			setAddDelAbled(true);
			setDownLoadAbled(true); //
			this.billListPanel.setItemVisible("edit", true); //
			boolean cansort = tbUtil.getSysOptionBooleanValue("附件是否可排序", true);//附件是否可排序，多选文件上传很可能需要按优先级排序，默认为可排序【李春娟/2015-06-30】
			if (cansort && isMultiSel) {//可多选的情况下才加排序按钮【李春娟/2018-08-18】
				WLTButton btn = billListPanel.getBillListBtn("上移");
				if (btn == null || !"上移".equals(btn.getText())) {//是否已经添加该按钮【李春娟/2015-12-24】
					this.billListPanel.addBatchBillListButton(new WLTButton[] { WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEUP), WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEDOWN) });
					this.billListPanel.repaintBillListButton();
				}
			}
		} else {
			setOnlineLookAbled(true); //
			setOnlineSaveAbled(true); //
			setAddDelAbled(false);
			setDownLoadAbled(true); //
			this.billListPanel.setItemVisible("edit", false); //
		}
		resetHeight();

		// 文件多于1个时才显示批量下载按钮
		if (billListPanel.getRowCount() > 1) {
			this.billListPanel.setBillListBtnVisiable("批量下载", true);
		} else {
			this.billListPanel.setBillListBtnVisiable("批量下载", false);
		}
	}

	/**
	 * 点击按钮事件动作方法
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_upload) { //上传
			onUploadFile(); //上传文件
		} else if (e.getSource() == btn_batchdownload) {
			onBatchDownLoadFile(); //批量下载文件
		} else if (e.getSource() == btn_dealHist) {
			onShowDealHist(); //
		}
	}

	/**
	 * 取得所有文件的返回值..
	 * @return
	 */
	public RefItemVO getAllFileRefItemVOs() {
		BillVO[] billVOs = this.billListPanel.getAllBillVOs(); //
		if (billVOs == null || billVOs.length == 0) {
			return null;
		}
		StringBuilder sb_fileIDs = new StringBuilder(); //
		StringBuilder sb_fileNames = new StringBuilder(); //
		for (int i = 0; i < billVOs.length; i++) {
			sb_fileIDs.append(billVOs[i].getStringValue("filename")); //
			sb_fileNames.append(getViewFileName(billVOs[i].getStringValue("filename"))); //
			if (i != billVOs.length - 1) { //
				sb_fileIDs.append(";"); //
				sb_fileNames.append(";"); //
			}
		}
		return new RefItemVO(sb_fileIDs.toString(), null, sb_fileNames.toString()); //
	}

	/**
	 * 设置值.
	 * @param _refVO
	 */
	public void setRefItemVO(RefItemVO _refVO) {
		this.billListPanel.clearTable(); //先清空以前的数据
		if (_refVO == null) {
			return;
		}

		String str_id = _refVO.getId(); //
		String[] str_ids = tbUtil.split(str_id, ";"); //文件清单是带索引号的实际文件名
		for (int i = 0; i < str_ids.length; i++) {
			int k = billListPanel.addEmptyRow(false);
			//String str_ViewFileName = getViewFileName(str_items[i]); // 
			billListPanel.setValueAt(new RefItemVO(str_ids[i], null, getViewFileName(str_ids[i])), k, "filename"); //文件名
			billListPanel.setValueAt(new StringItemVO("编辑"), k, "edit"); //编辑
			billListPanel.setValueAt(new StringItemVO("下载"), k, "download"); //下载
			billListPanel.setValueAt(new StringItemVO("删除"), k, "delete"); //删除
		}
		resetHeight(); //刷新高度
	}

	//取得显示的文件名!即去掉索引号
	private String getViewFileName(String _realFileName) {
		if (_realFileName != null && _realFileName.indexOf("/") != -1) {
			String param = _realFileName.substring(_realFileName.lastIndexOf("/") + 1, _realFileName.length());
			if (param != null && param.startsWith("N")) {
				TBUtil tbUtil = new TBUtil(); //
				int li_extentNamePos = param.lastIndexOf("."); //文件的扩展名的位置!即必须有个点!但在兴业项目中有许多文件是从后台灌入的!!也遇到到没后辍的!!所以报错!
				if (li_extentNamePos > 0) {
					return tbUtil.convertHexStringToStr(param.substring(param.indexOf("_") + 1, li_extentNamePos)) + param.substring(li_extentNamePos, param.length()); //
				} else {
					return tbUtil.convertHexStringToStr(param.substring(param.indexOf("_") + 1, param.length())); ////
				}
			} else {
				return param; //以前的版本也有存路径的？
			}
		} else {
			if (_realFileName == null || _realFileName.indexOf("_") < 0) {
				return _realFileName; //
			}
			return _realFileName.substring(_realFileName.indexOf("_") + 1, _realFileName.length()); //
		}
	}

	public void clearAllFile() {
		this.billListPanel.clearTable(); //清空表格数据
	}

	/**
	 * 上传文件..
	 */
	public void onUploadFile() {
		if (intercept != null) {
			if (!intercept.beforeUpLoad(this.parentBillPanel)) {
				return;
			}
		}
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(isMultiSel); //是否可以多选  Gwang 2016-07-22 从深农商移迁
		//chooser.setMultiSelectionEnabled(true); //可以多选!!招行提的,很有价值!!
		chooser.setCurrentDirectory(new File(getUploadDir()));
		if (ClientEnvironment.isAdmin()) {
			chooser.setDialogTitle("打开(不得超过" + filesize + "M)[CardCPanel_FileDeal,RefFileDealPanel]"); //这儿竟然没有提醒?
		} else {
			chooser.setDialogTitle("打开(不得超过" + filesize + "M)"); //这儿竟然没有提醒?
		}

		//增加"文件类型"参数, 可以对打开文件类型进行过滤 Gwang 2013/3/13			=========>Begin
		//比如: "文件类型","Excle文档,xls,xlsx;Word文档,doc,docx;所有文件,*"
		String filterString = uCDfVO.getConfValue("文件类型", "");
		if (!filterString.equals("")) {
			chooser.setAcceptAllFileFilterUsed(false); //不允许选择所有文件
			String[] filters = filterString.split(";"); //分割多个过滤器
			for (String strFilter : filters) {
				String[] strTemp = strFilter.split(","); //分割每个过滤器的描述, 过滤内容
				String desc = strTemp[0];
				//String[] to Array
				ArrayList<String> extList = new ArrayList<String>(Arrays.asList(strTemp));
				extList.remove(0);
				//Array to String[]
				String[] exts = new String[extList.size()];
				extList.toArray(exts);

				//比如过滤内容是"*", 则允许选择所有文件, 否则增加此过滤器
				if (extList.contains("*") && !chooser.isAcceptAllFileFilterUsed()) {
					chooser.setAcceptAllFileFilterUsed(true);
				} else {
					FileNameExtensionFilter filter = new FileNameExtensionFilter(desc, exts);
					chooser.setFileFilter(filter);
				}
			}
		}
		//<=========>End

		int result = chooser.showOpenDialog(this.parentBillPanel == null ? (this.cardCompent == null ? this : this.cardCompent) : this.parentBillPanel); //防止在最右下角弹出!

		if (result == 0 && chooser.getSelectedFile() != null) {
			setUploadDir(chooser.getSelectedFile().getParent());
		} else {
			return;
		}

		//是否可以多选  Gwang 2016-07-22 从深农商移迁
		File[] selFiles = new File[1];
		if (chooser.getSelectedFiles().length == 0) {
			selFiles[0] = chooser.getSelectedFile();
		} else {
			selFiles = chooser.getSelectedFiles();
		}
		final File[] allChooseFiles = selFiles;//以前钢哥迁移时没有加这句，一直无法获得文件，故修改之【李春娟/2017-08-17】

		if (intercept != null && result == 0 && chooser.getSelectedFile() != null) {
			if (!intercept.afterSelectFilesAndBeforUpload(this.parentBillPanel, allChooseFiles)) {
				return;
			}
		}

		new SplashWindow(this, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				try {
					dealUpload(allChooseFiles); //recordDealLog
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}, 366, 366);

		for (int i = 0; i < allChooseFiles.length; i++) {
			recordDealLog("新增", allChooseFiles[i].getName()); //
		}
		resetHeight(); //
		if (intercept != null) {
			intercept.afterUpLoad(this.parentBillPanel, allChooseFiles);
		}
	}

	/**
	 * 获得上传路径需记忆 民生提出 以前是上传与下载使用同一个路径
	 * @return
	 */
	private String getUploadDir() {
		Object o = ClientEnvironment.getInstance().get("上传路径");
		if (o == null) {
			return "C:\\";
		} else {
			return o.toString();
		}
	}

	private void setUploadDir(String uploadDir) {
		ClientEnvironment.getInstance().put("上传路径", uploadDir);
	}

	private void dealUpload(File[] _allChooseFiles) throws Exception {
		for (int i = 0; i < _allChooseFiles.length; i++) {
			String str_newFileName = onUploadBtnClicked(_allChooseFiles[i]); //
			String str_str_newFileName_viewed = getViewFileName(str_newFileName); //去掉索引号的文件名
			if (str_newFileName != null && !str_newFileName.equals("")) { //如果成功了,则会返回新的文件名!!
				int k = billListPanel.addEmptyRow(false);
				billListPanel.setValueAt(new RefItemVO(str_newFileName, null, str_str_newFileName_viewed), k, "filename"); //文件名
				billListPanel.setValueAt(new StringItemVO("编辑"), k, "edit"); //编辑
				billListPanel.setValueAt(new StringItemVO("下载"), k, "download"); //下载
				billListPanel.setValueAt(new StringItemVO("删除"), k, "delete"); //删除
			}
		}
	}

	// 上传处理
	private String onUploadBtnClicked(File uploadfile) throws Exception {
		FileInputStream fins = null; //
		try {
			String str_initFileName = uploadfile.getName(); //上传的初始文件名!

			if (uploadfile.length() > (filesize * 1024 * 1024)) {
				MessageBox.show(this, "您上传的文件过大,最大为" + filesize + "MB!");
				return null;
			}
			if (str_initFileName.lastIndexOf(".") < 0 || str_initFileName.lastIndexOf(".") == str_initFileName.length()) {
				MessageBox.show(this, "请上传有后缀名的文件!");
				return null;
			}
			String str_initFileMasterName = null; //文件主名!
			String str_initFileExtName = null; //文件扩展名!
			if (str_initFileName.lastIndexOf(".") > 0) {
				str_initFileMasterName = str_initFileName.substring(0, str_initFileName.lastIndexOf(".")); //文件主名
				str_initFileExtName = str_initFileName.substring(str_initFileName.lastIndexOf(".") + 1, str_initFileName.length()); //文件的扩展名
				for (int i = 0; i < str_initFileExtName.length(); i++) { //扩展名必须是英文,即不允许有中文!
					if (!((str_initFileExtName.charAt(i) >= 'a' && str_initFileExtName.charAt(i) <= 'z') || (str_initFileExtName.charAt(i) >= 'A' && str_initFileExtName.charAt(i) <= 'Z'))) {
						MessageBox.show(this, "请上传有合法后缀名的文件(必须是英文)!"); //
						return null;
					}
				}
			}
			TBUtil tbUtil = new TBUtil(); //
			int li_fileGBLength = tbUtil.getStrUnicodeLength(str_initFileName); //
			if (li_fileGBLength > 125) { //125
				if (JOptionPane.showConfirmDialog(this, "你上传的文件过长,系统将自动截取前120位字节(一个汉字算2个字节)!\r\n你是否真的想继续?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
					return null;
				}
			}

			int filelength = new Long(uploadfile.length()).intValue(); //文件大小!
			byte[] filecontent = new byte[filelength]; //一下子读进文件!!!
			fins = new FileInputStream(uploadfile); //创建文件流!
			fins.read(filecontent);
			ClassFileVO filevo = new ClassFileVO(); //
			filevo.setByteCodes(filecontent); //设置字节!
			String str_newFileName = "";
			str_initFileMasterName = tbUtil.subStrByGBLength(str_initFileMasterName, 120, true); //截取前120位(即60个汉字),如果真截取了,则最后补上省略号!!!

			boolean isConvertFileName = uCDfVO.getConfBooleanValue("文件名是否转码", true); //
			String str_uploaddir = uCDfVO.getConfValue("文件存储子路径", ""); //
			if (!isConvertFileName) { //如果不转码!!
				filevo.setClassFileName(str_uploaddir + "/" + str_initFileMasterName + "." + str_initFileExtName); //文件名
				str_newFileName = UIUtil.uploadFileFromClient(filevo, false);
			} else { //如果文件名需要转码,即要换成16进制码!!
				StringBuilder sb_fileName = new StringBuilder(); //
				sb_fileName.append(str_uploaddir + "/"); //路径
				sb_fileName.append(tbUtil.getCurrDate(false, true) + "/"); //日期目录,日期不带中杠!!!
				sb_fileName.append(tbUtil.convertStrToHexString(getFileNo() + str_initFileMasterName)); //对主名进行16进制转换!!
				sb_fileName.append("."); //主名与扩展名的连接点!!!
				sb_fileName.append(str_initFileExtName); //原来的扩展名
				filevo.setClassFileName(sb_fileName.toString()); //设置文件名!
				str_newFileName = UIUtil.uploadFileFromClient(filevo);
			}
			fins.close(); //
			return str_newFileName;
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
			return null;
		} finally {
			try {
				if (fins != null) {
					fins.close(); //
				}
			} catch (Exception e) {
				e.printStackTrace(); //
			}
		}
	}

	/**
	 * 批量下载文件.. 要有重名是否覆盖的提示!!!
	 */
	private void onBatchDownLoadFile() {
		try {
			BillVO[] billVOs = this.billListPanel.getAllBillVOs(); //
			if (billVOs == null || billVOs.length == 0) {
				MessageBox.show(this, "没有需要下载的文件"); //
				return;
			}
			String[] filenames = new String[billVOs.length];
			for (int i = 0; i < billVOs.length; i++) {
				filenames[i] = billVOs[i].getStringValue("filename");
			}
			File f = null;
			JFileChooser chooser = null;
			if (ClientEnvironment.str_downLoadFileDir.endsWith("/") || ClientEnvironment.str_downLoadFileDir.endsWith("\\")) {
				f = new File(ClientEnvironment.str_downLoadFileDir);
				chooser = new JFileChooser(f); //
			} else {
				f = new File(ClientEnvironment.str_downLoadFileDir + "/");
				chooser = new JFileChooser(f);
			}
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setDialogTitle("选择一个目录,批量将文件下载到该目录下!"); //
			int li_rewult = chooser.showSaveDialog(this.parentBillPanel == null ? (this.cardCompent == null ? this : this.cardCompent) : this.parentBillPanel);
			if (li_rewult == JFileChooser.CANCEL_OPTION) {
				return;// 点击取消什么也不做
			}

			if (chooser.getSelectedFile().isFile()) {
				MessageBox.show(this, "批量下载时必须选择一个目录,系统将会把所有文件都下载该目录下!!"); //
				return;
			}
			String str_pathdir = chooser.getSelectedFile().getAbsolutePath(); // ;
			if (str_pathdir.endsWith("\\")) {
				str_pathdir = str_pathdir.substring(0, str_pathdir.length() - 1); //
			}

			// 循环下载所有文件
			for (int i = 0; i < filenames.length; i++) {
				if (filenames[i].indexOf("/") != -1) {
					UIUtil.downLoadFile("/upload", filenames[i], false, str_pathdir, getViewFileName(filenames[i]), true);
				} else {
					UIUtil.downLoadFile("/upload", filenames[i], false, str_pathdir, filenames[i], true); // 下载文件
				}
			}
			ClientEnvironment.str_downLoadFileDir = str_pathdir;
			MessageBox.show(this, "下载" + filenames.length + "个文件到目录[" + str_pathdir + "]下成功!!!"); ////
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * 查看处理历史意见!!!
	 */
	private void onShowDealHist() {
		String str_bno = getDealLogBNO(); //
		if (str_bno == null) {
			MessageBox.show(this, "该控件没有注册处理历史!"); //
			return; //
		}
		OneSQLBillListConfirmDialog dialog = new OneSQLBillListConfirmDialog(this, "select dealtype 处理类型,filename 文件名,username 用户,deptname 机构,dealtime 处理时间 from pub_reffiledeallog where bno='" + str_bno + "' order by id", "处理历史", 700, 400, false); //
		//上面用sql创造BillListPanel，列宽默认为200，但因为列太多，出现滚动条太难看了，需要手动设置一下列宽，李春娟修改
		BillListPanel list = dialog.getBillListPanel();
		if (list.getRowCount() == 0) {//如果没有历史意见直接提示一下，不要弹出空空的列表了【李春娟/2012-03-27】
			MessageBox.show(this, "该控件没有处理历史!"); //
			return; //
		}
		list.setItemWidth("处理类型", 70);
		list.setItemWidth("文件名", 150);
		list.setItemWidth("用户", 130);
		list.setItemWidth("机构", 130);
		list.setItemWidth("处理时间", 130);
		dialog.setVisible(true); //
	}

	/**
	 * 点击超链接,一种是直接点击文件名,在线查看
	 * 一种是点击下载的超链接,下载某个文件
	 * @param _event
	 */
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		BillVO billVO = billListPanel.getBillVO(_event.getRow()); //
		String str_filename = billVO.getStringValue("filename");
		String str_fileViewName = billVO.getStringViewValue("filename"); //
		String filetype = str_filename.substring(str_filename.lastIndexOf(".") + 1, str_filename.length());
		if (_event.getItemkey().equals("filename")) { //如果是直接点击文件名!
			//***************李春娟添加，用于显示水印************************//
			StringBuffer sb_httpurl = new StringBuffer();
			String sql = "select textwater,picwater,picposition from pub_filewatermark  where filename ='" + str_filename + "'"; //查询是否需要显示水印
			String[][] watermsg = null;
			try {
				watermsg = UIUtil.getStringArrayByDS(null, sql);
			} catch (WLTRemoteException e2) {
				e2.printStackTrace();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			if (watermsg != null && watermsg.length > 0) { //如果需要加水印
				if (watermsg[0][0] != null && !"".equals(watermsg[0][0])) {
					sb_httpurl.append("&textwater=" + watermsg[0][0]);
				}
				if (watermsg[0][1] != null && !"".equals(watermsg[0][1])) {
					sb_httpurl.append("&picwater=" + watermsg[0][1]);
				}
				if (watermsg[0][2] != null && !"".equals(watermsg[0][2])) {
					sb_httpurl.append("&picposition=" + watermsg[0][2]);
				}
				if (filetype.equalsIgnoreCase("doc") || filetype.equalsIgnoreCase("docx") || filetype.equalsIgnoreCase("wps") || filetype.equalsIgnoreCase("xls") || filetype.equalsIgnoreCase("xlsx") || filetype.equalsIgnoreCase("ppt") || filetype.equalsIgnoreCase("pptx")) {//邮储项目提出，增加可编辑的文件类型【李春娟/2012-11-06】
					if (str_filename.indexOf("/") != -1) {
						str_filename = str_filename.replaceAll("\\\\", "/");
					}
					OfficeCompentControlVO controlVO = new OfficeCompentControlVO(false, true, true, null); //不能保存
					controlVO.setSubdir("upload"); //更改子目录!!!
					try {
						ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class); //
						String str_sessionId = service.registerOfficeCallSessionID(controlVO); //
						final String str_url = System.getProperty("CALLURL") + "/OfficeViewServlet?RecordID=" + str_filename.substring(0, str_filename.lastIndexOf(".")) + "&filetype=" + filetype.toLowerCase() + "&sessionid=" + str_sessionId + ""; //						

						try {
							Desktop.browse(new URL(str_url + sb_httpurl.toString())); //
						} catch (Exception e1) {
							e1.printStackTrace();
							System.err.println("使用JDIC打开浏览器失败,改用直接调IE进程!!"); //曾遇到有的人的机器很妖,jdic用不了,比如王永龙的机器就曾发生过这个问题!!
							Runtime.getRuntime().exec("explorer.exe \"" + str_url + "\""); //
						} //	
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					MessageBox.show(this, "只有doc,docx,wps,xls,xlsx,ppt,pptx类型的文件才能打开!"); //
				}
				return;
			}
			if (str_filename.endsWith("#frominterface")) {
				String str_filename_temp = str_filename.replaceAll("#frominterface", "");
				String filetitle = str_filename_temp.substring(str_filename_temp.lastIndexOf("/") + 1, str_filename_temp.length());
				String str_url = new TBUtil().getSysOptionStringValue("DownloadFileFromInterceptUrl", null) + "?filename=" + filetitle + "&path=" + str_filename_temp; // "https://biz1.cmbchina.com/AIOWService/Member/DownloadProvider.aspx?;
				try {
					Runtime.getRuntime().exec("explorer.exe \"" + str_url + "\"");
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				UIUtil.openRemoteServerFile("upload", str_filename); //直接在线打开文件,使用Servlet在线输出MIME类型的文件	
			}

		} else if (_event.getItemkey().equals("edit")) { //在线编辑!!!!
			if (filetype.equalsIgnoreCase("doc") || filetype.equalsIgnoreCase("docx") || filetype.equalsIgnoreCase("wps") || filetype.equalsIgnoreCase("xls") || filetype.equalsIgnoreCase("xlsx") || filetype.equalsIgnoreCase("ppt") || filetype.equalsIgnoreCase("pptx")) { //邮储项目提出，增加可编辑的文件类型【李春娟/2012-11-06】
				if (str_filename.indexOf("/") != -1) {
					str_filename = str_filename.replaceAll("\\\\", "/");
				}

				/***
				 * Gwang 2012-4-21修改
				 * 问题:当附件是docx时点[编辑]会报错"文件传输错误!"
				 * 原因:是由于文件名中包括了一层目录如(20120421/N101_CEC4B5B561616161.docx), 但如果是doc类型就是没事, 没搞清千航控件是怎么处理的!
				 * 将文件名和路径分开后以上问题解决!
				 * 并且将附件的编辑方式和文件正文编辑方式统一, 不再弹出IE窗口, 改为Dialog!
				 */
				String path = "", fileName = "";
				if (str_filename != null && str_filename.indexOf("/") != -1) {
					path = str_filename.substring(0, str_filename.lastIndexOf("/"));
					;
					fileName = str_filename.substring(str_filename.lastIndexOf("/") + 1, str_filename.length());
				}
				RefItemVO refItemVO = new RefItemVO(fileName, null, str_fileViewName);
				CommUCDefineVO defVO = new CommUCDefineVO("Office控件");
				defVO.setConfValue("存储目录", "/upload" + path);
				RefDialog_Office refDialog = new RefDialog_Office(null, "编辑", refItemVO, this.billListPanel, defVO); //弹出窗口!!
				refDialog.initialize(); //
				refDialog.setVisible(true);

				//停顿一下再关, 否则窗口会卡死!
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					refDialog = null;
				}

				/*
				OfficeCompentControlVO controlVO = new OfficeCompentControlVO(true, true, true, null); //
				controlVO.setSubdir("upload"); //更改子目录!!!
				try {
					ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class); //
					String str_sessionId = service.registerOfficeCallSessionID(controlVO); //
					final String str_url = System.getProperty("CALLURL") + "/OfficeViewServlet?RecordID=" + str_filename.substring(0, str_filename.lastIndexOf(".")) + "&filetype=" + filetype + "&sessionid=" + str_sessionId + ""; //
					try {
						BrowserService browserservice = (BrowserService) ServiceManager.getService("BrowserService");
						browserservice.show(new URL(str_url), "_blank");//ie6默认在同一窗口打开，这样在文件打开的时候再点击编辑会把打开的界面关闭，见unload()脚本
						//改成这种写法可以指定target，在新窗口打开，解决了这个问题。
						//Desktop.browse(new URL(str_url)); //
					} catch (Exception e1) {
						e1.printStackTrace();
						System.err.println("使用JDIC打开浏览器失败,改用直接调IE进程!!"); //曾遇到有的人的机器很妖,jdic用不了,比如王永龙的机器就曾发生过这个问题!!
						Runtime.getRuntime().exec("explorer.exe \"" + str_url + "\""); //
					} //	
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					recordDealLog("编辑", str_fileViewName); //
				}
				*/
			} else {
				MessageBox.show(this, "只有doc,docx,wps,xls,xlsx,ppt,pptx类型的文件才能在线编辑!"); //
				return;
			}
		} else if (_event.getItemkey().equals("delete")) { //点击删除!!! 需要记录日志!!!
			if (JOptionPane.showConfirmDialog(this, "您是否真的想删除该上传的文件?", "删除文件?", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			} else {
				try {
					FrameWorkMetaDataServiceIfc serives = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //
					boolean del_result = serives.deleteZipFileName(str_filename); //远程删除
				} catch (WLTRemoteException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					recordDealLog("删除", str_fileViewName); //记录日志!!
				}
				billListPanel.removeRow();
				resetHeight(); //
			}
		} else if (_event.getItemkey().equals("download")) { //下载,需要记录日志么??
			if (str_filename.endsWith("#frominterface")) {
				String str_filename_temp = str_filename.replaceAll("#frominterface", "");
				String filetitle = str_filename_temp.substring(str_filename_temp.lastIndexOf("/") + 1, str_filename_temp.length());
				String str_url = new TBUtil().getSysOptionStringValue("DownloadFileFromInterceptUrl", null) + "?filename=" + filetitle + "&path=" + str_filename_temp;
				//String str_url = "https://biz1.cmbchina.com/AIOWService/Member/DownloadProvider.aspx?filename=" + filetitle + "&path=" + str_filename_temp;
				try {
					Runtime.getRuntime().exec("explorer.exe \"" + str_url + "\"");
				} catch (IOException e) {
					e.printStackTrace(); ////
				}
			} else {
				downLoadFile(str_filename); //
			}

		}
	}

	/**
	 * 点击文件名超链接，根据文件名直接弹窗显示文件
	 * 请求文件的url中需要附加参数&directopenfile=XXX，其取值不能为空
	 * @param _filename
	 */
	private void getFile_directly(String _filename) {
		String str_filename = _filename;
		OfficeCompentControlVO controlVO = new OfficeCompentControlVO(this.isOnlineSaveAbled(), true, true, null); //
		controlVO.setSubdir("upload"); // 更改子目录!!!

		try {
			TBUtil tb = new TBUtil();
			ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class);
			String str_sessionId = service.registerOfficeCallSessionID(controlVO); //
			final String str_url = System.getProperty("CALLURL") + "/OfficeViewServlet?RecordID=" + tb.convertStrToHexString(str_filename.substring(0, str_filename.length() - 4)) + "&filetype=" + str_filename.substring(str_filename.length() - 3, str_filename.length()) + "&sessionid=" + str_sessionId + "&directopenfile=3"; //

			//弹窗显示
			BillDialog dialog = new BillDialog(this, 800, 600, 200, 200);
			JScrollPane scrollpanel = new JScrollPane();
			scrollpanel.getViewport().removeAll(); //
			WebBrowser webPanel = new WebBrowser(new java.net.URL(str_url)); //老版本jdic将会在释放该资源时抛出异常（解决方法是升级到新的版本，0.9.5之后的，将true改成false）
			scrollpanel.getViewport().add(webPanel, BorderLayout.CENTER); //
			scrollpanel.getViewport().updateUI();
			dialog.add(scrollpanel);
			dialog.setVisible(true);
			dialog = null;
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param _filename
	 */
	public void downLoadFile(final String _filename) {
		if (_filename == null) {
			MessageBox.show(this, "没有上传附件!"); //
			return; //
		}

		try {
			JFileChooser chooser = new JFileChooser();
			try {
				File f = null;
				if (ClientEnvironment.str_downLoadFileDir.endsWith("/") || ClientEnvironment.str_downLoadFileDir.endsWith("\\")) {
					if (_filename != null && _filename.indexOf("/") != -1) {
						f = new File(new File(ClientEnvironment.str_downLoadFileDir + getViewFileName(_filename)).getCanonicalPath());
					} else
						f = new File(new File(ClientEnvironment.str_downLoadFileDir + _filename).getCanonicalPath());
				} else {
					if (_filename != null && _filename.indexOf("/") != -1) {
						f = new File(new File(ClientEnvironment.str_downLoadFileDir + "/" + getViewFileName(_filename)).getCanonicalPath());
					} else
						f = new File(new File(ClientEnvironment.str_downLoadFileDir + "/" + _filename).getCanonicalPath());
				}
				chooser.setSelectedFile(f); //
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}

			int li_rewult = chooser.showSaveDialog(this.parentBillPanel == null ? (this.cardCompent == null ? this : this.cardCompent) : this.parentBillPanel);
			if (li_rewult == JFileChooser.APPROVE_OPTION) { // 如果点了确定
				final File chooseFile = chooser.getSelectedFile(); // 取得文件路径
				ClientEnvironment.str_downLoadFileDir = chooseFile.getParent();
				if (chooseFile != null) {

					new SplashWindow(this, new AbstractAction() {
						private static final long serialVersionUID = -287905438900197436L;

						public void actionPerformed(ActionEvent e) {
							String str_pathdir = chooseFile.getParent(); //
							if (str_pathdir.endsWith("\\")) {
								str_pathdir = str_pathdir.substring(0, str_pathdir.length() - 1); //
							}
							try {
								// liuxuanfei update start 下载时, 文件选择框, 用户有时候修改文件保存名, 但没有加上扩展名, 系统应该自动加上"已知"扩展名
								// 当然, 如果上传的文件本身就没有扩展名, 系统同样不加扩展名.
								// 这里有一点故意没有去做: 用户修改后写的扩展名和"已知"的扩展名不一样时, 系统没有进行"修正". 考虑到做与不做都具有两面性, 就看以后的客户怎么提了(当然, 还得客户能够提出来)
								// 比如说: 上传的文件是123.txt, 用户在文件选择框将文件名修改为1234.doc, 那么系统是否应该强制加上"已知"的扩展名? 加上之后保存的文件为: 1234.doc.txt
								String chooseFileName = chooseFile.getName();
								if (chooseFileName != null && !chooseFileName.contains(".")) {
									int ipoint = _filename.lastIndexOf(".");
									if (ipoint > 0) {
										chooseFileName = chooseFileName + _filename.substring(ipoint, _filename.length());
									}
								}
								String name = UIUtil.downLoadFile("/upload", _filename, false, str_pathdir, chooseFileName, true); //
								if (name == null || name.trim().equals("")) {
									MessageBox.show(RefFileDealPanel.this, "下载文件不存在!!!"); //
								} else {
									MessageBox.show(RefFileDealPanel.this, "下载文件成功!!!"); //
								}
							} catch (Exception e1) {
								e1.printStackTrace();
								MessageBox.show(RefFileDealPanel.this, "下载文件失败，原因【" + e1.getMessage() + "】!!!", WLTConstants.MESSAGE_ERROR); //
							} // 下载文件
						}
					}, 366, 366);
				}
			}

		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		}
	}

	public BillListPanel getBillListPanel() {
		return billListPanel;
	}

	private void resetHeight() {
		if (cardCompent != null) { //刷新高度
			int li_rows = billListPanel.getRowCount(); //
			if (li_rows == 0) { //如果没有文件的话,则提示没有文件!
				billListPanel.findColumn("filename").setHeaderValue("没有文件"); //重新修改表头名称
			} else {
				billListPanel.findColumn("filename").setHeaderValue("文件列表"); //重新修改表头列的名称
			}
			cardCompent.setPreferredSize(new Dimension((int) cardCompent.getPreferredSize().getWidth(), getComputeRowHeight(li_rows))); //重新计算行高
			cardCompent.updateUI(); //

			//附件是否可以上传多个,  Gwang 2016-07-22 从深农商移迁
			if (!isMultiSel) {
				if (li_rows == 0) {
					btn_upload.setEnabled(true);
				} else {
					btn_upload.setEnabled(false);

				}
			}

		}
	}

	/**
	 * 取得计算后的行高
	 * @return
	 */
	private int getComputeRowHeight(int li_rows) {
		if (billListPanel.getBillListBtnPanel().hasOneButtonVisiable()) {
			return 60 + (22 * li_rows);
		} else {
			return 40 + (22 * li_rows);
		}

	}

	/**
	 * 有时需要文件编辑生成器!!
	 * @return
	 */
	private String getFileNo() {//得到文件编号
		try {
			if (!"".equals(this.uCDfVO.getConfValue("文件编号生成器", ""))) {
				AbstractRefFileNoCreate filenoc = (AbstractRefFileNoCreate) Class.forName(uCDfVO.getConfValue("文件编号生成器")).newInstance();
				return filenoc.getFileNo(this.parentBillPanel);
			}
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	private String getRefFileParam(String whichParam) {
		String returnValue = "";
		if (cardCompent == null || cardCompent.getTempletItemVO() == null || cardCompent.getTempletItemVO().getRefdesc() == null || "".equals(cardCompent.getTempletItemVO().getRefdesc().trim())) {
		} else {
			Object oo = new TBUtil().parseStrAsMap(cardCompent.getTempletItemVO().getRefdesc()).get(whichParam);
			if (oo != null) {
				returnValue = oo.toString();
			}
		}
		return returnValue;
	}

	//记录日志时必须知道该控件是哪个控件,目前的方法是使用表名+主键字段名+主键字段值..
	private String getDealLogBNO() {
		if (this.parentBillPanel == null) {
			return null; //
		}
		Pub_Templet_1VO templetVO = null; //
		String str_pkValue = null; //
		if (parentBillPanel instanceof BillCardPanel) {
			BillCardPanel cardPanel = ((BillCardPanel) parentBillPanel); //
			templetVO = cardPanel.getTempletVO(); //
			BillVO billVO = cardPanel.getBillVO(); //
			if (billVO != null) {
				str_pkValue = billVO.getPkValue(); //
			}
		} else if (parentBillPanel instanceof BillListPanel) {
			BillListPanel listPanel = ((BillListPanel) parentBillPanel); //
			templetVO = ((BillListPanel) parentBillPanel).getTempletVO(); //
			BillVO billVO = listPanel.getSelectedBillVO(); ////
			if (billVO != null) {
				str_pkValue = billVO.getPkValue(); ////
			}
		}
		if (templetVO == null || str_pkValue == null) {
			return null; //
		}

		String str_tableName = templetVO.getSavedtablename(); //表名..
		if (str_tableName == null) {
			return null; //
		}
		String str_PkName = templetVO.getPkname(); //
		if (str_PkName == null || str_PkName.trim().equals("")) {
			str_PkName = "id"; //
		}
		return (str_tableName + "_" + str_PkName + "_" + str_pkValue).toLowerCase(); //拼在一起
	}

	//记录处理日志!
	private void recordDealLog(String _dealType, String _fileName) {
		try {
			String str_btn = getDealLogBNO(); //
			if (str_btn == null) {
				return; //
			}
			String str_loginUserName = ClientEnvironment.getInstance().getLoginUserCode() + "/" + ClientEnvironment.getInstance().getLoginUserName(); //人员名称
			String str_loginUserDeptName = ClientEnvironment.getInstance().getLoginUserDeptName(); //部门名称..
			String str_currtime = new TBUtil().getCurrTime(); //当前时间..
			InsertSQLBuilder isql = new InsertSQLBuilder("pub_reffiledeallog"); //参照文件处理日志
			isql.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_PUB_REFFILEDEALLOG")); //
			isql.putFieldValue("bno", str_btn); //批号,是由表名,主键字段名,主键字段值拼起来的!!
			isql.putFieldValue("dealtype", _dealType); //处理类型,新增/编辑/删除
			isql.putFieldValue("filename", _fileName); //文件名
			isql.putFieldValue("username", str_loginUserName); //处理的用户名
			isql.putFieldValue("deptname", str_loginUserDeptName); //处理的机构名
			isql.putFieldValue("dealtime", str_currtime); //处理时间
			UIUtil.executeUpdateByDS(null, isql.getSQL()); //执行!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}
}