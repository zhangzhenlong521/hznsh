package com.pushworld.ipushgrc.ui.duty.p050;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.RemoteServiceFactory;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTRadioPane;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.infostrategy.ui.report.BillHtmlPanel;

import com.pushworld.ipushgrc.ui.IPushGRCServiceIfc;
import com.pushworld.ipushgrc.ui.law.LawAndRuleShowDetailDialog;
import com.pushworld.ipushgrc.ui.wfrisk.WFGraphEditItemDialog;

/**
 * �ҵĸ�λһ����!!
 * 
 * @author xch
 * 
 */
public class MyPostAndDutyQueryWKPanel extends AbstractWorkPanel implements ActionListener, ChangeListener, BillListHtmlHrefListener, BillListSelectListener {
	private String userid = ClientEnvironment.getCurrSessionVO().getLoginUserId();
	private WLTTabbedPane tabPane = new WLTTabbedPane(); // ҳǩ��ʾ���и�λ
	private HashVO[] hvo_post = null; // ��λ
	private HashVO[] hvo_duty = null; // ����
	HashMap postMap = new HashMap(); // ��¼�����Ƿ���ع��� key��Ÿ�ʽ
	// postid_radioindex����:18224_0
	// 18224_1....
	private String[] currPostId = null; // ��ǰѡ��ĸ�λID
	private TBUtil tbutil = new TBUtil();
	private FrameWorkMetaDataServiceIfc metaservice;

	private HashMap hvo_postmap = new HashMap(); // ��λ����
	private HashMap hvo_dutymap = new HashMap(); // ���𻺴�
	private String custCellTemplet = tbutil.getSysOptionStringValue("��λ˵�����Զ���ģ��", "N"); //����ط��ж������ã�Ĭ��ΪN�����Y�Ļ�������Ҫ�ѡ�mypostandduty�����ģ������Ҫ��ʾ��key����hashvo��key��Ҳ����ֱ���Զ���ģ��

