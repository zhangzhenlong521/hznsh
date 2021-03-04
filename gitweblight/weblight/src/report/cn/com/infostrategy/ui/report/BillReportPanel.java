package cn.com.infostrategy.ui.report;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.math.BigDecimal;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;
import cn.com.infostrategy.to.report.BillChartItemVO;
import cn.com.infostrategy.to.report.BillChartVO;
import cn.com.infostrategy.to.report.ReportUtil;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTPanelUI;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;
import cn.com.infostrategy.ui.report.chart.BillChartPanel;

/**
 * Ŀǰ����ǿ��ı�����,��ֻ���ڶԵ�����ά���ĸ�ά�ȵ���ȡ!! ������������ϲ�!
 * ֻ�ǱȽ��ŵĵط���,�����Զ���ά��!!
 * @author xch
 */
public class BillReportPanel extends JPanel implements ActionListener, ItemListener {

	private static final long serialVersionUID = 1L;
	private String str_builderClassName = null; //

	private String[] allCanGroupFields; //
	private String[] allCanSumFields;
	private HashMap groupFieldOrder; //��������!!

	private BillQueryPanel billQueryPanel = null;

	private JLabel groupLabel = null; //
	private JComboBox comboBox_beforeHand = null; //Ԥ�õ�ά��!!
	private WLTButton btn_chooseGroup = null; //�Զ���ά�ȱ���!!
	private BeforeHandGroupTypeVO custGroupVO = null; //�Զ���ѡ���ά��
	private MultiLevelChooseGroupDialog chooseGroupDialog = null; //�Զ���ѡ��ά�ȴ���??

	private JPanel contentPanel = null; // ������
	private ReportCellPanel currReportCellPanel = null; //����Ǳ�񱨱�ʱ�����!
	private BillChartPanel chartPanel = null; //ͼ����ʱ�����!

	private JPanel WLPP = null; // ���ⲿһ���ӿ� ������չ �������
	private BillRadarPanel spliderPanel = null;
	private String drillClassPath, drillTempletCode;
	private HashMap drillgroupbindMap = null; //��ȡԤ�ð󶨵�Map
	private HashMap zeroReportConfMap = null; //��㱨���Ƶ�Map
	private HashMap dateGroupDefineMap = null; //ʱ��ά�ȶ����ֶ�!
	//private HashMap secondHashVOComputeMap = null; //��HashVO���μ����Map,���ֻ�����ڶԹ��˺�����ݽ�������������!
	private TBUtil tbUtil = null; //������!
	private Icon icon = UIUtil.getImage("office_134.gif");

	//�������ΪԬ�������  ��Ҫ�������������Ȩ������
	private Pub_Templet_1VO pub_Templet_vo = null; //Ԭ����20120913��ӣ���ҪΪ�˴�������Ȩ��  ���getDataPolicyCondition���Ȩ�޷���
	private FrameWorkMetaDataServiceIfc metaDataService = null; //Ԫ����Զ�̷���.
	private String str_dataPolicyInfo = null; //����Ȩ�޵�˵��
	private String str_dataPolicySQL = null; //����Ȩ�޵�˵��
	private String reportExpName = null;
	private String graphName = "ͼ��";//����ά������Ϊ��ͼ��ʱ��ͼ������ʾ���֡����/2016-01-26��

