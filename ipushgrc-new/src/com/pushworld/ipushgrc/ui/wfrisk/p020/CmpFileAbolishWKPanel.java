package com.pushworld.ipushgrc.ui.wfrisk.p020;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.wfrisk.CmpFileHistoryViewDialog;
import com.pushworld.ipushgrc.ui.wfrisk.CmpfileAndWFGraphDialog;
import com.pushworld.ipushgrc.ui.wfrisk.PublishNewCmpFileVersionDialog;
import com.pushworld.ipushgrc.ui.wfrisk.WFRiskUIUtil;

/**
 * ��ϵ�ļ���ֹ/����! 
 * A.��һЩ�ͻ������п�������:�༭�뷢����Ա����ͬһ�ֽ�ɫ! ��һ��Ա������༭,����Ա���𷢲�! ����Ҫ������һ�������Ĺ��ܵ�!!
 * B.��������߹�����,�����ֱ��ѡ��һ��������Աָ��ĳ���˽��з���!
 * @author xch
 *
 */
public class CmpFileAbolishWKPanel extends AbstractWorkPanel implements ActionListener, BillListHtmlHrefListener {
	private BillListPanel billList_cmpfile; // �����ļ��б�!

	private WLTButton btn_wf_process, btn_looktempfile, btn_looktempfile2; //
	private WLTButton btn_publish, btn_abolish; //����,��ֹ!!
	private boolean showreffile = TBUtil.getTBUtil().getSysOptionBooleanValue("�����ļ��Ƿ�����������word", true);

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout()); //
		String templetcode = TBUtil.getTBUtil().getSysOptionHashItemStringValue("�������������ļ���ģ��", "����", "CMP_CMPFILE_CODE3");//��ģ����������ʱ��󻷽ڳ��ֵ��°汾��newversionno���Ƿ�ͬ��isapprove �ֶΡ����/2012-07-13��
		billList_cmpfile = new BillListPanel(templetcode); //
		//billList_cmpfile.setDataFilterCustCondition("filestate in('1','3')");//ֻ�鿴�༭�к���Ч��,��Խϼǿ�ҽ��������ʾ�����еģ�Ϊ�˿��Լ�أ������û�����������̣������������ʾ
		btn_looktempfile = new WLTButton("WordԤ��");
		btn_looktempfile.setToolTipText("��ʱ�汾WordԤ��");
		btn_looktempfile2 = new WLTButton("HtmlԤ��");
		btn_looktempfile2.setToolTipText("��ʱ�汾HtmlԤ��");
		btn_looktempfile.addActionListener(this);
		btn_looktempfile2.addActionListener(this);
		if (TBUtil.getTBUtil().getSysOptionBooleanValue("�����ļ������Ƿ�������", false)) {//��������ļ����������̣�Ĭ�ϲ������̣�
			btn_wf_process = WLTButton.createButtonByType(WLTButton.LIST_WORKFLOWSTART_MONITOR); //���̷���/���
			btn_wf_process.addActionListener(this);
			billList_cmpfile.addBatchBillListButton(new WLTButton[] { btn_wf_process, btn_looktempfile, btn_looktempfile2 });// �����ļ���Ӱ�ť
		} else {
			btn_publish = new WLTButton("����");
			btn_abolish = new WLTButton("��ֹ");
			btn_publish.addActionListener(this);
			btn_abolish.addActionListener(this);
			billList_cmpfile.addBatchBillListButton(new WLTButton[] { btn_publish, btn_abolish, btn_looktempfile, btn_looktempfile2 });// �����ļ���Ӱ�ť
		}
		billList_cmpfile.repaintBillListButton();// �������»��ư�ť
		billList_cmpfile.addBillListHtmlHrefListener(this);
		this.add(billList_cmpfile); //
	}

	/**
	 * �б�ť�ĵ���¼�
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_publish) {//��������
			onPublishFile();
		} else if (e.getSource() == btn_abolish) {//����ֹ��
			onAbolishFile();
		} else if (e.getSource() == btn_wf_process) {//�����̷���/��ء�
			onDealProcess();
		} else if (e.getSource() == btn_looktempfile) {//��WordԤ����
			onLookTempFileByWord();
		} else if (e.getSource() == btn_looktempfile2) {//��HtmlԤ����
			onLookTempFileByHtml();
		}
	}

	/**
	 * �б������¼���һ�����ļ����Ƶ����ӣ�һ������ʷ�汾�����ӣ����ҳ�水ť̫�࣬���Խ��ļ����ƺ���ʷ�汾������Ӳ鿴��
	 * Ϊ�˺ͺ��������ļ���ͼ�ȹ��ܵ�һ�£��ļ��������Ӵ��ǲ鿴�ļ������̵�dialog���������ò鿴���ĵķ�ʽ��
	 */
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		if ("cmpfilename".equals(_event.getItemkey())) {
			onLookFileAndWf();
		} else {
			onShowHist();
		}
	}

	/**
	 * ���̴�����߼����༭�е��ļ��ض���Ҫ��������ʱ�͵��ж��������������word������û��д���ģ���������.
	 * ������ļ���������ʵ�������������꣬���ҵ�ǰ��¼����ʵ���Ĵ����ˣ���������̣����·������̡�
	 * ��������������ϴ����̴�����Ϊ�쵼��ͬ�ⷢ����ͬ���ֹ�����д��Ҫ�鿴���������������Ӧ���޸ģ��������̽���ʱ����������̴����¼!!!\
	 * ����Ҫ��д�����µ�����̴���ʱ���!!!�����Ǳ�д��(Ҳ�������̷�����)
	 */
	private void onDealProcess() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		billList_cmpfile.refreshCurrSelectedRow();
		billVO = billList_cmpfile.getSelectedBillVO();
		String cmpfileid = billVO.getStringValue("id");
		String filestate = billVO.getStringValue("filestate");//1- �༭��, 2- ����������, 3- ��Ч, 4- ��ֹ������, 5- ʧЧ
		String wfprinstanceid = billVO.getStringValue("wfprinstanceid");
		if (wfprinstanceid == null) {//����Ƿ�������
			if (!onChangeFilestate(billVO, filestate)) {//�ж��Ƿ�����̴����������ͬʱ�޸��ļ�״̬��
				return;
			}
			new cn.com.infostrategy.ui.workflow.WorkFlowDealActionFactory().dealAction("deal", billList_cmpfile, null); //��������!
			try {//һ������̷���ť��ʹȡ�������̷���������״̬Ҳ�ı��ˡ������޸�ȷ�����������˲Ÿı��ļ�״̬  ��zzl��2016/1/28
				String id=UIUtil.getStringValueByDS(null, "select wfprinstanceid from cmp_cmpfile where id="+cmpfileid+"");
				if(!id.equals("") && filestate.equals("1")){
					UIUtil.executeUpdateByDS(null, "update cmp_cmpfile set filestate='2' where id="+cmpfileid+"");
					billList_cmpfile.reload();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				//�жϵ�ǰ��¼���Ƿ��Ƿ����ˣ�����ǣ����Ҹ������ѽ�������Ӧ��ɾ������������Ϣ�����·������̣�����ֻ�Ǽ������
				String creater = UIUtil.getStringValueByDS(null, "select creater from pub_wf_prinstance where status='END' and id=" + wfprinstanceid);
				if (ClientEnvironment.getCurrLoginUserVO().getId().equals(creater) && MessageBox.showConfirmDialog(billList_cmpfile, "�������ѽ���,��δͨ������,�Ƿ�Ҫ���·�������(���·���ɾ������������Ϣ)?") == JOptionPane.YES_OPTION) {
					String[] sqls = new String[] { "delete from pub_wf_prinstance where rootinstanceid=" + wfprinstanceid, "delete from pub_wf_dealpool where rootinstanceid=" + wfprinstanceid, "delete from pub_task_deal where rootinstanceid=" + wfprinstanceid, "delete from pub_task_off where rootinstanceid=" + wfprinstanceid, "update cmp_cmpfile set wfprinstanceid=null where id=" + cmpfileid };
					UIUtil.executeBatchByDS(null, sqls);//ɾ������ʵ���������¼�������¼���Ѱ��¼���������ļ�ʵ��id��Ϊ��
					if (!onChangeFilestate(billVO, filestate)) {//�ж��Ƿ�����̴����������ͬʱ�޸��ļ�״̬��
						return;
					}
					billList_cmpfile.setRealValueAt(null, billList_cmpfile.getSelectedRow(), "wfprinstanceid");//��������һ�£�������billList_cmpfile.refreshCurrSelectedRow()����ΪonChangeFilestate()�������Ѹı��ļ�״̬�������ܱ���
					new cn.com.infostrategy.ui.workflow.WorkFlowDealActionFactory().dealAction("deal", billList_cmpfile, null); //������!
				} else {//�������û�н�������ֻ�����̼��
					cn.com.infostrategy.ui.workflow.engine.WorkflowMonitorDialog wfMonitorDialog = new cn.com.infostrategy.ui.workflow.engine.WorkflowMonitorDialog(billList_cmpfile, wfprinstanceid, billVO); //
					wfMonitorDialog.setMaxWindowMenuBar();
					wfMonitorDialog.setVisible(true); //
				}
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}
		billList_cmpfile.refreshCurrSelectedRow();
	}

	/**
	 * ��������֮ǰ��Ҫ�޸������ļ����ļ�״̬
	 * @param _billvo    �����ļ�billvo
	 * @param _filestate �ļ�״̬
	 * @return           �Ƿ�������̴���
	 */
	private boolean onChangeFilestate(BillVO _billvo, String _filestate) {
		if ("2".equals(_filestate) || "4".equals(_filestate) || "5".equals(_filestate)) {//[����������]��[��ֹ������]��[��ֹ]���ļ����ܽ�����������
			MessageBox.show(this, "���ļ���״̬Ϊ[" + _billvo.getStringViewValue("filestate") + "],���ܽ��д˲���!");
		} else if ("3".equals(_filestate)) {//[��Ч]���ļ�ֻ���߷�ֹ����
			if ("�༭��".equals(_billvo.getStringValue("riskstate"))) {
				MessageBox.show(this, "�÷���״̬Ϊ[�༭��],���ܽ��д˲���,��֪ͨ����������Ա�����༭!"); //
				return false;
			}
			if (MessageBox.showConfirmDialog(this, "�ò���������[��ֹ]����, �Ƿ����?") == JOptionPane.YES_OPTION) {
				billList_cmpfile.setRealValueAt("4", billList_cmpfile.getSelectedRow(), "filestate");
				return true;
			}
		} else {//[�༭��]���ļ�Ҫ�߷�������,��������ļ�����������word����������Ϊ�գ���û�б�д������������
			if ("�༭��".equals(_billvo.getStringValue("riskstate"))) {
				MessageBox.show(this, "�÷���״̬Ϊ[�༭��],���ܽ��д˲���,��֪ͨ����������Ա�����༭!"); //
				return false;
			}
			if (MessageBox.showConfirmDialog(this, "�ò���������[����]����, �Ƿ����?") == JOptionPane.YES_OPTION) {
				String reffilename = _billvo.getStringValue("reffile", "");
				if (showreffile && "".equals(reffilename)) {
					MessageBox.show(this, "���ļ�����δ��д,���ܷ���!"); //
					return false;
				}
//				billList_cmpfile.setRealValueAt("2", billList_cmpfile.getSelectedRow(), "filestate");��ʹ�ǵ��ȡ���ļ�״̬Ҳ�ı�
				return true;
			}
		}
		return false;
	}

	/**
	 * �����°汾!!!
	 */
	private void onPublishFile() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		billList_cmpfile.refreshCurrSelectedRow();//ˢ��ѡ���У��Է����˷����˰汾�����ظ�������
		billVO = billList_cmpfile.getSelectedBillVO();//���»��billvo
		if ("5".equals(billVO.getStringValue("filestate"))) {
			MessageBox.show(this, "���ļ��Ѵ���[" + billVO.getStringViewValue("filestate") + "]״̬�����ܷ���!"); //
			return;
		}
		if ("�༭��".equals(billVO.getStringValue("riskstate"))) {
			MessageBox.show(this, "�÷���״̬Ϊ[�༭��],���ܽ��д˲���,��֪ͨ����������Ա�����༭!"); //
			return;
		}
		if ("3".equals(billVO.getStringValue("filestate")) && !MessageBox.confirm(this, "�������ļ�״̬����[��Ч], �Ƿ���Ҫ���·���?")) {//������������Ч������ʾ�Ƿ����·��������/2012-03-08��
			return;
		}
		final String cmpfileid = billVO.getStringValue("id");
		final String cmpfilename = billVO.getStringValue("cmpfilename");
		String str_oldversion = billVO.getStringValue("versionno"); //�ɵİ汾��
		final String reffilename = billVO.getStringValue("reffile", "");
		//��������ļ�����������word����������Ϊ�գ���û�б�д������������
		if (showreffile && "".equals(reffilename)) {
			MessageBox.show(this, "���ļ�����δ��д,���ܷ���!"); //
			return;
		}

		String str_newversionno = null; //
		if (str_oldversion == null || "".equals(str_oldversion)) { //���û�а汾�ţ���һ�η���Ĭ����Ϊ��1.0��
			str_newversionno = "1.0";
		} else {
			if (!str_oldversion.contains(".")) {
				str_oldversion = str_oldversion + ".0";
			}
			PublishNewCmpFileVersionDialog dialog = new PublishNewCmpFileVersionDialog(this, str_oldversion); //
			dialog.setVisible(true); //
			if (dialog.getCloseType() == 1) {
				str_newversionno = dialog.getReturnNewVersion(); //ȡ�÷��صİ汾��!!
			}
		}
		if (str_newversionno != null) { //������°汾!!
			final String newversionno = str_newversionno;
			new SplashWindow(this, "���ڷ���,��ȴ�...", new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					new WFRiskUIUtil().dealpublish(CmpFileAbolishWKPanel.this, cmpfileid, cmpfilename, showreffile, newversionno);
				}
			});
			billList_cmpfile.refreshCurrSelectedRow(); //ˢ��
		}
	}

	/**
	 * ��ֹĳ�������ļ�!
	 */
	private void onAbolishFile() {
		try {
			BillVO billVO = billList_cmpfile.getSelectedBillVO();
			if (billVO == null) {
				MessageBox.showSelectOne(this);
				return;
			}
			if ("5".equals(billVO.getStringValue("filestate"))) {
				MessageBox.show(this, "���ļ��Ѵ���[" + billVO.getStringViewValue("filestate") + "]״̬�������ظ�����!"); //
				return;
			}
			if ("�༭��".equals(billVO.getStringValue("riskstate"))) {
				MessageBox.show(this, "�÷���״̬Ϊ[�༭��],���ܽ��д˲���,��֪ͨ����������Ա�����༭!"); //
				return;
			}
			if (!MessageBox.confirm(this, "�����ļ���ֹ��ֻ�ܲ鿴,�޷�������ά��,\r\n���Ǻ�̨�޸����ݿ�,�Ƿ����?")) { //�����/2012-03-08��
				return; //
			}
			UIUtil.executeBatchByDS(null, new String[] { "update cmp_cmpfile set filestate='5' where id=" + billVO.getStringValue("id"), "update cmp_risk_editlog set filestate='5' where cmpfile_id =" + billVO.getStringValue("id") });
			billList_cmpfile.refreshCurrSelectedRow(); //
			MessageBox.show(this, "��ֹ�ɹ�!"); //
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	/**
	 * �ļ�����-���ӣ��鿴�����ļ�������������
	 */
	private void onLookFileAndWf() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, "��ѡ��һ�������ļ�!"); //
			return;
		}
		String tabcount = this.getMenuConfMapValueAsStr("�ļ��鿴ҳǩ��", "0");//����ά���ͷ�����ֹ�������ܿ������ļ��鿴ҳǩ��������ƽ̨���������ȼ�Ҫ�ߡ�Ĭ��ֵΪ0����ʾ�ò˵�������Ȩ������ƽ̨�����жϡ����/2015-02-11��
		CmpfileAndWFGraphDialog dialog = new CmpfileAndWFGraphDialog(this, "�鿴�ļ�������", billVO.getStringValue("id"), tabcount);
		dialog.setVisible(true);
	}

	/**
	 * ��ʷ�汾-���ӵ��߼�
	 */
	private void onShowHist() {
		try {
			BillVO billVO = billList_cmpfile.getSelectedBillVO();
			if (billVO == null) {
				MessageBox.showSelectOne(this);
				return;
			}
			String versionno = billVO.getStringValue("versionno");//ֻ���ݰ汾���Ƿ�Ϊ���жϼ��ɣ���Ϊ������������ļ��϶��а汾�ţ������������ɾ����ʷ�汾�Ļ�����ǰ�汾�϶���Ҫ������
			if (versionno == null) {
				MessageBox.show(this, "���ļ�δ��������û����ʷ�汾!"); //
				return;
			}
			String str_cmpfileid = billVO.getStringValue("id"); //
			CmpFileHistoryViewDialog dialog = new CmpFileHistoryViewDialog(this, "�ļ�[" + billVO.getStringValue("cmpfilename") + "]����ʷ�汾", str_cmpfileid, false); //
			dialog.setVisible(true); //
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	/**
	 * ��WordԤ�������߼�
	 */
	private void onLookTempFileByWord() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		String str_cmpfileid = billVO.getStringValue("id"); //
		try {
			new WFRiskUIUtil().openOneFileAsWord(this, str_cmpfileid);
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	/**
	 * ��HtmlԤ�������߼�
	 */
	private void onLookTempFileByHtml() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, "��ѡ��һ�������ļ�!"); //
			return;
		}
		String str_cmpfileID = billVO.getStringValue("id"); //
		int htmlStyle = 0;
		if (getMenuConfMapValueAsStr("htmlStyle") != null) {
			htmlStyle = Integer.parseInt(getMenuConfMapValueAsStr("htmlStyle"));
		}
		try {
			new WFRiskUIUtil().openOneFileAsHTML(billList_cmpfile, str_cmpfileID, htmlStyle); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}
}