	public void initialize() {
		try {
			HashVO[] myPostvos = UIUtil.getHashVoArrayByDS(null, "select * from v_pub_user_post_1 where userid = '" + userid + "' and postid is not null order by seq"); // ��½�˵����и�λ
			int first = 0; // �ж��Ƿ��ǵ�һ���ǿո�λ
			for (int i = 0; i < myPostvos.length; i++) {
				String postID = myPostvos[i].getStringValue("postid");
				String refpost = myPostvos[i].getStringValue("refpostid");
				String[] postids = null;
				if (refpost != null && refpost.contains(";")) { //����֧�ָ�λ���ѡ��[2012-07-31����]������Ŀ��̫�����
					String refs[] = tbutil.split(refpost, ";");
					if (refs != null && refs.length == 0) {
						postids = new String[] { postID, "-999999" };
					} else if (refs != null && refs.length > 0) {
						postids = new String[1 + refs.length];
						postids[0] = postID;
						for (int j = 0; j < refs.length; j++) {
							postids[1 + j] = refs[j];
						}
					}
				} else {
					postids = new String[] { postID, refpost };
				}
				if (postID == null || postID.equals("")) {
					continue;
				} else {
					first++;
				}
				WLTRadioPane radioPane = null;
				if (first == 1) { // Ĭ�ϼ��ص�һ����λ�� ��λ˵���� htmlҳ��
					radioPane = getRadioPane(postids, first);
					postMap.put(postids + "_" + 0, true);
					currPostId = postids;
				} else {
					radioPane = getRadioPane(postids, first); // �ڶ���������
					// �Ƿ��ǵ�һ����λ��
				}
				radioPane.putClientProperty("postid", postids);
				tabPane.addTab(myPostvos[i].getStringValue("postname", "û�ж����λ����"), radioPane);
			}
			if (first == 0) { // ���v_pub_user_post_1�����ݣ�����û�и�λ����ô����ʾû�ж����λ
				BillHtmlPanel htmlPanel = new BillHtmlPanel(false);
				htmlPanel.loadHtml(getHtml_no());
				WLTRadioPane radioPane = getRadioPane(new String[] { "-999999", "-999999" }, first);
				tabPane.addTab("û�ж����λ", radioPane);
			}
			tabPane.addChangeListener(this);
			this.add(tabPane);
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * �õ�ҳǩ��塣��Ҫ�����úõ�htmlpanel���롣 ����postid�������λΪ�� ����-99999�ȿ�
	 * 
	 * @param postID
	 * @param times�ڼ�����λ
	 * @return
	 * @throws Exception
	 */
	private WLTRadioPane getRadioPane(String[] postID, int times) throws Exception {
		WLTRadioPane radioPane = new WLTRadioPane();
		BillListPanel listPanel_1 = new BillListPanel("CMP_CMPFILE_WFOPEREQ_CODE1");
		WLTButton btn = new WLTButton("��ʾȫ��");
		StringBuffer curr_postid = new StringBuffer();
		StringBuffer like = new StringBuffer();
		if (postID.length >= 1 && postID[1] != null && !postID[1].equals("-999999")) { //��Ӹ�λ��
			curr_postid.append(tbutil.getInCondition(postID));
			like.append(getMultiOrCondition("operaterefpost", postID));
		} else {
			curr_postid.append(postID[0]);
			like.append(" operaterefpost like '%;" + postID[0] + ";%' ");
		}
		String[] wfactivity_id = UIUtil.getStringArrayFirstColByDS(null, "select wfactivity_id from cmp_cmpfile_wfopereq where operatepost in (" + curr_postid + ") or " + like.toString()); // �鴦���а����˸�λ�Ļ���
		String[] wfprocess_id = UIUtil.getStringArrayFirstColByDS(null, "select wfprocess_id from cmp_cmpfile_wfopereq where operatepost in (" + curr_postid + ")  or " + like.toString()); // �鴦���а����˸�λ������
		String wfactiveID = tbutil.getInCondition(wfactivity_id);
		String wfprocessID = new TBUtil().getInCondition(wfprocess_id);
		listPanel_1.addBillListHtmlHrefListener(this);
		btn.addActionListener(this);
		listPanel_1.addBatchBillListButton(new WLTButton[] { btn });
		listPanel_1.repaintBillListButton();
		BillListPanel listPanel_2 = new BillListPanel("CMP_RISK_CODE1");
		listPanel_2.setDataFilterCustCondition(" wfactivity_id in (" + wfactiveID + ")");
		BillListPanel listPanel_3 = new BillListPanel("LAW_LAW_CODE1", false); // �ֶ�init
		listPanel_3.getTempletVO().setAutoLoads(0);
		listPanel_3.initialize(); // ��ʼ��panels
		listPanel_3.setQuickQueryPanelVisiable(false);
		// �����˸�λ�����ڻ��ڵķ���͸�λ�����̵���ط���
		listPanel_3.setDataFilterCustCondition("  id in ( select law_id from cmp_cmpfile_law where wfactivity_id in(" + wfactiveID + ") or (wfprocess_id in (" + wfprocessID + ") and relationtype='����') )");
		listPanel_3.addBillListHtmlHrefListener(this);
		BillListPanel listPanel_4 = new BillListPanel("RULE_RULE_CODE3", false);
		listPanel_4.getTempletVO().setAutoLoads(0); // ���ò��Զ�����
		listPanel_4.initialize();
		listPanel_4.setQuickQueryPanelVisiable(false);
		// �����˸�λ�����ڻ��ڵ��ƶȺ͸�λ�����̵�����ƶ�
		listPanel_4.setDataFilterCustCondition("  id in ( select rule_id from cmp_cmpfile_rule where wfactivity_id in(" + wfactiveID + ") or (wfprocess_id in (" + wfprocessID + ") and relationtype='����') )");
		listPanel_4.addBillListHtmlHrefListener(this);

		BillListPanel listPanel_0 = new BillListPanel("CMP_POSTDUTY_CODE1", false);
		listPanel_0.initialize();
		listPanel_0.setDataFilterCustCondition(" postid in (" + curr_postid + ") ");
		listPanel_0.getTempletVO().setOrdercondition("postid,seq");//������ܻ���ʾ���ٸ�λ��ְ����Ҫ�ֶ����������ֶΡ����/2014-12-16��
		if (times == 1) {
			BillCellPanel cellPanel = getBillCellPanel(postID[0]);
			try {
				setBillCellPanelValue(cellPanel);
				radioPane.addTab("��λ��Ϣ", cellPanel);
			} catch (Exception e) {
				e.printStackTrace();//�����/2014-08-21��
				MessageBox.show("��λ��Ϣҳ�棨BillCellPanel���������ݳ�������ϵ����Ա...");
			}
		} else {
			radioPane.addTab("��λ��Ϣ", getBillCellPanel(null));
		}
		listPanel_0.addBillListSelectListener(this);
		BillHtmlPanel htmlPanel = new BillHtmlPanel(true);
		htmlPanel.putClientProperty("wfactiveID", wfactiveID);
		htmlPanel.putClientProperty("wfprocessID", wfprocessID);
		WLTSplitPane splitePane = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT);
		splitePane.setDividerLocation(180);
		splitePane.add(listPanel_0, WLTSplitPane.TOP);
		splitePane.add(listPanel_1, WLTSplitPane.BOTTOM);
		btn.putClientProperty("splitePane", splitePane);
		radioPane.addTab("���𼰲���ָ��", splitePane);
		radioPane.addTab("����ָ��", listPanel_2);
		radioPane.addTab("��ط���", listPanel_3);
		radioPane.addTab("����ƶ�", listPanel_4);
		radioPane.addTab("��λ˵����", htmlPanel);
		radioPane.addChangeListener(this);
		return radioPane;
	}

	public BillCellPanel getBillCellPanel(String postID) {
		final BillCellPanel cellPanel = new BillCellPanel(getBillCellVO());
		if (postID == null) {
			cellPanel.getBillCellItemVOAt(0, 0).setCellvalue("ϵͳ����Աδ�����ƶ���λ");
		} else {
			try {
				cellPanel.loadBillCellData(getInitBillCellVO(postID));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		JPopupMenu pop_menu = new JPopupMenu();
		JMenuItem exportHtml = new JMenuItem("����Html", UIUtil.getImage("office_160.gif"));
		pop_menu.add(exportHtml);
		exportHtml.setPreferredSize(new Dimension(100, 19)); //
		exportHtml.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cellPanel.exportHtml("��λ��Ϣ");
			}
		});
		cellPanel.setCustPopMenu(pop_menu);
		cellPanel.setEditable(false);
		return cellPanel;
	}

