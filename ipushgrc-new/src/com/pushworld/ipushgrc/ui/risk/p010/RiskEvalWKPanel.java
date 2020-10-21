package com.pushworld.ipushgrc.ui.risk.p010;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.to.workflow.design.ActivityVO;
import cn.com.infostrategy.to.workflow.design.ProcessVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.BillFrame;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.workflow.design.WorkFlowDesignWPanel;

import com.pushworld.ipushgrc.ui.wfrisk.CmpFileHistoryViewDialog;
import com.pushworld.ipushgrc.ui.wfrisk.CmpfileAndWFGraphDialog;
import com.pushworld.ipushgrc.ui.wfrisk.WFGraphEditItemDialog;
import com.pushworld.ipushgrc.ui.wfrisk.WFRiskUIUtil;
import com.pushworld.ipushgrc.ui.wfrisk.p010.WFGraphEditDialog;
import com.pushworld.ipushgrc.ui.wfrisk.p010.WFGraphEditItemPanel;

/**
 * ����ʶ��������! ������ԭ����һͼ����༭����! ֻ����ֻҪ���յ�ı༭!������Ҫͬʱ�в��������̵ķ��յ�����!
 * ��ҳ��������̵��б����������������ļ�״̬Ϊ���༭�С��͡���Ч�����������̡�Ҳ����˵�ļ�״̬Ϊ�����뷢���С����������ֹ�С�������ֹ���������ǲ�������չ���Ա���������յģ�
 * A.��ť��ֱ�����������߼�����ѡ�����̣�
 *      ������������������ļ����ļ�״̬Ϊ���༭�С�����Ҫ�������ʼ�༭����ť���ļ��ķ���״̬��Ϊ���༭�С�����ʱ�ſ��������޸ķ��յ㣬
 * ������������༭��ʱ���ļ��ķ���״̬��Ϊ����Ч������ʱ�����ļ�����Ա�ſ��Է������뷢�����ֹ���̣�
 *      ����ļ�״̬Ϊ����Ч�������չ���ԱҪ�������գ�ͬ��������ʼ�༭����ť��
 * ��������������༭����ťʱ����Ҫ�޸ķ���״̬Ϊ����Ч����ͬʱ��Ҫ����һ��С�汾�����ļ�������Ѵ���һ��С�汾�����ļ���Ҫ���Ǵ��ڵ�С�汾�����ļ���
 *      ����ļ�״̬Ϊ�����뷢���С��������ֹ�С�����ʾ �������������������
 *      ����ļ�״̬Ϊ����ֹ��������ʾ ��ֹ���ļ����ܽ��з���������
 * 
 * B.��ť���������롿���߼���ֱ����ʾ�������̲���δ�����꣨iseval=null�������������¼���ڣ�ѡ��һ����¼��ֻ�ܵ�ѡ���������ȷ������ť���ɸ��ݲ�����������ʾҳ�棨
 * ���������¼���ں����̱༭���ڷֿ���ʾ�򲻷ֿ���ʾ������ʱҲҪ�С���ʼ�༭��(�߼�ͬ��)�͡������༭����ť��������������༭��ʱ������������������ֶ�iseval='Y'���´ε��
 * ���������롿�����ܿ����ü�¼��������Ҫִ��A�еĽ����߼���
 *      ��Ϊ���������������������û�д����߼���ֻ���������в鿴�쵼�Ƿ�ͬ���޸ĺ��޸�����������ͬ���޸��Ƿ���Ҫ�ڡ���ʼ�༭����ť�����һ��������������ť��
 * ��˼�����벻��������Ҫʶ����յ㣬��ʱ�������������߼�ֻ���޸� ������������ֶ�iseval='Y'��Ҳû��Ҫ�ٷ���С�汾��
 * 
 *
 * C.��ť���鿴������ʷ�����߼��������ҳ����ѡ�м�¼����򿪸��ļ���ص��������룬����������ļ�ѡ�񴰿ڣ�ѡ��һ�������ļ���Ϣ��Ȼ��鿴��ص����������¼��
 * 
 * @author xch
 *
 */
public class RiskEvalWKPanel extends AbstractWorkPanel implements ActionListener, BillListHtmlHrefListener {

	private BillListPanel billlist_main_process = null;//�������б�
	private WLTButton btn_directeval, btn_dealapply, btn_showhistapply;//�������б��ϵİ�ť
	private WLTButton btn_beginedit, btn_risk, btn_endedit, btn_closeitemdialog;

	private WFGraphEditItemDialog itemdialog = null;//ֱ�������򿪵�һ������ͼ�Ĵ���
	private WFGraphEditDialog dealEditDialog = null;//�������룬�򿪵�ĳ�������ļ�����������ͼ�Ĵ���
	private WorkFlowDesignWPanel workFlowPanel = null;//��ǰ��ʾ������ͼ���

	private boolean editable = true;
	private boolean evalable = true;
	private boolean isdirecteval = true;
	private boolean dealSelected = false;//Ĭ�ϲ���ѡ�е������ļ����д������룬���ǵ������Դ�������������ļ��б����/2012-06-01��

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout());

		billlist_main_process = new BillListPanel("V_PROCESS_FILE_CODE1");
		//billlist_main_process.setDataFilterCustCondition("CMPFILE_ID is not null");//filestate in('1','3'),�ļ�״̬Ϊ"�༭��"��"��Ч"������
		billlist_main_process.getQuickQueryPanel().addBillQuickActionListener(this);
		btn_directeval = new WLTButton("ֱ������");
		btn_dealapply = new WLTButton("��������");
		btn_showhistapply = new WLTButton("�鿴������ʷ");
		btn_directeval.addActionListener(this);
		btn_dealapply.addActionListener(this);
		btn_showhistapply.addActionListener(this);
		billlist_main_process.addBatchBillListButton(new WLTButton[] { btn_directeval, btn_dealapply, btn_showhistapply, WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD) });
		billlist_main_process.repaintBillListButton();
		billlist_main_process.addBillListHtmlHrefListener(this);
		this.add(billlist_main_process);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == billlist_main_process.getQuickQueryPanel()) {
			onQuery();//��ҳ���ϲ�ѯ��ť���߼�
		} else if (e.getSource() == btn_directeval) {
			onDirectEval();//��ҳ����ֱ��������ť���߼�
		} else if (e.getSource() == btn_dealapply) {
			onDealAppley();//��ҳ���ϴ������밴ť���߼�
		} else if (e.getSource() == btn_showhistapply) {
			onShowHistApply();//��ҳ���ϲ鿴������ʷ��ť���߼�
		} else if (e.getSource() == btn_risk) {
			onEvaluateProcessRisk();//ֱ�����������봦�� ���򿪵�����ͼ������ķ��յ㰴ť
		} else if (e.getSource() == btn_closeitemdialog) {
			onCloseItemDialog();//ֱ������������ͼ�ϵĹرհ�ť
		} else if (e.getSource() == btn_beginedit) {
			onBeginEditRisk();//���յ��б����Ŀ�ʼ�༭��ť�߼�
		} else if (e.getSource() == btn_endedit) {
			onEndEditRisk();//���յ��б����Ľ����༭��ť�߼�
		}
	}

	/**
	 * ��д��ѯ��ť���¼�
	 */
	private void onQuery() {
		String str_sql = billlist_main_process.getQuickQueryPanel().getQuerySQL();
		if (str_sql.contains("risk_name like")) {//�Ƿ�Ҫ��ѯ�������ƣ����Ҫ��ѯ�Ļ�����Ҫ�ӷ������̱��в�����̣�select * from V_PROCESS_FILE where 1=1  and (filestate in('1','3'))  and (riskname like '%3%' and riskname like '%�淶%')
			str_sql = str_sql.substring(0, str_sql.indexOf("risk_name like") - 1) + " wfprocess_id in(select wfprocess_id from v_risk_process_file where " + str_sql.substring(str_sql.indexOf("risk_name like") - 1, str_sql.length()) + ")";
		}
		billlist_main_process.QueryData(str_sql);
	}

	/**
	 * ��ֱ�����������߼������ѡ����������������ļ�״̬Ϊ���뷢���С������ֹ�л��ֹ��ֻ�ɲ鿴���༭�л���Ч����ԶԷ��յ����ʶ������
	 */
	private void onDirectEval() {
		BillVO billvo = billlist_main_process.getSelectedBillVO();
		if (billvo == null) {//���û��ѡ�м�¼������Ҫѡ��һ�����̽�������������ֱ�Ӷ�ѡ�����̽�������
			MessageBox.showSelectOne(this);
			return;
		}
		billlist_main_process.refreshCurrSelectedRow();
		billvo = billlist_main_process.getSelectedBillVO();
		String filestate = billvo.getStringValue("filestate");
		if ("2".equals(filestate) || "4".equals(filestate) || "5".equals(filestate)) {//���뷢���С������ֹ�кͷ�ֹ״̬���ļ��ǲ�������з���������
			filestate = billvo.getStringValue("filestatename");//�õ��ļ�״̬��ʾ����
			if (MessageBox.showConfirmDialog(this, "�������ļ���״̬Ϊ[" + filestate + "],���ܽ��б༭,�Ƿ���Ҫ�鿴��") != JOptionPane.YES_OPTION) {//��ʾ�Ƿ�鿴
				return;
			}
			editable = false;//�޸ı༭״̬Ϊ���ɱ༭
		} else {
			editable = true;
		}
		onShowItemDialog(billvo.getStringValue("cmpfile_id"), billvo.getStringValue("wfprocess_id"), billvo.getStringValue("wfprocess_name"));
	}

	/**
	 * ��ֱ��������������ҳ�棬��ѡ����һ��������Ϣ���򿪸�����ͼ�����з��յ���ɾ�Ĳ�����
	 * @param _wfid   ����id 
	 * @param _wfname ��������
	 */
	private void onShowItemDialog(String _cmpfileid, String _wfid, String _wfname) {
		isdirecteval = true;
		if (this.editable) {
			itemdialog = new WFGraphEditItemDialog(this, "ʶ����յ�[" + _wfname + "]", _wfid, false, false);
		} else {
			itemdialog = new WFGraphEditItemDialog(this, "�鿴���յ�[" + _wfname + "]", _wfid, false, false);
		}
		itemdialog.setMaxWindowMenuBar();
		WLTPanel northbtnPanel = new WLTPanel(WLTPanel.HORIZONTAL_LEFT_TO_RIGHT, new BorderLayout(), LookAndFeel.defaultShadeColor1, false); //
		WLTPanel southbtnPanel = new WLTPanel(WLTPanel.HORIZONTAL_FROM_MIDDLE, new FlowLayout(FlowLayout.CENTER), LookAndFeel.defaultShadeColor1, false); //

		WFGraphEditItemPanel itempanel = itemdialog.getItempanel();
		workFlowPanel = itempanel.getWorkFlowPanel();
		//btn_closeitemdialog = new WLTButton(" �ر� ");
		//btn_closeitemdialog.addActionListener(this);
		WLTPanel northEastPanel = new WLTPanel(WLTPanel.HORIZONTAL_RIGHT_TO_LEFT, new FlowLayout(FlowLayout.CENTER), LookAndFeel.defaultShadeColor1, false); //
		if (this.editable) {
			try {
				String riskstate = UIUtil.getStringValueByDS(null, "select riskstate from cmp_cmpfile where id =" + _cmpfileid);
				btn_endedit = new WLTButton("�����༭");
				btn_endedit.addActionListener(this);
				if ("�༭��".equals(riskstate)) {//����״̬���༭�У���Ч������Ǳ༭�еĻ�����ʶ���޸ġ�ɾ���������༭�Ȱ�ť��������Ҫ���µ������ʼ�༭����������������ť��
					this.evalable = true;
					btn_risk = new WLTButton("���յ�", "office_016.gif");
					northEastPanel.add(btn_risk);
					northEastPanel.add(btn_endedit);
				} else {
					this.evalable = false;
					btn_risk = new WLTButton("�鿴���յ�", "office_016.gif");
					btn_beginedit = new WLTButton("��ʼ�༭");
					btn_beginedit.addActionListener(this);
					btn_endedit.setVisible(false);
					northEastPanel.add(btn_beginedit);
					northEastPanel.add(btn_risk);
					northEastPanel.add(btn_endedit);
				}
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		} else {
			this.evalable = false;
			btn_risk = new WLTButton("�鿴���յ�", "office_016.gif");
			northEastPanel.add(btn_risk);
		}
		btn_risk.addActionListener(this);
		northbtnPanel.add(northEastPanel, BorderLayout.EAST);

		JRadioButton radioBtn = new JRadioButton(_wfname); //
		radioBtn.setOpaque(false);
		radioBtn.setSelected(true);
		northbtnPanel.add(radioBtn, BorderLayout.CENTER);

		//southbtnPanel.add(btn_closeitemdialog); //ȥ���ظ���ť[�ر�], Gwang 2013/4/15 
		itempanel.add(northbtnPanel, BorderLayout.NORTH);
		itempanel.add(southbtnPanel, BorderLayout.SOUTH);
		itemdialog.setVisible(true);
	}

	/**
	 * ���������롿���߼�����Ϊ���������������ĳ�������ļ����������̣���������򿪵��������ļ�����������ͼ��
	 */
	private void onDealAppley() {
		BillListDialog listdialog = new BillListDialog(this, "��ѡ��һ����¼���д���", "CMP_RISKEVAL_CODE1");
		BillListPanel listpanel_apply = listdialog.getBilllistPanel();
		if (this.dealSelected) {//��Խϼ��Ϊѡ����һ�����̽��д���������Щ���壬��Ϊ�����ǻ��������ļ��ģ������������ÿ������ã��Ƿ�ÿ�ζ������������̵�����
			BillVO billvo = billlist_main_process.getSelectedBillVO();
			if (billvo == null) {//���û��ѡ�м�¼������Ҫѡ��һ�������ļ���������������ֱ�Ӷ�ѡ�������ļ���������
				listpanel_apply.QueryData("select eval.* from CMP_RISKEVAL eval,pub_wf_prinstance prinstance where eval.wfprinstanceid=prinstance.id and prinstance.status='END' and (eval.iseval is null or eval.iseval='') order by eval.cmpfile_id,eval.evaldate desc");//��ѯ���������ѽ�������δ�������
			} else {
				listpanel_apply.QueryData("select eval.* from CMP_RISKEVAL eval,pub_wf_prinstance prinstance where eval.wfprinstanceid=prinstance.id and prinstance.status='END' and (eval.iseval is null or eval.iseval='') and eval.cmpfile_id=" + billvo.getStringValue("cmpfile_id")
						+ " order by eval.cmpfile_id,eval.evaldate desc");//��ѯ���������ѽ�������δ�������
			}
		} else {
			listpanel_apply.QueryData("select eval.* from CMP_RISKEVAL eval,pub_wf_prinstance prinstance where eval.wfprinstanceid=prinstance.id and prinstance.status='END' and (eval.iseval is null or eval.iseval='') order by eval.cmpfile_id,eval.evaldate desc");//��ѯ���������ѽ�������δ�������
		}
		if (listpanel_apply.getRowCount() == 0) {
			listdialog.dispose();
			MessageBox.show(this, "û��Ҫ���������!");
			return;
		} else if (listpanel_apply.getRowCount() > 1) {//�����Ҫ����ļ�¼��ֹһ������Ҫѡ������һ�������ֻ��һ������ֱ�Ӵ�
			listpanel_apply.getBillListBtnPanel().setVisible(false);
			listpanel_apply.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));
			listpanel_apply.repaintBillListButton();
			listpanel_apply.setQuickQueryPanelVisiable(false);
			listpanel_apply.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // ��ѡ
			listdialog.setVisible(true);
			if (listdialog.getCloseType() != 1) {//������ǵ��ȷ���رյ���ֱ���˳���
				return;
			}
		} else {
			listpanel_apply.setSelectedRow(0);
			listdialog.onConfirm();
		}

		final String eval_id = listdialog.getReturnBillVOs()[0].getStringValue("id");
		final String cmpfileid = listdialog.getReturnBillVOs()[0].getStringValue("cmpfile_id");
		final String cmpfilename = listdialog.getReturnBillVOs()[0].getStringValue("cmpfile_name");
		String[][] processes = null;// �����ļ�����������
		try {
			processes = UIUtil.getStringArrayByDS(null, "select id,code,name from pub_wf_process where cmpfileid =" + cmpfileid + " order by userdef04");
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
		if (processes == null || processes.length == 0) {
			MessageBox.show(this, "������û�ж�Ӧ�����̣����ܽ��д���!");
			return;
		}
		BillFrame billframe = new BillFrame("��������");
		BillCardPanel cardpanel = new BillCardPanel("CMP_RISKEVAL_CODE1");
		cardpanel.queryDataByCondition("id=" + eval_id);
		cardpanel.setGroupExpandable("������Ϣ", false);//����������Ϣ��
		if (new TBUtil().getSysOptionBooleanValue("����ʶ�������д�������ʱ���������ͼ�Ƿ�ֿ���ʾ", true)) {
			billframe.add(cardpanel);
			billframe.setSize(530, 760);//�����������봰�ڵĴ�С
			billframe.setAlwaysOnTop(true);//�����������봰��һֱ����ǰ�棬��������õĻ����������ͼʱ�������봰�ھͻ��ܵ�����ȥ
			billframe.setVisible(true);//�������������ʾʱ����ʾ�������봰��

			isdirecteval = false;
			dealEditDialog = new WFGraphEditDialog(this, "���ձ༭", 1000, 700, cmpfileid, cmpfilename, processes, false); //�������룬����ĳ�������ļ�����������ͼ�Ĵ���
			workFlowPanel = dealEditDialog.getGraphPanel().getCurrSelectedItemPanel().getWorkFlowPanel();//���»�õ�ǰ������ͼ������
			dealEditDialog.setShowRefPanel(false);//���ò���ʾ������ؼ�������صİ�ť���
			dealEditDialog.getGraphPanel().setDividerLocation(200);//���÷ָ������ұ߰�ť�Ĺ̶����
			dealEditDialog.setMaxWindowMenuBar();//���ô��ڿ������С��

			if (this.editable) {
				WLTPanel northEastPanel = new WLTPanel(WLTPanel.HORIZONTAL_RIGHT_TO_LEFT, new FlowLayout(FlowLayout.CENTER), LookAndFeel.defaultShadeColor1, false); //
				try {
					String riskstate = UIUtil.getStringValueByDS(null, "select riskstate from cmp_cmpfile where id =" + cmpfileid);
					btn_endedit = new WLTButton("�����༭");
					btn_endedit.addActionListener(this);
					if ("�༭��".equals(riskstate)) {//����״̬���༭�У���Ч������Ǳ༭�еĻ�����ʶ���޸ġ�ɾ���Ȱ�ť��������Ҫ���µ������ʼ�༭����������������ť��
						this.evalable = true;//���÷��յ����ʶ��
						btn_risk = new WLTButton("���յ�", "office_016.gif");
						northEastPanel.add(btn_risk);
						northEastPanel.add(btn_endedit);
					} else {
						this.evalable = false;//��û�е����ʼ�༭�������ļ��ķ���״̬(riskstate)Ϊ��Ч�������÷��յ㲻��ʶ��ֻ�ɲ鿴
						btn_risk = new WLTButton("�鿴���յ�", "office_016.gif");
						btn_beginedit = new WLTButton("��ʼ�༭");
						btn_beginedit.addActionListener(this);
						btn_endedit.setVisible(false);
						northEastPanel.add(btn_beginedit);
						northEastPanel.add(btn_risk);
						northEastPanel.add(btn_endedit);
					}
				} catch (Exception e) {
					MessageBox.showException(this, e);
				}
				dealEditDialog.setSize(1024, 760);
				dealEditDialog.setLocation(0, 0);
				dealEditDialog.getGraphPanel().getNorthPanel().add(northEastPanel);
			} else {
				this.evalable = false;//�������ļ�״̬Ϊ"����������"��"��ֹ������"��"��ֹ"���ʲ��ɱ༭�����÷��յ㲻��ʶ��ֻ�ɲ鿴
				btn_risk = new WLTButton("�鿴���յ�", "office_016.gif");
				dealEditDialog.getGraphPanel().getNorthPanel().add(btn_risk);
			}
			btn_risk.addActionListener(this);
			dealEditDialog.setVisible(true);
		} else {
			//���������⣬����ͼӦ���Ƕ���ģ�����
			billframe.maxToScreenSizeBy1280AndLocationCenter();
			//onShowItemDialog(cmpfileid, billvo.getStringValue("wfprocess_id"), billvo.getStringValue("wfprocess_name"));

			JScrollPane scrollpanel = new JScrollPane(itemdialog.getItempanel());

			WLTSplitPane split = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, cardpanel, scrollpanel);
			split.setOneTouchExpandable(true);
			split.setDividerSize(10);
			split.setDividerLocation(500);
			billframe.add(split);
			billframe.setVisible(true);
		}
	}

	/**
	 * ���鿴������ʷ�����߼�
	 */
	private void onShowHistApply() {
		BillVO billvo = null;//billlist_main_process.getSelectedBillVO(); //����Խϼ��Ϊѡ����һ�����̽��в鿴��Щ���壬��Ϊ�����ǻ��������ļ��ģ�����������ע�͵���ÿ�ζ���ѡ�������ļ����в鿴
		if (billvo == null) {//���û��ѡ�м�¼������Ҫѡ��һ�������ļ����в鿴
			BillListDialog listdialog_file = new BillListDialog(this, "��ѡ�������ļ����в鿴", TBUtil.getTBUtil().getSysOptionHashItemStringValue("�������������ļ���ģ��", "ά��", "CMP_CMPFILE_CODE2"));//ֻ�ܲ鿴�����ŵ������ļ��������¼�����/2012-07-13��
			BillListPanel billlist_file = listdialog_file.getBilllistPanel();
			billlist_file.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));//���������ť�����/2012-03-28��
			billlist_file.repaintBillListButton();
			billlist_file.setDataFilterCustCondition("id in(select distinct(cmpfile_id) from cmp_riskeval where wfprinstanceid is not null)");//�Ѿ����������������̵��ļ�
			billlist_file.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // ��ѡ
			billlist_file.addBillListHtmlHrefListener(this);
			billlist_file.getQuickQueryPanel().resetAllQuickQueryCompent();//��������Ĭ�ϲ�ѯ��������ꡢ2012-03-28��
			listdialog_file.setVisible(true);
			if (listdialog_file.getCloseType() == 1) {
				BillVO[] billvos = listdialog_file.getReturnBillVOs();
				BillListDialog listdialog_apply = new BillListDialog(this, "�����ļ���" + billvos[0].getStringValue("cmpfilename") + "����������ʷ", "CMP_RISKEVAL_CODE1");
				BillListPanel listpanel_apply = listdialog_apply.getBilllistPanel();
				listpanel_apply.QueryDataByCondition("cmpfile_id=" + billvos[0].getStringValue("id"));
				if (listpanel_apply.getRowCount() == 0) {
					listdialog_apply.dispose();
					MessageBox.show(this, "�������ļ�û����������!");
					return;
				}

				listpanel_apply.getBillListBtnPanel().setVisible(false);
				listpanel_apply.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));
				listpanel_apply.repaintBillListButton();
				listpanel_apply.setItemVisible("cmpfile_name", false);//���ڱ��������ļ�����
				listpanel_apply.setQuickQueryPanelVisiable(false);
				listdialog_apply.getBtn_confirm().setVisible(false);
				listdialog_apply.getBtn_cancel().setText("�ر�");
				listdialog_apply.setVisible(true);
			}
		} else {
			String cmpfile_id = billvo.getStringValue("cmpfile_id");
			String cmpfilename = billvo.getStringValue("cmpfilename");
			BillListDialog listdialog_apply = new BillListDialog(this, "�����ļ���" + cmpfilename + "����������ʷ", "CMP_RISKEVAL_CODE1");
			BillListPanel listpanel_apply = listdialog_apply.getBilllistPanel();
			listpanel_apply.QueryData("select * from CMP_RISKEVAL where cmpfile_id=" + cmpfile_id);//
			if (listpanel_apply.getRowCount() == 0) {
				listdialog_apply.dispose();
				MessageBox.show(this, "�������ļ�û����������!");
				return;
			}
			listpanel_apply.getBillListBtnPanel().setVisible(false);
			listpanel_apply.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));
			listpanel_apply.repaintBillListButton();
			listpanel_apply.setItemVisible("cmpfile_name", false);//���ڱ��������ļ�����
			listpanel_apply.setQuickQueryPanelVisiable(false);
			listdialog_apply.getBtn_confirm().setVisible(false);
			//listdialog_apply.getBtn_cancel().setText("�ر�");			
			listdialog_apply.setVisible(true);
		}
	}

	/**
	 * ֱ������ ���򿪵�����ͼ������Ĺرհ�ť���߼�
	 */

	private void onCloseItemDialog() {
		if (billlist_main_process.getSelectedBillVO() != null) {
			billlist_main_process.refreshData();
		}
		itemdialog.dispose();
	}

	private void onEvaluateProcessRisk() {
		if (!this.isdirecteval) {//����Ǵ������룬��Ϊ�ǻ���ĳ�������ļ��ģ��������ļ�����������̣�����workFlowPanel ��ȷ������Ҫ����������һ�£�
			this.workFlowPanel = dealEditDialog.getGraphPanel().getCurrSelectedItemPanel().getWorkFlowPanel();
		}
		ActivityVO activityvo = workFlowPanel.getSelectedActivityVO();
		if (activityvo == null) {
			MessageBox.show(itemdialog == null ? dealEditDialog : itemdialog, "��ѡ��һ�����ڽ��д˲���");
			return;
		}
		String cmpfile_id = workFlowPanel.getCurrentProcessVO().getCmpfileid();
		String wfprocess_id = workFlowPanel.getCurrentProcessVO().getId();
		String wfprocess_code = workFlowPanel.getCurrentProcessVO().getCode();
		String wfprocess_name = workFlowPanel.getCurrentProcessVO().getName();
		String cmpfile_name = null;
		if (this.isdirecteval) {
			BillVO billvo = billlist_main_process.getSelectedBillVO();
			cmpfile_name = billvo.getStringValue("cmpfilename");
		} else {
			try {
				cmpfile_name = UIUtil.getStringValueByDS(null, "select cmpfilename from cmp_cmpfile where id=" + cmpfile_id);
			} catch (Exception e) {
				MessageBox.showException(itemdialog == null ? dealEditDialog : itemdialog, e);
			}
		}
		String activityid = activityvo.getId() + "";
		//ϵͳ�ɸ���ƽ̨��������������һͼ��������ط��յ�Ľ��棬Ϊ�������������ͳһ�����ڷ���ʶ����ͨ��ƽ̨�������á��Զ������ֱ�����������ࡱ���Զ������ʶ����档
		//ע�⣺���õ�����Ҫ�̳�BillDialog�࣬�����й涨�Ĺ��췽��
		//���췽���еĲ����ֱ��ʾ��������壬���ڱ��⣬�����ļ�id�������ļ����ƣ�����id�����̱��룬�������ƣ�����id�����ڱ��룬�������ƣ��������ͣ�����"�����ļ�";"����";"����" �����Ƿ�ɱ༭��Boolean�ࣩ�����/2012-07-16��
		String str_clsName = TBUtil.getTBUtil().getSysOptionStringValue("�Զ������ֱ������������", "com.pushworld.ipushgrc.ui.wfrisk.p010.LookRiskDialog");
		try {
			Class dialog_class = Class.forName(str_clsName);
			Class cp[] = { java.awt.Container.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, Boolean.class }; //���췽���еĲ������ͣ�
			Constructor constructor = dialog_class.getConstructor(cp);
			BillDialog dialog = (BillDialog) constructor.newInstance(new Object[] { itemdialog == null ? dealEditDialog : itemdialog, null, cmpfile_id, cmpfile_name, wfprocess_id, wfprocess_code, wfprocess_name, activityid, activityvo.getCode(), activityvo.getWfname(), "����",
					this.editable && this.evalable }); //
			dialog.setVisible(true);
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}

		if (this.isdirecteval) {
			itemdialog.getItempanel().resetRisk(activityid);
		} else {//����Ǵ������룬��Ϊ�ǻ���ĳ�������ļ��ģ��������ļ�����������̣�����workFlowPanel ��ȷ������Ҫ����������һ�£�
			dealEditDialog.getGraphPanel().getCurrSelectedItemPanel().resetRisk(activityid);
		}
	}

	/**
	 * ����ʼ�༭����ť���߼����������ļ��ķ���״̬��riskstate���޸�Ϊ���༭�С������������ļ��Ͳ��ܷ��𷢲����̣�����ȷ��չ���Ա�޸�����յ㣬����������༭����ť��ſ��Է���
	 */
	private void onBeginEditRisk() {
		//������Ҫ���� �����ļ��Ͳ��ܷ��𷢲����̣�����ȷ��չ���Ա�޸�����յ㣬����������༭����ť��ſ��Է���
		this.evalable = true;
		btn_beginedit.setVisible(false);
		btn_endedit.setVisible(true);
		btn_risk.setText("���յ�");
		btn_risk.setToolTipText("���յ�");
		String cmpfile_id = workFlowPanel.getCurrentProcessVO().getCmpfileid();
		try {
			UpdateSQLBuilder isql = new UpdateSQLBuilder("cmp_cmpfile", "id=" + cmpfile_id); //
			isql.putFieldValue("riskstate", "�༭��");
			UIUtil.executeUpdateByDS(null, isql.getSQL());
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	/**
	 * �������༭����ť���߼����������ļ��ķ���״̬��riskstate���޸�Ϊ����Ч�����˺������ļ��Ϳ��Է������ֹ
	 */
	private void onEndEditRisk() {

		String cmpfile_id = null;
		String filestate = null;
		String cmpfilename = null;
		try {
			if (isdirecteval) {//ֱ������
				if (MessageBox.showConfirmDialog(itemdialog, "��ȷ��Ҫ�����༭��?") != JOptionPane.YES_OPTION) {
					return;
				}
				billlist_main_process.refreshCurrSelectedRow();//ˢ��һ�£����滹��Ҫ����ļ�״̬��
				BillVO billvo = billlist_main_process.getSelectedBillVO();
				cmpfile_id = billvo.getStringValue("cmpfile_id");
				filestate = billvo.getStringValue("filestate");
				cmpfilename = billvo.getStringValue("cmpfilename");
			} else {
				if (MessageBox.showConfirmDialog(dealEditDialog, "�������Ƿ������,���ѡ���ǡ����������ٴ���?") != JOptionPane.YES_OPTION) {
					return;
				}
				ProcessVO processvo = dealEditDialog.getGraphPanel().getCurrSelectedItemPanel().getWorkFlowPanel().getCurrentProcessVO();
				cmpfile_id = processvo.getCmpfileid();
				String[][] cmpfile = UIUtil.getStringArrayByDS(null, "select filestate,cmpfilename from cmp_cmpfile where id =" + cmpfile_id);
				if (cmpfile == null || cmpfile.length == 0) {
					return;
				}
				filestate = cmpfile[0][0];
				cmpfilename = cmpfile[0][1];
				UIUtil.executeUpdateByDS(null, "update cmp_riskeval set iseval='Y' where cmpfile_id=" + cmpfile_id);
			}

			if ("3".equals(filestate)) {//�������Ч�������ļ�������Ҫ���·���һ��С�汾
				String maxversionno = UIUtil.getStringValueByDS(null, "select versionno from cmp_cmpfile where id=" + cmpfile_id);
				String newversionno = "1.01";
				if (maxversionno == null) {//���û�а汾�ţ�����û�з������ֶ��޸��������ļ���״̬Ϊ"��Ч"
					return;
				} else if (maxversionno.contains(".")) {
					int length = maxversionno.substring(maxversionno.indexOf("."), maxversionno.length()).length();
					if (length == 1) {
						newversionno = maxversionno + "01";
					} else if (length == 2) {
						newversionno = maxversionno + "1";
					} else if (length == 3) {
						newversionno = maxversionno;
					}
				} else {
					newversionno = maxversionno + ".01";
				}

				final String tmp_cmpfile_id = cmpfile_id;
				final String tmp_cmpfilename = cmpfilename;
				final String tmp_newversionno = newversionno;
				final boolean showreffile = new TBUtil().getSysOptionBooleanValue("�����ļ��Ƿ�����������word", true);
				new SplashWindow(itemdialog == null ? dealEditDialog : itemdialog, "�������ļ����ļ�״̬Ϊ[��Ч],���ڷ���С�汾,��ȴ�...", new AbstractAction() {
					public void actionPerformed(ActionEvent e) {
						new WFRiskUIUtil().dealpublish(itemdialog == null ? dealEditDialog : itemdialog, tmp_cmpfile_id, tmp_cmpfilename, showreffile, tmp_newversionno, true);
					}
				});

			}
			UpdateSQLBuilder isql = new UpdateSQLBuilder("cmp_cmpfile", "id=" + cmpfile_id); //
			isql.putFieldValue("riskstate", "��Ч");
			UIUtil.executeUpdateByDS(null, isql.getSQL());//�������ļ��ķ���״̬��Ϊ����Ч�����˺������ļ��ſ��Է������ֹ
		} catch (Exception e) {
			MessageBox.showException(itemdialog == null ? dealEditDialog : itemdialog, e);
		}
		if (isdirecteval) {
			if (billlist_main_process.getSelectedBillVO() != null) {
				billlist_main_process.refreshData();
			}
			itemdialog.dispose();
		} else {
			dealEditDialog.dispose();
		}
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		if (_event.getBillListPanel() == billlist_main_process) {//��ҳ�������
			BillVO billvo = billlist_main_process.getSelectedBillVO();
			if ("wfprocess_name".equalsIgnoreCase(_event.getItemkey())) {
				WFGraphEditItemDialog itemdialog = new WFGraphEditItemDialog(this, "�鿴����[" + billvo.getStringValue("wfprocess_name") + "]", billvo.getStringValue("wfprocess_id"), false, true);
				itemdialog.setVisible(true);
			} else {
				CmpfileAndWFGraphDialog dialog = new CmpfileAndWFGraphDialog(this, "�鿴�ļ�������", billvo.getStringValue("cmpfile_id"));
				dialog.setShowprocessid(billvo.getStringValue("wfprocess_id"));
				dialog.setVisible(true);
			}
		} else {//ֱ���������鿴�ļ�����ʷ�汾������
			if ("cmpfilename".equalsIgnoreCase(_event.getItemkey())) {//�ļ�����-���ӣ��鿴�����ļ�������������
				CmpfileAndWFGraphDialog dialog = new CmpfileAndWFGraphDialog(this, "�鿴�ļ�������", _event.getBillListPanel().getSelectedBillVO().getStringValue("id"));
				dialog.setVisible(true);
			} else {// ��ʷ�汾-���ӵ��߼�
				BillVO billvo = _event.getBillListPanel().getSelectedBillVO();//�����ļ���¼
				CmpFileHistoryViewDialog dialog = new CmpFileHistoryViewDialog(this, "�ļ�[" + billvo.getStringValue("cmpfilename") + "]����ʷ�汾", billvo.getStringValue("id"), false); //
				dialog.setVisible(true); //
			}
		}
	}

	public WLTButton getBtn_directeval() {
		return btn_directeval;
	}

	public void setBtn_directeval(WLTButton btn_directeval) {
		this.btn_directeval = btn_directeval;
	}

	public WLTButton getBtn_dealapply() {
		return btn_dealapply;
	}

	public void setBtn_dealapply(WLTButton btn_dealapply) {
		this.btn_dealapply = btn_dealapply;
	}

	public WLTButton getBtn_showhistapply() {
		return btn_showhistapply;
	}

	public void setBtn_showhistapply(WLTButton btn_showhistapply) {
		this.btn_showhistapply = btn_showhistapply;
	}

	public BillListPanel getBilllist_main_process() {
		return billlist_main_process;
	}

	public boolean isDealSelected() {
		return dealSelected;
	}

	public void setDealSelected(boolean dealSelected) {
		this.dealSelected = dealSelected;
	}
}
