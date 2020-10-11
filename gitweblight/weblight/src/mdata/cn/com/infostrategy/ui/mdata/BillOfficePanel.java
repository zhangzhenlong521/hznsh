package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.net.MalformedURLException;

import javax.swing.JPanel;

import org.jdesktop.jdic.browser.WebBrowser;

import cn.com.infostrategy.to.mdata.OfficeCompentControlVO;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.report.ReportServiceIfc;

public class BillOfficePanel extends JPanel {

	/**�����˼�����ť�Ƿ���ʾ���ֶ�
	 * Ĭ�϶���ʾ�Ұ���ƽ̨���������õ�����ʾ
	 * �������ifselfdescΪtrue
	 * �����Լ��趨������ʾ
	 */
	private static final long serialVersionUID = 5801067482168595005L;
	private String str_filename = null; //
	private WebBrowser wb = null; //
	private boolean editable = false; //�Ƿ�ɱ༭!
	private boolean printable = true; //�Ƿ�ɴ�ӡ!
	private boolean titlebar = false; //�Ƿ���ʾ�˵�!
	private boolean ifshowsave = true;//�Ƿ���ʾ����
	private boolean ifshowprint_tao = true;//�Ƿ���ʾ�״�
	private boolean ifshowprint_all = true;//�Ƿ���ʾȫ��
	private boolean ifshowprint_fen = true;//�Ƿ���ʾ�ִ�
	private boolean ifshowprint = true;//�Ƿ���ʾ��ӡ
	private boolean ifshowwater = true;//�Ƿ���ʾˮӡ
	private boolean ifshowshowcomment = true;//�Ƿ���ʾ��ʾ��ע
	private boolean ifshowhidecomment = true;//������ע
	private boolean ifshowedit = true;//�޶�
	private boolean ifshowshowedit = true;//��ʾ�޶�
	private boolean ifshowhideedit = true;//�����޶�
	private boolean ifshowacceptedit = true;//�����޶�
	private boolean ifshowclose = true;//�ر�
	private boolean ifselfdesc = false;//�Ƿ��Զ���
	private boolean ifshowsaveas = false; //�Ƿ���ʾ���Ϊ
	private String fromClientDir = null; //�Ƿ�ӿͻ�������ҳ��
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

			//ע��Session,���涨�����Ƿ�ɱ༭,�ɴ�ӡ,��ǩ�滻��,�Ժ󻹿��ܸ���!!
			try {
				if (this.controlVO == null) { //�������ǩ�滻��Hash��,����ע��һ���Ự,Ȼ��sessionID�ٴ���ȥ!
					controlVO = new OfficeCompentControlVO(); //����һ��������.
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
				//����Ƿ������ľ���·�� Gwang 2012-09-17�޸�
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