	// ��cellPanel��ֵ
	public void setBillCellPanelValue(BillCellPanel cellPanel) throws Exception {
		if ("N".equals(custCellTemplet)) {
			cellPanel.getBillCellItemVOAt("1-A").setCellvalue(hvo_post[0].getStringValue("name"));
			cellPanel.getBillCellItemVOAt("3-B").setCellvalue(getvalue(hvo_post[0].getStringValue("Name")));
			cellPanel.getBillCellItemVOAt("3-D").setCellvalue(getvalue(hvo_post[0].getStringValue("code")));
			String deptName = "δ����";
			try {
				deptName = UIUtil.getStringValueByDS(null, "select name  from  pub_corp_dept where id=" + hvo_post[0].getStringValue("deptid") + "");
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			cellPanel.getBillCellItemVOAt("4-B").setCellvalue(deptName);
			cellPanel.getBillCellItemVOAt("4-D").setCellvalue(getvalue(hvo_post[0].getStringValue("postlevel")));
			cellPanel.getBillCellItemVOAt("6-A").setCellvalue(getvalue(hvo_post[0].getStringValue("intent")));
			cellPanel.getBillCellItemVOAt("8-B").setCellvalue(getvalue(hvo_post[0].getStringValue("innercontact")));
			cellPanel.getBillCellItemVOAt("9-B").setCellvalue(getvalue(hvo_post[0].getStringValue("outcontact")));

			cellPanel.getBillCellItemVOAt("11-B").setCellvalue(getvalue(hvo_post[0].getStringValue("education")));
			cellPanel.getBillCellItemVOAt("12-B").setCellvalue(getvalue(hvo_post[0].getStringValue("skill")));
			cellPanel.setEditable(false);
		} else {
			BillCellVO cellVO = getBillCellVO(); //ƽ̨�и�С���⣬billCellPanel.getBillCellVO().������vo���ԡ����µ�����html��ȶ�Ϊ75���ء�
			BillCellItemVO itemVOS[][] = cellVO.getCellItemVOs(); //�õ����е�cellitem
			for (int i = 0; i < itemVOS.length; i++) {
				for (int j = 0; j < itemVOS[i].length; j++) {
					BillCellItemVO itemvo = itemVOS[i][j];
					String itemKey = itemvo.getCellkey();// ��������Զ�����ֶ�ֵ�
					String itemValue = itemvo.getCellvalue();
					String currValue = "";
					if (itemValue != null && !itemValue.equals("") && itemValue.contains("select") && itemValue.contains("from")) {
						String[] condition = tbutil.getFormulaMacPars(itemValue);
						for (int k = 0; k < condition.length; k++) {
							String out_id = hvo_post[0].getStringValue(condition[k]);
							if (out_id != null && out_id.contains(";")) {
								out_id = tbutil.getInCondition(out_id);
							} else if (out_id == null || out_id.equals("")) {
								out_id = "-999999";
							}
							itemValue = tbutil.replaceAll(itemValue, "{" + condition[k] + "}", out_id);
						}
						String values[] = UIUtil.getStringArrayFirstColByDS(null, itemValue);
						itemValue = "";
						if (values.length == 1) {
							itemValue += values[0];
						} else {
							for (int k = 0; k < values.length; k++) {
								itemValue += values[k] + ";";
							}
						}
						itemvo.setCellvalue(itemValue);
					} else {
						currValue = hvo_post[0].getStringValue(itemKey, null);
						if (currValue != null && !currValue.equals("")) {
							itemvo.setCellvalue(currValue);
						}
					}
				}
			}
			cellPanel.loadBillCellData(cellVO);
			cellPanel.setEditable(false);
		}

	}

	// �õ���Ӹ����е�CellVO
	private BillCellVO getInitBillCellVO(String postID) {
		loadPost(postID);
		BillCellVO cellvo = getBillCellVO(); // �õ�ģ��BillCellVO
		return cellvo;
	}

	// ����ģ��ӷ������˵õ�BillCellVO
	private BillCellVO cellvo = null;

	// һ������ģ�塣
	private BillCellVO getBillCellVO() {

		if (cellvo == null) {
			try {
				if ("N".equals(custCellTemplet) || "Y".equals(custCellTemplet) || custCellTemplet == null || custCellTemplet.equals("")) {
					cellvo = getMetaService().getBillCellVO("mypostandduty", null, null);
				} else {
					cellvo = getMetaService().getBillCellVO(custCellTemplet, null, null);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return cellvo;
		} else {
			BillCellVO cell = null;
			try {
				cell = (BillCellVO) new TBUtil().deepClone(cellvo);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return cell;
		}
	}

	public void loadPost(String post_id) {
		try {
			hvo_post = UIUtil.getHashVoArrayByDS(null, "select *  from  pub_post where id='" + post_id + "' ");
			if (!hvo_postmap.containsKey(post_id)) {
				hvo_postmap.put(post_id, hvo_post);
			}
			if (hvo_post.length == 0) {
				return;
			}
			hvo_duty = UIUtil.getHashVoArrayByDS(null, "select *  from  cmp_postduty where postid='" + hvo_post[0].getStringValue("id") + "'");
			if (!hvo_dutymap.containsKey(post_id)) {
				hvo_dutymap.put(post_id, hvo_duty);
			}
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��λ˵���� html
	 */

	private String gethtml(BillHtmlPanel htmlPanel, String[] post_id) {

		try {
			IPushGRCServiceIfc server = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
			String wfactiveID = (String) htmlPanel.getClientProperty("wfactiveID");
			String wfprocessID = (String) htmlPanel.getClientProperty("wfprocessID");
			String htmlstr = server.getPostAndDutyHtmlStr(wfactiveID, wfprocessID, post_id);
			return htmlstr;
		} catch (Exception e) {
			e.printStackTrace();
			return getHtml_no();
		}

	}

	// ���û�з����λ�����߱���
	public String getHtml_no() {
		StringBuffer sb = new StringBuffer();
		sb.append("<html>");
		sb.append("<style type=\"text/css\">");
		sb.append("tr { font-size: 14px; color: #0066cc; line-height: 18px; font-family: ����}");
		sb.append(".style_2 {");
		sb.append(" font-size: 30px;  color:red;  font-weight: bold;  line-height: 60px; font-family: ����}");
		sb.append(".style_3 {");
		sb.append(" font-size: 20px;  color:#0066cc;  font-weight: bold;  line-height: 20px; font-family: ����}");
		sb.append("</style>");
		sb.append("<head> <meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\">");
		sb.append("<title>��λ˵����</title>");
		sb.append("</head> <body>");
		sb.append("<table width=100% border=0>");
		sb.append("<tr align=left><td  colspan=4 class=style_3 >ϵͳ����Աδ�����ƶ���λ,������ϵϵͳ������Ա....</td></tr>");
		sb.append(" </table></body></html>");
		return sb.toString();
	}

	private String getvalue(String str) {
		if (str == null || str.equals("")) {
			return "δ��д";
		} else {
			return str;
		}
	}

	public void stateChanged(ChangeEvent e) {
		Object obj = e.getSource();
		if (obj == tabPane) { // ��λ�л�
			int selectIndex = tabPane.getSelectedIndex();
			WLTRadioPane radioPane = (WLTRadioPane) tabPane.getComponentAt(selectIndex);
			String[] postids = (String[]) radioPane.getClientProperty("postid");
			currPostId = postids;
			if (!postMap.containsKey(currPostId + "_" + 0)) {
				BillCellPanel panel = (BillCellPanel) radioPane.getTabCompent(0);
				try {
					BillCellVO cellvo = getInitBillCellVO(currPostId[0]);
					panel.loadBillCellData(cellvo);
					setBillCellPanelValue(panel);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				panel = getBillCellPanel(currPostId[0]);
				postMap.put(currPostId + "_" + 0, true);
			}
		} else if (obj instanceof WLTRadioPane) { // ��λ˵���飬���𡭡��л�
			WLTRadioPane radioPane = (WLTRadioPane) e.getSource();
			int selectIndex = radioPane.getSelectIndex();
			Object ob = radioPane.getTabCompent(selectIndex);
			if (!postMap.containsKey(currPostId + "_" + selectIndex)) {
				if (ob instanceof BillListPanel) {
					BillListPanel listPanel = (BillListPanel) ob;
					listPanel.QueryDataByCondition(null);
					postMap.put(currPostId + "_" + selectIndex, true);
				} else if (ob instanceof BillHtmlPanel) {
					BillHtmlPanel htmlPanel = (BillHtmlPanel) ob;
					htmlPanel.loadHtml(gethtml(htmlPanel, currPostId));
					postMap.put(currPostId + "_" + selectIndex, true);
				} else if (ob instanceof WLTSplitPane) {
					WLTSplitPane split = (WLTSplitPane) ob;
					BillListPanel listp1 = (BillListPanel) split.getTopComponent(); //  ����
					listp1.QueryDataByCondition(null);
					BillListPanel listp2 = (BillListPanel) split.getBottomComponent(); //���̲���ָ��
					postMap.put(currPostId + "_" + selectIndex, true);
				}
			}
		}
	}

	// �������ָ��
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		BillListPanel listPanel = _event.getBillListPanel();
		if ("CMP_CMPFILE_WFOPEREQ".equalsIgnoreCase(listPanel.getTempletVO().getTablename())) { // �鿴����
			BillVO vo = listPanel.getSelectedBillVO();
			showWFprocess(vo.getStringValue("wfprocess_name"), vo.getStringValue("wfprocess_id"), vo.getStringValue("wfactivity_id"));
		} else if ("LAW_LAW".equalsIgnoreCase(listPanel.getTempletVO().getTablename())) {
			showLaw(listPanel);
		} else if ("RULE_RULE".equalsIgnoreCase(listPanel.getTempletVO().getTablename())) {
			showRule(listPanel);
		}
	}

	// ������  ���Ҹ���ĳ������
	public void showWFprocess(String processName, String processId, String wfactivity_id) {
		WFGraphEditItemDialog itemdialog = new WFGraphEditItemDialog(this, "�鿴����[" + processName + "]", processId, new String[] { wfactivity_id }, false, true, 1000, 700);
		itemdialog.setVisible(true);
	}

	// �鿴���
	public void showLaw(BillListPanel listPanel) {
		new LawAndRuleShowDetailDialog("LAW_LAW_ITEM_CODE1", listPanel, 850, 550, true);
	}

	// �鿴�ƶ�
	public void showRule(BillListPanel listPanel) {
		new LawAndRuleShowDetailDialog("RULE_RULE_ITEM_CODE1", listPanel, 850, 550, true);
	}

	private FrameWorkMetaDataServiceIfc getMetaService() throws Exception {
		if (metaservice == null) {
			metaservice = (FrameWorkMetaDataServiceIfc) RemoteServiceFactory.getInstance().lookUpService(FrameWorkMetaDataServiceIfc.class);
		}
		return metaservice;
	}

	/*
	 *  ���𼰲���ָ��������
	 */
	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		String ids = _event.getBillListPanel().getSelectedBillVO().getStringValue("id");
		WLTSplitPane sp = (WLTSplitPane) _event.getBillListPanel().getParent();
		BillListPanel listP = (BillListPanel) sp.getBottomComponent();
		listP.QueryDataByCondition(" task like '%;" + ids + ";%'");
	}

	/**
	 *  �鿴�ø�λ���в���Ҫ��  ��ֹ�еĻ��ڲ���Ҫ��û��ϸ������������
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof WLTButton) {
			WLTButton btn = (WLTButton) e.getSource();
			WLTSplitPane p = (WLTSplitPane) btn.getClientProperty("splitePane");
			((BillListPanel) p.getTopComponent()).refreshData();
			BillListPanel list = (BillListPanel) p.getBottomComponent();
			StringBuffer curr_postid = new StringBuffer();
			StringBuffer like = new StringBuffer();
			if (currPostId.length >= 1 && currPostId[1] != null && !currPostId[1].equals("-999999")) { //��Ӹ�λ��
				curr_postid.append(tbutil.getInCondition(currPostId));
				like.append(getMultiOrCondition("operaterefpost", currPostId));
			} else {
				curr_postid.append(currPostId[0]);
				like.append(" operaterefpost like '%;" + currPostId[0] + ";%' ");
			}
			list.QueryDataByCondition("operatepost in(" + curr_postid + ") or " + like);
		}
	}

	/*
	 * �õ�like or ����
	 */
	private String getMultiOrCondition(String key, String[] tempid) {
		StringBuffer sb_sql = new StringBuffer();
		if (tempid != null && tempid.length > 0) {
			sb_sql.append(" (");
			for (int j = 0; j < tempid.length; j++) {
				sb_sql.append(key + " like '%;" + tempid[j] + ";%'"); // 
				if (j != tempid.length - 1) { //
					sb_sql.append(" or ");
				}
			}
			sb_sql.append(") "); //
		}
		return sb_sql.toString();
	}
	//	public HashMap getPostGroups (){
	//		if(postgroups == null){
	//			try {
	//				postgroups = UIUtil.getHashMapBySQLByDS(null, "select id,name from pub_post where deptid is null");
	//			} catch (WLTRemoteException e) {
	//				e.printStackTrace();
	//			} catch (Exception e) {
	//				e.printStackTrace();
	//			}
	//		}
	//		return postgroups;
	//	}
}
