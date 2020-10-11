package cn.com.infostrategy.ui.sysapp.login;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillCardEditEvent;
import cn.com.infostrategy.ui.mdata.BillCardEditListener;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_ComboBox;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_TextField;

public class ReloginDialog extends BillDialog implements BillCardEditListener {

	private LoginAppletLoader loader = null;
	private BillCardPanel cardPanel = null;

	public ReloginDialog(Container _parent, LoginAppletLoader _loader) {
		super(_parent, "���µ�¼", 350, 253);
		this.loader = _loader; //
		this.setResizable(false); //
		initialize();
	}

	/**
	 * ��ʼ��ҳ��
	 */
	private void initialize() {
		this.getContentPane().setLayout(new BorderLayout()); //
		cardPanel = new BillCardPanel(new LoginPanelVO()); //
		cardPanel.setScrollable(false); //
		cardPanel.setTitleable(false);
		cardPanel.setEditable(true); //
		cardPanel.setRealValueAt("ISADMIN", "N");
		cardPanel.setEditable("ADMINPWD", false); //
		if (ClientEnvironment.getInstance().isMultiLanguage()) {
			cardPanel.setVisiable("LANGUAGE", true);
		} else {
			cardPanel.setVisiable("LANGUAGE", false);
		}
		boolean flag = new TBUtil().getSysOptionBooleanValue("ע��ʱ�Ƿ���ʾ�����������", true);
		if (!flag) {
			cardPanel.setVisiable("ADMINPWD", false);
			cardPanel.setVisiable("ISADMIN", false);
		}

		((CardCPanel_TextField) cardPanel.getCompentByKey("PWD")).getTextField().addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == e.VK_ENTER) {
					onConfirm();
				}
			}

			public void keyReleased(KeyEvent e) {

			}

			public void keyTyped(KeyEvent e) {

			}
		});
		((CardCPanel_TextField) cardPanel.getCompentByKey("ADMINPWD")).getTextField().addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == e.VK_ENTER) {
					onConfirm();
				}
			}

			public void keyReleased(KeyEvent e) {

			}

			public void keyTyped(KeyEvent e) {

			}
		});

		cardPanel.addBillCardEditListener(this);
		((CardCPanel_ComboBox) cardPanel.getCompentByKey("LANGUAGE")).getComBox().setSelectedIndex(1); //
		this.getContentPane().add(cardPanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout());  ////
		JButton btn_1 = new WLTButton(UIUtil.getLanguage("ȷ��")); //
		JButton btn_2 = new WLTButton(UIUtil.getLanguage("ȡ��")); //
		btn_1.setPreferredSize(new Dimension(75, 20));
		btn_2.setPreferredSize(new Dimension(75, 20));
		btn_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onConfirm();
			}
		});

		btn_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		panel.add(btn_1);
		panel.add(btn_2);
		return panel;
	}

	public void onBillCardValueChanged(BillCardEditEvent _evt) {
		BillCardPanel card = (BillCardPanel) _evt.getSource();
		if (_evt.getItemKey().equals("ISADMIN")) {
			StringItemVO obj = (StringItemVO) _evt.getNewObject(); //
			if (obj != null && obj.getStringValue().equals("Y")) {
				card.getCompentByKey("ADMINPWD").setItemEditable(true);
				((CardCPanel_TextField) cardPanel.getCompentByKey("ADMINPWD")).getTextField().requestFocus(); //
			} else {
				card.getCompentByKey("ADMINPWD").setItemEditable(false);
			}
		}
	}

	private void onConfirm() {
		String user_code = cardPanel.getRealValueAt("USERCODE"); //
		String pwd = cardPanel.getRealValueAt("PWD"); //

		String adminpwd = null;
		ComBoxItemVO boxItemVO = (ComBoxItemVO) cardPanel.getCompentObjectValue("LANGUAGE");
		String str_language = WLTConstants.SIMPLECHINESE;
		if (boxItemVO != null) {
			str_language = boxItemVO.getId();
		}

		boolean bo_isAdmin = false;
		if (1 == 1) {
			String isAdmin = cardPanel.getRealValueAt("ISADMIN"); //
			if (isAdmin != null && isAdmin.equals("Y")) {
				adminpwd = cardPanel.getRealValueAt("ADMINPWD"); //
				bo_isAdmin = true;
				if(adminpwd == null || adminpwd.equals("")){ //���������������[����2012-12-05]
					MessageBox.show(this, "�������벻��Ϊ��!"); //
					return;
				}
			} else {
				bo_isAdmin = false;
			}
		}

		this.dispose();
		this.loader.dealLogin(true, user_code, pwd, adminpwd, ClientEnvironment.chooseISys, bo_isAdmin, false, str_language, null); //
	}

	private void onCancel() {
		this.dispose(); //
	}

	class LoginPanelVO extends AbstractTMO {

		public HashVO getPub_templet_1Data() {
			HashVO vo = new HashVO(); //
			vo.setAttributeValue("templetcode", "RELOGIN"); //ģ�����,��������޸�
			vo.setAttributeValue("templetname", "�û���Ϣ"); //ģ������
			vo.setAttributeValue("tablename", null); //��ѯ���ݵı�(��ͼ)��
			vo.setAttributeValue("pkname", "ID"); //������
			vo.setAttributeValue("pksequencename", "S_PUB_MENU"); //������
			vo.setAttributeValue("savedtablename", null); //�������ݵı���
			vo.setAttributeValue("cardwidth", "200"); //�������ݵı���
			vo.setAttributeValue("listcustpanel", null); //�б��Զ������
			vo.setAttributeValue("cardcustpanel", null); //��Ƭ�Զ������
			return vo;
		}

		public HashVO[] getPub_templet_1_itemData() {
			Vector vector = new Vector();
			HashVO itemVO = null;

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "USERCODE"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�û���"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "UserCode"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "PWD"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "����"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Password"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "�����"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ADMINPWD"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "��������"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "SupportPassword"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "�����"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "ISADMIN"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "�Ƿ�������"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "IsSupport"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "��ѡ��"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "LANGUAGE"); //Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "Language"); //��ʾ����
			itemVO.setAttributeValue("itemname_e", "Language"); //��ʾ����
			itemVO.setAttributeValue("itemtype", "������"); //�ؼ�����
			itemVO.setAttributeValue("comboxdesc", "select 'SIMPLECHINESE' id,'SIMPLECHINESE' code,'��������' name from wltdual union all select 'ENGLISH' id,'ENGLISH' code,'English' name from wltdual union all select 'TRADITIONALCHINESE' id,'TRADITIONALCHINESE' code,'��������' name from wltdual"); //��������
			itemVO.setAttributeValue("refdesc", null); //���ն���
			itemVO.setAttributeValue("issave", "N"); //�Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); //1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); //�Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); //���ع�ʽ
			itemVO.setAttributeValue("editformula", null); //�༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); //Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "145"); //�б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150"); //��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); //�б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); //�б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); //��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); //��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "Y");
			vector.add(itemVO);

			return (HashVO[]) vector.toArray(new HashVO[0]);
		}
	}

}
