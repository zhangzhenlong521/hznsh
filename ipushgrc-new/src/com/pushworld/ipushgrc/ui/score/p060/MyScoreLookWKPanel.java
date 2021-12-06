package com.pushworld.ipushgrc.ui.score.p060;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.OfficeCompentControlVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillOfficeDialog;
import cn.com.infostrategy.ui.report.BillCellPanel;

import com.pushworld.ipushgrc.ui.IPushGRCServiceIfc;
import com.pushworld.ipushgrc.ui.score.ScoreUIUtil;
import com.pushworld.ipushgrc.ui.score.p020.PrintBillOfficeIntercept;
import com.pushworld.ipushgrc.ui.score.p020.RegisterEditWKPanel;

/**
 * Υ�����-���ҵĻ��֡����/2013-05-09��
 * @author lcj
 *
 */
public class MyScoreLookWKPanel extends AbstractWorkPanel implements ActionListener {

	private BillListPanel listPanel;
	private BillListPanel punishListPanel;
	private WLTButton btn_apply, btn_showscore, btn_Registerword, btn_showPunish, btn_effect;

	@Override
	public void initialize() {
		JTabbedPane tabpane = new JTabbedPane();
		listPanel = new BillListPanel("SCORE_USER_LCJ_Q01");
		//����"����ȷ��", ��û������Ļ���ֱ��ȷ����Ч Gwang 2014-11-26
		btn_effect = new WLTButton("����ȷ��", "office_175.gif");
		btn_apply = new WLTButton("��������", "office_044.gif");
		btn_showscore = new WLTButton("�ҵ��ܻ���", "refsearch.gif");
		btn_Registerword = new WLTButton("�鿴֪ͨ��", "files.png");

		btn_effect.addActionListener(this);
		btn_Registerword.addActionListener(this);
		btn_apply.addActionListener(this);
		btn_showscore.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] { btn_effect, btn_apply, btn_showscore, btn_Registerword });
		listPanel.repaintBillListButton();

		String curryear = TBUtil.getTBUtil().getCurrDate().substring(0, 4);
		listPanel.QueryDataByCondition("EFFECTDATE like '" + curryear + "%'");//��ѯ����ȵļ�¼

		punishListPanel = new BillListPanel("SCORE_USER_LCJ_Q04");
		btn_showPunish = new WLTButton("�ͷ����鿴");
		btn_showPunish.addActionListener(this);
		punishListPanel.addBillListButton(btn_showPunish);
		punishListPanel.repaintBillListButton();
		punishListPanel.QueryDataByCondition("EFFECTDATE like '" + curryear + "%'");//��ѯ����ȵļ�¼

		tabpane.addTab("���˻���", listPanel);
		tabpane.addTab("���˳ͷ�", punishListPanel);
		this.setLayout(new BorderLayout());
		this.add(tabpane);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_apply) {
			onApply();
		} else if (e.getSource() == btn_showscore) {
			onShowScore();
		} else if (e.getSource() == btn_Registerword) {
			onShowRegisterWorld(listPanel, "�϶�");
		} else if (e.getSource() == btn_showPunish) {
			onShowRegisterWorld(punishListPanel, "�ͷ�");
		} else if (e.getSource() == btn_effect) {
			onEffect();
		}
	}

	/**
	 * ����ȷ��
	 */
	private void onEffect() {

		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		String state = billVO.getStringValue("state");

		if ("δ����".equals(state)) {
			if (!MessageBox.confirm("����ȷ�Ϻ󽫲����Ը���,�Ƿ����?")) {
				return;
			}
		} else {
			MessageBox.show(this, "�ü�¼" + state + ",����ִ�д˲���!");
			return;
		}
		try {
			IPushGRCServiceIfc server = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
			server.effectScoreById((billVO.getStringValue("id")));
			listPanel.refreshCurrSelectedRow();
		} catch (WLTRemoteException e) {
			e.printStackTrace();
			MessageBox.showException(this, e);
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showException(this, e);
		}
	}

	/**
	 * ���������߼�
	 * �����������̡��鿴���̴����������Ϣ�����/2013-06-03��
	 */
	private void onApply() {
		BillVO billVO = listPanel.getSelectedBillVO(); //	
		if (billVO == null) {
			MessageBox.showSelectOne(this); //
			return; //
		}
		String str_wfprinstanceid = billVO.getStringValue("wfprinstanceid"); //
		if ("����Ч".equals(billVO.getStringValue("state")) && (str_wfprinstanceid == null || str_wfprinstanceid.trim().equals(""))) {
			MessageBox.show(this, "�û�������Ч,���ܷ���������!");
			return;
		}
		if (!billVO.containsKey("wfprinstanceid")) {
			MessageBox.show(listPanel, "ѡ�еļ�¼��û�ж��幤�����ֶ�(wfprinstanceid)!"); //
			return; //
		}

		if (str_wfprinstanceid == null || str_wfprinstanceid.trim().equals("")) {//�������δ�����������̣�����������
			new cn.com.infostrategy.ui.workflow.WorkFlowDealActionFactory().dealAction("deal", listPanel, null); //��������!
		} else {
			cn.com.infostrategy.ui.workflow.engine.WorkflowMonitorDialog wfMonitorDialog = new cn.com.infostrategy.ui.workflow.engine.WorkflowMonitorDialog(listPanel, str_wfprinstanceid, billVO); //
			wfMonitorDialog.setMaxWindowMenuBar();
			wfMonitorDialog.setVisible(true); //
		}
	}

	/**
	 * �鿴�����ܻ���
	 */
	private void onShowScore() {
		String userid = ClientEnvironment.getInstance().getLoginUserID();
		String username = ClientEnvironment.getInstance().getLoginUserName();
		new ScoreUIUtil().showOneUserScore(this, null, null, userid, username);
	}

	//�鿴�϶�֪ͨ��.
	private void viewRD() {
		final BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		BillCellVO cellvo = null;
		try {
			cellvo = UIUtil.getMetaDataService().getBillCellVO("Υ������϶�֪ͨ��ģ��", null, null);
		} catch (Exception ex) {
			WLTLogger.getLogger(RegisterEditWKPanel.class).error("û���ҵ�[Υ������϶�֪ͨ��ģ��]ģ��.");
		}
		try {
			if (cellvo == null) {
				BillListPanel listpanel = new BillListPanel(new cn.com.infostrategy.ui.report.cellcompent.CellTMO());
				listpanel.QueryDataByCondition(" templetcode like '%�϶�%'");
				BillListDialog listdialog = new BillListDialog(this, "��ѡ�����Ӧ��ģ��", listpanel);
				listdialog.setVisible(true);
				if (listdialog.getCloseType() != 1) {
					return;
				}
				BillVO rtvos[] = listdialog.getReturnBillVOs();
				if (rtvos.length > 0) {
					cellvo = UIUtil.getMetaDataService().getBillCellVO(rtvos[0].getStringValue("templetcode"), null, null);
				} else {
					return;
				}
			}
			HashVO hvo = new HashVO();
			String keys[] = billVO.getKeys();
			String names[] = billVO.getNames();
			for (int i = 0; i < keys.length; i++) {
				hvo.setAttributeValue(names[i].trim(), billVO.getStringViewValue(keys[i]));
				hvo.setAttributeValue(keys[i], billVO.getStringViewValue(keys[i]));
			}
			BillCellVO cellvo_new = UIUtil.getCommonService().parseCellTempetToWord(cellvo, hvo);
			BillCellPanel cellPanel = new BillCellPanel(cellvo_new, true, false);
			TBUtil tbUtil = new TBUtil();
			int daycount = tbUtil.getSysOptionIntegerValue("Υ������Զ���Чʱ��", 5);
			final BillDialog dialog = new BillDialog(this, "Ԥ��", 1000, 800);
			BillVO vo = listPanel.getSelectedBillVO();
			String currdate = UIUtil.getServerCurrDate();
			hvo.setAttributeValue("�϶�����", currdate);
			dialog.add(cellPanel, BorderLayout.CENTER);
			dialog.setVisible(true);
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	private String viewtype = TBUtil.getTBUtil().getSysOptionStringValue("Υ�����֪ͨ���鿴��ʽ", "WORD");

	/**
	 * �鿴�϶�֪ͨ��
	 **/
	private void onShowRegisterWorld(BillListPanel billListPanel, String type) {
		BillVO billVO = billListPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(billListPanel);
			return;
		}
		String filename = "";
		if (type.equals("�϶�")) {
			if ("WORD".equals(viewtype)) {
				filename = billVO.getStringValue("PUBLISHFILEPATH");
				if (filename == null) {//��ǰ����ͨ���϶�֪ͨ���϶����϶�֪ͨ�飬��������ֱ����Ч���ܣ�����Ҫ�ж�һ�¡����/2014-11-04��
					MessageBox.show(billListPanel, "�ü�¼û���϶�֪ͨ��");
					return;
				}
			} else {
				viewRDByCellPanel();
				return;
			}
		} else if (type.equals("�ͷ�")) {
			if(!"WORD".equalsIgnoreCase(viewtype)){
				viewCFByCellPanel();
				return;
			}
			filename = billVO.getStringValue("punishfilepath");
			if (filename == null) {
				MessageBox.show(billListPanel, "�ü�¼û�гͷ�֪ͨ��");
				return;
			}
		}
		if (filename.contains("/")) {
			filename = filename.substring(filename.lastIndexOf("/") + 1);
		}
		OfficeCompentControlVO officeVO = new OfficeCompentControlVO(false, false, false, null); //
		officeVO.setIfshowsave(false);
		officeVO.setIfshowprint_all(false);
		officeVO.setIfshowprint_fen(false);
		officeVO.setIfshowprint_tao(false);
		officeVO.setIfshowedit(false);
		officeVO.setToolbar(false);
		officeVO.setIfshowclose(false);
		officeVO.setPrintable(true);
		officeVO.setMenubar(false);
		officeVO.setMenutoolbar(false);
		officeVO.setIfshowhidecomment(false);
		officeVO.setTitlebar(false);
		officeVO.setIfshowprint(false);
		officeVO.setIfshowhidecomment(false);
		officeVO.setIfshowshowcomment(false);
		officeVO.setIfshowacceptedit(false);
		officeVO.setIfshowshowedit(false);
		officeVO.setIfshowhideedit(false);
		officeVO.setIfshowwater(false);
		officeVO.setIfShowResult(false); //����ʾ���������ʾ��
		officeVO.setIfselfdesc(true); //�ؼ�
		officeVO.setSubdir("/upload/score");//subdir=/upload/score
		try {
			final BillOfficeDialog officeDialog = new BillOfficeDialog(this, filename, officeVO);
			officeDialog.addSomeActionListener(new PrintBillOfficeIntercept(officeDialog));
			JPanel southpanel = new JPanel(new FlowLayout());//dialog�·��İ�ť���
			WLTButton btn_confirm = new WLTButton("��ӡ", "zt_014.gif"); //
			btn_confirm.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					officeDialog.callSwingFunctionByWebBrowse("button_printall_click");
				}
			});

			WLTButton btn_close = new WLTButton("�ر�"); //
			btn_close.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					officeDialog.setCloseType(BillDialog.CANCEL);
					officeDialog.callWebBrowseJavaScriptFunction("closedoc"); //
					officeDialog.dispose(); //
				}
			});

			southpanel.add(btn_confirm);
			southpanel.add(btn_close);
			officeDialog.getContentPane().add(southpanel, BorderLayout.SOUTH);
			officeDialog.maxToScreenSizeBy1280AndLocationCenter();
			officeDialog.setTitle("�ҵ�" + type + "֪ͨ��");
			officeDialog.setVisible(true);
		} catch (Exception e) {
			MessageBox.showException(billListPanel, e);
		}
	}

	//�鿴�϶�֪ͨ��
	private void viewRDByCellPanel() {
		final BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		BillCellVO cellvo = null;
		try {
			cellvo = UIUtil.getMetaDataService().getBillCellVO("Υ������϶�֪ͨ��ģ��", null, null);
		} catch (Exception ex) {
			WLTLogger.getLogger(RegisterEditWKPanel.class).error("û���ҵ�[Υ������϶�֪ͨ��ģ��]ģ��.");
		}
		try {
			if (cellvo == null) {
				BillListPanel listpanel = new BillListPanel(new cn.com.infostrategy.ui.report.cellcompent.CellTMO());
				listpanel.QueryDataByCondition(" templetcode like '%�϶�%'");
				BillListDialog listdialog = new BillListDialog(this, "��ѡ�����Ӧ��ģ��", listpanel);
				listdialog.setVisible(true);
				if (listdialog.getCloseType() != 1) {
					return;
				}
				BillVO rtvos[] = listdialog.getReturnBillVOs();
				if (rtvos.length > 0) {
					cellvo = UIUtil.getMetaDataService().getBillCellVO(rtvos[0].getStringValue("templetcode"), null, null);
				} else {
					return;
				}
			}
			final BillDialog dialog = new BillDialog(this, "Ԥ��", 800, 800);
			BillVO vo = listPanel.getSelectedBillVO();

			HashVO hvo = new HashVO();
			String keys[] = vo.getKeys();
			String names[] = vo.getNames();
			String currdate = UIUtil.getServerCurrDate();
			hvo.setAttributeValue("�϶�����", currdate);
			for (int i = 0; i < keys.length; i++) {
				hvo.setAttributeValue(keys[i], vo.getStringViewValue(keys[i]));
				if ("PUBLISHDATE".equals(keys[i])) {
					String date = vo.getStringViewValue(keys[i]);
					if (!TBUtil.isEmpty(date)) {
						hvo.setAttributeValue(names[i].trim(), vo.getStringViewValue(keys[i]));
					}
				} else {
					hvo.setAttributeValue(names[i].trim(), vo.getStringViewValue(keys[i]));
				}
			}
			Pub_Templet_1VO templet_1VO = UIUtil.getPub_Templet_1VO("SCORE_USER_LCJ_E01");//��ģ��
			LinkedHashMap hashmap = new LinkedHashMap();
			BillCellVO cellvo_new = UIUtil.getCommonService().parseCellTempetToWord(cellvo, hvo);
			BillCellPanel cellPanel = new BillCellPanel(cellvo_new, true, false);
			dialog.add(cellPanel, BorderLayout.CENTER);
			JPanel southpanel = new JPanel(new FlowLayout());//dialog�·��İ�ť���
			WLTButton btn_close = new WLTButton("�ر�"); //
			btn_close.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dialog.setCloseType(BillDialog.CANCEL);
					dialog.dispose(); //
				}
			});
			southpanel.add(btn_close);
			dialog.add(southpanel, BorderLayout.SOUTH);
			dialog.setVisible(true);
		} catch (Exception ex) {
			MessageBox.show(this, ex);
		}
	}

	//�鿴�ͷ�֪ͨ��
	private void viewCFByCellPanel() {
		final BillVO billVO = punishListPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		BillCellVO cellvo = null;
		try {
			cellvo = UIUtil.getMetaDataService().getBillCellVO("Υ����ֳͷ�֪ͨ��ģ��", null, null);
		} catch (Exception ex) {
			WLTLogger.getLogger(RegisterEditWKPanel.class).error("û���ҵ�[Υ����ֳͷ�֪ͨ��ģ��]ģ��.");
		}
		try {
			if (cellvo == null) {
				BillListPanel listpanel = new BillListPanel(new cn.com.infostrategy.ui.report.cellcompent.CellTMO());
				listpanel.QueryDataByCondition(" templetcode like '%�϶�%'");
				BillListDialog listdialog = new BillListDialog(this, "��ѡ�����Ӧ��ģ��", listpanel);
				listdialog.setVisible(true);
				if (listdialog.getCloseType() != 1) {
					return;
				}
				BillVO rtvos[] = listdialog.getReturnBillVOs();
				if (rtvos.length > 0) {
					cellvo = UIUtil.getMetaDataService().getBillCellVO(rtvos[0].getStringValue("templetcode"), null, null);
				} else {
					return;
				}
			}
			final BillDialog dialog = new BillDialog(this, "Ԥ��", 650, 450);
			BillVO vo = punishListPanel.getSelectedBillVO();

			HashVO hvo = new HashVO();
			String keys[] = vo.getKeys();
			String names[] = vo.getNames();
			String currdate = UIUtil.getServerCurrDate();
			hvo.setAttributeValue("", currdate);
			for (int i = 0; i < keys.length; i++) {
				hvo.setAttributeValue(keys[i], vo.getStringViewValue(keys[i]));
				hvo.setAttributeValue(names[i].trim(), vo.getStringViewValue(keys[i]));
			}
			BillCellVO cellvo_new = UIUtil.getCommonService().parseCellTempetToWord(cellvo, hvo);
			BillCellPanel cellPanel = new BillCellPanel(cellvo_new, true, false);
			dialog.add(cellPanel, BorderLayout.CENTER);
			JPanel southpanel = new JPanel(new FlowLayout());//dialog�·��İ�ť���
			WLTButton btn_close = new WLTButton("�ر�"); //
			btn_close.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dialog.setCloseType(BillDialog.CANCEL);
					dialog.dispose(); //
				}
			});
			southpanel.add(btn_close);
			dialog.add(southpanel, BorderLayout.SOUTH);
			dialog.setVisible(true);
		} catch (Exception ex) {
			MessageBox.show(this, ex);
		}
	}
}