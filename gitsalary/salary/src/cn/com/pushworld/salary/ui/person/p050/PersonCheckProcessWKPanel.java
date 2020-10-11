package cn.com.pushworld.salary.ui.person.p050;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;
import cn.com.infostrategy.ui.report.style2.DefaultStyleReportPanel_2;
import cn.com.pushworld.salary.to.SalaryTBUtil;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

/**
 * �������ֵĽ���ͳ��
 * @author Administrator
 */
public class PersonCheckProcessWKPanel extends AbstractWorkPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private DefaultStyleReportPanel_2 dr = null;
	private BillQueryPanel bq = null;

	private String exportFileName = "���˿��˽���_";

	private WLTButton auto_score = new WLTButton("Э�����˴��", UIUtil.getImage("user_edit.png"));
	private WLTButton submit = new WLTButton("Э���ύ", UIUtil.getImage("zt_071.gif"));
	private String input_value_range = TBUtil.getTBUtil().getSysOptionStringValue("н��ģ����Ա�������", "0-10");

	public void initialize() {
		dr = new DefaultStyleReportPanel_2("SAL_TARGET_CHECK_LOG_PERSON", "");
		bq = dr.getBillQueryPanel();
		auto_score.addActionListener(this);
		dr.getPanel_btn().add(auto_score);
		dr.getPanel_btn().add(submit);
		submit.addActionListener(this);
		//��������Ĭ��ֵΪ��ǰ��������  Gwang 2013-08-21
		QueryCPanel_UIRefPanel dateRef = (QueryCPanel_UIRefPanel) bq.getCompentByKey("checkdate");
		String checkDate = new SalaryUIUtil().getCheckDate();
		dateRef.setValue(checkDate);

		//���õ����ļ����� Gwang 2013-08-31
		dr.setReportExpName(exportFileName);

		dr.getBillCellPanel().setEditable(false);
		bq.addBillQuickActionListener(this);
		this.add(dr, BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == auto_score) {
			autoScore();
		} else if (e.getSource() == confirm) {
			onClickAutoScore();
		} else if (e.getSource() == submit) {
			onAutoSubmit();
		} else {
			new SplashWindow(this, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					onQuery();
				}
			});
		}
	}

	private void onAutoSubmit() {
		String checkdate = bq.getRealValueAt("checkdate");
		try {
			String logid = UIUtil.getStringValueByDS(null, "select id from sal_target_check_log where checkdate='" + checkdate + "' ");
			if (logid == null) {
				return;
			}
			BillListPanel listpanel = new BillListPanel(null, "select t2.scoreuser userid ,t1.name ����,t1.deptname ���� from v_sal_personinfo t1 left join sal_person_check_score t2 on t1.id = t2.scoreuser where t2.scoretype='�ֶ����' and (t2.status is null or t2.status!='���ύ') and t2.logid =" + logid + "  group by t2.scoreuser having count(t2.id)>0 ");
			listpanel.setItemVisible("userid", false);
			listpanel.setRowNumberChecked(true);
			BillListDialog dialog = new BillListDialog(this, "��ѡ����ҪЭ���ύ����Ա", listpanel, 400, 600, false);
			dialog.setVisible(true);
			BillVO vos[] = listpanel.getCheckedBillVOs();
			if (vos.length > 0) {
				String userids = new SalaryTBUtil().getInCondition(vos, "userid");
				UIUtil.executeUpdateByDS(null, "update sal_person_check_score set status='���ύ' where logid = " + logid + " and scoreuser in(" + userids + ") and scoretype='�ֶ����' and checkscore is not null and checkscore !=''");
				MessageBox.show(this,"��Э���ύ�������²�ѯ!");
			}
		} catch (Exception ex) {

		}
	}

	//����Զ���ְ�ť
	private void onClickAutoScore() {
		String text = field.getText();
		if (TBUtil.isEmpty(text)) {
			MessageBox.show(dialog, "�������Զ���ַ���");
			return;
		}
		if (!TBUtil.getTBUtil().isStrAllNunbers(text)) {
			MessageBox.show(dialog, "����������");
			return;
		}
		try {
			int num = Integer.parseInt(text);
			int range[] = getInputNumRange();
			if (num < range[0] || num > range[1]) {// Ӧ�ò������
				MessageBox.show(dialog, "������[" + input_value_range + "]������.");
				return;
			}

			BillVO vos[] = listpanel.getSelectedBillVOs(true);
			if (vos.length == 0) {
				MessageBox.show(this, "�빴ѡҪ�Զ���ֵ���Ա.");
				return;
			}
			StringBuffer bf = new StringBuffer();
			StringBuffer userid = new StringBuffer();
			for (int i = 0; i < vos.length; i++) {
				bf.append(vos[i].getStringValue("username", "") + ",");
				userid.append(vos[i].getStringValue("userid", "") + ",");
			}
			if (MessageBox.confirm(this, "ȷ��Ϊ������[" + bf.substring(0, bf.length() - 1) + "]δ��ɵ�Ա������ָ���[" + num + "]����?")) {
				String checkdate = bq.getRealValueAt("checkdate");
				String logid = UIUtil.getStringValueByDS(null, "select id from sal_target_check_log where checkdate='" + checkdate + "' ");
				if (logid == null) {
					return;
				}
				String currdate = UIUtil.getServerCurrDate();
				String sql = "update sal_person_check_score set descr='����Ա�Զ����',lasteditdate='" + currdate + "',status='���ύ',checkscore=" + num + " where (checkscore is null or checkscore='' ) and targettype='Ա������ָ��' and logid =" + logid + " and scoretype='�ֶ����' and scoreuser in(" + userid.substring(0, userid.length() - 1) + ")";
				UIUtil.executeUpdateByDS(null, sql);
				MessageBox.show(this, "������,�����²�ѯ.");
				dialog.dispose();
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(dialog, "����������");
		}
	}

	private void onQuery() {
		try {
			if (bq.checkValidate()) {
				SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
				dr.getBillCellPanel().loadBillCellData(ifc.getPersonCheckProcessVO(bq.getRealValueAt("checkdate")));
				dr.getBillCellPanel().setEditable(false);
				if (dr.getBillCellPanel().getRowCount() > 2) {
					dr.getBillCellPanel().setLockedCell(2, 1); //������ͷ ��� 2013-10-10
				}

				//���õ����ļ����� Gwang 2013-08-31
				dr.setReportExpName(exportFileName + bq.getRealValueAt("checkdate"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(this, "�����쳣�������Ա��ϵ!");
		}
	}

	private JTextField field = new JTextField();
	private WLTButton confirm = new WLTButton("�Զ����");
	private BillListPanel listpanel = new BillListPanel(new MyTMO());
	private BillDialog dialog;

	private void autoScore() {
		String checkdate = bq.getRealValueAt("checkdate");
		try {
			String logid = UIUtil.getStringValueByDS(null, "select id from sal_target_check_log where checkdate='" + checkdate + "' ");
			if (logid == null) {
				return;
			}
			HashVO vos[] = UIUtil.getHashVoArrayByDS(null, "select t2.scoreuser userid,t1.name username,t1.deptname,count(t2.id) totle,count(t3.id) stotle from v_sal_personinfo t1 left join sal_person_check_score t2 on t1.id = t2.scoreuser left join sal_person_check_score t3 on t3.id = t2.id and (t3.checkscore is not null and t3.checkscore !='') where t2.scoretype='�ֶ����' and t2.logid = '" + logid
					+ "'   group by t2.scoreuser having (count(t2.id)-count(t3.id))>0");
			for (int i = 0; i < vos.length; i++) {
				vos[i].setAttributeValue("curr", vos[i].getStringValue("stotle") + "/" + vos[i].getStringValue("totle"));
			}
			if (vos.length == 0) {
				MessageBox.show(this, "���δ�ֶ������.");
				return;
			}
			listpanel.setRowNumberChecked(true);
			listpanel.queryDataByHashVOs(vos);
			for (int i = 0; i < vos.length; i++) {
				listpanel.setCheckedRow(i, true);
			}
			dialog = new BillDialog(this, "Э�����˴��", 400, 400);
			WLTLabel label = new WLTLabel("<html><a color='red'>�˹��ܽ�Э��δ�ܲμӴ�ֵ���Ա��δ��ɵĲ����Զ����.��ѡ������Ա,������������,����Զ����</a>");
			dialog.setLayout(new BorderLayout());
			dialog.add(label, BorderLayout.NORTH);
			dialog.add(listpanel, BorderLayout.CENTER);
			JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			JLabel label_info = new WLTLabel("���������:");
			field.setPreferredSize(new Dimension(80, 20));
			southPanel.add(label_info);
			southPanel.add(field);
			southPanel.add(confirm);
			confirm.addActionListener(this);
			dialog.add(southPanel, BorderLayout.SOUTH);
			dialog.setVisible(true);
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// �õ�����¼��ֵ�ķ�Χ,�������ȵ�����.
	public int[] getInputNumRange() {
		String[] values = TBUtil.getTBUtil().split(input_value_range, "-");
		int begin = 0;
		int end = 10;
		try {
			if (values.length == 2) {
				begin = Integer.parseInt(values[0]);
				end = Integer.parseInt(values[1]);
				if (begin >= end) { // ���ֵǰ��˳�������⡣
					begin = 0;
					end = 10;
				}
			}
		} catch (Exception _ex) {

		}
		return new int[] { begin, end };
	}

	class MyTMO extends AbstractTMO {

		public HashVO getPub_templet_1Data() {
			HashVO vo = new HashVO(); //
			vo.setAttributeValue("templetcode", "C"); // ģ�����,��������޸�
			vo.setAttributeValue("templetname", "Ա������δ�����Ա"); // ģ������
			vo.setAttributeValue("templetname_e", ""); // ģ������
			vo.setAttributeValue("tablename", "WLTDUAL"); // ��ѯ���ݵı�(��ͼ)��
			vo.setAttributeValue("pkname", "ID"); // ������
			vo.setAttributeValue("pksequencename", "S_SAL_PERSON_CHECK_SCORE"); // ������
			vo.setAttributeValue("savedtablename", "SAL_PERSON_CHECK_SCORE"); // �������ݵı���
			vo.setAttributeValue("CardWidth", "577"); // ��Ƭ���
			vo.setAttributeValue("Isshowlistpagebar", "N"); // �б��Ƿ���ʾ��ҳ��
			vo.setAttributeValue("Isshowlistopebar", "N"); // �б��Ƿ���ʾ������ť��
			vo.setAttributeValue("listcustpanel", null); // �б��Զ������
			vo.setAttributeValue("cardcustpanel", null); // ��Ƭ�Զ������

			vo.setAttributeValue("TREEPK", "id"); // �б��Ƿ���ʾ������ť��
			vo.setAttributeValue("TREEPARENTPK", ""); // �б��Ƿ���ʾ������ť��
			vo.setAttributeValue("Treeviewfield", ""); // �б��Ƿ���ʾ������ť��
			vo.setAttributeValue("Treeisshowtoolbar", "Y"); // ������ʾ������
			vo.setAttributeValue("Treeisonlyone", "N"); // ������ʾ������
			vo.setAttributeValue("Treeseqfield", "seq"); // �б��Ƿ���ʾ������ť��
			vo.setAttributeValue("Treeisshowroot", "Y"); // �б��Ƿ���ʾ������ť��
			return vo;
		}

		@Override
		public HashVO[] getPub_templet_1_itemData() {
			List itemvoList = new ArrayList();

			HashVO itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "userid"); // Ψһ��ʶ,����ȡ���뱣�� //ָ��ID
			itemVO.setAttributeValue("itemname", "����"); // ��ʾ����
			itemVO.setAttributeValue("itemname_e", "Id"); // ��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); // �ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); // ��������
			itemVO.setAttributeValue("refdesc", null); // ���ն���
			itemVO.setAttributeValue("issave", "Y"); // �Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
			itemVO.setAttributeValue("editformula", null); // �༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "145"); // �б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150"); // ��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); // �б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "N"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemvoList.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "username"); // Ψһ��ʶ,����ȡ���뱣�� //ָ��ID
			itemVO.setAttributeValue("itemname", "������"); // ��ʾ����
			itemVO.setAttributeValue("itemname_e", "Id"); // ��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); // �ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); // ��������
			itemVO.setAttributeValue("refdesc", null); // ���ն���
			itemVO.setAttributeValue("issave", "N"); // �Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
			itemVO.setAttributeValue("editformula", null); // �༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "80"); // �б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150"); // ��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "N"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemvoList.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "deptname"); // Ψһ��ʶ,����ȡ���뱣�� //ָ��ID
			itemVO.setAttributeValue("itemname", "����"); // ��ʾ����
			itemVO.setAttributeValue("itemname_e", "Id"); // ��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); // �ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); // ��������
			itemVO.setAttributeValue("refdesc", null); // ���ն���
			itemVO.setAttributeValue("issave", "N"); // �Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
			itemVO.setAttributeValue("editformula", null); // �༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "80"); // �б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150"); // ��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "N"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemvoList.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "curr"); // Ψһ��ʶ,����ȡ���뱣�� //ָ��ID
			itemVO.setAttributeValue("itemname", "����"); // ��ʾ����
			itemVO.setAttributeValue("itemname_e", "Id"); // ��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); // �ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); // ��������
			itemVO.setAttributeValue("refdesc", null); // ���ն���
			itemVO.setAttributeValue("issave", "N"); // �Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
			itemVO.setAttributeValue("editformula", null); // �༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "80"); // �б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150"); // ��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "N"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemvoList.add(itemVO);
			return (HashVO[]) itemvoList.toArray(new HashVO[0]);
		}

	}
}