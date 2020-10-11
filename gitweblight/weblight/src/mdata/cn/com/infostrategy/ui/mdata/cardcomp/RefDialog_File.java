/**************************************************************************
 * $RCSfile: RefDialog_File.java,v $  $Revision: 1.12 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.ClassFileVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.UIRefPanel;

public class RefDialog_File extends AbstractRefDialog implements ActionListener {

	private static final long serialVersionUID = 1L; //

	private RefFileDealPanel refFileDealPanel = null; //实际处理的面板
	private RefItemVO allfilename;
	private RefItemVO newfilenameref;
	private Boolean boolean_isEditable = null; //是否可编辑..
	private WLTButton btn_confirm, btn_cancel;
	private CommUCDefineVO uCDfVO; //
	int filesize = TBUtil.getTBUtil().getSysOptionIntegerValue("上传附件大小", 25);//项目中经常有个别人纠结上传附件的大小，故设置一个参数【李春娟/2016-12-26】

	public RefDialog_File(Container _parent, String _title, RefItemVO value, BillPanel _panel, CommUCDefineVO _uCDfVO) {// _parent控件本身，panel是控件所在面板
		super(_parent, _title, value, _panel);
		uCDfVO = _uCDfVO; //
	}

	public void initialize() {
		Object parentContainer = this.getParentContainer(); //
		if (parentContainer != null && (parentContainer instanceof UIRefPanel) && (getInitRefItemVO() == null || getInitRefItemVO().getId() == null)) { //只有在卡片状态下才直接点击,在列表状态下还是打开对话框!!
			this.setShowAfterInitialize(false); //
			dirUploadFile(); //直接弹出文件选择框,上传文件!!
		} else {
			this.setLayout(new BorderLayout());
			refFileDealPanel = new RefFileDealPanel(this.getBillPanel(), uCDfVO); //
			refFileDealPanel.setRefItemVO(this.getInitRefItemVO()); //设置初始值
			refFileDealPanel.setEditabled(getListIsEditabled()); //判断列表下是否可编辑

			if (this.getPubTempletItemVO() != null) {
				int li_width = this.getPubTempletItemVO().getCardwidth().intValue();
				refFileDealPanel.getBillListPanel().setItemWidth("filename", li_width > 240 ? li_width - 100 : li_width); //流程处理界面中最下方附件是用DefaultTMO创建的，卡片宽度默认为145，以前直接减去100，宽度太小了【李春娟/2012-05-03】
			}

			JPanel tempPanel = new WLTPanel(WLTPanel.INCLINE_NW_TO_SE, new BorderLayout(), LookAndFeel.defaultShadeColor1); //
			tempPanel.add(refFileDealPanel); //
			tempPanel.add(getSouthPanel(), BorderLayout.SOUTH); //

			this.getContentPane().add(tempPanel, BorderLayout.CENTER);
			if (this.getInitRefItemVO() == null || this.getInitRefItemVO().getId() == null) {
				//refFileDealPanel.onUploadFile(); //本来想如果为空,则直接弹出选择文件框的!!
			}
		}
	}

	/**
	 * 按钮栏
	 * @return
	 */
	private JPanel getSouthPanel() {
		JPanel panel_south = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 3)); //
		panel_south.setOpaque(false); //透明!!!
		btn_confirm = new WLTButton("确定"); //
		btn_cancel = new WLTButton("取消");
		btn_confirm.addActionListener(this);
		btn_cancel.addActionListener(this); //
		panel_south.add(btn_confirm);
		panel_south.add(btn_cancel);
		return panel_south; //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		}
	}

	private boolean getListIsEditabled() {
		if (this.getPubTempletItemVO() != null) {
			String str_editable = getPubTempletItemVO().getListiseditable(); //
			if (str_editable != null && str_editable.equalsIgnoreCase("4")) {
				return false;
			}
		}
		return true;
	}

	//直接上传文件!!
	private void dirUploadFile() {
		try {
			JFileChooser chooser = new JFileChooser();
			chooser.setMultiSelectionEnabled(true); //可以多选!!招行提的,很有价值!!
			chooser.setCurrentDirectory(new File(getUploadDir()));
			BillPanel billPanel = ((UIRefPanel) this.getParentContainer()).getBillPanelFrom(); //
			int result = chooser.showOpenDialog(billPanel == null ? this.getParentContainer() : billPanel); //防止在最右下角弹出!
			if (result == 0 && chooser.getSelectedFile() != null) {
				setUploadDir(chooser.getSelectedFile().getParent());
			} else {
				this.setCloseType(BillDialog.CANCEL); //直接关闭!!
				this.dispose(); //因为它还从没打开过,关闭会有问题吗?
				return;
			}
			final File[] allChooseFiles = chooser.getSelectedFiles(); //
			StringBuilder sb_ids = new StringBuilder(); //
			StringBuilder sb_names = new StringBuilder(); //
			for (int i = 0; i < allChooseFiles.length; i++) {
				String str_newFileName = realUploadFile(allChooseFiles[i]); //上传文件!!!
				if (str_newFileName == null || "".equals(str_newFileName)) {//如果名称为空，有可能因为文件过大或传输错误导致未上传，则直接跳过，否则会添加一条null的记录【李春娟/2012-05-03】
					continue;
				}
				String str_str_newFileName_viewed = getViewFileName(str_newFileName); //去掉索引号的文件名
				sb_ids.append(str_newFileName + ";"); //
				sb_names.append(str_str_newFileName_viewed + ";"); //
			}
			RefItemVO tmpRefVO = new RefItemVO(sb_ids.toString(), null, sb_names.toString()); ////创建参数VO
			newfilenameref = tmpRefVO; //
			this.setCloseType(BillDialog.CONFIRM);
			this.dispose();
		} catch (Exception _ex) {
			MessageBox.showException(this.getParentContainer(), _ex); ////
		}
	}

	private String realUploadFile(File uploadfile) throws Exception {
		try {
			FileInputStream fins = new FileInputStream(uploadfile);
			if (uploadfile.length() > (filesize * 1024 * 1024)) {
				MessageBox.show(getParentContainer(), "您上传的文件过大,最大为" + filesize + "MB!");
				return null;
			}
			if (uploadfile.getName().lastIndexOf(".") < 0 || uploadfile.getName().lastIndexOf(".") == uploadfile.getName().length()) {
				MessageBox.show(getParentContainer(), "请上传有后缀名的文件!");
				return null;
			}
			if (uploadfile.getName().lastIndexOf(".") > 0) {
				String houzhui = uploadfile.getName().substring(uploadfile.getName().lastIndexOf(".") + 1, uploadfile.getName().length());
				for (int i = 0; i < houzhui.length(); i++) {
					if (!((houzhui.charAt(i) >= 'a' && houzhui.charAt(i) <= 'z') || (houzhui.charAt(i) >= 'A' && houzhui.charAt(i) <= 'Z'))) {
						MessageBox.show(getParentContainer(), "请上传有合法后缀名的文件!");
						return null;
					}
				}
			}
			int filelength = new Long(uploadfile.length()).intValue();
			byte[] filecontent = new byte[filelength];
			fins.read(filecontent);
			ClassFileVO filevo = new ClassFileVO();
			filevo.setByteCodes(filecontent);
			String str_newFileName = "";
			if ("false".equals(getRefFileParam("文件名是否转码"))) {
				filevo.setClassFileName(getRefFileParam("文件存储子路径") + "/" + uploadfile.getName());
				str_newFileName = UIUtil.uploadFileFromClient(filevo, false);
			} else {
				filevo.setClassFileName(getRefFileParam("文件存储子路径") + "/" + new TBUtil().getCurrDate().replaceAll("-", "") + "/" + new TBUtil().convertStrToHexString(getFileNo() + uploadfile.getName().substring(0, uploadfile.getName().lastIndexOf("."))) + uploadfile.getName().substring(uploadfile.getName().lastIndexOf("."), uploadfile.getName().length()));
				str_newFileName = UIUtil.uploadFileFromClient(filevo);
			}

			fins.close(); //
			return str_newFileName;
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
			return null;
		}
	}

	//如果定义了参数
	private String getRefFileParam(String whichParam) {
		String returnValue = ""; //返回值!!
		Pub_Templet_1_ItemVO templetItemVO = getPubTempletItemVO(); //
		if (templetItemVO == null || templetItemVO.getRefdesc() == null || "".equals(templetItemVO.getRefdesc().trim())) {
		} else {
			Object oo = new TBUtil().parseStrAsMap(templetItemVO.getRefdesc()).get(whichParam);
			if (oo != null) {
				returnValue = oo.toString();
			}
		}
		return returnValue; //
	}

	//取得显示的文件名!即去掉索引号
	private String getViewFileName(String _realFileName) {
		if (_realFileName != null && _realFileName.indexOf("/") != -1) {
			String param = _realFileName.substring(_realFileName.lastIndexOf("/") + 1, _realFileName.length());
			if (param != null && param.startsWith("N")) {
				return (new TBUtil().convertHexStringToStr(param.substring(param.indexOf("_") + 1, param.lastIndexOf("."))) + (param.substring(param.lastIndexOf("."), param.length()))); //
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

	private String getFileNo() {//得到文件编号
		try {
			if (!"".equals(getRefFileParam("文件编号生成器"))) {
				AbstractRefFileNoCreate filenoc = (AbstractRefFileNoCreate) Class.forName(getRefFileParam("文件编号生成器")).newInstance(); //
				return filenoc.getFileNo(this.getBillPanel()); //
			}
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

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

	/**
	 * 确定返回
	 */
	protected void onConfirm() {
		newfilenameref = refFileDealPanel.getAllFileRefItemVOs(); //
		this.setCloseType(BillDialog.CONFIRM);
		this.dispose();
	}

	protected void onCancel() {
		this.setCloseType(BillDialog.CANCEL);
		this.dispose();
	}

	public RefItemVO getReturnRefItemVO() {
		return newfilenameref;
	}

	public int getInitWidth() {
		return 450;
	}

	public int getInitHeight() {
		return 250;
	}

	public RefFileDealPanel getRefFileDealPanel() {
		return refFileDealPanel;
	}

	/*
	 * 附件初始话时设置附件不可编辑
	 */
	public void Editshow(Boolean bool) {
		this.setLayout(new BorderLayout());
		refFileDealPanel = new RefFileDealPanel(this.getBillPanel(), this.uCDfVO); //
		refFileDealPanel.setRefItemVO(this.getInitRefItemVO()); //设置初始值
		refFileDealPanel.setEditabled(bool); //判断列是否可编辑

		if (this.getPubTempletItemVO() != null) {
			int li_width = this.getPubTempletItemVO().getCardwidth().intValue();
			refFileDealPanel.getBillListPanel().setItemWidth("filename", li_width - 100); //
		}

		this.getContentPane().add(refFileDealPanel, BorderLayout.CENTER);
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);

	}

}
