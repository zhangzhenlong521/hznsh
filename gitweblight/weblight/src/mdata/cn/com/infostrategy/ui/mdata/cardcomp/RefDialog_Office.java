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
 * Office��,���ǵ���һ��Dialog������ֱ��Ƕ��һ�������,����������ֵ�����ǧ��/����Office�ؼ�!!!
 * ���������ʱ��̫�ȶ�,ԭ������ͬʱʹ������������������,һ����JDIC,�е�����ΪĬ�����������IE,��򲻿�! һ����������Office�ؼ�,�е��˻���Ϊ������İ�ȫ���ö����ز���Activex,���߼���ʱ�ǳ���!!
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
	int closeType = -1;//���������Ļ������ùر�״̬Ϊ1
	private RefItemVO currRefItemVO = null; //
	private RefItemVO returnRefItemVO = null; //
	private BillPanel billPanel; //
	private boolean issave = false;
	private String imgfromtype = null;

	private String str_officeActivexType = null; //�ؼ�����!!
	private String str_templetFileName = null; //ģ���ļ�����

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

		//�ؼ�����!!
		str_officeActivexType = dfvo.getConfValue("�ؼ�����"); //����û�ж���
		if (str_officeActivexType == null) { //���û����,��ʹ��WebLight.xml�ж����,�����Զ�����ͻ��˵�ϵͳ������!!
			str_officeActivexType = System.getProperty("OFFICEACTIVEXTYPE"); //
			if (str_officeActivexType == null) { //�����û����,��ʹ��Ĭ�ϵ�"ǧ��"
				str_officeActivexType = "ǧ��"; //
			}
		}
		str_templetFileName = dfvo.getConfValue("ģ���ļ���");
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
				String str_tablename = cardPanel.getTempletVO().getSavedtablename(); //����
				String str_pkvalue = cardPanel.getRealValueAt(cardPanel.getTempletVO().getPkname()); //
				String realValueK = cardPanel.getRealValueAt(refPanel.getItemKey()) == null ? "" : cardPanel.getRealValueAt(refPanel.getItemKey());
				str_recordID = str_tablename + "_" + refPanel.getItemKey() + "_" + str_pkvalue + "." + getType(dfvo.getConfValue("�ļ�����"), realValueK.substring(realValueK.lastIndexOf(".") + 1)); ////
			} else if (billPanel instanceof BillListPanel) {
				BillListPanel listPanel = (BillListPanel) billPanel; //
				String str_tablename = listPanel.getTempletVO().getSavedtablename(); //����
				String str_pkvalue = "" + listPanel.getValueAt(listPanel.getSelectedRow(), listPanel.getTempletVO().getPkname()); //
				String realValueK = listPanel.getSelectedBillVO().getRealValue(refPanel.getItemKey()) == null ? "" : listPanel.getSelectedBillVO().getRealValue(refPanel.getItemKey());
				str_recordID = str_tablename + "_" + refPanel.getItemKey() + "_" + str_pkvalue + "." + getType(dfvo.getConfValue("�ļ�����"), realValueK.substring(realValueK.lastIndexOf(".") + 1)); //
			}
			try {
				FrameWorkMetaDataServiceIfc serives = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //
				serives.deleteOfficeFileName(str_recordID);//Զ��ɾ��
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		String str_fileid = str_recordID.substring(0, str_recordID.lastIndexOf(".")); ////
		StringBuffer sb_httpurl = new StringBuffer(System.getProperty("URL") + "/OfficeViewServlet?RecordID=" + str_fileid); //
		if (addwatermark) { //�½����ļ����ò�ѯ����
			//��ˮӡ��ȡ???�������ӵ��߼�!!! ΪʲôҪ��һ������? ������ƿ϶�����ԭ���!! ������Ҫ��겹��ע��!!!
			//��겹�䣺��Ϊˮӡ��ˮӡ���ֺ�ˮӡͼƬ������ϵͳ�в��������ļ���Ҫ��ˮӡ������ˮӡͼƬ��λ�ò�����������Ҫ����һ��������¼��Щ��Ϣ��
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

		if (str_officeActivexType.indexOf("���") == 0) {
			sb_httpurl.append("&OFFICEACTIVEXTYPE=GOLDGRID"); //	
			if (str_officeActivexType.equals("���")) {
				sb_httpurl.append("&ISFLOAT=N"); //
			} else {
				sb_httpurl.append("&ISFLOAT=Y"); //
			}
		} else if (str_officeActivexType.indexOf("ǧ��") == 0) { //�����ǧ����ͷ��
			sb_httpurl.append("&OFFICEACTIVEXTYPE=NTKO"); //	
			if (str_officeActivexType.equals("ǧ��")) { //
				sb_httpurl.append("&ISFLOAT=N"); //
			} else {
				sb_httpurl.append("&ISFLOAT=Y"); //
			}
		}

		//�Զ������ļ���׺���ж��ļ�����
		sb_httpurl.append("&filetype=" + str_recordID.substring(str_recordID.lastIndexOf(".") + 1)); //�ļ�����!!
		if (str_templetFileName != null && !str_templetFileName.trim().equals("")) { //���������ģ���ļ�
			int li_pos = str_templetFileName.lastIndexOf("."); //
			String str_templetfilename = ""; //
			if (li_pos > 0) {
				str_templetfilename = str_templetFileName.substring(0, li_pos); //
			} else {
				str_templetfilename = str_templetFileName; //
			}
			sb_httpurl.append("&templetfilename=" + str_templetfilename); //
		}

		OfficeCompentControlVO controlVO = new OfficeCompentControlVO(); //�������ƶ������!!!Ĭ���ǿɱ༭,�ɴ�ӡ��
		if (refPanel != null) {
			controlVO.setEditable(refPanel.getItemEditable()); //��ǰ�Ǹ��ݿ�Ƭ��״̬���жϵ�,���ڸĳ���ֱ�Ӹ��ݿؼ�����Ŀɱ༭״̬���ж�!!!
		}

		controlVO.setSubdir(dfvo.getConfValue("�洢Ŀ¼")); //������Ŀ¼!!!���� [/help]
		if (!(dfvo.getConfValue("��ǩ������", "").trim().equals(""))) { //�����������ǩ�Ĵ�����...
			try {
				RefDialog_Office_BookMarkCreaterIFC creater = (RefDialog_Office_BookMarkCreaterIFC) (Class.forName(dfvo.getConfValue("��ǩ������")).newInstance()); //
				controlVO = creater.createBookMarkReplaceMap(billPanel); //
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}
		}
		controlVO.setIfshowprint(true);
		controlVO.setPrintable(true);
		try {
			ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class); //
			String str_sessionId = service.registerOfficeCallSessionID(controlVO); //ע��Ự
			sb_httpurl.append("&sessionid=" + str_sessionId); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}

		/***
		 * Gwang 2012-09-17�޸�
		 * Office�ؼ��м���"�Ƿ����·��"����, Ĭ��ΪFlase
		 * ��Ϊϵͳ�е�һЩ�ĵ�����Ҫ���Ű�װ��һ��װ��ָ��Ŀ¼, ��������ĵ�. 
		 * ����Ҫ�ֶ����ƹ�ȥ����ϵͳ����ʱ�ĸ��Ӷ�, �ر����ڲ�Ʒ��װ��ʱ 
		 */
		boolean isAbsoluteSeverDir = dfvo.getConfBooleanValue("�Ƿ����·��", false);
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
		btn_confirm = new WLTButton("�ر�"); //
		btn_confirm.addActionListener(this); //
		panel.add(btn_confirm); //
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) { //����ǹرհ�ť
			onConfirm();
		}
	}

	private boolean dealBeforeClose() {
		if (JOptionPane.showConfirmDialog(this, "��ȷ��Ҫ�ر����˳���?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) { //��ʾ�Ƿ�ر�ǰ����һ���ļ�!
			return false; //
		}
		if (issave) {
			String str_name = "����鿴"; //
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
	 * ��WebBrowse��ͨ��JavaScript����Swing�еĸú���,ԭ������JS�иı䴰�ڱ���,Ȼ�������������仯�¼���ͨ����������������!
	 * @param _type
	 */
	public void callSwingFunctionByWebBrowse(String _type) {
		if (_type.equals("button_save_click")) {
			clickSaveButton();
		}
	}

	/**
	 * ��Swing�е���WebBrowse�е�JavaScript����,��Html����һ��������swingCall,��������д����,Ȼ������ĵ��û��Զ����ݹ�ȥ!!
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
		if ("all".equals(_defineType)) { //�����all,��ʹ��ʵ���ļ����!
			if (type_ != null && !type_.equals("")) {
				return type_; //
			} else {
				return "doc"; //
			}
		} else { //�������all
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
