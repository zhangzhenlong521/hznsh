package cn.com.infostrategy.ui.workflow.engine;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_TextArea;

/**
 * �������¼���!!
 * @author xch
 *
 */
public class WorkFlowYjBdMsgPanel extends JPanel {
	private static final long serialVersionUID = 1L; //

	private BillCardPanel cardPanel = null;
	private String str_prDealPoolId, str_realDealUser = null;

	public WorkFlowYjBdMsgPanel(String str_prDealPoolId_, String _realDealUser) {
		super();
		this.setLayout(new BorderLayout(0, 5)); //
		this.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
		this.setBackground(LookAndFeel.cardbgcolor);

		this.str_prDealPoolId = str_prDealPoolId_; //����ʵ��id
		this.str_realDealUser = _realDealUser; //������id
		cardPanel = new BillCardPanel("PUB_WF_YJBDMSG"); //����������
		cardPanel.setEditableByEditInit();

		//��ǰ�����︻�����ع��˲��յĵ����ť�¼�! �ǲ��Ǳ�׼�İ취! ��׼��������һ���Զ�����!!!������ս�[WFYiJianBuDengObjRefDialog]
		loadInitData(); //���س�ʼ������...

		this.add(cardPanel, BorderLayout.CENTER);
	}

	/**
	 * ���س�ʼ������! ���������ǹ�������ټ��ؽ���!!!
	 */
	private void loadInitData() {
		if (str_prDealPoolId != null && !str_prDealPoolId.trim().equals("")) {
			try {
				String str_sql = "select submitmessage,submitmessagefile,participant_user,participant_username,participant_yjbduserid from pub_wf_dealpool where id='" + TBUtil.getTBUtil().getNullCondition(str_prDealPoolId) + "' and issubmit='N' and isprocess='N'";
				HashVO[] hvs_msg = UIUtil.getHashVoArrayByDS(null, str_sql); ////
				if (hvs_msg.length > 0) {
					String participant_yjbduserid = hvs_msg[0].getStringValue("participant_yjbduserid", ""); //
					String participant_user = hvs_msg[0].getStringValue("participant_user", ""); //
					String participant_username = hvs_msg[0].getStringValue("participant_username", ""); //
					if (!"".equals(participant_user)) {
						if (participant_username.indexOf("/") > 0) {
							participant_username = participant_username.substring(participant_username.indexOf("/") + 1, participant_username.length()); //
						}
						if (!ClientEnvironment.getCurrLoginUserVO().getId().equals(participant_user)) {//����Ǳ��˾�Ӧ�����ڴ������񲹵��ȸ���Ժ���ܻ��и��õ����/sunfujun/20121121
							cardPanel.setValueAt("bdobj", new RefItemVO(participant_user, null, participant_username));
						}
					}
					String msg = hvs_msg[0].getStringValue("submitmessage", "");
					String[] msgs = msg.split("����ԭ��:");
					if (msgs[0].endsWith("\r\n")) {
						msgs[0] = msgs[0].substring(0, msgs[0].lastIndexOf("\r\n"));
					}
					if (msgs[0].startsWith("\r\n")) {
						msgs[0] = msgs[0].substring(msgs[0].indexOf("\r\n"), msgs[0].length());
					}
					cardPanel.setRealValueAt("ldps", msgs[0]);
					if (msgs.length > 1) {
						if (msgs[1].endsWith("\r\n")) {
							msgs[1] = msgs[1].substring(0, msgs[1].lastIndexOf("\r\n"));
						}
						if (msgs[1].startsWith("\r\n")) {
							msgs[1] = msgs[1].substring(msgs[1].indexOf("\r\n") + 2, msgs[1].length());
						}
						cardPanel.setRealValueAt("bdyy", msgs[1]);
					}
					if (hvs_msg[0].getStringValue("submitmessagefile") != null) {
						String str_encryFileName = hvs_msg[0].getStringValue("submitmessagefile");
						String[] str_fileNames = TBUtil.getTBUtil().split(str_encryFileName, ";");
						StringBuilder sb_newFileName = new StringBuilder(); //
						for (int i = 0; i < str_fileNames.length; i++) {
							if (str_fileNames[i].lastIndexOf(".") > 0) {
								String str_fileexttype = str_fileNames[i].substring(str_fileNames[i].lastIndexOf(".") + 1, str_fileNames[i].length());
								str_fileNames[i] = str_fileNames[i].substring(str_fileNames[i].indexOf("_") + 1, str_fileNames[i].lastIndexOf("."));
								str_fileNames[i] = TBUtil.getTBUtil().convertHexStringToStr(str_fileNames[i]) + str_fileexttype;
							} else {
								str_fileNames[i] = str_fileNames[i].substring(str_fileNames[i].indexOf("_") + 1, str_fileNames[i].length());
								str_fileNames[i] = TBUtil.getTBUtil().convertHexStringToStr(str_fileNames[i]);
							}
							sb_newFileName.append(str_fileNames[i] + ";");
						}
						RefItemVO refVO = new RefItemVO(hvs_msg[0].getStringValue("submitmessagefile"), null, sb_newFileName.toString());
						cardPanel.setValueAt("bdyj", refVO);
					}
				}
			} catch (Exception _ex) {
				_ex.printStackTrace();
			}
		}
	}

