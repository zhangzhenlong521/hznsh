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
 * �ϴ�/�����ļ����յ�ʵ�������
 * ����ǰ���ļ�ѡ���
 * ��Ϊ�����޸ĳ��ڿ�Ƭ�в���ֱ���Բ��յ����ť����ʽ����RefDialog_File������ֱ�����������ֱ�Ӳ���,������ʱ�б��뵥����������ó�����Ȼ����RefDialog_File�뿨Ƭ���ٵ�����!
 * �ڿ�Ƭ�����б�����һ����ͬ��Ч��������Ҫ��̬�޸ĸ߶ȣ����ϴ��ļ�Խ�࣬�߶�Խ�ߣ��Զ��Ÿߣ�����Զ��֤һ����Ŀ��������ļ���
 * ���Լ�Ҫʵ���ڼ���ʱ�޸ĸ߶�,��Ҫʵ��������/ɾ��ʱ�޸ĸ߶ȣ�������һ����ս���ؼ���Ҫ�ȶ�,��ǰ�������ӱ���������ڲ����ȶ�����
 * 
 * Ϊ�˱�֤Linux�µ��ļ��洢�������ļ���������,�������ϴ��ļ��ڷ�����Ŀ¼����16���Ƶ��ļ���,���洢�����ݿ��еĻ���ԭʼ�ļ���(��Ϊ������Ҫ���ݹؼ��ֲ�ѯ)���Ȿ��Ҳ��һ�ּ��ܰ�ȫ����
 * ������Ҫ��ÿؼ��ڴ����ļ���ʱҪ��ͣ�������ܸ��ӣ������׳���һ��Ҫע�⣡��Ҫ���ݾɵ��ļ�����
 * @author xch
 *
 */
public class RefFileDealPanel extends JPanel implements ActionListener, BillListHtmlHrefListener {

	private static final long serialVersionUID = 1L;

	private CardCPanel_FileDeal cardCompent = null; //��Ƭ�еĿؼ�..
	private BillPanel parentBillPanel = null; //���״��ڴ���
	private BillListPanel billListPanel = null;
	private WLTButton btn_upload = null; //�ϴ��ļ��İ�ť!!
	private WLTButton btn_batchdownload = null; //�������صİ�ť!!
	private WLTButton btn_dealHist = null; //�鿴������ʷ
	//private String str_fileName = null; //

	//��Ϊһ���ļ��ϴ�/���ؿؼ�,�������һ����4������...
	private boolean isOnlineLookAbled = true; //�Ƿ�������߲鿴,���ļ����ϵ��Ǹ�������,���߲鿴��ֱ���ڷ������˴�,���߲鿴�ǲ�����༭,�������������������Ϊ�����ǿ������߱���!!!
	private boolean isOnlineSaveAbled = true; //�Ƿ�������߱���,��ʹ��Office�ؼ��Ĺ���ֱ�ӱ��浽��������,���߱���ʱ��ͬʱ�ǿ��Ա༭�뿽����!!!
	private boolean isAddDelAbled = true; //�Ƿ�����ϴ����ļ���ɾ�����ļ�
	private boolean isDownLoadAbled = true; //�Ƿ��������

	private TBUtil tbUtil = new TBUtil(); //
	private AbstractRefFileIntercept intercept = null;
	private CommUCDefineVO uCDfVO; //

	private boolean isMultiSel = true; //�����Ƿ�����ϴ����, ͨ���ؼ�����"�ļ�����"����  Gwang 2016-07-22 ����ũ����Ǩ
	int filesize = TBUtil.getTBUtil().getSysOptionIntegerValue("�ϴ�������С", 25);//��Ŀ�о����и����˾����ϴ������Ĵ�С��������һ�����������/2016-12-26��

	/**
	 * Ĭ�Ϲ��췽�����õ�
	 */
	private RefFileDealPanel() {
	}

	/**
	 * ���췽��
	 * @param initFileNames ��ʼ�����ļ���,�Էָ����ָ��ɶ���ļ�
	 */
	public RefFileDealPanel(BillPanel _billPanel, CommUCDefineVO _uCDfVO) {
		this.parentBillPanel = _billPanel; //
		this.uCDfVO = _uCDfVO; //
		initialize(); //
	}

	/**
	 * ���췽��
	 * @param initFileNames ��ʼ�����ļ���,�Էָ����ָ��ɶ���ļ�
	 */
	public RefFileDealPanel(CardCPanel_FileDeal _cardCompent, BillPanel _billPanel, CommUCDefineVO _uCDfVO) {
		this.cardCompent = _cardCompent; //
		this.parentBillPanel = _billPanel; //
		this.uCDfVO = _uCDfVO; //
		initialize(); //
	}