	/**
	 * ����һ��ģ�����,һ����������,�Ϳ��Թ���һ������ģ����!
	 * @param _templetCode
	 * @param _classname,����̳��ڳ����ࡾcn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter����ʵ���ڽӿڡ�cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderIFC��
	 */
	public BillReportPanel(String _templetCode, String _classname) {
		super();
		try {
			Pub_Templet_1VO templetVO = UIUtil.getPub_Templet_1VO(_templetCode);
			initialize(templetVO, _classname); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	public BillReportPanel(String _graphName, String _templetCode, String _classname) {
		super();
		try {
			this.graphName = _graphName;
			Pub_Templet_1VO templetVO = UIUtil.getPub_Templet_1VO(_templetCode);
			initialize(templetVO, _classname); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	public BillReportPanel(AbstractTMO _abstractTempletVO, String _classname) {
		super();
		try {
			Pub_Templet_1VO templetVO = UIUtil.getPub_Templet_1VO(_abstractTempletVO);
			initialize(templetVO, _classname); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	//����ҳ��!!
	private void initialize(Pub_Templet_1VO _templetVO, String _classname) {
		this.str_builderClassName = _classname; //

		JPanel temp_panel = new JPanel(new BorderLayout(0, 10)); //
		temp_panel.setOpaque(false); //͸��!!!

		this.pub_Templet_vo = _templetVO;
		reportExpName = this.pub_Templet_vo.getTempletname();
		//��ѯ���!
		billQueryPanel = new BillQueryPanel(_templetVO, true, getDataPolicyCondition()); // ������ѯ��ť  //Ԭ����20120913�޸ģ��������һ��������ʾ���������Ȩ��
		billQueryPanel.addBillQuickActionListener(this); //
		temp_panel.add(billQueryPanel, BorderLayout.NORTH); //�����ѯ���

		//��������!
		contentPanel = new JPanel(new BorderLayout()); //WLTPanel.createDefaultPanel(new BorderLayout()); //
		contentPanel.setOpaque(false); //͸��!!
		BillCellPanel cellPanel = new BillCellPanel(null, true, false, true); //�ֹ�����һ��Ĭ�ϵı��!
		cellPanel.setEditable(false);
		cellPanel.setAllowShowPopMenu(false);
		cellPanel.span(0, 2, 2, 4); //
		cellPanel.setValueAt("�������ѯ����������ѯ��ť!", 0, 2); //
		cellPanel.setHalign(new int[] { 0 }, new int[] { 2 }, 2); //
		cellPanel.setCrystal(true); //ˮ��,͸��!!!
		contentPanel.add(cellPanel); //
		temp_panel.add(contentPanel, BorderLayout.CENTER); //����Ĭ�ϱ�����!

		//������
		this.setLayout(new BorderLayout(0, 0)); //
		this.setBackground(LookAndFeel.defaultShadeColor1); //
		this.setUI(new WLTPanelUI(WLTPanel.INCLINE_NW_TO_SE, Color.WHITE, true)); //��ײ���ǽ���!!
		this.add(getReportTypePanel(), BorderLayout.NORTH);
		this.add(temp_panel, BorderLayout.CENTER); //
	}

	/**
	 * ��������
	 * @return
	 */
	private JPanel getReportTypePanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5)); //
		panel.setOpaque(false); //
		groupLabel = new JLabel("����ά������", SwingConstants.RIGHT); //
		groupLabel.setPreferredSize(new Dimension(110, 25)); //ѡ���Զ���ά�Ⱥ�JLabelǰ�������һ��ͼ�꣬������Ŵ������µ���������ʾ��ȫ�����������ӿ�ȡ����/2019-03-25��
		comboBox_beforeHand = new JComboBox(); //
		comboBox_beforeHand.setEditable(true); //
		comboBox_beforeHand.setMaximumRowCount(25); //���һ������ʾ25��,�����쵼һ����֪������ά��!!ʡ�ò�֪�����滹�и�ͼ���͵ı���!!
		comboBox_beforeHand.setFocusable(false); //
		comboBox_beforeHand.addItemListener(this); //
		try {
			ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class); //
			HashMap hm_return = service.getMultiLevelReportGroup(this.str_builderClassName); //��������ȥ��������ȡ��
			allCanGroupFields = (String[]) hm_return.get("AllGroupFields"); //���е���
			allCanSumFields = (String[]) hm_return.get("AllComputeFields"); //���еļ�����
			groupFieldOrder = (HashMap) hm_return.get("groupfieldorder"); //�õ������ֶΣ���ǰ��Ĭ��string���򣬺ܶ�ط��������⡣���Ա���ָ������ʽ��
			drillClassPath = (String) hm_return.get("drillclass"); //�ӷ������˵õ��ͻ���ʵ�ֵ����·��
			drillTempletCode = (String) hm_return.get("drilltempletcode"); //��ȡ��ϸʱ�������б�ģ�����!!
			drillgroupbindMap = (HashMap) hm_return.get("drillgroupbind"); //��󶨵Ķ���
			zeroReportConfMap = (HashMap) hm_return.get("zeroReportConfMap"); //��㱨���ƶ����Map����
			dateGroupDefineMap = (HashMap) hm_return.get("dateGroupDefineMap"); //ʱ���ֶ�ά�ȣ���

			BeforeHandGroupTypeVO[] beforeHandTypeVOs_grid = (BeforeHandGroupTypeVO[]) hm_return.get("BeforeHandGroupType_Grid"); //
			BeforeHandGroupTypeVO[] beforeHandTypeVOs_chart = (BeforeHandGroupTypeVO[]) hm_return.get("BeforeHandGroupType_Chart"); //
			BeforeHandGroupTypeVO[] beforeHandTypeVOs_splider = (BeforeHandGroupTypeVO[]) hm_return.get("BeforeHandGroupType_Splider"); //�״�ͼ!
			int li_maxlength = 55; //
			if (beforeHandTypeVOs_grid != null) {
				for (int i = 0; i < beforeHandTypeVOs_grid.length; i++) {
					beforeHandTypeVOs_grid[i].setType("grid"); //
					if (getTBUtil().getSysOptionBooleanValue("���������Ƿ�ȥ�Ǳ��", false)) {
						beforeHandTypeVOs_grid[i].setName("���-" + beforeHandTypeVOs_grid[i].getName()); //�ʴ���ʽ �����/2012-09-07��
					} else {
						beforeHandTypeVOs_grid[i].setName("����--" + beforeHandTypeVOs_grid[i].getName());
					}
					comboBox_beforeHand.addItem(beforeHandTypeVOs_grid[i]); //
					int li_length = SwingUtilities.computeStringWidth(comboBox_beforeHand.getFontMetrics(comboBox_beforeHand.getFont()), beforeHandTypeVOs_grid[i].getName()); //
					if (li_length > li_maxlength) {
						li_maxlength = li_length;
					}
				}
			}

			if (beforeHandTypeVOs_chart != null) {
				for (int i = 0; i < beforeHandTypeVOs_chart.length; i++) {
					beforeHandTypeVOs_chart[i].setType("chart"); //
					if (getTBUtil().getSysOptionBooleanValue("���������Ƿ�ȥ�Ǳ��", false)) {
						beforeHandTypeVOs_chart[i].setName("ͼ��-" + beforeHandTypeVOs_chart[i].getName()); //�ʴ���ʽ �����/2012-09-07��
					} else {
						beforeHandTypeVOs_chart[i].setName("��ͼ��--" + beforeHandTypeVOs_chart[i].getName());
					}

					comboBox_beforeHand.addItem(beforeHandTypeVOs_chart[i]); //
					int li_length = SwingUtilities.computeStringWidth(comboBox_beforeHand.getFontMetrics(comboBox_beforeHand.getFont()), beforeHandTypeVOs_chart[i].getName()); //
					if (li_length > li_maxlength) {
						li_maxlength = li_length;
					}
				}
			}

			if (beforeHandTypeVOs_splider != null) {
				for (int i = 0; i < beforeHandTypeVOs_splider.length; i++) {
					beforeHandTypeVOs_splider[i].setType("splider"); //
					beforeHandTypeVOs_splider[i].setName("���״�ͼ--" + beforeHandTypeVOs_splider[i].getName());
					comboBox_beforeHand.addItem(beforeHandTypeVOs_splider[i]); //
					int li_length = SwingUtilities.computeStringWidth(comboBox_beforeHand.getFontMetrics(comboBox_beforeHand.getFont()), beforeHandTypeVOs_splider[i].getName()); //
					if (li_length > li_maxlength) {
						li_maxlength = li_length;
					}
				}
			}
			comboBox_beforeHand.setPreferredSize(new Dimension(li_maxlength + 25, 22)); //

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		panel.add(groupLabel); //
		panel.add(comboBox_beforeHand); //

		btn_chooseGroup = new WLTButton("�Զ���ά��", "office_135.gif"); //
		btn_chooseGroup.setToolTipText("����ѡ��ά�Ⱥ�,�����ѯ��ť!"); //
		btn_chooseGroup.addActionListener(this); //
		btn_chooseGroup.addCustPopMenuItem("�鿴Demo����", "office_181.gif", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onRunDemoReport(); //�������ӱ���
			}
		}); //
		panel.add(btn_chooseGroup); //

		WLPP = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2)); //
		WLPP.setOpaque(false); //͸��
		panel.add(WLPP); //

