package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.net.MalformedURLException;

import javax.swing.JPanel;

import org.jdesktop.jdic.browser.WebBrowser;

import cn.com.infostrategy.to.mdata.OfficeCompentControlVO;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.report.ReportServiceIfc;

public class BillOfficePanel extends JPanel {

	/**增加了几个按钮是否显示的字段
	 * 默认都显示且按照平台参数的配置的来显示
	 * 如果设置ifselfdesc为true
	 * 则按照自己设定的来显示
	 */
	private static final long serialVersionUID = 5801067482168595005L;
	private String str_filename = null; //
	private WebBrowser wb = null; //
	private boolean editable = false; //是否可编辑!
	private boolean printable = true; //是否可打印!
	private boolean titlebar = false; //是否显示菜单!
	private boolean ifshowsave = true;//是否显示保存
	private boolean ifshowprint_tao = true;//是否显示套打
	private boolean ifshowprint_all = true;//是否显示全打
	private boolean ifshowprint_fen = true;//是否显示分打
	private boolean ifshowprint = true;//是否显示打印
	private boolean ifshowwater = true;//是否显示水印
	private boolean ifshowshowcomment = true;//是否显示显示批注
	private boolean ifshowhidecomment = true;//隐藏批注
	private boolean ifshowedit = true;//修订
	private boolean ifshowshowedit = true;//显示修订
	private boolean ifshowhideedit = true;//隐藏修订
	private boolean ifshowacceptedit = true;//接收修订
	private boolean ifshowclose = true;//关闭
	private boolean ifselfdesc = false;//是否自定义
	private boolean ifshowsaveas = false; //是否显示另存为
	private String fromClientDir = null; //是否从客户端生成页面
	private String subdir = null; //

	private OfficeCompentControlVO controlVO = null; //

	private boolean isEditSaveAsEditable = true; // liuxuanfei
	private String encryCode = null; // liuxuanfei
	
	public BillOfficePanel() {

	}

	public BillOfficePanel(String _filename) {
		this.str_filename = _filename;
		initialize();
	}

	public BillOfficePanel(String _filename, boolean _editable, boolean _printable) {
		this.str_filename = _filename;
		this.editable = _editable;
		this.printable = _printable;
		initialize();
	}

	public BillOfficePanel(String _filename, String _fromClientDir) {
		this.str_filename = _filename;
		this.fromClientDir = _fromClientDir;
		initialize();
	}

	public BillOfficePanel(String _filename, boolean _editable, boolean _printable, String _subdir) {
		this.str_filename = _filename;
		this.editable = _editable;
		this.printable = _printable;
		this.subdir = _subdir;
		initialize();
	}

	public BillOfficePanel(String _filename, OfficeCompentControlVO _controlVO) {
		this.str_filename = _filename;
		this.controlVO = _controlVO;
		initialize();
	}