	/**
	 * ��ʼ��ҳ��..
	 */
	private void initialize() {
		if (this.uCDfVO == null) {
			uCDfVO = new CommUCDefineVO("�ļ�ѡ���"); //
		}
		this.setLayout(new BorderLayout()); //���ò�����
		this.setBackground(LookAndFeel.cardbgcolor); //
		this.setOpaque(false); //͸��
		billListPanel = new BillListPanel(new ServerTMODefine("cn.com.infostrategy.bs.mdata.servertmo.TMO_RefFile")); //�����б�
		billListPanel.setBillListOpaque(false); //͸��
		btn_upload = new WLTButton("�ϴ��ļ�");
		btn_batchdownload = new WLTButton("��������");
		btn_dealHist = new WLTButton("������ʷ"); //
		btn_upload.addActionListener(this); //
		btn_batchdownload.addActionListener(this); //
		btn_dealHist.addActionListener(this); //����
		//û��ʲô�ô��� ȥ�ˡ� Gwang 2016-09-29
		//		//��ҵ���пͻ�����Ҫ��ʾ�ϴ���������ʷ��¼(ʲôʱ��ʲô���ϴ���,�޸���,ɾ���˸���)��
		//		if (TBUtil.getTBUtil().getSysOptionBooleanValue("�ϴ������Ƿ���ʾ������ʷ��ť", true)) { //�ϴ������Ƿ��С�������ʷ����ť
		//			billListPanel.insertBatchBillListButton(new WLTButton[] { btn_upload, btn_batchdownload, btn_dealHist }); //����������ť!!!
		//		} else {
		//			billListPanel.insertBatchBillListButton(new WLTButton[] { btn_upload, btn_batchdownload });
		//		}
		billListPanel.insertBatchBillListButton(new WLTButton[] { btn_upload, btn_batchdownload });
		billListPanel.repaintBillListButton(); //ˢ�°�ť
		billListPanel.addBillListHtmlHrefListener(this); //
		this.add(billListPanel, BorderLayout.CENTER); //
		String interceptname = uCDfVO.getConfValue("������");
		if (interceptname != null && !"".equals(interceptname.trim())) {
			try {
				intercept = (AbstractRefFileIntercept) Class.forName(interceptname.trim()).newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		//��ȡ����"�ļ�����" Gwang 2016-07-22 ����ũ����Ǩ
		if (getRefFileParam("�ļ�����") != null && !getRefFileParam("�ļ�����").equals("")) {
			if (Integer.parseInt(getRefFileParam("�ļ�����").toString()) == 1) {
				isMultiSel = false;
			}
		}
		//��ȡ����"�ļ�����" ����ũ����Ǩ�ؼ��������ϸ�ʽ�����ﴦ��һ�£������¸�ʽ �����/2017-08-18��
		if (isMultiSel) {
			String filecount = uCDfVO.getConfValue("�ļ�����");
			if (filecount != null && "1".equals(filecount.trim())) {
				isMultiSel = false;
			}
		}
	}

	/**
	 * �Ƿ�������߿ɿ�
	 * @return
	 */
	public boolean isOnlineLookAbled() {
		return isOnlineLookAbled;
	}

	/**
	 * �Ƿ�������߱���
	 * @return
	 */
	public boolean isOnlineSaveAbled() {
		return isOnlineSaveAbled;
	}

	/**
	 * �Ƿ��������,ɾ���������Լ����ϴ���ɾ��
	 * @return
	 */
	public boolean isAddDelAbled() {
		return isAddDelAbled;
	}

	/**
	 * �Ƿ��������
	 * @return
	 */
	public boolean isDownLoadAbled() {
		return isDownLoadAbled;
	}

	/**
	 * �����Ƿ�������߲鿴
	 * @param isOnlineLookAbled
	 */
	public void setOnlineLookAbled(boolean isOnlineLookAbled) {
		this.isOnlineLookAbled = isOnlineLookAbled;
	}

	/**
	 * �����Ƿ�������߱���
	 * @param isOnlineSaveAbled
	 */
	public void setOnlineSaveAbled(boolean isOnlineSaveAbled) {
		this.isOnlineSaveAbled = isOnlineSaveAbled;
	}

	/**
	 * �����Ƿ����������ɾ��
	 * @param isAddDelAbled
	 */
	public void setAddDelAbled(boolean _isAddDelAbled) {
		this.isAddDelAbled = _isAddDelAbled;
		if (isAddDelAbled) {
			this.billListPanel.setBillListBtnVisiable("�ϴ��ļ�", true); //
			this.billListPanel.setItemVisible("delete", true); //
		} else {
			this.billListPanel.setBillListBtnVisiable("�ϴ��ļ�", false); //
			this.billListPanel.setItemVisible("delete", false); //
		}
	}

	/**
	 * �����Ƿ��������
	 * @param isDownLoadAbled
	 */
	public void setDownLoadAbled(boolean _isDownLoadAbled) {
		this.isDownLoadAbled = _isDownLoadAbled;
		if (isDownLoadAbled) {
			this.billListPanel.setBillListBtnVisiable("��������", true); //
			this.billListPanel.setItemVisible("download", true); //
		} else {
			this.billListPanel.setBillListBtnVisiable("��������", false); //
			this.billListPanel.setItemVisible("download", false); //
		}
	}

	/**
	 * �����Ƿ�ɱ༭,��ʵ��ֱ����4������..
	 * @param _editabled
	 */
	public void setEditabled(boolean _editabled) {
		if (_editabled) {
			setOnlineLookAbled(true); //
			setOnlineSaveAbled(true); //
			setAddDelAbled(true);
			setDownLoadAbled(true); //
			this.billListPanel.setItemVisible("edit", true); //
			boolean cansort = tbUtil.getSysOptionBooleanValue("�����Ƿ������", true);//�����Ƿ�����򣬶�ѡ�ļ��ϴ��ܿ�����Ҫ�����ȼ�����Ĭ��Ϊ���������/2015-06-30��
			if (cansort && isMultiSel) {//�ɶ�ѡ������²ż�����ť�����/2018-08-18��
				WLTButton btn = billListPanel.getBillListBtn("����");
				if (btn == null || !"����".equals(btn.getText())) {//�Ƿ��Ѿ���Ӹð�ť�����/2015-12-24��
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

		// �ļ�����1��ʱ����ʾ�������ذ�ť
		if (billListPanel.getRowCount() > 1) {
			this.billListPanel.setBillListBtnVisiable("��������", true);
		} else {
			this.billListPanel.setBillListBtnVisiable("��������", false);
		}
	}

	/**
	 * �����ť�¼���������
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_upload) { //�ϴ�
			onUploadFile(); //�ϴ��ļ�
		} else if (e.getSource() == btn_batchdownload) {
			onBatchDownLoadFile(); //���������ļ�
		} else if (e.getSource() == btn_dealHist) {
			onShowDealHist(); //
		}
	}

	/**
	 * ȡ�������ļ��ķ���ֵ..
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
	 * ����ֵ.
	 * @param _refVO
	 */
	public void setRefItemVO(RefItemVO _refVO) {
		this.billListPanel.clearTable(); //�������ǰ������
		if (_refVO == null) {
			return;
		}

		String str_id = _refVO.getId(); //
		String[] str_ids = tbUtil.split(str_id, ";"); //�ļ��嵥�Ǵ������ŵ�ʵ���ļ���
		for (int i = 0; i < str_ids.length; i++) {
			int k = billListPanel.addEmptyRow(false);
			//String str_ViewFileName = getViewFileName(str_items[i]); // 
			billListPanel.setValueAt(new RefItemVO(str_ids[i], null, getViewFileName(str_ids[i])), k, "filename"); //�ļ���
			billListPanel.setValueAt(new StringItemVO("�༭"), k, "edit"); //�༭
			billListPanel.setValueAt(new StringItemVO("����"), k, "download"); //����
			billListPanel.setValueAt(new StringItemVO("ɾ��"), k, "delete"); //ɾ��
		}
		resetHeight(); //ˢ�¸߶�
	}

	//ȡ����ʾ���ļ���!��ȥ��������
	private String getViewFileName(String _realFileName) {
		if (_realFileName != null && _realFileName.indexOf("/") != -1) {
			String param = _realFileName.substring(_realFileName.lastIndexOf("/") + 1, _realFileName.length());
			if (param != null && param.startsWith("N")) {
				TBUtil tbUtil = new TBUtil(); //
				int li_extentNamePos = param.lastIndexOf("."); //�ļ�����չ����λ��!�������и���!������ҵ��Ŀ��������ļ��ǴӺ�̨�����!!Ҳ������û��꡵�!!���Ա���!
				if (li_extentNamePos > 0) {
					return tbUtil.convertHexStringToStr(param.substring(param.indexOf("_") + 1, li_extentNamePos)) + param.substring(li_extentNamePos, param.length()); //
				} else {
					return tbUtil.convertHexStringToStr(param.substring(param.indexOf("_") + 1, param.length())); ////
				}
			} else {
				return param; //��ǰ�İ汾Ҳ�д�·���ģ�
			}
		} else {
			if (_realFileName == null || _realFileName.indexOf("_") < 0) {
				return _realFileName; //
			}
			return _realFileName.substring(_realFileName.indexOf("_") + 1, _realFileName.length()); //
		}
	}

	public void clearAllFile() {
		this.billListPanel.clearTable(); //��ձ������
	}

	/**
	 * �ϴ��ļ�..
	 */
	public void onUploadFile() {
		if (intercept != null) {
			if (!intercept.beforeUpLoad(this.parentBillPanel)) {
				return;
			}
		}
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(isMultiSel); //�Ƿ���Զ�ѡ  Gwang 2016-07-22 ����ũ����Ǩ
		//chooser.setMultiSelectionEnabled(true); //���Զ�ѡ!!�������,���м�ֵ!!
		chooser.setCurrentDirectory(new File(getUploadDir()));
		if (ClientEnvironment.isAdmin()) {
			chooser.setDialogTitle("��(���ó���" + filesize + "M)[CardCPanel_FileDeal,RefFileDealPanel]"); //�����Ȼû������?
		} else {
			chooser.setDialogTitle("��(���ó���" + filesize + "M)"); //�����Ȼû������?
		}

		//����"�ļ�����"����, ���ԶԴ��ļ����ͽ��й��� Gwang 2013/3/13			=========>Begin
		//����: "�ļ�����","Excle�ĵ�,xls,xlsx;Word�ĵ�,doc,docx;�����ļ�,*"
		String filterString = uCDfVO.getConfValue("�ļ�����", "");
		if (!filterString.equals("")) {
			chooser.setAcceptAllFileFilterUsed(false); //������ѡ�������ļ�
			String[] filters = filterString.split(";"); //�ָ���������
			for (String strFilter : filters) {
				String[] strTemp = strFilter.split(","); //�ָ�ÿ��������������, ��������
				String desc = strTemp[0];
				//String[] to Array
				ArrayList<String> extList = new ArrayList<String>(Arrays.asList(strTemp));
				extList.remove(0);
				//Array to String[]
				String[] exts = new String[extList.size()];
				extList.toArray(exts);

				//�������������"*", ������ѡ�������ļ�, �������Ӵ˹�����
				if (extList.contains("*") && !chooser.isAcceptAllFileFilterUsed()) {
					chooser.setAcceptAllFileFilterUsed(true);
				} else {
					FileNameExtensionFilter filter = new FileNameExtensionFilter(desc, exts);
					chooser.setFileFilter(filter);
				}
			}
		}
		//<=========>End

		int result = chooser.showOpenDialog(this.parentBillPanel == null ? (this.cardCompent == null ? this : this.cardCompent) : this.parentBillPanel); //��ֹ�������½ǵ���!

		if (result == 0 && chooser.getSelectedFile() != null) {
			setUploadDir(chooser.getSelectedFile().getParent());
		} else {
			return;
		}

		//�Ƿ���Զ�ѡ  Gwang 2016-07-22 ����ũ����Ǩ
		File[] selFiles = new File[1];
		if (chooser.getSelectedFiles().length == 0) {
			selFiles[0] = chooser.getSelectedFile();
		} else {
			selFiles = chooser.getSelectedFiles();
		}
		final File[] allChooseFiles = selFiles;//��ǰ�ָ�Ǩ��ʱû�м���䣬һֱ�޷�����ļ������޸�֮�����/2017-08-17��

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
			recordDealLog("����", allChooseFiles[i].getName()); //
		}
		resetHeight(); //
		if (intercept != null) {
			intercept.afterUpLoad(this.parentBillPanel, allChooseFiles);
		}
	}

	/**
	 * ����ϴ�·������� ������� ��ǰ���ϴ�������ʹ��ͬһ��·��
	 * @return
	 */
	private String getUploadDir() {
		Object o = ClientEnvironment.getInstance().get("�ϴ�·��");
		if (o == null) {
			return "C:\\";
		} else {
			return o.toString();
		}
	}

	private void setUploadDir(String uploadDir) {
		ClientEnvironment.getInstance().put("�ϴ�·��", uploadDir);
	}

	private void dealUpload(File[] _allChooseFiles) throws Exception {
		for (int i = 0; i < _allChooseFiles.length; i++) {
			String str_newFileName = onUploadBtnClicked(_allChooseFiles[i]); //
			String str_str_newFileName_viewed = getViewFileName(str_newFileName); //ȥ�������ŵ��ļ���
			if (str_newFileName != null && !str_newFileName.equals("")) { //����ɹ���,��᷵���µ��ļ���!!
				int k = billListPanel.addEmptyRow(false);
				billListPanel.setValueAt(new RefItemVO(str_newFileName, null, str_str_newFileName_viewed), k, "filename"); //�ļ���
				billListPanel.setValueAt(new StringItemVO("�༭"), k, "edit"); //�༭
				billListPanel.setValueAt(new StringItemVO("����"), k, "download"); //����
				billListPanel.setValueAt(new StringItemVO("ɾ��"), k, "delete"); //ɾ��
			}
		}
	}

	// �ϴ�����
	private String onUploadBtnClicked(File uploadfile) throws Exception {
		FileInputStream fins = null; //
		try {
			String str_initFileName = uploadfile.getName(); //�ϴ��ĳ�ʼ�ļ���!

			if (uploadfile.length() > (filesize * 1024 * 1024)) {
				MessageBox.show(this, "���ϴ����ļ�����,���Ϊ" + filesize + "MB!");
				return null;
			}
			if (str_initFileName.lastIndexOf(".") < 0 || str_initFileName.lastIndexOf(".") == str_initFileName.length()) {
				MessageBox.show(this, "���ϴ��к�׺�����ļ�!");
				return null;
			}
			String str_initFileMasterName = null; //�ļ�����!
			String str_initFileExtName = null; //�ļ���չ��!
			if (str_initFileName.lastIndexOf(".") > 0) {
				str_initFileMasterName = str_initFileName.substring(0, str_initFileName.lastIndexOf(".")); //�ļ�����
				str_initFileExtName = str_initFileName.substring(str_initFileName.lastIndexOf(".") + 1, str_initFileName.length()); //�ļ�����չ��
				for (int i = 0; i < str_initFileExtName.length(); i++) { //��չ��������Ӣ��,��������������!
					if (!((str_initFileExtName.charAt(i) >= 'a' && str_initFileExtName.charAt(i) <= 'z') || (str_initFileExtName.charAt(i) >= 'A' && str_initFileExtName.charAt(i) <= 'Z'))) {
						MessageBox.show(this, "���ϴ��кϷ���׺�����ļ�(������Ӣ��)!"); //
						return null;
					}
				}
			}
			TBUtil tbUtil = new TBUtil(); //
			int li_fileGBLength = tbUtil.getStrUnicodeLength(str_initFileName); //
			if (li_fileGBLength > 125) { //125
				if (JOptionPane.showConfirmDialog(this, "���ϴ����ļ�����,ϵͳ���Զ���ȡǰ120λ�ֽ�(һ��������2���ֽ�)!\r\n���Ƿ���������?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
					return null;
				}
			}

			int filelength = new Long(uploadfile.length()).intValue(); //�ļ���С!
			byte[] filecontent = new byte[filelength]; //һ���Ӷ����ļ�!!!
			fins = new FileInputStream(uploadfile); //�����ļ���!
			fins.read(filecontent);
			ClassFileVO filevo = new ClassFileVO(); //
			filevo.setByteCodes(filecontent); //�����ֽ�!
			String str_newFileName = "";
			str_initFileMasterName = tbUtil.subStrByGBLength(str_initFileMasterName, 120, true); //��ȡǰ120λ(��60������),������ȡ��,�������ʡ�Ժ�!!!

			boolean isConvertFileName = uCDfVO.getConfBooleanValue("�ļ����Ƿ�ת��", true); //
			String str_uploaddir = uCDfVO.getConfValue("�ļ��洢��·��", ""); //
			if (!isConvertFileName) { //�����ת��!!
				filevo.setClassFileName(str_uploaddir + "/" + str_initFileMasterName + "." + str_initFileExtName); //�ļ���
				str_newFileName = UIUtil.uploadFileFromClient(filevo, false);
			} else { //����ļ�����Ҫת��,��Ҫ����16������!!
				StringBuilder sb_fileName = new StringBuilder(); //
				sb_fileName.append(str_uploaddir + "/"); //·��
				sb_fileName.append(tbUtil.getCurrDate(false, true) + "/"); //����Ŀ¼,���ڲ����и�!!!
				sb_fileName.append(tbUtil.convertStrToHexString(getFileNo() + str_initFileMasterName)); //����������16����ת��!!
				sb_fileName.append("."); //��������չ�������ӵ�!!!
				sb_fileName.append(str_initFileExtName); //ԭ������չ��
				filevo.setClassFileName(sb_fileName.toString()); //�����ļ���!
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
	 * ���������ļ�.. Ҫ�������Ƿ񸲸ǵ���ʾ!!!
	 */
	private void onBatchDownLoadFile() {
		try {
			BillVO[] billVOs = this.billListPanel.getAllBillVOs(); //
			if (billVOs == null || billVOs.length == 0) {
				MessageBox.show(this, "û����Ҫ���ص��ļ�"); //
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
			chooser.setDialogTitle("ѡ��һ��Ŀ¼,�������ļ����ص���Ŀ¼��!"); //
			int li_rewult = chooser.showSaveDialog(this.parentBillPanel == null ? (this.cardCompent == null ? this : this.cardCompent) : this.parentBillPanel);
			if (li_rewult == JFileChooser.CANCEL_OPTION) {
				return;// ���ȡ��ʲôҲ����
			}

			if (chooser.getSelectedFile().isFile()) {
				MessageBox.show(this, "��������ʱ����ѡ��һ��Ŀ¼,ϵͳ����������ļ������ظ�Ŀ¼��!!"); //
				return;
			}
			String str_pathdir = chooser.getSelectedFile().getAbsolutePath(); // ;
			if (str_pathdir.endsWith("\\")) {
				str_pathdir = str_pathdir.substring(0, str_pathdir.length() - 1); //
			}

			// ѭ�����������ļ�
			for (int i = 0; i < filenames.length; i++) {
				if (filenames[i].indexOf("/") != -1) {
					UIUtil.downLoadFile("/upload", filenames[i], false, str_pathdir, getViewFileName(filenames[i]), true);
				} else {
					UIUtil.downLoadFile("/upload", filenames[i], false, str_pathdir, filenames[i], true); // �����ļ�
				}
			}
			ClientEnvironment.str_downLoadFileDir = str_pathdir;
			MessageBox.show(this, "����" + filenames.length + "���ļ���Ŀ¼[" + str_pathdir + "]�³ɹ�!!!"); ////
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * �鿴������ʷ���!!!
	 */
	private void onShowDealHist() {
		String str_bno = getDealLogBNO(); //
		if (str_bno == null) {
			MessageBox.show(this, "�ÿؼ�û��ע�ᴦ����ʷ!"); //
			return; //
		}
		OneSQLBillListConfirmDialog dialog = new OneSQLBillListConfirmDialog(this, "select dealtype ��������,filename �ļ���,username �û�,deptname ����,dealtime ����ʱ�� from pub_reffiledeallog where bno='" + str_bno + "' order by id", "������ʷ", 700, 400, false); //
		//������sql����BillListPanel���п�Ĭ��Ϊ200������Ϊ��̫�࣬���ֹ�����̫�ѿ��ˣ���Ҫ�ֶ�����һ���п�����޸�
		BillListPanel list = dialog.getBillListPanel();
		if (list.getRowCount() == 0) {//���û����ʷ���ֱ����ʾһ�£���Ҫ�����տյ��б��ˡ����/2012-03-27��
			MessageBox.show(this, "�ÿؼ�û�д�����ʷ!"); //
			return; //
		}
		list.setItemWidth("��������", 70);
		list.setItemWidth("�ļ���", 150);
		list.setItemWidth("�û�", 130);
		list.setItemWidth("����", 130);
		list.setItemWidth("����ʱ��", 130);
		dialog.setVisible(true); //
	}

	/**
	 * ���������,һ����ֱ�ӵ���ļ���,���߲鿴
	 * һ���ǵ�����صĳ�����,����ĳ���ļ�
	 * @param _event
	 */
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		BillVO billVO = billListPanel.getBillVO(_event.getRow()); //
		String str_filename = billVO.getStringValue("filename");
		String str_fileViewName = billVO.getStringViewValue("filename"); //
		String filetype = str_filename.substring(str_filename.lastIndexOf(".") + 1, str_filename.length());
		if (_event.getItemkey().equals("filename")) { //�����ֱ�ӵ���ļ���!
			//***************�����ӣ�������ʾˮӡ************************//
			StringBuffer sb_httpurl = new StringBuffer();
			String sql = "select textwater,picwater,picposition from pub_filewatermark  where filename ='" + str_filename + "'"; //��ѯ�Ƿ���Ҫ��ʾˮӡ
			String[][] watermsg = null;
			try {
				watermsg = UIUtil.getStringArrayByDS(null, sql);
			} catch (WLTRemoteException e2) {
				e2.printStackTrace();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			if (watermsg != null && watermsg.length > 0) { //�����Ҫ��ˮӡ
				if (watermsg[0][0] != null && !"".equals(watermsg[0][0])) {
					sb_httpurl.append("&textwater=" + watermsg[0][0]);
				}
				if (watermsg[0][1] != null && !"".equals(watermsg[0][1])) {
					sb_httpurl.append("&picwater=" + watermsg[0][1]);
				}
				if (watermsg[0][2] != null && !"".equals(watermsg[0][2])) {
					sb_httpurl.append("&picposition=" + watermsg[0][2]);
				}
				if (filetype.equalsIgnoreCase("doc") || filetype.equalsIgnoreCase("docx") || filetype.equalsIgnoreCase("wps") || filetype.equalsIgnoreCase("xls") || filetype.equalsIgnoreCase("xlsx") || filetype.equalsIgnoreCase("ppt") || filetype.equalsIgnoreCase("pptx")) {//�ʴ���Ŀ��������ӿɱ༭���ļ����͡����/2012-11-06��
					if (str_filename.indexOf("/") != -1) {
						str_filename = str_filename.replaceAll("\\\\", "/");
					}
					OfficeCompentControlVO controlVO = new OfficeCompentControlVO(false, true, true, null); //���ܱ���
					controlVO.setSubdir("upload"); //������Ŀ¼!!!
					try {
						ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class); //
						String str_sessionId = service.registerOfficeCallSessionID(controlVO); //
						final String str_url = System.getProperty("CALLURL") + "/OfficeViewServlet?RecordID=" + str_filename.substring(0, str_filename.lastIndexOf(".")) + "&filetype=" + filetype.toLowerCase() + "&sessionid=" + str_sessionId + ""; //						

						try {
							Desktop.browse(new URL(str_url + sb_httpurl.toString())); //
						} catch (Exception e1) {
							e1.printStackTrace();
							System.err.println("ʹ��JDIC�������ʧ��,����ֱ�ӵ�IE����!!"); //�������е��˵Ļ�������,jdic�ò���,�����������Ļ��������������������!!
							Runtime.getRuntime().exec("explorer.exe \"" + str_url + "\""); //
						} //	
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					MessageBox.show(this, "ֻ��doc,docx,wps,xls,xlsx,ppt,pptx���͵��ļ����ܴ�!"); //
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
				UIUtil.openRemoteServerFile("upload", str_filename); //ֱ�����ߴ��ļ�,ʹ��Servlet�������MIME���͵��ļ�	
			}

		} else if (_event.getItemkey().equals("edit")) { //���߱༭!!!!
			if (filetype.equalsIgnoreCase("doc") || filetype.equalsIgnoreCase("docx") || filetype.equalsIgnoreCase("wps") || filetype.equalsIgnoreCase("xls") || filetype.equalsIgnoreCase("xlsx") || filetype.equalsIgnoreCase("ppt") || filetype.equalsIgnoreCase("pptx")) { //�ʴ���Ŀ��������ӿɱ༭���ļ����͡����/2012-11-06��
				if (str_filename.indexOf("/") != -1) {
					str_filename = str_filename.replaceAll("\\\\", "/");
				}

				/***
				 * Gwang 2012-4-21�޸�
				 * ����:��������docxʱ��[�༭]�ᱨ��"�ļ��������!"
				 * ԭ��:�������ļ����а�����һ��Ŀ¼��(20120421/N101_CEC4B5B561616161.docx), �������doc���;���û��, û����ǧ���ؼ�����ô�����!
				 * ���ļ�����·���ֿ�������������!
				 * ���ҽ������ı༭��ʽ���ļ����ı༭��ʽͳһ, ���ٵ���IE����, ��ΪDialog!
				 */
				String path = "", fileName = "";
				if (str_filename != null && str_filename.indexOf("/") != -1) {
					path = str_filename.substring(0, str_filename.lastIndexOf("/"));
					;
					fileName = str_filename.substring(str_filename.lastIndexOf("/") + 1, str_filename.length());
				}
				RefItemVO refItemVO = new RefItemVO(fileName, null, str_fileViewName);
				CommUCDefineVO defVO = new CommUCDefineVO("Office�ؼ�");
				defVO.setConfValue("�洢Ŀ¼", "/upload" + path);
				RefDialog_Office refDialog = new RefDialog_Office(null, "�༭", refItemVO, this.billListPanel, defVO); //��������!!
				refDialog.initialize(); //
				refDialog.setVisible(true);

				//ͣ��һ���ٹ�, ���򴰿ڻῨ��!
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					refDialog = null;
				}

				/*
				OfficeCompentControlVO controlVO = new OfficeCompentControlVO(true, true, true, null); //
				controlVO.setSubdir("upload"); //������Ŀ¼!!!
				try {
					ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class); //
					String str_sessionId = service.registerOfficeCallSessionID(controlVO); //
					final String str_url = System.getProperty("CALLURL") + "/OfficeViewServlet?RecordID=" + str_filename.substring(0, str_filename.lastIndexOf(".")) + "&filetype=" + filetype + "&sessionid=" + str_sessionId + ""; //
					try {
						BrowserService browserservice = (BrowserService) ServiceManager.getService("BrowserService");
						browserservice.show(new URL(str_url), "_blank");//ie6Ĭ����ͬһ���ڴ򿪣��������ļ��򿪵�ʱ���ٵ���༭��Ѵ򿪵Ľ���رգ���unload()�ű�
						//�ĳ�����д������ָ��target�����´��ڴ򿪣������������⡣
						//Desktop.browse(new URL(str_url)); //
					} catch (Exception e1) {
						e1.printStackTrace();
						System.err.println("ʹ��JDIC�������ʧ��,����ֱ�ӵ�IE����!!"); //�������е��˵Ļ�������,jdic�ò���,�����������Ļ��������������������!!
						Runtime.getRuntime().exec("explorer.exe \"" + str_url + "\""); //
					} //	
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					recordDealLog("�༭", str_fileViewName); //
				}
				*/
			} else {
				MessageBox.show(this, "ֻ��doc,docx,wps,xls,xlsx,ppt,pptx���͵��ļ��������߱༭!"); //
				return;
			}
		} else if (_event.getItemkey().equals("delete")) { //���ɾ��!!! ��Ҫ��¼��־!!!
			if (JOptionPane.showConfirmDialog(this, "���Ƿ������ɾ�����ϴ����ļ�?", "ɾ���ļ�?", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			} else {
				try {
					FrameWorkMetaDataServiceIfc serives = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //
					boolean del_result = serives.deleteZipFileName(str_filename); //Զ��ɾ��
				} catch (WLTRemoteException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					recordDealLog("ɾ��", str_fileViewName); //��¼��־!!
				}
				billListPanel.removeRow();
				resetHeight(); //
			}
		} else if (_event.getItemkey().equals("download")) { //����,��Ҫ��¼��־ô??
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
	 * ����ļ��������ӣ������ļ���ֱ�ӵ�����ʾ�ļ�
	 * �����ļ���url����Ҫ���Ӳ���&directopenfile=XXX����ȡֵ����Ϊ��
	 * @param _filename
	 */
	private void getFile_directly(String _filename) {
		String str_filename = _filename;
		OfficeCompentControlVO controlVO = new OfficeCompentControlVO(this.isOnlineSaveAbled(), true, true, null); //
		controlVO.setSubdir("upload"); // ������Ŀ¼!!!

		try {
			TBUtil tb = new TBUtil();
			ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class);
			String str_sessionId = service.registerOfficeCallSessionID(controlVO); //
			final String str_url = System.getProperty("CALLURL") + "/OfficeViewServlet?RecordID=" + tb.convertStrToHexString(str_filename.substring(0, str_filename.length() - 4)) + "&filetype=" + str_filename.substring(str_filename.length() - 3, str_filename.length()) + "&sessionid=" + str_sessionId + "&directopenfile=3"; //

			//������ʾ
			BillDialog dialog = new BillDialog(this, 800, 600, 200, 200);
			JScrollPane scrollpanel = new JScrollPane();
			scrollpanel.getViewport().removeAll(); //
			WebBrowser webPanel = new WebBrowser(new java.net.URL(str_url)); //�ϰ汾jdic�������ͷŸ���Դʱ�׳��쳣������������������µİ汾��0.9.5֮��ģ���true�ĳ�false��
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
			MessageBox.show(this, "û���ϴ�����!"); //
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
			if (li_rewult == JFileChooser.APPROVE_OPTION) { // �������ȷ��
				final File chooseFile = chooser.getSelectedFile(); // ȡ���ļ�·��
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
								// liuxuanfei update start ����ʱ, �ļ�ѡ���, �û���ʱ���޸��ļ�������, ��û�м�����չ��, ϵͳӦ���Զ�����"��֪"��չ��
								// ��Ȼ, ����ϴ����ļ������û����չ��, ϵͳͬ��������չ��.
								// ������һ�����û��ȥ��: �û��޸ĺ�д����չ����"��֪"����չ����һ��ʱ, ϵͳû�н���"����". ���ǵ����벻��������������, �Ϳ��Ժ�Ŀͻ���ô����(��Ȼ, ���ÿͻ��ܹ������)
								// ����˵: �ϴ����ļ���123.txt, �û����ļ�ѡ����ļ����޸�Ϊ1234.doc, ��ôϵͳ�Ƿ�Ӧ��ǿ�Ƽ���"��֪"����չ��? ����֮�󱣴���ļ�Ϊ: 1234.doc.txt
								String chooseFileName = chooseFile.getName();
								if (chooseFileName != null && !chooseFileName.contains(".")) {
									int ipoint = _filename.lastIndexOf(".");
									if (ipoint > 0) {
										chooseFileName = chooseFileName + _filename.substring(ipoint, _filename.length());
									}
								}
								String name = UIUtil.downLoadFile("/upload", _filename, false, str_pathdir, chooseFileName, true); //
								if (name == null || name.trim().equals("")) {
									MessageBox.show(RefFileDealPanel.this, "�����ļ�������!!!"); //
								} else {
									MessageBox.show(RefFileDealPanel.this, "�����ļ��ɹ�!!!"); //
								}
							} catch (Exception e1) {
								e1.printStackTrace();
								MessageBox.show(RefFileDealPanel.this, "�����ļ�ʧ�ܣ�ԭ��" + e1.getMessage() + "��!!!", WLTConstants.MESSAGE_ERROR); //
							} // �����ļ�
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
		if (cardCompent != null) { //ˢ�¸߶�
			int li_rows = billListPanel.getRowCount(); //
			if (li_rows == 0) { //���û���ļ��Ļ�,����ʾû���ļ�!
				billListPanel.findColumn("filename").setHeaderValue("û���ļ�"); //�����޸ı�ͷ����
			} else {
				billListPanel.findColumn("filename").setHeaderValue("�ļ��б�"); //�����޸ı�ͷ�е�����
			}
			cardCompent.setPreferredSize(new Dimension((int) cardCompent.getPreferredSize().getWidth(), getComputeRowHeight(li_rows))); //���¼����и�
			cardCompent.updateUI(); //

			//�����Ƿ�����ϴ����,  Gwang 2016-07-22 ����ũ����Ǩ
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
	 * ȡ�ü������и�
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
	 * ��ʱ��Ҫ�ļ��༭������!!
	 * @return
	 */
	private String getFileNo() {//�õ��ļ����
		try {
			if (!"".equals(this.uCDfVO.getConfValue("�ļ����������", ""))) {
				AbstractRefFileNoCreate filenoc = (AbstractRefFileNoCreate) Class.forName(uCDfVO.getConfValue("�ļ����������")).newInstance();
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

	//��¼��־ʱ����֪���ÿؼ����ĸ��ؼ�,Ŀǰ�ķ�����ʹ�ñ���+�����ֶ���+�����ֶ�ֵ..
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

		String str_tableName = templetVO.getSavedtablename(); //����..
		if (str_tableName == null) {
			return null; //
		}
		String str_PkName = templetVO.getPkname(); //
		if (str_PkName == null || str_PkName.trim().equals("")) {
			str_PkName = "id"; //
		}
		return (str_tableName + "_" + str_PkName + "_" + str_pkValue).toLowerCase(); //ƴ��һ��
	}

	//��¼������־!
	private void recordDealLog(String _dealType, String _fileName) {
		try {
			String str_btn = getDealLogBNO(); //
			if (str_btn == null) {
				return; //
			}
			String str_loginUserName = ClientEnvironment.getInstance().getLoginUserCode() + "/" + ClientEnvironment.getInstance().getLoginUserName(); //��Ա����
			String str_loginUserDeptName = ClientEnvironment.getInstance().getLoginUserDeptName(); //��������..
			String str_currtime = new TBUtil().getCurrTime(); //��ǰʱ��..
			InsertSQLBuilder isql = new InsertSQLBuilder("pub_reffiledeallog"); //�����ļ�������־
			isql.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_PUB_REFFILEDEALLOG")); //
			isql.putFieldValue("bno", str_btn); //����,���ɱ���,�����ֶ���,�����ֶ�ֵƴ������!!
			isql.putFieldValue("dealtype", _dealType); //��������,����/�༭/ɾ��
			isql.putFieldValue("filename", _fileName); //�ļ���
			isql.putFieldValue("username", str_loginUserName); //������û���
			isql.putFieldValue("deptname", str_loginUserDeptName); //����Ļ�����
			isql.putFieldValue("dealtime", str_currtime); //����ʱ��
			UIUtil.executeUpdateByDS(null, isql.getSQL()); //ִ��!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}
}