		return panel; //
	}

	/**
	 * ��ȡ���Զ���ά�ȡ���ť��������Ŀ�����ò���ʾ�����/2018-12-06��
	 * @return
	 */
	public WLTButton getBtn_chooseGroup() {
		return btn_chooseGroup;
	}

	/**
	 * ��ѯ����..
	 */
	private void onQueryData(boolean _isCtrlDown) {
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
		try {
			BeforeHandGroupTypeVO groupVO = null; //
			if (custGroupVO != null) {
				groupVO = custGroupVO;
			} else {
				groupVO = (BeforeHandGroupTypeVO) comboBox_beforeHand.getSelectedItem(); //
			}
			if (groupVO == null) {
				MessageBox.show(this, "��ѡ��һ����������(Ԥ��Ļ��Զ����)!"); //
				return; //
			}

			HashMap queryConsMap = billQueryPanel.getQuickQueryConditionAsMap(true, _isCtrlDown ? false : true); //�������ѯ����,����б�����Ĳ�ѯ������û����,��ֱ�ӷ���!
			if (queryConsMap == null) {
				return; //
			}

			String[][] computeFunAndFields = new String[groupVO.getComputeGroupFields().length][3]; //ʵ������3��!
			for (int i = 0; i < groupVO.getComputeGroupFields().length; i++) { //
				if (groupVO.getComputeGroupFields()[i].length == 3) { //�����3λ
					computeFunAndFields[i] = new String[] { groupVO.getComputeGroupFields()[i][1], groupVO.getComputeGroupFields()[i][2], groupVO.getComputeGroupFields()[i][0] }; //cout,count,�����¼�!
				} else { //���ֻ��2λ
					computeFunAndFields[i] = new String[] { groupVO.getComputeGroupFields()[i][1], "", groupVO.getComputeGroupFields()[i][0] }; //cout,count,�����¼�!
				}
			}
			queryConsMap.put("obj_rowheader", groupVO.getRowHeaderGroupFields()); //�����д�������ѡ���ά�ȡ���̨���Ը���ѡ���ά�ȶԷ��ص�HashVo���߼�����
			queryConsMap.put("obj_colheader", groupVO.getColHeaderGroupFields()); //��̨�õ�����ά�����顣
			queryConsMap.put("obj_$typename", groupVO.getName()); //��ѡ��ͳ���������ƴ�����̨

			if (groupVO.getType().equalsIgnoreCase("GRID")) { //�������ά�����͵ģ�����
				currReportCellPanel = new ReportCellPanel(this.str_builderClassName, this.allCanGroupFields, this.allCanSumFields, groupVO.getRowHeaderGroupFields(), groupVO.getColHeaderGroupFields(), computeFunAndFields, groupVO.getFilterGroupValueMap(), groupVO.getSecondHashVOComputeMap(), groupVO.getRowHeaderFormulaGroupMap(), groupVO.isTotal(), groupVO.isRowGroupTiled(), groupVO
						.isRowGroupSubTotal(), groupVO.isColGroupTiled(), groupVO.isColGroupSubTotal(), groupVO.isSortByCpValue(), queryConsMap, null, groupFieldOrder, zeroReportConfMap, dateGroupDefineMap, _isCtrlDown); //
				currReportCellPanel.setStr_drillActionClass(drillClassPath); //���÷�����!
				currReportCellPanel.setStr_drillTempletCode(drillTempletCode); //����ģ�����!
				currReportCellPanel.setDrillgroupbindMap(drillgroupbindMap); //���ð󶨵�Map
				currReportCellPanel.putClientProperty("templetname", reportExpName);
				this.contentPanel.removeAll();
				this.contentPanel.setLayout(new BorderLayout()); //
				this.contentPanel.add(currReportCellPanel); //���¼���!!
				this.contentPanel.updateUI(); //
			} else if (groupVO.getType().equalsIgnoreCase("CHART")) { //�����ͼ������!
				String[] str_groupFields = new String[groupVO.getRowHeaderGroupFields().length + groupVO.getColHeaderGroupFields().length];
				System.arraycopy(groupVO.getRowHeaderGroupFields(), 0, str_groupFields, 0, groupVO.getRowHeaderGroupFields().length);
				System.arraycopy(groupVO.getColHeaderGroupFields(), 0, str_groupFields, groupVO.getRowHeaderGroupFields().length, groupVO.getColHeaderGroupFields().length);
				ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class); //
				HashVO[] hvs_data = service.queryMultiLevelReportData(queryConsMap, this.str_builderClassName, str_groupFields, computeFunAndFields, _isCtrlDown); // ��Ϊ�����˵��������ֻ��������л����,����ͻ�������!
				BillChartVO chartVO = new ReportUtil().convertHashVOToChartVO(hvs_data, groupFieldOrder, computeFunAndFields); //���ϲ��������ת����ͼ���BillChartVO���� Ԭ����20121108��ӵ�������������ʾͼ���պ��ַ�ʽ�����㣬֮ǰ��ֻ֧�ְ���count������
				if (chartVO == null || chartVO.getDataVO() == null) {
					MessageBox.show(this, "û�з������������ݣ�");
					return;
				}

				//���������Ϊ��...
				if (groupVO.getRowHeaderGroupFields().length == 0 || groupVO.getColHeaderGroupFields().length == 0) {
					if (groupVO.getRowHeaderGroupFields().length == 0) {
						chartPanel = new BillChartPanel(this.graphName, "", groupVO.getColHeaderGroupFields()[0], convertHashVOToChartVO(hvs_data)); //
					} else {
						chartPanel = new BillChartPanel(this.graphName, groupVO.getRowHeaderGroupFields()[0], "", convertHashVOToChartVO(hvs_data)); //
					}
				} else {
					chartPanel = new BillChartPanel(this.graphName, groupVO.getColHeaderGroupFields()[0], groupVO.getRowHeaderGroupFields()[0], chartVO); //�����޸ģ����б���
				}
				chartPanel.setStr_builderClassName(str_builderClassName); //
				chartPanel.setAllCanGroupFields(allCanGroupFields); //
				chartPanel.setQueryConditionMap(queryConsMap); //
				chartPanel.setStr_drillActionClass(drillClassPath); //
				chartPanel.setStr_drillTempletCode(drillTempletCode);

				contentPanel.removeAll(); //
				contentPanel.setLayout(new BorderLayout()); //
				contentPanel.add(chartPanel, BorderLayout.CENTER); //
				contentPanel.updateUI(); //
			} else if (groupVO.getType().equalsIgnoreCase("splider")) { //������״�ͼ
				String[] str_groupFields = new String[groupVO.getRowHeaderGroupFields().length + groupVO.getColHeaderGroupFields().length];
				System.arraycopy(groupVO.getRowHeaderGroupFields(), 0, str_groupFields, 0, groupVO.getRowHeaderGroupFields().length);
				System.arraycopy(groupVO.getColHeaderGroupFields(), 0, str_groupFields, groupVO.getRowHeaderGroupFields().length, groupVO.getColHeaderGroupFields().length);
				ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class); //
				HashVO[] hvs_data = service.queryMultiLevelReportData(queryConsMap, this.str_builderClassName, str_groupFields, computeFunAndFields, _isCtrlDown);
				spliderPanel = new BillRadarPanel("", hvs_data, groupVO.getRowHeaderGroupFields(), groupVO.getColHeaderGroupFields(), groupVO.getComputeGroupFields()[0][0], true);
				contentPanel.removeAll(); //
				contentPanel.setLayout(new BorderLayout()); //
				contentPanel.add(spliderPanel, BorderLayout.CENTER); //
				contentPanel.updateUI(); //
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		} finally {
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == billQueryPanel) {
			onQueryData(Boolean.parseBoolean(e.getActionCommand())); //
		} else if (e.getSource() == btn_chooseGroup) {
			onChooseGroupField(); //
		}
	}

	//Ԥ����ά��������ѡ��仯���¼�
	public void itemStateChanged(ItemEvent evt) {
		if (evt.getStateChange() == ItemEvent.SELECTED) {
			this.groupLabel.setIcon(null); //
			this.groupLabel.setForeground(Color.BLACK); //
			custGroupVO = null; //�������仯ʱ,�������������ά��!
		}
	}

	/**
	 * �������ӱ���!!������ϵͳ��DemoReportBuilder
	 */
	private void onRunDemoReport() {
		//MessageBox.show(this, "����ϵͳDemo����!");  //
		JFrame frame = new JFrame();
		frame.setTitle("Demo Report"); //
		frame.setSize(1027, 768); //
		frame.setLocation(30, 20);
		BillReportPanel reportDemoPane = new BillReportPanel("WLTDUAL", "cn.com.infostrategy.bs.report.DemoReportBuilder");//demo����ģ��
		frame.setLayout(new BorderLayout());
		frame.getContentPane().add(reportDemoPane);
		frame.setVisible(true);
	}

	/**
	 * ѡ����
	 */
	private void onChooseGroupField() {
		try {
			if (chooseGroupDialog == null) {
				chooseGroupDialog = new MultiLevelChooseGroupDialog(this, allCanGroupFields, allCanSumFields); //����ѡ���Զ���ά�ȵĴ���!!!
			}
			chooseGroupDialog.setVisible(true); //��ʾ!!
			if (chooseGroupDialog.getCloseType() == 1) { //�����ȷ���ر�
				String[] chooseGroupRowFields = chooseGroupDialog.getRowGroupnames(); //��ͷ.
				String[] chooseGroupColFields = chooseGroupDialog.getColGroupnames(); //��ͷ.
				String[][] chooseComputeFunAndField = chooseGroupDialog.getComputeFunAndFields(); //���صļ�����!
				String str_reportType = chooseGroupDialog.getReportType(); //��������,��"GRID"��"CHART"����!

				StringBuffer sb_text = new StringBuffer(); //
				if (str_reportType.equalsIgnoreCase("GRID")) {
					if (getTBUtil().getSysOptionBooleanValue("���������Ƿ�ȥ�Ǳ��", false)) {
						sb_text.append("���-"); //�ʴ���ʽ �����/2012-09-07��
					} else {
						sb_text.append("����--");
					}
				} else if (str_reportType.equalsIgnoreCase("CHART")) {
					if (getTBUtil().getSysOptionBooleanValue("���������Ƿ�ȥ�Ǳ��", false)) {
						sb_text.append("ͼ��-"); //�ʴ���ʽ �����/2012-09-07��
					} else {
						sb_text.append("��ͼ��--");
					}
				}

				for (int i = 0; i < chooseGroupRowFields.length; i++) {
					sb_text.append(chooseGroupRowFields[i] + "-"); //
				}
				for (int i = 0; i < chooseGroupColFields.length; i++) {
					sb_text.append(chooseGroupColFields[i] + "-"); //
				}
				String str_text = sb_text.toString(); //
				if (str_text.endsWith("-")) {
					str_text = str_text.substring(0, str_text.length() - 1); ////???
				}
				comboBox_beforeHand.setSelectedItem(null); //�����ѡ��!
				JTextField cmb_textField = ((JTextField) ((JComponent) comboBox_beforeHand.getEditor().getEditorComponent())); //
				cmb_textField.setText(str_text); //
				groupLabel.setIcon(icon); //
				groupLabel.setForeground(Color.BLUE); //

				//���õ�ǰѡ�е�ά�ȵ�ֵ������
				custGroupVO = new BeforeHandGroupTypeVO("�Զ�ѡ���ά��"); ////
				custGroupVO.setType(str_reportType); //�����!
				custGroupVO.setRowHeaderGroupFields(chooseGroupRowFields); //��ͷ
				custGroupVO.setColHeaderGroupFields(chooseGroupColFields); //��ͷ
				custGroupVO.setRowHeaderFormulaGroupMap(chooseGroupDialog.getRowHeaderFormulaMap()); //��ͷ���Զ����ά�ȶ���!
				custGroupVO.setComputeGroupFields(chooseComputeFunAndField); //������!
				custGroupVO.setRowGroupTiled(chooseGroupDialog.isRowGroupTiled()); //��ͷ�Ƿ���ƽ��
				custGroupVO.setRowGroupSubTotal(chooseGroupDialog.isRowGroupSubTotal()); //��ͷ�Ƿ���С��
				custGroupVO.setColGroupTiled(chooseGroupDialog.isColGroupTiled()); //��ͷ�Ƿ���ƽ��
				custGroupVO.setColGroupSubTotal(chooseGroupDialog.isColGroupSubTotal()); //��ͷ�Ƿ���С��?
				custGroupVO.setSortByCpValue(chooseGroupDialog.isSortByCpValue()); //�Ƿ񰴼���ֵ����? �����/2012-08-21��
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	//Ԭ������20120913��Ȩ�޲��ԣ���ȡȨ�޻���
	public String getDataPolicyCondition() {
		if (this.pub_Templet_vo.getDatapolicy() != null && !this.pub_Templet_vo.getDatapolicy().trim().equals("")) { //�����������Ȩ�޶���!!���������Ȩ�޹���!!
			try {
				String[] str_datapolicyCondition = getMetaDataService().getDataPolicyCondition(ClientEnvironment.getCurrLoginUserVO().getId(), this.pub_Templet_vo.getDatapolicy(), this.pub_Templet_vo.getDatapolicymap()); //
				str_dataPolicyInfo = str_datapolicyCondition[0]; //
				str_dataPolicySQL = str_datapolicyCondition[1]; //				
				return str_dataPolicySQL;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private FrameWorkMetaDataServiceIfc getMetaDataService() {
		if (metaDataService == null) {
			try {
				metaDataService = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); ////
			} catch (Exception e) {
				e.printStackTrace();
				return null; //
			}
		}
		return metaDataService; //
	}

	public JComboBox getComboBox_beforeHand() {
		return comboBox_beforeHand;
	}

	public void setComboBox_beforeHand(JComboBox comboBox_beforeHand) {
		this.comboBox_beforeHand = comboBox_beforeHand;
	}

	public JPanel getWLPP() {
		return WLPP;
	}

	public void setWLPP(JPanel wlpp) {
		WLPP = wlpp;
	}

	public ReportCellPanel getCurrReportCellPanel() {
		return currReportCellPanel;
	}

	public BillQueryPanel getBillQueryPanel() {
		return billQueryPanel;
	}

	private TBUtil getTBUtil() {
		if (this.tbUtil != null) {
			return tbUtil;
		}
		tbUtil = new TBUtil();
		return tbUtil;
	}

	/**
	 * ���ñ�������Ĭ���ļ���
	 * Ĭ����ģ������
	 * ���ģ������Ϊ����Ĭ��Ϊ"�������ݵ���"
	 * @param reportExpName
	 */
	public void setReportExpName(String reportExpName) {
		this.reportExpName = reportExpName;
	}

	public BillChartVO convertHashVOToChartVO(HashVO[] _hvs) {
		String[] str_XSerial = null; //
		HashMap map_x = new HashMap();
		for (int i = 0; i < _hvs.length; i++) {
			map_x.put(_hvs[i].getStringValue(0), null); //
		}
		str_XSerial = (String[]) map_x.keySet().toArray(new String[0]);
		new TBUtil().sortStrs(str_XSerial); // Ĭ�ϰ���ĸ˳��..
		BillChartItemVO[][] ld_data = new BillChartItemVO[str_XSerial.length][1]; //
		for (int i = 0; i < str_XSerial.length; i++) {
			for (int k = 0; k < _hvs.length; k++) { // ȥ��������λ��
				if (str_XSerial[i].equals(_hvs[k].getStringValue(0))) { //
					Object cellValue = _hvs[k].getObjectValue(1); //
					if (cellValue instanceof Double) { // �����Double����
						ld_data[i][0] = new BillChartItemVO((Double) cellValue); //
					} else if (cellValue instanceof String) {
						ld_data[i][0] = new BillChartItemVO(Double.parseDouble((String) cellValue)); //
					} else if (cellValue instanceof Integer) {
						ld_data[i][0] = new BillChartItemVO(Double.valueOf("" + cellValue)); //
					} else if (cellValue instanceof BillChartItemVO) {
						ld_data[i][0] = (BillChartItemVO) cellValue; //
					} else if (cellValue instanceof BigDecimal) {
						ld_data[i][0] = new BillChartItemVO(((BigDecimal) cellValue).doubleValue());
					} else {
						ld_data[i][0] = new BillChartItemVO(-77777);
					}
					ld_data[i][0].setCustProperty("#value", _hvs[k].getStringValue("#value")); //
					break;
				}
			}
		}

		BillChartVO chartVO = new BillChartVO(); //
		chartVO.setXSerial(str_XSerial); //
		chartVO.setYSerial(new String[] { "����" }); //
		chartVO.setDataVO(ld_data);
		return chartVO.convertXY(); //
	}

}
