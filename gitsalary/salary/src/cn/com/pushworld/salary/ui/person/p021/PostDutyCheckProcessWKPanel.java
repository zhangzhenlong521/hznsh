package cn.com.pushworld.salary.ui.person.p021;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JTextField;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;
import cn.com.infostrategy.ui.report.style2.DefaultStyleReportPanel_2;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

/**
 * �������۽����ѯ.
 * @author haoming
 * create by 2014-3-25
 */
public class PostDutyCheckProcessWKPanel extends AbstractWorkPanel implements ActionListener {

	private static final long serialVersionUID = -2743335801600774045L;

	private DefaultStyleReportPanel_2 dr = null;
	private BillQueryPanel bq = null;

	private String exportFileName = "���˿��˽���_";

	public void initialize() {
		dr = new DefaultStyleReportPanel_2("SAL_TARGET_CHECK_LOG_PERSON", "");
		bq = dr.getBillQueryPanel();
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
		new SplashWindow(this, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				onQuery();
			}
		});
	}

	private void onQuery() {
		try {
			if (bq.checkValidate()) {
				SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
				String logid = UIUtil.getStringValueByDS(null, "select id from sal_target_check_log where checkdate = '" + bq.getRealValueAt("checkdate") + "'");
				dr.getBillCellPanel().loadBillCellData(ifc.getPostDutyCheckProcess(logid));
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
