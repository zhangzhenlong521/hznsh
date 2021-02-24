package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jdesktop.jdic.browser.WebBrowser;
import org.jdesktop.jdic.browser.WebBrowserEvent;
import org.jdesktop.jdic.browser.WebBrowserListener;

import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.OfficeCompentControlVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;
import cn.com.infostrategy.ui.mdata.UIRefPanel;
import cn.com.infostrategy.ui.report.ReportServiceIfc;

/**
 * Office控,就是弹出一个Dialog，里面直接嵌了一个浏览器,浏览器里面又调用了千航/金格的Office控件!!!
 * 这个参照有时不太稳定,原因是他同时使用了两个第三方技术,一个是JDIC,有的人因为默认浏览器不是IE,会打不开! 一个就是用了Office控件,有的人会因为浏览器的安全设置而加载不了Activex,或者加载时非常慢!!
 * @author xch
 *
 */
public class RefDialog_Office extends AbstractRefDialog implements ActionListener {

	private static final long serialVersionUID = 7240993480143688006L;

	private CommUCDefineVO dfvo = null; //
	private String str_recordID = ""; //
	private UIRefPanel refPanel = null; //
	private WLTButton btn_confirm;
	private WebBrowser wb = null; //
	int closeType = -1;//如果保存过的话，设置关闭状态为1
	private RefItemVO currRefItemVO = null; //
	private RefItemVO returnRefItemVO = null; //
	private BillPanel billPanel; //
	private boolean issave = false;
	private String imgfromtype = null;

	private String str_officeActivexType = null; //控件类型!!
	private String str_templetFileName = null; //模板文件名称

	public String getImgfromtype() {
		return imgfromtype;
	}

	public void setImgfromtype(String imgfromtype) {
		this.imgfromtype = imgfromtype;
	}

	public RefDialog_Office(Container _parent, String _title, RefItemVO value, BillPanel _panel, CommUCDefineVO _dfvo) {
		super(_parent, _title, value, _panel);
		this.setSize(1024, 733);
		this.setLocation(0, 0); //
		this.setAddDefaultWindowListener(true); //
		refPanel = (UIRefPanel) _parent; //
		currRefItemVO = value;
		billPanel = _panel;
		dfvo = _dfvo; //

		//控件类型!!
		str_officeActivexType = dfvo.getConfValue("控件厂家"); //看有没有定义
		if (str_officeActivexType == null) { //如果没定义,则使用WebLight.xml中定义的,它会自动进入客户端的系统属性中!!
			str_officeActivexType = System.getProperty("OFFICEACTIVEXTYPE"); //
			if (str_officeActivexType == null) { //如果还没定义,则使用默认的"千航"
				str_officeActivexType = "千航"; //
			}
		}
		str_templetFileName = dfvo.getConfValue("模板文件名");
	}