	public void initialize() {
		this.setLayout(new BorderLayout()); //
		try {
			String str_recordID = str_filename.substring(0, str_filename.lastIndexOf(".")); //
			String str_filetype = str_filename.substring(str_filename.lastIndexOf(".") + 1, str_filename.length()); //
			StringBuffer sb_url = new StringBuffer(); //
			sb_url.append(System.getProperty("URL") + "/OfficeViewServlet?"); //
			sb_url.append("RecordID=" + str_recordID); //
			sb_url.append("&filetype=" + str_filetype); //

			if (fromClientDir != null && !"".equals("fromClientDir")) {
				sb_url.append("&fromclientdir=" + fromClientDir); //
			}

			//注册Session,里面定义了是否可编辑,可打印,书签替换等,以后还可能更多!!
			try {
				if (this.controlVO == null) { //如果有书签替换的Hash表,则先注册一个会话,然后将sessionID再传过去!
					controlVO = new OfficeCompentControlVO(); //创建一个控制类.
					controlVO.setEditable(this.editable); //
					controlVO.setPrintable(this.printable); //
					controlVO.setSubdir(this.subdir); //
					controlVO.setTitlebar(this.titlebar);
					controlVO.setIfshowacceptedit(this.ifshowacceptedit);
					controlVO.setIfshowclose(this.ifshowclose);
					controlVO.setIfshowedit(this.ifshowedit);
					controlVO.setIfshowhidecomment(this.ifshowhidecomment);
					controlVO.setIfshowhideedit(this.ifshowhideedit);
					controlVO.setIfshowprint(this.ifshowprint);
					controlVO.setIfshowprint_all(this.ifshowprint_all);
					controlVO.setIfshowprint_fen(this.ifshowprint_fen);
					controlVO.setIfshowprint_tao(this.ifshowprint_tao);
					controlVO.setIfshowsave(this.ifshowsave);
					controlVO.setIfshowshowcomment(this.ifshowshowcomment);
					controlVO.setIfshowshowedit(this.ifshowshowedit);
					controlVO.setIfshowwater(this.ifshowwater);
					controlVO.setIfselfdesc(this.ifselfdesc);
					controlVO.setSaveas(this.ifshowsaveas);
					controlVO.setEditSaveAsEditable(this.isEditSaveAsEditable); // liuxuanfei
					controlVO.setEncryCode(this.getEncryCode());  //
				}
				ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class); //
				String str_sessionId = service.registerOfficeCallSessionID(controlVO); //
				sb_url.append("&sessionid=" + str_sessionId); //
				//如果是服务器的绝对路径 Gwang 2012-09-17修改
				if (controlVO.isAbsoluteSeverDir()) {
					sb_url.append("&isAbsoluteSeverDir=Y");
				}
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}
			wb = new WebBrowser(new java.net.URL(sb_url.toString())); //URL = http://127.0.0.1:9001/tcm3
			this.add(wb, BorderLayout.CENTER);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		//		OfficeCompentControlVO controlVO=new OfficeCompentControlVO();
		//		BillOfficeDialog dialog=new BillOfficeDialog(this.getParent(),str_filename , controlVO);
		//		dialog.invalidate();
		//		dialog.setVisible(true);

	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public void setPrintable(boolean printable) {
		this.printable = printable;
	}

	public void setSubdir(String subdir) {
		this.subdir = subdir;
	}

	public void setFilename(String filename) {
		this.str_filename = filename;
	}

	public void setFromClientDir(String fromClientDir) {
		this.fromClientDir = fromClientDir;
	}

	public WebBrowser getWebBrowser() {
		return wb;
	}

	public boolean isIfshowclose() {
		return ifshowclose;
	}

	public void setIfshowclose(boolean ifshowclose) {
		this.ifshowclose = ifshowclose;
	}

	public boolean isIfshowsave() {
		return ifshowsave;
	}

	public void setIfshowsave(boolean ifshowsave) {
		this.ifshowsave = ifshowsave;
	}

	public boolean isIfshowprint_tao() {
		return ifshowprint_tao;
	}

	public void setIfshowprint_tao(boolean ifshowprint_tao) {
		this.ifshowprint_tao = ifshowprint_tao;
	}

	public boolean isIfshowprint_all() {
		return ifshowprint_all;
	}

	public void setIfshowprint_all(boolean ifshowprint_all) {
		this.ifshowprint_all = ifshowprint_all;
	}

	public boolean isIfshowprint_fen() {
		return ifshowprint_fen;
	}

	public void setIfshowprint_fen(boolean ifshowprint_fen) {
		this.ifshowprint_fen = ifshowprint_fen;
	}

	public boolean isIfshowprint() {
		return ifshowprint;
	}

	public void setIfshowprint(boolean ifshowprint) {
		this.ifshowprint = ifshowprint;
	}

	public boolean isIfshowwater() {
		return ifshowwater;
	}

	public void setIfshowwater(boolean ifshowwater) {
		this.ifshowwater = ifshowwater;
	}

	public boolean isIfshowshowcomment() {
		return ifshowshowcomment;
	}

	public void setIfshowshowcomment(boolean ifshowshowcomment) {
		this.ifshowshowcomment = ifshowshowcomment;
	}

	public boolean isIfshowhidecomment() {
		return ifshowhidecomment;
	}

	public void setIfshowhidecomment(boolean ifshowhidecomment) {
		this.ifshowhidecomment = ifshowhidecomment;
	}

	public boolean isIfshowedit() {
		return ifshowedit;
	}

	public void setIfshowedit(boolean ifshowedit) {
		this.ifshowedit = ifshowedit;
	}

	public boolean isIfshowshowedit() {
		return ifshowshowedit;
	}

	public void setIfshowshowedit(boolean ifshowshowedit) {
		this.ifshowshowedit = ifshowshowedit;
	}

	public boolean isIfshowhideedit() {
		return ifshowhideedit;
	}

	public void setIfshowhideedit(boolean ifshowhideedit) {
		this.ifshowhideedit = ifshowhideedit;
	}

	public boolean isIfshowacceptedit() {
		return ifshowacceptedit;
	}

	public void setIfshowacceptedit(boolean ifshowacceptedit) {
		this.ifshowacceptedit = ifshowacceptedit;
	}

	public boolean isIfselfdesc() {
		return ifselfdesc;
	}

	public void setIfselfdesc(boolean ifselfdesc) {
		this.ifselfdesc = ifselfdesc;
	}

	public boolean isEditSaveAsEditable() {
		return isEditSaveAsEditable;
	}

	public void setEditSaveAsEditable(boolean isEditSaveAsEditable) {
		this.isEditSaveAsEditable = isEditSaveAsEditable;
	}

	public String getEncryCode() {
		return encryCode;
	}

	public void setEncryCode(String encryCode) {
		this.encryCode = encryCode;
	}
}
