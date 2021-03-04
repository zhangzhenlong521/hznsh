package cn.com.pushworld.salary.ui.person.p020;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.DivComponentLayoutUtil;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.pushworld.salary.to.SalaryTBUtil;
import cn.com.pushworld.salary.ui.SalaryUIUtil;
import cn.com.pushworld.salary.ui.targetcheck.SalaryBomClickEvent;
import cn.com.pushworld.salary.ui.targetcheck.SalaryBomClickListener;
import cn.com.pushworld.salary.ui.targetcheck.SalaryBomPanel;

/**
 * Ա���������档 ��ҳ����Խ��� ��ͨԱ���������в㡣
 * 
 * 
 * @author haoming create by 2013-7-15
 */
public class PersonMutualWKPanel extends AbstractWorkPanel implements ChangeListener, ActionListener {

	private static final long serialVersionUID = 3880878924865803724L;
	private HashVO currLog = null; // ��ǰ�ƻ���־ID��
	private String userid = ClientEnvironment.getCurrSessionVO().getLoginUserId(); // ��ǰ��¼��ID
	private String inputChecktype = null;
	private HashVO currCheckVOs[]; // ��ǰ��Ա���ֵ���������

	private WLTTabbedPane tabpane = new WLTTabbedPane();// ������˿������۶���ָ�꣬��Ա���������в�ȣ���ô����ҳǩ������ֱ�Ӹ�һ��BillListPanel.

	private List canchecktype; // ��¼��Ա��������ָ�����͡�һ��Ա���������в����֡�

	private int value_col_start_index = 2; // ��������ݴӵĵڼ��п�ʼ��
	private int value_row_start_index = 2; // ��������ݴӵĵڼ��п�ʼ��

	private HashMap tabOpenHis = new HashMap(); // ҳǩ��Ӧ�����(BillCellPanel,BillListPanel)��

	private SalaryBomPanel planBomPanel = new SalaryBomPanel();
	/*
	 * ���õ�ǰҳ��״̬
	 */
	private boolean otherPanelInit = false; // �Ƿ�������ҳ�����

	private int beginTabIndex = 0;

	private String input_value_range = TBUtil.getTBUtil().getSysOptionStringValue("н��ģ����Ա�������", "0-10");

	private boolean autoCheckCommit = true;

	private TBUtil tbutil = new TBUtil();

	public static final String ggccTabName = SalaryTBUtil.ggCheckTargetType;

	public static final boolean hideTotle = TBUtil.getTBUtil().getSysOptionBooleanValue("��ֽ����Ƿ������ܼ�", false); // Ĭ�ϲ�����

	private boolean editable = true;

	private static final Logger logger = WLTLogger.getLogger(PersonMutualWKPanel.class);

	private SalaryTBUtil salaryTBUtil = new SalaryTBUtil();

	public void initialize() {
		this.add(init());
	}

	private JPanel init() {
		HashVO vos[];
		try {
			// �����½�˿��������мƻ�
			vos = UIUtil.getHashVoArrayByDS(null, "select distinct(t1.id),t1.checkdate,t1.status,t1.name from SAL_TARGET_CHECK_LOG t1,sal_person_check_score t2 where t1.id = t2.logid and t1.status!='���˽���' and t2.scoreuser='" + userid + "'");
			if (vos == null || vos.length == 0) {
				JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.CENTER));
				WLTLabel label = new WLTLabel("Ŀǰȫ��û�п����еļƻ�...");
				label.setFont(new Font("����", Font.BOLD, 16));
				panel.add(label);
				return panel;
			}
			if (vos.length > 1) { // ����ж����bom��ʾ��
				return getPlanBomPanel(vos);
			} else {// ������о�һ��ִ����
				currLog = vos[0];
				return loadPanelByLogPlanVO();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JPanel();
	}

	/*
	 * ���������е��ó�ʼ����������Ҫ����ҳǩ����ǰ�ļƻ�VO����ʼ��ҳǩ��
	 */
	public void customInit(WLTTabbedPane _tabPanel, HashVO _logVO, String _userid, String _inputchecktype, boolean _editable) {
		tabpane = _tabPanel;
		currLog = _logVO;
		otherPanelInit = true;
		beginTabIndex = tabpane.getTabCount(); // ��ʼλ�ã����������
		if (beginTabIndex > 0) {
			for (int i = 0; i < beginTabIndex; i++) {
				tabOpenHis.put(i + "", null);
			}

		}
		userid = _userid;
		editable = _editable;
		inputChecktype = _inputchecktype;
		loadPanelByLogPlanVO();
	}