	/**
	 * ȡ�ò��Ƕ������ԱVO
	 * @return
	 */
	public RefItemVO getBdObjUserVO() {
		return (RefItemVO) cardPanel.getValueAt("bdobj"); //
	}

	/**
	 * ���������
	 * @return
	 */
	public String getMsgText() {
		return cardPanel.getRealValueAt("ldps") + "\r\n" + "����ԭ��:\r\n" + cardPanel.getRealValueAt("bdyy"); //
	}

	/**
	 * ����ĸ���
	 * @return
	 */
	public String getMsgFile() {
		return cardPanel.getRealValueAt("bdyj"); //
	}

	//У�������Ƿ�
	public boolean checkDataValidate() {
		if (cardPanel.getRealValueAt("bdobj") == null || "".equals(cardPanel.getRealValueAt("bdobj").trim())) {
			MessageBox.show(this, "��ѡ�񲹵Ƕ���!");
			return false;
		}
		if (cardPanel.getRealValueAt("ldps") == null || "".equals(cardPanel.getRealValueAt("ldps").trim())) {
			((CardCPanel_TextArea) cardPanel.getCompentByKey("ldps")).focus();
			MessageBox.show(this, "����д�쵼���!\r\n�˴����ܳ���1000������\r\n�����������1000������ʱ���ø����ϴ�!");
			return false;
		}
		if (cardPanel.getRealValueAt("bdyy") == null || "".equals(cardPanel.getRealValueAt("bdyy").trim())) {
			((CardCPanel_TextArea) cardPanel.getCompentByKey("bdyy")).focus();
			MessageBox.show(this, "����д����ԭ��!\r\n�˴����ܳ���1000������\r\n�����������1000������ʱ���ø����ϴ�!");
			return false;
		}
		if (cardPanel.getRealValueAt("bdyj") == null || "".equals(cardPanel.getRealValueAt("bdyj").trim())) {
			MessageBox.show(this, "���ϴ���������!");
			return false;
		}		
		try {
			int li_length = cardPanel.getRealValueAt("ldps").getBytes("GBK").length; //
			if (li_length > 2000) {
				MessageBox.show(this, "�쵼��ʾ���ܳ���1000������,���ø����ϴ�!");
				return false;
			}
			int li_length2 = cardPanel.getRealValueAt("bdyy").getBytes("GBK").length; //
			if (li_length2 > 1600) {
				MessageBox.show(this, "����ԭ���ܳ���800������,���ø����ϴ�!");
				return false;
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		return true;
	}

	public BillCardPanel getCardPanel() {
		return cardPanel;
	}

}