	public void initialize() {
		this.getContentPane().setLayout(new BorderLayout()); //
		boolean addwatermark = false;
		if (currRefItemVO != null && currRefItemVO.getId() != null) {
			str_recordID = currRefItemVO.getId(); //
			addwatermark = true;
		} else {
			BillPanel billPanel = this.getBillPanel(); //
			if (billPanel instanceof BillCardPanel) {
				BillCardPanel cardPanel = (BillCardPanel) billPanel; //
				String str_tablename = cardPanel.getTempletVO().getSavedtablename(); //表名
				String str_pkvalue = cardPanel.getRealValueAt(cardPanel.getTempletVO().getPkname()); //
				String realValueK = cardPanel.getRealValueAt(refPanel.getItemKey()) == null ? "" : cardPanel.getRealValueAt(refPanel.getItemKey());
				str_recordID = str_tablename + "_" + refPanel.getItemKey() + "_" + str_pkvalue + "." + getType(dfvo.getConfValue("文件类型"), realValueK.substring(realValueK.lastIndexOf(".") + 1)); ////
			} else if (billPanel instanceof BillListPanel) {
				BillListPanel listPanel = (BillListPanel) billPanel; //
				String str_tablename = listPanel.getTempletVO().getSavedtablename(); //表名
				String str_pkvalue = "" + listPanel.getValueAt(listPanel.getSelectedRow(), listPanel.getTempletVO().getPkname()); //
				String realValueK = listPanel.getSelectedBillVO().getRealValue(refPanel.getItemKey()) == null ? "" : listPanel.getSelectedBillVO().getRealValue(refPanel.getItemKey());
				str_recordID = str_tablename + "_" + refPanel.getItemKey() + "_" + str_pkvalue + "." + getType(dfvo.getConfValue("文件类型"), realValueK.substring(realValueK.lastIndexOf(".") + 1)); //
			}
			try {
				FrameWorkMetaDataServiceIfc serives = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //
				serives.deleteOfficeFileName(str_recordID);//远程删除
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		String str_fileid = str_recordID.substring(0, str_recordID.lastIndexOf(".")); ////
		StringBuffer sb_httpurl = new StringBuffer(System.getProperty("URL") + "/OfficeViewServlet?RecordID=" + str_fileid); //
		if (addwatermark) { //新建的文件不用查询！！
			//从水印中取???李春娟后来加的逻辑!!! 为什么要搞一个表呢? 当初设计肯定是有原因的!! 所以需要李春娟补充注释!!!
			//李春娟补充：因为水印分水印文字和水印图片，并且系统中并非所有文件都要有水印，并且水印图片的位置不定，所以需要创建一个表来记录这些信息。
			String sql = "select textwater,picwater,picposition from pub_filewatermark  where filename ='" + str_recordID + "'";
			try {
				String[][] watermsg = UIUtil.getStringArrayByDS(null, sql); //String[][] watermsg = {{"ZZZZZ","sun.gif","340,560"}};
				if (watermsg != null && watermsg.length > 0) {
					if (watermsg[0][0] != null && !"".equals(watermsg[0][0])) {
						sb_httpurl.append("&textwater=" + watermsg[0][0]);
					}
					if (watermsg[0][1] != null && !"".equals(watermsg[0][1])) {
						sb_httpurl.append("&picwater=" + watermsg[0][1]);
					}
					if (watermsg[0][2] != null && !"".equals(watermsg[0][2])) {
						sb_httpurl.append("&picposition=" + watermsg[0][2]);
					}
					if (imgfromtype != null) {
						sb_httpurl.append("&fromtype=" + imgfromtype);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (str_officeActivexType.indexOf("金格") == 0) {
			sb_httpurl.append("&OFFICEACTIVEXTYPE=GOLDGRID"); //	
			if (str_officeActivexType.equals("金格")) {
				sb_httpurl.append("&ISFLOAT=N"); //
			} else {
				sb_httpurl.append("&ISFLOAT=Y"); //
			}
		} else if (str_officeActivexType.indexOf("千航") == 0) { //如果以千航开头的
			sb_httpurl.append("&OFFICEACTIVEXTYPE=NTKO"); //	
			if (str_officeActivexType.equals("千航")) { //
				sb_httpurl.append("&ISFLOAT=N"); //
			} else {
				sb_httpurl.append("&ISFLOAT=Y"); //
			}
		}

		//自动根据文件后缀名判断文件类型
		sb_httpurl.append("&filetype=" + str_recordID.substring(str_recordID.lastIndexOf(".") + 1)); //文件类型!!
		if (str_templetFileName != null && !str_templetFileName.trim().equals("")) { //如果定义了模板文件
			int li_pos = str_templetFileName.lastIndexOf("."); //
			String str_templetfilename = ""; //
			if (li_pos > 0) {
				str_templetfilename = str_templetFileName.substring(0, li_pos); //
			} else {
				str_templetfilename = str_templetFileName; //
			}
			sb_httpurl.append("&templetfilename=" + str_templetfilename); //
		}

		OfficeCompentControlVO controlVO = new OfficeCompentControlVO(); //创建控制定义对象!!!默认是可编辑,可打印的
		if (refPanel != null) {
			controlVO.setEditable(refPanel.getItemEditable()); //以前是根据卡片的状态来判断的,现在改成是直接根据控件本身的可编辑状态来判断!!!
		}

		controlVO.setSubdir(dfvo.getConfValue("存储目录")); //设置子目录!!!比如 [/help]
		if (!(dfvo.getConfValue("书签生成器", "").trim().equals(""))) { //如果定义了书签的创建者...
			try {
				RefDialog_Office_BookMarkCreaterIFC creater = (RefDialog_Office_BookMarkCreaterIFC) (Class.forName(dfvo.getConfValue("书签生成器")).newInstance()); //
				controlVO = creater.createBookMarkReplaceMap(billPanel); //
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}
		}
		controlVO.setIfshowprint(true);
		controlVO.setPrintable(true);
		try {
			ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class); //
			String str_sessionId = service.registerOfficeCallSessionID(controlVO); //注册会话
			sb_httpurl.append("&sessionid=" + str_sessionId); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}

		/***
		 * Gwang 2012-09-17修改
		 * Office控件中加入"是否绝对路径"参数, 默认为Flase
		 * 因为系统中的一些文档必须要随着安装盘一起安装到指定目录, 比如帮助文档. 
		 * 否则还要手动复制过去增加系统部署时的复杂度, 特别是在产品安装盘时 
		 */
		boolean isAbsoluteSeverDir = dfvo.getConfBooleanValue("是否绝对路径", false);
		if (isAbsoluteSeverDir) {
			sb_httpurl.append("&isAbsoluteSeverDir=Y");
		}		
		
		try {
			wb = new WebBrowser(new java.net.URL(sb_httpurl.toString())); //
			wb.addWebBrowserListener(new WebBrowserListener() {
				public void titleChange(WebBrowserEvent arg0) {
					callSwingFunctionByWebBrowse(arg0.getData());
				}

				public void windowClose(WebBrowserEvent arg0) {
				}

				public void documentCompleted(WebBrowserEvent arg0) {
				}

				public void downloadCompleted(WebBrowserEvent arg0) {
				}

				public void downloadError(WebBrowserEvent arg0) {
				}

				public void downloadProgress(WebBrowserEvent arg0) {
				}

				public void downloadStarted(WebBrowserEvent arg0) {
				}

				public void statusTextChange(WebBrowserEvent arg0) {
				}
			});
			this.getContentPane().add(wb, BorderLayout.CENTER); //
			this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} //
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		btn_confirm = new WLTButton("关闭"); //
		btn_confirm.addActionListener(this); //
		panel.add(btn_confirm); //
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) { //如果是关闭按钮
			onConfirm();
		}
	}

	private boolean dealBeforeClose() {
		if (JOptionPane.showConfirmDialog(this, "您确定要关闭与退出吗?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) { //提示是否关闭前保存一下文件!
			return false; //
		}
		if (issave) {
			String str_name = "点击查看"; //
			if (currRefItemVO != null && currRefItemVO.getName() != null) {
				str_name = currRefItemVO.getName(); //
			}
			returnRefItemVO = new RefItemVO(str_recordID, null, str_name); //
			closeType = 1; //
		} else if (currRefItemVO != null && currRefItemVO.getName() != null) {
			returnRefItemVO = new RefItemVO(str_recordID, null, currRefItemVO.getName()); //
		}
		callWebBrowseJavaScriptFunction("closedoc"); //
		return true; //
	}

	@Override
	public boolean beforeWindowClosed() {
		return dealBeforeClose(); //
	}

	public void onConfirm() {
		boolean isCanClose = dealBeforeClose(); //
		if (isCanClose) {
			this.dispose(); //
		}
	}

	/**
	 * 在WebBrowse中通过JavaScript调用Swing中的该函数,原理是在JS中改变窗口标题,然后这里监听标题变化事件，通过标题名称来传递!
	 * @param _type
	 */
	public void callSwingFunctionByWebBrowse(String _type) {
		if (_type.equals("button_save_click")) {
			clickSaveButton();
		}
	}

	/**
	 * 在Swing中调用WebBrowse中的JavaScript函数,在Html中有一个函数叫swingCall,在里面填写代码,然后这里的调用会自动传递过去!!
	 * @param _type
	 */
	public void callWebBrowseJavaScriptFunction(String _type) {
		wb.executeScript("swingCall('" + _type + "');");
	}

	public int getCloseType() {
		return closeType; //
	}

	@Override
	public RefItemVO getReturnRefItemVO() {
		return returnRefItemVO;
	}

	private String getType(String _defineType, String type_) {
		if ("all".equals(_defineType)) { //如果是all,则使用实际文件后辍!
			if (type_ != null && !type_.equals("")) {
				return type_; //
			} else {
				return "doc"; //
			}
		} else { //如果不是all
			if (_defineType != null && !_defineType.trim().equals("")) {
				return _defineType; //
			} else {
				return "doc"; //
			}
		}
	}

	public void clickSaveButton() {
		this.issave = true;
	}

}