	/*
	 * ����ѡ��ļƻ�����ȡ������塣
	 */
	private JPanel loadPanelByLogPlanVO() {
		try {
			StringBuffer sqlsb = new StringBuffer();
			sqlsb.append("select t1.*,t2.catalog from  sal_person_check_score t1 left join sal_person_check_list t2 on t1.targetid=t2.id left join v_pub_user_post_2 u  on " + "u.userid = t1.checkeduser and u.isdefault='Y' left join pub_corp_Dept u2 on u2.id = u.deptid left join sal_person_check_type t4 on t4.name=t1.checktype where t1.scoreuser='" + userid + "' and t1.logid='"
					+ currLog.getStringValue("id") + "' and t1.targettype!='Ա������ָ��' ");
			if (!tbutil.isEmpty(inputChecktype)) {
				sqlsb.append(" and t1.checktype='" + inputChecktype + "' ");
			}
			sqlsb.append(" order by t4.code,t1.targettype,t2.seq,u2.seq,u.seq");
			currCheckVOs = UIUtil.getHashVoArrayByDS(null, sqlsb.toString()); // ���ݲ�����Ա����
			String currcheckType = "-1";// ��ǰ�������������͡�
			canchecktype = new ArrayList();
			// �������п�������Ϣ�����ȶԱ����˶�����з��ࣨ�в㣬һ��Ա�������ٶ�ͬһָ���Ӧ������Ա���й��ࣨһָ���Ӧ���������Ա��,
			HashMap<String, String> checktypeAndtargetType = new HashMap<String, String>(); // ������¼������ͺ�ָ�����͡�
			for (int i = 0; i < currCheckVOs.length; i++) {
				String targeteID = currCheckVOs[i].getStringValue("targetid");
				String targettype = currCheckVOs[i].getStringValue("targettype"); // ָ�������
				String catalog = currCheckVOs[i].getStringValue("catalog", "���޷���");
				String targetname = currCheckVOs[i].getStringValue("targetname");
				String userName = currCheckVOs[i].getStringValue("checkedusername");
				String checkuserid = currCheckVOs[i].getStringValue("checkeduser");
				String score = currCheckVOs[i].getStringValue("checkscore");
				String checktype = currCheckVOs[i].getStringValue("checktype", "����ȱʧ"); // ָ��Ŀ�������
				String status = currCheckVOs[i].getStringValue("status");
				String weights = currCheckVOs[i].getStringValue("weights"); // Ȩ��
				// if ("�߹�".equals(checktype) && targettype.equals("�߹ܶ���ָ��")) {
				// currCheckVOs[i].setAttributeValue("checktype", ggccTabName);
				// checktype = ggccTabName; // ָ��Ŀ�������
				// }
				if ("".equals(checktype)) {
					checktype = "����ȱʧ";
				}
				LinkedHashMap userset = null; // ������¼��½�˿���һ������ָ�������[��������Ա],key:USERID,value��{userid,username}
				LinkedHashMap scorevomap = null;// ������¼ÿһ��ָ���Ӧ��Ա�����˷���TargetHashVO��key:targetid,value:HashVO{id,targetName,userscore1,userscore2..}
				// HashMap sumMap = null; // ��¼�ܷ�key:userid,value:sumcount
				LinkedHashMap cellCompareHashVOMap; // ������¼�������ݿ�Ĺ�ϵ��Ŀǰ�������ֻ��һ����ֵ����ʵӦ�ö�Ӧ���ݿ��һ�����ݡ�
				if (!checktype.equals(currcheckType)) { // ������µ����͡����¶���һ����Աmap��ָ��map
					userset = new LinkedHashMap(); // Ψһ���˱�������,���Ǳ�ͷ��
					scorevomap = new LinkedHashMap(); //
					cellCompareHashVOMap = new LinkedHashMap();
					// sumMap = new HashMap(); // ��Ա�ܷ�
					Boolean submit = Boolean.FALSE; // ����Ƿ��Ѿ��ύ����
					if (status != null && !status.equals("") && !status.equals("���ύ")) {// ��һ����Ϊ�գ��ü�¼���ύ�ˡ�����һ����ȫ���ύ��
						submit = Boolean.TRUE;
					}
					Object[] obj = new Object[] { userset, scorevomap, null, checktype, cellCompareHashVOMap, submit };
					if (ggccTabName.equals(checktype)) { // �߹���Ҫ���⴦��
						List targetAndScoreList = new ArrayList();
						targetAndScoreList.add(currCheckVOs[i]);
						obj = new Object[] { targetAndScoreList, null, null, checktype, null, submit };
						canchecktype.add(obj);
						currcheckType = checktype; // �趨��ʶֵ��
						continue;
					} else {
						canchecktype.add(obj);
						currcheckType = checktype; // �趨��ʶֵ��
					}

				} else {// �������趨
					Object[] obj = (Object[]) canchecktype.get(canchecktype.size() - 1);
					if (ggccTabName.equals(checktype)) { // �߹���Ҫ���⴦��
						List targetAndScoreList = (List) obj[0];
						targetAndScoreList.add(currCheckVOs[i]);
						continue;
					}
					if ((Boolean) obj[5]) { // ������Ѿ���ʶ��״̬�����ύ����Ҫ�ж��µ�ǰ�����ǲ���δ�ύ��
						if (status == null || status.equals("") || status.equals("���ύ")) {
							obj[5] = Boolean.FALSE;
						}
					}
					userset = (LinkedHashMap) obj[0];
					scorevomap = (LinkedHashMap) obj[1];
					cellCompareHashVOMap = (LinkedHashMap) obj[4];
				}
				userset.put(checkuserid, new String[] { checkuserid, userName });//
				HashVO rowTargetVO = null; // һ��ָ���HashVO��
				if (scorevomap.containsKey(targeteID)) {
					rowTargetVO = (HashVO) scorevomap.get(targeteID); // �õ�һ��
				} else {
					rowTargetVO = new HashVO();
					rowTargetVO.setAttributeValue("id", targeteID); // �趨id
				}
				if (!rowTargetVO.containsKey(checkuserid)) {
					rowTargetVO.setAttributeValue(checkuserid, score);
				}
				rowTargetVO.setAttributeValue("targetName", targetname);// �趨����
				rowTargetVO.setAttributeValue("catalog", catalog);// �趨����
				rowTargetVO.setAttributeValue("weights", weights);// Ȩ��
				scorevomap.put(targeteID, rowTargetVO);
				cellCompareHashVOMap.put((scorevomap.size() - 1) + "_" + checkuserid, i); // key
				// =
				// ����+userid
			}
			for (int i = 0; i < canchecktype.size(); i++) { //
				Object[] obj = (Object[]) canchecktype.get(i);
				String tabName = (String) obj[3];
				String imageName = "office_021.gif";
				if ("�߹�".equals(tabName)) {
					imageName = "office_015.gif";
				} else if ("�в�".equals(tabName)) {
					imageName = "office_108.gif";
				}
				tabpane.addTab(tabName, UIUtil.getImage(imageName), new JPanel());
			}
			tabpane.addChangeListener(this);
			if (beginTabIndex == 0) {
				tabPanelLoad(beginTabIndex); // ���ص�һ�����
			}
			return tabpane;
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JPanel();
	}

	/**
	 * ����Ԫ����ͬ���ݵĺϲ�
	 * 
	 * @param cellItemVOs
	 * @param _spanColumns
	 *            �Ǽ�����Ҫ����
	 */
	public void formatSpan(BillCellItemVO[][] cellItemVOs, int[] _spanColumns) {
		if (_spanColumns != null) {
			HashMap temp = new HashMap();
			for (int i = 0; i < _spanColumns.length; i++) {
				int li_pos = _spanColumns[i];
				if (li_pos >= 0) {
					int li_spancount = 1;
					int li_spanbeginpos = 1;
					for (int k = 1; k < cellItemVOs.length; k++) {
						String str_value = cellItemVOs[k][li_pos].getCellvalue();
						String str_value_front = cellItemVOs[k - 1][li_pos].getCellvalue();
						if (TBUtil.getTBUtil().compareTwoString(str_value_front, str_value)) {
							if (i >= 1) {
								String str_value0 = cellItemVOs[k][_spanColumns[i - 1]].getCellvalue();
								String str_value_front0 = cellItemVOs[k - 1][_spanColumns[i - 1]].getCellvalue();
								if (TBUtil.getTBUtil().compareTwoString(str_value0, str_value_front0)) {
									li_spancount++;
								} else {
									cellItemVOs[li_spanbeginpos][li_pos].setSpan(li_spancount + ",1");
									li_spancount = 1;
									li_spanbeginpos = k;
								}
							} else {
								li_spancount++;
							}

						} else {
							cellItemVOs[li_spanbeginpos][li_pos].setSpan(li_spancount + ",1");
							li_spancount = 1;
							li_spanbeginpos = k;
						}
					}
					cellItemVOs[li_spanbeginpos][li_pos].setSpan(li_spancount + ",1");
				}
			}
		}
	}

	/*
	 * ����ҳǩ��ţ���ȡBillCellPanel����ҳ�档
	 */
	private JPanel getCheckScoreMainBillCellPanel(int _index) {
		BillCellPanel currCellpanel = new BillCellPanel(getBillCellVo());
		currCellpanel.setAllowShowPopMenu(false); //
		int colCount = currCellpanel.getColumnCount();
		currCellpanel.putClientProperty("this", this);
		Object[] obj = (Object[]) canchecktype.get(_index);
		if (!ggccTabName.equals(obj[3])) {
			for (int i = 2; i < colCount; i++) { // ���
				resetColSum(currCellpanel, i);
			}
		}
		boolean issubmit = (Boolean) obj[5]; // �������ͣ��Ǹ��в㣬����һ��Ա����
		JPanel mainPanel = new WLTPanel(new BorderLayout());
		mainPanel.setOpaque(false);
		mainPanel.add(currCellpanel, BorderLayout.CENTER);
		StringBuffer showWarnInfo = new StringBuffer();
		showWarnInfo.append("&nbsp;&nbsp;&nbsp;&nbsp;��������ֱ�׼: �ǳ����� 9-10 ��; ���� 7-8 ��; �ϸ� 5-6 ��; �����ϸ� 3-4 ��; ���ϸ� 0-2��.");
		if ("һ����Ա".equals(obj[3]) && !hideTotle) {
			showWarnInfo.append("(���Ա���ܷ���ͬ�����ύ)");
		}
		JLabel info_label = new JLabel("<html><font color='blue'>" + showWarnInfo.toString() + "</font></html>");
		// info_label.setPreferredSize(new Dimension(550 +
		// LookAndFeel.getFONT_REVISE_SIZE() * showWarnInfo.length(),
		// hideTotle?18:33));
		info_label.setVerticalTextPosition(JLabel.CENTER);
		info_label.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
		if (!issubmit) { //
			WLTButton btn_submit = new WLTButton("�ύ", UIUtil.getImage("zt_050.gif"));
			btn_submit.setToolTipText("�ύ���, �ύ�����޸�");
			btn_submit.addActionListener(this);
			WLTPanel btnPanel = new WLTPanel(new BorderLayout());
			btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			btnPanel.add(btn_submit, BorderLayout.WEST);
			btnPanel.add(info_label, BorderLayout.CENTER);
			mainPanel.add(btnPanel, BorderLayout.NORTH);
		} else { // ����Ѿ��ύ.
			currCellpanel.setEditable(false);
			WLTButton btn_submit = new WLTButton("�޸�", UIUtil.getImage("zt_071.gif"));
			btn_submit.addActionListener(this);
			WLTPanel btnPanel = new WLTPanel(new FlowLayout(FlowLayout.LEFT));
			// btnPanel.add(btn_submit);
			btnPanel.add(info_label);
			mainPanel.add(btnPanel, BorderLayout.NORTH);
			setCurrCellPanelStatus(currCellpanel, "�� �� ��");
		}
		tabOpenHis.put((_index + beginTabIndex) + "", currCellpanel); // �����ڵ�ҳǩ��ż���
		if (!editable) {
			currCellpanel.setEditable(editable);
		}
		currCellpanel.setLockedCell(2, 1);
		return mainPanel;
	}

	private void setCurrCellPanelStatus(final JComponent parent, final String _status) {
		new Timer().schedule(new TimerTask() {
			public void run() {
				JPanel jp = new JPanel() {
					protected void paintComponent(Graphics g) {
						// ����
						if (parent != null && parent.isShowing()) { // �㵽bufferedimage���ر������
							super.paintComponent(g);
							Graphics2D g2d = (Graphics2D) g.create();
							g2d.translate(15, 8);
							g2d.setComposite(AlphaComposite.SrcOver.derive(0.6f));
							g2d.setColor(Color.red.brighter());
							g2d.rotate(0.4);
							g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
							g2d.setStroke(new BasicStroke(3.5f));
							g2d.drawRect(1, 1, 150, 30);
							g2d.setFont(new Font("����", Font.BOLD, 25));
							g2d.drawString(_status, 15, 25);
						}
					}
				};
				jp.setOpaque(false);
				int i = 0;
				while (!parent.isShowing()) {
					i++;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (i > 300) { // 30���ٳ�������ֱ�ӷ��ء�Ӧ�ò�����ô����
						return;
					}
				}
				Point p = parent.getLocationOnScreen();
				SwingUtilities.convertPointFromScreen(p, parent.getRootPane().getLayeredPane());
				BillCellPanel cellPanel = (BillCellPanel) parent;
				int tablewidth = (int) cellPanel.getTable().getVisibleRect().getWidth();
				int tableheight = (int) cellPanel.getTable().getVisibleRect().getHeight();
				Rectangle rect = new Rectangle((tablewidth / 2 - 40), tableheight / 2, 180, 130);
				DivComponentLayoutUtil.getInstanse().addComponentOnDiv(cellPanel, jp, rect, null); // ��Ӹ�����
			}
		}, 100);
	}

	/*
	 * ����ҳǩ��
	 */
	public void tabPanelLoad(int index) {
		if (!tabOpenHis.containsKey((index) + "")) {
			JPanel jp = (JPanel) tabpane.getComponentAt(index);
			jp.removeAll();
			jp.setLayout(new BorderLayout());
			if (index - beginTabIndex < 0) {
				tabOpenHis.put(index + "", null);
				return;
			}
			JPanel currListPanel = getCheckScoreMainBillCellPanel(index - beginTabIndex);
			jp.add(currListPanel, BorderLayout.CENTER);
		} else {
			// listPanel = tabOpenHis.get(index + "");
		}

	}

	private SalaryBomPanel getPlanBomPanel(HashVO[] _planLogs) {
		for (int i = 0; i < _planLogs.length; i++) {
			_planLogs[i].setToStringFieldName("checkdate");
		}

		planBomPanel.addBomPanel(Arrays.asList(_planLogs));
		planBomPanel.addBomClickListener(new SalaryBomClickListener() {
			public void onBomClickListener(SalaryBomClickEvent event) {
				currLog = event.getHashvo();
				planBomPanel.addBomPanel(loadPanelByLogPlanVO());
			}
		});
		return planBomPanel;
	}

	/*
	 * �õ���ǰ�ƻ�����
	 */
	public HashVO getCurrLog() {
		return currLog;
	}

	public void stateChanged(ChangeEvent changeevent) {
		int selectIndex = tabpane.getSelectedIndex();
		tabPanelLoad(selectIndex);
	}

	public HashVO getHashVO(int _row, String itemKey) {
		Object[] curTabVO = getCurrCheckPageObject();
		Map cellCompareHashVOMap = (Map) curTabVO[4];
		if (!cellCompareHashVOMap.containsKey(_row + "_" + itemKey)) {
			HashVO vo = new HashVO();
			return vo;
		}
		int index = (Integer) cellCompareHashVOMap.get(_row + "_" + itemKey); // �õ�
		return currCheckVOs[index];
	}

	/*
	 * ����ύ!
	 */
	private void onSubmit() {
		int currPanelIndex = 0; // �õ���ǰҳ���λ��
		if (!otherPanelInit && canchecktype.size() > 1) {
			currPanelIndex = tabpane.getSelectedIndex();
		} else if (otherPanelInit) {
			currPanelIndex = tabpane.getSelectedIndex();
		}
		BillPanel billpanel = (BillPanel) tabOpenHis.get(currPanelIndex + "");
		if (billpanel instanceof BillCellPanel) {
			onCellPanelSubmit((BillCellPanel) billpanel);
			return;
		}
	}

	/*
	 * Excel����д�ύ.
	 */
	private void onCellPanelSubmit(BillCellPanel cellPanel) {
		cellPanel.stopEditing();
		Object[] curTabVO = getCurrCheckPageObject();
		String chekctype = (String) curTabVO[3];
		int lastRowNum = cellPanel.getRowCount();
		if (!ggccTabName.equals(chekctype) && !hideTotle) {
			lastRowNum--; // ������Ǹ߹ܴ�ֱ����ô���һ���Ǻϼơ���Ҫȥ��.
		}
		int lastColNum = cellPanel.getColumnCount();
		int alldatanum = 0;
		try {
			int range[] = getInputNumRange();
			for (int i = value_row_start_index; i < lastRowNum; i++) {
				for (int j = value_col_start_index; j < lastColNum; j++) {
					String cellValue = cellPanel.getValueAt(i, j);
					if (cellValue == null || "".equals(cellValue)) {
						MessageBox.show(cellPanel, "��������û����д!");
						cellPanel.getTable().scrollRectToVisible(cellPanel.getTable().getCellRect(i, j, true));
						cellPanel.getTable().setColumnSelectionInterval(j, j);
						cellPanel.getTable().setRowSelectionInterval(i, i);
						return;
					} else {
						try {
							Float value = Float.parseFloat(cellValue); //
							if (value < range[0] || value > range[1]) {// Ӧ�ò������
								MessageBox.show(cellPanel, "����ֵ��Χ" + input_value_range + "֮��");
								cellPanel.getTable().scrollRectToVisible(cellPanel.getTable().getCellRect(i, j, true));
								cellPanel.getTable().setColumnSelectionInterval(j, j);
								cellPanel.getTable().setRowSelectionInterval(i, i);
								return;
							}
							alldatanum++;
						} catch (Exception _ex) {
							_ex.printStackTrace();
							MessageBox.show(cellPanel, "��дֵ��ΧΪ" + input_value_range + ",�����������ú�ɫ��ʶ,���޸ĺ����ύ.");
							cellPanel.getTable().scrollRectToVisible(cellPanel.getTable().getCellRect(i, j, true));
							cellPanel.getTable().setColumnSelectionInterval(j, j);
							cellPanel.getTable().setRowSelectionInterval(i, i);
							return;
						}

					}
				}
			}
			if ((Boolean) curTabVO[5]) {
				MessageBox.show(cellPanel, "�Ѿ��ύ!");
				return;
			}
			StringBuffer msgsb = new StringBuffer();
			boolean havesameTotleScore = checkPersonTotleSame(cellPanel, msgsb); // ����Ƿ�����ͬ��ֵ
			if (havesameTotleScore) {
				MessageBox.show(cellPanel, "�����ڲ�Ա�������ֲܷ�����ͬ\r\n" + msgsb);
				return;
			}
			boolean maxSameCount = checkPersonEveryFactorMaxScoreSameCount(cellPanel);
			if (!maxSameCount) {
				return;
			}
			// ��Ҫ���������˺Ϳͻ����Ƿ�һֱ.����ʱ������Щ����¼����,����û�и��µ����ݿ���.
			HashMap undo = UIUtil.getHashMapBySQLByDS(null, "select t1.id,checkscore from  sal_person_check_score t1  where t1.scoreuser='" + userid + "' and t1.logid='" + currLog.getStringValue("id") + "' and t1.targettype!='Ա������ָ��' and (t1.checkscore is null or t1.checkscore='') and t1.checktype='" + chekctype + "'");
			if (undo.size() > 0) { // ���ݿ����п�ֵ.ҳ��������.��Ҫ����ͬ��ҳ�����ݵ�������.
				List updateSqls = new ArrayList();
				for (int i = value_row_start_index; i < lastRowNum; i++) {
					for (int j = value_col_start_index; j < lastColNum; j++) {
						BillCellItemVO itemVO = cellPanel.getBillCellItemVOAt(i, j);
						HashVO hashvo = (HashVO) itemVO.getCustProperty("hashvo");
						if (!undo.containsKey(hashvo.getStringValue("id"))) {
							continue;
						}
						String score = cellPanel.getValueAt(i, j);
						UpdateSQLBuilder sqlBuilder = new UpdateSQLBuilder("sal_person_check_score"); // ����updateһ��
						if (hashvo != null) {
							sqlBuilder.setWhereCondition(" id = '" + hashvo.getStringValue("id") + "'");
						} else {
							MessageBox.showWarn(this, "���[" + convertIntColToEn(j + 1) + "" + (i + 1) + "]���ݴ��������ش���,�뾡����ϵ����Ա");
							InsertSQLBuilder insert = new SalaryUIUtil().getErrLogSql("Ա����ֱ����ûȡ��[hashvo]ֵ", "���ݴ���", "��������PersonMutualWKPanel��,���[" + convertIntColToEn(j + 1) + "" + (i + 1) + "]ִ��getCustProperty����ȡ����hashvoֵ��", "����");
							if (insert != null) {
								UIUtil.executeUpdateByDS(null, insert);
							}
							return;
						}
						sqlBuilder.putFieldValue("lasteditdate", UIUtil.getServerCurrDate());
						sqlBuilder.putFieldValue("checkscore", score);
						sqlBuilder.putFieldValue("status", "���ύ");
						updateSqls.add(sqlBuilder);
					}
				}
				UIUtil.executeBatchByDS(null, updateSqls, true, false);
				// �����²�һ�� ���ݿ�
				String strtotle = UIUtil.getStringValueByDS(null, "select count(t1.id) from  sal_person_check_score t1  where t1.scoreuser='" + userid + "' and t1.logid='" + currLog.getStringValue("id") + "' and t1.targettype!='Ա������ָ��' and (t1.checkscore is null or t1.checkscore='') and t1.checktype='" + chekctype + "'");
				if (!"0".equals(strtotle)) { // ��������ݿ��еĿ�ֵ���Ϻ��ٲ�һ�飬�ڲ�һ���Ͳ����ˡ�ֱ��ˢ��ҳ�����ݡ��û��Լ��㡣
					currCheckVOs = UIUtil.getHashVoArrayByDS(null, "select t1.*,t2.catalog from  sal_person_check_score t1 left join sal_person_check_list t2 on t1.targetid=t2.id where t1.scoreuser='" + userid + "' and t1.logid='" + currLog.getStringValue("id") + "' and t1.targettype!='Ա������ָ��' order by t1.checktype,t2.catalog,t1.targetid,t1.id ");
					cellPanel.loadBillCellData(getBillCellVo());// ���¼��ط�����������
					MessageBox.show(cellPanel, "ҳ������δ�ܼ�ʱͬ����������,�����Ѿ�ˢ��,�����º�ʵ��©��.");
					return;
				}
			}
			if (!MessageBox.confirm(cellPanel, "�ύ�����޸ģ�ȷ���ύ��")) {
				return;
			}
			UpdateSQLBuilder updateSql = new UpdateSQLBuilder("sal_person_check_score");
			updateSql.putFieldValue("status", "���ύ");
			updateSql.setWhereCondition(" logid='" + currLog.getStringValue("id") + "' and checktype='" + chekctype + "' and scoreuser='" + userid + "'");
			int num = UIUtil.executeUpdateByDS(null, updateSql);
			if (num == alldatanum) {
				curTabVO[5] = Boolean.TRUE;// ����ִ�б�־
				cellPanel.setEditable(false);
				setCurrCellPanelStatus(cellPanel, "�� �� ��");
				String[][] result = UIUtil.getStringArrayByDS(null, "select count(id),checktype from sal_person_check_score where scoreuser = " + userid + " and (status <> '���ύ' or status is null) and logid=" + currLog.getStringValue("id") + " group by checktype" + " union all select count(id),targettype checktype from sal_dept_check_score where  scoreuser = " + userid
						+ " and targettype='���Ŷ���ָ��' and (status <> '���ύ' or status is null) and logid=" + currLog.getStringValue("id") + " group by targettype");
				StringBuffer msg = new StringBuffer("����û�ж� ");
				boolean have = false;
				for (int i = 0; i < result.length; i++) {
					if (!"0".equals(result[i][0])) {
						msg.append(result[i][1] + "��");
						have = true;
					}
				}
				if (have) {
					msg = new StringBuffer(msg.substring(0, msg.length() - 1));
				}
				if (have) {
					msg.append(" �������ֻ����ύ�����������Ϸ�ҳǩ����ɲ���.");
					MessageBox.show(cellPanel, msg.toString());
				} else {
					MessageBox.show(cellPanel, "���Ѿ�����˱������д������,лл��");
				}
			}
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void actionPerformed(ActionEvent actionevent) {
		WLTButton clickButton = (WLTButton) actionevent.getSource();
		if (clickButton.getText().equals("�޸�")) {
			JLabel l = new JLabel("�޸���ʱ��֧��");
			l.setBackground(Color.BLUE);
			Rectangle rect = new Rectangle(100, -25, 100, 30);
			int currPanelIndex = 0; // �õ���ǰҳ���λ��
			if (canchecktype.size() > 1) { // ��һ��ҳǩ
				currPanelIndex = tabpane.getSelectedIndex();
			}
			BillPanel billpanel = (BillPanel) tabOpenHis.get(currPanelIndex + "");
			DivComponentLayoutUtil.getInstanse().addComponentOnDiv(billpanel, l, rect, null);
		} else {
			onSubmit();
		}
	}

	// ������ת����Ӣ�ı���
	private String convertIntColToEn(int _index) {
		StringBuffer sb = new StringBuffer();
		int in = _index / 26;
		if (in == 0) { // ��������һ��
			char ccc = (char) (65 - 1 + _index);
			sb.append(ccc);
		} else {
			char ccc = (char) (65 - 1 + _index % 26);
			sb.append(ccc);
			sb.insert(0, convertIntColToEn(in));
		}
		return sb.toString();
	}

	/*
	 * �õ���ǰ����ҳ��Ķ������顣{�������û�Map��һ��ָ���Ӧ�Ĵ��Map���ܷ�map����ҳǩ���ƣ�ÿһ������Ӧ��HashVO���к�}
	 */
	public Object[] getCurrCheckPageObject() {
		Object[] curTabVO = null;
		if (canchecktype.size() > 1) {
			curTabVO = (Object[]) canchecktype.get(tabpane.getSelectedIndex() - beginTabIndex); // ��ǰҳǩ��Ӧ������
		} else {
			curTabVO = (Object[]) canchecktype.get(0);
		}
		return curTabVO;
	}

	/*
	 * �߹ܴ�ֽ��档
	 */
	public BillCellVO getGGCellVO() {
		BillCellVO cellVO = new BillCellVO();
		Object obj[] = getCurrCheckPageObject();
		List<HashVO> targetVOList = (ArrayList<HashVO>) obj[0];
		HashVO vos[] = targetVOList.toArray(new HashVO[0]);

		TBUtil.getTBUtil().sortHashVOs(vos, new String[][] { { "checkeduser", "N", "N" } }); // �Ȱ�����Ա����
		int colLength = 3;
		// List userList = new ArrayList(); // ��¼��ǰ��Ա������ԱID������
		List<BillCellItemVO[]> cellVOs = new ArrayList<BillCellItemVO[]>();
		List<BillCellItemVO> titleRow = new ArrayList<BillCellItemVO>();
		BillCellItemVO cell_title_1 = getBillCellVO_Blue(currLog.getStringValue("name")); // �ƻ�����
		cell_title_1.setHalign(2); //
		cell_title_1.setFontsize("15");
		cell_title_1.setFontstyle("1");
		cell_title_1.setSpan("1," + colLength);
		titleRow.add(cell_title_1);
		titleRow.add(getBillCellVO_Normal(""));
		// ��һ��
		List<BillCellItemVO> firstRow = new ArrayList<BillCellItemVO>();
		BillCellItemVO cell_1_1 = getBillCellVO_Blue("��������");
		cell_1_1.setHalign(2); //
		cell_1_1.setSpan("1,1");
		firstRow.add(cell_1_1);

		BillCellItemVO cell_1_2 = getBillCellVO_Blue("��������"); //
		cell_1_2.setColwidth("400");
		firstRow.add(cell_1_2);		
		// BillCellItemVO cell_1_3 = getBillCellVO_Blue("Ȩ��(%)"); // ��ͷ
		// firstRow.add(cell_1_3);

		BillCellItemVO cell_1_4 = getBillCellVO_Blue("���"); // ��ͷ
		firstRow.add(cell_1_4);
		// ���һ��
		cellVOs.add(titleRow.toArray(new BillCellItemVO[0]));
		cellVOs.add(firstRow.toArray(new BillCellItemVO[0]));
		// �ڶ���
		int rowIndex = 2;
		HashMap<String, Float> xiaoji = new HashMap<String, Float>(); // С��
		String flag = ""; // ���
		if (vos.length > 0) {
			flag = vos[0].getStringValue("checkeduser");
		}
		int beginRowIndex = rowIndex; // ��ʼλ�á�
		for (int i = 0; i < vos.length; i++) {
			HashVO targetVO = vos[i]; // �õ�ûһ��vo
			String checkUserID = targetVO.getStringValue("checkeduser");
			BillCellItemVO cell_N_1 = getBillCellVO_Blue(targetVO.getStringValue("checkedusername"));
			cell_N_1.setValign(2);
			cell_N_1.setHalign(2);
			String targetName = targetVO.getStringValue("targetName");
			BillCellItemVO cell_N_2 = getBillCellVO_Blue(targetName);
			cell_N_2.setValign(2);
			cell_N_2.setCelltype("TEXTAREA");
			cell_N_2.setRowheight(getRowMaxHeight(targetName, 400) + "");
			String weights = targetVO.getStringValue("weights");
			// BillCellItemVO cell_N_3 = getBillCellVO_Blue(weights); // Ȩ��
			List<BillCellItemVO> row_N_VO = new ArrayList();
			row_N_VO.add(cell_N_1);
			row_N_VO.add(cell_N_2);
			// row_N_VO.add(cell_N_3);

			BillCellItemVO cell_N = getBillCellVO_Normal(targetVO.getStringValue("checkscore"));// ��ʾ����
			cell_N.setCustProperty("hashvo", targetVO);
			cell_N.setEditformula("AddItemValueChangedListener(\"cn.com.pushworld.salary.ui.person.p020.PersonMutualCheckValueChangeLinstener\",\"Cellִ��\")");
			cell_N.setCelltype("NUMBERTEXT");
			row_N_VO.add(cell_N);
			if (weights != null && !weights.equals("")) {
				if (xiaoji.containsKey(checkUserID)) { // �û�ID
					Float oldSum = xiaoji.get(checkUserID);
					oldSum += Float.parseFloat(weights);
					xiaoji.put(checkUserID, oldSum);
				} else {
					Float oldSum = Float.parseFloat(weights);
					xiaoji.put(checkUserID, oldSum);
				}
			}
			if (!checkUserID.equals(flag)) {
				beginRowIndex = rowIndex;
			}
			flag = checkUserID;
			cellVOs.add(row_N_VO.toArray(new BillCellItemVO[0]));
			// rowIndex++;
			if (i != vos.length - 1) { // ����������һ��
				String nextUserID = vos[i + 1].getStringValue("checkeduser");
				if (nextUserID.equals(flag)) { // �����һ����ͬһ���ˡ�
					continue;
				}
			}
			BillCellItemVO xiaoji_1 = getBillCellVO_Blue("С��");
			xiaoji_1.setSpan("1,2");
			// BillCellItemVO xiaoji_2 = getBillCellVO_Blue(""); // �հ�
			// BillCellItemVO xiaoji_3 =
			// getBillCellVO_Blue(String.valueOf(xiaoji.get(flag))); // �հ�
			// List<BillCellItemVO> cell_xiaoji = new
			// ArrayList<BillCellItemVO>();
			// cell_xiaoji.add(xiaoji_1);
			// cell_xiaoji.add(xiaoji_2);
			// cell_xiaoji.add(xiaoji_3);
			// cell_xiaoji.add(getBillCellVO_Blue(""));
			// cellVOs.add(cell_xiaoji.toArray(new BillCellItemVO[0])); //

			if (obj[1] == null) {
				HashMap userXiaojiRowIndex = new HashMap();
				userXiaojiRowIndex.put(checkUserID, new int[] { beginRowIndex, rowIndex }); // ��ʼ�ͽ���
				obj[1] = userXiaojiRowIndex;
			} else {
				HashMap userXiaojiRowIndex = (HashMap) obj[1];
				userXiaojiRowIndex.put(checkUserID, new int[] { beginRowIndex, rowIndex });
			}
			rowIndex++;
		}
		// �ܼ�
		BillCellItemVO[][] cellitemvos = cellVOs.toArray(new BillCellItemVO[0][0]);
		// formatSpan(cellitemvos, new int[] { 0, 1 });
		// if (obj[1] != null) {
		// HashMap userXiaojiRowIndex = (HashMap) obj[1];
		// for (Iterator iterator = userXiaojiRowIndex.entrySet().iterator();
		// iterator.hasNext();) {
		// Entry userAndXiaojiRowIndex = (Entry) iterator.next();
		// String userid = (String) userAndXiaojiRowIndex.getKey();
		// int[] index = (int[]) userAndXiaojiRowIndex.getValue();
		// cellitemvos[index[1]][0].setSpan("1,2");
		// cellitemvos[index[1]][0].setHalign(2);
		// }
		// }
		cellVO.setCellItemVOs(cellitemvos);
		cellVO.setRowlength(rowIndex); // ���ϱ�ͷ��
		cellVO.setCollength(3);
		return cellVO;
	}

	/*
	 * ���ݵ�ǰ����������(�в㣬һ��Ա��)��ȡCellVO
	 */
	public BillCellVO getBillCellVo() {
		BillCellVO cellVO = new BillCellVO();
		Object obj[] = getCurrCheckPageObject();
		String tabName = (String) obj[3];
		// ���� �߹�
		if (ggccTabName.equals(tabName)) {
			return getGGCellVO();
		}
		LinkedHashMap userset = (LinkedHashMap) obj[0];
		LinkedHashMap scorevomap = (LinkedHashMap) obj[1];
		int colLength = userset.size() + 2;// ������
		int rowLength = scorevomap.size() + (hideTotle ? 2 : 3); // ������
		// List userList = new ArrayList(); // ��¼��ǰ��Ա������ԱID������
		List<BillCellItemVO[]> cellVOs = new ArrayList<BillCellItemVO[]>();

		List<BillCellItemVO> titleRow = new ArrayList<BillCellItemVO>();
		BillCellItemVO cell_title_1 = getBillCellVO_Blue(currLog.getStringValue("name")); // �ƻ�����
		cell_title_1.setHalign(2); //
		cell_title_1.setFontsize("15");
		cell_title_1.setFontstyle("1");
		cell_title_1.setSpan("1," + colLength);
		titleRow.add(cell_title_1);
		titleRow.add(getBillCellVO_Normal(""));
		// ��һ��
		List<BillCellItemVO> firstRow = new ArrayList<BillCellItemVO>();
		BillCellItemVO cell_1_1 = getBillCellVO_Blue("��������");
		cell_1_1.setHalign(2);
		cell_1_1.setSpan("1,2");
		cell_1_1.setFontstyle("1");
		firstRow.add(cell_1_1);

		BillCellItemVO cell_1_2 = getBillCellVO_Blue(""); // �ձ�ͷ
		firstRow.add(cell_1_2);
		// BillCellItemVO cell_1_3 = getBillCellVO_Blue("Ȩ��(%)"); // ��ͷ
		// firstRow.add(cell_1_3);
		// ���һ��
		List<BillCellItemVO> lastRowVO = new ArrayList<BillCellItemVO>();
		BillCellItemVO last_1_1 = getBillCellVO_Blue("�ܼ�");
		last_1_1.setIshtmlhref("Y");
		last_1_1.setForeground("255,69,0");
		last_1_1.setFonttype("1");
		last_1_1.setCellhelp("�ܷ����㷨����Ȩƽ��(����A*Ȩ��A+����B*Ȩ��B+...+����Z*Ȩ��Z)/(Ȩ��A+Ȩ��B+...+Ȩ��Z)");
		last_1_1.setSpan("1,2");
		lastRowVO.add(last_1_1);
		BillCellItemVO last_1_2 = getBillCellVO_Blue(""); // �ո�
		lastRowVO.add(last_1_2);

		// BillCellItemVO last_1_3 = getBillCellVO_Blue("");// �ո�
		// lastRowVO.add(last_1_3);

		for (Iterator iterator = userset.entrySet().iterator(); iterator.hasNext();) {
			titleRow.add(getBillCellVO_Normal(""));
			Entry object = (Entry) iterator.next();
			String[] user = (String[]) object.getValue();
			BillCellItemVO cell_1 = getBillTitleCellItemVO(user[1]); // ��ʾ�û�����
			cell_1.setIseditable("N");
			cell_1.setHalign(2);
			firstRow.add(cell_1);
			BillCellItemVO cell_last = getBillTitleCellItemVO(""); // ���һ�е����ݡ�
			cell_last.setIseditable("N");
			lastRowVO.add(cell_last);
		}
		cellVOs.add(titleRow.toArray(new BillCellItemVO[0]));
		cellVOs.add(firstRow.toArray(new BillCellItemVO[0]));
		// �ڶ���
		int rowIndex = 0;
		float weightcount = 0;
		String maxLengthCatalog = ""; // ���ݷ������ֳ���
		String maxLengthTargetName = ""; // ����ָ���������ֳ���
		for (Iterator iterator = scorevomap.entrySet().iterator(); iterator.hasNext();) {
			Entry object = (Entry) iterator.next();
			HashVO targetVO = (HashVO) object.getValue();
			String catalog = targetVO.getStringValue("catalog");
			if (catalog.length() > maxLengthCatalog.length()) {
				maxLengthCatalog = catalog;
			}
			BillCellItemVO cell_N_1 = getBillCellVO_Blue(catalog);
			cell_N_1.setHalign(2);
			String targetName = targetVO.getStringValue("targetName");
			BillCellItemVO cell_N_2 = getBillCellVO_Blue(targetVO.getStringValue("targetName"));
			cell_N_2.setCelltype("TEXTAREA");
			cell_N_2.setValign(2);
			int length = tbutil.getStrWidth(cell_N_2.getCellvalue());
			if (length > 600) {
				cell_N_2.setRowheight("" + (length / 600 + 1) * 23);
			}
			String weights = targetVO.getStringValue("weights");
			if (targetName.length() > maxLengthTargetName.length()) {
				maxLengthTargetName = targetName;
			}
			if (weights != null && !"".equals(weights)) {
				weightcount += Float.parseFloat(weights);
			}
			// BillCellItemVO cell_N_3 = getBillCellVO_Blue(weights); // Ȩ��
			List<BillCellItemVO> row_N_VO = new ArrayList();
			row_N_VO.add(cell_N_1);
			row_N_VO.add(cell_N_2);
			// row_N_VO.add(cell_N_3);
			for (Iterator iterator2 = userset.keySet().iterator(); iterator2.hasNext();) {
				String userid = (String) iterator2.next();
				BillCellItemVO cell_N = getBillCellVO_Normal(targetVO.getStringValue(userid));// ��ʾ����
				cell_N.setHalign(2);
				cell_N.setCustProperty("hashvo", getHashVO(rowIndex, userid));
				cell_N.setCelldesc("������");
				cell_N.setEditformula("AddItemValueChangedListener(\"cn.com.pushworld.salary.ui.person.p020.PersonMutualCheckValueChangeLinstener\",\"Cellִ��\")");
				cell_N.setCelltype("NUMBERTEXT");
				row_N_VO.add(cell_N);
			}
			cellVOs.add(row_N_VO.toArray(new BillCellItemVO[0]));
			rowIndex++;
		}
		// ���������
		cell_1_1.setColwidth(TBUtil.getTBUtil().getStrWidth(maxLengthCatalog) + 30 + "");
		int length = TBUtil.getTBUtil().getStrWidth(maxLengthTargetName);
		if (length > 600) {
			cell_1_2.setColwidth((600) + "");
		} else {
			cell_1_2.setColwidth(TBUtil.getTBUtil().getStrWidth(maxLengthTargetName) + 30 + "");
		}
		// last_1_3.setCellvalue(weightcount + "");
		// �ܼ�
		if (!hideTotle) {
			cellVOs.add(lastRowVO.toArray(new BillCellItemVO[0]));
		}
		BillCellItemVO[][] cellitemvos = cellVOs.toArray(new BillCellItemVO[0][0]);
		formatSpan(cellitemvos, new int[] { 0 });

		cellitemvos[1][0].setSpan("1,2"); // ���ºϲ���ͷ(��������)
		if (!hideTotle) {
			cellitemvos[cellitemvos.length - 1][0].setSpan("1,2"); // �ϲ����һ�еģ��ܼƣ�
			cellitemvos[cellitemvos.length - 1][0].setHalign(2);
		}
		cellVO.setCellItemVOs(cellitemvos);
		cellVO.setRowlength(rowLength);
		cellVO.setCollength(colLength);
		return cellVO;
	}

	/*
	 * �õ����к���ı���߶ȡ�
	 */
	private int getRowMaxHeight(String str_value, int _columnWidth) {
		if (str_value != null) {
			int li_worllength = TBUtil.getTBUtil().getStrWidth(str_value); // �����ַ��ĳ���(����)����������˻������õ������Ͳ�׼�ˣ�����
			int height = Toolkit.getDefaultToolkit().getFontMetrics(LookAndFeel.font).getHeight();
			int li_rows = li_worllength / _columnWidth + 1; //
			if (li_rows == 1) {
				return 22;
			}
			return li_rows * height;
		}
		return 22;// �����ı����к�һ�еĸ߶�Ҫ��22����СЩ������ڼ���html���ӣ��и߾͸�С�ˣ�����޸ģ�
	}

	/*
	 * ��ɫ�����ı��
	 */
	private BillCellItemVO getBillCellVO_Blue(String value) {
		BillCellItemVO item = new BillCellItemVO();
		item.setCellvalue(value);
		item.setBackground("191,213,255");
		item.setFonttype("������");
		item.setFontsize("12");
		item.setFontstyle("0");
		item.setSpan("1,1");
		item.setIseditable("N");
		item.setCellhelp(null);
		return item;
	}

	/*
	 * Ĭ�ϰ�ɫ
	 */
	private BillCellItemVO getBillCellVO_Normal(String value) {
		BillCellItemVO item = new BillCellItemVO();
		item.setCellvalue(value);
		item.setFonttype("������");
		item.setFontsize("12");
		item.setFontstyle("0");
		item.setSpan("1,1");
		item.setCellhelp(null);
		return item;
	}

	/*
	 * ����
	 */
	private BillCellItemVO getBillTitleCellItemVO(String value) {
		BillCellItemVO item = new BillCellItemVO();
		item.setCellvalue(value);
		item.setBackground("191,213,0");
		item.setFonttype("������");
		item.setFontsize("12");
		item.setFontstyle("1");
		item.setSpan("1,1");
		item.setIseditable("N");
		item.setCellhelp(null);
		return item;
	}

	/*
	 * ����ĳһ�е��ܼ�
	 */
	public void resetColSum(BillCellPanel cellPanel, int _colIndex) {
		Object obj[] = getCurrCheckPageObject();
		if (ggccTabName.equals(obj[3]) || hideTotle) { // ����Ƕ��³��Ը߹ܾ�������,����ҪС��.
			return;
		}
		int lastRow = cellPanel.getRowCount() - 1;
		float sum = 0;
		StringBuffer help = new StringBuffer();
		help.append("(");
		float weightsSum = 0f;// Float.parseFloat(cellPanel.getBillCellItemVOAt(lastRow,
		// 2).getCellvalue()); // Ȩ�غ�
		for (int i = value_row_start_index; i < lastRow; i++) {
			HashVO hvo = (HashVO) cellPanel.getBillCellItemVOAt(i, 2).getCustProperty("hashvo");
			String weights = hvo.getStringValue("weights");// cellPanel.getBillCellItemVOAt(i,
			// 2).getCellvalue();
			String cellValue = cellPanel.getBillCellItemVOAt(i, _colIndex).getCellvalue();
			try {
				if (cellValue != null && !cellValue.equals("")) {
					sum += Float.parseFloat(cellValue) * Float.parseFloat(weights);
					help.append(Float.parseFloat(cellValue) + "*" + Float.parseFloat(weights) + "+");
				}
			} catch (Exception ex) {
				MessageBox.show("��������д" + convertIntColToEn(_colIndex + 1) + "" + (i + 1));
				cellPanel.getTable().scrollRectToVisible(cellPanel.getTable().getCellRect(i, _colIndex, true));
				cellPanel.getTable().setColumnSelectionInterval(_colIndex, _colIndex);
				cellPanel.getTable().setRowSelectionInterval(i, i);
				return;
			}
			weightsSum += Float.parseFloat(weights);
		}
		if (help.length() > 2) {
			help = new StringBuffer(help.substring(0, help.length() - 1));
		}
		help.append(")");
		help.append("/" + weightsSum);
		float lastsum = (sum / weightsSum);
		BillCellItemVO sumitemvo = cellPanel.getBillCellItemVOAt(cellPanel.getRowCount() - 1, _colIndex);
		if (lastsum == 0) {
			sumitemvo.setCellhelp(null);
			sumitemvo.setCellvalue("");
		} else {
			sumitemvo.setCellvalue(String.format("%." + 2 + "f", lastsum));
		}
		// ����Ƿ����ظ��ķ�ֵ
		checkPersonTotleSame(cellPanel, new StringBuffer());
	}

	/*
	 * ����˶�ÿһ��ָ�����֣�ͬһָ�겻ͬ��Ա����(��߷���ͬ)�������п��ơ���[1-4]�ˣ�ĳָ��ֻ����1��Ϊ10�֡�
	 */
	private boolean checkPersonEveryFactorMaxScoreSameCount(BillCellPanel cellPanel) {
		// �˷���Ŀǰһ��Ҫ�롾��ֽ����Ƿ������ܼơ�������ͬʱִ�С�
		if (!hideTotle) {// �����ֲܷ�����ͬ��
			return true;
		}
		int range[] = getInputNumRange();
		int lastColNum = cellPanel.getColumnCount();
		int lastRowNum = cellPanel.getRowCount();
		int percount = lastColNum - value_col_start_index;
		String maxscorecount = tbutil.getSysOptionStringValue("ͬһָ�겻ͬ��Ա����(��߷���ͬ)�������п���", null);// ȡ���������������������ζ�Ӧ����߷�������
		if (tbutil.isEmpty(maxscorecount)) { //��ϸ�ģ��������ϵͳĬ���ǲ����õġ�
			return true;
		}
		String defcount = salaryTBUtil.getScoreConfigMinVal(maxscorecount, percount);
		int maxcount = 0;
		if (defcount.contains("%")) {
			Double maxde = 0.4 * percount;
			maxcount = Integer.parseInt(format(0, maxde));
		} else {
			maxcount = Integer.parseInt(defcount);
		}
		String isMAx = tbutil.getSysOptionStringValue("ͬһָ�겻ͬ��Ա���ַ���ͬ��������", "");
		if (isMAx.equals("")) {
			return true;
		}
		int maxscore = 0;
		try {
			for (int i = value_row_start_index; i < lastRowNum; i++) {// ����ÿһ�У������жϡ�����������ʾ
				int count = 0;
				if (isMAx.equalsIgnoreCase("N")) {// �ж���ȡ���ֻ������ֵ
					maxscore = range[1];
				} else {
					maxscore = 0;
				}
				for (int j = value_col_start_index; j < lastColNum; j++) {
					String colValue = cellPanel.getValueAt(i, j);
					int score = Integer.parseInt(colValue);
					if (score > maxscore) {
						maxscore = score;
						count = 1;
					} else if (score == maxscore) {
						count++;
					}
				}
				if (count > maxcount) {
					MessageBox.show(cellPanel, "������ָ�꡾" + cellPanel.getValueAt(i, value_row_start_index - 1) + "������߷�Ϊ:" + maxscore + "��,\n�õ��÷��߲��ó���" + maxcount + "��(" + percount + "�˵�" + defcount + ");��������ύ");
					return false;
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * ��������
	 */
	public String format(int size, double number) {
		StringBuffer formatString = new StringBuffer("0");
		if (size > 0) {
			formatString.append(".");
		}
		for (int i = 0; i < size; i++) {
			formatString.append("#");
		}
		DecimalFormat df = new DecimalFormat(formatString.toString());
		return df.format(number);
	}

	/*
	 * ����Ƿ�����ͬ�ķ����ܺ�
	 */
	private boolean checkPersonTotleSame(BillCellPanel cellPanel, StringBuffer samemsg) {
		if (samemsg == null) {
			samemsg = new StringBuffer();
		}
		if (hideTotle) {
			return false;
		}
		int lastColNum = cellPanel.getColumnCount();
		int lastRowNum = cellPanel.getRowCount() - 1;
		Object obj[] = getCurrCheckPageObject();
		HashMap sameMap = new HashMap();// ��¼��ͬ
		try {
			if ("һ����Ա".equals(obj[3])) { // һ����Ա��ҪУ���Ƿ���ͬ����ֵ�ġ�
				for (int i = value_col_start_index; i < lastColNum; i++) {
					String cellValue = cellPanel.getValueAt(lastRowNum, i);
					if (tbutil.isEmpty(cellValue)) {
						continue;
					}
					if (sameMap.containsKey(cellValue)) {
						List cells = (List) sameMap.get(cellValue);
						cells.add(i);
					} else {
						List cells = new ArrayList();
						cells.add(i);// ���мӽ���
						sameMap.put(cellValue, cells);
					}
					BillCellItemVO itemvo = cellPanel.getBillCellItemVOAt(lastRowNum, i); // �ָ�������ɫ
					itemvo.setCellhelp(null);
					itemvo.setBackground("255,255,255");

				}
			} else {
				return false;
			}
			boolean have = false;
			int index = 0; // �ж��ٶ��ظ���

			if (sameMap.size() > 0) {
				for (Iterator iterator = sameMap.entrySet().iterator(); iterator.hasNext();) {
					Entry<String, List> type = (Entry<String, List>) iterator.next();
					String cellvalue = type.getKey();
					List cells = type.getValue();
					if (cells.size() < 2) {
						continue;
					}
					have = true;
					StringBuffer cellHelp = new StringBuffer();
					for (int i = 0; i < cells.size(); i++) {
						samemsg.append(convertIntColToEn((Integer) cells.get(i) + 1));
						cellHelp.append(convertIntColToEn((Integer) cells.get(i) + 1));
						if (i < cells.size() - 1) {
							samemsg.append("��");
							cellHelp.append("��");
						}
						int colindex = (Integer) cells.get(i);
						BillCellItemVO itemvo = cellPanel.getBillCellItemVOAt(lastRowNum, colindex);
						itemvo.setBackground(getColor(index));
					}
					samemsg.append("�ܼ�ֵ�����ظ�,Ŀǰ��Ϊ:" + cellvalue + "\r\n");
					cellHelp.append("�ܼ�ֵ�����ظ�,Ŀǰ��Ϊ:" + cellvalue + "\r\n");
					for (int i = 0; i < cells.size(); i++) {
						int colindex = (Integer) cells.get(i);
						BillCellItemVO itemvo = cellPanel.getBillCellItemVOAt(lastRowNum, colindex);
						itemvo.setCellhelp(cellHelp.toString());
					}
					index++;
				}
				return have;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	private String getColor(int index) {
		String[] beautifulColors = new String[] { "255,80,0", "74,63,228",//
				"254,0,254", "169,143,80", "128,0,254", "0,128,0", "254,254,128", "128,0,128", "64,0,128", "128,128,0", "128,128,192", "254,128,192", "0,128,254", "128,64,0",//
				"225,213,218", "255,30,0", "200,234,210", "224,242,26", "231,192,229", "98,69,77", "192,188,226", "73,129,90", "241,244,210",//
				"225,39,214", "195,229,226", "0,254,254", "225,48,94", "100,99,108", "42,231,99", "153,159,87", "96,69,94", "74,143,138", "244,178,18" }; // һϵ�кÿ�����ɫ,һ��25��,���ν�������
		return beautifulColors[index % 28];
	}

	public void autoCheckIFCanCommit() {
		if (!autoCheckCommit) {// �Ƿ���Ҫ�Զ����
			return;
		}
		Object[] curTabVO = getCurrCheckPageObject();
		String chekctype = (String) curTabVO[3];
		int currPanelIndex = 0; // �õ���ǰҳ���λ��
		if (!otherPanelInit && canchecktype.size() > 1) {
			currPanelIndex = tabpane.getSelectedIndex();
		} else if (otherPanelInit) {
			currPanelIndex = tabpane.getSelectedIndex();
		}
		final BillCellPanel cellPanel = (BillCellPanel) tabOpenHis.get(currPanelIndex + "");
		int lastColNum = cellPanel.getColumnCount();
		int lastRowNum = cellPanel.getRowCount();
		if (!ggccTabName.equals(chekctype) && !hideTotle) {
			lastRowNum--; // ������Ǹ߹ܴ�ֱ����ô���һ���Ǻϼơ���Ҫȥ��.
		}
		int alldatanum = 0;
		int range[] = getInputNumRange();
		try {
			for (int i = value_row_start_index; i < lastRowNum; i++) {
				for (int j = value_col_start_index; j < lastColNum; j++) {
					String cellValue = cellPanel.getValueAt(i, j);
					if (cellValue == null || "".equals(cellValue)) {
						return;
					} else {
						try {
							Float value = Float.parseFloat(cellValue); //
							if (value < range[0] || value > range[1]) {// Ӧ�ò������
								return;
							}
							alldatanum++;
						} catch (Exception _ex) {
							return;
						}

					}
				}
			}
			// ȫ����д���ˡ�
			StringBuffer msgsb = new StringBuffer();
			boolean havesameTotleScore = checkPersonTotleSame(cellPanel, msgsb); // ����Ƿ�����ͬ��ֵ
			if (havesameTotleScore) {
				return;
			}
			new Timer().schedule(new TimerTask() {
				public void run() {
					String msg = "��ǰҳ�����������,�Ƿ��ύ���?";
					int index = MessageBox.showOptionDialog(cellPanel, msg, "ϵͳ��ʾ", new String[] { "�����ύ", "�Ժ��ύ" });
					if (index != 0) {
						autoCheckCommit = false;// �����ϲ���Զ���ʾ������Ϊfalse��
						return;
					}
					onCellPanelSubmit(cellPanel);
				}
			}, 300); // �ӳ�һ����ٵ�����.���ܺ�һЩ��
		} catch (Exception ex) {
			ex.printStackTrace();
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
			logger.error("", _ex);
		}
		return new int[] { begin, end };
	}

	public void markTargetScoresByName(String checktype, String[] targetname, String _color) {
		if (tbutil.isEmpty(_color)) {
			_color = "246,53,89";
		}
		if (tabpane != null) {
			int tabindex = -1;
			if (canchecktype != null) {
				for (int i = 0; i < canchecktype.size(); i++) {
					Object[] obj = (Object[]) canchecktype.get(i);
					if (obj != null) {
						String ctype = (String) obj[3];
						if (ctype != null && ctype.equals(checktype)) {
							tabindex = i;
							break;
						}
					}
				}
			}
			if (tabindex >= 0) {
				BillCellPanel cellp = (BillCellPanel) tabOpenHis.get((tabindex + beginTabIndex) + "");
				if (cellp != null) {
					int rowcount = cellp.getRowCount();
					int colcount = cellp.getColumnCount();
					HashMap<String, Integer> ind = new HashMap<String, Integer>();

					for (int i = 2; i < rowcount; i++) {
						try {
							String value = cellp.getValueAt(i, 1);
							ind.put(value, (Integer) i);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					for (int i = 0; i < targetname.length; i++) {
						String onetagetname = targetname[i];
						Integer index = ind.get(onetagetname);
						if (index != null && index > 0) {
							for (int j = 1; j < colcount; j++) {
								BillCellItemVO cellvo = cellp.getBillCellItemVOAt(index, j);
								cellvo.setBackground(_color);
							}
						}
					}

				}
			}
		}
	}
}

/*
 * �Զ�����Ա����TMO,��ͷ�и��ݴ���'����'������������һ��Ա���͸߹�
 */
class PersonMutualTMO extends AbstractTMO {
	String[][] personVos;

	private PersonMutualTMO() {
	}

	public PersonMutualTMO(String[][] _persons) { // id,name
		personVos = _persons;
	}

	@Override
	public HashVO getPub_templet_1Data() {
		HashVO vo = new HashVO(); //
		vo.setAttributeValue("templetcode", "SAL_PERSON_CHECK_SCORE_CODE6"); // ģ�����,��������޸�
		vo.setAttributeValue("templetname", "Ա�����࿼��"); // ģ������
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
		itemVO.setAttributeValue("itemkey", "ID"); // Ψһ��ʶ,����ȡ���뱣�� //ָ��ID
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
		itemVO.setAttributeValue("itemkey", "catalog"); // Ψһ��ʶ,����ȡ���뱣�� //ָ��ID
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
		itemVO.setAttributeValue("itemkey", "targetName"); // Ψһ��ʶ,����ȡ���뱣��
		// //ָ��ID
		itemVO.setAttributeValue("itemname", "��������"); // ��ʾ����
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

		for (int i = 0; i < personVos.length; i++) {
			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", personVos[i][0]); // Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", personVos[i][1]); // ��ʾ����
			itemVO.setAttributeValue("itemname_e", ""); // ��ʾ����
			// itemVO.setAttributeValue("itemtype", "�Զ���ؼ�"); // �ؼ�����
			itemVO.setAttributeValue("itemtype", "���ֿ�"); // �ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); // ��������
			// itemVO.setAttributeValue("refdesc",
			// "getCommUC(\"�Զ���ؼ�\",\"��Ƭ�е���\",\"\",\"�б���Ⱦ��\",\"cn.com.pushworld.salary.ui.target.p040.ScoreCellRenderer\",\"�б�༭��\",\"cn.com.pushworld.salary.ui.target.p040.ScoreCellEditor\");");
			// //
			// ���ն���
			itemVO.setAttributeValue("issave", "N"); // �Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
			itemVO.setAttributeValue("editformula", "AddItemValueChangedListener(\"cn.com.pushworld.salary.ui.person.p020.PersonMutualCheckValueChangeLinstener\",\"�б�ִ��\");"); // �༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "80"); // �б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150"); // ��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "N"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("iswrap", "N");
			itemvoList.add(itemVO);
		}
		return (HashVO[]) itemvoList.toArray(new HashVO[0]);
	}
}
