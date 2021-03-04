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

	private RefFileDealPanel refFileDealPanel = null; //ʵ�ʴ�������
	private RefItemVO allfilename;
	private RefItemVO newfilenameref;
	private Boolean boolean_isEditable = null; //�Ƿ�ɱ༭..
	private WLTButton btn_confirm, btn_cancel;
	private CommUCDefineVO uCDfVO; //
	int filesize = TBUtil.getTBUtil().getSysOptionIntegerValue("�ϴ�������С", 25);//��Ŀ�о����и����˾����ϴ������Ĵ�С��������һ�����������/2016-12-26��

	public RefDialog_File(Container _parent, String _title, RefItemVO value, BillPanel _panel, CommUCDefineVO _uCDfVO) {// _parent�ؼ�����panel�ǿؼ��������
		super(_parent, _title, value, _panel);
		uCDfVO = _uCDfVO; //
	}

	public void initialize() {
		Object parentContainer = this.getParentContainer(); //
		if (parentContainer != null && (parentContainer instanceof UIRefPanel) && (getInitRefItemVO() == null || getInitRefItemVO().getId() == null)) { //ֻ���ڿ�Ƭ״̬�²�ֱ�ӵ��,���б�״̬�»��Ǵ򿪶Ի���!!
			this.setShowAfterInitialize(false); //
			dirUploadFile(); //ֱ�ӵ����ļ�ѡ���,�ϴ��ļ�!!
		} else {
			this.setLayout(new BorderLayout());
			refFileDealPanel = new RefFileDealPanel(this.getBillPanel(), uCDfVO); //
			refFileDealPanel.setRefItemVO(this.getInitRefItemVO()); //���ó�ʼֵ
			refFileDealPanel.setEditabled(getListIsEditabled()); //�ж��б����Ƿ�ɱ༭

			if (this.getPubTempletItemVO() != null) {
				int li_width = this.getPubTempletItemVO().getCardwidth().intValue();
				refFileDealPanel.getBillListPanel().setItemWidth("filename", li_width > 240 ? li_width - 100 : li_width); //���̴�����������·���������DefaultTMO�����ģ���Ƭ���Ĭ��Ϊ145����ǰֱ�Ӽ�ȥ100�����̫С�ˡ����/2012-05-03��
			}

			JPanel tempPanel = new WLTPanel(WLTPanel.INCLINE_NW_TO_SE, new BorderLayout(), LookAndFeel.defaultShadeColor1); //
			tempPanel.add(refFileDealPanel); //
			tempPanel.add(getSouthPanel(), BorderLayout.SOUTH); //

			this.getContentPane().add(tempPanel, BorderLayout.CENTER);
			if (this.getInitRefItemVO() == null || this.getInitRefItemVO().getId() == null) {
				//refFileDealPanel.onUploadFile(); //���������Ϊ��,��ֱ�ӵ���ѡ���ļ����!!
			}
		}
	}

	/**
	 * ��ť��
	 * @return
	 */
	private JPanel getSouthPanel() {
		JPanel panel_south = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 3)); //
		panel_south.setOpaque(false); //͸��!!!
		btn_confirm = new WLTButton("ȷ��"); //
		btn_cancel = new WLTButton("ȡ��");
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

	//ֱ���ϴ��ļ�!!
	private void dirUploadFile() {
		try {
			JFileChooser chooser = new JFileChooser();
			chooser.setMultiSelectionEnabled(true); //���Զ�ѡ!!�������,���м�ֵ!!
			chooser.setCurrentDirectory(new File(getUploadDir()));
			BillPanel billPanel = ((UIRefPanel) this.getParentContainer()).getBillPanelFrom(); //
			int result = chooser.showOpenDialog(billPanel == null ? this.getParentContainer() : billPanel); //��ֹ�������½ǵ���!
			if (result == 0 && chooser.getSelectedFile() != null) {
				setUploadDir(chooser.getSelectedFile().getParent());
			} else {
				this.setCloseType(BillDialog.CANCEL); //ֱ�ӹر�!!
				this.dispose(); //��Ϊ������û�򿪹�,�رջ���������?
				return;
			}
			final File[] allChooseFiles = chooser.getSelectedFiles(); //
			StringBuilder sb_ids = new StringBuilder(); //
			StringBuilder sb_names = new StringBuilder(); //
			for (int i = 0; i < allChooseFiles.length; i++) {
				String str_newFileName = realUploadFile(allChooseFiles[i]); //�ϴ��ļ�!!!
				if (str_newFileName == null || "".equals(str_newFileName)) {//�������Ϊ�գ��п�����Ϊ�ļ�������������δ�ϴ�����ֱ����������������һ��null�ļ�¼�����/2012-05-03��
					continue;
				}
				String str_str_newFileName_viewed = getViewFileName(str_newFileName); //ȥ�������ŵ��ļ���
				sb_ids.append(str_newFileName + ";"); //
				sb_names.append(str_str_newFileName_viewed + ";"); //
			}
			RefItemVO tmpRefVO = new RefItemVO(sb_ids.toString(), null, sb_names.toString()); ////��������VO
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
				MessageBox.show(getParentContainer(), "���ϴ����ļ�����,���Ϊ" + filesize + "MB!");
				return null;
			}
			if (uploadfile.getName().lastIndexOf(".") < 0 || uploadfile.getName().lastIndexOf(".") == uploadfile.getName().length()) {
				MessageBox.show(getParentContainer(), "���ϴ��к�׺�����ļ�!");
				return null;
			}
			if (uploadfile.getName().lastIndexOf(".") > 0) {
				String houzhui = uploadfile.getName().substring(uploadfile.getName().lastIndexOf(".") + 1, uploadfile.getName().length());
				for (int i = 0; i < houzhui.length(); i++) {
					if (!((houzhui.charAt(i) >= 'a' && houzhui.charAt(i) <= 'z') || (houzhui.charAt(i) >= 'A' && houzhui.charAt(i) <= 'Z'))) {
						MessageBox.show(getParentContainer(), "���ϴ��кϷ���׺�����ļ�!");
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
			if ("false".equals(getRefFileParam("�ļ����Ƿ�ת��"))) {
				filevo.setClassFileName(getRefFileParam("�ļ��洢��·��") + "/" + uploadfile.getName());
				str_newFileName = UIUtil.uploadFileFromClient(filevo, false);
			} else {
				filevo.setClassFileName(getRefFileParam("�ļ��洢��·��") + "/" + new TBUtil().getCurrDate().replaceAll("-", "") + "/" + new TBUtil().convertStrToHexString(getFileNo() + uploadfile.getName().substring(0, uploadfile.getName().lastIndexOf("."))) + uploadfile.getName().substring(uploadfile.getName().lastIndexOf("."), uploadfile.getName().length()));
				str_newFileName = UIUtil.uploadFileFromClient(filevo);
			}

			fins.close(); //
			return str_newFileName;
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
			return null;
		}
	}

	//��������˲���
	private String getRefFileParam(String whichParam) {
		String returnValue = ""; //����ֵ!!
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

	//ȡ����ʾ���ļ���!��ȥ��������
	private String getViewFileName(String _realFileName) {
		if (_realFileName != null && _realFileName.indexOf("/") != -1) {
			String param = _realFileName.substring(_realFileName.lastIndexOf("/") + 1, _realFileName.length());
			if (param != null && param.startsWith("N")) {
				return (new TBUtil().convertHexStringToStr(param.substring(param.indexOf("_") + 1, param.lastIndexOf("."))) + (param.substring(param.lastIndexOf("."), param.length()))); //
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

	private String getFileNo() {//�õ��ļ����
		try {
			if (!"".equals(getRefFileParam("�ļ����������"))) {
				AbstractRefFileNoCreate filenoc = (AbstractRefFileNoCreate) Class.forName(getRefFileParam("�ļ����������")).newInstance(); //
				return filenoc.getFileNo(this.getBillPanel()); //
			}
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

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

	/**
	 * ȷ������
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
	 * ������ʼ��ʱ���ø������ɱ༭
	 */
	public void Editshow(Boolean bool) {
		this.setLayout(new BorderLayout());
		refFileDealPanel = new RefFileDealPanel(this.getBillPanel(), this.uCDfVO); //
		refFileDealPanel.setRefItemVO(this.getInitRefItemVO()); //���ó�ʼֵ
		refFileDealPanel.setEditabled(bool); //�ж����Ƿ�ɱ༭

		if (this.getPubTempletItemVO() != null) {
			int li_width = this.getPubTempletItemVO().getCardwidth().intValue();
			refFileDealPanel.getBillListPanel().setItemWidth("filename", li_width - 100); //
		}

		this.getContentPane().add(refFileDealPanel, BorderLayout.CENTER);
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);

	}

}
